
package com.tcs.sgv.dcps.service.attendanceReportHelper;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.tcs.sgv.dcps.service.attendanceReportHelper.DataDeptAttendanceList;
import com.tcs.sgv.dcps.service.attendanceReportHelper.DataDeptInfoList;
import com.tcs.sgv.dcps.service.attendanceReportHelper.DataEmpAttendanceList;





/**
 * @author
 */
@XmlRootElement(name = "absenceDataResp")
public class BasDataShareResp {
	
	

	public BasDataShareResp() {
		// TODO Auto-generated constructor stub
	}
	
	
	private String cardId;
	private String sevarthId;
	private String uid;
	private String fullName;
	private String desigName;
	private String deptName;
	private String mobileNo;
	private String message;
	private List<DataDeptInfoList> dataDeptInfoList;
	private List<DataEmpAttendanceList> dataEmpAttendanceList;
	private List<DataDeptAttendanceList> dataDeptAttendanceList;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<DataDeptInfoList> getDataDeptInfoList() {
		return dataDeptInfoList;
	}
	public void setDataDeptInfoList(List<DataDeptInfoList> dataDeptInfoList) {
		this.dataDeptInfoList = dataDeptInfoList;
	}
	public List<DataEmpAttendanceList> getDataEmpAttendanceList() {
		return dataEmpAttendanceList;
	}
	public void setDataEmpAttendanceList(
			List<DataEmpAttendanceList> dataEmpAttendanceList) {
		this.dataEmpAttendanceList = dataEmpAttendanceList;
	}
	public List<DataDeptAttendanceList> getDataDeptAttendanceList() {
		return dataDeptAttendanceList;
	}
	public void setDataDeptAttendanceList(
			List<DataDeptAttendanceList> dataDeptAttendanceList) {
		this.dataDeptAttendanceList = dataDeptAttendanceList;
	}
	@Override
	public String toString() {
		return "BasDataShareResp [cardId=" + cardId + ", sevarthId="
				+ sevarthId + ", uid=" + uid + ", fullName=" + fullName
				+ ", desigName=" + desigName + ", deptName=" + deptName
				+ ", mobileNo=" + mobileNo + ", message=" + message
				+ ", dataDeptInfoList=" + dataDeptInfoList
				+ ", dataEmpAttendanceList=" + dataEmpAttendanceList
				+ ", dataDeptAttendanceList=" + dataDeptAttendanceList + "]";
	}
	public List getResultList()
	{
		List resultList=null;
		
		if(dataDeptInfoList!=null && !dataDeptInfoList.isEmpty())
		{
			resultList=dataDeptInfoList;
		}
		if(dataEmpAttendanceList!=null && !dataEmpAttendanceList.isEmpty())
		{
			resultList=dataEmpAttendanceList;
		}
		if(dataDeptAttendanceList!=null && !dataDeptAttendanceList.isEmpty())
		{
			resultList=dataDeptAttendanceList;
		}
		return resultList;
	}
	
	/*private String cardId;
	private String uidNo;
	private String departName;
	private String desgName;
	private String fullName;
	private String msg;
*/	

	
	
	
	
}