package com.nowellpoint.registration.event;

import java.io.IOException;
import java.util.concurrent.Executors;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.braintreegateway.Address;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.api.model.Registration;
import com.nowellpoint.registration.util.EnvironmentVariables;
import com.nowellpoint.util.Properties;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class RegistrationEventListener {
	
	@Inject
	private Logger logger;
	
	private static final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
	private static final SendGrid sendgrid = new SendGrid(EnvironmentVariables.getSendgridApiKey());
	private static final String QUEUE_NAME = "PROVISION_REQUEST";
	
//	private static BraintreeGateway gateway = new BraintreeGateway(
//			Environment.parseEnvironment(EnvironmentVariables.getPaymentGatewayEnvironment()),
//			EnvironmentVariables.getPaymentGatewayMerchantId(),
//			EnvironmentVariables.getPaymentGatewayPublicKey(),
//			EnvironmentVariables.getPaymentGatewayPublicKey()
//	);
//	
//	static {
//		gateway.clientToken().generate();
//	}
	
//	public void createdEventHandler(@Observes @Created Registration registration) {
//		Executors.newSingleThreadExecutor().execute(new Runnable() {
//			@Override
//			public void run() {
//				CustomerRequest request = new CustomerRequest()
//						  .firstName(registration.getFirstName())
//						  .lastName(registration.getLastName())
//						  .company(registration.getDomain())
//						  .email(registration.getEmail());
//				
//				Result<Customer> customerResult = gateway.customer().create(request);
//				
//				AddressRequest addressRequest = new AddressRequest()
//						.countryCodeAlpha2(registration.getCountryCode());
//				
//				Result<Address> addressResult = gateway.address().create(customerResult.getTarget().getId(), addressRequest);
//				
//				CreditCardRequest creditCardRequest = new CreditCardRequest()
////						.cardholderName(registration.getCreditCard().getCardholderName())
////						.expirationMonth(registration.getCreditCard().getExpirationMonth())
////						.expirationYear(registration.getCreditCard().getExpirationYear())
////						.number(registration.getCreditCard().getCardNumber())
////						.cvv(registration.getCreditCard().getCvv())
//						.customerId(customerResult.getTarget().getId())
//						.billingAddressId(addressResult.getTarget().getId());
//				
//				Result<CreditCard> creditCardResult = gateway.creditCard().create(creditCardRequest);
//			}
//		});	
//	}
//	
	public void registrationEventHandler(@Observes Registration registration) {
		
		if (registration.getVerified()) {
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
					try {
						SendMessageRequest request = new SendMessageRequest()
								.withQueueUrl(queueUrl)
								.withMessageBody(registration.asJson());
						
						sqs.sendMessage(request);
						
					} catch (JsonProcessingException e) {
						logger.error(e);
					}
				}
			});
			
		} else {
			
			if (!registration.getVerified()) {
				
			}
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					Email from = new Email();
					from.setEmail(registration.getCreatedBy().getEmail());
					from.setName(registration.getCreatedBy().getName());
				    
				    Email to = new Email();
				    to.setEmail(registration.getEmail());
				    to.setName(registration.getName());
				    
				    Content content = new Content();
				    content.setType("text/html");
				    content.setValue("<html><body>some text here</body></html>");
				    
				    Personalization personalization = new Personalization();
				    personalization.addTo(to);
				    personalization.addSubstitution("%name%", registration.getName());
				    personalization.addSubstitution("%emailVerificationToken%", buildEmailVerificationHref(registration.getEmailVerificationToken()));
				    
				    Mail mail = new Mail();
				    mail.setFrom(from);
				    mail.addContent(content);
				    mail.setTemplateId(System.getProperty(Properties.REGISTRATION_EMAIL_TEMPLATE_ID));
				    mail.addPersonalization(personalization);
				    
				    Request request = new Request();
				    try {
				    	request.setMethod(Method.POST);
				    	request.setEndpoint("mail/send");
				    	request.setBody(mail.build());
				    	Response response = sendgrid.api(request);
				    	logger.info("sendEmailVerificationMessage: " + response.getStatusCode() + " " + response.getBody());
				    } catch (IOException e) {
				    	logger.error(e);
				    }
				}
			});
		}
	}
	
	private String buildEmailVerificationHref(String emailVerificationToken) {
		return UriBuilder.fromUri(System.getProperty(Properties.VERIFY_EMAIL_REDIRECT))
				.queryParam("emailVerificationToken", "{emailVerificationToken}")
				.build(emailVerificationToken)
				.toString();
	}
}