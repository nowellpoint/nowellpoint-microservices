package com.nowellpoint.registration.service.impl;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.mongodb.morphia.query.Query;

import com.netflix.hystrix.HystrixCommandProperties;
import com.nowellpoint.api.model.DomainConflictException;
import com.nowellpoint.api.model.DomainRequest;
import com.nowellpoint.api.model.ExpiredRegistrationException;
import com.nowellpoint.api.model.InvalidEmailVerificationTokenException;
import com.nowellpoint.api.model.InvalidIdValueException;
import com.nowellpoint.api.model.PlanNotFoundException;
import com.nowellpoint.api.model.RegistrationNotFoundException;
import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.api.model.ServiceException;
import com.nowellpoint.api.model.UpgradeRequest;
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
		HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(10);
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
		
		validate(request);

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
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.lastUpdatedBy(userInfo)
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
	public Registration verifyEmail(String id, String emailVerificationToken) {
		
		Registration registration = findById(id);
		
		validateEmailVerificationToken(registration, emailVerificationToken);
		
		Registration instance = Registration.builder()
				.from(registration)
				.lastUpdatedOn(getCurrentDate())
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
		
		save(instance);
		
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
	
	private void validateEmailVerificationToken(Registration registration, String emailVerificationToken) {
		if (! registration.getEmailVerificationToken().equals(emailVerificationToken)) {
			throw new InvalidEmailVerificationTokenException(emailVerificationToken);
		}
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
	
	/**
	 * 
	 * @param request
	 */
	
	private void validate(RegistrationRequest request) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    Validator validator = factory.getValidator();
	    
	    Set<ConstraintViolation<RegistrationRequest>> constraintViolations = validator.validate( request );
	    
	    if (! constraintViolations.isEmpty()) {
	    	Set<String> errors = new HashSet<String>();
	    	constraintViolations.stream().forEach(v -> {
	    		errors.add(v.getMessage());
	    	});
	    	
	    	throw new ServiceException(Status.BAD_REQUEST, "VALIDATION_FAILED", errors);
	    }
	}
	
//	class CreateCustomer extends HystrixCommand<Registration> {
//		public CreateCustomer() {
//			super(HystrixCommandGroupKey.Factory.asKey("Registration"));
//		}
//
//		@Override
//		protected Customer run() {
//			
//		}
//	}
}