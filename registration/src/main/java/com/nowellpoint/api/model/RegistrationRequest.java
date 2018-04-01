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
	
	@NotNull
	@Size(min=2, max=2)
	private String countryCode;
	
	@NotNull
	@Size(min=1, max=100)
	private String domain;
	
	@NotNull
	private String plan;
	
	private String cardholderName;
	
	private String expirationMonth;
	
	private String expirationYear;
	
	private String cardNumber;
	
	private String cvv;
	
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

	public String getCountryCode() {
		return countryCode;
	}

	public String getDomain() {
		return domain;
	}

	public String getPlan() {
		return plan;
	}

	public String getCardholderName() {
		return cardholderName;
	}

	public String getExpirationMonth() {
		return expirationMonth;
	}

	public String getExpirationYear() {
		return expirationYear;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public String getCvv() {
		return cvv;
	}
}