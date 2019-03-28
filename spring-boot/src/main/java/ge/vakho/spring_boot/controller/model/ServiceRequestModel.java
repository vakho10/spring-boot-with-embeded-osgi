package ge.vakho.spring_boot.controller.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class ServiceRequestModel {

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@Positive
	private int age;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "ServiceRequestModel [firstName=" + firstName + ", lastName=" + lastName + ", age=" + age + "]";
	}

}