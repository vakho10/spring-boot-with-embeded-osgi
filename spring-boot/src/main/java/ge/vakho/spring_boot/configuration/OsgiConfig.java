package ge.vakho.spring_boot.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
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

import ge.vakho.spring_boot.logger.FelixLogger;
import ge.vakho.spring_boot.property.OsgiProperties;

/**
 * Loads Felix OSGI framework.
 * 
 * @author v.laluashvili
 */
@Configuration
@EnableConfigurationProperties(OsgiProperties.class)
public class OsgiConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(OsgiConfig.class);

	private Framework framework;

	private final OsgiProperties osgiProperties;

	@Autowired
	public OsgiConfig(OsgiProperties osgiProperties) {
		this.osgiProperties = osgiProperties;
	}

	@Bean
	public Framework framework() {
		return framework;
	}

	@Bean
	public BundleContext bundleContext() {
		return framework.getBundleContext();
	}

	@PostConstruct
	public void start() throws BundleException, JsonParseException, JsonMappingException, IOException {
		LOGGER.info("Starting Felix framework...");
		framework = new Felix(getConfig());
		framework.start();
		LOGGER.info("Felix framwork has started");
	}

	@PreDestroy
	public void stop() throws BundleException, InterruptedException {
		LOGGER.info("Stopping Felix framework...");
		framework.stop();
		framework.waitForStop(0);
		LOGGER.info("Felix framework has been stopped");
	}

	private Map<String, Object> getConfig() {
		final Map<String, Object> config = new HashMap<>();

		// Clean cache on first init
		config.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);

		addLoggerConfigurations(config);
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

}