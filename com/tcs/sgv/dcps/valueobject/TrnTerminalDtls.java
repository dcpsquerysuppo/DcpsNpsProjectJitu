package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class TrnTerminalDtls implements java.io.Serializable {

	private Long terminationId;
	private String sevarthId;
	private Date dateOfTermination;
	private Long reasonOfTermination;
	private String curAddress;
	private long dsgn;
	private Long statusFlag;
	private String ddoCode;
	private String locCode;
	private String transactionId;
	private String reasonOfTOReject;
	private Long createdPostId;
	private Date createdDate;
	private Long updatedPostId;
	private Date updatedDate;
	private Date formAGenDate;
	
	
	public Date getFormAGenDate() {
		return formAGenDate;
	}
	public void setFormAGenDate(Date formAGenDate) {
		this.formAGenDate = formAGenDate;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getReasonOfTOReject() {
		return reasonOfTOReject;
	}
	public void setReasonOfTOReject(String reasonOfTOReject) {
		this.reasonOfTOReject = reasonOfTOReject;
	}
	public Long getTerminationId() {
		return terminationId;
	}
	public void setTerminationId(Long terminationId) {
		this.terminationId = terminationId;
	}
	public String getSevarthId() {
		return sevarthId;
	}
	public void setSevarthId(String sevarthId) {
		this.sevarthId = sevarthId;
	}
	public Date getDateOfTermination() {
		return dateOfTermination;
	}
	public void setDateOfTermination(Date dateOfTermination) {
		this.dateOfTermination = dateOfTermination;
	}
	public Long getReasonOfTermination() {
		return reasonOfTermination;
	}
	public void setReasonOfTermination(Long reasonOfTermination) {
		this.reasonOfTermination = reasonOfTermination;
	}
	public String getCurAddress() {
		return curAddress;
	}
	public void setCurAddress(String curAddress) {
		this.curAddress = curAddress;
	}
	public long getDsgn() {
		return dsgn;
	}
	public void setDsgn(long dsgn) {
		this.dsgn = dsgn;
	}
	public Long getStatusFlag() {
		return statusFlag;
	}
	public void setStatusFlag(Long statusFlag) {
		this.statusFlag = statusFlag;
	}
	public String getDdoCode() {
		return ddoCode;
	}
	public void setDdoCode(String ddoCode) {
		this.ddoCode = ddoCode;
	}
	public String getLocCode() {
		return locCode;
	}
	public void setLocCode(String locCode) {
		this.locCode = locCode;
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
