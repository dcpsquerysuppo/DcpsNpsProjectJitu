/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 12, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 12, 2011
 */
public class MstDummyOffice implements java.io.Serializable {

	private String dummyOfficeId;
	private String dummyOfficeName;
	private Long adminDept;
	private String offAddr1;
	private String offAddr2;
	private Long district;
	private Long taluka;
	private Long town;
	private String village;
	private Long pinCode;
	private String telNo1;
	private String telNo2;
	private String faxNo;
	private String emailAddr;
	private Character statusFlag;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	private String treasury;
	
	private Character isWithoutEmplrContri;

	public MstDummyOffice() {

	}

	public MstDummyOffice(String dummyOfficeId, String dummyOfficeName,
			Long adminDept, String offAddr1, String offAddr2,
			Long district, Long taluka, Long town, String village,
			Long pinCode, String telNo1, String telNo2, String faxNo,
			String emailAddr, Character statusFlag, Long langId, Long locId,
			Long dbId, Long createdPostId, Long createdUserId,
			Date createdDate, Long updatedUserId, Long updatedPostId,
			Date updatedDate,String treasury,Character isWithoutEmplrContri) {
		super();
		this.dummyOfficeId = dummyOfficeId;
		this.dummyOfficeName = dummyOfficeName;
		this.adminDept = adminDept;
		this.offAddr1 = offAddr1;
		this.offAddr2 = offAddr2;
		this.district = district;
		this.taluka = taluka;
		this.town = town;
		this.village = village;
		this.pinCode = pinCode;
		this.telNo1 = telNo1;
		this.telNo2 = telNo2;
		this.faxNo = faxNo;
		this.emailAddr = emailAddr;
		this.statusFlag = statusFlag;
		this.langId = langId;
		this.locId = locId;
		this.dbId = dbId;
		this.createdPostId = createdPostId;
		this.createdUserId = createdUserId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.treasury = treasury;
		this.isWithoutEmplrContri = isWithoutEmplrContri;
	}

	public String getDummyOfficeId() {
		return dummyOfficeId;
	}

	public void setDummyOfficeId(String dummyOfficeId) {
		this.dummyOfficeId = dummyOfficeId;
	}

	public String getDummyOfficeName() {
		return dummyOfficeName;
	}

	public void setDummyOfficeName(String dummyOfficeName) {
		this.dummyOfficeName = dummyOfficeName;
	}

	public Long getAdminDept() {
		return adminDept;
	}

	public void setAdminDept(Long adminDept) {
		this.adminDept = adminDept;
	}

	public String getOffAddr1() {
		return offAddr1;
	}

	public void setOffAddr1(String offAddr1) {
		this.offAddr1 = offAddr1;
	}

	public String getOffAddr2() {
		return offAddr2;
	}

	public void setOffAddr2(String offAddr2) {
		this.offAddr2 = offAddr2;
	}

	public Long getDistrict() {
		return district;
	}

	public void setDistrict(Long district) {
		this.district = district;
	}

	public Long getTaluka() {
		return taluka;
	}

	public void setTaluka(Long taluka) {
		this.taluka = taluka;
	}

	public Long getTown() {
		return town;
	}

	public void setTown(Long town) {
		this.town = town;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public Long getPinCode() {
		return pinCode;
	}

	public void setPinCode(Long pinCode) {
		this.pinCode = pinCode;
	}

	public String getTelNo1() {
		return telNo1;
	}

	public void setTelNo1(String telNo1) {
		this.telNo1 = telNo1;
	}

	public String getTelNo2() {
		return telNo2;
	}

	public void setTelNo2(String telNo2) {
		this.telNo2 = telNo2;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public Character getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(Character statusFlag) {
		this.statusFlag = statusFlag;
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
	
	public String getTreasury() {
		return treasury;
	}

	public void setTreasury(String treasury) {
		this.treasury = treasury;
	}
	
	public Character getIsWithoutEmplrContri() {
		return isWithoutEmplrContri;
	}

	public void setIsWithoutEmplrContri(Character isWithoutEmplrContri) {
		this.isWithoutEmplrContri = isWithoutEmplrContri;
	}
}
