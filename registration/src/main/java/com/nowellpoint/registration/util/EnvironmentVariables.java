package com.nowellpoint.registration.util;

public class EnvironmentVariables {
	
	private static final String MONGO_CLIENT_URI = "MONGO_CLIENT_URI";
	private static final String SENDGRID_API_KEY = "SENDGRID_API_KEY";
	private static final String REDIS_ENCRYPTION_KEY = "REDIS_ENCRYPTION_KEY";
	private static final String REDIS_HOST = "REDIS_HOST";
	private static final String REDIS_PORT = "REDIS_PORT";
	private static final String REDIS_PASSWORD = "REDIS_PASSWORD";
	private static final String PAYMENT_GATEWAY_ENVIRONMENT = "BRAINTREE_ENVIRONMENT";
	private static final String PAYMENT_GATEWAY_MERCHANT_ID = "BRAINTREE_MERCHANT_ID";
	private static final String PAYMENT_GATEWAY_PUBLIC_KEY = "BRAINTREE_PUBLIC_KEY";
	private static final String PAYMENT_GATEWAY_PRIVATE_KEY = "BRAINTREE_PRIVATE_KEY";
	
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
	
	public static String getPaymentGatewayEnvironment() {
		return getenv(PAYMENT_GATEWAY_ENVIRONMENT);
	}
	
	public static String getPaymentGatewayPublicKey() {
		return getenv(PAYMENT_GATEWAY_PUBLIC_KEY);
	}
	
	public static String getPaymentGatewayPrivateKey() {
		return getenv(PAYMENT_GATEWAY_PRIVATE_KEY);
	}
	
	public static String getPaymentGatewayMerchantId() {
		return getenv(PAYMENT_GATEWAY_MERCHANT_ID);
	}
	
	private static String getenv(String name) {
		return System.getenv(name);
	}
}