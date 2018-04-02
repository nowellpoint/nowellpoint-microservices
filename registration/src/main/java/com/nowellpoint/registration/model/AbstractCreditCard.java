package com.nowellpoint.registration.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = CreditCard.class)
@JsonDeserialize(as = CreditCard.class)
public abstract class AbstractCreditCard {
	public abstract String getCardholderName();
	public abstract String getExpirationMonth();
	public abstract String getExpirationYear();
	public abstract String getCardNumber();
	public abstract String getCvv();
	public abstract String getPaymentMethodId();
}
