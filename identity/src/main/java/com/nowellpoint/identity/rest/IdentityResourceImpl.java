package com.nowellpoint.identity.rest;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.nowellpoint.api.IdentityResource;
import com.nowellpoint.api.OrganizationResource;
import com.nowellpoint.api.UserProfileResource;
import com.nowellpoint.api.model.Address;
import com.nowellpoint.api.model.Identity;
import com.nowellpoint.api.model.Meta;
import com.nowellpoint.api.model.Organization;
import com.nowellpoint.api.model.Resources;
import com.nowellpoint.identity.entity.OrganizationDAO;
import com.nowellpoint.identity.entity.OrganizationEntity;
import com.nowellpoint.identity.entity.UserProfileDAO;
import com.nowellpoint.identity.entity.UserProfileEntity;
import com.nowellpoint.identity.provider.DatastoreProvider;
import com.nowellpoint.util.Assert;

public class IdentityResourceImpl implements IdentityResource {
	
	@Inject
	private DatastoreProvider datastoreProvider;
	
	@Context 
	private SecurityContext securityContext;
	
	private UserProfileDAO userProfileDAO;
	
	private OrganizationDAO organizationDAO;
	
	@PostConstruct
	public void init() {
		userProfileDAO = new UserProfileDAO(UserProfileEntity.class, datastoreProvider.getDatastore());
		organizationDAO = new OrganizationDAO(OrganizationEntity.class, datastoreProvider.getDatastore());
	}
	
	@Override
	public Response getIdentity(String organizationId, String userId) {
		
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		if (Assert.isNotEqual(subject, userId)) {
//			throw new ForbiddenException("You are not authorized to access this resource");
//		}
		
		UserProfileEntity userProfile = findUser(userId);
		
		OrganizationEntity organization = findOrganization(organizationId);
		
		String organizationHref = UriBuilder.fromUri(System.getProperty("api.hostname"))
				.path(OrganizationResource.class)
				.build()
				.toString();
		
		String jobsHref = UriBuilder.fromUri(System.getProperty("api.hostname"))
				//.path(JobResource.class)
				.build()
				.toString();
		
		String connectorsHref = UriBuilder.fromUri(System.getProperty("api.hostname"))
				//.path(ConnectorResource.class)
				.build()
				.toString();
		
		Resources resources = Resources.builder()
				.connectors(connectorsHref)
				.organization(organizationHref)
				.jobs(jobsHref)
				.build();
		
		Identity identity = Identity.builder()
				.id(userProfile.getId().toString())
				.firstName(userProfile.getFirstName())
				.lastName(userProfile.getLastName())
				.name(userProfile.getName())
				.locale(userProfile.getLocale())
				.timeZone(userProfile.getTimeZone())
				.resources(resources)
				.meta(Meta.builder()
						.id(userProfile.getId().toString())
						.resourceClass(UserProfileResource.class)
						.build())
				.address(Address.builder()
						.addedOn(userProfile.getAddress().getAddedOn())
						.city(userProfile.getAddress().getCity())
						.countryCode(userProfile.getAddress().getCountryCode())
						.id(userProfile.getAddress().getId())
						.latitude(userProfile.getAddress().getLatitude())
						.longitude(userProfile.getAddress().getLongitude())
						.postalCode(userProfile.getAddress().getPostalCode())
						.state(userProfile.getAddress().getState())
						.street(userProfile.getAddress().getStreet())
						.updatedOn(userProfile.getAddress().getUpdatedOn())
						.build())
				.organization(Organization.builder()
						.domain(organization.getDomain())
						.id(organization.getId().toString())
						.name(organization.getName())
						.number(organization.getNumber())
						//.subscription(organization.)
						//.transactions(elements)
						//.users(elements)
						.build())
				.build();
				
		return Response.ok(identity)
				.build();
	}
	
	private UserProfileEntity findUser(String userId) {
		UserProfileEntity entity = userProfileDAO.get(new ObjectId(userId));
		return entity;
	}
	
	private OrganizationEntity findOrganization(String organizationId) {
		OrganizationEntity entity = organizationDAO.get(new ObjectId(organizationId));
		return entity;
	}
}