/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jan 29, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

/**
 * Class Description -Value Object class for DCPS_PHY_FORM_STATUS table.
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Jan 29, 2011
 */
public class RltPhyFormStatus implements java.io.Serializable {

	private Long dcpsPhyFormStatusId;
	private MstEmp dcpsEmpId;
	private Long treasuryCode;
	private String ddoCode;
	private Date ddoVerifTime;
	private String verifFlag;
	private Long phyFormRcvd;

	private Long langId;
	private Long locId;
	private Long dbId;

	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	/**
	 * @return the dcpsPhyFormStatusId
	 */
	public Long getDcpsPhyFormStatusId() {
		return dcpsPhyFormStatusId;
	}

	/**
	 * @param dcpsPhyFormStatusId
	 *            the dcpsPhyFormStatusId to set
	 */
	public void setDcpsPhyFormStatusId(Long dcpsPhyFormStatusId) {
		this.dcpsPhyFormStatusId = dcpsPhyFormStatusId;
	}

	/**
	 * @return the dcpsEmpId
	 */
	public MstEmp getDcpsEmpId() {
		return dcpsEmpId;
	}

	/**
	 * @param dcpsEmpId
	 *            the dcpsEmpId to set
	 */
	public void setDcpsEmpId(MstEmp dcpsEmpId) {
		this.dcpsEmpId = dcpsEmpId;
	}

	/**
	 * @return the treasuryCode
	 */
	public Long getTreasuryCode() {
		return treasuryCode;
	}

	/**
	 * @param treasuryCode
	 *            the treasuryCode to set
	 */
	public void setTreasuryCode(Long treasuryCode) {
		this.treasuryCode = treasuryCode;
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
	 * @return the ddoVerifTime
	 */
	public Date getDdoVerifTime() {
		return ddoVerifTime;
	}

	/**
	 * @param ddoVerifTime
	 *            the ddoVerifTime to set
	 */
	public void setDdoVerifTime(Date ddoVerifTime) {
		this.ddoVerifTime = ddoVerifTime;
	}

	/**
	 * @return the verifFlag
	 */
	public String getVerifFlag() {
		return verifFlag;
	}

	/**
	 * @param verifFlag
	 *            the verifFlag to set
	 */
	public void setVerifFlag(String verifFlag) {
		this.verifFlag = verifFlag;
	}

	/**
	 * @return the phyFormRcvd
	 */
	public Long getPhyFormRcvd() {
		return phyFormRcvd;
	}

	/**
	 * @param phyFormRcvd
	 *            the phyFormRcvd to set
	 */
	public void setPhyFormRcvd(Long phyFormRcvd) {
		this.phyFormRcvd = phyFormRcvd;
	}

	/**
	 * @return the langId
	 */
	public Long getLangId() {
		return langId;
	}

	/**
	 * @param langId
	 *            the langId to set
	 */
	public void setLangId(Long langId) {
		this.langId = langId;
	}

	/**
	 * @return the locId
	 */
	public Long getLocId() {
		return locId;
	}

	/**
	 * @param locId
	 *            the locId to set
	 */
	public void setLocId(Long locId) {
		this.locId = locId;
	}

	/**
	 * @return the dbId
	 */
	public Long getDbId() {
		return dbId;
	}

	/**
	 * @param dbId
	 *            the dbId to set
	 */
	public void setDbId(Long dbId) {
		this.dbId = dbId;
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

}