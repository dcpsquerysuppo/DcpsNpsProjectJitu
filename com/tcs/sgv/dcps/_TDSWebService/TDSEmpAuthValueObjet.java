package com.tcs.sgv.dcps._TDSWebService;

public class TDSEmpAuthValueObjet 
{
	private String userName;
	private String md5Pwd;
	private String authFlag;
	private String mobileNo;
	private String emailId;
	private String userPersonalName;
	public String getUserPersonalName() {
		return userPersonalName;
	}
	public void setUserPersonalName(String userPersonalName) {
		this.userPersonalName = userPersonalName;
	}
	private String userType;
	
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMd5Pwd() {
		return md5Pwd;
	}
	public void setMd5Pwd(String md5Pwd) {
		this.md5Pwd = md5Pwd;
	}
	public String getAuthFlag() {
		return authFlag;
	}
	public void setAuthFlag(String authFlag) {
		this.authFlag = authFlag;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
}
