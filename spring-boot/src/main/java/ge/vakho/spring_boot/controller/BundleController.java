package ge.vakho.spring_boot.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ge.vakho.spring_boot.controller.model.BundleModel;

@RestController
@RequestMapping("/bundle")
public class BundleController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleController.class);

	@Autowired
	private BundleContext bundleContext;

	@GetMapping
	public List<BundleModel> bundles() {
		return Arrays.stream(bundleContext.getBundles()).map(BundleModel::from).collect(Collectors.toList());
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

}