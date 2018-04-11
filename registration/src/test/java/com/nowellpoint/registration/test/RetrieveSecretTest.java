package com.nowellpoint.registration.test;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;

public class RetrieveSecretTest {

	@Test
	public void getSecretTest() {

	 String secretName = "production";
	 String endpoint = "secretsmanager.us-east-1.amazonaws.com";
	 String region = "us-east-1";

	 AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
	 AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
	 clientBuilder.setEndpointConfiguration(config);
	 AWSSecretsManager client = clientBuilder.build();

	 String secret;
	 ByteBuffer binarySecretData;
	 GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
	         .withSecretId(secretName);
	 
	 GetSecretValueResult getSecretValueResponse = null;
	 try {
	     getSecretValueResponse = client.getSecretValue(getSecretValueRequest);

	 } catch(ResourceNotFoundException e) {
	     System.out.println("The requested secret " + secretName + " was not found");
	 } catch (InvalidRequestException e) {
	     System.out.println("The request was invalid due to: " + e.getMessage());
	 } catch (InvalidParameterException e) {
	     System.out.println("The request had invalid params: " + e.getMessage());
	 }

	 if(getSecretValueResponse == null) {
	     return;
	 }

	 // Decrypted secret using the associated KMS CMK
	 // Depending on whether the secret was a string or binary, one of these fields will be populated
	 if(getSecretValueResponse.getSecretString() != null) {
	     secret = getSecretValueResponse.getSecretString();
	     System.out.println(secret);
	 }
	 else {
	     binarySecretData = getSecretValueResponse.getSecretBinary();
	 }
	 
	 // Your code goes here. 
	}
}