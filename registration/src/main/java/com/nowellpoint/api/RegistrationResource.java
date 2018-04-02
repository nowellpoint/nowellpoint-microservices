package com.nowellpoint.api;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.model.DomainRequest;
import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.api.model.UpgradeRequest;

@Path("/registrations")
public interface RegistrationResource {
	
	@PermitAll
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistration(@PathParam("id") String id);
	
	@PermitAll
	@POST
	@Path("{id}/email-verification-token/{emailVerificationToken}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifyEmail(@PathParam("id") String id, @PathParam("emailVerificationToken") String emailVerificationToken);
	
	@PermitAll
	@GET
	@Path("{id}/email-verification-token")
	public Response resendVerificationEmail(@PathParam("id") String id);
	
	@PermitAll
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response register(RegistrationRequest request);
	
	@PermitAll
	@POST
	@Path("{id}/domain")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response domain(@PathParam("id") String id, DomainRequest request);
	
	@PermitAll
	@POST
	@Path("{id}/plan")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response upgrade(@PathParam("id") String id, UpgradeRequest request);
	
	@PermitAll
	@POST
	@Path("{id}/password")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response setPassword(
    		@FormParam("password") String password, 
    		@FormParam("confirmPassword") String confirmPassword);
	
	@PermitAll
	@DELETE
	@Path("{id}")
    public Response deleteRegistration(@PathParam("id") String id);
	
}