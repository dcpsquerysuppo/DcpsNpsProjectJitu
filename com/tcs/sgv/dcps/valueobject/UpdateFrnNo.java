package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class UpdateFrnNo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4988725121706130795L;
	private Long frnhstid;
	private String fileName;
	private String oldFrn;
	private String newFrnNo;
	private String empAmount;
	private String emplyrAmount;
	private String remarks;
	
	private Date createdDate;
	private Date updatedDate;
	
	
	
	public Long getFrnhstid() {
		return frnhstid;
	}
	public void setFrnhstid(Long frnhstid) {
		this.frnhstid = frnhstid;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getOldFrn() {
		return oldFrn;
	}
	public void setOldFrn(String oldFrn) {
		this.oldFrn = oldFrn;
	}
	public String getNewFrnNo() {
		return newFrnNo;
	}
	public void setNewFrnNo(String newFrnNo) {
		this.newFrnNo = newFrnNo;
	}
	public String getEmpAmount() {
		return empAmount;
	}
	public void setEmpAmount(String empAmount) {
		this.empAmount = empAmount;
	}
	public String getEmplyrAmount() {
		return emplyrAmount;
	}
	public void setEmplyrAmount(String emplyrAmount) {
		this.emplyrAmount = emplyrAmount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreatedDate() {
		return createdDate;
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
	
	
	
}
