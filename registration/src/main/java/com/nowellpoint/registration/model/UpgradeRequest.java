package com.nowellpoint.registration.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.CreditCardNumber;

public class UpgradeRequest {
	
	@NotNull
	private String plan;
	
	@NotNull
	private String cardholderName;
	
	@NotNull
	private String expirationMonth;
	
	@NotNull
	private String expirationYear;
	
	@NotNull
	@CreditCardNumber
	private String cardNumber;
	
	@NotNull
	private String cvv;
	
	public UpgradeRequest() {
		
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