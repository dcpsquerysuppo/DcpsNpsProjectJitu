package com.tcs.sgv.common.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.DashBoardDao;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

public class DashBoardService extends ServiceImpl {

	private final Log logger = LogFactory.getLog(getClass());

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
		} catch (Exception e) {
		}
	}
		
	
	public ResultObject loadEmpList(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		logger.info("DATA For dashboard ");
		String month="";
		String year="";
		
		Map<Object,Object> dashboarddata=null;

		try {
			setSessionInfo(inputMap);
			DashBoardDao objUpdateDao = new DashBoardDao(null, serv.getSessionFactory());
			if (!StringUtility.getParameter("month", request).equalsIgnoreCase("")&& StringUtility.getParameter("month", request) != null && !StringUtility.getParameter("year", request).equalsIgnoreCase("")&& StringUtility.getParameter("month", request) != null) {
				month = StringUtility.getParameter("month", request).trim().toUpperCase();
				year = StringUtility.getParameter("year", request).trim().toUpperCase();
				 dashboarddata=objUpdateDao.getPayBill(month,year);
				 }
			else if (!StringUtility.getParameter("yearfin", request).equalsIgnoreCase("")&& StringUtility.getParameter("yearfin", request) != null) {
				year = StringUtility.getParameter("yearfin", request).trim().toUpperCase();
				
				dashboarddata=objUpdateDao.getPayBillfinancial(year);
			}
			else {
			 dashboarddata=objUpdateDao.getPayBill();	
			}
			StringBuffer strbuflag = new StringBuffer();
			  // using for-each loop for iteration over Map.entrySet()
	        for (Map.Entry<Object,Object> entry : dashboarddata.entrySet()) {
	        	strbuflag.append("<XMLDOC>");
				strbuflag.append("<KEY>");
				strbuflag.append(entry.getKey());
				strbuflag.append("</KEY>");
				strbuflag.append("<VALUE>");
				strbuflag.append(entry.getValue());
				strbuflag.append("</VALUE>");
				strbuflag.append("</XMLDOC>");
	        }
	        
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
						
			}
			catch (Exception e) {
				logger.info("Exception is " + e);
				objRes.setResultValue(null);
				objRes.setThrowable(e);
				objRes.setResultCode(ErrorConstants.ERROR);
				objRes.setViewName("errorPage");
			}
			return objRes;
	}
	
	
	public ResultObject loadgroupwiseEmpExp(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		logger.info("DATA For dashboard ");
		Map<Object,Object> groupwiseemployeeexpendeture=null;

		try {
			setSessionInfo(inputMap);
			DashBoardDao objUpdateDao = new DashBoardDao(null, serv.getSessionFactory());
			/*if (!StringUtility.getParameter("month", request).equalsIgnoreCase("")&& StringUtility.getParameter("month", request) != null && !StringUtility.getParameter("year", request).equalsIgnoreCase("")&& StringUtility.getParameter("month", request) != null) {
				month = StringUtility.getParameter("month", request).trim().toUpperCase();
				year = StringUtility.getParameter("year", request).trim().toUpperCase();
				 dashboarddata=objUpdateDao.getPayBill(month,year);
				 }
			else if (!StringUtility.getParameter("yearfin", request).equalsIgnoreCase("")&& StringUtility.getParameter("yearfin", request) != null) {
				year = StringUtility.getParameter("yearfin", request).trim().toUpperCase();
				
				dashboarddata=objUpdateDao.getPayBillfinancial(year);
			}
			else {
			 dashboarddata=objUpdateDao.getPayBill();	
			}
			*/
			groupwiseemployeeexpendeture=objUpdateDao.groupWiseYealyExp();
			StringBuffer strbuflag = new StringBuffer();
		
			  // using for-each loop for iteration over Map.entrySet()
	        for (Map.Entry<Object,Object> entry : groupwiseemployeeexpendeture.entrySet()) {
	        	strbuflag.append("<XMLDOC>");
				strbuflag.append("<KEY>");
				strbuflag.append(entry.getKey());
				strbuflag.append("</KEY>");
				strbuflag.append("<VALUE>");
				strbuflag.append(entry.getValue());
				strbuflag.append("</VALUE>");
				strbuflag.append("</XMLDOC>");
	        }
	        
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
						
			}
			catch (Exception e) {
				logger.info("Exception is " + e);
				objRes.setResultValue(null);
				objRes.setThrowable(e);
				objRes.setResultCode(ErrorConstants.ERROR);
				objRes.setViewName("errorPage");
			}
			return objRes;
	}
	
	public ResultObject loadDashBord(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		//DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
		DashBoardDao objUpdateDao = new DashBoardDao(null, serv.getSessionFactory());
		/* Get Months */
		List lLstMonths = objUpdateDao.getMonths();

		/* Get Years */
		List lLstYears = objUpdateDao.getFinyears();
		List lofYears = objUpdateDao.getYear();
		
		inputMap.put("YEARS", lLstYears);
		inputMap.put("MONTHS", lLstMonths);
		inputMap.put("ONLYYEAR", lofYears);
		objRes.setResultValue(inputMap);
		objRes.setViewName("dashboard");
		logger.info("year and month ");
		return objRes;
		
	}
	
	public ResultObject getfinyear(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		logger.info("year and month ");
		return objRes;
		
	}
}
