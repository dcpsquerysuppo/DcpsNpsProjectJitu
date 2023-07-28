package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.ChangeAdminFieldDeptForDdoDAO;
import com.tcs.sgv.dcps.dao.ChangeAdminFieldDeptForDdoDAOImpl;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Sep 10, 2012
 */

public class ChangeAdminFieldDeptForDDOServiceImpl extends ServiceImpl implements ChangeAdminFieldDeptForDDOService
{
	private final Log gLogger = LogFactory.getLog(getClass());

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is: " + e, e);
		}
	}
	
	public ResultObject loadChangeAdminFieldDeptForm(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		
		try{
			ChangeAdminFieldDeptForDdoDAO lObjChangeAdminFieldDept = new ChangeAdminFieldDeptForDdoDAOImpl(OrgDdoMst.class,serv.getSessionFactory());
			List lLstAdminDept = lObjChangeAdminFieldDept.getAllAdminDepartment();			
			
			inputMap.put("Admin", lLstAdminDept);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ChangeAdminFieldDeptForDDO");
		}catch(Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadChangeAdminFieldDeptForm");
		}
		return resObj;
	}
	
	public ResultObject getAdminAndFieldDeptForDDO(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		
		String lStrDdoDtls = "";
		String []lStrData = null;
		String lStrAdminCode = "";
		String lStrFieldCode = "";
		String lStrAdminDept = "";
		String lStrFieldDept = "";
		
		try{
			ChangeAdminFieldDeptForDdoDAO lObjChangeAdminFieldDept = new ChangeAdminFieldDeptForDdoDAOImpl(OrgDdoMst.class,serv.getSessionFactory());
			String lStrDdoCode = StringUtility.getParameter("ddoCode", request);
			
			lStrDdoDtls = lObjChangeAdminFieldDept.getAdminAndFieldDeptOfDdo(lStrDdoCode);
			
			if(lStrDdoDtls.length() > 0){
				lStrData = lStrDdoDtls.split("#");
				lStrAdminCode = lStrData[0];
				lStrAdminDept = lStrData[1];
				lStrFieldDept = lStrData[2];
				lStrFieldCode = lStrData[3];
			}			
			
			String lSBStatus = getResponseXMLDocAdminFieldDeptOfDDO(lStrFieldCode, lStrAdminDept, lStrFieldDept).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		}catch(Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getAdminAndFieldDeptForDDO");
		}
		return resObj;
	}
	
	public ResultObject changeAdminAndFieldDeptForDDO(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		List lLstDesgnOld = null;
		List lLstDesgnNew = null;
		List lLstCadreOld = null;
		List lLstCadreNew = null;
		
		String lStrDsgnFlag = "N";
		String lStrCadreFlag = "N";
		String lSBStatus = "";
		String lStrExist = "";
		
		Object []lObj = null;
		Long lLngCadreId = null;
		Integer lIntUpdtCnt = 1;
		
		Map<String, Long> lMapOldCadre = new HashMap<String, Long>();
		Map<String, Long> lMapNewCadre = new HashMap<String, Long>();
		Map<Integer, String> lMapCadreUpdate = new HashMap<Integer, String>();
		
		try{
			ChangeAdminFieldDeptForDdoDAO lObjChangeAdminFieldDept = new ChangeAdminFieldDeptForDdoDAOImpl(OrgDdoMst.class,serv.getSessionFactory());
			
			String lStrDdoCode = StringUtility.getParameter("ddoCode", request).trim();
			Long lLngFieldDeptOld = Long.parseLong(StringUtility.getParameter("fieldDeptOld", request).trim());
			Long lLngFieldDeptNew = Long.parseLong(StringUtility.getParameter("fieldDeptNew", request).trim());
			String lStrAdminDeptNew = StringUtility.getParameter("adminDeptNew", request);
			
			
			//lLstDesgnOld = lObjChangeAdminFieldDept.getAllDesignation(lLngFieldDeptOld);
			lLstDesgnOld = lObjChangeAdminFieldDept.getAllDesignationUsed(lLngFieldDeptOld,lStrDdoCode);
			lLstDesgnNew = lObjChangeAdminFieldDept.getAllDesignation(lLngFieldDeptNew);			
			
			for(Integer lIntOld = 0; lIntOld < lLstDesgnOld.size(); lIntOld++)
			{
				lStrExist = "N";
				for(Integer lIntNew = 0; lIntNew < lLstDesgnNew.size(); lIntNew++)
				{
					if(lLstDesgnNew.get(lIntNew).toString().trim().equalsIgnoreCase(lLstDesgnOld.get(lIntOld).toString().trim())){
						lStrExist = "Y";
						break;
					}
				}
				
				
				if(lStrExist == "N"){
					lStrDsgnFlag = "Y";
					break;
				}
			}
			
			//lLstCadreOld = lObjChangeAdminFieldDept.getAllCadreNames(lLngFieldDeptOld);
			lLstCadreOld = lObjChangeAdminFieldDept.getAllCadreNamesUsed(lLngFieldDeptOld,lStrDdoCode);
			lLstCadreNew = lObjChangeAdminFieldDept.getAllCadreNames(lLngFieldDeptNew);			
			
			for(Integer lIntOld = 0; lIntOld < lLstCadreOld.size(); lIntOld++)
			{
				lStrExist = "N";
				for(Integer lIntNew = 0; lIntNew < lLstCadreNew.size(); lIntNew++)
				{
					if(lLstCadreNew.get(lIntNew).toString().trim().equalsIgnoreCase(lLstCadreOld.get(lIntOld).toString().trim())){
						lStrExist = "Y";
						break;
					}
				}
				
				
				if(lStrExist == "N"){
					lStrCadreFlag = "Y";
					break;
				}
			}
			
			
			if(lStrDsgnFlag.equals("N") && lStrCadreFlag.equals("N"))
			{
				lLstCadreOld = lObjChangeAdminFieldDept.getAllCadreIDName(lLngFieldDeptOld);
				for(Integer lIntCnt = 0; lIntCnt < lLstCadreOld.size(); lIntCnt++)
				{
					lObj = (Object[]) lLstCadreOld.get(lIntCnt);
					lMapOldCadre.put(lObj[1].toString().toUpperCase().trim(), Long.parseLong(lObj[0].toString()));
				}
				
				lLstCadreNew = lObjChangeAdminFieldDept.getAllCadreIDName(lLngFieldDeptNew);
				for(Integer lIntCnt = 0; lIntCnt < lLstCadreNew.size(); lIntCnt++)
				{
					lObj = (Object[]) lLstCadreNew.get(lIntCnt);
					lMapNewCadre.put(lObj[1].toString().toUpperCase().trim(), Long.parseLong(lObj[0].toString()));
					
					lLngCadreId = lMapOldCadre.get(lObj[1].toString().toUpperCase().trim());
					if(lLngCadreId != null){
						lMapCadreUpdate.put(lIntUpdtCnt, lLngCadreId.toString() + "#" + lObj[0].toString());
						lIntUpdtCnt++;
					}
				}
								
				lObjChangeAdminFieldDept.updateData(lStrDdoCode, lStrAdminDeptNew, lLngFieldDeptNew.toString(), lMapCadreUpdate);
				
				lSBStatus = getResponseXMLDocAdminFieldDeptOfDDO("true", "", "").toString();
			}else{
				if(lStrDsgnFlag.equals("Y") && lStrCadreFlag.equals("Y")){
					lSBStatus = getResponseXMLDocAdminFieldDeptOfDDO("CD", "", "").toString();
				}else if(lStrDsgnFlag.equals("Y")){
					lSBStatus = getResponseXMLDocAdminFieldDeptOfDDO("D", "", "").toString();
				}else if(lStrCadreFlag.equals("Y")){
					lSBStatus = getResponseXMLDocAdminFieldDeptOfDDO("C", "", "").toString();
				}else{
					lSBStatus = getResponseXMLDocAdminFieldDeptOfDDO("", "", "").toString();
				}				
			}
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
	
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		}catch(Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in changeAdminAndFieldDeptForDDO");
		}
	return resObj;
}
		
	
	private StringBuilder getResponseXMLDocAdminFieldDeptOfDDO(String lStrAdminCode, String lStrAdminDept, String lStrFieldDept) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<AdminCode>");
		lStrBldXML.append(lStrAdminCode);
		lStrBldXML.append("</AdminCode>");
		lStrBldXML.append("<AdminDept>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(lStrAdminDept.trim());
		lStrBldXML.append("]]>");
		lStrBldXML.append("</AdminDept>");
		lStrBldXML.append("<FieldDept>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(lStrFieldDept.trim());
		lStrBldXML.append("]]>");
		lStrBldXML.append("</FieldDept>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
