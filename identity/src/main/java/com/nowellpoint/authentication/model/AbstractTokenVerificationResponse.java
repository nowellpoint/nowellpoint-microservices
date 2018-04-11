package com.nowellpoint.authentication.model;

import java.util.Collections;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = TokenVerificationResponse.class)
@JsonDeserialize(as = TokenVerificationResponse.class)
public abstract class AbstractTokenVerificationResponse {
	@JsonProperty(value="active") public abstract Boolean getActive();
	@JsonProperty(value="scope") public abstract String getScope();
	@JsonProperty(value="username") public abstract String getUsername();
	@JsonProperty(value="exp") public abstract Long getExpirationTime();
	@JsonProperty(value="iat") public abstract Long getIssuedAt();
	@JsonProperty(value="sub") public abstract String getSubject();
	@JsonProperty(value="aud") public abstract String getAudience();
	@JsonProperty(value="iss") public abstract String getIssuer();
	@JsonProperty(value="jti") public abstract String getId();
	@JsonProperty(value="token_type") public abstract String getTokenType();
	@JsonProperty(value="client_id") public abstract String getClientId();
	@JsonProperty(value="uid") public abstract String getUserId();
	@JsonProperty(value="groups") @Value.Default public List<String> getGroups() { return Collections.emptyList(); };
}