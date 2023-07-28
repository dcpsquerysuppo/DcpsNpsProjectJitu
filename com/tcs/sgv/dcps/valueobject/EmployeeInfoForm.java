package com.tcs.sgv.dcps.valueobject;

import java.util.Date;

public class EmployeeInfoForm implements java.io.Serializable {

	private Long  identity;
	private String EmpName;
	private String country;
	private Long  primaryKey;
	
	public Long getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(Long primaryKey) {
		this.primaryKey = primaryKey;
	}
	public Long getIdentity() {
		return identity;
	}
	public void setIdentity(Long identity) {
		this.identity = identity;
	}
	public String getEmpName() {
		return EmpName;
	}
	public void setEmpName(String empName) {
		EmpName = empName;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
		

	
}
