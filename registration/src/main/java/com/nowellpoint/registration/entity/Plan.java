package com.nowellpoint.registration.entity;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "plans")
public class Plan extends BaseEntity {
	
	private Boolean recommendedPlan;
	
	private String locale;
	
	private String language;
	
	private String planName;
	
	private String planCode;
	
	private String billingFrequency;
	
	private Boolean isActive;
	
	public Plan() {
		
	}

	public Boolean getRecommendedPlan() {
		return recommendedPlan;
	}

	public String getLocale() {
		return locale;
	}

	public String getLanguage() {
		return language;
	}

	public String getPlanName() {
		return planName;
	}

	public String getPlanCode() {
		return planCode;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public Boolean getIsActive() {
		return isActive;
	}
}