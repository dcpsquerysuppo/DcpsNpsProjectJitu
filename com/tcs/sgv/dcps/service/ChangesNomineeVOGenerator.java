/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 26, 2011		Vihan Khatri								
 *******************************************************************************
 */

/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 26, 2011
 */

package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DCPSNomineeDtlsVOGenerator
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.HstDcpsNomineeChanges;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;

public class ChangesNomineeVOGenerator extends ServiceImpl implements
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

		HstDcpsNomineeChanges[] lArrHstDcpsNomineeChanges = null;
		HstDcpsNomineeChanges[] lArrDcpsNomineesFromMst = null;
		try {
			// inputMap.put("DCPSNomineeDtls", lObjNomineeDtls);
			lArrHstDcpsNomineeChanges = generateNomineeData(inputMap);
			inputMap
					.put("lArrHstDcpsNomineeChanges", lArrHstDcpsNomineeChanges);

			lArrDcpsNomineesFromMst = getAndGenerateNomineesListFromMst(inputMap);
			inputMap.put("lArrDcpsNomineesFromMst", lArrDcpsNomineesFromMst);
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

	public HstDcpsNomineeChanges[] generateNomineeData(Map inputMap)
			throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		String lStrNomName = StringUtility
				.getParameter("lNomineeName", request);
		String lStrAddress1 = StringUtility.getParameter("lAddress1", request);
		String lStrDateOfBirth = StringUtility.getParameter("lDob", request);
		String lStrPercentShare = StringUtility.getParameter("lPercentShare",
				request);

		String lStrRelationship = StringUtility.getParameter("lRelationship",
				request);
		String lStrNomGuardian="";
		String lStrNomInvalid="";
		lStrNomGuardian = StringUtility.getParameter("guardian", request);
		lStrNomInvalid = StringUtility.getParameter("invalid", request);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		String[] lArrNomName = lStrNomName.split("~");
		String[] lArrAddress1 = lStrAddress1.split("~");
		String[] lArrDateOfBirth = lStrDateOfBirth.split("~");
		String[] lArrPercentShare = lStrPercentShare.split("~");
		String[] lArrRelationship = lStrRelationship.split("~");
		String[] lArrNomGuardian = lStrNomGuardian.split("~");
		String[] lArrNomInvalid = lStrNomInvalid.split("~");

		HstDcpsNomineeChanges[] lArrHstDcpsNomineeChanges = new HstDcpsNomineeChanges[lArrNomName.length];

		for (Integer i = 0; i < lArrNomName.length; i++) {

			HstDcpsNomineeChanges lObjHstDcpsNomineeChanges = new HstDcpsNomineeChanges();

			lObjHstDcpsNomineeChanges.setName(lArrNomName[i]);
			lObjHstDcpsNomineeChanges.setAddress1(lArrAddress1[i]);
			Date[] lArrDtBirthDate = new Date[lArrNomName.length];

			if (lStrDateOfBirth != null && !"".equals(lStrDateOfBirth.trim())) {

				lArrDtBirthDate[i] = sdf.parse(lArrDateOfBirth[i]);
			}

			lObjHstDcpsNomineeChanges.setDob(lArrDtBirthDate[i]);
			Long lLngPercentShare = Long.parseLong(lArrPercentShare[i]);
			lObjHstDcpsNomineeChanges.setShare(lLngPercentShare);
			lObjHstDcpsNomineeChanges.setRlt(lArrRelationship[i]);
			lObjHstDcpsNomineeChanges.setDbId(lLngDbId);
			lObjHstDcpsNomineeChanges.setLangId(lLngLangId);
			lObjHstDcpsNomineeChanges.setLocId(lLngLocId);
			lObjHstDcpsNomineeChanges.setCreatedPostId(gLngPostId);
			lObjHstDcpsNomineeChanges.setCreatedUserId(gLngUserId);
			lObjHstDcpsNomineeChanges.setCreatedDate(gDtCurrDt);
			if(lArrNomGuardian!=null && lArrNomGuardian.length>i)
				lObjHstDcpsNomineeChanges.setGuardian(lArrNomGuardian[i]);
			if(lArrNomInvalid!=null && lArrNomInvalid.length>i)
				lObjHstDcpsNomineeChanges.setInValid(lArrNomInvalid[i]);

			lArrHstDcpsNomineeChanges[i] = lObjHstDcpsNomineeChanges;

		}

		return lArrHstDcpsNomineeChanges;
	}

	public HstDcpsNomineeChanges[] getAndGenerateNomineesListFromMst(
			Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmpNmn.class,
				serv.getSessionFactory());

		String lStrEmpId = StringUtility.getParameter("empId", request);
		List<MstEmpNmn> lListNomineesFromMst = null;

		lListNomineesFromMst = lObjNewRegDdoDAO.getNominees(lStrEmpId);

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		new SimpleDateFormat("dd/MM/yyyy");

		HstDcpsNomineeChanges[] lArrDcpsNomineesFromMst = new HstDcpsNomineeChanges[lListNomineesFromMst
				.size()];

		for (Integer i = 0; i < lListNomineesFromMst.size(); i++) {

			HstDcpsNomineeChanges lObjHstDcpsNomineeChanges = new HstDcpsNomineeChanges();

			lObjHstDcpsNomineeChanges.setName(lListNomineesFromMst.get(i)
					.getName());
			lObjHstDcpsNomineeChanges.setAddress1(lListNomineesFromMst.get(i)
					.getAddress1());
			lObjHstDcpsNomineeChanges.setDob(lListNomineesFromMst.get(i)
					.getDob());
			lObjHstDcpsNomineeChanges.setShare(lListNomineesFromMst.get(i)
					.getShare());
			lObjHstDcpsNomineeChanges.setRlt(lListNomineesFromMst.get(i)
					.getRlt());
			lObjHstDcpsNomineeChanges.setDbId(lLngDbId);
			lObjHstDcpsNomineeChanges.setLangId(lLngLangId);
			lObjHstDcpsNomineeChanges.setLocId(lLngLocId);
			lObjHstDcpsNomineeChanges.setCreatedPostId(gLngPostId);
			lObjHstDcpsNomineeChanges.setCreatedUserId(gLngUserId);
			lObjHstDcpsNomineeChanges.setCreatedDate(gDtCurrDt);

			lArrDcpsNomineesFromMst[i] = lObjHstDcpsNomineeChanges;

		}

		return lArrDcpsNomineesFromMst;
	}
}
