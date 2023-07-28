package com.tcs.sgv.gpf.valueobject;

import java.util.Date;

public class MstEmpGpfAcc {
	private Long mstEmpGpfAccId;
	private String gpfAccNo;
	private String sevaarthId;
	private Long mstGpfEmpId;
	private Double currentBalance;
	private Double monthlySubscription;
	private String ddoCode;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	public String getDdoCode() {
		return ddoCode;
	}

	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
	}

	public String getGpfAccNo() {
		return gpfAccNo;
	}

	public void setGpfAccNo(String gpfAccNo) {
		this.gpfAccNo = gpfAccNo;
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

	public Long getMstGpfEmpId() {
		return mstGpfEmpId;
	}

	public void setMstGpfEmpId(Long mstGpfEmpId) {
		this.mstGpfEmpId = mstGpfEmpId;
	}

	public void setSevaarthId(String sevaarthId) {
		this.sevaarthId = sevaarthId;
	}

	public void setCurrentBalance(Double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public void setMonthlySubscription(Double monthlySubscription) {
		this.monthlySubscription = monthlySubscription;
	}

	public String getSevaarthId() {
		return sevaarthId;
	}

	public Double getCurrentBalance() {
		return currentBalance;
	}

	public Double getMonthlySubscription() {
		return monthlySubscription;
	}

	public Long getMstEmpGpfAccId() {
		return mstEmpGpfAccId;
	}

	public void setMstEmpGpfAccId(Long mstEmpGpfAccId) {
		this.mstEmpGpfAccId = mstEmpGpfAccId;
	}
}
