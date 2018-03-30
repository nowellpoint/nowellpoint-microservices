package com.nowellpoint.api.model;

public class UserConflictException extends ServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8275843048451488765L;
	
	public UserConflictException(String username) {
		super(400, "USER_CONFLICT", getRegistrationUserConflict(username));
	}
}