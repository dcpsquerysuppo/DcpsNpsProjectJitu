package com.tcs.sgv.common.valueobject;

import java.math.BigDecimal;

public class BillEdpVO {
	
	private long billEdpId;
	private long typeEdpId;
	private long billNo;
	private BigDecimal edpAmt;
	private String edpCode;
	private String edpDesc;
	private String addDedFlag;
	private String edpCategory;
	private String budmjrHd;
	private String budsubmjrHd;
	private String budminHd;
	private String budsubHd;
	private String buddtlHd;
	private String budobjHd;
	private String objHdCode;
	private String receiptEdp;

	
	public String getReceiptEdp() {
		return receiptEdp;
	}
	public void setReceiptEdp(String receiptEdp) {
		this.receiptEdp = receiptEdp;
	}
	public long getBillEdpId() {
		return billEdpId;
	}
	public void setBillEdpId(long billEdpId) {
		this.billEdpId = billEdpId;
	}
	public long getBillNo() {
		return billNo;
	}
	public void setBillNo(long billNo) {
		this.billNo = billNo;
	}
	public String getBuddtlHd() {
		return buddtlHd;
	}
	public void setBuddtlHd(String buddtlHd) {
		this.buddtlHd = buddtlHd;
	}
	public String getBudmjrHd() {
		return budmjrHd;
	}
	public void setBudmjrHd(String budmjrHd) {
		this.budmjrHd = budmjrHd;
	}
	public String getBudobjHd() {
		return budobjHd;
	}
	public void setBudobjHd(String budobjHd) {
		this.budobjHd = budobjHd;
	}
	public String getBudsubHd() {
		return budsubHd;
	}
	public void setBudsubHd(String budsubHd) {
		this.budsubHd = budsubHd;
	}
	public String getBudsubmjrHd() {
		return budsubmjrHd;
	}
	public void setBudsubmjrHd(String budsubmjrHd) {
		this.budsubmjrHd = budsubmjrHd;
	}
	public BigDecimal getEdpAmt() {
		return edpAmt;
	}
	public void setEdpAmt(BigDecimal edpAmt) {
		this.edpAmt = edpAmt;
	}
	public String getEdpCode() {
		return edpCode;
	}
	public void setEdpCode(String edpCode) {
		this.edpCode = edpCode;
	}
	public String getEdpDesc() {
		return edpDesc;
	}
	public void setEdpDesc(String edpDesc) {
		this.edpDesc = edpDesc;
	}
	public String getObjHdCode() {
		return objHdCode;
	}
	public void setObjHdCode(String objHdCode) {
		this.objHdCode = objHdCode;
	}
	public long getTypeEdpId() {
		return typeEdpId;
	}
	public void setTypeEdpId(long typeEdpId) {
		this.typeEdpId = typeEdpId;
	}
	public String getAddDedFlag() {
		return addDedFlag;
	}
	public void setAddDedFlag(String addDedFlag) {
		this.addDedFlag = addDedFlag;
	}
	public String getBudminHd() {
		return budminHd;
	}
	public void setBudminHd(String budminHd) {
		this.budminHd = budminHd;
	}
	public String getEdpCategory() {
		return edpCategory;
	}
	public void setEdpCategory(String edpCategory) {
		this.edpCategory = edpCategory;
	}
}
