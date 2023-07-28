package com.tcs.sgv.dcps.valueobject;

import java.math.BigDecimal;
import java.util.Date;

public class MstDcpsTierICntrnbtn {

	private Long tierICntrnbtnId;
	private Long dbId;
	private Long locationCode;
	private Long employeeId;
	private Long employeePensionId;
	private String employeeName;
	private Character statusFlag;
	private String remarks; 
	private Long arrearstypeLookupid;
	private Long parentDepartmentId;
	private Long designationId;
	private Date tierICntrnbtnFromDate;
	private Date tierICntrnbtnToDate;
	private BigDecimal totalAmount = BigDecimal.ZERO;
	private Long noOfInstlmnt;
	private BigDecimal wholeMonthInstlmntAmount = BigDecimal.ZERO;
	private BigDecimal firstOddInstlmntAmount = BigDecimal.ZERO;
	private BigDecimal lastOddInstlmntAmount = BigDecimal.ZERO;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	
	public MstDcpsTierICntrnbtn()
	{}
	
	
	
	
	public MstDcpsTierICntrnbtn(Long tierICntrnbtnId, Long dbId,
			Long locationCode, Long employeeId, Long employeePensionId,
			String employeeName, Character statusFlag, String remarks,
			Long arrearstypeLookupid, Long parentDepartmentId,
			Long designationId, Date tierICntrnbtnFromDate,
			Date tierICntrnbtnToDate, BigDecimal totalAmount,
			Long noOfInstlmnt, BigDecimal wholeMonthInstlmntAmount,
			BigDecimal firstOddInstlmntAmount,
			BigDecimal lastOddInstlmntAmount, Long createdUserId,
			Long createdPostId, Date createdDate, Long updatedUserId,
			Long updatedPostId, Date updatedDate) {
		super();
		this.tierICntrnbtnId = tierICntrnbtnId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.employeeId = employeeId;
		this.employeePensionId = employeePensionId;
		this.employeeName = employeeName;
		this.statusFlag = statusFlag;
		this.remarks = remarks;
		this.arrearstypeLookupid = arrearstypeLookupid;
		this.parentDepartmentId = parentDepartmentId;
		this.designationId = designationId;
		this.tierICntrnbtnFromDate = tierICntrnbtnFromDate;
		this.tierICntrnbtnToDate = tierICntrnbtnToDate;
		this.totalAmount = totalAmount;
		this.noOfInstlmnt = noOfInstlmnt;
		this.wholeMonthInstlmntAmount = wholeMonthInstlmntAmount;
		this.firstOddInstlmntAmount = firstOddInstlmntAmount;
		this.lastOddInstlmntAmount = lastOddInstlmntAmount;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}




	public Long getTierICntrnbtnId() {
		return tierICntrnbtnId;
	}

	public void setTierICntrnbtnId(Long tierICntrnbtnId) {
		this.tierICntrnbtnId = tierICntrnbtnId;
	}

	public Long getDbId() {
		return dbId;
	}

	public Character getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(Character statusFlag) {
		this.statusFlag = statusFlag;
	}
	
	

	public String getRemarks() {
		return remarks;
	}




	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getEmployeePensionId() {
		return employeePensionId;
	}

	public void setEmployeePensionId(Long employeePensionId) {
		this.employeePensionId = employeePensionId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Long getArrearstypeLookupid() {
		return arrearstypeLookupid;
	}

	public void setArrearstypeLookupid(Long arrearstypeLookupid) {
		this.arrearstypeLookupid = arrearstypeLookupid;
	}

	public Long getParentDepartmentId() {
		return parentDepartmentId;
	}

	public void setParentDepartmentId(Long parentDepartmentId) {
		this.parentDepartmentId = parentDepartmentId;
	}

	public Long getDesignationId() {
		return designationId;
	}

	public void setDesignationId(Long designationId) {
		this.designationId = designationId;
	}

	public Date getTierICntrnbtnFromDate() {
		return tierICntrnbtnFromDate;
	}

	public void setTierICntrnbtnFromDate(Date tierICntrnbtnFromDate) {
		this.tierICntrnbtnFromDate = tierICntrnbtnFromDate;
	}

	public Date getTierICntrnbtnToDate() {
		return tierICntrnbtnToDate;
	}

	public void setTierICntrnbtnToDate(Date tierICntrnbtnToDate) {
		this.tierICntrnbtnToDate = tierICntrnbtnToDate;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getNoOfInstlmnt() {
		return noOfInstlmnt;
	}

	public void setNoOfInstlmnt(Long noOfInstlmnt) {
		this.noOfInstlmnt = noOfInstlmnt;
	}

	public BigDecimal getWholeMonthInstlmntAmount() {
		return wholeMonthInstlmntAmount;
	}

	public void setWholeMonthInstlmntAmount(BigDecimal wholeMonthInstlmntAmount) {
		this.wholeMonthInstlmntAmount = wholeMonthInstlmntAmount;
	}

	public BigDecimal getFirstOddInstlmntAmount() {
		return firstOddInstlmntAmount;
	}

	public void setFirstOddInstlmntAmount(BigDecimal firstOddInstlmntAmount) {
		this.firstOddInstlmntAmount = firstOddInstlmntAmount;
	}

	public BigDecimal getLastOddInstlmntAmount() {
		return lastOddInstlmntAmount;
	}

	public void setLastOddInstlmntAmount(BigDecimal lastOddInstlmntAmount) {
		this.lastOddInstlmntAmount = lastOddInstlmntAmount;
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

}
