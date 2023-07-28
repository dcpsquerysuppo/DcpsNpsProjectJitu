package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class SavedCasesVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4138424810076909618L;

	private String ppoNo;
	private Long pensionerId;
	private Long pensionerDtlsId;
	private String pnsrName;
	private String regNo;
	private Date ppoInwardDate;
	private Long branchCode;
	private String bankName;
	private String branchName;
	private String auditorName;
	private Long auditorPostId;
	private String caseType;
	private String inwardMode;
	private Date retirementDate;
	private Date commencementDate;
	private BigDecimal pnsnAmt;
	private BigDecimal gratuityAmt;
	private BigDecimal cvpAmt;
	private String caseStatus;
	private Date callDate;
	private String callTime;
	private String identStatus;
	private String accountNo;
	private String oldTreasuryName;
	private String remarks;
	private Long bankCode;
	private Long pensionRequestId;
	private Long slotNo;
	private String auditorFname;
	private String auditorMname;
	private String auditorLname;
	private Date ppoRegDate;
	private String familyMemName;
	private Date deathDate;

	public SavedCasesVO() {

	}

	public String getPpoNo() {

		return ppoNo;
	}

	public void setPpoNo(String ppoNo) {

		this.ppoNo = ppoNo;
	}

	public Long getPensionerId() {

		return pensionerId;
	}

	public void setPensionerId(Long pensionerId) {

		this.pensionerId = pensionerId;
	}

	public Long getPensionerDtlsId() {

		return pensionerDtlsId;
	}

	public void setPensionerDtlsId(Long pensionerDtlsId) {

		this.pensionerDtlsId = pensionerDtlsId;
	}

	public String getPnsrName() {

		return pnsrName;
	}

	public void setPnsrName(String pnsrName) {

		this.pnsrName = pnsrName;
	}

	public String getRegNo() {

		return regNo;
	}

	public void setRegNo(String regNo) {

		this.regNo = regNo;
	}

	public Date getPpoInwardDate() {

		return ppoInwardDate;
	}

	public void setPpoInwardDate(Date ppoInwardDate) {

		this.ppoInwardDate = ppoInwardDate;
	}

	public Long getBranchCode() {

		return branchCode;
	}

	public void setBranchCode(Long branchCode) {

		this.branchCode = branchCode;
	}

	public String getBankName() {

		return bankName;
	}

	public void setBankName(String bankName) {

		this.bankName = bankName;
	}

	public String getBranchName() {

		return branchName;
	}

	public void setBranchName(String branchName) {

		this.branchName = branchName;
	}

	public String getAuditorName() {

		return auditorName;
	}

	public void setAuditorName(String auditorName) {

		this.auditorName = auditorName;
	}

	public Long getAuditorPostId() {

		return auditorPostId;
	}

	public void setAuditorPostId(Long auditorPostId) {

		this.auditorPostId = auditorPostId;
	}

	public String getCaseType() {

		return caseType;
	}

	public void setCaseType(String caseType) {

		this.caseType = caseType;
	}

	public String getInwardMode() {

		return inwardMode;
	}

	public void setInwardMode(String inwardMode) {

		this.inwardMode = inwardMode;
	}

	public Date getRetirementDate() {

		return retirementDate;
	}

	public void setRetirementDate(Date retirementDate) {

		this.retirementDate = retirementDate;
	}

	public Date getCommencementDate() {

		return commencementDate;
	}

	public void setCommencementDate(Date commencementDate) {

		this.commencementDate = commencementDate;
	}

	public BigDecimal getPnsnAmt() {

		return pnsnAmt;
	}

	public void setPnsnAmt(BigDecimal pnsnAmt) {

		this.pnsnAmt = pnsnAmt;
	}

	public BigDecimal getGratuityAmt() {

		return gratuityAmt;
	}

	public void setGratuityAmt(BigDecimal gratuityAmt) {

		this.gratuityAmt = gratuityAmt;
	}

	public BigDecimal getCvpAmt() {

		return cvpAmt;
	}

	public void setCvpAmt(BigDecimal cvpAmt) {

		this.cvpAmt = cvpAmt;
	}

	public String getCaseStatus() {

		return caseStatus;
	}

	public void setCaseStatus(String caseStatus) {

		this.caseStatus = caseStatus;
	}

	public Date getCallDate() {

		return callDate;
	}

	public void setCallDate(Date callDate) {

		this.callDate = callDate;
	}

	public String getCallTime() {

		return callTime;
	}

	public void setCallTime(String callTime) {

		this.callTime = callTime;
	}

	public String getIdentStatus() {

		return identStatus;
	}

	public void setIdentStatus(String identStatus) {

		this.identStatus = identStatus;
	}

	public String getAccountNo() {

		return accountNo;
	}

	public void setAccountNo(String accountNo) {

		this.accountNo = accountNo;
	}

	public String getOldTreasuryName() {

		return oldTreasuryName;
	}

	public void setOldTreasuryName(String oldTreasuryName) {

		this.oldTreasuryName = oldTreasuryName;
	}

	public String getRemarks() {

		return remarks;
	}

	public void setRemarks(String remarks) {

		this.remarks = remarks;
	}

	public Long getBankCode() {

		return bankCode;
	}

	public void setBankCode(Long bankCode) {

		this.bankCode = bankCode;
	}

	public Long getSlotNo() {

		return slotNo;
	}

	public void setSlotNo(Long slotNo) {

		this.slotNo = slotNo;
	}

	public Long getPensionRequestId() {

		return pensionRequestId;
	}

	public void setPensionRequestId(Long pensionRequestId) {

		this.pensionRequestId = pensionRequestId;
	}

	public String getAuditorFname() {

		return auditorFname;
	}

	public void setAuditorFname(String auditorFname) {

		this.auditorFname = auditorFname;
	}

	public String getAuditorMname() {

		return auditorMname;
	}

	public void setAuditorMname(String auditorMname) {

		this.auditorMname = auditorMname;
	}

	public String getAuditorLname() {

		return auditorLname;
	}

	public void setAuditorLname(String auditorLname) {

		this.auditorLname = auditorLname;
	}

	public Date getPpoRegDate() {

		return ppoRegDate;
	}

	public void setPpoRegDate(Date ppoRegDate) {

		this.ppoRegDate = ppoRegDate;
	}

	public String getFamilyMemName() {

		return familyMemName;
	}

	public void setFamilyMemName(String familyMemName) {

		this.familyMemName = familyMemName;
	}

	public Date getDeathDate() {

		return deathDate;
	}

	public void setDeathDate(Date deathDate) {

		this.deathDate = deathDate;
	}

	public static long getSerialVersionUID() {

		return serialVersionUID;
	}

}