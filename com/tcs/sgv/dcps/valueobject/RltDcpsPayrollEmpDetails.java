/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 6, 2011		Vihan Khatri								
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
 * @since JDK 5.0 May 6, 2011
 */
public class RltDcpsPayrollEmpDetails implements java.io.Serializable,
		Cloneable {

	private Long dcpsPayrollEmpId;
	private Long dcpsEmpId;
	private Date currPostJoiningDate;
	private Date currCadreJoiningDate;
	private String acMaintainedBy;
	private String pfSeries;
	private String pfSeriesDesc;
	private String phychallanged;
	private Long postId;
	private String pfAcNo;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedPostId;
	private Long updatedUserId;
	private Date updatedDate;

	public Long getDcpsPayrollEmpId() {
		return dcpsPayrollEmpId;
	}

	public void setDcpsPayrollEmpId(Long dcpsPayrollEmpId) {
		this.dcpsPayrollEmpId = dcpsPayrollEmpId;
	}

	public Long getDcpsEmpId() {

		return dcpsEmpId;
	}

	public void setDcpsEmpId(Long dcpsEmpId) {

		this.dcpsEmpId = dcpsEmpId;
	}

	public Date getCurrPostJoiningDate() {
		return currPostJoiningDate;
	}

	public void setCurrPostJoiningDate(Date currPostJoiningDate) {
		this.currPostJoiningDate = currPostJoiningDate;
	}

	public Date getCurrCadreJoiningDate() {
		return currCadreJoiningDate;
	}

	public void setCurrCadreJoiningDate(Date currCadreJoiningDate) {
		this.currCadreJoiningDate = currCadreJoiningDate;
	}

	public String getAcMaintainedBy() {
		return acMaintainedBy;
	}

	public void setAcMaintainedBy(String acMaintainedBy) {
		this.acMaintainedBy = acMaintainedBy;
	}

	public String getPfSeries() {
		return pfSeries;
	}

	public void setPfSeries(String pfSeries) {
		this.pfSeries = pfSeries;
	}

	public String getPfSeriesDesc() {
		return pfSeriesDesc;
	}

	public void setPfSeriesDesc(String pfSeriesDesc) {
		this.pfSeriesDesc = pfSeriesDesc;
	}

	public String getPfAcNo() {
		return pfAcNo;
	}

	public void setPfAcNo(String pfAcNo) {
		this.pfAcNo = pfAcNo;
	}

	public String getPhychallanged() {
		return phychallanged;
	}

	public void setPhychallanged(String phychallanged) {
		this.phychallanged = phychallanged;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
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

	public Long getUpdatedPostId() {
		return updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Long getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		this.updatedUserId = updatedUserId;
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
