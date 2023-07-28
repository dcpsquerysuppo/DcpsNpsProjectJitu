package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;



import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.dao.SancBudgetDAO;
import com.tcs.sgv.dcps.dao.SancBudgetDAOImpl;
import com.tcs.sgv.dcps.dao.TickerMessageDAO;
import com.tcs.sgv.dcps.dao.TickerMessageDAOImpl;
import com.tcs.sgv.dcps.valueobject.NoticeBoard;
import com.tcs.sgv.dcps.valueobject.SanctionedBudget;
import com.tcs.sgv.dcps.valueobject.TickerMessage;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class TickerMessageServiceImpl extends ServiceImpl implements
TickerMessageService{
	

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gStrPostId = SessionHelper.getPostId(inputMap).toString();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}
	
	public ResultObject loadTickerMessage(Map inputMap){

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		
		
		try{
			/* Sets the Session Information */
			setSessionInfo(inputMap);
			TickerMessageDAO dcpsTickerMessage = new TickerMessageDAOImpl(
					TickerMessageDAO.class, serv.getSessionFactory());			
			List tickerMessageList = dcpsTickerMessage.getTickerMessageList();
			inputMap.put("tickerMessageList", tickerMessageList);
		} catch (Exception e) {
		
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}
		
		
		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSTickerMessageEntry");
		return resObj;
	}
	public ResultObject saveTickerMessage(Map inputMap){

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBFlag = false;
		
		try{
			/* Sets the Session Information */
			setSessionInfo(inputMap);
	
			TickerMessage objTickerMessage = (TickerMessage)inputMap.get("TickerMessage");
			String editFlag = (String)inputMap.get("editFlag");
			gLogger.error(" editFlag "+editFlag);
			TickerMessageDAO dcpsTickerMessage = new TickerMessageDAOImpl(
					TickerMessageDAO.class, serv.getSessionFactory());
			if(editFlag.equals("N"))
			dcpsTickerMessage.create(objTickerMessage);
			else if(editFlag.equals("Y"))
				dcpsTickerMessage.update(objTickerMessage);
			lBFlag = true;
		} catch (Exception e) {
		
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();
		
		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}
	private StringBuilder getResponseXMLDoc(boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("  </Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	public ResultObject showTickerMessage(Map inputMap){
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			setSessionInfo(inputMap);
			
			TickerMessageDAO lObjTickerMessageDAO = new TickerMessageDAOImpl(null, serv
					.getSessionFactory());
			List strTickerMessage = lObjTickerMessageDAO.getTickerMessage();
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append("<XMLDOC>");

			Iterator it=strTickerMessage.iterator();
			while(it.hasNext()){
				StringBuffer strTickerMessages=new StringBuffer();
				Object obj[] = (Object[])it.next();
				if(obj[0] != null)
				strTickerMessages.append(obj[0].toString());
				else
					strTickerMessages.append("");
				if(obj[1] != null)
					if(strTickerMessages.length()>0)
						strTickerMessages.append(" --**-- " + obj[1].toString());
					else 
						strTickerMessages.append(obj[1].toString());
				
				if(obj[2] != null)
					if(strTickerMessages.length()>0)
						strTickerMessages.append(" --**-- " + obj[2].toString());
					else 
						strTickerMessages.append(obj[2].toString());
				lStrBldXML.append("<genMsg>");
				lStrBldXML.append("<![CDATA[");
				lStrBldXML.append(strTickerMessages.toString());
				lStrBldXML.append("]]>");	
				lStrBldXML.append("</genMsg>");
				strTickerMessages=null;
			}
			
			inputMap.put("TickerMessage", strTickerMessage);
			
			lStrBldXML.append("</XMLDOC>");
			String lStrTempResult = null;				
			
			lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrTempResult);
			gLogger.info("post id generated.."+lStrTempResult);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			//return resObj;
		
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}
		
		//resObj.setResultValue(inputMap);
		//resObj.setViewName("DCPSTickerMessage");
		return resObj;
	}
	
	public ResultObject deleteTickerMessage(Map inputMap){
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			gLogger.info("post id generated..");
			setSessionInfo(inputMap);
			TickerMessageDAO lObjTickerMessageDAO = new TickerMessageDAOImpl(null, serv
					.getSessionFactory());
			String tickerMessagePk = StringUtility.getParameter("tickerMessagePk", request) != null ?
					StringUtility.getParameter("tickerMessagePk", request) : ""; 
			int rowsUpdated = 0;
			if(!tickerMessagePk.equals(""))
				rowsUpdated = lObjTickerMessageDAO.deleteTickerMessage(tickerMessagePk);
			
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<rowsUpdated>");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(rowsUpdated);
			lStrBldXML.append("]]>");	
			lStrBldXML.append("</rowsUpdated>");
			lStrBldXML.append("</XMLDOC>");
			String lStrTempResult = null;				
			
			lStrTempResult = new AjaxXmlBuilder().addItem("ajax_Key",lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrTempResult);
			gLogger.info("post id generated.."+lStrTempResult);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		}
		catch(Exception e){
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}
		return resObj;
	}
}
