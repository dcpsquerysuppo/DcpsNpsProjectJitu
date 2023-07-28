/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 23, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.valueobject;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Mar 23, 2011
 */

import java.util.Date;


public class DcpsCadreMst {

	private Long cadreId;
	private Long fieldDeptId;
	private String groupId;
	private Long cadreCode;
	private String cadreName;
	private String ministerialFlag;
	private Long superAntunAge;
	private String cadreCntrlFlag;
	private Long cntrlFieldDeptId;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedPostId;
	private Long updatedUserId;
	private Date updatedDate;

	public Long getCadreId() {

		return cadreId;
	}

	public void setCadreId(Long cadreId) {

		this.cadreId = cadreId;
	}

	public Long getFieldDeptId() {

		return fieldDeptId;
	}

	public void setFieldDeptId(Long fieldDeptId) {

		this.fieldDeptId = fieldDeptId;
	}

	public String getGroupId() {

		return groupId;
	}

	public void setGroupId(String groupId) {

		this.groupId = groupId;
	}

	public Long getCadreCode() {

		return cadreCode;
	}

	public void setCadreCode(Long cadreCode) {

		this.cadreCode = cadreCode;
	}

	public String getCadreName() {

		return cadreName;
	}

	public void setCadreName(String cadreName) {

		this.cadreName = cadreName;
	}

	public String getMinisterialFlag() {

		return ministerialFlag;
	}

	public void setMinisterialFlag(String ministerialFlag) {

		this.ministerialFlag = ministerialFlag;
	}

	public Long getCntrlFieldDeptId() {

		return cntrlFieldDeptId;
	}

	public void setCntrlFieldDeptId(Long cntrlFieldDeptId) {

		this.cntrlFieldDeptId = cntrlFieldDeptId;
	}
	
	public Long getSuperAntunAge() {

		return superAntunAge;
	}

	public void setSuperAntunAge(Long superAntunAge) {

		this.superAntunAge = superAntunAge;
	}

	public String getCadreCntrlFlag() {

		return cadreCntrlFlag;
	}

	public void setCadreCntrlFlag(String cadreCntrlFlag) {

		this.cadreCntrlFlag = cadreCntrlFlag;
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

}
