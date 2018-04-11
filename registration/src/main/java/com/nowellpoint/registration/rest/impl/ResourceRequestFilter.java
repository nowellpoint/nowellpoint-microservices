package com.nowellpoint.registration.rest.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.RegistrationResource;
import com.nowellpoint.api.model.LogEntry;
import com.nowellpoint.registration.util.EnvironmentVariables;
import com.nowellpoint.registration.util.LocaleThreadLocal;
import com.nowellpoint.registration.util.UriInfoThreadLocal;

@Provider
public class ResourceRequestFilter implements ContainerRequestFilter, ContainerResponseFilter {
	
	private static final String START_TIME = "startTime";
	
	@Inject
	private Logger logger;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest httpRequest;
	
	@Context
    private HttpHeaders headers;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		final Integer statusCode = responseContext.getStatus();
		final String statusInfo = responseContext.getStatusInfo().toString();
		final String requestMethod = requestContext.getMethod();
		final String requestUri = uriInfo.getRequestUri().toString();
		final Long startTime = Long.valueOf(httpRequest.getAttribute(START_TIME).toString());
		final Long duration = System.currentTimeMillis() - startTime;
		final Locale locale = httpRequest.getLocale() != null ? httpRequest.getLocale() : Locale.getDefault();
		
		LogEntry logEntry = LogEntry.builder()
				.statusCode(statusCode)
				.statusInfo(statusInfo)
				.requestMethod(requestMethod)
				.requestUri(requestUri)
				.startTime(startTime)
				.duration(duration)
				.locale(locale)
				.build();
		
		writeLogEntry(RegistrationResource.class.getName(), logEntry);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		httpRequest.setAttribute(START_TIME, System.currentTimeMillis());
		UriInfoThreadLocal.set(uriInfo);
		LocaleThreadLocal.set(Locale.getDefault());
	}
	
	private void writeLogEntry(String tag, Object logEntry) {
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					
					HttpURLConnection connection = (HttpURLConnection) new URL(EnvironmentVariables.getLogglyApiEndpoint()
							.concat("/")
							.concat(EnvironmentVariables.getLogglyApiKey())
							.concat("/")
							.concat(tag)
							.concat("/api")
							.concat("/")
					).openConnection();
					
					connection.setRequestMethod("GET");
					connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
					connection.setDoOutput(true);
					
					String json = new ObjectMapper().writeValueAsString(logEntry);
					
					byte[] outputInBytes = json.getBytes("UTF-8");
					OutputStream outputStream = connection.getOutputStream();
					outputStream.write( outputInBytes );    
					outputStream.close();
					
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