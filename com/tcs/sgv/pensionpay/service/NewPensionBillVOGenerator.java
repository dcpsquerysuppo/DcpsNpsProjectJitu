package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.NewPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;


/**
 * Pension Bill Specific VO Generator.
 * 
 * @author Sagar Patel
 * @version 1.0
 */
public class NewPensionBillVOGenerator extends ServiceImpl implements VOGeneratorService {

	/* Global Variable for PostId */
	private Long gLngPostId = null;

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Global Variable for Current Date */
	private Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	private String gStrLocCode = null;

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for ResourceBundle */
	private static ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	/* Global Variable for LAT Amount */
	private Double lLtaAmount = 0D;

	/**
	 * Generates VO for insertion of Pension Bill Data
	 * 
	 * @param Map
	 *            :inputMap
	 * @return ResultObject
	 */
	public ResultObject generateMap(Map lMapInput) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) lMapInput.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		String lStrBillType = bundleConst.getString("RECOVERY.PENSION");
		String lStrStatus = bundleConst.getString("STATUS.CRTD");
		NewPensionBillDAO lObPensionBillDAO = new NewPensionBillDAOImpl(srvcLoc.getSessionFactory());
		TrnPensionBillHdr lObjTrnPensionBillHdr = null;
		TrnPensionBillDtls lObjTrnPensionBillDtls = null;
		try {
			String lStrPPONo = StringUtility.getParameter("txtPPONO", request).trim();
			setSessionInfo(lMapInput);
			if (lStrPPONo != null) {
				lObjTrnPensionBillHdr = generateTrnPensionBillHdrVO(lMapInput);
				lObjTrnPensionBillDtls = generateTrnPensionBillDtlsVO(lMapInput);
				List lPnsnCaseElem = lObPensionBillDAO.getRltPensioncaseBillPK(lStrPPONo, lStrBillType, lStrStatus, gStrLocCode);
				if (lPnsnCaseElem != null && !lPnsnCaseElem.isEmpty()) {
					lMapInput.put("RltPensnBillPK", new Long(lPnsnCaseElem.get(0).toString()));
				}
				lMapInput.put("TrnPensionBillHdrVO", lObjTrnPensionBillHdr);
				lMapInput.put("TrnPensionBillDtlsVO", lObjTrnPensionBillDtls);
			}
			lMapInput.put("PPONO", lStrPPONo);
			objRes.setResultValue(lMapInput);
		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	/**
	 * Generate TrnPensionBillHdrVO from JSP Data
	 * 
	 * @param lMapInput
	 * @return TrnPensionBillHdr
	 */
	public TrnPensionBillHdr generateTrnPensionBillHdrVO(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		TrnPensionBillHdr lObjTrnPensionBillHdr = new TrnPensionBillHdr();
		String lStrBillForMonth = StringUtility.getParameter("hidBillForMnth", request);
		String lStrSchemeNo = StringUtility.getParameter("txtSchemeNo", request);
		String lStrBankCode = StringUtility.getParameter("hidBankCode", request).trim();
		String lStrBranchCode = StringUtility.getParameter("hidBranchCode", request).trim();
		try {
			setSessionInfo(lMapInput);
			lObjTrnPensionBillHdr.setBillDate(gDtCurrDt);
			lObjTrnPensionBillHdr.setForMonth(Integer.valueOf(lStrBillForMonth));
			lObjTrnPensionBillHdr.setCreatedDate(gDtCurrDt);
			lObjTrnPensionBillHdr.setLocationCode(gStrLocCode);
			lObjTrnPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
			lObjTrnPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
			lObjTrnPensionBillHdr.setSchemeCode(lStrSchemeNo);
			if (!StringUtility.getParameter("hidHeadcode", request).trim().equals("")) {
				lObjTrnPensionBillHdr.setHeadCode(new BigDecimal(StringUtility.getParameter("hidHeadcode", request).trim()));
			}
			if (!StringUtility.getParameter("hidScheme", request).trim().equals("")) {
				lObjTrnPensionBillHdr.setScheme(StringUtility.getParameter("hidScheme", request).trim());
			}
			lObjTrnPensionBillHdr.setTrnCounter(1);
			lObjTrnPensionBillHdr.setRejected(0);

			String lStrPnsnAmt = StringUtility.getParameter("txtPensionBillAmt", request).trim();
			String lStrRecovery = StringUtility.getParameter("txtPnsnRecovery", request).trim();
			String lStrNetPensionAmt = StringUtility.getParameter("txtNetPensionAmt", request).trim();

			Double lGrossAmt = 0d;
			lGrossAmt = new Double(lStrPnsnAmt.length() != 0 ? lStrPnsnAmt : "0");
			lObjTrnPensionBillHdr.setGrossAmount(new BigDecimal(lGrossAmt > 0D ? lGrossAmt : 0D));
			lObjTrnPensionBillHdr.setDeductionA(new BigDecimal(lStrRecovery.length() > 0 ? lStrRecovery : "0"));
			lObjTrnPensionBillHdr.setNetAmount(new BigDecimal(lStrNetPensionAmt.length() > 0 ? lStrNetPensionAmt : "0"));
			if (lStrBankCode.length() > 0) {
				lObjTrnPensionBillHdr.setBankCode(lStrBankCode);
			}
			if (lStrBranchCode.length() > 0) {
				lObjTrnPensionBillHdr.setBranchCode(lStrBranchCode);
			}

		} catch (Exception e) {
			gLogger.error("Error in " + e, e);
			throw (e);
		}
		return lObjTrnPensionBillHdr;
	}

	/**
	 * Generate TrnPensionBillDtlsVO from JSP Data
	 * 
	 * @param lMapInput
	 * @return TrnPensionBillDtls
	 */
	public TrnPensionBillDtls generateTrnPensionBillDtlsVO(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");

		TrnPensionBillDtls lObjTrnPensionBillDtls = new TrnPensionBillDtls();

		String lStrPPONo = StringUtility.getParameter("hidDPPONO", request).trim();
		String lStrPensionerCode = StringUtility.getParameter("hidPensionerCode", request).trim();
		StringUtility.getParameter("hidAliveFlg", request).trim();
		String lStrBillForMonth = StringUtility.getParameter("hidBillForMnth", request);
		String lStrPnsnerName = StringUtility.getParameter("txtPnsnerName", request).trim();
		String lStrBasicPension = StringUtility.getParameter("txtBasicPension", request).trim();
		String lStrDPAmt = StringUtility.getParameter("txtDPPercentAmt", request).trim();
		String lStrADPAmt = StringUtility.getParameter("txtADPAmt", request).trim();
		String lStrIR1Amt = StringUtility.getParameter("txtIR1Amt", request).trim();
		String lStrIR2Amt = StringUtility.getParameter("txtIR2Amt", request).trim();
		String lStrIR3Amt = StringUtility.getParameter("txtIR3Amt", request).trim();
		String lStrTIAmt = StringUtility.getParameter("txtTIPercentAmt", request).trim();
		String lStrTIArrearAmt = StringUtility.getParameter("txtTIArrearAmt", request).trim();
		String lStrOthArearAmt = StringUtility.getParameter("txtOthArrearAmt", request);
		String lStrCVPMonthly = StringUtility.getParameter("txtCvpMonthlyAmt", request).trim();
		String lStrGrossPnsnAmt = StringUtility.getParameter("txtPensionBillAmt", request).trim();
		String lStrRecovery = StringUtility.getParameter("txtPnsnRecovery", request).trim();
		String lStrNetPensionAmt = StringUtility.getParameter("txtNetPensionAmt", request).trim();
		String lStrArrearDtls = StringUtility.getParameter("arrearDtlsStr", request).trim();
		String lStrPeonAllowance = StringUtility.getParameter("txtPeonAllowanceAmt", request).trim();
		String lStrMedicalAllowance = StringUtility.getParameter("txtMedicalAllowenceAmt", request).trim();
		String lStrGallantryAmt = StringUtility.getParameter("txtGallantryAmt", request).trim();
		String lStrOtherBenefitAmt = StringUtility.getParameter("txtOtherBenefitAmt", request).trim();
		String lStrPensionCutAmt = StringUtility.getParameter("txtPensionCutAmount", request).trim();
		String lStrAllnBf11136Amt = StringUtility.getParameter("hidAllnBf11136", request).trim();
		String lStrAllnBf11156Amt = StringUtility.getParameter("hidAllnBf11156", request).trim();
		String lStrAllnAf11156Amt = StringUtility.getParameter("hidAllnAf11156", request).trim();
		String lStrAllnAf10560Amt = StringUtility.getParameter("hidAllnAf10560", request).trim();
		String lStrAllnAfZpAmt = StringUtility.getParameter("hidAllnAfZp", request).trim();
		String lStr6PCArrearAmt = StringUtility.getParameter("txt6PCArrearAmt", request).trim();
		String lStrCmmArrearAmt = StringUtility.getParameter("txtCommArrearAmt", request).trim();
		String lStrOthrDiffArrearAmt = StringUtility.getParameter("txtOthrDiffArrearAmt", request).trim();
		String lStrAccountNo = StringUtility.getParameter("hidAccountNo", request).trim();
		String lStrArrearLC = StringUtility.getParameter("hidArrearLC", request).trim();
		String lStrDaRate = StringUtility.getParameter("hidDaRate", request).trim();
		String lStrLedgerNo = StringUtility.getParameter("hidLedgerNo", request).trim();
		String lStrPageNo = StringUtility.getParameter("hidPageNo", request).trim();
		String lStrPunishmentCut = StringUtility.getParameter("hidPunishmentCut", request).trim();
		String lStrROPType = StringUtility.getParameter("hidROPType", request).trim();
		String lStrTotalArrearAmt = StringUtility.getParameter("hidTotalArrearAmt", request).trim();
		try {
			setSessionInfo(lMapInput);
			lObjTrnPensionBillDtls.setPpoNo(lStrPPONo);
			lObjTrnPensionBillDtls.setPensionerCode(lStrPensionerCode);
			lObjTrnPensionBillDtls.setPayForMonth(Integer.valueOf(lStrBillForMonth));
			lObjTrnPensionBillDtls.setPensionerName(lStrPnsnerName);
			lObjTrnPensionBillDtls.setPensionAmount(new BigDecimal(lStrBasicPension.length() != 0 ? lStrBasicPension : "0"));
			lObjTrnPensionBillDtls.setDpAmount(new BigDecimal(lStrDPAmt.length() != 0 ? lStrDPAmt : "0"));
			lObjTrnPensionBillDtls.setAdpAmount(new BigDecimal(lStrADPAmt.length() != 0 ? lStrADPAmt : "0"));
			lObjTrnPensionBillDtls.setTiAmount(new BigDecimal(lStrTIAmt.length() != 0 ? lStrTIAmt : "0"));
			lObjTrnPensionBillDtls.setIr1Amount(new BigDecimal(lStrIR1Amt.length() != 0 ? lStrIR1Amt : "0"));
			lObjTrnPensionBillDtls.setIr2Amount(new BigDecimal(lStrIR2Amt.length() != 0 ? lStrIR2Amt : "0"));
			lObjTrnPensionBillDtls.setIr3Amount(new BigDecimal(lStrIR3Amt.length() != 0 ? lStrIR3Amt : "0"));
			lObjTrnPensionBillDtls.setArrearAmount(new BigDecimal(lStrOthArearAmt.length() != 0 ? lStrOthArearAmt : "0"));
			lObjTrnPensionBillDtls.setTiArrearAmount(new BigDecimal(lStrTIArrearAmt.length() != 0 ? lStrTIArrearAmt : "0"));
			lObjTrnPensionBillDtls.setPeonAllowance(new BigDecimal(lStrPeonAllowance.length() != 0 ? lStrPeonAllowance : "0"));
			lObjTrnPensionBillDtls.setMedicalAllowenceAmount(new BigDecimal(lStrMedicalAllowance.length() != 0 ? lStrMedicalAllowance : "0"));
			lObjTrnPensionBillDtls.setOther1(new BigDecimal(lStrGallantryAmt.length() != 0 ? lStrGallantryAmt : "0"));
			lObjTrnPensionBillDtls.setOtherBenefits(new BigDecimal(lStrOtherBenefitAmt.length() != 0 ? lStrOtherBenefitAmt : "0"));
			lObjTrnPensionBillDtls.setPensnCutAmount(new BigDecimal(lStrPensionCutAmt.length() != 0 ? lStrPensionCutAmt : "0"));
			lObjTrnPensionBillDtls.setCvpMonthAmount(new BigDecimal(lStrCVPMonthly.length() != 0 ? lStrCVPMonthly : "0"));
			lObjTrnPensionBillDtls.setGrossAmount(new BigDecimal(lStrGrossPnsnAmt.length() != 0 ? lStrGrossPnsnAmt : "0"));
			lObjTrnPensionBillDtls.setRecoveryAmount(new BigDecimal(lStrRecovery.length() != 0 ? lStrRecovery : "0"));
			lObjTrnPensionBillDtls.setNetAmount(new BigDecimal(lStrNetPensionAmt.length() != 0 ? lStrNetPensionAmt : "0"));
			lObjTrnPensionBillDtls.setAllcationBf1436(new BigDecimal(lStrAllnBf11136Amt.length() != 0 ? lStrAllnBf11136Amt : "0"));
			lObjTrnPensionBillDtls.setAllcationBf11156(new BigDecimal(lStrAllnBf11156Amt.length() != 0 ? lStrAllnBf11156Amt : "0"));
			lObjTrnPensionBillDtls.setAllcationAf11156(new BigDecimal(lStrAllnAf11156Amt.length() != 0 ? lStrAllnAf11156Amt : "0"));
			lObjTrnPensionBillDtls.setAllcationAf10560(new BigDecimal(lStrAllnAf10560Amt.length() != 0 ? lStrAllnAf10560Amt : "0"));
			lObjTrnPensionBillDtls.setAllcationAfZp(new BigDecimal(lStrAllnAfZpAmt.length() != 0 ? lStrAllnAfZpAmt : "0"));
			lObjTrnPensionBillDtls.setArrear6PC(new BigDecimal(lStr6PCArrearAmt.length() != 0 ? lStr6PCArrearAmt : "0"));
			lObjTrnPensionBillDtls.setArrearCommutation(new BigDecimal(lStrCmmArrearAmt.length() != 0 ? lStrCmmArrearAmt : "0"));
			lObjTrnPensionBillDtls.setArrearOthrDiff(new BigDecimal(lStrOthrDiffArrearAmt.length() != 0 ? lStrOthrDiffArrearAmt : "0"));
			Double lReducedPension = 0d;
			lReducedPension = new Double(lStrBasicPension.length() != 0 ? lStrBasicPension : "0");
			lReducedPension += new Double(lStrDPAmt.length() != 0 ? lStrDPAmt : "0");
			lReducedPension -= new Double(lStrCVPMonthly.length() != 0 ? lStrCVPMonthly : "0");
			lObjTrnPensionBillDtls.setReducedPension(new BigDecimal(lReducedPension));
			lObjTrnPensionBillDtls.setTrnCounter(1);
			lObjTrnPensionBillDtls.setArrearDtls(lStrArrearDtls);
			lObjTrnPensionBillDtls.setAccountNo(lStrAccountNo);
			lObjTrnPensionBillDtls.setArrearLC(new BigDecimal(lStrArrearLC.length() != 0 ? lStrArrearLC : "0"));
			lObjTrnPensionBillDtls.setDaRate(new BigDecimal(lStrDaRate.length() != 0 ? lStrDaRate : "0"));
			lObjTrnPensionBillDtls.setLedgerNo(lStrLedgerNo);
			lObjTrnPensionBillDtls.setPageNo(lStrPageNo);
			lObjTrnPensionBillDtls.setPunishmentCutAmt(new BigDecimal(lStrPunishmentCut.length() != 0 ? lStrPunishmentCut : "0"));
			lObjTrnPensionBillDtls.setRopType(lStrROPType);
			lObjTrnPensionBillDtls.setTotalArrearAmt(new BigDecimal(lStrTotalArrearAmt.length() != 0 ? lStrTotalArrearAmt : "0"));
		} catch (Exception e) {
			gLogger.error("Error in" + e, e);
			throw (e);
		}
		return lObjTrnPensionBillDtls;
	}

	/**
	 * Generate TrnPensionBillDtlsVO from JSP Data
	 * 
	 * @param lMapInput
	 * @return TrnPensionBillDtls
	 */
	public List<TrnPensionArrearDtls> generateTrnPensionArrearDtlsVO(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");

		List<TrnPensionArrearDtls> lPensionArrearDtlsVoLst = new ArrayList<TrnPensionArrearDtls>();
		TrnPensionArrearDtls lArrearDtlsVo = null;

		String[] lStrArrearFieldType = StringUtility.getParameterValues("hidArrFieldType", request);
		String[] lStrArrearWEF = StringUtility.getParameterValues("hidArrWEF", request);
		String[] lStrArrearDiffAmt = StringUtility.getParameterValues("hidArrDiffAmt", request);
		String[] lStrArrearPAY = StringUtility.getParameterValues("hidArrPAY", request);

		String lStrTrnPnsnRqstID = StringUtility.getParameter("hidTrnPensionRqstID", request).trim();
		String lStrPnsnerCode = StringUtility.getParameter("hidPensionerCode", request).trim();
		// String lStrBillForMonth =
		// StringUtility.getParameter("hidBillForMnth", request);

		String lStrArrPnsn = bundleConst.getString("ARREAR.PENSION");

		try {
			setSessionInfo(lMapInput);
			for (int i = 0; i < lStrArrearFieldType.length; i++) {
				// Arrear For Pension
				lArrearDtlsVo = new TrnPensionArrearDtls();

				lArrearDtlsVo.setPensionRequestId(Long.parseLong(lStrTrnPnsnRqstID));
				lArrearDtlsVo.setPensionerCode(lStrPnsnerCode);
				lArrearDtlsVo.setArrearFieldType(lStrArrearFieldType[i]);

				lArrearDtlsVo.setOldAmountPercentage(new BigDecimal("0.00"));
				lArrearDtlsVo.setNewAmountPercentage(new BigDecimal(lStrArrearDiffAmt[i].length() != 0 ? lStrArrearDiffAmt[i] : "0.00"));

				lArrearDtlsVo.setEffectedFromYyyymm(Integer.parseInt(lStrArrearWEF[i]));
				lArrearDtlsVo.setEffectedToYyyymm(Integer.parseInt(lStrArrearWEF[i]));

				lArrearDtlsVo.setDifferenceAmount(new BigDecimal(lStrArrearDiffAmt[i].length() != 0 ? lStrArrearDiffAmt[i] : "0.00"));
				lArrearDtlsVo.setTotalDifferenceAmt(new BigDecimal(lStrArrearDiffAmt[i].length() != 0 ? lStrArrearDiffAmt[i] : "0.00"));

				lArrearDtlsVo.setPaymentFromYyyymm(Integer.parseInt(lStrArrearPAY[i]));
				lArrearDtlsVo.setPaymentToYyyymm(Integer.parseInt(lStrArrearPAY[i]));

				lArrearDtlsVo.setCreatedUserId(new BigDecimal(gLngUserId));
				lArrearDtlsVo.setCreatedPostId(new BigDecimal(gLngPostId));
				lArrearDtlsVo.setCreatedDate(gDtCurrDt);

				/* Logic add For Calculate a LTA */
				if (lStrArrearFieldType[i].equalsIgnoreCase(lStrArrPnsn)) {
					lLtaAmount += Double.valueOf(lStrArrearDiffAmt[i].toString());
					/* Month vise NetAmount paid as Arrear */
				}

				lPensionArrearDtlsVoLst.add(lArrearDtlsVo);
			}

		} catch (Exception e) {
			gLogger.error("Error in" + e, e);
			throw (e);
		}
		return lPensionArrearDtlsVoLst;
	}

	/**
	 * Generate TrnPensionArrearDtls from Previouse Month Data
	 * 
	 * @param lMapInput
	 * @return TrnPensionArrearDtls
	 */
	public List<TrnPensionArrearDtls> generateTrnPaymentArrearDtlsVO(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		List<TrnPensionArrearDtls> lTrnPaymentLst = null;
		List<TrnPensionArrearDtls> lPensionArrearDtlsVoLst = null;
		TrnPensionArrearDtls lArrearDtlsVo = null;

		Double lDiifAmt = 0D;
		Integer lTrnForMnth = 0;
		Integer lTrnPayMnth = 0;

		try {
			// Fetching & Remove Attribuites From session
			HttpSession session = request.getSession();
			lTrnPaymentLst = (List<TrnPensionArrearDtls>) session.getAttribute("PBTrnPaymentLst");

			if (lTrnPaymentLst != null && !lTrnPaymentLst.isEmpty()) {
				lPensionArrearDtlsVoLst = new ArrayList<TrnPensionArrearDtls>();
				for (int i = 0; i < lTrnPaymentLst.size(); i++) {
					// Arrear For Pension
					lArrearDtlsVo = new TrnPensionArrearDtls();
					lArrearDtlsVo = lTrnPaymentLst.get(i);

					lDiifAmt = Double.valueOf(lArrearDtlsVo.getDifferenceAmount().toString());
					lTrnForMnth = lArrearDtlsVo.getEffectedFromYyyymm();
					lTrnPayMnth = lArrearDtlsVo.getPaymentFromYyyymm();

					lArrearDtlsVo.setOldAmountPercentage(new BigDecimal("0.00"));
					lArrearDtlsVo.setNewAmountPercentage(new BigDecimal(lDiifAmt != 0 ? lDiifAmt.toString() : "0.00"));
					lArrearDtlsVo.setEffectedToYyyymm(lTrnForMnth);
					lArrearDtlsVo.setDifferenceAmount(new BigDecimal(lDiifAmt != 0 ? lDiifAmt.toString() : "0.00"));
					lArrearDtlsVo.setTotalDifferenceAmt(new BigDecimal(lDiifAmt != 0 ? lDiifAmt.toString() : "0.00"));
					lArrearDtlsVo.setPaymentToYyyymm(lTrnPayMnth);
					lArrearDtlsVo.setCreatedUserId(new BigDecimal(gLngUserId));
					lArrearDtlsVo.setCreatedPostId(new BigDecimal(gLngPostId));
					lArrearDtlsVo.setCreatedDate(gDtCurrDt);

					lPensionArrearDtlsVoLst.add(lArrearDtlsVo);
				}
			}

			session.removeAttribute("PBTrnPaymentLst");
		} catch (Exception e) {
			gLogger.error("Error in" + e, e);
			throw (e);
		}
		return lPensionArrearDtlsVoLst;
	}

	/**
	 * Generate TrnPensionRqstHdrVO
	 * 
	 * @param lMapInput
	 * @return TrnPensionRqstHdr
	 */
	/*
	 * public TrnPensionRqstHdr generatePensionRqstHdrVO(Map lMapInput) throws
	 * Exception { HttpServletRequest request = (HttpServletRequest)
	 * lMapInput.get("requestObj"); ServiceLocator srvcLoc = (ServiceLocator)
	 * lMapInput.get("serviceLocator");
	 * 
	 * TrnPensionRqstHdrDAO lObjPensionRqstHdrDAO = new
	 * TrnPensionRqstHdrDAOImpl(
	 * TrnPensionRqstHdr.class,srvcLoc.getSessionFactory()); TrnPensionRqstHdr
	 * lObjPensionRqstHdrVo = null;
	 * 
	 * String lStrTrnPnsnRqstID =
	 * StringUtility.getParameter("hidTrnPensionRqstID", request).trim();
	 * 
	 * String lNewROPBasic = StringUtility.getParameter("hidNewROPBasic",
	 * request).trim(); String lNewROPFP1 =
	 * StringUtility.getParameter("hidNewROPFP1", request).trim(); String
	 * lNewROPFP2 = StringUtility.getParameter("hidNewROPFP2", request).trim();
	 * 
	 * try { lObjPensionRqstHdrVo =
	 * lObjPensionRqstHdrDAO.read(Long.valueOf(lStrTrnPnsnRqstID));
	 * 
	 * lObjPensionRqstHdrVo.setBasicPensionAmount(new
	 * BigDecimal(lNewROPBasic.length() != 0 ? lNewROPBasic : "0.00"));
	 * lObjPensionRqstHdrVo.setFp1Amount(new BigDecimal(lNewROPFP1.length() != 0
	 * ? lNewROPFP1 : "0.00")); lObjPensionRqstHdrVo.setFp2Amount(new
	 * BigDecimal(lNewROPFP2.length() != 0 ? lNewROPFP2 : "0.00"));
	 * 
	 * } catch (Exception e) { gLogger.error("Error in " + e,e); throw(e); }
	 * return lObjPensionRqstHdrVo; }
	 */

	/**
	 * Generate TrnPensionBillArrearDtlsVO.
	 * 
	 * @param lMapInput
	 * @return TrnPensionBillDtls List
	 */
	public List<TrnPensionBillDtls> generateTrnPnsnBillArrearDtlsLst(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");

		List<MonthlyPensionBillVO> lMonthlyArrearDtlsLst = null;
		List<TrnPensionBillDtls> lPnsnBillArrDtlsLst = null;
		MonthlyPensionBillVO lPensionBillVO = null;
		TrnPensionBillDtls lPnsnBillArrearDtlsVO = null;

		try {
			setSessionInfo(lMapInput);

			// Fetching & Remove Attribuites From session
			HttpSession session = request.getSession();
			lMonthlyArrearDtlsLst = (List<MonthlyPensionBillVO>) session.getAttribute("PBMonthlyArrearVoLst");

			if (lMonthlyArrearDtlsLst != null && !lMonthlyArrearDtlsLst.isEmpty()) {
				lPnsnBillArrDtlsLst = new ArrayList<TrnPensionBillDtls>();
				for (int i = 0; i < lMonthlyArrearDtlsLst.size(); i++) {
					// Arrear For Pension
					lPensionBillVO = new MonthlyPensionBillVO();
					lPnsnBillArrearDtlsVO = new TrnPensionBillDtls();
					lPensionBillVO = lMonthlyArrearDtlsLst.get(i);

					lPnsnBillArrearDtlsVO.setPpoNo(lPensionBillVO.getPpoNo());
					lPnsnBillArrearDtlsVO.setPensionerCode(lPensionBillVO.getPensionerCode());
					lPnsnBillArrearDtlsVO.setPayForMonth(lPensionBillVO.getForMonth());

					/*
					 * lPnsnBillArrearDtlsVO.setPensionAmount(lPensionBillVO.
					 * getBasicPensionAmount ());
					 * lPnsnBillArrearDtlsVO.setPensnCutAmount(lPensionBillVO
					 * .getPensionCutAmount());
					 * lPnsnBillArrearDtlsVO.setDpAmount
					 * (lPensionBillVO.getDpPercentAmount());
					 * lPnsnBillArrearDtlsVO
					 * .setAdpAmount(lPensionBillVO.getAdpAmount());
					 * lPnsnBillArrearDtlsVO
					 * .setTiAmount(lPensionBillVO.getTiPercentAmount());
					 * lPnsnBillArrearDtlsVO
					 * .setCvpMonthAmount(lPensionBillVO.getCvpMonthlyAmount());
					 * lPnsnBillArrearDtlsVO
					 * .setMedicalAllowenceAmount(lPensionBillVO
					 * .getMedicalAllowenceAmount());
					 * lPnsnBillArrearDtlsVO.setTiArrearAmount
					 * (lPensionBillVO.getTIArrearsAmount());
					 * lPnsnBillArrearDtlsVO
					 * .setOtherBenefits(lPensionBillVO.getOtherBenefit());
					 * lPnsnBillArrearDtlsVO
					 * .setPersonalPensionAmount(lPensionBillVO
					 * .getPersonalPension());
					 * lPnsnBillArrearDtlsVO.setIrAmount(
					 * lPensionBillVO.getIr());
					 * lPnsnBillArrearDtlsVO.setIncomeTaxCutAmount
					 * (lPensionBillVO.getItCutAmount());
					 * lPnsnBillArrearDtlsVO.setSpecialCutAmount
					 * (lPensionBillVO.getSpecialCutAmount());
					 * lPnsnBillArrearDtlsVO
					 * .setArrearAmount(lPensionBillVO.getOtherArrearsAmount());
					 * lPnsnBillArrearDtlsVO
					 * .setRecoveryAmount(lPensionBillVO.getRecoveryAmount());
					 * lPnsnBillArrearDtlsVO.setOmr(lPensionBillVO.getOMR());
					 * lPnsnBillArrearDtlsVO
					 * .setReducedPension(lPensionBillVO.getNetPensionAmount());
					 */

					String lStrFromPayDate = new SimpleDateFormat("dd/MM/yyyy").format(lPensionBillVO.getFromDate());
					String lStrToPayDate = new SimpleDateFormat("dd/MM/yyyy").format(lPensionBillVO.getToDate());
					lPnsnBillArrearDtlsVO.setFromDate(new SimpleDateFormat("dd/MM/yyyy").parse(lStrFromPayDate));
					lPnsnBillArrearDtlsVO.setToDate(new SimpleDateFormat("dd/MM/yyyy").parse(lStrToPayDate));

					lPnsnBillArrDtlsLst.add(lPnsnBillArrearDtlsVO);
				}

			}

			session.removeAttribute("PBMonthlyArrearVoLst");
		} catch (Exception e) {
			gLogger.error("Error in" + e, e);
			throw (e);
		}

		return lPnsnBillArrDtlsLst;
	}

	private void setSessionInfo(Map lMapInput) {

		gLngPostId = SessionHelper.getPostId(lMapInput);
		gLngUserId = SessionHelper.getUserId(lMapInput);
		gStrLocCode = SessionHelper.getLocationCode(lMapInput);
		gDtCurrDt = DBUtility.getCurrentDateFromDB();
	}

}