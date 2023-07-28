package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionArrearDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionArrearDtlsDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeSpecial;
import com.tcs.sgv.pensionpay.valueobject.RltPensionRevisedPayment;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.ValidPcodeView;


public class PensionBillProcessServiceImpl extends ServiceImpl {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Logger Class */
	Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for Current Date */
	Date gDate = null;

	public Map<String, Object> getCurrMonthData(Map<String, Object> inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
		NumberFormat lDblFrmt = new DecimalFormat("0.00");
		ValidPcodeView lObjValidPcodeVO = null;
		List<MonthlyPensionBillVO> lLstMonthlyPensionBillVO = new ArrayList<MonthlyPensionBillVO>();
		MonthlyPensionBillVO lObjMonthlyPensionVO = null;
		List<TrnPensionArrearDtls> lLstInsertArrears = new ArrayList<TrnPensionArrearDtls>();
		TrnPensionArrearDtls lObjArrearVO = null;

		List<TrnPensionArrearDtls> lTrnPaymentLst = null;

		MonthlyPensionBillVO lRatePensionBillVO = null;
		MonthlyPensionBillVO lRatePensionBillVOTemp = null;

		String lStrMoneyOrder = "MONEY ORDER";
		String lStrArrTI = bundleConst.getString("ARREAR.TI");
		String lStrArrPension = bundleConst.getString("ARREAR.PENSION");
		String lStrArrFP1 = bundleConst.getString("ARREAR.FP1");
		String lStrArrFP2 = bundleConst.getString("ARREAR.FP2");
		String lStrArrOMR = bundleConst.getString("ARREAR.OMR");
		String lStrCutPP = bundleConst.getString("CUT.PP");
		String lStrCutPT = bundleConst.getString("CUT.PT");
		String lStrCutIT = bundleConst.getString("CUT.IT");
		String lStrCutST = bundleConst.getString("CUT.ST");
		String lStrCutPerBen = bundleConst.getString("CMN.PBENEFIT");
		String lStrCutTmpBen = bundleConst.getString("CMN.TBENEFIT");
		String lStrVoluntary = bundleConst.getString("CMN.VOLUNTARY");
		String lStrFamilyLost = bundleConst.getString("CMN.FAMILYLOST");
		String lStrFamily = bundleConst.getString("CMN.FAMILY");

		String lStrDate = null;
		String lDateOfDeathYYYYMM = null;
		String lDayOfDeathDD = null;
		String lStrRcvryFlag = null;
		String lStrPenType = null;

		String lStrScheme = null;
		String lStrPnsnrCode = null;
		String lStrPnsnerName = null;
		String lStrPPONo = null;
		String lStrAccNo = "";
		Double lDPPer = 0D;
		Double lTIPer = 0D;
		Long lStrPenRqstId = 0L;
		Long lRecoveryId = 0L;
		Double lCVPMonthlyAmt = 0D;
		Double lMAAmt = 0D;
		Double lFP1Amt = 0D;
		Double lFP2Amt = 0D;
		Double lDPPerAmt = 0D;
		Double lTIPerAmt = 0D;
		Double lFPDPAmt = 0D;
		Double lFPTIAmt = 0D;
		Double lBasicPensionAmt = 0D;
		Double lPensionCutAmt = 0D;
		Double lSpecialCutAmt = 0D;
		Double lITCutAmt = 0D;
		Double lOtherBenefits = 0D;
		Double lRecoveryAmt = 0D;
		Double lArrearAmt = 0D;
		Date lDateOfDeath = null;
		Date lPPODate = null;
		Date lCVPPaidDate = null;
		Date lFP1Date = null;
		Date lFP2Date = null;
		Date lEndDate = null;
		String endDays = null;
		Integer EDays = 0;
		Double lOMR = 0D;
		Double lDP = 0D;
		Double lTI = 0D;
		Double lPendingArrear = 0D;
		// List lItCutDtls = new ArrayList();
		// List lArrearDtls = new ArrayList();
		Integer lDays = 0;
		Integer date = 0;
		Integer date1 = 0;
		Integer lIntDate = 0;
		String lArrComputeFlag = null;
		MstPensionerFamilyDtls lLstFPmembers = null;
		String lStrFPFlag = "N";
		List fpMemberList = null;
		Double lTmpNetAmt = 0D;
		String fp1Date = null;
		String fp2Date = null;
		int lfp1Days = 0;
		int lfp2Days = 0;
		String endDate = null;
		Double lAllnBf56 = 0D;
		Double lAllnAf56 = 0D;
		Double lPerPension = 0D;
		Double lIR = 0D;
		Double lTIArr = 0D;
		Double lOtherArr = 0D;
		Double lCutATI = 0D;
		Double lRecovery = 0D;

		Double lITAmtForBudTab = 0d;

		String lStrBillType = null;
		Integer lDODyyyyMM = 0;

		String lStrPayTI = null;
		String lStrPaySTI = null;
		String lStrPayDP = null;
		String lStrPayMA = null;
		String lStrPayIR = null;

		// String lStrOMRType = null;
		String lStrBillCrtMonth = null;

		Date lPayStartDate = null;
		String lStrPayStartYM = null;
		Integer lPayStartDay = 0;

		Date lFromPayDate = null;
		Date lToPayDate = null;

		String currDate = null;

		boolean l6PayFlg = false;
		Double lADPAmt = 0d;
		Double lFPADPAmt = 0D;
		List lADPAmtLst = null;
		// List lstOMR = null;
		int l6PayCount = 0;
		List lRcvArrCutDtlLst = null;
		// List<Long> lLstOMRPaidLst = null;
		// char omrFlag = 'N';

		MonthlyPensionBillDAO lObjMnthlyBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		NewPensionBillDAO lObjPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());
		MonthlyCases lObjMonthlyCases = new MonthlyCases();
		List lInnerArrearDtlVoLst = null;

		try {
			setSessionInfo(inputMap);
			lStrBillType = inputMap.get("BillType").toString();
			lStrDate = inputMap.get("Date").toString();
			// System.out.println("Date "+lStrDate);
			lIntDate = Integer.parseInt(lStrDate);
			inputMap.put("forMonth", lIntDate);

			// compute no of days in this month
			lDays = getDaysOfMonth(lIntDate);

			/*
			 * Here we setting dates of staring and ending of payment for each
			 * month for saving those in TrnPensionBillDtls
			 */
			lFromPayDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/" + lStrDate.substring(4, 6) + "/" + lStrDate.substring(0, 4));
			lToPayDate = new SimpleDateFormat("dd/MM/yyyy").parse(lDays + "/" + lStrDate.substring(4, 6) + "/" + lStrDate.substring(0, 4));
			currDate = inputMap.get("BillCrtMonth").toString();

			if (lStrBillType.equalsIgnoreCase("Monthly")) {
				lStrScheme = inputMap.get("Scheme").toString();
			}
			lStrRcvryFlag = "Monthly";

			if (inputMap.containsKey("lObjValidPcode") && inputMap.get("lObjValidPcode") != null) {
				lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");
			}

			if (inputMap.containsKey("PayStartDate") && inputMap.get("PayStartDate") != null) {
				lPayStartDate = (Date) inputMap.get("PayStartDate");
				if (lPayStartDate != null) {
					lStrPayStartYM = new SimpleDateFormat("yyyyMM").format(lPayStartDate);
					if (lObjValidPcodeVO.getCommensionDate() != null && (new SimpleDateFormat("yyyyMM").format(lObjValidPcodeVO.getCommensionDate())).equals(lStrDate)) {
						lPayStartDay = Integer.valueOf(new SimpleDateFormat("dd").format(lPayStartDate));
						lPayStartDay -= 1;
						// staring date of month payment.
						lFromPayDate = (Date) lPayStartDate.clone();
					} else {
						lPayStartDay = Integer.valueOf(new SimpleDateFormat("dd").format(lPayStartDate));
						// staring date of month payment.
						lFromPayDate = (Date) lPayStartDate.clone();
						if (lStrPayStartYM.equals(lStrDate)) {
							lFromPayDate = getNextDate(lFromPayDate);
						}
					}
				}
			}

			lStrBillCrtMonth = inputMap.get("BillCrtMonth").toString();
			// get Next Month for setting into Arrears for No Family Pensioner.
			Integer compMonth = Integer.valueOf(lStrBillCrtMonth);
			compMonth = (compMonth % 100 == 12) ? (compMonth + 89) : (compMonth + 1);

			lArrComputeFlag = inputMap.get("ArrComputeFlag").toString();

			String lStrHeadcode = lObjValidPcodeVO.getHeadCode().toString();

			// This is to read scheme for the bills which are not monthly bills.
			if (lStrScheme == null && lObjValidPcodeVO != null) {
				lStrScheme = lObjValidPcodeVO.getSchemeType();
			}
			lPendingArrear = Double.parseDouble(inputMap.get("lPendingArrear").toString());

			lStrPnsnrCode = lObjValidPcodeVO.getPensionerCode();
			lStrPenRqstId = lObjValidPcodeVO.getPensionRequestId();

			inputMap.put("PenRqstId", lStrPenRqstId);
			inputMap.put("PenCode", lStrPnsnrCode);

			lEndDate = lObjValidPcodeVO.getEndDate();
			if (lEndDate != null) {
				endDate = new SimpleDateFormat("yyyyMM").format(lEndDate);
				endDays = new SimpleDateFormat("dd").format(lEndDate);
				EDays = Integer.valueOf(endDays);

				// till date of month payment is done.
				if (endDate.equals(lStrDate)) {
					lToPayDate = (Date) lEndDate.clone();
				}
			} else {
				endDate = "000000";
			}

			// check the last month for which monthly pension or pension
			// paid....if 0 implies no bill generated for this pensioner...not
			// even pension bill....
			// hence should not compute monthly bill for tht pensioner
			// lLastPaidMonth = lObjMnthlyBillDAO.getLastMonth(lStrPnsnrCode,
			// lStrScheme);

			if (lStrBillType.equalsIgnoreCase("Monthly")) {
				if (inputMap.get("AccNo") != null) {
					lStrAccNo = inputMap.get("AccNo").toString();
				}
			}

			// Getting Recovery, Arrear, Cuts Details for the Month of
			// Pensioner. ((PP/PT),IT,SPCut,CVPMon,Benefit,Recv,Arr,TIArr)
			/*
			 * lRcvArrCutDtlLst = new ArrayList();
			 * lRcvArrCutDtlLst.add(lStrPnsnrCode); // 0
			 * 
			 * if (inputMap.containsKey("lCutDtlMap") &&
			 * inputMap.get("lCutDtlMap") != null) { if (((Map)
			 * inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"PT"+"~" +
			 * lStrDate)) // pcut 1 { lRcvArrCutDtlLst.add(((Map)
			 * inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"PT"+"~" +
			 * lStrDate)); } else { lRcvArrCutDtlLst.add(0); } if (((Map)
			 * inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"IT"+"~" +
			 * lStrDate)) //icut 2 { lRcvArrCutDtlLst.add(((Map)
			 * inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"IT"+"~" +
			 * lStrDate)); } else { lRcvArrCutDtlLst.add(0); } if (((Map)
			 * inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode +"ST"+"~" +
			 * lStrDate)) //scut 3 { lRcvArrCutDtlLst.add(((Map)
			 * inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"ST"+"~" +
			 * lStrDate)); } else { lRcvArrCutDtlLst.add(0); } if (((Map)
			 * inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode
			 * +"TempBenefit"+"~" + lStrDate)) //tempBenefit 4 {
			 * lRcvArrCutDtlLst.add(((Map)
			 * inputMap.get("lCutDtlMap")).get(lStrPnsnrCode +"TempBenefit"+"~"
			 * + lStrDate)); } else { lRcvArrCutDtlLst.add(0); } } else {
			 * lRcvArrCutDtlLst.add(0); lRcvArrCutDtlLst.add(0);
			 * lRcvArrCutDtlLst.add(0); lRcvArrCutDtlLst.add(0); }
			 * 
			 * if (inputMap.containsKey("lRcvDtlMap") &&
			 * inputMap.get("lRcvDtlMap") != null) { if (((Map)
			 * inputMap.get("lRcvDtlMap")).containsKey(lStrPnsnrCode + "~" +
			 * lStrDate)) // recovery 5 { lRcvArrCutDtlLst.add(((Map)
			 * inputMap.get("lRcvDtlMap")).get(lStrPnsnrCode + "~" + lStrDate));
			 * } else { lRcvArrCutDtlLst.add(0); } } else {
			 * lRcvArrCutDtlLst.add(0); }
			 * 
			 * if (inputMap.containsKey("lArrDtlMap") &&
			 * inputMap.get("lArrDtlMap") != null) { if (((Map)
			 * inputMap.get("lArrDtlMap")).containsKey(lStrPnsnrCode +"~" +
			 * lStrDate)) // arrear 6 { lRcvArrCutDtlLst.add(((Map)
			 * inputMap.get("lArrDtlMap")).get(lStrPnsnrCode +"~" + lStrDate));
			 * } else { lRcvArrCutDtlLst.add(0); } if (((Map)
			 * inputMap.get("lArrDtlMap")).containsKey(lStrPnsnrCode +"TI"+"~" +
			 * lStrDate)) //TIArrear 7 { lRcvArrCutDtlLst.add(((Map)
			 * inputMap.get("lArrDtlMap")).get(lStrPnsnrCode +"TI"+"~" +
			 * lStrDate)); } else { lRcvArrCutDtlLst.add(0); } } else {
			 * lRcvArrCutDtlLst.add(0); lRcvArrCutDtlLst.add(0); }
			 */

			/*
			 * if(lStrDate.equals(currDate)) { if
			 * (inputMap.containsKey("lRcvArrCutDtlMap") &&
			 * inputMap.get("lRcvArrCutDtlMap") != null) {
			 * if(inputMap.containsKey(lStrPnsnrCode + "~" + lStrDate)) {
			 * lRcvArrCutDtlLst = (List) ((Map)
			 * inputMap.get("lRcvArrCutDtlMap")).get(lStrPnsnrCode + "~" +
			 * lStrDate); } } }
			 */

			// lRecoveryAmt = lObjMnthlyBillDAO.getRecoveryDtls(lStrPnsnrCode,
			// lStrRcvryFlag, lStrDate);
			// lItCutDtls = lObjMnthlyBillDAO.getItCutDtls(lStrPnsnrCode,
			// lStrDate);
			// lArrearDtls = lObjMnthlyBillDAO.getArrearDtls(lStrPnsnrCode,
			// lStrDate);
			// Getting the Value from TrnPensionRqstHdr... Start...
			lStrPenRqstId = lObjValidPcodeVO.getPensionRequestId();
			if (lObjValidPcodeVO.getPpoNo() != null) {
				lStrPPONo = lObjValidPcodeVO.getPpoNo();
			}

			if (lObjValidPcodeVO.getBasicPensionAmount() != null) {
				lBasicPensionAmt = Double.parseDouble(lObjValidPcodeVO.getBasicPensionAmount().toString());
			}
			if (lObjValidPcodeVO.getFp1Amount() != null) {
				lFP1Amt = Double.parseDouble(lObjValidPcodeVO.getFp1Amount().toString());
			}
			if (lObjValidPcodeVO.getFp2Amount() != null) {
				lFP2Amt = Double.parseDouble(lObjValidPcodeVO.getFp2Amount().toString());
			}
			if (lObjValidPcodeVO.getRedBf11156() != null) {
				lAllnBf56 = Double.parseDouble(lObjValidPcodeVO.getRedBf11156().toString());
			}
			if (lObjValidPcodeVO.getRedAf11156() != null) {
				lAllnAf56 = Double.parseDouble(lObjValidPcodeVO.getRedAf11156().toString());
			}
			if (lObjValidPcodeVO.getPersonalPension() != null) {
				lPerPension = Double.parseDouble(lObjValidPcodeVO.getPersonalPension().toString());
			}
			if (lObjValidPcodeVO.getCvpMonthlyAmount() != null) {
				lCVPMonthlyAmt = Double.valueOf(lObjValidPcodeVO.getCvpMonthlyAmount().toString());
			}

			// Logic Added For start cut of CVP Monthly from pension.
			Integer lCVPYYYMM = 0;
			Integer lCVPDtYYYYMM = 0;
			lCVPPaidDate = lObjValidPcodeVO.getCvpDate();
			if (lObjValidPcodeVO.getCvpEffectiveMonth() != null && lObjValidPcodeVO.getCvpEffectiveMonth() > 0) {
				Integer lCVPEffMonth = lObjValidPcodeVO.getCvpEffectiveMonth();
				if (lCVPEffMonth != null && lCVPEffMonth > 0 && lIntDate.intValue() < lCVPEffMonth.intValue()) {
					lCVPMonthlyAmt = 0D;
				}
			} else if (lStrBillType.equalsIgnoreCase("Monthly")) {
				if (lCVPPaidDate != null) {
					lCVPDtYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lCVPPaidDate));
					lCVPDtYYYYMM += ((Integer.parseInt((lCVPDtYYYYMM.toString().substring(4, 6)))) == 12) ? 89 : 1;
					if (lCVPDtYYYYMM > 0 && lIntDate < lCVPDtYYYYMM) {
						lCVPMonthlyAmt = 0D;
					}
				}
			} else if (lStrBillType.equalsIgnoreCase("PENSION")) {
				Integer lPPODtYYYYMM = 0;
				lPPODate = lObjValidPcodeVO.getPpoDate();
				Date lPPOSeenDate = (Date) inputMap.get("PPOSeenDate");
				if (lPPODate != null) {
					lPPODtYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lPPODate));
					if (lPPODtYYYYMM != null && lPPODtYYYYMM != 0 && (lPPODtYYYYMM % 100) >= 10 && (lPPODtYYYYMM % 100) <= 12) {
						lPPODtYYYYMM = lPPODtYYYYMM + 92;
					} else {
						lPPODtYYYYMM = lPPODtYYYYMM + 4;
					}
					lCVPYYYMM = lPPODtYYYYMM;
				}

				if (lPPOSeenDate != null) {
					lCVPDtYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lPPOSeenDate));
					if (lPPODtYYYYMM > 0 && lCVPDtYYYYMM > lPPODtYYYYMM) {
						lCVPDtYYYYMM = lPPODtYYYYMM;
					} else {
						lCVPDtYYYYMM += ((Integer.parseInt((lCVPDtYYYYMM.toString().substring(4, 6)))) == 12) ? 89 : 1;
					}
					lCVPYYYMM = lCVPDtYYYYMM;
				}

				if (lCVPYYYMM > 0 && lIntDate < lCVPYYYMM) {
					lCVPMonthlyAmt = 0D;
				}
			}

			// check if cvp restoration applied...if yes then cvp monthly should
			// not be cut
			Date lCVPAppliedDate = lObjValidPcodeVO.getAppliedDate();
			if (lCVPAppliedDate != null) {
				Integer lAppliedDtYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lCVPAppliedDate));
				if (lAppliedDtYYYYMM <= lIntDate) {
					lCVPMonthlyAmt = 0D;
				}
			}

			/*
			 * if(lObjTrnPensionRqstHdrVO.getIr() != null){ lIR =
			 * Double.parseDouble(lObjTrnPensionRqstHdrVO.getIr().toString()); }
			 */
			lFP1Date = lObjValidPcodeVO.getFp1Date();
			lFP2Date = lObjValidPcodeVO.getFp2Date();
			// lStrOMRType = lObjTrnPensionRqstHdrVO.getOmrType();
			// lStrOMRType = (lObjValidPcodeVO.getOmrType() != null) ?
			// lObjValidPcodeVO.getOmrType() : "-1";

			lStrPnsnerName = (String) inputMap.get("PensionerName");
			lDateOfDeath = (Date) inputMap.get("DateOfDeath");

			// if Pensioner having special headcode and he/she die then also
			// they getting basic pension.
			if (lDateOfDeath != null && (lStrHeadcode.equals("16") || lStrHeadcode.equals("17") || lStrHeadcode.equals("18") || lStrHeadcode.equals("19"))) {
				lDateOfDeath = null;
				fpMemberList = (List) inputMap.get("fpMemberList");
				if (fpMemberList != null && !fpMemberList.isEmpty()) {
					lStrPnsnerName = fpMemberList.get(0).toString();
				}
			}

			if (lDateOfDeath != null && lDateOfDeath.toString().length() > 0) {
				// compute lDateOfDeathYYYYMM
				lDateOfDeathYYYYMM = new SimpleDateFormat("yyyyMM").format(lDateOfDeath);
				lDayOfDeathDD = new SimpleDateFormat("dd").format(lDateOfDeath);
				lDODyyyyMM = Integer.parseInt(lDateOfDeathYYYYMM);

				lStrPenType = lObjValidPcodeVO.getPensionType();
				if (lStrPenType.equals(lStrVoluntary) || lStrPenType.equals(lStrFamilyLost) || lStrPenType.equals(lStrFamily)) {
					List arrFPlist = null;
					// If 2006 is Applied
					if (lObjValidPcodeVO.getIsFp1datechange() != null && "Y".equals(lObjValidPcodeVO.getIsFp1datechange())) {
						// compute correct FP1 n fp2 date ---- temp = DoD + 10
						// yrs
						arrFPlist = getDatePlus10yrs(lDateOfDeath, lFP1Date, lFP2Date);
					} else {
						// compute correct FP1 n fp2 date ---- temp = DoD + 7
						// yrs
						arrFPlist = getDatePlus7yrs(lDateOfDeath, lFP1Date, lFP2Date);
					}
					lFP1Date = (Date) arrFPlist.get(0);
					lFP2Date = (Date) arrFPlist.get(1);
				}

				// Logic add for remove discrepancy of FP1 or FP2 Date are blank
				if (lFP1Date != null && lFP2Date == null) {
					lFP2Date = getNextDate(lFP1Date);
				} else if (lFP1Date == null && lFP2Date != null) {
					lFP1Date = getPrevDate(lFP2Date);
				} else if (lFP1Date == null && lFP2Date == null) {
					List arrFPlist = null;
					// If 2006 is Applied
					if (lObjValidPcodeVO.getIsFp1datechange() != null && "Y".equals(lObjValidPcodeVO.getIsFp1datechange())) {
						// compute correct FP1 n fp2 date ---- temp = DoD + 10
						// yrs
						arrFPlist = getDatePlus10yrs(lDateOfDeath, lFP1Date, lFP2Date);
					} else {
						// compute correct FP1 n fp2 date ---- temp = DoD + 7
						// yrs
						arrFPlist = getDatePlus7yrs(lDateOfDeath, lFP1Date, lFP2Date);
					}
					lFP1Date = (Date) arrFPlist.get(0);
					lFP2Date = (Date) arrFPlist.get(1);
				}

				if (lFP1Date != null) {
					fp1Date = new SimpleDateFormat("yyyyMM").format(lFP1Date);
					lfp1Days = Integer.parseInt(new SimpleDateFormat("dd").format(lFP1Date));
					inputMap.put("FP1Date", lFP1Date);
				}
				if (lFP2Date != null) {
					fp2Date = new SimpleDateFormat("yyyyMM").format(lFP2Date);
					lfp2Days = Integer.parseInt(new SimpleDateFormat("dd").format(lFP2Date));
					inputMap.put("FP2Date", lFP2Date);
				}
			}

			if (lStrBillType.equalsIgnoreCase("PENSION")) {
				/*
				 * lRcvArrCutDtlLst =
				 * lObjMnthlyBillDAO.getRcvrArrCutDtlsForMonth(lStrPnsnrCode,
				 * lStrRcvryFlag,
				 * lStrDate,SessionHelper.getLocationCode(inputMap));
				 * 
				 * if (lRcvArrCutDtlLst != null && !lRcvArrCutDtlLst.isEmpty())
				 * { //Object[] lArrObjects = (Object[])
				 * lRcvArrCutDtlLst.get(0); lPensionCutAmt =
				 * Double.valueOf(lRcvArrCutDtlLst.get(2).toString()); lITCutAmt
				 * = Double.valueOf(lRcvArrCutDtlLst.get(3).toString());
				 * lSpecialCutAmt =
				 * Double.valueOf(lRcvArrCutDtlLst.get(4).toString());
				 * //lCVPMonthlyAmt =
				 * Double.valueOf(lRcvArrCutDtlLst.get(5).toString());
				 * lOtherBenefits =
				 * Double.valueOf(lRcvArrCutDtlLst.get(6).toString());
				 * lRecoveryAmt = ((lRcvArrCutDtlLst.get(7) != null) ?
				 * Double.valueOf(lRcvArrCutDtlLst.get(7).toString()) : 0D);
				 * lOtherArr = ((lRcvArrCutDtlLst.get(8) != null) ?
				 * Double.valueOf(lRcvArrCutDtlLst.get(8).toString()) : 0D);
				 * lTIArr = ((lRcvArrCutDtlLst.get(9) != null) ?
				 * Double.valueOf(lRcvArrCutDtlLst.get(9).toString()) : 0D); }
				 */
				if (inputMap.containsKey("fromAdmin") && inputMap.get("fromAdmin").toString().equals("Y")) {
					if (inputMap.containsKey("lMapPenCut") && inputMap.get("lMapPenCut") != null) {
						if (((Map) inputMap.get("lMapPenCut")).containsKey(lStrPnsnrCode + "PT" + "~" + lStrDate)) {
							lPensionCutAmt += (Double) ((Map) inputMap.get("lMapPenCut")).get(lStrPnsnrCode + "PT" + "~" + lStrDate);
						}
						if (((Map) inputMap.get("lMapPenCut")).containsKey(lStrPnsnrCode + "PP")) {
							lPensionCutAmt += (Double) ((Map) inputMap.get("lMapPenCut")).get(lStrPnsnrCode + "PP");
						}
					}

					lSpecialCutAmt = 0D;
					lOtherBenefits = 0D;
					lITCutAmt = 0D;
					lRecoveryAmt = 0D;

				} else {

					lPensionCutAmt = lObjPensionBillDAO.getTotalPensionCutForMonth(lStrPnsnrCode, lIntDate);
					lSpecialCutAmt = lObjPensionBillDAO.getTotalSpecialCutForMonth(lStrPnsnrCode, lIntDate);
					lOtherBenefits = lObjPensionBillDAO.getTotalOtherBenefitsForMonth(lStrPnsnrCode, lIntDate);
					lITCutAmt = lObjPensionBillDAO.getTotalITCutForMonth(lStrPnsnrCode, lIntDate);

					lRecoveryAmt = lObjMnthlyBillDAO.getRecoveryDtls(lStrPnsnrCode, lStrRcvryFlag, lStrDate);

					TrnPensionArrearDtlsDAO lObjArrearDtlsDAO = new TrnPensionArrearDtlsDAOImpl(TrnPensionArrearDtls.class, srvcLoc.getSessionFactory());
					lInnerArrearDtlVoLst = lObjArrearDtlsDAO.getArrearDtls(lStrPnsnrCode, lIntDate);
				}

				if (lInnerArrearDtlVoLst != null) {
					lOtherArr = 0D;
					lTIArr = 0D;
					for (int i = 0; i < lInnerArrearDtlVoLst.size(); i++) {
						Object lObjArr = lInnerArrearDtlVoLst.get(i);
						Object[] lObjTemp = (Object[]) lObjArr;

						if (lObjTemp[0] != null && lObjTemp[0].toString().equalsIgnoreCase("TI")) {
							lTIArr += Double.valueOf(lObjTemp[1].toString());
						} else {
							lOtherArr += Double.valueOf(lObjTemp[1].toString());
						}
					}
				}
			} else {
				if (inputMap.containsKey("lCutDtlMap") && inputMap.get("lCutDtlMap") != null) {
					if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode + "PT" + "~" + lStrDate)) {
						lPensionCutAmt += (Double) ((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode + "PT" + "~" + lStrDate);
					}
					if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode + "IT" + "~" + lStrDate)) {
						lITCutAmt = (Double) ((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode + "IT" + "~" + lStrDate);
					}
					if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode + "ST" + "~" + lStrDate)) {
						lSpecialCutAmt = (Double) ((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode + "ST" + "~" + lStrDate);
					}
					if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode + "TempBenefit" + "~" + lStrDate)) {
						lOtherBenefits += (Double) ((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode + "TempBenefit" + "~" + lStrDate);
					}

					if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode + "PP")) {
						lPensionCutAmt += (Double) ((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode + "PP");
					}
					if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode + "PermanentBenefit")) {
						lOtherBenefits += (Double) ((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode + "PermanentBenefit");
					}
					if (((Map) inputMap.get("lCutDtlMap")).containsKey(lStrPnsnrCode + "PM")) {
						lSpecialCutAmt += (Double) ((Map) inputMap.get("lCutDtlMap")).get(lStrPnsnrCode + "PM");
					}

				}
				if (inputMap.containsKey("lRcvDtlMap") && inputMap.get("lRcvDtlMap") != null) {
					if (((Map) inputMap.get("lRcvDtlMap")).containsKey(lStrPnsnrCode + "~" + lStrDate)) {
						lRecoveryAmt = (Double) ((Map) inputMap.get("lRcvDtlMap")).get(lStrPnsnrCode + "~" + lStrDate);
					}
				}
				if (inputMap.containsKey("lArrDtlMap") && inputMap.get("lArrDtlMap") != null) {
					if (((Map) inputMap.get("lArrDtlMap")).containsKey(lStrPnsnrCode + "~" + lStrDate)) {
						lOtherArr = (Double) ((Map) inputMap.get("lArrDtlMap")).get(lStrPnsnrCode + "~" + lStrDate);
					}
					if (((Map) inputMap.get("lArrDtlMap")).containsKey(lStrPnsnrCode + "TI" + "~" + lStrDate)) {
						lTIArr = (Double) ((Map) inputMap.get("lArrDtlMap")).get(lStrPnsnrCode + "TI" + "~" + lStrDate);
					}
				}
			}

			// set arrear as zero because due to any reason in last month,
			// pension payment i not fully paid but arrear is paid fully.
			// so here we check if this month is calculate last paid month
			// remaining portion or not.
			if (lStrBillType.equalsIgnoreCase("Monthly") && lPayStartDate != null) {
				if (inputMap.containsKey("BillDtlsPayForMonth") && ((Integer) inputMap.get("BillDtlsPayForMonth")) >= Integer.valueOf(lStrDate)) {
					lOtherArr = 0d;
					lTIArr = 0d;
				}
			}
			lCutATI = lTIArr;
			// Putting All pensioner total IT Cut Amount into Map. will be use
			// in budget details Receipt calculation.
			if (lITCutAmt != null && lITCutAmt > 0) {
				if (inputMap.containsKey("ITAmtForBudTab")) {
					lITAmtForBudTab = (Double) inputMap.get("ITAmtForBudTab");
					lITAmtForBudTab += lITCutAmt;
					inputMap.put("ITAmtForBudTab", lITAmtForBudTab);
				} else {
					lITAmtForBudTab += lITCutAmt;
					inputMap.put("ITAmtForBudTab", lITAmtForBudTab);
				}
			}

			// Flags Set For maintain a List for transaction Payment.
			inputMap.put("CurntPayTI", lStrPayTI);
			inputMap.put("CurntPaySTI", lStrPaySTI);
			inputMap.put("CurntPayDP", lStrPayDP);
			inputMap.put("CurntPayMA", lStrPayMA);
			inputMap.put("CurntPayIR", lStrPayIR);

			inputMap.put("PensionCutAmt", lPensionCutAmt);

			// lDPPerAmt = (lBasicPensionAmt - lPensionCutAmt) * lDPPer / 100;
			// lTIPerAmt = ((Math.round(lBasicPensionAmt) - lPensionCutAmt) +
			// lDPPerAmt) * lTIPer / 100;

			// All values for normal pension are available now compute for other
			// cases

			// Get basic applicable for this month.
			getBasicsForMonth(inputMap);

			// 6th Pay commission Calculation. .......... Start Logic Added for
			// calculate Six Pay ADP Amount.
			if (lIntDate > 200512 && !lStrHeadcode.equals("16") && !lStrHeadcode.equals("17") && !lStrHeadcode.equals("18") && !lStrHeadcode.equals("19")) {
				String lStrROP_1986 = (String) inputMap.get("ROP_1986");
				String lStrROP_1996 = (String) inputMap.get("ROP_1996");
				String lStrROP_2006 = (String) inputMap.get("ROP_2006");

				// If 1986 and 1996 both are not Checked and 2006 is checked
				if (lStrROP_1986 != null && (lStrROP_1986.equalsIgnoreCase("Y") || lStrROP_1986.equalsIgnoreCase("P")) && lStrROP_1996 != null
						&& (lStrROP_1996.equalsIgnoreCase("Y") || lStrROP_1996.equalsIgnoreCase("P")) && lStrROP_2006 != null
						&& (lStrROP_2006.equalsIgnoreCase("Y") || lStrROP_2006.equalsIgnoreCase("P"))) {
					lADPAmtLst = getAditionalPensionAmt(inputMap);
					l6PayCount = 0;
					l6PayFlg = true;

					Double TotalMntAdPAmt = 0D;
					if (lADPAmtLst != null && !lADPAmtLst.isEmpty()) {
						for (int i = 0; i < lADPAmtLst.size(); i++) {
							TotalMntAdPAmt = Double.valueOf(lADPAmtLst.get(i).toString());
						}
					}
					inputMap.put("TotalMntAdPAmt", TotalMntAdPAmt);
				}
			}// 6th Pay commission Calculation. .......... End

			if (lDateOfDeath != null && lDateOfDeath.toString().length() > 0 && lDODyyyyMM.intValue() <= lIntDate.intValue()) {
				// pensioner has expired

				lLstFPmembers = (MstPensionerFamilyDtls) inputMap.get("FPmembersVo");
				fpMemberList = (List) inputMap.get("fpMemberList");
				lEndDate = (Date) inputMap.get("EndDate");

				if (lLstFPmembers != null && (lLstFPmembers.getDateOfDeath() == null || lDateOfDeath.before(lLstFPmembers.getDateOfDeath()) || lDateOfDeath.equals(lLstFPmembers.getDateOfDeath()))) {
					lStrFPFlag = "Y";
				}

				if (lEndDate != null) {
					endDate = new SimpleDateFormat("yyyyMM").format(lEndDate);
					endDays = new SimpleDateFormat("dd").format(lEndDate);
					EDays = Integer.valueOf(endDays);

					// till date of month payment is done.
					if (endDate.equals(lStrDate)) {
						lToPayDate = (Date) lEndDate.clone();
					}
				} else {
					endDate = "000000";
				}

				// OMR BOLCK COMMENTED BECAUSE IT IS NOT BE GIVEN THROUGH
				// MONTHLY FROM NOW

				// check whether the date of death is the last day of the paid
				// month or not.
				// if it is then we need to generate the OMR bill for the same
				// in the current .
				/*
				 * int preMnthDeath = 0; Calendar cal =
				 * GregorianCalendar.getInstance(); Calendar cal1 =
				 * GregorianCalendar.getInstance();
				 * cal.set(Integer.valueOf(lStrDate
				 * .substring(0,4)),Integer.valueOf
				 * (lStrDate.substring(4,6)),01); int lMnth =
				 * cal.get(cal.MONTH); if(lMnth == 1) { preMnthDeath =
				 * Integer.valueOf(lStrDate) - 89; } else { preMnthDeath =
				 * Integer.valueOf(lStrDate) - 1; }
				 * 
				 * int lstDayOfMnth =
				 * lObjMonthlyCases.getDaysOfMonth(preMnthDeath);
				 * cal.setTime(lDateOfDeath);
				 * 
				 * String preDate = String.valueOf(preMnthDeath);
				 * cal1.set(Integer
				 * .valueOf(preDate.substring(0,4)),Integer.valueOf
				 * (preDate.substring(4,6))-1,lstDayOfMnth);
				 * 
				 * if(cal1.get(cal1.MONTH) == cal.get(cal.MONTH) &&
				 * cal1.get(cal1.DATE) == cal.get(cal.DATE) &&
				 * cal1.get(cal1.YEAR) == cal.get(cal.YEAR)) { omrFlag = 'Y'; }
				 * 
				 * //OMR BLOCK if (lDateOfDeathYYYYMM.equals(lStrDate) ||
				 * omrFlag=='Y') { if
				 * (lObjValidPcodeVO.getCommensionDate()!=null &&
				 * lDateOfDeath!=null &&
				 * lObjValidPcodeVO.getCommensionDate().before(lDateOfDeath) &&
				 * !"P".equals(lStrOMRType) && ("M".equals(lStrOMRType) ||
				 * "-1".equals(lStrOMRType))) { if (lStrBillType != null &&
				 * lStrBillType.equalsIgnoreCase("Monthly") ) {
				 * inputMap.put("OldBasicAmt", lBasicPensionAmt);
				 * inputMap.put("BasicAmtType", "Basic");
				 * inputMap.put("IsAlive", "N"); lRatePensionBillVO =
				 * getMasterRatesForMonth(inputMap);
				 * 
				 * Double lTBasicAmt =
				 * Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount
				 * ().toString()); lDPPer =
				 * Double.parseDouble(lRatePensionBillVO
				 * .getDpPercent().toString()); lTIPer =
				 * Double.parseDouble(lRatePensionBillVO
				 * .getTiPercent().toString()); Double ltIRAmt =
				 * Double.parseDouble(lRatePensionBillVO.getIr().toString());
				 * 
				 * List<String> llstRec = new ArrayList<String>(); List<String>
				 * lOmrPPo = new ArrayList<String>(); lDP = ((lBasicPensionAmt
				 * lDPPer) / 100);
				 * 
				 * if (lTIPer > 0) lTI = (((lBasicPensionAmt + lDP) lTIPer) /
				 * 100); else lTI =
				 * Double.parseDouble(lRatePensionBillVO.getTiPercentAmount
				 * ().toString());
				 * 
				 * lOMR = ((lTBasicAmt + lDP + lTI) - lCVPMonthlyAmt) + ltIRAmt;
				 * 
				 * if(lOMR > 0 ) { Map lRecoMap =
				 * lObjMnthlyBillDAO.getRecoveryDtls(lStrPnsnrCode, "OMR",
				 * lStrDate,inputMap); for(int i=0;i<lRecoMap.size();i++) {
				 * if(lRecoMap.containsKey("RecoveryAmnt"+i)) { lRecovery =
				 * (Double)lRecoMap.get("RecoveryAmnt"+i);
				 * lRecoMap.remove("RecoveryAmnt"+i); }
				 * if(lRecoMap.containsKey("RecoveryId"+i)) { lRecoveryId =
				 * (Long)lRecoMap.get("RecoveryId"+i);
				 * lRecoMap.remove("RecoveryId"+i); }
				 * 
				 * if(lRecovery > 0 ) { lRecoveryAmt += lRecovery;
				 * inputMap.put("RecIdOMR", lRecoveryId+"~"+lStrPnsnrCode);
				 * llstRec.add(lRecoveryId+"~"+lStrPnsnrCode);
				 * 
				 * if(lStrDate.equals(currDate) || omrFlag=='Y') {
				 * lOmrPPo.add(lStrPnsnrCode); } }
				 * 
				 * lRecovery = 0D;
				 * 
				 * } inputMap.put("RecIdOMR", llstRec); inputMap.put("lOmrPPo",
				 * lOmrPPo); / if(lRecoMap.containsKey("RecoveryAmnt"))
				 * lRecovery = (Double) lRecoMap.get("RecoveryAmnt");
				 * if(lRecoMap.containsKey("RecoveryId")) lRecoveryId = (Long)
				 * lRecoMap.get("RecoveryId");
				 */

				/*
				 * if(lRecovery > 0 ) { lRecoveryAmt += lRecovery;
				 * inputMap.put("RecIdOMR", llstRec); } }
				 * 
				 * inputMap.put("PenRqstId", lStrPenRqstId);
				 * inputMap.put("PenCode", lStrPnsnrCode);
				 * inputMap.put("ArrearType", lStrArrOMR);
				 * inputMap.put("EffFrom", lStrDate); inputMap.put("DiffAmt",
				 * Math.round(lOMR));
				 * 
				 * if (fpMemberList != null && fpMemberList.size() > 0 &&
				 * "Y".equalsIgnoreCase(lStrFPFlag)) { inputMap.put("PayFrom",
				 * lStrBillCrtMonth); } else { lOMR = 0D; }
				 * 
				 * lObjArrearVO = new TrnPensionArrearDtls(); lObjArrearVO =
				 * insertArrearDtls(inputMap);
				 * lLstInsertArrears.add(lObjArrearVO); } else if (lStrBillType
				 * != null && lStrBillType.equalsIgnoreCase("PENSION") &&
				 * lStrPenType != null && !(lStrPenType.equals(lStrFamily) ||
				 * lStrPenType.equals(lStrFamilyLost))) { // OMR calculation //
				 * 1 Call For Master Rates
				 * 
				 * inputMap.put("OldBasicAmt", lBasicPensionAmt);
				 * inputMap.put("BasicAmtType", "Basic");
				 * inputMap.put("IsAlive", "N"); lRatePensionBillVO =
				 * getMasterRatesForMonth(inputMap);
				 * 
				 * Double lTBasicAmt =
				 * Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount
				 * ().toString()); lDPPer =
				 * Double.parseDouble(lRatePensionBillVO
				 * .getDpPercent().toString()); lTIPer =
				 * Double.parseDouble(lRatePensionBillVO
				 * .getTiPercent().toString()); Double ltIRAmt =
				 * Double.parseDouble(lRatePensionBillVO.getIr().toString());
				 * 
				 * lDP = ((lBasicPensionAmt lDPPer) / 100);
				 * 
				 * if (lTIPer > 0) lTI = (((lBasicPensionAmt + lDP) lTIPer) /
				 * 100); else lTI =
				 * Double.parseDouble(lRatePensionBillVO.getTiPercentAmount
				 * ().toString());
				 * 
				 * lOMR = ((lTBasicAmt + lDP + lTI) - lCVPMonthlyAmt) + ltIRAmt;
				 * if(lOMR > 0 ) { Map lRecoMap =
				 * lObjMnthlyBillDAO.getRecoveryDtls(lStrPnsnrCode,
				 * lStrRcvryFlag, lStrDate,inputMap);
				 * if(lRecoMap.containsKey("RecoveryAmnt0")) lRecovery =
				 * (Double) lRecoMap.get("RecoveryAmnt0");
				 * 
				 * if(lRecovery > 0 ) { lRecoveryAmt += lRecovery; } }
				 * 
				 * inputMap.put("PenRqstId", lStrPenRqstId);
				 * inputMap.put("PenCode", lStrPnsnrCode);
				 * inputMap.put("ArrearType", lStrArrOMR);
				 * inputMap.put("EffFrom", lStrDate); inputMap.put("DiffAmt",
				 * Math.round(lOMR));
				 * 
				 * if (fpMemberList != null && fpMemberList.size() > 0 &&
				 * "Y".equalsIgnoreCase(lStrFPFlag)) { inputMap.put("PayFrom",
				 * lStrBillCrtMonth); } else { //inputMap.put("PayFrom",
				 * lStrPayNxtMonth); lOMR = 0D; }
				 * 
				 * lObjArrearVO = new TrnPensionArrearDtls(); lObjArrearVO =
				 * insertArrearDtls(inputMap);
				 * lLstInsertArrears.add(lObjArrearVO);
				 * //gLogger.info("OMR"+OMR); } } omrFlag = 'N'; } //OMR BLOCK
				 * END
				 */

				// check if date of death in month for which bill generated
				if (lDateOfDeathYYYYMM.equals(lStrDate)) {
					// pensioner died in current month
					// compute complex calculation for this month
					date = Integer.parseInt(lDayOfDeathDD);

					if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && date < lPayStartDay) {
						date = lPayStartDay;
					}

					if (lStrDate.equals(endDate)) {
						// end date is in this month itself check if end date
						// before date of death
						if (lEndDate.before(lDateOfDeath) || lEndDate.equals(lDateOfDeath)) {
							inputMap.put("OldBasicAmt", lBasicPensionAmt);
							inputMap.put("BasicAmtType", "Basic"); // For
							// Identify
							// Pension
							// Type in
							// getNewROPBasicAmt
							// is (BASIC
							// / FP1 /
							// FP2)
							inputMap.put("IsAlive", "Y");
							lRatePensionBillVO = getMasterRatesForMonth(inputMap);

							lBasicPensionAmt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
							lDPPer = Double.parseDouble(lRatePensionBillVO.getDpPercent().toString());
							lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
							lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
							lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());
							lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
							lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

							if (lTIPer > 0) {
								lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lDPPerAmt) * ((lTIPer) / 100));
							}

							// Pension start Payment date is in current month.
							// Payment start date is before end date.
							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay <= EDays) {
								// Payment Start date in current month....so
								// give him pension only start date to end date.
								lBasicPensionAmt = lBasicPensionAmt * (EDays - lPayStartDay) / lDays;
								lPensionCutAmt = lPensionCutAmt * (EDays - lPayStartDay) / lDays;
								lCVPMonthlyAmt = lCVPMonthlyAmt * (EDays - lPayStartDay) / lDays;
								lMAAmt = lMAAmt * (EDays - lPayStartDay) / lDays;
								lITCutAmt = lITCutAmt * (EDays - lPayStartDay) / lDays;
								lSpecialCutAmt = lSpecialCutAmt * (EDays - lPayStartDay) / lDays;
								lOtherBenefits = lOtherBenefits * (EDays - lPayStartDay) / lDays;
								lPerPension = lPerPension * (EDays - lPayStartDay) / lDays;
								lIR = lIR * (EDays - lPayStartDay) / lDays;
								lDPPerAmt = lDPPerAmt * (EDays - lPayStartDay) / lDays;
								lTIPerAmt = lTIPerAmt * (EDays - lPayStartDay) / lDays;
							} else {
								// if end date less than date of death hence pay
								// till end date....computation till end date
								lBasicPensionAmt = lBasicPensionAmt * EDays / lDays;
								lPensionCutAmt = lPensionCutAmt * EDays / lDays;
								lCVPMonthlyAmt = lCVPMonthlyAmt * EDays / lDays;
								lMAAmt = lMAAmt * EDays / lDays;
								lITCutAmt = lITCutAmt * EDays / lDays;
								lSpecialCutAmt = lSpecialCutAmt * EDays / lDays;
								lOtherBenefits = lOtherBenefits * EDays / lDays;
								lPerPension = lPerPension * EDays / lDays;
								lIR = lIR * EDays / lDays;
								lDPPerAmt = lDPPerAmt * EDays / lDays;
								lTIPerAmt = lTIPerAmt * EDays / lDays;
							}

							if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
								lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
								lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lADPAmt) * ((lTIPer)) / 100);
							}

							if ("Y".equals(lArrComputeFlag)) {
								lTmpNetAmt = 0D;
								lTmpNetAmt = Math.round(lBasicPensionAmt) - Math.round(lPensionCutAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) + Math.round(lPerPension)
										+ Math.round(lIR) - Math.round(lCVPMonthlyAmt) + Math.round(lMAAmt) - Math.round(lITCutAmt) - Math.round(lSpecialCutAmt) + Math.round(lOtherBenefits)
										- lRecoveryAmt + lArrearAmt - lCutATI;

								inputMap.put("PenRqstId", lStrPenRqstId);
								inputMap.put("PenCode", lStrPnsnrCode);
								inputMap.put("ArrearType", lStrArrPension);
								inputMap.put("EffFrom", lStrDate);
								inputMap.put("PayFrom", lStrBillCrtMonth);
								inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

								lObjArrearVO = new TrnPensionArrearDtls();
								lObjArrearVO = insertArrearDtls(inputMap);
								lLstInsertArrears.add(lObjArrearVO);
							}
						} else {
							inputMap.put("OldBasicAmt", lBasicPensionAmt);
							inputMap.put("BasicAmtType", "Basic"); // For
							// Identify
							// Pension
							// Type in
							// getNewROPBasicAmt
							// is (BASIC
							// / FP1 /
							// FP2)
							inputMap.put("IsAlive", "Y");
							lRatePensionBillVO = getMasterRatesForMonth(inputMap);

							lBasicPensionAmt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
							lDPPer = Double.parseDouble(lRatePensionBillVO.getDpPercent().toString());
							lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
							lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
							lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());
							lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
							lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

							if (lTIPer > 0) {
								lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lDPPerAmt) * ((lTIPer) / 100));
							}

							// pensioner date of death before end date...hence
							// pay him till dod according to basic then till end
							// date according to fp1 and/or fp2 whatever
							// applicable

							// Pension start Payment date is in current month.
							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay <= date) {
								// compute till Pay start to DoD according to
								// basic and after DoD to end date according to
								// fp1 or/and fp2 applicable
								lBasicPensionAmt = lBasicPensionAmt * (date - lPayStartDay) / lDays;
								lPensionCutAmt = lPensionCutAmt * (date - lPayStartDay) / lDays;
								lCVPMonthlyAmt = lCVPMonthlyAmt * (date - lPayStartDay) / lDays;
								lITCutAmt = lITCutAmt * (date - lPayStartDay) / lDays;
								lDPPerAmt = lDPPerAmt * (date - lPayStartDay) / lDays;
								lTIPerAmt = lTIPerAmt * (date - lPayStartDay) / lDays;

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lBasicPensionAmt) - Math.round(lPensionCutAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) - Math.round(lCVPMonthlyAmt)
											+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (date - lPayStartDay)) / lDays) - Math.round(lITCutAmt) - lRecoveryAmt + lArrearAmt
											- lCutATI;

									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrPension);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							} else {
								// compute till DoD according to basic and after
								// DoD to end date according to fp1 or/and fp2
								// applicable
								lBasicPensionAmt = lBasicPensionAmt * date / lDays;
								lPensionCutAmt = lPensionCutAmt * date / lDays;
								lCVPMonthlyAmt = lCVPMonthlyAmt * date / lDays;
								lITCutAmt = lITCutAmt * date / lDays;
								lDPPerAmt = lDPPerAmt * date / lDays;
								lTIPerAmt = lTIPerAmt * date / lDays;

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lBasicPensionAmt) - Math.round(lPensionCutAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) - Math.round(lCVPMonthlyAmt)
											+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (date - lPayStartDay)) / lDays) - Math.round(lITCutAmt) - lRecoveryAmt + lArrearAmt
											- lCutATI;

									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrPension);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							}
							// now need to chk if fp1 date and fp2 date in
							// current month
							// if yes....chk accordingly and compute chk if fp1
							// date after dod...thyen some payment will be
							// according to fp1
							// fp1 before end date...pay till fp1 date acc to
							// fp1 and from fp1 till end date acc fp2 and
							// remaining in arrears
							// else fp1 is after end date...hence pay till end
							// date acc to fp1 then remaining in arrears from
							// end date to fp1 date acc to fp1 and from fp1 to
							// remaining acc fp2
							// else fp2 applicable after dod...pay till end date
							// and remaining in arrears if no...pay according to
							// fp1 or fp2 till end date and remaining in arrears

							if (fp1Date.equals(lStrDate) && fp2Date.equals(lStrDate)) {
								if (lfp1Days != date && lfp1Days > date) {
									// fp1 date after dod..hence some payment
									// will be according to fp1...remaining
									// according to fp2
									// considering end date

									if (lEndDate.before(lFP1Date) || lEndDate.equals(lFP1Date)) {
										// pay till end date according to fp1
										inputMap.put("OldBasicAmt", lFP1Amt);
										inputMap.put("BasicAmtType", "FP1");
										inputMap.put("IsAlive", "N");
										lRatePensionBillVO = getMasterRatesForMonth(inputMap);

										lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
										lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
										lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
										lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

										if (lTIPer > 0) {
											lFPTIAmt = ((lFP1Amt + lFPDPAmt) * ((lTIPer) / 100));
										}

										// Pension start Payment date is in
										// current month.
										// and start date before end date and
										// after death date.
										if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay < EDays && lPayStartDay >= date) {
											lFP1Amt = (lFP1Amt / lDays * (EDays - lPayStartDay));
											lFPDPAmt = (lFPDPAmt / lDays * (EDays - lPayStartDay));
											lFPTIAmt = (lFPTIAmt / lDays * (EDays - lPayStartDay));

											if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
												lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
												lADPAmt = lADPAmt + lFPADPAmt;
												lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * ((lTIPer) / 100));
											}

											if ("Y".equals(lArrComputeFlag)) {
												lTmpNetAmt = 0D;
												lTmpNetAmt = Math.round(lFP1Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
														// +
														// dp
														// +
														// ti
														+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - lPayStartDay)) / lDays);

												inputMap.put("PenRqstId", lStrPenRqstId);
												inputMap.put("PenCode", lStrPnsnrCode);
												inputMap.put("ArrearType", lStrArrFP1);
												inputMap.put("EffFrom", lStrDate);
												inputMap.put("PayFrom", lStrBillCrtMonth);
												inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

												lObjArrearVO = new TrnPensionArrearDtls();
												lObjArrearVO = insertArrearDtls(inputMap);
												lLstInsertArrears.add(lObjArrearVO);
											}
										} else {
											lFP1Amt = (lFP1Amt / lDays * (EDays - date));
											lFPDPAmt = (lFPDPAmt / lDays * (EDays - date));
											lFPTIAmt = (lFPTIAmt / lDays * (EDays - date));

											if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
												lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
												lADPAmt = lADPAmt + lFPADPAmt;
												lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * ((lTIPer) / 100));
											}

											if ("Y".equals(lArrComputeFlag)) {
												lTmpNetAmt = 0D;
												lTmpNetAmt = Math.round(lFP1Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
														// +
														// dp
														// +
														// ti
														+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - date)) / lDays);

												inputMap.put("PenRqstId", lStrPenRqstId);
												inputMap.put("PenCode", lStrPnsnrCode);
												inputMap.put("ArrearType", lStrArrFP1);
												inputMap.put("EffFrom", lStrDate);
												inputMap.put("PayFrom", lStrBillCrtMonth);
												inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

												lObjArrearVO = new TrnPensionArrearDtls();
												lObjArrearVO = insertArrearDtls(inputMap);
												lLstInsertArrears.add(lObjArrearVO);
											}
										}

										lBasicPensionAmt = lBasicPensionAmt + lFP1Amt;
										lDPPerAmt = lDPPerAmt + lFPDPAmt;
										lTIPerAmt = lTIPerAmt + lFPTIAmt;

									} else {
										// fp1 date before end date
										// hence pay till fp1 date acc tp fp1
										inputMap.put("OldBasicAmt", lFP1Amt);
										inputMap.put("BasicAmtType", "FP1");
										inputMap.put("IsAlive", "N");
										lRatePensionBillVO = getMasterRatesForMonth(inputMap);

										lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
										lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
										lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
										lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

										if (lTIPer > 0) {
											lFPTIAmt = ((lFP1Amt + lFPDPAmt) * ((lTIPer) / 100));
										}

										// Pension start Payment date is in
										// current month.
										// and start date before fp1 date and
										// after death date.
										if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay < lfp1Days && lPayStartDay >= date) {
											lFP1Amt = (lFP1Amt * (lfp1Days - lPayStartDay) / lDays);
											lFPDPAmt = (lFPDPAmt * (lfp1Days - lPayStartDay) / lDays);
											lFPTIAmt = (lFPTIAmt * (lfp1Days - lPayStartDay) / lDays);

											if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
												lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
												lADPAmt = lADPAmt + lFPADPAmt;
												lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * ((lTIPer) / 100));
											}

											if ("Y".equals(lArrComputeFlag)) {
												lTmpNetAmt = 0D;
												lTmpNetAmt = Math.round(lFP1Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
														// +
														// dp
														// amt
														// +
														// Ti
														// Amt
														+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lfp1Days - lPayStartDay)) / lDays);
												inputMap.put("PenRqstId", lStrPenRqstId);
												inputMap.put("PenCode", lStrPnsnrCode);
												inputMap.put("ArrearType", lStrArrFP1);
												inputMap.put("EffFrom", lStrDate);
												inputMap.put("PayFrom", lStrBillCrtMonth);
												inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

												lObjArrearVO = new TrnPensionArrearDtls();
												lObjArrearVO = insertArrearDtls(inputMap);
												lLstInsertArrears.add(lObjArrearVO);
											}
										} else {
											lFP1Amt = (lFP1Amt * (lfp1Days - date) / lDays);
											lFPDPAmt = (lFPDPAmt * (lfp1Days - date) / lDays);
											lFPTIAmt = (lFPTIAmt * (lfp1Days - date) / lDays);

											if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
												lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
												lADPAmt = lADPAmt + lFPADPAmt;
												lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * ((lTIPer) / 100));
											}

											if ("Y".equals(lArrComputeFlag)) {
												lTmpNetAmt = 0D;
												lTmpNetAmt = Math.round(lFP1Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
														// +
														// dp
														// amt
														// +
														// Ti
														// Amt
														+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lfp1Days - date)) / lDays);
												inputMap.put("PenRqstId", lStrPenRqstId);
												inputMap.put("PenCode", lStrPnsnrCode);
												inputMap.put("ArrearType", lStrArrFP1);
												inputMap.put("EffFrom", lStrDate);
												inputMap.put("PayFrom", lStrBillCrtMonth);
												inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

												lObjArrearVO = new TrnPensionArrearDtls();
												lObjArrearVO = insertArrearDtls(inputMap);
												lLstInsertArrears.add(lObjArrearVO);
											}
										}

										lBasicPensionAmt = lBasicPensionAmt + lFP1Amt;
										lDPPerAmt = lDPPerAmt + lFPDPAmt;
										lTIPerAmt = lTIPerAmt + lFPTIAmt;

										// pay from fp1 date till end date acc
										// to fp2
										inputMap.put("OldBasicAmt", lFP2Amt);
										inputMap.put("BasicAmtType", "FP2");
										inputMap.put("IsAlive", "N");
										lRatePensionBillVO = getMasterRatesForMonth(inputMap);

										lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
										lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
										lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
										lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

										if (lTIPer > 0) {
											lFPTIAmt = ((lFP2Amt + lFPDPAmt) * ((lTIPer) / 100));
										}

										// Pension start Payment date is in
										// current month.
										// and start date before end date and
										// after fp1 date.
										if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay < EDays && lPayStartDay >= lfp1Days) {
											lFP2Amt = (lFP2Amt) * (EDays - lPayStartDay) / lDays;
											lFPDPAmt = (lFPDPAmt * (EDays - lPayStartDay) / lDays);
											lFPTIAmt = (lFPTIAmt * (EDays - lPayStartDay) / lDays);

											if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
												lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
												lADPAmt = lADPAmt + lFPADPAmt;
												lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
											}

											if ("Y".equals(lArrComputeFlag)) {
												lTmpNetAmt = 0D;
												lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
														// +
														// DP
														// +
														// lTIPerAmt
														+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - lPayStartDay)) / lDays);
												inputMap.put("PenRqstId", lStrPenRqstId);
												inputMap.put("PenCode", lStrPnsnrCode);
												inputMap.put("ArrearType", lStrArrFP2);
												inputMap.put("EffFrom", lStrDate);
												inputMap.put("PayFrom", lStrBillCrtMonth);
												inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

												lObjArrearVO = new TrnPensionArrearDtls();
												lObjArrearVO = insertArrearDtls(inputMap);
												lLstInsertArrears.add(lObjArrearVO);
											}
										} else {
											lFP2Amt = (lFP2Amt) * (EDays - lfp1Days) / lDays;
											lFPDPAmt = (lFPDPAmt * (EDays - lfp1Days) / lDays);
											lFPTIAmt = (lFPTIAmt * (EDays - lfp1Days) / lDays);

											if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
												lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
												lADPAmt = lADPAmt + lFPADPAmt;
												lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
											}

											if ("Y".equals(lArrComputeFlag)) {
												lTmpNetAmt = 0D;
												lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
														// +
														// DP
														// +
														// lTIPerAmt
														+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - lfp1Days)) / lDays);
												inputMap.put("PenRqstId", lStrPenRqstId);
												inputMap.put("PenCode", lStrPnsnrCode);
												inputMap.put("ArrearType", lStrArrFP2);
												inputMap.put("EffFrom", lStrDate);
												inputMap.put("PayFrom", lStrBillCrtMonth);
												inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

												lObjArrearVO = new TrnPensionArrearDtls();
												lObjArrearVO = insertArrearDtls(inputMap);
												lLstInsertArrears.add(lObjArrearVO);
											}
										}

										lBasicPensionAmt = lBasicPensionAmt + lFP2Amt;
										lDPPerAmt = lDPPerAmt + lFPDPAmt;
										lTIPerAmt = lTIPerAmt + lFPTIAmt;
									}
								} else {
									// fp1 date before dod...hence pay acc to
									// fp2 till end date and remaining acc fp2
									// in arrears

									inputMap.put("OldBasicAmt", lFP2Amt);
									inputMap.put("BasicAmtType", "FP2");
									inputMap.put("IsAlive", "N");
									lRatePensionBillVO = getMasterRatesForMonth(inputMap);

									lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
									lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
									lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
									lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

									if (lTIPer > 0) {
										lFPTIAmt = ((lFP2Amt + lFPDPAmt) * ((lTIPer) / 100));
									}

									// Pension start Payment date is in current
									// month.
									// and start date before end date and after
									// death date.
									if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay < EDays && lPayStartDay >= date) {
										lFP2Amt = (lFP2Amt * (EDays - lPayStartDay) / lDays);
										lFPDPAmt = (lFPDPAmt * (EDays - lPayStartDay) / lDays);
										lFPTIAmt = (lFPTIAmt * (EDays - lPayStartDay) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - lPayStartDay)) / lDays);
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									} else {
										lFP2Amt = (lFP2Amt) * (EDays - date) / lDays;
										lFPDPAmt = (lFPDPAmt * (EDays - date) / lDays);
										lFPTIAmt = (lFPTIAmt * (EDays - date) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - date)) / lDays);
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									}

									lBasicPensionAmt = lBasicPensionAmt + lFP2Amt;
									lDPPerAmt = lDPPerAmt + lFPDPAmt;
									lTIPerAmt = lTIPerAmt + lFPTIAmt;
								}
							} else {
								// fp1 and fp2 date not in current month
								// pay according to either fp1 or fp2 for

								if (lEndDate.before(lFP1Date) || lEndDate.equals(lFP1Date)) {
									// pay acc to fp1
									inputMap.put("OldBasicAmt", lFP1Amt);
									inputMap.put("BasicAmtType", "FP1");
									inputMap.put("IsAlive", "N");
									lRatePensionBillVO = getMasterRatesForMonth(inputMap);

									lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
									lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
									lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
									lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

									if (lTIPer > 0) {
										lFPTIAmt = ((lFP1Amt + lFPDPAmt) * ((lTIPer) / 100));
									}

									// Pension start Payment date is in current
									// month.
									// and start date before end date and after
									// death date.
									if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay < EDays && lPayStartDay >= date) {
										lFP1Amt = (lFP1Amt * (EDays - lPayStartDay) / lDays);
										lFPDPAmt = (lFPDPAmt * (EDays - lPayStartDay) / lDays);
										lFPTIAmt = (lFPTIAmt * (EDays - lPayStartDay) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP1Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - lPayStartDay)) / lDays);
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP1);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									} else {
										lFP1Amt = (lFP1Amt * (EDays - date) / lDays);
										lFPDPAmt = (lFPDPAmt * (EDays - date) / lDays);
										lFPTIAmt = (lFPTIAmt * (EDays - date) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP1Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - date)) / lDays);
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP1);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									}

									lBasicPensionAmt = lBasicPensionAmt + lFP1Amt;
									lDPPerAmt = lDPPerAmt + lFPDPAmt;
									lTIPerAmt = lTIPerAmt + lFPTIAmt;
								} else if (lEndDate.equals(lFP2Date) || lEndDate.after(lFP2Date)) {
									// pay acc to fp2
									inputMap.put("OldBasicAmt", lFP2Amt);
									inputMap.put("BasicAmtType", "FP2");
									inputMap.put("IsAlive", "N");
									lRatePensionBillVO = getMasterRatesForMonth(inputMap);

									lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
									lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
									lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
									lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

									if (lTIPer > 0) {
										lFPTIAmt = ((lFP2Amt + lFPDPAmt) * ((lTIPer) / 100));
									}

									// Pension start Payment date is in current
									// month.
									// and start date before end date and after
									// death date.
									if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay < EDays && lPayStartDay >= date) {
										lFP2Amt = (lFP2Amt) * (EDays - lPayStartDay) / lDays;
										lFPDPAmt = (lFPDPAmt * (EDays - lPayStartDay) / lDays);
										lFPTIAmt = (lFPTIAmt * (EDays - lPayStartDay) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - lPayStartDay)) / lDays);
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									} else {
										lFP2Amt = (lFP2Amt) * (EDays - date) / lDays;
										lFPDPAmt = (lFPDPAmt * (EDays - date) / lDays);
										lFPTIAmt = (lFPTIAmt * (EDays - date) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - date)) / lDays);
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									}

									lBasicPensionAmt = lBasicPensionAmt + lFP2Amt;
									lDPPerAmt = lDPPerAmt + lFPDPAmt;
									lTIPerAmt = lTIPerAmt + lFPTIAmt;
								}
							}

							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay < EDays) {
								lMAAmt = lMAAmt * (EDays - lPayStartDay) / lDays;
								lSpecialCutAmt = lSpecialCutAmt * (EDays - lPayStartDay) / lDays;
								lOtherBenefits = lOtherBenefits * (EDays - lPayStartDay) / lDays;
								lPerPension = lPerPension * (EDays - lPayStartDay) / lDays;
								lIR = lIR * (EDays - lPayStartDay) / lDays;
							} else {
								lMAAmt = lMAAmt * EDays / lDays;
								lSpecialCutAmt = lSpecialCutAmt * EDays / lDays;
								lOtherBenefits = lOtherBenefits * EDays / lDays;
								lPerPension = lPerPension * EDays / lDays;
								lIR = lIR * EDays / lDays;
							}
						}
					} else {
						// end date not in this month

						inputMap.put("OldBasicAmt", lBasicPensionAmt);
						inputMap.put("BasicAmtType", "Basic"); // For Identify
						// Pension Type
						// in
						// getNewROPBasicAmt
						// is (BASIC /
						// FP1 / FP2)
						inputMap.put("IsAlive", "Y");
						lRatePensionBillVO = getMasterRatesForMonth(inputMap);

						lBasicPensionAmt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
						lDPPer = Double.parseDouble(lRatePensionBillVO.getDpPercent().toString());
						lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
						lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
						lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());
						lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
						lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

						if (lTIPer > 0) {
							lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lDPPerAmt) * ((lTIPer) / 100));
						}

						// compute reduced amount for the days alive

						// Pension start Payment date is in current month.
						// and start date before death date.
						if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay <= date) {
							lBasicPensionAmt = lBasicPensionAmt * (date - lPayStartDay) / lDays;
							lPensionCutAmt = lPensionCutAmt * (date - lPayStartDay) / lDays;
							lCVPMonthlyAmt = lCVPMonthlyAmt * (date - lPayStartDay) / lDays;
							lITCutAmt = lITCutAmt * (date - lPayStartDay) / lDays;
							lDPPerAmt = lDPPerAmt * (date - lPayStartDay) / lDays;
							lTIPerAmt = lTIPerAmt * (date - lPayStartDay) / lDays;

							if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
								lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
								lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lADPAmt) * ((lTIPer) / 100));
							}

							if ("Y".equals(lArrComputeFlag)) {
								lTmpNetAmt = 0D;
								lTmpNetAmt = Math.round(lBasicPensionAmt) - Math.round(lPensionCutAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) - Math.round(lCVPMonthlyAmt)
										+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (date - lPayStartDay) / lDays) - Math.round(lITCutAmt) - lRecoveryAmt + lArrearAmt
										- lCutATI;

								inputMap.put("PenRqstId", lStrPenRqstId);
								inputMap.put("PenCode", lStrPnsnrCode);
								inputMap.put("ArrearType", lStrArrPension);
								inputMap.put("EffFrom", lStrDate);
								inputMap.put("PayFrom", lStrBillCrtMonth);
								inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

								lObjArrearVO = new TrnPensionArrearDtls();
								lObjArrearVO = insertArrearDtls(inputMap);
								lLstInsertArrears.add(lObjArrearVO);
							}
						} else {
							lBasicPensionAmt = lBasicPensionAmt * date / lDays;
							lPensionCutAmt = lPensionCutAmt * date / lDays;
							lCVPMonthlyAmt = lCVPMonthlyAmt * date / lDays;
							lITCutAmt = lITCutAmt * date / lDays;
							lDPPerAmt = lDPPerAmt * date / lDays;
							lTIPerAmt = lTIPerAmt * date / lDays;

							if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
								lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
								lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lADPAmt) * ((lTIPer)) / 100);
							}

							if ("Y".equals(lArrComputeFlag)) {
								lTmpNetAmt = 0D;
								lTmpNetAmt = Math.round(lBasicPensionAmt) - Math.round(lPensionCutAmt) + Math.round(lDPPerAmt) + Math.round(lADPAmt) + lTIPerAmt - Math.round(lCVPMonthlyAmt)
										+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * date / lDays) - lITCutAmt - lRecoveryAmt + lArrearAmt - lCutATI;

								inputMap.put("PenRqstId", lStrPenRqstId);
								inputMap.put("PenCode", lStrPnsnrCode);
								inputMap.put("ArrearType", lStrArrPension);
								inputMap.put("EffFrom", lStrDate);
								inputMap.put("PayFrom", lStrBillCrtMonth);
								inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

								lObjArrearVO = new TrnPensionArrearDtls();
								lObjArrearVO = insertArrearDtls(inputMap);
								lLstInsertArrears.add(lObjArrearVO);
							}
						}

						// check for fp1 n fp2 in the remaining days
						if (fp1Date.equals(lStrDate) && fp2Date.equals(lStrDate)) {
							if (lDateOfDeath.equals(lFP2Date) || lDateOfDeath.after(lFP2Date)) {
								// given according to basic till date of death
								// now for remaining days give according to fp2
								// coz death after fp1 date

								inputMap.put("OldBasicAmt", lFP2Amt);
								inputMap.put("BasicAmtType", "FP2");
								inputMap.put("IsAlive", "N");
								lRatePensionBillVO = getMasterRatesForMonth(inputMap);

								lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
								lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
								lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
								lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

								if (lTIPer > 0) {
									lFPTIAmt = ((lFP2Amt + lFPDPAmt) * ((lTIPer) / 100));
								}

								// if FPFlage is N then do all computation and
								// put that into Arrears.
								// if FPFlage is Y and Family Pension is
								// available then do the computation and will
								// not add in Arrears(Bcuz ArrCommFlg will be N)
								// .
								if (("N".equals(inputMap.get("FPFlag"))) || ("Y".equals(inputMap.get("FPFlag")) && fpMemberList != null && fpMemberList.size() > 0)) {
									// Pension start Payment date is in current
									// month.
									// and start date after death date.
									if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay >= date) {
										lFP2Amt = (lFP2Amt * (lDays - lPayStartDay) / lDays);
										lFPDPAmt = (lFPDPAmt * (lDays - lPayStartDay) / lDays);
										lFPTIAmt = (lFPTIAmt * (lDays - lPayStartDay) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer)) / 100);
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt)
													+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - lPayStartDay)) / lDays);
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									} else {
										lFP2Amt = (lFP2Amt * (lDays - date) / lDays);
										lFPDPAmt = (lFPDPAmt * (lDays - date) / lDays);
										lFPTIAmt = (lFPTIAmt * (lDays - date) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * (lTIPer)) / 100;
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt)
													+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - date) / lDays);
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									}
									lBasicPensionAmt = lBasicPensionAmt + lFP2Amt;
									lDPPerAmt = lDPPerAmt + lFPDPAmt;
									lTIPerAmt = lTIPerAmt + lFPTIAmt;
								}
							} else {
								// give for remaining days... till fp1 date
								// according to fp1, rest according to fp2
								date1 = Integer.parseInt(new SimpleDateFormat("dd").format(lFP1Date));
								// 1 - date....according to basic
								// date - date1 ...according to fp1
								// date1 - ldays ...according to fp2

								inputMap.put("OldBasicAmt", lFP1Amt);
								inputMap.put("BasicAmtType", "FP1");
								inputMap.put("IsAlive", "N");
								lRatePensionBillVO = getMasterRatesForMonth(inputMap);

								lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
								lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
								lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
								lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

								if (lTIPer > 0) {
									lFPTIAmt = ((lFP1Amt + lFPDPAmt) * ((lTIPer) / 100));
								}

								// List divAmt = getDividedBasic(lFP1Amt,
								// lDPPer);

								// if FPFlage is N then do all cumputation and
								// put that into Arrears.
								// if FPFlage is Y and Family Pension is
								// available then do the cumputation and will
								// not add in Arrears(Bcuz ArrCommFlg will be N)
								// .
								if (("N".equals(inputMap.get("FPFlag"))) || ("Y".equals(inputMap.get("FPFlag")) && fpMemberList != null && fpMemberList.size() > 0)) {
									// Pension start Payment date is in current
									// month.
									// and start date before fp1 date and after
									// death date.
									if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay < date1 && lPayStartDay >= date) {
										lFP1Amt = (lFP1Amt * (date1 - lPayStartDay)) / lDays;
										lFPDPAmt = (lFPDPAmt * (date1 - lPayStartDay)) / lDays;
										lFPTIAmt = (lFPTIAmt * (date1 - lPayStartDay)) / lDays;

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP1Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// TI
													+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (date1 - lPayStartDay)) / lDays);
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP1);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									} else {
										lFP1Amt = (lFP1Amt * (date1 - date) / lDays);
										lFPDPAmt = (lFPDPAmt * (date1 - date) / lDays);
										lFPTIAmt = (lFPTIAmt * (date1 - date) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * (lTIPer)) / 100;
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP1Amt) + lFPDPAmt + Math.round(lFPADPAmt) + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// TI
													+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (date1 - date)) / lDays;
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP1);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									}

									lBasicPensionAmt = lBasicPensionAmt + lFP1Amt;
									lDPPerAmt = lDPPerAmt + lFPDPAmt;
									lTIPerAmt = lTIPerAmt + lFPTIAmt;

									// Pay acc to FP2.

									inputMap.put("OldBasicAmt", lFP2Amt);
									inputMap.put("BasicAmtType", "FP2");
									inputMap.put("IsAlive", "N");
									lRatePensionBillVO = getMasterRatesForMonth(inputMap);

									lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
									lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
									lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
									lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

									if (lTIPer > 0) {
										lFPTIAmt = ((lFP2Amt + lFPDPAmt) * ((lTIPer) / 100));
									}

									// Pension start Payment date is in current
									// month.
									// and start date after death date.
									if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay >= date1) {
										lFP2Amt = (lFP2Amt * (lDays - lPayStartDay)) / lDays;
										lFPDPAmt = (lFPDPAmt * (lDays - lPayStartDay)) / lDays;
										lFPTIAmt = (lFPTIAmt * (lDays - lPayStartDay)) / lDays;

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * (lTIPer)) / 100;
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - date1)) / lDays;
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									} else {
										lFP2Amt = (lFP2Amt * (lDays - date1) / lDays);
										lFPDPAmt = (lFPDPAmt * (lDays - date1) / lDays);
										lFPTIAmt = (lFPTIAmt * (lDays - date1) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - date1)) / lDays;
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									}

									lBasicPensionAmt = lBasicPensionAmt + lFP2Amt;
									lDPPerAmt = lDPPerAmt + lFPDPAmt;
									lTIPerAmt = lTIPerAmt + lFPTIAmt;
								}
							}
						} else {
							// give according to fp1 or fp2 whichever is
							// applicable
							if (lDateOfDeath.before(lFP1Date) || lDateOfDeath.equals(lFP1Date)) {

								inputMap.put("OldBasicAmt", lFP1Amt);
								inputMap.put("BasicAmtType", "FP1");
								inputMap.put("IsAlive", "N");
								lRatePensionBillVO = getMasterRatesForMonth(inputMap);

								lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
								lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
								lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
								lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

								if (lTIPer > 0) {
									lFPTIAmt = ((lFP1Amt + lFPDPAmt) * ((lTIPer) / 100));
								}

								// if FPFlage is N then do all computation and
								// put that into Arrears.
								// if FPFlage is Y and Family Pension is
								// available then do the computation and will
								// not add in Arrears(Bcuz ArrCommFlg will be N)
								// .
								if (("N".equals(inputMap.get("FPFlag"))) || ("Y".equals(inputMap.get("FPFlag")) && fpMemberList != null && fpMemberList.size() > 0)) {
									// Pension start Payment date is in current
									// month.
									// and start date after death date.
									if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay >= date) {
										lFP1Amt = (lFP1Amt * (lDays - lPayStartDay) / lDays);
										lFPDPAmt = (lFPDPAmt * (lDays - lPayStartDay) / lDays);
										lFPTIAmt = (lFPTIAmt * (lDays - lPayStartDay) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP1Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - lPayStartDay)) / lDays;
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP1);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									} else {
										lFP1Amt = (lFP1Amt * (lDays - date) / lDays);
										lFPDPAmt = (lFPDPAmt * (lDays - date) / lDays);
										lFPTIAmt = (lFPTIAmt * (lDays - date) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP1Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP1Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - date)) / lDays;
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP1);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									}
									lBasicPensionAmt = lBasicPensionAmt + lFP1Amt;
									lDPPerAmt = lDPPerAmt + lFPDPAmt;
									lTIPerAmt = lTIPerAmt + lFPTIAmt;
								}
							} else if (lDateOfDeath.after(lFP2Date) || lDateOfDeath.equals(lFP2Date)) {
								inputMap.put("OldBasicAmt", lFP2Amt);
								inputMap.put("BasicAmtType", "FP2");
								inputMap.put("IsAlive", "N");
								lRatePensionBillVO = getMasterRatesForMonth(inputMap);

								lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
								lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
								lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
								lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

								if (lTIPer > 0) {
									lFPTIAmt = ((lFP2Amt + lFPDPAmt) * ((lTIPer) / 100));
								}

								// if FPFlage is N then do all cumputation and
								// put that into Arrears.
								// if FPFlage is Y and Family Pension is
								// available then do the cumputation and will
								// not add in Arrears(Bcuz ArrCommFlg will be N)
								// .
								if (("N".equals(inputMap.get("FPFlag"))) || ("Y".equals(inputMap.get("FPFlag")) && fpMemberList != null && fpMemberList.size() > 0)) {
									// Pension start Payment date is in current
									// month.
									// and start date after death date.
									if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay >= date) {
										lFP2Amt = (lFP2Amt * (lDays - lPayStartDay) / lDays);
										lFPDPAmt = (lFPDPAmt * (lDays - lPayStartDay) / lDays);
										lFPTIAmt = (lFPTIAmt * (lDays - lPayStartDay) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - lPayStartDay)) / lDays;
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									} else {
										lFP2Amt = (lFP2Amt * (lDays - date) / lDays);
										lFPDPAmt = (lFPDPAmt * (lDays - date) / lDays);
										lFPTIAmt = (lFPTIAmt * (lDays - date) / lDays);

										if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
											lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
											lADPAmt = lADPAmt + lFPADPAmt;
											lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
										}

										if ("Y".equals(lArrComputeFlag)) {
											lTmpNetAmt = 0D;
											lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt) // basic
													// +
													// DP
													// +
													// lTIPerAmt
													+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - date)) / lDays;
											inputMap.put("PenRqstId", lStrPenRqstId);
											inputMap.put("PenCode", lStrPnsnrCode);
											inputMap.put("ArrearType", lStrArrFP2);
											inputMap.put("EffFrom", lStrDate);
											inputMap.put("PayFrom", lStrBillCrtMonth);
											inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

											lObjArrearVO = new TrnPensionArrearDtls();
											lObjArrearVO = insertArrearDtls(inputMap);
											lLstInsertArrears.add(lObjArrearVO);
										}
									}
									lBasicPensionAmt = lBasicPensionAmt + lFP2Amt;
									lDPPerAmt = lDPPerAmt + lFPDPAmt;
									lTIPerAmt = lTIPerAmt + lFPTIAmt;
								}
							}
						}

						if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate)) {
							lMAAmt = lMAAmt * (lDays - lPayStartDay) / lDays;
							lSpecialCutAmt = lSpecialCutAmt * (lDays - lPayStartDay) / lDays;
							lOtherBenefits = lOtherBenefits * (lDays - lPayStartDay) / lDays;
							lPerPension = lPerPension * (lDays - lPayStartDay) / lDays;
							lIR = lIR * (lDays - lPayStartDay) / lDays;
						}
					}
				} else {
					// pensioner is dead but has not expired in current month

					// gLogger.info("*************pensioner is dead but has not expired in current month**********");

					// in this case we have to make pensioncut, itcut and
					// cvpMonthly as zero
					lCVPMonthlyAmt = 0.0;
					lPensionCutAmt = 0.0;
					lITCutAmt = 0.0;

					if (fp1Date.equals(lStrDate) && fp2Date.equals(lStrDate) && endDate.equals(lStrDate)) {
						// all three fp1 n fp2 n end date in curr
						// month...compute division for them
						if (lEndDate.before(lFP1Date) || lEndDate.equals(lFP1Date)) { // end
							// date
							// is
							// before
							// or
							// equal
							// to
							// fp1
							// date
							// give according to fp1 till end date
							// gLogger.info("end date is before or equal to fp1 date, give according to fp1 till end date");

							// pay till end date acc to fp1
							inputMap.put("OldBasicAmt", lFP1Amt);
							inputMap.put("BasicAmtType", "FP1");
							inputMap.put("IsAlive", "N");
							lRatePensionBillVO = getMasterRatesForMonth(inputMap);

							lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
							lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
							lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
							lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

							lBasicPensionAmt = lFP1Amt;
							lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
							lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

							if (lTIPer > 0) {
								lTIPerAmt = ((lFP1Amt + lDPPerAmt) * ((lTIPer) / 100));
							}

							// Pension start Payment date is in current month.
							// and start date before end date.
							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay <= EDays) {
								lBasicPensionAmt = lBasicPensionAmt * (EDays - lPayStartDay) / lDays;
								lDPPerAmt = lDPPerAmt * (EDays - lPayStartDay) / lDays;
								lTIPerAmt = lTIPerAmt * (EDays - lPayStartDay) / lDays;
								lMAAmt = lMAAmt * (EDays - lPayStartDay) / lDays;
								lSpecialCutAmt = lSpecialCutAmt * (EDays - lPayStartDay) / lDays;
								lOtherBenefits = lOtherBenefits * (EDays - lPayStartDay) / lDays;
								lPerPension = lPerPension * (EDays - lPayStartDay) / lDays;
								lIR = lIR * (EDays - lPayStartDay) / lDays;

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) + Math.round(lPerPension) + Math.round(lIR)
											+ Math.round(lMAAmt) - Math.round(lSpecialCutAmt) + Math.round(lOtherBenefits))
											- lRecoveryAmt + lArrearAmt - lCutATI;
									// - lPensionCutAmt + lDPPerAmt -
									// lCVPMonthlyAmt - lITCutAmt
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP1);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							} else {
								lBasicPensionAmt = lBasicPensionAmt * EDays / lDays;
								lDPPerAmt = lDPPerAmt * EDays / lDays;
								lTIPerAmt = lTIPerAmt * EDays / lDays;
								lMAAmt = lMAAmt * EDays / lDays;
								lSpecialCutAmt = lSpecialCutAmt * EDays / lDays;
								lOtherBenefits = lOtherBenefits * EDays / lDays;
								lPerPension = lPerPension * EDays / lDays;
								lIR = lIR * EDays / lDays;

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) + Math.round(lPerPension) + Math.round(lIR)
											+ Math.round(lMAAmt) - Math.round(lSpecialCutAmt) + Math.round(lOtherBenefits))
											- lRecoveryAmt + lArrearAmt - lCutATI;
									// - lPensionCutAmt + lDPPerAmt -
									// lCVPMonthlyAmt - lITCutAmt
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP1);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							}
						} else {
							// give upto fp1 date according to fp1 after tht
							// give according to fp2 till end date

							// pay till fp1 date acc to fp1
							inputMap.put("OldBasicAmt", lFP1Amt);
							inputMap.put("BasicAmtType", "FP1");
							inputMap.put("IsAlive", "N");
							lRatePensionBillVO = getMasterRatesForMonth(inputMap);

							lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
							lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
							lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
							lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

							// Pension start Payment date is in current
							// month.and start date before end date.
							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay <= lfp1Days) {
								lBasicPensionAmt = lFP1Amt * (lfp1Days - lPayStartDay) / lDays;
								lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString()) * (lfp1Days - lPayStartDay) / lDays;
								lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString()) * (lfp1Days - lPayStartDay) / lDays;

								if (lTIPer > 0) {
									lTIPerAmt = ((lBasicPensionAmt + lDPPerAmt) * ((lTIPer) / 100));
								}

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt)
											+ (((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lfp1Days - lPayStartDay)) / lDays) - lRecoveryAmt + lArrearAmt - lCutATI;
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP1);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							} else {

								lBasicPensionAmt = lFP1Amt * lfp1Days / lDays;
								lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString()) * lfp1Days / lDays;
								lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString()) * lfp1Days / lDays;

								if (lTIPer > 0) {
									lTIPerAmt = ((lBasicPensionAmt + lDPPerAmt) * ((lTIPer) / 100));
								}

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt)
											+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * lfp1Days / lDays) - lRecoveryAmt + lArrearAmt - lCutATI;
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP1);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							}

							// pay from fp1 date to end date acc to fp2
							inputMap.put("OldBasicAmt", lFP2Amt);
							inputMap.put("BasicAmtType", "FP2");
							inputMap.put("IsAlive", "N");
							lRatePensionBillVO = getMasterRatesForMonth(inputMap);

							lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
							lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
							lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
							lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

							if (lTIPer > 0) {
								lFPTIAmt = ((lFP2Amt + lFPDPAmt) * ((lTIPer) / 100));
							}

							// Pension start Payment date is in current month.
							// and start date before end date and after fp1
							// date.
							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay < EDays && lPayStartDay >= lfp1Days) {
								lFP2Amt = (lFP2Amt * (EDays - lPayStartDay) / lDays);
								lFPDPAmt = (lFPDPAmt * (EDays - lPayStartDay) / lDays);
								lFPTIAmt = (lFPTIAmt * (EDays - lPayStartDay) / lDays);

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lADPAmt = lADPAmt + lFPADPAmt;
									lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt)
											+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - lPayStartDay) / lDays);
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP2);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							} else {
								lFP2Amt = (lFP2Amt * (EDays - lfp1Days) / lDays);
								lFPDPAmt = (lFPDPAmt * (EDays - lfp1Days) / lDays);
								lFPTIAmt = (lFPTIAmt * (EDays - lfp1Days) / lDays);

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lADPAmt = lADPAmt + lFPADPAmt;
									lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt)
											+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (EDays - lfp1Days) / lDays);
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP2);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							}

							lBasicPensionAmt = lBasicPensionAmt + lFP2Amt;
							lDPPerAmt = lDPPerAmt + lFPDPAmt;
							lTIPerAmt = lTIPerAmt + lFPTIAmt;
						}
					} else {
						// all three fp1 fp2 n end date not in this month...
						if (fp1Date.equals(lStrDate) && fp2Date.equals(lStrDate)) {
							// give till fp1 date according to fp1, rest
							// according to fp2
							// gLogger.info("give till fp1 date according to fp1, rest according to fp2");
							date = Integer.parseInt(new SimpleDateFormat("dd").format(lFP1Date));

							inputMap.put("OldBasicAmt", lFP1Amt);
							inputMap.put("BasicAmtType", "FP1");
							inputMap.put("IsAlive", "N");
							lRatePensionBillVO = getMasterRatesForMonth(inputMap);

							lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
							lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
							lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
							lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

							// Pension start Payment date is in current month.
							// start date before fp1 date.
							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay <= date) {
								lBasicPensionAmt = lFP1Amt * (date - lPayStartDay) / lDays;
								lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString()) * (date - lPayStartDay) / lDays;
								lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString()) * (date - lPayStartDay) / lDays;

								if (lTIPer > 0) {
									lTIPerAmt = ((lBasicPensionAmt + lDPPerAmt) * ((lTIPer) / 100));
								}

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt)
											+ (lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (date - lPayStartDay) / lDays - lRecoveryAmt + lArrearAmt - lCutATI;
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP1);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							} else {
								lBasicPensionAmt = lFP1Amt * date / lDays;
								lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString()) * date / lDays;
								lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString()) * date / lDays;

								if (lTIPer > 0) {
									lTIPerAmt = ((lBasicPensionAmt + lDPPerAmt) * ((lTIPer) / 100));
								}

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt)
											+ (lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * date / lDays - lRecoveryAmt + lArrearAmt - lCutATI;
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP1);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							}

							// Payment According to FP2 Amount.

							inputMap.put("OldBasicAmt", lFP2Amt);
							inputMap.put("BasicAmtType", "FP2");
							inputMap.put("IsAlive", "N");
							lRatePensionBillVO = getMasterRatesForMonth(inputMap);

							lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
							lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
							lFPDPAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
							lFPTIAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

							if (lTIPer > 0) {
								lFPTIAmt = ((lFP2Amt + lFPDPAmt) * ((lTIPer) / 100));
							}

							// Pension start Payment date is in current month.
							// start date after death date.
							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay >= date) {
								lFP2Amt = ((lFP2Amt * (lDays - lPayStartDay) / lDays));
								lFPDPAmt = (lFPDPAmt * (lDays - lPayStartDay) / lDays);
								lFPTIAmt = (lFPTIAmt * (lDays - lPayStartDay) / lDays);

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lADPAmt = lADPAmt + lFPADPAmt;
									lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt)
											+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - lPayStartDay) / lDays);
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP2);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							} else {
								lFP2Amt = ((lFP2Amt * (lDays - date) / lDays));
								lFPDPAmt = (lFPDPAmt * (lDays - date) / lDays);
								lFPTIAmt = (lFPTIAmt * (lDays - date) / lDays);

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lFPADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lADPAmt = lADPAmt + lFPADPAmt;
									lFPTIAmt = (((lFP2Amt) + lFPADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = Math.round(lFP2Amt) + Math.round(lFPDPAmt) + lFPADPAmt + Math.round(lFPTIAmt)
											+ ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits) * (lDays - date) / lDays);
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP2);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							}

							lBasicPensionAmt = lBasicPensionAmt + lFP2Amt;
							lDPPerAmt = lDPPerAmt + lFPDPAmt;
							lTIPerAmt = lTIPerAmt + lFPTIAmt;
						} else if (Integer.valueOf(fp1Date) >= Integer.valueOf(lStrDate) && endDate.equals(lStrDate)) {
							// fp1 applicable for whole month

							// give according to fp1 till end date

							inputMap.put("OldBasicAmt", lFP1Amt);
							inputMap.put("BasicAmtType", "FP1");
							inputMap.put("IsAlive", "N");
							lRatePensionBillVO = getMasterRatesForMonth(inputMap);

							lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
							lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
							lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
							lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

							// List divAmt = getDividedBasic(lFP1Amt, lDPPer);
							lBasicPensionAmt = lFP1Amt;
							lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
							lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

							if (lTIPer > 0) {
								lTIPerAmt = ((lFP1Amt + lDPPerAmt) * ((lTIPer) / 100));
							}

							// Pension start Payment date is in current month.
							// start date before end date.
							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay <= EDays) {
								lBasicPensionAmt = lBasicPensionAmt * (EDays - lPayStartDay) / lDays;
								lDPPerAmt = lDPPerAmt * (EDays - lPayStartDay) / lDays;
								lTIPerAmt = lTIPerAmt * (EDays - lPayStartDay) / lDays;

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) + ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits)
											* (EDays - lPayStartDay) / lDays))
											- lRecoveryAmt + lArrearAmt - lCutATI;
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP1);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							} else {
								lBasicPensionAmt = lBasicPensionAmt * EDays / lDays;
								lDPPerAmt = lDPPerAmt * EDays / lDays;
								lTIPerAmt = lTIPerAmt * EDays / lDays;
								lMAAmt = lMAAmt * EDays / lDays;

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) + ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits)
											* EDays / lDays))
											- lRecoveryAmt + lArrearAmt - lCutATI;
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP1);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							}
						} else if (Integer.valueOf(fp2Date) <= Integer.valueOf(lStrDate) && endDate.equals(lStrDate)) {
							// fp2 applicable for whole month

							// give according to fp2 till end date
							// gLogger.info("give according to fp2 till end date");

							inputMap.put("OldBasicAmt", lFP2Amt);
							inputMap.put("BasicAmtType", "FP2");
							inputMap.put("IsAlive", "N");
							lRatePensionBillVO = getMasterRatesForMonth(inputMap);

							lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
							lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
							lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
							lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

							lBasicPensionAmt = lFP2Amt;
							lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
							lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

							if (lTIPer > 0) {
								lTIPerAmt = ((lFP2Amt + lDPPerAmt) * ((lTIPer) / 100));
							}

							// Pension start Payment date is in current month.
							// start date before end date.
							if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay <= EDays) {
								lBasicPensionAmt = lBasicPensionAmt * (EDays - lPayStartDay) / lDays;
								lDPPerAmt = lDPPerAmt * (EDays - lPayStartDay) / lDays;
								lTIPerAmt = lTIPerAmt * (EDays - lPayStartDay) / lDays;

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) + ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits)
											* (EDays - lPayStartDay) / lDays))
											- lRecoveryAmt + lArrearAmt - lCutATI;
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP2);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							} else {
								lBasicPensionAmt = lBasicPensionAmt * EDays / lDays;
								lDPPerAmt = lDPPerAmt * EDays / lDays;
								lTIPerAmt = lTIPerAmt * EDays / lDays;

								if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
									lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
									lTIPerAmt = (((lBasicPensionAmt) + lADPAmt) * ((lTIPer) / 100));
								}

								if ("Y".equals(lArrComputeFlag)) {
									lTmpNetAmt = 0D;
									lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + lADPAmt + Math.round(lTIPerAmt) + ((lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits)
											* EDays / lDays))
											- lRecoveryAmt + lArrearAmt - lCutATI;
									inputMap.put("PenRqstId", lStrPenRqstId);
									inputMap.put("PenCode", lStrPnsnrCode);
									inputMap.put("ArrearType", lStrArrFP2);
									inputMap.put("EffFrom", lStrDate);
									inputMap.put("PayFrom", lStrBillCrtMonth);
									inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

									lObjArrearVO = new TrnPensionArrearDtls();
									lObjArrearVO = insertArrearDtls(inputMap);
									lLstInsertArrears.add(lObjArrearVO);
								}
							}
						} else {
							// only 1 either fp1 or fp2 applicable for whole
							// month
							if (Integer.valueOf(lStrDate) <= Integer.valueOf(fp1Date)) {
								inputMap.put("OldBasicAmt", lFP1Amt);
								inputMap.put("BasicAmtType", "FP1");
								inputMap.put("IsAlive", "N");
								lRatePensionBillVO = getMasterRatesForMonth(inputMap);

								lFP1Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
								lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
								lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
								lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

								lBasicPensionAmt = lFP1Amt;
								lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
								lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

								if (lTIPer > 0) {
									lTIPerAmt = ((lFP1Amt + lDPPerAmt) * lTIPer) / 100;
								}

								// Payment Start date is in this month...
								if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate)) {
									lBasicPensionAmt = lBasicPensionAmt * (lDays - lPayStartDay) / lDays;
									lDPPerAmt = lDPPerAmt * (lDays - lPayStartDay) / lDays;
									lTIPerAmt = lTIPerAmt * (lDays - lPayStartDay) / lDays;
									lMAAmt = lMAAmt * (lDays - lPayStartDay) / lDays;
									lSpecialCutAmt = lSpecialCutAmt * (lDays - lPayStartDay) / lDays;
									lOtherBenefits = lOtherBenefits * (lDays - lPayStartDay) / lDays;
									lPerPension = lPerPension * (lDays - lPayStartDay) / lDays;
									lIR = lIR * (lDays - lPayStartDay) / lDays;

									if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
										lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
										lTIPerAmt = ((lBasicPensionAmt + lADPAmt) * lTIPer) / 100;
									}

									if ("Y".equals(lArrComputeFlag)) {
										lTmpNetAmt = 0D;
										lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + Math.round(lADPAmt) + Math.round(lTIPerAmt) + lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits)
												- lRecoveryAmt + lArrearAmt - lCutATI;

										inputMap.put("PenRqstId", lStrPenRqstId);
										inputMap.put("PenCode", lStrPnsnrCode);
										inputMap.put("ArrearType", lStrArrFP1);
										inputMap.put("EffFrom", lStrDate);
										inputMap.put("PayFrom", lStrBillCrtMonth);
										inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

										lObjArrearVO = new TrnPensionArrearDtls();
										lObjArrearVO = insertArrearDtls(inputMap);
										lLstInsertArrears.add(lObjArrearVO);
									}
								} else {
									if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
										lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
										lTIPerAmt = ((lBasicPensionAmt + lADPAmt) * lTIPer) / 100;
									}

									if ("Y".equals(lArrComputeFlag)) {
										lTmpNetAmt = 0D;
										lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + Math.round(lADPAmt) + Math.round(lTIPerAmt) + lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits)
												- lRecoveryAmt + lArrearAmt - lCutATI;

										inputMap.put("PenRqstId", lStrPenRqstId);
										inputMap.put("PenCode", lStrPnsnrCode);
										inputMap.put("ArrearType", lStrArrFP1);
										inputMap.put("EffFrom", lStrDate);
										inputMap.put("PayFrom", lStrBillCrtMonth);
										inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

										lObjArrearVO = new TrnPensionArrearDtls();
										lObjArrearVO = insertArrearDtls(inputMap);
										lLstInsertArrears.add(lObjArrearVO);
									}
								}
							} else if (Integer.valueOf(lStrDate) >= Integer.valueOf(fp2Date)) {

								inputMap.put("OldBasicAmt", lFP2Amt);
								inputMap.put("BasicAmtType", "FP2");
								inputMap.put("IsAlive", "N");
								lRatePensionBillVO = getMasterRatesForMonth(inputMap);

								lFP2Amt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
								lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
								lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
								lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());

								lBasicPensionAmt = lFP2Amt;
								lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
								lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());

								if (lTIPer > 0) {
									lTIPerAmt = ((lFP2Amt + lDPPerAmt) * lTIPer) / 100;
								}

								// Payment Start date is in this month...
								if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate)) {
									lBasicPensionAmt = lBasicPensionAmt * (lDays - lPayStartDay) / lDays;
									lDPPerAmt = lDPPerAmt * (lDays - lPayStartDay) / lDays;
									lTIPerAmt = lTIPerAmt * (lDays - lPayStartDay) / lDays;
									lMAAmt = lMAAmt * (lDays - lPayStartDay) / lDays;
									lSpecialCutAmt = lSpecialCutAmt * (lDays - lPayStartDay) / lDays;
									lOtherBenefits = lOtherBenefits * (lDays - lPayStartDay) / lDays;
									lPerPension = lPerPension * (lDays - lPayStartDay) / lDays;
									lIR = lIR * (lDays - lPayStartDay) / lDays;

									if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
										lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
										lTIPerAmt = ((lBasicPensionAmt + lADPAmt) * lTIPer) / 100;
									}

									if ("Y".equals(lArrComputeFlag)) {
										lTmpNetAmt = 0D;
										lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + Math.round(lADPAmt) + Math.round(lTIPerAmt) + lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits)
												- lRecoveryAmt + lArrearAmt - lCutATI;

										inputMap.put("PenRqstId", lStrPenRqstId);
										inputMap.put("PenCode", lStrPnsnrCode);
										inputMap.put("ArrearType", lStrArrFP1);
										inputMap.put("EffFrom", lStrDate);
										inputMap.put("PayFrom", lStrBillCrtMonth);
										inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

										lObjArrearVO = new TrnPensionArrearDtls();
										lObjArrearVO = insertArrearDtls(inputMap);
										lLstInsertArrears.add(lObjArrearVO);
									}
								} else {
									if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
										lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
										lTIPerAmt = ((lBasicPensionAmt + lADPAmt) * lTIPer) / 100;
									}

									if ("Y".equals(lArrComputeFlag)) {
										lTmpNetAmt = 0D;
										lTmpNetAmt = (Math.round(lBasicPensionAmt) + Math.round(lDPPerAmt) + Math.round(lADPAmt) + Math.round(lTIPerAmt) + lPerPension + lIR + lMAAmt - lSpecialCutAmt + lOtherBenefits)
												- lRecoveryAmt + lArrearAmt - lCutATI;
										inputMap.put("PenRqstId", lStrPenRqstId);
										inputMap.put("PenCode", lStrPnsnrCode);
										inputMap.put("ArrearType", lStrArrFP2);
										inputMap.put("EffFrom", lStrDate);
										inputMap.put("PayFrom", lStrBillCrtMonth);
										inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

										lObjArrearVO = new TrnPensionArrearDtls();
										lObjArrearVO = insertArrearDtls(inputMap);
										lLstInsertArrears.add(lObjArrearVO);
									}
								}
							}
						}
					}
				}
				inputMap.put("TransactionBasic", lBasicPensionAmt);
				inputMap.put("TransactionDPAmnt", lDPPerAmt);
				inputMap.put("TransactionPCut", lPensionCutAmt);
				inputMap.put("TrnsactionFlag", "Y");
				lRatePensionBillVOTemp = getMasterRatesForMonth(inputMap);
				lRatePensionBillVOTemp = null;
				inputMap.put("TransactionBasic", null);
				inputMap.put("TransactionPCut", null);
				inputMap.put("TrnsactionFlag", "N");
			} else {
				// pensioner is alive....but check if end date in curr mnth

				inputMap.put("OldBasicAmt", lBasicPensionAmt);
				inputMap.put("BasicAmtType", "Basic"); // For Identify Pension
				// Type in
				// getNewROPBasicAmt is
				// (BASIC / FP1 / FP2)
				inputMap.put("IsAlive", "Y");
				lRatePensionBillVO = getMasterRatesForMonth(inputMap);

				lBasicPensionAmt = Double.parseDouble(lRatePensionBillVO.getBasicPensionAmount().toString());
				lDPPer = Double.parseDouble(lRatePensionBillVO.getDpPercent().toString());
				lTIPer = Double.parseDouble(lRatePensionBillVO.getTiPercent().toString());
				lDPPerAmt = Double.parseDouble(lRatePensionBillVO.getDpPercentAmount().toString());
				lTIPerAmt = Double.parseDouble(lRatePensionBillVO.getTiPercentAmount().toString());
				lMAAmt = Double.parseDouble(lRatePensionBillVO.getMedicalAllowenceAmount().toString());
				lIR = Double.parseDouble(lRatePensionBillVO.getIr().toString());
				// lTIArr = lTIArr +
				// Double.parseDouble(lRatePensionBillVO.getTIArrearsAmount().toString());

				if (lTIPer > 0) {
					lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lDPPerAmt) * lTIPer) / 100;
				}

				if (endDate.equals(lStrDate)) {
					// Pension start Payment date is in current month.
					// Payment start date is before end date.
					if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay <= EDays) {

						// Payment Start date in current month....
						// so give him pension only start date to end date.
						lBasicPensionAmt = lBasicPensionAmt * (EDays - lPayStartDay) / lDays;
						lDPPerAmt = lDPPerAmt * (EDays - lPayStartDay) / lDays;
						lTIPerAmt = lTIPerAmt * (EDays - lPayStartDay) / lDays;
						lMAAmt = lMAAmt * (EDays - lPayStartDay) / lDays;
						lCVPMonthlyAmt = lCVPMonthlyAmt * (EDays - lPayStartDay) / lDays;
						lPensionCutAmt = lPensionCutAmt * (EDays - lPayStartDay) / lDays;
						lITCutAmt = lITCutAmt * (EDays - lPayStartDay) / lDays;
						lSpecialCutAmt = lSpecialCutAmt * (EDays - lPayStartDay) / lDays;
						lOtherBenefits = lOtherBenefits * (EDays - lPayStartDay) / lDays;
						lPerPension = lPerPension * (EDays - lPayStartDay) / lDays;
						lIR = lIR * (EDays - lPayStartDay) / lDays;

						if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
							lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
							lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lADPAmt) * lTIPer) / 100;
						}

						if ("Y".equals(lArrComputeFlag)) {
							lTmpNetAmt = 0D;
							lTmpNetAmt += Math.round(lBasicPensionAmt) - Math.round(lPensionCutAmt) + Math.round(lDPPerAmt) + Math.round(lADPAmt) + Math.round(lTIPerAmt) - Math.round(lCVPMonthlyAmt)
									+ Math.round(lPerPension) + Math.round(lIR) + Math.round(lMAAmt) - Math.round(lITCutAmt) - Math.round(lSpecialCutAmt) + Math.round(lOtherBenefits)
									- Math.round(lRecoveryAmt) + Math.round(lArrearAmt) - Math.round(lCutATI);

							inputMap.put("PenRqstId", lStrPenRqstId);
							inputMap.put("PenCode", lStrPnsnrCode);
							inputMap.put("ArrearType", lStrArrPension);
							inputMap.put("EffFrom", lStrDate);
							inputMap.put("PayFrom", lStrBillCrtMonth);
							inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

							lObjArrearVO = new TrnPensionArrearDtls();
							lObjArrearVO = insertArrearDtls(inputMap);
							lLstInsertArrears.add(lObjArrearVO);
						}
					} else {
						// end date in curr month...so give him pension only
						// till end date

						// dividing acc to end date.
						lBasicPensionAmt = lBasicPensionAmt * EDays / lDays;
						lDPPerAmt = lDPPerAmt * EDays / lDays;
						lTIPerAmt = lTIPerAmt * EDays / lDays;
						lMAAmt = lMAAmt * EDays / lDays;
						lCVPMonthlyAmt = lCVPMonthlyAmt * EDays / lDays;
						lPensionCutAmt = lPensionCutAmt * EDays / lDays;
						lITCutAmt = lITCutAmt * EDays / lDays;
						lSpecialCutAmt = lSpecialCutAmt * EDays / lDays;
						lOtherBenefits = lOtherBenefits * EDays / lDays;
						lPerPension = lPerPension * EDays / lDays;
						lIR = lIR * EDays / lDays;

						if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
							lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
							lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lADPAmt) * lTIPer) / 100;
						}

						if ("Y".equals(lArrComputeFlag)) {
							lTmpNetAmt = 0D;
							lTmpNetAmt += Math.round(lBasicPensionAmt) - Math.round(lPensionCutAmt) + Math.round(lDPPerAmt) + Math.round(lADPAmt) + Math.round(lTIPerAmt) - Math.round(lCVPMonthlyAmt)
									+ Math.round(lPerPension) + Math.round(lIR) + Math.round(lMAAmt) - Math.round(lITCutAmt) - Math.round(lSpecialCutAmt) + Math.round(lOtherBenefits)
									- Math.round(lRecoveryAmt) + Math.round(lArrearAmt) - Math.round(lCutATI);

							inputMap.put("PenRqstId", lStrPenRqstId);
							inputMap.put("PenCode", lStrPnsnrCode);
							inputMap.put("ArrearType", lStrArrPension);
							inputMap.put("EffFrom", lStrDate);
							inputMap.put("PayFrom", lStrBillCrtMonth);
							inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

							lObjArrearVO = new TrnPensionArrearDtls();
							lObjArrearVO = insertArrearDtls(inputMap);
							lLstInsertArrears.add(lObjArrearVO);
						}
					}
				} else {
					// //pensioner is alive and PayStart date is in this
					// month...
					if (lStrPayStartYM != null && lStrPayStartYM.equals(lStrDate) && lPayStartDay != lDays) {
						// Payment Start date in current month....
						// so give him pension only start date to end date.
						lBasicPensionAmt = lBasicPensionAmt * (lDays - lPayStartDay) / lDays;
						lDPPerAmt = lDPPerAmt * (lDays - lPayStartDay) / lDays;
						lTIPerAmt = lTIPerAmt * (lDays - lPayStartDay) / lDays;
						lMAAmt = lMAAmt * (lDays - lPayStartDay) / lDays;
						lCVPMonthlyAmt = lCVPMonthlyAmt * (lDays - lPayStartDay) / lDays;
						lPensionCutAmt = lPensionCutAmt * (lDays - lPayStartDay) / lDays;
						lITCutAmt = lITCutAmt * (lDays - lPayStartDay) / lDays;
						lSpecialCutAmt = lSpecialCutAmt * (lDays - lPayStartDay) / lDays;
						lOtherBenefits = lOtherBenefits * (lDays - lPayStartDay) / lDays;
						lPerPension = lPerPension * (lDays - lPayStartDay) / lDays;
						lIR = lIR * (lDays - lPayStartDay) / lDays;

						if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
							lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
							lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lADPAmt) * lTIPer) / 100;
						}

						if ("Y".equals(lArrComputeFlag)) {
							lTmpNetAmt = 0D;
							lTmpNetAmt += Math.round(lBasicPensionAmt) - Math.round(lPensionCutAmt) + Math.round(lDPPerAmt) + Math.round(lADPAmt) + Math.round(lTIPerAmt) - Math.round(lCVPMonthlyAmt)
									+ Math.round(lPerPension) + Math.round(lIR) + Math.round(lMAAmt) - Math.round(lITCutAmt) - Math.round(lSpecialCutAmt) + Math.round(lOtherBenefits)
									- Math.round(lRecoveryAmt) + Math.round(lArrearAmt) - Math.round(lCutATI);

							inputMap.put("PenRqstId", lStrPenRqstId);
							inputMap.put("PenCode", lStrPnsnrCode);
							inputMap.put("ArrearType", lStrArrPension);
							inputMap.put("EffFrom", lStrDate);
							inputMap.put("PayFrom", lStrBillCrtMonth);
							inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

							lObjArrearVO = new TrnPensionArrearDtls();
							lObjArrearVO = insertArrearDtls(inputMap);
							lLstInsertArrears.add(lObjArrearVO);
						}
					} else {
						// pensioner is alive and end & PayStart date not in
						// this month...i.e normal case
						// check if arrear to be inserted

						if (l6PayFlg == true && lADPAmtLst != null && !lADPAmtLst.isEmpty() && lADPAmtLst.size() > l6PayCount) {
							lADPAmt = Double.valueOf(lADPAmtLst.get(l6PayCount++).toString());
							lTIPerAmt = (((Math.round(lBasicPensionAmt) - lPensionCutAmt) + lADPAmt) * lTIPer) / 100;
						}

						if ("Y".equals(lArrComputeFlag)) {
							lTmpNetAmt = 0D;
							lTmpNetAmt += Math.round(lBasicPensionAmt) - Math.round(lPensionCutAmt) + Math.round(lDPPerAmt) + Math.round(lADPAmt) + Math.round(lTIPerAmt) - Math.round(lCVPMonthlyAmt)
									+ Math.round(lPerPension) + Math.round(lIR) + Math.round(lMAAmt) - Math.round(lITCutAmt) - Math.round(lSpecialCutAmt) + Math.round(lOtherBenefits)
									- Math.round(lRecoveryAmt) + Math.round(lArrearAmt) - Math.round(lCutATI);

							inputMap.put("PenRqstId", lStrPenRqstId);
							inputMap.put("PenCode", lStrPnsnrCode);
							inputMap.put("ArrearType", lStrArrPension);
							inputMap.put("EffFrom", lStrDate);
							inputMap.put("PayFrom", lStrBillCrtMonth);
							inputMap.put("DiffAmt", Math.round(lTmpNetAmt));

							lObjArrearVO = new TrnPensionArrearDtls();
							lObjArrearVO = insertArrearDtls(inputMap);
							lLstInsertArrears.add(lObjArrearVO);
						}
					}
				}
				inputMap.put("TransactionBasic", lBasicPensionAmt);
				inputMap.put("TransactionDPAmnt", lDPPerAmt);
				inputMap.put("TransactionPCut", lPensionCutAmt);
				inputMap.put("TrnsactionFlag", "Y");
				lRatePensionBillVOTemp = getMasterRatesForMonth(inputMap);
				lRatePensionBillVOTemp = null;
				inputMap.put("TransactionBasic", null);
				inputMap.put("TransactionPCut", null);
				inputMap.put("TrnsactionFlag", "N");
			}

			// DP Effected 2004 applied into 2005 Login Added... Start
			// Logic Add for
			// Pensioner who's not getting pension between 01/04/2004 (200404)
			// and 31/03/2005 (200503)
			// or Month of July 2007 (33%) and October 2007 (67%) where actual
			// DP applied difference would be paid

			Double lDP2004MrgArrear = 0D;
			if (lStrBillType.equalsIgnoreCase("Monthly")) {
				if (lIntDate != 0 && lIntDate > 200404 && lIntDate <= 200503) {
					if (!inputMap.containsKey("DP2004MrgArrear")) {
						lDP2004MrgArrear = getDifferenceOfAppliedDP2004(inputMap);
						inputMap.put("DP2004MrgArrear", lDP2004MrgArrear);
					}
				} else if (lIntDate != 0 && lIntDate.intValue() == 200707) // July
				// 2007
				// (33%)
				// Payment
				{
					if (inputMap.containsKey("DP2004MrgArrear")) {
						lDP2004MrgArrear = (Double) inputMap.get("DP2004MrgArrear");
						Double lDivdValue = (lDP2004MrgArrear * 33.34) / 100;
						lOtherArr = lOtherArr + Math.round(lDivdValue);
					} else {
						lDP2004MrgArrear = getDifferenceOfAppliedDP2004(inputMap);
						Double lDivdValue = (lDP2004MrgArrear * 33.34) / 100;
						lOtherArr = lOtherArr + Math.round(lDivdValue);
						inputMap.put("DP2004MrgArrear", lDP2004MrgArrear);
					}
				} else if (lIntDate != 0 && lIntDate.intValue() == 200710) // Oct
				// 2007
				// (67%)
				// Payment
				{
					if (inputMap.containsKey("DP2004MrgArrear")) {
						lDP2004MrgArrear = (Double) inputMap.get("DP2004MrgArrear");
						Double lDivdValue = (lDP2004MrgArrear * 66.67) / 100;
						lOtherArr = lOtherArr + Math.round(lDivdValue);
					} else {
						lDP2004MrgArrear = getDifferenceOfAppliedDP2004(inputMap);
						Double lDivdValue = (lDP2004MrgArrear * 66.67) / 100;
						lOtherArr = lOtherArr + Math.round(lDivdValue);
						inputMap.put("DP2004MrgArrear", lDP2004MrgArrear);
					}

				}
			}
			// DP Effected 2004 applied into 2005 Login Added... End

			// / All Final values Are Received.

			// Getting Transaction Data to be Paid in Current Month... Start
			lTrnPaymentLst = (List<TrnPensionArrearDtls>) inputMap.get("TrnPaymentLst");
			if (lTrnPaymentLst != null) {
				int tempForMont = 0;
				for (int t = 0; t < lTrnPaymentLst.size(); t++) {
					lObjArrearVO = lTrnPaymentLst.get(t);

					if (lObjArrearVO.getPaymentFromYyyymm() == lIntDate.intValue() && lObjArrearVO.getPensionerCode().equals(lStrPnsnrCode)
							&& (!inputMap.containsKey("BillDtlsPayForMonth") || ((Integer) inputMap.get("BillDtlsPayForMonth")) <= lObjArrearVO.getPaymentFromYyyymm())) {
						if ((tempForMont != lObjArrearVO.getEffectedFromYyyymm()) || (t == 0)) {
							if (lObjArrearVO.getArrearFieldType().equalsIgnoreCase("TI")) {
								lTIArr += Math.round(Double.valueOf(lObjArrearVO.getDifferenceAmount().toString()));
							} else {
								lOtherArr += Double.valueOf(lObjArrearVO.getDifferenceAmount().toString());
							}
						}
						/*
						 * else { lTrnPaymentLst.remove(t); }
						 */
						tempForMont = lObjArrearVO.getEffectedFromYyyymm();
					}
				}
			}
			// Getting Transaction Data to be Paid in Current Month... End

			lArrearAmt = lTIArr + lOtherArr;
			// Transaction Payment Arrear Pay in current month so add that inot
			// arrear details.
			if ("Y".equals(lArrComputeFlag)) {
				inputMap.put("PenRqstId", lStrPenRqstId);
				inputMap.put("PenCode", lStrPnsnrCode);
				inputMap.put("ArrearType", lStrArrPension);
				inputMap.put("EffFrom", lStrDate);
				inputMap.put("PayFrom", lStrBillCrtMonth);
				inputMap.put("DiffAmt", Math.round(lArrearAmt));

				lObjArrearVO = new TrnPensionArrearDtls();
				lObjArrearVO = insertArrearDtls(inputMap);
				lLstInsertArrears.add(lObjArrearVO);
			}

			// for adding arrears of earlier month
			lArrearAmt = lArrearAmt + lPendingArrear;

			/*
			 * // Net Pension Amount Calculation. lNetPensionAmt =
			 * lBasicPensionAmt - lPensionCutAmt; // NetPension = Basic -
			 * PensionCut; lNetPensionAmt = lNetPensionAmt + lDPPerAmt; //
			 * NetPension = NetPension + DPAmt; lNetPensionAmt = lNetPensionAmt
			 * + lTIPerAmt; // NetPension = NetPension + TIAmt; lNetPensionAmt =
			 * lNetPensionAmt + lMAAmt; // NetPension = NetPension + Medical
			 * Allowance; lNetPensionAmt = lNetPensionAmt - lITCutAmt; //
			 * NetPension = NetPension - IT Cut; lNetPensionAmt = lNetPensionAmt
			 * - lSpecialCutAmt; // NetPension = NetPension - Special Cut;
			 * lNetPensionAmt = lNetPensionAmt + lOtherBenefits; // NetPension =
			 * NetPension + OtherBenefits; lNetPensionAmt = lNetPensionAmt +
			 * OMR; // NetPension = NetPension + OMR; lNetPensionAmt =
			 * lNetPensionAmt - lRecoveryAmt; // NetPension = NetPension -
			 * Recovery / Deduction; lNetPensionAmt = lNetPensionAmt -
			 * lCVPMonthlyAmt; // NetPension = NetPension - CVPMonthlyAmt ;
			 * lNetPensionAmt = lNetPensionAmt + lArrearAmt; // NetPension =
			 * NetPension + ArrearAmt ; lNetPensionAmt = lNetPensionAmt +
			 * lPerPension; // NetPension = NetPension + PersonalPension ;
			 * lNetPensionAmt = lNetPensionAmt + lIR; // NetPension = NetPension
			 * + IRAmt ; if("Monthly".equalsIgnoreCase(lStrBillType)) {
			 * if(lStrScheme.equals(lStrMoneyOrder)){ lMOComm = lNetPensionAmt
			 * 0.05; } lNetPensionAmt = lNetPensionAmt - lMOComm;
			 * //gLogger.info(
			 * "lNetPensionAmt for the month of"+lStrDate+"is:"+lNetPensionAmt);
			 * } lReducedPen = lBasicPensionAmt - lPensionCutAmt + lDPPerAmt -
			 * lCVPMonthlyAmt; allnAf60 = lReducedPen - allnBf56 - allnAf56;
			 */

			// Rounding a Double at 2 decimal point
			lBasicPensionAmt = Double.valueOf(lDblFrmt.format(lBasicPensionAmt));
			lDPPerAmt = Double.valueOf(lDblFrmt.format(lDPPerAmt));
			lTIPerAmt = Double.valueOf(lDblFrmt.format(lTIPerAmt));
			lIR = Double.valueOf(lDblFrmt.format(lIR));

			// rounding off values
			Long BasicPensionAmt = Math.round(lBasicPensionAmt);
			Long PensionCutAmt = Math.round(lPensionCutAmt);
			Long MAAmt = Math.round(lMAAmt);
			Long CVPMonthlyAmt = Math.round(lCVPMonthlyAmt);
			Long ITCutAmt = Math.round(lITCutAmt);
			Long SpecialCutAmt = Math.round(lSpecialCutAmt);
			Long OtherBenefits = Math.round(lOtherBenefits);
			Long OMR = Math.round(lOMR);
			Long RecoveryAmt = Math.round(lRecoveryAmt);
			Long ArrearAmt = Math.round(lArrearAmt);
			Long PerPension = Math.round(lPerPension);
			Long IR = Math.round(lIR);
			Long NetPensionAmt = 0L;
			Long DPPerAmt = Math.round(lDPPerAmt);
			BigDecimal TIPerAmt = BigDecimal.ZERO;
			if (inputMap.containsKey("fromAdmin") && inputMap.get("fromAdmin").toString().equals("Y")) {

				TIPerAmt = new BigDecimal(lTIPerAmt.toString());
				// TIPerAmt = Long.parseLong((new
				// BigDecimal(lTIPerAmt.toString())).toString());
			} else {
				if ("P".equals(inputMap.get("ROP_2006"))) {
					TIPerAmt = new BigDecimal(Math.ceil(lTIPerAmt));
				} else {
					TIPerAmt = new BigDecimal(Math.round(lTIPerAmt));
				}
			}

			Long ADPAmt = Math.round(lADPAmt);
			Long MOComm = 0L;

			Double lDBMoComm = 0D;
			Double lDbNetPensionAmnt = 0D;

			// Net Pension Amount Calculation.
			NetPensionAmt = BasicPensionAmt - PensionCutAmt; // NetPension =
			// Basic -
			// PensionCut;
			NetPensionAmt = NetPensionAmt + DPPerAmt; // NetPension = NetPension
			// + DPAmt;
			NetPensionAmt = NetPensionAmt + ADPAmt; // NetPension = NetPension +
			// ADPAmt;
			NetPensionAmt = NetPensionAmt + TIPerAmt.longValue(); // NetPension
			// =
			// NetPension
			// + TIAmt;
			NetPensionAmt = NetPensionAmt + MAAmt; // NetPension = NetPension +
			// Medical Allowance;
			NetPensionAmt = NetPensionAmt - CVPMonthlyAmt; // NetPension =
			// NetPension -
			// CVPMonthlyAmt ;
			NetPensionAmt = NetPensionAmt - ITCutAmt; // NetPension = NetPension
			// - IT Cut;
			NetPensionAmt = NetPensionAmt - SpecialCutAmt; // NetPension =
			// NetPension -
			// Special Cut;
			NetPensionAmt = NetPensionAmt + OtherBenefits; // NetPension =
			// NetPension +
			// OtherBenefits;
			NetPensionAmt = NetPensionAmt + OMR; // NetPension = NetPension +
			// OMR;
			NetPensionAmt = NetPensionAmt - RecoveryAmt; // NetPension =
			// NetPension -
			// Recovery /
			// Deduction;
			NetPensionAmt = NetPensionAmt + ArrearAmt; // NetPension =
			// NetPension +
			// ArrearAmt ;
			NetPensionAmt = NetPensionAmt + PerPension; // NetPension =
			// NetPension +
			// PersonalPension ;
			NetPensionAmt = NetPensionAmt + IR; // NetPension = NetPension +
			// IRAmt ;

			lDbNetPensionAmnt = Double.valueOf(NetPensionAmt);

			if (lStrMoneyOrder.equals(lStrScheme)) {
				lDBMoComm = lDbNetPensionAmnt * 5 / 100;
				lDBMoComm = Math.ceil(lDBMoComm);
				MOComm = lDBMoComm.longValue();

				if ("P".equals(inputMap.get("ROP_2006"))) {
					if (BasicPensionAmt <= 3500) {
						NetPensionAmt = NetPensionAmt + MOComm;
					}
				} else if (BasicPensionAmt <= 2500) {
					NetPensionAmt = NetPensionAmt + MOComm;
				}
			}

			Long ReducedPen = BasicPensionAmt - PensionCutAmt + DPPerAmt - CVPMonthlyAmt;
			Long allnBf56 = Math.round(lAllnBf56);
			Long allnAf56 = Math.round(lAllnAf56);
			Long allnAf60 = ReducedPen - allnBf56 - allnAf56;

			if ("Y".equals(lStrFPFlag) && lStrBillType.equalsIgnoreCase("Monthly")) {
				if (fpMemberList != null && !fpMemberList.isEmpty()) {
					for (int y = 0; y < (fpMemberList.size()); y += 3) {
						lStrPnsnerName = fpMemberList.get(y + 0).toString();
						Long prcnt = Long.parseLong(fpMemberList.get(y + 1).toString()) / 100;
						/*
						 * if(fpMemberList.get(y+2) != null) { lStrAccNo =
						 * fpMemberList.get(y+2).toString(); } else { lStrAccNo
						 * = ""; }
						 */

						lObjMonthlyPensionVO = new MonthlyPensionBillVO();
						Long TIArr = Math.round(lTIArr);
						Long TmpOthArr = ArrearAmt - TIArr;

						if (BasicPensionAmt > 0) {
							inputMap.put("MnthPpoNo", lStrPPONo);
							inputMap.put("MnthPnsnrCode", lStrPnsnrCode);
							inputMap.put("MnthPensionerName", lStrPnsnerName);
							inputMap.put("MnthAccountNo", lStrAccNo);
							inputMap.put("MnthBasicPensionAmount", BasicPensionAmt * prcnt);
							inputMap.put("MnthDpPercent", lDPPer.toString().length() != 0 ? lDPPer.toString() : "0");
							inputMap.put("MnthTiPercent", lTIPer.toString().length() != 0 ? lTIPer.toString() : "0");
							inputMap.put("MnthDpPercentAmount", DPPerAmt * prcnt);
							inputMap.put("MnthTiPercentAmount", TIPerAmt.longValue() * prcnt);
							inputMap.put("MnthADPAmount", ADPAmt * prcnt);
							inputMap.put("MnthMedicalAllowenceAmount", MAAmt * prcnt);
							inputMap.put("MnthCvpMonthlyAmount", CVPMonthlyAmt * prcnt);
							inputMap.put("MnthTIArrearsAmount", TIArr * prcnt);
							inputMap.put("MnthOtherArrearsAmount", TmpOthArr * prcnt);
							inputMap.put("MnthItCutAmount", ITCutAmt * prcnt);
							inputMap.put("MnthSpecialCutAmount", SpecialCutAmt * prcnt);
							inputMap.put("MnthOtherBenefit", OtherBenefits * prcnt);
							inputMap.put("MnthOMR", OMR * prcnt);
							inputMap.put("MnthPensionCutAmount", PensionCutAmt * prcnt);
							inputMap.put("MnthRecoveryAmount", RecoveryAmt * prcnt);
							inputMap.put("MnthNetPensionAmount", NetPensionAmt * prcnt);
							inputMap.put("MnthAllnBf56", allnBf56 * prcnt);
							inputMap.put("MnthAllnAf56", allnAf56 * prcnt);
							inputMap.put("MnthAllnAf60", allnAf60 * prcnt);
							inputMap.put("MnthPersonalPension", PerPension * prcnt);
							inputMap.put("MnthIRAmount", IR * prcnt);
							inputMap.put("MnthMOComm", MOComm * prcnt);
							inputMap.put("MnthYYYYMM", lIntDate);
							inputMap.put("MnthHeadcode", lStrHeadcode);
							inputMap.put("MnthFromPayDate", lFromPayDate);
							inputMap.put("MnthToPayDate", lToPayDate);

							lObjMonthlyPensionVO = insertMonthlyDtls(inputMap);
							lLstMonthlyPensionBillVO.add(lObjMonthlyPensionVO);
						}
					}
				}
			} else if ("Y".equals(lStrFPFlag) && lStrBillType.equalsIgnoreCase("PENSION")) {
				lObjMonthlyPensionVO = new MonthlyPensionBillVO();
				Long TIArr = Math.round(lTIArr);
				Long TmpOthArr = ArrearAmt - TIArr;

				if (BasicPensionAmt > 0) {
					inputMap.put("MnthPpoNo", lStrPPONo);
					inputMap.put("MnthPnsnrCode", lStrPnsnrCode);
					inputMap.put("MnthPensionerName", lStrPnsnerName);
					inputMap.put("MnthAccountNo", lStrAccNo);
					inputMap.put("MnthBasicPensionAmount", BasicPensionAmt);
					inputMap.put("MnthDpPercent", lDPPer.toString().length() != 0 ? lDPPer.toString() : "0");
					inputMap.put("MnthTiPercent", lTIPer.toString().length() != 0 ? lTIPer.toString() : "0");
					inputMap.put("MnthDpPercentAmount", DPPerAmt);
					inputMap.put("MnthTiPercentAmount", TIPerAmt);
					inputMap.put("MnthADPAmount", ADPAmt);
					inputMap.put("MnthMedicalAllowenceAmount", MAAmt);
					inputMap.put("MnthCvpMonthlyAmount", CVPMonthlyAmt);
					inputMap.put("MnthTIArrearsAmount", TIArr);
					inputMap.put("MnthOtherArrearsAmount", TmpOthArr);
					inputMap.put("MnthItCutAmount", ITCutAmt);
					inputMap.put("MnthSpecialCutAmount", SpecialCutAmt);
					inputMap.put("MnthOtherBenefit", OtherBenefits);
					inputMap.put("MnthOMR", OMR);
					inputMap.put("MnthPensionCutAmount", PensionCutAmt);
					inputMap.put("MnthRecoveryAmount", RecoveryAmt);
					inputMap.put("MnthNetPensionAmount", NetPensionAmt);
					inputMap.put("MnthAllnBf56", allnBf56);
					inputMap.put("MnthAllnAf56", allnAf56);
					inputMap.put("MnthAllnAf60", allnAf60);
					inputMap.put("MnthPersonalPension", PerPension);
					inputMap.put("MnthIRAmount", IR);
					inputMap.put("MnthMOComm", MOComm);
					inputMap.put("MnthYYYYMM", lIntDate);
					inputMap.put("MnthHeadcode", lStrHeadcode);
					inputMap.put("MnthFromPayDate", lFromPayDate);
					inputMap.put("MnthToPayDate", lToPayDate);

					if (fpMemberList != null && !fpMemberList.isEmpty()) {
						lObjMonthlyPensionVO = insertMonthlyDtls(inputMap);
						lLstMonthlyPensionBillVO.add(lObjMonthlyPensionVO);
					} else if (lIntDate.intValue() == lDODyyyyMM.intValue()) {
						lObjMonthlyPensionVO = insertMonthlyDtls(inputMap);
						lLstMonthlyPensionBillVO.add(lObjMonthlyPensionVO);
					} else if (lIntDate.intValue() >= lDODyyyyMM.intValue()) {
						// lObjMonthlyPensionVO = insertMonthlyDtls(inputMap);
						lLstMonthlyPensionBillVO.add(lObjMonthlyPensionVO);
					}
				}
			} else {
				lObjMonthlyPensionVO = new MonthlyPensionBillVO();
				Long TIArr = Math.round(lTIArr);
				Long TmpOthArr = ArrearAmt - TIArr;

				if (BasicPensionAmt > 0) {
					inputMap.put("MnthPpoNo", lStrPPONo);
					inputMap.put("MnthPnsnrCode", lStrPnsnrCode);
					inputMap.put("MnthPensionerName", lStrPnsnerName);
					inputMap.put("MnthAccountNo", lStrAccNo);
					inputMap.put("MnthBasicPensionAmount", BasicPensionAmt);
					inputMap.put("MnthDpPercent", lDPPer.toString().length() != 0 ? lDPPer.toString() : "0");
					inputMap.put("MnthTiPercent", lTIPer.toString().length() != 0 ? lTIPer.toString() : "0");
					inputMap.put("MnthDpPercentAmount", DPPerAmt);
					inputMap.put("MnthTiPercentAmount", TIPerAmt);
					inputMap.put("MnthADPAmount", ADPAmt);
					inputMap.put("MnthMedicalAllowenceAmount", MAAmt);
					inputMap.put("MnthCvpMonthlyAmount", CVPMonthlyAmt);
					inputMap.put("MnthTIArrearsAmount", TIArr);
					inputMap.put("MnthOtherArrearsAmount", TmpOthArr);
					inputMap.put("MnthItCutAmount", ITCutAmt);
					inputMap.put("MnthSpecialCutAmount", SpecialCutAmt);
					inputMap.put("MnthOtherBenefit", OtherBenefits);
					inputMap.put("MnthOMR", OMR);
					inputMap.put("MnthPensionCutAmount", PensionCutAmt);
					inputMap.put("MnthRecoveryAmount", RecoveryAmt);
					inputMap.put("MnthNetPensionAmount", NetPensionAmt);
					inputMap.put("MnthAllnBf56", allnBf56);
					inputMap.put("MnthAllnAf56", allnAf56);
					inputMap.put("MnthAllnAf60", allnAf60);
					inputMap.put("MnthPersonalPension", PerPension);
					inputMap.put("MnthIRAmount", IR);
					inputMap.put("MnthMOComm", MOComm);
					inputMap.put("MnthYYYYMM", lIntDate);
					inputMap.put("MnthHeadcode", lStrHeadcode);
					inputMap.put("MnthFromPayDate", lFromPayDate);
					inputMap.put("MnthToPayDate", lToPayDate);

					lObjMonthlyPensionVO = insertMonthlyDtls(inputMap);
					lLstMonthlyPensionBillVO.add(lObjMonthlyPensionVO);
				}
			}

			if (inputMap.containsKey("TotalMntAdPAmt")) {
				inputMap.remove("TotalMntAdPAmt");
			}

			// inputMap.put("ArrComputeFlag", lArrComputeFlag);
			inputMap.put("lLstMonthlyPensionBillVO", lLstMonthlyPensionBillVO);

			inputMap.put("lLstInsertArrears", lLstInsertArrears);
		} catch (Exception e) {
			gLogger.error(" Error is : Some data are not available ");
			throw (e);
		}
		return inputMap;
	}

	private List getDividedBasic(Double OrigBasicAmt, Double dpPer) throws Exception {

		List<Double> arrData = new ArrayList<Double>();
		Double lFinalBasic = 0D;
		Double lDPAmt = 0D;
		try {
			lFinalBasic = (OrigBasicAmt * 100) / (100 + dpPer);
			lDPAmt = OrigBasicAmt - lFinalBasic;

			arrData.add(lFinalBasic);
			arrData.add(lDPAmt);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return arrData;
	}

	public List getDatePlus7yrs(Date lDateOfDeath, Date argsFP1Date, Date argsFP2Date) throws Exception {

		List<Date> arrFP = new ArrayList<Date>();
		Date lFP1Date = argsFP1Date;
		Date lFP2Date = argsFP2Date;
		Date lTEMPFPDate = null;

		try {
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(lDateOfDeath);

			/*
			 * String DATE_FORMAT = "yyyy-MM-dd"; java.text.SimpleDateFormat sdf
			 * = new java.text.SimpleDateFormat(DATE_FORMAT);
			 */
			Calendar cal = Calendar.getInstance();
			cal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

			cal.add(Calendar.YEAR, 7);
			// cal.add(Calendar.DATE, -1);

			// if(lTEMPFPDate.before(lFP1Date)){
			if (lFP1Date != null && lFP2Date != null) {
				if (cal.getTimeInMillis() < lFP1Date.getTime()) {
					java.sql.Date jsqlD = new java.sql.Date(cal.getTime().getTime());
					lFP1Date = jsqlD;
					cal.add(Calendar.DATE, 1);
					java.sql.Date jsqlD2 = new java.sql.Date(cal.getTime().getTime());
					lFP2Date = jsqlD2;
				}
			} else {
				java.sql.Date jsqlD = new java.sql.Date(cal.getTime().getTime());
				lFP1Date = jsqlD;
				cal.add(Calendar.DATE, 1);
				java.sql.Date jsqlD2 = new java.sql.Date(cal.getTime().getTime());
				lFP2Date = jsqlD2;
			}
			arrFP.add(lFP1Date);
			arrFP.add(lFP2Date);
		} catch (Exception e) {
			gLogger.error(" Error is : FP1 or FP2 Date may be null ");
			throw (e);
		}
		return arrFP;
	}

	public List getDatePlus10yrs(Date lDateOfDeath, Date argsFP1Date, Date argsFP2Date) throws Exception {

		List<Date> arrFP = new ArrayList<Date>();
		Date lFP1Date = argsFP1Date;
		Date lFP2Date = argsFP2Date;
		Date lTEMPFPDate = null;

		try {
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(lDateOfDeath);

			/*
			 * String DATE_FORMAT = "yyyy-MM-dd"; java.text.SimpleDateFormat sdf
			 * = new java.text.SimpleDateFormat(DATE_FORMAT);
			 */
			Calendar cal = Calendar.getInstance();
			cal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

			cal.add(Calendar.YEAR, 10);
			// cal.add(Calendar.DATE, -1);

			// if(lTEMPFPDate.before(lFP1Date)){
			if (lFP1Date != null && lFP2Date != null) {
				if (cal.getTimeInMillis() < lFP1Date.getTime()) {
					java.sql.Date jsqlD = new java.sql.Date(cal.getTime().getTime());
					lFP1Date = jsqlD;
					cal.add(Calendar.DATE, 1);
					java.sql.Date jsqlD2 = new java.sql.Date(cal.getTime().getTime());
					lFP2Date = jsqlD2;
				}
			} else {
				java.sql.Date jsqlD = new java.sql.Date(cal.getTime().getTime());
				lFP1Date = jsqlD;
				cal.add(Calendar.DATE, 1);
				java.sql.Date jsqlD2 = new java.sql.Date(cal.getTime().getTime());
				lFP2Date = jsqlD2;
			}
			arrFP.add(lFP1Date);
			arrFP.add(lFP2Date);
		} catch (Exception e) {
			gLogger.error(" Error is : FP1 or FP2 Date may be null ");
			throw (e);
		}
		return arrFP;
	}

	/*
	 * public List getFpMemberList(List<MstPensionerFamilyDtls> lLstFPmembers)
	 * throws Exception { List fpMemberList = new ArrayList(); Double salary =
	 * 0D; try { for(int x=0; x<lLstFPmembers.size(); x++) {
	 * if(lLstFPmembers.get(x) != null) { MstPensionerFamilyDtls
	 * lObjMstPensionerFamilyDtlsVO = (MstPensionerFamilyDtls)
	 * lLstFPmembers.get(x); if(lObjMstPensionerFamilyDtlsVO.getDateOfDeath() ==
	 * null && lObjMstPensionerFamilyDtlsVO.getPercentage() != 0) {
	 * if("Spouse".equals(lObjMstPensionerFamilyDtlsVO.getRelationship()) ||
	 * "Mother".equals(lObjMstPensionerFamilyDtlsVO.getRelationship()) ||
	 * "Father".equals(lObjMstPensionerFamilyDtlsVO.getRelationship())) {
	 * fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getName());
	 * fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getPercentage());
	 * fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getAccntNo()); } else {
	 * if("N".equals(lObjMstPensionerFamilyDtlsVO.getMarriedFlag())) {
	 * if(lObjMstPensionerFamilyDtlsVO.getSalary() != null) { salary =
	 * Double.parseDouble(lObjMstPensionerFamilyDtlsVO.getSalary().toString());
	 * } else { salary = 0.00; } if(salary <= 2550) {
	 * if("N".equals(lObjMstPensionerFamilyDtlsVO.getMajorFlag())) {
	 * if(lObjMstPensionerFamilyDtlsVO.getGuardianName() != null) {
	 * fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getGuardianName()); } else
	 * { fpMemberList.add(" "); }
	 * fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getPercentage());
	 * fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getAccntNo()); } else {
	 * if("M".equals(lObjMstPensionerFamilyDtlsVO.getHandicapeFlag())) {
	 * if(lObjMstPensionerFamilyDtlsVO.getGuardianName() != null) {
	 * fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getGuardianName()); } else
	 * { fpMemberList.add("
	 * "); } } else { fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getName()); } fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getPercentage()); fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getAccntNo()); } } } } } } } } catch (Exception e){ gLogger.error("
	 * Error is :: " + e,e); throw(e); } return fpMemberList; }
	 */

	public List getFpMemberList(MstPensionerFamilyDtls lObjMstPensionerFamilyDtlsVO) throws Exception {

		List fpMemberList = new ArrayList();
		Double salary = 0D;
		char lflag = 'Y';
		try {
			if (lObjMstPensionerFamilyDtlsVO != null) {
				if (!"Son".equals(lObjMstPensionerFamilyDtlsVO.getRelationship()) && !"Daughter".equals(lObjMstPensionerFamilyDtlsVO.getRelationship())) {
					fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getName());
					fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getPercentage());
					fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getAccntNo());
				} else {
					if ("N".equals(lObjMstPensionerFamilyDtlsVO.getMarriedFlag())) {

						Date fpDOB = lObjMstPensionerFamilyDtlsVO.getDateOfBirth();
						if (fpDOB != null) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(fpDOB);
							cal.add(Calendar.YEAR, 25);
							Date dtCurrDt = DBUtility.getCurrentDateFromDB();
							if (dtCurrDt.after(cal.getTime())) {
								lflag = 'N';
							}

						}
						// if ('Y' == lflag) {
						if (lObjMstPensionerFamilyDtlsVO.getSalary() != null) {
							salary = Double.parseDouble(lObjMstPensionerFamilyDtlsVO.getSalary().toString());
						} else {
							salary = 0.00;
						}

						if (salary <= 2550) {
							if ("N".equals(lObjMstPensionerFamilyDtlsVO.getMajorFlag())) {
								if (lObjMstPensionerFamilyDtlsVO.getGuardianName() != null) {
									fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getGuardianName());
								} else {
									fpMemberList.add(" ");
								}
								fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getPercentage());
								fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getAccntNo());
							} else {
								if ("Y".equals(lObjMstPensionerFamilyDtlsVO.getHandicapeFlag())) {
									if (lObjMstPensionerFamilyDtlsVO.getGuardianName() != null) {
										fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getGuardianName());
									} else {
										fpMemberList.add(" ");
									}
								} else {
									fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getName());
								}
								fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getPercentage());
								fpMemberList.add(lObjMstPensionerFamilyDtlsVO.getAccntNo());
							}
						}
						// }
					}
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return fpMemberList;
	}

	public MonthlyPensionBillVO insertMonthlyDtls(Map inputMap) throws Exception {

		MonthlyPensionBillVO lObjMonthlyPensionVO = new MonthlyPensionBillVO();

		try {
			lObjMonthlyPensionVO = new MonthlyPensionBillVO();

			lObjMonthlyPensionVO.setPpoNo(inputMap.get("MnthPpoNo").toString());
			lObjMonthlyPensionVO.setPensionerCode(inputMap.get("MnthPnsnrCode").toString());
			if (inputMap.get("MnthPensionerName") != null) {
				lObjMonthlyPensionVO.setPensionerName(inputMap.get("MnthPensionerName").toString());
			} else {
				lObjMonthlyPensionVO.setPensionerName("");
			}

			lObjMonthlyPensionVO.setAccountNo(inputMap.get("MnthAccountNo").toString());
			lObjMonthlyPensionVO.setBasicPensionAmount(new BigDecimal(inputMap.get("MnthBasicPensionAmount").toString()));
			lObjMonthlyPensionVO.setDpPercent(new BigDecimal(inputMap.get("MnthDpPercent").toString()));
			lObjMonthlyPensionVO.setTiPercent(new BigDecimal(inputMap.get("MnthTiPercent").toString()));
			lObjMonthlyPensionVO.setAdpAmount(new BigDecimal(inputMap.get("MnthADPAmount").toString()));
			lObjMonthlyPensionVO.setDpPercentAmount(new BigDecimal(inputMap.get("MnthDpPercentAmount").toString()));
			lObjMonthlyPensionVO.setTiPercentAmount(new BigDecimal(inputMap.get("MnthTiPercentAmount").toString()));
			lObjMonthlyPensionVO.setMedicalAllowenceAmount(new BigDecimal(inputMap.get("MnthMedicalAllowenceAmount").toString()));
			lObjMonthlyPensionVO.setCvpMonthlyAmount(new BigDecimal(inputMap.get("MnthCvpMonthlyAmount").toString()));
			lObjMonthlyPensionVO.setTIArrearsAmount(new BigDecimal(inputMap.get("MnthTIArrearsAmount").toString()));
			lObjMonthlyPensionVO.setOtherArrearsAmount(new BigDecimal(inputMap.get("MnthOtherArrearsAmount").toString()));
			lObjMonthlyPensionVO.setItCutAmount(new BigDecimal(inputMap.get("MnthItCutAmount").toString()));
			lObjMonthlyPensionVO.setSpecialCutAmount(new BigDecimal(inputMap.get("MnthSpecialCutAmount").toString()));
			lObjMonthlyPensionVO.setOtherBenefit(new BigDecimal(inputMap.get("MnthOtherBenefit").toString()));
			lObjMonthlyPensionVO.setOMR(new BigDecimal(inputMap.get("MnthOMR").toString()));
			lObjMonthlyPensionVO.setPensionCutAmount(new BigDecimal(inputMap.get("MnthPensionCutAmount").toString()));
			lObjMonthlyPensionVO.setRecoveryAmount(new BigDecimal(inputMap.get("MnthRecoveryAmount").toString()));
			lObjMonthlyPensionVO.setNetPensionAmount(new BigDecimal(inputMap.get("MnthNetPensionAmount").toString()));
			lObjMonthlyPensionVO.setAllnBf11156(new BigDecimal(inputMap.get("MnthAllnBf56").toString()));
			lObjMonthlyPensionVO.setAllnAf11156(new BigDecimal(inputMap.get("MnthAllnAf56").toString()));
			lObjMonthlyPensionVO.setAllnAf10560(new BigDecimal(inputMap.get("MnthAllnAf60").toString()));
			lObjMonthlyPensionVO.setPersonalPension(new BigDecimal(inputMap.get("MnthPersonalPension").toString()));
			lObjMonthlyPensionVO.setIr(new BigDecimal(inputMap.get("MnthIRAmount").toString()));
			lObjMonthlyPensionVO.setMOComm(new BigDecimal(inputMap.get("MnthMOComm").toString()));
			lObjMonthlyPensionVO.setForMonth(Integer.valueOf(inputMap.get("MnthYYYYMM").toString()));
			lObjMonthlyPensionVO.setHeadCode(new BigDecimal(inputMap.get("MnthHeadcode").toString()));
			lObjMonthlyPensionVO.setFromDate((Date) inputMap.get("MnthFromPayDate"));
			lObjMonthlyPensionVO.setToDate((Date) inputMap.get("MnthToPayDate"));
			lObjMonthlyPensionVO.setPageNo(inputMap.get("MnthPageNo") == null || inputMap.get("MnthPageNo").toString().trim().equals("") ? "0" : inputMap.get("MnthPageNo").toString());
			lObjMonthlyPensionVO.setLedgerNo(inputMap.get("MnthLedNo") == null || inputMap.get("MnthLedNo").toString().trim().equals("") ? "0" : inputMap.get("MnthLedNo").toString());
			if (inputMap.containsKey("brnchCode")) {
				lObjMonthlyPensionVO.setBranchCode(inputMap.get("brnchCode").toString());
			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return lObjMonthlyPensionVO;
	}

	private TrnPensionArrearDtls insertArrearDtls(Map inputMap) throws Exception {

		TrnPensionArrearDtls lObjArrearDtlsVo = new TrnPensionArrearDtls();

		try {
			lObjArrearDtlsVo.setPensionRequestId((Long) inputMap.get("PenRqstId"));
			lObjArrearDtlsVo.setPensionerCode(inputMap.get("PenCode").toString());
			lObjArrearDtlsVo.setArrearFieldType(inputMap.get("ArrearType").toString());
			lObjArrearDtlsVo.setEffectedFromYyyymm(Integer.parseInt(inputMap.get("EffFrom").toString()));
			lObjArrearDtlsVo.setEffectedToYyyymm(Integer.parseInt(inputMap.get("EffFrom").toString()));
			if (inputMap.get("PayFrom") != null) {
				lObjArrearDtlsVo.setPaymentFromYyyymm(Integer.parseInt(inputMap.get("PayFrom").toString()));
				lObjArrearDtlsVo.setPaymentToYyyymm(Integer.parseInt(inputMap.get("PayFrom").toString()));
			}
			lObjArrearDtlsVo.setDifferenceAmount(new BigDecimal(inputMap.get("DiffAmt").toString()));
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return lObjArrearDtlsVo;
	}

	/**
	 * By this Service you Getting a ROP Basic, Dp Amt, TI Amt, ATI Amt, MA Amt,
	 * IR Amt according to HeadCode and for duration .
	 * 
	 * @param inputMap
	 * @return {@link MonthlyPensionBillVO}
	 * @throws Exception
	 */
	public MonthlyPensionBillVO getMasterRatesForMonth(Map<String, Object> inputMap) throws Exception {

		MonthlyPensionBillVO lMonthlyPensionBillVO = new MonthlyPensionBillVO();

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSmplDateFrm = new SimpleDateFormat("dd/MM/yyyy");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		// Double lOldDBlBasicAmt = 0D;
		// Double lOldBasicAmt = 0D;
		Double lNewBasicAmt = 0D;
		Double lPensionCutAmt = 0D;
		Double lTrnBasicAmt = 0D;
		Double lTrnPensionCutAmt = 0D;
		Double lTrnDPAmt = 0D;
		Double lDPAmt = 0D;
		Double lTIAmt = 0D;
		// Double lATIAmt = 0D;
		Double lIRAmt = 0D;
		Double lMAAmt = 0D;

		Double lDPPer = 0D;
		Double lTIPer = 0D;
		Double lZeroAmt = 0D;

		Double lOthMAArrAmt = 0D;
		Double lOthIRArrAmt = 0D;
		Double lOthDPArrAmt = 0D;
		Double lOthTIArrAmt = 0D;
		// Double lOthSPArrAmt = 0D;

		String lStrIsAlive = (String) inputMap.get("IsAlive");
		String lStrIsROPApplied = null;
		String lStrROP_1986 = null;
		String lStrROP_1996 = null;
		String lStrROP_2006 = null;
		String lStrIsSpecialCase = null;
		String lStrROPPay1986 = null;
		String lStrROPPay1996 = null;
		String lStrROPPay2006 = null;

		// String lStrPayTI = null;
		// String lStrPaySTI = null;
		// String lStrPayDP = null;
		String lStrPayMA = null;
		// String lStrPayIR = null;

		Long lHeadCode = 0L;

		String ForMonth = inputMap.get("Date").toString();

		Date lForDate = null;
		Date lPrvForDate = null;
		String lBasicAmtType = null;
		// Date lDateOfRtmnt = null;
		Date lDateOfComm = null;

		ValidPcodeView lObjValidPcodeVO = null;
		if (inputMap.containsKey("lObjValidPcode") && inputMap.get("lObjValidPcode") != null) {
			lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");
		}
		// TrnPensionRqstHdr lObjPensionRqstHdrVO = (TrnPensionRqstHdr)
		// inputMap.get("TrnPensionRqstHdrVO");
		// MstPensionerHdr lObjMstPensionerHdrVO = (MstPensionerHdr)
		// inputMap.get("MstPensionerHdrVO");
		NewPensionBillDAOImpl lPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());
		RltPensionHeadcodeRate lPensionHeadcodeRateVO = null;
		RltPensionHeadcodeRate ltPensionHeadcodeRateVO = null;
		RltPensionHeadcodeSpecial lPensionHeadcodeSpecialVO = null;
		RltPensionRevisedPayment lObjRevisedPaymentVO = null;
		RltPensionRevisedPayment ltObjRevisedPaymentVO = null;
		// List<RltPensionRevisedPayment> lRevisedPaymentLst = null;

		List<TrnPensionArrearDtls> lTrnPaymentLst = null;
		List<TrnPensionArrearDtls> lTrnPaymentTemp = null;
		TrnPensionArrearDtls lTrnPaymetVo = null;

		try {
			// lDateOfRtmnt = (Date) inputMap.get("DateOfRetirement");

			lDateOfComm = lObjValidPcodeVO.getCommensionDate();

			lStrPayMA = (String) inputMap.get("CurntPayMA");

			lTrnPaymentLst = (List<TrnPensionArrearDtls>) inputMap.get("TrnPaymentLst");

			lForDate = lObjSmplDateFrm.parse("01/" + ForMonth.substring(4, 6) + "/" + ForMonth.substring(0, 4));

			lHeadCode = lObjValidPcodeVO.getHeadCode();
			lStrIsROPApplied = lObjValidPcodeVO.getIsRop();
			lStrIsSpecialCase = lObjValidPcodeVO.getDpFlag();

			lStrROP_1986 = (String) inputMap.get("ROP_1986");
			lStrROP_1996 = (String) inputMap.get("ROP_1996");
			lStrROP_2006 = (String) inputMap.get("ROP_2006");

			lStrROPPay1986 = (String) inputMap.get("PayROP1986");
			lStrROPPay1996 = (String) inputMap.get("PayROP1996");
			lStrROPPay2006 = (String) inputMap.get("PayROP2006");

			// If Pensioner Alive flag is Y,then Pension Cut is calculate.
			if (lStrIsAlive != null && lStrIsAlive.equalsIgnoreCase("Y")) {
				lPensionCutAmt = (Double) inputMap.get("PensionCutAmt");
			}

			// Old Case Basic (Basic/FP1/FP2)
			// lOldDBlBasicAmt = (Double) inputMap.get("OldBasicAmt");
			// lOldBasicAmt += Math.round((Double) inputMap.get("OldBasicAmt"));

			lBasicAmtType = inputMap.get("BasicAmtType").toString();

			if (lBasicAmtType.equalsIgnoreCase("Basic")) {
				lNewBasicAmt = (Double) inputMap.get("NewPensionBasic");
			} else if (lBasicAmtType.equalsIgnoreCase("FP1")) {
				lNewBasicAmt = (Double) inputMap.get("NewFP1Basic");
			} else if (lBasicAmtType.equalsIgnoreCase("FP2")) {
				lNewBasicAmt = (Double) inputMap.get("NewFP2Basic");
			}

			if (inputMap.containsKey("fromAdmin") && inputMap.get("fromAdmin").toString().equals("Y")) {

				BigDecimal lTiDiff = new BigDecimal(inputMap.get("lDiffRate").toString());
				lMonthlyPensionBillVO.setBasicPensionAmount(new BigDecimal(lNewBasicAmt));
				lMonthlyPensionBillVO.setDpPercent(lObjValidPcodeVO.getDpPercent());
				if (lBasicAmtType.equalsIgnoreCase("Basic")) {
					lMonthlyPensionBillVO.setDpPercentAmount(new BigDecimal(lMonthlyPensionBillVO.getBasicPensionAmount().toString()).multiply(new BigDecimal(lMonthlyPensionBillVO.getDpPercent()
							.toString()).divide(new BigDecimal("100"))));
				} else {
					lMonthlyPensionBillVO.setDpPercentAmount(BigDecimal.ZERO);
				}
				lMonthlyPensionBillVO.setTiPercent(lTiDiff);
				lMonthlyPensionBillVO.setTiPercentAmount(BigDecimal.ZERO);
				lMonthlyPensionBillVO.setMedicalAllowenceAmount(BigDecimal.ZERO);
				lMonthlyPensionBillVO.setIr(BigDecimal.ZERO);
				lMonthlyPensionBillVO.setCvpMonthlyAmount(BigDecimal.ZERO);

				return lMonthlyPensionBillVO;
			}

			// Getting MA Amt For HeadCode

			if (lObjValidPcodeVO.getMedicalAllowenceAmount() != null && Double.valueOf(lObjValidPcodeVO.getMedicalAllowenceAmount().toString()) > 0) {
				lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_MA_" + lZeroAmt + "_" + lForDate);
				if (lPensionHeadcodeRateVO == null) {
					lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "MA", lZeroAmt, lForDate);
					inputMap.put(lHeadCode + "_MA_" + lZeroAmt + "_" + lForDate, lPensionHeadcodeRateVO);
				}

				if (lPensionHeadcodeRateVO != null) {
					// Getting Revised Payment Vo for Checking Transaction
					// Payment.
					lObjRevisedPaymentVO = (RltPensionRevisedPayment) inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_MA_" + ForMonth);
					if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_MA_" + ForMonth + "NULL"))) {
						lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "MA", ForMonth);
						inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_MA_" + ForMonth, lObjRevisedPaymentVO);
						if (lObjRevisedPaymentVO == null) {
							inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_MA_" + ForMonth + "NULL", "TRUE");
						}
					}

					if (lObjRevisedPaymentVO != null) // MA Transection Payment
					// Logic.
					{
						lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
						ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_MA_" + lZeroAmt + "_" + lPrvForDate);
						if (ltPensionHeadcodeRateVO == null) {
							ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "MA", lZeroAmt, lPrvForDate);
							inputMap.put(lHeadCode + "_MA_" + lZeroAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
						}

						if (ltPensionHeadcodeRateVO != null) {
							lMAAmt = Double.valueOf(ltPensionHeadcodeRateVO.getMinAmount().toString());
							if (lStrPayMA == null) {
								lOthMAArrAmt = Double.valueOf(ltPensionHeadcodeRateVO.getMinAmount().toString()) - lMAAmt;
								inputMap.put("CurntPayMA", "Y"); // Set Flag for
								// indicate
								// Payment
								// of MA
								// Done.
								inputMap.put("TrnForMonth", ForMonth);
								inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
								inputMap.put("DiffAmt", lOthMAArrAmt);
								inputMap.put("FieldType", "MA");
								if (!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) {
									lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
									lTrnPaymentLst.add(lTrnPaymetVo);
								}
							}
						}
					} else {
						lMAAmt = Double.valueOf(lPensionHeadcodeRateVO.getMinAmount().toString());
					}

				}
			}

			// HeadCode is not equals to FreedomFoghter && Navniraman.
			if (lHeadCode.longValue() != 16 && lHeadCode.longValue() != 17 && lHeadCode.longValue() != 18 && lHeadCode.longValue() != 19) {
				// / ROP is N then No Revesion Appied on Basic and TI Rate is
				// applicable.
				if (lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("N")) {
					// lNewBasicAmt = lOldDBlBasicAmt;

					lDPAmt = 0D;
					Double lNewTiAmt = 0D;
					Double lNewAdsTiAmt = 0D;

					// Calculate TI Amt For NEW Basic
					lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lForDate);
					if (lPensionHeadcodeRateVO == null) {
						lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI", lNewBasicAmt, lForDate);
						inputMap.put(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lForDate, lPensionHeadcodeRateVO);
					}

					if (lPensionHeadcodeRateVO != null) {
						lObjRevisedPaymentVO = (RltPensionRevisedPayment) inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth);
						if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL"))) {
							lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "TI", ForMonth);
							inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth, lObjRevisedPaymentVO);
							if (lObjRevisedPaymentVO == null) {
								inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL", "TRUE");
							}
						}

						if (lObjRevisedPaymentVO != null) // TI Transection
						// Payment Logic.
						{
							lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
							ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lPrvForDate);
							if (ltPensionHeadcodeRateVO == null) {
								ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI", lNewBasicAmt, lPrvForDate);
								inputMap.put(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
							}

							if (ltPensionHeadcodeRateVO != null) {
								if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
									lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
									lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
									lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
								}
								lNewTiAmt = getGreaterTIAmt(ltPensionHeadcodeRateVO, lTrnBasicAmt, lTrnPensionCutAmt);
								lNewAdsTiAmt = getGreaterTIAmt(lPensionHeadcodeRateVO, lTrnBasicAmt, lTrnPensionCutAmt);

								lOthTIArrAmt = lNewAdsTiAmt - lNewTiAmt; // TI
								// Arrear
								// For
								// TI
								// itself
								// change

								inputMap.put("CurntPayTI", "Y"); // Set Flag for
								// indicate
								// Payment
								// of MA
								// Done.
								inputMap.put("TrnForMonth", ForMonth);
								inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
								inputMap.put("DiffAmt", lOthTIArrAmt);
								inputMap.put("FieldType", "TI");
								if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthTIArrAmt > 0D) {
									lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
									lTrnPaymentLst.add(lTrnPaymetVo);
								}

							}
						} else {
							lNewTiAmt = getGreaterTIAmt(lPensionHeadcodeRateVO, lNewBasicAmt, lPensionCutAmt);
						}
					}
					lTIAmt = lNewTiAmt;
					lTIPer = 0d;

					// Getting IR Amt from Duration.
					if (lForDate != null && lForDate.after(lObjSmplDateFrm.parse("31/03/1995"))) {
						/*
						 * if(lForDate.after(lObjSmplDateFrm.parse("01/04/1995"))
						 * ) // Fixed Rs. 50 For 01/04/1995 { lIRAmt = 50.0;
						 * if(50 > ((lNewBasicAmt10)/100)) { lIRAmt += 50.0; }
						 * else { lIRAmt += Math.round((lNewBasicAmt10)/100); }
						 * }
						 */

						/*
						 * if(lForDate.before(lObjSmplDateFrm.parse("01/04/1996")
						 * )) // Fixed Rs. 50 For 01/04/1995 to 31/03/1996 {
						 */
						lIRAmt = 50.0;
						/* } */

						List<RltPensionHeadcodeRate> llPensionHeadcodeRateLst = null;

						llPensionHeadcodeRateLst = (List<RltPensionHeadcodeRate>) inputMap.get(lHeadCode + "_IR_");
						if (llPensionHeadcodeRateLst == null) {
							List<String> lLstIRApplicable = new ArrayList<String>();
							lLstIRApplicable.add("IR2");
							lLstIRApplicable.add("IR3");
							llPensionHeadcodeRateLst = lPensionBillDAO.getRateLstFromHeadcode(lHeadCode, lLstIRApplicable);
							inputMap.put(lHeadCode + "_IR_", llPensionHeadcodeRateLst);
						}

						if (llPensionHeadcodeRateLst != null) {
							/*
							 * if(lObjRevisedPaymentVO != null) // IR
							 * Transection Payment Logic. { lPrvForDate =
							 * getPrevDate
							 * (lPensionHeadcodeRateVO.getEffectiveFromDate());
							 * ltPensionHeadcodeRateVO =
							 * (RltPensionHeadcodeRate)
							 * inputMap.get(lHeadCode+"_IR_");
							 * if(ltPensionHeadcodeRateVO == null) {
							 * ltPensionHeadcodeRateLst =
							 * lPensionBillDAO.getRateLstFromHeadcode(lHeadCode,
							 * "IR"); inputMap.put(lHeadCode+"_IR_",
							 * ltPensionHeadcodeRateVO); }
							 * if(ltPensionHeadcodeRateVO != null) { Double
							 * lOldIRAmt =
							 * getGreaterIRAmt(ltPensionHeadcodeRateVO,
							 * lNewBasicAmt); Double lNewIRAmt =
							 * getGreaterIRAmt(lPensionHeadcodeRateVO,
							 * lNewBasicAmt); lOthIRArrAmt = lNewIRAmt -
							 * lOldIRAmt; lIRAmt += lOthIRArrAmt;
							 * inputMap.put("CurntPayIR","Y"); // Set Flag for
							 * indicate Payment of IR Done.
							 * inputMap.put("TrnForMonth",ForMonth);
							 * inputMap.put
							 * ("TrnPayMonth",lObjRevisedPaymentVO.getPayInMonth
							 * ()); inputMap.put("DiffAmt",lOthIRArrAmt);
							 * inputMap.put("FieldType","PENSION"); lTrnPaymetVo
							 * = insertTrnPaymentDtls(inputMap);
							 * lTrnPaymentLst.add(lTrnPaymetVo); } } else
							 */
							for (int i = 0; i < llPensionHeadcodeRateLst.size(); i++) {
								lPensionHeadcodeRateVO = null;
								lPensionHeadcodeRateVO = llPensionHeadcodeRateLst.get(i);

								if ((lForDate.equals(lPensionHeadcodeRateVO.getEffectiveFromDate()) || lForDate.after(lPensionHeadcodeRateVO.getEffectiveFromDate()))
										&& lPensionHeadcodeRateVO.getEffectiveToDate() == null) {
									for (int j = 0; j < llPensionHeadcodeRateLst.size(); j++) {
										lIRAmt += Math.round(getGreaterIRAmt(llPensionHeadcodeRateLst.get(j), lNewBasicAmt));
									}
								} else if ((lForDate.equals(lPensionHeadcodeRateVO.getEffectiveFromDate()) || lForDate.after(lPensionHeadcodeRateVO.getEffectiveFromDate()))
										&& lForDate.before(lPensionHeadcodeRateVO.getEffectiveToDate())) {
									lIRAmt += Math.round(getGreaterIRAmt(lPensionHeadcodeRateVO, lNewBasicAmt));
								}
							}
						}
					}

				}
				// ROP is Y then we check follwing All Condition one by one.
				// 1.) 1986 is (Applied / Revised) then Revised basic with 1986
				// ROP and TI Rate is applicable.
				// 2.) 1996 is (Applied / Revised) then Revised basic with 1996
				// ROP and if DP is Merged then STI Rate is applicable.
				// 3.) 1996 is (Applied / Revised) then Revised basic with 1996
				// ROP and if DP is Not Merged then RTI Rate is applicable.
				// 4.) 2006 is (Applied / Revised) then Revised basic with 2006
				// ROP and TI_06 Rate is applicable and DP is 0.
				else if (lStrIsROPApplied != null && !lStrIsROPApplied.equalsIgnoreCase("N")) {
					// For 1986
					if (lForDate != null && lForDate.before(lObjSmplDateFrm.parse("01/01/1996")) && lForDate.after(lObjSmplDateFrm.parse("31/12/1985"))) {
						Double lNewTiAmt = 0D;
						Double lNewAdsTiAmt = 0D;

						// Normal TI Master Rate
						// Calculate TI Amt For NEW Basic
						lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lForDate);
						if (lPensionHeadcodeRateVO == null) {
							lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI", lNewBasicAmt, lForDate);
							inputMap.put(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lForDate, lPensionHeadcodeRateVO);
						}

						if (lPensionHeadcodeRateVO != null) {
							lObjRevisedPaymentVO = (RltPensionRevisedPayment) inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth);
							if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL"))) {
								lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "TI", ForMonth);
								inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth, lObjRevisedPaymentVO);
								if (lObjRevisedPaymentVO == null) {
									inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL", "TRUE");
								}
							}

							if (lObjRevisedPaymentVO != null) // TI Transection
							// Payment
							// Logic.
							{
								lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
								ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lPrvForDate);
								if (ltPensionHeadcodeRateVO == null) {
									ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI", lNewBasicAmt, lPrvForDate);
									inputMap.put(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
								}

								if (ltPensionHeadcodeRateVO != null) {
									if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
										lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
										lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
										lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
									}
									lNewTiAmt = getGreaterTIAmt(ltPensionHeadcodeRateVO, lTrnBasicAmt, lTrnPensionCutAmt);
									lNewAdsTiAmt = getGreaterTIAmt(lPensionHeadcodeRateVO, lTrnBasicAmt, lTrnPensionCutAmt);

									lOthTIArrAmt = lNewAdsTiAmt - lNewTiAmt; // TI
									// Arrear
									// For
									// TI
									// itself
									// change

									inputMap.put("CurntPayTI", "Y"); // Set Flag
									// for
									// indicate
									// Payment
									// of MA
									// Done.
									inputMap.put("TrnForMonth", ForMonth);
									inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
									inputMap.put("DiffAmt", lOthTIArrAmt);
									inputMap.put("FieldType", "TI");
									if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthTIArrAmt > 0D) {
										lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
										lTrnPaymentLst.add(lTrnPaymetVo);
									}
								}
							} else {
								lNewTiAmt = getGreaterTIAmt(lPensionHeadcodeRateVO, lNewBasicAmt, lPensionCutAmt);
							}
						}

						lTIAmt = lNewTiAmt;
						lTIPer = 0d;

						// Getting IR Amt from Duration.
						if (lForDate != null && lForDate.after(lObjSmplDateFrm.parse("31/03/1995"))) {
							/*
							 * if(lForDate.after(lObjSmplDateFrm.parse("01/04/1995"
							 * ))) // Fixed Rs. 50 For 01/04/1995 { lIRAmt =
							 * 50.0; if(50 > ((lNewBasicAmt10)/100)) { lIRAmt +=
							 * 50.0; } else { lIRAmt +=
							 * Math.round((lNewBasicAmt10)/100); } }
							 */

							/*
							 * if(lForDate.before(lObjSmplDateFrm.parse("01/04/1996"
							 * ))) // Fixed Rs. 50 For 01/04/1995 to 31/03/1996
							 * {
							 */
							lIRAmt = 50.0;
							/* } */

							List<RltPensionHeadcodeRate> llPensionHeadcodeRateLst = null;

							llPensionHeadcodeRateLst = (List<RltPensionHeadcodeRate>) inputMap.get(lHeadCode + "_IR_");
							if (llPensionHeadcodeRateLst == null) {
								List<String> lLstIRApplicable = new ArrayList<String>();
								lLstIRApplicable.add("IR2");
								lLstIRApplicable.add("IR3");
								llPensionHeadcodeRateLst = lPensionBillDAO.getRateLstFromHeadcode(lHeadCode, lLstIRApplicable);
								inputMap.put(lHeadCode + "_IR_", llPensionHeadcodeRateLst);
							}

							if (llPensionHeadcodeRateLst != null) {
								/*
								 * if(lObjRevisedPaymentVO != null) // IR
								 * Transection Payment Logic. { lPrvForDate =
								 * getPrevDate
								 * (lPensionHeadcodeRateVO.getEffectiveFromDate
								 * ()); ltPensionHeadcodeRateVO =
								 * (RltPensionHeadcodeRate
								 * )inputMap.get(lHeadCode+"_IR_");
								 * if(ltPensionHeadcodeRateVO == null) {
								 * ltPensionHeadcodeRateLst =
								 * lPensionBillDAO.getRateLstFromHeadcode
								 * (lHeadCode, "IR");
								 * inputMap.put(lHeadCode+"_IR_",
								 * ltPensionHeadcodeRateVO); }
								 * if(ltPensionHeadcodeRateVO != null) { Double
								 * lOldIRAmt =
								 * getGreaterIRAmt(ltPensionHeadcodeRateVO,
								 * lNewBasicAmt); Double lNewIRAmt =
								 * getGreaterIRAmt(lPensionHeadcodeRateVO,
								 * lNewBasicAmt); lOthIRArrAmt = lNewIRAmt -
								 * lOldIRAmt; lIRAmt += lOthIRArrAmt;
								 * inputMap.put("CurntPayIR","Y"); // Set Flag
								 * for indicate Payment of IR Done.
								 * inputMap.put("TrnForMonth",ForMonth);
								 * inputMap
								 * .put("TrnPayMonth",lObjRevisedPaymentVO
								 * .getPayInMonth());
								 * inputMap.put("DiffAmt",lOthIRArrAmt);
								 * inputMap.put("FieldType","PENSION");
								 * lTrnPaymetVo =
								 * insertTrnPaymentDtls(inputMap);
								 * lTrnPaymentLst.add(lTrnPaymetVo); } } else
								 */
								for (int i = 0; i < llPensionHeadcodeRateLst.size(); i++) {
									lPensionHeadcodeRateVO = null;
									lPensionHeadcodeRateVO = llPensionHeadcodeRateLst.get(i);

									if ((lForDate.equals(lPensionHeadcodeRateVO.getEffectiveFromDate()) || lForDate.after(lPensionHeadcodeRateVO.getEffectiveFromDate()))
											&& lPensionHeadcodeRateVO.getEffectiveToDate() == null) {
										for (int j = 0; j < llPensionHeadcodeRateLst.size(); j++) {
											lIRAmt += Math.round(getGreaterIRAmt(llPensionHeadcodeRateLst.get(j), lNewBasicAmt));
										}
									} else if ((lForDate.equals(lPensionHeadcodeRateVO.getEffectiveFromDate()) || lForDate.after(lPensionHeadcodeRateVO.getEffectiveFromDate()))
											&& lForDate.before(lPensionHeadcodeRateVO.getEffectiveToDate())) {
										lIRAmt += Math.round(getGreaterIRAmt(lPensionHeadcodeRateVO, lNewBasicAmt));
									}
								}
							}
						}

					}
					// For 1996
					else if (lForDate != null && lForDate.before(lObjSmplDateFrm.parse("01/01/2006")) && lForDate.after(lObjSmplDateFrm.parse("31/12/1995"))) {
						Double lNewTIPer = 0D;
						Double lNewTiAmt = 0D;
						Double lNewAdsTiAmt = 0D;

						// If 1986 is Checked and 1996 is not checked
						if (lStrROP_1986 != null && (lStrROP_1986.equalsIgnoreCase("Y") || lStrROP_1986.equalsIgnoreCase("P")) && lStrROP_1996 == null) {
							// Normal TI Master Rate
							// Calculate TI Amt For NEW Basic
							lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lForDate);
							if (lPensionHeadcodeRateVO == null) {
								lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI", lNewBasicAmt, lForDate);
								inputMap.put(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lForDate, lPensionHeadcodeRateVO);
							}

							if (lPensionHeadcodeRateVO != null) {
								if (lObjRevisedPaymentVO != null) // TI
								// Transection
								// Payment
								// Logic.
								{
									lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
									ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lPrvForDate);
									if (ltPensionHeadcodeRateVO == null) {
										ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI", lNewBasicAmt, lPrvForDate);
										inputMap.put(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
									}

									if (ltPensionHeadcodeRateVO != null) {
										if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
											lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
											lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
											lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
										}

										lNewTiAmt = getGreaterTIAmt(ltPensionHeadcodeRateVO, lTrnBasicAmt, lTrnPensionCutAmt);
										lNewAdsTiAmt = getGreaterTIAmt(lPensionHeadcodeRateVO, lTrnBasicAmt, lTrnPensionCutAmt);

										lOthTIArrAmt = lNewAdsTiAmt - lNewTiAmt; // TI
										// Arrear
										// For
										// TI
										// itself
										// change

										inputMap.put("CurntPayTI", "Y"); // Set
										// Flag
										// for
										// indicate
										// Payment
										// of
										// MA
										// Done.
										inputMap.put("TrnForMonth", ForMonth);
										inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
										inputMap.put("DiffAmt", lOthTIArrAmt);
										inputMap.put("FieldType", "TI");
										if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthTIArrAmt > 0D) {
											lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
											lTrnPaymentLst.add(lTrnPaymetVo);
										}
									}
								} else {
									lNewTiAmt = getGreaterTIAmt(lPensionHeadcodeRateVO, lNewBasicAmt, lPensionCutAmt);
								}
							}

							lTIAmt = lNewTiAmt;
							lTIPer = 0d;

							// Getting IR Amt from Duration.
							if (lForDate != null && lForDate.after(lObjSmplDateFrm.parse("31/03/1995"))) {
								lIRAmt = 50.0;

								List<RltPensionHeadcodeRate> llPensionHeadcodeRateLst = null;

								llPensionHeadcodeRateLst = (List<RltPensionHeadcodeRate>) inputMap.get(lHeadCode + "_IR_");
								if (llPensionHeadcodeRateLst == null) {
									List<String> lLstIRApplicable = new ArrayList<String>();
									lLstIRApplicable.add("IR2");
									lLstIRApplicable.add("IR3");
									llPensionHeadcodeRateLst = lPensionBillDAO.getRateLstFromHeadcode(lHeadCode, lLstIRApplicable);
									inputMap.put(lHeadCode + "_IR_", llPensionHeadcodeRateLst);
								}

								if (llPensionHeadcodeRateLst != null) {
									for (int i = 0; i < llPensionHeadcodeRateLst.size(); i++) {
										lPensionHeadcodeRateVO = null;
										lPensionHeadcodeRateVO = llPensionHeadcodeRateLst.get(i);

										if ((lForDate.equals(lPensionHeadcodeRateVO.getEffectiveFromDate()) || lForDate.after(lPensionHeadcodeRateVO.getEffectiveFromDate()))
												&& lPensionHeadcodeRateVO.getEffectiveToDate() == null) {
											for (int j = 0; j < llPensionHeadcodeRateLst.size(); j++) {
												lIRAmt += Math.round(getGreaterIRAmt(llPensionHeadcodeRateLst.get(j), lNewBasicAmt));
											}
										} else if ((lForDate.equals(lPensionHeadcodeRateVO.getEffectiveFromDate()) || lForDate.after(lPensionHeadcodeRateVO.getEffectiveFromDate()))
												&& lForDate.before(lPensionHeadcodeRateVO.getEffectiveToDate())) {
											lIRAmt += Math.round(getGreaterIRAmt(lPensionHeadcodeRateVO, lNewBasicAmt));
										}
									}
								}
							}
						}
						// If 1986 and 1996 both are Checked
						else if (lStrROP_1986 != null && (lStrROP_1986.equalsIgnoreCase("Y") || lStrROP_1986.equalsIgnoreCase("P")) && lStrROP_1996 != null
								&& (lStrROP_1996.equalsIgnoreCase("Y") || lStrROP_1996.equalsIgnoreCase("P"))) {
							// ROP = Y and SpecialCase = Y
							if (lStrIsROPApplied != null && lStrIsSpecialCase != null && !lStrIsROPApplied.equalsIgnoreCase("N") && lStrIsSpecialCase.equalsIgnoreCase("Y")) {
								lDPAmt = 0D;
								// Getting RTI Amt from Duration.
								lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_RTI_" + lZeroAmt + "_" + lForDate);
								if (lPensionHeadcodeRateVO == null) {
									lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "RTI", lZeroAmt, lForDate);
									inputMap.put(lHeadCode + "_RTI_" + lZeroAmt + "_" + lForDate, lPensionHeadcodeRateVO);
								}

								if (lPensionHeadcodeRateVO != null) {
									// Getting Revised Payment Vo for Checking
									// Transaction Payment.
									lObjRevisedPaymentVO = (RltPensionRevisedPayment) inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth);
									if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL"))) {
										lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "TI", ForMonth);
										inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth, lObjRevisedPaymentVO);
										if (lObjRevisedPaymentVO == null) {
											inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL", "TRUE");
										}
									}

									if (lObjRevisedPaymentVO != null) // TI
									// Transection
									// Payment
									// Logic.
									{
										lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
										ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_RTI_" + lZeroAmt + "_" + lPrvForDate);
										if (ltPensionHeadcodeRateVO == null) {
											ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "RTI", lZeroAmt, lPrvForDate);
											inputMap.put(lHeadCode + "_RTI_" + lZeroAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
										}

										if (ltPensionHeadcodeRateVO != null) {
											if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
												lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
												lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
												lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
											}
											lTIPer = Double.valueOf(ltPensionHeadcodeRateVO.getRate().toString());
											lNewTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
											lNewTiAmt = ((lTrnBasicAmt - lTrnPensionCutAmt) * ((lTIPer) / 100));
											lNewAdsTiAmt = ((lTrnBasicAmt - lTrnPensionCutAmt) * ((lNewTIPer) / 100));

											lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lNewAdsTiAmt))) - Double.parseDouble(String.valueOf(Math.round(lNewTiAmt)));
											lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lOthTIArrAmt)));

											inputMap.put("CurntPayTI", "Y"); // Set
											// Flag
											// for
											// indicate
											// Payment
											// of
											// TI
											// Done.
											inputMap.put("TrnForMonth", ForMonth);
											inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
											inputMap.put("DiffAmt", lOthTIArrAmt);
											inputMap.put("FieldType", "TI");
											if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthTIArrAmt > 0D) {
												lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
												lTrnPaymentLst.add(lTrnPaymetVo);
											}
										}
									} else {
										lTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
									}
									// lNewTiAmt = ((lNewBasicAmt -
									// lPensionCutAmt) * ((lTIPer) / 100 ));
								}
							}
							// ROP = Y and SpecialCase = N
							else if (lStrIsROPApplied != null && lStrIsSpecialCase != null && !lStrIsROPApplied.equalsIgnoreCase("N") && lStrIsSpecialCase.equalsIgnoreCase("N")) {
								lDPAmt = 0D;
								if (lForDate.after(lObjSmplDateFrm.parse("31/3/2004"))) // fordate
								// >
								// 1/1/2004.
								// DP
								// Mrg.
								{
									lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_DP_" + lZeroAmt + "_" + lForDate);
									if (lPensionHeadcodeRateVO == null) {
										lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "DP", lZeroAmt, lForDate);
										inputMap.put(lHeadCode + "_DP_" + lZeroAmt + "_" + lForDate, lPensionHeadcodeRateVO);
									}

									if (lPensionHeadcodeRateVO != null) {
										// Getting Revised Payment Vo for
										// Checking Transaction Payment.
										lObjRevisedPaymentVO = (RltPensionRevisedPayment) inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_DP_" + ForMonth);
										if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_DP_" + ForMonth + "NULL"))) {
											lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "DP", ForMonth);
											inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_DP_" + ForMonth, lObjRevisedPaymentVO);
											if (lObjRevisedPaymentVO == null) {
												inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_DP_" + ForMonth + "NULL", "TRUE");
											}
										}

										if (lObjRevisedPaymentVO != null) // DP
										// Transection
										// Payment
										// Logic.
										{
											lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
											ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_DP_" + lZeroAmt + "_" + lPrvForDate);
											if (ltPensionHeadcodeRateVO == null) {
												ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "DP", lZeroAmt, lPrvForDate);
												inputMap.put(lHeadCode + "_DP_" + lZeroAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
											}

											if (ltPensionHeadcodeRateVO != null) {
												if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
													lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
													lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
													lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
												}
												lDPPer = Double.valueOf(ltPensionHeadcodeRateVO.getRate().toString());
												Double lNewDPPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
												lOthDPArrAmt = ((lTrnBasicAmt - lTrnPensionCutAmt) * ((lNewDPPer) / 100)) - lDPPer;

												inputMap.put("CurntPayDP", "Y"); // Set
												// Flag
												// for
												// indicate
												// Payment
												// of
												// DP
												// Done.
												inputMap.put("TrnForMonth", ForMonth);
												inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
												inputMap.put("DiffAmt", lOthDPArrAmt);
												inputMap.put("FieldType", "PENSION");
												if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthDPArrAmt > 0D) {
													lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
													lTrnPaymentLst.add(lTrnPaymetVo);
												}
											}
										} else {
											lDPPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
											lDPAmt += Math.round(((lNewBasicAmt - lPensionCutAmt) * ((lDPPer) / 100)));
										}
									}

									// If Pensioner is not Alive then FPAmt is
									// split with DPPer.
									if ((!lBasicAmtType.equalsIgnoreCase("Basic")) && lStrIsAlive.equalsIgnoreCase("N") && lDPPer > 0 && lDateOfComm != null) {
										// Check For Splite FPBasic with DP Per.
										if (lDateOfComm.after(lObjSmplDateFrm.parse("31/3/2004"))) {
											List lresLst = getDividedBasic(lNewBasicAmt, Double.valueOf(lDPPer.toString()));
											lNewBasicAmt = Double.valueOf(lresLst.get(0).toString());
											lDPAmt = 0D;
											lDPAmt += Math.round(Double.valueOf(lresLst.get(1).toString()));
										} else {
											lDPAmt = 0D;
											lDPAmt += Math.round(lNewBasicAmt * (lDPPer / 100));
										}
									}
								}
								// Getting STI Amt from Duration.
								lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_STI_" + lZeroAmt + "_" + lForDate);
								if (lPensionHeadcodeRateVO == null) {
									lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "STI", lZeroAmt, lForDate);
									inputMap.put(lHeadCode + "_STI_" + lZeroAmt + "_" + lForDate, lPensionHeadcodeRateVO);
								}

								if (lPensionHeadcodeRateVO != null) {
									if (inputMap.containsKey("Mst6PayAfter2006") && "Y".equals(inputMap.get("Mst6PayAfter2006")) && !(inputMap.containsKey("Mst6Pay3050_2")))

									{
										lObjRevisedPaymentVO = null;
									} else {
										// Getting Revised Payment Vo for
										// Checking Transaction Payment.
										lObjRevisedPaymentVO = (RltPensionRevisedPayment) inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth);
										if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL"))) {
											lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "TI", ForMonth);
											inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth, lObjRevisedPaymentVO);
											if (lObjRevisedPaymentVO == null) {
												inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL", "TRUE");
											}
										}
									}

									if (lObjRevisedPaymentVO != null) // TI
									// Transection
									// Payment
									// Logic.
									{
										lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
										ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_STI_" + lZeroAmt + "_" + lPrvForDate);
										if (ltPensionHeadcodeRateVO == null) {
											ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "STI", lZeroAmt, lPrvForDate);
											ltObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(ltPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "TI", null);
											if (ltObjRevisedPaymentVO != null && ltObjRevisedPaymentVO.getPayInMonth() >= lObjRevisedPaymentVO.getForPayMonth()) {
												lPrvForDate = getPrevDate(ltPensionHeadcodeRateVO.getEffectiveFromDate());
												ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_STI_" + lZeroAmt + "_" + lPrvForDate);
												if (ltPensionHeadcodeRateVO == null) {
													ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "STI", lZeroAmt, lPrvForDate);
												}
											}
											inputMap.put(lHeadCode + "_STI_" + lZeroAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
										}

										if (ltPensionHeadcodeRateVO != null) {
											if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
												lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
												lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
												lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
											}
											lTIPer = Double.valueOf(ltPensionHeadcodeRateVO.getRate().toString());
											lNewTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
											lNewTiAmt = (((lTrnBasicAmt - lTrnPensionCutAmt) + lTrnDPAmt) * ((lTIPer) / 100));
											lNewAdsTiAmt = (((lTrnBasicAmt - lTrnPensionCutAmt) + lTrnDPAmt) * ((lNewTIPer) / 100));
											lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lNewAdsTiAmt))) - Double.parseDouble(String.valueOf(Math.round(lNewTiAmt)));
											lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lOthTIArrAmt)));
											inputMap.put("CurntPayTI", "Y"); // Set
											// Flag
											// for
											// indicate
											// Payment
											// of
											// DP
											// Done.
											inputMap.put("TrnForMonth", ForMonth);
											inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
											inputMap.put("DiffAmt", lOthTIArrAmt);
											inputMap.put("FieldType", "TI");
											if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthTIArrAmt > 0D) {
												lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
												lTrnPaymentLst.add(lTrnPaymetVo);
											}
										}
									} else {
										lTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
									}
									// lNewTiAmt = (((lNewBasicAmt -
									// lPensionCutAmt)+lDPAmt) * ((lTIPer) / 100
									// ));
								}
							}

							// lTIAmt = lNewTiAmt;
						}
					}
					// For 2006
					else if (lForDate != null && lForDate.after(lObjSmplDateFrm.parse("31/12/2005"))) {
						Double lNewTIPer = 0D;
						Double lNewTiAmt = 0D;
						Double lNewAdsTiAmt = 0D;

						// If 1986 is Checked and 1996 and 2006 is not checked
						if (lStrROP_1986 != null && (lStrROP_1986.equalsIgnoreCase("Y") || lStrROP_1986.equalsIgnoreCase("P")) && lStrROP_1996 == null && lStrROP_2006 == null) {
							// Normal TI Master Rate
							// Calculate TI Amt For NEW Basic
							lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lForDate);
							if (lPensionHeadcodeRateVO == null) {
								lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI", lNewBasicAmt, lForDate);
								inputMap.put(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lForDate, lPensionHeadcodeRateVO);
							}

							if (lPensionHeadcodeRateVO != null) {
								if (lObjRevisedPaymentVO != null) // TI
								// Transection
								// Payment
								// Logic.
								{
									lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
									ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lPrvForDate);
									if (ltPensionHeadcodeRateVO == null) {
										ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI", lNewBasicAmt, lPrvForDate);
										inputMap.put(lHeadCode + "_TI_" + lNewBasicAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
									}

									if (ltPensionHeadcodeRateVO != null) {
										if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
											lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
											lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
											lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
										}

										lNewTiAmt = getGreaterTIAmt(ltPensionHeadcodeRateVO, lTrnBasicAmt, lTrnPensionCutAmt);
										lNewAdsTiAmt = getGreaterTIAmt(lPensionHeadcodeRateVO, lTrnBasicAmt, lTrnPensionCutAmt);

										lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lNewAdsTiAmt))) - Double.parseDouble(String.valueOf(Math.round(lNewTiAmt))); // TI
										// Arrear
										// For
										// TI
										// itself
										// change
										lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lOthTIArrAmt)));
										inputMap.put("CurntPayTI", "Y"); // Set
										// Flag
										// for
										// indicate
										// Payment
										// of
										// MA
										// Done.
										inputMap.put("TrnForMonth", ForMonth);
										inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
										inputMap.put("DiffAmt", lOthTIArrAmt);
										inputMap.put("FieldType", "TI");
										if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthTIArrAmt > 0D) {
											lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
											lTrnPaymentLst.add(lTrnPaymetVo);
										}
									}
								} else {
									lNewTiAmt = getGreaterTIAmt(lPensionHeadcodeRateVO, lNewBasicAmt, lPensionCutAmt);
								}
							}

							lTIAmt = lNewTiAmt;
							lTIPer = 0d;

							// Getting IR Amt from Duration.
							if (lForDate != null && lForDate.after(lObjSmplDateFrm.parse("31/03/1995"))) {
								lIRAmt = 50.0;

								List<RltPensionHeadcodeRate> llPensionHeadcodeRateLst = null;

								llPensionHeadcodeRateLst = (List<RltPensionHeadcodeRate>) inputMap.get(lHeadCode + "_IR_");
								if (llPensionHeadcodeRateLst == null) {
									List<String> lLstIRApplicable = new ArrayList<String>();
									lLstIRApplicable.add("IR2");
									lLstIRApplicable.add("IR3");
									llPensionHeadcodeRateLst = lPensionBillDAO.getRateLstFromHeadcode(lHeadCode, lLstIRApplicable);
									inputMap.put(lHeadCode + "_IR_", llPensionHeadcodeRateLst);
								}

								if (llPensionHeadcodeRateLst != null) {
									for (int i = 0; i < llPensionHeadcodeRateLst.size(); i++) {
										lPensionHeadcodeRateVO = null;
										lPensionHeadcodeRateVO = llPensionHeadcodeRateLst.get(i);

										if ((lForDate.equals(lPensionHeadcodeRateVO.getEffectiveFromDate()) || lForDate.after(lPensionHeadcodeRateVO.getEffectiveFromDate()))
												&& lPensionHeadcodeRateVO.getEffectiveToDate() == null) {
											for (int j = 0; j < llPensionHeadcodeRateLst.size(); j++) {
												lIRAmt += Math.round(getGreaterIRAmt(llPensionHeadcodeRateLst.get(j), lNewBasicAmt));
											}
										} else if ((lForDate.equals(lPensionHeadcodeRateVO.getEffectiveFromDate()) || lForDate.after(lPensionHeadcodeRateVO.getEffectiveFromDate()))
												&& lForDate.before(lPensionHeadcodeRateVO.getEffectiveToDate())) {
											lIRAmt += Math.round(getGreaterIRAmt(lPensionHeadcodeRateVO, lNewBasicAmt));
										}
									}
								}
							}
						}
						// If 1986 and 1996 both are Checked and 2006 is not
						// checked
						else if (lStrROP_1986 != null && (lStrROP_1986.equalsIgnoreCase("Y") || lStrROP_1986.equalsIgnoreCase("P")) && lStrROP_1996 != null
								&& (lStrROP_1996.equalsIgnoreCase("Y") || lStrROP_1996.equalsIgnoreCase("P")) && lStrROP_2006 == null) {
							// ROP = Y and SpecialCase = Y
							if (lStrIsROPApplied != null && lStrIsSpecialCase != null && !lStrIsROPApplied.equalsIgnoreCase("N") && lStrIsSpecialCase.equalsIgnoreCase("Y")) {
								lDPAmt = 0D;
								// Getting RTI Amt from Duration.
								lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_RTI_" + lZeroAmt + "_" + lForDate);
								if (lPensionHeadcodeRateVO == null) {
									lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "RTI", lZeroAmt, lForDate);
									inputMap.put(lHeadCode + "_RTI_" + lZeroAmt + "_" + lForDate, lPensionHeadcodeRateVO);
								}

								if (lPensionHeadcodeRateVO != null) {
									// Getting Revised Payment Vo for Checking
									// Transaction Payment.
									lObjRevisedPaymentVO = (RltPensionRevisedPayment) inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth);
									if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL"))) {
										lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "TI", ForMonth);
										inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth, lObjRevisedPaymentVO);
										if (lObjRevisedPaymentVO == null) {
											inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL", "TRUE");
										}
									}

									if (lObjRevisedPaymentVO != null) // TI
									// Transection
									// Payment
									// Logic.
									{
										lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
										ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_RTI_" + lZeroAmt + "_" + lPrvForDate);
										if (ltPensionHeadcodeRateVO == null) {
											ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "RTI", lZeroAmt, lPrvForDate);
											inputMap.put(lHeadCode + "_RTI_" + lZeroAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
										}

										if (ltPensionHeadcodeRateVO != null) {
											if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
												lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
												lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
												lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
											}

											lTIPer = Double.valueOf(ltPensionHeadcodeRateVO.getRate().toString());
											lNewTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
											lNewTiAmt = ((lTrnBasicAmt - lTrnPensionCutAmt) * ((lTIPer) / 100));
											lNewAdsTiAmt = ((lTrnBasicAmt - lTrnPensionCutAmt) * ((lNewTIPer) / 100));

											lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lNewAdsTiAmt))) - Double.parseDouble(String.valueOf(Math.round(lNewTiAmt)));
											lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lOthTIArrAmt)));

											inputMap.put("CurntPayTI", "Y"); // Set
											// Flag
											// for
											// indicate
											// Payment
											// of
											// TI
											// Done.
											inputMap.put("TrnForMonth", ForMonth);
											inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
											inputMap.put("DiffAmt", lOthTIArrAmt);
											inputMap.put("FieldType", "TI");
											if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthTIArrAmt > 0D) {
												lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
												lTrnPaymentLst.add(lTrnPaymetVo);
											}
										}
									} else {
										lTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
									}
									// lNewTiAmt = ((lNewBasicAmt -
									// lPensionCutAmt) * ((lTIPer) / 100 ));
								}
							}
							// ROP = Y and SpecialCase = N
							else if (lStrIsROPApplied != null && lStrIsSpecialCase != null && !lStrIsROPApplied.equalsIgnoreCase("N") && lStrIsSpecialCase.equalsIgnoreCase("N")) {
								lDPAmt = 0D;
								if (lForDate.after(lObjSmplDateFrm.parse("31/3/2004"))) // fordate
								// >
								// 1/1/2004.
								// DP
								// Mrg.
								{
									lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_DP_" + lZeroAmt + "_" + lForDate);
									if (lPensionHeadcodeRateVO == null) {
										lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "DP", lZeroAmt, lForDate);
										inputMap.put(lHeadCode + "_DP_" + lZeroAmt + "_" + lForDate, lPensionHeadcodeRateVO);
									}

									if (lPensionHeadcodeRateVO != null) {
										// Getting Revised Payment Vo for
										// Checking Transaction Payment.
										lObjRevisedPaymentVO = (RltPensionRevisedPayment) inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_DP_" + ForMonth);
										if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_DP_" + ForMonth + "NULL"))) {
											lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "DP", ForMonth);
											inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_DP_" + ForMonth, lObjRevisedPaymentVO);
											if (lObjRevisedPaymentVO == null) {
												inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_DP_" + ForMonth + "NULL", "TRUE");
											}
										}

										if (lObjRevisedPaymentVO != null) // DP
										// Transection
										// Payment
										// Logic.
										{
											lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
											ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_DP_" + lZeroAmt + "_" + lPrvForDate);
											if (ltPensionHeadcodeRateVO == null) {
												ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "DP", lZeroAmt, lPrvForDate);
												inputMap.put(lHeadCode + "_DP_" + lZeroAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
											}

											if (ltPensionHeadcodeRateVO != null) {
												if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
													lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
													lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
													lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
												}
												lDPPer = Double.valueOf(ltPensionHeadcodeRateVO.getRate().toString());
												Double lNewDPPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
												lOthDPArrAmt = ((lTrnBasicAmt - lTrnPensionCutAmt) * ((lNewDPPer) / 100)) - lDPPer;

												inputMap.put("CurntPayDP", "Y"); // Set
												// Flag
												// for
												// indicate
												// Payment
												// of
												// DP
												// Done.
												inputMap.put("TrnForMonth", ForMonth);
												inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
												inputMap.put("DiffAmt", lOthDPArrAmt);
												inputMap.put("FieldType", "PENSION");
												if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthDPArrAmt > 0D) {
													lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
													lTrnPaymentLst.add(lTrnPaymetVo);
												}
											}
										} else {
											lDPPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
											lDPAmt += Math.round(((lNewBasicAmt - lPensionCutAmt) * ((lDPPer) / 100)));
										}
									}

									// If Pensioner is not Alive then FPAmt is
									// split with DPPer.
									if ((!lBasicAmtType.equalsIgnoreCase("Basic")) && lStrIsAlive.equalsIgnoreCase("N") && lDPPer > 0 && lDateOfComm != null) {
										// Check For Splite FPBasic with DP Per.
										if (lDateOfComm.after(lObjSmplDateFrm.parse("31/3/2004"))) {
											List lresLst = getDividedBasic(lNewBasicAmt, Double.valueOf(lDPPer.toString()));
											lNewBasicAmt = Double.valueOf(lresLst.get(0).toString());
											lDPAmt = 0D;
											lDPAmt += Math.round(Double.valueOf(lresLst.get(1).toString()));
										} else {
											lDPAmt = 0D;
											lDPAmt += Math.round(lNewBasicAmt * (lDPPer / 100));
										}
									}
								}
								// Getting STI Amt from Duration.
								lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_STI_" + lZeroAmt + "_" + lForDate);
								if (lPensionHeadcodeRateVO == null) {
									lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "STI", lZeroAmt, lForDate);
									inputMap.put(lHeadCode + "_STI_" + lZeroAmt + "_" + lForDate, lPensionHeadcodeRateVO);
								}

								if (lPensionHeadcodeRateVO != null) {
									if (inputMap.containsKey("Mst6PayAfter2006") && "Y".equals(inputMap.get("Mst6PayAfter2006")) && !(inputMap.containsKey("Mst6Pay3050_2")))

									{
										lObjRevisedPaymentVO = null;
									} else {
										// Getting Revised Payment Vo for
										// Checking Transaction Payment.
										lObjRevisedPaymentVO = (RltPensionRevisedPayment) inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth);
										if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL"))) {
											lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "TI", ForMonth);
											inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth, lObjRevisedPaymentVO);
											if (lObjRevisedPaymentVO == null) {
												inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL", "TRUE");
											}
										}
									}

									if (lObjRevisedPaymentVO != null) // TI
									// Transection
									// Payment
									// Logic.
									{
										lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
										ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_STI_" + lZeroAmt + "_" + lPrvForDate);
										if (ltPensionHeadcodeRateVO == null) {
											ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "STI", lZeroAmt, lPrvForDate);
											ltObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(ltPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "TI", null);
											if (ltObjRevisedPaymentVO != null && ltObjRevisedPaymentVO.getPayInMonth() >= lObjRevisedPaymentVO.getForPayMonth()) {
												lPrvForDate = getPrevDate(ltPensionHeadcodeRateVO.getEffectiveFromDate());
												ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_STI_" + lZeroAmt + "_" + lPrvForDate);
												if (ltPensionHeadcodeRateVO == null) {
													ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "STI", lZeroAmt, lPrvForDate);
												}
											}

											inputMap.put(lHeadCode + "_STI_" + lZeroAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
										}

										if (ltPensionHeadcodeRateVO != null) {
											if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
												lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
												lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
												lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
											}
											lTIPer = Double.valueOf(ltPensionHeadcodeRateVO.getRate().toString());
											lNewTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
											lNewTiAmt = (((lTrnBasicAmt - lTrnPensionCutAmt) + lTrnDPAmt) * ((lTIPer) / 100));
											lNewAdsTiAmt = (((lTrnBasicAmt - lTrnPensionCutAmt) + lTrnDPAmt) * ((lNewTIPer) / 100));
											lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lNewAdsTiAmt))) - Double.parseDouble(String.valueOf(Math.round(lNewTiAmt)));
											lOthTIArrAmt = Double.parseDouble(String.valueOf(Math.round(lOthTIArrAmt)));

											inputMap.put("CurntPayTI", "Y"); // Set
											// Flag
											// for
											// indicate
											// Payment
											// of
											// DP
											// Done.
											inputMap.put("TrnForMonth", ForMonth);
											inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
											inputMap.put("DiffAmt", lOthTIArrAmt);
											inputMap.put("FieldType", "TI");
											if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthTIArrAmt > 0D) {
												lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
												lTrnPaymentLst.add(lTrnPaymetVo);

											}

										}
									} else {
										lTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
									}
									// lNewTiAmt = (((lNewBasicAmt -
									// lPensionCutAmt)+lDPAmt) * ((lTIPer) / 100
									// ));
								}
							}

							// lTIAmt = lNewTiAmt;
						}
						// If 1986 and 1996 both are not Checked and 2006 is
						// checked
						else if (lStrROP_1986 != null && (lStrROP_1986.equalsIgnoreCase("Y") || lStrROP_1986.equalsIgnoreCase("P")) && lStrROP_1996 != null
								&& (lStrROP_1996.equalsIgnoreCase("Y") || lStrROP_1996.equalsIgnoreCase("P")) && lStrROP_2006 != null
								&& (lStrROP_2006.equalsIgnoreCase("Y") || lStrROP_2006.equalsIgnoreCase("P"))) {
							// Getting STI Amt from Duration.
							lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_06_" + lZeroAmt + "_" + lForDate);
							if (lPensionHeadcodeRateVO == null) {
								lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI_06", lZeroAmt, lForDate);
								inputMap.put(lHeadCode + "_TI_06_" + lZeroAmt + "_" + lForDate, lPensionHeadcodeRateVO);
							}

							if (lPensionHeadcodeRateVO != null) {
								// After 2006 Revision Screen Retated.
								if (inputMap.containsKey("Mst6PayAfter2006") && "Y".equals(inputMap.get("Mst6PayAfter2006")) && !(inputMap.containsKey("Mst6Pay3050_2"))
										&& !inputMap.containsKey("16Perfor200901")) {
									lObjRevisedPaymentVO = null;
								} else {
									// Getting Revised Payment Vo for Checking
									// Transaction Payment.
									if (lObjRevisedPaymentVO == null && !"TRUE".equals(inputMap.get(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL"))) {
										lObjRevisedPaymentVO = lPensionBillDAO.getPaymentMnthDtls(lPensionHeadcodeRateVO.getPensionHeadcodeRateId(), "TI", ForMonth);
										inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth, lObjRevisedPaymentVO);
										if (lObjRevisedPaymentVO == null) {
											inputMap.put(lPensionHeadcodeRateVO.getPensionHeadcodeRateId() + "_TI_" + ForMonth + "NULL", "TRUE");
										}
									}
								}

								if (lObjRevisedPaymentVO != null) // TI
								// Transection
								// Payment
								// Logic.
								{
									lPrvForDate = getPrevDate(lPensionHeadcodeRateVO.getEffectiveFromDate());
									ltPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_06_" + lZeroAmt + "_" + lPrvForDate);
									if (ltPensionHeadcodeRateVO == null) {
										ltPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI_06", lZeroAmt, lPrvForDate);
										inputMap.put(lHeadCode + "_TI_06_" + lZeroAmt + "_" + lPrvForDate, ltPensionHeadcodeRateVO);
									}

									if (ltPensionHeadcodeRateVO != null) {
										if (inputMap.containsKey("TrnsactionFlag") && inputMap.get("TrnsactionFlag").toString().equals("Y")) {
											lTrnBasicAmt = (Double) inputMap.get("TransactionBasic");
											lTrnPensionCutAmt = (Double) inputMap.get("TransactionPCut");
											lTrnDPAmt = (Double) inputMap.get("TransactionDPAmnt");
										}
										Double lDbTotADPAmt = 0D;
										if (inputMap.containsKey("TotalMntAdPAmt") && inputMap.get("TotalMntAdPAmt") != null) {
											lDbTotADPAmt = Double.valueOf(inputMap.get("TotalMntAdPAmt").toString());
										}

										lTIPer = Double.valueOf(ltPensionHeadcodeRateVO.getRate().toString());
										lNewTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
										lNewTiAmt = (((lTrnBasicAmt - lTrnPensionCutAmt) + lTrnDPAmt + lDbTotADPAmt) * ((lTIPer) / 100));
										lNewAdsTiAmt = (((lTrnBasicAmt - lTrnPensionCutAmt) + lTrnDPAmt + lDbTotADPAmt) * ((lNewTIPer) / 100));
										lOthTIArrAmt = Math.ceil(lNewAdsTiAmt) - Math.ceil(lNewTiAmt);
										lOthTIArrAmt = Math.ceil(lOthTIArrAmt);

										inputMap.put("CurntPayTI", "Y"); // Set
										// Flag
										// for
										// indicate
										// Payment
										// of
										// DP
										// Done.
										inputMap.put("TrnForMonth", ForMonth);
										inputMap.put("TrnPayMonth", lObjRevisedPaymentVO.getPayInMonth());
										inputMap.put("DiffAmt", lOthTIArrAmt);
										inputMap.put("FieldType", "TI");
										if ((!"Y".equalsIgnoreCase(StringUtility.getParameter("isFromAfterExpiryScreen", request))) && lOthTIArrAmt > 0D) {
											lTrnPaymetVo = insertTrnPaymentDtls(inputMap);
											lTrnPaymentLst.add(lTrnPaymetVo);
										}

										// After 2006 Revision Screen Retated.
										if (inputMap.containsKey("Mst6Pay3050_2")) {
											lTIPer = lNewTIPer;// - lTIPer;
										}
									}
								} else {
									lTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
								}
								// lNewTiAmt = (((lNewBasicAmt -
								// lPensionCutAmt)+lDPAmt) * ((lTIPer) / 100 ));
							}
						}
						// lTIAmt = lNewTiAmt;
					}
				}
			}
			// HeadCode is equals to FreedomFoghter
			else if (lHeadCode.longValue() == 16 || lHeadCode.longValue() == 17 || lHeadCode.longValue() == 19) {
				if (lForDate != null && lForDate.after(lObjSmplDateFrm.parse("31/07/1998")) && lForDate.before(lObjSmplDateFrm.parse("31/07/2007"))) {
					lNewBasicAmt = 1500.00; // Fix Basic Amount for conditional
					// Period.
					lDPAmt = 0D;

					// Getting TI For Duration.
					lPensionHeadcodeRateVO = (RltPensionHeadcodeRate) inputMap.get(lHeadCode + "_TI_" + lZeroAmt + "_" + lForDate);
					if (lPensionHeadcodeRateVO == null) {
						lPensionHeadcodeRateVO = lPensionBillDAO.getRateFromHeadcodeRateRlt(lHeadCode, "TI", lZeroAmt, lForDate);
						inputMap.put(lHeadCode + "_TI_" + lZeroAmt + "_" + lForDate, lPensionHeadcodeRateVO);
					}

					if (lPensionHeadcodeRateVO != null) {
						lTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
					}

					// lTIAmt = ((lNewBasicAmt - lPensionCutAmt) * ((lTIPer) /
					// 100 ));
				}
			}

			/*
			 * if(lStrIsAlive.equalsIgnoreCase("N") && lDPPer > 0 &&
			 * lDateOfRtmnt != null) { // Check For Splite FPBasic with DP Per.
			 * if(lDateOfRtmnt.after(lObjSmplDateFrm.parse("31/12/2003"))) {
			 * List lresLst = getDividedBasic(lNewBasicAmt,
			 * Double.valueOf(lDPPer.toString())); lNewBasicAmt =
			 * Double.valueOf(lresLst.get(0).toString()); lDPAmt =
			 * Double.valueOf(lresLst.get(1).toString()); } else { lDPAmt =
			 * lNewBasicAmt (lDPPer / 100); } }
			 */

			inputMap.put("TrnPaymentLst", lTrnPaymentLst);

			// Set Basic/DP/TI/MA/IR/ATI Amount for Month in Custom MothlyVo.
			lMonthlyPensionBillVO.setBasicPensionAmount(new BigDecimal(lNewBasicAmt != 0 ? lNewBasicAmt.toString() : "0"));
			lMonthlyPensionBillVO.setDpPercent(new BigDecimal(lDPPer != 0 ? lDPPer.toString() : "0"));
			lMonthlyPensionBillVO.setDpPercentAmount(new BigDecimal(lDPAmt != 0 ? lDPAmt.toString() : "0"));
			lMonthlyPensionBillVO.setTiPercent(new BigDecimal(lTIPer != 0 ? lTIPer.toString() : "0"));
			lMonthlyPensionBillVO.setTiPercentAmount(new BigDecimal(lTIAmt != 0 ? lTIAmt.toString() : "0"));
			lMonthlyPensionBillVO.setMedicalAllowenceAmount(new BigDecimal(lMAAmt != 0 ? lMAAmt.toString() : "0"));
			// lMonthlyPensionBillVO.setTIArrearsAmount(new BigDecimal(lATIAmt
			// != 0 ? lATIAmt.toString() : "0"));
			lMonthlyPensionBillVO.setIr(new BigDecimal(lIRAmt != 0 ? lIRAmt.toString() : "0"));

		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}

		return lMonthlyPensionBillVO;
	}

	/**
	 * Calculate Greater TI Amount by Per. and Minimum amount.
	 * 
	 * @param lPensionHeadcodeRateVO
	 * @param lOldBasicAmt
	 * @param lPensionCutAmt
	 * @throws Exception
	 * @author Sagar
	 */
	private Double getGreaterTIAmt(RltPensionHeadcodeRate lPensionHeadcodeRateVO, Double lBasicAmt, Double lPensionCutAmt) throws Exception {

		Double lGreaterTIAmt = 0D;

		Double lMinAmt = 0D;
		Double lTIPer = 0D;

		try {
			if (lPensionHeadcodeRateVO != null) {
				if (lPensionHeadcodeRateVO.getRate() != null) {
					lTIPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
					lGreaterTIAmt = ((lBasicAmt - lPensionCutAmt) * ((lTIPer) / 100));
				}
				if (lPensionHeadcodeRateVO.getMinAmount() != null) // Checking
				// for
				// greater
				// TI Amount
				// from
				// (Per||MinAmt)
				{
					lMinAmt = Double.valueOf(lPensionHeadcodeRateVO.getMinAmount().toString());
					// lTIPer =
					// Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
					// lGreaterTIAmt = ((lBasicAmt - lPensionCutAmt) * ((lTIPer)
					// / 100 ));

					if (lMinAmt > lGreaterTIAmt) {
						lGreaterTIAmt = lMinAmt;
					}
				}
				/*
				 * else { lTIPer =
				 * Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
				 * lGreaterTIAmt = ((lBasicAmt - lPensionCutAmt) ((lTIPer) / 100
				 * )); }
				 */
			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}

		return lGreaterTIAmt;
	}

	/**
	 * Calculate Greater IR Amount by Per. and Minimum amount.
	 * 
	 * @param lPensionHeadcodeRateVO
	 * @param lOldBasicAmt
	 * @throws Exception
	 * @author Sagar
	 */
	private Double getGreaterIRAmt(RltPensionHeadcodeRate lPensionHeadcodeRateVO, Double lBasicAmt) throws Exception {

		Double lGreaterIRAmt = 0D;

		Double lMinAmt = 0D;
		Double lIRPer = 0D;

		try {
			if (lPensionHeadcodeRateVO != null) {
				if (lPensionHeadcodeRateVO.getRate() != null) {
					lIRPer = Double.valueOf(lPensionHeadcodeRateVO.getRate().toString());
					lGreaterIRAmt = ((lBasicAmt) * ((lIRPer) / 100));
				}
				if (lPensionHeadcodeRateVO.getMinAmount() != null) // Checking
				// for
				// greater
				// IR Amount
				// from
				// (Per||MinAmt)
				{
					lMinAmt = Double.valueOf(lPensionHeadcodeRateVO.getMinAmount().toString());

					if (lMinAmt > lGreaterIRAmt) {
						lGreaterIRAmt = lMinAmt;
					}
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}

		return lGreaterIRAmt;
	}

	/**
	 * Determine a New Basic value according to current basic, Applide ROP,
	 * Retirment Date and adapted type of rop86.
	 * 
	 * @param inputMap
	 * @return {@link Double}
	 * @throws Exception
	 */
	public Map getNewROPBasicAmt(Map inputMap) throws Exception {

		Map lRopResMap = new HashMap();

		Double lDNewBasicAmt = 0D;
		Double lDOldBasicAmt = 0D;

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		NewPensionBillDAO lObjPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());

		ValidPcodeView lObjValidPcodeVO = null;

		Date lDtOfRtmnt = null;
		Date lDateOfComm = null;
		SimpleDateFormat lObjSmplDateFrm = new SimpleDateFormat("dd/MM/yyyy");

		String lOldBasicType = null;
		String lStrROP_1986 = null;
		String lStrROP86_AdptedType = null;
		String lStrROP_1996 = null;
		String lStrROP_2006 = null;

		String lStrPayROP1986 = "N";
		String lStrPayROP1996 = "N";
		String lStrPayROP2006 = "N";

		try {
			if (inputMap.containsKey("lObjValidPcode") && inputMap.get("lObjValidPcode") != null) {
				lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");
			}

			lDOldBasicAmt += Math.round((Double) inputMap.get("OldBasicAmt"));
			lOldBasicType = (String) inputMap.get("OldBasicType");

			lDtOfRtmnt = lObjValidPcodeVO.getDateOfRetirement();
			lDateOfComm = lObjValidPcodeVO.getCommensionDate();

			lStrROP_1986 = (String) inputMap.get("ROP_1986");
			lStrROP86_AdptedType = (String) inputMap.get("ROP86_AdptedType");
			lStrROP_1996 = (String) inputMap.get("ROP_1996");
			lStrROP_2006 = (String) inputMap.get("ROP_2006");

			if (inputMap.containsKey("PayROP1986")) {
				lStrPayROP1986 = (inputMap.get("PayROP1986") != null) ? inputMap.get("PayROP1986").toString() : "N";
			}

			if (inputMap.containsKey("PayROP1996")) {
				lStrPayROP1996 = (inputMap.get("PayROP1996") != null) ? inputMap.get("PayROP1996").toString() : "N";
			}

			if (inputMap.containsKey("PayROP2006")) {
				lStrPayROP2006 = (inputMap.get("PayROP2006") != null) ? inputMap.get("PayROP2006").toString() : "N";
			}

			// For ROP 1986....
			if (lStrROP_1986 != null && lStrROP_1986.equalsIgnoreCase("Y") && !lStrPayROP1986.equalsIgnoreCase("Y")) {
				if (lDtOfRtmnt != null && lDtOfRtmnt.before(lObjSmplDateFrm.parse("30/09/1977"))) // /
				// 1
				{
					lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("1986", "2", lDOldBasicAmt); // /
					// Column
					// 2
					// from
					// ROP86.
				} else if (lDtOfRtmnt != null && lDtOfRtmnt.after(lObjSmplDateFrm.parse("29/09/1977")) // 2
						&& lDtOfRtmnt.before(lObjSmplDateFrm.parse("01/05/1979"))) {
					if (lStrROP86_AdptedType != null && lStrROP86_AdptedType.equalsIgnoreCase("DAN")) {
						lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("1986", "2", lDOldBasicAmt); // /
						// Column
						// 2
						// from
						// ROP86.
					} else if (lStrROP86_AdptedType != null && lStrROP86_AdptedType.equalsIgnoreCase("DAY")) {
						lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("1986", "3", lDOldBasicAmt); // /
						// Column
						// 3
						// from
						// ROP86.
					}
				} else if (lDtOfRtmnt != null && lDtOfRtmnt.after(lObjSmplDateFrm.parse("30/04/1979")) // 3
						&& lDtOfRtmnt.before(lObjSmplDateFrm.parse("31/01/1982"))) {
					lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("1986", "3", lDOldBasicAmt); // /
					// Column
					// 3
					// from
					// ROP86.
				} else if (lDtOfRtmnt != null && lDtOfRtmnt.after(lObjSmplDateFrm.parse("30/01/1982")) // 4
						&& lDtOfRtmnt.before(lObjSmplDateFrm.parse("31/03/1985"))) {
					if (lStrROP86_AdptedType != null && lStrROP86_AdptedType.equalsIgnoreCase("ADAN")) {
						lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("1986", "3", lDOldBasicAmt); // /
						// Column
						// 3
						// from
						// ROP86.
					} else if (lStrROP86_AdptedType != null && lStrROP86_AdptedType.equalsIgnoreCase("ADAY")) {
						lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("1986", "4", lDOldBasicAmt); // /
						// Column
						// 4
						// from
						// ROP86.
					}
				} else if (lDtOfRtmnt != null && lDtOfRtmnt.after(lObjSmplDateFrm.parse("30/03/1985")) // 5
						&& lDtOfRtmnt.before(lObjSmplDateFrm.parse("01/01/1986"))) {
					lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("1986", "5", lDOldBasicAmt); // /
					// Column
					// 5
					// from
					// ROP86.
				}

				if (lDNewBasicAmt != null && lDNewBasicAmt > 0) {
					lDOldBasicAmt = lDNewBasicAmt;
				}

				lRopResMap.put("NewBasic1986", lDNewBasicAmt);
				lStrPayROP1986 = "Y";
			}
			// For ROP 1986....
			if (lStrROP_1996 != null && lStrROP_1996.equalsIgnoreCase("Y") && !lStrPayROP1996.equalsIgnoreCase("Y")) // ROP
			// 1996
			{
				lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("1996", "0", lDOldBasicAmt);

				if (lDNewBasicAmt != null && lDNewBasicAmt > 0) {
					lDOldBasicAmt = lDNewBasicAmt;
				}

				lRopResMap.put("NewBasic1996", lDNewBasicAmt);
				lStrPayROP1996 = "Y";
			}
			// For ROP 2006....
			if (lStrROP_2006 != null && lStrROP_2006.equalsIgnoreCase("Y")) // ROP
			// 1996
			{
				if (lOldBasicType != null && (lOldBasicType.equals("FP1") || lOldBasicType.equals("FP2")) && lDateOfComm.after(lObjSmplDateFrm.parse("31/3/2004"))) {
					lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("2006", "0", lDOldBasicAmt);
				} else {
					lDNewBasicAmt = lObjPensionBillDAO.getNewBasicFromROPMst("2006", "1", lDOldBasicAmt);
				}

				lRopResMap.put("NewBasic2006", lDNewBasicAmt);
				lStrPayROP2006 = "Y";
			}

			lRopResMap.put("PayROP1986", lStrPayROP1986);
			lRopResMap.put("PayROP1996", lStrPayROP1996);
			lRopResMap.put("PayROP2006", lStrPayROP2006);

		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}

		return lRopResMap;
	}

	/**
	 * Method to set Session variables
	 * 
	 * @param inputMap
	 */
	private void setSessionInfo(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gDate = DBUtility.getCurrentDateFromDB();
	}

	/**
	 * Return No of Day for the given month.
	 * 
	 * @param lYYYYMM
	 * @return {@link Integer}
	 */
	public Integer getDaysOfMonth(Integer lYYYYMM) {

		Integer YYYY = Integer.parseInt(lYYYYMM.toString().substring(0, 4));
		Integer MM = Integer.parseInt(lYYYYMM.toString().substring(4, 6));
		Calendar cal = new GregorianCalendar(YYYY, (MM - 1), 1);
		Integer days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		return days;
	}

	/**
	 * Return subtract my Date by one day
	 * 
	 * @param lDate
	 * @return {@link Date}
	 */
	public Date getPrevDate(Date lDate) throws Exception {

		Date lNewDate = new Date();
		try {
			// SimpleDateFormat lObjSmplDateFrm = new
			// SimpleDateFormat("dd/MM/yyyy");
			long dateMillis = lDate.getTime();
			long dayInMillis = 1000 * 60 * 60 * 24;// subtract a day
			dateMillis = dateMillis - dayInMillis;
			lNewDate.setTime(dateMillis);// set myDate to new time

			java.sql.Date jsqlD = new java.sql.Date(lNewDate.getTime());
			lNewDate = jsqlD;
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return lNewDate;
	}

	/**
	 * Return add my Date by one day
	 * 
	 * @param lDate
	 * @return {@link Date}
	 */
	public Date getNextDate(Date lDate) throws Exception {

		Date lNewDate = new Date();
		try {
			// SimpleDateFormat lObjSmplDateFrm = new
			// SimpleDateFormat("dd/MM/yyyy");
			long dateMillis = lDate.getTime();
			long dayInMillis = 1000 * 60 * 60 * 24;// subtract a day
			dateMillis = dateMillis + dayInMillis;
			lNewDate.setTime(dateMillis);// set myDate to new time

			java.sql.Date jsqlD = new java.sql.Date(lNewDate.getTime());
			lNewDate = jsqlD;
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return lNewDate;
	}

	/**
	 * Generate A Vo of Transaction Payment.
	 * 
	 * @param inputMap
	 * @return {@link TrnPensionArrearDtls}
	 * @throws Exception
	 */
	private TrnPensionArrearDtls insertTrnPaymentDtls(Map inputMap) throws Exception {

		TrnPensionArrearDtls lObjArrearDtlsVo = new TrnPensionArrearDtls();

		try {
			lObjArrearDtlsVo.setPensionRequestId((Long) inputMap.get("PenRqstId"));
			lObjArrearDtlsVo.setPensionerCode(inputMap.get("PenCode").toString());
			lObjArrearDtlsVo.setArrearFieldType(inputMap.get("FieldType").toString());
			lObjArrearDtlsVo.setEffectedFromYyyymm(Integer.parseInt(inputMap.get("TrnForMonth").toString()));
			lObjArrearDtlsVo.setDifferenceAmount(new BigDecimal(inputMap.get("DiffAmt").toString()));
			lObjArrearDtlsVo.setPaymentFromYyyymm(Integer.parseInt(inputMap.get("TrnPayMonth").toString()));
		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
		return lObjArrearDtlsVo;
	}

	/**
	 * Calculate Difference for each month which are paid to pensioner, as per
	 * old rate TI and without DP
	 * 
	 * @param inputMap
	 * @return inputMap
	 * @throws Exception
	 */
	private Double getDifferenceOfAppliedDP2004(Map<String, Object> inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");

		Integer lForMonth = 0;
		Integer lCommMont = 0;
		Integer lLstMont = 0;
		Double lDiffDP2004 = 0d;
		int lPhase1 = 0;
		int lPhase2 = 0;
		int lPhase3 = 0;

		String lStrPPONo = null;

		Date lCommDate = null;

		TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = null;
		ValidPcodeView lObjValidPcodeVO = null;
		if (inputMap.containsKey("lObjValidPcode") && inputMap.get("lObjValidPcode") != null) {
			lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");
		}
		MonthlyPensionBillDAO lObjpensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());

		try {
			lForMonth = Integer.parseInt(inputMap.get("forMonth").toString());
			lLstMont = lForMonth;

			lObjTrnPensionRqstHdrVO = (TrnPensionRqstHdr) inputMap.get("TrnPensionRqstHdrVO");

			if (lObjValidPcodeVO != null) {
				lCommDate = lObjValidPcodeVO.getCommensionDate();
				lCommMont = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lCommDate));
				lStrPPONo = lObjValidPcodeVO.getPpoNo();
			}

			if (lForMonth != 0 && (lForMonth.intValue() == 200707 || lForMonth.intValue() == 200710)) {
				lLstMont = 200504;
			}

			for (Integer i = 200404, f = 1; i < lLstMont; f++) {
				if (i.intValue() == lCommMont.intValue()) {
					lPhase1 = lPhase2 = lPhase3 = 0;
				}
				if (i.intValue() < 200407) {
					lPhase1++;
				} else if (i.intValue() < 200501) {
					lPhase2++;
				} else if (i.intValue() < 200504) {
					lPhase3++;
				}

				i += ((Integer.parseInt((i.toString().substring(4, 6)))) == 12) ? 89 : 1;
			}

			lDiffDP2004 = lObjpensionBillDAO.getAppliedDP2004DiffAmt(lStrPPONo, lPhase1, lPhase2, lPhase3, SessionHelper.getLocationCode(inputMap));

		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}

		return lDiffDP2004;
	}

	/**
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public void getBasicsForMonth(Map<String, Object> inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSmplDateFrm = new SimpleDateFormat("dd/MM/yyyy");

		Double lOldDBlBasicAmt = 0D;
		Double lOldBasicAmt = 0D;
		Double lNewBasicAmt = 0D;

		Double lOldFP1Amt = 0D;

		Double lOldFP2Amt = 0D;

		String lStrIsROPApplied = null;
		String lStrROP_1986 = null;
		String lStrROP_1996 = null;
		String lStrROP_2006 = null;
		String lStrROPPay1986 = null;
		String lStrROPPay1996 = null;
		String lStrROPPay2006 = null;

		Long lHeadCode = 0L;
		String ForMonth = inputMap.get("Date").toString();

		Date lForDate = null;

		ValidPcodeView lObjValidPcodeVO = null;
		if (inputMap.containsKey("lObjValidPcode") && inputMap.get("lObjValidPcode") != null) {
			lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");
		}
		NewPensionBillDAO lPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());

		try {
			lForDate = lObjSmplDateFrm.parse("01/" + ForMonth.substring(4, 6) + "/" + ForMonth.substring(0, 4));

			lHeadCode = lObjValidPcodeVO.getHeadCode();
			lStrIsROPApplied = lObjValidPcodeVO.getIsRop();

			lStrROP_1986 = (String) inputMap.get("ROP_1986");
			lStrROP_1996 = (String) inputMap.get("ROP_1996");
			lStrROP_2006 = (String) inputMap.get("ROP_2006");

			lStrROPPay1986 = (String) inputMap.get("PayROP1986");
			lStrROPPay1996 = (String) inputMap.get("PayROP1996");
			lStrROPPay2006 = (String) inputMap.get("PayROP2006");

			// Old Case Basic (Basic/FP1/FP2)
			lOldDBlBasicAmt = lObjValidPcodeVO.getBasicPensionAmount().doubleValue();
			lOldBasicAmt += Math.round(lObjValidPcodeVO.getBasicPensionAmount().doubleValue());

			if (lObjValidPcodeVO.getDateOfDeath() != null) {
				if (lObjValidPcodeVO.getFp1Amount() != null) {
					lOldFP1Amt = lObjValidPcodeVO.getFp1Amount().doubleValue();
				}
				if (lObjValidPcodeVO.getFp2Amount() != null) {
					lOldFP2Amt = lObjValidPcodeVO.getFp2Amount().doubleValue();
				}

			}

			// HeadCode is not equals to FreedomFoghter && Navniraman.
			if (lHeadCode.longValue() != 16 && lHeadCode.longValue() != 17 && lHeadCode.longValue() != 18 && lHeadCode.longValue() != 19) {
				// / ROP is N then No Revesion Appied on Basic and TI Rate is
				// applicable.
				if (lStrIsROPApplied != null && lStrIsROPApplied.equalsIgnoreCase("N")) {
					inputMap.put("NewPensionBasic", lOldDBlBasicAmt);
					inputMap.put("NewFP1Basic", lOldFP1Amt);
					inputMap.put("NewFP2Basic", lOldFP2Amt);
				}
				// ROP is Y then we check follwing All Condition one by one.
				// 1.) 1986 is (Applied / Revised) then Revised basic with 1986
				// ROP and TI Rate is applicable.
				// 2.) 1996 is (Applied / Revised) then Revised basic with 1996
				// ROP and if DP is Merged then STI Rate is applicable.
				// 3.) 1996 is (Applied / Revised) then Revised basic with 1996
				// ROP and if DP is Not Merged then RTI Rate is applicable.
				// 4.) 2006 is (Applied / Revised) then Revised basic with 2006
				// ROP and TI_06 Rate is applicable and DP is 0.
				else if (lStrIsROPApplied != null && !lStrIsROPApplied.equalsIgnoreCase("N")) {
					// For 1986
					if (lForDate != null && lForDate.before(lObjSmplDateFrm.parse("01/01/1996")) && lForDate.after(lObjSmplDateFrm.parse("31/12/1985"))) {
						if (lStrROP_1986 != null && lStrROP_1986.equalsIgnoreCase("Y") && !lStrROPPay1986.equalsIgnoreCase("N")) {
							inputMap.put("NewPensionBasic", inputMap.get("Basic1986"));
							inputMap.put("NewFP1Basic", inputMap.get("FP11986"));
							inputMap.put("NewFP2Basic", inputMap.get("FP21986"));
						} else {
							inputMap.put("NewPensionBasic", lOldDBlBasicAmt);
							inputMap.put("NewFP1Basic", lOldFP1Amt);
							inputMap.put("NewFP2Basic", lOldFP2Amt);

						}
					}
					// For 1996
					else if (lForDate != null && lForDate.before(lObjSmplDateFrm.parse("01/01/2006")) && lForDate.after(lObjSmplDateFrm.parse("31/12/1995"))) {
						if (lStrROP_1996 != null && lStrROP_1996.equalsIgnoreCase("Y") && !lStrROPPay1996.equalsIgnoreCase("N")) {
							inputMap.put("NewPensionBasic", inputMap.get("Basic1996"));
							inputMap.put("NewFP1Basic", inputMap.get("FP11996"));
							inputMap.put("NewFP2Basic", inputMap.get("FP21996"));
						} else if (lStrROP_1986 != null && lStrROP_1986.equalsIgnoreCase("Y") && !lStrROPPay1986.equalsIgnoreCase("N")) {
							inputMap.put("NewPensionBasic", inputMap.get("Basic1986"));
							inputMap.put("NewFP1Basic", inputMap.get("FP11986"));
							inputMap.put("NewFP2Basic", inputMap.get("FP21986"));
						} else {
							inputMap.put("NewPensionBasic", lOldDBlBasicAmt);
							inputMap.put("NewFP1Basic", lOldFP1Amt);
							inputMap.put("NewFP2Basic", lOldFP2Amt);
						}
					}
					// For 2006
					else if (lForDate != null && lForDate.after(lObjSmplDateFrm.parse("31/12/2005"))) {
						if (lStrROP_2006 != null && lStrROP_2006.equalsIgnoreCase("Y") && !lStrROPPay2006.equalsIgnoreCase("N")) {
							inputMap.put("NewPensionBasic", inputMap.get("Basic2006"));
							inputMap.put("NewFP1Basic", inputMap.get("FP12006"));
							inputMap.put("NewFP2Basic", inputMap.get("FP22006"));
						} else if (lStrROP_1996 != null && lStrROP_1996.equalsIgnoreCase("Y") && !lStrROPPay1996.equalsIgnoreCase("N")) {
							inputMap.put("NewPensionBasic", inputMap.get("Basic1996"));
							inputMap.put("NewFP1Basic", inputMap.get("FP11996"));
							inputMap.put("NewFP2Basic", inputMap.get("FP21996"));
						} else if (lStrROP_1986 != null && lStrROP_1986.equalsIgnoreCase("Y") && !lStrROPPay1986.equalsIgnoreCase("N")) {
							inputMap.put("NewPensionBasic", inputMap.get("Basic1986"));
							inputMap.put("NewFP1Basic", inputMap.get("FP11986"));
							inputMap.put("NewFP2Basic", inputMap.get("FP21986"));
						} else {
							inputMap.put("NewPensionBasic", lOldDBlBasicAmt);
							inputMap.put("NewFP1Basic", lOldFP1Amt);
							inputMap.put("NewFP2Basic", lOldFP2Amt);
						}
					} else {
						inputMap.put("NewPensionBasic", lOldDBlBasicAmt);
						inputMap.put("NewFP1Basic", lOldFP1Amt);
						inputMap.put("NewFP2Basic", lOldFP2Amt);
					}
				} else {
					inputMap.put("NewPensionBasic", lOldDBlBasicAmt);
					inputMap.put("NewFP1Basic", lOldFP1Amt);
					inputMap.put("NewFP2Basic", lOldFP2Amt);
				}

			}
			// HeadCode is equals to Navniraman.
			else if (lHeadCode.longValue() == 18) {
				if (lForDate != null && lForDate.before(lObjSmplDateFrm.parse("01/10/2001"))) {
					lNewBasicAmt = lOldBasicAmt;
				} else {
					Double lBDNetAmount = lPensionBillDAO.getBasicFromHeadcodeSpecialRlt(lHeadCode, lOldBasicAmt, lForDate);
					if (lBDNetAmount != null) {
						lNewBasicAmt = lBDNetAmount;
					} else {
						lNewBasicAmt = lOldBasicAmt;
					}
				}

				inputMap.put("NewPensionBasic", lNewBasicAmt);
				inputMap.put("NewFP1Basic", lNewBasicAmt);
				inputMap.put("NewFP2Basic", lNewBasicAmt);
			}
			// HeadCode is equals to FreedomFoghter
			else if (lHeadCode.longValue() == 16 || lHeadCode.longValue() == 17 || lHeadCode.longValue() == 19) {
				if (lForDate != null && lForDate.before(lObjSmplDateFrm.parse("01/07/1971"))) {
					lNewBasicAmt = lOldBasicAmt;
				} else if (lForDate != null && lForDate.before(lObjSmplDateFrm.parse("01/08/1998"))) {
					Double lBDNetAmount = lPensionBillDAO.getBasicFromHeadcodeSpecialRlt(lHeadCode, lOldBasicAmt, lForDate);
					if (lBDNetAmount != null) {
						lNewBasicAmt = lBDNetAmount;
					} else {
						lNewBasicAmt = lOldBasicAmt;
					}
				} else if (lForDate != null && lForDate.after(lObjSmplDateFrm.parse("31/07/1998")) && lForDate.before(lObjSmplDateFrm.parse("31/07/2007"))) {
					lNewBasicAmt = 1500.00; // Fix Basic Amount for conditional
					// Period.
				} else if (lForDate != null && lForDate.after(lObjSmplDateFrm.parse("01/08/2007"))) {
					Double lBDNetAmount = lPensionBillDAO.getBasicFromHeadcodeSpecialRlt(lHeadCode, lOldBasicAmt, lForDate);
					if (lBDNetAmount != null) {
						lNewBasicAmt = lBDNetAmount;
					} else {
						lNewBasicAmt = lOldBasicAmt;
					}
				} else {
					lNewBasicAmt = lOldBasicAmt;
				}

				inputMap.put("NewPensionBasic", lNewBasicAmt);
				inputMap.put("NewFP1Basic", lNewBasicAmt);
				inputMap.put("NewFP2Basic", lNewBasicAmt);
			}

		} catch (Exception e) {
			gLogger.error("Error is :: " + e, e);
			throw (e);
		}
	}

	/**
	 * Six Pay Additional Pay Calculation Logic
	 * 
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public List getAditionalPensionAmt(Map inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");

		List<Double> LADPAmtLst = new ArrayList<Double>();

		Double lAdPnsnAmt = 0D;
		Double lPensionBasic = 0d;

		Date lDOBDate = null;
		Date lDODDate = null;
		Date lEndDate = null;
		Date lPayStartDate = null;
		Date lFP1Date = null;
		Date lFP2Date = null;

		int frmDD = 0;
		int frmMM = 0;
		int frmYYYY = 0;
		int toDD = 0;
		int toMM = 0;
		int toYYYY = 0;

		int DODdd = 0;
		int lfp1dd = 0;
		int lfp2dd = 0;
		int Enddd = 0;
		int lPayStartDay = 0;

		Integer lDobYYYYMM = 0;
		Integer lDODyyyyMM = 0;
		Integer lFP1YYYYMM = 0;
		Integer lFP2YYYYMM = 0;
		Integer lCurYYYYMM = 0;
		Integer lEndYYYYMM = 0;
		Integer lDayOfMonth = 0;
		Integer lPayStartYM = 0;

		Long ldiffofAge = 0l;
		Long lNewdiffofAge = 0l;

		String lStrPnsnrCode = null;

		Integer lAdnPnsnPerc = 0;
		Integer lNewAdnPnsnPerc = 0;

		// TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = null;
		ValidPcodeView lObjValidPcodeVO = null;
		MstPensionerFamilyDtls lPensionerFamilyDtlsVo = null;

		MonthlyPensionBillDAO lOBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());

		try {
			if (inputMap.containsKey("lObjValidPcode") && inputMap.get("lObjValidPcode") != null) {
				lObjValidPcodeVO = (ValidPcodeView) inputMap.get("lObjValidPcode");
			}

			lStrPnsnrCode = lObjValidPcodeVO.getPensionerCode();

			lDOBDate = lObjValidPcodeVO.getDateOfBirth();
			lDODDate = lObjValidPcodeVO.getDateOfDeath();
			lEndDate = lObjValidPcodeVO.getEndDate();

			lCurYYYYMM = (Integer.parseInt(inputMap.get("Date").toString()));
			if (inputMap.containsKey("PayStartDate") && inputMap.get("PayStartDate") != null) {
				lPayStartDate = (Date) inputMap.get("PayStartDate");
				if (lPayStartDate != null) {
					lPayStartYM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lPayStartDate));

					if (lObjValidPcodeVO.getCommensionDate() != null && (new SimpleDateFormat("yyyyMM").format(lObjValidPcodeVO.getCommensionDate())).equals(lCurYYYYMM.toString())) {
						lPayStartDay = Integer.valueOf(new SimpleDateFormat("dd").format(lPayStartDate));
						lPayStartDay -= 1;
					} else {
						lPayStartDay = Integer.valueOf(new SimpleDateFormat("dd").format(lPayStartDate));
					}

				}
			}

			if (lObjValidPcodeVO.getEndDate() != null) {
				lEndDate = lObjValidPcodeVO.getEndDate();
				lEndYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lEndDate));
				Enddd = Integer.parseInt(new SimpleDateFormat("dd").format(lEndDate));
			} else {
				lEndYYYYMM = 0;
			}

			if (lDODDate != null && lDODDate.toString().length() > 0) {
				lDODyyyyMM = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(lDODDate));
				if (lDODyyyyMM.intValue() <= lCurYYYYMM.intValue()) {
					DODdd = Integer.valueOf(new SimpleDateFormat("dd").format(lDODDate));

					lEndDate = (Date) inputMap.get("EndDate");
					if (lEndDate != null) {
						lEndYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lEndDate));
						Enddd = Integer.parseInt(new SimpleDateFormat("dd").format(lEndDate));
					} else {
						lEndYYYYMM = 0;
					}
					lDOBDate = null;
				}
			}
			lDayOfMonth = getDaysOfMonth(lCurYYYYMM);

			frmDD = 1;
			frmMM = Integer.valueOf(lCurYYYYMM.toString().substring(4, 6));
			frmYYYY = Integer.valueOf(lCurYYYYMM.toString().substring(0, 4));

			if (lDOBDate != null || lDODDate != null) {
				if (lDOBDate != null) {
					lDobYYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lDOBDate));

					toDD = Integer.parseInt(new SimpleDateFormat("dd").format(lDOBDate));
					toMM = Integer.parseInt(new SimpleDateFormat("MM").format(lDOBDate));
					toYYYY = Integer.parseInt(new SimpleDateFormat("yyyy").format(lDOBDate));

					ldiffofAge = calculateAgeSlab(frmDD, frmMM, frmYYYY, toDD, toMM, toYYYY);

					lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());

					if (ldiffofAge < 79) {
						return null;
					}

					if (frmMM == toMM) {
						lNewdiffofAge = calculateAgeSlab(toDD, frmMM, frmYYYY, toDD, toMM, toYYYY);
						lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
						ldiffofAge = lNewdiffofAge;
						toDD = toDD - 1;
					}
				}

				lPensionBasic = (Double) inputMap.get("NewPensionBasic");

				// / Pensioner is Dead.
				if (lDODDate != null && lDODDate.toString().length() > 0 && lDODyyyyMM.intValue() <= lCurYYYYMM.intValue()) {
					// pensioner has expired

					// getting Family Members details.
					if (inputMap.containsKey("lMapFamilyDtls")) {
						lPensionerFamilyDtlsVo = (MstPensionerFamilyDtls) ((Map) inputMap.get("lMapFamilyDtls")).get("Family" + lStrPnsnrCode);
					} else {
						lPensionerFamilyDtlsVo = lOBillDAO.getMstPensionerFamilyDtls(lStrPnsnrCode);
					}

					lFP1Date = (Date) inputMap.get("FP1Date");
					lFP1YYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lFP1Date));
					lfp1dd = Integer.parseInt(new SimpleDateFormat("dd").format(lFP1Date));

					lFP2Date = (Date) inputMap.get("FP2Date");
					lFP2YYYYMM = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(lFP2Date));
					lfp2dd = Integer.parseInt(new SimpleDateFormat("dd").format(lFP2Date));

					// check if date of death in month for which bill generated
					if (lDODyyyyMM.equals(lCurYYYYMM)) {
						// pensioner died in current month

						// end date is in this month itself
						if (lCurYYYYMM.equals(lEndYYYYMM)) {
							// check if end date before date of death
							if (lEndDate.before(lDODDate) || lEndDate.equals(lDODDate)) {
								// if end date less than date of death
								// hence pay till end date....

								// Pension start Payment date is in current
								// month.
								// Payment start date is before end date.
								if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay <= Enddd) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD <= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										} else if (toDD > lPayStartDay && toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else if (toDD >= Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
									}
								} else {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd);
										}
									} else {
										lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd;
									}
								}

								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}
							} else {
								// pensioner date of death before end
								// date...hence pay him till dod according to
								// basic
								// then till end date according to fp1 and/or
								// fp2 whatever applicable

								// compute till DoD according to basic and after
								// DoD to end date according to fp1 or/and fp2
								// applicable
								// Pension start Payment date is in current
								// month.
								if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay <= DODdd) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD <= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - lPayStartDay));
										} else if (toDD > lPayStartDay && toDD < DODdd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - toDD));
										} else if (toDD >= DODdd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - lPayStartDay));
									}
								} else {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD < DODdd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * DODdd);
										}
									} else {
										lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * DODdd;
									}
								}
								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}

								// now need to chk if fp1 date and fp2 date in
								// current month
								// if yes....chk accordingly and compute
								// chk if fp1 date after dod...thyen some
								// payment will be according to fp1
								// if fp1 before end date...pay till fp1 date
								// acc to fp1 and from fp1 till end date acc fp2
								// and remaining in arrears
								// else fp1 is after end date...hence pay till
								// end date acc to fp1 then remaining in arrears
								// from end date to fp1 date acc to fp1 and from
								// fp1 to remaining acc fp2
								// else fp2 applicable after dod...pay till end
								// date and remaining in arrears
								// if no...pay according to fp1 or fp2 till end
								// date and remaining in arrears

								lDOBDate = null;
								ldiffofAge = 0L;
								lAdnPnsnPerc = 0;
								lNewdiffofAge = 0L;
								lNewAdnPnsnPerc = 0;

								if (lPensionerFamilyDtlsVo != null && lPensionerFamilyDtlsVo.getDateOfBirth() != null) {
									lDOBDate = lPensionerFamilyDtlsVo.getDateOfBirth();

									toDD = Integer.parseInt(new SimpleDateFormat("dd").format(lDOBDate));
									toMM = Integer.parseInt(new SimpleDateFormat("MM").format(lDOBDate));
									toYYYY = Integer.parseInt(new SimpleDateFormat("yyyy").format(lDOBDate));

									ldiffofAge = calculateAgeSlab(frmDD, frmMM, frmYYYY, toDD, toMM, toYYYY);

									lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());

									if (ldiffofAge < 79) {
										return LADPAmtLst;
									}

									if (frmMM == toMM) {
										lNewdiffofAge = calculateAgeSlab(toDD, frmMM, frmYYYY, toDD, toMM, toYYYY);
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										ldiffofAge = lNewdiffofAge;
										toDD = toDD - 1;
									}

									if (lFP1YYYYMM.equals(lCurYYYYMM) && lFP2YYYYMM.equals(lCurYYYYMM)) {
										if (lfp1dd != DODdd && lfp1dd > DODdd) {
											// fp1 date after dod..hence some
											// payment will be according to
											// fp1...remaining according to fp2
											// considering end date

											if (lEndDate.before(lFP1Date) || lEndDate.equals(lFP1Date)) {
												// pay till end date according
												// to fp1
												lPensionBasic = (Double) inputMap.get("NewFP1Basic");

												// Pension start Payment date is
												// in current month.
												// and start date before end
												// date and after death date.
												if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay < Enddd && lPayStartDay >= DODdd) {
													if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
														lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
														lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
														if (toDD <= lPayStartDay) {
															lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
														} else if (toDD > lPayStartDay && toDD < Enddd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
															lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
														} else if (toDD >= Enddd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
														}
													} else {
														lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
													}
												} else {
													if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
														lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
														lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
														if (toDD <= DODdd) {
															lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
														} else if (toDD > DODdd && Enddd < toDD) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - DODdd));
															lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
														} else if (toDD >= Enddd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
														}
													} else {
														lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - DODdd);
													}
												}
												if (lAdPnsnAmt > 0D) {
													LADPAmtLst.add(lAdPnsnAmt);
												}
											} else {
												// fp1 date before end date
												// hence pay till fp1 date acc
												// tp fp1
												lPensionBasic = (Double) inputMap.get("NewFP1Basic");

												// Pension start Payment date is
												// in current month.
												// and start date before fp1
												// date and after death date.
												if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay < lfp1dd && lPayStartDay >= DODdd) {
													if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
														lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
														lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
														if (toDD <= lPayStartDay) {
															lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - lPayStartDay));
														} else if (toDD > lPayStartDay && toDD < lfp1dd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
															lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - toDD));
														} else if (toDD >= lfp1dd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - lPayStartDay));
														}
													} else {
														lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - lPayStartDay));
													}
												} else {
													if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
														lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
														lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
														if (toDD <= DODdd) {
															lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - DODdd));
														} else if (toDD > DODdd && toDD < lfp1dd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - DODdd));
															lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - toDD));
														} else if (toDD >= lfp1dd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - DODdd));
														}
													} else {
														lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - DODdd);
													}
												}
												if (lAdPnsnAmt > 0D) {
													LADPAmtLst.add(lAdPnsnAmt);
												}

												// pay from fp1 date till end
												// date acc to fp2
												lPensionBasic = (Double) inputMap.get("NewFP2Basic");

												// Pension start Payment date is
												// in current month.
												// and start date before end
												// date and after fp1 date.
												if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay < Enddd && lPayStartDay >= lfp1dd) {
													if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
														lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
														lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
														if (toDD <= lPayStartDay) {
															lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
														} else if (toDD > lPayStartDay && toDD < Enddd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
															lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
														} else if (toDD >= Enddd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
														}
													} else {
														lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
													}
												} else {
													if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
														lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
														lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
														if (toDD <= lfp1dd) {
															lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lfp1dd));
														} else if (toDD > lfp1dd && toDD < Enddd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lfp1dd));
															lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
														} else if (toDD >= Enddd) {
															lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lfp1dd));
														}
													} else {
														lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lfp1dd);
													}
												}
												if (lAdPnsnAmt > 0D) {
													LADPAmtLst.add(lAdPnsnAmt);
												}
											}
										} else {
											// fp1 date before dod...hence pay
											// acc to fp2 till end date
											lPensionBasic = (Double) inputMap.get("NewFP2Basic");

											// Pension start Payment date is in
											// current month.
											// and start date before end date
											// and after death date.
											if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay < Enddd && lPayStartDay >= DODdd) {
												if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
													lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
													lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
													if (toDD <= lPayStartDay) {
														lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
													} else if (toDD > lPayStartDay && toDD < Enddd) {
														lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
														lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
													} else if (toDD >= Enddd) {
														lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
													}
												} else {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
												}
											} else {
												if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
													lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
													lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
													if (toDD <= DODdd) {
														lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - DODdd));
													} else if (toDD > DODdd && toDD < Enddd) {
														lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - DODdd));
														lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
													} else if (toDD >= Enddd) {
														lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - DODdd));
													}
												} else {
													lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - DODdd);
												}
											}
											if (lAdPnsnAmt > 0D) {
												LADPAmtLst.add(lAdPnsnAmt);
											}
										}
									} else {
										// fp1 and fp2 date not in current month
										// pay according to either fp1 or fp2
										// for

										if (lEndDate.before(lFP1Date) || lEndDate.equals(lFP1Date)) {
											// pay acc to fp1
											lPensionBasic = (Double) inputMap.get("NewFP1Basic");
										} else if (lEndDate.equals(lFP2Date) || lEndDate.after(lFP2Date)) {
											// pay acc to fp2
											lPensionBasic = (Double) inputMap.get("NewFP2Basic");
										}

										// Pension start Payment date is in
										// current month.
										// and start date before end date and
										// after death date.
										if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay < Enddd && lPayStartDay >= DODdd) {
											if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
												lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
												lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
												if (toDD <= lPayStartDay) {
													lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
												} else if (toDD > lPayStartDay && toDD < Enddd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
													lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
												} else if (toDD >= Enddd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
												}
											} else {
												lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
											}
										} else {
											if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
												lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
												lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
												if (toDD <= DODdd) {
													lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - DODdd));
												} else if (toDD > DODdd && toDD < Enddd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - DODdd));
													lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
												} else if (toDD >= Enddd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - DODdd));
												}
											} else {
												lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - DODdd);
											}
										}
										if (lAdPnsnAmt > 0D) {
											LADPAmtLst.add(lAdPnsnAmt);
										}
									}
								}
							}
						} else {
							// end date not in this month
							// compute reduced amount for the days alive

							if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay <= DODdd) {
								if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
									lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
									lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
									if (toDD <= lPayStartDay) {
										lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - lPayStartDay));
									} else if (toDD > lPayStartDay && toDD < DODdd) {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
										lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - toDD));
									} else if (toDD >= DODdd) {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - lPayStartDay));
									}
								} else {
									lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - lPayStartDay));
								}
							} else {
								if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
									lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
									lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
									if (toDD < DODdd) {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
										lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - toDD));
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * DODdd);
									}
								} else {
									lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * DODdd;
								}
							}
							if (lAdPnsnAmt > 0D) {
								LADPAmtLst.add(lAdPnsnAmt);
							}

							// check for fp1 n fp2 in the remaining days

							lDOBDate = null;
							ldiffofAge = 0L;
							lAdnPnsnPerc = 0;
							lNewdiffofAge = 0L;
							lNewAdnPnsnPerc = 0;

							if (lPensionerFamilyDtlsVo != null && lPensionerFamilyDtlsVo.getDateOfBirth() != null) {
								lDOBDate = lPensionerFamilyDtlsVo.getDateOfBirth();

								toDD = Integer.parseInt(new SimpleDateFormat("dd").format(lDOBDate));
								toMM = Integer.parseInt(new SimpleDateFormat("MM").format(lDOBDate));
								toYYYY = Integer.parseInt(new SimpleDateFormat("yyyy").format(lDOBDate));

								ldiffofAge = calculateAgeSlab(frmDD, frmMM, frmYYYY, toDD, toMM, toYYYY);

								lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());

								if (ldiffofAge < 79) {
									return LADPAmtLst;
								}

								if (frmMM == toMM) {
									lNewdiffofAge = calculateAgeSlab(toDD, frmMM, frmYYYY, toDD, toMM, toYYYY);
									lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
									ldiffofAge = lNewdiffofAge;
									toDD = toDD - 1;
								}

								if (lFP1YYYYMM.equals(lCurYYYYMM) && lFP2YYYYMM.equals(lCurYYYYMM)) {
									if (lDODDate.equals(lFP2Date) || lDODDate.after(lFP2Date)) {
										// now for remaining days give according
										// to fp2
										// coz death after fp1 date

										lPensionBasic = (Double) inputMap.get("NewFP2Basic");

										if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay >= DODdd) {
											if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
												lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
												lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
												if (toDD >= lPayStartDay) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
													lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
												} else {
													lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
												}
											} else {
												lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
											}
										} else {
											if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
												lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
												lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
												if (toDD >= DODdd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - DODdd));
													lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
												} else {
													lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - DODdd));
												}
											} else {
												lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - DODdd));
											}
										}
										if (lAdPnsnAmt > 0D) {
											LADPAmtLst.add(lAdPnsnAmt);
										}
									} else {
										// give for remaining days... till fp1
										// date according to fp1, rest according
										// to fp2

										// Pay acc to FP1.
										lPensionBasic = (Double) inputMap.get("NewFP1Basic");

										if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay < lfp1dd && lPayStartDay >= DODdd) {
											if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
												lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
												lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
												if (toDD <= lPayStartDay) {
													lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - lPayStartDay));
												} else if (toDD > lPayStartDay && toDD < lfp1dd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
													lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - toDD));
												} else if (toDD >= lfp1dd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - lPayStartDay));
												}
											} else {
												lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - lPayStartDay));
											}
										} else {
											if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
												lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
												lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
												if (toDD <= DODdd) {
													lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - DODdd));
												} else if (toDD > DODdd && toDD < lfp1dd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - DODdd));
													lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - toDD));
												} else if (toDD >= lfp1dd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - DODdd));
												}
											} else {
												lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - DODdd);
											}
										}
										if (lAdPnsnAmt > 0D) {
											LADPAmtLst.add(lAdPnsnAmt);
										}

										// Pay acc to FP2.
										lPensionBasic = (Double) inputMap.get("NewFP2Basic");

										if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay >= lfp1dd) {
											if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
												lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
												lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
												if (toDD >= lPayStartDay) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
													lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
												} else {
													lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
												}
											} else {
												lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
											}
										} else {
											if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
												lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
												lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
												if (toDD >= lfp1dd) {
													lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lfp1dd));
													lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
												} else {
													lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lfp1dd));
												}
											} else {
												lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lfp1dd));
											}
										}
										if (lAdPnsnAmt > 0D) {
											LADPAmtLst.add(lAdPnsnAmt);
										}
									}
								} else {
									// give according to fp1 or fp2 whichever is
									// applicable
									if (lDODDate.before(lFP1Date) || lDODDate.equals(lFP1Date)) {
										lPensionBasic = (Double) inputMap.get("NewFP1Basic");
									} else if (lDODDate.after(lFP2Date) || lDODDate.equals(lFP2Date)) {
										lPensionBasic = (Double) inputMap.get("NewFP2Basic");
									}

									if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay >= DODdd) {
										if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
											lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
											lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
											if (toDD >= lPayStartDay) {
												lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
												lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
											} else {
												lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
											}
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
										}
									} else {
										if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
											lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
											lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
											if (toDD >= DODdd) {
												lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - DODdd));
												lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
											} else {
												lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - DODdd));
											}
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - DODdd));
										}
									}
									if (lAdPnsnAmt > 0D) {
										LADPAmtLst.add(lAdPnsnAmt);
									}
								}
							}
						}
					} else {
						// pensioner is dead but has not expired in current
						// month

						lDOBDate = null;
						ldiffofAge = 0L;
						lAdnPnsnPerc = 0;
						lNewdiffofAge = 0L;
						lNewAdnPnsnPerc = 0;

						if (lPensionerFamilyDtlsVo != null && lPensionerFamilyDtlsVo.getDateOfBirth() != null) {
							lDOBDate = lPensionerFamilyDtlsVo.getDateOfBirth();

							toDD = Integer.parseInt(new SimpleDateFormat("dd").format(lDOBDate));
							toMM = Integer.parseInt(new SimpleDateFormat("MM").format(lDOBDate));
							toYYYY = Integer.parseInt(new SimpleDateFormat("yyyy").format(lDOBDate));

							ldiffofAge = calculateAgeSlab(frmDD, frmMM, frmYYYY, toDD, toMM, toYYYY);

							lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());

							if (ldiffofAge < 79) {
								return LADPAmtLst;
							}

							if (frmMM == toMM) {
								lNewdiffofAge = calculateAgeSlab(toDD, frmMM, frmYYYY, toDD, toMM, toYYYY);
								lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
								ldiffofAge = lNewdiffofAge;
								toDD = toDD - 1;
							}

						}

						if (lFP1YYYYMM.equals(lCurYYYYMM) && lFP2YYYYMM.equals(lCurYYYYMM) && lEndYYYYMM.equals(lCurYYYYMM)) {
							// all three fp1 n fp2 n end date in curr
							// month...compute division for them
							if (lEndDate.before(lFP1Date) || lEndDate.equals(lFP1Date)) { // end
								// date
								// is
								// before
								// or
								// equal
								// to
								// fp1
								// date
								// pay till end date acc to fp1
								lPensionBasic = (Double) inputMap.get("NewFP1Basic");

								if (lPayStartYM != 0 && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay <= Enddd) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD <= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										} else if (toDD > lPayStartDay && toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else if (toDD >= Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
									}
								} else {
									// end date in curr month...so give him
									// pension only till end date
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd);
										}
									} else {
										lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd;
									}

								}
								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}
							} else {
								// give upto fp1 date according to fp1
								// pay till fp1 date acc to fp1
								lPensionBasic = (Double) inputMap.get("NewFP1Basic");
								if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay <= lfp1dd) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD <= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - lPayStartDay));
										} else if (toDD > lPayStartDay && toDD < lfp1dd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - toDD));
										} else if (toDD >= lfp1dd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - lPayStartDay));
									}
								} else {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD < lfp1dd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lfp1dd - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * lfp1dd);
										}
									} else {
										lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * lfp1dd;
									}
								}
								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}

								// pay from fp1 date to end date acc to fp2
								lPensionBasic = (Double) inputMap.get("NewFP2Basic");
								if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay < Enddd && lPayStartDay >= lfp1dd) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD <= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										} else if (toDD > lPayStartDay && toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else if (toDD >= Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
									}
								} else {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD <= lfp1dd) {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lfp1dd));
										} else if (toDD > lfp1dd && toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lfp1dd));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else if (toDD >= Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lfp1dd));
										}
									} else {
										lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lfp1dd);
									}
								}
								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}
							}
						} else {
							// all three fp1 fp2 n end date not in this month...
							if (lFP1YYYYMM.equals(lCurYYYYMM) && lFP2YYYYMM.equals(lCurYYYYMM)) {
								// give till fp1 date according to fp1, rest
								// according to fp2
								lPensionBasic = (Double) inputMap.get("NewFP1Basic");
								if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay <= DODdd) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD <= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - lPayStartDay));
										} else if (toDD > lPayStartDay && toDD < DODdd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - toDD));
										} else if (toDD >= DODdd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - lPayStartDay));
									}
								} else {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD < DODdd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (DODdd - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * DODdd);
										}
									} else {
										lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * DODdd;
									}
								}
								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}

								// Payment According to FP2 Amount.
								lPensionBasic = (Double) inputMap.get("NewFP2Basic");
								if (lPayStartYM != null && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay >= DODdd) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD >= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
									}
								} else {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD >= DODdd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - DODdd));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - DODdd));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - DODdd));
									}
								}
								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}
							} else if (lFP1YYYYMM >= lCurYYYYMM && lEndYYYYMM.equals(lCurYYYYMM)) {
								// fp1 applicable for whole month
								lPensionBasic = (Double) inputMap.get("NewFP1Basic");

								if (lPayStartYM != 0 && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay <= Enddd) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD <= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										} else if (toDD > lPayStartDay && toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else if (toDD >= Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
									}
								} else {
									// end date in curr month...so give him
									// pension only till end date
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd);
										}
									} else {
										lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd;
									}

								}
								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}
							} else if (lFP2YYYYMM <= lCurYYYYMM && lEndYYYYMM.equals(lCurYYYYMM)) {
								// fp2 applicable for whole month
								lPensionBasic = (Double) inputMap.get("NewFP2Basic");

								if (lPayStartYM != 0 && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay <= Enddd) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD <= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										} else if (toDD > lPayStartDay && toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else if (toDD >= Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
									}
								} else {
									// end date in curr month...so give him
									// pension only till end date
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD < Enddd) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd);
										}
									} else {
										lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd;
									}

								}
								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}
							} else {
								// only 1 either fp1 or fp2 applicable for whole
								// month
								if (lCurYYYYMM <= lFP1YYYYMM) {
									lPensionBasic = (Double) inputMap.get("NewFP1Basic");
								} else if (lCurYYYYMM >= lFP2YYYYMM) {
									lPensionBasic = (Double) inputMap.get("NewFP2Basic");
								}

								if (lPayStartYM != 0 && lPayStartYM.equals(lCurYYYYMM)) {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										if (toDD >= lPayStartDay) {
											lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
											lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
										} else {
											lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
										}
									} else {
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
									}
								} else {
									if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
										lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
										lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
										lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
										lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
									} else {
										lAdPnsnAmt = (lPensionBasic * lAdnPnsnPerc) / 100;
									}

								}
								if (lAdPnsnAmt > 0D) {
									LADPAmtLst.add(lAdPnsnAmt);
								}
							}
						}
					}

				} else if (lDOBDate != null) {
					// pensioner is alive....but check if end date in curr mnth

					if (lEndYYYYMM.equals(lCurYYYYMM)) {
						// Pension start Payment date is in current month.
						// Payment start date is before end date.
						if (lPayStartYM != 0 && lPayStartYM.equals(lCurYYYYMM) && lPayStartDay <= Enddd) {
							if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
								lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
								lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
								if (toDD <= lPayStartDay) {
									lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
								} else if (toDD > lPayStartDay && toDD < Enddd) {
									lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (toDD - lPayStartDay));
									lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
								} else if (toDD >= Enddd) {
									lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
								}
							} else {
								lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - lPayStartDay));
							}
						} else {
							// end date in curr month...so give him pension only
							// till end date
							if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
								lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
								lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
								if (toDD < Enddd) {
									lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
									lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (Enddd - toDD));
								} else {
									lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd);
								}
							} else {
								lAdPnsnAmt = (((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * Enddd;
							}

						}
						if (lAdPnsnAmt > 0D) {
							LADPAmtLst.add(lAdPnsnAmt);
						}
					} else {
						// pensioner is alive and end date not in this
						// month...i.e normal case

						if (lPayStartYM != 0 && lPayStartYM.equals(lCurYYYYMM)) {
							if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
								lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
								lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
								if (toDD < lPayStartDay) {
									lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lPayStartDay - toDD));
									lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
								} else {
									lAdPnsnAmt = ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
								}
							} else {
								lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - lPayStartDay));
							}
						} else {
							if (lNewdiffofAge.intValue() != 0 && lAdnPnsnPerc.intValue() != lNewAdnPnsnPerc.intValue()) {
								lAdnPnsnPerc = getSlabBasedonAgeYear(ldiffofAge.intValue());
								lNewAdnPnsnPerc = getSlabBasedonAgeYear(lNewdiffofAge.intValue());
								lAdPnsnAmt = ((((lPensionBasic * lAdnPnsnPerc) / 100) / lDayOfMonth) * toDD);
								lAdPnsnAmt += ((((lPensionBasic * lNewAdnPnsnPerc) / 100) / lDayOfMonth) * (lDayOfMonth - toDD));
							} else {
								lAdPnsnAmt = (lPensionBasic * lAdnPnsnPerc) / 100;
							}
						}
						if (lAdPnsnAmt > 0D) {
							LADPAmtLst.add(lAdPnsnAmt);
						}
					}
				}

			}

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			throw (e);
		}

		return LADPAmtLst;
	}

	public Long calculateAgeSlab(int FRmDD, int FRmMM, int FRmYY, int ToDD, int ToMM, int ToYY) throws Exception {

		long diffDays = 0L;
		try {
			/*
			 * Calendar calendar1 = Calendar.getInstance(); Calendar calendar2 =
			 * Calendar.getInstance(); calendar2.set(FRmYY, FRmMM-1,
			 * FRmDD);//currentDate calendar1.set(ToYY, ToMM-1, ToDD); //Date of
			 * birth or fp1 date long milliseconds1 =
			 * calendar1.getTimeInMillis(); long milliseconds2 =
			 * calendar2.getTimeInMillis(); long diff = milliseconds2 -
			 * milliseconds1; diffDays = diff / (24 60 60 1000);
			 */
			int tempDifYear = FRmYY - ToYY;
			if (FRmMM < ToMM) {
				tempDifYear = tempDifYear - 1;
				diffDays = Long.valueOf(tempDifYear);
			} else {
				if (ToMM == FRmMM) {
					if (FRmDD < ToDD) {
						tempDifYear = tempDifYear - 1;
						diffDays = Long.valueOf(tempDifYear);
					} else {
						diffDays = Long.valueOf(tempDifYear);
					}
				} else {
					diffDays = Long.valueOf(tempDifYear);
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			throw (e);
		}
		return diffDays;
	}

	public Integer getSlabBasedonAgeYear(int lIntNoOfYear) {

		Integer lBDReturnVal = 0;
		if (lIntNoOfYear >= 80 && lIntNoOfYear < 85) {
			lBDReturnVal = 20;
		}
		if (lIntNoOfYear >= 85 && lIntNoOfYear < 90) {
			lBDReturnVal = 30;
		}
		if (lIntNoOfYear >= 90 && lIntNoOfYear < 95) {
			lBDReturnVal = 40;
		}
		if (lIntNoOfYear >= 95 && lIntNoOfYear < 100) {
			lBDReturnVal = 50;
		}
		if (lIntNoOfYear >= 100) {
			lBDReturnVal = 100;
		}
		return lBDReturnVal;
	}

}
