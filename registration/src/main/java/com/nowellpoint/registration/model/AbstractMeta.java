package com.nowellpoint.registration.model;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.registration.model.Meta;
import com.nowellpoint.registration.util.UriInfoThreadLocal;
import com.nowellpoint.util.Assert;

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
	
	@Value.Derived
	public String getHref() {
		return UriBuilder.fromUri(UriInfoThreadLocal.get().getBaseUri())
				.path(getResourceClass())
				.path("/{id}")
				.build(Assert.isNotNullOrEmpty(getId()) ? getId() : "{id}")
				.toString();
	}
}