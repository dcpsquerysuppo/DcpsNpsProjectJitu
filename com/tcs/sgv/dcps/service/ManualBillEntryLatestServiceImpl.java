/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.hibernate.Session;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAO;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAOImpl;
import com.tcs.sgv.dcps.dao.FormSubscriber2DAOImpl;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.valueobject.HstDcpsContribution;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;
import com.tcs.sgv.dcps.dao.ManualBillEntryLatestDAOImpl;

public class ManualBillEntryLatestServiceImpl extends ServiceImpl  {

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
	private void setSessionInfoSchdlr(Map inputMap) {

		try {
			//request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			//session = (Session)inputMap.get("currentSession");
			Map  SchedlrLoginMap =(HashMap)inputMap.get("baseLoginMap");
			gStrPostId = String.valueOf(SchedlrLoginMap.get("postId"));
			gLngPostId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("postId")));
			gStrLocationCode =String.valueOf(SchedlrLoginMap.get("locationCode"));
			gLngUserId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("userId")));
			gStrUserId = String.valueOf(SchedlrLoginMap.get("userId"));
			gLngDBId = Long.valueOf(String.valueOf(SchedlrLoginMap.get("dbId")));
			gDtCurDate = new Date();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}
	public ResultObject loadManualEntryForm(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		List BillGroupList = null;
		Long monthId = null;
		Long yearId = null;
		String lStrMissingCreditOrNot = null;
		String FirstTimeMissingCreditLoad = null;
		String empName = null;
		String payCommission = null;
		String basicPay = null;
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			ManualBillEntryLatestDAOImpl lObjDAO = new ManualBillEntryLatestDAOImpl(null, serv.getSessionFactory());

			List lLstMonths = lObjDAO.getMonths();

			List lLstYears = lObjDAO.getFinyears();
			List ddonames = lObjDAO.getAllDDOForApprovedContriInTO(gStrLocationCode);

			String lStrDcpsId = StringUtility.getParameter("dcpsId", request).trim();
			String lStrtransId = StringUtility.getParameter("transId", request).trim();
			String lStrsevarthId = StringUtility.getParameter("sevarthId", request).trim();
				
			List empDetails = lObjDAO.getEmployeeDetails(lStrDcpsId);
			
			if(empDetails.size()>0){
				Object tuple[] = (Object[]) empDetails.get(0);
				 empName = tuple[0].toString();
				 payCommission = tuple[1].toString();
				 basicPay = tuple[2].toString();
			}
			List<CmnLookupMst> listPayCommissionOld = IFMSCommonServiceImpl
			.getLookupValues("PayCommissionDCPS", SessionHelper
					.getLangId(inputMap), inputMap);

			List<CmnLookupMst> listPayCommission = new ArrayList<CmnLookupMst>();
			CmnLookupMst tempCmnLookupMst = null;



				if (payCommission.equals(
				"700015")) {
					tempCmnLookupMst = new CmnLookupMst();
					tempCmnLookupMst.setLookupId(700015l);
					tempCmnLookupMst.setLookupDesc("5 PC");
					listPayCommission.add(tempCmnLookupMst);
				}
				if (payCommission.equals(
				"700016")) {
					tempCmnLookupMst = new CmnLookupMst();
					tempCmnLookupMst.setLookupId(700016l);
					tempCmnLookupMst.setLookupDesc("6 PC");
					listPayCommission.add(tempCmnLookupMst);
				}

				if (payCommission.equals(
				"700338")) {
					tempCmnLookupMst = new CmnLookupMst();
					tempCmnLookupMst.setLookupId(700338l);
					tempCmnLookupMst.setLookupDesc("NonGovt");
					listPayCommission.add(tempCmnLookupMst);
				}

				if (payCommission.equals(
				"700339")) {
					tempCmnLookupMst = new CmnLookupMst();
					tempCmnLookupMst.setLookupId(700339l);
					tempCmnLookupMst.setLookupDesc("Padmanabhan");
					listPayCommission.add(tempCmnLookupMst);
				}

				if (payCommission.equals(
				"700340")) {
					tempCmnLookupMst = new CmnLookupMst();
					tempCmnLookupMst.setLookupId(700340l);
					tempCmnLookupMst.setLookupDesc("Fourth(IV)");
					listPayCommission.add(tempCmnLookupMst);
				}

				if (payCommission.equals(
				"700345")) {
					tempCmnLookupMst = new CmnLookupMst();
					tempCmnLookupMst.setLookupId(700345l);
					tempCmnLookupMst.setLookupDesc("Shetty");
					listPayCommission.add(tempCmnLookupMst);
				}

			

			inputMap.put("listPayCommission", listPayCommission);

			gLogger.info("lStrDcpsId********"+lStrDcpsId);
			inputMap.put("lStrDcpsId", lStrDcpsId);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("ddonames", ddonames);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lStrtransId", lStrtransId);
			inputMap.put("lStrsevarthId", lStrsevarthId);
			inputMap.put("lStrempName", empName);
			inputMap.put("lStrpayCommission", payCommission);
			inputMap.put("lStrbasicPay", basicPay);

			resObj.setResultValue(inputMap);
			resObj.setViewName("manualBillEntryLatest");
			
		}catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

		}
	

	public ResultObject getManualEntryData(Map inputMap)throws Exception {
			gLogger.info("Inside getManualEntryData ");

			ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			String startDate = null;
			String endDate= null;
			String basicPay= null;
			String da= null;
			String contribution= null;
			String bankState= null;
			String bankPincode= null;
			String bankMicrCode= null;
			String bankIfsc= null;
			
			String NotExist="";
			
			try {
			setSessionInfo(inputMap);
			ManualBillEntryLatestDAOImpl lObjDAO = new ManualBillEntryLatestDAOImpl(null, serv.getSessionFactory());
			
			String lStrDcpsId = StringUtility.getParameter("cmbDcpsId", request).trim();
			String lStrMonth = StringUtility.getParameter("cmbMonth", request).trim();
			String lStrYear = StringUtility.getParameter("cmbYear", request).trim();
			
			gLogger.info("in lStrDcpsId***************"+lStrDcpsId);
			gLogger.info("in lStrMonth***************"+lStrMonth);
			gLogger.info("in lStrYear***************"+lStrYear);

			List existList = lObjDAO.getEmployeeContriDetails(lStrDcpsId,lStrYear,lStrMonth);

			
			if(existList!= null && existList.size()>0)
			{			
			Object[] tuple = (Object[]) existList.get(0);
			startDate = tuple[0].toString();
			endDate = tuple[1].toString();
			basicPay = tuple[2].toString();
			da = tuple[3].toString();
			contribution = tuple[4].toString();
			
			}
			else{
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

				String yearCode = lObjDcpsCommonDAO.getYearCodeForYearId(Long.parseLong(lStrYear));

				Integer lIntMonth = Integer.parseInt(lStrMonth.toString());
				Integer lIntYear = Integer.parseInt(yearCode);

				if (lIntMonth == 1 || lIntMonth == 2 || lIntMonth == 3) {
					lIntYear += 1;
				}

				/*
				 * Get First Date and Last Date of Month For Selected Year and
				 * Month
				 */
				gLogger.info("lIntYear**********"+lIntYear);
				gLogger.info("lIntMonth**********"+lIntMonth);
				Date lDtLastDate = null;
				Date lDtFirstDate = null;

				lDtLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 1,
						lIntYear);
				lDtFirstDate = lObjDcpsCommonDAO.getFirstDate(lIntMonth - 1,
						lIntYear);
				gLogger.info("lDtFirstDate**********"+lDtFirstDate);
				gLogger.info("lDtLastDate**********"+lDtLastDate);
				startDate= lDtFirstDate.toString();
				endDate = lDtFirstDate.toString();
			}
			
			StringBuffer strbuflag = new StringBuffer();
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<startDate>");
			strbuflag.append(startDate);
			strbuflag.append("</startDate>");
			strbuflag.append("<endDate>");
			strbuflag.append(endDate);
			strbuflag.append("</endDate>");	
			strbuflag.append("<basicPay>");
			strbuflag.append(basicPay);
			strbuflag.append("</basicPay>");
			strbuflag.append("<da>");
			strbuflag.append(da);
			strbuflag.append("</da>");	
			strbuflag.append("<contribution>");
			strbuflag.append(contribution);
			strbuflag.append("</contribution>");			
			strbuflag.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();
			if(lStrResult != null){
			inputMap.put("ajaxKey", lStrResult);
			}
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			
			}
			catch (Exception e) {
				e.printStackTrace();
				gLogger.info("Exception is " + e);
				objRes.setResultValue(null);
				objRes.setThrowable(e);
				objRes.setResultCode(ErrorConstants.ERROR);
				objRes.setViewName("errorPage");
			}
			return objRes;
			}

	
}

