package com.tcs.sgv.pensionpay.valueobject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jun 01, 2012
 */

public class HstTrnPensionSixpayfpArrear 
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
	
	private Long revision;


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
	
	public Long getRevision() {
		return revision;
	}

	public void setRevision(Long revision) {
		this.revision = revision;
	}
}
