package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DcpsEmpStatReportDAOImpl;
import com.tcs.sgv.dcps.dao.DcpsEmpStatReportDao;
import com.tcs.sgv.dcps.dao.DdoRegDAOImpl;
import com.tcs.sgv.dcps.dao.DdoRegDao;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTOImpl;
import com.tcs.sgv.dcps.dao.NsdlReportsDAO;
import com.tcs.sgv.dcps.dao.NsdlReportsDAOImpl;
import com.tcs.sgv.dcps.dao.NsdlSrkaPranFileGenerationDAO;
import com.tcs.sgv.dcps.dao.NsdlSrkaPranFileGenerationDAOImpl;
import com.tcs.sgv.dcps.dao.OldTransactionIdUpdateDAO;
import com.tcs.sgv.dcps.dao.OldTransactionIdUpdateDAOImpl;
import com.tcs.sgv.dcps.dao.TranIdGenBillNotGenDAO;
import com.tcs.sgv.dcps.dao.TranIdGenBillNotGenDAOImpl;

public class DcpsEmpStatReportService extends ServiceImpl {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrLocationCode = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private String gStrUserId = null; /* STRING USER ID */

	/* Global Variable for UserId */
	Long gLngUserId = null;

	private Long gLngDBId = null; /* DB ID */

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
			session = request.getSession();
			gStrPostId = SessionHelper.getPostId(inputMap).toString();
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	
	
	public ResultObject getDataOnLoad(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesMatchEntries = null;
		Long yearId = null;
		Long totalSAmount = 0L;
		Long totalTAmount = 0L;
		Long AcMain = null;
		try {

			setSessionInfo(inputMap);

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			DcpsEmpStatReportDao lObjDcpsEmpStatReportDAO = new DcpsEmpStatReportDAOImpl(null, serv.getSessionFactory());
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			List lstDataList = lObjDcpsEmpStatReportDAO.getDataOnLoad();

			inputMap.put("lstDataList", lstDataList);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DcpsEmpStatReport");

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	
	
	
	public ResultObject getDataOnLoadForTo(Map inputMap) {
		setSessionInfo(inputMap);
		gLogger.info("gStrLocationCode####"+gStrLocationCode);

		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesMatchEntries = null;
		Long yearId = null;
		Long totalSAmount = 0L;
		Long totalTAmount = 0L;
		Long AcMain = null;
		try {

			setSessionInfo(inputMap);

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			DcpsEmpStatReportDao lObjDcpsEmpStatReportDAO = new DcpsEmpStatReportDAOImpl(null, serv.getSessionFactory());
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			List lstDataListForTo = lObjDcpsEmpStatReportDAO.getDataOnLoadForTo(gStrLocationCode);

			inputMap.put("lstDataListForTo", lstDataListForTo);
			gLogger.info("lstDataListForToSize####"+lstDataListForTo);
			resObj.setResultValue(inputMap);
			resObj.setViewName("DcpsEmpStatReport");

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}
	
	
	
}