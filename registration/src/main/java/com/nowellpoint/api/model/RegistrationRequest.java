package com.nowellpoint.api.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {
	
	private String firstName;
	
	@NotNull(message = "{registration.last.name.notnull}")
    @Size(min=1, max=50)
	private String lastName;
	
	@Email
	private String email;
	
	private String phone;
	
	@NotNull
	@Size(min=2, max=2)
	private String countryCode;
	
	@NotNull
	@Size(min=1, max=100)
	private String domain;
	
	@NotNull
	private String plan;
	
	public RegistrationRequest() {
		
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getDomain() {
		return domain;
	}

	public String getPlan() {
		return plan;
	}
}