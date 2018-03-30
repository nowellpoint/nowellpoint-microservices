package com.nowellpoint.registration.service.impl;

import java.time.Instant;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.mongodb.morphia.query.Query;

import com.nowellpoint.api.model.DomainConflictException;
import com.nowellpoint.api.model.ExpiredRegistrationException;
import com.nowellpoint.api.model.InvalidEmailVerificationTokenException;
import com.nowellpoint.api.model.InvalidIdValueException;
import com.nowellpoint.api.model.PlanNotFoundException;
import com.nowellpoint.api.model.RegistrationNotFoundException;
import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.api.model.UserConflictException;
import com.nowellpoint.registration.entity.OrganizationEntity;
import com.nowellpoint.registration.entity.PlanEntity;
import com.nowellpoint.registration.entity.RegistrationDAO;
import com.nowellpoint.registration.entity.RegistrationEntity;
import com.nowellpoint.registration.entity.UserProfileEntity;
import com.nowellpoint.registration.model.ModifiableRegistration;
import com.nowellpoint.registration.model.ModifiableUserInfo;
import com.nowellpoint.registration.model.Registration;
import com.nowellpoint.registration.model.UserInfo;
import com.nowellpoint.registration.provider.DatastoreProvider;
import com.nowellpoint.registration.service.RegistrationService;
import com.nowellpoint.util.Assert;

public class RegistrationServiceImpl extends AbstractService implements RegistrationService {
	
	@Inject
	private Logger logger;
	
	@Inject
	private DatastoreProvider datastoreProvider;
	
	@Inject
	private Event<Registration> registrationEvent;
	
	private RegistrationDAO dao;
	
	@PostConstruct
	public void init() {
		dao = new RegistrationDAO(RegistrationEntity.class, datastoreProvider.getDatastore());
	}
	
	@Override
	public Registration findById(String id) {
		RegistrationEntity entity = get(RegistrationEntity.class, id);
		if (Assert.isNull(entity)) {
			try {
				entity = dao.get(new ObjectId(id));
				set(entity.getId().toString(), entity);
			} catch (IllegalArgumentException e) {
				throw new InvalidIdValueException(id);
			}
		}
		if (Assert.isNull(entity)) {
			throw new RegistrationNotFoundException(id);
		}
		
		return modelMapper.map(entity, ModifiableRegistration.class).toImmutable();
	}

	@Override
	public Registration register(RegistrationRequest request) {
		
		/**
		 * 
		 */
		
		isValidPlan(request.getPlan());
		
		/**
		 * 
		 */

		isUserRegistred(request.getEmail());
		
		/**
		 * 
		 */
		
		isDomainRegistered(request.getDomain());

		/**
		 * 
		 */
		
		UserInfo userInfo = getSystemAdmin();
		
		/**
		 * 
		 */

		Registration instance = Registration.builder()
				.countryCode(request.getCountryCode())
				.createdBy(userInfo)
				.domain(request.getDomain())
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.lastUpdatedBy(userInfo)
				.plan(request.getPlan())
				.phone(request.getPhone())
				.build();

		/**
		 * 
		 */
		
		save(instance);
		
		/**
		 * 
		 */

		registrationEvent.fire(instance);
		
		/**
		 * 
		 */

		return instance;
	}
	
	@Override
	public Registration updateRegistration(String id, RegistrationRequest request) {
		
		Registration registration = findById(id);
		
		if (Assert.isNotNullOrEmpty(request.getPlan())) {
			isValidPlan(request.getPlan());
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
				.plan(request.getPlan()) 
				.phone(request.getPhone())
				.verified(verified)
				.build();
		
		save(instance);
		
		if (!instance.getVerified()) {
			registrationEvent.fire(instance);
		}
		
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
		
		save(instance);
		
		return instance;
	}
	
	@Override
	public void resendVerificationEmail(String id) {
		Registration registration = findById(id);
		registrationEvent.fire(registration);
	}
	
	private Registration provision(Registration registration) {
		
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
	
	/**
	 * 
	 * @param emailVerificationToken
	 * @return
	 */
	
	private Registration findByEmailVerificationToken(String emailVerificationToken) {
		Query<RegistrationEntity> query = datastoreProvider.getDatastore()
				.createQuery(RegistrationEntity.class)
				.field("emailVerificationToken")
				.equal(emailVerificationToken);
		
		if (query.count() == 0) {
			throw new InvalidEmailVerificationTokenException(emailVerificationToken);
		}
		
		Registration registration = modelMapper.map(query.get(), ModifiableRegistration.class).toImmutable();
		
		isExpired(registration.getExpiresAt());
		
		return registration;
	}
	
	/**
	 * 
	 * @param registration
	 */
	
	private void save(Registration registration) {
		RegistrationEntity entity = modelMapper.map(registration, RegistrationEntity.class);
		dao.save(entity);
		set(entity.getId().toString(), entity);
		registration = modelMapper.map(entity, ModifiableRegistration.class).toImmutable();
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
	
	private void isExpired(Long expiresAt) {
		if (Instant.ofEpochMilli(expiresAt).isBefore(Instant.now())) {
			throw new ExpiredRegistrationException();
		}
	}
	
	/**
	 * 
	 * @param domain
	 */
	
	private void isDomainRegistered(String domain) {
		Query<OrganizationEntity> query = datastoreProvider.getDatastore()
				.createQuery(OrganizationEntity.class)
				.field("domain")
				.equal(domain);
		
		if (query.count() > 0) {
			throw new DomainConflictException(domain);
		} 
	}
	
	/**
	 * 
	 * @param username
	 */
	
	private void isUserRegistred(String username) {
		Query<UserProfileEntity> query = datastoreProvider.getDatastore()
				.createQuery(UserProfileEntity.class)
				.field("username")
				.equal(username);
		
		if (query.count() > 0) {
			throw new UserConflictException(username);
		} 
	}
	
	/**
	 * 
	 * @return UserInfo for System Administrator
	 */
	
	private UserInfo getSystemAdmin() {
		Query<UserProfileEntity> query = datastoreProvider.getDatastore()
				.createQuery(UserProfileEntity.class)
				.field("username")
				.equal("system.administrator@nowellpoint.com");
				 
		UserProfileEntity userProfileEntity = query.get();
		
		return modelMapper.map(userProfileEntity, ModifiableUserInfo.class).toImmutable();
	}
	
	/**
	 * 
	 * @param plan
	 */
	
	private void isValidPlan(String plan) {
		Query<PlanEntity> query = datastoreProvider.getDatastore()
				.createQuery(PlanEntity.class)
				.field("planCode")
				.equal(plan);
		
		if (query.count() == 0) {
			throw new PlanNotFoundException(plan);
		}
	}
}