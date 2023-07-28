package com.tcs.sgv.pensionpay.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.BillMovementDAOImpl;
import com.tcs.sgv.common.dao.BptmCommonServicesDAO;
import com.tcs.sgv.common.dao.BptmCommonServicesDAOImpl;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.dao.LocationDAOImpl;
import com.tcs.sgv.common.dao.RltBillPartyDAO;
import com.tcs.sgv.common.dao.RltBillPartyDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ReportExportHelper;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.BptmCommonServiceImpl;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.RltBillParty;
import com.tcs.sgv.common.valueobject.SgvaMonthMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.common.valueobject.TrnBillMvmnt;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pension.dao.TrnBillRegisterDAO;
import com.tcs.sgv.pension.dao.TrnBillRegisterDAOImpl;
import com.tcs.sgv.pensionpay.comparator.AccountComparator;
import com.tcs.sgv.pensionpay.comparator.LedgerComparator;
import com.tcs.sgv.pensionpay.comparator.PPONumberComparator;
import com.tcs.sgv.pensionpay.dao.ChangeStatementDAO;
import com.tcs.sgv.pensionpay.dao.ChangeStatementDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionBillDAO;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAO;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.SupplementaryBillDAO;
import com.tcs.sgv.pensionpay.dao.SupplementaryBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionOtherPartyPayDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionOtherPartyPayDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPnsncaseRopRltDAO;
import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.MstBankGrp;
import com.tcs.sgv.pensionpay.valueobject.MstPensionHeadcode;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;
import com.tcs.sgv.pensionpay.valueobject.TrnBillChangeStmntMpg;
import com.tcs.sgv.pensionpay.valueobject.TrnMonthlyChangeRqst;
import com.tcs.sgv.pensionpay.valueobject.TrnMonthlyPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillReceipt;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionChangeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionChangeHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionOtherPartyPay;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsncaseRopRlt;
import com.tcs.sgv.pensionpay.valueobject.ValidPcodeView;


public class MonthlyPensionBillServiceImpl extends ServiceImpl implements MonthlyPensionBillService {

	private Long gLngPostId = null;

	private Long gLngUserId = null;

	private Long gLngLangId = null;

	private Log gLogger = LogFactory.getLog(getClass());

	private Date gDate = null;

	private String gStrLocCode = null;

	private Long gDbId = null;

	private String gStrLocShortName = null;

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
	private ResourceBundle bundleCaseConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	// static HashMap sMapMonthly = new HashMap();

	public ResultObject loadMonthlyPension(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List<String> lLstAuditorBankCodeList = new ArrayList<String>();
		List<ComboValuesVO> lLstAuditor = new ArrayList<ComboValuesVO>();
		StringBuffer sbLog = new StringBuffer();
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		String lStrLocCode = SessionHelper.getLocationCode(inputMap).toString();
		PensionConfigDAO lObjPensionConfigDAO = null;
		setSessionInfo(inputMap);
		List<ComboValuesVO> lLstBankBrnchGroup = new ArrayList<ComboValuesVO>();
		try {
			List<SgvaMonthMst> lObjSgvaMonthMst = new ArrayList<SgvaMonthMst>();

			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lObjPensionConfigDAO = new PensionConfigDAOImpl(MstBankGrp.class, serv.getSessionFactory());
			String currentMonth = new SimpleDateFormat("MM").format(gDate);
			String currentYear = new SimpleDateFormat("yyyy").format(gDate);

			inputMap.put("CurrentMonth", Integer.parseInt(currentMonth));
			inputMap.put("CurrentYear", currentYear);

			lObjSgvaMonthMst = lObjCommonPensionDAO.getSgvaMonthMstVO(lStrLangId);

			if (lObjSgvaMonthMst != null) {
				inputMap.put("SgvaMonthMstVOArray", lObjSgvaMonthMst);
			}

			List<SgvcFinYearMst> lObjSgvcFinYearMst = new ArrayList<SgvcFinYearMst>();

			lObjSgvcFinYearMst = lObjCommonPensionDAO.getSgvcFinYearMstVO(lStrLangId);

			if (lObjSgvcFinYearMst != null) {
				inputMap.put("SgvcFinYearMstVOArray", lObjSgvcFinYearMst);
			}
			lLstAuditorBankCodeList = lObjCommonPensionDAO.getAuditorBankCodeList(gLngPostId, gStrLocCode);
			lLstBankBrnchGroup = lObjPensionConfigDAO.getBankGroupOfAuditor(gLngPostId, gStrLocCode);
			ArrayList<ComboValuesVO> arrBankCode = new ArrayList<ComboValuesVO>();
			if (lLstAuditorBankCodeList != null && !lLstAuditorBankCodeList.isEmpty()) {
				ComboValuesVO lObjComboValuesVO = null;
				Iterator it = lLstAuditorBankCodeList.iterator();
				while (it.hasNext()) {
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO = (ComboValuesVO) it.next();

					arrBankCode.add(lObjComboValuesVO);
				}
			}

			inputMap.put("ListAuditorBankCode", arrBankCode);
			inputMap.put("lLstBankBrnchGroup", lLstBankBrnchGroup);
			lLstAuditor = lObjCommonPensionDAO.getAllAuditor(lStrLocCode, Long.valueOf(lStrLangId));
			inputMap.put("lLstAuditor", lLstAuditor);

			/*
			 * MonthlyPensionBillDAOImpl lObjMonthlyPensionBill = new
			 * MonthlyPensionBillDAOImpl(MstPensionerHdr.class,
			 * serv.getSessionFactory());
			 * 
			 * List listPnsnScheme = (List)
			 * lObjMonthlyPensionBill.getAllScheme(gLngLangId);
			 * 
			 * if (listPnsnScheme != null) { inputMap.put("listScheme",
			 * listPnsnScheme); }
			 */
		} catch (Exception e) {
			sbLog.append("Problem in loading monthly para page is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				resObj.setThrowable(e);
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				resObj.setThrowable(ex);
			}
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("monthlyPension");
		return resObj;
	}

	public ResultObject printMonthlyBill(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		StringBuffer sbLine = new StringBuffer();
		List lLstNonBilledBrnchs = new ArrayList();
		int pgCoutner = 1;
		Map lLocDtls = new HashMap();
		BigDecimal netTotal = BigDecimal.ZERO;
		BigDecimal headCodeTotal = BigDecimal.ZERO;
		int lIntNoPnsn = 0;
		String result = "";
		String lStrheadCodePen = "";
		Map lMap = null;
		StringBuffer sbLog = new StringBuffer();
		RuleBasedNumberFormat fomatter = new RuleBasedNumberFormat(IReportConstants.INDIAN_ENG_RULE_SET);

		Double lDBMoneyOrderMinAmnt = 0D;

		Double lOtherPartyPay = 0.0;

		try {
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			String lStrSortBy = StringUtility.getParameter("cmbSortBy", request).trim();
			if (inputMap.containsKey("PrintString")) {
				sbLine = sbLine.append(inputMap.get("PrintString").toString());
			}
			setSessionInfo(inputMap);
			sbLine.append(getChar(132, " "));
			sbLine.append("\r\n");
			sbLine.append(getChar(132, " "));
			sbLine.append("\r\n");
			List MonthlyPensionList = (List) inputMap.get("MonthlyPensionList");

			if (MonthlyPensionList != null) {
				if ("ppoNo".equals(lStrSortBy)) {
					Collections.sort(MonthlyPensionList, new PPONumberComparator());
				} else if ("ledNo".equals(lStrSortBy)) {
					Collections.sort(MonthlyPensionList, new LedgerComparator());
				} else if ("accNo".equals(lStrSortBy)) {
					Collections.sort(MonthlyPensionList, new AccountComparator());
				}

				lIntNoPnsn = MonthlyPensionList.size();
			}

			MonthlyPensionBillVO MonthlyPensionVo = null;
			String BranchCode = null;

			MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());

			if (inputMap.containsKey("NonBilledBanks") && ((List) inputMap.get("NonBilledBanks")).size() != 0) {
				lLstNonBilledBrnchs = (List) inputMap.get("NonBilledBanks");
			} else if (inputMap.get("brnchCode") == null || "-1".equals(inputMap.get("brnchCode"))) {
				BranchCode = "";
			} else {
				BranchCode = inputMap.get("brnchCode").toString();
			}

			if (inputMap.containsKey("lLocDtls")) {
				lLocDtls = (Map) inputMap.get("lLocDtls");
			} else {
				lLocDtls = lObjMonthlyDAO.getHeaderDtl(BranchCode, gStrLocCode, gLngLangId, null);
			}

			String lStrTrsryName = "";
			String lStrCityName = "";

			if (((BranchCode != null && "".equals(BranchCode)) || BranchCode == null) && inputMap.containsKey("lLocDtls") && lLocDtls.containsKey("LocName~")) {

				lStrTrsryName = (((List) lLocDtls.get("LocName~")).get(0)).toString();
				inputMap.put("BankName", "");
				inputMap.put("BranchName", "");
			}

			else if ((lLstNonBilledBrnchs != null && lLstNonBilledBrnchs.size() > 0) || (BranchCode != null && !"".equals(BranchCode))) {
				if (inputMap.containsKey("brnchCode") && !"".equals(inputMap.get("brnchCode").toString())) {
					Object[] lObjTemp = (Object[]) lLocDtls.get(inputMap.get("brnchCode").toString() + "~");
					lStrTrsryName = lObjTemp[0].toString();
					String lStrBankName = lObjTemp[1].toString();
					String lStrBranchName = lObjTemp[2].toString();
					inputMap.put("BankName", lStrBankName);
					inputMap.put("BranchName", lStrBranchName);
				} else {
					inputMap.put("BankName", "");
					inputMap.put("BranchName", "");
				}
			}

			if (!"".equals(lStrTrsryName)) {
				lStrCityName = lStrTrsryName.split(",")[1];
			}

			inputMap.put("TreasuryName", lStrTrsryName);

			int headcodeCount = 0;
			String lStrHeadCode = null;
			MonthlyPensionBillVO lObjTempVo = new MonthlyPensionBillVO();

			int pageCounter = 1;
			if (MonthlyPensionList != null && MonthlyPensionList.size() > 0) {
				Long[] lLngFinalTotArray = new Long[20];
				for (int i = 0; i < 20; i++) {
					lLngFinalTotArray[i] = Long.valueOf(0L);
				}
				BigDecimal tempHeadCode = new BigDecimal(0);
				int recordCount = 0;
				int stratIndex = 0;
				int srNo = 1;
				Map lMapHeadDesc = (Map) inputMap.get("HeadDesc");

				for (int i = 0; i < MonthlyPensionList.size(); i++) {
					MonthlyPensionVo = (MonthlyPensionBillVO) MonthlyPensionList.get(i);
					if ("MONEY ORDER".equals(inputMap.get("Scheme")) && inputMap.containsKey("lMapROPPCode")) {
						if (((Map) inputMap.get("lMapROPPCode")).containsKey("ROP06" + MonthlyPensionVo.getPensionerCode())) {
							lDBMoneyOrderMinAmnt = 3500D;
						}
					}

					if (!tempHeadCode.equals(MonthlyPensionVo.getHeadCode()) || (recordCount - headcodeCount) % 15 == 0) {
						if (!tempHeadCode.equals(MonthlyPensionVo.getHeadCode())) {
							if (!tempHeadCode.equals(new BigDecimal(0))) {
								Map lTempMap = null;
								lMap = null;
								lStrheadCodePen = "Total No. of Pensioners under HeadCode " + tempHeadCode + " :";
								lTempMap = getFooterForMnthlyReport("Total Per HeadCode:", pageCounter, MonthlyPensionList, headcodeCount, i - 1, inputMap, lOtherPartyPay, lDBMoneyOrderMinAmnt);
								headCodeTotal = new BigDecimal(lTempMap.get("NetTotal").toString());
								result = fomatter.format(new com.ibm.icu.math.BigDecimal(headCodeTotal));
								sbLine.append(lTempMap.get("FooterString"));
								sbLine.append(getAbbrForReport());
								if ((recordCount - headcodeCount) % 15 >= 11 || (recordCount - headcodeCount) % 15 == 0) {
									sbLine.append("</pre></div>");
									sbLine.append("<div><pre>");
								}
								lMap = printSignature(inputMap, headCodeTotal, srNo - 1, result, lStrCityName, lStrheadCodePen);
								sbLine.append(lMap.get("Signature").toString());
								lOtherPartyPay = 0.0;

							}
							headcodeCount = i;
							srNo = 1;
						}
						sbLine.append("<div><pre>");

						lStrHeadCode = MonthlyPensionVo.getHeadCode().toString();
						inputMap.put("HeadCode", lStrHeadCode);
						inputMap.put("HeadCodeDesc", lMapHeadDesc.get(lStrHeadCode));

						tempHeadCode = MonthlyPensionVo.getHeadCode();
						sbLine.append(getHeadForMnthlyReport(pageCounter, inputMap, "Y"));

						pageCounter++;
					}
					sbLine.append(printRecord(MonthlyPensionVo, srNo++, 'B', lDBMoneyOrderMinAmnt));
					recordCount++;
					if ((recordCount - headcodeCount) % 15 == 0 && i + 1 < MonthlyPensionList.size() && ((MonthlyPensionBillVO) MonthlyPensionList.get(i + 1)).getHeadCode().equals(tempHeadCode)) {
						Map lTempMap = null;
						lTempMap = getFooterForMnthlyReport("Total Per Page:", pageCounter, MonthlyPensionList, stratIndex, i, inputMap, lOtherPartyPay, lDBMoneyOrderMinAmnt);
						sbLine.append(lTempMap.get("FooterString"));
						sbLine.append(getAbbrForReport());
						pgCoutner = pgCoutner + 1;
						stratIndex = stratIndex + 15;
						sbLine.append("</pre></div>");
					} else if (i + 1 < MonthlyPensionList.size()) {
						lObjTempVo = (MonthlyPensionBillVO) MonthlyPensionList.get(i + 1);
						if (!lObjTempVo.getHeadCode().equals(tempHeadCode)) {
							Map lTempMap = null;
							lTempMap = getFooterForMnthlyReport("Total Per Page:", pageCounter, MonthlyPensionList, stratIndex, i, inputMap, lOtherPartyPay, lDBMoneyOrderMinAmnt);
							sbLine.append(lTempMap.get("FooterString"));
							pgCoutner = pgCoutner + 1;
							stratIndex = i + 1;
						}
					} else {
						Map lTempMap = null;
						lMap = null;
						lStrheadCodePen = "Total No. of Pensioners under HeadCode " + tempHeadCode + " :";
						lTempMap = getFooterForMnthlyReport("Total Per Page:", pageCounter, MonthlyPensionList, stratIndex, MonthlyPensionList.size() - 1, inputMap, lOtherPartyPay,
								lDBMoneyOrderMinAmnt);
						sbLine.append(lTempMap.get("FooterString"));
						lTempMap = getFooterForMnthlyReport("Total Per HeadCode:", pageCounter, MonthlyPensionList, headcodeCount, i, inputMap, lOtherPartyPay, lDBMoneyOrderMinAmnt);
						sbLine.append(lTempMap.get("FooterString"));
						headCodeTotal = new BigDecimal(lTempMap.get("NetTotal").toString());
						result = fomatter.format(new com.ibm.icu.math.BigDecimal(headCodeTotal));
						sbLine.append("<div><pre>");
						lMap = printSignature(inputMap, headCodeTotal, srNo - 1, result, lStrCityName, lStrheadCodePen);
						sbLine.append(lMap.get("Signature").toString());
						lOtherPartyPay = 0.0;
					}
					if ("MONEY ORDER".equals(inputMap.get("Scheme"))) {
						lDBMoneyOrderMinAmnt = 2500D;
					}

				}

				sbLine.append(getHeadForMnthlyReport(pageCounter, inputMap, "F"));
				Map lTempMap = null;
				lTempMap = getFooterForMnthlyReport("Total Per Report:", pageCounter, MonthlyPensionList, 0, MonthlyPensionList.size() - 1, inputMap, lOtherPartyPay, lDBMoneyOrderMinAmnt);
				sbLine.append(lTempMap.get("FooterString"));
				netTotal = new BigDecimal(lTempMap.get("NetTotal").toString());
				inputMap.put("NetTotal", netTotal);
				sbLine.append(getAbbrForReport());

			}

			List CVPList = (List) inputMap.get("CVPList");
			List DCRGList = (List) inputMap.get("DCRGList");
			inputMap.put("pageCounter", pageCounter);

			if ((CVPList != null && DCRGList != null && CVPList.isEmpty() && DCRGList.isEmpty()) || (CVPList == null && DCRGList == null) || MonthlyPensionList.size() == 0) {
				lMap = null;
				lStrheadCodePen = "Total No. of Pensioners :";
				result = fomatter.format(new com.ibm.icu.math.BigDecimal(netTotal));
				lMap = printSignature(inputMap, netTotal, lIntNoPnsn, result, lStrCityName, lStrheadCodePen);
				sbLine.append(lMap.get("Signature").toString());
			}

			if (CVPList != null && !CVPList.isEmpty() && MonthlyPensionList.size() > 0) {
				sbLine.append("</pre></div>");
				sbLine.append(getCVPDCRGPrintReport("CVP", CVPList, inputMap));
				if (DCRGList == null || DCRGList.isEmpty()) {
					lMap = null;
					lStrheadCodePen = "Total No. of Pensioners :";
					netTotal = new BigDecimal(inputMap.get("NetTotal").toString());
					result = fomatter.format(new com.ibm.icu.math.BigDecimal(netTotal));
					lMap = printSignature(inputMap, netTotal, lIntNoPnsn, result, lStrCityName, lStrheadCodePen);
					sbLine.append(lMap.get("Signature").toString());
				}
			}
			if (DCRGList != null && !DCRGList.isEmpty() && MonthlyPensionList.size() > 0) {

				sbLine.append("</pre></div>");
				lMap = null;
				lStrheadCodePen = "Total No. of Pensioners :";
				sbLine.append(getCVPDCRGPrintReport("DCRG", DCRGList, inputMap));
				netTotal = new BigDecimal(inputMap.get("NetTotal").toString());
				result = fomatter.format(new com.ibm.icu.math.BigDecimal(netTotal));
				lMap = printSignature(inputMap, netTotal, lIntNoPnsn, result, lStrCityName, lStrheadCodePen);

				sbLine.append(lMap.get("Signature").toString());
			}

			// /////////////////////////////////////////////////////Vipul
			// Commented///////////////////////////
			/*
			 * // SSNL print from bill if(inputMap.containsKey("lOthPartyLst")
			 * && ((List)inputMap.get("lOthPartyLst")).size()>0) { List
			 * lOthPartyLst = (List)inputMap.get("lOthPartyLst");
			 * 
			 * List<TrnPensionOtherPartyPay> lstOthPartyPay = new
			 * ArrayList<TrnPensionOtherPartyPay>(); TrnPensionOtherPartyPay
			 * OthrPartyVo = null; int lSize =0;
			 * 
			 * if(inputMap.containsKey("lstParty")) { lstParty = (List)
			 * inputMap.get("lstParty"); Integer pageNum = (Integer)
			 * inputMap.get("pageCounter"); Long lTotalOtherPartyAmount = 0L;
			 * 
			 * for(int i =0;i<lstParty.size();i++) { lPartyNm =
			 * lstParty.get(i).toString().split("~")[1]; lnType =
			 * lstParty.get(i).toString().split("~")[0];
			 * 
			 * 
			 * for(Object o : lOthPartyLst) { lType = (Long) ((List) o).get(0);
			 * pCode = (String)((List) o).get(1); lBgAmnt = (BigDecimal) ((List)
			 * o).get(2); lPPONO = (String)((List) o).get(3); lName =
			 * (String)((List) o).get(4); lngAmont = new
			 * Long(lBgAmnt.setScale(0).toString());
			 * 
			 * if(tmpType.equals("")) {
			 * sbLine.append(getOtherPartyHeader(lPartyNm, inputMap,pageNum)); }
			 * 
			 * if(lnType.equals(lType.toString())) { sNo++; lSize++; OthrPartyVo
			 * = new TrnPensionOtherPartyPay();
			 * OthrPartyVo.setOtherPartyType(lType);
			 * OthrPartyVo.setPartyName(lPartyNm);
			 * OthrPartyVo.setPartyAmnt(lBgAmnt);
			 * OthrPartyVo.setPensionerCode(pCode); OthrPartyVo.setRejected(0);
			 * OthrPartyVo.setPpoNo(lPPONO);
			 * OthrPartyVo.setPensionerName(lName);
			 * lstOthPartyPay.add(OthrPartyVo);
			 * sbLine.append(printOtherPartyDtls
			 * (sNo,lPPONO,lName,lngAmont,inputMap)); lTotal +=lngAmont;
			 * OthrPartyVo = null; lTotalOtherPartyAmount += lngAmont; }
			 * if(!tmpType.equals(lnType) && !tmpType.equals("") &&
			 * lSize!=lOthPartyLst.size()) { sNo = 0; pageNum++;
			 * sbLine.append(printOtherPartyFooter
			 * (lStrCityName,lTotal,inputMap));
			 * sbLine.append(getOtherPartyHeader(lPartyNm, inputMap,pageNum));
			 * lTotal = 0l; }
			 * 
			 * tmpType = lnType; } }
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * sbLine.append(printOtherPartyFooter(lStrCityName,lTotal,inputMap))
			 * ; inputMap.put("TotalOtherPartyAmount", lTotalOtherPartyAmount);
			 * } if(lstOthPartyPay.size()>0) { HttpSession session =
			 * request.getSession(); session.setAttribute("lstOthPartyPay",
			 * lstOthPartyPay); }
			 * 
			 * } //SSNL print from bill
			 * 
			 * // SSNl Report
			 * if(inputMap.containsKey("otherPartyPaid"+BranchCode)) {
			 * 
			 * List<TrnPensionOtherPartyPay> lstOthPartyPay = new
			 * ArrayList<TrnPensionOtherPartyPay>(); lstOthPartyPay =
			 * (List<TrnPensionOtherPartyPay
			 * >)inputMap.get("otherPartyPaid"+BranchCode); tmpType = ""; sNo =
			 * 0; lTotal = 0l; Integer pageNum = (Integer)
			 * inputMap.get("pageCounter"); Long lTotalOtherPartyAmount = 0L;
			 * 
			 * for(TrnPensionOtherPartyPay o : lstOthPartyPay) {
			 * 
			 * lPartyNm = o.getPartyName(); lType = o.getOtherPartyType(); pCode
			 * = o.getPensionerCode(); lBgAmnt = o.getPartyAmnt(); lPPONO =
			 * o.getPpoNo(); lName = o.getPensionerName(); lngAmont = new
			 * Long(lBgAmnt.setScale(0).toString());
			 * 
			 * if(!tmpType.equals(lType.toString())) { if(!tmpType.equals("")) {
			 * sNo = 0; pageNum++;
			 * sbLine.append(printOtherPartyFooter(lStrCityName
			 * ,lTotal,inputMap)); lTotal = 0l; }
			 * sbLine.append(getOtherPartyHeader(lPartyNm, inputMap,pageNum));
			 * tmpType = lType.toString(); } sNo++;
			 * sbLine.append(printOtherPartyDtls
			 * (sNo,lPPONO,lName,lngAmont,inputMap)); lTotal +=lngAmont;
			 * lTotalOtherPartyAmount += lngAmont; }
			 * inputMap.put("TotalOtherPartyAmount", lTotalOtherPartyAmount);
			 * sbLine
			 * .append(printOtherPartyFooter(lStrCityName,lTotal,inputMap));
			 * 
			 * 
			 * }
			 * 
			 * ///////////////////////////////////////////////////////Vipul
			 * Commented/////////////////////////// // SSNL Report end
			 */
			DCRGList = null;
			CVPList = null;
			MonthlyPensionList = null;
			inputMap.put("PrintString", sbLine.toString());
			inputMap.put("DisplayString", sbLine.toString().replace("</pre></div>", "").replace("<div><pre>", ""));
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			sbLog.append("Problem in printing form-c is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				resObj.setThrowable(e);
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				resObj.setThrowable(ex);
			}
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/*
	 * private Map printSignature(Map lInputMap,BigDecimal netTotal,int
	 * lIntTotalPen,String result,String lStrCityNm,String lStrheadCodePen) {
	 * StringBuffer sbLine = new StringBuffer();
	 * 
	 * sbLine.append("\r\n\n"); sbLine.append("\r\n"); sbLine.append(getChar(2,
	 * " ")); sbLine.append(lStrheadCodePen + lIntTotalPen);
	 * sbLine.append("\r\n\n"); sbLine.append(getChar(2, " "));
	 * sbLine.append("Pay Rs:"); sbLine.append(getChar(18, " "));
	 * sbLine.append(netTotal + ".00 "); lInputMap.put("FinalAmount", netTotal);
	 * if (lInputMap.containsKey("ChqNo")) { sbLine.append(getChar(18, " "));
	 * sbLine.append("Cheque/ECS No.:"); sbLine.append(getChar(5, " "));
	 * sbLine.append(lInputMap.get("ChqNo").toString()); }
	 * sbLine.append("\r\n\n"); sbLine.append(getChar(2, " "));
	 * sbLine.append("Rs. In Words:"); sbLine.append(getChar(11, " "));
	 * sbLine.append(" Rupees " + result + " Only -");
	 * lInputMap.put("FinalAmtInWrds", result); sbLine.append("\r\n\n\n\n\n");
	 * 
	 * sbLine.append(getChar(2, " ")); sbLine.append("Signed By");
	 * sbLine.append(getChar(10, " ")); sbLine.append("Pensioner's Clerk");
	 * sbLine.append(getChar(10, " ")); sbLine.append("D.A");
	 * sbLine.append(getChar(10, " ")); sbLine.append("H.A");
	 * sbLine.append(getChar(10, " "));
	 * sbLine.append("Additional Treasury Officer"); sbLine.append(getChar(10,
	 * " ")); sbLine.append("Treasury Officer"); sbLine.append("\r\n");
	 * sbLine.append(getChar(80, " ")); sbLine.append(lStrCityNm);
	 * sbLine.append(getChar(25, " ")); sbLine.append(lStrCityNm);
	 * sbLine.append("</pre></div>");
	 * 
	 * lInputMap.put("Signature", sbLine); return lInputMap; }
	 */
	private Map printSignature(Map lInputMap, BigDecimal netTotal, int lIntTotalPen, String result, String lStrCityNm, String lStrheadCodePen) {

		StringBuffer sbLine = new StringBuffer();

		sbLine.append("\r\n\n");
		sbLine.append("\r\n");
		sbLine.append(getChar(2, " "));
		sbLine.append(lStrheadCodePen + lIntTotalPen);
		sbLine.append("\r\n\n");
		sbLine.append(getChar(2, " "));
		sbLine.append("Pay Rs:");
		sbLine.append(getChar(18, " "));
		sbLine.append(netTotal + ".00 ");
		lInputMap.put("FinalAmount", netTotal);
		if (lInputMap.containsKey("ChqNo")) {
			sbLine.append(getChar(18, " "));
			sbLine.append("Cheque/ECS No.:");
			sbLine.append(getChar(5, " "));
			sbLine.append(lInputMap.get("ChqNo").toString());
		}
		sbLine.append("\r\n\n");
		sbLine.append(getChar(2, " "));
		sbLine.append("Rs. In Words:");
		sbLine.append(getChar(11, " "));
		sbLine.append(" Rupees " + result + " Only -");
		lInputMap.put("FinalAmtInWrds", result);
		sbLine.append("\r\n\n\n\n\n");

		sbLine.append(getChar(2, " "));
		sbLine.append("Signed By");
		sbLine.append(getChar(15, " "));
		sbLine.append("Pensioner's Clerk");
		sbLine.append(getChar(15, " "));
		sbLine.append("D.A");
		sbLine.append(getChar(15, " "));
		sbLine.append("H.A");
		sbLine.append(getChar(15, " "));
		sbLine.append("Treasury Officer");
		sbLine.append("\r\n");
		sbLine.append(getChar(95, " "));
		sbLine.append(lStrCityNm);
		sbLine.append("</pre></div>");

		lInputMap.put("Signature", sbLine);
		return lInputMap;
	}

	private String getCVPDCRGPrintReport(String type, List<MonthlyPensionBillVO> list, Map lInputMap) throws Exception {

		StringBuffer sbLine = new StringBuffer();
		String lStrHeadCode = null;
		String lStrHeadCodeDesc = null;
		ServiceLocator srvcLoc = (ServiceLocator) lInputMap.get("serviceLocator");
		CommonPensionDAOImpl lObjMonthlyPnsnDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());

		BigDecimal lTotGrossAmt = new BigDecimal(0);
		BigDecimal lTotDedn = new BigDecimal(0);
		BigDecimal lTotNetAmt = new BigDecimal(0);
		StringBuffer sbLog = new StringBuffer();
		try {
			Integer pageCounter = (Integer) lInputMap.get("pageCounter");

			sbLine.append("<div><pre>");
			sbLine.append(getChar(55, " "));
			sbLine.append(type + " CONSOLIDATED LIST");
			sbLine.append("\r\n\n");

			// now printing records
			MonthlyPensionBillVO lObjMonthlyVO = null;
			Integer srNo = 0;
			String lStrChkHeadcode = "";
			for (int i = 0; i < list.size(); i++) {
				lObjMonthlyVO = list.get(i);

				if (!lStrChkHeadcode.equals(lObjMonthlyVO.getHeadCode().toString())) {
					if (!lStrChkHeadcode.equals("")) {
						sbLine.append(getCVPDCRGFooterPrint(list, (i - srNo), (i - 1), lInputMap, type));
						sbLine.append("</pre></div>");
						sbLine.append("<div><pre>");
					}
					lStrChkHeadcode = lObjMonthlyVO.getHeadCode().toString();
					lStrHeadCode = lObjMonthlyVO.getHeadCode().toString();
					lInputMap.put("HeadCode", lStrHeadCode);
					lStrHeadCodeDesc = lObjMonthlyPnsnDAO.getAllHeadCodeDesc(lStrHeadCode, SessionHelper.getLangId(lInputMap));
					lInputMap.put("HeadCodeDesc", lStrHeadCodeDesc);
					sbLine.append(getCVPDCRGHeaderPrint(pageCounter++, "Y", lInputMap));
					srNo = 0;

				}

				sbLine.append(getChar(2, " "));
				int srlen = (++srNo).toString().length() / 2;
				sbLine.append(getChar(srlen, " "));
				sbLine.append(srNo);
				sbLine.append(getChar(6 - srlen - srNo.toString().length(), " "));
				sbLine.append(getChar(2, " "));
				int temp = (8 - lObjMonthlyVO.getHeadCode().toString().length()) / 2;
				sbLine.append(getChar(temp, " "));
				sbLine.append(lObjMonthlyVO.getHeadCode());
				sbLine.append(getChar((8 - temp - lObjMonthlyVO.getHeadCode().toString().length()), " "));
				sbLine.append(getChar(2, " "));
				sbLine.append(lObjMonthlyVO.getPpoNo());
				sbLine.append(getChar(27 - lObjMonthlyVO.getPpoNo().length(), " "));
				sbLine.append(getChar(2, " "));
				if (lObjMonthlyVO.getPensionerName().length() > 28) {
					sbLine.append(lObjMonthlyVO.getPensionerName().substring(0, 28));
				} else {
					sbLine.append(lObjMonthlyVO.getPensionerName());
				}
				sbLine.append(getChar(28 - lObjMonthlyVO.getPensionerName().length(), " "));
				sbLine.append(getChar(2, " "));
				sbLine.append(String.format("%12s", (lObjMonthlyVO.getBasicPensionAmount() != null) ? lObjMonthlyVO.getBasicPensionAmount() : 0D));
				sbLine.append(getChar(2, " "));
				sbLine.append(String.format("%9s", (lObjMonthlyVO.getRecoveryAmount() != null) ? lObjMonthlyVO.getRecoveryAmount() : 0D));
				sbLine.append(getChar(2, " "));
				sbLine.append(String.format("%12s", (lObjMonthlyVO.getRecoveryAmount() != null) ? lObjMonthlyVO.getNetPensionAmount() : 0D));
				sbLine.append(getChar(2, " "));
				sbLine.append(lObjMonthlyVO.getAccountNo());
				sbLine.append(getChar(2, " "));
				sbLine.append("\r\n");
				if (lObjMonthlyVO.getPensionerName().length() > 28) {
					sbLine.append(getChar((srlen + 50), " "));
					sbLine.append(lObjMonthlyVO.getPensionerName().substring(28, lObjMonthlyVO.getPensionerName().length()));
				}
				sbLine.append("\n");

				// lTotGrossAmt =
				// lTotGrossAmt.add(lObjMonthlyVO.getBasicPensionAmount());
				// lTotDedn = lTotDedn.add(lObjMonthlyVO.getRecoveryAmount());
				// lTotNetAmt =
				// lTotNetAmt.add(lObjMonthlyVO.getNetPensionAmount());
			}
			sbLine.append(getCVPDCRGFooterPrint(list, (list.size() - srNo), (list.size() - 1), lInputMap, type));

			sbLine.append("\r\n");
			sbLine.append("\r\n");
			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");

			sbLine.append(getChar(10, " "));
			sbLine.append("TOTAL PER REPORT:");
			sbLine.append(getChar(52, " "));
			sbLine.append(String.format("%12s", lTotGrossAmt.setScale(0)));
			sbLine.append(getChar(2, " "));
			sbLine.append(String.format("%9s", lTotDedn.setScale(0)));
			sbLine.append(getChar(2, " "));
			sbLine.append(String.format("%12s", lTotNetAmt.setScale(0)));
			sbLine.append("\r\n");

			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");

			lInputMap.put("NetTotal", lTotNetAmt.add(new BigDecimal(lInputMap.get("NetTotal").toString())));
			lInputMap.put("pageCounter", pageCounter);

		} catch (Exception e) {
			sbLog.append("Problem in printing CVP-DCRG report is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(lInputMap));
			try {
				new MonthlyLogger(lInputMap).writeMonthlyLog(sbLog.toString());
				throw e;
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				throw ex;
			}

		}
		return sbLine.toString();
	}

	private String getCVPDCRGHeaderPrint(int pageNum, String HdCdFlag, Map lInputMap) throws Exception {

		StringBuffer sbLine = new StringBuffer();
		StringBuffer sbLog = new StringBuffer();
		String lStrBankName = "";
		String lStrBranchName = "";
		try {
			String lStrTrsryName = lInputMap.get("TreasuryName").toString();
			if (lInputMap.get("BankName") != null) {
				lStrBankName = lInputMap.get("BankName").toString();
			}
			if (lInputMap.get("BranchName") != null) {
				lStrBranchName = lInputMap.get("BranchName").toString();
			}
			String lStrBranchCode = "";

			// header details like date, traesury, bank branch, for
			// month,scheme, headcode
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			int temp = 0;
			temp = (132 - lStrTrsryName.length()) / 2;
			sbLine.append(getChar(temp, " "));
			sbLine.append(lStrTrsryName);
			sbLine.append(getChar(temp, " "));
			sbLine.append("\r\n");
			sbLine.append(getChar(5, " "));
			sbLine.append("DATE : ");
			sbLine.append(lObjDateFormat.format(DBUtility.getCurrentDateFromDB()));

			if (lInputMap.get("brnchCode") == null || "-1".equals(lInputMap.get("brnchCode"))) {
				lStrBranchCode = "";
			} else {
				lStrBranchCode = lInputMap.get("brnchCode").toString();
			}

			if (!lStrBankName.equals("") && !lStrBranchName.equals("")) {
				temp = 70 - ((lStrBankName + " : " + lStrBranchName + "(" + lStrBranchCode + ")").length() + 7);
				sbLine.append(getChar(temp / 2, " "));
				sbLine.append("Bank : ");
				sbLine.append(lStrBankName + " - " + lStrBranchName + "(" + lStrBranchCode + ")");
				sbLine.append(getChar(temp - temp / 2, " "));
			} else {
				sbLine.append(getChar(70, " "));
			}

			sbLine.append("For Month : ");
			sbLine.append((String) lInputMap.get("MonthName") + " " + lInputMap.get("ForYear").toString());
			sbLine.append("\r\n");
			sbLine.append(getChar(5, " "));
			sbLine.append("Page : ");
			sbLine.append(pageNum);
			Integer page = pageNum;
			sbLine.append(getChar((3 - (page.toString()).length()), " "));

			// 16
			if ("Y".equals(HdCdFlag)) {
				int temphdcd = ((String) lInputMap.get("HeadCode")).length() + ((String) lInputMap.get("HeadCodeDesc")).length() + 11;
				if (temphdcd <= 70) {
					sbLine.append(getChar(15, " "));
					temp = 70 - temphdcd;
					sbLine.append(getChar(temp / 2, " "));
					sbLine.append("Headcode: ");
					sbLine.append((String) lInputMap.get("HeadCode") + "-" + (String) lInputMap.get("HeadCodeDesc"));
					sbLine.append(getChar(temp - temp / 2, " "));
				} else if (temphdcd <= 75) {
					sbLine.append(getChar(10, " "));
					temp = 70 - temphdcd;
					sbLine.append(getChar(temp / 2, " "));
					sbLine.append("Headcode: ");
					sbLine.append((String) lInputMap.get("HeadCode") + "-" + (String) lInputMap.get("HeadCodeDesc"));
					sbLine.append(getChar(temp - temp / 2, " "));
				} else if (temphdcd <= 80) {
					sbLine.append(getChar(5, " "));
					temp = 70 - temphdcd;
					sbLine.append(getChar(temp / 2, " "));
					sbLine.append("Headcode: ");
					sbLine.append((String) lInputMap.get("HeadCode") + "-" + (String) lInputMap.get("HeadCodeDesc"));
					sbLine.append(getChar(temp - temp / 2, " "));
				} else {
					temp = 85 - temphdcd;
					sbLine.append(getChar(temp / 2, " "));
					sbLine.append("Headcode: ");
					sbLine.append((String) lInputMap.get("HeadCode") + "-" + (String) lInputMap.get("HeadCodeDesc"));
					sbLine.append(getChar(temp - temp / 2, " "));
				}
			} else {
				sbLine.append(getChar(85, " "));
			}

			sbLine.append(" Scheme : ");
			sbLine.append((String) lInputMap.get("Scheme"));
			sbLine.append("\r\n");
			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");

			sbLine.append(getChar(2, " "));
			sbLine.append("Sr No.");
			sbLine.append(getChar(2, " "));
			sbLine.append("Headcode");
			sbLine.append(getChar(2, " "));
			sbLine.append("PPO No.");
			sbLine.append(getChar(20, " "));
			sbLine.append(getChar(2, " "));
			sbLine.append("Pensioner Name");
			sbLine.append(getChar(14, " "));
			sbLine.append(getChar(2, " "));
			sbLine.append("Gross Amount");
			sbLine.append(getChar(2, " "));
			sbLine.append("Deduction");
			sbLine.append(getChar(2, " "));
			sbLine.append(" Net Amount ");
			sbLine.append(getChar(2, " "));
			sbLine.append("Account No. ");
			sbLine.append(getChar(2, " "));
			sbLine.append("\r\n");
			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");

		} catch (Exception e) {
			sbLog.append("Problem in printing CVP-DCRG header is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(lInputMap));
			try {
				new MonthlyLogger(lInputMap).writeMonthlyLog(sbLog.toString());
				throw e;
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				throw ex;
			}

		}
		return sbLine.toString();
	}

	private String getCVPDCRGFooterPrint(List<MonthlyPensionBillVO> list, int start, int end, Map inputMap, String billType) throws Exception {

		StringBuffer sbLine = new StringBuffer();
		MonthlyPensionBillVO lObjMonthlyVO = null;
		Double lTotGrossAmt = 0D;
		Double lTotDedn = 0D;
		Double lTotNetAmt = 0D;
		StringBuffer sbLog = new StringBuffer();
		try {
			for (int i = start; i <= end; i++) {
				lObjMonthlyVO = list.get(i);
				lTotGrossAmt += lObjMonthlyVO.getBasicPensionAmount().doubleValue();
				lTotDedn += lObjMonthlyVO.getRecoveryAmount().doubleValue();
				lTotNetAmt += lObjMonthlyVO.getNetPensionAmount().doubleValue();
			}
			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");

			sbLine.append(getChar(10, " "));
			sbLine.append("TOTAL PER HEADCODE:");
			sbLine.append(getChar(50, " "));
			sbLine.append(String.format("%12s", Math.round(lTotGrossAmt)));
			sbLine.append(getChar(2, " "));
			sbLine.append(String.format("%9s", Math.round(lTotDedn)));
			sbLine.append(getChar(2, " "));
			sbLine.append(String.format("%12s", Math.round(lTotNetAmt)));
			sbLine.append("\r\n");

			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");

			// Generate A List For View HeadCode wise Gross Deduction and NetAmt
			if ("CVP".equals(billType)) {
				Long lTtlCVPDedA = 0L;
				Long lTtlCVPDedB = 0L;

				if (inputMap.containsKey("CVPHeadCodeWiseDtls")) {
					List<Object> lHeadCodeWiseDtlsLst = (List) inputMap.get("CVPHeadCodeWiseDtls");
					List<Object> lTempHDCodeDtl = new ArrayList<Object>();

					lTempHDCodeDtl.add(inputMap.get("HeadCode"));
					lTempHDCodeDtl.add(inputMap.get("HeadCodeDesc"));
					lTempHDCodeDtl.add(Math.round(lTotGrossAmt)); // Gross Amt
					// lTempHDCodeDtl.add(Math.round(lTotDedn)); // Deduction

					if (inputMap.containsKey("HeadCodewiseDedDtls") && inputMap.get("HeadCodewiseDedDtls") != null) {
						List lHdcodeDedDtls = (List) inputMap.get("HeadCodewiseDedDtls");

						for (int i = 0; i < lHdcodeDedDtls.size(); i++) {
							Object[] lObjArr = (Object[]) lHdcodeDedDtls.get(i);

							if (lObjArr[0] != null && lObjArr[0].toString().equals(lObjMonthlyVO.getHeadCode().toString()) && lObjArr[1] != null && lObjArr[1].toString().equals("CVP")) {
								lTtlCVPDedA += lObjArr[2] != null ? Math.round(Double.valueOf(lObjArr[2].toString())) : 0L;
								lTtlCVPDedB += lObjArr[3] != null ? Math.round(Double.valueOf(lObjArr[3].toString())) : 0L;
							}
						}
					}

					lTempHDCodeDtl.add(lTtlCVPDedA); // Deduction A
					lTempHDCodeDtl.add(lTtlCVPDedB); // Deduction B
					lTempHDCodeDtl.add(Math.round(lTotNetAmt)); // Net Amt

					if (lHeadCodeWiseDtlsLst == null) {
						lHeadCodeWiseDtlsLst = new ArrayList<Object>();
						lHeadCodeWiseDtlsLst.add(lTempHDCodeDtl);
					} else {
						lHeadCodeWiseDtlsLst.add(lTempHDCodeDtl);
					}
					inputMap.put("CVPHeadCodeWiseDtls", lHeadCodeWiseDtlsLst);
				}
			} else if ("DCRG".equals(billType)) {
				Long lTtlDCRGDedA = 0L;
				Long lTtlDCRGDedB = 0L;

				if (inputMap.containsKey("DCRGHeadCodeWiseDtls")) {
					List<Object> lHeadCodeWiseDtlsLst = (List) inputMap.get("DCRGHeadCodeWiseDtls");
					List<Object> lTempHDCodeDtl = new ArrayList<Object>();

					lTempHDCodeDtl.add(inputMap.get("HeadCode"));
					lTempHDCodeDtl.add(inputMap.get("HeadCodeDesc"));
					lTempHDCodeDtl.add(Math.round(lTotGrossAmt)); // Gross Amt
					// lTempHDCodeDtl.add(Math.round(lTotDedn)); // Deduction

					if (inputMap.containsKey("HeadCodewiseDedDtls") && inputMap.get("HeadCodewiseDedDtls") != null) {
						List lHdcodeDedDtls = (List) inputMap.get("HeadCodewiseDedDtls");

						for (int i = 0; i < lHdcodeDedDtls.size(); i++) {
							Object[] lObjArr = (Object[]) lHdcodeDedDtls.get(i);

							if (lObjArr[0] != null && lObjArr[0].toString().equals(lObjMonthlyVO.getHeadCode().toString()) && lObjArr[1] != null && lObjArr[1].toString().equals("DCRG")) {
								lTtlDCRGDedA += lObjArr[2] != null ? Math.round(Double.valueOf(lObjArr[2].toString())) : 0L;
								lTtlDCRGDedB += lObjArr[3] != null ? Math.round(Double.valueOf(lObjArr[3].toString())) : 0L;
							}
						}
					}

					lTempHDCodeDtl.add(lTtlDCRGDedA); // Deduction A
					lTempHDCodeDtl.add(lTtlDCRGDedB); // Deduction B
					lTempHDCodeDtl.add(Math.round(lTotNetAmt)); // Net Amt

					if (lHeadCodeWiseDtlsLst == null) {
						lHeadCodeWiseDtlsLst = new ArrayList<Object>();
						lHeadCodeWiseDtlsLst.add(lTempHDCodeDtl);
					} else {
						lHeadCodeWiseDtlsLst.add(lTempHDCodeDtl);
					}
					inputMap.put("DCRGHeadCodeWiseDtls", lHeadCodeWiseDtlsLst);
				}
			}

		} catch (Exception e) {
			sbLog.append("Problem in printing CVP-DCRG footer is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				throw e;
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				throw ex;
			}

		}
		return sbLine.toString();
	}

	private String getOtherPartyHeader(String partyName, Map lInputMap, int pageNum) throws Exception {

		StringBuffer sbLine = new StringBuffer();
		StringBuffer sbLog = new StringBuffer();

		try {

			sbLine.append("<div><pre>");
			sbLine.append(getChar(55, " "));
			sbLine.append("OTHER PARTY PAYMENT");

			sbLine.append("\r\n\n");
			String lStrTrsryName = lInputMap.get("TreasuryName").toString();

			// header details like date, traesury, bank branch, for
			// month,scheme, headcode
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			int temp = 0;
			temp = (132 - lStrTrsryName.length()) / 2;
			sbLine.append(getChar(temp, " "));
			sbLine.append(lStrTrsryName);
			sbLine.append(getChar(temp, " "));
			sbLine.append("\r\n");
			sbLine.append(getChar(5, " "));
			sbLine.append("DATE : ");
			sbLine.append(lObjDateFormat.format(DBUtility.getCurrentDateFromDB()));

			if (!partyName.equals("")) {
				temp = 62 - partyName.length();
				sbLine.append(getChar(temp / 2, " "));
				sbLine.append("Party : ");
				sbLine.append(partyName);
				sbLine.append(getChar(temp - temp / 2, " "));
			} else {
				sbLine.append(getChar(62, " "));
			}

			sbLine.append("For Month : ");
			sbLine.append((String) lInputMap.get("MonthName") + " " + lInputMap.get("ForYear").toString());
			sbLine.append("\r\n");
			sbLine.append(getChar(5, " "));
			sbLine.append("Page : ");
			sbLine.append(pageNum);
			Integer page = pageNum;
			sbLine.append(getChar((3 - (page.toString()).length()), " "));

			sbLine.append(getChar(85, " "));

			sbLine.append(" Scheme : ");
			sbLine.append((String) lInputMap.get("Scheme"));
			sbLine.append("\r\n");
			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");

			sbLine.append(getChar(2, " "));
			sbLine.append("Sr No.");
			sbLine.append(getChar(2, " "));
			sbLine.append("");
			sbLine.append(getChar(10, " "));
			sbLine.append("PPO No.");
			sbLine.append(getChar(20, " "));
			sbLine.append(getChar(2, " "));
			sbLine.append("Pensioner Name");
			sbLine.append(getChar(14, " "));

			sbLine.append(getChar(32, " "));
			sbLine.append(" Net Amount ");
			sbLine.append("\r\n");
			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");

		} catch (Exception e) {
			sbLog.append("Problem in printing OtherParty header is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(lInputMap));
			try {
				new MonthlyLogger(lInputMap).writeMonthlyLog(sbLog.toString());
				throw e;
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				throw ex;
			}

		}
		return sbLine.toString();
	}

	private String printOtherPartyDtls(Integer srNo, String lPPONO, String lName, Long lngAmont, Map lInputMap) throws Exception {

		StringBuffer sbLine = new StringBuffer();
		new BigDecimal(0);
		new BigDecimal(0);
		new BigDecimal(0);
		StringBuffer sbLog = new StringBuffer();
		try {
			sbLine.append(getChar(2, " "));
			int srlen = (srNo).toString().length() / 2;
			sbLine.append(getChar(srlen, " "));
			sbLine.append(srNo);
			sbLine.append(getChar(6 - srlen - srNo.toString().length(), " "));
			sbLine.append(getChar(2, " "));
			sbLine.append(getChar(8, " "));
			sbLine.append(getChar(2, " "));
			sbLine.append(lPPONO);
			sbLine.append(getChar(27 - lPPONO.length(), " "));
			sbLine.append(getChar(2, " "));
			if (lName.length() > 28) {
				sbLine.append(lName.substring(0, 28));
			} else {
				sbLine.append(lName);
			}
			sbLine.append(getChar(28 - lName.length(), " "));
			sbLine.append(getChar(2, " "));
			sbLine.append(String.format("%12s", ""));
			sbLine.append(getChar(2, " "));
			sbLine.append(String.format("%9s", ""));
			sbLine.append(getChar(2, " "));
			sbLine.append(String.format("%12s", (lngAmont != null) ? lngAmont : 0l));
			sbLine.append(getChar(2, " "));
			sbLine.append("");
			sbLine.append(getChar(2, " "));
			sbLine.append("\r\n");
			if (lName.length() > 28) {
				sbLine.append(getChar((srlen + 50), " "));
				sbLine.append(lName.substring(28, lName.length()));
			}
			sbLine.append("\r\n");

			/*
			 * sbLine.append(getCVPDCRGFooterPrint(list, (list.size() - srNo),
			 * (list.size() - 1), lInputMap, type));
			 * 
			 * sbLine.append("\r\n"); sbLine.append("\r\n");
			 * sbLine.append(getChar(132, "-")); sbLine.append("\r\n");
			 * 
			 * sbLine.append(getChar(10, " "));
			 * sbLine.append("TOTAL PER REPORT:"); sbLine.append(getChar(52,
			 * " ")); sbLine.append(String.format("%12s",
			 * lTotGrossAmt.setScale(0))); sbLine.append(getChar(2, " "));
			 * sbLine.append(String.format("%9s", lTotDedn.setScale(0)));
			 * sbLine.append(getChar(2, " "));
			 * sbLine.append(String.format("%12s", lTotNetAmt.setScale(0)));
			 * sbLine.append("\r\n");
			 * 
			 * sbLine.append(getChar(132, "-")); sbLine.append("\r\n");
			 */

		} catch (Exception e) {
			sbLog.append("Problem in printing other party report is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(lInputMap));
			try {
				new MonthlyLogger(lInputMap).writeMonthlyLog(sbLog.toString());
				throw e;
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				throw ex;
			}

		}
		return sbLine.toString();
	}

	private String printOtherPartyFooter(String lStrCityName, Long lTotal, Map lInputMap) throws Exception {

		StringBuffer sbLine = new StringBuffer();
		StringBuffer sbLog = new StringBuffer();
		RuleBasedNumberFormat fomatter = new RuleBasedNumberFormat(IReportConstants.INDIAN_ENG_RULE_SET);
		try {
			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");
			sbLine.append(getChar(15, " "));
			sbLine.append("Total :");
			sbLine.append(getChar(90, " "));
			sbLine.append(lTotal);
			sbLine.append("\r\n");
			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");
			sbLine.append(getChar(2, " "));
			sbLine.append("Pay Rs:");
			sbLine.append(getChar(18, " "));
			sbLine.append(lTotal + ".00 ");

			sbLine.append("\r\n\n");
			sbLine.append(getChar(2, " "));
			sbLine.append("Rs. In Words:");
			sbLine.append(getChar(11, " "));
			sbLine.append(" Rupees " + fomatter.format(new com.ibm.icu.math.BigDecimal(lTotal)) + " Only -");
			sbLine.append("\r\n\n\n\n\n");

			sbLine.append(getChar(2, " "));
			sbLine.append("Signed By");
			sbLine.append(getChar(15, " "));
			sbLine.append("Pensioner's Clerk");
			sbLine.append(getChar(15, " "));
			sbLine.append("D.A");
			sbLine.append(getChar(15, " "));
			sbLine.append("H.A");
			sbLine.append(getChar(15, " "));
			sbLine.append("Treasury Officer");
			sbLine.append("\r\n");
			sbLine.append(getChar(95, " "));
			sbLine.append(lStrCityName);

			sbLine.append("</pre></div>");

		} catch (Exception e) {
			sbLog.append("Problem in printing other party Footer is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(lInputMap));
			try {
				new MonthlyLogger(lInputMap).writeMonthlyLog(sbLog.toString());
				throw e;
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				throw ex;
			}

		}
		return sbLine.toString();
	}

	private String getChar(int count, String ele) {

		StringBuffer lSBSpace = new StringBuffer();
		for (int i = 0; i < count; i++) {
			lSBSpace.append(ele);
		}
		return lSBSpace.toString();
	}

	private String printRecord(MonthlyPensionBillVO MonthlyPensionVo, int i, char lFlag, Double lDBMoneyOrderMinAmnt) {

		StringBuffer sbLine = new StringBuffer();
		String lstrName = null;
		String lStrForMonth = MonthlyPensionVo.getForMonth().toString();
		lStrForMonth = MonthlyPensionVo.getPpoNo() + "(" + lStrForMonth.substring(0, 4) + "-" + lStrForMonth.substring(4, 6) + ")";

		// calculating reduced pension
		Long lRedPen = Long.valueOf(0L);
		if (MonthlyPensionVo.getBasicPensionAmount() != null) {
			lRedPen = Math.round(MonthlyPensionVo.getBasicPensionAmount().doubleValue());
		}

		if (MonthlyPensionVo.getDpPercentAmount() != null && MonthlyPensionVo.getDpPercentAmount().doubleValue() > 0D) {
			lRedPen = lRedPen + Math.round(MonthlyPensionVo.getDpPercentAmount().doubleValue());
		} else if (MonthlyPensionVo.getAdpAmount() != null && MonthlyPensionVo.getAdpAmount().doubleValue() > 0D) {
			lRedPen = lRedPen + Math.round(MonthlyPensionVo.getAdpAmount().doubleValue());
		}

		if (MonthlyPensionVo.getCvpMonthlyAmount() != null) {
			lRedPen = lRedPen - Math.round(MonthlyPensionVo.getCvpMonthlyAmount().doubleValue());
		}
		if (MonthlyPensionVo.getPensionCutAmount() != null) {
			lRedPen = lRedPen - Math.round(MonthlyPensionVo.getPensionCutAmount().doubleValue());
		}

		// calculating other arrears
		Long lOtherArr = 0L;
		if (MonthlyPensionVo.getOMR() != null) {
			lOtherArr = lOtherArr + Math.round(MonthlyPensionVo.getOMR().doubleValue());
		}
		if (MonthlyPensionVo.getOtherArrearsAmount() != null) {
			lOtherArr = lOtherArr + Math.round(MonthlyPensionVo.getOtherArrearsAmount().doubleValue());
		}

		// calculating gross
		Long lGross = Long.valueOf(0L);
		lGross = lGross + lRedPen;
		if (MonthlyPensionVo.getPersonalPension() != null) {
			lGross = lGross + Math.round(MonthlyPensionVo.getPersonalPension().doubleValue());
		}
		if (MonthlyPensionVo.getIr() != null) {
			lGross = lGross + Math.round(MonthlyPensionVo.getIr().doubleValue());
		}
		if (MonthlyPensionVo.getMedicalAllowenceAmount() != null) {
			lGross = lGross + Math.round(MonthlyPensionVo.getMedicalAllowenceAmount().doubleValue());
		}
		if (MonthlyPensionVo.getTiPercentAmount() != null) {
			lGross = lGross + Math.round(MonthlyPensionVo.getTiPercentAmount().doubleValue());
		}
		if (MonthlyPensionVo.getTIArrearsAmount() != null) {
			lGross = lGross + Math.round(MonthlyPensionVo.getTIArrearsAmount().doubleValue());
		}
		lGross = lGross + lOtherArr;
		if (MonthlyPensionVo.getOtherBenefit() != null) {
			lGross = lGross + Math.round(MonthlyPensionVo.getOtherBenefit().doubleValue());
		}

		if (MonthlyPensionVo.getSpecialCutAmount() != null) {
			lGross = lGross - Math.round(MonthlyPensionVo.getSpecialCutAmount().doubleValue());
		}

		// calculating deduction
		Long lDeduction = 0L;
		if (MonthlyPensionVo.getRecoveryAmount() != null) {
			lDeduction = lDeduction + Math.round(MonthlyPensionVo.getRecoveryAmount().doubleValue());
		}
		if (MonthlyPensionVo.getItCutAmount() != null) {
			lDeduction = lDeduction + Math.round(MonthlyPensionVo.getItCutAmount().doubleValue());
		}
		Long lMoCom = 0L;
		if (MonthlyPensionVo.getMOComm() != null) {
			lMoCom = Math.round(MonthlyPensionVo.getMOComm().doubleValue());
		}

		// creating line 1
		if (i != 0) {
			sbLine.append(String.format("%4s", i));
		} else {
			sbLine.append(getChar(4, " "));
		}

		sbLine.append(getChar(1, " "));
		if (MonthlyPensionVo.getPpoNo().length() > 19) {
			sbLine.append(MonthlyPensionVo.getPpoNo().substring(0, 19));
			sbLine.append(getChar(3, " "));
		} else {
			sbLine.append(MonthlyPensionVo.getPpoNo());
			sbLine.append(getChar((22 - MonthlyPensionVo.getPpoNo().length()), " "));
		}

		// if(MonthlyPensionVo.getPpoNo().length() < 19)
		// sbLine.append(getChar(Math.abs((22 -
		// (MonthlyPensionVo.getPpoNo().length()+lStrForMonth.length())))," "));
		sbLine.append(String.format("%6s", MonthlyPensionVo.getLedgerNo() == null || MonthlyPensionVo.getLedgerNo().equals("0") ? "" : MonthlyPensionVo.getLedgerNo()));
		// sbLine.append(String.format("%7s", (MonthlyPensionVo.getAllnBf11156()
		// != null) ? MonthlyPensionVo.getAllnBf11156().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		// sbLine.append(String.format("%7s", (MonthlyPensionVo.getAllnAf10560()
		// != null) ? MonthlyPensionVo.getAllnAf10560().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		// sbLine.append(String.format("%9s",
		// (MonthlyPensionVo.getBasicPensionAmount() != null) ?
		// MonthlyPensionVo.getBasicPensionAmount().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));

		if (MonthlyPensionVo.getAdpAmount() != null && MonthlyPensionVo.getAdpAmount().doubleValue() > 0D) {
			// sbLine.append(String.format("%9s",
			// (MonthlyPensionVo.getAdpAmount() != null) ?
			// MonthlyPensionVo.getAdpAmount().setScale(0, BigDecimal.ROUND_UP)
			// : new BigDecimal(0)));
		} else {
			// sbLine.append(String.format("%9s",
			// (MonthlyPensionVo.getDpPercentAmount() != null) ?
			// MonthlyPensionVo.getDpPercentAmount().setScale(0,
			// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		}

		sbLine.append(String.format("%12s", lRedPen));
		// sbLine.append(String.format("%5s",
		// (MonthlyPensionVo.getPersonalPension() != null) ?
		// MonthlyPensionVo.getPersonalPension().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		// sbLine.append(String.format("%5s",
		// (MonthlyPensionVo.getMedicalAllowenceAmount() != null) ?
		// MonthlyPensionVo.getMedicalAllowenceAmount().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		// sbLine.append(String.format("%7s",
		// (MonthlyPensionVo.getTiPercentAmount() != null) ?
		// MonthlyPensionVo.getTiPercentAmount().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		sbLine.append(String.format("%8s", lOtherArr));
		sbLine.append(String.format("%8s", lGross));
		sbLine.append(String.format("%7s", lDeduction));

		if (MonthlyPensionVo.getBasicPensionAmount().doubleValue() > lDBMoneyOrderMinAmnt) {
			sbLine.append(String.format("%14s", (lGross - lDeduction) + ".00"));
		} else {
			sbLine.append(String.format("%14s", (lGross - lDeduction + lMoCom) + ".00"));
		}

		sbLine.append("\r\n");
		if (MonthlyPensionVo.getPpoNo().length() > 19) {
			sbLine.append(getChar(5, " "));
			sbLine.append(MonthlyPensionVo.getPpoNo().substring(19, MonthlyPensionVo.getPpoNo().length()));
			sbLine.append("\r\n");
		}

		// sbLine.append("\r\n");

		// creating line 2
		sbLine.append(getChar(5, " "));
		if (MonthlyPensionVo.getPensionerName() != null) {
			lstrName = MonthlyPensionVo.getPensionerName();
			if (lFlag == 'V') {
				lstrName = MonthlyPensionVo.getPensionerName() + " (" + MonthlyPensionVo.getForMonth() + ")";
			} else {
				lstrName = MonthlyPensionVo.getPensionerName();
			}
		} else {
			lstrName = "";
		}
		if (lstrName.length() > 19) {
			sbLine.append(lstrName.substring(0, 19));
			sbLine.append(getChar(3, " "));
		} else {
			sbLine.append(lstrName);
			sbLine.append(getChar((22 - lstrName.length()), " "));
		}
		sbLine.append(String.format("%6s", MonthlyPensionVo.getPageNo() == null || MonthlyPensionVo.getPageNo().equals("0") ? "" : MonthlyPensionVo.getPageNo()));
		// sbLine.append(String.format("%7s", (MonthlyPensionVo.getAllnAf11156()
		// != null) ? MonthlyPensionVo.getAllnAf11156().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		sbLine.append(getChar(7, " "));
		// sbLine.append(String.format("%9s",
		// (MonthlyPensionVo.getPensionCutAmount() != null) ?
		// MonthlyPensionVo.getPensionCutAmount().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		// sbLine.append(String.format("%9s",
		// (MonthlyPensionVo.getCvpMonthlyAmount() != null) ?
		// MonthlyPensionVo.getCvpMonthlyAmount().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		sbLine.append(getChar(12, " "));
		// sbLine.append(String.format("%5s", (MonthlyPensionVo.getIr() != null)
		// ? MonthlyPensionVo.getIr().setScale(0, BigDecimal.ROUND_UP) : new
		// BigDecimal(0)));
		// sbLine.append(String.format("%5s",
		// (MonthlyPensionVo.getOtherBenefit() != null) ?
		// MonthlyPensionVo.getOtherBenefit().setScale(0, BigDecimal.ROUND_UP) :
		// new BigDecimal(0)));
		// sbLine.append(String.format("%7s",
		// (MonthlyPensionVo.getTIArrearsAmount() != null) ?
		// MonthlyPensionVo.getTIArrearsAmount().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		// sbLine.append(String.format("%8s",
		// (MonthlyPensionVo.getSpecialCutAmount() != null) ?
		// MonthlyPensionVo.getSpecialCutAmount().setScale(0,
		// BigDecimal.ROUND_UP) : new BigDecimal(0)));
		sbLine.append(getChar(7, " "));
		sbLine.append(String.format("%8s", lMoCom));
		sbLine.append(String.format("%14s", MonthlyPensionVo.getAccountNo()));
		sbLine.append("\r\n");
		if (lstrName.length() > 19) {
			sbLine.append(getChar(5, " "));
			sbLine.append(lstrName.substring(19, lstrName.length()));
		}
		sbLine.append("\r\n");
		return sbLine.toString();
	}

	private String getHeadForMnthlyReport(int pageNum, Map<String, Object> lInputMap, String HdCdFlag) throws Exception {

		StringBuffer sbLine = new StringBuffer();
		String BranchCode = null;
		StringBuffer sbLog = new StringBuffer();

		try {
			setSessionInfo(lInputMap);

			// called repeatedly for each header fn but is same for the whoe
			// report so just needs to be called once in the common printBill
			// method

			sbLine.append(getChar(62, " "));
			sbLine.append("FORM - C ");
			sbLine.append(getChar(62, " "));
			sbLine.append("\r\n");

			String lStrTrsryName = lInputMap.get("TreasuryName").toString();
			String lStrBankName = null;
			String lStrBranchName = null;

			if (lInputMap.get("BankName") == null) {
				lStrBankName = "";
			} else if (("-1").equals(lInputMap.get("BankName").toString())) {
				lStrBankName = "";
			} else {
				lStrBankName = lInputMap.get("BankName").toString();
			}

			if (lInputMap.get("BranchName") == null) {
				lStrBranchName = "";
			} else if (("-1").equals(lInputMap.get("BranchName").toString())) {
				lStrBranchName = "";
			} else {
				lStrBranchName = lInputMap.get("BranchName").toString();
			}

			if (lInputMap.get("brnchCode") == null || "-1".equals(lInputMap.get("brnchCode"))) {
				BranchCode = "";
			} else {
				BranchCode = lInputMap.get("brnchCode").toString();
			}

			// header details like date, traesury, bank branch, for
			// month,scheme, headcode
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			int temp = 0;
			temp = (132 - lStrTrsryName.length()) / 2;
			sbLine.append(getChar(temp, " "));
			sbLine.append(lStrTrsryName);
			sbLine.append(getChar(temp, " "));
			sbLine.append("\r\n");
			sbLine.append(getChar(5, " "));
			sbLine.append("DATE : ");
			sbLine.append(lObjDateFormat.format(SessionHelper.getCurDate()));

			if (!lStrBankName.equals("") && !lStrBranchName.equals("")) {
				temp = 70 - ((lStrBankName + " : " + lStrBranchName + "(" + BranchCode + ")").length() + 7);
				sbLine.append(getChar(temp / 2, " "));
				sbLine.append("Bank : ");
				sbLine.append(lStrBankName + " - " + lStrBranchName + "(" + BranchCode + ")");
				sbLine.append(getChar(temp - temp / 2, " "));
			} else {
				sbLine.append(getChar(70, " "));
			}

			sbLine.append("For Month : ");
			sbLine.append((String) lInputMap.get("MonthName") + " " + lInputMap.get("ForYear").toString());
			sbLine.append("\r\n");
			sbLine.append(getChar(5, " "));
			sbLine.append("Page : ");
			sbLine.append(pageNum);
			Integer page = pageNum;
			sbLine.append(getChar((3 - (page.toString()).length()), " "));

			// 16
			if ("Y".equals(HdCdFlag)) {
				int temphdcd = ((String) lInputMap.get("HeadCode")).length() + ((String) lInputMap.get("HeadCodeDesc")).length() + 11;
				if (temphdcd <= 70) {
					sbLine.append(getChar(15, " "));
					temp = 70 - temphdcd;
					sbLine.append(getChar(temp / 2, " "));
					sbLine.append("Headcode: ");
					sbLine.append((String) lInputMap.get("HeadCode") + "-" + (String) lInputMap.get("HeadCodeDesc"));
					sbLine.append(getChar(temp - temp / 2, " "));
				} else if (temphdcd <= 75) {
					sbLine.append(getChar(10, " "));
					temp = 70 - temphdcd;
					sbLine.append(getChar(temp / 2, " "));
					sbLine.append("Headcode: ");
					sbLine.append((String) lInputMap.get("HeadCode") + "-" + (String) lInputMap.get("HeadCodeDesc"));
					sbLine.append(getChar(temp - temp / 2, " "));
				} else if (temphdcd <= 80) {
					sbLine.append(getChar(5, " "));
					temp = 70 - temphdcd;
					sbLine.append(getChar(temp / 2, " "));
					sbLine.append("Headcode: ");
					sbLine.append((String) lInputMap.get("HeadCode") + "-" + (String) lInputMap.get("HeadCodeDesc"));
					sbLine.append(getChar(temp - temp / 2, " "));
				} else {
					temp = 85 - temphdcd;
					sbLine.append(getChar(temp / 2, " "));
					sbLine.append("Headcode: ");
					sbLine.append((String) lInputMap.get("HeadCode") + "-" + (String) lInputMap.get("HeadCodeDesc"));
					sbLine.append(getChar(temp - temp / 2, " "));
				}
			} else if ("F".equals(HdCdFlag)) {
				sbLine.append(getChar(30, " "));
				sbLine.append("------------ Final Summary ------------");
				sbLine.append(getChar(16, " "));
			} else {
				sbLine.append(getChar(85, " "));
			}

			sbLine.append(" Scheme : ");
			sbLine.append((String) lInputMap.get("Scheme"));
			sbLine.append("\r\n");
			sbLine.append(getChar(132, "-"));

			// header line 1 for depicting parameters of the report
			sbLine.append("\r\n");
			sbLine.append(getChar(1, " "));
			sbLine.append("SR#");
			sbLine.append(getChar(2, " "));
			sbLine.append("PPO No.");
			sbLine.append(getChar(15, " "));
			sbLine.append(String.format("%6s", "Led.No"));
			sbLine.append(String.format("%7s", "BB56"));
			sbLine.append(String.format("%7s", "GA60"));
			sbLine.append(String.format("%9s", "Basic"));
			sbLine.append(String.format("%6s", "DA"));
			sbLine.append(String.format("%14s", "Red.Pen"));
			sbLine.append(String.format("%5s", "P.P"));
			sbLine.append(String.format("%5s", "MA"));
			sbLine.append(String.format("%7s", "TI"));
			sbLine.append(String.format("%8s", "Arr."));
			sbLine.append(String.format("%8s", "Gross"));
			sbLine.append(String.format("%9s", "Dedn"));
			sbLine.append(String.format("%10s", "Net"));
			sbLine.append("\r\n");

			// header line 3 for depicting parameters of the report
			sbLine.append(getChar(6, " "));
			sbLine.append("Pensioner Name");
			sbLine.append(getChar(8, " "));
			sbLine.append(String.format("%6s", "Pg.No."));
			sbLine.append(String.format("%7s", "BA56"));
			sbLine.append(getChar(7, " "));
			sbLine.append(String.format("%7s", "PC"));
			sbLine.append(String.format("%11s", "CVPMnh"));
			sbLine.append(String.format(getChar(10, " ")));
			sbLine.append(String.format("%5s", "IR"));
			sbLine.append(String.format("%8s", "Ot.Ben"));
			sbLine.append(String.format("%6s", "ATI"));
			sbLine.append(String.format("%8s", "Ot.Cut"));
			sbLine.append(getChar(8, " "));
			sbLine.append(String.format("%8s", "MO.Com"));
			sbLine.append(String.format("%11s", "Acc No"));
			sbLine.append("\r\n");

			sbLine.append(getChar(132, "-"));
			sbLine.append("\r\n");

			// ////////////////////////////////////
			/*
			 * BB56 B.B. 1/11/56 BA56 B.A. 1/11/56 GA60 G.A. 1/5/60 Basic Basic
			 * Pension PC Pen Cut DA DA Pension CVPMnth CVP Monthly Red.Pen
			 * Reduced Pension With DP P.P Personal Pension Oth.Ben Other
			 * Benefit ATI TI Arrear Arr. Other Arrear Oth.Cut Other Cut Dedn
			 * Deduction Net Net Amount Acc No. Account No.
			 */
			// /////////////////////////////////////
		} catch (Exception e) {
			sbLog.append("Problem in printing form-c header is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(lInputMap));
			try {
				new MonthlyLogger(lInputMap).writeMonthlyLog(sbLog.toString());
				throw e;
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				throw ex;
			}

		}
		return sbLine.toString();
	}

	private Map getFooterForMnthlyReport(String FootString, int pgCoutner, List MonthlyPensionList, int serialNoSt, int serialNoEnd, Map<String, Object> inputMap, Double lOtherPartyPay,
			Double lDBMoneyOrderMinAmnt) {

		StringBuilder sbLine = new StringBuilder();
		Map iInputMap = new HashMap();
		MonthlyPensionBillVO lObjMonthlyVO = new MonthlyPensionBillVO();
		Long[] lBDArray = new Long[20];
		Long lTtlRedPen = 0L;
		Long lTtlGross = 0L;
		Long lLngMoCom = 0L;

		Long lTtlDedA = 0L;
		Long lTtlDedB = 0L;

		Long lTtlITCutAmt = 0l;

		for (int i = 0; i < 20; i++) {
			lBDArray[i] = Long.valueOf(0L);
		}
		sbLine.append("\r\n");
		sbLine.append(getChar(132, "-"));
		sbLine.append("\r\n");

		// line 1
		sbLine.append(getChar(2, " "));
		int temp = FootString.length();
		sbLine.append(FootString);
		// Total Per HeadCode
		// Total Per Report
		// TOTAL Per Page:
		sbLine.append(getChar(26 - temp, " "));
		// computing sum of records of that page
		for (int i = serialNoSt; i <= serialNoEnd; i++) {
			lObjMonthlyVO = (MonthlyPensionBillVO) MonthlyPensionList.get(i);
			if (lObjMonthlyVO.getAllnBf11156() != null) {
				lBDArray[0] = lBDArray[0] + Math.round((lObjMonthlyVO.getAllnBf11156().doubleValue()));
			}
			if (lObjMonthlyVO.getAllnAf11156() != null) {
				lBDArray[1] = lBDArray[1] + Math.round((lObjMonthlyVO.getAllnAf11156().doubleValue()));
			}
			if (lObjMonthlyVO.getAllnAf10560() != null) {
				lBDArray[2] = lBDArray[2] + Math.round((lObjMonthlyVO.getAllnAf10560().doubleValue()));
			}
			if (lObjMonthlyVO.getBasicPensionAmount() != null) {
				lBDArray[3] = lBDArray[3] + Math.round((lObjMonthlyVO.getBasicPensionAmount().doubleValue()));
			}
			if (lObjMonthlyVO.getPersonalPension() != null) {
				lBDArray[4] = lBDArray[4] + Math.round((lObjMonthlyVO.getPersonalPension().doubleValue()));
			}
			if (lObjMonthlyVO.getMedicalAllowenceAmount() != null) {
				lBDArray[5] = lBDArray[5] + Math.round((lObjMonthlyVO.getMedicalAllowenceAmount().doubleValue()));
			}
			if (lObjMonthlyVO.getTiPercentAmount() != null) {
				lBDArray[6] = lBDArray[6] + Math.round((lObjMonthlyVO.getTiPercentAmount().doubleValue()));
			}
			if (lObjMonthlyVO.getRecoveryAmount() != null) {
				lBDArray[7] = lBDArray[7] + Math.round((lObjMonthlyVO.getRecoveryAmount().doubleValue()));
			}
			if (lObjMonthlyVO.getItCutAmount() != null) {
				lBDArray[7] = lBDArray[7] + Math.round(lObjMonthlyVO.getItCutAmount().doubleValue());
				lTtlITCutAmt += Math.round(lObjMonthlyVO.getItCutAmount().doubleValue());
			}

			if (lObjMonthlyVO.getDpPercentAmount() != null && lObjMonthlyVO.getDpPercentAmount().doubleValue() > 0D) {
				lBDArray[9] = lBDArray[9] + Math.round((lObjMonthlyVO.getDpPercentAmount().doubleValue()));
				lObjMonthlyVO.getDpPercentAmount().doubleValue();
			} else if (lObjMonthlyVO.getAdpAmount() != null && lObjMonthlyVO.getAdpAmount().doubleValue() > 0D) {
				lBDArray[9] = lBDArray[9] + Math.round((lObjMonthlyVO.getAdpAmount().doubleValue()));
				lObjMonthlyVO.getAdpAmount().doubleValue();
			}

			if (lObjMonthlyVO.getIr() != null) {
				lBDArray[10] = lBDArray[10] + Math.round((lObjMonthlyVO.getIr().doubleValue()));
			}
			if (lObjMonthlyVO.getTIArrearsAmount() != null) {
				lBDArray[11] = lBDArray[11] + Math.round((lObjMonthlyVO.getTIArrearsAmount().doubleValue()));
			}
			if (lObjMonthlyVO.getOtherArrearsAmount() != null) {
				lBDArray[12] = lBDArray[12] + Math.round((lObjMonthlyVO.getOtherArrearsAmount().doubleValue()));
			}
			if (lObjMonthlyVO.getOMR() != null) {
				lBDArray[12] = lBDArray[12] + Math.round((lObjMonthlyVO.getOMR().doubleValue()));
			}
			// computing sum of cvp monthly for computing reduced pension
			if (lObjMonthlyVO.getCvpMonthlyAmount() != null) {
				lBDArray[13] = lBDArray[13] + Math.round((lObjMonthlyVO.getCvpMonthlyAmount().doubleValue()));
			}
			if (lObjMonthlyVO.getPensionCutAmount() != null) {
				lBDArray[14] = lBDArray[14] + Math.round(lObjMonthlyVO.getPensionCutAmount().doubleValue());
			}
			if (lObjMonthlyVO.getSpecialCutAmount() != null) {
				lBDArray[15] = lBDArray[15] + Math.round(lObjMonthlyVO.getSpecialCutAmount().doubleValue());
			}
			if (lObjMonthlyVO.getOtherBenefit() != null) {
				lBDArray[18] = lBDArray[18] + Math.round(lObjMonthlyVO.getOtherBenefit().doubleValue());
			}
			if (lObjMonthlyVO.getMOComm() != null) {
				lBDArray[19] = lBDArray[19] + Math.round(lObjMonthlyVO.getMOComm().doubleValue());
			}

			/*
			 * if(Math.round(lObjMonthlyVO.getBasicPensionAmount().doubleValue()
			 * - lObjMonthlyVO.getPensionCutAmount().doubleValue() + lDPAmt -
			 * lObjMonthlyVO.getCvpMonthlyAmount().doubleValue() +
			 * lObjMonthlyVO.getPersonalPension() .doubleValue() +
			 * lObjMonthlyVO.getIr().doubleValue() +
			 * lObjMonthlyVO.getMedicalAllowenceAmount().doubleValue() +
			 * lObjMonthlyVO.getTiPercentAmount().doubleValue() +
			 * lObjMonthlyVO.getTIArrearsAmount().doubleValue() +
			 * lObjMonthlyVO.getOtherArrearsAmount().doubleValue() +
			 * lObjMonthlyVO.getOMR().doubleValue() -
			 * lObjMonthlyVO.getRecoveryAmount().doubleValue() -
			 * lObjMonthlyVO.getItCutAmount().doubleValue() -
			 * lObjMonthlyVO.getSpecialCutAmount().doubleValue() +
			 * lObjMonthlyVO.getOtherBenefit().doubleValue()) <
			 * lDBMoneyOrderMinAmnt )
			 */

			if ("MONEY ORDER".equals(inputMap.get("Scheme"))) {
				if (inputMap.containsKey("lMapROPPCode") && ((Map) inputMap.get("lMapROPPCode")).containsKey("ROP06" + lObjMonthlyVO.getPensionerCode())) {
					lDBMoneyOrderMinAmnt = 3500D;
				} else {
					lDBMoneyOrderMinAmnt = 2500D;
				}
				if (lObjMonthlyVO.getBasicPensionAmount().doubleValue() <= lDBMoneyOrderMinAmnt) {
					lLngMoCom = lLngMoCom + Math.round(lObjMonthlyVO.getMOComm().doubleValue());
				}
			}

			if (inputMap.containsKey("lCutDtlMap") && inputMap.get("lCutDtlMap") != null && ((Map) inputMap.get("lCutDtlMap")).containsKey(lObjMonthlyVO.getPensionerCode() + "PM")) {
				lOtherPartyPay += (Double) ((Map) inputMap.get("lCutDtlMap")).get(lObjMonthlyVO.getPensionerCode() + "PM");
			}
		}

		/*
		 * if((lBDArray[3] + lBDArray[9] - lBDArray[13] - lBDArray[14]) > 2500)
		 * { lLngMoCom = lBDArray[19]; } else { lLngMoCom = 0L; }
		 */

		// reduced pension
		lTtlRedPen = lBDArray[3] + lBDArray[9] - lBDArray[13] - lBDArray[14];
		// gross
		lTtlGross = lTtlRedPen + lBDArray[4] + lBDArray[10] + lBDArray[5] + lBDArray[6] + lBDArray[11] + lBDArray[12] - lBDArray[15] + lBDArray[16] + lBDArray[18] + lLngMoCom;

		lBDArray[8] = lTtlGross - lBDArray[7];

		// Generate A List For View HeadCode wise Gross Deduction and NetAmt
		if (inputMap.containsKey("HeadCodeWiseDtls") && FootString.equalsIgnoreCase("Total Per HeadCode:")) {
			List<Object> lHeadCodeWiseDtlsLst = (List) inputMap.get("HeadCodeWiseDtls");
			List<Object> lTempHDCodeDtl = new ArrayList<Object>();

			lTempHDCodeDtl.add(lObjMonthlyVO.getHeadCode());
			lTempHDCodeDtl.add(inputMap.get("HeadCodeDesc"));
			lTempHDCodeDtl.add(lTtlGross + lOtherPartyPay); // Gross Amt

			// lTempHDCodeDtl.add(lBDArray[7]); // Deduction

			if (inputMap.containsKey("HeadCodewiseDedDtls") && inputMap.get("HeadCodewiseDedDtls") != null) {
				List lHdcodeDedDtls = (List) inputMap.get("HeadCodewiseDedDtls");

				for (int i = 0; i < lHdcodeDedDtls.size(); i++) {
					Object[] lObjArr = (Object[]) lHdcodeDedDtls.get(i);

					if (lObjArr[0] != null && lObjArr[0].toString().equals(lObjMonthlyVO.getHeadCode().toString()) && lObjArr[1] != null
							&& (lObjArr[1].toString().equals("Monthly") || lObjArr[1].toString().equals("OMR"))) {
						lTtlDedA += lObjArr[2] != null ? Math.round(Double.valueOf(lObjArr[2].toString())) : 0L;
						lTtlDedB += lObjArr[3] != null ? Math.round(Double.valueOf(lObjArr[3].toString())) : 0L;

					}
				}
			}

			lTtlDedA = lTtlDedA + lTtlITCutAmt;

			lTempHDCodeDtl.add(lTtlDedA); // Deduction A
			lTempHDCodeDtl.add(lTtlDedB); // Deduction B
			lTempHDCodeDtl.add(lBDArray[8] + lOtherPartyPay); // Net Amt
			lTempHDCodeDtl.add(lTtlITCutAmt); // IT Cut Amt

			if (lHeadCodeWiseDtlsLst == null) {
				lHeadCodeWiseDtlsLst = new ArrayList<Object>();
				lHeadCodeWiseDtlsLst.add(lTempHDCodeDtl);
			} else {
				lHeadCodeWiseDtlsLst.add(lTempHDCodeDtl);
			}
			inputMap.put("HeadCodeWiseDtls", lHeadCodeWiseDtlsLst);
		}

		// line 1 continues
		sbLine.append(String.format("%6s", " "));
		sbLine.append(String.format("%7s", lBDArray[0]));
		sbLine.append(String.format("%7s", lBDArray[2]));
		sbLine.append(String.format("%9s", lBDArray[3]));
		sbLine.append(String.format("%9s", lBDArray[9]));
		sbLine.append(String.format("%11s", lTtlRedPen));
		sbLine.append(String.format("%5s", lBDArray[4]));
		sbLine.append(String.format("%5s", lBDArray[5]));
		sbLine.append(String.format("%7s", lBDArray[6]));
		sbLine.append(String.format("%8s", lBDArray[12]));
		sbLine.append(String.format("%8s", lTtlGross));
		sbLine.append(String.format("%7s", lBDArray[7]));
		sbLine.append(String.format("%14s", lBDArray[8] + ".00"));
		sbLine.append("\r\n");

		// line 2
		sbLine.append(getChar(28, " "));
		sbLine.append(String.format("%6s", " "));
		sbLine.append(String.format("%7s", lBDArray[1]));
		sbLine.append(getChar(7, " "));
		sbLine.append(String.format("%9s", lBDArray[14]));
		sbLine.append(String.format("%9s", lBDArray[13]));
		sbLine.append(getChar(11, " "));
		sbLine.append(String.format("%5s", lBDArray[10]));
		sbLine.append(String.format("%5s", lBDArray[18]));
		sbLine.append(String.format("%7s", lBDArray[11]));
		sbLine.append(String.format("%8s", lBDArray[15]));
		sbLine.append(getChar(7, " "));
		sbLine.append(String.format("%8s", lBDArray[19]));
		lBDArray[16] = lTtlRedPen;
		lBDArray[17] = lTtlGross;
		sbLine.append("\r\n");
		sbLine.append(getChar(132, "-"));
		sbLine.append("\r\n");
		/*
		 * sbLine.append("\r\n"); sbLine.append("\r\n");
		 */

		// iInputMap.put("netAmnt", iInputMap)
		iInputMap.put("FooterString", sbLine.toString());
		iInputMap.put("PageTotals", lBDArray);
		iInputMap.put("NetTotal", lBDArray[8]);
		return iInputMap;
	}

	private String getAbbrForReport() {

		StringBuilder sbLine = new StringBuilder();
		// ////////////////////////////////////
		/*
		 * BB56 B.B. 1/11/56 BA56 B.A. 1/11/56 GA60 G.A. 1/5/60 Basic Basic
		 * Pension PC Pen Cut DA DA Pension CVPMnth CVP Monthly Red.Pen Reduced
		 * Pension With DP P.P Personal Pension Oth.Ben Other Benefit ATI TI
		 * Arrear Arr. Other Arrear Oth.Cut Other Cut Dedn Deduction Net Net
		 * Amount Acc No. Account No.
		 */
		// /////////////////////////////////////
		sbLine.append("\r\n");
		// line 1
		sbLine.append("  BB56 - B.B. 1/11/56"); // 19
		sbLine.append(getChar(5, " "));
		sbLine.append("Basic - Basic Pension"); // 21
		sbLine.append(getChar(5, " "));
		sbLine.append("PC - Pension Cut"); // 16
		sbLine.append(getChar(5, " "));
		sbLine.append("DA - DA Pension"); // 15
		sbLine.append(getChar(5, " "));
		sbLine.append("ATI - TI Arrear"); // 15
		sbLine.append(getChar(5, " "));
		sbLine.append("Dedn - Deduction"); // 16
		sbLine.append(getChar(5, " "));
		sbLine.append("\r\n");

		// line 2
		sbLine.append("  BA56 - B.A. 1/11/56"); // 19
		sbLine.append(getChar(5, " "));
		sbLine.append("CVPMnth - CVP Monthly"); // 21
		sbLine.append(getChar(5, " "));
		sbLine.append("Red.Pen - Reduced Pension With DP"); // 33
		sbLine.append(getChar(5, " "));
		sbLine.append("Arr. - Other Arrear"); // 19
		sbLine.append(getChar(5, " "));
		sbLine.append("Net - Net Amount"); // 16
		sbLine.append(getChar(5, " "));
		sbLine.append("\r\n");

		// line 3
		sbLine.append("  GA60 - G.A. 1/5/60"); // 18
		sbLine.append(getChar(5, " "));
		sbLine.append("P.P - Personal Pension"); // 22
		sbLine.append(getChar(5, " "));
		sbLine.append("Oth.Ben - Other Benefit"); // 23
		sbLine.append(getChar(5, " "));
		sbLine.append("Oth.Cut - Other Cut"); // 19
		sbLine.append(getChar(5, " "));
		sbLine.append("Acc No. - Account No."); // 21
		sbLine.append(getChar(5, " "));
		sbLine.append("\r\n\n");

		return sbLine.toString();
	}

	/**
	 * Get Monthly Pension Bill data for displaying in bill
	 * 
	 * @param inputMap
	 * @return objRes ResultObject
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.MonthlyPensionBillService#getMonthlyPension
	 * (java.util.Map)
	 */
	// getMonthlyPensionBillVOListForGetData
	public ResultObject getMonthlyPension(Map<String, Object> argsMap) {

		long prevTime = 0;
		long startTime = 0;
		prevTime = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		gLogger.info("Test MonthlyBill Performance =================Started :" + prevTime);
		// System.out.println("Test MonthlyBill Performance ===========Started:"
		// + prevTime);

		Map<String, Object> inputMap = argsMap;
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		MonthlyPensionBillVO lObjMonthlyPensionVO = null;
		NewPensionBillService lObjNewPensionBillService = new NewPensionBillServiceImpl();
		String lStrBank = null;
		String lStrBranch = null;
		String lStrGenChangStmntBy = null;
		String lStrDate = null;
		String lStrMonth = null;
		String lStrYear = null;
		String lStrSeenFlag = null;
		String lStrPenName = null;
		String lStrBranchCode = null;
		String lStrBankCode = null;
		Date DoD = null;
		Date DoR = null;
		Integer lLastPaidMonth = null;
		String lStrCaseStatus = null;
		ValidPcodeView lObjValidPcode = null;
		List<List> lTillPaidDateLst = new ArrayList<List>();
		inputMap.put("TillPaidDateLst", lTillPaidDateLst);
		Map lLocDtls = new HashMap();
		MonthlyPensionBillDAOImpl lObjMnthlyBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
		Map<String, List<MonthlyPensionBillVO>> lMapHeadWiseMonthlyVO = new HashMap<String, List<MonthlyPensionBillVO>>();
		List<MonthlyPensionBillVO> lLstMonthlyVO = null;
		BigDecimal lBdHeadCode = null;
		Long lLngMonthlyChngRqstId = null;
		Long lLngPnsnChngHdrId = null;
		Long lLngPnsnChngDtlId = null;
		TrnMonthlyChangeRqst lObjTrnMonthlyChangeRqst = null;
		TrnPensionChangeHdr lObjTrnPensionChangeHdr = null;
		TrnPensionChangeDtls lObjTrnPensionChangeDtls = null;
		Double lDblHeadWiseGrossAmt = 0.00;
		Double lDblHeadWiseRecAmt = 0.00;
		Double lDblHeadWiseNetAmt = 0.00;
		Double lDblChngStWiseGrossAmt = 0.00;
		Double lDblChngStWiseRecAmt = 0.00;
		Double lDblChngStWiseNetAmt = 0.00;
		ChangeStatementDAO lObjTrnMonthlyChangeRqstDAO = null;
		ChangeStatementDAO lObjTrnPensionChangeHdrDAO = null;
		ChangeStatementDAO lObjTrnPensionChangeDtlsDAO = null;
		String lStrBillType = bundleConst.getString("RECOVERY.MONTHLY");
		Set<String> lHeadCodeSet = null;
		StringBuilder lSBStatus = new StringBuilder();
		String lStrChngStmntStatus = "NtGenerated";
		SimpleDateFormat lSdf = new SimpleDateFormat("yyyyMM");
		Integer lIntStrDate = 0;
		Integer lIntCommDate = 0;
		Date lDtPpoEnd = null;
		Integer lIntEndDate = null;
		String lStrPpoEndFlag = "N";
		Date lDtCommencement = null;
		HibernateTemplate hitStg = null;
		List<TrnMonthlyChangeRqst> lLstTrnMonthlyChangeRqstVO = null;
		List<TrnPensionChangeHdr> lLstTrnPensionChangeHdrVO = null;
		List<TrnPensionChangeDtls> lLstTrnPensionChangeDtlsVO = null;
		Long lLngPkCntTrnMonthlyChangeRqst = null;
		Long lLngPkCntTrnPensionChangeHdr = null;
		Long lLngPkCntTrnPensionChangeDtls = null;
		List<TrnPensionRecoveryDtls> lLstRcvryDtls = null;
		TrnMonthlyPensionRecoveryDtls lObjTrnMonthlyPensionRecoveryDtls = null;
		List<TrnMonthlyPensionRecoveryDtls> lLstTrnMonthlyPensionRecoveryDtls = new ArrayList<TrnMonthlyPensionRecoveryDtls>();
		;
		Map<String, List<TrnPensionRecoveryDtls>> lRcvrDtlVOMap = null;
		String[] lArrStrBankBranchDtls = null;
		List<String> lLstBranchCode = new ArrayList<String>();
		Map<String, List<ValidPcodeView>> lMapBranchwisePnsrs = new HashMap<String, List<ValidPcodeView>>();
		List<ValidPcodeView> lLstBranchwisePnsrDtls = null;
		Set<String> lSetBranchCode = new HashSet<String>();
		Map<String, Map<String, List<MonthlyPensionBillVO>>> lMapBranchwiseMonthlyVO = new HashMap<String, Map<String, List<MonthlyPensionBillVO>>>();
		String lParaStrBranchCode = null;
		String lParaStrBankCode = null;
		Map<String, String> lMapBankBranch = new HashMap<String, String>();
		int lTotalPnsnChangeHdrCount = 0;
		int lTotalPnsnChangeDtlsCount = 0;
		List<String> lLstBranchWithNoPnsr = new ArrayList<String>();
		Map<String, List<String>> lMapProbPPOBranch = new HashMap<String, List<String>>();
		Map<String, List<String>> lMapNoFPDatesPpoBranch = new HashMap<String, List<String>>();
		List<Long> lLstHeadCode = new ArrayList<Long>();
		Map<Long, RltPensionHeadcodeChargable> lMapHeadCodeChargable = null;
		RltPensionHeadcodeChargable lObjRltPensionHeadcodeChargable = null;
		SimpleDateFormat lSdf1 = new SimpleDateFormat("dd/MM/yyyy");
		Long lLngPkRcvryDtls = 0l;
		try {
			setSessionInfo(inputMap);
			lStrCaseStatus = bundleConst.getString("STATUS.APPROVED");

			MonthlyPensionBillDAO lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());

			lStrMonth = StringUtility.getParameter("cmbForMonth", request).trim();
			lStrYear = StringUtility.getParameter("cmbForYear", request).trim();
			lStrBank = StringUtility.getParameter("cmbBank", request).trim();
			lStrGenChangStmntBy = StringUtility.getParameter("genChangStmntBy", request).trim();
			lArrStrBankBranchDtls = StringUtility.getParameterValues("bankBranchDtls", request);
			for (int i = 0; i < lArrStrBankBranchDtls.length; i++) {
				lParaStrBranchCode = (lArrStrBankBranchDtls[i].split("~"))[0];
				lParaStrBankCode = (lArrStrBankBranchDtls[i].split("~"))[1];
				lLstBranchCode.add(lParaStrBranchCode);
				lMapBankBranch.put(lParaStrBranchCode, lParaStrBankCode);
			}

			if (Integer.parseInt(lStrMonth) < 10) {
				lStrDate = lStrYear + "0" + lStrMonth;
			} else {
				lStrDate = lStrYear + lStrMonth;
			}

			inputMap.put("lStrCaseStatus", lStrCaseStatus);
			inputMap.put("ForMonth", lStrMonth);
			inputMap.put("ForYear", lStrYear);
			inputMap.put("ForBillYear", lStrYear);
			if (!"-1".equals(lStrBank) && lStrBank != null) {
				inputMap.put("Bank", lStrBank);
				inputMap.put("Branch", lStrBranch);
			}
			inputMap.put("Date", lStrDate);
			inputMap.put("BillCrtMonth", lStrDate);
			inputMap.put("BranchCode", lLstBranchCode);
			inputMap.put("FPFlag", "Y");

			List lLstMonthDtls = lObjMonthlyDAO.getMonthDtls(lStrMonth, gLngLangId);
			inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
			inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());

			// --Getting list of all pensioner code in record room whose case
			// status is approved and alive flag is Y.
			List<ValidPcodeView> lLstValidPnsnrCode = lObjMnthlyBillDAO.getValidPensionerCode(lStrBank, lLstBranchCode, inputMap, lStrCaseStatus);
			// --Getting list of pensioner code for which change statement is
			// generated
			List lLstGenStatementPnsrCode = lObjMnthlyBillDAO.getPnsrCodesOfGeneratedChangeStatement(Integer.valueOf(lStrDate));

			// ---Getting list of pensioner code for which first pension bill is
			// generated in current month
			List lLstGenFirstPensionPnsrCode = lObjMnthlyBillDAO.getPnsrCodesOfGeneratedFirstPension(Integer.valueOf(lStrDate), gStrLocCode);

			// --Making list of pensioner code for which change statement is yet
			// to be created.
			List<ValidPcodeView> lLstPnsnrCode = new ArrayList<ValidPcodeView>();
			for (ValidPcodeView lObjValidPcodeView : lLstValidPnsnrCode) {
				if (!lLstGenStatementPnsrCode.contains(lObjValidPcodeView.getPensionerCode())) {
					lLstPnsnrCode.add(lObjValidPcodeView);
				}
			}

			// ---Removing pensioners from valid pensioner list,whose first
			// // pension bill is generated in current month
			List<ValidPcodeView> lTmpLstValidPnsrDtls = new ArrayList<ValidPcodeView>();
			for (ValidPcodeView lInnObjValidPcodeView : lLstPnsnrCode) {
				if (!lLstGenFirstPensionPnsrCode.contains(lInnObjValidPcodeView.getPensionerCode())) {
					lTmpLstValidPnsrDtls.add(lInnObjValidPcodeView);
				}
			}
			lLstPnsnrCode.clear();
			lLstPnsnrCode.addAll(lTmpLstValidPnsrDtls);
			gLogger.info("Test MonthlyBill Performance =================After Getting Valid Pnsrs :" + (System.currentTimeMillis() - prevTime));
			// System.out.println("Test MonthlyBill Performance ===========After Getting Valid Pnsrs:"
			// + (System.currentTimeMillis() - prevTime));
			prevTime = System.currentTimeMillis();

			for (ValidPcodeView lObjValidPcodeView : lLstPnsnrCode) {
				lLstBranchwisePnsrDtls = lMapBranchwisePnsrs.get(lObjValidPcodeView.getBranchCode());
				if (lLstBranchwisePnsrDtls != null) {
					lLstBranchwisePnsrDtls.add(lObjValidPcodeView);
				} else {
					lLstBranchwisePnsrDtls = new ArrayList<ValidPcodeView>();
					lLstBranchwisePnsrDtls.add(lObjValidPcodeView);
				}
				lMapBranchwisePnsrs.put(lObjValidPcodeView.getBranchCode(), lLstBranchwisePnsrDtls);
			}
			lSetBranchCode = lMapBranchwisePnsrs.keySet();
			if (lSetBranchCode != null && lSetBranchCode.size() > 0) {
				lObjMnthlyBillDAO.getAllRcvrMap(inputMap);
				lObjMnthlyBillDAO.getMstPensionerFamilyDtlsMap(inputMap);
				lObjMnthlyBillDAO.getAllArrearMap(inputMap);
				lObjMnthlyBillDAO.getAllCVPCutMap(inputMap);
				lObjMnthlyBillDAO.getAllSixPayArrearMap(inputMap);
				lObjMnthlyBillDAO.getAllPensionCutMap(inputMap);

				gLogger.info("Test MonthlyBill Performance =================After Getting All Map Dtls :" + (System.currentTimeMillis() - prevTime));
				// System.out.println("Test MonthlyBill Performance ===========After Getting All Map Dtls:"
				// + (System.currentTimeMillis() - prevTime));
				prevTime = System.currentTimeMillis();

				lLstRcvryDtls = new ArrayList<TrnPensionRecoveryDtls>();
				lRcvrDtlVOMap = (Map<String, List<TrnPensionRecoveryDtls>>) inputMap.get("lRcvrDtlVOMap");
				Integer lIntTotalRcvryRecords = (Integer) inputMap.get("lIntTotalRcvryRecords");
				lLngPkRcvryDtls = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_monthly_pension_recovery_dtls", inputMap, lIntTotalRcvryRecords.intValue());
				inputMap.put("lMapProbPPOBranch", lMapProbPPOBranch);
				inputMap.put("lMapNoFPDatesPpoBranch", lMapNoFPDatesPpoBranch);

				gLogger.info("Test MonthlyBill Performance =================Before Pen Calc Loop :" + (System.currentTimeMillis() - prevTime));
				// System.out.println("Test MonthlyBill Performance ===========Before Pen Calc Loop :"
				// + (System.currentTimeMillis() - prevTime));
				prevTime = System.currentTimeMillis();

				lMapHeadCodeChargable = lObjCommonPensionDAO.getRltPensionHeadcodeChargableDtls(bundleConst.getString("RECOVERY.PENSION"));
				/**
				 * Preparing list of monthlybillvo branchwise-headwise starts
				 * <<<
				 */
				for (String lBranchCode : lSetBranchCode) {
					lLstPnsnrCode = lMapBranchwisePnsrs.get(lBranchCode);
					if (lLstPnsnrCode != null && !lLstPnsnrCode.isEmpty()) {
						lMapHeadWiseMonthlyVO = null;
						for (int i = 0; i < lLstPnsnrCode.size(); i++) {
							lStrPpoEndFlag = "N";
							lObjValidPcode = lLstPnsnrCode.get(i);
							lDtPpoEnd = lObjValidPcode.getEndDate();
							lStrPenName = (lObjValidPcode.getFirstName() != null ? lObjValidPcode.getFirstName() : "") + (lObjValidPcode.getMiddleName() != null ? lObjValidPcode.getMiddleName() : "")
									+ (lObjValidPcode.getLastName() != null ? lObjValidPcode.getLastName() : "");
							DoD = lObjValidPcode.getDateOfDeath();
							DoR = lObjValidPcode.getDateOfRetirement();
							lStrSeenFlag = lObjValidPcode.getSeenFlag();
							lLastPaidMonth = (lObjValidPcode.getLastPaidDate() != null) ? Integer.parseInt(lSdf.format(lObjValidPcode.getLastPaidDate())) : 0;
							lStrBranchCode = lObjValidPcode.getBranchCode();
							lStrBankCode = lObjValidPcode.getBankCode();
							lIntStrDate = Integer.valueOf(lStrDate);
							lDtCommencement = lObjValidPcode.getCommensionDate();

							/*
							 * ---If ppo end date is null or is in or after
							 * current month then only calculate monthly pension
							 * for that pensioner. ---If commencement date is
							 * after current date then not calculate monthly
							 * pension.
							 */
							if (lDtCommencement != null) {
								lIntCommDate = Integer.parseInt(lSdf.format(lDtCommencement));
								if (lIntCommDate > lIntStrDate) {
									lStrPpoEndFlag = "Y";
								}
							}

							if (lDtPpoEnd != null) {
								lIntEndDate = Integer.parseInt(lSdf.format(lDtPpoEnd));
								if ((lIntEndDate < lIntStrDate)) {
									lStrPpoEndFlag = "Y";
								}
							}
							inputMap.put("brnchCode", lStrBranchCode);
							/*
							 * ---If seen flag is Y and last paid date,ppo end
							 * date are before current month year then only
							 * calculate monthly pension. if
							 * ("Y".equals(lStrSeenFlag) && ((lLastPaidMonth <
							 * lIntStrDate) && ("N".equals(lStrPpoEndFlag)))) {
							 * --Validation of last paid date is removed.
							 */

							if ("Y".equals(lStrSeenFlag) && ("N".equals(lStrPpoEndFlag))) {
								inputMap.put("Date", lStrDate);
								inputMap.put("lObjValidPcode", lObjValidPcode);

								inputMap = lObjNewPensionBillService.getMonthlyPensionBillVO(inputMap);
								lObjMonthlyPensionVO = (MonthlyPensionBillVO) inputMap.get("lMonthlyPensionBillVO");

								/**
								 * -----Creating map of list of monthlybillvo
								 * headcodewise starts <<<<
								 **/
								if (lMapHeadWiseMonthlyVO == null) {
									lMapHeadWiseMonthlyVO = new HashMap<String, List<MonthlyPensionBillVO>>();
								}
								lBdHeadCode = lObjMonthlyPensionVO.getHeadCode();
								lLstMonthlyVO = lMapHeadWiseMonthlyVO.get(lBdHeadCode.toString());
								if (lLstMonthlyVO != null) {
									lLstMonthlyVO.add(lObjMonthlyPensionVO);
								} else {
									lLstMonthlyVO = new ArrayList<MonthlyPensionBillVO>();
									lLstMonthlyVO.add(lObjMonthlyPensionVO);
								}
								lMapHeadWiseMonthlyVO.put(lBdHeadCode.toString(), lLstMonthlyVO);
								/**
								 * -----Creating map of list of monthlybillvo
								 * headcodewise ends <<<<
								 **/
							}
						}
						/*
						 * Preparing map of branchwise-headwise monthlybillvo
						 * only for branches that have valid pensioners in it.
						 */
						if (lMapHeadWiseMonthlyVO != null) {
							lMapBranchwiseMonthlyVO.put(lBranchCode, lMapHeadWiseMonthlyVO);
						}
					}
				}
			}
			/**
			 * Preparing list of monthlybillvo branchwise-headwise ends <<<
			 */

			gLogger.info("Test MonthlyBill Performance =================After Pen Calc Loop :" + (System.currentTimeMillis() - prevTime));
			// System.out.println("Test MonthlyBill Performance ===========After Pen Calc Loop :"
			// + (System.currentTimeMillis() - prevTime));
			prevTime = System.currentTimeMillis();

			/*
			 * ----If the list of pensioners(lLstProbPpoNos) having net amount
			 * negative is not empty then showing alert to correct recovery or
			 * ---If the list of pensioner(lLstNoFPDatesPpoNos), having fp1 or
			 * fp2 date not available and death date and valid family pensioner
			 * available,is not empty then not allowing change statement
			 * generation
			 */

			lMapProbPPOBranch = (Map<String, List<String>>) inputMap.get("lMapProbPPOBranch");
			lMapNoFPDatesPpoBranch = (Map<String, List<String>>) inputMap.get("lMapNoFPDatesPpoBranch");
			inputMap.put("NEGATIVE_NET_AMT_ERROR", lMapProbPPOBranch);
			inputMap.put("NO_FP_DATES_ERROR", lMapNoFPDatesPpoBranch);
			if (lMapProbPPOBranch != null && lMapProbPPOBranch.keySet().size() > 0) {
				throw new Exception("Net amount is less than zero for some ppo.");
			} else if (lMapNoFPDatesPpoBranch != null && lMapNoFPDatesPpoBranch.keySet().size() > 0) {
				throw new Exception("Fp1 or Fp2 dates are not available for some ppo whose death date is avilable. ");
			} else {
				/**
				 * Getting total count of headcodes in all branches and total
				 * pensioners in all branches to be inserted starts
				 */
				lSetBranchCode = lMapBranchwiseMonthlyVO.keySet();
				for (String lBrnchCode : lSetBranchCode) {
					lMapHeadWiseMonthlyVO = lMapBranchwiseMonthlyVO.get(lBrnchCode);
					lTotalPnsnChangeHdrCount = lTotalPnsnChangeHdrCount + lMapHeadWiseMonthlyVO.keySet().size();
					lHeadCodeSet = lMapHeadWiseMonthlyVO.keySet();
					for (String lHeadCode : lHeadCodeSet) {
						lLstMonthlyVO = lMapHeadWiseMonthlyVO.get(lHeadCode);
						lTotalPnsnChangeDtlsCount = lTotalPnsnChangeDtlsCount + lLstMonthlyVO.size();
					}
				}
				/**
				 * Getting total count of headcodes in all branches and total
				 * pensioners in all branches to be inserted ends
				 */

				lLngPkCntTrnMonthlyChangeRqst = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_monthly_change_rqst", inputMap, lLstBranchCode.size());
				lLngPkCntTrnPensionChangeHdr = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_change_hdr", inputMap, lTotalPnsnChangeHdrCount);
				lLngPkCntTrnPensionChangeDtls = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_change_dtls", inputMap, lTotalPnsnChangeDtlsCount);
				/**
				 * Insertion of data in change statement related tables starts
				 * <<<<
				 */
				lObjTrnMonthlyChangeRqstDAO = new ChangeStatementDAOImpl(TrnMonthlyChangeRqst.class, srvcLoc.getSessionFactory());
				lObjTrnPensionChangeHdrDAO = new ChangeStatementDAOImpl(TrnPensionChangeHdr.class, srvcLoc.getSessionFactory());
				hitStg = new HibernateTemplate(srvcLoc.getSessionFactory());
				lLstTrnMonthlyChangeRqstVO = new ArrayList<TrnMonthlyChangeRqst>();
				lLstTrnPensionChangeHdrVO = new ArrayList<TrnPensionChangeHdr>();
				lLstTrnPensionChangeDtlsVO = new ArrayList<TrnPensionChangeDtls>();
				for (String lStrBrnchCode : lLstBranchCode) {
					lStrBank = lMapBankBranch.get(lStrBrnchCode);
					lLngMonthlyChngRqstId = ++lLngPkCntTrnMonthlyChangeRqst;
					lLngMonthlyChngRqstId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngMonthlyChngRqstId, inputMap);
					lObjTrnMonthlyChangeRqst = new TrnMonthlyChangeRqst();
					lObjTrnMonthlyChangeRqst.setChangeRqstId(lLngMonthlyChngRqstId);
					lObjTrnMonthlyChangeRqst.setBillType(lStrBillType);
					lObjTrnMonthlyChangeRqst.setChangeRqstDate(lSdf1.parse(lSdf1.format(gDate)));
					lObjTrnMonthlyChangeRqst.setForMonth(Integer.valueOf(lStrDate));
					lObjTrnMonthlyChangeRqst.setBankCode(lStrBank);
					lObjTrnMonthlyChangeRqst.setBranchCode(lStrBrnchCode);
					lObjTrnMonthlyChangeRqst.setUpToDate(lSdf1.parse(lSdf1.format(gDate)));
					lObjTrnMonthlyChangeRqst.setStatus(bundleConst.getString("CHANGSTMNTSTATUS.BEFOREAPPROVAL"));
					lObjTrnMonthlyChangeRqst.setLocationCode(gStrLocCode);
					lObjTrnMonthlyChangeRqst.setCreatedUserId(gLngUserId);
					lObjTrnMonthlyChangeRqst.setCreatedPostId(gLngPostId);
					lObjTrnMonthlyChangeRqst.setCreatedDate(gDate);
					lObjTrnMonthlyChangeRqst.setDbId(gDbId.intValue());

					lDblChngStWiseGrossAmt = 0.00;
					lDblChngStWiseRecAmt = 0.00;
					lDblChngStWiseNetAmt = 0.00;
					lMapHeadWiseMonthlyVO = lMapBranchwiseMonthlyVO.get(lStrBrnchCode);
					if (lMapHeadWiseMonthlyVO != null) {
						lHeadCodeSet = lMapHeadWiseMonthlyVO.keySet();
						if (lHeadCodeSet != null && lHeadCodeSet.size() > 0) {
							for (String lStrHeadCode : lHeadCodeSet) {
								lDblHeadWiseGrossAmt = 0.00;
								lDblHeadWiseRecAmt = 0.00;
								lDblHeadWiseNetAmt = 0.00;
								lObjRltPensionHeadcodeChargable = lMapHeadCodeChargable.get(Long.valueOf(lStrHeadCode));
								lObjTrnPensionChangeHdr = new TrnPensionChangeHdr();
								lLngPnsnChngHdrId = ++lLngPkCntTrnPensionChangeHdr;
								lLngPnsnChngHdrId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngPnsnChngHdrId, inputMap);
								lObjTrnPensionChangeHdr.setTrnPensionChangeHdrId(lLngPnsnChngHdrId);
								lObjTrnPensionChangeHdr.setChangeRqstId(lLngMonthlyChngRqstId);
								lObjTrnPensionChangeHdr.setBillType(lStrBillType);
								lObjTrnPensionChangeHdr.setHeadCode(Integer.valueOf(lStrHeadCode));
								lObjTrnPensionChangeHdr.setForMonth(Integer.valueOf(lStrDate));
								lObjTrnPensionChangeHdr.setBankCode(lStrBank);
								lObjTrnPensionChangeHdr.setBranchCode(lStrBrnchCode);
								lObjTrnPensionChangeHdr.setScheme("IRLA");
								lObjTrnPensionChangeHdr.setSchemeCode((lObjRltPensionHeadcodeChargable != null) ? lObjRltPensionHeadcodeChargable.getSchemeCode() : null);
								lObjTrnPensionChangeHdr.setLocationCode(gStrLocCode);
								lObjTrnPensionChangeHdr.setCreatedUserId(gLngUserId);
								lObjTrnPensionChangeHdr.setCreatedPostId(gLngPostId);
								lObjTrnPensionChangeHdr.setCreatedDate(gDate);
								lObjTrnPensionChangeHdr.setDbId(gDbId.intValue());

								lLstMonthlyVO = lMapHeadWiseMonthlyVO.get(lStrHeadCode);
								for (MonthlyPensionBillVO lObjMonthlyPensionBillVO : lLstMonthlyVO) {
									lObjTrnPensionChangeDtls = new TrnPensionChangeDtls();
									lLngPnsnChngDtlId = ++lLngPkCntTrnPensionChangeDtls;
									lLngPnsnChngDtlId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngPnsnChngDtlId, inputMap);
									lObjTrnPensionChangeDtls.setTrnPensionChangeDtlsId(lLngPnsnChngDtlId);
									lObjTrnPensionChangeDtls.setTrnPensionChangeHdrId(lLngPnsnChngHdrId);
									lObjTrnPensionChangeDtls.setPpoNo(lObjMonthlyPensionBillVO.getPpoNo());
									lObjTrnPensionChangeDtls.setPensionerCode(lObjMonthlyPensionBillVO.getPensionerCode());
									lObjTrnPensionChangeDtls.setAccountNo(lObjMonthlyPensionBillVO.getAccountNo());
									lObjTrnPensionChangeDtls.setPensionerName(lObjMonthlyPensionBillVO.getPensionerName());
									lObjTrnPensionChangeDtls.setHeadCode(lObjMonthlyPensionBillVO.getHeadCode().intValue());
									lObjTrnPensionChangeDtls.setPensionAmount(new BigDecimal(lObjMonthlyPensionBillVO.getBasicPensionAmount()));
									lObjTrnPensionChangeDtls.setDpAmount(new BigDecimal(lObjMonthlyPensionBillVO.getDpPercentAmount()));
									lObjTrnPensionChangeDtls.setAdpAmount(new BigDecimal(lObjMonthlyPensionBillVO.getAdpAmount()));
									lObjTrnPensionChangeDtls.setTiAmount(new BigDecimal(lObjMonthlyPensionBillVO.getTiPercentAmount()));
									lObjTrnPensionChangeDtls.setDaRate(new BigDecimal(lObjMonthlyPensionBillVO.getTiPercent()));
									lObjTrnPensionChangeDtls.setIr1Amount(new BigDecimal(lObjMonthlyPensionBillVO.getIr1Amt()));
									lObjTrnPensionChangeDtls.setIr2Amount(new BigDecimal(lObjMonthlyPensionBillVO.getIr2Amt()));
									lObjTrnPensionChangeDtls.setIr3Amount(new BigDecimal(lObjMonthlyPensionBillVO.getIr3Amt()));
									lObjTrnPensionChangeDtls.setArrearAmount(new BigDecimal(lObjMonthlyPensionBillVO.getOtherArrearsAmount()));
									lObjTrnPensionChangeDtls.setTiArrearAmount(new BigDecimal(lObjMonthlyPensionBillVO.getTIArrearsAmount()));// DA
																																				// Arrear
																																				// from
																																				// da
																																				// rate
																																				// configuration.
									lObjTrnPensionChangeDtls.setCvpMonthAmount(new BigDecimal(lObjMonthlyPensionBillVO.getCvpMonthlyAmount()));
									lObjTrnPensionChangeDtls.setRecoveryAmount(new BigDecimal(lObjMonthlyPensionBillVO.getRecoveryAmount()));
									lObjTrnPensionChangeDtls.setReducedPension(lObjMonthlyPensionBillVO.getReducedPension());
									lObjTrnPensionChangeDtls.setAllcationBf1436(new BigDecimal(lObjMonthlyPensionBillVO.getAllnBf11136()));
									lObjTrnPensionChangeDtls.setAllcationBf11156(new BigDecimal(lObjMonthlyPensionBillVO.getAllnBf11156()));
									lObjTrnPensionChangeDtls.setAllcationAf11156(new BigDecimal(lObjMonthlyPensionBillVO.getAllnAf11156()));
									lObjTrnPensionChangeDtls.setAllcationAf10560(new BigDecimal(lObjMonthlyPensionBillVO.getAllnAf10560()));
									lObjTrnPensionChangeDtls.setAllcationAfZp(new BigDecimal(lObjMonthlyPensionBillVO.getAllnAfZp()));
									lObjTrnPensionChangeDtls.setPersonalPensionAmount(new BigDecimal(lObjMonthlyPensionBillVO.getPersonalPension()));
									lObjTrnPensionChangeDtls.setPeonAllowance(lObjMonthlyPensionBillVO.getPeonAllowanceAmt());
									lObjTrnPensionChangeDtls.setMedicalAllowenceAmount(new BigDecimal(lObjMonthlyPensionBillVO.getMedicalAllowenceAmount()));
									lObjTrnPensionChangeDtls.setOtherBenefits(new BigDecimal(lObjMonthlyPensionBillVO.getOtherBenefit()));
									lObjTrnPensionChangeDtls.setOther1(lObjMonthlyPensionBillVO.getGallantryAmt());
									lObjTrnPensionChangeDtls.setTotalArrearAmt(new BigDecimal(lObjMonthlyPensionBillVO.getTotalArrearAmt()));
									lObjTrnPensionChangeDtls.setPensionCutAmount(new BigDecimal(lObjMonthlyPensionBillVO.getPensionCutAmount()));
									lObjTrnPensionChangeDtls.setGrossAmount(new BigDecimal(lObjMonthlyPensionBillVO.getGrossAmount()));
									lObjTrnPensionChangeDtls.setNetAmount(new BigDecimal(lObjMonthlyPensionBillVO.getNetPensionAmount()));
									lObjTrnPensionChangeDtls.setPayForMonth(Integer.valueOf(lStrDate));
									lObjTrnPensionChangeDtls.setLedgerNo(lObjMonthlyPensionBillVO.getLedgerNo());
									lObjTrnPensionChangeDtls.setPageNo(lObjMonthlyPensionBillVO.getPageNo());
									lObjTrnPensionChangeDtls.setRopType(lObjMonthlyPensionBillVO.getRopType());
									lObjTrnPensionChangeDtls.setArrear6PC(lObjMonthlyPensionBillVO.getArrear6PC());
									lObjTrnPensionChangeDtls.setArrearCommutation(lObjMonthlyPensionBillVO.getArrearCommutation());
									lObjTrnPensionChangeDtls.setArrearLC(lObjMonthlyPensionBillVO.getArrearLC());
									lObjTrnPensionChangeDtls.setArrearOthrDiff(lObjMonthlyPensionBillVO.getArrearOthrDiff());
									lObjTrnPensionChangeDtls.setPunishmentCutAmt(lObjMonthlyPensionBillVO.getPunishmentCutAmt());
									lLstTrnPensionChangeDtlsVO.add(lObjTrnPensionChangeDtls);

									lDblHeadWiseGrossAmt += lObjMonthlyPensionBillVO.getGrossAmount().doubleValue();
									lDblHeadWiseRecAmt += lObjMonthlyPensionBillVO.getRecoveryAmount().doubleValue();
									lDblHeadWiseNetAmt += lObjMonthlyPensionBillVO.getNetPensionAmount().doubleValue();

									/**
									 * Preparing list of recovery details starts
									 * <<<
									 **/
									if (lRcvrDtlVOMap != null) {
										lLstRcvryDtls = lRcvrDtlVOMap.get(lObjMonthlyPensionBillVO.getPensionerCode());
										if (lLstRcvryDtls != null) {
											for (TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls : lLstRcvryDtls) {
												lObjTrnMonthlyPensionRecoveryDtls = new TrnMonthlyPensionRecoveryDtls();
												lObjTrnMonthlyPensionRecoveryDtls.setTrnMonthlyPensionRecoveryDtls(IFMSCommonServiceImpl.getFormattedPrimaryKey(++lLngPkRcvryDtls, inputMap));
												lObjTrnMonthlyPensionRecoveryDtls.setPensionerCode(lObjMonthlyPensionBillVO.getPensionerCode());
												lObjTrnMonthlyPensionRecoveryDtls.setRecoveryFromFlag(lObjTrnPensionRecoveryDtls.getRecoveryFromFlag());
												lObjTrnMonthlyPensionRecoveryDtls.setRecoveryType(lObjTrnPensionRecoveryDtls.getRecoveryType());
												lObjTrnMonthlyPensionRecoveryDtls.setAccountNumber(lObjTrnPensionRecoveryDtls.getAccountNumber());
												lObjTrnMonthlyPensionRecoveryDtls.setAmount(lObjTrnPensionRecoveryDtls.getAmount());
												lObjTrnMonthlyPensionRecoveryDtls.setForMonth(Integer.valueOf(lStrDate));
												lObjTrnMonthlyPensionRecoveryDtls.setBankCode(lStrBank);
												lObjTrnMonthlyPensionRecoveryDtls.setBranchCode(lStrBrnchCode);
												lObjTrnMonthlyPensionRecoveryDtls.setChangeRqstId(lLngMonthlyChngRqstId);
												lObjTrnMonthlyPensionRecoveryDtls.setChangeStmntStatus(bundleConst.getString("CHANGSTMNTSTATUS.BEFOREAPPROVAL"));
												lObjTrnMonthlyPensionRecoveryDtls.setTrnCounter(1);
												lObjTrnMonthlyPensionRecoveryDtls.setNoOfInstallments(lObjTrnPensionRecoveryDtls.getNoOfInstallments());
												lObjTrnMonthlyPensionRecoveryDtls.setNature(lObjTrnPensionRecoveryDtls.getNature());
												lObjTrnMonthlyPensionRecoveryDtls.setSchemeCode(lObjTrnPensionRecoveryDtls.getSchemeCode());
												lObjTrnMonthlyPensionRecoveryDtls.setPpoNo(lObjMonthlyPensionBillVO.getPpoNo());
												lObjTrnMonthlyPensionRecoveryDtls.setLocationCode(Integer.valueOf(gStrLocCode));
												lObjTrnMonthlyPensionRecoveryDtls.setCreatedUserId(gLngUserId);
												lObjTrnMonthlyPensionRecoveryDtls.setCreatedPostId(gLngPostId);
												lObjTrnMonthlyPensionRecoveryDtls.setCreatedDate(gDate);
												lObjTrnMonthlyPensionRecoveryDtls.setDbId(gDbId.intValue());
												lLstTrnMonthlyPensionRecoveryDtls.add(lObjTrnMonthlyPensionRecoveryDtls);
											}
										}

									}
									/**
									 * Preparing list of recovery details ends
									 * >>>>
									 **/
								}
								lObjTrnPensionChangeHdr.setGrossAmount(new BigDecimal(lDblHeadWiseGrossAmt));
								lObjTrnPensionChangeHdr.setRecoveryAmount(new BigDecimal(lDblHeadWiseRecAmt));
								lObjTrnPensionChangeHdr.setNetAmount(new BigDecimal(lDblHeadWiseNetAmt));
								lObjTrnPensionChangeHdr.setNoOfPnsr(new Long(lLstMonthlyVO.size()));
								lLstTrnPensionChangeHdrVO.add(lObjTrnPensionChangeHdr);

								lDblChngStWiseGrossAmt += lDblHeadWiseGrossAmt;
								lDblChngStWiseRecAmt += lDblHeadWiseRecAmt;
								lDblChngStWiseNetAmt += lDblHeadWiseNetAmt;
							}
						}
					}
					lObjTrnMonthlyChangeRqst.setGrossAmount(new BigDecimal(lDblChngStWiseGrossAmt));
					lObjTrnMonthlyChangeRqst.setRecoveryAmount(new BigDecimal(lDblChngStWiseRecAmt));
					lObjTrnMonthlyChangeRqst.setNetAmount(new BigDecimal(lDblChngStWiseNetAmt));
					lLstTrnMonthlyChangeRqstVO.add(lObjTrnMonthlyChangeRqst);
				}

				if (lLstTrnMonthlyChangeRqstVO.size() > 0) {
					// lStrChngStmntStatus = "Generated";
					hitStg.saveOrUpdateAll(lLstTrnMonthlyChangeRqstVO);
				}
				if (lLstTrnPensionChangeHdrVO.size() > 0) {
					hitStg.saveOrUpdateAll(lLstTrnPensionChangeHdrVO);
				}
				if (lLstTrnPensionChangeDtlsVO.size() > 0) {
					hitStg.saveOrUpdateAll(lLstTrnPensionChangeDtlsVO);
				}
				if (lLstTrnMonthlyPensionRecoveryDtls.size() > 0) {
					hitStg.saveOrUpdateAll(lLstTrnMonthlyPensionRecoveryDtls);
				}
				/**
				 * Insertion of data in change statement related tables starts
				 * <<<<
				 */

				gLogger.info("Test MonthlyBill Performance =================After Insertion of data in db:" + (System.currentTimeMillis() - prevTime));
				// System.out.println("Test MonthlyBill Performance ===========After Insertion of data in db :"
				// + (System.currentTimeMillis() - prevTime));
			}
			lLstBranchWithNoPnsr.addAll(lLstBranchCode);
			lLstBranchWithNoPnsr.removeAll(lMapBranchwiseMonthlyVO.keySet());
			lSBStatus.append("<XMLDOC>");
			// lSBStatus.append("<STATUS>");
			// lSBStatus.append(lStrChngStmntStatus);
			// lSBStatus.append("</STATUS>");
			if ("branchWise".equals(lStrGenChangStmntBy)) {
				lSBStatus.append("<CHNGRQSTID>");
				lSBStatus.append(lLngMonthlyChngRqstId);
				lSBStatus.append("</CHNGRQSTID>");
			}
			for (String lBranchWithNoPnsr : lLstBranchWithNoPnsr) {
				lSBStatus.append("<BRANCHWITHNOPNSR>");
				lSBStatus.append(lBranchWithNoPnsr);
				lSBStatus.append("</BRANCHWITHNOPNSR>");
			}
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			gLogger.info("Test MonthlyBill Performance =================Total Time:" + (System.currentTimeMillis() - startTime));
			// System.out.println("Test MonthlyBill Performance ===========Total Time: :"
			// + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			gLogger.error("Problem in GetMonthlyPensionMethod error is " + e, e);
			lMapProbPPOBranch = (Map<String, List<String>>) inputMap.get("NEGATIVE_NET_AMT_ERROR");
			lMapNoFPDatesPpoBranch = (Map<String, List<String>>) inputMap.get("NO_FP_DATES_ERROR");

			if ((lMapProbPPOBranch != null && lMapProbPPOBranch.keySet().size() > 0) || (lMapNoFPDatesPpoBranch != null && lMapNoFPDatesPpoBranch.keySet().size() > 0)) {
				lSBStatus.append("<XMLDOC>");
				if (lMapProbPPOBranch != null) {
					for (String lStrProbPPOBranchCode : lMapProbPPOBranch.keySet()) {
						lSBStatus.append("<NETAMTERROR>");
						lSBStatus.append("<PROBPPOBRANCHCODE>");
						lSBStatus.append(lStrProbPPOBranchCode);
						lSBStatus.append("</PROBPPOBRANCHCODE>");
						lSBStatus.append("<PROBPPOLIST>");
						lSBStatus.append(lMapProbPPOBranch.get(lStrProbPPOBranchCode).toString());
						lSBStatus.append("</PROBPPOLIST>");
						lSBStatus.append("</NETAMTERROR>");
					}
				}
				if (lMapNoFPDatesPpoBranch != null) {
					for (String lStrNoFpDatesBranchCode : lMapNoFPDatesPpoBranch.keySet()) {
						lSBStatus.append("<NOFPDATEERROR>");
						lSBStatus.append("<NOFPDATEBRANCHCODE>");
						lSBStatus.append(lStrNoFpDatesBranchCode);
						lSBStatus.append("</NOFPDATEBRANCHCODE>");
						lSBStatus.append("<NOFPDATEPPOLIST>");
						lSBStatus.append(lMapNoFPDatesPpoBranch.get(lStrNoFpDatesBranchCode).toString());
						lSBStatus.append("</NOFPDATEPPOLIST>");
						lSBStatus.append("</NOFPDATEERROR>");
					}
				}
				lSBStatus.append("</XMLDOC>");
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				// resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setResultValue(inputMap);
				resObj.setViewName("ajaxData");
			} else {
				resObj.setResultValue(null);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
			}
		}
		return resObj;
	}

	/**
	 * Method to save monthly pension
	 * 
	 * @param inputMap
	 * @return ResultObject
	 */
	public ResultObject saveMonthlyPension(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

		List<TrnPensionBillHdr> lLstPensionBillHdr = null;
		List<TrnPensionBillDtls> lLstPensionBillDtls = null;
		List<TrnPensionBillReceipt> pnsnRcptVOlist = null;
		List<TrnPensionArrearDtls> lLstPensionArrearDtls = null;
		List<TrnPensionOtherPartyPay> lLstothPartyPay = null;
		Map lBillHdrPks = null;
		StringBuffer sbLog = new StringBuffer();
		String lPnsnTokenNo = null;
		try {
			setSessionInfo(inputMap);

			// insert record in trn_pension_bill_hdr
			lLstPensionBillHdr = (List) inputMap.get("TrnPensionBillHdr");

			if (lLstPensionBillHdr != null && !lLstPensionBillHdr.isEmpty()) {
				lBillHdrPks = saveMonthlyHdr(lLstPensionBillHdr, inputMap);
			}
			// insert multiple records in trn_pension_bill_dtls...for current
			// month
			lLstPensionBillDtls = (List<TrnPensionBillDtls>) inputMap.get("TrnPensionBillDtls");

			if (lLstPensionBillDtls != null && !lLstPensionBillDtls.isEmpty()) {
				saveMonthlyDtls(lLstPensionBillDtls, lBillHdrPks, inputMap);
			}

			pnsnRcptVOlist = (List<TrnPensionBillReceipt>) inputMap.get("PnsnRcptVOlist");
			if (pnsnRcptVOlist != null && !pnsnRcptVOlist.isEmpty()) {
				saveHeadCodeWisePensionRcpt(pnsnRcptVOlist, lBillHdrPks, inputMap);
			}

			Long lLngPnsnBillNo = Long.valueOf(lLstPensionBillHdr.get(0).getBillNo());

			inputMap.put("TrnPensionBillDtls", lLstPensionBillDtls);

			// insert multiple records in trn_pension_arrear_dtls...for previous
			// arrears
			lLstPensionArrearDtls = (List<TrnPensionArrearDtls>) inputMap.get("TrnPensionArrearDtls");

			if (lLstPensionArrearDtls != null && !lLstPensionArrearDtls.isEmpty()) {
				saveArrearDtls(lLstPensionArrearDtls, inputMap, lLngPnsnBillNo);
			}
			inputMap.put("TrnPensionArrearDtls", lLstPensionArrearDtls);

			// // Saving Previous Month Arrear Data in TrnPensionBillDtls
			lLstPensionBillDtls = (List<TrnPensionBillDtls>) inputMap.get("PnsnBillArrearDtlsLst");

			if (lLstPensionBillDtls != null && !lLstPensionBillDtls.isEmpty()) {
				saveMonthlyDtls(lLstPensionBillDtls, lBillHdrPks, inputMap);
			}

			lLstothPartyPay = (List<TrnPensionOtherPartyPay>) inputMap.get("lLstothPartyPay");
			if (lLstothPartyPay != null && !lLstothPartyPay.isEmpty()) {
				saveOthrPartyDtls(lLstothPartyPay, inputMap);
			}

			// getting data from session
			HttpSession session = request.getSession();
			List lLstROPData = (ArrayList) session.getAttribute("PensionROPLst");
			List<TrnPensionArrearDtls> lTrnPaymentLst = (List<TrnPensionArrearDtls>) session.getAttribute("MntlyTrnPaymentLst");
			// List<TrnPensionHeldReasonDtls> lLstHeldReasonDtls =
			// (List<TrnPensionHeldReasonDtls>)
			// session.getAttribute("lLstHeldReasonDtls");

			// removing attributes from session
			session.removeAttribute("PensionROPLst");
			session.removeAttribute("MntlyTrnPaymentLst");
			// session.removeAttribute("lLstHeldReasonDtls");

			// inserting ROP data....if any
			if (lLstROPData != null && !lLstROPData.isEmpty()) {
				saveROPDtls(lLstROPData, inputMap);
			}

			if (lTrnPaymentLst != null && !lTrnPaymentLst.isEmpty()) {
				List<TrnPensionArrearDtls> lLstTrnPaymentLstForInsert = getArrearsLstForTransaction(lTrnPaymentLst);
				if (lLstTrnPaymentLstForInsert != null && !lLstTrnPaymentLstForInsert.isEmpty()) {
					saveArrearDtls(lLstTrnPaymentLstForInsert, inputMap, lLngPnsnBillNo);
				}
			}

			Long lLngBillNo = (Long.parseLong(inputMap.get("billNo").toString()));
			String lStrBillCntrlNo = (String) inputMap.get("PnsnBillCntrlNo");
			Long lLngTokenNo = (Long) inputMap.get("PnsnTokenNo");
			String lLstPpoNos = null;
			if (inputMap.containsKey("PpoNoList")) {
				lLstPpoNos = inputMap.get("PpoNoList").toString();
				lObjMonthlyDAO.updateRltPensioncaseBill(lLngBillNo, lStrBillCntrlNo, lLngTokenNo, lLstPpoNos, gStrLocCode, gLngPostId, gLngUserId, gDate);
			}

			List<Long> lLstRecList = (List) session.getAttribute("cvpDcrgRecId");
			new StringBuffer();
			if (lLstRecList != null && !lLstRecList.isEmpty()) {
				lObjMonthlyDAO.updateRecoveryDtls(lLngBillNo, lLstRecList, gLngPostId, gLngUserId, gDate);
			}
			/*
			 * if(lLstOmrPenCode!=null && lLstOmrPenCode.size()>0) {
			 * lObjMonthlyDAO
			 * .updateRqstHdrOmrFlg(lLngBillNo,lLstOmrPenCode,gStrLocCode); }
			 */

			session.removeAttribute("cvpDcrgRecId");
			// session.removeAttribute("lLstOMR");
			// updating token status
			lPnsnTokenNo = lLstPensionBillHdr.get(0).getTokenNo();
			if (lPnsnTokenNo != null) {
				/*
				 * OrgTokenStatusDAO lObjTokenStatusDAO = new
				 * OrgTokenStatusDAOImpl(OrgTokenStatus.class,
				 * serv.getSessionFactory()); lLngPnsnTokenNo =
				 * Long.parseLong(lPnsnTokenNo);
				 * lObjTokenStatusDAO.updateTokenStatus(lLngPnsnTokenNo,
				 * gStrLocCode, lLngPnsnBillNo, gLngUserId, gLngPostId,
				 * BillProcConstants.INWARD_TYPE_BILL);
				 * 
				 * ReportServiceImpl rptService = new ReportServiceImpl();
				 * rptService.updateTokenIssue(inputMap, lPnsnTokenNo,
				 * lLngPnsnBillNo, DBUtility.getCurrentDateFromDB(), null, "B");
				 */}
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			sbLog.append("Problem in saving monthly pension  is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				objRes.setThrowable(e);
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				objRes.setThrowable(ex);
			}
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}

		return objRes;
	}

	private List<TrnPensionArrearDtls> getArrearsLstForTransaction(List<TrnPensionArrearDtls> lLstArrVO) throws Exception {

		// creating arrear Vo for transaction data from available info
		List<TrnPensionArrearDtls> lLstFinalList = null;
		TrnPensionArrearDtls lObjArrearVO = null;
		TrnPensionArrearDtls lobjArrVO = null;

		try {
			for (int i = 0; i < lLstArrVO.size(); i++) {
				lobjArrVO = lLstArrVO.get(i);
				lObjArrearVO = new TrnPensionArrearDtls();
				lObjArrearVO.setPensionRequestId(lobjArrVO.getPensionRequestId());
				lObjArrearVO.setPensionerCode(lobjArrVO.getPensionerCode());
				lObjArrearVO.setArrearFieldType(lobjArrVO.getArrearFieldType());
				lObjArrearVO.setOldAmountPercentage(new BigDecimal("0.00"));
				lObjArrearVO.setNewAmountPercentage(lobjArrVO.getDifferenceAmount());
				lObjArrearVO.setEffectedFromYyyymm(lobjArrVO.getEffectedFromYyyymm());
				lObjArrearVO.setEffectedToYyyymm(lobjArrVO.getEffectedFromYyyymm());
				lObjArrearVO.setDifferenceAmount(lobjArrVO.getDifferenceAmount());
				lObjArrearVO.setTotalDifferenceAmt(lobjArrVO.getDifferenceAmount());
				lObjArrearVO.setPaymentFromYyyymm(lobjArrVO.getPaymentFromYyyymm());
				lObjArrearVO.setPaymentToYyyymm(lobjArrVO.getPaymentFromYyyymm());
				lObjArrearVO.setCreatedDate(DBUtility.getCurrentDateFromDB());
				lObjArrearVO.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjArrearVO.setCreatedUserId(new BigDecimal(gLngUserId));

				if (lLstFinalList == null) {
					lLstFinalList = new ArrayList<TrnPensionArrearDtls>();
				}
				lLstFinalList.add(lObjArrearVO);
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstFinalList;
	}

	/**
	 * Method to insert/save a record in TrnPensionBillHdr
	 * 
	 * @param TrnPensionBillDtls
	 *            , Map
	 * @return void
	 */
	private Map saveMonthlyHdr(List<TrnPensionBillHdr> lLstPensionBillHdr, Map<String, Object> inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
		Map<String, Object> lBillHdrPks = new HashMap<String, Object>();

		try {
			String lStrBillType = bundleConst.getString("RECOVERY.MONTHLY");
			String lStrCVPBillType = bundleConst.getString("RECOVERY.CVP");
			String lStrDCRGBillType = bundleConst.getString("RECOVERY.DCRG");

			Long lBillNo = (Long.parseLong(inputMap.get("billNo").toString()));
			TrnPensionBillHdrDAO lObjPensionHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, serv.getSessionFactory());
			Long ledgerID = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_bill_hdr", inputMap, lLstPensionBillHdr.size());
			for (TrnPensionBillHdr lObjPensionBillHdr : lLstPensionBillHdr) {
				lObjPensionBillHdr.setBillNo(lBillNo);
				lObjPensionBillHdr.setTrnPensionBillHdrId(IFMSCommonServiceImpl.getFormattedPrimaryKey(++ledgerID, inputMap));
				lObjPensionHdrDAO.create(lObjPensionBillHdr);
				if (lStrBillType.equals(lObjPensionBillHdr.getBillType())) {
					lBillHdrPks.put("90" + lObjPensionBillHdr.getHeadCode().toString(), lObjPensionBillHdr.getTrnPensionBillHdrId());
				} else if (lStrCVPBillType.equals(lObjPensionBillHdr.getBillType())) {
					lBillHdrPks.put("110" + lObjPensionBillHdr.getHeadCode().toString(), lObjPensionBillHdr.getTrnPensionBillHdrId());
				} else if (lStrDCRGBillType.equals(lObjPensionBillHdr.getBillType())) {
					lBillHdrPks.put("100" + lObjPensionBillHdr.getHeadCode().toString(), lObjPensionBillHdr.getTrnPensionBillHdrId());
				}
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}

		return lBillHdrPks;
	}

	private Map saveOthrPartyDtls(List<TrnPensionOtherPartyPay> lLstOthrPartyPay, Map<String, Object> inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
		Map<String, Object> lBillHdrPks = new HashMap<String, Object>();

		try {
			Long lBillNo = (Long.parseLong(inputMap.get("billNo").toString()));
			TrnPensionOtherPartyPayDAO lObjDAO = new TrnPensionOtherPartyPayDAOImpl(TrnPensionOtherPartyPay.class, serv.getSessionFactory());
			Long ledgerID = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("Trn_Pension_Other_Party_Pay", inputMap, lLstOthrPartyPay.size());
			for (TrnPensionOtherPartyPay lObj : lLstOthrPartyPay) {
				lObj.setBillNo(lBillNo);
				lObj.setBillDate(gDate);
				lObj.setOtherPartyPaymentId(IFMSCommonServiceImpl.getFormattedPrimaryKey(++ledgerID, inputMap));
				lObjDAO.create(lObj);

			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}

		return lBillHdrPks;
	}

	/**
	 * Method to insert/save a record in TrnPensionBillDtls
	 * 
	 * @param TrnPensionBillDtls
	 *            , Map
	 * @return void
	 */
	private void saveMonthlyDtls(List<TrnPensionBillDtls> lLstPensionBillDtls, Map lBillHdrPks, Map<String, Object> inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lHeadCode = null;
		Long BillHdrId = null;
		try {
			HibernateTemplate hmt = new HibernateTemplate(serv.getSessionFactory());
			Long ledgerID = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_bill_dtls", inputMap, lLstPensionBillDtls.size());
			for (TrnPensionBillDtls lObjPensionBillDtls : lLstPensionBillDtls) {
				lHeadCode = lObjPensionBillDtls.getTrnPensionBillHdrId().toString();
				BillHdrId = (Long) lBillHdrPks.get(lHeadCode);
				lObjPensionBillDtls.setTrnPensionBillHdrId(BillHdrId);
				lObjPensionBillDtls.setTrnPensionBillDtlsId(IFMSCommonServiceImpl.getFormattedPrimaryKey(++ledgerID, inputMap));
				hmt.save(lObjPensionBillDtls);
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	/**
	 * Method to insert/save a HeadCode wise Bill Receipt Details in
	 * TrnPensionBillReceiptDtls
	 * 
	 * @param lLstPensionBillDtls
	 * @param lBillHdrPks
	 * @param inputMap
	 * @throws Exception
	 */
	private void saveHeadCodeWisePensionRcpt(List<TrnPensionBillReceipt> lLstPnsnRcptVoLst, Map lBillHdrPks, Map<String, Object> inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lHeadCode = null;
		Long BillHdrId = null;

		try {
			HibernateTemplate hmt = new HibernateTemplate(serv.getSessionFactory());
			Long ledgerID = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_bill_receipt", inputMap, lLstPnsnRcptVoLst.size());
			for (TrnPensionBillReceipt lObjPnsnBillReceipt : lLstPnsnRcptVoLst) {
				lHeadCode = lObjPnsnBillReceipt.getTrnPensionBillHdrId().toString();
				BillHdrId = (Long) lBillHdrPks.get(lHeadCode);
				lObjPnsnBillReceipt.setTrnPensionBillHdrId(BillHdrId);
				lObjPnsnBillReceipt.setTrnPensionBillReceiptId(IFMSCommonServiceImpl.getFormattedPrimaryKey(++ledgerID, inputMap));
				hmt.save(lObjPnsnBillReceipt);
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	private void saveArrearDtls(List<TrnPensionArrearDtls> lLstPensionArrearDtls, Map<String, Object> inputMap, Long billNo) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		try {
			HibernateTemplate hmt = new HibernateTemplate(serv.getSessionFactory());
			TrnPensionArrearDtls lObjPensionArrearDtls = null;
			Long ledgerID = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_arrear_dtls", inputMap, lLstPensionArrearDtls.size());
			for (int i = 0; i < lLstPensionArrearDtls.size(); i++) {
				lObjPensionArrearDtls = lLstPensionArrearDtls.get(i);
				lObjPensionArrearDtls.setBillNo(billNo);
				lObjPensionArrearDtls.setPensionArrearDtlsId(IFMSCommonServiceImpl.getFormattedPrimaryKey(++ledgerID, inputMap));
				hmt.save(lObjPensionArrearDtls);
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	private void saveROPDtls(List lLstMainROPLst, Map<String, Object> inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		Double lSanctionAmt = 0d;
		SimpleDateFormat lObjSmplDateFrm = new SimpleDateFormat("dd/MM/yyyy");

		try {
			TrnPensionRqstDtlsDAOImpl lObjrqstHdr = new TrnPensionRqstDtlsDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			ArrayList lLstROP = null;
			for (int i = 0; i < lLstMainROPLst.size(); i++) {
				lLstROP = (ArrayList) lLstMainROPLst.get(i);
				TrnPensionRqstHdr lObjRqstHdrVO = new TrnPensionRqstHdr();
				lObjRqstHdrVO = lObjrqstHdr.read((Long) lLstROP.get(0));

				if (lObjRqstHdrVO.getCommensionDate().before(lObjSmplDateFrm.parse("01/04/2004"))) {
					lSanctionAmt = Double.valueOf(lLstROP.get(5).toString());
				} else if (lObjRqstHdrVO.getDpPercent() != null && lObjRqstHdrVO.getDpPercent().intValue() > 0) {
					if (lLstROP.get(4) != null && lLstROP.get(4).toString().equalsIgnoreCase("Y")) {
						lSanctionAmt = Double.valueOf(lLstROP.get(5).toString());
					} else {
						Double lDPAmt = ((Double.valueOf(lLstROP.get(5).toString()) * lObjRqstHdrVO.getDpPercent().doubleValue()) / 100);
						lSanctionAmt = (Double.valueOf(lLstROP.get(5).toString()) + Math.round(lDPAmt));
					}
				} else if (lLstROP.get(4) != null && lLstROP.get(4).toString().equalsIgnoreCase("Y")) {
					lSanctionAmt = Double.valueOf(lLstROP.get(5).toString());
				}

				if (lSanctionAmt > 0) {
					lObjRqstHdrVO.setDppfAmount(new BigDecimal(lSanctionAmt));
				}

				lObjRqstHdrVO.setBasicPensionAmount(new BigDecimal(lLstROP.get(5).toString()));
				lObjRqstHdrVO.setFp1Amount(new BigDecimal(lLstROP.get(6).toString()));
				lObjRqstHdrVO.setFp2Amount(new BigDecimal(lLstROP.get(7).toString()));
				lObjRqstHdrVO.setIsRop("P"); // Set ROP As a Paid
				lObjrqstHdr.update(lObjRqstHdrVO);

				// now need to update the rop records
				NewPensionBillDAOImpl lObjNewPensionDAO = new NewPensionBillDAOImpl(serv.getSessionFactory());
				Long lPKofROP = 0L;
				TrnPnsncaseRopRlt lobjROP = null;
				TrnPnsncaseRopRltDAO lObjROPDAO = new TrnPnsncaseRopRltDAO(TrnPnsncaseRopRlt.class, serv.getSessionFactory());

				if ("Y".equals(lLstROP.get(4).toString())) // for 2006 updation
				{
					lPKofROP = lObjNewPensionDAO.getPKOfPnsncaseROPRlt(lLstROP.get(1).toString(), "2006");
					lobjROP = lObjROPDAO.read(lPKofROP);
					lobjROP.setRopPaid("P");
					lObjROPDAO.update(lobjROP);

					if ("Y".equals(lLstROP.get(3).toString())) // for 1996
					// updation
					{
						lPKofROP = lObjNewPensionDAO.getPKOfPnsncaseROPRlt(lLstROP.get(1).toString(), "1996");
						lobjROP = lObjROPDAO.read(lPKofROP);
						lobjROP.setRopPaid("P");
						lObjROPDAO.update(lobjROP);

						if ("Y".equals(lLstROP.get(2).toString())) // for 1986
						// updation
						{
							lPKofROP = lObjNewPensionDAO.getPKOfPnsncaseROPRlt(lLstROP.get(1).toString(), "1986");
							lobjROP = lObjROPDAO.read(lPKofROP);
							lobjROP.setRopPaid("P");
							lObjROPDAO.update(lobjROP);
						}
					}
				}
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	/*
	 * private void saveHeldDtls(List<TrnPensionHeldReasonDtls> lLstHeldDtls,
	 * Map<String, Object> inputMap) throws Exception {
	 * 
	 * ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
	 * TrnPensionHeldReasonDtls lObjHeldDtls = null;
	 * 
	 * try { TrnPensionHeldReasonDtlsDaoImpl lObjHeldDtlsDAO = new
	 * TrnPensionHeldReasonDtlsDaoImpl(TrnPensionHeldReasonDtls.class,
	 * serv.getSessionFactory()); setSessionInfo(inputMap); for (int i = 0; i <
	 * lLstHeldDtls.size(); i++) { lObjHeldDtls = null; lObjHeldDtls =
	 * lLstHeldDtls.get(i); lObjHeldDtls.setCreatedDate(gDate);
	 * lObjHeldDtls.setCreatedPostId(new BigDecimal(gLngPostId));
	 * lObjHeldDtls.setCreatedUserId(new BigDecimal(gLngUserId)); long pkID =
	 * IFMSCommonServiceImpl.getNextSeqNum("trn_pension_held_reason_dtls",
	 * inputMap); lObjHeldDtls.setRltPpoHeldId(pkID);
	 * lObjHeldDtlsDAO.create(lObjHeldDtls); } } catch(Exception e) {
	 * gLogger.error(" Error is : " + e, e); throw (e); } }
	 */

	/**
	 * Method to view saved monthly pension Bill
	 * 
	 * @param Map
	 * @return ResultObject
	 */

	public ResultObject viewMonthlyBill(Map<String, Object> argsMap) {

		Map<String, Object> inputMap = argsMap;
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
		List<TrnPensionBillHdr> lLstPensionBillHdr = null;
		BigDecimal lTotalNetAmt = BigDecimal.ZERO;
		List<Long> lLstBillNo = new ArrayList<Long>();
		String lStrBankName = null;
		String lStrBranchName = null;
		List lstBank = new ArrayList();
		List lLstMonthDtls = new ArrayList();
		StringBuffer sbLog = new StringBuffer();
		Map<BigDecimal, String> lMapHeadCodeSeries = new HashMap<BigDecimal, String>();
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
		try {
			setSessionInfo(inputMap);
			String lStrBillType = bundleConst.getString("RECOVERY.MONTHLY");
			String lStrCVPBillType = bundleConst.getString("RECOVERY.CVP");
			String lStrDCRGBillType = bundleConst.getString("RECOVERY.DCRG");
			lMapHeadCodeSeries = lObjCommonPensionDAO.getAllHeadCodeSeriesMap();
			if (inputMap.containsKey("BillNo") && inputMap.get("BillNo") != null) {
				lLstBillNo = (List) inputMap.get("BillNo");
			} else {
				lLstBillNo.add(Long.parseLong(StringUtility.getParameter("billNo", request).trim()));
			}
			MonthlyPensionBillDAOImpl lObjMonthlyBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			if (lLstBillNo != null && !lLstBillNo.isEmpty()) {
				lLstPensionBillHdr = lObjMonthlyBillDAO.getTrnPensionBillHdr(lLstBillNo);
				//
				// lStrCheqNo = lObjMonthlyBillDAO.getChqNoFrmBil(lLstBillNo);
				//
				// if (lStrCheqNo != null) {
				// inputMap.put("ChqNo", lStrCheqNo);
				// }

				// for (TrnPensionBillHdr lObjTrnPensionBillHdr :
				// lLstPensionBillHdr) {
				// if (lStrBillType.equals(lObjTrnPensionBillHdr.getBillType()))
				// {
				// lStrTrnBillHdrIds +=
				// lObjTrnPensionBillHdr.getTrnPensionBillHdrId() + ",";
				// lBillHdrIds.put(lObjTrnPensionBillHdr.getTrnPensionBillHdrId().toString(),
				// lObjTrnPensionBillHdr.getHeadCode());
				// } else if
				// (lStrCVPBillType.equals(lObjTrnPensionBillHdr.getBillType()))
				// {
				// lStrCVPBillHdrIds +=
				// lObjTrnPensionBillHdr.getTrnPensionBillHdrId() + ",";
				// lBillHdrIds.put(lObjTrnPensionBillHdr.getTrnPensionBillHdrId().toString(),
				// lObjTrnPensionBillHdr.getHeadCode());
				// } else if
				// (lStrDCRGBillType.equals(lObjTrnPensionBillHdr.getBillType()))
				// {
				// lStrDCRGBillHdrIds +=
				// lObjTrnPensionBillHdr.getTrnPensionBillHdrId() + ",";
				// lBillHdrIds.put(lObjTrnPensionBillHdr.getTrnPensionBillHdrId().toString(),
				// lObjTrnPensionBillHdr.getHeadCode());
				// }
				// }

				// lStrTrnBillHdrIds = lStrTrnBillHdrIds.substring(0,
				// lStrTrnBillHdrIds.length() - 1);
				// if (lStrCVPBillHdrIds.length() > 0) {
				// lStrCVPBillHdrIds = lStrCVPBillHdrIds.substring(0,
				// lStrCVPBillHdrIds.length() - 1);
				// }
				// if (lStrDCRGBillHdrIds.length() > 0) {
				// lStrDCRGBillHdrIds = lStrDCRGBillHdrIds.substring(0,
				// lStrDCRGBillHdrIds.length() - 1);
				// }
				//
				// lStrForMonth =
				// lLstPensionBillHdr.get(0).getForMonth().toString();
				// if (lStrTrnBillHdrIds != "") {
				// lObjMonthlyBillDAO.getTrnPensionBillDtls(lStrTrnBillHdrIds,
				// lStrForMonth, "Y");
				// lObjMonthlyBillDAO.getTrnPensionBillDtls(lStrTrnBillHdrIds,
				// lStrForMonth, "N");
				// }
				//
				// if (lStrCVPBillHdrIds != "") {
				// lObjMonthlyBillDAO.getTrnPensionBillDtls(lStrCVPBillHdrIds,
				// lStrForMonth, "Y");
				// }
				// if (lStrDCRGBillHdrIds != "") {
				// lObjMonthlyBillDAO.getTrnPensionBillDtls(lStrDCRGBillHdrIds,
				// lStrForMonth, "Y");
				// }

				// if (lLstCVPBillDtls != null && !lLstCVPBillDtls.isEmpty()) {
				// for (int i = 0; i < lLstCVPBillDtls.size(); i++) {
				// lObjTrnPensionBillDtls = lLstCVPBillDtls.get(i);
				// lObjMonthlyPensionVO = new MonthlyPensionBillVO();
				// lObjMonthlyPensionVO =
				// getMonthlyFromBilldtls(lObjTrnPensionBillDtls, lBillHdrIds);
				// lTotalNetAmt = lTotalNetAmt +
				// getInitAmt(lObjTrnPensionBillDtls.getReducedPension()).doubleValue();
				// lLstCVPVO.add(lObjMonthlyPensionVO);
				// }
				// }

				// if (lLstDCRGBillDtls != null && !lLstDCRGBillDtls.isEmpty())
				// {
				// for (int i = 0; i < lLstDCRGBillDtls.size(); i++) {
				// lObjTrnPensionBillDtls = lLstDCRGBillDtls.get(i);
				// lObjMonthlyPensionVO = new MonthlyPensionBillVO();
				// lObjMonthlyPensionVO =
				// getMonthlyFromBilldtls(lObjTrnPensionBillDtls, lBillHdrIds);
				// lTotalNetAmt = lTotalNetAmt +
				// getInitAmt(lObjTrnPensionBillDtls.getReducedPension()).doubleValue();
				// lLstDCRGVO.add(lObjMonthlyPensionVO);
				// }
			}
			Long lLngBillNo = Long.parseLong(StringUtility.getParameter("billNo", request).trim());
			List<MonthlyPensionBillVO> lLstTmpMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			MonthlyPensionBillDAOImpl lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			lLstTmpMonthlyPensionBillVO = lObjMonthlyPensionBillDAO.getMonthlyPensionBillVOList(lLngBillNo, gStrLocCode, null);
			// ---For showing pension category(series) instead of headcode in
			// bill.
			for (MonthlyPensionBillVO lObjMonthlyPensionBillVO : lLstTmpMonthlyPensionBillVO) {
				lObjMonthlyPensionBillVO.setSeries(lMapHeadCodeSeries.get(lObjMonthlyPensionBillVO.getHeadCode()));
				lLstMonthlyPensionBillVO.add(lObjMonthlyPensionBillVO);
			}
			inputMap.put("MonthlyPensionList", lLstMonthlyPensionBillVO);

			// Map lMapLedgerAndPageNo =
			// lObjMonthlyBillDAO.getLedgerAndPageNo(lStrTrnBillHdrIds,
			// lStrForMonth, "Y", gStrLocCode);
			// String ledgerNo = null;
			// String pageNo = null;
			//
			// for (int i = 0; i < lLstBillDtlsCurrMonth.size(); i++) {
			// lObjTrnPensionBillDtls = lLstBillDtlsCurrMonth.get(i);
			// lObjMonthlyPensionVO = new MonthlyPensionBillVO();
			// lObjMonthlyPensionVO =
			// getMonthlyFromBilldtls(lObjTrnPensionBillDtls, lBillHdrIds);
			// ledgerNo =
			// lMapLedgerAndPageNo.get(lObjMonthlyPensionVO.getPensionerCode())
			// != null
			// ?
			// lMapLedgerAndPageNo.get(lObjMonthlyPensionVO.getPensionerCode()).toString().split("~")[0]
			// : "0";
			// pageNo =
			// lMapLedgerAndPageNo.get(lObjMonthlyPensionVO.getPensionerCode())
			// != null ?
			// lMapLedgerAndPageNo.get(lObjMonthlyPensionVO.getPensionerCode()).toString().split("~")[1]
			// : "0";
			// lObjMonthlyPensionVO.setLedgerNo(ledgerNo);
			// lObjMonthlyPensionVO.setPageNo(pageNo);
			// lTotalNetAmt = lTotalNetAmt +
			// getInitAmt(lObjTrnPensionBillDtls.getReducedPension()).doubleValue();
			// lTotalNetAmt = lTotalNetAmt +
			// getInitAmt(lObjTrnPensionBillDtls.getMoCommission()).doubleValue();
			// lLstMonthlyPensionVO.add(lObjMonthlyPensionVO);
			//
			// // creating pensioner code list for computing prev month
			// // data...start
			// if
			// (!lStrChkPnsnrCode.equals(lObjTrnPensionBillDtls.getPensionerCode()))
			// {
			// lStrChkPnsnrCode = lObjTrnPensionBillDtls.getPensionerCode();
			// lLstPnsnrCode.add(lObjTrnPensionBillDtls.getPensionerCode());
			// }
			// // creating pensioner code list for computing prev month
			// // data...end
			//
			// }

			// to fetch other [party details for printing

			// lLstOthrParty =
			// lObjMonthlyBillDAO.getOtherPartyPayLst(lStrForMonth, lLstBillNo,
			// gStrLocCode);
			// to fetch other [party details for printing

			// list of current month data is ready...end

			// now prepare list of previous month data we need a list of
			// (pensioner code and that pensioner's prev month data list)
			// item 1 : pensioner Code and item 2 : list of prev month data
			// for that pensioner we also need another list which has flag
			// Y/N and pensioner
			// code for all pensioners having current month data...for the
			// hyperlink to open arrear window for that pensioner

			// for (int x = 0; x < lLstPnsnrCode.size(); x++) {
			// String lPnsnrCode = lLstPnsnrCode.get(x).toString();
			// lLstMonthlyArrearVO = new ArrayList<MonthlyPensionBillVO>();
			//
			// for (int y = 0; y < lLstBillDtlsPrevMonth.size(); y++) {
			// // now create a list for each pensioner for all his
			// // previous month data
			// TrnPensionBillDtls lObjBillDtlsVO = lLstBillDtlsPrevMonth.get(y);
			//
			// if (lPnsnrCode.equals(lObjBillDtlsVO.getPensionerCode())) {
			// // this record is for the current pensioner being
			// // considered...hence add in list
			// lObjMonthlyPensionVO = new MonthlyPensionBillVO();
			// lObjMonthlyPensionVO = getMonthlyFromBilldtls(lObjBillDtlsVO,
			// lBillHdrIds);
			// lLstMonthlyArrearVO.add(lObjMonthlyPensionVO);
			// }
			// }

			// // lLstMonthlyArrearVO...this list contains prev month data
			// // for this pensioner
			// if (!lLstMonthlyArrearVO.isEmpty()) {
			// // this pensioner has some previous month data add it to
			// // the list (pensioner code and list)
			// lPrvMonthlyArrearLst.add(lPnsnrCode);
			// lPrvMonthlyArrearLst.add(lLstMonthlyArrearVO);
			//
			// List<String> ltemp = new ArrayList();
			// ltemp.add("Y");
			// ltemp.add(lPnsnrCode);
			// lPrvArrChckLst.add(ltemp);
			// } else {
			// // this pensioner does not have any previous month data
			// List<String> ltemp = new ArrayList();
			// ltemp.add("N");
			// ltemp.add(lPnsnrCode);
			// lPrvArrChckLst.add(ltemp);
			// }
			// }
			//
			// inputMap.put("PrvArrChckLst", lPrvArrChckLst);

			// put required list in session
			// HttpSession session = request.getSession();
			// session.setAttribute("PrvMonthlyArrearLst",
			// lPrvMonthlyArrearLst);
			//
			// inputMap.put("CVPList", lLstCVPVO);
			// inputMap.put("DCRGList", lLstDCRGVO);
			// inputMap.put("MonthlyPensionList", lLstMonthlyPensionVO);

			inputMap.put("Date", lLstPensionBillHdr.get(0).getForMonth());
			inputMap.put("PnsnHeadCode", lLstPensionBillHdr.get(0).getHeadCode());
			inputMap.put("PensionType", lLstPensionBillHdr.get(0).getBillType());
			inputMap.put("PnsnBillTokenNo", lLstPensionBillHdr.get(0).getTokenNo());
			MonthlyPensionBillDAO lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			String lStrHeadcode = lLstPensionBillHdr.get(0).getHeadCode().toString();
			String lStrBank = lLstPensionBillHdr.get(0).getBankCode();
			String lStrBranch = lLstPensionBillHdr.get(0).getBranchCode();
			String lStrScheme = lLstPensionBillHdr.get(0).getScheme();

			if (lStrBank != null && lStrBranch != null && !lStrBranch.toString().equals("")) {
				lstBank = lObjMonthlyDAO.getBankCode(lStrBranch, gStrLocCode);
				if (lstBank != null && !lstBank.isEmpty()) {
					Object[] lObjArr = (Object[]) lstBank.get(0);
					lStrBank = (String) lObjArr[0];
					lStrBankName = (String) lObjArr[1];
					lStrBranchName = (String) lObjArr[2];
					inputMap.put("BankName", lStrBankName);
					inputMap.put("BranchName", lStrBranchName);
				} else {
					inputMap.put("BankName", "");
					inputMap.put("BranchName", "");

				}

			}

			inputMap.put("Bank", lStrBank);
			inputMap.put("Branch", lStrBranch);
			inputMap.put("Scheme", lStrScheme);
			inputMap.put("HeadCode", lStrHeadcode);
			CommonPensionDAOImpl lObjMonthlyPnsnDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
			String lStrDesc = lObjMonthlyPnsnDAO.getAllHeadCodeDesc(lStrHeadcode, SessionHelper.getLangId(inputMap));
			inputMap.put("HeadCodeDesc", lStrDesc);

			// Integer date = lLstPensionBillHdr.get(0).getForMonth();
			// Integer month = date % 100;
			// Integer year = date / 100;
			// String lStrMonth = month.toString();
			// String lStrYear = year.toString();

			// find bank name and branch name and month dtls

			// if (!lStrScheme.equalsIgnoreCase("Money Order") &&
			// !lStrScheme.equalsIgnoreCase("RUBARU") &&
			// !lStrBranch.equals("")) { lStrBankName =
			// lObjMonthlyDAO.getBankName(lStrBank); lStrBranchName =
			// lObjMonthlyDAO.getBranchName(lStrBank, lStrBranch); }
			// lLstMonthDtls = lObjMonthlyDAO.getMonthDtls(lStrMonth,
			// gLngLangId);

			// if (!lStrScheme.equalsIgnoreCase("Money Order") &&
			// !lStrScheme.equalsIgnoreCase("RUBARU") &&
			// !lStrBranch.equals("")) { inputMap.put("BankName",
			// lStrBankName); inputMap.put("BranchName", lStrBranchName); }
			// inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
			// inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
			// inputMap.put("ForYear", lStrYear);
			// inputMap.put("ForBillYear", lStrYear);
			String lStrDatePayMonth = lLstPensionBillHdr.get(0).getForMonth().toString();
			lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			String lStrMonth = lStrDatePayMonth.substring(4);
			String lStrYear = lStrDatePayMonth.substring(0, 4);
			lLstMonthDtls = lObjMonthlyDAO.getMonthDtls(lStrMonth, gLngLangId);
			inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
			inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
			inputMap.put("ForBillYear", lStrYear);

			// generate the print string for monthly pension report
			inputMap.put("brnchCode", lStrBranch);

			// if (lLstOthrParty != null && lLstOthrParty.size() > 0) {
			// inputMap.put("otherPartyPaid" + lStrBranch, lLstOthrParty);
			// }

			// Create distinct Head_Code,Descript Map
			// Set<BigDecimal> lSetHeadCode = new HashSet<BigDecimal>();
			// for (MonthlyPensionBillVO mo : lLstMonthlyPensionVO) {
			// lSetHeadCode.add(mo.getHeadCode());
			// }
			// CommonPensionDAO lObjCommonPnsnDAO = new
			// CommonPensionDAOImpl(srvcLoc.getSessionFactory());
			// Map<String, String> lMapHeadDesc =
			// lObjCommonPnsnDAO.getHeadCodeDesc(lSetHeadCode.toArray(),
			// gLngLangId);
			// inputMap.put("HeadDesc", lMapHeadDesc);
			//
			// resObj = srvcLoc.executeService("PRINT_MNTHLY_PEN_BILL",
			// inputMap);
			// inputMap = (Map<String, Object>) resObj.getResultValue();
			List<MstPensionHeadcode> lLstMstPensionHeadcode = new ArrayList<MstPensionHeadcode>();
			lLstMstPensionHeadcode = lObjMonthlyPensionBillDAO.getAllHeadCodeAndDesc();
			Map<BigDecimal, String> lMpHeadCodeDesc = new HashMap<BigDecimal, String>();

			for (MstPensionHeadcode lObjMstPensionHeadcode : lLstMstPensionHeadcode) {
				lMpHeadCodeDesc.put(lObjMstPensionHeadcode.getHeadCode(), lObjMstPensionHeadcode.getDescription());
			}
			List lHeadCodeWiseDtlsLst = new ArrayList();
			List lCVPHeadCodeWiseDtlsLst = new ArrayList();
			List lDCRGHeadCodeWiseDtlsLst = new ArrayList();

			for (TrnPensionBillHdr lObjTrnPensionBillHdr : lLstPensionBillHdr) {
				List lTemp = new ArrayList();

				lTemp.add(lMapHeadCodeSeries.get(lObjTrnPensionBillHdr.getHeadCode()));
				lTemp.add(lMpHeadCodeDesc.get(lObjTrnPensionBillHdr.getHeadCode()));
				lTemp.add(lObjTrnPensionBillHdr.getGrossAmount());
				lTemp.add(lObjTrnPensionBillHdr.getDeductionA());
				lTemp.add(lObjTrnPensionBillHdr.getDeductionB());
				lTemp.add(lObjTrnPensionBillHdr.getNetAmount());
				lTotalNetAmt = lTotalNetAmt.add(lObjTrnPensionBillHdr.getNetAmount());
				if (lStrBillType.equals(lObjTrnPensionBillHdr.getBillType())) {
					lHeadCodeWiseDtlsLst.add(lTemp);
				} else if (lStrCVPBillType.equals(lObjTrnPensionBillHdr.getBillType())) {
					lCVPHeadCodeWiseDtlsLst.add(lTemp);
				} else if (lStrDCRGBillType.equals(lObjTrnPensionBillHdr.getBillType())) {
					lDCRGHeadCodeWiseDtlsLst.add(lTemp);
				}
			}
			inputMap.put("billNo", StringUtility.getParameter("billNo", request).trim());
			inputMap.put("HeadCodeWiseDtls", lHeadCodeWiseDtlsLst);
			// inputMap.put("CVPHeadCodeWiseDtls", lCVPHeadCodeWiseDtlsLst);
			// inputMap.put("DCRGHeadCodeWiseDtls", lDCRGHeadCodeWiseDtlsLst);
			inputMap.put("TotalNetAmt", lTotalNetAmt);
			RuleBasedNumberFormat fomatter = new RuleBasedNumberFormat(IReportConstants.INDIAN_ENG_RULE_SET);
			String result = fomatter.format(new com.ibm.icu.math.BigDecimal(lTotalNetAmt));
			inputMap.put("TotalNetAmtInWords", result);
			inputMap.put("isMonthlyBill", "Y");// This is temporary flag for
												// setting hardcoded scheme code
												// in monthly bill display.

			// inputMap.put("MonthlyPrint", "Y");
			getMonthlyReport(inputMap);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			sbLog.append("Problem in viewing monthly   is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				resObj.setThrowable(e);
				// e.printStackTrace();
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				// ex.printStackTrace();
				resObj.setThrowable(ex);
			}
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/**
	 * Method to create MonthlyPensionBillVO from TrnPensionBillDtls VO
	 * 
	 * @param TrnPensionBillDtls
	 * @return MonthlyPensionBillVO
	 */
	// private MonthlyPensionBillVO getMonthlyFromBilldtls(TrnPensionBillDtls
	// lObjTrnPensionBillDtls, Map lBillHdrIds) throws Exception {
	//
	// MonthlyPensionBillVO lObjMonthlyPensionVO = null;
	// try {
	// lObjMonthlyPensionVO = new MonthlyPensionBillVO();
	// lObjMonthlyPensionVO.setPpoNo(lObjTrnPensionBillDtls.getPpoNo().toString());
	// lObjMonthlyPensionVO.setHeadCode((BigDecimal)
	// lBillHdrIds.get(lObjTrnPensionBillDtls.getTrnPensionBillHdrId().toString()));
	// lObjMonthlyPensionVO.setPensionerName(lObjTrnPensionBillDtls.getPensionerName());
	// lObjMonthlyPensionVO.setAccountNo(lObjTrnPensionBillDtls.getAccountNo());
	// lObjMonthlyPensionVO.setAllnBf11156(getInitAmt(lObjTrnPensionBillDtls.getAllcationBf11156()));
	// lObjMonthlyPensionVO.setAllnAf11156(getInitAmt(lObjTrnPensionBillDtls.getAllcationAf11156()));
	// lObjMonthlyPensionVO.setAllnAf10560(getInitAmt(lObjTrnPensionBillDtls.getAllcationAf10560()));
	// lObjMonthlyPensionVO.setBasicPensionAmount(getInitAmt(lObjTrnPensionBillDtls.getPensionAmount()));
	// lObjMonthlyPensionVO.setDpPercentAmount(getInitAmt(lObjTrnPensionBillDtls.getDpAmount()));
	// lObjMonthlyPensionVO.setAdpAmount(getInitAmt(lObjTrnPensionBillDtls.getAdpAmount()));
	// lObjMonthlyPensionVO.setTiPercentAmount(getInitAmt(lObjTrnPensionBillDtls.getTiAmount()));
	// lObjMonthlyPensionVO.setMedicalAllowenceAmount(getInitAmt(lObjTrnPensionBillDtls.getMedicalAllowenceAmount()));
	// lObjMonthlyPensionVO.setCvpMonthlyAmount(getInitAmt(lObjTrnPensionBillDtls.getCvpMonthAmount()));
	// lObjMonthlyPensionVO.setTIArrearsAmount(getInitAmt(lObjTrnPensionBillDtls.getTiArrearAmount()));
	// lObjMonthlyPensionVO.setOtherArrearsAmount(getInitAmt(lObjTrnPensionBillDtls.getArrearAmount()));
	// lObjMonthlyPensionVO.setItCutAmount(getInitAmt(lObjTrnPensionBillDtls.getIncomeTaxCutAmount()));
	// lObjMonthlyPensionVO.setSpecialCutAmount(getInitAmt(lObjTrnPensionBillDtls.getSpecialCutAmount()));
	// lObjMonthlyPensionVO.setOtherBenefit(getInitAmt(lObjTrnPensionBillDtls.getOtherBenefits()));
	// lObjMonthlyPensionVO.setOMR(getInitAmt(lObjTrnPensionBillDtls.getOmr()));
	// lObjMonthlyPensionVO.setPensionCutAmount(getInitAmt(lObjTrnPensionBillDtls.getPensnCutAmount()));
	// lObjMonthlyPensionVO.setRecoveryAmount(getInitAmt(lObjTrnPensionBillDtls.getRecoveryAmount()));
	// lObjMonthlyPensionVO.setNetPensionAmount(getInitAmt(lObjTrnPensionBillDtls.getReducedPension()));
	// lObjMonthlyPensionVO.setPersonalPension(getInitAmt(lObjTrnPensionBillDtls.getPersonalPensionAmount()));
	// lObjMonthlyPensionVO.setIr(getInitAmt(lObjTrnPensionBillDtls.getIrAmount()));
	// lObjMonthlyPensionVO.setMOComm(getInitAmt(lObjTrnPensionBillDtls.getMoCommission()));
	// lObjMonthlyPensionVO.setForMonth(lObjTrnPensionBillDtls.getPayForMonth());
	// lObjMonthlyPensionVO.setPensionerCode(lObjTrnPensionBillDtls.getPensionerCode());
	// } catch (Exception e) {
	// gLogger.error("Error is :" + e, e);
	// throw (e);
	// }
	// return lObjMonthlyPensionVO;
	// }
	/**
	 * Method to returns 0 if null otherwise returns the variable value
	 * 
	 * @param BigDecimal
	 * @return BigDecimal
	 */
	private BigDecimal getInitAmt(BigDecimal lBdAmt) {

		BigDecimal lBdRetrnAmt = new BigDecimal(0);
		if (lBdAmt != null) {
			lBdRetrnAmt = lBdAmt;
		}
		return lBdRetrnAmt;

	}

	/**
	 * Method to validate if monthly bill has already been generated for the
	 * parameters selected by user
	 * 
	 * @param Map
	 * @return ResultObject
	 */
	public ResultObject validateBill(Map<String, Object> inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		StringBuffer sbLog = new StringBuffer();
		try {
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

			String lstrForMonth = StringUtility.getParameter("cmbForMonth", request).trim();
			StringUtility.getParameter("cmbForYear", request).trim();
			String lstrBank = StringUtility.getParameter("cmbBank", request).trim();
			String lstrBranch = StringUtility.getParameter("cmbBranch", request).trim();
			String lStrScheme = StringUtility.getParameter("cmbForScheme", request).trim();
			StringUtility.getParameter("txtPPONo", request).trim();
			List lstBank = new ArrayList();

			SessionHelper.getPostId(inputMap);
			SessionHelper.getLocationCode(inputMap);

			MonthlyPensionBillDAOImpl lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			if (Integer.parseInt(lstrForMonth) < 10) {
			} else {
			}

			if ("".equals(lstrBank) && lstrBank.length() <= 0 && lstrBranch.length() > 0 && "IRLA".equals(lStrScheme)) {
				lstBank = lObjPensionBillDAO.getBankCode(lstrBranch, gStrLocCode);
				if (lstBank.size() > 0) {
					lstrBank = lstBank.get(0).toString();
				}
			}

			String lValidFlag = "bilNtCreated";
			// lValidFlag = lObjPensionBillDAO.getBilStatusFrmBillNo(lstrBank,
			// lstrBranch, lStrScheme,
			// lStrPPoNo,lLocCode,SessionHelper.getPostId(inputMap),lStrDate);

			StringBuilder lStrGrant = new StringBuilder();
			lStrGrant.append(" <BILL> ");
			lStrGrant.append(lValidFlag);
			lStrGrant.append(" </BILL> ");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrGrant.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			sbLog.append("Problem in validateBill is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				objRes.setThrowable(e);
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				objRes.setThrowable(ex);
			}
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;
	}

	/**
	 * Method called to compute monthly report data in case bill has already
	 * been made for this month
	 * 
	 * @param inputMap
	 * @return ResultObject
	 */
	public ResultObject viewMonthlyReport(Map<String, Object> argsMap) {

		Map<String, Object> inputMap = argsMap;
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lstBank = new ArrayList();

		String lStrMonth = null;
		String lStrYear = null;
		String lStrBank = null;

		String lStrBranch = null;
		// String lStrHeadCode = null;
		String lStrScheme = null;
		String lStrDate = null;

		String lStrPPoPara = null;
		String lStrAuditor = null;

		String lStrFlag = null;
		List<Long> lBillNo = new ArrayList<Long>();
		String tempBranch = "";
		StringBuffer sbLog = new StringBuffer();
		new HashMap<String, String>();

		try {
			setSessionInfo(inputMap);
			MonthlyPensionBillDAO lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			new CommonPensionDAOImpl(srvcLoc.getSessionFactory());

			lStrMonth = StringUtility.getParameter("cmbForMonth", request).trim();
			lStrYear = StringUtility.getParameter("cmbForYear", request).trim();
			lStrBank = StringUtility.getParameter("cmbBank", request).trim();
			lStrBranch = StringUtility.getParameter("cmbBranch", request).trim();
			lStrScheme = StringUtility.getParameter("cmbForScheme", request).trim();

			lStrPPoPara = StringUtility.getParameter("txtPPONo", request).trim();

			lStrAuditor = StringUtility.getParameter("cmbAuditor", request).trim();
			lStrFlag = StringUtility.getParameter("lflag", request).trim();

			new ArrayList();
			List lLstBranch = new ArrayList();
			inputMap.put("Scheme", lStrScheme);
			inputMap.put("BranchCode", lStrBranch);
			inputMap.put("PPoNoPara", lStrPPoPara);
			inputMap.put("lAuditor", lStrAuditor);

			if (Integer.parseInt(lStrMonth) < 10) {
				lStrDate = lStrYear + "0" + lStrMonth;
			} else {
				lStrDate = lStrYear + lStrMonth;
			}

			if ("B".equalsIgnoreCase(lStrFlag)) {
				if ("".equals(lStrBank) && lStrBank.length() <= 0 && lStrBranch.length() > 0) {
					lstBank = lObjMonthlyDAO.getBankCode(lStrBranch, gStrLocCode);
					if (lstBank != null && !lstBank.isEmpty()) {
						Object[] lObjArr = (Object[]) lstBank.get(0);
						lStrBank = ((Long) lObjArr[0]).toString();
					} else {
						lStrBank = "";

					}
				}

				lBillNo = lObjMonthlyDAO.getBillNo(lStrDate, lStrBranch, lStrScheme, lStrPPoPara, gStrLocCode, lStrAuditor);
				inputMap.put("BillNo", lBillNo);

				resObj = viewMonthlyBill(inputMap);
				inputMap = (Map<String, Object>) resObj.getResultValue();
				resObj.setResultValue(inputMap);
				resObj.setViewName("printMonthlyBill");
			} else {
				if (!"-1".equalsIgnoreCase(lStrAuditor)) {
					if (("-1".equalsIgnoreCase(lStrBranch) || "".equalsIgnoreCase(lStrBranch)) && "IRLA".equals(lStrScheme)) {
						lLstBranch = lObjMonthlyDAO.getAuditorBilledBranches(lStrDate, Long.parseLong(lStrAuditor), lStrBank, gStrLocCode, lStrScheme);
						lLstBranch.iterator();
						for (Object lObj : lLstBranch) {
							Object[] lArrObj = (Object[]) lObj;
							lStrBranch = (String) lArrObj[1];

							if (!lStrBranch.equals(tempBranch) && !"".equals(tempBranch)) {
								inputMap.put("BillNo", lBillNo);
								inputMap.put("brnchCode", tempBranch);
								resObj = viewMonthlyBill(inputMap);
								inputMap = (Map<String, Object>) resObj.getResultValue();
								lBillNo.clear();
							}

							lBillNo.add(new Long(lArrObj[0].toString()));

							tempBranch = lStrBranch;
						}
						inputMap.put("BillNo", lBillNo);
						inputMap.put("brnchCode", lStrBranch);
						resObj = viewMonthlyBill(inputMap);
						inputMap = (Map<String, Object>) resObj.getResultValue();
					} else {
						lBillNo = lObjMonthlyDAO.getBillNo(lStrDate, lStrBranch, lStrScheme, lStrPPoPara, gStrLocCode, lStrAuditor);
						inputMap.put("BillNo", lBillNo);
						inputMap.put("brnchCode", lStrBranch);
						resObj = viewMonthlyBill(inputMap);
						inputMap = (Map<String, Object>) resObj.getResultValue();
					}
				}
			}
			resObj.setResultValue(inputMap);
			resObj.setViewName("printMonthlyBill");

		} catch (Exception e) {
			sbLog.append("Problem in viewMonthlyReport is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				resObj.setThrowable(e);
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				resObj.setThrowable(ex);
			}
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	// this method for distribute case
	public ResultObject getDetailsOfBank(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrBCBankcode = null;
		String lStrBCBankName = null;
		String lStrBCBranchcode = null;
		String lStrBCBranchName = null;
		try {
			setSessionInfo(inputMap);
			StringBuilder lStrRes = new StringBuilder();
			String bankCodeFrmRequest = StringUtility.getParameter("txtBranchCode", request).trim();
			List arrylstBranch = new ArrayList();
			MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			CommonPensionDAOImpl lObjCmnPnsnDao = new CommonPensionDAOImpl(serv.getSessionFactory());
			if (bankCodeFrmRequest.length() > 0) {
				// getting bank branch corresponding to the entered branchcode
				List arrylstBranch1 = lObjMonthlyDAO.getBranchDetailsByBranchId(bankCodeFrmRequest, gStrLocCode, gLngPostId, gLngUserId, SessionHelper.getLangId(inputMap));
				arrylstBranch = lObjCmnPnsnDao.getAllBank(bankCodeFrmRequest, SessionHelper.getLangId(inputMap), gStrLocCode);
				Object[] tuple;
				if (arrylstBranch1 != null && !arrylstBranch1.isEmpty()) {
					lStrRes.append("<XMLDOCAUDDETAILS>");
					for (int i = 0; i < arrylstBranch1.size(); i++) {
						lStrRes.append("<AUDDTLS" + i + ">" + arrylstBranch1.get(i));
						lStrRes.append("</AUDDTLS" + i + ">");
					}
					lStrRes.append("</XMLDOCAUDDETAILS>");
				}

				if (arrylstBranch != null && !arrylstBranch.isEmpty()) {
					tuple = (Object[]) arrylstBranch.get(0);
					if (tuple[0] != null) {
						lStrBCBankcode = tuple[0].toString();
					}
					if (tuple[1] != null) {
						lStrBCBankName = tuple[1].toString();
					}
					if (tuple[2] != null) {
						lStrBCBranchcode = tuple[2].toString();
					}
					if (tuple[3] != null) {
						lStrBCBranchName = tuple[3].toString();
					}
					lStrRes.append("<XMLDOCBANKDETAILS>");
					lStrRes.append("<BANKCODE>" + lStrBCBankcode);
					lStrRes.append("</BANKCODE>");
					lStrRes.append("<BANKNAME>" + lStrBCBankName);
					lStrRes.append("</BANKNAME>");
					lStrRes.append("<BRANCHCODE>" + lStrBCBranchcode);
					lStrRes.append("</BRANCHCODE>");
					lStrRes.append("<BRANCHNAME>" + lStrBCBranchName);
					lStrRes.append("</BRANCHNAME>");
					lStrRes.append("</XMLDOCBANKDETAILS>");
				}
			}

			/*
			 * if (arrylstBranch != null && !arrylstBranch.isEmpty()) { if
			 * (arrylstBranch.get(4) != null) { lStrAuditorName =
			 * arrylstBranch.get(4).toString(); } if (arrylstBranch.get(5) !=
			 * null) { lStrPostId = arrylstBranch.get(5).toString(); }
			 * lStrRes.append("<AUDITORNAME>" + lStrAuditorName);
			 * lStrRes.append("</AUDITORNAME>"); lStrRes.append("<POSTID>" +
			 * lStrPostId); lStrRes.append("</POSTID>"); }
			 */

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrRes.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/**
	 * Method called using ajax to get bank and branch for the branchcode(name
	 * Bank code on jsp) this method also creates the valid headcodelist for
	 * that bank-branch
	 * 
	 * @param inputMap
	 * @return ResultObject
	 */
	public ResultObject getBranchFromBranchCode(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrBCBankcode = null;
		String lStrBCBankName = null;
		String lStrBCBranchcode = null;
		String lStrBCBranchName = null;
		try {
			setSessionInfo(inputMap);
			StringBuilder lStrRes = new StringBuilder();

			String bankCodeFrmRequest = StringUtility.getParameter("txtbranchCode", request).trim();
			String lFlag = StringUtility.getParameter("lFlag", request);
			if (!"".equals(StringUtility.getParameter("lAuditor", request))) {
				gLngPostId = Long.valueOf(StringUtility.getParameter("lAuditor", request));
			}
			if (!"".equals(lFlag)) {
				bankCodeFrmRequest = bankCodeFrmRequest + "~" + lFlag;
			}

			List arrylstBranch = new ArrayList();

			MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			if (bankCodeFrmRequest.length() > 0) {
				arrylstBranch = lObjMonthlyDAO.getBranchByBranchId(bankCodeFrmRequest, gStrLocCode, gLngPostId);
			}

			lStrRes.append("<XMLDOC>");
			if (arrylstBranch != null && !arrylstBranch.isEmpty()) {
				if (arrylstBranch.get(0) != null) {
					lStrBCBankcode = arrylstBranch.get(0).toString();
				}
				if (arrylstBranch.get(1) != null) {
					lStrBCBankName = arrylstBranch.get(1).toString();
				}
				if (arrylstBranch.get(2) != null) {
					lStrBCBranchcode = arrylstBranch.get(2).toString();
				}
				if (arrylstBranch.get(3) != null) {
					lStrBCBranchName = arrylstBranch.get(3).toString();
				}

				lStrRes.append("<BANKCODE>" + lStrBCBankcode);
				lStrRes.append("</BANKCODE>");

				lStrRes.append("<BANKNAME>" + lStrBCBankName);
				lStrRes.append("</BANKNAME>");

				lStrRes.append("<BRANCHCODE>" + lStrBCBranchcode);
				lStrRes.append("</BRANCHCODE>");

				lStrRes.append("<BRANCHNAME>" + lStrBCBranchName);
				lStrRes.append("</BRANCHNAME>");
			}
			lStrRes.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrRes.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/**
	 * Method called using ajax to get valid headcodelist for the branch
	 * selected
	 * 
	 * @param inputMap
	 * @return ResultObject
	 */
	public ResultObject getHeadcodeList(Map<String, Object> inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			setSessionInfo(inputMap);
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			String lstrBranch = StringUtility.getParameter("AuditorBranchCode", request).trim();
			MonthlyPensionBillDAOImpl lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			List lLstHeadCodeList = lObjPensionBillDAO.getHeadCodeListFromBranchCode(lstrBranch, gStrLocCode);
			ArrayList<ComboValuesVO> arrHeadCode = new ArrayList<ComboValuesVO>();
			ComboValuesVO vo = new ComboValuesVO();

			vo.setId("-1");
			vo.setDesc("--Select--");
			arrHeadCode.add(vo);

			if (lLstHeadCodeList != null && !lLstHeadCodeList.isEmpty()) {
				Iterator it = lLstHeadCodeList.iterator();
				while (it.hasNext()) {
					vo = new ComboValuesVO();
					vo = (ComboValuesVO) it.next();
					arrHeadCode.add(vo);
				}
			}
			String lStrAjaxResult = new AjaxXmlBuilder().addItems(arrHeadCode, "desc", "id").toString();
			inputMap.put("ajaxKey", lStrAjaxResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return objRes;
	}

	/**
	 * Method for the pop up opened on click of other arrear link of a pensioner
	 * depicting his prev month arrear
	 * 
	 * @param inputMap
	 * @return ResultObject
	 */
	public ResultObject getPrvMonthArrDtls(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		StringBuffer sbLog = new StringBuffer();
		String lStrPnsnrCode = null;
		List lLstMonthlyList = null;
		List<MonthlyPensionBillVO> lResultLst = null;
		HttpSession session = request.getSession();
		try {
			setSessionInfo(inputMap);
			CommonPensionDAOImpl lObjCommonPensionDAOImpl = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
			lStrPnsnrCode = request.getParameter("pnsnerCode");

			lLstMonthlyList = (List) session.getAttribute("PrvMonthlyArrearLst");
			for (int i = 0; i < lLstMonthlyList.size(); i += 2) {
				if ((lLstMonthlyList.get(i)).equals(lStrPnsnrCode)) {
					lResultLst = (List<MonthlyPensionBillVO>) lLstMonthlyList.get(i + 1);
				}
			}

			inputMap.put("LangId", gLngLangId);

			List lMonthList = lObjCommonPensionDAOImpl.getMonthDtls(gLngLangId);

			inputMap.put("MonthList", lMonthList);
			inputMap.put("PensionerCode", lStrPnsnrCode);
			inputMap.put("MonthlyPensionList", lResultLst);

			resObj.setResultValue(inputMap);
			resObj.setViewName("OtherArrearPopup");
		} catch (Exception e) {
			sbLog.append("Problem in viewMonthlyReport is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				resObj.setThrowable(e);
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				resObj.setThrowable(ex);
			}
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getPrvMonthArrDtlsAfterBillSave(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		StringBuffer sbLog = new StringBuffer();
		String lStrPnsnrCode = null;
		Long lBillDtlId = null;
		Long lBillHdrId = null;
		List<MonthlyPensionBillVO> lResultLst = null;

		try {
			setSessionInfo(inputMap);
			lStrPnsnrCode = request.getParameter("pnsnerCode");
			lBillDtlId = Long.valueOf(StringUtility.getParameter("billDtlId", request));
			lBillHdrId = Long.valueOf(StringUtility.getParameter("billHdrId", request));

			MonthlyPensionBillDAOImpl lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());

			lResultLst = lObjPensionBillDAO.getPrvMonthArrDtls(lStrPnsnrCode, lBillHdrId, lBillDtlId);

			inputMap.put("LangId", gLngLangId);
			ResultObject lResultObj = srvcLoc.executeService("GET_MONTH_DTLS", inputMap);
			Map lMap = (Map) lResultObj.getResultValue();
			Map lResultMap = (Map) lMap.get("ResultMap");
			List lMonthList = (List) lResultMap.get("MonthList");

			inputMap.put("MonthList", lMonthList);
			inputMap.put("PensionerCode", lStrPnsnrCode);
			inputMap.put("MonthlyPensionList", lResultLst);

			resObj.setResultValue(inputMap);
			resObj.setViewName("OtherArrearPopup");
		} catch (Exception e) {
			sbLog.append("Problem in viewMonthlyReport is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				resObj.setThrowable(e);
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				resObj.setThrowable(ex);
			}
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	// Under Construction

	/*
	 * public ResultObject getMonthlyVariation(Map<String, Object> inputMap) {
	 * 
	 * ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
	 * ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	 * 
	 * String lStrPnsnrCode = null; Long lLastPaidMonth = null; Date lstPaidMnth
	 * = null; String lStrChkPnsnrCode = null; MonthlyPensionBillVO
	 * lObjCurrMonthlyVO = null; MonthlyPensionBillVO lObjPrevMonthlyVO = null;
	 * ValidPcodeView lObjValidPcodeViewVO = null; TrnPensionBillDtls
	 * lObjBillDtls = null; List<TrnPensionBillDtls> lLstBillDtls = null; List
	 * lLstClosedPnsn = null;
	 * 
	 * List<MonthlyPensionBillVO> lLstChanged = new
	 * ArrayList<MonthlyPensionBillVO>(); List<MonthlyPensionBillVO> lLstNew =
	 * new ArrayList<MonthlyPensionBillVO>(); List<MonthlyPensionBillVO>
	 * lLstClosed = new ArrayList<MonthlyPensionBillVO>();
	 * List<MonthlyPensionBillVO> lLstPrevious = new
	 * ArrayList<MonthlyPensionBillVO>(); List<MonthlyPensionBillVO> lLstCurrent
	 * = new ArrayList<MonthlyPensionBillVO>();
	 * 
	 * MonthlyPensionBillDAOImpl lObjMonthlyDAO = new
	 * MonthlyPensionBillDAOImpl(MstPensionerHdr.class,
	 * srvcLoc.getSessionFactory());
	 * 
	 * try { try { resObj = getMonthlyPension(inputMap); inputMap = (Map<String,
	 * Object>) resObj.getResultValue(); } catch(Exception x) {
	 * gLogger.error("Error in getmonthly call is: " + x, x); }
	 * 
	 * lLstCurrent = (List<MonthlyPensionBillVO>)
	 * inputMap.get("MonthlyPensionList"); List lLstPnsnrCodeVO = (List)
	 * inputMap.get("lLstPnsnrCode"); String lStrBrnchCode =
	 * inputMap.get("Branch").toString();
	 * 
	 * CommonPensionDAO lObjCommonPnsnDAO = new
	 * CommonPensionDAOImpl(srvcLoc.getSessionFactory());
	 * 
	 * if (lLstCurrent != null && !lLstCurrent.isEmpty()) { for (int i = 0; i <
	 * lLstCurrent.size(); i++) {
	 * 
	 * lObjCurrMonthlyVO = null; lObjPrevMonthlyVO = null; lObjValidPcodeViewVO
	 * = null;
	 * 
	 * lObjCurrMonthlyVO = lLstCurrent.get(i); lStrPnsnrCode =
	 * lObjCurrMonthlyVO.getPensionerCode(); for (int j = 0; j <
	 * lLstPnsnrCodeVO.size(); j++) {
	 * 
	 * lObjValidPcodeViewVO = (ValidPcodeView) lLstPnsnrCodeVO.get(j);
	 * lStrChkPnsnrCode = lObjValidPcodeViewVO.getPensionerCode(); if
	 * (lStrPnsnrCode.equals(lStrChkPnsnrCode)) {
	 * 
	 * // this record corresponds to the same pensioner lstPaidMnth =
	 * lObjValidPcodeViewVO.getLastPaidDate(); if (lstPaidMnth != null) {
	 * 
	 * lLastPaidMonth = Long.valueOf(new
	 * SimpleDateFormat("yyyyMM").format(lstPaidMnth)); } else { lLastPaidMonth
	 * = Long.valueOf(lObjCommonPnsnDAO.getLastBillCreatedMonth(lStrPnsnrCode));
	 * // lstPaidMnth = new // SimpleDateFormat("dd/MM/yyyy").parse(new //
	 * SimpleDateFormat("yyyyMM").format(lstPaidMnth)+"01"); lstPaidMnth = new
	 * SimpleDateFormat("dd/MM/yyyy").parse(("01/").concat((new
	 * SimpleDateFormat("MM/yyyy").format(lstPaidMnth)))); }
	 * 
	 * } } // now we have got the last paid month for this pensioner so we get
	 * the record of last paid for this pensioner lLstBillDtls =
	 * lObjMonthlyDAO.getBillDtlsForMonth(lStrPnsnrCode, lLastPaidMonth); //
	 * there can be list bcoz in case of expired pensioners with more than 1
	 * family pensioners we have more than 1 record
	 * 
	 * if (lLstBillDtls != null && !lLstBillDtls.isEmpty()) {
	 * 
	 * if(lLstBillDtls.size() > 1) { //there are more than 1 record for this
	 * pensioner..hence get the record corresponding to the party name for
	 * comparison for(int k=0; k<lLstBillDtls.size(); k++) { lObjBillDtls =
	 * lLstBillDtls.get(k);
	 * if(lObjCurrMonthlyVO.getPensionerName().equals(lObjBillDtls
	 * .getPensionerName())) {
	 * 
	 * // the records match // lObjPrevMonthlyVO =
	 * getBillToMonthly(lObjBillDtls); // } // }
	 * 
	 * if(lObjPrevMonthlyVO == null) { //pensioner names dont match with
	 * previous record/(s) //need to handle death case where pensioner name will
	 * never match for first payment after death //also corresponding to 1 old
	 * record there are 2 new records...so how are they to be shown }
	 * 
	 * 
	 * } else {
	 * 
	 * lObjBillDtls = lLstBillDtls.get(0); lObjPrevMonthlyVO =
	 * getBillToMonthly(lObjBillDtls); // } // }
	 * 
	 * if (lObjPrevMonthlyVO != null) { // now we have got both the records for
	 * current month and previous month so compare and see if there is any
	 * difference if (lObjPrevMonthlyVO.getBasicPensionAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getBasicPensionAmount().doubleValue()) { // basic
	 * amount has changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO);
	 * 
	 * } else if (lObjPrevMonthlyVO.getCvpMonthlyAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getCvpMonthlyAmount().doubleValue()) { // cvp monthly
	 * amount has changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getDpPercentAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getDpPercentAmount().doubleValue()) { // dp percent
	 * amount has changed for the pensionerm hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getIr().doubleValue() !=
	 * lObjCurrMonthlyVO.getIr().doubleValue()) { // ir amount has changed for
	 * the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getItCutAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getItCutAmount().doubleValue()) { // it cut amount has
	 * changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getMedicalAllowenceAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getMedicalAllowenceAmount().doubleValue()) {
	 * 
	 * // ma amount has changed for the pensioner // hence add it to changed
	 * list lLstChanged.add(lObjPrevMonthlyVO);
	 * lLstChanged.add(lObjCurrMonthlyVO); }
	 * 
	 * else
	 * if(!lObjPrevMonthlyVO.getMOComm().equals(lObjCurrMonthlyVO.getMOComm()))
	 * { //mo comm amount has changed for the pensioner hence add it to changed
	 * list lLstChanged.add(lObjPrevMonthlyVO);
	 * lLstChanged.add(lObjCurrMonthlyVO); }
	 * 
	 * else if (lObjPrevMonthlyVO.getNetPensionAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getNetPensionAmount().doubleValue()) { // net pension
	 * amount has changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * 
	 * else if(!lObjPrevMonthlyVO.getOMR().equals(lObjCurrMonthlyVO.getOMR())) {
	 * //omr amount has changed for the pensioner //hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * 
	 * else if (lObjPrevMonthlyVO.getOtherArrearsAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getOtherArrearsAmount().doubleValue()) { // other
	 * arrears amount has changed for thepensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getOtherBenefit().doubleValue() !=
	 * lObjCurrMonthlyVO.getOtherBenefit().doubleValue()) { // other benefits
	 * amount has changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getPensionCutAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getPensionCutAmount().doubleValue()) { // pension cut
	 * amount has changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getPersonalPension().doubleValue() !=
	 * lObjCurrMonthlyVO.getPersonalPension().doubleValue())
	 * 
	 * // personal pension amount has changed for the pensioner hence add it to
	 * changed list lLstChanged.add(lObjPrevMonthlyVO);
	 * lLstChanged.add(lObjCurrMonthlyVO); } else if
	 * (lObjPrevMonthlyVO.getRecoveryAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getRecoveryAmount().doubleValue()) { // recovery amount
	 * has changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getSpecialCutAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getSpecialCutAmount().doubleValue()) { // special cut
	 * amount has changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getTIArrearsAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getTIArrearsAmount().doubleValue()) { // ti arrear
	 * amount has changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * else if (lObjPrevMonthlyVO.getTiPercentAmount().doubleValue() !=
	 * lObjCurrMonthlyVO.getTiPercentAmount().doubleValue()) { // ti percent
	 * amount has changed for the pensioner hence add it to changed list
	 * lLstChanged.add(lObjPrevMonthlyVO); lLstChanged.add(lObjCurrMonthlyVO); }
	 * } else { // this is a record for a new pensioner...who's pension has
	 * started from this month itself // hence needs to be added in list of new
	 * pensioners lLstNew.add(lObjCurrMonthlyVO); }
	 * 
	 * } }
	 * 
	 * if (lLstCurrent != null && !lLstCurrent.isEmpty()) { lLstClosedPnsn =
	 * lObjMonthlyDAO.getMonthlyClosed(lLastPaidMonth, lStrBrnchCode,
	 * lstPaidMnth); } if (lLstClosedPnsn != null) { for (int l = 0; l <
	 * lLstClosedPnsn.size(); l++) {
	 * 
	 * lLstBillDtls =
	 * lObjMonthlyDAO.getBillDtlsForMonth(lLstClosedPnsn.get(l).toString(),
	 * lLastPaidMonth); lObjBillDtls = lLstBillDtls.get(0);
	 * lLstClosed.add(getBillToMonthly(lObjBillDtls)); } } // we have 2 lists
	 * for changed records and new records we need to prepare the printstring
	 * for these String printString = getPrintForMonthlyVariation(lLstChanged,
	 * lLstNew, lLstClosed, inputMap);
	 * 
	 * inputMap.put("PrintString", printString); inputMap.put("DisplayString",
	 * printString.replace("</pre></div>", "").replace("<div><pre>", ""));
	 * 
	 * inputMap.put("MonthlyPrint", "Y"); resObj.setResultValue(inputMap);
	 * resObj.setViewName("monthlyVariationRpt"); } catch(Exception e) {
	 * gLogger.error("Error is :" + e, e); resObj.setResultValue(null);
	 * resObj.setThrowable(e); resObj.setResultCode(ErrorConstants.ERROR);
	 * resObj.setViewName("errorPage"); } return resObj; }
	 */

	private MonthlyPensionBillVO getBillToMonthly(TrnPensionBillDtls lObjTrnPensionBillDtls) throws Exception {

		MonthlyPensionBillVO lObjMonthlyPensionVO = null;
		try {
			lObjMonthlyPensionVO = new MonthlyPensionBillVO();
			lObjMonthlyPensionVO.setPpoNo(lObjTrnPensionBillDtls.getPpoNo().toString());
			lObjMonthlyPensionVO.setPensionerName(lObjTrnPensionBillDtls.getPensionerName());
			lObjMonthlyPensionVO.setAccountNo(lObjTrnPensionBillDtls.getAccountNo());
			lObjMonthlyPensionVO.setAllnBf11156(getInitAmt(lObjTrnPensionBillDtls.getAllcationBf11156()));
			lObjMonthlyPensionVO.setAllnAf11156(getInitAmt(lObjTrnPensionBillDtls.getAllcationAf11156()));
			lObjMonthlyPensionVO.setAllnAf10560(getInitAmt(lObjTrnPensionBillDtls.getAllcationAf10560()));
			lObjMonthlyPensionVO.setBasicPensionAmount(getInitAmt(lObjTrnPensionBillDtls.getPensionAmount()));
			lObjMonthlyPensionVO.setDpPercentAmount(getInitAmt(lObjTrnPensionBillDtls.getDpAmount()));
			lObjMonthlyPensionVO.setTiPercentAmount(getInitAmt(lObjTrnPensionBillDtls.getTiAmount()));
			lObjMonthlyPensionVO.setMedicalAllowenceAmount(getInitAmt(lObjTrnPensionBillDtls.getMedicalAllowenceAmount()));
			lObjMonthlyPensionVO.setCvpMonthlyAmount(getInitAmt(lObjTrnPensionBillDtls.getCvpMonthAmount()));
			lObjMonthlyPensionVO.setTIArrearsAmount(getInitAmt(lObjTrnPensionBillDtls.getTiArrearAmount()));
			lObjMonthlyPensionVO.setOtherArrearsAmount(getInitAmt(lObjTrnPensionBillDtls.getArrearAmount()));
			lObjMonthlyPensionVO.setItCutAmount(getInitAmt(lObjTrnPensionBillDtls.getIncomeTaxCutAmount()));
			lObjMonthlyPensionVO.setSpecialCutAmount(getInitAmt(lObjTrnPensionBillDtls.getSpecialCutAmount()));
			lObjMonthlyPensionVO.setOtherBenefit(getInitAmt(lObjTrnPensionBillDtls.getOtherBenefits()));
			lObjMonthlyPensionVO.setOMR(getInitAmt(lObjTrnPensionBillDtls.getOmr()));
			lObjMonthlyPensionVO.setPensionCutAmount(getInitAmt(lObjTrnPensionBillDtls.getPensnCutAmount()));
			lObjMonthlyPensionVO.setRecoveryAmount(getInitAmt(lObjTrnPensionBillDtls.getRecoveryAmount()));
			lObjMonthlyPensionVO.setNetPensionAmount(getInitAmt(lObjTrnPensionBillDtls.getReducedPension()));
			lObjMonthlyPensionVO.setPersonalPension(getInitAmt(lObjTrnPensionBillDtls.getPersonalPensionAmount()));
			lObjMonthlyPensionVO.setIr(getInitAmt(lObjTrnPensionBillDtls.getIrAmount()));
			lObjMonthlyPensionVO.setMOComm(getInitAmt(lObjTrnPensionBillDtls.getMoCommission()));
			lObjMonthlyPensionVO.setForMonth(lObjTrnPensionBillDtls.getPayForMonth());
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw (e);
		}
		return lObjMonthlyPensionVO;
	}

	// Under construction

	/*
	 * private String getPrintForMonthlyVariation(List<MonthlyPensionBillVO>
	 * lLstChanged, List<MonthlyPensionBillVO> lLstNew,
	 * List<MonthlyPensionBillVO> lLstClosed, Map inputMap) throws Exception {
	 * 
	 * StringBuffer lSbrPrintString = new StringBuffer(); try { int pageNum = 0;
	 * int srNo = 1; MonthlyPensionBillVO lObjMonthlyVO = null; char lFlag =
	 * 'V';
	 * 
	 * lSbrPrintString.append("<div><pre>"); if (lLstChanged != null &&
	 * !lLstChanged.isEmpty()) { // there are some records for changed pension
	 * lSbrPrintString.append(getChar((132 - 47) / 2, " "));
	 * lSbrPrintString.append
	 * ("LIST OF PENSIONERS FOR WHOM PENSION HAS CHANGED");
	 * lSbrPrintString.append("\r\n\n"); for (int x = 0; x < lLstChanged.size();
	 * x++) { lObjMonthlyVO = lLstChanged.get(x); if (x % 12 == 0) { if (x != 0)
	 * { lSbrPrintString.append(getAbbrForReport());
	 * lSbrPrintString.append("</pre></div>");
	 * lSbrPrintString.append("<div><pre>"); } pageNum++;
	 * lSbrPrintString.append(getHeadForMnthlyReport(pageNum, inputMap, "N")); }
	 * if (x % 2 == 0) { lSbrPrintString.append(printRecord(lObjMonthlyVO,
	 * srNo++, lFlag)); } if (x % 2 != 0) {
	 * lSbrPrintString.append(printRecord(lObjMonthlyVO, 0, lFlag));
	 * lSbrPrintString.append(getChar(132, "-"));
	 * lSbrPrintString.append("\r\n\n"); } }
	 * lSbrPrintString.append(getAbbrForReport()); } if (lLstChanged != null &&
	 * !lLstChanged.isEmpty() && lLstNew != null && !lLstNew.isEmpty() &&
	 * lLstClosed != null && !lLstClosed.isEmpty()) { // there are both changed
	 * and new records...hence after list of // changed need to add new
	 * pensioners from new page lSbrPrintString.append("</pre></div>");
	 * lSbrPrintString.append("<div><pre>"); } srNo = 1; if (lLstNew != null &&
	 * !lLstNew.isEmpty()) { // there are some records for new pension
	 * lSbrPrintString.append(getChar((132 - 22) / 2, " "));
	 * lSbrPrintString.append("LIST OF NEW PENSIONERS");
	 * lSbrPrintString.append("\r\n\n"); for (int x = 0; x < lLstNew.size();
	 * x++) { lObjMonthlyVO = lLstNew.get(x); if (x % 15 == 0) { if (x != 0) {
	 * lSbrPrintString.append(getAbbrForReport());
	 * lSbrPrintString.append("</pre></div>");
	 * lSbrPrintString.append("<div><pre>"); } pageNum++;
	 * lSbrPrintString.append(getHeadForMnthlyReport(pageNum, inputMap, "N")); }
	 * lSbrPrintString.append(printRecord(lObjMonthlyVO, srNo++, lFlag)); }
	 * lSbrPrintString.append(getAbbrForReport()); } srNo = 1; if (lLstClosed !=
	 * null && !lLstClosed.isEmpty()) { lSbrPrintString.append(getChar((132 -
	 * 22) / 2, " ")); lSbrPrintString.append("LIST OF CLOSED PENSIONERS");
	 * lSbrPrintString.append("\r\n\n"); for (int x = 0; x < lLstClosed.size();
	 * x++) { lObjMonthlyVO = lLstClosed.get(x); if (x % 15 == 0) { if (x != 0)
	 * { lSbrPrintString.append(getAbbrForReport());
	 * lSbrPrintString.append("</pre></div>");
	 * lSbrPrintString.append("<div><pre>"); } pageNum++;
	 * lSbrPrintString.append(getHeadForMnthlyReport(pageNum, inputMap, "N")); }
	 * lSbrPrintString.append(printRecord(lObjMonthlyVO, srNo++, lFlag)); }
	 * lSbrPrintString.append(getAbbrForReport()); }
	 * lSbrPrintString.append("</pre></div>"); } catch(Exception e) {
	 * gLogger.error("Error is: " + e, e); throw (e); } return
	 * lSbrPrintString.toString(); }
	 */

	private void getAuditorWiseBranches(String lStrDate, String lStrBranch, String lStrScheme, Long lAuditor, String lStrBank, String gStrLocCode, Map inputMap, String lStrPPONoPara) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		StringBuffer sbLog = new StringBuffer();

		List lBillNo = new ArrayList<Long>();
		new ArrayList();
		new ArrayList();
		List lLstBranch = new ArrayList();
		MonthlyPensionBillDAO lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
		try {
			if (!"-1".equalsIgnoreCase(lStrBranch) && !"".equalsIgnoreCase(lStrBranch)) {
				lBillNo = lObjMonthlyDAO.getBillNo(lStrDate, lStrBranch, lStrScheme, lStrPPONoPara, gStrLocCode, String.valueOf(lAuditor));
				if (lBillNo.size() == 0 || lBillNo == null) {
					lLstBranch.add(lStrBranch);
				}

			} else if ("IRLA".equals(lStrScheme)) {
				lLstBranch = lObjMonthlyDAO.getAuditorNonBilledBranches(lStrDate, lAuditor, lStrBank, gStrLocCode, lStrScheme);

			}
			inputMap.put("NonBilledBanks", lLstBranch);
		} catch (Exception e) {
			sbLog.append("Problem in getAuditorWiseBranches is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				resObj.setThrowable(e);
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				resObj.setThrowable(ex);
			}
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
	}

	/**
	 * Method to set Session variables
	 * 
	 * @param inputMap
	 */
	private void setSessionInfo(Map<String, Object> inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gDate = DBUtility.getCurrentDateFromDB();
		gDbId = SessionHelper.getDbId(inputMap);
		gStrLocShortName = SessionHelper.getLocationVO(inputMap).getLocShortName();

	}

	public String getStackTrace(Exception x) {

		OutputStream buf = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(buf);
		x.printStackTrace(p);
		return buf.toString();
	}

	/**
	 * Method called using ajax to get Modified PPO List for generating Monthly
	 * Bill Vidhi
	 */

	public ResultObject getModifiedPPOForMonthly(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List<String> lLstBranchCode = new ArrayList<String>();
		List<Object[]> lLstModifiedPPO = new ArrayList<Object[]>();
		Map<String, List<String>> lMapBranchPPO = new HashMap<String, List<String>>();
		List<String> lLstBranchWisePPO = null;
		String lStrBranchCode = null;
		String lStrPPONo = null;
		Set<String> lSetBranchCode = new HashSet<String>();
		StringBuilder lStrRes = new StringBuilder();
		try {
			setSessionInfo(inputMap);

			String[] lArrBranchCode = StringUtility.getParameterValues("branchCode", request);
			for (int i = 0; i < lArrBranchCode.length; i++) {
				lLstBranchCode.add(lArrBranchCode[i].trim());
			}
			MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			lLstModifiedPPO = lObjMonthlyDAO.getModifiedPPOForMonthly(lLstBranchCode, gStrLocCode, gLngPostId);

			for (Object[] lArrObj : lLstModifiedPPO) {
				lStrPPONo = (String) lArrObj[0];
				lStrBranchCode = (String) lArrObj[1];
				if (lStrBranchCode != null && lStrBranchCode.trim().length() > 0) {
					lLstBranchWisePPO = lMapBranchPPO.get(lStrBranchCode);
					if (lLstBranchWisePPO != null) {
						lLstBranchWisePPO.add(lStrPPONo);
					} else {
						lLstBranchWisePPO = new ArrayList<String>();
						lLstBranchWisePPO.add(lStrPPONo);
					}
					lMapBranchPPO.put(lStrBranchCode, lLstBranchWisePPO);
				}
			}
			lSetBranchCode = lMapBranchPPO.keySet();
			lStrRes.append("<XMLDOC>");
			for (String lBranchCode : lSetBranchCode) {
				lLstBranchWisePPO = lMapBranchPPO.get(lBranchCode);
				lStrRes.append("<MODIFIEDPPODTLS>");
				lStrRes.append("<BRANCHCODE>");
				lStrRes.append(lBranchCode);
				lStrRes.append("</BRANCHCODE>");
				lStrRes.append("<PPOList>");
				lStrRes.append(lLstBranchWisePPO);
				lStrRes.append("</PPOList>");
				lStrRes.append("</MODIFIEDPPODTLS>");
			}
			lStrRes.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrRes.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			lStrRes.append("<ERROR>");
			lStrRes.append("Some problem occurred during generation of change statement.");
			lStrRes.append("</ERROR>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrRes.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		}
		return resObj;
	}

	/*
	 * Method for getting Variation Report for Monthly Vidhi
	 */
	public ResultObject getVariationReportForMonthly(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		List<MonthlyPensionBillVO> lCurrMonthlyPensionVOLst = null;
		List<MonthlyPensionBillVO> lPrevMonthlyPensionVOLst = null;
		HttpSession session = request.getSession();
		List<MonthlyPensionBillVO> lCommonPensioner = new ArrayList<MonthlyPensionBillVO>();
		List<MonthlyPensionBillVO> lNewPensioner = null;
		List<MonthlyPensionBillVO> lClosedPensioner = null;
		List<String> lCommonPensionerPCode = new ArrayList<String>();
		Map<String, String> lMapHeadDesc = new HashMap<String, String>();
		Set<BigDecimal> lSetHeadCode = new HashSet<BigDecimal>();
		Boolean lBoolVariation = false;
		String PrintStr = null;
		int lintCurrMonthPnsnr = 0;
		int lintPrevMonthPnsnr = 0;
		BigDecimal lCurrMonthPnsnTotal = BigDecimal.ZERO;
		BigDecimal lPrevMonthPnsnTotal = BigDecimal.ZERO;
		new HashMap<String, String>();

		try {
			setSessionInfo(inputMap);

			MonthlyPensionBillDAOImpl lObjMonthlyPensionBillDAOImpl = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			CommonPensionDAO lObjCommonPnsnDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			String lStrBranch = StringUtility.getParameter("hidBranch", request).trim();
			Integer lIntbillStatus = new Integer(StringUtility.getParameter("billStatus", request).trim());
			Integer lIntForMonth = new Integer(StringUtility.getParameter("hidforMonth", request).trim());
			Integer lIntPrevMonth = lIntForMonth;
			lIntPrevMonth -= ((Integer.parseInt((lIntPrevMonth.toString().substring(4, 6)))) == 01) ? 89 : 1;

			String lChkBB = StringUtility.getParameter("chkBB", request).trim();
			String lChkBA = StringUtility.getParameter("chkBA", request).trim();
			String lChkGA = StringUtility.getParameter("chkGA", request).trim();
			String lChkBasic = StringUtility.getParameter("chkBasic", request).trim();
			String lChkPenCut = StringUtility.getParameter("chkPenCut", request).trim();
			String lChkDA = StringUtility.getParameter("chkDA", request).trim();
			String lChkCVP = StringUtility.getParameter("chkCVP", request).trim();
			String lChkPerPen = StringUtility.getParameter("chkPerPen", request).trim();
			String lChkIR = StringUtility.getParameter("chkIR", request).trim();
			String lChkMA = StringUtility.getParameter("chkMA", request).trim();
			String lChkOthBen = StringUtility.getParameter("chkOthBen", request).trim();
			String lChkTI = StringUtility.getParameter("chkTI", request).trim();
			String lChkATI = StringUtility.getParameter("chkATI", request).trim();
			String lChkArr = StringUtility.getParameter("chkArr", request).trim();
			String lChkOthCut = StringUtility.getParameter("chkOthCut", request).trim();
			String lChkDed = StringUtility.getParameter("chkDed", request).trim();
			String lChkMOCom = StringUtility.getParameter("chkMOCom", request).trim();
			String lChkNetAmt = StringUtility.getParameter("chkNetAmt", request).trim();

			lPrevMonthlyPensionVOLst = lObjMonthlyPensionBillDAOImpl.getPrvMonthlyBillDtls(gStrLocCode, gLngPostId, lStrBranch, lIntPrevMonth);
			if (lIntbillStatus > 0) {
				// Bill is already saved and opened from work list
				lCurrMonthlyPensionVOLst = lObjMonthlyPensionBillDAOImpl.getPrvMonthlyBillDtls(gStrLocCode, gLngPostId, lStrBranch, lIntForMonth);
			} else {
				// At the tiem of bill generation, bill is not saved yet
				lCurrMonthlyPensionVOLst = (List) session.getAttribute("MonthlyPensionList");
				// (Map) session.getAttribute("lMapROPPCode");
			}

			if (lPrevMonthlyPensionVOLst == null || (lPrevMonthlyPensionVOLst != null && lPrevMonthlyPensionVOLst.isEmpty())) {
				PrintStr = new String("There is no pensioner in Monthly Report of Previous Month.");
			} else if (lCurrMonthlyPensionVOLst == null || (lCurrMonthlyPensionVOLst != null && lCurrMonthlyPensionVOLst.isEmpty())) {
				PrintStr = new String("There is no pensioner in Monthly Report of Current Month.");
			} else {
				lintCurrMonthPnsnr = lCurrMonthlyPensionVOLst.size();
				lintPrevMonthPnsnr = lPrevMonthlyPensionVOLst.size();

				for (MonthlyPensionBillVO lObjPrev : lPrevMonthlyPensionVOLst) {
					// lPrevMonthPnsnTotal =
					// lObjPrev.getNetPensionAmount().add(new
					// BigDecimal(lPrevMonthPnsnTotal));
				}

				for (MonthlyPensionBillVO lObjPrev : lCurrMonthlyPensionVOLst) {
					// lCurrMonthPnsnTotal =
					// lObjPrev.getNetPensionAmount().add(lCurrMonthPnsnTotal);
				}

				for (MonthlyPensionBillVO lObjPrev : lPrevMonthlyPensionVOLst) {

					for (MonthlyPensionBillVO lObjCurr : lCurrMonthlyPensionVOLst) {
						if (lObjPrev.getPensionerCode().equals(lObjCurr.getPensionerCode())) {
							lCommonPensionerPCode.add(lObjPrev.getPensionerCode());
							lSetHeadCode.add(lObjPrev.getHeadCode());

							if (!lBoolVariation && lChkBB.equals("Y") && lObjPrev.getAllnBf11156().doubleValue() != lObjCurr.getAllnBf11156().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkBA.equals("Y") && lObjPrev.getAllnAf11156().doubleValue() != lObjCurr.getAllnAf11156().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkGA.equals("Y") && lObjPrev.getAllnAf10560().doubleValue() != lObjCurr.getAllnAf10560().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkBasic.equals("Y") && lObjPrev.getBasicPensionAmount().doubleValue() != lObjCurr.getBasicPensionAmount().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkPenCut.equals("Y") && lObjPrev.getPensionCutAmount().doubleValue() != lObjCurr.getPensionCutAmount().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation
									&& lChkDA.equals("Y")
									&& (lObjPrev.getDpPercentAmount().doubleValue() != lObjCurr.getDpPercentAmount().doubleValue() || lObjPrev.getAdpAmount().doubleValue() != lObjCurr.getAdpAmount()
											.doubleValue())) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkCVP.equals("Y") && lObjPrev.getCvpMonthlyAmount().doubleValue() != lObjCurr.getCvpMonthlyAmount().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkPerPen.equals("Y") && lObjPrev.getPersonalPension().doubleValue() != lObjCurr.getPersonalPension().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkIR.equals("Y") && lObjPrev.getIr().doubleValue() != lObjCurr.getIr().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkMA.equals("Y") && lObjPrev.getMedicalAllowenceAmount().doubleValue() != lObjCurr.getMedicalAllowenceAmount().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkOthBen.equals("Y") && lObjPrev.getOtherBenefit().doubleValue() != lObjCurr.getOtherBenefit().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkTI.equals("Y") && lObjPrev.getTiPercentAmount().doubleValue() != lObjCurr.getTiPercentAmount().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkATI.equals("Y") && lObjPrev.getTIArrearsAmount().doubleValue() != lObjCurr.getTIArrearsAmount().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkArr.equals("Y") && lObjPrev.getOtherArrearsAmount().doubleValue() != lObjCurr.getOtherArrearsAmount().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkOthCut.equals("Y") && lObjPrev.getSpecialCutAmount().doubleValue() != lObjCurr.getSpecialCutAmount().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation
									&& lChkDed.equals("Y")
									&& (lObjPrev.getItCutAmount().doubleValue() + lObjPrev.getRecoveryAmount().doubleValue()) != (lObjCurr.getItCutAmount().doubleValue() + lObjPrev
											.getRecoveryAmount().doubleValue())) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkMOCom.equals("Y") && lObjPrev.getMOComm().doubleValue() != lObjCurr.getMOComm().doubleValue()) {
								lBoolVariation = true;
							}
							if (!lBoolVariation && lChkNetAmt.equals("Y") && lObjPrev.getNetPensionAmount().doubleValue() != lObjCurr.getNetPensionAmount().doubleValue()) {
								lBoolVariation = true;
							}

							if (lBoolVariation) {
								lCommonPensioner.add(lObjPrev);
								lCommonPensioner.add(lObjCurr);
								lBoolVariation = false;
							}
							break;
						}

					}

				}

				lClosedPensioner = new ArrayList<MonthlyPensionBillVO>(lPrevMonthlyPensionVOLst);
				for (MonthlyPensionBillVO lObjPrev : lPrevMonthlyPensionVOLst) {
					lSetHeadCode.add(lObjPrev.getHeadCode());
					for (String lStrPCode : lCommonPensionerPCode) {
						if (lObjPrev.getPensionerCode().equals(lStrPCode)) {
							lClosedPensioner.remove(lObjPrev);
							break;
						}
					}
				}

				lNewPensioner = new ArrayList<MonthlyPensionBillVO>(lCurrMonthlyPensionVOLst);
				for (MonthlyPensionBillVO lObjCurr : lCurrMonthlyPensionVOLst) {
					lSetHeadCode.add(lObjCurr.getHeadCode());
					for (String lStrPCode : lCommonPensionerPCode) {
						if (lObjCurr.getPensionerCode().equals(lStrPCode)) {
							lNewPensioner.remove(lObjCurr);
							break;
						}
					}
				}

				Integer month = lIntForMonth % 100;
				Integer year = lIntForMonth / 100;
				String lStrMonth = month.toString();
				String lStrYear = year.toString();
				List lLstMonthDtls = lObjMonthlyPensionBillDAOImpl.getMonthDtls(lStrMonth, gLngLangId);
				inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
				inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
				inputMap.put("ForYear", lStrYear);
				inputMap.put("ForBillYear", lStrYear);
				inputMap.put("Scheme", "IRLA");
				inputMap.put("PrevMonTotalPnsnr", lintPrevMonthPnsnr);
				inputMap.put("CurrMonTatalPnsnr", lintCurrMonthPnsnr);
				inputMap.put("PrevMonTotalPnsnrAmt", lPrevMonthPnsnTotal);
				inputMap.put("CurrMonTotalPnsnrAmt", lCurrMonthPnsnTotal);

				lMapHeadDesc = lObjCommonPnsnDAO.getHeadCodeDesc(lSetHeadCode.toArray(), gLngLangId);
				inputMap.put("HeadDesc", lMapHeadDesc);
				inputMap.put("MonthlyPensionList", lCommonPensioner);
				inputMap.put("brnchCode", lStrBranch);
				PrintStr = getPrintForMonthlyVariation(lCommonPensioner, lNewPensioner, lClosedPensioner, inputMap);
			}

			inputMap.put("DisplayString", PrintStr);
			objRes.setResultValue(inputMap);
			objRes.setViewName("openVariation");

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	public ResultObject printMonthlyVariation(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		StringBuffer sbLine = new StringBuffer();
		new ArrayList();
		int pgCoutner = 1;
		Map lLocDtls = new HashMap();
		StringBuffer sbLog = new StringBuffer();
		new RuleBasedNumberFormat(IReportConstants.INDIAN_ENG_RULE_SET);

		try {
			inputMap.put("VariationFlag", "Y");
			if (inputMap.containsKey("PrintVariationString")) {
				sbLine = sbLine.append(inputMap.get("PrintVariationString").toString());
			}
			setSessionInfo(inputMap);
			sbLine.append(getChar(132, " "));
			sbLine.append("\r\n");
			sbLine.append(getChar(132, " "));
			sbLine.append("\r\n");
			List MonthlyPensionList = (List) inputMap.get("MonthlyPensionList");

			MonthlyPensionBillVO MonthlyPensionVo = null;
			String BranchCode = null;

			MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());

			BranchCode = inputMap.get("brnchCode").toString();
			lLocDtls = lObjMonthlyDAO.getHeaderDtl(BranchCode, gStrLocCode, gLngLangId, null);

			String lStrTrsryName = "";
			Object[] lObjTemp = (Object[]) lLocDtls.get(inputMap.get("brnchCode").toString() + "~");
			lStrTrsryName = lObjTemp[0].toString();
			String lStrBankName = lObjTemp[1].toString();
			String lStrBranchName = lObjTemp[2].toString();
			inputMap.put("BankName", lStrBankName);
			inputMap.put("BranchName", lStrBranchName);

			if (!"".equals(lStrTrsryName)) {
				// lStrTrsryName.split(",")[1];
			}

			inputMap.put("TreasuryName", lStrTrsryName);

			int headcodeCount = 0;
			String lStrHeadCode = null;
			MonthlyPensionBillVO lObjTempVo = new MonthlyPensionBillVO();

			int pageCounter = 1;
			if (MonthlyPensionList != null && MonthlyPensionList.size() > 0) {
				BigDecimal tempHeadCode = new BigDecimal(0);
				int recordCount = 0;
				int stratIndex = 0;
				int srNo = 1;
				Map lMapHeadDesc = (Map) inputMap.get("HeadDesc");

				for (int i = 0; i < MonthlyPensionList.size(); i++) {
					MonthlyPensionVo = (MonthlyPensionBillVO) MonthlyPensionList.get(i);

					if (!tempHeadCode.equals(MonthlyPensionVo.getHeadCode()) || (recordCount - headcodeCount) % 15 == 0) {
						if (!tempHeadCode.equals(MonthlyPensionVo.getHeadCode())) {
							if (!tempHeadCode.equals(new BigDecimal(0))) {
								sbLine.append(getAbbrForReport());
								if ((recordCount - headcodeCount) % 15 >= 11 || (recordCount - headcodeCount) % 15 == 0) {
									sbLine.append("</pre></div>");
									sbLine.append("<div><pre>");
								}
							}
							headcodeCount = i;
							srNo = 1;
						}
						sbLine.append("<div><pre>");

						lStrHeadCode = MonthlyPensionVo.getHeadCode().toString();
						inputMap.put("HeadCode", lStrHeadCode);
						inputMap.put("HeadCodeDesc", lMapHeadDesc.get(lStrHeadCode));

						tempHeadCode = MonthlyPensionVo.getHeadCode();
						sbLine.append(getHeadForMnthlyReport(pageCounter, inputMap, "Y"));

						pageCounter++;
					}
					sbLine.append(printRecord(MonthlyPensionVo, srNo++, 'V', 0D));
					recordCount++;
					if ((recordCount - headcodeCount) % 15 == 0 && i + 1 < MonthlyPensionList.size() && ((MonthlyPensionBillVO) MonthlyPensionList.get(i + 1)).getHeadCode().equals(tempHeadCode)) {
						pgCoutner = pgCoutner + 1;
						stratIndex = stratIndex + 15;
						sbLine.append("</pre></div>");
					} else if (i + 1 < MonthlyPensionList.size()) {
						lObjTempVo = (MonthlyPensionBillVO) MonthlyPensionList.get(i + 1);
						if (!lObjTempVo.getHeadCode().equals(tempHeadCode)) {
							pgCoutner = pgCoutner + 1;
							stratIndex = i + 1;
						}
					} else {
						sbLine.append("<div><pre>");
					}
				}

				sbLine.append(getAbbrForReport());

			}

			inputMap.put("pageCounter", pageCounter);

			MonthlyPensionList = null;
			inputMap.put("PrintVariationString", sbLine.toString());
			inputMap.put("DisplayVariationString", sbLine.toString().replace("</pre></div>", "").replace("<div><pre>", ""));
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			sbLog.append("Problem in printing Variation form-c is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			try {
				new MonthlyLogger(inputMap).writeMonthlyLog(sbLog.toString());
				resObj.setThrowable(e);
			} catch (Exception ex) {
				gLogger.error("Error while writing Monthly log..." + ex, ex);
				resObj.setThrowable(ex);
			}
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private String getPrintForMonthlyVariation(List<MonthlyPensionBillVO> lLstChanged, List<MonthlyPensionBillVO> lLstNew, List<MonthlyPensionBillVO> lLstClosed, Map inputMap) throws Exception {

		StringBuffer lSbrPrintString = new StringBuffer();
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		Map lLocDtls = new HashMap();
		String BranchCode = null;

		try {
			int pageNum = 0;
			int srNo = 1;

			BranchCode = inputMap.get("brnchCode").toString();
			lLocDtls = lObjMonthlyDAO.getHeaderDtl(BranchCode, gStrLocCode, gLngLangId, null);

			String lStrTrsryName = "";
			Object[] lObjTemp = (Object[]) lLocDtls.get(inputMap.get("brnchCode").toString() + "~");
			lStrTrsryName = lObjTemp[0].toString();
			String lStrBankName = lObjTemp[1].toString();
			String lStrBranchName = lObjTemp[2].toString();
			inputMap.put("BankName", lStrBankName);
			inputMap.put("BranchName", lStrBranchName);

			if (!"".equals(lStrTrsryName)) {
				// lStrTrsryName.split(",")[1];
			}

			inputMap.put("TreasuryName", lStrTrsryName);

			MonthlyPensionBillVO lObjMonthlyVO = null;
			lSbrPrintString.append("<div><pre>");

			lSbrPrintString.append("\n");
			lSbrPrintString.append(getChar(132, "-"));
			lSbrPrintString.append("\n");
			lSbrPrintString.append("Total No. Of Pensioners in Previous Month:");
			lSbrPrintString.append(inputMap.get("PrevMonTotalPnsnr").toString());
			lSbrPrintString.append("\r\n");
			lSbrPrintString.append("Total Amount Of Pension paid in Previous Month:");
			lSbrPrintString.append(inputMap.get("PrevMonTotalPnsnrAmt").toString());
			lSbrPrintString.append("\r\n\n");
			lSbrPrintString.append("Total No. Of Pensioners in Current Month:");
			lSbrPrintString.append(inputMap.get("CurrMonTatalPnsnr").toString());
			lSbrPrintString.append("\r\n");
			lSbrPrintString.append("Total Amount Of Pension paid in Current Month:");
			lSbrPrintString.append(inputMap.get("CurrMonTotalPnsnrAmt").toString());
			lSbrPrintString.append("\r\n");
			lSbrPrintString.append(getChar(132, "-"));

			if (lLstChanged != null && !lLstChanged.isEmpty()) {
				// there are some records for changed pension
				lSbrPrintString.append(getChar((132 - 47) / 2, " "));
				lSbrPrintString.append("LIST OF PENSIONERS FOR WHOM PENSION HAS CHANGED");
				lSbrPrintString.append("\r\n\n");
				for (int x = 0; x < lLstChanged.size(); x++) {
					lObjMonthlyVO = lLstChanged.get(x);
					if (x % 12 == 0) {
						if (x != 0) {
							lSbrPrintString.append(getAbbrForReport());
							lSbrPrintString.append("</pre></div>");
							lSbrPrintString.append("<div><pre>");
						}
						pageNum++;
						lSbrPrintString.append(getHeadForMnthlyReport(pageNum, inputMap, "N"));
					}
					if (x % 2 == 0) {
						lSbrPrintString.append(printRecord(lObjMonthlyVO, srNo++, 'V', 0D));
					}
					if (x % 2 != 0) {
						lSbrPrintString.append(printRecord(lObjMonthlyVO, 0, 'V', 0D));
						lSbrPrintString.append(getChar(132, "-"));
						lSbrPrintString.append("\r\n\n");
					}
				}
				lSbrPrintString.append(getAbbrForReport());
			}
			if (lLstChanged != null && !lLstChanged.isEmpty() && lLstNew != null && !lLstNew.isEmpty()) {
				// there are both changed and new records...hence after list of
				// changed need to add new pensioners from new page
				lSbrPrintString.append("</pre></div>");
				lSbrPrintString.append("<div><pre>");
			}
			srNo = 1;
			if (lLstNew != null && !lLstNew.isEmpty()) {
				// there are some records for new pension
				lSbrPrintString.append(getChar((132 - 22) / 2, " "));
				lSbrPrintString.append("LIST OF NEW PENSIONERS");
				lSbrPrintString.append("\r\n\n");
				for (int x = 0; x < lLstNew.size(); x++) {
					lObjMonthlyVO = lLstNew.get(x);
					if (x % 15 == 0) {
						if (x != 0) {
							lSbrPrintString.append(getAbbrForReport());
							lSbrPrintString.append("</pre></div>");
							lSbrPrintString.append("<div><pre>");
						}
						pageNum++;
						lSbrPrintString.append(getHeadForMnthlyReport(pageNum, inputMap, "N"));
					}
					lSbrPrintString.append(printRecord(lObjMonthlyVO, srNo++, 'V', 0D));
				}
				lSbrPrintString.append(getAbbrForReport());
			}

			if (lLstClosed != null && !lLstClosed.isEmpty() && lLstNew != null && !lLstNew.isEmpty()) {
				// there are both changed and new records...hence after list of
				// changed need to add new pensioners from new page
				lSbrPrintString.append("</pre></div>");
				lSbrPrintString.append("<div><pre>");
			}
			srNo = 1;
			if (lLstClosed != null && !lLstClosed.isEmpty()) {
				// there are some records for closed pension
				lSbrPrintString.append(getChar((132 - 22) / 2, " "));
				lSbrPrintString.append("LIST OF CLOSED PENSIONERS");
				lSbrPrintString.append("\r\n\n");
				for (int x = 0; x < lLstClosed.size(); x++) {
					lObjMonthlyVO = lLstClosed.get(x);
					if (x % 15 == 0) {
						if (x != 0) {
							lSbrPrintString.append(getAbbrForReport());
							lSbrPrintString.append("</pre></div>");
							lSbrPrintString.append("<div><pre>");
						}
						pageNum++;
						lSbrPrintString.append(getHeadForMnthlyReport(pageNum, inputMap, "N"));
					}
					lSbrPrintString.append(printRecord(lObjMonthlyVO, srNo++, 'V', 0D));
				}
				lSbrPrintString.append(getAbbrForReport());
			}

			lSbrPrintString.append("</pre></div>");

		} catch (Exception e) {
			gLogger.error("Error is: " + e, e);
			throw (e);
		}
		return lSbrPrintString.toString();
	}

	/*
	 * This method is used to validate change statement when muliple change
	 * statements are allowed for one branch in one month This method will check
	 * change statement of upto which date for selected month-year is generated
	 * and not approved.
	 */
	/*
	 * public ResultObject validateChangeStatement(Map<String, Object> inputMap)
	 * throws Exception {
	 * 
	 * ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	 * StringBuffer sbLog = new StringBuffer(); StringBuilder lSBResp = new
	 * StringBuilder(); String lStrChngStmntStatus = "Approved"; String
	 * lStrUpToDate = ""; SimpleDateFormat lSdf = new
	 * SimpleDateFormat("dd/MM/yyyy"); Date lDtUpto = null; try {
	 * HttpServletRequest request = (HttpServletRequest)
	 * inputMap.get("requestObj"); ServiceLocator serv = (ServiceLocator)
	 * inputMap.get("serviceLocator");
	 * 
	 * String lstrForMonth = StringUtility.getParameter("cmbForMonth",
	 * request).trim(); String lstrForYear =
	 * StringUtility.getParameter("cmbForYear", request).trim(); String
	 * lstrBranch = StringUtility.getParameter("cmbBranch", request).trim();
	 * 
	 * Long lLngPostId = SessionHelper.getPostId(inputMap); String lLocCode =
	 * SessionHelper.getLocationCode(inputMap);
	 * 
	 * String lStrDate = null; MonthlyPensionBillDAOImpl lObjPensionBillDAO =
	 * new MonthlyPensionBillDAOImpl(MstPensionerHdr.class,
	 * serv.getSessionFactory());
	 * 
	 * if (Integer.parseInt(lstrForMonth) < 10) { lStrDate = lstrForYear + "0" +
	 * lstrForMonth; } else { lStrDate = lstrForYear + lstrForMonth; }
	 * 
	 * lDtUpto = lObjPensionBillDAO.getStatusOfChangeStatement(lLocCode,
	 * lstrBranch, Integer.valueOf(lStrDate), lLngPostId); if (lDtUpto != null)
	 * { lStrChngStmntStatus = "BfApproval"; lStrUpToDate =
	 * lSdf.format(lDtUpto); } lSBResp.append("<STATUS>");
	 * lSBResp.append(lStrChngStmntStatus); lSBResp.append("</STATUS>");
	 * lSBResp.append("<UPTODATE>"); lSBResp.append(lStrUpToDate);
	 * lSBResp.append("</UPTODATE>"); // if ("".equals(lstrBank) &&
	 * lstrBank.length() <= 0 && // lstrBranch.length() > 0 &&
	 * "IRLA".equals(lStrScheme)) { // lstBank =
	 * lObjPensionBillDAO.getBankCode(lstrBranch, // gStrLocCode); // if
	 * (lstBank.size() > 0) { // lstrBank = lstBank.get(0).toString(); // } // }
	 * 
	 * // lValidFlag = lObjPensionBillDAO.getBilStatusFrmBillNo(lstrBank, //
	 * lstrBranch, lStrScheme, //
	 * lStrPPoNo,lLocCode,SessionHelper.getPostId(inputMap),lStrDate);
	 * 
	 * String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
	 * lSBResp.toString()).toString(); inputMap.put("ajaxKey", lStrResult);
	 * objRes.setViewName("ajaxData"); objRes.setResultValue(inputMap); } catch
	 * (Exception e) { sbLog.append("Problem in validateChangeStatement is " +
	 * getStackTrace(e) + "location " +
	 * SessionHelper.getLocationCode(inputMap)); objRes.setResultValue(null);
	 * objRes.setResultCode(ErrorConstants.ERROR);
	 * objRes.setViewName("errorPage"); }
	 * 
	 * return objRes; }
	 */

	/*
	 * This method is used to validate change statement when muliple change
	 * statements are allowed for one branch in one month This method will check
	 * change statement of upto which date for selected month-year is generated
	 * and not approved.
	 */
	public ResultObject validateChangeStatement(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		StringBuffer sbLog = new StringBuffer();
		StringBuilder lSBResp = new StringBuilder();
		List<String> lLstBranchCode = new ArrayList<String>();
		List<Object[]> lLstChngStmntDtls = new ArrayList<Object[]>();
		String lStrChngStmntStatus = "NotGenerated";
		String lStrRjctedStatus = bundleConst.getString("CHANGSTMNTSTATUS.REJECTED");
		String lStrDiscardedStatus = bundleConst.getString("CHANGSTMNTSTATUS.DISCARDED");
		String lStrBranchCode = null;
		Long lLngChngStmntGenByPostId = null;
		CommonDAO lObjCommonDAO = null;
		try {
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			String lstrForMonth = StringUtility.getParameter("cmbForMonth", request).trim();
			String lstrForYear = StringUtility.getParameter("cmbForYear", request).trim();
			String[] lArrStrBranch = StringUtility.getParameterValues("branchCode", request);
			for (int i = 0; i < lArrStrBranch.length; i++) {
				lLstBranchCode.add(lArrStrBranch[i].trim());
			}

			Long lLngPostId = SessionHelper.getPostId(inputMap);
			String lLocCode = SessionHelper.getLocationCode(inputMap);

			String lStrDate = null;
			MonthlyPensionBillDAOImpl lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			if (Integer.parseInt(lstrForMonth) < 10) {
				lStrDate = lstrForYear + "0" + lstrForMonth;
			} else {
				lStrDate = lstrForYear + lstrForMonth;
			}

			// ---If status of change statement is null or rejected or discarded
			// then only
			// allowing generation of change statement.
			lLstChngStmntDtls = lObjPensionBillDAO.getStatusOfChangeStatement(lLocCode, lLstBranchCode, Integer.valueOf(lStrDate), lLngPostId);
			for (Object[] lObj : lLstChngStmntDtls) {
				lStrChngStmntStatus = (String) lObj[0];
				lStrBranchCode = (String) lObj[1];
				lLngChngStmntGenByPostId = (Long) lObj[2];
				if (lStrBranchCode != null) {
					if (lStrChngStmntStatus != null && ((!lStrRjctedStatus.equals(lStrChngStmntStatus)) && (!lStrDiscardedStatus.equals(lStrChngStmntStatus)))) {
						// lStrChngStmntStatus = "Generated";
						lSBResp.append("<BRANCHCODE>");
						lSBResp.append(lStrBranchCode);
						lSBResp.append("</BRANCHCODE>");
						lSBResp.append("<AUDITORNAME>");
						lSBResp.append((lLngChngStmntGenByPostId != null) ? lObjCommonDAO.getAuditorNameByPostId(lLngChngStmntGenByPostId) : "");
						lSBResp.append("</AUDITORNAME>");
					}
				}
			}
			// lSBResp.append("<STATUS>");
			// lSBResp.append(lStrChngStmntStatus);
			// lSBResp.append("</STATUS>");
			// if ("".equals(lstrBank) && lstrBank.length() <= 0 &&
			// lstrBranch.length() > 0 && "IRLA".equals(lStrScheme)) {
			// lstBank = lObjPensionBillDAO.getBankCode(lstrBranch,
			// gStrLocCode);
			// if (lstBank.size() > 0) {
			// lstrBank = lstBank.get(0).toString();
			// }
			// }

			// lValidFlag = lObjPensionBillDAO.getBilStatusFrmBillNo(lstrBank,
			// lstrBranch, lStrScheme,
			// lStrPPoNo,lLocCode,SessionHelper.getPostId(inputMap),lStrDate);

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			sbLog.append("Problem in validateChangeStatement is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			gLogger.error("Problem in validateChangeStatement is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			lSBResp.append("<ERROR>");
			lSBResp.append("Some problem occurred during generation of change statement.");
			lSBResp.append("</ERROR>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		}

		return objRes;
	}

	public ResultObject loadMonthlyPensionList(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		MonthlyPensionBillDAO lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		List<Object[]> lLstChngStmntDtl = null;
		String lStrSearchCrt = null;
		String lStrSearchVal = null;
		String lStrSearchBankCode = null;
		String lStrSearchBranchCode = null;
		String lStrSearchYearCode = null;
		String lStrSearchMonthId = null;
		Integer totalRecords = 0;
		String lStrShowWLFor = null;
		CommonPensionDAO lObjCommonPensionDAO = null;

		List lLstAuditorBankCodeList = new ArrayList();
		List lLstAuditorBranchCodeList = null;
		List lLstYear = new ArrayList();
		List lLstMonth = new ArrayList();
		List lLstAuditorList = new ArrayList();
		PensionBillDAO lObjPensionDao = null;
		CommonDAO lObjCommonDao = null;
		String lStrRoleId = bundleConst.getString("AUDITOR.ROLEID");
		String lStrLangId = bundleConst.getString("CMN.LANG");
		try {
			setSessionInfo(inputMap);
			Long lLngPostId = gLngPostId;
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			lStrShowWLFor = StringUtility.getParameter("showWLFor", request).trim();
			lStrSearchCrt = StringUtility.getParameter("hdnSearchCrt", request).trim();
			lStrSearchVal = StringUtility.getParameter("hdnSearchVal", request).trim();
			lStrSearchBankCode = StringUtility.getParameter("hdnBankCode", request).trim();
			lStrSearchBranchCode = StringUtility.getParameter("hdnBranchCode", request).trim();
			lStrSearchYearCode = StringUtility.getParameter("hdnYearCode", request).trim();
			lStrSearchMonthId = StringUtility.getParameter("hdnMonthId", request).trim();
			lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
			lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
			lObjCommonDao = new CommonDAOImpl(srvcLoc.getSessionFactory());
			lLstChngStmntDtl = new ArrayList<Object[]>();
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			if (bundleConst.getString("CHNGSTMNT.SHOWWLFORATO").equals(lStrShowWLFor)) {
				lLngPostId = null;
			}
			totalRecords = lObjMonthlyDAO.getChangeStatementDtlsCount(gStrLocCode, gLngPostId, lStrSearchCrt, lStrSearchVal, lStrSearchBankCode, lStrSearchBranchCode, lStrShowWLFor);
			if (totalRecords > 0) {
				lLstChngStmntDtl = lObjMonthlyDAO.getChangeStatementDtls(gStrLocCode, gLngPostId, lStrSearchCrt, lStrSearchVal, lStrSearchBankCode, lStrSearchBranchCode, displayTag, lStrShowWLFor);
			}
			lLstAuditorBankCodeList = lObjCommonPensionDAO.getAuditorBankCodeList(lLngPostId, gStrLocCode);
			lLstAuditorList = lObjPensionDao.getAuditorsListFromRoleID(Long.valueOf(lStrRoleId), gStrLocCode);
			lLstYear = lObjCommonDao.getYearList(lStrLangId);
			lLstMonth = lObjCommonDao.getMonthList(lStrLangId);
			if (!"".equals(lStrSearchBankCode) && !"-1".equals(lStrSearchBankCode)) {
				if (bundleConst.getString("CHNGSTMNT.SHOWWLFORATO").equals(lStrShowWLFor)) {
					lLstAuditorBranchCodeList = lObjCommonDao.getBranchListFromBankCode(Long.valueOf(lStrSearchBankCode), gStrLocCode, gLngLangId);
				} else {
					lLstAuditorBranchCodeList = lObjCommonPensionDAO.getAuditorBranchCodeList(gLngPostId, lStrSearchBankCode, gStrLocCode);
				}
			}
			inputMap.put("BankList", lLstAuditorBankCodeList);
			inputMap.put("BranchList", lLstAuditorBranchCodeList);
			inputMap.put("ListAuditors", lLstAuditorList);
			inputMap.put("lLstYear", lLstYear);
			inputMap.put("lLstMonth", lLstMonth);
			inputMap.put("showWLFor", lStrShowWLFor);
			inputMap.put("lStrSearchCrt", lStrSearchCrt);
			inputMap.put("lStrSearchVal", lStrSearchVal);
			inputMap.put("lStrSearchCrt", lStrSearchCrt);
			inputMap.put("lStrSearchBankCode", lStrSearchBankCode);
			inputMap.put("lStrSearchBranchCode", lStrSearchBranchCode);
			inputMap.put("lStrSearchYearCode", lStrSearchYearCode);
			inputMap.put("lStrSearchMonthId", lStrSearchMonthId);
			inputMap.put("lStrSearchVal", lStrSearchVal);
			inputMap.put("lLstChngStmntDtls", lLstChngStmntDtl);
			inputMap.put("totalRecords", totalRecords);
			objRes.setResultValue(inputMap);
			objRes.setViewName("GeneratedChangeStatement");
		} catch (Exception e) {
			gLogger.error("Error while getting list of change statement..." + e, e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;

	}

	public ResultObject generateAllBillVO(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");

		TrnMonthlyChangeRqst lObjTrnMonthlyChangeRqstVO = null;
		TrnPensionChangeHdr lObjTrnPensionChangeHdr = null;
		TrnPensionChangeDtls lObjTrnPensionChangeDtls = null;
		TrnBillRegister lObjTrnBillRegisterVO = null;
		TrnPensionBillHdr lObjPensionBillHdr = null;
		TrnPensionBillDtls lObjPensionBillDtlVO = null;
		List<TrnMonthlyChangeRqst> lLstTrnMonthlyChangeRqst = null;
		List<TrnPensionChangeHdr> lLstTrnPensionChangeHdr = null;
		List<TrnPensionChangeDtls> lLstTrnPensionChangeDtls = null;
		List<TrnPensionBillHdr> lLstTrnPensionBillHdr = null;
		List<TrnPensionBillDtls> lLstTrnPensionBillDtls = null;
		Long lLngChangeRqstId = null;
		Long lLngChangeHdrId = null;
		Long lLngBillNo = null;
		Long lLngBillHdrId = null;
		Long lLngBillDtlsId = null;
		Long lLngBillPartyId = null;
		Long lLngBillMvmntId = null;
		ChangeStatementDAO lObjChangeStatementDAO = null;
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		Map<BigDecimal, String> lMpHeadCodeDesc = new HashMap<BigDecimal, String>();
		List<MstPensionHeadcode> lLstMstPensionHeadcode = null;
		BigDecimal lTotalNetAmt = BigDecimal.ZERO;
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Long lLngPkCntBillRegister = null;
		Long lLngPkCntPensionBillHdr = null;
		Long lLngPkCntPensionBillDtls = null;
		Long lLngPkCntRltBillParty = null;
		Long lLngPkCntTrnBillMvmnt = null;
		HibernateTemplate hitStg = new HibernateTemplate(srvcLoc.getSessionFactory());
		List<Long> lLstChangeRqstId = new ArrayList<Long>();
		List<Long> lLstTrnPnsnChngHdrId = null;
		List<Long> lLstTrnPnsnChngDtlsId = null;
		List<Long> lLstAllTrnPnsnChngHdrId = new ArrayList<Long>();
		List<Long> lLstAllTrnPnsnChngDtlsId = new ArrayList<Long>();
		List<TrnPensionChangeHdr> lInnLstTrnPensionChangeHdr = null;
		List<TrnPensionChangeDtls> lInnLstTrnPensionChangeDtls = null;
		Map<Long, List<Long>> lMapChngRqstIdHdrId = new HashMap<Long, List<Long>>();
		Map<Long, List<Long>> lMapChngHdrIdDtlsId = new HashMap<Long, List<Long>>();
		Map<Long, TrnMonthlyChangeRqst> lMapTrnMonthlyChangeRqst = new HashMap<Long, TrnMonthlyChangeRqst>();
		Map<Long, TrnPensionChangeHdr> lMapTrnPensionChangeHdr = new HashMap<Long, TrnPensionChangeHdr>();
		Map<Long, TrnPensionChangeDtls> lMapTrnPensionChangeDtls = new HashMap<Long, TrnPensionChangeDtls>();
		List<TrnBillRegister> lLstTrnBillRegisterVO = null;
		List<RltBillParty> lLstRltBillParty = null;
		List<TrnBillMvmnt> lLstTrnBillMvmnt = null;
		Map<Long, Object[]> lMapChngRqstIdParyDtls = new HashMap<Long, Object[]>();
		RltBillParty lObjBillParty = null;
		TrnBillMvmnt lObjTrnBillMvmnt = null;
		Object[] lArrPartyDtls = null;
		StringBuilder lSBStatus = new StringBuilder();
		BptmCommonServicesDAO bptmDAO = null;
		Long lLongBillControlNum = 0L;
		List<String> lLstPensionerCode = null;
		PensionBillDAO lObjPensionBillDAO = null;
		Integer lIntForMonth = null;
		try {
			setSessionInfo(inputMap);
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			bptmDAO = new BptmCommonServicesDAOImpl(TrnBillRegister.class, srvcLoc.getSessionFactory());
			lObjPensionBillDAO = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
			lLstTrnBillRegisterVO = new ArrayList<TrnBillRegister>();
			lLstTrnPensionBillHdr = new ArrayList();
			lLstTrnPensionBillDtls = new ArrayList();
			lLstRltBillParty = new ArrayList<RltBillParty>();
			lLstTrnBillMvmnt = new ArrayList<TrnBillMvmnt>();
			String[] lArrStrChangeRqstId = StringUtility.getParameterValues("changeRqstId", request);
			for (int i = 0; i < lArrStrChangeRqstId.length; i++) {
				if (!"".equals(lArrStrChangeRqstId[i])) {
					lLstChangeRqstId.add(Long.valueOf(lArrStrChangeRqstId[i]));
				}
			}
			lObjTrnMonthlyChangeRqstVO = new TrnMonthlyChangeRqst();

			/*
			 * Preparing map of chngRqstId,TrnMonthlyChangeRqstVo
			 */
			lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnMonthlyChangeRqst.class, srvcLoc.getSessionFactory());
			lLstTrnMonthlyChangeRqst = lObjChangeStatementDAO.getListForColumnByValues("changeRqstId", lLstChangeRqstId);

			for (TrnMonthlyChangeRqst lObjTrnMonthlyChangeRqst : lLstTrnMonthlyChangeRqst) {
				lMapTrnMonthlyChangeRqst.put(lObjTrnMonthlyChangeRqst.getChangeRqstId(), lObjTrnMonthlyChangeRqst);
			}

			/*
			 * Preparing list of all hdr ids, (Map of
			 * chngRqstId,List<chngHdrIds>) and (Map of
			 * chngHdrId,TrnPensionChangeHdrVO)
			 */

			lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnPensionChangeHdr.class, srvcLoc.getSessionFactory());
			lLstTrnPensionChangeHdr = lObjChangeStatementDAO.getListForColumnByValues("changeRqstId", lLstChangeRqstId);
			for (TrnPensionChangeHdr lObjTrnPensionChangeHdrVO : lLstTrnPensionChangeHdr) {
				lLstTrnPnsnChngHdrId = lMapChngRqstIdHdrId.get(lObjTrnPensionChangeHdrVO.getChangeRqstId());
				if (lLstTrnPnsnChngHdrId != null) {
					lLstTrnPnsnChngHdrId.add(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId());
				} else {
					lLstTrnPnsnChngHdrId = new ArrayList<Long>();
					lLstTrnPnsnChngHdrId.add(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId());
				}
				lLstAllTrnPnsnChngHdrId.add(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId());
				lMapChngRqstIdHdrId.put(lObjTrnPensionChangeHdrVO.getChangeRqstId(), lLstTrnPnsnChngHdrId);
				lMapTrnPensionChangeHdr.put(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId(), lObjTrnPensionChangeHdrVO);
			}

			/*
			 * Preparing list of all dtls ids,(Map of
			 * chngHdrId,List<chngDtlsIds>) and (Map of
			 * chngDtlsId,TrnPensionChangeDtlsVo)
			 */

			lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnPensionChangeDtls.class, srvcLoc.getSessionFactory());
			lLstTrnPensionChangeDtls = lObjChangeStatementDAO.getListForColumnByValues("trnPensionChangeHdrId", lLstAllTrnPnsnChngHdrId);
			for (TrnPensionChangeDtls lObjTrnPensionChangeDtlsVO : lLstTrnPensionChangeDtls) {
				lLstTrnPnsnChngDtlsId = lMapChngHdrIdDtlsId.get(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeHdrId());
				if (lLstTrnPnsnChngDtlsId != null) {
					lLstTrnPnsnChngDtlsId.add(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeDtlsId());
				} else {
					lLstTrnPnsnChngDtlsId = new ArrayList<Long>();
					lLstTrnPnsnChngDtlsId.add(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeDtlsId());
				}
				lLstAllTrnPnsnChngDtlsId.add(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeDtlsId());
				lMapChngHdrIdDtlsId.put(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeHdrId(), lLstTrnPnsnChngDtlsId);
				lMapTrnPensionChangeDtls.put(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeDtlsId(), lObjTrnPensionChangeDtlsVO);
			}

			/*
			 * Getting pension party details from change rqst ids.
			 */
			lMapChngRqstIdParyDtls = lObjMonthlyPensionBillDAO.getPartyDtlsByChngRqstIds(lLstChangeRqstId, gStrLocCode, gLngLangId);
			/*
			 * Inserting all change statement data to pension bill tables
			 */
			lLongBillControlNum = bptmDAO.getBillControlNo(inputMap);
			String lStrBillCntrlNoPrefix = getBillCntrlNoPrefix();
			lLstTrnMonthlyChangeRqst = new ArrayList<TrnMonthlyChangeRqst>();
			lLstTrnPensionChangeHdr = new ArrayList<TrnPensionChangeHdr>();
			lLngPkCntBillRegister = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_bill_register", inputMap, lLstChangeRqstId.size());
			lLngPkCntPensionBillHdr = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_bill_hdr", inputMap, lLstAllTrnPnsnChngHdrId.size());
			lLngPkCntPensionBillDtls = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_bill_dtls", inputMap, lLstAllTrnPnsnChngDtlsId.size());
			lLngPkCntRltBillParty = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("rlt_bill_party", inputMap, lLstChangeRqstId.size());
			lLngPkCntTrnBillMvmnt = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_bill_mvmnt", inputMap, lLstChangeRqstId.size());
			for (Long lLngChngRqstId : lLstChangeRqstId) {
				lObjTrnMonthlyChangeRqstVO = lMapTrnMonthlyChangeRqst.get(lLngChngRqstId);
				lLngBillNo = ++lLngPkCntBillRegister;
				lLngBillNo = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillNo, inputMap);
				lObjTrnBillRegisterVO = new TrnBillRegister();
				lObjTrnBillRegisterVO.setBillNo(lLngBillNo);
				lObjTrnBillRegisterVO.setBillDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(gDate)));
				lObjTrnBillRegisterVO.setSubjectId(Short.parseShort(bundleConst.getString("BILLTYPE.MONTHLY")));
				lObjTrnBillRegisterVO.setPhyBill(Short.parseShort("1"));
				lObjTrnBillRegisterVO.setBillGrossAmount(lObjTrnMonthlyChangeRqstVO.getGrossAmount());
				lObjTrnBillRegisterVO.setBillNetAmount(lObjTrnMonthlyChangeRqstVO.getNetAmount());
				lObjTrnBillRegisterVO.setCreatedDate(gDate);
				lObjTrnBillRegisterVO.setCreatedPostId(gLngPostId);
				lObjTrnBillRegisterVO.setCreatedUserId(gLngUserId);
				lObjTrnBillRegisterVO.setDeductionA(lObjTrnMonthlyChangeRqstVO.getRecoveryAmount());
				lObjTrnBillRegisterVO.setDeductionB(lObjTrnMonthlyChangeRqstVO.getDeductionB());
				lObjTrnBillRegisterVO.setLocationCode(lObjTrnMonthlyChangeRqstVO.getLocationCode());
				lObjTrnBillRegisterVO.setDbId(Long.valueOf(lObjTrnMonthlyChangeRqstVO.getDbId()));
				lObjTrnBillRegisterVO.setCurrBillStatus(DBConstants.ST_BILL_CREATED);
				lObjTrnBillRegisterVO.setAudPostId(gLngPostId);
				lObjTrnBillRegisterVO.setAudUserId(gLngUserId);
				lObjTrnBillRegisterVO.setFinYearId(SessionHelper.getFinYrId(inputMap).toString());
				String lStrBillCntrlNo = BptmCommonServiceImpl.getBillControlNo(inputMap); // getBillCntrlNumSeq
				lObjTrnBillRegisterVO.setTcBill(bundleConst.getString("MNTH.MONTHLY"));
				lObjTrnBillRegisterVO.setBillCntrlNo(lStrBillCntrlNoPrefix + (lLongBillControlNum++));
				lLstTrnBillRegisterVO.add(lObjTrnBillRegisterVO);

				// ---Updating bill no in trn_monthly_change_rqst table
				lObjTrnMonthlyChangeRqstVO.setBillNo(lLngBillNo);
				lLstTrnMonthlyChangeRqst.add(lObjTrnMonthlyChangeRqstVO);

				// ---Inserting entry in rlt_bill_party tables
				lObjBillParty = new RltBillParty();
				lLngBillPartyId = ++lLngPkCntRltBillParty;
				lLngBillPartyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillPartyId, inputMap);
				lArrPartyDtls = lMapChngRqstIdParyDtls.get(lLngChngRqstId);
				lObjBillParty.setBillPartyId(lLngBillPartyId);
				lObjBillParty.setBillNo(lLngBillNo);
				lObjBillParty.setCreatedDate(gDate);
				lObjBillParty.setCreatedPostId(gLngPostId);
				lObjBillParty.setCreatedUserId(gLngUserId);
				lObjBillParty.setDbId(gDbId);
				lObjBillParty.setPartyName(lArrPartyDtls[0].toString());
				lObjBillParty.setPartyAmt(lObjTrnMonthlyChangeRqstVO.getNetAmount());
				lObjBillParty.setPaymentMode((lArrPartyDtls[1] != null) ? bundleCaseConst.getString("PPMT.PAYMODEECS") : bundleCaseConst.getString("PPMT.PAYMODECHQ"));
				lObjBillParty.setMicrCode((lArrPartyDtls[1] != null) ? new Long(lArrPartyDtls[1].toString()) : null);
				lObjBillParty.setBankCode(lObjTrnMonthlyChangeRqstVO.getBankCode());
				lObjBillParty.setBranchCode(lObjTrnMonthlyChangeRqstVO.getBranchCode());
				lObjBillParty.setLocationCode(gStrLocCode);
				lLstRltBillParty.add(lObjBillParty);

				// ---Inserting entry in trn_bill_mvmnt table
				lObjTrnBillMvmnt = new TrnBillMvmnt();
				lLngBillMvmntId = ++lLngPkCntTrnBillMvmnt;
				lLngBillMvmntId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillMvmntId, inputMap);
				lObjTrnBillMvmnt.setBillMvmtId(lLngBillMvmntId);
				lObjTrnBillMvmnt.setBillNo(lLngBillNo);
				lObjTrnBillMvmnt.setMvmntStatus(DBConstants.ST_BILL_CREATED);
				lObjTrnBillMvmnt.setCreatedDate(gDate);
				lObjTrnBillMvmnt.setCreatedPostId(gLngPostId);
				lObjTrnBillMvmnt.setCreatedUserId(gLngUserId);
				lObjTrnBillMvmnt.setMovemntId(1L);
				lObjTrnBillMvmnt.setReceivingUserId(gLngUserId);
				lObjTrnBillMvmnt.setReceivedDate(gDate);
				lObjTrnBillMvmnt.setStatusUpdtDate(gDate);
				lObjTrnBillMvmnt.setStatusUpdtPostid(gLngPostId); // next
				lObjTrnBillMvmnt.setStatusUpdtUserid(gLngUserId);
				lObjTrnBillMvmnt.setDbId(gDbId);
				lObjTrnBillMvmnt.setLocationCode(gStrLocCode);
				lLstTrnBillMvmnt.add(lObjTrnBillMvmnt);

				lLstTrnPnsnChngHdrId = lMapChngRqstIdHdrId.get(lLngChngRqstId);
				lLstPensionerCode = new ArrayList<String>();
				for (Long lLngPnsnChngHrdId : lLstTrnPnsnChngHdrId) {
					lLngBillHdrId = ++lLngPkCntPensionBillHdr;
					lLngBillHdrId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillHdrId, inputMap);
					lObjTrnPensionChangeHdr = lMapTrnPensionChangeHdr.get(lLngPnsnChngHrdId);
					lObjPensionBillHdr = new TrnPensionBillHdr();
					lObjPensionBillHdr.setTrnPensionBillHdrId(lLngBillHdrId);
					lObjPensionBillHdr.setBillType(lObjTrnPensionChangeHdr.getBillType());
					lObjPensionBillHdr.setBillNo(lLngBillNo);
					lObjPensionBillHdr.setBillDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(gDate)));
					lObjPensionBillHdr.setHeadCode(new BigDecimal(lObjTrnPensionChangeHdr.getHeadCode()));
					lObjPensionBillHdr.setForMonth(lObjTrnPensionChangeHdr.getForMonth());
					lObjPensionBillHdr.setBankCode(lObjTrnPensionChangeHdr.getBankCode());
					lObjPensionBillHdr.setBranchCode(lObjTrnPensionChangeHdr.getBranchCode());
					lObjPensionBillHdr.setScheme(lObjTrnPensionChangeHdr.getScheme());
					lObjPensionBillHdr.setSchemeCode(lObjTrnPensionChangeHdr.getSchemeCode());
					lObjPensionBillHdr.setTrnCounter(lObjTrnPensionChangeHdr.getTrnCounter());
					lObjPensionBillHdr.setLocationCode(lObjTrnPensionChangeHdr.getLocationCode());
					lObjPensionBillHdr.setGrossAmount(lObjTrnPensionChangeHdr.getGrossAmount());
					lObjPensionBillHdr.setNetAmount(lObjTrnPensionChangeHdr.getNetAmount());
					lObjPensionBillHdr.setDeductionA(lObjTrnPensionChangeHdr.getRecoveryAmount());
					lObjPensionBillHdr.setDeductionB(lObjTrnPensionChangeHdr.getDeductionB());
					lObjPensionBillHdr.setCreatedDate(gDate);
					lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
					lLstTrnPensionBillHdr.add(lObjPensionBillHdr);

					lObjTrnPensionChangeHdr.setBillNo(lLngBillNo);
					lLstTrnPensionChangeHdr.add(lObjTrnPensionChangeHdr);
					lIntForMonth = lObjTrnPensionChangeHdr.getForMonth();
					lLstTrnPnsnChngDtlsId = lMapChngHdrIdDtlsId.get(lLngPnsnChngHrdId);
					for (Long lLngTrnPnsnChngDtlsId : lLstTrnPnsnChngDtlsId) {
						lLngBillDtlsId = ++lLngPkCntPensionBillDtls;
						lLngBillDtlsId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillDtlsId, inputMap);
						lObjTrnPensionChangeDtls = lMapTrnPensionChangeDtls.get(lLngTrnPnsnChngDtlsId);
						lObjPensionBillDtlVO = new TrnPensionBillDtls();
						lObjPensionBillDtlVO.setTrnPensionBillDtlsId(lLngBillDtlsId);
						lObjPensionBillDtlVO.setTrnPensionBillHdrId(lLngBillHdrId);
						lObjPensionBillDtlVO.setPpoNo(lObjTrnPensionChangeDtls.getPpoNo());
						lObjPensionBillDtlVO.setPensionerCode(lObjTrnPensionChangeDtls.getPensionerCode());
						lObjPensionBillDtlVO.setCvpMonthAmount(lObjTrnPensionChangeDtls.getCvpMonthAmount());
						lObjPensionBillDtlVO.setPensionAmount(lObjTrnPensionChangeDtls.getPensionAmount());
						lObjPensionBillDtlVO.setDpAmount(lObjTrnPensionChangeDtls.getDpAmount());
						lObjPensionBillDtlVO.setTiAmount(lObjTrnPensionChangeDtls.getTiAmount());
						lObjPensionBillDtlVO.setArrearAmount(lObjTrnPensionChangeDtls.getArrearAmount()); // arrear
						// amount
						// =
						// other
						// arrear
						// amount
						lObjPensionBillDtlVO.setTiArrearAmount(lObjTrnPensionChangeDtls.getTiArrearAmount());
						lObjPensionBillDtlVO.setRecoveryAmount(lObjTrnPensionChangeDtls.getRecoveryAmount());
						lObjPensionBillDtlVO.setReducedPension(lObjTrnPensionChangeDtls.getReducedPension());
						lObjPensionBillDtlVO.setAccountNo(lObjTrnPensionChangeDtls.getAccountNo());
						lObjPensionBillDtlVO.setPensionerName(lObjTrnPensionChangeDtls.getPensionerName());
						lObjPensionBillDtlVO.setAllcationBf11156(lObjTrnPensionChangeDtls.getAllcationBf11156());
						lObjPensionBillDtlVO.setAllcationAf11156(lObjTrnPensionChangeDtls.getAllcationAf11156());
						lObjPensionBillDtlVO.setAllcationAf10560(lObjTrnPensionChangeDtls.getAllcationAf10560());
						lObjPensionBillDtlVO.setPersonalPensionAmount(lObjTrnPensionChangeDtls.getPersonalPensionAmount());
						lObjPensionBillDtlVO.setIr1Amount(lObjTrnPensionChangeDtls.getIr1Amount());
						lObjPensionBillDtlVO.setIr2Amount(lObjTrnPensionChangeDtls.getIr2Amount());
						lObjPensionBillDtlVO.setIr3Amount(lObjTrnPensionChangeDtls.getIr3Amount());
						lObjPensionBillDtlVO.setTiAmount(lObjTrnPensionChangeDtls.getTiAmount());
						lObjPensionBillDtlVO.setPayForMonth(lObjTrnPensionChangeDtls.getPayForMonth());
						lObjPensionBillDtlVO.setAllcationBf1436(lObjTrnPensionChangeDtls.getAllcationBf1436());
						lObjPensionBillDtlVO.setAllcationAfZp(lObjTrnPensionChangeDtls.getAllcationAfZp());
						lObjPensionBillDtlVO.setNetAmount(lObjTrnPensionChangeDtls.getNetAmount());
						lObjPensionBillDtlVO.setDaRate(lObjTrnPensionChangeDtls.getDaRate());
						lObjPensionBillDtlVO.setGrossAmount(lObjTrnPensionChangeDtls.getGrossAmount());
						lObjPensionBillDtlVO.setHeadCode(new BigDecimal(lObjTrnPensionChangeDtls.getHeadCode()));
						lObjPensionBillDtlVO.setAdpAmount(lObjTrnPensionChangeDtls.getAdpAmount());
						lObjPensionBillDtlVO.setLedgerNo(lObjTrnPensionChangeDtls.getLedgerNo());
						lObjPensionBillDtlVO.setPageNo(lObjTrnPensionChangeDtls.getPageNo());
						lObjPensionBillDtlVO.setPeonAllowance(lObjTrnPensionChangeDtls.getPeonAllowance());
						lObjPensionBillDtlVO.setMedicalAllowenceAmount(lObjTrnPensionChangeDtls.getMedicalAllowenceAmount());
						lObjPensionBillDtlVO.setOtherBenefits(lObjTrnPensionChangeDtls.getOtherBenefits());
						lObjPensionBillDtlVO.setPensnCutAmount(lObjTrnPensionChangeDtls.getPensionCutAmount());
						lObjPensionBillDtlVO.setOther1(lObjTrnPensionChangeDtls.getOther1());
						lObjPensionBillDtlVO.setArrear6PC(lObjTrnPensionChangeDtls.getArrear6PC());
						lObjPensionBillDtlVO.setArrearLC(lObjTrnPensionChangeDtls.getArrearLC());
						lObjPensionBillDtlVO.setArrearCommutation(lObjTrnPensionChangeDtls.getArrearCommutation());
						lObjPensionBillDtlVO.setArrearOthrDiff(lObjTrnPensionChangeDtls.getArrearOthrDiff());
						lObjPensionBillDtlVO.setTotalArrearAmt(lObjTrnPensionChangeDtls.getTotalArrearAmt());
						lObjPensionBillDtlVO.setPunishmentCutAmt(lObjTrnPensionChangeDtls.getPunishmentCutAmt());
						lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnPensionBillDtls.class, srvcLoc.getSessionFactory());
						// lObjChangeStatementDAO.create(lObjPensionBillDtlVO);
						lLstTrnPensionBillDtls.add(lObjPensionBillDtlVO);
						lLstPensionerCode.add(lObjTrnPensionChangeDtls.getPensionerCode());

					}
				}
				/*
				 * --Setting bill number in arrear details of all pensioner code
				 * in bill.
				 */
				if (lIntForMonth != null) {
					lObjPensionBillDAO.updateBillNoInArrearDtls(lLstPensionerCode, lLngBillNo, lIntForMonth, gLngUserId, gLngPostId, gDate);
					lObjPensionBillDAO.updateBillNoForRecovery(lLstPensionerCode, lIntForMonth, gStrLocCode, lLngBillNo, gLngUserId, gLngPostId, gDate);
				}
			}
			hitStg.saveOrUpdateAll(lLstTrnBillRegisterVO);
			hitStg.saveOrUpdateAll(lLstTrnPensionBillHdr);
			hitStg.saveOrUpdateAll(lLstTrnPensionBillDtls);
			hitStg.saveOrUpdateAll(lLstTrnMonthlyChangeRqst);
			hitStg.saveOrUpdateAll(lLstTrnPensionChangeHdr);
			hitStg.saveOrUpdateAll(lLstRltBillParty);
			hitStg.saveOrUpdateAll(lLstTrnBillMvmnt);

			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<SUCCESSSTATUS>");
			lSBStatus.append("Y");
			lSBStatus.append("</SUCCESSSTATUS>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			// inputMap.put("billNo", lLngBillNo);
			//
			// lLstMonthlyPensionBillVO =
			// lObjMonthlyPensionBillDAO.getMonthlyPensionBillVOList(lLngBillNo,
			// gStrLocCode);
			// inputMap.put("MonthlyPensionList", lLstMonthlyPensionBillVO);
			// inputMap.put("TrnPensionBillHdrLst", lLstTrnPensionBillHdr);
			// inputMap.put("TrnBillRegister", lObjTrnBillRegisterVO);
			// inputMap.put("BillRegVO", lObjTrnBillRegisterVO);
			// lLstMstPensionHeadcode =
			// lObjMonthlyPensionBillDAO.getAllHeadCodeAndDesc();
			//
			// for (MstPensionHeadcode lObjMstPensionHeadcode :
			// lLstMstPensionHeadcode) {
			// lMpHeadCodeDesc.put(lObjMstPensionHeadcode.getHeadCode(),
			// lObjMstPensionHeadcode.getDescription());
			// }
			//
			// List lHeadCodeWiseDtlsLst = new ArrayList();
			// for (TrnPensionBillHdr lObjTrnPensionBillHdr :
			// lLstTrnPensionBillHdr) {
			// List lTemp = new ArrayList();
			//
			// lTemp.add(lObjTrnPensionBillHdr.getHeadCode());
			// lTemp.add(lMpHeadCodeDesc.get(lObjTrnPensionBillHdr.getHeadCode()));
			// lTemp.add(lObjTrnPensionBillHdr.getGrossAmount());
			// lTemp.add(lObjTrnPensionBillHdr.getDeductionA());
			// lTemp.add(lObjTrnPensionBillHdr.getDeductionB());
			// lTemp.add(lObjTrnPensionBillHdr.getNetAmount());
			//
			// lHeadCodeWiseDtlsLst.add(lTemp);
			//
			// }
			// lTotalNetAmt = lObjTrnBillRegisterVO.getBillNetAmount();
			// inputMap.put("TotalNetAmt", lTotalNetAmt);
			// RuleBasedNumberFormat fomatter = new
			// RuleBasedNumberFormat(IReportConstants.INDIAN_ENG_RULE_SET);
			// String result = fomatter.format(new
			// com.ibm.icu.math.BigDecimal(lTotalNetAmt));
			// inputMap.put("TotalNetAmtInWords", result);
			// inputMap.put("HeadCodeWiseDtls", lHeadCodeWiseDtlsLst);
			// inputMap.put("TrnBillRegisterVO", lObjTrnBillRegisterVO);
			//
			// String lStrDatePayMonth =
			// lObjTrnMonthlyChangeRqstVO.getForMonth().toString();
			// MonthlyPensionBillDAOImpl lObjMonthlyDAO = new
			// MonthlyPensionBillDAOImpl(MstPensionerHdr.class,
			// srvcLoc.getSessionFactory());
			// String lStrMonth = lStrDatePayMonth.substring(4);
			// String lStrYear = lStrDatePayMonth.substring(0, 4);
			// List lLstMonthDtls = lObjMonthlyDAO.getMonthDtls(lStrMonth,
			// gLngLangId);
			// inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
			// inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
			// inputMap.put("ForBillYear", lStrYear);
			//
			// List lLstBank = new ArrayList();
			// inputMap.put("Branch",
			// lObjTrnMonthlyChangeRqstVO.getBranchCode());
			// String lStrBankName = null;
			// String lStrBranchName = null;
			// lLstBank =
			// lObjMonthlyDAO.getBankCode(lObjTrnMonthlyChangeRqstVO.getBranchCode(),
			// gStrLocCode);
			// if (lLstBank != null && !lLstBank.isEmpty()) {
			// Object[] lObjArr = (Object[]) lLstBank.get(0);
			// lStrBankName = (String) lObjArr[1];
			// lStrBranchName = (String) lObjArr[2];
			// inputMap.put("BankName", lStrBankName);
			// inputMap.put("BranchName", lStrBranchName);
			// } else {
			// inputMap.put("BankName", "");
			// inputMap.put("BranchName", "");
			//
			// }
			//
			// getMonthlyReport(inputMap);
			// // getSummary(inputMap);
			// resObj.setResultValue(inputMap);
			// // resObj.setViewName("GenerateBillVO");
		} catch (Exception e) {
			gLogger.error("Error is: " + e, e);
			// e.printStackTrace();
			throw (e);

		}

		return resObj;
	}

	// getMonthlyPensionBillVOListForGetData
	public ResultObject getMonthlyBillData(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");

		TrnMonthlyChangeRqst lObjTrnMonthlyChangeRqstVO = null;

		// TrnBillRegister lObjTrnBillRegisterVO = null;
		TrnPensionBillHdr lObjPensionBillHdr = null;
		List<TrnPensionChangeHdr> lLstTrnPensionChangeHdr = null;
		List<TrnPensionChangeDtls> lLstTrnPensionChangeDtls = null;
		List<TrnPensionBillHdr> lLstTrnPensionBillHdr = null;
		Long lLngChangeRqstId = null;
		Long lLngChangeHdrId = null;
		ChangeStatementDAO lObjChangeStatementDAO = null;
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		Map<BigDecimal, String> lMpHeadCodeDesc = new HashMap<BigDecimal, String>();
		List<MstPensionHeadcode> lLstMstPensionHeadcode = null;
		BigDecimal lTotalNetAmt = BigDecimal.ZERO;
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		MonthlyPensionBillVO lObjMonthlyPensionBillVO = null;
		Map<BigDecimal, String> lMapHeadCodeSeries = new HashMap<BigDecimal, String>();
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
		try {
			setSessionInfo(inputMap);
			lLstTrnPensionBillHdr = new ArrayList();
			lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			String lStrChangeRqstId = StringUtility.getParameter("changeRqstId", request);
			lMapHeadCodeSeries = lObjCommonPensionDAO.getAllHeadCodeSeriesMap();
			lLngChangeRqstId = Long.parseLong(lStrChangeRqstId);
			lObjTrnMonthlyChangeRqstVO = new TrnMonthlyChangeRqst();
			lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnMonthlyChangeRqst.class, srvcLoc.getSessionFactory());
			lObjTrnMonthlyChangeRqstVO = (TrnMonthlyChangeRqst) lObjChangeStatementDAO.read(lLngChangeRqstId);

			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			lLstTrnPensionChangeHdr = lObjMonthlyPensionBillDAO.getTrnPensionChangeHdrVOList(lLngChangeRqstId);

			for (TrnPensionChangeHdr lObjTrnPensionChangeHdr : lLstTrnPensionChangeHdr) {

				lLngChangeHdrId = lObjTrnPensionChangeHdr.getTrnPensionChangeHdrId();

				lObjPensionBillHdr = new TrnPensionBillHdr();
				lObjPensionBillHdr.setBillType(lObjTrnPensionChangeHdr.getBillType());
				lObjPensionBillHdr.setBillDate(lObjTrnPensionChangeHdr.getBillDate());
				lObjPensionBillHdr.setHeadCode(new BigDecimal(lObjTrnPensionChangeHdr.getHeadCode()));
				lObjPensionBillHdr.setForMonth(lObjTrnPensionChangeHdr.getForMonth());
				lObjPensionBillHdr.setBankCode(lObjTrnPensionChangeHdr.getBankCode());
				lObjPensionBillHdr.setBranchCode(lObjTrnPensionChangeHdr.getBranchCode());
				lObjPensionBillHdr.setScheme(lObjTrnPensionChangeHdr.getScheme());
				lObjPensionBillHdr.setTrnCounter(lObjTrnPensionChangeHdr.getTrnCounter());
				lObjPensionBillHdr.setLocationCode(lObjTrnPensionChangeHdr.getLocationCode());
				lObjPensionBillHdr.setGrossAmount(lObjTrnPensionChangeHdr.getGrossAmount());
				lObjPensionBillHdr.setNetAmount(lObjTrnPensionChangeHdr.getNetAmount());
				lObjPensionBillHdr.setDeductionA(lObjTrnPensionChangeHdr.getRecoveryAmount());
				lObjPensionBillHdr.setDeductionB(lObjTrnPensionChangeHdr.getDeductionB());
				lObjPensionBillHdr.setCreatedDate(gDate);
				lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));

				lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnPensionBillHdr.class, srvcLoc.getSessionFactory());
				lLstTrnPensionBillHdr.add(lObjPensionBillHdr);

				lLstTrnPensionChangeDtls = lObjMonthlyPensionBillDAO.getTrnPensionChangeDtlsVOList(lLngChangeHdrId);
				for (TrnPensionChangeDtls lObjTrnPensionChangeDtls : lLstTrnPensionChangeDtls) {
					lObjMonthlyPensionBillVO = new MonthlyPensionBillVO();
					lObjMonthlyPensionBillVO.setHeadCode(new BigDecimal(lObjTrnPensionChangeDtls.getHeadCode()));
					lObjMonthlyPensionBillVO.setSeries(lMapHeadCodeSeries.get(new BigDecimal(lObjTrnPensionChangeDtls.getHeadCode())));
					lObjMonthlyPensionBillVO.setLedgerNo(lObjTrnPensionChangeDtls.getLedgerNo());
					lObjMonthlyPensionBillVO.setPageNo(lObjTrnPensionChangeDtls.getPageNo());
					lObjMonthlyPensionBillVO.setPpoNo(lObjTrnPensionChangeDtls.getPpoNo());
					lObjMonthlyPensionBillVO.setPensionerName(lObjTrnPensionChangeDtls.getPensionerName());
					lObjMonthlyPensionBillVO.setAccountNo(lObjTrnPensionChangeDtls.getAccountNo());
					lObjMonthlyPensionBillVO.setAllnBf11136(lObjTrnPensionChangeDtls.getAllcationBf1436());
					lObjMonthlyPensionBillVO.setAllnBf11156(lObjTrnPensionChangeDtls.getAllcationBf11156());
					lObjMonthlyPensionBillVO.setAllnAf11156(lObjTrnPensionChangeDtls.getAllcationAf11156());
					lObjMonthlyPensionBillVO.setAllnAf10560(lObjTrnPensionChangeDtls.getAllcationAf10560());
					lObjMonthlyPensionBillVO.setAllnAfZp(lObjTrnPensionChangeDtls.getAllcationAfZp());
					lObjMonthlyPensionBillVO.setIr1Amt(lObjTrnPensionChangeDtls.getIr1Amount());
					lObjMonthlyPensionBillVO.setIr2Amt(lObjTrnPensionChangeDtls.getIr2Amount());
					lObjMonthlyPensionBillVO.setIr3Amt(lObjTrnPensionChangeDtls.getIr3Amount());
					lObjMonthlyPensionBillVO.setDpPercentAmount(lObjTrnPensionChangeDtls.getDpAmount());
					lObjMonthlyPensionBillVO.setTiPercentAmount(lObjTrnPensionChangeDtls.getTiAmount());
					lObjMonthlyPensionBillVO.setGrossAmount(lObjTrnPensionChangeDtls.getGrossAmount());
					lObjMonthlyPensionBillVO.setRecoveryAmount(lObjTrnPensionChangeDtls.getRecoveryAmount());
					lObjMonthlyPensionBillVO.setNetPensionAmount(lObjTrnPensionChangeDtls.getNetAmount());
					lObjMonthlyPensionBillVO.setTIArrearsAmount(lObjTrnPensionChangeDtls.getTiArrearAmount());
					lObjMonthlyPensionBillVO.setOtherArrearsAmount(lObjTrnPensionChangeDtls.getArrearAmount());
					lObjMonthlyPensionBillVO.setPeonAllowanceAmt(lObjTrnPensionChangeDtls.getPeonAllowance());
					lObjMonthlyPensionBillVO.setMedicalAllowenceAmount(lObjTrnPensionChangeDtls.getMedicalAllowenceAmount());
					lObjMonthlyPensionBillVO.setOtherBenefit(lObjTrnPensionChangeDtls.getOtherBenefits());
					lObjMonthlyPensionBillVO.setGallantryAmt(lObjTrnPensionChangeDtls.getOther1());
					lObjMonthlyPensionBillVO.setTotalArrearAmt(lObjTrnPensionChangeDtls.getTotalArrearAmt());
					lLstMonthlyPensionBillVO.add(lObjMonthlyPensionBillVO);
				}

			}

			inputMap.put("MonthlyPensionList", lLstMonthlyPensionBillVO);
			inputMap.put("TrnPensionBillHdrLst", lLstTrnPensionBillHdr);

			lLstMstPensionHeadcode = lObjMonthlyPensionBillDAO.getAllHeadCodeAndDesc();

			for (MstPensionHeadcode lObjMstPensionHeadcode : lLstMstPensionHeadcode) {
				lMpHeadCodeDesc.put(lObjMstPensionHeadcode.getHeadCode(), lObjMstPensionHeadcode.getDescription());
			}

			List lHeadCodeWiseDtlsLst = new ArrayList();
			for (TrnPensionBillHdr lObjTrnPensionBillHdr : lLstTrnPensionBillHdr) {
				List lTemp = new ArrayList();

				lTemp.add(lMapHeadCodeSeries.get(lObjTrnPensionBillHdr.getHeadCode()));
				lTemp.add(lMpHeadCodeDesc.get(lObjTrnPensionBillHdr.getHeadCode()));
				lTemp.add(lObjTrnPensionBillHdr.getGrossAmount());
				lTemp.add(lObjTrnPensionBillHdr.getDeductionA());
				lTemp.add(lObjTrnPensionBillHdr.getDeductionB());
				lTemp.add(lObjTrnPensionBillHdr.getNetAmount());

				lHeadCodeWiseDtlsLst.add(lTemp);

			}
			lTotalNetAmt = lObjTrnMonthlyChangeRqstVO.getNetAmount();
			inputMap.put("TotalNetAmt", lTotalNetAmt);
			RuleBasedNumberFormat fomatter = new RuleBasedNumberFormat(IReportConstants.INDIAN_ENG_RULE_SET);
			String result = fomatter.format(new com.ibm.icu.math.BigDecimal(lTotalNetAmt));
			inputMap.put("TotalNetAmtInWords", result);
			inputMap.put("HeadCodeWiseDtls", lHeadCodeWiseDtlsLst);
			// inputMap.put("TrnBillRegisterVO", lObjTrnBillRegisterVO);

			String lStrDatePayMonth = lObjTrnMonthlyChangeRqstVO.getForMonth().toString();
			MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			String lStrMonth = lStrDatePayMonth.substring(4);
			String lStrYear = lStrDatePayMonth.substring(0, 4);
			List lLstMonthDtls = lObjMonthlyDAO.getMonthDtls(lStrMonth, gLngLangId);
			inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
			inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
			inputMap.put("ForBillYear", lStrYear);

			List lLstBank = new ArrayList();
			inputMap.put("Branch", lObjTrnMonthlyChangeRqstVO.getBranchCode());
			String lStrBankName = null;
			String lStrBranchName = null;
			lLstBank = lObjMonthlyDAO.getBankCode(lObjTrnMonthlyChangeRqstVO.getBranchCode(), gStrLocCode);
			if (lLstBank != null && !lLstBank.isEmpty()) {
				Object[] lObjArr = (Object[]) lLstBank.get(0);
				lStrBankName = (String) lObjArr[1];
				lStrBranchName = (String) lObjArr[2];
				inputMap.put("BankName", lStrBankName);
				inputMap.put("BranchName", lStrBranchName);
			} else {
				inputMap.put("BankName", "");
				inputMap.put("BranchName", "");

			}
			inputMap.put("ChangeRqstId", lStrChangeRqstId);
			// RltBillParty lObjBillParty = null;
			// List<RltBillParty> lLstBillParty = null;
			// RltBillPartyDAO lObjBillPartyDAO = new
			// RltBillPartyDAOImpl(RltBillParty.class,
			// srvcLoc.getSessionFactory());
			//
			// // Deleting previous party Info
			// lLstBillParty = lObjBillPartyDAO.getPartyByBill(lLngBillNo);
			// Iterator<RltBillParty> lItrBillParty = lLstBillParty.iterator();
			// while (lItrBillParty.hasNext()) {
			// lObjBillPartyDAO.delete(lItrBillParty.next());
			// }
			//
			// PensionBillDAO lObjPensionBillDAO = new
			// PensionBillDAOImpl(srvcLoc.getSessionFactory());
			// List<RltBillParty> lLstRltBillPartyVO = null;
			//
			// // if(! "MR".equals( StringUtility.getParameter("PensionType",
			// // request)))
			// {
			// lLstRltBillPartyVO =
			// lObjPensionBillDAO.getPensionerPartyDtls(inputMap);
			// }
			// // Saving the Bill Party Details
			// if (lLstRltBillPartyVO != null || !lLstRltBillPartyVO.isEmpty())
			// {
			// lItrBillParty = lLstRltBillPartyVO.iterator();
			//
			// while (lItrBillParty.hasNext()) {
			// lObjBillParty = lItrBillParty.next();
			// lObjBillParty.setBillNo(lLngBillNo);
			// lObjBillParty.setCreatedDate(gDate);
			// lObjBillParty.setCreatedPostId(gLngPostId);
			// lObjBillParty.setCreatedUserId(gLngUserId);
			// lObjBillParty.setPartyName(lStrBankName);
			// lObjBillParty.setPartyAmt(lObjTrnMonthlyChangeRqstVO.getNetAmount());
			// lObjBillParty.setPaymentMode("ECS");
			// lObjBillParty.setBankCode(lObjTrnMonthlyChangeRqstVO.getBankCode());
			// lObjBillParty.setBranchCode(lObjTrnMonthlyChangeRqstVO.getBranchCode());
			// lObjBillParty.setLocationCode(gStrLocCode);
			// saveBillParty(lObjBillParty, inputMap);
			// }
			// }
			//
			// getMonthlyReport(inputMap);
			// getSummary(inputMap);
			resObj.setResultValue(inputMap);
			// resObj.setViewName("GenerateBillVO");
		} catch (Exception e) {
			gLogger.error("Error is: " + e, e);
			// e.printStackTrace();
			throw (e);

		}

		return resObj;
	}

	/**
	 * Saves the Bill Movement Data in TrnBillMvmnt. This method will be called
	 * only at time of Bill Creation.
	 * 
	 * @param lObjBillMvmntVO
	 *            TrnBillMvmntVO
	 * @param inputMap
	 *            InputMap.
	 * @throws Exception
	 *             Exception
	 */
	public void saveBillMovement(TrnBillMvmnt lObjBillMvmntVO, Map<String, Object> inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		BillMovementDAOImpl lObjMvmntDAO = new BillMovementDAOImpl(TrnBillMvmnt.class, srvcLoc.getSessionFactory());

		try {
			if (lObjBillMvmntVO.getBillMvmtId() > 0) {
				lObjMvmntDAO.update(lObjBillMvmntVO);
			} else {
				BptmCommonServiceImpl.insertMovement(lObjBillMvmntVO, inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
	}

	public void saveBillParty(RltBillParty lObjBillParty, Map<String, Object> inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		RltBillPartyDAO lObjBillPartyDAO = new RltBillPartyDAOImpl(RltBillParty.class, srvcLoc.getSessionFactory());
		Long lLngBillPartyId = null;

		try {
			lLngBillPartyId = IFMSCommonServiceImpl.getNextSeqNum("rlt_bill_party", inputMap);
			lObjBillParty.setBillPartyId(lLngBillPartyId);
			lObjBillPartyDAO.create(lObjBillParty);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
	}

	public void getMonthlyReport(Map inputMap) throws Exception {

		int iPageNo = 1;
		int iRowNo = 0;

		// BigDecimal lTotalBf1936 = BigDecimal.ZERO;
		// BigDecimal lTotalAf1936 = BigDecimal.ZERO;
		// BigDecimal lTotalAf1956 = BigDecimal.ZERO;
		// BigDecimal lTotalAf1960 = BigDecimal.ZERO;
		Long lLngBillNo = null;
		Long lLngChangeRqstId = null;
		BigDecimal lTotalAllnBf11136 = BigDecimal.ZERO;
		BigDecimal lTotalgetAllnBf11156 = BigDecimal.ZERO;
		BigDecimal lTotalAllnAf11156 = BigDecimal.ZERO;
		BigDecimal lTotalAllnAf10560 = BigDecimal.ZERO;
		BigDecimal lTotalAllnAfZp = BigDecimal.ZERO;
		BigDecimal lTotalDpPercentAmount = BigDecimal.ZERO;
		BigDecimal lTotalTiPercentAmount = BigDecimal.ZERO;
		BigDecimal lTotalZP_IR1 = BigDecimal.ZERO;
		BigDecimal lTotalDP_IR2 = BigDecimal.ZERO;
		BigDecimal lTotalRelf_IR3 = BigDecimal.ZERO;
		BigDecimal lTotalDiffAmt = BigDecimal.ZERO;
		BigDecimal lTotalGross = BigDecimal.ZERO;
		BigDecimal lTotalRcvryAmt = BigDecimal.ZERO;
		BigDecimal lTotalNetAmt = BigDecimal.ZERO;
		BigDecimal lTotalOtherArreaAmt = BigDecimal.ZERO;

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		MonthlyPensionBillVO lObjMonthlyPensionBillVO = new MonthlyPensionBillVO();
		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		String lStrLocationName = null;
		LocationDAOImpl lObjLocationDAOImpl = new LocationDAOImpl(CmnLocationMst.class, srvcLoc.getSessionFactory());
		lStrLocationName = lObjLocationDAOImpl.getLocationName(gStrLocCode, gLngLangId.toString());
		StringBuilder sLine = new StringBuilder();
		Map<BigDecimal, String> lMapHeadCodeSeries = new HashMap<BigDecimal, String>();
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
		lMapHeadCodeSeries = lObjCommonPensionDAO.getAllHeadCodeSeriesMap();
		String lStrSortBy = (String) inputMap.get("sortBy");
		for (int i = 0; i <= 130; i++) {
			sLine.append("-");
		}

		StringBuilder sHeading = new StringBuilder();
		String lStrForMnthYear = null;
		if (inputMap.containsKey("MonthName") && inputMap.containsKey("ForBillYear")) {
			lStrForMnthYear = inputMap.get("MonthName") + inputMap.get("ForBillYear").toString();
		}
		String lStrBankName = null;
		String lStrBranchName = null;
		if (inputMap.containsKey("BankName") && inputMap.containsKey("BranchName")) {
			lStrBankName = inputMap.get("BankName").toString();
			lStrBranchName = inputMap.get("BranchName").toString();
		}
		sHeading.append("2071 PENSION AND O.R.B. TYPE BANK BRANCH AND SUBTYPE 5REV/6REV FOR THE MONTH OF " + lStrForMnthYear + "      Page No : # \n");
		sHeading.append("Name of Bank : " + lStrBankName + "   Branch: " + lStrBranchName + " Treasury : " + lStrLocationName);

		StringBuilder sHeadingCol = new StringBuilder();

		// sHeadingCol
		// .append("\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
		// sHeadingCol
		// .append(" SR  | Category          | Name of         |  AC NO        | Before  | After    | After   | After    | Zilla    | D.P.    | Relf.    | Diff    | Gross     | Recov.    | Net      |\n");
		// sHeadingCol
		// .append(" NO  | PPO NO            | The             |               | 1 Apr   | 1 Apr    | 1 Nov   | 1 May    | Parishad |    &    |   &      | Amount. | DA + I.R.)| Major Hd  | Payable  |\n");
		// sHeadingCol
		// .append("     | Volume & Page     | Pensioner       |               | 1936    | 1936 &   | 1956 &  | 1960     |& I.R.-1  | I.R.-2  | I.R.-3   |         | If any    |           |          |\n");
		// sHeadingCol
		// .append("     |                   |                 |               |         | Prior    | Prior   |          |          |         |          |         |           |           |          |\n");
		// sHeadingCol
		// .append("     |                   |                 |               |         | 1 Nov    | 1 May   |          |          |         |          |         |           |           |          |\n");
		// sHeadingCol
		// .append("     |                   |                 |               |         | 1956     | 1960    |          |          |         |          |         |           |           |          |\n");
		// sHeadingCol
		// .append("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n");

		sHeadingCol.append("\n-----------------------------------------------------------------------------------------------------------------------------------\n");

		sHeadingCol.append("SR  | Category      |  Name Of    |   AC NO      | Before|After  |After |After |Zilla | D.P. | Relf. |Diff. |Gross  |Recov.|Net\n");

		sHeadingCol.append("NO  | PPO NO        |  The        |              | 1 Apr | 1 Apr |1 Nov |1 May |Pari- |  &   | & I.R.|Amount|(DA +  |Major |Payable\n");

		sHeadingCol.append("    | Volume & Page |  Pensioner  |              | 1936  |1936 & |1956 &|1960  | shad | I.R. | -3    |      |I.R.   |Head  |\n");

		sHeadingCol.append("    |               |             |              |       |prior  |prior |      |  &   | -2   |       |      |if any |      |\n");

		sHeadingCol.append("    |               |             |              |       |1 Nov  |1 May |      | I.R. |      |       |      |       |      |\n");

		sHeadingCol.append("    |               |             |              |       |1956   |1960  |      |  -1  |      |       |      |       |      |\n");

		sHeadingCol.append("-----------------------------------------------------------------------------------------------------------------------------------\r\n");

		StringBuilder sbOut = new StringBuilder();
		sbOut.append("<div><pre>");
		sbOut.append(sHeading.toString().replace("#", "" + iPageNo));
		sbOut.append(sHeadingCol.toString());

		// System.out.println(sbOut);

		int[] arrWidth = {5, 16, 14, 14, 8, 8, 7, 7, 7, 7, 8, 7, 8, 7, 8};
		// int[] arrWidth = {5, 20, 18, 14, 10, 11, 10, 11, 11, 10, 11, 10, 12,
		// 12, 11};
		String[] arrAlignment = {"-", "-", "-", "", "", "", "", "", "", "", "", "", "", "", ""};

		// ---If BillNo exist then getting details from bill tables by
		// billNo(Print Monthly Bill Report)
		if (inputMap.containsKey("billNo")) {
			String lStrBillNo = inputMap.get("billNo").toString();
			lLngBillNo = Long.parseLong(lStrBillNo);
			lLstMonthlyPensionBillVO = lObjMonthlyPensionBillDAO.getMonthlyPensionBillVOList(lLngBillNo, gStrLocCode, lStrSortBy);
		}
		// ---If changeRqstId exist then getting details from change statement
		// tables by changeRqstId(Generate Monthly Bill)
		if (inputMap.containsKey("changeRqstId")) {
			String lStrChangeRqstId = inputMap.get("changeRqstId").toString();
			lLngChangeRqstId = Long.parseLong(lStrChangeRqstId);
			lLstMonthlyPensionBillVO = lObjMonthlyPensionBillDAO.getMonthlyPensionBillVOListByChngStmntId(lLngChangeRqstId, gStrLocCode, lStrSortBy);
		}
		//Print Monthly bill report from bankcode and branchcode
		if (inputMap.containsKey("MonthlyPensionBillVO")) {
			lLstMonthlyPensionBillVO = (List<MonthlyPensionBillVO>) inputMap.get("MonthlyPensionBillVO");
		}
		
		// lLstMonthlyPensionBillVO = (List<MonthlyPensionBillVO>)
		// inputMap.get("MonthlyPensionList");

		String lStrPensionerName = "";
		String[] lArrPensionerName = null;
		for (int i = 0; i < lLstMonthlyPensionBillVO.size(); i++) {
			StringBuilder sbRowData = new StringBuilder();
			lObjMonthlyPensionBillVO = lLstMonthlyPensionBillVO.get(i);
			String lStrFirstName = " ";
			String lStrMiddleName = " ";
			String lStrLastName = " ";

			String lStrFName = " ";
			String lStrMName = " ";
			String lStrLName = " ";
			int lIntFirstNameLength = 0;
			int lIntMidNameLength = 0;
			int lIntLastNameLength = 0;
			// -------------------------------------------------------------------------------------------------
			// String lStrCategory = lInner.get(1);
			// String lStrNameofPensioner = lInner.get(2);

			// String lArrStrCategory = WordUtils.wrap(lStrCategory, 17, "\n",
			// false);
			// String[] lArrCategory = lArrStrCategory.split("\\n");

			// String lArrStrNameofPensioner =
			// WordUtils.wrap(lStrNameofPensioner, 15, "\n", false);
			// String[] lArrNameofPensioner =
			// lArrStrNameofPensioner.split("\\n");

			lStrPensionerName = lObjMonthlyPensionBillVO.getPensionerName();
			if (lStrPensionerName != null) {
				lArrPensionerName = lStrPensionerName.split(" ");
				if (lArrPensionerName.length > 0) {
					if (lArrPensionerName.length < 2) {

						lStrFirstName = lArrPensionerName[0];
						lStrMiddleName = " ";
						lStrLastName = " ";

						lIntFirstNameLength = lStrFirstName.length();
						lIntMidNameLength = lStrMiddleName.length();
						lIntLastNameLength = lStrLastName.length();
					} else if (lArrPensionerName.length < 3) {
						lStrFirstName = lArrPensionerName[0];
						lStrMiddleName = lArrPensionerName[1];
						lStrLastName = " ";

						lIntFirstNameLength = lStrFirstName.length();
						lIntMidNameLength = lStrMiddleName.length();
						lIntLastNameLength = lStrLastName.length();
					} else if (lArrPensionerName.length == 3) {
						lStrFirstName = lArrPensionerName[0];
						lStrMiddleName = lArrPensionerName[1];
						lStrLastName = lArrPensionerName[2];

						lIntFirstNameLength = lStrFirstName.length();
						lIntMidNameLength = lStrMiddleName.length();
						lIntLastNameLength = lStrLastName.length();
					} else if (lArrPensionerName.length > 3) {
						// ---If there are more than two spaces then concating
						// all the fragments to first name except last two.(This
						// is due to some pensioner have Shri/Smt. etc in their
						// name.)
						for (int l = 0; l < lArrPensionerName.length; l++) {
							if (l == (lArrPensionerName.length - 2)) {
								lStrMiddleName = lArrPensionerName[l];
							} else if (l == (lArrPensionerName.length - 1)) {
								lStrLastName = lArrPensionerName[l];
							} else {
								if (lArrPensionerName[l].trim().length() > 0) {
									if (lStrFirstName.trim().length() > 0) {
										lStrFirstName = lStrFirstName + " " + lArrPensionerName[l];
									} else {
										lStrFirstName = lArrPensionerName[l];
									}

								}
							}
						}
						lIntFirstNameLength = lStrFirstName.length();
						lIntMidNameLength = lStrMiddleName.length();
						lIntLastNameLength = lStrLastName.length();
					}
				}
			}

			if (lIntFirstNameLength > 14) {
				lStrFName = lStrFirstName;
				lStrFirstName = lStrFirstName.substring(0, 13);
			}
			if (lIntMidNameLength > 14) {
				lStrMName = lStrMiddleName;
				lStrMiddleName = lStrMiddleName.substring(0, 13);
			}
			if (lIntLastNameLength > 14) {
				lStrLName = lStrLastName;
				lStrLastName = lStrLastName.substring(0, 13);
			}
			String[] lRow1 = {i + 1 + " ", lMapHeadCodeSeries.get(lObjMonthlyPensionBillVO.getHeadCode()) + "", lStrFirstName, lObjMonthlyPensionBillVO.getAccountNo(),
					lObjMonthlyPensionBillVO.getAllnBf11136() + " ", lObjMonthlyPensionBillVO.getAllnBf11156() + " ", lObjMonthlyPensionBillVO.getAllnAf11156() + "",
					lObjMonthlyPensionBillVO.getAllnAf10560() + " ", lObjMonthlyPensionBillVO.getAllnAfZp() + " ", lObjMonthlyPensionBillVO.getDpPercentAmount() + " ",
					lObjMonthlyPensionBillVO.getTiPercentAmount() + " ", lObjMonthlyPensionBillVO.getTotalArrearAmt() + " ", lObjMonthlyPensionBillVO.getGrossAmount() + " ",
					lObjMonthlyPensionBillVO.getRecoveryAmount() + "", lObjMonthlyPensionBillVO.getNetPensionAmount() + " "};
			if (lIntFirstNameLength > 14) {
				lStrFirstName = lStrFName.substring(14);

			}
			String[] lRow4 = {"", "", lStrFirstName, " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "};
			String[] lRow2 = {" ", lObjMonthlyPensionBillVO.getPpoNo(), lStrMiddleName, " ", " ", " ", " ", " ", lObjMonthlyPensionBillVO.getIr1Amt() + " ",
					lObjMonthlyPensionBillVO.getIr2Amt() + " ", lObjMonthlyPensionBillVO.getIr3Amt() + " ", " ", " ", "0", " "};
			if (lIntMidNameLength > 14) {
				lStrMiddleName = lStrMName.substring(14);

			}
			String[] lRow5 = {"", "", lStrMiddleName, " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "};
			String[] lRow3 = {
					" ",
					((lObjMonthlyPensionBillVO.getLedgerNo() != null) ? lObjMonthlyPensionBillVO.getLedgerNo() : 0) + "/"
							+ ((lObjMonthlyPensionBillVO.getPageNo() != null) ? lObjMonthlyPensionBillVO.getPageNo() : 0), lStrLastName, " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", "0", " "};
			if (lIntLastNameLength > 14) {
				lStrLastName = lStrLName.substring(14);
			}
			String[] lRow6 = {"", "", lStrLastName, " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "};
			// -------------------------------------------------------------------------------------------------

			lTotalAllnBf11136 = lTotalAllnBf11136.add(new BigDecimal(lObjMonthlyPensionBillVO.getAllnBf11136()));
			lTotalgetAllnBf11156 = lTotalgetAllnBf11156.add(new BigDecimal(lObjMonthlyPensionBillVO.getAllnBf11156()));
			lTotalAllnAf11156 = lTotalAllnAf11156.add(new BigDecimal(lObjMonthlyPensionBillVO.getAllnAf11156()));
			lTotalAllnAf10560 = lTotalAllnAf10560.add(new BigDecimal(lObjMonthlyPensionBillVO.getAllnAf10560()));
			lTotalAllnAfZp = new BigDecimal(lObjMonthlyPensionBillVO.getAllnAfZp());
			lTotalDpPercentAmount = new BigDecimal(lObjMonthlyPensionBillVO.getDpPercentAmount());
			lTotalTiPercentAmount = new BigDecimal(lObjMonthlyPensionBillVO.getTiPercentAmount());
			lTotalZP_IR1 = lTotalAllnAfZp.add(lTotalZP_IR1.add(new BigDecimal(lObjMonthlyPensionBillVO.getIr1Amt())));
			lTotalDP_IR2 = lTotalDpPercentAmount.add(lTotalDP_IR2.add(new BigDecimal(lObjMonthlyPensionBillVO.getIr2Amt())));
			lTotalRelf_IR3 = lTotalTiPercentAmount.add(lTotalRelf_IR3.add(new BigDecimal(lObjMonthlyPensionBillVO.getIr3Amt())));
			// lTotalOtherArreaAmt = lTotalOtherArreaAmt.add(new
			// BigDecimal(lObjMonthlyPensionBillVO.getOtherArrearsAmount()));
			lTotalDiffAmt = lTotalDiffAmt.add(new BigDecimal(lObjMonthlyPensionBillVO.getTotalArrearAmt()));
			lTotalGross = lTotalGross.add(new BigDecimal(lObjMonthlyPensionBillVO.getGrossAmount()));
			lTotalRcvryAmt = lTotalRcvryAmt.add(new BigDecimal(lObjMonthlyPensionBillVO.getRecoveryAmount()));
			lTotalNetAmt = lTotalNetAmt.add(new BigDecimal(lObjMonthlyPensionBillVO.getNetPensionAmount()));

			lTotalDpPercentAmount = BigDecimal.ZERO;
			lTotalTiPercentAmount = BigDecimal.ZERO;
			lTotalOtherArreaAmt = BigDecimal.ZERO;

			sbRowData.append(getRowData(arrWidth, arrAlignment, lRow1));
			iRowNo++;
			if ((iRowNo != 0) && (iRowNo % 52 == 0)) {
				iPageNo++;
				sbOut.append("\n" + sLine);
				sbOut.append((char) 12);

				sbOut.append("\n" + sHeading.toString().replace("#", "" + iPageNo));
				sbOut.append(sHeadingCol.toString());

			}
			if (lIntFirstNameLength > 14) {

				sbRowData.append(getRowData(arrWidth, arrAlignment, lRow4));
				iRowNo++;
				if ((iRowNo != 0) && (iRowNo % 52 == 0)) {
					iPageNo++;
					sbOut.append("\n" + sLine);
					sbOut.append((char) 12);

					sbOut.append("\n" + sHeading.toString().replace("#", "" + iPageNo));
					sbOut.append(sHeadingCol.toString());

				}
			}
			sbRowData.append(getRowData(arrWidth, arrAlignment, lRow2));
			iRowNo++;
			if ((iRowNo != 0) && (iRowNo % 52 == 0)) {
				iPageNo++;
				sbOut.append("\n" + sLine);
				sbOut.append((char) 12);

				sbOut.append("\n" + sHeading.toString().replace("#", "" + iPageNo));
				sbOut.append(sHeadingCol.toString());
			}
			if (lIntMidNameLength > 14) {
				sbRowData.append(getRowData(arrWidth, arrAlignment, lRow5));
				iRowNo++;
				if ((iRowNo != 0) && (iRowNo % 52 == 0)) {
					iPageNo++;
					sbOut.append("\n" + sLine);
					sbOut.append((char) 12);

					sbOut.append("\n" + sHeading.toString().replace("#", "" + iPageNo));
					sbOut.append(sHeadingCol.toString());

				}
			}
			iRowNo++;
			sbRowData.append(getRowData(arrWidth, arrAlignment, lRow3));
			if ((iRowNo != 0) && (iRowNo % 52 == 0)) {
				iPageNo++;
				sbOut.append("\n" + sLine);
				sbOut.append((char) 12);

				sbOut.append("\n" + sHeading.toString().replace("#", "" + iPageNo));
				sbOut.append(sHeadingCol.toString());
			}
			if (lIntLastNameLength > 14) {
				sbRowData.append(getRowData(arrWidth, arrAlignment, lRow6));
				iRowNo++;
				if ((iRowNo != 0) && (iRowNo % 52 == 0)) {
					iPageNo++;
					sbOut.append("\n" + sLine);
					sbOut.append((char) 12);

					sbOut.append("\n" + sHeading.toString().replace("#", "" + iPageNo));
					sbOut.append(sHeadingCol.toString());

				}
			}
			sbOut.append(sbRowData);
		}

		/*** Total Row **/
		Object[] lTotal = {" ", "Total", " ", " ", lTotalAllnBf11136, lTotalgetAllnBf11156, lTotalAllnAf11156, lTotalAllnAf10560, lTotalZP_IR1, lTotalDP_IR2, lTotalRelf_IR3, lTotalDiffAmt,
				lTotalGross, lTotalRcvryAmt, lTotalNetAmt};
		iRowNo++;
		if ((52 - (iRowNo % 52)) > 3) // There is still 3 rows can accommodate
		// at end of the page
		{

			sbOut.append("\n" + sLine + "\n");
			sbOut.append(getRowData(arrWidth, arrAlignment, lTotal));
			sbOut.append(sLine);

		} else {
			iPageNo++;
			sbOut.append((char) 12);

			sbOut.append("\n" + sHeading.toString().replace("#", "" + iPageNo));
			sbOut.append(sHeadingCol.toString());

			sbOut.append("\n" + sLine + "\n");
			sbOut.append(getRowData(arrWidth, arrAlignment, lTotal));
			sbOut.append(sLine);
		}
		sbOut.append((char) 12);
		sbOut.append("</pre></div>");
		// System.out.print(sbOut);
		sbOut.append(getSummary(inputMap));
		inputMap.put("PrintString", sbOut.toString());

	}

	public StringBuilder getRowData(int[] arrWidth, String[] arrAlignment, Object[] lLstData) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < arrWidth.length; i++) {
			sb.append(String.format("%" + arrAlignment[i] + arrWidth[i] + "s", lLstData[i]));
		}

		sb.append("\n");
		return sb;
	}

	public StringBuilder getSummary(Map inputMap) throws Exception {

		int iPageNo = 1;
		int iRowNo = 0;
		Long lLngBillNo = null;
		Long lLngChangeRqstId = null;
		Long lTotalNoPensioners = 0L;
		BigDecimal lTotalAllnBf11136 = BigDecimal.ZERO;
		BigDecimal lTotalgetAllnBf11156 = BigDecimal.ZERO;
		BigDecimal lTotalAllnAf11156 = BigDecimal.ZERO;
		BigDecimal lTotalAllnAf10560 = BigDecimal.ZERO;
		BigDecimal lTotalAllnAfZp = BigDecimal.ZERO;
		BigDecimal lTotalDpPercentAmount = BigDecimal.ZERO;
		BigDecimal lTotalTiPercentAmount = BigDecimal.ZERO;
		BigDecimal lTotalZP_IR1 = BigDecimal.ZERO;
		BigDecimal lTotalDP_IR2 = BigDecimal.ZERO;
		BigDecimal lTotalRelf_IR3 = BigDecimal.ZERO;
		BigDecimal lTotalDiffAmt = BigDecimal.ZERO;
		BigDecimal lTotalGross = BigDecimal.ZERO;
		BigDecimal lTotalNetAmt = BigDecimal.ZERO;
		BigDecimal lTotalOtherArreaAmt = BigDecimal.ZERO;
		BigDecimal lTotalRecoveryAmount = BigDecimal.ZERO;
		StringBuilder sLine = new StringBuilder();
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		Map<BigDecimal, String> lMapHeadCodeSeries = new HashMap<BigDecimal, String>();
		StringBuilder sbOut = new StringBuilder();
		try {
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
			lMapHeadCodeSeries = lObjCommonPensionDAO.getAllHeadCodeSeriesMap();
			for (int i = 0; i < 131; i++) {
				sLine.append("-");
			}

			String lStrLocationName = null;
			LocationDAOImpl lObjLocationDAOImpl = new LocationDAOImpl(CmnLocationMst.class, srvcLoc.getSessionFactory());
			lStrLocationName = lObjLocationDAOImpl.getLocationName(gStrLocCode, gLngLangId.toString());

			String lStrForMnthYear = null;
			if (inputMap.containsKey("MonthName") && inputMap.containsKey("ForBillYear")) {
				lStrForMnthYear = inputMap.get("MonthName") + " " + inputMap.get("ForBillYear").toString();
			}
			String lStrBankName = null;
			String lStrBranchName = null;
			if (inputMap.containsKey("BankName") && inputMap.containsKey("BranchName")) {
				lStrBankName = inputMap.get("BankName").toString();
				lStrBranchName = inputMap.get("BranchName").toString();
			}

			List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			if (inputMap.containsKey("billNo")) {
				String lStrBillNo = inputMap.get("billNo").toString();
				lLngBillNo = Long.parseLong(lStrBillNo);
				lLstMonthlyPensionBillVO = lObjMonthlyPensionBillDAO.getMonthlyPensionBillSummary(lLngBillNo, gStrLocCode);
			}
			if (inputMap.containsKey("changeRqstId")) {
				String lStrChangeRqstId = inputMap.get("changeRqstId").toString();
				lLngChangeRqstId = Long.parseLong(lStrChangeRqstId);
				lLstMonthlyPensionBillVO = lObjMonthlyPensionBillDAO.getMonthlyPensionBillSummaryByChngStmntId(lLngChangeRqstId, gStrLocCode);
			}
			//print monthly bill report
			if (inputMap.containsKey("MonthlyPensionBillVO")) {
				String lStrBankCode = (String) inputMap.get("BankCode");
				String lStrBranchCode = (String) inputMap.get("BranchCode");
				String lStrForMonthYear = (String) inputMap.get("ForMonthYear");
				lLstMonthlyPensionBillVO  = lObjMonthlyPensionBillDAO.getMonthlyPensionBillBankBranchSummary(lStrBankCode, lStrBranchCode, lStrForMonthYear, gStrLocCode);
			}
			// lLstMonthlyPensionBillVO = (List<MonthlyPensionBillVO>)
			// inputMap.get("MonthlyPensionList");

			StringBuilder sHeading = new StringBuilder();
			sHeading.append("\nCATEGORY- WISE DETAILS FOR THE MONTH OF " + lStrForMnthYear + "\n");
			sHeading.append("Name of Bank : " + lStrBankName + "   Branch: " + lStrBranchName + " Treasury : " + lStrLocationName);

			StringBuilder sHeadingCol = new StringBuilder();

			// sHeadingCol.append("\n-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
			// sHeadingCol.append(" Category                    | No. | Before  | After   | After   | After   | Zilla   | D.P.    | Relf.   | Diff    | Gross     | Recovery| Net      |\n");
			// sHeadingCol.append(" PPO NO                      | of  | Apr     | 1 Apr   | 1 Nov   | 1 May   | Parishad|    &    |   &     | Amount. | DA + I.R.)| Major Hd| Payable  |\n");
			// sHeadingCol.append(" Volume & Page               |Penrs| 1936    | 1936 &  | 1956 &  | 1960    |& I.R.-1 | I.R.-2  | I.R.-3  |         | If any    |         |          |\n");
			// sHeadingCol.append("                             |     |         | Prior   | Prior   |         |         |         |         |         |           |         |          |\n");
			// sHeadingCol.append("                             |     |         | 1 Nov   | 1 May   |         |         |         |         |         |           |         |          |\n");
			// sHeadingCol.append("                             |     |         | 1956    | 1960    |         |         |         |         |         |           |         |          |\n");
			// sHeadingCol.append("-----------------------------------------------------------------------------------------------------------------------------------------------------\r\n");

			sHeadingCol.append("\n-----------------------------------------------------------------------------------------------------------------------------------\n");
			sHeadingCol.append(" Category            | No. | Before  | After   | After   | After  | Zilla  | D.P.  | Relf. | Diff   | Gross    | Recovery| Net    \n");
			sHeadingCol.append(" Name                | of  | Apr     | 1 Apr   | 1 Nov   | 1 May  |Parishad|  &    |   &   | Amount.| DA + I.R.| Major Hd| Payable\n");
			sHeadingCol.append("                     |Penrs| 1936    | 1936 &  | 1956 &  | 1960   |& I.R.1 | I.R.2 | I.R.3 |        | If any   |         |        \n");
			sHeadingCol.append("                     |     |         | Prior   | Prior   |        |        |       |       |        |          |         |        \n");
			sHeadingCol.append("                     |     |         | 1 Nov   | 1 May   |        |        |       |       |        |          |         |        \n");
			sHeadingCol.append("                     |     |         | 1956    | 1960    |        |        |       |       |        |          |         |        \n");
			sHeadingCol.append("-----------------------------------------------------------------------------------------------------------------------------------\r\n");

			sbOut.append("<div><pre>");
			sbOut.append(sHeading.toString());
			sbOut.append(sHeadingCol.toString());

			new ArrayList<String>();

			int[] arrWidth = {22, 6, 10, 10, 10, 9, 9, 8, 8, 9, 11, 10, 9};
			// int[] arrWidth = {30, 5, 9, 10, 10, 10, 12, 10, 10, 10, 12, 10,
			// 12};
			String[] arrAlignment = {"-", "", "", "", "", "", "", "", "", "", "", "", ""};

			MonthlyPensionBillVO lObjMonthlyPensionBillVO = new MonthlyPensionBillVO();
			for (int i = 0; i < lLstMonthlyPensionBillVO.size(); i++) {
				StringBuilder sbRowData = new StringBuilder();
				lObjMonthlyPensionBillVO = lLstMonthlyPensionBillVO.get(i);
				String[] lRow1 = {"" + lObjMonthlyPensionBillVO.getMainCatDesc(), lObjMonthlyPensionBillVO.getTotalPensioners() + "", lObjMonthlyPensionBillVO.getAllnBf11136() + "",
						lObjMonthlyPensionBillVO.getAllnBf11156() + "", lObjMonthlyPensionBillVO.getAllnAf11156() + "", lObjMonthlyPensionBillVO.getAllnAf10560() + "",
						lObjMonthlyPensionBillVO.getAllnAfZp() + "", lObjMonthlyPensionBillVO.getDpPercentAmount() + "", lObjMonthlyPensionBillVO.getTiPercentAmount() + "",
						lObjMonthlyPensionBillVO.getTotalArrearAmt() + "", lObjMonthlyPensionBillVO.getGrossAmount() + "", lObjMonthlyPensionBillVO.getRecoveryAmount() + "",
						lObjMonthlyPensionBillVO.getNetPensionAmount() + ""};
				String[] lRow2 = {"", "", "", "", "", "", lObjMonthlyPensionBillVO.getIr1Amt() + "", lObjMonthlyPensionBillVO.getIr2Amt() + "", lObjMonthlyPensionBillVO.getIr3Amt() + "", "", "", "0",
						""};
				//
				// -------------------------------------------------------------------------------------------------
				lTotalNoPensioners = lTotalNoPensioners + lObjMonthlyPensionBillVO.getTotalPensioners();
				lTotalAllnBf11136 = lTotalAllnBf11136.add(new BigDecimal(lObjMonthlyPensionBillVO.getAllnBf11136()));
				lTotalgetAllnBf11156 = lTotalgetAllnBf11156.add(new BigDecimal(lObjMonthlyPensionBillVO.getAllnBf11156()));
				lTotalAllnAf11156 = lTotalAllnAf11156.add(new BigDecimal(lObjMonthlyPensionBillVO.getAllnAf11156()));
				lTotalAllnAf10560 = lTotalAllnAf10560.add(new BigDecimal(lObjMonthlyPensionBillVO.getAllnAf10560()));
				lTotalAllnAfZp = new BigDecimal(lObjMonthlyPensionBillVO.getAllnAfZp());
				lTotalDpPercentAmount = new BigDecimal(lObjMonthlyPensionBillVO.getDpPercentAmount());
				lTotalTiPercentAmount = new BigDecimal(lObjMonthlyPensionBillVO.getTiPercentAmount());
				lTotalZP_IR1 = lTotalAllnAfZp.add(lTotalZP_IR1.add(new BigDecimal(lObjMonthlyPensionBillVO.getIr1Amt())));
				lTotalDP_IR2 = lTotalDpPercentAmount.add(lTotalDP_IR2.add(new BigDecimal(lObjMonthlyPensionBillVO.getIr2Amt())));
				lTotalRelf_IR3 = lTotalTiPercentAmount.add(lTotalRelf_IR3.add(new BigDecimal(lObjMonthlyPensionBillVO.getIr3Amt())));

				// lTotalOtherArreaAmt = lTotalOtherArreaAmt.add(new
				// BigDecimal(lObjMonthlyPensionBillVO.getOtherArrearsAmount()));
				lTotalDiffAmt = lTotalDiffAmt.add(new BigDecimal(lObjMonthlyPensionBillVO.getTotalArrearAmt()));
				lTotalGross = lTotalGross.add(new BigDecimal(lObjMonthlyPensionBillVO.getGrossAmount()));
				lTotalNetAmt = lTotalNetAmt.add(new BigDecimal(lObjMonthlyPensionBillVO.getNetPensionAmount()));
				lTotalRecoveryAmount = lTotalRecoveryAmount.add(new BigDecimal(lObjMonthlyPensionBillVO.getRecoveryAmount()));

				sbRowData.append(getRowData(arrWidth, arrAlignment, lRow1));
				iRowNo++;
				if ((iRowNo != 0) && (iRowNo % 52 == 0)) {
					iPageNo++;
					sbOut.append("\n" + sLine);
					sbOut.append((char) 12);

					sbOut.append("\n" + sHeading.toString());
					sbOut.append(sHeadingCol.toString());

				}

				sbRowData.append(getRowData(arrWidth, arrAlignment, lRow2));
				iRowNo++;
				if ((iRowNo != 0) && (iRowNo % 52 == 0)) {
					iPageNo++;
					sbOut.append("\n" + sLine);
					sbOut.append((char) 12);

					sbOut.append("\n" + sHeading.toString());
					sbOut.append(sHeadingCol.toString());
				}

				sbOut.append(sbRowData);
			}
			/*** Total Row **/
			Object[] lTotal = {" Category Total", lTotalNoPensioners, lTotalAllnBf11136, lTotalgetAllnBf11156, lTotalAllnAf11156, lTotalAllnAf10560, lTotalZP_IR1, lTotalDP_IR2, lTotalRelf_IR3,
					lTotalDiffAmt, lTotalGross, lTotalRecoveryAmount, lTotalNetAmt};
			iRowNo++;
			if ((52 - (iRowNo % 52)) > 3) // There is still 3 rows can
			// accommodate
			// at end of the page
			{

				sbOut.append(sLine + "\n");
				sbOut.append(getRowData(arrWidth, arrAlignment, lTotal));
				sbOut.append(sLine);

			} else {
				iPageNo++;
				sbOut.append((char) 12);

				sbOut.append("\n" + sHeading.toString());
				sbOut.append(sHeadingCol.toString());

				sbOut.append("\n" + sLine + "\n");
				sbOut.append(getRowData(arrWidth, arrAlignment, lTotal));
				sbOut.append(sLine);
			}

			String[] arrAlignmentSummary = {"-", "", "", "", "", "", "-", "", "", "", "", "", ""};
			int[] arrWidthSummary = {20, 15, 3, 2, 5, 5, 32, 1, 1, 12, 12, 1, 1};
			StringBuilder sHeadingCol2 = new StringBuilder();
			sHeadingCol2.append("\n\n\n                    NO OF PENSIONER                CATEGORY                                AMOUNT\n");
			sbOut.append(sHeadingCol2.toString());

			for (int i = 0; i < lLstMonthlyPensionBillVO.size(); i++) {
				StringBuilder sbRowData = new StringBuilder();
				lObjMonthlyPensionBillVO = lLstMonthlyPensionBillVO.get(i);
				// String[] lRow3 = {"",
				// lObjMonthlyPensionBillVO.getTotalPensioners() + "", "", "",
				// "", "", " " +
				// lMapHeadCodeSeries.get(lObjMonthlyPensionBillVO.getHeadCode()),
				// "", "Rs.", "",
				// lObjMonthlyPensionBillVO.getNetPensionAmount() + " ", "",
				// ""};

				String[] lRow3 = {"", lObjMonthlyPensionBillVO.getTotalPensioners() + "", "", "", "", "", " " + lObjMonthlyPensionBillVO.getMainCatDesc(), "", "Rs.",
						lObjMonthlyPensionBillVO.getNetPensionAmount() + " ", "", "", ""};

				sbRowData.append(getRowData(arrWidthSummary, arrAlignmentSummary, lRow3));
				iRowNo++;
				if ((iRowNo != 0) && (iRowNo % 52 == 0)) {
					iPageNo++;
					sbOut.append("\n" + sLine);
					sbOut.append((char) 12);

					sbOut.append("\n" + sHeading.toString());
					sbOut.append(sHeadingCol.toString());
				}

				sbOut.append(sbRowData);
			}
			arrWidthSummary[6] = 28;
			Object[] lRow4 = {"", "", "", "", "", "", "", "Total", "Rs.", lTotalNetAmt + " ", "", "", ""};

			if ((52 - (iRowNo % 52)) > 3) // There is still 3 rows can
			// accommodate
			// at end of the page
			{

				sbOut.append(sLine + "\n");
				sbOut.append(getRowData(arrWidthSummary, arrAlignmentSummary, lRow4));
				sbOut.append(sLine);

			} else {
				iPageNo++;
				sbOut.append((char) 12);

				sbOut.append("\n" + sHeading.toString());
				sbOut.append(sHeadingCol.toString());

				sbOut.append("\n" + sLine + "\n");
				sbOut.append(getRowData(arrWidthSummary, arrAlignmentSummary, lTotal));
				sbOut.append(sLine);
			}

			String sAmtInWords = getChar(20, " ") + "PASSED FOR PAYMENT AND PAY RS. " + lTotalNetAmt + "/-  IN WORDS";
			String sAmtInWords1 = getChar(20, " ") + "RUPEES " + EnglishDecimalFormat.convertWithSpace(lTotalNetAmt).toUpperCase() + " ONLY";

			String sFooter = getChar(40, " ") + "ASST. PAY AND ACCOUNT OFFICER,PAO MUMBAI";
			String sFooter1 = getChar(40, " ") + "ADDL. / TREASURY OFFICER";

			String sFooter2 = getChar(15, " ") + "CHEQUE NO : ";
			String sFooter3 = getChar(15, " ") + "DRAWN  NO : ";

			String sFooter4 = getChar(50, " ") + "ASST. PAY AND ACCOUNT OFFICER(CHQ),PAO MUMBAI";
			String sFooter5 = getChar(20, " ") + "DY/HEAD ACCT.		  ADDL./TREASURY OFFICER";

			String sFooter6 = getChar(17, " ") + "DATE : " + lObjSimpleDateFormat.format(gDate);

			sbOut.append("\n\n\n" + sAmtInWords);
			sbOut.append("\n" + sAmtInWords1);

			sbOut.append("\n\n\n\n" + sFooter);
			sbOut.append("\n" + sFooter1);

			sbOut.append("\n\n\n" + sFooter2);
			sbOut.append("\n" + sFooter3);

			sbOut.append("\n\n\n\n" + sFooter4);
			sbOut.append("\n" + sFooter5);

			sbOut.append("\n\n" + sFooter6);

			sbOut.append("</pre></div>");
			// System.out.print(sbOut);
			inputMap.put("PrintStringSummary", sbOut.toString());
		} catch (Exception e) {
			gLogger.error("Error while approving of the change statement..." + e, e);
			throw e;
		}
		return sbOut;
	}

	public ResultObject forwardChangeStatement(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		String lRqstStrChngStmntRqstId = null;
		String[] lArrChngStmntRqstId = null;
		ChangeStatementDAO lObjChangeStatementDAO = null;
		TrnMonthlyChangeRqst lObjTrnMonthlyChangeRqst = null;
		StringBuilder lSBStatus = new StringBuilder();
		try {
			setSessionInfo(inputMap);
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			lRqstStrChngStmntRqstId = StringUtility.getParameter("changeRqstId", request);
			lArrChngStmntRqstId = lRqstStrChngStmntRqstId.split("~");
			lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnMonthlyChangeRqst.class, srvcLoc.getSessionFactory());
			for (String lStrChngStmntRqstId : lArrChngStmntRqstId) {
				lObjTrnMonthlyChangeRqst = (TrnMonthlyChangeRqst) lObjChangeStatementDAO.read(Long.valueOf(lStrChngStmntRqstId));
				lObjTrnMonthlyChangeRqst.setStatus(bundleConst.getString("CHANGSTMNTSTATUS.WITHATOFORAPPROVAL"));
				lObjTrnMonthlyChangeRqst.setUpdatedDate(gDate);
				lObjTrnMonthlyChangeRqst.setUpdatedPostId(gLngPostId);
				lObjTrnMonthlyChangeRqst.setUpdatedUserId(gLngUserId);
				lObjChangeStatementDAO.update(lObjTrnMonthlyChangeRqst);
			}
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<FRWDSTATUS>");
			lSBStatus.append("Y");
			lSBStatus.append("</FRWDSTATUS>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
		} catch (Exception e) {
			gLogger.error("Error while forwarding the change statement..." + e, e);
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<FRWDSTATUS>");
			lSBStatus.append("N");
			lSBStatus.append("</FRWDSTATUS>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultCode(ErrorConstants.ERROR);
		}
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;
	}

	public ResultObject approveChangeStatement(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		String lRqstStrChngStmntRqstId = null;
		String lStrApproveRejectStatus = null;
		String[] lArrChngStmntRqstId = null;
		ChangeStatementDAO lObjChangeStatementDAO = null;
		TrnMonthlyChangeRqst lObjTrnMonthlyChangeRqst = null;
		StringBuilder lSBStatus = new StringBuilder();
		List<Long> lLstChngStmntRqstId = new ArrayList<Long>();
		String lStrChngStmntStatus = null;
		try {
			setSessionInfo(inputMap);
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			lRqstStrChngStmntRqstId = StringUtility.getParameter("changeRqstId", request);
			lStrApproveRejectStatus = StringUtility.getParameter("approveRejectFlag", request);

			lArrChngStmntRqstId = lRqstStrChngStmntRqstId.split("~");
			lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnMonthlyChangeRqst.class, srvcLoc.getSessionFactory());
			for (String lStrChngStmntRqstId : lArrChngStmntRqstId) {
				lObjTrnMonthlyChangeRqst = (TrnMonthlyChangeRqst) lObjChangeStatementDAO.read(Long.valueOf(lStrChngStmntRqstId));
				if (!lStrApproveRejectStatus.equals("")) {
					if (lStrApproveRejectStatus.equals("Approve")) {
						lObjTrnMonthlyChangeRqst.setStatus(bundleConst.getString("CHANGSTMNTSTATUS.APPROVED"));
						lStrChngStmntStatus = bundleConst.getString("CHANGSTMNTSTATUS.APPROVED");
					} else if (lStrApproveRejectStatus.equals("Discard")) {
						lObjTrnMonthlyChangeRqst.setStatus(bundleConst.getString("CHANGSTMNTSTATUS.DISCARDED"));
						lStrChngStmntStatus = bundleConst.getString("CHANGSTMNTSTATUS.DISCARDED");
					} else {
						lObjTrnMonthlyChangeRqst.setStatus(bundleConst.getString("CHANGSTMNTSTATUS.REJECTED"));
						lStrChngStmntStatus = bundleConst.getString("CHANGSTMNTSTATUS.REJECTED");
					}
					lObjTrnMonthlyChangeRqst.setUpdatedDate(gDate);
					lObjTrnMonthlyChangeRqst.setUpdatedPostId(gLngPostId);
					lObjTrnMonthlyChangeRqst.setUpdatedUserId(gLngUserId);
					lObjChangeStatementDAO.update(lObjTrnMonthlyChangeRqst);
					lLstChngStmntRqstId.add(Long.valueOf(lStrChngStmntRqstId));
				}

			}
			if (lLstChngStmntRqstId.size() > 0 && lStrChngStmntStatus != null) {
				lObjMonthlyPensionBillDAO.updateMonthlyPnsnRcryOnChngStmntApproval(lLstChngStmntRqstId, lStrChngStmntStatus, gStrLocCode, gLngUserId, gLngPostId, gDate);
			}
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<APPROVESTATUS>");
			if (!lStrApproveRejectStatus.equals("")) {
				lSBStatus.append(lStrApproveRejectStatus);
			}
			lSBStatus.append("</APPROVESTATUS>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
		} catch (Exception e) {
			gLogger.error("Error while approving of the change statement..." + e, e);
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<APPROVESTATUS>");
			lSBStatus.append("ERROR");
			lSBStatus.append("</APPROVESTATUS>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultCode(ErrorConstants.ERROR);
		}
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;
	}

	/*
	 * This method checks if previous month bill is generated/approved or not.
	 */
	public ResultObject checkPrevMonthBillStatus(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		StringBuffer sbLog = new StringBuffer();
		StringBuilder lSBStatus = new StringBuilder();
		String lStrPrevMonthBillStatus = "Generated";
		List<String> lLstChngStmntStatus = new ArrayList<String>();
		lLstChngStmntStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.BEFOREAPPROVAL"));
		lLstChngStmntStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.WITHATOFORAPPROVAL"));
		lLstChngStmntStatus.add(bundleConst.getString("CHANGSTMNTSTATUS.APPROVED"));

		List<Object[]> lLstPrevMonthStatusDtl = new ArrayList<Object[]>();
		String lStrPrevChngStmntStatus = null;
		Integer lIntPrevBillStatus = null;
		Integer lIntCurrMonthYear = null;
		Integer lIntPrevMonthYear = null;
		List<String> lLstBranchCode = new ArrayList<String>();
		List<Long> lLstLngBranchCode = new ArrayList<Long>();
		String lStrBranchCode = "";
		Set<String> lSetPreMonthNtGeneratedBranch = new HashSet<String>();
		List<Object[]> lLstPrevMonthChangStmntStatus = null;
		Set<String> lSetNotApprovedChngStmntBranches = new HashSet<String>();
		Set<String> lSetApprovedChngStmntBranches = new HashSet<String>();
		List<Object[]> lLstPrevMonthNotGenreatedBillDtls = null;
		CommonDAO lObjCommonDAO = null;
		List<Object[]> lLstBankBranchDtls = null;
		Map<String, String> lMapBranchCodeName = new HashMap<String, String>();
		String lStrBankName = null;
		String lStrBranchName = null;
		Map<String, List<String>> lMapBranchSchemeCodeDtls = new HashMap<String, List<String>>();
		Set<String> lSetPreMonthNotGeneratedBillBranch = new HashSet<String>();
		List<String> lLstSchemeCode = null;
		try {
			setSessionInfo(inputMap);
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

			String lStrForMonth = StringUtility.getParameter("cmbForMonth", request).trim();
			String lStrForYear = StringUtility.getParameter("cmbForYear", request).trim();
			String[] lArrStrBranch = StringUtility.getParameterValues("branchCode", request);
			for (int i = 0; i < lArrStrBranch.length; i++) {
				if (lArrStrBranch[i].trim().length() > 0) {
					lLstBranchCode.add(lArrStrBranch[i].trim());
					lLstLngBranchCode.add(Long.valueOf(lArrStrBranch[i].trim()));
				}
			}

			String lStrLocCode = SessionHelper.getLocationCode(inputMap);

			String lStrDate = null;
			MonthlyPensionBillDAO lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			if (Integer.parseInt(lStrForMonth) < 10) {
				lStrDate = lStrForYear + "0" + lStrForMonth;
			} else {
				lStrDate = lStrForYear + lStrForMonth;
			}

			if (!"".equals(lStrDate)) {
				lIntCurrMonthYear = Integer.parseInt(lStrDate);
				if ((lStrDate.substring(4, 6)).equals("01")) {
					lIntPrevMonthYear = lIntCurrMonthYear - 89;
				} else {
					lIntPrevMonthYear = lIntCurrMonthYear - 1;
				}
			}
			// lLstPrevMonthStatusDtl =
			// lObjPensionBillDAO.getPrevMonthBillStatus(lLstBranchCode,
			// lIntPrevMonthYear, lStrLocCode);

			// ---Preparing map of branchcode,bankname-branchname
			lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lLstBankBranchDtls = lObjCommonDAO.getBankBranchNameFromBranchCode(lStrLocCode, gLngLangId, lLstLngBranchCode);
			for (Object[] lArrObj : lLstBankBranchDtls) {
				if (lArrObj[0] != null) {
					lStrBankName = (lArrObj[1] != null) ? lArrObj[1].toString() : "";
					lStrBranchName = (lArrObj[2] != null) ? lArrObj[2].toString() : "";
					lMapBranchCodeName.put(lArrObj[0].toString(), lStrBankName + "," + lStrBranchName);
				}
			}

			lLstPrevMonthChangStmntStatus = lObjPensionBillDAO.getPrevMonthChangeStmntStatus(lIntPrevMonthYear, lStrLocCode, lLstBranchCode);
			for (Object[] lArrObj : lLstPrevMonthChangStmntStatus) {
				lStrPrevChngStmntStatus = (lArrObj[1] != null) ? lArrObj[1].toString() : null;
				lStrBranchCode = (lArrObj[0] != null) ? (String) lArrObj[0] : null;
				if (lStrBranchCode != null) {
					if (lLstChngStmntStatus.contains(lStrPrevChngStmntStatus)) {
						if ((bundleConst.getString("CHANGSTMNTSTATUS.APPROVED")).equals(lStrPrevChngStmntStatus)) {
							lSetApprovedChngStmntBranches.add(lStrBranchCode);
						} else {
							lSetNotApprovedChngStmntBranches.add(lStrBranchCode);
						}
					}
				}
			}
			lLstBranchCode.removeAll(lSetNotApprovedChngStmntBranches);
			if (lLstBranchCode != null && lLstBranchCode.size() > 0) {
				lLstPrevMonthNotGenreatedBillDtls = lObjPensionBillDAO.getPrevMonnthNotGeneratedBillsBranch(lIntPrevMonthYear, lStrLocCode, lLstBranchCode);

				for (Object[] lArrObj : lLstPrevMonthNotGenreatedBillDtls) {
					if (lArrObj[0] != null) {
						lLstSchemeCode = lMapBranchSchemeCodeDtls.get(lArrObj[0].toString());
						if (lLstSchemeCode != null) {
							lLstSchemeCode.add(lArrObj[1].toString());
						} else {
							lLstSchemeCode = new ArrayList<String>();
							lLstSchemeCode.add(lArrObj[1].toString());
							lMapBranchSchemeCodeDtls.put(lArrObj[0].toString(), lLstSchemeCode);
						}
					}
				}
				lSetPreMonthNotGeneratedBillBranch = lMapBranchSchemeCodeDtls.keySet();
			}
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<VALIDATIONSTATUS>");
			if (lSetNotApprovedChngStmntBranches.size() > 0 || lSetPreMonthNotGeneratedBillBranch.size() > 0) {
				lSBStatus.append("FAIL");
			} else {
				lSBStatus.append("SUCCESS");
			}
			lSBStatus.append("</VALIDATIONSTATUS>");
			lSBStatus.append("<NTAPPROVEDCHNGSTMNT>");
			for (String lBranchCode : lSetNotApprovedChngStmntBranches) {
				lSBStatus.append("<BRANCHNAME>");
				lSBStatus.append(lMapBranchCodeName.get(lBranchCode));
				lSBStatus.append("</BRANCHNAME>");
			}
			lSBStatus.append("</NTAPPROVEDCHNGSTMNT>");
			lSBStatus.append("<NTAPPROVEDBILLS>");
			for (String lBranchCode : lSetPreMonthNotGeneratedBillBranch) {
				lSBStatus.append("<BILLDTL>");
				lSBStatus.append("<BRANCHNAME>");
				lSBStatus.append(lMapBranchCodeName.get(lBranchCode));
				lSBStatus.append("</BRANCHNAME>");
				lLstSchemeCode = lMapBranchSchemeCodeDtls.get(lBranchCode);
				for (String lStrSchemeCode : lLstSchemeCode) {
					lSBStatus.append("<SCHEMECODE>");
					lSBStatus.append(lStrSchemeCode);
					lSBStatus.append("</SCHEMECODE>");
				}
				lSBStatus.append("</BILLDTL>");
			}
			lSBStatus.append("</NTAPPROVEDBILLS>");
			lSBStatus.append("</XMLDOC>");
			/*
			 * ---If previous month change statement's status is pending
			 * approval or withato approval then setting previous month bill
			 * status as 'NotGenerated' ---If previous month change statement's
			 * is approved and currBillStatus of previous bill is 5(created) or
			 * 10 (with ato for approval) then setting previous month bill
			 * status as 'NotGenerated'
			 */

			/*
			 * for (Object[] lArrObj : lLstPrevMonthStatusDtl) {
			 * lStrPrevChngStmntStatus = (lArrObj[0] != null) ?
			 * lArrObj[0].toString() : null; lIntPrevBillStatus = (lArrObj[1] !=
			 * null) ? (Integer) lArrObj[1] : 0; // if // change // statement //
			 * is // generated // but // bill // is // not // generated // then
			 * // setting bill status // to // 0. lStrBranchCode = (lArrObj[2]
			 * != null) ? (String) lArrObj[2] : ""; if
			 * (lLstChngStmntStatus.contains(lStrPrevChngStmntStatus)) { if
			 * ((bundleConst.getString("CHANGSTMNTSTATUS.APPROVED")).equals(
			 * lStrPrevChngStmntStatus)) { if (lIntPrevBillStatus == 0 ||
			 * lIntPrevBillStatus == 5 || lIntPrevBillStatus == 10) {
			 * lStrPrevMonthBillStatus = "NotGenerated"; // "NotGenerated" //
			 * indecates // previous // month's // changestatement // is //
			 * generated // but // monthly // bill // is // either // not //
			 * generated // or // not // approved
			 * lSetPreMonthNtGeneratedBranch.add(lStrBranchCode); } } else {
			 * lStrPrevMonthBillStatus = "NotGenerated";
			 * lSetPreMonthNtGeneratedBranch.add(lStrBranchCode); } } }
			 * lSBStatus.append("<XMLDOC>"); for (String lBranchCode :
			 * lSetPreMonthNtGeneratedBranch) {
			 * lSBStatus.append("<BRANCHCODE>"); lSBStatus.append(lBranchCode);
			 * lSBStatus.append("</BRANCHCODE>"); }
			 * lSBStatus.append("</XMLDOC>");
			 */
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			sbLog.append("Problem in checkPrevMonthBillStatus is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			gLogger.error("Problem in checkPrevMonthBillStatus is " + getStackTrace(e) + "location " + SessionHelper.getLocationCode(inputMap));
			lSBStatus.append("<ERROR>");
			lSBStatus.append("Some problem occurred during generation of change statement.");
			lSBStatus.append("</ERROR>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		}

		return objRes;
	}

	public ResultObject loadMonthlyBillReport(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			List lLstAuditorName = new ArrayList();
			List<ComboValuesVO> lLstBankName = new ArrayList<ComboValuesVO>();
			List<ComboValuesVO> lLstMonths = new ArrayList<ComboValuesVO>();
			List<ComboValuesVO> lLstYears = new ArrayList<ComboValuesVO>();

			PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());

			String lStrRoleId = bundleConst.getString("AUDITOR.ROLEID");
			lLstAuditorName = lObjPensionBillDAO.getAuditorsListFromRoleID(Long.parseLong(lStrRoleId), gStrLocCode);
			lLstBankName = lObjCommonDAO.getBankList(inputMap, gLngLangId);
			lLstMonths = lObjCommonDAO.getMonthList(SessionHelper.getLocale(request));
			lLstYears = lObjCommonDAO.getYearList(SessionHelper.getLocale(request));

			inputMap.put("AuditorName", lLstAuditorName);
			inputMap.put("BankName", lLstBankName);
			inputMap.put("MonthList", lLstMonths);
			inputMap.put("YearList", lLstYears);

			resObj.setViewName("MonthlyBillReport");
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

	public ResultObject getPrintAllBill(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		setSessionInfo(inputMap);
		String lStrFinalPrintString = "";
		int flag = 0;
		String lStrChngRqstId = null;
		String lStrBranchCode = null;
		String lStrForMonth = null;
		String lStrBillFlag = null;
		Integer lIntForMonth = null;
		List lLstBankBranch = null;
		try {
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			lStrBillFlag = StringUtility.getParameter("billFlag", request);
			String lStrBankCode = StringUtility.getParameter("bankCode", request);
			String lStrBranchCodes = StringUtility.getParameter("branchCodeString", request);
			String[] lArrBranchCodes = lStrBranchCodes.split("~");
			String lStrYear = StringUtility.getParameter("forYear", request);
			String lStrMonth = StringUtility.getParameter("forMonth", request);
			String[] lArrChngStmntDtls = StringUtility.getParameterValues("changeRqstDtls", request);
			String lStrSortBy = StringUtility.getParameter("sortBy", request).trim();
			inputMap.put("sortBy", lStrSortBy);
			Integer lIntPostId = null;
			List<BigInteger> lLstBillNos = new ArrayList<BigInteger>();
			List<Long> lLstChangeRqstId = new ArrayList<Long>();
			List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
			
			MonthlyPensionBillDAOImpl lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());

			// // ---Print Monthly Bill Report by Auditor(Print Monthly Bill
			// // Report)
			// if ("A".equals(lStrBillFlag)) {
			// String lStrPostId = StringUtility.getParameter("auditorPostID",
			// request);
			// if (!"".equals(lStrForMonth)) {
			// if (Integer.parseInt(lStrMonth) < 10) {
			// lStrForMonth = lStrYear + "0" + lStrMonth;
			// } else {
			// lStrForMonth = lStrYear + lStrMonth;
			// }
			// }
			//
			// if (!"".equals("lStrForMonth")) {
			// lIntForMonth = Integer.parseInt(lStrForMonth);
			// }
			// if (!"".equals(lStrPostId)) {
			// lIntPostId = Integer.parseInt(lStrPostId);
			// }
			// lLstBankBranch =
			// lObjPensionBillDAO.getBankBranchFromAuditor(lIntPostId,
			// gStrLocCode);
			//
			// if (!lLstBankBranch.isEmpty()) {
			// Iterator lObjIterator = lLstBankBranch.iterator();
			// while (lObjIterator.hasNext()) {
			// Object[] lObjArr = (Object[]) lObjIterator.next();
			// if (lObjArr[0] != null && lObjArr[1] != null) {
			// lLstBillNos =
			// lObjPensionBillDAO.getBillNosFromBnkBrnchPayMonth(lObjArr[0].toString(),
			// lObjArr[1].toString(), lIntForMonth, gStrLocCode);
			// Set<BigInteger> lSetBillNo = new HashSet<BigInteger>();
			// lSetBillNo.addAll(lLstBillNos);
			// for (BigInteger lLngBillNo : lSetBillNo) {
			// inputMap.put("billNo", lLngBillNo);
			// List lLstMonthDtls = lObjPensionBillDAO.getMonthDtls(lStrMonth,
			// gLngLangId);
			// inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
			// inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
			// inputMap.put("ForBillYear", lStrYear);
			// String lStrBankName = null;
			// String lStrBranchName = null;
			// List lLstBank = new ArrayList();
			// lLstBank = lObjPensionBillDAO.getBankCode(lObjArr[1].toString(),
			// gStrLocCode);
			// if (lLstBank != null && !lLstBank.isEmpty()) {
			// Object[] lObjArr1 = (Object[]) lLstBank.get(0);
			// lStrBankName = (String) lObjArr1[1];
			// lStrBranchName = (String) lObjArr1[2];
			// inputMap.put("BankName", lStrBankName);
			// inputMap.put("BranchName", lStrBranchName);
			// } else {
			// inputMap.put("BankName", "");
			// inputMap.put("BranchName", "");
			//
			// }
			// getMonthlyReport(inputMap);
			// if (flag == 0) {
			// lStrFinalPrintString = lStrFinalPrintString +
			// inputMap.get("PrintString") + "\n";
			// flag = 1;
			// } else {
			// lStrFinalPrintString = lStrFinalPrintString + "\n" + ((char) 12)
			// + inputMap.get("PrintString");
			// }
			// }
			// }
			// }
			// }
			// }
			// // ---Print Monthly Bill Report by Branches(Print Monthly Bill
			// // Report)
			// if ("B".equals(lStrBillFlag)) {
			// if (!"".equals(lStrForMonth)) {
			// if (Integer.parseInt(lStrMonth) < 10) {
			// lStrForMonth = lStrYear + "0" + lStrMonth;
			// } else {
			// lStrForMonth = lStrYear + lStrMonth;
			// }
			// }
			//
			// if (!"".equals("lStrForMonth")) {
			// lIntForMonth = Integer.parseInt(lStrForMonth);
			// }
			// for (String lInnStrBranchCode : lArrBranchCodes) {
			// lLstBillNos =
			// lObjPensionBillDAO.getBillNosFromBnkBrnchPayMonth(lStrBankCode,
			// lInnStrBranchCode, lIntForMonth, gStrLocCode);
			// Set<BigInteger> lSetBillNo = new HashSet<BigInteger>();
			// lSetBillNo.addAll(lLstBillNos);
			// for (BigInteger lLngBillNo : lSetBillNo) {
			// inputMap.put("billNo", lLngBillNo);
			// List lLstMonthDtls = lObjPensionBillDAO.getMonthDtls(lStrMonth,
			// gLngLangId);
			// inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
			// inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
			// inputMap.put("ForBillYear", lStrYear);
			// String lStrBankName = null;
			// String lStrBranchName = null;
			// List lLstBank = new ArrayList();
			// lLstBank = lObjPensionBillDAO.getBankCode(lInnStrBranchCode,
			// gStrLocCode);
			// if (lLstBank != null && !lLstBank.isEmpty()) {
			// Object[] lObjArr = (Object[]) lLstBank.get(0);
			// lStrBankName = (String) lObjArr[1];
			// lStrBranchName = (String) lObjArr[2];
			// inputMap.put("BankName", lStrBankName);
			// inputMap.put("BranchName", lStrBranchName);
			// } else {
			// inputMap.put("BankName", "");
			// inputMap.put("BranchName", "");
			//
			// }
			// getMonthlyReport(inputMap);
			// if (flag == 0) {
			// lStrFinalPrintString = lStrFinalPrintString +
			// inputMap.get("PrintString") + "\n";
			// flag = 1;
			// } else {
			// lStrFinalPrintString = lStrFinalPrintString + "\n" + ((char) 12)
			// + inputMap.get("PrintString");
			// }
			// }
			// }
			// }

			// ---Print Monthly Bill Report by Auditor(Print Monthly Bill
			// Report)
			if ("A".equals(lStrBillFlag)) {
				String lStrPostId = StringUtility.getParameter("auditorPostID", request);
				if (!"".equals(lStrForMonth)) {
					if (Integer.parseInt(lStrMonth) < 10) {
						lStrForMonth = lStrYear + "0" + lStrMonth;
					} else {
						lStrForMonth = lStrYear + lStrMonth;
					}
				}

				if (!"".equals("lStrForMonth")) {
					lIntForMonth = Integer.parseInt(lStrForMonth);
				}
				if (!"".equals(lStrPostId)) {
					lIntPostId = Integer.parseInt(lStrPostId);
				}
				lLstBankBranch = lObjPensionBillDAO.getBankBranchFromAuditor(lIntPostId, gStrLocCode);

				if (!lLstBankBranch.isEmpty()) {
					Iterator lObjIterator = lLstBankBranch.iterator();
					while (lObjIterator.hasNext()) {
						Object[] lObjArr = (Object[]) lObjIterator.next();
						if (lObjArr[0] != null && lObjArr[1] != null) {
							//lLstChangeRqstId = lObjPensionBillDAO.getChangeRqstIdsFromBnkBranch(lObjArr[0].toString(), lObjArr[1].toString(), lIntForMonth, gStrLocCode);
							//for (Long lLngChangeRqstId : lLstChangeRqstId) {
								//inputMap.put("changeRqstId", lLngChangeRqstId);
								List lLstMonthDtls = lObjPensionBillDAO.getMonthDtls(lStrMonth, gLngLangId);
								inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
								inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
								inputMap.put("ForBillYear", lStrYear);
								inputMap.put("ForMonthYear", lStrForMonth);
								inputMap.put("BankCode",lObjArr[0]);
								inputMap.put("BranchCode",lObjArr[1]);
								lLstMonthlyPensionBillVO = lObjPensionBillDAO.getMonthlyPensionBillVOListFrmBankBranch(lObjArr[0].toString(),lObjArr[1].toString(),lStrForMonth, gStrLocCode, lStrSortBy);
								String lStrBankName = null;
								String lStrBranchName = null;
								List lLstBank = new ArrayList();
								lLstBank = lObjPensionBillDAO.getBankCode(lObjArr[1].toString(), gStrLocCode);
								if (lLstBank != null && !lLstBank.isEmpty()) {
									Object[] lObjArr1 = (Object[]) lLstBank.get(0);
									lStrBankName = (String) lObjArr1[1];
									lStrBranchName = (String) lObjArr1[2];
									inputMap.put("BankName", lStrBankName);
									inputMap.put("BranchName", lStrBranchName);
								} else {
									inputMap.put("BankName", "");
									inputMap.put("BranchName", "");

								}
								if(lLstMonthlyPensionBillVO.size() >0)
								{
									inputMap.put("MonthlyPensionBillVO", lLstMonthlyPensionBillVO);
									getMonthlyReport(inputMap);
									if (flag == 0) {
										lStrFinalPrintString = lStrFinalPrintString + inputMap.get("PrintString") + "\n";
										flag = 1;
									} else {
										lStrFinalPrintString = lStrFinalPrintString + "\n" + ((char) 12) + inputMap.get("PrintString");
									}
								}
							//}
						}
					}
				}
			}
			// ---Print Monthly Bill Report by Branches(Print Monthly Bill
			// Report)
			if ("B".equals(lStrBillFlag)) {
				if (!"".equals(lStrForMonth)) {
					if (Integer.parseInt(lStrMonth) < 10) {
						lStrForMonth = lStrYear + "0" + lStrMonth;
					} else {
						lStrForMonth = lStrYear + lStrMonth;
					}
				}

				if (!"".equals("lStrForMonth")) {
					lIntForMonth = Integer.parseInt(lStrForMonth);
				}
				for (String lInnStrBranchCode : lArrBranchCodes) {
					//lLstChangeRqstId = lObjPensionBillDAO.getChangeRqstIdsFromBnkBranch(lStrBankCode, lInnStrBranchCode, lIntForMonth, gStrLocCode);
					//for (Long lLngChangeRqstId : lLstChangeRqstId) {
						//inputMap.put("changeRqstId", lLngChangeRqstId);
						List lLstMonthDtls = lObjPensionBillDAO.getMonthDtls(lStrMonth, gLngLangId);
						inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
						inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
						inputMap.put("ForBillYear", lStrYear);
						inputMap.put("ForMonthYear", lStrForMonth);
						inputMap.put("BankCode",lStrBankCode);
						inputMap.put("BranchCode",lInnStrBranchCode);
						lLstMonthlyPensionBillVO = lObjPensionBillDAO.getMonthlyPensionBillVOListFrmBankBranch(lStrBankCode,lInnStrBranchCode,lStrForMonth, gStrLocCode, lStrSortBy);
						String lStrBankName = null;
						String lStrBranchName = null;
						List lLstBank = new ArrayList();
						lLstBank = lObjPensionBillDAO.getBankCode(lInnStrBranchCode, gStrLocCode);
						if (lLstBank != null && !lLstBank.isEmpty()) {
							Object[] lObjArr = (Object[]) lLstBank.get(0);
							lStrBankName = (String) lObjArr[1];
							lStrBranchName = (String) lObjArr[2];
							inputMap.put("BankName", lStrBankName);
							inputMap.put("BranchName", lStrBranchName);
						} else {
							inputMap.put("BankName", "");
							inputMap.put("BranchName", "");

						}
						if(lLstMonthlyPensionBillVO.size() >0)
						{
							inputMap.put("MonthlyPensionBillVO", lLstMonthlyPensionBillVO);
							getMonthlyReport(inputMap);
							if (flag == 0) {
								lStrFinalPrintString = lStrFinalPrintString + inputMap.get("PrintString") + "\n";
								flag = 1;
							} else {
								lStrFinalPrintString = lStrFinalPrintString + "\n" + ((char) 12) + inputMap.get("PrintString");
							}
						}
					//}
				}
			}
//			// ---Print Monthly Bill Report by Auditor(Print Monthly Bill
//			// Report)
//			if ("A".equals(lStrBillFlag)) {
//				String lStrPostId = StringUtility.getParameter("auditorPostID", request);
//				if (!"".equals(lStrForMonth)) {
//					if (Integer.parseInt(lStrMonth) < 10) {
//						lStrForMonth = lStrYear + "0" + lStrMonth;
//					} else {
//						lStrForMonth = lStrYear + lStrMonth;
//					}
//				}
//
//				if (!"".equals("lStrForMonth")) {
//					lIntForMonth = Integer.parseInt(lStrForMonth);
//				}
//				if (!"".equals(lStrPostId)) {
//					lIntPostId = Integer.parseInt(lStrPostId);
//				}
//				lLstBankBranch = lObjPensionBillDAO.getBankBranchFromAuditor(lIntPostId, gStrLocCode);
//
//				if (!lLstBankBranch.isEmpty()) {
//					Iterator lObjIterator = lLstBankBranch.iterator();
//					while (lObjIterator.hasNext()) {
//						Object[] lObjArr = (Object[]) lObjIterator.next();
//						if (lObjArr[0] != null && lObjArr[1] != null) {
//							lLstChangeRqstId = lObjPensionBillDAO.getChangeRqstIdsFromBnkBranch(lObjArr[0].toString(), lObjArr[1].toString(), lIntForMonth, gStrLocCode);
//							for (Long lLngChangeRqstId : lLstChangeRqstId) {
//								inputMap.put("changeRqstId", lLngChangeRqstId);
//								List lLstMonthDtls = lObjPensionBillDAO.getMonthDtls(lStrMonth, gLngLangId);
//								inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
//								inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
//								inputMap.put("ForBillYear", lStrYear);
//								String lStrBankName = null;
//								String lStrBranchName = null;
//								List lLstBank = new ArrayList();
//								lLstBank = lObjPensionBillDAO.getBankCode(lObjArr[1].toString(), gStrLocCode);
//								if (lLstBank != null && !lLstBank.isEmpty()) {
//									Object[] lObjArr1 = (Object[]) lLstBank.get(0);
//									lStrBankName = (String) lObjArr1[1];
//									lStrBranchName = (String) lObjArr1[2];
//									inputMap.put("BankName", lStrBankName);
//									inputMap.put("BranchName", lStrBranchName);
//								} else {
//									inputMap.put("BankName", "");
//									inputMap.put("BranchName", "");
//
//								}
//								getMonthlyReport(inputMap);
//								if (flag == 0) {
//									lStrFinalPrintString = lStrFinalPrintString + inputMap.get("PrintString") + "\n";
//									flag = 1;
//								} else {
//									lStrFinalPrintString = lStrFinalPrintString + "\n" + ((char) 12) + inputMap.get("PrintString");
//								}
//							}
//						}
//					}
//				}
//			}
			// ---Print Monthly Bill Report by Branches(Print Monthly Bill
			// Report)
//			if ("B".equals(lStrBillFlag)) {
//				if (!"".equals(lStrForMonth)) {
//					if (Integer.parseInt(lStrMonth) < 10) {
//						lStrForMonth = lStrYear + "0" + lStrMonth;
//					} else {
//						lStrForMonth = lStrYear + lStrMonth;
//					}
//				}
//
//				if (!"".equals("lStrForMonth")) {
//					lIntForMonth = Integer.parseInt(lStrForMonth);
//				}
//				for (String lInnStrBranchCode : lArrBranchCodes) {
//					lLstChangeRqstId = lObjPensionBillDAO.getChangeRqstIdsFromBnkBranch(lStrBankCode, lInnStrBranchCode, lIntForMonth, gStrLocCode);
//					for (Long lLngChangeRqstId : lLstChangeRqstId) {
//						inputMap.put("changeRqstId", lLngChangeRqstId);
//						List lLstMonthDtls = lObjPensionBillDAO.getMonthDtls(lStrMonth, gLngLangId);
//						inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
//						inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
//						inputMap.put("ForBillYear", lStrYear);
//						String lStrBankName = null;
//						String lStrBranchName = null;
//						List lLstBank = new ArrayList();
//						lLstBank = lObjPensionBillDAO.getBankCode(lInnStrBranchCode, gStrLocCode);
//						if (lLstBank != null && !lLstBank.isEmpty()) {
//							Object[] lObjArr = (Object[]) lLstBank.get(0);
//							lStrBankName = (String) lObjArr[1];
//							lStrBranchName = (String) lObjArr[2];
//							inputMap.put("BankName", lStrBankName);
//							inputMap.put("BranchName", lStrBranchName);
//						} else {
//							inputMap.put("BankName", "");
//							inputMap.put("BranchName", "");
//
//						}
//						getMonthlyReport(inputMap);
//						if (flag == 0) {
//							lStrFinalPrintString = lStrFinalPrintString + inputMap.get("PrintString") + "\n";
//							flag = 1;
//						} else {
//							lStrFinalPrintString = lStrFinalPrintString + "\n" + ((char) 12) + inputMap.get("PrintString");
//						}
//					}
//				}
//			}
			// ---Print Monthly Bill Report by Change Request Ids (Generate
			// Monthly Pension Bill)
			if ("C".equals(lStrBillFlag)) {
				if (lArrChngStmntDtls.length > 0) {
					for (String lStrChngStmntDtls : lArrChngStmntDtls) {
						Object[] lObjArr = lStrChngStmntDtls.split("~");
						lStrChngRqstId = lObjArr[0].toString();
						lStrBankCode = lObjArr[1].toString();
						lStrBranchCode = lObjArr[2].toString();
						lStrForMonth = lObjArr[3].toString();
						lStrMonth = lStrForMonth.substring(4, 6);
						lStrYear = lStrForMonth.substring(0, 4);
						inputMap.put("changeRqstId", lStrChngRqstId);
						List lLstMonthDtls = lObjPensionBillDAO.getMonthDtls(lStrMonth, gLngLangId);
						inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
						inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
						inputMap.put("ForBillYear", lStrYear);
						String lStrBankName = null;
						String lStrBranchName = null;
						List lLstBank = new ArrayList();
						lLstBank = lObjPensionBillDAO.getBankCode(lStrBranchCode, gStrLocCode);
						if (lLstBank != null && !lLstBank.isEmpty()) {
							Object[] lObjArrBankDtls = (Object[]) lLstBank.get(0);
							lStrBankName = (String) lObjArrBankDtls[1];
							lStrBranchName = (String) lObjArrBankDtls[2];
							inputMap.put("BankName", lStrBankName);
							inputMap.put("BranchName", lStrBranchName);
						} else {
							inputMap.put("BankName", "");
							inputMap.put("BranchName", "");
						}
						getMonthlyReport(inputMap);
						if (flag == 0) {
							lStrFinalPrintString = lStrFinalPrintString + inputMap.get("PrintString") + "\n";
							flag = 1;
						} else {
							lStrFinalPrintString = lStrFinalPrintString + "\n" + ((char) 12) + inputMap.get("PrintString");
						}
					}
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		lStrFinalPrintString = lStrFinalPrintString.replace("</pre></div>", "").replace("<div><pre>", "");
		inputMap.put("FinalPrintString", lStrFinalPrintString);
		inputMap.put("billFlag", lStrBillFlag);
		// inputMap.put("FinalPrintString", lStrFinalPrintString);
		// stem.out.println("Final Print strig is :" + lStrFinalPrintString);
		resObj.setResultValue(inputMap);
		resObj.setViewName("PrintAllMonthlyBill");
		return resObj;
	}

	private String getBillCntrlNoPrefix() {

		String lStrLocShortName = gStrLocShortName;
		String lStrDate = "";
		String lStrBillCntrlNoPrefix = "";
		NumberFormat f = new DecimalFormat("#00");
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date(System.currentTimeMillis()));
		lStrDate = f.format(gc.get(gc.MONTH) + 1) + f.format(gc.get(gc.YEAR));
		lStrBillCntrlNoPrefix = lStrLocShortName + "/" + lStrDate + "/";
		return lStrBillCntrlNoPrefix;
	}

	public ResultObject loadMonthlyPensionSchemewise(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		MonthlyPensionBillDAO lObjPensionBillDAO = null;
		List<String> lLstSchemeCodes = null;
		List<SgvaMonthMst> lObjSgvaMonthMst = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		List<SgvcFinYearMst> lObjSgvcFinYearMst = null;
		String currentMonth = null;
		String currentYear = null;
		try {
			setSessionInfo(inputMap);
			lObjSgvaMonthMst = new ArrayList<SgvaMonthMst>();
			lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			currentMonth = new SimpleDateFormat("MM").format(gDate);
			currentYear = new SimpleDateFormat("yyyy").format(gDate);
			inputMap.put("CurrentMonth", Integer.parseInt(currentMonth));
			inputMap.put("CurrentYear", currentYear);
			lLstSchemeCodes = lObjPensionBillDAO.getAllSchemeCode();
			lObjSgvaMonthMst = lObjCommonPensionDAO.getSgvaMonthMstVO(lStrLangId);
			if (lObjSgvaMonthMst != null) {
				inputMap.put("SgvaMonthMstVOArray", lObjSgvaMonthMst);
			}
			lObjSgvcFinYearMst = new ArrayList<SgvcFinYearMst>();
			lObjSgvcFinYearMst = lObjCommonPensionDAO.getSgvcFinYearMstVO(lStrLangId);
			if (lObjSgvcFinYearMst != null) {
				inputMap.put("SgvcFinYearMstVOArray", lObjSgvcFinYearMst);
			}
			inputMap.put("lLstSchemeCode", lLstSchemeCodes);
			resObj.setResultValue(inputMap);
			resObj.setViewName("genMonthlyPnsnBill");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getSchemeDescBySchemeCode(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		MonthlyPensionBillDAO lObjPensionBillDAO = null;
		List<SgvcFinYearMst> lObjSgvcFinYearMst = null;
		String currentMonth = null;
		String currentYear = null;
		String lStrSchemeCode = null;
		String lStrSchemeName = null;
		StringBuilder lSBStatus = new StringBuilder();
		try {
			setSessionInfo(inputMap);
			lStrSchemeCode = StringUtility.getParameter("schemeCode", request);
			lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lStrSchemeName = lObjPensionBillDAO.getSchemeNameBySchemeCode(lStrSchemeCode, lStrLangId);
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<SCHEMENAME>");
			lSBStatus.append("<![CDATA[");
			lSBStatus.append(lStrSchemeName);
			lSBStatus.append("]]>");
			lSBStatus.append("</SCHEMENAME>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject generateMonthlyPensionBill(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPensionChangeHdr lObjTrnPensionChangeHdr = null;
		TrnPensionChangeDtls lObjTrnPensionChangeDtls = null;
		TrnBillRegister lObjTrnBillRegisterVO = null;
		TrnPensionBillHdr lObjPensionBillHdr = null;
		TrnPensionBillDtls lObjPensionBillDtlVO = null;
		TrnBillChangeStmntMpg lObjTrnBillChangeStmntMpg = null;
		List<TrnPensionChangeDtls> lLstTrnPensionChangeDtls = null;
		List<TrnPensionBillHdr> lLstTrnPensionBillHdr = null;
		List<TrnPensionBillDtls> lLstTrnPensionBillDtls = null;
		Long lLngChangeRqstId = null;
		Long lLngChangeHdrId = null;
		Long lLngBillNo = null;
		Long lLngBillHdrId = null;
		Long lLngBillDtlsId = null;
		Long lLngBillPartyId = null;
		Long lLngBillMvmntId = null;
		Long lLngBillChngStmntId = null;
		ChangeStatementDAO lObjChangeStatementDAO = null;
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		Map<BigDecimal, String> lMpHeadCodeDesc = new HashMap<BigDecimal, String>();
		List<MstPensionHeadcode> lLstMstPensionHeadcode = null;
		BigDecimal lTotalNetAmt = BigDecimal.ZERO;
		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Long lLngPkCntBillRegister = null;
		Long lLngPkCntPensionBillHdr = null;
		Long lLngPkCntPensionBillDtls = null;
		Long lLngPkCntBillChngStmntMpg = null;
		Long lLngPkCntRltBillParty = null;
		Long lLngPkCntTrnBillMvmnt = null;
		HibernateTemplate hitStg = new HibernateTemplate(srvcLoc.getSessionFactory());
		List<Long> lLstChangeRqstId = new ArrayList<Long>();
		List<Long> lLstTrnPnsnChngHdrId = null;
		List<Long> lLstTrnPnsnChngDtlsId = null;
		List<Long> lLstAllTrnPnsnChngHdrId = new ArrayList<Long>();
		List<Long> lLstAllTrnPnsnChngDtlsId = new ArrayList<Long>();
		List<TrnPensionChangeHdr> lInnLstTrnPensionChangeHdr = null;
		List<TrnPensionChangeDtls> lInnLstTrnPensionChangeDtls = null;
		Map<Long, List<Long>> lMapChngRqstIdHdrId = new HashMap<Long, List<Long>>();
		Map<Long, List<Long>> lMapChngHdrIdDtlsId = new HashMap<Long, List<Long>>();
		Map<Long, TrnMonthlyChangeRqst> lMapTrnMonthlyChangeRqst = new HashMap<Long, TrnMonthlyChangeRqst>();
		Map<Long, TrnPensionChangeHdr> lMapTrnPensionChangeHdr = new HashMap<Long, TrnPensionChangeHdr>();
		Map<Long, TrnPensionChangeDtls> lMapTrnPensionChangeDtls = new HashMap<Long, TrnPensionChangeDtls>();
		List<TrnBillRegister> lLstTrnBillRegisterVO = null;
		List<TrnBillMvmnt> lLstTrnBillMvmnt = null;
		List<TrnBillChangeStmntMpg> lLstTrnBillChangeStmntMpg = null;
		Map<Long, Object[]> lMapChngRqstIdParyDtls = new HashMap<Long, Object[]>();
		RltBillParty lObjBillParty = null;
		TrnBillMvmnt lObjTrnBillMvmnt = null;
		Object[] lArrPartyDtls = null;
		StringBuilder lSBStatus = new StringBuilder();
		BptmCommonServicesDAO bptmDAO = null;
		Long lLongBillControlNum = 0L;

		// --New Vars.
		String lStrSchemeCode = null;
		String lStrPayMode = null;
		String lStrMonth = null;
		String lStrYear = null;
		String lStrForMonthYear = null;
		List<TrnPensionChangeHdr> lLstChngStmntHdrDtls = null;
		List<String> lLstBranchCodesOfGeneratedBills = null;
		// List<String> lLstValidBranchCode = new ArrayList<String>();
		List<Long> lLstValidChngRqstIds = new ArrayList<Long>();
		Set<String> lSetBranchCodesInChngStmnt = new HashSet<String>();
		Set<Long> lSetChangeRqstIds = new HashSet<Long>();
		// Map<String, List<Long>> lMapBranchCodeHdrId = new HashMap<String,
		// List<Long>>();
		Map<Long, List<Long>> lMapChangeRqstHdrId = new HashMap<Long, List<Long>>();
		List<Long> lLstBranchwiseHdrIds = new ArrayList<Long>();
		List<Long> lLstLngChngHdrIds = new ArrayList<Long>();
		Double lDblHeadWiseGrossAmt = 0d;
		Double lDblHeadWiseRecAmt = 0d;
		Double lDblHeadWiseNetAmt = 0d;
		Double lDblBillGrossAmt = 0d;
		Double lDblBillRecAmt = 0d;
		Double lDblBillNetAmt = 0d;
		String[] lArrStrChngRqstIds = null;
		List<Long> lLstGeneratedBillChngRqstIds = null;
		PensionBillDAO lObjPensionBillDAO = null;
		List<String> lLstPensionerCode = new ArrayList<String>();
		List<RltBillParty> lLstRltBillParty = new ArrayList<RltBillParty>();
		String lStrECSBranchFlag = null;
		BptmCommonServicesDAOImpl lObjBptmCommonServicesDAO = null;
		List<Object[]> lLstBillPartyDtls = null;
		String lStrBillPartyName = null;
		String lStrBillPartyBankCode = null;
		String lStrBillPartyBranchCode = null;
		Double lDblBillPartyNetAmt = null;
		try {
			setSessionInfo(inputMap);
			lStrSchemeCode = StringUtility.getParameter("schemeCode", request).trim();
			lStrMonth = StringUtility.getParameter("month", request).trim();
			lStrYear = StringUtility.getParameter("year", request).trim();
			lStrPayMode = StringUtility.getParameter("payMode", request).trim();

			if (Integer.parseInt(lStrMonth) < 10) {
				lStrForMonthYear = lStrYear + "0" + lStrMonth;
			} else {
				lStrForMonthYear = lStrYear + lStrMonth;
			}
			if (bundleCaseConst.getString("PPMT.PAYMODEECS").equals(lStrPayMode)) {
				lStrECSBranchFlag = "Y";
			} else {
				lStrECSBranchFlag = "N";
			}
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
			bptmDAO = new BptmCommonServicesDAOImpl(TrnBillRegister.class, srvcLoc.getSessionFactory());
			lObjPensionBillDAO = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
			lLstTrnBillRegisterVO = new ArrayList<TrnBillRegister>();
			lLstTrnPensionBillHdr = new ArrayList();
			lLstTrnPensionBillDtls = new ArrayList();
			lLstTrnBillMvmnt = new ArrayList<TrnBillMvmnt>();

			/*
			 * Getting List of Change_Stmnt_HdrVOs for selected
			 * schemecode,paymode and month-year.
			 */
			lLstChngStmntHdrDtls = lObjMonthlyPensionBillDAO.getChngHdrDtlsBySchemeCode(lStrSchemeCode, lStrPayMode, lStrForMonthYear, gStrLocCode);
			for (TrnPensionChangeHdr lObjTrnPensionChangeHdrVO : lLstChngStmntHdrDtls) {
				lSetBranchCodesInChngStmnt.add(lObjTrnPensionChangeHdrVO.getBranchCode());
				lSetChangeRqstIds.add(lObjTrnPensionChangeHdrVO.getChangeRqstId());
				lMapTrnPensionChangeHdr.put(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId(), lObjTrnPensionChangeHdrVO);
				// lLstAllTrnPnsnChngHdrId.add(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId());

				lLstLngChngHdrIds = lMapChangeRqstHdrId.get(lObjTrnPensionChangeHdrVO.getChangeRqstId());
				if (lLstLngChngHdrIds != null) {
					lLstLngChngHdrIds.add(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId());
				} else {
					lLstLngChngHdrIds = new ArrayList<Long>();
					lLstLngChngHdrIds.add(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId());
				}
				lMapChangeRqstHdrId.put(lObjTrnPensionChangeHdrVO.getChangeRqstId(), lLstLngChngHdrIds);
				// lLstBranchwiseHdrIds =
				// lMapBranchCodeHdrId.get(lObjTrnPensionChangeHdrVO.getBranchCode());
				// if (lLstBranchwiseHdrIds != null) {
				// lLstBranchwiseHdrIds.add(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId());
				// } else {
				// lLstBranchwiseHdrIds = new ArrayList<Long>();
				// lLstBranchwiseHdrIds.add(lObjTrnPensionChangeHdrVO.getTrnPensionChangeHdrId());
				// }
				// lMapBranchCodeHdrId.put(lObjTrnPensionChangeHdrVO.getBranchCode(),
				// lLstBranchwiseHdrIds);

			}

			/*
			 * Getting Branchcode List of Generated Pension Bills.
			 */

			/*
			 * 
			 * avoid this for loop. array to null.
			 */
			lLstBranchCodesOfGeneratedBills = lObjMonthlyPensionBillDAO.getBranchCodeOfGeneratedBills(lStrSchemeCode, lStrPayMode, lStrForMonthYear, gStrLocCode);
			lLstGeneratedBillChngRqstIds = new ArrayList<Long>();
			for (String lStrChngRqstIds : lLstBranchCodesOfGeneratedBills) {
				lArrStrChngRqstIds = lStrChngRqstIds.split(",");
				for (String lStrChngRqstId : lArrStrChngRqstIds) {
					if (lStrChngRqstId.length() > 0) {
						lLstGeneratedBillChngRqstIds.add(Long.valueOf(lStrChngRqstId));
					}
				}
			}
			lLstValidChngRqstIds.addAll(lSetChangeRqstIds);
			/*
			 * Preparing list of branch codes and pnsn_change_hdr_id to be
			 * considered for bill generation.
			 * 
			 * map.values .
			 */
			lLstValidChngRqstIds.removeAll(lLstGeneratedBillChngRqstIds);
			for (Long lStrChngRqstId : lLstValidChngRqstIds) {
				lLstAllTrnPnsnChngHdrId.addAll(lMapChangeRqstHdrId.get(lStrChngRqstId));
			}
			lMapChangeRqstHdrId = null;
			/*
			 * Preparing list of all dtls ids,(Map of
			 * chngHdrId,List<chngDtlsIds>) and (Map of
			 * chngDtlsId,TrnPensionChangeDtlsVo)
			 */

			lObjChangeStatementDAO = new ChangeStatementDAOImpl(TrnPensionChangeDtls.class, srvcLoc.getSessionFactory());
			if (lLstValidChngRqstIds.size() > 0) {
				lLstTrnPensionChangeDtls = lObjChangeStatementDAO.getListForColumnByValues("trnPensionChangeHdrId", lLstAllTrnPnsnChngHdrId);
				for (TrnPensionChangeDtls lObjTrnPensionChangeDtlsVO : lLstTrnPensionChangeDtls) {
					lLstTrnPnsnChngDtlsId = lMapChngHdrIdDtlsId.get(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeHdrId());
					if (lLstTrnPnsnChngDtlsId != null) {
						lLstTrnPnsnChngDtlsId.add(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeDtlsId());
					} else {
						lLstTrnPnsnChngDtlsId = new ArrayList<Long>();
						lLstTrnPnsnChngDtlsId.add(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeDtlsId());
					}
					lLstAllTrnPnsnChngDtlsId.add(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeDtlsId());
					// --put counter
					lMapChngHdrIdDtlsId.put(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeHdrId(), lLstTrnPnsnChngDtlsId);
					lMapTrnPensionChangeDtls.put(lObjTrnPensionChangeDtlsVO.getTrnPensionChangeDtlsId(), lObjTrnPensionChangeDtlsVO);
				}

				/*
				 * Preparing batches of comma seperated change request ids
				 * starts <<<
				 */

				// ----String Builder.

				// String lStrChangeRqstIds = "";
				// String lStrCurrChangeRqstId = "";
				// List<String> lLstChangeRqstIdsBatch = new
				// ArrayList<String>();
				// for (Long lLngChngRqstId : lLstValidChngRqstIds) {
				// lStrCurrChangeRqstId = lLngChngRqstId.toString() + ",";
				// if ((lStrChangeRqstIds.length() +
				// lStrCurrChangeRqstId.length()) > 1000) {
				// lLstChangeRqstIdsBatch.add(lStrChangeRqstIds);
				// lStrChangeRqstIds = "";
				// lStrChangeRqstIds = lStrChangeRqstIds + lStrCurrChangeRqstId;
				// } else {
				// lStrChangeRqstIds = lStrChangeRqstIds + lStrCurrChangeRqstId;
				// }
				// }

				StringBuilder lSBChangeRqstIds = new StringBuilder();
				StringBuilder lSBCurrChangeRqstId = new StringBuilder();
				List<String> lLstChangeRqstIdsBatch = new ArrayList<String>();
				String lStrCurrChangeRqstId = "";
				for (Long lLngChngRqstId : lLstValidChngRqstIds) {
					lStrCurrChangeRqstId = lLngChngRqstId.toString() + ",";
					if ((lSBChangeRqstIds.length() + lStrCurrChangeRqstId.length()) > 1000) {
						lLstChangeRqstIdsBatch.add(lSBChangeRqstIds.toString());
						lSBChangeRqstIds = new StringBuilder();
						lSBChangeRqstIds.append(lStrCurrChangeRqstId);
					} else {
						lSBChangeRqstIds.append(lStrCurrChangeRqstId);
					}
				}
				if (lSBChangeRqstIds.length() > 0) {
					lLstChangeRqstIdsBatch.add(lSBChangeRqstIds.toString());
				}
				/*
				 * Preparing batches of comma seperated change request ids ends
				 * >>>
				 */

				/*
				 * Getting Details for inserting bill party details bank branch
				 * groupwise
				 */
				lLstBillPartyDtls = lObjMonthlyPensionBillDAO.getBillPartyDtlsForSchemewiseBill(Integer.valueOf(lStrForMonthYear), gStrLocCode, lStrSchemeCode, lStrECSBranchFlag);

				/*
				 * Inserting all change statement data to pension bill tables
				 */
				lLongBillControlNum = bptmDAO.getBillControlNo(inputMap);
				String lStrBillCntrlNoPrefix = getBillCntrlNoPrefix();
				lLngPkCntBillRegister = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_bill_register", inputMap, 1);
				lLngPkCntPensionBillHdr = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_bill_hdr", inputMap, lLstAllTrnPnsnChngHdrId.size());
				lLngPkCntPensionBillDtls = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_bill_dtls", inputMap, lLstAllTrnPnsnChngDtlsId.size());
				lLngPkCntBillChngStmntMpg = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_bill_change_stmnt_mpg", inputMap, lLstChangeRqstIdsBatch.size());
				lLngPkCntTrnBillMvmnt = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_bill_mvmnt", inputMap, 1);
				lLngPkCntRltBillParty = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("rlt_bill_party", inputMap, lLstBillPartyDtls.size());

				lLngBillNo = ++lLngPkCntBillRegister;
				lLngBillNo = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillNo, inputMap);
				lObjTrnBillRegisterVO = new TrnBillRegister();
				lObjTrnBillRegisterVO.setBillNo(lLngBillNo);
				lObjTrnBillRegisterVO.setBillDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(gDate)));
				lObjTrnBillRegisterVO.setSubjectId(Short.parseShort(bundleConst.getString("BILLTYPE.MONTHLY")));
				lObjTrnBillRegisterVO.setPhyBill(Short.parseShort("1"));
				lObjTrnBillRegisterVO.setSchemeNo(lStrSchemeCode);
				lObjTrnBillRegisterVO.setCreatedDate(gDate);
				lObjTrnBillRegisterVO.setCreatedPostId(gLngPostId);
				lObjTrnBillRegisterVO.setCreatedUserId(gLngUserId);
				lObjTrnBillRegisterVO.setLocationCode(gStrLocCode);
				lObjTrnBillRegisterVO.setDbId(gDbId);
				lObjTrnBillRegisterVO.setCurrBillStatus(DBConstants.ST_BILL_CREATED);
				lObjTrnBillRegisterVO.setAudPostId(gLngPostId);
				lObjTrnBillRegisterVO.setAudUserId(gLngUserId);
				lObjTrnBillRegisterVO.setFinYearId(SessionHelper.getFinYrId(inputMap).toString());
				lObjTrnBillRegisterVO.setTcBill(bundleConst.getString("MNTH.MONTHLY"));
				lObjTrnBillRegisterVO.setBillCntrlNo(lStrBillCntrlNoPrefix + (lLongBillControlNum++));
				lObjTrnBillRegisterVO.setPayMode(lStrPayMode);
				lObjTrnBillRegisterVO.setForMonth(Integer.valueOf(lStrForMonthYear));
				// ---Inserting entry in trn_bill_mvmnt table
				lObjTrnBillMvmnt = new TrnBillMvmnt();
				lLngBillMvmntId = ++lLngPkCntTrnBillMvmnt;
				lLngBillMvmntId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillMvmntId, inputMap);
				lObjTrnBillMvmnt.setBillMvmtId(lLngBillMvmntId);
				lObjTrnBillMvmnt.setBillNo(lLngBillNo);
				lObjTrnBillMvmnt.setMvmntStatus(DBConstants.ST_BILL_CREATED);
				lObjTrnBillMvmnt.setCreatedDate(gDate);
				lObjTrnBillMvmnt.setCreatedPostId(gLngPostId);
				lObjTrnBillMvmnt.setCreatedUserId(gLngUserId);
				lObjTrnBillMvmnt.setMovemntId(1L);
				lObjTrnBillMvmnt.setReceivingUserId(gLngUserId);
				lObjTrnBillMvmnt.setReceivedDate(gDate);
				lObjTrnBillMvmnt.setStatusUpdtDate(gDate);
				lObjTrnBillMvmnt.setStatusUpdtPostid(gLngPostId); // next
				lObjTrnBillMvmnt.setStatusUpdtUserid(gLngUserId);
				lObjTrnBillMvmnt.setDbId(gDbId);
				lObjTrnBillMvmnt.setLocationCode(gStrLocCode);
				lLstTrnBillMvmnt.add(lObjTrnBillMvmnt);

				for (Long lLngPnsnChngHrdId : lLstAllTrnPnsnChngHdrId) {
					lLstTrnPnsnChngDtlsId = lMapChngHdrIdDtlsId.get(lLngPnsnChngHrdId);
					lLngBillHdrId = ++lLngPkCntPensionBillHdr;
					lLngBillHdrId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillHdrId, inputMap);
					lObjTrnPensionChangeHdr = lMapTrnPensionChangeHdr.get(lLngPnsnChngHrdId);
					lObjPensionBillHdr = new TrnPensionBillHdr();
					lObjPensionBillHdr.setTrnPensionBillHdrId(lLngBillHdrId);
					lObjPensionBillHdr.setBillType(lObjTrnPensionChangeHdr.getBillType());
					lObjPensionBillHdr.setBillNo(lLngBillNo);
					lObjPensionBillHdr.setBillDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(gDate)));
					lObjPensionBillHdr.setHeadCode(new BigDecimal(lObjTrnPensionChangeHdr.getHeadCode()));
					lObjPensionBillHdr.setForMonth(lObjTrnPensionChangeHdr.getForMonth());
					lObjPensionBillHdr.setBankCode(lObjTrnPensionChangeHdr.getBankCode());
					lObjPensionBillHdr.setBranchCode(lObjTrnPensionChangeHdr.getBranchCode());
					lObjPensionBillHdr.setScheme(lObjTrnPensionChangeHdr.getScheme());
					lObjPensionBillHdr.setSchemeCode(lObjTrnPensionChangeHdr.getSchemeCode());
					lObjPensionBillHdr.setPayMode(lStrPayMode);
					lObjPensionBillHdr.setTrnCounter(lObjTrnPensionChangeHdr.getTrnCounter());
					lObjPensionBillHdr.setLocationCode(lObjTrnPensionChangeHdr.getLocationCode());
					lObjPensionBillHdr.setGrossAmount(lObjTrnPensionChangeHdr.getGrossAmount());
					lObjPensionBillHdr.setNetAmount(lObjTrnPensionChangeHdr.getNetAmount());
					lObjPensionBillHdr.setDeductionA(lObjTrnPensionChangeHdr.getRecoveryAmount());
					lObjPensionBillHdr.setDeductionB(lObjTrnPensionChangeHdr.getDeductionB());
					if (lObjTrnPensionChangeHdr.getNoOfPnsr() > 0) {
						lObjPensionBillHdr.setNoOfPnsr(lObjTrnPensionChangeHdr.getNoOfPnsr());
					} else {
						lObjPensionBillHdr.setNoOfPnsr(Integer.valueOf(lLstTrnPnsnChngDtlsId.size()).longValue());
					}
					lObjPensionBillHdr.setCreatedDate(gDate);
					lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
					lLstTrnPensionBillHdr.add(lObjPensionBillHdr);
					lObjPensionBillHdr = null;
					// -----
					lDblHeadWiseGrossAmt += lObjTrnPensionChangeHdr.getGrossAmount().doubleValue();
					lDblHeadWiseRecAmt += lObjTrnPensionChangeHdr.getRecoveryAmount().doubleValue();
					lDblHeadWiseNetAmt += lObjTrnPensionChangeHdr.getNetAmount().doubleValue();
					for (Long lLngTrnPnsnChngDtlsId : lLstTrnPnsnChngDtlsId) {
						lLngBillDtlsId = ++lLngPkCntPensionBillDtls;
						lLngBillDtlsId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillDtlsId, inputMap);
						lObjTrnPensionChangeDtls = lMapTrnPensionChangeDtls.get(lLngTrnPnsnChngDtlsId);
						lObjPensionBillDtlVO = new TrnPensionBillDtls();
						lObjPensionBillDtlVO.setTrnPensionBillDtlsId(lLngBillDtlsId);
						lObjPensionBillDtlVO.setTrnPensionBillHdrId(lLngBillHdrId);
						lObjPensionBillDtlVO.setPpoNo(lObjTrnPensionChangeDtls.getPpoNo());
						lObjPensionBillDtlVO.setPensionerCode(lObjTrnPensionChangeDtls.getPensionerCode());
						lObjPensionBillDtlVO.setCvpMonthAmount(lObjTrnPensionChangeDtls.getCvpMonthAmount());
						lObjPensionBillDtlVO.setPensionAmount(lObjTrnPensionChangeDtls.getPensionAmount());
						lObjPensionBillDtlVO.setDpAmount(lObjTrnPensionChangeDtls.getDpAmount());
						lObjPensionBillDtlVO.setTiAmount(lObjTrnPensionChangeDtls.getTiAmount());
						lObjPensionBillDtlVO.setArrearAmount(lObjTrnPensionChangeDtls.getArrearAmount()); // arrear
						// amount
						// =
						// other
						// arrear
						// amount
						lObjPensionBillDtlVO.setTiArrearAmount(lObjTrnPensionChangeDtls.getTiArrearAmount());
						lObjPensionBillDtlVO.setRecoveryAmount(lObjTrnPensionChangeDtls.getRecoveryAmount());
						lObjPensionBillDtlVO.setReducedPension(lObjTrnPensionChangeDtls.getReducedPension());
						lObjPensionBillDtlVO.setAccountNo(lObjTrnPensionChangeDtls.getAccountNo());
						lObjPensionBillDtlVO.setPensionerName(lObjTrnPensionChangeDtls.getPensionerName());
						lObjPensionBillDtlVO.setAllcationBf11156(lObjTrnPensionChangeDtls.getAllcationBf11156());
						lObjPensionBillDtlVO.setAllcationAf11156(lObjTrnPensionChangeDtls.getAllcationAf11156());
						lObjPensionBillDtlVO.setAllcationAf10560(lObjTrnPensionChangeDtls.getAllcationAf10560());
						lObjPensionBillDtlVO.setPersonalPensionAmount(lObjTrnPensionChangeDtls.getPersonalPensionAmount());
						lObjPensionBillDtlVO.setIr1Amount(lObjTrnPensionChangeDtls.getIr1Amount());
						lObjPensionBillDtlVO.setIr2Amount(lObjTrnPensionChangeDtls.getIr2Amount());
						lObjPensionBillDtlVO.setIr3Amount(lObjTrnPensionChangeDtls.getIr3Amount());
						lObjPensionBillDtlVO.setTiAmount(lObjTrnPensionChangeDtls.getTiAmount());
						lObjPensionBillDtlVO.setPayForMonth(lObjTrnPensionChangeDtls.getPayForMonth());
						lObjPensionBillDtlVO.setAllcationBf1436(lObjTrnPensionChangeDtls.getAllcationBf1436());
						lObjPensionBillDtlVO.setAllcationAfZp(lObjTrnPensionChangeDtls.getAllcationAfZp());
						lObjPensionBillDtlVO.setNetAmount(lObjTrnPensionChangeDtls.getNetAmount());
						lObjPensionBillDtlVO.setDaRate(lObjTrnPensionChangeDtls.getDaRate());
						lObjPensionBillDtlVO.setGrossAmount(lObjTrnPensionChangeDtls.getGrossAmount());
						lObjPensionBillDtlVO.setHeadCode(new BigDecimal(lObjTrnPensionChangeDtls.getHeadCode()));
						lObjPensionBillDtlVO.setAdpAmount(lObjTrnPensionChangeDtls.getAdpAmount());
						lObjPensionBillDtlVO.setLedgerNo(lObjTrnPensionChangeDtls.getLedgerNo());
						lObjPensionBillDtlVO.setPageNo(lObjTrnPensionChangeDtls.getPageNo());
						lObjPensionBillDtlVO.setPeonAllowance(lObjTrnPensionChangeDtls.getPeonAllowance());
						lObjPensionBillDtlVO.setMedicalAllowenceAmount(lObjTrnPensionChangeDtls.getMedicalAllowenceAmount());
						lObjPensionBillDtlVO.setOtherBenefits(lObjTrnPensionChangeDtls.getOtherBenefits());
						lObjPensionBillDtlVO.setPensnCutAmount(lObjTrnPensionChangeDtls.getPensionCutAmount());
						lObjPensionBillDtlVO.setOther1(lObjTrnPensionChangeDtls.getOther1());
						lObjPensionBillDtlVO.setTotalArrearAmt(lObjTrnPensionChangeDtls.getTotalArrearAmt());
						lObjPensionBillDtlVO.setRopType(lObjTrnPensionChangeDtls.getRopType());
						lObjPensionBillDtlVO.setArrear6PC(lObjTrnPensionChangeDtls.getArrear6PC());
						lObjPensionBillDtlVO.setArrearLC(lObjTrnPensionChangeDtls.getArrearLC());
						lObjPensionBillDtlVO.setArrearCommutation(lObjTrnPensionChangeDtls.getArrearCommutation());
						lObjPensionBillDtlVO.setArrearOthrDiff(lObjTrnPensionChangeDtls.getArrearOthrDiff());
						lObjPensionBillDtlVO.setPunishmentCutAmt(lObjTrnPensionChangeDtls.getPunishmentCutAmt());
						lLstTrnPensionBillDtls.add(lObjPensionBillDtlVO);
						lObjPensionBillDtlVO = null;
						lLstPensionerCode.add(lObjTrnPensionChangeDtls.getPensionerCode());
					}
				}
				lDblBillGrossAmt += lDblHeadWiseGrossAmt;
				lDblBillRecAmt += lDblHeadWiseRecAmt;
				lDblBillNetAmt += lDblHeadWiseNetAmt;
				lObjTrnBillRegisterVO.setBillGrossAmount(new BigDecimal(lDblBillGrossAmt));
				lObjTrnBillRegisterVO.setBillNetAmount(new BigDecimal(lDblBillNetAmt));
				lObjTrnBillRegisterVO.setDeductionA(new BigDecimal(lDblBillRecAmt));
				lLstTrnBillRegisterVO.add(lObjTrnBillRegisterVO);

				lLstTrnBillChangeStmntMpg = new ArrayList<TrnBillChangeStmntMpg>();
				for (String lStrChangeRqstIdsBatch : lLstChangeRqstIdsBatch) {
					lLngBillChngStmntId = ++lLngPkCntBillChngStmntMpg;
					lLngBillChngStmntId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillChngStmntId, inputMap);
					lObjTrnBillChangeStmntMpg = new TrnBillChangeStmntMpg();
					lObjTrnBillChangeStmntMpg.setBillChngStmntId(lLngBillChngStmntId);
					lObjTrnBillChangeStmntMpg.setBillNo(lLngBillNo);
					lObjTrnBillChangeStmntMpg.setChngStmntIdList(lStrChangeRqstIdsBatch);
					lObjTrnBillChangeStmntMpg.setCreatedDate(gDate);
					lObjTrnBillChangeStmntMpg.setCreatedPostId(gLngPostId);
					lObjTrnBillChangeStmntMpg.setCreatedUserId(gLngUserId);
					lObjTrnBillChangeStmntMpg.setLocationCode(gStrLocCode);
					lObjTrnBillChangeStmntMpg.setDbId(gDbId.intValue());
					lObjTrnBillChangeStmntMpg.setForMonth(Integer.valueOf(lStrForMonthYear));
					lLstTrnBillChangeStmntMpg.add(lObjTrnBillChangeStmntMpg);
					lObjTrnBillChangeStmntMpg = null;
				}

				for (Object[] lArrObj : lLstBillPartyDtls) {
					lStrBillPartyName = (lArrObj[1] != null) ? lArrObj[1].toString() : "";
					lStrBillPartyBankCode = (String) lArrObj[0];
					lStrBillPartyBranchCode = (lArrObj[2] != null) ? lArrObj[2].toString() : null;
					lDblBillPartyNetAmt = (lArrObj[3] != null) ? ((BigInteger) lArrObj[3]).doubleValue() : 0;
					lObjBillParty = new RltBillParty();
					lLngBillPartyId = ++lLngPkCntRltBillParty;
					lLngBillPartyId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngBillPartyId, inputMap);
					lObjBillParty.setBillPartyId(lLngBillPartyId);
					lObjBillParty.setBillNo(lLngBillNo);
					lObjBillParty.setPartyCode(lStrSchemeCode);
					lObjBillParty.setPartyAmt(new BigDecimal(lDblBillPartyNetAmt));
					lObjBillParty.setCreatedDate(gDate);
					lObjBillParty.setCreatedPostId(gLngPostId);
					lObjBillParty.setCreatedUserId(gLngUserId);
					lObjBillParty.setDbId(gDbId);
					lObjBillParty.setPartyName(lStrBillPartyName);
					lObjBillParty.setLocationCode(gStrLocCode);
					lObjBillParty.setPaymentMode(lStrPayMode);
					lObjBillParty.setBankCode(lStrBillPartyBankCode);
					lObjBillParty.setBranchCode(lStrBillPartyBranchCode);
					lLstRltBillParty.add(lObjBillParty);
					lObjBillParty = null;
					lStrBillPartyName = null;
					lStrBillPartyBankCode = null;
					lStrBillPartyBranchCode = null;
					lDblBillPartyNetAmt = null;
				}
				// --Setting bill number in arrear details of all pensioner code
				// in bill.

				if (lStrForMonthYear != null && lLstPensionerCode.size() > 0) {
					lObjMonthlyPensionBillDAO.updateMonthlyBillNoInArrearDtls(lLngBillNo, Integer.valueOf(lStrForMonthYear), gLngUserId, gLngPostId, gDate, gStrLocCode, lStrSchemeCode,
							Short.parseShort(bundleConst.getString("BILLTYPE.MONTHLY")), lStrECSBranchFlag);
					lObjMonthlyPensionBillDAO.updateMonthlyBillNoForRecovery(lLstPensionerCode, Integer.valueOf(lStrForMonthYear), gStrLocCode, lLngBillNo, gLngUserId, gLngPostId, gDate,
							lStrSchemeCode, Short.parseShort(bundleConst.getString("BILLTYPE.MONTHLY")), lStrECSBranchFlag);
				}
				lLstPensionerCode = null;
				// hitStg.saveOrUpdateAll(lLstTrnBillRegisterVO);
				// lLstTrnBillRegisterVO = null;
				// hitStg.saveOrUpdateAll(lLstTrnPensionBillHdr);
				// lLstTrnPensionBillHdr = null;
				// hitStg.saveOrUpdateAll(lLstTrnPensionBillDtls);
				// lLstTrnPensionBillDtls = null;
				// hitStg.saveOrUpdateAll(lLstTrnBillMvmnt);
				// lLstTrnBillMvmnt = null;
				// hitStg.saveOrUpdateAll(lLstTrnBillChangeStmntMpg);
				// lLstTrnBillChangeStmntMpg = null;
				// hitStg.saveOrUpdateAll(lLstRltBillParty);
				// lLstRltBillParty = null;

				for (TrnBillRegister lObjTrnBillRegister : lLstTrnBillRegisterVO) {
					hitStg.save(lObjTrnBillRegister);
				}
				lLstTrnBillRegisterVO = null;
				for (TrnPensionBillHdr lObjTrnPensionBillHdr : lLstTrnPensionBillHdr) {
					hitStg.save(lObjTrnPensionBillHdr);
				}
				lLstTrnPensionBillHdr = null;

				for (TrnPensionBillDtls lObjTrnPensionBillDtls : lLstTrnPensionBillDtls) {
					hitStg.save(lObjTrnPensionBillDtls);
				}
				lLstTrnPensionBillDtls = null;

				for (TrnBillMvmnt lTrnBillMvmnt : lLstTrnBillMvmnt) {
					hitStg.save(lTrnBillMvmnt);
				}
				lLstTrnBillMvmnt = null;

				for (TrnBillChangeStmntMpg lTrnBillChangeStmntMpg : lLstTrnBillChangeStmntMpg) {
					hitStg.save(lTrnBillChangeStmntMpg);
				}
				lLstTrnBillChangeStmntMpg = null;

				for (RltBillParty lRltBillParty : lLstRltBillParty) {
					hitStg.save(lRltBillParty);
				}
				lLstRltBillParty = null;
				// lObjBptmCommonServicesDAO = new
				// BptmCommonServicesDAOImpl(TrnBillRegister.class,
				// srvcLoc.getSessionFactory());
				// for (TrnBillRegister lObjTrnBillRegister :
				// lLstTrnBillRegisterVO) {
				// lObjBptmCommonServicesDAO.create(lObjTrnBillRegister);
				// }
				// lLstTrnBillRegisterVO = null;
				//
				// lObjBptmCommonServicesDAO = new
				// BptmCommonServicesDAOImpl(TrnPensionBillHdr.class,
				// srvcLoc.getSessionFactory());
				// for (TrnPensionBillHdr lObjTrnPensionBillHdr :
				// lLstTrnPensionBillHdr) {
				// lObjBptmCommonServicesDAO.create(lObjTrnPensionBillHdr);
				// }
				// lLstTrnPensionBillHdr = null;
				//
				// lObjBptmCommonServicesDAO = new
				// BptmCommonServicesDAOImpl(TrnPensionBillDtls.class,
				// srvcLoc.getSessionFactory());
				// for (TrnPensionBillDtls lObjTrnPensionBillDtls :
				// lLstTrnPensionBillDtls) {
				// lObjBptmCommonServicesDAO.create(lObjTrnPensionBillDtls);
				// }
				// lLstTrnPensionBillDtls = null;
				//
				// lObjBptmCommonServicesDAO = new
				// BptmCommonServicesDAOImpl(TrnBillMvmnt.class,
				// srvcLoc.getSessionFactory());
				// for (TrnBillMvmnt lTrnBillMvmnt : lLstTrnBillMvmnt) {
				// lObjBptmCommonServicesDAO.create(lTrnBillMvmnt);
				// }
				// lLstTrnBillMvmnt = null;
				//
				// lObjBptmCommonServicesDAO = new
				// BptmCommonServicesDAOImpl(TrnBillChangeStmntMpg.class,
				// srvcLoc.getSessionFactory());
				// for (TrnBillChangeStmntMpg lTrnBillChangeStmntMpg :
				// lLstTrnBillChangeStmntMpg) {
				// lObjBptmCommonServicesDAO.create(lTrnBillChangeStmntMpg);
				// }
				// lLstTrnBillChangeStmntMpg = null;
				// lObjBptmCommonServicesDAO = new
				// BptmCommonServicesDAOImpl(RltBillParty.class,
				// srvcLoc.getSessionFactory());
				// for (RltBillParty lRltBillParty : lLstRltBillParty) {
				// lObjBptmCommonServicesDAO.create(lRltBillParty);
				// }
				// lLstRltBillParty = null;

				// lObjMonthlyPensionBillDAO.saveAllCollection(lLstTrnBillRegisterVO);
				// lLstTrnBillRegisterVO = null;
				// lObjMonthlyPensionBillDAO.saveAllCollection(lLstTrnPensionBillHdr);
				// lLstTrnPensionBillHdr = null;
				// lObjMonthlyPensionBillDAO.saveAllCollection(lLstTrnPensionBillDtls);
				// lLstTrnPensionBillDtls = null;
				// lObjMonthlyPensionBillDAO.saveAllCollection(lLstTrnBillMvmnt);
				// lLstTrnBillMvmnt = null;
				// lObjMonthlyPensionBillDAO.saveAllCollection(lLstRltBillParty);
				// lLstRltBillParty = null;

				lSBStatus.append("<XMLDOC>");
				lSBStatus.append("<STATUS>");
				lSBStatus.append("SUCCESS");
				lSBStatus.append("</STATUS>");
				lSBStatus.append("</XMLDOC>");
			} else if (lSetBranchCodesInChngStmnt.size() == 0) {
				lSBStatus.append("<XMLDOC>");
				lSBStatus.append("<STATUS>");
				lSBStatus.append("NOBRANCH");
				lSBStatus.append("</STATUS>");
				lSBStatus.append("</XMLDOC>");
			} else {
				lSBStatus.append("<XMLDOC>");
				lSBStatus.append("<STATUS>");
				lSBStatus.append("NONEWBRANCH");
				lSBStatus.append("</STATUS>");
				lSBStatus.append("</XMLDOC>");
			}
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			// inputMap.put("billNo", lLngBillNo);
			//
			// lLstMonthlyPensionBillVO =
			// lObjMonthlyPensionBillDAO.getMonthlyPensionBillVOList(lLngBillNo,
			// gStrLocCode);
			// inputMap.put("MonthlyPensionList", lLstMonthlyPensionBillVO);
			// inputMap.put("TrnPensionBillHdrLst", lLstTrnPensionBillHdr);
			// inputMap.put("TrnBillRegister", lObjTrnBillRegisterVO);
			// inputMap.put("BillRegVO", lObjTrnBillRegisterVO);
			// lLstMstPensionHeadcode =
			// lObjMonthlyPensionBillDAO.getAllHeadCodeAndDesc();
			//
			// for (MstPensionHeadcode lObjMstPensionHeadcode :
			// lLstMstPensionHeadcode) {
			// lMpHeadCodeDesc.put(lObjMstPensionHeadcode.getHeadCode(),
			// lObjMstPensionHeadcode.getDescription());
			// }
			//
			// List lHeadCodeWiseDtlsLst = new ArrayList();
			// for (TrnPensionBillHdr lObjTrnPensionBillHdr :
			// lLstTrnPensionBillHdr) {
			// List lTemp = new ArrayList();
			//
			// lTemp.add(lObjTrnPensionBillHdr.getHeadCode());
			// lTemp.add(lMpHeadCodeDesc.get(lObjTrnPensionBillHdr.getHeadCode()));
			// lTemp.add(lObjTrnPensionBillHdr.getGrossAmount());
			// lTemp.add(lObjTrnPensionBillHdr.getDeductionA());
			// lTemp.add(lObjTrnPensionBillHdr.getDeductionB());
			// lTemp.add(lObjTrnPensionBillHdr.getNetAmount());
			//
			// lHeadCodeWiseDtlsLst.add(lTemp);
			//
			// }
			// lTotalNetAmt = lObjTrnBillRegisterVO.getBillNetAmount();
			// inputMap.put("TotalNetAmt", lTotalNetAmt);
			// RuleBasedNumberFormat fomatter = new
			// RuleBasedNumberFormat(IReportConstants.INDIAN_ENG_RULE_SET);
			// String result = fomatter.format(new
			// com.ibm.icu.math.BigDecimal(lTotalNetAmt));
			// inputMap.put("TotalNetAmtInWords", result);
			// inputMap.put("HeadCodeWiseDtls", lHeadCodeWiseDtlsLst);
			// inputMap.put("TrnBillRegisterVO", lObjTrnBillRegisterVO);
			//
			// String lStrDatePayMonth =
			// lObjTrnMonthlyChangeRqstVO.getForMonth().toString();
			// MonthlyPensionBillDAOImpl lObjMonthlyDAO = new
			// MonthlyPensionBillDAOImpl(MstPensionerHdr.class,
			// srvcLoc.getSessionFactory());
			// String lStrMonth = lStrDatePayMonth.substring(4);
			// String lStrYear = lStrDatePayMonth.substring(0, 4);
			// List lLstMonthDtls = lObjMonthlyDAO.getMonthDtls(lStrMonth,
			// gLngLangId);
			// inputMap.put("MonthName", lLstMonthDtls.get(1).toString());
			// inputMap.put("MonthCode", lLstMonthDtls.get(0).toString());
			// inputMap.put("ForBillYear", lStrYear);
			//
			// List lLstBank = new ArrayList();
			// inputMap.put("Branch",
			// lObjTrnMonthlyChangeRqstVO.getBranchCode());
			// String lStrBankName = null;
			// String lStrBranchName = null;
			// lLstBank =
			// lObjMonthlyDAO.getBankCode(lObjTrnMonthlyChangeRqstVO.getBranchCode(),
			// gStrLocCode);
			// if (lLstBank != null && !lLstBank.isEmpty()) {
			// Object[] lObjArr = (Object[]) lLstBank.get(0);
			// lStrBankName = (String) lObjArr[1];
			// lStrBranchName = (String) lObjArr[2];
			// inputMap.put("BankName", lStrBankName);
			// inputMap.put("BranchName", lStrBranchName);
			// } else {
			// inputMap.put("BankName", "");
			// inputMap.put("BranchName", "");
			//
			// }
			//
			// getMonthlyReport(inputMap);
			// // getSummary(inputMap);
			// resObj.setResultValue(inputMap);
			// // resObj.setViewName("GenerateBillVO");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject loadViewMonthlyPensionBillList(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		MonthlyPensionBillDAO lObjPensionBillDAO = null;
		List<String> lLstSchemeCodes = null;
		List<SgvaMonthMst> lObjSgvaMonthMst = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		List<SgvcFinYearMst> lObjSgvcFinYearMst = null;
		String currentMonth = null;
		String currentYear = null;
		List<Object[]> lLstMonthlyBillDtls = null;
		Integer lTotalRecords = 0;
		String lStrSearchCrt1 = null;
		String lStrSearchCrt2 = null;
		String lStrSearchCrt1Value = null;
		String lStrSearchCrt2Value = null;
		Integer lIntForMonth = null;
		String lStrForMonth = null;
		try {
			setSessionInfo(inputMap);
			lStrSearchCrt1 = StringUtility.getParameter("lStrSearchCrt1", request).trim();
			lStrSearchCrt2 = StringUtility.getParameter("lStrSearchCrt2", request).trim();
			lStrSearchCrt1Value = StringUtility.getParameter("lStrSearchVal1", request).trim();
			lStrSearchCrt2Value = StringUtility.getParameter("lStrSearchVal2", request).trim();
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lObjSgvaMonthMst = new ArrayList<SgvaMonthMst>();
			lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lLstSchemeCodes = lObjPensionBillDAO.getAllSchemeCode();
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			currentMonth = new SimpleDateFormat("MM").format(gDate);
			currentYear = new SimpleDateFormat("yyyy").format(gDate);
			lStrForMonth = currentYear + currentMonth;
			lIntForMonth = Integer.valueOf(lStrForMonth);
			inputMap.put("CurrentMonth", Integer.parseInt(currentMonth));
			inputMap.put("CurrentYear", currentYear);
			lLstSchemeCodes = lObjPensionBillDAO.getAllSchemeCode();
			lObjSgvaMonthMst = lObjCommonPensionDAO.getSgvaMonthMstVO(lStrLangId);
			if (lObjSgvaMonthMst != null) {
				inputMap.put("SgvaMonthMstVOArray", lObjSgvaMonthMst);
			}
			lObjSgvcFinYearMst = new ArrayList<SgvcFinYearMst>();
			lObjSgvcFinYearMst = lObjCommonPensionDAO.getSgvcFinYearMstVO(lStrLangId);
			if (lObjSgvcFinYearMst != null) {
				inputMap.put("SgvcFinYearMstVOArray", lObjSgvcFinYearMst);
			}
			lTotalRecords = lObjPensionBillDAO.getViewMonthlyPensionBillListCount(gStrLocCode, lStrSearchCrt1, lStrSearchCrt2, lStrSearchCrt1Value, lStrSearchCrt2Value, lIntForMonth);
			if (lTotalRecords > 0) {
				lLstMonthlyBillDtls = lObjPensionBillDAO.getViewMonthlyPensionBillListDtls(gStrLocCode, displayTag, lStrSearchCrt1, lStrSearchCrt2, lStrSearchCrt1Value, lStrSearchCrt2Value,
						lIntForMonth);
			}
			inputMap.put("lLstMonthlyBillDtls", lLstMonthlyBillDtls);
			inputMap.put("totalRecords", lTotalRecords);
			inputMap.put("lLstSchemeCode", lLstSchemeCodes);
			inputMap.put("lStrSearchCrt1", lStrSearchCrt1);
			inputMap.put("lStrSearchCrt2", lStrSearchCrt2);
			inputMap.put("lStrSearchCrt1Value", lStrSearchCrt1Value);
			inputMap.put("lStrSearchCrt2Value", lStrSearchCrt2Value);
			resObj.setResultValue(inputMap);
			resObj.setViewName("viewMonthlyPnsnBill");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject rejectAllChangeStmntsOnRejectMonthlyBill(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		PensionBillDAO lObjPensionBillDAO = null;
		String lStrForMonth = "";
		StringBuilder lSBStatus = new StringBuilder();
		List<Long> lLstBillNo = null;
		List<Object[]> lLstPnsrDtls = null;
		List<String> lLstPnsrCode = new ArrayList<String>();
		List<String> lLstPPONo = new ArrayList<String>();
		Long lLngBillNo = null;
		Map<Long, List<Object[]>> lMapBillNoPnsrDtls = new HashMap<Long, List<Object[]>>();
		List<Object[]> lLstPnsrCodePPONo = null;
		Object[] lArrPnsrCodePPONo = null;
		Integer lIntForMonth = null;
		try {
			setSessionInfo(inputMap);
			lStrForMonth = StringUtility.getParameter("forMonth", request);
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			lLstBillNo = lObjMonthlyPensionBillDAO.getAllBillNoToReject(lStrForMonth, gStrLocCode);
			lObjMonthlyPensionBillDAO.rejectAllMonthlyBilL(lStrForMonth, gStrLocCode, gLngUserId, gLngPostId);
			// lObjMonthlyPensionBillDAO.rejectAllChangeStmntOnRejectOfBill(lStrForMonth,
			// gStrLocCode, gLngUserId, gLngPostId)
			// --Setting arrear paid flag to N on rejection of bill.
			if (lLstBillNo != null && lLstBillNo.size() > 0) {
				lObjPensionBillDAO.updatePaidFlagInArrearDtlsOnApproveReject(lLstBillNo, "N", gLngUserId, gLngPostId, gDate);
			}
			/*
			 * lLstPnsrDtls =
			 * lObjMonthlyPensionBillDAO.getAllPensionerCodeOfBill(lLstBillNo);
			 * 
			 * // ---Preparing map of billwise pensionercode,ppono details.
			 * 
			 * for (Object[] lArrObj : lLstPnsrDtls) { lLngBillNo = (Long)
			 * lArrObj[2]; lArrPnsrCodePPONo = new Object[3];
			 * lArrPnsrCodePPONo[0] = lArrObj[0];// pensionercode
			 * lArrPnsrCodePPONo[1] = lArrObj[1];// ppono lArrPnsrCodePPONo[2] =
			 * lArrObj[3];// formonth if (lLngBillNo != null) {
			 * lLstPnsrCodePPONo = lMapBillNoPnsrDtls.get(lLngBillNo); if
			 * (lLstPnsrCodePPONo != null) {
			 * lLstPnsrCodePPONo.add(lArrPnsrCodePPONo); } else {
			 * lLstPnsrCodePPONo = new ArrayList<Object[]>();
			 * lLstPnsrCodePPONo.add(lArrPnsrCodePPONo); }
			 * lMapBillNoPnsrDtls.put(lLngBillNo, lLstPnsrCodePPONo); } }
			 */

			for (Long lBillNo : lLstBillNo) {
				/*
				 * lLstPnsrCodePPONo = lMapBillNoPnsrDtls.get(lBillNo); for
				 * (Object[] lArrObj : lLstPnsrCodePPONo) { if (lArrObj[0] !=
				 * null && lArrObj[0].toString().length() > 0) {
				 * lLstPnsrCode.add(lArrObj[0].toString()); } if (lArrObj[1] !=
				 * null && lArrObj[1].toString().length() > 0) {
				 * lLstPPONo.add(lArrObj[1].toString()); } if (lArrObj[2] !=
				 * null) { lIntForMonth = (Integer) lArrObj[2]; } }
				 */
				// if (lIntForMonth != null) {
				if (lBillNo != null) {
					/*
					 * --Updating six pay arrear paid flag to "Y"
					 */
					lObjPensionBillDAO.updateSixpayArrearPaidFlag(lLstPnsrCode, gLngUserId, gLngUserId, gDate, 'N', lIntForMonth, lBillNo);
				}
				// }
			}
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<STATUS>");
			lSBStatus.append("REJECTED");
			lSBStatus.append("</STATUS>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject viewMonthlyPensionOuterBill(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		MonthlyPensionBillDAO lObjPensionBillDAO = null;
		List<String> lLstSchemeCodes = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lObjSmplDateFormat = new SimpleDateFormat("MMMM yyyy ");

		String lStrTreasuryName = null;
		List<Object[]> lLstMonthlyBillOuterDtls = null;
		List<Object[]> lLstRecoveryDtls = null;
		StringBuilder lSbHeader = new StringBuilder();
		StringBuilder lSbFooter = new StringBuilder();
		String lStrMonthlyOuterBill = "";
		String lStrRecoveryDtls = "";
		String lStrMajorHead = "";
		String lStrSubMajorHead = "";
		String lStrMinorHead = "";
		String lStrSubMinorHead = "";
		String lStrSubHead = "";
		String lStrDemandCode = "";
		String lStrCharged = "";
		String lStrPlan = "";
		String lStrFooter = "";
		String lStrNetAmount = "";
		Double lDbNetBillAmt = 0D;
		Double lDbDeductionAmt = 0D;
		String lineSeperator = "\r\n";
		Integer lIntNoOfLinesPrintedOnPage = 0;
		Date lDtBillDate = null;

		Double lDbTotalIr1Amt = 0D;
		Double lDbTotalIr2Amt = 0D;
		Double lDbTotalIr3Amt = 0D;
		Double lDbTotalAlloc1Amt = 0D;
		Double lDbTotalAlloc2Amt = 0D;
		Double lDbTotalAlloc3Amt = 0D;
		Double lDbTotalAlloc4Amt = 0D;
		Double lDbTotalAlloc5Amt = 0D;
		List<Object[]> lLstCategoryWiseSummary = new ArrayList<Object[]>();
		List<Object[]> lLstCategoryWiseDtls = new ArrayList<Object[]>();
		Map<String, List> lMapCategoryWiseSummary = new HashMap<String, List>();
		try {
			setSessionInfo(inputMap);
			String lStrBillNo = StringUtility.getParameter("billNo", request);
			String lStrForMonthYear = StringUtility.getParameter("forMonthYear", request);
			String lStrBillDate = StringUtility.getParameter("billDate", request);
			String lStrBillFlag = StringUtility.getParameter("billFlag", request);

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());
			lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			lLstMonthlyBillOuterDtls = lObjPensionBillDAO.getMonthlyPensionBillOuterDtls(lStrBillNo, gStrLocCode, null);
			ArrayList inList = new ArrayList();

			if (!lLstMonthlyBillOuterDtls.isEmpty()) {
				lStrTreasuryName = lObjPhysicalCaseInwardDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocCode));
				ColumnVo[] columnHeading = new ColumnVo[6];
				columnHeading[0] = new ColumnVo("Bank Name", 1, 32, 0, true, false, true);
				columnHeading[1] = new ColumnVo("Branch Name", 1, 32, 0, true, false, true);
				columnHeading[2] = new ColumnVo("No of    Pensioner", 2, 9, 1, false, false, true);
				columnHeading[3] = new ColumnVo("Gross", 2, 17, 1, false, false, true);
				columnHeading[4] = new ColumnVo("Deduction", 2, 17, 1, false, false, true);
				columnHeading[5] = new ColumnVo("Net", 2, 17, 1, false, false, true);

				List<List> arrOuter = new ArrayList<List>();
				ReportExportHelper lObjExport = new ReportExportHelper();

				for (int lIntCnt = 0; lIntCnt < lLstMonthlyBillOuterDtls.size(); lIntCnt++) {
					Object[] obj = lLstMonthlyBillOuterDtls.get(lIntCnt);
					if (lIntCnt == 0) {
						lSbHeader.append("MTR 38");
						lSbHeader.append("\r\n");
						lSbHeader.append("2071 PENSION BILL FOR THE MONTH OF ");
						if (obj[18] != null) {
							lSbHeader.append(lObjSmplDateFormat.format(lObjSimpleDateFormat.parse(obj[18].toString())));
						}
						lSbHeader.append("\r\n");
						lSbHeader.append("Treasury Name : " + lStrTreasuryName);
						lSbHeader.append("\r\n");

						lSbHeader.append(String.format("%-85s", "Scheme Descr. " + obj[3]));
						if (obj[10] != null) {
							lStrMajorHead = obj[10].toString();
						}
						if (obj[11] != null) {
							lStrSubMajorHead = obj[11].toString();
						}
						if (obj[12] != null) {
							lStrMinorHead = obj[12].toString();
						}
						if (obj[13] != null) {
							lStrSubMinorHead = obj[13].toString();
						}
						if (obj[14] != null) {
							lStrSubHead = obj[14].toString();
						}
						if (obj[15] != null) {
							lStrDemandCode = obj[15].toString();
						}
						if (obj[16] != null) {
							if ("C".equals(obj[16])) {
								lStrCharged = "Charged";
							} else if ("V".equals(obj[16])) {
								lStrCharged = "Voted";
							}
						}
						if (obj[17] != null) {
							if ("P".equals(obj[17])) {
								lStrPlan = "Plan";
							} else if ("N".equals(obj[17])) {
								lStrPlan = "Non Plan";
							}
						}
						lSbHeader.append("\r\n");
						lSbHeader.append(String.format("%40s", "Scheme Code : " + obj[2] + "    "));
						lSbHeader.append(String.format("%15s", "Demand Code : " + lStrDemandCode + " "));
						lSbHeader.append(String.format("%20s", "Major Head : " + lStrMajorHead + "  "));
						lSbHeader.append(String.format("%20s", "Sub Major Head : " + lStrSubMajorHead + "  "));
						lSbHeader.append(String.format("%20s", "Minor Head : " + lStrMinorHead + "  "));
						lSbHeader.append(String.format("%16s", "Sub Minor Head : " + lStrSubMinorHead + "  "));
						lSbHeader.append(String.format("%15s", "Sub Head : " + lStrSubHead + "  "));
						lSbHeader.append("\r\n");
						lSbHeader.append(String.format("%16s", "Charged/Voted : " + lStrCharged + "   "));
						lSbHeader.append(String.format("%16s", "Plan/Non Plan : " + lStrPlan));
						lSbHeader.append("\r\n");
						lSbHeader.append(String.format("%30s", "Bill No  : " + obj[1] + "    "));
						lSbHeader.append(String.format("%-95s", "Date   : " + lStrBillDate));
						lSbHeader.append("\r\n");
					}

					inList = new ArrayList();
					inList.add(obj[4]);
					inList.add(obj[5]);
					inList.add(obj[6]);
					inList.add(obj[7]);
					inList.add(obj[8]);
					inList.add(obj[9]);
					lDbNetBillAmt = lDbNetBillAmt + Double.parseDouble(obj[9].toString());
					lDbDeductionAmt = lDbDeductionAmt + Double.parseDouble(obj[8].toString());
					arrOuter.add(inList);

				}
				lStrMonthlyOuterBill = lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), lStrFooter, null, 132, true, 1, true, "|");
				if (lStrMonthlyOuterBill != null) {
					lIntNoOfLinesPrintedOnPage = (lStrMonthlyOuterBill.split(lineSeperator)).length;
				}

				if ("S".equals(lStrBillFlag)) {
					lLstRecoveryDtls = lObjPensionBillDAO.getSupplBillSchemeCodeWiseRecovery(lStrBillNo);
				} else {
					lLstRecoveryDtls = lObjPensionBillDAO.getSchemeCodeWiseRecovery(lStrBillNo, gStrLocCode);
				}
				if (!lLstRecoveryDtls.isEmpty()) {
					lSbHeader = new StringBuilder();
					arrOuter = new ArrayList<List>();
					columnHeading = new ColumnVo[2];
					columnHeading[0] = new ColumnVo("Deduction Scheme Code", 1, 28, 0, true, false, true);
					columnHeading[1] = new ColumnVo("Amount", 2, 20, 1, false, false, true);

					for (int lIntCnt = 0; lIntCnt < lLstRecoveryDtls.size(); lIntCnt++) {
						Object[] obj = lLstRecoveryDtls.get(lIntCnt);
						inList = new ArrayList();
						inList.add(obj[0]);
						if (obj[1] != null) {
							inList.add(obj[1]);
						}
						arrOuter.add(inList);
					}
					lStrMonthlyOuterBill = lStrMonthlyOuterBill
							+ lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), lStrFooter, null, 50, true, lIntNoOfLinesPrintedOnPage, true, "|");
				}
				if (lStrMonthlyOuterBill != null) {
					lIntNoOfLinesPrintedOnPage = lStrMonthlyOuterBill.split(lineSeperator).length;
				}

				List lLstTotalAmt = lObjPensionBillDAO.getMonthlyOuterBillTotalAmts(lStrBillNo, gStrLocCode);
				List<List<Object>> lLstOuter = new ArrayList<List<Object>>();
				List lLstData = new ArrayList();
				if (!lLstTotalAmt.isEmpty()) {
					// Object obj = lLstTotalAmt.get(0);
					Object[] obj = (Object[]) lLstTotalAmt.get(0);
					lDbTotalIr1Amt = Double.parseDouble(obj[0].toString());
					lDbTotalIr2Amt = Double.parseDouble(obj[1].toString());
					lDbTotalIr3Amt = Double.parseDouble(obj[2].toString());
					lDbTotalAlloc1Amt = Double.parseDouble(obj[3].toString());
					lDbTotalAlloc2Amt = Double.parseDouble(obj[4].toString());
					lDbTotalAlloc3Amt = Double.parseDouble(obj[5].toString());
					lDbTotalAlloc4Amt = Double.parseDouble(obj[6].toString());
					lDbTotalAlloc5Amt = Double.parseDouble(obj[7].toString());

					ColumnVo[] columnIrAllocAmt = new ColumnVo[8];
					columnIrAllocAmt[0] = new ColumnVo("IR1", 1, 10, 0, false, false, true);
					columnIrAllocAmt[1] = new ColumnVo("IR2", 1, 10, 0, true, false, true);
					columnIrAllocAmt[2] = new ColumnVo("IR3", 2, 10, 0, false, false, true);
					columnIrAllocAmt[3] = new ColumnVo("ALLOC1", 2, 10, 0, false, false, true);
					columnIrAllocAmt[4] = new ColumnVo("ALLOC2", 2, 10, 0, false, false, true);
					columnIrAllocAmt[5] = new ColumnVo("ALLOC3", 2, 10, 0, false, false, true);
					columnIrAllocAmt[6] = new ColumnVo("ALLOC4", 2, 10, 0, false, false, true);
					columnIrAllocAmt[7] = new ColumnVo("ALLOC5", 2, 10, 0, false, false, true);

					lLstData.add(lDbTotalIr1Amt.longValue());
					lLstData.add(lDbTotalIr2Amt.longValue());
					lLstData.add(lDbTotalIr3Amt.longValue());
					lLstData.add(lDbTotalAlloc1Amt.longValue());
					lLstData.add(lDbTotalAlloc2Amt.longValue());
					lLstData.add(lDbTotalAlloc3Amt.longValue());
					lLstData.add(lDbTotalAlloc4Amt.longValue());
					lLstData.add(lDbTotalAlloc5Amt.longValue());
					lLstOuter.add(lLstData);

					lStrMonthlyOuterBill = lStrMonthlyOuterBill
							+ lObjExport.getReportFile(lLstOuter, columnIrAllocAmt, lSbHeader.toString(), lStrFooter, null, 88, true, lIntNoOfLinesPrintedOnPage, true, "|");
				}
				if (lStrMonthlyOuterBill != null) {
					lIntNoOfLinesPrintedOnPage = (lStrMonthlyOuterBill.split(lineSeperator)).length;
				}
				lStrMonthlyOuterBill = lStrMonthlyOuterBill + "Category Wise Summary \r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 2;

				ColumnVo[] columnCtgryWiseSumry = new ColumnVo[3];
				columnCtgryWiseSumry[0] = new ColumnVo("No Of Pensioner", 2, 10, 1, false, false, true);
				columnCtgryWiseSumry[1] = new ColumnVo("Category Code Description", 1, 35, 0, true, false, true);
				columnCtgryWiseSumry[2] = new ColumnVo("Net Amount Rs.", 2, 14, 1, false, false, true);

				lLstOuter = new ArrayList<List<Object>>();
				lLstData = new ArrayList();
				lLstCategoryWiseSummary = lObjPensionBillDAO.getCategoryWiseSummary(lStrBillNo);
				if (!lLstCategoryWiseSummary.isEmpty()) {
					for (Object[] cols : lLstCategoryWiseSummary) {
						lLstData = new ArrayList();
						lLstData.add(cols[0]);
						lLstData.add(cols[2]);
						lLstData.add((long) (Double.parseDouble(cols[3].toString())));
						lLstOuter.add(lLstData);
					}
				}
				lStrMonthlyOuterBill = lStrMonthlyOuterBill
						+ lObjExport.getReportFile(lLstOuter, columnCtgryWiseSumry, lSbHeader.toString(), lStrFooter, null, 62, true, lIntNoOfLinesPrintedOnPage, true, "|");

				if (lStrMonthlyOuterBill != null) {
					lIntNoOfLinesPrintedOnPage = (lStrMonthlyOuterBill.split(lineSeperator)).length;
				}

				lStrNetAmount = "Below Rs.  " + lDbNetBillAmt.longValue() + "   " + "(In Words  " + EnglishDecimalFormat.convert1WithSpace(lDbNetBillAmt.longValue()) + " Only)" + "\r\n\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 2;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
					lIntNoOfLinesPrintedOnPage = 2;
				}

				lSbHeader = new StringBuilder();
				lSbFooter.append("\r\n");
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lSbFooter.append(String.format("%121s", "Signature With Stamp"));
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lSbFooter.append("\r\n");
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lSbFooter.append(String.format("%120s", "D.D.O. Pension"));
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lSbFooter.append("\r\n");
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lSbFooter.append("\r\n");
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
					lIntNoOfLinesPrintedOnPage = 2;
				}

				List blankList = new ArrayList();
				blankList.add("");
				blankList.add("");
				blankList.add("");
				lStrFooter = "------------------Used for Audit Section Only--------------------------------------Used for Cheque/Cash Section-------------------\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 2;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
					lIntNoOfLinesPrintedOnPage = 2;
				}
				columnHeading = new ColumnVo[3];
				columnHeading[0] = new ColumnVo("", 1, 60, 0, true, false, true);
				columnHeading[1] = new ColumnVo("", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("", 1, 40, 0, false, false, true);
				arrOuter = new ArrayList<List>();
				// inList = new ArrayList();
				// inList.add("-----------------Used for Audit Section Only------------------");
				// inList.add("----------------Used for Cheque/Cash Section------------------");
				// arrOuter.add(inList);
				arrOuter.add(blankList);
				inList = new ArrayList();
				inList.add("PASSED FOR PAYMENT AND PAY RS. " + lDbNetBillAmt.longValue() + "/-");
				inList.add("");
				inList.add("CHEQUE NO/ECS/");
				arrOuter.add(inList);
				inList = new ArrayList();
				inList.add("IN WORDS RUPEES " + (EnglishDecimalFormat.convert1WithSpace(lDbNetBillAmt.longValue())).toUpperCase() + " Only");
				inList.add("");
				inList.add("EFT/NEFT Advice No");
				arrOuter.add(inList);
				inList = new ArrayList();
				inList.add("");
				inList.add("");
				inList.add("Date");
				arrOuter.add(inList);
				arrOuter.add(blankList);
				arrOuter.add(blankList);
				arrOuter.add(blankList);
				inList = new ArrayList();
				inList.add("ASST.PAY AND ACCOUNT OFFICER," + lStrTreasuryName + " :");
				inList.add("");
				inList.add("ASST.PAY AND ACCOUNT OFFICER," + lStrTreasuryName);
				arrOuter.add(inList);
				inList = new ArrayList();
				inList.add("ADDL.TREASURY OFFICER  :");
				inList.add("");
				inList.add("ADDL.TREASURY OFFICER  ");
				arrOuter.add(inList);

				lStrFooter = lStrFooter + lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), "", null, 132, true, lIntNoOfLinesPrintedOnPage, false, "");
				if (lStrFooter != null) {
					lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + (lStrFooter.split(lineSeperator)).length;
				}

				lStrFooter = lStrFooter + "------------------For Purpose Of A.G.---------------------------------------------------------------------------------------------\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 2;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lStrFooter = lStrFooter + "Admitted for Rs.\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lStrFooter = lStrFooter + "Objected for Rs.\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lStrFooter = lStrFooter + "\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lStrFooter = lStrFooter + "\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
					lIntNoOfLinesPrintedOnPage = 2;
				}
				lStrFooter = lStrFooter + String.format("%70s", "Audit Officer");
				lStrMonthlyOuterBill = lStrMonthlyOuterBill + lStrNetAmount + lSbFooter.toString() + lStrFooter;

				Map lDetailMap = new HashMap();
				String lStrExportTo = DBConstants.DIS_ONSCREEN;
				if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
					lDetailMap.put(DBConstants.DIS_ONSCREEN, lStrMonthlyOuterBill);
				} else if ((DBConstants.DIS_TEXTFILE).equals(lStrExportTo)) {
					lDetailMap.put(DBConstants.DIS_TEXTFILE, lStrMonthlyOuterBill);
				}
				ReportExportHelper rptExpHelper = new ReportExportHelper();
				rptExpHelper.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
				resObj.setResultValue(inputMap);
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject viewMonthlyPensionInnerBill(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSdf1 = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lObjSmplDateFormat = new SimpleDateFormat("MMMM yyyy ");
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;

		List<Object[]> lLstMonthlyBillInnerDtls = null;
		List<Object[]> lLstCategoryWiseSummary = new ArrayList<Object[]>();
		String lStrTreasuryName = null;
		String lStrBillCntrlNo = null;
		String lStrSchemeCode = "";
		String lStrSchemeName = "";
		String lStrMajorHead = "";
		String lStrSubMajorHead = "";
		String lStrMinorHead = "";
		String lStrSubMinorHead = "";
		String lStrSubHead = "";
		String lStrDemandCode = "";
		String lStrCharged = "";
		String lStrPlan = "";
		String lStrPaymentMode = "";
		String lStrBankKey = null;
		String lStrBranchKey = null;
		String lStrBankName = null;
		String lStrBranchName = null;
		String lStrBankBranchKey = "";
		String lStrMonthlyInnerBill = "";
		StringBuilder lSbMonthlyInnerBill = new StringBuilder();
		String lStrFooter = "";
		StringBuilder lSbHeader = new StringBuilder();
		Map<String, String> lBankMap = new HashMap<String, String>();
		Map<String, String> lBranchMap = new HashMap<String, String>();
		List<String> lLstBankBranchKey = new ArrayList<String>();
		Map<String, List> lBlllDetailMap = new HashMap<String, List>();
		List<Object[]> lLstBillDtls = new ArrayList<Object[]>();
		Map<String, List> lMapCategoryWiseSummary = new HashMap<String, List>();
		List<Object[]> lLstCategoryWiseDtls = new ArrayList<Object[]>();
		String lineSeperator = "\r\n";
		String lStrHdgForMonthYear = "";
		Integer lIntNoOfLinesPrintedOnPage = 0;
		String lStrMonthlyBill = null;
		try {
			setSessionInfo(inputMap);
			String lStrBillNo = StringUtility.getParameter("billNo", request).trim();
			String lStrForMonthYear = StringUtility.getParameter("forMonthYear", request).trim();
			String lStrParaBankCode = StringUtility.getParameter("bankCode", request).trim();
			String lStrParaBranchCode = StringUtility.getParameter("branchCode", request).trim();
			// String lStrBillDate = StringUtility.getParameter("billDate",
			// request);

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			lLstMonthlyBillInnerDtls = lObjMonthlyPensionBillDAO.getMonthlyPensionBillInnerDtls(lStrBillNo, gStrLocCode, lStrParaBankCode, lStrParaBranchCode);

			ColumnVo[] columnCtgryWiseSumry = new ColumnVo[3];
			columnCtgryWiseSumry[0] = new ColumnVo("No Of Pensioner", 2, 10, 1, false, false, true);
			columnCtgryWiseSumry[1] = new ColumnVo("Category Code Description", 1, 35, 0, true, false, true);
			columnCtgryWiseSumry[2] = new ColumnVo("Net Amount Rs.", 2, 14, 1, false, false, true);

			if (!lLstMonthlyBillInnerDtls.isEmpty()) {
				lStrTreasuryName = lObjPhysicalCaseInwardDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocCode));
				for (Object[] cols : lLstMonthlyBillInnerDtls) {
					if (cols[1] != null) {
						lStrBillCntrlNo = cols[1].toString();
					}
					if (cols[6] != null) {
						lStrSchemeCode = cols[6].toString();
					}
					if (cols[7] != null) {
						lStrSchemeName = cols[7].toString();
					}
					if (cols[37] != null) {
						lStrMajorHead = cols[37].toString();
					}
					if (cols[38] != null) {
						lStrSubMajorHead = cols[38].toString();
					}
					if (cols[39] != null) {
						lStrMinorHead = cols[39].toString();
					}
					if (cols[40] != null) {
						lStrSubMinorHead = cols[40].toString();
					}
					if (cols[41] != null) {
						lStrSubHead = cols[41].toString();
					}
					if (cols[42] != null) {
						lStrDemandCode = cols[42].toString();
					}
					if (cols[43] != null) {
						if ("C".equals(cols[43])) {
							lStrCharged = "Charged";
						} else if ("V".equals(cols[43])) {
							lStrCharged = "Voted";
						}
					}
					if (cols[44] != null) {
						if ("P".equals(cols[44])) {
							lStrPlan = "Plan";
						} else if ("N".equals(cols[44])) {
							lStrPlan = "Non Plan";
						}
					}
					if (cols[8] != null) {
						lStrPaymentMode = cols[8].toString();
					}
					if (cols[2] != null && cols[3] != null) {
						lStrBankBranchKey = cols[2].toString() + "~" + cols[3].toString();
						lStrBankKey = cols[2].toString();
						lStrBranchKey = cols[3].toString();
					}
					lStrBankName = lBankMap.get(lStrBankKey);
					if (lStrBankName == null) {
						lBankMap.put(lStrBankKey, cols[4].toString());
					}

					lStrBranchName = lBranchMap.get(lStrBranchKey);
					if (lStrBranchName == null) {
						lBranchMap.put(lStrBranchKey, cols[5].toString());
					}
					lLstBillDtls = lBlllDetailMap.get(lStrBankBranchKey);
					if (lLstBillDtls != null) {
						lLstBillDtls.add(cols);
					} else {
						lLstBillDtls = new ArrayList();
						lLstBillDtls.add(cols);
						lBlllDetailMap.put(lStrBankBranchKey, lLstBillDtls);
					}
					if (!lLstBankBranchKey.contains(lStrBankBranchKey)) {
						lLstBankBranchKey.add(lStrBankBranchKey);
					}
					if (cols[45] != null) {
						lStrHdgForMonthYear = lObjSmplDateFormat.format(lObjSdf1.parse(cols[45].toString()));
					}
				}
				lLstMonthlyBillInnerDtls = null;
				
				lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
				lLstCategoryWiseSummary = lObjMonthlyPensionBillDAO.getBankBranchCategoryWiseSummary(lStrBillNo);
				if (!lLstCategoryWiseSummary.isEmpty()) {
					for (Object[] cols : lLstCategoryWiseSummary) {
						String lStrKey = cols[0].toString() + "~" + cols[1].toString();
						lLstCategoryWiseDtls = lMapCategoryWiseSummary.get(lStrKey);
						if (lLstCategoryWiseDtls != null) {
							lLstCategoryWiseDtls.add(cols);
						} else {
							lLstCategoryWiseDtls = new ArrayList<Object[]>();
							lLstCategoryWiseDtls.add(cols);
							lMapCategoryWiseSummary.put(lStrKey, lLstCategoryWiseDtls);
						}
					}
				}
				lLstCategoryWiseSummary = null;
				
				List lLstData = new ArrayList();
				ReportExportHelper lObjExport = new ReportExportHelper();
				ArrayList blankList = new ArrayList();
				blankList.add("");
				blankList.add("");
				blankList.add("");
				blankList.add("");
				blankList.add("");
				blankList.add("");
				blankList.add("");
				blankList.add("");
				blankList.add("");
				blankList.add("");
				blankList.add("");

				ColumnVo[] columnHeading = new ColumnVo[11];
				columnHeading[0] = new ColumnVo("Sr. No. Pay com", 1, 5, 0, false, false, true);
				columnHeading[1] = new ColumnVo("Name of Pensioner                $PPO NO      Account No", 1, 35, 0, true, false, true);
				columnHeading[2] = new ColumnVo("Category  Vol/PgNo", 2, 9, 0, false, false, true);
				columnHeading[3] = new ColumnVo("B. Pen/Inj  Pen.cut/Gal", 2, 12, 0, false, false, true);
				columnHeading[4] = new ColumnVo("Tot Alloc   Tot IR/DP", 2, 12, 0, false, false, true);
				columnHeading[5] = new ColumnVo("Relief    Peon Allw", 2, 7, 0, false, false, true);
				columnHeading[6] = new ColumnVo("Add Pen   Med Allw", 2, 7, 0, false, false, true);
				columnHeading[7] = new ColumnVo("Arrears", 2, 8, 0, false, false, true);
				columnHeading[8] = new ColumnVo("Gross", 2, 9, 0, false, false, true);
				columnHeading[9] = new ColumnVo("Recoveries", 2, 8, 0, false, false, true);
				columnHeading[10] = new ColumnVo("Net", 2, 9, 0, false, false, true);

				ColumnVo[] columnIrAllocAmt = new ColumnVo[8];
				columnIrAllocAmt[0] = new ColumnVo("IR1", 1, 10, 0, false, false, true);
				columnIrAllocAmt[1] = new ColumnVo("IR2", 1, 10, 0, true, false, true);
				columnIrAllocAmt[2] = new ColumnVo("IR3", 2, 10, 0, false, false, true);
				columnIrAllocAmt[3] = new ColumnVo("ALLOC1", 2, 10, 0, false, false, true);
				columnIrAllocAmt[4] = new ColumnVo("ALLOC2", 2, 10, 0, false, false, true);
				columnIrAllocAmt[5] = new ColumnVo("ALLOC3", 2, 10, 0, false, false, true);
				columnIrAllocAmt[6] = new ColumnVo("ALLOC4", 2, 10, 0, false, false, true);
				columnIrAllocAmt[7] = new ColumnVo("ALLOC5", 2, 10, 0, false, false, true);

				Double lDbTotalBasicPnsnAmt = 0D;
				Double lDbTotalPnsnCutAmt = 0D;
				Double lDbTotalGallantryAmt = 0D;
				Double lDbTotalAllocation1Amt = 0D;
				Double lDbTotalAllocation2Amt = 0D;
				Double lDbTotalAllocation3Amt = 0D;
				Double lDbTotalAllocation4Amt = 0D;
				Double lDbTotalAllocation5Amt = 0D;
				Double lDbTotalIR1Amt = 0D;
				Double lDbTotalIR2Amt = 0D;
				Double lDbTotalIR3Amt = 0D;
				Double lDbTotalDPAmt = 0D;
				Double lDbTotalReliefAmt = 0D;
				Double lDbTotalPeonAllwAmt = 0D;
				Double lDbTotalADPAmt = 0D;
				Double lDbTotalMedAllwAmt = 0D;
				Double lDbTotalArrearAmt = 0D;
				Double lDbTotalGrossAmt = 0D;
				Double lDbTotalRecoveryAmt = 0D;
				Double lDbTotalNetAmt = 0D;

				for (String mainKey : lLstBankBranchKey) {
					Integer lIntSrNo = 1;
					List<List<Object>> lLstOuter = new ArrayList<List<Object>>();
					List lLstBillDetails = new ArrayList();
					Double lDbTotalAllocAmt = 0D;
					Double lDbTotalIrAmt = 0D;
					Double lDbBranchTotalBasicPnsnAmt = 0D;
					Double lDbBranchTotalPnsnCutAmt = 0D;
					Double lDbBranchTotalGallantryAmt = 0D;
					Double lDbBranchTotalIrAmt = 0D;
					Double lDbBranchTotalIr1Amt = 0D;
					Double lDbBranchTotalIr2Amt = 0D;
					Double lDbBranchTotalIr3Amt = 0D;
					Double lDbBranchTotalAllocationAmt = 0D;
					Double lDbBranchTotalAlloc1Amt = 0D;
					Double lDbBranchTotalAlloc2Amt = 0D;
					Double lDbBranchTotalAlloc3Amt = 0D;
					Double lDbBranchTotalAlloc4Amt = 0D;
					Double lDbBranchTotalAlloc5Amt = 0D;
					Double lDbBranchTotalDPAmt = 0D;
					Double lDbBranchTotalReliefAmt = 0D;
					Double lDbBranchTotalPeonAllwAmt = 0D;
					Double lDbBranchTotalADPAmt = 0D;
					Double lDbBranchTotalMedAllwAmt = 0D;
					Double lDbBranchTotalArrearAmt = 0D;
					Double lDbBranchTotalGrossAmt = 0D;
					Double lDbBranchTotalRecoveryAmt = 0D;
					Double lDbBranchTotalNetAmt = 0D;

					lLstBillDetails = lBlllDetailMap.get(mainKey);
					String[] lArrBankBranch = mainKey.split("~");
					String lStrBankCode = lArrBankBranch[0];
					String lStrBranchCode = lArrBankBranch[1];

					lSbHeader = new StringBuilder();
					lSbHeader.append("MTR 38");
					lSbHeader.append("\r\n");
					lSbHeader.append("2071 PENSION FOR THE MONTH OF " + lStrHdgForMonthYear);
					lSbHeader.append("\r\n");
					lSbHeader.append("Treasury Name : " + lStrTreasuryName);
					lSbHeader.append("\r\n");
					lSbHeader.append(String.format("%-85s", "Scheme Descr. " + lStrSchemeName));
					lSbHeader.append("\r\n");
					lSbHeader.append(String.format("%40s", "Scheme Code : " + lStrSchemeCode + "    "));
					lSbHeader.append(String.format("%15s", "Demand Code : " + lStrDemandCode + " "));
					lSbHeader.append(String.format("%20s", "Major Head : " + lStrMajorHead + "  "));
					lSbHeader.append(String.format("%20s", "Sub Major Head : " + lStrSubMajorHead + "  "));
					lSbHeader.append(String.format("%20s", "Minor Head : " + lStrMinorHead + "  "));
					lSbHeader.append(String.format("%16s", "Sub Minor Head : " + lStrSubMinorHead + "  "));
					lSbHeader.append(String.format("%15s", "Sub Head : " + lStrSubHead + "  "));
					lSbHeader.append("\r\n");
					lSbHeader.append(String.format("%16s", "Charged/Voted : " + lStrCharged + "   "));
					lSbHeader.append(String.format("%16s", "Plan/Non Plan : " + lStrPlan));
					lSbHeader.append("\r\n");
					lSbHeader.append(String.format("%40s", "Bill No : " + lStrBillCntrlNo + "    "));
					lSbHeader.append(String.format("%-32s", "Payment Mode : " + lStrPaymentMode + "    "));
					lSbHeader.append(String.format("%-30s", "Date  : " + lObjSimpleDateFormat.format(gDate)));
					lSbHeader.append("\r\n");

					lSbHeader.append("Bank Name / Code : " + lBankMap.get(lStrBankCode) + " / " + lStrBankCode + "   ");
					lSbHeader.append("Branch Name / Code : " + lBranchMap.get(lStrBranchCode) + " / " + lStrBranchCode);

					List<Object[]> dataList = lBlllDetailMap.get(mainKey);
					for (Object[] cols : dataList) {
						lLstData = new ArrayList();
						lLstData.add(lIntSrNo);
						lLstData.add(cols[11]);
						lLstData.add(cols[14]);
						lLstData.add((long) (Double.parseDouble(cols[17].toString())));
						lDbTotalBasicPnsnAmt = lDbTotalBasicPnsnAmt + (Double.parseDouble(cols[17].toString()));
						lDbBranchTotalBasicPnsnAmt = lDbBranchTotalBasicPnsnAmt + (Double.parseDouble(cols[17].toString()));
						lDbTotalAllocAmt = Double.parseDouble(cols[20].toString()) + Double.parseDouble(cols[21].toString()) + Double.parseDouble(cols[22].toString())
								+ Double.parseDouble(cols[23].toString()) + Double.parseDouble(cols[24].toString());
						lLstData.add(lDbTotalAllocAmt.longValue());
						lLstData.add((long) (Double.parseDouble(cols[29].toString())));
						lDbTotalReliefAmt = lDbTotalReliefAmt + (Double.parseDouble(cols[29].toString()));
						lDbBranchTotalReliefAmt = lDbBranchTotalReliefAmt + (Double.parseDouble(cols[29].toString()));
						lLstData.add((long) (Double.parseDouble(cols[31].toString())));
						lDbTotalADPAmt = lDbTotalADPAmt + Double.parseDouble(cols[31].toString());
						lDbBranchTotalADPAmt = lDbBranchTotalADPAmt + Double.parseDouble(cols[31].toString());
						lLstData.add((long) (Double.parseDouble(cols[33].toString())));
						lDbTotalArrearAmt = lDbTotalArrearAmt + Double.parseDouble(cols[33].toString());
						lDbBranchTotalArrearAmt = lDbBranchTotalArrearAmt + Double.parseDouble(cols[33].toString());
						lLstData.add((long) (Double.parseDouble(cols[34].toString())));
						lDbTotalGrossAmt = lDbTotalGrossAmt + Double.parseDouble(cols[34].toString());
						lDbBranchTotalGrossAmt = lDbBranchTotalGrossAmt + Double.parseDouble(cols[34].toString());
						lLstData.add((long) (Double.parseDouble(cols[35].toString())));
						lDbTotalRecoveryAmt = lDbTotalRecoveryAmt + Double.parseDouble(cols[35].toString());
						lDbBranchTotalRecoveryAmt = lDbBranchTotalRecoveryAmt + Double.parseDouble(cols[35].toString());
						lLstData.add((long) (Double.parseDouble(cols[36].toString())));
						lDbTotalNetAmt = lDbTotalNetAmt + Double.parseDouble(cols[36].toString());
						lDbBranchTotalNetAmt = lDbBranchTotalNetAmt + Double.parseDouble(cols[36].toString());
						lLstOuter.add(lLstData);

						lLstData = new ArrayList();
						lLstData.add(cols[13]);
						lLstData.add(String.format("%14s", cols[10]) + " " + String.format("%20s", cols[12]));
						lLstData.add(cols[15] + "/" + cols[16]);
						lLstData.add((long) (Double.parseDouble(cols[18].toString())) + "/" + (long) (Double.parseDouble(cols[19].toString())));
						lDbTotalPnsnCutAmt = lDbTotalPnsnCutAmt + Double.parseDouble(cols[18].toString());
						lDbBranchTotalPnsnCutAmt = lDbBranchTotalPnsnCutAmt + Double.parseDouble(cols[18].toString());
						lDbTotalGallantryAmt = lDbTotalGallantryAmt + Double.parseDouble(cols[19].toString());
						lDbBranchTotalGallantryAmt = lDbBranchTotalGallantryAmt + Double.parseDouble(cols[19].toString());
						lDbTotalIrAmt = Double.parseDouble(cols[25].toString()) + Double.parseDouble(cols[26].toString()) + Double.parseDouble(cols[27].toString());
						lLstData.add(lDbTotalIrAmt.longValue() + "/" + (long) (Double.parseDouble(cols[28].toString())));
						lDbTotalDPAmt = lDbTotalDPAmt + Double.parseDouble(cols[28].toString());
						lDbBranchTotalDPAmt = lDbBranchTotalDPAmt + Double.parseDouble(cols[28].toString());
						lLstData.add((long) (Double.parseDouble(cols[30].toString())));
						lDbTotalPeonAllwAmt = lDbTotalPeonAllwAmt + Double.parseDouble(cols[30].toString());
						lDbBranchTotalPeonAllwAmt = lDbBranchTotalPeonAllwAmt + Double.parseDouble(cols[30].toString());
						lLstData.add((long) (Double.parseDouble(cols[32].toString())));
						lDbTotalMedAllwAmt = lDbTotalMedAllwAmt + Double.parseDouble(cols[32].toString());
						lDbBranchTotalMedAllwAmt = lDbBranchTotalMedAllwAmt + Double.parseDouble(cols[32].toString());
						lLstData.add("");
						lLstData.add("");
						lLstData.add("");
						lLstData.add("");
						lLstOuter.add(lLstData);
						lLstData = null;
						// lLstOuter.add(blankList);

						lDbBranchTotalIr1Amt = lDbBranchTotalIr1Amt + Double.parseDouble(cols[25].toString());
						lDbBranchTotalIr2Amt = lDbBranchTotalIr2Amt + Double.parseDouble(cols[26].toString());
						lDbBranchTotalIr3Amt = lDbBranchTotalIr3Amt + Double.parseDouble(cols[27].toString());
						lDbBranchTotalIrAmt = lDbBranchTotalIrAmt + Double.parseDouble(cols[25].toString()) + Double.parseDouble(cols[26].toString()) + Double.parseDouble(cols[27].toString());

						lDbBranchTotalAlloc1Amt = lDbBranchTotalAlloc1Amt + Double.parseDouble(cols[20].toString());
						lDbBranchTotalAlloc2Amt = lDbBranchTotalAlloc2Amt + Double.parseDouble(cols[21].toString());
						lDbBranchTotalAlloc3Amt = lDbBranchTotalAlloc3Amt + Double.parseDouble(cols[22].toString());
						lDbBranchTotalAlloc4Amt = lDbBranchTotalAlloc4Amt + Double.parseDouble(cols[23].toString());
						lDbBranchTotalAlloc5Amt = lDbBranchTotalAlloc5Amt + Double.parseDouble(cols[24].toString());
						lDbBranchTotalAllocationAmt = lDbBranchTotalAllocationAmt + Double.parseDouble(cols[20].toString()) + Double.parseDouble(cols[21].toString())
								+ Double.parseDouble(cols[22].toString()) + Double.parseDouble(cols[23].toString()) + Double.parseDouble(cols[24].toString());
						lIntSrNo++;

					}
					//lBlllDetailMap = null;
					
					lLstData = new ArrayList();
					lLstData.add(getChar(5, "-"));
					lLstData.add(getChar(35, "-"));
					lLstData.add(getChar(9, "-"));
					lLstData.add(getChar(12, "-"));
					lLstData.add(getChar(12, "-"));
					lLstData.add(getChar(7, "-"));
					lLstData.add(getChar(7, "-"));
					lLstData.add(getChar(8, "-"));
					lLstData.add(getChar(9, "-"));
					lLstData.add(getChar(8, "-"));
					lLstData.add(getChar(9, "-"));
					lLstOuter.add(lLstData);
					lLstData = null;

					lLstData = new ArrayList();
					lLstData.add("Total");
					lLstData.add("");
					lLstData.add("");
					lLstData.add(lDbBranchTotalBasicPnsnAmt.longValue());
					lLstData.add(lDbBranchTotalAllocationAmt.longValue());
					lLstData.add(lDbBranchTotalReliefAmt.longValue());
					lLstData.add(lDbBranchTotalADPAmt.longValue());
					lLstData.add(lDbBranchTotalArrearAmt.longValue());
					lLstData.add(lDbBranchTotalGrossAmt.longValue());
					lLstData.add(lDbBranchTotalRecoveryAmt.longValue());
					lLstData.add(lDbBranchTotalNetAmt.longValue());
					lLstOuter.add(lLstData);
					lLstData = null;

					lLstData = new ArrayList();
					lLstData.add("");
					lLstData.add("");
					lLstData.add("");
					lLstData.add(lDbBranchTotalPnsnCutAmt.longValue() + "/" + lDbBranchTotalGallantryAmt.longValue());
					lLstData.add(lDbBranchTotalIrAmt.longValue() + "/" + lDbBranchTotalDPAmt.longValue());
					lLstData.add(lDbBranchTotalPeonAllwAmt.longValue());
					lLstData.add(lDbBranchTotalMedAllwAmt.longValue());
					lLstData.add("");
					lLstData.add("");
					lLstData.add("");
					lLstData.add("");
					lLstOuter.add(lLstData);
					lLstData = null;

					lSbMonthlyInnerBill.append(lObjExport.getReportFileForCvpBill(lLstOuter, columnHeading, lSbHeader.toString(), lStrFooter, null, 132, true, 1));
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + lObjExport.getReportFileForCvpBill(lLstOuter, columnHeading, lSbHeader.toString(), lStrFooter, null, 132, true, 1);

					lStrMonthlyBill = lSbMonthlyInnerBill.toString();
					if (lStrMonthlyBill != null) {
						lIntNoOfLinesPrintedOnPage = (lStrMonthlyBill.split(lineSeperator)).length;
					}
					lStrMonthlyBill = null;
					
					lSbHeader = new StringBuilder();
					lLstOuter = new ArrayList<List<Object>>();
					lLstData = new ArrayList();
					lLstData.add(lDbBranchTotalIr1Amt.longValue());
					lLstData.add(lDbBranchTotalIr2Amt.longValue());
					lLstData.add(lDbBranchTotalIr3Amt.longValue());
					lLstData.add(lDbBranchTotalAlloc1Amt.longValue());
					lLstData.add(lDbBranchTotalAlloc2Amt.longValue());
					lLstData.add(lDbBranchTotalAlloc3Amt.longValue());
					lLstData.add(lDbBranchTotalAlloc4Amt.longValue());
					lLstData.add(lDbBranchTotalAlloc5Amt.longValue());

					lDbTotalIR1Amt = lDbTotalIR1Amt + lDbBranchTotalIr1Amt;
					lDbTotalIR2Amt = lDbTotalIR2Amt + lDbBranchTotalIr2Amt;
					lDbTotalIR3Amt = lDbTotalIR3Amt + lDbBranchTotalIr3Amt;
					lDbTotalAllocation1Amt = lDbTotalAllocation1Amt + lDbBranchTotalAlloc1Amt;
					lDbTotalAllocation2Amt = lDbTotalAllocation2Amt + lDbBranchTotalAlloc2Amt;
					lDbTotalAllocation3Amt = lDbTotalAllocation3Amt + lDbBranchTotalAlloc3Amt;
					lDbTotalAllocation4Amt = lDbTotalAllocation4Amt + lDbBranchTotalAlloc4Amt;
					lDbTotalAllocation5Amt = lDbTotalAllocation5Amt + lDbBranchTotalAlloc5Amt;

					lLstOuter.add(lLstData);
					lLstData = null;

					lSbMonthlyInnerBill.append(lObjExport.getReportFile(lLstOuter, columnIrAllocAmt, lSbHeader.toString(), lStrFooter, null, 88, true, lIntNoOfLinesPrintedOnPage, true, "|"));
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill
					//		+ lObjExport.getReportFile(lLstOuter, columnIrAllocAmt, lSbHeader.toString(), lStrFooter, null, 88, true, lIntNoOfLinesPrintedOnPage, true, "|");
					lStrMonthlyBill = lSbMonthlyInnerBill.toString();
					if (lStrMonthlyBill != null) {
						lIntNoOfLinesPrintedOnPage = (lStrMonthlyBill.split(lineSeperator)).length;
					}
					lStrMonthlyBill = null;
					
					lSbMonthlyInnerBill.append("Category Wise Summary");
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + "Category Wise Summary";
					
					lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
					if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
						lSbMonthlyInnerBill.append((char) 12);
						//lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;
					}
					lSbMonthlyInnerBill.append("\r\n");
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + "\r\n";
					lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
					if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
						lSbMonthlyInnerBill.append((char) 12);
						//lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;
					}
					lSbHeader = new StringBuilder();
					Double lDbNetAmount = 0D;
					lLstOuter = new ArrayList<List<Object>>();
					lLstData = new ArrayList();
					List<Object[]> lLstCtgryWise = new ArrayList<Object[]>();
					lLstCtgryWise = lMapCategoryWiseSummary.get(mainKey);
					if (lLstCtgryWise != null) {
						for (Object[] cols : lLstCtgryWise) {
							lLstData = new ArrayList();
							lLstData.add(cols[2]);
							lLstData.add(cols[4]);
							lLstData.add((long) (Double.parseDouble(cols[5].toString())));
							lDbNetAmount = lDbNetAmount + Double.parseDouble(cols[5].toString());
							lLstOuter.add(lLstData);
						}
						lSbMonthlyInnerBill.append(lObjExport.getReportFile(lLstOuter, columnCtgryWiseSumry, lSbHeader.toString(), lStrFooter, null, 62, true, lIntNoOfLinesPrintedOnPage, true, "|"));
						//lStrMonthlyInnerBill = lStrMonthlyInnerBill
						//		+ lObjExport.getReportFile(lLstOuter, columnCtgryWiseSumry, lSbHeader.toString(), lStrFooter, null, 62, true, lIntNoOfLinesPrintedOnPage, true, "|");
					}
					lStrMonthlyBill = lSbMonthlyInnerBill.toString();
					if (lStrMonthlyBill != null) {
						lIntNoOfLinesPrintedOnPage = (lStrMonthlyBill.split(lineSeperator)).length;
					}
					if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
						lSbMonthlyInnerBill.append((char) 12);
						//lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;
					}
					lSbMonthlyInnerBill.append("\r\n");
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + "\r\n";
					lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
					if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
						lSbMonthlyInnerBill.append((char) 12);
						//lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;
					}
					lSbMonthlyInnerBill.append("In Word Rupees  " + EnglishDecimalFormat.convert1WithSpace(lDbNetAmount.longValue()) + " Only");
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + "In Word Rupees  " + EnglishDecimalFormat.convert1WithSpace(lDbNetAmount.longValue()) + " Only";
					lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
					if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
						lSbMonthlyInnerBill.append((char) 12);
						//lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;
					}
					lSbMonthlyInnerBill.append("\r\n");
					lSbMonthlyInnerBill.append("\r\n");
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + "\r\n\r\n";
					lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 2;
					if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
						lSbMonthlyInnerBill.append((char) 12);
						//lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;
					}
					lSbMonthlyInnerBill.append(String.format("%132s", "ASST. PAY AND ACCOUNT OFFICER, " + lStrTreasuryName));
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + String.format("%132s", "ASST. PAY AND ACCOUNT OFFICER, " + lStrTreasuryName);
					lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
					if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
						lSbMonthlyInnerBill.append((char) 12);
						//lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;
					}
					lSbMonthlyInnerBill.append("\r\n");
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + "\r\n";
					lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
					if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
						lSbMonthlyInnerBill.append((char) 12);
						//lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;
					}
					lSbMonthlyInnerBill.append(String.format("%113s", "ADDL./TREASURY OFFICER"));
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + String.format("%113s", "ADDL./TREASURY OFFICER");
					lSbMonthlyInnerBill.append((char) 12);
					//lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;

				}
				// lStrMonthlyInnerBill = lStrMonthlyInnerBill + (char) 12;
				StringBuilder lSbFooter = new StringBuilder();
				lSbFooter.append("Total Basic Pension Amount      : " + String.format("%15s", lDbTotalBasicPnsnAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Pension Cut Amount        : " + String.format("%15s", lDbTotalPnsnCutAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Gallentry Amount          : " + String.format("%15s", lDbTotalGallantryAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Allocation1 Amount        : " + String.format("%15s", lDbTotalAllocation1Amt.longValue()) + "\r\n");
				lSbFooter.append("Total Allocation2 Amount        : " + String.format("%15s", lDbTotalAllocation2Amt.longValue()) + "\r\n");
				lSbFooter.append("Total Allocation3 Amount        : " + String.format("%15s", lDbTotalAllocation3Amt.longValue()) + "\r\n");
				lSbFooter.append("Total Allocation4 Amount        : " + String.format("%15s", lDbTotalAllocation4Amt.longValue()) + "\r\n");
				lSbFooter.append("Total Allocation5 Amount        : " + String.format("%15s", lDbTotalAllocation5Amt.longValue()) + "\r\n");
				lSbFooter.append("Total IR1 Amount                : " + String.format("%15s", lDbTotalIR1Amt.longValue()) + "\r\n");
				lSbFooter.append("Total IR2 Amount                : " + String.format("%15s", lDbTotalIR2Amt.longValue()) + "\r\n");
				lSbFooter.append("Total IR3 Amount                : " + String.format("%15s", lDbTotalIR3Amt.longValue()) + "\r\n");
				lSbFooter.append("Total DP Amount                 : " + String.format("%15s", lDbTotalDPAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Relief Amount             : " + String.format("%15s", lDbTotalReliefAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Peon Allowance Amount     : " + String.format("%15s", lDbTotalPeonAllwAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Additional Pension Amount : " + String.format("%15s", lDbTotalADPAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Medical Allowance Amount  : " + String.format("%15s", lDbTotalMedAllwAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Arrear Amount             : " + String.format("%15s", lDbTotalArrearAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Gross Amount              : " + String.format("%15s", lDbTotalGrossAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Recovery Amount           : " + String.format("%15s", lDbTotalRecoveryAmt.longValue()) + "\r\n");
				lSbFooter.append("Total Net Amount                : " + String.format("%15s", lDbTotalNetAmt.longValue()) + "\r\n");
				lSbFooter.append("\r\n\r\n");
				lSbFooter.append("Category Wise Summary");
				lSbFooter.append("\r\n");
				lSbMonthlyInnerBill.append(lSbFooter.toString());
				//lStrMonthlyInnerBill = lStrMonthlyInnerBill + lSbFooter.toString();

				List<List<Object>> lLstOuter = new ArrayList<List<Object>>();
				lLstData = new ArrayList();
				lLstCategoryWiseSummary = lObjMonthlyPensionBillDAO.getCategoryWiseSummary(lStrBillNo);
				if (!lLstCategoryWiseSummary.isEmpty()) {
					for (Object[] cols : lLstCategoryWiseSummary) {
						lLstData = new ArrayList();
						lLstData.add(cols[0]);
						lLstData.add(cols[2]);
						lLstData.add((long) (Double.parseDouble(cols[3].toString())));
						lLstOuter.add(lLstData);
					}
				}
				lSbMonthlyInnerBill.append(lObjExport.getReportFile(lLstOuter, columnCtgryWiseSumry, lSbHeader.toString(), lStrFooter, null, 62, true, 2, true, "|"));
				//lStrMonthlyInnerBill = lStrMonthlyInnerBill + lObjExport.getReportFile(lLstOuter, columnCtgryWiseSumry, lSbHeader.toString(), lStrFooter, null, 62, true, 2, true, "|");
			}

			Map lDetailMap = new HashMap();
			String lStrExportTo = DBConstants.DIS_ONSCREEN;
			if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_ONSCREEN, lSbMonthlyInnerBill.toString());
			} else if ((DBConstants.DIS_TEXTFILE).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_TEXTFILE, lSbMonthlyInnerBill.toString());
			}
			ReportExportHelper rptExpHelper = new ReportExportHelper();
			rptExpHelper.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	public ResultObject approveMonthlyPensionBill(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		String[] lArrStrBillNo = null;
		String lParaStrBillNo = null;
		StringBuilder lSBStatus = new StringBuilder();
		BptmCommonServicesDAOImpl lObjBptmCommonServicesDAO = null;
		TrnBillRegister lObjTrnBillRegister = null;
		PensionBillDAO lObjPensionBillDAO = null;
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		List<Long> lLstBillNo = null;
		List<Object[]> lLstPnsrDtls = null;
		List<String> lLstPnsrCode = new ArrayList<String>();
		List<String> lLstPPONo = new ArrayList<String>();
		Long lLngBillNo = null;
		Map<Long, List<Object[]>> lMapBillNoPnsrDtls = new HashMap<Long, List<Object[]>>();
		List<Object[]> lLstPnsrCodePPONo = null;
		Object[] lArrPnsrCodePPONo = null;
		Integer lIntForMonth = null;
		try {
			setSessionInfo(inputMap);
			lObjBptmCommonServicesDAO = new BptmCommonServicesDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
			lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lParaStrBillNo = StringUtility.getParameter("billNo", request).trim();
			lArrStrBillNo = lParaStrBillNo.split("~");
			if (lArrStrBillNo.length > 0) {
				lLstBillNo = new ArrayList<Long>();
				for (String lStrBillNo : lArrStrBillNo) {
					if (lStrBillNo != null && lStrBillNo.length() > 0) {
						lObjTrnBillRegister = (TrnBillRegister) lObjBptmCommonServicesDAO.read(Long.parseLong(lStrBillNo));
						lObjTrnBillRegister.setCurrBillStatus(DBConstants.ST_BILL_APPROVED);
						lObjTrnBillRegister.setUpdatedDate(gDate);
						lObjTrnBillRegister.setUpdatedPostId(gLngPostId);
						lObjTrnBillRegister.setUpdatedUserId(gLngUserId);
						lObjBptmCommonServicesDAO.update(lObjTrnBillRegister);
						lLstBillNo.add(Long.valueOf(lStrBillNo));
					}
				}
				// --Setting arrear paid flag to Y on approval of bill.
				lObjPensionBillDAO.updatePaidFlagInArrearDtlsOnApproveReject(lLstBillNo, "Y", gLngUserId, gLngPostId, gDate);
				// lLstPnsrDtls =
				// lObjMonthlyPensionBillDAO.getAllPensionerCodeOfBill(lLstBillNo);
				//
				// // ---Preparing map of billwise pensionercode,ppono details.
				// for (Object[] lArrObj : lLstPnsrDtls) {
				// lLngBillNo = (Long) lArrObj[2];
				// lArrPnsrCodePPONo = new Object[3];
				// lArrPnsrCodePPONo[0] = lArrObj[0];// pensionercode
				// lArrPnsrCodePPONo[1] = lArrObj[1];// ppono
				// lArrPnsrCodePPONo[2] = lArrObj[3];// formonth
				// if (lLngBillNo != null) {
				// lLstPnsrCodePPONo = lMapBillNoPnsrDtls.get(lLngBillNo);
				// if (lLstPnsrCodePPONo != null) {
				// lLstPnsrCodePPONo.add(lArrPnsrCodePPONo);
				// } else {
				// lLstPnsrCodePPONo = new ArrayList<Object[]>();
				// lLstPnsrCodePPONo.add(lArrPnsrCodePPONo);
				// }
				// lMapBillNoPnsrDtls.put(lLngBillNo, lLstPnsrCodePPONo);
				// }
				// }

				for (Long lBillNo : lLstBillNo) {
					// lLstPnsrCodePPONo = lMapBillNoPnsrDtls.get(lBillNo);
					// for (Object[] lArrObj : lLstPnsrCodePPONo) {
					// if (lArrObj[0] != null && lArrObj[0].toString().length()
					// > 0) {
					// lLstPnsrCode.add(lArrObj[0].toString());
					// }
					// if (lArrObj[1] != null && lArrObj[1].toString().length()
					// > 0) {
					// lLstPPONo.add(lArrObj[1].toString());
					// }
					// if (lArrObj[2] != null) {
					// lIntForMonth = (Integer) lArrObj[2];
					// }
					// }
					// if (lIntForMonth != null) {
					if (lBillNo != null) {
						/*
						 * -- Updating bill numbers in recovery records.
						 */
						// lObjPensionBillDAO.updateBillNoForRecovery(lLstPnsrCode,
						// lIntForMonth, gStrLocCode, lBillNo, gLngUserId,
						// gLngPostId, gDate);

						/*
						 * --Updating six pay arrear paid flag to "Y"
						 */
						lObjPensionBillDAO.updateSixpayArrearPaidFlag(lLstPnsrCode, gLngUserId, gLngUserId, gDate, 'Y', lIntForMonth, lBillNo);
					}
					// }
				}
			}
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<STATUS>");
			lSBStatus.append("APPROVED");
			lSBStatus.append("</STATUS>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject loadGenerateECSCPBKFile(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List<Long> lLstTreasuryId = new ArrayList<Long>();
		List<ComboValuesVO> lLstAllTreasury = new ArrayList<ComboValuesVO>();
		List<SgvcFinYearMst> lObjSgvcFinYearMst = null;
		List<SgvaMonthMst> lObjSgvaMonthMst = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = null;
		try {
			setSessionInfo(inputMap);
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(null, serv.getSessionFactory());
			lObjSgvaMonthMst = new ArrayList<SgvaMonthMst>();
			lObjSgvaMonthMst = lObjCommonPensionDAO.getSgvaMonthMstVO(gLngLangId.toString());
			if (lObjSgvaMonthMst != null) {
				inputMap.put("SgvaMonthMstVOArray", lObjSgvaMonthMst);
			}

			lObjSgvcFinYearMst = new ArrayList<SgvcFinYearMst>();
			lObjSgvcFinYearMst = lObjCommonPensionDAO.getSgvcFinYearMstVO(gLngLangId.toString());
			if (lObjSgvcFinYearMst != null) {
				inputMap.put("SgvcFinYearMstVOArray", lObjSgvcFinYearMst);
			}
			lLstTreasuryId.add(Long.parseLong(bundleCaseConst.getString("PPMT.TREASURYID1")));
			lLstAllTreasury = lObjPhysicalCaseInwardDAO.getAllTreasury(lLstTreasuryId, gLngLangId);
			String currentMonth = new SimpleDateFormat("MM").format(gDate);
			String currentYear = new SimpleDateFormat("yyyy").format(gDate);
			inputMap.put("CurrentMonth", Integer.parseInt(currentMonth));
			inputMap.put("CurrentYear", currentYear);
			inputMap.put("lLstTreasury", lLstAllTreasury);
			resObj.setResultValue(inputMap);
			resObj.setViewName("GenerateECSCPBKFile");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject generateECSCPBKFile(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrForMonth = null;
		String lStrLocCode = null;
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		List<Object[]> lLstECSDtls = null;
		StringBuilder lSBStatus = new StringBuilder();
		StringBuilder lSBECSDtls = new StringBuilder();
		String lStrFileName = null;
		try {
			setSessionInfo(inputMap);
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lStrForMonth = StringUtility.getParameter("forMonth", request).trim();
			lStrLocCode = StringUtility.getParameter("locCode", request).trim();
			if (lStrForMonth.length() > 0 && lStrLocCode.length() > 0) {
				lLstECSDtls = lObjMonthlyPensionBillDAO.getECSCPBKFileDetails(lStrLocCode, lStrForMonth);
				lStrFileName = "cpbk" + lStrForMonth.substring(4, 6) + lStrForMonth.substring(2, 4);
			}
			if (lLstECSDtls != null && lLstECSDtls.size() > 0) {
				String lStrBankCode = null;
				String lStrBranchCode = null;
				String lStrAcNo = null;
				String lStrSex = null;
				String lStrPnsrName = null;
				String lStrPPONo = null;
				String lStrTotalAllocAmt = null;
				String lStrDAAmt = null;
				String lStrArrearAmt = null;
				String lStrRecoveryAmt = null;
				String lStrNetAmt = null;
				for (Object[] lArrObj : lLstECSDtls) {
					lStrBankCode = (lArrObj[0] != null) ? lArrObj[0].toString() : getZeroString(3);
					lStrBranchCode = (lArrObj[1] != null) ? lArrObj[1].toString() : getZeroString(3);
					lStrAcNo = (lArrObj[2] != null) ? lArrObj[2].toString() : getZeroString(15);
					lStrSex = (lArrObj[3] != null) ? lArrObj[3].toString() : getSpace(1);
					lStrPnsrName = (lArrObj[4] != null) ? lArrObj[4].toString() : getSpace(50);
					lStrPPONo = (lArrObj[5] != null) ? lArrObj[5].toString() : getSpace(20);
					lStrTotalAllocAmt = (lArrObj[6] != null) ? lArrObj[6].toString() : getZeroString(5);
					lStrDAAmt = (lArrObj[7] != null) ? lArrObj[7].toString() : getZeroString(5);
					lStrArrearAmt = (lArrObj[8] != null) ? lArrObj[8].toString() : getZeroString(7);
					lStrRecoveryAmt = (lArrObj[9] != null) ? lArrObj[9].toString() : getZeroString(7);
					lStrNetAmt = (lArrObj[10] != null) ? lArrObj[10].toString() : getZeroString(7);
					lSBECSDtls.append(lStrBankCode);
					lSBECSDtls.append(lStrBranchCode);
					lSBECSDtls.append(lStrAcNo);
					lSBECSDtls.append(lStrSex);
					lSBECSDtls.append(lStrPnsrName);
					lSBECSDtls.append(lStrPPONo);
					lSBECSDtls.append(lStrTotalAllocAmt);
					lSBECSDtls.append(lStrDAAmt);
					lSBECSDtls.append(lStrArrearAmt);
					lSBECSDtls.append(lStrRecoveryAmt);
					lSBECSDtls.append(lStrNetAmt);
					lSBECSDtls.append("\r\n");
				}
			}
			Map lDetailMap = new HashMap();
			inputMap.put("FileName", lStrFileName);
			lDetailMap.put(DBConstants.DIS_TEXTFILE, lSBECSDtls.toString());
			ReportExportHelper rptExpHelper = new ReportExportHelper();
			rptExpHelper.getExportData(resObj, DBConstants.DIS_TEXTFILE, inputMap, lDetailMap, false);
			// rptExpHelper.exportToTextFile(request,resObj, lDetailMap, false);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private String getSpace(int noOfSpaces) {

		StringBuilder lSBSpace = new StringBuilder();
		for (int i = 0; i < noOfSpaces; i++) {
			lSBSpace.append(" ");
		}
		return lSBSpace.toString();
	}

	private String getZeroString(int size) {

		StringBuilder lSBZero = new StringBuilder();
		for (int i = 0; i < size; i++) {
			lSBZero.append("0");
		}
		return lSBZero.toString();
	}

	public ResultObject getSchemewiseBankBranchReport(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		List<Object[]> lArrOuterDtls = null;
		String lStrBillNo = null;
		Integer lTotalRecords = 0;
		String lStrSchemeCode = null;
		String lStrSchemeName = null;
		String lStrMjrHead = null;
		String lStrSubMjrHead = null;
		String lStrMinorHead = null;
		String lStrSubMinorHead = null;
		String lStrSubHead = null;
		String lStrDemandCode = null;
		String lStrChargedVoted = null;
		String lStrPlanNonPlan = null;
		String lStrBillCntrlNo = null;
		String lStrForMonthYear = null;
		Object[] lArrObjDtls = null;
		SimpleDateFormat lObjSdf1 = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lObjSdf2 = new SimpleDateFormat("MMMM yyyy ");
		try {
			setSessionInfo(inputMap);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lStrBillNo = StringUtility.getParameter("billNo", request).trim();
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lTotalRecords = lObjMonthlyPensionBillDAO.getMonthlyPensionBillOuterDtlsCount(lStrBillNo, gStrLocCode);
			if (lTotalRecords > 0) {
				lArrOuterDtls = lObjMonthlyPensionBillDAO.getMonthlyPensionBillOuterDtls(lStrBillNo, gStrLocCode, displayTag);
			}
			if (lArrOuterDtls != null && lArrOuterDtls.size() > 0) {
				lArrObjDtls = lArrOuterDtls.get(0);
				if (lArrObjDtls != null && lArrObjDtls.length > 0) {
					lStrSchemeCode = (lArrObjDtls[2] != null) ? lArrObjDtls[2].toString() : "";
					lStrSchemeName = (lArrObjDtls[3] != null) ? lArrObjDtls[3].toString() : "";
					lStrMjrHead = (lArrObjDtls[10] != null) ? lArrObjDtls[10].toString() : "";
					lStrSubMjrHead = (lArrObjDtls[11] != null) ? lArrObjDtls[11].toString() : "";
					lStrMinorHead = (lArrObjDtls[12] != null) ? lArrObjDtls[12].toString() : "";
					lStrSubMinorHead = (lArrObjDtls[13] != null) ? lArrObjDtls[13].toString() : "";
					lStrSubHead = (lArrObjDtls[14] != null) ? lArrObjDtls[14].toString() : "";
					lStrDemandCode = (lArrObjDtls[15] != null) ? lArrObjDtls[15].toString() : "";
					lStrChargedVoted = (lArrObjDtls[16] != null) ? lArrObjDtls[16].toString() : "";
					if ("C".equals(lStrChargedVoted)) {
						lStrChargedVoted = "Charged";
					} else if ("V".equals(lStrChargedVoted)) {
						lStrChargedVoted = "Voted";
					}
					lStrPlanNonPlan = (lArrObjDtls[17] != null) ? lArrObjDtls[17].toString() : "";
					if ("P".equals(lStrPlanNonPlan)) {
						lStrPlanNonPlan = "Plan";
					} else if ("N".equals(lStrPlanNonPlan)) {
						lStrPlanNonPlan = "Non Plan";
					}
					lStrBillCntrlNo = (lArrObjDtls[1] != null) ? lArrObjDtls[1].toString() : "";
					lStrForMonthYear = (lArrObjDtls[18] != null) ? lArrObjDtls[18].toString() : "";
					if (lStrForMonthYear.length() > 0) {
						lStrForMonthYear = lObjSdf2.format(lObjSdf1.parse(lStrForMonthYear));
					}
				}
			}
			inputMap.put("lStrSchemeCode", lStrSchemeCode);
			inputMap.put("lStrSchemeName", lStrSchemeName);
			inputMap.put("lStrMjrHead", lStrMjrHead);
			inputMap.put("lStrSubMjrHead", lStrSubMjrHead);
			inputMap.put("lStrMinorHead", lStrMinorHead);
			inputMap.put("lStrSubMinorHead", lStrSubMinorHead);
			inputMap.put("lStrSubHead", lStrSubHead);
			inputMap.put("lStrDemandCode", lStrDemandCode);
			inputMap.put("lStrChargedVoted", lStrChargedVoted);
			inputMap.put("lStrPlanNonPlan", lStrPlanNonPlan);
			inputMap.put("lStrBillCntrlNo", lStrBillCntrlNo);
			inputMap.put("lStrForMonthYear", lStrForMonthYear);
			inputMap.put("lArrOuterDtls", lArrOuterDtls);
			inputMap.put("totalRecords", lTotalRecords);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ViewBankBrachReport");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject checkIsBillGeneratedForChangeStmnt(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		String lStrParaMonthYear = null;
		String lStrParaAllChangeRqstId = null;
		List<Object[]> lArrObjResult = null;
		String lStrChangeRqstIds = "";
		String[] lArrChangeRqstIds = null;
		Long lLngBillNo = null;
		String lStrSchemeCode = null;
		Boolean isBillGenerated = false;
		StringBuilder lSBStatus = new StringBuilder();
		SimpleDateFormat lSdf1 = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lSdf2 = new SimpleDateFormat("MMM-yyyy");
		String lStrMonthYearDesc = "";
		String[] lStrArrParaChngRqstIds = null;
		try {
			setSessionInfo(inputMap);
			lStrParaMonthYear = StringUtility.getParameter("forMonth", request).trim();
			lStrParaAllChangeRqstId = StringUtility.getParameter("changeRqstId", request).trim();
			lStrArrParaChngRqstIds = lStrParaAllChangeRqstId.split("~");
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			if (lStrParaMonthYear.length() > 0) {
				lArrObjResult = lObjMonthlyPensionBillDAO.getChangeStmntIdsBYMonthYear(Integer.parseInt(lStrParaMonthYear), gStrLocCode);
				if (lArrObjResult != null) {
					for (Object[] lArrObj : lArrObjResult) {
						lStrChangeRqstIds = (String) lArrObj[2];
						if (lStrChangeRqstIds != null && lStrChangeRqstIds.length() > 0 && !isBillGenerated) {
							lArrChangeRqstIds = lStrChangeRqstIds.split(",");
							for (String lStrParaChngRqstId : lStrArrParaChngRqstIds) {
								for (String lStrChangeRqstId : lArrChangeRqstIds) {
									if (lStrParaChngRqstId.equals(lStrChangeRqstId)) {
										isBillGenerated = true;
									}
								}
							}
						}
					}
				}
			}
			if (lStrParaMonthYear.length() > 0) {
				lStrMonthYearDesc = lSdf2.format(lSdf1.parse(lStrParaMonthYear));
			}
			lSBStatus.append("<XMLDOC>");
			lSBStatus.append("<MONTHYEAR>");
			lSBStatus.append(lStrMonthYearDesc);
			lSBStatus.append("</MONTHYEAR>");
			lSBStatus.append("<ISBILLGENERATED>");
			if (isBillGenerated) {
				lSBStatus.append("Y");
			} else {
				lSBStatus.append("N");
			}
			lSBStatus.append("</ISBILLGENERATED>");
			lSBStatus.append("</XMLDOC>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject viewMonthlyPensionBankwiseOuterBill(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		MonthlyPensionBillDAO lObjPensionBillDAO = null;
		List<String> lLstSchemeCodes = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat lObjSmplDateFormat = new SimpleDateFormat("MMMM yyyy ");

		String lStrTreasuryName = null;
		List<Object[]> lLstMonthlyBillOuterDtls = null;
		List<Object[]> lLstRecoveryDtls = null;
		StringBuilder lSbHeader = new StringBuilder();
		StringBuilder lSbFooter = new StringBuilder();
		String lStrMonthlyOuterBill = "";
		String lStrRecoveryDtls = "";
		String lStrMajorHead = "";
		String lStrSubMajorHead = "";
		String lStrMinorHead = "";
		String lStrSubMinorHead = "";
		String lStrSubHead = "";
		String lStrDemandCode = "";
		String lStrCharged = "";
		String lStrPlan = "";
		String lStrFooter = "";
		String lStrNetAmount = "";
		Double lDbNetBillAmt = 0D;
		Double lDbDeductionAmt = 0D;
		String lineSeperator = "\r\n";
		Integer lIntNoOfLinesPrintedOnPage = 0;
		Date lDtBillDate = null;

		Double lDbTotalIr1Amt = 0D;
		Double lDbTotalIr2Amt = 0D;
		Double lDbTotalIr3Amt = 0D;
		Double lDbTotalAlloc1Amt = 0D;
		Double lDbTotalAlloc2Amt = 0D;
		Double lDbTotalAlloc3Amt = 0D;
		Double lDbTotalAlloc4Amt = 0D;
		Double lDbTotalAlloc5Amt = 0D;
		List<Object[]> lLstCategoryWiseSummary = new ArrayList<Object[]>();
		try {
			setSessionInfo(inputMap);
			String lStrBillNo = StringUtility.getParameter("billNo", request);
			String lStrForMonthYear = StringUtility.getParameter("forMonthYear", request);
			String lStrBillDate = StringUtility.getParameter("billDate", request);

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			lLstMonthlyBillOuterDtls = lObjPensionBillDAO.getMonthlyPensionBillBankwiseOuterDtls(lStrBillNo, gStrLocCode);
			ArrayList inList = new ArrayList();

			if (!lLstMonthlyBillOuterDtls.isEmpty()) {
				lStrTreasuryName = lObjPhysicalCaseInwardDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocCode));
				ColumnVo[] columnHeading = new ColumnVo[5];
				columnHeading[0] = new ColumnVo("Bank Name/Reporting Bank-Branch Name", 1, 32, 0, true, false, true);
				columnHeading[1] = new ColumnVo("No of    Pensioner", 2, 9, 1, false, false, true);
				columnHeading[2] = new ColumnVo("Gross", 2, 17, 1, false, false, true);
				columnHeading[3] = new ColumnVo("Deduction", 2, 17, 1, false, false, true);
				columnHeading[4] = new ColumnVo("Net", 2, 17, 1, false, false, true);

				List<List> arrOuter = new ArrayList<List>();
				ReportExportHelper lObjExport = new ReportExportHelper();

				for (int lIntCnt = 0; lIntCnt < lLstMonthlyBillOuterDtls.size(); lIntCnt++) {
					Object[] obj = lLstMonthlyBillOuterDtls.get(lIntCnt);
					if (lIntCnt == 0) {
						lSbHeader.append("MTR 38");
						lSbHeader.append("\r\n");
						lSbHeader.append("2071 PENSION BILL FOR THE MONTH OF ");
						if (obj[17] != null) {
							lSbHeader.append(lObjSmplDateFormat.format(lObjSimpleDateFormat.parse(obj[17].toString())));
						}
						lSbHeader.append("\r\n");
						lSbHeader.append("Treasury Name : " + lStrTreasuryName);
						lSbHeader.append("\r\n");

						lSbHeader.append(String.format("%-85s", "Scheme Descr. " + obj[3]));
						if (obj[9] != null) {
							lStrMajorHead = obj[9].toString();
						}
						if (obj[10] != null) {
							lStrSubMajorHead = obj[10].toString();
						}
						if (obj[11] != null) {
							lStrMinorHead = obj[11].toString();
						}
						if (obj[12] != null) {
							lStrSubMinorHead = obj[12].toString();
						}
						if (obj[13] != null) {
							lStrSubHead = obj[13].toString();
						}
						if (obj[14] != null) {
							lStrDemandCode = obj[14].toString();
						}
						if (obj[15] != null) {
							if ("C".equals(obj[15])) {
								lStrCharged = "Charged";
							} else if ("V".equals(obj[15])) {
								lStrCharged = "Voted";
							}
						}
						if (obj[16] != null) {
							if ("P".equals(obj[16])) {
								lStrPlan = "Plan";
							} else if ("N".equals(obj[16])) {
								lStrPlan = "Non Plan";
							}
						}
						lSbHeader.append("\r\n");
						lSbHeader.append(String.format("%40s", "Scheme Code : " + obj[2] + "    "));
						lSbHeader.append(String.format("%15s", "Demand Code : " + lStrDemandCode + " "));
						lSbHeader.append(String.format("%20s", "Major Head : " + lStrMajorHead + "  "));
						lSbHeader.append(String.format("%20s", "Sub Major Head : " + lStrSubMajorHead + "  "));
						lSbHeader.append(String.format("%20s", "Minor Head : " + lStrMinorHead + "  "));
						lSbHeader.append(String.format("%16s", "Sub Minor Head : " + lStrSubMinorHead + "  "));
						lSbHeader.append(String.format("%15s", "Sub Head : " + lStrSubHead + "  "));
						lSbHeader.append("\r\n");
						lSbHeader.append(String.format("%16s", "Charged/Voted : " + lStrCharged + "   "));
						lSbHeader.append(String.format("%16s", "Plan/Non Plan : " + lStrPlan));
						lSbHeader.append("\r\n");
						lSbHeader.append(String.format("%30s", "Bill No  : " + obj[1] + "    "));
						lSbHeader.append(String.format("%-95s", "Date   : " + lStrBillDate));
						lSbHeader.append("\r\n");
					}

					inList = new ArrayList();
					inList.add(obj[4]);
					inList.add(obj[5]);
					inList.add(obj[6]);
					inList.add(obj[7]);
					inList.add(obj[8]);
					// inList.add(obj[9]);
					lDbNetBillAmt = lDbNetBillAmt + Double.parseDouble(obj[8].toString());
					lDbDeductionAmt = lDbDeductionAmt + Double.parseDouble(obj[7].toString());
					arrOuter.add(inList);

				}
				lStrMonthlyOuterBill = lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), lStrFooter, null, 97, true, 1, true, "|");
				if (lStrMonthlyOuterBill != null) {
					lIntNoOfLinesPrintedOnPage = (lStrMonthlyOuterBill.split(lineSeperator)).length;
				}

				lStrNetAmount = "Below Rs.  " + lDbNetBillAmt.longValue() + "   " + "(In Words  " + EnglishDecimalFormat.convert1WithSpace(lDbNetBillAmt.longValue()) + " Only)" + "\r\n\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 2;
				lLstRecoveryDtls = lObjPensionBillDAO.getSchemeCodeWiseRecovery(lStrBillNo, gStrLocCode);

				if (!lLstRecoveryDtls.isEmpty()) {
					lSbHeader = new StringBuilder();
					arrOuter = new ArrayList<List>();
					columnHeading = new ColumnVo[2];
					columnHeading[0] = new ColumnVo("Deduction Scheme Code", 1, 28, 0, true, false, true);
					columnHeading[1] = new ColumnVo("Amount", 2, 20, 1, false, false, true);

					for (int lIntCnt = 0; lIntCnt < lLstRecoveryDtls.size(); lIntCnt++) {
						Object[] obj = lLstRecoveryDtls.get(lIntCnt);
						inList = new ArrayList();
						inList.add(obj[0]);
						if (obj[1] != null) {
							inList.add(obj[1]);
						}
						arrOuter.add(inList);
					}
					lStrMonthlyOuterBill = lStrMonthlyOuterBill
							+ lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), lStrFooter, null, 50, true, lIntNoOfLinesPrintedOnPage, true, "|");
				}
				if (lStrMonthlyOuterBill != null) {
					lIntNoOfLinesPrintedOnPage = lStrMonthlyOuterBill.split(lineSeperator).length;
				}

				List lLstTotalAmt = lObjPensionBillDAO.getMonthlyOuterBillTotalAmts(lStrBillNo, gStrLocCode);
				List<List<Object>> lLstOuter = new ArrayList<List<Object>>();
				List lLstData = new ArrayList();
				if (!lLstTotalAmt.isEmpty()) {
					// Object obj = lLstTotalAmt.get(0);
					Object[] obj = (Object[]) lLstTotalAmt.get(0);
					lDbTotalIr1Amt = Double.parseDouble(obj[0].toString());
					lDbTotalIr2Amt = Double.parseDouble(obj[1].toString());
					lDbTotalIr3Amt = Double.parseDouble(obj[2].toString());
					lDbTotalAlloc1Amt = Double.parseDouble(obj[3].toString());
					lDbTotalAlloc2Amt = Double.parseDouble(obj[4].toString());
					lDbTotalAlloc3Amt = Double.parseDouble(obj[5].toString());
					lDbTotalAlloc4Amt = Double.parseDouble(obj[6].toString());
					lDbTotalAlloc5Amt = Double.parseDouble(obj[7].toString());

					ColumnVo[] columnIrAllocAmt = new ColumnVo[8];
					columnIrAllocAmt[0] = new ColumnVo("IR1", 1, 10, 0, false, false, true);
					columnIrAllocAmt[1] = new ColumnVo("IR2", 1, 10, 0, true, false, true);
					columnIrAllocAmt[2] = new ColumnVo("IR3", 2, 10, 0, false, false, true);
					columnIrAllocAmt[3] = new ColumnVo("ALLOC1", 2, 10, 0, false, false, true);
					columnIrAllocAmt[4] = new ColumnVo("ALLOC2", 2, 10, 0, false, false, true);
					columnIrAllocAmt[5] = new ColumnVo("ALLOC3", 2, 10, 0, false, false, true);
					columnIrAllocAmt[6] = new ColumnVo("ALLOC4", 2, 10, 0, false, false, true);
					columnIrAllocAmt[7] = new ColumnVo("ALLOC5", 2, 10, 0, false, false, true);

					lLstData.add(lDbTotalIr1Amt.longValue());
					lLstData.add(lDbTotalIr2Amt.longValue());
					lLstData.add(lDbTotalIr3Amt.longValue());
					lLstData.add(lDbTotalAlloc1Amt.longValue());
					lLstData.add(lDbTotalAlloc2Amt.longValue());
					lLstData.add(lDbTotalAlloc3Amt.longValue());
					lLstData.add(lDbTotalAlloc4Amt.longValue());
					lLstData.add(lDbTotalAlloc5Amt.longValue());
					lLstOuter.add(lLstData);

					lStrMonthlyOuterBill = lStrMonthlyOuterBill
							+ lObjExport.getReportFile(lLstOuter, columnIrAllocAmt, lSbHeader.toString(), lStrFooter, null, 88, true, lIntNoOfLinesPrintedOnPage, true, "|");
				}

				if (lStrMonthlyOuterBill != null) {
					lIntNoOfLinesPrintedOnPage = (lStrMonthlyOuterBill.split(lineSeperator)).length;
				}
				lStrMonthlyOuterBill = lStrMonthlyOuterBill + "Category Wise Summary \r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 2;

				ColumnVo[] columnCtgryWiseSumry = new ColumnVo[3];
				columnCtgryWiseSumry[0] = new ColumnVo("No Of Pensioner", 2, 10, 1, false, false, true);
				columnCtgryWiseSumry[1] = new ColumnVo("Category Code Description", 1, 35, 0, true, false, true);
				columnCtgryWiseSumry[2] = new ColumnVo("Net Amount Rs.", 2, 14, 1, false, false, true);

				lLstOuter = new ArrayList<List<Object>>();
				lLstData = new ArrayList();
				lLstCategoryWiseSummary = lObjPensionBillDAO.getCategoryWiseSummary(lStrBillNo);
				if (!lLstCategoryWiseSummary.isEmpty()) {
					for (Object[] cols : lLstCategoryWiseSummary) {
						lLstData = new ArrayList();
						lLstData.add(cols[0]);
						lLstData.add(cols[2]);
						lLstData.add((long) (Double.parseDouble(cols[3].toString())));
						lLstOuter.add(lLstData);
					}
				}
				lStrMonthlyOuterBill = lStrMonthlyOuterBill
						+ lObjExport.getReportFile(lLstOuter, columnCtgryWiseSumry, lSbHeader.toString(), lStrFooter, null, 62, true, lIntNoOfLinesPrintedOnPage, true, "|");

				if (lStrMonthlyOuterBill != null) {
					lIntNoOfLinesPrintedOnPage = (lStrMonthlyOuterBill.split(lineSeperator)).length;
				}

				lSbHeader = new StringBuilder();
				lSbFooter.append("\r\n");
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
				}
				lSbFooter.append(String.format("%121s", "Signature With Stamp"));
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
				}
				lSbFooter.append("\r\n");
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
				}
				lSbFooter.append(String.format("%120s", "D.D.O. Pension"));
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
				}
				lSbFooter.append("\r\n");
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
				}
				lSbFooter.append("\r\n");
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
				}

				List blankList = new ArrayList();
				blankList.add("");
				blankList.add("");
				blankList.add("");
				lStrFooter = "------------------Used for Audit Section Only--------------------------------------Used for Cheque/Cash Section-------------------\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 2;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lSbFooter.append((char) 12);
				}
				columnHeading = new ColumnVo[3];
				columnHeading[0] = new ColumnVo("", 1, 60, 0, true, false, true);
				columnHeading[1] = new ColumnVo("", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("", 1, 40, 0, false, false, true);
				arrOuter = new ArrayList<List>();
				// inList = new ArrayList();
				// inList.add("-----------------Used for Audit Section Only------------------");
				// inList.add("----------------Used for Cheque/Cash Section------------------");
				// arrOuter.add(inList);
				arrOuter.add(blankList);
				inList = new ArrayList();
				inList.add("PASSED FOR PAYMENT AND PAY RS. " + lDbNetBillAmt.longValue() + "/-");
				inList.add("");
				inList.add("CHEQUE NO/ECS/");
				arrOuter.add(inList);
				inList = new ArrayList();
				inList.add("IN WORDS RUPEES " + (EnglishDecimalFormat.convert1WithSpace(lDbNetBillAmt.longValue())).toUpperCase() + " ONLY");
				inList.add("");
				inList.add("EFT/NEFT Advice No");
				arrOuter.add(inList);
				inList = new ArrayList();
				inList.add("");
				inList.add("");
				inList.add("Date");
				arrOuter.add(inList);
				arrOuter.add(blankList);
				arrOuter.add(blankList);
				arrOuter.add(blankList);
				inList = new ArrayList();
				inList.add("ASST.PAY AND ACCOUNT OFFICER," + lStrTreasuryName + " :");
				inList.add("");
				inList.add("ASST.PAY AND ACCOUNT OFFICER," + lStrTreasuryName);
				arrOuter.add(inList);
				inList = new ArrayList();
				inList.add("ADDL.TREASURY OFFICER  :");
				inList.add("");
				inList.add("ADDL.TREASURY OFFICER  ");
				arrOuter.add(inList);

				lStrFooter = lStrFooter + lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), "", null, 132, true, lIntNoOfLinesPrintedOnPage, false, "");
				if (lStrFooter != null) {
					lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + (lStrFooter.split(lineSeperator)).length;
				}

				lStrFooter = lStrFooter + "------------------For Purpose Of A.G.---------------------------------------------------------------------------------------------\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 2;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
				}
				lStrFooter = lStrFooter + "Admitted for Rs.\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
				}
				lStrFooter = lStrFooter + "Objected for Rs.\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
				}
				lStrFooter = lStrFooter + "\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
				}
				lStrFooter = lStrFooter + "\r\n";
				lIntNoOfLinesPrintedOnPage = lIntNoOfLinesPrintedOnPage + 1;
				if ((lIntNoOfLinesPrintedOnPage != 0) && (lIntNoOfLinesPrintedOnPage % 52 == 0)) {
					lStrFooter = lStrFooter + (char) 12;
				}
				lStrFooter = lStrFooter + String.format("%70s", "Audit Officer");
				lStrMonthlyOuterBill = lStrMonthlyOuterBill + lStrNetAmount + lSbFooter.toString() + lStrFooter;

				Map lDetailMap = new HashMap();
				String lStrExportTo = DBConstants.DIS_ONSCREEN;
				if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
					lDetailMap.put(DBConstants.DIS_ONSCREEN, lStrMonthlyOuterBill);
				} else if ((DBConstants.DIS_TEXTFILE).equals(lStrExportTo)) {
					lDetailMap.put(DBConstants.DIS_TEXTFILE, lStrMonthlyOuterBill);
				}
				ReportExportHelper rptExpHelper = new ReportExportHelper();
				rptExpHelper.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
				resObj.setResultValue(inputMap);
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject viewMonthlyBillVoucherMapping(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrBillNo = null;
		RltBillPartyDAO lObjRltBillPartyDAO = null;
		List<RltBillParty> lLstRltBillPartyDtls = null;
		TrnBillRegisterDAO lObjTrnBillRegisterDAO = null;
		TrnBillRegister lObjTrnBillRegister = null;
		SimpleDateFormat lSdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			setSessionInfo(inputMap);
			lStrBillNo = StringUtility.getParameter("billNo", request).trim();
			lObjRltBillPartyDAO = new RltBillPartyDAOImpl(RltBillParty.class, serv.getSessionFactory());
			lObjTrnBillRegisterDAO = new TrnBillRegisterDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
			if (lStrBillNo.length() > 0) {
				lLstRltBillPartyDtls = lObjRltBillPartyDAO.getPartyByBill(Long.valueOf(lStrBillNo));
				lObjTrnBillRegister = lObjTrnBillRegisterDAO.read(Long.valueOf(lStrBillNo));
			}
			inputMap.put("lLstRltBillPartyDtls", lLstRltBillPartyDtls);
			inputMap.put("lObjTrnBillRegister", lObjTrnBillRegister);
			inputMap.put("totalRecords", lLstRltBillPartyDtls.size());
			inputMap.put("billNo", lStrBillNo);
			inputMap.put("currdate", lSdf.format(gDate));
			resObj.setResultValue(inputMap);
			resObj.setViewName("MonthlyBillVoucherMpg");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject saveMonthlyBillVoucherMapping(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		RltBillPartyDAO lObjRltBillPartyDAO = null;
		List<RltBillParty> lLstRltBillPartyDtls = null;
		TrnBillRegisterDAO lObjTrnBillRegisterDAO = null;
		TrnBillRegister lObjTrnBillRegister = null;
		String lStrBillNo = null;
		String lStrVoucherNo = null;
		String lStrVoucherDate = null;
		String lStrBillChequeDtls = null;
		String[] lArrStrBillChequeDtls = null;
		SimpleDateFormat lSdf = new SimpleDateFormat("dd/MM/yyyy");
		String lStrPartyDtlsId = null;
		Long lLngChequeNo = null;
		Date lDtVoucher = null;
		StringBuilder lSBResp = new StringBuilder();
		List<Long> lLstChequeNo = null;
		List<Long> lLstBillPartyId = null;
		Map<Long, Long> lMapBillPartyCheque = null;
		List<RltBillParty> lLstRltBillParty = null;
		String lStrChequeNo = null;
		try {
			setSessionInfo(inputMap);
			lStrBillNo = StringUtility.getParameter("billNo", request).trim();
			lStrVoucherNo = StringUtility.getParameter("voucherNo", request).trim();
			lStrVoucherDate = StringUtility.getParameter("voucherDate", request).trim();
			lArrStrBillChequeDtls = StringUtility.getParameterValues("partyChequeDtls", request);
			lObjRltBillPartyDAO = new RltBillPartyDAOImpl(RltBillParty.class, serv.getSessionFactory());
			lObjTrnBillRegisterDAO = new TrnBillRegisterDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
			if ((lStrBillNo.length() > 0 && lStrVoucherNo.length() > 0) && lStrVoucherDate.length() > 0) {
				// --Saving vouche date and voucher no in trn_bill_register
				lObjTrnBillRegister = lObjTrnBillRegisterDAO.read(Long.valueOf(lStrBillNo));

				lDtVoucher = lSdf.parse(lStrVoucherDate);
				lObjTrnBillRegister.setVoucherNo(Long.valueOf(lStrVoucherNo));
				lObjTrnBillRegister.setVoucherDate(lDtVoucher);
				lObjTrnBillRegister.setUpdatedDate(gDate);
				lObjTrnBillRegister.setUpdatedPostId(gLngPostId);
				lObjTrnBillRegister.setUpdatedUserId(gLngUserId);
				lObjTrnBillRegisterDAO.update(lObjTrnBillRegister);

				String[] lArrDtls = null;
				// for (String lStrBilLChequeDtls : lArrStrBillChequeDtls) {
				// lArrDtls = lStrBilLChequeDtls.split("~");
				// lStrPartyDtlsId = lArrDtls[0];
				// lStrChequeNo = null;
				// // --If cheque no is left blank then assigning null to it.
				// if (lArrDtls.length == 2) {
				//
				// if (lArrDtls[1] != null && lArrDtls[1].length() > 0) {
				// lStrChequeNo = lArrDtls[1];
				// }
				// }
				// if (lStrPartyDtlsId.length() > 0) {
				// lObjRltBillParty =
				// lObjRltBillPartyDAO.read(Long.valueOf(lStrPartyDtlsId));
				// lObjRltBillParty.setChequeNo((lStrChequeNo != null) ?
				// Long.valueOf(lStrChequeNo) : null);
				// lObjRltBillParty.setChequeDate((lStrChequeNo != null) ?
				// lDtVoucher : null);
				// lObjRltBillParty.setUpdtaedDate(gDate);
				// lObjRltBillParty.setUpdatedUserId(gLngUserId);
				// lObjRltBillParty.setUpdatedPostId(gLngPostId);
				// lObjRltBillPartyDAO.update(lObjRltBillParty);
				// }
				// }

				// --Preparing list and map of party_dtls_id nad cheque no which
				// are selected from fornt end for saving
				lLstBillPartyId = new ArrayList<Long>();
				lMapBillPartyCheque = new HashMap<Long, Long>();
				for (String lStrBilLChequeDtls : lArrStrBillChequeDtls) {
					lArrDtls = lStrBilLChequeDtls.split("~");
					lStrPartyDtlsId = lArrDtls[0];
					lStrChequeNo = null;
					// --If cheque no is left blank then not saving cheque no.
					if (lArrDtls.length == 2) {

						if (lArrDtls[1] != null && lArrDtls[1].length() > 0) {
							lStrChequeNo = lArrDtls[1];
							lLstBillPartyId.add(Long.valueOf(lStrPartyDtlsId));
							lMapBillPartyCheque.put(Long.valueOf(lStrPartyDtlsId), Long.valueOf(lStrChequeNo));
						}
					}
				}
				// --Saving cheque no in selected records and cheque date =
				// voucher date in all the party recover in rlt_bill_party
				lLstRltBillParty = lObjRltBillPartyDAO.getPartyByBill(Long.valueOf(lStrBillNo));
				for (RltBillParty lObjRltBillParty : lLstRltBillParty) {
					if (lLstBillPartyId.contains(lObjRltBillParty.getBillPartyId())) {
						lLngChequeNo = lMapBillPartyCheque.get(lObjRltBillParty.getBillPartyId());
						if (lLngChequeNo != null) {
							lObjRltBillParty.setChequeNo(lLngChequeNo);
						}
					}
					lObjRltBillParty.setChequeDate(lDtVoucher);
					lObjRltBillParty.setUpdtaedDate(gDate);
					lObjRltBillParty.setUpdatedUserId(gLngUserId);
					lObjRltBillParty.setUpdatedPostId(gLngPostId);
					lObjRltBillPartyDAO.update(lObjRltBillParty);
				}
				lSBResp.append("<XMLDOC>");
				lSBResp.append("<STATUS>");
				lSBResp.append("SUCCESS");
				lSBResp.append("</STATUS>");
				lSBResp.append("</XMLDOC>");
			}

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
}
