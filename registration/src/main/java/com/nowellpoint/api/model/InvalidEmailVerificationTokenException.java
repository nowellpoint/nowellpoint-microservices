package com.nowellpoint.api.model;

public class InvalidEmailVerificationTokenException extends ServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4387967318552179121L;	
	
	public InvalidEmailVerificationTokenException(String emailVerificationToken) {
		super(404, "NOT_FOUND", getInvalidEmailVerificationToken(emailVerificationToken));
	}
}