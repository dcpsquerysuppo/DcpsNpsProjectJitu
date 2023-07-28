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
/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 17, 2011		Vihan Khatri								
 *******************************************************************************
 */
/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 17, 2011
 */
package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DCPSNomineeDtlsVOGenerator
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class ContributionsVOGenerator extends ServiceImpl implements
		VOGeneratorService {
	private final Log gLogger = LogFactory.getLog(getClass());

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
		inputMap.get("requestObj");

		TrnDcpsContribution[] lArrTrnDcpsContributions = null;

		try {
			lArrTrnDcpsContributions = generateContributionsVOList(inputMap);
			inputMap.put("lArrTrnDcpsContributions", lArrTrnDcpsContributions);
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

	public TrnDcpsContribution[] generateContributionsVOList(Map inputMap)
			throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		String lStrdcpsEmpIds = StringUtility.getParameter("dcpsEmpIds",
				request);
		String lStrtxtStartDate = StringUtility.getParameter("txtStartDate",
				request);
		String lStrtxtEndDate = StringUtility.getParameter("txtEndDate",
				request);

		String lStrTypeOfPayment = StringUtility.getParameter(
				"cmbTypeOfPayment", request);
		String lStrPayCommission = StringUtility.getParameter(
				"cmbPayCommission", request);
		String lStrBasic = StringUtility.getParameter("basic", request);
		String lStrDP = StringUtility.getParameter("DP", request);
		String lStrDA = StringUtility.getParameter("DA", request);
		String lStrContribution = StringUtility.getParameter("contribution",
				request);
		String lStrMonthId = StringUtility.getParameter("monthId", request);
		String lStrYearId = StringUtility.getParameter("yearId", request);
		String lStrTreasuryCode = StringUtility.getParameter("cmbTreasuryCode",
				request);
		String lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request);
		String lStrBillGroupId = StringUtility.getParameter("cmbBillGroup",
				request);

		Long lLongBillGroupId = Long.valueOf(lStrBillGroupId);

		String schemeCode = StringUtility.getParameter("schemeCode", request);

		Long lLongMonthId = Long.valueOf(lStrMonthId);
		Long lLongYearId = Long.valueOf(lStrYearId);
		Long lLongTreasuryCode = Long.valueOf(lStrTreasuryCode);

		String[] lStrArrdcpsEmpIds = lStrdcpsEmpIds.split("~");
		Long[] lLongArrdcpsEmpIds = new Long[lStrArrdcpsEmpIds.length];
		for (Integer lInt = 0; lInt < lStrArrdcpsEmpIds.length; lInt++) {
			lLongArrdcpsEmpIds[lInt] = Long.valueOf(lStrArrdcpsEmpIds[lInt]);
		}

		String[] lStrArrTypeOfPayment = lStrTypeOfPayment.split("~");

		String[] lStrArrBasics = lStrBasic.split("~");
		Double[] lDoubleArrBasics = new Double[lStrArrBasics.length];
		for (Integer lInt = 0; lInt < lStrArrBasics.length; lInt++) {
			lDoubleArrBasics[lInt] = Double.valueOf(lStrArrBasics[lInt]);
		}

		String[] lStrArrpaycommission = lStrPayCommission.split("~");

		String[] lStrArrDP = lStrDP.split("~");
		Double[] lDoubleArrDP = new Double[lStrArrDP.length];
		for (Integer lInt = 0; lInt < lStrArrDP.length; lInt++) {
			lDoubleArrDP[lInt] = Double.valueOf(lStrArrDP[lInt]);
		}

		String[] lStrArrDA = lStrDA.split("~");
		Double[] lDoubleArrDA = new Double[lStrArrDA.length];
		for (Integer lInt = 0; lInt < lStrArrDA.length; lInt++) {
			lDoubleArrDA[lInt] = Double.valueOf(lStrArrDA[lInt]);
		}

		String[] lStrArrContributions = lStrContribution.split("~");
		Double[] lDoubleArrContributions = new Double[lStrArrContributions.length];
		for (Integer lInt = 0; lInt < lStrArrContributions.length; lInt++) {
			lDoubleArrContributions[lInt] = Double
					.valueOf(lStrArrContributions[lInt]);
		}

		String[] lStrArrStartDate = lStrtxtStartDate.split("~");
		//System.out.println("length of contr=" + lDoubleArrContributions.length);
		//System.out.println("length of start date=" + lStrArrStartDate.length);
		Date[] lDateArrStartDate = new Date[lStrArrStartDate.length];
		for (Integer lInt = 0; lInt < lStrArrStartDate.length; lInt++) {
			if (lStrArrStartDate[lInt] != null
					&& !"".equals(lStrArrStartDate[lInt].trim())) {
				lDateArrStartDate[lInt] = IFMSCommonServiceImpl
						.getDateFromString(lStrArrStartDate[lInt]);
			}
		}

		String[] lStrArrEndDate = lStrtxtEndDate.split("~");
		Date[] lDateArrEndDate = new Date[lStrArrEndDate.length];
		for (Integer lInt = 0; lInt < lStrArrEndDate.length; lInt++) {
			if (lStrArrEndDate[lInt] != null
					&& !"".equals(lStrArrEndDate[lInt].trim())) {
				lDateArrEndDate[lInt] = IFMSCommonServiceImpl
						.getDateFromString(lStrArrEndDate[lInt]);
			}
		}

		TrnDcpsContribution[] lArrTrnDcpsContribution = new TrnDcpsContribution[lStrArrdcpsEmpIds.length];

		for (Integer lInt = 0; lInt < lStrArrdcpsEmpIds.length; lInt++) {

			TrnDcpsContribution lObjTrnDcpsContribution = new TrnDcpsContribution();

			lObjTrnDcpsContribution.setDcpsEmpId(lLongArrdcpsEmpIds[lInt]);
			gLogger.info("setDcpsEmpId"+lLongArrdcpsEmpIds[lInt]);
			lObjTrnDcpsContribution.setTreasuryCode(lLongTreasuryCode);
			gLogger.info("lLongTreasuryCode"+lLongTreasuryCode);

			lObjTrnDcpsContribution.setDdoCode(lStrDDOCode);
			gLogger.info("lStrDDOCode"+lStrDDOCode);

			lObjTrnDcpsContribution.setDcpsDdoBillGroupId(lLongBillGroupId);
			gLogger.info("lLongBillGroupId"+lLongBillGroupId);

			lObjTrnDcpsContribution.setSchemeCode(schemeCode);
			gLogger.info("schemeCode"+schemeCode);

			lObjTrnDcpsContribution
					.setTypeOfPayment(lStrArrTypeOfPayment[lInt]);
			gLogger.info("setTypeOfPayment"+lStrArrTypeOfPayment[lInt]);

			lObjTrnDcpsContribution
					.setPayCommission(lStrArrpaycommission[lInt]);
			gLogger.info("setPayCommission"+lStrArrpaycommission[lInt]);

			lObjTrnDcpsContribution.setFinYearId(lLongYearId);
			gLogger.info("setFinYearId"+lLongYearId);

			lObjTrnDcpsContribution.setMonthId(lLongMonthId);
			gLogger.info("setMonthId"+lLongMonthId);

			lObjTrnDcpsContribution.setBasicPay(lDoubleArrBasics[lInt]);
			gLogger.info("setBasicPay"+lDoubleArrBasics[lInt]);

			lObjTrnDcpsContribution.setDP(lDoubleArrDP[lInt]);
			gLogger.info("setDP"+lDoubleArrDP[lInt]);

			lObjTrnDcpsContribution.setDA(lDoubleArrDA[lInt]);
			gLogger.info("setDA"+lDoubleArrDA[lInt]);

			lObjTrnDcpsContribution.setContribution(lDoubleArrContributions[lInt]);
			gLogger.info("setContribution"+lDoubleArrContributions[lInt]);
			//$t 2019
//			lObjTrnDcpsContribution.setContributionEmplr(lDoubleArrContributions[lInt]);
//	        gLogger.info("set setContributionEmplr Contribution"+lDoubleArrContributions[lInt]);

			lObjTrnDcpsContribution.setStartDate(lDateArrStartDate[lInt]);
			gLogger.info("setStartDate"+lDateArrStartDate[lInt]);

			lObjTrnDcpsContribution.setEndDate(lDateArrEndDate[lInt]);
			gLogger.info("setEndDate"+lDateArrEndDate[lInt]);

			lObjTrnDcpsContribution.setLangId(lLngLangId);
			gLogger.info("setLangId"+lLngLangId);

			lObjTrnDcpsContribution.setLocId(lLngLocId);
			gLogger.info("setLocId"+lLngLocId);

			lObjTrnDcpsContribution.setDbId(lLngDbId);
			gLogger.info("setDbId"+lLngDbId);

			lObjTrnDcpsContribution.setCreatedPostId(gLngPostId);
			gLogger.info("setCreatedPostId"+gLngPostId);

			lObjTrnDcpsContribution.setCreatedUserId(gLngUserId);
			gLogger.info("setCreatedUserId"+gLngUserId);

			lObjTrnDcpsContribution.setCreatedDate(gDtCurrDt);
			gLogger.info("setCreatedDate"+gDtCurrDt);

			lArrTrnDcpsContribution[lInt] = lObjTrnDcpsContribution;
		}
		return lArrTrnDcpsContribution;
	}
}
