package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
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
import com.tcs.sgv.common.valueobject.BillEdpVO;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.BudgetDtlsDAO;
import com.tcs.sgv.pensionpay.dao.BudgetDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MonthlyPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillReceipt;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionOtherPartyPay;


public class MonthlyPensionBillVOGenerator extends ServiceImpl implements VOGeneratorService {

	/* Global Variable for PostId */
	private Long gLngPostId = null;

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Global Variable for Current Date */
	private Date gDtCurrDt = null;

	/* Global Variable for Logger Class */
	private Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for Location Code */
	private String gStrLocCode = null;

	/**
	 * It generates Monthly Pension Bill Specific VOs.
	 * 
	 * @param objectArgs
	 *            ServiceMap
	 * @return objRes ResultObject
	 */

	public ResultObject generateMap(Map objArgs) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List<TrnPensionBillDtls> lPnsnBillArrearDtlsLst = null;

		try {
			setSessionInfo(objArgs);

			// Generate VO of Pension bill hdr
			List<TrnPensionBillHdr> lObjPensionBillHdr = generateTrnPensionBillHdr(objArgs);

			// generate List of Vos of Pension bill dtls
			List<TrnPensionBillDtls> lLstPensionBillDtls = generateTrnPensionBillDtls(objArgs);

			// generate Receipt and Recovery Amount into
			// Trn_Pension_bill_Receipt
			List<TrnPensionBillReceipt> pnsnRcptVOlist = generateBillReceiptDtls(objArgs);

			// generate list of Vos of Pension arrear dtls
			List<TrnPensionArrearDtls> lLstArrearDtls = generateTrnPensionArrearDtls(objArgs);

			// Generate VO List For Pending monthly Details for Pension bill
			// dtls
			lPnsnBillArrearDtlsLst = generateTrnPnsnBillArrearDtlsLst(objArgs);

			List<TrnPensionOtherPartyPay> lLstothPartyPay = generateTrnPensionOtherPartyPay(objArgs);

			objArgs.put("TrnPensionBillHdr", lObjPensionBillHdr);
			objArgs.put("TrnPensionBillDtls", lLstPensionBillDtls);
			objArgs.put("PnsnRcptVOlist", pnsnRcptVOlist);
			objArgs.put("TrnPensionArrearDtls", lLstArrearDtls);
			objArgs.put("PnsnBillArrearDtlsLst", lPnsnBillArrearDtlsLst);
			objArgs.put("lLstothPartyPay", lLstothPartyPay);
			objRes.setResultValue(objArgs);
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;

	}

	/**
	 * It generates VO of TrnPensionBillHdr
	 * 
	 * @param objectArgs
	 *            ServiceMap
	 * @return {@link TrnPensionBillHdr}
	 */
	private List<TrnPensionBillHdr> generateTrnPensionBillHdr(Map<String, Object> lMapInput) throws Exception {

		List<TrnPensionBillHdr> lPensionBillHdrLst = null;
		TrnPensionBillHdr lObjPensionBillHdr = null;
		List lHeadCodeWiseDtlsLst = null;
		List lCVPHeadCodeWiseDtlsLst = null;
		List lDCRGHeadCodeWiseDtlsLst = null;
		BillEdpVO lITBillEdpVO = null;
		List lHeadCodeWiseITCutAmt = null;
		ServiceLocator servLoc = (ServiceLocator) lMapInput.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
		String lStrTokenNo = null;

		try {
			// String lStrBillNo=lMapInput.get("billNo").toString();
			if (lMapInput.get("PnsnTokenNo") != null) {
				lStrTokenNo = lMapInput.get("PnsnTokenNo").toString();
			}
			String lStrBillType = bundleConst.getString("RECOVERY.MONTHLY");
			String lStrCVPBillType = bundleConst.getString("RECOVERY.CVP");
			String lStrDCRGBillType = bundleConst.getString("RECOVERY.DCRG");
			String lStrForMonth = StringUtility.getParameter("hidforMonth", request).trim();
			// String lStrHeadCode = StringUtility.getParameter("hidHeadCode",
			// request).trim();
			String lStrBank = StringUtility.getParameter("hidBank", request).trim();
			String lStrBranch = StringUtility.getParameter("hidBranch", request).trim();
			String lStrScheme = StringUtility.getParameter("hidScheme", request).trim();

			// Head Code wise Bill Header VO Generate
			HttpSession session = request.getSession();
			lHeadCodeWiseDtlsLst = (List) session.getAttribute("HeadCodeWiseDtls");
			lCVPHeadCodeWiseDtlsLst = (List) session.getAttribute("CVPHeadCodeWiseDtls");
			lDCRGHeadCodeWiseDtlsLst = (List) session.getAttribute("DCRGHeadCodeWiseDtls");
			session.removeAttribute("HeadCodeWiseDtls");
			session.removeAttribute("CVPHeadCodeWiseDtls");
			session.removeAttribute("DCRGHeadCodeWiseDtls");

			if (lHeadCodeWiseDtlsLst != null && !lHeadCodeWiseDtlsLst.isEmpty()) {
				lPensionBillHdrLst = new ArrayList<TrnPensionBillHdr>();
				lHeadCodeWiseITCutAmt = new ArrayList();
				for (Object lArrObj : lHeadCodeWiseDtlsLst) {
					lObjPensionBillHdr = new TrnPensionBillHdr();

					List lArrList = (List) lArrObj;

					lObjPensionBillHdr.setHeadCode(new BigDecimal(lArrList.get(0).toString()));
					lObjPensionBillHdr.setGrossAmount(new BigDecimal(lArrList.get(2).toString()));
					lObjPensionBillHdr.setDeductionA(new BigDecimal(lArrList.get(3).toString()));
					lObjPensionBillHdr.setDeductionB(new BigDecimal(lArrList.get(4).toString()));
					lObjPensionBillHdr.setNetAmount(new BigDecimal(lArrList.get(5).toString()));
					lObjPensionBillHdr.setRejected(0);

					// It cut Amount Set when headcode wise details Map
					// generated.
					if (lArrList.get(6) != null && Long.valueOf(lArrList.get(6).toString()) > 0) {
						BudgetDtlsDAO lObjBudgetDtlsDAO = new BudgetDtlsDAOImpl(servLoc.getSessionFactory());
						if (lITBillEdpVO == null) {
							lITBillEdpVO = lObjBudgetDtlsDAO.getITHeadStructureDtl(SessionHelper.getLangId(lMapInput), SessionHelper.getFinYrId(lMapInput));
						}

						TrnPensionBillReceipt lPensionBillReceiptVo = new TrnPensionBillReceipt();

						lPensionBillReceiptVo.setTrnPensionBillHdrId(Long.valueOf("90" + lArrList.get(0).toString())); // HeadCode
						lPensionBillReceiptVo.setEdpCode(lITBillEdpVO.getEdpCode());
						lPensionBillReceiptVo.setDedType(lITBillEdpVO.getEdpCategory());
						lPensionBillReceiptVo.setMajorHead(lITBillEdpVO.getBudmjrHd());
						lPensionBillReceiptVo.setSubmajorHead(lITBillEdpVO.getBudsubmjrHd());
						lPensionBillReceiptVo.setMinorHead(lITBillEdpVO.getBudminHd());
						lPensionBillReceiptVo.setSubhead(lITBillEdpVO.getBudsubHd());
						lPensionBillReceiptVo.setAmount(new BigDecimal(lArrList.get(6).toString())); // IT
																										// Cut
																										// Amt
																										// For
																										// HeadCode

						lHeadCodeWiseITCutAmt.add(lPensionBillReceiptVo);
					}

					lObjPensionBillHdr.setBillType(lStrBillType);
					// lObjPensionBillHdr.setBillNo(lStrBillNo);
					if (lStrTokenNo != null) {
						lObjPensionBillHdr.setTokenNo(lStrTokenNo);
					}

					lObjPensionBillHdr.setForMonth(Integer.parseInt(lStrForMonth));
					lObjPensionBillHdr.setBankCode(lStrBank);
					lObjPensionBillHdr.setBranchCode(lStrBranch);
					lObjPensionBillHdr.setScheme(lStrScheme);
					lObjPensionBillHdr.setRejected(0);
					lObjPensionBillHdr.setBillDate(gDtCurrDt);
					lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjPensionBillHdr.setCreatedDate(new Date(System.currentTimeMillis()));
					lObjPensionBillHdr.setTrnCounter(1);
					lObjPensionBillHdr.setLocationCode(gStrLocCode);

					lPensionBillHdrLst.add(lObjPensionBillHdr);
				}

				// Generated IT Cut Details Vo's For Receipt into Map. will be
				// use in TrePensionRecpt Vos Generation
				if (lHeadCodeWiseITCutAmt != null && !lHeadCodeWiseITCutAmt.isEmpty()) {
					lMapInput.put("HeadCodeWiseITCutAmt", lHeadCodeWiseITCutAmt);
				}
			}

			if (lCVPHeadCodeWiseDtlsLst != null && !lCVPHeadCodeWiseDtlsLst.isEmpty()) {
				for (Object lArrObj : lCVPHeadCodeWiseDtlsLst) {
					lObjPensionBillHdr = new TrnPensionBillHdr();

					List lArrList = (List) lArrObj;

					lObjPensionBillHdr.setHeadCode(new BigDecimal(lArrList.get(0).toString()));
					lObjPensionBillHdr.setGrossAmount(new BigDecimal(lArrList.get(2).toString()));
					lObjPensionBillHdr.setDeductionA(new BigDecimal(lArrList.get(3).toString()));
					lObjPensionBillHdr.setDeductionB(new BigDecimal(lArrList.get(4).toString()));
					lObjPensionBillHdr.setNetAmount(new BigDecimal(lArrList.get(5).toString()));
					lObjPensionBillHdr.setRejected(0);
					lObjPensionBillHdr.setBillType(lStrCVPBillType);
					// lObjPensionBillHdr.setBillNo(lStrBillNo);
					if (lStrTokenNo != null) {
						lObjPensionBillHdr.setTokenNo(lStrTokenNo);
					}

					lObjPensionBillHdr.setForMonth(Integer.parseInt(lStrForMonth));
					lObjPensionBillHdr.setBankCode(lStrBank);
					lObjPensionBillHdr.setBranchCode(lStrBranch);
					lObjPensionBillHdr.setScheme(lStrScheme);

					lObjPensionBillHdr.setBillDate(gDtCurrDt);
					lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjPensionBillHdr.setCreatedDate(new Date(System.currentTimeMillis()));
					lObjPensionBillHdr.setTrnCounter(1);
					lObjPensionBillHdr.setLocationCode(gStrLocCode);

					lPensionBillHdrLst.add(lObjPensionBillHdr);
				}
			}

			if (lDCRGHeadCodeWiseDtlsLst != null && !lDCRGHeadCodeWiseDtlsLst.isEmpty()) {
				for (Object lArrObj : lDCRGHeadCodeWiseDtlsLst) {
					lObjPensionBillHdr = new TrnPensionBillHdr();

					List lArrList = (List) lArrObj;

					lObjPensionBillHdr.setHeadCode(new BigDecimal(lArrList.get(0).toString()));
					lObjPensionBillHdr.setGrossAmount(new BigDecimal(lArrList.get(2).toString()));
					lObjPensionBillHdr.setDeductionA(new BigDecimal(lArrList.get(3).toString()));
					lObjPensionBillHdr.setDeductionB(new BigDecimal(lArrList.get(4).toString()));
					lObjPensionBillHdr.setNetAmount(new BigDecimal(lArrList.get(5).toString()));
					lObjPensionBillHdr.setRejected(0);
					lObjPensionBillHdr.setBillType(lStrDCRGBillType);
					// lObjPensionBillHdr.setBillNo(lStrBillNo);
					if (lStrTokenNo != null) {
						lObjPensionBillHdr.setTokenNo(lStrTokenNo);
					}

					lObjPensionBillHdr.setForMonth(Integer.parseInt(lStrForMonth));
					lObjPensionBillHdr.setBankCode(lStrBank);
					lObjPensionBillHdr.setBranchCode(lStrBranch);
					lObjPensionBillHdr.setScheme(lStrScheme);

					lObjPensionBillHdr.setBillDate(gDtCurrDt);
					lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjPensionBillHdr.setCreatedDate(new Date(System.currentTimeMillis()));
					lObjPensionBillHdr.setTrnCounter(1);
					lObjPensionBillHdr.setLocationCode(gStrLocCode);

					lPensionBillHdrLst.add(lObjPensionBillHdr);
				}
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lPensionBillHdrLst;
	}

	/**
	 * It generates Pension Bill Specific TrnPensionBillDtls VO.
	 * 
	 * @param lMapInput
	 *            InputMap.
	 * @return lLstTrnPensionBillDtls List<TrnPensionBillDtls>
	 * @author Aparna
	 */
	// Changed By sagar For improve a performance of bill Saving.
	private List<TrnPensionBillDtls> generateTrnPensionBillDtls(Map<String, Object> lMapInput) throws Exception {

		List<TrnPensionBillDtls> lLstTrnPensionBillDtls = new ArrayList<TrnPensionBillDtls>();
		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

		String lStrForMonth = null;
		MonthlyPensionBillVO lPensionBillVO = null;
		List lMonthlyPensionVOLst = null;
		List lCVPVOLst = null;
		List lDCRGVOLst = null;

		TrnPensionBillDtls lObjPensionBillDtls = null;

		try {

			// All Pensioners Monthly Payment Details
			HttpSession session = request.getSession();
			lMonthlyPensionVOLst = (List) session.getAttribute("MonthlyPensionList");
			lCVPVOLst = (List) session.getAttribute("CVPList");
			lDCRGVOLst = (List) session.getAttribute("DCRGList");
			// session.removeAttribute("MonthlyPensionList");
			// session.removeAttribute("CVPList");
			// session.removeAttribute("DCRGList");

			lStrForMonth = StringUtility.getParameter("hidforMonth", request);

			if (lMonthlyPensionVOLst != null && !lMonthlyPensionVOLst.isEmpty()) {

				for (Integer x = 0; x < lMonthlyPensionVOLst.size(); x++) {
					lPensionBillVO = (MonthlyPensionBillVO) lMonthlyPensionVOLst.get(x);
					lObjPensionBillDtls = new TrnPensionBillDtls();

					// Set Headcode for make easy to replace headoce wise
					// BillHdrIDPK comes after saving bill in Service.
					lObjPensionBillDtls.setTrnPensionBillHdrId(Long.valueOf("90" + lPensionBillVO.getHeadCode().toString()));

					lObjPensionBillDtls.setPpoNo(lPensionBillVO.getPpoNo());
					lObjPensionBillDtls.setPensionerCode(lPensionBillVO.getPensionerCode());
					lObjPensionBillDtls.setPayForMonth(Integer.valueOf(lStrForMonth));
					// lObjPensionBillDtls.setIncomeTaxCutAmount(lPensionBillVO.getItCutAmount());
					// lObjPensionBillDtls.setPensnCutAmount(lPensionBillVO.getPensionCutAmount());
					// lObjPensionBillDtls.setSpecialCutAmount(lPensionBillVO.getSpecialCutAmount());
					// lObjPensionBillDtls.setCvpMonthAmount(lPensionBillVO.getCvpMonthlyAmount());
					// lObjPensionBillDtls.setPensionAmount(lPensionBillVO.getBasicPensionAmount());
					// lObjPensionBillDtls.setDpAmount(lPensionBillVO.getDpPercentAmount());
					// lObjPensionBillDtls.setAdpAmount(lPensionBillVO.getAdpAmount());
					// lObjPensionBillDtls.setTiAmount(lPensionBillVO.getTiPercentAmount());
					// lObjPensionBillDtls.setMedicalAllowenceAmount(lPensionBillVO.getMedicalAllowenceAmount());
					// lObjPensionBillDtls.setTiArrearAmount(lPensionBillVO.getTIArrearsAmount());
					// lObjPensionBillDtls.setArrearAmount(lPensionBillVO.getOtherArrearsAmount());
					// lObjPensionBillDtls.setRecoveryAmount(lPensionBillVO.getRecoveryAmount());
					// lObjPensionBillDtls.setReducedPension(lPensionBillVO.getNetPensionAmount());
					// lObjPensionBillDtls.setAccountNo(lPensionBillVO.getAccountNo());
					// lObjPensionBillDtls.setPensionerName(lPensionBillVO.getPensionerName());
					// lObjPensionBillDtls.setAllcationBf11156(lPensionBillVO.getAllnBf11156());
					// lObjPensionBillDtls.setAllcationAf11156(lPensionBillVO.getAllnAf11156());
					// lObjPensionBillDtls.setAllcationAf10560(lPensionBillVO.getAllnAf10560());
					// lObjPensionBillDtls.setPersonalPensionAmount(lPensionBillVO.getPersonalPension());
					// lObjPensionBillDtls.setIrAmount(lPensionBillVO.getIr());
					// lObjPensionBillDtls.setMoCommission(lPensionBillVO.getMOComm());
					// lObjPensionBillDtls.setOtherBenefits(lPensionBillVO.getOtherBenefit());
					// lObjPensionBillDtls.setOmr(lPensionBillVO.getOMR());
					lObjPensionBillDtls.setTrnCounter(1);
					lObjPensionBillDtls.setFromDate(lPensionBillVO.getFromDate());
					lObjPensionBillDtls.setToDate(lPensionBillVO.getToDate());

					lLstTrnPensionBillDtls.add(lObjPensionBillDtls);
				}
			}

			if (lCVPVOLst != null && !lCVPVOLst.isEmpty()) {

				for (Integer x = 0; x < lCVPVOLst.size(); x++) {
					lPensionBillVO = (MonthlyPensionBillVO) lCVPVOLst.get(x);
					lObjPensionBillDtls = new TrnPensionBillDtls();

					// Set Headcode for make easy to replace headoce wise
					// BillHdrIDPK comes after saving bill in Service.
					lObjPensionBillDtls.setTrnPensionBillHdrId(Long.valueOf("110" + lPensionBillVO.getHeadCode().toString()));

					lObjPensionBillDtls.setPpoNo(lPensionBillVO.getPpoNo());
					lObjPensionBillDtls.setPensionerCode(lPensionBillVO.getPensionerCode());
					lObjPensionBillDtls.setPayForMonth(Integer.valueOf(lStrForMonth));
					// lObjPensionBillDtls.setPensionAmount(lPensionBillVO.getBasicPensionAmount());
					// lObjPensionBillDtls.setRecoveryAmount(lPensionBillVO.getRecoveryAmount());
					// lObjPensionBillDtls.setReducedPension(lPensionBillVO.getNetPensionAmount());
					lObjPensionBillDtls.setAccountNo(lPensionBillVO.getAccountNo());
					lObjPensionBillDtls.setPensionerName(lPensionBillVO.getPensionerName());
					lObjPensionBillDtls.setTrnCounter(1);
					lLstTrnPensionBillDtls.add(lObjPensionBillDtls);
				}
			}

			if (lDCRGVOLst != null && !lDCRGVOLst.isEmpty()) {

				for (Integer x = 0; x < lDCRGVOLst.size(); x++) {
					lPensionBillVO = (MonthlyPensionBillVO) lDCRGVOLst.get(x);
					lObjPensionBillDtls = new TrnPensionBillDtls();

					// Set Headcode for make easy to replace headoce wise
					// BillHdrIDPK comes after saving bill in Service.
					lObjPensionBillDtls.setTrnPensionBillHdrId(Long.valueOf("100" + lPensionBillVO.getHeadCode().toString()));

					lObjPensionBillDtls.setPpoNo(lPensionBillVO.getPpoNo());
					lObjPensionBillDtls.setPensionerCode(lPensionBillVO.getPensionerCode());
					lObjPensionBillDtls.setPayForMonth(Integer.valueOf(lStrForMonth));
					// lObjPensionBillDtls.setPensionAmount(lPensionBillVO.getBasicPensionAmount());
					// lObjPensionBillDtls.setRecoveryAmount(lPensionBillVO.getRecoveryAmount());
					// lObjPensionBillDtls.setReducedPension(lPensionBillVO.getNetPensionAmount());
					lObjPensionBillDtls.setAccountNo(lPensionBillVO.getAccountNo());
					lObjPensionBillDtls.setPensionerName(lPensionBillVO.getPensionerName());
					lObjPensionBillDtls.setTrnCounter(1);
					lLstTrnPensionBillDtls.add(lObjPensionBillDtls);
				}
			}

			if ((lCVPVOLst != null && !lCVPVOLst.isEmpty()) || (lDCRGVOLst != null && !lDCRGVOLst.isEmpty())) {
				// there is some record for either/both CVP and DCRG
				// hence need to get the list of rlt_pensioncase_bill_ids
				StringBuffer PpoNoList = (StringBuffer) session.getAttribute("PpoNoList");
				session.removeAttribute("PpoNoList");
				lMapInput.put("PpoNoList", PpoNoList);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstTrnPensionBillDtls;
	}

	/**
	 * Method Generate a headcode wise Receipt Vos.
	 * 
	 * @param lMapInput
	 * @return
	 * @throws Exception
	 */
	private List generateBillReceiptDtls(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) lMapInput.get("serviceLocator");

		List<TrnPensionBillReceipt> pnsnRcptVOlist = null;

		HttpSession session = request.getSession();
		List lLstOmrPenCode = (ArrayList) session.getAttribute("lLstOmrPenCode");

		session.removeAttribute("lLstOmrPenCode");

		String rcptRows[] = null;
		String lStrArrPnsner[] = null;
		String lStrForMonth = null;
		int flag = 0;
		int lsize = 0;
		List lPnsnrCodeLst = new ArrayList<String>();
		StringBuffer lStrPnsnCode = new StringBuffer();

		MonthlyPensionBillDAO lObjPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, servLoc.getSessionFactory());

		try {
			setSessionInfo(lMapInput);

			rcptRows = StringUtility.getParameterValues("rcptRows", request);

			if (rcptRows != null && rcptRows.length > 0) {
				lStrForMonth = StringUtility.getParameter("hidforMonth", request);
				lStrArrPnsner = StringUtility.getParameterValues("hidPnsnerCode", request);
				StringUtility.getParameter("hidPenReqIdLst", request);

				for (int n = 0; n < lStrArrPnsner.length; n++) {
					if (flag == 1) {
						lStrPnsnCode.append(",");
					}
					lStrPnsnCode.append("'" + lStrArrPnsner[n] + "'");
					flag = 1;
					lsize++;
					if (lsize == 999) {
						lPnsnrCodeLst.add(lStrPnsnCode.toString());
						lsize = 0;
						flag = 0;
						lStrPnsnCode = new StringBuffer();
					}
				}

				if (lStrPnsnCode != null && lStrPnsnCode.length() > 0) {
					lPnsnrCodeLst.add(lStrPnsnCode.toString());
				}

				String lStrPPOList = null;
				if (lMapInput.containsKey("PpoNoList")) {
					lStrPPOList = lMapInput.get("PpoNoList").toString();
				}

				pnsnRcptVOlist = lObjPensionBillDAO.getHeadCodeWiseBillRcptEdpDtl(lStrForMonth, lPnsnrCodeLst, lStrPPOList, SessionHelper.getLocationCode(lMapInput), lLstOmrPenCode);

				// Getting Vo's Of IT Cut Details Generated into HdrDetails Vo's
				// Generate.
				if (lMapInput.containsKey("HeadCodeWiseITCutAmt")) {
					List lHeadCodeWiseITCutAmt = (List) lMapInput.get("HeadCodeWiseITCutAmt");
					TrnPensionBillReceipt lPensionBillReceiptVo = null;

					for (int i = 0; i < lHeadCodeWiseITCutAmt.size(); i++) {
						lPensionBillReceiptVo = (TrnPensionBillReceipt) lHeadCodeWiseITCutAmt.get(i);

						if (lPensionBillReceiptVo != null) {
							pnsnRcptVOlist.add(lPensionBillReceiptVo);
						}
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return pnsnRcptVOlist;
	}

	/**
	 * It generates TrnPensionArrearDtls VOs for inserting previous month dtls
	 * in arrear table.
	 * 
	 * @param lMapInput
	 *            InputMap.
	 * @return lLstTrnPensionArrearDtls List<TrnPensionArrearDtls>
	 * @author Aparna
	 */
	private List<TrnPensionArrearDtls> generateTrnPensionArrearDtls(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<TrnPensionArrearDtls> lLstArrearDtls = new ArrayList<TrnPensionArrearDtls>();
		TrnPensionArrearDtls lObjArrearVO = new TrnPensionArrearDtls();
		String lStrDate = null;

		try {
			lStrDate = StringUtility.getParameter("hidforMonth", request);

			HttpSession session = request.getSession();
			List lLstArrearSession = (ArrayList) session.getAttribute("ArrearList");

			session.removeAttribute("ArrearList");

			for (Integer x = 0; x < lLstArrearSession.size(); x++) {
				TrnPensionArrearDtls lObjTemp = (TrnPensionArrearDtls) lLstArrearSession.get(x);
				if (lObjTemp != null && lObjTemp.getDifferenceAmount() != null) {
					if (Double.valueOf(lObjTemp.getDifferenceAmount().toString()) != 0D) {
						lObjArrearVO = new TrnPensionArrearDtls();

						lObjArrearVO.setPensionRequestId(lObjTemp.getPensionRequestId());
						lObjArrearVO.setPensionerCode(lObjTemp.getPensionerCode());
						lObjArrearVO.setArrearFieldType(lObjTemp.getArrearFieldType());
						lObjArrearVO.setOldAmountPercentage(new BigDecimal("0.00"));
						lObjArrearVO.setNewAmountPercentage(lObjTemp.getDifferenceAmount());
						lObjArrearVO.setEffectedFromYyyymm(lObjTemp.getEffectedFromYyyymm());
						lObjArrearVO.setEffectedToYyyymm(lObjTemp.getEffectedToYyyymm());
						lObjArrearVO.setDifferenceAmount(lObjTemp.getDifferenceAmount());
						lObjArrearVO.setTotalDifferenceAmt(lObjTemp.getDifferenceAmount());
						lObjArrearVO.setPaymentFromYyyymm(Integer.parseInt(lStrDate));
						lObjArrearVO.setPaymentToYyyymm(Integer.parseInt(lStrDate));
						lObjArrearVO.setCreatedDate(gDtCurrDt);
						lObjArrearVO.setCreatedPostId(new BigDecimal(gLngPostId));
						lObjArrearVO.setCreatedUserId(new BigDecimal(gLngUserId));

						lLstArrearDtls.add(lObjArrearVO);
					}
				}
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstArrearDtls;
	}

	private List<TrnPensionOtherPartyPay> generateTrnPensionOtherPartyPay(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<TrnPensionOtherPartyPay> lLstOthPartyDtls = new ArrayList<TrnPensionOtherPartyPay>();
		new TrnPensionArrearDtls();
		try {
			TrnPensionOtherPartyPay lObjOthrPartyVO = new TrnPensionOtherPartyPay();
			HttpSession session = request.getSession();

			List lLstOthrPartySession = (ArrayList) session.getAttribute("lstOthPartyPay");

			session.removeAttribute("lstOthPartyPay");
			if (lLstOthrPartySession != null && !lLstOthrPartySession.isEmpty()) {
				for (Integer x = 0; x < lLstOthrPartySession.size(); x++) {
					TrnPensionOtherPartyPay lObjTemp = (TrnPensionOtherPartyPay) lLstOthrPartySession.get(x);
					if (lObjTemp != null && lObjTemp.getPartyAmnt() != null) {
						if (Double.valueOf(lObjTemp.getPartyAmnt().toString()) != 0D) {
							lObjOthrPartyVO = new TrnPensionOtherPartyPay();

							lObjOthrPartyVO.setOtherPartyPaymentId(lObjTemp.getOtherPartyPaymentId());
							lObjOthrPartyVO.setPartyAmnt(lObjTemp.getPartyAmnt());
							lObjOthrPartyVO.setPartyName(lObjTemp.getPartyName());
							lObjOthrPartyVO.setOtherPartyType(lObjTemp.getOtherPartyType());
							lObjOthrPartyVO.setPensionerCode(lObjTemp.getPensionerCode());
							lObjOthrPartyVO.setPpoNo(lObjTemp.getPpoNo());
							lObjOthrPartyVO.setPensionerName(lObjTemp.getPensionerName());
							lObjOthrPartyVO.setLocationCode(gStrLocCode);
							lObjOthrPartyVO.setRejected(0);
							lObjOthrPartyVO.setCreatedDate(gDtCurrDt);
							lObjOthrPartyVO.setCreatedPostId(gLngPostId);
							lObjOthrPartyVO.setCreatedUserId(gLngUserId);

							lLstOthPartyDtls.add(lObjOthrPartyVO);
						}
					}
				}
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstOthPartyDtls;
	}

	/**
	 * Generate TrnPensionBillDtlsVO.
	 * 
	 * @param lMapInput
	 * @return TrnPensionBillDtls List
	 */
	public List<TrnPensionBillDtls> generateTrnPnsnBillArrearDtlsLst(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");

		List lPrvMonthlyArrearDtlsLst = null;
		List<MonthlyPensionBillVO> lMonthlyArrearDtlsLst = null;
		List<TrnPensionBillDtls> lPnsnBillArrDtlsLst = null;
		MonthlyPensionBillVO lPensionBillVO = null;
		TrnPensionBillDtls lPnsnBillArrearDtlsVO = null;

		try {
			setSessionInfo(lMapInput);

			// Fetching & Remove Attribuites From session
			HttpSession session = request.getSession();
			lPrvMonthlyArrearDtlsLst = (List) session.getAttribute("PrvMonthlyArrearLst");
			lPnsnBillArrDtlsLst = new ArrayList<TrnPensionBillDtls>();
			for (int p = 0; p < lPrvMonthlyArrearDtlsLst.size(); p += 2) {
				lMonthlyArrearDtlsLst = (List<MonthlyPensionBillVO>) lPrvMonthlyArrearDtlsLst.get(p + 1);

				if (lMonthlyArrearDtlsLst != null && !lMonthlyArrearDtlsLst.isEmpty()) {
					for (int i = 0; i < lMonthlyArrearDtlsLst.size(); i++) {
						// Arrear For Pension
						lPensionBillVO = new MonthlyPensionBillVO();
						lPnsnBillArrearDtlsVO = new TrnPensionBillDtls();
						lPensionBillVO = lMonthlyArrearDtlsLst.get(i);

						// Set Headcode for make easy to replace headoce wise
						// BillHdrIDPK comes after saving bill in Service.
						lPnsnBillArrearDtlsVO.setTrnPensionBillHdrId(Long.valueOf("90" + lPensionBillVO.getHeadCode().toString()));

						lPnsnBillArrearDtlsVO.setPpoNo(lPensionBillVO.getPpoNo());
						lPnsnBillArrearDtlsVO.setPensionerCode(lPensionBillVO.getPensionerCode());
						lPnsnBillArrearDtlsVO.setPensionerName(lPensionBillVO.getPensionerName());
						lPnsnBillArrearDtlsVO.setAccountNo(lPensionBillVO.getAccountNo());
						lPnsnBillArrearDtlsVO.setPayForMonth(lPensionBillVO.getForMonth());

						// lPnsnBillArrearDtlsVO.setPensionAmount(lPensionBillVO.getBasicPensionAmount());
						// lPnsnBillArrearDtlsVO.setPensnCutAmount(lPensionBillVO.getPensionCutAmount());
						// lPnsnBillArrearDtlsVO.setDpAmount(lPensionBillVO.getDpPercentAmount());
						// lPnsnBillArrearDtlsVO.setAdpAmount(lPensionBillVO.getAdpAmount());
						// lPnsnBillArrearDtlsVO.setTiAmount(lPensionBillVO.getTiPercentAmount());
						// lPnsnBillArrearDtlsVO.setCvpMonthAmount(lPensionBillVO.getCvpMonthlyAmount());
						// lPnsnBillArrearDtlsVO.setMedicalAllowenceAmount(lPensionBillVO.getMedicalAllowenceAmount());
						// lPnsnBillArrearDtlsVO.setTiArrearAmount(lPensionBillVO.getTIArrearsAmount());
						// lPnsnBillArrearDtlsVO.setOtherBenefits(lPensionBillVO.getOtherBenefit());
						// lPnsnBillArrearDtlsVO.setPersonalPensionAmount(lPensionBillVO.getPersonalPension());
						// lPnsnBillArrearDtlsVO.setIrAmount(lPensionBillVO.getIr());
						// lPnsnBillArrearDtlsVO.setIncomeTaxCutAmount(lPensionBillVO.getItCutAmount());
						// lPnsnBillArrearDtlsVO.setSpecialCutAmount(lPensionBillVO.getSpecialCutAmount());
						// lPnsnBillArrearDtlsVO.setArrearAmount(lPensionBillVO.getOtherArrearsAmount());
						// lPnsnBillArrearDtlsVO.setRecoveryAmount(lPensionBillVO.getRecoveryAmount());
						// lPnsnBillArrearDtlsVO.setOmr(lPensionBillVO.getOMR());
						// lPnsnBillArrearDtlsVO.setReducedPension(lPensionBillVO.getNetPensionAmount());
						// lPnsnBillArrearDtlsVO.setAllcationAf10560(lPensionBillVO.getAllnAf10560());
						// lPnsnBillArrearDtlsVO.setAllcationAf11156(lPensionBillVO.getAllnAf11156());
						// lPnsnBillArrearDtlsVO.setAllcationBf11156(lPensionBillVO.getAllnBf11156());
						// lPnsnBillArrearDtlsVO.setMoCommission(lPensionBillVO.getMOComm());
						// lPnsnBillArrearDtlsVO.setFromDate(lPensionBillVO.getFromDate());
						// lPnsnBillArrearDtlsVO.setToDate(lPensionBillVO.getToDate());

						lPnsnBillArrDtlsLst.add(lPnsnBillArrearDtlsVO);
					}
				}
			}
			session.removeAttribute("PrvMonthlyArrearLst");
		} catch (Exception e) {
			gLogger.error("Error in" + e, e);
			throw (e);
		}

		return lPnsnBillArrDtlsLst;
	}

	/**
	 * Method getting session info
	 * 
	 * @param inputMap
	 * @throws Exception
	 */
	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gDtCurrDt = DBUtility.getCurrentDateFromDB();
			gStrLocCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is: " + e, e);
			throw (e);
		}
	}
}
