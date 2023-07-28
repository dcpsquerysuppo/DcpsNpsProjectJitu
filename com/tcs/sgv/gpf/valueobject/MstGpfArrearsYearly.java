package com.tcs.sgv.gpf.valueobject;

import java.util.Date;

public class MstGpfArrearsYearly {

	private Long gpfArrearsYearlyId;
	private String gpfAccNo;
	private Long yearId;
	private Long monthId;
	private String ddoCode;
	private Double totalAmount;
	private Double openBalance;
	private Double arrearAmount;
	private Integer installmentNo;
	private Float interestrate;
	private Double closeBalance;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	public Long getGpfArrearsYearlyId() {
		return gpfArrearsYearlyId;
	}

	public void setGpfArrearsYearlyId(Long gpfArrearsYearlyId) {
		this.gpfArrearsYearlyId = gpfArrearsYearlyId;
	}

	public String getGpfAccNo() {
		return gpfAccNo;
	}

	public void setGpfAccNo(String gpfAccNo) {
		this.gpfAccNo = gpfAccNo;
	}

	public Long getYearId() {
		return yearId;
	}

	public void setYearId(Long yearId) {
		this.yearId = yearId;
	}

	public Long getMonthId() {
		return monthId;
	}

	public void setMonthId(Long monthId) {
		this.monthId = monthId;
	}

	public String getDdoCode() {
		return ddoCode;
	}

	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
	}

	public Double getOpenBalance() {
		return openBalance;
	}

	public void setOpenBalance(Double openBalance) {
		this.openBalance = openBalance;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getInstallmentNo() {
		return installmentNo;
	}

	public void setInstallmentNo(Integer installmentNo) {
		this.installmentNo = installmentNo;
	}

	public Double getArrearAmount() {
		return arrearAmount;
	}

	public void setArrearAmount(Double arrearAmount) {
		this.arrearAmount = arrearAmount;
	}

	public Float getInterestrate() {
		return interestrate;
	}

	public void setInterestrate(Float interestrate) {
		this.interestrate = interestrate;
	}

	public Double getCloseBalance() {
		return closeBalance;
	}

	public void setCloseBalance(Double closeBalance) {
		this.closeBalance = closeBalance;
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
