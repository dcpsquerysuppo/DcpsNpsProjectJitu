package com.tcs.sgv.common.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.GetLoginPageDetailsDAOImpl;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

public class GetLoginPageDetailsServiceImpl extends ServiceImpl
{
	private final Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject getLoginPageDetails(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		HttpSession localHttpSession = request.getSession();
		Long lLngTotalDdo = null;
		Long lLngTotalEmp = null;
		Long lLngTotalBill = null;
		Long lLngTotalEmpInBill = null;		
		Long lLngLoggedInEmp = null;
		
		try {
			GetLoginPageDetailsDAOImpl lObjGetLoginPageDetailsDAOImpl = new GetLoginPageDetailsDAOImpl(OrgDdoMst.class,serv.getSessionFactory());
			
			lLngTotalDdo = lObjGetLoginPageDetailsDAOImpl.getTotalDdo();
			lLngTotalEmp = lObjGetLoginPageDetailsDAOImpl.getTotalEmployee();
			lLngTotalBill = lObjGetLoginPageDetailsDAOImpl.getTotalBillCount();
			lLngTotalEmpInBill = lObjGetLoginPageDetailsDAOImpl.getTotalEmpInBill();
			lLngLoggedInEmp = lObjGetLoginPageDetailsDAOImpl.getLoggedInEmp();
			
			Calendar lObjCalendar = Calendar.getInstance();		
			Integer lIntYear = lObjCalendar.get(lObjCalendar.YEAR);
			Integer lIntCurMonth = lObjCalendar.get(lObjCalendar.MONTH) + 1;
			String lStrMonth = lObjGetLoginPageDetailsDAOImpl.getMonthDesc(lIntCurMonth);			
			
			String lSBStatus = getResponseXMLDoc(lLngTotalDdo, lLngTotalEmp, lLngTotalBill, lLngTotalEmpInBill, lStrMonth, lIntYear, lLngLoggedInEmp).toString();
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getLoginPageDetails");
		}
		return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(Long lLngTotalDdo, Long lLngTotalEmp, Long lLngTotalBill, Long lLngTotalEmpInBill, String lStrMonth,
			Integer lIntYear, Long lLngLoggedInEmp)
	{
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DDO>");
		lStrBldXML.append(lLngTotalDdo);
		lStrBldXML.append("</DDO>");
		lStrBldXML.append("<EMP>");
		lStrBldXML.append(lLngTotalEmp);
		lStrBldXML.append("</EMP>");
		lStrBldXML.append("<BILL>");
		lStrBldXML.append(lLngTotalBill);
		lStrBldXML.append("</BILL>");
		lStrBldXML.append("<BILLEMP>");
		lStrBldXML.append(lLngTotalEmpInBill);
		lStrBldXML.append("</BILLEMP>");
		lStrBldXML.append("<MONTH>");
		lStrBldXML.append(lStrMonth);
		lStrBldXML.append("</MONTH>");
		lStrBldXML.append("<YEAR>");
		lStrBldXML.append(lIntYear);
		lStrBldXML.append("</YEAR>");
		lStrBldXML.append("<LOGIN>");
		lStrBldXML.append(lLngLoggedInEmp);
		lStrBldXML.append("</LOGIN>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
