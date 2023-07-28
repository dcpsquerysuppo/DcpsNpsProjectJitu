/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Nov 1, 2011		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Nov 1, 2011
 */
public class MstBankGrp {

	private Long bankGrpId;
	private String groupName;
	private Long auditorPostId;
	private String locationCode;
	private Date createdDate;
	private Long createdPostId;
	private Long createdUserId;
	private Long updatedDate;
	private Long updatedPostId;
	private Long updatedUserId;
	private Long dbId;

	public MstBankGrp() {

	}

	public Long getBankGrpId() {

		return bankGrpId;
	}

	public void setBankGrpId(Long bankGrpId) {

		this.bankGrpId = bankGrpId;
	}

	public String getGroupName() {

		return groupName;
	}

	public void setGroupName(String groupName) {

		this.groupName = groupName;
	}

	public Long getAuditorPostId() {

		return auditorPostId;
	}

	public void setAuditorPostId(Long auditorPostId) {

		this.auditorPostId = auditorPostId;
	}

	public String getLocationCode() {

		return locationCode;
	}

	public void setLocationCode(String locationCode) {

		this.locationCode = locationCode;
	}

	public Date getCreatedDate() {

		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {

		this.createdDate = createdDate;
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

	public Long getUpdatedDate() {

		return updatedDate;
	}

	public void setUpdatedDate(Long updatedDate) {

		this.updatedDate = updatedDate;
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

	public Long getDbId() {

		return dbId;
	}

	public void setDbId(Long dbId) {

		this.dbId = dbId;
	}
}
