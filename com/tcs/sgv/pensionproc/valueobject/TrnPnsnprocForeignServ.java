package com.tcs.sgv.pensionproc.valueobject;

import java.util.Date;

public class TrnPnsnprocForeignServ {

	private Long foreignServId;	
	private String locationCode;
	private Long inwardPensionId;
	private Date toDate;
	private Date fromDate;	
	private Long totalAmt;
	private Long paidAmt;
	private String challanNo;
	private Date challanDate;
	private String deptOffName;
	private Long createdUserId;
    private Long createdPostId;
    private Date createdDate;
    private Long updatedUserId;
    private Long updatedPostId;
    private Date updatedDate;
	
	public Long getForeignServId() {
		return foreignServId;
	}
	public void setForeignServId(Long foreignServId) {
		this.foreignServId = foreignServId;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public Long getInwardPensionId() {
		return inwardPensionId;
	}
	public void setInwardPensionId(Long inwardPensionId) {
		this.inwardPensionId = inwardPensionId;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Long getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(Long totalAmt) {
		this.totalAmt = totalAmt;
	}
	public Long getPaidAmt() {
		return paidAmt;
	}
	public void setPaidAmt(Long paidAmt) {
		this.paidAmt = paidAmt;
	}
	public String getChallanNo() {
		return challanNo;
	}
	public void setChallanNo(String challanNo) {
		this.challanNo = challanNo;
	}
	public Date getChallanDate() {
		return challanDate;
	}
	public void setChallanDate(Date challanDate) {
		this.challanDate = challanDate;
	}
	public String getDeptOffName() {
		return deptOffName;
	}
	public void setDeptOffName(String deptOffName) {
		this.deptOffName = deptOffName;
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
