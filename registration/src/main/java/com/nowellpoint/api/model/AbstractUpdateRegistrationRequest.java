package com.nowellpoint.api.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = UpdateRegistrationRequest.class)
@JsonDeserialize(as = UpdateRegistrationRequest.class)
public abstract class AbstractUpdateRegistrationRequest {
	public abstract @Nullable String getDomain();
	public abstract String getPlan();
}