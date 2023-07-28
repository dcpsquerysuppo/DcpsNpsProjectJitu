package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

import com.tcs.sgv.common.valueobject.CmnLookupMst;

public class MstBrokenPeriodPay {
	
	private Long brokenPeriodId;
	private Long eisEmpId;
	private Long monthId;
	private Long yearId;
	private Date fromDate;
	private Date toDate;
	private Long noOfDays;
	private Long basicPay;
	private Long netPay;
	private String reason;
	private String remarks;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String typeOfBill;
	
	public String getTypeOfBill() {
		return typeOfBill;
	}
	public void setTypeOfBill(String typeOfBill) {
		this.typeOfBill = typeOfBill;
	}

	
	public CmnLookupMst getCmnLookupMst() {
		return cmnLookupMst;
	}
	public void setCmnLookupMst(CmnLookupMst cmnLookupMst) {
		this.cmnLookupMst = cmnLookupMst;
	}
	private CmnLookupMst cmnLookupMst;
	public Long getBrokenPeriodId() {
		return brokenPeriodId;
	}
	public void setBrokenPeriodId(Long brokenPeriodId) {
		this.brokenPeriodId = brokenPeriodId;
	}
	public Long getEisEmpId() {
		return eisEmpId;
	}
	public void setEisEmpId(Long eisEmpId) {
		this.eisEmpId = eisEmpId;
	}
	public Long getMonthId() {
		return monthId;
	}
	public void setMonthId(Long monthId) {
		this.monthId = monthId;
	}
	public Long getYearId() {
		return yearId;
	}
	public void setYearId(Long yearId) {
		this.yearId = yearId;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public Long getNoOfDays() {
		return noOfDays;
	}
	public void setNoOfDays(Long noOfDays) {
		this.noOfDays = noOfDays;
	}
	public Long getBasicPay() {
		return basicPay;
	}
	public void setBasicPay(Long basicPay) {
		this.basicPay = basicPay;
	}
	public Long getNetPay() {
		return netPay;
	}
	public void setNetPay(Long netPay) {
		this.netPay = netPay;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Long getLangId() {
		return langId;
	}
	public void setLangId(Long langId) {
		this.langId = langId;
	}
	public Long getLocId() {
		return locId;
	}
	public void setLocId(Long locId) {
		this.locId = locId;
	}
	public Long getDbId() {
		return dbId;
	}
	public void setDbId(Long dbId) {
		this.dbId = dbId;
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
