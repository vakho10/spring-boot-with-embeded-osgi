package ge.vakho.spring_boot.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ge.vakho.spring_boot.property.OsgiProperties;

/**
 * This class loads predefined bundles.
 * 
 * @author v.laluashvili
 */
@Configuration
public class BundleConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleConfig.class);

	private static final String BUNDLE_FOLDER_KEY_NAME = "bundleFolder";
	private static final String BUNDLE_PREFIX_NAME = "bundle";

	private final BundleContext bundleContext;
	private final OsgiProperties osgiProperties;
	
	private PropertiesConfiguration propertiesConfiguration;
	private Path bundleFolder;

	@Autowired
	public BundleConfig(BundleContext bundleContext, OsgiProperties osgiProperties) {
		this.bundleContext = bundleContext;
		this.osgiProperties = osgiProperties;
	}

	@PostConstruct
	private void init() throws IOException, ConfigurationException {

		// Parse the bundle configuration properties
		initConfiguration();

		// Install && start (force start) bundles
		startBundles(installBundles());
	}

	private void initConfiguration() throws ConfigurationException {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(
				PropertiesConfiguration.class)
						.configure(params.properties().setFileName(osgiProperties.getBundleConfigFile().getAbsolutePath())
								.setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
		builder.setAutoSave(true);
		this.propertiesConfiguration = builder.getConfiguration();

		// Validate bundle folder path
		this.bundleFolder = Paths.get(propertiesConfiguration.getString(BUNDLE_FOLDER_KEY_NAME));
		if (!Files.exists(bundleFolder)) {
			throw new IllegalStateException("Bundle folder doesn't exist! Path: " + bundleFolder.toAbsolutePath());
		}
		if (!Files.isDirectory(bundleFolder)) {
			throw new IllegalStateException(
					"Bundle folder path should point to a folder! Path: " + bundleFolder.toAbsolutePath());
		}
	}

	private List<Entry> installBundles() {
		List<Entry> installedEntries = new ArrayList<>();
		List<String> failedBundles = new ArrayList<>();
		LOGGER.info("Installing bundles...");
		
		for (Entry entry : getEntries()) {

			final String bundleAbsolutePath //
					= bundleFolder.resolve(removePrefix(entry.key)).toAbsolutePath().toString();
			try {
				Bundle installedBundle = bundleContext.installBundle("file:" + bundleAbsolutePath);

				entry.setBundleId(installedBundle.getBundleId());
				
				// Update installed bundle id
				propertiesConfiguration.setProperty(entry.key, //
						new Object[] { true, entry.forceStart, entry.bundleId });
				
				// Add to installed entries list
				installedEntries.add(entry);

				LOGGER.debug("Installed bundle: {}", installedBundle.getSymbolicName());
			} catch (BundleException e) {
				failedBundles.add(bundleAbsolutePath);

				// Failed bundles should have id -1
				propertiesConfiguration.setProperty(entry.key, //
						new Object[] { false, entry.forceStart, -1 });

				LOGGER.debug("Failed to install bundle: {}", bundleAbsolutePath);
				LOGGER.error(e.getMessage(), e);
				continue;
			}
		}
		LOGGER.info("Installed {} bundles", installedEntries.size());
		if (!failedBundles.isEmpty()) {
			LOGGER.warn("Failed to install {} bundles: {}", failedBundles.size(), failedBundles);
		}
		return installedEntries;
	}

	private void startBundles(List<Entry> installedEntries) {
		LOGGER.info("Starting bundles...");
		List<Entry> startedBundles = new ArrayList<>();
		List<String> failedBundles = new ArrayList<>();
		for (Entry installedBundle : installedEntries) {
			final Bundle bundle = bundleContext.getBundle(installedBundle.bundleId);
			try {
				if (installedBundle.forceStart) {
					// Start bundle
					bundle.start();
					startedBundles.add(installedBundle);
					LOGGER.debug("Started bundle: {}", bundle.getSymbolicName());
				}
			} catch (BundleException e) {
				failedBundles.add(bundle.getSymbolicName());
				LOGGER.debug("Failed to start bundle: {}", bundle.getSymbolicName());
				LOGGER.error(e.getMessage(), e);
				continue;
			}
		}
		LOGGER.info("Started {} bundles", startedBundles.size());
		if (!failedBundles.isEmpty()) {
			LOGGER.warn("Failed to start {} bundles: {}", failedBundles.size(), failedBundles);
		}
	}

	public Path getBundleFolder() {
		return bundleFolder;
	}

	public List<Entry> getEntries() {
		return getEntries(null);
	}
	
	public List<Entry> getEntries(Predicate<Entry> predicate) {
		List<Entry> entries = new ArrayList<>();
		for (Iterator<String> iterator = propertiesConfiguration.getKeys(BUNDLE_PREFIX_NAME); iterator.hasNext();) {
			final String key = iterator.next();

			String[] values = propertiesConfiguration.getStringArray(key);
			boolean installed = Boolean.valueOf(values[0]);
			boolean forceStart = Boolean.valueOf(values[1]);
			long bundleId = Long.valueOf(values[2]);
			
			Entry entry = new Entry(key, installed, forceStart, bundleId);
			
			if (predicate == null || predicate.test(entry)) {
				entries.add(entry);
			}
		}
		return entries;
	}
	
	private boolean isEntryInstalled(Entry entry) {
		return entry.installed;
	}

	public <T> Stream<T> getServicesByClass(Class<T> clazz) {
		Stream<T> serviceStream = null;
		try {
			serviceStream = bundleContext.getServiceReferences(clazz, null) //
					.parallelStream() //
					.map(ref -> bundleContext.getService(ref));
		} catch (InvalidSyntaxException e) {
			// Unreachable error block!
		}
		return serviceStream;
	}

	private String removePrefix(String key) {
		return key.replace(BUNDLE_PREFIX_NAME + ".", "");
	}

	private String addPrefix(String name) {
		return BUNDLE_PREFIX_NAME + "." + name;
	}

	public static class Entry {

		private String key;
		private boolean forceStart;
		private boolean installed;
		private long bundleId;

		public Entry() {
		}

		public Entry(String key, boolean forceStart, boolean installed, long bundleId) {
			this.key = key;
			this.forceStart = forceStart;
			this.installed = installed;
			this.bundleId = bundleId;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public boolean isForceStart() {
			return forceStart;
		}

		public void setForceStart(boolean forceStart) {
			this.forceStart = forceStart;
		}

		public boolean isInstalled() {
			return installed;
		}

		public void setInstalled(boolean installed) {
			this.installed = installed;
		}

		public long getBundleId() {
			return bundleId;
		}

		public void setBundleId(long bundleId) {
			this.bundleId = bundleId;
		}
	}
}