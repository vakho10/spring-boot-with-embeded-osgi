package ge.vakho.spring_boot.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ge.vakho.spring_boot.exception.BundleNotFoundException;
import ge.vakho.spring_boot.property.BundleProperties;

/**
 * Serves as API for bundle installation, start, stop and deletion.
 * 
 * @author v.laluashvili
 */
@Service
public class BundleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

	private final BundleConfigFile bundleConfigFile;
	private final BundleContext bundleContext;
	private final BundleInstallService bundleInstallService;
	private final BundleCleanupService bundleCleanupService;

	@Autowired
	public BundleService(BundleConfigFile bundleConfigFile, BundleContext bundleContext,
			BundleProperties bundleProperties, BundleInstallService bundleInstallService,
			BundleCleanupService bundleCleanupService) {
		this.bundleConfigFile = bundleConfigFile;
		this.bundleContext = bundleContext;
		this.bundleInstallService = bundleInstallService;
		this.bundleCleanupService = bundleCleanupService;
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

	public Bundle[] getAll() {
		return bundleContext.getBundles();
	}

	public void start(long bundleId) throws BundleNotFoundException, BundleException {
		getBundleById(bundleId).start();
		bundleConfigFile.setForceStartTo(bundleId, true);
	}

	public void stop(long bundleId) throws BundleNotFoundException, BundleException {
		getBundleById(bundleId).stop();
		bundleConfigFile.setForceStartTo(bundleId, false);
	}

	private Bundle getBundleById(long bundleId) throws BundleNotFoundException {
		Bundle bundle = bundleContext.getBundle(bundleId);
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
		String fileName = bundleConfigFile.getFileNameBy(bundleId);

		LOGGER.info("Uninstalling bundle: {} ...", bundle.getSymbolicName());

		// Uninstall bundle from framework
		bundleCleanupService.uninstallBundle(bundle);

		// Remove leftover JAR file
		bundleCleanupService.removeJar(fileName);

		LOGGER.info("Uninstalled bundle: {} and its JAR file: {}", bundle.getSymbolicName(), fileName);
	}
}