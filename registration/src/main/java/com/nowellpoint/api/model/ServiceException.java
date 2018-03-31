package com.nowellpoint.api.model;

import java.util.Arrays;
import java.util.Collection;

public class ServiceException extends AbstractServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8741790786745579281L;

	public ServiceException(int statusCode, String errorCode, String error) {
		this(statusCode, errorCode, Arrays.asList(error));
	}
	
	public ServiceException(int statusCode, String errorCode, Collection<String> errors) {
		super(statusCode, errorCode, errors);
	}
}