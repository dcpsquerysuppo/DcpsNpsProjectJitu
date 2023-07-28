package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class SanctionedBudget {

	private Long dcpsSanctionedBudgetIdPk;

	private Long dcpsSancBudgetOrgId;
	private String dcpsSancBudgetSchemeCode;
	private Long dcpsSancBudgetFinYear;
	private Date dcpsSancBudgetDate;
	private Long dcpsSancBudgetAmount;
	private Long expenditureTillDate;
	private Long totalBudget;
	private String dcpsSancBudgetType;
	private Long PostId;
	private Long UserId;
	private Date CreatedDate;
	private Long UpdatedPostId;
	private Long UpdatedUserId;
	private Date UpdatedDate;

	public Long getDcpsSanctionedBudgetIdPk() {
		return dcpsSanctionedBudgetIdPk;
	}

	public void setDcpsSanctionedBudgetIdPk(Long dcpsSanctionedBudgetIdPk) {
		this.dcpsSanctionedBudgetIdPk = dcpsSanctionedBudgetIdPk;
	}

	public Long getDcpsSancBudgetOrgId() {
		return dcpsSancBudgetOrgId;
	}

	public void setDcpsSancBudgetOrgId(Long dcpsSancBudgetOrgId) {
		this.dcpsSancBudgetOrgId = dcpsSancBudgetOrgId;

	}

	public String getDcpsSancBudgetSchemeCode() {
		return dcpsSancBudgetSchemeCode;
	}

	public void setDcpsSancBudgetSchemeCode(String dcpsSancBudgetSchemeCode) {
		this.dcpsSancBudgetSchemeCode = dcpsSancBudgetSchemeCode;
	}

	public Long getDcpsSancBudgetFinYear() {
		return dcpsSancBudgetFinYear;
	}

	public void setDcpsSancBudgetFinYear(Long dcpsSancBudgetFinYear) {
		this.dcpsSancBudgetFinYear = dcpsSancBudgetFinYear;
	}

	public Date getDcpsSancBudgetDate() {
		return dcpsSancBudgetDate;
	}

	public void setDcpsSancBudgetDate(Date dcpsSancBudgetDate) {
		this.dcpsSancBudgetDate = dcpsSancBudgetDate;
	}

	public Long getDcpsSancBudgetAmount() {
		return dcpsSancBudgetAmount;
	}

	public void setDcpsSancBudgetAmount(Long dcpsSancBudgetAmount) {
		this.dcpsSancBudgetAmount = dcpsSancBudgetAmount;
	}

	public Long getExpenditureTillDate() {
		return expenditureTillDate;
	}

	public void setExpenditureTillDate(Long expenditureTillDate) {
		this.expenditureTillDate = expenditureTillDate;
	}

	/**
	 * @return the totalBudget
	 */
	public Long getTotalBudget() {
		return totalBudget;
	}

	/**
	 * @param totalBudget
	 *            the totalBudget to set
	 */
	public void setTotalBudget(Long totalBudget) {
		this.totalBudget = totalBudget;
	}

	public String getDcpsSancBudgetType() {
		return dcpsSancBudgetType;
	}

	public void setDcpsSancBudgetType(String dcpsSancBudgetType) {
		this.dcpsSancBudgetType = dcpsSancBudgetType;
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
