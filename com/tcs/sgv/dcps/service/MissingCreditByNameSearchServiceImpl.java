package com.tcs.sgv.dcps.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.MissingCreditByNameSearchDAOImpl;
import com.tcs.sgv.dcps.dao.MissingCreditDAO;
import com.tcs.sgv.dcps.dao.MissingCreditDAOImpl;
import com.tcs.sgv.dcps.dao.R1_InputDAO;
import com.tcs.sgv.dcps.dao.R1_InputDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpDetails;
import com.tcs.sgv.eis.valueobject.HrPayArrearPaybill;

public class MissingCreditByNameSearchServiceImpl extends ServiceImpl implements MissingCreditByNameSearchService{

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


	public ResultObject displayEmpName(Map inputMap) {
		
		setSessionInfo(inputMap);
	
		List lLstYears = null;
		List lLstAllDdo = null;
		List lLstAllTreasury = null;

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesMatchEntries = null;
		Long yearId = null;

		try {
			String fName = StringUtility.getParameter("fName", request);
			String mName = StringUtility.getParameter("mName", request);
			String lName = StringUtility.getParameter("lName", request);
			
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(OrgDdoMst.class, serv.getSessionFactory());	
			MissingCreditDAO lObjR1Form = new MissingCreditDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			String lStrUser = StringUtility.getParameter("user", request);
			
			lLstYears = lObjCommonDAO.getFinyears();
			lLstAllDdo = lObjR1Form.getAllDdoCode(gStrLocationCode);
			lLstAllTreasury = lObjR1Form.getAllTreasury();
			
			String Name=fName+"% "+mName+"% "+lName;
			
			setSessionInfo(inputMap);
			MissingCreditByNameSearchDAOImpl  missingCreditDAO= new  MissingCreditByNameSearchDAOImpl(null, serv.getSessionFactory());
			
			
			lListTreasuriesMatchEntries = missingCreditDAO.getEmpName(fName,mName,lName);
			inputMap.put("ListEmpEntries",lListTreasuriesMatchEntries);
			inputMap.put("totalRecords", lListTreasuriesMatchEntries.size());
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("AllDDOList", lLstAllDdo);
			inputMap.put("AllTreasury", lLstAllTreasury);
			resObj.setResultValue(inputMap);
			resObj.setViewName("TestCodeSearch");

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
	
	public ResultObject checkcode(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		
		List lLstYears = null;
		List lLstAllDdo = null;
		List lLstAllTreasury = null;
		
		try{
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(OrgDdoMst.class, serv.getSessionFactory());	
			MissingCreditDAO lObjR1Form = new MissingCreditDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			
			String lStrUser = StringUtility.getParameter("user", request);
			
			lLstYears = lObjCommonDAO.getFinyears();
			
			if(lStrUser.trim().equals("TO")){
				lLstAllDdo = lObjR1Form.getAllDdoCode(gStrLocationCode);
			}
			lLstAllTreasury = lObjR1Form.getAllTreasury();
			
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("AllDDOList", lLstAllDdo);
			inputMap.put("AllTreasury", lLstAllTreasury);
			inputMap.put("userType", lStrUser);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("TestCodeSearch");
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFDataEntryForm");
		}
		return resObj;
	}
}
