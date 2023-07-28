package com.tcs.sgv.common.valueobject;

import java.math.BigDecimal;

public class BillVitoVO {
	
	
	private long srNo;
	private String auditorName;
	private Long noOfBills;
	private String billType;
	private BigDecimal totalAmount;
	private Long audUserId;
	private String strtotalAmount;
	private String billTypeCode;
	
	
	public String getStrtotalAmount() {
		return strtotalAmount;
	}
	public void setStrtotalAmount(String strtotalAmount) {
		this.strtotalAmount = strtotalAmount;
	}
	public Long getAudUserId() {
		return audUserId;
	}
	public void setAudUserId(Long audUserId) {
		this.audUserId = audUserId;
	}
	public String getAuditorName() {
		return auditorName;
	}
	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}
	
	public long getSrNo() {
		return srNo;
	}
	public void setSrNo(long srNo) {
		this.srNo = srNo;
	}
	public Long getNoOfBills() {
		return noOfBills;
	}
	public void setNoOfBills(Long noOfBills) {
		this.noOfBills = noOfBills;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getBillTypeCode() {
		return billTypeCode;
	}
	public void setBillTypeCode(String billTypeCode) {
		this.billTypeCode = billTypeCode;
	}
	
	
}	