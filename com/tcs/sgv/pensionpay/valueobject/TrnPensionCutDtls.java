/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Oct 7, 2011		519693								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Class Description - 
 *
 *
 * @author 519693
 * @version 0.1
 * @since JDK 5.0
 * Oct 7, 2011
 */
public class TrnPensionCutDtls {
	
	private Long pensionCutDtlsId;
	private Long dbId;
	private Long locationCode;
	private String pensionerCode;
    private BigDecimal amount = BigDecimal.ZERO;
    private Date fromDate;
    private Date toDate;
    private Long createdUserId;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedUserId;
	private Long updatedPostId;
	private Date updatedDate;
	
	public Long getPensionCutDtlsId() {
	
		return pensionCutDtlsId;
	}
	
	public void setPensionCutDtlsId(Long pensionCutDtlsId) {
	
		this.pensionCutDtlsId = pensionCutDtlsId;
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
	
	public String getPensionerCode() {
	
		return pensionerCode;
	}
	
	public void setPensionerCode(String pensionerCode) {
	
		this.pensionerCode = pensionerCode;
	}
	
	public BigDecimal getAmount() {
	
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
	
		this.amount = amount;
	}
	
	public Date getFromDate() {
	
		return fromDate;
	}
	
	public void setFromDate(Date fromDate) {
	
		this.fromDate = fromDate;
	}
	
	public Date getToDate() {
	
		return toDate;
	}
	
	public void setToDate(Date toDate) {
	
		this.toDate = toDate;
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

	
}
