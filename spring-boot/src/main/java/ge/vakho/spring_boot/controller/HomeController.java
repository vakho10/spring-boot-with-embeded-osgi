package ge.vakho.spring_boot.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ge.vakho.spring_boot.controller.model.BundleModel;
import ge.vakho.spring_boot.exception.BundleNotFoundException;
import ge.vakho.spring_boot.service.BundleService;

@Controller
@RequestMapping(path = { "/", "/home" })
public class HomeController {

	@Autowired
	private BundleService bundleService;

	@GetMapping
	public String index(Model model) {
		model.addAttribute("bundles",
				Arrays.stream(bundleService.getAll()).map(BundleModel::from).collect(Collectors.toList()));
		return "index";
	}

	@GetMapping("/start")
	public String startBundle(@RequestParam long id) throws BundleNotFoundException, BundleException {
		bundleService.start(id);
		return "redirect:/";
	}

	@GetMapping("/stop")
	public String stopBundle(@RequestParam long id) throws BundleNotFoundException, BundleException {
		bundleService.stop(id);
		return "redirect:/";
	}

	@GetMapping("/uninstall")
	public String uninstallBundle(@RequestParam long id) throws BundleNotFoundException, BundleException {
		bundleService.uninstall(id);
		return "redirect:/";
	}

	@PostMapping
	public String installBundle(@RequestParam MultipartFile file, RedirectAttributes ra) {

		do {
			if (file.isEmpty()) {
				ra.addFlashAttribute("errorMessage", "No file selected! Please select a file.");
				break;
			}

			if (!new Tika().detect(file.getOriginalFilename()).equals("application/java-archive")) {
				ra.addFlashAttribute("errorMessage", "Not a JAR file!");
				break;
			}

			// Check if JAR is a bundle (should at least have 'Bundle-SymbolicName' present
			// in MANIFEST.MF file)
			try (ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
					JarInputStream jarStream = new JarInputStream(bais)) {

				Manifest mf = jarStream.getManifest();
				if (mf == null) {
					ra.addFlashAttribute("errorMessage", "No MANIFEST.MF file present in JAR!");
					break;
				}

				Attributes mainAttribs = mf.getMainAttributes();
				String bundleSymbolicName = mainAttribs.getValue("Bundle-SymbolicName");
				if (bundleSymbolicName == null) {
					ra.addFlashAttribute("errorMessage", "Not an OSGI bundle!");
					break;
				}

				Bundle installedBundle = bundleService.install(file);
				ra.addFlashAttribute("successMessage",
						"Successfully installed bundle: " + installedBundle.getSymbolicName());

			} catch (IOException | BundleException e) {
				ra.addFlashAttribute("errorMessage", e.getMessage());
			}
		} while (false);

		return "redirect:/";
	}
}