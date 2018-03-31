package com.nowellpoint.registration.test;

import org.junit.Test;

import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

public class OktaAuthenticationTest {
	
	@Test
	public void testApiKeyAuth() {
		HttpResponse httpResponse = RestResource.post("https://dev-309807.oktapreview.com/oauth2/default")
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("v1")
				.path("authorize")
				.parameter("grant_type", "client_credentials")
				.parameter("scope", "api")
				.execute();
		
		System.out.println(httpResponse.getStatusCode());
		System.out.println(httpResponse.getAsString());
	}
}