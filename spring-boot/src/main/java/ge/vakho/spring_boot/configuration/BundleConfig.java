package ge.vakho.spring_boot.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ge.vakho.spring_boot.configuration.model.BundleConfigurationFile;

/**
 * This class loads predefined bundles.
 * 
 * @author v.laluashvili
 */
@Configuration
public class BundleConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleConfig.class);

	private final BundleContext bundleContext;
	private final BundleConfigurationFile bundleConfigurationFile;

	@Autowired
	public BundleConfig(BundleContext bundleContext, BundleConfigurationFile bundleConfigurationFile) {
		this.bundleContext = bundleContext;
		this.bundleConfigurationFile = bundleConfigurationFile;
	}

	@PostConstruct
	public void init() throws IOException {
		List<Bundle> installedBundles = new ArrayList<>();
		List<BundleConfigurationFile.Entry> installedEntries = new ArrayList<>();
		
		// Install bundles
		installConfigurationBundles(installedBundles, installedEntries);

		// Start bundles which have start set to true
		startConfigurationBundles(installedBundles, installedEntries);
	}

	private List<Bundle> installConfigurationBundles(List<Bundle> installedBundles, List<BundleConfigurationFile.Entry> installedEntries) {
		List<String> failedBundles = new ArrayList<>();
		LOGGER.info("Installing bundles...");
		for (BundleConfigurationFile.Entry entry : bundleConfigurationFile.getBundleEntries()) {
			String bundleAbsolutePath //
					= bundleConfigurationFile.getBundleFolder().toPath().resolve(entry.getFileName()).toAbsolutePath().toString();
			try {
				Bundle installedBundle = bundleContext.installBundle("file:" + bundleAbsolutePath);
				installedBundles.add(installedBundle);

				entry.setBundleId(installedBundle.getBundleId());
				installedEntries.add(entry);
				
				LOGGER.debug("Installed bundle: {}", installedBundle.getSymbolicName());
			} catch (BundleException e) {
				failedBundles.add(bundleAbsolutePath);
				LOGGER.debug("Failed to install bundle: {}", bundleAbsolutePath);
				LOGGER.error(e.getMessage(), e);
				continue;
			}
		}
		LOGGER.info("Installed {} bundles", installedBundles.size());
		if (!failedBundles.isEmpty()) {
			LOGGER.warn("Failed to install {} bundles: {}", failedBundles.size(), failedBundles);
		}
		return installedBundles;
	}

	private void startConfigurationBundles(List<Bundle> installedBundles, List<BundleConfigurationFile.Entry> installedEntries) {
		LOGGER.info("Starting {} bundles...", installedBundles.size());
		List<Bundle> startedBundles = new ArrayList<>();
		List<String> failedBundles = new ArrayList<>();
		for (int i = 0; i < installedBundles.size(); i++) {
			Bundle installedBundle = installedBundles.get(i);
			try {
				if (installedEntries.get(i).isForceStart()) {
					installedBundle.start();
					startedBundles.add(installedBundle);
					LOGGER.debug("Started bundle: {}", installedBundle.getSymbolicName());
				}
			} catch (BundleException e) {
				failedBundles.add(installedBundle.getSymbolicName());
				LOGGER.debug("Failed to start bundle: {}", installedBundle.getSymbolicName());
				LOGGER.error(e.getMessage(), e);
				continue;
			}
		}
		LOGGER.info("Started {} bundles", startedBundles.size());
		if (!failedBundles.isEmpty()) {
			LOGGER.warn("Failed to start {} bundles: {}", failedBundles.size(), failedBundles);
		}
	}

}