package com.tcs.sgv.pensionproc.valueobject;

import java.util.Date;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 
 * @author Anjana Suvariya
 * 
 */
public class TrnPnsnprocNomineedtls implements Serializable {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = -2666656013736630262L;

	private Long pnsnrNomineeId;
	private Long dbId;
	private Long locationCode;
	private Long pensionerDtlId;
	private Long inwardPensionId;
	private String name;
	private String relation;
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
	private BigDecimal amtOfSharePercn = BigDecimal.ZERO;
	private String remarks;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private BigDecimal percentage;
	private String bankCode;
	private String branchCode;
	private String accountNo;

	public TrnPnsnprocNomineedtls() {

	}

	public TrnPnsnprocNomineedtls(Long pnsnrNomineeId, Long dbId, Long locationCode, Long pensionerDtlId, Long inwardPensionId, String name,
			String relation, Date birthDate, Integer age, String addrSameFlag, Long state, Long district, String area, String road, String flateNo, Integer pincode, String landLineNo,
			String mobileNo, String emailId, BigDecimal amtOfSharePercn, String remarks, Long createdUserId, Long createdPostId, Date createdDate, Long updatedUserId, Long updatedPostId,
			Date updatedDate, BigDecimal percentage, String bankCode, String branchCode, String accountNo) {

		super();
		this.pnsnrNomineeId = pnsnrNomineeId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pensionerDtlId = pensionerDtlId;
		this.inwardPensionId = inwardPensionId;
		this.name = name;
		this.relation = relation;
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
		this.amtOfSharePercn = amtOfSharePercn;
		this.remarks = remarks;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.percentage = percentage;
		this.bankCode = bankCode;
		this.branchCode = branchCode;
		this.accountNo = accountNo;
	}

	public Long getPnsnrNomineeId() {

		return pnsnrNomineeId;
	}

	public void setPnsnrNomineeId(Long pnsnrNomineeId) {

		this.pnsnrNomineeId = pnsnrNomineeId;
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

	public String getRelation() {

		return relation;
	}

	public void setRelation(String relation) {

		this.relation = relation;
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

	public BigDecimal getAmtOfSharePercn() {

		return amtOfSharePercn;
	}

	public void setAmtOfSharePercn(BigDecimal amtOfSharePercn) {

		this.amtOfSharePercn = amtOfSharePercn;
	}

	public String getRemarks() {

		return remarks;
	}

	public void setRemarks(String remarks) {

		this.remarks = remarks;
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

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
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

}
