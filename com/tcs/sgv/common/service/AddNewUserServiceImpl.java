package com.tcs.sgv.common.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.AddNewUserDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

public class AddNewUserServiceImpl extends ServiceImpl 
{
	private final Log gLogger = LogFactory.getLog(getClass());
	
	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;
	
	public ResultObject LoadNewUser(Map inputMap)
	{
		  HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		  ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		  ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		  List lLstTreasuryList = null;
		  List lLstRoles = null;
		  List lLstDesignation = null;
		  
		  try{
			  AddNewUserDAOImpl lObjNewUserDaoImpl = new AddNewUserDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			  lLstTreasuryList = lObjNewUserDaoImpl.getAllTreasury();
			  lLstRoles = lObjNewUserDaoImpl.getAllRoles();
			  lLstDesignation = lObjNewUserDaoImpl.getAllDesignation();
			  
			  String lStrUser = StringUtility.getParameter("user", request);
			  
			  inputMap.put("treasuryList", lLstTreasuryList);
			  inputMap.put("roleList", lLstRoles);
			  inputMap.put("dsgnList", lLstDesignation);
			  inputMap.put("user", lStrUser);
			  resObj.setResultValue(inputMap);
			  resObj.setViewName("AddNewUser");
		  }catch(Exception e){
				IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in LoadNewUser");
		  }
		  return resObj;
	}
	
	public ResultObject CheckSevaarthId(Map inputMap)
	{
		  HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		  ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		  ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		  List lLstTreasuryList = null;
		  List lLstRoles = null;
		  
		  try{
			  AddNewUserDAOImpl lObjNewUserDaoImpl = new AddNewUserDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			  String lStrSevaarthId = StringUtility.getParameter("sevaarth", request);
			  
			  String lStrChkSevaarth = lObjNewUserDaoImpl.chkSevaarthId(lStrSevaarthId);
			  String lSBStatus = getResponseXMLDoc(lStrChkSevaarth).toString();
			  String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
				
			  inputMap.put("ajaxKey", lStrResult);
			  resObj.setResultValue(inputMap);
			  resObj.setViewName("ajaxData");
		  }catch(Exception e){
			  IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in CheckSevaarthId");
		  }
		  return resObj;
	}
	
	public ResultObject AddNewUser(Map inputMap)
	{
		  HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		  ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		  ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		  Long lLngPostId = null;
		  String lStrLocationCode = "";
		  
		  try{
			  AddNewUserDAOImpl lObjNewUserDaoImpl = new AddNewUserDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			  gLngPostId = SessionHelper.getPostId(inputMap);			
			  gLngUserId = SessionHelper.getUserId(inputMap);
			  String lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request);
			  String lStrName = StringUtility.getParameter("txtName", request);
			  String lStrGender = StringUtility.getParameter("gender", request);
			  String lStrSalutation = StringUtility.getParameter("salutation", request);			  
			  Long lLngDsgnId = Long.parseLong(StringUtility.getParameter("cmbDesignation", request));
			  String lStrPostName = StringUtility.getParameter("txtPostName", request);
			  Long lLngRoleId = Long.parseLong(StringUtility.getParameter("cmbRoles", request));
			  String lStrDOB = StringUtility.getParameter("txtEmpDOB", request);
			  String lStrExistSevaarth = StringUtility.getParameter("hidExistSevaarth", request);
			  String lStrUser = StringUtility.getParameter("hidUser", request);
			  
			  if(lStrUser.equals("ATO")){
				  lStrLocationCode = SessionHelper.getLocationCode(inputMap);
			  }else if(lStrUser.equals("State_Admin")){
				  lStrLocationCode = StringUtility.getParameter("cmbTreasury", request);
			  }
			  
			  if(lStrExistSevaarth.equals("E")){
				  lObjNewUserDaoImpl.updateEmpUserName(lStrSevaarthId);
			  }
			  Long lLngUserId = lObjNewUserDaoImpl.insertUserMst(lStrSevaarthId, gLngUserId, gLngPostId, inputMap);
			  lLngPostId = new Long(lLngUserId);
			  lObjNewUserDaoImpl.insertEmpMst(lLngUserId, lStrName, gLngUserId, gLngPostId, lStrSalutation, lStrDOB, inputMap);
			  lObjNewUserDaoImpl.insertOrgPostMst(lLngUserId, lStrLocationCode, gLngUserId, gLngPostId, lLngDsgnId.toString(), inputMap);
			  lObjNewUserDaoImpl.insertPostDtlsRlt(lStrLocationCode, lLngPostId, lStrPostName, lLngDsgnId, gLngUserId, gLngPostId, inputMap);
			  lObjNewUserDaoImpl.insertPostRoleRlt(lLngPostId, gLngUserId, gLngPostId, lLngRoleId, inputMap);
			  lObjNewUserDaoImpl.insertUserPostRlt(lLngPostId, lLngUserId, gLngUserId, gLngPostId, inputMap);
			  
			  String lSBStatus = getResponseXMLDoc("true").toString();
			  String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			  
			  inputMap.put("ajaxKey", lStrResult);
			  resObj.setResultValue(inputMap);
			  resObj.setViewName("ajaxData");
		  }catch(Exception e){
			  IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in AddNewUser");
		  }
		  return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrSevaarth) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<SEVAARTH>");
		lStrBldXML.append(lStrSevaarth);
		lStrBldXML.append("</SEVAARTH>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
