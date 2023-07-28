package com.tcs.sgv.dcps.valueobject;

import java.util.Date;


public class InterestRate {

	private Long interestRateIdPk;
	private Float interest;
	private Date effectiveFromDate;
	private Date applicableToDate;
	private Long status;
	private Long PostId;
	private Long UserId;
	private Date CreatedDate;
	private Long UpdatedPostId;
	private Long UpdatedUserId;
	private Date UpdatedDate;
	
	private Long finYearId;

	public Long getInterestRateIdPk() {

		return interestRateIdPk;
	}

	public void setInterestRateIdPk(Long interestRateIdPk) {

		this.interestRateIdPk = interestRateIdPk;
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

	/**
	 * @return the status
	 */
	public Long getStatus() {

		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Long status) {

		this.status = status;
	}

	public Long getPostId() {

		return PostId;
	}

	public void setPostId(Long postId) {

		this.PostId = postId;
	}

	public Long getUserId() {

		return UserId;
	}

	public void setUserId(Long userId) {

		this.UserId = userId;
	}

	public Date getCreatedDate() {

		return CreatedDate;
	}

	public void setCreatedDate(Date createdDate) {

		this.CreatedDate = createdDate;
	}

	public Long getUpdatedPostId() {

		return UpdatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {

		this.UpdatedPostId = updatedPostId;
	}

	public Long getUpdatedUserId() {

		return UpdatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {

		this.UpdatedUserId = updatedUserId;
	}

	public Date getUpdatedDate() {

		return UpdatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {

		this.UpdatedDate = updatedDate;
	}
	
	public Long getFinYearId() {

		return finYearId;
	}

	public void setFinYearId(Long finYearId) {

		this.finYearId = finYearId;
	}
}
