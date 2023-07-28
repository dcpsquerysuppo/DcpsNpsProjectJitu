package com.tcs.sgv.common.valuebeans;

public class CommonVO extends BaseValueBean 
{
	String commonId;
	String commonDesc;
	String frmDate;
	public String getCommonDesc() {
		return commonDesc;
	}
	public void setCommonDesc(String commonDesc) {
		this.commonDesc = commonDesc;
	}
	public String getCommonId() {
		return commonId;
	}
	public void setCommonId(String commonId) {
		this.commonId = commonId;
	}
	public String getFrmDate() {
		return frmDate;
	}
	public void setFrmDate(String frmDate) {
		this.frmDate = frmDate;
	}
		
}
