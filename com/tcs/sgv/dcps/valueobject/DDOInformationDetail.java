package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class DDOInformationDetail implements java.io.Serializable {

	private Long ddoDetailId;

	private String ddoCode;

	private String administrativeDept;

	private String fieldHodDept;

	private String ddoName;

	private String ddoDesignation;

	private Date wefDate;

	private String tanNo;

	private String itawardcircle;

	private String bankName;

	private String branchName;

	private String accountNo;

	private String remarks;

	private Long createdBy;

	private Long createdByPost;

	private Date createdDate;

	private Long updatedBy;

	private Long updatedByPost;

	private Date updatedDate;

	private String ifscCode;

	/**
	 * @return the ifscCode
	 */
	public String getIfscCode() {
		return ifscCode;
	}

	/**
	 * @param ifscCode
	 *            the ifscCode to set
	 */
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	// Default Constructer
	public DDOInformationDetail() {
		super();
	}

	public Long getDdoDetailId() {
		return ddoDetailId;
	}

	public void setDdoDetailId(Long ddoDetailId) {
		this.ddoDetailId = ddoDetailId;
	}

	/**
	 * @return the ddoCode
	 */
	public String getDdoCode() {
		return ddoCode;
	}

	/**
	 * @param ddoCode
	 *            the ddoCode to set
	 */
	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
	}

	public String getAdministrativeDept() {
		return administrativeDept;
	}

	public void setAdministrativeDept(String administrativeDept) {
		this.administrativeDept = administrativeDept;
	}

	public String getFieldHodDept() {
		return fieldHodDept;
	}

	public void setFieldHodDept(String fieldHodDept) {
		this.fieldHodDept = fieldHodDept;
	}

	public String getDdoName() {
		return ddoName;
	}

	public void setDdoName(String ddoName) {
		this.ddoName = ddoName;
	}

	public String getDdoDesignation() {
		return ddoDesignation;
	}

	public void setDdoDesignation(String ddoDesignation) {
		this.ddoDesignation = ddoDesignation;
	}

	public String getTanNo() {
		return tanNo;
	}

	public void setTanNo(String tanNo) {
		this.tanNo = tanNo;
	}

	public String getItawardcircle() {
		return itawardcircle;
	}

	public void setItawardcircle(String itawardcircle) {
		this.itawardcircle = itawardcircle;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public Date getWefDate() {
		return wefDate;
	}

	public void setWefDate(Date wefDate) {
		this.wefDate = wefDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
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

	public Date getCreatedDate() {
		return createdDate;
	}

}
