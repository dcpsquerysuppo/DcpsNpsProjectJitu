package com.tcs.sgv.gpf.valueobject;

import java.util.Date;

public class MstGpfChallanDtls {
	private Long mstGpfChallanDtlsId;
	private String gpfAccNo;
	private Long challanType;
	private String challanNo;
	private Date challanDate;
	private Double amount;
	private String advanceTrnId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Integer instFrom;
	private Integer instTo;
	private String statusFlag;
	private String dataEntry;

	public Long getMstGpfChallanDtlsId() {
		return mstGpfChallanDtlsId;
	}

	public void setMstGpfChallanDtlsId(Long mstGpfChallanDtlsId) {
		this.mstGpfChallanDtlsId = mstGpfChallanDtlsId;
	}

	public String getGpfAccNo() {
		return gpfAccNo;
	}

	public void setGpfAccNo(String gpfAccNo) {
		this.gpfAccNo = gpfAccNo;
	}

	public Long getChallanType() {
		return challanType;
	}

	public void setChallanType(Long challanType) {
		this.challanType = challanType;
	}

	public String getChallanNo() {
		return challanNo;
	}

	public void setChallanNo(String challanNo) {
		this.challanNo = challanNo;
	}

	public Date getChallanDate() {
		return challanDate;
	}

	public void setChallanDate(Date challanDate) {
		this.challanDate = challanDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getAdvanceTrnId() {
		return advanceTrnId;
	}

	public void setAdvanceTrnId(String advanceTrnId) {
		this.advanceTrnId = advanceTrnId;
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

	public Integer getInstFrom() {
		return instFrom;
	}

	public void setInstFrom(Integer instFrom) {
		this.instFrom = instFrom;
	}

	public Integer getInstTo() {
		return instTo;
	}

	public void setInstTo(Integer instTo) {
		this.instTo = instTo;
	}

	public String getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(String statusFlag) {
		this.statusFlag = statusFlag;
	}

	public String getDataEntry() {
		return dataEntry;
	}

	public void setDataEntry(String dataEntry) {
		this.dataEntry = dataEntry;
	}
	
}
