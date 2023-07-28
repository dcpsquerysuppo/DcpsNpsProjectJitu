/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 17, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Jun 17, 2011
 */
public class HstMstPensionerHdr  implements Serializable {
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -7722151172580401689L;

	// Fields

	private HstMstPensionerHdrId Id;

	private String pensionerCode;

	private String pnsrFileNo; // new

	private String applnNO; // new

	private String firstName;

	private String middleName;

	private String lastName;

	private String pensionerAddr;

	private String apenBRAddr; // new

	private BigDecimal stateCode;

	private BigDecimal districtCode;

	private String teleNo;

	private Date dateOfBirth;

	private Date dateOfJoin;

	private Date dateOfRetirement;

	private Date dateOfDeath;

	private String aliveFlag;

	private String panNo;

	private String payScale;

	private BigDecimal lastPay;

	private String gender;

	private String officeAddr;

	private BigDecimal deptCode;

	private BigDecimal hodCode;

	private BigDecimal attachmentPhotoId;

	private String locationCode;

	private BigDecimal createdUserId;

	private BigDecimal createdPostId;

	private Date createdDate;

	private BigDecimal updatedUserId;

	private BigDecimal updatedPostId;

	private Date updatedDate;

	private String classType;

	private String cadreType;

	private String designation;

	private String caseStatus;

	private String identityMark;

	private String height;
	
	private Integer trnCounter;
	
	private String pnsnrEmailId;

	private String officeEmailId;

	private String pnsnrPrefix;

	private BigDecimal pensionerPhotoId;

	private String otherDesignation;

	private BigDecimal pensionerSignId;

	private String employmentOffice;
	
	// --------New Fields Added
	private String  moblileNo;
	
	private Long retiringDepartment;
	
    private String pnsnrAddr1;
	
	private String pnsnrAddr2;
	
	private String pnsnrAddrTown;
	
	private String pnsnrAddrLocality;
	
	private Integer pinCode;
	
	private String pnsnrNameInMarathi;
	
	private String pnsnrFatherName;
	
	private String guardianName;
	
	private String guardianFatherName;
	
	private String guardianAddr1;
	
	private String guardianAddr2;
	
	private String guardianAddrTown;
	
	private String guardianAddrLocality;
	
	private BigDecimal guardianAddrState;

	private BigDecimal guardianAddrDistrict; 
	
	private Integer guardianAddrPinCode;
	
	private String guardianRelation;
	
	private String guardianAddr;
	
	private String isAddrSameFlag;
	// Constructors

	public String getEmploymentOffice() {
		return employmentOffice;
	}

	public void setEmploymentOffice(String employmentOffice) {
		this.employmentOffice = employmentOffice;
	}

	/** default constructor */
	public HstMstPensionerHdr() {
	}

	
	/**
	 * 
	 * @param id
	 * @param pensionerCode
	 * @param pnsrFileNo
	 * @param applnNO
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param pensionerAddr
	 * @param apenBRAddr
	 * @param stateCode
	 * @param districtCode
	 * @param teleNo
	 * @param dateOfBirth
	 * @param dateOfJoin
	 * @param dateOfRetirement
	 * @param dateOfDeath
	 * @param aliveFlag
	 * @param panNo
	 * @param payScale
	 * @param lastPay
	 * @param gender
	 * @param officeAddr
	 * @param deptCode
	 * @param hodCode
	 * @param attachmentPhotoId
	 * @param locationCode
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param classType
	 * @param cadreType
	 * @param designation
	 * @param caseStatus
	 * @param identityMark
	 * @param height
	 * @param trnCounter
	 * @param pnsnrEmailId
	 * @param officeEmailId
	 * @param pnsnrPrefix
	 * @param pensionerPhotoId
	 * @param otherDesignation
	 * @param pensionerSignId
	 * @param employmentOffice
	 * @param moblileNo
	 * @param retiringDepartment
	 * @param pnsnrAddr1
	 * @param pnsnrAddr2
	 * @param pnsnrAddrTown
	 * @param pnsnrAddrLocality
	 * @param pinCode
	 * @param pnsnrNameInMarathi
	 * @param pnsnrFatherName
	 * @param guardianName
	 * @param guardianFatherName
	 * @param guardianAddr1
	 * @param guardianAddr2
	 * @param guardianAddrTown
	 * @param guardianAddrLocality
	 * @param guardianAddrState
	 * @param guardianAddrDistrict
	 * @param guardianAddrPinCode
	 * @param guardianRelation
	 * @param guardianAddr
	 * @param isAddrSameFlag
	 */
	public HstMstPensionerHdr(HstMstPensionerHdrId id, String pensionerCode, String pnsrFileNo, String applnNO, String firstName, String middleName, String lastName, String pensionerAddr,
			String apenBRAddr, BigDecimal stateCode, BigDecimal districtCode, String teleNo, Date dateOfBirth, Date dateOfJoin, Date dateOfRetirement, Date dateOfDeath, String aliveFlag,
			String panNo, String payScale, BigDecimal lastPay, String gender, String officeAddr, BigDecimal deptCode, BigDecimal hodCode, BigDecimal attachmentPhotoId, String locationCode,
			BigDecimal createdUserId, BigDecimal createdPostId, Date createdDate, BigDecimal updatedUserId, BigDecimal updatedPostId, Date updatedDate, String classType, String cadreType,
			String designation, String caseStatus, String identityMark, String height, Integer trnCounter, String pnsnrEmailId, String officeEmailId, String pnsnrPrefix, BigDecimal pensionerPhotoId,
			String otherDesignation, BigDecimal pensionerSignId, String employmentOffice, String moblileNo, Long retiringDepartment, String pnsnrAddr1, String pnsnrAddr2, String pnsnrAddrTown,
			String pnsnrAddrLocality, Integer pinCode, String pnsnrNameInMarathi, String pnsnrFatherName, String guardianName, String guardianFatherName, String guardianAddr1, String guardianAddr2,
			String guardianAddrTown, String guardianAddrLocality, BigDecimal guardianAddrState, BigDecimal guardianAddrDistrict, Integer guardianAddrPinCode, String guardianRelation,
			String guardianAddr,String isAddrSameFlag) {

		super();
		Id = id;
		this.pensionerCode = pensionerCode;
		this.pnsrFileNo = pnsrFileNo;
		this.applnNO = applnNO;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.pensionerAddr = pensionerAddr;
		this.apenBRAddr = apenBRAddr;
		this.stateCode = stateCode;
		this.districtCode = districtCode;
		this.teleNo = teleNo;
		this.dateOfBirth = dateOfBirth;
		this.dateOfJoin = dateOfJoin;
		this.dateOfRetirement = dateOfRetirement;
		this.dateOfDeath = dateOfDeath;
		this.aliveFlag = aliveFlag;
		this.panNo = panNo;
		this.payScale = payScale;
		this.lastPay = lastPay;
		this.gender = gender;
		this.officeAddr = officeAddr;
		this.deptCode = deptCode;
		this.hodCode = hodCode;
		this.attachmentPhotoId = attachmentPhotoId;
		this.locationCode = locationCode;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.classType = classType;
		this.cadreType = cadreType;
		this.designation = designation;
		this.caseStatus = caseStatus;
		this.identityMark = identityMark;
		this.height = height;
		this.trnCounter = trnCounter;
		this.pnsnrEmailId = pnsnrEmailId;
		this.officeEmailId = officeEmailId;
		this.pnsnrPrefix = pnsnrPrefix;
		this.pensionerPhotoId = pensionerPhotoId;
		this.otherDesignation = otherDesignation;
		this.pensionerSignId = pensionerSignId;
		this.employmentOffice = employmentOffice;
		this.moblileNo = moblileNo;
		this.retiringDepartment = retiringDepartment;
		this.pnsnrAddr1 = pnsnrAddr1;
		this.pnsnrAddr2 = pnsnrAddr2;
		this.pnsnrAddrTown = pnsnrAddrTown;
		this.pnsnrAddrLocality = pnsnrAddrLocality;
		this.pinCode = pinCode;
		this.pnsnrNameInMarathi = pnsnrNameInMarathi;
		this.pnsnrFatherName = pnsnrFatherName;
		this.guardianName = guardianName;
		this.guardianFatherName = guardianFatherName;
		this.guardianAddr1 = guardianAddr1;
		this.guardianAddr2 = guardianAddr2;
		this.guardianAddrTown = guardianAddrTown;
		this.guardianAddrLocality = guardianAddrLocality;
		this.guardianAddrState = guardianAddrState;
		this.guardianAddrDistrict = guardianAddrDistrict;
		this.guardianAddrPinCode = guardianAddrPinCode;
		this.guardianRelation = guardianRelation;
		this.guardianAddr = guardianAddr;
		this.isAddrSameFlag=isAddrSameFlag;
	}

	// Property accessors
	

	public HstMstPensionerHdrId getId() {
		return Id;
	}

	public void setId(HstMstPensionerHdrId id) {
		Id = id;
	}

	public String getPensionerCode() {
		return this.pensionerCode;
	}

	public void setPensionerCode(String pensionerCode) {
		this.pensionerCode = pensionerCode;
	}

	public String getPnsrFileNo() {
		return pnsrFileNo;
	}

	public void setPnsrFileNo(String pnsrFileNo) {
		this.pnsrFileNo = pnsrFileNo;
	}

	public String getApplnNO() {
		return applnNO;
	}

	public void setApplnNO(String applnNO) {
		this.applnNO = applnNO;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPensionerAddr() {
		return pensionerAddr;
	}

	public void setPensionerAddr(String pensionerAddr) {
		this.pensionerAddr = pensionerAddr;
	}

	public String getApenBRAddr() {
		return apenBRAddr;
	}

	public void setApenBRAddr(String apenBRAddr) {
		this.apenBRAddr = apenBRAddr;
	}

	public BigDecimal getStateCode() {
		return this.stateCode;
	}

	public void setStateCode(BigDecimal stateCode) {
		this.stateCode = stateCode;
	}

	public BigDecimal getDistrictCode() {
		return this.districtCode;
	}

	public void setDistrictCode(BigDecimal districtCode) {
		this.districtCode = districtCode;
	}

	public String getTeleNo() {
		return this.teleNo;
	}

	public void setTeleNo(String teleNo) {
		this.teleNo = teleNo;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Date getDateOfJoin() {
		return this.dateOfJoin;
	}

	public void setDateOfJoin(Date dateOfJoin) {
		this.dateOfJoin = dateOfJoin;
	}

	public Date getDateOfRetirement() {
		return this.dateOfRetirement;
	}

	public void setDateOfRetirement(Date dateOfRetirement) {
		this.dateOfRetirement = dateOfRetirement;
	}

	public Date getDateOfDeath() {
		return this.dateOfDeath;
	}

	public void setDateOfDeath(Date dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	public String getAliveFlag() {
		return this.aliveFlag;
	}

	public void setAliveFlag(String aliveFlag) {
		this.aliveFlag = aliveFlag;
	}

	public String getPanNo() {
		return this.panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getPayScale() {
		return this.payScale;
	}

	public void setPayScale(String payScale) {
		this.payScale = payScale;
	}

	public BigDecimal getLastPay() {
		return this.lastPay;
	}

	public void setLastPay(BigDecimal lastPay) {
		this.lastPay = lastPay;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getOfficeAddr() {
		return this.officeAddr;
	}

	public void setOfficeAddr(String officeAddr) {
		this.officeAddr = officeAddr;
	}

	public BigDecimal getDeptCode() {
		return this.deptCode;
	}

	public void setDeptCode(BigDecimal deptCode) {
		this.deptCode = deptCode;
	}

	public BigDecimal getHodCode() {
		return this.hodCode;
	}

	public void setHodCode(BigDecimal hodCode) {
		this.hodCode = hodCode;
	}

	public BigDecimal getAttachmentPhotoId() {
		return this.attachmentPhotoId;
	}

	public void setAttachmentPhotoId(BigDecimal attachmentPhotoId) {
		this.attachmentPhotoId = attachmentPhotoId;
	}

	public String getLocationCode() {
		return this.locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public BigDecimal getCreatedUserId() {
		return this.createdUserId;
	}

	public void setCreatedUserId(BigDecimal createdUserId) {
		this.createdUserId = createdUserId;
	}

	public BigDecimal getCreatedPostId() {
		return this.createdPostId;
	}

	public void setCreatedPostId(BigDecimal createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public BigDecimal getUpdatedUserId() {
		return this.updatedUserId;
	}

	public void setUpdatedUserId(BigDecimal updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public BigDecimal getUpdatedPostId() {
		return this.updatedPostId;
	}

	public void setUpdatedPostId(BigDecimal updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getClassType() {
		return this.classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getCadreType() {
		return this.cadreType;
	}

	public void setCadreType(String cadreType) {
		this.cadreType = cadreType;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getCaseStatus() {
		return this.caseStatus;
	}

	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}

	public String getIdentityMark() {
		return this.identityMark;
	}

	public void setIdentityMark(String identityMark) {
		this.identityMark = identityMark;
	}

	public String getHeight() {
		return this.height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public Integer getTrnCounter() {
		return trnCounter;
	}

	public void setTrnCounter(Integer trnCounter) {
		this.trnCounter = trnCounter;
	}

	public void setPnsnrEmailId(String pnsnrEmailId) {
		this.pnsnrEmailId = pnsnrEmailId;
	}

	public String getPnsnrEmailId() {
		return this.pnsnrEmailId;
	}

	public void setOfficeEmailId(String officeEmailId) {
		this.officeEmailId = officeEmailId;
	}

	public String getOfficeEmailId() {
		return this.officeEmailId;
	}

	public void setPnsnrPrefix(String pnsnrPrefix) {
		this.pnsnrPrefix = pnsnrPrefix;
	}

	public String getPnsnrPrefix() {
		return this.pnsnrPrefix;
	}

	public BigDecimal getPensionerPhotoId() {
		return this.pensionerPhotoId;
	}

	public void setPensionerPhotoId(BigDecimal pensionerPhotoId) {
		this.pensionerPhotoId = pensionerPhotoId;
	}

	public void setOtherDesignation(String otherDesignation) {
		this.otherDesignation = otherDesignation;
	}

	public String getOtherDesignation() {
		return this.otherDesignation;
	}

	public BigDecimal getPensionerSignId() {
		return this.pensionerSignId;
	}

	public void setPensionerSignId(BigDecimal pensionerSignId) {
		this.pensionerSignId = pensionerSignId;
	}

	public String getMoblileNo() {
		return moblileNo;
	}

	public void setMoblileNo(String moblileNo) {
		this.moblileNo = moblileNo;
	}

	public Long getRetiringDepartment() {
		return retiringDepartment;
	}

	public void setRetiringDepartment(Long retiringDepartment) {
		this.retiringDepartment = retiringDepartment;
	}

	public String getPnsnrAddr1() {
	
		return pnsnrAddr1;
	}
	
	public void setPnsnrAddr1(String pnsnrAddr1) {
	
		this.pnsnrAddr1 = pnsnrAddr1;
	}
	
	public String getPnsnrAddr2() {
	
		return pnsnrAddr2;
	}
	
	public void setPnsnrAddr2(String pnsnrAddr2) {
	
		this.pnsnrAddr2 = pnsnrAddr2;
	}
	
	public String getPnsnrAddrTown() {
	
		return pnsnrAddrTown;
	}
	
	public void setPnsnrAddrTown(String pnsnrAddrTown) {
	
		this.pnsnrAddrTown = pnsnrAddrTown;
	}

	public String getPnsnrAddrLocality() {
		return pnsnrAddrLocality;
	}

	public void setPnsnrAddrLocality(String pnsnrAddrLocality) {
		this.pnsnrAddrLocality = pnsnrAddrLocality;
	}

	public Integer getPinCode() {
		return pinCode;
	}

	public void setPinCode(Integer pinCode) {
		this.pinCode = pinCode;
	}

	public String getPnsnrNameInMarathi() {
		return pnsnrNameInMarathi;
	}

	public void setPnsnrNameInMarathi(String pnsnrNameInMarathi) {
		this.pnsnrNameInMarathi = pnsnrNameInMarathi;
	}

	public String getPnsnrFatherName() {
		return pnsnrFatherName;
	}

	public void setPnsnrFatherName(String pnsnrFatherName) {
		this.pnsnrFatherName = pnsnrFatherName;
	}

	public String getGuardianName() {
		return guardianName;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	public String getGuardianFatherName() {
		return guardianFatherName;
	}

	public void setGuardianFatherName(String guardianFatherName) {
		this.guardianFatherName = guardianFatherName;
	}

	public String getGuardianAddr1() {
		return guardianAddr1;
	}
	
	public void setGuardianAddr1(String guardianAddr1) {
		this.guardianAddr1 = guardianAddr1;
	}
	
	public String getGuardianAddr2() {
		return guardianAddr2;
	}
	
	public void setGuardianAddr2(String guardianAddr2) {
		this.guardianAddr2 = guardianAddr2;
	}
	
	public String getGuardianAddrTown() {
		return guardianAddrTown;
	}
	
	public void setGuardianAddrTown(String guardianAddrTown) {
	
		this.guardianAddrTown = guardianAddrTown;
	}

	public String getGuardianAddrLocality() {
		return guardianAddrLocality;
	}

	public void setGuardianAddrLocality(String guardianAddrLocality) {
		this.guardianAddrLocality = guardianAddrLocality;
	}

	public BigDecimal getGuardianAddrState() {
		return guardianAddrState;
	}

	public void setGuardianAddrState(BigDecimal guardianAddrState) {
		this.guardianAddrState = guardianAddrState;
	}

	public BigDecimal getGuardianAddrDistrict() {
		return guardianAddrDistrict;
	}

	public void setGuardianAddrDistrict(BigDecimal guardianAddrDistrict) {
		this.guardianAddrDistrict = guardianAddrDistrict;
	}

	public Integer getGuardianAddrPinCode() {
		return guardianAddrPinCode;
	}

	public void setGuardianAddrPinCode(Integer guardianAddrPinCode) {
		this.guardianAddrPinCode = guardianAddrPinCode;
	}

	public String getGuardianRelation() {
		return guardianRelation;
	}

	public void setGuardianRelation(String guardianRelation) {
		this.guardianRelation = guardianRelation;
	}

	public String getGuardianAddr() {
		return guardianAddr;
	}

	public void setGuardianAddr(String guardianAddr) {
		this.guardianAddr = guardianAddr;
	}

	public String getIsAddrSameFlag() {
		return isAddrSameFlag;
	}
	
	public void setIsAddrSameFlag(String isAddrSameFlag) {
		this.isAddrSameFlag = isAddrSameFlag;
	}


}
