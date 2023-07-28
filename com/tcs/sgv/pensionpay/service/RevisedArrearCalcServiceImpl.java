package com.tcs.sgv.pensionpay.service;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ReportExportHelper;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMpgDAOImpl;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttdocMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.RevisedArrearCalcDAO;
import com.tcs.sgv.pensionpay.dao.RevisedArrearCalcDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionArrearDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionArrearDtlsDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstReEmploymentDtls;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCutDtls;
import com.tcs.sgv.pensionpay.valueobject.ValidPcodeView;


public class RevisedArrearCalcServiceImpl extends ServiceImpl implements RevisedArrearCalcService {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for EmpId */
	Long gLngEmpId = null;

	/* Global Variable for Location Id */
	String gStrLocId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Location Code */
	Long glLocId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	long gLngFinYearId = 0;

	/* Global Variable for Current Date */
	Date gCurrDate = null;

	ServiceLocator gServLoc = null;
	
	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	Log gLogger = LogFactory.getLog(getClass());
	
	ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	static Map<Long, Map<String, Object>> gMapRateFromHeadCode = new HashMap<Long, Map<String, Object>>();
	
	static Map<String,BigDecimal> gMapDPRate =  new HashMap<String,BigDecimal>();

	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gLngFinYearId = Long.parseLong(SessionHelper.getFinYrId(inputMap).toString());
		gCurrDate = DBUtility.getCurrentDateFromDB();
		glLocId = SessionHelper.getLocationId(inputMap);
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	public ResultObject loadArrearCalculationPage(Map<String, Object> inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		CmnLookupMstDAOImpl cmnLookupMstObj = new CmnLookupMstDAOImpl(CmnLookupMst.class, servLoc.getSessionFactory());
		List listYesNo = null;
		List RevisedType = null;
		List<ComboValuesVO> lLstBanks = null;
		List<ComboValuesVO> lLstBankBranch = null;
		String lStrBillFlag="";
		String lStrPpoNo = "";
		String lStrRowCnt = "";
		try {
			setSessionInfo(inputMap);
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			listYesNo = cmnLookupMstObj.getAllChildrenByLookUpNameAndLang("Yes/No_PPO", gLngLangId);
			RevisedType = cmnLookupMstObj.getAllChildrenByLookUpNameAndLang("RevisedType", gLngLangId);
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);
			inputMap.put("listYesNo", listYesNo);
			inputMap.put("RevisedType", RevisedType);
			inputMap.put("lLstBanks", lLstBanks);
			lStrBillFlag = StringUtility.getParameter("BillFlag", request);
			lStrPpoNo = StringUtility.getParameter("PpoNo", request);
			lStrRowCnt = StringUtility.getParameter("rowCnt", request);
			
			inputMap.put("BillFlag", lStrBillFlag);
			inputMap.put("PpoNo", lStrPpoNo);
			inputMap.put("RowCnt", lStrRowCnt);
			resObj.setResultValue(inputMap);
			if(!"".equals(lStrBillFlag))
			{
				resObj.setViewName("OpenPopupRevisedArrear");
			}
			else
			{
				resObj.setViewName("OpenPopupForRevisedArrear");
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

	public ResultObject calculateArrearForRevision(Map<String, Object> inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);

		boolean isSave = false;

		if (inputMap.get("isSave") != null) {
			isSave = (Boolean) inputMap.get("isSave");
		}

		StringBuffer sbLineDisplay = null;
		if (!isSave) {
			sbLineDisplay = new StringBuffer();
		}

		RevisedArrearCalcDAO lObjRevisedArrearCalcDAO =new RevisedArrearCalcDAOImpl(serv.getSessionFactory());
		NewPensionBillDAOImpl lPensionBillDAO = new NewPensionBillDAOImpl(serv.getSessionFactory());
		List<List<RltPensionHeadcodeRate>> lLstRltPensionHeadcodeRate = new ArrayList<List<RltPensionHeadcodeRate>>();
	   
		Calendar lObjCalendar = null;
		try {
			SimpleDateFormat lObjSimpleDateFormat=new SimpleDateFormat("dd/MM/yyyy hh:MM:ss");
			SimpleDateFormat lObjSmplDtFormat=new SimpleDateFormat("dd/MM/yyyy");
			String lStrCurrDt = lObjSimpleDateFormat.format(gCurrDate);
			int lIntRowNo = 21;
			int lIntPageNo = 1;
			long lLngPunishAmtNew = Long.valueOf(0);
			long lLngPunishAmtOld = Long.valueOf(0);
			long lLngADPAmountNew = Long.valueOf(0);
			long lLngADPAmountOld = Long.valueOf(0);
			long lLngDPAmountNew = Long.valueOf(0);
			Long lLngTIAmountNew = Long.valueOf(0);
			long lLngDPAmountOld = Long.valueOf(0);
			Long lLngTIAmountOld = Long.valueOf(0);
			long lLngDPYesDaysNew = Long.valueOf(0);
			long lLngDPYesDaysOld = Long.valueOf(0);
			long lLngOldPeonAllow = Long.valueOf(0);
			long lLngNewPeonAllow = Long.valueOf(0);
			long lLngOldMedicalAllow = Long.valueOf(0);
			long lLngNewMedicalAllow = Long.valueOf(0);
			long lLngOldGallantryAmt = Long.valueOf(0);
			long lLngNewGallantryAmt = Long.valueOf(0);
			long lLngOldOtherAllow = Long.valueOf(0);
			long lLngNewOtherAllow = Long.valueOf(0);
			Long[] lLngIRAmountOld = new Long[3];
			Long[] lLngIRAmountNew = new Long[3];

			Date lDtFromDt = null;
			Date lDtFromDtUpdated = null;
			Date lDtToDt = null;
			Date lDtToDtUpdated = null;
			boolean isEndMonth = false;

			double lDbNewBasicTotal = 0D;
			double lDbOldBasicTotal = 0D;
			Double lDbNewPunishCutAmt = 0D;
            Double lDbOldPunishCutAmt = 0D;
//			double lDbNewPnsnCutAmt = 0D;
//			double lDbOldPnsnCutAmt = 0D;

			Long lLngTotalDue = Long.valueOf(0);
			Long lLngTotalDrawn = Long.valueOf(0);
			Long lLngNetArrear = Long.valueOf(0);
			Long lLngTotalPunishOld = Long.valueOf(0);
			Long lLngTotalPunishNew = Long.valueOf(0);
			Long lLngTotalDPOld = Long.valueOf(0);
			Long lLngTotalIR1Old = Long.valueOf(0);
			Long lLngTotalIR2Old = Long.valueOf(0);
			Long lLngTotalIR3Old = Long.valueOf(0);
			Long lLngTotalTIOld = Long.valueOf(0);
			Long lLngTotalDPNew = Long.valueOf(0);
			Long lLngTotalADPNew = Long.valueOf(0);
			Long lLngTotalADPOld = Long.valueOf(0);
			
			Long lLngTotalIR1New = Long.valueOf(0);
			Long lLngTotalIR2New = Long.valueOf(0);
			Long lLngTotalIR3New = Long.valueOf(0);
			Long lLngTotalTINew = Long.valueOf(0);
			Long lLngTotalDueSum = Long.valueOf(0);
			Long lLngTotalDrawnSum = Long.valueOf(0);
			Long lLngNetArrearSum = Long.valueOf(0);
			Long lLngCVPEffAmountNew = Long.valueOf(0);
			Long lLngTotalCVPAmountNew = Long.valueOf(0);
			Long lLngCVPEffAmountOld = Long.valueOf(0);
			Long lLngTotalCVPAmountOld = Long.valueOf(0);
			Long lLngTotalPeonAllowOld = Long.valueOf(0);
			Long lLngTotalPeonAllowNew = Long.valueOf(0);
			Long lLngTotalMedicalAllowOld = Long.valueOf(0);
			Long lLngTotalMedicalAllowNew = Long.valueOf(0);
			Long lLngTotalGallantryAmtOld = Long.valueOf(0);
			Long lLngTotalGallantryAmtNew = Long.valueOf(0);
			Long lLngTotalOtherAllowOld = Long.valueOf(0);
			Long lLngTotalOtherAllowNew = Long.valueOf(0);
						
			//for page wise total amount
			Double lLngPageTotalNewBasic = Double.valueOf(0);
			Long lLngPageTotalOldPunish = Long.valueOf(0);
			Long lLngPageTotalNewPunish = Long.valueOf(0);
			Long lLngPageTotalNewADP = Long.valueOf(0);
			Long lLngPageTotalNewDP = Long.valueOf(0);
			Long lLngPageTotalNewIR1 = Long.valueOf(0);
			Long lLngPageTotalNewIR2 = Long.valueOf(0);
			Long lLngPageTotalNewIR3 = Long.valueOf(0);
			Long lLngPageTotalNewTI = Long.valueOf(0);
			Long lLngPageTotalNewCVP = Long.valueOf(0);
			Long lLngPageTotalDue = Long.valueOf(0);
			Double lLngPageTotalOldBasic = Double.valueOf(0);
			Long lLngPageTotalOldADP = Long.valueOf(0);
			Long lLngPageTotalOldDP = Long.valueOf(0);
			Long lLngPageTotalOldIR1 = Long.valueOf(0);
			Long lLngPageTotalOldIR2 = Long.valueOf(0);
			Long lLngPageTotalOldIR3 = Long.valueOf(0);
			Long lLngPageTotalOldTI = Long.valueOf(0);
			Long lLngPageTotalOldCVP = Long.valueOf(0);
			Long lLngPageTotalDrawn = Long.valueOf(0);
			Long lLngPageNetArrer = Long.valueOf(0);
			Long lLngPageTotalOldPeonAllow = Long.valueOf(0);
			Long lLngPageTotalNewPeonAllow = Long.valueOf(0);
			Long lLngPageTotalOldMedicalAllow = Long.valueOf(0);
			Long lLngPageTotalNewMedicalAllow = Long.valueOf(0);
			Long lLngPageTotalOldGallantryAmt = Long.valueOf(0);
			Long lLngPageTotalNewGallantryAmt = Long.valueOf(0);
			Long lLngPageTotalOldOtherAllow = Long.valueOf(0);
			Long lLngPageTotalNewOtherAllow = Long.valueOf(0);
			
			int revTypeCntr = 0;

//			int oldPnsnCutCntr = 0;
			int oldBasicCntr = 0;
			int oldDPCntr = 0;
			int oldCVPCntr = 0;
			int oldReEmpCntr = 0;
//			int newPnsnCutCntr = 0;
			int punishCutCntr = 0;
			int newBasicCntr = 0;
			int newDPCntr = 0;
			int newCVPCntr = 0;
			int newReEmpCntr = 0;
			int peonAllowCntr = 0;
			int medicalAllowCntr = 0;
			int gallantryCntr = 0;
			int otherAllowCntr = 0;

			double lDbNewBasicUpdated = 0D;
			double lDbOldBasicUpdated = 0D;

			String[] lStrArrMonth = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

			/** Input from jsp starts */
			
			/**
			 * General Inputs
			 */
			String lStrPnsnrName=StringUtility.getParameter("hdnPensionerName", request);
			String lStrBankName=StringUtility.getParameter("hidBankName", request);
			String lStrBranchName=StringUtility.getParameter("hidBranchName", request);
			String lStrAccountNo=StringUtility.getParameter("hidAccountNo", request);
			String lStrSupplyFlg = StringUtility.getParameter("supplyFlg", request);
			String[] lStrRevType = StringUtility.getParameterValues("cmbRevisionType", request);
			
			String lStrppoNo = StringUtility.getParameter("hidppono", request).trim();
			String lStrPensionerCode = StringUtility.getParameter("hidPnsnrCode", request).trim();
			long pensionReqId = Long.parseLong(StringUtility.getParameter("hidPensnReqId", request).trim());
			long lLngHeadCode = Long.parseLong(StringUtility.getParameter("hidheadcode", request).trim());
			String lStrPnsnrDOB = StringUtility.getParameter("hidPnsnrDOB", request);
			String lStrPnsnrDOR = StringUtility.getParameter("hidPnsnrDOR", request);
			String lStrBillFlag=StringUtility.getParameter("billFlag", request);
			String lStrRowCnt = StringUtility.getParameter("rowCnt", request);

			Date lDtPnsnrDOB = null;
			Date lDtPnsnrDOR = null;
			if (lStrPnsnrDOB != null && !"".equals(lStrPnsnrDOB)) {
				lDtPnsnrDOB = IFMSCommonServiceImpl.getDateFromString(lStrPnsnrDOB);
			}
			if (lStrPnsnrDOR != null && !"".equals(lStrPnsnrDOR)) {
				lDtPnsnrDOR = IFMSCommonServiceImpl.getDateFromString(lStrPnsnrDOR);
			}
			String lStrPnsnrDOD = StringUtility.getParameter("hidPnsnrDOD", request);
			Date lDtPnsnrDOD = null;
			if (lStrPnsnrDOD != null && !"".equals(lStrPnsnrDOD)) {
				lDtPnsnrDOD = IFMSCommonServiceImpl.getDateFromString(lStrPnsnrDOD);
			}
			String lStrFamMemDOB = StringUtility.getParameter("hidFamMemDOB", request);
			Date lDtFamMemDOB = null;
			if (lStrFamMemDOB != null && !"".equals(lStrFamMemDOB)) {
				lDtFamMemDOB = IFMSCommonServiceImpl.getDateFromString(lStrFamMemDOB);
			}
			String lStrFamMemDOD = StringUtility.getParameter("hidFamMemDOD", request);
			Date lDtFamMemDOD = null;
			if (lStrFamMemDOD != null && !"".equals(lStrFamMemDOD)) {
				lDtFamMemDOD = IFMSCommonServiceImpl.getDateFromString(lStrFamMemDOD);
			}

			String lStrFP1Date = StringUtility.getParameter("hidFP1Date", request);
			Date lDtFP1Date = null;
			if (lStrFP1Date != null && !"".equals(lStrFP1Date)) {
				lDtFP1Date = IFMSCommonServiceImpl.getDateFromString(lStrFP1Date);
			}
			String lStrFP2Date = StringUtility.getParameter("hidFP2Date", request);
			Date lDtFP2Date = null;
			if (lStrFP2Date != null && !"".equals(lStrFP2Date)) {
				lDtFP2Date = IFMSCommonServiceImpl.getDateFromString(lStrFP2Date);
			}
			String lStrNewFP1Basic = StringUtility.getParameter("hidFP1Amnt", request);
			Double lDbNewFP1Basic = 0D;
			if (lStrNewFP1Basic != null && !"".equals(lStrNewFP1Basic)) {
				lDbNewFP1Basic = Double.parseDouble(lStrNewFP1Basic);
			}
			String lStrNewFP2Basic = StringUtility.getParameter("hidFP2Amnt", request);
			Double lDbNewFP2Basic = 0D;
			if (lStrNewFP2Basic != null && !"".equals(lStrNewFP2Basic)) {
				lDbNewFP2Basic = Double.parseDouble(lStrNewFP2Basic);
			}
			String lStrCommensionDate = StringUtility.getParameter("hidCommensionDate", request);
			Date lDtCommensionDate = null;
			if (lStrCommensionDate != null && !"".equals(lStrCommensionDate)) {
				lDtCommensionDate = IFMSCommonServiceImpl.getDateFromString(lStrCommensionDate);
			}
			String lStrEndDate = StringUtility.getParameter("hidPPOEndDate", request);
			Date lDtEndDate = null;
			if (lStrEndDate != null && !"".equals(lStrEndDate)) {
				lDtEndDate = IFMSCommonServiceImpl.getDateFromString(lStrEndDate);
			}
			
			/**
			 * Pension basic input
			 */
			String[] lStrOldBasic = StringUtility.getParameterValues("txtOldBasic", request);
			String[] lStrOldBasicEffFrom = StringUtility.getParameterValues("txtOldBasicEffFrom", request);
			String[] lStrOldBasicEffTo = StringUtility.getParameterValues("txtOldBasicEffTo", request);
			Double[] lDbOldBasic = new Double[lStrOldBasic.length];
			Date[] lDtOldBasicEffFrom = new Date[lStrOldBasic.length];
			int[] lIntOldFromMonth = new int[lStrOldBasic.length];
			int[] lIntOldFromYear = new int[lStrOldBasic.length];
			int[] lIntOldStartDate = new int[lStrOldBasic.length];
			Date[] lDtOldBasicEffTo = new Date[lStrOldBasicEffTo.length];
			int[] lIntOldToMonth = new int[lStrOldBasicEffTo.length];
			int[] lIntOldToYear = new int[lStrOldBasicEffTo.length];
			int[] lIntOldEndMonthDays = new int[lStrOldBasicEffTo.length];
			for (int i = 0; i < lStrOldBasic.length; i++) {
				lDbOldBasic[i] = Double.parseDouble(lStrOldBasic[i]);
				if (!"".equals(lStrOldBasicEffFrom[i]) && lStrOldBasicEffFrom[i] != null) {
					lDtOldBasicEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldBasicEffFrom[i]);
					lIntOldFromMonth[i] = Integer.parseInt(lStrOldBasicEffFrom[i].substring(3, 5));
					lIntOldFromYear[i] = Integer.parseInt(lStrOldBasicEffFrom[i].substring(6));
					lIntOldStartDate[i] = Integer.parseInt(lStrOldBasicEffFrom[i].substring(0, 2));
				}
				if (!"".equals(lStrOldBasicEffTo[i]) && lStrOldBasicEffTo[i] != null)

				{
					lDtOldBasicEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldBasicEffTo[i]);
					lIntOldToMonth[i] = Integer.parseInt(lStrOldBasicEffTo[i].substring(3, 5));
					lIntOldToYear[i] = Integer.parseInt(lStrOldBasicEffTo[i].substring(6));
					lIntOldEndMonthDays[i] = Integer.parseInt(lStrOldBasicEffTo[i].substring(0, 2));
				}
			}

			String[] lStrNewBasic = StringUtility.getParameterValues("txtNewBasic", request);
			String[] lStrNewBasicEffFrom = StringUtility.getParameterValues("txtOldBasicEffFrom", request);
			String[] lStrNewBasicEffTo = StringUtility.getParameterValues("txtOldBasicEffTo", request);
			Double[] lDbNewBasic = new Double[lStrNewBasic.length];
			Date[] lDtNewBasicEffFrom = new Date[lStrNewBasic.length];
			int[] lIntNewFromMonth = new int[lStrNewBasic.length];
			int[] lIntNewFromYear = new int[lStrNewBasic.length];
			int[] lIntNewStartDate = new int[lStrNewBasic.length];
			Date[] lDtNewBasicEffTo = new Date[lStrNewBasicEffTo.length];
			int[] lIntNewToMonth = new int[lStrNewBasicEffTo.length];
			int[] lIntNewToYear = new int[lStrNewBasicEffTo.length];
			int[] lIntNewEndMonthDays = new int[lStrNewBasicEffTo.length];
			for (int i = 0; i < lStrNewBasic.length; i++) {
				lDbNewBasic[i] = Double.parseDouble(lStrNewBasic[i]);
				if (!"".equals(lStrNewBasicEffFrom[i]) && lStrNewBasicEffFrom[i] != null) {
					lDtNewBasicEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrNewBasicEffFrom[i]);
					lIntNewFromMonth[i] = Integer.parseInt(lStrNewBasicEffFrom[i].substring(3, 5));
					lIntNewFromYear[i] = Integer.parseInt(lStrNewBasicEffFrom[i].substring(6));
					lIntNewStartDate[i] = Integer.parseInt(lStrNewBasicEffFrom[i].substring(0, 2));
				}
				if (!"".equals(lStrNewBasicEffTo[i]) && lStrNewBasicEffTo[i] != null)

				{
					lDtNewBasicEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrNewBasicEffTo[i]);
					lIntNewToMonth[i] = Integer.parseInt(lStrNewBasicEffTo[i].substring(3, 5));
					lIntNewToYear[i] = Integer.parseInt(lStrNewBasicEffTo[i].substring(6));
					lIntNewEndMonthDays[i] = Integer.parseInt(lStrNewBasicEffTo[i].substring(0, 2));
				}
			}

			Date lDtMinDate = lDtNewBasicEffFrom[0];
			for (int i = 1; i < lDtNewBasicEffTo.length; i++) {
				if (lDtNewBasicEffFrom[i] != null && lDtNewBasicEffFrom[i].before(lDtMinDate)) {
					lDtMinDate = lDtNewBasicEffFrom[i];
				}
			}
			String lStrMinDate = IFMSCommonServiceImpl.getStringFromDate(lDtMinDate);

			Date lDtMaxDate = lDtNewBasicEffTo[0];
			for (int i = 1; i < lDtNewBasicEffTo.length; i++) {
				if (lDtNewBasicEffTo[i] != null && lDtNewBasicEffTo[i].after(lDtMaxDate)) {
					lDtMaxDate = lDtNewBasicEffTo[i];
				}
			}
			String lStrMaxDate = IFMSCommonServiceImpl.getStringFromDate(lDtMaxDate);

			/**
			 * Punishment Cut input
			 */
			String[] lStrPunishAmt = StringUtility.getParameterValues("txtPnshmntAmount", request);
			String[] lStrPunishFromDate = StringUtility.getParameterValues("txtPnshmntFromDate", request);
			String[] lStrPunishToDate = StringUtility.getParameterValues("txtPnshmntToDate", request);
			
			Double[] lDbPunishAmt = new Double[lStrPunishAmt.length];
			Date[] lDtPunishEffFrom = new Date[lStrPunishFromDate.length];
			Date[] lDtPunishEffTo = new Date[lStrPunishToDate.length];
			
			for (int i = 0; i < lStrPunishFromDate.length; i++) {
				if(!"".equals(lStrPunishAmt[i]) && lStrPunishAmt[i] != null){
					lDbPunishAmt[i] = Double.parseDouble(lStrPunishAmt[i]);
				}
				if (!"".equals(lStrPunishFromDate[i]) && lStrPunishFromDate[i] != null) {
					lDtPunishEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrPunishFromDate[i]);
				}
				if (!"".equals(lStrPunishToDate[i]) && lStrPunishToDate[i] != null) {
					lDtPunishEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrPunishToDate[i]);
				}
			}

			/**
			 * DP input
			 */
			String[] lStrOldDP = StringUtility.getParameterValues("cmbOldDP", request);
			String[] lStrOldDPEffFrom = StringUtility.getParameterValues("txtOldDPEffFrom", request);
			String[] lStrOldDPEffTo = StringUtility.getParameterValues("txtOldDPEffTo", request);
			Date[] lDtOldDPEffFrom = new Date[lStrOldDPEffFrom.length];
			Date[] lDtOldDPEffTo = new Date[lStrOldDPEffTo.length];
			for (int i = 0; i < lStrOldDP.length; i++) {
				if (!"".equals(lStrOldDPEffFrom[i]) && lStrOldDPEffFrom[i] != null) {
					lDtOldDPEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldDPEffFrom[i]);
				}
				if (!"".equals(lStrOldDPEffTo[i]) && lStrOldDPEffTo[i] != null)

				{
					lDtOldDPEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldDPEffTo[i]);
				}
			}
			String[] lStrNewDP = StringUtility.getParameterValues("cmbNewDP", request);
			String[] lStrNewDPEffFrom = StringUtility.getParameterValues("txtOldDPEffFrom", request);
			String[] lStrNewDPEffTo = StringUtility.getParameterValues("txtOldDPEffTo", request);
			Date[] lDtNewDPEffFrom = new Date[lStrNewDPEffFrom.length];
			Date[] lDtNewDPEffTo = new Date[lStrNewDPEffTo.length];
			for (int i = 0; i < lStrNewDP.length; i++) {
				if (!"".equals(lStrNewDPEffFrom[i]) && lStrNewDPEffFrom[i] != null) {
					lDtNewDPEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrNewDPEffFrom[i]);
				}
				if (!"".equals(lStrNewDPEffTo[i]) && lStrNewDPEffTo[i] != null) {
					lDtNewDPEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrNewDPEffTo[i]);
				}
			}

			/**
			 * CVP input
			 */
			String[] lStrOldCVP = StringUtility.getParameterValues("txtOldCVP", request);
			String[] lStrOldCVPEffFrom = StringUtility.getParameterValues("txtOldCVPEffFrom", request);
			String[] lStrOldCVPEffTo = StringUtility.getParameterValues("txtOldCVPEffTo", request);
			Date[] lDtOldCVPEffFrom = new Date[lStrOldCVPEffFrom.length];
			Date[] lDtOldCVPEffTo = new Date[lStrOldCVPEffTo.length];
			Double[] lDbOldCVP = new Double[lStrOldCVP.length];
			for (int i = 0; i < lStrOldCVP.length; i++) {
				if (!"".equals(lStrOldCVP[i]) && lStrOldCVP[i] != null) {
					lDbOldCVP[i] = Double.parseDouble(lStrOldCVP[i]);
					if (!"".equals(lStrOldCVPEffFrom[i]) && lStrOldCVPEffFrom[i] != null) {
						lDtOldCVPEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldCVPEffFrom[i]);
					}
					if (!"".equals(lStrOldCVPEffTo[i]) && lStrOldCVPEffTo[i] != null) {
						lDtOldCVPEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldCVPEffTo[i]);
					}
				}
			}
			String[] lStrNewCVP = StringUtility.getParameterValues("txtNewCVP", request);
			String[] lStrNewCVPEffFrom = StringUtility.getParameterValues("txtOldCVPEffFrom", request);
			String[] lStrNewCVPEffTo = StringUtility.getParameterValues("txtOldCVPEffTo", request);
			Date[] lDtNewCVPEffFrom = new Date[lStrNewCVPEffFrom.length];
			Date[] lDtNewCVPEffTo = new Date[lStrNewCVPEffTo.length];
			Double[] lDbNewCVP = new Double[lStrNewCVP.length];
			for (int i = 0; i < lStrNewCVP.length; i++) {
				if (!"".equals(lStrNewCVP[i]) && lStrNewCVP[i] != null) {
					lDbNewCVP[i] = Double.parseDouble(lStrNewCVP[i]);
					if (!"".equals(lStrNewCVPEffFrom[i]) && lStrNewCVPEffFrom[i] != null) {
						lDtNewCVPEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrNewCVPEffFrom[i]);
					}
					if (!"".equals(lStrNewCVPEffTo[i]) && lStrNewCVPEffTo[i] != null) {
						lDtNewCVPEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrNewCVPEffTo[i]);
					}
				}
			}

			/**
			 * ReEmp input
			 */
			//String lStrDAInFlag = StringUtility.getParameter("radioPnsnSalary", request); 
			String[] lStrOldReEmpEffFrom = StringUtility.getParameterValues("txtOldReEmpEffFrom", request);
			String[] lStrOldReEmpEffTo = StringUtility.getParameterValues("txtOldReEmpEffTo", request);
			String[] lStrDAInPnsnSal = StringUtility.getParameterValues("cmbDAInPnsnSal", request);
			
			Date[] lDtOldReEmpEffFrom = new Date[lStrOldReEmpEffFrom.length];
			Date[] lDtOldReEmpEffTo = new Date[lStrOldReEmpEffTo.length];
			for (int i = 0; i < lStrOldReEmpEffFrom.length; i++) {
				if (!"".equals(lStrOldReEmpEffFrom[i]) && lStrOldReEmpEffFrom[i] != null) {
					lDtOldReEmpEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldReEmpEffFrom[i]);
				}
				if (!"".equals(lStrOldReEmpEffTo[i]) && lStrOldReEmpEffTo[i] != null) {
					lDtOldReEmpEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldReEmpEffTo[i]);
				}
			}
			String[] lStrNewReEmpEffFrom = StringUtility.getParameterValues("txtOldReEmpEffFrom", request);
			String[] lStrNewReEmpEffTo = StringUtility.getParameterValues("txtOldReEmpEffTo", request);
			Date[] lDtNewReEmpEffFrom = new Date[lStrNewReEmpEffFrom.length];
			Date[] lDtNewReEmpEffTo = new Date[lStrNewReEmpEffTo.length];
			for (int i = 0; i < lStrNewReEmpEffFrom.length; i++) {
				if (!"".equals(lStrNewReEmpEffFrom[i]) && lStrNewReEmpEffFrom[i] != null) {
					lDtNewReEmpEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrNewReEmpEffFrom[i]);
				}
				if (!"".equals(lStrNewReEmpEffTo[i]) && lStrNewReEmpEffTo[i] != null) {
					lDtNewReEmpEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrNewReEmpEffTo[i]);
				}
			}
			
			/**Other Allowance input */
			String[] lStrAllowanceType = StringUtility.getParameterValues("cmbAllowanceType", request);
			String[] lStrOldAllowanceAmt = StringUtility.getParameterValues("txtOldAllowanceAmt", request);
			String[] lStrNewAllowanceAmt = StringUtility.getParameterValues("txtNewAllowanceAmt", request);
			String[] lStrAllowanceEffFrom = StringUtility.getParameterValues("txtAllowanceEffFrom", request);
			String[] lStrAllowanceEffTo = StringUtility.getParameterValues("txtAllowanceEffTo", request);
			Date[] lDtAllowanceEffFrom = new Date[lStrAllowanceEffFrom.length];
			Date[] lDtAllowanceEffTo = new Date[lStrAllowanceEffTo.length];
			Double[] lDbOldAllowanceAmt = new Double[lStrOldAllowanceAmt.length];
			Double[] lDbNewAllowanceAmt = new Double[lStrNewAllowanceAmt.length];
			for (int i = 0; i < lStrOldAllowanceAmt.length; i++) {
				if (!"".equals(lStrOldAllowanceAmt[i]) && lStrOldAllowanceAmt[i] != null && !"".equals(lStrNewAllowanceAmt[i]) && lStrNewAllowanceAmt[i] != null) {
					lDbOldAllowanceAmt[i] = Double.parseDouble(lStrOldAllowanceAmt[i]);
					lDbNewAllowanceAmt[i] = Double.parseDouble(lStrNewAllowanceAmt[i]);
					if (!"".equals(lStrAllowanceEffFrom[i]) && lStrAllowanceEffFrom[i] != null) {
						lDtAllowanceEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrAllowanceEffFrom[i]);
					}
					if (!"".equals(lStrAllowanceEffTo[i]) && lStrAllowanceEffTo[i] != null) {
						lDtAllowanceEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrAllowanceEffTo[i]);
					}
				}
			}
			Integer lIntPeonAllowCnt = 0;
			Integer lIntMedicalAllowCnt = 0;
			Integer lIntGallantryCnt = 0;
			Integer lIntOtherAllowCnt = 0;
			
			for(int lIntCnt = 0; lIntCnt < lStrAllowanceType.length; lIntCnt++)
			{
				if(!"".equals(lStrAllowanceType[lIntCnt]) && lStrAllowanceType[lIntCnt] != null)
				{
					if(gObjRsrcBndle.getString("ALLOWANCE.PEON").equalsIgnoreCase(lStrAllowanceType[lIntCnt]))
					{
						lIntPeonAllowCnt++;
					}
					else if(gObjRsrcBndle.getString("ALLOWANCE.MEDICAL").equalsIgnoreCase(lStrAllowanceType[lIntCnt]))
					{
						lIntMedicalAllowCnt++;
					}
					else if(gObjRsrcBndle.getString("ALLOWANCE.GALLANTRY").equalsIgnoreCase(lStrAllowanceType[lIntCnt]))
					{
						lIntGallantryCnt++;
					}
					else if(gObjRsrcBndle.getString("ALLOWANCE.OTHER").equalsIgnoreCase(lStrAllowanceType[lIntCnt]))
					{
						lIntOtherAllowCnt++;
					}
				}
			}
			Date[] lDtPeonAllowEffFrom = new Date[lIntPeonAllowCnt];
			Date[] lDtPeonAllowEffTo = new Date[lIntPeonAllowCnt];
			Double[] lDbOldPeonAllowAmt = new Double[lIntPeonAllowCnt];
			Double[] lDbNewPeonAllowAmt = new Double[lIntPeonAllowCnt];
			
			Date[] lDtMedicalAllowEffFrom = new Date[lIntMedicalAllowCnt];
			Date[] lDtMedicalAllowEffTo = new Date[lIntMedicalAllowCnt];
			Double[] lDbOldMedicalAllowAmt = new Double[lIntMedicalAllowCnt];
			Double[] lDbNewMedicalAllowAmt = new Double[lIntMedicalAllowCnt];
			
			Date[] lDtGallantryAllowEffFrom = new Date[lIntGallantryCnt];
			Date[] lDtGallantryAllowEffTo = new Date[lIntGallantryCnt];
			Double[] lDbOldGallantryAllowAmt = new Double[lIntGallantryCnt];
			Double[] lDbNewGallantryAllowAmt = new Double[lIntGallantryCnt];
			
			Date[] lDtOtherAllowEffFrom = new Date[lIntOtherAllowCnt];
			Date[] lDtOtherAllowEffTo = new Date[lIntOtherAllowCnt];
			Double[] lDbOldOtherAllowAmt = new Double[lIntOtherAllowCnt];
			Double[] lDbNewOtherAllowAmt = new Double[lIntOtherAllowCnt];
			
			Integer lIntPeonCnt = 0;
			Integer lIntMedicalCnt=0;
			Integer lIntGalltryCnt=0;
			Integer lIntOtherCnt=0;
			for(int lIntCnt = 0; lIntCnt < lStrAllowanceType.length; lIntCnt++)
			{
				if(!"".equals(lStrAllowanceType[lIntCnt]) && lStrAllowanceType[lIntCnt] != null)
				{
					if(gObjRsrcBndle.getString("ALLOWANCE.PEON").equalsIgnoreCase(lStrAllowanceType[lIntCnt]))
					{
						lDbOldPeonAllowAmt[lIntPeonCnt]=lDbOldAllowanceAmt[lIntCnt];
						lDbNewPeonAllowAmt[lIntPeonCnt]=lDbNewAllowanceAmt[lIntCnt];
						lDtPeonAllowEffFrom[lIntPeonCnt]=lDtAllowanceEffFrom[lIntCnt];
						lDtPeonAllowEffTo[lIntPeonCnt]=lDtAllowanceEffTo[lIntCnt];
						lIntPeonCnt++;
					}
					else if(gObjRsrcBndle.getString("ALLOWANCE.MEDICAL").equalsIgnoreCase(lStrAllowanceType[lIntCnt]))
					{
						lDbOldMedicalAllowAmt[lIntMedicalCnt]=lDbOldAllowanceAmt[lIntCnt];
						lDbNewMedicalAllowAmt[lIntMedicalCnt]=lDbNewAllowanceAmt[lIntCnt];
						lDtMedicalAllowEffFrom[lIntMedicalCnt]=lDtAllowanceEffFrom[lIntCnt];
						lDtMedicalAllowEffTo[lIntMedicalCnt]=lDtAllowanceEffTo[lIntCnt];
						lIntMedicalCnt++;
					}
					else if(gObjRsrcBndle.getString("ALLOWANCE.GALLANTRY").equalsIgnoreCase(lStrAllowanceType[lIntCnt]))
					{
						lDbOldGallantryAllowAmt[lIntGalltryCnt]=lDbOldAllowanceAmt[lIntCnt];
						lDbNewGallantryAllowAmt[lIntGalltryCnt]=lDbNewAllowanceAmt[lIntCnt];
						lDtGallantryAllowEffFrom[lIntGalltryCnt]=lDtAllowanceEffFrom[lIntCnt];
						lDtGallantryAllowEffTo[lIntGalltryCnt]=lDtAllowanceEffTo[lIntCnt];
						lIntGalltryCnt++;
					}
					else if(gObjRsrcBndle.getString("ALLOWANCE.OTHER").equalsIgnoreCase(lStrAllowanceType[lIntCnt]))
					{
						lDbOldOtherAllowAmt[lIntOtherCnt]=lDbOldAllowanceAmt[lIntCnt];
						lDbNewOtherAllowAmt[lIntOtherCnt]=lDbNewAllowanceAmt[lIntCnt];
						lDtOtherAllowEffFrom[lIntOtherCnt]=lDtAllowanceEffFrom[lIntCnt];
						lDtOtherAllowEffTo[lIntOtherCnt]=lDtAllowanceEffTo[lIntCnt];
						lIntOtherCnt++;
					}
				}
			}
			Date lDtFromDate=IFMSCommonServiceImpl.getDateFromString(lStrNewBasicEffFrom[0]);
			Date lDtToDate=IFMSCommonServiceImpl.getDateFromString(lStrOldBasicEffTo[lStrOldBasicEffTo.length-1]);
			
			
			
			/** Input from jsp ends */
			
			/**get DA and IR rates */
			List<String> lLstRevType=new ArrayList<String>();
			for(int lIntCnt=0;lIntCnt<lStrRevType.length;lIntCnt++)
			{
				if("Old To Old".equals(lStrRevType[lIntCnt]))
				{
					if(!lLstRevType.contains("DA_1986"))
						lLstRevType.add("DA_1986");
				}
				else if("Old To New".equals(lStrRevType[lIntCnt]))
				{
					if(!lLstRevType.contains("DA_1986"))
						lLstRevType.add("DA_1986");
//					if(!lLstRevType.contains("DA_1996"))
//						lLstRevType.add("DA_1996");
					if(!lLstRevType.contains("DA_1996_DP"))
						lLstRevType.add("DA_1996_DP");
				}
				else if("New To New".equals(lStrRevType[lIntCnt]))
				{
//					if(!lLstRevType.contains("DA_1996"))
//						lLstRevType.add("DA_1996");
					if(!lLstRevType.contains("DA_1996_DP"))
						lLstRevType.add("DA_1996_DP");
				}
				else if("New To 2006".equals(lStrRevType[lIntCnt]))
				{
//					if(!lLstRevType.contains("DA_1996"))
//						lLstRevType.add("DA_1996");
					if(!lLstRevType.contains("DA_1996_DP"))
					    lLstRevType.add("DA_1996_DP");
					if(!lLstRevType.contains("DA_2006"))
						lLstRevType.add("DA_2006");
				}
				else if("2006 To 2006".equals(lStrRevType[lIntCnt]))
				{
					if(!lLstRevType.contains("DA_2006"))
						lLstRevType.add("DA_2006");
				}
			}
			List<RltPensionHeadcodeRate> lLstDA1996HeadCodeRate = new ArrayList<RltPensionHeadcodeRate>();
			List<RltPensionHeadcodeRate> lLstDA1996DPHeadCodeRate = new ArrayList<RltPensionHeadcodeRate>();
			List<RltPensionHeadcodeRate> lLstDA2006HeadCodeRate = new ArrayList<RltPensionHeadcodeRate>();
			List<String> lLstIRApplicable = new ArrayList<String>();
			lLstIRApplicable.add("IR2");
			lLstIRApplicable.add("IR3");

			List<RltPensionHeadcodeRate> lLstIRHeadCodeRate = new ArrayList<RltPensionHeadcodeRate>();
				
			if(lLstRevType.contains("DA_1986"))
			{
				lLstIRHeadCodeRate=lPensionBillDAO.getRateLstFromHeadcode(lLngHeadCode, lLstIRApplicable);
				lLstRltPensionHeadcodeRate.add(lLstIRHeadCodeRate);
			}
					
			List<RltPensionHeadcodeRate> lLstDA1986HeadCodeRate = new ArrayList<RltPensionHeadcodeRate>();
//			List<Double> lLstUptoBasic=new ArrayList<Double>();
//			if(lLstRevType.contains("DA_1986"))
//			{
//				for(int lIntCnt=0;lIntCnt<lStrOldBasic.length;lIntCnt++)
//				{
//					if (Double.valueOf(lStrOldBasic[lIntCnt]) <= 1750) {
//						if(!lLstUptoBasic.contains(1750D))
//							lLstUptoBasic.add(1750D);
//					} else if (Double.valueOf(lStrOldBasic[lIntCnt]) <= 3000) {
//						if(!lLstUptoBasic.contains(3000D))
//							lLstUptoBasic.add(3000D);
//					} else {
//						if(!lLstUptoBasic.contains(4000D))
//							lLstUptoBasic.add(4000D);
//						
//					}
//				}
//				for(int lIntCnt=0;lIntCnt<lStrNewBasic.length;lIntCnt++)
//				{
//					if (Double.valueOf(lStrNewBasic[lIntCnt]) <= 1750) {
//						if(!lLstUptoBasic.contains(1750D))
//							lLstUptoBasic.add(1750D);
//					} else if (Double.valueOf(lStrNewBasic[lIntCnt]) <= 3000) {
//						if(!lLstUptoBasic.contains(3000D))
//							lLstUptoBasic.add(3000D);
//					} else {
//						if(!lLstUptoBasic.contains(4000D))
//							lLstUptoBasic.add(4000D);
//						
//					}
//				}
//			}
		
			if(lLstRevType.contains("DA_1986"))
			{
//				for(int lIntCnt=0;lIntCnt<lLstUptoBasic.size();lIntCnt++)
//				{
					lLstDA1986HeadCodeRate=lObjRevisedArrearCalcDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_1986", 0D, lDtFromDate,lDtToDate);
					lLstRltPensionHeadcodeRate.add(lLstDA1986HeadCodeRate);
//				}
			}
//			if(lLstRevType.contains("DA_1996"))
//				lLstDA1996HeadCodeRate=lObjRevisedArrearCalcDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_1996", 0D, lDtFromDate,lDtToDate);
			if(lLstRevType.contains("DA_1996_DP"))
				lLstDA1996DPHeadCodeRate=lObjRevisedArrearCalcDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_1996_DP", 0D, lDtFromDate,lDtToDate);
			if(lLstRevType.contains("DA_2006"))
				lLstDA2006HeadCodeRate=lObjRevisedArrearCalcDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_2006", 0D, lDtFromDate,lDtToDate);
			
     		
			
			lLstRltPensionHeadcodeRate.add(lLstDA1996HeadCodeRate);
			lLstRltPensionHeadcodeRate.add(lLstDA1996DPHeadCodeRate);
			lLstRltPensionHeadcodeRate.add(lLstDA2006HeadCodeRate);
			/**get DA and IR rates ends*/

			StringBuilder sHeadingCol = new StringBuilder();
			StringBuilder sHeading = new StringBuilder();
			/** Header generation starts */
			
			if (!isSave) {
				sHeading.append("\r\n");
				//sHeading.append("Pension Due Drawn Statement of  " +lStrPnsnrName+"                                                                         Page No : # \n");
				sHeading.append("Pension Due Drawn Statement of  ");
				sHeading.append(String.format("%-87s",lStrPnsnrName));
				sHeading.append(String.format("%12s","Page No : # \n"));
				sHeading.append("\r\n");
				//sHeading.append("PPO No.  "+lStrppoNo+"                                                                               Print Date :  "+lStrCurrDt);
				sHeading.append("PPO No. ");
				sHeading.append(String.format("%-91s", lStrppoNo));
				sHeading.append(String.format("%32s","Print Date : "+lStrCurrDt));
				sHeading.append("\r\n\r\n");
				sHeading.append("Bank Name : "+lStrBankName+"  "+"Branch Name : "+lStrBranchName+"  "+"Account No. "+lStrAccountNo);
				sHeading.append("\r\n\r\n");
				//sHeading.append("Arrears From : " + IFMSCommonServiceImpl.getStringFromDate(lDtMinDate) + "  To  " + IFMSCommonServiceImpl.getStringFromDate(lDtMaxDate)+"                                                                       All Amounts in Rs.");
				sHeading.append("Arrears From : ");
				sHeading.append(String.format("%-98s", IFMSCommonServiceImpl.getStringFromDate(lDtMinDate) + "  To  " + IFMSCommonServiceImpl.getStringFromDate(lDtMaxDate)));
				sHeading.append(String.format("%18s", "All Amounts in Rs."));
				sHeading.append("\r\n");
				sHeading.append("\r\n");
				sHeading.append(getChar(70, "-"));
				sHeading.append("\r\n");
				sHeading.append("DA Type      | Basic Upto | Percentage/ |   From Date  |     To Date");
				sHeading.append("\r\n");
				sHeading.append("             |            |  Amount     |              |");
				sHeading.append("\r\n");
//				sHeading.append(String.format("%-12s","DA Type"));
//				sHeading.append(String.format("%2s","|"));
//				sHeading.append(String.format("%5s","Basic Upto"));
//				sHeading.append(String.format("%2s","|"));
//				sHeading.append(String.format("%11s","Percentage/"));
//				sHeading.append(String.format("%2s","|"));
//				sHeading.append(String.format("%12s","From Date"));
//				sHeading.append(String.format("%2s","|"));
//				sHeading.append(String.format("%15s","To Date"));
//				sHeading.append("\r\n");
//				sHeading.append(String.format("%42s","Amount"));
//				sHeading.append("\r\n");
				sHeading.append(getChar(70, "-"));
				sHeading.append("\r\n");
				for(List<RltPensionHeadcodeRate> lLstRltHeadCodeRate:lLstRltPensionHeadcodeRate)
				{
					for(RltPensionHeadcodeRate lObjRltPensionHeadcodeRate:lLstRltHeadCodeRate)
					{
						
						sHeading.append(String.format("%-12s",lObjRltPensionHeadcodeRate.getFieldType()));
						sHeading.append(String.format("%2s","|"));
						sHeading.append(String.format("%11s",("DA_1986".equals(lObjRltPensionHeadcodeRate.getFieldType())?lObjRltPensionHeadcodeRate.getUptoBasic():"")));
						sHeading.append(String.format("%2s","|"));
						sHeading.append(String.format("%12s",lObjRltPensionHeadcodeRate.getRate()));
						sHeading.append(String.format("%2s","|"));
						sHeading.append(String.format("%12s",lObjSmplDtFormat.format(lObjRltPensionHeadcodeRate.getEffectiveFromDate())));
						sHeading.append(String.format("%3s","|"));
						if("IR2".equalsIgnoreCase(lObjRltPensionHeadcodeRate.getFieldType()) || "IR3".equalsIgnoreCase(lObjRltPensionHeadcodeRate.getFieldType()))
						{
							sHeading.append(String.format("%13s",IFMSCommonServiceImpl.getStringFromDate(lDtMaxDate)));
						}
						else
						{
							sHeading.append(String.format("%13s",(lObjRltPensionHeadcodeRate.getEffectiveToDate()==null? IFMSCommonServiceImpl.getStringFromDate(lDtMaxDate):lObjSmplDtFormat.format(lObjRltPensionHeadcodeRate.getEffectiveToDate()))));
						}
						
						sHeading.append("\r\n");
						lIntRowNo++;
					}
				}
				sHeading.append("\r\n");
			}
			sHeadingCol.append("\r\n");
			sHeadingCol.append(getChar(132, "-"));
			sHeadingCol.append("\r\n");
			sHeadingCol.append("                       Amount Due                              |                        Amount Drawn");
			sHeadingCol.append("\r\n");
			sHeadingCol.append(getChar(132, "-"));
			sHeadingCol.append("\r\n");
			
			if (!"-1".equals(lStrRevType[0])) {
				if (!isSave) {
					sHeadingCol.append(" Period   Pension/   ADP   DP    IR1/    DA   CVP New   Total  | Pension/    ADP    DP    IR1/     DA   CVP Old   Total |Difference");
					sHeadingCol.append("\r\n");
					sHeadingCol.append("         Punish Cut              IR2/                   Due    | Punish Cut               IR2/                    Drawn |           ");
					sHeadingCol.append("\r\n");
					sHeadingCol.append("            /PA      MA    GA    IR3    Other                  |   /PA       MA     GA    IR3     Other                 |");
					sHeadingCol.append("\r\n");
					sHeadingCol.append(getChar(132, "-"));
					sHeadingCol.append("\r\n");
					sbLineDisplay.append("<div><pre>");
					sbLineDisplay.append("\n" + sHeading.toString().replace("#", "" + lIntPageNo));
					sbLineDisplay.append(sHeadingCol);
				}

				/** Header generation ends */

				Long[] lLngTempAmount = new Long[29];

				String lStrMonthYear = null;
				boolean isLastEndLoop = false;
				List lLstAdpAmount = new ArrayList();
				int lIntMonth = 0;
				boolean isNextMonthYearSame = false;
				for (int masterCtr = 0; masterCtr < lStrRevType.length; masterCtr++) {
					
					if (!"".equals(lStrOldBasicEffFrom[masterCtr]) && lStrOldBasicEffFrom[masterCtr] != null) {
						lDtOldBasicEffFrom[masterCtr] = IFMSCommonServiceImpl.getDateFromString(lStrOldBasicEffFrom[masterCtr]);
						lIntOldFromMonth[masterCtr] = Integer.parseInt(lStrOldBasicEffFrom[masterCtr].substring(3, 5));
						lIntOldFromYear[masterCtr] = Integer.parseInt(lStrOldBasicEffFrom[masterCtr].substring(6));
						lIntOldStartDate[masterCtr] = Integer.parseInt(lStrOldBasicEffFrom[masterCtr].substring(0, 2));
					}
					if (!"".equals(lStrOldBasicEffTo[masterCtr]) && lStrOldBasicEffTo[masterCtr] != null) {
						lDtOldBasicEffTo[masterCtr] = IFMSCommonServiceImpl.getDateFromString(lStrOldBasicEffTo[masterCtr]);
						lIntOldToMonth[masterCtr] = Integer.parseInt(lStrOldBasicEffTo[masterCtr].substring(3, 5));
						lIntOldToYear[masterCtr] = Integer.parseInt(lStrOldBasicEffTo[masterCtr].substring(6));
						lIntOldEndMonthDays[masterCtr] = Integer.parseInt(lStrOldBasicEffTo[masterCtr].substring(0, 2));
					}

					if (lDtOldBasicEffFrom.length > (masterCtr + 1)) {
						lDtOldBasicEffFrom[masterCtr + 1] = IFMSCommonServiceImpl.getDateFromString(lStrOldBasicEffFrom[masterCtr + 1]);
						lIntOldFromMonth[masterCtr + 1] = Integer.parseInt(lStrOldBasicEffFrom[masterCtr + 1].substring(3, 5));
						lIntOldFromYear[masterCtr + 1] = Integer.parseInt(lStrOldBasicEffFrom[masterCtr + 1].substring(6));
						lIntOldStartDate[masterCtr + 1] = Integer.parseInt(lStrOldBasicEffFrom[masterCtr + 1].substring(0, 2));

						if (lIntOldToMonth[masterCtr] == lIntOldFromMonth[masterCtr + 1] && lIntOldToYear[masterCtr] == lIntOldFromYear[masterCtr + 1]) {
							isNextMonthYearSame = true;
						} else {
							isNextMonthYearSame = false;
						}
					}

					int lIntFromYear = lIntOldFromYear[masterCtr];
					int lIntToYear = lIntOldToYear[masterCtr];
					int lIntFromMonth = lIntOldFromMonth[masterCtr];
					int lIntToMonth = lIntOldToMonth[masterCtr];

					if (masterCtr == lStrRevType.length - 1) {
						isLastEndLoop = true;
					}
					boolean isMonthYearSame = false;
					for (int yearCtr = lIntFromYear; yearCtr <= lIntToYear; yearCtr++) {
						for (int monthCtr = 1; monthCtr <= 12; monthCtr++) {
							lLngPunishAmtNew = Long.valueOf(0);
							lLngPunishAmtOld = Long.valueOf(0);
							lLngADPAmountNew = Long.valueOf(0);
							lLngADPAmountOld = Long.valueOf(0);
							lLngDPAmountNew = Long.valueOf(0);
							lLngDPAmountOld = Long.valueOf(0);
							lLngTIAmountNew = Long.valueOf(0);
							lLngTIAmountOld = Long.valueOf(0);
							lLngIRAmountNew[0] = Long.valueOf(0);
							lLngIRAmountNew[1] = Long.valueOf(0);
							lLngIRAmountNew[2] = Long.valueOf(0);
							lLngIRAmountOld[0] = Long.valueOf(0);
							lLngIRAmountOld[1] = Long.valueOf(0);
							lLngIRAmountOld[2] = Long.valueOf(0);
							lLngCVPEffAmountNew = Long.valueOf(0);
							lLngCVPEffAmountOld = Long.valueOf(0);
							lLngOldPeonAllow = Long.valueOf(0);
							lLngNewPeonAllow = Long.valueOf(0);
							lLngOldMedicalAllow = Long.valueOf(0);
							lLngNewMedicalAllow = Long.valueOf(0);
							lLngOldGallantryAmt = Long.valueOf(0);
							lLngNewGallantryAmt = Long.valueOf(0);
							lLngOldOtherAllow = Long.valueOf(0);
							lLngNewOtherAllow = Long.valueOf(0);

							isEndMonth = false;

							/**
							 * ############## Date Column logic Starts
							 * ##############
							 */

							if ((yearCtr == lIntFromYear) && monthCtr == 1) // for
							// effective_from
							// date
							{
								monthCtr = lIntFromMonth;
								lDtFromDtUpdated = lDtOldBasicEffFrom[masterCtr];

								lObjCalendar = Calendar.getInstance();

								if ((yearCtr == lIntToYear) && (monthCtr == lIntToMonth)) {
									lDtToDtUpdated = lDtOldBasicEffTo[masterCtr];
								} else {
									lObjCalendar.setTime(lDtFromDtUpdated);
									lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
									lDtToDtUpdated = lObjCalendar.getTime();
								}
							}

							lIntMonth = lObjCalendar.get(lObjCalendar.MONTH) + 1;
							String lStrYYYYMM = lObjCalendar.get(lObjCalendar.YEAR) + "" + (lIntMonth < 10 ? "0" + lIntMonth : lIntMonth);

							if (yearCtr == lIntToYear && monthCtr == lIntToMonth) {
								isEndMonth = true;
							}

							if ((lStrArrMonth[monthCtr - 1] + "-" + yearCtr).equals(lStrMonthYear)) {
								isMonthYearSame = true;
							}

							lStrMonthYear = lStrArrMonth[monthCtr - 1] + "-" + yearCtr;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append("\r\n");
							}

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%-8s", lStrArrMonth[monthCtr - 1] + "-" + yearCtr));
							}

							/**
							 * ############## Date Column logic Ends
							 * ##############
							 */

							

							long lLngEffDaysinMonth = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtNewBasicEffFrom[masterCtr], lDtNewBasicEffTo[masterCtr], lDbNewBasic[newBasicCntr],lDbNewPunishCutAmt, true);

							lObjCalendar.setTime(lDtFromDtUpdated);
							int lIntTotalDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

							/**
							 * ############## Calculation of Punishment Cut Starts
							 * ##############
							 */
							if(lDtPunishEffFrom.length > punishCutCntr && lDtPunishEffFrom[punishCutCntr] != null)
    						{
								lDbNewPunishCutAmt = (double) calcPnsnCut(inputMap, lDtFromDtUpdated, lDtToDtUpdated, lDtPunishEffFrom, 
										lDtPunishEffTo, lDbPunishAmt, punishCutCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
    						}
							lDbOldPunishCutAmt = lDbNewPunishCutAmt;
							lLngPunishAmtNew = lDbNewPunishCutAmt.longValue();
							lLngPunishAmtOld = lDbOldPunishCutAmt.longValue();
							if (!isMonthYearSame) {
								lLngTempAmount[27] = lLngPunishAmtNew;
							} else {
								lLngTempAmount[27] = lLngTempAmount[27] + lLngPunishAmtNew;
							}
							lLngTotalPunishNew += lLngPunishAmtNew;
							lLngPageTotalNewPunish += lLngPunishAmtNew;
							
							if (!isMonthYearSame) {
								lLngTempAmount[28] = lLngPunishAmtOld;
							} else {
								lLngTempAmount[28] = lLngTempAmount[28] + lLngPunishAmtOld;
							}
							lLngTotalPunishOld += lLngPunishAmtOld;
							lLngPageTotalOldPunish += lLngPunishAmtOld;
							
//    		    			if(lDtOldPnsnCutEffFrom.length > oldPnsnCutCntr && lDtOldPnsnCutEffFrom[oldPnsnCutCntr] != null)
//    						{
//    							lDbOldPnsnCutAmt = calcPnsnCut(inputMap, lDtFromDtUpdated, lDtToDtUpdated, lDtOldPnsnCutEffFrom, 
//    									lDtOldPnsnCutEffTo, lDbOldPnsnCut, oldPnsnCutCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
//    						}
							
							/**
							 * ############## Calculation of Punishment Cut Ends
							 * ##############
							 */

							/**
							 * ############## For calulation of Basic For Start
							 * and End Date Starts ##############
							 */

							lDbNewBasicUpdated = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtNewBasicEffFrom[masterCtr], lDtNewBasicEffTo[masterCtr], lDbNewBasic[newBasicCntr], lDbNewPunishCutAmt, false);

							lDbOldBasicUpdated = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtOldBasicEffFrom[masterCtr], lDtOldBasicEffTo[masterCtr], lDbOldBasic[newBasicCntr], lDbOldPunishCutAmt,false);

							/**
							 * ############## For calulation of Basic For Start
							 * and End Date Ends ##############
							 */

							/**
							 * ################ New Basic Start
							 * ####################
							 */

							
							/**
							 * ################ Revised Pension Start
							 * ####################
							 */

							if (!isMonthYearSame) {
								lLngTempAmount[0] = Math.round(lDbNewBasicUpdated);
							} else {
								lLngTempAmount[0] = lLngTempAmount[0] + Math.round(lDbNewBasicUpdated);
							}
							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%9s", lLngTempAmount[0]));
							}
							lDbNewBasicTotal += lDbNewBasicUpdated;
							lLngPageTotalNewBasic = lLngPageTotalNewBasic + lDbNewBasicUpdated;

							/**
							 * ################ Revised Pension End
							 * ####################
							 */

							/**
							 * ################ DA/IR/ADP Start
							 * ####################
							 */

					
							if ("Old To New".equals(lStrRevType[revTypeCntr]) || "New To New".equals(lStrRevType[revTypeCntr])) {
//								if("Yes".equals(lStrNewDP[newDPCntr]))
//								{
								if (lDtPnsnrDOR != null && (lDtPnsnrDOR.before(IFMSCommonServiceImpl.getDateFromString("31/07/2004")) 
										|| lDtPnsnrDOR.equals(IFMSCommonServiceImpl.getDateFromString("31/07/2004"))) 
										&& lDtFromDtUpdated.after(IFMSCommonServiceImpl.getDateFromString("31/07/2004")))
								{
									lLngDPAmountNew = calcDP(inputMap, lDtFromDtUpdated, lLngHeadCode, lDbNewBasicUpdated - lLngPunishAmtNew, lDbNewBasic, newBasicCntr, 
															false, lLngEffDaysinMonth, lIntTotalDaysInMonth);
								}
//								}

							} else if ("New To 2006".equals(lStrRevType[revTypeCntr]) || "2006 To 2006".equals(lStrRevType[revTypeCntr])) {
								
								ValidPcodeView lObjValidPcodeVO = new ValidPcodeView();
								lObjValidPcodeVO.setPensionerCode(lStrPensionerCode);
								lObjValidPcodeVO.setDateOfBirth(lDtPnsnrDOB);
								lObjValidPcodeVO.setDateOfDeath(lDtPnsnrDOD);
								if (lStrEndDate != null && !"".equals(lStrEndDate)) {
									lObjValidPcodeVO.setEndDate(lDtEndDate);
								}
								if (lStrCommensionDate != null && !"".equals(lStrCommensionDate)) {
									lObjValidPcodeVO.setCommensionDate(lDtCommensionDate);
								}
						      
								Date lMonthStartDt = lDtFromDtUpdated;
								Date lMonthEndDate = lDtToDtUpdated;
							    
								inputMap.put("lObjValidPcode", lObjValidPcodeVO);
								inputMap.put("BasicPensionAmt", lDbNewBasicUpdated - lLngPunishAmtNew);
								inputMap.put("lMonthStartDt", lMonthStartDt);
								inputMap.put("lMonthEndDt", lMonthEndDate);
								
								//lLngADPAmountNew = (long) calcADPAmount(inputMap);
								
							} else if ("Old To Old".equals(lStrRevType[revTypeCntr])) {
								if (lDtFromDtUpdated.after(IFMSCommonServiceImpl.getDateFromString("31/03/1995"))) {
									lLngIRAmountNew = calcIR(inputMap, lStrRevType, revTypeCntr, lDtFromDtUpdated, lLngHeadCode, lDbNewBasicUpdated - lLngPunishAmtNew, lDbNewBasic, newBasicCntr, lLngEffDaysinMonth,
												lIntTotalDaysInMonth);
								}
							}

							if (!isMonthYearSame) {
								lLngTempAmount[1] = lLngADPAmountNew;
							} else {
								lLngTempAmount[1] = lLngTempAmount[1] + lLngADPAmountNew;
							}
							lLngTotalADPNew += lLngADPAmountNew;
							lLngPageTotalNewADP += lLngADPAmountNew;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%7s", lLngTempAmount[1]));
							}
							if (!isMonthYearSame) {
								lLngTempAmount[2] = lLngDPAmountNew;
							} else {
								lLngTempAmount[2] = lLngTempAmount[2] + lLngDPAmountNew;
							}
							lLngTotalDPNew += lLngDPAmountNew;
							lLngPageTotalNewDP += lLngDPAmountNew;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%5s", lLngTempAmount[2]));
							}

							if (!isMonthYearSame) {
								lLngTempAmount[3] = lLngIRAmountNew[0];
								lLngTempAmount[4] = lLngIRAmountNew[1];
								lLngTempAmount[5] = lLngIRAmountNew[2];
							} else {
								lLngTempAmount[3] = lLngTempAmount[3] + lLngIRAmountNew[0];
								lLngTempAmount[4] = lLngTempAmount[4] + lLngIRAmountNew[1];
								lLngTempAmount[5] = lLngTempAmount[5] + lLngIRAmountNew[2];
							}
							lLngTotalIR1New += lLngIRAmountNew[0]; 
							lLngTotalIR2New += lLngIRAmountNew[1];
							lLngTotalIR3New += lLngIRAmountNew[2];
							
							lLngPageTotalNewIR1 += lLngIRAmountNew[0]; 
							lLngPageTotalNewIR2 += lLngIRAmountNew[1];
							lLngPageTotalNewIR3 += lLngIRAmountNew[2];
							
							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%7s", lLngTempAmount[3]));
								
							}
							
							/** ################ Other Allowance Start #################### */
							
							lLngNewPeonAllow = calcAllowance(lDtFromDtUpdated, lDtToDtUpdated, lDtPeonAllowEffFrom, lDtPeonAllowEffTo, lDbNewPeonAllowAmt, peonAllowCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
							if (!isMonthYearSame) {
								lLngTempAmount[19] = lLngNewPeonAllow;
							} else {
								lLngTempAmount[19] = lLngTempAmount[19] + lLngNewPeonAllow;
							}
							lLngTotalPeonAllowNew += lLngNewPeonAllow;
							lLngPageTotalNewPeonAllow += lLngNewPeonAllow;
							
//							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
//								sbLineDisplay.append(String.format("%7s", lLngTempAmount[19]));
//							}
							
							lLngNewMedicalAllow = calcAllowance(lDtFromDtUpdated, lDtToDtUpdated, lDtMedicalAllowEffFrom, lDtMedicalAllowEffTo, lDbNewMedicalAllowAmt, medicalAllowCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
							if (!isMonthYearSame) {
								lLngTempAmount[20] = lLngNewMedicalAllow;
							} else {
								lLngTempAmount[20] = lLngTempAmount[20] + lLngNewMedicalAllow;
							}
							lLngTotalMedicalAllowNew += lLngNewMedicalAllow;
							lLngPageTotalNewMedicalAllow += lLngNewMedicalAllow;
							
//							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
//								sbLineDisplay.append(String.format("%7s", lLngTempAmount[20]));
//							}
							
							lLngNewGallantryAmt = calcAllowance(lDtFromDtUpdated, lDtToDtUpdated, lDtGallantryAllowEffFrom, lDtGallantryAllowEffTo, lDbNewGallantryAllowAmt, gallantryCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
							if (!isMonthYearSame) {
								lLngTempAmount[21] = lLngNewGallantryAmt;
							} else {
								lLngTempAmount[21] = lLngTempAmount[21] + lLngNewGallantryAmt;
							}
							lLngTotalGallantryAmtNew += lLngNewGallantryAmt;
							lLngPageTotalNewGallantryAmt += lLngNewGallantryAmt;
							
//							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
//								sbLineDisplay.append(String.format("%7s", lLngTempAmount[21]));
//							}
							
							lLngNewOtherAllow = calcAllowance(lDtFromDtUpdated, lDtToDtUpdated, lDtOtherAllowEffFrom, lDtOtherAllowEffTo, lDbNewOtherAllowAmt, otherAllowCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
							if (!isMonthYearSame) {
								lLngTempAmount[22] = lLngNewOtherAllow;
							} else {
								lLngTempAmount[22] = lLngTempAmount[22] + lLngNewOtherAllow;
							}
							lLngTotalOtherAllowNew += lLngNewOtherAllow;
							lLngPageTotalNewOtherAllow += lLngNewOtherAllow;
							
//							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
//								sbLineDisplay.append(String.format("%7s", lLngTempAmount[22]));
//							}
							/** ################ Other Allowance End #################### */
							
							/** ################ DA End #################### */

							/** ################ TI Start #################### */

							if (!"New To 2006".equals(lStrRevType[revTypeCntr]) && !"2006 To 2006".equals(lStrRevType[revTypeCntr])) {
//								if("Yes".equals(lStrNewDP[newDPCntr]))
//								{
								if (lDtPnsnrDOR != null && (lDtPnsnrDOR.before(IFMSCommonServiceImpl.getDateFromString("31/07/2004")) 
										|| lDtPnsnrDOR.equals(IFMSCommonServiceImpl.getDateFromString("31/07/2004"))) 
										&& lDtFromDtUpdated.after(IFMSCommonServiceImpl.getDateFromString("31/07/2004")))
								{
									lLngDPYesDaysNew = calcDP(inputMap, lDtFromDtUpdated, lLngHeadCode, lDbNewBasicUpdated - lLngPunishAmtNew, lDbNewBasic, newBasicCntr, 
															true, lLngEffDaysinMonth, lIntTotalDaysInMonth);
									
								}
//								}
								lLngTIAmountNew= calcDA(lStrRevType, revTypeCntr, lDtFromDtUpdated, lDtToDtUpdated, lLngHeadCode,  lLngDPAmountNew, lDtNewReEmpEffFrom, 
										lDtNewReEmpEffTo, newReEmpCntr, lDbNewBasicUpdated - lLngPunishAmtNew, newBasicCntr, "New", lLngDPYesDaysNew, lLngEffDaysinMonth,
										lIntTotalDaysInMonth,lStrDAInPnsnSal);
								
							} else if ("New To 2006".equals(lStrRevType[revTypeCntr]) || "2006 To 2006".equals(lStrRevType[revTypeCntr])) {
								
								lLngTIAmountNew= calcDA(lStrRevType, revTypeCntr, lDtFromDtUpdated, lDtToDtUpdated, lLngHeadCode,  lLngADPAmountNew, lDtNewReEmpEffFrom, 
										lDtNewReEmpEffTo, newReEmpCntr, lDbNewBasicUpdated - lLngPunishAmtNew, newBasicCntr, "New", lLngDPYesDaysNew, lLngEffDaysinMonth,
										lIntTotalDaysInMonth,lStrDAInPnsnSal);
								
//								int lIntEffReEmpDays = getReEmpEffectDays(lDtNewReEmpEffFrom, lDtNewReEmpEffTo, newReEmpCntr, lDtFromDtUpdated, lDtToDtUpdated);
//
//								lLngTIAmountNew = Math.round((lLngTIAmountNew.doubleValue() * (lLngEffDaysinMonth - lIntEffReEmpDays)) / lLngEffDaysinMonth);

							}

							if (!isMonthYearSame) {
								lLngTempAmount[6] = lLngTIAmountNew;
							} else {
								lLngTempAmount[6] = lLngTempAmount[6] + lLngTIAmountNew;
							}
							lLngTotalTINew += lLngTIAmountNew;
							lLngPageTotalNewTI += lLngTIAmountNew;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%7s", lLngTempAmount[6]));
							}

							/** ################ TI End #################### */

							/**
							 * ################ New CVP calc Start
							 * ####################
							 */
							if (lDtNewCVPEffFrom[newCVPCntr] != null) {
								lLngCVPEffAmountNew = calcCVP(inputMap, lDtFromDtUpdated, lDtToDtUpdated, lDtNewCVPEffFrom, lDtNewCVPEffTo, lDbNewCVP, newCVPCntr, lLngEffDaysinMonth,
										lIntTotalDaysInMonth);
							}

							if (!isMonthYearSame) {
								lLngTempAmount[7] = lLngCVPEffAmountNew;
							} else {
								lLngTempAmount[7] = lLngTempAmount[7] + lLngCVPEffAmountNew;
							}
							lLngTotalCVPAmountNew += lLngCVPEffAmountNew;
							lLngPageTotalNewCVP += lLngCVPEffAmountNew;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%10s", lLngTempAmount[7]));
							}

							/**
							 * ################ New CVP calc End
							 * ####################
							 */

							/**
							 * ################ Total Due Start
							 * ####################
							 */

							lLngTotalDue = Math.round(lDbNewBasicUpdated - lLngPunishAmtNew) + lLngTIAmountNew -  lLngCVPEffAmountNew;

							if ("Old To New".equals(lStrRevType[revTypeCntr]) || "New To New".equals(lStrRevType[revTypeCntr])) {
								lLngTotalDue += lLngDPAmountNew;
							} else if ("Old To Old".equals(lStrRevType[revTypeCntr])) {
								lLngTotalDue += lLngIRAmountNew[0] + lLngIRAmountNew[1] + lLngIRAmountNew[2];
							} else if ("2006 To 2006".equals(lStrRevType[revTypeCntr]) || "New To 2006".equals(lStrRevType[revTypeCntr])) {
								lLngTotalDue += lLngADPAmountNew;
							}
							lLngTotalDue += lLngNewPeonAllow +lLngNewMedicalAllow + lLngNewGallantryAmt + lLngNewOtherAllow;
							
							if (!isMonthYearSame) {
								lLngTempAmount[8] = lLngTotalDue;
							} else {
								lLngTempAmount[8] = lLngTempAmount[8] + lLngTotalDue;
							}
							lLngTotalDueSum += lLngTotalDue;
							lLngPageTotalDue += lLngTotalDue;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%8s", lLngTempAmount[8]));
							}
							sbLineDisplay.append(String.format("%3s", "|"));
							/**
							 * ################ Total Due Start
							 * ####################
							 */

							/**
							 * ################ New Basic End
							 * ####################
							 */

							/**
							 * ################ Old Basic Start
							 * ####################
							 */

							/**
							 * ################ Original Pension Start
							 * ####################
							 */

							if (!isMonthYearSame) {
								lLngTempAmount[9] = Math.round(lDbOldBasicUpdated);
							} else {
								lLngTempAmount[9] = lLngTempAmount[9] + Math.round(lDbOldBasicUpdated);
							}
							lDbOldBasicTotal += lDbOldBasicUpdated;
							lLngPageTotalOldBasic = lLngPageTotalOldBasic + lDbOldBasicUpdated;
							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%8s", Math.round(lLngTempAmount[9])));
							}

							/**
							 * ################ Original Pension End
							 * ####################
							 */

							if ("2006 To 2006".equals(lStrRevType[revTypeCntr])) {
								
								ValidPcodeView lObjValidPcodeVO = new ValidPcodeView();
								lObjValidPcodeVO.setPensionerCode(lStrPensionerCode);
								lObjValidPcodeVO.setDateOfBirth(lDtPnsnrDOB);
								lObjValidPcodeVO.setDateOfDeath(lDtPnsnrDOD);
								if (lStrEndDate != null && !"".equals(lStrEndDate)) {
									lObjValidPcodeVO.setEndDate(lDtEndDate);
								}
								if (lStrCommensionDate != null && !"".equals(lStrCommensionDate)) {
									lObjValidPcodeVO.setCommensionDate(lDtCommensionDate);
								}
						    
								Date lMonthStartDt = lDtFromDtUpdated;
								Date lMonthEndDate = lDtToDtUpdated;
							    
								inputMap.put("lObjValidPcode", lObjValidPcodeVO);
								inputMap.put("BasicPensionAmt", lDbOldBasicUpdated - lLngPunishAmtOld);
								inputMap.put("lMonthStartDt", lMonthStartDt);
								inputMap.put("lMonthEndDt", lMonthEndDate);
	
								//lLngADPAmountOld = (long) calcADPAmount(inputMap);

							}

							/** ################ DA Start #################### */

							if ("New To New".equals(lStrRevType[revTypeCntr]) || "New To 2006".equals(lStrRevType[revTypeCntr])) {
//								if("Yes".equals(lStrOldDP[oldDPCntr]))
//								{
								if (lDtPnsnrDOR != null && (lDtPnsnrDOR.before(IFMSCommonServiceImpl.getDateFromString("31/07/2004")) 
										|| lDtPnsnrDOR.equals(IFMSCommonServiceImpl.getDateFromString("31/07/2004"))) 
										&& lDtFromDtUpdated.after(IFMSCommonServiceImpl.getDateFromString("31/07/2004")))
								{
									lLngDPAmountOld = calcDP(inputMap, lDtFromDtUpdated, lLngHeadCode, lDbOldBasicUpdated - lLngPunishAmtOld, lDbOldBasic, oldBasicCntr, 
																false, lLngEffDaysinMonth, lIntTotalDaysInMonth);
									
								}
//								}
							} else if ("Old To Old".equals(lStrRevType[revTypeCntr]) || "Old To New".equals(lStrRevType[revTypeCntr])) {
								if (lDtFromDtUpdated.after(IFMSCommonServiceImpl.getDateFromString("31/03/1995"))) {
									lLngIRAmountOld = calcIR(inputMap, lStrRevType, revTypeCntr, lDtFromDtUpdated, lLngHeadCode, lDbOldBasicUpdated - lLngPunishAmtOld, lDbOldBasic, oldBasicCntr, lLngEffDaysinMonth,
											lIntTotalDaysInMonth);
								}
							} else if ("2006 To 2006".equals(lStrRevType[revTypeCntr])) {
								if (lLstAdpAmount != null && !lLstAdpAmount.isEmpty()) {
									for (int l = 0; l < lLstAdpAmount.size(); l++) {
										lLngADPAmountOld += Math.round((Double) lLstAdpAmount.get(0));
									}
								}
							}

							if (!isMonthYearSame) {
								lLngTempAmount[10] = lLngADPAmountOld;
							} else {
								lLngTempAmount[10] = lLngTempAmount[10] + lLngADPAmountOld;
							}
							lLngTotalADPOld += lLngADPAmountOld;
							lLngPageTotalOldADP += lLngADPAmountOld;
							
							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%8s", lLngTempAmount[10]));
							}

							if (!isMonthYearSame) {
								lLngTempAmount[11] = lLngDPAmountOld;
							} else {
								lLngTempAmount[11] = lLngTempAmount[11] + lLngDPAmountOld;
							}
							lLngTotalDPOld += lLngDPAmountOld;
							lLngPageTotalOldDP += lLngDPAmountOld;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%7s", lLngTempAmount[11]));
							}

							if (!isMonthYearSame) {
								lLngTempAmount[12] = lLngIRAmountOld[0];
								lLngTempAmount[13] = lLngIRAmountOld[1];
								lLngTempAmount[14] = lLngIRAmountOld[2];
							} else {
								lLngTempAmount[12] = lLngTempAmount[12] + lLngIRAmountOld[0];
								lLngTempAmount[13] = lLngTempAmount[13] + lLngIRAmountOld[1];
								lLngTempAmount[14] = lLngTempAmount[14] + lLngIRAmountOld[2];
							}
							lLngTotalIR1Old += lLngIRAmountOld[0];
							lLngTotalIR2Old += lLngIRAmountOld[1];
							lLngTotalIR3Old += lLngIRAmountOld[2];
							
							lLngPageTotalOldIR1 += lLngIRAmountOld[0];
							lLngPageTotalOldIR2 += lLngIRAmountOld[1];
							lLngPageTotalOldIR3 += lLngIRAmountOld[2];

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%7s", lLngTempAmount[12]));
								
							}

							/** ################ DA End #################### */
							
							/** ################ Other Allowance Start #################### */
							
							lLngOldPeonAllow = calcAllowance(lDtFromDtUpdated, lDtToDtUpdated, lDtPeonAllowEffFrom, lDtPeonAllowEffTo, lDbOldPeonAllowAmt, peonAllowCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
							if (!isMonthYearSame) {
								lLngTempAmount[23] = lLngOldPeonAllow;
							} else {
								lLngTempAmount[23] = lLngTempAmount[23] + lLngOldPeonAllow;
							}
							lLngTotalPeonAllowOld += lLngOldPeonAllow;
							lLngPageTotalOldPeonAllow += lLngOldPeonAllow;
							
//							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
//								sbLineDisplay.append(String.format("%7s", lLngTempAmount[23]));
//							}
							
							lLngOldMedicalAllow = calcAllowance(lDtFromDtUpdated, lDtToDtUpdated, lDtMedicalAllowEffFrom, lDtMedicalAllowEffTo, lDbOldMedicalAllowAmt, medicalAllowCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
							if (!isMonthYearSame) {
								lLngTempAmount[24] = lLngOldMedicalAllow;
							} else {
								lLngTempAmount[24] = lLngTempAmount[24] + lLngOldMedicalAllow;
							}
							lLngTotalMedicalAllowOld += lLngOldMedicalAllow;
							lLngPageTotalOldMedicalAllow += lLngOldMedicalAllow;
							
//							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
//								sbLineDisplay.append(String.format("%7s", lLngTempAmount[24]));
//							}
							
							lLngOldGallantryAmt = calcAllowance(lDtFromDtUpdated, lDtToDtUpdated, lDtGallantryAllowEffFrom, lDtGallantryAllowEffTo, lDbOldGallantryAllowAmt, gallantryCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
							if (!isMonthYearSame) {
								lLngTempAmount[25] = lLngOldGallantryAmt;
							} else {
								lLngTempAmount[25] = lLngTempAmount[25] + lLngOldGallantryAmt;
							}
							lLngTotalGallantryAmtOld += lLngOldGallantryAmt;
							lLngPageTotalOldGallantryAmt += lLngOldGallantryAmt;
							
//							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
//								sbLineDisplay.append(String.format("%7s", lLngTempAmount[25]));
//							}
							
							lLngOldOtherAllow = calcAllowance(lDtFromDtUpdated, lDtToDtUpdated, lDtOtherAllowEffFrom, lDtOtherAllowEffTo, lDbOldOtherAllowAmt, otherAllowCntr, lLngEffDaysinMonth, lIntTotalDaysInMonth);
							if (!isMonthYearSame) {
								lLngTempAmount[26] = lLngOldOtherAllow;
							} else {
								lLngTempAmount[26] = lLngTempAmount[26] + lLngOldOtherAllow;
							}
							lLngTotalOtherAllowOld += lLngOldOtherAllow;
							lLngPageTotalOldOtherAllow += lLngOldOtherAllow;
							
//							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
//								sbLineDisplay.append(String.format("%7s", lLngTempAmount[26]));
//							}
							/** ################ Other Allowance End #################### */

							/** ################ TI Start #################### */

							if (!"2006 To 2006".equals(lStrRevType[revTypeCntr])) {
//								if("Yes".equals(lStrOldDP[oldDPCntr]))
//								{
								if (lDtPnsnrDOR != null && (lDtPnsnrDOR.before(IFMSCommonServiceImpl.getDateFromString("31/07/2004")) 
										|| lDtPnsnrDOR.equals(IFMSCommonServiceImpl.getDateFromString("31/07/2004"))) 
										&& lDtFromDtUpdated.after(IFMSCommonServiceImpl.getDateFromString("31/07/2004")))
								{
									lLngDPYesDaysOld = calcDP(inputMap, lDtFromDtUpdated,lLngHeadCode, lDbOldBasicUpdated - lLngPunishAmtOld, lDbOldBasic, oldBasicCntr, 
										 true, lLngEffDaysinMonth, lIntTotalDaysInMonth);
									
								}
//								}
								lLngTIAmountOld= calcDA(lStrRevType, revTypeCntr, lDtFromDtUpdated, lDtToDtUpdated, lLngHeadCode,  lLngDPAmountOld, lDtOldReEmpEffFrom, 
										lDtOldReEmpEffTo, oldReEmpCntr, lDbOldBasicUpdated - lLngPunishAmtOld, oldBasicCntr, "Old", lLngDPYesDaysOld, lLngEffDaysinMonth,
										lIntTotalDaysInMonth,lStrDAInPnsnSal);
								

							} else if ("2006 To 2006".equals(lStrRevType[revTypeCntr])) {
								
								lLngTIAmountOld= calcDA(lStrRevType, revTypeCntr, lDtFromDtUpdated, lDtToDtUpdated, lLngHeadCode,  lLngADPAmountOld, lDtOldReEmpEffFrom, 
										lDtOldReEmpEffTo, oldReEmpCntr, lDbOldBasicUpdated - lLngPunishAmtOld, oldBasicCntr, "Old", lLngDPYesDaysOld, lLngEffDaysinMonth,
										lIntTotalDaysInMonth,lStrDAInPnsnSal);
								

//								int lIntEffReEmpDays = getReEmpEffectDays(lDtNewReEmpEffFrom, lDtNewReEmpEffTo, newReEmpCntr, lDtFromDtUpdated, lDtToDtUpdated);
//
//								lLngTIAmountOld = Math.round((lLngTIAmountOld.doubleValue() * (lLngEffDaysinMonth - lIntEffReEmpDays)) / lLngEffDaysinMonth);

							}

							if (!isMonthYearSame) {
								lLngTempAmount[15] = lLngTIAmountOld;
							} else {
								lLngTempAmount[15] = lLngTempAmount[15] + lLngTIAmountOld;
							}
							lLngTotalTIOld += lLngTIAmountOld;
							lLngPageTotalOldTI += lLngTIAmountOld;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%7s", lLngTempAmount[15]));
							}

							/** ################ TI End #################### */

							/**
							 * ################ Old CVP calc Start
							 * ####################
							 */

							if (lDtOldCVPEffFrom[oldCVPCntr] != null) {
								lLngCVPEffAmountOld = calcCVP(inputMap, lDtFromDtUpdated, lDtToDtUpdated, lDtOldCVPEffFrom, lDtOldCVPEffTo, lDbOldCVP, oldCVPCntr, lLngEffDaysinMonth,
										lIntTotalDaysInMonth);
							}

							if (!isMonthYearSame) {
								lLngTempAmount[16] = lLngCVPEffAmountOld;
							} else {
								lLngTempAmount[16] = lLngTempAmount[16] + lLngCVPEffAmountOld;
							}
							lLngTotalCVPAmountOld += lLngCVPEffAmountOld;
							lLngPageTotalOldCVP += lLngCVPEffAmountOld;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%9s", lLngTempAmount[16]));
							}

							/**
							 * ################ Old CVP calc End
							 * ####################
							 */

							/**
							 * ################ Total Drawn Start
							 * ####################
							 */

							//lLngTotalDrawn = Math.round(lDbOldBasicUpdated) + lLngTIAmountOld + lLngMAAmountOld - lLngCVPEffAmountOld;
							lLngTotalDrawn = Math.round(lDbOldBasicUpdated - lLngPunishAmtOld) + lLngTIAmountOld  - lLngCVPEffAmountOld;

							if ("New To New".equals(lStrRevType[revTypeCntr]) || "New To 2006".equals(lStrRevType[revTypeCntr])) {
								lLngTotalDrawn += lLngDPAmountOld;
							} else if ("Old To Old".equals(lStrRevType[revTypeCntr]) || "Old To New".equals(lStrRevType[revTypeCntr])) {
								lLngTotalDrawn += lLngIRAmountOld[0] + lLngIRAmountOld[1] + lLngIRAmountOld[2];
							} else if ("2006 To 2006".equals(lStrRevType[revTypeCntr])) {
								lLngTotalDrawn += lLngADPAmountOld;
							}
							lLngTotalDrawn += lLngOldPeonAllow +lLngOldMedicalAllow + lLngOldGallantryAmt + lLngOldOtherAllow;
							
							if (!isMonthYearSame) {
								lLngTempAmount[17] = lLngTotalDrawn;
							} else {
								lLngTempAmount[17] = lLngTempAmount[17] + lLngTotalDrawn;
							}
							lLngTotalDrawnSum += lLngTotalDrawn;
							lLngPageTotalDrawn += lLngTotalDrawn;

							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%9s", lLngTempAmount[17]));
							}
							sbLineDisplay.append(String.format("%2s", "|"));
							/**
							 * ################ Total Drawn Ends
							 * ####################
							 */

							/**
							 * ################ Old Basic End
							 * ####################
							 */

							/**
							 * ################ Net Arrear Starts
							 * ####################
							 */

							lLngNetArrear = lLngTotalDue - lLngTotalDrawn;

							if (!isMonthYearSame) {
								lLngTempAmount[18] = lLngNetArrear;
							} else {
								lLngTempAmount[18] = lLngTempAmount[18] + lLngNetArrear;
							}
							lLngNetArrearSum += lLngNetArrear;
							lLngPageNetArrer += lLngNetArrear;
							if (!isSave && (!isEndMonth || isLastEndLoop || (isEndMonth && !isNextMonthYearSame) || isMonthYearSame)) {
								sbLineDisplay.append(String.format("%10s", lLngTempAmount[18]));
							}
							lIntRowNo++;
							if ((lIntRowNo != 0) && (lIntRowNo % 47 == 0)) {
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(getChar(132, "-"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%-9s", "Total:"));
								sbLineDisplay.append(String.format("%8s", Math.round(lLngPageTotalNewBasic)));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewADP));
								sbLineDisplay.append(String.format("%5s", lLngPageTotalNewDP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewIR1));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewTI));
								sbLineDisplay.append(String.format("%10s", lLngPageTotalNewCVP));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalDue));
								sbLineDisplay.append(String.format("%3s", "|"));
								sbLineDisplay.append(String.format("%8s", Math.round(lLngPageTotalOldBasic)));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldADP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldDP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldIR1));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldTI));
								sbLineDisplay.append(String.format("%9s", lLngPageTotalOldCVP));
								sbLineDisplay.append(String.format("%9s", lLngPageTotalDrawn));
								sbLineDisplay.append(String.format("%2s", "|"));
								sbLineDisplay.append(String.format("%10s", lLngPageNetArrer));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%17s", lLngPageTotalNewPunish));
								sbLineDisplay.append(String.format("%19s", lLngPageTotalNewIR2));
								sbLineDisplay.append(String.format("%28s", "|"));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldPunish));
								sbLineDisplay.append(String.format("%22s", lLngPageTotalOldIR2));
								sbLineDisplay.append(String.format("%27s", "|"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%-9s", ""));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalNewPeonAllow));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewMedicalAllow));
								sbLineDisplay.append(String.format("%5s", lLngPageTotalNewGallantryAmt));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewIR3));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewOtherAllow));
								sbLineDisplay.append(String.format("%21s", "|"));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldPeonAllow));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldMedicalAllow));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldGallantryAmt));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldIR3));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldOtherAllow));
								sbLineDisplay.append(String.format("%20s", "|"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(getChar(132, "-"));
								sbLineDisplay.append("\r\n");
								
								lLngPageTotalNewBasic = Double.valueOf(0);
								lLngPageTotalNewPunish = Long.valueOf(0);
								lLngPageTotalOldPunish = Long.valueOf(0);
								lLngPageTotalNewADP = Long.valueOf(0);
								lLngPageTotalNewDP = Long.valueOf(0);
								lLngPageTotalNewIR1 = Long.valueOf(0);
								lLngPageTotalNewIR2 = Long.valueOf(0);
								lLngPageTotalNewIR3 = Long.valueOf(0);
								lLngPageTotalNewTI = Long.valueOf(0);
								lLngPageTotalNewCVP = Long.valueOf(0);
								lLngPageTotalDue = Long.valueOf(0);
								lLngPageTotalOldBasic = Double.valueOf(0);
								lLngPageTotalOldADP = Long.valueOf(0);
								lLngPageTotalOldDP = Long.valueOf(0);
								lLngPageTotalOldIR1 = Long.valueOf(0);
								lLngPageTotalOldIR2 = Long.valueOf(0);
								lLngPageTotalOldIR3 = Long.valueOf(0);
								lLngPageTotalOldTI = Long.valueOf(0);
								lLngPageTotalOldCVP = Long.valueOf(0);
								lLngPageTotalDrawn = Long.valueOf(0);
								lLngPageNetArrer = Long.valueOf(0);
								lLngPageTotalNewPeonAllow = Long.valueOf(0);
								lLngPageTotalNewMedicalAllow = Long.valueOf(0);
								lLngPageTotalNewGallantryAmt = Long.valueOf(0);
								lLngPageTotalNewOtherAllow = Long.valueOf(0);
								lLngPageTotalOldPeonAllow = Long.valueOf(0);
								lLngPageTotalOldMedicalAllow = Long.valueOf(0);
								lLngPageTotalOldGallantryAmt = Long.valueOf(0);
								lLngPageTotalOldOtherAllow = Long.valueOf(0);

								lIntPageNo++;
							
								sbLineDisplay.append((char) 12);

								sbLineDisplay.append("\r\n\r\n");
								sbLineDisplay.append(String.format("%132s", "Page No : "+lIntPageNo+" \n"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%132s", "Print Date : "+lStrCurrDt+" \n"));
								sbLineDisplay.append(sHeadingCol.toString());
								lIntRowNo =13;
							}
							sbLineDisplay.append("\r\n");
							sbLineDisplay.append(String.format("%17s", lLngTempAmount[27]));
							sbLineDisplay.append(String.format("%19s", lLngTempAmount[4]));
							sbLineDisplay.append(String.format("%28s", "|"));
							sbLineDisplay.append(String.format("%8s", lLngTempAmount[28]));
							sbLineDisplay.append(String.format("%22s", lLngTempAmount[13]));
							sbLineDisplay.append(String.format("%27s", "|"));
							sbLineDisplay.append("\r\n");
							lIntRowNo++;
							if ((lIntRowNo != 0) && (lIntRowNo % 47 == 0)) {
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(getChar(132, "-"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%-9s", "Total:"));
								sbLineDisplay.append(String.format("%8s", Math.round(lLngPageTotalNewBasic)));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewADP));
								sbLineDisplay.append(String.format("%5s", lLngPageTotalNewDP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewIR1));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewTI));
								sbLineDisplay.append(String.format("%10s", lLngPageTotalNewCVP));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalDue));
								sbLineDisplay.append(String.format("%3s", "|"));
								sbLineDisplay.append(String.format("%8s", Math.round(lLngPageTotalOldBasic)));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldADP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldDP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldIR1));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldTI));
								sbLineDisplay.append(String.format("%9s", lLngPageTotalOldCVP));
								sbLineDisplay.append(String.format("%9s", lLngPageTotalDrawn));
								sbLineDisplay.append(String.format("%2s", "|"));
								sbLineDisplay.append(String.format("%10s", lLngPageNetArrer));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%17s", lLngPageTotalNewPunish));
								sbLineDisplay.append(String.format("%19s", lLngPageTotalNewIR2));
								sbLineDisplay.append(String.format("%28s", "|"));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldPunish));
								sbLineDisplay.append(String.format("%22s", lLngPageTotalOldIR2));
								sbLineDisplay.append(String.format("%27s", "|"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%-9s", ""));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalNewPeonAllow));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewMedicalAllow));
								sbLineDisplay.append(String.format("%5s", lLngPageTotalNewGallantryAmt));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewIR3));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewOtherAllow));
								sbLineDisplay.append(String.format("%21s", "|"));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldPeonAllow));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldMedicalAllow));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldGallantryAmt));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldIR3));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldOtherAllow));
								sbLineDisplay.append(String.format("%20s", "|"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(getChar(132, "-"));
								sbLineDisplay.append("\r\n");
								
								lLngPageTotalNewBasic = Double.valueOf(0);
								lLngPageTotalNewPunish = Long.valueOf(0);
								lLngPageTotalOldPunish = Long.valueOf(0);
								lLngPageTotalNewADP = Long.valueOf(0);
								lLngPageTotalNewDP = Long.valueOf(0);
								lLngPageTotalNewIR1 = Long.valueOf(0);
								lLngPageTotalNewIR2 = Long.valueOf(0);
								lLngPageTotalNewIR3 = Long.valueOf(0);
								lLngPageTotalNewTI = Long.valueOf(0);
								lLngPageTotalNewCVP = Long.valueOf(0);
								lLngPageTotalDue = Long.valueOf(0);
								lLngPageTotalOldBasic = Double.valueOf(0);
								lLngPageTotalOldADP = Long.valueOf(0);
								lLngPageTotalOldDP = Long.valueOf(0);
								lLngPageTotalOldIR1 = Long.valueOf(0);
								lLngPageTotalOldIR2 = Long.valueOf(0);
								lLngPageTotalOldIR3 = Long.valueOf(0);
								lLngPageTotalOldTI = Long.valueOf(0);
								lLngPageTotalOldCVP = Long.valueOf(0);
								lLngPageTotalDrawn = Long.valueOf(0);
								lLngPageNetArrer = Long.valueOf(0);
								lLngPageTotalNewPeonAllow = Long.valueOf(0);
								lLngPageTotalNewMedicalAllow = Long.valueOf(0);
								lLngPageTotalNewGallantryAmt = Long.valueOf(0);
								lLngPageTotalNewOtherAllow = Long.valueOf(0);
								lLngPageTotalOldPeonAllow = Long.valueOf(0);
								lLngPageTotalOldMedicalAllow = Long.valueOf(0);
								lLngPageTotalOldGallantryAmt = Long.valueOf(0);
								lLngPageTotalOldOtherAllow = Long.valueOf(0);
								lIntPageNo++;
							
								sbLineDisplay.append((char) 12);
								sbLineDisplay.append("\r\n\r\n");
								sbLineDisplay.append(String.format("%132s", "Page No : "+lIntPageNo+" \n"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%132s", "Print Date : "+lStrCurrDt+" \n"));
								sbLineDisplay.append(sHeadingCol.toString());
								lIntRowNo =13;
							}
							sbLineDisplay.append(String.format("%-9s", ""));
							sbLineDisplay.append(String.format("%8s", lLngTempAmount[19]));
							sbLineDisplay.append(String.format("%7s", lLngTempAmount[20]));
							sbLineDisplay.append(String.format("%5s", lLngTempAmount[21]));
							sbLineDisplay.append(String.format("%7s", lLngTempAmount[5]));
							sbLineDisplay.append(String.format("%7s", lLngTempAmount[22]));
							sbLineDisplay.append(String.format("%21s", "|"));
							sbLineDisplay.append(String.format("%8s", lLngTempAmount[23]));
							sbLineDisplay.append(String.format("%8s", lLngTempAmount[24]));
							sbLineDisplay.append(String.format("%7s", lLngTempAmount[25]));
							sbLineDisplay.append(String.format("%7s", lLngTempAmount[14]));
							sbLineDisplay.append(String.format("%7s", lLngTempAmount[26]));
							sbLineDisplay.append(String.format("%20s", "|"));
							sbLineDisplay.append("\r\n");
						
							
							
							lIntRowNo++;
							if ((lIntRowNo != 0) && (lIntRowNo % 47 == 0)) {
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(getChar(132, "-"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%-9s", "Total:"));
								sbLineDisplay.append(String.format("%8s", Math.round(lLngPageTotalNewBasic)));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewADP));
								sbLineDisplay.append(String.format("%5s", lLngPageTotalNewDP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewIR1));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewTI));
								sbLineDisplay.append(String.format("%10s", lLngPageTotalNewCVP));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalDue));
								sbLineDisplay.append(String.format("%3s", "|"));
								sbLineDisplay.append(String.format("%8s", Math.round(lLngPageTotalOldBasic)));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldADP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldDP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldIR1));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldTI));
								sbLineDisplay.append(String.format("%9s", lLngPageTotalOldCVP));
								sbLineDisplay.append(String.format("%9s", lLngPageTotalDrawn));
								sbLineDisplay.append(String.format("%2s", "|"));
								sbLineDisplay.append(String.format("%10s", lLngPageNetArrer));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%17s", lLngPageTotalNewPunish));
								sbLineDisplay.append(String.format("%19s", lLngPageTotalNewIR2));
								sbLineDisplay.append(String.format("%28s", "|"));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldPunish));
								sbLineDisplay.append(String.format("%22s", lLngPageTotalOldIR2));
								sbLineDisplay.append(String.format("%27s", "|"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%-9s", ""));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalNewPeonAllow));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewMedicalAllow));
								sbLineDisplay.append(String.format("%5s", lLngPageTotalNewGallantryAmt));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewIR3));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewOtherAllow));
								sbLineDisplay.append(String.format("%21s", "|"));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldPeonAllow));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldMedicalAllow));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldGallantryAmt));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldIR3));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldOtherAllow));
								sbLineDisplay.append(String.format("%20s", "|"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(getChar(132, "-"));
								sbLineDisplay.append("\r\n");
								
								lLngPageTotalNewBasic = Double.valueOf(0);
								lLngPageTotalNewPunish = Long.valueOf(0);
								lLngPageTotalOldPunish = Long.valueOf(0);
								lLngPageTotalNewADP = Long.valueOf(0);
								lLngPageTotalNewDP = Long.valueOf(0);
								lLngPageTotalNewIR1 = Long.valueOf(0);
								lLngPageTotalNewIR2 = Long.valueOf(0);
								lLngPageTotalNewIR3 = Long.valueOf(0);
								lLngPageTotalNewTI = Long.valueOf(0);
								lLngPageTotalNewCVP = Long.valueOf(0);
								lLngPageTotalDue = Long.valueOf(0);
								lLngPageTotalOldBasic = Double.valueOf(0);
								lLngPageTotalOldADP = Long.valueOf(0);
								lLngPageTotalOldDP = Long.valueOf(0);
								lLngPageTotalOldIR1 = Long.valueOf(0);
								lLngPageTotalOldIR2 = Long.valueOf(0);
								lLngPageTotalOldIR3 = Long.valueOf(0);
								lLngPageTotalOldTI = Long.valueOf(0);
								lLngPageTotalOldCVP = Long.valueOf(0);
								lLngPageTotalDrawn = Long.valueOf(0);
								lLngPageNetArrer = Long.valueOf(0);
								lLngPageTotalNewPeonAllow = Long.valueOf(0);
								lLngPageTotalNewMedicalAllow = Long.valueOf(0);
								lLngPageTotalNewGallantryAmt = Long.valueOf(0);
								lLngPageTotalNewOtherAllow = Long.valueOf(0);
								lLngPageTotalOldPeonAllow = Long.valueOf(0);
								lLngPageTotalOldMedicalAllow = Long.valueOf(0);
								lLngPageTotalOldGallantryAmt = Long.valueOf(0);
								lLngPageTotalOldOtherAllow = Long.valueOf(0);
								lIntPageNo++;
							
								sbLineDisplay.append((char) 12);
								sbLineDisplay.append("\r\n\r\n");	
								sbLineDisplay.append(String.format("%132s",  "Page No : "+lIntPageNo+" \n"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%132s", "Print Date : "+lStrCurrDt+" \n"));
								sbLineDisplay.append(sHeadingCol.toString());
								lIntRowNo =13;
							}
							lIntRowNo++;
							if ((lIntRowNo != 0) && (lIntRowNo % 47 == 0)) {
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(getChar(132, "-"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%-9s", "Total:"));
								sbLineDisplay.append(String.format("%8s", Math.round(lLngPageTotalNewBasic)));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewADP));
								sbLineDisplay.append(String.format("%5s", lLngPageTotalNewDP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewIR1));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewTI));
								sbLineDisplay.append(String.format("%10s", lLngPageTotalNewCVP));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalDue));
								sbLineDisplay.append(String.format("%3s", "|"));
								sbLineDisplay.append(String.format("%8s", Math.round(lLngPageTotalOldBasic)));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldADP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldDP));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldIR1));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldTI));
								sbLineDisplay.append(String.format("%9s", lLngPageTotalOldCVP));
								sbLineDisplay.append(String.format("%9s", lLngPageTotalDrawn));
								sbLineDisplay.append(String.format("%2s", "|"));
								sbLineDisplay.append(String.format("%10s", lLngPageNetArrer));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%17s", lLngPageTotalNewPunish));
								sbLineDisplay.append(String.format("%19s", lLngPageTotalNewIR2));
								sbLineDisplay.append(String.format("%28s", "|"));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldPunish));
								sbLineDisplay.append(String.format("%22s", lLngPageTotalOldIR2));
								sbLineDisplay.append(String.format("%27s", "|"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%-9s", ""));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalNewPeonAllow));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewMedicalAllow));
								sbLineDisplay.append(String.format("%5s", lLngPageTotalNewGallantryAmt));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewIR3));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalNewOtherAllow));
								sbLineDisplay.append(String.format("%21s", "|"));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldPeonAllow));
								sbLineDisplay.append(String.format("%8s", lLngPageTotalOldMedicalAllow));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldGallantryAmt));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldIR3));
								sbLineDisplay.append(String.format("%7s", lLngPageTotalOldOtherAllow));
								sbLineDisplay.append(String.format("%20s", "|"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(getChar(132, "-"));
								sbLineDisplay.append("\r\n");
								
								lLngPageTotalNewBasic = Double.valueOf(0);
								lLngPageTotalNewPunish = Long.valueOf(0);
								lLngPageTotalOldPunish = Long.valueOf(0);
								lLngPageTotalNewADP = Long.valueOf(0);
								lLngPageTotalNewDP = Long.valueOf(0);
								lLngPageTotalNewIR1 = Long.valueOf(0);
								lLngPageTotalNewIR2 = Long.valueOf(0);
								lLngPageTotalNewIR3 = Long.valueOf(0);
								lLngPageTotalNewTI = Long.valueOf(0);
								lLngPageTotalNewCVP = Long.valueOf(0);
								lLngPageTotalDue = Long.valueOf(0);
								lLngPageTotalOldBasic = Double.valueOf(0);
								lLngPageTotalOldADP = Long.valueOf(0);
								lLngPageTotalOldDP = Long.valueOf(0);
								lLngPageTotalOldIR1 = Long.valueOf(0);
								lLngPageTotalOldIR2 = Long.valueOf(0);
								lLngPageTotalOldIR3 = Long.valueOf(0);
								lLngPageTotalOldTI = Long.valueOf(0);
								lLngPageTotalOldCVP = Long.valueOf(0);
								lLngPageTotalDrawn = Long.valueOf(0);
								lLngPageNetArrer = Long.valueOf(0);
								lLngPageTotalNewPeonAllow = Long.valueOf(0);
								lLngPageTotalNewMedicalAllow = Long.valueOf(0);
								lLngPageTotalNewGallantryAmt = Long.valueOf(0);
								lLngPageTotalNewOtherAllow = Long.valueOf(0);
								lLngPageTotalOldPeonAllow = Long.valueOf(0);
								lLngPageTotalOldMedicalAllow = Long.valueOf(0);
								lLngPageTotalOldGallantryAmt = Long.valueOf(0);
								lLngPageTotalOldOtherAllow = Long.valueOf(0);
								lIntPageNo++;
							
								sbLineDisplay.append((char) 12);
								sbLineDisplay.append("\r\n\r\n");
								sbLineDisplay.append(String.format("%132s", "Page No : "+lIntPageNo+" \n"));
								sbLineDisplay.append("\r\n");
								sbLineDisplay.append(String.format("%132s", "Print Date : "+lStrCurrDt+" \n"));
								sbLineDisplay.append(sHeadingCol.toString());
								lIntRowNo =13;
							}

							/**
							 * ################ Net Arrear End
							 * ####################
							 */

							// For Punishment Cut
							if (lDtPunishEffFrom.length > (punishCutCntr + 1)
									&& lDtPunishEffFrom[punishCutCntr + 1] != null
									&& (lDtPunishEffFrom[punishCutCntr + 1].before(lDtToDtUpdated) || lDtPunishEffFrom[punishCutCntr + 1].equals(lDtToDtUpdated)
											|| lDtPunishEffTo[punishCutCntr].before(lDtToDtUpdated) || lDtPunishEffTo[punishCutCntr].equals(lDtToDtUpdated))) {
								punishCutCntr++;
							}
							// For DP old and new
							if (lDtNewDPEffFrom.length > (newDPCntr + 1)
									&& lDtNewDPEffFrom[newDPCntr + 1] != null
									&& (lDtNewDPEffFrom[newDPCntr + 1].before(lDtToDtUpdated) || lDtNewDPEffFrom[newDPCntr + 1].equals(lDtToDtUpdated)
											|| lDtNewDPEffTo[newDPCntr].before(lDtToDtUpdated) || lDtNewDPEffTo[newDPCntr].equals(lDtToDtUpdated))) {
								newDPCntr++;
							}

							if (lDtOldDPEffFrom.length > (oldDPCntr + 1)
									&& lDtOldDPEffFrom[oldDPCntr + 1] != null
									&& (lDtOldDPEffFrom[oldDPCntr + 1].before(lDtToDtUpdated) || lDtOldDPEffFrom[oldDPCntr + 1].equals(lDtToDtUpdated)
											|| lDtOldDPEffTo[oldDPCntr].before(lDtToDtUpdated) || lDtOldDPEffTo[oldDPCntr].equals(lDtToDtUpdated))) {
								oldDPCntr++;
							}

							// For CVP old and new
							if (lDtNewCVPEffFrom.length > (newCVPCntr + 1)
									&& lDtNewCVPEffFrom[newCVPCntr + 1] != null
									&& (lDtNewCVPEffFrom[newCVPCntr + 1].before(lDtToDtUpdated) || lDtNewCVPEffFrom[newCVPCntr + 1].equals(lDtToDtUpdated)
											|| lDtNewCVPEffTo[newCVPCntr].before(lDtToDtUpdated) || lDtNewCVPEffTo[newCVPCntr].equals(lDtToDtUpdated))) {
								newCVPCntr++;
							}

							if (lDtOldCVPEffFrom.length > (oldCVPCntr + 1)
									&& lDtOldCVPEffFrom[oldCVPCntr + 1] != null
									&& (lDtOldCVPEffFrom[oldCVPCntr + 1].before(lDtToDtUpdated) || lDtOldCVPEffFrom[oldCVPCntr + 1].equals(lDtToDtUpdated)
											|| lDtOldCVPEffTo[oldCVPCntr].before(lDtToDtUpdated) || lDtOldCVPEffTo[oldCVPCntr].equals(lDtToDtUpdated))) {
								oldCVPCntr++;
							}

							// For ReEmp old and new
							if (lDtNewReEmpEffFrom.length > (newReEmpCntr + 1)
									&& lDtNewReEmpEffFrom[newReEmpCntr + 1] != null
									&& (lDtNewReEmpEffFrom[newReEmpCntr + 1].before(lDtToDtUpdated) || lDtNewReEmpEffFrom[newReEmpCntr + 1].equals(lDtToDtUpdated)
											|| lDtNewReEmpEffTo[newReEmpCntr].before(lDtToDtUpdated) || lDtNewReEmpEffTo[newReEmpCntr].equals(lDtToDtUpdated))) {
								newReEmpCntr++;
							}

							if (lDtOldReEmpEffFrom.length > (oldReEmpCntr + 1)
									&& lDtOldReEmpEffFrom[oldReEmpCntr + 1] != null
									&& (lDtOldReEmpEffFrom[oldReEmpCntr + 1].before(lDtToDtUpdated) || lDtOldReEmpEffFrom[oldReEmpCntr + 1].equals(lDtToDtUpdated)
											|| lDtOldReEmpEffTo[oldReEmpCntr].before(lDtToDtUpdated) || lDtOldReEmpEffTo[oldReEmpCntr].equals(lDtToDtUpdated))) {
								oldReEmpCntr++;
							}
							
							// For Other Allowance
							if (lDtPeonAllowEffFrom.length > (peonAllowCntr + 1)
									&& lDtPeonAllowEffFrom[peonAllowCntr + 1] != null
									&& (lDtPeonAllowEffFrom[peonAllowCntr + 1].before(lDtToDtUpdated) || lDtPeonAllowEffFrom[peonAllowCntr + 1].equals(lDtToDtUpdated)
											|| lDtPeonAllowEffTo[peonAllowCntr].before(lDtToDtUpdated) || lDtPeonAllowEffTo[peonAllowCntr].equals(lDtToDtUpdated))) {
								peonAllowCntr++;
							}
							if (lDtMedicalAllowEffFrom.length > (medicalAllowCntr + 1)
									&& lDtMedicalAllowEffFrom[medicalAllowCntr + 1] != null
									&& (lDtMedicalAllowEffFrom[medicalAllowCntr + 1].before(lDtToDtUpdated) || lDtMedicalAllowEffFrom[medicalAllowCntr + 1].equals(lDtToDtUpdated)
											|| lDtMedicalAllowEffTo[medicalAllowCntr].before(lDtToDtUpdated) || lDtMedicalAllowEffTo[medicalAllowCntr].equals(lDtToDtUpdated))) {
								medicalAllowCntr++;
							}
							if (lDtGallantryAllowEffFrom.length > (gallantryCntr + 1)
									&& lDtGallantryAllowEffFrom[gallantryCntr + 1] != null
									&& (lDtGallantryAllowEffFrom[gallantryCntr + 1].before(lDtToDtUpdated) || lDtGallantryAllowEffFrom[gallantryCntr + 1].equals(lDtToDtUpdated)
											|| lDtGallantryAllowEffTo[gallantryCntr].before(lDtToDtUpdated) || lDtGallantryAllowEffTo[gallantryCntr].equals(lDtToDtUpdated))) {
								gallantryCntr++;
							}
							if (lDtOtherAllowEffFrom.length > (otherAllowCntr + 1)
									&& lDtOtherAllowEffFrom[otherAllowCntr + 1] != null
									&& (lDtOtherAllowEffFrom[otherAllowCntr + 1].before(lDtToDtUpdated) || lDtOtherAllowEffFrom[otherAllowCntr + 1].equals(lDtToDtUpdated)
											|| lDtOtherAllowEffTo[otherAllowCntr].before(lDtToDtUpdated) || lDtOtherAllowEffTo[otherAllowCntr].equals(lDtToDtUpdated))) {
								otherAllowCntr++;
							}


							lObjCalendar.setTime(lDtFromDtUpdated);
							lObjCalendar.add(Calendar.MONTH, 1);
							lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
							lDtFromDtUpdated = lObjCalendar.getTime();

							lObjCalendar.setTime(lDtToDtUpdated);
							lObjCalendar.add(Calendar.MONTH, 1);
							lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
							lDtToDtUpdated = lObjCalendar.getTime();

							if (lDtOldBasicEffTo[masterCtr].before(lDtToDtUpdated)) {
								lDtToDtUpdated = lDtOldBasicEffTo[masterCtr];
							}

							isMonthYearSame = false;
							if (yearCtr == lIntToYear && monthCtr == lIntToMonth) {
								break;
							}

						}
					}
					
					
					revTypeCntr++;
					oldBasicCntr++;
					newBasicCntr++;
				}

				if (!isSave/*
							 * &&
							 * !"New To 2006".equals(lStrRevType[revTypeCntr])
							 */) {
					sbLineDisplay.append("\r\n");
					sbLineDisplay.append(getChar(132, "-"));
					sbLineDisplay.append("\r\n");
					sbLineDisplay.append(String.format("%-9s", "Total:"));
					sbLineDisplay.append(String.format("%8s", Math.round(lDbNewBasicTotal)));
					sbLineDisplay.append(String.format("%7s", lLngTotalADPNew));
					sbLineDisplay.append(String.format("%5s", lLngTotalDPNew));
					sbLineDisplay.append(String.format("%7s", lLngTotalIR1New));
					sbLineDisplay.append(String.format("%7s", lLngTotalTINew));
					sbLineDisplay.append(String.format("%10s", lLngTotalCVPAmountNew));
					sbLineDisplay.append(String.format("%8s", lLngTotalDueSum));
					sbLineDisplay.append(String.format("%3s", "|"));
					sbLineDisplay.append(String.format("%8s", Math.round(lDbOldBasicTotal)));
					sbLineDisplay.append(String.format("%8s", lLngTotalADPOld));
					sbLineDisplay.append(String.format("%7s", lLngTotalDPOld));
					sbLineDisplay.append(String.format("%7s", lLngTotalIR1Old));
					sbLineDisplay.append(String.format("%7s", lLngTotalTIOld));
					sbLineDisplay.append(String.format("%9s", lLngTotalCVPAmountOld));
					sbLineDisplay.append(String.format("%9s", lLngTotalDrawnSum));
					sbLineDisplay.append(String.format("%2s", "|"));
					sbLineDisplay.append(String.format("%10s", lLngNetArrearSum));
					sbLineDisplay.append("\r\n");
					sbLineDisplay.append(String.format("%17s", lLngTotalPunishNew));
					sbLineDisplay.append(String.format("%19s", lLngTotalIR2New));
					sbLineDisplay.append(String.format("%28s", "|"));
					sbLineDisplay.append(String.format("%8s", lLngTotalPunishOld));
					sbLineDisplay.append(String.format("%22s", lLngTotalIR2Old));
					sbLineDisplay.append(String.format("%27s", "|"));
					sbLineDisplay.append("\r\n");
					sbLineDisplay.append(String.format("%-9s", ""));
					sbLineDisplay.append(String.format("%8s", lLngTotalPeonAllowNew));
					sbLineDisplay.append(String.format("%7s", lLngTotalMedicalAllowNew));
					sbLineDisplay.append(String.format("%5s", lLngTotalGallantryAmtNew));
					sbLineDisplay.append(String.format("%7s", lLngTotalIR3New));
					sbLineDisplay.append(String.format("%7s", lLngTotalOtherAllowNew));
					sbLineDisplay.append(String.format("%21s", "|"));
					sbLineDisplay.append(String.format("%8s", lLngTotalPeonAllowOld));
					sbLineDisplay.append(String.format("%8s", lLngTotalMedicalAllowOld));
					sbLineDisplay.append(String.format("%7s", lLngTotalGallantryAmtOld));
					sbLineDisplay.append(String.format("%7s", lLngTotalIR3Old));
					sbLineDisplay.append(String.format("%7s", lLngTotalOtherAllowOld));
					sbLineDisplay.append(String.format("%20s", "|"));
					sbLineDisplay.append("\r\n");

					sbLineDisplay.append("\r\n");
					sbLineDisplay.append("Rs. "+EnglishDecimalFormat.convertWithSpace(new BigDecimal(lLngNetArrearSum))+"  Only");
					sbLineDisplay.append("\r\n");
					sbLineDisplay.append(getChar(132, "-"));
					sbLineDisplay.append("\r\n");
					sbLineDisplay.append("\r\n");
					LoginDetails lObjLoginDetails=new LoginDetails();
					lObjLoginDetails=(LoginDetails) inputMap.get("baseLoginVO");
					OrgEmpMst lObjOrgEmpMst=lObjLoginDetails.getEmployee();
					String lStrEmpName=((lObjOrgEmpMst.getEmpPrefix() != null) ? lObjOrgEmpMst.getEmpPrefix()+" " : "")
										+((lObjOrgEmpMst.getEmpFname() != null ) ? lObjOrgEmpMst.getEmpFname()+" " : "")
										+((lObjOrgEmpMst.getEmpMname() != null) ? lObjOrgEmpMst.getEmpMname()+" " : "")
										+((lObjOrgEmpMst.getEmpLname() != null) ? lObjOrgEmpMst.getEmpLname()+" " : "");
					sbLineDisplay.append("                                                                                       Prepared By  "+lStrEmpName);
					sbLineDisplay.append("\r\n");
					sbLineDisplay.append("\r\n");
					sbLineDisplay.append("                                                                                       Approved By");
					sbLineDisplay.append("</pre></div>");
				}
				
		    
			}
			StringBuilder sbTotalAmounts = new StringBuilder();
			sbTotalAmounts.append("<TABLE>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='center'><B>");
			sbTotalAmounts.append("Component Name");
			sbTotalAmounts.append("</B></TD>");
			sbTotalAmounts.append("<TD align='center'><B>");
			sbTotalAmounts.append("Due Amount");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='center'><B>");
			sbTotalAmounts.append("Payable Amount");
			sbTotalAmounts.append("</B></TD>");
			sbTotalAmounts.append("<TD align='center'><B>");
			sbTotalAmounts.append("Difference");
			sbTotalAmounts.append("</B></TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("Pension");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(Math.round(lDbNewBasicTotal));
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(Math.round(lDbOldBasicTotal));
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(Math.round(lDbNewBasicTotal - lDbOldBasicTotal));
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("ADP");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalADPNew);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalADPOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalADPNew - lLngTotalADPOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("DP");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalDPNew);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalDPOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalDPNew - lLngTotalDPOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("IR1");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalIR1New);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalIR1Old);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalIR1New - lLngTotalIR1Old);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("IR2");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalIR2New);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalIR2Old);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalIR2New - lLngTotalIR2Old);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("IR3");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalIR3New);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalIR3Old);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalIR3New - lLngTotalIR3Old);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("DA");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalTINew);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalTIOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalTINew - lLngTotalTIOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("CVP");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalCVPAmountNew);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalCVPAmountOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalCVPAmountNew - lLngTotalCVPAmountOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("PA");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalPeonAllowNew);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalPeonAllowOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalPeonAllowNew - lLngTotalPeonAllowOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("MA");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalMedicalAllowNew);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalMedicalAllowOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalMedicalAllowNew - lLngTotalMedicalAllowOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("GA");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalGallantryAmtNew);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalGallantryAmtOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalGallantryAmtNew - lLngTotalGallantryAmtOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("Other");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalOtherAllowNew);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalOtherAllowOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalOtherAllowNew - lLngTotalOtherAllowOld);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("<TR>");
			sbTotalAmounts.append("<TD align='left'>");
			sbTotalAmounts.append("Total");
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalDueSum);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngTotalDrawnSum);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("<TD align='right'>");
			sbTotalAmounts.append(lLngNetArrearSum);
			sbTotalAmounts.append("</TD>");
			sbTotalAmounts.append("</TR>");
			sbTotalAmounts.append("</TABLE>");	
			sbTotalAmounts.append("<BR>");
			sbTotalAmounts.append("<DIV>");
			sbTotalAmounts.append("<B>Difference Amount     ");
			sbTotalAmounts.append(lLngNetArrearSum);
			sbTotalAmounts.append("</B>");
			sbTotalAmounts.append("</DIV>");
			
			
			inputMap.put("PrintString", sbLineDisplay.toString());
			inputMap.put("PrintTotalAmount", sbTotalAmounts.toString());
			inputMap.put("DisplayString",  sbLineDisplay.toString().replace("</pre></div>", "").replace("<div><pre>", ""));
			inputMap.put("PensionerCode", lStrPensionerCode);
			inputMap.put("pensionReqId", pensionReqId);
			inputMap.put("Arrear", lLngNetArrearSum);
			inputMap.put("SupplyFlag", lStrSupplyFlg);
			inputMap.put("BillFlag", lStrBillFlag);
			inputMap.put("RowCnt",lStrRowCnt);
			
			inputMap.put("PensionAmount", Math.round(lDbNewBasicTotal - lDbOldBasicTotal));
			inputMap.put("ADPAmount", lLngTotalADPNew - lLngTotalADPOld);
			inputMap.put("DPAmount",lLngTotalDPNew - lLngTotalDPOld);
			inputMap.put("IR1Amount", lLngTotalIR1New - lLngTotalIR1Old);
			inputMap.put("IR2Amount", lLngTotalIR2New - lLngTotalIR2Old);
			inputMap.put("IR3Amount", lLngTotalIR3New - lLngTotalIR3Old);
			inputMap.put("DAAmount", lLngTotalTINew - lLngTotalTIOld);
			inputMap.put("CVPAmount", lLngTotalCVPAmountNew - lLngTotalCVPAmountOld);
			inputMap.put("PeonAllowAmount", lLngTotalPeonAllowNew - lLngTotalPeonAllowOld);
			inputMap.put("MedicalAllowAmount", lLngTotalMedicalAllowNew - lLngTotalMedicalAllowOld);
			inputMap.put("GallantryAmount", lLngTotalGallantryAmtNew - lLngTotalGallantryAmtOld);
			inputMap.put("OtherBenefit", lLngTotalOtherAllowNew - lLngTotalOtherAllowOld);
			
		    resObj.setResultValue(inputMap);

		    Map lDetailMap =new HashMap();
			String lStrExportTo = StringUtility.getParameter("exportTo", request);	
			if((DBConstants.DIS_TEXTFILE).equals(lStrExportTo))
				lDetailMap.put(DBConstants.DIS_TEXTFILE,sbLineDisplay.toString().replace("</pre></div>", "").replace("<div><pre>", ""));
			
			ReportExportHelper rptExpHelper = new ReportExportHelper();
			rptExpHelper.getExportData(resObj,lStrExportTo,inputMap,lDetailMap,false);
			
			resObj.setResultValue(inputMap);
			
			if(!(DBConstants.DIS_TEXTFILE).equals(lStrExportTo))
				resObj.setViewName("OpenCalculatedArrearPage");
			
			
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	
	
	private long calcPension(Date lDtFromDtUpdated, Date lDtToDtUpdated, Date lDtPensionFrm, Date lDtPensionTo, double lDbPensionAmt, double lDbPunishCutAmt, boolean lBoolPnsDays)
			throws Exception {

		double lDbPension = 0D;
		Calendar lObjCalendar = null;
		int lIntDaysInMonth = 0;
		int lIntPnsnDays = 0;
		Integer lIntEffDaysForPnsn = 0;
		try {
			if ((lDtPensionFrm.compareTo(lDtFromDtUpdated) >= 0) && (lDtPensionFrm.compareTo(lDtToDtUpdated) <= 0) && (lDtPensionTo.compareTo(lDtToDtUpdated) > 0)) {
				lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtPensionFrm);
				lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				lIntPnsnDays = lObjCalendar.get(Calendar.DATE);

				lIntEffDaysForPnsn = lIntDaysInMonth - lIntPnsnDays + 1;
			} else if ((lDtPensionTo.compareTo(lDtToDtUpdated) <= 0) && (lDtPensionTo.compareTo(lDtFromDtUpdated) >= 0) && (lDtPensionFrm.compareTo(lDtFromDtUpdated) < 0)) {
				lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtPensionTo);
				lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				lIntPnsnDays = lObjCalendar.get(Calendar.DATE);

				lIntEffDaysForPnsn = lIntPnsnDays;
			} else if ((lDtPensionFrm.compareTo(lDtFromDtUpdated) >= 0) && (lDtPensionTo.compareTo(lDtToDtUpdated) <= 0)) {
				lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtPensionFrm);
				int FirstDate = lObjCalendar.get(Calendar.DATE);

				lObjCalendar.setTime(lDtPensionTo);
				int ToDate = lObjCalendar.get(Calendar.DATE);

				lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

				lIntEffDaysForPnsn = ToDate - FirstDate + 1;
			} else if (lDtPensionFrm.before(lDtFromDtUpdated) && lDtPensionTo.after(lDtToDtUpdated)) {
				lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtFromDtUpdated);
				lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				lIntEffDaysForPnsn = lIntDaysInMonth;
			}

			lDbPension = (lDbPensionAmt * lIntEffDaysForPnsn) / lIntDaysInMonth;
		} catch (Exception e) {
			// gLogger.error("Error is :: " + e,e);
			// throw(e);
		}
		if (lBoolPnsDays) {
			return lIntEffDaysForPnsn.longValue();
		} else {
			return Math.round(lDbPension);
		}
	}

	
	private long calcCVP(Map inputMap, Date lDtFromDtUpdated, Date lDtToDtUpdated, Date[] lDtCVPEffFrom, Date[] lDtCVPEffTo, Double[] lDbCVPAmount, int cvpCntr, long lLngEffDaysinMonth,
			int lIntTotalDaysInMonth) throws Exception {

		double lDbCVPAmt = 0D;
		Calendar lObjCalendar = null;
		int lIntDaysInMonth = 0;
		int lIntEffDaysForCVP = 0;
		int lIntDaysForCVP = 0;
		try {
			for (int i = cvpCntr; i < lDbCVPAmount.length; i++) {
				if ((lDtCVPEffFrom[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtCVPEffFrom[i].compareTo(lDtToDtUpdated) <= 0) && (lDtCVPEffTo[i].compareTo(lDtToDtUpdated) > 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtCVPEffFrom[i]);
					lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					lIntDaysForCVP = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForCVP = lIntDaysInMonth - lIntDaysForCVP + 1;
					lDbCVPAmt += (lDbCVPAmount[i] / lIntTotalDaysInMonth) * lIntEffDaysForCVP;
				} else if ((lDtCVPEffTo[i].compareTo(lDtToDtUpdated) <= 0) && (lDtCVPEffTo[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtCVPEffFrom[i].compareTo(lDtFromDtUpdated) < 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtCVPEffTo[i]);
					lIntDaysForCVP = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForCVP = lIntDaysForCVP;
					lDbCVPAmt += (lDbCVPAmount[i] / lIntTotalDaysInMonth) * lIntEffDaysForCVP;
				} else if ((lDtCVPEffFrom[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtCVPEffTo[i].compareTo(lDtToDtUpdated) <= 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtCVPEffFrom[i]);
					int FirstDate = lObjCalendar.get(Calendar.DATE);

					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtCVPEffTo[i]);
					int ToDate = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForCVP = ToDate - FirstDate + 1;
					lDbCVPAmt += (lDbCVPAmount[i] / lIntTotalDaysInMonth) * lIntEffDaysForCVP;
				} else if (lDtCVPEffFrom[i].before(lDtFromDtUpdated) && lDtCVPEffTo[i].after(lDtToDtUpdated)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtFromDtUpdated);
					int FirstDate = lObjCalendar.get(Calendar.DATE);

					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtToDtUpdated);
					int ToDate = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForCVP = ToDate - FirstDate + 1;
					lDbCVPAmt += (lDbCVPAmount[i] / lIntTotalDaysInMonth) * lIntEffDaysForCVP;
				}

			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return Math.round(lDbCVPAmt);
	}
	
	private void getDPRate(Map<String, Object> inputMap) throws Exception
	{
		try
		{
			setSessionInfo(inputMap);
			RevisedArrearCalcDAO lObjRevisedArrearCalcDAO =new RevisedArrearCalcDAOImpl(serv.getSessionFactory());
			RltPensionHeadcodeRate lObjRltPensionHeadCodeRate = new RltPensionHeadcodeRate();
			lObjRltPensionHeadCodeRate = lObjRevisedArrearCalcDAO.getDPRate("DP");
			if(lObjRltPensionHeadCodeRate != null)
			{
				gMapDPRate.put("DPRATE", lObjRltPensionHeadCodeRate.getRate());
			}
		}
		catch (Exception e) {
		gLogger.error("Error is :: " + e, e);
		throw (e);
		}
		
	}
	private long calcDP(Map inputMap, Date lDtFromDtUpdated, Long lLngHeadCode, double lDbPnsnBasic, Double[] lDbOrgBasicPnsn,
			int basicCntr, boolean TIFlag, long lLngEffDaysinMonth, int lIntTotalDaysInMonth) throws Exception {
		
		Double lDbDPAmount = 0D;
		Double lDbDPPercnt = 0D;
						
		try {
			if (lDtFromDtUpdated != null && lDtFromDtUpdated.after(IFMSCommonServiceImpl.getDateFromString("31/12/1995"))) // DP
			// is
			// valid
			// for
			// 1.1.1996
			// and
			// onwards
			{
				if(!gMapDPRate.containsKey("DPRATE"))
				{
					getDPRate(inputMap);
				}
				lDbDPPercnt = (gMapDPRate.get("DPRATE")).doubleValue();
				lDbDPAmount = (lDbPnsnBasic ) * (lDbDPPercnt / 100);

			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		if (TIFlag) {
			return lLngEffDaysinMonth;
		} else {
			return Math.round(lDbDPAmount);
		}
	}
	
	private long calcDP1(Map inputMap, String[] lStrRevType, int revTypeCntr, Date lDtFromDtUpdated, Date lDtToDtUpdated, Long lLngHeadCode, double lDbPnsnBasic, Double[] lDbOrgBasicPnsn,
			int basicCntr, String[] lStrDP, int DPCntr, Date[] lDtDPEffFrom, Date[] lDtDPEffTo, boolean TIFlag, long lLngEffDaysinMonth, int lIntTotalDaysInMonth) throws Exception {

		Double lDbDPAmount = 0D;
		Long lLngDPAmount = Long.valueOf(0);
		Long lLngPensionTemp = Long.valueOf(0);
		Double lDbDPPercnt = 0D;
		Object[] lObjArr = null;
		List lLstOfObjArr = null;
		Calendar lObjCalendar = null;
		int lIntDaysInMonth = 0;
		int lIntEffDaysForDP = 0;
		int FirstDate = 0;
		int ToDate = 0;
		int FirstDate2 = 0;
		int DPlength = lStrDP.length;
		Integer DPYesDays = 0;
		try {
			if (lDtFromDtUpdated != null && lDtFromDtUpdated.after(IFMSCommonServiceImpl.getDateFromString("31/12/1995"))) // DP
			// is
			// valid
			// for
			// 1.1.1996
			// and
			// onwards
			{
				lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, "DP", lDbOrgBasicPnsn[basicCntr], lDtFromDtUpdated);
				if (!lLstOfObjArr.isEmpty()) {
					lObjArr = (Object[]) lLstOfObjArr.get(0);
					if (lObjArr != null) {
						lDbDPPercnt = ((BigDecimal) lObjArr[4]).doubleValue();

						for (int i = DPCntr; i < DPlength; i++) {
							if ((lDtDPEffFrom[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtDPEffFrom[i].compareTo(lDtToDtUpdated) <= 0) && (lDtDPEffTo[i].compareTo(lDtToDtUpdated) > 0)) {
								if ("Yes".equals(lStrDP[i])) {
									lObjCalendar = Calendar.getInstance();
									lObjCalendar.setTime(lDtDPEffFrom[i]);
									lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
									lIntEffDaysForDP = lObjCalendar.get(Calendar.DATE);

									DPYesDays += lIntDaysInMonth - lIntEffDaysForDP + 1;
								}
							} else if ((lDtDPEffTo[i].compareTo(lDtToDtUpdated) <= 0) && (lDtDPEffTo[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtDPEffFrom[i].compareTo(lDtFromDtUpdated) < 0)) {
								if ("Yes".equals(lStrDP[i])) {
									lObjCalendar = Calendar.getInstance();
									lObjCalendar.setTime(lDtDPEffTo[i]);
									lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
									lIntEffDaysForDP = lObjCalendar.get(Calendar.DATE);

									DPYesDays += lIntEffDaysForDP;
								}

							} else if ((lDtDPEffFrom[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtDPEffTo[i].compareTo(lDtToDtUpdated) <= 0)) {
								if ("Yes".equals(lStrDP[i])) {
									lObjCalendar = Calendar.getInstance();
									lObjCalendar.setTime(lDtDPEffFrom[i]);
									FirstDate = lObjCalendar.get(Calendar.DATE);

									lObjCalendar = Calendar.getInstance();
									lObjCalendar.setTime(lDtDPEffTo[i]);
									ToDate = lObjCalendar.get(Calendar.DATE);

									DPYesDays += ToDate - FirstDate + 1;
								}

							} else if (lDtDPEffFrom[i].before(lDtFromDtUpdated) && lDtDPEffTo[i].after(lDtToDtUpdated)) {
								if ("Yes".equals(lStrDP[i])) {
									lObjCalendar = Calendar.getInstance();
									lObjCalendar.setTime(lDtFromDtUpdated);
									lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

									lObjCalendar = Calendar.getInstance();
									lObjCalendar.setTime(lDtFromDtUpdated);
									FirstDate = lObjCalendar.get(Calendar.DATE);

									lObjCalendar = Calendar.getInstance();
									lObjCalendar.setTime(lDtToDtUpdated);
									ToDate = lObjCalendar.get(Calendar.DATE);

									DPYesDays += ToDate - FirstDate + 1;

								}
							}
						}

						lDbDPAmount = ((lDbPnsnBasic * DPYesDays) / lLngEffDaysinMonth) * (lDbDPPercnt / 100);
					}
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		if (TIFlag) {
			return DPYesDays.longValue();
		} else {
			return Math.round(lDbDPAmount);
		}
	}


	private Long[] calcIR(Map inputMap, String[] lStrRevType, int revTypeCntr, Date lDtFromDt, Long lLngHeadCode, double lDbPnsnBasic, Double[] lDbOrgBasicPnsn, int basicCntr, long lLngEffDaysinMonth,
			int lIntTotalDaysInMonth) throws Exception {

		Long lLngIRAmount[]=new Long[3];
		Long lLngIR1Amt = 0L;
		Long lLngIR2Amt = 0L;
		Long lLngIR3Amt = 0L;
		List<RltPensionHeadcodeRate> lLstRltPensionHeadcodeRateVO = null;
		NewPensionBillDAOImpl lPensionBillDAO = new NewPensionBillDAOImpl(serv.getSessionFactory());
		try {
			
				List<String> lLstIRApplicable = new ArrayList<String>();
				lLstIRApplicable.add("IR2");
				lLstIRApplicable.add("IR3");
					
						
				if(lDbPnsnBasic > 0)
				{
					lLngIR1Amt = 50L;
					lLngIRAmount[0]=lLngIR1Amt;
					if (lLstRltPensionHeadcodeRateVO == null) {
						lLstRltPensionHeadcodeRateVO = lPensionBillDAO.getRateLstFromHeadcode(lLngHeadCode, lLstIRApplicable);
						
					}
					if (lLstRltPensionHeadcodeRateVO != null && lLstRltPensionHeadcodeRateVO.size() > 0) {
						Double lCalculatedIRAmt = 0.0;
						Double lMinIRAmt = 0.0;
	
						for (Object lObjVO : lLstRltPensionHeadcodeRateVO) {
							
							RltPensionHeadcodeRate lObjPensionHeadcodeRate = (RltPensionHeadcodeRate) lObjVO;
							lCalculatedIRAmt = (lObjPensionHeadcodeRate.getRate().doubleValue() * lDbPnsnBasic) / 100;
							lMinIRAmt = (lObjPensionHeadcodeRate.getMinAmount().doubleValue() / lIntTotalDaysInMonth) * lLngEffDaysinMonth;
						
							if ("IR2".equals(lObjPensionHeadcodeRate.getFieldType())) {
								lLngIR2Amt = (lCalculatedIRAmt > lMinIRAmt) ? lCalculatedIRAmt.longValue() : lMinIRAmt.longValue();
							}
							if ("IR3".equals(lObjPensionHeadcodeRate.getFieldType())) {
								lLngIR3Amt = (lCalculatedIRAmt > lMinIRAmt) ? lCalculatedIRAmt.longValue() : lMinIRAmt.longValue();
							}
						}
					}
				}
				lLngIRAmount[0]=lLngIR1Amt;
				lLngIRAmount[1]=lLngIR2Amt;
				lLngIRAmount[2]=lLngIR3Amt;
		
			
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return lLngIRAmount;
	}

	private Long calcDA(String[] lStrRevType, int revTypeCntr, Date lDtFromDt, Date lDtToDt, long lLngHeadCode,  long lLngDPAmount, Date[] lDtReEmpFrmDt, 
			Date[] lDtReEmpToDt, int reEmpCntr, double lDbOrgBasicPnsn, int basicCntr, String lStrOldNewFlag, long lLngDPYesDays, long lLngEffDaysinMonth,
			int lIntTotalDaysInMonth,String[] lStrDAInPnsnSal) throws Exception
	{
		RltPensionHeadcodeRate lRltPensionHeadcodeRateVO = null;
		int lIntReEmpEffDays = 0;
		Long lLngDAAmount = Long.valueOf(0);
		Double lDbDAPercent = 0.0;
		Double lDbDAAmount = 0.0;
		Long lLngBasicPlusDP = Long.valueOf(0);
		BigDecimal lBgDcmlDAAmount =BigDecimal.ZERO;
		try
		{
			NewPensionBillDAOImpl lPensionBillDAO = new NewPensionBillDAOImpl(serv.getSessionFactory());
			if ((lStrRevType[revTypeCntr].equals("Old To Old")) || (lStrRevType[revTypeCntr].equals("Old To New") && "Old".equals(lStrOldNewFlag))) {
				lRltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_1986", lDbOrgBasicPnsn, lDtFromDt);
					if (lRltPensionHeadcodeRateVO != null && !lRltPensionHeadcodeRateVO.equals("")) {
						lDbDAPercent = lRltPensionHeadcodeRateVO.getRate().doubleValue();
		
						lDbDAAmount =(double) (lDbOrgBasicPnsn) * (lDbDAPercent / 100);
					
					
					if (lRltPensionHeadcodeRateVO.getMinAmount() != null && !lRltPensionHeadcodeRateVO.equals("")) {
						
						Double lMinAmt = (Double.valueOf(lRltPensionHeadcodeRateVO.getMinAmount().toString()) * lLngEffDaysinMonth) / lIntTotalDaysInMonth;
						if (lMinAmt > lDbDAAmount) {
							lDbDAAmount = lMinAmt;
						}
					}
				}
			}
		    else if (("New To New".equals(lStrRevType[revTypeCntr])) || ("New To 2006".equals(lStrRevType[revTypeCntr]) && "Old".equals(lStrOldNewFlag)) || ("Old To New".equals(lStrRevType[revTypeCntr]) && "New".equals(lStrOldNewFlag))) 
		    {
//				 lRltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_1996", lDbOrgBasicPnsn, lDtFromDt);
//				 if (lRltPensionHeadcodeRateVO != null && !lRltPensionHeadcodeRateVO.equals("")) {
//					 lDbDAPercent = lRltPensionHeadcodeRateVO.getRate().doubleValue();
//					 lDbDAAmount = Math.round(lDbOrgBasicPnsn * (lLngEffDaysinMonth - lLngDPYesDays) / lLngEffDaysinMonth) * (lDbDAPercent / 100);
//			
//				 }
				 lRltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_1996_DP", lDbOrgBasicPnsn, lDtFromDt);
				 if (lRltPensionHeadcodeRateVO != null && !lRltPensionHeadcodeRateVO.equals("")) {
					 lDbDAPercent = lRltPensionHeadcodeRateVO.getRate().doubleValue();
					 //lLngBasicPlusDP = lLngDPAmount + Math.round(lDbOrgBasicPnsn * lLngDPYesDays / lLngEffDaysinMonth);
					 lLngBasicPlusDP = lLngDPAmount + Math.round(lDbOrgBasicPnsn);
					 
					 lDbDAAmount = (double) (lLngBasicPlusDP.doubleValue() * ((lDbDAPercent) / 100));
				 }
		
		   }
		   else if (("2006 To 2006".equals(lStrRevType[revTypeCntr])) || ("New To 2006".equals(lStrRevType[revTypeCntr]) && "New".equals(lStrOldNewFlag))) {
			   
			   lRltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_2006", lDbOrgBasicPnsn, lDtFromDt);
			   if (lRltPensionHeadcodeRateVO != null && !lRltPensionHeadcodeRateVO.equals("")) {
				   lDbDAPercent = lRltPensionHeadcodeRateVO.getRate().doubleValue();
				   
				   lLngBasicPlusDP = lLngDPAmount + Math.round(lDbOrgBasicPnsn);
				   lDbDAAmount = (double) (lLngBasicPlusDP.doubleValue() * ((lDbDAPercent) / 100));
			   }
			   
		
			}
			/** ############### RE Employment Effect Starts ############## */
			if(lStrDAInPnsnSal.length >0)
			{
				if(!"".equals(lStrDAInPnsnSal[reEmpCntr]))
				{
					if("Salary".equals(lStrDAInPnsnSal[reEmpCntr]))
					{
						if (lDtReEmpFrmDt != null && lDtReEmpToDt != null) {
							lIntReEmpEffDays = getReEmpEffectDays(lDtReEmpFrmDt, lDtReEmpToDt, reEmpCntr, lDtFromDt, lDtToDt);
						}
						lBgDcmlDAAmount = new BigDecimal(lDbDAAmount).setScale(0, BigDecimal.ROUND_UP);
						lDbDAAmount =lBgDcmlDAAmount.doubleValue();
					    lLngDAAmount =(long) (lDbDAAmount * (lLngEffDaysinMonth - lIntReEmpEffDays) / lLngEffDaysinMonth);
					    return lLngDAAmount;
					}
				}
			}
			lBgDcmlDAAmount = new BigDecimal(lDbDAAmount).setScale(0, BigDecimal.ROUND_UP);
			lDbDAAmount =lBgDcmlDAAmount.doubleValue();
			
		}
		catch(Exception e)
		{
			gLogger.error("Error is :: "+ e, e);
			throw e;
		}
		return lDbDAAmount.longValue();
	}
	
	private long calcAllowance(Date lDtFromDtUpdated, Date lDtToDtUpdated, Date[] lDtAllowEffFrom, Date[] lDtAllowEffTo, Double[] lDbAllowanceAmount, int allowCntr, long lLngEffDaysinMonth,
			int lIntTotalDaysInMonth) throws Exception {

		double lDbAllowAmt = 0D;
		Calendar lObjCalendar = null;
		int lIntDaysInMonth = 0;
		int lIntEffDaysForAllow = 0;
		int lIntDaysForAllow = 0;
		try {
			for (int i = allowCntr; i < lDbAllowanceAmount.length; i++) {
				if ((lDtAllowEffFrom[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtAllowEffFrom[i].compareTo(lDtToDtUpdated) <= 0) && (lDtAllowEffTo[i].compareTo(lDtToDtUpdated) > 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtAllowEffFrom[i]);
					lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					lIntDaysForAllow = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForAllow = lIntDaysInMonth - lIntDaysForAllow + 1;
					lDbAllowAmt += (lDbAllowanceAmount[i] / lIntTotalDaysInMonth) * lIntEffDaysForAllow;
				} else if ((lDtAllowEffTo[i].compareTo(lDtToDtUpdated) <= 0) && (lDtAllowEffTo[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtAllowEffFrom[i].compareTo(lDtFromDtUpdated) < 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtAllowEffTo[i]);
					lIntDaysForAllow = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForAllow = lIntDaysForAllow;
					lDbAllowAmt += (lDbAllowanceAmount[i] / lIntTotalDaysInMonth) * lIntEffDaysForAllow;
				} else if ((lDtAllowEffFrom[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtAllowEffTo[i].compareTo(lDtToDtUpdated) <= 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtAllowEffFrom[i]);
					int FirstDate = lObjCalendar.get(Calendar.DATE);

					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtAllowEffTo[i]);
					int ToDate = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForAllow = ToDate - FirstDate + 1;
					lDbAllowAmt += (lDbAllowanceAmount[i] / lIntTotalDaysInMonth) * lIntEffDaysForAllow;
				} else if (lDtAllowEffFrom[i].before(lDtFromDtUpdated) && lDtAllowEffTo[i].after(lDtToDtUpdated)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtFromDtUpdated);
					int FirstDate = lObjCalendar.get(Calendar.DATE);

					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtToDtUpdated);
					int ToDate = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForAllow = ToDate - FirstDate + 1;
					lDbAllowAmt += (lDbAllowanceAmount[i] / lIntTotalDaysInMonth) * lIntEffDaysForAllow;
				}

			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return Math.round(lDbAllowAmt);
	}
	
	public double calcADPAmount(Map inputMap) throws Exception {
		
		Calendar calendar = Calendar.getInstance();
        SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
 
//        System.out.println("Today: " + lObjSimpleDateFormat.format(calendar.getTime()));

		ValidPcodeView lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");
		Date lCurrentDate =  lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(calendar.getTime()));
		Date lMonthStartDt = (Date) inputMap.get("lMonthStartDt");
		Date lMonthEndDate = (Date) inputMap.get("lMonthEndDt");
		Date lPayStartDate = lMonthStartDt; // First Date of month
		Date lPayEndDate = lMonthEndDate; // Last Date of month

		Date lPPOEndDate = lObjValidPcodeVO.getEndDate();
		Date lCommDate = lObjValidPcodeVO.getCommensionDate();
		Date lBirthDate = lObjValidPcodeVO.getDateOfBirth();
		Date lDeathDate = lObjValidPcodeVO.getDateOfDeath();

		int lCurrentyyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCurrentDate));
		int lCommyyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCommDate));
		int lPPOEndYYYYMM = lPPOEndDate != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lPPOEndDate)) : 0;
		
		Double ldBasicAmount = (Double) inputMap.get("BasicPensionAmt");
		
		double ldADPAmount = 0;

		try {
//			if (lCommyyyyMM == lCurrentyyyyMM) {
//				lPayStartDate = lCommDate.after(lMonthStartDt) ? lCommDate : lMonthStartDt; // which
//				// ever
//				// is
//				// later
//			} else {
				lPayStartDate = lMonthStartDt;
//			}

			if (lPPOEndDate != null) {
				if (lPPOEndYYYYMM == lCurrentyyyyMM) {
					lPayEndDate = lPPOEndDate.after(lMonthEndDate) ? lMonthEndDate : lPPOEndDate; // which
					// ever
					// is
					// earlier
				} else {
					lPayEndDate = lMonthEndDate;
				}
			} else {
				lPayEndDate = lMonthEndDate;
			}

			int lPayDays = getDaysDifference(lPayStartDate, lPayEndDate);

			Map inputArgMap = new HashMap();
			inputArgMap.put("lBirthDate", lBirthDate);
			inputArgMap.put("lPayStartDate", lPayStartDate);
			inputArgMap.put("lPayEndDate", lPayEndDate);
			inputArgMap.put("lCurrentyyyyMM", lCurrentyyyyMM);
			inputArgMap.put("lPayDays", lPayDays);
			inputArgMap.put("lCalculatedBasicAmount", ldBasicAmount);

			ldADPAmount = calculateADPAmount(inputArgMap);
			
		
			return ldADPAmount;
		} 
		catch (Exception e) {
		throw e;
		}
	
	}
	
	public double calculateADPAmount(Map inputArgMap) throws Exception {

		Date lBirthDate = (Date) inputArgMap.get("lBirthDate");
		Date lPayStartDate = (Date) inputArgMap.get("lPayStartDate");
		Date lPayEndDate = (Date) inputArgMap.get("lPayEndDate");
		Integer lCurrentyyyyMM = (Integer) inputArgMap.get("lCurrentyyyyMM");
		int lPayDays = getDaysDifference(lPayStartDate, lPayEndDate);
		double ldCalulatedBasicAmount = (Double) inputArgMap.get("lCalculatedBasicAmount");

		double lPayDay80 = 0;
		double lPayDay85 = 0;
		double lPayDay90 = 0;
		double lPayDay95 = 0;
		double lPayDay100 = 0;
		int li80_85 = 20;
		int li85_90 = 30;
		int li90_95 = 40;
		int li95_100 = 50;
		int li100 = 100;

		int liYearsAge = getYearsDifference(lBirthDate, lPayStartDate);
		if (liYearsAge >= 79) {
			Calendar c = Calendar.getInstance();
			c.setTime(lBirthDate);

			c.add(Calendar.YEAR, 80);
			Date lBirthDate80 = c.getTime();
			c.add(Calendar.YEAR, 5);
			Date lBirthDate85 = c.getTime();
			c.add(Calendar.YEAR, 0);
			Date lBirthDate90 = c.getTime();
			c.add(Calendar.YEAR, 5);
			Date lBirthDate95 = c.getTime();
			c.add(Calendar.YEAR, 5);
			Date lBirthDate100 = c.getTime();

			int lBirthYYYYMM80 = lBirthDate80 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate80)) : 0;
			int lBirthYYYYMM85 = lBirthDate85 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate85)) : 0;
			int lBirthYYYYMM90 = lBirthDate90 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate90)) : 0;
			int lBirthYYYYMM95 = lBirthDate95 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate95)) : 0;
			int lBirthYYYYMM100 = lBirthDate100 != null ? Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lBirthDate100)) : 0;

			if (liYearsAge == 79) {
				if (lCurrentyyyyMM == lBirthYYYYMM80) {
					lPayDay80 = getDaysDifference(lBirthDate80, lPayEndDate);
				}
			} else if (liYearsAge == 84) {
				if (lCurrentyyyyMM == lBirthYYYYMM85) {
					if (lBirthDate85.before(lPayEndDate)) {
						lPayDay80 = getDaysDifference(lPayStartDate, addDaysInDate1(lBirthDate85, -1));
						lPayDay85 = getDaysDifference(lBirthDate85, lPayEndDate);
					} else {
						lPayDay80 = getDaysDifference(lPayStartDate, lPayEndDate);
						lPayDay85 = 0;
					}
				} else {
					lPayDay80 = lPayDays;
				}
			} else if (liYearsAge == 89) {
				if (lCurrentyyyyMM == lBirthYYYYMM90) {
					if (lBirthDate90.before(lPayEndDate)) {
						lPayDay85 = getDaysDifference(lPayStartDate, addDaysInDate1(lBirthDate90, -1));
						lPayDay90 = getDaysDifference(lBirthDate90, lPayEndDate);
					} else {
						lPayDay85 = getDaysDifference(lPayStartDate, lPayEndDate);
						lPayDay90 = 0;
					}
				} else {
					lPayDay85 = lPayDays;
				}
			} else if (liYearsAge == 94) {
				if (lCurrentyyyyMM == lBirthYYYYMM95) {
					if (lBirthDate95.before(lPayEndDate)) {
						lPayDay90 = getDaysDifference(lPayStartDate, addDaysInDate1(lBirthDate95, -1));
						lPayDay95 = getDaysDifference(lBirthDate95, lPayEndDate);
					} else {
						lPayDay90 = getDaysDifference(lPayStartDate, lPayEndDate);
						lPayDay95 = 0;
					}
				} else {
					lPayDay90 = lPayDays;
				}
			} else if (liYearsAge == 99) {
				if (lCurrentyyyyMM == lBirthYYYYMM100) {
					if (lBirthDate100.before(lPayEndDate)) {
						lPayDay95 = getDaysDifference(lPayStartDate, addDaysInDate1(lBirthDate100, -1));
						lPayDay100 = getDaysDifference(lBirthDate100, lPayEndDate);
					} else {
						lPayDay95 = getDaysDifference(lPayStartDate, lPayEndDate);
						lPayDay100 = 0;
					}
				} else {
					lPayDay95 = lPayDays;
				}
			} else if (liYearsAge >= 80 && liYearsAge < 85) {
				lPayDay80 = lPayDays;
			} else if (liYearsAge >= 85 && liYearsAge < 90) {
				lPayDay85 = lPayDays;
			} else if (liYearsAge >= 90 && liYearsAge < 95) {
				lPayDay90 = lPayDays;
			} else if (liYearsAge >= 95 && liYearsAge < 100) {
				lPayDay95 = lPayDays;
			} else if (liYearsAge >= 100) {
				lPayDay100 = lPayDays;
			}
		} else {
			// ADP ==0
		}

		return ldCalulatedBasicAmount * ((lPayDay80 * li80_85) + (lPayDay85 * li85_90) + (lPayDay90 * li90_95) + (lPayDay95 * li95_100) + (lPayDay100 * li100)) / (lPayDays * 100);
	}
	
	private static Integer getYearsDifference(Date lDtFrom, Date lDtTo) {

		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(lDtFrom);

		Calendar calTo = Calendar.getInstance();
		calTo.setTime(lDtTo);

		int lYearDiff = calTo.get(Calendar.YEAR) - calFrom.get(Calendar.YEAR);

		calFrom.add(Calendar.YEAR, lYearDiff);
		if (lDtTo.before(calFrom.getTime())) {
			lYearDiff--;
		}

		return lYearDiff;
	}
	
	public static Date addDaysInDate1(Date lDate, int Days) throws Exception {

		Date lNewDate = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(lDate);
			cal.add(Calendar.DATE, Days);
			lNewDate = cal.getTime();
		} catch (Exception e) {
			throw (e);
		}
		return lNewDate;
	}
	
	private Integer getDaysDifference(Date lDtFrom, Date lDtTo) {

		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(lDtFrom);

		Calendar calTo = Calendar.getInstance();
		calTo.setTime(lDtTo);
		Integer lIntDaysDiff = (calTo.get(Calendar.DATE) - calFrom.get(Calendar.DATE) + 1);
		return (lIntDaysDiff > 0) ? lIntDaysDiff : 0;
	}
	
	private int getReEmpEffectDays(Date[] lDtReEmpFrmDt, Date[] lDtReEmpToDt, int reEmpCntr, Date lDtFromDt, Date lDtToDt) throws Exception {

		Calendar lObjCalendar = null;
		int lIntDaysInMonth = 0;
		int lIntEffDaysForTIDeduc = 0;
		int lIntEffDaysInMonth = 0;
		try {
			for (int i = reEmpCntr; i < lDtReEmpFrmDt.length; i++) {
				if ((lDtReEmpFrmDt[i].compareTo(lDtFromDt) >= 0) && (lDtReEmpFrmDt[i].compareTo(lDtToDt) <= 0) && (lDtReEmpToDt[i].compareTo(lDtToDt) > 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtReEmpFrmDt[i]);
					lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					lIntEffDaysInMonth = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForTIDeduc += lIntDaysInMonth - lIntEffDaysInMonth + 1;
				} else if ((lDtReEmpToDt[i].compareTo(lDtToDt) <= 0) && (lDtReEmpToDt[i].compareTo(lDtFromDt) >= 0) && (lDtReEmpFrmDt[i].compareTo(lDtFromDt) < 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtReEmpToDt[i]);
					lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					lIntEffDaysInMonth = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForTIDeduc += lIntEffDaysInMonth;
				} else if ((lDtReEmpFrmDt[i].compareTo(lDtFromDt) >= 0) && (lDtReEmpToDt[i].compareTo(lDtToDt) <= 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtReEmpFrmDt[i]);
					int FirstDate = lObjCalendar.get(Calendar.DATE);

					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtReEmpToDt[i]);
					int ToDate = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForTIDeduc += ToDate - FirstDate + 1;
				} else if (lDtReEmpFrmDt[i].before(lDtFromDt) && lDtReEmpToDt[i].after(lDtToDt)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtFromDt);
					int FirstDate = lObjCalendar.get(Calendar.DATE);

					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtToDt);
					int ToDate = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForTIDeduc += ToDate - FirstDate + 1;
				}
			}
		} catch (Exception e) {

		}
		return lIntEffDaysForTIDeduc;
	}

	private List getRateFromHeadcodeRateRltMap(Map inputMap, Long lLngHeadCode, String lStrFieldType, double lDbPnsnBasic, Date lDtFromDt) throws Exception {

		Object[] lObjarr = null;
		List lLstOfObjArr = null;
		Object[] lObjTemparr = null;
		BigDecimal lBgDcml1750 = new BigDecimal(1750).setScale(2);
		BigDecimal lBgDcml3000 = new BigDecimal(3000).setScale(2);
		BigDecimal lBgDcml999999 = new BigDecimal(999999).setScale(2);
		try {
			SimpleDateFormat lSDFormat = new SimpleDateFormat("yyyy-MM-dd");

			// Key for the map is in the form
			// FieldType ~ FromDate ~ ToDate ~ PK of Rlt_Pension_Headcode_Rate

			if (gMapRateFromHeadCode.get(lLngHeadCode) == null) {
				getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode);
			}

			if (gMapRateFromHeadCode.get(lLngHeadCode) != null) {
				Map lMapResult = gMapRateFromHeadCode.get(lLngHeadCode);
				Set lKeySet = lMapResult.keySet();
				lLstOfObjArr = new ArrayList();
				for (Object lObjkeySet : lKeySet) {
					String lStrKey = lObjkeySet.toString();
					String[] lStrArrKey = lStrKey.split("~");
					String lStrFieldTypeFrmDB = lStrArrKey[0];
					Date lDtEffFrmDt = null;
					if (!"".equals(lStrArrKey[1]) && lStrArrKey[1] != null) {
						lDtEffFrmDt = lSDFormat.parse(lStrArrKey[1]);
					}
					Date lDtEffToDt = null;
					if (!"".equals(lStrArrKey[2]) && !"null".equals(lStrArrKey[2])) {
						lDtEffToDt = lSDFormat.parse(lStrArrKey[2]);
					}

					if (lStrFieldType != "IR"
							&& lStrFieldType.equals(lStrFieldTypeFrmDB)
							&& ((lDtEffFrmDt == null && (lDtFromDt.before(lDtEffToDt) || lDtFromDt.equals(lDtEffToDt))) || (lDtFromDt.equals(lDtEffFrmDt))
									|| (lDtFromDt.after(lDtEffFrmDt) && lDtEffToDt == null) || (lDtFromDt.after(lDtEffFrmDt) && lDtFromDt.before(lDtEffToDt)))) {
						if (lStrFieldType != "DA_1986") {
							lObjarr = (Object[]) lMapResult.get(lObjkeySet);
							lLstOfObjArr.add(lObjarr);
							break;
						} else {
							if (lDbPnsnBasic <= 1750) {
								lObjTemparr = (Object[]) lMapResult.get(lObjkeySet);
								if (lBgDcml1750.equals(lObjTemparr[6])) {
									lObjarr = lObjTemparr;
									lLstOfObjArr.add(lObjarr);
									break;
								}
							} else if (lDbPnsnBasic <= 3000) {
								lObjTemparr = (Object[]) lMapResult.get(lObjkeySet);
								if (lBgDcml3000.equals(lObjTemparr[6])) {
									lObjarr = lObjTemparr;
									lLstOfObjArr.add(lObjarr);
									break;
								}
							} else {
								lObjTemparr = (Object[]) lMapResult.get(lObjkeySet);
								if (lBgDcml999999.equals(lObjTemparr[6])) {
									lObjarr = lObjTemparr;
									lLstOfObjArr.add(lObjarr);
									break;
								}
							}
						}
					} else if (lStrFieldType == "IR" && lStrFieldType.equals(lStrFieldTypeFrmDB)) {
						lObjTemparr = (Object[]) lMapResult.get(lObjkeySet);
						lLstOfObjArr.add(lObjTemparr);
						if (lLstOfObjArr.size() == 2) {
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return lLstOfObjArr;
		// return lObjarr;
	}

	private Void getRateFromHeadcodeRateRltMap(Map inputMap, Long lLngHeadCode) throws Exception {

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		Map lResultMap = null;
		NewPensionBillDAO lPensionBillDAO = new NewPensionBillDAOImpl(servLoc.getSessionFactory());
		try {
			lResultMap = lPensionBillDAO.getRateFromHeadcodeRateRlt(lLngHeadCode);

			gMapRateFromHeadCode = new HashMap<Long, Map<String, Object>>();
			gMapRateFromHeadCode.put(lLngHeadCode, lResultMap);
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return null;
	}

	private String getChar(int count, String ele) {

		StringBuffer lSBSpace = new StringBuffer();
		for (int i = 0; i < count-1; i++) {
			lSBSpace.append(ele);
		}
		return lSBSpace.toString();
	}

	public ResultObject saveRevisedArrear(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPensionArrearDtls lObjTrnPensionArrearDtls = null;
		TrnPensionArrearDtlsDAO lObjPensionArrearDtlsDAO = new TrnPensionArrearDtlsDAOImpl(TrnPensionArrearDtls.class, serv.getSessionFactory());

		try {
			lObjTrnPensionArrearDtls = (TrnPensionArrearDtls) inputMap.get("lObjTrnPensionArrearDtls");

			inputMap.put("isSave", true);
			String lStrSupplyFlg = StringUtility.getParameter("SupplyFlg", request);
			BigDecimal lBgDcmlRevArrearAmt = lObjTrnPensionArrearDtls.getDifferenceAmount();

			if (lObjTrnPensionArrearDtls != null && !"Y".equals(lStrSupplyFlg) && (lBgDcmlRevArrearAmt.compareTo(BigDecimal.ZERO) >= 0)) {
				Long lLngArrearDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_arrear_dtls", inputMap);

				Long lAttachmentID = 0L;
				if (StringUtility.getParameter("hidPrintString", request) != null && StringUtility.getParameter("hidPrintString", request).toString().length() > 0) {
					lAttachmentID = saveAttachmentTxtFile(inputMap);
					lObjTrnPensionArrearDtls.setArrearAttacmentId(lAttachmentID > 0 ? lAttachmentID : null);
				}

				lObjTrnPensionArrearDtls.setPensionArrearDtlsId(lLngArrearDtlsId);

				lObjPensionArrearDtlsDAO.create(lObjTrnPensionArrearDtls);
			}

			StringBuilder lStrRes = new StringBuilder();
			lStrRes.append(" <RVSAMT> ");
			lStrRes.append(" <FLAG> ");
			lStrRes.append(lStrSupplyFlg);
			lStrRes.append(" </FLAG> ");
			lStrRes.append(" <AMOUNT> ");
			lStrRes.append(lObjTrnPensionArrearDtls.getTotalDifferenceAmt());
			lStrRes.append(" </AMOUNT> ");
			lStrRes.append(" </RVSAMT> ");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrRes.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
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

	private Long saveAttachmentTxtFile(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		byte[] theByteArray = null;
		Long lLngAttDocMst = 0L;
		Long lAttDocSrNo = 0L;
		Long lAttachmentID = 0L;

		try {
			setSessionInfo(inputMap);

			String lStrPrintString = StringUtility.getParameter("hidPrintString", request);

			String lStrNEWPrintString = "<div><pre>" + lStrPrintString + "</pre></div>";
			lStrPrintString = null;

			theByteArray = lStrNEWPrintString.getBytes();

			CmnDatabaseMstDaoImpl cmnDatabaseMstDaoImpl = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class, serv.getSessionFactory());
			CmnLocationMstDaoImpl lObjLocationMstDaoImpl = new CmnLocationMstDaoImpl(CmnLocationMst.class, serv.getSessionFactory());
			CmnLookupMstDAOImpl lObjLookupMstDAOImpl = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());
			// Saving into Attachment Master.
			CmnAttachmentMst lObjAttachmentMst = new CmnAttachmentMst();
			CmnAttachmentMstDAOImpl lObjAttMstDAOImpl = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
			lAttachmentID = IFMSCommonServiceImpl.getNextSeqNum("cmn_attachment_mst", inputMap);
			lObjAttachmentMst.setAttachmentId(lAttachmentID);
			lObjAttachmentMst.setCmnDatabaseMst(cmnDatabaseMstDaoImpl.read(gLngDBId));
			lObjAttachmentMst.setCmnLocationMst(lObjLocationMstDaoImpl.read(glLocId));
			lObjAttachmentMst.setCreatedBy(gLngUserId);
			lObjAttachmentMst.setCreatedByPost(gLngPostId);
			lObjAttachmentMst.setCreatedDate(gCurrDate);
			lObjAttachmentMst.setActivateFlag("Y");
			lObjAttachmentMst.setTrnCounter(1);
			lObjAttMstDAOImpl.create(lObjAttachmentMst);
			// Saving into cmn_attachment_mpg mapping table
			CmnAttachmentMpg lObjAttachmentMpg = new CmnAttachmentMpg();
			CmnAttachmentMpgDAOImpl lObjMpgDAOImpl = new CmnAttachmentMpgDAOImpl(CmnAttachmentMpg.class, serv.getSessionFactory());
			lAttDocSrNo = IFMSCommonServiceImpl.getNextSeqNum("cmn_attachment_mpg", inputMap);
			lObjAttachmentMpg.setSrNo(lAttDocSrNo);
			lObjAttachmentMpg.setCmnDatabaseMst(cmnDatabaseMstDaoImpl.read(gLngDBId));
			lObjAttachmentMpg.setCmnLocationMst(lObjLocationMstDaoImpl.read(glLocId));
			lObjAttachmentMpg.setCmnAttachmentMst(lObjAttMstDAOImpl.read(lAttachmentID));
			lObjAttachmentMpg.setCmnLookupMst(lObjLookupMstDAOImpl.read(100062L));
			lObjAttachmentMpg.setAttachmentDesc("Revised Arrear Calculated Sheet");
			lObjAttachmentMpg.setCreatedBy(gLngUserId);
			lObjAttachmentMpg.setCreatedByPost(gLngPostId);
			lObjAttachmentMpg.setCreatedDate(gCurrDate);
			lObjAttachmentMpg.setActivateFlag("Y");
			lObjAttachmentMpg.setTrnCounter(1);
			lObjMpgDAOImpl.create(lObjAttachmentMpg);

			// Saving Actual ECS file into cmn_attdoc_mst table
			CmnAttdocMst lObjAttdocMst = new CmnAttdocMst();
			CmnAttdocMstDAOImpl lObjAttdocMstDAOImpl = new CmnAttdocMstDAOImpl(CmnAttdocMst.class, serv.getSessionFactory());
			lLngAttDocMst = IFMSCommonServiceImpl.getNextSeqNum("cmn_attdoc_mst", inputMap);
			lObjAttdocMst.setAttdocId(lLngAttDocMst);
			lObjAttdocMst.setCmnDatabaseMst(cmnDatabaseMstDaoImpl.read(gLngDBId));
			lObjAttdocMst.setCmnLocationMst(lObjLocationMstDaoImpl.read(glLocId));
			lObjAttdocMst.setCmnAttachmentMpg(lObjMpgDAOImpl.read(lAttDocSrNo));
			lObjAttdocMst.setFinalAttachment(theByteArray);
			lObjAttdocMst.setCreatedBy(gLngUserId);
			lObjAttdocMst.setCreatedByPost(gLngPostId);
			lObjAttdocMst.setCreatedDate(gCurrDate);
			// lObjAttdocMst.setAttdocId(IFMSCommonServiceImpl.getNextSeqNum("cmn_attdoc_mst",
			// inputMap));
			lObjAttdocMst.setTrnCounter(1);
			lObjAttdocMstDAOImpl.create(lObjAttdocMst);

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lLngAttDocMst;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.RevisedArrearCalcService#getPensionerDtlsFromPPONo(java.util.Map)
	 */
	public ResultObject getPensionerDtlsFromPPONo(Map<String, Object> inputMap) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		setSessionInfo(inputMap);
		List lLstPensionerDtls = new ArrayList();
		
		String lStrPensionRequestId=null;
		String lStrPensionerCode=null;
		String lStrPpoNo=null;
		String lStrLinkedPpoNo=null;
		String lStrDaRateForState=null;
		String lStrPercentage=null;
		String lStrFMDateOfBirth=null;
		String lStrFMDateOfDeath="";
		String lStrPnsnrDateOfBirth=null;
		String lStrPnsnrDateOfDeath="";
		String lStrBasicPensionAmount=null;
		String lStrCommencementDate=null;
		String lStrFP1Date=null;
		String lStrFP2Date=null;
		String lStrFP1Amount="";
		String lStrFP2Amount="";
		String lStrCvpMonthlyAmount=null;
		String lStrPpoEndDate=null;
		String lStrEffToDate="";
		String lStrRetirementDate=null;
		String lStrPnsnrName=null;
		String lStrBankName=null;
		String lStrBranchName=null;
		String lStrAccountNo=null;
		StringBuilder lStrBldXML = null;
		String lStrBankCode=null;
		String lStrBranchCode=null;
		String lStrReEmpFromDate=null;
		String lStrReEmpToDate=null;
		String lStrDaInPnsnSal=null;
		String lStrVolumeNo = "";
		String lStrPageNo = "";
		String lStrTreasuryName = "";
		Calendar lObjCalendar = Calendar.getInstance();
		
		lObjCalendar.set(lObjCalendar.get(lObjCalendar.YEAR),lObjCalendar.get(lObjCalendar.MONTH),1);
		
		//System.out.println("current date="+lObjSimpleDateFormat.format(lObjCalendar.getTime()));

		try {
			RevisedArrearCalcDAO lObjRevisedArrearCalcDAO =new RevisedArrearCalcDAOImpl(serv.getSessionFactory());
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstReEmploymentDtls.class, serv.getSessionFactory());
			List<HstReEmploymentDtls> lLstHstReEmploymentDtlsVO =new ArrayList<HstReEmploymentDtls>();
			List<TrnPensionCutDtls> lLstTrnPensionCutDtlsVO = new ArrayList<TrnPensionCutDtls>();
			
			lStrPpoNo = StringUtility.getParameter("PpoNo", request);
			lStrBankCode = StringUtility.getParameter("bankCode", request);
			lStrBranchCode = StringUtility.getParameter("branchCode", request);
			lStrAccountNo = StringUtility.getParameter("accountNo", request);
			
			lLstPensionerDtls=lObjRevisedArrearCalcDAO.getPensionerDtlsFromPPONo(lStrPpoNo,lStrBankCode,lStrBranchCode,lStrAccountNo,gStrLocationCode);	
					
			if(!lLstPensionerDtls.isEmpty())
			{
				Object[] obj=(Object[])lLstPensionerDtls.get(0);
				if(obj[0] != null)
				{
					lStrPensionRequestId=obj[0].toString();
				}
				if(obj[1] !=null)
				{
					lStrPensionerCode=obj[1].toString();
				}
				if(obj[2] !=null)
				{
					lStrPpoNo=obj[2].toString();
				}
				if(obj[3] !=null)
				{
					lStrLinkedPpoNo=obj[3].toString();
				}
				if(obj[4] !=null)
				{
					lStrDaRateForState=obj[4].toString();
				}
				if(obj[5] !=null)
				{
					lStrPnsnrDateOfBirth=lObjSimpleDateFormat.format(obj[5]);
					
				}
				if(obj[6] !=null)
				{
					lStrPnsnrDateOfDeath=lObjSimpleDateFormat.format(obj[6]);
					
				}
				if(obj[7] !=null)
				{
					lStrBasicPensionAmount=obj[7].toString();
					
				}
				if(obj[8] !=null)
				{
					lStrCommencementDate=lObjSimpleDateFormat.format(obj[8]);
				}
				if(obj[9] !=null)
				{
					lStrFP1Date=lObjSimpleDateFormat.format(obj[9]);
				}
				if(obj[10] !=null)
				{
					lStrFP2Date=lObjSimpleDateFormat.format(obj[10]);
				}
				if(obj[11] !=null)
				{
					lStrFP1Amount=obj[11].toString();
				}
				if(obj[12] !=null)
				{
					lStrFP2Amount=obj[12].toString();
				}
				if(obj[13] !=null)
				{
					lStrCvpMonthlyAmount=obj[13].toString();
				}
				if(obj[14] !=null)
				{
					lStrPpoEndDate=obj[14].toString();
				}
				if(obj[15] !=null)
				{
					lStrRetirementDate=lObjSimpleDateFormat.format(obj[15]);
				}
				if(obj[16] !=null)
				{
					if(obj[20] !=null)
					{
						if("Y".equals(obj[20].toString())){
							lStrPnsnrName=obj[16].toString();
						}
						else
						{
							if(obj[21] !=null)
							{
								lStrPnsnrName=obj[21].toString();
							}
						}
					}
					
				}
				if(obj[17] !=null)
				{
					lStrFMDateOfBirth=lObjSimpleDateFormat.format(obj[17]);
				}
				if(obj[18] !=null)
				{
					lStrFMDateOfDeath=lObjSimpleDateFormat.format(obj[18]);
				}
				if(obj[19] !=null)
				{
					lStrPercentage=obj[19].toString();
				}
				if(obj[22] !=null)
				{
					lStrBankName=obj[22].toString();
				}
				if(obj[23] !=null)
				{
					lStrBranchName=obj[23].toString();
				}
				if(obj[24] !=null)
				{
					lStrAccountNo=obj[24].toString();
				}
				if(obj[25] !=null)
				{
					lStrReEmpFromDate=lObjSimpleDateFormat.format(obj[25]);
				}
				if(obj[26] !=null)
				{
					lStrReEmpToDate=lObjSimpleDateFormat.format(obj[26]);
				}
				if(obj[27] !=null)
				{
					lStrDaInPnsnSal=obj[27].toString();
				}
				if(obj[28] !=null)
				{
					lStrVolumeNo=obj[28].toString();
				}
				if(obj[29] !=null)
				{
					lStrPageNo=obj[29].toString();
				}
				if(obj[30] !=null)
				{
					lStrTreasuryName=obj[30].toString();
				}
				lLstHstReEmploymentDtlsVO=lObjPhysicalCaseInwardDAO.getHstReEmploymentDtlsVO(lStrPensionerCode);
				
				lLstTrnPensionCutDtlsVO = lObjPhysicalCaseInwardDAO.getPensionCutDtls(lStrPensionerCode);
//				 String lastbillmonyr =
//					 lObjRevisedArrearCalcDAO.getMaxOfForMonth(lStrPensionerCode);
//					 if (lastbillmonyr != null && !lastbillmonyr.equals("")) {
//					 String mon = lastbillmonyr.substring(4);
//					 String yr = lastbillmonyr.substring(0, 4);
//					
//					 String day = "31";
//					
//					 if (mon.equals("02")) {
//					 day = "28";
//					 } else if (mon.equals("04") || mon.equals("06") ||
//					 mon.equals("09") || mon.equals("11")) {
//					 day = "30";
//					 }
//					 lStrEffToDate = day + "/" + mon + "/" + yr;
//				}
				if(lStrPnsnrName != null && !"".equals(lStrPnsnrName))
				{
					inputMap.put("PensionRequestId", lStrPensionRequestId);
					inputMap.put("PensionerCode", lStrPensionerCode);
					inputMap.put("PpoNo", lStrPpoNo);
					inputMap.put("LinkedPpoNo", lStrLinkedPpoNo);
					inputMap.put("HeadCode", lStrDaRateForState);
					inputMap.put("FMDateOfBirth", lStrFMDateOfBirth);
					inputMap.put("FMDateOfDeath", lStrFMDateOfDeath);
					inputMap.put("PnsnrDateOfBirth", lStrPnsnrDateOfBirth);
					inputMap.put("PnsnrDateOfDeath", lStrPnsnrDateOfDeath);
					inputMap.put("BasicPensionAmount", lStrBasicPensionAmount);
					inputMap.put("CommencementDate", lStrCommencementDate);
					inputMap.put("FP1Date", lStrFP1Date);
					inputMap.put("FP2Date", lStrFP2Date);
					inputMap.put("FP1Amount", lStrFP1Amount);
					inputMap.put("FP2Amount", lStrFP2Amount);
					inputMap.put("CvpMonthlyAmount", lStrCvpMonthlyAmount);
					inputMap.put("PpoEndDate", lStrPpoEndDate);
					//inputMap.put("EffToDate",lStrEffToDate);
					inputMap.put("RetirementDate",lStrRetirementDate);
					inputMap.put("PensionerName",lStrPnsnrName);
					inputMap.put("BankName",lStrBankName);
					inputMap.put("BranchName",lStrBranchName);
					inputMap.put("AccountNo",lStrAccountNo);
					inputMap.put("ReEmpFromDate",lStrReEmpFromDate);
					inputMap.put("ReEmpToDate",lStrReEmpToDate);
					inputMap.put("DaInPnsnSal",lStrDaInPnsnSal);
					inputMap.put("lLstHstReEmploymentDtlsVO", lLstHstReEmploymentDtlsVO);
					inputMap.put("lLstTrnPensionCutDtlsVO", lLstTrnPensionCutDtlsVO);
					inputMap.put("CurrentDate", lObjSimpleDateFormat.format(lObjCalendar.getTime()));
					inputMap.put("VolumeNo", lStrVolumeNo);
					inputMap.put("PageNo", lStrPageNo);
					inputMap.put("TreasuryName", lStrTreasuryName);
				}
				
			}
			
			resObj.setResultValue(inputMap);
			
			lStrBldXML = getResponseXMLDoc(inputMap);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Map<String, Object> inputMap) {

			StringBuilder lStrHidPKs = new StringBuilder();
			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			List<HstReEmploymentDtls> lLstHstReEmploymentDtlsVO =new ArrayList<HstReEmploymentDtls>();
			List<TrnPensionCutDtls> lLstTrnPensionCutDtlsVO = new ArrayList<TrnPensionCutDtls>();
			TrnPensionCutDtls lObjTrnPensionCutDtls = new TrnPensionCutDtls();
			HstReEmploymentDtls lObjHstReEmploymentDtlsVO = new HstReEmploymentDtls();
			if(inputMap.containsKey("PensionRequestId"))
			{
				lStrHidPKs.append("<XMLDOC>");
				lStrHidPKs.append("<PNSNRQSTID>" + inputMap.get("PensionRequestId"));
				lStrHidPKs.append("</PNSNRQSTID>");
				lStrHidPKs.append("<PNSNRCODE>" + inputMap.get("PensionerCode"));
				lStrHidPKs.append("</PNSNRCODE>");
				lStrHidPKs.append("<PPONO><![CDATA[" + inputMap.get("PpoNo"));
				lStrHidPKs.append("]]></PPONO>");
				lStrHidPKs.append("<LINKEDPPONO>" + inputMap.get("LinkedPpoNo"));  //unused
				lStrHidPKs.append("</LINKEDPPONO>");
				lStrHidPKs.append("<HEADCODE>" + inputMap.get("HeadCode"));
				lStrHidPKs.append("</HEADCODE>");
				lStrHidPKs.append("<FMDATEOFBIRTH>" + inputMap.get("FMDateOfBirth"));
				lStrHidPKs.append("</FMDATEOFBIRTH>");
				lStrHidPKs.append("<FMDATEOFDEATH>" + inputMap.get("FMDateOfDeath"));
				lStrHidPKs.append("</FMDATEOFDEATH>");
				lStrHidPKs.append("<PNSNRDOB>" + inputMap.get("PnsnrDateOfBirth"));
				lStrHidPKs.append("</PNSNRDOB>");
				lStrHidPKs.append("<PNSNRDOD>" + inputMap.get("PnsnrDateOfDeath"));
				lStrHidPKs.append("</PNSNRDOD>");
				lStrHidPKs.append("<BASICPNSNAMT>" + inputMap.get("BasicPensionAmount"));
				lStrHidPKs.append("</BASICPNSNAMT>");
				lStrHidPKs.append("<COMNTDATE>" + inputMap.get("CommencementDate"));
				lStrHidPKs.append("</COMNTDATE>");
				lStrHidPKs.append("<FP1DATE>" + inputMap.get("FP1Date"));
				lStrHidPKs.append("</FP1DATE>");
				lStrHidPKs.append("<FP2DATE>" + inputMap.get("FP2Date"));
				lStrHidPKs.append("</FP2DATE>");
				lStrHidPKs.append("<FP1AMOUNT>" + inputMap.get("FP1Amount"));
				lStrHidPKs.append("</FP1AMOUNT>");
				lStrHidPKs.append("<FP2AMOUNT>" + inputMap.get("FP2Amount"));
				lStrHidPKs.append("</FP2AMOUNT>");
				lStrHidPKs.append("<CVPMONTHLYAMT>" + inputMap.get("CvpMonthlyAmount"));  //unused
				lStrHidPKs.append("</CVPMONTHLYAMT>");
				lStrHidPKs.append("<PPOENDDATE>" + inputMap.get("PpoEndDate"));
				lStrHidPKs.append("</PPOENDDATE>");
	//			lStrHidPKs.append("<EFFTODATE>" + inputMap.get("EffToDate"));
	//			lStrHidPKs.append("</EFFTODATE>");
				lStrHidPKs.append("<RETDATE>" + inputMap.get("RetirementDate"));
				lStrHidPKs.append("</RETDATE>");
				lStrHidPKs.append("<PNSNRNAME>" + inputMap.get("PensionerName"));
				lStrHidPKs.append("</PNSNRNAME>");
				lStrHidPKs.append("<BANKNAME><![CDATA[" + inputMap.get("BankName"));
				lStrHidPKs.append("]]></BANKNAME>");
				lStrHidPKs.append("<BRANCHNAME><![CDATA[" + inputMap.get("BranchName"));
				lStrHidPKs.append("]]></BRANCHNAME>");
				lStrHidPKs.append("<ACCOUNTNO>" + inputMap.get("AccountNo"));
				lStrHidPKs.append("</ACCOUNTNO>");
				lStrHidPKs.append("<CURRENTDATE>" + inputMap.get("CurrentDate"));
				lStrHidPKs.append("</CURRENTDATE>");
				lStrHidPKs.append("<VOLUMENO>" + inputMap.get("VolumeNo"));
				lStrHidPKs.append("</VOLUMENO>");
				lStrHidPKs.append("<PAGENO>" + inputMap.get("PageNo"));
				lStrHidPKs.append("</PAGENO>");
				lStrHidPKs.append("<TREASURYNAME><![CDATA[" + inputMap.get("TreasuryName"));
				lStrHidPKs.append("]]></TREASURYNAME>");
			    //Re-Employment Details					
				lStrHidPKs.append("<REEMPDTLS>");

				lLstHstReEmploymentDtlsVO=(List<HstReEmploymentDtls>) inputMap.get("lLstHstReEmploymentDtlsVO");
								
				for(int lIntCnt=0;lIntCnt<lLstHstReEmploymentDtlsVO.size();lIntCnt++)
				{
					lObjHstReEmploymentDtlsVO = lLstHstReEmploymentDtlsVO.get(lIntCnt);
					lStrHidPKs.append("<REEMPFROMDT>" + lObjSimpleDateFormat.format(lObjHstReEmploymentDtlsVO.getFromDate()));
					lStrHidPKs.append("</REEMPFROMDT>");
					lStrHidPKs.append("<REEMPTODT>");
					if(lObjHstReEmploymentDtlsVO.getToDate() !=null)
					{
					lStrHidPKs.append(lObjSimpleDateFormat.format(lObjHstReEmploymentDtlsVO.getToDate()));
					}
					else
					{
						lStrHidPKs.append("");
					}
					lStrHidPKs.append("</REEMPTODT>");
					lStrHidPKs.append("<DAINPNSNSAL>" + lObjHstReEmploymentDtlsVO.getDaInPensionSalary());
					lStrHidPKs.append("</DAINPNSNSAL>");
				}
				if(inputMap.get("ReEmpFromDate")!=null)
				{
					lStrHidPKs.append("<REEMPFROMDT>" + inputMap.get("ReEmpFromDate"));
					lStrHidPKs.append("</REEMPFROMDT>");
					lStrHidPKs.append("<REEMPTODT>" + inputMap.get("ReEmpToDate"));
					lStrHidPKs.append("</REEMPTODT>");
					lStrHidPKs.append("<DAINPNSNSAL>" + inputMap.get("DaInPnsnSal"));
					lStrHidPKs.append("</DAINPNSNSAL>");
				}
				lStrHidPKs.append("</REEMPDTLS>");
				
				//Punishment Cut Details
				lStrHidPKs.append("<PUNISHCUTDTLS>");
				lLstTrnPensionCutDtlsVO =(List<TrnPensionCutDtls>)inputMap.get("lLstTrnPensionCutDtlsVO");
				for(int lIntCnt=0;lIntCnt<lLstTrnPensionCutDtlsVO.size();lIntCnt++)
				{
					lObjTrnPensionCutDtls = lLstTrnPensionCutDtlsVO.get(lIntCnt);
					lStrHidPKs.append("<PUNISHAMT>" + lObjTrnPensionCutDtls.getAmount());
					lStrHidPKs.append("</PUNISHAMT>");
					lStrHidPKs.append("<PUNISHFROMDT>" + lObjSimpleDateFormat.format(lObjTrnPensionCutDtls.getFromDate()));
					lStrHidPKs.append("</PUNISHFROMDT>");
					lStrHidPKs.append("<PUNISHTODT>");
					if(lObjTrnPensionCutDtls.getToDate() !=null)
					{
					lStrHidPKs.append(lObjSimpleDateFormat.format(lObjTrnPensionCutDtls.getToDate()));
					}
					else
					{
						lStrHidPKs.append("");
					}
					lStrHidPKs.append("</PUNISHTODT>");
					
				}
				lStrHidPKs.append("</PUNISHCUTDTLS>");
				
				lStrHidPKs.append("</XMLDOC>");
			}
			else
			{
				lStrHidPKs.append("<XMLDOC>");
				lStrHidPKs.append("<ISEMPTY>");
				lStrHidPKs.append("Y");
				lStrHidPKs.append("</ISEMPTY>");
				lStrHidPKs.append("</XMLDOC>");
			}
			gLogger.info("lStrHidPKs : " + lStrHidPKs);
			return lStrHidPKs;
	}
	
	
	private long calcTI(Map inputMap, String[] lStrRevType, int revTypeCntr, Date lDtFromDt, Date lDtToDt, long lLngHeadCode, double lDbPnsnBasic, String[] lStrDP, int DPCntr, long lLngDPAmount,
			Date[] lDtReEmpFrmDt, Date[] lDtReEmpToDt, int reEmpCntr, Double[] lDbOrgBasicPnsn, int basicCntr, String lStrOldNewFlag, long lLngDPYesDays, long lLngEffDaysinMonth,
			int lIntTotalDaysInMonth) throws Exception {

		Double lDbTIAmount = 0D;
		Long lLngTIAmount = Long.valueOf(0);
		int lIntReEmpEffDays = 0;
		String lStrFieldTypeTI = "TI";
		Object[] lObjArr = null;
		Calendar lObjCalendar = null;
		List lLstOfObjArr = null;
		try {
			if (("".equals(lDtReEmpFrmDt[reEmpCntr]) || lDtReEmpFrmDt[reEmpCntr] == null) || !(lDtReEmpFrmDt[reEmpCntr].before(lDtFromDt) && lDtReEmpToDt[reEmpCntr].after(lDtToDt))) // wont
			// go
			// inside
			// if
			// for
			// full
			// month
			// pensioner
			// is
			// re-employed
			{
				if (lStrRevType[revTypeCntr].equals("Old To Old")) {
					lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, lStrFieldTypeTI, lDbOrgBasicPnsn[basicCntr], lDtFromDt);
					if (!lLstOfObjArr.isEmpty()) {
						lObjArr = (Object[]) lLstOfObjArr.get(0);
						if (lObjArr != null) {
							lDbTIAmount = getGreaterTIAmt(lObjArr, lDbPnsnBasic, lLngDPAmount, lLngEffDaysinMonth, lIntTotalDaysInMonth);
						}
					}
				} else if (lStrRevType[revTypeCntr].equals("Old To New")) {
					if (lDtFromDt.before(IFMSCommonServiceImpl.getDateFromString("01/01/1996")) && lDtFromDt.after(IFMSCommonServiceImpl.getDateFromString("31/12/1985"))) // Slab
					// 1
					// Case
					// before
					// 1996
					{
						lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, lStrFieldTypeTI, lDbOrgBasicPnsn[basicCntr], lDtFromDt);
						if (!lLstOfObjArr.isEmpty()) {
							lObjArr = (Object[]) lLstOfObjArr.get(0);
							if (lObjArr != null) {
								lDbTIAmount = getGreaterTIAmt(lObjArr, lDbPnsnBasic, lLngDPAmount, lLngEffDaysinMonth, lIntTotalDaysInMonth);
							}
						}
					} else if (lDtFromDt.after(IFMSCommonServiceImpl.getDateFromString("31/12/1995"))) {
						if ("Old".equals(lStrOldNewFlag)) // Slab 2 Case from
						// 1996 onwards upto
						// 2006 for old
						{
							lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, lStrFieldTypeTI, lDbOrgBasicPnsn[basicCntr], lDtFromDt);
							if (!lLstOfObjArr.isEmpty()) {
								lObjArr = (Object[]) lLstOfObjArr.get(0);
								if (lObjArr != null) {
									lDbTIAmount = getGreaterTIAmt(lObjArr, lDbPnsnBasic, lLngDPAmount, lLngEffDaysinMonth, lIntTotalDaysInMonth);
								}
							}
						} else if ("New".equals(lStrOldNewFlag)) // Slab 2 Case
						// from 1996
						// onwards
						// upto 2006
						// for news
						{
							lDbTIAmount = getRTISTIAmt(inputMap, lLngHeadCode, lDbOrgBasicPnsn, basicCntr, lDtFromDt, lLngDPAmount, lDbPnsnBasic, lLngDPYesDays, lLngEffDaysinMonth,
									lIntTotalDaysInMonth, lStrOldNewFlag, lStrRevType[revTypeCntr]);
						}
					}
				} else if (lStrRevType[revTypeCntr].equals("New To New")) {
					lDbTIAmount = getRTISTIAmt(inputMap, lLngHeadCode, lDbOrgBasicPnsn, basicCntr, lDtFromDt, lLngDPAmount, lDbPnsnBasic, lLngDPYesDays, lLngEffDaysinMonth, lIntTotalDaysInMonth,
							lStrOldNewFlag, lStrRevType[revTypeCntr]);

				} else if (lStrRevType[revTypeCntr].equals("New To 2006")) {
					lDbTIAmount = getRTISTIAmt(inputMap, lLngHeadCode, lDbOrgBasicPnsn, basicCntr, lDtFromDt, lLngDPAmount, lDbPnsnBasic, lLngDPYesDays, lLngEffDaysinMonth, lIntTotalDaysInMonth,
							lStrOldNewFlag, lStrRevType[revTypeCntr]);
				} else if (lStrRevType[revTypeCntr].equals("2006 To 2006")) {
					lDbTIAmount = getRTISTIAmt(inputMap, lLngHeadCode, lDbOrgBasicPnsn, basicCntr, lDtFromDt, lLngDPAmount, lDbPnsnBasic, lLngDPYesDays, lLngEffDaysinMonth, lIntTotalDaysInMonth,
							lStrOldNewFlag, lStrRevType[revTypeCntr]);
				}

				/** ############### RE Employment Effect Starts ############## */

				if (lDtReEmpFrmDt != null && lDtReEmpToDt != null) {
					lIntReEmpEffDays = getReEmpEffectDays(lDtReEmpFrmDt, lDtReEmpToDt, reEmpCntr, lDtFromDt, lDtToDt);
				}

				lLngTIAmount = Math.round(lDbTIAmount * (lLngEffDaysinMonth - lIntReEmpEffDays) / lLngEffDaysinMonth);

				/** ############### RE Employment Effect Starts ############## */

			}
		} catch (Exception e) {
			// gLogger.error("Error is :: " + e,e);
			// throw(e);
		}
		return lLngTIAmount;
	}

	private Double getRTISTIAmt(Map inputMap, Long lLngHeadCode, Double[] lDbOrgBasicPnsn, int basicCntr, Date lDtFromDt, Long lLngDPAmount, Double lDbPnsnBasic, Long lLngDPYesDays,
			long lLngEffDaysinMonth, int lIntTotalDaysInMonth, String lStrOldNewFlag, String lStrRevType) throws Exception {

		String lStrFieldTypeSTI = "STI";
		String lStrFieldTypeRTI = "RTI";
		String lStrFieldTypeTI06 = "TI_06";
		List lLstOfObjArr = null;
		Object[] lObjArr = null;
		Long lLngBasicPlusDP = Long.valueOf(0);
		Double lDbTIAmount = 0D;
		try {
			if ("New To New".equals(lStrRevType) || ("New To 2006".equals(lStrRevType) && "Old".equals(lStrOldNewFlag)) || ("Old To New".equals(lStrRevType) && "New".equals(lStrOldNewFlag))) {
				lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, lStrFieldTypeSTI, lDbOrgBasicPnsn[basicCntr], lDtFromDt);
				if (!lLstOfObjArr.isEmpty()) {
					lObjArr = (Object[]) lLstOfObjArr.get(0);
					if (lObjArr != null) {
						Double lSTIPer = ((BigDecimal) lObjArr[4]).doubleValue();
						lLngBasicPlusDP = lLngDPAmount + Math.round(lDbPnsnBasic * lLngDPYesDays / lLngEffDaysinMonth);
						lDbTIAmount = (lLngBasicPlusDP.doubleValue() * ((lSTIPer) / 100));
					}
				}

				lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, lStrFieldTypeRTI, lDbOrgBasicPnsn[basicCntr], lDtFromDt);

				if (!lLstOfObjArr.isEmpty()) {
					lObjArr = (Object[]) lLstOfObjArr.get(0);
					if (lObjArr != null) {
						Double lRTIPer = ((BigDecimal) lObjArr[4]).doubleValue();
						lDbTIAmount += Math.round(lDbPnsnBasic * (lLngEffDaysinMonth - lLngDPYesDays) / lLngEffDaysinMonth) * (lRTIPer / 100);
					}
				}
			} else if ("2006 To 2006".equals(lStrRevType) || ("New To 2006".equals(lStrRevType) && "New".equals(lStrOldNewFlag))) {
				lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, lStrFieldTypeTI06, lDbOrgBasicPnsn[basicCntr], lDtFromDt);
				if (!lLstOfObjArr.isEmpty()) {
					lObjArr = (Object[]) lLstOfObjArr.get(0);
					if (lObjArr != null) {
						Double lTI06Per = ((BigDecimal) lObjArr[4]).doubleValue();
						lLngBasicPlusDP = lLngDPAmount + Math.round(lDbPnsnBasic);
						lDbTIAmount = (lLngBasicPlusDP.doubleValue() * ((lTI06Per) / 100));
					}
				}
			}

		} catch (Exception e) {
			// gLogger.error("Error is :: " + e,e);
			// throw(e);
		}
		return lDbTIAmount;
	}
	private Double getGreaterTIAmt(Object[] lObjArr, Double lDbPnsnBasic, long lLngDPAmount, long lLngEffDaysinMonth, int lIntTotalDaysInMonth) throws Exception {

		Double lDbMaxTIAmt = 0D;
		Double lDbTIPercnt = 0D;
		double lDbTIAmount = 0D;
		double lDbMinAmt = 0D;

		try {
			lDbTIPercnt = ((BigDecimal) lObjArr[4]).doubleValue();
			lDbTIAmount = ((Math.round(lDbPnsnBasic) + lLngDPAmount) * ((lDbTIPercnt) / 100));
			lDbMinAmt = (((lObjArr[5] != null ? ((BigDecimal) lObjArr[5]).doubleValue() : 0D) / lIntTotalDaysInMonth) * lLngEffDaysinMonth);
			lDbMaxTIAmt = lDbMinAmt > lDbTIAmount ? lDbMinAmt : lDbTIAmount;

		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return lDbMaxTIAmt;
	}
	
	//Sixth Pay Arrear Statement Report
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.RevisedArrearCalcService#loadSixthPayArrearStatement(java.util.Map)
	 */
	public ResultObject loadSixthPayArrearStatement(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<ComboValuesVO> lLstBanks = null;
		String lStrBillFlag = "";
		try {
			setSessionInfo(inputMap);
			lStrBillFlag = StringUtility.getParameter("billFlag", request);
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);
		
			inputMap.put("lLstBanks", lLstBanks);
			inputMap.put("BillFlag", lStrBillFlag);
						
			if("S".equals(lStrBillFlag))
			{
				resObj.setViewName("SixthPayArrearStmtPopUp");
			}
			else
			{
				resObj.setViewName("SixthPayArrearStatement");	
			}
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
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.RevisedArrearCalcService#generateSixthPayArrearStatementRpt(java.util.Map)
	 */
	public ResultObject generateSixthPayArrearStatement(Map<String, Object> inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSmplDateFormat = new SimpleDateFormat("dd MMMM yyyy ");
		setSessionInfo(inputMap);
		try
		{
			boolean isEndMonth = false;
			Long lLngPensionReqId = null;
			//inputs
			String lStrPnsnrName=StringUtility.getParameter("hdnPensionerName", request);
			String lStrBankName=StringUtility.getParameter("hidBankName", request);
			String lStrBranchName=StringUtility.getParameter("hidBranchName", request);
			String lStrTreasuryName=StringUtility.getParameter("hidTreasuryName", request);
			String lStrAccountNo=StringUtility.getParameter("hidAccountNo", request);
			String lStrVolumneNo=StringUtility.getParameter("hidVolumeNo", request);
			String lStrPageNo=StringUtility.getParameter("hidPageNo", request);
			String lStrPpoNo = StringUtility.getParameter("hidppono", request).trim();
			String lStrPensionerCode = StringUtility.getParameter("hidPnsnrCode", request).trim();
			String lStrPensionReqId = StringUtility.getParameter("hidPensnReqId", request);
			if(lStrPensionReqId != null && !"".equals(lStrPensionReqId))
				lLngPensionReqId = Long.parseLong(lStrPensionReqId.trim());
			Long lLngHeadCode = Long.parseLong(StringUtility.getParameter("hidheadcode", request).trim());
			String lStrPnsnrDOB = StringUtility.getParameter("hidPnsnrDOB", request);
			String lStrPnsnrDOR = StringUtility.getParameter("hidPnsnrDOR", request);
			String lStrBillFlag = StringUtility.getParameter("billFlag", request);
//			String lStrEffectiveFromDt = StringUtility.getParameter("txtEffectiveFromDt", request);
//			String lStrEffectiveToDt = StringUtility.getParameter("txtEffectiveToDt", request);
//			String lStrOldBasicAmt  = StringUtility.getParameter("txtOldBasic", request);
//			String lStrNewBasicAmt  = StringUtility.getParameter("txtNewBasic", request);
//			String lStrOldCvpAmt  = StringUtility.getParameter("txtOldCvp", request);
//			String lStrNewCvpAmt  = StringUtility.getParameter("txtNewCvp", request);
			Double lDbAdvanceAmt = 0D;
			Double lDbRecoveryAmt = 0D;
			
			String lStrAdvanceAmt = StringUtility.getParameter("txtAdvanceAmt", request);
			if(!"".equals(lStrAdvanceAmt))
				lDbAdvanceAmt = Double.parseDouble(lStrAdvanceAmt);
			String lStrRecoveryAmt = StringUtility.getParameter("txtRecoveryAmt", request);
			if(!"".equals(lStrRecoveryAmt))
				lDbRecoveryAmt =Double.parseDouble(lStrRecoveryAmt);
			int lIntPnsnCutCntr = 0;
			int lIntReEmpCntr = 0;
			//Pensioner Basic Dtls
			
			String[] lStrOldBasic = StringUtility.getParameterValues("txtOldBasic", request);
			String[] lStrNewBasic = StringUtility.getParameterValues("txtNewBasic", request);
			String[] lStrOldCvp = StringUtility.getParameterValues("txtOldCvp", request);
			String[] lStrNewCvp = StringUtility.getParameterValues("txtNewCvp", request);
			String[] lStrOldBasicEffFrom = StringUtility.getParameterValues("txtEffectiveFromDt", request);
			String[] lStrOldBasicEffTo = StringUtility.getParameterValues("txtEffectiveToDt", request);
			Double[] lDbOldBasic = new Double[lStrOldBasic.length];
			Double[] lDbNewBasic = new Double[lStrNewBasic.length];
			Double[] lDbOldCvp = new Double[lStrOldCvp.length];
			Double[] lDbNewCvp = new Double[lStrNewCvp.length];
			Date[] lDtOldBasicEffFrom = new Date[lStrOldBasic.length];
			int[] lIntOldFromMonth = new int[lStrOldBasic.length];
			int[] lIntOldFromYear = new int[lStrOldBasic.length];
			int[] lIntOldStartDate = new int[lStrOldBasic.length];
			Date[] lDtOldBasicEffTo = new Date[lStrOldBasicEffTo.length];
			int[] lIntOldToMonth = new int[lStrOldBasicEffTo.length];
			int[] lIntOldToYear = new int[lStrOldBasicEffTo.length];
			int[] lIntOldEndMonthDays = new int[lStrOldBasicEffTo.length];
			String lStrEffectiveFromDt = lStrOldBasicEffFrom[0];
			String lStrEffectiveToDt = lStrOldBasicEffTo[lStrOldBasic.length-1];
			
			for (int i = 0; i < lStrOldBasic.length; i++) {
				if(!"".equals(lStrOldBasic[i]))
					lDbOldBasic[i] = Double.parseDouble(lStrOldBasic[i]);
				else
					lDbOldBasic[i] = 0D;
				if(!"".equals(lStrNewBasic[i]))
					lDbNewBasic[i] = Double.parseDouble(lStrNewBasic[i]);
				else
					lDbNewBasic[i] = 0D;
				if(!"".equals(lStrOldCvp[i]))
					lDbOldCvp[i] = Double.parseDouble(lStrOldCvp[i]);
				else
					lDbOldCvp[i] = 0D;
				if(!"".equals(lStrNewCvp[i]))
					lDbNewCvp[i] = Double.parseDouble(lStrNewCvp[i]);
				else
					lDbNewCvp[i] = 0D;
				if (!"".equals(lStrOldBasicEffFrom[i]) && lStrOldBasicEffFrom[i] != null) {
					lDtOldBasicEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldBasicEffFrom[i]);
					lIntOldFromMonth[i] = Integer.parseInt(lStrOldBasicEffFrom[i].substring(3, 5));
					lIntOldFromYear[i] = Integer.parseInt(lStrOldBasicEffFrom[i].substring(6));
					lIntOldStartDate[i] = Integer.parseInt(lStrOldBasicEffFrom[i].substring(0, 2));
				}
				if (!"".equals(lStrOldBasicEffTo[i]) && lStrOldBasicEffTo[i] != null)
				{
					lDtOldBasicEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrOldBasicEffTo[i]);
					lIntOldToMonth[i] = Integer.parseInt(lStrOldBasicEffTo[i].substring(3, 5));
					lIntOldToYear[i] = Integer.parseInt(lStrOldBasicEffTo[i].substring(6));
					lIntOldEndMonthDays[i] = Integer.parseInt(lStrOldBasicEffTo[i].substring(0, 2));
				}
			}
			
			Date lDtPnsnrDOB = null;
			Date lDtPnsnrDOR = null;
			Date lDtEffectiveFrom = null;
			Date lDtEffectiveTo = null;
			Double lDbNewBasicAmt = 0D;
			Double lDbOldBasicAmt = 0D;
			Double lDbNewCvpAmt = 0D;
			Double lDbOldCvpAmt = 0D;
			
			Date lDtFromDtUpdated = null;
			
			Date lDtToDtUpdated = null;

			if (lStrEffectiveFromDt != null && !"".equals(lStrEffectiveFromDt)) {
				lDtEffectiveFrom = lObjSimpleDateFormat.parse(lStrEffectiveFromDt);
			}
			if (lStrEffectiveToDt != null && !"".equals(lStrEffectiveToDt)) {
				lDtEffectiveTo = lObjSimpleDateFormat.parse(lStrEffectiveToDt);
			}
			
			if (lStrPnsnrDOB != null && !"".equals(lStrPnsnrDOB)) {
				lDtPnsnrDOB = IFMSCommonServiceImpl.getDateFromString(lStrPnsnrDOB);
			}
			if (lStrPnsnrDOR != null && !"".equals(lStrPnsnrDOR)) {
				lDtPnsnrDOR = IFMSCommonServiceImpl.getDateFromString(lStrPnsnrDOR);
			}
			String lStrPnsnrDOD = StringUtility.getParameter("hidPnsnrDOD", request);
			Date lDtPnsnrDOD = null;
			if (lStrPnsnrDOD != null && !"".equals(lStrPnsnrDOD)) {
				lDtPnsnrDOD = IFMSCommonServiceImpl.getDateFromString(lStrPnsnrDOD);
			}
			String lStrFamMemDOB = StringUtility.getParameter("hidFamMemDOB", request);
			Date lDtFamMemDOB = null;
			if (lStrFamMemDOB != null && !"".equals(lStrFamMemDOB)) {
				lDtFamMemDOB = IFMSCommonServiceImpl.getDateFromString(lStrFamMemDOB);
			}
			String lStrFamMemDOD = StringUtility.getParameter("hidFamMemDOD", request);
			Date lDtFamMemDOD = null;
			if (lStrFamMemDOD != null && !"".equals(lStrFamMemDOD)) {
				lDtFamMemDOD = IFMSCommonServiceImpl.getDateFromString(lStrFamMemDOD);
			}

			String lStrFP1Date = StringUtility.getParameter("hidFP1Date", request);
			Date lDtFP1Date = null;
			if (lStrFP1Date != null && !"".equals(lStrFP1Date)) {
				lDtFP1Date = IFMSCommonServiceImpl.getDateFromString(lStrFP1Date);
			}
			String lStrFP2Date = StringUtility.getParameter("hidFP2Date", request);
			Date lDtFP2Date = null;
			if (lStrFP2Date != null && !"".equals(lStrFP2Date)) {
				lDtFP2Date = IFMSCommonServiceImpl.getDateFromString(lStrFP2Date);
			}
			String lStrNewFP1Basic = StringUtility.getParameter("hidFP1Amnt", request);
			Double lDbNewFP1Basic = 0D;
			if (lStrNewFP1Basic != null && !"".equals(lStrNewFP1Basic)) {
				lDbNewFP1Basic = Double.parseDouble(lStrNewFP1Basic);
			}
			String lStrNewFP2Basic = StringUtility.getParameter("hidFP2Amnt", request);
			Double lDbNewFP2Basic = 0D;
			if (lStrNewFP2Basic != null && !"".equals(lStrNewFP2Basic)) {
				lDbNewFP2Basic = Double.parseDouble(lStrNewFP2Basic);
			}
			String lStrCommensionDate = StringUtility.getParameter("hidCommensionDate", request);
			Date lDtCommensionDate = null;
			if (lStrCommensionDate != null && !"".equals(lStrCommensionDate)) {
				lDtCommensionDate = IFMSCommonServiceImpl.getDateFromString(lStrCommensionDate);
			}
			
			/**
			 * ReEmp input
			 */
			 
			String[] lStrReEmpEffFrom = StringUtility.getParameterValues("txtOldReEmpEffFrom", request);
			String[] lStrReEmpEffTo = StringUtility.getParameterValues("txtOldReEmpEffTo", request);
			String[] lStrDAInPnsnSal = StringUtility.getParameterValues("cmbDAInPnsnSal", request);
			
			Date[] lDtReEmpEffFrom = new Date[lStrReEmpEffFrom.length];
			Date[] lDtReEmpEffTo = new Date[lStrReEmpEffTo.length];
			for (int i = 0; i < lStrReEmpEffFrom.length; i++) {
				if (!"".equals(lStrReEmpEffFrom[i]) && lStrReEmpEffFrom[i] != null) {
					lDtReEmpEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrReEmpEffFrom[i]);
				}
				if (!"".equals(lStrReEmpEffTo[i]) && lStrReEmpEffTo[i] != null) {
					lDtReEmpEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrReEmpEffTo[i]);
				}
			}
						
			/**
			 * Punishment Cut input
			 */
			 
			String[] lStrPunishAmt = StringUtility.getParameterValues("txtPnshmntAmount", request);
			String[] lStrPunishFromDate = StringUtility.getParameterValues("txtPnshmntFromDate", request);
			String[] lStrPunishToDate = StringUtility.getParameterValues("txtPnshmntToDate", request);
			
			Double[] lDbPunishAmt = new Double[lStrPunishAmt.length];
			Date[] lDtPunishFromDate = new Date[lStrPunishFromDate.length];
			Date[] lDtPunishToDate = new Date[lStrPunishToDate.length];
			for (int i = 0; i < lStrPunishFromDate.length; i++) {
				if(!"".equals(lStrPunishAmt[i]) && lStrPunishAmt[i] != null)
				{
					lDbPunishAmt[i] = Double.parseDouble(lStrPunishAmt[i]);
				}
				if (!"".equals(lStrPunishFromDate[i]) && lStrPunishFromDate[i] != null) {
					lDtPunishFromDate[i] = IFMSCommonServiceImpl.getDateFromString(lStrPunishFromDate[i]);
				}
				if (!"".equals(lStrPunishToDate[i]) && lStrPunishToDate[i] != null) {
					lDtPunishToDate[i] = IFMSCommonServiceImpl.getDateFromString(lStrPunishToDate[i]);
				}
			}
			
			//Prepare list of DA Rate Range start
			List<RltPensionHeadcodeRate> lLstDA1996DPHeadCodeRate = new ArrayList<RltPensionHeadcodeRate>();
			List<RltPensionHeadcodeRate> lLstDA2006HeadCodeRate = new ArrayList<RltPensionHeadcodeRate>();
			Set<Date> lLstDARateDate = new TreeSet<Date>();
			List<String> lLstDARateRange = new ArrayList<String>();
			boolean lDAToFromDtSameFlag = false;
			boolean lDAFromToDtSameFlag = false;
			RevisedArrearCalcDAO lObjRevisedArrearCalcDAO =new RevisedArrearCalcDAOImpl(serv.getSessionFactory());
			lLstDA1996DPHeadCodeRate=lObjRevisedArrearCalcDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_1996_DP", 0D, lDtEffectiveFrom,lDtEffectiveTo);
			lLstDA2006HeadCodeRate=lObjRevisedArrearCalcDAO.getRateFromHeadcodeRateRlt(lLngHeadCode, "DA_2006", 0D, lDtEffectiveFrom,lDtEffectiveTo);
			
			for(RltPensionHeadcodeRate lObjRltPensionHeadCodeRate : lLstDA1996DPHeadCodeRate)
			{
				if(lObjRltPensionHeadCodeRate.getEffectiveFromDate().after(lDtEffectiveFrom))
				{
					lLstDARateDate.add(lObjRltPensionHeadCodeRate.getEffectiveFromDate());
				}
				else
				{
					lLstDARateDate.add(lDtEffectiveFrom);
				}
				if(lObjRltPensionHeadCodeRate.getEffectiveToDate() != null && lObjRltPensionHeadCodeRate.getEffectiveToDate().after(IFMSCommonServiceImpl.getDateFromString("31/03/2009")) 
						&& lDtEffectiveTo.after(IFMSCommonServiceImpl.getDateFromString("31/03/2009"))
						&& lDtEffectiveFrom.before(IFMSCommonServiceImpl.getDateFromString("31/03/2009")))
				{
					lLstDARateDate.add(IFMSCommonServiceImpl.getDateFromString("31/03/2009"));
					lLstDARateDate.add(IFMSCommonServiceImpl.getDateFromString("01/04/2009"));
				}
				if(lObjRltPensionHeadCodeRate.getEffectiveToDate() != null && (lObjRltPensionHeadCodeRate.getEffectiveToDate().before(lDtEffectiveTo) || lObjRltPensionHeadCodeRate.getEffectiveToDate().compareTo(lDtEffectiveTo) == 0))
				{
					lLstDARateDate.add(lObjRltPensionHeadCodeRate.getEffectiveToDate());
				}
				else
				{
//					if(lObjRltPensionHeadCodeRate.getEffectiveToDate() != null)
//					{
//						if(lObjRltPensionHeadCodeRate.getEffectiveToDate().before(IFMSCommonServiceImpl.getDateFromString("31/03/2009")) && lDtEffectiveTo.after(IFMSCommonServiceImpl.getDateFromString("31/03/2009")))
//						{
//							lLstDARateDate.add(IFMSCommonServiceImpl.getDateFromString("31/03/2009"));
//							lLstDARateDate.add(IFMSCommonServiceImpl.getDateFromString("01/04/2009"));
//						}
//					}
					lLstDARateDate.add(lDtEffectiveTo);
					
				}
				if(lObjRltPensionHeadCodeRate.getEffectiveToDate() != null && (lObjRltPensionHeadCodeRate.getEffectiveToDate().compareTo(lDtEffectiveFrom) == 0 || (IFMSCommonServiceImpl.getDateFromString("31/03/2009").compareTo(lDtEffectiveFrom)==0)))
				{
					lDAToFromDtSameFlag = true;
				}
				if(lObjRltPensionHeadCodeRate.getEffectiveFromDate().compareTo(lDtEffectiveTo) == 0)
				{
					lDAFromToDtSameFlag = true;
				}
			}
			for(RltPensionHeadcodeRate lObjRltPensionHeadCodeRate : lLstDA2006HeadCodeRate)
			{
				if(lObjRltPensionHeadCodeRate.getEffectiveFromDate().after(lDtEffectiveFrom))
				{
					lLstDARateDate.add(lObjRltPensionHeadCodeRate.getEffectiveFromDate());
				}
				else
				{
					lLstDARateDate.add(lDtEffectiveFrom);
				}
				if(lObjRltPensionHeadCodeRate.getEffectiveToDate() != null && lObjRltPensionHeadCodeRate.getEffectiveToDate().after(IFMSCommonServiceImpl.getDateFromString("31/03/2009"))  
						&& lDtEffectiveTo.after(IFMSCommonServiceImpl.getDateFromString("31/03/2009"))
						&& lDtEffectiveFrom.before(IFMSCommonServiceImpl.getDateFromString("31/03/2009")))
				{
					lLstDARateDate.add(IFMSCommonServiceImpl.getDateFromString("31/03/2009"));
					lLstDARateDate.add(IFMSCommonServiceImpl.getDateFromString("01/04/2009"));
				}
				if(lObjRltPensionHeadCodeRate.getEffectiveToDate() != null && (lObjRltPensionHeadCodeRate.getEffectiveToDate().before(lDtEffectiveTo) || lObjRltPensionHeadCodeRate.getEffectiveToDate().compareTo(lDtEffectiveTo) == 0))
				{
					lLstDARateDate.add(lObjRltPensionHeadCodeRate.getEffectiveToDate());
				}
				else
				{
//					if(lObjRltPensionHeadCodeRate.getEffectiveToDate() != null)
//					{
//						if(lObjRltPensionHeadCodeRate.getEffectiveToDate().before(IFMSCommonServiceImpl.getDateFromString("31/03/2009"))  && lDtEffectiveTo.after(IFMSCommonServiceImpl.getDateFromString("31/03/2009")))
//						{
//							lLstDARateDate.add(IFMSCommonServiceImpl.getDateFromString("31/03/2009"));
//							lLstDARateDate.add(IFMSCommonServiceImpl.getDateFromString("01/04/2009"));
//						}
//					}
					lLstDARateDate.add(lDtEffectiveTo);
				}
				if(lObjRltPensionHeadCodeRate.getEffectiveToDate() != null && (lObjRltPensionHeadCodeRate.getEffectiveToDate().compareTo(lDtEffectiveFrom) == 0 || (IFMSCommonServiceImpl.getDateFromString("31/03/2009").compareTo(lDtEffectiveFrom)==0)))
				{
					lDAToFromDtSameFlag = true;
				}
				if(lObjRltPensionHeadCodeRate.getEffectiveFromDate().compareTo(lDtEffectiveTo) == 0)
				{
					lDAFromToDtSameFlag = true;
				}
			}
			//Prepare list of DA Rate Range end
			
			//Prepare Range of DA rate by combining two continuous date from lLstDARateDate list start
			Object[] lArrDARateDt= lLstDARateDate.toArray();
			Set<Date> lLstEffFromToDate = new TreeSet<Date>();
			for(int lIntCnt=0;lIntCnt<lArrDARateDt.length-1;lIntCnt++)
			{
				String lStrDateRange = "";
				//When From date is same as DA To Date
				if(lIntCnt == 0 && lDAToFromDtSameFlag == true)
				{
					lStrDateRange =lObjSimpleDateFormat.format(lArrDARateDt[lIntCnt])+"~"+lObjSimpleDateFormat.format(lArrDARateDt[lIntCnt]);
				}
				else
				{
					lStrDateRange =lObjSimpleDateFormat.format(lArrDARateDt[lIntCnt])+"~"+lObjSimpleDateFormat.format(lArrDARateDt[lIntCnt+1]);
					++lIntCnt;
				}	
				lLstDARateRange.add(lStrDateRange);
			}
			//When To date is same as DA From Date or To date is 01/04/2009
			if(lDAFromToDtSameFlag == true || (IFMSCommonServiceImpl.getDateFromString("01/04/2009")).equals(lDtEffectiveTo))
			{
				lLstDARateRange.add(lObjSimpleDateFormat.format(lArrDARateDt[lArrDARateDt.length-1])+"~"+lObjSimpleDateFormat.format(lArrDARateDt[lArrDARateDt.length-1]));
			}
			//Prepare Range of DA rate by combining two continuous date from lLstDARateDate list end
			
			//map DA rate range with applicable basic and cvp start
			Map<String,Map<String,Long[]>> lMapDARangeBasicCvpDtls = new HashMap<String,Map<String,Long[]>>();
			Map<String,Long[]> lMapEffRangeBasicCvp = new HashMap<String,Long[]>();
			Long[] lLngBasicCvpAmt = new Long[4];
			
			for(int masterCtr = 0;masterCtr<lLstDARateRange.size();masterCtr++)
			{
				String lStrDARateRange = lLstDARateRange.get(masterCtr);
				String[] lArrStrDARateRange=lStrDARateRange.split("~");
				//DA Rate Effective from date
				Date lDtDAEffFromDate = lObjSimpleDateFormat.parse(lArrStrDARateRange[0]);
				//DA Rate Effective to date
				Date lDtDAEffToDate = lObjSimpleDateFormat.parse(lArrStrDARateRange[1]);
				
				for (int lIntCnt = 0; lIntCnt < lStrOldBasic.length; lIntCnt++) {
				
					String lStrEffRangeKey ="";
					Integer lIntFlag =0;
					if((lDtOldBasicEffFrom[lIntCnt].equals(lDtDAEffFromDate) || lDtOldBasicEffFrom[lIntCnt].after(lDtDAEffFromDate)) && 
							(lDtOldBasicEffTo[lIntCnt].equals(lDtDAEffToDate) || lDtOldBasicEffTo[lIntCnt].before(lDtDAEffToDate)))
					{
						lStrEffRangeKey = lObjSimpleDateFormat.format(lDtOldBasicEffFrom[lIntCnt])+"~"+lObjSimpleDateFormat.format(lDtOldBasicEffTo[lIntCnt]);
					}
					else if((lDtOldBasicEffFrom[lIntCnt].after(lDtDAEffFromDate) || lDtOldBasicEffFrom[lIntCnt].equals(lDtDAEffFromDate)) && lDtOldBasicEffTo[lIntCnt].after(lDtDAEffToDate) && lDtDAEffFromDate.compareTo(lDtDAEffToDate) != 0)
					{
						if(lDtOldBasicEffFrom[lIntCnt].before(lDtDAEffToDate))
						{
							lStrEffRangeKey = lObjSimpleDateFormat.format(lDtOldBasicEffFrom[lIntCnt])+"~"+lObjSimpleDateFormat.format(lDtDAEffToDate);
							lIntFlag =1;
						}
						
					}
					else if(lDtOldBasicEffFrom[lIntCnt].before(lDtDAEffFromDate) && (lDtOldBasicEffTo[lIntCnt].after(lDtDAEffToDate) || lDtOldBasicEffTo[lIntCnt].equals(lDtDAEffToDate))
							|| lDtDAEffFromDate.compareTo(lDtDAEffToDate) == 0)
					{
						lStrEffRangeKey = lObjSimpleDateFormat.format(lDtDAEffFromDate)+"~"+lObjSimpleDateFormat.format(lDtDAEffToDate);
						
					}
					else if(lDtOldBasicEffFrom[lIntCnt].before(lDtDAEffFromDate) && lDtOldBasicEffTo[lIntCnt].before(lDtDAEffToDate))
					{
						lStrEffRangeKey = lObjSimpleDateFormat.format(lDtDAEffFromDate)+"~"+lObjSimpleDateFormat.format(lDtOldBasicEffTo[lIntCnt]);
					}
					
					if(!"".equals(lStrEffRangeKey))
					{
						lLngBasicCvpAmt = new Long[4];
						lLngBasicCvpAmt[0] = lDbOldBasic[lIntCnt].longValue();
						lLngBasicCvpAmt[1] = lDbNewBasic[lIntCnt].longValue();
						if(!"".equals(lDbOldCvp[lIntCnt]))
							lLngBasicCvpAmt[2] = lDbOldCvp[lIntCnt].longValue();
						if(!"".equals(lDbNewCvp[lIntCnt]))
							lLngBasicCvpAmt[3] = lDbNewCvp[lIntCnt].longValue();
					
						lMapEffRangeBasicCvp = lMapDARangeBasicCvpDtls.get(lStrDARateRange);
						if(lMapEffRangeBasicCvp != null)
						{
							lMapEffRangeBasicCvp.put(lStrEffRangeKey, lLngBasicCvpAmt);
						}
						else
						{
							lMapEffRangeBasicCvp = new HashMap<String,Long[]>();
							lMapEffRangeBasicCvp.put(lStrEffRangeKey, lLngBasicCvpAmt);
						}
						lMapDARangeBasicCvpDtls.put(lStrDARateRange, lMapEffRangeBasicCvp);
						lStrEffRangeKey ="";
					}
				}
			}
			StringBuilder lSbLineDisplay = new StringBuilder();
			StringBuilder lSbHeading = new StringBuilder();
			StringBuilder lSbFooter = new StringBuilder();
			List lLstInnerList = new ArrayList();
			/** Header generation starts */
			lSbHeading.append("\r\n");
			lSbHeading.append(String.format("%45s","SIXTHPAY PENSION ARREARS AS ON  "+lObjSmplDateFormat.format(lDtEffectiveTo)+"       "));
			lSbHeading.append(String.format("%30s","Treasury Name : "+lStrTreasuryName));
			lSbHeading.append("\r\n");
			lSbHeading.append(String.format("%-30s","P.P.O. No."+lStrPpoNo));
			lSbHeading.append("Bank / Branch : "+lStrBankName+" / "+lStrBranchName);
			lSbHeading.append("\r\n");
			lSbHeading.append(String.format("%50s","A/C No. "+lStrAccountNo));
			lSbHeading.append("\r\n");
			lSbHeading.append(String.format("%-26s","Volume/Page : "+lStrVolumneNo+"/"+lStrPageNo));
			lSbHeading.append(String.format("%-59s","Name of Pensioner : "+lStrPnsnrName));
			lSbHeading.append("\r\n");
			lSbHeading.append(String.format("%-30s","Pension Start Date : "+lStrCommensionDate));
			lSbHeading.append("\r\n");
			lSbHeading.append("\r\n");
			lSbHeading.append(getChar(88, "-"));
			lSbHeading.append("\r\n");
			lSbHeading.append("| New Basic  |    New Basic   | Old Basic |    Old Basic   |  Effective  | Effective  |");
			lSbHeading.append("\r\n");
			lSbHeading.append("|            | After Commuted |           | After Commuted |  From Date  |  To Date   |");
			lSbHeading.append("\r\n");
			lSbHeading.append(getChar(88, "-"));
			lSbHeading.append("\r\n");
			for (int i = 0; i < lStrOldBasic.length; i++) {
				lDbOldBasic[i] = Double.parseDouble(lStrOldBasic[i]);
				lDbNewBasic[i] = Double.parseDouble(lStrNewBasic[i]);
				//lDbOldCvp[i] = Double.parseDouble(lStrOldCvp[i]);
				//lDbNewCvp[i] = Double.parseDouble(lStrNewCvp[i]);
				Double lDbNewAftComtn = lDbNewBasic[i] - lDbNewCvp[i];
				Double lDbOldAftComtn = lDbOldBasic[i] - lDbOldCvp[i];
				lSbHeading.append("|");
				lSbHeading.append(String.format("%12s", lDbNewBasic[i].longValue()));
				lSbHeading.append("|");
				lSbHeading.append(String.format("%16s", lDbNewAftComtn.longValue()));
				lSbHeading.append("|");
				lSbHeading.append(String.format("%11s", lDbOldBasic[i].longValue()));
				lSbHeading.append("|");
				lSbHeading.append(String.format("%16s", lDbOldAftComtn.longValue()));
				lSbHeading.append("|");
				lSbHeading.append(String.format("%13s", lStrOldBasicEffFrom[i]));
				lSbHeading.append("|");
				lSbHeading.append(String.format("%12s", lStrOldBasicEffTo[i]));
				lSbHeading.append("|");
				lSbHeading.append("\r\n");
				
			}
			lSbHeading.append(getChar(88, "-"));
			lSbHeading.append("\r\n");
			lSbHeading.append("\r\n");
			lSbHeading.append(getChar(132, "-"));
			lSbHeading.append("\r\n");
			lSbHeading.append("|                                  Due                                     |                        Drawn                         |");
			lSbHeading.append("\r\n");
						
			int nMode = 132;
			boolean pageBreakonGrouping = false;
			
			ReportExportHelper objExport = new ReportExportHelper();
			String lStrDisplay = "";
			String lStrDisplayString = "";
			List lLstArrOuter = new ArrayList();
			ArrayList lineList = new ArrayList();
			StringBuffer sbLine = new StringBuffer();
			Long lLngInstltAmt=Long.valueOf(0);
			Long lLngInstlt5Amt=Long.valueOf(0);
			Long lLngDiffAmt=Long.valueOf(0);
			
			for(int i = 0 ; i < 132; i++) sbLine.append("-");
			lineList.add(sbLine.toString());
			lineList.add("");
			lineList.add("");
			lineList.add("");
			lineList.add("");
			lineList.add("");
			lineList.add("");
			lineList.add("");
			lineList.add("");
			lineList.add("");
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
			blankList.add("");
			blankList.add("");
			blankList.add("");
			blankList.add("");
			blankList.add("");
			blankList.add("");
			blankList.add("");
			blankList.add("");
			
			
			ColumnVo[] columnHeading = new ColumnVo[13];
			
			columnHeading[0] =  new ColumnVo("DA%",2,7,0,false,false,true);
			columnHeading[1] =  new ColumnVo("DA Period",1,25,0,true,false,true);
			columnHeading[2] =  new ColumnVo("No Month",1,3,0,true,false,true);
			columnHeading[3] =  new ColumnVo("Pension",2,10,0,false,false,true);
			columnHeading[4] =  new ColumnVo("Punishment Cut",2,6,0,false,false,true);
			columnHeading[5] =  new ColumnVo("DA",2,8,0,false,false,true);
			columnHeading[6] =  new ColumnVo("Net Pension",2,10,0,false,false,true);
			columnHeading[7] =  new ColumnVo("DA%",2,7,0,false,false,true);
			columnHeading[8] =  new ColumnVo("Pension",2,10,0,false,false,true);
			columnHeading[9] =  new ColumnVo("Punishment Cut",2,6,0,false,false,true);
			columnHeading[10] = new ColumnVo("DP",2,8,0,false,false,true);
			columnHeading[11] = new ColumnVo("DA",2,8,0,false,false,true);
			columnHeading[12] = new ColumnVo("Net Pension",2,10,0,false,false,true);
			
			boolean isNextMonthYearSame = false;
			Long lLngTotalOldNetPension = Long.valueOf(0);
			Long lLngTotalNewNetPension = Long.valueOf(0);
			Long lLngTotalDiffAmt = Long.valueOf(0);
			Map<String,Long[]> lMapBasicCvpRange = new HashMap<String,Long[]>();
			
			int lIntFlag = 0;
			for(int lIntCnt=0;lIntCnt<lLstDARateRange.size();lIntCnt++)
			{
				String lStrDaRangeKey = lLstDARateRange.get(lIntCnt);
				lMapBasicCvpRange = lMapDARangeBasicCvpDtls.get(lStrDaRangeKey);
				String[] lArrStrDARateRange = lStrDaRangeKey.split("~");
				
				Date lDtDAEffFromDate = lObjSimpleDateFormat.parse(lArrStrDARateRange[0]);
				
				Date lDtDAEffToDate = lObjSimpleDateFormat.parse(lArrStrDARateRange[1]);
				
				if(lDtDAEffToDate.equals(IFMSCommonServiceImpl.getDateFromString("31/03/2009")) && lIntCnt != lLstDARateRange.size()-1)
				{
					lIntFlag = 1;
				}
				if((lDtDAEffFromDate.equals(IFMSCommonServiceImpl.getDateFromString("01/04/2009")) || lDtDAEffFromDate.after(IFMSCommonServiceImpl.getDateFromString("01/04/2009"))) && lIntCnt == 0)
				{
					lIntFlag = 1;
					lLstInnerList = new ArrayList();
					lLstInnerList.add(getChar(8, "-"));
					lLstInnerList.add(getChar(26, "-"));
					lLstInnerList.add(getChar(4, "-"));
					lLstInnerList.add(getChar(11, "-"));
					lLstInnerList.add(getChar(7, "-"));
					lLstInnerList.add(getChar(9, "-"));
					lLstInnerList.add(getChar(11, "-"));
					lLstInnerList.add(getChar(8, "-"));
					lLstInnerList.add(getChar(11, "-"));
					lLstInnerList.add(getChar(7, "-"));
					lLstInnerList.add(getChar(9, "-"));
					lLstInnerList.add(getChar(9, "-"));
					lLstInnerList.add(getChar(11, "-"));
					lLstArrOuter.add(lLstInnerList);
					lLstInnerList = new ArrayList();
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add(lLngTotalNewNetPension);
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add(lLngTotalOldNetPension);
					lLstArrOuter.add(lLstInnerList);
					lLngTotalDiffAmt = lLngTotalNewNetPension - lLngTotalOldNetPension;
					
					lStrDisplay =objExport.getReportFile(lLstArrOuter, columnHeading,lSbHeading.toString(),
							"",null,nMode ,true,1,true,"|");
					lStrDisplayString = lStrDisplayString + lStrDisplay;
					
					Double lDbDiffAdvRecAmt=0D;
					Double lDbInstallmentAmt=0D;
					lLngInstltAmt=Long.valueOf(0);
					lLngInstlt5Amt=Long.valueOf(0);
					
					lDbDiffAdvRecAmt = lLngTotalDiffAmt - (lDbAdvanceAmt + lDbRecoveryAmt);
					lDbInstallmentAmt = lDbDiffAdvRecAmt/5;
					
					lLngInstltAmt = lDbInstallmentAmt.longValue();
					lLngInstlt5Amt = (long) (lLngInstltAmt +(lDbDiffAdvRecAmt%5));
					lSbFooter.append("Difference from "+lStrEffectiveFromDt+" to 31/03/2009 - (Advance + Other Recovery) "+ lLngTotalDiffAmt.toString()+" - ("+lDbAdvanceAmt.longValue()+" + "+lDbRecoveryAmt.longValue()+") = "+lDbDiffAdvRecAmt.longValue());
					lSbFooter.append("\r\n");
					lSbFooter.append("Differance Payable in Five Installment        "+lDbDiffAdvRecAmt.longValue()+" / 5 = "+lLngInstltAmt);
					lSbFooter.append("\r\n");
					lSbFooter.append("1St Inst    2nd Inst    3rd Inst    4th Inst    5th Inst");
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%-12s", lLngInstltAmt));
					lSbFooter.append(String.format("%-12s", lLngInstltAmt));
					lSbFooter.append(String.format("%-12s", lLngInstltAmt));
					lSbFooter.append(String.format("%-12s", lLngInstltAmt));
					lSbFooter.append(String.format("%-12s", lLngInstlt5Amt));
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("Paid Date");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					if(lIntFlag == 1)
					{
					    lIntFlag = 2;
						lSbFooter.append((char) 12);
					}
					lStrDisplayString = lStrDisplayString + lSbFooter.toString();
					lSbFooter = new StringBuilder();
					lLstArrOuter = new ArrayList();
					lLngTotalNewNetPension = Long.valueOf(0);
					lLngTotalOldNetPension = Long.valueOf(0);
				}
				Double lDbNewDARate = 0D;
				Double lDbOldDARate = 0D;
				BigDecimal lBgDcmlNewDARate = BigDecimal.ZERO;
				BigDecimal lBgDcmlOldDARate = BigDecimal.ZERO;
				Double lDbDPRate = 0D;
				
				Long lLngRangeNewPension = Long.valueOf(0);
				Long lLngRangeNewPunishAmt = Long.valueOf(0);
				Long lLngRangeNewDAAmt = Long.valueOf(0);
				Long lLngRangeNewNetPension = Long.valueOf(0);
				Long lLngRangeOldPension = Long.valueOf(0);
				Long lLngRangeOldPunishAmt = Long.valueOf(0);
				Long lLngRangeOldDPAmt = Long.valueOf(0);
				Long lLngRangeOldDAAmt = Long.valueOf(0);
				Long lLngRangeOldNetPension = Long.valueOf(0);
				
				lLstInnerList = new ArrayList();
				
				List lLstOfObjArr = null;
				Object[] lObjArr = null;
				
				lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, "DA_2006", 0, lDtDAEffFromDate);
				if (!lLstOfObjArr.isEmpty()) {
					lObjArr = (Object[]) lLstOfObjArr.get(0);
					if (lObjArr != null) {
						lBgDcmlNewDARate = (BigDecimal) lObjArr[4];
						lDbNewDARate = ((BigDecimal) lObjArr[4]).doubleValue();
						
						lLstInnerList.add(lObjArr[4]);
					}
				}
				else
				{
					lLstInnerList.add(0L);
				}
				
				lLstInnerList.add(lArrStrDARateRange[0] + " To "+lArrStrDARateRange[1]);
				
				lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, "DA_1996_DP", 0, lDtDAEffFromDate);
				if (!lLstOfObjArr.isEmpty()) {
					lObjArr = (Object[]) lLstOfObjArr.get(0);
					if (lObjArr != null) {
						lBgDcmlOldDARate = (BigDecimal) lObjArr[4];
						lDbOldDARate = ((BigDecimal) lObjArr[4]).doubleValue();
											
					}
				}
				
				Calendar lObjCalendar = null;
				lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtDAEffFromDate);
				int lIntEffFromDate = lObjCalendar.get(Calendar.DATE);
				
				int lIntFromDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				int lIntEffFromYear=lObjCalendar.get(Calendar.YEAR); 
				int lIntEffFromMonth=lObjCalendar.get(Calendar.MONTH);
				
				lObjCalendar.setTime(lDtDAEffToDate);
				int lIntToDaysInMonth  = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				int lIntEffToDate = lObjCalendar.get(Calendar.DATE);
				int lIntEffToYear=lObjCalendar.get(Calendar.YEAR);
				int lIntEffToMonth=lObjCalendar.get(Calendar.MONTH);
				int lIntMonths = 0;
				
				lIntMonths = (lIntEffToYear - lIntEffFromYear)*12 + (lIntEffToMonth - lIntEffFromMonth) + 1;
				lLstInnerList.add(lIntMonths);
				
				for(String lStrEffDtRangeKey : lMapBasicCvpRange.keySet())
				{
					String[] lArrStrEffRange=lStrEffDtRangeKey.split("~");
					//Effective start date
					Date lDtEffFromDate = lObjSimpleDateFormat.parse(lArrStrEffRange[0]);
					//Effective end date
					Date lDtEffToDate = lObjSimpleDateFormat.parse(lArrStrEffRange[1]);
					
					Long[] lLngBasicCvpAmount = new Long[4];
					lLngBasicCvpAmount = lMapBasicCvpRange.get(lStrEffDtRangeKey);
					Long lLngOldBasic = lLngBasicCvpAmount[0];
					Long lLngNewBasic = lLngBasicCvpAmount[1];
					Long lLngOldCvp = lLngBasicCvpAmount[2];
					Long lLngNewCvp = lLngBasicCvpAmount[3];
					
					int lIntFromYear = Integer.parseInt(lArrStrEffRange[0].substring(6));
					int lIntToYear =  Integer.parseInt(lArrStrEffRange[1].substring(6));
					int lIntFromMonth =Integer.parseInt(lArrStrEffRange[0].substring(3, 5));
					int lIntToMonth =Integer.parseInt(lArrStrEffRange[1].substring(3, 5));
					
					boolean isMonthYearSame = false;
					for (int yearCtr = lIntFromYear; yearCtr <= lIntToYear; yearCtr++) {
						for (int monthCtr = 1; monthCtr <= 12; monthCtr++) {
							
							Long lLngNewPension = Long.valueOf(0);
							Long lLngNewPnsnWtCvpAmt =Long.valueOf(0);
							Long lLngNewPunishAmt = Long.valueOf(0);
							Long lLngNewDAAmt = Long.valueOf(0);
							Double lDbNewDAAmt = 0D;
							Long lLngNewNetPnsion = Long.valueOf(0);
							Long lLngOldPension = Long.valueOf(0);
							Long lLngOldPnsnWtCvpAmt =Long.valueOf(0);
							Long lLngOldPunishAmt = Long.valueOf(0);
							Long lLngOldDPAmt = Long.valueOf(0);
							Long lLngOldDAAmt = Long.valueOf(0);
							Double lDbOldDAAmt = 0D;
							BigDecimal lBgDcmlDAAmount = BigDecimal.ZERO;
							Long lLngOldNetPension = Long.valueOf(0);
							int lIntReEmpEffDays = 0;
							
							isEndMonth = false;
							
							/**
							 * ############## Date Column logic Starts
							 * ##############
							 */

							if ((yearCtr == lIntFromYear) && monthCtr == 1) // for
							// effective_from
							// date
							{
								monthCtr = lIntFromMonth;
								lDtFromDtUpdated = lDtEffFromDate;

								lObjCalendar = Calendar.getInstance();

								if ((yearCtr == lIntToYear) && (monthCtr == lIntToMonth)) {
									lDtToDtUpdated = lDtEffToDate;
								} else {
									lObjCalendar.setTime(lDtFromDtUpdated);
									lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
									lDtToDtUpdated = lObjCalendar.getTime();
								}
							}
							
							if (yearCtr == lIntToYear && monthCtr == lIntToMonth) {
								isEndMonth = true;
							}
							
							// Calculation of Punishment Cut Starts
							long lLngEffDaysinMonth = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtEffFromDate, lDtEffToDate, lLngNewBasic - lLngNewCvp, lLngNewPunishAmt, true);

							lObjCalendar.setTime(lDtFromDtUpdated);
							int lIntTotalDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

							if (lDtPunishFromDate.length > lIntPnsnCutCntr && lDtPunishFromDate[lIntPnsnCutCntr] != null) {
								lLngNewPunishAmt = calcPnsnCut(inputMap, lDtFromDtUpdated, lDtToDtUpdated, lDtPunishFromDate, lDtPunishToDate, lDbPunishAmt, lIntPnsnCutCntr, lLngEffDaysinMonth,
										lIntTotalDaysInMonth);
							}
							lLngRangeNewPunishAmt = lLngRangeNewPunishAmt +lLngNewPunishAmt;
							
							lLngOldPunishAmt = lLngNewPunishAmt;

							// Calculation of Punishment Cut end
							
							/**
							 * ############## For calulation of Basic For Start
							 * and End Date Starts ##############
							 */
							lLngNewPension = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtEffFromDate, lDtEffToDate, lLngNewBasic - lLngNewCvp,lLngNewPunishAmt, false);
							//New Pension amount without deducting cvp amount
							lLngNewPnsnWtCvpAmt = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtEffFromDate, lDtEffToDate, lLngNewBasic, lLngNewPunishAmt, false);
							lLngRangeNewPension = lLngRangeNewPension + lLngNewPension;

							lLngOldPension = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtEffFromDate, lDtEffToDate, lLngOldBasic - lLngOldCvp, lLngOldPunishAmt, false);
							//Old Pension amount without deducting cvp amount
							lLngOldPnsnWtCvpAmt = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtEffFromDate, lDtEffToDate, lLngOldBasic, lLngOldPunishAmt, false);
							lLngRangeOldPension =  lLngRangeOldPension + lLngOldPension;

							/**
							 * ############## For calulation of Basic For Start
							 * and End Date Ends ##############
							 */
							
							
							
							
							//Calculation of DA Amount start
							
							lDbNewDAAmt = (lLngNewPnsnWtCvpAmt - lLngNewPunishAmt) * (lDbNewDARate/100);
							lBgDcmlDAAmount = new BigDecimal(lDbNewDAAmt).setScale(0, BigDecimal.ROUND_UP);
							lLngNewDAAmt = lBgDcmlDAAmount.longValue();
							/** ############### RE Employment Effect Starts ############## */
							if(lStrDAInPnsnSal.length >0)
							{
								if(!"".equals(lStrDAInPnsnSal[lIntReEmpCntr]))
								{
									if("Salary".equals(lStrDAInPnsnSal[lIntReEmpCntr]))
									{
										if (lDtReEmpEffFrom != null && lDtReEmpEffFrom != null) {
											lIntReEmpEffDays = getReEmpEffectDays(lDtReEmpEffFrom, lDtReEmpEffTo, lIntReEmpCntr, lDtFromDtUpdated, lDtToDtUpdated);
										}
							
										lDbNewDAAmt =(double) (lLngNewDAAmt * (lLngEffDaysinMonth - lIntReEmpEffDays) / lLngEffDaysinMonth);
										lBgDcmlDAAmount = new BigDecimal(lDbNewDAAmt).setScale(0, BigDecimal.ROUND_UP);
										lLngNewDAAmt = lBgDcmlDAAmount.longValue();
									}
								}
							}
							lLngRangeNewDAAmt = lLngRangeNewDAAmt + lLngNewDAAmt;
							
							
							if (lDtPnsnrDOR != null && (lDtPnsnrDOR.before(IFMSCommonServiceImpl.getDateFromString("31/07/2004")) 
									|| lDtPnsnrDOR.equals(IFMSCommonServiceImpl.getDateFromString("31/07/2004"))) 
									&& lDtFromDtUpdated.after(IFMSCommonServiceImpl.getDateFromString("31/07/2004")))
							{
								lLngOldDPAmt = calcDP(inputMap, lDtFromDtUpdated,lLngHeadCode, lLngOldPnsnWtCvpAmt - lLngOldPunishAmt, lDbOldBasic, 0, 
									 false, lLngEffDaysinMonth, lIntTotalDaysInMonth);
								
							}
							lLngRangeOldDPAmt = lLngRangeOldDPAmt + lLngOldDPAmt;
							lDbOldDAAmt = (lLngOldPnsnWtCvpAmt + lLngOldDPAmt - lLngOldPunishAmt) * (lDbOldDARate/100);
							lBgDcmlDAAmount = new BigDecimal(lDbOldDAAmt).setScale(0, BigDecimal.ROUND_UP);
							lLngOldDAAmt = 	lBgDcmlDAAmount.longValue();					
							/** ############### RE Employment Effect Starts ############## */
							if(lStrDAInPnsnSal.length >0)
							{
								if(!"".equals(lStrDAInPnsnSal[lIntReEmpCntr]))
								{
									if("Salary".equals(lStrDAInPnsnSal[lIntReEmpCntr]))
									{
										if (lDtReEmpEffFrom != null && lDtReEmpEffFrom != null) {
											lIntReEmpEffDays = getReEmpEffectDays(lDtReEmpEffFrom, lDtReEmpEffTo, lIntReEmpCntr, lDtFromDtUpdated, lDtToDtUpdated);
										}
							
										lDbOldDAAmt = (double) (lLngOldDAAmt * (lLngEffDaysinMonth - lIntReEmpEffDays) / lLngEffDaysinMonth);
										lBgDcmlDAAmount = new BigDecimal(lDbOldDAAmt).setScale(0, BigDecimal.ROUND_UP);
										lLngOldDAAmt = 	lBgDcmlDAAmount.longValue();	
									   
									}
								}
							}
							lLngRangeOldDAAmt = lLngRangeOldDAAmt + lLngOldDAAmt;
							
							//Calculation of DA Amount end
							
							// For ReEmployment counter
							if (lDtReEmpEffFrom.length > (lIntReEmpCntr + 1)
									&& lDtReEmpEffFrom[lIntReEmpCntr + 1] != null
									&& (lDtReEmpEffFrom[lIntReEmpCntr + 1].before(lDtToDtUpdated) || lDtReEmpEffFrom[lIntReEmpCntr + 1].equals(lDtToDtUpdated)
											|| lDtReEmpEffTo[lIntReEmpCntr].before(lDtToDtUpdated) || lDtReEmpEffTo[lIntReEmpCntr].equals(lDtToDtUpdated))) {
								lIntReEmpCntr++;
							}

							// For punishment Cut counter
							if (lDtPunishFromDate.length > (lIntPnsnCutCntr + 1)
									&& lDtPunishFromDate[lIntPnsnCutCntr + 1] != null
									&& (lDtPunishFromDate[lIntPnsnCutCntr + 1].before(lDtToDtUpdated) || lDtPunishFromDate[lIntPnsnCutCntr + 1].equals(lDtToDtUpdated)
											|| lDtPunishToDate[lIntPnsnCutCntr].before(lDtToDtUpdated) || lDtPunishToDate[lIntPnsnCutCntr].equals(lDtToDtUpdated))) {
								lIntPnsnCutCntr++;
							}
							
							lObjCalendar.setTime(lDtFromDtUpdated);
							lObjCalendar.add(Calendar.MONTH, 1);
							lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
							lDtFromDtUpdated = lObjCalendar.getTime();

							lObjCalendar.setTime(lDtToDtUpdated);
							lObjCalendar.add(Calendar.MONTH, 1);
							lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
							lDtToDtUpdated = lObjCalendar.getTime();

							if (lDtEffToDate.before(lDtToDtUpdated)) {
								lDtToDtUpdated = lDtEffToDate;
							}
							if (yearCtr == lIntToYear && monthCtr == lIntToMonth) {
								break;
							}
							
						}
					}
					
				}
				lLngRangeNewNetPension = (lLngRangeNewPension - lLngRangeNewPunishAmt) + lLngRangeNewDAAmt;
				lLngTotalNewNetPension = lLngTotalNewNetPension + lLngRangeNewNetPension;
				lLngRangeOldNetPension = (lLngRangeOldPension - lLngRangeNewPunishAmt) + lLngRangeOldDAAmt + lLngRangeOldDPAmt;
				lLngTotalOldNetPension = lLngTotalOldNetPension + lLngRangeOldNetPension;
				lLstInnerList.add(lLngRangeNewPension);
				lLstInnerList.add(lLngRangeNewPunishAmt);
				lLstInnerList.add(lLngRangeNewDAAmt);
				lLstInnerList.add(lLngRangeNewNetPension);
				lLstInnerList.add(lObjArr[4]);
				lLstInnerList.add(lLngRangeOldPension);
				lLstInnerList.add(lLngRangeNewPunishAmt);
				lLstInnerList.add(lLngRangeOldDPAmt);
				lLstInnerList.add(lLngRangeOldDAAmt);
				lLstInnerList.add(lLngRangeOldNetPension);
				lLstArrOuter.add(lLstInnerList);
				
				if(lIntFlag == 1 || (lIntFlag == 0 && lIntCnt == lLstDARateRange.size()-1))
				{
					lLstInnerList = new ArrayList();
					lLstInnerList.add(getChar(8, "-"));
					lLstInnerList.add(getChar(26, "-"));
					lLstInnerList.add(getChar(4, "-"));
					lLstInnerList.add(getChar(11, "-"));
					lLstInnerList.add(getChar(7, "-"));
					lLstInnerList.add(getChar(9, "-"));
					lLstInnerList.add(getChar(11, "-"));
					lLstInnerList.add(getChar(8, "-"));
					lLstInnerList.add(getChar(11, "-"));
					lLstInnerList.add(getChar(7, "-"));
					lLstInnerList.add(getChar(9, "-"));
					lLstInnerList.add(getChar(9, "-"));
					lLstInnerList.add(getChar(11, "-"));
					lLstArrOuter.add(lLstInnerList);
					lLstInnerList = new ArrayList();
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add(lLngTotalNewNetPension);
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add("");
					lLstInnerList.add(lLngTotalOldNetPension);
					lLstArrOuter.add(lLstInnerList);
					lLngTotalDiffAmt = lLngTotalNewNetPension - lLngTotalOldNetPension;
					
					lStrDisplay =objExport.getReportFile(lLstArrOuter, columnHeading,lSbHeading.toString(),
							"",null,nMode ,true,1,true,"|");
					lStrDisplayString = lStrDisplayString + lStrDisplay;
					
					Double lDbDiffAdvRecAmt=0D;
					Double lDbInstallmentAmt=0D;
									
					lDbDiffAdvRecAmt = lLngTotalDiffAmt - (lDbAdvanceAmt + lDbRecoveryAmt);
					lDbInstallmentAmt = lDbDiffAdvRecAmt/5;
					
					lLngInstltAmt = lDbInstallmentAmt.longValue();
					lLngInstlt5Amt = (long) (lLngInstltAmt +(lDbDiffAdvRecAmt%5));
					lSbFooter.append("Difference from "+lStrEffectiveFromDt+" to 31/03/2009 - (Advance + Other Recovery) "+ lLngTotalDiffAmt.toString()+" - ("+lDbAdvanceAmt.longValue()+" + "+lDbRecoveryAmt.longValue()+") = "+lDbDiffAdvRecAmt.longValue());
					lSbFooter.append("\r\n");
					lSbFooter.append("Differance Payable in Five Installment        "+lDbDiffAdvRecAmt.longValue()+" / 5 = "+lLngInstltAmt);
					lSbFooter.append("\r\n");
					lSbFooter.append("1St Inst    2nd Inst    3rd Inst    4th Inst    5th Inst");
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%-12s", lLngInstltAmt));
					lSbFooter.append(String.format("%-12s", lLngInstltAmt));
					lSbFooter.append(String.format("%-12s", lLngInstltAmt));
					lSbFooter.append(String.format("%-12s", lLngInstltAmt));
					lSbFooter.append(String.format("%-12s", lLngInstlt5Amt));
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append("Paid Date");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					if(lIntFlag == 1)
					{
					    lIntFlag = 2;
						lSbFooter.append((char) 12);
					}
					lStrDisplayString = lStrDisplayString + lSbFooter.toString();
					lSbFooter = new StringBuilder();
					lLstArrOuter = new ArrayList();
					lLngTotalNewNetPension = Long.valueOf(0);
					lLngTotalOldNetPension = Long.valueOf(0);
					
					
				}
				
			}
			if(lIntFlag == 2)
			{
				lSbHeading =  new StringBuilder();
				lSbHeading.append("\r\n");
				lSbHeading.append(getChar(132, "-"));
				lSbHeading.append("\r\n");
				lSbHeading.append("|                                  Due                                     |                        Drawn                         |");
				lSbHeading.append("\r\n");
				
				lLstInnerList = new ArrayList();
				lLstInnerList.add(getChar(8, "-"));
				lLstInnerList.add(getChar(26, "-"));
				lLstInnerList.add(getChar(4, "-"));
				lLstInnerList.add(getChar(11, "-"));
				lLstInnerList.add(getChar(7, "-"));
				lLstInnerList.add(getChar(9, "-"));
				lLstInnerList.add(getChar(11, "-"));
				lLstInnerList.add(getChar(8, "-"));
				lLstInnerList.add(getChar(11, "-"));
				lLstInnerList.add(getChar(7, "-"));
				lLstInnerList.add(getChar(9, "-"));
				lLstInnerList.add(getChar(9, "-"));
				lLstInnerList.add(getChar(11, "-"));
				lLstArrOuter.add(lLstInnerList);
				lLstInnerList = new ArrayList();
				lLstInnerList.add("");
				lLstInnerList.add("");
				lLstInnerList.add("");
				lLstInnerList.add("");
				lLstInnerList.add("");
				lLstInnerList.add("");
				lLstInnerList.add(lLngTotalNewNetPension);
				lLstInnerList.add("");
				lLstInnerList.add("");
				lLstInnerList.add("");
				lLstInnerList.add("");
				lLstInnerList.add("");
				lLstInnerList.add(lLngTotalOldNetPension);
				lLstArrOuter.add(lLstInnerList);
				
				lStrDisplay =objExport.getReportFile(lLstArrOuter, columnHeading,lSbHeading.toString(),
						"",null,nMode ,true,1,true,"|");
			
				lSbFooter = new StringBuilder();
				lLngDiffAmt=Long.valueOf(0);
				lLngDiffAmt = lLngTotalNewNetPension-lLngTotalOldNetPension;
				lSbFooter.append("Differance from 01/04/2009 to "+lStrEffectiveToDt+"       "+lLngTotalNewNetPension+" - "+lLngTotalOldNetPension+" = "+lLngDiffAmt+" Paid in Cash");
			}
			else
			{
				lSbFooter = new StringBuilder();
				lStrDisplay = "";
			}
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
								
			LoginDetails lObjLoginDetails=new LoginDetails();
			lObjLoginDetails=(LoginDetails) inputMap.get("baseLoginVO");
			OrgEmpMst lObjOrgEmpMst=lObjLoginDetails.getEmployee();
			String lStrEmpName=((lObjOrgEmpMst.getEmpPrefix() != null) ? lObjOrgEmpMst.getEmpPrefix()+" " : "")
								+((lObjOrgEmpMst.getEmpFname() != null ) ? lObjOrgEmpMst.getEmpFname()+" " : "")
								+((lObjOrgEmpMst.getEmpMname() != null) ? lObjOrgEmpMst.getEmpMname()+" " : "")
								+((lObjOrgEmpMst.getEmpLname() != null) ? lObjOrgEmpMst.getEmpLname()+" " : "");
			lSbFooter.append(String.format("%-100s", "Developed by "+lStrEmpName+" (Pay And Accounts Office)"));
			lSbFooter.append(String.format("%32s", "Asstt. Pay and Accounts officer"));
			lStrDisplay = lStrDisplay + lSbFooter.toString();
			lStrDisplayString = lStrDisplayString + lStrDisplay;
			
			//For multiple row end
			
			
			
		    //DA Rate Changed duration end
			
			//lSbLineDisplay.append("<div><pre>");
			//lSbLineDisplay.append(lSbHeading);
			
			
			/*
			Long lLngTotalNetPensionNew = Long.valueOf(0);
			Long lLngTotalNetPensionOld = Long.valueOf(0);
			String lStrDisplay = "";
			Integer lIntFlag = 0;
			List lLstArrOuter = new ArrayList();
			for(int lIntCnt = 0;lIntCnt<lLstDARateRange.size();lIntCnt++)
			{
				Double lDbNewDARate = 0D;
				Long lLngNewPension = Long.valueOf(0);
				Long lLngNewDAAmt = Long.valueOf(0);
				Long lLngNewNetPnsion = Long.valueOf(0);
				Double lDbOldDARate = 0D;
				Long lLngOldPension = Long.valueOf(0);
				Double lDbDPRate = 0D;
				Long lLngOldDPAmt = Long.valueOf(0);
				Long lLngOldDAAmt = Long.valueOf(0);
				Long lLngOldNetPension = Long.valueOf(0);
								
				String lStrDARateRange = lLstDARateRange.get(lIntCnt);
				String[] lArrStrDARateRange=lStrDARateRange.split("~");
				
				Date lDtEffFromDate = lObjSimpleDateFormat.parse(lArrStrDARateRange[0]);
			
				Date lDtEffToDate = lObjSimpleDateFormat.parse(lArrStrDARateRange[1]);
				
				Calendar lObjCalendar = null;
				lObjCalendar = Calendar.getInstance();
				lObjCalendar.setTime(lDtEffFromDate);
				int lIntEffFromDate = lObjCalendar.get(Calendar.DATE);
				
				int lIntFromDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				System.out.println("No of days in month="+lIntFromDaysInMonth);
				int lIntEffFromYear=lObjCalendar.get(Calendar.YEAR); 
				int lIntEffFromMonth=lObjCalendar.get(Calendar.MONTH);
				
				lObjCalendar.setTime(lDtEffToDate);
				int lIntToDaysInMonth  = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				int lIntEffToDate = lObjCalendar.get(Calendar.DATE);
				int lIntEffToYear=lObjCalendar.get(Calendar.YEAR);
				int lIntEffToMonth=lObjCalendar.get(Calendar.MONTH);
				int lIntMonths = 0;
				int lIntEffDaysForPnsn = 0;
				if(lIntCnt == 0)
				{			
					lIntEffDaysForPnsn = lIntEffToDate - lIntEffFromDate + 1;
					
					if(lIntEffFromDate != 1)
					{
						lIntMonths = (lIntEffToYear - lIntEffFromYear)*12 + (lIntEffToMonth - lIntEffFromMonth);
					}
					else
					{
						lIntMonths = (lIntEffToYear - lIntEffFromYear)*12 + (lIntEffToMonth - lIntEffFromMonth) + 1;
					}
					
				}
				else if(lIntCnt==lLstDARateRange.size()-1)
				{
					lIntEffDaysForPnsn = lIntEffToDate - lIntEffFromDate + 1;
					
					if(lIntToDaysInMonth != lIntEffDaysForPnsn)
					{
						lIntMonths = (lIntEffToYear - lIntEffFromYear)*12 + (lIntEffToMonth - lIntEffFromMonth);
					}
					else
					{
						lIntMonths = (lIntEffToYear - lIntEffFromYear)*12 + (lIntEffToMonth - lIntEffFromMonth) + 1;
					}
				}
				else
				{
					lIntMonths = (lIntEffToYear - lIntEffFromYear)*12 + (lIntEffToMonth - lIntEffFromMonth) + 1;	
				}
				System.out.println("Effective days in month="+lIntEffDaysForPnsn);
				System.out.println("No of months ="+lIntMonths);
				
				List lLstInnerList = new ArrayList();
				
				List lLstOfObjArr = null;
				Object[] lObjArr = null;
				
				lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, "DA_2006", 0, lDtEffFromDate);
				if (!lLstOfObjArr.isEmpty()) {
					lObjArr = (Object[]) lLstOfObjArr.get(0);
					if (lObjArr != null) {
						lDbNewDARate = ((BigDecimal) lObjArr[4]).doubleValue();
						//lLngNewDARate = Long.valueOf(lObjArr[4].toString());
						lLstInnerList.add(lDbNewDARate.longValue());
					}
				}
				lLstInnerList.add(lArrStrDARateRange[0] + " To "+lArrStrDARateRange[1]);	
				
				if(lIntCnt == 0)
				{			
					if(lIntEffFromDate != 1)
					{
						lLstInnerList.add(lIntMonths +" Mnts "+lIntEffDaysForPnsn+" Days");
					}
					else
					{
						lLstInnerList.add(lIntMonths +" Mnts");
					}
				}
				else if(lIntCnt==lLstDARateRange.size()-1)
				{
					if(lIntToDaysInMonth != lIntEffDaysForPnsn)
					{
						lLstInnerList.add(lIntMonths +" Mnts "+lIntEffDaysForPnsn+" Days");
					}
					else
					{
						lLstInnerList.add(lIntMonths +" Mnts");
					}
				}
				else
				{
					lLstInnerList.add(lIntMonths +" Mnts");
				}
//				if(lIntEffDaysForPnsn == 0)
//					lLstInnerList.add(lIntMonths +" Mnts");
//				else
//					lLstInnerList.add(lIntMonths +" Mnts "+lIntEffDaysForPnsn+" Days");
						
				//lLngNewPension = calcPension(lDtEffFromDate, lDtEffToDate, lDtEffFromDate, lDtEffToDate, lDbNewBasicAmt-lDbNewCvpAmt, false);
				lLngNewPension =(long) ((lDbNewBasicAmt-lDbNewCvpAmt)*lIntMonths);
				if(lIntCnt == 0)
				{			
					if(lIntEffFromDate != 1)
					{
						lLngNewPension = lLngNewPension +(long) ((lDbNewBasicAmt-lDbNewCvpAmt)*lIntEffDaysForPnsn/lIntFromDaysInMonth);
					}
					
				}
				else if(lIntCnt==lLstDARateRange.size()-1)
				{
					if(lIntToDaysInMonth != lIntEffDaysForPnsn)
					{
						lLngNewPension = lLngNewPension +(long) ((lDbNewBasicAmt-lDbNewCvpAmt)*lIntEffDaysForPnsn/lIntFromDaysInMonth);
					}
				}
				
				lLstInnerList.add(lLngNewPension);
				
				lLngNewDAAmt = Math.round(lDbNewBasicAmt * (lDbNewDARate/100)) * lIntMonths;
				lLstInnerList.add(lLngNewDAAmt);
				
				lLngNewNetPnsion = lLngNewPension +lLngNewDAAmt;
				lLstInnerList.add(lLngNewNetPnsion);
				
				//Drawn Amount
				
				lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, "DA_1996_DP", 0, lDtEffFromDate);
				if (!lLstOfObjArr.isEmpty()) {
					lObjArr = (Object[]) lLstOfObjArr.get(0);
					if (lObjArr != null) {
						lDbOldDARate =  ((BigDecimal) lObjArr[4]).doubleValue();
						lLstInnerList.add(lDbOldDARate.longValue());
					}
				}
				
				//lLngOldPension = calcPension(lDtEffFromDate, lDtEffToDate, lDtEffFromDate, lDtEffToDate, lDbOldBasicAmt-lDbOldCvpAmt, false);
				lLngOldPension = (long) ((lDbOldBasicAmt-lDbOldCvpAmt) * lIntMonths);
				if(lIntCnt == 0)
				{			
					if(lIntEffFromDate != 1)
					{
						lLngOldPension = lLngOldPension +(long) ((lDbOldBasicAmt-lDbOldCvpAmt)*lIntEffDaysForPnsn/lIntFromDaysInMonth);
					}
					
				}
				else if(lIntCnt==lLstDARateRange.size()-1)
				{
					if(lIntToDaysInMonth != lIntEffDaysForPnsn)
					{
						lLngOldPension = lLngOldPension +(long) ((lDbOldBasicAmt-lDbOldCvpAmt)*lIntEffDaysForPnsn/lIntFromDaysInMonth);
					}
					
				}
				lLstInnerList.add(lLngOldPension);
				
				lLstOfObjArr = getRateFromHeadcodeRateRltMap(inputMap, lLngHeadCode, "DP", 0, lDtEffFromDate);
				if (!lLstOfObjArr.isEmpty()) {
					lObjArr = (Object[]) lLstOfObjArr.get(0);
					if (lObjArr != null) {
						lDbDPRate = ((BigDecimal) lObjArr[4]).doubleValue();;
						lLngOldDPAmt = Math.round(lDbOldBasicAmt * (lDbDPRate/100))*lIntMonths;
						lLstInnerList.add(lLngOldDPAmt);
					}
				}
				
				lLngOldDAAmt = (long) (Math.round((Math.round(lDbOldBasicAmt * (lDbDPRate/100))+lDbOldBasicAmt)*(lDbOldDARate/100))* lIntMonths);
				lLstInnerList.add(lLngOldDAAmt);
				
				lLngOldNetPension = lLngOldPension + lLngOldDPAmt + lLngOldDAAmt;
				lLstInnerList.add(lLngOldNetPension);
				
				lLstArrOuter.add(lLstInnerList);
				lIntFlag =1;
						
						
			}*/
			
			//lSbLineDisplay.append("</pre></div>");
			inputMap.put("PrintString", lSbLineDisplay.toString());
			//inputMap.put("PrintTotalAmount", sbTotalAmounts.toString());
			inputMap.put("DisplayString",  lSbLineDisplay.toString().replace("</pre></div>", "").replace("<div><pre>", ""));
			inputMap.put("InstallmentAmt", lLngInstltAmt);
			inputMap.put("Installment5Amt", lLngInstlt5Amt);
			inputMap.put("CashAmount",lLngDiffAmt);
			inputMap.put("BillFlag", lStrBillFlag);
			
			resObj.setResultValue(inputMap);
			
			Map lDetailMap =new HashMap();
			String lStrExportTo = StringUtility.getParameter("exportTo", request);	
			if((DBConstants.DIS_ONSCREEN).equals(lStrExportTo))
				lDetailMap.put(DBConstants.DIS_ONSCREEN,lStrDisplayString);
			else if((DBConstants.DIS_TEXTFILE).equals(lStrExportTo))
				lDetailMap.put(DBConstants.DIS_TEXTFILE,lStrDisplayString);
			
			ReportExportHelper rptExpHelper = new ReportExportHelper();
			rptExpHelper.getExportData(resObj,lStrExportTo,inputMap,lDetailMap,false);
			resObj.setResultValue(inputMap);
			
			if((DBConstants.DIS_ONSCREEN).equals(lStrExportTo))
			   resObj.setViewName("SixthPayArrearReport");

		}
		catch (Exception e) {
			
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	
	private long calcPnsnCut(Map inputMap, Date lDtFromDtUpdated, Date lDtToDtUpdated, Date[] lDtPnsnCutFrm, Date[] lDtPnsnCutTo, Double[] lDbPnsnCutAmount, int pnsnCutCtr, long lLngEffDaysinMonth,
			int lIntTotalDaysInMonth) throws Exception {

		double lDbPnsnCutAmt = 0D;
		Calendar lObjCalendar = null;
		int lIntDaysInMonth = 0;
		int lIntEffDaysForPnsnCut = 0;
		int lIntPnsnCutDays = 0;
		try {
			for (int i = pnsnCutCtr; i < lDbPnsnCutAmount.length; i++) {
				if ((lDtPnsnCutFrm[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtPnsnCutFrm[i].compareTo(lDtToDtUpdated) <= 0) && (lDtPnsnCutTo[i].compareTo(lDtToDtUpdated) > 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtPnsnCutFrm[i]);
					lIntDaysInMonth = lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					lIntPnsnCutDays = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForPnsnCut = lIntDaysInMonth - lIntPnsnCutDays + 1;
					lDbPnsnCutAmt += (lDbPnsnCutAmount[i] * lIntEffDaysForPnsnCut) / lIntTotalDaysInMonth;
				} else if ((lDtPnsnCutTo[i].compareTo(lDtToDtUpdated) <= 0) && (lDtPnsnCutTo[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtPnsnCutFrm[i].compareTo(lDtFromDtUpdated) < 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtPnsnCutTo[i]);
					lIntPnsnCutDays = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForPnsnCut = lIntPnsnCutDays;
					lDbPnsnCutAmt += (lDbPnsnCutAmount[i] * lIntEffDaysForPnsnCut) / lIntTotalDaysInMonth;
				} else if ((lDtPnsnCutFrm[i].compareTo(lDtFromDtUpdated) >= 0) && (lDtPnsnCutTo[i].compareTo(lDtToDtUpdated) <= 0)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtPnsnCutFrm[i]);
					int FirstDate = lObjCalendar.get(Calendar.DATE);

					lObjCalendar.setTime(lDtPnsnCutTo[i]);
					int ToDate = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForPnsnCut = ToDate - FirstDate + 1;
					lDbPnsnCutAmt += (lDbPnsnCutAmount[i] * lIntEffDaysForPnsnCut) / lIntTotalDaysInMonth;
				} else if (lDtPnsnCutFrm[i].before(lDtFromDtUpdated) && lDtPnsnCutTo[i].after(lDtToDtUpdated)) {
					lObjCalendar = Calendar.getInstance();
					lObjCalendar.setTime(lDtFromDtUpdated);
					int FirstDate = lObjCalendar.get(Calendar.DATE);

					lObjCalendar.setTime(lDtToDtUpdated);
					int ToDate = lObjCalendar.get(Calendar.DATE);

					lIntEffDaysForPnsnCut = ToDate - FirstDate + 1;
					lDbPnsnCutAmt += (lDbPnsnCutAmount[i] * lIntEffDaysForPnsnCut) / lIntTotalDaysInMonth;
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return Math.round(lDbPnsnCutAmt);
	}
	
	public ResultObject getDaRateFromPayCommission(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrAjaxResult = null;
		try {
			String lStrPayCommissionType = StringUtility.getParameter("PayCommissionType", request);
			RevisedArrearCalcDAO lObjRArrearCalcDAO = new RevisedArrearCalcDAOImpl(serv.getSessionFactory());
			List lArrListDARate = lObjRArrearCalcDAO.getDaRateFromPayCommission(lStrPayCommissionType);
			lStrAjaxResult = new AjaxXmlBuilder().addItems(lArrListDARate, "desc", "id").toString();
			inputMap.put("ajaxKey", lStrAjaxResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getDaRateFromPayCommission: ");
		}
		return resObj;
	}
	
	public ResultObject generateDAArrearRecoveryReport(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSmplDtFormat = new SimpleDateFormat("dd/MM/yyyy");
		ReportExportHelper objExport = new ReportExportHelper();
		Integer nMode = 80;
		ReportExportHelper rptExpHelper = new ReportExportHelper();
		try {
			String[] lStrPayCommissionType = StringUtility.getParameterValues("cmbPayCommissionType", request);
			String[] lStrBasic = StringUtility.getParameterValues("txtBasic", request);
			String[] lStrDARateEffFrom = StringUtility.getParameterValues("txtDARateEffFrom", request);
			String[] lStrDARateEffTo = StringUtility.getParameterValues("txtDARateEffTo", request);
			String[] lStrOldDARate = StringUtility.getParameterValues("cmbOldDARate", request);
			String[] lStrNewDARate = StringUtility.getParameterValues("cmbNewDARate", request);

			Double[] lDbBasic = new Double[lStrBasic.length];
			Date[] lDtDARateEffFrom = new Date[lStrDARateEffFrom.length];
			Date[] lDtDARateEffTo = new Date[lStrDARateEffTo.length];
			Double[] lDbOldDARate = new Double[lStrOldDARate.length];
			Double[] lDbNewDARate = new Double[lStrNewDARate.length];
			Double lDbOldDAAmount = 0D;
			Double lDbNewDAAmount = 0D;
			Date lDtFromDtUpdated = null;
			Calendar lObjCalendar = Calendar.getInstance();
			Date lDtToDtUpdated = null;

			Integer lIntReEmpCntr = 0;
			Integer lIntReEmpEffDays = 0;
			BigDecimal lBgDcmlDAAmount = BigDecimal.ZERO;
			Double lDbNewDAAmt = 0D;
			Long lLngNewDAAmt = 0L;
			Double lDbOldDAAmt = 0D;
			Long lLngOldDAAmt = 0L;
			for (Integer lInt = 0; lInt < lStrPayCommissionType.length; lInt++) {

				if (!"".equals(lStrBasic[lInt])) {
					lDbBasic[lInt] = Double.parseDouble(lStrBasic[lInt]);
				} else {
					lDbBasic[lInt] = 0D;
				}

				if (!"".equals(lStrDARateEffFrom[lInt])) {
					lDtDARateEffFrom[lInt] = IFMSCommonServiceImpl.getDateFromString(lStrDARateEffFrom[lInt]);
				}

				if (!"".equals(lStrDARateEffTo[lInt])) {
					lDtDARateEffTo[lInt] = IFMSCommonServiceImpl.getDateFromString(lStrDARateEffTo[lInt]);
				}

				if (!"".equals(lStrOldDARate[lInt])) {
					lDbOldDARate[lInt] = Double.parseDouble(lStrOldDARate[lInt]);
				} else {
					lDbOldDARate[lInt] = 0D;
				}

				if (!"".equals(lStrNewDARate[lInt])) {
					lDbNewDARate[lInt] = Double.parseDouble(lStrNewDARate[lInt]);
				} else {
					lDbNewDARate[lInt] = 0D;
				}
			}

			/**
			 * ReEmp input
			 */

			String[] lStrReEmpEffFrom = StringUtility.getParameterValues("txtOldReEmpEffFrom", request);
			String[] lStrReEmpEffTo = StringUtility.getParameterValues("txtOldReEmpEffTo", request);
			String[] lStrDAInPnsnSal = StringUtility.getParameterValues("cmbDAInPnsnSal", request);

			Date[] lDtReEmpEffFrom = new Date[lStrReEmpEffFrom.length];
			Date[] lDtReEmpEffTo = new Date[lStrReEmpEffTo.length];
			for (Integer i = 0; i < lStrReEmpEffFrom.length; i++) {
				if (!"".equals(lStrReEmpEffFrom[i]) && lStrReEmpEffFrom[i] != null) {
					lDtReEmpEffFrom[i] = IFMSCommonServiceImpl.getDateFromString(lStrReEmpEffFrom[i]);
				}
				if (!"".equals(lStrReEmpEffTo[i]) && lStrReEmpEffTo[i] != null) {
					lDtReEmpEffTo[i] = IFMSCommonServiceImpl.getDateFromString(lStrReEmpEffTo[i]);
				}
			}

			String lStrHeading = "DA Arrears/Recovery Report";

			List<Object> lLstInnerList = new ArrayList<Object>();
			List<Object> lLstArrOuter = new ArrayList<Object>();

			for (Integer lInt = 0; lInt < lStrPayCommissionType.length; lInt++) {

				Long lLngRangeBasicAmt = 0L;
				Long lLngRangeOldDaAmt = 0L;
				Long lLngRangeNewDaAmt = 0L;
				Long lLngRangeDaDiffAmt = 0L;

				Integer lIntFromYear = Integer.parseInt(lStrDARateEffFrom[lInt].substring(6));
				Integer lIntToYear = Integer.parseInt(lStrDARateEffTo[lInt].substring(6));

				Integer lIntFromMonth = Integer.parseInt(lStrDARateEffFrom[lInt].substring(3, 5));
				Integer lIntToMonth = Integer.parseInt(lStrDARateEffTo[lInt].substring(3, 5));
				Date lDtEffToDate = IFMSCommonServiceImpl.getDateFromString(lStrDARateEffTo[lInt]);
				for (Integer yearCtr = lIntFromYear; yearCtr <= lIntToYear; yearCtr++) {
					for (Integer monthCtr = 1; monthCtr <= 12; monthCtr++) {
						Long lLngNewPension = 0L;
						if ((yearCtr.equals(lIntFromYear)) && monthCtr.equals(1)) // for
						// effective_from
						// date
						{
							monthCtr = lIntFromMonth;
							lDtFromDtUpdated = lDtDARateEffFrom[lInt];

							lObjCalendar = Calendar.getInstance();

							if ((yearCtr.equals(lIntToYear)) && (monthCtr.equals(lIntToMonth))) {
								lDtToDtUpdated = lDtDARateEffTo[lInt];
							} else {
								lObjCalendar.setTime(lDtFromDtUpdated);
								lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
								lDtToDtUpdated = lObjCalendar.getTime();
							}
						}

						Long lLngEffDaysinMonth = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtDARateEffFrom[lInt], lDtDARateEffTo[lInt], lDbBasic[lInt].longValue(), 0L, true);

						lLngNewPension = calcPension(lDtFromDtUpdated, lDtToDtUpdated, lDtDARateEffFrom[lInt], lDtDARateEffTo[lInt], lDbBasic[lInt].longValue(), 0L, false);
						lLngRangeBasicAmt = lLngRangeBasicAmt + lLngNewPension;

						lDbOldDAAmount = (lLngNewPension * (lDbOldDARate[lInt] / 100D));
						lBgDcmlDAAmount = new BigDecimal(lDbOldDAAmount).setScale(0, BigDecimal.ROUND_UP);
						lLngOldDAAmt = lBgDcmlDAAmount.longValue();

						lDbNewDAAmount = (lLngNewPension * (lDbNewDARate[lInt] / 100D));
						lBgDcmlDAAmount = new BigDecimal(lDbNewDAAmount).setScale(0, BigDecimal.ROUND_UP);
						lLngNewDAAmt = lBgDcmlDAAmount.longValue();

						
						
						//Re-Employment
						if (lStrDAInPnsnSal.length > 0) {
							if (!"".equals(lStrDAInPnsnSal[lIntReEmpCntr])) {
								if ("Salary".equals(lStrDAInPnsnSal[lIntReEmpCntr])) {
									if (lDtReEmpEffFrom != null && lDtReEmpEffFrom != null) {
										lIntReEmpEffDays = getReEmpEffectDays(lDtReEmpEffFrom, lDtReEmpEffTo, lIntReEmpCntr, lDtFromDtUpdated, lDtToDtUpdated);
									}

									lDbOldDAAmt = (double) (lDbOldDAAmount.longValue() * (lLngEffDaysinMonth - lIntReEmpEffDays) / lLngEffDaysinMonth);
									lBgDcmlDAAmount = new BigDecimal(lDbOldDAAmt).setScale(0, BigDecimal.ROUND_UP);
									lLngOldDAAmt = lBgDcmlDAAmount.longValue();

									lDbNewDAAmt = (double) (lDbNewDAAmount.longValue() * (lLngEffDaysinMonth - lIntReEmpEffDays) / lLngEffDaysinMonth);
									lBgDcmlDAAmount = new BigDecimal(lDbNewDAAmt).setScale(0, BigDecimal.ROUND_UP);
									lLngNewDAAmt = lBgDcmlDAAmount.longValue();

								}
							}

						}

						lLngRangeNewDaAmt = lLngRangeNewDaAmt + lLngNewDAAmt;
						lLngRangeOldDaAmt = lLngRangeOldDaAmt + lLngOldDAAmt;
						lLngRangeDaDiffAmt = lLngRangeDaDiffAmt + (lLngNewDAAmt - lLngOldDAAmt);

						if (lDtReEmpEffFrom.length > (lIntReEmpCntr + 1)
								&& lDtReEmpEffFrom[lIntReEmpCntr + 1] != null
								&& (lDtReEmpEffFrom[lIntReEmpCntr + 1].before(lDtToDtUpdated) || lDtReEmpEffFrom[lIntReEmpCntr + 1].equals(lDtToDtUpdated)
										|| lDtReEmpEffTo[lIntReEmpCntr].before(lDtToDtUpdated) || lDtReEmpEffTo[lIntReEmpCntr].equals(lDtToDtUpdated))) {
							lIntReEmpCntr++;
						} 
						
						lObjCalendar.setTime(lDtFromDtUpdated);
						lObjCalendar.add(Calendar.MONTH, 1);
						lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
						lDtFromDtUpdated = lObjCalendar.getTime();

						lObjCalendar.setTime(lDtToDtUpdated);
						lObjCalendar.add(Calendar.MONTH, 1);
						lObjCalendar.set(Calendar.DATE, lObjCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
						lDtToDtUpdated = lObjCalendar.getTime();

						if (lDtEffToDate.before(lDtToDtUpdated)) {
							lDtToDtUpdated = lDtEffToDate;
						}
						if (yearCtr.equals(lIntToYear) && monthCtr.equals(lIntToMonth)) {
							break;
						}
					}
				}

				lLstInnerList = new ArrayList<Object>();
				lLstInnerList.add(lObjSmplDtFormat.format(lDtDARateEffFrom[lInt]));
				lLstInnerList.add(lObjSmplDtFormat.format(lDtDARateEffTo[lInt]));
				lLstInnerList.add(lLngRangeBasicAmt);
				lLstInnerList.add(lStrOldDARate[lInt]);
				lLstInnerList.add(lStrNewDARate[lInt]);
				lLstInnerList.add(lLngRangeOldDaAmt);
				lLstInnerList.add(lLngRangeNewDaAmt);
				lLstInnerList.add(lLngRangeDaDiffAmt);
				lLstArrOuter.add(lLstInnerList);
			}

			ColumnVo[] columnHeading = new ColumnVo[8];

			columnHeading[0] = new ColumnVo("From Date", 1, 11, 0, false, false, true);
			columnHeading[1] = new ColumnVo("To Date", 1, 11, 0, true, false, true);
			columnHeading[2] = new ColumnVo("Basic", 2, 10, 0, false, false, true);
			columnHeading[3] = new ColumnVo("Old DA Rate", 2, 7, 0, false, false, true);
			columnHeading[4] = new ColumnVo("New DA Rate", 2, 7, 0, false, false, true);
			columnHeading[5] = new ColumnVo("Old DA Amount", 2, 10, 1, false, false, true);
			columnHeading[6] = new ColumnVo("New DA Amount ", 2, 10, 1, false, false, true);
			columnHeading[7] = new ColumnVo("DA Difference Amount", 2, 9, 1, false, false, true);

			String lStrDisplay = objExport.getReportFile(lLstArrOuter, columnHeading, lStrHeading, "", null, nMode, true, 1,true,"|");

			Map lDetailMap = new HashMap();
			String lStrExportTo = DBConstants.DIS_ONSCREEN;
			if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_ONSCREEN, lStrDisplay);
			} else if ((DBConstants.DIS_TEXTFILE).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_TEXTFILE, lStrDisplay);
			}
			rptExpHelper.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in generateDAArrearRecoveryReport: ");
		}

		return resObj;
	}
}