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
import com.tcs.sgv.dcps.valueobject.HstDcpsOtherChanges;

public class ChangesOtherVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	@Override
	public ResultObject generateMap(Map inputMap) {
		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HstDcpsOtherChanges lObjHstDcpsOtherChanges = null;

		try {
			lObjHstDcpsOtherChanges = generateEmpData(inputMap);
			inputMap.put("lObjHstDcpsOtherChanges", lObjHstDcpsOtherChanges);
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

	private HstDcpsOtherChanges generateEmpData(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		HstDcpsOtherChanges lObjHstDcpsOtherChanges = new HstDcpsOtherChanges();

		// Get the audit purpose details from session helper
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		try {

			String lStrBankName = StringUtility.getParameter("cmbBankName",
					request).trim();
			String lStrBranchName = StringUtility.getParameter("cmbBranchName",
					request).trim();
			String lStrBankAccountNo = StringUtility.getParameter(
					"txtbankAccountNo", request).trim();
			String lStrIFSCCode = StringUtility.getParameter("txtIFSCCode",
					request).trim();
			
			String lStrDcpsOrGPF = StringUtility.getParameter("radioDCPS",
					request).trim();
			if (!"".equals(lStrDcpsOrGPF.trim())) {
				lObjHstDcpsOtherChanges.setDcpsOrGpf(lStrDcpsOrGPF.trim().toCharArray()[0]);
			}
			
			String lStrDcpsAcMntndBy = StringUtility.getParameter(
					"dcpsAcntMntndBy", request).trim();
			if (!"".equals(lStrDcpsAcMntndBy.trim())
					&& !(lStrDcpsAcMntndBy.equals("-1"))) {
				lObjHstDcpsOtherChanges.setAcDcpsMaintainedBy(lStrDcpsAcMntndBy.trim());
			} else {
				lObjHstDcpsOtherChanges.setAcDcpsMaintainedBy(null);
			}
			
			String lStrAcNoNonSRKAEmp = StringUtility.getParameter(
					"txtAcNoForNonSRKAEmp", request).trim();
			if (!"".equals(lStrAcNoNonSRKAEmp.trim())) {
				lObjHstDcpsOtherChanges.setAcNonSRKAEmp(lStrAcNoNonSRKAEmp.trim());
			}
			
			String lStrAcMntndByOthers = StringUtility.getParameter(
					"txtAcNoMntndByOthers", request).trim();
			if (!"".equals(lStrAcMntndByOthers.trim())) {
				lObjHstDcpsOtherChanges.setAcMntndByOthers(lStrAcMntndByOthers.trim());
			}
			
			String lStrAcMaintainedBy = StringUtility.getParameter(
					"cmbAcMaintainedBy", request).trim();
			if (lStrAcMaintainedBy != null	&& !(lStrAcMaintainedBy.equals("-1"))) {
				lObjHstDcpsOtherChanges.setAcMaintainedBy(lStrAcMaintainedBy.trim());
			}
			
			String lStrPFSeries = StringUtility.getParameter("cmbPFSeries", request).trim();
			if (lStrPFSeries != null && !(lStrPFSeries.equals(""))) {
				lObjHstDcpsOtherChanges.setPfSeries(lStrPFSeries.trim());
			} else {
				lObjHstDcpsOtherChanges.setPfSeries(null);
			}
			
			String lStrPfSeriesDesc = StringUtility.getParameter("txtPFSeriesDesc",
					request).trim();
			if (lStrPfSeriesDesc != null && !(lStrPfSeriesDesc.trim().equals(""))) {
				lObjHstDcpsOtherChanges.setPfSeriesDesc(lStrPfSeriesDesc.trim());
			}
			
			String lStrPfAccountNo = StringUtility.getParameter("txtPfAccountNo",
					request).trim();
			if (lStrPfAccountNo != null && !(lStrPfAccountNo.equals(""))) {
				lObjHstDcpsOtherChanges.setPfAcNo(lStrPfAccountNo.trim());
			}

			if (lStrBankName != null && !(lStrBankName.equals("-1"))) {
				lObjHstDcpsOtherChanges.setBankName(lStrBankName);
			}

			if (lStrBranchName != null && !(lStrBranchName.equals("-1"))) {
				lObjHstDcpsOtherChanges.setBranchName(lStrBranchName);
			}
			if (lStrBankAccountNo != null && !(lStrBankAccountNo.equals(""))) {
				lObjHstDcpsOtherChanges.setBankAccountNo(lStrBankAccountNo);
			}
			if (lStrIFSCCode != null && !(lStrIFSCCode.equals(""))) {
				lObjHstDcpsOtherChanges.setIFSCCode(lStrIFSCCode);
			}

			lObjHstDcpsOtherChanges.setDbId(lLngDbId);
			lObjHstDcpsOtherChanges.setLangId(lLngLangId);
			lObjHstDcpsOtherChanges.setLocId(lLngLocId);
			lObjHstDcpsOtherChanges.setCreatedPostId(gLngPostId);
			lObjHstDcpsOtherChanges.setCreatedUserId(gLngUserId);
			lObjHstDcpsOtherChanges.setCreatedDate(gDtCurrDt);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lObjHstDcpsOtherChanges;
	}
}
