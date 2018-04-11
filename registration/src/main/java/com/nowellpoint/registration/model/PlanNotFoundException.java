package com.nowellpoint.registration.model;

public class PlanNotFoundException extends ServiceException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8275843048451488765L;
	
	public PlanNotFoundException(String plan) {
		super(400, "INVALID_VALUE", getInvalidPlan(plan));
	}
}