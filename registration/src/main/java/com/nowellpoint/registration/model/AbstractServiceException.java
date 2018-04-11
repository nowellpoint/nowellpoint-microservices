package com.nowellpoint.registration.model;

import java.util.Collection;
import java.util.ResourceBundle;

import com.nowellpoint.registration.util.LocaleThreadLocal;

abstract class AbstractServiceException extends RuntimeException {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3784726879667516118L;
	private static final String REGISTRATION_NOT_FOUND = "registration.not.found";
	private static final String INVALID_VALUE_FOR_ID = "invalid.value.for.id";
	private static final String REGISTRATION_INVALID_PLAN = "registration.invalid.plan";
	private static final String REGISTRATION_INVALIED_OR_EXPIRED = "registration.invalid.or.expired";
	private static final String REGISTRATION_USER_CONFLICT = "registration.user.conflict";
	private static final String REGISTRATION_DOMAIN_CONFLICT = "registration.domain.conflict";
	private static final String INVALID_EMAIL_VERIFICATION_TOKEN = "invalid.email.verification.token";
	
	private int statusCode;
	private String errorCode;
	private Collection<String> errors;
	
	public AbstractServiceException(int statusCode, String errorCode, Collection<String> errors) {
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
	
	public static String getRegistrationIdNotFound(String id) {
		return getMessage(REGISTRATION_NOT_FOUND, id);
	}
	
	public static String getInvalidValueForId(String id) {
		return getMessage(INVALID_VALUE_FOR_ID, id);
	}
	
	public static String getInvalidPlan(String plan) {
		return getMessage(REGISTRATION_INVALID_PLAN, plan);
	}
	
	public static String getInvalidOrExpiredRegistration() {
		return getMessage(REGISTRATION_INVALIED_OR_EXPIRED);
	}
	
	public static String getRegistrationUserConflict(String username) {
		return getMessage(REGISTRATION_USER_CONFLICT, username);
	}
	
	public static String getRegistrationDomainConflict(String domain) {
		return getMessage(REGISTRATION_DOMAIN_CONFLICT, domain);
	}
	
	public static String getInvalidEmailVerificationToken(String emailVerificationToken) {
		return getMessage(INVALID_EMAIL_VERIFICATION_TOKEN, emailVerificationToken);
	}
	
	private static String getMessage(String key, Object... args) {
		return String.format(ResourceBundle.getBundle("messages", LocaleThreadLocal.get()).getString(key), args);
	}
}