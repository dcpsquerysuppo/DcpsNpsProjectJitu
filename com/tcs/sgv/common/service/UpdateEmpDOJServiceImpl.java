package com.tcs.sgv.common.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.UpdateEmpDOJDAO;
import com.tcs.sgv.common.dao.UpdateEmpDOJDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.MstEmp;

public class UpdateEmpDOJServiceImpl extends ServiceImpl {

	Log gLogger = LogFactory.getLog(getClass());
	
	private void setSessionInfo(Map inputMap) {
		try {

			
			Map loginMap = (Map) inputMap.get("baseLoginMap");
			long postId = StringUtility.convertToLong(loginMap.get("primaryPostId").toString());
			gLogger.info("postId:"+postId);

		} catch (Exception e) {

		}

	}
	
	
	
	public ResultObject getEmpInfoFromSevaarthId(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		UpdateEmpDOJDAO lObjEmployeeInfoDAO = new UpdateEmpDOJDAOImpl(MstEmp.class, serv.getSessionFactory());

		try {
			String lStrSevaarthId = StringUtility.getParameter("SevaarthId", request);
			List lLstEmployeeInfo = lObjEmployeeInfoDAO.getEmployeeInfo(lStrSevaarthId);
			
			Map loginMap = (Map) inputMap.get("baseLoginMap");
			long postId = StringUtility.convertToLong(loginMap.get("primaryPostId").toString());
			gLogger.info("postId in service:"+postId);
			
			if (lLstEmployeeInfo != null && !lLstEmployeeInfo.isEmpty()) {
				Object[] lArrObj = (Object[]) lLstEmployeeInfo.get(0);
				if((Integer.parseInt(lArrObj[9].toString())==2) && (lArrObj[10].toString().equalsIgnoreCase("N")))
				{
				inputMap.put("Ddocode", lArrObj[0]);
				inputMap.put("DdoName", lArrObj[1]);
				inputMap.put("Cadre", lArrObj[2]);
				inputMap.put("DdoOffice", lArrObj[3]);
				inputMap.put("DOB", lArrObj[4]);
				inputMap.put("DOR", lArrObj[5]);
				inputMap.put("EmpId", lArrObj[6]);
				inputMap.put("EmpName", lArrObj[7]);
				inputMap.put("DOJ", lArrObj[8]);
				inputMap.put("RegStatus", lArrObj[9]);
			}else if((Integer.parseInt(lArrObj[9].toString())==1))
			{
				//inputMap.put("RegStatus", lArrObj[9]);
				inputMap.put("dcpsMsg", "Date of joining of DCPS employee cannot be updated");
	System.out.println("in else if loop of reg satus");
			}
			}
				else {
				inputMap.put("invalid", "invalid");
			}
			inputMap.put("SevaarthId", lStrSevaarthId);
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("UpdateDOJ");
		return resObj;
	}
 
	public ResultObject updateDOJ(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		UpdateEmpDOJDAO lObjEmployeeInfoDAO = new UpdateEmpDOJDAOImpl(MstEmp.class, serv.getSessionFactory());
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Boolean lBlFlag = false;
		try {
			String lStrSevaarthId = StringUtility.getParameter("SevaarthId", request);
			String lStrOrgEmpId = StringUtility.getParameter("txtEmpId", request);
			String lStrNewDOJ = StringUtility.getParameter("txtNewDOJ", request);
			String txtReason = StringUtility.getParameter("txtReason", request);
			
			String lStrEmpName = StringUtility.getParameter("txtEmpName", request);
			String lStrDdoCode = StringUtility.getParameter("txtDdoCode", request);
			String lStrOldDOJ = StringUtility.getParameter("txtDOJ", request);			
				
			Map loginMap = (Map) inputMap.get("baseLoginMap");
			long postId = StringUtility.convertToLong(loginMap.get("primaryPostId").toString());
			gLogger.info("postId in service:"+postId);
			
			
			Date lDtNewDOJ = null;
			Date lDtOldDOJ = null;
			gLogger.info("Date lDtOldDOJ:"+lDtOldDOJ);


			if (!lStrNewDOJ.equals("")) {
				lDtNewDOJ = lObjSimpleDateFormat.parse(lStrNewDOJ);
			}
			if (!lStrOldDOJ.equals("")) {
				lDtOldDOJ = lObjSimpleDateFormat.parse(lStrOldDOJ);
			}
			lObjEmployeeInfoDAO.updateDOJ(lStrSevaarthId, lDtNewDOJ);
			if (!lStrOrgEmpId.equals("")) {
				Long lLngOrgEmpId = Long.parseLong(lStrOrgEmpId);
				lObjEmployeeInfoDAO.updateJoiningDate(lLngOrgEmpId, lDtNewDOJ);
				lObjEmployeeInfoDAO.updateJoiningDateTable(lStrSevaarthId,lStrEmpName,postId,lStrDdoCode,lDtOldDOJ,lDtNewDOJ,txtReason);
			}
			lBlFlag = true;

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getUpdateResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getUpdateResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
