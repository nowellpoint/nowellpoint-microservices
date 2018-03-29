package com.nowellpoint.api.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8741790786745579281L;
	
	private static final String REGISTRATION_NOT_FOUND = "registration.not.found";
	private static final String INVALID_VALUE_FOR_ID = "invalid.value.for.id";
	private static final String REGISTRATION_INVALID_PLAN = "registration.invalid.plan";
	private static final String REGISTRATION_INVALIED_OR_EXPIRED = "registration.invalid.or.expired";
	private static final String REGISTRATION_USER_CONFLICT = "registration.user.conflict";
	private static final String REGISTRATION_DOMAIN_CONFLICT = "registration.domain.conflict";
	
	private int statusCode;
	private String errorCode;
	private Collection<String> errors;
	
	public ServiceException() {
		super();
	}

	public ServiceException(int statusCode, String errorCode, String error) {
		this(statusCode, errorCode, Arrays.asList(error));
	}
	
	public ServiceException(int statusCode, String errorCode, Collection<String> errors) {
		super(String.join(", ", errors));
		this.statusCode = statusCode;
		this.errorCode = errorCode;
		this.errors = errors;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Collection<String> getErrors() {
		return errors;
	}
	
	public static String getRegistrationIdNotFound(Locale locale, String id) {
		return getMessage(locale, REGISTRATION_NOT_FOUND, id);
	}
	
	public static String getInvalidValueForId(Locale locale, String id) {
		return getMessage(locale, INVALID_VALUE_FOR_ID, id);
	}
	
	public static String getInvalidPlan(Locale locale, String plan) {
		return getMessage(locale, REGISTRATION_INVALID_PLAN, plan);
	}
	
	public static String getInvalidOrExpiredRegistration(Locale locale) {
		return getMessage(locale, REGISTRATION_INVALIED_OR_EXPIRED);
	}
	
	public static String getRegistrationUserConflict(Locale locale, String username) {
		return getMessage(locale, REGISTRATION_USER_CONFLICT, username);
	}
	
	public static String getRegistrationDomainConflict(Locale locale, String domain) {
		return getMessage(locale, REGISTRATION_DOMAIN_CONFLICT, domain);
	}
	
	private static String getMessage(Locale locale, String key, Object... args) {
		return String.format(ResourceBundle.getBundle("messages", locale).getString(key), args);
	}
}