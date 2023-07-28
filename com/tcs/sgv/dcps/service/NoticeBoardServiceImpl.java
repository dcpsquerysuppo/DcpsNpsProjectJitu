/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 27, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import com.tcs.sgv.dcps.dao.ArrearsDAO;
import com.tcs.sgv.dcps.dao.ArrearsDAOImpl;
import com.tcs.sgv.dcps.dao.FeedbackDAO;
import com.tcs.sgv.dcps.dao.FeedbackDAOImpl;
import com.tcs.sgv.dcps.dao.NoticeBoardDAO;
import com.tcs.sgv.dcps.dao.NoticeBoardDAOImpl;
import com.tcs.sgv.dcps.dao.SancBudgetDAO;
import com.tcs.sgv.dcps.dao.SancBudgetDAOImpl;
import com.tcs.sgv.dcps.valueobject.Feedback;
import com.tcs.sgv.dcps.valueobject.NoticeBoard;



/**
 * Class Description - 
 *
 *
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0
 * May 27, 2011
 */
public class NoticeBoardServiceImpl extends ServiceImpl implements NoticeBoardService{
	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

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
	private StringBuilder getResponseXMLDoc(boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("  </Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	public ResultObject loadNoticeBoard(Map<String, Object> inputMap)
	throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		try {
			setSessionInfo(inputMap);
			
			NoticeBoardDAO lObjNoticeBoardDAO = new NoticeBoardDAOImpl(null, serv
					.getSessionFactory());
			List lLstSavedNotices = lObjNoticeBoardDAO.getAllNotices();
			inputMap.put("NoticeList", lLstSavedNotices);

		
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}
		
		resObj.setResultValue(inputMap);
		resObj.setViewName("DcpsNoticeBoard");
		return resObj;
	}
	public ResultObject saveNotice(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		
		Boolean lBFlag = false;
		
		try {
			setSessionInfo(inputMap);
			
			NoticeBoard objNoticeBoard = (NoticeBoard)inputMap.get("NoticeBoard");

			NoticeBoardDAO dcpsNoticeBoard = new NoticeBoardDAOImpl(
					NoticeBoardDAO.class, serv.getSessionFactory());
			
			dcpsNoticeBoard.create(objNoticeBoard);
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
}
