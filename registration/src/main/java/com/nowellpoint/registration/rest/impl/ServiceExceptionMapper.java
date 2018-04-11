package com.nowellpoint.registration.rest.impl;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.registration.model.ServiceException;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

	@Override
	public Response toResponse(ServiceException exception) {		
		JsonArrayBuilder errors = Json.createArrayBuilder();
		exception.getErrors().stream().forEach(e -> {
			errors.add(e);
		});
		
		JsonObject entity = Json.createObjectBuilder()
				.add("errorCode", exception.getErrorCode())
				.add("errorMessage", exception.getMessage())
				.add("errors", errors)
				.build();
		
		return Response.status(exception.getStatusCode()).entity(entity).build();
	}
}