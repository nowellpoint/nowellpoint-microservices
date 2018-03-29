package com.nowellpoint.registration.rest.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import com.nowellpoint.api.RegistrationResource;
import com.nowellpoint.registration.model.LogEntry;

@Provider
public class ResourceRequestFilter implements ContainerRequestFilter, ContainerResponseFilter {
	
	@Inject
	private Logger logger;
	
	@Context
	private UriInfo uriInfo;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		final Integer statusCode = responseContext.getStatus();
		final String statusInfo = responseContext.getStatusInfo().toString();
		final String requestMethod = requestContext.getMethod();
		final String requestUri = uriInfo.getRequestUri().toString();
		
		LogEntry logEntry = LogEntry.builder()
				.statusCode(statusCode)
				.statusInfo(statusInfo)
				.requestMethod(requestMethod)
				.requestUri(requestUri)
				.build();
		
		System.out.println(requestContext.getHeaderString("Date"));
		System.out.println(requestContext.getHeaderString("From"));
		
		writeLogEntry(RegistrationResource.class.getName(), logEntry);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
	}
	
	private void writeLogEntry(String tag, LogEntry logEntry) {
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					
					HttpURLConnection connection = (HttpURLConnection) new URL(System.getenv("LOGGLY_API_ENDPOINT")
							.concat("/")
							.concat(System.getenv("LOGGLY_API_KEY"))
							.concat("/")
							.concat(tag)
							.concat("/api")
							.concat("/")
					).openConnection();
					
					connection.setRequestMethod("GET");
					connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
					connection.setDoOutput(true);
					
					byte[] outputInBytes = logEntry.toJson().getBytes("UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write( outputInBytes );    
					os.close();
					
					connection.connect();
					
					if (connection.getResponseCode() != 200) {
						logger.error(IOUtils.toString(connection.getErrorStream(), "UTF-8"));
					}
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});	
	}
}