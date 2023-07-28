/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Vihan Khatri								
 *******************************************************************************
 */
/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 25, 2011
 */
package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class HstDcpsPersonalChanges implements java.io.Serializable, Cloneable {

	private Long dcpsPersonalChangesId;
	private Long dcpsChangesId;
	private Long dcpsEmpId;
	private String name;
	private String name_marathi;
	private String father_or_husband;
	private String salutation;
	private Character gender;
	private Date dob;
	private Date doj;
	private String phychallanged;
	private String building_address;
	private String building_street;
	private String landmark;
	private String locality;
	private String district;
	private String state;
	private Long pincode;
	private String cntctNo;
	private String cellNo;
	private String emailId;
	private String UIDNo;
	private String EIDNo;
	private String PANNo;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	// added by arpan 06/01
	private String PBuildingAddress;
	private String PBuildingStreet;
	private String PLandmark;
	private String PLocality;
	private String PDistrict;
	private String PState;
	private Long PPincode;
	private String PCntctNo;
	private String PCellNo;
	private String PAddressVTC;
	public String getAddressVTC() {
		return addressVTC;
	}

	public void setAddressVTC(String addressVTC) {
		this.addressVTC = addressVTC;
	}

	private String addressVTC; 
	private String isAddressSame;

	public String getPBuildingAddress() {
		return PBuildingAddress;
	}

	public void setPBuildingAddress(String buildingAddress) {
		PBuildingAddress = buildingAddress;
	}

	public String getPBuildingStreet() {
		return PBuildingStreet;
	}

	public void setPBuildingStreet(String buildingStreet) {
		PBuildingStreet = buildingStreet;
	}

	public String getPLandmark() {
		return PLandmark;
	}

	public void setPLandmark(String landmark) {
		PLandmark = landmark;
	}

	public String getPLocality() {
		return PLocality;
	}

	public void setPLocality(String locality) {
		PLocality = locality;
	}

	public String getPDistrict() {
		return PDistrict;
	}

	public void setPDistrict(String district) {
		PDistrict = district;
	}

	public String getPState() {
		return PState;
	}

	public void setPState(String state) {
		PState = state;
	}

	public Long getPPincode() {
		return PPincode;
	}

	public void setPPincode(Long pincode) {
		PPincode = pincode;
	}

	public String getPCntctNo() {
		return PCntctNo;
	}

	public void setPCntctNo(String cntctNo) {
		PCntctNo = cntctNo;
	}

	public String getPCellNo() {
		return PCellNo;
	}

	public void setPCellNo(String cellNo) {
		PCellNo = cellNo;
	}

	public String getPAddressVTC() {
		return PAddressVTC;
	}

	public void setPAddressVTC(String addressVTC) {
		PAddressVTC = addressVTC;
	}

	public String getIsAddressSame() {
		return isAddressSame;
	}

	public void setIsAddressSame(String isAddressSame) {
		this.isAddressSame = isAddressSame;
	}

	public Long getDcpsPersonalChangesId() {
		return dcpsPersonalChangesId;
	}

	public void setDcpsPersonalChangesId(Long dcpsPersonalChangesId) {
		this.dcpsPersonalChangesId = dcpsPersonalChangesId;
	}

	public Long getDcpsChangesId() {
		return dcpsChangesId;
	}

	public void setDcpsChangesId(Long dcpsChangesId) {
		this.dcpsChangesId = dcpsChangesId;
	}

	public Long getDcpsEmpId() {
		return dcpsEmpId;
	}

	public void setDcpsEmpId(Long dcpsEmpId) {
		this.dcpsEmpId = dcpsEmpId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName_marathi() {
		return name_marathi;
	}

	public void setName_marathi(String name_marathi) {
		this.name_marathi = name_marathi;
	}

	public String getFather_or_husband() {
		return father_or_husband;
	}

	public void setFather_or_husband(String father_or_husband) {
		this.father_or_husband = father_or_husband;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public Character getGender() {
		return gender;
	}

	public void setGender(Character gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getDoj() {
		return doj;
	}

	public void setDoj(Date doj) {
		this.doj = doj;
	}

	public String getPhychallanged() {
		return phychallanged;
	}

	public void setPhychallanged(String phychallanged) {
		this.phychallanged = phychallanged;
	}

	public String getBuilding_address() {
		return building_address;
	}

	public void setBuilding_address(String building_address) {
		this.building_address = building_address;
	}

	public String getBuilding_street() {
		return building_street;
	}

	public void setBuilding_street(String building_street) {
		this.building_street = building_street;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}

	public String getCntctNo() {
		return cntctNo;
	}

	public void setCntctNo(String cntctNo) {
		this.cntctNo = cntctNo;
	}

	public String getCellNo() {
		return cellNo;
	}

	public void setCellNo(String cellNo) {
		this.cellNo = cellNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getUIDNo() {
		return UIDNo;
	}

	public void setUIDNo(String no) {
		UIDNo = no;
	}
	
	public String getEIDNo() {
		return EIDNo;
	}

	public void setEIDNo(String no) {
		EIDNo = no;
	}

	public String getPANNo() {
		return PANNo;
	}

	public void setPANNo(String no) {
		PANNo = no;
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

	public Object clone() {

		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}

}
