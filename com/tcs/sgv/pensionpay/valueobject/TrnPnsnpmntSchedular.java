/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 15, 2011		Shripal Soni								
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
 * @since JDK 5.0 Feb 15, 2011
 */
public class TrnPnsnpmntSchedular {

	private Long schedularId;
	private Date callDate;
	private Long slotNo;
	private String slotTime;
	private Long auditorPostId;
	private Long pensionerCode;
	private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	private Long dbId;
	private Long locationCode;
	private String scheduleStatus;

	public Long getSchedularId() {

		return schedularId;
	}

	public void setSchedularId(Long schedularId) {

		this.schedularId = schedularId;
	}

	public Date getCallDate() {

		return callDate;
	}

	public void setCallDate(Date callDate) {

		this.callDate = callDate;
	}

	public Long getSlotNo() {

		return slotNo;
	}

	public void setSlotNo(Long slotNo) {

		this.slotNo = slotNo;
	}

	public Long getAuditorPostId() {

		return auditorPostId;
	}

	public void setAuditorPostId(Long auditorPostId) {

		this.auditorPostId = auditorPostId;
	}

	public Long getPensionerCode() {

		return pensionerCode;
	}

	public void setPensionerCode(Long pensionerCode) {

		this.pensionerCode = pensionerCode;
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

	public Date getUpdatedDate() {

		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {

		this.updatedDate = updatedDate;
	}

	public String getSlotTime() {

		return slotTime;
	}

	public void setSlotTime(String slotTime) {

		this.slotTime = slotTime;
	}

	public String getScheduleStatus() {

		return scheduleStatus;
	}

	public void setScheduleStatus(String scheduleStatus) {

		this.scheduleStatus = scheduleStatus;
	}
}
