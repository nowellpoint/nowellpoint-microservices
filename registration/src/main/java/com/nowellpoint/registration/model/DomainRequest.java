package com.nowellpoint.registration.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DomainRequest {

	@NotNull
	@Size(min=1, max=100)
	private String domain;
	
	public DomainRequest() {
		
	}

	public String getDomain() {
		return domain;
	}
}