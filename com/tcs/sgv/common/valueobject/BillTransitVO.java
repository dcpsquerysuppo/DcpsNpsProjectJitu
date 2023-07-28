package com.tcs.sgv.common.valueobject;

import java.math.BigDecimal;

public class BillTransitVO {
	
	
	private long srNo;
	private String billCntrlNo;
	private Long tokenNo;
	private String majorHead;
	private String billType;
	private String cardexNo;
	private String ddoName;
	private BigDecimal billGrossAmount;
	private String auditorName;
	private Long audUserId;
	private String status;
	private String refNum;
	private String strBillGrossAmount;
	
	public String getStrBillGrossAmount() {
		return strBillGrossAmount;
	}
	public void setStrBillGrossAmount(String strBillGrossAmount) {
		this.strBillGrossAmount = strBillGrossAmount;
	}
	public String getRefNum() {
		return refNum;
	}
	public void setRefNum(String refNum) {
		this.refNum = refNum;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAuditorName() {
		return auditorName;
	}
	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}
	public Long getAudUserId() {
		return audUserId;
	}
	public void setAudUserId(Long audUserId) {
		this.audUserId = audUserId;
	}
	public String getBillCntrlNo() {
		return billCntrlNo;
	}
	public void setBillCntrlNo(String billCntrlNo) {
		this.billCntrlNo = billCntrlNo;
	}
	public BigDecimal getBillGrossAmount() {
		return billGrossAmount;
	}
	public void setBillGrossAmount(BigDecimal billGrossAmount) {
		this.billGrossAmount = billGrossAmount;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getCardexNo() {
		return cardexNo;
	}
	public void setCardexNo(String cardexNo) {
		this.cardexNo = cardexNo;
	}
	public String getMajorHead() {
		return majorHead;
	}
	public void setMajorHead(String majorHead) {
		this.majorHead = majorHead;
	}
	public long getSrNo() {
		return srNo;
	}
	public void setSrNo(long srNo) {
		this.srNo = srNo;
	}
	public Long getTokenNo() {
		return tokenNo;
	}
	public void setTokenNo(Long tokenNo) {
		this.tokenNo = tokenNo;
	}
	public String getDdoName() {
		return ddoName;
	}
	public void setDdoName(String ddoName) {
		this.ddoName = ddoName;
	}
}	