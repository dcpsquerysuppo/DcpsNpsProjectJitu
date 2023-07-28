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

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.BrokenPeriodDAO;
import com.tcs.sgv.dcps.dao.BrokenPeriodDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstBrokenPeriodPay;
import com.tcs.sgv.dcps.valueobject.MstSixPCArrears;
import com.tcs.sgv.dcps.valueobject.RltBrokenPeriodAllow;
import com.tcs.sgv.dcps.valueobject.RltBrokenPeriodDeduc;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class BrokenPeriodPayVOGenerator extends ServiceImpl implements
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
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		
		MstBrokenPeriodPay[] lArrMstBrokenPeriodPay = null;
		List<RltBrokenPeriodAllow> lListRltBrokenPeriodAllow = new ArrayList<RltBrokenPeriodAllow>();
		List<RltBrokenPeriodDeduc> lListRltBrokenPeriodDeduc = new ArrayList<RltBrokenPeriodDeduc>();
		MstBrokenPeriodPay lObjMstBrokenPeriodPay = null;
		RltBrokenPeriodAllow lObjRltBrokenPeriodAllow = null;
		RltBrokenPeriodDeduc lObjRltBrokenPeriodDeduc = null;
		Long lLongMstBrokenPrdId = null;
		Long lLongRltBrokenPrdAllowId = null;
		Long lLongRltBrokenPrdDeducId = null;
		String[] lStrArrAllowancesCodes = null;
		String[] lStrArrAllowancesValues = null;
		Long[] lLongArrAllowancesCodesFinal = null;
		Long[] lLongArrAllowancesValuesFinal = null;
		String[] lStrArrAllowancesCodesFinal = null;
		String[] lStrArrAllowancesValuesFinal = null;
		String[] lStrArrDeductionsCodes = null;
		String[] lStrArrDeductionsValues = null;
		Long[] lLongArrDeductionsCodesFinal = null;
		Long[] lLongArrDeductionsValuesFinal = null;
		String[] lStrArrDeductionsCodesFinal = null;
		String[] lStrArrDeductionsValuesFinal = null;

		try {
			
			lArrMstBrokenPeriodPay = generateBrokenPeriodPayVOList(inputMap);
			
			String lStrAllowancesCodes = StringUtility.getParameter("allowancesCodes", request);
			String lStrAllowancesValues = StringUtility.getParameter("allowancesValues", request);
			lStrArrAllowancesCodes = lStrAllowancesCodes.split("~");
			lStrArrAllowancesValues = lStrAllowancesValues.split("~");
			
			String lStrDeductionCodes = StringUtility.getParameter("deductionCodes", request);
			String lStrDeductionValues = StringUtility.getParameter("deductionValues", request);
			lStrArrDeductionsCodes = lStrDeductionCodes.split("~");
			lStrArrDeductionsValues = lStrDeductionValues.split("~");
			
			
			for(Integer lInt=0;lInt<lArrMstBrokenPeriodPay.length;lInt++)
			{
				lObjMstBrokenPeriodPay = lArrMstBrokenPeriodPay[lInt];
				lLongMstBrokenPrdId =  IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_broken_period_pay",inputMap);
				lObjMstBrokenPeriodPay.setBrokenPeriodId(lLongMstBrokenPrdId);
				
				// Generates VO List for Allowances
				
				lStrArrAllowancesCodesFinal = lStrArrAllowancesCodes[lInt].split(":");
				lStrArrAllowancesValuesFinal = lStrArrAllowancesValues[lInt].split(":");
				
				lLongArrAllowancesCodesFinal = new Long[lStrArrAllowancesCodesFinal.length] ;
				lLongArrAllowancesValuesFinal = new Long[lStrArrAllowancesCodesFinal.length] ;
				
				for(Integer lIntInner = 0 ;lIntInner<lStrArrAllowancesCodesFinal.length;lIntInner++)
				{
					lLongArrAllowancesCodesFinal[lIntInner] = Long.valueOf(lStrArrAllowancesCodesFinal[lIntInner].trim());
				}
				
				for(Integer lIntInner = 0 ;lIntInner<lStrArrAllowancesValuesFinal.length;lIntInner++)
				{
					lLongArrAllowancesValuesFinal[lIntInner] = Long.valueOf(lStrArrAllowancesValuesFinal[lIntInner].trim());
				}
				
				for(Integer lIntInner = 0 ;lIntInner<lStrArrAllowancesCodesFinal.length;lIntInner++)
				{
					lObjRltBrokenPeriodAllow = new RltBrokenPeriodAllow();
					lLongRltBrokenPrdAllowId =  IFMSCommonServiceImpl.getNextSeqNum("rlt_dcps_broken_period_allow",inputMap);
					lObjRltBrokenPeriodAllow.setBrokenPeriodAllowId(lLongRltBrokenPrdAllowId);
					lObjRltBrokenPeriodAllow.setRltBrokenPeriodId(lObjMstBrokenPeriodPay);
					lObjRltBrokenPeriodAllow.setAllowCode(lLongArrAllowancesCodesFinal[lIntInner]);
					lObjRltBrokenPeriodAllow.setAllowValue(lLongArrAllowancesValuesFinal[lIntInner]);
					lObjRltBrokenPeriodAllow.setLangId(lLngLangId);
					lObjRltBrokenPeriodAllow.setLocId(lLngLocId);
					lObjRltBrokenPeriodAllow.setDbId(lLngDbId);
					lObjRltBrokenPeriodAllow.setCreatedPostId(gLngPostId);
					lObjRltBrokenPeriodAllow.setCreatedUserId(gLngUserId);
					lObjRltBrokenPeriodAllow.setCreatedDate(gDtCurrDt);
					lListRltBrokenPeriodAllow.add(lObjRltBrokenPeriodAllow);
				}
				
				// Generates VO List for Deductions
				
				lStrArrDeductionsCodesFinal = lStrArrDeductionsCodes[lInt].split(":");
				lStrArrDeductionsValuesFinal = lStrArrDeductionsValues[lInt].split(":");
				
				lLongArrDeductionsCodesFinal = new Long[lStrArrDeductionsCodesFinal.length];
				lLongArrDeductionsValuesFinal = new Long[lStrArrDeductionsCodesFinal.length];
				
				for(Integer lIntInner = 0 ;lIntInner<lStrArrDeductionsCodesFinal.length;lIntInner++)
				{
					lLongArrDeductionsCodesFinal[lIntInner] = Long.valueOf(lStrArrDeductionsCodesFinal[lIntInner].trim());
				}
				
				for(Integer lIntInner = 0 ;lIntInner<lStrArrDeductionsValuesFinal.length;lIntInner++)
				{
					lLongArrDeductionsValuesFinal[lIntInner] = Long.valueOf(lStrArrDeductionsValuesFinal[lIntInner].trim());
				}
				
				for(Integer lIntInner = 0 ;lIntInner<lStrArrDeductionsCodesFinal.length;lIntInner++)
				{
					lObjRltBrokenPeriodDeduc = new RltBrokenPeriodDeduc();
					lLongRltBrokenPrdDeducId =  IFMSCommonServiceImpl.getNextSeqNum("rlt_dcps_broken_period_deduc",inputMap);
					lObjRltBrokenPeriodDeduc.setBrokenPeriodDeducId(lLongRltBrokenPrdDeducId);
					lObjRltBrokenPeriodDeduc.setRltBrokenPeriodId(lObjMstBrokenPeriodPay);
					lObjRltBrokenPeriodDeduc.setDeducCode(lLongArrDeductionsCodesFinal[lIntInner]);
					lObjRltBrokenPeriodDeduc.setDeducValue(lLongArrDeductionsValuesFinal[lIntInner]);
					lObjRltBrokenPeriodDeduc.setLangId(lLngLangId);
					lObjRltBrokenPeriodDeduc.setLocId(lLngLocId);
					lObjRltBrokenPeriodDeduc.setDbId(lLngDbId);
					lObjRltBrokenPeriodDeduc.setCreatedPostId(gLngPostId);
					lObjRltBrokenPeriodDeduc.setCreatedUserId(gLngUserId);
					lObjRltBrokenPeriodDeduc.setCreatedDate(gDtCurrDt);
					
					lListRltBrokenPeriodDeduc.add(lObjRltBrokenPeriodDeduc);
				}
				
			}
			
			inputMap.put("lArrMstBrokenPeriodPay", lArrMstBrokenPeriodPay);
			inputMap.put("lListBrokenPeriodAllows", lListRltBrokenPeriodAllow);
			inputMap.put("lListBrokenPeriodDeducs", lListRltBrokenPeriodDeduc);
			
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

	public MstBrokenPeriodPay[] generateBrokenPeriodPayVOList(Map inputMap)
			throws Exception {
		
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Long lLongYear = null;
		Long lLongMonth = null;
		Long lLongEisEmpId = null;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		lLongYear = Long.valueOf(StringUtility.getParameter("year", request).trim());
		lLongMonth = Long.valueOf(StringUtility.getParameter("month", request).trim());
		lLongEisEmpId = Long.valueOf(StringUtility.getParameter("eisEmpId", request).trim());
		
		String lStrFromDates = StringUtility.getParameter("fromDates", request);
		String[] lStrArrFromDate = lStrFromDates.split("~");
		Date[] lDateArrFromDate = new Date[lStrArrFromDate.length];
		for (Integer lInt = 0; lInt < lStrArrFromDate.length; lInt++) {
			if (lStrArrFromDate[lInt] != null
					&& !"".equals(lStrArrFromDate[lInt].trim())) {
				lDateArrFromDate[lInt] = sdf.parse(lStrArrFromDate[lInt].trim());
			}
		}
		
		String lStrToDates = StringUtility.getParameter("toDates", request);
		String[] lStrArrToDate = lStrToDates.split("~");
		Date[] lDateArrToDate = new Date[lStrArrToDate.length];
		for (Integer lInt = 0; lInt < lStrArrToDate.length; lInt++) {
			if (lStrArrToDate[lInt] != null
					&& !"".equals(lStrArrToDate[lInt].trim())) {
				lDateArrToDate[lInt] = sdf.parse(lStrArrToDate[lInt].trim());
			}
		}
		
		String lStrNoOfDays = StringUtility.getParameter("noOfDays", request);
		String[] lStrArrNoOfDays = lStrNoOfDays.split("~");
		Long[] lLongArrNoOfDays = new Long[lStrArrNoOfDays.length];
		for (Integer lInt = 0; lInt < lStrArrNoOfDays.length; lInt++) {
			if (lStrArrNoOfDays[lInt] != null
					&& !"".equals(lStrArrNoOfDays[lInt].trim())) {
				lLongArrNoOfDays[lInt] = Long.valueOf(lStrArrNoOfDays[lInt].trim());
			}
		}
		
		String lStrBasicPays = StringUtility.getParameter("basicPays", request);
		String[] lStrArrBasicPays = lStrBasicPays.split("~");
		Long[] lLongArrBasicPays = new Long[lStrArrBasicPays.length];
		for (Integer lInt = 0; lInt < lStrArrBasicPays.length; lInt++) {
			if (lStrArrBasicPays[lInt] != null
					&& !"".equals(lStrArrBasicPays[lInt].trim())) {
				lLongArrBasicPays[lInt] = Long.valueOf(lStrArrBasicPays[lInt].trim());
			}
		}
		
		String lStrNetPays = StringUtility.getParameter("netPays", request);
		String[] lStrArrNetPays = lStrNetPays.split("~");
		Long[] lLongArrNetPays = new Long[lStrArrNetPays.length];
		for (Integer lInt = 0; lInt < lStrArrNetPays.length; lInt++) {
			if (lStrArrNetPays[lInt] != null
					&& !"".equals(lStrArrNetPays[lInt].trim())) {
				lLongArrNetPays[lInt] = Long.valueOf(lStrArrNetPays[lInt].trim());
			}
		}
		
		String lStrReasons = StringUtility.getParameter("reasons", request);
		String[] lStrArrReasons = lStrReasons.split("~");
		
		String lStrRemarks = StringUtility.getParameter("remarks", request).trim();
		if(lStrRemarks.contains("~~"))
		{
			lStrRemarks = lStrRemarks.replace("~~", "~###~");        // When old value is null but new value is there.
		}
		
		String[] lStrArrRemarks = lStrRemarks.split("~");

		MstBrokenPeriodPay[] lArrMstBrokenPeriodPay = new MstBrokenPeriodPay[lStrArrFromDate.length];

		for (Integer lInt = 0; lInt < lStrArrFromDate.length; lInt++) {

			MstBrokenPeriodPay lObjMstBrokenPeriodPay = new MstBrokenPeriodPay();

			lObjMstBrokenPeriodPay.setEisEmpId(lLongEisEmpId);
			lObjMstBrokenPeriodPay.setMonthId(lLongMonth);
			lObjMstBrokenPeriodPay.setYearId(lLongYear);
			lObjMstBrokenPeriodPay.setFromDate(sdf.parse(lStrArrFromDate[lInt].trim()));
			lObjMstBrokenPeriodPay.setToDate(sdf.parse(lStrArrToDate[lInt].trim()));
			lObjMstBrokenPeriodPay.setNoOfDays(lLongArrNoOfDays[lInt]);
			lObjMstBrokenPeriodPay.setBasicPay(lLongArrBasicPays[lInt]);
			lObjMstBrokenPeriodPay.setNetPay(lLongArrNetPays[lInt]);
			lObjMstBrokenPeriodPay.setReason(lStrArrReasons[lInt].trim());
			if(lStrArrRemarks.length == 0)
			{
				lObjMstBrokenPeriodPay.setRemarks(null);
			}
			else if(!lStrArrRemarks[lInt].trim().equals("###"))
			{
				lObjMstBrokenPeriodPay.setRemarks(lStrArrRemarks[lInt].trim());
			}
			lObjMstBrokenPeriodPay.setLangId(lLngLangId);
			lObjMstBrokenPeriodPay.setLocId(lLngLocId);
			lObjMstBrokenPeriodPay.setDbId(lLngDbId);
			lObjMstBrokenPeriodPay.setCreatedPostId(gLngPostId);
			lObjMstBrokenPeriodPay.setCreatedUserId(gLngUserId);
			lObjMstBrokenPeriodPay.setCreatedDate(gDtCurrDt);
			String typeOfBill = StringUtility.getParameter("billType", request).trim();
			lObjMstBrokenPeriodPay.setTypeOfBill(typeOfBill);

			lArrMstBrokenPeriodPay[lInt] = lObjMstBrokenPeriodPay;
		}
		return lArrMstBrokenPeriodPay;
	}
}
