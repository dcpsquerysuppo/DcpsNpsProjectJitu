package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.EmployeeCornerDAOImpl;
import com.tcs.sgv.dcps.dao.SearchEmployeeDAO;
import com.tcs.sgv.dcps.dao.SearchEmployeeDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.eis.service.GeneratePaySlipService;

public class EmployeeCornerServiceImpl extends ServiceImpl{
	
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
	
	public ResultObject getEmployeeReport(Map inputMap){
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			setSessionInfo(inputMap);
            long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
            
			  CmnLookupMstDAO lookupDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
	    		List monthList = lookupDAO.getAllChildrenByLookUpNameAndLang("Month", langId);
	            
	    	    		
	    		List yearList = lookupDAO.getAllChildrenByLookUpNameAndLang("Year",langId);
	    		Collections.reverse(yearList);
	    		
	    		inputMap.put("yearList", yearList);
	    		inputMap.put("monthList", monthList);
	    		
	    		java.util.Calendar calendar = java.util.Calendar.getInstance(); 
	    		int curYear=calendar.get(java.util.Calendar.YEAR); 
	    		int curMonth=calendar.get(java.util.Calendar.MONTH);
	    		inputMap.put("curYear", curYear);
	    		inputMap.put("curMonth", (curMonth+1));
	    		inputMap.put("totalRecords", 0);
			/* Sets the Session Information */
	    		
	    		
			//setSessionInfo(inputMap);
			resObj.setResultValue(inputMap);
	        resObj.setViewName("EmployeeCorner");
			/*
			 * Checks if request is sent by click of GO button or the page is
			 * loaded for the first time.
			 */

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
	public ResultObject searchEmployee(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List listSavedRequests = null;
		Integer totalRecords = 0;

		try {
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			setSessionInfo(inputMap);
			long langId=StringUtility.convertToLong(loginDetailsMap.get("langId").toString());
            
			  CmnLookupMstDAO lookupDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
	    		List monthList = lookupDAO.getAllChildrenByLookUpNameAndLang("Month", langId);
	            
	    	    		
	    		List yearList = lookupDAO.getAllChildrenByLookUpNameAndLang("Year",langId);
	    		Collections.reverse(yearList);
	    		
	    		inputMap.put("yearList", yearList);
	    		inputMap.put("monthList", monthList);
	    		
	    		java.util.Calendar calendar = java.util.Calendar.getInstance(); 
	    		int curYear=calendar.get(java.util.Calendar.YEAR); 
	    		int curMonth=calendar.get(java.util.Calendar.MONTH);
	    		inputMap.put("curYear", curYear);
	    		inputMap.put("curMonth", (curMonth+1));
	    		
	    		gLogger.info("login map details"+loginDetailsMap.toString());

			EmployeeCornerDAOImpl lObjEmpCornerDao = new EmployeeCornerDAOImpl(
					MstEmp.class, serv.getSessionFactory());
			new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			

		/*	String lStrSevarthId = StringUtility.getParameter("sevarthId",
					request).trim();
			
			inputMap.put("lStrSevarthId", lStrSevarthId);

			String lStrEmpName = StringUtility.getParameter("employeeName",
					request).trim();
			String month = StringUtility.getParameter("month",
					request);
			
			String year = StringUtility.getParameter("year",
					request);
			inputMap.put("Month", month);
			inputMap.put("Year", year);
			inputMap.put("lStrEmpName", lStrEmpName);*/
			
			long empId=StringUtility.convertToLong(loginDetailsMap.get("empId").toString());
			gLogger.info("employeeID*********"+empId);
			if (!"".equals(empId) ) {
				listSavedRequests = lObjEmpCornerDao.searchEmpsForEmpCorner(
						empId);
			}
			
			/*if (!"".equals(empId) || !"".equals(lStrEmpName)) {
				listSavedRequests = lObjEmpCornerDao.searchEmpsForEmpCorner(
						empId, lStrEmpName);
			}*/

			if (listSavedRequests != null) {
				if (listSavedRequests.size() != 0) {
					totalRecords = listSavedRequests.size();
				}
			}

			inputMap.put("totalRecords", totalRecords);
			inputMap.put("CaseList", listSavedRequests);
			
			resObj.setViewName("EmployeeCorner");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			//ex.printStackTrace();
			gLogger.error("Error is;" + ex, ex);
		}

		return resObj;
	}
	public ResultObject getNameForAutoComplete(Map<String, Object> inputMap) throws Exception {
		
		
		

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList  = new ArrayList<ComboValuesVO>();
	
		List finalListFromname  = new ArrayList<ComboValuesVO>();
		
		String lStrEmpName = null;
		
		try 
		{
			setSessionInfo(inputMap);
			EmployeeCornerDAOImpl lObjEmpCornerDao = new EmployeeCornerDAOImpl(
					MstEmp.class, serv.getSessionFactory());
			lStrEmpName = StringUtility.getParameter("searchKey", request)
			.trim();
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			
			finalList = lObjEmpCornerDao.getNameForAutoComplete(lStrEmpName.toUpperCase());
		
			
			gLogger.info("finalList size is **********"+finalList.size());
			
			String lStrTempResult = null;
			if (finalList != null && finalList.size()>0 ) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList, "desc", "id",true).toString();
			}
			gLogger.info("Result------------------------"+lStrTempResult);
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			gLogger.error("Error is: "+ ex.getMessage());
			return objRes;
		}

		return objRes;

	}
	
	public ResultObject viewEmpPaySlip(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		setSessionInfo(inputMap);
		List finalList  = new ArrayList();
		String SevarthId = StringUtility.getParameter("sevarthId",
				request).trim();
		
		String month = StringUtility.getParameter("month",
				request);
		
		String year = StringUtility.getParameter("year",
				request);
		String printOrNot = StringUtility.getParameter("printOrNot", request); 
		EmployeeCornerDAOImpl lObjEmpCornerDao = new EmployeeCornerDAOImpl(
				MstEmp.class, serv.getSessionFactory());
		String billNo=null;
		String dsgnId=null;
		String empId=null;
		String locId=null;
		finalList = lObjEmpCornerDao.viewpaySlip(SevarthId,month,year);
		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<isGenerated>");
		if(finalList.size()==0){
			
			lStrBldXML.append("no");
			
		}
		else{
			lStrBldXML.append("yes");
		}
		lStrBldXML.append("</isGenerated>");
		lStrBldXML.append("</XMLDOC>");
		String lSBStatus = lStrBldXML.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
		gLogger.info("********************************************" + lStrResult);
		inputMap.put("ajaxKey", lStrResult);
		gLogger.info("^^^^^^^^^^^^^^^^^^^^^^^^^^" + lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setViewName("ajaxData");
		return resObj;
	
	}
	
	public ResultObject getEmpPaySlip(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		
		List finalList  = new ArrayList();
		EmployeeCornerDAOImpl lObjEmpCornerDao=null;
		String SevarthId=null;
		String month=null;
		String year=null;
		ServiceLocator servcLoc=null;
		gLogger.info("inside getEmpPaySlip");
		

		if(inputMap.get("webServiceCall")!=null && !inputMap.get("webServiceCall").toString().equalsIgnoreCase(""))
		{
			if(inputMap.get("sevarthId")!=null && !inputMap.get("sevarthId").toString().equalsIgnoreCase(""))
				SevarthId = inputMap.get("sevarthId").toString();

			if(inputMap.get("month")!=null && !inputMap.get("month").toString().equalsIgnoreCase(""))
				month = inputMap.get("month").toString();

			if(inputMap.get("year")!=null && !inputMap.get("year").toString().equalsIgnoreCase(""))
				year = inputMap.get("year").toString();
			
			if(inputMap.get("serviceLocator")!=null && !inputMap.get("serviceLocator").toString().equalsIgnoreCase(""))
				servcLoc = (ServiceLocator) inputMap.get("serviceLocator");
			
			gLogger.info("inside if == getEmpPaySlip==SevarthId-="+SevarthId+"month==="+month+"==year"+year);
			
			lObjEmpCornerDao = new EmployeeCornerDAOImpl(MstEmp.class, servcLoc.getSessionFactory());
		}
		else
		{
			setSessionInfo(inputMap);
			
			SevarthId = StringUtility.getParameter("sevarthId", request).trim();

			month = StringUtility.getParameter("month", request);

			year = StringUtility.getParameter("year", request);
			
			lObjEmpCornerDao = new EmployeeCornerDAOImpl(MstEmp.class, serv.getSessionFactory());
			
		}

		

		String billNo=null;
		String dsgnId=null;
		String empId=null;
		String locId=null;
		finalList = lObjEmpCornerDao.viewpaySlip(SevarthId,month,year);

		Iterator itr = finalList.iterator();
		

		while (itr.hasNext())
		{
			Object[] rowList = (Object[]) itr.next();
			billNo=rowList[12].toString();
			dsgnId=rowList[13].toString();
			empId=rowList[10].toString();
			locId=rowList[11].toString();
			gLogger.info("inside if == getEmpPaySlip==empId-===="+empId);
		}
		gLogger.info("getEmpPaySlip==empId-===="+empId);
		inputMap.put("month", month);
		inputMap.put("year", year);
		inputMap.put("billNo",billNo);
		//inputMap.put("dsgnId",dsgnId);
		inputMap.put("employeeid",empId);
		inputMap.put("locId",locId);

		GeneratePaySlipService lobjService=new GeneratePaySlipService();
		resObj=lobjService.generatePayslip(inputMap);
		return resObj;


	}
	
	
	
}
