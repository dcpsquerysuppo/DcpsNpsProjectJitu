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

public class HstDcpsOfficeChanges implements java.io.Serializable, Cloneable {

	private Long dcpsOfficeChangesId;
	private Long dcpsChangesId;
	private Long dcpsEmpId;
	private String parentDept;
	private String cadre;
	private String group;
	private String designation;
	private String payCommission;
	private String payScale;
	private Long reasonForPSChange;
	private Date withEffectFromDate;
	private String otherReasonForPSChange;
	private Double basicPay;
	private Long postId;
	private String firstDesignation;
	private Date appointmentDate;
	private Date currPostJoiningDate;
	private Date currCadreJoiningDate;
	private String currOff;
	private String remarks;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	public Long getDcpsOfficeChangesId() {
		return dcpsOfficeChangesId;
	}

	public void setDcpsOfficeChangesId(Long dcpsOfficeChangesId) {
		this.dcpsOfficeChangesId = dcpsOfficeChangesId;
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
	
	public String getParentDept() {
		return parentDept;
	}

	public void setParentDept(String parentDept) {
		this.parentDept = parentDept;
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

	public Double getBasicPay() {

		return basicPay;
	}

	public void setBasicPay(Double basicPay) {

		this.basicPay = basicPay;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getFirstDesignation() {
		return firstDesignation;
	}

	public void setFirstDesignation(String firstDesignation) {
		this.firstDesignation = firstDesignation;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
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

	public String getCurrOff() {
		return currOff;
	}

	public void setCurrOff(String currOff) {
		this.currOff = currOff;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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