// default package
// Generated Sep 27, 2007 12:20:12 PM by Hibernate Tools 3.2.0.beta8
package com.tcs.sgv.common.valueobject;

import java.util.Date;

/**
 * SgvaGrantSchemeCoDdoMpg generated by hbm2java
 */
public class SgvaGrantSchemeCoDdoMpg implements java.io.Serializable {

	// Fields    

	private long schemeMpgId;

	private String planNonplan;

	private String deptId;

	private String demandCode;

	private String budmjrhdCode;

	private String budsubmjrhdCode;

	private String budminhdCode;

	private String budsubhdCode;

	private String coDdoType;

	private String coDdoCode;

	private Date startDate;

	private Date endDate;

	private String status;

	private Character delFlag;

	private Date crtDt;

	private String crtUsr;

	private String lstUpdUsr;

	private Date lstUpdDt;

	// Constructors

	/** default constructor */
	public SgvaGrantSchemeCoDdoMpg() {
	}

	/** minimal constructor */
	public SgvaGrantSchemeCoDdoMpg(long schemeMpgId, String planNonplan,
			String deptId, String demandCode, String budmjrhdCode,
			String budsubmjrhdCode, String budminhdCode, String budsubhdCode,
			String coDdoType, String coDdoCode, String status, Date crtDt,
			String crtUsr) {
		this.schemeMpgId = schemeMpgId;
		this.planNonplan = planNonplan;
		this.deptId = deptId;
		this.demandCode = demandCode;
		this.budmjrhdCode = budmjrhdCode;
		this.budsubmjrhdCode = budsubmjrhdCode;
		this.budminhdCode = budminhdCode;
		this.budsubhdCode = budsubhdCode;
		this.coDdoType = coDdoType;
		this.coDdoCode = coDdoCode;
		this.status = status;
		this.crtDt = crtDt;
		this.crtUsr = crtUsr;
	}

	/** full constructor */
	public SgvaGrantSchemeCoDdoMpg(long schemeMpgId, String planNonplan,
			String deptId, String demandCode, String budmjrhdCode,
			String budsubmjrhdCode, String budminhdCode, String budsubhdCode,
			String coDdoType, String coDdoCode, Date startDate, Date endDate,
			String status, Character delFlag, Date crtDt, String crtUsr,
			String lstUpdUsr, Date lstUpdDt) {
		this.schemeMpgId = schemeMpgId;
		this.planNonplan = planNonplan;
		this.deptId = deptId;
		this.demandCode = demandCode;
		this.budmjrhdCode = budmjrhdCode;
		this.budsubmjrhdCode = budsubmjrhdCode;
		this.budminhdCode = budminhdCode;
		this.budsubhdCode = budsubhdCode;
		this.coDdoType = coDdoType;
		this.coDdoCode = coDdoCode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.delFlag = delFlag;
		this.crtDt = crtDt;
		this.crtUsr = crtUsr;
		this.lstUpdUsr = lstUpdUsr;
		this.lstUpdDt = lstUpdDt;
	}

	// Property accessors
	public long getSchemeMpgId() {
		return this.schemeMpgId;
	}

	public void setSchemeMpgId(long schemeMpgId) {
		this.schemeMpgId = schemeMpgId;
	}

	public String getPlanNonplan() {
		return this.planNonplan;
	}

	public void setPlanNonplan(String planNonplan) {
		this.planNonplan = planNonplan;
	}

	public String getDeptId() {
		return this.deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDemandCode() {
		return this.demandCode;
	}

	public void setDemandCode(String demandCode) {
		this.demandCode = demandCode;
	}

	public String getBudmjrhdCode() {
		return this.budmjrhdCode;
	}

	public void setBudmjrhdCode(String budmjrhdCode) {
		this.budmjrhdCode = budmjrhdCode;
	}

	public String getBudsubmjrhdCode() {
		return this.budsubmjrhdCode;
	}

	public void setBudsubmjrhdCode(String budsubmjrhdCode) {
		this.budsubmjrhdCode = budsubmjrhdCode;
	}

	public String getBudminhdCode() {
		return this.budminhdCode;
	}

	public void setBudminhdCode(String budminhdCode) {
		this.budminhdCode = budminhdCode;
	}

	public String getBudsubhdCode() {
		return this.budsubhdCode;
	}

	public void setBudsubhdCode(String budsubhdCode) {
		this.budsubhdCode = budsubhdCode;
	}

	public String getCoDdoType() {
		return this.coDdoType;
	}

	public void setCoDdoType(String coDdoType) {
		this.coDdoType = coDdoType;
	}

	public String getCoDdoCode() {
		return this.coDdoCode;
	}

	public void setCoDdoCode(String coDdoCode) {
		this.coDdoCode = coDdoCode;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Character getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(Character delFlag) {
		this.delFlag = delFlag;
	}

	public Date getCrtDt() {
		return this.crtDt;
	}

	public void setCrtDt(Date crtDt) {
		this.crtDt = crtDt;
	}

	public String getCrtUsr() {
		return this.crtUsr;
	}

	public void setCrtUsr(String crtUsr) {
		this.crtUsr = crtUsr;
	}

	public String getLstUpdUsr() {
		return this.lstUpdUsr;
	}

	public void setLstUpdUsr(String lstUpdUsr) {
		this.lstUpdUsr = lstUpdUsr;
	}

	public Date getLstUpdDt() {
		return this.lstUpdDt;
	}

	public void setLstUpdDt(Date lstUpdDt) {
		this.lstUpdDt = lstUpdDt;
	}

}
