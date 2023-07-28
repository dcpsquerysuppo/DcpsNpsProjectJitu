/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 14, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.dao.ChangeStatementDAO;
import com.tcs.sgv.pensionpay.dao.ChangeStatementDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAO;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MstChangeStmntCtgry;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnMonthlyChangeRqst;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionChangeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.reports.service.ReportServiceImpl;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Jun 14, 2011
 */
public class ChangeStatement extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(GenerateMandateReport.class);
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	Long gLngLocCode = null;
	ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
	private static ResourceBundle bundleCaseConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");
	Map lMapSeriesHeadCode = null;
	PensionpayQueryDAO gObjRptQueryDAO = null;
	LoginDetails lObjLoginVO = null;
	public Map<String, Integer> gMapPnsrCodeSrNo = new HashMap<String, Integer>();
	List<String> currMonthPnsrCodeLst = null;
	Map<String, TrnPensionChangeDtls> currMonthBillDtlsMap = null;
	Map<String, TrnPensionBillDtls> prevMonthBillDtlsMap = null;
	List<String> prevMonthPnsrCodeLst = null;

	public ChangeStatement() {

	}

	public ChangeStatement(SessionFactory lObjSessionFactory, Map lMapSeriesHeadCode, Long gLngLocCode) {

		this.lObjSessionFactory = lObjSessionFactory;
		this.lMapSeriesHeadCode = lMapSeriesHeadCode;
		this.gLngLocCode = gLngLocCode;
	}

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		List dataList = new ArrayList();
		ReportServiceImpl reportServiceImpl = null;
		Session ghibSession = null;
		try {
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");
			lObjLoginVO = (LoginDetails) sessionKeys.get("loginDetails");
			gLngLocCode = (Long) loginDetail.get("locationId");
			ghibSession = lObjSessionFactory.getCurrentSession();

			gObjRptQueryDAO = new PensionpayQueryDAOImpl(null, lObjSessionFactory);
			gObjRptQueryDAO.setReportHeaderAndFooter(report, lObjLoginVO);
			// gObjRptQueryDAO = new PensionpayQueryDAOImpl(null,
			// lObjSessionFactory);
			// gObjRptQueryDAO.setReportHeaderAndFooter(report, lObjLoginVO,
			// gLngLocCode.toString());
			if (gMapPnsrCodeSrNo == null) {
				gMapPnsrCodeSrNo = new HashMap<String, Integer>();
			}
			dataList = generateReportData(report);
			report.setParameterValue("gMapPnsrCodeSrNo", gMapPnsrCodeSrNo);
		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		}
		return dataList;

	}

	public List generateReportData(ReportVO report) {

		List dataList = new ArrayList();
		SimpleDateFormat lSdf1 = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lSdf2 = new SimpleDateFormat("MMM-yy");
		MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, lObjSessionFactory);
		ChangeStatementDAO lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnMonthlyChangeRqst.class, lObjSessionFactory);
		Integer lIntCurrMonthYear = null;
		Integer lIntPrevMonthYear = null;
		String lStrCurrMonthYear = "";
		String lStrPrevMonthYear = "";
		String LStrChngStmntCategory = null;
		TrnMonthlyChangeRqst lObjTrnMonthlyChangeRqst = null;
		String lStrForMonth = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		Date lDtEndDate = null;
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(lObjSessionFactory);
		PensionpayReportServiceImpl lObjPnsnpayReportSrvcImpl = new PensionpayReportServiceImpl();
		String lStrSchemeCode = null;
		String lStrBillNo = null;
		List<String> lLstAllChngRqstIds = null;
		String lFlagGenChngStmntBy = null;
		try {
			String lStrChangeRqstId = report.getParameterValue("changeRqstId").toString();
			lStrBillNo = (report.getParameterValue("billNo") != null) ? report.getParameterValue("billNo").toString() : null;
			lStrSchemeCode = (report.getParameterValue("schemeCode") != null) ? report.getParameterValue("schemeCode").toString() : null;
			lStrForMonth = (report.getParameterValue("forMonth") != null) ? report.getParameterValue("forMonth").toString() : null;
			if (!"".equals(lStrChangeRqstId)) {
				lFlagGenChngStmntBy = "B"; // Bank-Branch-wise Report
											// Generation
				lLstAllChngRqstIds = new ArrayList<String>();
				lLstAllChngRqstIds.add(lStrChangeRqstId);
				lObjTrnMonthlyChangeRqst = (TrnMonthlyChangeRqst) lObjChangeStatementDAO.read(Long.valueOf(lStrChangeRqstId));
				lStrForMonth = (lObjTrnMonthlyChangeRqst.getForMonth() != null) ? lObjTrnMonthlyChangeRqst.getForMonth().toString() : "";
				lStrBankCode = (lObjTrnMonthlyChangeRqst.getBankCode() != null) ? lObjTrnMonthlyChangeRqst.getBankCode().toString() : "";
				lStrBranchCode = (lObjTrnMonthlyChangeRqst.getBranchCode() != null) ? lObjTrnMonthlyChangeRqst.getBranchCode().toString() : "";
			}
			if (lStrForMonth != null && !"".equals(lStrForMonth)) {
				lIntCurrMonthYear = Integer.parseInt(lStrForMonth);
				if ((lStrForMonth.substring(4, 6)).equals("01")) {
					lIntPrevMonthYear = lIntCurrMonthYear - 89;
				} else {
					lIntPrevMonthYear = lIntCurrMonthYear - 1;
				}
				lStrCurrMonthYear = lSdf2.format(lSdf1.parse(lIntCurrMonthYear.toString()));
				lStrPrevMonthYear = lSdf2.format(lSdf1.parse(lIntPrevMonthYear.toString()));
			}
			lMapSeriesHeadCode = lObjCommonPensionDAO.getAllHeadCodeSeriesMap();
			if (report.getReportCode().equals("365454") || report.getReportCode().equals("365460")) {
				setCurrAndPrevMonthDtls(lStrChangeRqstId, lIntPrevMonthYear, lStrSchemeCode);
			}
			dataList = new ArrayList();

			// -----Change Statement Summary Report
			if (report.getReportCode().equals("365453")) {
				gMapPnsrCodeSrNo = (Map<String, Integer>) report.getParameterValue("gMapPnsrCodeSrNo");
				if (lStrBillNo != null) {
					lFlagGenChngStmntBy = "S"; // Schemewise Report
												// Generation.
					lLstAllChngRqstIds = getChangeRqstIdsForOuter(lStrBillNo);
				}
				dataList = getSummaryOfChngStmnt(lLstAllChngRqstIds, lIntPrevMonthYear, lStrSchemeCode, lFlagGenChngStmntBy);
				ReportColumnVO[] lArrRptCols = report.getReportColumns();
				lArrRptCols[2].setColumnHeader(lStrPrevMonthYear + " " + "Count");
				lArrRptCols[3].setColumnHeader(lStrPrevMonthYear + " " + "Amount");
				lArrRptCols[4].setColumnHeader(lStrCurrMonthYear + " " + "Count");
				lArrRptCols[5].setColumnHeader(lStrCurrMonthYear + " " + "Amount");
				if ("S".equals(lFlagGenChngStmntBy)) {
					lArrRptCols[0].setGroupByOrder(1);
					lArrRptCols[1].setGroupByOrder(2);
				}
				report.setReportColumns(lArrRptCols);
			}

			// -----Change Statement Report
			if (report.getReportCode().equals("365454")) {
				dataList = getChangeStmntDtls(lIntPrevMonthYear, lIntCurrMonthYear);
				report.setParameterValue("gMapPnsrCodeSrNo", gMapPnsrCodeSrNo);
			}
			// ----Recovery detail report
			if (report.getReportCode().equals("365460")) {
				gMapPnsrCodeSrNo = (Map<String, Integer>) report.getParameterValue("gMapPnsrCodeSrNo");
				dataList = getChangeStmntRecoveryDtls(lStrForMonth);
			}
		} catch (Exception e) {
			gLogger.error("Exception is in Change Statement Report :" + e, e);
		}
		return dataList;
	}

	public void setCurrAndPrevMonthDtls(String lStrChangeRqstId, Integer lIntPrevMonthYear, String lStrSchemeCode) throws Exception {

		List<Object[]> lLstPrevMonthBillDtl = null;
		List<Object[]> lLstCurrMonthBillDtl = null;
		MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, lObjSessionFactory);
		String lStrBranchCode = null;
		try {
			// ----Getting Current Month Bill Details From Change Statement
			lLstCurrMonthBillDtl = lObjMonthlyDAO.getCurrMnthChngStmntDtlVOList(lStrChangeRqstId, lStrSchemeCode);
			currMonthBillDtlsMap = new HashMap<String, TrnPensionChangeDtls>();
			currMonthPnsrCodeLst = new ArrayList<String>();
			for (Object[] lArrObj : lLstCurrMonthBillDtl) {
				lStrBranchCode = (lArrObj[2] != null) ? lArrObj[2].toString() : null;
				currMonthPnsrCodeLst.add(lArrObj[0].toString());
				currMonthBillDtlsMap.put(lArrObj[0].toString(), (TrnPensionChangeDtls) lArrObj[1]);
			}

			// ----Getting Previous Month Bill Details
			if (lStrBranchCode != null) {
				lLstPrevMonthBillDtl = lObjMonthlyDAO.getPrevMonthBillDtlVOList(lStrBranchCode, gLngLocCode.toString(), lIntPrevMonthYear, lStrSchemeCode);
				prevMonthBillDtlsMap = new HashMap<String, TrnPensionBillDtls>();
				prevMonthPnsrCodeLst = new ArrayList<String>();
				for (Object[] lArrObj : lLstPrevMonthBillDtl) {
					prevMonthPnsrCodeLst.add(lArrObj[0].toString());
					prevMonthBillDtlsMap.put(lArrObj[0].toString(), (TrnPensionBillDtls) lArrObj[1]);
				}
			}
		} catch (Exception e) {
			gLogger.error("Exception Occured in setCurrAndPrevMonthDtls :" + e, e);
			throw e;
		}
	}

	public List getSummaryOfChngStmnt(List<String> lLstAllChngRqstIds, Integer lIntPrevMonthYear, String lStrSchemeCode, String lFlagGenChngStmntBy) throws Exception {

		Object[] lArrChngStmntSummary = null;
		Object[] lArrPrevMonthSummary = null;
		MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, lObjSessionFactory);
		Long lLngCurrMonthPnsrCount = null;
		Long lLngPrevMonthPnsrCount = null;
		Long lLngCurrMonthTotNetAmount = null;
		Long lLngPrevMonthTotNetAmount = null;
		String lStrBankName = null;
		String lStrBranchName = null;
		Long lLngDiffAmount = null;
		List rowList = null;
		String lStrBranchCode = null;
		List dataList = new ArrayList();
		try {
			String urlPrefixChngStmnt = "ifms.htm?actionFlag=reportService&reportCode=365454&action=generateReport&DirectReport=TRUE&asPopup=TRUE&displayOK=TRUE";
			for (String lStrChangeRqstId : lLstAllChngRqstIds) {
				lArrChngStmntSummary = lObjMonthlyDAO.getCurrMnthChngStmntSummary(lStrChangeRqstId, lStrSchemeCode);
				lStrBranchCode = (lArrChngStmntSummary[4] != null) ? lArrChngStmntSummary[4].toString() : null;
				lLngCurrMonthPnsrCount = ((Integer) lArrChngStmntSummary[0]).longValue();
				lLngCurrMonthTotNetAmount = ((lArrChngStmntSummary[1] != null) ? ((Double) lArrChngStmntSummary[1]).longValue() : 0);
				lStrBankName = (lArrChngStmntSummary[2] != null) ? lArrChngStmntSummary[2].toString() : "";
				lStrBranchName = (lArrChngStmntSummary[3] != null) ? lArrChngStmntSummary[3].toString() : "";
				if (lStrBranchCode != null) {
					lArrPrevMonthSummary = lObjMonthlyDAO.getPrevMonthSummary(lStrBranchCode, gLngLocCode.toString(), lIntPrevMonthYear, lStrSchemeCode);
					lLngPrevMonthPnsrCount = (Long) lArrPrevMonthSummary[0];
					lLngPrevMonthTotNetAmount = ((lArrPrevMonthSummary[1] != null) ? ((BigDecimal) lArrPrevMonthSummary[1]).longValue() : 0);
					lLngDiffAmount = lLngCurrMonthTotNetAmount - lLngPrevMonthTotNetAmount;
				}

				rowList = new ArrayList();
				rowList.add(lStrBankName);
				rowList.add(lStrBranchName);
				rowList.add(lLngPrevMonthPnsrCount);
				rowList.add(lLngPrevMonthTotNetAmount);
				rowList.add(lLngCurrMonthPnsrCount);
				rowList.add(lLngCurrMonthTotNetAmount);
				if ("B".equals(lFlagGenChngStmntBy)) {
					rowList.add(lLngDiffAmount);
				} else {
					rowList.add(new URLData(lLngDiffAmount, urlPrefixChngStmnt + "&schemeCode=" + lStrSchemeCode + "&changeRqstId=" + lStrChangeRqstId));
				}
				dataList.add(rowList);
			}
		} catch (Exception e) {
			gLogger.error("Exception Occured in getSummaryOfChngStmnt :" + e, e);
			throw e;
		}
		return dataList;
	}

	public List addBillDataToReport(String lStrCurrMonthYear, String lStrPrevMonthYear, List dataList, List<String> lLstPnsrForCategory, Map<String, TrnPensionChangeDtls> lMapCurrMonthBillDtls,
			Map<String, TrnPensionBillDtls> lMapPrevMonthBillDtls, String lStrCrtCategory) {

		Integer lIntSrNo = 1;
		TrnPensionBillDtls lObjTrnPensionBillDtls = null;
		TrnPensionChangeDtls lObjTrnPensionChangeDtls = null;
		ArrayList rowList = null;
		String lStrLedgerNo = null;
		String lStrPageNo = null;
		for (String lStrPnsrCode : lLstPnsrForCategory) {

			// ---For current month bill data
			lObjTrnPensionChangeDtls = lMapCurrMonthBillDtls.get(lStrPnsrCode);
			if (lObjTrnPensionChangeDtls != null) {
				lStrLedgerNo = (lObjTrnPensionChangeDtls.getLedgerNo() != null) ? lObjTrnPensionChangeDtls.getLedgerNo() + "/" : "";
				lStrPageNo = (lObjTrnPensionChangeDtls.getPageNo() != null) ? lObjTrnPensionChangeDtls.getPageNo() : "";
				rowList = new ArrayList();
				rowList.add(lIntSrNo);
				rowList.add(lMapSeriesHeadCode.get(new BigDecimal(lObjTrnPensionChangeDtls.getHeadCode())) + " - " + lObjTrnPensionChangeDtls.getPpoNo() + " - " + lStrLedgerNo + lStrPageNo);
				rowList.add(lObjTrnPensionChangeDtls.getPensionerName());
				rowList.add(lStrCurrMonthYear);
				rowList.add((lObjTrnPensionChangeDtls.getPensionAmount()).doubleValue() + (lObjTrnPensionChangeDtls.getAdpAmount()).doubleValue());
				rowList.add(lObjTrnPensionChangeDtls.getDpAmount());
				rowList.add(lObjTrnPensionChangeDtls.getTiAmount());
				rowList.add(lObjTrnPensionChangeDtls.getIr1Amount());
				rowList.add(lObjTrnPensionChangeDtls.getIr2Amount());
				rowList.add(lObjTrnPensionChangeDtls.getIr3Amount());
				rowList.add((lObjTrnPensionChangeDtls.getArrearAmount()).doubleValue() + (lObjTrnPensionChangeDtls.getTiArrearAmount()).doubleValue());
				rowList.add(lObjTrnPensionChangeDtls.getCvpMonthAmount());
				rowList.add(lObjTrnPensionChangeDtls.getGrossAmount());
				rowList.add(lObjTrnPensionChangeDtls.getRecoveryAmount());
				rowList.add(lObjTrnPensionChangeDtls.getNetAmount());
				rowList.add(lStrCrtCategory);
				dataList.add(rowList);
			}

			// ---For previous month bill data
			lObjTrnPensionBillDtls = lMapPrevMonthBillDtls.get(lStrPnsrCode);
			if (lObjTrnPensionBillDtls != null) {
				lStrLedgerNo = (lObjTrnPensionBillDtls.getLedgerNo() != null) ? lObjTrnPensionBillDtls.getLedgerNo() + "/" : "";
				lStrPageNo = (lObjTrnPensionBillDtls.getPageNo() != null) ? lObjTrnPensionBillDtls.getPageNo() + "/" : "";
				rowList = new ArrayList();
				// ---If category is 'pensioner removed from other treasury'
				// ,then show ppo/category/volume/pageno and pensioner name
				if (lObjTrnPensionChangeDtls != null) {
					rowList.add("");
					rowList.add("");
					rowList.add("");
				} else {
					rowList.add(lIntSrNo);
					rowList.add(lMapSeriesHeadCode.get(lObjTrnPensionBillDtls.getHeadCode()) + " - " + lObjTrnPensionBillDtls.getPpoNo() + " - " + lStrLedgerNo + lStrPageNo);
					rowList.add(lObjTrnPensionBillDtls.getPensionerName());
				}
				rowList.add(lStrPrevMonthYear);
				rowList.add((lObjTrnPensionBillDtls.getPensionAmount()).doubleValue() + (lObjTrnPensionBillDtls.getAdpAmount()).doubleValue());
				rowList.add(lObjTrnPensionBillDtls.getDpAmount());
				rowList.add(lObjTrnPensionBillDtls.getTiAmount());
				rowList.add(lObjTrnPensionBillDtls.getIr1Amount());
				rowList.add(lObjTrnPensionBillDtls.getIr2Amount());
				rowList.add(lObjTrnPensionBillDtls.getIr3Amount());
				rowList.add((lObjTrnPensionBillDtls.getArrearAmount()).doubleValue() + (lObjTrnPensionBillDtls.getTiArrearAmount()).doubleValue());
				rowList.add(lObjTrnPensionBillDtls.getCvpMonthAmount());
				rowList.add(lObjTrnPensionBillDtls.getGrossAmount());
				rowList.add(lObjTrnPensionBillDtls.getRecoveryAmount());
				rowList.add(lObjTrnPensionBillDtls.getNetAmount());
				rowList.add(lStrCrtCategory);
				dataList.add(rowList);
			}
			lIntSrNo++;
		}
		return dataList;
	}

	private List prepareChngStmntReport(Map lMapChngStmntReport) throws Exception {

		List dataList = new ArrayList();
		ArrayList lLstCurrMonthRow = null;
		ArrayList lLstPrevMonthRow = null;
		TrnPensionBillDtls lObjTrnPensionBillDtls = null;
		TrnPensionChangeDtls lObjTrnPensionChangeDtls = null;
		Integer lIntSrNo = 1;
		Integer lIntVolNo = null;
		Integer lIntPageNo = null;
		StyleVO[] boldRedFontVO = new StyleVO[2];
		boldRedFontVO[0] = new StyleVO();
		boldRedFontVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldRedFontVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		boldRedFontVO[1] = new StyleVO();
		boldRedFontVO[1].setStyleId(IReportConstants.BACKGROUNDCOLOR);
		boldRedFontVO[1].setStyleValue("#E34234");
		Map<Integer, Map<Integer, List<String>>> lMapVolumePage = new TreeMap<Integer, Map<Integer, List<String>>>();
		Set<Integer> lSetVolumneNo = new TreeSet<Integer>();
		Set<Integer> lSetPageNo = new TreeSet<Integer>();
		Map<Integer, List<String>> lMapOrderPagePnsrCode = null;
		List<String> lLstOrderPageWisePnsrCode = new ArrayList<String>();
		try {
			Map<String, TrnPensionChangeDtls> lMapCurrMonthBillDtls = (Map<String, TrnPensionChangeDtls>) lMapChngStmntReport.get("lMapCurrMonthBillDtls");
			Map<String, TrnPensionBillDtls> lMapPrevMonthBillDtls = (Map<String, TrnPensionBillDtls>) lMapChngStmntReport.get("lMapPrevMonthBillDtls");
			String lStrCurrMonthYear = (String) lMapChngStmntReport.get("lStrCurrMonthYear");
			String lStrPrevMonthYear = (String) lMapChngStmntReport.get("lStrPrevMonthYear");
			Set<String> lSetUniquePensioner = (Set<String>) lMapChngStmntReport.get("lSetUniquePensioner");
			List<String> lLstPnsrForAddedToTresCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForAddedToTresCategory");
			List<String> lLstPnsrForTransferToBranchCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForTransferToBranchCategory");
			List<String> lLstPnsrForRemovedFromTresCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForRemovedFromTresCategory");
			List<String> lLstPnsrForTransferFromBranchCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForTransferFromBranchCategory");
			List<String> lLstPnsrForDACategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForDACategory");
			List<String> lLstPnsrForIncBasicCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForIncBasicCategory");
			List<String> lLstPnsrForDecBasicCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForDecBasicCategory");
			List<String> lLstPnsrForRecCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForRecCategory");
			List<String> lLstPnsrForArrearCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForArrearCategory");
			List<String> lLstPnsrForOthrArrearCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForOthrArrearCategory");
			List<String> lLstPnsrForCVPMonthlyCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForCVPMonthlyCategory");
			List<String> lLstPnsrForClosedCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForClosedCategory");
			List<String> lLstPnsrForNotSeenCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForNotSeenCategory");
			List<String> lLstPnsrForNetAmtZero = (List<String>) lMapChngStmntReport.get("lLstPnsrForNetAmtZero");
			List<String> lLstPnsrForReturnDueToReleasePauseCategory = (List<String>) lMapChngStmntReport.get("lLstPnsrForReturnDueToReleasePauseCategory");

			// ----Preparing Volumne No , Page No ,Pensioner Code wise maps
			// starts <<<<<
			for (String lStrPnsrCode : lSetUniquePensioner) {
				Map<Integer, List<String>> lMapPagePnsrCode = null;
				List<String> lLstPageWisePnsrCode = null;
				lObjTrnPensionChangeDtls = lMapCurrMonthBillDtls.get(lStrPnsrCode);
				lObjTrnPensionBillDtls = lMapPrevMonthBillDtls.get(lStrPnsrCode);
				if (lObjTrnPensionChangeDtls != null) {
					lIntVolNo = (lObjTrnPensionChangeDtls.getLedgerNo() != null && !"".equals(lObjTrnPensionChangeDtls.getLedgerNo())) ? Integer.valueOf(lObjTrnPensionChangeDtls.getLedgerNo()) : 0;
					lIntPageNo = (lObjTrnPensionChangeDtls.getPageNo() != null && !"".equals(lObjTrnPensionChangeDtls.getPageNo())) ? Integer.valueOf(lObjTrnPensionChangeDtls.getPageNo()) : 0;
				} else if (lObjTrnPensionBillDtls != null) {
					lIntVolNo = (lObjTrnPensionBillDtls.getLedgerNo() != null && !"".equals(lObjTrnPensionBillDtls.getLedgerNo())) ? Integer.valueOf(lObjTrnPensionBillDtls.getLedgerNo()) : 0;
					lIntPageNo = (lObjTrnPensionBillDtls.getPageNo() != null && !"".equals(lObjTrnPensionBillDtls.getPageNo())) ? Integer.valueOf(lObjTrnPensionBillDtls.getPageNo()) : 0;
				}
				lMapPagePnsrCode = lMapVolumePage.get(lIntVolNo);
				if (lMapPagePnsrCode != null) {
					lLstPageWisePnsrCode = lMapPagePnsrCode.get(lIntPageNo);
					if (lLstPageWisePnsrCode != null) {
						lLstPageWisePnsrCode.add(lStrPnsrCode);
						lMapPagePnsrCode.put(lIntPageNo, lLstPageWisePnsrCode);
					} else {
						lLstPageWisePnsrCode = new ArrayList<String>();
						lLstPageWisePnsrCode.add(lStrPnsrCode);
						lMapPagePnsrCode.put(lIntPageNo, lLstPageWisePnsrCode);
					}
				} else {
					lMapPagePnsrCode = new TreeMap<Integer, List<String>>();
					lLstPageWisePnsrCode = new ArrayList<String>();
					lLstPageWisePnsrCode.add(lStrPnsrCode);
					lMapPagePnsrCode.put(lIntPageNo, lLstPageWisePnsrCode);
				}
				lMapVolumePage.put(lIntVolNo, lMapPagePnsrCode);
			}
			// ----Preparing Volume No , Page No ,Pensioner Code wise maps ends
			// >>>>>>

			// -----Showing records order by Volume No,Page No. starts <<<<<<
			lSetVolumneNo = lMapVolumePage.keySet();
			String lStrLedgerNo = null;
			String lStrPageNo = null;
			for (Integer lIntOrderVolumeNo : lSetVolumneNo) {
				lMapOrderPagePnsrCode = lMapVolumePage.get(lIntOrderVolumeNo);
				lSetPageNo = lMapOrderPagePnsrCode.keySet();
				for (Integer lIntOrderPageNo : lSetPageNo) {
					lLstOrderPageWisePnsrCode = lMapOrderPagePnsrCode.get(lIntOrderPageNo);
					for (String lStrPnsrCode : lLstOrderPageWisePnsrCode) {
						// ---For Current Month Bill Data
						lObjTrnPensionChangeDtls = lMapCurrMonthBillDtls.get(lStrPnsrCode);
						if (lObjTrnPensionChangeDtls != null) {
							lStrLedgerNo = (lObjTrnPensionChangeDtls.getLedgerNo() != null) ? lObjTrnPensionChangeDtls.getLedgerNo() : "";
							lStrPageNo = (lObjTrnPensionChangeDtls.getPageNo() != null) ? lObjTrnPensionChangeDtls.getPageNo() : "";
							lLstCurrMonthRow = new ArrayList();
							lLstCurrMonthRow.add(lIntSrNo);
							lLstCurrMonthRow.add(lMapSeriesHeadCode.get(new BigDecimal(lObjTrnPensionChangeDtls.getHeadCode())) + " - " + lObjTrnPensionChangeDtls.getPpoNo());
							lLstCurrMonthRow.add(lStrLedgerNo);
							lLstCurrMonthRow.add(lStrPageNo);
							lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getPensionerName());
							lLstCurrMonthRow.add(lStrCurrMonthYear);
							if (lLstPnsrForIncBasicCategory.contains(lStrPnsrCode) || lLstPnsrForDecBasicCategory.contains(lStrPnsrCode)) {
								lLstCurrMonthRow.add(new StyledData((lObjTrnPensionChangeDtls.getPensionAmount()).longValue(), boldRedFontVO));
							} else {
								lLstCurrMonthRow.add((lObjTrnPensionChangeDtls.getPensionAmount()).longValue());
							}
							lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getAdpAmount().longValue());
							lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getDpAmount().longValue());

							if (lLstPnsrForDACategory.contains(lStrPnsrCode)) {
								lLstCurrMonthRow.add(new StyledData(lObjTrnPensionChangeDtls.getTiAmount().longValue(), boldRedFontVO));
							} else {
								lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getTiAmount().longValue());
							}
							lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getIr1Amount().longValue());
							lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getIr2Amount().longValue());
							lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getIr3Amount().longValue());

							if (lLstPnsrForArrearCategory.contains(lStrPnsrCode) || lLstPnsrForOthrArrearCategory.contains(lStrPnsrCode)) {
								lLstCurrMonthRow.add(new StyledData((lObjTrnPensionChangeDtls.getArrearAmount()).longValue() + (lObjTrnPensionChangeDtls.getTiArrearAmount()).longValue()
										+ lObjTrnPensionChangeDtls.getPeonAllowance().longValue() + lObjTrnPensionChangeDtls.getMedicalAllowenceAmount().longValue()
										+ lObjTrnPensionChangeDtls.getOtherBenefits().longValue() + lObjTrnPensionChangeDtls.getOther1().longValue()
										+ lObjTrnPensionChangeDtls.getArrear6PC().longValue() + lObjTrnPensionChangeDtls.getArrearOthrDiff().longValue()
										+ lObjTrnPensionChangeDtls.getArrearCommutation().longValue() + lObjTrnPensionChangeDtls.getArrearLC().longValue(), boldRedFontVO));
							} else {
								lLstCurrMonthRow.add((lObjTrnPensionChangeDtls.getArrearAmount()).longValue() + (lObjTrnPensionChangeDtls.getTiArrearAmount()).longValue()
										+ lObjTrnPensionChangeDtls.getPeonAllowance().longValue() + lObjTrnPensionChangeDtls.getMedicalAllowenceAmount().longValue()
										+ lObjTrnPensionChangeDtls.getOtherBenefits().longValue() + lObjTrnPensionChangeDtls.getOther1().longValue()
										+ lObjTrnPensionChangeDtls.getArrear6PC().longValue() + lObjTrnPensionChangeDtls.getArrearOthrDiff().longValue()
										+ lObjTrnPensionChangeDtls.getArrearCommutation().longValue() + lObjTrnPensionChangeDtls.getArrearLC().longValue());
							}

							if (lLstPnsrForCVPMonthlyCategory.contains(lStrPnsrCode)) {
								lLstCurrMonthRow.add(new StyledData(lObjTrnPensionChangeDtls.getCvpMonthAmount().longValue(), boldRedFontVO));
							} else {
								lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getCvpMonthAmount().longValue());
							}
							lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getGrossAmount().longValue());

							if (lLstPnsrForRecCategory.contains(lStrPnsrCode)) {
								lLstCurrMonthRow.add(new StyledData(lObjTrnPensionChangeDtls.getRecoveryAmount().longValue(), boldRedFontVO));
							} else {
								lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getRecoveryAmount().longValue());
							}
							if (lLstPnsrForNetAmtZero.contains(lStrPnsrCode)) {
								lLstCurrMonthRow.add(new StyledData(lObjTrnPensionChangeDtls.getNetAmount().longValue(), boldRedFontVO));
							} else {
								lLstCurrMonthRow.add(lObjTrnPensionChangeDtls.getNetAmount().longValue());
							}
							dataList.add(lLstCurrMonthRow);
						}

						// ---For previous month bill data
						lObjTrnPensionBillDtls = lMapPrevMonthBillDtls.get(lStrPnsrCode);
						if (lObjTrnPensionBillDtls != null) {
							lStrLedgerNo = (lObjTrnPensionBillDtls.getLedgerNo() != null) ? lObjTrnPensionBillDtls.getLedgerNo() : "";
							lStrPageNo = (lObjTrnPensionBillDtls.getPageNo() != null) ? lObjTrnPensionBillDtls.getPageNo() : "";
							lLstPrevMonthRow = new ArrayList();
							// ---If category is 'pensioner removed from other
							// treasury'
							// ,then show ppo/category/volume/pageno and
							// pensioner name
							if (lObjTrnPensionChangeDtls != null) {
								lLstPrevMonthRow.add("");
								lLstPrevMonthRow.add("");
								lLstPrevMonthRow.add("");
								lLstPrevMonthRow.add("");
								lLstPrevMonthRow.add("");
							} else {
								lLstPrevMonthRow.add(lIntSrNo);
								lLstPrevMonthRow.add(lMapSeriesHeadCode.get(lObjTrnPensionBillDtls.getHeadCode()) + " - " + lObjTrnPensionBillDtls.getPpoNo());
								lLstPrevMonthRow.add(lStrLedgerNo);
								lLstPrevMonthRow.add(lStrPageNo);
								lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getPensionerName());
							}
							lLstPrevMonthRow.add(lStrPrevMonthYear);

							if (lLstPnsrForIncBasicCategory.contains(lStrPnsrCode) || lLstPnsrForDecBasicCategory.contains(lStrPnsrCode)) {
								lLstPrevMonthRow.add(new StyledData((lObjTrnPensionBillDtls.getPensionAmount()).longValue(), boldRedFontVO));
							} else {
								lLstPrevMonthRow.add((lObjTrnPensionBillDtls.getPensionAmount()).longValue());
							}
							lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getAdpAmount().longValue());
							lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getDpAmount().longValue());

							if (lLstPnsrForDACategory.contains(lStrPnsrCode)) {
								lLstPrevMonthRow.add(new StyledData(lObjTrnPensionBillDtls.getTiAmount().longValue(), boldRedFontVO));
							} else {
								lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getTiAmount().longValue());
							}

							lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getIr1Amount().longValue());
							lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getIr2Amount().longValue());
							lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getIr3Amount().longValue());

							if (lLstPnsrForArrearCategory.contains(lStrPnsrCode) || lLstPnsrForOthrArrearCategory.contains(lStrPnsrCode)) {
								lLstPrevMonthRow.add(new StyledData((lObjTrnPensionBillDtls.getArrearAmount()).longValue() + (lObjTrnPensionBillDtls.getTiArrearAmount()).longValue()
										+ lObjTrnPensionBillDtls.getPeonAllowance().longValue() + lObjTrnPensionBillDtls.getMedicalAllowenceAmount().longValue()
										+ lObjTrnPensionBillDtls.getOtherBenefits().longValue() + lObjTrnPensionBillDtls.getOther1().longValue() + lObjTrnPensionBillDtls.getArrear6PC().longValue()
										+ lObjTrnPensionBillDtls.getArrearCommutation().longValue() + lObjTrnPensionBillDtls.getArrearOthrDiff().longValue()
										+ lObjTrnPensionBillDtls.getArrearLC().longValue(), boldRedFontVO));
							} else {
								lLstPrevMonthRow.add((lObjTrnPensionBillDtls.getArrearAmount()).longValue() + (lObjTrnPensionBillDtls.getTiArrearAmount()).longValue()
										+ lObjTrnPensionBillDtls.getPeonAllowance().longValue() + lObjTrnPensionBillDtls.getMedicalAllowenceAmount().longValue()
										+ lObjTrnPensionBillDtls.getOtherBenefits().longValue() + lObjTrnPensionBillDtls.getOther1().longValue() + lObjTrnPensionBillDtls.getArrear6PC().longValue()
										+ lObjTrnPensionBillDtls.getArrearCommutation().longValue() + lObjTrnPensionBillDtls.getArrearOthrDiff().longValue()
										+ lObjTrnPensionBillDtls.getArrearLC().longValue());
							}

							if (lLstPnsrForCVPMonthlyCategory.contains(lStrPnsrCode)) {
								lLstPrevMonthRow.add(new StyledData(lObjTrnPensionBillDtls.getCvpMonthAmount().longValue(), boldRedFontVO));
							} else {
								lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getCvpMonthAmount().longValue());
							}

							lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getGrossAmount().longValue());
							if (lLstPnsrForRecCategory.contains(lStrPnsrCode)) {
								lLstPrevMonthRow.add(new StyledData(lObjTrnPensionBillDtls.getRecoveryAmount().longValue(), boldRedFontVO));
							} else {
								lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getRecoveryAmount().longValue());
							}

							// ---If pensioner is present in current month then
							// only checking for category 15 for previous month.
							if (lLstPnsrForNetAmtZero.contains(lStrPnsrCode)) {
								if ((lObjTrnPensionBillDtls.getGrossAmount().longValue() > 0) && (lObjTrnPensionBillDtls.getNetAmount().longValue() == 0)) {
									lLstPrevMonthRow.add(new StyledData(lObjTrnPensionBillDtls.getNetAmount().longValue(), boldRedFontVO));
								} else {
									lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getNetAmount().longValue());
								}
							} else {
								lLstPrevMonthRow.add(lObjTrnPensionBillDtls.getNetAmount().longValue());
							}
							dataList.add(lLstPrevMonthRow);
						} else {
							lLstPrevMonthRow = new ArrayList();
							lLstPrevMonthRow.add("");
							lLstPrevMonthRow.add("");
							lLstPrevMonthRow.add("");
							lLstPrevMonthRow.add("");
							lLstPrevMonthRow.add("");
							lLstPrevMonthRow.add(lStrPrevMonthYear);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							lLstPrevMonthRow.add(0);
							dataList.add(lLstPrevMonthRow);
						}
						gMapPnsrCodeSrNo.put(lStrPnsrCode, lIntSrNo);
						lIntSrNo++;
					}
				}
			}
			// -----Showing records order by Volume No,Page No. ends >>>>>
		} catch (Exception e) {
			gLogger.error("Exception Occured in prepareChangStmntReport :" + e, e);
			throw e;
		}
		return dataList;
	}

	public List getChangeStmntDtls(Integer lIntPrevMonthYear, Integer lIntCurrMonthYear) throws Exception {

		String lStrPensionerCode = null;
		String lStrLocCode = null;
		String lStrStatus = null;
		String lStrSeenFlag = null;
		List<String> lLstPnsrAddedToBranch = null;
		List<String> lLstPnsrForCategory = null;
		List<String> lLstPnsrForAddedToTresCategory = new ArrayList<String>();
		List<String> lLstPnsrForTransferToBranchCategory = new ArrayList<String>();
		List<Object[]> lLstPnsrRemovedFromBranchDtls = null;
		List<String> lLstPnsrRemovedFromBranch = null;
		List<String> lLstPnsrForRemovedFromTresCategory = new ArrayList<String>();
		List<Object[]> lLstPnsrReleasePauseDtls = new ArrayList<Object[]>();
		List<Integer> lLstChngStmntCategories = new ArrayList<Integer>();
		List<String> lLstPnsrForClosedCategory = new ArrayList<String>();
		List<String> lLstPnsrForTransferFromBranchCategory = new ArrayList<String>();
		List<String> lLstPnsrForNotSeenCategory = new ArrayList<String>();
		List<String> lLstPnsrForArrearCategory = new ArrayList<String>();
		List<String> lLstPnsrForCVPMonthlyCategory = new ArrayList<String>();
		List<String> lLstPnsrForOthrArrearCategory = new ArrayList<String>();
		List<String> lLstPnsrForReturnDueToReleasePauseCategory = new ArrayList<String>();
		List<String> lLstPnsrForNetAmtZero = new ArrayList<String>();
		List<String> lLstPnsrForDACategory = new ArrayList<String>();
		List<String> lLstPnsrForIncBasicCategory = new ArrayList<String>();
		List<String> lLstPnsrForDecBasicCategory = new ArrayList<String>();
		List<String> lLstPnsrForRecCategory = new ArrayList<String>();
		List<Object[]> lLstPnsrAddedToBranchDtls = null;
		Set<String> lSetUniquePensioner = new HashSet<String>();
		Map lMapChngStmntReport = new HashMap();
		TrnPensionChangeDtls lObjTrnPensionChangeDtls = null;
		TrnPensionBillDtls lObjTrnPensionBillDtls = null;
		Integer lIntPpoEndMonthYear = 0;
		Double lDblCurrArrearAmt = 0D;
		Double lDblPrevArrearAmt = 0D;
		SimpleDateFormat lSdf1 = new SimpleDateFormat("yyyyMM");
		PensionConfigDAO lObjPensionConfigDAO = new PensionConfigDAOImpl(MstChangeStmntCtgry.class, lObjSessionFactory);
		MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, lObjSessionFactory);
		List dataList = new ArrayList();
		SimpleDateFormat lSdf2 = new SimpleDateFormat("MMM-yy");
		try {
			// ---Getting list of selected category by ato.
			lLstChngStmntCategories = lObjPensionConfigDAO.getSelectedChngStmntCatgry(gLngLocCode);
			/**
			 * ----Category id of all categories should be in
			 * lArrAllChangStmntCategory -----Do not change order of this
			 * array.On change of this order ,change condition(i.e arrayIndex in
			 * conditions) of preparing category-wise list also.
			 */

			Integer[] lArrAllChangStmntCategory = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

			// ---Setting is category selected for treasury flag to default
			// N.
			String[] lArrIsCategorySelected = {"", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N"};

			// -----If category is selected for current treasury (from ato
			// admin screen) then setting is Category Seleted flag to Y
			for (int cnt = 0; cnt < lArrAllChangStmntCategory.length; cnt++) {
				if (lLstChngStmntCategories.contains(lArrAllChangStmntCategory[cnt])) {
					lArrIsCategorySelected[cnt] = "Y";
				}
			}

			// ---Getting List of Pensioners Added To Bank Branch or added
			// to treasury( pensioner
			// present in current month and not present in previous month)
			// or Pensioners whose case status was Withheld in previous
			// month and continue in current month
			// (Category 1,2,14)
			if ("Y".equals(lArrIsCategorySelected[1]) || "Y".equals(lArrIsCategorySelected[2]) || "Y".equals(lArrIsCategorySelected[14])) {
				lLstPnsrForCategory = new ArrayList<String>();
				lLstPnsrForCategory.addAll(currMonthPnsrCodeLst);
				lLstPnsrForCategory.removeAll(prevMonthPnsrCodeLst);

				// ---Getting list of pensioners for category 14.(Return due
				// to release pause)
				if (!lLstPnsrForCategory.isEmpty()) {
					lLstPnsrReleasePauseDtls = lObjMonthlyDAO.getPnsrWithPreMonthHeldStatus(lLstPnsrForCategory, lIntPrevMonthYear);
					String lStrCaseStatus = null;
					String lStrHeldStatus = bundleCaseConst.getString("STATUS.WITHHELD");
					for (Object[] lObjArr : lLstPnsrReleasePauseDtls) {
						lStrCaseStatus = (lObjArr[1] != null) ? lObjArr[1].toString() : null;
						if (lStrCaseStatus != null && lStrHeldStatus.equals(lStrCaseStatus)) {
							if (lObjArr[0] != null) {
								lLstPnsrForReturnDueToReleasePauseCategory.add(lObjArr[0].toString());
							}
						}
					}
				}

				// ---Removing pensioners of category 14 from list to check
				// for category 1 and category 2.
				lLstPnsrForCategory.removeAll(lLstPnsrForReturnDueToReleasePauseCategory);
				if (!lLstPnsrForCategory.isEmpty()) {
					lLstPnsrAddedToBranchDtls = lObjMonthlyDAO.getPnsrAddedToBranchDtls(lLstPnsrForCategory, lIntPrevMonthYear);
					for (Object[] lObjArr : lLstPnsrAddedToBranchDtls) {
						if (lObjArr[0] != null) {
							lStrPensionerCode = lObjArr[0].toString();
							lLstPnsrAddedToBranch = new ArrayList<String>();
							lLstPnsrAddedToBranch.add(lStrPensionerCode);
							// --lLstPnsrAddedToBranch list contains
							// pensioner
							// that
							// are
							// --either added to treasury
							// or added to branch
							lStrLocCode = (lObjArr[1] != null) ? lObjArr[1].toString() : "";
							// ---If location code for previous month is
							// different
							// from current month then adding pensioner to
							// added
							// to
							// treasury category else to transfer to branch
							// category
							if (!lStrLocCode.equals(gLngLocCode.toString())) {
								lLstPnsrForAddedToTresCategory.add(lStrPensionerCode);
							} else {
								lLstPnsrForTransferToBranchCategory.add(lStrPensionerCode);
							}
						}
					}
					// --Pensioners that are not in lLstPnsrAddedToBranch
					// list
					// are
					// fresh cases.So adding them to added to tres category
					if (lLstPnsrAddedToBranch != null) {
						lLstPnsrForCategory.removeAll(lLstPnsrAddedToBranch);
					}
					lLstPnsrForAddedToTresCategory.addAll(lLstPnsrForCategory);
				}
				lSetUniquePensioner.addAll(lLstPnsrForAddedToTresCategory);
				lSetUniquePensioner.addAll(lLstPnsrForTransferToBranchCategory);
				lSetUniquePensioner.addAll(lLstPnsrForReturnDueToReleasePauseCategory);
				// dataList = addBillDataToReport(lStrCurrMonthYear,
				// lStrPrevMonthYear, dataList,
				// lLstPnsrForAddedToTresCategory, lMapCurrMonthBillDtls,
				// lMapPrevMonthBillDtls, bundleConst
				// .getString("CHANGSTMNT.CATEGORY1"));

				// dataList = addBillDataToReport(lStrCurrMonthYear,
				// lStrPrevMonthYear, dataList,
				// lLstPnsrForTransferToBranchCategory,
				// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
				// .getString("CHANGSTMNT.CATEGORY2"));
			}

			// ---Getting List of pensioner that are transferred from
			// treasury or from bank branch(pensioner present in previous
			// month and not present
			// in current month)
			// (Category 3,4,12,13)
			if ("Y".equals(lArrIsCategorySelected[3]) || "Y".equals(lArrIsCategorySelected[4]) || "Y".equals(lArrIsCategorySelected[12]) || "Y".equals(lArrIsCategorySelected[13])) {
				lLstPnsrForCategory = new ArrayList<String>();
				lLstPnsrForCategory.addAll(prevMonthPnsrCodeLst);
				lLstPnsrForCategory.removeAll(currMonthPnsrCodeLst);
				if (!lLstPnsrForCategory.isEmpty()) {
					lLstPnsrRemovedFromBranchDtls = lObjMonthlyDAO.getPnsrRemovedFromBranchDtls(lLstPnsrForCategory);
					for (Object[] lObjArr : lLstPnsrRemovedFromBranchDtls) {
						if (lObjArr[0] != null) {
							lStrPensionerCode = (lObjArr[0] != null) ? lObjArr[0].toString() : "";
							lStrLocCode = (lObjArr[1] != null) ? lObjArr[1].toString() : "";
							lStrStatus = (lObjArr[2] != null) ? lObjArr[2].toString() : "";
							lStrSeenFlag = (lObjArr[3] != null) ? lObjArr[3].toString() : "";
							lIntPpoEndMonthYear = (lObjArr[4] != null) ? Integer.valueOf(lSdf1.format((Date) lObjArr[4])) : 0;
							if (!lStrLocCode.equals(gLngLocCode.toString())) {
								lLstPnsrForRemovedFromTresCategory.add(lStrPensionerCode);
							} else if (lStrStatus.equals(bundleCaseConst.getString("STATUS.ENDOFPNSN"))) {
								lLstPnsrForClosedCategory.add(lStrPensionerCode);
							} // else if (lStrSeenFlag.equals("N")) {
								// lLstPnsrForNotSeenCategory.add(lStrPensionerCode);
								// }
							else if (lIntPpoEndMonthYear != 0 && lIntPpoEndMonthYear < lIntCurrMonthYear) {
								lLstPnsrForClosedCategory.add(lStrPensionerCode); // --putting
								// pensioners
								// whose
								// end
								// date
								// is
								// in
								// previous
								// month
								// in ppo
								// closed
								// category

							} else {
								lLstPnsrForTransferFromBranchCategory.add(lStrPensionerCode);
							}
						}
					}
					lSetUniquePensioner.addAll(lLstPnsrForRemovedFromTresCategory);
					lSetUniquePensioner.addAll(lLstPnsrForTransferFromBranchCategory);
					lSetUniquePensioner.addAll(lLstPnsrForClosedCategory);
					// lSetUniquePensioner.addAll(lLstPnsrForNotSeenCategory);
					// dataList = addBillDataToReport(lStrCurrMonthYear,
					// lStrPrevMonthYear, dataList,
					// lLstPnsrForRemovedFromTresCategory,
					// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls,
					// bundleConst
					// .getString("CHANGSTMNT.CATEGORY3"));

					// dataList = addBillDataToReport(lStrCurrMonthYear,
					// lStrPrevMonthYear, dataList,
					// lLstPnsrForTransferFromBranchCategory,
					// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls,
					// bundleConst
					// .getString("CHANGSTMNT.CATEGORY4"));
				}
			}

			// ---Getting List of pensioner whose basic amount or da rate is
			// changed or have recovery in current month
			// (ie. for category 5,6,7,8,9,10,11)
			lLstPnsrForCategory = new ArrayList<String>();
			lLstPnsrForCategory.addAll(currMonthPnsrCodeLst);
			lLstPnsrForCategory.retainAll(prevMonthPnsrCodeLst);
			for (String lStrPnsrCode : lLstPnsrForCategory) {
				lObjTrnPensionChangeDtls = currMonthBillDtlsMap.get(lStrPnsrCode);
				lObjTrnPensionBillDtls = prevMonthBillDtlsMap.get(lStrPnsrCode);
				// --Sum of all arrear amounts except ti arrear amount.
				lDblCurrArrearAmt = lObjTrnPensionChangeDtls.getArrearAmount().doubleValue() + lObjTrnPensionChangeDtls.getPeonAllowance().doubleValue()
						+ lObjTrnPensionChangeDtls.getMedicalAllowenceAmount().doubleValue() + lObjTrnPensionChangeDtls.getOtherBenefits().doubleValue()
						+ lObjTrnPensionChangeDtls.getOther1().doubleValue() + lObjTrnPensionChangeDtls.getArrear6PC().doubleValue() + lObjTrnPensionChangeDtls.getArrearCommutation().doubleValue()
						+ lObjTrnPensionChangeDtls.getArrearLC().doubleValue() + lObjTrnPensionChangeDtls.getArrearOthrDiff().doubleValue(); // other1
				// amount
				// is
				// gallantry
				// amount
				lDblPrevArrearAmt = lObjTrnPensionBillDtls.getArrearAmount().doubleValue() + lObjTrnPensionBillDtls.getPeonAllowance().doubleValue()
						+ lObjTrnPensionBillDtls.getMedicalAllowenceAmount().doubleValue() + lObjTrnPensionBillDtls.getOtherBenefits().doubleValue() + lObjTrnPensionBillDtls.getOther1().doubleValue()
						+ lObjTrnPensionBillDtls.getArrear6PC().doubleValue() + lObjTrnPensionBillDtls.getArrearCommutation().doubleValue() + lObjTrnPensionBillDtls.getArrearLC().doubleValue()
						+ lObjTrnPensionBillDtls.getArrearOthrDiff().doubleValue();
				// --List of pensioner having da rate change(Category 5)
				// if ("Y".equals(lArrIsCategorySelected[5])) {
				// if
				// (!(lObjTrnPensionChangeDtls.getDaRate()).equals(lObjTrnPensionBillDtls.getDaRate()))
				// {
				// lLstPnsrForDACategory.add(lStrPnsrCode);
				// }
				// }

				// --List of pensioner having increase in basic
				// amount(Category 6)
				if ("Y".equals(lArrIsCategorySelected[6])) {
					if ((lObjTrnPensionChangeDtls.getPensionAmount().doubleValue()) > (lObjTrnPensionBillDtls.getPensionAmount().doubleValue())) {
						lLstPnsrForIncBasicCategory.add(lStrPnsrCode);
					}
				}

				// --List of pensioner having descrease in basic
				// amount(Category 7)
				if ("Y".equals(lArrIsCategorySelected[7])) {
					if ((lObjTrnPensionChangeDtls.getPensionAmount().doubleValue()) < (lObjTrnPensionBillDtls.getPensionAmount().doubleValue())) {
						lLstPnsrForDecBasicCategory.add(lStrPnsrCode);
					}
				}

				// --List of pensioner having recovery amount(Category 8)
				if ("Y".equals(lArrIsCategorySelected[8])) {
					if (!(lObjTrnPensionChangeDtls.getRecoveryAmount()).equals(lObjTrnPensionBillDtls.getRecoveryAmount())) {
						lLstPnsrForRecCategory.add(lStrPnsrCode);
					}
				}

				// --List of pensioner having arrear amount(Category 9)
				if ("Y".equals(lArrIsCategorySelected[9])) {
					// lDblCurrArrearAmt =
					// lObjTrnPensionChangeDtls.getTiArrearAmount().doubleValue()
					// +
					// lObjTrnPensionChangeDtls.getArrearAmount().doubleValue();
					// lDblPrevArrearAmt =
					// lObjTrnPensionBillDtls.getTiArrearAmount().doubleValue()
					// +
					// lObjTrnPensionBillDtls.getArrearAmount().doubleValue();
					if (!lDblCurrArrearAmt.equals(lDblPrevArrearAmt)) {
						lLstPnsrForArrearCategory.add(lStrPnsrCode);
					}
				}

				// --List of pensioners having other arrear amount in
				// current and previous month(Category 10)
				if ("Y".equals(lArrIsCategorySelected[10])) {
					if ((lDblCurrArrearAmt > 0) && (lDblPrevArrearAmt > 0)) {
						lLstPnsrForOthrArrearCategory.add(lStrPnsrCode);
					}
				}

				// --List of pensioner having difference in cvp monthly
				// amount(Category 11)
				if ("Y".equals(lArrIsCategorySelected[11])) {
					if (!(lObjTrnPensionChangeDtls.getCvpMonthAmount()).equals(lObjTrnPensionBillDtls.getCvpMonthAmount())) {
						lLstPnsrForCVPMonthlyCategory.add(lStrPnsrCode);
					}
				}
			}

			// ----List of pensioners having gross amount greater than zero
			// and net amount zero for current month(Category 15)
			if ("Y".equals(lArrIsCategorySelected[15])) {
				lLstPnsrForCategory = new ArrayList<String>();
				lLstPnsrForCategory.addAll(currMonthPnsrCodeLst);
				for (String lStrPnsrCode : lLstPnsrForCategory) {
					lObjTrnPensionChangeDtls = currMonthBillDtlsMap.get(lStrPnsrCode);
					if (lObjTrnPensionChangeDtls != null) {
						if ((lObjTrnPensionChangeDtls.getGrossAmount().doubleValue() > 0) && (lObjTrnPensionChangeDtls.getNetAmount().doubleValue() == 0)) {
							lLstPnsrForNetAmtZero.add(lStrPnsrCode);
						}
					}
				}
			}

			// lSetUniquePensioner.addAll(lLstPnsrForDACategory);
			lSetUniquePensioner.addAll(lLstPnsrForIncBasicCategory);
			lSetUniquePensioner.addAll(lLstPnsrForDecBasicCategory);
			lSetUniquePensioner.addAll(lLstPnsrForRecCategory);
			lSetUniquePensioner.addAll(lLstPnsrForArrearCategory);
			lSetUniquePensioner.addAll(lLstPnsrForOthrArrearCategory);
			lSetUniquePensioner.addAll(lLstPnsrForCVPMonthlyCategory);
			lSetUniquePensioner.addAll(lLstPnsrForNetAmtZero);

			lMapChngStmntReport.put("lMapCurrMonthBillDtls", currMonthBillDtlsMap);
			lMapChngStmntReport.put("lMapPrevMonthBillDtls", prevMonthBillDtlsMap);
			lMapChngStmntReport.put("lStrCurrMonthYear", lSdf2.format(lSdf1.parse(lIntCurrMonthYear.toString())));
			lMapChngStmntReport.put("lStrPrevMonthYear", lSdf2.format(lSdf1.parse(lIntPrevMonthYear.toString())));
			lMapChngStmntReport.put("lSetUniquePensioner", lSetUniquePensioner);
			lMapChngStmntReport.put("lLstPnsrForAddedToTresCategory", lLstPnsrForAddedToTresCategory);
			lMapChngStmntReport.put("lLstPnsrForTransferToBranchCategory", lLstPnsrForTransferToBranchCategory);
			lMapChngStmntReport.put("lLstPnsrForRemovedFromTresCategory", lLstPnsrForRemovedFromTresCategory);
			lMapChngStmntReport.put("lLstPnsrForTransferFromBranchCategory", lLstPnsrForTransferFromBranchCategory);
			lMapChngStmntReport.put("lLstPnsrForDACategory", lLstPnsrForDACategory);
			lMapChngStmntReport.put("lLstPnsrForIncBasicCategory", lLstPnsrForIncBasicCategory);
			lMapChngStmntReport.put("lLstPnsrForDecBasicCategory", lLstPnsrForDecBasicCategory);
			lMapChngStmntReport.put("lLstPnsrForRecCategory", lLstPnsrForRecCategory);
			lMapChngStmntReport.put("lLstPnsrForArrearCategory", lLstPnsrForArrearCategory);
			lMapChngStmntReport.put("lLstPnsrForOthrArrearCategory", lLstPnsrForOthrArrearCategory);
			lMapChngStmntReport.put("lLstPnsrForCVPMonthlyCategory", lLstPnsrForCVPMonthlyCategory);
			lMapChngStmntReport.put("lLstPnsrForClosedCategory", lLstPnsrForClosedCategory);
			lMapChngStmntReport.put("lLstPnsrForNotSeenCategory", lLstPnsrForNotSeenCategory);
			lMapChngStmntReport.put("lLstPnsrForNetAmtZero", lLstPnsrForNetAmtZero);
			lMapChngStmntReport.put("lLstPnsrForReturnDueToReleasePauseCategory", lLstPnsrForReturnDueToReleasePauseCategory);

			dataList = prepareChngStmntReport(lMapChngStmntReport);

			// dataList = addBillDataToReport(lStrCurrMonthYear,
			// lStrPrevMonthYear, dataList, lLstPnsrForDACategory,
			// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
			// .getString("CHANGSTMNT.CATEGORY5"));
			//
			// dataList = addBillDataToReport(lStrCurrMonthYear,
			// lStrPrevMonthYear, dataList, lLstPnsrForIncBasicCategory,
			// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
			// .getString("CHANGSTMNT.CATEGORY6"));
			//
			// dataList = addBillDataToReport(lStrCurrMonthYear,
			// lStrPrevMonthYear, dataList, lLstPnsrForDecBasicCategory,
			// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
			// .getString("CHANGSTMNT.CATEGORY7"));
			//
			// dataList = addBillDataToReport(lStrCurrMonthYear,
			// lStrPrevMonthYear, dataList, lLstPnsrForRecCategory,
			// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
			// .getString("CHANGSTMNT.CATEGORY8"));
			//
			// dataList = addBillDataToReport(lStrCurrMonthYear,
			// lStrPrevMonthYear, dataList, lLstPnsrForArrearCategory,
			// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
			// .getString("CHANGSTMNT.CATEGORY9"));
			//
			// dataList = addBillDataToReport(lStrCurrMonthYear,
			// lStrPrevMonthYear, dataList, lLstPnsrForOthrArrearCategory,
			// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
			// .getString("CHANGSTMNT.CATEGORY10"));
			//
			// dataList = addBillDataToReport(lStrCurrMonthYear,
			// lStrPrevMonthYear, dataList, lLstPnsrForCVPMonthlyCategory,
			// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
			// .getString("CHANGSTMNT.CATEGORY11"));
			//
			// dataList = addBillDataToReport(lStrCurrMonthYear,
			// lStrPrevMonthYear, dataList, lLstPnsrForClosedCategory,
			// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
			// .getString("CHANGSTMNT.CATEGORY12"));
			//
			// dataList = addBillDataToReport(lStrCurrMonthYear,
			// lStrPrevMonthYear, dataList, lLstPnsrForNotSeenCategory,
			// lMapCurrMonthBillDtls, lMapPrevMonthBillDtls, bundleConst
			// .getString("CHANGSTMNT.CATEGORY13"));

		} catch (Exception e) {
			gLogger.error("Exception Occured in getChangeStmntDtls :" + e, e);
			throw e;
		}
		return dataList;
	}

	public List getChangeStmntRecoveryDtls(String lStrForMonth) throws Exception {

		List dataList = new ArrayList();
		Map<String, List<TrnPensionRecoveryDtls>> lMapRcvryDtl = new HashMap<String, List<TrnPensionRecoveryDtls>>();
		MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, lObjSessionFactory);
		List rowList = null;
		try {
			lMapRcvryDtl = lObjMonthlyDAO.getRecoveryDtlsForChngStmnt(currMonthPnsrCodeLst, lStrForMonth);
			Set<String> lSetPnsrCode = lMapRcvryDtl.keySet();
			Integer lIntSrNo = null;
			List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsVO = null;
			if (lSetPnsrCode != null && lSetPnsrCode.size() > 0 && gMapPnsrCodeSrNo != null) {
				for (String lStrPnsrCode : lSetPnsrCode) {
					rowList = new ArrayList();
					lIntSrNo = gMapPnsrCodeSrNo.get(lStrPnsrCode);
					rowList.add((lIntSrNo != null) ? lIntSrNo : 0);
					lLstTrnPensionRecoveryDtlsVO = lMapRcvryDtl.get(lStrPnsrCode);
					for (TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtlsVO : lLstTrnPensionRecoveryDtlsVO) {
						if (rowList.size() == 1) {
							if (bundleCaseConst.getString("PPMT.ASSESSEDDUES").equals(lObjTrnPensionRecoveryDtlsVO.getRecoveryType())) {
								rowList.add(lObjTrnPensionRecoveryDtlsVO.getRecoveryType() + "(" + lObjTrnPensionRecoveryDtlsVO.getNature() + ")");
							} else {
								rowList.add(lObjTrnPensionRecoveryDtlsVO.getRecoveryType());
							}
							rowList.add((lObjTrnPensionRecoveryDtlsVO.getSchemeCode() != null) ? lObjTrnPensionRecoveryDtlsVO.getSchemeCode() : "");
							rowList.add(lObjTrnPensionRecoveryDtlsVO.getAmount());
						} else {
							rowList = new ArrayList();
							rowList.add("");
							if (bundleCaseConst.getString("PPMT.ASSESSEDDUES").equals(lObjTrnPensionRecoveryDtlsVO.getRecoveryType())) {
								rowList.add(lObjTrnPensionRecoveryDtlsVO.getRecoveryType() + "(" + lObjTrnPensionRecoveryDtlsVO.getNature() + ")");
							} else {
								rowList.add(lObjTrnPensionRecoveryDtlsVO.getRecoveryType());
							}
							rowList.add((lObjTrnPensionRecoveryDtlsVO.getSchemeCode() != null) ? lObjTrnPensionRecoveryDtlsVO.getSchemeCode() : "");
							rowList.add(lObjTrnPensionRecoveryDtlsVO.getAmount());
						}
						dataList.add(rowList);
					}
				}
			} else {
				rowList = new ArrayList();
				rowList.add("");
				rowList.add("");
				rowList.add("");
				rowList.add("");
				dataList.add(rowList);
			}
		} catch (Exception e) {
			gLogger.error("Exception Occured in getChangeStmntRecoveryDtls :" + e, e);
			throw e;
		}
		return dataList;
	}

	public List<String> getChangeRqstIdsForOuter(String lStrBillNo) throws Exception {

		MonthlyPensionBillDAO lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, lObjSessionFactory);
		List dataList = null;
		List<String> lLstStrChngRqst = null;
		String[] lArrStrChngRqstIds = null;
		List<String> lLstAllChngRqstIds = new ArrayList<String>();
		try {
			if (lStrBillNo != null && !"".equals(lStrBillNo)) {
				lLstStrChngRqst = lObjMonthlyDAO.getChangeStmntRqstIdsForOuterByBillNo(Long.valueOf(lStrBillNo));
				for (String lStrChngRqstIds : lLstStrChngRqst) {
					lArrStrChngRqstIds = lStrChngRqstIds.split(",");
					for (String lStrChngRqstId : lArrStrChngRqstIds) {
						if (lStrChngRqstId.length() > 0) {
							lLstAllChngRqstIds.add(lStrChngRqstId);
						}
					}
				}
			}
		} catch (Exception e) {
			gLogger.error("Exception Occured in getChangeStmntOuter :" + e, e);
			throw e;
		}
		return lLstAllChngRqstIds;
	}
}
