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
public class MstPnsnpmntSlots {

	private Integer slotNo;
	private String slotTime;
	private Long createdBy;
	private Long createdByPost;
	private Date createdDate;
	private Long updatedBy;
	private Long updatedByPost;
	private Date updatedDate;

	public Integer getSlotNo() {

		return slotNo;
	}

	public void setSlotNo(Integer slotNo) {

		this.slotNo = slotNo;
	}

	public String getSlotTime() {

		return slotTime;
	}

	public void setSlotTime(String slotTime) {

		this.slotTime = slotTime;
	}

	public Long getCreatedBy() {

		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {

		this.createdBy = createdBy;
	}

	public Long getCreatedByPost() {

		return createdByPost;
	}

	public void setCreatedByPost(Long createdByPost) {

		this.createdByPost = createdByPost;
	}

	public Date getCreatedDate() {

		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {

		this.createdDate = createdDate;
	}

	public Long getUpdatedBy() {

		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {

		this.updatedBy = updatedBy;
	}

	public Long getUpdatedByPost() {

		return updatedByPost;
	}

	public void setUpdatedByPost(Long updatedByPost) {

		this.updatedByPost = updatedByPost;
	}

	public Date getUpdatedDate() {

		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {

		this.updatedDate = updatedDate;
	}

}
