package com.tcs.sgv.dcps.service.attendanceReportHelper;
public class DataEmpAttendanceList {
	
	private String date;
	private String inTime;
	private String outTime;
	private String workHours;
	private String status;
	
	
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
	public String getWorkHours() {
		return workHours;
	}
	public void setWorkHours(String workHours) {
		this.workHours = workHours;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "DataEmpAttendanceList [date=" + date + ", inTime=" + inTime
				+ ", outTime=" + outTime + ", workHours=" + workHours
				+ ", status=" + status + "]";
	}
		
	

	
	
}
