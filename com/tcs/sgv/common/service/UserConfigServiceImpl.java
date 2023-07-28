package com.tcs.sgv.common.service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.fms.valueobject.WfHierachyPostMpg;
import com.tcs.sgv.common.dao.UserConfigDAOImpl;
/**
 * A class that implements reports related services 
 * 
 * @author Jayraj Chudasama
 */

public class UserConfigServiceImpl extends ServiceImpl
{
	  /* Global Variable for PostId */
	  private Long gLngPostId = null;

	  /* Global Variable for UserId */
	  private Long gLngUserId = null;

	  /* Global Variable for LangId */
	  private Long gLngLangId = null;

	  /* Global Variable for DB Id */
	  private Long gLngDBId = null;

	  /* Global Variable for Location Code */
	  private String gStrLocationCode = null;
	  /* Global variable for financial year id */
	  private Integer gIntFinYrId = null;
	  
	  /*Global variable for lang name*/
	  private String gStrLangName = null;
	  
	  private final Log gLogger = LogFactory.getLog(getClass());
	  
	  public IFMSCommonServiceImpl lObjIFMSCommonServiceImpl = new IFMSCommonServiceImpl();
	  
	  private static SimpleDateFormat oSimpleFormat = new SimpleDateFormat("dd/MM/yyyy");
	  static NumberFormat objNumFormat =  NumberFormat.getInstance();
	  static
	  {
		  objNumFormat.setMinimumFractionDigits(2);
		  objNumFormat.setMaximumFractionDigits(2);
		  objNumFormat.setGroupingUsed(false);
	  }
	 
	  public ResultObject loadUserConfig(Map inputMap)
	  {
		  HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		  ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		  ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		  List lLstDept = null;
		  List lLstDist = null;
		  
		  try{
			  UserConfigDAOImpl lObjUserConfigDAOImpl = new UserConfigDAOImpl(null,serv.getSessionFactory());
			  
			  lLstDept = lObjUserConfigDAOImpl.getAllDepartment();
			  lLstDist = lObjUserConfigDAOImpl.getAllDistrict();
			  
			  inputMap.put("AllDept", lLstDept);
			  inputMap.put("AllDistrict", lLstDist);
			  
			  resObj.setResultValue(inputMap);
			  resObj.setViewName("gpfUserMapping");
		  }catch(Exception e){
				IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFDataEntryForm");
			}
		return resObj;
	  }
	  
	  public ResultObject getAllLocation(Map objectArgs)
		{
			 ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, DBConstants.CONST_FAIL);
			  ServiceLocator serv = (ServiceLocator)objectArgs.get(DBConstants.SERV_LOCATOR);
			  HttpServletRequest request = (HttpServletRequest) objectArgs.get(DBConstants.REQUEST_OBJ);
			  String module ="";
			  Long lLngDistrictId = null;
			  List lLstAllLocs = null;
			  
			  try
			  {
				  if (objRes == null || objectArgs == null )
			      {				
			        objRes.setResultCode(-1);
			        return objRes; 
			      }
				  setSessionInfo(objectArgs);
				  
				  String OfficeCode = StringUtility.getParameter("officeCode",request);
				  if(!OfficeCode.equals("Office")){
					  
					  String dist = StringUtility.getParameter("districtId",request);
					  if(dist != null && dist != ""){
						  lLngDistrictId = Long.parseLong(StringUtility.getParameter("districtId",request));
					  }
					  
					  UserConfigDAOImpl userDAO = new UserConfigDAOImpl(WfHierachyPostMpg.class,serv.getSessionFactory());
					  Long langId = SessionHelper.getLangId(objectArgs);				  
					  
					  if(OfficeCode.equals("100003") || OfficeCode.equals("100007")){				  
						  lLstAllLocs = userDAO.getAllLocations(langId,"100003");
					  }
					  	
					  String lStrTempResult = null;
						if (lLstAllLocs != null) {
							lStrTempResult = new AjaxXmlBuilder().addItems(lLstAllLocs, "desc", "id", true).toString();
						}
						objectArgs.put("ajaxKey", lStrTempResult);
						objRes.setResultValue(objectArgs);
						objRes.setViewName("ajaxData");
				  }
			  }
			  catch(Exception ex)
			  {
				objRes.setThrowable(ex);
				objRes.setResultCode(-1);
				objRes.setViewName("errorPage");
		        gLogger.error("Error in UserConfigServiceImpl.getAllLocation"+ex,ex);
			  }
			  return objRes;
		}
	  
	  public ResultObject getAllSubTreasury(Map objectArgs)
	  {
		  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, DBConstants.CONST_FAIL);
		  ServiceLocator serv = (ServiceLocator)objectArgs.get(DBConstants.SERV_LOCATOR);
		  HttpServletRequest request = (HttpServletRequest) objectArgs.get(DBConstants.REQUEST_OBJ);
		  String module ="";
		  Long lLngDistrictId = null;
		  List lLstAllDdoCode = null;
		  
		  try
		  {
			  UserConfigDAOImpl userDAO = new UserConfigDAOImpl(WfHierachyPostMpg.class,serv.getSessionFactory());
			  
			  if (objRes == null || objectArgs == null )
		      {				
		        objRes.setResultCode(-1);
		        return objRes; 
		      }
			  setSessionInfo(objectArgs);
			  
			  String treasuryCode = StringUtility.getParameter("trCode",request);	
			  String Department = StringUtility.getParameter("officeCode",request);
				  
			  if(!treasuryCode.equals("") && Department.equals("100007")){
				  lLstAllDdoCode = userDAO.getAllSubTreasury(treasuryCode);
			  }
			  	
			  String lStrTempResult = null;
				if (lLstAllDdoCode != null) {
					lStrTempResult = new AjaxXmlBuilder().addItems(lLstAllDdoCode, "desc", "id", true).toString();
				}
				objectArgs.put("ajaxKey", lStrTempResult);
				objRes.setResultValue(objectArgs);
				objRes.setViewName("ajaxData");
		  }
		  catch(Exception ex)
		  {
			objRes.setThrowable(ex);
			objRes.setResultCode(-1);
			objRes.setViewName("errorPage");
	        gLogger.error("Error in UserConfigServiceImpl.getAllSubTreasury"+ex,ex);
		  }
		  return objRes;
	  }
	  
	  public ResultObject getAllDdoCode(Map objectArgs)
	  {
			 ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, DBConstants.CONST_FAIL);
			  ServiceLocator serv = (ServiceLocator)objectArgs.get(DBConstants.SERV_LOCATOR);
			  HttpServletRequest request = (HttpServletRequest) objectArgs.get(DBConstants.REQUEST_OBJ);
			  String module ="";
			  Long lLngDistrictId = null;
			  List lLstAllDdoCode = null;
			  
			  try
			  {
				  UserConfigDAOImpl userDAO = new UserConfigDAOImpl(WfHierachyPostMpg.class,serv.getSessionFactory());
				  
				  if (objRes == null || objectArgs == null )
			      {				
			        objRes.setResultCode(-1);
			        return objRes; 
			      }
				  setSessionInfo(objectArgs);
				  
				  String locCode = StringUtility.getParameter("locCode",request);				  
					  
				  if(!locCode.equals("")){
					  lLstAllDdoCode = userDAO.getAllDdoCode(locCode);
				  }
				  	
				  String lStrTempResult = null;
					if (lLstAllDdoCode != null) {
						lStrTempResult = new AjaxXmlBuilder().addItems(lLstAllDdoCode, "desc", "id", true).toString();
					}
					objectArgs.put("ajaxKey", lStrTempResult);
					objRes.setResultValue(objectArgs);
					objRes.setViewName("ajaxData");
			  }
			  catch(Exception ex)
			  {
				objRes.setThrowable(ex);
				objRes.setResultCode(-1);
				objRes.setViewName("errorPage");
		        gLogger.error("Error in UserConfigServiceImpl.getAllDdoCode"+ex,ex);
			  }
			  return objRes;
	  }
	  
	  
	  //getAllDataForUsers
	public ResultObject getAllUsersAndLevelForDesc(Map objectArgs)
	  {
		  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, DBConstants.CONST_FAIL);
		  ServiceLocator serv = (ServiceLocator)objectArgs.get(DBConstants.SERV_LOCATOR);
		  HttpServletRequest request = (HttpServletRequest) objectArgs.get(DBConstants.REQUEST_OBJ);		  
		  List<Long> lLstRoleId = new ArrayList<Long>();
		  Long lLngRoleId = null;
		  Integer lIntRoleId = null;
		  try
		  {
			  if (objRes==null || objectArgs == null )
		      {				
		        objRes.setResultCode(-1);
		        return objRes; 
		      }
		      setSessionInfo(objectArgs);
		      
		      String moduleName = StringUtility.getParameter("module", request);
		      String locCode = StringUtility.getParameter("Loc", request);
		      String ddoCode = StringUtility.getParameter("ddoCode",request);
		      String lStrDept = StringUtility.getParameter("Dept", request);
			  
			  if(!moduleName.equals(""))
			  {
				  UserConfigDAOImpl userDAO = new UserConfigDAOImpl(null,serv.getSessionFactory());
				 				  				  
				  Long langId = SessionHelper.getLangId(objectArgs);
				  
				  // to get all user names
				  List lLstRoles = userDAO.getAllRolesModuleWise(moduleName,lStrDept);
				  
				  for(int lCnt=0;lCnt<lLstRoles.size();lCnt++){
					  lIntRoleId = (Integer) lLstRoles.get(lCnt);
					  lLngRoleId = Long.parseLong(lIntRoleId.toString());
					  lLstRoleId.add(lLngRoleId);
				  }
				  String resUserList = userDAO.getAllUserData(locCode,langId,lLstRoleId,ddoCode,moduleName,lStrDept);
		  
				  // to get user names and level id and desc
				  String resUserLevel = "";
				  
				  resUserLevel = userDAO.getAllUserLevelData(moduleName,lStrDept);
				   
				   String finalData= resUserList+"#"+resUserLevel;
				   
				   String lSBStatus = getResponseXMLDoc(finalData).toString();
					
				   String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
				   
				   objectArgs.put("ajaxKey", lStrResult);
				   objRes.setResultValue(objectArgs);
				   objRes.setViewName("ajaxData");
			}			
				   
		  }
		  catch(Exception ex)
		  {
		  	objRes.setThrowable(ex);
			objRes.setResultCode(-1);
			objRes.setViewName("errorPage");
	       gLogger.error("Error in UserConfigServiceImpl.getAllUsersAndLevelForDesc"+ex,ex);
		  }
		  return objRes;
	  }

	private StringBuilder getResponseXMLDoc(String lStrData) {
	
		StringBuilder lStrBldXML = new StringBuilder();
	
		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DATA>");
		lStrBldXML.append(lStrData);
		lStrBldXML.append("</DATA>");		
		lStrBldXML.append("</XMLDOC>");
	
		return lStrBldXML;
	}
	public ResultObject getAllLevelForUser(Map objectArgs)
	  {
		  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, DBConstants.CONST_FAIL);
		  ServiceLocator serv = (ServiceLocator)objectArgs.get(DBConstants.SERV_LOCATOR);
		  HttpServletRequest request = (HttpServletRequest) objectArgs.get(DBConstants.REQUEST_OBJ);
		
		  try
		  {
			  if (objRes==null || objectArgs == null )
		      {				
		        objRes.setResultCode(-1);
		        return objRes; 
		      }
		      setSessionInfo(objectArgs);
		      			 
			  UserConfigDAOImpl userDAO = new UserConfigDAOImpl(WfHierachyPostMpg.class,serv.getSessionFactory());
			  
			  Long langId = SessionHelper.getLangId(objectArgs);
			  String postId = request.getParameter("postId");
			  String module = request.getParameter("module");
			  
			  //to get the levels mapped to a user
			   String resLevelForUser = userDAO.getAllLevelForUser(postId,module);
			   
			  String finalData= resLevelForUser;
			  String lSBStatus = getResponseXMLDoc(finalData).toString();			
				
			  String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			  
			  objectArgs.put("ajaxKey", lStrResult);
			  objRes.setResultValue(objectArgs);
			  objRes.setViewName("ajaxData");
		  }
		  catch(Exception ex)
		  {
			objRes.setThrowable(ex);
			objRes.setResultCode(-1);
			objRes.setViewName("errorPage");
	       gLogger.error("Error in UserConfigServiceImpl.getAllLevelForUser"+ex,ex);
		  }
		  return objRes;
	  }
	
	
	public ResultObject insertDataForUserLevel(Map objectArgs)
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, DBConstants.CONST_FAIL);
		ServiceLocator serv = (ServiceLocator)objectArgs.get(DBConstants.SERV_LOCATOR);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get(DBConstants.REQUEST_OBJ);		
		String[] workFlow = null;
		String lStrTempRole = "";
		String w_type = "";
		ArrayList tempArr = null;
		ArrayList selectedRoleId = new ArrayList();
		ArrayList roleIntArr = new ArrayList();
		List levelIdArr = null;
		List<Long> lLstRoleId = new ArrayList<Long>();
		Long lLngRoleId = null;
		Integer lIntRoleId = null;
		String retLevelStr ="";
		String lStrfinal = "";
		String uType = "";
		Integer indx = null;
		
		try{
			if(objRes == null || objectArgs == null )
		    {				
		       objRes.setResultCode(-1);
		       return objRes; 
		    }
		      setSessionInfo(objectArgs);
		      UserConfigDAOImpl userDAO = new UserConfigDAOImpl(WfHierachyPostMpg.class,serv.getSessionFactory());
		      
		      String PostId = StringUtility.getParameter("pId", request);		      
		      String userLevelId = StringUtility.getParameter("userLevelId",request);
		      String allRoles = StringUtility.getParameter("allRoles",request);
		      String Dept = StringUtility.getParameter("Dept",request);
		      String Desc = StringUtility.getParameter("Desc",request);
			  String[] levelId = userLevelId.split("~");
			  String[] allRolesArr = allRoles.split("~");
			  uType = StringUtility.getParameter("uType", request);
			  String lStrDdoCode = StringUtility.getParameter("ddoCode", request);
			  String lStrSubStrCode = StringUtility.getParameter("subStrCode",request);
			  
			  /*List lLstRoles = userDAO.getAllRolesModuleWise(Desc,Dept);
			  
			  for(int lCnt=0;lCnt<lLstRoles.size();lCnt++){
				  lIntRoleId = (Integer) lLstRoles.get(lCnt);
				  lLngRoleId = Long.parseLong(lIntRoleId.toString());
				  lLstRoleId.add(lLngRoleId);
			  }*/
			  
			  if(allRoles != null && !allRoles.equals("")){
				  w_type = allRolesArr[0].substring(allRolesArr[0].length()-1);
				  for(int i=0; i< allRolesArr.length; i++)
				  {
					  indx = allRolesArr[i].indexOf('$');
					  lLstRoleId.add(Long.parseLong(allRolesArr[i].substring(0, indx)));
				  }
			  }			  
			  
			  if(!"".equals(userLevelId) && userLevelId != null)
			  {
				  tempArr = new ArrayList();
				  w_type = levelId[0].substring(levelId[0].length()-1);
				  for(int i=0; i< levelId.length;i++)
				  {
					  workFlow = levelId[i].split("$");
					  indx = levelId[i].indexOf('$');
					  tempArr.add(Long.parseLong(workFlow[0].substring(0, indx)));
				  }				  
			  }
			  
			  if(tempArr != null){
				  selectedRoleId = (ArrayList) tempArr.clone();
			  }
			  
			  retLevelStr = userDAO.insertDataForPost(PostId, Desc, gLngLangId, selectedRoleId, objectArgs, lLstRoleId ,w_type,gLngPostId,gLngUserId,uType,lStrSubStrCode);
			  
			 /* if(w_type.equals("Y")){
				  if(tempArr != null && tempArr.size() > 0){
					  for(int lIntCnt=0;lIntCnt<tempArr.size();lIntCnt++)
					  {
						  roleIntArr.add(Integer.parseInt(tempArr.get(lIntCnt).toString()));
					  }
					  levelIdArr = userDAO.getLevelIdForRoles(roleIntArr);
				  }
				  retLevelStr = userDAO.insertDataForUserLevel(lStrSubStrCode,PostId, Desc,gLngLangId,levelIdArr,objectArgs,gLngUserId,gLngPostId,uType,lStrDdoCode);
			  }*/
			  
			  IFMSCommonServiceImpl lObjIFMSCommonServiceImpl = new IFMSCommonServiceImpl();
			  lObjIFMSCommonServiceImpl.clearAllCache(serv.getSessionFactory());
			  lObjIFMSCommonServiceImpl.clearAllCache(serv.getSessionFactorySlave());
			  
			  if(retLevelStr.contains("I") && retLevelStr.contains("D")){
				  lStrfinal = "Role(s) have been assigned to user and Role(s) have been removed from user";
			  }else if(retLevelStr.contains("I")){
				  lStrfinal = "Role(s) have been assigned to user";
			  }else if(retLevelStr.contains("D")){
				  lStrfinal = "Role(s) have been removed from user";
			  }else{
				  lStrfinal = "No Role is mapped or removed from user";
			  }
			  String lSBStatus = getResponseXMLDoc(lStrfinal).toString();
				
			  String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			  
			  objectArgs.put("ajaxKey", lStrResult);
			  objRes.setResultValue(objectArgs);
			  objRes.setViewName("ajaxData");
		}catch(Exception e){
			objRes.setThrowable(e);
			objRes.setResultCode(-1);
			objRes.setViewName("errorPage");
	       gLogger.error("Error in UserConfigServiceImpl.insertDataForUserLevel"+e,e);
		}
		
		return objRes;
	}
	
	public ResultObject getUserDtls(Map objectArgs)
	  {
		  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, DBConstants.CONST_FAIL);
		  ServiceLocator serv = (ServiceLocator)objectArgs.get(DBConstants.SERV_LOCATOR);
		  HttpServletRequest request = (HttpServletRequest) objectArgs.get(DBConstants.REQUEST_OBJ);
		  //String locCode = SessionHelper.getLocationCode(objectArgs);
		  String locCode = request.getParameter("locCode").toString();
		  
		  try
		  {			  
		      setSessionInfo(objectArgs);		      
		      
		      Long langId = SessionHelper.getLangId(objectArgs);
		      
		      UserConfigDAOImpl userDAO = new UserConfigDAOImpl(WfHierachyPostMpg.class,serv.getSessionFactory());
		      
		      String userName= request.getParameter("userName").toString();
		      String resUNamePid = userDAO.getUserDtls(userName,locCode,langId);
		      		      
		      String lSBStatus = getResponseXMLDoc(resUNamePid).toString();
				
			  String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			  
			  objectArgs.put("ajaxKey", lStrResult);
			  objRes.setResultValue(objectArgs);
			  objRes.setViewName("ajaxData");
			  
		  }catch (Exception ex)
		  {
				objRes.setThrowable(ex);
				objRes.setResultCode(-1);
				objRes.setViewName("errorPage");
		       gLogger.error("Error in UserConfigServiceImpl.getUserDtls"+ex,ex);
		}
		  return objRes;
	  }
	
	
	private void setSessionInfo(Map<String,Object> inputMap)
	 {
		  if(gLngLangId == null)
		  {
		      gLngLangId = SessionHelper.getLangId(inputMap);
		      gLngPostId = SessionHelper.getPostId(inputMap);
		      gLngUserId = SessionHelper.getUserId(inputMap);
		      gLngDBId = SessionHelper.getDbId(inputMap);
		      gStrLocationCode = SessionHelper.getLocationCode(inputMap);		     
		  }
	 }
		
}
