/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 16, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.BptmCommonServicesDAO;
import com.tcs.sgv.common.dao.BptmCommonServicesDAOImpl;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ExcelExportHelper;
import com.tcs.sgv.common.helper.ReportExportHelper;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pension.valueobject.TrnPrvosionalPensionDtls;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionBillDAO;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.LifeCertificateVO;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerNomineeDtls;
import com.tcs.sgv.pensionpay.valueobject.PensionAuditRegisterVO;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCorrectionDtls;

import edu.emory.mathcs.backport.java.util.Arrays;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Aug 16, 2011
 */
public class PensionpayReportServiceImpl extends ServiceImpl {

	/** Global Variable for Logger Class */
	private static final Logger gLogger = Logger.getLogger("PensionpayReports");

	private PensionpayQueryDAO gObjRptQueryDAO = null;

	/** Global Variable for ResourceBundle */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for Location Code */
	String gStrLocCode = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");
	private ResourceBundle gObjLblRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels");
	/*
	 * / Global variable initiation
	 */
	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gDtCurrDt = DBUtility.getCurrentDateFromDB();
	}

	public ResultObject loadBankPaymentReport(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			List lLstAuditorName = new ArrayList();
			List<ComboValuesVO> lLstBankName = new ArrayList<ComboValuesVO>();
			List<ComboValuesVO> lLstMonths = new ArrayList<ComboValuesVO>();
			List<ComboValuesVO> lLstYears = new ArrayList<ComboValuesVO>();
			List<ComboValuesVO> lLstReportingBanks = new ArrayList<ComboValuesVO>();
			List<String> lLstSchemeCodes = new ArrayList<String>();

			PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			MonthlyPensionBillDAO lObjMonthlyPnsnBillDAO = new MonthlyPensionBillDAOImpl(null, serv.getSessionFactory());
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());

			String lStrRoleId = bundleConst.getString("AUDITOR.ROLEID");
			lLstAuditorName = lObjPensionBillDAO.getAuditorsListFromRoleID(Long.parseLong(lStrRoleId), gStrLocCode);
			lLstBankName = lObjCommonPensionDAO.getAuditorBankCodeList(gLngPostId, gStrLocCode);
			lLstMonths = lObjCommonDAO.getMonthList(SessionHelper.getLocale(request));
			lLstYears = lObjCommonDAO.getYearList(SessionHelper.getLocale(request));
			lLstReportingBanks = lObjCommonDAO.getReportingBankList(gStrLocCode, gLngLangId);
			// lLstSchemeCodes = lObjMonthlyPnsnBillDAO.getAllSchemeCode();

			inputMap.put("AuditorName", lLstAuditorName);
			inputMap.put("BankName", lLstBankName);
			inputMap.put("MonthList", lLstMonths);
			inputMap.put("YearList", lLstYears);
			inputMap.put("SchemeCodeList", lLstSchemeCodes);
			inputMap.put("ReportingBankList", lLstReportingBanks);

			resObj.setViewName("BankPaymentReport");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	public ResultObject getBankPaymentReport(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSmplDateFormat = new SimpleDateFormat("MMMM yyyy ");
		SimpleDateFormat lObjSmplDtFormat = new SimpleDateFormat("MMM-yyyy ");
		SessionFactory lObjSessionFactory = null;
		Session lhibSession = null;
		String lStrTreasuryName = "";
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrMonth = null;
		String lStrYear = null;
		String lStrForMonthYear = null;
		String lStrAudPostId = null;
		String lStrAudBankFlag = null;
		String lStrPaymentMode = null;
		String lStrExportTo = "";
		String lStrFromDate = "";
		String lStrToDate = "";
		String lStrBillType = "";
		String lStrOrderBy = "";
		Date lDtFromDate = null;
		Date lDtToDate = null;
		String lStrSchemeCode = "";
		String lStrBillDate = "";
		Date lDtBillDate = null;
		String lStrReportingBankCode = "";
		String lStrReportingBranchCode = "";
		List<String> lLstPaymentMode = new ArrayList<String>();
		try {
			List<Object[]> lLstResult = new ArrayList<Object[]>();
			lObjSessionFactory = serv.getSessionFactorySlave();

			lhibSession = lObjSessionFactory.getCurrentSession();
			lStrBankCode = StringUtility.getParameter("BankCode", request);
			lStrBranchCode = StringUtility.getParameter("BranchCode", request);
			lStrMonth = StringUtility.getParameter("ForMonth", request);
			lStrYear = StringUtility.getParameter("ForYear", request);
			lStrAudPostId = StringUtility.getParameter("AudPostId", request);
			lStrAudBankFlag = StringUtility.getParameter("AudBankFlag", request);
			lStrPaymentMode = StringUtility.getParameter("PaymentMode", request);
			lStrExportTo = StringUtility.getParameter("ExportTo", request);
			lStrFromDate = StringUtility.getParameter("fromDate", request);
			lStrToDate = StringUtility.getParameter("toDate", request);
			lStrBillType = StringUtility.getParameter("billType", request);
			lStrSchemeCode = StringUtility.getParameter("schemeCode", request);
			lStrOrderBy = StringUtility.getParameter("orderBy", request);
			lStrReportingBankCode = StringUtility.getParameter("rptBankCode", request);
			lStrReportingBranchCode = StringUtility.getParameter("rptBranchCode", request);

			if (!"".equals(lStrFromDate)) {
				lDtFromDate = lObjSimpleDateFormat.parse(lStrFromDate);
			}

			if (!"".equals(lStrToDate)) {
				lDtToDate = lObjSimpleDateFormat.parse(lStrToDate);
			}

			if (gObjRsrcBndle.getString("PPMT.PAYMODEECS").equalsIgnoreCase(lStrPaymentMode)) {
				lLstPaymentMode.add(gObjRsrcBndle.getString("PPMT.PAYMODEECS"));
			} else if (gObjRsrcBndle.getString("PPMT.PAYMODECHQ").equalsIgnoreCase(lStrPaymentMode)) {
				lLstPaymentMode.add(gObjRsrcBndle.getString("PPMT.PAYMODECHQ"));
			} else {
				lLstPaymentMode.add(gObjRsrcBndle.getString("PPMT.PAYMODEECS"));
				lLstPaymentMode.add(gObjRsrcBndle.getString("PPMT.PAYMODECHQ"));
			}
			if (lStrMonth.length() > 1) {
				lStrForMonthYear = lStrYear + lStrMonth;
			} else {
				lStrForMonthYear = lStrYear + "0" + lStrMonth;
			}
			PensionpayQueryDAO lObjPensionPayQueryDAO = new PensionpayQueryDAOImpl(null, lObjSessionFactory);
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocCode));
			Map lDetailMap = new HashMap();
			StringBuilder lSbHeader = null;
			List<List<Object>> excelArrOuter = new ArrayList<List<Object>>();
			List<List<Object>> arrOuter = new ArrayList<List<Object>>();
			// lStrExportTo = DBConstants.DIS_ONSCREEN;
			if (bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType)) {
				lLstResult = lObjPensionPayQueryDAO.getBankPaymentDtlsForMonthly(lStrBankCode, lStrBranchCode, lStrForMonthYear, lStrAudPostId, gStrLocCode, lStrAudBankFlag, lLstPaymentMode,
						lStrExportTo, lDtFromDate, lDtToDate, lStrBillType, lStrSchemeCode, lStrOrderBy, lStrReportingBankCode, lStrReportingBranchCode);
				lStrBillDate = "01/" + lStrForMonthYear.substring(4) + "/" + lStrForMonthYear.substring(0, 4);
				lDtBillDate = lObjSimpleDateFormat.parse(lStrBillDate);
			} else {
				lLstResult = lObjPensionPayQueryDAO.getBankPaymentDtls(lStrBankCode, lStrBranchCode, lStrForMonthYear, lStrAudPostId, gStrLocCode, lStrAudBankFlag, lLstPaymentMode, lStrExportTo,
						lDtFromDate, lDtToDate, lStrBillType, lStrOrderBy);
			}
			lSbHeader = lObjPensionPayQueryDAO.getReportHeader(gStrLocCode);
			Map headingMap = new HashMap();
			Map<String, Map<String, ArrayList>> lBankMap = new TreeMap<String, Map<String, ArrayList>>();
			Map<String, ArrayList> lBranchMap = new TreeMap<String, ArrayList>();
			Map<String, Map<String, String>> lChequeMap = new TreeMap<String, Map<String, String>>();
			Map<String, String> lChequeDtlsMap = new TreeMap<String, String>();

			Map<String, String> lMapBankDtls = new TreeMap<String, String>();
			Map<String, String> lMapBranchDtls = new TreeMap<String, String>();

			for (Object[] cols : lLstResult) {
				String mainKey = cols[6].toString();
				String lLngChequeAmt = null;
				Double lDbChequeAmt = 0D;
				String key = cols[6].toString() + " - " + cols[7].toString();

				/********** heading map *************/
				if (!headingMap.containsKey(key)) {
					headingMap.put(key, "Bank Name : " + cols[4].toString() + "     " + "Branch Name : " + cols[5].toString());
				}

				lMapBankDtls.put(cols[6].toString(), cols[4].toString());
				lMapBranchDtls.put(cols[7].toString(), cols[5].toString());
				lBranchMap = lBankMap.get(mainKey);
				if (lBranchMap != null) {
					ArrayList dataList = lBranchMap.get(key);
					Map chequeDtlsMap = lChequeMap.get(key);

					if (dataList != null) {
						dataList.add(cols);
						if (chequeDtlsMap != null) {
							if (cols[9] != null) {
								chequeDtlsMap.put("ChqNo", cols[9].toString());
							} else {
								chequeDtlsMap.put("ChqNo", "");
							}
							if (cols[10] != null) {
								chequeDtlsMap.put("ChqAmt", cols[10].toString());
							} else {
								chequeDtlsMap.put("ChqAmt", "");
							}
							if (cols[11] != null) {
								chequeDtlsMap.put("ChqDt", lObjSimpleDateFormat.format(cols[11]));
							} else {
								chequeDtlsMap.put("ChqDt", "");
							}
							if (cols[10] != null) {
								lDbChequeAmt = Double.parseDouble(cols[10].toString());
								chequeDtlsMap.put("ChqAmtWds", EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDbChequeAmt)));
							} else {
								chequeDtlsMap.put("ChqAmtWds", "");
							}
							lChequeMap.put(key, chequeDtlsMap);
						} else {
							Map<String, String> chequeDtls = new TreeMap<String, String>();
							if (cols[9] != null) {
								chequeDtls.put("ChqNo", cols[9].toString());
							} else {
								chequeDtls.put("ChqNo", "");
							}
							if (cols[10] != null) {
								chequeDtls.put("ChqAmt", cols[10].toString());
							} else {
								chequeDtls.put("ChqAmt", "");
							}
							if (cols[11] != null) {
								chequeDtls.put("ChqDt", lObjSimpleDateFormat.format(cols[11]));
							} else {
								chequeDtls.put("ChqDt", "");
							}
							if (cols[10] != null) {
								lDbChequeAmt = Double.parseDouble(cols[10].toString());
								chequeDtls.put("ChqAmtWds", EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDbChequeAmt)));
							} else {
								chequeDtls.put("ChqAmtWds", "");
							}
							lChequeMap.put(key, chequeDtls);

						}
					} else {
						if (chequeDtlsMap != null) {
							if (cols[9] != null) {
								chequeDtlsMap.put("ChqNo", cols[9].toString());
							} else {
								chequeDtlsMap.put("ChqNo", "");
							}
							if (cols[10] != null) {
								chequeDtlsMap.put("ChqAmt", cols[10].toString());
							} else {
								chequeDtlsMap.put("ChqAmt", "");
							}
							if (cols[11] != null) {
								chequeDtlsMap.put("ChqDt", lObjSimpleDateFormat.format(cols[11]));
							} else {
								chequeDtlsMap.put("ChqDt", "");
							}
							if (cols[10] != null) {
								lDbChequeAmt = Double.parseDouble(cols[10].toString());
								chequeDtlsMap.put("ChqAmtWds", EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDbChequeAmt)));
							} else {
								chequeDtlsMap.put("ChqAmtWds", "");
							}
							lChequeMap.put(key, chequeDtlsMap);
						} else {
							Map<String, String> chequeDtls = new TreeMap<String, String>();
							if (cols[9] != null) {
								chequeDtls.put("ChqNo", cols[9].toString());
							} else {
								chequeDtls.put("ChqNo", "");
							}
							if (cols[10] != null) {
								chequeDtls.put("ChqAmt", cols[10].toString());
							} else {
								chequeDtls.put("ChqAmt", "");
							}
							if (cols[11] != null) {
								chequeDtls.put("ChqDt", lObjSimpleDateFormat.format(cols[11]));
							} else {
								chequeDtls.put("ChqDt", "");
							}
							if (cols[10] != null) {
								lDbChequeAmt = Double.parseDouble(cols[10].toString());
								chequeDtls.put("ChqAmtWds", EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDbChequeAmt)));
							} else {
								chequeDtls.put("ChqAmtWds", "");
							}
							lChequeMap.put(key, chequeDtls);

						}
						dataList = new ArrayList();
						dataList.add(cols);
					}
					lBranchMap.put(key, dataList);
				} else {

					lBranchMap = new TreeMap<String, ArrayList>();
					ArrayList dataList = new ArrayList();
					dataList.add(cols);

					lBranchMap.put(key, dataList);
					lBankMap.put(mainKey, lBranchMap);
					Map<String, String> chequeDtls = new TreeMap<String, String>();
					if (cols[9] != null) {
						chequeDtls.put("ChqNo", cols[9].toString());
					} else {
						chequeDtls.put("ChqNo", "");
					}
					if (cols[10] != null) {
						chequeDtls.put("ChqAmt", cols[10].toString());
					} else {
						chequeDtls.put("ChqAmt", "");
					}
					if (cols[11] != null) {
						chequeDtls.put("ChqDt", lObjSimpleDateFormat.format(cols[11]));
					} else {
						chequeDtls.put("ChqDt", "");
					}
					if (cols[10] != null) {
						lDbChequeAmt = Double.parseDouble(cols[10].toString());
						chequeDtls.put("ChqAmtWds", EnglishDecimalFormat.convertWithSpace(new BigDecimal(lDbChequeAmt)));
					} else {
						chequeDtls.put("ChqAmtWds", "");
					}
					lChequeMap.put(key, chequeDtls);
				}
			}
			String lStrDisplay = "";
			String lStrBranchDisplay = "";
			if (bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType)) {
				lSbHeader.append("Bank Payment Report for the month of " + lObjSmplDateFormat.format(lDtBillDate) + "\r\n");
			} else {
				lSbHeader.append("Bank Payment Report \r\n");
			}
			String lStrHeader = "";
			// lStrHeader = lStrHeader +"\r\n";

			String strParameterString = "";
			String lStrFooter = "";
			int nMode = 0;
			if (bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType)) {
				nMode = 80;
			} else {
				nMode = 132;
			}
			boolean pageBreakonGrouping = false;
			ReportExportHelper objExport = new ReportExportHelper();
			// Map returnMap = new HashMap();
			ArrayList lineList = new ArrayList();
			StringBuffer sbLine = new StringBuffer();

			byte[] aryData = null;
			List maindata = new ArrayList();
			List columndata = new ArrayList();
			String[] param = new String[10];
			List Headerdata = new ArrayList();
			List footerdata = new ArrayList();
			int s = 0;
			Long lLngTotalAmount = 0L;

			for (int i = 0; i < 80; i++) {
				sbLine.append("-");
			}
			lineList.add(sbLine.toString());
			lineList.add("");
			lineList.add("");
			lineList.add("");
			lineList.add("");

			ArrayList blankList = new ArrayList();
			blankList.add("");
			blankList.add("");
			blankList.add("");
			blankList.add("");
			blankList.add("");

			List<List<List<Object>>> lLstExcelOuter = new ArrayList<List<List<Object>>>();

			ColumnVo[] columnHeading = null;
			if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo) && bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType)) {
				columnHeading = new ColumnVo[11];
				columnHeading[0] = new ColumnVo("Sr. No.", 1, 7, 0, false, false, true, 1);
				columnHeading[1] = new ColumnVo("PPO Number", 1, 19, 0, true, false, true, 1);
				columnHeading[2] = new ColumnVo("Pensioner Name", 1, 25, 0, true, false, true, 1);
				columnHeading[3] = new ColumnVo("Net Amount", 2, 9, 1, false, true, true, 4);
				columnHeading[4] = new ColumnVo("Month - Year", 1, 15, 0, true, false, true, 1);
				columnHeading[5] = new ColumnVo("Treasury Name", 1, 25, 0, true, false, true, 1);
				columnHeading[6] = new ColumnVo("Bank Name", 1, 25, 0, true, false, true, 1);
				columnHeading[7] = new ColumnVo("Branch Name", 1, 25, 0, true, false, true, 1);
				columnHeading[8] = new ColumnVo("Account Number", 2, 18, 0, false, false, true, 1);
				columnHeading[9] = new ColumnVo("Cheque Number", 1, 18, 0, false, false, true, 1);
				columnHeading[10] = new ColumnVo("Cheque Date", 1, 15, 0, false, false, true, 1);

			}
			if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo)
					&& (bundleConst.getString("RECOVERY.PENSION").equals(lStrBillType) || bundleConst.getString("RECOVERY.DCRG").equals(lStrBillType)
							|| bundleConst.getString("RECOVERY.CVP").equals(lStrBillType) || bundleConst.getString("RECOVERY.SUPP").equals(lStrBillType))) {
				columnHeading = new ColumnVo[10];
				columnHeading[0] = new ColumnVo("Sr. No.", 1, 7, 0, false, false, true, 1);
				columnHeading[1] = new ColumnVo("PPO Number", 1, 19, 0, true, false, true, 1);
				columnHeading[2] = new ColumnVo("Pensioner Name", 1, 25, 0, true, false, true, 1);
				columnHeading[3] = new ColumnVo("Net Amount", 2, 9, 1, false, true, true, 4);
				columnHeading[4] = new ColumnVo("Treasury Name", 1, 25, 0, true, false, true, 1);
				columnHeading[5] = new ColumnVo("Bank Name", 1, 25, 0, true, false, true, 1);
				columnHeading[6] = new ColumnVo("Branch Name", 1, 25, 0, true, false, true, 1);
				columnHeading[7] = new ColumnVo("Account Number", 2, 18, 0, false, false, true, 1);
				columnHeading[8] = new ColumnVo("Cheque Number", 1, 18, 0, false, false, true, 1);
				columnHeading[9] = new ColumnVo("Cheque Date", 1, 15, 0, false, false, true, 1);

			}
			if (!(DBConstants.DIS_EXCELFILE).equals(lStrExportTo) && bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType)) {
				columnHeading = new ColumnVo[5];
				columnHeading[0] = new ColumnVo("Sr. No.", 1, 4, 0, false, false, true);
				columnHeading[1] = new ColumnVo("PPO Number", 1, 19, 0, true, false, true);
				columnHeading[2] = new ColumnVo("Pensioner Name", 1, 25, 0, true, false, true);
				columnHeading[3] = new ColumnVo("Net Amount", 2, 9, 0, false, false, true);
				columnHeading[4] = new ColumnVo("Account Number", 2, 18, 0, false, false, true);
			}
			if (!(DBConstants.DIS_EXCELFILE).equals(lStrExportTo) && !bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType)) {
				columnHeading = new ColumnVo[7];
				columnHeading[0] = new ColumnVo("Sr. No.", 1, 4, 0, false, false, true);
				columnHeading[1] = new ColumnVo("PPO Number", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("Pensioner Name", 1, 35, 0, true, false, true);
				columnHeading[3] = new ColumnVo("Net Amount", 2, 11, 0, false, false, true);
				columnHeading[4] = new ColumnVo("Account Number", 2, 20, 0, false, false, true);
				columnHeading[5] = new ColumnVo("Cheque Number", 1, 19, 0, false, false, true);
				columnHeading[6] = new ColumnVo("Cheque Date", 1, 16, 0, false, false, true);
			}
			int lIntFlag = 0;
			Integer lIntSrNo = 0;
			for (String mainKey : lBankMap.keySet()) {
				Map<String, ArrayList> dataMap = lBankMap.get(mainKey);

				boolean mHeadFlag = true;
				String lStrBranchKey = "";
				for (String key : dataMap.keySet()) {
					Integer lIntSerialNo = 0;
					String lStrChequeNo = null;
					String lStrChequeDate = null;
					arrOuter = new ArrayList<List<Object>>();
					// excelArrOuter = new ArrayList<List<Object>>();
					Double lDbChequeAmt = 0D;
					ArrayList<Object[]> dataList = dataMap.get(key);
					for (Object[] cols : dataList) {
						lIntSerialNo++;
						lIntSrNo++;
						if (mHeadFlag) {
							lStrHeader = (String) headingMap.get(key);
							mHeadFlag = false;
							// Headerdata.add(lSbHeader.toString()+lStrHeader);
						}

						List inList = new ArrayList();
						if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo)) {
							inList.add(lIntSrNo);
						} else {
							inList.add(lIntSerialNo);
						}
						inList.add(cols[0]);
						inList.add(cols[1]);
						inList.add(Math.round(Double.parseDouble(cols[2].toString())));
						lDbChequeAmt = lDbChequeAmt + Math.round(Double.parseDouble(cols[2].toString()));
						lLngTotalAmount = lLngTotalAmount + Math.round(Double.parseDouble(cols[2].toString()));
						if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo) && bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType)) {
							inList.add(lObjSmplDtFormat.format(lDtBillDate));
						}
						if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo)
								&& (bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType) || bundleConst.getString("RECOVERY.PENSION").equals(lStrBillType)
										|| bundleConst.getString("RECOVERY.DCRG").equals(lStrBillType) || bundleConst.getString("RECOVERY.CVP").equals(lStrBillType) || bundleConst.getString(
										"RECOVERY.SUPP").equals(lStrBillType))) {

							inList.add(lStrTreasuryName);
							inList.add(lMapBankDtls.get(cols[6].toString()));
							inList.add(lMapBranchDtls.get(cols[7].toString()));
						}

						inList.add(cols[3]);
						if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo) && bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType)) {
							lChequeDtlsMap = lChequeMap.get(key);
							inList.add(lChequeDtlsMap.get("ChqNo"));
							inList.add(lChequeDtlsMap.get("ChqDt"));
						}
						if (bundleConst.getString("RECOVERY.PENSION").equals(lStrBillType) || bundleConst.getString("RECOVERY.DCRG").equals(lStrBillType)
								|| bundleConst.getString("RECOVERY.CVP").equals(lStrBillType) || bundleConst.getString("RECOVERY.SUPP").equals(lStrBillType)) {
							inList.add(cols[9]);
							if (cols[11] != null) {
								inList.add(lObjSimpleDateFormat.format(cols[11]));
							} else {
								inList.add("");
							}
						}

						lStrBranchKey = key;
						arrOuter.add(inList);
						excelArrOuter.add(inList);

						lStrBranchDisplay = objExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString() + lStrHeader, lStrFooter, null, nMode, true, 1, true, "|");

					}

					lStrHeader = "";
					StringBuffer lStrChequeDtls = new StringBuffer();

					if (!(DBConstants.DIS_EXCELFILE).equals(lStrExportTo) && bundleConst.getString("RECOVERY.MONTHLY").equals(lStrBillType)) {
						lChequeDtlsMap = lChequeMap.get(lStrBranchKey);

						lStrChequeDtls.append(String.format("%-40s", "Cheque Number  " + lChequeDtlsMap.get("ChqNo")));
						lStrChequeDtls.append(String.format("%-40s", "Cheque Date  " + lChequeDtlsMap.get("ChqDt")));
						lStrChequeDtls.append("\r\n");
						lStrChequeDtls.append(String.format("%-40s", "Cheque Amount  " + lDbChequeAmt.longValue()));
						lStrChequeDtls.append("\r\n");
						lStrChequeDtls.append(String.format("%-40s", "Amount in word " + lChequeDtlsMap.get("ChqAmtWds")+ " Only"));
						lStrChequeDtls.append("\r\n");
						for (int i = 0; i < nMode; i++) {
							lStrChequeDtls.append("-");
						}
					}
					// if(!(DBConstants.DIS_EXCELFILE).equals(lStrExportTo))
					// {
					// for(int i = 0 ; i < 80; i++) lStrChequeDtls.append("-");
					// }
					lStrChequeDtls.append((char) 12);
					lStrDisplay = lStrDisplay + lStrBranchDisplay + lStrChequeDtls.toString();
					mHeadFlag = true;
					// footerdata.add(lStrChequeDtls.toString());

				}

			}

			if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo)) {
				List inList = new ArrayList();
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				excelArrOuter.add(inList);
				inList = new ArrayList();
				inList.add("Total");
				inList.add("");
				inList.add("");
				inList.add(lLngTotalAmount);
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				inList.add("");
				excelArrOuter.add(inList);
				lLstExcelOuter.add(excelArrOuter);

				columndata.add(columnHeading);
				maindata.add(lLstExcelOuter);
				param[s] = strParameterString;

				ExcelExportHelper exph = new ExcelExportHelper();

				inputMap.put("FileName", "BankPaymentReport");
				aryData = exph.getExcelBankPmntReportPrintFormat(maindata, columndata, param, Headerdata, footerdata);

				lDetailMap.put(DBConstants.DIS_EXCELFILE, aryData);
			} else if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_ONSCREEN, lStrDisplay);
			} else if ((DBConstants.DIS_TEXTFILE).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_TEXTFILE, lStrDisplay);
			}

			ReportExportHelper rptExpHelper = new ReportExportHelper();
			rptExpHelper.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	private String getChar(int count, String ele) {

		StringBuffer lSBSpace = new StringBuffer();
		for (int i = 0; i < count - 1; i++) {
			lSBSpace.append(ele);
		}
		return lSBSpace.toString();
	}

	public ResultObject printChangeStatement(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		Map output = new HashMap();
		String strOut = "";
		StringBuffer strOut1 = new StringBuffer();
		List arraylistOuter = null;
		List dataList = new ArrayList();
		String exportTo = DBConstants.DIS_ONSCREEN;
		StringBuilder lSBOut = new StringBuilder();
		Map lMapSeriesHeadCode = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		int noOfLinesPrintedOnPage = 1;
		String lineSeperator = "\r\n";
		String[] lParaArrStrChngeStmntDtls = null;
		String[] lArrStrChngeStmntDtls = null;
		String lStrChngRqstId = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrForMonth = null;
		Integer lIntCurrMonthYear = 0;
		Integer lIntPrevMonthYear = 0;
		String lStrCurrMonthYear = "";
		String lStrPrevMonthYear = "";
		SimpleDateFormat lSdf1 = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lSdf2 = new SimpleDateFormat("MMM-yy");
		char pageBreak = 12;
		StringBuilder lSbHeader = null;
		String lStrHeader = "";
		String lStrFooter = "";
		String lStrHeaderBankBranch = "";
		String lStrHeaderMonthYear = "";
		String lStrParameterString = "";
		String lStrEmpName = "";
		PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());
		BptmCommonServicesDAO lObjBptmCommonServicesDAO = new BptmCommonServicesDAOImpl(null, serv.getSessionFactory());
		List rowList = new ArrayList();
		List<String> lLstAllChngRqstIds = null;
		String lStrBillNo = null;
		String lStrSchemeCode = null;
		String lFlagGenChngStmntBy = null;
		try {
			setSessionInfo(inputMap);
			lParaArrStrChngeStmntDtls = StringUtility.getParameterValues("changeRqstDtls", request);
			lStrBillNo = StringUtility.getParameter("billNo", request).trim();
			lStrSchemeCode = (StringUtility.getParameter("schemeCode", request).trim().length() > 0) ? StringUtility.getParameter("schemeCode", request).trim() : null;
			lStrForMonth = StringUtility.getParameter("forMonth", request).trim();
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lMapSeriesHeadCode = lObjCommonPensionDAO.getAllHeadCodeSeriesMap();
			ChangeStatement lObjChangeStmnt = new ChangeStatement(serv.getSessionFactory(), lMapSeriesHeadCode, Long.valueOf(gStrLocCode));
			if (lStrBillNo != null && lStrBillNo.length() > 0) {
				lFlagGenChngStmntBy = "S";
				lLstAllChngRqstIds = lObjChangeStmnt.getChangeRqstIdsForOuter(lStrBillNo);
				lParaArrStrChngeStmntDtls = new String[lLstAllChngRqstIds.size()];
				for (int i = 0; i < lLstAllChngRqstIds.size(); i++) {
					lParaArrStrChngeStmntDtls[i] = lLstAllChngRqstIds.get(i);
				}
			} else {
				lFlagGenChngStmntBy = "B";
			}
			lStrEmpName = lObjBptmCommonServicesDAO.getEmpNameFrmUserId(gLngUserId.toString(), gLngLangId);
			lSbHeader = lObjPensionpayQueryDAO.getReportHeader(gStrLocCode);
			lSbHeader.append("Pension Change Statement" + "\r\n");
			int nMode = 131;
			boolean pageBreakonGrouping = false;
			ReportExportHelper objExport = new ReportExportHelper();

			ColumnVo[] summaryRptColumnHeading = new ColumnVo[7];
			summaryRptColumnHeading[0] = new ColumnVo("Bank Name", 1, 33, 0, true, false, true);
			summaryRptColumnHeading[1] = new ColumnVo("Branch Name", 1, 33, 0, true, false, true);
			summaryRptColumnHeading[6] = new ColumnVo("Diff. Amount", 2, 14, 0, false, false, true);

			ColumnVo[] chngStmntDtlRptColumnHeading = new ColumnVo[18];
			chngStmntDtlRptColumnHeading[0] = new ColumnVo("Sr. No", 2, 4, 0, false, false, true);
			chngStmntDtlRptColumnHeading[1] = new ColumnVo("Category-PPO No", 1, 10, 0, true, false, true);
			chngStmntDtlRptColumnHeading[2] = new ColumnVo("Volume No", 2, 5, 0, false, false, true);
			chngStmntDtlRptColumnHeading[3] = new ColumnVo("Page No", 2, 5, 0, false, false, true);
			chngStmntDtlRptColumnHeading[4] = new ColumnVo("Pensioner Name", 1, 12, 0, true, false, true);
			chngStmntDtlRptColumnHeading[5] = new ColumnVo("Month", 1, 6, 0, false, false, true);
			chngStmntDtlRptColumnHeading[6] = new ColumnVo("Basic", 2, 6, 0, false, false, true);
			chngStmntDtlRptColumnHeading[7] = new ColumnVo("ADP", 2, 6, 0, false, false, true);
			chngStmntDtlRptColumnHeading[8] = new ColumnVo("DP", 2, 6, 0, false, false, true);
			chngStmntDtlRptColumnHeading[9] = new ColumnVo("DA", 2, 6, 0, false, false, true);
			chngStmntDtlRptColumnHeading[10] = new ColumnVo("IR1", 2, 5, 0, false, false, true);
			chngStmntDtlRptColumnHeading[11] = new ColumnVo("IR2", 2, 5, 0, false, false, true);
			chngStmntDtlRptColumnHeading[12] = new ColumnVo("IR3", 2, 5, 0, false, false, true);
			chngStmntDtlRptColumnHeading[13] = new ColumnVo("Diff.", 2, 6, 0, false, false, true);
			chngStmntDtlRptColumnHeading[14] = new ColumnVo("CVP Monthly", 2, 6, 0, false, false, true);
			chngStmntDtlRptColumnHeading[15] = new ColumnVo("Gross", 2, 7, 0, false, false, true);
			chngStmntDtlRptColumnHeading[16] = new ColumnVo("Recovery", 2, 6, 0, false, false, true);
			chngStmntDtlRptColumnHeading[17] = new ColumnVo("Net", 2, 7, 0, false, false, true);

			for (int i = 0; i < lParaArrStrChngeStmntDtls.length; i++) {
				lArrStrChngeStmntDtls = lParaArrStrChngeStmntDtls[i].split("~");
				lStrChngRqstId = lArrStrChngeStmntDtls[0];
				if ("B".equals(lFlagGenChngStmntBy)) {
					lStrForMonth = lArrStrChngeStmntDtls[3];
				}
				// lStrBankCode = lArrStrChngeStmntDtls[1];
				// lStrBranchCode = lArrStrChngeStmntDtls[2];
				if (!"".equals(lStrForMonth)) {
					lIntCurrMonthYear = Integer.parseInt(lStrForMonth);
					if ((lStrForMonth.substring(4, 6)).equals("01")) {
						lIntPrevMonthYear = lIntCurrMonthYear - 89;
					} else {
						lIntPrevMonthYear = lIntCurrMonthYear - 1;
					}
					lStrCurrMonthYear = lSdf2.format(lSdf1.parse(lIntCurrMonthYear.toString()));
					lStrPrevMonthYear = lSdf2.format(lSdf1.parse(lIntPrevMonthYear.toString()));
				}
				summaryRptColumnHeading[2] = new ColumnVo(lStrPrevMonthYear + " Count ", 2, 11, 0, false, false, true);
				summaryRptColumnHeading[3] = new ColumnVo(lStrPrevMonthYear + " Amount", 2, 11, 0, false, false, true);
				summaryRptColumnHeading[4] = new ColumnVo(lStrCurrMonthYear + " Count", 2, 11, 0, false, false, true);
				summaryRptColumnHeading[5] = new ColumnVo(lStrCurrMonthYear + " Amount", 2, 11, 0, false, false, true);

				lLstAllChngRqstIds = new ArrayList<String>();
				lLstAllChngRqstIds.add(lStrChngRqstId);
				dataList = lObjChangeStmnt.getSummaryOfChngStmnt(lLstAllChngRqstIds, lIntPrevMonthYear, lStrSchemeCode, "B");
				lStrHeaderBankBranch = "";
				lStrHeaderMonthYear = "Month-Year :" + lStrCurrMonthYear;
				if (dataList.size() > 0) {
					rowList = (List) dataList.get(0);
					if (rowList.size() > 0) {
						lStrHeaderBankBranch = "Bank Name :" + rowList.get(0) + appendSpace(8) + "Branch Name :" + rowList.get(1) + "\r\n";
					}
				}
				lStrHeader = lSbHeader.toString() + lStrHeaderBankBranch + lStrHeaderMonthYear + appendSpace(lStrHeaderBankBranch.length() - lStrHeaderMonthYear.length());
				lStrFooter = "";
				lStrParameterString = "Change Statement Summary";
				strOut = objExport.getReportFile(dataList, summaryRptColumnHeading, lStrHeader, lStrFooter, lStrParameterString, nMode, pageBreakonGrouping, noOfLinesPrintedOnPage, true, "|");
				if (strOut != null) {
					noOfLinesPrintedOnPage = (strOut.split(lineSeperator)).length;
				}
				lSBOut.append(strOut);
				lObjChangeStmnt.setCurrAndPrevMonthDtls(lStrChngRqstId, lIntPrevMonthYear, lStrSchemeCode);
				dataList = lObjChangeStmnt.getChangeStmntDtls(lIntPrevMonthYear, lIntCurrMonthYear);
				lStrParameterString = "Change Statement Report";
				lStrFooter = "";
				strOut = objExport.getReportFile(dataList, chngStmntDtlRptColumnHeading, lStrHeader, lStrFooter, lStrParameterString, nMode, pageBreakonGrouping, noOfLinesPrintedOnPage, true, "|");
				lSBOut.append(strOut);

				if (strOut != null) {
					noOfLinesPrintedOnPage = (strOut.split(lineSeperator)).length;
				}

				ColumnVo[] recoveryRptColumnHeading = new ColumnVo[4];
				recoveryRptColumnHeading[0] = new ColumnVo("Sr.No", 2, 20, 0, false, false, false);
				recoveryRptColumnHeading[1] = new ColumnVo("Recovery Type", 1, 47, 0, true, false, false);
				recoveryRptColumnHeading[2] = new ColumnVo("Scheme Code", 3, 30, 0, false, false, false);
				recoveryRptColumnHeading[3] = new ColumnVo("Amount", 2, 30, 0, false, false, false);

				dataList = lObjChangeStmnt.getChangeStmntRecoveryDtls(lIntPrevMonthYear.toString());
				lStrParameterString = "Change Statement Recovery Report";
				lStrFooter = "Prepared By :" + lStrEmpName;
				strOut = objExport.getReportFile(dataList, recoveryRptColumnHeading, lStrHeader, lStrFooter, lStrParameterString, nMode, pageBreakonGrouping, noOfLinesPrintedOnPage, true, "|");
				lSBOut.append(strOut);
				lSBOut.append(pageBreak);
				noOfLinesPrintedOnPage = 1;
			}
			output.put(DBConstants.DIS_ONSCREEN, lSBOut.toString());
			objExport.getExportData(resObj, exportTo, inputMap, output, false);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private String appendSpace(int noOfSpaces) {

		String lStrSpaces = "";
		for (int i = 0; i < noOfSpaces; i++) {
			lStrSpaces = lStrSpaces + " ";
		}
		return lStrSpaces;
	}

	public ResultObject generateSchemewiseChangeStatement(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		MonthlyPensionBillDAO lObjPensionBillDAO = null;
		String lStrBillNo = null;
		String lStrForMonth = null;
		List<Object[]> lLstChangeRqstId = null;
		String lStrChangeRqstId = null;
		String lStrBranchCode = null;
		try {
			setSessionInfo(inputMap);
			lStrBillNo = StringUtility.getParameter("billNo", request).trim();
			lStrForMonth = StringUtility.getParameter("forMonth", request).trim();
			lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			if (lStrBillNo.length() > 0) {
				lLstChangeRqstId = lObjPensionBillDAO.getChangeRqstIdByBillNo(BigInteger.valueOf(Long.valueOf(lStrBillNo)), gStrLocCode, lStrForMonth);
				for (Object[] lArrObj : lLstChangeRqstId) {
					lStrChangeRqstId = (lArrObj[0] != null) ? lArrObj[0].toString() : "";
					lStrBranchCode = (lArrObj[1] != null) ? lArrObj[1].toString() : "";
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getVoucherNo(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());
		String lStrVoucherNoList = null;
		Integer lIntMonth = null;
		String lStrForMonth = null;
		try {
			setSessionInfo(inputMap);
			String lStrMonthId = StringUtility.getParameter("MonthId", request);
			String lStrYearId = StringUtility.getParameter("YearId", request);
			String lStrTreasuryCode = StringUtility.getParameter("TreasuryCode", request);

			if (!lStrMonthId.equals("")) {
				lIntMonth = Integer.parseInt(lStrMonthId);
			}
			if (lIntMonth < 10 && lIntMonth > 0) {
				lStrForMonth = lStrYearId + "0" + lStrMonthId;
			} else {
				lStrForMonth = lStrYearId + lStrMonthId;
			}

			List<ComboValuesVO> lLstVoucherNo = lObjPensionpayQueryDAO.getVoucherNo(Integer.parseInt(lStrForMonth), lStrTreasuryCode);

			if (!lLstVoucherNo.isEmpty()) {
				lStrVoucherNoList = new AjaxXmlBuilder().addItems(lLstVoucherNo, "desc", "id", true).toString();
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		inputMap.put("ajaxKey", lStrVoucherNoList);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject loadMonthlyPensionReportAG(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
		PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());		

		List<ComboValuesVO> lLstMonth = null;
		List<ComboValuesVO> lLstYear = null;
		List<ComboValuesVO> lLstTreasuryName = null;
		List<ComboValuesVO> lLstVoucherNo = null;
		List<Long> lLstTreasuryId = new ArrayList<Long>();
		Integer lIntMonth = null;
		String lStrForMonth = null;
		try {
			setSessionInfo(inputMap);
			String lStrMonthId = StringUtility.getParameter("cmbMonth", request);
			String lStrYearId = StringUtility.getParameter("cmbYear", request);
			String lStrLocCode = StringUtility.getParameter("cmbTreasuryName", request);
			String lStrVoucherNo = StringUtility.getParameter("cmbVoucherNo", request);
			if (!lStrMonthId.equals("")) {
				lIntMonth = Integer.parseInt(lStrMonthId);
				if (lIntMonth < 10 && lIntMonth > 0) {
					lStrForMonth = lStrYearId + "0" + lStrMonthId;
				} else {
					lStrForMonth = lStrYearId + lStrMonthId;
				}
				lLstVoucherNo = lObjPensionpayQueryDAO.getVoucherNo(Integer.parseInt(lStrForMonth), lStrLocCode);
			}
			
			lLstMonth = lObjCommonDAO.getMonthList(SessionHelper.getLocale(request));
			lLstYear = lObjPensionpayQueryDAO.getYearListForMonthlyPenReport(SessionHelper.getLocale(request));			
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASURYID1")));
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASIRYID2")));
			lLstTreasuryName = lObjCommonDAO.getAllTreasury(gLngLangId, lLstTreasuryId);

			inputMap.put("MonthList", lLstMonth);
			inputMap.put("YearList", lLstYear);
			inputMap.put("TreasuryNameList", lLstTreasuryName);
			inputMap.put("VoucherNoList", lLstVoucherNo);
			inputMap.put("MonthId", lStrMonthId);
			inputMap.put("YearId", lStrYearId);
			inputMap.put("LocCode", lStrLocCode);
			inputMap.put("VoucherNo", lStrVoucherNo);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("MonthlyPensionReportAG");
		return resObj;
	}

	public ResultObject generateMonthlyPenReport(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());
		SimpleDateFormat lObjSdfyyyyMM = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lObjSdfMMMYYYY = new SimpleDateFormat("MMM-yyyy");
		SimpleDateFormat lObjSdf = new SimpleDateFormat("dd/MM/yyyy");
		Date lDtForMonthYear = null;

		String lStrFileName = null;
		Integer lIntMonth = null;
		String lStrForMonth = null;
		ReportExportHelper objExport = new ReportExportHelper();
		Map lDetailMap = new HashMap();
		Object lObj[];

		List columndata = new ArrayList();
		String[] param = new String[1];
		Integer[] lArrIntHidCol = new Integer[1];
		List Headerdata = new ArrayList();
		List footerdata = new ArrayList();
		List maindata = new ArrayList();
		byte[] aryOut = null;
		Integer lIntHeaderCol = null;
		lArrIntHidCol[0] = -1;
		try {
			setSessionInfo(inputMap);
			String lStrMonthId = StringUtility.getParameter("monthId", request);
			String lStrYearId = StringUtility.getParameter("yearId", request);
			String lStrLocCode = StringUtility.getParameter("locCode", request);
			String lStrVoucherNo = StringUtility.getParameter("voucherNo", request);
			String lStrCriteria = StringUtility.getParameter("criteria", request);

			if (!lStrMonthId.equals("")) {
				lIntMonth = Integer.parseInt(lStrMonthId);
			}
			if (lIntMonth < 10 && lIntMonth > 0) {
				lStrForMonth = lStrYearId + "0" + lStrMonthId;
			} else {
				lStrForMonth = lStrYearId + lStrMonthId;
			}

			if (lStrCriteria.equals("1")) {
				lIntHeaderCol = 6;
				List<Object> lLstMonthlyPenDtls = lObjPensionpayQueryDAO.getMonthlyPenDtlsReport(Integer.parseInt(lStrForMonth), lStrLocCode, lStrVoucherNo, gLngLangId.toString());
				List<Object> lLstInnerList = new ArrayList<Object>();
				List<Object> lLstArrOuter = new ArrayList<Object>();

				if (lLstMonthlyPenDtls != null && !lLstMonthlyPenDtls.isEmpty()) {
					Iterator it = lLstMonthlyPenDtls.iterator();
					while (it.hasNext()) {
						lObj = (Object[]) it.next();
						lLstInnerList = new ArrayList<Object>();
						if (lObj[0] != null) {
							lLstInnerList.add(lObj[0]);
						} else {
							lLstInnerList.add("");
						}

						if (lObj[1] != null) {
							lLstInnerList.add(lObj[1]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[2] != null) {
							lLstInnerList.add(lObj[2]);
						} else {
							lLstInnerList.add("");
						}

						if (lObj[3] != null) {
							lLstInnerList.add(lObjSdf.format(lObj[3]));
						} else {
							lLstInnerList.add("");
						}

						if (lObj[4] != null) {
							lLstInnerList.add(lObj[4]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[5] != null) {
							lLstInnerList.add(lObj[5]);
						} else {
							lLstInnerList.add("");
						}

						if (lObj[6] != null) {
							lLstInnerList.add(lObj[6]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[7] != null) {
							lLstInnerList.add(lObj[7]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[8] != null) {
							lLstInnerList.add(lObj[8]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[9] != null) {
							lLstInnerList.add(lObj[9]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[14] != null) {
							if (lObj[14].equals("V")) {
								lLstInnerList.add("Voted");
							} else if (lObj[14].equals("C")) {
								lLstInnerList.add("Charged");
							} else {
								lLstInnerList.add("");
							}
						} else {
							lLstInnerList.add("");
						}
						if (lObj[15] != null) {
							if (lObj[15].equals("P")) {
								lLstInnerList.add("Plan");
							} else if (lObj[15].equals("N")) {
								lLstInnerList.add("Non Plan");
							} else {
								lLstInnerList.add("");
							}
						} else {
							lLstInnerList.add("");
						}
						if (lObj[10] != null) {
							lDtForMonthYear = lObjSdfyyyyMM.parse(lObj[10].toString());
							lLstInnerList.add(lObjSdfMMMYYYY.format(lDtForMonthYear));
						} else {
							lLstInnerList.add("");
						}
						if (lObj[11] != null) {
							lLstInnerList.add(lObj[11]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[12] != null) {
							lLstInnerList.add(lObj[12]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[13] != null) {
							lLstInnerList.add(lObj[13]);
						} else {
							lLstInnerList.add("");
						}
						lLstArrOuter.add(lLstInnerList);
						lStrFileName = lStrLocCode + "-" + lStrVoucherNo + "-" + lObj[4].toString() + "-Details";
					}
				} else {
					lStrFileName = lStrLocCode + "-" + lStrVoucherNo + "-Details";
				}

				String lStrHeading = "Monthly Pension Detail Report";
				ColumnVo[] columnHeading = new ColumnVo[16];

				columnHeading[0] = new ColumnVo("Treasury Code", 1, 15, 0, false, false, true);
				columnHeading[1] = new ColumnVo("Treasury Name", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("Voucher No", 2, 15, 0, false, false, true);
				columnHeading[3] = new ColumnVo("Voucher Date", 3, 15, 0, false, false, true);
				columnHeading[4] = new ColumnVo("Payment Scheme Code", 2, 15, 0, false, false, true);
				columnHeading[5] = new ColumnVo("Demand No", 2, 10, 0, false, false, true);
				columnHeading[6] = new ColumnVo("Major Head", 2, 10, 0, false, false, true);
				columnHeading[7] = new ColumnVo("Sub Major Head", 2, 10, 0, false, false, true);
				columnHeading[8] = new ColumnVo("Minor Head", 2, 10, 0, false, false, true);
				columnHeading[9] = new ColumnVo("Sub Minor Head", 2, 10, 0, false, false, true);
				columnHeading[10] = new ColumnVo("Voted/Charged", 1, 10, 0, true, false, true);
				columnHeading[11] = new ColumnVo("Plan/Non Plan", 1, 9, 0, true, false, true);
				columnHeading[12] = new ColumnVo("Month Year", 3, 10, 0, false, false, true);
				columnHeading[13] = new ColumnVo("Gross Amount", 2, 15, 0, false, false, true);
				columnHeading[14] = new ColumnVo("Deduction Amount", 2, 15, 0, false, false, true);
				columnHeading[15] = new ColumnVo("Net Amount", 2, 15, 0, false, false, true);

				columndata.add(columnHeading);
				maindata.add(lLstArrOuter);
				param[0] = "";

				Headerdata.add(lStrHeading.toString());
				footerdata.add("");

				ExcelExportHelper exph = new ExcelExportHelper();

				aryOut = exph.getExcelReportPrintFormatCustom(maindata, columndata, param, Headerdata, footerdata, lIntHeaderCol, lArrIntHidCol);

			} else if (lStrCriteria.equals("2")) {

				lIntHeaderCol = 5;
				List<Object> lLstMonthlyPenRecovery = lObjPensionpayQueryDAO.getMonthlyPenRecoveryReport(Integer.parseInt(lStrForMonth), lStrLocCode, lStrVoucherNo, gLngLangId.toString());
				List<Object> lLstInnerList = new ArrayList<Object>();
				List<Object> lLstArrOuter = new ArrayList<Object>();

				if (lLstMonthlyPenRecovery != null && !lLstMonthlyPenRecovery.isEmpty()) {
					Iterator it = lLstMonthlyPenRecovery.iterator();
					while (it.hasNext()) {
						lObj = (Object[]) it.next();
						lLstInnerList = new ArrayList<Object>();
						if (lObj[0] != null) {
							lLstInnerList.add(lObj[0]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[1] != null) {
							lLstInnerList.add(lObj[1]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[2] != null) {
							lLstInnerList.add(lObj[2]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[3] != null) {
							lLstInnerList.add(lObjSdf.format(lObj[3]));
						} else {
							lLstInnerList.add("");
						}

						if (lObj[4] != null) {
							lLstInnerList.add(lObj[4]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[5] != null) {
							lLstInnerList.add(lObj[5]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[6] != null) {
							lLstInnerList.add(lObj[6]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[7] != null) {
							lLstInnerList.add(lObj[7]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[8] != null) {
							lLstInnerList.add(lObj[8]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[9] != null) {
							lLstInnerList.add(lObj[9]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[11] != null) {
							if (lObj[11].equals("V")) {
								lLstInnerList.add("Voted");
							} else if (lObj[11].equals("C")) {
								lLstInnerList.add("Charged");
							} else {
								lLstInnerList.add("");
							}
						} else {
							lLstInnerList.add("");
						}
						if (lObj[12] != null) {
							if (lObj[12].equals("P")) {
								lLstInnerList.add("Plan");
							} else if (lObj[12].equals("N")) {
								lLstInnerList.add("Non Plan");
							} else {
								lLstInnerList.add("");
							}
						} else {
							lLstInnerList.add("");
						}
						if (lObj[10] != null) {
							lLstInnerList.add(lObj[10]);
						} else {
							lLstInnerList.add("");
						}
						lLstArrOuter.add(lLstInnerList);
					}
				}
				lStrFileName = lStrLocCode + "-" + lStrVoucherNo + "-Recovery";
				String lStrHeading = "Monthly Pension Recovery Report";

				ColumnVo[] columnHeading = new ColumnVo[13];

				columnHeading[0] = new ColumnVo("Treasury Code", 1, 15, 0, false, false, true);
				columnHeading[1] = new ColumnVo("Treasury Name", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("Voucher No", 2, 15, 0, false, false, true);
				columnHeading[3] = new ColumnVo("Voucher Date", 3, 15, 0, false, false, true);
				columnHeading[4] = new ColumnVo("Deduction Scheme Code", 2, 15, 0, false, false, true);
				columnHeading[5] = new ColumnVo("Demand No", 2, 10, 0, false, false, true);
				columnHeading[6] = new ColumnVo("Major Head", 2, 10, 0, false, false, true);
				columnHeading[7] = new ColumnVo("Sub Major Head", 2, 10, 0, false, false, true);
				columnHeading[8] = new ColumnVo("Minor Head", 2, 10, 0, false, false, true);
				columnHeading[9] = new ColumnVo("Sub Minor Head", 2, 10, 0, false, false, true);
				columnHeading[10] = new ColumnVo("Voted/Charged", 1, 10, 0, true, false, true);
				columnHeading[11] = new ColumnVo("Plan/Non Plan", 1, 9, 0, true, false, true);
				columnHeading[12] = new ColumnVo("Deduction Amount scheme", 2, 15, 0, false, false, true);

				columndata.add(columnHeading);
				maindata.add(lLstArrOuter);
				param[0] = "";

				Headerdata.add(lStrHeading.toString());
				footerdata.add("");

				ExcelExportHelper exph = new ExcelExportHelper();

				aryOut = exph.getExcelReportPrintFormatCustom(maindata, columndata, param, Headerdata, footerdata, lIntHeaderCol, lArrIntHidCol);

			} else if (lStrCriteria.equals("3")) {
				lIntHeaderCol = 4;
				List<Object> lLstMonthlyPenAllocation = lObjPensionpayQueryDAO.getMonthlyPenAllocationReport(Integer.parseInt(lStrForMonth), lStrLocCode, lStrVoucherNo, gLngLangId.toString());
				List<Object> lLstInnerList = new ArrayList<Object>();
				List<Object> lLstArrOuter = new ArrayList<Object>();

				if (lLstMonthlyPenAllocation != null && !lLstMonthlyPenAllocation.isEmpty()) {
					Iterator it = lLstMonthlyPenAllocation.iterator();
					while (it.hasNext()) {
						lObj = (Object[]) it.next();
						lLstInnerList = new ArrayList<Object>();
						if (lObj[0] != null) {
							lLstInnerList.add(lObj[0]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[1] != null) {
							lLstInnerList.add(lObj[1]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[2] != null) {
							lLstInnerList.add(lObj[2]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[3] != null) {
							lLstInnerList.add(lObjSdf.format(lObj[3]));
						} else {
							lLstInnerList.add("");
						}

						if (lObj[4] != null) {
							lLstInnerList.add(lObj[4]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[5] != null) {
							lLstInnerList.add(lObj[5]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[6] != null) {
							lLstInnerList.add(lObj[6]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[7] != null) {
							lLstInnerList.add(lObj[7]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[8] != null) {
							lLstInnerList.add(lObj[8]);
						} else {
							lLstInnerList.add("");
						}
						lLstArrOuter.add(lLstInnerList);
					}
				}

				lStrFileName = lStrLocCode + "-" + lStrVoucherNo + "-Allocation";
				String lStrHeading = "Monthly Pension Allocation Report";

				ColumnVo[] columnHeading = new ColumnVo[9];

				columnHeading[0] = new ColumnVo("Treasury Code", 1, 15, 0, false, false, true);
				columnHeading[1] = new ColumnVo("Treasury Name", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("Voucher No", 2, 13, 0, false, false, true);
				columnHeading[3] = new ColumnVo("Voucher Date", 3, 15, 0, false, false, true);
				columnHeading[4] = new ColumnVo("Allocation 1", 2, 15, 0, false, false, true);
				columnHeading[5] = new ColumnVo("Allocation 2", 2, 15, 0, false, false, true);
				columnHeading[6] = new ColumnVo("Allocation 3", 2, 15, 0, false, false, true);
				columnHeading[7] = new ColumnVo("Allocation 4", 2, 15, 0, false, false, true);
				columnHeading[8] = new ColumnVo("Allocation 5", 2, 15, 0, false, false, true);

				columndata.add(columnHeading);
				maindata.add(lLstArrOuter);
				param[0] = "";

				Headerdata.add(lStrHeading.toString());
				footerdata.add("");

				ExcelExportHelper exph = new ExcelExportHelper();

				aryOut = exph.getExcelReportPrintFormatCustom(maindata, columndata, param, Headerdata, footerdata, lIntHeaderCol, lArrIntHidCol);
			}
			String lStrExportTo = DBConstants.DIS_EXCELFILE;
			if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_ONSCREEN, aryOut);
			} else if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_EXCELFILE, aryOut);
			}
			inputMap.put("FileName", lStrFileName);
			objExport.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject downloadPensionerBankACNo(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());

		String lStrFileName = null;
		Integer lIntMonth = null;
		String lStrForMonth = null;
		ReportExportHelper objExport = new ReportExportHelper();
		Map lDetailMap = new HashMap();
		Object lObj[];

		List columndata = new ArrayList();
		String[] param = new String[1];
		List Headerdata = new ArrayList();
		List footerdata = new ArrayList();
		List maindata = new ArrayList();
		byte[] aryOut = null;
		Integer lIntHeaderCol = 3;
		param[0] = "";
		String lStrBankCode = null;
		String lStrBranchCode = null;

		try {
			setSessionInfo(inputMap);

			lStrBankCode = StringUtility.getParameter("cmbBankList", request);
			lStrBranchCode = StringUtility.getParameter("cmbBranchList", request);

			List<Object> lLstMonthlyPenAllocation = lObjPensionpayQueryDAO.getPensionerACNumber(gStrLocCode, lStrBankCode, lStrBranchCode, gLngLangId);
			List<Object> lLstInnerList = new ArrayList<Object>();
			List<Object> lLstArrOuter = new ArrayList<Object>();

			if (lLstMonthlyPenAllocation != null && !lLstMonthlyPenAllocation.isEmpty()) {
				Iterator it = lLstMonthlyPenAllocation.iterator();
				while (it.hasNext()) {
					lObj = (Object[]) it.next();
					lLstInnerList = new ArrayList<Object>();
					if (lObj[0] != null) {
						lLstInnerList.add(lObj[0]);
					} else {
						lLstInnerList.add("");
					}
					if (lObj[1] != null) {
						lLstInnerList.add(lObj[1]);
					} else {
						lLstInnerList.add("");
					}
					if (lObj[2] != null) {
						lLstInnerList.add(lObj[2]);
					} else {
						lLstInnerList.add("");
					}
					if (lObj[3] != null) {
						lLstInnerList.add(lObj[3]);
					} else {
						lLstInnerList.add("");
					}
					if (lObj[4] != null) {
						lLstInnerList.add(lObj[4]);
					} else {
						lLstInnerList.add("");
					}

					lLstInnerList.add("");

					if (lObj[5] != null) {
						lLstInnerList.add(lObj[5]);
					} else {
						lLstInnerList.add("");
					}

					lLstArrOuter.add(lLstInnerList);
				}
			}

			lStrFileName = "Pensioner_Bank_AC_No_" + lStrBankCode + "_" + lStrBranchCode;

			ColumnVo[] columnHeading = new ColumnVo[7];

			columnHeading[0] = new ColumnVo("PPO Number", 1, 20, 0, false, false, true);
			columnHeading[1] = new ColumnVo("Pensioner Name", 1, 35, 0, true, false, true);
			columnHeading[2] = new ColumnVo("Bank Name", 1, 30, 0, false, false, true);
			columnHeading[3] = new ColumnVo("Branch Name", 1, 30, 0, false, false, true);
			columnHeading[4] = new ColumnVo("Old A/C Number", 1, 20, 0, false, false, true);
			columnHeading[5] = new ColumnVo("New A/C Number", 1, 20, 0, false, false, true);
			columnHeading[6] = new ColumnVo(" ", 1, 20, 0, false, false, true);

			columndata.add(columnHeading);
			maindata.add(lLstArrOuter);
			Integer[] lArrIntHidCol = new Integer[1];
			lArrIntHidCol[0] = 6;
			Headerdata.add("");
			footerdata.add("");

			ExcelExportHelper exph = new ExcelExportHelper();

			aryOut = exph.getExcelReportForPensionACNo(maindata, columndata, param, Headerdata, footerdata, lIntHeaderCol, lArrIntHidCol);

			String lStrExportTo = DBConstants.DIS_EXCELFILE;
			if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_ONSCREEN, aryOut);
			} else if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_EXCELFILE, aryOut);
			}
			inputMap.put("FileName", lStrFileName);
			objExport.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject loadPensionerBankACNoForm(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			List lLstBank = lObjCommonDAO.getBankList(inputMap, gLngLangId);
			inputMap.put("lLstBank", lLstBank);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("DownloadBankACNo");
		return resObj;
	}
	public ResultObject loadFirstPayReportParaForm(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			List<Long> lLstTreasuryId = new ArrayList<Long>();
			String lStrReportType = StringUtility.getParameter("reportType", request);
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASURYID1")));
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASIRYID2")));
			List lLstTreasury = lObjCommonDAO.getAllTreasury(gLngLangId, lLstTreasuryId);
			inputMap.put("lLstTreasury", lLstTreasury);
			inputMap.put("reportType", lStrReportType);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("FirstPayReport");
		return resObj;
	}
	public ResultObject generateFirstPayReportExcel(Map<String,Object> inputMap){


		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());
		SimpleDateFormat lObjSdfyyyyMM = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lObjSdfMMMYYYY = new SimpleDateFormat("MMM-yyyy");
		SimpleDateFormat lObjSdf = new SimpleDateFormat("dd/MM/yyyy");
		Date lDtForMonthYear = null;

		String lStrFileName = null;		
		ReportExportHelper objExport = new ReportExportHelper();
		Map lDetailMap = new HashMap();
		Object lObj[];

		List columndata = new ArrayList();
		String[] param = new String[1];
		Integer[] lArrIntHidCol = new Integer[1];
		List Headerdata = new ArrayList();
		List footerdata = new ArrayList();
		List maindata = new ArrayList();
		byte[] aryOut = null;
		Integer lIntHeaderCol = null;
		lArrIntHidCol[0] = -1;
		try {
			setSessionInfo(inputMap);
			String lStrFormVoucherDate = StringUtility.getParameter("formVoucherDate",request);
			String lStrToVoucherDate =  StringUtility.getParameter("toVoucherDate",request);
			String lStrLocCode =  StringUtility.getParameter("treasuryCode",request);
			String lStrPPONo =  StringUtility.getParameter("ppoNo",request);
			String lStrCriteria = StringUtility.getParameter("criteria", request);
			String lStrReportType = StringUtility.getParameter("reportType", request);

			Date lDtFromVoucherDate = null;
			Date lDtToVoucherDate = null;
			Long lLngTreasuryCode = null;
			
			if (!"".equals(lStrFormVoucherDate)) {
				lDtFromVoucherDate = StringUtility.convertStringToDate(lStrFormVoucherDate);
			}
			if (!"".equals(lStrToVoucherDate)) {
				lDtToVoucherDate = StringUtility.convertStringToDate(lStrToVoucherDate);
				lDtToVoucherDate.setDate(lDtToVoucherDate.getDate() + 1);
			}
			if (!"".equals(lStrLocCode)) {
				lLngTreasuryCode = Long.parseLong(lStrLocCode);
			}
			Integer lIntVoucherNo = 0;
			if (lStrCriteria.equals("1")) {
				lIntHeaderCol = 6;
				List<Object> lLstMonthlyPenDtls = lObjPensionpayQueryDAO.getPensionDtlsReport(lDtFromVoucherDate,lDtToVoucherDate, lLngTreasuryCode, lStrPPONo,lStrReportType, gLngLangId.toString());
				List<Object> lLstInnerList = new ArrayList<Object>();
				List<Object> lLstArrOuter = new ArrayList<Object>();

				if (lLstMonthlyPenDtls != null && !lLstMonthlyPenDtls.isEmpty()) {
					Iterator it = lLstMonthlyPenDtls.iterator();
					while (it.hasNext()) {
						lObj = (Object[]) it.next();
						lLstInnerList = new ArrayList<Object>();
						if (lObj[0] != null) {
							lLstInnerList.add(lObj[0]);
						} else {
							lLstInnerList.add("");
						}

						if (lObj[1] != null) {
							lLstInnerList.add(lObj[1]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[16] != null) {
							lLstInnerList.add(lObj[16]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[2] != null) {
							lLstInnerList.add(lObj[2]);
						} else {
							lLstInnerList.add("");
						}

						if (lObj[3] != null) {
							lLstInnerList.add(lObjSdf.format(lObj[3]));
						} else {
							lLstInnerList.add("");
						}

						if (lObj[4] != null) {
							lLstInnerList.add(lObj[4]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[5] != null) {
							lLstInnerList.add(lObj[5]);
						} else {
							lLstInnerList.add("");
						}

						if (lObj[6] != null) {
							lLstInnerList.add(lObj[6]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[7] != null) {
							lLstInnerList.add(lObj[7]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[8] != null) {
							lLstInnerList.add(lObj[8]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[9] != null) {
							lLstInnerList.add(lObj[9]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[14] != null) {
							if (lObj[14].equals("V")) {
								lLstInnerList.add("Voted");
							} else if (lObj[14].equals("C")) {
								lLstInnerList.add("Charged");
							} else {
								lLstInnerList.add("");
							}
						} else {
							lLstInnerList.add("");
						}
						if (lObj[15] != null) {
							if (lObj[15].equals("P")) {
								lLstInnerList.add("Plan");
							} else if (lObj[15].equals("N")) {
								lLstInnerList.add("Non Plan");
							} else {
								lLstInnerList.add("");
							}
						} else {
							lLstInnerList.add("");
						}
						if (lObj[10] != null) {
							lDtForMonthYear = lObjSdfyyyyMM.parse(lObj[10].toString());
							lLstInnerList.add(lObjSdfMMMYYYY.format(lDtForMonthYear));
						} else {
							lLstInnerList.add("");
						}
						if (lObj[11] != null) {
							lLstInnerList.add(lObj[11]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[12] != null) {
							lLstInnerList.add(lObj[12]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[13] != null) {
							lLstInnerList.add(lObj[13]);
						} else {
							lLstInnerList.add("");
						}
						lLstArrOuter.add(lLstInnerList);
						
						
						if (lObj[2] != null) {
							lIntVoucherNo = (Integer) lObj[2];
						}
						lStrFileName = lStrLocCode + "-" + lIntVoucherNo + "-" + lObj[4].toString() + "-Details";
					}
				} else {
					lStrFileName = lStrLocCode + "-" + lIntVoucherNo + "-Details";
				}

				String lStrHeading = "";
				if(lStrReportType.equals("F"))
					lStrHeading = "First Payment Detail Report";
				else if(lStrReportType.equals("C"))
					lStrHeading = "CVP Payment Detail Report";
				else if(lStrReportType.equals("D"))
					lStrHeading = "DCRG Payment Detail Report";
				
				ColumnVo[] columnHeading = new ColumnVo[17];

				columnHeading[0] = new ColumnVo("Treasury Code", 1, 15, 0, false, false, true);
				columnHeading[1] = new ColumnVo("Treasury Name", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("PPO No.", 1, 20, 0, true, false, true);
				columnHeading[3] = new ColumnVo("Voucher No", 2, 15, 0, false, false, true);
				columnHeading[4] = new ColumnVo("Voucher Date", 3, 15, 0, false, false, true);
				columnHeading[5] = new ColumnVo("Payment Scheme Code", 2, 15, 0, false, false, true);
				columnHeading[6] = new ColumnVo("Demand No", 2, 10, 0, false, false, true);
				columnHeading[7] = new ColumnVo("Major Head", 2, 10, 0, false, false, true);
				columnHeading[8] = new ColumnVo("Sub Major Head", 2, 10, 0, false, false, true);
				columnHeading[9] = new ColumnVo("Minor Head", 2, 10, 0, false, false, true);
				columnHeading[10] = new ColumnVo("Sub Minor Head", 2, 10, 0, false, false, true);
				columnHeading[11] = new ColumnVo("Voted/Charged", 1, 10, 0, true, false, true);
				columnHeading[12] = new ColumnVo("Plan/Non Plan", 1, 9, 0, true, false, true);
				columnHeading[13] = new ColumnVo("Month Year", 3, 10, 0, false, false, true);
				columnHeading[14] = new ColumnVo("Gross Amount", 2, 15, 0, false, false, true);
				columnHeading[15] = new ColumnVo("Deduction Amount", 2, 15, 0, false, false, true);
				columnHeading[16] = new ColumnVo("Net Amount", 2, 15, 0, false, false, true);

				columndata.add(columnHeading);
				maindata.add(lLstArrOuter);
				param[0] = "";

				Headerdata.add(lStrHeading.toString());
				footerdata.add("");

				ExcelExportHelper exph = new ExcelExportHelper();

				aryOut = exph.getExcelReportPrintFormatCustom(maindata, columndata, param, Headerdata, footerdata, lIntHeaderCol, lArrIntHidCol);

			} else if (lStrCriteria.equals("2")) {

				lIntHeaderCol = 5;
				List<Object> lLstMonthlyPenRecovery = lObjPensionpayQueryDAO.getPensionRecoveryReport(lDtFromVoucherDate,lDtToVoucherDate, lLngTreasuryCode, lStrPPONo,lStrReportType, gLngLangId.toString());
				List<Object> lLstInnerList = new ArrayList<Object>();
				List<Object> lLstArrOuter = new ArrayList<Object>();

				if (lLstMonthlyPenRecovery != null && !lLstMonthlyPenRecovery.isEmpty()) {
					Iterator it = lLstMonthlyPenRecovery.iterator();
					while (it.hasNext()) {
						lObj = (Object[]) it.next();
						lLstInnerList = new ArrayList<Object>();
						if (lObj[0] != null) {
							lLstInnerList.add(lObj[0]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[1] != null) {
							lLstInnerList.add(lObj[1]);
						} else {
							lLstInnerList.add("");
						}	
						if (lObj[13] != null) {
							lLstInnerList.add(lObj[13]);
						} else {
							lLstInnerList.add("");
						}	
						if (lObj[2] != null) {
							lLstInnerList.add(lObj[2]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[3] != null) {
							lLstInnerList.add(lObjSdf.format(lObj[3]));
						} else {
							lLstInnerList.add("");
						}

						if (lObj[4] != null) {
							lLstInnerList.add(lObj[4]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[5] != null) {
							lLstInnerList.add(lObj[5]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[6] != null) {
							lLstInnerList.add(lObj[6]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[7] != null) {
							lLstInnerList.add(lObj[7]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[8] != null) {
							lLstInnerList.add(lObj[8]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[9] != null) {
							lLstInnerList.add(lObj[9]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[11] != null) {
							if (lObj[11].equals("V")) {
								lLstInnerList.add("Voted");
							} else if (lObj[11].equals("C")) {
								lLstInnerList.add("Charged");
							} else {
								lLstInnerList.add("");
							}
						} else {
							lLstInnerList.add("");
						}
						if (lObj[12] != null) {
							if (lObj[12].equals("P")) {
								lLstInnerList.add("Plan");
							} else if (lObj[12].equals("N")) {
								lLstInnerList.add("Non Plan");
							} else {
								lLstInnerList.add("");
							}
						} else {
							lLstInnerList.add("");
						}
						if (lObj[10] != null) {
							lLstInnerList.add(lObj[10]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[2] != null) {
							lIntVoucherNo = (Integer) lObj[2];
						}
						lLstArrOuter.add(lLstInnerList);
					}
				}
				lStrFileName = lStrLocCode + "-" + lIntVoucherNo + "-Recovery";
				
				String lStrHeading = "";
				if(lStrReportType.equals("F"))
					lStrHeading = "First Payment Recovery Report";
				else if(lStrReportType.equals("C"))
					lStrHeading = "CVP Payment Recovery Report";
				else if(lStrReportType.equals("D"))
					lStrHeading = "DCRG Payment Recovery Report";
				
				ColumnVo[] columnHeading = new ColumnVo[14];

				columnHeading[0] = new ColumnVo("Treasury Code", 1, 15, 0, false, false, true);
				columnHeading[1] = new ColumnVo("Treasury Name", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("PPO No.", 1, 20, 0, true, false, true);
				columnHeading[3] = new ColumnVo("Voucher No", 2, 15, 0, false, false, true);
				columnHeading[4] = new ColumnVo("Voucher Date", 3, 15, 0, false, false, true);
				columnHeading[5] = new ColumnVo("Deduction Scheme Code", 2, 15, 0, false, false, true);
				columnHeading[6] = new ColumnVo("Demand No", 2, 10, 0, false, false, true);
				columnHeading[7] = new ColumnVo("Major Head", 2, 10, 0, false, false, true);
				columnHeading[8] = new ColumnVo("Sub Major Head", 2, 10, 0, false, false, true);
				columnHeading[9] = new ColumnVo("Minor Head", 2, 10, 0, false, false, true);
				columnHeading[10] = new ColumnVo("Sub Minor Head", 2, 10, 0, false, false, true);
				columnHeading[11] = new ColumnVo("Voted/Charged", 1, 10, 0, true, false, true);
				columnHeading[12] = new ColumnVo("Plan/Non Plan", 1, 9, 0, true, false, true);
				columnHeading[13] = new ColumnVo("Deduction Amount scheme", 2, 15, 0, false, false, true);

				columndata.add(columnHeading);
				maindata.add(lLstArrOuter);
				param[0] = "";

				Headerdata.add(lStrHeading.toString());
				footerdata.add("");

				ExcelExportHelper exph = new ExcelExportHelper();

				aryOut = exph.getExcelReportPrintFormatCustom(maindata, columndata, param, Headerdata, footerdata, lIntHeaderCol, lArrIntHidCol);

			} else if (lStrCriteria.equals("3")) {
				lIntHeaderCol = 4;
				List<Object> lLstMonthlyPenAllocation = lObjPensionpayQueryDAO.getPensionAllocationReport(lDtFromVoucherDate,lDtToVoucherDate, lLngTreasuryCode, lStrPPONo,lStrReportType, gLngLangId.toString());
				List<Object> lLstInnerList = new ArrayList<Object>();
				List<Object> lLstArrOuter = new ArrayList<Object>();

				if (lLstMonthlyPenAllocation != null && !lLstMonthlyPenAllocation.isEmpty()) {
					Iterator it = lLstMonthlyPenAllocation.iterator();
					while (it.hasNext()) {
						lObj = (Object[]) it.next();
						lLstInnerList = new ArrayList<Object>();
						if (lObj[0] != null) {
							lLstInnerList.add(lObj[0]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[1] != null) {
							lLstInnerList.add(lObj[1]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[9] != null) {
							lLstInnerList.add(lObj[9]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[2] != null) {
							lLstInnerList.add(lObj[2]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[3] != null) {
							lLstInnerList.add(lObjSdf.format(lObj[3]));
						} else {
							lLstInnerList.add("");
						}

						if (lObj[4] != null) {
							lLstInnerList.add(lObj[4]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[5] != null) {
							lLstInnerList.add(lObj[5]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[6] != null) {
							lLstInnerList.add(lObj[6]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[7] != null) {
							lLstInnerList.add(lObj[7]);
						} else {
							lLstInnerList.add("");
						}
						if (lObj[8] != null) {
							lLstInnerList.add(lObj[8]);
						} else {
							lLstInnerList.add("");
						}
						lLstArrOuter.add(lLstInnerList);
						if (lObj[2] != null) {
							lIntVoucherNo = (Integer) lObj[2];
						}
					}
				}

				lStrFileName = lStrLocCode + "-" + lIntVoucherNo + "-Allocation";
				
				String lStrHeading = "";
				if(lStrReportType.equals("F"))
					lStrHeading = "First Payment Allocation Report";
				else if(lStrReportType.equals("C"))
					lStrHeading = "CVP Payment Allocation Report";
				else if(lStrReportType.equals("D"))
					lStrHeading = "DCRG Payment Allocation Report";
			
				ColumnVo[] columnHeading = new ColumnVo[10];

				columnHeading[0] = new ColumnVo("Treasury Code", 1, 15, 0, false, false, true);
				columnHeading[1] = new ColumnVo("Treasury Name", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("PPO No.", 1, 20, 0, true, false, true);
				columnHeading[3] = new ColumnVo("Voucher No", 2, 13, 0, false, false, true);
				columnHeading[4] = new ColumnVo("Voucher Date", 3, 15, 0, false, false, true);
				columnHeading[5] = new ColumnVo("Allocation 1", 2, 15, 0, false, false, true);
				columnHeading[6] = new ColumnVo("Allocation 2", 2, 15, 0, false, false, true);
				columnHeading[7] = new ColumnVo("Allocation 3", 2, 15, 0, false, false, true);
				columnHeading[8] = new ColumnVo("Allocation 4", 2, 15, 0, false, false, true);
				columnHeading[9] = new ColumnVo("Allocation 5", 2, 15, 0, false, false, true);

				columndata.add(columnHeading);
				maindata.add(lLstArrOuter);
				param[0] = "";

				Headerdata.add(lStrHeading.toString());
				footerdata.add("");

				ExcelExportHelper exph = new ExcelExportHelper();

				aryOut = exph.getExcelReportPrintFormatCustom(maindata, columndata, param, Headerdata, footerdata, lIntHeaderCol, lArrIntHidCol);
			}
			String lStrExportTo = DBConstants.DIS_EXCELFILE;
			if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_ONSCREEN, aryOut);
			} else if ((DBConstants.DIS_EXCELFILE).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_EXCELFILE, aryOut);
			}
			inputMap.put("FileName", lStrFileName);
			objExport.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	
	}
	
	public ResultObject loadAuditRegisterReport(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			
			List<ComboValuesVO> lLstFinYear = new ArrayList<ComboValuesVO>();
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lLstFinYear = lObjCommonDAO.getFinYearDesc(gLngLangId.toString(), gStrLocCode);
			inputMap.put("lLstFinYear", lLstFinYear);
			
			resObj.setViewName("GenerateAuditRegisterReport");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}
	
	public ResultObject getAuditRegisterRptPpoList(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List lLstResult = new ArrayList();
		Integer lIntCount = 0;
		try {
			String lStrFinYearId = StringUtility.getParameter("finYear", request);
			PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			SgvcFinYearMst lObjSgvcFinYearMst = new SgvcFinYearMst();
			lObjSgvcFinYearMst = lObjCommonDAO.getFinYearDtlsFromFinYearId(lStrFinYearId);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lIntCount = lObjPensionpayQueryDAO.getAuditRegisterRptPpoCount(lStrFinYearId,lObjSgvcFinYearMst.getFromDate(),lObjSgvcFinYearMst.getToDate(), gStrLocCode, gLngPostId.toString());
			if(lIntCount > 0)
			{
				lLstResult = lObjPensionpayQueryDAO.getAuditRegisterRptPpoList(displayTag,lStrFinYearId,lObjSgvcFinYearMst.getFromDate(),lObjSgvcFinYearMst.getToDate(), gStrLocCode, gLngPostId.toString());
			}
			inputMap.put("totalRecords", lIntCount);
			inputMap.put("AudRegPpoList", lLstResult);
			inputMap.put("FinYear", lStrFinYearId);
			
			resObj.setViewName("AuditRegisterReportPpoList");
			resObj.setResultValue(inputMap);
			
		} catch (Exception e) {
			
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}
	
	public ResultObject viewAuditRegisterRptPnsnrDtls(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Set<CmnAttachmentMpg> cmnAttachmentMpgs = null;
		Iterator<CmnAttachmentMpg> cmnAttachmentMpgIterator = null;
		CmnAttachmentMpg cmnAttachmentMpg = null;
		Long lLngPhotoAttachmentId = null;
		Long lLngSignAttachmentId = null;
		List lLstResult = new ArrayList();
		String lStrTreasuryName = "";
		try {
			String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());
			PensionAuditRegisterVO lObjPensionAuditRegisterVO = new PensionAuditRegisterVO();
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
						
			List<TrnPrvosionalPensionDtls> lLstTrnProvisionalPensionDtls = new ArrayList<TrnPrvosionalPensionDtls>();
			List<MstPensionerNomineeDtls> lLstPensionerNomineeDtls = new ArrayList<MstPensionerNomineeDtls>();
			
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCorrectionDtls.class, serv.getSessionFactory());
			
			if(lStrPensionerCode != null && !"".equals(lStrPensionerCode))
			{
				lLstResult = lObjPensionpayQueryDAO.getAuditRegisterRptPnsnrDtls(lStrPensionerCode,gStrLocCode,gLngLangId);
			}
			if (!lLstResult.isEmpty()) {
				Object[] obj = (Object[]) lLstResult.get(0);
				if (obj[0] != null) {
					lObjPensionAuditRegisterVO.setPensionerCode(obj[0].toString());
				}
				if (obj[1] != null) {
					lObjPensionAuditRegisterVO.setPpoNo(obj[1].toString());
				}
				if (obj[2] != null) {
					lObjPensionAuditRegisterVO.setPensionerName(obj[2].toString());
				}
				if (obj[3] != null) {
					lObjPensionAuditRegisterVO.setLedgerNo(obj[3].toString());
				}
				if (obj[4] != null) {
					lObjPensionAuditRegisterVO.setPageNo(obj[4].toString());
				}
				if (obj[5] != null) {
					lObjPensionAuditRegisterVO.setApplicationNo(obj[5].toString());
				}
				if (obj[6] != null) {
					lObjPensionAuditRegisterVO.setPnsrFileNo(obj[6].toString());
				}
				if (obj[7] != null) {
					lObjPensionAuditRegisterVO.setGuardianName(obj[7].toString());
				}
				if (obj[8] != null) {
					lObjPensionAuditRegisterVO.setBankName(obj[8].toString());
				}
				if (obj[9] != null) {
					lObjPensionAuditRegisterVO.setBranchName(obj[9].toString());
				}
				if (obj[10] != null) {
					if((gObjRsrcBndle.getString("PPMT.ABSORPTION")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.ABSORPTION"));
					else if((gObjRsrcBndle.getString("PPMT.COMPASSIONATE")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.COMPASSIONATE"));
					else if((gObjRsrcBndle.getString("PPMT.COMPENSATION")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.COMPENSATION"));
					else if((gObjRsrcBndle.getString("PPMT.COMPULSORY")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.COMPULSORY"));
					else if((gObjRsrcBndle.getString("PPMT.EXTRAORDINARY")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.EXTRAORDINARY"));
					else if((gObjRsrcBndle.getString("PPMT.FAMILYPNSN")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.FAMILYPNSN"));
					else if((gObjRsrcBndle.getString("PPMT.INVALID")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.INVALID"));
					else if((gObjRsrcBndle.getString("PPMT.RETIRING105")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.RETIRING105"));
					else if((gObjRsrcBndle.getString("PPMT.RETIRING104")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.RETIRING104"));
					else if((gObjRsrcBndle.getString("PPMT.SUPERANNU")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.SUPERANNU"));
					else if((gObjRsrcBndle.getString("PPMT.VOLUNTARY64")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.VOLUNTARY64"));
					else if((gObjRsrcBndle.getString("PPMT.VOLUNTARY65")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.VOLUNTARY65"));
					else if((gObjRsrcBndle.getString("PPMT.INJURY")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.INJURY"));
					else if((gObjRsrcBndle.getString("PPMT.GALLANTRY")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.GALLANTRY"));
					else if((gObjRsrcBndle.getString("PPMT.POLITICAL")).equals(obj[10]))
						lObjPensionAuditRegisterVO.setPensionType(gObjLblRsrcBndle.getString("PNSNTYPE.POLITICAL"));
					
					//lObjPensionAuditRegisterVO.setPensionType(obj[10].toString());
				}
				if (obj[11] != null) {
					lObjPensionAuditRegisterVO.setAccountNo(obj[11].toString());
				}
				if (obj[12] != null) {
					lObjPensionAuditRegisterVO.setDesignation(obj[12].toString());
				}
				if (obj[13] != null) {
					lObjPensionAuditRegisterVO.setJoiningDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[13])));
				}
				if (obj[14] != null) {
					lObjPensionAuditRegisterVO.setRetirementDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[14])));
				}
				if (obj[15] != null) {
					lObjPensionAuditRegisterVO.setBirthDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[15])));
				}
				if (obj[16] != null) {
					lObjPensionAuditRegisterVO.setIdentificationDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[16])));
				}
				if (obj[17] != null) {
					lObjPensionAuditRegisterVO.setIdentityMark(obj[17].toString());
				}
				if (obj[18] != null) {
					lObjPensionAuditRegisterVO.setPnsnrAddr1(obj[18].toString());
				}
				if (obj[19] != null) {
					lObjPensionAuditRegisterVO.setPnsnrAddr2(obj[19].toString());
				}
				if (obj[20] != null) {
					lObjPensionAuditRegisterVO.setPnsnrAddrTown(obj[20].toString());
				}
				if (obj[21] != null) {
					lObjPensionAuditRegisterVO.setPinCode(Integer.parseInt(obj[21].toString()));
				}
				if (obj[22] != null) {
					lObjPensionAuditRegisterVO.setStateName(obj[22].toString());
				}
				if (obj[23] != null) {
					lObjPensionAuditRegisterVO.setDistrictName(obj[23].toString());
				}
				if (obj[24] != null) {
					lObjPensionAuditRegisterVO.setPanNo(obj[24].toString());
				}
				if (obj[25] != null) {
					lObjPensionAuditRegisterVO.setMobileNo(obj[25].toString());
				}
				if (obj[26] != null) {
					lObjPensionAuditRegisterVO.setTeleNo(obj[26].toString());
				}
				if (obj[27] != null) {
					lObjPensionAuditRegisterVO.setPnsnrEmailId(obj[27].toString());
				}
				if (obj[28] != null) {
					lObjPensionAuditRegisterVO.setBasicPensionAmount(new BigDecimal(obj[28].toString()));
				}
				if (obj[29] != null) {
					lObjPensionAuditRegisterVO.setCommencementDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[29])));
				}
				if (obj[30] != null) {
					lObjPensionAuditRegisterVO.setCvpAmount(new BigDecimal(obj[30].toString()));
				}
				if (obj[31] != null) {
					lObjPensionAuditRegisterVO.setDcrgOrderNo(obj[31].toString());
				}
				if (obj[32] != null) {
					lObjPensionAuditRegisterVO.setGratuityAmount(new BigDecimal(obj[32].toString()));
				}
				if (obj[33] != null) {
					lObjPensionAuditRegisterVO.setFamilyMemName(obj[33].toString());
				}
				if (obj[34] != null) {
					lObjPensionAuditRegisterVO.setFp1Amount(new BigDecimal(obj[34].toString()));
				}
				if (obj[35] != null) {
					lObjPensionAuditRegisterVO.setFp1Date(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[35])));
				}
				if (obj[36] != null) {
					lObjPensionAuditRegisterVO.setFp2Amount(new BigDecimal(obj[36].toString()));
				}
				if (obj[37] != null) {
					lObjPensionAuditRegisterVO.setFp2Date(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[37])));
				}
				if (obj[38] != null) {
					lObjPensionAuditRegisterVO.setOrgBf1436(new BigDecimal(obj[38].toString()));
				}
				if (obj[39] != null) {
					lObjPensionAuditRegisterVO.setOrgAf1436(new BigDecimal(obj[39].toString()));
				}
				if (obj[40] != null) {
					lObjPensionAuditRegisterVO.setOrgAf1156(new BigDecimal(obj[40].toString()));
				}
				if (obj[41] != null) {
					lObjPensionAuditRegisterVO.setOrgBf1560(new BigDecimal(obj[41].toString()));
				}
				if (obj[42] != null) {
					lObjPensionAuditRegisterVO.setOrgAfZp(new BigDecimal(obj[42].toString()));
				}

				// get photo and signature dtls
				if (obj[43] != null) {
					Long lLngSrNo = 0L;
					CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
					CmnAttachmentMst lObjCmnAttachmentMst = null;
					if (obj[43] != null) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(obj[43].toString()));

						cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

						if (lObjCmnAttachmentMst != null) {
							lLngPhotoAttachmentId = lObjCmnAttachmentMst.getAttachmentId();
						}
						if (lObjCmnAttachmentMst != null && lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
							cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
						}
						cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();

						for (int j = 0; j < cmnAttachmentMpgs.size(); j++) {
							cmnAttachmentMpg = cmnAttachmentMpgIterator.next();
							if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("photo")) {
								lLngSrNo = cmnAttachmentMpg.getSrNo();
								inputMap.put("Photo", lObjCmnAttachmentMst);
								inputMap.put("PhotoId", lLngPhotoAttachmentId);
								inputMap.put("PhotosrNo", lLngSrNo);
							}
						}
					}

					if (obj[44] != null) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(obj[44].toString()));

						cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

						if (lObjCmnAttachmentMst != null) {
							lLngSignAttachmentId = lObjCmnAttachmentMst.getAttachmentId();
						}
						if (lObjCmnAttachmentMst != null && lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
							cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
						}

						cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();

						for (int j = 0; j < cmnAttachmentMpgs.size(); j++) {
							cmnAttachmentMpg = cmnAttachmentMpgIterator.next();
							if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("signature")) {
								lLngSrNo = cmnAttachmentMpg.getSrNo();
								inputMap.put("Sign", lObjCmnAttachmentMst);
								inputMap.put("SignId", lLngSignAttachmentId);
								inputMap.put("SignsrNo", lLngSrNo);
							}
						}
					}
				}
				if (obj[45] != null) {
					lObjPensionAuditRegisterVO.setReducedPension(new BigDecimal(obj[45].toString()));
				}
				if (obj[46] != null) {
					lObjPensionAuditRegisterVO.setPnsnSanctionAuthority(obj[46].toString());
				}
				if (obj[47] != null) {
					lObjPensionAuditRegisterVO.setCommutationAuthority(obj[47].toString());
				}
				if (obj[48] != null) {
					lObjPensionAuditRegisterVO.setLibraryNo(obj[48].toString());
				}
				if (obj[49] != null) {
					if((gObjRsrcBndle.getString("PPMT.ROPTYPE1986")).equals(obj[49]))
						lObjPensionAuditRegisterVO.setRopType(gObjLblRsrcBndle.getString("PPMT.1986"));
					else if((gObjRsrcBndle.getString("PPMT.ROPTYPE1996")).equals(obj[49]))
						lObjPensionAuditRegisterVO.setRopType(gObjLblRsrcBndle.getString("PPMT.1996"));
					else if((gObjRsrcBndle.getString("PPMT.ROPTYPE2006")).equals(obj[49]))
						lObjPensionAuditRegisterVO.setRopType(gObjLblRsrcBndle.getString("PPMT.2006"));
					
					//lObjPensionAuditRegisterVO.setRopType(obj[49].toString());
				}
				if (obj[50] != null) {
					lObjPensionAuditRegisterVO.setBranchCode(obj[50].toString());
				}
				if (obj[51] != null) {
					lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(obj[51].toString()));
				}
				if (obj[52] != null) {
					lObjPensionAuditRegisterVO.setSeries(obj[52].toString());
				}
				if (obj[53] != null) {
					lObjPensionAuditRegisterVO.setCvpAuthority(obj[53].toString());
				}
			}
			//lLstTrnProvisionalPensionDtls = lObjPhysicalCaseInwardDAO.getProvisionalPensionDtls(lStrPensionerCode);
			lLstPensionerNomineeDtls = lObjPhysicalCaseInwardDAO.getMstPensionerNomineeDtlsVO(lStrPensionerCode);
			
			inputMap.put("TreasuryName", lStrTreasuryName);
			inputMap.put("CurrDate", lObjSimpleDateFormat.format(gDtCurrDt));
			inputMap.put("PensionAuditRegisterVO", lObjPensionAuditRegisterVO);
			//inputMap.put("TrnProvisionalPensionDtls", lLstTrnProvisionalPensionDtls);
			inputMap.put("PensionerNomineeDtls", lLstPensionerNomineeDtls);
			inputMap.put("PpoNo", lObjPensionAuditRegisterVO.getPpoNo());
			
			resObj.setViewName("AuditRegisterPensionerDtls");
			resObj.setResultValue(inputMap);
			
		} catch (Exception e) {
			
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}
	
	public ResultObject viewAuditRegisterRptCvpDtls(Map<String, Object> inputMap) {
		
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List lLstResult = new ArrayList();
		String lStrTreasuryName = "";
		List<Short> lLstBillStatus = new ArrayList<Short>();
		List<HstCommutationDtls> lLstHstCommutationDtls = new ArrayList<HstCommutationDtls>();
		HstCommutationDtls lObjHstCommutationDtls = new HstCommutationDtls();
		try {
			String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			String lStrPpoNo = StringUtility.getParameter("ppoNo", request);
			String lStrPensionerName = StringUtility.getParameter("pensionerName", request);
			String lStrFinYearId =  StringUtility.getParameter("finYear", request);
//			if(lStrFinYear != null && !"".equals(lStrFinYear))
//			{
//				Integer lIntFinYear = Integer.parseInt(lStrFinYear);
//				lStrFinYear = lStrFinYear +" - "+ (lIntFinYear+1);
//			}
			PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			SgvcFinYearMst lObjSgvcFinYearMst = new SgvcFinYearMst();
			lObjSgvcFinYearMst = lObjCommonDAO.getFinYearDtlsFromFinYearId(lStrFinYearId);
			
			lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
			
			if(lStrPensionerCode != null && !"".equals(lStrPensionerCode))
			{
				lLstResult = lObjPensionpayQueryDAO.getAuditRegisterRptCvpDtls(lStrPensionerCode,gStrLocCode,lLstBillStatus);
			}
			lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocCode));
			
			if(lLstResult != null && !lLstResult.isEmpty())
			{
				for(Integer lIntCnt=0;lIntCnt<lLstResult.size();lIntCnt++)
				{
					lObjHstCommutationDtls =new HstCommutationDtls();
					Object[] obj=(Object[])lLstResult.get(lIntCnt);
					if(obj[0]!=null)
					{
						lObjHstCommutationDtls.setOrderNo(obj[0].toString());
					}
					if(obj[1]!=null)
					{
						lObjHstCommutationDtls.setBasicPensionAmt(new BigDecimal(obj[1].toString()));
					}
					if(obj[2]!=null)
					{
						lObjHstCommutationDtls.setCvpMonthlyAmt(new BigDecimal(obj[2].toString()));
					}
					if(obj[3]!=null)
					{
						lObjHstCommutationDtls.setReducedPensionAmt(new BigDecimal(obj[3].toString()));
					}
					if(obj[4]!=null)
					{
						lObjHstCommutationDtls.setCvpStartDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[4])));
					}
					if(obj[5]!=null)
					{
						lObjHstCommutationDtls.setCvpEndDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[5])));
					}
					if(obj[6]!=null)
					{
						lObjHstCommutationDtls.setPaymentAmount(new BigDecimal(obj[6].toString()));
					}
					if(obj[7]!=null)
					{
						lObjHstCommutationDtls.setChequeNo(obj[7].toString());
					}
					if(obj[8]!=null)
					{
						lObjHstCommutationDtls.setChequeDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[8])));
					}
					if(obj[9]!=null)
					{
						lObjHstCommutationDtls.setVoucherNo(obj[9].toString());
					}
					if(obj[10]!=null)
					{
						lObjHstCommutationDtls.setVoucherDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[10])));
					}
					lLstHstCommutationDtls.add(lObjHstCommutationDtls);
				}
			}
			inputMap.put("TreasuryName", lStrTreasuryName);
			inputMap.put("PensionerName", lStrPensionerName);
			inputMap.put("PpoNo", lStrPpoNo);
			inputMap.put("FinYearDesc", lObjSgvcFinYearMst.getFinYearDesc());
			inputMap.put("lLstHstCommutationDtls", lLstHstCommutationDtls);
			
			resObj.setViewName("AuditRegisterCommutationDtls");
			resObj.setResultValue(inputMap);
			
		} catch (Exception e) {
			
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
	
		}
	return resObj;
	}
	
	public ResultObject viewAuditRegisterRptGratuityDtls(Map<String, Object> inputMap) {
		
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List lLstResult = new ArrayList();
		String lStrTreasuryName = "";
		List<Short> lLstBillStatus = new ArrayList<Short>();
		List<HstCommutationDtls> lLstHstCommutationDtls = new ArrayList<HstCommutationDtls>();
		HstCommutationDtls lObjHstCommutationDtls = new HstCommutationDtls();
		try {
			String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			String lStrPpoNo = StringUtility.getParameter("ppoNo", request);
			String lStrPensionerName = StringUtility.getParameter("pensionerName", request);
			String lStrFinYearId =  StringUtility.getParameter("finYear", request);
//			if(lStrFinYear != null && !"".equals(lStrFinYear))
//			{
//				Integer lIntFinYear = Integer.parseInt(lStrFinYear);
//				lStrFinYear = lStrFinYear +" - "+ (lIntFinYear+1);
//			}
			PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			SgvcFinYearMst lObjSgvcFinYearMst = new SgvcFinYearMst();
			lObjSgvcFinYearMst = lObjCommonDAO.getFinYearDtlsFromFinYearId(lStrFinYearId);
			
			lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
			
			if(lStrPensionerCode != null && !"".equals(lStrPensionerCode))
			{
				lLstResult = lObjPensionpayQueryDAO.getAuditRegisterRptGratuityDtls(lStrPensionerCode,gStrLocCode,lLstBillStatus);
			}
			lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocCode));
			
			inputMap.put("TreasuryName", lStrTreasuryName);
			inputMap.put("PensionerName", lStrPensionerName);
			inputMap.put("PpoNo", lStrPpoNo);
			inputMap.put("FinYearDesc", lObjSgvcFinYearMst.getFinYearDesc());
			inputMap.put("lLstGratuityDtls", lLstResult);
			
			resObj.setViewName("AuditRegisterGratuityDtls");
			resObj.setResultValue(inputMap);
			
		} catch (Exception e) {
			
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
	
		}
	return resObj;
	}
	
	public ResultObject viewAuditRegisterRptPaymentHistoty(Map<String, Object> inputMap) {
		
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List lLstPaymentHst = new ArrayList();
		List<List> lLstPaymentHistory = new ArrayList<List>();
		List lLstResult = new ArrayList();
		String lStrTreasuryName = "";
		String lStrBankName = "";
		String lStrBranchName = "";
		String lStrAccountNo = "";
		String lStrFromMonthYear;
		String lStrToMonthYear;
		List<Short> lLstBillStatus = new ArrayList<Short>();
		List<HstCommutationDtls> lLstHstCommutationDtls = new ArrayList<HstCommutationDtls>();
		HstCommutationDtls lObjHstCommutationDtls = new HstCommutationDtls();
		Map<String,List> lMapPaymentHst = new HashMap<String,List>();
		Map<Integer,String> lMapMonthDesc = new HashMap<Integer,String>();
		try {
			String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			String lStrPpoNo = StringUtility.getParameter("ppoNo", request);
			String lStrPensionerName = StringUtility.getParameter("pensionerName", request);
			String lStrFinYear =  StringUtility.getParameter("finYear", request);
//			if(lStrFinYear != null && !"".equals(lStrFinYear))
//			{
//				Integer lIntFinYear = Integer.parseInt(lStrFinYear);
//				lStrFinYear = lStrFinYear +" - "+ (lIntFinYear+1);
//			}
			PensionpayQueryDAO lObjPensionpayQueryDAO = new PensionpayQueryDAOImpl(null, serv.getSessionFactory());
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			
			lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
			
			Calendar lObjCalendar = Calendar.getInstance();
			lObjCalendar.setTime(gDtCurrDt);
			Integer lIntYear = lObjCalendar.get(lObjCalendar.YEAR);
			lStrFromMonthYear = lIntYear + "03";
			lStrToMonthYear = (lIntYear +1) + "02";
			lStrFinYear = lIntYear + " - "+ (lIntYear +1);
			if(lStrPensionerCode != null && !"".equals(lStrPensionerCode))
			{
				lLstPaymentHst = lObjPensionpayQueryDAO.getAuditRegisterRptPaymentHistory(lStrPensionerCode, lStrFromMonthYear,lStrToMonthYear,lLstBillStatus);
				Integer lIntMonth = 2;
				
				String lStrMonthYear = "";
				
				if(lLstPaymentHst != null && !lLstPaymentHst.isEmpty())
				{
					for(Integer lIntCnt=0;lIntCnt < lLstPaymentHst.size();lIntCnt++)
					{
						String lStrKey = "";
						Object[] obj=(Object[])lLstPaymentHst.get(lIntCnt);
						if(obj[10] != null)
						{
							lStrKey = obj[10].toString();
						}
						List lLstData = Arrays.asList(obj);
						lMapPaymentHst.put(lStrKey, lLstData);
					}
				}
				Map<Integer,List> lMapMonthWisePayHst = new HashMap<Integer,List>();
				for(Integer lIntCnt=1;lIntCnt<=12;lIntCnt++)
				{
					++lIntMonth;
					if(lIntMonth < 10) {
						lStrMonthYear = lIntYear + "0"+ lIntMonth;
					}
					else {
						lStrMonthYear = lIntYear.toString() + lIntMonth;
					}
					List lLstData = lMapPaymentHst.get(lStrMonthYear);
					List lRowList = new ArrayList();
					if(lLstData != null)
					{
						lRowList = new ArrayList();
						lRowList.add(getMonthDesc(lIntMonth));
						lRowList.addAll(lLstData);
						lLstPaymentHistory.add(lRowList);
					}
					else
					{
						lRowList = new ArrayList();
						lRowList.add(getMonthDesc(lIntMonth));
						lRowList.add(" ");
						lRowList.add(" ");
						lRowList.add(" ");
						lRowList.add(" ");
						lRowList.add(" ");
						lRowList.add(" ");
						lRowList.add("");
						lRowList.add("");
						lRowList.add("");
						lRowList.add("");
						lRowList.add("");
						lLstPaymentHistory.add(lRowList);
					}
					
					if(lIntMonth == 12)
					{
						lIntMonth = 0;
						lIntYear = lIntYear + 1;
					}
				}
						
				lLstResult = lObjPensionpayQueryDAO.getBankBranchDtlsFromPnsnrCode(lStrPensionerCode);
				if(lLstResult != null && !lLstResult.isEmpty())
				{
					Object[] obj=(Object[])lLstResult.get(0);
					if(obj[0]!=null)
					{
						lStrBankName = obj[0].toString();
					}
					if(obj[1]!=null)
					{
						lStrBranchName = obj[1].toString();
					}
					if(obj[2]!=null)
					{
						lStrAccountNo = obj[2].toString();
					}
				}
			}
			lStrTreasuryName = lObjCommonDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocCode));
			
			inputMap.put("TreasuryName", lStrTreasuryName);
			inputMap.put("PensionerName", lStrPensionerName);
			inputMap.put("PpoNo", lStrPpoNo);
			inputMap.put("FinYear", lStrFinYear);
			inputMap.put("lLstPaymentHst", lLstPaymentHistory);
			inputMap.put("BankName",lStrBankName);
			inputMap.put("BranchName",lStrBranchName);
			inputMap.put("AccountNo",lStrAccountNo);
			
			resObj.setViewName("AuditRegisterPaymentHistory");
			resObj.setResultValue(inputMap);
			
		} catch (Exception e) {
			
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
	
		}
	return resObj;
	}
	
	String getMonthDesc(Integer lIntMonthId)
	{
		String lStrMonthDesc = "";
		switch (lIntMonthId) {
	        case 1:  lStrMonthDesc = "January";
	                 break;
	        case 2:  lStrMonthDesc = "February";
	                 break;
	        case 3:  lStrMonthDesc = "March";
	                 break;
	        case 4:  lStrMonthDesc = "April";
	                 break;
	        case 5:  lStrMonthDesc = "May";
	                 break;
	        case 6:  lStrMonthDesc = "June";
	                 break;
	        case 7:  lStrMonthDesc = "July";
	                 break;
	        case 8:  lStrMonthDesc = "August";
	                 break;
	        case 9:  lStrMonthDesc = "September";
	                 break;
	        case 10: lStrMonthDesc = "October";
	                 break;
	        case 11: lStrMonthDesc = "November";
	                 break;
	        case 12: lStrMonthDesc = "December";
	                 break;
	        default: lStrMonthDesc = "Invalid month";
	                 break;
		}
		return lStrMonthDesc;

	}
	
	public ResultObject viewAuditRegisterReport(Map<String, Object> inputMap) {
		
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List lLstResult = new ArrayList();
		String lStrTreasuryName = "";
		List<Short> lLstBillStatus = new ArrayList<Short>();
		List<HstCommutationDtls> lLstHstCommutationDtls = new ArrayList<HstCommutationDtls>();
		HstCommutationDtls lObjHstCommutationDtls = new HstCommutationDtls();
		try {
			String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			String lStrPpoNo = StringUtility.getParameter("ppoNo", request);
			String lStrPensionerName = StringUtility.getParameter("pensionerName", request);
			String lStrFinYear =  StringUtility.getParameter("finYear", request);
			
			resObj = serv.executeService("viewAuditRegisterRptPnsnrDtls", inputMap);
			resObj = serv.executeService("viewAuditRegisterRptCvpDtls", inputMap);
			resObj = serv.executeService("viewAuditRegisterRptGratuityDtls", inputMap);
			resObj = serv.executeService("viewAuditRegisterRptPaymentHistoty", inputMap);
			
			inputMap.put("showButtonFlag","N");
			resObj.setViewName("AuditPensionRegisterReport");
			resObj.setResultValue(inputMap);
			
		} catch (Exception e) {
			
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
	
		}
		return resObj;
	}
			
}
