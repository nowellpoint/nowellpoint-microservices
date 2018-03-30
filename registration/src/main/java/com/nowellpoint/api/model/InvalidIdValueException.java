package com.nowellpoint.api.model;

public class InvalidIdValueException extends ServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8275843048451488765L;
	
	public InvalidIdValueException(String id) {
		super(400, "INVALID_VALUE", getInvalidValueForId(id));
	}
}