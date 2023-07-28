package com.tcs.sgv.dcps.service.attendanceReportHelper;

public class DataDeptInfoList {

	private String DeptId;
	private String DeptName;
	
	public String getDeptId() {
		return DeptId;
	}
	public void setDeptId(String deptId) {
		DeptId = deptId;
	}
	public String getDeptName() {
		return DeptName;
	}
	public void setDeptName(String deptName) {
		DeptName = deptName;
	}
	@Override
	public String toString() {
		return "DataDeptInfoList [DeptId=" + DeptId + ", DeptName=" + DeptName
				+ "]";
	}
	
	
	
	
}
