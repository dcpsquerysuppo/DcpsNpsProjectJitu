package com.tcs.sgv.pensionpay.valueobject;

import java.util.Date;


public class StgAcNoErrorDtls{
	
	private Long stgAcNoErrorDtlsId;
	private Long delvId;
	private String ppoNo;	
	private Long errorCode;
	private String locationCode;	
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	public Long getStgAcNoErrorDtlsId() {
		return stgAcNoErrorDtlsId;
	}
	public void setStgAcNoErrorDtlsId(Long stgAcNoErrorDtlsId) {
		this.stgAcNoErrorDtlsId = stgAcNoErrorDtlsId;
	}
	public Long getDelvId() {
		return delvId;
	}
	public void setDelvId(Long delvId) {
		this.delvId = delvId;
	}
	public String getPpoNo() {
		return ppoNo;
	}
	public void setPpoNo(String ppoNo) {
		this.ppoNo = ppoNo;
	}	
	public Long getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(Long errorCode) {
		this.errorCode = errorCode;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
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
