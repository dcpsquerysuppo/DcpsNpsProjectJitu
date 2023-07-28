package com.tcs.sgv.gpf.valueobject;

import java.util.Date;

public class TrnGpfFinalWithdrawal {
	private Long trnGpfFinalWithdrawalId;
	private String transactionId;
	private String gpfAccNo;
	private Date applicationDate;
	private Long finalAmount;
	private Double amountSanctioned;
	private Double balanceOutstanding;
	private Long attachmentId;
	private String reqStatus;
	private String userRemarks;
	private String approverRemarks;
	private String hoRemarks;
	private Long orderNo;
	private Date orderDate;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Date deoActionDate;
	private Date verifierActionDate;
	private Date hoActionDate;
	private Date hoReceiveDate;
	private Long reason;

	public void setTrnGpfFinalWithdrawalId(Long trnGpfFinalWithdrawalId) {
		this.trnGpfFinalWithdrawalId = trnGpfFinalWithdrawalId;
	}

	public Long getTrnGpfFinalWithdrawalId() {
		return trnGpfFinalWithdrawalId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getGpfAccNo() {
		return gpfAccNo;
	}

	public void setGpfAccNo(String gpfAccNo) {
		this.gpfAccNo = gpfAccNo;
	}

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public Long getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(Long finalAmount) {
		this.finalAmount = finalAmount;
	}

	public Double getAmountSanctioned() {
		return amountSanctioned;
	}

	public void setAmountSanctioned(Double amountSanctioned) {
		this.amountSanctioned = amountSanctioned;
	}

	public Double getBalanceOutstanding() {
		return balanceOutstanding;
	}

	public void setBalanceOutstanding(Double balanceOutstanding) {
		this.balanceOutstanding = balanceOutstanding;
	}

	public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public void setReqStatus(String reqStatus) {
		this.reqStatus = reqStatus;
	}

	public String getReqStatus() {
		return reqStatus;
	}

	public String getUserRemarks() {
		return userRemarks;
	}

	public void setUserRemarks(String userRemarks) {
		this.userRemarks = userRemarks;
	}

	public String getApproverRemarks() {
		return approverRemarks;
	}

	public void setApproverRemarks(String approverRemarks) {
		this.approverRemarks = approverRemarks;
	}

	public String getHoRemarks() {
		return hoRemarks;
	}

	public void setHoRemarks(String hoRemarks) {
		this.hoRemarks = hoRemarks;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}

	public Long getOrderNo() {
		return orderNo;
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

	public Date getDeoActionDate() {
		return deoActionDate;
	}

	public void setDeoActionDate(Date deoActionDate) {
		this.deoActionDate = deoActionDate;
	}

	public Date getVerifierActionDate() {
		return verifierActionDate;
	}

	public void setVerifierActionDate(Date verifierActionDate) {
		this.verifierActionDate = verifierActionDate;
	}

	public Date getHoActionDate() {
		return hoActionDate;
	}

	public void setHoActionDate(Date hoActionDate) {
		this.hoActionDate = hoActionDate;
	}

	public Date getHoReceiveDate() {
		return hoReceiveDate;
	}

	public void setHoReceiveDate(Date hoReceiveDate) {
		this.hoReceiveDate = hoReceiveDate;
	}

	public Long getReason() {
		return reason;
	}

	public void setReason(Long reason) {
		this.reason = reason;
	}

}
