package com.tcs.sgv.gpf.valueobject;

import java.util.Date;

public class MstGpfMonthly {
	private Long mstGpfMonthlyId;
	private Long billgroupId;
	private String gpfAccNo;
	private Long finYearId;
	private Long monthId;
	private Double openingBalance;
	private Double regularSubscription;
	private Double advanceRecovery;
	private String advanceTrnId;
	private Integer instNo;
	private Integer totalInst;
	private Double arrearAmount;
	private Double advanceSanctioned;
	private Double excessPayment;
	private Double prePayOfAdvance;
	private Double deputationChallan;
	private Double closingBalance;
	private String status;
	private String voucherNo;
	private Date voucherDate;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Integer paybillGenerated;
	private String dataEntry;

	public Long getMstGpfMonthlyId() {
		return mstGpfMonthlyId;
	}

	public void setMstGpfMonthlyId(Long mstGpfMonthlyId) {
		this.mstGpfMonthlyId = mstGpfMonthlyId;
	}

	public String getGpfAccNo() {
		return gpfAccNo;
	}

	public void setGpfAccNo(String gpfAccNo) {
		this.gpfAccNo = gpfAccNo;
	}

	public Long getFinYearId() {
		return finYearId;
	}

	public void setFinYearId(Long finYearId) {
		this.finYearId = finYearId;
	}

	public Long getMonthId() {
		return monthId;
	}

	public void setMonthId(Long monthId) {
		this.monthId = monthId;
	}

	public Double getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(Double openingBalance) {
		this.openingBalance = openingBalance;
	}

	public Double getRegularSubscription() {
		return regularSubscription;
	}

	public void setRegularSubscription(Double regularSubscription) {
		this.regularSubscription = regularSubscription;
	}

	public Double getAdvanceRecovery() {
		return advanceRecovery;
	}

	public void setAdvanceRecovery(Double advanceRecovery) {
		this.advanceRecovery = advanceRecovery;
	}

	public String getAdvanceTrnId() {
		return advanceTrnId;
	}

	public void setAdvanceTrnId(String advanceTrnId) {
		this.advanceTrnId = advanceTrnId;
	}

	public Integer getInstNo() {
		return instNo;
	}

	public void setInstNo(Integer instNo) {
		this.instNo = instNo;
	}

	public Integer getTotalInst() {
		return totalInst;
	}

	public void setTotalInst(Integer totalInst) {
		this.totalInst = totalInst;
	}

	public Double getArrearAmount() {
		return arrearAmount;
	}

	public void setArrearAmount(Double arrearAmount) {
		this.arrearAmount = arrearAmount;
	}

	public Double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(Double closingBalance) {
		this.closingBalance = closingBalance;
	}

	public Double getAdvanceSanctioned() {
		return advanceSanctioned;
	}

	public void setAdvanceSanctioned(Double advanceSanctioned) {
		this.advanceSanctioned = advanceSanctioned;
	}

	public Double getExcessPayment() {
		return excessPayment;
	}

	public void setExcessPayment(Double excessPayment) {
		this.excessPayment = excessPayment;
	}

	public Double getPrePayOfAdvance() {
		return prePayOfAdvance;
	}

	public void setPrePayOfAdvance(Double prePayOfAdvance) {
		this.prePayOfAdvance = prePayOfAdvance;
	}

	public Double getDeputationChallan() {
		return deputationChallan;
	}

	public void setDeputationChallan(Double deputationChallan) {
		this.deputationChallan = deputationChallan;
	}

	public Long getCreatedPostId() {
		return createdPostId;
	}

	public void setCreatedPostId(Long lngPostId) {
		this.createdPostId = lngPostId;
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

	public Long getBillgroupId() {
		return billgroupId;
	}

	public void setBillgroupId(Long billgroupId) {
		this.billgroupId = billgroupId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Integer getPaybillGenerated() {
		return paybillGenerated;
	}

	public void setPaybillGenerated(Integer paybillGenerated) {
		this.paybillGenerated = paybillGenerated;
	}

	public String getDataEntry() {
		return dataEntry;
	}

	public void setDataEntry(String dataEntry) {
		this.dataEntry = dataEntry;
	}
	
}
