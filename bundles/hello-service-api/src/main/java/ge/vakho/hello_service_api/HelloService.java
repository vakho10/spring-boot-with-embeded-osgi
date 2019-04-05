package ge.vakho.hello_service_api;

import com.neovisionaries.i18n.CountryCode;

import ge.vakho.hello_service_api.model.Person;

/**
 * Interface for service implementers.
 * 
 * @author vakho
 */
public interface HelloService {

	/**
	 * Says hello to passed {@link Person} object.
	 * 
	 * @param name
	 * @return String that says hello
	 */
	default String sayHelloTo(Person person) {
		return "Hello, " + person.getFirstName() + " " + person.getLastName() + ". ";
	}

	/**
	 * @return {@link CountryCode} object for service.
	 */
	CountryCode getCountryCode();

}