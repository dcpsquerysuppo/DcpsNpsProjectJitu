/**
 * 
 */
package com.tcs.sgv.dcps.service.attendanceReportHelper;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author
 */
@XmlRootElement(name = "absenceDataReq")
public class BasDataShareReq {

	private String userId;
	private String passWord;
	private String cardId;
	private String sevarthId;
	private String fromDate;
	private String toDate;
	private String deptId;
	private String deptName;
	
	
	
	
	
	/*private String cardId;
	private String uidNo;
	private String ipAddress;
	private String machineId;
	private String flag;
	private String date;
	private String  latitude;
	private String  longitude;
	private String isType;
	private String remarks;
	private String  address;
*/	
	
	
	
	public String getUserId() {
		return userId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
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
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	@Override
	public String toString() {
		return "BasDataShareReq [userId=" + userId + ", passWord=" + passWord
				+ ", cardId=" + cardId + ", sevarthId=" + sevarthId
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", deptId="
				+ deptId + ", deptName=" + deptName + "]";
	}
	
	
	
	
	
	
	
	
	

	
}
