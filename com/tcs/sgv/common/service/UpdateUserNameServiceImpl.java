package com.tcs.sgv.common.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.tcs.sgv.common.dao.UpdateUserNameDAOImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;

public class UpdateUserNameServiceImpl extends ServiceImpl
{
	private final Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject getDataFromUserName(Map<String, Object> inputMap)
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		
		String lSBStatus = "";
		String lStrFinal = "";
		
		try{
			UpdateUserNameDAOImpl lObjUpdateUserNameDaoImpl = new UpdateUserNameDAOImpl(OrgEmpMst.class,serv.getSessionFactory());			
			
			String lStrUserName = StringUtility.getParameter("userName", request);	
			String lStrEmpName = lObjUpdateUserNameDaoImpl.getDataFromUserName(lStrUserName);
			
			if(lStrEmpName.equals("N") || lStrEmpName.equals("Invalid")){
				lStrFinal = "Invalid";
				lSBStatus = getResponseXMLDoc(lStrFinal, "").toString();
			}else{
				String []lStrData = lStrEmpName.split("#");
				String lStrEmployeeName = lStrData[0];
				String lStrLocation = lStrData[1];
				lSBStatus = getResponseXMLDoc(lStrEmployeeName, lStrLocation).toString();
			}
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in chkUserName");
		}
		
		return resObj;
	}
	
	public ResultObject updateUserName(Map<String, Object> inputMap)
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		
		try{
			UpdateUserNameDAOImpl lObjUpdateUserNameDaoImpl = new UpdateUserNameDAOImpl(OrgEmpMst.class,serv.getSessionFactory());
			String lStrOldUserName = StringUtility.getParameter("oldUserName", request);
			String lStrNewUserName = StringUtility.getParameter("newUserName", request);
			
			lObjUpdateUserNameDaoImpl.updateUserName(lStrOldUserName, lStrNewUserName);
			
			String lSBStatus = getResponseXMLDoc("Y", "").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in updateUserName");
		}
		
		return resObj;
	}
	
	public ResultObject chkNewUserName(Map<String, Object> inputMap)
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		
		try{
			UpdateUserNameDAOImpl lObjUpdateUserNameDaoImpl = new UpdateUserNameDAOImpl(OrgEmpMst.class,serv.getSessionFactory());
			String lStrUserName = StringUtility.getParameter("userName", request);
			
			String lStrResData = lObjUpdateUserNameDaoImpl.chkNewUserName(lStrUserName);
			
			String lSBStatus = getResponseXMLDoc(lStrResData, "").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in chkNewUserName");
		}
		
		return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrDdoName, String lStrDdoPrsnlName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DDONAME>");
		lStrBldXML.append(lStrDdoName);
		lStrBldXML.append("</DDONAME>");
		lStrBldXML.append("<DDOPNAME>");
		lStrBldXML.append(lStrDdoPrsnlName);
		lStrBldXML.append("</DDOPNAME>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
