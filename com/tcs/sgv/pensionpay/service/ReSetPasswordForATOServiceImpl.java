package com.tcs.sgv.pensionpay.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.ReSetPasswordDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.pensionpay.dao.ReSetPasswordForATODAOImpl;

public class ReSetPasswordForATOServiceImpl extends ServiceImpl
{
private final Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject validateUserName(Map<String, Object> inputMap)
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		String lStrFinal = "";
		String lSBStatus = "";		
		
		try{
			ReSetPasswordForATODAOImpl lObjReSetPasswdATO = new ReSetPasswordForATODAOImpl(OrgUserMst.class,serv.getSessionFactory());
			
			String gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			String lStrUserName = StringUtility.getParameter("userName", request);
			
			String lStrEmpName = lObjReSetPasswdATO.chkUserName(lStrUserName, gStrLocationCode);
			if(lStrEmpName.equals("Invalid") || lStrEmpName.equals("N")){
				lStrFinal = "Invalid";
				lSBStatus = getResponseXMLDoc(lStrFinal, "").toString();
			}else if(lStrEmpName.equals("location")){
				lSBStatus = getResponseXMLDoc(lStrEmpName, "").toString();
			}else{
				String []lStrData = lStrEmpName.split("#");
				String lStrEmployeeName = lStrData[0];
				String lStrUserId = lStrData[1];
				lSBStatus = getResponseXMLDoc(lStrEmployeeName, lStrUserId).toString();
			}
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in validateUserName");
		}
		return resObj;
	}
	
	public ResultObject setPasswordForATO(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		
		String lStrPwd = "";
		String lStrBdate = "";
		
		try {
			ReSetPasswordForATODAOImpl lObjReSetPasswdATO = new ReSetPasswordForATODAOImpl(OrgUserMst.class,serv.getSessionFactory());
			ReSetPasswordDAOImpl lObjreSetDaoImpl = new ReSetPasswordDAOImpl(OrgEmpMst.class,serv.getSessionFactory());
						
			Long lLngUserId = Long.parseLong(StringUtility.getParameter("userId", request));
			
			if(lLngUserId != null){
				lStrBdate = lObjReSetPasswdATO.getBirthDate(lLngUserId);
				if(!lStrBdate.equals("")){					
					lObjreSetDaoImpl.setPassword(lLngUserId, lStrBdate);
					lStrPwd = "Bday";
				}else{					
					lObjreSetDaoImpl.setPassword(lLngUserId, "ifms123");
					lStrPwd = "default";
				}
			}
			String lSBStatus = getResponseXMLDoc(lStrPwd, lStrBdate).toString();
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in updateUname");
		}
		
		return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrEmpName, String lStrUserId) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<EMPNAME>");
		lStrBldXML.append(lStrEmpName);
		lStrBldXML.append("</EMPNAME>");
		lStrBldXML.append("<USERID>");
		lStrBldXML.append(lStrUserId);
		lStrBldXML.append("</USERID>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
