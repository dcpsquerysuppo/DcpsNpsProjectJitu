package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class MstEmp implements java.io.Serializable, Cloneable {

	private Long dcpsEmpId;
	private Long orgEmpMstId;
	private String ddoCode;
	private String dcpsId;
	private String name;
	private String name_marathi;
	private String father_or_husband;
	private String salutation;
	private Character gender;
	private Date dob;
	private Date doj;
	private String designation;
	private String payCommission;
	private String payScale;
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
	private String reasonChangePFD;
	private String parentDept;
	private String cadre;
	private String group;
	private String firstDesignation;
	private Date appointmentDate;
	private String currOff;
	private String remarks;
	private String EIDNo;
	private String UIDNo;
	private String PANNo;
	private String bankName;
	private String branchName;
	private String IFSCCode;
	private String bankAccountNo;
	private Long photoAttachmentID;
	private Long signatureAttachmentID;
	private Long billGroupId;
	private String sentBackRemarks;
	private Double basicPay;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long formStatus;
	private Long phyRcvdFormStatus;
	private Date phyRcvdDate;
	private Long regStatus;
	private Date regStatusUpdtdDate;
	private Date approvalByDDODate;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Integer empOnDeptn;
	private String acDcpsMaintainedBy;
	private Character dcpsOrGpf;
	private Long pfdChangedBySRKA;
	private String acNonSRKAEmp;
	private String acMntndByOthers;
	private Long reasonForPSChange;
	private Date withEffectFromDate;
	private String otherReasonForPSChange;
	private String sevarthId;
	private Date deselectionDate;
	private String addressVTC;
	private Long ddoAsstOrNot;
	private Long empTransferred;
	private Long payInPayBand;
	private Long gradePay;
	private Long acMntndByOtherState;
	
	private Date superAnndate;
	private String buckleNo;
	private Date servEndDate;
	
	private String bioMetricNo;
	
	private Character servEndFlag;
	
	private Long dupEmpSentToMDC;
	
	//added bu shailesh
	private String religion;
	private String pranNo;

	private Character allindiaservoldemp ;
	
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
	//For NPS
	private String ppan; 
	private Date prangenDate; 
	//end
	//for deputation Employee configuration
	private String EmployeeType;
	private String intialAppOrderNo;
	public Double getSevenBasicPay() {
		return sevenBasicPay;
	}

	public void setSevenBasicPay(Double sevenBasicPay) {
		this.sevenBasicPay = sevenBasicPay;
	}

	private Date empDeputationDoj;
	private Date intialAppOrderDate;
	private String deputationFlag;
	private String deptDDOCode;
	//end
	//For 7pc integration
	private Double sevenBasicPay;
	//ended
	public String getDeptDDOCode() {
		return deptDDOCode;
	}

	public void setDeptDDOCode(String deptDDOCode) {
		this.deptDDOCode = deptDDOCode;
	}

	public String getDeputationFlag() {
		return deputationFlag;
	}

	public void setDeputationFlag(String deputationFlag) {
		this.deputationFlag = deputationFlag;
	}

	public String getEmployeeType() {
		return EmployeeType;
	}

	public void setEmployeeType(String employeeType) {
		EmployeeType = employeeType;
	}

	public String getIntialAppOrderNo() {
		return intialAppOrderNo;
	}

	public void setIntialAppOrderNo(String intialAppOrderNo) {
		this.intialAppOrderNo = intialAppOrderNo;
	}

	public Date getEmpDeputationDoj() {
		return empDeputationDoj;
	}

	public void setEmpDeputationDoj(Date empDeputationDoj) {
		this.empDeputationDoj = empDeputationDoj;
	}

	public Date getIntialAppOrderDate() {
		return intialAppOrderDate;
	}

	public void setIntialAppOrderDate(Date intialAppOrderDate) {
		this.intialAppOrderDate = intialAppOrderDate;
	}

	public String getIsAddressSame() {
		return isAddressSame;
	}

	public String getPpan() {
		return ppan;
	}

	public void setPpan(String ppan) {
		this.ppan = ppan;
	}

	public Date getPrangenDate() {
		return prangenDate;
	}

	public void setPrangenDate(Date prangenDate) {
		this.prangenDate = prangenDate;
	}

	public void setIsAddressSame(String isAddressSame) {
		this.isAddressSame = isAddressSame;
	}

	private String isAddressSame;


	public String getPAddressVTC() {
		return PAddressVTC;
	}

	public void setPAddressVTC(String addressVTC) {
		PAddressVTC = addressVTC;
	}

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

	public Character getAllindiaservoldemp() {
		return allindiaservoldemp;
	}

	public void setAllindiaservoldemp(Character allindiaservoldemp) {
		this.allindiaservoldemp = allindiaservoldemp;
	}
	
	public Long getDcpsEmpId() {

		return dcpsEmpId;
	}

	public void setDcpsEmpId(Long dcpsEmpId) {

		this.dcpsEmpId = dcpsEmpId;
	}

	/**
	 * @return the orgEmpMstId
	 */
	public Long getOrgEmpMstId() {

		return orgEmpMstId;
	}

	/**
	 * @param orgEmpMstId
	 *            the orgEmpMstId to set
	 */
	public void setOrgEmpMstId(Long orgEmpMstId) {

		this.orgEmpMstId = orgEmpMstId;
	}

	public String getDdoCode() {

		return ddoCode;
	}

	public void setDdoCode(String ddoCode) {

		this.ddoCode = ddoCode;
	}

	public String getDcpsId() {

		return dcpsId;
	}

	public void setDcpsId(String dcpsId) {

		this.dcpsId = dcpsId;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
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

	public String getParentDept() {

		return parentDept;
	}

	public void setParentDept(String parentDept) {

		this.parentDept = parentDept;
	}
	
	public String getReasonChangePFD() {

		return reasonChangePFD;
	}

	public void setReasonChangePFD(String reasonChangePFD) {

		this.reasonChangePFD = reasonChangePFD;
	}

	public String getCadre() {

		return cadre;
	}

	public void setCadre(String cadre) {

		this.cadre = cadre;
	}

	public String getGroup() {

		return group;
	}

	public void setGroup(String group) {

		this.group = group;
	}

	public String getCurrOff() {

		return currOff;
	}

	public void setCurrOff(String currOff) {

		this.currOff = currOff;
	}

	public String getDesignation() {

		return designation;
	}

	public void setDesignation(String designation) {

		this.designation = designation;
	}

	public String getPayCommission() {

		return payCommission;
	}

	public void setPayCommission(String payCommission) {

		this.payCommission = payCommission;
	}

	public String getPayScale() {

		return payScale;
	}

	public void setPayScale(String payScale) {

		this.payScale = payScale;
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

	public String getRemarks() {

		return remarks;
	}

	public void setRemarks(String remarks) {

		this.remarks = remarks;
	}

	public String getEIDNo() {
		return EIDNo;
	}

	public void setEIDNo(String no) {
		EIDNo = no;
	}

	public String getUIDNo() {

		return UIDNo;
	}

	public void setUIDNo(String no) {

		UIDNo = no;
	}

	public String getPANNo() {

		return PANNo;
	}

	public void setPANNo(String no) {

		PANNo = no;
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

	public String getIFSCCode() {

		return IFSCCode;
	}

	public void setIFSCCode(String IFSCCode) {

		this.IFSCCode = IFSCCode;
	}

	public String getBankAccountNo() {

		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {

		this.bankAccountNo = bankAccountNo;
	}

	public Long getPhotoAttachmentID() {

		return photoAttachmentID;
	}

	public void setPhotoAttachmentID(Long photoAttachmentID) {

		this.photoAttachmentID = photoAttachmentID;
	}

	public Long getSignatureAttachmentID() {

		return signatureAttachmentID;
	}

	public void setSignatureAttachmentID(Long signatureAttachmentID) {

		this.signatureAttachmentID = signatureAttachmentID;
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

	public Long getFormStatus() {

		return formStatus;
	}

	public void setFormStatus(Long formStatus) {

		this.formStatus = formStatus;
	}

	public Long getPhyRcvdFormStatus() {

		return phyRcvdFormStatus;
	}

	public void setPhyRcvdFormStatus(Long phyRcvdFormStatus) {

		this.phyRcvdFormStatus = phyRcvdFormStatus;
	}
	
	public Date getPhyRcvdDate() {

		return phyRcvdDate;
	}

	public void setPhyRcvdDate(Date phyRcvdDate) {

		this.phyRcvdDate = phyRcvdDate;
	}

	public Long getRegStatus() {

		return regStatus;
	}

	public void setRegStatus(Long regStatus) {

		this.regStatus = regStatus;
	}

	public Date getRegStatusUpdtdDate() {

		return regStatusUpdtdDate;
	}

	public void setRegStatusUpdtdDate(Date regStatusUpdtdDate) {

		this.regStatusUpdtdDate = regStatusUpdtdDate;
	}

	public Date getApprovalByDDODate() {

		return approvalByDDODate;
	}

	public void setApprovalByDDODate(Date approvalByDDODate) {

		this.approvalByDDODate = approvalByDDODate;
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

	public Long getBillGroupId() {

		return billGroupId;
	}

	public void setBillGroupId(Long billGroupId) {

		this.billGroupId = billGroupId;
	}

	public String getSentBackRemarks() {

		return sentBackRemarks;
	}

	public void setSentBackRemarks(String sentBackRemarks) {

		this.sentBackRemarks = sentBackRemarks;
	}

	public Double getBasicPay() {

		return basicPay;
	}

	public void setBasicPay(Double basicPay) {

		this.basicPay = basicPay;
	}

	/**
	 * @return the name_marathi
	 */
	public String getName_marathi() {

		return name_marathi;
	}

	/**
	 * @param nameMarathi
	 *            the name_marathi to set
	 */
	public void setName_marathi(String nameMarathi) {

		name_marathi = nameMarathi;
	}

	/**
	 * @return the father_or_husband
	 */
	public String getFather_or_husband() {

		return father_or_husband;
	}

	/**
	 * @param fatherOrHusband
	 *            the father_or_husband to set
	 */
	public void setFather_or_husband(String fatherOrHusband) {

		father_or_husband = fatherOrHusband;
	}

	public String getSalutation() {

		return salutation;
	}

	/**
	 * @param salutation
	 *            the salutation to set
	 */
	public void setSalutation(String salutation) {

		this.salutation = salutation;
	}

	/**
	 * @return the building_address
	 */
	public String getBuilding_address() {

		return building_address;
	}

	/**
	 * @param buildingAddress
	 *            the building_address to set
	 */
	public void setBuilding_address(String buildingAddress) {

		building_address = buildingAddress;
	}

	/**
	 * @return the building_street
	 */
	public String getBuilding_street() {

		return building_street;
	}

	/**
	 * @param buildingStreet
	 *            the building_street to set
	 */
	public void setBuilding_street(String buildingStreet) {

		building_street = buildingStreet;
	}

	/**
	 * @return the landmark
	 */
	public String getLandmark() {

		return landmark;
	}

	/**
	 * @param landmark
	 *            the landmark to set
	 */
	public void setLandmark(String landmark) {

		this.landmark = landmark;
	}

	/**
	 * @return the locality
	 */
	public String getLocality() {

		return locality;
	}

	/**
	 * @param locality
	 *            the locality to set
	 */
	public void setLocality(String locality) {

		this.locality = locality;
	}

	/**
	 * @return the district
	 */
	public String getDistrict() {

		return district;
	}

	/**
	 * @param district
	 *            the district to set
	 */
	public void setDistrict(String district) {

		this.district = district;
	}

	/**
	 * @return the state
	 */
	public String getState() {

		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {

		this.state = state;
	}

	/**
	 * @return the pincode
	 */
	public Long getPincode() {

		return pincode;
	}

	/**
	 * @param pincode
	 *            the pincode to set
	 */
	public void setPincode(Long pincode) {

		this.pincode = pincode;
	}

	/**
	 * @return the emailId
	 */
	public String getEmailId() {

		return emailId;
	}

	/**
	 * @param emailId
	 *            the emailId to set
	 */
	public void setEmailId(String emailId) {

		this.emailId = emailId;
	}
	
	/**
	 * @return the firstDesignation
	 */
	public String getFirstDesignation() {

		return firstDesignation;
	}

	/**
	 * @param firstDesignation
	 *            the firstDesignation to set
	 */
	public void setFirstDesignation(String firstDesignation) {

		this.firstDesignation = firstDesignation;
	}

	/**
	 * @return the appointmentDate
	 */
	public Date getAppointmentDate() {

		return appointmentDate;
	}

	/**
	 * @param appointmentDate
	 *            the appointmentDate to set
	 */
	public void setAppointmentDate(Date appointmentDate) {

		this.appointmentDate = appointmentDate;
	}

	public Integer getEmpOnDeptn() {

		return empOnDeptn;
	}

	public void setEmpOnDeptn(Integer empOnDeptn) {

		this.empOnDeptn = empOnDeptn;
	}
	
	public String getAcDcpsMaintainedBy() {

		return acDcpsMaintainedBy;
	}

	public void setAcDcpsMaintainedBy(String acDcpsMaintainedBy) {

		this.acDcpsMaintainedBy = acDcpsMaintainedBy;
	}
	
	public Character getDcpsOrGpf() {
		
		return dcpsOrGpf;
	}

	public void setDcpsOrGpf(Character dcpsOrGpf) {
		
		this.dcpsOrGpf = dcpsOrGpf;
	}
	
	public Long getPfdChangedBySRKA() {

		return pfdChangedBySRKA;
	}

	public void setPfdChangedBySRKA(Long pfdChangedBySRKA) {

		this.pfdChangedBySRKA = pfdChangedBySRKA;
	}
	
	public String getAcNonSRKAEmp() {

		return acNonSRKAEmp;
	}

	public void setAcNonSRKAEmp(String acNonSRKAEmp) {

		this.acNonSRKAEmp = acNonSRKAEmp;
	}
	
	public String getAcMntndByOthers() {

		return acMntndByOthers;
	}

	public void setAcMntndByOthers(String acMntndByOthers) {

		this.acMntndByOthers = acMntndByOthers;
	}
	
	public Long getReasonForPSChange() {
		
		return reasonForPSChange;
	}

	public void setReasonForPSChange(Long reasonForPSChange) {
		
		this.reasonForPSChange = reasonForPSChange;
	}
	
	public String getOtherReasonForPSChange() {

		return otherReasonForPSChange;
	}

	public void setOtherReasonForPSChange(String otherReasonForPSChange) {

		this.otherReasonForPSChange = otherReasonForPSChange;
	}
	
	public Date getWithEffectFromDate() {
		
		return withEffectFromDate;
	}

	public void setWithEffectFromDate(Date withEffectFromDate) {
		
		this.withEffectFromDate = withEffectFromDate;
	}
	
	public String getSevarthId() {

		return sevarthId;
	}

	public void setSevarthId(String sevarthId) {

		this.sevarthId = sevarthId;
	}
	
	public Date getDeselectionDate() {

		return deselectionDate;
	}

	public void setDeselectionDate(Date deselectionDate) {

		this.deselectionDate = deselectionDate;
	}
	
	public String getAddressVTC() {

		return addressVTC;
	}

	public void setAddressVTC(String addressVTC) {

		this.addressVTC = addressVTC;
	}
	
	public Long getDdoAsstOrNot() {

		return ddoAsstOrNot;
	}

	public void setDdoAsstOrNot(Long ddoAsstOrNot) {

		this.ddoAsstOrNot = ddoAsstOrNot;
	}
	
	
	public Long getEmpTransferred() {

		return empTransferred;
	}

	public void setEmpTransferred(Long empTransferred) {

		this.empTransferred = empTransferred;
	}
	
	public Long getPayInPayBand() {

		return payInPayBand;
	}

	public void setPayInPayBand(Long payInPayBand) {

		this.payInPayBand = payInPayBand;
	}
	
	public Long getGradePay() {

		return gradePay;
	}

	public void setGradePay(Long gradePay) {

		this.gradePay = gradePay;
	}
	
	public Long getAcMntndByOtherState() {

		return acMntndByOtherState;
	}

	public void setAcMntndByOtherState(Long acMntndByOtherState) {

		this.acMntndByOtherState = acMntndByOtherState;
	}
	
	public Date getSuperAnndate() {

		return superAnndate;
	}

	public void setSuperAnndate(Date superAnndate) {

		this.superAnndate = superAnndate;
	}
	
	public String getBuckleNo() {

		return buckleNo;
	}

	public void setBuckleNo(String buckleNo) {

		this.buckleNo = buckleNo;
	}
	
	public Date getServEndDate() {

		return servEndDate;
	}

	public void setServEndDate(Date servEndDate) {

		this.servEndDate = servEndDate;
	}
	
	public String getBioMetricNo() {

		return bioMetricNo;
	}

	public void setBioMetricNo(String bioMetricNo) {

		this.bioMetricNo = bioMetricNo;
	}
	
	public Character getServEndFlag() {
		
		return servEndFlag;
	}

	public void setServEndFlag(Character servEndFlag) {
		
		this.servEndFlag = servEndFlag;
	}
	
	public Long getDupEmpSentToMDC() {

		return dupEmpSentToMDC;
	}

	public void setDupEmpSentToMDC(Long dupEmpSentToMDC) {

		this.dupEmpSentToMDC = dupEmpSentToMDC;
	}
	
	public Object clone() {

		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getPranNo() {
		return pranNo;
	}

	public void setPranNo(String pranNo) {
		this.pranNo = pranNo;
	}

	public Object getEmpEligibilityFlag() {
		// TODO Auto-generated method stub
		return null;
	}

}