package com.nowellpoint.registration.model;

import java.net.URI;
import java.time.Instant;
import java.util.Date;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.RegistrationResource;
import com.nowellpoint.registration.model.Meta;
import com.nowellpoint.registration.model.Registration;
import com.nowellpoint.registration.model.UserInfo;
import com.nowellpoint.registration.util.UriInfoThreadLocal;
import com.nowellpoint.util.Assert;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Registration.class)
@JsonDeserialize(as = Registration.class)
@JsonPropertyOrder({ "id", "meta", "createdBy", "createdOn", "lastUpdatedBy", "lastUpdatedOn" })
public abstract class AbstractRegistration {
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getCountryCode();
	public abstract @Nullable String getDomain();
	public abstract @Nullable String getIdentityHref(); 
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	
	private static final Date now = Date.from(Instant.now());
	
	@Value.Derived
	public String getName() {
		return Assert.isNotNullOrEmpty(getFirstName()) ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	@Value.Derived
	public URI getEmailVerificationHref() {
		return UriBuilder.fromUri(UriInfoThreadLocal.get().getBaseUri()) 
				.path(RegistrationResource.class)
				.path("{id}")
				.path("email-verification-token")
				.path("{emailVerificationToken}")
				.build(getId(), getEmailVerificationToken());
	}
	
	@Value.Derived
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(RegistrationResource.class)
				.build();
	}
	
	@Value.Default
	public String getId() {
		return new ObjectId().toString();
	}
	
	@Value.Default
	public String getPlan() {
		return "FREE";
	}
	
	@Value.Default
	public Boolean getVerified() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Long getExpiresAt() {
		return Instant.now().plusSeconds(1209600).toEpochMilli();
	}
	
	@JsonIgnore
	@Value.Default
	public String getEmailVerificationToken() {
		return RandomStringUtils.randomAlphanumeric(32);
	}
	
	@Value.Default
	public Date getCreatedOn() {
		return now;
	}
	
	@Value.Default
	public Date getLastUpdatedOn() {
		return now;
	}
	
	@JsonIgnore
	public String asJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
}