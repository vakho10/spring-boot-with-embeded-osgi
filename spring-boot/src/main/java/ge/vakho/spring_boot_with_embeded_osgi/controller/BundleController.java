package ge.vakho.spring_boot_with_embeded_osgi.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ge.vakho.spring_boot_with_embeded_osgi.controller.model.BundleModel;

@RestController
@RequestMapping("/bundle")
public class BundleController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleController.class);

	@Autowired
	private BundleContext bundleContext;

	@PostConstruct
	public void init() throws BundleException {
		LOGGER.info("Installing bundles...");

		Bundle helloServiceGeo = bundleContext.installBundle(
				"file:E:\\Projects\\github\\spring-boot-with-embeded-osgi\\hello-service-geo\\target\\hello-service-geo-0.0.1-SNAPSHOT.jar");
		helloServiceGeo.start();
		LOGGER.info("Installed bundle {} with state {}", helloServiceGeo, helloServiceGeo.getState());

		Bundle helloServiceRus = bundleContext.installBundle(
				"file:E:\\Projects\\github\\spring-boot-with-embeded-osgi\\hello-service-rus\\target\\hello-service-rus-0.0.1-SNAPSHOT.jar");
		helloServiceRus.start();
		LOGGER.info("Installed bundle {} with state {}", helloServiceRus, helloServiceRus.getState());
	}

	@GetMapping
	public List<BundleModel> bundles() {
		return Arrays.stream(bundleContext.getBundles()).map(this::from).collect(Collectors.toList());
	}
	
	@GetMapping("/start/{bundleId}")
	public void start(@PathVariable long bundleId) throws BundleException {
		bundleContext.getBundle(bundleId).start();
	}
	
	@GetMapping("/stop/{bundleId}")
	public void stop(@PathVariable long bundleId) throws BundleException {
		bundleContext.getBundle(bundleId).stop();
	}
	
	@GetMapping("/uninstall/{bundleId}")
	public void uninstall(@PathVariable long bundleId) throws BundleException {
		bundleContext.getBundle(bundleId).uninstall();
		// TODO Remove bundle's JAR files if any... 
	}

	private BundleModel from(Bundle bundle) {
		return new BundleModel(bundle.getBundleId(), bundle.getSymbolicName(), bundle.getState());
	}
}