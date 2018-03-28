package com.nowellpoint.registration.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProvider {
	
	public static final String REGISTRATION_ID_NOT_FOUND = "registration.id.not.found";
	public static final String INVALID_VALUE_FOR_ID = "invalid.value.for.id";
	private static final String REGISTRATION_INVALID_PLAN = "registration.invalid.plan";
	private static final String REGISTRATION_INVALIED_OR_EXPIRED = "registration.invalid.or.expired";
	
	public static String getInvalidPlan(Locale locale) {
		return getMessage(locale, REGISTRATION_INVALID_PLAN);
	}
	
	public static String getInvalidOrExpiredRegistration(Locale locale) {
		return getMessage(locale, REGISTRATION_INVALIED_OR_EXPIRED);
	}
	
	public static String getMessage(Locale locale, String key) {
		return ResourceBundle.getBundle("messages", locale).getString(key);
	}
}