package com.tcs.sgv.common.valueobject;

public class DDODetailsVO implements java.io.Serializable{
	
	private int ddoNo;
	private int cardexNo;
	private String ddoName;
	private String ddoDept;
	private String ddoUnit;
	private int ddoUserId;
	private int ddoLocId; 
	
	public int getCardexNo() {
		return cardexNo;
	}
	public void setCardexNo(int cardexNo) {
		this.cardexNo = cardexNo;
	}
	public String getDdoDept() {
		return ddoDept;
	}
	public void setDdoDept(String ddoDept) {
		this.ddoDept = ddoDept;
	}
	public String getDdoName() {
		return ddoName;
	}
	public void setDdoName(String ddoName) {
		this.ddoName = ddoName;
	}
	public int getDdoNo() {
		return ddoNo;
	}
	public void setDdoNo(int ddoNo) {
		this.ddoNo = ddoNo;
	}
	public String getDdoUnit() {
		return ddoUnit;
	}
	public void setDdoUnit(String ddoUnit) {
		this.ddoUnit = ddoUnit;
	}
	public int getDdoUserId() {
		return ddoUserId;
	}
	public void setDdoUserId(int ddoUserId) {
		this.ddoUserId = ddoUserId;
	}
	public int getDdoLocId() {
		return ddoLocId;
	}
	public void setDdoLocId(int ddoLocId) {
		this.ddoLocId = ddoLocId;
	}
	
	
}
