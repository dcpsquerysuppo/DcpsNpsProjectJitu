/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 29, 2011		383385								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.valueobject;

import java.io.Serializable;
import java.util.Date;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Sep 29, 2011
 */
public class RltBankBranchTmp implements Serializable{
	
	   /**
	   * serial version uid
	   */
	   private static final long serialVersionUID = 3697338070498206142L;
	   
	   private Long bankBranchId;   
	   private String bankCode;
	   private Long branchCode;
	   private String branchName;
	   private String branchAddress;
	   private String micrCode;
	   private String ifscCode;
	   private String contact;
	   private String city;
	   private String district;
	   private String state;
	   private Long headOff;   
	   private Long createdUserId;
	   private Long createdPostId;
	   private Date createdDate;
	   private Long updatedUserId;
	   private Long updatedPostId;
	   private Date updatedDate;
	   private Long dbId;
	   private Long langId;
  	   private Long reportingBranchCode;
	   private String centerName;
	   private String bsrCode; 
	   
	   public RltBankBranchTmp()
	   {}

	 

	public Long getBankBranchId() {
	
		return bankBranchId;
	}

	
	public void setBankBranchId(Long bankBranchId) {
	
		this.bankBranchId = bankBranchId;
	}

	
	public String getBankCode() {
	
		return bankCode;
	}

	
	public void setBankCode(String bankCode) {
	
		this.bankCode = bankCode;
	}

	
	public Long getBranchCode() {
	
		return branchCode;
	}

	
	public void setBranchCode(Long branchCode) {
	
		this.branchCode = branchCode;
	}

	
	public String getBranchName() {
	
		return branchName;
	}

	
	public void setBranchName(String branchName) {
	
		this.branchName = branchName;
	}

	
	public String getBranchAddress() {
	
		return branchAddress;
	}

	
	public void setBranchAddress(String branchAddress) {
	
		this.branchAddress = branchAddress;
	}

	
	public String getMicrCode() {
	
		return micrCode;
	}
	
	public void setMicrCode(String micrCode) {
	
		this.micrCode = micrCode;
	}

	public String getIfscCode() {
	
		return ifscCode;
	}

	
	public void setIfscCode(String ifscCode) {
	
		this.ifscCode = ifscCode;
	}

	
	public String getContact() {
	
		return contact;
	}

	
	public void setContact(String contact) {
	
		this.contact = contact;
	}

	
	public String getCity() {
	
		return city;
	}

	
	public void setCity(String city) {
	
		this.city = city;
	}

	
	public String getDistrict() {
	
		return district;
	}

	
	public void setDistrict(String district) {
	
		this.district = district;
	}

	
	public String getState() {
	
		return state;
	}

	
	public void setState(String state) {
	
		this.state = state;
	}

	
	public Long getHeadOff() {
	
		return headOff;
	}

	
	public void setHeadOff(Long headOff) {
	
		this.headOff = headOff;
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

	public Long getLangId() {
	
		return langId;
	}

	public void setLangId(Long langId) {
	
		this.langId = langId;
	}

	public Long getReportingBranchCode() {
	
		return reportingBranchCode;
	}

	
	public void setReportingBranchCode(Long reportingBranchCode) {
	
		this.reportingBranchCode = reportingBranchCode;
	}

	
	public String getCenterName() {
	
		return centerName;
	}

	
	public void setCenterName(String centerName) {
	
		this.centerName = centerName;
	}

	
	public String getBsrCode() {
	
		return bsrCode;
	}

	
	public void setBsrCode(String bsrCode) {
	
		this.bsrCode = bsrCode;
	}
	   
	   

}
