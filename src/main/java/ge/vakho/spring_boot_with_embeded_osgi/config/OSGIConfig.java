package ge.vakho.spring_boot_with_embeded_osgi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
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
		final FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class) //
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
	
	private static Map<String, String> getConfig() {
		HashMap<String, String> config = new HashMap<>();
        config.put("osgi.clean", "true");
		return config;
	}
	
	@Bean
	public BundleContext bundleContext() throws BundleException {
		return framework.getBundleContext();
	} 
}