package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DCPSNomineeDtlsVOGenerator
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

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
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;


public class NomineeDtlsVOGenerator extends ServiceImpl implements VOGeneratorService {

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

		MstEmpNmn[] lArrNomineeDtls = null;

		try {
			// inputMap.put("DCPSNomineeDtls", lObjNomineeDtls);
			lArrNomineeDtls = generateNomineeData(inputMap);
			inputMap.put("DCPSNomineeDtls", lArrNomineeDtls);
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

	public MstEmpNmn[] generateNomineeData(Map inputMap) throws Exception {

		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		// DcpsEmpNmnMst lObjNomineeDtls=(DcpsEmpNmnMst)
		// inputMap.get("DCPSNomineeDtls");

		MstEmp lObjEmpData = (MstEmp) inputMap.get("DCPSEmpData");

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		String lStrNomName = StringUtility.getParameter("lNomineeName", request);
		String lStrAddress1 = StringUtility.getParameter("lAddress1", request);
		String lStrDateOfBirth = StringUtility.getParameter("lDob", request);
		String lStrPercentShare = StringUtility.getParameter("lPercentShare", request);
		String lStrNomGuardian = StringUtility.getParameter("guardian", request);
		String lStrNomInvalid = StringUtility.getParameter("invalid", request);
		String lStrRelationship = StringUtility.getParameter("lRelationship", request);

		Integer SaveOrUpdateNominee = Integer.parseInt(StringUtility.getParameter("SaveOrUpdateNominee", request));
		//System.out.println("SaveOrUpdateNominee" + SaveOrUpdateNominee);
		inputMap.put("SaveOrUpdateNominee", SaveOrUpdateNominee);

		// START: To get the employee id for saving the nominees
		String lStrEmpId = StringUtility.getParameter("empId", request);
		Long lLngEmpID = Long.parseLong(lStrEmpId);

		inputMap.put("lLngEmpID", lLngEmpID);
		//System.out.println("The employee id generated for nominee is " + lLngEmpID);
		NewRegDdoDAO dcpsRegisDAO = new NewRegDdoDAOImpl(MstEmp.class, servLoc.getSessionFactory());
		lObjEmpData = (MstEmp) dcpsRegisDAO.read(lLngEmpID);
		// END

		String[] lArrNomName = lStrNomName.split("~");
		String[] lArrAddress1 = lStrAddress1.split("~");
		String[] lArrDateOfBirth = lStrDateOfBirth.split("~");
		String[] lArrPercentShare = lStrPercentShare.split("~");
		String[] lArrRelationship = lStrRelationship.split("~");
		String[] lArrNomGuardian = lStrNomGuardian.split("~");
		String[] lArrNomInvalid = lStrNomInvalid.split("~");
		
		MstEmpNmn[] lArrNomineeDtls = new MstEmpNmn[lArrNomName.length];

		if (SaveOrUpdateNominee > 1) {
			dcpsRegisDAO.deleteNomineesForGivenEmployee(lLngEmpID);
		}

		for (int i = 0; i < lArrNomName.length; i++) {
			MstEmpNmn lObjNomineeDtls = new MstEmpNmn();

			lObjNomineeDtls.setDcpsEmpId(lObjEmpData);
			lObjNomineeDtls.setName(lArrNomName[i]);
			lObjNomineeDtls.setAddress1(lArrAddress1[i]);
			Date dtBirthDate = null;

			if (lStrDateOfBirth != null && !"".equals(lStrDateOfBirth.trim())) {

				dtBirthDate = IFMSCommonServiceImpl.getDateFromString(lArrDateOfBirth[i]);
			}

			lObjNomineeDtls.setDob(dtBirthDate);
			long lLngPercentShare = Long.parseLong(lArrPercentShare[i]);
			lObjNomineeDtls.setShare(lLngPercentShare);
			lObjNomineeDtls.setRlt(lArrRelationship[i]);
			lObjNomineeDtls.setDbId(lLngDbId);
			lObjNomineeDtls.setLangId(lLngLangId);
			lObjNomineeDtls.setLocId(lLngLocId);
			lObjNomineeDtls.setCreatedPostId(gLngPostId);
			lObjNomineeDtls.setCreatedUserId(gLngUserId);
			lObjNomineeDtls.setCreatedDate(gDtCurrDt);
			System.out.println("i-----"+i);
			lObjNomineeDtls.setGuardian(lArrNomGuardian[i]);
			lObjNomineeDtls.setInValid(lArrNomInvalid[i]);
			
			lArrNomineeDtls[i] = lObjNomineeDtls;
		}

		return lArrNomineeDtls;

	}
}
