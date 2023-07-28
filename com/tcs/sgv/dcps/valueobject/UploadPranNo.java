package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class UploadPranNo implements java.io.Serializable {

	private Long uploadPranId;
	private String fileName;
	private Long fileId;
	private String ppan;
	private String pranNo;
	private String empName;
	private Date doj;
	private Date pranGenDate;
	private Long dtoCode;
	private String ddoRegNo;
	private int status;
	private int isPranUpdated;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedPostId;
	private Date updatedDate;
	private String attachDesc;
	private Long attachId;
	
	
	
	public String getAttachDesc() {
		return attachDesc;
	}
	public void setAttachDesc(String attachDesc) {
		this.attachDesc = attachDesc;
	}
	public Long getAttachId() {
		return attachId;
	}
	public void setAttachId(Long attachId) {
		this.attachId = attachId;
	}
	public int getIsPranUpdated() {
		return isPranUpdated;
	}
	public void setIsPranUpdated(int isPranUpdated) {
		this.isPranUpdated = isPranUpdated;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Long getUploadPranId() {
		return uploadPranId;
	}
	public void setUploadPranId(Long uploadPranId) {
		this.uploadPranId = uploadPranId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPpan() {
		return ppan;
	}
	public void setPpan(String ppan) {
		this.ppan = ppan;
	}
	public String getPranNo() {
		return pranNo;
	}
	public void setPranNo(String pranNo) {
		this.pranNo = pranNo;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public Date getDoj() {
		return doj;
	}
	public void setDoj(Date doj) {
		this.doj = doj;
	}
	public Date getPranGenDate() {
		return pranGenDate;
	}
	public void setPranGenDate(Date pranGenDate) {
		this.pranGenDate = pranGenDate;
	}
	public Long getDtoCode() {
		return dtoCode;
	}
	public void setDtoCode(Long dtoCode) {
		this.dtoCode = dtoCode;
	}
	public String getDdoRegNo() {
		return ddoRegNo;
	}
	public void setDdoRegNo(String ddoRegNo) {
		this.ddoRegNo = ddoRegNo;
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
