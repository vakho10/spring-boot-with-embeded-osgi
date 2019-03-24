package ge.vakho.spring_boot_with_embeded_osgi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSGIConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(OSGIConfig.class);

	private Framework framework;

	@PostConstruct
	public void start() throws BundleException {
		LOGGER.info("Starting OSGI framework...");
		FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class) //
				.iterator().next();
		framework = frameworkFactory.newFramework(getConfig());
		framework.start();
		LOGGER.info("OSGI framework has started");
	}

	@PreDestroy
	public void stop() throws BundleException, InterruptedException {
		LOGGER.info("Stopping OSGI framework...");
		framework.stop();
		framework.waitForStop(0);
		LOGGER.info("OSGI framework has been stopped");
	}

	/**
	 * We had to add extra package exports to use our services from the spring boot
	 * application (where we've started the framework). If you have any question see
	 * the links below.
	 * 
	 * @see <a href=
	 *      "https://stackoverflow.com/questions/15270044/consuming-services-from-embedded-osgi-framework">Link
	 *      1</a>
	 * @see <a href=
	 *      "https://stackoverflow.com/questions/34117386/embedded-osgi-framework-how-to-call-service-functions">Link
	 *      2</a>
	 */
	private static Map<String, String> getConfig() {
		final HashMap<String, String> config = new HashMap<>();
		config.put("felix.log.level", "4");

		// Clean cache on first init
		config.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);

		// Export (non-direct) api and model packages
		// (so this app can use them)...
		final String[] packages = { "ge.vakho.hello_service_api", "ge.vakho.hello_service_api.model" };
		config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, String.join(",", packages));
		return config;
	}

	@Bean
	public BundleContext bundleContext() throws BundleException {
		return framework.getBundleContext();
	}
}