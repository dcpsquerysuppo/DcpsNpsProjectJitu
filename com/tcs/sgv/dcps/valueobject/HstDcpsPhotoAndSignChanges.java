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

public class HstDcpsPhotoAndSignChanges implements java.io.Serializable {

	private Long dcpsPhotoAndSignChangesId;
	private Long dcpsChangesId;
	private Long dcpsEmpId;
	private Long photoAttachmentID;
	private Long signatureAttachmentID;
	private Long langId;
	private Long locId;
	private Long dbId;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;

	public Long getDcpsOtherChangesId() {
		return dcpsPhotoAndSignChangesId;
	}

	public void setDcpsOtherChangesId(Long dcpsPhotoAndSignChangesId) {
		this.dcpsPhotoAndSignChangesId = dcpsPhotoAndSignChangesId;
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

	public Long getPhotoAttachmentID() {
		return photoAttachmentID;
	}

	public void setPhotoAttachmentID(Long photoAttachmentID) {
		this.photoAttachmentID = photoAttachmentID;
	}

	public Long getSignatureAttachmentID() {
		return signatureAttachmentID;
	}

	public void setSignatureAttachmentID(Long signatureAttachmentID) {
		this.signatureAttachmentID = signatureAttachmentID;
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
