package com.nowellpoint.registration.model;

public class RegistrationNotFoundException extends ServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4387967318552179121L;	
	
	public RegistrationNotFoundException(String id) {
		super(404, "NOT_FOUND", getRegistrationIdNotFound(id));
	}
}