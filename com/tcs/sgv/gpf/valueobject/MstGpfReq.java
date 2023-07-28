package com.tcs.sgv.gpf.valueobject;

import java.util.Date;

public class MstGpfReq {
	private Long mstGpfReqId;
	private String transactionId;
	private Long reqDtlId;
	private String reqType;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String orderNo;
	
	public Long getMstGpfReqId() {
	
		return mstGpfReqId;
	}
	
	public void setMstGpfReqId(Long mstGpfReqId) {
	
		this.mstGpfReqId = mstGpfReqId;
	}
	
	public String getTransactionId() {
	
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
	
		this.transactionId = transactionId;
	}
	
	public Long getReqDtlId() {
	
		return reqDtlId;
	}
	
	public void setReqDtlId(Long reqDtlId) {
	
		this.reqDtlId = reqDtlId;
	}
	
	public String getReqType() {
	
		return reqType;
	}
	
	public void setReqType(String reqType) {
	
		this.reqType = reqType;
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
	
	public String getOrderNo() {
	
		return orderNo;
	}
	
	public void setOrderNo(String orderNo) {
	
		this.orderNo = orderNo;
	}

	
}
