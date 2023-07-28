/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 30, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
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
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DARateDAO;
import com.tcs.sgv.dcps.dao.DARateDAOImpl;
import com.tcs.sgv.dcps.valueobject.DARate;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Apr 30, 2011
 */
public class DARateServiceImpl extends ServiceImpl implements DARateService {

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
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {

		}

	}

	public ResultObject saveDARateEntry(Map<String, Object> inputMap)
			throws Exception {

		Log logger = LogFactory.getLog(getClass());
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		Boolean lBFlag = false;
		Long lLngdaRateIdPk = null;
		setSessionInfo(inputMap);

		try {
			DARateDAO daRateDAO = new DARateDAOImpl(DARateDAO.class, serv
					.getSessionFactory());
			DARate lObjDARate = (DARate) inputMap.get("DCPSDARate");
			Integer lIntTotalCount = Integer.parseInt(StringUtility
					.getParameter("totalEntries", request));

			if (lIntTotalCount != 0) {
				String strToDate = StringUtility.getParameter("txtFromDate",
						request);
				Date ToDate = null;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"dd/MM/yyyy");

				if (!(strToDate.equals(""))) {
					ToDate = simpleDateFormat.parse(strToDate);
				}
				long dateMillis = ToDate.getTime();
				long dayInMillis = 1000 * 60 * 60 * 24;// subtract a day
				dateMillis = dateMillis - dayInMillis;
				ToDate.setTime(dateMillis);// set Date to new time
				String strPayComm = StringUtility.getParameter("cmbPayComm",
						request);
				daRateDAO.UpdatePreviousDARate(ToDate, strPayComm);
			}

			lLngdaRateIdPk = IFMSCommonServiceImpl.getNextSeqNum(
					"MST_DCPS_DA_RATE", inputMap);
			lObjDARate.setDaRateIdPk(lLngdaRateIdPk);
			daRateDAO.create(lObjDARate);
			
			lBFlag = true;

		} catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			logger.error(" Error in getDigiSig " + e, e);
		}
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject loadDARateEntry(Map<String, Object> inputMap)
			throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		try {
			DARateDAO lObjcmnDCPSDARateDAO = new DARateDAOImpl(null, serv
					.getSessionFactory());
			List lLstPayCommission = IFMSCommonServiceImpl.getLookupValues(
					"PayCommissionDCPS", SessionHelper.getLangId(inputMap),
					inputMap);
			inputMap.put("lLstPayCommission", lLstPayCommission);

			List lLstDARates = lObjcmnDCPSDARateDAO.getAllDARates();
			inputMap.put("DARateList", lLstDARates);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("DARateEntry");
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

}
