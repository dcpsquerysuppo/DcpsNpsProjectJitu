/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Oct 1, 2011		383385								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;
import java.util.Date;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Oct 1, 2011
 */


public class MstBankTmp 
{
	private Long bankId;

	private String bankCode;

	private String bankName;

	private String bShortName;

	private String bankAddress;

	private Long langId;

	private Long activateFlag;

	private Date startDate;

	private Date endDate;

	private Long createdUserId;

	private Long createdPostId;

	private Date createdDate;

	private Long updatedUserId;

	private Long updatedPostId;

	private Date updatedDate;

	private Long dbId;
	
	private String locationCode;
	
	private String micrCode;
	
	private Long bankGroupId;
	
	public MstBankTmp()
	{}
	
	/**
	 * 
	 * @param bankId
	 * @param bankCode
	 * @param bankName
	 * @param bShortName
	 * @param bankAddress
	 * @param langId
	 * @param activateFlag
	 * @param startDate
	 * @param endDate
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param dbId
	 * @param locationCode
	 * @param micrCode
	 * @param bankGroupId
	 */
	public MstBankTmp(Long bankId, String bankCode, String bankName,
			String bShortName, String bankAddress, Long langId,
			Long activateFlag, Date startDate, Date endDate,
			Long createdUserId, Long createdPostId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate,
			Long dbId, String locationCode, String micrCode, Long bankGroupId) {
		super();
		this.bankId = bankId;
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.bShortName = bShortName;
		this.bankAddress = bankAddress;
		this.langId = langId;
		this.activateFlag = activateFlag;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.micrCode = micrCode;
		this.bankGroupId = bankGroupId;
	}

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getbShortName() {
		return bShortName;
	}

	public void setbShortName(String bShortName) {
		this.bShortName = bShortName;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public Long getActivateFlag() {
		return activateFlag;
	}

	public void setActivateFlag(Long activateFlag) {
		this.activateFlag = activateFlag;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	public Long getDbId() {
		return dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getMicrCode() {
		return micrCode;
	}

	public void setMicrCode(String micrCode) {
		this.micrCode = micrCode;
	}

	public Long getbankGroupId() {
		return bankGroupId;
	}

	public void setbankGroupId(Long bankGroupId) {
		this.bankGroupId = bankGroupId;
	}

	
}
