/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 1, 2011		Jayraj Chudasama								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFDataEntryFormDAO;
import com.tcs.sgv.gpf.dao.GPFDataEntryFormDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;
import com.tcs.sgv.gpf.valueobject.MstGpfChallanDtls;
import com.tcs.sgv.gpf.valueobject.MstGpfEmpSubscription;
import com.tcs.sgv.gpf.valueobject.MstGpfMonthly;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Dec 1, 2011
 */
public class GPFDataEntryVOGenerator extends ServiceImpl implements VOGeneratorService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.core.service.VOGeneratorService#generateMap(java.util.Map)
	 */
	Log gLogger = LogFactory.getLog(getClass());

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/gpf/GPFConstants");

	public ResultObject generateMap(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List<MstGpfAdvance> lLstAdvanceDtlsNRA = new ArrayList<MstGpfAdvance>();
		List<MstGpfAdvance> lLstAdvanceDtlsRA = new ArrayList<MstGpfAdvance>();
		MstGpfAdvance lObjMstGpfAdvanceCur = null;
		List<MstGpfEmpSubscription> lLstSubscriptionDtls = new ArrayList<MstGpfEmpSubscription>();
		Map<String, List> lScheduleVOMap = new HashMap<String, List>();
		try {
			lLstAdvanceDtlsNRA = getAdvanceDetailsNRA(inputMap);
			lLstAdvanceDtlsRA = getAdvanceDetailsRA(inputMap);
			lObjMstGpfAdvanceCur = getAdvanceDetailsRACur(inputMap);
			lLstSubscriptionDtls = getSubscriptionDetails(inputMap);
			lScheduleVOMap = getScheduleDtls(inputMap);
			inputMap.put("AdvanceDetailsNRA", lLstAdvanceDtlsNRA);
			inputMap.put("AdvanceDetailsRA", lLstAdvanceDtlsRA);
			inputMap.put("AdvanceDetailsRACur", lObjMstGpfAdvanceCur);
			inputMap.put("SubscriptionDetails", lLstSubscriptionDtls);
			inputMap.put("ScheduleDtls", lScheduleVOMap);
		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, ex, "Error is: ");
		}

		return objRes;
	}

	private List<MstGpfEmpSubscription> getSubscriptionDetails(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		MstGpfEmpSubscription lObjMstGpfEmpSubscription = null;
		List<MstGpfEmpSubscription> lLstSubscriptionDtls = new ArrayList<MstGpfEmpSubscription>();
		List lLstPrvRecords = null;
		Object[] obj = null;

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		String lStrSaveOrUpdateSub = "";
		String lStrSevaarthId = "";
		String lStrGpfAccNo = "";

		try {
			String[] lArrStrSubDtlsPk = StringUtility.getParameterValues("hidSubDtlsPk", request);
			String[] lArrStrSubMonth = StringUtility.getParameterValues("hidSubMonth", request);
			String[] lArrStrSubYear = StringUtility.getParameterValues("hidSubYear", request);
			String[] lArrStrSubAmount = StringUtility.getParameterValues("txtSubscrAmount", request);
			String lStrPrevYearSub = StringUtility.getParameter("txtPrevSubAmt", request);

			lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
			GPFDataEntryFormDAO lObjGpfDataEntryFormDAO = new GPFDataEntryFormDAOImpl(MstGpfEmpSubscription.class, serv
					.getSessionFactory());
			lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
			lLstPrvRecords = lObjGpfDataEntryFormDAO.getSubscriptionDetails(lStrGpfAccNo, "Draft");

			for (int lIntCnt = 0; lIntCnt < lLstPrvRecords.size(); lIntCnt++) {
				// lObjMstGpfEmpSubscription = new MstGpfEmpSubscription();
				obj = (Object[]) lLstPrvRecords.get(lIntCnt);
				/*
				 * lObjMstGpfEmpSubscription = (MstGpfEmpSubscription)
				 * lObjGpfDataEntryFormDAO
				 * .read(Long.parseLong(obj[3].toString()));
				 * lObjMstGpfEmpSubscription.setStatusFlag("X");
				 * lObjGpfDataEntryFormDAO.update(lObjMstGpfEmpSubscription);
				 */
				lObjGpfDataEntryFormDAO.updateEmpSubscription(obj[3].toString(), "Discard");
			}

			if (lArrStrSubMonth.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrSubMonth.length; lIntCnt++) {

					if (lArrStrSubDtlsPk[lIntCnt] != null && lArrStrSubDtlsPk[lIntCnt].trim().length() > 0) {
						lObjMstGpfEmpSubscription = (MstGpfEmpSubscription) lObjGpfDataEntryFormDAO.read(Long
								.parseLong(lArrStrSubDtlsPk[lIntCnt]));
						lStrSaveOrUpdateSub = lStrSaveOrUpdateSub + "2,";
					} else {
						lObjMstGpfEmpSubscription = new MstGpfEmpSubscription();
						lStrSaveOrUpdateSub = lStrSaveOrUpdateSub + "1,";
					}

					if (lArrStrSubMonth[lIntCnt] != null && lArrStrSubMonth[lIntCnt].trim().length() > 0) {
						lObjMstGpfEmpSubscription.setEffectFromMonth(Integer.parseInt(lArrStrSubMonth[lIntCnt].trim()));
					}

					if (lArrStrSubYear[lIntCnt] != null && lArrStrSubYear[lIntCnt].trim().length() > 0) {
						lObjMstGpfEmpSubscription.setFinYearId(Long.parseLong(lArrStrSubYear[lIntCnt].trim()));
					}

					if (lArrStrSubAmount[lIntCnt] != null && lArrStrSubAmount[lIntCnt].trim().length() > 0) {
						lObjMstGpfEmpSubscription.setMonthlySubscription((double) Math.round(Double
								.parseDouble(lArrStrSubAmount[lIntCnt].trim())));
					}

					if (lIntCnt == 0) {
						if (lStrPrevYearSub != null && lStrPrevYearSub != "") {
							if (Math.round(Double.parseDouble(lArrStrSubAmount[lIntCnt])) > Math.round(Double
									.parseDouble(lStrPrevYearSub))) {
								lObjMstGpfEmpSubscription.setChangeType("I");
							} else {
								lObjMstGpfEmpSubscription.setChangeType("D");
							}
						}
					} else {
						if (Math.round(Double.parseDouble(lArrStrSubAmount[lIntCnt])) > Math.round(Double
								.parseDouble(lArrStrSubAmount[lIntCnt - 1]))) {
							lObjMstGpfEmpSubscription.setChangeType("I");
						} else {
							lObjMstGpfEmpSubscription.setChangeType("D");
						}
					}

					lObjMstGpfEmpSubscription.setCreatedDate(gDtCurrDt);
					lObjMstGpfEmpSubscription.setCreatedPostId(gLngPostId);
					lObjMstGpfEmpSubscription.setCreatedUserId(gLngUserId);
					//lObjMstGpfEmpSubscription.setDeoActionDate(gDtCurrDt);

					lLstSubscriptionDtls.add(lObjMstGpfEmpSubscription);
				}
			}
			inputMap.put("isSaveOrUpdateSub", lStrSaveOrUpdateSub);

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstSubscriptionDtls;
	}

	private List<MstGpfAdvance> getAdvanceDetailsNRA(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjDateFormate = new SimpleDateFormat("dd/MM/yyyy");

		MstGpfAdvance lObjMstGpfAdvanceNRA = null;
		List<MstGpfAdvance> lLstAdvanceDtlsNRA = new ArrayList<MstGpfAdvance>();
		Object[] obj = null;

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		List lLstPrvRecords = null;
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		String lStrSaveOrUpdateNRA = "";
		String lStrSevaarthId = "";
		String lStrGpfAccNo = "";
		String lStrAllVoucherNo = "";
		String[] lArrStrAllVchrNo = null;
		String lStrAllVoucherDate = "";
		String[] lArrStrAllVchrDate = null;

		try {
			String[] lArrStrNRADtlsPk = StringUtility.getParameterValues("hidNRADtlsPk", request);
			String[] lArrStrSrNoNRA = StringUtility.getParameterValues("txtNRASrno", request);
			String[] lArrStrPurposeNRA = StringUtility.getParameterValues("hidPurposeOfAdvanceNRA", request);
			String[] lArrStrSancAmountNRA = StringUtility.getParameterValues("txtSancAmountNRA", request);
			String[] lArrStrSancDateNRA = StringUtility.getParameterValues("txtSanctnDateNRA", request);
			String[] lArrStrVchrNoNRA = StringUtility.getParameterValues("txtVoucherNoNRA", request);
			String[] lArrStrVchrDateNRA = StringUtility.getParameterValues("txtVoucherDateNRA", request);
			String[] lArrStrNoInstlmntNRA = StringUtility.getParameterValues("txtNoOfInstlmntsNRA", request);
			String[] lArrStrRecovrdInstlmntNRA = StringUtility.getParameterValues("txtNoOfRecoveredInstlmntNRA",
					request);
			String[] lArrStrInstlmntAmtNRA = StringUtility.getParameterValues("txtInstlmntAmtPerDisbrmnt", request);
			String[] lArrStrOtherPurposeNRA = StringUtility.getParameterValues("txtReasonOtherNRA", request);
			Integer lIntFinYearId = null;

			GPFDataEntryFormDAO lObjGpfDataEntryFormDAO = new GPFDataEntryFormDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());

			lIntFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();

			lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
			lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
			lLstPrvRecords = lObjGpfDataEntryFormDAO.getNRADetails(lStrGpfAccNo, "Draft");

			for (int lIntCnt = 0; lIntCnt < lLstPrvRecords.size(); lIntCnt++) {
				// lObjMstGpfAdvanceNRA = new MstGpfAdvance();
				obj = (Object[]) lLstPrvRecords.get(lIntCnt);
				/*
				 * lObjMstGpfAdvanceNRA = (MstGpfAdvance)
				 * lObjGpfDataEntryFormDAO
				 * .read(Long.parseLong(obj[7].toString()));
				 * lObjMstGpfAdvanceNRA.setStatusFlag("X");
				 * lObjGpfDataEntryFormDAO.update(lObjMstGpfAdvanceNRA);
				 */
				lObjGpfDataEntryFormDAO.updateAdvanceDetails(obj[7].toString());
			}

			if (lArrStrSrNoNRA.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrSrNoNRA.length; lIntCnt++) {
					if (lArrStrNRADtlsPk[lIntCnt] != null && lArrStrNRADtlsPk[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceNRA = (MstGpfAdvance) lObjGpfDataEntryFormDAO.read(Long
								.parseLong(lArrStrNRADtlsPk[lIntCnt]));
						lStrSaveOrUpdateNRA = lStrSaveOrUpdateNRA + "2,";
					} else {
						lObjMstGpfAdvanceNRA = new MstGpfAdvance();
						lStrSaveOrUpdateNRA = lStrSaveOrUpdateNRA + "1,";
					}

					if (lArrStrPurposeNRA[lIntCnt] != null && lArrStrPurposeNRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceNRA.setPurposeCategory(Long.parseLong(lArrStrPurposeNRA[lIntCnt]));
					}
					if (lArrStrSancAmountNRA[lIntCnt] != null && lArrStrSancAmountNRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceNRA.setAmountSanctioned(Double.parseDouble(lArrStrSancAmountNRA[lIntCnt]));
						lObjMstGpfAdvanceNRA.setPayableAmount(Double.parseDouble(lArrStrSancAmountNRA[lIntCnt]));
						lObjMstGpfAdvanceNRA.setAdvanceAmt(Double.parseDouble(lArrStrSancAmountNRA[lIntCnt]));
						lObjMstGpfAdvanceNRA.setRecoveredAmount(Double.parseDouble(lArrStrSancAmountNRA[lIntCnt]));
					}
					if (lArrStrSancDateNRA[lIntCnt] != null && lArrStrSancDateNRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceNRA.setSanctionedDate(lObjDateFormate.parse(lArrStrSancDateNRA[lIntCnt]));
					}
					if (lArrStrVchrNoNRA[lIntCnt] != null && lArrStrVchrNoNRA[lIntCnt].trim().length() > 0) {
						lStrAllVoucherNo = lArrStrVchrNoNRA[lIntCnt];
						lArrStrAllVchrNo = lStrAllVoucherNo.split(",");

						lObjMstGpfAdvanceNRA.setVoucherNo(lArrStrAllVchrNo[0]);

						if (lArrStrRecovrdInstlmntNRA[lIntCnt].equals("2")) {
							lObjMstGpfAdvanceNRA.setVoucherNo2(lArrStrAllVchrNo[1]);
						} else if (lArrStrRecovrdInstlmntNRA[lIntCnt].equals("3")) {
							lObjMstGpfAdvanceNRA.setVoucherNo2(lArrStrAllVchrNo[1]);
							lObjMstGpfAdvanceNRA.setVoucherNo3(lArrStrAllVchrNo[2]);
						} else if (lArrStrRecovrdInstlmntNRA[lIntCnt].equals("4")) {
							lObjMstGpfAdvanceNRA.setVoucherNo2(lArrStrAllVchrNo[1]);
							lObjMstGpfAdvanceNRA.setVoucherNo3(lArrStrAllVchrNo[2]);
							lObjMstGpfAdvanceNRA.setVoucherNo4(lArrStrAllVchrNo[3]);
						}
					}
					if (lArrStrVchrDateNRA[lIntCnt] != null && lArrStrVchrDateNRA[lIntCnt].trim().length() > 0) {
						lStrAllVoucherDate = lArrStrVchrDateNRA[lIntCnt];
						lArrStrAllVchrDate = lStrAllVoucherDate.split(",");

						lObjMstGpfAdvanceNRA.setVoucherDate(lObjDateFormate.parse(lArrStrAllVchrDate[0]));

						if (lArrStrRecovrdInstlmntNRA[lIntCnt].equals("2")) {
							lObjMstGpfAdvanceNRA.setVoucherDate2(lObjDateFormate.parse(lArrStrAllVchrDate[1]));
						} else if (lArrStrRecovrdInstlmntNRA[lIntCnt].equals("3")) {
							lObjMstGpfAdvanceNRA.setVoucherDate2(lObjDateFormate.parse(lArrStrAllVchrDate[1]));
							lObjMstGpfAdvanceNRA.setVoucherDate3(lObjDateFormate.parse(lArrStrAllVchrDate[2]));
						} else if (lArrStrRecovrdInstlmntNRA[lIntCnt].equals("4")) {
							lObjMstGpfAdvanceNRA.setVoucherDate2(lObjDateFormate.parse(lArrStrAllVchrDate[1]));
							lObjMstGpfAdvanceNRA.setVoucherDate3(lObjDateFormate.parse(lArrStrAllVchrDate[2]));
							lObjMstGpfAdvanceNRA.setVoucherDate4(lObjDateFormate.parse(lArrStrAllVchrDate[3]));
						}
					}
					if (lArrStrNoInstlmntNRA[lIntCnt] != null && lArrStrNoInstlmntNRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceNRA.setNoOfInstallments(Integer.parseInt(lArrStrNoInstlmntNRA[lIntCnt]));
					}
					if (lArrStrRecovrdInstlmntNRA[lIntCnt] != null
							&& lArrStrRecovrdInstlmntNRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceNRA.setInstallmentsLeft(Integer.parseInt(lArrStrNoInstlmntNRA[lIntCnt])
								- Integer.parseInt(lArrStrRecovrdInstlmntNRA[lIntCnt]));
					}
					if (lArrStrInstlmntAmtNRA[lIntCnt] != null && lArrStrInstlmntAmtNRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceNRA.setInstallmentAmount(Double.parseDouble(lArrStrInstlmntAmtNRA[lIntCnt]));
					}
					if (lArrStrOtherPurposeNRA[lIntCnt] != null && lArrStrOtherPurposeNRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceNRA.setOtherPurpose(lArrStrOtherPurposeNRA[lIntCnt]);
					}

					lObjMstGpfAdvanceNRA.setAdvanceType("NRA");
					lObjMstGpfAdvanceNRA.setFinYearId(Long.parseLong(lIntFinYearId.toString()));
					lObjMstGpfAdvanceNRA.setCreatedDate(gDtCurrDt);
					lObjMstGpfAdvanceNRA.setCreatedPostId(gLngPostId);
					lObjMstGpfAdvanceNRA.setCreatedUserId(gLngUserId);
					//lObjMstGpfAdvanceNRA.setDeoActionDate(gDtCurrDt);
					lObjMstGpfAdvanceNRA.setOutstandingAmount(Double.parseDouble("0"));
					lObjMstGpfAdvanceNRA.setRecoveryStatus(Integer.parseInt("1"));
					lObjMstGpfAdvanceNRA.setDataEntry("1");
					lLstAdvanceDtlsNRA.add(lObjMstGpfAdvanceNRA);
				}
			}

			inputMap.put("isSaveOrUpdateNRA", lStrSaveOrUpdateNRA);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			throw e;
		}
		return lLstAdvanceDtlsNRA;
	}

	private List<MstGpfAdvance> getAdvanceDetailsRA(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjDateFormate = new SimpleDateFormat("dd/MM/yyyy");

		MstGpfAdvance lObjMstGpfAdvanceRA = null;
		List<MstGpfAdvance> lLstAdvanceDtlsRA = new ArrayList<MstGpfAdvance>();
		Object[] obj = null;

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		List lLstPrvRecords = null;
		String lStrSaveOrUpdateRA = "";
		String lStrSevaarthId = "";
		String lStrGpfAccNo = "";

		try {
			String[] lArrStrRAHisDtlsPk = StringUtility.getParameterValues("hidRADtlsPk", request);
			String[] lArrStrSrNoRA = StringUtility.getParameterValues("txtRAHistorySrnoRA", request);
			String[] lArrStrPurposeRA = StringUtility.getParameterValues("hidPurposeOfAdvanceRA", request);
			String[] lArrStrSancAmountRA = StringUtility.getParameterValues("txtSancAmountRA", request);
			String[] lArrStrSancDateRA = StringUtility.getParameterValues("txtSancDateRA", request);
			String[] lArrStrVchrNoRA = StringUtility.getParameterValues("txtVoucherNoRA", request);
			String[] lArrStrVchrDateRA = StringUtility.getParameterValues("txtVoucherDateRA", request);
			String[] lArrStrNoInstlmntRA = StringUtility.getParameterValues("txtNoOfInstlmntRA", request);
			String[] lArrStrInstlmntAmtRA = StringUtility.getParameterValues("txtInstlmntAmtPmRA", request);
			String[] lArrStrFirstOddInstlmntRA = StringUtility.getParameterValues("txtFirstOddInstlmntRA", request);
			// String[] lArrStrRecoveredInstlmntRA =
			// StringUtility.getParameterValues("txtRecoveredInstlmntRA",
			// request);
			String[] lArrStrOtherPurposeRA = StringUtility.getParameterValues("txtReasonOtherRAHis", request);

			Integer lIntFinYearId = null;

			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());

			GPFDataEntryFormDAO lObjGpfDataEntryFormDAO = new GPFDataEntryFormDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());
			lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
			lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrSevaarthId);
			lLstPrvRecords = lObjGpfDataEntryFormDAO.getRADetailsHis(lStrGpfAccNo, "Draft");

			for (int lIntCnt = 0; lIntCnt < lLstPrvRecords.size(); lIntCnt++) {
				// lObjMstGpfAdvanceRA = new MstGpfAdvance();
				obj = (Object[]) lLstPrvRecords.get(lIntCnt);
				/*
				 * lObjMstGpfAdvanceRA = (MstGpfAdvance)
				 * lObjGpfDataEntryFormDAO.
				 * read(Long.parseLong(obj[9].toString()));
				 * lObjMstGpfAdvanceRA.setStatusFlag("X");
				 * lObjGpfDataEntryFormDAO.update(lObjMstGpfAdvanceRA);
				 */
				lObjGpfDataEntryFormDAO.updateAdvanceDetails(obj[9].toString());
			}

			if (lArrStrSrNoRA.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrSrNoRA.length; lIntCnt++) {
					if (lArrStrRAHisDtlsPk[lIntCnt] != null && lArrStrRAHisDtlsPk[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA = (MstGpfAdvance) lObjGpfDataEntryFormDAO.read(Long
								.parseLong(lArrStrRAHisDtlsPk[lIntCnt]));
						lStrSaveOrUpdateRA = lStrSaveOrUpdateRA + "2,";
					} else {
						lObjMstGpfAdvanceRA = new MstGpfAdvance();
						lStrSaveOrUpdateRA = lStrSaveOrUpdateRA + "1,";
					}

					if (lArrStrPurposeRA[lIntCnt] != null && lArrStrPurposeRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA.setPurposeCategory(Long.parseLong(lArrStrPurposeRA[lIntCnt]));
					}
					if (lArrStrSancAmountRA[lIntCnt] != null && lArrStrSancAmountRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA.setAmountSanctioned(Double.parseDouble(lArrStrSancAmountRA[lIntCnt]));
						lObjMstGpfAdvanceRA.setPayableAmount(Double.parseDouble(lArrStrSancAmountRA[lIntCnt]));
						lObjMstGpfAdvanceRA.setAdvanceAmt(Double.parseDouble(lArrStrSancAmountRA[lIntCnt]));
						lObjMstGpfAdvanceRA.setRecoveredAmount(Double.parseDouble(lArrStrSancAmountRA[lIntCnt]));
					}
					if (lArrStrSancDateRA[lIntCnt] != null && lArrStrSancDateRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA.setSanctionedDate(lObjDateFormate.parse(lArrStrSancDateRA[lIntCnt]));
					}
					if (lArrStrVchrNoRA[lIntCnt] != null && lArrStrVchrNoRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA.setVoucherNo(lArrStrVchrNoRA[lIntCnt]);
					}
					if (lArrStrVchrDateRA[lIntCnt] != null && lArrStrVchrDateRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA.setVoucherDate(lObjDateFormate.parse(lArrStrVchrDateRA[lIntCnt]));
					}
					if (lArrStrNoInstlmntRA[lIntCnt] != null && lArrStrNoInstlmntRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA.setNoOfInstallments(Integer.parseInt(lArrStrNoInstlmntRA[lIntCnt]));
					}
					if (lArrStrInstlmntAmtRA[lIntCnt] != null && lArrStrInstlmntAmtRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA.setInstallmentAmount(Double.parseDouble(lArrStrInstlmntAmtRA[lIntCnt]));
					}
					if (lArrStrFirstOddInstlmntRA[lIntCnt] != null
							&& lArrStrFirstOddInstlmntRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA.setOddInstallment(Double.parseDouble(lArrStrFirstOddInstlmntRA[lIntCnt]));
					}

					lObjMstGpfAdvanceRA.setInstallmentsLeft(Integer.parseInt("0"));

					if (lArrStrOtherPurposeRA[lIntCnt] != null && lArrStrOtherPurposeRA[lIntCnt].trim().length() > 0) {
						lObjMstGpfAdvanceRA.setOtherPurpose(lArrStrOtherPurposeRA[lIntCnt]);
					}

					lIntFinYearId = lObjFinancialYearDAO.getFinYearId(lArrStrSancDateRA[lIntCnt]);
					lObjMstGpfAdvanceRA.setFinYearId(lIntFinYearId.longValue());

					lObjMstGpfAdvanceRA.setAdvanceType("RA");
					lObjMstGpfAdvanceRA.setCreatedDate(gDtCurrDt);
					lObjMstGpfAdvanceRA.setCreatedPostId(gLngPostId);
					lObjMstGpfAdvanceRA.setCreatedUserId(gLngUserId);
					//lObjMstGpfAdvanceRA.setDeoActionDate(gDtCurrDt);
					lObjMstGpfAdvanceRA.setOutstandingAmount(Double.parseDouble("0"));
					lObjMstGpfAdvanceRA.setRecoveryStatus(Integer.parseInt("1"));
					lObjMstGpfAdvanceRA.setDataEntry("1");
					lLstAdvanceDtlsRA.add(lObjMstGpfAdvanceRA);
				}
			}
			inputMap.put("isSaveOrUpdateRAHis", lStrSaveOrUpdateRA);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			throw e;
		}
		return lLstAdvanceDtlsRA;
	}

	private MstGpfAdvance getAdvanceDetailsRACur(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjDateFormate = new SimpleDateFormat("dd/MM/yyyy");

		MstGpfAdvance lObjMstGpfAdvanceRA = null;

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		String lStrRACurDtlsPk = StringUtility.getParameter("hidRADtlsCurPk", request);
		String lStrPurposeRA = StringUtility.getParameter("cmbPurposeCategoryCur", request);
		String lStrSancAmountRA = StringUtility.getParameter("txtSancAmountCur", request);
		String lStrSancDateRA = StringUtility.getParameter("txtSancDateCur", request);
		String lStrVchrNoRA = StringUtility.getParameter("txtVchrNoCurRA", request);
		String lStrVchrDateRA = StringUtility.getParameter("txtVchrDateCurRA", request);
		String lStrNoInstlmntRA = StringUtility.getParameter("txtNoOfInstlmntCur", request);
		String lStrInstlmntAmtRA = StringUtility.getParameter("txtInstlmntAmtPerMonCur", request);
		String lStrFirstOddInstlmntRA = StringUtility.getParameter("txtFirstOddInstlmntCur", request);
		String lStrRecoveredInstlmntRA = StringUtility.getParameter("txtOutstndgInstlmntCur", request);
		String lStrOutstndAmount = StringUtility.getParameter("outstndAmount", request);
		String lStrReasonOther = StringUtility.getParameter("textareaOtherCur", request);
		String lStrRecoveredInstlmnt = StringUtility.getParameter("recoveredInstlmnt", request);

		String lStrSaveOrUpdateRACur = "";
		Integer lIntFinYearId = null;

		GPFDataEntryFormDAO lObjGpfDataEntryFormDAO = new GPFDataEntryFormDAOImpl(MstGpfAdvance.class, serv
				.getSessionFactory());
		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
		lIntFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();

		try {
			if (!"-1".equals(lStrPurposeRA)) {
				if (lStrRACurDtlsPk.equals("")) {
					lObjMstGpfAdvanceRA = new MstGpfAdvance();
					lStrSaveOrUpdateRACur = "1";
				} else {
					lObjMstGpfAdvanceRA = (MstGpfAdvance) lObjGpfDataEntryFormDAO.read(Long.parseLong(lStrRACurDtlsPk));
					lStrSaveOrUpdateRACur = "2";
				}

				Integer lIntNoOfMonth = null;
				Date lObjDate = new Date();
				Integer lIntCurMonth = lObjDate.getMonth() + 1;

				if (lIntCurMonth >= 4 && lIntCurMonth <= 12) {
					lIntNoOfMonth = lIntCurMonth - 4;
				} else {
					lIntNoOfMonth = lIntCurMonth + 8;
				}

				lObjMstGpfAdvanceRA.setPurposeCategory(Long.parseLong(lStrPurposeRA));

				if (lStrSancAmountRA != null && lStrSancAmountRA != "") {
					lObjMstGpfAdvanceRA.setAmountSanctioned(Double.parseDouble(lStrSancAmountRA));
					lObjMstGpfAdvanceRA.setPayableAmount(Double.parseDouble(lStrSancAmountRA));
					lObjMstGpfAdvanceRA.setAdvanceAmt(Double.parseDouble(lStrSancAmountRA));
					lObjMstGpfAdvanceRA.setRecoveredAmount(Double.parseDouble(lStrSancAmountRA));
				}
				if (lStrSancDateRA!= null && !lStrSancDateRA.equals("")) {
					lObjMstGpfAdvanceRA.setSanctionedDate(lObjDateFormate.parse(lStrSancDateRA));
				}
				if (lStrVchrNoRA != null && !lStrVchrNoRA.equals("")) {
					lObjMstGpfAdvanceRA.setVoucherNo(lStrVchrNoRA);
				}
				if (lStrVchrDateRA != null && !lStrVchrDateRA.equals("")) {
					lObjMstGpfAdvanceRA.setVoucherDate(lObjDateFormate.parse(lStrVchrDateRA));
				}
				if (lStrNoInstlmntRA != null && !lStrNoInstlmntRA.equals("")) {
					lObjMstGpfAdvanceRA.setNoOfInstallments(Integer.parseInt(lStrNoInstlmntRA));
				}
				if (lStrInstlmntAmtRA != null && !lStrInstlmntAmtRA.equals("")) {
					lObjMstGpfAdvanceRA.setInstallmentAmount(Double.parseDouble(lStrInstlmntAmtRA));
				}
				if (lStrFirstOddInstlmntRA != null && !lStrFirstOddInstlmntRA.equals("")) {
					lObjMstGpfAdvanceRA.setOddInstallment(Double.parseDouble(lStrFirstOddInstlmntRA));
				}				
				if (lStrRecoveredInstlmntRA != null && !lStrRecoveredInstlmntRA.equals("")) {
					lObjMstGpfAdvanceRA.setOutstandingInstallmentPrevFinYear(Integer.parseInt(lStrRecoveredInstlmntRA));
				}
				if (lStrOutstndAmount != null && !lStrOutstndAmount.equals("")) {
					lObjMstGpfAdvanceRA.setOutstandingAmount(Double.parseDouble(lStrOutstndAmount));
				}
				if (lStrReasonOther != null && !lStrReasonOther.equals("")) {
					lObjMstGpfAdvanceRA.setOtherPurpose(lStrReasonOther);
				}
				
				lObjMstGpfAdvanceRA.setInstallmentsLeft(Integer.parseInt(lStrNoInstlmntRA) - Integer.parseInt(lStrRecoveredInstlmnt));
				
				lObjMstGpfAdvanceRA.setAdvanceType("RA");
				lObjMstGpfAdvanceRA.setFinYearId(Long.parseLong(lIntFinYearId.toString()));
				lObjMstGpfAdvanceRA.setCreatedDate(gDtCurrDt);
				lObjMstGpfAdvanceRA.setCreatedPostId(gLngPostId);
				lObjMstGpfAdvanceRA.setCreatedUserId(gLngUserId);
				lObjMstGpfAdvanceRA.setDataEntry("1");
				//lObjMstGpfAdvanceRA.setDeoActionDate(gDtCurrDt);
				lObjMstGpfAdvanceRA.setRecoveryStatus(Integer.parseInt("1"));

			}

			inputMap.put("isSaveOrUpdateRACur", lStrSaveOrUpdateRACur);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			throw e;
		}
		return lObjMstGpfAdvanceRA;
	}

	private Map<String, List> getScheduleDtls(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjDateFormate = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, List> lScheduleVOMap = new HashMap<String, List>();
		MstGpfMonthly lObjMstGpfMonthly = null;
		MstGpfChallanDtls lObjMstGpfChallanDtls = null;
		List<MstGpfMonthly> lLstVoucherDtls = new ArrayList<MstGpfMonthly>();
		List<MstGpfChallanDtls> lLstChallanDtls = new ArrayList<MstGpfChallanDtls>();
		List lLstPrvDataVchr = null;
		List lLstPrvDataChallan = null;

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		Integer lIntVchrCnt = 0;
		Integer lIntChlnCnt = 0;
		String lStrSaveOrUpdateChalln = "";
		String lStrSaveOrUpdateVchr = "";
		String lStrSevaarthId = "";
		String lStrGpfAccNo = "";

		GPFDataEntryFormDAO lObjGpfDataEntryFormDAOMon = new GPFDataEntryFormDAOImpl(MstGpfMonthly.class, serv
				.getSessionFactory());
		GPFDataEntryFormDAO lObjGpfDataEntryFormDAOChln = new GPFDataEntryFormDAOImpl(MstGpfChallanDtls.class, serv
				.getSessionFactory());

		try {
			String[] lArrStrVoucherChallnPk = StringUtility.getParameterValues("hidSchdlDtlsPk", request);
			String[] lArrStrVoucherChallnNo = StringUtility.getParameterValues("txtVchrChallnNoRACur", request);
			String[] lArrStrVoucherChallnDate = StringUtility.getParameterValues("txtVchrChallnDateRACur", request);
			String[] lArrStrMonth = StringUtility.getParameterValues("cmbMonthCur", request);
			String[] lArrStrYear = StringUtility.getParameterValues("cmbYearCur", request);
			String[] lArrStrInstallmentAmt = StringUtility.getParameterValues("txtInstAmountRACur", request);
			String[] lArrStrVchrInstNo = StringUtility.getParameterValues("txtInstlmntVchr", request);
			String lStrNoOfInstlmnt = StringUtility.getParameter("txtNoOfInstlmntCur", request);
			//monthly subscription
			String[] lArrStrMonthlySubscription = StringUtility.getParameterValues("monthlySubsc", request); 
			String[] lArrStrVoucherChallan = null;
			String[] lArrStrChallanInstFrom = null;
			String[] lArrStrChallanInstTo = null;
			String lStrChallanInstFrom = "";
			String lStrChallanInstTo = "";
			String lStrVoucherChallan = "";

			Integer lIntScheduleListSize = Integer.parseInt(StringUtility.getParameter("scheduleSize", request));
			lStrSevaarthId = StringUtility.getParameter("txtEmployeeCode", request);
			lStrGpfAccNo = lObjGpfDataEntryFormDAOMon.getGpfAccNo(lStrSevaarthId);
			lLstPrvDataVchr = lObjGpfDataEntryFormDAOMon.getPrevDetailsVoucher(lStrGpfAccNo);
			for (int lIntCnt = 0; lIntCnt < lLstPrvDataVchr.size(); lIntCnt++) {
				lObjMstGpfMonthly = new MstGpfMonthly();
				lObjMstGpfMonthly = (MstGpfMonthly) lLstPrvDataVchr.get(lIntCnt);
				lObjMstGpfMonthly.setStatus("D");
				lObjGpfDataEntryFormDAOMon.update(lObjMstGpfMonthly);
			}

			lLstPrvDataChallan = lObjGpfDataEntryFormDAOChln.getPrevDetailsChallan(lStrGpfAccNo);
			for (int lIntCnt = 0; lIntCnt < lLstPrvDataChallan.size(); lIntCnt++) {
				lObjMstGpfChallanDtls = new MstGpfChallanDtls();
				lObjMstGpfChallanDtls = (MstGpfChallanDtls) lLstPrvDataChallan.get(lIntCnt);
				lObjMstGpfChallanDtls.setStatusFlag("X");
				lObjGpfDataEntryFormDAOChln.update(lObjMstGpfChallanDtls);
			}

			for (int lIntCnt = 0; lIntCnt < lIntScheduleListSize; lIntCnt++) {
				if (!StringUtility.getParameter("radioVoucherChallan" + lIntCnt, request).equals("")) {
					lStrVoucherChallan += StringUtility.getParameter("radioVoucherChallan" + lIntCnt, request) + ",";

					if (StringUtility.getParameter("radioVoucherChallan" + lIntCnt, request).equals("C")) {
						lStrChallanInstFrom += StringUtility.getParameter("txtInstlmntChallanFrom" + lIntCnt, request)
								+ ",";
						lStrChallanInstTo += StringUtility.getParameter("txtInstlmntChallanTo" + lIntCnt, request)
								+ ",";
					}
				}
			}

			lArrStrVoucherChallan = lStrVoucherChallan.split(",");
			lArrStrChallanInstFrom = lStrChallanInstFrom.split(",");
			lArrStrChallanInstTo = lStrChallanInstTo.split(",");

			if (lArrStrInstallmentAmt.length > 0) {
				for (int lIntcnt = 0; lIntcnt < lArrStrInstallmentAmt.length; lIntcnt++) {
					if (lArrStrVoucherChallan[lIntcnt].equals("V")) {

						if (lArrStrVoucherChallnPk[lIntcnt] != null
								&& lArrStrVoucherChallnPk[lIntcnt].trim().length() > 0) {
							lStrSaveOrUpdateVchr = lStrSaveOrUpdateVchr + "2,";
							lObjMstGpfMonthly = (MstGpfMonthly) lObjGpfDataEntryFormDAOMon.read(Long
									.parseLong(lArrStrVoucherChallnPk[lIntcnt]));
						} else {
							lStrSaveOrUpdateVchr = lStrSaveOrUpdateVchr + "1,";
							lObjMstGpfMonthly = new MstGpfMonthly();
						}

						if (lArrStrVoucherChallnNo[lIntcnt] != null
								&& lArrStrVoucherChallnNo[lIntcnt].trim().length() > 0) {
							lObjMstGpfMonthly.setVoucherNo(lArrStrVoucherChallnNo[lIntcnt]);
						}
						if (lArrStrVoucherChallnDate[lIntcnt] != null
								&& lArrStrVoucherChallnDate[lIntcnt].trim().length() > 0) {
							lObjMstGpfMonthly.setVoucherDate(lObjDateFormate.parse(lArrStrVoucherChallnDate[lIntcnt]));
						}
						if (lArrStrMonth[lIntcnt] != null && lArrStrMonth[lIntcnt].trim().length() > 0) {
							lObjMstGpfMonthly.setMonthId(Long.parseLong(lArrStrMonth[lIntcnt]));
						}
						if (lArrStrYear[lIntcnt] != null && lArrStrYear[lIntcnt].trim().length() > 0) {
							lObjMstGpfMonthly.setFinYearId(Long.parseLong(lArrStrYear[lIntcnt]));
						}
						if (lArrStrInstallmentAmt[lIntcnt] != null
								&& lArrStrInstallmentAmt[lIntcnt].trim().length() > 0) {
							lObjMstGpfMonthly.setAdvanceRecovery(Double.parseDouble(lArrStrInstallmentAmt[lIntcnt]));
						}
						if (lArrStrVchrInstNo[lIntVchrCnt] != null
								&& lArrStrVchrInstNo[lIntVchrCnt].trim().length() > 0) {
							lObjMstGpfMonthly.setInstNo(Integer.parseInt(lArrStrVchrInstNo[lIntVchrCnt]));
						}
						if (!lStrNoOfInstlmnt.equals("")) {
							lObjMstGpfMonthly.setTotalInst(Integer.parseInt(lStrNoOfInstlmnt));
						}

						lObjMstGpfMonthly.setCreatedDate(gDtCurrDt);
						lObjMstGpfMonthly.setCreatedPostId(gLngPostId);
						lObjMstGpfMonthly.setCreatedUserId(gLngUserId);
						if (lArrStrMonthlySubscription[lIntcnt] != null
								&& lArrStrMonthlySubscription[lIntcnt].trim().length() > 0) {
						lObjMstGpfMonthly.setRegularSubscription(Double.parseDouble(lArrStrMonthlySubscription[lIntcnt]));
						}
						lObjMstGpfMonthly.setDataEntry("1");
						lLstVoucherDtls.add(lObjMstGpfMonthly);

						lIntVchrCnt++;
					} else if (lArrStrVoucherChallan[lIntcnt].equals("C")) {
						if (lArrStrVoucherChallnPk[lIntcnt] != null
								&& lArrStrVoucherChallnPk[lIntcnt].trim().length() > 0) {
							lStrSaveOrUpdateChalln = lStrSaveOrUpdateChalln + "2,";
							lObjMstGpfChallanDtls = (MstGpfChallanDtls) lObjGpfDataEntryFormDAOChln.read(Long
									.parseLong(lArrStrVoucherChallnPk[lIntcnt]));
						} else {
							lStrSaveOrUpdateChalln = lStrSaveOrUpdateChalln + "1,";
							lObjMstGpfChallanDtls = new MstGpfChallanDtls();
						}

						// lStrChallanInstFrom =
						// StringUtility.getParameter("txtInstlmntChallanFrom"+lIntcnt,request);
						// lStrChallanInstTo =
						// StringUtility.getParameter("txtInstlmntChallanTo"+lIntcnt,request);

						if (lArrStrVoucherChallnNo[lIntcnt] != null
								&& lArrStrVoucherChallnNo[lIntcnt].trim().length() > 0) {
							lObjMstGpfChallanDtls.setChallanNo(lArrStrVoucherChallnNo[lIntcnt]);
						}
						if (lArrStrVoucherChallnDate[lIntcnt] != null
								&& lArrStrVoucherChallnDate[lIntcnt].trim().length() > 0) {
							lObjMstGpfChallanDtls.setChallanDate(lObjDateFormate
									.parse(lArrStrVoucherChallnDate[lIntcnt]));
						}
						if (lArrStrInstallmentAmt[lIntcnt] != null
								&& lArrStrInstallmentAmt[lIntcnt].trim().length() > 0) {
							lObjMstGpfChallanDtls.setAmount(Double.parseDouble(lArrStrInstallmentAmt[lIntcnt]));
						}
						if (lArrStrChallanInstFrom[lIntChlnCnt] != null
								&& lArrStrChallanInstFrom[lIntChlnCnt].trim().length() > 0) {
							lObjMstGpfChallanDtls.setInstFrom(Integer.parseInt(lArrStrChallanInstFrom[lIntChlnCnt]));
						}
						if (lArrStrChallanInstTo[lIntChlnCnt] != null
								&& lArrStrChallanInstTo[lIntChlnCnt].trim().length() > 0) {
							lObjMstGpfChallanDtls.setInstTo(Integer.parseInt(lArrStrChallanInstTo[lIntChlnCnt]));
						}

						lObjMstGpfChallanDtls.setChallanType(Long.parseLong(gObjRsrcBndle
								.getString("GPF.PREPAYOFADVANCE")));
						lObjMstGpfChallanDtls.setCreatedDate(gDtCurrDt);
						lObjMstGpfChallanDtls.setCreatedPostId(gLngPostId);
						lObjMstGpfChallanDtls.setCreatedUserId(gLngUserId);

						lLstChallanDtls.add(lObjMstGpfChallanDtls);

						lIntChlnCnt++;
					}
				}
			}
			lScheduleVOMap.put("lLstChallanDtls", lLstChallanDtls);
			lScheduleVOMap.put("lLstVoucherDtls", lLstVoucherDtls);
			inputMap.put("isSaveOrUpdateChln", lStrSaveOrUpdateChalln);
			inputMap.put("isSaveOrUpdateVchr", lStrSaveOrUpdateVchr);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			throw e;
		}
		return lScheduleVOMap;
	}
}
