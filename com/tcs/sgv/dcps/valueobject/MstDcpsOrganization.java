/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Bhargav Trivedi								
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
 * @since JDK 5.0
 * Apr 7, 2011
 */
public class MstDcpsOrganization 
{
	private Long orgId ;
	private Long dbId;
	private Long locationCode;
	private Long langId;
	private String orgType;
	private String orgDesc;
	private String empHeadAcc;
	private String empSchemeCode;
	private String emplrHeadAcc;
	private String emplrSchemeCode;
	private String deptEmpHeadAcc;
	private String deptEmpSchemeCode;
	private String deptEmplrHeadAcc;
	private String deptEmplrSchemeCode;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	public MstDcpsOrganization()
	{}

	public MstDcpsOrganization(Long orgId, Long dbId, Long locationCode,
			Long langId, String orgType, String orgDesc, String empHeadAcc,
			String empSchemeCode, String emplrHeadAcc, String emplrSchemeCode,
			String deptEmpHeadAcc, String deptEmpSchemeCode,
			String deptEmplrHeadAcc, String deptEmplrSchemeCode,
			Long createdUserId, Long createdPostId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate) {
		super();
		this.orgId = orgId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.langId = langId;
		this.orgType = orgType;
		this.orgDesc = orgDesc;
		this.empHeadAcc = empHeadAcc;
		this.empSchemeCode = empSchemeCode;
		this.emplrHeadAcc = emplrHeadAcc;
		this.emplrSchemeCode = emplrSchemeCode;
		this.deptEmpHeadAcc = deptEmpHeadAcc;
		this.deptEmpSchemeCode = deptEmpSchemeCode;
		this.deptEmplrHeadAcc = deptEmplrHeadAcc;
		this.deptEmplrSchemeCode = deptEmplrSchemeCode;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
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

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getOrgDesc() {
		return orgDesc;
	}

	public void setOrgDesc(String orgDesc) {
		this.orgDesc = orgDesc;
	}

	public String getEmpHeadAcc() {
		return empHeadAcc;
	}

	public void setEmpHeadAcc(String empHeadAcc) {
		this.empHeadAcc = empHeadAcc;
	}

	public String getEmpSchemeCode() {
		return empSchemeCode;
	}

	public void setEmpSchemeCode(String empSchemeCode) {
		this.empSchemeCode = empSchemeCode;
	}

	public String getEmplrHeadAcc() {
		return emplrHeadAcc;
	}

	public void setEmplrHeadAcc(String emplrHeadAcc) {
		this.emplrHeadAcc = emplrHeadAcc;
	}

	public String getEmplrSchemeCode() {
		return emplrSchemeCode;
	}

	public void setEmplrSchemeCode(String emplrSchemeCode) {
		this.emplrSchemeCode = emplrSchemeCode;
	}

	public String getDeptEmpHeadAcc() {
		return deptEmpHeadAcc;
	}

	public void setDeptEmpHeadAcc(String deptEmpHeadAcc) {
		this.deptEmpHeadAcc = deptEmpHeadAcc;
	}

	public String getDeptEmpSchemeCode() {
		return deptEmpSchemeCode;
	}

	public void setDeptEmpSchemeCode(String deptEmpSchemeCode) {
		this.deptEmpSchemeCode = deptEmpSchemeCode;
	}

	public String getDeptEmplrHeadAcc() {
		return deptEmplrHeadAcc;
	}

	public void setDeptEmplrHeadAcc(String deptEmplrHeadAcc) {
		this.deptEmplrHeadAcc = deptEmplrHeadAcc;
	}

	public String getDeptEmplrSchemeCode() {
		return deptEmplrSchemeCode;
	}

	public void setDeptEmplrSchemeCode(String deptEmplrSchemeCode) {
		this.deptEmplrSchemeCode = deptEmplrSchemeCode;
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
