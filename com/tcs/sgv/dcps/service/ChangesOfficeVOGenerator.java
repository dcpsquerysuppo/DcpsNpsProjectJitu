/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Vihan Khatri								
 *******************************************************************************
 */

/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 25, 2011
 */
/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 25, 2011
 */
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
import com.tcs.sgv.dcps.valueobject.HstDcpsOfficeChanges;

public class ChangesOfficeVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	@Override
	public ResultObject generateMap(Map inputMap) {
		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HstDcpsOfficeChanges lObjHstDcpsOfficeChanges = null;

		try {
			lObjHstDcpsOfficeChanges = generateEmpData(inputMap);
			inputMap.put("lObjHstDcpsOfficeChanges", lObjHstDcpsOfficeChanges);
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

	private HstDcpsOfficeChanges generateEmpData(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		HstDcpsOfficeChanges lObjHstDcpsOfficeChanges = new HstDcpsOfficeChanges();

		// Get the audit purpose details from session helper
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		try {
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd/MM/yyyy");
		
			String lStrParentFldDept = StringUtility.getParameter(
					"listParentDept", request);
			String lStrCadre = StringUtility.getParameter("cmbCadre", request);
			String lStrGroup = StringUtility.getParameter("txtGroup", request);
			String lStrCurrentOffice = StringUtility.getParameter(
					"cmbCurrentOffice", request);
			String lStrDesignation = StringUtility.getParameter(
					"cmbDesignation", request);
			if (lStrDesignation != null && !(lStrDesignation.equals("-1"))) {
				lObjHstDcpsOfficeChanges.setDesignation(lStrDesignation);
			}
			String lStrPayCommission = StringUtility.getParameter(
					"cmbPayCommission", request);
			if (lStrPayCommission != null && !(lStrPayCommission.equals("-1"))) {
				lObjHstDcpsOfficeChanges.setPayCommission(lStrPayCommission);
			}
			if (lStrParentFldDept != null && !lStrParentFldDept.equals("") && !lStrParentFldDept.equals("-1")) {
				lObjHstDcpsOfficeChanges.setParentDept(lStrParentFldDept);
			}
			if (lStrCadre != null && !(lStrCadre.equals("-1"))) {
				lObjHstDcpsOfficeChanges.setCadre(lStrCadre);
			}
			if (lStrGroup != null && !(lStrGroup.equals(""))) {
				lObjHstDcpsOfficeChanges.setGroup(lStrGroup);
			}
			if (lStrCurrentOffice != null && !(lStrCurrentOffice.equals("-1"))) {
				lObjHstDcpsOfficeChanges.setCurrOff(lStrCurrentOffice);
			}
			
			String lStrFirstDesignation = StringUtility.getParameter(
					"cmbFirstDesignation", request);
			String lStrAppointmentDate = StringUtility.getParameter(
					"txtJoinParentDeptDate", request);
			String lStrJoinPostDate = StringUtility.getParameter(
					"txtJoinPostDate", request);
			String lStrJoinCadreDate = StringUtility.getParameter(
					"txtJoinCadreDate", request);
			String lStrRemarks = StringUtility.getParameter("txtRemarks",
					request);
			String lStrPayScale = StringUtility.getParameter("cmbPayScale",
					request);
			String lStrbasicPay = StringUtility.getParameter("txtBasicPay",
					request);
			
			if (lStrRemarks != null && !(lStrRemarks.equals(""))) {
				lObjHstDcpsOfficeChanges.setRemarks(lStrRemarks);
			}
			if (lStrPayScale != null && !(lStrPayScale.equals(""))
					&& !lStrPayScale.equals("-1")) {
				lObjHstDcpsOfficeChanges.setPayScale(lStrPayScale);
			}
			
			if (lStrbasicPay != null && !(lStrbasicPay.equals("")))
			{
				lObjHstDcpsOfficeChanges.setBasicPay(Double
						.parseDouble(lStrbasicPay));
			}
			if (lStrFirstDesignation != null
					&& !(lStrFirstDesignation.equals(""))) {
				lObjHstDcpsOfficeChanges
						.setFirstDesignation(lStrFirstDesignation);
			}
			Date dtAppointmentDate = null;
			if (lStrAppointmentDate != null
					&& !"".equals(lStrAppointmentDate.trim())) {
				dtAppointmentDate = simpleDateFormat.parse(lStrAppointmentDate);
				lObjHstDcpsOfficeChanges.setAppointmentDate(dtAppointmentDate);
			}
			Date dtJoinPostDate = null;
			if (lStrJoinPostDate != null && !"".equals(lStrJoinPostDate.trim())) {
				dtJoinPostDate = simpleDateFormat.parse(lStrJoinPostDate);
			}
			Date dtJoinCadreDate = null;
			if (lStrJoinCadreDate != null
					&& !"".equals(lStrJoinCadreDate.trim())) {
				dtJoinCadreDate = simpleDateFormat.parse(lStrJoinCadreDate);
			}
			if (dtJoinPostDate != null && !(dtJoinPostDate.equals(""))) {
				lObjHstDcpsOfficeChanges.setCurrPostJoiningDate(dtJoinPostDate);
			}
			if (dtJoinCadreDate != null && !(dtJoinCadreDate.equals(""))) {
				lObjHstDcpsOfficeChanges
						.setCurrCadreJoiningDate(dtJoinCadreDate);
			}
			
			Long lLongReasonForSalChange = null;
			String lStrReasonForSalaryChange = StringUtility.getParameter("cmbReasonForSalChange", request) ;
			if(!lStrReasonForSalaryChange.equalsIgnoreCase("") && !lStrReasonForSalaryChange.equalsIgnoreCase("-1"))
			{
				lLongReasonForSalChange = Long.valueOf(lStrReasonForSalaryChange);
			}
			lObjHstDcpsOfficeChanges.setReasonForPSChange(lLongReasonForSalChange);
			
			
			String lStrOtherReasonForSalChange = StringUtility.getParameter("txtOtherReason", request);
			if(!lStrOtherReasonForSalChange.equalsIgnoreCase(""))
			{
				lObjHstDcpsOfficeChanges.setOtherReasonForPSChange(lStrOtherReasonForSalChange);
			}
			
			
			String lStrWithEffectFromDate = StringUtility.getParameter("txtWithEffectFromDate", request);
			Date lDateWithEffectFromDate = null ;
			if(!"".equals(lStrWithEffectFromDate))
			{
				lDateWithEffectFromDate = simpleDateFormat.parse(lStrWithEffectFromDate);
			}
			if (lDateWithEffectFromDate != null && !(lDateWithEffectFromDate.equals(""))) {
				lObjHstDcpsOfficeChanges.setWithEffectFromDate(lDateWithEffectFromDate);
			}
			
			lObjHstDcpsOfficeChanges.setDbId(lLngDbId);
			lObjHstDcpsOfficeChanges.setLangId(lLngLangId);
			lObjHstDcpsOfficeChanges.setLocId(lLngLocId);
			lObjHstDcpsOfficeChanges.setCreatedPostId(gLngPostId);
			lObjHstDcpsOfficeChanges.setCreatedUserId(gLngUserId);
			lObjHstDcpsOfficeChanges.setCreatedDate(gDtCurrDt);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lObjHstDcpsOfficeChanges;
	}
}
