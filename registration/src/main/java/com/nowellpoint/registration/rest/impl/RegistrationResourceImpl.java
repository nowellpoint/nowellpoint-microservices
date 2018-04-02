package com.nowellpoint.registration.rest.impl;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.nowellpoint.api.RegistrationResource;
import com.nowellpoint.api.model.DomainRequest;
import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.api.model.UpgradeRequest;
import com.nowellpoint.registration.service.RegistrationService;
import com.nowellpoint.registration.model.Registration;

public class RegistrationResourceImpl implements RegistrationResource {
	
	@Inject
	private RegistrationService registrationService;
	
	@Override
	public Response getRegistration(String id) {
		Registration registration = registrationService.findById(id);
		return Response.ok(registration)
				.build();
	}
    
	@Override
    public Response register(RegistrationRequest request) {
		Registration registration = registrationService.register(request);
		URI uri = UriBuilder.fromPath(registration.getMeta().getHref()).build();
		return Response.created(uri)
				.entity(registration)
				.build();
    }
	
	@Override
	public Response resendVerificationEmail(String id) {
		registrationService.resendVerificationEmail(id);
		return Response.noContent()
				.build();
	}
	
	@Override
	public Response upgrade(String id, UpgradeRequest request) {
		Registration registration = registrationService.upgrade(id, request);
		return Response.ok(registration)
				.build();
	}
	
	@Override
	public Response domain(String id, DomainRequest request) {
		Registration registration = registrationService.addDomain(id, request);
		return Response.ok(registration)
				.build();
	}
    
	@Override
    public Response setPassword(String password, String confirmPassword) {
    	return Response.ok().build();
    }
	
	@Override
	public Response verifyEmail(String id, String emailVerificationToken) {	
		Registration registration = registrationService.verifyEmail(id, emailVerificationToken);
		return Response.ok(registration)
				.build();
	}

	@Override
	public Response deleteRegistration(String id) {
		registrationService.deleteRegistration(id);
		return Response.ok().build();
	}
}