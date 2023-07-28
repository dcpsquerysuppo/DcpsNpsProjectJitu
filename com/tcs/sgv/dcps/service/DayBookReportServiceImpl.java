package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.R1_InputDAO;
import com.tcs.sgv.dcps.dao.R1_InputDAOImpl;

public class DayBookReportServiceImpl extends ServiceImpl implements DayBookReportService
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

	private void setSessionInfo(Map inputMap){
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
	
	public ResultObject loadDayBookReport(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		List lLstYears = null;
		List lLstMonths = null;
		List lLstAllTreasury = null;
		
		try{
			DcpsCommonDAO lObjCommomDao = new DcpsCommonDAOImpl(SgvcFinYearMst.class,serv.getSessionFactory());
			R1_InputDAO lObjR1Form = new R1_InputDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			
			lLstMonths = lObjCommomDao.getMonths();
			lLstYears = lObjCommomDao.getFinyearsAfterCurrYear();
			lLstAllTreasury = lObjR1Form.getAllTreasury();
			
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("AllTreasury", lLstAllTreasury);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("DayBookReport");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadDayBookReport");
		}
		return resObj;
	}
}
