package com.nowellpoint.api.model;

import java.util.Locale;

public class RegistrationNotFoundException extends ServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4387967318552179121L;	
	
	public RegistrationNotFoundException(String id) {
		super(404, "NOT_FOUND", getRegistrationIdNotFound(Locale.getDefault(), id));
	}
}