package ge.vakho.spring_boot.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ge.vakho.spring_boot.configuration.BundleConfig;
import ge.vakho.spring_boot.configuration.BundleConfig.Entry;
import ge.vakho.spring_boot.exception.BundleNotFoundException;

/**
 * Serves as API for bundle installation, start, stop and deletion.
 * 
 * @author v.laluashvili
 */
@Service
public class BundleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

	private final BundleConfig bundleConfig;
	private final BundleInstallService bundleInstallService;
	private final BundleCleanupService bundleCleanupService;

	@Autowired
	public BundleService(BundleConfig bundleConfig, BundleInstallService bundleInstallService, 
			BundleCleanupService bundleCleanupService) {
		this.bundleConfig = bundleConfig;
		this.bundleInstallService = bundleInstallService;
		this.bundleCleanupService = bundleCleanupService;
	}

	public <T> Stream<T> getServicesByClass(Class<T> clazz) {
		return bundleConfig.getServicesByClass(clazz);
	}

	public List<Entry> getAll() {
		return bundleConfig.getEntries();
	}

	public void start(long bundleId) throws BundleNotFoundException, BundleException {
		LOGGER.info("Starting bundle with id: {}", bundleId);
		getBundleById(bundleId).start();
		bundleConfig.setEntryForceStartTo(bundleId, true);
		LOGGER.info("Started bundle with id: {}", bundleId);
	}

	public void stop(long bundleId) throws BundleNotFoundException, BundleException {
		LOGGER.info("Stopping bundle with id: {}", bundleId);
		getBundleById(bundleId).stop();
		bundleConfig.setEntryForceStartTo(bundleId, false);
		LOGGER.info("Stopped bundle with id: {}", bundleId);
	}

	private Bundle getBundleById(long bundleId) throws BundleNotFoundException {
		Bundle bundle = bundleConfig.getBundle(bundleId);
		if (bundle == null) {
			throw new BundleNotFoundException(bundleId);
		}
		return bundle;
	}

	public Bundle install(MultipartFile file) throws IOException, BundleException {
		return install(file.getOriginalFilename(), file.getBytes());
	}

	/**
	 * If the installation fails remove JAR file from bundles' folder.
	 * 
	 * @return
	 */
	public Bundle install(String fileName, byte[] jarBytes) throws IOException, BundleException {
		LOGGER.info("Installing new bundle: {}...", fileName);

		// Create JAR file in bundles' folder
		Path newBundle = bundleInstallService.createJarFileInBundlesFolder(fileName, jarBytes);

		// Install new bundle
		Bundle newInstalledBundle = bundleInstallService.installBundleFromPath(newBundle);

		// Insert new bundle entry at the end of bundles' configuration file
		bundleInstallService.insertEntryInConfigurationFile(newInstalledBundle, newBundle);

		LOGGER.info("Installed new bundle: {}", fileName);
		return newInstalledBundle;
	}

	public void uninstall(long bundleId) throws BundleNotFoundException {

		Bundle bundle = getBundleById(bundleId);
		String fileName = bundleConfig.getEntryFileNameBy(bundleId);

		LOGGER.info("Uninstalling bundle: {} ...", bundle.getSymbolicName());

		// Uninstall bundle from framework
		bundleCleanupService.uninstallBundle(bundle);

		// Remove leftover JAR file
		bundleCleanupService.removeJar(fileName);

		LOGGER.info("Uninstalled bundle: {} and its JAR file: {}", bundle.getSymbolicName(), fileName);
	}
}