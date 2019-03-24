package ge.vakho.hello_service_geo.impl;

import ge.vakho.hello_service_api.HelloService;
import ge.vakho.hello_service_api.model.Person;

/**
 * Implementation for {@link HelloService} interface.
 * 
 * @author vakho
 */
public class HelloServiceGeo implements HelloService {

	@Override
	public String sayHelloTo(Person person) {
		return "გამარჯობა, " + person.getFirstName() + ". ";
	}
}