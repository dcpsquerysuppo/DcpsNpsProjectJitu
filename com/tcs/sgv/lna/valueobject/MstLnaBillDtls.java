package com.tcs.sgv.lna.valueobject;

import java.util.Date;

public class MstLnaBillDtls {

	private Long billDtlsId;
	private String locationCode;
	private Long billAmount;
	private Date billGeneratedDate;
	private Long advacnceType;
	private Long statusFlag;
	private String voucherNo;
	private Date voucherDate;
	private String orderNo;
	private Date orderDate;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	
	public Long getBillDtlsId() {
		return billDtlsId;
	}
	public void setBillDtlsId(Long billDtlsId) {
		this.billDtlsId = billDtlsId;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public Long getBillAmount() {
		return billAmount;
	}
	public void setBillAmount(Long billAmount) {
		this.billAmount = billAmount;
	}
	public Date getBillGeneratedDate() {
		return billGeneratedDate;
	}
	public void setBillGeneratedDate(Date billGeneratedDate) {
		this.billGeneratedDate = billGeneratedDate;
	}
	public void setAdvacnceType(Long advacnceType) {
		this.advacnceType = advacnceType;
	}
	public Long getAdvacnceType() {
		return advacnceType;
	}
	public Long getStatusFlag() {
		return statusFlag;
	}
	public void setStatusFlag(Long statusFlag) {
		this.statusFlag = statusFlag;
	}
	public String getVoucherNo() {
		return voucherNo;
	}
	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}
	public Date getVoucherDate() {
		return voucherDate;
	}
	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public Date getOrderDate() {
		return orderDate;
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
