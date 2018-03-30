package com.nowellpoint.registration.service;

import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.registration.model.Registration;

public interface RegistrationService {
	
	public Registration findById(String id);
	
	public Registration register(RegistrationRequest request);
	
	public Registration updateRegistration(String id, RegistrationRequest request);
	
	public Registration verifyEmail(String emailVerificationToken);
	
	public void resendVerificationEmail(String id);
	
	public void deleteRegistration(String id);
}