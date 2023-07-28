package com.tcs.sgv.common.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.ChangeUserNameDAOImpl;
import com.tcs.sgv.common.dao.ReSetPasswordDAOImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.pensionpay.dao.ReSetPasswordForATODAOImpl;

public class ReSetPasswordServiceImpl extends ServiceImpl
{
	private final Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject validateDdoCode(Map<String, Object> inputMap)
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		
		String lSBStatus = "";
		String lStrFinal = "";
		
		try{
			ReSetPasswordDAOImpl lObjreSetDaoImpl = new ReSetPasswordDAOImpl(OrgEmpMst.class,serv.getSessionFactory());			
			
			String lStrUserName = StringUtility.getParameter("userName", request);	
			String lStrEmpName = lObjreSetDaoImpl.chkUserName(lStrUserName);
			
			if(lStrEmpName.equals("N") || lStrEmpName.equals("Invalid")){
				lStrFinal = "Invalid";
				lSBStatus = getResponseXMLDoc(lStrFinal, "").toString();
			}else if(lStrEmpName.equals("DCPS")){
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
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in validateDdoCode");
		}
		
		return resObj;
	}
	
	public ResultObject setPasswordForDDO(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		
		String lStrPwd = "";
		String lStrBdate = "";
		
		try {
			ReSetPasswordDAOImpl lObjreSetDaoImpl = new ReSetPasswordDAOImpl(OrgEmpMst.class,serv.getSessionFactory());
			ReSetPasswordForATODAOImpl lObjReSetPasswdATO = new ReSetPasswordForATODAOImpl(OrgUserMst.class,serv.getSessionFactory());
			
			Long lLngUserId = Long.parseLong(StringUtility.getParameter("userId", request));
			
			if(lLngUserId != null){
				/*lStrBdate = lObjReSetPasswdATO.getBirthDate(lLngUserId);
				if(!lStrBdate.equals("")){					
					lObjreSetDaoImpl.setPassword(lLngUserId, lStrBdate);
					lStrPwd = "Bday";
				}else{	*/				
					lObjreSetDaoImpl.setPassword(lLngUserId, "ifms123");
					lStrPwd = "default";
				//}
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
	
	private StringBuilder getResponseXMLDoc(String lStrPwd, String lStrBdate) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<PWD>");
		lStrBldXML.append(lStrPwd);
		lStrBldXML.append("</PWD>");
		lStrBldXML.append("<BDATE>");
		lStrBldXML.append(lStrBdate);
		lStrBldXML.append("</BDATE>");		
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	private StringBuilder getResponseXMLDocDDO(String lStrDDOName, String lStrDDOPersnlName, String lStrUserName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DDONAME>");
		lStrBldXML.append(lStrDDOName);
		lStrBldXML.append("</DDONAME>");
		lStrBldXML.append("<DDOPERNAME>");
		lStrBldXML.append(lStrDDOPersnlName);
		lStrBldXML.append("</DDOPERNAME>");
		lStrBldXML.append("<USRNAME>");
		lStrBldXML.append(lStrUserName);
		lStrBldXML.append("</USRNAME>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
