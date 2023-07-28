package com.tcs.sgv.dcps.service.attendanceReportHelper;

public class DailyAttendanceReportVO {
	public String cardID = "";
	public String empFullName = "";
	public String designationName = "";
	public String inTime = "";
	public String outTime = "";
	public String hrsWorked = "";
	public String shiftDesc = "";
	public String status = "";
	
	public DailyAttendanceReportVO() {
		
	}
	
	public DailyAttendanceReportVO(String cardID, String empFullName,String designationName, 
			String inTime, String outTime, String hrsWorked,String shiftDesc,String status) {
		this.cardID = cardID;
		this.designationName = designationName;
		this.empFullName = empFullName;
		this.inTime = inTime;
		this.outTime = outTime ;
		this.hrsWorked = hrsWorked;
		this.status = status;
		this.shiftDesc = shiftDesc;
	}

	public String getCardID() {
		return cardID;
	}

	public void setCardID(String cardID) {
		this.cardID = cardID;
	}

	public String getEmpFullName() {
		return empFullName;
	}

	public void setEmpFullName(String empFullName) {
		this.empFullName = empFullName;
	}

	public String getDesignationName() {
		return designationName;
	}

	public void setDesignationName(String designationName) {
		this.designationName = designationName;
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

	public String getHrsWorked() {
		return hrsWorked;
	}

	public void setHrsWorked(String hrsWorked) {
		this.hrsWorked = hrsWorked;
	}

	public String getShiftDesc() {
		return shiftDesc;
	}

	public void setShiftDesc(String shiftDesc) {
		this.shiftDesc = shiftDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
