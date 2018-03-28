package com.nowellpoint.api.model;

import java.util.Arrays;
import java.util.Collection;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8741790786745579281L;
	
	private int statusCode;
	private String errorCode;
	private Collection<String> messages;
	
	public ServiceException() {
		super();
	}

	public ServiceException(int statusCode, String errorCode, String message) {
		this(statusCode, errorCode, Arrays.asList(message));
	}
	
	public ServiceException(int statusCode, String errorCode, Collection<String> messages) {
		super(messages.iterator().next());
		this.statusCode = statusCode;
		this.errorCode = errorCode;
		this.messages = messages;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Collection<String> getMessages() {
		return messages;
	}
}