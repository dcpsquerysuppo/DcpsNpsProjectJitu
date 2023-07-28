/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 17, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Aug 17, 2011
 */
public class HstPnsnPmntPpoNo 
{
	private Long hstpnsnpmntId;

	private BigDecimal dbId;

	private Long locationCode;
	
	private String pensionerCode;

	private String oldppoNo;

	private String newppoNo;
	
	private String remarks;
	
	private BigDecimal createdUserId;

	private BigDecimal createdPostId;

	private Date createdDate;

	private BigDecimal updatedUserId;

	private BigDecimal updatedPostId;

	private Date updatedDate;

	public HstPnsnPmntPpoNo()
	{
		
	}
	
	public HstPnsnPmntPpoNo(Long hstpnsnpmntId, BigDecimal dbId,
			Long locationCode, String pensionerCode, String oldppoNo,
			String newppoNo, String remarks, BigDecimal createdUserId,
			BigDecimal createdPostId, Date createdDate,
			BigDecimal updatedUserId, BigDecimal updatedPostId, Date updatedDate) {
		super();
		this.hstpnsnpmntId = hstpnsnpmntId;
		this.dbId = dbId;
		this.locationCode = locationCode;
		this.pensionerCode = pensionerCode;
		this.oldppoNo = oldppoNo;
		this.newppoNo = newppoNo;
		this.remarks = remarks;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
	}

	public Long getHstpnsnpmntId() {
		return hstpnsnpmntId;
	}

	public void setHstpnsnpmntId(Long hstpnsnpmntId) {
		this.hstpnsnpmntId = hstpnsnpmntId;
	}

	public BigDecimal getDbId() {
		return dbId;
	}

	public void setDbId(BigDecimal dbId) {
		this.dbId = dbId;
	}

	public Long getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(Long locationCode) {
		this.locationCode = locationCode;
	}

	public String getPensionerCode() {
		return pensionerCode;
	}

	public void setPensionerCode(String pensionerCode) {
		this.pensionerCode = pensionerCode;
	}

	public String getOldppoNo() {
		return oldppoNo;
	}

	public void setOldppoNo(String oldppoNo) {
		this.oldppoNo = oldppoNo;
	}

	public String getNewppoNo() {
		return newppoNo;
	}

	public void setNewppoNo(String newppoNo) {
		this.newppoNo = newppoNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(BigDecimal createdUserId) {
		this.createdUserId = createdUserId;
	}

	public BigDecimal getCreatedPostId() {
		return createdPostId;
	}

	public void setCreatedPostId(BigDecimal createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public BigDecimal getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(BigDecimal updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public BigDecimal getUpdatedPostId() {
		return updatedPostId;
	}

	public void setUpdatedPostId(BigDecimal updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	
	
	
}
