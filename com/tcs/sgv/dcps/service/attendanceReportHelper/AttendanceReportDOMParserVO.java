package com.tcs.sgv.dcps.service.attendanceReportHelper;

public class AttendanceReportDOMParserVO {
	private String date;
	private String inTime;
	private String outTime;
	private String status;
	private String workHrs;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getInTime() {
		return inTime;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
	public String getOutTime() {
		return outTime;
	}
	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getWorkHrs() {
		return workHrs;
	}
	public void setWorkHrs(String workHrs) {
		this.workHrs = workHrs;
	}
}
