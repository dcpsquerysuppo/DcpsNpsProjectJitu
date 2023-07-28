package com.tcs.sgv.pensionproc.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 
 * @author Anjana Suvariya
 * 
 */

public class TrnPnsnprocFamilydtls implements Serializable {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 3204349330873277866L;

	private Long pnsnrFamilyId;
	private Long dbId;
	private Long locationCode;
	private Long pensionerDtlId;
	private Long inwardPensionId;
	private String name;
	private Date birthDate;
	private Integer age;
	private String addrSameFlag;
	private Long state;
	private Long district;
	private String area;
	private String road;
	private String flateNo;
	private Integer pincode;
	private String landLineNo;
	private String mobileNo;
	private String emailId;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private String relation;
	private Date deathDate;
	private String minorFlag;
	private BigDecimal salary = BigDecimal.ZERO;
	private String handicapeFlag;
	private String guardianName;
	private Long percentage;
	private String marriedFlag;
	
	public TrnPnsnprocFamilydtls() {

	}

	/**
	 * 
	 * @param pnsnrFamilyId
	 * @param dbId
	 * @param locationCode
	 * @param pensionerDtlId
	 * @param inwardPensionId
	 * @param name
	 * @param birthDate
	 * @param age
	 * @param addrSameFlag
	 * @param state
	 * @param district
	 * @param area
	 * @param road
	 * @param flateNo
	 * @param pincode
	 * @param landLineNo
	 * @param mobileNo
	 * @param emailId
	 * @param createdUserId
	 * @param createdPostId
	 * @param createdDate
	 * @param updatedUserId
	 * @param updatedPostId
	 * @param updatedDate
	 * @param relation
	 * @param deathDate
	 * @param minorFlag
	 * @param salary
	 * @param handicapeFlag
	 * @param guardianName
	 * @param percentage
	 * @param marriedFlag
	 */

	public TrnPnsnprocFamilydtls(Long pnsnrFamilyId, Long dbId,
			Long locationCode, Long pensionerDtlId,
			Long inwardPensionId, String name,
			Date birthDate, Integer age, String addrSameFlag, Long state,
			Long district, String area, String road, String flateNo,
			Integer pincode, String landLineNo, String mobileNo,
			String emailId, Long createdUserId, Long createdPostId,
			Date createdDate, Long updatedUserId, Long updatedPostId,
			Date updatedDate, String relation, Date deathDate,
			String minorFlag, BigDecimal salary, String handicapeFlag,
			String guardianName, Long percentage, String marriedFlag) {
		super();
		this.pnsnrFamilyId = pnsnrFamilyId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pensionerDtlId = pensionerDtlId;
		this.inwardPensionId = inwardPensionId;
		this.name = name;
		this.birthDate = birthDate;
		this.age = age;
		this.addrSameFlag = addrSameFlag;
		this.state = state;
		this.district = district;
		this.area = area;
		this.road = road;
		this.flateNo = flateNo;
		this.pincode = pincode;
		this.landLineNo = landLineNo;
		this.mobileNo = mobileNo;
		this.emailId = emailId;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.relation = relation;
		this.deathDate = deathDate;
		this.minorFlag = minorFlag;
		this.salary = salary;
		this.handicapeFlag = handicapeFlag;
		this.guardianName = guardianName;
		this.percentage = percentage;
		this.marriedFlag = marriedFlag;
	}



	public Long getPnsnrFamilyId() {

		return pnsnrFamilyId;
	}

	public void setPnsnrFamilyId(Long pnsnrFamilyId) {

		this.pnsnrFamilyId = pnsnrFamilyId;
	}

	public Long getDbId() {

		return dbId;
	}

	public void setDbId(Long dbId) {

		this.dbId = dbId;
	}

	public Long getLocationCode() {

		return locationCode;
	}

	public void setLocationCode(Long locationCode) {

		this.locationCode = locationCode;
	}

	public Long getPensionerDtlId() {
		return pensionerDtlId;
	}

	public void setPensionerDtlId(Long pensionerDtlId) {
		this.pensionerDtlId = pensionerDtlId;
	}

	public Long getInwardPensionId() {
		return inwardPensionId;
	}

	public void setInwardPensionId(Long inwardPensionId) {
		this.inwardPensionId = inwardPensionId;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public Date getBirthDate() {

		return birthDate;
	}

	public void setBirthDate(Date birthDate) {

		this.birthDate = birthDate;
	}

	public Integer getAge() {

		return age;
	}

	public void setAge(Integer age) {

		this.age = age;
	}

	public String getAddrSameFlag() {

		return addrSameFlag;
	}

	public void setAddrSameFlag(String addrSameFlag) {

		this.addrSameFlag = addrSameFlag;
	}

	public Long getState() {

		return state;
	}

	public void setState(Long state) {

		this.state = state;
	}

	public Long getDistrict() {

		return district;
	}

	public void setDistrict(Long district) {

		this.district = district;
	}

	public String getArea() {

		return area;
	}

	public void setArea(String area) {

		this.area = area;
	}

	public String getRoad() {

		return road;
	}

	public void setRoad(String road) {

		this.road = road;
	}

	public String getFlateNo() {

		return flateNo;
	}

	public void setFlateNo(String flateNo) {

		this.flateNo = flateNo;
	}

	public Integer getPincode() {

		return pincode;
	}

	public void setPincode(Integer pincode) {

		this.pincode = pincode;
	}

	public String getLandLineNo() {

		return landLineNo;
	}

	public void setLandLineNo(String landLineNo) {

		this.landLineNo = landLineNo;
	}

	public String getMobileNo() {

		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {

		this.mobileNo = mobileNo;
	}

	public String getEmailId() {

		return emailId;
	}

	public void setEmailId(String emailId) {

		this.emailId = emailId;
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

	public String getRelation() {

		return relation;
	}

	public void setRelation(String relation) {

		this.relation = relation;
	}

	public Date getDeathDate() {

		return deathDate;
	}

	public void setDeathDate(Date deathDate) {

		this.deathDate = deathDate;
	}

	
	public String getMinorFlag() {
		return minorFlag;
	}

	public void setMinorFlag(String minorFlag) {
		this.minorFlag = minorFlag;
	}

	public BigDecimal getSalary() {

		return salary;
	}

	public void setSalary(BigDecimal salary) {

		this.salary = salary;
	}

	public String getHandicapeFlag() {

		return handicapeFlag;
	}

	public void setHandicapeFlag(String handicapeFlag) {

		this.handicapeFlag = handicapeFlag;
	}

	public String getGuardianName() {

		return guardianName;
	}

	public void setGuardianName(String guardianName) {

		this.guardianName = guardianName;
	}

	public Long getPercentage() {

		return percentage;
	}

	public void setPercentage(Long percentage) {

		this.percentage = percentage;
	}

	public String getMarriedFlag() {

		return marriedFlag;
	}

	public void setMarriedFlag(String marriedFlag) {

		this.marriedFlag = marriedFlag;
	}

	
}
