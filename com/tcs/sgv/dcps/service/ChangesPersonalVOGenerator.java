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
import com.tcs.sgv.dcps.valueobject.HstDcpsPersonalChanges;

public class ChangesPersonalVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	@Override
	public ResultObject generateMap(Map inputMap) {

		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HstDcpsPersonalChanges lObjHstDcpsPersonalChanges = null;

		try {
			lObjHstDcpsPersonalChanges = generateEmpData(inputMap);
			inputMap.put("lObjHstDcpsPersonalChanges",
					lObjHstDcpsPersonalChanges);
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

	private HstDcpsPersonalChanges generateEmpData(Map inputMap)
			throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		inputMap.get("serviceLocator");

		HstDcpsPersonalChanges lObjHstDcpsPersonalChanges = new HstDcpsPersonalChanges();

		// Get the audit purpose details from session helper
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		try {
			String lStrName = StringUtility.getParameter("txtName", request).trim();
			String lStrGender = StringUtility.getParameter("radioGender",
					request).trim();
			String lStrHandicapped = StringUtility.getParameter("radioHandic",
					request).trim();
			String lStrBirthDate = StringUtility.getParameter("txtBirthDate",
					request).trim();
			String lStrNameMarathi = StringUtility.getParameter(
					"txtNameInMarathi", request).trim();
			String lStrFatherHusband = StringUtility.getParameter(
					"txtFatherOrHusband", request).trim();
			String lStrSalutation = StringUtility.getParameter("cmbSalutation",
					request).trim();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			Date lDtBirthDate = null;
			if (lStrBirthDate != null && !"".equals(lStrBirthDate.trim())) {
				lDtBirthDate = sdf.parse(lStrBirthDate);
			}

			String lStrJoiningDate = StringUtility.getParameter(
					"txtJoiningDate", request);

			Date lDtJoiningDate = null;

			if (lStrJoiningDate != null && !"".equals(lStrJoiningDate.trim())) {
				lDtJoiningDate = sdf.parse(lStrJoiningDate);
			}

			StringUtility.getParameter("txtResidentialAddress", request);
			StringUtility.getParameter("txtAddress2", request);
			String lStrCellNo = StringUtility
					.getParameter("txtCellNo", request);
			String lStrContactTelNo = StringUtility.getParameter(
					"txtContactTelNo", request);

			if(!"".equals(lStrName))
			{
				lObjHstDcpsPersonalChanges.setName(lStrName.toUpperCase());
			}
			if(!"".equals(lStrGender))
			{
				lObjHstDcpsPersonalChanges.setGender(lStrGender.trim().toUpperCase().charAt(0));
			}
			if(!"".equals(lStrNameMarathi))
			{
				lObjHstDcpsPersonalChanges.setName_marathi(lStrNameMarathi);
			}
			if(!"-1".equals(lStrSalutation))
			{
				lObjHstDcpsPersonalChanges.setSalutation(lStrSalutation);
			}
			if(!"".equals(lStrFatherHusband))
			{
				lObjHstDcpsPersonalChanges.setFather_or_husband(lStrFatherHusband);
			}
			if(!"".equals(lDtBirthDate))
			{
				lObjHstDcpsPersonalChanges.setDob(lDtBirthDate);
			}
			if(!"".equals(lDtJoiningDate))
			{
				lObjHstDcpsPersonalChanges.setDoj(lDtJoiningDate);
			}
			if(!"".equals(lStrHandicapped))
			{
				lObjHstDcpsPersonalChanges.setPhychallanged(lStrHandicapped);
			}
			String lStrBuilding = StringUtility.getParameter(
					"txtAddressBuilding", request);
			String lStrStreet = StringUtility.getParameter("txtAddressStreet",
					request);
			String lStrLandmark = StringUtility.getParameter("txtLandmark",
					request);
			String lStrLocality = StringUtility.getParameter("txtLocality",
					request);
			String lStrDistrict = StringUtility.getParameter("txtDistrict",
					request);
			String lStrState = StringUtility.getParameter("cmbState", request);
			String lStrPinCode = StringUtility.getParameter("txtPinCode",
					request);
			String lStrEmailId = StringUtility.getParameter("txtEmailId",
					request);
			String lStrUIDNo1 = StringUtility.getParameter("txtUIDNo1", request);
			String lStrUIDNo2 = StringUtility.getParameter("txtUIDNo2", request);
			String lStrUIDNo3 = StringUtility.getParameter("txtUIDNo3", request);
			String lStrUIDNo = lStrUIDNo1.concat(lStrUIDNo2.concat(lStrUIDNo3));
			String lStrEIDNo = StringUtility.getParameter("txtEIDNo", request);
			String lStrPANNo = StringUtility.getParameter("txtPANNo", request);
			
			//added by arpan 06/01
			String pLStrBuilding = StringUtility.getParameter("pTxtAddressBuilding", request).trim();
			String pLStrStreet = StringUtility.getParameter("pTxtAddressStreet",request).trim();
			String pLStrLandmark = StringUtility.getParameter("pTxtLandmark",request).trim();
			String pLStrLocality = StringUtility.getParameter("pTxtLocality",request).trim();
			String pLStrDistrict = StringUtility.getParameter("pTxtDistrict",request).trim();
			String pLStrState = StringUtility.getParameter("pCmbState", request).trim();
			String pLStrPinCode = StringUtility.getParameter("pTxtPinCode",request).trim();
			String pLStrCellNo = StringUtility.getParameter("pTxtCellNo", request).trim();
			String pLStrContactTelNo = StringUtility.getParameter("pTxtContactTelNo", request).trim();
			String pLStrAddressVTC = StringUtility.getParameter("pTxtAddressVTC", request).trim();
			String lStrAddressVTC = StringUtility.getParameter("txtAddressVTC", request).trim();
			String isAddressSame = StringUtility.getParameter("isAddressSame", request).trim();
			//end

			if (lStrBuilding != null && !(lStrBuilding.equals(""))) {
				lObjHstDcpsPersonalChanges.setBuilding_address(lStrBuilding);
			}

			if (lStrStreet != null && !(lStrStreet.equals(""))) {
				lObjHstDcpsPersonalChanges.setBuilding_street(lStrStreet);
			}

			if (lStrLandmark != null && !(lStrLandmark.equals(""))) {
				lObjHstDcpsPersonalChanges.setLandmark(lStrLandmark);
			}
			if (lStrLocality != null && !(lStrLocality.equals(""))) {
				lObjHstDcpsPersonalChanges.setLocality(lStrLocality);
			}
			if (lStrDistrict != null && !(lStrDistrict.equals(""))) {
				lObjHstDcpsPersonalChanges.setDistrict(lStrDistrict);
			}
			if (lStrState != null && !(lStrState.equals("-1"))) {
				lObjHstDcpsPersonalChanges.setState(lStrState);
			}
			//added by arpan
			if (lStrAddressVTC != null && !(lStrAddressVTC.equals(""))) {
				lObjHstDcpsPersonalChanges.setAddressVTC(lStrAddressVTC);
			}
			//end
			if (lStrPinCode != null && !(lStrPinCode.equals(""))) {
				lObjHstDcpsPersonalChanges.setPincode(Long
						.parseLong(lStrPinCode));
			}
			if (lStrEmailId != null && !(lStrEmailId.equals(""))) {
				lObjHstDcpsPersonalChanges.setEmailId(lStrEmailId);
			}

			if (lStrCellNo != null && !(lStrCellNo.equals(""))) {
				lObjHstDcpsPersonalChanges
						.setCellNo(lStrCellNo);
			}

			if (lStrContactTelNo != null && !(lStrContactTelNo.equals(""))) {
				lObjHstDcpsPersonalChanges.setCntctNo(lStrContactTelNo);
			}

			if (lStrUIDNo != null && !(lStrUIDNo.equals(""))) {
				lObjHstDcpsPersonalChanges.setUIDNo(lStrUIDNo);
			}
			
			if (lStrEIDNo != null && !(lStrEIDNo.equals(""))) {
				lObjHstDcpsPersonalChanges.setEIDNo(lStrEIDNo);
			}

			if (lStrPANNo != null && !(lStrPANNo.equals(""))) {
				lObjHstDcpsPersonalChanges.setPANNo(lStrPANNo);
			}

			if (lStrContactTelNo != null && !(lStrContactTelNo.equals(""))) {
				lObjHstDcpsPersonalChanges.setCntctNo(lStrContactTelNo);
			}
			if (lStrCellNo != null && !(lStrCellNo.equals(""))) {
				lObjHstDcpsPersonalChanges
						.setCellNo(lStrCellNo);
			}
			
			//added by arpan
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
				if (pLStrBuilding != null && !(pLStrBuilding.equals(""))) {
					lObjHstDcpsPersonalChanges.setPBuildingAddress(pLStrBuilding.trim());
				}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrBuilding != null && !(lStrBuilding.equals("")))
				lObjHstDcpsPersonalChanges.setPBuildingAddress(lStrBuilding.trim());

			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrStreet != null && !(pLStrStreet.equals(""))) {
				lObjHstDcpsPersonalChanges.setPBuildingStreet(pLStrStreet.trim());
			}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrStreet != null && !(lStrStreet.equals("")))
				lObjHstDcpsPersonalChanges.setPBuildingStreet(lStrStreet.trim());

			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrLandmark != null && !(pLStrLandmark.equals(""))) {
				lObjHstDcpsPersonalChanges.setPLandmark(pLStrLandmark.trim());
			}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrLandmark != null && !(lStrLandmark.equals("")))
				lObjHstDcpsPersonalChanges.setPLandmark(lStrLandmark.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrLocality != null && !(pLStrLocality.equals(""))) {
				lObjHstDcpsPersonalChanges.setPLocality(pLStrLocality.trim());
			}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrLocality != null && !(lStrLocality.equals("")))
				lObjHstDcpsPersonalChanges.setPLocality(lStrLocality.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
				if (pLStrDistrict != null && !(pLStrDistrict.equals(""))) {
					lObjHstDcpsPersonalChanges.setPDistrict(pLStrDistrict.trim());
				}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrDistrict != null && !(lStrDistrict.equals("")))
				lObjHstDcpsPersonalChanges.setPDistrict(lStrDistrict.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
				if (pLStrState != null && !(pLStrState.equals(""))) {
					lObjHstDcpsPersonalChanges.setPState(pLStrState.trim());
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrState != null && !(lStrState.equals("")))
				lObjHstDcpsPersonalChanges.setPState(lStrState.trim());

			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrPinCode != null && !(pLStrPinCode.equals(""))) {
				lObjHstDcpsPersonalChanges.setPPincode(Long.parseLong(pLStrPinCode.trim()));
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrPinCode != null && !(lStrPinCode.equals("")))
				lObjHstDcpsPersonalChanges.setPPincode(Long.parseLong(lStrPinCode.trim()));

			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
				if (pLStrCellNo != null && !(pLStrCellNo.equals(""))){ 
					lObjHstDcpsPersonalChanges.setPCellNo(pLStrCellNo.trim());
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrCellNo != null && !(lStrCellNo.equals("")))
				lObjHstDcpsPersonalChanges.setPCellNo(lStrCellNo.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrContactTelNo != null && !(pLStrContactTelNo.equals(""))) {
				lObjHstDcpsPersonalChanges.setPCntctNo(pLStrContactTelNo.trim());
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrContactTelNo != null && !(lStrContactTelNo.equals("")))
				lObjHstDcpsPersonalChanges.setPCntctNo(lStrContactTelNo.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrAddressVTC != null && !(pLStrAddressVTC.equals(""))) {
				lObjHstDcpsPersonalChanges.setPCntctNo(pLStrAddressVTC.trim());
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y") && lStrAddressVTC != null && !(lStrAddressVTC.equals("")))
				lObjHstDcpsPersonalChanges.setPCntctNo(lStrAddressVTC.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")))
			{
				lObjHstDcpsPersonalChanges.setIsAddressSame(isAddressSame.trim().toUpperCase());
			}
			//end
			lObjHstDcpsPersonalChanges.setDbId(lLngDbId);
			lObjHstDcpsPersonalChanges.setLangId(lLngLangId);
			lObjHstDcpsPersonalChanges.setLocId(lLngLocId);
			lObjHstDcpsPersonalChanges.setCreatedPostId(gLngPostId);
			lObjHstDcpsPersonalChanges.setCreatedUserId(gLngUserId);
			lObjHstDcpsPersonalChanges.setCreatedDate(gDtCurrDt);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lObjHstDcpsPersonalChanges;
	}
}
