package com.nowellpoint.registration.service.impl;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriBuilder;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.mongodb.morphia.query.Query;

import com.nowellpoint.api.RegistrationResource;
import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.api.model.UpdateRegistrationRequest;
import com.nowellpoint.registration.entity.Plan;
import com.nowellpoint.registration.entity.RegistrationDAO;
import com.nowellpoint.registration.entity.RegistrationDocument;
import com.nowellpoint.registration.entity.UserProfile;
import com.nowellpoint.registration.provider.DatastoreProvider;
import com.nowellpoint.registration.service.RegistrationService;
import com.nowellpoint.registration.util.MessageProvider;
import com.nowellpoint.registration.model.ModifiableRegistration;
import com.nowellpoint.registration.model.ModifiableUserInfo;
import com.nowellpoint.registration.model.Registration;
import com.nowellpoint.registration.model.UserInfo;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

public class RegistrationServiceImpl extends AbstractService implements RegistrationService {
	
	@Inject
	private Logger logger;
	
	@Inject
	private DatastoreProvider datastoreProvider;
	
	private RegistrationDAO dao;
	
	@Inject
	private Event<Registration> registrationEvent; 
	
	@PostConstruct
	public void init() {
		dao = new RegistrationDAO(RegistrationDocument.class, datastoreProvider.getDatastore());
	}
	
	@Override
	public Registration findById(String id) {
		RegistrationDocument entity = get(RegistrationDocument.class, id);
		if (Assert.isNull(entity)) {
			try {
				entity = dao.get(new ObjectId(id));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageProvider.INVALID_VALUE_FOR_ID), id));
			}
		}
		if (Assert.isNull(entity)) {
			throw new NotFoundException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageProvider.REGISTRATION_ID_NOT_FOUND), id));
		}
		return modelMapper.map(entity, ModifiableRegistration.class).toImmutable();
	}

	@Override
	public Registration register(RegistrationRequest request) {
		
		Optional<Plan> query = getPlan(request.getPlan());
		
		if (! query.isPresent()) {
			throw new IllegalArgumentException(String.format(MessageProvider.getInvalidPlan(Locale.getDefault()), request.getPlan()));
		}

		//isRegistred(request.getEmail(), request.getDomain());

		/**
		 * 
		 */
		
		UserInfo userInfo = getSystemAdmin();
		
		Date now = getCurrentDate();

		Registration registration = Registration.builder()
				.countryCode(request.getCountryCode())
				.email(request.getEmail())
				.phone(request.getPhone())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.verified(Boolean.FALSE)
				.plan(query.get().getPlanCode())
				.createdBy(userInfo)
				.createdOn(now)
				.lastUpdatedBy(userInfo)
				.lastUpdatedOn(now)
				.domain(Assert.isNotNullOrEmpty(request.getDomain()) ? request.getDomain() : null)
				.expiresAt(Instant.now().plusSeconds(1209600).toEpochMilli())
				.build();

		/**
		 * 
		 */
		
		RegistrationDocument entity = modelMapper.map(registration, RegistrationDocument.class);
		
		dao.save(entity);
		
		set(entity.getId().toString(), entity);
		
		registration = modelMapper.map(entity, ModifiableRegistration.class).toImmutable();

		if (Assert.isNotNullOrEmpty(registration.getDomain())) {
			registrationEvent.fire(registration);
		}

		return registration;
	}
	
	@Override
	public Registration updateRegistration(String id, UpdateRegistrationRequest request) {
		
		//if (Assert.isNullOrEmpty(domain)) {
		//	throw new ValidationException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_MISSING_DOMAIN));
		//}
		
		Registration registration = findById(id);
		
		isExpired(registration.getExpiresAt());
		
		URI emailVerificationTokenUri = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME)) 
				.path(RegistrationResource.class)
				.path("verify-email")
				.path("{emailVerificationToken}")
				.build(registration.getEmailVerificationToken());
		
		logger.info(emailVerificationTokenUri);
		
		UserInfo userInfo = getSystemAdmin();
		
		Date now = getCurrentDate();
		
		Optional<Plan> query = Optional.empty();
		if (Assert.isNotNullOrEmpty(request.getPlan())) {
			query = getPlan(request.getPlan());
		}
		
		Registration instance = Registration.builder()
				.from(registration)
				.domain(request.getDomain())
				.emailVerificationHref(emailVerificationTokenUri)
				.lastUpdatedOn(now)
				.lastUpdatedBy(userInfo)
				.plan(query.isPresent() ? query.get().getPlanCode() : registration.getPlan()) 
				.build();
		
		/**
		 * 
		 */
		
		RegistrationDocument entity = modelMapper.map(registration, RegistrationDocument.class);
		
		dao.save(entity);
		
		registrationEvent.fire(registration);
		
		return instance;
	}
	
	@Override
	public Registration verifyEmail(String emailVerificationToken) {
		Registration registration = findByEmailVerificationToken(emailVerificationToken);
		
		Registration instance = Registration.builder()
				.from(registration)
				.expiresAt(System.currentTimeMillis())
				.verified(Boolean.TRUE)
				.build();
		
		//update(instance);
		
		return instance;
	}
	
	@Override
	public void resendVerificationEmail(String id) {
		Registration registration = findById(id);
		registrationEvent.fire(registration);
	}
	
	@Override
	public Registration provision(
			String id, 
			String cardholderName, 
			String expirationMonth, 
			String expirationYear,
			String number, 
			String cvv) {
		
		Registration registration = findById(id);
		
	//	Plan plan = findPlanById(registration.getPlanId());
		
//		Organization organization = null;
		
//		if (plan.getPrice().getUnitPrice() > 0) {
//			organization = createOrganization(
//					plan,
//					registration.getDomain(), 
//					registration.getFirstName(),
//					registration.getLastName(),
//					registration.getEmail(),
//					registration.getPhone(),
//					registration.getCountryCode(),
//					cardholderName, 
//					expirationMonth, 
//					expirationYear, 
//					number, 
//					cvv);
//		} else {
//			organization = createOrganization(
//					plan,
//					registration.getDomain(), 
//					registration.getFirstName(),
//					registration.getLastName(),
//					registration.getEmail(),
//					registration.getPhone(),
//					registration.getCountryCode());
//		}
//		
//		UserProfile userProfile = createUserProfile(
//				registration.getFirstName(), 
//				registration.getLastName(), 
//				registration.getEmail(), 
//				registration.getPhone(),
//				registration.getCountryCode(),
//				organization);
//		
//		URI uri = UriBuilder.fromUri(EnvUtil.getValue(Variable.API_HOSTNAME))
//				.path(IdentityResource.class)
//				.path("/{organizationId}/{userId}")
//				.build(organization.getId(), userProfile.getId());
		
		Registration instance = Registration.builder()
				.from(registration)
				.identityHref(null) //uri.toString())
				.build();
		
		//update(instance);
		
		return instance;
		
	}
	
	@Override
	public void deleteRegistration(String id) {
		dao.deleteById(new ObjectId(id));
	}
	
	private Registration findByEmailVerificationToken(String emailVerificationToken) {
//		Registration registration = null;
//		try {
//			registration = findOne( Filters.eq ( "emailVerificationToken", emailVerificationToken ) );
//			isExpired(registration.getExpiresAt());
//		} catch (DocumentNotFoundException e) {
//			throw new ValidationException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_INVALID_OR_EXPIRED));
//		}
//		return registration;
		return null;
	}
	
	private void publish(String email) {
//		if (Boolean.valueOf(System.getProperty("registration.send.notification"))) {
//			AmazonSNS snsClient = AmazonSNSClient.builder().build(); 
//			PublishRequest publishRequest = new PublishRequest(
//					"arn:aws:sns:us-east-1:600862814314:REGISTRATION", 
//					MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_RECEIVED), email);
//			
//			snsClient.publish(publishRequest);
//		}
	}
	
	private void isExpired(Long expiresAt) {
//		if (Instant.ofEpochMilli(expiresAt).isBefore(Instant.now())) {
//			throw new ValidationException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_INVALID_OR_EXPIRED));
//		}
	}
	
	//private UserProfile createUserProfile(String firstName, String lastName, String email, String phone, String countryCode, Organization organization) {
		//return userProfileService.createUserProfile(firstName, lastName, email, phone, countryCode, organization);
	//}
	
//	private Organization createOrganization(Plan plan, String domain, String firstName, String lastName, String email, String phone, String countryCode) {
//		return organizationService.createOrganization(
//				plan, 
//				domain, 
//				firstName, 
//				lastName, 
//				email, 
//				phone, 
//				countryCode);
//	}
//	
//	private Organization createOrganization(Plan plan, String domain, String firstName, String lastName, String email, String phone, String countryCode, String cardholderName, String expirationMonth, String expirationYear, String number, String cvv) {
//		return organizationService.createOrganization(
//				plan, 
//				domain, 
//				firstName, 
//				lastName, 
//				email, 
//				phone, 
//				countryCode, 
//				cardholderName, 
//				expirationMonth, 
//				expirationYear, 
//				number, 
//				cvv);
//	}
	
	/*private Plan findPlanById(String planId) {
		try {
    		return planService.findById(planId);
    	} catch (DocumentNotFoundException ignore) {
    		throw new ValidationException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_INVALID_PLAN), planId));
		}
	}*/
	
	private void isRegistred(String username, String domain) {
//		try {
//			userProfileService.findByUsername(username);
//			throw new ValidationException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_ACCOUNT_CONFLICT), username));
//		} catch (DocumentNotFoundException e) {
//			try {
//				organizationService.findByDomain(domain);
//				throw new ValidationException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_DOMAIN_CONFLICT), domain));
//			} catch (DocumentNotFoundException ignore) {
//				publish(username);
//			}
//		}
	}
	
	private UserInfo getSystemAdmin() {
		Query<UserProfile> query = datastoreProvider.getDatastore()
				.createQuery(UserProfile.class)
				.field("username")
				.equal("system.administrator@nowellpoint.com");
				 
		UserProfile userProfile = query.get();
		
		return modelMapper.map(userProfile, ModifiableUserInfo.class).toImmutable();
	}
	
	private Optional<Plan> getPlan(String plan) {
		Query<Plan> query = datastoreProvider.getDatastore()
				.createQuery(Plan.class)
				.field("planCode")
				.equal(plan);
		
		return Optional.ofNullable(query.get());
	}
}