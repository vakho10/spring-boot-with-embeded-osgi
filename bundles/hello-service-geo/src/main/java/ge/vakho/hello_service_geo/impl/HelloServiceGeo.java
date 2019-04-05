package ge.vakho.hello_service_geo.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.i18n.CountryCode;

import ge.vakho.hello_service_api.HelloService;
import ge.vakho.hello_service_api.model.Person;

/**
 * Implementation for {@link HelloService} interface.
 * 
 * @author vakho
 */
public class HelloServiceGeo implements HelloService {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelloService.class); 
	
	@Override
	public String sayHelloTo(Person person) {
		LOGGER.info("Called geo hello service :) for person: {}", person);
		return "გამარჯობა, " + person.getFirstName() + ". ";
	}

	@Override
	public CountryCode getCountryCode() {
		return CountryCode.GE;
	}
}