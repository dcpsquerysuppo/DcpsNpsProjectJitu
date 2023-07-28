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
 * May 22, 2012
 */

public class HstRltPensionHeadcodeRate {
	
	private Long pensionHeadcodeRateId;

	private BigDecimal headCode;

	private String fieldType;

	private Date effectiveFromDate;

	private Date effectiveToDate;

	private BigDecimal rate;

	private BigDecimal minAmount;

	private BigDecimal uptoBasic;

	private BigDecimal createdUserId;

	private BigDecimal createdPostId;

	private Date createdDate;

	private BigDecimal updatedUserId;

	private BigDecimal updatedPostId;

	private Date updatedDate;

	private String grNo;

	private Date grDate;
	
	private BigDecimal revision;

	public Long getPensionHeadcodeRateId() {
		return pensionHeadcodeRateId;
	}

	public void setPensionHeadcodeRateId(Long pensionHeadcodeRateId) {
		this.pensionHeadcodeRateId = pensionHeadcodeRateId;
	}

	public BigDecimal getHeadCode() {
		return headCode;
	}

	public void setHeadCode(BigDecimal headCode) {
		this.headCode = headCode;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public Date getEffectiveFromDate() {
		return effectiveFromDate;
	}

	public void setEffectiveFromDate(Date effectiveFromDate) {
		this.effectiveFromDate = effectiveFromDate;
	}

	public Date getEffectiveToDate() {
		return effectiveToDate;
	}

	public void setEffectiveToDate(Date effectiveToDate) {
		this.effectiveToDate = effectiveToDate;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getUptoBasic() {
		return uptoBasic;
	}

	public void setUptoBasic(BigDecimal uptoBasic) {
		this.uptoBasic = uptoBasic;
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

	public String getGrNo() {
		return grNo;
	}

	public void setGrNo(String grNo) {
		this.grNo = grNo;
	}

	public Date getGrDate() {
		return grDate;
	}

	public void setGrDate(Date grDate) {
		this.grDate = grDate;
	}

	public BigDecimal getRevision() {
		return revision;
	}

	public void setRevision(BigDecimal revision) {
		this.revision = revision;
	}
	
}
