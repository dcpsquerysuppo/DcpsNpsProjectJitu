/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 24, 2011		Vihan Khatri								
 *******************************************************************************
 */
/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 24, 2011
 */
package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DCPSNewEntryVOGenerator
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.HstDcpsContribution;

public class ContributionsHistoryVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	@Override
	public ResultObject generateMap(Map inputMap) {
		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HstDcpsContribution lObjHstDcpsContribution = null;

		try {
			inputMap.put("lObjHstDcpsContribution", lObjHstDcpsContribution);
			lObjHstDcpsContribution = generateVOMap(inputMap);
			inputMap.put("lObjHstDcpsContribution", lObjHstDcpsContribution);

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

	private HstDcpsContribution generateVOMap(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		HstDcpsContribution lObjHstDcpsContribution = new HstDcpsContribution();

		// Get the audit purpose details from session helper
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		try {

			String lStrVoucherOrChallan = StringUtility.getParameter(
					"voucherOrChallan", request);
			String lStrtxtVCNo = StringUtility.getParameter("txtVCNo", request);
			String lStrtxtVCDate = StringUtility.getParameter("txtVCDate",
					request);
			String lStrtxtBillNo = StringUtility.getParameter("txtBillNo",
					request);
			String lStrdcpsEmpIds = StringUtility.getParameter("dcpsEmpIds",
					request);
			String[] lStrArrdcpsEmpIds = lStrdcpsEmpIds.split("~");

			Long[] lLongArrdcpsEmpIds = new Long[lStrArrdcpsEmpIds.length];
			for (Integer lInt = 0; lInt < lStrArrdcpsEmpIds.length; lInt++) {
				lLongArrdcpsEmpIds[lInt] = Long
						.valueOf(lStrArrdcpsEmpIds[lInt]);
			}

			lObjHstDcpsContribution.setDcpsEmpId(lLongArrdcpsEmpIds[0]);

			if (lStrVoucherOrChallan != null
					&& !(lStrVoucherOrChallan.equals(""))) {
				lObjHstDcpsContribution
						.setVoucherOrChallan(lStrVoucherOrChallan);
			}

			if (lStrtxtVCNo != null && !(lStrtxtVCNo.equals(""))) {
				lObjHstDcpsContribution.setVCNo(lStrtxtVCNo);
			}

			Date lDtVCDate = null;

			if (lStrtxtVCDate != null && !"".equals(lStrtxtVCDate.trim())) {
				lDtVCDate = sdf.parse(lStrtxtVCDate);
				lObjHstDcpsContribution.setVCDate(lDtVCDate);
			}

			if (lStrtxtBillNo != null && !(lStrtxtBillNo.equals(""))) {
				lObjHstDcpsContribution.setBillNo(lStrtxtBillNo);
			}

			lObjHstDcpsContribution.setDbId(lLngDbId);
			lObjHstDcpsContribution.setLangId(lLngLangId);
			lObjHstDcpsContribution.setLocId(lLngLocId);

			lObjHstDcpsContribution.setCreatedPostId(gLngPostId);
			lObjHstDcpsContribution.setCreatedUserId(gLngUserId);
			lObjHstDcpsContribution.setCreatedDate(gDtCurrDt);

			// END
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lObjHstDcpsContribution;
	}
}
