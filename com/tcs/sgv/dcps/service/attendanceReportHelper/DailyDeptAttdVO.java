package com.tcs.sgv.dcps.service.attendanceReportHelper;

public class DailyDeptAttdVO {
	
@Override
	public String toString() {
		return "DailyDeptAttdVO [deptName=" + deptName + ", total=" + total
				+ ", present=" + present + ", absent=" + absent + ", perCent="
				+ perCent + "]";
	}
public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getPresent() {
		return present;
	}
	public void setPresent(String present) {
		this.present = present;
	}
	public String getAbsent() {
		return absent;
	}
	public void setAbsent(String absent) {
		this.absent = absent;
	}
	public String getPerCent() {
		return perCent;
	}
	public void setPerCent(String perCent) {
		this.perCent = perCent;
	}
private String deptName;
private String total;
private String present;
private String absent;
private String perCent;

}
