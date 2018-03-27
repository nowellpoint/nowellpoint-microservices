package com.nowellpoint.registration.rest.impl;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;

import com.nowellpoint.api.RegistrationResource;
import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.api.model.UpdateRegistrationRequest;
import com.nowellpoint.registration.service.RegistrationService;
import com.nowellpoint.registration.model.Registration;

public class RegistrationResourceImpl implements RegistrationResource {
	
	@Inject
	private Logger logger;
	
	@Inject
	private RegistrationService registrationService;
	
	@Override
	public Response getRegistration(String id) {
		Registration registration = registrationService.findById(id);
		return Response.ok(registration)
				.build();
	}
    
	@Override
    public Response createRegistration(RegistrationRequest request) {
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
	public Response updateRegistration(String id, UpdateRegistrationRequest request) {
		Registration registration = registrationService.updateRegistration(id, request);
		return Response.ok(registration)
				.build();
	}
	
	@Override
	public Response provision(
			String id, 
			String cardholderName, 
			String expirationMonth, 
			String expirationYear,
			String cardNumber, 
			String cvv) {
		
		try {
			
			Registration registration = registrationService.provision(
					id, 
					cardholderName,
					expirationMonth, 
					expirationYear, 
					cardNumber, 
					cvv);
			
			return Response.ok(registration)
					.build();
		
		} catch (Exception e) {
			
			logger.error(e);
			
			//throw new InternalServerErrorException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.UNEXPECTED_EXCEPTION));
			
			return null;
		}
	}
    
	@Override
    public Response setPassword(String password, String confirmPassword) {
    	return Response.ok().build();
    }
	
	@Override
	public Response verifyEmail(String emailVerificationToken) {	
		Registration registration = registrationService.verifyEmail(emailVerificationToken);
		return Response.ok(registration)
				.build();
	}

	@Override
	public Response deleteRegistration(String id) {
		registrationService.deleteRegistration(id);
		return Response.ok().build();
	}
}