package com.tcs.sgv.pensionpay.valueobject;

import java.util.Date;

public class TrnFinYearLetterNo{

	private Long trnFinYearLetterNoId;	
	private String pensionerCode;
	private String ppoNo;
	private String letterType;
	private Integer finYearId; 
	private Long letterNo;
	private Integer locationCode;
	private Long createdPostId;
	private Long createdUserId;
	private Date createdDate;
	private Long updatedPostId;
	private Long updatedUserId;
	private Date updatedDate;
	
	public Long getTrnFinYearLetterNoId() {
		return trnFinYearLetterNoId;
	}
	public void setTrnFinYearLetterNoId(Long trnFinYearLetterNoId) {
		this.trnFinYearLetterNoId = trnFinYearLetterNoId;
	}
	public String getPensionerCode() {
		return pensionerCode;
	}
	public void setPensionerCode(String pensionerCode) {
		this.pensionerCode = pensionerCode;
	}
	public String getPpoNo() {
		return ppoNo;
	}
	public void setPpoNo(String ppoNo) {
		this.ppoNo = ppoNo;
	}
	public String getLetterType() {
		return letterType;
	}
	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}
	public Integer getFinYearId() {
		return finYearId;
	}
	public void setFinYearId(Integer finYearId) {
		this.finYearId = finYearId;
	}
	public Long getLetterNo() {
		return letterNo;
	}
	public void setLetterNo(Long letterNo) {
		this.letterNo = letterNo;
	}
	public Integer getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(Integer locationCode) {
		this.locationCode = locationCode;
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
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
}
