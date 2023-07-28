package com.tcs.sgv.pensionpay.valueobject;

// Generated Jun 21, 2007 7:19:53 PM by Hibernate Tools 3.2.0.beta8

import java.math.BigDecimal;
import java.util.Date;

/**
 *  com.tcs.sgv.billproc.cheque.valueobject.RltBillCheque
 *  
 *  This is Java bean for bill cheque prepared relation
 *  
 * 	Date of Creation : 10th July 2007
 * 
 *  Author : Vidhya Mashru 
 *  
 *  Revision History 
 *  =====================
 *  Vidhya M    23-Oct-2007  Made changes for code formatting
 */
public class RltBillCheque implements java.io.Serializable {

	// Fields    

	private long billChequeId;

	private long chequeId;

	private long billNo;

	private long createdUserId;

	private Long createdPostId;

	private Date createdDate;

	private Long updatedUserId;

	private Long updatedPostId;

	private Date updatedDate;

	private Long dbId;

	private BigDecimal partyAmt = BigDecimal.ZERO;

	private String locationCode;
	// Constructors

	/** default constructor */
	public RltBillCheque() {
	}

	/** minimal constructor */
	public RltBillCheque(long billChequeId, long chequeId, long billNo,
			long createdUserId,Long createdPostId,Date createdDate) {
		this.billChequeId = billChequeId;
		this.chequeId = chequeId;
		this.billNo = billNo;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
	}

	/** full constructor */
	public RltBillCheque(long billChequeId, long chequeId, long billNo,
			long createdUserId, Long createdPostId, Date createdDate,
			Long updatedUserId, Long updatedPostId, Date updatedDate,
			Long dbId, BigDecimal partyAmt,String locationCode) {
		this.billChequeId = billChequeId;
		this.chequeId = chequeId;
		this.billNo = billNo;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;		
		this.dbId = dbId;
		this.partyAmt = partyAmt;
		this.locationCode=locationCode;
	}

	// Property accessors
	public long getBillChequeId() {
		return this.billChequeId;
	}

	public void setBillChequeId(long billChequeId) {
		this.billChequeId = billChequeId;
	}

	public long getChequeId() {
		return this.chequeId;
	}

	public void setChequeId(long chequeId) {
		this.chequeId = chequeId;
	}

	public long getBillNo() {
		return this.billNo;
	}

	public void setBillNo(long billNo) {
		this.billNo = billNo;
	}

	public long getCreatedUserId() {
		return this.createdUserId;
	}

	public void setCreatedUserId(long createdUserId) {
		this.createdUserId = createdUserId;
	}

	public Long getCreatedPostId() {
		return this.createdPostId;
	}

	public void setCreatedPostId(Long createdPostId) {
		this.createdPostId = createdPostId;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getUpdatedUserId() {
		return this.updatedUserId;
	}

	public void setUpdatedUserId(Long updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Long getUpdatedPostId() {
		return this.updatedPostId;
	}

	public void setUpdatedPostId(Long updatedPostId) {
		this.updatedPostId = updatedPostId;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	
	public Long getDbId() {
		return this.dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public BigDecimal getPartyAmt() {
		return this.partyAmt;
	}

	public void setPartyAmt(BigDecimal partyAmt) {
		this.partyAmt = partyAmt;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

}
