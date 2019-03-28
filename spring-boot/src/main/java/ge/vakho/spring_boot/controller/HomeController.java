package ge.vakho.spring_boot.controller;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.osgi.framework.BundleContext;
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

@Controller
@RequestMapping(path = { "/", "/home" })
public class HomeController {

	@Autowired
	private BundleContext bundleContext;

	@GetMapping
	public String index(Model model) {
		model.addAttribute("bundles",
				Arrays.stream(bundleContext.getBundles()).map(BundleModel::from).collect(Collectors.toList()));
		return "index";
	}

	@PostMapping
	public String uploadBundle(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "No file selected! Please select a file.");
		}
		
		if (!new Tika().detect(file.getOriginalFilename()).equals("application/java-archive")) {
			redirectAttributes.addFlashAttribute("errorMessage", "Not a JAR file!");
		}
		
		// TODO logic
//		redirectAttributes.addFlashAttribute("successMessage", "Successfully uploaded file: " + file.getOriginalFilename());

		return "redirect:/";
	}
}