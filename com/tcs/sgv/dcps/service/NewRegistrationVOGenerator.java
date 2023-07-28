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
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;

public class NewRegistrationVOGenerator extends ServiceImpl implements
VOGeneratorService {

	@Override
	public ResultObject generateMap(Map inputMap) {

		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		MstEmp lObjEmpData = null;
		RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = null;

		try {
			inputMap.put("DCPSEmpData", lObjEmpData);
			lObjEmpData = generateEmpData(inputMap);
			inputMap.put("DCPSEmpData", lObjEmpData);

			lObjRltDcpsPayrollEmp = generateEmpPayrollData(inputMap);
			inputMap.put("DCPSEmpPayrollData", lObjRltDcpsPayrollEmp);

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

	private MstEmp generateEmpData(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap
		.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) inputMap
		.get("serviceLocator");
		NewRegDdoDAO dcpsRegisDAO = new NewRegDdoDAOImpl(MstEmp.class, servLoc
				.getSessionFactory());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String saveOrUpdateFlag = StringUtility.getParameter(
				"saveOrUpdateFlag", request);
		Integer intSaveOrUpdateFlag = Integer.parseInt(saveOrUpdateFlag);
		Long lLngEmpId = 0l;
		MstEmp lObjEmpData = null;
		Long lLongRegStatus = 0L;

		if (intSaveOrUpdateFlag == 1) {
			if (lObjEmpData == null) {
				lObjEmpData = new MstEmp();
			}
		}

		if (intSaveOrUpdateFlag == 2) {

			lLngEmpId = Long.parseLong(StringUtility.getParameter("empId",
					request));
			lObjEmpData = (MstEmp) dcpsRegisDAO.read(lLngEmpId);
			// lObjEmpData.setFormStatus(MstEmpObj.getFormStatus());
		}
		inputMap.put("lLngEmpId", lLngEmpId);

		// Get the audit purpose details from session helper
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		try {

			String lStrEmpName = StringUtility.getParameter("txtName", request).trim();
			String lStrNameMarathi = StringUtility.getParameter(
					"txtNameInMarathi", request).trim();
			String lStrFatherHusband = StringUtility.getParameter(
					"txtFatherOrHusband", request).trim();
			String lStrSalutation = StringUtility.getParameter("cmbSalutation",
					request).trim();
			String lStrGender = StringUtility.getParameter("radioGender",
					request).trim();
			String lStrBirthDate = StringUtility.getParameter("txtBirthDate",
					request).trim();
			String lStrSuperAnnDate = StringUtility.getParameter("txtSuperAnnDate",
					request).trim();
			String lStrJoiningDate = StringUtility.getParameter(
					"txtJoiningDate", request).trim();
			String lStrDesignation = StringUtility.getParameter(
					"cmbDesignation", request).trim();
			String lStrPayCommission = StringUtility.getParameter(
					"cmbPayCommission", request).trim();
			String lStrBasicPay = StringUtility.getParameter("txtBasicPay",
					request).trim();
			String lStrPayScale = StringUtility.getParameter("cmbPayScale",
					request).trim();
			String lStrBuilding = StringUtility.getParameter(
					"txtAddressBuilding", request).trim();
			String lStrStreet = StringUtility.getParameter("txtAddressStreet",
					request).trim();
			String lStrLandmark = StringUtility.getParameter("txtLandmark",
					request).trim();
			String lStrLocality = StringUtility.getParameter("txtLocality",
					request).trim();
			String lStrDistrict = StringUtility.getParameter("txtDistrict",
					request).trim();
			String lStrState = StringUtility.getParameter("cmbState", request).trim();
			String lStrPinCode = StringUtility.getParameter("txtPinCode",
					request).trim();
			String lStrCellNo = StringUtility
			.getParameter("txtCellNo", request).trim();
			String lStrContactTelNo = StringUtility.getParameter(
					"txtContactTelNo", request).trim();
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
			String isAddressSame = StringUtility.getParameter("isAddressSame", request).trim();
			
			//end
			String lStrEmailId = StringUtility.getParameter("txtEmailId",
					request).trim();

			String lStrDDOCode = StringUtility.getParameter("txtDdoCode",
					request).trim();

			String lStrParentFldDept = StringUtility.getParameter(
					"listParentDept", request).trim();
			String lStrReasonForChangeInPFD = StringUtility.getParameter(
					"reasonForChangeInPFD", request).trim();
			String lStrCadre = StringUtility.getParameter("cmbCadre", request).trim();
			String lStrGroup = StringUtility.getParameter("txtGroup", request).trim();
			String lStrCurrentOffice = StringUtility.getParameter(
					"cmbCurrentOffice", request).trim();

			String lStrFirstDesignation = StringUtility.getParameter(
					"cmbFirstDesignation", request).trim();
			String lStrAppointmentDate = StringUtility.getParameter(
					"txtJoinParentDeptDate", request).trim();
			String lStrRemarks = StringUtility.getParameter("txtRemarks",
					request).trim();
			String lStrEIDNo = StringUtility.getParameter("txtEIDNo", request).trim();
			String lStrUIDNo1 = StringUtility
			.getParameter("txtUIDNo1", request).trim();
			String lStrUIDNo2 = StringUtility
			.getParameter("txtUIDNo2", request).trim();
			String lStrUIDNo3 = StringUtility
			.getParameter("txtUIDNo3", request).trim();
			String lStrUIDNo = lStrUIDNo1.concat(lStrUIDNo2.concat(lStrUIDNo3));
			String lStrPANNo = StringUtility.getParameter("txtPANNo", request);
			String lStrBankName = StringUtility.getParameter("cmbBankName",
					request).trim();
			String lStrBranchName = StringUtility.getParameter("cmbBranchName",
					request).trim();
			String lStrIFSCCode = StringUtility.getParameter("txtIFSCCode",
					request).trim();
			String lStrBankAccountNo = StringUtility.getParameter(
					"txtbankAccountNo", request).trim();
			String lStrDcpsOrGPF = StringUtility.getParameter("radioDCPS",
					request).trim();
			String lStrDcpsAcMntndBy = StringUtility.getParameter(
					"dcpsAcntMntndBy", request).trim();
			String lStrAcNoNonSRKAEmp = StringUtility.getParameter(
					"txtAcNoForNonSRKAEmp", request).trim();
			String lStrAcMntndByOthers = StringUtility.getParameter(
					"txtAcNoMntndByOthers", request).trim();
			String lStrAcntMntndByOtherState = StringUtility.getParameter("dcpsAcntMntndByOtherState", request).trim();

			String lStrAddressVTC = StringUtility.getParameter("txtAddressVTC", request).trim();

			String lStrBuckleNo = StringUtility.getParameter("txtBuckleNo", request).trim();
			String lStrBioMetricNo = StringUtility.getParameter("txtBioMetricNo", request).trim();

			Date dtBirthDate = null;
			if (lStrBirthDate != null && !"".equals(lStrBirthDate.trim())) {
				dtBirthDate = simpleDateFormat.parse(lStrBirthDate.trim());
			}

			Date dtSuperAnnDate = null;
			if (lStrSuperAnnDate != null && !"".equals(lStrSuperAnnDate.trim())) {
				dtSuperAnnDate = simpleDateFormat.parse(lStrSuperAnnDate.trim());
			}

			Date dtJoiningDate = null;
			if (lStrJoiningDate != null && !"".equals(lStrJoiningDate.trim())) {
				dtJoiningDate = simpleDateFormat.parse(lStrJoiningDate.trim());
			}
			Date dtAppointmentDate = null;
			if (lStrAppointmentDate != null
					&& !"".equals(lStrAppointmentDate.trim())) {
				dtAppointmentDate = simpleDateFormat.parse(lStrAppointmentDate.trim());
			}

			if (lStrNameMarathi != null && !(lStrNameMarathi.equals(""))) {
				lObjEmpData.setName_marathi(lStrNameMarathi.trim());
			}
			if (lStrFatherHusband.equals("")) {
				lStrFatherHusband = "Not Provided";
			}
			lObjEmpData.setFather_or_husband(lStrFatherHusband.trim().toUpperCase());

			if (lStrSalutation != null && !(lStrSalutation.equals(""))) {
				lObjEmpData.setSalutation(lStrSalutation.trim());
			}

			if (lStrParentFldDept != null && !(lStrParentFldDept.equals(""))) {
				lObjEmpData.setParentDept(lStrParentFldDept.trim());
			}

			if (lStrReasonForChangeInPFD != null
					&& !(lStrReasonForChangeInPFD.equals(""))) {
				lObjEmpData.setReasonChangePFD(lStrReasonForChangeInPFD.trim());
			}

			if (lStrCadre != null && !(lStrCadre.equals("-1"))) {
				lObjEmpData.setCadre(lStrCadre.trim());
			}

			if (lStrGroup != null && !(lStrGroup.equals(""))) {
				lObjEmpData.setGroup(lStrGroup.trim());
			}
			if (lStrBasicPay != null && !(lStrBasicPay.equals(""))) {
				lObjEmpData.setBasicPay(Double.parseDouble(lStrBasicPay.trim()));
			}
			if (lStrCurrentOffice != null && !(lStrCurrentOffice.equals("-1"))) {
				lObjEmpData.setCurrOff(lStrCurrentOffice.trim());
			}

			if (lStrDesignation != null && !(lStrDesignation.equals("-1"))) {
				lObjEmpData.setDesignation(lStrDesignation.trim());
			}

			if (lStrPayCommission != null && !(lStrPayCommission.equals("-1"))) {
				lObjEmpData.setPayCommission(lStrPayCommission.trim());
			}

			// Code added to store Pay in pay band and grade pay in case of Sixth pay commission
			if(lStrPayCommission.equals("700016"))
			{
				String lStrPayInPayBand = StringUtility.getParameter("txtPayInPayBand", request).trim();
				String lStrGradePay = StringUtility.getParameter("txtGradePay", request).trim();
				if(!"".equals(lStrPayInPayBand))
				{
					lObjEmpData.setPayInPayBand(Long.valueOf(lStrPayInPayBand));
				}
				if(!"".equals(lStrGradePay))
				{
					lObjEmpData.setGradePay(Long.valueOf(lStrGradePay));
				}
			}

			// Sets Pay-scale to default 101 for Consolidated pay-commission

			if(lStrPayCommission.equals("700337"))
			{
				lObjEmpData.setPayScale("101");
			}
			else
			{
				if (lStrPayScale != null && !(lStrPayScale.equals("-1"))) {
					lObjEmpData.setPayScale(lStrPayScale.trim());
				}
			}

			if (lStrBuilding != null && !(lStrBuilding.equals(""))) {
				lObjEmpData.setBuilding_address(lStrBuilding.trim());
			}

			if (lStrStreet != null && !(lStrStreet.equals(""))) {
				lObjEmpData.setBuilding_street(lStrStreet.trim());
			}

			if (lStrLandmark != null && !(lStrLandmark.equals(""))) {
				lObjEmpData.setLandmark(lStrLandmark.trim());
			}
			if (lStrLocality != null && !(lStrLocality.equals(""))) {
				lObjEmpData.setLocality(lStrLocality.trim());
			}
			if (lStrDistrict != null && !(lStrDistrict.equals(""))) {
				lObjEmpData.setDistrict(lStrDistrict.trim());
			}
			if (lStrState != null && !(lStrState.equals(""))) {
				lObjEmpData.setState(lStrState.trim());
			}
			if (lStrFirstDesignation != null
					&& !(lStrFirstDesignation.equals(""))) {
				lObjEmpData.setFirstDesignation(lStrFirstDesignation.trim());
			}

			if (lStrPinCode != null && !(lStrPinCode.equals(""))) {
				lObjEmpData.setPincode(Long.parseLong(lStrPinCode.trim()));
			}
			if (lStrEmailId != null && !(lStrEmailId.equals(""))) {
				lObjEmpData.setEmailId(lStrEmailId.trim());
			}

			if (lStrCellNo != null && !(lStrCellNo.equals(""))) {
				lObjEmpData.setCellNo(lStrCellNo.trim());
			}
			
			//added by arpan
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
				if (pLStrBuilding != null && !(pLStrBuilding.equals(""))) {
					lObjEmpData.setPBuildingAddress(pLStrBuilding.trim());
				}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPBuildingAddress(lStrBuilding.trim());

			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrStreet != null && !(pLStrStreet.equals(""))) {
				lObjEmpData.setPBuildingStreet(pLStrStreet.trim());
			}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPBuildingStreet(lStrStreet.trim());

			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrLandmark != null && !(pLStrLandmark.equals(""))) {
				lObjEmpData.setPLandmark(pLStrLandmark.trim());
			}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPLandmark(lStrLandmark.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrLocality != null && !(pLStrLocality.equals(""))) {
				lObjEmpData.setPLocality(pLStrLocality.trim());
			}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPLocality(lStrLocality.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
				if (pLStrDistrict != null && !(pLStrDistrict.equals(""))) {
				lObjEmpData.setPDistrict(pLStrDistrict.trim());
				}
			}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPDistrict(lStrDistrict.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
				if (pLStrState != null && !(pLStrState.equals(""))) {
				lObjEmpData.setPState(pLStrState.trim());
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPState(lStrState.trim());

			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrPinCode != null && !(pLStrPinCode.equals(""))) {
				lObjEmpData.setPPincode(Long.parseLong(pLStrPinCode.trim()));
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPPincode(Long.parseLong(lStrPinCode.trim()));

			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
				if (pLStrCellNo != null && !(pLStrCellNo.equals(""))){ 
					lObjEmpData.setPCellNo(pLStrCellNo.trim());
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPCellNo(lStrCellNo.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrContactTelNo != null && !(pLStrContactTelNo.equals(""))) {
				lObjEmpData.setPCntctNo(pLStrContactTelNo.trim());
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPCntctNo(lStrContactTelNo.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("N"))
			{
			if (pLStrAddressVTC != null && !(pLStrAddressVTC.equals(""))) {
				lObjEmpData.setPAddressVTC(pLStrAddressVTC.trim());
			}}
			else if(isAddressSame!=null && !(isAddressSame.equals("")) && isAddressSame.equals("Y"))
				lObjEmpData.setPAddressVTC(lStrAddressVTC.trim());
			
			if(isAddressSame!=null && !(isAddressSame.equals("")))
			{
				lObjEmpData.setIsAddressSame(isAddressSame.trim().toUpperCase());
			}
				
			//end

			if (lStrContactTelNo != null && !(lStrContactTelNo.equals(""))) {
				lObjEmpData.setCntctNo(lStrContactTelNo.trim());
			}

			if (lStrRemarks != null && !(lStrRemarks.equals(""))) {
				lObjEmpData.setRemarks(lStrRemarks.trim());
			}

			if (lStrEIDNo != null && !(lStrEIDNo.equals(""))) {
				lObjEmpData.setEIDNo(lStrEIDNo.trim());
			}

			if (lStrUIDNo != null && !(lStrUIDNo.equals(""))) {
				lObjEmpData.setUIDNo(lStrUIDNo.trim());
			}

			if (lStrPANNo != null && !(lStrPANNo.equals(""))) {
				lObjEmpData.setPANNo(lStrPANNo.trim());
			}

			if (lStrBankName != null && !(lStrBankName.equals(""))) {
				lObjEmpData.setBankName(lStrBankName.trim());
			}

			if (lStrBranchName != null && !(lStrBranchName.equals(""))) {
				lObjEmpData.setBranchName(lStrBranchName.trim());
			}

			if (lStrIFSCCode != null && !(lStrIFSCCode.equals(""))) {
				lObjEmpData.setIFSCCode(lStrIFSCCode.trim());
			}

			if (lStrBankAccountNo != null && !(lStrBankAccountNo.equals(""))) {
				lObjEmpData.setBankAccountNo(lStrBankAccountNo.trim());
			}

			if (!"".equals(lStrDcpsOrGPF.trim())) {
				lObjEmpData.setDcpsOrGpf(lStrDcpsOrGPF.trim().toCharArray()[0]);
			}

			if (!"".equals(lStrDcpsAcMntndBy.trim())
					&& !(lStrDcpsAcMntndBy.equals("-1"))) {
				lObjEmpData.setAcDcpsMaintainedBy(lStrDcpsAcMntndBy.trim());
			} else {
				lObjEmpData.setAcDcpsMaintainedBy(null);
			}

			if (!"".equals(lStrAcNoNonSRKAEmp.trim())) {
				lObjEmpData.setAcNonSRKAEmp(lStrAcNoNonSRKAEmp.trim());
			}

			if (!"".equals(lStrAcMntndByOthers.trim())) {
				lObjEmpData.setAcMntndByOthers(lStrAcMntndByOthers.trim());
			}

			if(!"".equals(lStrAcntMntndByOtherState))
			{
				lObjEmpData.setAcMntndByOtherState(Long.valueOf(lStrAcntMntndByOtherState));
			}

			if (!"".equals(lStrAddressVTC.trim())) {
				lObjEmpData.setAddressVTC(lStrAddressVTC.trim());
			}

			if(!"".equals(lStrBuckleNo))
			{
				lObjEmpData.setBuckleNo(lStrBuckleNo);
			}

			if(!"".equals(lStrBioMetricNo))
			{
				lObjEmpData.setBioMetricNo(lStrBioMetricNo);
			}

			//added by shailesh for All India Services
			String lStrAcNoMntndByAllIndiaServices = StringUtility.getParameter(
					"txtAcNoMntndByAllIndiaServices", request).trim();
			if(!lStrAcNoMntndByAllIndiaServices.trim().equals("")){
				lObjEmpData.setPranNo(lStrAcNoMntndByAllIndiaServices);
			}
			//end by shailesh
			
			lObjEmpData.setName(lStrEmpName.toUpperCase());
			lObjEmpData.setGender(lStrGender.trim().toUpperCase().charAt(0));
			lObjEmpData.setDob(dtBirthDate);
			lObjEmpData.setSuperAnndate(dtSuperAnnDate);
			lObjEmpData.setServEndDate(dtSuperAnnDate);
			lObjEmpData.setDoj(dtJoiningDate);
			lObjEmpData.setDdoCode(lStrDDOCode.trim());
			lObjEmpData.setPhyRcvdFormStatus(null);
			lObjEmpData.setEmpOnDeptn(0);

			lObjEmpData.setAppointmentDate(dtAppointmentDate);
			lObjEmpData.setRegStatusUpdtdDate(gDtCurrDt);
			lObjEmpData.setPfdChangedBySRKA(0l);

			if (intSaveOrUpdateFlag == 1) {
				lObjEmpData.setRegStatus(lLongRegStatus);
			}

			if (intSaveOrUpdateFlag == 1) {
				lObjEmpData.setFormStatus(0l);
			}

			lObjEmpData.setDbId(lLngDbId);
			lObjEmpData.setLangId(lLngLangId);
			lObjEmpData.setLocId(lLngLocId);
			if (intSaveOrUpdateFlag == 1) {
				lObjEmpData.setCreatedPostId(gLngPostId);
				lObjEmpData.setCreatedUserId(gLngUserId);
				lObjEmpData.setCreatedDate(gDtCurrDt);
			}
			if (intSaveOrUpdateFlag == 2) {

				lObjEmpData.setUpdatedPostId(gLngPostId);
				lObjEmpData.setUpdatedUserId(gLngUserId);
				lObjEmpData.setUpdatedDate(gDtCurrDt);
			}
			// END
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lObjEmpData;
	}

	private RltDcpsPayrollEmp generateEmpPayrollData(Map inputMap)
	throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap
		.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) inputMap
		.get("serviceLocator");
		NewRegDdoDAO dcpsRegisDAO = new NewRegDdoDAOImpl(
				RltDcpsPayrollEmp.class, servLoc.getSessionFactory());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String lStrPhyChallenged = StringUtility.getParameter("radioHandic",
				request).trim();
		String lStrCurrentPostId = StringUtility.getParameter("cmbCurrentPost",
				request).trim();
		System.out.println("lStrCurrentPostId "+lStrCurrentPostId);
		RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = null;
		Boolean PayrollIdExistsOrNot = false;
		Long lLngEmpId = null;

		String lStrDcpsOrGPF = StringUtility.getParameter("radioDCPS", request).trim();

		String saveOrUpdateFlag = StringUtility.getParameter(
				"saveOrUpdateFlag", request).trim();
		Integer intSaveOrUpdateFlag = Integer.parseInt(saveOrUpdateFlag);

		if (intSaveOrUpdateFlag == 1) {
			if (lObjRltDcpsPayrollEmp == null) {
				lObjRltDcpsPayrollEmp = new RltDcpsPayrollEmp();
			}
		}

		if (intSaveOrUpdateFlag == 2) {

			lLngEmpId = Long.parseLong(StringUtility.getParameter("empId",
					request).trim());

			if (dcpsRegisDAO.checkDcpsEmpPayrollIdForEmpIdExists(lLngEmpId)) {

				Long lLngDcpsPayrollId = dcpsRegisDAO
				.getDcpsEmpPayrollIdForEmpId(lLngEmpId);
				lObjRltDcpsPayrollEmp = (RltDcpsPayrollEmp) dcpsRegisDAO
				.read(lLngDcpsPayrollId);
				PayrollIdExistsOrNot = true;

			}
		}

		inputMap.put("lLngEmpId", lLngEmpId);
		inputMap.put("PayrollIdExistsOrNot", PayrollIdExistsOrNot);

		// Get the audit purpose details from session helper
		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Long lLngDbId = SessionHelper.getDbId(inputMap);
		Long lLngLocId = SessionHelper.getLocationId(inputMap);
		Long lLngLangId = SessionHelper.getLangId(inputMap);
		Long lLngCurrentPostId = null;
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();

		String lStrJoinPostDate = StringUtility.getParameter("txtJoinPostDate",
				request).trim();
		String lStrJoinCadreDate = StringUtility.getParameter(
				"txtJoinCadreDate", request).trim();
		String lStrAcMaintainedBy = StringUtility.getParameter(
				"cmbAcMaintainedBy", request).trim();
		String lStrPFSeries = StringUtility
		.getParameter("cmbPFSeries", request).trim();
		String lStrPfSeriesDesc = StringUtility.getParameter("txtPFSeriesDesc",
				request).trim();
		String lStrPfAccountNo = StringUtility.getParameter("txtPfAccountNo",
				request).trim();

		/* GIS Details */
		String lStrGisApplicable = StringUtility.getParameter(
				"cmbGisApplicable", request).trim();
		String lStrGisGroup = StringUtility
		.getParameter("cmbGisGroup", request).trim();
		String lStrMembershipDate = StringUtility.getParameter(
				"txtMembershipDate", request).trim();
		Date lDtMembershipDate = null;
		if (lStrMembershipDate != null && !"".equals(lStrMembershipDate.trim())) {
			lDtMembershipDate = simpleDateFormat.parse(lStrMembershipDate.trim());
		}

		lObjRltDcpsPayrollEmp.setGisApplicable(Long
				.parseLong(lStrGisApplicable.trim()));
		lObjRltDcpsPayrollEmp.setGisGroup(Long.parseLong(lStrGisGroup.trim()));
		lObjRltDcpsPayrollEmp.setMembershipDate(lDtMembershipDate);

		// Code changed to hide the issue of mandatory GPF number in payroll
		/*
		if (lStrPfAccountNo.equals("")) {
			lStrPfAccountNo = "123";
		}
		 */

		String lStrPostId = StringUtility.getParameter("cmbCurrentPost",
				request).trim();

		Date dtJoinPostDate = null;
		if (lStrJoinPostDate != null && !"".equals(lStrJoinPostDate.trim())) {
			dtJoinPostDate = simpleDateFormat.parse(lStrJoinPostDate.trim());
		}
		if (lStrPhyChallenged != null && !"".equals(lStrPhyChallenged.trim())) {
			lObjRltDcpsPayrollEmp.setPhychallanged(lStrPhyChallenged.trim());

		}

		if (lStrCurrentPostId != null && !"".equals(lStrCurrentPostId.trim())) {
			lLngCurrentPostId = Long.parseLong(lStrCurrentPostId.trim());
			lObjRltDcpsPayrollEmp.setPostId(lLngCurrentPostId);
		}
		Date dtJoinCadreDate = null;
		if (lStrJoinCadreDate != null && !"".equals(lStrJoinCadreDate.trim())) {
			dtJoinCadreDate = simpleDateFormat.parse(lStrJoinCadreDate.trim());
		}

		if (dtJoinPostDate != null && !(dtJoinPostDate.equals(""))) {
			lObjRltDcpsPayrollEmp.setCurrPostJoiningDate(dtJoinPostDate);
		}

		if (dtJoinCadreDate != null && !(dtJoinCadreDate.equals(""))) {
			lObjRltDcpsPayrollEmp.setCurrCadreJoiningDate(dtJoinCadreDate);
		}

		if (lStrDcpsOrGPF.trim().equals("N")) {

			if (lStrAcMaintainedBy != null
					&& !(lStrAcMaintainedBy.equals("-1"))) {
				lObjRltDcpsPayrollEmp.setAcMaintainedBy(lStrAcMaintainedBy.trim());
			}

			if (lStrPFSeries != null && !(lStrPFSeries.equals("")) && !(lStrPFSeries.equals("-1"))) {
				lObjRltDcpsPayrollEmp.setPfSeries(lStrPFSeries.trim());
			} else {
				lObjRltDcpsPayrollEmp.setPfSeries(null);
			}

			if (lStrPfSeriesDesc != null && !(lStrPfSeriesDesc.trim().equals(""))) {
				lObjRltDcpsPayrollEmp.setPfSeriesDesc(lStrPfSeriesDesc.trim());
			}

			if (lStrPfAccountNo != null && !(lStrPfAccountNo.equals(""))) {
				lObjRltDcpsPayrollEmp.setPfAcNo(lStrPfAccountNo.trim());
			}
		} else {
			lObjRltDcpsPayrollEmp.setAcMaintainedBy(null);
			lObjRltDcpsPayrollEmp.setPfSeries(null);
			// PF Series Description is stored as "DCPS" for a DCPS Employee as suggested by the Payroll team.
			lObjRltDcpsPayrollEmp.setPfSeriesDesc("DCPS");
			lObjRltDcpsPayrollEmp.setPfAcNo(null);
		}

		if (lStrPostId != null && !lStrPostId.equalsIgnoreCase("")) {
			lObjRltDcpsPayrollEmp.setPostId(Long.parseLong(lStrPostId));
		}

		lObjRltDcpsPayrollEmp.setDbId(lLngDbId);
		lObjRltDcpsPayrollEmp.setLangId(lLngLangId);
		lObjRltDcpsPayrollEmp.setLocId(lLngLocId);
		if (!PayrollIdExistsOrNot) {
			lObjRltDcpsPayrollEmp.setCreatedPostId(gLngPostId);
			lObjRltDcpsPayrollEmp.setCreatedUserId(gLngUserId);
			lObjRltDcpsPayrollEmp.setCreatedDate(gDtCurrDt);
		} else {
			lObjRltDcpsPayrollEmp.setUpdatedPostId(gLngPostId);
			lObjRltDcpsPayrollEmp.setUpdatedUserId(gLngUserId);
			lObjRltDcpsPayrollEmp.setUpdatedDate(gDtCurrDt);
		}

		String lStrPhyPTApplicable = StringUtility.getParameter("hidPTApplicableForPhyHandi",
				request).trim();
		if(lStrPhyPTApplicable != null && !"".equals(lStrPhyPTApplicable))
		{
			lObjRltDcpsPayrollEmp.setPhyPTApplicable(lStrPhyPTApplicable.trim().toUpperCase().charAt(0));
		}

		return lObjRltDcpsPayrollEmp;
	}
}
