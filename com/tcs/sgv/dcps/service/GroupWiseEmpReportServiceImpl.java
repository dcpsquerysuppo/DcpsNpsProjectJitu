package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.tcs.sgv.dcps.dao.GroupWiseEmpReportDAO;
import com.tcs.sgv.dcps.dao.GroupWiseEmpReportDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;

public class GroupWiseEmpReportServiceImpl extends ServiceImpl implements GroupWiseEmpReportService
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
			gLogger.error("Error is: "+ e, e);
		}
	}
	
	public ResultObject loadGroupWiseReport(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		List lLstYears = null;
		
		try{
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			lLstYears = lObjCommonDAO.getFinyears();
			
			inputMap.put("lLstYears", lLstYears);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("GroupWiseEmployees");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGroupWiseReport");
		}
		return resObj;
	}
	
	public ResultObject getEmpCountForGroup(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		List lLstGroupData = null;
		List lLstYears = null;
		
		try{
			GroupWiseEmpReportDAO lObjGroupWiseReport = new GroupWiseEmpReportDAOImpl(MstEmp.class,serv.getSessionFactory());
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			
			String lStrFinYear = StringUtility.getParameter("finYear", request);
			lLstGroupData = lObjGroupWiseReport.getEmpCountForYear(lStrFinYear);
			lLstYears = lObjCommonDAO.getFinyears();
			
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("EmpData", lLstGroupData);
			inputMap.put("FinYear", lStrFinYear);
			resObj.setResultValue(inputMap);
			resObj.setViewName("GroupWiseEmployees");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getEmpCountForGroup");
		}
		return resObj;
	}
}
