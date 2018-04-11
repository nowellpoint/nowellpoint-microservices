package com.nowellpoint.registration.model;

import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.core.Response.Status;

public class ServiceException extends AbstractServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8741790786745579281L;

	public ServiceException(int statusCode, String errorCode, String error) {
		this(statusCode, errorCode, Arrays.asList(error));
	}
	
	public ServiceException(Status status, String errorCode, String error) {
		this(status.getStatusCode(), errorCode, Arrays.asList(error));
	}
	
	public ServiceException(Status status, String errorCode, Collection<String> errors) {
		this(status.getStatusCode(), errorCode, errors);
	}
	
	public ServiceException(int statusCode, String errorCode, Collection<String> errors) {
		super(statusCode, errorCode, errors);
	}
}