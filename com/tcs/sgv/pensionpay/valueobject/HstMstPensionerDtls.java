/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 19, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Jun 19, 2011
 */
public class HstMstPensionerDtls implements Serializable {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -4199583223731167278L;

	// Fields
	private HstMstPensionerDtlsId id;

	private String pensionerCode;

	private String bankCode;

	private String branchCode;

	private String accountNo;

	private String locationCode;

	private String activeFlag;

	private BigDecimal createdUserId;

	private BigDecimal createdPostId;

	private Date createdDate;

	private BigDecimal updatedUserId;

	private BigDecimal updatedPostId;

	private Date updatedDate;

	private String oldStateCode;

	private String oldTreasury;

	private String oldSubTreasury;

	private String caseStatus;
	
	private Integer trnCounter;

	private String bankEmailId;

	private String isPwrofAtrny;
	
	private String registrationNo;

	private String identificationFlag;

	private Timestamp identificationDate = null;
	
	public HstMstPensionerDtls()
	{}

	/**
	 * 
	 * @param id
	 * @param pensionerCode
	 * @param bankCode
	 * @param branchCode
	 * @param accountNo
	 * @param locationCode
	 * @param activeFlag
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param oldStateCode
	 * @param oldTreasury
	 * @param oldSubTreasury
	 * @param caseStatus
	 * @param trnCounter
	 * @param bankEmailId
	 * @param isPwrofAtrny
	 * @param registrationNo
	 * @param identificationFlag
	 * @param identificationDate
	 */
	public HstMstPensionerDtls(HstMstPensionerDtlsId id, String pensionerCode,
			String bankCode, String branchCode, String accountNo,
			String locationCode, String activeFlag, BigDecimal createdUserId,
			BigDecimal createdPostId, Date createdDate,
			BigDecimal updatedUserId, BigDecimal updatedPostId,
			Date updatedDate, String oldStateCode, String oldTreasury,
			String oldSubTreasury, String caseStatus, Integer trnCounter,
			String bankEmailId, String isPwrofAtrny, String registrationNo,
			String identificationFlag, Timestamp identificationDate) {
		super();
		this.id = id;
		this.pensionerCode = pensionerCode;
		this.bankCode = bankCode;
		this.branchCode = branchCode;
		this.accountNo = accountNo;
		this.locationCode = locationCode;
		this.activeFlag = activeFlag;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.oldStateCode = oldStateCode;
		this.oldTreasury = oldTreasury;
		this.oldSubTreasury = oldSubTreasury;
		this.caseStatus = caseStatus;
		this.trnCounter = trnCounter;
		this.bankEmailId = bankEmailId;
		this.isPwrofAtrny = isPwrofAtrny;
		this.registrationNo = registrationNo;
		this.identificationFlag = identificationFlag;
		this.identificationDate = identificationDate;
	}


	public HstMstPensionerDtlsId getId() {
		return id;
	}

	public void setId(HstMstPensionerDtlsId id) {
		this.id = id;
	}

	public String getPensionerCode() {
		return pensionerCode;
	}

	public void setPensionerCode(String pensionerCode) {
		this.pensionerCode = pensionerCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}

	public BigDecimal getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(BigDecimal createdUserId) {
		this.createdUserId = createdUserId;
	}

	public BigDecimal getCreatedPostId() {
		return createdPostId;
	}

	public void setCreatedPostId(BigDecimal createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public BigDecimal getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(BigDecimal updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public BigDecimal getUpdatedPostId() {
		return updatedPostId;
	}

	public void setUpdatedPostId(BigDecimal updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getOldStateCode() {
		return oldStateCode;
	}

	public void setOldStateCode(String oldStateCode) {
		this.oldStateCode = oldStateCode;
	}

	public String getOldTreasury() {
		return oldTreasury;
	}

	public void setOldTreasury(String oldTreasury) {
		this.oldTreasury = oldTreasury;
	}

	public String getOldSubTreasury() {
		return oldSubTreasury;
	}

	public void setOldSubTreasury(String oldSubTreasury) {
		this.oldSubTreasury = oldSubTreasury;
	}

	public String getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}

	public Integer getTrnCounter() {
		return trnCounter;
	}

	public void setTrnCounter(Integer trnCounter) {
		this.trnCounter = trnCounter;
	}

	public String getBankEmailId() {
		return bankEmailId;
	}

	public void setBankEmailId(String bankEmailId) {
		this.bankEmailId = bankEmailId;
	}

	public String getIsPwrofAtrny() {
		return isPwrofAtrny;
	}

	public void setIsPwrofAtrny(String isPwrofAtrny) {
		this.isPwrofAtrny = isPwrofAtrny;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public String getIdentificationFlag() {
		return identificationFlag;
	}

	public void setIdentificationFlag(String identificationFlag) {
		this.identificationFlag = identificationFlag;
	}

	public Timestamp getIdentificationDate() {
		return identificationDate;
	}

	public void setIdentificationDate(Timestamp identificationDate) {
		this.identificationDate = identificationDate;
	}
	
		
}
