package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class DARate {

	private Long daRateIdPk;
	private String payCommission;
	private Float daRate;
	private Date effectiveFromDate;
	private Date applicableToDate;
	private Integer status;
	private Long PostId;
	private Long UserId;
	private Date CreatedDate;
	private Long UpdatedPostId;
	private Long UpdatedUserId;
	private Date UpdatedDate;

	public Long getDaRateIdPk() {

		return daRateIdPk;
	}

	public void setDaRateIdPk(Long daRateIdPk) {

		this.daRateIdPk = daRateIdPk;
	}

	public String getPayCommission() {

		return payCommission;
	}

	public void setPayCommission(String payCommission) {

		this.payCommission = payCommission;
	}

	public Float getDaRate() {

		return daRate;
	}

	public void setDaRate(Float daRate) {

		this.daRate = daRate;
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
	public Integer getStatus() {

		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Integer status) {

		this.status = status;
	}

	public Long getPostId() {

		return PostId;
	}

	public void setPostId(Long postId) {

		PostId = postId;
	}

	public Long getUserId() {

		return UserId;
	}

	public void setUserId(Long userId) {

		UserId = userId;
	}

	public Date getCreatedDate() {

		return CreatedDate;
	}

	public void setCreatedDate(Date createdDate) {

		CreatedDate = createdDate;
	}

	public Long getUpdatedPostId() {

		return UpdatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {

		UpdatedPostId = updatedPostId;
	}

	public Long getUpdatedUserId() {

		return UpdatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {

		UpdatedUserId = updatedUserId;
	}

	public Date getUpdatedDate() {

		return UpdatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {

		UpdatedDate = updatedDate;
	}
}
