package com.nowellpoint.identity.rest;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import org.mongodb.morphia.query.Query;

import com.nowellpoint.api.IdentityResource;
import com.nowellpoint.api.OrganizationResource;
import com.nowellpoint.api.model.Identity;
import com.nowellpoint.api.model.Organization;
import com.nowellpoint.api.model.Resources;
import com.nowellpoint.authentication.provider.DatastoreProvider;
import com.nowellpoint.identity.entity.OrganizationEntity;
import com.nowellpoint.identity.entity.UserProfileDAO;
import com.nowellpoint.identity.entity.UserProfileEntity;
import com.nowellpoint.util.Assert;

public class IdentityResourceImpl implements IdentityResource {
	
	@Inject
	private DatastoreProvider datastoreProvider;
	
	@Context 
	private SecurityContext securityContext;
	
	private UserProfileDAO userProfileDAO;
	
	@Override
	public Response getIdentity(String organizationId, String userId) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		if (Assert.isNotEqual(subject, userId)) {
			throw new ForbiddenException("You are not authorized to access this resource");
		}
		
		UserProfileEntity userProfile = findUser(userId);
		
		OrganizationEntity organization = findOrganization(organizationId);
		
		String organizationHref = UriBuilder.fromUri(System.getProperty("api.hostname"))
				.path(OrganizationResource.class)
				.build()
				.toString();
		
//		String jobsHref = UriBuilder.fromUri(EnvUtil.getValue(Variable.API_HOSTNAME))
//				.path(JobResource.class)
//				.build()
//				.toString();
		
//		String connectorsHref = UriBuilder.fromUri(EnvUtil.getValue(Variable.API_HOSTNAME))
//				.path(ConnectorResource.class)
//				.build()
//				.toString();
		
		Resources resources = Resources.builder()
				//.connectors(connectorsHref)
				.organization(organizationHref)
				//.jobs(jobsHref)
				.build();
		
		Organization instance = Organization.builder()
				.domain(organization.getDomain())
				.id(organization.getId().toString())
				.name(organization.getName())
				.number(organization.getNumber())
				//.subscription(organization.)
				//.transactions(elements)
				//.users(elements)
				.build();
		
		Identity identity = Identity.builder()
				.id(userProfile.getId().toString())
				.firstName(userProfile.getFirstName())
				.lastName(userProfile.getLastName())
				.name(userProfile.getName())
				.locale(userProfile.getLocale())
				.timeZone(userProfile.getTimeZone())
				.resources(resources)
				//.meta(userProfile.getMeta())
			//	.address(userProfile.getAddress())
				.organization(instance)
				.build();
				
		return Response.ok(identity)
				.build();
	}
	
	private UserProfileEntity findUser(String userId) {
		UserProfileEntity entity = userProfileDAO.findOne(userId, UserProfileEntity.class);
		return entity;
	}
	
	private OrganizationEntity findOrganization(String organizationId) {
		return null;
	}
}