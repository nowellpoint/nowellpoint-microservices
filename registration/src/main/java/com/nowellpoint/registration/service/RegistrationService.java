package com.nowellpoint.registration.service;

import com.nowellpoint.api.model.DomainRequest;
import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.api.model.UpgradeRequest;
import com.nowellpoint.registration.model.Registration;

public interface RegistrationService {
	
	public Registration findById(String id);
	
	public Registration register(RegistrationRequest request);
	
	public Registration upgrade(String id, UpgradeRequest request);
	
	public Registration addDomain(String id, DomainRequest request);
	
	public Registration verifyEmail(String id, String emailVerificationToken);
	
	public void resendVerificationEmail(String id);
	
	public void deleteRegistration(String id);
}