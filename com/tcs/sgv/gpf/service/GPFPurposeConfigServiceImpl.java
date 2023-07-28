package com.tcs.sgv.gpf.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFPurposeConfigDAO;
import com.tcs.sgv.gpf.dao.GPFPurposeConfigDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 18, 2012
 */

public class GPFPurposeConfigServiceImpl extends ServiceImpl implements GPFPurposeConfigService
{
	private final Log gLogger = LogFactory.getLog(getClass());
	
	private HttpServletRequest request = null;
	
	private ServiceLocator serv = null;
	
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
			gLogger.error("Error is: "+ e, e);
		}
	}
	
	public ResultObject loadPurposeCongif(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		List llStRAPurpose = null;
		List llStNRAPurpose = null;
		
		try{
			GPFPurposeConfigDAO lObjPurposeConfig = new GPFPurposeConfigDAOImpl(MstGpfAdvance.class,serv.getSessionFactory());
			llStRAPurpose = lObjPurposeConfig.getAllRAPurpose();
			llStNRAPurpose = lObjPurposeConfig.getAllNRAPurpose();
			
			inputMap.put("RAList", llStRAPurpose);
			inputMap.put("NRAList", llStNRAPurpose);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ConfigurePurposeOfAdvance");
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadPurposeCongif");
		}
		return resObj;
	}
	
	public ResultObject AddNewPurposeCategory(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		String lStrLookupName = "";
		Long lLngParentLookUpId = null;
		String lStrType = "";
		
		try{
			GPFPurposeConfigDAO lObjPurposeConfig = new GPFPurposeConfigDAOImpl(MstGpfAdvance.class,serv.getSessionFactory());
			lStrLookupName = StringUtility.getParameter("purposeName", request);
			lStrType = StringUtility.getParameter("type", request);
				
			if(lStrType.equals("RA")){
				lLngParentLookUpId = 800001l;
			}else if(lStrType.equals("NRA")){
				lLngParentLookUpId = 800020l;
			}
			lObjPurposeConfig.insertCmnLookupMst(lStrLookupName, lLngParentLookUpId, gLngUserId, gLngPostId);
			
			String lSBStatus = getResponseXMLDoc("Add").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in AddNewPurposeCategory");
		}
		return resObj;
	}
	
	public ResultObject UpdatePurposeCategory(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		
		String lStrType = "";
		String []lArrStrLookupName = null;
		String []lArrStrLookupID = null;
		
		try{
			GPFPurposeConfigDAO lObjPurposeConfig = new GPFPurposeConfigDAOImpl(MstGpfAdvance.class,serv.getSessionFactory());			
			lStrType = StringUtility.getParameter("type", request);
				
			if(lStrType.equals("RA")){
				lArrStrLookupName = StringUtility.getParameterValues("txtPurposeNameRA", request);
				lArrStrLookupID = StringUtility.getParameterValues("hidPurposeIdRA", request);
			}else if(lStrType.equals("NRA")){
				lArrStrLookupName = StringUtility.getParameterValues("txtPurposeNameNRA", request);
				lArrStrLookupID = StringUtility.getParameterValues("hidPurposeIdNRA", request);
			}
			
			lObjPurposeConfig.updatePurposeCategory(lArrStrLookupName, lArrStrLookupID);
			
			String lSBStatus = getResponseXMLDoc("Update").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in UpdatePurposeCategory");
		}
		return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrFlag);
		lStrBldXML.append("</FLAG>");		
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
