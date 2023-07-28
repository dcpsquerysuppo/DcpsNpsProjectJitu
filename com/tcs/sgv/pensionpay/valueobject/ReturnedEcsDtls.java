/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 25, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.util.Date;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Aug 25, 2011
 */
public class ReturnedEcsDtls implements java.io.Serializable {

	private long returnedEcsDtlsId;

	private String ppoNo;

	private Long mandateSerialNo;

	private String pensionerName;

	private String reason;

	private long amount;

	private Long createdUserId;

	private Long createdPostId;

	private Date createdDate;

	private Long updatedUserId;

	private Long updatedPostId;

	private Date updatedDate;

	private Long dbId;

	private String locationCode;

	public long getReturnedEcsDtlsId() {

		return returnedEcsDtlsId;
	}

	public void setReturnedEcsDtlsId(long returnedEcsDtlsId) {

		this.returnedEcsDtlsId = returnedEcsDtlsId;
	}

	public String getPpoNo() {

		return ppoNo;
	}

	public void setPpoNo(String ppoNo) {

		this.ppoNo = ppoNo;
	}

	public Long getMandateSerialNo() {

		return mandateSerialNo;
	}

	public void setMandateSerialNo(Long mandateSerialNo) {

		this.mandateSerialNo = mandateSerialNo;
	}

	public String getPensionerName() {

		return pensionerName;
	}

	public void setPensionerName(String pensionerName) {

		this.pensionerName = pensionerName;
	}

	public String getReason() {

		return reason;
	}

	public void setReason(String reason) {

		this.reason = reason;
	}

	public long getAmount() {

		return amount;
	}

	public void setAmount(long amount) {

		this.amount = amount;
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

	public Long getDbId() {

		return dbId;
	}

	public void setDbId(Long dbId) {

		this.dbId = dbId;
	}

	public String getLocationCode() {

		return locationCode;
	}

	public void setLocationCode(String locationCode) {

		this.locationCode = locationCode;
	}

}
