/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 25, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.FeedbackDAO;
import com.tcs.sgv.dcps.dao.FeedbackDAOImpl;
import com.tcs.sgv.dcps.valueobject.Feedback;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 May 25, 2011
 */
public class FeedbackServiceImpl extends ServiceImpl implements FeedbackService {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");

			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();

		} catch (Exception e) {

		}

	}

	public ResultObject loadFeedbackForm(Map<String, Object> inputMap)
			throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		try {
			setSessionInfo(inputMap);

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
			/* Gets DDO Name */
			String strDDOName = lObjDcpsCommonDAO
					.getDdoNameForCode(lStrDdoCode);

			/* Get Treasury Name */
			String strTreasury = lObjDcpsCommonDAO
					.getTreasuryNameForDDO(lStrDdoCode);
			String strTreasuryCode = lObjDcpsCommonDAO
					.getTreasuryCodeForDDO(lStrDdoCode);
			/* Get Department Name */
			List lLstDept = lObjDcpsCommonDAO
					.getDeptNameFromDdoCode(lStrDdoCode);

			inputMap.put("DDOName", strDDOName);
			inputMap.put("DDOCode", lStrDdoCode);
			inputMap.put("Treasury", strTreasury);
			inputMap.put("TreasuryCode", strTreasuryCode);
			inputMap.put("lLstDept", lLstDept);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSFeedbackForm");
		return resObj;
	}

	public ResultObject saveFeedback(Map<String, Object> inputMap)
			throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		Boolean lBFlag = false;

		try {
			setSessionInfo(inputMap);

			Feedback objFeedback = (Feedback) inputMap.get("Feedback");

			FeedbackDAO dcpsFeedback = new FeedbackDAOImpl(FeedbackDAO.class,
					serv.getSessionFactory());

			dcpsFeedback.create(objFeedback);
			lBFlag = true;
			
		} catch (Exception e) {

			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
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
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	public ResultObject showFeedbacksForSRKA(Map<String, Object> inputMap)
	throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		
		try {
			setSessionInfo(inputMap);
		
			FeedbackDAO lObjFeedbackDAO = new FeedbackDAOImpl(FeedbackDAO.class,
					serv.getSessionFactory());
			
			
			List lListFeedbacks = lObjFeedbackDAO.getFeedbacksForSRKA();
			
			inputMap.put("lListFeedbacks", lListFeedbacks);
		
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}
		
			resObj.setResultValue(inputMap);
			resObj.setViewName("FeedbacksForSRKA");
			return resObj;
		}

}
