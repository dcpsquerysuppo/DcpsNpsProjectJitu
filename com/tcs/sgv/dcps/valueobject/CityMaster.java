package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

import java.sql.Blob;
//import oracle.sql.BLOB;

public class CityMaster {

	@Override
	public String toString() {
		return "CityMaster [cId=" + cId + ", cName=" + cName + ", cAddress="
				+ cAddress + ", cEmailId=" + cEmailId + "]";
	}
	private Long cId;
	private String cName,cAddress,cEmailId;
	private Blob img;
	
//	public Blob getImg() {
//		return img;
//	}
//	public void setImg(Blob img) {
//		this.img = img;
//	}
	public Long getcId() {
		return cId;
	}
	public void setcId(Long cId) {
		this.cId = cId;
	}
	public String getcName() {
		return cName;
	}
	public void setcName(String cName) {
		this.cName = cName;
	}
	public String getcAddress() {
		return cAddress;
	}
	public void setcAddress(String cAddress) {
		this.cAddress = cAddress;
	}
	public String getcEmailId() {
		return cEmailId;
	}
	public void setcEmailId(String cEmailId) {
		this.cEmailId = cEmailId;
	}

	
}
