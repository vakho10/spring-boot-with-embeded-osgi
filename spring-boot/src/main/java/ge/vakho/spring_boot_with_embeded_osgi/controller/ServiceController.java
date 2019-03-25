package ge.vakho.spring_boot_with_embeded_osgi.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ge.vakho.hello_service_api.HelloService;
import ge.vakho.hello_service_api.model.Person;
import ge.vakho.spring_boot_with_embeded_osgi.controller.model.ServiceRequestModel;

@RestController
@RequestMapping("/service")
public class ServiceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);

	@Autowired
	private BundleContext bundleContext;

	@GetMapping
	public List<String> services() throws InvalidSyntaxException {
		return getServices().map(service -> service.toString()).collect(Collectors.toList());
	}

	@PostMapping
	public String call(@RequestBody @Valid ServiceRequestModel serviceRequestModel) throws InvalidSyntaxException {
		LOGGER.info("Calling any service with person {}", serviceRequestModel);
		return getServices().map(i -> i.sayHelloTo(from(serviceRequestModel))).reduce("", String::concat);
	}

	private Stream<HelloService> getServices() throws InvalidSyntaxException {
		return bundleContext.getServiceReferences(HelloService.class, null) //
				.parallelStream() //
				.map(ref -> bundleContext.getService(ref));
	}

	private Person from(ServiceRequestModel serviceRequestModel) {
		return new Person(serviceRequestModel.getFirstName(), serviceRequestModel.getLastName(),
				serviceRequestModel.getAge());
	}
}