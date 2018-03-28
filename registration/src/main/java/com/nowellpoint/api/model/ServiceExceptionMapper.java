package com.nowellpoint.api.model;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

	@Override
	public Response toResponse(ServiceException exception) {		
		JsonArrayBuilder errors = Json.createArrayBuilder();
		exception.getMessages().stream().forEach(e -> {
			System.out.println(e);
			errors.add(e);
		});
		
		JsonObject entity = Json.createObjectBuilder()
				.add("errorCode", exception.getErrorCode())
				.add("messages", errors)
				.build();
		
		return Response.status(exception.getStatusCode()).entity(entity).build();
	}
}