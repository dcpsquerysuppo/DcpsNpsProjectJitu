package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO;
import com.tcs.sgv.gpf.dao.GPFInterestCalculationDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfArrearsYearly;
import com.tcs.sgv.gpf.valueobject.MstGpfMissingCredit;
import com.tcs.sgv.gpf.valueobject.MstGpfMonthly;
import com.tcs.sgv.gpf.valueobject.MstGpfYearly;

public class GPFInterestCalculationServiceImpl extends ServiceImpl implements GPFInterestCalculationService {
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

	/* Global Variable for User Location */
	String gStrUserLocation = null;
	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/gpf/GPFConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.GPFInterestCalculationService#loadInterestCal
	 * (java.util.Map)
	 */
	public ResultObject loadInterestCal(Map inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			GPFInterestCalculationDAO lObjInterestCalculationDAO = new GPFInterestCalculationDAOImpl(DdoOffice.class,
					serv.getSessionFactory());
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, serv.getSessionFactory());
			Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();
			String lStrFinYear = lObjCommonDAO.getFinYearCodeForYearId(lIntFinYearId.longValue());
			String lStrDdoCode = lObjCommonDAO.getDdoCodeForDDO(gLngPostId);
			List lOfficeList = lObjInterestCalculationDAO.getOfficeDtls(lStrDdoCode);
			List lstGroup = IFMSCommonServiceImpl.getLookupValues("GroupList", SessionHelper.getLangId(inputMap),
					inputMap);
			List lLstYear = lObjCommonDAO.getFinyears();
			inputMap.put("OfficeName", lOfficeList);
			inputMap.put("groupList", lstGroup);
			inputMap.put("yearList", lLstYear);
			inputMap.put("CurFinYear", lStrFinYear);
			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFInterestCalculation");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.gpf.service.GPFInterestCalculationService#
	 * popUpEmpDtlsForInterestCal(java.util.Map)
	 */
	public ResultObject popUpEmpDtlsForInterestCal(Map inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		Date lDtJavaCurrDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Double lDblRegularSubscription = 0d;
		Double lDblAdvanceRecovery = 0d;
		Double lDblAdvanceSanctioned = 0d;
		Double lDblWithdrawalSanctioned = 0d;
		Double lDblPrePayOfAdvance = 0d;
		Double lDblDeputationChallan = 0d;
		Double lDblMissingCredits = 0d;
		try {
			String lStrSevaarthID = StringUtility.getParameter("sevaarthID", request);

			GPFInterestCalculationDAO lObjInterestCalculationDAO = new GPFInterestCalculationDAOImpl(null, serv
					.getSessionFactory());

			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serv
					.getSessionFactory());
			String lStrDdoCode = lObjScheduleGenerationDAO.getDdoCodeForDDO(gLngPostId);
			List lEmpDtlsList = lObjInterestCalculationDAO.getEmpDtls(lStrSevaarthID, lStrDdoCode);
			String lStrEmpName = "";
			String lStrGpfAccNo = "";
			String lStrOfficeName = "";
			Double lDblCurrBalance = 0d;
			Float lFltInterestRate = 0f;
			if (lEmpDtlsList != null && lEmpDtlsList.size() > 0) {
				Iterator it = lEmpDtlsList.iterator();
				Object lObj[] = new Object[lEmpDtlsList.size()];
				while (it.hasNext()) {
					lObj = (Object[]) it.next();
				}
				if (lObj[0] != null) {
					lStrEmpName = (String) lObj[0];
				}
				if (lObj[1] != null) {
					lStrGpfAccNo = (String) lObj[1];
				}
				if (lObj[2] != null) {
					lStrOfficeName = (String) lObj[2];
				}
				if (lObj[3] != null) {
					lDblCurrBalance = (Double) lObj[3];
				}
			}
			GPFRequestProcessDAO lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());
			Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearId(sdf.format(lDtJavaCurrDate));
			Long lLngYearId = lIntFinYearId.longValue();
			lDblMissingCredits = lObjInterestCalculationDAO.getMissingCreditsPreviousYears(lStrGpfAccNo, lLngYearId);
			Double lDblOpeningBal = lObjGPFRequestProcessDAO.getOpeningBalForCurrYear(lStrGpfAccNo, lLngYearId);
			lDblOpeningBal = lDblOpeningBal + lDblMissingCredits;
			List lLstYearlyData = lObjGPFRequestProcessDAO.getGPFAccountBalance(lStrGpfAccNo, lLngYearId);
			List lLstAdvanceHistory = lObjGPFRequestProcessDAO.getAdvanceHistory(lStrGpfAccNo, lLngYearId);

			if (lLstYearlyData != null && lLstYearlyData.size() > 0) {
				Object[] lObjArrYearly = (Object[]) lLstYearlyData.get(0);
				lDblRegularSubscription = (Double) lObjArrYearly[0];
				lDblAdvanceRecovery = (Double) lObjArrYearly[1];
				lDblPrePayOfAdvance = (Double) lObjArrYearly[3];
				lDblDeputationChallan = (Double) lObjArrYearly[2];
			}
			if (lLstAdvanceHistory != null && lLstAdvanceHistory.size() > 0) {
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
			}
			lDblCurrBalance = lDblOpeningBal + lDblRegularSubscription + lDblAdvanceRecovery - lDblAdvanceSanctioned
					- lDblWithdrawalSanctioned + lDblPrePayOfAdvance + lDblDeputationChallan;
			lFltInterestRate = lObjInterestCalculationDAO.getCurrentInterestRateForEmp(lStrSevaarthID,
					lObjSqlDateFormat.format(lDtJavaCurrDate));
			lBlFlag = true;
			String lSBStatus = getResponseXMLDocForEmpDtls(lBlFlag, lStrEmpName, lStrGpfAccNo, lStrOfficeName,
					lDblCurrBalance, lFltInterestRate).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.gpf.service.GPFInterestCalculationService#
	 * popUpEmpDtlsUsingOfficeName(java.util.Map)
	 */
	public ResultObject popUpEmpDtlsUsingOfficeName(Map inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String lStrFlag = "";
		try {
			String lStrOfficeId = StringUtility.getParameter("officeId", request);
			String lStrGroupId = StringUtility.getParameter("groupId", request);
			String lStrFinancialYear = StringUtility.getParameter("financialYear", request);
			lStrFinancialYear = lStrFinancialYear.substring(0, 4);
			// String lStrInterestRate =
			// StringUtility.getParameter("interestRate", request);
			Float lFltInterestRate = 0f;
			Long lLngGroupId = null;
			// Date lDtToDate = null;
			GPFInterestCalculationDAO lObjInterestCalculationDAO = new GPFInterestCalculationDAOImpl(null, serv
					.getSessionFactory());
			if (!"".equals(lStrGroupId.trim())) {
				lLngGroupId = Long.parseLong(lStrGroupId);
			}
			// if (!"".equals(lStrInterestRate.trim())) {
			// lFltInterestRate = Float.parseFloat(lStrInterestRate);
			// }
			// if (!"".equals(lStrFromDate.trim())) {
			// lDtSqlFromDt = new
			// java.sql.Date(lObjDateFormat.parse(lStrFromDate).getTime());
			// }
			// if (!"".equals(lStrFromDate.trim())) {
			// lDtToDate = lObjDateFormat.parse(lStrToDate);
			// }

			// Calendar startDate = Calendar.getInstance();
			// startDate.setTime(lDtFromDate);
			// Calendar endDate = Calendar.getInstance();
			// endDate.setTime(lDtToDate);

			// Long dateDiff = Math.abs(startDate.getTimeInMillis() -
			// endDate.getTimeInMillis());
			// Integer lLngTotalDays = Math.round((dateDiff / (24 * 60 * 60 *
			// 1000)) + 1);
			// Double lLngYear = lLngTotalDays.doubleValue() / 365;
			Integer lIntFinYearId = lObjInterestCalculationDAO.getFinYearIdByFinCode(lStrFinancialYear).intValue();
			Long lLngYearId = lIntFinYearId.longValue();
			lFltInterestRate = lObjInterestCalculationDAO.getInterestRateForGroup(lLngGroupId, lStrFinancialYear);
			List lEmpDtls = lObjInterestCalculationDAO.getEmpDtlsUsingOfficeId(lStrOfficeId, lLngGroupId, lIntFinYearId
					.longValue(), lFltInterestRate);
			Double lDblRegularSubscription = 0d;
			Double lDblAdvanceRecovery = 0d;
			Double lDblAdvanceSanctioned = 0d;
			Double lDblWithdrawalSanctioned = 0d;
			Double lDblPrePayOfAdvance = 0d;
			Double lDblDeputationChallan = 0d;
			Double lDblCurrBalance = 0d;
			Double lDblMissingCredits = 0d;
			if (lEmpDtls != null && lEmpDtls.size() > 0) {
				Object[] lArrObj = null;
				GPFRequestProcessDAO lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(null, serv
						.getSessionFactory());
				String lStrGpfAccNo = null;
				for (int index = 0; index < lEmpDtls.size(); index++) {
					lDblAdvanceSanctioned = 0d;
					lDblWithdrawalSanctioned = 0d;
					lDblRegularSubscription = 0d;
					lDblAdvanceRecovery = 0d;
					lDblPrePayOfAdvance = 0d;
					lDblDeputationChallan = 0d;

					lArrObj = (Object[]) lEmpDtls.get(index);
					lStrGpfAccNo = (String) lArrObj[2];
					Double lDblOpeningBal = lObjGPFRequestProcessDAO.getOpeningBalForCurrYear(lStrGpfAccNo, lLngYearId);
					lDblMissingCredits = lObjInterestCalculationDAO.getMissingCreditsPreviousYears(lStrGpfAccNo, lLngYearId);
					List lLstYearlyData = lObjGPFRequestProcessDAO.getGPFAccountBalance(lStrGpfAccNo, lLngYearId);
					List lLstAdvanceHistory = lObjGPFRequestProcessDAO.getAdvanceHistory(lStrGpfAccNo, lLngYearId);
					
					lDblOpeningBal = lDblOpeningBal + lDblMissingCredits;

					if (lLstYearlyData != null && lLstYearlyData.size() > 0) {
						Object[] lObjArrYearly = (Object[]) lLstYearlyData.get(0);
						lDblRegularSubscription = (Double) lObjArrYearly[0];
						lDblAdvanceRecovery = (Double) lObjArrYearly[1];
						lDblPrePayOfAdvance = (Double) lObjArrYearly[3];
						lDblDeputationChallan = (Double) lObjArrYearly[2];
					}
					if (lLstAdvanceHistory != null && lLstAdvanceHistory.size() > 0) {
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
					}
					lDblCurrBalance = lDblOpeningBal + lDblRegularSubscription + lDblAdvanceRecovery
							- lDblAdvanceSanctioned - lDblWithdrawalSanctioned + lDblPrePayOfAdvance
							+ lDblDeputationChallan;
					lArrObj[4] = lDblCurrBalance;
				}
				lStrFlag = "true";
			} else {
				lStrFlag = "schedule";
			}
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			String lStrDdoCode = lObjCommonDAO.getDdoCodeForDDO(gLngPostId);

			String lStrFinYear = StringUtility.getParameter("financialYear", request);
			List lOfficeList = lObjInterestCalculationDAO.getOfficeDtls(lStrDdoCode);
			List lstGroup = IFMSCommonServiceImpl.getLookupValues("GroupList", SessionHelper.getLangId(inputMap),
					inputMap);
			List lLstYears = lObjCommonDAO.getFinyears();
			inputMap.put("OfficeName", lOfficeList);
			inputMap.put("groupList", lstGroup);
			// inputMap.put("Year", lLngYear);
			inputMap.put("InterestRate", lFltInterestRate);
			inputMap.put("EmpDtls", lEmpDtls);
			inputMap.put("OfficeId", Long.parseLong(lStrOfficeId));
			inputMap.put("GroupId", lLngGroupId);
			inputMap.put("finYear", lStrFinYear);
			inputMap.put("yearList", lLstYears);
			inputMap.put("lStrFlag", lStrFlag);

			/*
			 * lSBStatus = getResponseXMLDoc(lStrFlag).toString();
			 * 
			 * String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
			 * lSBStatus).toString();
			 * 
			 * inputMap.put("ajaxKey", lStrResult);
			 * resObj.setResultValue(inputMap); resObj.setViewName("ajaxData");
			 */
			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFInterestCalculation");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.GPFInterestCalculationService#popUpInterestRate
	 * (java.util.Map)
	 */
	public ResultObject popUpInterestRate(Map inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long lLngGroupId = Long.parseLong(gObjRsrcBndle.getString("GPF.GROUPD"));
		Float lFltInterestRate = 0f;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Integer lIntFinYearId = 0;
		Double lDblMonthlyTotal = 0d;
		Double lDblAmtAfterInterest = 0d;
		String lStrCurrDate = lObjSqlDateFormat.format(new Date());
		String lStrSqlFromDate = null;

		try {
			String lStrGroupId = StringUtility.getParameter("groupId", request);
			String lStrFromDate = StringUtility.getParameter("effectFromDate", request);
			String lStrGpfAccNo = StringUtility.getParameter("gpfAccNo", request);

			if (!lStrGroupId.equals("") && !lStrGroupId.equals(null)) {
				lLngGroupId = Long.parseLong(StringUtility.getParameter("groupId", request));
			}
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());
			GPFInterestCalculationDAO lObjInterestCalculationDAO = new GPFInterestCalculationDAOImpl(null, serv
					.getSessionFactory());
			if (!lStrFromDate.equals("") && !lStrFromDate.equals(null)) {
				lStrSqlFromDate = lObjSqlDateFormat.format(lObjDateFormat.parse(lStrFromDate));
				lIntFinYearId = lObjFinancialYearDAO.getFinYearId(lStrFromDate);
				lFltInterestRate = lObjInterestCalculationDAO.getInterestRateForSingleEmp(lLngGroupId, lStrSqlFromDate);
				lDblMonthlyTotal = lObjInterestCalculationDAO.getMonthlyTotalForInterestCalc(lStrGpfAccNo,
						lIntFinYearId.longValue());
				lDblAmtAfterInterest = lDblMonthlyTotal * lFltInterestRate / 100;
			} else {
				lStrCurrDate = lStrCurrDate.substring(0, 4);
				lFltInterestRate = lObjInterestCalculationDAO.getInterestRateForGroup(lLngGroupId, lStrCurrDate);
				lIntFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();

				lDblAmtAfterInterest = lDblMonthlyTotal * lFltInterestRate / 1200;
			}

			String lSBStatus = getResponseXMLDocForInterestRate(lFltInterestRate, lDblAmtAfterInterest).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.GPFInterestCalculationService#approveInterestCase
	 * (java.util.Map)
	 */
	public ResultObject approveInterestCase(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		String lStrScheduleStatus = "";
		String lStrApprove = "";

		try {
			setSessionInfo(inputMap);
			String lStrReqType = StringUtility.getParameter("reqType", request);
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());
			GPFInterestCalculationDAO lObjInterestCalculationDAO = new GPFInterestCalculationDAOImpl(
					MstGpfYearly.class, serv.getSessionFactory());
			GPFRequestProcessDAO lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(MstGpfMissingCredit.class, serv.getSessionFactory());
			MstGpfYearly lObjMstGpfYearly = null;
			MstGpfArrearsYearly lObjMstGpfArrearsYearly = null;
			Double lDblArrearAmount = 0d;
			Double lDblOpenBalance = 0d;
			Double lDblArrearInterest = 0d;
			Double lDblArrearBalance = 0d;
			Long lLngScheduleCount = 0l;
			List lLstMissingCredit = null;
			Long lLngMissingCrdtId = null;
			MstGpfMissingCredit lObjMstGpfMissingCredit = null;
			
			if (lStrReqType.equals("multiple")) {
				String lStrGpfAccNo = StringUtility.getParameter("gpfAccNo", request);
				String lStrYearlyId = StringUtility.getParameter("yearlyId", request);
				String lStrFinalAmount = StringUtility.getParameter("amtAfterInterest", request);
				String lStrFinancialYear = StringUtility.getParameter("financialYear", request);
				lStrFinancialYear = lStrFinancialYear.substring(0, 4);
				String lStrInterestRate = StringUtility.getParameter("interestRate", request);
				String[] lStrArrGpfAccNo = lStrGpfAccNo.split("~");
				String[] lStrArrYearlyId = lStrYearlyId.split("~");
				String[] lStrArrFinalAmount = lStrFinalAmount.split("~");
				Integer lIntFinYearId = lObjInterestCalculationDAO.getFinYearIdByFinCode(lStrFinancialYear).intValue();
				Long lLngYearId = lIntFinYearId.longValue();
				Float lFltInterestRate = Float.parseFloat(lStrInterestRate);
				Double lDblGpfYearlyClosingBal = 0d;

				for (int index = 0; index < lStrArrYearlyId.length; index++) {
					lObjMstGpfYearly = (MstGpfYearly) lObjInterestCalculationDAO.read(Long
							.parseLong(lStrArrYearlyId[index]));

					// lObjMstGpfYearly = new MstGpfYearly();
					// Long lLngGpfYearlyId =
					// IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_YEARLY",
					// inputMap);
					// lObjMstGpfYearly.setMstGpfYearlyId(lLngGpfYearlyId);
					// lObjMstGpfYearly.setGpfAccNo(lStrArrGpfAccNo[index]);
					// lObjMstGpfYearly.setFinYearId(lLngYearId);
					// Double lDblOpeningBal =
					// lObjGPFRequestProcessDAO.getOpeningBalForCurrYear(lStrArrGpfAccNo[index],
					// lLngYearId);
					// lObjMstGpfYearly.setOpeningBalance(lDblOpeningBal);
					// List lLstYearlyData =
					// lObjGPFRequestProcessDAO.getGPFAccountBalance(lStrArrGpfAccNo[index],
					// lLngYearId);
					// List lLstAdvanceHistory =
					// lObjGPFRequestProcessDAO.getAdvanceHistory(lStrArrGpfAccNo[index],
					// lLngYearId);
					//
					// if (lLstYearlyData != null && lLstYearlyData.size() > 0)
					// {
					// Object[] lObjArrYearly = (Object[])
					// lLstYearlyData.get(0);
					// lDblRegularSubscription = (Double) lObjArrYearly[0];
					// lDblAdvanceRecovery = (Double) lObjArrYearly[1];
					// lDblDeputationChallan = (Double) lObjArrYearly[2];
					// lDblPrePayAdvance = (Double) lObjArrYearly[3];
					// lDblExcessPayment = (Double) lObjArrYearly[4];
					// }
					// if (lLstAdvanceHistory != null &&
					// lLstAdvanceHistory.size() > 0) {
					// Object[] lObjArrAdvance = (Object[])
					// lLstAdvanceHistory.get(0);
					//
					// if (lObjArrAdvance[0].equals("RA")) {
					// lDblAdvanceSanctioned = (Double) lObjArrAdvance[1];
					// } else {
					// lDblWithdrawalSanctioned = (Double) lObjArrAdvance[1];
					// }
					// if (lLstAdvanceHistory.size() > 1) {
					// lObjArrAdvance = (Object[]) lLstAdvanceHistory.get(1);
					// lDblAdvanceSanctioned = (Double) lObjArrAdvance[1];
					// }
					// }
					// lObjMstGpfYearly.setRegularSubscription(lDblRegularSubscription);
					// lObjMstGpfYearly.setAdvanceRecovery(lDblAdvanceRecovery);
					// lObjMstGpfYearly.setAdvanceSanctioned(lDblAdvanceSanctioned);
					// lObjMstGpfYearly.setWithdrawalSanctioned(lDblWithdrawalSanctioned);
					// lObjMstGpfYearly.setDeputationChallan(lDblDeputationChallan);
					// lObjMstGpfYearly.setPrePayOfAdvance(lDblPrePayAdvance);
					// lObjMstGpfYearly.setExcessPayment(lDblExcessPayment);

					List lLstYearlyData = lObjGPFRequestProcessDAO.getGPFAccountBalance(lStrArrGpfAccNo[index],
							lLngYearId);
					if (lLstYearlyData != null && lLstYearlyData.size() > 0) {
						Object[] lObjArrYearly = (Object[]) lLstYearlyData.get(0);
						lLngScheduleCount = Long.parseLong(lObjArrYearly[5].toString());
					}
					if (lLngScheduleCount < 12) {
						lStrScheduleStatus = "pendingSchedules";
					} else {
						if (lObjMstGpfYearly.getFlatInterest() == null) {
							lObjMstGpfYearly.setFlatInterest(lFltInterestRate.doubleValue());
						} else {
							lStrApprove = "Approved";
							break;
						}
						// lDblCurrBal = lDblOpeningBal +
						// lDblRegularSubscription +
						// lDblAdvanceRecovery - lDblAdvanceSanctioned -
						// lDblWithdrawalSanctioned
						// + lDblDeputationChallan + lDblPrePayAdvance +
						// lDblArrearBalance;
						// lDblInterest = lFltInterestRate * lDblCurrBal / 100;

						if (!lStrArrFinalAmount[index].equals("") || !lStrArrFinalAmount[index].equals(null)) {
							lDblGpfYearlyClosingBal = Double.parseDouble(lStrArrFinalAmount[index]);
						}
						// Arrears Interest Calculation
						if (lLngYearId <= 25) {
							GPFInterestCalculationDAO lObjInterestCalculationDAOForArrear = new GPFInterestCalculationDAOImpl(
									MstGpfArrearsYearly.class, serv.getSessionFactory());
							lObjMstGpfArrearsYearly = lObjInterestCalculationDAOForArrear.getArrearYearlyIdForFinYear(
									lStrArrGpfAccNo[index], lLngYearId);
							if (lObjMstGpfArrearsYearly != null) {
								lDblArrearAmount = lObjMstGpfArrearsYearly.getArrearAmount();
								lDblOpenBalance = lObjMstGpfArrearsYearly.getOpenBalance();
								lDblArrearInterest = (lDblOpenBalance + lDblArrearAmount) * lFltInterestRate / 100;
								lObjMstGpfArrearsYearly.setInterestrate(lFltInterestRate);
								lObjMstGpfArrearsYearly.setCloseBalance(lDblOpenBalance + lDblArrearAmount
										+ lDblArrearInterest);
								lObjInterestCalculationDAOForArrear.update(lObjMstGpfArrearsYearly);
							}
						}
						if (lLngYearId == 25) {
							lDblArrearBalance = lObjInterestCalculationDAO.getArrearBalance(lStrArrGpfAccNo[index],
									lLngYearId);
							lDblGpfYearlyClosingBal = lDblGpfYearlyClosingBal + lDblArrearBalance;
						}
						lObjMstGpfYearly.setClosingBalance(lDblGpfYearlyClosingBal);
						lObjInterestCalculationDAO.update(lObjMstGpfYearly);
						
						
						//To approve all the missing credits
						lLstMissingCredit = lObjInterestCalculationDAO.getMissingCreditsToApprove(lStrArrGpfAccNo[index], lLngYearId);
						if(lLstMissingCredit != null && lLstMissingCredit.size() > 0){
							for(Integer lIntCnt = 0; lIntCnt < lLstMissingCredit.size(); lIntCnt++)
							{
								lLngMissingCrdtId = (Long) lLstMissingCredit.get(lIntCnt);
								lObjMstGpfMissingCredit = (MstGpfMissingCredit) lObjGPFRequestProcessDAO.read(lLngMissingCrdtId);
								lObjMstGpfMissingCredit.setStatus("A");
								lObjGPFRequestProcessDAO.update(lObjMstGpfMissingCredit);
							}
						}
					}
				}

			} else if (lStrReqType.equals("single")) {
				String lStrGpfAccNo = StringUtility.getParameter("empGpfAccNo", request);
				String lStrFromDate = StringUtility.getParameter("effectFromDate", request);
				String lStrInterestAmount = StringUtility.getParameter("interestAmount", request);
				Float lFltInterestRate = Float.parseFloat(StringUtility.getParameter("currentInt", request));
				Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearId(lStrFromDate);
				Long lLngYearId = lIntFinYearId.longValue();
				Long lLngGpfYearlyId;

				lObjGPFRequestProcessDAO.getOpeningBalForCurrYear(lStrGpfAccNo, lLngYearId);
				List lLstYearlyData = lObjGPFRequestProcessDAO.getGPFAccountBalance(lStrGpfAccNo, lLngYearId);
				List lLstAdvanceHistory = lObjGPFRequestProcessDAO.getAdvanceHistory(lStrGpfAccNo, lLngYearId);

				if (lLstYearlyData != null && lLstYearlyData.size() > 0) {
					Object[] lObjArrYearly = (Object[]) lLstYearlyData.get(0);
					lLngScheduleCount = Long.parseLong(lObjArrYearly[5].toString());
				}
				if (lLngScheduleCount < 12) {
					lStrScheduleStatus = "pendingSchedules";
				} else {
					if (lLstAdvanceHistory != null && lLstAdvanceHistory.size() > 0) {
						Object[] lObjArrAdvance = (Object[]) lLstAdvanceHistory.get(0);

						if (lObjArrAdvance[0].equals("RA")) {
						} else {
						}
						if (lLstAdvanceHistory.size() > 1) {
							lObjArrAdvance = (Object[]) lLstAdvanceHistory.get(1);
						}
					}

					lObjMstGpfYearly = new MstGpfYearly();
					lLngGpfYearlyId = lObjInterestCalculationDAO.getYearlyIdForFinYear(lStrGpfAccNo, lLngYearId);
					lObjMstGpfYearly = (MstGpfYearly) lObjInterestCalculationDAO.read(lLngGpfYearlyId);
					if (lObjMstGpfYearly != null) {
						if (lObjMstGpfYearly.getFlatInterest() == null) {
							lObjMstGpfYearly.setFlatInterest(Double.parseDouble(lStrInterestAmount));
							lObjMstGpfYearly.setClosingBalance(lObjMstGpfYearly.getClosingBalance()
									+ Double.parseDouble(lStrInterestAmount));

							lObjInterestCalculationDAO.update(lObjMstGpfYearly);
						} else {
							lStrApprove = "Approved";
						}
					} else {
						lStrScheduleStatus = "pendingSchedules";
					}

					// Arrears Interest Calculation
					GPFInterestCalculationDAO lObjInterestCalculationDAOForArrear = new GPFInterestCalculationDAOImpl(
							MstGpfArrearsYearly.class, serv.getSessionFactory());
					lObjMstGpfArrearsYearly = lObjInterestCalculationDAOForArrear.getArrearYearlyIdForFinYear(
							lStrGpfAccNo, lLngYearId);
					if (lObjMstGpfArrearsYearly == null) {
						// lStrScheduleStatus = "pendingArrear";
					} else {
						lDblArrearAmount = lObjMstGpfArrearsYearly.getArrearAmount();
						lDblOpenBalance = lObjMstGpfArrearsYearly.getOpenBalance();
						lDblArrearInterest = (lDblOpenBalance + lDblArrearAmount) * lFltInterestRate / 100;
						lObjMstGpfArrearsYearly.setInterestrate(lFltInterestRate);
						lObjMstGpfArrearsYearly
								.setCloseBalance(lDblOpenBalance + lDblArrearAmount + lDblArrearInterest);
						lObjInterestCalculationDAOForArrear.update(lObjMstGpfArrearsYearly);
					}
				}
				
				
				//To approve all the missing credits
				lLstMissingCredit = lObjInterestCalculationDAO.getMissingCreditsToApprove(lStrGpfAccNo, lLngYearId);
				if(lLstMissingCredit != null && lLstMissingCredit.size() > 0){
					for(Integer lIntCnt = 0; lIntCnt < lLstMissingCredit.size(); lIntCnt++)
					{
						lLngMissingCrdtId = (Long) lLstMissingCredit.get(lIntCnt);
						lObjMstGpfMissingCredit = (MstGpfMissingCredit) lObjGPFRequestProcessDAO.read(lLngMissingCrdtId);
						lObjMstGpfMissingCredit.setStatus("A");
						lObjGPFRequestProcessDAO.update(lObjMstGpfMissingCredit);
					}
				}
			}
			lBlFlag = true;
			String lSBStatus = getResponseXMLDoc(lBlFlag, lStrScheduleStatus, lStrApprove).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
		}
		return resObj;
	}

	/**
	 * @param lBlFlag
	 * @param lStrEmpName
	 * @param lStrGpfAccNo
	 * @param lStrOfficeName
	 * @param lDblCurrBalance
	 * @param lFltInterestRate
	 * @return
	 */
	private StringBuilder getResponseXMLDocForEmpDtls(Boolean lBlFlag, String lStrEmpName, String lStrGpfAccNo,
			String lStrOfficeName, Double lDblCurrBalance, Float lFltInterestRate) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<lBlFlag>");
		lStrBldXML.append(lBlFlag);
		lStrBldXML.append("</lBlFlag>");
		lStrBldXML.append("<lStrEmpName>");
		lStrBldXML.append(lStrEmpName);
		lStrBldXML.append("</lStrEmpName>");
		lStrBldXML.append("<lStrGpfAccNo>");
		lStrBldXML.append(lStrGpfAccNo);
		lStrBldXML.append("</lStrGpfAccNo>");
		lStrBldXML.append("<lStrOfficeName>");
		lStrBldXML.append(lStrOfficeName);
		lStrBldXML.append("</lStrOfficeName>");
		lStrBldXML.append("<lDblCurrBalance>");
		lStrBldXML.append(lDblCurrBalance);
		lStrBldXML.append("</lDblCurrBalance>");
		lStrBldXML.append("<lFltInterestRate>");
		lStrBldXML.append(lFltInterestRate);
		lStrBldXML.append("</lFltInterestRate>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	/**
	 * @param lFltInterestRate
	 * @param lDblAmountAfterInt
	 * @return
	 */
	private StringBuilder getResponseXMLDocForInterestRate(Float lFltInterestRate, Double lDblAmountAfterInt) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<lFltInterestRate>");
		lStrBldXML.append(lFltInterestRate);
		lStrBldXML.append("</lFltInterestRate>");
		lStrBldXML.append("<lDblAmountAfterInt>");
		lStrBldXML.append(lDblAmountAfterInt);
		lStrBldXML.append("</lDblAmountAfterInt>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	/**
	 * @param lBlFlag
	 * @param lStrScheduleStatus
	 * @param lStrApprove
	 * @return
	 */
	private StringBuilder getResponseXMLDoc(Boolean lBlFlag, String lStrScheduleStatus, String lStrApprove) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<lBlFlag>");
		lStrBldXML.append(lBlFlag);
		lStrBldXML.append("</lBlFlag>");
		lStrBldXML.append("<lStrScheduleStatus>");
		lStrBldXML.append(lStrScheduleStatus);
		lStrBldXML.append("</lStrScheduleStatus>");
		lStrBldXML.append("<lStrApprove>");
		lStrBldXML.append(lStrApprove);
		lStrBldXML.append("</lStrApprove>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

}
