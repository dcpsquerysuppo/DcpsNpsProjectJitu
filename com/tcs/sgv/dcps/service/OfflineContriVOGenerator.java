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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.id.jericho.lib.html.Logger;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class OfflineContriVOGenerator extends ServiceImpl implements
		VOGeneratorService {

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
	
		ServiceLocator servLoc = (ServiceLocator) inputMap
		.get("serviceLocator");

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		
		String lStrUserType = StringUtility.getParameter("User", request).trim();

		String lStrUseType = StringUtility.getParameter("Use", request).trim();

		String lStrTotalRecords = StringUtility.getParameter("hdnCounter",
				request).trim();
		String lStrTotalRecords1 = StringUtility.getParameter("hdnCounter1",
				request).trim();
		
		Integer lIntTotalRecords = Integer.parseInt(lStrTotalRecords);
		
		
		if(lStrTotalRecords1!=null && Integer.parseInt(lStrTotalRecords1)>0)
		{
			lIntTotalRecords=Integer.parseInt(lStrTotalRecords1)+1;
		}

		TrnDcpsContribution[] lArrTrnDcpsContribution = new TrnDcpsContribution[lIntTotalRecords];

		Long lLongContributionId = null;
		String lStrDcpsEmpId = null;
		String lStrTreasuryCode = null;
		String schemeCode = null;
		String lStrDDOCode = null;
		String lStrMonthId = null;
		String lStrYearId = null;
		String lStrDelayedMonthId = null;
		String lStrDelayedYearId = null;
		String lStrBillGroupId = null;
		String lStrTypeOfPayment = null;
		String lStrPayCommission = null;
		String lStrtxtStartDate = null;
		String lStrtxtEndDate = null;
		String lStrBasic = null;
		String lStrDP = null;
		String lStrDA = null;
		String lStrContribution = null;
		//$t 2019 front
		String lStrContributionEmplr = null;
		StringBuffer dcpsContributionIds = new StringBuffer();
		String lStrDeletedContributionIndexes = StringUtility.getParameter(
				"deletedContributionIndexes", request).trim();
		String[] lStrArrDeletedContributionIndexes = null;

		if (!"".equals(lStrDeletedContributionIndexes)) {
			lStrArrDeletedContributionIndexes = lStrDeletedContributionIndexes
					.split("~");
		}
		
		String lStrElementId = StringUtility.getParameter("hidElementId",
				request).trim();
		inputMap.put("elementId", lStrElementId);
		
		String lStrType = StringUtility.getParameter("Type", request).trim();
		inputMap.put("RLType", lStrType);
		
		lStrDelayedMonthId = StringUtility.getParameter("cmbDelayedMonth", request).trim();
		lStrDelayedYearId = StringUtility.getParameter("cmbDelayedYear", request).trim();
		
		String lStrSchemeName = StringUtility.getParameter("txtSchemeName",
				request).trim();
		inputMap.put("schemeName", lStrSchemeName);
		
		lStrTreasuryCode = StringUtility.getParameter("cmbTreasuryCode",
				request).trim();
		inputMap.put("TreasuryCode", lStrTreasuryCode);

		lStrBillGroupId = StringUtility.getParameter("cmbBillGroup",
				request).trim();
		inputMap.put("cmbBillGroup", lStrBillGroupId);

		schemeCode = StringUtility.getParameter("schemeCode", request)
				.trim();
		inputMap.put("schemeCode", schemeCode);
		lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request)
				.trim();
		inputMap.put("cmbDDOCode", lStrDDOCode);

		lStrMonthId = StringUtility.getParameter("cmbMonth", request)
				.trim();
		inputMap.put("cmbMonth", lStrMonthId);
		lStrYearId = StringUtility.getParameter("cmbYear", request).trim();
		inputMap.put("cmbYear", lStrYearId);

		Integer lIntContinueFlag = 0;
		
		TrnDcpsContribution lObjTrnDcpsContribution = null;
		
		OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(TrnDcpsContribution.class, servLoc.getSessionFactory());

		for (Integer lInt = 1; lInt <= lIntTotalRecords; lInt++) {

			// Code added not to consider the deleted contributions.

			lIntContinueFlag = 0;

			if (lStrArrDeletedContributionIndexes != null) {
				for (Integer lIntDelete = 0; lIntDelete < lStrArrDeletedContributionIndexes.length; lIntDelete++) {
					System.out.println("$t in for loop:-->");
					System.out.println("$t lInt:-->"+lInt);
					System.out.println("$t lStrArrDeletedContributionIndexes:-->"+lStrArrDeletedContributionIndexes);
					System.out.println("$t lIntDelete:-->"+lIntDelete);
					if (Integer.parseInt(lStrArrDeletedContributionIndexes[lIntDelete]) == lInt) {
						System.out.println("$t in if :-->");
						lIntContinueFlag = 1;
					}
				}

				if (lIntContinueFlag == 1) {
					continue;
				}
			}

			if(StringUtility.getParameter("checkbox" + lInt, request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("checkbox" + lInt, request).trim()))
			{
				lLongContributionId = Long.parseLong(StringUtility.getParameter("checkbox" + lInt, request).trim());
				
				if (lLongContributionId == 0l)
				{
					lObjTrnDcpsContribution = new TrnDcpsContribution();
				}
				else
				{
					lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO.read(lLongContributionId);
				}
		
				dcpsContributionIds.append(StringUtility.getParameter(
						"checkbox" + lInt, request).trim());
				dcpsContributionIds.append("~");
				inputMap.put("dcpsContributionIds", dcpsContributionIds);

				lStrDcpsEmpId = StringUtility.getParameter("dcpsempId" + lInt,
						request).trim();
				
				lStrTypeOfPayment = StringUtility.getParameter("cmbTypeOfPayment" + lInt, request).trim();////$t 30-1-2021 cmbTypeOfPaymentMaster
				if("".equals(lStrTypeOfPayment))
				lStrTypeOfPayment = StringUtility.getParameter("cmbTypeOfPaymentMaster", request).trim();

				lStrPayCommission = StringUtility.getParameter(
						"cmbPayCommission" + lInt, request).trim();

				if(lStrUserType.equals("DDOAsst") && lStrUseType.equals("ViewAll"))
				{
					if(!"".equals(lStrDelayedMonthId) && !"-1".equals(lStrDelayedMonthId))
					{
						lObjTrnDcpsContribution.setDelayedMonthId(Long.valueOf(lStrDelayedMonthId));
					}
					if(!"".equals(lStrDelayedYearId) && !"-1".equals(lStrDelayedYearId))
					{
						lObjTrnDcpsContribution.setDelayedFinYearId(Long.valueOf(lStrDelayedYearId));
					}
				}
				
				lStrtxtStartDate = StringUtility.getParameter(
						"txtStartDate" + lInt, request);
				lStrtxtEndDate = StringUtility.getParameter("txtEndDate" + lInt,
						request);
				lStrBasic = StringUtility.getParameter("basic" + lInt, request)
						.trim();
				lStrDP = StringUtility.getParameter("DP" + lInt, request).trim();
				lStrDA = StringUtility.getParameter("DA" + lInt, request).trim();

				Double lDoubleDP = null;
				if (lStrDP.equals("")) {
					lDoubleDP = 0d;
				} else {
					lDoubleDP = Double.parseDouble(lStrDP);
				}

				Double lDoubleDA = null;
				if (lStrDA.equals("")) {
					lDoubleDA = 0d;
				} else {
					lDoubleDA = Double.parseDouble(lStrDA);
				}

				lStrContribution = StringUtility.getParameter("contribution" + lInt, request).trim();
				//$t 2019 front
				lStrContributionEmplr = StringUtility.getParameter("contributionEmplr" + lInt, request).trim();
				final Log gLogger = LogFactory.getLog(getClass());

				gLogger.info("lStrContributionEmplr:"+lStrContributionEmplr);
				
				Double lDoubleContribution = null;
				if (lStrContribution.equals("")) {
					lDoubleContribution = 0d;
				} else {
					lDoubleContribution = Double.parseDouble(lStrContribution);
				}
				//$t 2019 23-11 front
				Double lDoubleContributionEmplr = null;
				if (lStrContributionEmplr.equals("")) {
					lDoubleContributionEmplr = 0d;
				} else {
					lDoubleContributionEmplr = Double.parseDouble(lStrContributionEmplr);
					gLogger.info("lDoubleContributionEmplr 2:"+lDoubleContributionEmplr);

				}

				lObjTrnDcpsContribution.setDcpsEmpId(Long.parseLong(lStrDcpsEmpId));
				lObjTrnDcpsContribution.setTreasuryCode(Long
						.parseLong(lStrTreasuryCode));
				lObjTrnDcpsContribution.setDdoCode(lStrDDOCode);
				lObjTrnDcpsContribution.setDcpsDdoBillGroupId(Long
						.parseLong(lStrBillGroupId));
				lObjTrnDcpsContribution.setSchemeCode(schemeCode);
				lObjTrnDcpsContribution.setTypeOfPayment(lStrTypeOfPayment);
				lObjTrnDcpsContribution.setPayCommission(lStrPayCommission);
				lObjTrnDcpsContribution.setFinYearId(Long.parseLong(lStrYearId));
				lObjTrnDcpsContribution.setMonthId(Long.parseLong(lStrMonthId));
				// /

				if (lStrTypeOfPayment.equals("700046")
						|| lStrTypeOfPayment.equals("700047")) {
					if(!"".equals(lStrBasic)){
					lObjTrnDcpsContribution.setBasicPay(Double.parseDouble(lStrBasic));
					}else{
					lObjTrnDcpsContribution.setBasicPay(0d);//////$t 30-1-2021
					}
					lObjTrnDcpsContribution.setDP(lDoubleDP);
					lObjTrnDcpsContribution.setDA(lDoubleDA);
				}
				if (lStrTypeOfPayment.equals("700048")) {
					//lObjTrnDcpsContribution.setBasicPay(0d);
					if(!"".equals(lStrBasic))
					{
						lObjTrnDcpsContribution.setBasicPay(Double.parseDouble(lStrBasic));
					}
					else
					{
						lObjTrnDcpsContribution.setBasicPay(0d);
					}
					lObjTrnDcpsContribution.setDP(lDoubleDP);
					lObjTrnDcpsContribution.setDA(lDoubleDA);
				}
				if (lStrTypeOfPayment.equals("700049")) {
					//lObjTrnDcpsContribution.setBasicPay(0d);
					if(!"".equals(lStrBasic))
					{
						lObjTrnDcpsContribution.setBasicPay(Double.parseDouble(lStrBasic));
					}
					else
					{
						lObjTrnDcpsContribution.setBasicPay(0d);
					}
					lObjTrnDcpsContribution.setDP(lDoubleDP);
					lObjTrnDcpsContribution.setDA(lDoubleDA);
				}
				if (lStrTypeOfPayment.equals("700080")) {////PayArrearDiff $t 23-2-2021
					//lObjTrnDcpsContribution.setBasicPay(0d);
					if(!"".equals(lStrBasic))
					{
						lObjTrnDcpsContribution.setBasicPay(Double.parseDouble(lStrBasic));
					}
					else
					{
						lObjTrnDcpsContribution.setBasicPay(0d);
					}
					lObjTrnDcpsContribution.setDP(lDoubleDP);
					lObjTrnDcpsContribution.setDA(lDoubleDA);
				}
				
				        ////$t 2019 23-11 front
						//System.out.println("Third-->");
						lObjTrnDcpsContribution.setContribution(lDoubleContribution);
						///// $t 2019
						lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
					
				
				
//				//$t 2019 8-11 backend
//				Double lDoubleContributionEmplr = null;
//				//System.out.print("Offline lStrtxtStartDate---->"+ lStrtxtStartDate);
//				SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//				//$t 2019
//				DecimalFormat deci = new DecimalFormat("###");
//				String septEDate = "31/03/2019";
//
//				if ((lStrTypeOfPayment.equals("700048")||lStrTypeOfPayment.equals("700049"))&& (sdf1.parse(lStrtxtStartDate).after(sdf1.parse(septEDate)))) {
//					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//					//System.out.println("Second-->" + lDoubleContribution);
//					//System.out.println("lDoubleContribution* 0.40-->" + lDoubleContribution* 0.40);
//					/*lDoubleContributionEmplr=Double.parseDouble(deci.format(lDoubleContribution * 0.40));
//					lDoubleContributionEmplr = lDoubleContribution+ lDoubleContributionEmplr;
//					//System.out.println("19 bf^^^" + lDoubleContributionEmplr);
//					lDoubleContributionEmplr = Math.ceil(Double.parseDouble(deci.format((lDoubleContributionEmplr))));*/
//					
//				    lDoubleContributionEmplr=(lDoubleContribution * 0.40);
//					lDoubleContributionEmplr = lDoubleContribution+ lDoubleContributionEmplr;
//					lDoubleContributionEmplr = Math.ceil((lDoubleContributionEmplr));
//					
//					
//					//System.out.println("19 final af^^^" + lDoubleContributionEmplr);
//					// $t 2019
//					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
//				} else {
//					//System.out.println("Third-->");
//					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//					// $t 2019
//					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
//				}
//				if ((lStrTypeOfPayment.equals("700047")) && (sdf1.parse(lStrtxtStartDate).after(sdf1.parse(septEDate)))) {
//					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//					//System.out.println("Second-->" + lDoubleContribution);
//					//System.out.println("lDoubleContribution* 0.40-->" + lDoubleContribution* 0.40);
//					lDoubleContributionEmplr=Double.parseDouble(deci.format(lDoubleContribution * 0.40));
//					lDoubleContributionEmplr = lDoubleContribution+ lDoubleContributionEmplr;
//					//System.out.println("19 bf^^^" + lDoubleContributionEmplr);
//					lDoubleContributionEmplr = Math.ceil(Double.parseDouble(deci.format((lDoubleContributionEmplr))));
//					/*lDoubleContributionEmplr=(lDoubleContribution * 0.40);
//					lDoubleContributionEmplr = lDoubleContribution+ lDoubleContributionEmplr;
//					lDoubleContributionEmplr = Math.ceil((lDoubleContributionEmplr));*/
//					
//					
//					//System.out.println("19 final af^^^" + lDoubleContributionEmplr);
//					// $t 2019
//					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
//				} else {
//					//System.out.println("Third-->");
//					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//					// $t 2019
//					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
//				}
				
				
				
				//lObjTrnDcpsContribution.setContribution(lDoubleContribution);
				lObjTrnDcpsContribution.setStartDate(IFMSCommonServiceImpl
						.getDateFromString(lStrtxtStartDate));
				lObjTrnDcpsContribution.setEndDate(IFMSCommonServiceImpl
						.getDateFromString(lStrtxtEndDate));
				lObjTrnDcpsContribution.setEmployerContriFlag('N');
				lObjTrnDcpsContribution.setPostEmplrContriStatus(0l);
				lObjTrnDcpsContribution.setLangId(lLngLangId);
				lObjTrnDcpsContribution.setLocId(lLngLocId);
				lObjTrnDcpsContribution.setDbId(lLngDbId);
				lObjTrnDcpsContribution.setCreatedPostId(gLngPostId);
				lObjTrnDcpsContribution.setCreatedUserId(gLngUserId);
				lObjTrnDcpsContribution.setCreatedDate(gDtCurrDt);
				
				if(lStrUserType.equals("TO") && lStrUseType.equals("MissingCreditEntry"))
				{
					lObjTrnDcpsContribution.setIsMissingCredit('Y');
					lObjTrnDcpsContribution.setStatus('F');
					lObjTrnDcpsContribution.setRegStatus(1l);
				}
				else
				{
					lObjTrnDcpsContribution.setRegStatus(0l);
				}

				lArrTrnDcpsContribution[lInt - 1] = lObjTrnDcpsContribution;
			}
		

		}

		return lArrTrnDcpsContribution;
	}
	

	public ResultObject generateContriVOListSchdlr(Map inputMap) throws Exception
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		
		
		Map loginMap =(HashMap) inputMap.get("baseLoginMap");
		Long gLngPostId = Long.valueOf((loginMap.get("postId").toString()));
		Long gLngUserId = Long.valueOf((loginMap.get("userId").toString()));
		Long lLngDbId = 99L;
		Long lLngLocId = Long.valueOf((loginMap.get("locationCode").toString()));
		Long lLngLangId = 1L;
		Date gDtCurrDt = new Date();

		String lStrUserType = inputMap.get("User") != null ? inputMap.get("User").toString():"";
		String lStrUseType = inputMap.get("Use") != null ? inputMap.get("Use").toString():"";

		String lStrTotalRecords = inputMap.get("hdnCounter").toString().trim();
		Integer lIntTotalRecords = Integer.parseInt(lStrTotalRecords);
		System.out.println("Total Records"+lIntTotalRecords);

		TrnDcpsContribution[] lArrTrnDcpsContribution = new TrnDcpsContribution[lIntTotalRecords];

		Long lLongContributionId = null;
		String lStrDcpsEmpId = null;
		String lStrTreasuryCode = null;
		String schemeCode = null;
		String lStrDDOCode = null;
		String lStrMonthId = null;
		String lStrYearId = null;
		String lStrDelayedMonthId = null;
		String lStrDelayedYearId = null;
		String lStrBillGroupId = null;
		String lStrTypeOfPayment = null;
		String lStrPayCommission = null;
		String lStrtxtStartDate = null;
		String lStrtxtEndDate = null;
		String lStrBasic = null;
		String lStrDP = null;
		String lStrDA = null;
		String lStrContribution = null;
		//$t 2019 23-11 front
		String lStrContributionEmplr = null;
		StringBuffer dcpsContributionIds = new StringBuffer();
		//String lStrDeletedContributionIndexes = StringUtility.getParameter("deletedContributionIndexes", request).trim();
		String[] lStrArrDeletedContributionIndexes = null;

		/*if (!"".equals(lStrDeletedContributionIndexes))
		{
			lStrArrDeletedContributionIndexes = lStrDeletedContributionIndexes.split("~");
		}*/

		/*String lStrElementId = StringUtility.getParameter("hidElementId", request).trim();
		inputMap.put("elementId", lStrElementId);

		String lStrType = StringUtility.getParameter("Type", request).trim();
		inputMap.put("RLType", lStrType);

		lStrDelayedMonthId = StringUtility.getParameter("cmbDelayedMonth", request).trim();
		lStrDelayedYearId = StringUtility.getParameter("cmbDelayedYear", request).trim();

		String lStrSchemeName = StringUtility.getParameter("txtSchemeName", request).trim();
		inputMap.put("schemeName", lStrSchemeName);

		lStrTreasuryCode = StringUtility.getParameter("cmbTreasuryCode", request).trim();
		inputMap.put("TreasuryCode", lStrTreasuryCode);

		lStrBillGroupId = StringUtility.getParameter("cmbBillGroup", request).trim();
		inputMap.put("cmbBillGroup", lStrBillGroupId);

		schemeCode = StringUtility.getParameter("schemeCode", request).trim();
		inputMap.put("schemeCode", schemeCode);
		lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request).trim();
		inputMap.put("cmbDDOCode", lStrDDOCode);

		lStrMonthId = StringUtility.getParameter("cmbMonth", request).trim();
		inputMap.put("cmbMonth", lStrMonthId);
		lStrYearId = StringUtility.getParameter("cmbYear", request).trim();
		inputMap.put("cmbYear", lStrYearId);
*/
		inputMap.get("Type").toString();
		String lStrType = inputMap.get("Type") != null ? inputMap.get("Type").toString():"";
		inputMap.put("RLType", lStrType);

//		lStrTreasuryCode = StringUtility.getParameter("cmbTreasuryCode", request).trim(); // add in map
		
		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, servLoc.getSessionFactory());
		
		lStrDDOCode = inputMap.get("cmbDDOCode") != null ? inputMap.get("cmbDDOCode").toString():"";
		inputMap.put("cmbDDOCode", lStrDDOCode);
		
		lStrTreasuryCode = lObjDcpsCommonDAO.getTreasuryCodeForDDO(lStrDDOCode);
		inputMap.put("TreasuryCode", lStrTreasuryCode);

		lStrBillGroupId = inputMap.get("cmbBillGroup") != null ? inputMap.get("cmbBillGroup").toString():"";
		inputMap.put("cmbBillGroup", lStrBillGroupId);

		schemeCode = inputMap.get("schemeCodeForBG") != null ? inputMap.get("schemeCodeForBG").toString():"";
		inputMap.put("schemeCode", schemeCode);

		lStrMonthId = inputMap.get("cmbMonth") != null ? inputMap.get("cmbMonth").toString():"";
		inputMap.put("cmbMonth", lStrMonthId);
		
		lStrYearId = inputMap.get("cmbYear") != null ? inputMap.get("cmbYear").toString():"";
		inputMap.put("cmbYear", lStrYearId);
		
		List empList = (List) (inputMap.get("empList") != null ? inputMap.get("empList"):null);
		
		//System.out.println("Size of EmpList is"+empList.size());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		Integer lIntContinueFlag = 0;

		TrnDcpsContribution lObjTrnDcpsContribution = null;

		OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(TrnDcpsContribution.class, servLoc.getSessionFactory());
		

		for (Integer lInt = 1; lInt <= lIntTotalRecords; lInt++)
		{

			// Code added not to consider the deleted contributions.

			lIntContinueFlag = 0;

			if (lStrArrDeletedContributionIndexes != null)
			{
				for (Integer lIntDelete = 0; lIntDelete < lStrArrDeletedContributionIndexes.length; lIntDelete++)
				{
					if (Integer.parseInt(lStrArrDeletedContributionIndexes[lIntDelete]) == lInt)
					{
						lIntContinueFlag = 1;
					}
				}

				if (lIntContinueFlag == 1)
				{
					continue;
				}
			}

			//lLongContributionId = Long.parseLong(StringUtility.getParameter("checkbox" + lInt, request).trim());
			lLongContributionId = 0l;

			if (lLongContributionId == 0l)
			{
				lObjTrnDcpsContribution = new TrnDcpsContribution();
			}
			else
			{
				lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO.read(lLongContributionId);
			}

			//dcpsContributionIds.append(StringUtility.getParameter("checkbox" + lInt, request).trim());
			dcpsContributionIds.append("0");
			dcpsContributionIds.append("~");
			inputMap.put("dcpsContributionIds", dcpsContributionIds);

			//lStrDcpsEmpId = StringUtility.getParameter("dcpsempId" + lInt, request).trim();
			Object[] lArrObjectEmpList = (Object[]) empList.get(lInt-1);
			lStrDcpsEmpId =  lArrObjectEmpList[0].toString();

			//lStrTypeOfPayment = StringUtility.getParameter("cmbTypeOfPayment" + lInt, request).trim();
			lStrTypeOfPayment = "700046";

			//lStrPayCommission = StringUtility.getParameter("cmbPayCommission" + lInt, request).trim();
			lStrPayCommission = lArrObjectEmpList[3].toString();

			/*if (lStrUserType.equals("DDOAsst") && lStrUseType.equals("ViewAll"))
			{
				if (!"".equals(lStrDelayedMonthId) && !"-1".equals(lStrDelayedMonthId))
				{
					lObjTrnDcpsContribution.setDelayedMonthId(Long.valueOf(lStrDelayedMonthId));
				}
				if (!"".equals(lStrDelayedYearId) && !"-1".equals(lStrDelayedYearId))
				{
					lObjTrnDcpsContribution.setDelayedFinYearId(Long.valueOf(lStrDelayedYearId));
				}
			}*/

			lStrtxtStartDate = inputMap.get("schdlStartDate") != null ? inputMap.get("schdlStartDate").toString():null;
			lStrtxtEndDate =inputMap.get("schdlEndDate") != null ? inputMap.get("schdlEndDate").toString():null; 
				
			lStrBasic = lArrObjectEmpList[4].toString();
			lStrDP = lArrObjectEmpList[17].toString();
			lStrDA = lArrObjectEmpList[18].toString();
			Double lDoubleDP = null;
			if (lStrDP.equals(""))
			{
				lDoubleDP = 0d;
			}
			else
			{
				lDoubleDP = Double.parseDouble(lStrDP);
			}

			Double lDoubleDA = null;
			if (lStrDA.equals(""))
			{
				lDoubleDA = 0d;
			}
			else
			{
				lDoubleDA = Double.parseDouble(lStrDA);
			}

			lStrContribution = lArrObjectEmpList[19].toString();
			//$t 2019 23-11 front
			lStrContributionEmplr = lArrObjectEmpList[22].toString();
			
			Double lDoubleContribution = null;
			if (lStrContribution.equals(""))
			{
				lDoubleContribution = 0d;
			}
			else
			{
				lDoubleContribution = Double.parseDouble(lStrContribution);
			}
			//$t 2019 23-11 front
			Double lDoubleContributionEmplr = null;
			if (lStrContributionEmplr.equals("")) {
				lDoubleContributionEmplr = 0d;
			} else {
				lDoubleContributionEmplr = Double.parseDouble(lStrContributionEmplr);
			}
			
			
			lObjTrnDcpsContribution.setDcpsEmpId(Long.parseLong(lStrDcpsEmpId));
			lObjTrnDcpsContribution.setTreasuryCode(Long.parseLong(lStrTreasuryCode));
			lObjTrnDcpsContribution.setDdoCode(lStrDDOCode);
			lObjTrnDcpsContribution.setDcpsDdoBillGroupId(Long.parseLong(lStrBillGroupId));
			lObjTrnDcpsContribution.setSchemeCode(schemeCode);
			lObjTrnDcpsContribution.setTypeOfPayment(lStrTypeOfPayment);
			lObjTrnDcpsContribution.setPayCommission(lStrPayCommission);
			lObjTrnDcpsContribution.setFinYearId(Long.parseLong(lStrYearId));
			lObjTrnDcpsContribution.setMonthId(Long.parseLong(lStrMonthId));
			// /

			if (lStrTypeOfPayment.equals("700046") || lStrTypeOfPayment.equals("700047"))
			{
				lObjTrnDcpsContribution.setBasicPay(Double.parseDouble(lStrBasic));
				lObjTrnDcpsContribution.setDP(lDoubleDP);
				lObjTrnDcpsContribution.setDA(lDoubleDA);
			}
			if (lStrTypeOfPayment.equals("700048"))
			{
				lObjTrnDcpsContribution.setBasicPay(0d);
				lObjTrnDcpsContribution.setDP(lDoubleDP);
				lObjTrnDcpsContribution.setDA(lDoubleDA);
			}
			if (lStrTypeOfPayment.equals("700049"))
			{
				lObjTrnDcpsContribution.setBasicPay(0d);
				lObjTrnDcpsContribution.setDP(lDoubleDP);
				lObjTrnDcpsContribution.setDA(lDoubleDA);
			}
			if (lStrTypeOfPayment.equals("700080"))////PayArrearDiff $t 23-2-2021
			{
				lObjTrnDcpsContribution.setBasicPay(0d);
				lObjTrnDcpsContribution.setDP(lDoubleDP);
				lObjTrnDcpsContribution.setDA(lDoubleDA);
			}
			
			//$t 2019 23-11 front
//			//System.out.println("Third-->");
			lObjTrnDcpsContribution.setContribution(lDoubleContribution);
			lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
			
			

//			//$t 2019 12-12 backend
//			Double lDoubleContributionEmplr = null;
//			//System.out.print("Offline lStrtxtStartDate---->"+ lStrtxtStartDate);
//			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//			//$t 2019
//			DecimalFormat deci = new DecimalFormat("###");
//			String septEDate = "31/03/2019";
//
//			if ((lStrTypeOfPayment.equals("700048")||lStrTypeOfPayment.equals("700049"))&& (sdf1.parse(lStrtxtStartDate).after(sdf1.parse(septEDate)))) {
//				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//				//System.out.println("Second-->" + lDoubleContribution);
//				//System.out.println("lDoubleContribution* 0.40-->" + lDoubleContribution* 0.40);
//				/*lDoubleContributionEmplr=Double.parseDouble(deci.format(lDoubleContribution * 0.40));
//				lDoubleContributionEmplr = lDoubleContribution+ lDoubleContributionEmplr;
//				//System.out.println("19 bf^^^" + lDoubleContributionEmplr);
//				lDoubleContributionEmplr = Math.ceil(Double.parseDouble(deci.format((lDoubleContributionEmplr))));*/
//				
//			    lDoubleContributionEmplr=(lDoubleContribution * 0.40);
//				lDoubleContributionEmplr = lDoubleContribution+ lDoubleContributionEmplr;
//				lDoubleContributionEmplr = Math.ceil((lDoubleContributionEmplr));
//				
//				
//				//System.out.println("19 final af^^^" + lDoubleContributionEmplr);
//				// $t 2019
//				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
//			} else {
//				//System.out.println("Third-->");
//				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//				// $t 2019
//				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
//			}
//			if ((lStrTypeOfPayment.equals("700047")) && (sdf1.parse(lStrtxtStartDate).after(sdf1.parse(septEDate)))) {
//				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//				//System.out.println("Second-->" + lDoubleContribution);
//				//System.out.println("lDoubleContribution* 0.40-->" + lDoubleContribution* 0.40);
//				lDoubleContributionEmplr=Double.parseDouble(deci.format(lDoubleContribution * 0.40));
//				lDoubleContributionEmplr = lDoubleContribution+ lDoubleContributionEmplr;
//				//System.out.println("19 bf^^^" + lDoubleContributionEmplr);
//				lDoubleContributionEmplr = Math.ceil(Double.parseDouble(deci.format((lDoubleContributionEmplr))));
//				/*lDoubleContributionEmplr=(lDoubleContribution * 0.40);
//				lDoubleContributionEmplr = lDoubleContribution+ lDoubleContributionEmplr;
//				lDoubleContributionEmplr = Math.ceil((lDoubleContributionEmplr));*/
//				
//				
//				//System.out.println("19 final af^^^" + lDoubleContributionEmplr);
//				// $t 2019
//				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
//			} else {
//				//System.out.println("Third-->");
//				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//				// $t 2019
//				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
//			}

			
//			//$t 2019 8-11 backend
//			Double lDoubleContributionEmplr = null;
//			//System.out.print("Offline lStrtxtStartDate---->"+ lStrtxtStartDate);
//			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//			//$t 2019
//			DecimalFormat deci = new DecimalFormat("##");
//			String septEDate = "31/03/2019";
//
//			if ((lStrTypeOfPayment.equals("700047") || lStrTypeOfPayment.equals("700048")||lStrTypeOfPayment.equals("700049"))&& (sdf1.parse(lStrtxtStartDate).after(sdf1.parse(septEDate)))) {
//				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//				//System.out.println("Second-->" + lDoubleContribution);
//				//System.out.println("lDoubleContribution* 0.40-->" + lDoubleContribution* 0.40);
//				lDoubleContributionEmplr=Double.parseDouble(deci.format(lDoubleContribution * 0.40));
//				lDoubleContributionEmplr = lDoubleContribution+ lDoubleContributionEmplr;
//				//System.out.println("19 bf^^^" + lDoubleContributionEmplr);
//				lDoubleContributionEmplr = Math.ceil(Double.parseDouble(deci.format((lDoubleContributionEmplr))));
//				//System.out.println("19 final af^^^" + lDoubleContributionEmplr);
//				// $t 2019
//				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
//			} else {
//				//System.out.println("Third-->");
//				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//				// $t 2019
//				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
//			}

			
			//lObjTrnDcpsContribution.setContribution(lDoubleContribution);
			lObjTrnDcpsContribution.setStartDate(sdf.parse(lStrtxtStartDate));
			lObjTrnDcpsContribution.setEndDate(sdf.parse(lStrtxtEndDate));
			lObjTrnDcpsContribution.setEmployerContriFlag('N');
			lObjTrnDcpsContribution.setPostEmplrContriStatus(0l);
			lObjTrnDcpsContribution.setLangId(lLngLangId);
			lObjTrnDcpsContribution.setLocId(lLngLocId);
			lObjTrnDcpsContribution.setDbId(lLngDbId);
			lObjTrnDcpsContribution.setCreatedPostId(gLngPostId);
			lObjTrnDcpsContribution.setCreatedUserId(gLngUserId);
			lObjTrnDcpsContribution.setCreatedDate(gDtCurrDt);

			lArrTrnDcpsContribution[lInt - 1] = lObjTrnDcpsContribution;

		}

		
		inputMap.put("lArrTrnDcpsContributions", lArrTrnDcpsContribution);
		objRes.setResultValue(inputMap);
		return objRes;
		//return 
	}
	
}


//
////$t 2019 till 8-11 backup 
///* Copyright TCS 2011, All Rights Reserved.
// * 
// * 
// ******************************************************************************
// ***********************Modification History***********************************
// *  Date   				Initials	     Version		Changes and additions
// ******************************************************************************
// * 	Mar 21, 2011		Vihan Khatri								
// *******************************************************************************
// */
//
///**
// * Class Description - 
// *
// *
// * @author Vihan Khatri
// * @version 0.1
// * @since JDK 5.0
// * Mar 21, 2011
// */
///* Copyright TCS 2011, All Rights Reserved.
// * 
// * 
// ******************************************************************************
// ***********************Modification History***********************************
// *  Date   				Initials	     Version		Changes and additions
// ******************************************************************************
// * 	Mar 17, 2011		Vihan Khatri								
// *******************************************************************************
// */
///**
// * Class Description - 
// *
// *
// * @author Vihan Khatri
// * @version 0.1
// * @since JDK 5.0
// * Mar 17, 2011
// */
//package com.tcs.sgv.dcps.service;
//
////com.tcs.sgv.dcps.service.DCPSNomineeDtlsVOGenerator
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import au.id.jericho.lib.html.Logger;
//
//import com.ibm.icu.util.GregorianCalendar;
//import com.tcs.sgv.common.helper.SessionHelper;
//import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
//import com.tcs.sgv.common.utils.DBUtility;
//import com.tcs.sgv.common.utils.StringUtility;
//import com.tcs.sgv.core.constant.ErrorConstants;
//import com.tcs.sgv.core.service.ServiceImpl;
//import com.tcs.sgv.core.service.ServiceLocator;
//import com.tcs.sgv.core.service.VOGeneratorService;
//import com.tcs.sgv.core.valueobject.ResultObject;
//import com.tcs.sgv.dcps.dao.OfflineContriDAO;
//import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
//import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
//
//public class OfflineContriVOGenerator extends ServiceImpl implements
//		VOGeneratorService {
//
//	/*
//	 * @Description : Method to generate VO For DcpsEmpNmnMst.
//	 * 
//	 * @Input : Map : inputMap
//	 * 
//	 * @Output : ResultObject : ResultObject
//	 */
//
//	@Override
//	public ResultObject generateMap(Map inputMap) {
//
//		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
//		inputMap.get("requestObj");
//
//		TrnDcpsContribution[] lArrTrnDcpsContributions = null;
//
//		try {
//			lArrTrnDcpsContributions = generateContributionsVOList(inputMap);
//			inputMap.put("lArrTrnDcpsContributions", lArrTrnDcpsContributions);
//			objRes.setResultValue(inputMap);
//		} catch (Exception ex) {
//			objRes.setResultValue(null);
//			objRes.setThrowable(ex);
//			objRes.setResultCode(ErrorConstants.ERROR);
//			objRes.setViewName("errorPage");
//			ex.printStackTrace();
//		}
//		return objRes;
//	}
//
//	/*
//	 * @Description : Method to generate VO For DCPSNomineeDtlsVOGenerator.
//	 * 
//	 * @Input : Map : inputMap
//	 * 
//	 * @Output : ResultObject : DcpsEmpNmnMst[]
//	 */
//
//	public TrnDcpsContribution[] generateContributionsVOList(Map inputMap)
//			throws Exception {
//		
//		HttpServletRequest request = (HttpServletRequest) inputMap
//				.get("requestObj");
//	
//		ServiceLocator servLoc = (ServiceLocator) inputMap
//		.get("serviceLocator");
//
//		Long gLngPostId = SessionHelper.getPostId(inputMap);
//		Long gLngUserId = SessionHelper.getUserId(inputMap);
//		Long lLngDbId = SessionHelper.getDbId(inputMap);
//		Long lLngLocId = SessionHelper.getLocationId(inputMap);
//		Long lLngLangId = SessionHelper.getLangId(inputMap);
//		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
//		
//		String lStrUserType = StringUtility.getParameter("User", request).trim();
//
//		String lStrUseType = StringUtility.getParameter("Use", request).trim();
//
//		String lStrTotalRecords = StringUtility.getParameter("hdnCounter",
//				request).trim();
//		String lStrTotalRecords1 = StringUtility.getParameter("hdnCounter1",
//				request).trim();
//		
//		Integer lIntTotalRecords = Integer.parseInt(lStrTotalRecords);
//		
//		
//		if(lStrTotalRecords1!=null && Integer.parseInt(lStrTotalRecords1)>0)
//		{
//			lIntTotalRecords=Integer.parseInt(lStrTotalRecords1)+1;
//		}
//
//		TrnDcpsContribution[] lArrTrnDcpsContribution = new TrnDcpsContribution[lIntTotalRecords];
//
//		Long lLongContributionId = null;
//		String lStrDcpsEmpId = null;
//		String lStrTreasuryCode = null;
//		String schemeCode = null;
//		String lStrDDOCode = null;
//		String lStrMonthId = null;
//		String lStrYearId = null;
//		String lStrDelayedMonthId = null;
//		String lStrDelayedYearId = null;
//		String lStrBillGroupId = null;
//		String lStrTypeOfPayment = null;
//		String lStrPayCommission = null;
//		String lStrtxtStartDate = null;
//		String lStrtxtEndDate = null;
//		String lStrBasic = null;
//		String lStrDP = null;
//		String lStrDA = null;
//		String lStrContribution = null;
//		//$t 2019 24/10
//		String lStrContributionEmplr=null;
//		StringBuffer dcpsContributionIds = new StringBuffer();
//		String lStrDeletedContributionIndexes = StringUtility.getParameter(
//				"deletedContributionIndexes", request).trim();
//		String[] lStrArrDeletedContributionIndexes = null;
//
//		if (!"".equals(lStrDeletedContributionIndexes)) {
//			lStrArrDeletedContributionIndexes = lStrDeletedContributionIndexes
//					.split("~");
//		}
//		
//		String lStrElementId = StringUtility.getParameter("hidElementId",
//				request).trim();
//		inputMap.put("elementId", lStrElementId);
//		
//		String lStrType = StringUtility.getParameter("Type", request).trim();
//		inputMap.put("RLType", lStrType);
//		
//		lStrDelayedMonthId = StringUtility.getParameter("cmbDelayedMonth", request).trim();
//		lStrDelayedYearId = StringUtility.getParameter("cmbDelayedYear", request).trim();
//		
//		String lStrSchemeName = StringUtility.getParameter("txtSchemeName",
//				request).trim();
//		inputMap.put("schemeName", lStrSchemeName);
//		
//		lStrTreasuryCode = StringUtility.getParameter("cmbTreasuryCode",
//				request).trim();
//		inputMap.put("TreasuryCode", lStrTreasuryCode);
//
//		lStrBillGroupId = StringUtility.getParameter("cmbBillGroup",
//				request).trim();
//		inputMap.put("cmbBillGroup", lStrBillGroupId);
//
//		schemeCode = StringUtility.getParameter("schemeCode", request)
//				.trim();
//		inputMap.put("schemeCode", schemeCode);
//		lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request)
//				.trim();
//		inputMap.put("cmbDDOCode", lStrDDOCode);
//
//		lStrMonthId = StringUtility.getParameter("cmbMonth", request)
//				.trim();
//		inputMap.put("cmbMonth", lStrMonthId);
//		lStrYearId = StringUtility.getParameter("cmbYear", request).trim();
//		inputMap.put("cmbYear", lStrYearId);
//
//		Integer lIntContinueFlag = 0;
//		
//		TrnDcpsContribution lObjTrnDcpsContribution = null;
//		
//		OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(TrnDcpsContribution.class, servLoc.getSessionFactory());
//
//		for (Integer lInt = 1; lInt <= lIntTotalRecords; lInt++) {
//
//			// Code added not to consider the deleted contributions.
//
//			lIntContinueFlag = 0;
//
//			if (lStrArrDeletedContributionIndexes != null) {
//				for (Integer lIntDelete = 0; lIntDelete < lStrArrDeletedContributionIndexes.length; lIntDelete++) {
//					if (Integer
//							.parseInt(lStrArrDeletedContributionIndexes[lIntDelete]) == lInt) {
//						lIntContinueFlag = 1;
//					}
//				}
//
//				if (lIntContinueFlag == 1) {
//					continue;
//				}
//			}
//
//			if(StringUtility.getParameter("checkbox" + lInt, request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("checkbox" + lInt, request).trim()))
//			{
//				lLongContributionId = Long.parseLong(StringUtility.getParameter("checkbox" + lInt, request).trim());
//				
//				if (lLongContributionId == 0l)
//				{
//					lObjTrnDcpsContribution = new TrnDcpsContribution();
//				}
//				else
//				{
//					lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO.read(lLongContributionId);
//				}
//		
//				dcpsContributionIds.append(StringUtility.getParameter(
//						"checkbox" + lInt, request).trim());
//				dcpsContributionIds.append("~");
//				inputMap.put("dcpsContributionIds", dcpsContributionIds);
//
//				lStrDcpsEmpId = StringUtility.getParameter("dcpsempId" + lInt,
//						request).trim();
//
//				lStrTypeOfPayment = StringUtility.getParameter(
//						"cmbTypeOfPayment" + lInt, request).trim();
//
//				lStrPayCommission = StringUtility.getParameter(
//						"cmbPayCommission" + lInt, request).trim();
//
//				if(lStrUserType.equals("DDOAsst") && lStrUseType.equals("ViewAll"))
//				{
//					if(!"".equals(lStrDelayedMonthId) && !"-1".equals(lStrDelayedMonthId))
//					{
//						lObjTrnDcpsContribution.setDelayedMonthId(Long.valueOf(lStrDelayedMonthId));
//					}
//					if(!"".equals(lStrDelayedYearId) && !"-1".equals(lStrDelayedYearId))
//					{
//						lObjTrnDcpsContribution.setDelayedFinYearId(Long.valueOf(lStrDelayedYearId));
//					}
//				}
//				
//				lStrtxtStartDate = StringUtility.getParameter("txtStartDate" + lInt, request);
//				System.out.print("Offline lStrtxtStartDate---->"+lStrtxtStartDate);
//				lStrtxtEndDate = StringUtility.getParameter("txtEndDate" + lInt,request);
//				System.out.print("Offline lStrtxtEndDate---->"+lStrtxtEndDate);
//				
//				lStrBasic = StringUtility.getParameter("basic" + lInt, request).trim();
//				lStrDP = StringUtility.getParameter("DP" + lInt, request).trim();
//				lStrDA = StringUtility.getParameter("DA" + lInt, request).trim();
//
//				Double lDoubleDP = null;
//				if (lStrDP.equals("")) {
//					lDoubleDP = 0d;
//				} else {
//					lDoubleDP = Double.parseDouble(lStrDP);
//				}
//
//				Double lDoubleDA = null;
//				if (lStrDA.equals("")) {
//					lDoubleDA = 0d;
//				} else {
//					lDoubleDA = Double.parseDouble(lStrDA);
//				}
//
//				lStrContribution = StringUtility.getParameter("contribution" + lInt, request).trim();
//				//$t 2019
//				//lStrContributionEmplr = StringUtility.getParameter("contributionEmplr" + lInt, request).trim();
//				//$t 2019 24/10
//				Double lDoubleContribution = null;
//				Double lDoubleContributionEmplr = null;
//				if (lStrContribution.equals("")) {
//					lDoubleContribution = 0d;
//				} else {
//					lDoubleContribution = Double.parseDouble(lStrContribution);
//				}
//				//$t 2019
////				Double lDoubleContributionEmplr = null;
////				if (lStrContributionEmplr.equals("")) {
////					lDoubleContributionEmplr = 0d;
////				} else {
////					lDoubleContributionEmplr = Double.parseDouble(lStrContributionEmplr);
////				}
//
//				lObjTrnDcpsContribution.setDcpsEmpId(Long.parseLong(lStrDcpsEmpId));
//				lObjTrnDcpsContribution.setTreasuryCode(Long
//						.parseLong(lStrTreasuryCode));
//				lObjTrnDcpsContribution.setDdoCode(lStrDDOCode);
//				lObjTrnDcpsContribution.setDcpsDdoBillGroupId(Long
//						.parseLong(lStrBillGroupId));
//				lObjTrnDcpsContribution.setSchemeCode(schemeCode);
//				lObjTrnDcpsContribution.setTypeOfPayment(lStrTypeOfPayment);
//				lObjTrnDcpsContribution.setPayCommission(lStrPayCommission);
//				lObjTrnDcpsContribution.setFinYearId(Long.parseLong(lStrYearId));
//				lObjTrnDcpsContribution.setMonthId(Long.parseLong(lStrMonthId));
//				
//				
//				if (lStrTypeOfPayment.equals("700046")
//						|| lStrTypeOfPayment.equals("700047")) {
//					lObjTrnDcpsContribution.setBasicPay(Double.parseDouble(lStrBasic));
//					lObjTrnDcpsContribution.setDP(lDoubleDP);
//					lObjTrnDcpsContribution.setDA(lDoubleDA);
//				}
//				if (lStrTypeOfPayment.equals("700048")) {
//					//lObjTrnDcpsContribution.setBasicPay(0d);
//					if(!"".equals(lStrBasic))
//					{
//						lObjTrnDcpsContribution.setBasicPay(Double.parseDouble(lStrBasic));
//					}
//					else
//					{
//						lObjTrnDcpsContribution.setBasicPay(0d);
//					}
//					lObjTrnDcpsContribution.setDP(lDoubleDP);
//					lObjTrnDcpsContribution.setDA(lDoubleDA);
//				}
//				if (lStrTypeOfPayment.equals("700049")) {
//					//lObjTrnDcpsContribution.setBasicPay(0d);
//					if(!"".equals(lStrBasic))
//					{
//						lObjTrnDcpsContribution.setBasicPay(Double.parseDouble(lStrBasic));
//					}
//					else
//					{
//						lObjTrnDcpsContribution.setBasicPay(0d);
//					}
//					lObjTrnDcpsContribution.setDP(lDoubleDP);
//					lObjTrnDcpsContribution.setDA(lDoubleDA);
//				}
//				
////				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////				//$t 2019
////				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
//				//$t 2019 24/10
//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//				String octSDate="30/09/2019";
//				
//	            if((lStrTypeOfPayment.equals("700047"))// || lStrTypeOfPayment.equals("700048")|| lStrTypeOfPayment.equals("700049") 
//	            		&&  (sdf.parse(octSDate).after(sdf.parse(lStrtxtStartDate)))){
//	                lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//					
//	                System.out.println("Second-->"+lDoubleContribution);
//					lDoubleContributionEmplr=lDoubleContribution+(lDoubleContribution*0.40);
//					System.out.println("19 bf^^^"+lDoubleContributionEmplr);
//					lDoubleContributionEmplr=(Math.round(lDoubleContributionEmplr)*100.0)/100.0;
//					System.out.println("19 af^^^"+lDoubleContributionEmplr);
//					lDoubleContributionEmplr = (double) Math.ceil(lDoubleContributionEmplr);
//					System.out.println("19 af^^^"+lDoubleContributionEmplr);
//					//$t 2019
//					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
//				}else{
//					System.out.println("Third-->");
//					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//					//$t 2019
//					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
//					}
//
//				
//				
//				
//				
//////				//$t 2019
////				//else code on line 700???? 
////				//& startdate=31/03/2019 & end date=01/04/2019 ????
////				Double lDoubleContributionEmplr = null;
////				DecimalFormat deci=new DecimalFormat("##.##");
////						  //Double.parseDouble(deci.format(value));
////						  //Double a=Math.ceil(value);
////				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
////				String aprSDate="01/04/2019";
////				String marEDate="31/03/2019";
////				Date dateBefore = sdf.parse(lStrtxtStartDate);
////			    Date dateAfter = sdf.parse(lStrtxtEndDate);
////			    long difference = dateAfter.getTime() - dateBefore.getTime();
////			    float daysBetween = ((difference / (1000*60*60*24))+1);
////			    System.out.println("Number of Days between dates-->: "+daysBetween);
////			    double oneDayBasic =Double.parseDouble(deci.format((Double.parseDouble(lStrBasic))/daysBetween));
////			    System.out.println("oneDayBasic-->: "+oneDayBasic);
////			    double oneDayDA =Double.parseDouble(deci.format((Double.parseDouble(lStrDA))/daysBetween));
////			    System.out.println("oneDayDA-->: "+oneDayDA);
////			    double oneDayDP =Double.parseDouble(deci.format((Double.parseDouble(lStrDP))/daysBetween));
////			    System.out.println("oneDayDP-->: "+oneDayDP);
////			    
////			    if((lStrTypeOfPayment.equals("700047") || lStrTypeOfPayment.equals("700048")|| lStrTypeOfPayment.equals("700049")) 
////	            		&& (sdf.parse(lStrtxtStartDate).before(sdf.parse(marEDate)))&& (sdf.parse(lStrtxtEndDate).after(sdf.parse(aprSDate)))){
////			    	System.out.println("First-->");
////			    	Date dateBefore1 = sdf.parse(lStrtxtStartDate);
////				    Date dateAfter1 = sdf.parse(marEDate);
////				    long difference1 = dateAfter1.getTime() - dateBefore1.getTime();
////				    float daysBetween1 = ((difference1 / (1000*60*60*24))+1);
////				    System.out.println("before march between dates-->: "+daysBetween1);
////				    double basicMarch =Double.parseDouble(deci.format((oneDayBasic * daysBetween1)));
////				    System.out.println("basicBfmarch-->: "+basicMarch);
////				    double DAMarch =Double.parseDouble(deci.format((oneDayDA * daysBetween1)));
////				    System.out.println("basicBfmarch-->: "+DAMarch);
////				    double DPMarch =Double.parseDouble(deci.format((oneDayDP * daysBetween1)));
////				    System.out.println("basicBfmarch-->: "+DPMarch);
////				    double c1=Math.ceil(Double.parseDouble(deci.format(((basicMarch+DAMarch+DPMarch)*0.10))));
////				    Date dateBefore2 = sdf.parse(aprSDate);
////				    Date dateAfter2 = sdf.parse(lStrtxtEndDate);
////				    long difference2 = dateAfter2.getTime() - dateBefore2.getTime();
////				    float daysBetween2 = ((difference2 / (1000*60*60*24))+1);
////				    System.out.println("Number of Days between dates-->: "+daysBetween2);
////				    double basicApr =Double.parseDouble(deci.format((oneDayBasic * daysBetween2)));
////				    System.out.println("basicAfApr-->: "+basicApr);
////				    double DAApr =Double.parseDouble(deci.format((oneDayDA * daysBetween2)));
////				    System.out.println("DAAfApr-->: "+DAApr);
////				    double DPApr =Double.parseDouble(deci.format((oneDayDP * daysBetween2)));
////				    System.out.println("DPAfApr-->: "+DPApr);
////				    double c2=Math.ceil(Double.parseDouble(deci.format(((basicApr+DAApr+DPApr)*0.14))));
////				    lDoubleContributionEmplr=c1+c2;
////				    System.out.println("lDoubleContributionEmplr-->: "+lDoubleContributionEmplr);
////				    lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////					//$t 2019
////					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
////			    }else if((lStrTypeOfPayment.equals("700047") || lStrTypeOfPayment.equals("700048")|| lStrTypeOfPayment.equals("700049")) &&  (sdf.parse(lStrtxtStartDate).after(sdf.parse(marEDate)))){
////					System.out.println("Second-->");
////					System.out.println("19 basic^^^"+Double.parseDouble(lStrBasic));
////					System.out.println("19 DA^^^"+Double.parseDouble(lStrDA));
////					System.out.println("19 DP^^^"+Double.parseDouble(lStrDP));
////					lDoubleContributionEmplr=Double.parseDouble(deci.format((Double.parseDouble(lStrBasic)+Double.parseDouble(lStrDA)+Double.parseDouble(lStrDP))*0.14));
////					System.out.println("19 bf^^^"+lDoubleContributionEmplr);
////					lDoubleContributionEmplr = (double) Math.ceil(lDoubleContributionEmplr);
////					System.out.println("19 af^^^"+lDoubleContributionEmplr);
////					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////					//$t 2019
////					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
////				}else{
////					System.out.println("Third-->");
////					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////					//$t 2019
////					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
////					}
//
//			       
//			    //Double lDoubleContributionEmplr = null;
////	            Double lDoubleContributionEmplr1 = null;
////	            Double lDoubleContributionEmplrF = null;
////
////				
////	            if((lStrTypeOfPayment.equals("700047") || lStrTypeOfPayment.equals("700048")|| lStrTypeOfPayment.equals("700049")) 
////	            		&& (sdf.parse(lStrtxtStartDate).before(sdf.parse(marEDate)))&& (sdf.parse(lStrtxtEndDate).after(sdf.parse(aprSDate)))){
////					//march
////	            	System.out.println("first-->");
////	            	lDoubleContributionEmplr=((lDoubleContribution)/diffMonth);//Double.parseDouble(lStrBasic));
////	            	System.out.println("lDoubleContributionEmplr-->"+lDoubleContributionEmplr);
////					lDoubleContributionEmplr1 =(lDoubleContributionEmplr*0.40);
////					System.out.println("lDoubleContributionEmplr1-->"+lDoubleContributionEmplr1);
////					lDoubleContributionEmplrF = (lDoubleContributionEmplr*diffMonth2+lDoubleContributionEmplr1)*diffMonth1;
////					System.out.println("lDoubleContributionEmplrF1-->"+lDoubleContributionEmplrF);
////					lDoubleContributionEmplrF=(Math.round(lDoubleContributionEmplrF+lDoubleContributionEmplr*diffMonth2)*100.0)/100.0;
////					System.out.println("lDoubleContributionEmplrF2-->"+lDoubleContributionEmplrF);
////					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////					System.out.println("lDoubleContribution-->"+lDoubleContribution);
////					lDoubleContributionEmplrF = (double) Math.ceil(lDoubleContributionEmplrF);
////						//$t 2019
////						lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplrF);
////						System.out.println("lDoubleContributionEmplrF-->"+lDoubleContributionEmplrF);
////				}else if((lStrTypeOfPayment.equals("700047") || lStrTypeOfPayment.equals("700048")|| lStrTypeOfPayment.equals("700049")) &&  (sdf.parse(lStrtxtStartDate).after(sdf.parse(marEDate)))){
////					System.out.println("Second-->");
////					//System.out.println("19 basic^^^"+Double.parseDouble(lStrBasic));
////					//System.out.println("19 DA^^^"+Double.parseDouble(lStrDA));
////					//System.out.println("19 DP^^^"+Double.parseDouble(lStrDP));
////					lDoubleContributionEmplr=(Double.parseDouble(lStrBasic)+Double.parseDouble(lStrDA)+Double.parseDouble(lStrDP))*0.14;
////					lDoubleContributionEmplr=(Math.round(lDoubleContributionEmplr)*100.0)/100.0;
////					//lDoubleContributionEmplr=Math.round(((100000d+9000d)*0.14)* 100.0) / 100.0;
////					 System.out.println("19 bf^^^"+lDoubleContributionEmplr);
////					lDoubleContributionEmplr = (double) Math.ceil(lDoubleContributionEmplr);
////					System.out.println("19 af^^^"+lDoubleContributionEmplr);
////					//System.out.println("19 EmplrContribution^^^"+lDoubleContributionEmplr);
////					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////					//$t 2019
////					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
////				}else{
////					System.out.println("Third-->");
////					lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////					//$t 2019
////					lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
////					}
//
//				 
//				 
//				lObjTrnDcpsContribution.setStartDate(IFMSCommonServiceImpl
//						.getDateFromString(lStrtxtStartDate));
//				lObjTrnDcpsContribution.setEndDate(IFMSCommonServiceImpl
//						.getDateFromString(lStrtxtEndDate));
//				lObjTrnDcpsContribution.setEmployerContriFlag('N');
//				lObjTrnDcpsContribution.setPostEmplrContriStatus(0l);
//				lObjTrnDcpsContribution.setLangId(lLngLangId);
//				lObjTrnDcpsContribution.setLocId(lLngLocId);
//				lObjTrnDcpsContribution.setDbId(lLngDbId);
//				lObjTrnDcpsContribution.setCreatedPostId(gLngPostId);
//				lObjTrnDcpsContribution.setCreatedUserId(gLngUserId);
//				lObjTrnDcpsContribution.setCreatedDate(gDtCurrDt);
//				
//				if(lStrUserType.equals("TO") && lStrUseType.equals("MissingCreditEntry"))
//				{
//					lObjTrnDcpsContribution.setIsMissingCredit('Y');
//					lObjTrnDcpsContribution.setStatus('F');
//					lObjTrnDcpsContribution.setRegStatus(1l);
//				}
//				else
//				{
//					lObjTrnDcpsContribution.setRegStatus(0l);
//				}
//
//				lArrTrnDcpsContribution[lInt - 1] = lObjTrnDcpsContribution;
//			}
//		
//
//		}
//
//		return lArrTrnDcpsContribution;
//	}
//	
//
//	public ResultObject generateContriVOListSchdlr(Map inputMap) throws Exception
//	{
//		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
//
//		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
//		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
//		
//		
//		Map loginMap =(HashMap) inputMap.get("baseLoginMap");
//		Long gLngPostId = Long.valueOf((loginMap.get("postId").toString()));
//		Long gLngUserId = Long.valueOf((loginMap.get("userId").toString()));
//		Long lLngDbId = 99L;
//		Long lLngLocId = Long.valueOf((loginMap.get("locationCode").toString()));
//		Long lLngLangId = 1L;
//		Date gDtCurrDt = new Date();
//
//		String lStrUserType = inputMap.get("User") != null ? inputMap.get("User").toString():"";
//		String lStrUseType = inputMap.get("Use") != null ? inputMap.get("Use").toString():"";
//
//		String lStrTotalRecords = inputMap.get("hdnCounter").toString().trim();
//		Integer lIntTotalRecords = Integer.parseInt(lStrTotalRecords);
//		System.out.println("Total Records"+lIntTotalRecords);
//
//		TrnDcpsContribution[] lArrTrnDcpsContribution = new TrnDcpsContribution[lIntTotalRecords];
//
//		Long lLongContributionId = null;
//		String lStrDcpsEmpId = null;
//		String lStrTreasuryCode = null;
//		String schemeCode = null;
//		String lStrDDOCode = null;
//		String lStrMonthId = null;
//		String lStrYearId = null;
//		String lStrDelayedMonthId = null;
//		String lStrDelayedYearId = null;
//		String lStrBillGroupId = null;
//		String lStrTypeOfPayment = null;
//		String lStrPayCommission = null;
//		String lStrtxtStartDate = null;
//		String lStrtxtEndDate = null;
//		String lStrBasic = null;
//		String lStrDP = null;
//		String lStrDA = null;
//		String lStrContribution = null;
//		//$t 2019
//	    String lStrContributionEmplr=null;
//		StringBuffer dcpsContributionIds = new StringBuffer();
//		//String lStrDeletedContributionIndexes = StringUtility.getParameter("deletedContributionIndexes", request).trim();
//		String[] lStrArrDeletedContributionIndexes = null;
//
//		/*if (!"".equals(lStrDeletedContributionIndexes))
//		{
//			lStrArrDeletedContributionIndexes = lStrDeletedContributionIndexes.split("~");
//		}*/
//
//		/*String lStrElementId = StringUtility.getParameter("hidElementId", request).trim();
//		inputMap.put("elementId", lStrElementId);
//
//		String lStrType = StringUtility.getParameter("Type", request).trim();
//		inputMap.put("RLType", lStrType);
//
//		lStrDelayedMonthId = StringUtility.getParameter("cmbDelayedMonth", request).trim();
//		lStrDelayedYearId = StringUtility.getParameter("cmbDelayedYear", request).trim();
//
//		String lStrSchemeName = StringUtility.getParameter("txtSchemeName", request).trim();
//		inputMap.put("schemeName", lStrSchemeName);
//
//		lStrTreasuryCode = StringUtility.getParameter("cmbTreasuryCode", request).trim();
//		inputMap.put("TreasuryCode", lStrTreasuryCode);
//
//		lStrBillGroupId = StringUtility.getParameter("cmbBillGroup", request).trim();
//		inputMap.put("cmbBillGroup", lStrBillGroupId);
//
//		schemeCode = StringUtility.getParameter("schemeCode", request).trim();
//		inputMap.put("schemeCode", schemeCode);
//		lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request).trim();
//		inputMap.put("cmbDDOCode", lStrDDOCode);
//
//		lStrMonthId = StringUtility.getParameter("cmbMonth", request).trim();
//		inputMap.put("cmbMonth", lStrMonthId);
//		lStrYearId = StringUtility.getParameter("cmbYear", request).trim();
//		inputMap.put("cmbYear", lStrYearId);
//*/
//		inputMap.get("Type").toString();
//		String lStrType = inputMap.get("Type") != null ? inputMap.get("Type").toString():"";
//		inputMap.put("RLType", lStrType);
//
////		lStrTreasuryCode = StringUtility.getParameter("cmbTreasuryCode", request).trim(); // add in map
//		
//		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, servLoc.getSessionFactory());
//		
//		lStrDDOCode = inputMap.get("cmbDDOCode") != null ? inputMap.get("cmbDDOCode").toString():"";
//		inputMap.put("cmbDDOCode", lStrDDOCode);
//		
//		lStrTreasuryCode = lObjDcpsCommonDAO.getTreasuryCodeForDDO(lStrDDOCode);
//		inputMap.put("TreasuryCode", lStrTreasuryCode);
//
//		lStrBillGroupId = inputMap.get("cmbBillGroup") != null ? inputMap.get("cmbBillGroup").toString():"";
//		inputMap.put("cmbBillGroup", lStrBillGroupId);
//
//		schemeCode = inputMap.get("schemeCodeForBG") != null ? inputMap.get("schemeCodeForBG").toString():"";
//		inputMap.put("schemeCode", schemeCode);
//
//		lStrMonthId = inputMap.get("cmbMonth") != null ? inputMap.get("cmbMonth").toString():"";
//		inputMap.put("cmbMonth", lStrMonthId);
//		
//		lStrYearId = inputMap.get("cmbYear") != null ? inputMap.get("cmbYear").toString():"";
//		inputMap.put("cmbYear", lStrYearId);
//		
//		List empList = (List) (inputMap.get("empList") != null ? inputMap.get("empList"):null);
//		
//		//System.out.println("Size of EmpList is"+empList.size());
//		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		
//		Integer lIntContinueFlag = 0;
//
//		TrnDcpsContribution lObjTrnDcpsContribution = null;
//
//		OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(TrnDcpsContribution.class, servLoc.getSessionFactory());
//		
//
//		for (Integer lInt = 1; lInt <= lIntTotalRecords; lInt++)
//		{
//
//			// Code added not to consider the deleted contributions.
//
//			lIntContinueFlag = 0;
//
//			if (lStrArrDeletedContributionIndexes != null)
//			{
//				for (Integer lIntDelete = 0; lIntDelete < lStrArrDeletedContributionIndexes.length; lIntDelete++)
//				{
//					if (Integer.parseInt(lStrArrDeletedContributionIndexes[lIntDelete]) == lInt)
//					{
//						lIntContinueFlag = 1;
//					}
//				}
//
//				if (lIntContinueFlag == 1)
//				{
//					continue;
//				}
//			}
//
//			//lLongContributionId = Long.parseLong(StringUtility.getParameter("checkbox" + lInt, request).trim());
//			lLongContributionId = 0l;
//
//			if (lLongContributionId == 0l)
//			{
//				lObjTrnDcpsContribution = new TrnDcpsContribution();
//			}
//			else
//			{
//				lObjTrnDcpsContribution = (TrnDcpsContribution) lObjOfflineContriDAO.read(lLongContributionId);
//			}
//
//			//dcpsContributionIds.append(StringUtility.getParameter("checkbox" + lInt, request).trim());
//			dcpsContributionIds.append("0");
//			dcpsContributionIds.append("~");
//			inputMap.put("dcpsContributionIds", dcpsContributionIds);
//
//			//lStrDcpsEmpId = StringUtility.getParameter("dcpsempId" + lInt, request).trim();
//			Object[] lArrObjectEmpList = (Object[]) empList.get(lInt-1);
//			lStrDcpsEmpId =  lArrObjectEmpList[0].toString();
//
//			//lStrTypeOfPayment = StringUtility.getParameter("cmbTypeOfPayment" + lInt, request).trim();
//			lStrTypeOfPayment = "700046";
//
//			//lStrPayCommission = StringUtility.getParameter("cmbPayCommission" + lInt, request).trim();
//			lStrPayCommission = lArrObjectEmpList[3].toString();
//
//			/*if (lStrUserType.equals("DDOAsst") && lStrUseType.equals("ViewAll"))
//			{
//				if (!"".equals(lStrDelayedMonthId) && !"-1".equals(lStrDelayedMonthId))
//				{
//					lObjTrnDcpsContribution.setDelayedMonthId(Long.valueOf(lStrDelayedMonthId));
//				}
//				if (!"".equals(lStrDelayedYearId) && !"-1".equals(lStrDelayedYearId))
//				{
//					lObjTrnDcpsContribution.setDelayedFinYearId(Long.valueOf(lStrDelayedYearId));
//				}
//			}*/
//
//			lStrtxtStartDate = inputMap.get("schdlStartDate") != null ? inputMap.get("schdlStartDate").toString():null;
//			lStrtxtEndDate =inputMap.get("schdlEndDate") != null ? inputMap.get("schdlEndDate").toString():null; 
//				
//			lStrBasic = lArrObjectEmpList[4].toString();
//			lStrDP = lArrObjectEmpList[17].toString();
//			lStrDA = lArrObjectEmpList[18].toString();
//			Double lDoubleDP = null;
//			if (lStrDP.equals(""))
//			{
//				lDoubleDP = 0d;
//			}
//			else
//			{
//				lDoubleDP = Double.parseDouble(lStrDP);
//			}
//
//			Double lDoubleDA = null;
//			if (lStrDA.equals(""))
//			{
//				lDoubleDA = 0d;
//			}
//			else
//			{
//				lDoubleDA = Double.parseDouble(lStrDA);
//			}
//
//			lStrContribution = lArrObjectEmpList[19].toString();
//			//$t 2019
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//			lStrContributionEmplr = lArrObjectEmpList[22].toString();
//			
//			Double lDoubleContribution = null;
//			if (lStrContribution.equals(""))
//			{
//				lDoubleContribution = 0d;
//			}
//			else
//			{
//				lDoubleContribution = Double.parseDouble(lStrContribution);
//			}
//			//$t 2019
//			Double lDoubleContributionEmplr = null;
//			if (lStrContributionEmplr.equals(""))
//			{
//				lDoubleContributionEmplr = 0d;
//			}
//			else
//			{
//				lDoubleContributionEmplr = Double.parseDouble(lStrContributionEmplr);
//			}
//
//			lObjTrnDcpsContribution.setDcpsEmpId(Long.parseLong(lStrDcpsEmpId));
//			lObjTrnDcpsContribution.setTreasuryCode(Long.parseLong(lStrTreasuryCode));
//			lObjTrnDcpsContribution.setDdoCode(lStrDDOCode);
//			lObjTrnDcpsContribution.setDcpsDdoBillGroupId(Long.parseLong(lStrBillGroupId));
//			lObjTrnDcpsContribution.setSchemeCode(schemeCode);
//			lObjTrnDcpsContribution.setTypeOfPayment(lStrTypeOfPayment);
//			lObjTrnDcpsContribution.setPayCommission(lStrPayCommission);
//			lObjTrnDcpsContribution.setFinYearId(Long.parseLong(lStrYearId));
//			lObjTrnDcpsContribution.setMonthId(Long.parseLong(lStrMonthId));
//			// /
//
//			if (lStrTypeOfPayment.equals("700046") || lStrTypeOfPayment.equals("700047"))
//			{
//				lObjTrnDcpsContribution.setBasicPay(Double.parseDouble(lStrBasic));
//				lObjTrnDcpsContribution.setDP(lDoubleDP);
//				lObjTrnDcpsContribution.setDA(lDoubleDA);
//			}
//			if (lStrTypeOfPayment.equals("700048"))
//			{
//				lObjTrnDcpsContribution.setBasicPay(0d);
//				lObjTrnDcpsContribution.setDP(lDoubleDP);
//				lObjTrnDcpsContribution.setDA(lDoubleDA);
//			}
//			if (lStrTypeOfPayment.equals("700049"))
//			{
//				lObjTrnDcpsContribution.setBasicPay(0d);
//				lObjTrnDcpsContribution.setDP(lDoubleDP);
//				lObjTrnDcpsContribution.setDA(lDoubleDA);
//			}
//			
//			
//			lObjTrnDcpsContribution.setContribution(lDoubleContribution);
//			//$t 2019
//			lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
//			
//			
////			//$t 2019
//			//$t 2019
//			//else code on line 700???? 
//			//& startdate=31/03/2019 & end date=01/04/2019 ????
////			Double lDoubleContributionEmplr = null;
////			DecimalFormat deci=new DecimalFormat("##.##");
////					  //Double.parseDouble(deci.format(value));
////					  //Double a=Math.ceil(value);
////			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
////			String aprSDate="01/04/2019";
////			String marEDate="31/03/2019";
////			Date dateBefore = sdf.parse(lStrtxtStartDate);
////		    Date dateAfter = sdf.parse(lStrtxtEndDate);
////		    long difference = dateAfter.getTime() - dateBefore.getTime();
////		    float daysBetween = ((difference / (1000*60*60*24))+1);
////		    System.out.println("Number of Days between dates-->: "+daysBetween);
////		    double oneDayBasic =Double.parseDouble(deci.format((Double.parseDouble(lStrBasic))/daysBetween));
////		    System.out.println("oneDayBasic-->: "+oneDayBasic);
////		    double oneDayDA =Double.parseDouble(deci.format((Double.parseDouble(lStrDA))/daysBetween));
////		    System.out.println("oneDayDA-->: "+oneDayDA);
////		    double oneDayDP =Double.parseDouble(deci.format((Double.parseDouble(lStrDP))/daysBetween));
////		    System.out.println("oneDayDP-->: "+oneDayDP);
////		    
////		    if((lStrTypeOfPayment.equals("700047") || lStrTypeOfPayment.equals("700048")|| lStrTypeOfPayment.equals("700049")) 
////            		&& (sdf.parse(lStrtxtStartDate).before(sdf.parse(marEDate)))&& (sdf.parse(lStrtxtEndDate).after(sdf.parse(aprSDate)))){
////		    	System.out.println("First-->");
////		    	Date dateBefore1 = sdf.parse(lStrtxtStartDate);
////			    Date dateAfter1 = sdf.parse(marEDate);
////			    long difference1 = dateAfter1.getTime() - dateBefore1.getTime();
////			    float daysBetween1 = ((difference1 / (1000*60*60*24))+1);
////			    System.out.println("before march between dates-->: "+daysBetween1);
////			    double basicMarch =Double.parseDouble(deci.format((oneDayBasic * daysBetween1)));
////			    System.out.println("basicBfmarch-->: "+basicMarch);
////			    double DAMarch =Double.parseDouble(deci.format((oneDayDA * daysBetween1)));
////			    System.out.println("basicBfmarch-->: "+DAMarch);
////			    double DPMarch =Double.parseDouble(deci.format((oneDayDP * daysBetween1)));
////			    System.out.println("basicBfmarch-->: "+DPMarch);
////			    double c1=Double.parseDouble(deci.format(((basicMarch+DAMarch+DPMarch)*0.10)));
////			    Date dateBefore2 = sdf.parse(aprSDate);
////			    Date dateAfter2 = sdf.parse(lStrtxtEndDate);
////			    long difference2 = dateAfter2.getTime() - dateBefore2.getTime();
////			    float daysBetween2 = ((difference2 / (1000*60*60*24))+1);
////			    System.out.println("Number of Days between dates-->: "+daysBetween2);
////			    double basicApr =Double.parseDouble(deci.format((oneDayBasic * daysBetween2)));
////			    System.out.println("basicAfApr-->: "+basicApr);
////			    double DAApr =Double.parseDouble(deci.format((oneDayDA * daysBetween2)));
////			    System.out.println("DAAfApr-->: "+DAApr);
////			    double DPApr =Double.parseDouble(deci.format((oneDayDP * daysBetween2)));
////			    System.out.println("DPAfApr-->: "+DPApr);
////			    double c2=Double.parseDouble(deci.format(((basicApr+DAApr+DPApr)*0.14)));
////			    lDoubleContributionEmplr=Math.ceil(c1+c2);
////			    System.out.println("lDoubleContributionEmplr-->: "+lDoubleContributionEmplr);
////			    lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////				//$t 2019
////				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
////		    }else if((lStrTypeOfPayment.equals("700047") || lStrTypeOfPayment.equals("700048")|| lStrTypeOfPayment.equals("700049")) &&  (sdf.parse(lStrtxtStartDate).after(sdf.parse(marEDate)))){
////				System.out.println("Second-->");
////				System.out.println("19 basic^^^"+Double.parseDouble(lStrBasic));
////				System.out.println("19 DA^^^"+Double.parseDouble(lStrDA));
////				System.out.println("19 DP^^^"+Double.parseDouble(lStrDP));
////				lDoubleContributionEmplr=Double.parseDouble(deci.format((Double.parseDouble(lStrBasic)+Double.parseDouble(lStrDA)+Double.parseDouble(lStrDP))*0.14));
////				System.out.println("19 bf^^^"+lDoubleContributionEmplr);
////				lDoubleContributionEmplr = (double) Math.ceil(lDoubleContributionEmplr);
////				System.out.println("19 af^^^"+lDoubleContributionEmplr);
////				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////				//$t 2019
////				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
////			}else{
////				System.out.println("Third-->");
////				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////				//$t 2019
////				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
////				}
//
////			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
////			String aprDate="31/03/2019";
////            sdf.parse(aprDate);		
////			System.out.println(sdf1.parse(lStrtxtStartDate));
////			//System.out.println(sdf1.parse(lStrtxtEndDate));
////			//System.out.println("hehrheh-->"+(sdf1.parse(lStrtxtStartDate).before(sdf1.parse(lStrtxtEndDate))));
////			
////			Double lDoubleContributionEmplr = null;
////			if ((lStrTypeOfPayment.equals("700047") || lStrTypeOfPayment.equals("700048")|| lStrTypeOfPayment.equals("700049")) &&  (sdf1.parse(lStrtxtStartDate).after(sdf1.parse(aprDate)))){
////				System.out.println("19 basic^^^"+Double.parseDouble(lStrBasic));
////				System.out.println("19 DA^^^"+Double.parseDouble(lStrDA));
////				System.out.println("19 DP^^^"+Double.parseDouble(lStrDP));
////				lDoubleContributionEmplr=(Math.round((Double.parseDouble(lStrBasic)+Double.parseDouble(lStrDA)+Double.parseDouble(lStrDP))*0.14)*100.0)/100.0;
////				//lDoubleContributionEmplr=Math.round(((100000d+9000d)*0.14)* 100.0) / 100.0;
////				System.out.println("19 bf^^^"+lDoubleContributionEmplr);
////				lDoubleContributionEmplr = (double) Math.ceil(lDoubleContributionEmplr);
////				System.out.println("19 af^^^"+lDoubleContributionEmplr);
////				System.out.println("19 EmplrContribution^^^"+lDoubleContributionEmplr);
////				lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////				//$t 2019
////				lObjTrnDcpsContribution.setContributionEmplr(lDoubleContributionEmplr);
////			}else{
////			lObjTrnDcpsContribution.setContribution(lDoubleContribution);
////			//$t 2019
////			lObjTrnDcpsContribution.setContributionEmplr(lDoubleContribution);
////			}
//			
//			
//			lObjTrnDcpsContribution.setStartDate(sdf.parse(lStrtxtStartDate));
//			lObjTrnDcpsContribution.setEndDate(sdf.parse(lStrtxtEndDate));
//			lObjTrnDcpsContribution.setEmployerContriFlag('N');
//			lObjTrnDcpsContribution.setPostEmplrContriStatus(0l);
//			lObjTrnDcpsContribution.setLangId(lLngLangId);
//			lObjTrnDcpsContribution.setLocId(lLngLocId);
//			lObjTrnDcpsContribution.setDbId(lLngDbId);
//			lObjTrnDcpsContribution.setCreatedPostId(gLngPostId);
//			lObjTrnDcpsContribution.setCreatedUserId(gLngUserId);
//			lObjTrnDcpsContribution.setCreatedDate(gDtCurrDt);
//
//			lArrTrnDcpsContribution[lInt - 1] = lObjTrnDcpsContribution;
//
//		}
//
//		
//		inputMap.put("lArrTrnDcpsContributions", lArrTrnDcpsContribution);
//		objRes.setResultValue(inputMap);
//		return objRes;
//		//return 
//	}
//	
//}
