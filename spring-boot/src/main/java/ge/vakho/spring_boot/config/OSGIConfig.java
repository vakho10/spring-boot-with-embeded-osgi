package ge.vakho.spring_boot.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.framework.util.FelixConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ge.vakho.spring_boot.logger.FelixLogger;
import ge.vakho.spring_boot.property.OsgiProperties;

@Configuration
@EnableConfigurationProperties(OsgiProperties.class)
public class OSGIConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(OSGIConfig.class);

	private Framework framework;
	private final OsgiProperties osgiProperties;

	@Autowired
	public OSGIConfig(OsgiProperties osgiProperties) {
		this.osgiProperties = osgiProperties;
	}

	@PostConstruct
	public void start() throws BundleException, JsonParseException, JsonMappingException, IOException {
		LOGGER.info("Starting OSGI framework...");
		framework = new Felix(getConfig());
		framework.start();
		LOGGER.info("OSGI framework has started");

		installBundles();
	}

	private void installBundles() throws JsonParseException, JsonMappingException, IOException, BundleException {
		LOGGER.info("Installing bundles...");
		ObjectMapper mapper = new ObjectMapper();
		Path bundlesFolder = Paths.get("bundles/");
		String[] bundles = mapper.readValue(bundlesFolder.resolve("config.json").toFile(), String[].class);
		for (String bundle : bundles) {
			String path = bundlesFolder.resolve(bundle).toAbsolutePath().toString();
			LOGGER.info("Installing bundle {}", path);
			Bundle installedBundle = framework.getBundleContext().installBundle("file:" + path);
			LOGGER.info("Installed bundle {}", installedBundle.getSymbolicName());
			installedBundle.start();
			LOGGER.info("Started bundle {}", installedBundle.getSymbolicName());
		}
		LOGGER.info("All {} bundles installed successfully", bundles.length);
	}

	@PreDestroy
	public void stop() throws BundleException, InterruptedException {
		LOGGER.info("Stopping OSGI framework...");
		framework.stop();
		framework.waitForStop(0);
		LOGGER.info("OSGI framework has been stopped");
	}

	private Map<String, Object> getConfig() {

		// Create configuration with Felix logger set
		final Map<String, Object> config = new HashMap<>();

		// Add logger and its configurations
		addLoggerConfigurations(config);

		// Clean cache on first init
		config.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);

		// Add extra exports for logging and service calls...
		addExtraPackages(config);
		return config;
	}

	private void addLoggerConfigurations(Map<String, Object> config) {
		config.put(FelixConstants.LOG_LOGGER_PROP, new FelixLogger());
		config.put(FelixConstants.LOG_LEVEL_PROP, osgiProperties.getLogLevel());
		LOGGER.info("Added Felix logger with level: {}", osgiProperties.getLogLevel());
	}

	/**
	 * Adds extra package exports from the spring boot application (where we've
	 * started the framework).
	 * 
	 * @param config - OSGI framework's configuration
	 */
	private void addExtraPackages(Map<String, Object> config) {
		// Export (non-direct) api and model packages so this app can use them...
		String extraPackages = String.join(",", osgiProperties.getExtraPackages());
		config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, extraPackages);
		LOGGER.info("Added extra packages: {}", extraPackages);
	}

	@Bean
	public BundleContext bundleContext() throws BundleException {
		return framework.getBundleContext();
	}

}