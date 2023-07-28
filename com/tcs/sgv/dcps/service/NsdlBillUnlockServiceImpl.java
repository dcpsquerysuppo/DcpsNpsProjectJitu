package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
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

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOTOImpl;
import com.tcs.sgv.dcps.dao.NsdlBillUnlockDAO;
import com.tcs.sgv.dcps.dao.NsdlBillUnlockDAOImpl;

public class NsdlBillUnlockServiceImpl extends ServiceImpl 
	 {

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

	public ResultObject NsdlBillUnlock(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesMatchEntries = null;
		Long yearId = null;
		Long totalSAmount=0L;
		Long totalTAmount=0L;
		Long AcMain=null;
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

		/*	DummyOfficeDAO lObjDummyOfficeDAO = new DummyOfficeDAOImpl(
					null, serv.getSessionFactory());
			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());
			MatchContriEntryDAOTO lObjMatchEntryDAOTO = new MatchContriEntryDAOTOImpl(
					null, serv.getSessionFactory());*/
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			NsdlBillUnlockDAO lObjNsdlReportsDAO = new NsdlBillUnlockDAOImpl(null, serv
					.getSessionFactory());
/*public List getFinyear() {

	String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '2014' order by finYearCode ASC";
	List<Object> lLstReturnList = null;
	StringBuilder sb = new StringBuilder();
	sb.append(query);
	Query selectQuery = ghibSession.createQuery(sb.toString());
	List lLstResult = selectQuery.list();
	ComboValuesVO lObjComboValuesVO = null;

	if (lLstResult != null && lLstResult.size() != 0) {
		lLstReturnList = new ArrayList<Object>();
		Object obj[];
		for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
			obj = (Object[]) lLstResult.get(liCtr);
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId(obj[0].toString());
			lObjComboValuesVO.setDesc(obj[1].toString());
			lLstReturnList.add(lObjComboValuesVO);
		}
	} else {
		lLstReturnList = new ArrayList<Object>();
		lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
	}
	return lLstReturnList;
}
*/
			String allowedOrNot="1";//(String) lObjNsdlReportsDAO.getStatusOfDist(gStrLocationCode);
	   		 gLogger.info("allowedOrNot for paybill " + allowedOrNot);
	   		if(allowedOrNot.equals("1")){
			/* Get Years */
			List lLstYears = lObjNsdlReportsDAO.getFinyears();
		List lLstMonths= lObjDcpsCommonDAO.getMonths();
		/*	List lLstDummyOffice= lObjDummyOfficeDAO.getDummyOffices();*/
			
			
			
		if(!StringUtility.getParameter("finYear",request).equalsIgnoreCase("") && StringUtility.getParameter("finYear",request) != null
				&& !StringUtility.getParameter("month",request).equalsIgnoreCase("") && StringUtility.getParameter("month",request) != null)
			{
				String lstrFinYear=StringUtility.getParameter("finYear", request);
				String lstrMonth=StringUtility.getParameter("month", request);
			
				
				List lLstDetails= lObjNsdlReportsDAO.getDetails(lstrFinYear,lstrMonth,gStrLocationCode);
				gLogger.info("lLstDetails########################"+lLstDetails);
				
				inputMap.put("lstrFinYear", lstrFinYear);
				inputMap.put("lstrMonth", lstrMonth);
				inputMap.put("lLstDetails", lLstDetails);
			}
				

			
				//   gLogger.info("totalSAmount is ***"+totalSAmount);
				/*inputMap.put("lLstDummyOffice", lLstDummyOffice);*/
				inputMap.put("lLstYears", lLstYears);
				inputMap.put("lLstMonths", lLstMonths);
				
				
			System.out.println("Year list is "+lLstYears.size());



			resObj.setResultValue(inputMap);
			resObj.setViewName("NsdlBillUnlockReports");
	   		}
			else{
				inputMap.put("allowedOrNot", allowedOrNot);	
				inputMap.put("restrictmsg", "You are not allowed to use this functionality as on today. Kindly contact MDC for further action.");
				resObj.setResultCode(ErrorConstants.SUCCESS);
				resObj.setResultValue(inputMap);
				resObj.setViewName("NsdlBillUnlockReports");
			}
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