package com.tcs.sgv.gpf.valueobject;

import java.util.Date;

public class MstGpfInterestRate {

	private Long gpfInterestRateId;
	private Long empGroup;
	private Float interest;
	private Date effectiveFromDate;
	private Date applicableToDate;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	public Long getGpfInterestRateId() {
		return gpfInterestRateId;
	}

	public void setGpfInterestRateId(Long gpfInterestRateId) {
		this.gpfInterestRateId = gpfInterestRateId;
	}

	public Long getEmpGroup() {
		return empGroup;
	}

	public void setEmpGroup(Long empGroup) {
		this.empGroup = empGroup;
	}

	public Float getInterest() {
		return interest;
	}

	public void setInterest(Float interest) {
		this.interest = interest;
	}

	public Date getEffectiveFromDate() {
		return effectiveFromDate;
	}

	public void setEffectiveFromDate(Date effectiveFromDate) {
		this.effectiveFromDate = effectiveFromDate;
	}

	public Date getApplicableToDate() {
		return applicableToDate;
	}

	public void setApplicableToDate(Date applicableToDate) {
		this.applicableToDate = applicableToDate;
	}

	public Long getCreatedPostId() {
		return createdPostId;
	}

	public void setCreatedPostId(Long createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Long getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(Long createdUserId) {
		this.createdUserId = createdUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Long getUpdatedPostId() {
		return updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

}
