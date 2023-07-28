/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 9, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.tcs.sgv.dcps.dao.ChangeEmpDeptDAO;
import com.tcs.sgv.dcps.dao.ChangeEmpDeptDAOImpl;
import com.tcs.sgv.dcps.dao.ConsolidateDcpsR2BroadSheetDAO;
import com.tcs.sgv.dcps.dao.ConsolidateDcpsR2BroadsheetDAOImpl;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Jun 9, 2011
 */
public class ConsolidateDcpsR2BroadsheetSeviceImpl extends ServiceImpl 
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

	public ResultObject loadConsolidateTreasury(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTempFromDate = null;
		String lStrTempToDate = null;
		String lStrFromDate = null;
		String lStrToDate = null;
		Date lDateFromDate = null;
		Date lDateToDate = null;
		List lListTreasuriesConsolidate = null;
		Long yearId = null;
		Long totalSAmount=0L;
		Long totalTAmount=0L;
		Long AcMain=null;
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			ConsolidateDcpsR2BroadSheetDAO lObjconsolidate = new ConsolidateDcpsR2BroadsheetDAOImpl(
					null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getFinyears();
			List lActMain=lObjconsolidate.getAcMainDetails();

			if (!StringUtility.getParameter("yearId", request).equalsIgnoreCase("")&& StringUtility.getParameter("yearId", request) != null
					&& !StringUtility.getParameter("acmaintBy", request).equalsIgnoreCase("") && StringUtility.getParameter("acmaintBy", request) != null)	
			 {

				yearId = Long.valueOf(StringUtility.getParameter("yearId",request));
				lStrTempFromDate = StringUtility.getParameter("fromDate",request);
				lStrTempToDate = StringUtility.getParameter("toDate", request);
				AcMain=Long.valueOf(StringUtility.getParameter("acmaintBy", request));
				
				lStrFromDate = sdf2.format(sdf1.parse(lStrTempFromDate));
				lStrToDate = sdf2.format(sdf1.parse(lStrTempToDate));

				lDateFromDate = sdf1.parse(lStrTempFromDate);
				lDateToDate = sdf1.parse(lStrTempToDate);

				lListTreasuriesConsolidate = lObjconsolidate
				.getAllTreasuriesForConsolidateEnteries(yearId,
						AcMain);



				//   gLogger.info("totalSAmount is ***"+totalSAmount);
				inputMap.put("totalRecords", lListTreasuriesConsolidate.size());
				inputMap.put("selectedYear", yearId);
				inputMap.put("fromDate", lDateFromDate);
				inputMap.put("toDate", lDateToDate);
				inputMap.put("selectedAcc", AcMain);


/*				Object[] tuple = null;


				Iterator it = lListTreasuriesMatchEntries.iterator();

				while (it.hasNext())
				{
					tuple = (Object[]) it.next();

					totalSAmount = totalSAmount+Long.valueOf(tuple[2].toString());
					totalTAmount = totalTAmount+Long.valueOf(tuple[3].toString());

				}
				gLogger.info("totalSAmount is ***"+totalSAmount);
				gLogger.info("totalTAmount is ***"+totalTAmount);*/
			}


			inputMap.put("YEARS", lLstYears);
			inputMap.put("AccMain", lActMain);
			inputMap.put("ListTreasuriesMatchEntries",
					lListTreasuriesConsolidate);

			/*inputMap.put("totalSAmount", totalSAmount);
			inputMap.put("totalTAmount", totalTAmount);*/

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSConsolidateEnteries");

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
