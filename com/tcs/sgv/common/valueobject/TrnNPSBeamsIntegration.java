package com.tcs.sgv.common.valueobject;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;

public class TrnNPSBeamsIntegration implements java.io.Serializable {
	private Long npsBeamsIntegrationId;
	private String billType;
	private String beamsBillType;
	private Long paybillId;
	private String billNo;
	private BigDecimal billGrossAmt = BigDecimal.ZERO;
	private BigDecimal totalRecoveryAmt = BigDecimal.ZERO;
	private BigDecimal billNetAmt = BigDecimal.ZERO;
	private String schemeCode;
	public Long getNpsBeamsIntegrationId() {
		return npsBeamsIntegrationId;
	}
	public void setNpsBeamsIntegrationId(Long npsBeamsIntegrationId) {
		this.npsBeamsIntegrationId = npsBeamsIntegrationId;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getBeamsBillType() {
		return beamsBillType;
	}
	public void setBeamsBillType(String beamsBillType) {
		this.beamsBillType = beamsBillType;
	}
	public Long getPaybillId() {
		return paybillId;
	}
	public void setPaybillId(Long paybillId) {
		this.paybillId = paybillId;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public BigDecimal getBillGrossAmt() {
		return billGrossAmt;
	}
	public void setBillGrossAmt(BigDecimal billGrossAmt) {
		this.billGrossAmt = billGrossAmt;
	}
	public BigDecimal getTotalRecoveryAmt() {
		return totalRecoveryAmt;
	}
	public void setTotalRecoveryAmt(BigDecimal totalRecoveryAmt) {
		this.totalRecoveryAmt = totalRecoveryAmt;
	}
	public BigDecimal getBillNetAmt() {
		return billNetAmt;
	}
	public void setBillNetAmt(BigDecimal billNetAmt) {
		this.billNetAmt = billNetAmt;
	}
	public String getSchemeCode() {
		return schemeCode;
	}
	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}
	public String getDtlheadCode() {
		return dtlheadCode;
	}
	public void setDtlheadCode(String dtlheadCode) {
		this.dtlheadCode = dtlheadCode;
	}
	public String getDdoCode() {
		return ddoCode;
	}
	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
	}
	public Date getBillCreationDate() {
		return billCreationDate;
	}
	public void setBillCreationDate(Date billCreationDate) {
		this.billCreationDate = billCreationDate;
	}
	public String getFinYear1() {
		return finYear1;
	}
	public void setFinYear1(String finYear1) {
		this.finYear1 = finYear1;
	}
	public String getFinYear2() {
		return finYear2;
	}
	public void setFinYear2(String finYear2) {
		this.finYear2 = finYear2;
	}
	public Integer getYearMonth() {
		return yearMonth;
	}
	public void setYearMonth(Integer yearMonth) {
		this.yearMonth = yearMonth;
	}
	public Integer getNoOfBeneficiary() {
		return noOfBeneficiary;
	}
	public void setNoOfBeneficiary(Integer noOfBeneficiary) {
		this.noOfBeneficiary = noOfBeneficiary;
	}
	public String getAuthNo() {
		return authNo;
	}
	public void setAuthNo(String authNo) {
		this.authNo = authNo;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public Blob getAuthSlip() {
		return authSlip;
	}
	public void setAuthSlip(Blob authSlip) {
		this.authSlip = authSlip;
	}
	public String getBillValidSatus() {
		return billValidSatus;
	}
	public void setBillValidSatus(String billValidSatus) {
		this.billValidSatus = billValidSatus;
	}
	public String getBeamsBillStatus() {
		return beamsBillStatus;
	}
	public void setBeamsBillStatus(String beamsBillStatus) {
		this.beamsBillStatus = beamsBillStatus;
	}
	public Integer getVoucherNo() {
		return voucherNo;
	}
	public void setVoucherNo(Integer voucherNo) {
		this.voucherNo = voucherNo;
	}
	public Date getVoucherDate() {
		return voucherDate;
	}
	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public Integer getDbId() {
		return dbId;
	}
	public void setDbId(Integer dbId) {
		this.dbId = dbId;
	}
	public Long getCreatedUserId() {
		return createdUserId;
	}
	public void setCreatedUserId(Long createdUserId) {
		this.createdUserId = createdUserId;
	}
	public Long getCreatedPostId() {
		return createdPostId;
	}
	public void setCreatedPostId(Long createdPostId) {
		this.createdPostId = createdPostId;
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
	private String dtlheadCode;
	private String ddoCode;
	private Date billCreationDate;
	private String finYear1;
	private String finYear2;
	private Integer yearMonth;
	private Integer noOfBeneficiary;
	private String authNo;
	private String statusCode;
	private Blob authSlip;
	private String billValidSatus;
	private String beamsBillStatus;
	private Integer voucherNo;
	private Date voucherDate;
	private String locationCode;
	private Integer dbId;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

}
