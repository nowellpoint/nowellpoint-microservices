package com.nowellpoint.api.model;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Meta.class)
@JsonDeserialize(as = Meta.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractMeta {
	public abstract @Nullable String getMethod();
	public abstract @Nullable String getAccepts();
	public abstract @Nullable String getProduces();
	public abstract @Nullable String getRel();
	
	public abstract @JsonIgnore @Nullable Class<?> getResourceClass();
	public abstract @JsonIgnore @Nullable String getId();
	
	public String getHref() {
		return UriBuilder.fromUri(System.getProperty("api.hostname"))
				.path(getResourceClass())
				.path("/{id}")
				.build(getId())
				.toString();
	}
}