package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class MstDcpsContriVoucherDtls implements java.io.Serializable {

	private Long dcpsContriVoucherDtlsId;
	private Long treasuryCode;
	private Long yearId;
	private Long monthId;
	private String ddoCode;
	private Long billGroupId;
	private Long voucherNo;
	private Date voucherDate;
	private Long voucherStatus;
	private Double voucherAmount;
	private String reasonForReversion;
	private Long reversionFlag;
	private String remarksForRejection;
	private Long manuallyMatched;
	private Long PostId;
	private Long UserId;
	private Date CreatedDate;
	private Long UpdatedPostId;
	private Long UpdatedUserId;
	private Date UpdatedDate;
	private Long postEmplrContriStatus;
	private Long emplrVoucherNo;
	private Date emplrVoucherDate;
	private String emplrBillNo;
	private Long emplrYearId;
	private Double emplrVoucherAmount;

	private Character status;
	private Long nillBillVoucherNo;
	private Date nillBillDate;

	private Character schedulerUsed;

	private Character isChallan;

	private String schemeCode;

	private Character isMissingCredit;

	private Date missingCreditApprovalDate;

	private String manualremark;

	private Long manualChangeCount;


	public String getManualremark() {
		return manualremark;
	}

	public void setManualremark(String manualremark) {
		this.manualremark = manualremark;
	}

	public Long getManualChangeCount() {
		return manualChangeCount;
	}

	public void setManualChangeCount(Long manualChangeCount) {
		this.manualChangeCount = manualChangeCount;
	}



	public Long getDcpsContriVoucherDtlsId() {
		return dcpsContriVoucherDtlsId;
	}

	public void setDcpsContriVoucherDtlsId(Long dcpsContriVoucherDtlsId) {
		this.dcpsContriVoucherDtlsId = dcpsContriVoucherDtlsId;
	}

	public Long getTreasuryCode() {
		return treasuryCode;
	}

	public void setTreasuryCode(Long treasuryCode) {
		this.treasuryCode = treasuryCode;
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

	public String getReasonForReversion() {
		return reasonForReversion;
	}

	public void setReasonForReversion(String reasonForReversion) {
		this.reasonForReversion = reasonForReversion;
	}

	public Long getReversionFlag() {
		return reversionFlag;
	}

	public void setReversionFlag(Long reversionFlag) {
		this.reversionFlag = reversionFlag;
	}

	public String getRemarksForRejection() {
		return remarksForRejection;
	}

	public void setRemarksForRejection(String remarksForRejection) {
		this.remarksForRejection = remarksForRejection;
	}

	public Long getVoucherStatus() {
		return voucherStatus;
	}

	public void setVoucherStatus(Long voucherStatus) {
		this.voucherStatus = voucherStatus;
	}

	public Long getBillGroupId() {
		return billGroupId;
	}

	public void setBillGroupId(Long billGroupId) {
		this.billGroupId = billGroupId;
	}

	public Long getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(Long voucherNo) {
		this.voucherNo = voucherNo;
	}

	public Date getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}

	public Long getManuallyMatched() {
		return manuallyMatched;
	}

	public void setManuallyMatched(Long manuallyMatched) {
		this.manuallyMatched = manuallyMatched;
	}

	public Double getVoucherAmount() {
		return voucherAmount;
	}

	public void setVoucherAmount(Double voucherAmount) {
		this.voucherAmount = voucherAmount;
	}

	public String getDdoCode() {
		return ddoCode;
	}

	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
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

	public Long getPostEmplrContriStatus() {
		return postEmplrContriStatus;
	}

	public void setPostEmplrContriStatus(Long postEmplrContriStatus) {
		this.postEmplrContriStatus = postEmplrContriStatus;
	}

	public Long getEmplrVoucherNo() {
		return emplrVoucherNo;
	}

	public void setEmplrVoucherNo(Long emplrVoucherNo) {
		this.emplrVoucherNo = emplrVoucherNo;
	}

	public Date getEmplrVoucherDate() {
		return emplrVoucherDate;
	}

	public void setEmplrVoucherDate(Date emplrVoucherDate) {
		this.emplrVoucherDate = emplrVoucherDate;
	}

	public String getEmplrBillNo() {
		return emplrBillNo;
	}

	public void setEmplrBillNo(String emplrBillNo) {
		this.emplrBillNo = emplrBillNo;
	}
	public Long getEmplrYearId() {
		return emplrYearId;
	}

	public void setEmplrYearId(Long emplrYearId) {
		this.emplrYearId = emplrYearId;
	}

	public Double getEmplrVoucherAmount() {
		return emplrVoucherAmount;
	}

	public void setEmplrVoucherAmount(Double emplrVoucherAmount) {
		this.emplrVoucherAmount = emplrVoucherAmount;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public Long getNillBillVoucherNo() {
		return nillBillVoucherNo;
	}

	public void setNillBillVoucherNo(Long nillBillVoucherNo) {
		this.nillBillVoucherNo = nillBillVoucherNo;
	}

	public Date getNillBillDate() {
		return nillBillDate;
	}

	public void setNillBillDate(Date nillBillDate) {
		this.nillBillDate = nillBillDate;
	}

	public Character getSchedulerUsed() {
		return schedulerUsed;
	}

	public void setSchedulerUsed(Character schedulerUsed) {
		this.schedulerUsed = schedulerUsed;
	}

	public Character getIsChallan() {
		return isChallan;
	}

	public void setIsChallan(Character isChallan) {
		this.isChallan = isChallan;
	}

	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	public Character getIsMissingCredit() {
		return isMissingCredit;
	}

	public void setIsMissingCredit(Character isMissingCredit) {
		this.isMissingCredit = isMissingCredit;
	}

	public Date getMissingCreditApprovalDate() {
		return missingCreditApprovalDate;
	}

	public void setMissingCreditApprovalDate(Date missingCreditApprovalDate) {
		this.missingCreditApprovalDate = missingCreditApprovalDate;
	}
}
