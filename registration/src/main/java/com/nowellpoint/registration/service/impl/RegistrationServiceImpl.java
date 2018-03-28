package com.nowellpoint.registration.service.impl;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.mongodb.morphia.query.Query;

import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.api.model.ServiceException;
import com.nowellpoint.registration.entity.PlanDocument;
import com.nowellpoint.registration.entity.RegistrationDAO;
import com.nowellpoint.registration.entity.RegistrationDocument;
import com.nowellpoint.registration.entity.UserProfile;
import com.nowellpoint.registration.model.ModifiableRegistration;
import com.nowellpoint.registration.model.ModifiableUserInfo;
import com.nowellpoint.registration.model.Registration;
import com.nowellpoint.registration.model.UserInfo;
import com.nowellpoint.registration.provider.DatastoreProvider;
import com.nowellpoint.registration.service.RegistrationService;
import com.nowellpoint.registration.util.MessageProvider;
import com.nowellpoint.util.Assert;

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
				throw new ServiceException(400, "INVALID_VALUE", String.format(MessageProvider.getMessage(Locale.getDefault(), MessageProvider.INVALID_VALUE_FOR_ID), id));
			}
		}
		if (Assert.isNull(entity)) {
			throw new NotFoundException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageProvider.REGISTRATION_ID_NOT_FOUND), id));
		}
		return modelMapper.map(entity, ModifiableRegistration.class).toImmutable();
	}

	@Override
	public Registration register(RegistrationRequest request) {
		
		Optional<PlanDocument> query = getPlan(request.getPlan());
		
		if (! query.isPresent()) {
			throw new ServiceException(400, "INVALID_VALUE", String.format(MessageProvider.getInvalidPlan(Locale.getDefault()), request.getPlan()));
		}

		//isRegistred(request.getEmail(), request.getDomain());

		/**
		 * 
		 */
		
		UserInfo userInfo = getSystemAdmin();

		Registration instance = Registration.builder()
				.countryCode(request.getCountryCode())
				.createdBy(userInfo)
				.domain(request.getDomain())
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.lastUpdatedBy(userInfo)
				.plan(query.get().getPlanCode())
				.phone(request.getPhone())
				.build();

		/**
		 * 
		 */
		
		save(instance);

		registrationEvent.fire(instance);

		return instance;
	}
	
	@Override
	public Registration updateRegistration(String id, RegistrationRequest request) {
		
		Registration registration = findById(id);
		
		if (checkExpired(registration.getExpiresAt())) {
			
			Registration instance = Registration.builder()
					.from(registration)
					.domain(UUID.randomUUID().toString())
					.lastUpdatedOn(getCurrentDate())
					.lastUpdatedBy(getSystemAdmin())
					.build();
			
			save(instance);
			
			throw new ServiceException(422, "EXPIRED", MessageProvider.getInvalidOrExpiredRegistration(Locale.getDefault()));
			
		} else {
						
			Optional<PlanDocument> query = getPlan(Assert.isNotNullOrEmpty(request.getPlan()) ? 
					request.getPlan() : 
						registration.getPlan());
			
			if (query.isPresent() == Boolean.FALSE) {
				throw new ServiceException(400, "INVALID_VALUE", String.format(MessageProvider.getInvalidPlan(Locale.getDefault()), request.getPlan()));
			}
			
			String domain = Assert.isNotNullOrEmpty(request.getDomain()) ? request.getDomain() : registration.getDomain();
			String countryCode = Assert.isNotNullOrEmpty(request.getCountryCode()) ? request.getCountryCode() : registration.getCountryCode();
			String email = Assert.isNotNullOrEmpty(request.getEmail()) ? request.getEmail() : registration.getEmail();
			String lastName = Assert.isNotNullOrEmpty(request.getLastName()) ? request.getLastName() : registration.getLastName();
			Boolean verified = Assert.isNotEqual(request.getEmail(), registration.getEmail());
			
			Registration instance = Registration.builder()
					.from(registration)
					.countryCode(countryCode)
					.domain(domain)
					.email(email)
					.lastUpdatedOn(getCurrentDate())
					.lastUpdatedBy(getSystemAdmin())
					.firstName(request.getFirstName())
					.lastName(lastName)
					.plan(query.get().getPlanCode()) 
					.phone(request.getPhone())
					.verified(verified)
					.build();
			
			save(instance);
			
			if (!instance.getVerified()) {
				registrationEvent.fire(instance);
			}
			
			return instance;
		}
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
	
	private void save(Registration registration) {
		RegistrationDocument entity = modelMapper.map(registration, RegistrationDocument.class);
		dao.save(entity);
		set(entity.getId().toString(), entity);
		registration = modelMapper.map(entity, ModifiableRegistration.class).toImmutable();
	}
	
	private Boolean checkExpired(Long expiresAt) {
		return Instant.ofEpochMilli(expiresAt).isBefore(Instant.now());
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
	
	private Optional<PlanDocument> getPlan(String plan) {
		Query<PlanDocument> query = datastoreProvider.getDatastore()
				.createQuery(PlanDocument.class)
				.field("planCode")
				.equal(plan);
		
		return Optional.ofNullable(query.get());
	}
}