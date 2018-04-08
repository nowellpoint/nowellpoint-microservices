package com.nowellpoint.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.model.Identity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("identity")
@Api(value = "/identity")
public interface IdentityResource {
	
	@GET
	@Path("{organizationId}/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Resturns the identity for the access token", notes = "Returns the Identity", response = Identity.class)
	public Response getIdentity(@ApiParam(value = "Organization Id", required = true) @PathParam("organizationId") String organizationId, @ApiParam(value = "User Id", required = true) @PathParam("userId") String userId);
}