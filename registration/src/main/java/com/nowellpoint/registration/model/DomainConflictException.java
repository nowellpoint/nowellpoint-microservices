package com.nowellpoint.registration.model;

public class DomainConflictException extends ServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8275843048451488765L;
	
	public DomainConflictException(String domain) {
		super(400, "DOMAIN_CONFLICT", getRegistrationDomainConflict(domain));
	}
}