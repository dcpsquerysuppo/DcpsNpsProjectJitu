/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 21, 2011		Vihan Khatri								
 *******************************************************************************
 */

/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 21, 2011
 */

package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DCPSNomineeDtlsVOGenerator
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.InterestCalculationDAO;
import com.tcs.sgv.dcps.dao.InterestCalculationDAOImpl;
import com.tcs.sgv.dcps.dao.TerminalRequestDAO;
import com.tcs.sgv.dcps.dao.TerminalRequestDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionYearly;
import com.tcs.sgv.dcps.valueobject.TrnDcpsMissingCreditsDtls;
import com.tcs.sgv.dcps.valueobject.TrnDcpsTerminalDtls;

public class TerminalRequestVOGenerator extends ServiceImpl implements VOGeneratorService {

	/*
	 * @Description : Method to generate VO For DcpsEmpNmnMst.
	 * 
	 * @Input : Map : inputMap
	 * 
	 * @Output : ResultObject : ResultObject
	 */

	@Override
	public ResultObject generateMap(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		List<TrnDcpsMissingCreditsDtls> lListTrnDcpsMissingCredits = null;
		TrnDcpsTerminalDtls lObjTrnDcpsTerminalDtls = null;
		MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
		String lStrUser = null;
		String lStrUse = null;

		try {

			lStrUser = StringUtility.getParameter("hidUser", request).trim();
			lStrUse = StringUtility.getParameter("hidUse", request).trim();

			lObjTrnDcpsTerminalDtls = generateTrnDcpsTerminalDtlsVO(inputMap);
			inputMap.put("lObjTrnDcpsTerminalDtls", lObjTrnDcpsTerminalDtls);

			if ((lStrUser.equals("DDO") && (lStrUse.equals("NewRequest") || lStrUse.equals("FromDraft")))
					|| (lStrUser.equals("TO"))) // with DDO for first time or
			// with TO
			{
				lListTrnDcpsMissingCredits = generateMissingCreditsVOList(inputMap);
			}

			inputMap.put("lListTrnDcpsMissingCredits", lListTrnDcpsMissingCredits);
			if (lStrUser.equals("SRKA")) {
				lObjMstDcpsContributionYearly = generateMstDcpsContributionYealyVO(inputMap);
			}
			inputMap.put("lObjMstDcpsContributionYearly", lObjMstDcpsContributionYearly);

			objRes.setResultValue(inputMap);
		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			ex.printStackTrace();
		}
		return objRes;
	}

	/*
	 * @Description : Method to generate VO For DCPSNomineeDtlsVOGenerator.
	 * 
	 * @Input : Map : inputMap
	 * 
	 * @Output : ResultObject : DcpsEmpNmnMst[]
	 */

	public List<TrnDcpsMissingCreditsDtls> generateMissingCreditsVOList(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");

		TerminalRequestDAO lObjTerminalRequestMissingCreditsDAO = new TerminalRequestDAOImpl(
				TrnDcpsMissingCreditsDtls.class, servLoc.getSessionFactory());

		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);

		String lStrUser = StringUtility.getParameter("hidUser", request).trim();
		String lStrUse = StringUtility.getParameter("hidUse", request).trim();
		String lStrHidMissingCreditSPK = null;
		Long lLongHidMissingCreditSPK = null;

		List<TrnDcpsMissingCreditsDtls> lListTrnDcpsMissingCreditsDtls = new ArrayList<TrnDcpsMissingCreditsDtls>();

		String lStrTotalMissingCredits = StringUtility.getParameter("hidTotalMissingCredits", request).trim();

		TrnDcpsMissingCreditsDtls lObjTrnDcpsMissingCreditsDtls = null;

		Integer lIntTotalMissingCredits = Integer.parseInt(lStrTotalMissingCredits);

		for (Integer lInt = 1; lInt <= lIntTotalMissingCredits; lInt++) {

			lStrHidMissingCreditSPK = StringUtility.getParameter("hidMissingCreditPK" + lInt, request).trim();

			if (lStrHidMissingCreditSPK.equals("0")) {
				lObjTrnDcpsMissingCreditsDtls = new TrnDcpsMissingCreditsDtls();
			} else {
				lLongHidMissingCreditSPK = Long.valueOf(lStrHidMissingCreditSPK);
				lObjTrnDcpsMissingCreditsDtls = (TrnDcpsMissingCreditsDtls) lObjTerminalRequestMissingCreditsDAO
						.read(lLongHidMissingCreditSPK);
			}
			if (lStrUser.equals("DDO") && (lStrUse.equals("NewRequest") || lStrUse.equals("FromDraft"))) {
				String lStrDcpsEmpId = null;
				Long lLongDcpsEmpId = null;
				String lStrMonth = null;
				String lStrYear = null;
				String lStrRemarks = null;

				lStrDcpsEmpId = StringUtility.getParameter("hidDcpsEmpId", request).trim();
				if (!"".equals(lStrDcpsEmpId)) {
					lLongDcpsEmpId = Long.valueOf(lStrDcpsEmpId);
				}
				lObjTrnDcpsMissingCreditsDtls.setDcpsEmpId(lLongDcpsEmpId);

				lStrMonth = StringUtility.getParameter("hidMonth" + lInt, request).trim();
				if (!"".equals(lStrMonth)) {
					lObjTrnDcpsMissingCreditsDtls.setMonth(Long.valueOf(lStrMonth));
				}

				lStrYear = StringUtility.getParameter("hidYear" + lInt, request).trim();
				if (!"".equals(lStrYear)) {
					lObjTrnDcpsMissingCreditsDtls.setYear(Long.valueOf(lStrYear));
				}

				lStrRemarks = StringUtility.getParameter("txtRemarks" + lInt, request).trim();
				lObjTrnDcpsMissingCreditsDtls.setRemarks(lStrRemarks);

			}

			if (lStrUser.equals("TO")) {
				String lStrAmountDeduction = null;
				String lStrVoucherNo = null;
				String lStrVoucherDate = null;
				Date lDtVoucherDate = null;

				lStrAmountDeduction = StringUtility.getParameter("txtDeducAmount" + lInt, request).trim();
				if (!"".equals(lStrAmountDeduction)) {
					lObjTrnDcpsMissingCreditsDtls.setAmountDeduction(Double.valueOf(lStrAmountDeduction));
				}

				lStrVoucherNo = StringUtility.getParameter("txtVoucherNo" + lInt, request).trim();
				if (!"".equals(lStrVoucherNo)) {
					lObjTrnDcpsMissingCreditsDtls.setVoucherNo(Long.valueOf(lStrVoucherNo));
				}

				lStrVoucherDate = StringUtility.getParameter("txtVoucherDate" + lInt, request).trim();
				if (!"".equals(lStrVoucherDate)) {
					lDtVoucherDate = sdf.parse(lStrVoucherDate);
				}

				if (lDtVoucherDate != null && !(lDtVoucherDate.equals(""))) {
					lObjTrnDcpsMissingCreditsDtls.setVoucherDate(lDtVoucherDate);
				}

			}

			lObjTrnDcpsMissingCreditsDtls.setLangId(lLngLangId);
			lObjTrnDcpsMissingCreditsDtls.setLocId(lLngLocId);
			lObjTrnDcpsMissingCreditsDtls.setDbId(lLngDbId);

			lListTrnDcpsMissingCreditsDtls.add(lObjTrnDcpsMissingCreditsDtls);

		}

		return lListTrnDcpsMissingCreditsDtls;
	}

	public TrnDcpsTerminalDtls generateTrnDcpsTerminalDtlsVO(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, servLoc
				.getSessionFactory());

		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);

		TrnDcpsTerminalDtls lObjTrnDcpsTerminalDtls = null;

		String lStrUser = StringUtility.getParameter("hidUser", request).trim();
		String lStrUse = StringUtility.getParameter("hidUse", request).trim();
		String lStrTerminalId = StringUtility.getParameter("hidTerminalId", request).trim();
		Long lLongTerminalId = null;

		if (!"".equals(lStrTerminalId)) {
			lLongTerminalId = Long.valueOf(lStrTerminalId);
		}

		if (lStrUser.equals("DDO") && lStrUse.equals("NewRequest")) {
			lObjTrnDcpsTerminalDtls = new TrnDcpsTerminalDtls();
		} else {
			lObjTrnDcpsTerminalDtls = (TrnDcpsTerminalDtls) lObjTerminalRequestDAO.read(lLongTerminalId);
		}

		if (lStrUser.equals("DDO") && (lStrUse.equals("NewRequest") || lStrUse.equals("FromDraft"))) {
			String lStrDcpsEmpId = null;
			Long lLongDcpsEmpId = null;
			String lStrDateOfTermination = null;
			Date lDtDateOfTermination = null;
			String lStrReasonForTermination = null;

			lStrDcpsEmpId = StringUtility.getParameter("hidDcpsEmpId", request).trim();
			if (!"".equals(lStrDcpsEmpId)) {
				lLongDcpsEmpId = Long.valueOf(lStrDcpsEmpId);
			}
			lObjTrnDcpsTerminalDtls.setDcpsEmpId(lLongDcpsEmpId);

			lStrDateOfTermination = StringUtility.getParameter("txtTerminationDate", request).trim();
			if (!"".equals(lStrDateOfTermination)) {
				lDtDateOfTermination = sdf.parse(lStrDateOfTermination);
			}

			if (lDtDateOfTermination != null && !(lDtDateOfTermination.equals(""))) {
				lObjTrnDcpsTerminalDtls.setDateOfTermination(lDtDateOfTermination);
			}

			lStrReasonForTermination = StringUtility.getParameter("cmbReasonForTermination", request).trim();
			if (!"".equals(lStrReasonForTermination) && !"".equals("-1")) {
				lObjTrnDcpsTerminalDtls.setReasonOfTermination(Long.valueOf(lStrReasonForTermination));
			}

			lObjTrnDcpsTerminalDtls.setStatusFlag(0l);
			lObjTrnDcpsTerminalDtls.setLangId(lLngLangId);
			lObjTrnDcpsTerminalDtls.setLocId(lLngLocId);
			lObjTrnDcpsTerminalDtls.setDbId(lLngDbId);

		} else if (lStrUser.equals("SRKA")) {
			String lStrOrderNo = StringUtility.getParameter("txtOrderNo", request).trim();
			String lStrOrderDate = StringUtility.getParameter("txtOrderDate", request).trim();
			String lStrAuthorityLetterNo = StringUtility.getParameter("txtLetterNo", request).trim();
			String lStrRemarks = StringUtility.getParameter("textareaRemarks", request).trim();
			String lStrOpeningBal = StringUtility.getParameter("txtOpeningBal", request).trim();
			String lStrEmpContri = StringUtility.getParameter("txtCurrYearAndMissingContri", request).trim();
			String lStrEmplrContri = StringUtility.getParameter("txtEmplrContri", request).trim();
			String lStrEmplrContriPending = StringUtility.getParameter("txtEmplrContriPending", request).trim();
			String lStrInterestContri = StringUtility.getParameter("txtInterest", request).trim();
			String lStrTier2Contri = StringUtility.getParameter("txtTier2Contri", request).trim();
			String lStrTier2Interest = StringUtility.getParameter("txtTier2Interest", request).trim();
			String lStrPayableAmount = StringUtility.getParameter("txtTotalAmt", request).trim();

			lObjTrnDcpsTerminalDtls.setOrderNo(lStrOrderNo);
			if (lStrOrderDate != null && !lStrOrderDate.equals("")) {
				lObjTrnDcpsTerminalDtls.setOrderDate(sdf.parse(lStrOrderDate));
			}
			lObjTrnDcpsTerminalDtls.setAuthorityLetterNo(lStrAuthorityLetterNo);
			lObjTrnDcpsTerminalDtls.setRemarksSrka(lStrRemarks);
			if (lStrOpeningBal != null && !lStrOpeningBal.equals("")) {
				lObjTrnDcpsTerminalDtls.setOpeningBalance(Double.parseDouble(lStrOpeningBal));
			}
			if (lStrEmpContri != null && !lStrEmpContri.equals("")) {
				lObjTrnDcpsTerminalDtls.setEmpContriCurrYear(Double.parseDouble(lStrEmpContri));
			}
			if (lStrEmplrContri != null && !lStrEmplrContri.equals("")) {
				lObjTrnDcpsTerminalDtls.setEmplrContriCurrYear(Double.parseDouble(lStrEmplrContri)
						+ Double.parseDouble(lStrEmplrContriPending));
			}
			if (lStrInterestContri != null && !lStrInterestContri.equals("")) {
				lObjTrnDcpsTerminalDtls.setInterestContri(Double.parseDouble(lStrInterestContri));
			}
			if (lStrTier2Contri != null && !lStrTier2Contri.equals("")) {
				lObjTrnDcpsTerminalDtls.setTier2Contri(Double.parseDouble(lStrTier2Contri));
			}
			if (lStrTier2Interest != null && !lStrTier2Interest.equals("")) {
				lObjTrnDcpsTerminalDtls.setTier2Interest(Double.parseDouble(lStrTier2Interest));
			}
			if (lStrPayableAmount != null && !lStrPayableAmount.equals("")) {
				lObjTrnDcpsTerminalDtls.setPayableAmount(Double.parseDouble(lStrPayableAmount));
			}

		}

		return lObjTrnDcpsTerminalDtls;

	}

	public MstDcpsContributionYearly generateMstDcpsContributionYealyVO(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		new SimpleDateFormat("dd/MM/yyyy");

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");

		SessionHelper.getDbId(inputMap);
		SessionHelper.getLocationId(inputMap);
		SessionHelper.getLangId(inputMap);

		InterestCalculationDAO lObjInterestCalculationDAO = new InterestCalculationDAOImpl(null, servLoc
				.getSessionFactory());

		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, servLoc.getSessionFactory());

		StringUtility.getParameter("hidUser", request).trim();
		StringUtility.getParameter("hidUse", request).trim();
		String lStrDcpsId = StringUtility.getParameter("hidDcpsId", request).trim();
		Long lLngDcpsEmpId = Long.parseLong(StringUtility.getParameter("hidDcpsEmpId", request).trim());
		Long lLngCurrYearId = new Long(lObjFinancialYearDAO.getFinYearIdByCurDate());

		String lStrContriTier2 = StringUtility.getParameter("txtTier2Contri", request).trim();
		String lStrContriTier2Int = StringUtility.getParameter("txtTier2Interest", request).trim();
		String lStrContriEmp = StringUtility.getParameter("txtCurrYearAndMissingContri", request).trim();
		String lStrContriEmpInt = StringUtility.getParameter("hidContriEmpInt", request).trim();
		String lStrOpenInt = StringUtility.getParameter("hidOpenInt", request).trim();

		MstDcpsContributionYearly lObjMstDcpsContributionYearlyPreYear = lObjInterestCalculationDAO
				.getContriYearlyVOForYear(lLngDcpsEmpId, lLngCurrYearId - 1);
		Long lLngContributionYearlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_yearly", inputMap);

		MstDcpsContributionYearly lObjMstDcpsContributionYearly = new MstDcpsContributionYearly();

		lObjMstDcpsContributionYearly.setDcpsContributionYearlyId(lLngContributionYearlyId);
		lObjMstDcpsContributionYearly.setDcpsId(lStrDcpsId);
		lObjMstDcpsContributionYearly.setOpenNet(lObjMstDcpsContributionYearlyPreYear.getCloseNet());
		lObjMstDcpsContributionYearly.setOpenEmp(lObjMstDcpsContributionYearlyPreYear.getCloseEmp());
		lObjMstDcpsContributionYearly.setOpenEmplr(lObjMstDcpsContributionYearlyPreYear.getCloseEmplr());
		lObjMstDcpsContributionYearly.setOpenTier2(lObjMstDcpsContributionYearlyPreYear.getCloseTier2());
		if (lStrContriTier2 != null && !lStrContriTier2.equals("")) {
			lObjMstDcpsContributionYearly.setContribTier2(Double.parseDouble(lStrContriTier2));
		}
		if (lStrContriEmpInt != null && !lStrContriEmpInt.equals("")) {
			lObjMstDcpsContributionYearly.setIntContribEmp(Double.parseDouble(lStrContriEmpInt));
			lObjMstDcpsContributionYearly.setIntContribEmplr(Double.parseDouble(lStrContriEmpInt));
		}
		if (lStrContriTier2Int != null && !lStrContriTier2Int.equals("")) {
			lObjMstDcpsContributionYearly.setIntContribTier2(Double.parseDouble(lStrContriTier2Int));
		}
		if (lStrContriEmp != null && !lStrContriEmp.equals("")) {
			lObjMstDcpsContributionYearly.setContribEmp(Double.parseDouble(lStrContriEmp));
			lObjMstDcpsContributionYearly.setContribEmplr(Double.parseDouble(lStrContriEmp));
		}

		Double lDblCloseEmp = Double.parseDouble(lStrContriEmpInt) + Double.parseDouble(lStrContriEmp);
		Double lDblCloseEmplr = Double.parseDouble(lStrContriEmpInt) + Double.parseDouble(lStrContriEmp);
		Double lDblCloseTier2 = Double.parseDouble(lStrContriTier2) + Double.parseDouble(lStrContriTier2Int);
		lObjMstDcpsContributionYearly.setCloseEmp(lDblCloseEmp);
		lObjMstDcpsContributionYearly.setCloseEmplr(lDblCloseEmplr);
		lObjMstDcpsContributionYearly.setCloseTier2(lDblCloseTier2);
		lObjMstDcpsContributionYearly.setCloseNet(lDblCloseEmp + lDblCloseEmplr + lDblCloseTier2);
		if (lStrOpenInt != null && !lStrOpenInt.equals("")) {
			lObjMstDcpsContributionYearly.setOpenInt(Double.parseDouble(lStrOpenInt));
		}
		lObjMstDcpsContributionYearly.setYearId(lLngCurrYearId);
		lObjMstDcpsContributionYearly.setCurTreasuryCD(lObjMstDcpsContributionYearlyPreYear.getCurTreasuryCD());
		lObjMstDcpsContributionYearly.setCurDdoCD(lObjMstDcpsContributionYearlyPreYear.getCurDdoCD());
		return lObjMstDcpsContributionYearly;
	}
}
