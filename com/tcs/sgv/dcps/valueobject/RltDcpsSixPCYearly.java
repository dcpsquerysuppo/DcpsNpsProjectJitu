/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 17, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Mar 17, 2011
 */
public class RltDcpsSixPCYearly implements java.io.Serializable {

	private Long dcpsSixPCYearlyId;
	private Long dcpsEmpId;
	private Long yearlyAmount;
	private Long finYearId;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Character statusFlag;
	private Integer activeFlag;
	private String remarks;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Long scheduleId;
	private String ddoCode;
	
	private Character statusOldSevaarth;
	private Long voucherNo;
	private Date voucherDate;
	private Long treasury;
	

	public RltDcpsSixPCYearly() {
	}

	public RltDcpsSixPCYearly(Long dcpsSixPCYearlyId, Long dcpsEmpId,
			Long yearlyAmount, Long finYearId, Long langId, Long locId,
			Long dbId, Character statusFlag,Integer activeFlag, String remarks,
			Long createdPostId, Long createdUserId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate,Long scheduleId,String ddoCode,Character statusOldSevaarth) {
		super();
		this.dcpsSixPCYearlyId = dcpsSixPCYearlyId;
		this.dcpsEmpId = dcpsEmpId;
		this.yearlyAmount = yearlyAmount;
		this.finYearId = finYearId;
		this.langId = langId;
		this.locId = locId;
		this.dbId = dbId;
		this.statusFlag = statusFlag;
		this.activeFlag = activeFlag;
		this.remarks = remarks;
		this.createdPostId = createdPostId;
		this.createdUserId = createdUserId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.scheduleId= scheduleId;
		this.ddoCode= ddoCode;
		this.statusOldSevaarth = statusOldSevaarth;
	}

	public Long getDcpsSixPCYearlyId() {
		return dcpsSixPCYearlyId;
	}

	public void setDcpsSixPCYearlyId(Long dcpsSixPCYearlyId) {
		this.dcpsSixPCYearlyId = dcpsSixPCYearlyId;
	}

	public Long getDcpsEmpId() {
		return dcpsEmpId;
	}

	public void setDcpsEmpId(Long dcpsEmpId) {
		this.dcpsEmpId = dcpsEmpId;
	}

	public Long getYearlyAmount() {
		return yearlyAmount;
	}

	public void setYearlyAmount(Long yearlyAmount) {
		this.yearlyAmount = yearlyAmount;
	}

	public Long getFinYearId() {
		return finYearId;
	}

	public void setFinYearId(Long finYearId) {
		this.finYearId = finYearId;
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

	public Character getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(Character statusFlag) {
		this.statusFlag = statusFlag;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getDdoCode() {
		return ddoCode;
	}

	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
	}
	
	
	
	
	public Character getStatusOldSevaarth() {
		return statusOldSevaarth;
	}
	public void setStatusOldSevaarth(Character statusOldSevaarth) {
		this.statusOldSevaarth = statusOldSevaarth;
	}
	public Long getVoucherNo() {
		return voucherNo;
	}
	public void setVoucherNo(Long voucherNo) {
		this.voucherNo = voucherNo;
	}
	public Date getVoucherDate() {
		return voucherDate;
	}
	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}

	public Long getTreasury() {
		return treasury;
	}

	public void setTreasury(Long treasury) {
		this.treasury = treasury;
	}

		
}
