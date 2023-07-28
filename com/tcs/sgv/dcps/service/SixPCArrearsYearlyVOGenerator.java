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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.tcs.sgv.dcps.dao.ArrearsDAO;
import com.tcs.sgv.dcps.dao.ArrearsDAOImpl;
import com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO;
import com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstSixPCArrears;
import com.tcs.sgv.dcps.valueobject.RltDcpsSixPCYearly;

public class SixPCArrearsYearlyVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	/*
	 * @Description : Method to generate VO For DcpsEmpNmnMst.
	 * 
	 * @Input : Map : inputMap
	 * 
	 * @Output : ResultObject : ResultObject
	 */
	Log gLogger = LogFactory.getLog(getClass());

	public ResultObject generateMap(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		inputMap.get("requestObj");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		RltDcpsSixPCYearly[] lArrRltDcpsSixPCYearly = null;
		List<RltDcpsSixPCYearly> lListRltDcpsSixPCYearly = null;

		try {
			if (StringUtility.getParameter("hidDcpsEmpId", request) != null
					&& StringUtility.getParameter("hidDcpsEmpId", request)
							.length() > 0) {
				lListRltDcpsSixPCYearly = generateSixPCArrearsYearlyVOList(inputMap);
				inputMap
						.put("lListRltDcpsSixPCYearly", lListRltDcpsSixPCYearly);
			} else {
				lArrRltDcpsSixPCYearly = generateSixPCYearlyData(inputMap);
				inputMap.put("lArrRltDcpsSixPCYearly", lArrRltDcpsSixPCYearly);
			}
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
		Integer lIntNoOfInstallment = 5;

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
				// lLongAmountPaid = lLongAmountPaid -
				// lObjRltDcpsSixPCYearly.getYearlyAmount();
				// lObjRltDcpsSixPCYearly.setYearlyAmount(lLongArramounts[lInt]);
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
				lObjRltDcpsSixPCYearly.setActiveFlag(1);

			}

			MstSixPCArrearsObj.setAmountPaid(lLongAmountPaid);
			MstSixPCArrearsObj.setNoOfInstallment(--lIntNoOfInstallment);

			lArrRltDcpsSixPCYearly[lInt] = lObjRltDcpsSixPCYearly;
		}
		return lArrRltDcpsSixPCYearly;
	}

	public List<RltDcpsSixPCYearly> generateSixPCArrearsYearlyVOList(
			Map inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		List<RltDcpsSixPCYearly> lListRltDcpsSixPCYearly = new ArrayList<RltDcpsSixPCYearly>();
		RltDcpsSixPCYearly lObjRltDcpsSixPCYearly = null;
		SixPCArrearsYearlyDAO lObjSixPCArrearsYearlyDAO = null;
		String lStrDcpsEmpIds = null;
		String[] lStrArrdcpsEmpIds = null;
		String lStrTotalAmount = null;
		String[] lStrArrTotalAmount = null;
		Long lLngTotalAmount = null;
		String lStrDdoCode = null;
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Boolean lBlFlagPadmanabhan = false;
		
		Double lDoubleTempArrAmount = null;
		
		try {

			//	
			if (StringUtility.getParameter("hidDcpsEmpId", request) != null
					&& StringUtility.getParameter("hidDcpsEmpId", request)
							.length() > 0)

			{

				lStrDcpsEmpIds = StringUtility.getParameter("hidDcpsEmpId",
						request);
				lStrArrdcpsEmpIds = lStrDcpsEmpIds.split("~");
				if (StringUtility.getParameter("hidTotalAmount", request) != null
						&& StringUtility
								.getParameter("hidTotalAmount", request)
								.length() > 0) {
					lStrTotalAmount = StringUtility.getParameter(
							"hidTotalAmount", request);
					lStrArrTotalAmount = lStrTotalAmount.split("~");
				}

				lObjSixPCArrearsYearlyDAO = new SixPCArrearsYearlyDAOImpl(
						RltDcpsSixPCYearly.class, serv.getSessionFactory());
				lStrDdoCode = lObjSixPCArrearsYearlyDAO
						.getDdoCodeForDDO(gLngPostId);
				int lIntCount = lStrArrdcpsEmpIds.length;

				int cnt = 0;
				while (lIntCount != 0)

				{
					Long lLongfinYearId = 22L;
					lLngTotalAmount = Long.valueOf(lStrArrTotalAmount[cnt]
							.trim());
					
					lBlFlagPadmanabhan = false;
					lBlFlagPadmanabhan = lObjSixPCArrearsYearlyDAO.checkPayComPadmabhan(Long.valueOf(lStrArrdcpsEmpIds[cnt].trim()));
					
					if(lBlFlagPadmanabhan)
					{
						lLongfinYearId = 23L;
						for (Integer lInt = 0; lInt < 2; lInt++) {
							lObjRltDcpsSixPCYearly = new RltDcpsSixPCYearly();
							lObjRltDcpsSixPCYearly.setDcpsEmpId(Long.valueOf(lStrArrdcpsEmpIds[cnt].trim()));
							
							lDoubleTempArrAmount = Double.valueOf(lLngTotalAmount.toString());
							
							lObjRltDcpsSixPCYearly.setYearlyAmount(Math.round(lDoubleTempArrAmount / 2));
							
							lObjRltDcpsSixPCYearly.setFinYearId(lLongfinYearId);
							lObjRltDcpsSixPCYearly.setStatusFlag('D');
							lObjRltDcpsSixPCYearly.setDbId(lLngDbId);
							lObjRltDcpsSixPCYearly.setLangId(lLngLangId);
							lObjRltDcpsSixPCYearly.setLocId(lLngLocId);
							lObjRltDcpsSixPCYearly.setCreatedPostId(gLngPostId);
							lObjRltDcpsSixPCYearly.setCreatedUserId(gLngUserId);
							lObjRltDcpsSixPCYearly.setCreatedDate(gDtCurrDt);
							lObjRltDcpsSixPCYearly.setRemarks(null);
							lObjRltDcpsSixPCYearly.setActiveFlag(1);
							lObjRltDcpsSixPCYearly.setDdoCode(lStrDdoCode);
	
							lListRltDcpsSixPCYearly.add(lObjRltDcpsSixPCYearly);
							lLongfinYearId++;
						}
					}
					else
					{
							lLongfinYearId = 22L;
							for (Integer lInt = 0; lInt < 5; lInt++) {
								lObjRltDcpsSixPCYearly = new RltDcpsSixPCYearly();
								lObjRltDcpsSixPCYearly.setDcpsEmpId(Long
										.valueOf(lStrArrdcpsEmpIds[cnt].trim()));
								
								lDoubleTempArrAmount = Double.valueOf(lLngTotalAmount.toString());
								
								lObjRltDcpsSixPCYearly.setYearlyAmount(Math.round(lDoubleTempArrAmount / 5));
								
								lObjRltDcpsSixPCYearly.setFinYearId(lLongfinYearId);
								lObjRltDcpsSixPCYearly.setStatusFlag('D');
								lObjRltDcpsSixPCYearly.setDbId(lLngDbId);
								lObjRltDcpsSixPCYearly.setLangId(lLngLangId);
								lObjRltDcpsSixPCYearly.setLocId(lLngLocId);
								lObjRltDcpsSixPCYearly.setCreatedPostId(gLngPostId);
								lObjRltDcpsSixPCYearly.setCreatedUserId(gLngUserId);
								lObjRltDcpsSixPCYearly.setCreatedDate(gDtCurrDt);
								lObjRltDcpsSixPCYearly.setRemarks(null);
								lObjRltDcpsSixPCYearly.setActiveFlag(1);
								lObjRltDcpsSixPCYearly.setDdoCode(lStrDdoCode);
		
								lListRltDcpsSixPCYearly.add(lObjRltDcpsSixPCYearly);
								lLongfinYearId++;
							}
					}
					
					cnt++;
					lIntCount--;
				}

			}
		} catch (Exception e) {
			gLogger
					.error(
							"Error in generateSixPCArrearsYearlyVOList method is :"
									+ e, e);
			e.printStackTrace();
		}

		return lListRltDcpsSixPCYearly;

	}
}
