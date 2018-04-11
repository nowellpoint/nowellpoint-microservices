package com.nowellpoint.authentication.rest.impl;

import java.util.Date;

import org.wildfly.swarm.health.HealthStatus;

import com.nowellpoint.identity.HealthCheckResource;

public class HealthCheckResourceImpl implements HealthCheckResource {
	
	@Override
	public HealthStatus status() {
		return HealthStatus.named("status")
				.withAttribute("date", new Date().toString())
				.up();
	}
}