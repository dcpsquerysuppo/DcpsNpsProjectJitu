package com.tcs.sgv.common.valueobject;

public class EmpPostVO {
	private long empId;
	private long userId;
	private long postId;
	private String empPrefix;
	private String empFname;
	private String empMname;
	private String empLname;
	private String fullName;
	
	public String getEmpFname() {
		return empFname;
	}
	public void setEmpFname(String empFname) {
		this.empFname = empFname;
	}
	public long getEmpId() {
		return empId;
	}
	public void setEmpId(long empId) {
		this.empId = empId;
	}
	public String getEmpLname() {
		return empLname;
	}
	public void setEmpLname(String empLname) {
		this.empLname = empLname;
	}
	public String getEmpMname() {
		return empMname;
	}
	public void setEmpMname(String empMname) {
		this.empMname = empMname;
	}
	public String getEmpPrefix() {
		return empPrefix;
	}
	public void setEmpPrefix(String empPrefix) {
		this.empPrefix = empPrefix;
	}
	public long getPostId() {
		return postId;
	}
	public void setPostId(long postId) {
		this.postId = postId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	
}
