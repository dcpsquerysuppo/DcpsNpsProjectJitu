/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 19, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.GpfLnaDashboardDao;
import com.tcs.sgv.common.dao.GpfLnaDashboardDaoImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.TreasuryDAO;
import com.tcs.sgv.dcps.dao.TreasuryDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.TrnDcpsDeputationContribution;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 May 20, 2011
 */
public class DeputationContributionVOGenerator extends ServiceImpl {
	
	Log gLogger = LogFactory.getLog(getClass());
	////$t 20-04-2020 IRRI
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	////$t

	public ResultObject generateMap(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TrnDcpsDeputationContribution[] lArrTrnDcpsDeputationContribution = null;

		try {

			lArrTrnDcpsDeputationContribution = generateVOList(inputMap);
			inputMap.put("lArrTrnDcpsDeputationContribution",
					lArrTrnDcpsDeputationContribution);

			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			gLogger.info("Error is  " + e);
		}

		return objRes;
	}

	public TrnDcpsDeputationContribution[] generateVOList(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		////$t 20-04-2020 IRRI
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		//$t
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		String gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		
		TrnDcpsDeputationContribution[] lArrTrnDcpsDeputationContribution = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		TrnDcpsDeputationContribution lObjTrnDcpsDeputationContribution = null;

		try {
		////$t 20-04-2020 IRRI
			TreasuryDAO lObjTreasuryDAO = new TreasuryDAOImpl(MstDcpsContriVoucherDtls.class,serv.getSessionFactory());
			GpfLnaDashboardDao lObjGpfLnaDashboardDao = new GpfLnaDashboardDaoImpl(null, serv.getSessionFactory());
			String lStrUserType = lObjGpfLnaDashboardDao.getUserType(gLngUserId);//lStrUserType=Treasury
		    this.gLogger.info("lStrUserType" + lStrUserType);
			
			if (lStrUserType.equals("EmployeeSelfService")||lStrUserType.equals("DDO Assistant")) {
				lLngLocId = Long.parseLong(((TreasuryDAOImpl) lObjTreasuryDAO).getTreasuryCodeForAst(lLngLocId.toString()));
				gStrLocationCode=lLngLocId.toString();
			}
			////$t
			
			String lStrDummyOfficeId = StringUtility.getParameter(
					"dummyOfficeId", request).trim();
			Long lLongMonthId = Long.valueOf(StringUtility.getParameter(
					"monthId", request).trim());
			Long lLongYearId = Long.valueOf(StringUtility.getParameter(
					"yearId", request).trim());

			String lStrdcpsEmpIds = StringUtility.getParameter("dcpsEmpIds",
					request).trim();
			String[] lStrArrdcpsEmpIds = lStrdcpsEmpIds.split("~");
			Long[] lLongArrdcpsEmpIds = new Long[lStrArrdcpsEmpIds.length];
			for (Integer lInt = 0; lInt < lStrArrdcpsEmpIds.length; lInt++) {
				lLongArrdcpsEmpIds[lInt] = Long
						.valueOf(lStrArrdcpsEmpIds[lInt]);
			}
//$t 2019
			String lStrContributions = StringUtility.getParameter(
					"contributions", request).trim();
			String[] lStrArrContributions = lStrContributions.split("~");
			Double[] lDoubleArrContributions = new Double[lStrArrContributions.length];
			for (Integer lInt = 0; lInt < lStrArrContributions.length; lInt++) {
				lDoubleArrContributions[lInt] = Double
						.valueOf(lStrArrContributions[lInt]);
			}
			
			String lStrChallanNos = StringUtility.getParameter("challanNos",
					request).trim();
			String[] lStrArrChallanNos = lStrChallanNos.split("~");

			String lStrChallanDates = StringUtility.getParameter(
					"challanDates", request).trim();
			String[] lStrArrChallanDates = lStrChallanDates.split("~");
			Date[] lDateArrChallanDates = new Date[lStrArrChallanDates.length];
			for (Integer lInt = 0; lInt < lStrArrChallanDates.length; lInt++) {
				if (lStrArrChallanDates[lInt] != null
						&& !"".equals(lStrArrChallanDates[lInt].trim())) {
					lDateArrChallanDates[lInt] = sdf
							.parse(lStrArrChallanDates[lInt]);
				}
			}
			
			String lStrUse = StringUtility.getParameter("Use", request).trim();
			
			String lStrContributionsEmplr = StringUtility.getParameter(
					"contributionsEmplr", request).trim();
			String[] lStrArrContributionsEmplr = lStrContributionsEmplr.split("~");
			Double[] lDoubleArrContributionsEmplr = new Double[lStrArrContributionsEmplr.length];
			for (Integer lInt = 0; lInt < lStrArrContributionsEmplr.length; lInt++) {
				
				if(lStrUse.equals("WithEmplrContri"))
				{
					lDoubleArrContributionsEmplr[lInt] = Double
							.valueOf(lStrArrContributionsEmplr[lInt]);
				}
				if(lStrUse.equals("WithoutEmplrContri"))
				{
					lDoubleArrContributionsEmplr[lInt] = 0d;
				}
			}
			
			String lStrChallanNosEmplr = StringUtility.getParameter("challanNosEmplr",
					request).trim();
			String[] lStrArrChallanNosEmplr = lStrChallanNosEmplr.split("~");

			String lStrChallanDatesEmplr = StringUtility.getParameter(
					"challanDatesEmplr", request).trim();
			String[] lStrArrChallanDatesEmplr = lStrChallanDatesEmplr.split("~");
			Date[] lDateArrChallanDatesEmplr = new Date[lStrArrChallanDatesEmplr.length];
			for (Integer lInt = 0; lInt < lStrArrChallanDatesEmplr.length; lInt++) {
				if (lStrArrChallanDatesEmplr[lInt] != null
						&& !"".equals(lStrArrChallanDatesEmplr[lInt].trim())) {
					
					if(lStrUse.equals("WithEmplrContri"))
					{
						lDateArrChallanDatesEmplr[lInt] = sdf
							.parse(lStrArrChallanDatesEmplr[lInt]);
					}
					if(lStrUse.equals("WithoutEmplrContri"))
					{
						lDateArrChallanDatesEmplr[lInt] = null;
					}
				}
			}
			
			String lStrStartDates = StringUtility.getParameter(
					"txtStartDate", request).trim();
			String[] lStrArrStartDates = lStrStartDates.split("~");
			Date[] lDateArrStartDates = new Date[lStrArrStartDates.length];
			for (Integer lInt = 0; lInt < lDateArrStartDates.length; lInt++) {
				if (lStrArrStartDates[lInt] != null
						&& !"".equals(lStrArrStartDates[lInt].trim())) {
					lDateArrStartDates[lInt] = sdf
							.parse(lStrArrStartDates[lInt]);
				}
			}
			
			String lStrEndDates = StringUtility.getParameter(
					"txtEndDate", request).trim();
			String[] lStrArrEndDates = lStrEndDates.split("~");
			Date[] lDateArrEndDates = new Date[lStrArrEndDates.length];
			for (Integer lInt = 0; lInt < lDateArrEndDates.length; lInt++) {
				if (lStrArrEndDates[lInt] != null
						&& !"".equals(lStrArrEndDates[lInt].trim())) {
					lDateArrEndDates[lInt] = sdf
							.parse(lStrArrEndDates[lInt]);
				}
			}
			
			String lStrPayCommission = StringUtility.getParameter("cmbPayCommission",
					request).trim();
			String[] lStrArrPayCommission = lStrPayCommission.split("~");

			String lStrTypeOfPayment = StringUtility.getParameter("cmbTypeOfPayment",
					request).trim();
			String[] lStrArrTypeOfPayment = lStrTypeOfPayment.split("~");
			
			String lStrBasic = StringUtility.getParameter(
					"basic", request).trim();
			String[] lStrArrBasic = lStrBasic.split("~");
			Double[] lDoubleBasic = new Double[lStrArrBasic.length];
			for (Integer lInt = 0; lInt < lStrArrBasic.length; lInt++) {
				lDoubleBasic[lInt] = Double
						.valueOf(lStrArrBasic[lInt]);
			}
			
			String lStrDP = StringUtility.getParameter(
					"DP", request).trim();
			String[] lStrArrDP = lStrDP.split("~");
			Double[] lDoubleDP = new Double[lStrArrDP.length];
			for (Integer lInt = 0; lInt < lStrArrDP.length; lInt++) {
				lDoubleDP[lInt] = Double
						.valueOf(lStrArrDP[lInt]);
			}
			
			String lStrDA = StringUtility.getParameter(
					"DA", request).trim();
			String[] lStrArrDA = lStrDA.split("~");
			Double[] lDoubleDA = new Double[lStrArrDA.length];
			for (Integer lInt = 0; lInt < lStrArrDA.length; lInt++) {
				lDoubleDA[lInt] = Double
						.valueOf(lStrArrDA[lInt]);
			}

			lArrTrnDcpsDeputationContribution = new TrnDcpsDeputationContribution[lLongArrdcpsEmpIds.length];

			for (Integer lInt = 0; lInt < lLongArrdcpsEmpIds.length; lInt++) {

				lObjTrnDcpsDeputationContribution = new TrnDcpsDeputationContribution();

				lObjTrnDcpsDeputationContribution
						.setDummyOfficeId(lStrDummyOfficeId);
				gLogger.info("setDummyOfficeId"+lStrDummyOfficeId);
				lObjTrnDcpsDeputationContribution.setMonthId(lLongMonthId);
				gLogger.info("setMonthId"+lLongMonthId);

				lObjTrnDcpsDeputationContribution.setFinYearId(lLongYearId);
				gLogger.info("setFinYearId"+lLongYearId);

				lObjTrnDcpsDeputationContribution.setDcpsEmpId(lLongArrdcpsEmpIds[lInt]);
				gLogger.info("setDcpsEmpId"+lLongArrdcpsEmpIds[lInt]);

				lObjTrnDcpsDeputationContribution.setContribution(lDoubleArrContributions[lInt]);
				gLogger.info("setContribution"+lDoubleArrContributions[lInt]);

				lObjTrnDcpsDeputationContribution.setChallanNo(lStrArrChallanNos[lInt]);
				gLogger.info("setChallanNo"+lStrArrChallanNos[lInt]);

				lObjTrnDcpsDeputationContribution.setChallanDate(lDateArrChallanDates[lInt]);
				gLogger.info("setChallanDate"+lDateArrChallanDates[lInt]);

				
				if(lStrUse.equals("WithEmplrContri"))
				{
					lObjTrnDcpsDeputationContribution.setChallanNoEmplr(lStrArrChallanNosEmplr[lInt]);
					gLogger.info("setChallanNoEmplr"+lStrArrChallanNosEmplr[lInt]);

					lObjTrnDcpsDeputationContribution.setChallanDateEmplr(lDateArrChallanDatesEmplr[lInt]);
					gLogger.info("setChallanDateEmplr"+lDateArrChallanDatesEmplr[lInt]);

					lObjTrnDcpsDeputationContribution.setContributionEmplr(lDoubleArrContributionsEmplr[lInt]);
					gLogger.info("setContributionEmplr"+lDoubleArrContributionsEmplr[lInt]);

				}
				else
				{
					lObjTrnDcpsDeputationContribution.setContributionEmplr(0d);
					gLogger.info("setContributionEmplr"+0d);

				}
				
				lObjTrnDcpsDeputationContribution.setLangId(lLngLangId);
				gLogger.info("setLangId"+lLngLangId);

				/////$t 20-04-2020 IRRI
				//lObjTrnDcpsDeputationContribution.setLocId(6101L);
				lObjTrnDcpsDeputationContribution.setLocId(lLngLocId);
				//gLogger.info("setLocId"+lLngLocId);
                ////$t   
				
				lObjTrnDcpsDeputationContribution.setDbId(lLngDbId);
				gLogger.info("setDbId"+lLngDbId);

				lObjTrnDcpsDeputationContribution.setCreatedPostId(gLngPostId);
				gLogger.info("setCreatedPostId"+gLngPostId);

				lObjTrnDcpsDeputationContribution.setCreatedUserId(gLngUserId);
				gLogger.info("setCreatedUserId"+gLngUserId);

				lObjTrnDcpsDeputationContribution.setCreatedDate(gDtCurrDt);
				gLogger.info("setCreatedDate"+gDtCurrDt);

			    /////$t 20-04-2020 IRRI
				//lObjTrnDcpsDeputationContribution.setTreasury("6101");
				lObjTrnDcpsDeputationContribution.setTreasury(gStrLocationCode);
				gLogger.info("setTreasury"+gStrLocationCode);
                /////$t
				
				lObjTrnDcpsDeputationContribution.setStartDate(lDateArrStartDates[lInt]);
				gLogger.info("setStartDate"+lDateArrStartDates[lInt]);

				lObjTrnDcpsDeputationContribution.setEndDate(lDateArrEndDates[lInt]);
				gLogger.info("setEndDate"+lDateArrEndDates[lInt]);

				
				lObjTrnDcpsDeputationContribution.setPayCommission(lStrArrPayCommission[lInt]);
				gLogger.info("setPayCommission"+lStrArrPayCommission[lInt]);

				lObjTrnDcpsDeputationContribution.setTypeOfPayment(lStrArrTypeOfPayment[lInt]);
				gLogger.info("setTypeOfPayment"+lStrArrTypeOfPayment[lInt]);

				lObjTrnDcpsDeputationContribution.setBasicPay(lDoubleBasic[lInt]);
				gLogger.info("setBasicPay"+lDoubleBasic[lInt]);

				lObjTrnDcpsDeputationContribution.setDP(lDoubleDP[lInt]);
				gLogger.info("setDP"+lDoubleDP[lInt]);

				lObjTrnDcpsDeputationContribution.setDA(lDoubleDA[lInt]);
				gLogger.info("setDA"+lDoubleDA[lInt]);

				//$t 2019 
				lObjTrnDcpsDeputationContribution.setSchemeCode(lStrArrChallanNos[lInt]);
				gLogger.info("setSchemeCode"+lStrArrChallanNos[lInt]);

				
				lArrTrnDcpsDeputationContribution[lInt] = lObjTrnDcpsDeputationContribution;

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			gLogger.error("Error is:" + ex, ex);
		}

		return lArrTrnDcpsDeputationContribution;
	}
}
