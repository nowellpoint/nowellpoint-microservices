package com.nowellpoint.registration.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;

@Entity(value = "registrations", noClassnameStored = true)
@Indexes({
    @Index(
        options = @IndexOptions(unique = true),
        fields = {
            @Field(value = "domain")
        })
})
public class RegistrationEntity extends BaseEntity {
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String phone;
	
	private String countryCode;
	
	private String emailVerificationToken;
	
	private String domain;
	
	private Long expiresAt;
	
	private String plan;
	
	private String identityHref;
	
	private String stage;
	
	@Reference
	private UserProfileEntity createdBy;
	
	@Reference
	private UserProfileEntity lastUpdatedBy;
	
	private Boolean verified;
	
	public RegistrationEntity() {
		
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getIdentityHref() {
		return identityHref;
	}

	public void setIdentityHref(String identityHref) {
		this.identityHref = identityHref;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public UserProfileEntity getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserProfileEntity createdBy) {
		this.createdBy = createdBy;
	}

	public UserProfileEntity getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserProfileEntity lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
}