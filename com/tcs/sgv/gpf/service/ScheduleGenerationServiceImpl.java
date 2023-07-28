/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 29, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import biometric.XMLConverter;

import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstEmpGpfAcc;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;
import com.tcs.sgv.gpf.valueobject.MstGpfMonthly;
import com.tcs.sgv.gpf.valueobject.MstGpfYearly;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Aug 29, 2011
 */
public class ScheduleGenerationServiceImpl extends ServiceImpl implements ScheduleGenerationService {
	Log glogger = LogFactory.getLog(getClass());
	private Date gDtCurDate = null; /* CURRENT DATE */
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
			gDtCurDate = SessionHelper.getCurDate();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			glogger.error("Error is :" + e, e);
		}
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.ScheduleGenerationService#loadScheduleGenerationForm
	 * (java.util.Map)
	 */
	public ResultObject loadScheduleGenerationForm(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrDdoCode = null;
		try {
			setSessionInfo(inputMap);
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serv
					.getSessionFactory());
			lStrDdoCode = lObjScheduleGenerationDAO.getDdoCodeForDDO(gLngPostId);

			List lLstMonths = lObjScheduleGenerationDAO.getMonths();
			inputMap.put("lLstMonths", lLstMonths);

			List lLstYears = lObjScheduleGenerationDAO.getYears();
			inputMap.put("lLstYears", lLstYears);

			// List lLstOffices =
			// lObjScheduleGenerationDAO.getCurrentOffices(lStrDdoCode);
			// inputMap.put("lLstOffices", lLstOffices);

			List lLstBillGroups = lObjScheduleGenerationDAO.getBillGroups(lStrDdoCode);
			inputMap.put("lLstBillGroup", lLstBillGroups);

			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFGenerateSchedule");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
		}
		return resObj;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.ScheduleGenerationService#generateGPFSchedule
	 * (java.util.Map)
	 */
	public ResultObject generateGPFSchedule(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstEmpMonthlySubs = null;
		List lLstAdvanceRecovery = null;
		Double lDblAdRecovery = 0d;
		String lStrAdvanceTrnId = null;
		Integer lIntInstNo = null;
		Integer lIntTotalInst = null;
		String lStrGpfAccNo = null;
		Double lDblRegularSubs = 0d;
		Double lDblAdvanceSanctioned = 0d;
		Double lDblPrePayOfAdvance = 0d;
		// Double lDblOpeningBal = null;
		// Double lDblClosingBal = null;
		Long lLngPreMonth = null;
		Long lLngPreYear = null;
		String lStrAlert = "true";
		Double lDblOpeningBal = 0d;
		Double lDblClosingBal = 0d;
		Boolean lBlFlag = false;
		Long lLngGpfMonthlyId = null;
		MstGpfMonthly lObjMstGpfMonthly = null;

		try {
			setSessionInfo(inputMap);
			Long lLngBillGroupId = Long.parseLong(StringUtility.getParameter("billGroupId", request));
			Long lLngMonthId = Long.parseLong(StringUtility.getParameter("monthId", request));
			Long lLngYearId = Long.parseLong(StringUtility.getParameter("yearId", request));
			// if (lLngMonthId <= 3) {
			// lLngYearId = lLngYearId + 1;
			// }

			// DAO Object to create the monthly VO
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());

			// check if the schedule for month-year is already generated
			if (!lObjScheduleGenerationDAO.isScheduleAlreadyGenerated(lLngBillGroupId, lLngMonthId, lLngYearId)) {
				if (lLngMonthId == 1) {
					lLngPreMonth = 12L;
					lLngPreYear = lLngYearId - 1;
				} else {
					lLngPreMonth = lLngMonthId - 1;
					lLngPreYear = lLngYearId;
				}
				// check the previous month schedule is approved
				if (lObjScheduleGenerationDAO.isScheduleAlreadyApproved(lLngBillGroupId, lLngPreMonth, lLngPreYear)) {

					// get the monthly subscription data for employees of given
					// billgroup
					lLstEmpMonthlySubs = lObjScheduleGenerationDAO.getEmpMonthlySubsData(lLngBillGroupId, lLngMonthId,
							lLngYearId);

					// loop through the employee list of the bill group
					if (lLstEmpMonthlySubs != null && lLstEmpMonthlySubs.size() > 0) {
						for (int i = 0; i < lLstEmpMonthlySubs.size(); i++) {
							Object[] lArrMonthlySubs = (Object[]) lLstEmpMonthlySubs.get(i);
							lStrGpfAccNo = lArrMonthlySubs[0].toString();
							lDblRegularSubs = (Double) lArrMonthlySubs[1];
							
							lObjMstGpfMonthly = lObjScheduleGenerationDAO.getMstGpfMonthlyObject(lStrGpfAccNo, lLngMonthId, lLngYearId);
							if(lObjMstGpfMonthly == null){
								lObjMstGpfMonthly = new MstGpfMonthly();
								lLngGpfMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_MONTHLY", inputMap);
								lObjMstGpfMonthly.setMstGpfMonthlyId(lLngGpfMonthlyId);
								lObjMstGpfMonthly.setRegularSubscription(lDblRegularSubs);
								lObjMstGpfMonthly.setBillgroupId(lLngBillGroupId);
								lObjMstGpfMonthly.setGpfAccNo(lStrGpfAccNo);
								lObjMstGpfMonthly.setMonthId(lLngMonthId);
								lObjMstGpfMonthly.setFinYearId(lLngYearId);								
							}
							
							// get the data of advance recovery for the employee
							lLstAdvanceRecovery = lObjScheduleGenerationDAO.getEmpAdvanceRecovery(lStrGpfAccNo);
							if (lLstAdvanceRecovery != null && lLstAdvanceRecovery.size() > 0) {
								Object[] lArrObjReco = (Object[]) lLstAdvanceRecovery.get(0);
								lStrAdvanceTrnId = (String) lArrObjReco[3];
								lDblAdRecovery = (Double) lArrObjReco[2];
								lIntInstNo = (Integer) lArrObjReco[0];
								lIntTotalInst = (Integer) lArrObjReco[4];
								lObjMstGpfMonthly.setAdvanceTrnId(lStrAdvanceTrnId);
								lObjMstGpfMonthly.setInstNo(lIntInstNo);
								lObjMstGpfMonthly.setTotalInst(lIntTotalInst);
							}
							// if there is no active advance recovery, set it to
							// 0
							else {
								lDblAdRecovery = 0d;
							}
							
							if(lObjMstGpfMonthly.getAdvanceRecovery() != null){
								lObjMstGpfMonthly.setAdvanceRecovery(lDblAdRecovery);
							}
							
							lObjMstGpfMonthly.setStatus("P");
							
							lDblOpeningBal = lObjScheduleGenerationDAO.getOpeningBalForCurrMonth(lStrGpfAccNo,
									lLngMonthId, lLngYearId);
							lObjMstGpfMonthly.setOpeningBalance(lDblOpeningBal);

							String lStrFinYear = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(lLngYearId);
							lDblAdvanceSanctioned = lObjScheduleGenerationDAO.getAdvanceSanctionedForMonth(
									lStrGpfAccNo, lLngMonthId.intValue(), lStrFinYear);
							lObjMstGpfMonthly.setAdvanceSanctioned(lDblAdvanceSanctioned);
							lDblPrePayOfAdvance = lObjScheduleGenerationDAO.getChallanPaidForMonth(lStrGpfAccNo,
									lLngMonthId.intValue(), lStrFinYear, 800044L);
							lObjMstGpfMonthly.setPrePayOfAdvance(lDblPrePayOfAdvance);
							Double lDblExcessPay = lObjScheduleGenerationDAO.getChallanPaidForMonth(lStrGpfAccNo,
									lLngMonthId.intValue(), lStrFinYear, 800046L);
							lObjMstGpfMonthly.setExcessPayment(lDblExcessPay);
							Double lDblDeputationChallan = lObjScheduleGenerationDAO.getChallanPaidForMonth(
									lStrGpfAccNo, lLngMonthId.intValue(), lStrFinYear, 800045L);
							lObjMstGpfMonthly.setDeputationChallan(lDblDeputationChallan);
							lDblClosingBal = lDblOpeningBal + lDblRegularSubs + lDblAdRecovery - lDblAdvanceSanctioned
									+ lDblPrePayOfAdvance + lDblDeputationChallan;

							lObjMstGpfMonthly.setClosingBalance(lDblClosingBal);
							Double lDblArrearAmount = lObjScheduleGenerationDAO.getArrearsDetails(lStrGpfAccNo,
									lLngMonthId, lLngYearId);
							lObjMstGpfMonthly.setArrearAmount(lDblArrearAmount);
							lObjMstGpfMonthly.setCreatedDate(gDtCurDate);
							lObjMstGpfMonthly.setCreatedPostId(gLngPostId);
							lObjMstGpfMonthly.setCreatedUserId(gLngUserId);
							lObjScheduleGenerationDAO.create(lObjMstGpfMonthly);

						}
					}
				}
				// if previous month schedule is not yet generated/approved, set
				// the alert String
				else {
					lStrAlert = "prePending";
				}
			}
			// if schedule for the given month-year is already
			// generated/approved, set the alert String
			else {
				lStrAlert = "alreadyGenerated";
			}
			lBlFlag = true;

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, ex, "Error is: ");
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag, lStrAlert).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Boolean flag, String lStrAlert) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<Alert>");
		lStrBldXML.append(lStrAlert);
		lStrBldXML.append("</Alert>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	/**
	 * @param inputMap
	 * @return
	 */
	public ResultObject loadDraftSchedule(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		List lLstMonthlySchedule = null;

		try {
			setSessionInfo(inputMap);

			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, serv.getSessionFactory());
			Integer lIntCurrFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();

			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			lStrDdoCode = lObjScheduleGenerationDAO.getDdoCodeForDDO(gLngPostId);

			// get the pending schedules of bill groups under given DDO
			lLstMonthlySchedule = lObjScheduleGenerationDAO.getDraftScheduleForBillGroups(lStrDdoCode,
					lIntCurrFinYearId.longValue());
			inputMap.put("MonthlyData", lLstMonthlySchedule);

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, ex, "Error is: ");
			return resObj;
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("GPFDraftSchedule");
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.ScheduleGenerationService#approveGPFSchedule(
	 * java.util.Map)
	 */
	public ResultObject approveGPFSchedule(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Long lLngBillgroupId = null;
		Long lLngMonthId = null;
		Long lLngFinYearId = null;
		String lStrGpfAccNo = null;
		Double lDblRegularSubs = null;

		Boolean lBlFlag = false;

		try {
			setSessionInfo(inputMap);
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			String lStrBillgroupIds = StringUtility.getParameter("billGroupIds", request);
			String lStrMonthIds = StringUtility.getParameter("monthId", request);
			String lStrFinYearIds = StringUtility.getParameter("finYearIds", request);
			String lStrVoucherNos = StringUtility.getParameter("voucherNos", request);
			String lStrVoucherDates = StringUtility.getParameter("voucherDates", request);
			String[] strArrBillGroup = lStrBillgroupIds.split("~");
			String[] strArrMonth = lStrMonthIds.split("~");
			String[] strArrFinYear = lStrFinYearIds.split("~");
			String[] strArrVoucherNo = lStrVoucherNos.split("~");
			String[] strArrVoucherDate = lStrVoucherDates.split("~");
			String lStrVoucherNo = "";
			String lStrVoucherDate = "";
			List lLstMonthlyList = null;
			// loop through the bill group-month-year selected for approval
			for (int index = 0; index < strArrBillGroup.length; index++) {

				lLngBillgroupId = Long.parseLong(strArrBillGroup[index]);
				lLngMonthId = Long.parseLong(strArrMonth[index]);
				lLngFinYearId = Long.parseLong(strArrFinYear[index]);
				MstGpfMonthly lObjMstGpfMonthly = null;

				// get the schedules(employee list) for the billgroup
				lLstMonthlyList = lObjScheduleGenerationDAO.getPendingSchedule(lLngBillgroupId, lLngMonthId,
						lLngFinYearId);
				if (lLstMonthlyList != null && lLstMonthlyList.size() > 0) {

					// loop through the employee schedule list
					for (int i = 0; i < lLstMonthlyList.size(); i++) {
						lObjMstGpfMonthly = (MstGpfMonthly) lLstMonthlyList.get(i);
						lStrVoucherNo = strArrVoucherNo[index];
						lStrVoucherDate = strArrVoucherDate[index];
						lLngFinYearId = Long.parseLong(strArrFinYear[index]);
						if (lStrVoucherNo != null) {
							lObjMstGpfMonthly.setVoucherNo(lStrVoucherNo);
						}
						if (lStrVoucherDate != null && !lStrVoucherDate.equals("")) {
							lObjMstGpfMonthly.setVoucherDate(lObjDateFormat.parse(lStrVoucherDate));
						}
						lObjMstGpfMonthly.setStatus("A");
						lObjMstGpfMonthly.setUpdatedUserId(gLngUserId);
						lObjMstGpfMonthly.setUpdatedPostId(gLngPostId);
						lObjMstGpfMonthly.setUpdatedDate(gDtCurDate);
						lObjScheduleGenerationDAO.update(lObjMstGpfMonthly);

						lStrGpfAccNo = lObjMstGpfMonthly.getGpfAccNo();

						// new record in gpf yearly table if its a financial
						// year end
						if (lLngMonthId == 3) {
							lLngFinYearId = lLngFinYearId - 1;
							ScheduleGenerationDAO lObjScheduleGenerationDAOForYearly = new ScheduleGenerationDAOImpl(
									MstGpfYearly.class, serv.getSessionFactory());
							GPFRequestProcessDAO lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(null, serv
									.getSessionFactory());
							MstGpfYearly lObjMstGpfYearly = new MstGpfYearly();
							Long lLngGpfYearlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_YEARLY", inputMap);
							lObjMstGpfYearly.setMstGpfYearlyId(lLngGpfYearlyId);
							lObjMstGpfYearly.setGpfAccNo(lStrGpfAccNo);
							lObjMstGpfYearly.setFinYearId(lLngFinYearId);
							Double lDblOpeningBal = lObjGPFRequestProcessDAO.getOpeningBalForCurrYear(lStrGpfAccNo,
									lLngFinYearId);
							lObjMstGpfYearly.setOpeningBalance(lDblOpeningBal);
							List lLstYearlyData = lObjGPFRequestProcessDAO.getGPFAccountBalance(lStrGpfAccNo,
									lLngFinYearId);
							List lLstAdvanceHistory = lObjGPFRequestProcessDAO.getAdvanceHistory(lStrGpfAccNo,
									lLngFinYearId);
							Double lDblRegularSubscription = 0d;
							Double lDblAdvanceRecovery = 0d;
							Double lDblDeputationChallan = 0d;
							Double lDblPrePayOfAdvance = 0d;
							Double lDblExcessPay = 0d;
							Double lDblAdvanceSanctioned = 0d;
							Double lDblWithdrawalSanctioned = 0d;
							if (lLstYearlyData != null && !lLstYearlyData.isEmpty()) {
								Object[] lObjArrYearly = (Object[]) lLstYearlyData.get(0);
								lDblRegularSubscription = (Double) lObjArrYearly[0];
								lDblAdvanceRecovery = (Double) lObjArrYearly[1];
								lDblDeputationChallan = (Double) lObjArrYearly[2];
								lDblPrePayOfAdvance = (Double) lObjArrYearly[3];
								lDblExcessPay = (Double) lObjArrYearly[4];

								lObjMstGpfYearly.setRegularSubscription(lDblRegularSubscription);
								lObjMstGpfYearly.setAdvanceRecovery(lDblAdvanceRecovery);
								lObjMstGpfYearly.setDeputationChallan(lDblDeputationChallan);
								lObjMstGpfYearly.setPrePayOfAdvance(lDblPrePayOfAdvance);
								lObjMstGpfYearly.setExcessPayment(lDblExcessPay);
							}
							if (lLstAdvanceHistory != null && !lLstAdvanceHistory.isEmpty()) {
								Object[] lObjArrAdvance = (Object[]) lLstAdvanceHistory.get(0);

								if (lObjArrAdvance[0].equals("RA")) {
									lDblAdvanceSanctioned = (Double) lObjArrAdvance[1];
								} else {
									lDblWithdrawalSanctioned = (Double) lObjArrAdvance[1];
								}
								if (lLstAdvanceHistory.size() > 1) {
									lObjArrAdvance = (Object[]) lLstAdvanceHistory.get(1);
									lDblAdvanceSanctioned = (Double) lObjArrAdvance[1];
								}
								lObjMstGpfYearly.setAdvanceSanctioned(lDblAdvanceSanctioned);
								lObjMstGpfYearly.setWithdrawalSanctioned(lDblWithdrawalSanctioned);
							}
							lObjMstGpfYearly.setClosingBalance(lDblOpeningBal + lDblRegularSubscription
									+ lDblAdvanceRecovery - lDblAdvanceSanctioned - lDblWithdrawalSanctioned);
							lObjScheduleGenerationDAOForYearly.create(lObjMstGpfYearly);
						}

						// update monthly subscription in MstEmpGpfAcc
						lDblRegularSubs = lObjMstGpfMonthly.getRegularSubscription();
						GPFAdvanceProcessDAO gpfAdvanceProcessDAOForGpfBal = new GPFAdvanceProcessDAOImpl(
								MstEmpGpfAcc.class, serv.getSessionFactory());
						MstEmpGpfAcc lObjMstEmpGpfAcc = (MstEmpGpfAcc) gpfAdvanceProcessDAOForGpfBal
								.getGPFAccountObjectForAccountNo(lStrGpfAccNo);
						lObjMstEmpGpfAcc.setMonthlySubscription(lDblRegularSubs);
						gpfAdvanceProcessDAOForGpfBal.update(lObjMstEmpGpfAcc);

						// update mst_gpf_advance installment details
						String lStrAdvanceTrnId = lObjMstGpfMonthly.getAdvanceTrnId();
						Double lDblAdvanceRecovery = lObjMstGpfMonthly.getAdvanceRecovery();
						if (lStrAdvanceTrnId != null) {
							GPFAdvanceProcessDAO lObjGPFAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(
									MstGpfAdvance.class, serv.getSessionFactory());
							Long lLngAdvanceId = lObjGPFAdvanceProcessDAO
									.getMstAdvanceIdForTransactionId(lStrAdvanceTrnId);
							MstGpfAdvance lObjMstGpfAdvance = (MstGpfAdvance) lObjGPFAdvanceProcessDAO
									.read(lLngAdvanceId);
							Integer lIntInstallmentsLeft = lObjMstGpfAdvance.getInstallmentsLeft() - 1;
							lObjMstGpfAdvance.setInstallmentsLeft(lIntInstallmentsLeft);
							if (lIntInstallmentsLeft == 0) {
								lObjMstGpfAdvance.setRecoveryStatus(1);
							}
							Double lDblRecoveredAmount = lObjMstGpfAdvance.getRecoveredAmount() + lDblAdvanceRecovery;
							Double lDblOutstandingAmount = lObjMstGpfAdvance.getOutstandingAmount()
									- lDblAdvanceRecovery;
							lObjMstGpfAdvance.setRecoveredAmount(lDblRecoveredAmount);
							lObjMstGpfAdvance.setOutstandingAmount(lDblOutstandingAmount);
							lObjGPFAdvanceProcessDAO.update(lObjMstGpfAdvance);
						}
					}
				}
				// // insert data in payroll module
				//
				// inputMap.put("billgroupId", lLngBillgroupId);
				// inputMap.put("monthId", lLngMonthId);
				// inputMap.put("yearId", lLngFinYearId);
				// inputMap.put("typeId", 36);
				//
				// lLstMonthlyListForPayroll =
				// lObjScheduleGenerationDAO.getMonthlyEmpListForPayroll(lLngBillgroupId,
				// lLngMonthId, lLngFinYearId);
				// inputMap.put("empList", lLstMonthlyListForPayroll);
				//
				// serv.executeService("", inputMap);
				//
				// // end of insertion in payroll
			}

			lBlFlag = true;

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, ex, "Error is: ");
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.ScheduleGenerationService#discardGPFSchedule(
	 * java.util.Map)
	 */
	public ResultObject discardGPFSchedule(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;

		try {
			setSessionInfo(inputMap);
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			String lStrBillgroupIds = StringUtility.getParameter("billGroupIds", request);
			String lStrMonthIds = StringUtility.getParameter("monthId", request);
			String lStrFinYearIds = StringUtility.getParameter("finYearIds", request);
			String[] strArrBillGroup = lStrBillgroupIds.split("~");
			String[] strArrMonth = lStrMonthIds.split("~");
			String[] strArrFinYear = lStrFinYearIds.split("~");
			List lLstMonthlyList = null;

			// loop through the selected Bill groups
			for (int index = 0; index < strArrBillGroup.length; index++) {

				Long lLngBillgroupId = Long.parseLong(strArrBillGroup[index]);
				Long lLngMonthId = Long.parseLong(strArrMonth[index]);
				Long lLngFinYearId = Long.parseLong(strArrFinYear[index]);
				MstGpfMonthly lObjMstGpfMonthly = null;

				// get the employee schedule list for the bill group
				lLstMonthlyList = lObjScheduleGenerationDAO.getPendingSchedule(lLngBillgroupId, lLngMonthId,
						lLngFinYearId);
				if (lLstMonthlyList != null && !lLstMonthlyList.isEmpty()) {
					for (int i = 0; i < lLstMonthlyList.size(); i++) {
						lObjMstGpfMonthly = (MstGpfMonthly) lLstMonthlyList.get(i);

						lObjMstGpfMonthly.setStatus("D");
						lObjMstGpfMonthly.setUpdatedUserId(gLngUserId);
						lObjMstGpfMonthly.setUpdatedPostId(gLngPostId);
						lObjMstGpfMonthly.setUpdatedDate(gDtCurDate);

						lObjScheduleGenerationDAO.update(lObjMstGpfMonthly);

					}
				}
			}

			lBlFlag = true;

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, ex, "Error is: ");
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
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

	/**
	 * service method to get the discarded schedules
	 * 
	 * @param inputMap
	 * @return
	 */
	public ResultObject getDiscardedSchedules(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		List lLstDiscardedSchedule = null;

		try {
			setSessionInfo(inputMap);

			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, serv.getSessionFactory());
			Integer lIntCurrFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();

			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			lStrDdoCode = lObjScheduleGenerationDAO.getDdoCodeForDDO(gLngPostId);
			lLstDiscardedSchedule = lObjScheduleGenerationDAO.getDiscardedScheduleForBillGroups(lStrDdoCode,
					lIntCurrFinYearId.longValue());
			inputMap.put("lLstDiscardedSchedule", lLstDiscardedSchedule);
			XMLConverter xmlConverterObj = new XMLConverter();
			String result = xmlConverterObj.objectToXML(lLstDiscardedSchedule);
			List empdtls = (List) xmlConverterObj.XMLToObject(result);
			System.out.println(result);
			Object[] lObjArr = (Object[]) empdtls.get(0);
			for (Integer i = 0; i < lObjArr.length; i++) {
				System.out.println(lObjArr[i]);
			}

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, ex, "Error is: ");
			return resObj;
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("GPFDiscardedSchedules");
		return resObj;
	}

	public ResultObject updateGPFDataOnPaybillApproval(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		// Long lLngBillGroupId = 0L;
		Integer lIntYearId = null;
		Integer lIntMonthId = null;
		Long lLngYearId = null;
		Long lLngMonthId = null;
		List lLstBillData = null;
		MstGpfMonthly lObjMstGpfMonthly = null;
		Object[] lObjEmpData = null;
		Long lLngVoucherNo = null;
		String lStrVoucherDate = "";
		Date lDtVoucherDate = null;
		Long lLngBillGroupId = null;
		Long lLngMstGpfMonthlyId = null;
		
		try {
			setSessionInfo(inputMap);
			
			lLngBillGroupId = (Long) inputMap.get("billGroupId");
			//lIntYearId = (Integer) inputMap.get("yearId");
			lIntYearId = (Integer) inputMap.get("cmbYear");			
			//lLngMonthId = (Long) inputMap.get("monthId");
			lIntMonthId = (Integer) inputMap.get("cmbMonth");
			lLstBillData = (List) inputMap.get("lstDeductionData");
			
			
			lLngVoucherNo = (Long) inputMap.get("txtVoucherNo");
			lStrVoucherDate = (String) inputMap.get("txtVoucherDt");
			
			java.text.SimpleDateFormat lObjDateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
			lDtVoucherDate = lObjDateFormat.parse(lStrVoucherDate);
			
			// Map lLstGPFBillData = (Map)inputMap.get("billData");
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMonthly.class, serv
					.getSessionFactory());
			
			if(lIntYearId != null && lIntYearId >= 0){
				lLngYearId = lObjScheduleGenerationDAO.getFinyearFromYearCode(lIntYearId.toString());
			}
			
			if(lIntMonthId != null){
				lLngMonthId = Long.parseLong(lIntMonthId.toString());
			}
			
			for (Integer index = 0; index < lLstBillData.size(); index++) {
				lObjEmpData = (Object[]) lLstBillData.get(index);
				lObjMstGpfMonthly = lObjScheduleGenerationDAO.getMstGpfMonthlyObject((String) lObjEmpData[0],
						lLngMonthId, lLngYearId);
				if(lObjMstGpfMonthly == null){
					lObjMstGpfMonthly = new MstGpfMonthly();
					lLngMstGpfMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_MONTHLY", inputMap);
					lObjMstGpfMonthly.setMstGpfMonthlyId(lLngMstGpfMonthlyId);
				}
				lObjMstGpfMonthly.setRegularSubscription(Double.parseDouble(lObjEmpData[1].toString()));
				lObjMstGpfMonthly.setAdvanceRecovery(Double.parseDouble(lObjEmpData[2].toString()));			
				lObjMstGpfMonthly.setVoucherNo(lLngVoucherNo.toString());				
				lObjMstGpfMonthly.setVoucherDate(lDtVoucherDate);
				lObjMstGpfMonthly.setBillgroupId(lLngBillGroupId);
				lObjMstGpfMonthly.setPaybillGenerated(1);
				lObjScheduleGenerationDAO.update(lObjMstGpfMonthly);
			}

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, ex, "Error is: ");
			return resObj;
		}
		resObj.setResultValue(inputMap);
		return resObj;
	}
}
