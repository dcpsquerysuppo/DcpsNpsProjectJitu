/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 13, 2011		Bhargav Trivedi								
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
 * Jun 13, 2011
 */
public class TrnPensionSixpayfpArrear 
{
	private Long arrearId;	
 
	private String pensionerCode;
 
	private Character arrearType;
	
	private Integer installmentNo;
	
	private String payInMonth;
	
	private BigDecimal installmentAmnt;
	
	private BigDecimal diffAmnt;
	
	private Character paidFlag;
	
	private Character activeFlag;
	
	private Character revisionCounter;
	
	private BigDecimal createdUserId;

	private BigDecimal createdPostId;

	private Date createdDate;

	private BigDecimal updatedUserId;

	private BigDecimal updatedPostId;

	private Date updatedDate;
	
	private String remarks;
	
	private Long billNo;
	
	private Character configFromSuppl;
	
	private Long pensionSupplyBillId;
	
	public TrnPensionSixpayfpArrear()
	{
		
	}



	public TrnPensionSixpayfpArrear(Long arrearId, String pensionerCode,
			Character arrearType, Integer installmentNo, String payInMonth,
			BigDecimal installmentAmnt,BigDecimal diffAmnt, Character paidFlag,
			Character activeFlag, Character revisionCounter,
			BigDecimal createdUserId, BigDecimal createdPostId,
			Date createdDate, BigDecimal updatedUserId,
			BigDecimal updatedPostId, Date updatedDate,String remarks,Long billNo,Character configFromSuppl,Long pensionSupplyBillId) 
	{
		super();
		this.arrearId = arrearId;
		this.pensionerCode = pensionerCode;
		this.arrearType = arrearType;
		this.installmentNo = installmentNo;
		this.payInMonth = payInMonth;
		this.installmentAmnt = installmentAmnt;
		this.diffAmnt = diffAmnt;
		this.paidFlag = paidFlag;
		this.activeFlag = activeFlag;
		this.revisionCounter = revisionCounter;
		this.createdUserId = createdUserId;
		this.createdPostId = createdPostId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedPostId = updatedPostId;
		this.updatedDate = updatedDate;
		this.remarks = remarks;
		this.billNo = billNo;
		this.configFromSuppl = configFromSuppl;
		this.pensionSupplyBillId = pensionSupplyBillId;
	}

	public TrnPensionSixpayfpArrear(Integer installmentNo,BigDecimal installmentAmnt,BigDecimal diffAmnt){
		super();
		this.installmentNo = installmentNo;
		this.installmentAmnt = installmentAmnt;
		this.diffAmnt = diffAmnt;
	}

	public Long getArrearId() {
		return arrearId;
	}



	public void setArrearId(Long arrearId) {
		this.arrearId = arrearId;
	}



	public String getPensionerCode() {
		return pensionerCode;
	}



	public void setPensionerCode(String pensionerCode) {
		this.pensionerCode = pensionerCode;
	}



	public Character getArrearType() {
		return arrearType;
	}



	public void setArrearType(Character arrearType) {
		this.arrearType = arrearType;
	}



	public Integer getInstallmentNo() {
		return installmentNo;
	}



	public void setInstallmentNo(Integer installmentNo) {
		this.installmentNo = installmentNo;
	}



	public String getPayInMonth() {
		return payInMonth;
	}


	public void setPayInMonth(String payInMonth) {
		this.payInMonth = payInMonth;
	}


	public BigDecimal getInstallmentAmnt() {
		return installmentAmnt;
	}


	public void setInstallmentAmnt(BigDecimal installmentAmnt) {
		this.installmentAmnt = installmentAmnt;
	}

	public BigDecimal getDiffAmnt() {
	
		return diffAmnt;
	}

	public void setDiffAmnt(BigDecimal diffAmnt) {
	
		this.diffAmnt = diffAmnt;
	}

	public Character getPaidFlag() {
		return paidFlag;
	}



	public void setPaidFlag(Character paidFlag) {
		this.paidFlag = paidFlag;
	}



	public Character getActiveFlag() {
		return activeFlag;
	}



	public void setActiveFlag(Character activeFlag) {
		this.activeFlag = activeFlag;
	}



	public Character getRevisionCounter() {
		return revisionCounter;
	}



	public void setRevisionCounter(Character revisionCounter) {
		this.revisionCounter = revisionCounter;
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



	public String getRemarks() {
		return remarks;
	}



	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}



	public Long getBillNo() {
		return billNo;
	}



	public void setBillNo(Long billNo) {
		this.billNo = billNo;
	}



	public Character getConfigFromSuppl() {
		return configFromSuppl;
	}



	public void setConfigFromSuppl(Character configFromSuppl) {
		this.configFromSuppl = configFromSuppl;
	}



	public Long getPensionSupplyBillId() {
		return pensionSupplyBillId;
	}



	public void setPensionSupplyBillId(Long pensionSupplyBillId) {
		this.pensionSupplyBillId = pensionSupplyBillId;
	}
	
	
	
	
}
