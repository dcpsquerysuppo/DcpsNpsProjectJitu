package com.tcs.sgv.lna.service;

import java.util.ArrayList;
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
import com.tcs.sgv.lna.dao.LNAWorkFlowConfigDAO;
import com.tcs.sgv.lna.dao.LNAWorkFlowConfigDAOImpl;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 12, 2012
 */

public class LNAWorkFlowConfigServiceImpl extends ServiceImpl implements LNAWorkFlowConfigService
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

	public ResultObject loadLNAWorkFlowConfig(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");

		List lLstAllRoles = null;
		List lLstAllUsers = null;
		String lStrHODtls = null;
		String lStrDdoCode = "";

		try{
			LNAWorkFlowConfigDAO lObjLNAWorkFlowConfig = new LNAWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			lStrDdoCode = lObjLNAWorkFlowConfig.getDdoCode(gLngPostId);
			lLstAllUsers = lObjLNAWorkFlowConfig.getAllUsersForDDO(lStrDdoCode);
			lLstAllRoles = lObjLNAWorkFlowConfig.getAllRoles();
			lStrHODtls = lObjLNAWorkFlowConfig.getActiveHOLst(gStrLocationCode);

			inputMap.put("currHoDtls", lStrHODtls);
			inputMap.put("AllUsers", lLstAllUsers);
			inputMap.put("AllRoles", lLstAllRoles);
			resObj.setResultValue(inputMap);
			resObj.setViewName("WorkFlowConfigLNA");
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadLNAWorkFlowConfig");
		}
		return resObj;
	}

	public ResultObject getAllRolesForUsers(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		

		String lStrAllRoles = "";
		Long lLngPostId = null;
		try{
			LNAWorkFlowConfigDAO lObjLNAWorkFlowConfig = new LNAWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());

			lLngPostId = Long.parseLong(StringUtility.getParameter("postId",request));

			lStrAllRoles = lObjLNAWorkFlowConfig.getAllRolesForUser(lLngPostId);
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
		String chkHOEntry= "";
		try{
			LNAWorkFlowConfigDAO lObjLNAWorkFlowConfig = new LNAWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());
			GPFWorkFlowConfigDAO lObjGPFWoConfigDAO = new GPFWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());			
			lLngPostId = Long.parseLong(StringUtility.getParameter("postId",request));

			userName=StringUtility.getParameter("userName",request);
			String user[]=userName.split(",");

			lStrSelectedRoles = StringUtility.getParameter("selectedRoles", request);			

			if(!lStrSelectedRoles.equals("")){
				lArrStrSelctdRoles = lStrSelectedRoles.split(",");

				for(Integer lIntCnt=0; lIntCnt < lArrStrSelctdRoles.length; lIntCnt++){
					lLstSelectedRoles.add(Long.parseLong(lArrStrSelctdRoles[lIntCnt]));
					lLstSlctdRolesInt.add(Integer.parseInt(lArrStrSelctdRoles[lIntCnt]));

					/*if(lArrStrSelctdRoles[lIntCnt].equals("800001")){
						lStrVerifierFlag = lObjLNAWorkFlowConfig.chkEntryForVerifier(gStrLocationCode);
					}*/
					if(lArrStrSelctdRoles[lIntCnt].equals("800002")){
						chkHOEntry = lObjLNAWorkFlowConfig.chkRoleHoEntry(gStrLocationCode);
						//lStrHOFlag = lObjLNAWorkFlowConfig.chkEntryForHO(gStrLocationCode);
					}
				}
			}
			//modified by aditya

			/*if(!lStrVerifierFlag.equals("Y") || !lStrHOFlag.equals("Y")){*/

			if(chkHOEntry.equals("") || chkHOEntry.equals("N")){

				lStrFinalData = lObjLNAWorkFlowConfig.assignRolesToUser(lLngPostId, lLstSelectedRoles, gLngUserId, gLngPostId, inputMap);

				/*if(lLstSlctdRolesInt != null && lLstSlctdRolesInt.size() > 0){
					levelIdArr = lObjLNAWorkFlowConfig.getLevelIdForRoles(lLstSlctdRolesInt);
				}*/


				/// for Loan and advance level id for HO is 20
				levelIdArr = new ArrayList();
				if(lLstSlctdRolesInt != null && lLstSlctdRolesInt.size() > 0){
					levelIdArr.add(new Integer(20));
				}
				lObjLNAWorkFlowConfig.insertDataForWorkflow(gStrLocationCode, lLngPostId, gLngLangId, levelIdArr, inputMap, gLngUserId, gLngPostId);
				levelIdArr= null;
				/// for GPF grp D level id for HO is 30
				levelIdArr = new ArrayList();
				if(lLstSlctdRolesInt != null && lLstSlctdRolesInt.size() > 0){
					levelIdArr.add(new Integer(30));
				}
				lObjGPFWoConfigDAO.insertDataForWorkflow(gStrLocationCode, lLngPostId, gLngLangId, levelIdArr, inputMap, gLngUserId, gLngPostId);
			}
			if(lStrFinalData.contains("I") && lStrFinalData.contains("R")){
				lSrtFinalMsg = "Role(s) have been assigned to user and Role(s) have been removed from user";
			}else if(lStrFinalData.contains("I")){
				lSrtFinalMsg = "Role(s) have been assigned to user with USERNAME:" +user[1]+ " and Password: ifms123";
			}else if(lStrFinalData.contains("R")){
				lSrtFinalMsg = "Role(s) have been removed from user";
			}else{
				lSrtFinalMsg = "No Role is mapped or removed from user";
			}

			if(chkHOEntry.equals("Y"))
				lSrtFinalMsg = "Role of HO is already assigned.";
			/*}else{
				if(lStrVerifierFlag.equals("Y")){
					lSrtFinalMsg = "Verifier";
				}else if(lStrHOFlag.equals("Y")){
					lSrtFinalMsg = "HO";
				}
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
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadLNAWorkFlowConfig");
		}
		return resObj;
	}

	public ResultObject chkHOMappedOrNot(Map inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		

		String hoMapdOrNot = "N";
		Long lLngPostId = null;
		boolean roleOfUser = false;
		String lStrDdoCode = null;		
		try{
			LNAWorkFlowConfigDAO lObjLNAWorkFlowConfig = new LNAWorkFlowConfigDAOImpl(RltLevelRole.class, serv.getSessionFactory());

			//lLngPostId = Long.parseLong(StringUtility.getParameter("postId",request));
			roleOfUser = lObjLNAWorkFlowConfig.isDDOOrNot(gLngPostId.toString());
			if(roleOfUser){
				lStrDdoCode = lObjLNAWorkFlowConfig.getDdoCode(gLngPostId);
					if(lObjLNAWorkFlowConfig.lnaActiveDdoOrNot(lStrDdoCode)){
						hoMapdOrNot = lObjLNAWorkFlowConfig.chkRoleHoEntry(gStrLocationCode);	
					}
					else hoMapdOrNot = "Y";
			}
			else hoMapdOrNot = "Y";

			String lSBStatus = getResponseXMLDoc(hoMapdOrNot).toString();
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

	private StringBuilder getResponseXMLDoc(String lStrData) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrData);
		lStrBldXML.append("</FLAG>");		
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}


}
