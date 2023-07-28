package com.tcs.sgv.gpf.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.RltLevelRole;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFWorkFlowConfigDAO;
import com.tcs.sgv.gpf.dao.GPFWorkFlowConfigDAOImpl;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 12, 2012
 */

public class GPFWorkFlowConfigServiceImpl extends ServiceImpl implements GPFWorkFlowConfigService
{
	private final Log gLogger = LogFactory.getLog(getClass());

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */


	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;		

	private void setSessionInfo(Map inputMap)
	{
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");			
			serv = (ServiceLocator) inputMap.get("serviceLocator");									
			gLngPostId = SessionHelper.getPostId(inputMap);	

			gLngUserId = SessionHelper.getUserId(inputMap);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);


		} catch (Exception e) {
			gLogger.error("Error is: "+ e, e);
		}
	}

	public ResultObject loadGPFWorkFlowConfig(Map<String, Object> inputMap) 
	{

		gLogger.info("loadGPFWorkFlowConfig service called");
		gLogger.error("gLngPostId"+gLngPostId);
		gLogger.error("gLngUserId"+gLngUserId);
		gLogger.error("gLngLangId "+gLngLangId);
		gLogger.error("gStrLocationCode"+gStrLocationCode);

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		List lLstAllRoles = null;
		List lLstAllUsers = null;
		String lStrDdoCode = "";
		String lStrHODtls = null;
		List lListVerDtls = null;
		List lListdeoDtls = null;
		List lLstHoDtls = null;
		List CurrentRhoAsst=null;
		List lLstActRole = null;

		int x = 0;
		int y = 0;
		int z=0;
		int activeVerfier=0;
		int activeDeo=0;
		int activeRghoAst=0;
		int activeHo=0;
		int actDeo=0;
		
		
		//if
		try{

			//if( gStrLocationCode.equalsIgnoreCase("380001") ){

			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			lStrDdoCode = lObjGpfWorkFlowConfig.getDdoCode(gLngPostId);
			lLstAllUsers = lObjGpfWorkFlowConfig.getAllUsersForDDO(lStrDdoCode,gStrLocationCode);
			lLstAllRoles = lObjGpfWorkFlowConfig.getAllRoles();
			//lStrHODtls = lObjGpfWorkFlowConfig.getActiveHOLst(gStrLocationCode);

			//current verifier
			lListVerDtls = lObjGpfWorkFlowConfig.getCurrentVerifiers(gStrLocationCode);
			//current verifier

			lListdeoDtls = lObjGpfWorkFlowConfig.getCurrentDEO(gStrLocationCode);
			//current ho
			lLstHoDtls = lObjGpfWorkFlowConfig.getActiveHOLst(gStrLocationCode);

			//Current RHO asst
			CurrentRhoAsst=lObjGpfWorkFlowConfig.getActiveRhoAsstLst(gStrLocationCode);
			
			lLstActRole = lObjGpfWorkFlowConfig.getActiveFlagRole(gLngPostId);   // Added by vivek 31 jan 2017
			
			//get deo ver ho rhoasst i sactive or not by its list size
			
			if(lListVerDtls !=null && lListVerDtls.get(0)!=null && lListVerDtls.size()>0)
				activeVerfier=lListVerDtls.size();
			else
				activeVerfier=0;
			
			
			
			if(lLstHoDtls !=null && lLstHoDtls.get(0)!=null && lLstHoDtls.size()>0)
				activeHo=lLstHoDtls.size();
			else
				activeHo=0;
			
			
			
			if(CurrentRhoAsst !=null && CurrentRhoAsst.get(0)!=null && CurrentRhoAsst.size()>0)
				activeRghoAst=CurrentRhoAsst.size();
			else
				activeRghoAst=0;


            //Commented By Vivek 19 June 2017			
			/*if(lListdeoDtls !=null && lListdeoDtls.get(0)!=null && lListdeoDtls.size()>0){
				activeDeo=lListdeoDtls.size();
			gLogger.info("activeDeo***********************"+activeDeo);
			}
			else{
				activeDeo=0;
			}*/
			
			activeDeo=0;
			//Ended By Vivek 19 June 2017
			// x = lLstAllUsers.size();
			if(lLstAllUsers !=null && lLstAllUsers.get(0)!=null && lLstAllUsers.size()>0)
				x=lLstAllUsers.size();
			else
				x=0;



			// y = lLstAllRoles.size();
			if(lLstAllRoles !=null && lLstAllRoles.get(0)!=null && lLstAllRoles.size()>0)
			{	y=lLstAllRoles.size();
				gLogger.info("1234567890"+lLstAllRoles.get(0).toString());
				gLogger.info(lLstAllRoles.get(2).toString());
				gLogger.info(lLstAllRoles.get(3).toString());
			}
			else
				y=0;



			


			gLogger.info("lListVerDtls"+lListVerDtls);
			gLogger.info("lListdeoDtls"+lListdeoDtls);
			gLogger.info("lLstHoDtls"+lLstHoDtls);


			gLogger.info("lStrDdoCode"+lStrDdoCode);
			gLogger.info("lStrHODtls"+lStrHODtls);
			gLogger.info("x:::"+x);
			gLogger.info("y:::"+y);
			gLogger.info("Z:::"+z);

			//gLogger.info("lListVerDtls"+lListVerDtls.size());

			inputMap.put("AllHO",lLstHoDtls);
			inputMap.put("AllUsers", lLstAllUsers);
			inputMap.put("AllRoles", lLstAllRoles);
			inputMap.put("AllVerifiers", lListVerDtls);
			inputMap.put("AllDEO", lListdeoDtls);
			inputMap.put("AllRHOAST", CurrentRhoAsst);

			
			
			
			inputMap.put("activeVerfier", activeVerfier);
			inputMap.put("activeDeo", activeDeo);
			inputMap.put("activeRghoAst", activeRghoAst);

			
			
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("WorkFlowConfigGPF");

		/*	}
			
			else
			{
				inputMap.put("msg", "This functionality doesnot exist for this ddo.");
				resObj.setResultValue(inputMap);
				resObj.setViewName("WorkFlowConfigGPF");
			}
*/
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFWorkFlowConfig");
		}
		return resObj;
	}
	
	
	public ResultObject getAllRolesForUsers(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);		

		String lStrAllRoles = "";
		Long lLngPostId = null;
		try{
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());

			lLngPostId = Long.parseLong(StringUtility.getParameter("postId",request));

			lStrAllRoles = lObjGpfWorkFlowConfig.getAllRolesForUser(lLngPostId);
			String lSBStatus = getResponseXMLDoc(lStrAllRoles).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getAllRolesForUsers");
		}
		return resObj;
	}

	public ResultObject AddUserToWorkFlow(Map<String, Object> inputMap) 
	{

		gLogger.error("AddUserToWorkFlow service called");
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");

		String lStrSelectedRoles = "";
		String[] lArrStrSelctdRoles = null;
		Long lLngPostId = null;
		String userName="";
		String sevaarthId="";
		List<Long> lLstSelectedRoles = new ArrayList<Long>();
		List<Integer> lLstSlctdRolesInt = new ArrayList<Integer>();
		List levelIdArr = null;
		String lStrFinalData = "";
		String lSrtFinalMsg = "";
		String lStrVerifierFlag = "";
		String lStrHOFlag = "";
		String lStrRoleArr[] =  new String[3];
		String lStrHoRoleEntryFlag = "";
		String lStrHoEmpGrpEntryFlag = "";
		String chkRolesOfUser = "";
		Long lLngUserId = 0l;
		List lListActandRole = null;
		String activateFlagss="";
		String roleId="";
		
		try{
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			lLngPostId = Long.parseLong(StringUtility.getParameter("postId",request));
			gLogger.error("lLngPostId"+lLngPostId);

			lLngUserId = lObjGpfWorkFlowConfig.getUserIdFromPostId(lLngPostId);
			gLogger.error("lLngUserId"+lLngUserId);
			userName = lObjGpfWorkFlowConfig.getUsername(lLngUserId);
			gLogger.error("userName"+userName);			
			lStrSelectedRoles = StringUtility.getParameter("selectedRoles", request);			
			gLogger.error("lStrSelectedRoles"+lStrSelectedRoles);	
			
			lListActandRole = lObjGpfWorkFlowConfig.getActiveFlagRole(lLngPostId);        // Added by vivek 31 jan 2017
			// Added by vivek 31 jan 2017
			Iterator itC=lListActandRole.iterator();
			
			while(itC.hasNext()){
			Object[] tupleC = (Object[]) itC.next();
			activateFlagss=tupleC[0].toString();
			roleId=tupleC[1].toString();
			
			}
			
			inputMap.put("activateFlagss", activateFlagss);
			inputMap.put("roleId", roleId);
			
			gLogger.error("activateFlagss 29000 = "+activateFlagss);
			gLogger.error("roleId ***29000 = "+roleId);
			// ended by vivek 31 jan 2017
			if(!lStrSelectedRoles.equals("")){
				lArrStrSelctdRoles = lStrSelectedRoles.split(",");
             gLogger.error("lArrStrSelctdRoles******"+lArrStrSelctdRoles.length);
				for(Integer lIntCnt=0; lIntCnt < lArrStrSelctdRoles.length; lIntCnt++){
					
					gLogger.error("lArrStrSelctdRoles[lIntCnt]************  "+lArrStrSelctdRoles[lIntCnt]+"  ******  "+lIntCnt+"  lArrStrSelctdRoles.length  "+lArrStrSelctdRoles.length);
					lLstSelectedRoles.add(Long.parseLong(lArrStrSelctdRoles[lIntCnt]));
					lLstSlctdRolesInt.add(Integer.parseInt(lArrStrSelctdRoles[lIntCnt]));

					if(lArrStrSelctdRoles[lIntCnt].equals("800001")){
						gLogger.error(" In Verifier Field *************");
						lStrVerifierFlag = lObjGpfWorkFlowConfig.chkEntryForVerifier(gStrLocationCode);		

						gLogger.error("lStrVerifierFlag"+lStrVerifierFlag);	
						lStrRoleArr[lIntCnt] = "Verifier";
					}
					if(lArrStrSelctdRoles[lIntCnt].equals("800002")){
						gLogger.error(" In Ho Field *************");	
						lStrHOFlag = lObjGpfWorkFlowConfig.chkEntryForHO(gStrLocationCode);	
						gLogger.error("lStrHOFlag"+lStrHOFlag);	
						lStrHoRoleEntryFlag = lObjGpfWorkFlowConfig.chkRoleHoEntry(lLngPostId,gStrLocationCode);
						gLogger.error("lStrHoRoleEntryFlag"+lStrHoRoleEntryFlag);	
						lStrRoleArr[lIntCnt] = "HO";
						String lStrDdoCode = lObjGpfWorkFlowConfig.getDdoCode(gLngPostId);
						lStrHoEmpGrpEntryFlag =lObjGpfWorkFlowConfig.chkEmpGroupHoEntry(lLngPostId,lStrDdoCode);
						gLogger.error("lStrHoEmpGrpEntryFlag"+lStrHoEmpGrpEntryFlag);	
					}
					if(lArrStrSelctdRoles[lIntCnt].equals("800005")){
						gLogger.error(" In DEO Field *************");
						lStrRoleArr[lIntCnt] = "DEO";
					}
				}
			}
			// modified by aditya
			gLogger.error("lStrHoRoleEntryFlag"+lStrHoRoleEntryFlag);
			/*if(!lStrVerifierFlag.equals("Y") || !lStrHOFlag.equals("Y")){*/

			if((lStrHoEmpGrpEntryFlag.equals("Y") || lStrHoEmpGrpEntryFlag.equals("")) && (lStrHoRoleEntryFlag.equals("") || lStrHoRoleEntryFlag.equals("N")) && (activateFlagss =="" || activateFlagss.equalsIgnoreCase("1") || activateFlagss.equalsIgnoreCase("2")) )        // Conditioned (&& activateFlagss =="") Added by vivek 31 jan 2017

			{
				
				gLogger.error("lStrHoRoleEntryFlag::::::::::::::::::::::::::::::::::::"+lStrHoRoleEntryFlag);	
				lStrFinalData = lObjGpfWorkFlowConfig.assignRolesToUser(lLngPostId, lLstSelectedRoles, gLngUserId, gLngPostId, inputMap);
				gLogger.error("lStrFinalData344"+lStrFinalData);	
				if(lLstSlctdRolesInt != null && lLstSlctdRolesInt.size() > 0){
					levelIdArr = lObjGpfWorkFlowConfig.getLevelIdForRoles(lLstSlctdRolesInt);
					gLogger.error("levelIdArr"+levelIdArr);	
				}
				
				
				gLogger.error("gStrLocationCode::::::::::::::::::::::::::::::::::::"+gStrLocationCode);
				gLogger.error("lLngPostId::::::::::::::::::::::::::::::::::::"+lLngPostId);
				gLogger.error("gLngLangId::::::::::::::::::::::::::::::::::::"+gLngLangId);
				gLogger.error("levelIdArr::::::::::::::::::::::::::::::::::::"+levelIdArr);
				gLogger.error("gLngUserId::::::::::::::::::::::::::::::::::::"+gLngUserId);
				gLogger.error("gLngPostId::::::::::::::::::::::::::::::::::::"+gLngPostId);
				lObjGpfWorkFlowConfig.insertDataForWorkflow(gStrLocationCode, lLngPostId, gLngLangId, levelIdArr, inputMap, gLngUserId, gLngPostId);

				//String lStrRoleName ;
				gLogger.error("lArrStrSelctdRoles.length::::::::::::::::::::::::::::::::::::"+lArrStrSelctdRoles.length);
				if(lArrStrSelctdRoles != null)
					if(lArrStrSelctdRoles.length > 2)
						lSrtFinalMsg = "Role of "+lStrRoleArr[0]+", "+lStrRoleArr[1]+" and "+lStrRoleArr[2]+" has been assigned to user having his username as his sevaath ID "
						//+user[1] +" and Password: ifms123";
						+userName +" and Password: ifms123";
					else if(lArrStrSelctdRoles.length > 1)
						lSrtFinalMsg = "Role of "+lStrRoleArr[0]+" and "+lStrRoleArr[1] +" has been assigned to user having his username as his sevaath ID "
						//+user[1] +" and Password: ifms123";
						+userName +" and Password: ifms123";
					else if(lArrStrSelctdRoles.length > 0)
						lSrtFinalMsg = "Role of "+lStrRoleArr[0]+" has been assigned to user having his username as his sevaath ID "
						//+user[1] +" and Password: ifms123";
						+userName +" and Password: ifms123";

				if(lStrFinalData.contains("I") && lStrFinalData.contains("R")){	


					//lSrtFinalMsg = "Role(s) have been assigned to user and Role(s) have been removed from user";
				}else if(lStrFinalData.contains("I")){					
					//lSrtFinalMsg = "Role(s) have been assigned to user with USERNAME:" +user[1]+ "and Password: ifms123";
				}else
					if(lStrFinalData.contains("R")){


						lSrtFinalMsg = "Role(s) have been removed from user";
					}else{


						//lSrtFinalMsg = "No Role is mapped or removed from user";        //Commented Msz By Vivek 19 June 2017
						lSrtFinalMsg = " Role is already mapped ";
					}
			}
			else{
				gLogger.info(" ***lStrResult*** 394 ");
				lSrtFinalMsg = "Role has already assigned.";
			}
			/*}else{
				if(lStrVerifierFlag.equals("Y")){
					lSrtFinalMsg = "Verifier";
				}else if(lStrHOFlag.equals("Y")){
					lSrtFinalMsg = "HO";
				}
			}*/
			if(lStrHoRoleEntryFlag.equals("Y")){
				lSrtFinalMsg = "Role of HO is already assigned.";
				
			}
			//ADD BY KAVITA 
			if(lStrHoEmpGrpEntryFlag.equals("N") && lStrHoRoleEntryFlag.equals("N")){
				lSrtFinalMsg = "Only A and B group users are applicable for HO role.";
			}
			
			/*else if(lStrVerifierFlag.equals("Y")){
				lSrtFinalMsg = "Role of Verifier is already assigned.";
			}*/
			String lSBStatus = getResponseXMLDoc(lSrtFinalMsg).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			gLogger.info("lStrResult "+lStrResult);
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

			IFMSCommonServiceImpl lObjIFMSCommonServiceImpl = new IFMSCommonServiceImpl();
			lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactory());
			lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactorySlave());

			return resObj;
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFWorkFlowConfig");
		}
		return resObj;
	}

	private StringBuilder getResponseXMLDoc(String lStrData) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrData);
		lStrBldXML.append("</FLAG>");		
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject AddUserToWorkFlowMDC(Map<String, Object> inputMap) 
	{
		gLogger.error("AddUserToWorkFlowMDC service called");

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");

		String lStrSelectedRoles = "";
		String []lArrStrSelctdRoles = null;
		Long lLngPostId = null;
		String userName="";
		List<Long> lLstSelectedRoles = new ArrayList<Long>();
		List<Integer> lLstSlctdRolesInt = new ArrayList<Integer>();
		List levelIdArr = null;
		String lStrFinalData = "";
		String lSrtFinalMsg = "";
		String lStrVerifierFlag = "";
		String lStrHOFlag = "";
		String lStrRoleArr =  "";
		String lStrHoRoleEntryFlag = "";
		String chkRolesOfUser = "";
		String gStrLocationCodeMDC = "";
		try{
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());

			//Fetching values from javascript

			String lStrddocode = StringUtility.getParameter("ddocode", request);		
			gStrLocationCodeMDC = lObjGpfWorkFlowConfig.getLocationCode(lStrddocode);

			gLogger.error("lStrddocode"+lStrddocode);
			gLogger.error("gStrLocationCodeMDC"+gStrLocationCodeMDC);
			userName=StringUtility.getParameter("username",request);


			gLogger.error("userName"+userName);


			lLngPostId = Long.parseLong(lObjGpfWorkFlowConfig.getPostId(userName));
			gLogger.error("lLngPostId"+lLngPostId);

			lStrSelectedRoles = "800002";
			gLogger.error("lStrSelectedRoles"+lStrSelectedRoles);
			lLstSelectedRoles.add(Long.parseLong(lStrSelectedRoles));



			lStrHOFlag = lObjGpfWorkFlowConfig.chkEntryForHO(gStrLocationCodeMDC);										
			lStrHoRoleEntryFlag = lObjGpfWorkFlowConfig.chkRoleHoEntry(lLngPostId,gStrLocationCodeMDC);
			lStrRoleArr = "HO";

			/*if(lArrStrSelctdRoles[lIntCnt].equals("800005")){						
						lStrRoleArr[lIntCnt] = "DEO";
					}*/


			//modified by aditya

			/*if(!lStrVerifierFlag.equals("Y") || !lStrHOFlag.equals("Y")){*/

			if(lStrHoRoleEntryFlag.equals("") || lStrHoRoleEntryFlag.equals("N") ){
				lStrFinalData = lObjGpfWorkFlowConfig.assignRolesToUser(lLngPostId, lLstSelectedRoles, gLngUserId, gLngPostId, inputMap);

				if(lLstSlctdRolesInt != null && lLstSlctdRolesInt.size() > 0){
					levelIdArr = lObjGpfWorkFlowConfig.getLevelIdForRoles(lLstSlctdRolesInt);
				}

				lObjGpfWorkFlowConfig.insertDataForWorkflow(gStrLocationCodeMDC, lLngPostId, gLngLangId, levelIdArr, inputMap, gLngUserId, gLngPostId);

				//String lStrRoleName ;
				if(lStrFinalData != null && lStrFinalData != "")

					//if(lArrStrSelctdRoles.length > 0)
					lSrtFinalMsg = "Role of HO has been assigned to user having his username as his sevaath ID "
						+userName +" and Password: ifms123";

				if(lStrFinalData.contains("I") && lStrFinalData.contains("R")){					
					//lSrtFinalMsg = "Role(s) have been assigned to user and Role(s) have been removed from user";
				}

				else if(lStrFinalData.contains("I")){					
					//lSrtFinalMsg = "Role(s) have been assigned to user with USERNAME:" +user[1]+ "and Password: ifms123";
				}

				else
					if(lStrFinalData.contains("R")){
						lSrtFinalMsg = "Role(s) have been removed from user";
					}else{
						lSrtFinalMsg = "No Role is mapped or removed from user";
					}
			}
			/*}else{
				if(lStrVerifierFlag.equals("Y")){
					lSrtFinalMsg = "Verifier";
				}else if(lStrHOFlag.equals("Y")){
					lSrtFinalMsg = "HO";
				}
			}*/
			if(lStrHoRoleEntryFlag.equals("Y"))
				lSrtFinalMsg = "Role of HO is already assigned.";

			String lSBStatus = getResponseXMLDoc(lSrtFinalMsg).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			gLogger.error("lStrResult "+lStrResult);
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

			IFMSCommonServiceImpl lObjIFMSCommonServiceImpl = new IFMSCommonServiceImpl();
			lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactory());
			lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactorySlave());

			return resObj;
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFWorkFlowConfig");
		}
		return resObj;
	}
	
	

	private StringBuilder getResponseXMLDocMDC(String lStrData) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrData);
		lStrBldXML.append("</FLAG>");		
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject CheckPendingFlag(Map<String, Object> inputMap) {

		gLogger.error("CheckPendingFlag service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstSchemeName = null;

		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			String lStrUserName = StringUtility.getParameter("username", request);

			gLogger.error("lStrUserName"+lStrUserName);

			String lStrPostId = lObjGpfWorkFlowConfig.getPostId(lStrUserName);

			gLogger.error("lStrPostId"+lStrPostId);

			String lStrPendingFlag = lObjGpfWorkFlowConfig.getPendingFlag(lStrPostId);

			gLogger.error("lStrPendingFlag"+lStrPendingFlag);

			String temp = getPendingXMLDoc(lStrPendingFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();



			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}


	private StringBuilder getPendingXMLDoc(String lStrPendingFlag) {

		gLogger.error("getPendingXMLDoc called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrPendingFlag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject DeassignRole(Map<String, Object> inputMap) {

		gLogger.error("DeassignRole service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		int requestPending=0;
		int rere=0;
		String lStrDeassignFlag="";
		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			String lStrUserName1 = StringUtility.getParameter("username", request);
			int length = lStrUserName1.length();
			String lStrUserName = lStrUserName1.substring(1,length);

			gLogger.error("lStrUserName1"+lStrUserName1);
			gLogger.error("lStrUserName"+lStrUserName);

			String lStrPostId = lObjGpfWorkFlowConfig.getPostId(lStrUserName);

			gLogger.error("lStrPostId"+lStrPostId);
			
			//
			String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleIVER(String.valueOf(gStrLocationCode));
			//
            Long postRoleid=Long.parseLong(PostRoleId);
			//String lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleDEO(postRoleid);
			
            //Added by Sooraj
            //Checks if there any request is pending
            requestPending=lObjGpfWorkFlowConfig.checkPendingRequestVerifier(lStrPostId);
			gLogger.info("No of requests pending is"+requestPending);
			
			
			if(requestPending>0)
            {
            	rere=0;
            	lStrDeassignFlag=Integer.toString(rere);
            gLogger.info("value after operation"+lStrDeassignFlag);
            }
            else
            {
			lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleDEO(postRoleid);
            }
			//end
			String temp = getDeassignXML(lStrDeassignFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();


			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}

	private StringBuilder getDeassignXML(String lStrDeassignFlag) {

		gLogger.error("getDeassignXML called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrDeassignFlag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
//HO DEASSIGN METHOD
	public ResultObject CheckPendingFlagHO(Map<String, Object> inputMap) {

		gLogger.error("CheckPendingFlagHO service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstSchemeName = null;
		List lLstHoDtls=null;
		String lStrDeassignFlag="";
		int requestPending=0;
		 int rere=0;
		 String PostId="";
		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			//String lStrUserName = StringUtility.getParameter("username", request).trim();

			
			
			
			
			//PostId=lObjGpfWorkFlowConfig.getPostIDuser(lStrUserName); //ADD BY KAVITA
			
			String lStrUserName1 = StringUtility.getParameter("username", request);
			gLogger.error("lStrUserName111"+lStrUserName1);
			int length = lStrUserName1.length();
			String lStrUserName = lStrUserName1.substring(1,length);

			gLogger.error("lStrUserName1"+lStrUserName1);
			gLogger.error("lStrUserName"+lStrUserName);

			String lStrPostId = lObjGpfWorkFlowConfig.getPostId(lStrUserName);
			
			//Added bySooraj
			//request is pending or not
			requestPending=lObjGpfWorkFlowConfig.checkPendingRequestHO(lStrPostId);
			gLogger.info("No of requests pending is"+requestPending);
			//End-Sooraj
			
			//GET POSTROLE ID OF ACTIVE HO
			
			String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleHO(String.valueOf(gStrLocationCode));
		
            Long postRoleid=Long.parseLong(PostRoleId);
            
           // String postid=lObjGpfWorkFlowConfig.getPostIdforPending(lStrUserName);
            //gLogger.info("post_id is "+postid);
			//GET CURRENT HO
			
			//lLstHoDtls = lObjGpfWorkFlowConfig.getActiveHOLst(gStrLocationCode);

           
            
           /* if(lLstHoDtls !=null && lLstHoDtls.get(0)!=null && lLstHoDtls.size()>0)
            	lStrDeassignFlag="Y";
			else
				lStrDeassignFlag="N";
*/
            
            
			/*String lStrPostId = lObjGpfWorkFlowConfig.getPostId(lStrUserName);

			gLogger.error("lStrPostId"+lStrPostId);*/
            
            //Added by sooraj
            //checks if there any request is pending
            if(requestPending>0)
            {
            	rere=0;
            	lStrDeassignFlag=Integer.toString(rere);
            gLogger.info("value after operation"+lStrDeassignFlag);
            }
            else
            {
	        lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleDEO(postRoleid);
            }
            //End
			gLogger.error("lStrPendingFlag"+lStrDeassignFlag);

			String temp = getPendingXMLDocHO(lStrDeassignFlag).toString();
            
			gLogger.info("temp:::::::::"+temp);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();



			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");
            

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}


	private StringBuilder getPendingXMLDocHO(String lStrPendingFlag) {

		gLogger.error("getPendingXMLDocHO called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrPendingFlag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject DeassignRoleHO(Map<String, Object> inputMap) {

		gLogger.error("DeassignRoleHO service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");


		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			String lStrUserName1 = StringUtility.getParameter("username", request);
			int length = lStrUserName1.length();
			String lStrUserName = lStrUserName1.substring(1,length);

			gLogger.error("lStrUserName1"+lStrUserName1);
			gLogger.error("lStrUserName"+lStrUserName);

			String lStrPostId = lObjGpfWorkFlowConfig.getPostId(lStrUserName);

			String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleHO(String.valueOf(gStrLocationCode));
			
            Long postRoleid=Long.parseLong(PostRoleId);
			
			
			
			
			
			gLogger.error("lStrPostId"+lStrPostId);

			String lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleHO(postRoleid);

			String temp = getDeassignXMLHO(lStrDeassignFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();


			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}

	private StringBuilder getDeassignXMLHO(String lStrDeassignFlag) {

		gLogger.error("getDeassignXML called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrDeassignFlag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

//gokarna
	public ResultObject DeassignRoleDEO(Map<String, Object> inputMap) {

		gLogger.error("DeassignRoleDEO service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		int requestPendingUnderDEO=0;
		String lStrDeassignFlag="";
		int checkpendingDEOFlag=0;

		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			String lStrUserName1 = StringUtility.getParameter("username", request);
			int length = lStrUserName1.length();
			String lStrUserName = lStrUserName1.substring(1,length);

			gLogger.error("lStrUserName1"+lStrUserName1);
			gLogger.error("lStrUserName"+lStrUserName);
			gLogger.error("length"+length);
			gLogger.error("gStrLocationCode"+gStrLocationCode);

			String lStrPostId = lObjGpfWorkFlowConfig.getPostId(lStrUserName);

			gLogger.error("lStrPostId"+lStrPostId);
			
			//get current DEO and demap it
			requestPendingUnderDEO=lObjGpfWorkFlowConfig.checkPendingRequestDEO(lStrUserName);
			gLogger.info("Number of request Pending under ddo is"+requestPendingUnderDEO);
			
			String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleID(String.valueOf(gStrLocationCode));
			//
			Long postRoleid=Long.parseLong(PostRoleId);
			if(requestPendingUnderDEO>0)
			{
				lStrDeassignFlag=Integer.toString(checkpendingDEOFlag);
			}
			else
			{
			lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleDEO(postRoleid);
			}
			String temp = getDeassignXMLDEO(lStrDeassignFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();


			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}

	private StringBuilder getDeassignXMLDEO(String lStrDeassignFlag) {

		gLogger.error("getDeassignXMLDEO called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrDeassignFlag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject getPostId(Map<String, Object> inputMap) {

		gLogger.error("getPostId service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			String lStrUserName = StringUtility.getParameter("username", request);

			gLogger.error("lStrUserName"+lStrUserName);

			String lStrPostId = lObjGpfWorkFlowConfig.getPostId(lStrUserName);

			gLogger.error("lStrPostId"+lStrPostId);

			String temp = getPostIdXML(lStrPostId).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();



			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}

	private StringBuilder getPostIdXML(String lStrPostId) {

		gLogger.error("getPostIdXML called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<POST>");
		lStrBldXML.append(lStrPostId);
		lStrBldXML.append("</POST>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject validateDDOCode(Map<String, Object> inputMap) {

		gLogger.error("validateDDOCode service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			String lStrDDOCode = StringUtility.getParameter("ddocode", request);

			gLogger.error("lStrDDOCode"+lStrDDOCode);

			String lStrValidateDDOCodeFlag = lObjGpfWorkFlowConfig.getValidateDDOCodeFlag(lStrDDOCode);

			gLogger.error("lStrValidateDDOCodeFlag"+lStrValidateDDOCodeFlag);

			String temp = getValidateDDOCodeXML(lStrValidateDDOCodeFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();



			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}

	private StringBuilder getValidateDDOCodeXML(String lStrPostId) {

		gLogger.error("getValidateDDOCodeXML called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DDOCODE>");
		lStrBldXML.append(lStrPostId);
		lStrBldXML.append("</DDOCODE>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject validateUsername(Map<String, Object> inputMap) {

		gLogger.error("validateUsername service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			String lStrUsername = StringUtility.getParameter("username", request);


			gLogger.error("lStrUsername"+lStrUsername);


			String lStrValidateUsernameFlag = lObjGpfWorkFlowConfig.getValidateUsernameFlag(lStrUsername);

			gLogger.error("lStrValidateUsernameFlag"+lStrValidateUsernameFlag);

			String temp = getValidateUsernameXML(lStrValidateUsernameFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();



			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}

	private StringBuilder getValidateUsernameXML(String lStrValidateUsernameFlag) {

		gLogger.error("getValidateUsernameXML called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<USERNAME>");
		lStrBldXML.append(lStrValidateUsernameFlag);
		lStrBldXML.append("</USERNAME>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject CheckExistingHO(Map<String, Object> inputMap) {

		gLogger.error("CheckExistingHO service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			String lStrddocode = StringUtility.getParameter("ddocode", request);
			gLogger.error("lStrddocode"+lStrddocode);

			String lStrLocationCode = lObjGpfWorkFlowConfig.getLocationCode(lStrddocode);
			gLogger.error("lStrLocationCode"+lStrLocationCode);


			String lStrCheckhoFlag = lObjGpfWorkFlowConfig.chkEntryForHOMDC(lStrLocationCode);

			gLogger.error("lStrCheckhoFlag"+lStrCheckhoFlag);

			String temp = getCheckExistingHOXML(lStrCheckhoFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();



			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}

	private StringBuilder getCheckExistingHOXML(String lStrCheckhoFlag) {

		gLogger.error("getCheckExistingHOXML called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<HOFLAG>");
		lStrBldXML.append(lStrCheckhoFlag);
		lStrBldXML.append("</HOFLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
	//gokarna newww

	public ResultObject addRhoAsstToGpf(Map<String, Object> inputMap) {

		gLogger.error("*********addRhoAsstToGpf***********");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstSchemeName = null;
		List lListActandRole = null;
		String activateFlagRho = "";
		String roleIdRho = "";
		String roleNameRho = "";
		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());
			long lLngPostId = Long.parseLong(StringUtility.getParameter("postId",request));                          // Added by vivek 31 jan 2017
			String SelectedRole = StringUtility.getParameter("selectedRoles", request);
			String lStrPostId = StringUtility.getParameter("postId", request);		
			gLogger.error("SelectedRole**********"+SelectedRole);
			gLogger.error("Postid ***********"+lStrPostId);
			gLogger.error("lLngPostId ***********"+lLngPostId);
			Long Postid=Long.parseLong(lStrPostId);
			//lObjGpfWorkFlowConfig.InsertPostForRhoAsst1(Postid);
             lListActandRole = lObjGpfWorkFlowConfig.getActiveFlagRole(lLngPostId);                            // Added by vivek 31 jan 2017
             // Added by vivek 31 jan 2017
			Iterator itC=lListActandRole.iterator();                                    
			while(itC.hasNext()){
		    gLogger.error("while 1121 = ");
			Object[] tupleC = (Object[]) itC.next();
			activateFlagRho=tupleC[0].toString();
			roleNameRho=tupleC[1].toString();
			roleIdRho=tupleC[2].toString();                                      // Added by vivek 17 jun 2017
			gLogger.error("activateFlagRho WHILE = "+activateFlagRho);
			gLogger.error("roleIdRho ***WHILE = "+roleNameRho);
			}
			inputMap.put("activateFlagRho", activateFlagRho);
			inputMap.put("roleNameRho", roleNameRho);
			inputMap.put("roleIdRho", roleIdRho);
			
			gLogger.error("activateFlagRho 29000 = "+activateFlagRho);
			gLogger.error("roleIdRho ***29000 = "+roleNameRho);
			gLogger.error("inserted Successfullyy::::::::::::"+lStrPostId);
			 // ended by vivek 31 jan 2017
           if((activateFlagRho == "") || ((activateFlagRho.equalsIgnoreCase("1") || activateFlagRho.equalsIgnoreCase("2")) && !roleIdRho.equalsIgnoreCase("8000016"))){                                            // Condition Added by vivek 31 jan 2017
        	   lObjGpfWorkFlowConfig.InsertPostForRhoAsst1(Postid);
			String Flag="true";
			String temp = getPendingXMLDocRho(Flag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();
			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");
           }
           else{                                                                                     //Else Condition added by Vivek 17 June 2017
        	   gLogger.error("activateFlagRho 1152 ");
        	   lObjGpfWorkFlowConfig.reAssignRoleRHOAST(Postid);
   			String Flag="true";
   			String temp = getPendingXMLDocRho(Flag).toString();
   			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();
   			inputMap.put("ajaxKey", lStrResult);
   			lObjResultObj.setResultValue(inputMap);
   			lObjResultObj.setViewName("ajaxData");
   			gLogger.error("service complete");
           }

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}


	private StringBuilder getPendingXMLDocRho(String Flag) {

		gLogger.error("getPendingXMLDoc called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(Flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
	//deassign role of RHO ASST-------------
	
	public ResultObject DeassignRoleRhoAsst(Map<String, Object> inputMap) {

		gLogger.info("DeassignRoleDEO service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");


		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());

			String lStrUserName1 = StringUtility.getParameter("username", request);
			int length = lStrUserName1.length();
			String lStrUserName = lStrUserName1.substring(1,length);

			gLogger.error("lStrUserName1"+lStrUserName1);
			gLogger.error("lStrUserName"+lStrUserName);
			gLogger.error("length"+length);
			gLogger.error("gStrLocationCode"+gStrLocationCode);

			String lStrPostId = lObjGpfWorkFlowConfig.getPostId(lStrUserName);

			gLogger.error("lStrPostId"+lStrPostId);
			
			//get current DEO and demap it
			
			String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleRHOASTT(String.valueOf(gStrLocationCode));
			gLogger.error("****lStrPostId*******"+PostRoleId);
            Long postRoleid=Long.parseLong(PostRoleId);
            gLogger.error("****lStrPostId*Longuuu******"+postRoleid);
			String lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleDEO(postRoleid);

			String temp = getDeassignXMLRhoAst(lStrDeassignFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();


			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
			gLogger.error("Exception"+e);
		}
		return lObjResultObj;

	}

	private StringBuilder getDeassignXMLRhoAst(String lStrDeassignFlag) {

		gLogger.error("getDeassignXMLDEO called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrDeassignFlag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
	
	//swt 16/12/2020 for MDC 
			public ResultObject loadAssignDeassign(Map<String, Object> inputMap) 
			{
                setSessionInfo(inputMap);
				ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
				String lStrDdoCode = "";
				String txtDdoCodeEmp = "";
				String lStrSevaarthId = "";
				String chkDdoCode = "";
				String lStrEmployeeName = "";
				String lStrDdoName = "";
				List lLstAllUsers= null;
				List EmpList = null;
				List EmpDtls = null;
				List lLstHoDtls = null;
				List Emprole = null;
				int activeHo=0;
				String flag="0";
				String txtSevaarthId="";
				String txtEmployeeName="";
				try{

				

					GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
					
					
					flag = StringUtility.getParameter(
							"flag", request).trim();
					
					if(flag.length()==0)
						flag="0";
					
					txtDdoCodeEmp = StringUtility.getParameter(
							"txtDdoCode", request).trim();
					
					lStrDdoCode = StringUtility.getParameter(
							"txtDdoCodeEmpnew", request).trim();
					
					lStrSevaarthId = StringUtility.getParameter(
							"txtSevaarthId", request).trim();
					
					lStrEmployeeName = StringUtility.getParameter(
							"txtEmployeeName", request).trim();
					
					lStrDdoName = StringUtility.getParameter(
							"chkDdoName", request).trim();
					
					txtSevaarthId = StringUtility.getParameter(
							"txtSevaarthId", request).trim();
					
					txtEmployeeName = StringUtility.getParameter(
							"txtEmployeeName", request).trim();
					
					
					if(flag.equalsIgnoreCase("1"))
					{
						
					}
					
					
		            inputMap.put("txtDdoCodeEmp", txtDdoCodeEmp);
		            inputMap.put("AllUsers", lLstAllUsers);
		            inputMap.put("lStrDdoCode", txtDdoCodeEmp);
		            inputMap.put("lStrDdoName", lStrDdoName);
		            inputMap.put("txtSevaarthId", txtSevaarthId);
		            inputMap.put("txtEmployeeName", txtEmployeeName);
		            inputMap.put("flag", flag);
		            
		            String loc_id = lObjGpfWorkFlowConfig.getLocationCodeNew(txtDdoCodeEmp,lStrDdoCode,lStrDdoName);
		            
		        	Emprole=lObjGpfWorkFlowConfig.getMappedRoleForEmp(loc_id,lStrSevaarthId);
					gLogger.info("Emprole"+Emprole);

					//String role_id = lObjGpfWorkFlowConfig.getRoleOfEmp(loc_id,lStrSevaarthId);
					

		           if(!txtDdoCodeEmp.equalsIgnoreCase("")|| !lStrDdoCode.equalsIgnoreCase("") || !lStrDdoName.equalsIgnoreCase("") ){
		           //String loc_id = lObjGpfWorkFlowConfig.getLocationCodeNew(txtDdoCodeEmp,lStrDdoCode);
		           gLogger.info("loc_id"+loc_id);
		           EmpList=lObjGpfWorkFlowConfig.getAllRolesForMDC(loc_id);
		           
      
		         //current ho
					lLstHoDtls = lObjGpfWorkFlowConfig.getActiveHOLst(loc_id);
					
				
		           
		           //inputMap.put("EmpList", EmpList);
		           }
					
					
					

					inputMap.put("EmpList", EmpList);
				
					inputMap.put("lStrDdoCode", lStrDdoCode);
					inputMap.put("Emprole", Emprole);
					
					
					
					
					
					if(lLstHoDtls !=null && lLstHoDtls.get(0)!=null && lLstHoDtls.size()>0)
						activeHo=lLstHoDtls.size();
					else
						activeHo=0;
					
					gLogger.info("lLstHoDtls"+lLstHoDtls);

					inputMap.put("AllHO",lLstHoDtls);
				
					resObj.setResultValue(inputMap);
					resObj.setViewName("loadAssignDeassign");

				
				}catch(Exception e){
					IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFWorkFlowConfig");
				}
				return resObj;
			}
			
			public ResultObject searchEmployeeForRoleMapMDC(Map<String, Object> inputMap) {

				gLogger.info("DeassignRoleDEO service called");
				ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

				setSessionInfo(inputMap);
				ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
				String lStrDdoCode = "";
				String txtDdoCodeEmp = "";
				String lStrSevaarthId = "";
				String lStrEmployeeName = "";
				String lStrDdoName = "";
				List EmpDtls = null;
				String SEVARTH_ID="";
				String EMP_NAME="";
				String EMP_DOJ="";
				String EMP_SRVC_EXP="";
				String DSGN_NAME="";
				String GRADE_NAME="";
				String HO="";
				String VERIFIRER="";
				String DEO="";
				String RHO_ASST="";
				String POST_ID="";
				//String USER_ID="";
				
				try{

				

					GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
					
					
					
					txtDdoCodeEmp = StringUtility.getParameter(
							"txtDdoCode", request).trim();
					
					lStrDdoCode = StringUtility.getParameter(
							"txtDdoCodeEmpnew", request).trim();
					
					lStrSevaarthId = StringUtility.getParameter(
							"txtSevaarthId", request).trim();
					
					lStrEmployeeName = StringUtility.getParameter(
							"txtEmployeeName", request).trim();
					
					lStrDdoName = StringUtility.getParameter(
							"chkDdoName", request).trim();
					
		            inputMap.put("txtDdoCodeEmp", txtDdoCodeEmp);
		            inputMap.put("lStrDdoCode", lStrDdoCode);
		            inputMap.put("chkDdoName", lStrDdoName);
		            
		            if ((!lStrDdoName.equalsIgnoreCase(""))&& ((txtDdoCodeEmp.equalsIgnoreCase("")) || (lStrDdoCode.equalsIgnoreCase("")))){
		            	lStrDdoCode=lObjGpfWorkFlowConfig.getEmpDdoCode(lStrDdoName);
		            }
		            
		            String loc_id = lObjGpfWorkFlowConfig.getLocationCodeNew(txtDdoCodeEmp,lStrDdoCode,lStrDdoName);
		            
		    
					if((!lStrSevaarthId.equalsIgnoreCase(""))||(!lStrEmployeeName.equalsIgnoreCase(""))){
					EmpDtls = lObjGpfWorkFlowConfig.getEmpForDDO(lStrSevaarthId,lStrEmployeeName,loc_id,lStrDdoCode,lStrDdoName);
					}
					if (!EmpDtls.isEmpty() || EmpDtls == null) {
						Object[] lLstDdoDtls1 = (Object[]) EmpDtls.get(0);
						
						
						SEVARTH_ID = lLstDdoDtls1[0].toString();
						EMP_NAME = lLstDdoDtls1[1].toString();
						EMP_DOJ = lLstDdoDtls1[2].toString();
						EMP_SRVC_EXP = lLstDdoDtls1[3].toString();
						DSGN_NAME = lLstDdoDtls1[4].toString();
						GRADE_NAME = lLstDdoDtls1[5].toString();
						
						
						HO = lLstDdoDtls1[6].toString();
                        VERIFIRER = lLstDdoDtls1[7].toString();
	                    DEO = lLstDdoDtls1[8].toString();
	                    RHO_ASST = lLstDdoDtls1[9].toString();
						
						POST_ID = lLstDdoDtls1[10].toString();
						//USER_ID = lLstDdoDtls1[11].toString();
					}
					//gLogger.error("SEVARTH_ID"+SEVARTH_ID+"EMP_NAME"+EMP_NAME+"EMP_DOJ"+EMP_DOJ+"EMP_SRVC_EXP"+EMP_SRVC_EXP+"DSGN_NAME"+DSGN_NAME+"GRADE_NAME"+GRADE_NAME+"HO"+HO+"VERIFIRER"+VERIFIRER+"DEO"+DEO+"RHO_ASST"+RHO_ASST+"lStrDdoCode"+lStrDdoCode);
					//String temp = getsearchEmployeeForRoleMapMDC(SEVARTH_ID,EMP_NAME,EMP_DOJ,EMP_SRVC_EXP,DSGN_NAME,GRADE_NAME,HO,VERIFIRER,DEO,RHO_ASST,lStrDdoCode).toString();
					gLogger.error("SEVARTH_ID"+SEVARTH_ID+"EMP_NAME"+EMP_NAME+"EMP_DOJ"+EMP_DOJ+"EMP_SRVC_EXP"+EMP_SRVC_EXP+"DSGN_NAME"+DSGN_NAME+"GRADE_NAME"+GRADE_NAME+"HO"+HO+"VERIFIRER"+VERIFIRER+"DEO"+DEO+"RHO_ASST"+RHO_ASST+"POST_ID"+POST_ID);
					String temp = getsearchEmployeeForRoleMapMDC(SEVARTH_ID,EMP_NAME,EMP_DOJ,EMP_SRVC_EXP,DSGN_NAME,GRADE_NAME,HO,VERIFIRER,DEO,RHO_ASST,POST_ID).toString();
					String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();
				
				

					inputMap.put("ajaxKey", lStrResult);
					lObjResultObj.setResultValue(inputMap);
					lObjResultObj.setViewName("ajaxData");
					gLogger.error("service complete");


				} catch (Exception e) {
					IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
					gLogger.error("Exception"+e);
				}
				return lObjResultObj;

			}

			private StringBuilder getsearchEmployeeForRoleMapMDC(String SEVARTH_ID, String eMP_NAME, String eMP_DOJ, String eMP_SRVC_EXP, String dSGN_NAME, String gRADE_NAME, String hO, String vERIFIRER, String dEO, String rHO_ASST,String pOST_ID) 
			{

				gLogger.error("getDeassignXMLDEO called");


				StringBuilder lStrBldXML = new StringBuilder();

				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<SEVARTH_ID>");
				lStrBldXML.append(SEVARTH_ID);
				lStrBldXML.append("</SEVARTH_ID>");
				lStrBldXML.append("<eMP_NAME>");
				lStrBldXML.append(eMP_NAME);
				lStrBldXML.append("</eMP_NAME>");
				lStrBldXML.append("<eMP_DOJ>");
				lStrBldXML.append(eMP_DOJ);
				lStrBldXML.append("</eMP_DOJ>");
				lStrBldXML.append("<eMP_SRVC_EXP>");
				lStrBldXML.append(eMP_SRVC_EXP);
				lStrBldXML.append("</eMP_SRVC_EXP>");
				lStrBldXML.append("<dSGN_NAME>");
				lStrBldXML.append(dSGN_NAME);
				lStrBldXML.append("</dSGN_NAME>");
				lStrBldXML.append("<gRADE_NAME>");
				lStrBldXML.append(gRADE_NAME);
				lStrBldXML.append("</gRADE_NAME>");
				lStrBldXML.append("<hO>");
				lStrBldXML.append(hO);
				lStrBldXML.append("</hO>");
				lStrBldXML.append("<vERIFIRER>");
				lStrBldXML.append(vERIFIRER);
				lStrBldXML.append("</vERIFIRER>");
				lStrBldXML.append("<dEO>");
				lStrBldXML.append(dEO);
				lStrBldXML.append("</dEO>");
				lStrBldXML.append("<rHO_ASST>");
				lStrBldXML.append(rHO_ASST);
				lStrBldXML.append("</rHO_ASST>");
				lStrBldXML.append("<pOST_ID>");
				lStrBldXML.append(pOST_ID);
				lStrBldXML.append("</pOST_ID>");
				lStrBldXML.append("</XMLDOC>");
				
				return lStrBldXML;
			}
			
			//swt 01/01/2021
			public ResultObject AssignAllRolesForMDC(Map<String, Object> inputMap) 
			{


				gLogger.error("AssignAllRolesForMDC service called");
				setSessionInfo(inputMap);
				ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");

				String lStrSelectedRoles = "";
				String[] lArrStrSelctdRoles = null;
				Long lLngPostId = null;
				//Long lLnguserId = null;
				String userName="";
				String sevaarthId="";
				List<Long> lLstSelectedRoles = new ArrayList<Long>();
				List<Integer> lLstSlctdRolesInt = new ArrayList<Integer>();
				List levelIdArr = null;
				String lStrFinalData = "";
				String lSrtFinalMsg = "N";
				String lStrVerifierFlag = "";
				String lStrHOFlag = "";
				String lStrRoleArr[] =  new String[3];
				String lStrHoRoleEntryFlag = "";
				String lStrHoEmpGrpEntryFlag = "";
				String chkRolesOfUser = "";
				//Long lLngUserId = 0l;
				Long lLngUserId = null;
				List lListActandRole = null;
				String activateFlagss="";
				String lStrHoRole="";
				String roleId="";
				String lStrVeriRole="";
				String lStrDEORole="";
				String lStrRhoRole="";
				String txtDdoCodeEmp="";
				int requestPending=0;
				int requestPendingLNA=0;
				int requestPendingOrderGen=0;
				 int assign=0;
				 int deassign=0;
				 int pending=0;
				 String PendingFlag="N";
				 String lStrDeassignFlag="N";
				 int requestPendingUnderDEO=0;
				 int checkpendingDEOFlag=0;
				 String lStrAssignHO="";
				 String chkEmpGroupVerifierEntry="";
			     String lStrVerifierRoleEntryFlag="";
			     String lStrDeoRoleEntryFlag="";
			     String chkEmpGroupDeoEntry="";
			     String chkEmpGroupRhoEntry="";
			     String lStrRhoRoleEntryFlag="";
			     List lListActandRhoRole = null;
					String activateFlagRho = "";
					String roleIdRho = "";
					String roleNameRho = "";
					String lStrDdoName = "";
					String lSrtFinalMsg1 = "";
					String lSrtFinalMsg2 = "";
					String lSrtFinalMsg3 = "";
					String lSrtFinalMsg4 = "";
					List lListActRho = null;
					int assignHO=0;
					int assignVeri=0;
					int assignDEO=0;
					int assignRHO=0;
					int deassignHO=0;
					int deassignVeri=0;
					int deassignDEO=0;
					int deassignRHO=0;
					
					int ReassignHO=0;
					int ReassignVeri=0;
					//int ReassignDEO=0;
					int ReassignRHO=0;
                   String lSrtFinalMsgMap = "N";
                   String strOrderTransactionId = "";
					String OrderTranId="N";
				
				try{
					GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
					

					/*if((StringUtility.getParameter("postId", request)!=null)){
						lLngPostId = Long.parseLong(StringUtility.getParameter("postId",request));
					}*/
					lLngPostId = Long.parseLong(StringUtility.getParameter("postId",request));
					gLogger.error("lLngPostId"+lLngPostId);

					lLngUserId = lObjGpfWorkFlowConfig.getUserIdFromPostId(lLngPostId);
					gLogger.error("lLngUserId"+lLngUserId);
					userName = lObjGpfWorkFlowConfig.getUsername(lLngUserId);
					gLogger.error("userName"+userName);			
					lStrSelectedRoles = StringUtility.getParameter("selectedRoles", request);			
					gLogger.error("lStrSelectedRoles"+lStrSelectedRoles);	
					
					lStrHoRole = StringUtility.getParameter("hoFlag", request);
					gLogger.error("hoFlag"+lStrHoRole);
					lStrVeriRole = StringUtility.getParameter("veri", request);
					gLogger.error("veri"+lStrVeriRole);
					lStrDEORole = StringUtility.getParameter("deo", request);
					gLogger.error("deo"+lStrDEORole);
					lStrRhoRole = StringUtility.getParameter("rhoASST", request);
					gLogger.error("rhoASST"+lStrRhoRole);
					
					
					txtDdoCodeEmp = StringUtility.getParameter(
							"txtDdoCodeEmp", request).trim();
					
					lStrDdoName = StringUtility.getParameter(
							"chkDdoName", request).trim();
					
		            inputMap.put("chkDdoName", lStrDdoName);
		            
		            if ((!lStrDdoName.equalsIgnoreCase(""))&& (txtDdoCodeEmp.equalsIgnoreCase(""))){
		            	txtDdoCodeEmp=lObjGpfWorkFlowConfig.getEmpDdoCode(lStrDdoName);
		            }
				
                    String lStrlocid = lObjGpfWorkFlowConfig.getLocationForPost(txtDdoCodeEmp);
                    
                    lListActandRole = lObjGpfWorkFlowConfig.getActiveFlagRole(lLngPostId);        // Added by vivek 31 jan 2017
        			// Added by vivek 31 jan 2017
        			Iterator itC=lListActandRole.iterator();
        			
        			while(itC.hasNext()){
        			Object[] tupleC = (Object[]) itC.next();
        			activateFlagss=tupleC[0].toString();
        			roleId=tupleC[1].toString();
        			
        			}
        			
        			inputMap.put("activateFlagss", activateFlagss);
        			inputMap.put("roleId", roleId);
	  
					
                  
							if(lStrHoRole.equals("800002"))
								{
								gLogger.error(" In Ho Field *************");	
								lStrHOFlag = lObjGpfWorkFlowConfig.chkEntryForHO(lStrlocid);	
								gLogger.error("lStrHOFlag"+lStrHOFlag);	
								lStrHoRoleEntryFlag = lObjGpfWorkFlowConfig.chkRoleHoEntry(lLngPostId,lStrlocid);
								gLogger.error("lStrHoRoleEntryFlag"+lStrHoRoleEntryFlag);	
                                //String lStrDdoCode = lObjGpfWorkFlowConfig.getDdoCode(lLngPostId);
								lStrHoEmpGrpEntryFlag =lObjGpfWorkFlowConfig.chkEmpGroupHoEntryMDC(lLngPostId,txtDdoCodeEmp);
								gLogger.error("lStrHoEmpGrpEntryFlag"+lStrHoEmpGrpEntryFlag);
								
								//if(lStrHoRoleEntryFlag.equals("N") || lStrHoRoleEntryFlag.equals(""))  
								if((lStrHoEmpGrpEntryFlag.equals("Y") || lStrHoEmpGrpEntryFlag.equals("")) && (lStrHoRoleEntryFlag.equals("") || lStrHoRoleEntryFlag.equals("N")))	
                                {
									String HoFlag=assignHO(lStrlocid,lLngPostId,lStrHoRole,inputMap);
									lObjGpfWorkFlowConfig.updateWfHierachyPostMpgFlag(lLngPostId);
									lObjGpfWorkFlowConfig.unlockAccountForuserId(lLngUserId);
									/*lSrtFinalMsg = "Role of HO has been assigned to user having his username as his sevaath ID "
											+userName +" and Password: ifms123";*/
									if(HoFlag.equals("HO")){
									//assign=1;
									assignHO=1;
									}
									lSrtFinalMsg=Integer.toString(assignHO);
									//lSrtFinalMsg1=Integer.toString(assign);
									
									
								}
								else{
                                    gLogger.info(" ***lStrResult*** 394 ");
                                    if(Long.parseLong(lStrHoRoleEntryFlag) != lLngPostId)
                                    {
                                    //lSrtFinalMsg = "Role of HO is already assigned.";
										ReassignHO=11;
										lSrtFinalMsgMap=Integer.toString(ReassignHO);
                                    }
								}
								
							  }
							else{
								requestPending=lObjGpfWorkFlowConfig.checkPendingRequestHOmdc(lLngPostId.toString());
								gLogger.info("No of requests pending is"+requestPending);
								//requestPendingLNA=lObjGpfWorkFlowConfig.checkPendingLNARequestHOmdc(lLngPostId.toString());
								//gLogger.info("No of requests pending is"+requestPendingLNA);
								String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleHOMDC(lStrlocid,lLngPostId.toString());
								String chkDeoRole=lObjGpfWorkFlowConfig.checkDeoRoleMapped(lStrlocid);
								requestPendingOrderGen=lObjGpfWorkFlowConfig.checkPendingRequestHOmdcOrderGen(lLngPostId.toString());
								
								List chkPendingReqInRHO=null;
								List chkPendingReqInRHOApproved=null;
								
								chkPendingReqInRHO=lObjGpfWorkFlowConfig.chkPendingReqInRHO(lLngPostId.toString());
								chkPendingReqInRHOApproved=lObjGpfWorkFlowConfig.chkPendingReqInRHOApproved(lLngPostId.toString());
								
								//GPF request pending under HO or RHO
								if(requestPending>0 && PostRoleId.length()>0 && (chkDeoRole.length()>0 || chkPendingReqInRHO.size()>0 || chkPendingReqInRHOApproved.size()>0) 
										&& requestPendingOrderGen==0)
								{
								//swt 20/04/2021 
								List PendingHoAdv=null;
								List PendingHoFw=null;
								List PendingHoInitial=null;
								List PendingHoApprovedAdv=null;
								List PendingHoApprovedFw=null;
								PendingHoAdv=lObjGpfWorkFlowConfig.chkPendingReqHoAdvance(lLngPostId.toString());
								PendingHoFw=lObjGpfWorkFlowConfig.chkPendingReqHoFinal(lLngPostId.toString());
                                PendingHoInitial=lObjGpfWorkFlowConfig.chkPendingReqHoInitialDataEntry(lLngPostId.toString());
                                PendingHoApprovedAdv=lObjGpfWorkFlowConfig.chkPendingReqHoApprovedAdv(lLngPostId.toString());
                                PendingHoApprovedFw=lObjGpfWorkFlowConfig.chkPendingReqHoApprovedFw(lLngPostId.toString());
								
                                //LNA pending req
                                List chkPendingReqLNA=null;
                                chkPendingReqLNA=lObjGpfWorkFlowConfig.chkPendingReqLNAHo(lLngPostId.toString());
                                if (!chkPendingReqLNA.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = chkPendingReqLNA.iterator();
									String advanceId="";
									String advancetype="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										if(tuple[1] != null)
										{
											 advancetype = tuple[1].toString();
										}
										

										String updatePendingLNA=lObjGpfWorkFlowConfig.updatePendingReqLNA(advanceId,advancetype);


									}

								}
								
								if (!PendingHoAdv.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = PendingHoAdv.iterator();
									String advanceId="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										

										String updatePendingHo=lObjGpfWorkFlowConfig.updatePendingReqVeriAdvance(advanceId);


									}

								}
								
								if (!PendingHoFw.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = PendingHoFw.iterator();
									String advanceId="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										

										String updatePendingHo=lObjGpfWorkFlowConfig.updatePendingReqVeriFinal(advanceId);


									}

								}
								
								if (!PendingHoInitial.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = PendingHoInitial.iterator();
									String advanceId="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										

										String updatePendingHo=lObjGpfWorkFlowConfig.updatePendingReqHoInitial(advanceId);


									}

								}
								if (!PendingHoApprovedAdv.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = PendingHoApprovedAdv.iterator();
									String advanceId="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										

										String updatePendingHo=lObjGpfWorkFlowConfig.updatePendingReqVeriAdvance(advanceId);


									}

								}
								
								if (!PendingHoApprovedFw.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = PendingHoApprovedFw.iterator();
									String advanceId="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										

										String updatePendingHo=lObjGpfWorkFlowConfig.updatePendingReqVeriFinal(advanceId);


									}

								}
								
								requestPending=lObjGpfWorkFlowConfig.checkPendingRequestHOmdc(lLngPostId.toString());
								}
								
								 if(PostRoleId.length()>0 ){
					            //if(requestPending>0)
								if(chkDeoRole.length()==0 && requestPending>0 && requestPendingOrderGen==0)
					            {
					            	//PendingFlag="Kindly process the requests pending with HO ";
					            	pending=1;
					            	PendingFlag=Integer.toString(pending);
					            	
					            gLogger.info("PendingFlag"+PendingFlag);
					            //lSrtFinalMsg = "Kindly process the requests pending with this user and then deassign.";
	                            }

								else if(requestPendingOrderGen>0)
					            {
									String[] strOrderTranId = new String[1];
									
					            	//PendingFlag="Kindly process the requests pending with HO ";
									List OrderTranID=lObjGpfWorkFlowConfig.chkPendingReqHoOrderGen(lLngPostId.toString());
									
									if (OrderTranID.size() > 0)
								    {
								      /*for (int i = 0; i < OrderTranID.size(); i++)
								      {
								        String[] tmp = OrderTranID.get(i).toString().split("##");
								      
								      }*/
								      for (int i = 0; i < OrderTranID.size(); i++)
								      {
								        String[] tmp = OrderTranID.get(i).toString().split("##");
								        
								        
								   
								        strOrderTransactionId = strOrderTransactionId.replace("\n", "").replace("\r", "")
								                .concat(" ".concat(tmp[0])).concat(",");
								       
								      }
								      
								      strOrderTransactionId.replace("\n", "").replace("\r", "");
								      strOrderTransactionId = strOrderTransactionId.substring(0,strOrderTransactionId.length() - 1);
								   
								    }
									
								    
								     //strOrderTranId[0] = strOrderTransactionId;
									OrderTranId = strOrderTransactionId.toString();
									//lSrtFinalMsgMap=Integer.toString(strOrderTransactionId)
								   
									
					            	pending=4;
					            	PendingFlag=Integer.toString(pending);
					            	
					            gLogger.info("PendingFlag"+PendingFlag);
					            //lSrtFinalMsg = "Kindly process the requests pending with this user and then deassign.";
	                            }
					            else
					            {
					            	//String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleHOMDC(lStrlocid,lLngPostId.toString());
					            	
						            Long postRoleid=Long.parseLong(PostRoleId);
						        lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleDEO(postRoleid);
						        if(lStrDeassignFlag.equalsIgnoreCase("1")){
						        deassignHO=1;
						        }
				            	lStrDeassignFlag=Integer.toString(deassignHO);
					            }
					            }
					            //End
								gLogger.error("lStrDeassignFlag"+lStrDeassignFlag);
        				        }
                              
							if(lStrVeriRole.equals("800001")){
								gLogger.error(" In Verifier Field *************");
								lStrVerifierFlag = lObjGpfWorkFlowConfig.chkEntryForVerifier(lStrlocid);		

								gLogger.error("lStrVerifierFlag"+lStrVerifierFlag);	
								lStrVerifierRoleEntryFlag = lObjGpfWorkFlowConfig.chkRoleVerifierEntry(lLngPostId,lStrlocid);
								gLogger.error("lStrVerifierRoleEntryFlag"+lStrVerifierRoleEntryFlag);
								chkEmpGroupVerifierEntry =lObjGpfWorkFlowConfig.chkEmpGroupVerifierEntry(lLngPostId,txtDdoCodeEmp);
								gLogger.error("chkEmpGroupVerifierEntry"+chkEmpGroupVerifierEntry);
								
								//if(lStrHoRoleEntryFlag.equals("N") || lStrHoRoleEntryFlag.equals(""))  
								if((chkEmpGroupVerifierEntry.equals("Y") || chkEmpGroupVerifierEntry.equals("")) && (lStrVerifierRoleEntryFlag.equals("") || lStrVerifierRoleEntryFlag.equals("N")))	
                                {
									String VeriFlag=assignVerifier(lStrlocid,lLngPostId,lStrVeriRole,inputMap);
									lObjGpfWorkFlowConfig.updateWfHierachyPostMpgFlag(lLngPostId);
									lObjGpfWorkFlowConfig.unlockAccountForuserId(lLngUserId);
									/*lSrtFinalMsg = "Role of Verifier has been assigned to user having his username as his sevaath ID "
											+userName +" and Password: ifms123";*/
                                    //assign=2;
									if(VeriFlag.equals("Verifier")){
										//assign=2;
										assignVeri=2;
                                    }
									lSrtFinalMsg=Integer.toString(assignVeri);
									//lSrtFinalMsg2=Integer.toString(assign);
									
								}
								else{
                                    gLogger.info(" ***lStrResult*** 394 ");
                                    if(Long.parseLong(lStrVerifierRoleEntryFlag) != lLngPostId)
                                    {
									//lSrtFinalMsg = "Role of Verifier is already assigned.";
									ReassignVeri=12;
									lSrtFinalMsgMap=Integer.toString(ReassignVeri);
                                    }
								}
								
								//lStrRoleArr[lIntCnt] = "Verifier";
							} else
							{
								  requestPending=lObjGpfWorkFlowConfig.checkPendingRequestVerifierMdc(lLngPostId.toString());
									gLogger.info("No of requests pending is"+requestPending);
									
									String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleIVERMDC(lStrlocid,lLngPostId.toString());
									String chkDeoRole=lObjGpfWorkFlowConfig.checkDeoRoleMapped(lStrlocid);
									
									if(requestPending>0 && PostRoleId.length()>0 && chkDeoRole.length()>0)
									{
									//swt 20/04/2021 
									List PendingVeriAdv=null;
									List PendingVeriFw=null;
									PendingVeriAdv=lObjGpfWorkFlowConfig.chkPendingReqVeriAdvance(lLngPostId.toString());
									PendingVeriFw=lObjGpfWorkFlowConfig.chkPendingReqVeriFinal(lLngPostId.toString());
									
									
									if (!PendingVeriAdv.isEmpty())
									{
										gLogger.info(" disbursementType is hello ....###888888 ");
										Iterator iterator = PendingVeriAdv.iterator();
										String advanceId="";
										while (iterator.hasNext())
										{
											Object[] tuple = (Object[])iterator.next();
											//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

											if(tuple[0] != null)
											{
												 advanceId = tuple[0].toString();
											}
											

											String updatePendingVeri=lObjGpfWorkFlowConfig.updatePendingReqVeriAdvance(advanceId);


										}

									}
									
									if (!PendingVeriFw.isEmpty())
									{
										gLogger.info(" disbursementType is hello ....###888888 ");
										Iterator iterator = PendingVeriFw.iterator();
										String advanceId="";
										while (iterator.hasNext())
										{
											Object[] tuple = (Object[])iterator.next();
											//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

											if(tuple[0] != null)
											{
												 advanceId = tuple[0].toString();
											}
											

											String updatePendingVeri=lObjGpfWorkFlowConfig.updatePendingReqVeriFinal(advanceId);


										}

									}
									
									requestPending=lObjGpfWorkFlowConfig.checkPendingRequestVerifierMdc(lLngPostId.toString());
									}
									
						            if(PostRoleId.length()>0 ){
									
									//if(requestPending>0)
						            	
						            if(chkDeoRole.length()==0 && requestPending>0 )
						            {
										pending=2;						        
						            	
						            	PendingFlag=Integer.toString(pending);
						            gLogger.info("value after operation"+PendingFlag);
						            }
						            else
						            {
						            //String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleIVERMDC(lStrlocid,lLngPostId.toString());
						            //if(PostRoleId.length()>0 ){
						            //if(PostRoleId.equals("800005")){	
							        Long postRoleid=Long.parseLong(PostRoleId);
									lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleDEO(postRoleid);
									if(lStrDeassignFlag.equalsIgnoreCase("1")){
									deassignVeri=2;
									}
					            	lStrDeassignFlag=Integer.toString(deassignVeri);
						            }
						            }
								
							}
							if(lStrDEORole.equals("800005")){
									gLogger.error(" In DEO Field *************");
									String lStrDeoFlag = lObjGpfWorkFlowConfig.chkEntryForDeo(lStrlocid);		

									gLogger.error("lStrDeoFlag"+lStrDeoFlag);	
									lStrDeoRoleEntryFlag = lObjGpfWorkFlowConfig.chkRoleDeoEntry(lLngPostId,lStrlocid);
									gLogger.error("lStrDeoRoleEntryFlag"+lStrDeoRoleEntryFlag);
									chkEmpGroupDeoEntry =lObjGpfWorkFlowConfig.chkEmpGroupVerifierEntry(lLngPostId,txtDdoCodeEmp);
									gLogger.error("chkEmpGroupDeoEntry"+chkEmpGroupDeoEntry);
									
									//if(lStrHoRoleEntryFlag.equals("N") || lStrHoRoleEntryFlag.equals(""))  
									if((chkEmpGroupDeoEntry.equals("Y") || chkEmpGroupDeoEntry.equals("")) && (lStrDeoRoleEntryFlag.equals("") || lStrDeoRoleEntryFlag.equals("N")))	
	                                {
										String DeoFlag=assigndeo(lStrlocid,lLngPostId,lStrDEORole,inputMap);
										lObjGpfWorkFlowConfig.updateWfHierachyPostMpgFlag(lLngPostId);
										lObjGpfWorkFlowConfig.unlockAccountForuserId(lLngUserId);
										/*lSrtFinalMsg = "Role of Verifier has been assigned to user having his username as his sevaath ID "
												+userName +" and Password: ifms123";*/
										if(DeoFlag.equals("Deo")){
											//assign=3;
											assignDEO=3;
	                                    }
										
										
										lSrtFinalMsg=Integer.toString(assignDEO);
										//lSrtFinalMsg3=Integer.toString(assign);
										
									}
									
										
									
							} 
							else
				            {
								requestPendingUnderDEO=lObjGpfWorkFlowConfig.checkPendingRequestDEOMdc(userName);
								gLogger.info("Number of request Pending under ddo is"+requestPendingUnderDEO);
								
								String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleIDMDC(lStrlocid,lLngPostId.toString());
								
								if(requestPendingUnderDEO>0 && PostRoleId.length()>0)
								{
								//swt 20/04/2021 
								List PendingDeoAdv=null;
								List PendingDeoFw=null;
								PendingDeoAdv=lObjGpfWorkFlowConfig.chkPendingReqDeoAdvance(userName);
								PendingDeoFw=lObjGpfWorkFlowConfig.chkPendingReqDeoFinal(userName);
								
								
								if (!PendingDeoAdv.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = PendingDeoAdv.iterator();
									String advanceId="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										

										String updatePendingDeo=lObjGpfWorkFlowConfig.updatePendingReqDeoAdvance(advanceId);


									}

								}
								
								if (!PendingDeoFw.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = PendingDeoFw.iterator();
									String advanceId="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										

										String updatePendingDeo=lObjGpfWorkFlowConfig.updatePendingReqDeoFinal(advanceId);


									}

								}
								
								requestPendingUnderDEO=lObjGpfWorkFlowConfig.checkPendingRequestDEOMdc(userName);
								}
								
								 if(PostRoleId.length()>0 ){
								if(requestPendingUnderDEO>0)
								{
									//if(pending==1){
					            		//PendingFlag=PendingFlag+"DEO";
					            		pending=3;
					            	//}
					            	
									//lStrDeassignFlag=Integer.toString(checkpendingDEOFlag);
									PendingFlag=Integer.toString(pending);
								}
								else
								{
								//String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleIDMDC(lStrlocid,lLngPostId.toString());
								 //if(PostRoleId.length()>0 ){	
						        Long postRoleid=Long.parseLong(PostRoleId);
								lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleDEO(postRoleid);
								if(lStrDeassignFlag.equalsIgnoreCase("1")){
								deassignDEO=3;
								}
				            	lStrDeassignFlag=Integer.toString(deassignDEO);
								}
				            }
				            }
							
							if(lStrRhoRole.equals("8000016")){
								lStrRhoRoleEntryFlag = lObjGpfWorkFlowConfig.chkRoleRhoEntry(lLngPostId,lStrlocid);
								gLogger.error("lStrRhoRoleEntryFlag"+lStrRhoRoleEntryFlag);
								chkEmpGroupRhoEntry =lObjGpfWorkFlowConfig.chkEmpGroupRhoEntry(lLngPostId,txtDdoCodeEmp);
								gLogger.error("chkEmpGroupRhoEntry"+chkEmpGroupRhoEntry);
								
								//if(lStrHoRoleEntryFlag.equals("N") || lStrHoRoleEntryFlag.equals(""))  
								if((chkEmpGroupRhoEntry.equals("Y") || chkEmpGroupRhoEntry.equals("")) && (lStrRhoRoleEntryFlag.equals("") || lStrRhoRoleEntryFlag.equals("N")))	
                                {
									//Long Postid=Long.parseLong(lLngPostId);
									
									assignRho(lStrlocid,lLngPostId,lStrDEORole,inputMap);
									//addRhoAsstToGpf(inputMap);
									
                                         //lObjGpfWorkFlowConfig.InsertPostForRhoAsst1(lLngPostId);
                                         lObjGpfWorkFlowConfig.unlockAccountForuserId(lLngUserId);
							           
								
									assignRHO=4;
                                    lSrtFinalMsg=Integer.toString(assignRHO);
								
									
								}
								else{
                                    gLogger.info(" ***lStrResult*** 394 ");
                                    gLogger.info(" ***lStrResult*** 394 ");
                                    if(Long.parseLong(lStrRhoRoleEntryFlag) != lLngPostId)
                                    {
                                    //lSrtFinalMsg = "Role of RHO ASST is already assigned.";
										ReassignRHO=13;
										lSrtFinalMsgMap=Integer.toString(ReassignRHO);
                                    }
                                   /* if(Long.parseLong(lStrRhoRoleEntryFlag)!=lLngPostId)
									lSrtFinalMsg = "Role of RHO ASST is already assigned.";
									assign=13;
									lSrtFinalMsg=Integer.toString(assign);*/
								}
							
							    }
					       else{
					    	 
					    	   String PostRoleId=lObjGpfWorkFlowConfig.getPostRoleRHOASTTMdc(lStrlocid,lLngPostId.toString());
					    	   requestPending=lObjGpfWorkFlowConfig.checkPendingRequestRHOAsstMdc(lLngPostId.toString());
							   gLogger.info("No of requests pending is"+requestPending);
							   if(requestPending>0 && PostRoleId.length()>0)
								{
								//swt 20/04/2021 
								List PendingRhoAsstAdv=null;
								List PendingRhoAsstFw=null;
								PendingRhoAsstAdv=lObjGpfWorkFlowConfig.chkPendingReqRHOAsstAdvance(lLngPostId.toString());
								PendingRhoAsstFw=lObjGpfWorkFlowConfig.chkPendingReqRHOAsstFinal(lLngPostId.toString());
								
								 //LNA pending req
                                List chkPendingReqLNA=null;
                                chkPendingReqLNA=lObjGpfWorkFlowConfig.chkPendingReqLNAHo(lLngPostId.toString());
                                if (!chkPendingReqLNA.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = chkPendingReqLNA.iterator();
									String advanceId="";
									String advancetype="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										if(tuple[1] != null)
										{
											 advancetype = tuple[1].toString();
										}
										

										String updatePendingLNA=lObjGpfWorkFlowConfig.updatePendingReqLNA(advanceId,advancetype);


									}

								}
								
								
								if (!PendingRhoAsstAdv.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = PendingRhoAsstAdv.iterator();
									String advanceId="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										

										String updatePendingVeri=lObjGpfWorkFlowConfig.updatePendingReqVeriAdvance(advanceId);


									}

								}
								
								if (!PendingRhoAsstFw.isEmpty())
								{
									gLogger.info(" disbursementType is hello ....###888888 ");
									Iterator iterator = PendingRhoAsstFw.iterator();
									String advanceId="";
									while (iterator.hasNext())
									{
										Object[] tuple = (Object[])iterator.next();
										//Object[] tuple = (Object[]) PendingDeoAdv.get(0);

										if(tuple[0] != null)
										{
											 advanceId = tuple[0].toString();
										}
										

										String updatePendingVeri=lObjGpfWorkFlowConfig.updatePendingReqVeriFinal(advanceId);


									}

								}
								
								requestPending=lObjGpfWorkFlowConfig.checkPendingRequestRHOAsstMdc(lLngPostId.toString());
								}
								
					    	   
					    	   if(PostRoleId.length()>0 )
					    	   {
								gLogger.error("****lStrPostId*******"+PostRoleId);
								//if(PostRoleId != "" || PostRoleId != null){
								//if(PostRoleId.equals("Y")){
					            Long postRoleid=Long.parseLong(PostRoleId);
					            //gLogger.error("****lStrPostId*Longuuu******"+postRoleid);
					            lStrDeassignFlag = lObjGpfWorkFlowConfig.DeAssignRoleDEO(postRoleid);
								if(lStrDeassignFlag.equalsIgnoreCase("1")){
								deassignRHO=4;
								}
				            	lStrDeassignFlag=Integer.toString(deassignRHO);
								//}
                                }
					    	   
					       
					       }
							
							//Assign roles
							if(assignHO>0 && assignVeri>0 && assignDEO==0 && assignRHO==0){
								assign=5;
                                lSrtFinalMsg=Integer.toString(assign);
							}
							
							if(assignHO>0 && assignVeri>0 && assignDEO>0 && assignRHO==0){
								assign=6;
                                lSrtFinalMsg=Integer.toString(assign);
							}
							
							if(assignHO==0 && assignVeri>0 && assignDEO>0 && assignRHO==0){
								assign=7;
                                lSrtFinalMsg=Integer.toString(assign);
							}
							
							if(assignHO>0 && assignVeri==0 && assignDEO>0 && assignRHO==0){
								assign=8;
                                lSrtFinalMsg=Integer.toString(assign);
							}
							
							
							if(assignHO>0 && assignVeri>0 && assignDEO>0 && assignRHO>0){
								assign=9;
								lSrtFinalMsg=Integer.toString(assign);
							}
							if(assignHO==0 && assignVeri==0 && assignDEO>0 && assignRHO>0){
								assign=10;
								lSrtFinalMsg=Integer.toString(assign);
							}
							if(assignHO==0 && assignVeri>0 && assignDEO==0 && assignRHO>0){
								assign=11;
								lSrtFinalMsg=Integer.toString(assign);
							}
							if(assignHO>0 && assignVeri==0 && assignDEO==0 && assignRHO>0){
								assign=12;
								lSrtFinalMsg=Integer.toString(assign);
							}
							if(assignHO==0 && assignVeri>0 && assignDEO>0 && assignRHO>0){
								assign=13;
								lSrtFinalMsg=Integer.toString(assign);
							}
							
							
							
							
							//Deassign roles
							
							if(deassignHO>0 && deassignVeri>0 && deassignDEO==0 && deassignRHO==0){
								deassign=5;
								lStrDeassignFlag=Integer.toString(deassign);
							}
							if(deassignHO>0 && deassignVeri>0 && deassignDEO>0 && deassignRHO==0){
								deassign=6;
								lStrDeassignFlag=Integer.toString(deassign);
							}
							if(deassignHO==0 && deassignVeri>0 && deassignDEO>0 && deassignRHO==0){
								deassign=7;
								lStrDeassignFlag=Integer.toString(deassign);
							}
							if(deassignHO>0 && deassignVeri==0 && deassignDEO>0 && deassignRHO==0){
								deassign=8;
								lStrDeassignFlag=Integer.toString(deassign);
							}
							if(deassignHO>0 && deassignVeri>0 && deassignDEO>0 && deassignRHO>0){
								deassign=9;
								lStrDeassignFlag=Integer.toString(deassign);
							}
							if(deassignHO==0 && deassignVeri==0 && deassignDEO>0 && deassignRHO>0){
								deassign=10;
								lStrDeassignFlag=Integer.toString(deassign);
							}
							if(deassignHO==0 && deassignVeri>0 && deassignDEO==0 && deassignRHO>0){
								deassign=11;
								lStrDeassignFlag=Integer.toString(deassign);
							}
							if(deassignHO>0 && deassignVeri==0 && deassignDEO==0 && deassignRHO>0){
								deassign=12;
								lStrDeassignFlag=Integer.toString(deassign);
							}
							if(deassignHO==0 && deassignVeri>0 && deassignDEO>0 && deassignRHO>0){
								deassign=13;
								lStrDeassignFlag=Integer.toString(deassign);
							}
							
							//already mapped
							if(ReassignHO>0 && ReassignVeri>0 && ReassignRHO==0){
								assign=44;
								lSrtFinalMsgMap=Integer.toString(assign);
							}
							if(ReassignHO==0 && ReassignVeri>0 && ReassignRHO>0){
								assign=55;
								lSrtFinalMsgMap=Integer.toString(assign);
							}
							if(ReassignHO>0 && ReassignVeri==0 && ReassignRHO>0){
								assign=66;
								lSrtFinalMsgMap=Integer.toString(assign);
							}
							if(ReassignHO>0 && ReassignVeri>0 && ReassignRHO>0){
								assign=77;
								lSrtFinalMsgMap=Integer.toString(assign);
							}
							
							
							
							//lSrtFinalMsg=lSrtFinalMsg1+lSrtFinalMsg2+lSrtFinalMsg3+lSrtFinalMsg4;
							//gLogger.info("lSrtFinalMsg "+lSrtFinalMsg);
										
					String lSBStatus = getResponseXMLDocForMDC(lSrtFinalMsg,lStrDeassignFlag,PendingFlag,lStrHoRole,lStrVeriRole,lStrDEORole,lStrRhoRole,userName,txtDdoCodeEmp,lLngPostId,lSrtFinalMsgMap,OrderTranId).toString();
					
					String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
					gLogger.info("lStrResult "+lStrResult);
					inputMap.put("ajaxKey", lStrResult);
					resObj.setResultValue(inputMap);
					resObj.setViewName("ajaxData");

					
					return resObj;
				}catch (Exception e) {
					IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFWorkFlowConfig");
				}
				return resObj;
			
				
				
			}	
	
			private StringBuilder getResponseXMLDocForMDC(String lStrFlag,String lStrDeassignFlag, String pendingFlag,String lStrHoRole,String lStrVeriRole,String lStrDEORole,String lStrRhoRole,String userName,String txtDdoCodeEmp,Long lLngPostId,String lSrtFinalMsgMap,String OrderTranId) {

				StringBuilder lStrBldXML = new StringBuilder();

				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<FLAG>");
				lStrBldXML.append(lStrFlag);
				lStrBldXML.append("</FLAG>");
				lStrBldXML.append("<DEASSIGN>");
				lStrBldXML.append(lStrDeassignFlag);
				lStrBldXML.append("</DEASSIGN>");
				lStrBldXML.append("<pendingFlag>");
				lStrBldXML.append(pendingFlag);
				lStrBldXML.append("</pendingFlag>");
				lStrBldXML.append("<lStrHoRole>");
				lStrBldXML.append(lStrHoRole);
				lStrBldXML.append("</lStrHoRole>");
				lStrBldXML.append("<lStrVeriRole>");
				lStrBldXML.append(lStrVeriRole);
				lStrBldXML.append("</lStrVeriRole>");
				lStrBldXML.append("<lStrDEORole>");
				lStrBldXML.append(lStrDEORole);
				lStrBldXML.append("</lStrDEORole>");
				lStrBldXML.append("<lStrRhoRole>");
				lStrBldXML.append(lStrRhoRole);
				lStrBldXML.append("</lStrRhoRole>");
				lStrBldXML.append("<userName>");
				lStrBldXML.append(userName);
				lStrBldXML.append("</userName>");
				lStrBldXML.append("<txtDdoCodeEmp>");
				lStrBldXML.append(txtDdoCodeEmp);
				lStrBldXML.append("</txtDdoCodeEmp>");
				lStrBldXML.append("<lLngPostId>");
				lStrBldXML.append(lLngPostId);
				lStrBldXML.append("</lLngPostId>");
	            lStrBldXML.append("<lSrtFinalMsgMap>");
				lStrBldXML.append(lSrtFinalMsgMap);
				lStrBldXML.append("</lSrtFinalMsgMap>");
				lStrBldXML.append("<OrderTranId>");
				lStrBldXML.append(OrderTranId);
				lStrBldXML.append("</OrderTranId>");
				lStrBldXML.append("</XMLDOC>");

				return lStrBldXML;
			}
			
			public String assignHO(String lStrlocid,Long lLngPostId,String lStrHoRole,Map inputMap){
				
	            //List lStrSelectedRoles = null;
	      
				String[] lArrStrSelctdRoles = null;
				//Long lLngPostId = null;
				String userName="";
				//Long lLngUserId = 0l;
				String sevaarthId="";
				List<Long> lLstSelectedRoles = new ArrayList<Long>();
				
				String levelIdArr = null;
				String lStrFinalData = "";
				String lSrtFinalMsg = "";
				//String lStrRoleArr[] =  new String[3];
				String lStrRoleArr ="";
	            List<String> lStrSelectedRoles = new ArrayList<String>();
	            lStrSelectedRoles.add(lStrHoRole);
				
				
				
				 
				try{
						
					GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
					
                lStrFinalData = lObjGpfWorkFlowConfig.assignRolesToUser(lLngPostId, lStrSelectedRoles, gLngUserId, gLngPostId, inputMap);
				
				gLogger.error("lStrFinalData344"+lStrFinalData);	
				if(lStrHoRole != null){
					levelIdArr = lObjGpfWorkFlowConfig.getLevelIdForRolesMDC(lStrHoRole);
					gLogger.error("levelIdArr"+levelIdArr);	
					
					
}
				

                gLogger.error("lStrlocid::::::::::::::::::::::::::::::::::::"+lStrlocid);
				gLogger.error("lLngPostId::::::::::::::::::::::::::::::::::::"+lLngPostId);
				gLogger.error("gLngLangId::::::::::::::::::::::::::::::::::::"+gLngLangId);
				gLogger.error("levelIdArr::::::::::::::::::::::::::::::::::::"+levelIdArr);
				gLogger.error("gLngUserId::::::::::::::::::::::::::::::::::::"+gLngUserId);
				gLogger.error("gLngPostId::::::::::::::::::::::::::::::::::::"+gLngPostId);
			
				List<String> levelIdArrNew = new ArrayList<String>();
				levelIdArrNew.add(levelIdArr);
				lSrtFinalMsg = "HO";
				lObjGpfWorkFlowConfig.insertDataForWorkflowMDC(lStrlocid, lLngPostId, gLngLangId, levelIdArrNew, inputMap, gLngUserId, gLngPostId);

				//String lStrRoleName ;
				//gLogger.error("lArrStrSelctdRoles.length::::::::::::::::::::::::::::::::::::"+lArrStrSelctdRoles.length);
				//if(lStrFinalData != null && lStrFinalData != "")
				/*lSrtFinalMsg = "Role of HO has been assigned to user having his username as his sevaath ID "
						+userName +" and Password: ifms123";*/
				
				

					
	}catch (Exception e) {
		
	}
	return lSrtFinalMsg;
			
	}

   public String assignVerifier(String lStrlocid,Long lLngPostId,String lStrVeriRole,Map inputMap){
				
	           
				String[] lArrStrSelctdRoles = null;
		        String userName="";
	            String sevaarthId="";
				List<Long> lLstSelectedRoles = new ArrayList<Long>();
				
				String levelIdArr = null;
				String lStrFinalData = "";
				String lSrtFinalMsg = "";
				//String lStrRoleArr[] =  new String[3];
				String lStrRoleArr ="";
	            List<String> lStrSelectedRoles = new ArrayList<String>();
	            lStrSelectedRoles.add(lStrVeriRole);
				
	          try{
						
					GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
					
                lStrFinalData = lObjGpfWorkFlowConfig.assignRolesToUser(lLngPostId, lStrSelectedRoles, gLngUserId, gLngPostId, inputMap);
				
				gLogger.error("lStrFinalData344"+lStrFinalData);	
				if(lStrVeriRole != null){
					levelIdArr = lObjGpfWorkFlowConfig.getLevelIdForRolesMDC(lStrVeriRole);
					gLogger.error("levelIdArr"+levelIdArr);	
					
					
}
				

                gLogger.error("lStrlocid::::::::::::::::::::::::::::::::::::"+lStrlocid);
				gLogger.error("lLngPostId::::::::::::::::::::::::::::::::::::"+lLngPostId);
				gLogger.error("gLngLangId::::::::::::::::::::::::::::::::::::"+gLngLangId);
				gLogger.error("levelIdArr::::::::::::::::::::::::::::::::::::"+levelIdArr);
				gLogger.error("gLngUserId::::::::::::::::::::::::::::::::::::"+gLngUserId);
				gLogger.error("gLngPostId::::::::::::::::::::::::::::::::::::"+gLngPostId);
			
				List<String> levelIdArrNew = new ArrayList<String>();
				levelIdArrNew.add(levelIdArr);
				lSrtFinalMsg = "Verifier";
				lObjGpfWorkFlowConfig.insertDataForWorkflowMDC(lStrlocid, lLngPostId, gLngLangId, levelIdArrNew, inputMap, gLngUserId, gLngPostId);

				//String lStrRoleName ;
				//gLogger.error("lArrStrSelctdRoles.length::::::::::::::::::::::::::::::::::::"+lArrStrSelctdRoles.length);
				//if(lStrFinalData != null && lStrFinalData != "")
				//lSrtFinalMsg = "Role of Verifier has been assigned to user having his username as his sevaath ID "+userName +" and Password: ifms123";
				
					
			
					
	}catch (Exception e) {
		
	}
	return lSrtFinalMsg;
			
	}
   
   public String assigndeo(String lStrlocid,Long lLngPostId,String lStrDEORole,Map inputMap){
		
       
		String[] lArrStrSelctdRoles = null;
       String userName="";
       String sevaarthId="";
		List<Long> lLstSelectedRoles = new ArrayList<Long>();
		
		String levelIdArr = null;
		String lStrFinalData = "";
		String lSrtFinalMsg = "";
		//String lStrRoleArr[] =  new String[3];
		String lStrRoleArr ="";
       List<String> lStrSelectedRoles = new ArrayList<String>();
       lStrSelectedRoles.add(lStrDEORole);
		
     try{
				
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			
       lStrFinalData = lObjGpfWorkFlowConfig.assignRolesToUser(lLngPostId, lStrSelectedRoles, gLngUserId, gLngPostId, inputMap);
		
		gLogger.error("lStrFinalData344"+lStrFinalData);	
		if(lStrDEORole != null){
			levelIdArr = lObjGpfWorkFlowConfig.getLevelIdForRolesMDC(lStrDEORole);
			gLogger.error("levelIdArr"+levelIdArr);	
			
			
}
		

       gLogger.error("lStrlocid::::::::::::::::::::::::::::::::::::"+lStrlocid);
		gLogger.error("lLngPostId::::::::::::::::::::::::::::::::::::"+lLngPostId);
		gLogger.error("gLngLangId::::::::::::::::::::::::::::::::::::"+gLngLangId);
		gLogger.error("levelIdArr::::::::::::::::::::::::::::::::::::"+levelIdArr);
		gLogger.error("gLngUserId::::::::::::::::::::::::::::::::::::"+gLngUserId);
		gLogger.error("gLngPostId::::::::::::::::::::::::::::::::::::"+gLngPostId);
	
		List<String> levelIdArrNew = new ArrayList<String>();
		levelIdArrNew.add(levelIdArr);
		lSrtFinalMsg="Deo";
		lObjGpfWorkFlowConfig.insertDataForWorkflowMDC(lStrlocid, lLngPostId, gLngLangId, levelIdArrNew, inputMap, gLngUserId, gLngPostId);

		//String lStrRoleName ;
		//gLogger.error("lArrStrSelctdRoles.length::::::::::::::::::::::::::::::::::::"+lArrStrSelctdRoles.length);
		//if(lStrFinalData != null && lStrFinalData != "")
		//lSrtFinalMsg = "Role of DEO has been assigned to user having his username as his sevaath ID "+userName +" and Password: ifms123";
			

	
}catch (Exception e) {

}
return lSrtFinalMsg;
	
}
   
  // public ResultObject addRhoAsstToGpf(Map<String, Object> inputMap) {
   public ResultObject assignRho(String lStrlocid,Long lLngPostId,String lStrRhoRole,Map inputMap){
		gLogger.error("*********addRhoAsstToGpf***********");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstSchemeName = null;
		List lListActandRole = null;
		String activateFlagRho = "";
		String roleIdRho = "";
		String roleNameRho = "";
		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(null, serv.getSessionFactory());
			//long lLngPostId = Long.parseLong(StringUtility.getParameter("postId",request));                          // Added by vivek 31 jan 2017
			//String SelectedRole = StringUtility.getParameter("selectedRoles", request);
			//String lStrPostId = StringUtility.getParameter("postId", request);		
			
			gLogger.error("lLngPostId ***********"+lLngPostId);
			//Long Postid=Long.parseLong(lLngPostId);
			//lObjGpfWorkFlowConfig.InsertPostForRhoAsst1(Postid);
            lListActandRole = lObjGpfWorkFlowConfig.getActiveFlagRole(lLngPostId);                            // Added by vivek 31 jan 2017
            // Added by vivek 31 jan 2017
			Iterator itC=lListActandRole.iterator();                                    
			while(itC.hasNext()){
		    gLogger.error("while 1121 = ");
			Object[] tupleC = (Object[]) itC.next();
			activateFlagRho=tupleC[0].toString();
			roleNameRho=tupleC[1].toString();
			roleIdRho=tupleC[2].toString();                                      // Added by vivek 17 jun 2017
			gLogger.error("activateFlagRho WHILE = "+activateFlagRho);
			gLogger.error("roleIdRho ***WHILE = "+roleNameRho);
			}
			inputMap.put("activateFlagRho", activateFlagRho);
			inputMap.put("roleNameRho", roleNameRho);
			inputMap.put("roleIdRho", roleIdRho);
			
			gLogger.error("activateFlagRho 29000 = "+activateFlagRho);
			gLogger.error("roleIdRho ***29000 = "+roleNameRho);
			gLogger.error("inserted Successfullyy::::::::::::"+lLngPostId);
			 // ended by vivek 31 jan 2017
          if((activateFlagRho == "") || ((activateFlagRho.equalsIgnoreCase("1") || activateFlagRho.equalsIgnoreCase("2")) && !roleIdRho.equalsIgnoreCase("8000016"))){                                            // Condition Added by vivek 31 jan 2017
       	   lObjGpfWorkFlowConfig.InsertPostForRhoAsst1(lLngPostId);
			String Flag="true";
			String temp = getPendingXMLDocRho(Flag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();
			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");
          }
          else{                                                                                     //Else Condition added by Vivek 17 June 2017
       	   gLogger.error("activateFlagRho 1152 ");
       	   lObjGpfWorkFlowConfig.reAssignRoleRHOAST(lLngPostId);
  			String Flag="true";
  			String temp = getPendingXMLDocRho(Flag).toString();
  			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();
  			inputMap.put("ajaxKey", lStrResult);
  			lObjResultObj.setResultValue(inputMap);
  			lObjResultObj.setViewName("ajaxData");
  			gLogger.error("service complete");
          }

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
		}
		return lObjResultObj;

	}
   
   /*public String assignRho(String lStrlocid,Long lLngPostId,String lStrRhoRole,Map inputMap){
		
       
		String[] lArrStrSelctdRoles = null;
      String userName="";
      String sevaarthId="";
		List<Long> lLstSelectedRoles = new ArrayList<Long>();
		
		String levelIdArr = null;
		String lStrFinalData = "";
		String lSrtFinalMsg = "";
		//String lStrRoleArr[] =  new String[3];
		String lStrRoleArr ="";
      List<String> lStrSelectedRoles = new ArrayList<String>();
      lStrSelectedRoles.add(lStrRhoRole);
		
    try{
				
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			
      lStrFinalData = lObjGpfWorkFlowConfig.assignRolesToUser(lLngPostId, lStrSelectedRoles, gLngUserId, gLngPostId, inputMap);
		
		gLogger.error("lStrFinalData344"+lStrFinalData);	
		if(lStrRhoRole != null){
			levelIdArr = lObjGpfWorkFlowConfig.getLevelIdForRolesMDC(lStrRhoRole);
			gLogger.error("levelIdArr"+levelIdArr);	
			
			
}
		

      gLogger.error("lStrlocid::::::::::::::::::::::::::::::::::::"+lStrlocid);
		gLogger.error("lLngPostId::::::::::::::::::::::::::::::::::::"+lLngPostId);
		gLogger.error("gLngLangId::::::::::::::::::::::::::::::::::::"+gLngLangId);
		gLogger.error("levelIdArr::::::::::::::::::::::::::::::::::::"+levelIdArr);
		gLogger.error("gLngUserId::::::::::::::::::::::::::::::::::::"+gLngUserId);
		gLogger.error("gLngPostId::::::::::::::::::::::::::::::::::::"+gLngPostId);
	
		List<String> levelIdArrNew = new ArrayList<String>();
		levelIdArrNew.add(levelIdArr);
		lSrtFinalMsg = "RHO";
		lObjGpfWorkFlowConfig.insertDataForWorkflowMDC(lStrlocid, lLngPostId, gLngLangId, levelIdArrNew, inputMap, gLngUserId, gLngPostId);

		//String lStrRoleName ;
		//gLogger.error("lArrStrSelctdRoles.length::::::::::::::::::::::::::::::::::::"+lArrStrSelctdRoles.length);
		//if(lStrFinalData != null && lStrFinalData != "")
		//lSrtFinalMsg = "Role of RHO ASST has been assigned to user having his username as his sevaath ID "+userName +" and Password: ifms123";
		
		
		
			
}catch (Exception e) {

}
return lSrtFinalMsg;
	
}*/
   
   public ResultObject ValidateDdoCode(Map<String, Object> inputMap) 
    {

		gLogger.info("DeassignRoleDEO service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrDdoCode = "";
		String txtDdoCodeEmp = "";
		String lStrDdoName = "";
		String DDO_CODE="N";
		String DDO_NAME="N";
		List chkddocode = null;
try{
         GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			
	     txtDdoCodeEmp = StringUtility.getParameter(
					"txtDdoCode", request).trim();

			lStrDdoCode = StringUtility.getParameter(
					"txtDdoCodeEmpnew", request).trim();
			
			lStrDdoName = StringUtility.getParameter(
					"chkDdoName", request).trim();
			
            inputMap.put("chkDdoName", lStrDdoName);
		
	       chkddocode = lObjGpfWorkFlowConfig.getValidateDdoCode(txtDdoCodeEmp,lStrDdoName);
	       
	       if (!chkddocode.isEmpty() || chkddocode == null) {
				Object[] lLstDdoDtls1 = (Object[]) chkddocode.get(0);
       
				DDO_CODE = lLstDdoDtls1[0].toString();
				DDO_NAME = lLstDdoDtls1[1].toString();

       	 
            }
        
			inputMap.put("chkddocode", DDO_CODE);
			inputMap.put("lStrDdoCode", DDO_CODE);
			inputMap.put("lStrDdoName", DDO_NAME);
			inputMap.put("txtDdoCodeEmp", DDO_CODE);
			
			gLogger.error("DDO_CODE"+DDO_CODE);
			
			String temp = getDdoCodeMDC(DDO_CODE,DDO_NAME).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();
		
		

			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");


		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
			gLogger.error("Exception"+e);
		}
		return lObjResultObj;

	}

	private StringBuilder getDdoCodeMDC(String DDO_CODE,String DDO_NAME) 
	{

		gLogger.error("getDeassignXMLDEO called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DDO_CODE>");
		lStrBldXML.append(DDO_CODE);
		lStrBldXML.append("</DDO_CODE>");
		lStrBldXML.append("<DDO_NAME>");
		lStrBldXML.append(DDO_NAME);
		lStrBldXML.append("</DDO_NAME>");
		lStrBldXML.append("</XMLDOC>");
		
		return lStrBldXML;
	}
	
	public ResultObject ValidateSevaarthId(Map<String, Object> inputMap) 
    {

		gLogger.info("DeassignRoleDEO service called");
		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrDdoCode = "N";
		String txtDdoCodeEmp = "";
		//String chkSevaarthid = "";
		String lStrSevaarthId = "";
		String lStrEmployeeName = "";
		String SEVAARTH_ID="N";
		String EMP_NAME="N";
		String DDO_CODE="N";
		List chkSevaarthid = null;
		String EmpServend = "N";
		String empVerif = "N";
		String SevaId="";
		String EmpName="";
		String lStrDdoName = "";
	
try{
         GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			
	     txtDdoCodeEmp = StringUtility.getParameter(
					"txtDdoCode", request).trim();

			lStrDdoCode = StringUtility.getParameter(
					"txtDdoCodeEmp", request).trim();
			
			lStrSevaarthId = StringUtility.getParameter(
					"txtSevaarthId", request).trim();
			
			lStrEmployeeName = StringUtility.getParameter(
					"txtEmployeeName", request).trim();
			
			lStrDdoName = StringUtility.getParameter(
					"lStrDdoName", request).trim();
			
            inputMap.put("lStrDdoName", lStrDdoName);
            
            if ((!lStrDdoName.equalsIgnoreCase(""))&& ((txtDdoCodeEmp.equalsIgnoreCase("")) || (lStrDdoCode.equalsIgnoreCase("")))){
            	lStrDdoCode=lObjGpfWorkFlowConfig.getEmpDdoCode(lStrDdoName);
            }
			
			//SevaId = lObjGpfWorkFlowConfig.getSevaId(lStrSevaarthId);
			//EmpName = lObjGpfWorkFlowConfig.getEmpName(lStrEmployeeName,lStrDdoCode);
			EmpServend=lObjGpfWorkFlowConfig.getServiceEnd(lStrSevaarthId,lStrEmployeeName,lStrDdoCode);

			//if((!SevaId.equalsIgnoreCase("N")) || (!EmpName.equalsIgnoreCase("N"))){
			
			
			
            chkSevaarthid = lObjGpfWorkFlowConfig.getValidateSevarthId(lStrDdoCode,lStrSevaarthId,lStrEmployeeName);
			
			//empVerif = chkSevaarthid.toString();
			
			if (!chkSevaarthid.isEmpty() || chkSevaarthid == null) {
				Object[] lLstDdoDtls1 = (Object[]) chkSevaarthid.get(0);
        
				SEVAARTH_ID = lLstDdoDtls1[0].toString();
				EMP_NAME = lLstDdoDtls1[1].toString();
				DDO_CODE = lLstDdoDtls1[2].toString();
        	 
             }/*else{
            	 
            	 empVerif = chkSevaarthid.toString();
             }*/
			inputMap.put("chkSevaarthid", chkSevaarthid);
			inputMap.put("lStrDdoCode", lStrDdoCode);
			inputMap.put("txtDdoCodeEmp", txtDdoCodeEmp);
			
			gLogger.error("SEVAARTH_ID"+SEVAARTH_ID);
			//}
			//String temp = getValidateSevaarthIdMDC(SEVAARTH_ID,EMP_NAME,DDO_CODE,lStrDdoCode,EmpServend,empVerif,SevaId,EmpName).toString();
			String temp = getValidateSevaarthIdMDC(SEVAARTH_ID,EMP_NAME,DDO_CODE,lStrDdoCode,EmpServend).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", temp).toString();
		
		

			inputMap.put("ajaxKey", lStrResult);
			lObjResultObj.setResultValue(inputMap);
			lObjResultObj.setViewName("ajaxData");
			gLogger.error("service complete");

			
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, lObjResultObj, e, "Error is: ");
			gLogger.error("Exception"+e);
		}
		return lObjResultObj;

	}

	//private StringBuilder getValidateSevaarthIdMDC(String SEVAARTH_ID,String EMP_NAME,String DDO_CODE,String lStrDdoCode,String EmpServend,String empVerif,String SevaId,String EmpName) 
	private StringBuilder getValidateSevaarthIdMDC(String SEVAARTH_ID,String EMP_NAME,String DDO_CODE,String lStrDdoCode,String EmpServend) 
	{

		gLogger.error("getDeassignXMLDEO called");


		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<SEVAARTH_ID>");
		lStrBldXML.append(SEVAARTH_ID);
		lStrBldXML.append("</SEVAARTH_ID>");
		lStrBldXML.append("<EMP_NAME>");
		lStrBldXML.append(EMP_NAME);
		lStrBldXML.append("</EMP_NAME>");
		lStrBldXML.append("<DDO_CODE>");
		lStrBldXML.append(DDO_CODE);
		lStrBldXML.append("</DDO_CODE>");
		lStrBldXML.append("<lStrDdoCode>");
		lStrBldXML.append(lStrDdoCode);
		lStrBldXML.append("</lStrDdoCode>");
		lStrBldXML.append("<EmpServend>");
		lStrBldXML.append(EmpServend);
		lStrBldXML.append("</EmpServend>");
		/*lStrBldXML.append("<empVerif>");
		lStrBldXML.append(empVerif);
		lStrBldXML.append("</empVerif>");
		lStrBldXML.append("<SevaId>");
		lStrBldXML.append(SevaId);
		lStrBldXML.append("</SevaId>");
		lStrBldXML.append("<EmpName>");
		lStrBldXML.append(EmpName);
		lStrBldXML.append("</EmpName>");*/
		lStrBldXML.append("</XMLDOC>");
		
		return lStrBldXML;
	}
   
	public ResultObject popupDdoNameFromDdoCode(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		setSessionInfo(inputMap);
		String lStrDdoCode = null;
		String lStrDdoName = "";
		String lStrDdocodeEmp = "";
		Boolean lBlFlag = false;
		try {
			
		    GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
		    lStrDdoCode = StringUtility.getParameter("txtDdoCode", request).trim();	
		    
		    String lStrValidateDDOCodeFlag = lObjGpfWorkFlowConfig.getValidateDDOCodeFlag(lStrDdoCode);

			if(!"".equals(lStrDdoCode))
				lStrDdoName = lObjGpfWorkFlowConfig.getDdoNameFromDdoCode(lStrDdoCode);
				
			if(!"".equals(lStrDdoName))
				lBlFlag = true;

			String lSBStatus = getResponseXMLDocForDdoName(lBlFlag, lStrDdoName,lStrValidateDDOCodeFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		return resObj;

	}
	
	private StringBuilder getResponseXMLDocForDdoName(Boolean flag, String lStrDdoName,String lStrValidateDDOCodeFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<lStrDdoName>");
		lStrBldXML.append(lStrDdoName);
		lStrBldXML.append("</lStrDdoName>");
		lStrBldXML.append("<lStrValidateDDOCodeFlag>");
		lStrBldXML.append(lStrValidateDDOCodeFlag);
		lStrBldXML.append("</lStrValidateDDOCodeFlag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	public ResultObject getDdoForAutoComplete(Map<String, Object> inputMap) {
         gLogger.error("getDdoForAutoComplete service method called");
			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

			List finalList = null;
			String lStrdDdoName = null;
			try {
				setSessionInfo(inputMap);
				GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());

				lStrdDdoName = StringUtility.getParameter("searchKey", request).trim();
				String lStrUser = StringUtility.getParameter("userType", request);
				 //lStrDdoCode = StringUtility.getParameter("txtDdoCode", request).trim();
				
	            gLogger.error("lStrEmpName"+lStrdDdoName);
				gLogger.error("lStrUser"+lStrUser);

           if (lStrUser.equals("MDC")) {
					
					finalList = lObjGpfWorkFlowConfig.getDdoNameForAutoComplete(lStrdDdoName.toUpperCase());
	} 

				String lStrTempResult = null;
				if (!finalList.isEmpty()) {

					gLogger.error("finalList not empty");
					lStrTempResult = new AjaxXmlBuilder().addItems(finalList, "desc", "id", true).toString();
					gLogger.error("lStrTempResult"+lStrTempResult);

				}
				inputMap.put("ajaxKey", lStrTempResult);
				resObj.setResultValue(inputMap);
				resObj.setViewName("ajaxData");

			} catch (Exception e) {
				IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
			}

			return resObj;

		}
	
	public ResultObject getEmpNameForAutoCompleteMDC(Map<String, Object> inputMap) {

		gLogger.error("getEmpNameForAutoCompleteMDC service method called");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String lStrEmpName = null;
		String lStrDdoCode = null;
		try {
			setSessionInfo(inputMap);
			GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());

			lStrEmpName = StringUtility.getParameter("searchKey", request).trim();
			lStrDdoCode = StringUtility.getParameter("searchBy", request).trim();
			String lStrUser = StringUtility.getParameter("userType", request);
			 //lStrDdoCode = StringUtility.getParameter("txtDdoCode", request).trim();
			
            gLogger.error("lStrEmpName"+lStrEmpName);
			gLogger.error("lStrUser"+lStrUser);
			gLogger.error("lStrDdoCode"+lStrDdoCode);


			

			if (lStrUser.equals("MDC")) {
				
				finalList = lObjGpfWorkFlowConfig.getEmpNameForAutoComplete(lStrEmpName.toUpperCase(), lStrDdoCode,lStrUser);
} 

			String lStrTempResult = null;
			if (!finalList.isEmpty()) {

				gLogger.error("finalList not empty");
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList, "desc", "id", true).toString();
				gLogger.error("lStrTempResult"+lStrTempResult);

			}
			inputMap.put("ajaxKey", lStrTempResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		return resObj;

	}

	public ResultObject popupEmpNameFromSevarthId(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");


		String lStrSevaarthId = null;
		String lStrEmpName = "";
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);

			lStrSevaarthId = StringUtility.getParameter("sevaarthId", request).trim();		
			 GPFWorkFlowConfigDAO lObjGpfWorkFlowConfig = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());

			if(!"".equals(lStrSevaarthId))
				lStrEmpName = lObjGpfWorkFlowConfig.getEmpNameFromSevaarthId(lStrSevaarthId.toUpperCase());

			if(!"".equals(lStrEmpName))
				lBlFlag = true;

			String lSBStatus = getResponseXMLDocForEmpName(lBlFlag, lStrEmpName).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}

		return resObj;

	}
	
	private StringBuilder getResponseXMLDocForEmpName(Boolean flag, String lStrEmpName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<lEmpName>");
		lStrBldXML.append(lStrEmpName);
		lStrBldXML.append("</lEmpName>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}

