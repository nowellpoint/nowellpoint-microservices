package com.nowellpoint.registration.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.registration.model.ErrorMessage;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = ErrorMessage.class)
@JsonDeserialize(as = ErrorMessage.class)
public abstract class AbstractErrorMessage {
	public abstract String getErrorCode();
	public abstract String getMessage();
}