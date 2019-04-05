package ge.vakho.hello_service_rus.impl;

import com.google.gson.Gson;
import com.neovisionaries.i18n.CountryCode;

import ge.vakho.hello_service_api.HelloService;
import ge.vakho.hello_service_api.model.Person;

/**
 * Implementation for {@link HelloService} interface.
 * 
 * @author vakho
 */
public class HelloServiceRus implements HelloService {

	@Override
	public String sayHelloTo(Person person) {
		return "Здравствуй " + new Gson().toJson(person) + "! ";
	}

	@Override
	public CountryCode getCountryCode() {
		return CountryCode.RU;
	}
}