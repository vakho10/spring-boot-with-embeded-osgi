package ge.vakho.spring_boot.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import ge.vakho.spring_boot.property.BundleProperties;
import ge.vakho.spring_boot.service.BundleConfigFile;

/**
 * This class loads predefined bundles.
 * 
 * @author v.laluashvili
 */
@Configuration
@EnableConfigurationProperties(BundleProperties.class)
public class BundleConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleConfig.class);

	private final BundleConfigFile bundleConfigFile;
	private final BundleProperties bundleProperties;
	private final BundleContext bundleContext;

	@Autowired
	public BundleConfig(BundleProperties bundleProperties, BundleContext bundleContext, BundleConfigFile bundleConfigFile) {
		this.bundleProperties = bundleProperties;
		this.bundleContext = bundleContext;
		this.bundleConfigFile = bundleConfigFile;
	}

	@PostConstruct
	public void init() throws IOException {
		// Parse configuration file
		String[] bundles = parseConfigurationFile();
		
		// Install bundles
		List<Bundle> installedBundles = installConfigurationBundles(bundles);

		// Start bundles
		startConfigurationBundles(installedBundles);
	}

	private String[] parseConfigurationFile() throws IOException {
		try {
			return new ObjectMapper().readValue(bundleProperties.getConfigFilePath().toFile(), String[].class);
		} catch (IOException e) {
			LOGGER.error("Failed to parse configuration file: {}",
					bundleProperties.getConfigFilePath().toAbsolutePath().toString());
			throw e;
		}
	}
	
	private List<Bundle> installConfigurationBundles(String[] bundles) {
		Map<Long, String> installedMap = new HashMap<>();
		List<Bundle> installedBundles = new ArrayList<>();
		List<String> failedBundles = new ArrayList<>();
		LOGGER.info("Installing bundles...");
		for (String bundle : bundles) {
			String bundleAbsolutePath = bundleProperties.getFolderPath().resolve(bundle).toAbsolutePath()
					.toString();
			try {
				Bundle installedBundle = bundleContext.installBundle("file:" + bundleAbsolutePath);
				installedBundles.add(installedBundle);
				installedMap.put(installedBundle.getBundleId(), bundle);
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
		
		LOGGER.debug("Inserting installed bundles into config memory...");
		bundleConfigFile.insert(installedMap);
		LOGGER.debug("Inserted installed bundles into config memory");
		return installedBundles;
	}

	private void startConfigurationBundles(List<Bundle> installedBundles) {
		LOGGER.info("Starting {} bundles...", installedBundles.size());
		List<Bundle> startedBundles = new ArrayList<>();
		List<String> failedBundles = new ArrayList<>();
		for (Bundle installedBundle : installedBundles) {
			try {
				installedBundle.start();
				startedBundles.add(installedBundle);
				LOGGER.debug("Started bundle: {}", installedBundle.getSymbolicName());
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