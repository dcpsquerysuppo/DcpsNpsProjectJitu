package com.tcs.sgv.dcps.service.attendanceReportHelper;
public class DataDeptAttendanceList {
	
	
	private String cardId;
	private String sevarthId;
	private String uid;
	private String fullName;
	private String desigName;
	private String deptId;
	private String deptName;
	private String mobileNo;
	private String date;
	private String inTime;
	private String outTime;
	private String workedHours;
	private String status;
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getSevarthId() {
		return sevarthId;
	}
	public void setSevarthId(String sevarthId) {
		this.sevarthId = sevarthId;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getDesigName() {
		return desigName;
	}
	public void setDesigName(String desigName) {
		this.desigName = desigName;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
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
	public String getWorkedHours() {
		return workedHours;
	}
	public void setWorkedHours(String workedHours) {
		this.workedHours = workedHours;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "DataDeptAttendanceList [cardId=" + cardId + ", sevarthId="
				+ sevarthId + ", uid=" + uid + ", fullName=" + fullName
				+ ", desigName=" + desigName + ", deptId=" + deptId
				+ ", deptName=" + deptName + ", mobileNo=" + mobileNo
				+ ", date=" + date + ", inTime=" + inTime + ", outTime="
				+ outTime + ", workedHours=" + workedHours + ", status="
				+ status + "]";
	}
		
	
	
	
	
}
