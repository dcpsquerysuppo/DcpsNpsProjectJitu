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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.HstDcpsChanges;
import com.tcs.sgv.dcps.valueobject.TrnDcpsChanges;

public class ChangesVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	@Override
	public ResultObject generateMap(Map inputMap) {
		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HstDcpsChanges lObjChangesData = null;
		List<TrnDcpsChanges> lObjTrnDcpsChangesList = null;

		try {
			inputMap.put("lObjChangesData", lObjChangesData);
			lObjChangesData = generateVOMap(inputMap);
			inputMap.put("lObjChangesData", lObjChangesData);

			lObjTrnDcpsChangesList = generateTrnChangesVOList(inputMap);
			inputMap.put("lObjTrnDcpsChangesList", lObjTrnDcpsChangesList);

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

	private HstDcpsChanges generateVOMap(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		HstDcpsChanges lObjHstDcpsChanges = new HstDcpsChanges();

		// Get the audit purpose details from session helper
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		try {

			String lStrLetterNo = StringUtility.getParameter(
					"txtAuthorityLetterNo", request);
			String lStrLetterDate = StringUtility.getParameter(
					"txtAuthorityLetterDate", request);

			String lStrDdocode = StringUtility.getParameter("txtDdoCode",
					request);

			if (lStrLetterNo != null && !(lStrLetterNo.equals(""))) {
				lObjHstDcpsChanges.setLetterNo(Long.valueOf(lStrLetterNo));
			}

			Date lDtLetterDate = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if (lStrLetterDate != null && !"".equals(lStrLetterDate.trim())) {
				lDtLetterDate = sdf.parse(lStrLetterDate);
			}

			if (lDtLetterDate != null && !(lDtLetterDate.equals(""))) {
				lObjHstDcpsChanges.setLetterDate(lDtLetterDate);
			}

			lObjHstDcpsChanges.setDdoCode(lStrDdocode);
			lObjHstDcpsChanges.setDbId(lLngDbId);
			lObjHstDcpsChanges.setLangId(lLngLangId);
			lObjHstDcpsChanges.setLocId(lLngLocId);

			lObjHstDcpsChanges.setCreatedPostId(gLngPostId);
			lObjHstDcpsChanges.setCreatedUserId(gLngUserId);
			lObjHstDcpsChanges.setCreatedDate(gDtCurrDt);

			// END
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lObjHstDcpsChanges;
	}

	private List<TrnDcpsChanges> generateTrnChangesVOList(Map inputMap)
			throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		List<TrnDcpsChanges> lObjTrnDcpsChangesList = new ArrayList<TrnDcpsChanges>();

		try {

			// Get the audit purpose details from session helper
			Long gLngPostId = SessionHelper.getPostId(inputMap);
			Long gLngUserId = SessionHelper.getUserId(inputMap);
			Long lLngDbId = SessionHelper.getDbId(inputMap);
			Long lLngLocId = SessionHelper.getLocationId(inputMap);
			Long lLngLangId = SessionHelper.getLangId(inputMap);
			Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
			Date lDtLetterDate = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			String lStrLetterNo = StringUtility.getParameter(
					"txtAuthorityLetterNo", request);
			String lStrLetterDate = StringUtility.getParameter(
					"txtAuthorityLetterDate", request);

			String lStrDdocode = StringUtility.getParameter("txtDdoCode",
					request);

			String lStrChange = StringUtility.getParameter("change", request);

			if (!lStrChange.equals("NomineeDetails")
					&& !lStrChange.equals("PhotoAndSignDetails")) {

				String lStrChangeDetails = StringUtility.getParameter(
						"changeDetails", request);
				if(lStrChangeDetails.contains(",~"))
				{
					lStrChangeDetails = lStrChangeDetails.replace(",~", ",%%~");        // When old value is null but new value is there.
				}
				
				
				String[] lStrArrChangeDtls = lStrChangeDetails.split("~");

				for (Integer lInt1 = 0; lInt1 < lStrArrChangeDtls.length; lInt1++) {

					TrnDcpsChanges lObjTrnDcpsChanges = new TrnDcpsChanges();

					String[] lStrArrChangeOfOneDtl = new String[3];
					
					lStrArrChangeOfOneDtl = lStrArrChangeDtls[lInt1].split(",");
				
					lObjTrnDcpsChanges.setFieldName(lStrArrChangeOfOneDtl[0]
							.trim());

					if (!lStrArrChangeOfOneDtl[1].equalsIgnoreCase("")) {
						lObjTrnDcpsChanges.setNewValue(lStrArrChangeOfOneDtl[1]
								.trim());
					} else {
						lObjTrnDcpsChanges.setNewValue("");
					}

					if (!lStrArrChangeOfOneDtl[2].equalsIgnoreCase("") &&  !lStrArrChangeOfOneDtl[2].equalsIgnoreCase("%%")) {
						lObjTrnDcpsChanges.setOldValue(lStrArrChangeOfOneDtl[2]
								.trim());
					} else {
						lObjTrnDcpsChanges.setOldValue("");
					}

					if (lStrLetterNo != null && !(lStrLetterNo.equals(""))) {
						lObjTrnDcpsChanges.setLetterNo(Long
								.valueOf(lStrLetterNo));
					}

					if (lStrLetterDate != null
							&& !"".equals(lStrLetterDate.trim())) {
						lDtLetterDate = sdf.parse(lStrLetterDate);
					}

					if (lDtLetterDate != null && !(lDtLetterDate.equals(""))) {
						lObjTrnDcpsChanges.setLetterDate(lDtLetterDate);
					}

					lObjTrnDcpsChanges.setDdoCode(lStrDdocode);
					lObjTrnDcpsChanges.setDbId(lLngDbId);
					lObjTrnDcpsChanges.setLangId(lLngLangId);
					lObjTrnDcpsChanges.setLocId(lLngLocId);

					lObjTrnDcpsChanges.setCreatedPostId(gLngPostId);
					lObjTrnDcpsChanges.setCreatedUserId(gLngUserId);
					lObjTrnDcpsChanges.setCreatedDate(gDtCurrDt);

					lObjTrnDcpsChangesList.add(lObjTrnDcpsChanges);
				}
			}
			if (lStrChange.equals("NomineeDetails")) {
				TrnDcpsChanges lObjTrnDcpsChanges = new TrnDcpsChanges();

				if (lStrLetterNo != null && !(lStrLetterNo.equals(""))) {
					lObjTrnDcpsChanges.setLetterNo(Long.valueOf(lStrLetterNo));
				}

				if (lStrLetterDate != null && !"".equals(lStrLetterDate.trim())) {
					lDtLetterDate = sdf.parse(lStrLetterDate);
				}

				if (lDtLetterDate != null && !(lDtLetterDate.equals(""))) {
					lObjTrnDcpsChanges.setLetterDate(lDtLetterDate);
				}

				lObjTrnDcpsChanges.setDdoCode(lStrDdocode);
				lObjTrnDcpsChanges.setDbId(lLngDbId);
				lObjTrnDcpsChanges.setLangId(lLngLangId);
				lObjTrnDcpsChanges.setLocId(lLngLocId);

				lObjTrnDcpsChanges.setCreatedPostId(gLngPostId);
				lObjTrnDcpsChanges.setCreatedUserId(gLngUserId);
				lObjTrnDcpsChanges.setCreatedDate(gDtCurrDt);

				lObjTrnDcpsChangesList.add(lObjTrnDcpsChanges);
			}
			if (lStrChange.equals("PhotoAndSignDetails")) {
				// Two TRN VOs generated by default.One for Photo and One for
				// Sign.
				for (Integer lInt = 0; lInt < 2; lInt++) {
					TrnDcpsChanges lObjTrnDcpsChanges = new TrnDcpsChanges();

					if (lStrLetterNo != null && !(lStrLetterNo.equals(""))) {
						lObjTrnDcpsChanges.setLetterNo(Long
								.valueOf(lStrLetterNo));
					}

					if (lStrLetterDate != null
							&& !"".equals(lStrLetterDate.trim())) {
						lDtLetterDate = sdf.parse(lStrLetterDate);
					}

					if (lDtLetterDate != null && !(lDtLetterDate.equals(""))) {
						lObjTrnDcpsChanges.setLetterDate(lDtLetterDate);
					}

					lObjTrnDcpsChanges.setDdoCode(lStrDdocode);
					lObjTrnDcpsChanges.setDbId(lLngDbId);
					lObjTrnDcpsChanges.setLangId(lLngLangId);
					lObjTrnDcpsChanges.setLocId(lLngLocId);

					lObjTrnDcpsChanges.setCreatedPostId(gLngPostId);
					lObjTrnDcpsChanges.setCreatedUserId(gLngUserId);
					lObjTrnDcpsChanges.setCreatedDate(gDtCurrDt);

					lObjTrnDcpsChangesList.add(lObjTrnDcpsChanges);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lObjTrnDcpsChangesList;
	}
}
