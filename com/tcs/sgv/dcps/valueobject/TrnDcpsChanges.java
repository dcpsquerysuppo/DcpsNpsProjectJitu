/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 18, 2011		Vihan Khatri								
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
 * @since JDK 5.0 May 18, 2011
 */
public class TrnDcpsChanges implements java.io.Serializable {

	private Long dcpsChangesIdPk;
	private Long dcpsChangesId;
	private Long dcpsEmpId;
	private String typeOfChanges;
	private String fieldName;
	private String oldValue;
	private String newValue;
	private Long letterNo;
	private Date letterDate;
	private String ddoCode;
	private Long formStatus;
	private String sentBackRemarks;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	public Long getDcpsChangesIdPk() {
		return dcpsChangesIdPk;
	}

	public void setDcpsChangesIdPk(Long dcpsChangesIdPk) {
		this.dcpsChangesIdPk = dcpsChangesIdPk;
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

	public String getTypeOfChanges() {
		return typeOfChanges;
	}

	public void setTypeOfChanges(String typeOfChanges) {
		this.typeOfChanges = typeOfChanges;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public Long getLetterNo() {
		return letterNo;
	}

	public void setLetterNo(Long letterNo) {
		this.letterNo = letterNo;
	}

	public Date getLetterDate() {
		return letterDate;
	}

	public void setLetterDate(Date letterDate) {
		this.letterDate = letterDate;
	}

	public String getDdoCode() {
		return ddoCode;
	}

	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
	}

	public Long getFormStatus() {
		return formStatus;
	}

	public void setFormStatus(Long formStatus) {
		this.formStatus = formStatus;
	}

	public String getSentBackRemarks() {
		return sentBackRemarks;
	}

	public void setSentBackRemarks(String sentBackRemarks) {
		this.sentBackRemarks = sentBackRemarks;
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

}
