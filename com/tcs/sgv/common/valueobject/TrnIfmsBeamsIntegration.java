/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 28, 2012		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.common.valueobject;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Aug 28, 2012
 */
public class TrnIfmsBeamsIntegration implements java.io.Serializable {

	private Long ifmsBeamsIntegrationId;
	private String billType;
	private String beamsBillType;
	private Long paybillId;
	private Long billNo;
	private BigDecimal billGrossAmt = BigDecimal.ZERO;
	private BigDecimal totalRecoveryAmt = BigDecimal.ZERO;
	private BigDecimal billNetAmt = BigDecimal.ZERO;
	private String schemeCode;
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

	public TrnIfmsBeamsIntegration() {

	}

	/**
	 * @return the ifmsBeamsIntegrationId
	 */
	public Long getIfmsBeamsIntegrationId() {

		return ifmsBeamsIntegrationId;
	}

	/**
	 * @param ifmsBeamsIntegrationId
	 *            the ifmsBeamsIntegrationId to set
	 */
	public void setIfmsBeamsIntegrationId(Long ifmsBeamsIntegrationId) {

		this.ifmsBeamsIntegrationId = ifmsBeamsIntegrationId;
	}

	/**
	 * @return the billType
	 */
	public String getBillType() {

		return billType;
	}

	/**
	 * @param billType
	 *            the billType to set
	 */
	public void setBillType(String billType) {

		this.billType = billType;
	}

	/**
	 * @return the paybillId
	 */
	public Long getPaybillId() {

		return paybillId;
	}

	/**
	 * @param paybillId
	 *            the paybillId to set
	 */
	public void setPaybillId(Long paybillId) {

		this.paybillId = paybillId;
	}

	/**
	 * @return the billNo
	 */
	public Long getBillNo() {

		return billNo;
	}

	/**
	 * @param billNo
	 *            the billNo to set
	 */
	public void setBillNo(Long billNo) {

		this.billNo = billNo;
	}

	/**
	 * @return the billGrossAmt
	 */
	public BigDecimal getBillGrossAmt() {

		return billGrossAmt;
	}

	/**
	 * @param billGrossAmt
	 *            the billGrossAmt to set
	 */
	public void setBillGrossAmt(BigDecimal billGrossAmt) {

		this.billGrossAmt = billGrossAmt;
	}

	/**
	 * @return the totalRecoveryAmt
	 */
	public BigDecimal getTotalRecoveryAmt() {

		return totalRecoveryAmt;
	}

	/**
	 * @param totalRecoveryAmt
	 *            the totalRecoveryAmt to set
	 */
	public void setTotalRecoveryAmt(BigDecimal totalRecoveryAmt) {

		this.totalRecoveryAmt = totalRecoveryAmt;
	}

	/**
	 * @return the billNetAmt
	 */
	public BigDecimal getBillNetAmt() {

		return billNetAmt;
	}

	/**
	 * @param billNetAmt
	 *            the billNetAmt to set
	 */
	public void setBillNetAmt(BigDecimal billNetAmt) {

		this.billNetAmt = billNetAmt;
	}

	/**
	 * @return the schemeCode
	 */
	public String getSchemeCode() {

		return schemeCode;
	}

	/**
	 * @param schemeCode
	 *            the schemeCode to set
	 */
	public void setSchemeCode(String schemeCode) {

		this.schemeCode = schemeCode;
	}

	/**
	 * @return the dtlheadCode
	 */
	public String getDtlheadCode() {

		return dtlheadCode;
	}

	/**
	 * @param dtlheadCode
	 *            the dtlheadCode to set
	 */
	public void setDtlheadCode(String dtlheadCode) {

		this.dtlheadCode = dtlheadCode;
	}

	/**
	 * @return the ddoCode
	 */
	public String getDdoCode() {

		return ddoCode;
	}

	/**
	 * @param ddoCode
	 *            the ddoCode to set
	 */
	public void setDdoCode(String ddoCode) {

		this.ddoCode = ddoCode;
	}

	/**
	 * @return the billCreationDate
	 */
	public Date getBillCreationDate() {

		return billCreationDate;
	}

	/**
	 * @param billCreationDate
	 *            the billCreationDate to set
	 */
	public void setBillCreationDate(Date billCreationDate) {

		this.billCreationDate = billCreationDate;
	}

	/**
	 * @return the finYear1
	 */
	public String getFinYear1() {

		return finYear1;
	}

	/**
	 * @param finYear1
	 *            the finYear1 to set
	 */
	public void setFinYear1(String finYear1) {

		this.finYear1 = finYear1;
	}

	/**
	 * @return the finYear2
	 */
	public String getFinYear2() {

		return finYear2;
	}

	/**
	 * @param finYear2
	 *            the finYear2 to set
	 */
	public void setFinYear2(String finYear2) {

		this.finYear2 = finYear2;
	}

	/**
	 * @return the yearMonth
	 */
	public Integer getYearMonth() {

		return yearMonth;
	}

	/**
	 * @param yearMonth
	 *            the yearMonth to set
	 */
	public void setYearMonth(Integer yearMonth) {

		this.yearMonth = yearMonth;
	}

	/**
	 * @return the noOfBeneficiary
	 */
	public Integer getNoOfBeneficiary() {

		return noOfBeneficiary;
	}

	/**
	 * @param noOfBeneficiary
	 *            the noOfBeneficiary to set
	 */
	public void setNoOfBeneficiary(Integer noOfBeneficiary) {

		this.noOfBeneficiary = noOfBeneficiary;
	}

	/**
	 * @return the authNo
	 */
	public String getAuthNo() {

		return authNo;
	}

	/**
	 * @param authNo
	 *            the authNo to set
	 */
	public void setAuthNo(String authNo) {

		this.authNo = authNo;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {

		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(String statusCode) {

		this.statusCode = statusCode;
	}

	/**
	 * @return the authSlip
	 */
	public Blob getAuthSlip() {

		return authSlip;
	}

	/**
	 * @param authSlip
	 *            the authSlip to set
	 */
	public void setAuthSlip(Blob authSlip) {

		this.authSlip = authSlip;
	}

	/**
	 * @return the billValidSatus
	 */
	public String getBillValidSatus() {

		return billValidSatus;
	}

	/**
	 * @param billValidSatus
	 *            the billValidSatus to set
	 */
	public void setBillValidSatus(String billValidSatus) {

		this.billValidSatus = billValidSatus;
	}

	/**
	 * @return the beamsBillStatus
	 */
	public String getBeamsBillStatus() {

		return beamsBillStatus;
	}

	/**
	 * @param beamsBillStatus
	 *            the beamsBillStatus to set
	 */
	public void setBeamsBillStatus(String beamsBillStatus) {

		this.beamsBillStatus = beamsBillStatus;
	}

	/**
	 * @return the voucherNo
	 */
	public Integer getVoucherNo() {

		return voucherNo;
	}

	/**
	 * @param voucherNo
	 *            the voucherNo to set
	 */
	public void setVoucherNo(Integer voucherNo) {

		this.voucherNo = voucherNo;
	}

	/**
	 * @return the voucherDate
	 */
	public Date getVoucherDate() {

		return voucherDate;
	}

	/**
	 * @param voucherDate
	 *            the voucherDate to set
	 */
	public void setVoucherDate(Date voucherDate) {

		this.voucherDate = voucherDate;
	}

	/**
	 * @return the locationCode
	 */
	public String getLocationCode() {

		return locationCode;
	}

	/**
	 * @param locationCode
	 *            the locationCode to set
	 */
	public void setLocationCode(String locationCode) {

		this.locationCode = locationCode;
	}

	/**
	 * @return the dbId
	 */
	public Integer getDbId() {

		return dbId;
	}

	/**
	 * @param dbId
	 *            the dbId to set
	 */
	public void setDbId(Integer dbId) {

		this.dbId = dbId;
	}

	/**
	 * @return the createdUserId
	 */
	public Long getCreatedUserId() {

		return createdUserId;
	}

	/**
	 * @param createdUserId
	 *            the createdUserId to set
	 */
	public void setCreatedUserId(Long createdUserId) {

		this.createdUserId = createdUserId;
	}

	/**
	 * @return the createdPostId
	 */
	public Long getCreatedPostId() {

		return createdPostId;
	}

	/**
	 * @param createdPostId
	 *            the createdPostId to set
	 */
	public void setCreatedPostId(Long createdPostId) {

		this.createdPostId = createdPostId;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {

		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {

		this.createdDate = createdDate;
	}

	/**
	 * @return the updatedUserId
	 */
	public Long getUpdatedUserId() {

		return updatedUserId;
	}

	/**
	 * @param updatedUserId
	 *            the updatedUserId to set
	 */
	public void setUpdatedUserId(Long updatedUserId) {

		this.updatedUserId = updatedUserId;
	}

	/**
	 * @return the updatedPostId
	 */
	public Long getUpdatedPostId() {

		return updatedPostId;
	}

	/**
	 * @param updatedPostId
	 *            the updatedPostId to set
	 */
	public void setUpdatedPostId(Long updatedPostId) {

		this.updatedPostId = updatedPostId;
	}

	/**
	 * @return the updatedDate
	 */
	public Date getUpdatedDate() {

		return updatedDate;
	}

	/**
	 * @param updatedDate
	 *            the updatedDate to set
	 */
	public void setUpdatedDate(Date updatedDate) {

		this.updatedDate = updatedDate;
	}

	public String getBeamsBillType() {
		return beamsBillType;
	}

	public void setBeamsBillType(String beamsBillType) {
		this.beamsBillType = beamsBillType;
	}
}
