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

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.ArrearsDAO;
import com.tcs.sgv.dcps.dao.ArrearsDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstSixPCArrears;
import com.tcs.sgv.dcps.valueobject.RltDcpsSixPCYearly;

public class sixPCYearlyEntryVOGenerator extends ServiceImpl implements
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

		RltDcpsSixPCYearly[] lArrRltDcpsSixPCYearly = null;

		try {
			lArrRltDcpsSixPCYearly = generateSixPCYearlyData(inputMap);
			inputMap.put("lArrRltDcpsSixPCYearly", lArrRltDcpsSixPCYearly);
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

	public RltDcpsSixPCYearly[] generateSixPCYearlyData(Map inputMap)
			throws Exception {
		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(MstSixPCArrears.class,
				servLoc.getSessionFactory());

		ArrearsDAO ArrearsDAOObjRlt = new ArrearsDAOImpl(
				RltDcpsSixPCYearly.class, servLoc.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		Long lLongfinYearId = Long.valueOf(StringUtility.getParameter(
				"finYearId", request));
		String lStrdcpsEmpIds = StringUtility.getParameter("dcpsEmpIds",
				request);
		// String lStrdcpsIds = StringUtility.getParameter("dcpsIds", request);
		String lStramounts = StringUtility.getParameter("amounts", request);
		String lStrdcpsSixPCYearlyIds = StringUtility.getParameter(
				"dcpsSixPCYearlyIds", request);

		String[] lStrArrdcpsEmpIds = lStrdcpsEmpIds.split("~");
		// String[] lStrArrdcpsIds = lStrdcpsIds.split("~");

		String[] lStrArrdcpsSixPCYearlyIds = lStrdcpsSixPCYearlyIds.split("~");
		Long[] lLongArrdcpsSixPCYearlyIds = new Long[lStrArrdcpsSixPCYearlyIds.length];
		for (Integer lInt = 0; lInt < lStrArrdcpsSixPCYearlyIds.length; lInt++) {
			lLongArrdcpsSixPCYearlyIds[lInt] = Long
					.valueOf(lStrArrdcpsSixPCYearlyIds[lInt]);
		}

		Long[] lLongArrdcpsSixPCIds = new Long[lStrArrdcpsEmpIds.length];
		Long[] lLongArrdcpsEmpIds = new Long[lStrArrdcpsEmpIds.length];
		for (Integer lInt = 0; lInt < lStrArrdcpsEmpIds.length; lInt++) {
			lLongArrdcpsEmpIds[lInt] = Long.valueOf(lStrArrdcpsEmpIds[lInt]);
			lLongArrdcpsSixPCIds[lInt] = ArrearsDAOObj
					.getSixPCIDforEmpId(lLongArrdcpsEmpIds[lInt]);
		}
		// Long[] lLongArrdcpsIds = new Long[lStrArrdcpsIds.length];

		String[] lStrArramounts = lStramounts.split("~");
		Long[] lLongArramounts = new Long[lStrArramounts.length];
		for (Integer lInt = 0; lInt < lStrArramounts.length; lInt++) {
			lLongArramounts[lInt] = Long.valueOf(lStrArramounts[lInt]);
		}

		RltDcpsSixPCYearly[] lArrRltDcpsSixPCYearly = new RltDcpsSixPCYearly[lStrArrdcpsEmpIds.length];
		RltDcpsSixPCYearly lObjRltDcpsSixPCYearly = null;
		for (Integer lInt = 0; lInt < lLongArrdcpsEmpIds.length; lInt++) {

			MstSixPCArrears MstSixPCArrearsObj = (MstSixPCArrears) ArrearsDAOObj
					.read(lLongArrdcpsSixPCIds[lInt]);

			Long lLongAmountPaid = lLongArramounts[lInt]
					+ MstSixPCArrearsObj.getAmountPaid();

			if (lLongArrdcpsSixPCYearlyIds[lInt] != 0l) {
				lObjRltDcpsSixPCYearly = new RltDcpsSixPCYearly();
				lObjRltDcpsSixPCYearly = (RltDcpsSixPCYearly) ArrearsDAOObjRlt
						.read(lLongArrdcpsSixPCYearlyIds[lInt]);
				lLongAmountPaid = lLongAmountPaid
						- lObjRltDcpsSixPCYearly.getYearlyAmount();
				lObjRltDcpsSixPCYearly.setYearlyAmount(lLongArramounts[lInt]);
			}

			else {

				lObjRltDcpsSixPCYearly = new RltDcpsSixPCYearly();

				lObjRltDcpsSixPCYearly.setDcpsEmpId(lLongArrdcpsEmpIds[lInt]);
				lObjRltDcpsSixPCYearly.setYearlyAmount(lLongArramounts[lInt]);
				lObjRltDcpsSixPCYearly.setFinYearId(lLongfinYearId);
				lObjRltDcpsSixPCYearly.setStatusFlag('D');
				lObjRltDcpsSixPCYearly.setDbId(lLngDbId);
				lObjRltDcpsSixPCYearly.setLangId(lLngLangId);
				lObjRltDcpsSixPCYearly.setLocId(lLngLocId);
				lObjRltDcpsSixPCYearly.setCreatedPostId(gLngPostId);
				lObjRltDcpsSixPCYearly.setCreatedUserId(gLngUserId);
				lObjRltDcpsSixPCYearly.setCreatedDate(gDtCurrDt);

			}

			MstSixPCArrearsObj.setAmountPaid(lLongAmountPaid);

			lArrRltDcpsSixPCYearly[lInt] = lObjRltDcpsSixPCYearly;
		}
		return lArrRltDcpsSixPCYearly;
	}
}
