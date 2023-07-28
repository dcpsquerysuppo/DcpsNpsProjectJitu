package com.tcs.sgv.common.valueobject;

public class MstIntegrationBillTypes implements java.io.Serializable {
	private Long integBillTypeId;
	private String detailHead;
	private String billType;
	private String billDesc;
	private String formId;
	public Long getIntegBillTypeId() {
		return integBillTypeId;
	}
	public void setIntegBillTypeId(Long integBillTypeId) {
		this.integBillTypeId = integBillTypeId;
	}
	public String getDetailHead() {
		return detailHead;
	}
	public void setDetailHead(String detailHead) {
		this.detailHead = detailHead;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getBillDesc() {
		return billDesc;
	}
	public void setBillDesc(String billDesc) {
		this.billDesc = billDesc;
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}

}
