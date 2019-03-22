package ge.vakho.spring_boot_with_embeded_osgi.controller;

import java.util.Arrays;

import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bundle")
public class BundleContextController {

	@Autowired
	private BundleContext bundleContext;

	@GetMapping
	public String bundles() {
		return Arrays.toString(bundleContext.getBundles());
	}
}