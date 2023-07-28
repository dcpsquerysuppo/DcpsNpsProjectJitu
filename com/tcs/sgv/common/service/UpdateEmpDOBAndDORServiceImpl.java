package com.tcs.sgv.common.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.UpdateEmpDOBAndDORDAO;
import com.tcs.sgv.common.dao.UpdateEmpDOBAndDORDAOImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.MstEmp;

public class UpdateEmpDOBAndDORServiceImpl extends ServiceImpl {

	Log gLogger = LogFactory.getLog(getClass());

	public ResultObject getEmpInfoFromSevaarthId(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		UpdateEmpDOBAndDORDAO lObjEmployeeInfoDAO = new UpdateEmpDOBAndDORDAOImpl(MstEmp.class, serv.getSessionFactory());

		try {
			String lStrSevaarthId = StringUtility.getParameter("SevaarthId", request);
			List lLstEmployeeInfo = lObjEmployeeInfoDAO.getEmployeeInfo(lStrSevaarthId);
			if (lLstEmployeeInfo != null && !lLstEmployeeInfo.isEmpty()) {
				Object[] lArrObj = (Object[]) lLstEmployeeInfo.get(0);
				inputMap.put("Ddocode", lArrObj[0]);
				inputMap.put("DdoName", lArrObj[1]);
				inputMap.put("Cadre", lArrObj[2]);
				inputMap.put("DdoOffice", lArrObj[3]);
				inputMap.put("DOB", lArrObj[4]);
				inputMap.put("DOR", lArrObj[5]);
				inputMap.put("EmpId", lArrObj[6]);
			} else {
				inputMap.put("invalid", "invalid");
			}
			inputMap.put("SevaarthId", lStrSevaarthId);
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("UpdateDOB");
		return resObj;
	}

	public ResultObject updateDOBandDOR(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		UpdateEmpDOBAndDORDAO lObjEmployeeInfoDAO = new UpdateEmpDOBAndDORDAOImpl(MstEmp.class, serv.getSessionFactory());
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Boolean lBlFlag = false;
		try {
			String lStrSevaarthId = StringUtility.getParameter("SevaarthId", request);
			String lStrOrgEmpId = StringUtility.getParameter("txtEmpId", request);
			String lStrNewDOB = StringUtility.getParameter("txtNewDOB", request);
			String lStrNewDOR = StringUtility.getParameter("txtNewDOR", request);
			String txtReason = StringUtility.getParameter("txtReason", request);
			Date lDtNewDOB = null;
			Date lDtNewDOR = null;

			if (!lStrNewDOB.equals("")) {
				lDtNewDOB = lObjSimpleDateFormat.parse(lStrNewDOB);
			}
			if (!lStrNewDOR.equals("")) {
				lDtNewDOR = lObjSimpleDateFormat.parse(lStrNewDOR);
			}
			lObjEmployeeInfoDAO.updateDOB(lStrSevaarthId, lDtNewDOB, lDtNewDOR);
			if (!lStrOrgEmpId.equals("")) {
				Long lLngOrgEmpId = Long.parseLong(lStrOrgEmpId);
				lObjEmployeeInfoDAO.updateDOR(lLngOrgEmpId, lDtNewDOB, lDtNewDOR,txtReason);
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
