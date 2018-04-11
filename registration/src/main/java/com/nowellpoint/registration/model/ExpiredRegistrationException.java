package com.nowellpoint.registration.model;

public class ExpiredRegistrationException extends ServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8275843048451488765L;
	
	public ExpiredRegistrationException() {
		super(422, "EXPIRED", getInvalidOrExpiredRegistration());
	}
}