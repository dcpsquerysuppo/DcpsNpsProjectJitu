package com.tcs.sgv.common.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;

import com.tcs.sgv.common.dao.ChangeUserNameDAOImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.valueobject.OrgUserMst;


public class changeUserNameServiceImpl extends ServiceImpl
{
	private final Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject getDataFromDdoCode(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		List lLstDdoLst = null;
		String lStrUserName = "";
		String lStrDdoName = "";
		String lStrDdoPrsnlName = "";
		Long lLngPostId = null;
		
		try{
			ChangeUserNameDAOImpl lObjChngUserName = new ChangeUserNameDAOImpl(OrgUserMst.class,serv.getSessionFactory());
			String lStrDdoCode = StringUtility.getParameter("ddoCode", request);
			
			if(lStrDdoCode.length() > 0){
				lLstDdoLst = lObjChngUserName.getDataFromDdoCode(lStrDdoCode);
			}
			if(!lLstDdoLst.isEmpty() && lLstDdoLst.size() > 0){
				lStrUserName = lObjChngUserName.getUnameFromDdoCode(lStrDdoCode);
				Object []lObj = (Object[]) lLstDdoLst.get(0);
				lStrDdoName = lObj[0].toString();
				lStrDdoPrsnlName = lObj[1].toString();
				lLngPostId = Long.parseLong(lObj[2].toString());
			}else{
				lStrDdoName = "Invalid";
			}
			
			inputMap.put("ddoName", lStrDdoName);
			inputMap.put("ddoPName", lStrDdoPrsnlName);
			inputMap.put("uName", lStrUserName);
			inputMap.put("ddoCode", lStrDdoCode);
			inputMap.put("postId", lLngPostId);
			//String lSBStatus = getResponseXMLDoc(lStrDdoName,lStrDdoPrsnlName,lStrUserName).toString();
			
			//String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			   
			//inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			//resObj.setViewName("ajaxData");
			resObj.setViewName("changeUserName");
		}
		catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFDataEntryForm");
		}
		return resObj;
	}
	
	public ResultObject checkNewUsrName(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		String lStrRes = "";
		String lStrDdoCode = "";
		String lStrDdoName = "";
		String lStrData[] = null;
		String lStrPostId = "";
		Long lLngPostId = null;
		Integer lIntIndx = null;
		String lSBStatus = "";
		
		try {
			ChangeUserNameDAOImpl lObjChngUserName = new ChangeUserNameDAOImpl(OrgUserMst.class,serv.getSessionFactory());
			String lStrNewUname = StringUtility.getParameter("newUname", request);
			lStrPostId = StringUtility.getParameter("postId", request);			
			
			if(!lStrPostId.equals("")){
				lLngPostId = Long.parseLong(lStrPostId);
			}
			
			if(lStrNewUname.length() > 0){
				lStrRes = lObjChngUserName.checkNewUserName(lStrNewUname,lLngPostId);
				if(!lStrRes.equals("N")){
					lStrData = lStrRes.split("#");
					if(lStrData[0].equals("Y")){
						if(lStrData[1].equals("Child")){
							lSBStatus = getResponseXMLDoc(lStrData[0],"Child","").toString();
						}else{
							lIntIndx = lStrData[1].indexOf("$");
							lStrDdoCode = lStrData[1].substring(0, lIntIndx);
							lStrDdoName = lStrData[1].substring(lIntIndx+1);
							lSBStatus = getResponseXMLDoc(lStrData[0],lStrDdoCode,lStrDdoName).toString();
						}
					}else if(lStrData[0].equals("Exist")){
						lSBStatus = getResponseXMLDoc(lStrData[0],lStrData[1],"").toString();
					}
				}else{
					lSBStatus = getResponseXMLDoc(lStrRes,"","").toString();
				}
			}					
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {			
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in updateUname");
		}
		return resObj;
	}
	
	public ResultObject updateUname(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		String lStrRes = "";
		Long lLongPostId = null;
		Long lLongUserId = null;
		String lStrSecondaryData = "";
		String []lStrData = null;
		String lStrUpdate = "";
		String lStrUpdtdUserName = "";
		
		try {
			ChangeUserNameDAOImpl lObjChngUserName = new ChangeUserNameDAOImpl(OrgUserMst.class,serv.getSessionFactory());
			String lStrOldUname = StringUtility.getParameter("oldUname", request).trim();
			String lStrNewUname = StringUtility.getParameter("newUname",request).trim();				
			String lStrDdoCode = StringUtility.getParameter("ddoCode",request).trim();
			
			lStrSecondaryData = lObjChngUserName.getUserIdPostIDFromDdoCode(lStrDdoCode);
			lStrData = lStrSecondaryData.split("#");
			lLongPostId = Long.parseLong(lStrData[0].toString());
			lLongUserId = Long.parseLong(lStrData[1].toString());
			
			lStrUpdate = lObjChngUserName.updateUname(lStrOldUname,lStrNewUname,lLongPostId,lLongUserId,lStrDdoCode);
			lStrData = lStrUpdate.split("#");
			
			if(lStrData[0].equals("Update")){
				lStrRes = "Success";
				lStrUpdtdUserName = lStrData[1];
			}else if(lStrData[0].equals("inactive")){
				lStrRes = "inactive";
			}
			String lSBStatus = getResponseXMLDoc(lStrRes,lStrUpdtdUserName,"").toString();
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {			
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in updateUname");
		}
				
		return resObj;
	}
	
	public ResultObject setPrimaryPost(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		String lStrPrimary = "";
		String lStrSecondry = "";
		String lStrRes = "";
		String[] lStrData = null;
		Long lLngPrimaryUsr = null;
		Long lLngSecondryUsr = null;
		Long lLngPrimaryPost = null;
		Long lLngSecondryPost = null;
		
		try {
			ChangeUserNameDAOImpl lObjChngUserName = new ChangeUserNameDAOImpl(OrgUserMst.class,serv.getSessionFactory());
			String lStrPrimDdoCode = StringUtility.getParameter("primaryDdo", request);
			String lStrScndDdoCode = StringUtility.getParameter("secondaryDdo", request);
			
			lStrPrimary = lObjChngUserName.getUserIdPostIDFromDdoCode(lStrPrimDdoCode);
			lStrSecondry = lObjChngUserName.getUserIdPostIDFromDdoCode(lStrScndDdoCode);
				
			lStrData = lStrPrimary.split("#");
			lLngPrimaryPost = Long.parseLong(lStrData[0].toString());
			lLngPrimaryUsr = Long.parseLong(lStrData[1].toString());
			
			lStrData = lStrSecondry.split("#");
			lLngSecondryPost = Long.parseLong(lStrData[0].toString());
			lLngSecondryUsr = Long.parseLong(lStrData[1].toString());
			
			lObjChngUserName.setPrimaryPost(lLngPrimaryUsr,lLngPrimaryPost,lLngSecondryUsr,lLngSecondryPost);
			
			lStrRes = "Y";
			String lSBStatus = getResponseXMLDoc(lStrRes,"","").toString();
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {			
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in updateUname");
		}
				
		return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrDdoName, String lStrDdoPrsnlName, String lStrUserName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DDONAME>");
		lStrBldXML.append(lStrDdoName);
		lStrBldXML.append("</DDONAME>");
		lStrBldXML.append("<DDOPNAME>");
		lStrBldXML.append(lStrDdoPrsnlName);
		lStrBldXML.append("</DDOPNAME>");
		lStrBldXML.append("<USERNAME>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(lStrUserName);
		lStrBldXML.append("]]>");
		lStrBldXML.append("</USERNAME>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
	

}
