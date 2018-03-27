package com.nowellpoint.registration.util;

public class EnvironmentVariables {
	
	private static final String MONGO_CLIENT_URI = "MONGO_CLIENT_URI";
	private static final String SENDGRID_API_KEY = "SENDGRID_API_KEY";
	private static final String REDIS_ENCRYPTION_KEY = "REDIS_ENCRYPTION_KEY";
	private static final String REDIS_HOST = "REDIS_HOST";
	private static final String REDIS_PORT = "REDIS_PORT";
	private static final String REDIS_PASSWORD = "REDIS_PASSWORD";
	
	public static String getMongoClientUri() {
		return getenv(MONGO_CLIENT_URI);
	}
	
	public static Integer getRedisPort() {
		return Integer.valueOf(getenv(REDIS_PORT));
	}
	
	public static String getSendgridApiKey() {
		return getenv(SENDGRID_API_KEY);
	}
	
	public static String getRedisEncryptionKey() {
		return getenv(REDIS_ENCRYPTION_KEY);
	}
	
	public static String getRedisHost() {
		return getenv(REDIS_HOST);
	}
	
	public static String getRedisPassword() {
		return getenv(REDIS_PASSWORD);
	}
	
	private static String getenv(String name) {
		return System.getenv(name);
	}
}