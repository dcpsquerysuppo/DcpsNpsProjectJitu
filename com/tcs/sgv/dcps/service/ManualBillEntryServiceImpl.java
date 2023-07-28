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

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAO;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAOImpl;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.valueobject.HstDcpsContribution;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class ManualBillEntryServiceImpl extends ServiceImpl  {

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
		String manualBillEntry = "N";
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			// Gets Element Id and puts in the Map
			String hidElementId = StringUtility.getParameter("elementId",
					request);
			inputMap.put("hidElementId", hidElementId);

			/* Initializes the DAOs */
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			/* Get All the Bill Groups under selected DDO (As of now shown all) */
			//	List lLstBillGroups = lObjDcpsCommonDAO.getBillGroups();

			/* Get Months */
			List lLstMonths = lObjDcpsCommonDAO.getMonths();

			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getFinyears();

			List lLstDelayedYears = lObjDcpsCommonDAO.getFinyearsForDelayedType();

			/* Get User(ATO or TO) and Use type */
			String lStrUserType = StringUtility.getParameter("User", request).trim();
			String lStrUseType = StringUtility.getParameter("Use", request).trim();
			String lStrUseValueType = StringUtility.getParameter("UseValue", request).trim();
			String lStrTransactionId = StringUtility.getParameter("transId", request).trim();			
//			String lStrSevarthId = StringUtility.getParameter("sevarthId", request).trim();
			String lStrDcpsId = StringUtility.getParameter("dcpsId", request).trim();

			
			String lStrContiType = null;

			if (!StringUtility.getParameter("Type", request).equalsIgnoreCase("")&& StringUtility.getParameter("Type", request) != null) {
				lStrContiType = StringUtility.getParameter("Type", request);
			}

			inputMap.put("ContriType", lStrContiType);

			List treasuries = null;
			List ddonames = null;
				treasuries = lObjOfflineContriDAO.getCurrentTreasury(gStrLocationCode);
				if (lStrUserType.equals("TO") && lStrUseType.equals("MissingCreditEntry") ) {
					ddonames = lObjOfflineContriDAO.getAllDDOWOBrackets(gStrLocationCode);
					if (lStrUseValueType.equals("Y"))
					{
						gLogger.info("in lStrUseValueType");
						manualBillEntry = "Y";
					}
				} 

			

			inputMap.put("TREASURIES", treasuries);
			inputMap.put("DDONAMES", ddonames);

			MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;

			String schemeCode = null;
			String schemename = null;

			/* Get All Types Of Payment From Lookup */
			List listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues(
					"TypeOfPaymentDCPS", SessionHelper.getLangId(inputMap),
					inputMap);

			String lStrTypeOfPaymentMaster = StringUtility.getParameter("typeOfPaymentMaster", request).trim();
			if ("".equals(lStrTypeOfPaymentMaster)) {
				lStrTypeOfPaymentMaster = "700046"; // By default Regular Type
			}
			inputMap.put("typeOfPaymentMaster", lStrTypeOfPaymentMaster.trim());

			//Set the Month Id and Year Id for the current month and year from the system date for the first time load.
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String lStrCurDate = null;
			if(StringUtility.getParameter("cmbMonth", request).trim().equals(""))
			{
				lStrCurDate = sdf.format(gDtCurDate);
				if(lStrCurDate != null && !"".equals(lStrCurDate))
				{
					monthId = Long.valueOf(lStrCurDate.substring(3, 5));
					// month Id hard-coded to April temporarily so that DDOs dont forward May's contributions by mistake instead of April Month 
					monthId = 4l;
				}
			}
			if(StringUtility.getParameter("cmbYear", request).trim().equals(""))
			{
				if(lObjDcpsCommonDAO.getFinYearIdForDate(gDtCurDate) != null && !"".equals(lObjDcpsCommonDAO.getFinYearIdForDate(gDtCurDate)))
				{
					yearId = Long.valueOf(lObjDcpsCommonDAO.getFinYearIdForDate(gDtCurDate));
				}
			}

			if (lStrUserType.equals("TO") && lStrUseType.equals("MissingCreditEntry")) {
				if (lStrUseValueType.equals("Y"))
				{
					gLogger.info("in lStrUseValueType");
					manualBillEntry = "Y";
				}
				BillGroupList = new ArrayList<Object>();
				ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));
				lObjComboValuesVO.setDesc("Missing Credit");
				BillGroupList.add(lObjComboValuesVO);
				lStrMissingCreditOrNot = "Yes";
				FirstTimeMissingCreditLoad = "Yes";
			}

			inputMap.put("FirstTimeMissingCreditLoad", FirstTimeMissingCreditLoad);

			if (StringUtility.getParameter("cmbBillGroup", request) != null
					&& !StringUtility.getParameter("cmbBillGroup", request)
					.equalsIgnoreCase("")) {

				/* Get the DDO Code */
				lStrDdoCode = StringUtility.getParameter("cmbDDOCode", request).trim();

				if (lStrUserType.equals("DDO")) {
					if (lStrUseType.equals("ViewApproved")) {
						BillGroupList = lObjOfflineContriDAO
						.getApprovedBillGroupsForDdoInDDOLogin(lStrDdoCode);
					} else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdoInDDOLogin(lStrDdoCode);
					}
				} else if (lStrUserType.equals("TO")) {
					if (lStrUseType.equals("ViewReverted")) {
						String lStrBillGroupIdFromReversion = StringUtility
						.getParameter("cmbBillGroup", request);
						String lStrBillGroupDescFromReversion = StringUtility
						.getParameter("cmbBillGroupDesc", request);
						ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(lStrBillGroupIdFromReversion);
						lObjComboValuesVO.setDesc(lStrBillGroupDescFromReversion);
						BillGroupList = new ArrayList<Object>();
						BillGroupList.add(lObjComboValuesVO);
					} 
					else if (lStrUseType.equals("MissingCreditEntry")) {
						if (lStrUseValueType.equals("Y"))
						{
							gLogger.info("in lStrUseValueType");
							manualBillEntry = "Y";
						}
						BillGroupList = new ArrayList<Object>();
						ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId("-1");
						lObjComboValuesVO.setDesc("--Select--");
						lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));
						lObjComboValuesVO.setDesc("Missing Credit");
						BillGroupList.add(lObjComboValuesVO);
						lStrMissingCreditOrNot = "Yes";
					}
					else if(lStrUseType.equals("ViewApprovedByTO"))
					{
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdoInTOLoginInclMisngCrdt(lStrDdoCode);
					}
					else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdoInTOLogin(lStrDdoCode);
					}
				} else if (lStrUserType.equals("DDOAsst")) {

					if (lStrUseType.equals("ViewRejected")) {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsRejectedForDdo(lStrDdoCode);
					} else {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsForDdo(lStrDdoCode);
					}
				} else if (lStrUserType.equals("ATO")) {
					if (lStrUseType.equals("ViewRejected")) {
						BillGroupList = lObjOfflineContriDAO
						.getBillGroupsRejectedForDdoInATOLogin(lStrDdoCode);
					} 
					else
					{}
				}

				/*
				 * Get Month Id,Year Id,Bill Group Id,Treasury Code,Scheme
				 * Name,Scheme Code from Request
				 */
				monthId = Long.parseLong(StringUtility.getParameter(
						"cmbMonth", request));
				yearId = Long.parseLong(StringUtility.getParameter(
						"cmbYear", request));
				Long lLongbillGroupId = Long.valueOf(StringUtility
						.getParameter("cmbBillGroup", request));
				Long treasuryCode = Long.valueOf(StringUtility.getParameter(
						"treasuryCode", request));

				if (!StringUtility.getParameter("schemeName", request).trim()
						.equalsIgnoreCase("")
						&& StringUtility.getParameter("schemeName", request) != null) {
					schemename = StringUtility.getParameter("schemeName",
							request).trim();
				}
				if (!StringUtility.getParameter("schemeCode", request).trim()
						.equalsIgnoreCase("")
						&& StringUtility.getParameter("schemeCode", request) != null) {
					schemeCode = StringUtility.getParameter(
							"schemeCode", request).toString().trim();
				}

				// Code Added for checking previous month's contribution entry

				DcpsCommonDAO lObjDcpsCommonDAOForFinYear = new DcpsCommonDAOImpl(
						SgvcFinYearMst.class, serv.getSessionFactory());
				SgvcFinYearMst lObjSgvcFinYearMst = null;
				lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAOForFinYear
				.read(yearId);

				Long previousMonthId = null;
				Long previousYearId = null;
				if (monthId == 1) {
					previousMonthId = 12l;
				} else {
					previousMonthId = monthId - 1;
				}

				if (monthId == 4) {
					previousYearId = lObjSgvcFinYearMst.getPrevFinYearId();
				} else {
					previousYearId = yearId;
				}

				String contributionsForPrvsMonth = "NO";
				String voucherDtlsForPrvsMonth = "NO";

				MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtlsForPrvsYear = lObjOfflineContriDAO
				.checkContributionsForPrvsMonth(previousYearId,
						previousMonthId, lStrDdoCode, lLongbillGroupId);

				if (lObjMstDcpsContriVoucherDtlsForPrvsYear != null) {
					contributionsForPrvsMonth = "YES";
				}

				if (lObjMstDcpsContriVoucherDtlsForPrvsYear != null) {
					if (lObjMstDcpsContriVoucherDtlsForPrvsYear.getVoucherNo() != null
							&& lObjMstDcpsContriVoucherDtlsForPrvsYear
							.getVoucherDate() != null) {
						voucherDtlsForPrvsMonth = "YES";
					}
				}

				inputMap.put("contributionsForPrvsMonth",
						contributionsForPrvsMonth);
				inputMap
				.put("voucherDtlsForPrvsMonth", voucherDtlsForPrvsMonth);

				inputMap.put("previousMonthId", previousMonthId);
				inputMap.put("previousYearId", previousYearId);

				// Code overs for checking previous month's contribution entry

				/* Get Year code from Year Id */
				String yearCode = lObjDcpsCommonDAO
				.getYearCodeForYearId(yearId);

				Integer lIntMonth = Integer.parseInt(monthId.toString());
				Integer lIntYear = Integer.parseInt(yearCode);

				if (lIntMonth == 1 || lIntMonth == 2 || lIntMonth == 3) {
					lIntYear += 1;
				}

				/*
				 * Get First Date and Last Date of Month For Selected Year and
				 * Month
				 */
				Date lDtLastDate = null;
				Date lDtFirstDate = null;

				Integer lIntDelayedMonth = null;
				Long lLongDelayedYearId = null;
				Integer lIntDelayedYear = null;
				Date lDtLastDateDelayed = null;
				Date lDtFirstDateDelayed = null;
				Long delayedMonthId = null;
				Long delayedYearId = null;

				if (lStrTypeOfPaymentMaster.equals("700047")) // Delayed Type
					// payment
				{
					if ((lStrUserType.equals("DDOAsst") || lStrUserType
							.equals("ATO"))
							&& (lStrUseType.equals("ViewAll"))) {
						lIntDelayedMonth = Integer
						.parseInt(StringUtility.getParameter(
								"cmbDelayedMonth", request).trim());
						lLongDelayedYearId = Long
						.parseLong(StringUtility.getParameter(
								"cmbDelayedYear", request).trim());
						lIntDelayedYear = Integer
						.parseInt(lObjDcpsCommonDAO
								.getYearCodeForYearId(lLongDelayedYearId));

						delayedMonthId = Long.valueOf(StringUtility.getParameter(
								"cmbDelayedMonth", request).trim());
						delayedYearId = Long.valueOf(StringUtility.getParameter(
								"cmbDelayedYear", request).trim());

						inputMap.put("delayedmonthId", lIntDelayedMonth);
						inputMap.put("delayedyearId", lLongDelayedYearId);

						if (lIntDelayedMonth == 1 || lIntDelayedMonth == 2
								|| lIntDelayedMonth == 3) {
							lIntDelayedYear += 1;
						}

						lDtLastDateDelayed = lObjDcpsCommonDAO
						.getLastDate(lIntDelayedMonth - 1,
								lIntDelayedYear);
						lDtFirstDateDelayed = lObjDcpsCommonDAO
						.getFirstDate(lIntDelayedMonth - 1,
								lIntDelayedYear);

						inputMap.put("FirstDateDelayed", lDtFirstDateDelayed);
						inputMap.put("LastDateDelayed", lDtLastDateDelayed);

						if (lIntDelayedMonth == 1) {
							lIntDelayedYear--;
						}
					}
				}

				lDtLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 1,
						lIntYear);
				lDtFirstDate = lObjDcpsCommonDAO.getFirstDate(lIntMonth - 1,
						lIntYear);

				Boolean lBlFlagBillGenerated = false;

				if (lStrUserType.equals("DDOAsst")) 
				{
					lBlFlagBillGenerated = lObjOfflineContriDAO.checkIfBillAlreadyGenerated(lLongbillGroupId, monthId,Long.valueOf(lIntYear.toString()));
				}
				inputMap.put("lBlFlagBillGenerated", lBlFlagBillGenerated);

				// Below code commented as seems of no use
				/*
					if (lIntMonth == 1) {
						lIntYear--;
					}

					Date lDtDelFirstDate = lObjDcpsCommonDAO.getFirstDate(
							lIntMonth - 2, lIntYear);
					Date lDtDelLastDate = lObjDcpsCommonDAO.getLastDate(
							lIntMonth - 2, lIntYear);
				 */

				/* Get All Pay Commissions from Lookup */
				List<CmnLookupMst> listPayCommissionOld = IFMSCommonServiceImpl
				.getLookupValues("PayCommissionDCPS", SessionHelper
						.getLangId(inputMap), inputMap);

				List<CmnLookupMst> listPayCommission = new ArrayList<CmnLookupMst>();
				CmnLookupMst tempCmnLookupMst = null;

				for (CmnLookupMst lObjCommonLookupMst : listPayCommissionOld) {

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700015")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700015l);
						tempCmnLookupMst.setLookupDesc("5 PC");
						listPayCommission.add(tempCmnLookupMst);
					}
					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700016")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700016l);
						tempCmnLookupMst.setLookupDesc("6 PC");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700338")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700338l);
						tempCmnLookupMst.setLookupDesc("NonGovt");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700339")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700339l);
						tempCmnLookupMst.setLookupDesc("Padmanabhan");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700340")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700340l);
						tempCmnLookupMst.setLookupDesc("Fourth(IV)");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
					"700345")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700345l);
						tempCmnLookupMst.setLookupDesc("Shetty");
						listPayCommission.add(tempCmnLookupMst);
					}

				}

				inputMap.put("listPayCommission", listPayCommission);

				/* Puts the Edit form option's provision. */

				List UserList = null;

				if (lStrUseType.equals("MissingCreditEntry")) {
					inputMap.put("EditForm", "Y");

				}
				inputMap.put("UserList", UserList);

				/*
				 * Gets the Employees' contribution List for selected month and
				 * year and puts in the Map
				 */

				Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);

				List empList = null;

				SimpleDateFormat sdf1 = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
				String lStrFirstDate = null;
				String lStrFirstDateMainMonth = null;

				if(lDtFirstDateDelayed != null) // It will not be null when Delayed Type Payment is selected by DDO Asst or ATO.
				{
					lStrFirstDate = sdf1.format(lDtFirstDateDelayed);
				}
				else
				{
					lStrFirstDate = sdf1.format(lDtFirstDate);
				}

				lStrFirstDateMainMonth = sdf1.format(lDtFirstDate);

				String lStrLastDate = null;
				if(lDtLastDateDelayed != null) // It will not be null when Delayed Type Payment is selected by DDO Asst or ATO.
				{
					lStrLastDate = sdf1.format(lDtLastDateDelayed);
				}
				else
				{
					lStrLastDate = sdf1.format(lDtLastDate);
				}

				empList = lObjOfflineContriDAO.getEmpListForContributionFinal(
						lStrDdoCode, lLongbillGroupId, monthId, yearId,
						lStrUserType, lStrUseType, gStrPostId, displayTag,
						lStrFirstDate, lStrTypeOfPaymentMaster,delayedMonthId,delayedYearId,lStrLastDate,lStrFirstDateMainMonth,schemeCode);
				inputMap.put("totalRecords", empList.size());

				if (empList.size() == 0) {
					empList = null;
				}

				// Gets voucherDtlsVO

				lObjMstDcpsContriVoucherDtls = lObjOfflineContriDAO
				.getContriVoucherVOForInputDtls(yearId, monthId,
						lStrDdoCode, lLongbillGroupId);
				inputMap.put("lObjMstDcpsContriVoucherDtls",
						lObjMstDcpsContriVoucherDtls);

				List lListVoucherNoAndDate = lObjOfflineContriDAO.getVoucherNoAndDateForInputDtls(yearId, monthId, lStrDdoCode, lLongbillGroupId);
				Long lLongVoucherNo = null;
				Date lDateVoucherDate = null;

				if(lListVoucherNoAndDate != null)
				{
					if(lListVoucherNoAndDate.size() != 0)
					{
						Object[] lObjVoucherNoAndDate = null;
						lObjVoucherNoAndDate = (Object[]) lListVoucherNoAndDate.get(0);
						if(lObjVoucherNoAndDate[0] != null)
						{
							if(!"".equals(lObjVoucherNoAndDate[0].toString().trim()))
								lLongVoucherNo = Long.valueOf(lObjVoucherNoAndDate[0].toString().trim());
						}
						if(lObjVoucherNoAndDate[1] != null)
						{
							if(!"".equals(lObjVoucherNoAndDate[1].toString().trim()))
								lDateVoucherDate = sdf1.parse(lObjVoucherNoAndDate[1].toString().trim());
						}
					}
				}

				inputMap.put("voucherNo", lLongVoucherNo);
				inputMap.put("voucherDate", lDateVoucherDate);

				/* Puts All Above Values in InputMap */

				inputMap.put("lLongbillGroupId", lLongbillGroupId);
				inputMap.put("treasuryCode", treasuryCode);
				inputMap.put("schemename", schemename);
				inputMap.put("schemeCode", schemeCode);
				inputMap.put("FirstDate", lDtFirstDate);
				inputMap.put("LastDate", lDtLastDate);

				inputMap.put("schdlStartDate", sdf1.format(lDtFirstDate));
				inputMap.put("schdlEndDate", sdf1.format(lDtLastDate));

				// Below code commented as seems of no use.

				/*
				inputMap.put("DelFirstDate", lDtDelFirstDate);
				inputMap.put("DelLastDate", lDtDelLastDate);
				 */
				String elementId= StringUtility.getParameter("elementId",request).trim();
				gLogger.info(" elementId is : " +elementId);
				inputMap.put("elementId", elementId);
				inputMap.put("empList", empList);
				inputMap.put("GoPressed", "Y");

			}

				inputMap.put("MissingCreditOrNot", lStrMissingCreditOrNot);
			gLogger.info("MissingCreditOrNot"+lStrMissingCreditOrNot);
			inputMap.put("hidBGIdForMissingCredit", gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));

			inputMap.put("BillGroupList", BillGroupList);

			inputMap.put("monthId", monthId);
			inputMap.put("yearId", yearId);
			inputMap.put("lStrDDOCode", lStrDdoCode);
			inputMap.put("YEARS", lLstYears);
			inputMap.put("DELAYEDYEARS", lLstDelayedYears);
			//	inputMap.put("BILLGROUPS", lLstBillGroups);
			inputMap.put("MONTHS", lLstMonths);
			inputMap.put("lStrUser", lStrUserType);
			inputMap.put("lStrUse", lStrUseType);
			gLogger.info("lStrUseValueType"+lStrUseValueType);
			inputMap.put("lStrUseValue", lStrUseValueType);
			inputMap.put("lStrTransactionId", lStrTransactionId);
			inputMap.put("listTypeOfPayment", listTypeOfPayment);
			inputMap.put("manualBillEntry", manualBillEntry);
//			inputMap.put("lStrSevarthId", lStrSevarthId);
			inputMap.put("lStrDcpsId", lStrDcpsId);

			resObj.setResultValue(inputMap);

			//	if(inputMap.get("fromPayBillScheduler")==null) // For Scheuler only 
			resObj.setViewName("manualBillEntry");


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

	public ResultObject saveContributions(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long regStatus = null;
		Long lLngContriVoucherIdPassed = null;
		Integer savedFlag = null;
		Boolean successFlag = false;
		Integer lIntContinueFlag = 0;
		Long lLongContriIdForDelete = null;
		Long lLongIndexOfDeletedContri = null;
		TrnDcpsContribution lObjTrnDcpsContributionforDelete = null;

		Session saveTrnContriSession = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes DAO and variables */
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			resObj = serv.executeService("saveVoucherDtlsForContri", inputMap);

			String lStrContriVoucherIdPassed = inputMap.get(
			"lLngContriVoucherIdToBePassed").toString().trim();

			if (!"".equals(lStrContriVoucherIdPassed)) {
				lLngContriVoucherIdPassed = Long
				.valueOf(lStrContriVoucherIdPassed);
			}
			gLogger.info("lStrContriVoucherIdPassed is *********"+lStrContriVoucherIdPassed);
			// Below is a wrapper for taking care that when a voucher is forwarded through scheduler it does not allow to send forward the contributions from UI at the same time when scheduler is running.
			MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) inputMap.get("voucherVOToCheckThruSchdlr");
			Boolean lBlGoInsideSaveContri = true;

			if(lObjMstDcpsContriVoucherDtls != null)
			{
				if(lObjMstDcpsContriVoucherDtls.getVoucherStatus() != null)
				{
					if(lObjMstDcpsContriVoucherDtls.getVoucherStatus() == 3)
					{
						lBlGoInsideSaveContri = false;
					}
				}
			}

			//if(lBlGoInsideSaveContri) {

			/* Creates entry of HstDcpsContribution in table */

			if (inputMap.containsKey("lObjHstDcpsContribution")) {
				HstDcpsContribution lObjHstDcpsContribution = (HstDcpsContribution) inputMap
				.get("lObjHstDcpsContribution");

				Long lLongHstContributionId = IFMSCommonServiceImpl
				.getNextSeqNum("HST_DCPS_CONTRIBUTION", inputMap);

				lObjHstDcpsContribution
				.setDcpsContributionHstId(lLongHstContributionId);

				lObjOfflineContriDAO.create(lObjHstDcpsContribution);
			}

			/* Gets the Contribution Ids from request */
			String lStrTotalRecords = StringUtility.getParameter("hdnCounter",
					request).trim();
			Integer lIntTotalRecords = Integer.parseInt(lStrTotalRecords);
			String lStrDeletedContributionIndexes = StringUtility.getParameter(
					"deletedContributionIndexes", request).trim();

			String[] lStrArrDeletedContributionIndexes = null;

			if (!"".equals(lStrDeletedContributionIndexes)) {
				lStrArrDeletedContributionIndexes = lStrDeletedContributionIndexes
				.split("~");
			}
			gLogger.info("Indexes are *******"+lStrDeletedContributionIndexes);
			String[] lStrArrDeletedContributionIdPks = null;
			String lStrDeletedContributionIdPks = StringUtility.getParameter(
					"deletedContributionIdPks", request).trim();

			if (!"".equals(lStrDeletedContributionIndexes)) {
				lStrArrDeletedContributionIdPks = lStrDeletedContributionIdPks
				.split("~");
			}


			gLogger.info("lStrDeletedContributionIndexes are *******"+lStrDeletedContributionIndexes);

			/*
			 * String lStrdcpsContributionIds = inputMap
			 * .get("dcpsContributionIds").toString().trim(); String[]
			 * lStrArrdcpsContributionIds = lStrdcpsContributionIds .split("~");
			 * 
			 * Long[] lLongArrdcpsContributionIds = new
			 * Long[lStrArrdcpsContributionIds.length];
			 * 
			 * for (Integer lInt = 0; lInt < lStrArrdcpsContributionIds.length;
			 * lInt++) { lLongArrdcpsContributionIds[lInt] = Long
			 * .valueOf(lStrArrdcpsContributionIds[lInt]); }
			 */

			StringBuffer lStrContriIdsForForward = new StringBuffer();
			Long lLongTempdcpsContributionId = null;
			Integer saveOrForwardFlag = Integer.parseInt(StringUtility
					.getParameter("saveOrForwardFlag", request).trim());

			/* Creates or Updates the record in TrnDcpsContribution Table */
			TrnDcpsContribution[] lArrTrnDcpsContributions = (TrnDcpsContribution[]) inputMap
			.get("lArrTrnDcpsContributions");

			Long lLongContributionId = null;

			String lStrUser = StringUtility.getParameter("User", request).trim();
			String lStrUse = StringUtility.getParameter("Use", request).trim();
			String lStrUseValueType = StringUtility.getParameter("UseValue", request).trim();
			String lStrtransId = StringUtility.getParameter("transId", request).trim();
			
			// Code added later for Missing Credits
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			Long lLongMonthId = Long.valueOf(StringUtility.getParameter("cmbMonth", request).trim());
			Long lLongYearId = Long.valueOf(StringUtility.getParameter("cmbYear", request).trim());
			Long lLongBillGroupId = Long.valueOf(StringUtility.getParameter("cmbBillGroup",request).trim());
			String lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request).trim();
			String lStrSchemeCode = StringUtility.getParameter("schemeCode", request).trim();

			Long lLongVoucherNo = null;
			String lStrVoucherDate = null;
			Date lDateVoucherDate = null;
			Long manualEntry = Long.parseLong(lStrtransId);
			if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
			{
				lLongVoucherNo = Long.valueOf(StringUtility.getParameter("txtVoucherNo",request).trim());
				lStrVoucherDate = StringUtility.getParameter("txtVoucherDate",request).trim();
				lDateVoucherDate = simpleDateFormat.parse(lStrVoucherDate.trim());
			}
			//Code added later for Missing Credits ends

			for (Integer lInt = 1; lInt <= lIntTotalRecords; lInt++) {

				lIntContinueFlag = 0;

				if (lStrArrDeletedContributionIndexes != null) {
					for (Integer lIntDelete = 0; lIntDelete < lStrArrDeletedContributionIndexes.length; lIntDelete++) {
						if (Integer
								.parseInt(lStrArrDeletedContributionIndexes[lIntDelete].trim()) == lInt) {
							lIntContinueFlag = 1;
						}
					}

					if (lIntContinueFlag == 1) {
						continue;
					}
				}

				lLongContributionId = Long.parseLong(StringUtility
						.getParameter("checkbox" + lInt, request).trim());

				if (lLongContributionId == 0l) {
					lLongTempdcpsContributionId = IFMSCommonServiceImpl
					.getNextSeqNum("TRN_DCPS_CONTRIBUTION", inputMap);
					lArrTrnDcpsContributions[lInt - 1]
					                         .setDcpsContributionId(lLongTempdcpsContributionId);
					lArrTrnDcpsContributions[lInt - 1]
					                         .setRltContriVoucherId(lLngContriVoucherIdPassed);

					if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
					{
						lArrTrnDcpsContributions[lInt - 1].setVoucherNo(lLongVoucherNo);
						lArrTrnDcpsContributions[lInt - 1].setVoucherDate(lDateVoucherDate);
						lArrTrnDcpsContributions[lInt - 1].setIsMissingCredit('Y');
						lArrTrnDcpsContributions[lInt - 1].setMissingCreditApprovalDate(gDtCurDate);
						if (lStrUseValueType.equals("Y"))
						{
							lArrTrnDcpsContributions[lInt - 1].setManualBillEntry(manualEntry);
						}
					}
					gLogger.info("lLongTempdcpsContributionId is ***********"+lLongTempdcpsContributionId);
					lObjOfflineContriDAO
					.create(lArrTrnDcpsContributions[lInt - 1]);


					if (saveOrForwardFlag == 2) {
						lStrContriIdsForForward
						.append(lLongTempdcpsContributionId);
						lStrContriIdsForForward.append("~");
					}

				} else {

					lArrTrnDcpsContributions[lInt - 1].setDcpsContributionId(lLongContributionId);
					lArrTrnDcpsContributions[lInt - 1].setRltContriVoucherId(lLngContriVoucherIdPassed);

					// Below If is redundant so removed.
					/*
							if ((lObjOfflineContriDAO.getRegStatusForContriId(lLongContributionId
											.toString())) != null) {
								regStatus = Long.valueOf(lObjOfflineContriDAO.getRegStatusForContriId(lLongContributionId.toString()).toString());
								lArrTrnDcpsContributions[lInt - 1].setRegStatus(regStatus);
							}
					 */

					if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
					{
						lArrTrnDcpsContributions[lInt - 1].setVoucherNo(lLongVoucherNo);
						lArrTrnDcpsContributions[lInt - 1].setVoucherDate(lDateVoucherDate);
						lArrTrnDcpsContributions[lInt - 1].setIsMissingCredit('Y');
						lArrTrnDcpsContributions[lInt - 1].setMissingCreditApprovalDate(gDtCurDate);
						if (lStrUseValueType.equals("Y"))
						{
							lArrTrnDcpsContributions[lInt - 1].setManualBillEntry(manualEntry);
						}		
					}
					gLogger.info("lLongTempdcpsContributionId is ***********"+lLongTempdcpsContributionId);
					lObjOfflineContriDAO.update(lArrTrnDcpsContributions[lInt - 1]);

					if (saveOrForwardFlag == 2) {
						lStrContriIdsForForward.append(lLongContributionId);
						lStrContriIdsForForward.append("~");
					}
				}

			}
			gLogger.info("lLongContributionId is ***********"+lLongContributionId);
			if (saveOrForwardFlag == 1) {

				savedFlag = 1;

			} else if (saveOrForwardFlag == 2) {

				savedFlag = 2;
				inputMap
				.put("lStrContriIdsForForward", lStrContriIdsForForward);

				// Below lines commented for manual contributions
				/*
						if (lStrUser.equals("ATO")) // Manual Contribution
						{
							resObj = forwardRequestToTO(inputMap);
						} else if (lStrUser.equals("DDOAsst")) // Online Contribution
						{
							resObj = FwdContriToDDO(inputMap);
						}
				 */
			}

			inputMap.put("savedFlag", savedFlag);
			
			
			 String elementId=StringUtility.getParameter("elementId", request);
            gLogger.info("elementId is.... "+elementId);
			 inputMap.put("elementId",elementId);
			 
			// Code to delete deleted contributions
			Boolean lBlFlagEntryInTrnForRlt = true;
			if(lStrArrDeletedContributionIdPks != null)
			{
				for(Integer lInt=0;lInt<lStrArrDeletedContributionIdPks.length;lInt++)
				{
					lLongContriIdForDelete = Long.valueOf(lStrArrDeletedContributionIdPks[lInt].trim());
					if(lLongContriIdForDelete != 0L)
					{
						lObjTrnDcpsContributionforDelete = (TrnDcpsContribution) lObjOfflineContriDAO.read(lLongContriIdForDelete);
						lObjOfflineContriDAO.delete(lObjTrnDcpsContributionforDelete);
					}
				}

				if(!"".equals(lStrContriVoucherIdPassed))
				{
					lBlFlagEntryInTrnForRlt = lObjOfflineContriDAO.checkEntryInTrnForRltContriVoucherId(Long.valueOf(lStrContriVoucherIdPassed));
				}
				if(!lBlFlagEntryInTrnForRlt)
				{
					if(!"".equals(lStrContriVoucherIdPassed))
					{
						lObjOfflineContriDAO.deleteVoucherForNoContris(Long.valueOf(lStrContriVoucherIdPassed));
					}
				}
			}

			saveTrnContriSession = serv.getSessionFactory().getCurrentSession();
			saveTrnContriSession.flush();

			//Code to update voucher amount of the voucher added from missing credit screen.

			if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
			{
				/*
						Double voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucherMissingCredit(lLongMonthId,lLongYearId,
							lLongBillGroupId,lStrDDOCode,lStrSchemeCode,lLongVoucherNo,lDateVoucherDate);
				 */
				Double voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucherMissingCredit(lLngContriVoucherIdPassed);
				lObjOfflineContriDAO.updateVoucherAmountInMstContriForMissingCreditVoucher(lLngContriVoucherIdPassed,voucherAmount);
			}

			//	}

			// Code to delete deleted contributions overs

			successFlag = true;

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		/*
		 * Sends XML response for sending the Contribution Ids using AJAX and
		 * the success flag
		 */

		inputMap.put("successFlag", successFlag);
		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSOfflineEntryForm");
		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForRejectContri(Boolean flag,
			Integer regStatus) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<RegStatus>");
		lStrBldXML.append(regStatus);
		lStrBldXML.append("</RegStatus>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject getBillGroupsForDdo(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;

		try {

			setSessionInfo(inputMap);

			String lStrDdoCode = StringUtility.getParameter("cmbDDOCode",
					request).trim();

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			String lStrUser = StringUtility.getParameter("User", request).trim();
			String lStrUseType = StringUtility.getParameter("Use", request).trim();

			if (lStrUser.equals("TO")) {
				if(lStrUseType.equals("MissingCreditEntry"))
				{
					finalList = new ArrayList<Object>();
					ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("-1");
					lObjComboValuesVO.setDesc("--Select--");
					finalList.add(lObjComboValuesVO);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(gObjRsrcBndle.getString("DCPS.BGIdForMissingCredit"));
					lObjComboValuesVO.setDesc("Missing Credit");
					finalList.add(lObjComboValuesVO);
				}
				else if(lStrUseType.equals("ViewApprovedByTO"))
				{
					finalList = lObjOfflineContriDAO
					.getBillGroupsForDdoInTOLoginInclMisngCrdt(lStrDdoCode);	
				}
				else
				{
					finalList = lObjOfflineContriDAO
					.getBillGroupsForDdoInTOLogin(lStrDdoCode);
				}

			} else if (lStrUser.equals("DDO")) {

				if (lStrUseType.equals("ViewApproved")) {
					finalList = lObjOfflineContriDAO
					.getApprovedBillGroupsForDdoInDDOLogin(lStrDdoCode);
				} else {
					finalList = lObjOfflineContriDAO
					.getBillGroupsForDdoInDDOLogin(lStrDdoCode);
				}

			} else if (lStrUser.equals("ATO")) {

				if (lStrUseType.equals("ViewRejected")) {
					finalList = lObjOfflineContriDAO
					.getBillGroupsRejectedForDdoInATOLogin(lStrDdoCode);
				} else {
					finalList = lObjOfflineContriDAO
					.getBillGroupsForDdo(lStrDdoCode);
				}
			} else {
				finalList = lObjOfflineContriDAO
				.getBillGroupsForDdo(lStrDdoCode);
			}

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return objRes;
	}

	public ResultObject getApprovedBillGroupsForDdo(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;

		try {

			setSessionInfo(inputMap);

			String lStrDdoCode = StringUtility.getParameter("cmbDDOCode",
					request).trim();

			Long lLongMonthId = Long.valueOf(StringUtility.getParameter(
					"cmbMonth", request).trim());

			Long lLongYearId = Long.valueOf(StringUtility.getParameter(
					"cmbYear", request).trim());

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			finalList = lObjOfflineContriDAO.getApprovedBillGroupsForDdo(
					lStrDdoCode, lLongMonthId, lLongYearId);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return objRes;
	}

	public ResultObject getDDOsForTreasury(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;

		try {

			setSessionInfo(inputMap);

			String lStrTreasuryCode = StringUtility.getParameter(
					"cmbTreasuryCode", request).trim();

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			//finalList = lObjOfflineContriDAO.getAllDDO(lStrTreasuryCode);
			finalList = lObjOfflineContriDAO.getAllDDOCodeASC(lStrTreasuryCode);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return objRes;
	}

	public ResultObject popUpVoucherDtls(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstDcpsContriVoucherDtls lLObjMstContriVoucherDtls = null;

		try {

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			String lStrDDOCode = StringUtility.getParameter("cmbDDOCode",
					request);
			Long monthId = Long.parseLong(StringUtility.getParameter(
					"cmbMonth", request));
			Long yearId = Long.parseLong(StringUtility.getParameter("cmbYear",
					request));
			Long lLongbillGroupId = Long.valueOf(StringUtility.getParameter(
					"cmbBillGroup", request));
			Long treasuryCode = Long.valueOf(StringUtility.getParameter(
					"cmbTreasuryCode", request));

			lLObjMstContriVoucherDtls = lObjOfflineContriDAO
			.getContriVoucherVOForInputDtlsForPopup(yearId, monthId,
					lStrDDOCode, lLongbillGroupId, treasuryCode);

			String lSBStatus = getResponseXMLDocForPopupVoucherDtls(
					lLObjMstContriVoucherDtls).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}
	private StringBuilder getResponseXMLDocForPopupVoucherDtls(
			MstDcpsContriVoucherDtls lLObjMstContriVoucherDtls) {

		StringBuilder lStrBldXML = new StringBuilder();

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		DdoBillGroupDAO lObjDdoBillGroupDAO = new DdoBillGroupDAOImpl(null,
				serv.getSessionFactory());
		MstDcpsBillGroup lObjMstDcpsBillGroup = null;
		String lStrVoucherDate = "";

		try {
			if (lLObjMstContriVoucherDtls != null) {
				if(lLObjMstContriVoucherDtls.getVoucherDate() != null)
				{
					lStrVoucherDate = sdf2.format(sdf1
							.parse(lLObjMstContriVoucherDtls.getVoucherDate()
									.toString()));
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (lLObjMstContriVoucherDtls != null) {
			lObjMstDcpsBillGroup = lObjDdoBillGroupDAO
			.getBillGroupDtlsForBillGroupId(lLObjMstContriVoucherDtls
					.getBillGroupId());
		}

		lStrBldXML.append("<XMLDOC>");

		if (lLObjMstContriVoucherDtls != null) {
			lStrBldXML.append("<schemeName>");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(lObjMstDcpsBillGroup.getDcpsDdoBillSchemeName());
			lStrBldXML.append("]]>");
			lStrBldXML.append("</schemeName>");
		} else {
			lStrBldXML.append("<schemeName>");
			lStrBldXML.append("");
			lStrBldXML.append("</schemeName>");
		}

		if (lLObjMstContriVoucherDtls != null && lLObjMstContriVoucherDtls.getVoucherNo() != null) {
			lStrBldXML.append("<voucherNo>");
			lStrBldXML.append(lLObjMstContriVoucherDtls.getVoucherNo());
			lStrBldXML.append("</voucherNo>");
		} else {
			lStrBldXML.append("<voucherNo>");
			lStrBldXML.append("");
			lStrBldXML.append("</voucherNo>");
		}

		if (lLObjMstContriVoucherDtls != null) {
			lStrBldXML.append("<voucherDate>");
			lStrBldXML.append(lStrVoucherDate);
			lStrBldXML.append("</voucherDate>");
		} else {
			lStrBldXML.append("<voucherDate>");
			lStrBldXML.append("");
			lStrBldXML.append("</voucherDate>");
		}

		if (lLObjMstContriVoucherDtls != null) {
			lStrBldXML.append("<reasonForReversion>");
			if (lLObjMstContriVoucherDtls.getReasonForReversion() != null
					&& !lLObjMstContriVoucherDtls.getReasonForReversion()
					.equalsIgnoreCase("")) {
				lStrBldXML.append(lLObjMstContriVoucherDtls
						.getReasonForReversion());
			}
			lStrBldXML.append("</reasonForReversion>");
		} else {
			lStrBldXML.append("<reasonForReversion>");
			lStrBldXML.append("");
			lStrBldXML.append("</reasonForReversion>");
		}

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;

	}


	public ResultObject checkContriOfEmpForSelectedPeriod(
			Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = null;

		try {

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			String lStrDcpsEmpId = StringUtility.getParameter("dcpsEmpId",
					request);
			String lStrContriStartDate = StringUtility.getParameter(
					"contriStartDate", request);
			String lStrContriEndDate = StringUtility.getParameter(
					"contriEndDate", request);
			String lStrTypeOfPayment = StringUtility.getParameter(
					"typeOfPayment", request);

			Long lLngDcpsEmpId = null;
			Date lDateContriStartDate = null;
			Date lDateContriEndDate = null;
			String voucherNo="";
			String voucherDate="";
			Double Contribution=null;
			String lSBStatus =null;
			String status = null;
			if (!"".equals(lStrDcpsEmpId)) {
				lLngDcpsEmpId = Long.valueOf(lStrDcpsEmpId.trim());
			}
			if (!"".equals(lStrContriStartDate)) {
				lDateContriStartDate = sdf.parse(lStrContriStartDate.trim());
			}
			if (!"".equals(lStrContriEndDate)) {
				lDateContriEndDate = sdf.parse(lStrContriEndDate.trim());
			}

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);

			lBlFlag = lObjOfflineContriDAO.checkContriOfEmpForSelectedPeriod(
					lLngDcpsEmpId, lDateContriStartDate, lDateContriEndDate,
					lStrTypeOfPayment,lStrDdoCode);
			
			if(!lBlFlag){
				List lEmployeeRecords=lObjOfflineContriDAO.getContriOfEmpForSelectedPeriod(
						lLngDcpsEmpId, lDateContriStartDate, lDateContriEndDate,
						lStrTypeOfPayment,lStrDdoCode);
				if(lEmployeeRecords!=null && lEmployeeRecords.size()>0){
					Iterator it = lEmployeeRecords.iterator();
					Object [] tuple=null;
					while (it.hasNext())
					{
						tuple = (Object[]) it.next();
						if(tuple[0]!=null)
						{
							voucherNo =tuple[0].toString();
						}
						if(tuple[1]!=null)
						{
							voucherDate =tuple[1].toString();
						}
						
						Contribution =Double.parseDouble(tuple[2].toString());
						status =tuple[3].toString();
						gLogger.info("status is *********"+status);
						 lSBStatus = getResponseXMLDocForOffline(lBlFlag,voucherNo,voucherDate,Contribution,status).toString();
					}
				}
				
				
			}
			else{
				lSBStatus= getResponseXMLDocForOffline(lBlFlag,"NA","NA",0.0,"NA").toString();
			}

			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}

	public ResultObject displayEmployeeDtls(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstEmp lObjMstEmp = null;

		try {

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					MstEmp.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String lStrDcpsId = StringUtility.getParameter("dcpsId", request).trim();
			//String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);

			//lObjMstEmp = lObjNewRegDdoDAO.getEmpVOForDCPSId(lStrDcpsId,lStrDdoCode);
			lObjMstEmp = lObjOfflineContriDAO.getEmpVOForDCPSId(lStrDcpsId);

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is " + e, e);
		}

		/*
		 * Generates XML response for all schemes found whose scheme code starts
		 * with given scheme code
		 */
		String lSBStatus = getResponseXMLDocToDisplayEmpDtls(lObjMstEmp)
		.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDocToDisplayEmpDtls(MstEmp lObjMstEmp) {

		OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
				TrnDcpsContribution.class, serv.getSessionFactory());

		Float DARate = lObjOfflineContriDAO
		.getDARateForPayCommission(lObjMstEmp.getPayCommission());

		StringBuilder lStrBldXML = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<name>");
		lStrBldXML.append(lObjMstEmp.getName());
		lStrBldXML.append("</name>");

		lStrBldXML.append("<payCommission>");
		lStrBldXML.append(lObjMstEmp.getPayCommission());
		lStrBldXML.append("</payCommission>");

		lStrBldXML.append("<basic>");
		lStrBldXML.append(lObjMstEmp.getBasicPay());
		lStrBldXML.append("</basic>");

		lStrBldXML.append("<doj>");
		lStrBldXML.append(sdf.format(lObjMstEmp.getDoj()));
		lStrBldXML.append("</doj>");

		lStrBldXML.append("<dcpsEmpId>");
		lStrBldXML.append(lObjMstEmp.getDcpsEmpId());
		lStrBldXML.append("</dcpsEmpId>");

		lStrBldXML.append("<daRate>");
		lStrBldXML.append(DARate);
		lStrBldXML.append("</daRate>");

		lStrBldXML.append("<RegStatus>");
		lStrBldXML.append(lObjMstEmp.getRegStatus());
		lStrBldXML.append("</RegStatus>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject getDARateForGivenPrd(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Double lDoubleDARate = 0d;

		try {

			setSessionInfo(inputMap);

			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			StringUtility.getParameter("dcpsEmpId", request);
			String lStrContriStartDate = StringUtility.getParameter(
					"contriStartDate", request);
			String lStrContriEndDate = StringUtility.getParameter(
					"contriEndDate", request);
			String lStrPayCommission = StringUtility.getParameter(
					"payCommission", request);

			Date lDateContriStartDate = null;
			Date lDateContriEndDate = null;

			if (!"".equals(lStrContriStartDate)) {
				lDateContriStartDate = sdf.parse(lStrContriStartDate.trim());
			}
			if (!"".equals(lStrContriEndDate)) {
				lDateContriEndDate = sdf.parse(lStrContriEndDate.trim());
			}

			lDoubleDARate = lObjOfflineContriDAO
			.getDARateForGivenPrd(lDateContriStartDate,
					lDateContriEndDate, lStrPayCommission);

			String lSBStatus = getResponseXMLDocForDARate(lDoubleDARate)
			.toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;

	}

	private StringBuilder getResponseXMLDocForDARate(Double lDoubleDARate) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(lDoubleDARate.toString().trim());
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	private void insertRegularContributions(Map<String, Object> inputMap) throws Exception
	{
		Boolean lBlFlag = false; 
		String lStrTreasuryCode = null;
		String lStrMonthId = null;
		Long lLongMonthId = 0l;
		Integer lIntMonthIdCal = 0;
		String lStrYearId = null;
		String lStrYearIdFromPayroll = null;
		Long lLongYearIdFromPayroll = 0l;
		Long lLongYearId = 0l;
		Integer lIntYearIdCal = 0;
		String lStrBillGroupId = null;
		Long lLongBillGroupId = null;
		String lStrVoucherDate = null;
		String lStrVoucherNo = null;
		Long lLongVoucherNo = null;
		String lStrDDOCode = null;
		String lStrStartDate = null;
		String lStrEndDate = null;
		String lStrEndDateDay = null;
		Date lDtStartDate = null;
		Date lDtEndDate = null;

		List lListRegularContributionsFromPayroll = null;
		Long lLngContriVoucherIdToBePassed = null;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Long lLongTreasuryCode = null;
		String lStrSchemeCode = null;

		List lListDelayedTypeEmpListFromPayroll = null;
		List lListPayArrearTypeEmpListFromPayroll = null;
		List lListDAArrearTypeEmpListFromPayroll = null;

		try {
			setSessionInfo(inputMap);

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(TrnDcpsContribution.class, serv.getSessionFactory());

			lStrMonthId = inputMap.get("cmbMonth").toString().trim();
			lStrYearId = inputMap.get("cmbYear").toString().trim();
			lStrYearIdFromPayroll = lStrYearId;
			
			   if (inputMap.get("txtVoucherNo") != null) {
			        lStrVoucherNo = inputMap.get("txtVoucherNo").toString().trim();
			        lLongVoucherNo = Long.valueOf(lStrVoucherNo);
			      }
			      if (inputMap.get("txtVoucherDt") != null) {
			        lStrVoucherDate = inputMap.get("txtVoucherDt").toString().trim();
			      }
			      
			

			lStrBillGroupId = inputMap.get("cmbBillGroup").toString().trim();

			lStrDDOCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId).trim();
			lStrTreasuryCode = lObjDcpsCommonDAO.getTreasuryCodeForDDO(lStrDDOCode.trim()).trim();
			lLongTreasuryCode = Long.valueOf(lStrTreasuryCode);

			lStrStartDate = "01/" + lStrMonthId + "/" + lStrYearId;

			lLongMonthId = Long.valueOf(lStrMonthId);

			lIntMonthIdCal = Integer.valueOf(lLongMonthId.toString()) - 1;
			lIntYearIdCal = Integer.valueOf(lStrYearId); 

			lLongYearIdFromPayroll = Long.valueOf(lStrYearIdFromPayroll);

			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(lIntYearIdCal, lIntMonthIdCal , 1);

			int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			lStrEndDateDay = Integer.valueOf(days).toString().trim();

			lStrEndDate = lStrEndDateDay + "/" + lStrMonthId + "/" + lStrYearId;

			lDtStartDate = simpleDateFormat.parse(lStrStartDate.trim());
			lDtEndDate = simpleDateFormat.parse(lStrEndDate.trim());

			lStrYearId = lObjDcpsCommonDAO.getFinYearIdForDate(lDtStartDate);
			lLongYearId = Long.valueOf(lStrYearId);
			lLongBillGroupId = Long.valueOf(lStrBillGroupId);

			lListRegularContributionsFromPayroll = lObjOfflineContriDAO.getRegularContributionsFromPayroll(lLongYearIdFromPayroll,lLongMonthId,lLongBillGroupId);

			lLngContriVoucherIdToBePassed = Long.valueOf(inputMap.get("lLngContriVoucherIdToBePassed").toString());

			lStrSchemeCode = lObjOfflineContriDAO.getSchemeCodeForBillGroup(lLongBillGroupId);

			if(lLongMonthId != 0l && lLongYearId != 0l && lLongBillGroupId != null && lStrDDOCode != null)
			{
				lObjOfflineContriDAO.deleteRegularContributionsIfExist(lLongMonthId, lLongYearId, lLongBillGroupId, lStrDDOCode);
			}
			lObjOfflineContriDAO.insertRegularContributions(lListRegularContributionsFromPayroll,lLongMonthId,lLongYearId,lLongBillGroupId,lStrDDOCode,lLongTreasuryCode,lStrSchemeCode,lDtStartDate,lDtEndDate,gDtCurDate,gLngPostId,Long.valueOf(gStrLocationCode),lLngContriVoucherIdToBePassed,inputMap);

			lObjOfflineContriDAO.updateNonRegularTypeContriStatusInTrn(lLongYearId,lLongMonthId,lLongBillGroupId);

			lListDelayedTypeEmpListFromPayroll = lObjOfflineContriDAO.getEmpListForDelayedTypesInMonthAndBG(lLongYearIdFromPayroll, lLongMonthId, lLongBillGroupId);

			if(lListDelayedTypeEmpListFromPayroll != null)
			{
				if(lListDelayedTypeEmpListFromPayroll.size() != 0)
				{
					lObjOfflineContriDAO.updateDelayedContriStatusInTrn(lListDelayedTypeEmpListFromPayroll, lLongMonthId, lLongYearId, lLongBillGroupId, lStrDDOCode);
				}
			}

			lListPayArrearTypeEmpListFromPayroll = lObjOfflineContriDAO.getEmpListForPayArrearTypesInMonthAndBG(lLongYearIdFromPayroll, lLongMonthId, lLongBillGroupId);

			if(lListPayArrearTypeEmpListFromPayroll != null)
			{
				if(lListPayArrearTypeEmpListFromPayroll.size() != 0)
				{
					lObjOfflineContriDAO.updatePayArrearContriStatusInTrn(lListPayArrearTypeEmpListFromPayroll, lLongMonthId, lLongYearId, lLongBillGroupId, lStrDDOCode);
				}
			}

			lListDAArrearTypeEmpListFromPayroll = lObjOfflineContriDAO.getEmpListForDAArrearTypesInMonthAndBG(lLongYearIdFromPayroll, lLongMonthId, lLongBillGroupId);

			if(lListDAArrearTypeEmpListFromPayroll != null)
			{
				if(lListDAArrearTypeEmpListFromPayroll.size() != 0)
				{
					lObjOfflineContriDAO.updateDAArrearContriStatusInTrn(lListDAArrearTypeEmpListFromPayroll, lLongMonthId, lLongYearId, lLongBillGroupId, lStrDDOCode);
				}
			}

			//Flushing the session so that the above transactions commit before updating

			//Session saveTrnAndMstContriSession = serv.getSessionFactory().getCurrentSession();
			//saveTrnAndMstContriSession.flush();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			 Date voucherDate = null;
		      if (lStrVoucherDate != null)
		        voucherDate = sdf.parse(lStrVoucherDate);
		      lObjOfflineContriDAO.updateVoucherDetailsInTrnDcpsContri(lLongBillGroupId, lLongMonthId, lLongYearId, lStrDDOCode, lLongVoucherNo, voucherDate);

		      
			// Code to update voucher_amount,voucher_status,status in mst_dcps_contri_voucher_dtls

			Double lDoubleVoucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
			inputMap.put("voucherAmount", lDoubleVoucherAmount);
			//lObjOfflineContriDAO.updateVoucherDetailsInMstContriOnApproval(lLngContriVoucherIdToBePassed,lDoubleVoucherAmount);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			gLogger.error(" Error in insertRegularContributions " + e, e);
			throw e;
		}

	}

	public ResultObject saveVoucherDtlsForContri(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		resObj.setResultCode(-1);
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		MstDcpsContriVoucherDtls lObjPreviousMstDcpsContriVoucherDtls = null;
		Boolean lBlFlag = null;
		Long lLngDcpsContriVoucherId = null;
		Long lLongVoucherNo = null;
		Date lDateVoucherDate = null;
		Long lLongTreasuryCode = null;
		Double voucherAmount = null;
		Long lLngContriVoucherIdToBePassed = null;

		setSessionInfo(inputMap);

		OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(
				MstDcpsContriVoucherDtls.class, serv.getSessionFactory());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"dd/MM/yyyy");

		String lStrUser = StringUtility.getParameter("User", request)
		.trim();
		inputMap.put("RLUser", lStrUser);

		String lStrUse = StringUtility.getParameter("Use", request).trim();
		inputMap.put("RLUse", lStrUse);

		String lStrTypeOfPaymentMaster = StringUtility.getParameter(
				"cmbTypeOfPaymentMaster", request).trim();
		inputMap.put("typeOfPaymentMaster", lStrTypeOfPaymentMaster);

		String lStrDelayedMonth = "";
		String lStrDelayedYear = "";

		if (lStrTypeOfPaymentMaster.equals("700047")) {
			lStrDelayedMonth = StringUtility.getParameter(
					"cmbDelayedMonth", request).trim();
			lStrDelayedYear = StringUtility.getParameter("cmbDelayedYear",
					request).trim();
		}

		inputMap.put("delayedMonth", lStrDelayedMonth);
		inputMap.put("delayedYear", lStrDelayedYear);

		String lStrApprovedFlag = "";

		if (!"".equalsIgnoreCase(StringUtility.getParameter("approvedFlag",
				request).trim())) {
			lStrApprovedFlag = StringUtility.getParameter("approvedFlag",
					request).trim();
		}

		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
				.getSessionFactory());

		String lStrFreezeFlag = null;
		if (inputMap.containsKey("FreezeFlag")) {
			lStrFreezeFlag = inputMap.get("FreezeFlag").toString().trim();
		}
		Integer lIntFreezeFlag = 0;
		String lStrDDOCode = null;
		String lStrTreasuryCode = null;
		String lStrMonthId = null;
		Long lLongMonthId = 0l;
		String lStrYearId = null;
		Long lLongYearId = 0l;
		String lStrBillGroupId = null;
		String lStrVoucherDate = null;
		String lStrVoucherNo = null;
		if (lStrFreezeFlag != null) {
			lIntFreezeFlag = Integer.parseInt(lStrFreezeFlag);

		}
		if (lIntFreezeFlag == 1) {
			lStrDDOCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId).trim();
			lStrTreasuryCode = lObjDcpsCommonDAO
			.getTreasuryCodeForDDO(lStrDDOCode.trim()).trim();
			lStrMonthId = inputMap.get("cmbMonth").toString().trim();
			lStrYearId = inputMap.get("cmbYear").toString().trim();

			String lStrStartDate = "01/" + lStrMonthId + "/" + lStrYearId;

			Date lDtStartDate = simpleDateFormat.parse(lStrStartDate.trim());

			lStrYearId = lObjDcpsCommonDAO
			.getFinYearIdForDate(lDtStartDate);

			 if (inputMap.get("txtVoucherNo") != null)
			        lStrVoucherNo = inputMap.get("txtVoucherNo").toString().trim();

			      if (inputMap.get("txtVoucherDt") != null) {
			        lStrVoucherDate = inputMap.get("txtVoucherDt").toString().trim();
			      }


			lStrBillGroupId = inputMap.get("cmbBillGroup").toString().trim();

			//insertRegularContributions(inputMap);
			//resObj = serv.executeService("insertRegularContributions", inputMap);

		} else {

			lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request).trim();
			lStrTreasuryCode = StringUtility.getParameter(
					"cmbTreasuryCode", request).trim();
			lStrMonthId = StringUtility.getParameter("cmbMonth", request).trim();

			lStrYearId = StringUtility.getParameter("cmbYear", request).trim();

			lStrBillGroupId = StringUtility.getParameter("cmbBillGroup",
					request).trim();
			lStrVoucherNo = StringUtility.getParameter("txtVoucherNo",
					request).trim();
			lStrVoucherDate = StringUtility.getParameter("txtVoucherDate",
					request).trim();

		}

		lLongMonthId = Long.valueOf(lStrMonthId);
		lLongYearId = Long.valueOf(lStrYearId);

		if (lStrTreasuryCode != null
				&& !"".equalsIgnoreCase(lStrTreasuryCode)) {
			lLongTreasuryCode = Long.valueOf(lStrTreasuryCode);
		}

		Long lLongBillGroupId = Long.valueOf(lStrBillGroupId);

		// Scheme code got for bill-group

		String lStrSchemeCode = null;

		if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
		{
			lStrSchemeCode = StringUtility.getParameter("schemeCode", request).trim();
		}
		else
		{
			lStrSchemeCode = lObjOfflineContriDAO.getSchemeCodeForBillGroup(lLongBillGroupId).trim();
		}

		if (lStrVoucherNo != null && !"".equalsIgnoreCase(lStrVoucherNo)) {
			lLongVoucherNo = Long.valueOf(lStrVoucherNo.trim());
		}

		if (lStrVoucherDate != null && !"".equals(lStrVoucherDate.trim())) {
			lDateVoucherDate = simpleDateFormat.parse(lStrVoucherDate.trim());
		}

		if(lStrUser.equals("TO") && lStrUse.equals("MissingCreditEntry"))
		{
			lObjPreviousMstDcpsContriVoucherDtls = lObjOfflineContriDAO.getContriVoucherVOForInputDtlsMissingCredit(lLongYearId,lLongMonthId,
					lStrDDOCode,lLongBillGroupId,lStrSchemeCode,lLongVoucherNo,lDateVoucherDate);
		}
		else
		{
			lObjPreviousMstDcpsContriVoucherDtls = lObjOfflineContriDAO.getContriVoucherVOForInputDtls(lLongYearId, lLongMonthId,
					lStrDDOCode, lLongBillGroupId);
		}

		if (lObjPreviousMstDcpsContriVoucherDtls == null) {
			// Create
			lLngDcpsContriVoucherId = IFMSCommonServiceImpl.getNextSeqNum(
					"mst_dcps_contri_voucher_dtls", inputMap);
			lObjMstDcpsContriVoucherDtls = new MstDcpsContriVoucherDtls();
			lObjMstDcpsContriVoucherDtls
			.setDcpsContriVoucherDtlsId(lLngDcpsContriVoucherId);
			lObjMstDcpsContriVoucherDtls.setBillGroupId(lLongBillGroupId);
			lObjMstDcpsContriVoucherDtls.setCreatedDate(gDtCurDate);
			lObjMstDcpsContriVoucherDtls.setDdoCode(lStrDDOCode);
			lObjMstDcpsContriVoucherDtls.setMonthId(lLongMonthId);
			lObjMstDcpsContriVoucherDtls.setPostId(gLngPostId);
			lObjMstDcpsContriVoucherDtls.setUserId(gLngUserId);

			// scheme code updated

			lObjMstDcpsContriVoucherDtls.setSchemeCode(lStrSchemeCode);

			if (lStrVoucherDate != null
					&& !"".equals(lStrVoucherDate.trim())) {
				lObjMstDcpsContriVoucherDtls
				.setVoucherDate(lDateVoucherDate);
			} else {
				lObjMstDcpsContriVoucherDtls.setVoucherDate(null);
			}

			if (lStrVoucherNo != null
					&& !"".equalsIgnoreCase(lStrVoucherNo)) {
				lObjMstDcpsContriVoucherDtls.setVoucherNo(lLongVoucherNo);
			} else {
				lObjMstDcpsContriVoucherDtls.setVoucherNo(null);
			}

			lObjMstDcpsContriVoucherDtls.setTreasuryCode(lLongTreasuryCode);
			lObjMstDcpsContriVoucherDtls.setYearId(lLongYearId);

			if (lStrUser.equals("ATO")) {
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(null);
			}
			if (lStrUser.equals("TO")) {

				if(lStrUse.equals("MissingCreditEntry"))
				{
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					//voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucherMissingCredit(lLongMonthId,lLongYearId,lLongBillGroupId,lStrDDOCode,lStrSchemeCode,lLongVoucherNo,lDateVoucherDate);
					lObjMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
					lObjMstDcpsContriVoucherDtls.setStatus('F');
					lObjMstDcpsContriVoucherDtls.setIsMissingCredit('Y');
					lObjMstDcpsContriVoucherDtls.setMissingCreditApprovalDate(gDtCurDate);
				}
				else
				{
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
					lObjMstDcpsContriVoucherDtls.setStatus('A');
				}

			}
			if (lStrUser.equals("DDO")) {
				if (lStrApprovedFlag.equalsIgnoreCase("Approved")) {

					lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					lObjMstDcpsContriVoucherDtls.setStatus('A');
					voucherAmount = lObjOfflineContriDAO
					.getTotalVoucherAmountForGivenVoucher(
							lLongMonthId, lLongYearId,
							lLongBillGroupId, lStrDDOCode);
					lObjMstDcpsContriVoucherDtls
					.setVoucherAmount(voucherAmount);
				} else {
					lObjMstDcpsContriVoucherDtls.setVoucherStatus(3l); // Since
					// Forwarded
					// by
					// DDO
					// in
					// Online
				}
			}

			if (lIntFreezeFlag == 1) {

				// Below code uncommented as voucher amount will be inserted only when approved by treasury 
				//voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
				//lObjMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
				// Changed after last check in
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(3l);

			}
			lObjMstDcpsContriVoucherDtls.setManuallyMatched(0l);
			lObjMstDcpsContriVoucherDtls.setPostEmplrContriStatus(0l);
			lObjMstDcpsContriVoucherDtls.setReversionFlag(0l);

			if (lIntFreezeFlag != 1)
			{
				lObjOfflineContriDAO.create(lObjMstDcpsContriVoucherDtls);
			}

			lLngContriVoucherIdToBePassed = lLngDcpsContriVoucherId;

			inputMap.put("voucherVOToCheckThruSchdlr", lObjMstDcpsContriVoucherDtls);

		} else {
			// Updates
			lObjPreviousMstDcpsContriVoucherDtls
			.setBillGroupId(lLongBillGroupId);
			lObjPreviousMstDcpsContriVoucherDtls.setUpdatedDate(gDtCurDate);
			lObjPreviousMstDcpsContriVoucherDtls.setDdoCode(lStrDDOCode);
			lObjPreviousMstDcpsContriVoucherDtls.setMonthId(lLongMonthId);

			lObjPreviousMstDcpsContriVoucherDtls.setSchemeCode(lStrSchemeCode);

			if (lStrVoucherDate != null
					&& !"".equals(lStrVoucherDate.trim())) {
				lObjPreviousMstDcpsContriVoucherDtls
				.setVoucherDate(lDateVoucherDate);
			} else {
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherDate(null);
			}

			if (lStrVoucherNo != null
					&& !"".equalsIgnoreCase(lStrVoucherNo)) {
				lObjPreviousMstDcpsContriVoucherDtls
				.setVoucherNo(lLongVoucherNo);
			} else {
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherNo(null);
			}

			lObjPreviousMstDcpsContriVoucherDtls
			.setTreasuryCode(lLongTreasuryCode);
			lObjPreviousMstDcpsContriVoucherDtls.setYearId(lLongYearId);
			lObjPreviousMstDcpsContriVoucherDtls
			.setUpdatedPostId(gLngPostId);
			lObjPreviousMstDcpsContriVoucherDtls
			.setUpdatedUserId(gLngUserId);

			if (lStrUser.equals("ATO")) {
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(null);
			}
			if (lStrUser.equals("TO")) {

				if(lStrUse.equals("MissingCreditEntry"))
				{
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					//voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucherMissingCredit(lLongMonthId,lLongYearId,lLongBillGroupId,lStrDDOCode,lStrSchemeCode,lLongVoucherNo,lDateVoucherDate);
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
					lObjPreviousMstDcpsContriVoucherDtls.setStatus('F');
					lObjPreviousMstDcpsContriVoucherDtls.setIsMissingCredit('Y');
					lObjPreviousMstDcpsContriVoucherDtls.setMissingCreditApprovalDate(gDtCurDate);
				}
				else
				{
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(1l);
					lObjPreviousMstDcpsContriVoucherDtls.setStatus('A');
					voucherAmount = lObjOfflineContriDAO.getTotalVoucherAmountForGivenVoucher(lLongMonthId,lLongYearId, lLongBillGroupId, lStrDDOCode);
					lObjPreviousMstDcpsContriVoucherDtls.setVoucherAmount(voucherAmount);
				}

			}
			if (lStrUser.equals("DDO")) {
				if (lStrApprovedFlag.equalsIgnoreCase("Approved")) {
					lObjPreviousMstDcpsContriVoucherDtls
					.setVoucherStatus(1l);
					voucherAmount = lObjOfflineContriDAO
					.getTotalVoucherAmountForGivenVoucher(
							lLongMonthId, lLongYearId,
							lLongBillGroupId, lStrDDOCode);
					lObjPreviousMstDcpsContriVoucherDtls
					.setVoucherAmount(voucherAmount);
				} else {
					lObjPreviousMstDcpsContriVoucherDtls
					.setVoucherStatus(3l); // Since Forwarded by DDO
					// in Online
				}
			}

			if (lIntFreezeFlag == 1) {

				lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(3l);

			}

			lObjPreviousMstDcpsContriVoucherDtls.setManuallyMatched(0l);
			lObjPreviousMstDcpsContriVoucherDtls.setPostEmplrContriStatus(0l);
			lObjPreviousMstDcpsContriVoucherDtls.setReversionFlag(0l);

			if (lIntFreezeFlag != 1)
			{
				lObjOfflineContriDAO.update(lObjPreviousMstDcpsContriVoucherDtls);
			}

			lLngContriVoucherIdToBePassed = lObjPreviousMstDcpsContriVoucherDtls
			.getDcpsContriVoucherDtlsId();

			inputMap.put("voucherVOToCheckThruSchdlr", lObjPreviousMstDcpsContriVoucherDtls);
		}

		inputMap.put("lLngContriVoucherIdToBePassed",lLngContriVoucherIdToBePassed);

		// Code added to insert data in TrnDcpsContribution

		if (lIntFreezeFlag == 1) {
			insertRegularContributions(inputMap);
			Double lDoubleVoucherAmount = 0d;

			if(inputMap.get("voucherAmount") != null)
			{
				lDoubleVoucherAmount = Double.parseDouble(inputMap.get("voucherAmount").toString().trim());
			}

			if (lObjPreviousMstDcpsContriVoucherDtls == null) {
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(1L);
				lObjMstDcpsContriVoucherDtls.setStatus('A');
				lObjMstDcpsContriVoucherDtls.setVoucherAmount(lDoubleVoucherAmount);
				lObjOfflineContriDAO.create(lObjMstDcpsContriVoucherDtls);
			}
			else
			{
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherStatus(1L);
				lObjPreviousMstDcpsContriVoucherDtls.setStatus('A');
				lObjPreviousMstDcpsContriVoucherDtls.setVoucherAmount(lDoubleVoucherAmount);
				lObjOfflineContriDAO.update(lObjPreviousMstDcpsContriVoucherDtls);
			}
			//lObjOfflineContriDAO.updateVoucherDetailsInMstContriOnApproval(lLngContriVoucherIdToBePassed,lDoubleVoucherAmount);
		}

		lBlFlag = true;

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
		.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		resObj.setResultCode(1);
		return resObj;
	}

	public ResultObject displaySchemeNameForCodeInMissingCredit(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrSchemeCode = null;
		List<MstScheme> lListSchemes = null;
		Integer lNoOfSchemes = 0;

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(MstDcpsContriVoucherDtls.class, serv.getSessionFactory());

			/* Gets SchemeCode from request */
			lStrSchemeCode = StringUtility.getParameter("txtSchemeCode", request).trim();

			/*
			 * Gets the all the schemeNames whose scheme code starts with given
			 * scheme code and finds total schemes
			 */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			String lStrDdoCode = null;
			// Below list of schemes does not depend on ddo code
			lListSchemes = lObjOfflineContriDAO.getSchemeNamesFromCodeInMissingCredit(lStrSchemeCode, lStrDdoCode);
			//lNoOfSchemes = lListSchemes.size();

			String lStrTempResult = null;
			if (lListSchemes != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lListSchemes,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is " + e, e);
			return resObj;
		}

		/*
		 * Generates XML response for all schemes found whose scheme code starts
		 * with given scheme code
		 */

	}

	private StringBuilder getResponseXMLDocToDisplaySchemeName(Integer lNoOfSchemes, List lListSchemes) {

		StringBuilder lStrBldXML = new StringBuilder();
		Object[] lObjSchemes = null;
		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<lNoOfSchemes>");
		lStrBldXML.append(lNoOfSchemes);
		lStrBldXML.append("</lNoOfSchemes>");

		for (int lInt = 0; lInt < lListSchemes.size(); lInt++) {
			lObjSchemes = (Object[]) lListSchemes.get(lInt);
			lStrBldXML.append("<SchemeName" + lInt + ">");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(lObjSchemes[1].toString().trim());
			lStrBldXML.append("]]>");

			lStrBldXML.append("</SchemeName" + lInt + ">");
			lStrBldXML.append("<SchemeCode" + lInt + ">");
			lStrBldXML.append(lObjSchemes[0].toString().trim());
			lStrBldXML.append("</SchemeCode" + lInt + ">");
		}

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
//Added by ashish for displaying voucher_no,date and contribution
	private StringBuilder getResponseXMLDocForOffline(Boolean flag,String voucherno,String voucherdate,Double amount,String status) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
	

		lStrBldXML.append("<VNO>");
		lStrBldXML.append(voucherno);
		lStrBldXML.append("</VNO>");

		lStrBldXML.append("<VDATE>");
		lStrBldXML.append(voucherdate);
		lStrBldXML.append("</VDATE>");

		lStrBldXML.append("<AMT>");
		lStrBldXML.append(amount);
		lStrBldXML.append("</AMT>");
		
		lStrBldXML.append("<STATUS>");
		lStrBldXML.append(status);
		lStrBldXML.append("</STATUS>");
		
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}

