/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 28, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAO;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.HstReEmploymentDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerNomineeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnCvpRestorationDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCaseMvmnt;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCorrectionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCutDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalPensionDtls;


/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Apr 28, 2011
 */
public class PhysicalCaseInwardVOGeneratorImpl extends ServiceImpl implements PhysicalCaseInwardVOGenerator {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for EmpId */
	Long gLngEmpId = null;

	/* Global Variable for Location Id */
	String gStrLocId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	Log gLogger = LogFactory.getLog(getClass());

	Date gCurrDate = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	List<TrnPensionCorrectionDtls> gObjCorrectionDtls = new ArrayList<TrnPensionCorrectionDtls>();
	List gLstFieldTypeListToDelete = new ArrayList();
	Integer gIntApprovalLevel = 0;
	
	Map<String,Long> gMapArrearDtlsId = new HashMap<String,Long>();

	// Sets session information in the global variables
	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gCurrDate = DBUtility.getCurrentDateFromDB();
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#generateMap
	 * (java.util.Map)
	 */
	public ResultObject generateMap(Map inputMap) {

		gLogger.info("In generateMap of PhysicalCaseInwardVOGeneratorImpl........");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			MstPensionerHdr lObjMstPensionerHdrVO = new MstPensionerHdr();
			TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = new TrnPensionRqstHdr();
			MstPensionerDtls lObjMstPensionerDtlsVO = new MstPensionerDtls();
			List<TrnProvisionalPensionDtls> lLstTrnProvisionalPensionDtlsVO = new ArrayList<TrnProvisionalPensionDtls>();
			// List<TrnProvisionalVoucherDtls> lLstTrnProvisionalVoucherDtlsVO =
			// new ArrayList<TrnProvisionalVoucherDtls>();
			List<MstPensionerFamilyDtls> lLstMstPensionerFamilyDtlsVO = new ArrayList<MstPensionerFamilyDtls>();
			List<MstPensionerNomineeDtls> lLstMstPensionerNomineeDtlsVO = new ArrayList<MstPensionerNomineeDtls>();
			List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsVO = new ArrayList<TrnPensionRecoveryDtls>();
			List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtlsVO = new ArrayList<TrnCvpRestorationDtls>();
			List<HstReEmploymentDtls> lLstHstReEmploymentDtlsVO = new ArrayList<HstReEmploymentDtls>();
	//		List<HstCommutationDtls> lLstHstCommutationDtlsVO = new ArrayList<HstCommutationDtls>();
	//		List<HstPnsnPmntDcrgDtls> lLstHstPnsnPmntDcrgDtlsVO = new ArrayList<HstPnsnPmntDcrgDtls>();
			List<TrnPensionCaseMvmnt> lLstTrnPensionCaseMvmntVO = new ArrayList<TrnPensionCaseMvmnt>();
			List<TrnPensionCutDtls> lLstTrnPensionCutDtlsVO = new ArrayList<TrnPensionCutDtls>();
			List<TrnPensionArrearDtls> lLstTrnPensionArrearDtlsVO = new ArrayList<TrnPensionArrearDtls>();
			String lStrCaseStatus = null;
			String lStrCorrectionFlag = "N";
			boolean lValidPpoFlag = true;
			StringBuilder lStrBldXML = new StringBuilder();
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			String lStrPensionerCode = null;
			String lStrPpoNo = ((StringUtility.getParameter("txtPpoNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtPpoNo", request).trim() : null;
			if (!StringUtility.getParameter("PensionerId", request).equalsIgnoreCase("")) {
				lStrPensionerCode = StringUtility.getParameter("PensionerId", request);
				inputMap.put("Mode", "Update");
				lObjMstPensionerHdrVO = lObjPhysicalCaseInwardDAO.getMstPensionerHdrVO(lStrPensionerCode);
				inputMap.put("lObjMstPensionerHdrVO", lObjMstPensionerHdrVO);
				inputMap.put("PensionerCode", lObjMstPensionerHdrVO.getPensionerCode());

				lObjTrnPensionRqstHdrVO = lObjPhysicalCaseInwardDAO.getTrnPensionRqstHdrVO(lStrPensionerCode);
				inputMap.put("lObjTrnPensionRqstHdrVO", lObjTrnPensionRqstHdrVO);
				lStrCaseStatus = lObjTrnPensionRqstHdrVO.getCaseStatus();

				if(!(lStrPpoNo.toUpperCase()).equalsIgnoreCase(lObjTrnPensionRqstHdrVO.getPpoNo()))
				{
					inputMap.put("ppoNo", lStrPpoNo);
					objRes = serv.executeService("VALIDATION_OF_PPONO", inputMap);
					lValidPpoFlag = (Boolean) inputMap.get("ValidPpoFlag");
					lStrBldXML = (StringBuilder) inputMap.get("lStrBldXML");
				}
				if (gObjRsrcBndle.getString("STATFLG.APPROVED").equalsIgnoreCase(lStrCaseStatus) || gObjRsrcBndle.getString("STATFLG.REJECTED").equalsIgnoreCase(lStrCaseStatus)
						|| gObjRsrcBndle.getString("STATFLG.MODIFIED").equalsIgnoreCase(lStrCaseStatus) || gObjRsrcBndle.getString("STATFLG.REVISED").equalsIgnoreCase(lStrCaseStatus)
						|| gObjRsrcBndle.getString("STATFLG.MODIFIEDBYAUDITOR").equalsIgnoreCase(lStrCaseStatus)) {
					lStrCorrectionFlag = "Y";
				}
				inputMap.put("CorrectionFlag", lStrCorrectionFlag);
				lObjMstPensionerDtlsVO = lObjPhysicalCaseInwardDAO.getMstPensionerDtlsVO(lStrPensionerCode);
				inputMap.put("lObjMstPensionerDtlsVO", lObjMstPensionerDtlsVO);

			} else {
				
				inputMap.put("ppoNo", lStrPpoNo);
				objRes = serv.executeService("VALIDATION_OF_PPONO", inputMap);
				lValidPpoFlag = (Boolean) inputMap.get("ValidPpoFlag");
				lStrBldXML = (StringBuilder) inputMap.get("lStrBldXML");
				inputMap.put("Mode", "Add");
			}
			if(lValidPpoFlag)
			{
				lObjMstPensionerHdrVO = generateMstPensionerHdrVO(inputMap);
				lObjTrnPensionRqstHdrVO = generateTrnPensionRqstHdrVO(inputMap);
				lObjMstPensionerDtlsVO = generateMstPensionerDtlsVO(inputMap);
				lLstTrnProvisionalPensionDtlsVO = generateTrnProvisionalPensionDtlsVO(inputMap);
				// lLstTrnProvisionalVoucherDtlsVO =
				// generateTrnProvisionalVoucherDtlsVO(inputMap);
				lLstMstPensionerFamilyDtlsVO = generateMstPensionerFamilyDtlsVO(inputMap);
				lLstMstPensionerNomineeDtlsVO = generateMstPensionerNomineeDtlsVO(inputMap);
				lLstTrnPensionRecoveryDtlsVO = generateTrnPensionRecoveryDtlsVO(inputMap);
				lLstTrnCvpRestorationDtlsVO = generateTrnCvpRestorationDtlsVO(inputMap);
				lLstHstReEmploymentDtlsVO = generateHstReEmploymentDtlsVO(inputMap);
				//lLstHstCommutationDtlsVO = generateHstCommutationDtlsVO(inputMap);
			//	lLstHstPnsnPmntDcrgDtlsVO = generateHstPnsnPmntDcrgDtlsVO(inputMap);
				lLstTrnPensionCaseMvmntVO = generateTrnPensionCaseMvmntVO(inputMap);
				lLstTrnPensionCutDtlsVO = generateTrnPensionCutDtlsVO(inputMap);
				lLstTrnPensionArrearDtlsVO = generateTrnPensionArrearDtlsVO(inputMap);
	
				inputMap.put("MstPensionerHdrVO", lObjMstPensionerHdrVO);
				inputMap.put("TrnPensionRqstHdrVO", lObjTrnPensionRqstHdrVO);
				inputMap.put("MstPensionerDtlsVO", lObjMstPensionerDtlsVO);
				inputMap.put("lLstTrnProvisionalPensionDtlsVO", lLstTrnProvisionalPensionDtlsVO);
				// inputMap.put("lLstTrnProvisionalVoucherDtlsVO",
				// lLstTrnProvisionalVoucherDtlsVO);
				inputMap.put("lLstMstPensionerFamilyDtlsVO", lLstMstPensionerFamilyDtlsVO);
				inputMap.put("lLstMstPensionerNomineeDtlsVO", lLstMstPensionerNomineeDtlsVO);
				inputMap.put("lLstTrnPensionRecoveryDtlsVO", lLstTrnPensionRecoveryDtlsVO);
				inputMap.put("lLstTrnCvpRestorationDtlsVO", lLstTrnCvpRestorationDtlsVO);
				inputMap.put("lLstHstReEmploymentDtlsVO", lLstHstReEmploymentDtlsVO);
		//		inputMap.put("lLstHstCommutationDtlsVO", lLstHstCommutationDtlsVO);
		//		inputMap.put("lLstHstPnsnPmntDcrgDtlsVO", lLstHstPnsnPmntDcrgDtlsVO);
				inputMap.put("lLstTrnPensionCutDtlsVO", lLstTrnPensionCutDtlsVO);
				inputMap.put("lLstTrnPensionCaseMvmntVO", lLstTrnPensionCaseMvmntVO);
				inputMap.put("lLstTrnPensionArrearDtlsVO", lLstTrnPensionArrearDtlsVO);
				inputMap.put("PensionerCode", lObjMstPensionerHdrVO.getPensionerCode());
				inputMap.put("Status", lObjTrnPensionRqstHdrVO.getCaseStatus());
				inputMap.put("PpoNo", lObjTrnPensionRqstHdrVO.getPpoNo());
				inputMap.put("CorrectionDtlsList", gObjCorrectionDtls);
				inputMap.put("CorrectionFlag", lStrCorrectionFlag);
				inputMap.put("MapArrearDtlsId", gMapArrearDtlsId);
			}
			inputMap.put("ValidPpoFlag", lValidPpoFlag);
			inputMap.put("lStrBldXML",lStrBldXML);
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);

		}
		return objRes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateMstPensionerHdrVO(java.util.Map)
	 */
	public MstPensionerHdr generateMstPensionerHdrVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		MstPensionerHdr lObjMstPensionerHdrVO = null;
		MstPensionerHdr lObjPrvsMstPensionerHdrVO = new MstPensionerHdr();

		if (inputMap.containsKey("lObjMstPensionerHdrVO")) {
			lObjMstPensionerHdrVO = (MstPensionerHdr) inputMap.get("lObjMstPensionerHdrVO");
			lObjPrvsMstPensionerHdrVO = (MstPensionerHdr) lObjMstPensionerHdrVO.clone();
		} else {
			lObjMstPensionerHdrVO = new MstPensionerHdr();
		}
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {

			String lStrPnsrFileNo = null;
			String lStrApplnNO = null;
			String lStrPnsnrName = null;
			String lStrMiddleName = null;
			String lStrLastName = null;
			String lStrPensionerAddr = null;
			String lStrApenBRAddr = null;
			BigDecimal lBgDcmlStateCode = BigDecimal.ZERO;
			String lStrStateName = "";
			String lStrDistrictCode = "";
			String lStrDistrictName = "";
			String lStrPnsrAddr = null;
			String lStrTeleNo = null;
			Date lDtDateOfBirth = null;
			Date lDtDateOfJoining = null;
			Date lDtDateOfRetirement = null;
			Date lDtDateOfExpiry = null;
			String lStrAliveFlag = null;
			String lStrPanNo = null;
			String lStrPayScale = null;
			BigDecimal lBgDcmlLastPay = BigDecimal.ZERO;
			String lStrGender = null;
			String lStrOfficeAddr;
			BigDecimal lBgDcmlDeptCode = null;
			BigDecimal lBgDcmlHodCode = null;
			BigDecimal lBgDcmlAttachmentPhotoId = null;
			String lStrClassType = null;
			String lStrCadreType = null;
			String lStrDesignation = null;
			String lStrIdentityMark = null;
			String lStrHeight = null;
			String lStrPnsnrEmailId = null;
			String lStrOfficeEmailId = null;
			String lStrPnsnrPrefix = null;
			BigDecimal lBgDcmlPensionerPhotoId = null;
			String lStrOtherDesignation = null;
			BigDecimal lBgDcmlPensionerSignId = null;
			String lStrEmploymentOffice = null;

			// --------New Fields Added
			String lStrMoblileNo = null;
			Long lLngRetiringDepartment = null;
			String lStrPnsnrAddr1 = null;
			String lStrPnsnrAddr2 = null;
			String lStrPnsnrAddrTown = null;
			// String lStrPnsnrAddrLocality;
			Integer lIntPinCode = null;
			String lStrPnsnrNameInMarathi = null;
			String lStrPnsnrFatherName = null;
			String lStrGuardianName = null;
			String lStrGuardianFatherName = null;
			String lStrGuardianAddr1 = null;
			String lStrGuardianAddr2 = null;
			String lStrGuardianAddrTown = null;
			// String lStrGuardianAddrLocality;
			BigDecimal lBgDcmlGuardianAddrState = null;
			String lStrGuardianStateName = "";
			String lStrGuardianAddrDistrict = null;
			String lStrGuardianAddr = "";
			String lStrGuardianDistName = "";
			String lStrGuardianAddrPinCode = null;
			String lStrGuardianRelation = null;
			String lStrIsAddrSameFlag = null;
			String lStrDojAvailable = null;
			String lStrUidNo = null;
			String lStrEidNo = null;
			String lStrOtherPnsnrType = null;
			String lStrPnsnrAddr1InMarathi = null;
			String lStrPnsnrAddr2InMarathi = null;
			String lStrPnsnrAddrTownInMarathi = null;
			String lStrCorrectionFlag = null;
			String lStrMode = null;

			setSessionInfo(inputMap);

			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}

			lStrPnsnrName = ((StringUtility.getParameter("txtPnsnrName", request).trim()).length() > 0) ? StringUtility.getParameter("txtPnsnrName", request).trim() : null;

			lStrPnsnrNameInMarathi = ((StringUtility.getParameter("txtPnsnrNameInMarathi", request).trim()).length() > 0) ? StringUtility.getParameter("txtPnsnrNameInMarathi", request).trim() : null;

			lStrGender = ((StringUtility.getParameter("radioGender", request).trim()).length() > 0) ? StringUtility.getParameter("radioGender", request).trim().toUpperCase() : null;

			lDtDateOfBirth = ((StringUtility.getParameter("txtDateOfBirth", request).trim()).length() > 0)
					? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtDateOfBirth", request).trim())
					: null;
			lStrDojAvailable = StringUtility.getParameter("cmbDojAvailable", request);
			lDtDateOfJoining = ((StringUtility.getParameter("txtDateOfJoining", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtDateOfJoining", request)
					.trim()) : null;
			lDtDateOfRetirement = ((StringUtility.getParameter("txtDateOfRetirement", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtDateOfRetirement",
					request).trim()) : null;
			if (!StringUtility.getParameter("cmbAlive", request).equals("-1")) {
				lStrAliveFlag = StringUtility.getParameter("cmbAlive", request).trim();
			}
			lDtDateOfExpiry = ((StringUtility.getParameter("txtDateofExpiry", request).trim()).length() > 0) ? lObjSimpleDateFormat
					.parse(StringUtility.getParameter("txtDateofExpiry", request).trim()) : null;
			lStrIdentityMark = ((StringUtility.getParameter("txtIdentityMark", request).trim()).length() > 0) ? StringUtility.getParameter("txtIdentityMark", request).trim() : null;
			if (!StringUtility.getParameter("cmbCmsFtInch", request).equals("-1")) {
				lStrHeight = StringUtility.getParameter("cmbCmsFtInch", request).trim();
			}
			if (gObjRsrcBndle.getString("HEIGHT.CMS").equalsIgnoreCase(lStrHeight)) {
				lStrHeight = lStrHeight + "~" + (((StringUtility.getParameter("txtHeightInCms", request).trim()).length() > 0) ? StringUtility.getParameter("txtHeightInCms", request).trim() : "");
			}
			if (gObjRsrcBndle.getString("HEIGHT.FTINCHES").equalsIgnoreCase(lStrHeight)) {
				lStrHeight = lStrHeight + "~" + (((StringUtility.getParameter("cmbHeightInFt", request).trim()).length() > 0) ? StringUtility.getParameter("cmbHeightInFt", request).trim() : "") + "~"
						+ (((StringUtility.getParameter("cmbHeightInInch", request).trim()).length() > 0) ? StringUtility.getParameter("cmbHeightInInch", request).trim() : "");
			}
			// lStrHeight = ((StringUtility.getParameter("txtHeightInCms",
			// request).trim()).length() > 0) ?
			// StringUtility.getParameter("txtHeightInCms", request).trim() :
			// null;
			lStrPanNo = ((StringUtility.getParameter("txtPanNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtPanNo", request).trim().toUpperCase() : null;
			lStrPnsnrEmailId = ((StringUtility.getParameter("txtPnsnrEmailId", request).trim()).length() > 0) ? StringUtility.getParameter("txtPnsnrEmailId", request).trim() : null;
			lStrDesignation = ((StringUtility.getParameter("txtDesignation", request).trim()).length() > 0) ? StringUtility.getParameter("txtDesignation", request).trim() : null;
			// if (!StringUtility.getParameter("cmbDesignation",
			// request).equals("-1")) {
			// lStrDesignation = StringUtility.getParameter("cmbDesignation",
			// request).trim();
			// }
			lStrEmploymentOffice = ((StringUtility.getParameter("txtRetiringOffice", request).trim()).length() > 0) ? StringUtility.getParameter("txtRetiringOffice", request).trim() : null;
			if (!StringUtility.getParameter("cmbRetiringDepartment", request).equals("-1")) {
				lLngRetiringDepartment = Long.parseLong(StringUtility.getParameter("cmbRetiringDepartment", request).trim());
			}
			if (!StringUtility.getParameter("cmbPensionerType", request).equals("-1")) {
				lStrClassType = StringUtility.getParameter("cmbPensionerType", request).trim();
			}
			lStrPnsnrAddr1 = ((StringUtility.getParameter("txtPnsnrAddr1", request).trim()).length() > 0) ? StringUtility.getParameter("txtPnsnrAddr1", request).trim() : "";
			lStrTeleNo = ((StringUtility.getParameter("txtLandLineNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtLandLineNo", request).trim() : null;
			lStrPnsnrAddr2 = ((StringUtility.getParameter("txtPnsnrAddr2", request).trim()).length() > 0) ? StringUtility.getParameter("txtPnsnrAddr2", request).trim() : "";
			lStrMoblileNo = ((StringUtility.getParameter("txtMobileNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtMobileNo", request).trim() : null;
			lStrPnsnrAddrTown = ((StringUtility.getParameter("txtPnsnrAddrTown", request).trim()).length() > 0) ? StringUtility.getParameter("txtPnsnrAddrTown", request).trim() : "";
			
			lStrPnsnrAddr1InMarathi = ((StringUtility.getParameter("txtPnsnrAddr1InMarathi", request).trim()).length() > 0) ? StringUtility.getParameter("txtPnsnrAddr1InMarathi", request).trim() : "";
			lStrPnsnrAddr2InMarathi = ((StringUtility.getParameter("txtPnsnrAddr2InMarathi", request).trim()).length() > 0) ? StringUtility.getParameter("txtPnsnrAddr2InMarathi", request).trim() : "";
			lStrPnsnrAddrTownInMarathi = ((StringUtility.getParameter("txtPnsnrAddrTownMarathi", request).trim()).length() > 0) ? StringUtility.getParameter("txtPnsnrAddrTownMarathi", request).trim() : "";
			// lStrPnsnrAddrLocality =
			// ((StringUtility.getParameter("txtPnsnrAddrLocality",
			// request).trim()).length() > 0) ?
			// StringUtility.getParameter("txtPnsnrAddrLocality",
			// request).trim() : "";
			if (!StringUtility.getParameter("cmbPnsnrAddrState", request).equals("-1")) {
				lBgDcmlStateCode = new BigDecimal(StringUtility.getParameter("cmbPnsnrAddrState", request).trim());
				lStrStateName = StringUtility.getParameter("lPnsrStateName", request);
			}
			if (!StringUtility.getParameter("cmbPnsnrAddrDist", request).equals("-1")) {
				lStrDistrictCode = StringUtility.getParameter("cmbPnsnrAddrDist", request).trim();
				lStrDistrictName = StringUtility.getParameter("lPnsrDistName", request);
			}
			lIntPinCode = ((StringUtility.getParameter("txtPnsnrAddrPincode", request).trim()).length() > 0)
					? Integer.parseInt(StringUtility.getParameter("txtPnsnrAddrPincode", request).trim())
					: null;
			// lStrPnsrAddr = lStrPnsnrAddr1 +" "+ lStrPnsnrAddr2 +" "+
			// lStrPnsnrAddrTown +" "+ lStrDistrictName +" "+ lStrStateName
			// +" "+ lIntPinCode.toString();

			lStrPnsrAddr = ((lStrPnsnrAddr1.length() > 0) ? lStrPnsnrAddr1 + "," : "") + ((lStrPnsnrAddr2.length() > 0) ? lStrPnsnrAddr2 + "," : "")
					+ ((lStrPnsnrAddrTown.length() > 0) ? lStrPnsnrAddrTown + "," : "") + ((lStrDistrictName.length() > 0) ? lStrDistrictName + "," : "")
					+ ((lStrStateName.length() > 0) ? lStrStateName + "," : "") + ((lIntPinCode != null) ? lIntPinCode : "");

			lStrPnsnrFatherName = ((StringUtility.getParameter("txtFatherOrHusbandName", request).trim()).length() > 0) ? StringUtility.getParameter("txtFatherOrHusbandName", request).trim() : null;
			lStrGuardianName = ((StringUtility.getParameter("txtGuardianName", request).trim()).length() > 0) ? StringUtility.getParameter("txtGuardianName", request).trim() : null;
			lStrGuardianFatherName = ((StringUtility.getParameter("txtGuardianFHName", request).trim()).length() > 0) ? StringUtility.getParameter("txtGuardianFHName", request).trim() : null;
			lStrGuardianAddr1 = StringUtility.getParameter("txtGuardianAddr1", request).trim();

			lStrGuardianAddr2 = StringUtility.getParameter("txtGuardianAddr2", request).trim();
			lStrGuardianAddrTown = StringUtility.getParameter("txtGuardianAddrTown", request).trim();
			// lStrGuardianAddrLocality =
			// StringUtility.getParameter("txtGuardianAddrLocality",
			// request).trim();
			if (!StringUtility.getParameter("cmbGuardianAddrState", request).equals("-1")) {
				lBgDcmlGuardianAddrState = new BigDecimal(StringUtility.getParameter("cmbGuardianAddrState", request).trim());
				lStrGuardianStateName = StringUtility.getParameter("lGuardianStateName", request);
			}
			if (!StringUtility.getParameter("cmbGuardianAddrDist", request).equals("-1")) {
				lStrGuardianAddrDistrict = StringUtility.getParameter("cmbGuardianAddrDist", request).trim();
				lStrGuardianDistName = StringUtility.getParameter("lGuardianDistName", request);
			}
			lStrGuardianAddrPinCode = StringUtility.getParameter("txtGuardianAddrPincode", request).trim();
			lStrIsAddrSameFlag = ((StringUtility.getParameter("radioIsAddrSame", request).trim()).length() > 0) ? StringUtility.getParameter("radioIsAddrSame", request).trim() : null;
			lStrGuardianAddr = ((lStrGuardianAddr1.length() > 0) ? lStrGuardianAddr1 + "," : "")
					+ ((lStrGuardianAddr2.length() > 0) ? lStrGuardianAddr2 + "," : "")
					+ ((lStrGuardianAddrTown.length() > 0) ? lStrGuardianAddrTown + "," : "")
					// +((lStrGuardianAddrLocality.length() > 0) ?
					// lStrGuardianAddrLocality+"," : "")
					+ ((lStrGuardianDistName.length() > 0) ? lStrGuardianDistName + "," : "") + ((lStrGuardianStateName.length() > 0) ? lStrGuardianStateName + "," : "")
					+ ((lStrGuardianAddrPinCode.length() > 0) ? lStrGuardianAddrPinCode : "");

			if (!StringUtility.getParameter("cmbGuardianRelation", request).equals("-1")) {
				lStrGuardianRelation = StringUtility.getParameter("cmbGuardianRelation", request).trim();
			}
			lStrUidNo = ((!("").equals(StringUtility.getParameter("txtUidNo1", request))) ? StringUtility.getParameter("txtUidNo1", request) : "")
					+ ((!("").equals(StringUtility.getParameter("txtUidNo2", request))) ? StringUtility.getParameter("txtUidNo2", request) : "")
					+ ((!("").equals(StringUtility.getParameter("txtUidNo3", request))) ? StringUtility.getParameter("txtUidNo3", request) : "");

			lStrEidNo = ((StringUtility.getParameter("txtEidNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtEidNo", request).trim() : null;
			lStrOtherPnsnrType = ((StringUtility.getParameter("txtOtherPnsnrType", request).trim()).length() > 0) ? StringUtility.getParameter("txtOtherPnsnrType", request).trim() : null;

			lObjMstPensionerHdrVO.setPnsrFileNo(lStrPnsrFileNo);
			lObjMstPensionerHdrVO.setApplnNO(lStrApplnNO);

			lObjMstPensionerHdrVO.setLocationCode(gStrLocationCode);
			lObjMstPensionerHdrVO.setFirstName(lStrPnsnrName.toUpperCase());
			lObjMstPensionerHdrVO.setPnsnrNameInMarathi(lStrPnsnrNameInMarathi);
			lObjMstPensionerHdrVO.setGender(lStrGender);
			lObjMstPensionerHdrVO.setDateOfBirth(lDtDateOfBirth);
			lObjMstPensionerHdrVO.setDojAvailable(lStrDojAvailable);
			lObjMstPensionerHdrVO.setDateOfJoin(lDtDateOfJoining);
			lObjMstPensionerHdrVO.setDateOfRetirement(lDtDateOfRetirement);
			lObjMstPensionerHdrVO.setAliveFlag(lStrAliveFlag);
			lObjMstPensionerHdrVO.setDateOfDeath(lDtDateOfExpiry);
			lObjMstPensionerHdrVO.setIdentityMark(lStrIdentityMark);
			lObjMstPensionerHdrVO.setHeight(lStrHeight);
			lObjMstPensionerHdrVO.setPanNo(lStrPanNo);
			lObjMstPensionerHdrVO.setPnsnrEmailId(lStrPnsnrEmailId);
			lObjMstPensionerHdrVO.setDesignation(lStrDesignation);
			lObjMstPensionerHdrVO.setEmploymentOffice(lStrEmploymentOffice);
			lObjMstPensionerHdrVO.setRetiringDepartment(lLngRetiringDepartment);
			lObjMstPensionerHdrVO.setClassType(lStrClassType);
			lObjMstPensionerHdrVO.setPnsnrAddr1(lStrPnsnrAddr1);
			lObjMstPensionerHdrVO.setTeleNo(lStrTeleNo);
			lObjMstPensionerHdrVO.setPnsnrAddr2(lStrPnsnrAddr2);
			lObjMstPensionerHdrVO.setMoblileNo(lStrMoblileNo);
			lObjMstPensionerHdrVO.setPnsnrAddrTown(lStrPnsnrAddrTown);
			// lObjMstPensionerHdrVO.setPnsnrAddrLocality(lStrPnsnrAddrLocality);
			lObjMstPensionerHdrVO.setStateCode(lBgDcmlStateCode);
			lObjMstPensionerHdrVO.setDistrictCode(lStrDistrictCode);
			lObjMstPensionerHdrVO.setPinCode(lIntPinCode);
			lObjMstPensionerHdrVO.setPensionerAddr(lStrPnsrAddr);
			lObjMstPensionerHdrVO.setPnsnrAddr1Marathi(lStrPnsnrAddr1InMarathi);
			lObjMstPensionerHdrVO.setPnsnrAddr2Marathi(lStrPnsnrAddr2InMarathi);
			lObjMstPensionerHdrVO.setPnsnrAddrTownMarathi(lStrPnsnrAddrTownInMarathi);
			lObjMstPensionerHdrVO.setPnsnrFatherName(lStrPnsnrFatherName);
			lObjMstPensionerHdrVO.setGuardianName(lStrGuardianName);
			lObjMstPensionerHdrVO.setGuardianFatherName(lStrGuardianFatherName);
			lObjMstPensionerHdrVO.setGuardianAddr1(lStrGuardianAddr1);
			lObjMstPensionerHdrVO.setGuardianAddr2(lStrGuardianAddr2);
			lObjMstPensionerHdrVO.setGuardianAddrTown(lStrGuardianAddrTown);
			// lObjMstPensionerHdrVO.setGuardianAddrLocality(lStrGuardianAddrLocality);
			lObjMstPensionerHdrVO.setGuardianAddrState(lBgDcmlGuardianAddrState);
			lObjMstPensionerHdrVO.setGuardianAddrDistrict(lStrGuardianAddrDistrict);
			if (!"".equals(lStrGuardianAddrPinCode)) {
				lObjMstPensionerHdrVO.setGuardianAddrPinCode(Integer.parseInt(lStrGuardianAddrPinCode));
			}
			lObjMstPensionerHdrVO.setGuardianRelation(lStrGuardianRelation);
			lObjMstPensionerHdrVO.setGuardianAddr(lStrGuardianAddr);
			lObjMstPensionerHdrVO.setIsAddrSameFlag(lStrIsAddrSameFlag);
			lObjMstPensionerHdrVO.setUidNo(lStrUidNo);
			lObjMstPensionerHdrVO.setEidNo(lStrEidNo);
			lObjMstPensionerHdrVO.setOtherPnsnrType(lStrOtherPnsnrType);
			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjMstPensionerHdrVO.setTrnCounter(1);
				lObjMstPensionerHdrVO.setCreatedUserId(new BigDecimal(gLngUserId));
				lObjMstPensionerHdrVO.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjMstPensionerHdrVO.setCreatedDate(new Date());
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lObjMstPensionerHdrVO.setUpdatedDate(new Date());
				lObjMstPensionerHdrVO.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjMstPensionerHdrVO.setUpdatedUserId(new BigDecimal(gLngUserId));
				if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getFirstName(), lObjMstPensionerHdrVO.getFirstName())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getFirstName(), lObjMstPensionerHdrVO.getFirstName(), "Full Name of Pensioner", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrNameInMarathi(), lObjMstPensionerHdrVO.getPnsnrNameInMarathi())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPnsnrNameInMarathi(), lObjMstPensionerHdrVO.getPnsnrNameInMarathi(), "Pensioner Name in marathi", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGender(), lObjMstPensionerHdrVO.getGender())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGender(), lObjMstPensionerHdrVO.getGender(), "Pensioner Gender", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsMstPensionerHdrVO.getDateOfBirth(), lObjMstPensionerHdrVO.getDateOfBirth())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsMstPensionerHdrVO.getDateOfBirth(), lObjMstPensionerHdrVO.getDateOfBirth(), "Date of Birth", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getDojAvailable(), lObjMstPensionerHdrVO.getDojAvailable())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getDojAvailable(), lObjMstPensionerHdrVO.getDojAvailable(), "Date of Joining Available", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsMstPensionerHdrVO.getDateOfJoin(), lObjMstPensionerHdrVO.getDateOfJoin())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsMstPensionerHdrVO.getDateOfJoin(), lObjMstPensionerHdrVO.getDateOfJoin(), "Date of Joining", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsMstPensionerHdrVO.getDateOfRetirement(), lObjMstPensionerHdrVO.getDateOfRetirement())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsMstPensionerHdrVO.getDateOfRetirement(), lObjMstPensionerHdrVO.getDateOfRetirement(), "Date of Retirement", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getAliveFlag(), lObjMstPensionerHdrVO.getAliveFlag())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getAliveFlag(), lObjMstPensionerHdrVO.getAliveFlag(), "Alive", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsMstPensionerHdrVO.getDateOfDeath(), lObjMstPensionerHdrVO.getDateOfDeath())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsMstPensionerHdrVO.getDateOfDeath(), lObjMstPensionerHdrVO.getDateOfDeath(), "Date of Death", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getIdentityMark(), lObjMstPensionerHdrVO.getIdentityMark())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getIdentityMark(), lObjMstPensionerHdrVO.getIdentityMark(), "Identity Mark", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getHeight(), lObjMstPensionerHdrVO.getHeight())) {
						generateCorrectionDtlsVO(null, (lObjPrvsMstPensionerHdrVO.getHeight() == null ? "" : lObjPrvsMstPensionerHdrVO.getHeight().replaceAll("~", " ")),
								(lObjMstPensionerHdrVO.getHeight() == null ? "" : lObjMstPensionerHdrVO.getHeight().replaceAll("~", " ")), "Pensioner Height", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPanNo(), lObjMstPensionerHdrVO.getPanNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPanNo(), lObjMstPensionerHdrVO.getPanNo(), "Pan No", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrEmailId(), lObjMstPensionerHdrVO.getPnsnrEmailId())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPnsnrEmailId(), lObjMstPensionerHdrVO.getPnsnrEmailId(), "Pensioner Email Id", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getDesignation(), lObjMstPensionerHdrVO.getDesignation())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getDesignation(), lObjMstPensionerHdrVO.getDesignation(), "Designation", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getEmploymentOffice(), lObjMstPensionerHdrVO.getEmploymentOffice())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getEmploymentOffice(), lObjMstPensionerHdrVO.getEmploymentOffice(), "Employment Office", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getRetiringDepartment(), lObjMstPensionerHdrVO.getRetiringDepartment())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getRetiringDepartment(), lObjMstPensionerHdrVO.getRetiringDepartment(), "Retiring Department", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getClassType(), lObjMstPensionerHdrVO.getClassType())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getClassType(), lObjMstPensionerHdrVO.getClassType(), "Pensioner Type", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getOtherPnsnrType(), lObjMstPensionerHdrVO.getOtherPnsnrType())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getOtherPnsnrType(), lObjMstPensionerHdrVO.getOtherPnsnrType(), "Other Pensioner Type", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrAddr1(), lObjMstPensionerHdrVO.getPnsnrAddr1())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPnsnrAddr1(), lObjMstPensionerHdrVO.getPnsnrAddr1(), "Pensioner Residential Address 1", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getTeleNo(), lObjMstPensionerHdrVO.getTeleNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getTeleNo(), lObjMstPensionerHdrVO.getTeleNo(), "Pensioner Landline No", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrAddr2(), lObjMstPensionerHdrVO.getPnsnrAddr2())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPnsnrAddr2(), lObjMstPensionerHdrVO.getPnsnrAddr2(), "Pensioner Residential Address 2", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getMoblileNo(), lObjMstPensionerHdrVO.getMoblileNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getMoblileNo(), lObjMstPensionerHdrVO.getMoblileNo(), "Pensioner Mobile No", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrAddrTown(), lObjMstPensionerHdrVO.getPnsnrAddrTown())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPnsnrAddrTown(), lObjMstPensionerHdrVO.getPnsnrAddrTown(), "Pensioner Address Town/Village", 0);
					}
					// if (lObjPrvsMstPensionerHdrVO == null ||
					// getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrAddrLocality(),
					// lObjMstPensionerHdrVO.getPnsnrAddrLocality())) {
					// generateCorrectionDtlsVO(null,
					// lObjPrvsMstPensionerHdrVO.getPnsnrAddrLocality(),
					// lObjMstPensionerHdrVO.getPnsnrAddrLocality(),
					// "Pensioner Address Locality", 0);
					// }
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getStateCode(), lObjMstPensionerHdrVO.getStateCode())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getStateCode(), lObjMstPensionerHdrVO.getStateCode(), "Pensioner State", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getDistrictCode(), lObjMstPensionerHdrVO.getDistrictCode())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getDistrictCode(), lObjMstPensionerHdrVO.getDistrictCode(), "Pensioner District", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPinCode(), lObjMstPensionerHdrVO.getPinCode())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPinCode(), lObjMstPensionerHdrVO.getPinCode(), "Pensioner Pin Code", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrAddr1Marathi(), lObjMstPensionerHdrVO.getPnsnrAddr1Marathi())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPnsnrAddr1Marathi(), lObjMstPensionerHdrVO.getPnsnrAddr1Marathi(), "Pensioner Residential Address 1 in marathi", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrAddr2Marathi(), lObjMstPensionerHdrVO.getPnsnrAddr2Marathi())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPnsnrAddr2Marathi(), lObjMstPensionerHdrVO.getPnsnrAddr2Marathi(), "Pensioner Residential Address 2 in marathi", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrAddrTownMarathi(), lObjMstPensionerHdrVO.getPnsnrAddrTownMarathi())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPnsnrAddrTownMarathi(), lObjMstPensionerHdrVO.getPnsnrAddrTownMarathi(), "Pensioner Address Town/Village in marathi", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getPnsnrFatherName(), lObjMstPensionerHdrVO.getPnsnrFatherName())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getPnsnrFatherName(), lObjMstPensionerHdrVO.getPnsnrFatherName(), "Pensioner Father Name", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianName(), lObjMstPensionerHdrVO.getGuardianName())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGuardianName(), lObjMstPensionerHdrVO.getGuardianName(), "Guardian Name", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianFatherName(), lObjMstPensionerHdrVO.getGuardianFatherName())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGuardianFatherName(), lObjMstPensionerHdrVO.getGuardianFatherName(), "Guardian Father Name", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianAddr1(), lObjMstPensionerHdrVO.getGuardianAddr1())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGuardianAddr1(), lObjMstPensionerHdrVO.getGuardianAddr1(), "Guardian Residential Address 1", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianAddr2(), lObjMstPensionerHdrVO.getGuardianAddr2())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGuardianAddr2(), lObjMstPensionerHdrVO.getGuardianAddr2(), "Guardian Residential Address 2", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianAddrTown(), lObjMstPensionerHdrVO.getGuardianAddrTown())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGuardianAddrTown(), lObjMstPensionerHdrVO.getGuardianAddrTown(), "Guardian Address Town/Village", 0);
					}
					// if (lObjPrvsMstPensionerHdrVO == null ||
					// getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianAddrLocality(),
					// lObjMstPensionerHdrVO.getGuardianAddrLocality())) {
					// generateCorrectionDtlsVO(null,
					// lObjPrvsMstPensionerHdrVO.getGuardianAddrLocality(),
					// lObjMstPensionerHdrVO.getGuardianAddrLocality(),
					// "Guardian Address Locality", 0);
					// }
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianAddrState(), lObjMstPensionerHdrVO.getGuardianAddrState())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGuardianAddrState(), lObjMstPensionerHdrVO.getGuardianAddrState(), "Guardian State", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianAddrDistrict(), lObjMstPensionerHdrVO.getGuardianAddrDistrict())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGuardianAddrDistrict(), lObjMstPensionerHdrVO.getGuardianAddrDistrict(), "Guardian District", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianAddrPinCode(), lObjMstPensionerHdrVO.getGuardianAddrPinCode())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGuardianAddrPinCode(), lObjMstPensionerHdrVO.getGuardianAddrPinCode(), "Guardian Address Pincode", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getGuardianRelation(), lObjMstPensionerHdrVO.getGuardianRelation())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getGuardianRelation(), lObjMstPensionerHdrVO.getGuardianRelation(), "Guardian Relation", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getIsAddrSameFlag(), lObjMstPensionerHdrVO.getIsAddrSameFlag())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getIsAddrSameFlag(), lObjMstPensionerHdrVO.getIsAddrSameFlag(), "Guardian Address Same Flag", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getUidNo(), lObjMstPensionerHdrVO.getUidNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getUidNo(), lObjMstPensionerHdrVO.getUidNo(), "UID Number", 0);
					}
					if (lObjPrvsMstPensionerHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerHdrVO.getEidNo(), lObjMstPensionerHdrVO.getEidNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerHdrVO.getEidNo(), lObjMstPensionerHdrVO.getEidNo(), "EID Number", 0);
					}
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lObjMstPensionerHdrVO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateTrnPensionRqstHdrVO(java.util.Map)
	 */
	public TrnPensionRqstHdr generateTrnPensionRqstHdrVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = null;
		TrnPensionRqstHdr lObjPrvsTrnPensionRqstHdrVO = new TrnPensionRqstHdr();
		if (inputMap.containsKey("lObjTrnPensionRqstHdrVO")) {
			lObjTrnPensionRqstHdrVO = (TrnPensionRqstHdr) inputMap.get("lObjTrnPensionRqstHdrVO");
			lObjPrvsTrnPensionRqstHdrVO = (TrnPensionRqstHdr) lObjTrnPensionRqstHdrVO.clone();
		} else {
			lObjTrnPensionRqstHdrVO = new TrnPensionRqstHdr();
		}
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {

			Long lLngPensionRequestId = null;
			String lStrPensionerCode = null;
			String lStrPpoNo = null;
			Date lDtPpoDate = null;
			Date lDtPpoInwardDate = null;
			String lStrInwardMode = null;
			String lStrPensionType = null;
			String lStrSchemeType = null;
			String lStrStatus = null;
			String lStrRemark = null;
			BigDecimal lBgDcmlHeadCode = BigDecimal.ZERO;
			Date lDtCommencementDate = null;
			String lStrCvpOrderNo = null;
			BigDecimal lBgDcmlCvpPayableAmount = BigDecimal.ZERO;
			Date lDtCvpDate = null;
			String lStrDcrgOrderNo = null;
			BigDecimal lBgDcmlDcrgPayableAmount = BigDecimal.ZERO;
			Date lDtDcrgDate = null;
			Date lDtDcrgPaidDate = null;
			BigDecimal lBgDcmlPensionableAmount = BigDecimal.ZERO;
			BigDecimal lBgDcmlDppfAmount = BigDecimal.ZERO;
			BigDecimal lBgDcmlBasicPensionAmount = BigDecimal.ZERO;
			BigDecimal lBgDcmlDpPercent = BigDecimal.ZERO;
			BigDecimal lBgDcmlTiPercent = null;
			BigDecimal lBgDcmlCvpMonthlyAmount = BigDecimal.ZERO;
			BigDecimal lBgDcmlMedicalAllowanceAmount = BigDecimal.ZERO;
			Date lDtFp1Date = null;
			BigDecimal lBgDcmlFp1Amount = BigDecimal.ZERO;
			Date lDtFp2Date = null;
			BigDecimal lBgDcmlFp2Amount = BigDecimal.ZERO;
			String lStrTypeFlag = null;
			Date lDtCvpRestorationDate = null;
			String lStrApproveStatus = null;
			String lStrCorrectionFlag = null;
			BigDecimal lBgDcmlTotalRecovery = BigDecimal.ZERO;
			BigDecimal lBgDcmlPendingRecovery = BigDecimal.ZERO;
			Integer lIntTrnCounter = null;
			BigDecimal lBgDcmlDbId = null;
			String lStrLocationCode = null;
			BigDecimal lBgDcmlCaseOwner = null;
			String lStrAuditorFlag = null;
			String lStrTotalSrvc = null;
			String lStrDcrgPaidFlag = null;
			String lStrCvpPaidFlag = null;
			String lStrDeptOrdNo = null;
			BigDecimal lBgDcmlOrgBf11156 = BigDecimal.ZERO;
			BigDecimal lBgDcmlRedBf11156 = BigDecimal.ZERO;
			BigDecimal lBgDcmlOrgAf11156 = BigDecimal.ZERO; // after 01/11/1956
			BigDecimal lBgDcmlRedAf11156 = BigDecimal.ZERO;
			BigDecimal lBgDcmlOrgAf10560 = BigDecimal.ZERO; // after 01/05/1960
			BigDecimal lBgDcmlRedAf10560 = BigDecimal.ZERO;
			String lStrAuthority = null;
			String lStrSanctionLetterNo = null;
			BigDecimal lBgDcmlPersonalPension = BigDecimal.ZERO;
			BigDecimal lBgDcmlIr = BigDecimal.ZERO;
			Date lDtClosedDate = null;
			Date lDtEndDate = null;
			String lStrDPFlag = null;
			String lStrSecondPpoNo = null;
			String lStrForm22Issued = null;
			String lStrForm22IssuedAuth = null;
			Date lDtForm22IssuedDate = null;
			String lStrLpcIssued = null;
			String lStrLpcIssuedAuth = null;
			Date lDtLpcIssuedDate = null;
			BigDecimal lBgDcmlDaPercent = null;
			String lStrCaseRegisterNo = null;
			Integer lIntCurrCaseStatus = null;
			String lStrIsRop = null;
			String lStrCalcType = null;
			Date lDtCaseRegDate = null;
			BigDecimal lBgDcmlNewDaprcnt = null;
			String lStrSeenFlag = null;
			Date lDtAppliedDate = null;
			String lStrOmrType = null;
			String lStrCaseForMronly = null;
			BigDecimal lBgDcmlLastPayTransfer = BigDecimal.ZERO;
			String lStrTransferTreasury = null;
			String lStrRemarried = null;
			Date lDtRemarriedDate = null;
			String lStrCvpPayMode = null;
			String lStrDcrgPayMode = null;
			Date lDtLastPaidDate = null;
			BigDecimal lBgDcmlLastPaidAmount = BigDecimal.ZERO;
			String lStrLpcIssuedOtherauth = null;
			String lStrForm22IssuedOotherauth = null;
			String lStrProvPpoNo = null;
			Integer lIntCvpEffectiveMonth = null;
			Integer lIntApprovalLevel = null;
			Long lLngArrearAttacmentId = null;
			String lStrLinkedPpoNo = null;
			String lStrPageNo = null;
			String lStrLedgerNo = null;
			String lStrIsFp1datechange = null;
			String lStrFpDCRGPayMode = null;
			String lStrFpCvpPayMode = null;
			String lStrFpPensionPayMode = null;
			BigDecimal lBgDcmlOrgBf11136 = BigDecimal.ZERO; // before 01/04/1936
			BigDecimal lBgDcmlOrgAf11136 = BigDecimal.ZERO; // after 01/04/1936
			BigDecimal lBgDcmlOrgAfZp = BigDecimal.ZERO; // zilla parishad
			BigDecimal lBgDcmlRedBf11136 = BigDecimal.ZERO;
			BigDecimal lBgDcmlRedAf11136 = BigDecimal.ZERO;
			BigDecimal lBgDcmlRedAfZp = BigDecimal.ZERO;
			String lStrPpoAGOutwardNo = null;
			Date lDtPpoAGOutwardDate = null;
			BigDecimal lBgDcmlReducedPension = BigDecimal.ZERO;
			BigDecimal lBgDcmlAdpAmount = BigDecimal.ZERO;
			Date lDtCvpEffectiveDate = null;
			String lStrCaseReceivedFrom = null;
			String lStrCaseReceivedOffice = null;
			String lStrReEmploymentFlag = null;
			Date lDtReEmploymentFromDate = null;
			Date lDtReEmploymentToDate = null;
			String lStrDaInPensionSalary = null;
			BigDecimal lBgDcmlTotalCvpAmt = BigDecimal.ZERO;
			BigDecimal lBgDcmlTotalDcrgAmt = BigDecimal.ZERO;
			BigDecimal lBgDcmlPeonAllowanceAmount = BigDecimal.ZERO;
			BigDecimal lBgDcmlPensionCut = BigDecimal.ZERO;
			BigDecimal lBgDcmlOther1 = BigDecimal.ZERO;
			BigDecimal lBgDcmlOther2 = BigDecimal.ZERO;
			BigDecimal lBgDcmlOther3 = BigDecimal.ZERO;
			String lStrCvpVoucherNo = null;
			Date lDtCvpVoucherDate = null;
			String lStrBranchCode = "";
			Date lDtDateOfRetirement = null;
			// String lStrAllocIndicatorFlag=null;
			// --------New Fields Added
			String lStrStateCode = null;
			BigDecimal lBgDcmlArrearAmount = BigDecimal.ZERO;
			String lStrRecoveryFlag = null;
			String lStrROPType = null;
			String lStrShowCaseFor = null;
			String lStrMode = null;
			BigDecimal lBgDcmlDcrgAmntAftrWithHeld = BigDecimal.ZERO;
			String lStrDcrgVoucherNo = null;
			String lStrDcrgVoucherDate = null;
			String lStrDcrgWithHeldAmnt = null;
			String lStrDcrgPayingAuth = null;
			String lStrDcrgAmntAfterWithHeld = null;
			String lStrDcrgTotalRecoveryAmnt = null;
			BigDecimal lBgDcmlTotalAllocationAmt = BigDecimal.ZERO;
			BigDecimal lBgDcmlOrgBf10436Percent = BigDecimal.ZERO;
			BigDecimal lBgDcmlOrgAf10436Percent = BigDecimal.ZERO;
			BigDecimal lBgDcmlOrgAf11156Percent = BigDecimal.ZERO;
			BigDecimal lBgDcmlOrgAf10560Percent = BigDecimal.ZERO;
			BigDecimal lBgDcmlOrgZpAfPercent = BigDecimal.ZERO;
			String lStrFpAvailableFlag = null;
			String lStrPensionCutMonth = "";
			String lStrPensionCutYear = "";
			Integer lIntPensionCutMonthYear = 0;
			String lStrDcrgNameOfPayee = "";
			String lStrDcrgTrsryForPayment = "";
			String lStrCvpNameOfPayee = "";
			String lStrCvpTrsryForPayment = "";
			String lStrCvpAuthority = "";
			Date lDtCvpStartDate = null;
			Date lDtCvpEndDate = null;
			
			DecimalFormat lDecFormat = new DecimalFormat("#0.##"); 
			setSessionInfo(inputMap);
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}
			lStrStateCode = StringUtility.getParameter("cmbStateCode", request).trim();
			lStrPpoNo = ((StringUtility.getParameter("txtPpoNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtPpoNo", request).trim() : null;
			if (!StringUtility.getParameter("cmbPensionClass", request).equals("-1")) {
				lStrTypeFlag = StringUtility.getParameter("cmbPensionClass", request).trim();
			}
			if (!StringUtility.getParameter("cmbPensionType", request).equals("-1")) {
				lStrPensionType = StringUtility.getParameter("cmbPensionType", request).trim();
			}
			if (!StringUtility.getParameter("cmbPensionStatus", request).equals("-1")) {
				lStrStatus = StringUtility.getParameter("cmbPensionStatus", request).trim();
			}

			if (!StringUtility.getParameter("cmbCalculationType", request).equals("-1")) {
				lStrCalcType = StringUtility.getParameter("cmbCalculationType", request).trim();
			}
			lStrPpoAGOutwardNo = ((StringUtility.getParameter("txtPpoAGOutwNO", request).trim()).length() > 0) ? StringUtility.getParameter("txtPpoAGOutwNO", request).trim() : null;

			lDtPpoAGOutwardDate = ((StringUtility.getParameter("txtPpoAGOutwDate", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtPpoAGOutwDate", request)
					.trim()) : null;
			if (!StringUtility.getParameter("cmbHeadCode", request).equals("-1")) {
				lBgDcmlHeadCode = new BigDecimal(StringUtility.getParameter("cmbHeadCode", request).trim());
			}
			lDtCommencementDate = ((StringUtility.getParameter("txtDateOfCommencement", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtDateOfCommencement",
					request).trim()) : null;
			//lStrDPFlag = ((StringUtility.getParameter("radioDpMerge", request).trim()).length() > 0) ? StringUtility.getParameter("radioDpMerge", request).trim() : null;
			lBgDcmlBasicPensionAmount = ((StringUtility.getParameter("txtBasicPensionAmt", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtBasicPensionAmt", request)
					.trim()) : BigDecimal.ZERO;
			lBgDcmlDpPercent = ((StringUtility.getParameter("txtDpRate", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtDpRate", request).trim()) : null;
			// lBgDcmlAdpAmount = ((StringUtility.getParameter("txtAdpAmount",
			// request).trim()).length() > 0) ? new
			// BigDecimal(StringUtility.getParameter("txtAdpAmount",
			// request).trim()) : null;
			// lBgDcmlDaPercent = ((StringUtility.getParameter("txtDaRate",
			// request).trim()).length() > 0) ? new
			// BigDecimal(StringUtility.getParameter("txtDaRate",
			// request).trim()) : null;
			lBgDcmlCvpMonthlyAmount = ((StringUtility.getParameter("txtCvpMonthly", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtCvpMonthly", request).trim()) : null;

			lStrIsFp1datechange = (StringUtility.getParameter("chkFpForTenYears", request).length() > 0) ? StringUtility.getParameter("chkFpForTenYears", request) : "N";

			lStrDcrgVoucherNo = StringUtility.getParameter("txtDcrgVoucherNo", request);
			lStrDcrgVoucherDate = StringUtility.getParameter("txtDcrgVoucherDate", request);
			lStrDcrgWithHeldAmnt = StringUtility.getParameter("txtWithheldAmnt", request);
			lStrDcrgPayingAuth = StringUtility.getParameter("cmbPayingAuthority", request);
			lStrDcrgAmntAfterWithHeld = StringUtility.getParameter("txtAmntAfterWithHeld", request);
			lStrDcrgTotalRecoveryAmnt = StringUtility.getParameter("txtTotRecoveryAmnt", request);

			
			lDtFp1Date = ((StringUtility.getParameter("txtFp1Date", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtFp1Date", request).trim()) : null;
			lBgDcmlFp1Amount = ((StringUtility.getParameter("txtFp1amount", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtFp1amount", request).trim()) : BigDecimal.ZERO;
			lDtFp2Date = ((StringUtility.getParameter("txtFp2Date", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtFp2Date", request).trim()) : null;
			lBgDcmlFp2Amount = ((StringUtility.getParameter("txtFp2amount", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtFp2amount", request).trim()) : BigDecimal.ZERO;
			lStrLinkedPpoNo = ((StringUtility.getParameter("txtLinkedPpoId", request).trim()).length() > 0) ? StringUtility.getParameter("txtLinkedPpoId", request).trim() : null;

			lDtEndDate = ((StringUtility.getParameter("txtPpoEndDate", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtPpoEndDate", request).trim()) : null;
			lBgDcmlReducedPension = ((StringUtility.getParameter("txtReducedPension", request).trim()).length() > 0)
					? new BigDecimal(StringUtility.getParameter("txtReducedPension", request).trim())
					: null;
			//lBgDcmlArrearAmount = ((StringUtility.getParameter("txtArrearAmount", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtArrearAmount", request).trim()) : null;
			lStrCvpOrderNo = ((StringUtility.getParameter("txtCvpOrderNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtCvpOrderNo", request).trim() : null;
			lBgDcmlCvpPayableAmount = ((StringUtility.getParameter("txtCvpPayableAmount", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtCvpPayableAmount", request)
					.trim()) : BigDecimal.ZERO;
			//lStrCvpPaidFlag = ((StringUtility.getParameter("radioCvpPaid", request).trim()).length() > 0) ? StringUtility.getParameter("radioCvpPaid", request).trim() : null;
			lDtCvpDate = ((StringUtility.getParameter("txtCvpPaidDate", request).trim()).length() > 0)
					? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtCvpPaidDate", request).trim())
					: null;
			// lDtCvpRestorationDate =
			// ((StringUtility.getParameter("txtCvpRestorationDate",
			// request).trim()).length() > 0) ?
			// lObjSimpleDateFormat.parse(StringUtility.getParameter(
			// "txtCvpRestorationDate", request).trim()) : null;
			lStrDcrgPaidFlag = ((StringUtility.getParameter("radioDcrgPaid", request).trim()).length() > 0) ? StringUtility.getParameter("radioDcrgPaid", request).trim() : null;
			lStrDcrgOrderNo = ((StringUtility.getParameter("txtDcrgOrder", request).trim()).length() > 0) ? StringUtility.getParameter("txtDcrgOrder", request).trim() : null;

			lBgDcmlDcrgPayableAmount = ((StringUtility.getParameter("txtDcrgPayableAmount", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtDcrgPayableAmount", request)
					.trim()) : null;
			lDtDcrgPaidDate = ((StringUtility.getParameter("txtDcrgPaidDate", request).trim()).length() > 0) ? lObjSimpleDateFormat
					.parse(StringUtility.getParameter("txtDcrgPaidDate", request).trim()) : null;
			// lStrRecoveryFlag = ((StringUtility.getParameter("radioRecovery",
			// request).trim()).length() > 0) ?
			// StringUtility.getParameter("radioRecovery", request).trim() :
			// null;

			if (!StringUtility.getParameter("cmbRopType", request).equals("-1")) {
				lStrROPType = StringUtility.getParameter("cmbRopType", request).trim();
			}
			lStrLedgerNo = ((StringUtility.getParameter("txtLedgerNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtLedgerNo", request).trim() : null;
			lStrPageNo = ((StringUtility.getParameter("txtPageNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtPageNo", request).trim() : null;
			// lDtCvpEffectiveDate =
			// ((StringUtility.getParameter("txtCvpEffectFromDate",
			// request).trim()).length() > 0) ?
			// lObjSimpleDateFormat.parse(StringUtility.getParameter("txtCvpEffectFromDate",
			// request).trim()) : null;
			if (!StringUtility.getParameter("cmbCaseReceivedFrom", request).equals("-1")) {
				lStrCaseReceivedFrom = StringUtility.getParameter("cmbCaseReceivedFrom", request).trim();
				if (gObjRsrcBndle.getString("PPMT.OTHRSTATE").equalsIgnoreCase(lStrCaseReceivedFrom)) {
					if (!StringUtility.getParameter("cmbOtherState", request).equals("-1")) {
						lStrCaseReceivedOffice = StringUtility.getParameter("cmbOtherState", request).trim();
					}

				} else if (gObjRsrcBndle.getString("PPMT.OTHRTRSURY").equalsIgnoreCase(lStrCaseReceivedFrom)) {
					if (!StringUtility.getParameter("cmbReceivedTrsyOffice", request).equals("-1")) {
						lStrCaseReceivedOffice = StringUtility.getParameter("cmbReceivedTrsyOffice", request).trim();
					}
				} else if (gObjRsrcBndle.getString("PPMT.ANYOTHRSRC").equalsIgnoreCase(lStrCaseReceivedFrom)) {
					lStrCaseReceivedOffice = ((StringUtility.getParameter("txtCaseReceivedOffice", request).trim()).length() > 0)
							? StringUtility.getParameter("txtCaseReceivedOffice", request).trim()
							: null;
				}
			}
			lStrCvpVoucherNo =((StringUtility.getParameter("txtCvpVoucherNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtCvpVoucherNo", request).trim() : null;
			lDtCvpVoucherDate = ((StringUtility.getParameter("txtCvpVoucherDate", request).trim()).length() > 0)
					? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtCvpVoucherDate", request).trim())
					: null;
			// lStrAllocIndicatorFlag =
			// ((StringUtility.getParameter("radioAllocIndicator",
			// request).trim()).length() > 0) ?
			// StringUtility.getParameter("radioAllocIndicator", request).trim()
			// : null;
			lBgDcmlTotalAllocationAmt = ((StringUtility.getParameter("txtPensionAmount", request).trim()).length() > 0)
								? new BigDecimal(StringUtility.getParameter("txtPensionAmount", request).trim()): BigDecimal.ZERO;
			lBgDcmlOrgBf11136 = ((StringUtility.getParameter("txtBefore01041936", request).trim()).length() > 0)
					? new BigDecimal(StringUtility.getParameter("txtBefore01041936", request).trim())
					: BigDecimal.ZERO;
			lBgDcmlOrgAf11136 = ((StringUtility.getParameter("txtAfter01041936", request).trim()).length() > 0)
					? new BigDecimal(StringUtility.getParameter("txtAfter01041936", request).trim())
					: BigDecimal.ZERO;
			lBgDcmlOrgAf11156 = ((StringUtility.getParameter("txtAfter01111956", request).trim()).length() > 0)
					? new BigDecimal(StringUtility.getParameter("txtAfter01111956", request).trim())
					: BigDecimal.ZERO;
			lBgDcmlOrgAf10560 = ((StringUtility.getParameter("txtAfter01051960", request).trim()).length() > 0)
					? new BigDecimal(StringUtility.getParameter("txtAfter01051960", request).trim())
					: BigDecimal.ZERO;
			lBgDcmlOrgAfZp = ((StringUtility.getParameter("txtZillaParishad", request).trim()).length() > 0)
					? new BigDecimal(StringUtility.getParameter("txtZillaParishad", request).trim())
					: BigDecimal.ZERO;
					
			lBgDcmlOrgBf10436Percent = new BigDecimal(lDecFormat.format((lBgDcmlOrgBf11136.doubleValue() *100)/lBgDcmlTotalAllocationAmt.doubleValue()));
			lBgDcmlOrgAf10436Percent = new BigDecimal(lDecFormat.format((lBgDcmlOrgAf11136.doubleValue() *100)/lBgDcmlTotalAllocationAmt.doubleValue()));
			lBgDcmlOrgAf11156Percent = new BigDecimal(lDecFormat.format((lBgDcmlOrgAf11156.doubleValue() *100)/lBgDcmlTotalAllocationAmt.doubleValue()));
			lBgDcmlOrgAf10560Percent = new BigDecimal(lDecFormat.format((lBgDcmlOrgAf10560.doubleValue() *100)/lBgDcmlTotalAllocationAmt.doubleValue()));
			lBgDcmlOrgZpAfPercent = new BigDecimal(lDecFormat.format((lBgDcmlOrgAfZp.doubleValue() *100)/lBgDcmlTotalAllocationAmt.doubleValue()));
			
			
			lStrRemark = ((StringUtility.getParameter("txtRemarks", request).trim()).length() > 0) ? StringUtility.getParameter("txtRemarks", request).trim() : null;
			lStrReEmploymentFlag = ((StringUtility.getParameter("radioReEmployment", request).trim()).length() > 0) ? StringUtility.getParameter("radioReEmployment", request).trim() : null;
			lDtReEmploymentFromDate = ((StringUtility.getParameter("txtReEmpFromDate", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtReEmpFromDate",
					request).trim()) : null;
			lDtReEmploymentToDate = ((StringUtility.getParameter("txtReEmpToDate", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtReEmpToDate", request)
					.trim()) : null;
			if (!StringUtility.getParameter("cmbDaInPnsnSalary", request).equals("-1")) {
				lStrDaInPensionSalary = StringUtility.getParameter("cmbDaInPnsnSalary", request).trim();
			}
//			lBgDcmlTotalCvpAmt = ((StringUtility.getParameter("txtTotalCvpAmount", request).trim()).length() > 0)
//					? new BigDecimal(StringUtility.getParameter("txtTotalCvpAmount", request).trim())
//					: BigDecimal.ZERO;
			lBgDcmlTotalDcrgAmt = ((StringUtility.getParameter("txtTotalDcrgAmount", request).trim()).length() > 0)
					? new BigDecimal(StringUtility.getParameter("txtTotalDcrgAmount", request).trim())
					: BigDecimal.ZERO;
			lBgDcmlPeonAllowanceAmount = ((StringUtility.getParameter("txtPeonAllowance", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtPeonAllowance", request)
					.trim()) : BigDecimal.ZERO;
			lBgDcmlMedicalAllowanceAmount = ((StringUtility.getParameter("txtMedicalAllowance", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtMedicalAllowance",
					request).trim()) : BigDecimal.ZERO;
			lBgDcmlPensionCut = ((StringUtility.getParameter("txtPensionCut", request).trim()).length() > 0)
					? new BigDecimal(StringUtility.getParameter("txtPensionCut", request).trim())
					: BigDecimal.ZERO;
			lStrPensionCutMonth = StringUtility.getParameter("cmbPensionCutMonth", request);
			lStrPensionCutYear = StringUtility.getParameter("cmbPensionCutYear", request);
			String lStrMonthYear = "";
			String lStrMonth = "";
			String lStrYear = "";
			String lStrPnsnCutMonthYear = "";
			if (!"-1".equals(lStrPensionCutMonth) && !"-1".equals(lStrPensionCutYear)) 
			{
				if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
					if (lObjPrvsTrnPensionRqstHdrVO != null) {
						if (lObjPrvsTrnPensionRqstHdrVO.getPensionCutMonthYear() != null) {
							lStrMonthYear = lObjPrvsTrnPensionRqstHdrVO.getPensionCutMonthYear().toString();
							lStrYear = lStrMonthYear.substring(0, 4);
							lStrMonth = lStrMonthYear.substring(4, 6);
							lStrMonth = Integer.valueOf(lStrMonth).toString();
						}
					} else {
						lStrYear = null;
						lStrMonth = null;
					}
					generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO == null ? "" : lStrMonth, lStrPensionCutMonth, "Pension Cut Month", 0);
					generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO == null ? "" : lStrYear, lStrPensionCutYear, "Pension Cut Year", 0);

				}
				if(Integer.parseInt(lStrPensionCutMonth) < 10){
					lStrPnsnCutMonthYear = lStrPensionCutYear + "0" + lStrPensionCutMonth;
				} else {
					lStrPnsnCutMonthYear = lStrPensionCutYear + lStrPensionCutMonth;
				}
				lObjTrnPensionRqstHdrVO.setPensionCutMonthYear(Integer.parseInt(lStrPnsnCutMonthYear));
				
			}
			
			lBgDcmlOther1 = ((StringUtility.getParameter("txtOther1", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtOther1", request).trim()) : BigDecimal.ZERO;
			lBgDcmlOther2 = ((StringUtility.getParameter("txtOther2", request).trim()).length() > 0) ? new BigDecimal(StringUtility.getParameter("txtOther2", request).trim()) : BigDecimal.ZERO;
			lStrFpAvailableFlag = ((StringUtility.getParameter("radioFpAvailable", request).trim()).length() > 0) ? StringUtility.getParameter("radioFpAvailable", request).trim() : "Y";
			
			lDtDateOfRetirement = ((StringUtility.getParameter("txtDateOfRetirement", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtDateOfRetirement",
					request).trim()) : null;
			lStrDcrgNameOfPayee = ((StringUtility.getParameter("txtDcrgNameOfPayee", request).trim()).length() > 0) ? StringUtility.getParameter("txtDcrgNameOfPayee", request).trim() : null;
			if(StringUtility.getParameter("cmbDcrgTrsryForPayment", request) != "-1"){
				lStrDcrgTrsryForPayment = StringUtility.getParameter("cmbDcrgTrsryForPayment", request).trim();
			}
			lStrCvpNameOfPayee = ((StringUtility.getParameter("txtCvpNameOfPayee", request).trim()).length() > 0) ? StringUtility.getParameter("txtCvpNameOfPayee", request).trim() : null;
			if(StringUtility.getParameter("cmbCvpTrsryForPayment", request) != "-1"){
				lStrCvpTrsryForPayment = StringUtility.getParameter("cmbCvpTrsryForPayment", request).trim();
			}
			lStrCvpAuthority = ((StringUtility.getParameter("txtCvpAuthority", request).trim()).length() > 0) ? StringUtility.getParameter("txtCvpAuthority", request).trim() : null;
			lDtCvpStartDate = ((StringUtility.getParameter("txtCvpStartDate", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtCvpStartDate", request)
					.trim()) : null;
			lDtCvpEndDate = ((StringUtility.getParameter("txtCvpEndDate", request).trim()).length() > 0) ? lObjSimpleDateFormat.parse(StringUtility.getParameter("txtCvpEndDate", request)
					.trim()) : null;
			
			// lBgDcmlOther3=((StringUtility.getParameter("txtOther3",
			// request).trim()).length() > 0) ? new
			// BigDecimal(StringUtility.getParameter("txtOther3",
			// request).trim()) : BigDecimal.ZERO;

			// unused
			
			if(lStrDcrgVoucherNo.length() > 0 && !lStrDcrgVoucherNo.equals(""))
				lObjTrnPensionRqstHdrVO.setDcrgVoucherNo(lStrDcrgVoucherNo.trim());
			if(lStrDcrgVoucherDate.length() > 0 && !lStrDcrgVoucherDate.equals(""))
				lObjTrnPensionRqstHdrVO.setDcrgVoucherDate(StringUtility.convertStringToDate(lStrDcrgVoucherDate.trim()));
			if(lStrDcrgWithHeldAmnt.length() > 0 && !lStrDcrgWithHeldAmnt.equals(""))
				lObjTrnPensionRqstHdrVO.setDcrgWithHeldAmnt(new BigDecimal(lStrDcrgWithHeldAmnt.trim()));
			if(!lStrDcrgPayingAuth.trim().equals("-1") && lStrDcrgPayingAuth.length() > 0)
				lObjTrnPensionRqstHdrVO.setDcrgPayingAuth(lStrDcrgPayingAuth.trim());
			if(lStrDcrgAmntAfterWithHeld.length() > 0 && !lStrDcrgAmntAfterWithHeld.equals(""))
			{
				lBgDcmlDcrgAmntAftrWithHeld = new BigDecimal(lStrDcrgAmntAfterWithHeld.trim());
				lObjTrnPensionRqstHdrVO.setDcrgAmntAfterWithHeld(lBgDcmlDcrgAmntAftrWithHeld);
			}
			if(lStrDcrgTotalRecoveryAmnt.length() > 0 && !lStrDcrgTotalRecoveryAmnt.equals(""))
				lObjTrnPensionRqstHdrVO.setDcrgTotalRecoveryAmnt(new BigDecimal(lStrDcrgTotalRecoveryAmnt.trim()));
			
			
			lObjTrnPensionRqstHdrVO.setPensionableAmount(lBgDcmlPensionableAmount);
			lObjTrnPensionRqstHdrVO.setDppfAmount(lBgDcmlDppfAmount);

			lObjTrnPensionRqstHdrVO.setTotalRecovery(lBgDcmlTotalRecovery);
			lObjTrnPensionRqstHdrVO.setPendingRecovery(lBgDcmlPendingRecovery);

			lObjTrnPensionRqstHdrVO.setRedAf10560(lBgDcmlRedAf10560);
			lObjTrnPensionRqstHdrVO.setRedAf11136(lBgDcmlRedAf11136);
			lObjTrnPensionRqstHdrVO.setRedAf11156(lBgDcmlRedAf11156);
			lObjTrnPensionRqstHdrVO.setRedAfZp(lBgDcmlRedAfZp);
			lObjTrnPensionRqstHdrVO.setPersonalPension(lBgDcmlPersonalPension);
			lObjTrnPensionRqstHdrVO.setIr(lBgDcmlIr);
			lObjTrnPensionRqstHdrVO.setLastPayTransfer(lBgDcmlLastPayTransfer);
			lObjTrnPensionRqstHdrVO.setLastPaidAmount(lBgDcmlLastPaidAmount);

			// used
			lObjTrnPensionRqstHdrVO.setPpoNo(lStrPpoNo.toUpperCase());
			lObjTrnPensionRqstHdrVO.setTypeFlag(lStrTypeFlag);
			lObjTrnPensionRqstHdrVO.setPensionType(lStrPensionType);
			lObjTrnPensionRqstHdrVO.setStatus(lStrStatus);
			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjTrnPensionRqstHdrVO.setTrnCounter(1);
				lObjTrnPensionRqstHdrVO.setCaseStatus(gObjRsrcBndle.getString("STATFLG.NEW"));
				lObjTrnPensionRqstHdrVO.setSchemeType(gObjRsrcBndle.getString("PPMT.IRLA"));
				lObjTrnPensionRqstHdrVO.setInwardMode(gObjRsrcBndle.getString("INWDMODE.PHYSICAL"));
				lObjTrnPensionRqstHdrVO.setPpoInwardDate(gCurrDate);
			}
			lObjTrnPensionRqstHdrVO.setCalcType(lStrCalcType);
			lObjTrnPensionRqstHdrVO.setPpoAGOutwardNo(lStrPpoAGOutwardNo);
			lObjTrnPensionRqstHdrVO.setPpoAGOutwardDate(lDtPpoAGOutwardDate);
			if(!"".equals(lStrStateCode) && lStrStateCode.length() >0)
				lObjTrnPensionRqstHdrVO.setDaRateForState(new BigDecimal(lStrStateCode));
			lObjTrnPensionRqstHdrVO.setHeadCode(lBgDcmlHeadCode);
			lObjTrnPensionRqstHdrVO.setCommensionDate(lDtCommencementDate);
			
			if (gObjRsrcBndle.getString("PPMT.ROPTYPE1996").equals(lStrROPType) && lDtDateOfRetirement != null 
					&& (lDtDateOfRetirement.before(IFMSCommonServiceImpl.getDateFromString("31/07/2004")) 
					|| lDtDateOfRetirement.equals(IFMSCommonServiceImpl.getDateFromString("31/07/2004")))) 
			{
					lObjTrnPensionRqstHdrVO.setDpFlag("Y");
			}
			else
			{
					lObjTrnPensionRqstHdrVO.setDpFlag("N");
			}
			
			lObjTrnPensionRqstHdrVO.setBasicPensionAmount(lBgDcmlBasicPensionAmount);
			lObjTrnPensionRqstHdrVO.setDpPercent(lBgDcmlDpPercent);
			// lObjTrnPensionRqstHdrVO.setAdpAmount(lBgDcmlAdpAmount);
			// lObjTrnPensionRqstHdrVO.setDaPercent(lBgDcmlDaPercent);
			lObjTrnPensionRqstHdrVO.setCvpMonthlyAmount(lBgDcmlCvpMonthlyAmount);
			lObjTrnPensionRqstHdrVO.setFp1Date(lDtFp1Date);
			lObjTrnPensionRqstHdrVO.setFp1Amount(lBgDcmlFp1Amount);
			lObjTrnPensionRqstHdrVO.setFp2Date(lDtFp2Date);
			lObjTrnPensionRqstHdrVO.setFp2Amount(lBgDcmlFp2Amount);
			lObjTrnPensionRqstHdrVO.setLinkedPpoNo(lStrLinkedPpoNo);
			lObjTrnPensionRqstHdrVO.setEndDate(lDtEndDate);
			lObjTrnPensionRqstHdrVO.setReducedPension(lBgDcmlReducedPension);
			//lObjTrnPensionRqstHdrVO.setArrearAmount(lBgDcmlArrearAmount);
			lObjTrnPensionRqstHdrVO.setCvpOrderNo(lStrCvpOrderNo);
			lObjTrnPensionRqstHdrVO.setCvpAmount(lBgDcmlCvpPayableAmount);
			//lObjTrnPensionRqstHdrVO.setCvpPaidFlag(lStrCvpPaidFlag);
			lObjTrnPensionRqstHdrVO.setCvpDate(lDtCvpDate);
			lObjTrnPensionRqstHdrVO.setIsFp1datechange(lStrIsFp1datechange);
			// lObjTrnPensionRqstHdrVO.setCvpRestorationDate(lDtCvpRestorationDate);
			lObjTrnPensionRqstHdrVO.setDcrgPaidFlag(lStrDcrgPaidFlag);
			lObjTrnPensionRqstHdrVO.setDcrgOrderNo(lStrDcrgOrderNo);
			lObjTrnPensionRqstHdrVO.setDcrgAmount(lBgDcmlDcrgAmntAftrWithHeld);
			lObjTrnPensionRqstHdrVO.setPaidDate(lDtDcrgPaidDate);
			// lObjTrnPensionRqstHdrVO.setRecoveryFlag(lStrRecoveryFlag);
			lObjTrnPensionRqstHdrVO.setRopType(lStrROPType);
			lObjTrnPensionRqstHdrVO.setLedgerNo(lStrLedgerNo);
			lObjTrnPensionRqstHdrVO.setPageNo(lStrPageNo);
			lObjTrnPensionRqstHdrVO.setCaseReceivedFrom(lStrCaseReceivedFrom);
			lObjTrnPensionRqstHdrVO.setCaseReceivedOffice(lStrCaseReceivedOffice);
			// lObjTrnPensionRqstHdrVO.setAllocIndicatorFlag(lStrAllocIndicatorFlag);
			lObjTrnPensionRqstHdrVO.setOrgBf11136(lBgDcmlOrgBf11136);
			lObjTrnPensionRqstHdrVO.setOrgAf11136(lBgDcmlOrgAf11136);
			lObjTrnPensionRqstHdrVO.setOrgAf11156(lBgDcmlOrgAf11156);
			lObjTrnPensionRqstHdrVO.setOrgAf10560(lBgDcmlOrgAf10560);
			lObjTrnPensionRqstHdrVO.setOrgAfZp(lBgDcmlOrgAfZp);
			lObjTrnPensionRqstHdrVO.setRemark(lStrRemark);
			lObjTrnPensionRqstHdrVO.setReEmploymentFlag(lStrReEmploymentFlag);
			lObjTrnPensionRqstHdrVO.setReEmploymentFromDate(lDtReEmploymentFromDate);
			lObjTrnPensionRqstHdrVO.setReEmploymentToDate(lDtReEmploymentToDate);
			lObjTrnPensionRqstHdrVO.setDaInPensionSalary(lStrDaInPensionSalary);
			//lObjTrnPensionRqstHdrVO.setTotalCvpAmount(lBgDcmlTotalCvpAmt);
			lObjTrnPensionRqstHdrVO.setTotalDcrgAmount(lBgDcmlTotalDcrgAmt);
			lObjTrnPensionRqstHdrVO.setPeonAllowanceAmount(lBgDcmlPeonAllowanceAmount);
			lObjTrnPensionRqstHdrVO.setMedicalAllowenceAmount(lBgDcmlMedicalAllowanceAmount);
			lObjTrnPensionRqstHdrVO.setPensionCut(lBgDcmlPensionCut);
			lObjTrnPensionRqstHdrVO.setOther1(lBgDcmlOther1);
			lObjTrnPensionRqstHdrVO.setOther2(lBgDcmlOther2);
			lObjTrnPensionRqstHdrVO.setCvpVoucherNo(lStrCvpVoucherNo);
			lObjTrnPensionRqstHdrVO.setCvpVoucherDate(lDtCvpVoucherDate);
			// lObjTrnPensionRqstHdrVO.setOther3(lBgDcmlOther3);
			lObjTrnPensionRqstHdrVO.setOrgBf10436Percent(lBgDcmlOrgBf10436Percent);
			lObjTrnPensionRqstHdrVO.setOrgAf10436Percent(lBgDcmlOrgAf10436Percent);
			lObjTrnPensionRqstHdrVO.setOrgAf11156Percent(lBgDcmlOrgAf11156Percent);
			lObjTrnPensionRqstHdrVO.setOrgAf10560Percent(lBgDcmlOrgAf10560Percent);
			lObjTrnPensionRqstHdrVO.setOrgZpAfPercent(lBgDcmlOrgZpAfPercent);
			lObjTrnPensionRqstHdrVO.setFpAvailableFlag(lStrFpAvailableFlag);
			lObjTrnPensionRqstHdrVO.setCvpNameOfPayee(lStrCvpNameOfPayee);
			lObjTrnPensionRqstHdrVO.setCvpTrsryForPayment(lStrCvpTrsryForPayment);
			lObjTrnPensionRqstHdrVO.setDcrgNameOfPayee(lStrDcrgNameOfPayee);
			lObjTrnPensionRqstHdrVO.setDcrgTrsryForPayment(lStrDcrgTrsryForPayment);
			lObjTrnPensionRqstHdrVO.setCvpAuthority(lStrCvpAuthority);
			lObjTrnPensionRqstHdrVO.setCvpStartDate(lDtCvpStartDate);
			lObjTrnPensionRqstHdrVO.setCvpEndDate(lDtCvpEndDate);
			
			// lObjTrnPensionRqstHdrVO.setCvpEffectiveDate(lDtCvpEffectiveDate);
			lObjTrnPensionRqstHdrVO.setDbId(new BigDecimal(gLngDBId));
			lObjTrnPensionRqstHdrVO.setLocationCode(gStrLocationCode);
			lStrShowCaseFor = ((StringUtility.getParameter("hdnShowCaseFor", request).trim()).length() > 0) ? StringUtility.getParameter("hdnShowCaseFor", request).trim() : null;
			if (!StringUtility.getParameter("cmbBankBranch", request).equals("-1")) {
				lStrBranchCode = StringUtility.getParameter("cmbBankBranch", request).trim();
			}
			OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());
			if (!"".equals(lStrBranchCode)) {
				//if (gObjRsrcBndle.getString("CASEFOR.IDENTIFICATION").equalsIgnoreCase(lStrShowCaseFor)) {
					List lLstResult = lObjOnlinePensionCaseDAO.getAuditorName(inputMap, gStrLocationCode, lStrBranchCode);
					if (lLstResult != null && !lLstResult.isEmpty() && lLstResult.get(0) != null) {
						Object[] obj = (Object[]) lLstResult.get(0);
						if (obj[0] != null) {
							lBgDcmlCaseOwner = new BigDecimal(obj[0].toString());
							lObjTrnPensionRqstHdrVO.setCaseOwner(lBgDcmlCaseOwner);
						}
					}

				//}
			}
			else
			{
				lObjTrnPensionRqstHdrVO.setCaseOwner(null);
			}
			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjTrnPensionRqstHdrVO.setCreatedUserId(new BigDecimal(gLngUserId));
				lObjTrnPensionRqstHdrVO.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjTrnPensionRqstHdrVO.setCreatedDate(new Date());
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lObjTrnPensionRqstHdrVO.setUpdatedDate(new Date());
				lObjTrnPensionRqstHdrVO.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjTrnPensionRqstHdrVO.setUpdatedUserId(new BigDecimal(gLngUserId));
				if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
					// lStrShowCaseFor =
					// ((StringUtility.getParameter("hdnShowCaseFor",
					// request).trim()).length() > 0) ?
					// StringUtility.getParameter("hdnShowCaseFor",
					// request).trim() : null;

					if (!gObjRsrcBndle.getString("CASEFOR.MODIFICATIONCASE").equalsIgnoreCase(lStrShowCaseFor)) {
						lObjTrnPensionRqstHdrVO.setCaseStatus(gObjRsrcBndle.getString("STATFLG.MODIFIEDBYAUDITOR"));
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getPpoNo(), lObjTrnPensionRqstHdrVO.getPpoNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getPpoNo(), lObjTrnPensionRqstHdrVO.getPpoNo(), "PPO Number", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getTypeFlag(), lObjTrnPensionRqstHdrVO.getTypeFlag())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getTypeFlag(), lObjTrnPensionRqstHdrVO.getTypeFlag(), "Pension Class", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getPensionType(), lObjTrnPensionRqstHdrVO.getPensionType())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getPensionType(), lObjTrnPensionRqstHdrVO.getPensionType(), "Pension Type", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getStatus(), lObjTrnPensionRqstHdrVO.getStatus())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getStatus(), lObjTrnPensionRqstHdrVO.getStatus(), "Pension Case Status", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getCalcType(), lObjTrnPensionRqstHdrVO.getCalcType())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getCalcType(), lObjTrnPensionRqstHdrVO.getCalcType(), "Calculation Type", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getPpoAGOutwardNo(), lObjTrnPensionRqstHdrVO.getPpoAGOutwardNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getPpoAGOutwardNo(), lObjTrnPensionRqstHdrVO.getPpoAGOutwardNo(), "PPO Outward Number", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null
							|| getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getPpoAGOutwardDate(), lObjTrnPensionRqstHdrVO.getPpoAGOutwardDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getPpoAGOutwardDate(), lObjTrnPensionRqstHdrVO.getPpoAGOutwardDate(), "PPO Outward Date", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getHeadCode(), lObjTrnPensionRqstHdrVO.getHeadCode())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getHeadCode(), lObjTrnPensionRqstHdrVO.getHeadCode(), "Pensioner's Category", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getCommensionDate(), lObjTrnPensionRqstHdrVO.getCommensionDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getCommensionDate(), lObjTrnPensionRqstHdrVO.getCommensionDate(), "Date of Commencement", 0);
					}
//					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getDpFlag(), lObjTrnPensionRqstHdrVO.getDpFlag())) {
//						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getDpFlag(), lObjTrnPensionRqstHdrVO.getDpFlag(), "DP Merge Flag", 0);
//					}
					if (lObjPrvsTrnPensionRqstHdrVO == null
							|| getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getBasicPensionAmount(), lObjTrnPensionRqstHdrVO.getBasicPensionAmount())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getBasicPensionAmount(), lObjTrnPensionRqstHdrVO.getBasicPensionAmount(),
								"Basic Pension Before Commutation", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getDpPercent(), lObjTrnPensionRqstHdrVO.getDpPercent())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getDpPercent(), lObjTrnPensionRqstHdrVO.getDpPercent(), "DP Percent", 0);
					}
					// if (lObjPrvsTrnPensionRqstHdrVO == null ||
					// getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getAdpAmount(),
					// lObjTrnPensionRqstHdrVO.getAdpAmount())) {
					// generateCorrectionDtlsVOForAmount(null,
					// lObjPrvsTrnPensionRqstHdrVO.getAdpAmount(),
					// lObjTrnPensionRqstHdrVO.getAdpAmount(), "ADP Amount", 0);
					// }
					// if (lObjPrvsTrnPensionRqstHdrVO == null ||
					// getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getDaPercent(),
					// lObjTrnPensionRqstHdrVO.getDaPercent())) {
					// generateCorrectionDtlsVOForAmount(null,
					// lObjPrvsTrnPensionRqstHdrVO.getDaPercent(),
					// lObjTrnPensionRqstHdrVO.getDaPercent(), "DA Percent", 0);
					// }
					if (lObjPrvsTrnPensionRqstHdrVO == null
							|| getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getCvpMonthlyAmount(), lObjTrnPensionRqstHdrVO.getCvpMonthlyAmount())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getCvpMonthlyAmount(), lObjTrnPensionRqstHdrVO.getCvpMonthlyAmount(), "Commuted Pension", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getFpAvailableFlag(), lObjTrnPensionRqstHdrVO.getFpAvailableFlag())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getFpAvailableFlag(), lObjTrnPensionRqstHdrVO.getFpAvailableFlag(), "Do you want to include family pension details?", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getFp1Date(), lObjTrnPensionRqstHdrVO.getFp1Date())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getFp1Date(), lObjTrnPensionRqstHdrVO.getFp1Date(), "EFP Date", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getFp1Amount(), lObjTrnPensionRqstHdrVO.getFp1Amount())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getFp1Amount(), lObjTrnPensionRqstHdrVO.getFp1Amount(), "EFP Amount", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getFp2Date(), lObjTrnPensionRqstHdrVO.getFp2Date())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getFp2Date(), lObjTrnPensionRqstHdrVO.getFp2Date(), "FP Date", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getFp2Amount(), lObjTrnPensionRqstHdrVO.getFp2Amount())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getFp2Amount(), lObjTrnPensionRqstHdrVO.getFp2Amount(), "FP Amount", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getLinkedPpoNo(), lObjTrnPensionRqstHdrVO.getLinkedPpoNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getLinkedPpoNo(), lObjTrnPensionRqstHdrVO.getLinkedPpoNo(), "Linked PPO Number", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getEndDate(), lObjTrnPensionRqstHdrVO.getEndDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getEndDate(), lObjTrnPensionRqstHdrVO.getEndDate(), "PPO End Date", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getReducedPension(), lObjTrnPensionRqstHdrVO.getReducedPension())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getReducedPension(), lObjTrnPensionRqstHdrVO.getReducedPension(), "Basic Pension After Commutation", 0);
					}
//					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getArrearAmount(), lObjTrnPensionRqstHdrVO.getArrearAmount())) {
//						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getArrearAmount(), lObjTrnPensionRqstHdrVO.getArrearAmount(), "Arrear Amount", 0);
//					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getIsFp1datechange(), lObjTrnPensionRqstHdrVO.getIsFp1datechange())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getIsFp1datechange(), lObjTrnPensionRqstHdrVO.getIsFp1datechange(), "FP1 for 10 years", 0);
					}


					// if (lObjPrvsTrnPensionRqstHdrVO == null ||
					// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getRecoveryFlag(),
					// lObjTrnPensionRqstHdrVO.getRecoveryFlag())) {
					// generateCorrectionDtlsVO(null,
					// lObjPrvsTrnPensionRqstHdrVO.getRecoveryFlag(),
					// lObjTrnPensionRqstHdrVO.getRecoveryFlag(),
					// "Recovery Flag", 0);
					// }
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getRopType(), lObjTrnPensionRqstHdrVO.getRopType())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getRopType(), lObjTrnPensionRqstHdrVO.getRopType(), "Pay Commission Revision", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getLedgerNo(), lObjTrnPensionRqstHdrVO.getLedgerNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getLedgerNo(), lObjTrnPensionRqstHdrVO.getLedgerNo(), "Volume Number", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getPageNo(), lObjTrnPensionRqstHdrVO.getPageNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getPageNo(), lObjTrnPensionRqstHdrVO.getPageNo(), "Page Number", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getCaseReceivedFrom(), lObjTrnPensionRqstHdrVO.getCaseReceivedFrom())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getCaseReceivedFrom(), lObjTrnPensionRqstHdrVO.getCaseReceivedFrom(), "Case Received From", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getCaseReceivedOffice(), lObjTrnPensionRqstHdrVO.getCaseReceivedOffice())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getCaseReceivedOffice(), lObjTrnPensionRqstHdrVO.getCaseReceivedOffice(), "Name of Case Received Office", 0);
					}
					// if (lObjPrvsTrnPensionRqstHdrVO == null ||
					// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getAllocIndicatorFlag(),
					// lObjTrnPensionRqstHdrVO.getAllocIndicatorFlag())) {
					// generateCorrectionDtlsVO(null,
					// lObjPrvsTrnPensionRqstHdrVO.getAllocIndicatorFlag(),
					// lObjTrnPensionRqstHdrVO.getAllocIndicatorFlag(),
					// "Allocation Indicator Flag", 0);
					// }
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getOrgBf11136(), lObjTrnPensionRqstHdrVO.getOrgBf11136())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getOrgBf11136(), lObjTrnPensionRqstHdrVO.getOrgBf11136(), "Original Pension Before 01/04/1936", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getOrgAf11136(), lObjTrnPensionRqstHdrVO.getOrgAf11136())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getOrgAf11136(), lObjTrnPensionRqstHdrVO.getOrgAf11136(), "Original Pension After 01/04/1936", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getOrgAf11156(), lObjTrnPensionRqstHdrVO.getOrgAf11156())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getOrgAf11156(), lObjTrnPensionRqstHdrVO.getOrgAf11156(), "Original Pension After 01/11/1956", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getOrgAf10560(), lObjTrnPensionRqstHdrVO.getOrgAf10560())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getOrgAf10560(), lObjTrnPensionRqstHdrVO.getOrgAf10560(), "Original Pension After 01/05/1960", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getOrgAfZp(), lObjTrnPensionRqstHdrVO.getOrgAfZp())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getOrgAfZp(), lObjTrnPensionRqstHdrVO.getOrgAfZp(), "Original Pension Zilla Parishad", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getReEmploymentFlag(), lObjTrnPensionRqstHdrVO.getReEmploymentFlag())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getReEmploymentFlag(), lObjTrnPensionRqstHdrVO.getReEmploymentFlag(), "Re-Employment Flag", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null
							|| getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getReEmploymentFromDate(), lObjTrnPensionRqstHdrVO.getReEmploymentFromDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getReEmploymentFromDate(), lObjTrnPensionRqstHdrVO.getReEmploymentFromDate(), "Re-Employment From Date", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null
							|| getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getReEmploymentToDate(), lObjTrnPensionRqstHdrVO.getReEmploymentToDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getReEmploymentToDate(), lObjTrnPensionRqstHdrVO.getReEmploymentToDate(), "Re-Employment To Date", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getDaInPensionSalary(), lObjTrnPensionRqstHdrVO.getDaInPensionSalary())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getDaInPensionSalary(), lObjTrnPensionRqstHdrVO.getDaInPensionSalary(), "Re-Employment DA In Pension/Salary", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null
							|| getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getPeonAllowanceAmount(), lObjTrnPensionRqstHdrVO.getPeonAllowanceAmount())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getPeonAllowanceAmount(), lObjTrnPensionRqstHdrVO.getPeonAllowanceAmount(), "Peon Allowance Amount", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null
							|| getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getMedicalAllowenceAmount(), lObjTrnPensionRqstHdrVO.getMedicalAllowenceAmount())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getMedicalAllowenceAmount(), lObjTrnPensionRqstHdrVO.getMedicalAllowenceAmount(),
								"Medical Allowance Amount", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getPensionCut(), lObjTrnPensionRqstHdrVO.getPensionCut())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getPensionCut(), lObjTrnPensionRqstHdrVO.getPensionCut(), "Pension Cut", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getOther1(), lObjTrnPensionRqstHdrVO.getOther1())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getOther1(), lObjTrnPensionRqstHdrVO.getOther1(), "Galantory amount", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getOther2(), lObjTrnPensionRqstHdrVO.getOther2())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getOther2(), lObjTrnPensionRqstHdrVO.getOther2(), "Other Benefit", 0);
					}
					// if (lObjPrvsTrnPensionRqstHdrVO == null ||
					// getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getOther3(),
					// lObjTrnPensionRqstHdrVO.getOther3())) {
					// generateCorrectionDtlsVOForAmount(null,
					// lObjPrvsTrnPensionRqstHdrVO.getOther3(),
					// lObjTrnPensionRqstHdrVO.getOther3(), "Other3", 0);
					// }
					/*	if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getCvpPaidFlag(), lObjTrnPensionRqstHdrVO.getCvpPaidFlag())) {
					generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getCvpPaidFlag(), lObjTrnPensionRqstHdrVO.getCvpPaidFlag(), "Commutation Paid Flag", 0);
					}*/
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getCvpOrderNo(), lObjTrnPensionRqstHdrVO.getCvpOrderNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getCvpOrderNo(), lObjTrnPensionRqstHdrVO.getCvpOrderNo(), "Commutation Order Number", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getCvpDate(), lObjTrnPensionRqstHdrVO.getCvpDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getCvpDate(), lObjTrnPensionRqstHdrVO.getCvpDate(), "Commutation Order Date", 0);
					}
//					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getTotalCvpAmount(), lObjTrnPensionRqstHdrVO.getTotalCvpAmount())) {
//						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getTotalCvpAmount(), lObjTrnPensionRqstHdrVO.getTotalCvpAmount(), "Commutation Amount", 0);
//					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getCvpAmount(), lObjTrnPensionRqstHdrVO.getCvpAmount())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getCvpAmount(), lObjTrnPensionRqstHdrVO.getCvpAmount(), "Commuted Value of Pension", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getCvpVoucherNo(), lObjTrnPensionRqstHdrVO.getCvpVoucherNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getCvpVoucherNo(), lObjTrnPensionRqstHdrVO.getCvpVoucherNo(), "Commutation Voucher Number", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getCvpVoucherDate(), lObjTrnPensionRqstHdrVO.getCvpVoucherDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getCvpVoucherDate(), lObjTrnPensionRqstHdrVO.getCvpVoucherDate(), "Commutation Voucher Date", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getCvpNameOfPayee(), lObjTrnPensionRqstHdrVO.getCvpNameOfPayee())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getCvpNameOfPayee(), lObjTrnPensionRqstHdrVO.getCvpNameOfPayee(), "Commutation Name of Payee", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getCvpTrsryForPayment(), lObjTrnPensionRqstHdrVO.getCvpTrsryForPayment())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getCvpTrsryForPayment(), lObjTrnPensionRqstHdrVO.getCvpTrsryForPayment(), "Commutation Treasury for Payment", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getCvpAuthority(), lObjTrnPensionRqstHdrVO.getCvpAuthority())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getCvpAuthority(), lObjTrnPensionRqstHdrVO.getCvpAuthority(), "Commutation Authority", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getCvpStartDate(), lObjTrnPensionRqstHdrVO.getCvpStartDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getCvpStartDate(), lObjTrnPensionRqstHdrVO.getCvpStartDate(), "Commutation Start Date", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getCvpEndDate(), lObjTrnPensionRqstHdrVO.getCvpEndDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getCvpEndDate(), lObjTrnPensionRqstHdrVO.getCvpEndDate(), "Commutation End Date", 0);
					}
					// if (lObjPrvsTrnPensionRqstHdrVO == null
					// ||
					// getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getCvpRestorationDate(),
					// lObjTrnPensionRqstHdrVO.getCvpRestorationDate())) {
					// generateCorrectionDtlsVOForDate(null,
					// lObjPrvsTrnPensionRqstHdrVO.getCvpRestorationDate(),
					// lObjTrnPensionRqstHdrVO.getCvpRestorationDate(),
					// "CVP Restoration Date", 0);
					// }
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getDcrgPaidFlag(), lObjTrnPensionRqstHdrVO.getDcrgPaidFlag())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getDcrgPaidFlag(), lObjTrnPensionRqstHdrVO.getDcrgPaidFlag(), "DCRG Paid Flag", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getDcrgOrderNo(), lObjTrnPensionRqstHdrVO.getDcrgOrderNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getDcrgOrderNo(), lObjTrnPensionRqstHdrVO.getDcrgOrderNo(), "DCRG Order Number", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnPensionRqstHdrVO.getPaidDate(), lObjTrnPensionRqstHdrVO.getPaidDate())) {
						generateCorrectionDtlsVOForDate(null, lObjPrvsTrnPensionRqstHdrVO.getPaidDate(), lObjTrnPensionRqstHdrVO.getPaidDate(), "DCRG Order Date", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null
							|| getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getTotalDcrgAmount(), lObjTrnPensionRqstHdrVO.getTotalDcrgAmount())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getTotalDcrgAmount(), lObjTrnPensionRqstHdrVO.getTotalDcrgAmount(), "Sanction Gratuity Amount", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnPensionRqstHdrVO.getDcrgAmount(), lObjTrnPensionRqstHdrVO.getDcrgAmount())) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRqstHdrVO.getDcrgAmount(), lObjTrnPensionRqstHdrVO.getDcrgAmount(), "DCRG Payable Amount", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getDcrgNameOfPayee(), lObjTrnPensionRqstHdrVO.getDcrgNameOfPayee())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getDcrgNameOfPayee(), lObjTrnPensionRqstHdrVO.getDcrgNameOfPayee(), "DCRG Name of Payee", 0);
					}
					if (lObjPrvsTrnPensionRqstHdrVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnPensionRqstHdrVO.getDcrgTrsryForPayment(), lObjTrnPensionRqstHdrVO.getDcrgTrsryForPayment())) {
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRqstHdrVO.getDcrgTrsryForPayment(), lObjTrnPensionRqstHdrVO.getDcrgTrsryForPayment(), "DCRG Treasury for Payment", 0);
					}
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}

		return lObjTrnPensionRqstHdrVO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateMstPensionerDtlsVO(java.util.Map)
	 */
	public MstPensionerDtls generateMstPensionerDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		MstPensionerDtls lObjMstPensionerDtlsVO = null;
		MstPensionerDtls lObjPrvsMstPensionerDtlsVO = new MstPensionerDtls();
		if (inputMap.containsKey("lObjMstPensionerDtlsVO")) {
			lObjMstPensionerDtlsVO = (MstPensionerDtls) inputMap.get("lObjMstPensionerDtlsVO");
			lObjPrvsMstPensionerDtlsVO = (MstPensionerDtls) lObjMstPensionerDtlsVO.clone();
		} else {
			lObjMstPensionerDtlsVO = new MstPensionerDtls();
		}
		try {

			String lStrRegistrationNo = null;
			String lStrBankCode = null;
			String lStrBranchCode = null;
			String lStrAccountNo = null;
			String lStrActiveFlag = null;
			String lStrOldStateCode = null;
			String lStrOldTreasury = null;
			String lStrOldSubTreasury = null;
			String lStrCorrectionFlag = null;
			String lStrBankEmailId = null;
			String lStrIsPwrofAtrny = null;
			String lStrIdentificationFlag = null;
			String lStrPaymentScheme = null;
			String lStrMode = null;

			setSessionInfo(inputMap);

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}
			if (!StringUtility.getParameter("cmbBankCode", request).equals("-1")) {
				lStrBankCode = StringUtility.getParameter("cmbBankCode", request).trim();
			}
			if (!StringUtility.getParameter("cmbBankBranch", request).equals("-1")) {
				lStrBranchCode = StringUtility.getParameter("cmbBankBranch", request).trim();
			}
			if(!StringUtility.getParameter("cmbPaymentScheme", request).equals("-1")) {
				lStrPaymentScheme = StringUtility.getParameter("cmbPaymentScheme", request).trim();
			}

			lStrAccountNo = ((StringUtility.getParameter("txtAccountNo", request).trim()).length() > 0) ? StringUtility.getParameter("txtAccountNo", request).trim() : null;
			lObjMstPensionerDtlsVO.setBankCode(lStrBankCode);
			lObjMstPensionerDtlsVO.setBranchCode(lStrBranchCode);
			lObjMstPensionerDtlsVO.setAccountNo(lStrAccountNo);
			lObjMstPensionerDtlsVO.setLocationCode(gStrLocationCode);
			lObjMstPensionerDtlsVO.setPaymentScheme(lStrPaymentScheme);

			if ("Add".equalsIgnoreCase(lStrMode)) {
				lObjMstPensionerDtlsVO.setTrnCounter(1);
				lObjMstPensionerDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
				lObjMstPensionerDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjMstPensionerDtlsVO.setCreatedDate(new Date());
			}
			if ("Update".equalsIgnoreCase(lStrMode)) {
				lObjMstPensionerDtlsVO.setUpdatedDate(new Date());
				lObjMstPensionerDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjMstPensionerDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));

				if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
					if (lObjPrvsMstPensionerDtlsVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerDtlsVO.getBankCode(), lObjMstPensionerDtlsVO.getBankCode())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerDtlsVO.getBankCode(), lObjMstPensionerDtlsVO.getBankCode(), "Pensioner Bank", 0);
					}
					if (lObjPrvsMstPensionerDtlsVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerDtlsVO.getBranchCode(), lObjMstPensionerDtlsVO.getBranchCode())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerDtlsVO.getBranchCode(), lObjMstPensionerDtlsVO.getBranchCode(), "Pensioner Branch", 0);
					}
					if (lObjPrvsMstPensionerDtlsVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerDtlsVO.getAccountNo(), lObjMstPensionerDtlsVO.getAccountNo())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerDtlsVO.getAccountNo(), lObjMstPensionerDtlsVO.getAccountNo(), "Pensioner Account Number", 0);
					}
					if (lObjPrvsMstPensionerDtlsVO == null || getVerifyCurrentVoWithPreviousVo(lObjPrvsMstPensionerDtlsVO.getPaymentScheme(), lObjMstPensionerDtlsVO.getPaymentScheme())) {
						generateCorrectionDtlsVO(null, lObjPrvsMstPensionerDtlsVO.getPaymentScheme(), lObjMstPensionerDtlsVO.getPaymentScheme(), "Payment Scheme", 0);
					}
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lObjMstPensionerDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateTrnProvisionalPensionDtlsVO(java.util.Map)
	 */
	public List<TrnProvisionalPensionDtls> generateTrnProvisionalPensionDtlsVO(Map<String, Object> inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnProvisionalPensionDtls lObjTrnProvisionalPensionDtlsVO = null;
		List<TrnProvisionalPensionDtls> lListTrnProvisionalPensionDtlsVO = new ArrayList<TrnProvisionalPensionDtls>();

		try {
			String[] lArrStrProvPensionSanctionAuthority = StringUtility.getParameterValues("txtSancAuthName", request);
			String[] lArrStrProvPensionAuthorityNo = StringUtility.getParameterValues("txtSancAuthNo", request);
			String[] lArrStrProvPensionAuthorityDate = StringUtility.getParameterValues("txtSanctionedDate", request);
			String[] lArrStrCommensionDate = StringUtility.getParameterValues("txtApplnFromDate", request);
			String[] lArrStrProvPensionToDate = StringUtility.getParameterValues("txtApplnToDate", request);
			String[] lArrStrBillType = StringUtility.getParameterValues("cmbBillType", request);
			String[] lArrStrBasicPensionAmount = StringUtility.getParameterValues("txtPaidAmount", request);
			String[] lArrStrGratuityVoucherNo = StringUtility.getParameterValues("txtProVoucherNo", request);
			String[] lArrStrGratuityVoucherDate = StringUtility.getParameterValues("txtProVoucherDate", request);
			String[] lArrStrVoucherChargedVotedFlag = StringUtility.getParameterValues("cmbChargedVoted", request);
			String[] lArrStrProvisionalPensionDtlsId = StringUtility.getParameterValues("hdnProvPnsnDtlsId", request);

			setSessionInfo(inputMap);

			String lStrMode = null;
			String lStrCorrectionFlag = null;
			Integer lIntRowNumber = 1;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnProvisionalPensionDtls.class, serv.getSessionFactory());

			if (lArrStrProvPensionSanctionAuthority.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrProvPensionSanctionAuthority.length; lIntCnt++) {
					lObjTrnProvisionalPensionDtlsVO = new TrnProvisionalPensionDtls();
					TrnProvisionalPensionDtls lObjPrvTrnProvisionalPensionDtlsVO = null;

					lObjTrnProvisionalPensionDtlsVO.setDbId(new BigDecimal(gLngDBId));
					lObjTrnProvisionalPensionDtlsVO.setLocationCode(gStrLocId);
					lObjTrnProvisionalPensionDtlsVO.setCreatedDate(new Date());
					lObjTrnProvisionalPensionDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjTrnProvisionalPensionDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));

					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnProvisionalPensionDtlsVO.setUpdatedDate(new Date());
						lObjTrnProvisionalPensionDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjTrnProvisionalPensionDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lArrStrProvisionalPensionDtlsId[lIntCnt] != null && lArrStrProvisionalPensionDtlsId[lIntCnt].trim().length() > 0) {
								lObjPrvTrnProvisionalPensionDtlsVO = (TrnProvisionalPensionDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrProvisionalPensionDtlsId[lIntCnt].trim()));
							}
						}
					}
					if (lArrStrProvPensionSanctionAuthority[lIntCnt] != null && lArrStrProvPensionSanctionAuthority[lIntCnt].trim().length() > 0) {
						lObjTrnProvisionalPensionDtlsVO.setProvPensionSanctionAuthority(lArrStrProvPensionSanctionAuthority[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? "" : lObjPrvTrnProvisionalPensionDtlsVO.getProvPensionSanctionAuthority(),
									lObjTrnProvisionalPensionDtlsVO.getProvPensionSanctionAuthority(), "Provisional Pension Sanction Authority Name", 0);
						}
					}
					if (lArrStrProvPensionAuthorityNo[lIntCnt] != null && lArrStrProvPensionAuthorityNo[lIntCnt].trim().length() > 0) {
						lObjTrnProvisionalPensionDtlsVO.setProvPensionAuthorityNo(lArrStrProvPensionAuthorityNo[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? "" : lObjPrvTrnProvisionalPensionDtlsVO.getProvPensionAuthorityNo(),
									lObjTrnProvisionalPensionDtlsVO.getProvPensionAuthorityNo(), "Provisional Pension Sanction Authority No", 0);
						}
					}
					if (lArrStrProvPensionAuthorityDate[lIntCnt] != null && lArrStrProvPensionAuthorityDate[lIntCnt].trim().length() > 0) {
						lObjTrnProvisionalPensionDtlsVO.setProvPensionAuthorityDate(simpleDateFormat.parse(lArrStrProvPensionAuthorityDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? null : lObjPrvTrnProvisionalPensionDtlsVO.getProvPensionAuthorityDate(),
									lObjTrnProvisionalPensionDtlsVO.getProvPensionAuthorityDate(), "Provisional Pension Sanctioned Date", 0);
						}
					}
					if (lArrStrCommensionDate[lIntCnt] != null && lArrStrCommensionDate[lIntCnt].trim().length() > 0) {
						lObjTrnProvisionalPensionDtlsVO.setCommensionDate(simpleDateFormat.parse(lArrStrCommensionDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? null : lObjPrvTrnProvisionalPensionDtlsVO.getCommensionDate(),
									lObjTrnProvisionalPensionDtlsVO.getCommensionDate(), "Provisional Pension Applicable From Date", 0);
						}
					}
					if (lArrStrProvPensionToDate[lIntCnt] != null && lArrStrProvPensionToDate[lIntCnt].trim().length() > 0) {
						lObjTrnProvisionalPensionDtlsVO.setProvPensionToDate(simpleDateFormat.parse(lArrStrProvPensionToDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? null : lObjPrvTrnProvisionalPensionDtlsVO.getProvPensionToDate(),
									lObjTrnProvisionalPensionDtlsVO.getProvPensionToDate(), "Provisional Pension Applicable To Date", 0);
						}
					}
					if (lArrStrBillType[lIntCnt] != null && lArrStrBillType[lIntCnt].trim().length() > 0) {
						lObjTrnProvisionalPensionDtlsVO.setBillType(lArrStrBillType[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? "" : lObjPrvTrnProvisionalPensionDtlsVO.getBillType(),
									lObjTrnProvisionalPensionDtlsVO.getBillType(), "Provisional Pension Bill Type", 0);
						}
					}
					if (lArrStrBasicPensionAmount[lIntCnt] != null && lArrStrBasicPensionAmount[lIntCnt].trim().length() > 0) {
						lObjTrnProvisionalPensionDtlsVO.setBasicPensionAmount(new BigDecimal(lArrStrBasicPensionAmount[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? null : lObjPrvTrnProvisionalPensionDtlsVO.getBasicPensionAmount(),
									lObjTrnProvisionalPensionDtlsVO.getBasicPensionAmount(), "Provisional Pension Amount", 0);
						}
					}
					if (lArrStrGratuityVoucherNo[lIntCnt] != null && lArrStrGratuityVoucherNo[lIntCnt].trim().length() > 0) {
						lObjTrnProvisionalPensionDtlsVO.setGratuityVoucherNo(lArrStrGratuityVoucherNo[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? "" : lObjPrvTrnProvisionalPensionDtlsVO.getGratuityVoucherNo(),
									lObjTrnProvisionalPensionDtlsVO.getGratuityVoucherNo(), "Provisional Pension Voucher No", 0);
						}
					}
					if (lArrStrGratuityVoucherDate[lIntCnt] != null && lArrStrGratuityVoucherDate[lIntCnt].trim().length() > 0) {
						lObjTrnProvisionalPensionDtlsVO.setGratuityVoucherDate(simpleDateFormat.parse(lArrStrGratuityVoucherDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? null : lObjPrvTrnProvisionalPensionDtlsVO.getGratuityVoucherDate(),
									lObjTrnProvisionalPensionDtlsVO.getGratuityVoucherDate(), "Provisional Pension Voucher Date", 0);
						}
					}
					if (lArrStrVoucherChargedVotedFlag[lIntCnt] != null && !"-1".equals(lArrStrVoucherChargedVotedFlag[lIntCnt].trim())) {
						lObjTrnProvisionalPensionDtlsVO.setVoucherChargedVotedFlag(lArrStrVoucherChargedVotedFlag[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvTrnProvisionalPensionDtlsVO == null ? "" : lObjPrvTrnProvisionalPensionDtlsVO.getVoucherChargedVotedFlag(),
									lObjTrnProvisionalPensionDtlsVO.getVoucherChargedVotedFlag(), "Provisional Pension Charged Voted Flag", 0);
						}
					}
					lIntRowNumber++;
					lListTrnProvisionalPensionDtlsVO.add(lObjTrnProvisionalPensionDtlsVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lListTrnProvisionalPensionDtlsVO;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	// * generateTrnProvisionalPensionDtlsVO(java.util.Map)
	// */
	// public List<TrnProvisionalPensionDtls>
	// generateTrnProvisionalPensionDtlsVO(Map<String, Object> inputMap) throws
	// Exception {
	//
	// HttpServletRequest request = (HttpServletRequest)
	// inputMap.get("requestObj");
	// SimpleDateFormat lObjSimpleDateFormat = new
	// SimpleDateFormat("dd/MM/yyyy");
	//
	// TrnProvisionalPensionDtls lObjTrnProvisionalPensionDtlsVO = null;
	// TrnProvisionalPensionDtls lObjPrvsTrnProvisionalPensionDtlsVO = new
	// TrnProvisionalPensionDtls();
	//
	// try {
	// if (inputMap.containsKey("lObjTrnProvisionalPensionDtlsVO")) {
	// lObjTrnProvisionalPensionDtlsVO = (TrnProvisionalPensionDtls)
	// inputMap.get("lObjTrnProvisionalPensionDtlsVO");
	// lObjPrvsTrnProvisionalPensionDtlsVO = (TrnProvisionalPensionDtls)
	// lObjTrnProvisionalPensionDtlsVO.clone();
	// }
	// else {
	// lObjTrnProvisionalPensionDtlsVO = new TrnProvisionalPensionDtls();
	// }
	// String lStrPpoNo = null;
	// Date lDtCommensionDate = null;
	// BigDecimal lBgDcmlDcrgAmount = null;
	// Date lDtDcrgDate = null;
	// BigDecimal lBgDcmlProvPensionAmount = null;
	// Date lDtPaidDate = null;
	// Date lDtFp1Date = null;
	// BigDecimal lBgDcmlFp1Amount = null;
	// Date lDtFp2Date = null;
	// BigDecimal lBgDcmlFp2Amount = null;
	// BigDecimal lBgDcmlTrnCounter = null;
	// BigDecimal lBgDcmlDbId = null;
	// String lStrLocationCode = null;
	// BigDecimal lBgDcmlPensionRequestId = null;
	// BigDecimal lBgDcmlCvpAmount = null;
	// Date lDtCvpDate = null;
	// Date lDtCvpRestorationDate = null;
	//
	// // ------New fields added
	//
	// String lStrProvisionalPensionFlag = null;
	// Date lDtProvPensionToDate = null;
	// BigDecimal lBgDcmlProvPensionTotalAmountPaid = null;
	// String lStrProvPensionSanctionAuthority = null;
	// String lStrProvPensionAuthorityNo = null;
	// Date lDtProvPensionAuthorityDate = null;
	// String lStrProvGratuityFlag = null;
	// BigDecimal lBgDcmlGratuityAmount= BigDecimal.ZERO;
	// BigDecimal lBgDcmlGratuityActualAmountPaid= BigDecimal.ZERO;
	// Date lDtGratuityPaymentDate = null;
	// String lStrGratuitySanctionAuthority = null;
	// String lStrGratuityAuthorityNo = null;
	// Date lDtGratuityAuthorityDate = null;
	// String lStrGratuityVoucherNo = null;
	// Date lDtGratuityVoucherDate = null;
	// String lStrVoucherChargedVotedFlag = null;
	// String lStrMode = null;
	// String lStrCorrectionFlag=null;
	//
	// setSessionInfo(inputMap);
	// if (inputMap.containsKey("Mode")) {
	// lStrMode = inputMap.get("Mode").toString();
	// }
	// if (inputMap.containsKey("CorrectionFlag")) {
	// lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
	// }
	// lStrProvisionalPensionFlag =
	// ((StringUtility.getParameter("radioProvPnsn", request).trim()).length() >
	// 0) ? StringUtility.getParameter("radioProvPnsn", request).trim() : null;
	// lBgDcmlProvPensionAmount = ((StringUtility.getParameter("txtProvPnsnAmt",
	// request).trim()).length() > 0)
	// ? new BigDecimal(StringUtility.getParameter("txtProvPnsnAmt",
	// request).trim())
	// : null;
	// lDtCommensionDate = ((StringUtility.getParameter("txtProvPnsnFromDate",
	// request).trim()).length() > 0) ?
	// lObjSimpleDateFormat.parse(StringUtility.getParameter("txtProvPnsnFromDate",
	// request).trim()) : null;
	// lDtProvPensionToDate = ((StringUtility.getParameter("txtProvPnsnToDate",
	// request).trim()).length() > 0) ?
	// lObjSimpleDateFormat.parse(StringUtility.getParameter("txtProvPnsnToDate",
	// request).trim()) : null;
	// lBgDcmlProvPensionTotalAmountPaid =
	// ((StringUtility.getParameter("txtProvPnsnTotalAmountPaid",
	// request).trim()).length() > 0) ? new
	// BigDecimal(StringUtility.getParameter(
	// "txtProvPnsnTotalAmountPaid", request).trim()) : null;
	// lStrProvPensionSanctionAuthority =
	// ((StringUtility.getParameter("txtProvPnsnSanctionAuthority",
	// request).trim()).length() > 0) ?
	// StringUtility.getParameter("txtProvPnsnSanctionAuthority",
	// request).trim() : null;
	// lStrProvPensionAuthorityNo =
	// ((StringUtility.getParameter("txtProvPnsnAuthorityNo",
	// request).trim()).length() > 0)
	// ? StringUtility.getParameter("txtProvPnsnAuthorityNo", request).trim()
	// : null;
	// lDtProvPensionAuthorityDate =
	// ((StringUtility.getParameter("txtProvPnsnAuthorityDate",
	// request).trim()).length() > 0) ?
	// lObjSimpleDateFormat.parse(StringUtility.getParameter(
	// "txtProvPnsnAuthorityDate", request).trim()) : null;
	// lStrProvGratuityFlag = ((StringUtility.getParameter("radioProvGratuity",
	// request).trim()).length() > 0) ?
	// StringUtility.getParameter("radioProvGratuity", request).trim() : null;
	// lBgDcmlGratuityAmount = ((StringUtility.getParameter("txtGratuityAmount",
	// request).trim()).length() > 0)
	// ? new BigDecimal(StringUtility.getParameter("txtGratuityAmount",
	// request).trim())
	// : null;
	// lBgDcmlGratuityActualAmountPaid =
	// ((StringUtility.getParameter("txtGratuityActualAmtPaid",
	// request).trim()).length() > 0) ? new
	// BigDecimal(StringUtility.getParameter(
	// "txtGratuityActualAmtPaid", request).trim()) : null;
	// lDtGratuityPaymentDate =
	// ((StringUtility.getParameter("txtGratuityPaymentDate",
	// request).trim()).length() > 0) ?
	// lObjSimpleDateFormat.parse(StringUtility.getParameter(
	// "txtGratuityPaymentDate", request).trim()) : null;
	// lStrGratuitySanctionAuthority =
	// ((StringUtility.getParameter("txtGratuitySanctionAuth",
	// request).trim()).length() > 0) ?
	// StringUtility.getParameter("txtGratuitySanctionAuth", request)
	// .trim() : null;
	// lStrGratuityAuthorityNo =
	// ((StringUtility.getParameter("txtGratuityAuthorityNo",
	// request).trim()).length() > 0)
	// ? StringUtility.getParameter("txtGratuityAuthorityNo", request).trim()
	// : null;
	// lDtGratuityAuthorityDate =
	// ((StringUtility.getParameter("txtGratuityAuthorityDate",
	// request).trim()).length() > 0) ?
	// lObjSimpleDateFormat.parse(StringUtility.getParameter(
	// "txtGratuityAuthorityDate", request).trim()) : null;
	// lStrGratuityVoucherNo =
	// ((StringUtility.getParameter("txtGratuityVoucherNo",
	// request).trim()).length() > 0) ?
	// StringUtility.getParameter("txtGratuityVoucherNo", request).trim() :
	// null;
	// lDtGratuityVoucherDate =
	// ((StringUtility.getParameter("txtGratuityVoucherDate",
	// request).trim()).length() > 0) ?
	// lObjSimpleDateFormat.parse(StringUtility.getParameter(
	// "txtGratuityVoucherDate", request).trim()) : null;
	// lStrVoucherChargedVotedFlag =
	// ((StringUtility.getParameter("radioGratuityChargedVoted",
	// request).trim()).length() > 0) ?
	// StringUtility.getParameter("radioGratuityChargedVoted", request)
	// .trim() : null;
	//
	// lObjTrnProvisionalPensionDtlsVO.setProvisionalPensionFlag(lStrProvisionalPensionFlag);
	// lObjTrnProvisionalPensionDtlsVO.setBasicPensionAmount(lBgDcmlProvPensionAmount);
	// lObjTrnProvisionalPensionDtlsVO.setCommensionDate(lDtCommensionDate);
	// lObjTrnProvisionalPensionDtlsVO.setProvPensionToDate(lDtProvPensionToDate);
	// lObjTrnProvisionalPensionDtlsVO.setProvPensionTotalAmountPaid(lBgDcmlProvPensionTotalAmountPaid);
	// lObjTrnProvisionalPensionDtlsVO.setProvPensionSanctionAuthority(lStrProvPensionSanctionAuthority);
	// lObjTrnProvisionalPensionDtlsVO.setProvPensionAuthorityNo(lStrProvPensionAuthorityNo);
	// lObjTrnProvisionalPensionDtlsVO.setProvPensionAuthorityDate(lDtProvPensionAuthorityDate);
	// lObjTrnProvisionalPensionDtlsVO.setProvGratuityFlag(lStrProvGratuityFlag);
	// lObjTrnProvisionalPensionDtlsVO.setGratuityAmount(lBgDcmlGratuityAmount);
	// lObjTrnProvisionalPensionDtlsVO.setGratuityActualAmountPaid(lBgDcmlGratuityActualAmountPaid);
	// lObjTrnProvisionalPensionDtlsVO.setGratuityPaymentDate(lDtGratuityPaymentDate);
	// lObjTrnProvisionalPensionDtlsVO.setGratuitySanctionAuthority(lStrGratuitySanctionAuthority);
	// lObjTrnProvisionalPensionDtlsVO.setGratuityAuthorityNo(lStrGratuityAuthorityNo);
	// lObjTrnProvisionalPensionDtlsVO.setGratuityAuthorityDate(lDtGratuityAuthorityDate);
	// lObjTrnProvisionalPensionDtlsVO.setGratuityVoucherNo(lStrGratuityVoucherNo);
	// lObjTrnProvisionalPensionDtlsVO.setGratuityVoucherDate(lDtGratuityVoucherDate);
	// lObjTrnProvisionalPensionDtlsVO.setVoucherChargedVotedFlag(lStrVoucherChargedVotedFlag);
	// lObjTrnProvisionalPensionDtlsVO.setLocationCode(gStrLocationCode);
	// lObjTrnProvisionalPensionDtlsVO.setDbId(new BigDecimal(gLngDBId));
	//
	// if ("Add".equalsIgnoreCase(lStrMode)) {
	// lObjTrnProvisionalPensionDtlsVO.setCreatedUserId(new
	// BigDecimal(gLngUserId));
	// lObjTrnProvisionalPensionDtlsVO.setCreatedPostId(new
	// BigDecimal(gLngPostId));
	// lObjTrnProvisionalPensionDtlsVO.setCreatedDate(new Date());
	// }
	// if ("Update".equalsIgnoreCase(lStrMode)) {
	// lObjTrnProvisionalPensionDtlsVO.setUpdatedDate(new Date());
	// lObjTrnProvisionalPensionDtlsVO.setUpdatedPostId(new
	// BigDecimal(gLngPostId));
	// lObjTrnProvisionalPensionDtlsVO.setUpdatedUserId(new
	// BigDecimal(gLngUserId));
	// if("Y".equalsIgnoreCase(lStrCorrectionFlag))
	// {
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnProvisionalPensionDtlsVO.getProvisionalPensionFlag(),
	// lObjTrnProvisionalPensionDtlsVO.getProvisionalPensionFlag())) {
	// generateCorrectionDtlsVO(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getProvisionalPensionFlag(),
	// lObjTrnProvisionalPensionDtlsVO.getProvisionalPensionFlag(),
	// "Provisional Pension Flag", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnProvisionalPensionDtlsVO.getBasicPensionAmount(),
	// lObjTrnProvisionalPensionDtlsVO.getBasicPensionAmount())) {
	// generateCorrectionDtlsVOForAmount(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getBasicPensionAmount(),
	// lObjTrnProvisionalPensionDtlsVO.getBasicPensionAmount(),
	// "Basic Pension Amount", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnProvisionalPensionDtlsVO.getCommensionDate(),
	// lObjTrnProvisionalPensionDtlsVO.getCommensionDate())) {
	// generateCorrectionDtlsVOForDate(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getCommensionDate(),
	// lObjTrnProvisionalPensionDtlsVO.getCommensionDate(),
	// "Provisional Pension From Date", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionToDate(),
	// lObjTrnProvisionalPensionDtlsVO.getProvPensionToDate())) {
	// generateCorrectionDtlsVOForDate(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionToDate(),
	// lObjTrnProvisionalPensionDtlsVO.getProvPensionToDate(),
	// "Provisional Pension To Date", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionTotalAmountPaid(),
	// lObjTrnProvisionalPensionDtlsVO
	// .getProvPensionTotalAmountPaid())) {
	// generateCorrectionDtlsVOForAmount(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionTotalAmountPaid(),
	// lObjTrnProvisionalPensionDtlsVO.getProvPensionTotalAmountPaid(),
	// "Provisional Pension Total Amount", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionSanctionAuthority(),
	// lObjTrnProvisionalPensionDtlsVO.getProvPensionSanctionAuthority())) {
	// generateCorrectionDtlsVO(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionSanctionAuthority(),
	// lObjTrnProvisionalPensionDtlsVO.getProvPensionSanctionAuthority(),
	// "Provisional Pension Sanction Authority", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionAuthorityNo(),
	// lObjTrnProvisionalPensionDtlsVO.getProvPensionAuthorityNo())) {
	// generateCorrectionDtlsVO(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionAuthorityNo(),
	// lObjTrnProvisionalPensionDtlsVO.getProvPensionAuthorityNo(),
	// "Provisional Pension Authority No", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionAuthorityDate(),
	// lObjTrnProvisionalPensionDtlsVO.getProvPensionAuthorityDate())) {
	// generateCorrectionDtlsVOForDate(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getProvPensionAuthorityDate(),
	// lObjTrnProvisionalPensionDtlsVO.getProvPensionAuthorityDate(),
	// "Provisional Pension Authority Date", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnProvisionalPensionDtlsVO.getProvGratuityFlag(),
	// lObjTrnProvisionalPensionDtlsVO.getProvGratuityFlag())) {
	// generateCorrectionDtlsVO(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getProvGratuityFlag(),
	// lObjTrnProvisionalPensionDtlsVO.getProvGratuityFlag(),
	// "Provisional Gratuity Flag", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityAmount(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityAmount())) {
	// generateCorrectionDtlsVOForAmount(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityAmount(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityAmount(), "Gratuity Amount",
	// 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForAmount(lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityActualAmountPaid(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityActualAmountPaid())) {
	// generateCorrectionDtlsVOForAmount(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityActualAmountPaid(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityActualAmountPaid(),
	// "Gratuity Actual Amount Paid", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityPaymentDate(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityPaymentDate())) {
	// generateCorrectionDtlsVOForDate(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityPaymentDate(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityPaymentDate(),
	// "Gratuity Payment Date", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnProvisionalPensionDtlsVO.getGratuitySanctionAuthority(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuitySanctionAuthority())) {
	// generateCorrectionDtlsVO(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getGratuitySanctionAuthority(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuitySanctionAuthority(),
	// "Gratuity Sanction Authority", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityAuthorityNo(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityAuthorityNo())) {
	// generateCorrectionDtlsVO(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityAuthorityNo(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityAuthorityNo(),
	// "Gratuity Authority Number",
	// 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityAuthorityDate(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityAuthorityDate())) {
	// generateCorrectionDtlsVOForDate(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityAuthorityDate(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityAuthorityDate(),
	// "Gratuity Authority Date", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityVoucherNo(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityVoucherNo())) {
	// generateCorrectionDtlsVO(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityVoucherNo(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityVoucherNo(),
	// "Gratuity Voucher Number", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVoForDate(lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityVoucherDate(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityVoucherDate())) {
	// generateCorrectionDtlsVOForDate(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getGratuityVoucherDate(),
	// lObjTrnProvisionalPensionDtlsVO.getGratuityVoucherDate(),
	// "Gratuity Voucher Date", 0);
	// }
	// if (lObjPrvsTrnProvisionalPensionDtlsVO == null
	// ||
	// getVerifyCurrentVoWithPreviousVo(lObjPrvsTrnProvisionalPensionDtlsVO.getVoucherChargedVotedFlag(),
	// lObjTrnProvisionalPensionDtlsVO.getVoucherChargedVotedFlag())) {
	// generateCorrectionDtlsVO(null,
	// lObjPrvsTrnProvisionalPensionDtlsVO.getVoucherChargedVotedFlag(),
	// lObjTrnProvisionalPensionDtlsVO.getVoucherChargedVotedFlag(),
	// "Voucher Charged/Voted Flag", 0);
	// }
	// }
	// }
	// } catch (Exception e) {
	// gLogger.error("Error is :" + e, e);
	// throw e;
	//
	// }
	// return lObjTrnProvisionalPensionDtlsVO;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateTrnPnsnpmntProvisionalVoucherDtlsVO(java.util.Map)
	 */
	// public List<TrnProvisionalVoucherDtls>
	// generateTrnProvisionalVoucherDtlsVO(Map<String, Object> inputMap) throws
	// Exception {
	//
	// HttpServletRequest request = (HttpServletRequest)
	// inputMap.get("requestObj");
	// SimpleDateFormat lObjSimpleDateFormat = new
	// SimpleDateFormat("dd/MM/yyyy");
	// List<TrnProvisionalVoucherDtls> lLstTrnProvisionalVoucherDtlsVO = new
	// ArrayList<TrnProvisionalVoucherDtls>();
	// TrnProvisionalVoucherDtls lObjTrnProvisionalVoucherDtlsVO = new
	// TrnProvisionalVoucherDtls();
	// String lStrPensionerCode=null;
	// String lStrCorrectionFlag=null;
	// try {
	// setSessionInfo(inputMap);
	// String[] lArrStrVoucherMonth =
	// StringUtility.getParameterValues("cmbVoucherMonth", request);
	// String[] lArrStrVoucherYear =
	// StringUtility.getParameterValues("cmbVoucherYear", request);
	// String[] lArrStrVoucherNo =
	// StringUtility.getParameterValues("txtVoucherNo", request);
	// String[] lArrStrVoucherDate =
	// StringUtility.getParameterValues("txtVoucherDate", request);
	// String[] lArrStrProvVoucherDtlsId =
	// StringUtility.getParameterValues("hdnProvVoucherDtlsId", request);
	//
	// String lStrMode = null;
	// Integer lIntRowNumber = 1;
	// if (inputMap.containsKey("Mode")) {
	// lStrMode = inputMap.get("Mode").toString();
	// }
	// if (inputMap.containsKey("PensionerCode")) {
	// lStrPensionerCode=inputMap.get("PensionerCode").toString();
	// }
	// if (inputMap.containsKey("CorrectionFlag")) {
	// lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
	// }
	// PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new
	// PhysicalCaseInwardDAOImpl(
	// TrnProvisionalVoucherDtls.class, serv.getSessionFactory());
	// if (lArrStrVoucherMonth.length > 0) {
	// for (Integer lIntCnt = 0; lIntCnt < lArrStrVoucherMonth.length;
	// lIntCnt++) {
	// lObjTrnProvisionalVoucherDtlsVO = new TrnProvisionalVoucherDtls();
	// TrnProvisionalVoucherDtls lObjPrvsTrnProvisionalVoucherDtlsVO=null;
	// lObjTrnProvisionalVoucherDtlsVO.setDbId(gLngDBId);
	// lObjTrnProvisionalVoucherDtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));
	// lObjTrnProvisionalVoucherDtlsVO.setCreatedUserId(gLngUserId);
	// lObjTrnProvisionalVoucherDtlsVO.setCreatedPostId(gLngPostId);
	// lObjTrnProvisionalVoucherDtlsVO.setCreatedDate(new Date());
	// if ("Update".equalsIgnoreCase(lStrMode)) {
	// lObjTrnProvisionalVoucherDtlsVO.setUpdatedDate(new Date());
	// lObjTrnProvisionalVoucherDtlsVO.setUpdatedPostId(gLngPostId);
	// lObjTrnProvisionalVoucherDtlsVO.setUpdatedUserId(gLngUserId);
	// if("Y".equalsIgnoreCase(lStrCorrectionFlag))
	// {
	// if(lArrStrProvVoucherDtlsId[lIntCnt] !=null &&
	// lArrStrProvVoucherDtlsId[lIntCnt].trim().length() > 0)
	// lObjPrvsTrnProvisionalVoucherDtlsVO=(TrnProvisionalVoucherDtls)
	// lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrProvVoucherDtlsId[lIntCnt].trim()));
	// }
	// }
	// if (!lArrStrVoucherMonth[lIntCnt].equalsIgnoreCase("-1")) {
	// lObjTrnProvisionalVoucherDtlsVO.setVoucherMonth(lArrStrVoucherMonth[lIntCnt].trim());
	// if ("Update".equalsIgnoreCase(lStrMode) &&
	// "Y".equalsIgnoreCase(lStrCorrectionFlag))
	// {
	// generateCorrectionDtlsVO(lIntRowNumber,
	// lObjPrvsTrnProvisionalVoucherDtlsVO == null ? "" :
	// lObjPrvsTrnProvisionalVoucherDtlsVO.getVoucherMonth(),
	// lObjTrnProvisionalVoucherDtlsVO.getVoucherMonth(),
	// "Provisional Voucher Month", 0);
	// }
	// }
	// if (!lArrStrVoucherYear[lIntCnt].equalsIgnoreCase("-1")) {
	// lObjTrnProvisionalVoucherDtlsVO.setVoucherYear(Long.parseLong(lArrStrVoucherYear[lIntCnt].trim()));
	// if ("Update".equalsIgnoreCase(lStrMode) &&
	// "Y".equalsIgnoreCase(lStrCorrectionFlag))
	// {
	// generateCorrectionDtlsVO(lIntRowNumber,
	// lObjPrvsTrnProvisionalVoucherDtlsVO == null ? "" :
	// lObjPrvsTrnProvisionalVoucherDtlsVO.getVoucherYear(),
	// lObjTrnProvisionalVoucherDtlsVO.getVoucherYear(),
	// "Provisional Voucher Year", 0);
	// }
	// }
	// if (lArrStrVoucherNo[lIntCnt] != null &&
	// lArrStrVoucherNo[lIntCnt].trim().length() > 0) {
	// lObjTrnProvisionalVoucherDtlsVO.setVoucherNo(lArrStrVoucherNo[lIntCnt].trim());
	// if ("Update".equalsIgnoreCase(lStrMode) &&
	// "Y".equalsIgnoreCase(lStrCorrectionFlag))
	// {
	// generateCorrectionDtlsVO(lIntRowNumber,
	// lObjPrvsTrnProvisionalVoucherDtlsVO == null ? "" :
	// lObjPrvsTrnProvisionalVoucherDtlsVO.getVoucherNo(),
	// lObjTrnProvisionalVoucherDtlsVO.getVoucherNo(),
	// "Provisional Voucher Number", 0);
	// }
	// }
	// if (lArrStrVoucherDate[lIntCnt] != null &&
	// lArrStrVoucherDate[lIntCnt].trim().length() > 0) {
	// lObjTrnProvisionalVoucherDtlsVO.setVoucherDate(lObjSimpleDateFormat.parse(lArrStrVoucherDate[lIntCnt].trim()));
	// if ("Update".equalsIgnoreCase(lStrMode) &&
	// "Y".equalsIgnoreCase(lStrCorrectionFlag))
	// {
	// generateCorrectionDtlsVOForDate(lIntRowNumber,
	// lObjPrvsTrnProvisionalVoucherDtlsVO == null ? null :
	// lObjPrvsTrnProvisionalVoucherDtlsVO.getVoucherDate(),
	// lObjTrnProvisionalVoucherDtlsVO.getVoucherDate(),"Provisional Voucher Date",
	// 0);
	// }
	// }
	// lIntRowNumber++;
	// lLstTrnProvisionalVoucherDtlsVO.add(lObjTrnProvisionalVoucherDtlsVO);
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// gLogger.error("Error is :" + e, e);
	// throw e;
	//
	// }
	// return lLstTrnProvisionalVoucherDtlsVO;
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateMstPensionerFamilyDtlsVO(java.util.Map)
	 */
	public List<MstPensionerFamilyDtls> generateMstPensionerFamilyDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<MstPensionerFamilyDtls> lLstMstPensionerFamilyDtlsVO = new ArrayList<MstPensionerFamilyDtls>();
		MstPensionerFamilyDtls lObjMstPensionerFamilyDtlsVO = new MstPensionerFamilyDtls();
		String lStrPensionerCode = null;
		try {
			setSessionInfo(inputMap);
			String[] lArrStrName = StringUtility.getParameterValues("txtFMName", request);
			String[] lArrStrRelationship = StringUtility.getParameterValues("cmbRelation", request);
			String[] lArrStrGender = StringUtility.getParameterValues("cmbFamilyMemberGender", request);
			String[] lArrStrDateOfBirth = StringUtility.getParameterValues("txtFMDateOfBirth", request);
			String[] lArrStrDateOfDeath = StringUtility.getParameterValues("txtFMDateOfDeath", request);
			String[] lArrStrMinorFlag = StringUtility.getParameterValues("chkMinorVar", request);
			String[] lArrStrSalary = StringUtility.getParameterValues("txtFMSalary", request);
			String[] lArrStrHandicapeFlag = StringUtility.getParameterValues("cmbPhyHandicap", request);
			String[] lArrStrGuardianName = StringUtility.getParameterValues("txtFMGuardianName", request);
			String[] lArrStrPercentage = StringUtility.getParameterValues("isFamilyMember", request);
			String[] lArrStrMarriedFlag = StringUtility.getParameterValues("chkMarriedVar", request);
			String[] lArrStrFamilyMemberId = StringUtility.getParameterValues("hdnFamilyMemberId", request);
			String lStrMode = null;
			String lStrCorrectionFlag = null;
			Integer lIntRowNumber = 1;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("PensionerCode")) {
				lStrPensionerCode = inputMap.get("PensionerCode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerFamilyDtls.class, serv.getSessionFactory());
			if (lArrStrName.length > 0) {
				for (Integer lIntCnt = 0; lIntCnt < lArrStrName.length; lIntCnt++) {
					lObjMstPensionerFamilyDtlsVO = new MstPensionerFamilyDtls();
					MstPensionerFamilyDtls lObjPrvsMstPensionerFamilyDtlsVO = null;
					lObjMstPensionerFamilyDtlsVO.setCreatedUserId(gLngUserId);
					lObjMstPensionerFamilyDtlsVO.setCreatedPostId(gLngPostId);
					lObjMstPensionerFamilyDtlsVO.setCreatedDate(new Date());
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjMstPensionerFamilyDtlsVO.setUpdatedDate(new Date());
						lObjMstPensionerFamilyDtlsVO.setUpdatedPostId(gLngPostId);
						lObjMstPensionerFamilyDtlsVO.setUpdatedUserId(gLngUserId);
						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lArrStrFamilyMemberId[lIntCnt] != null && lArrStrFamilyMemberId[lIntCnt].trim().length() > 0) {
								lObjPrvsMstPensionerFamilyDtlsVO = (MstPensionerFamilyDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrFamilyMemberId[lIntCnt].trim()));
							}
						}
					}
					if (lArrStrName[lIntCnt] != null && lArrStrName[lIntCnt].trim().length() > 0) {
						lObjMstPensionerFamilyDtlsVO.setName(lArrStrName[lIntCnt].trim().toUpperCase());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? "" : lObjPrvsMstPensionerFamilyDtlsVO.getName(), lObjMstPensionerFamilyDtlsVO.getName(),
									"Full Name of Family Member", 0);
						}
					}
					if (!lArrStrRelationship[lIntCnt].equalsIgnoreCase("-1") && !"".equals(lArrStrRelationship[lIntCnt])) {
						lObjMstPensionerFamilyDtlsVO.setRelationship(lArrStrRelationship[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? "" : lObjPrvsMstPensionerFamilyDtlsVO.getRelationship(),
									lObjMstPensionerFamilyDtlsVO.getRelationship(), "Family Member RelationShip", 0);
						}
					} else {
						lObjMstPensionerFamilyDtlsVO.setRelationship(null);
					}
					if (!lArrStrGender[lIntCnt].equalsIgnoreCase("-1") && !"".equals(lArrStrGender[lIntCnt])) {
						lObjMstPensionerFamilyDtlsVO.setGender(lArrStrGender[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? "" : lObjPrvsMstPensionerFamilyDtlsVO.getGender(),
									lObjMstPensionerFamilyDtlsVO.getGender(), "Family Member Gender", 0);
						}
					} else {
						lObjMstPensionerFamilyDtlsVO.setGender(null);
					}
					if (lArrStrDateOfBirth[lIntCnt] != null && lArrStrDateOfBirth[lIntCnt].trim().length() > 0) {
						lObjMstPensionerFamilyDtlsVO.setDateOfBirth(lObjSimpleDateFormat.parse(lArrStrDateOfBirth[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? null : lObjPrvsMstPensionerFamilyDtlsVO.getDateOfBirth(),
									lObjMstPensionerFamilyDtlsVO.getDateOfBirth(), "Family Member Date Of Birth", 0);
						}
					}
					if (lArrStrDateOfDeath[lIntCnt] != null && lArrStrDateOfDeath[lIntCnt].trim().length() > 0) {
						lObjMstPensionerFamilyDtlsVO.setDateOfDeath(lObjSimpleDateFormat.parse(lArrStrDateOfDeath[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? null : lObjPrvsMstPensionerFamilyDtlsVO.getDateOfDeath(),
									lObjMstPensionerFamilyDtlsVO.getDateOfDeath(), "Family Member Date Of Death", 0);
						}
					}
					if (lArrStrMinorFlag[lIntCnt] != null && lArrStrMinorFlag[lIntCnt].length() > 0) {
						lObjMstPensionerFamilyDtlsVO.setMajorFlag("N");
					} else {
						lObjMstPensionerFamilyDtlsVO.setMajorFlag("Y");
					}
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? "" : ("Y".equals(lObjPrvsMstPensionerFamilyDtlsVO.getMajorFlag()) ? "N" : "Y"),
								("Y".equals(lObjMstPensionerFamilyDtlsVO.getMajorFlag()) ? "N" : "Y"), "Family Member Minor Flag", 0);
					}
					if (lArrStrSalary[lIntCnt] != null && lArrStrSalary[lIntCnt].trim().length() > 0) {
						lObjMstPensionerFamilyDtlsVO.setSalary(new BigDecimal(lArrStrSalary[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? null : lObjPrvsMstPensionerFamilyDtlsVO.getSalary(),
									lObjMstPensionerFamilyDtlsVO.getSalary(), "Family Member Salary", 0);
						}
					}
					if (!lArrStrHandicapeFlag[lIntCnt].equalsIgnoreCase("-1")) {
						lObjMstPensionerFamilyDtlsVO.setHandicapeFlag(lArrStrHandicapeFlag[lIntCnt].trim());
					} else {
						lObjMstPensionerFamilyDtlsVO.setHandicapeFlag("N");
					}
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						if (!"N".equals(lObjMstPensionerFamilyDtlsVO.getHandicapeFlag())) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? "" : lObjPrvsMstPensionerFamilyDtlsVO.getHandicapeFlag(),
									lObjMstPensionerFamilyDtlsVO.getHandicapeFlag(), "Family Member Handicap", 0);
						}
					}

					if (lArrStrGuardianName[lIntCnt] != null && lArrStrGuardianName[lIntCnt].trim().length() > 0) {
						lObjMstPensionerFamilyDtlsVO.setGuardianName(lArrStrGuardianName[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? "" : lObjPrvsMstPensionerFamilyDtlsVO.getGuardianName(),
									lObjMstPensionerFamilyDtlsVO.getGuardianName(), "Family Member Gaurdian Name", 0);
						}
					}
					if (lArrStrPercentage[lIntCnt] != null && lArrStrPercentage[lIntCnt].length() > 0) {
						lObjMstPensionerFamilyDtlsVO.setPercentage(100L);
					} else {
						lObjMstPensionerFamilyDtlsVO.setPercentage(0L);
					}
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? "" : ((lObjPrvsMstPensionerFamilyDtlsVO.getPercentage() == 100L) ? "Y" : "N"),
								((lObjMstPensionerFamilyDtlsVO.getPercentage() == 100L) ? "Y" : "N"), "Family Member", 0);
					}
//					if (lArrStrPercentage[lIntCnt] != null && lArrStrPercentage[lIntCnt].trim().length() > 0) {
//						lObjMstPensionerFamilyDtlsVO.setPercentage(Long.parseLong(lArrStrPercentage[lIntCnt].trim()));
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? "" : lObjPrvsMstPensionerFamilyDtlsVO.getPercentage(),
//									lObjMstPensionerFamilyDtlsVO.getPercentage(), "Family Member Percentage", 0);
//						}
//					}
					if (lArrStrMarriedFlag[lIntCnt] != null && lArrStrMarriedFlag[lIntCnt].length() > 0) {
						lObjMstPensionerFamilyDtlsVO.setMarriedFlag("Y");
					} else {
						lObjMstPensionerFamilyDtlsVO.setMarriedFlag("N");
					}
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						if (!"N".equals(lObjMstPensionerFamilyDtlsVO.getMarriedFlag())) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerFamilyDtlsVO == null ? "" : lObjPrvsMstPensionerFamilyDtlsVO.getMarriedFlag(),
									lObjMstPensionerFamilyDtlsVO.getMarriedFlag(), "Family Member Re-Married", 0);
						}
					}
					lIntRowNumber++;
					lLstMstPensionerFamilyDtlsVO.add(lObjMstPensionerFamilyDtlsVO);
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}

		return lLstMstPensionerFamilyDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateMstPensionerNomineeDtlsVO(java.util.Map)
	 */
	public List<MstPensionerNomineeDtls> generateMstPensionerNomineeDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<MstPensionerNomineeDtls> lLstMstPensionerNomineeDtlsVO = new ArrayList<MstPensionerNomineeDtls>();
		MstPensionerNomineeDtls lObjMstPensionerNomineeDtlsVO = new MstPensionerNomineeDtls();
		String lStrPensionerCode = null;
		try {
			setSessionInfo(inputMap);
			String[] lArrStrNomName = StringUtility.getParameterValues("txtNomineeName", request);
			String[] lArrStrNomRelation = StringUtility.getParameterValues("cmbNomRelation", request);
			String[] lArrStrNomDateOfBirth = StringUtility.getParameterValues("txtNomDateOfBirth", request);
			String[] lArrStrPercent = StringUtility.getParameterValues("txtNomPercentage", request);
			String[] lArrStrBankCode = StringUtility.getParameterValues("cmbNomBank", request);
			String[] lArrStrBranchCode = StringUtility.getParameterValues("cmbNomBankBranch", request);
			String[] lArrStrAccountNo = StringUtility.getParameterValues("txtNomAccountNo", request);
			String[] lArrStrNomineeId = StringUtility.getParameterValues("hdnNomineeId", request);
			String lStrMode = null;
			String lStrCorrectionFlag = null;
			Integer lIntRowNumber = 1;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("PensionerCode")) {
				lStrPensionerCode = inputMap.get("PensionerCode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerNomineeDtls.class, serv.getSessionFactory());
			if (lArrStrNomName.length > 0) {
				for (Integer lIntCnt = 0; lIntCnt < lArrStrNomName.length; lIntCnt++) {
					lObjMstPensionerNomineeDtlsVO = new MstPensionerNomineeDtls();
					MstPensionerNomineeDtls lObjPrvsMstPensionerNomineeDtlsVO = null;
					lObjMstPensionerNomineeDtlsVO.setLocationCode(gStrLocationCode);
					lObjMstPensionerNomineeDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjMstPensionerNomineeDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjMstPensionerNomineeDtlsVO.setCreatedDate(new Date());
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjMstPensionerNomineeDtlsVO.setUpdatedDate(new Date());
						lObjMstPensionerNomineeDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjMstPensionerNomineeDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lArrStrNomineeId[lIntCnt] != null && lArrStrNomineeId[lIntCnt].trim().length() > 0) {
								lObjPrvsMstPensionerNomineeDtlsVO = (MstPensionerNomineeDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrNomineeId[lIntCnt].trim()));
							}
						}
					}
					if (lArrStrNomName[lIntCnt] != null && lArrStrNomName[lIntCnt].trim().length() > 0) {
						lObjMstPensionerNomineeDtlsVO.setName(lArrStrNomName[lIntCnt].trim().toUpperCase());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerNomineeDtlsVO == null ? "" : lObjPrvsMstPensionerNomineeDtlsVO.getName(),
									lObjMstPensionerNomineeDtlsVO.getName(), "Nominee Name", 0);
						}
					}
					if (!lArrStrNomRelation[lIntCnt].equalsIgnoreCase("-1") && !"".equals(lArrStrNomRelation[lIntCnt])) {
						lObjMstPensionerNomineeDtlsVO.setRelationship(lArrStrNomRelation[lIntCnt]);
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerNomineeDtlsVO == null ? "" : lObjPrvsMstPensionerNomineeDtlsVO.getRelationship(),
									lObjMstPensionerNomineeDtlsVO.getRelationship(), "Nominee Relationship", 0);
						}
					}
					if (lArrStrNomDateOfBirth[lIntCnt] != null && lArrStrNomDateOfBirth[lIntCnt].trim().length() > 0) {
						lObjMstPensionerNomineeDtlsVO.setDateOfBirth(lObjSimpleDateFormat.parse(lArrStrNomDateOfBirth[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsMstPensionerNomineeDtlsVO == null ? null : lObjPrvsMstPensionerNomineeDtlsVO.getDateOfBirth(),
									lObjMstPensionerNomineeDtlsVO.getDateOfBirth(), "Nominee Date of Birth", 0);
						}
					}
					if (lArrStrPercent[lIntCnt] != null && lArrStrPercent[lIntCnt].trim().length() > 0) {
						lObjMstPensionerNomineeDtlsVO.setPercent(new BigDecimal(lArrStrPercent[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerNomineeDtlsVO == null ? "" : lObjPrvsMstPensionerNomineeDtlsVO.getPercent(),
									lObjMstPensionerNomineeDtlsVO.getPercent(), "Nominee Percentage", 0);
						}
					}
					if (!lArrStrBankCode[lIntCnt].equalsIgnoreCase("-1") && !"".equals(lArrStrBankCode[lIntCnt])) {
						lObjMstPensionerNomineeDtlsVO.setBankCode(lArrStrBankCode[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerNomineeDtlsVO == null ? "" : lObjPrvsMstPensionerNomineeDtlsVO.getBankCode(),
									lObjMstPensionerNomineeDtlsVO.getBankCode(), "Nominee Bank", 0);
						}
					}
					if (!lArrStrBranchCode[lIntCnt].equalsIgnoreCase("-1") && !"".equals(lArrStrBranchCode[lIntCnt])) {
						lObjMstPensionerNomineeDtlsVO.setBranchCode(Long.valueOf(lArrStrBranchCode[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerNomineeDtlsVO == null ? "" : lObjPrvsMstPensionerNomineeDtlsVO.getBranchCode(),
									lObjMstPensionerNomineeDtlsVO.getBranchCode(), "Nominee Branch", 0);
						}
					}
					if (lArrStrAccountNo[lIntCnt] != null && lArrStrAccountNo[lIntCnt].trim().length() > 0) {
						lObjMstPensionerNomineeDtlsVO.setAccountNo(lArrStrAccountNo[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsMstPensionerNomineeDtlsVO == null ? "" : lObjPrvsMstPensionerNomineeDtlsVO.getAccountNo(),
									lObjMstPensionerNomineeDtlsVO.getAccountNo(), "Nominee Account Number", 0);
						}
					}
					lIntRowNumber++;
					lLstMstPensionerNomineeDtlsVO.add(lObjMstPensionerNomineeDtlsVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lLstMstPensionerNomineeDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateTrnPensionRecoveryDtlsVO(java.util.Map)
	 */
	public List<TrnPensionRecoveryDtls> generateTrnPensionRecoveryDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsVO = new ArrayList<TrnPensionRecoveryDtls>();
		TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
		try {
			setSessionInfo(inputMap);
			String lStrStartMonthYear = null;
			String lStrEndMonthYear = null;
			String lStrOverPaymentAmt = null;
			String lStrArrearOfLicenseFee = null;
			String lStrLicenseFeeForGovt = null;
			String lStrSchemeCode = null;
			String[] lArrStrMajorHead = null;
			String[] lArrStrRecoveryType = null;
			String[] lArrStrNature = null;
			String[] lArrStrAmount = null;
			String[] lArrStrStartMonth = null;
			String[] lArrStrStartYear = null;
			String[] lArrStrEndMonth = null;
			String[] lArrStrEndYear = null;
			String[] lArrStrNoOfInstallments = null;
			String[] lArrStrSchemeCode = null;
			String[] lArrStrTotalAmtRecovered = null;
			String lStrMode = null;
			String lStrPensionerCode = null;
			String lStrYear = null;
			String lStrMonth = null;
			String lStrMonthYear = null;
			String lStrCorrectionFlag = null;
			Integer lIntRowNumber = null;
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("PensionerCode")) {
				lStrPensionerCode = inputMap.get("PensionerCode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());

			lArrStrNature = StringUtility.getParameterValues("txtAdvNature", request);
			lArrStrMajorHead = StringUtility.getParameterValues("txtAdvMajorHead", request);
			lArrStrAmount = StringUtility.getParameterValues("txtAdvanceAmount", request);
			lArrStrStartMonth = StringUtility.getParameterValues("cmbAdvanceStartMonth", request);
			lArrStrStartYear = StringUtility.getParameterValues("cmbAdvanceStartYear", request);
			lArrStrEndMonth = StringUtility.getParameterValues("cmbAdvanceEndMonth", request);
			lArrStrEndYear = StringUtility.getParameterValues("cmbAdvanceEndYear", request);
			lArrStrNoOfInstallments = StringUtility.getParameterValues("txtNoOfInstallment", request);
			lArrStrSchemeCode = StringUtility.getParameterValues("cmbAdvSchemeCode", request);
			lArrStrTotalAmtRecovered = StringUtility.getParameterValues("txtTotalAmtRecovered", request);
			String[] lArrStrAdvRecoveryDtlId = StringUtility.getParameterValues("hdnAdvRecoveryDtlsId", request);

			if (lArrStrMajorHead.length > 0) {
				lIntRowNumber = 1;
				for (Integer lIntCnt = 0; lIntCnt < lArrStrMajorHead.length; lIntCnt++) {
			
					lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
					lStrYear = null;
					lStrMonth = null;
					TrnPensionRecoveryDtls lObjPrvsTrnPensionRecoveryDtlsVO = null;
					lObjTrnPensionRecoveryDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjTrnPensionRecoveryDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjTrnPensionRecoveryDtlsVO.setCreatedDate(new Date());
					lObjTrnPensionRecoveryDtlsVO.setRecoveryFromFlag(gObjRsrcBndle.getString("PPMT.MONTHLY"));
					lObjTrnPensionRecoveryDtlsVO.setRecoveryType(gObjRsrcBndle.getString("PPMT.ADVANCE"));
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPensionRecoveryDtlsVO.setUpdatedDate(new Date());
						lObjTrnPensionRecoveryDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionRecoveryDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lArrStrAdvRecoveryDtlId[lIntCnt] != null && lArrStrAdvRecoveryDtlId[lIntCnt].trim().length() > 0) {
								lObjPrvsTrnPensionRecoveryDtlsVO = (TrnPensionRecoveryDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrAdvRecoveryDtlId[lIntCnt].trim()));						
							}
						}
					}
					if (!"".equals(lArrStrNature[lIntCnt])) {
						lObjTrnPensionRecoveryDtlsVO.setNature(lArrStrNature[lIntCnt].trim());
							if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
								generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getNature(),
										lObjTrnPensionRecoveryDtlsVO.getNature(), "Any Other Recovery Details Nature", 0);
							}
					}
					if (!"".equals(lArrStrMajorHead[lIntCnt])) {
						lObjTrnPensionRecoveryDtlsVO.setMjrhdCode(lArrStrMajorHead[lIntCnt].trim());
							if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
								generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getMjrhdCode(),
										lObjTrnPensionRecoveryDtlsVO.getMjrhdCode(), "Any Other Recovery Details Major Head", 0);
							}
						}

//					if (!lArrStrRecoveryType[lIntCnt].equalsIgnoreCase("-1")) {
//						lObjTrnPensionRecoveryDtlsVO.setRecoveryType(lArrStrRecoveryType[lIntCnt].trim());
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getRecoveryType(),
//									lObjTrnPensionRecoveryDtlsVO.getRecoveryType(), "Advance Detail Recovery Type", 0);
//						}
//					}
					if (lArrStrTotalAmtRecovered[lIntCnt] != null && lArrStrTotalAmtRecovered[lIntCnt].trim().length() > 0) {
						lObjTrnPensionRecoveryDtlsVO.setTotalRecoveryAmt(new BigDecimal(lArrStrTotalAmtRecovered[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getTotalRecoveryAmt(),
									lObjTrnPensionRecoveryDtlsVO.getTotalRecoveryAmt(), "Any Other Recovery Details Total Amount to be recovered", 0);
						}
					}
					if (lArrStrAmount[lIntCnt] != null && lArrStrAmount[lIntCnt].trim().length() > 0) {
						lObjTrnPensionRecoveryDtlsVO.setAmount(new BigDecimal(lArrStrAmount[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getAmount(),
									lObjTrnPensionRecoveryDtlsVO.getAmount(), "Any Other Recovery Details Monthly Recovery Amount", 0);
						}
					}
					if (!lArrStrStartYear[lIntCnt].equalsIgnoreCase("-1") && !lArrStrStartMonth[lIntCnt].equalsIgnoreCase("-1")) {
						lStrStartMonthYear = lArrStrStartYear[lIntCnt].trim();
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lObjPrvsTrnPensionRecoveryDtlsVO != null) {
								if (lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth() != null) {
									lStrMonthYear = lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth().toString();
									lStrYear = lStrMonthYear.substring(0, 4);
									lStrMonth = lStrMonthYear.substring(4, 6);
									lStrMonth = Integer.valueOf(lStrMonth).toString();
								}
							} else {
								lStrYear = null;
								lStrMonth = null;
							}
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrMonth, lArrStrStartMonth[lIntCnt].toString(), "Any Other Recovery Details Start Month", 0);
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrYear, lArrStrStartYear[lIntCnt].toString(), "Any Other Recovery Details Start Year", 0);

						}
						if (Integer.parseInt(lArrStrStartMonth[lIntCnt]) < 10) {
							lStrStartMonthYear = lStrStartMonthYear + "0" + lArrStrStartMonth[lIntCnt].trim();
						} else {
							lStrStartMonthYear = lStrStartMonthYear + lArrStrStartMonth[lIntCnt].trim();
						}
						lObjTrnPensionRecoveryDtlsVO.setFromMonth(Integer.parseInt(lStrStartMonthYear));
					}
					if (!lArrStrEndMonth[lIntCnt].equalsIgnoreCase("-1") && !lArrStrEndYear[lIntCnt].equalsIgnoreCase("-1")) {
						lStrEndMonthYear = lArrStrEndYear[lIntCnt].trim();
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lObjPrvsTrnPensionRecoveryDtlsVO != null) {
								if (lObjPrvsTrnPensionRecoveryDtlsVO.getToMonth() != null) {
									lStrMonthYear = lObjPrvsTrnPensionRecoveryDtlsVO.getToMonth().toString();
									lStrYear = lStrMonthYear.substring(0, 4);
									lStrMonth = lStrMonthYear.substring(4, 6);
									lStrMonth = Integer.valueOf(lStrMonth).toString();
								}
							} else {
								lStrYear = null;
								lStrMonth = null;
							}
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrMonth, lArrStrEndMonth[lIntCnt].toString(), "Any Other Recovery Details End Month", 0);
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrYear, lArrStrEndYear[lIntCnt].toString(), "Any Other Recovery Details End Year", 0);

						}

						if (Integer.parseInt(lArrStrEndMonth[lIntCnt]) < 10) {
							lStrEndMonthYear = lStrEndMonthYear + "0" + lArrStrEndMonth[lIntCnt].trim();
						} else {
							lStrEndMonthYear = lStrEndMonthYear + lArrStrEndMonth[lIntCnt].trim();
						}
						lObjTrnPensionRecoveryDtlsVO.setToMonth(Integer.parseInt(lStrEndMonthYear));
					}
					if (lArrStrNoOfInstallments[lIntCnt] != null && lArrStrNoOfInstallments[lIntCnt].trim().length() > 0) {
						lObjTrnPensionRecoveryDtlsVO.setNoOfInstallments(Integer.parseInt(lArrStrNoOfInstallments[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getNoOfInstallments(),
									lObjTrnPensionRecoveryDtlsVO.getNoOfInstallments(), "Any Other Recovery Details No of Installments", 0);
						}
					}
					if (lArrStrSchemeCode[lIntCnt] != "-1" && lArrStrSchemeCode[lIntCnt].trim().length() > 0) {
						lObjTrnPensionRecoveryDtlsVO.setSchemeCode(lArrStrSchemeCode[lIntCnt]);
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getSchemeCode(),
									lObjTrnPensionRecoveryDtlsVO.getSchemeCode(), "Any Other Recovery Details Scheme code", 0);
						}
					}
					lIntRowNumber++;
					lLstTrnPensionRecoveryDtlsVO.add(lObjTrnPensionRecoveryDtlsVO);
					
				}
			}
//			lStrOverPaymentAmt = ((StringUtility.getParameter("txtOverPaymentAmt", request).trim()).length() > 0) ? StringUtility.getParameter("txtOverPaymentAmt", request).trim() : null;
//
//			if (lStrOverPaymentAmt != null) {
//				if (Double.parseDouble(lStrOverPaymentAmt) > 0) {
//					lStrYear = null;
//					lStrMonth = null;
//					lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
//					TrnPensionRecoveryDtls lObjPrvsTrnPensionRecoveryDtlsVO = null;
//					lObjTrnPensionRecoveryDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
//					lObjTrnPensionRecoveryDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
//					lObjTrnPensionRecoveryDtlsVO.setCreatedDate(new Date());
//					lObjTrnPensionRecoveryDtlsVO.setRecoveryFromFlag(gObjRsrcBndle.getString("PPMT.MONTHLY"));
//					if ("Update".equalsIgnoreCase(lStrMode)) {
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedDate(new Date());
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
//						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							if (lStrPensionerCode != null) {
//								lObjPrvsTrnPensionRecoveryDtlsVO = lObjPhysicalCaseInwardDAO.getRecoveryDtlsFromRecoveryType(lStrPensionerCode, gObjRsrcBndle.getString("PPMT.OVERPAY"));
//							}
//						}
//					}
//
//					lObjTrnPensionRecoveryDtlsVO.setRecoveryType(gObjRsrcBndle.getString("PPMT.OVERPAY"));
//					lObjTrnPensionRecoveryDtlsVO.setAmount(new BigDecimal(lStrOverPaymentAmt));
//					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getAmount(),
//								lObjTrnPensionRecoveryDtlsVO.getAmount(), "Over Payment Amount", 0);
//					}
//					if (!StringUtility.getParameter("cmbOverPayFromMonth", request).equals("-1") && !StringUtility.getParameter("cmbOverPayFromYear", request).equals("-1")) {
//						lStrStartMonthYear = StringUtility.getParameter("cmbOverPayFromYear", request).trim();
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							if (lObjPrvsTrnPensionRecoveryDtlsVO != null) {
//								if (lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth() != null) {
//									lStrMonthYear = lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth().toString();
//									lStrYear = lStrMonthYear.substring(0, 4);
//									lStrMonth = lStrMonthYear.substring(4, 6);
//									lStrMonth = Integer.valueOf(lStrMonth).toString();
//								}
//							} else {
//								lStrYear = null;
//								lStrMonth = null;
//							}
//							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrMonth, StringUtility.getParameter("cmbOverPayFromMonth", request).trim(),
//									"Over Payment For Month", 0);
//							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrYear, lStrStartMonthYear, "Over Payment For Year", 0);
//
//						}
//						if (Integer.parseInt(StringUtility.getParameter("cmbOverPayFromMonth", request)) < 10) {
//							lStrStartMonthYear = lStrStartMonthYear + "0" + StringUtility.getParameter("cmbOverPayFromMonth", request).trim();
//						} else {
//							lStrStartMonthYear = lStrStartMonthYear + StringUtility.getParameter("cmbOverPayFromMonth", request).trim();
//						}
//						lObjTrnPensionRecoveryDtlsVO.setFromMonth(Integer.parseInt(lStrStartMonthYear));
//						lObjTrnPensionRecoveryDtlsVO.setToMonth(Integer.parseInt(lStrStartMonthYear));
//					}
//					lStrSchemeCode = StringUtility.getParameter("txtOverPaySchemeCode", request);
//					lObjTrnPensionRecoveryDtlsVO.setSchemeCode(lStrSchemeCode);
//					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getSchemeCode(),
//								lObjTrnPensionRecoveryDtlsVO.getSchemeCode(), "Over Payment Scheme Code", 0);
//					}
//					lLstTrnPensionRecoveryDtlsVO.add(lObjTrnPensionRecoveryDtlsVO);
//				}
//			}
//			lStrArrearOfLicenseFee = ((StringUtility.getParameter("txtArrearOfLicenseFee", request).trim()).length() > 0) ? StringUtility.getParameter("txtArrearOfLicenseFee", request).trim() : null;
//			if (lStrArrearOfLicenseFee != null) {
//				if (Double.parseDouble(lStrArrearOfLicenseFee) > 0) {
//					lStrYear = null;
//					lStrMonth = null;
//					lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
//					TrnPensionRecoveryDtls lObjPrvsTrnPensionRecoveryDtlsVO = null;
//					lObjTrnPensionRecoveryDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
//					lObjTrnPensionRecoveryDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
//					lObjTrnPensionRecoveryDtlsVO.setCreatedDate(new Date());
//					lObjTrnPensionRecoveryDtlsVO.setRecoveryFromFlag(gObjRsrcBndle.getString("PPMT.MONTHLY"));
//					if ("Update".equalsIgnoreCase(lStrMode)) {
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedDate(new Date());
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
//						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							if (lStrPensionerCode != null) {
//								lObjPrvsTrnPensionRecoveryDtlsVO = lObjPhysicalCaseInwardDAO.getRecoveryDtlsFromRecoveryType(lStrPensionerCode, gObjRsrcBndle.getString("PPMT.ARREAR"));
//							}
//						}
//					}
//
//					lObjTrnPensionRecoveryDtlsVO.setRecoveryType(gObjRsrcBndle.getString("PPMT.ARREAR"));
//					lObjTrnPensionRecoveryDtlsVO.setAmount(new BigDecimal(lStrArrearOfLicenseFee));
//					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getAmount(),
//								lObjTrnPensionRecoveryDtlsVO.getAmount(), "Arrears of license fee for Government Accommodation Amount", 0);
//					}
//					if (!StringUtility.getParameter("cmbArrearFromMonth", request).equals("-1") && !StringUtility.getParameter("cmbArrearFromYear", request).equals("-1")) {
//						lStrStartMonthYear = StringUtility.getParameter("cmbArrearFromYear", request).trim();
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							if (lObjPrvsTrnPensionRecoveryDtlsVO != null) {
//								if (lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth() != null) {
//									lStrMonthYear = lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth().toString();
//									lStrYear = lStrMonthYear.substring(0, 4);
//									lStrMonth = lStrMonthYear.substring(4, 6);
//									lStrMonth = Integer.valueOf(lStrMonth).toString();
//								}
//							} else {
//								lStrYear = null;
//								lStrMonth = null;
//							}
//							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrMonth, StringUtility.getParameter("cmbArrearFromMonth", request).trim(),
//									"Arrears of license fee for Government Accommodation For Month", 0);
//							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrYear, lStrStartMonthYear,
//									"Arrears of license fee for Government Accommodation For Year", 0);
//
//						}
//						if (Integer.parseInt(StringUtility.getParameter("cmbArrearFromMonth", request)) < 10) {
//							lStrStartMonthYear = lStrStartMonthYear + "0" + StringUtility.getParameter("cmbArrearFromMonth", request).trim();
//						} else {
//							lStrStartMonthYear = lStrStartMonthYear + StringUtility.getParameter("cmbArrearFromMonth", request).trim();
//						}
//						lObjTrnPensionRecoveryDtlsVO.setFromMonth(Integer.parseInt(lStrStartMonthYear));
//						lObjTrnPensionRecoveryDtlsVO.setToMonth(Integer.parseInt(lStrStartMonthYear));
//					}
//					lStrSchemeCode = StringUtility.getParameter("txtArrearSchemeCode", request);
//					lObjTrnPensionRecoveryDtlsVO.setSchemeCode(lStrSchemeCode);
//					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getSchemeCode(),
//								lObjTrnPensionRecoveryDtlsVO.getSchemeCode(), "Arrears of license fee for Government Accommodation Scheme Code", 0);
//					}
//					lLstTrnPensionRecoveryDtlsVO.add(lObjTrnPensionRecoveryDtlsVO);
//				}
//			}
//			lStrLicenseFeeForGovt = ((StringUtility.getParameter("txtLicenseFeeForGovt", request).trim()).length() > 0) ? StringUtility.getParameter("txtLicenseFeeForGovt", request).trim() : null;
//			if (lStrLicenseFeeForGovt != null) {
//				if (Double.parseDouble(lStrLicenseFeeForGovt) > 0) {
//					lStrYear = null;
//					lStrMonth = null;
//					lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
//					TrnPensionRecoveryDtls lObjPrvsTrnPensionRecoveryDtlsVO = null;
//					lObjTrnPensionRecoveryDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
//					lObjTrnPensionRecoveryDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
//					lObjTrnPensionRecoveryDtlsVO.setCreatedDate(new Date());
//					lObjTrnPensionRecoveryDtlsVO.setRecoveryFromFlag(gObjRsrcBndle.getString("PPMT.MONTHLY"));
//					if ("Update".equalsIgnoreCase(lStrMode)) {
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedDate(new Date());
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
//						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							if (lStrPensionerCode != null) {
//								lObjPrvsTrnPensionRecoveryDtlsVO = lObjPhysicalCaseInwardDAO.getRecoveryDtlsFromRecoveryType(lStrPensionerCode, gObjRsrcBndle.getString("PPMT.LICENSEFEE"));
//							}
//						}
//					}
//
//					lObjTrnPensionRecoveryDtlsVO.setRecoveryType(gObjRsrcBndle.getString("PPMT.LICENSEFEE"));
//					lObjTrnPensionRecoveryDtlsVO.setAmount(new BigDecimal(lStrLicenseFeeForGovt));
//					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getAmount(),
//								lObjTrnPensionRecoveryDtlsVO.getAmount(), "License fee for the retention of Government Amount", 0);
//					}
//					if (!StringUtility.getParameter("cmbLicenseFeeFromMonth", request).equals("-1") && !StringUtility.getParameter("cmbLicenseFeeFromYear", request).equals("-1")) {
//						lStrStartMonthYear = StringUtility.getParameter("cmbLicenseFeeFromYear", request).trim();
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							if (lObjPrvsTrnPensionRecoveryDtlsVO != null) {
//								if (lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth() != null) {
//									lStrMonthYear = lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth().toString();
//									lStrYear = lStrMonthYear.substring(0, 4);
//									lStrMonth = lStrMonthYear.substring(4, 6);
//									lStrMonth = Integer.valueOf(lStrMonth).toString();
//								}
//							} else {
//								lStrYear = null;
//								lStrMonth = null;
//							}
//							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrMonth, StringUtility.getParameter("cmbLicenseFeeFromMonth", request).trim(),
//									"License fee for the retention of Government For Month", 0);
//							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrYear, lStrStartMonthYear, "License fee for the retention of Government For Year", 0);
//
//						}
//						if (Integer.parseInt(StringUtility.getParameter("cmbLicenseFeeFromMonth", request)) < 10) {
//							lStrStartMonthYear = lStrStartMonthYear + "0" + StringUtility.getParameter("cmbLicenseFeeFromMonth", request).trim();
//						} else {
//							lStrStartMonthYear = lStrStartMonthYear + StringUtility.getParameter("cmbLicenseFeeFromMonth", request).trim();
//						}
//						lObjTrnPensionRecoveryDtlsVO.setFromMonth(Integer.parseInt(lStrStartMonthYear));
//						lObjTrnPensionRecoveryDtlsVO.setToMonth(Integer.parseInt(lStrStartMonthYear));
//					}
//					lStrSchemeCode = StringUtility.getParameter("txtLicFeeSchemeCode", request);
//					lObjTrnPensionRecoveryDtlsVO.setSchemeCode(lStrSchemeCode);
//					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getSchemeCode(),
//								lObjTrnPensionRecoveryDtlsVO.getSchemeCode(), "License fee for the retention of Government Scheme Code", 0);
//					}
//					lLstTrnPensionRecoveryDtlsVO.add(lObjTrnPensionRecoveryDtlsVO);
//				}
//			}
//			lArrStrNature = StringUtility.getParameterValues("txtNature", request);
//			lArrStrAmount = StringUtility.getParameterValues("txtDuesAmount", request);
//			lArrStrStartMonth = StringUtility.getParameterValues("cmbDuesStartMonth", request);
//			lArrStrStartYear = StringUtility.getParameterValues("cmbDuesStartYear", request);
//			lArrStrEndMonth = StringUtility.getParameterValues("cmbDuesEndMonth", request);
//			lArrStrEndYear = StringUtility.getParameterValues("cmbDuesEndYear", request);
//			lArrStrNoOfInstallments = StringUtility.getParameterValues("txtDuesNoOfInstlmt", request);
//			lArrStrSchemeCode = StringUtility.getParameterValues("txtAssdDueSchemeCode", request);
//			String[] lArrStrDueRecoveryDtlId = StringUtility.getParameterValues("hdnDueRecoveryDtlId", request);
//			if (lArrStrNature.length > 0) {
//				lIntRowNumber = 1;
//				for (Integer lIntCnt = 0; lIntCnt < lArrStrNature.length; lIntCnt++) {
//					lStrYear = null;
//					lStrMonth = null;
//					lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
//					TrnPensionRecoveryDtls lObjPrvsTrnPensionRecoveryDtlsVO = null;
//					lObjTrnPensionRecoveryDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
//					lObjTrnPensionRecoveryDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
//					lObjTrnPensionRecoveryDtlsVO.setCreatedDate(new Date());
//					lObjTrnPensionRecoveryDtlsVO.setRecoveryFromFlag(gObjRsrcBndle.getString("PPMT.MONTHLY"));
//					if ("Update".equalsIgnoreCase(lStrMode)) {
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedDate(new Date());
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
//						lObjTrnPensionRecoveryDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
//						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							if (lArrStrDueRecoveryDtlId[lIntCnt] != null && lArrStrDueRecoveryDtlId[lIntCnt].trim().length() > 0) {
//								lObjPrvsTrnPensionRecoveryDtlsVO = (TrnPensionRecoveryDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrDueRecoveryDtlId[lIntCnt].trim()));
//							}
//						}
//					}
//					lObjTrnPensionRecoveryDtlsVO.setRecoveryType(gObjRsrcBndle.getString("PPMT.ASSESSEDDUES"));
//					if (lArrStrNature[lIntCnt] != null && lArrStrNature[lIntCnt].trim().length() > 0) {
//						lObjTrnPensionRecoveryDtlsVO.setNature(lArrStrNature[lIntCnt].trim());
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getNature(),
//									lObjTrnPensionRecoveryDtlsVO.getNature(), "Other Recovery Nature", 0);
//						}
//					}
//					if (lArrStrAmount[lIntCnt] != null && lArrStrAmount[lIntCnt].trim().length() > 0) {
//						lObjTrnPensionRecoveryDtlsVO.setAmount(new BigDecimal(lArrStrAmount[lIntCnt].trim()));
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getAmount(),
//									lObjTrnPensionRecoveryDtlsVO.getAmount(), "Other Recovery Amount", 0);
//						}
//					}
//					if (!lArrStrStartYear[lIntCnt].equalsIgnoreCase("-1") && !lArrStrStartMonth[lIntCnt].equalsIgnoreCase("-1")) {
//						lStrStartMonthYear = lArrStrStartYear[lIntCnt].trim();
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							if (lObjPrvsTrnPensionRecoveryDtlsVO != null) {
//								if (lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth() != null) {
//									lStrMonthYear = lObjPrvsTrnPensionRecoveryDtlsVO.getFromMonth().toString();
//									lStrYear = lStrMonthYear.substring(0, 4);
//									lStrMonth = lStrMonthYear.substring(4, 6);
//									lStrMonth = Integer.valueOf(lStrMonth).toString();
//								}
//							} else {
//								lStrYear = null;
//								lStrMonth = null;
//							}
//							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrMonth, lArrStrStartMonth[lIntCnt].toString(), "Other Recovery Start Month", 0);
//							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrYear, lArrStrStartYear[lIntCnt].toString(), "Other Recovery Start Year", 0);
//
//						}
//						if (Integer.parseInt(lArrStrStartMonth[lIntCnt]) < 10) {
//							lStrStartMonthYear = lStrStartMonthYear + "0" + lArrStrStartMonth[lIntCnt].trim();
//						} else {
//							lStrStartMonthYear = lStrStartMonthYear + lArrStrStartMonth[lIntCnt].trim();
//						}
//						lObjTrnPensionRecoveryDtlsVO.setFromMonth(Integer.parseInt(lStrStartMonthYear));
//					}
//					if (!lArrStrEndMonth[lIntCnt].equalsIgnoreCase("-1") && !lArrStrEndYear[lIntCnt].equalsIgnoreCase("-1")) {
//						lStrEndMonthYear = lArrStrEndYear[lIntCnt].trim();
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							if (lObjPrvsTrnPensionRecoveryDtlsVO != null) {
//								if (lObjPrvsTrnPensionRecoveryDtlsVO.getToMonth() != null) {
//									lStrMonthYear = lObjPrvsTrnPensionRecoveryDtlsVO.getToMonth().toString();
//									lStrYear = lStrMonthYear.substring(0, 4);
//									lStrMonth = lStrMonthYear.substring(4, 6);
//									lStrMonth = Integer.valueOf(lStrMonth).toString();
//								}
//							} else {
//								lStrYear = null;
//								lStrMonth = null;
//							}
//							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrMonth, lArrStrEndMonth[lIntCnt].toString(), "Other Recovery End Month", 0);
//							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lStrYear, lArrStrEndYear[lIntCnt].toString(), "Other Recovery End Year", 0);
//
//						}
//						if (Integer.parseInt(lArrStrEndMonth[lIntCnt]) < 10) {
//							lStrEndMonthYear = lStrEndMonthYear + "0" + lArrStrEndMonth[lIntCnt].trim();
//						} else {
//							lStrEndMonthYear = lStrEndMonthYear + lArrStrEndMonth[lIntCnt].trim();
//						}
//						lObjTrnPensionRecoveryDtlsVO.setToMonth(Integer.parseInt(lStrEndMonthYear));
//					}
//					if (lArrStrNoOfInstallments[lIntCnt] != null && lArrStrNoOfInstallments[lIntCnt].trim().length() > 0) {
//						lObjTrnPensionRecoveryDtlsVO.setNoOfInstallments(Integer.parseInt(lArrStrNoOfInstallments[lIntCnt].trim()));
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getNoOfInstallments(),
//									lObjTrnPensionRecoveryDtlsVO.getNoOfInstallments(), "Other Recovery No of Installments", 0);
//						}
//					}
//					if (lArrStrSchemeCode[lIntCnt] != "" && lArrStrSchemeCode[lIntCnt].trim().length() > 0) {
//						lObjTrnPensionRecoveryDtlsVO.setSchemeCode(lArrStrSchemeCode[lIntCnt]);
//						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
//							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getSchemeCode(),
//									lObjTrnPensionRecoveryDtlsVO.getSchemeCode(), "Other Recovery Scheme code", 0);
//						}
//					}
//					lIntRowNumber++;
//					lLstTrnPensionRecoveryDtlsVO.add(lObjTrnPensionRecoveryDtlsVO);
//				}
//			}
			lArrStrNature = StringUtility.getParameterValues("txtDcrgNature", request);
			lArrStrAmount = StringUtility.getParameterValues("txtDcrgRecAmount", request);
			lArrStrSchemeCode = StringUtility.getParameterValues("txtDcrgSchemeCode", request);
			String[] lArrStrDcrgRecoveryDtlId = StringUtility.getParameterValues("hdnDcrgRecoveryDtlId", request);
			if (lArrStrNature.length > 0) {
				lIntRowNumber = 1;
				for (Integer lIntCnt = 0; lIntCnt < lArrStrNature.length; lIntCnt++) {
					lStrYear = null;
					lStrMonth = null;
					lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
					TrnPensionRecoveryDtls lObjPrvsTrnPensionRecoveryDtlsVO = null;
					lObjTrnPensionRecoveryDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjTrnPensionRecoveryDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjTrnPensionRecoveryDtlsVO.setCreatedDate(new Date());
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPensionRecoveryDtlsVO.setUpdatedDate(new Date());
						lObjTrnPensionRecoveryDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionRecoveryDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lArrStrDcrgRecoveryDtlId[lIntCnt] != null && lArrStrDcrgRecoveryDtlId[lIntCnt].trim().length() > 0) {
								lObjPrvsTrnPensionRecoveryDtlsVO = (TrnPensionRecoveryDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrDcrgRecoveryDtlId[lIntCnt].trim()));
							}
						}
					}
					lObjTrnPensionRecoveryDtlsVO.setRecoveryType(gObjRsrcBndle.getString("PPMT.DCRGRECOVERY"));
					lObjTrnPensionRecoveryDtlsVO.setRecoveryFromFlag(gObjRsrcBndle.getString("PPMT.DCRG"));
					if (lArrStrNature[lIntCnt] != null && lArrStrNature[lIntCnt].trim().length() > 0) {
						lObjTrnPensionRecoveryDtlsVO.setNature(lArrStrNature[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getNature(),
									lObjTrnPensionRecoveryDtlsVO.getNature(), "Recovery for DCRG Nature", 0);
						}
					}
					if (lArrStrAmount[lIntCnt] != null && lArrStrAmount[lIntCnt].trim().length() > 0) {
						lObjTrnPensionRecoveryDtlsVO.setAmount(new BigDecimal(lArrStrAmount[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? null : lObjPrvsTrnPensionRecoveryDtlsVO.getAmount(),
									lObjTrnPensionRecoveryDtlsVO.getAmount(), "Recovery for DCRG Amount", 0);
						}
					}
					if (lArrStrSchemeCode[lIntCnt] != null && lArrStrSchemeCode[lIntCnt].trim().length() > 0) {
						lObjTrnPensionRecoveryDtlsVO.setSchemeCode(lArrStrSchemeCode[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnPensionRecoveryDtlsVO == null ? "" : lObjPrvsTrnPensionRecoveryDtlsVO.getSchemeCode(),
									lObjTrnPensionRecoveryDtlsVO.getSchemeCode(), "Recovery for DCRG Scheme Code", 0);
						}
					}
					lIntRowNumber++;
					lLstTrnPensionRecoveryDtlsVO.add(lObjTrnPensionRecoveryDtlsVO);
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lLstTrnPensionRecoveryDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateTrnCvpRestorationDtlsVO(java.util.Map)
	 */
	public List<TrnCvpRestorationDtls> generateTrnCvpRestorationDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtlsVO = new ArrayList<TrnCvpRestorationDtls>();
		TrnCvpRestorationDtls lObjTrnCvpRestorationDtlsVO = new TrnCvpRestorationDtls();
		String lStrPensionerCode = null;
		String lStrCorrectionFlag = null;
		Integer lIntRowNumber = 1;
		try {
			setSessionInfo(inputMap);
			String[] lArrStrCvpRestnAmt = StringUtility.getParameterValues("txtCvpRestnAmt", request);
			String[] lArrStrRestnFromDate = StringUtility.getParameterValues("txtRestnFromDate", request);
			String[] lArrStrRestnToDate = StringUtility.getParameterValues("txtRestnToDate", request);
			String[] lArrStrCvpRestnDtlsId = StringUtility.getParameterValues("hdnCvpRestnDtlId", request);
			String[] lArrStrRestnAplnRecvd = StringUtility.getParameterValues("chkRetnAplnRecvd", request);
			String[] lArrStrRestnAplnRecvdDate = StringUtility.getParameterValues("txtRestnAplnRecvdDate", request);
			String lStrFromMonthYear = null;
			String lStrMonthYear = null;
			String lStrToMonthYear = null;
			String lStrYear = null;
			String lStrMonth = null;
			String lStrMode = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("PensionerCode")) {
				lStrPensionerCode = inputMap.get("PensionerCode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnCvpRestorationDtls.class, serv.getSessionFactory());
			if (lArrStrCvpRestnAmt.length > 0) {
				for (Integer lIntCnt = 0; lIntCnt < lArrStrCvpRestnAmt.length; lIntCnt++) {
					lObjTrnCvpRestorationDtlsVO = new TrnCvpRestorationDtls();
					TrnCvpRestorationDtls lObjPrvsTrnCvpRestorationDtlsVO = null;
					lObjTrnCvpRestorationDtlsVO.setDbId(gLngDBId);
					lObjTrnCvpRestorationDtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));
					lObjTrnCvpRestorationDtlsVO.setCreatedUserId(gLngUserId);
					lObjTrnCvpRestorationDtlsVO.setCreatedPostId(gLngPostId);
					lObjTrnCvpRestorationDtlsVO.setCreatedDate(new Date());
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnCvpRestorationDtlsVO.setUpdatedDate(new Date());
						lObjTrnCvpRestorationDtlsVO.setUpdatedPostId(gLngPostId);
						lObjTrnCvpRestorationDtlsVO.setUpdatedUserId(gLngUserId);
						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lArrStrCvpRestnDtlsId[lIntCnt] != null && lArrStrCvpRestnDtlsId[lIntCnt].trim().length() > 0) {
								lObjPrvsTrnCvpRestorationDtlsVO = (TrnCvpRestorationDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrCvpRestnDtlsId[lIntCnt].trim()));
							}
						}
					}
					if (lArrStrCvpRestnAmt[lIntCnt] != null && lArrStrCvpRestnAmt[lIntCnt].trim().length() > 0) {
						lObjTrnCvpRestorationDtlsVO.setAmount(new BigDecimal(lArrStrCvpRestnAmt[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsTrnCvpRestorationDtlsVO == null ? null : lObjPrvsTrnCvpRestorationDtlsVO.getAmount(),
									lObjTrnCvpRestorationDtlsVO.getAmount(), "Commutation Restoration Amount", 0);
						}
					}
					if (lArrStrRestnFromDate[lIntCnt] != null && lArrStrRestnFromDate[lIntCnt].trim().length() > 0) {
						lObjTrnCvpRestorationDtlsVO.setFromDate(lObjSimpleDateFormat.parse(lArrStrRestnFromDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsTrnCvpRestorationDtlsVO == null ? null : lObjPrvsTrnCvpRestorationDtlsVO.getFromDate(),
									lObjTrnCvpRestorationDtlsVO.getFromDate(), "Commutation Restoration From Date", 0);
						}
					}
					if (lArrStrRestnToDate[lIntCnt] != null && lArrStrRestnToDate[lIntCnt].trim().length() > 0) {
						lObjTrnCvpRestorationDtlsVO.setToDate(lObjSimpleDateFormat.parse(lArrStrRestnToDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsTrnCvpRestorationDtlsVO == null ? null : lObjPrvsTrnCvpRestorationDtlsVO.getToDate(),
									lObjTrnCvpRestorationDtlsVO.getToDate(), "Commutation Restoration To Date", 0);
						}
					}

					if (lArrStrRestnAplnRecvd[lIntCnt] != null && lArrStrRestnAplnRecvd[lIntCnt].length() > 0) {
						lObjTrnCvpRestorationDtlsVO.setRestnAplnReceivedFlag("Y");
					} else {
						lObjTrnCvpRestorationDtlsVO.setRestnAplnReceivedFlag("N");
					}
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsTrnCvpRestorationDtlsVO == null ? "" : lObjPrvsTrnCvpRestorationDtlsVO.getRestnAplnReceivedFlag(),
								lObjTrnCvpRestorationDtlsVO.getRestnAplnReceivedFlag(), "Commutation Restoration Application Received", 0);
					}
					if (lArrStrRestnAplnRecvdDate[lIntCnt] != null && lArrStrRestnAplnRecvdDate[lIntCnt].trim().length() > 0) {
						lObjTrnCvpRestorationDtlsVO.setRestnAplnReceivedDate(lObjSimpleDateFormat.parse(lArrStrRestnAplnRecvdDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsTrnCvpRestorationDtlsVO == null ? null : lObjPrvsTrnCvpRestorationDtlsVO.getRestnAplnReceivedDate(),
									lObjTrnCvpRestorationDtlsVO.getRestnAplnReceivedDate(), "Commutation Restoration Application Received Date", 0);
						}
					}
					lIntRowNumber++;
					lLstTrnCvpRestorationDtlsVO.add(lObjTrnCvpRestorationDtlsVO);
				}

			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lLstTrnCvpRestorationDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateHstReEmploymentDtlsVO(java.util.Map)
	 */
	public List<HstReEmploymentDtls> generateHstReEmploymentDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<HstReEmploymentDtls> lLstHstReEmploymentDtlsVO = new ArrayList<HstReEmploymentDtls>();
		HstReEmploymentDtls lObjHstReEmploymentDtlsVO = new HstReEmploymentDtls();
		String lStrPensionerCode = null;
		String lStrCorrectionFlag = null;
		Integer lIntRowNumber = 1;
		try {
			setSessionInfo(inputMap);
			String[] lArrStrReEmpltId = StringUtility.getParameterValues("hdnReEmpltId", request);
			String[] lArrStrReEmpltFromDate = StringUtility.getParameterValues("txtReEmpltFromDate", request);
			String[] lArrStrReEmpltToDate = StringUtility.getParameterValues("txtReEmpltToDate", request);
			String[] lArrStrDAInPnsnSal = StringUtility.getParameterValues("cmbDAInPnsnSal", request);
			String[] lArrStrEmployeeOrderNo = StringUtility.getParameterValues("txtEmployeeOrderNo", request);
			String[] lArrStrEmployeeOrderDate = StringUtility.getParameterValues("txtEmployeeOrderDate", request);

			String lStrMode = null;

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("PensionerCode")) {
				lStrPensionerCode = inputMap.get("PensionerCode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstReEmploymentDtls.class, serv.getSessionFactory());
			if (lArrStrReEmpltFromDate.length > 0) {
				for (Integer lIntCnt = 0; lIntCnt < lArrStrReEmpltFromDate.length; lIntCnt++) {
					lObjHstReEmploymentDtlsVO = new HstReEmploymentDtls();
					HstReEmploymentDtls lObjPrvsHstReEmploymentDtlsVO = null;
					lObjHstReEmploymentDtlsVO.setDbId(gLngDBId);
					lObjHstReEmploymentDtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));
					lObjHstReEmploymentDtlsVO.setCreatedUserId(gLngUserId);
					lObjHstReEmploymentDtlsVO.setCreatedPostId(gLngPostId);
					lObjHstReEmploymentDtlsVO.setCreatedDate(new Date());
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjHstReEmploymentDtlsVO.setUpdatedDate(new Date());
						lObjHstReEmploymentDtlsVO.setUpdatedPostId(gLngPostId);
						lObjHstReEmploymentDtlsVO.setUpdatedUserId(gLngUserId);
						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lArrStrReEmpltId[lIntCnt] != null && lArrStrReEmpltId[lIntCnt].trim().length() > 0) {
								lObjPrvsHstReEmploymentDtlsVO = (HstReEmploymentDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrReEmpltId[lIntCnt].trim()));
							}
						}
					}
					if (lArrStrReEmpltFromDate[lIntCnt] != null && lArrStrReEmpltFromDate[lIntCnt].trim().length() > 0) {
						lObjHstReEmploymentDtlsVO.setFromDate(lObjSimpleDateFormat.parse(lArrStrReEmpltFromDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsHstReEmploymentDtlsVO == null ? null : lObjPrvsHstReEmploymentDtlsVO.getFromDate(),
									lObjHstReEmploymentDtlsVO.getFromDate(), "Re-Employment From Date", 0);
						}
					}
					if (lArrStrReEmpltToDate[lIntCnt] != null && lArrStrReEmpltToDate[lIntCnt].trim().length() > 0) {
						lObjHstReEmploymentDtlsVO.setToDate(lObjSimpleDateFormat.parse(lArrStrReEmpltToDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsHstReEmploymentDtlsVO == null ? null : lObjPrvsHstReEmploymentDtlsVO.getToDate(),
									lObjHstReEmploymentDtlsVO.getToDate(), "Re-Employment To Date", 0);
						}
					}
					if (lArrStrDAInPnsnSal[lIntCnt] != null && lArrStrDAInPnsnSal[lIntCnt].trim().length() > 0) {
						lObjHstReEmploymentDtlsVO.setDaInPensionSalary(lArrStrDAInPnsnSal[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsHstReEmploymentDtlsVO == null ? "" : lObjPrvsHstReEmploymentDtlsVO.getDaInPensionSalary(),
									lObjHstReEmploymentDtlsVO.getDaInPensionSalary(), "Re-Employment DA in Pension/Salary", 0);
						}
					}
					if (lArrStrEmployeeOrderNo[lIntCnt] != null && lArrStrEmployeeOrderNo[lIntCnt].trim().length() > 0) {
						lObjHstReEmploymentDtlsVO.setEmployeeOrderNo(lArrStrEmployeeOrderNo[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsHstReEmploymentDtlsVO == null ? "" : lObjPrvsHstReEmploymentDtlsVO.getEmployeeOrderNo(),
									lObjHstReEmploymentDtlsVO.getEmployeeOrderNo(), "Employee Order Number", 0);
						}
					}
					if (lArrStrEmployeeOrderDate[lIntCnt] != null && lArrStrEmployeeOrderDate[lIntCnt].trim().length() > 0) {
						lObjHstReEmploymentDtlsVO.setEmployeeOrderDate(lObjSimpleDateFormat.parse(lArrStrEmployeeOrderDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsHstReEmploymentDtlsVO == null ? null : lObjPrvsHstReEmploymentDtlsVO.getEmployeeOrderDate(),
									lObjHstReEmploymentDtlsVO.getEmployeeOrderDate(), "Employee Order Date", 0);
						}
					}
					lIntRowNumber++;
					lLstHstReEmploymentDtlsVO.add(lObjHstReEmploymentDtlsVO);
				}

			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lLstHstReEmploymentDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateHstCommutationDtlsVO(java.util.Map)
	 */
	public List<HstCommutationDtls> generateHstCommutationDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<HstCommutationDtls> lLstHstCommutationDtlsVO = new ArrayList<HstCommutationDtls>();
		HstCommutationDtls lObjHstCommutationDtlsVO = new HstCommutationDtls();
		String lStrPensionerCode=null;
		String lStrCorrectionFlag=null;
		Integer lIntRowNumber = 1;
		try {
			setSessionInfo(inputMap);
			String[] lArrStrCvpDtlsId = StringUtility.getParameterValues("hdnCommtnDtlsId", request);
			String[] lArrStrCommtnOrderNo = StringUtility.getParameterValues("hdnCommtnOrderNo", request);
			String[] lArrStrPaymentAmount = StringUtility.getParameterValues("hdnPaymentAmount", request);
			String[] lArrStrCommtnVoucherNo = StringUtility.getParameterValues("hdnCommtnVoucherNo", request);
			String[] lArrStrCommtnVoucherDate = StringUtility.getParameterValues("hdnCommtnVoucherDate", request);
			
			String lStrMode = null;
			
			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("PensionerCode")) {
		    	lStrPensionerCode=inputMap.get("PensionerCode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(
					HstReEmploymentDtls.class, serv.getSessionFactory());
			if (lArrStrCommtnOrderNo.length > 0) {
				for (Integer lIntCnt = 0; lIntCnt < lArrStrCommtnOrderNo.length; lIntCnt++) {
					lObjHstCommutationDtlsVO = new HstCommutationDtls();
					HstCommutationDtls lObjPrvsHstCommutationDtlsVO=null;
					lObjHstCommutationDtlsVO.setDbId(gLngDBId);
					lObjHstCommutationDtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));
					lObjHstCommutationDtlsVO.setCreatedUserId(gLngUserId);
					lObjHstCommutationDtlsVO.setCreatedPostId(gLngPostId);
					lObjHstCommutationDtlsVO.setCreatedDate(new Date());
					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjHstCommutationDtlsVO.setUpdatedDate(new Date());
						lObjHstCommutationDtlsVO.setUpdatedPostId(gLngPostId);
						lObjHstCommutationDtlsVO.setUpdatedUserId(gLngUserId);
					    if("Y".equalsIgnoreCase(lStrCorrectionFlag))
					    {
						    if(lArrStrCvpDtlsId[lIntCnt] !=null && lArrStrCvpDtlsId[lIntCnt].trim().length() > 0)
						    	lObjPrvsHstCommutationDtlsVO=(HstCommutationDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrCvpDtlsId[lIntCnt].trim()));
					    }
					}
					if (lArrStrCommtnOrderNo[lIntCnt] != null && lArrStrCommtnOrderNo[lIntCnt].trim().length() > 0) {
						lObjHstCommutationDtlsVO.setOrderNo(lArrStrCommtnOrderNo[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag))
						{
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsHstCommutationDtlsVO == null ? null : lObjPrvsHstCommutationDtlsVO.getOrderNo(), lObjHstCommutationDtlsVO.getOrderNo(),"Commutation Order Number", 0);
						}
					}
					if (lArrStrPaymentAmount[lIntCnt] != null && lArrStrPaymentAmount[lIntCnt].trim().length() > 0) {
						lObjHstCommutationDtlsVO.setPaymentAmount(new BigDecimal(lArrStrPaymentAmount[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag))
						{
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsHstCommutationDtlsVO == null ? null : lObjPrvsHstCommutationDtlsVO.getPaymentAmount(), lObjHstCommutationDtlsVO.getPaymentAmount(),"Commutation Payment Amount", 0);
							
						}
					}
					if (lArrStrCommtnVoucherNo[lIntCnt] != null && lArrStrCommtnVoucherNo[lIntCnt].trim().length() > 0) {
						lObjHstCommutationDtlsVO.setVoucherNo(lArrStrCommtnVoucherNo[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag))
						{
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsHstCommutationDtlsVO == null ? "" : lObjPrvsHstCommutationDtlsVO.getVoucherNo(), lObjHstCommutationDtlsVO.getVoucherNo(), "Commutation Voucher Number", 0);
						}
					}
					if (lArrStrCommtnVoucherDate[lIntCnt] != null && lArrStrCommtnVoucherDate[lIntCnt].trim().length() > 0) {
						lObjHstCommutationDtlsVO.setVoucherDate(lObjSimpleDateFormat.parse(lArrStrCommtnVoucherDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag))
						{
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsHstCommutationDtlsVO == null ? null : lObjPrvsHstCommutationDtlsVO.getVoucherDate(), lObjHstCommutationDtlsVO.getVoucherDate(), "Commutation Voucher Date", 0);
						}
					}
					lIntRowNumber++;
					lLstHstCommutationDtlsVO.add(lObjHstCommutationDtlsVO);
				}

			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lLstHstCommutationDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateHstPnsnPmntDcrgDtlsVO(java.util.Map)
	 */
/*	public List<HstPnsnPmntDcrgDtls> generateHstPnsnPmntDcrgDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<HstPnsnPmntDcrgDtls> lLstHstPnsnPmntDcrgDtlsVO = new ArrayList<HstPnsnPmntDcrgDtls>();
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtlsVO = null;
		String lStrPensionerCode = null;
		String lStrCorrectionFlag = null;
		Integer lIntRowNumber = 1;

		try {
			String[] lArrStrDcrgDtlsId = StringUtility.getParameterValues("hdnDcrgDtlsId", request);
			String[] lArrStrOrderNo = StringUtility.getParameterValues("hdnDcrgOrderNo", request);
			String[] lArrStrOrderDate = StringUtility.getParameterValues("hdnDcrgOrderDate", request);
			String[] lArrStrTotalOrderAmount = StringUtility.getParameterValues("hdnDcrgOrderAmount", request);
			String[] lArrStrPaidAmount = StringUtility.getParameterValues("hdnDcrgAmount", request);
			String[] lArrStrVoucherNo = StringUtility.getParameterValues("hdnDcrgVoucherNo", request);
			String[] lArrStrVoucherDate = StringUtility.getParameterValues("hdnDcrgVoucherDate", request);
			String[] lArrStrPaymentAuthority = StringUtility.getParameterValues("hdnDcrgPaymentAuth", request);

			String lStrMode = null;

			setSessionInfo(inputMap);

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("PensionerCode")) {
				lStrPensionerCode = inputMap.get("PensionerCode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstPnsnPmntDcrgDtls.class, serv.getSessionFactory());
			if (lArrStrOrderNo.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrOrderNo.length; lIntCnt++) {
					lObjHstPnsnPmntDcrgDtlsVO = new HstPnsnPmntDcrgDtls();
					HstPnsnPmntDcrgDtls lObjPrvsHstPnsnPmntDcrgDtlsVO = null;

					lObjHstPnsnPmntDcrgDtlsVO.setDbId(gLngDBId);
					lObjHstPnsnPmntDcrgDtlsVO.setLocationCode(Long.parseLong(gStrLocId));
					lObjHstPnsnPmntDcrgDtlsVO.setCreatedPostId(gLngPostId);
					lObjHstPnsnPmntDcrgDtlsVO.setCreatedUserId(gLngUserId);
					lObjHstPnsnPmntDcrgDtlsVO.setCreatedDate(new Date());

					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjHstPnsnPmntDcrgDtlsVO.setUpdatedDate(new Date());
						lObjHstPnsnPmntDcrgDtlsVO.setUpdatedPostId(gLngPostId);
						lObjHstPnsnPmntDcrgDtlsVO.setUpdatedUserId(gLngUserId);
						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lArrStrDcrgDtlsId[lIntCnt] != null && lArrStrDcrgDtlsId[lIntCnt].trim().length() > 0) {
								lObjPrvsHstPnsnPmntDcrgDtlsVO = (HstPnsnPmntDcrgDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrDcrgDtlsId[lIntCnt].trim()));
							}
						}
					}

					if (lArrStrOrderNo[lIntCnt] != null && lArrStrOrderNo[lIntCnt].trim().length() > 0) {
						lObjHstPnsnPmntDcrgDtlsVO.setOrderNo(lArrStrOrderNo[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsHstPnsnPmntDcrgDtlsVO == null ? "" : lObjPrvsHstPnsnPmntDcrgDtlsVO.getOrderNo(), lObjHstPnsnPmntDcrgDtlsVO.getOrderNo(),
									"Dcrg Order Number", 0);
						}
					}
					if (lArrStrOrderDate[lIntCnt] != null && lArrStrOrderDate[lIntCnt].trim().length() > 0) {
						lObjHstPnsnPmntDcrgDtlsVO.setOrderDate(simpleDateFormat.parse(lArrStrOrderDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsHstPnsnPmntDcrgDtlsVO == null ? null : lObjPrvsHstPnsnPmntDcrgDtlsVO.getOrderDate(),
									lObjHstPnsnPmntDcrgDtlsVO.getOrderDate(), "Dcrg Order Date", 0);
						}
					}
					if (lArrStrTotalOrderAmount[lIntCnt] != null && lArrStrTotalOrderAmount[lIntCnt].trim().length() > 0) {
						lObjHstPnsnPmntDcrgDtlsVO.setTotalOrderAmount(new BigDecimal(lArrStrTotalOrderAmount[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsHstPnsnPmntDcrgDtlsVO == null ? null : lObjPrvsHstPnsnPmntDcrgDtlsVO.getTotalOrderAmount(),
									lObjHstPnsnPmntDcrgDtlsVO.getTotalOrderAmount(), "Dcrg Order Amount", 0);
						}
					}
					if (lArrStrPaidAmount[lIntCnt] != null && lArrStrPaidAmount[lIntCnt].trim().length() > 0) {
						lObjHstPnsnPmntDcrgDtlsVO.setPaidAmount(new BigDecimal(lArrStrPaidAmount[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsHstPnsnPmntDcrgDtlsVO == null ? null : lObjPrvsHstPnsnPmntDcrgDtlsVO.getPaidAmount(),
									lObjHstPnsnPmntDcrgDtlsVO.getPaidAmount(), "Dcrg Amount Paid/Payable", 0);
						}
					}
					if (lArrStrVoucherNo[lIntCnt] != null && lArrStrVoucherNo[lIntCnt].trim().length() > 0) {
						lObjHstPnsnPmntDcrgDtlsVO.setVoucherNo(lArrStrVoucherNo[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsHstPnsnPmntDcrgDtlsVO == null ? "" : lObjPrvsHstPnsnPmntDcrgDtlsVO.getVoucherNo(),
									lObjHstPnsnPmntDcrgDtlsVO.getVoucherNo(), "Dcrg Voucher Number", 0);
						}
					}
					if (lArrStrVoucherDate[lIntCnt] != null && lArrStrVoucherDate[lIntCnt].trim().length() > 0) {
						lObjHstPnsnPmntDcrgDtlsVO.setVoucherDate(simpleDateFormat.parse(lArrStrVoucherDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsHstPnsnPmntDcrgDtlsVO == null ? null : lObjPrvsHstPnsnPmntDcrgDtlsVO.getVoucherDate(),
									lObjHstPnsnPmntDcrgDtlsVO.getVoucherDate(), "Dcrg Voucher Date", 0);
						}
					}
					if (lArrStrPaymentAuthority[lIntCnt] != null && lArrStrPaymentAuthority[lIntCnt].trim().length() > 0) {
						lObjHstPnsnPmntDcrgDtlsVO.setPaymentAuthority(lArrStrPaymentAuthority[lIntCnt].trim());
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVO(lIntRowNumber, lObjPrvsHstPnsnPmntDcrgDtlsVO == null ? "" : lObjPrvsHstPnsnPmntDcrgDtlsVO.getPaymentAuthority(),
									lObjHstPnsnPmntDcrgDtlsVO.getPaymentAuthority(), "Dcrg Paying Authority", 0);
						}
					}
					lIntRowNumber++;
					lLstHstPnsnPmntDcrgDtlsVO.add(lObjHstPnsnPmntDcrgDtlsVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}

		return lLstHstPnsnPmntDcrgDtlsVO;
	}
*/
	private void changeApprovalStatus(int lIntStatus) {

		if (gIntApprovalLevel < lIntStatus) {
			gIntApprovalLevel = lIntStatus;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateTrnPensionCutDtlsVO(java.util.Map)
	 */
	public List<TrnPensionCutDtls> generateTrnPensionCutDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<TrnPensionCutDtls> lLstTrnPensionCutDtlsVO = new ArrayList<TrnPensionCutDtls>();
		TrnPensionCutDtls lObjTrnPensionCutDtlsVO = null;
		String lStrPensionerCode = null;
		String lStrCorrectionFlag = null;
		Integer lIntRowNumber = 1;

		try {
			String[] lArrStrPensionCutDtlsId = StringUtility.getParameterValues("hdnPensionCutDtlsId", request);
			String[] lArrStrAmount = StringUtility.getParameterValues("txtPnshmntAmount", request);
			String[] lArrStrFromDate = StringUtility.getParameterValues("txtPnshmntFromDate", request);
			String[] lArrStrToDate = StringUtility.getParameterValues("txtPnshmntToDate", request);

			String lStrMode = null;

			setSessionInfo(inputMap);

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("PensionerCode")) {
				lStrPensionerCode = inputMap.get("PensionerCode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCutDtls.class, serv.getSessionFactory());
			if (lArrStrAmount.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrAmount.length; lIntCnt++) {
					lObjTrnPensionCutDtlsVO = new TrnPensionCutDtls();
					TrnPensionCutDtls lObjPrvsTrnPensionCutDtlsVO = null;

					lObjTrnPensionCutDtlsVO.setDbId(gLngDBId);
					lObjTrnPensionCutDtlsVO.setLocationCode(Long.parseLong(gStrLocId));
					lObjTrnPensionCutDtlsVO.setCreatedPostId(gLngPostId);
					lObjTrnPensionCutDtlsVO.setCreatedUserId(gLngUserId);
					lObjTrnPensionCutDtlsVO.setCreatedDate(new Date());

					if ("Update".equalsIgnoreCase(lStrMode)) {
						lObjTrnPensionCutDtlsVO.setUpdatedDate(new Date());
						lObjTrnPensionCutDtlsVO.setUpdatedPostId(gLngPostId);
						lObjTrnPensionCutDtlsVO.setUpdatedUserId(gLngUserId);
						if ("Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lArrStrPensionCutDtlsId[lIntCnt] != null && lArrStrPensionCutDtlsId[lIntCnt].trim().length() > 0) {
								lObjPrvsTrnPensionCutDtlsVO = (TrnPensionCutDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lArrStrPensionCutDtlsId[lIntCnt].trim()));
							}
						}
					}

					if (lArrStrAmount[lIntCnt] != null && lArrStrAmount[lIntCnt].trim().length() > 0) {
						lObjTrnPensionCutDtlsVO.setAmount(new BigDecimal(lArrStrAmount[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForAmount(lIntRowNumber, lObjPrvsTrnPensionCutDtlsVO == null ? null : lObjPrvsTrnPensionCutDtlsVO.getAmount(), lObjTrnPensionCutDtlsVO.getAmount(),
									"Punishment Cut Amount", 0);
						}
					}

					if (lArrStrFromDate[lIntCnt] != null && lArrStrFromDate[lIntCnt].trim().length() > 0) {
						lObjTrnPensionCutDtlsVO.setFromDate(simpleDateFormat.parse(lArrStrFromDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsTrnPensionCutDtlsVO == null ? null : lObjPrvsTrnPensionCutDtlsVO.getFromDate(),
									lObjTrnPensionCutDtlsVO.getFromDate(), "Punishment Cut From Date", 0);
						}
					}

					if (lArrStrToDate[lIntCnt] != null && lArrStrToDate[lIntCnt].trim().length() > 0) {
						lObjTrnPensionCutDtlsVO.setToDate(simpleDateFormat.parse(lArrStrToDate[lIntCnt].trim()));
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							generateCorrectionDtlsVOForDate(lIntRowNumber, lObjPrvsTrnPensionCutDtlsVO == null ? null : lObjPrvsTrnPensionCutDtlsVO.getToDate(), lObjTrnPensionCutDtlsVO.getToDate(),
									"Punishment Cut To Date", 0);
						}
					}

					lIntRowNumber++;
					lLstTrnPensionCutDtlsVO.add(lObjTrnPensionCutDtlsVO);

				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}

		return lLstTrnPensionCutDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateCorrectionDtlsVo(java.lang.String, java.lang.Object,
	 * java.lang.Object, java.lang.String, java.lang.Integer)
	 */
	public void generateCorrectionDtlsVO(Integer lIntRowNumber, Object lObjApprovedObject, Object lObjCurrentObject, String lStrFieldType, Integer lIntApprovalStatus) throws Exception {

		try {
			TrnPensionCorrectionDtls lObjCorrectionVO = null;
			if (lObjApprovedObject != null && lObjCurrentObject != null) {
				if (!(lObjApprovedObject.toString().trim()).equalsIgnoreCase(lObjCurrentObject.toString().trim())) {
					lObjCorrectionVO = new TrnPensionCorrectionDtls();
					if (lIntRowNumber != null) {
						lObjCorrectionVO.setFieldType(lStrFieldType + "~" + lIntRowNumber);
					} else {
						lObjCorrectionVO.setFieldType(lStrFieldType);
					}
					lObjCorrectionVO.setPrvsFieldValue(lObjApprovedObject.toString().trim());
					lObjCorrectionVO.setCrntFieldValue(lObjCurrentObject.toString().trim());
				}
			} else if (lObjApprovedObject != null && lObjCurrentObject == null) {
				if (!"N".equals(lObjApprovedObject.toString().trim()) && lObjApprovedObject.toString().trim().length() > 0 && !"-1".equals(lObjApprovedObject.toString())
						&& !"0.00".equals(lObjApprovedObject.toString().trim()) && !"0".equals(lObjApprovedObject.toString())) {
					lObjCorrectionVO = new TrnPensionCorrectionDtls();
					if (lIntRowNumber != null) {
						lObjCorrectionVO.setFieldType(lStrFieldType + "~" + lIntRowNumber);
					} else {
						lObjCorrectionVO.setFieldType(lStrFieldType);
					}
					lObjCorrectionVO.setPrvsFieldValue(lObjApprovedObject.toString().trim());
					lObjCorrectionVO.setCrntFieldValue("");
				}
			} else if (lObjApprovedObject == null && lObjCurrentObject != null) {
				if (!"N".equals(lObjCurrentObject.toString().trim()) && lObjCurrentObject.toString().trim().length() > 0 && !"-1".equals(lObjCurrentObject.toString())
						&& !"0.00".equals(lObjCurrentObject.toString().trim()) && !"0".equals(lObjCurrentObject.toString())) {
					lObjCorrectionVO = new TrnPensionCorrectionDtls();
					if (lIntRowNumber != null) {
						lObjCorrectionVO.setFieldType(lStrFieldType + "~" + lIntRowNumber);
					} else {
						lObjCorrectionVO.setFieldType(lStrFieldType);
					}
					lObjCorrectionVO.setPrvsFieldValue("");
					lObjCorrectionVO.setCrntFieldValue(lObjCurrentObject.toString().trim());
				}
			}
			if (lObjCorrectionVO != null) {
				gObjCorrectionDtls.add(lObjCorrectionVO);
				// if (lIntApprovalStatus != 0) {
				// changeApprovalStatus(lIntApprovalStatus);
				// }
				if (lIntRowNumber != null) {
					gLstFieldTypeListToDelete.add(lStrFieldType + "~" + lIntRowNumber);
				} else {
					gLstFieldTypeListToDelete.add(lStrFieldType);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is" + e, e);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateCorrectionDtlsVoForAmount(java.lang.String, java.lang.Object,
	 * java.lang.Object, java.lang.String, java.lang.Integer)
	 */
	public void generateCorrectionDtlsVOForAmount(Integer lIntRowNumber, Object lObjApprovedObject, Object lObjCurrentObject, String lStrFieldType, Integer lIntApprovalStatus) throws Exception {

		try {
			TrnPensionCorrectionDtls lObjCorrectionVO = null;
			if (lObjApprovedObject != null && lObjApprovedObject != null) {
				if (lObjApprovedObject.toString().length() > 0 && lObjCurrentObject.toString().length() > 0) {
					if (new BigDecimal(lObjApprovedObject.toString().trim()).intValue() != new BigDecimal(lObjCurrentObject.toString().trim()).intValue()) {
						lObjCorrectionVO = new TrnPensionCorrectionDtls();
						if (lIntRowNumber != null) {
							lObjCorrectionVO.setFieldType(lStrFieldType + "~" + lIntRowNumber);
						} else {
							lObjCorrectionVO.setFieldType(lStrFieldType);
						}
						lObjCorrectionVO.setPrvsFieldValue(lObjApprovedObject.toString().trim());
						lObjCorrectionVO.setCrntFieldValue(lObjCurrentObject.toString().trim());
					}
				}
			} else if (lObjApprovedObject != null && lObjCurrentObject == null) {
				if (lObjApprovedObject.toString().trim().length() > 0 && !"-1".equals(lObjApprovedObject.toString().trim()) && !"0.00".equals(lObjApprovedObject.toString().trim())
						&& !"0".equals(lObjApprovedObject.toString().trim())) {
					lObjCorrectionVO = new TrnPensionCorrectionDtls();
					if (lIntRowNumber != null) {
						lObjCorrectionVO.setFieldType(lStrFieldType + "~" + lIntRowNumber);
					} else {
						lObjCorrectionVO.setFieldType(lStrFieldType);
					}
					lObjCorrectionVO.setPrvsFieldValue(lObjApprovedObject.toString().trim());
					lObjCorrectionVO.setCrntFieldValue("");
				}
			} else if (lObjApprovedObject == null && lObjCurrentObject != null) {
				if (lObjCurrentObject.toString().trim().length() > 0 && !"-1".equals(lObjCurrentObject.toString().trim()) && !"0.00".equals(lObjCurrentObject.toString().trim())
						&& !"0".equals(lObjCurrentObject.toString().trim())) {
					lObjCorrectionVO = new TrnPensionCorrectionDtls();
					if (lIntRowNumber != null) {
						lObjCorrectionVO.setFieldType(lStrFieldType + "~" + lIntRowNumber);
					} else {
						lObjCorrectionVO.setFieldType(lStrFieldType);
					}
					lObjCorrectionVO.setPrvsFieldValue("");
					lObjCorrectionVO.setCrntFieldValue(lObjCurrentObject.toString().trim());
				}
			}
			if (lObjCorrectionVO != null) {
				gObjCorrectionDtls.add(lObjCorrectionVO);
				if (lIntApprovalStatus != 0) {
					changeApprovalStatus(lIntApprovalStatus);
				}
				if (lIntRowNumber != null) {
					gLstFieldTypeListToDelete.add(lStrFieldType + "~" + lIntRowNumber);
				} else {
					gLstFieldTypeListToDelete.add(lStrFieldType);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is" + e, e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#
	 * generateCorrectionDtlsVOForDate(java.lang.String, java.lang.Object,
	 * java.lang.Object, java.lang.String, java.lang.Integer)
	 */
	public void generateCorrectionDtlsVOForDate(Integer lIntRowNumber, Object lObjApprovedObject, Object lObjCurrentObject, String lStrFieldType, Integer lIntApprovalStatus) throws Exception {

		TrnPensionCorrectionDtls lObjCorrectionVO = null;
		try {
			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			if (lObjApprovedObject != null && lObjCurrentObject != null) {
				if (!lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(lObjApprovedObject)).equals(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(lObjCurrentObject)))) {
					lObjCorrectionVO = new TrnPensionCorrectionDtls();
					if (lIntRowNumber != null) {
						lObjCorrectionVO.setFieldType(lStrFieldType + "~" + lIntRowNumber);
					} else {
						lObjCorrectionVO.setFieldType(lStrFieldType);
					}
					lObjCorrectionVO.setPrvsFieldValue(lObjSimpleDateFormat.format(lObjApprovedObject).toString().trim());
					lObjCorrectionVO.setCrntFieldValue(lObjSimpleDateFormat.format(lObjCurrentObject).toString().trim());
				}
			} else if (lObjApprovedObject != null && lObjCurrentObject == null) {
				if (lObjApprovedObject.toString().length() > 0) {
					lObjCorrectionVO = new TrnPensionCorrectionDtls();
					if (lIntRowNumber != null) {
						lObjCorrectionVO.setFieldType(lStrFieldType + "~" + lIntRowNumber);
					} else {
						lObjCorrectionVO.setFieldType(lStrFieldType);
					}
					lObjCorrectionVO.setPrvsFieldValue(lObjSimpleDateFormat.format(lObjApprovedObject).toString().trim());
					lObjCorrectionVO.setCrntFieldValue("");
				}
			} else if (lObjApprovedObject == null && lObjCurrentObject != null) {
				if (lObjCurrentObject.toString().length() > 0) {
					lObjCorrectionVO = new TrnPensionCorrectionDtls();
					if (lIntRowNumber != null) {
						lObjCorrectionVO.setFieldType(lStrFieldType + "~" + lIntRowNumber);
					} else {
						lObjCorrectionVO.setFieldType(lStrFieldType);
					}
					lObjCorrectionVO.setPrvsFieldValue("");
					lObjCorrectionVO.setCrntFieldValue(lObjSimpleDateFormat.format(lObjCurrentObject).toString().trim());
				}
			}
			if (lObjCorrectionVO != null) {
				gObjCorrectionDtls.add(lObjCorrectionVO);
				if (lIntApprovalStatus != 0) {
					changeApprovalStatus(lIntApprovalStatus);
				}
				if (lIntRowNumber != null) {
					gLstFieldTypeListToDelete.add(lStrFieldType + "~" + lIntRowNumber);
				} else {
					gLstFieldTypeListToDelete.add(lStrFieldType);
				}

			}
		} catch (Exception e) {
			gLogger.error("Error is" + e, e);
			throw e;
		}

	}

	private boolean getVerifyCurrentVoWithPreviousVo(Object lObjApprovedObject, Object lObjCurrentObject) {

		boolean lBLResult = false;
		if (lObjApprovedObject != null && lObjCurrentObject != null) {
			if (!lObjApprovedObject.toString().trim().equalsIgnoreCase(lObjCurrentObject.toString().trim())) {
				lBLResult = true;
			}
		} else if (lObjApprovedObject != null && lObjCurrentObject == null) {
			if (!"N".equals(lObjApprovedObject.toString()) && lObjApprovedObject.toString().length() > 0 && "-1".equals(lObjApprovedObject.toString()) && !"0.00".equals(lObjApprovedObject.toString())
					&& !"0".equals(lObjApprovedObject.toString())) {
				lBLResult = true;
			}
		} else if (lObjApprovedObject == null && lObjCurrentObject != null) {
			if (!"N".equals(lObjCurrentObject.toString()) && lObjCurrentObject.toString().length() > 0 && !"-1".equals(lObjCurrentObject.toString()) && !"0.00".equals(lObjCurrentObject.toString())
					&& !"0".equals(lObjCurrentObject.toString())) {
				lBLResult = true;
			}
		}
		return lBLResult;
	}

	private boolean getVerifyCurrentVoWithPreviousVoForAmount(Object lObjApprovedObject, Object lObjCurrentObject) {

		boolean lBLResult = false;
		if (lObjApprovedObject != null && lObjCurrentObject != null) {
			if (new BigDecimal(lObjApprovedObject.toString()).intValue() != new BigDecimal(lObjCurrentObject.toString()).intValue()) {
				lBLResult = true;
			}
		} else if (lObjApprovedObject != null && lObjCurrentObject == null) {
			if (lObjApprovedObject.toString().length() > 0 && "-1".equals(lObjApprovedObject.toString()) && !"0.00".equals(lObjApprovedObject.toString()) && !"0".equals(lObjApprovedObject.toString())) {
				lBLResult = true;
			}
		} else if (lObjApprovedObject == null && lObjCurrentObject != null) {
			if (lObjCurrentObject.toString().length() > 0 && !"-1".equals(lObjCurrentObject.toString()) && !"0.00".equals(lObjCurrentObject.toString()) && !"0".equals(lObjCurrentObject.toString())) {
				lBLResult = true;
			}
		}
		return lBLResult;
	}

	private boolean getVerifyCurrentVoWithPreviousVoForDate(Object lObjApprovedObject, Object lObjCurrentObject) {

		boolean lBLResult = false;
		try {
			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			if (lObjApprovedObject != null && lObjCurrentObject != null) {
				if (lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(lObjApprovedObject)) != lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(lObjCurrentObject))) {
					lBLResult = true;
				}
			} else if (lObjApprovedObject != null && lObjCurrentObject == null) {
				if (lObjApprovedObject.toString().length() > 0) {
					lBLResult = true;
				}
			} else if (lObjApprovedObject == null && lObjCurrentObject != null) {
				if (lObjCurrentObject.toString().length() > 0) {
					lBLResult = true;
				}
			}
		} catch (Exception e) {

		}
		return lBLResult;
	}

	public List<TrnPensionCaseMvmnt> generateTrnPensionCaseMvmntVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<TrnPensionCaseMvmnt> lLstTrnPensionCaseMvmntVO = new ArrayList<TrnPensionCaseMvmnt>();
		TrnPensionCaseMvmnt lObjTrnPensionCaseMvmntVO = null;
		String lStrPensionerCode = null;
		Integer lIntRowNumber = 1;

		try {
			String[] lArrStrOrderNo = StringUtility.getParameterValues("txtOrderNo", request);
			String[] lArrStrOrderDate = StringUtility.getParameterValues("txtOrderDate", request);
			String[] lArrStrRemarks = StringUtility.getParameterValues("txtRemarks", request);
			String lStrCurrRole = StringUtility.getParameter("currRole", request);
			setSessionInfo(inputMap);

			if (lArrStrOrderNo.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrOrderNo.length; lIntCnt++) {
					lObjTrnPensionCaseMvmntVO = new TrnPensionCaseMvmnt();

					if (lArrStrOrderNo[lIntCnt].trim().length() > 0) {
						lObjTrnPensionCaseMvmntVO.setOrderNo(lArrStrOrderNo[lIntCnt].trim());
					}
					if (lArrStrOrderDate[lIntCnt].trim().length() > 0) {
						lObjTrnPensionCaseMvmntVO.setOrderDate(StringUtility.convertStringToDate(lArrStrOrderDate[lIntCnt].trim()));
					}
					if (lArrStrRemarks[lIntCnt].trim().length() > 0) {
						lObjTrnPensionCaseMvmntVO.setRemarks((lArrStrRemarks[lIntCnt].trim()));
					}
					lObjTrnPensionCaseMvmntVO.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionCaseMvmntVO.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjTrnPensionCaseMvmntVO.setCreatedDate(DBUtility.getCurrentDateFromDB());
					
					if (!"".equals(lStrCurrRole) && lStrCurrRole.trim().length() > 0) {
						lObjTrnPensionCaseMvmntVO.setRoleId(Long.valueOf((lStrCurrRole)));
					}
					lObjTrnPensionCaseMvmntVO.setUpdatedDate(DBUtility.getCurrentDateFromDB());
					lObjTrnPensionCaseMvmntVO.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionCaseMvmntVO.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));

					lIntRowNumber++;
					lLstTrnPensionCaseMvmntVO.add(lObjTrnPensionCaseMvmntVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}

		return lLstTrnPensionCaseMvmntVO;

	}
	
	public ResultObject generateHstPnsnPmntDcrgDtlsVO(Map<String, Object> inputMap) throws Exception 
	{
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");		
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtlsVO = null;
		try
		{		
			setSessionInfo(inputMap);
			String lStrFlag = StringUtility.getParameter("flag", request);
			if(lStrFlag.equals("H"))
			{
				inputMap.put("Mode", "Update");
			}
			else
			{

				String lStrPensionerCode=StringUtility.getParameter("PensionerCode", request);
			    String lStrOrderNo = StringUtility.getParameter("GPONo", request);		    
			    String lStrOrderDate = StringUtility.getParameter("GPODate", request);
			    String lStrTotalOrderAmount = StringUtility.getParameter("TotalGratuityAmnt", request);
			    String lStrPaidAmount = StringUtility.getParameter("PayableAmount", request);
			    String lStrVoucherNo = StringUtility.getParameter("VoucherNo", request);
			    String lStrVoucherDate = StringUtility.getParameter("VoucherDate", request);
			    String lStrPaymentAuthority = StringUtility.getParameter("PayingAuthority", request);
			    String lStrWithHeldAmnt = StringUtility.getParameter("WithHeldAmnt", request);
			    String lStrTotalRecoveryAmnt = StringUtility.getParameter("TotRecoveryAmnt", request);
			    String lStrAmntAfterWithheld = 	StringUtility.getParameter("AmntAfterWithHeld", request);		
			    String lStrNameOfPayee = StringUtility.getParameter("nameOfPayee", request);
			    String lStrTrsryForPayment = StringUtility.getParameter("trsryForPayment", request);
				lObjHstPnsnPmntDcrgDtlsVO = new HstPnsnPmntDcrgDtls();
						
				lObjHstPnsnPmntDcrgDtlsVO.setDbId(gLngDBId); 
				lObjHstPnsnPmntDcrgDtlsVO.setLocationCode(Long.parseLong(gStrLocId));
				lObjHstPnsnPmntDcrgDtlsVO.setCreatedPostId(gLngPostId);
				lObjHstPnsnPmntDcrgDtlsVO.setCreatedUserId(gLngUserId);
				lObjHstPnsnPmntDcrgDtlsVO.setCreatedDate(DBUtility.getCurrentDateFromDB());		
				lObjHstPnsnPmntDcrgDtlsVO.setUpdatedDate(DBUtility.getCurrentDateFromDB());
				lObjHstPnsnPmntDcrgDtlsVO.setUpdatedPostId(gLngPostId);
				lObjHstPnsnPmntDcrgDtlsVO.setUpdatedUserId(gLngUserId);
				
				if(lStrPensionerCode.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setPensionerCode(lStrPensionerCode.trim());
				if(lStrOrderNo.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setOrderNo(lStrOrderNo.trim());
				if(lStrOrderDate.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setOrderDate(StringUtility.convertStringToDate(lStrOrderDate.trim()));
				if(lStrTotalOrderAmount.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setTotalOrderAmount(new BigDecimal(lStrTotalOrderAmount.trim()));
				if(lStrPaidAmount.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setPaidAmount(new BigDecimal(lStrPaidAmount.trim()));
				if(lStrVoucherNo.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setVoucherNo(lStrVoucherNo.trim());
				if(lStrVoucherDate.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setVoucherDate(StringUtility.convertStringToDate(lStrVoucherDate.trim()));
				if(lStrPaymentAuthority.length() > 0 && !"-1".equals(lStrPaymentAuthority))
					lObjHstPnsnPmntDcrgDtlsVO.setPaymentAuthority(lStrPaymentAuthority.trim());
				if(lStrWithHeldAmnt.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setWithHeldAmnt(new BigDecimal(lStrWithHeldAmnt.trim()));
				if(lStrTotalRecoveryAmnt.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setTotalRecoveryAmnt(new BigDecimal(lStrTotalRecoveryAmnt.trim()));
				if(lStrAmntAfterWithheld.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setAmntAfterWithHeld(new BigDecimal(lStrAmntAfterWithheld.trim()));
				if(lStrNameOfPayee.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setNameOfPayee(lStrNameOfPayee.trim());
				if(lStrTrsryForPayment.length() > 0 && !"-1".equals(lStrTrsryForPayment))
					lObjHstPnsnPmntDcrgDtlsVO.setTrsryForPayment(lStrTrsryForPayment);
				
				if(!"".equals(lStrPensionerCode) && lStrPensionerCode.length() > 0)
				{
					inputMap.put("lObjHstPnsnPmntDcrgDtlsVO", lObjHstPnsnPmntDcrgDtlsVO);
					inputMap.put("Mode", "Add");
				}
			}
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
		}
		catch(Exception e)
		{
			gLogger.error("Error is  " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;	
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardVOGenerator#generateTrnPensionArrearDtlsVO(java.util.Map)
	 */
	public List<TrnPensionArrearDtls> generateTrnPensionArrearDtlsVO(Map<String, Object> inputMap) throws Exception {

		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<TrnPensionArrearDtls> lLstTrnPensionArrearDtlsVO = new ArrayList<TrnPensionArrearDtls>();
		TrnPensionArrearDtls lObjTrnPensionArrearDtlsVO = null;
		String lStrPensionerCode = null;
		String lStrCorrectionFlag = null;
		String lStrPayInMonthYear ="";
		try {
			String lStrArrearDtlsIdForDA = StringUtility.getParameter("hdnArrearDtlsIdForDA", request);
			String lStrArrearPayOfDA = StringUtility.getParameter("txtArrearOfDA", request);
			String lStrArrearPayOfDAMonth = StringUtility.getParameter("cmbArrearOfDAPayMonth", request);
			String lStrArrearPayOfDAYear = StringUtility.getParameter("cmbArrearOfDAPayYear", request);
	
			String lStrArrearDtlsIdFor6PC = StringUtility.getParameter("hdnArrearDtlsIdFor6PC", request);
			String lStrArrearPayOf6PC = StringUtility.getParameter("txtArrearOf6PC", request);
			String lStrArrearPayOf6PCMonth = StringUtility.getParameter("cmbArrearOf6PCMonth", request);
			String lStrArrearPayOf6PCYear = StringUtility.getParameter("cmbArrearOf6PCYear", request);
			
			String lStrArrearDtlsIdForPnsn = StringUtility.getParameter("hdnArrearDtlsIdForPnsn", request);
			String lStrArrearPayOfPnsn = StringUtility.getParameter("txtArrearPayOfPnsn", request);
			String lStrArrearPayOfPnsnMonth = StringUtility.getParameter("cmbArrearOfPnsnMonth", request);
			String lStrArrearPayOfPnsnYear = StringUtility.getParameter("cmbArrearOfPnsnYear", request);
			
			String lStrArrearDtlsIdForComtPnsn = StringUtility.getParameter("hdnArrearDtlsIdForComtPnsn", request);
			String lStrArrearDiffOfComtPnsn = StringUtility.getParameter("txtArrearDiffOfComtPnsn", request);
			String lStrArrearDiffOfComtPnsnMonth = StringUtility.getParameter("cmbArrearDiffOfComtPnsnMonth", request);
			String lStrArrearDiffOfComtPnsnYear = StringUtility.getParameter("cmbArrearDiffOfComtPnsnYear", request);

			String lStrArrearDtlsIdForOther = StringUtility.getParameter("hdnArrearDtlsIdForOther", request);
			String lStrArrearAnyOtherDiff = StringUtility.getParameter("txtArrearAnyOtherDiff", request);
			String lStrArrearAnyOtherDiffMonth = StringUtility.getParameter("cmbArrearAnyOtherDiffMonth", request);
			String lStrArrearAnyOtherDiffYear = StringUtility.getParameter("cmbArrearAnyOtherDiffYear", request);

			String lStrMode = null;
			boolean isUpdate = false;
			setSessionInfo(inputMap);
			String lStrMonthYear ="";
			String lStrYear ="";
			String lStrMonth = "";

			if (inputMap.containsKey("Mode")) {
				lStrMode = inputMap.get("Mode").toString();
			}
			if (inputMap.containsKey("PensionerCode")) {
				lStrPensionerCode = inputMap.get("PensionerCode").toString();
			}
			if (inputMap.containsKey("CorrectionFlag")) {
				lStrCorrectionFlag = inputMap.get("CorrectionFlag").toString();
			}

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionArrearDtls.class, serv.getSessionFactory());
			//Arrear Payment of DA
			if(!"-1".equals(lStrArrearPayOfDAMonth))
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdForDA))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdForDA));
				}
				
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					isUpdate = true;
					lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
				}
				
				if(lObjTrnPensionArrearDtlsVO == null || (lObjTrnPensionArrearDtlsVO.getBillNo() != null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'Y') || isUpdate)
				{
					if(!isUpdate)
					{
						lObjTrnPensionArrearDtlsVO = new TrnPensionArrearDtls();
					}
					lObjTrnPensionArrearDtlsVO.setArrearFieldType(gObjRsrcBndle.getString("ARREARTYPE.DA"));
					
					if(!"".equals(lStrArrearPayOfDA))
						lObjTrnPensionArrearDtlsVO.setTotalDifferenceAmt(new BigDecimal(lStrArrearPayOfDA));
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), lObjTrnPensionArrearDtlsVO.getTotalDifferenceAmt(),
								"Arrear Payment of DA", 0);
					}
					if (!"".equals(lStrArrearPayOfDAMonth) && !"".equals(lStrArrearPayOfDAYear)) 
					{
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lObjPrvsTrnPensionArrearDtlsVO != null) {
								if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
									lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
									lStrYear = lStrMonthYear.substring(0, 4);
									lStrMonth = lStrMonthYear.substring(4, 6);
									lStrMonth = Integer.valueOf(lStrMonth).toString();
								}
							} else {
								lStrYear = null;
								lStrMonth = null;
							}
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, lStrArrearPayOfDAMonth, "Arrear Payment of DA Pay in Month", 0);
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, lStrArrearPayOfDAYear, "Arrear Payment of DA Pay in Year", 0);

						}
						if(Integer.parseInt(lStrArrearPayOfDAMonth) < 10){
							lStrPayInMonthYear = lStrArrearPayOfDAYear + "0" + lStrArrearPayOfDAMonth;
						} else {
							lStrPayInMonthYear = lStrArrearPayOfDAYear + lStrArrearPayOfDAMonth;
						}
						lObjTrnPensionArrearDtlsVO.setPaymentFromYyyymm(Integer.parseInt(lStrPayInMonthYear));
						lObjTrnPensionArrearDtlsVO.setPaymentToYyyymm(Integer.parseInt(lStrPayInMonthYear));
					}
					
					if (isUpdate) {
						lObjTrnPensionArrearDtlsVO.setOldAmountPercentage(null);
						lObjTrnPensionArrearDtlsVO.setNewAmountPercentage(null);
						lObjTrnPensionArrearDtlsVO.setEffectedFromYyyymm(null);
						lObjTrnPensionArrearDtlsVO.setEffectedToYyyymm(null);
						lObjTrnPensionArrearDtlsVO.setRemarks(null);
						lObjTrnPensionArrearDtlsVO.setUpdatedDate(gCurrDate);
						lObjTrnPensionArrearDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
						lObjPhysicalCaseInwardDAO.update(lObjTrnPensionArrearDtlsVO);
						gMapArrearDtlsId.put("DAArrearDtlsId", lObjTrnPensionArrearDtlsVO.getPensionArrearDtlsId());
					}
					else
					{
						lObjTrnPensionArrearDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
						lObjTrnPensionArrearDtlsVO.setCreatedDate(new Date());
						lObjTrnPensionArrearDtlsVO.setPaidFlag('N');
						lLstTrnPensionArrearDtlsVO.add(lObjTrnPensionArrearDtlsVO);
					}
				}
				isUpdate = false;
			}
			else
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdForDA))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdForDA));
				}
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						
						lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
						
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), "0",
								"Arrear Payment of DA", 0);
						
						if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
							lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
							lStrYear = lStrMonthYear.substring(0, 4);
							lStrMonth = lStrMonthYear.substring(4, 6);
							lStrMonth = Integer.valueOf(lStrMonth).toString();
						}
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, "", "Arrear Payment of DA Pay in Month", 0);
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, "", "Arrear Payment of DA Pay in Year", 0);

					}
					lObjPhysicalCaseInwardDAO.delete(lObjTrnPensionArrearDtlsVO);
				}
			}
			//Arrear Payment of 6 Pay Commission
			if(!"-1".equals(lStrArrearPayOf6PCMonth))
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdFor6PC))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdFor6PC));
				}
				
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					isUpdate = true;
					lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
				}
				
				if(lObjTrnPensionArrearDtlsVO == null || (lObjTrnPensionArrearDtlsVO.getBillNo() != null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'Y') || isUpdate)
				{
					if(!isUpdate)
					{
						lObjTrnPensionArrearDtlsVO = new TrnPensionArrearDtls();
					}
					lObjTrnPensionArrearDtlsVO.setArrearFieldType(gObjRsrcBndle.getString("ARREARTYPE.6PC"));
					
					if(!"".equals(lStrArrearPayOf6PC))
						lObjTrnPensionArrearDtlsVO.setTotalDifferenceAmt(new BigDecimal(lStrArrearPayOf6PC));
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), lObjTrnPensionArrearDtlsVO.getTotalDifferenceAmt(),
								"Arrear Payment of 6 PC", 0);
					}
					if (!"".equals(lStrArrearPayOf6PCMonth) && !"".equals(lStrArrearPayOf6PCYear)) 
					{
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lObjPrvsTrnPensionArrearDtlsVO != null) {
								if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
									lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
									lStrYear = lStrMonthYear.substring(0, 4);
									lStrMonth = lStrMonthYear.substring(4, 6);
									lStrMonth = Integer.valueOf(lStrMonth).toString();
								}
							} else {
								lStrYear = null;
								lStrMonth = null;
							}
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, lStrArrearPayOf6PCMonth, "Arrear Payment of 6 PC Pay in Month", 0);
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, lStrArrearPayOf6PCYear, "Arrear Payment of 6 PC Pay in Year", 0);

						}
						if(Integer.parseInt(lStrArrearPayOf6PCMonth) < 10){
							lStrPayInMonthYear = lStrArrearPayOf6PCYear + "0" + lStrArrearPayOf6PCMonth;
						} else {
							lStrPayInMonthYear = lStrArrearPayOf6PCYear + lStrArrearPayOf6PCMonth;
						}
						lObjTrnPensionArrearDtlsVO.setPaymentFromYyyymm(Integer.parseInt(lStrPayInMonthYear));
						lObjTrnPensionArrearDtlsVO.setPaymentToYyyymm(Integer.parseInt(lStrPayInMonthYear));
					}
					
					if (isUpdate) {
						
						lObjTrnPensionArrearDtlsVO.setUpdatedDate(gCurrDate);
						lObjTrnPensionArrearDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
						lObjPhysicalCaseInwardDAO.update(lObjTrnPensionArrearDtlsVO);
						gMapArrearDtlsId.put("6PCArrearDtlsId", lObjTrnPensionArrearDtlsVO.getPensionArrearDtlsId());
					}
					else
					{
						lObjTrnPensionArrearDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
						lObjTrnPensionArrearDtlsVO.setCreatedDate(new Date());
						lObjTrnPensionArrearDtlsVO.setPaidFlag('N');
						lLstTrnPensionArrearDtlsVO.add(lObjTrnPensionArrearDtlsVO);
					}
				}
				isUpdate = false;
			}
			else
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdFor6PC))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdFor6PC));
				}
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						
						lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
						
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), "0",
								"Arrear Payment of 6 PC", 0);
						
						if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
							lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
							lStrYear = lStrMonthYear.substring(0, 4);
							lStrMonth = lStrMonthYear.substring(4, 6);
							lStrMonth = Integer.valueOf(lStrMonth).toString();
						}
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, "", "Arrear Payment of 6 PC Pay in Month", 0);
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, "", "Arrear Payment of 6 PC Pay in Year", 0);

					}
					lObjPhysicalCaseInwardDAO.delete(lObjTrnPensionArrearDtlsVO);
				}
			}
			//Arrear Payment of Pension
			if(!"-1".equals(lStrArrearPayOfPnsnMonth))
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdForPnsn))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdForPnsn));
				}
				
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					isUpdate = true;
					lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
				}
				
				if(lObjTrnPensionArrearDtlsVO == null || (lObjTrnPensionArrearDtlsVO.getBillNo() != null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'Y') || isUpdate)
				{
					if(!isUpdate)
					{
						lObjTrnPensionArrearDtlsVO = new TrnPensionArrearDtls();
					}
					lObjTrnPensionArrearDtlsVO.setArrearFieldType(gObjRsrcBndle.getString("ARREARTYPE.PENSION"));
					
					if(!"".equals(lStrArrearPayOfPnsn))
						lObjTrnPensionArrearDtlsVO.setTotalDifferenceAmt(new BigDecimal(lStrArrearPayOfPnsn));
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), lObjTrnPensionArrearDtlsVO.getTotalDifferenceAmt(),
								"Arrear Payment of Pension", 0);
					}
					if (!"".equals(lStrArrearPayOfPnsnMonth) && !"".equals(lStrArrearPayOfPnsnYear)) 
					{
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lObjPrvsTrnPensionArrearDtlsVO != null) {
								if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
									lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
									lStrYear = lStrMonthYear.substring(0, 4);
									lStrMonth = lStrMonthYear.substring(4, 6);
									lStrMonth = Integer.valueOf(lStrMonth).toString();
								}
							} else {
								lStrYear = null;
								lStrMonth = null;
							}
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, lStrArrearPayOfPnsnMonth, "Arrear Payment of Pension Pay in Month", 0);
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, lStrArrearPayOfPnsnYear, "Arrear Payment of Pension Pay in Year", 0);

						}
						if(Integer.parseInt(lStrArrearPayOfPnsnMonth) < 10){
							lStrPayInMonthYear = lStrArrearPayOfPnsnYear + "0" + lStrArrearPayOfPnsnMonth;
						} else {
							lStrPayInMonthYear = lStrArrearPayOfPnsnYear + lStrArrearPayOfPnsnMonth;
						}
						lObjTrnPensionArrearDtlsVO.setPaymentFromYyyymm(Integer.parseInt(lStrPayInMonthYear));
						lObjTrnPensionArrearDtlsVO.setPaymentToYyyymm(Integer.parseInt(lStrPayInMonthYear));
					}
					
					if (isUpdate) {
						
						lObjTrnPensionArrearDtlsVO.setUpdatedDate(gCurrDate);
						lObjTrnPensionArrearDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
						lObjPhysicalCaseInwardDAO.update(lObjTrnPensionArrearDtlsVO);
						gMapArrearDtlsId.put("PensionArrearDtlsId", lObjTrnPensionArrearDtlsVO.getPensionArrearDtlsId());
					}
					else
					{
						lObjTrnPensionArrearDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
						lObjTrnPensionArrearDtlsVO.setCreatedDate(new Date());
						lObjTrnPensionArrearDtlsVO.setPaidFlag('N');
						lLstTrnPensionArrearDtlsVO.add(lObjTrnPensionArrearDtlsVO);
					}
				}
				isUpdate = false;
			}
			else
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdForPnsn))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdForPnsn));
				}
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						
						lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
						
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), "0",
								"Arrear Payment of Pension", 0);
						
						if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
							lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
							lStrYear = lStrMonthYear.substring(0, 4);
							lStrMonth = lStrMonthYear.substring(4, 6);
							lStrMonth = Integer.valueOf(lStrMonth).toString();
						}
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, "", "Arrear Payment of Pension Pay in Month", 0);
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, "", "Arrear Payment of Pension Pay in Year", 0);

					}
					lObjPhysicalCaseInwardDAO.delete(lObjTrnPensionArrearDtlsVO);
				}
			}
			
			//Difference of Commuted Pension
			if(!"-1".equals(lStrArrearDiffOfComtPnsnMonth))
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdForComtPnsn))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdForComtPnsn));
				}
				
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					isUpdate = true;
					lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
				}
				
				if(lObjTrnPensionArrearDtlsVO == null || (lObjTrnPensionArrearDtlsVO.getBillNo() != null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'Y') || isUpdate)
				{
					if(!isUpdate)
					{
						lObjTrnPensionArrearDtlsVO = new TrnPensionArrearDtls();
					}
					lObjTrnPensionArrearDtlsVO.setArrearFieldType(gObjRsrcBndle.getString("ARREARTYPE.COMPNSN"));
					
					if(!"".equals(lStrArrearDiffOfComtPnsn))
						lObjTrnPensionArrearDtlsVO.setTotalDifferenceAmt(new BigDecimal(lStrArrearDiffOfComtPnsn));
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), lObjTrnPensionArrearDtlsVO.getTotalDifferenceAmt(),
								"Difference of Commuted Pension", 0);
					}
					if (!"".equals(lStrArrearDiffOfComtPnsnMonth) && !"".equals(lStrArrearDiffOfComtPnsnYear)) 
					{
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lObjPrvsTrnPensionArrearDtlsVO != null) {
								if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
									lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
									lStrYear = lStrMonthYear.substring(0, 4);
									lStrMonth = lStrMonthYear.substring(4, 6);
									lStrMonth = Integer.valueOf(lStrMonth).toString();
								}
							} else {
								lStrYear = null;
								lStrMonth = null;
							}
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, lStrArrearDiffOfComtPnsnMonth, "Difference of Commuted Pension Pay in Month", 0);
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, lStrArrearDiffOfComtPnsnYear, "Difference of Commuted Pension Pay in Year", 0);

						}
						if(Integer.parseInt(lStrArrearDiffOfComtPnsnMonth) < 10){
							lStrPayInMonthYear = lStrArrearDiffOfComtPnsnYear + "0" + lStrArrearDiffOfComtPnsnMonth;
						} else {
							lStrPayInMonthYear = lStrArrearDiffOfComtPnsnYear + lStrArrearDiffOfComtPnsnMonth;
						}
						lObjTrnPensionArrearDtlsVO.setPaymentFromYyyymm(Integer.parseInt(lStrPayInMonthYear));
						lObjTrnPensionArrearDtlsVO.setPaymentToYyyymm(Integer.parseInt(lStrPayInMonthYear));
					}
					
					if (isUpdate) {
						
						lObjTrnPensionArrearDtlsVO.setUpdatedDate(gCurrDate);
						lObjTrnPensionArrearDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
						lObjPhysicalCaseInwardDAO.update(lObjTrnPensionArrearDtlsVO);
						gMapArrearDtlsId.put("ComtPnsnArrearDtlsId", lObjTrnPensionArrearDtlsVO.getPensionArrearDtlsId());
					}
					else
					{
						lObjTrnPensionArrearDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
						lObjTrnPensionArrearDtlsVO.setCreatedDate(new Date());
						lObjTrnPensionArrearDtlsVO.setPaidFlag('N');
						lLstTrnPensionArrearDtlsVO.add(lObjTrnPensionArrearDtlsVO);
					}
				}
				isUpdate = false;
			}
			else
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdForComtPnsn))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdForComtPnsn));
				}
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						
						lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
						
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), "0",
								"Difference of Commuted Pension", 0);
						
						if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
							lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
							lStrYear = lStrMonthYear.substring(0, 4);
							lStrMonth = lStrMonthYear.substring(4, 6);
							lStrMonth = Integer.valueOf(lStrMonth).toString();
						}
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, "", "Difference of Commuted Pension Pay in Month", 0);
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, "", "Difference of Commuted Pension Pay in Year", 0);

					}
					lObjPhysicalCaseInwardDAO.delete(lObjTrnPensionArrearDtlsVO);
				}
			}
			
			//Any Other Difference
			if(!"-1".equals(lStrArrearAnyOtherDiffMonth))
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdForOther))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdForOther));
				}
				
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					isUpdate = true;
					lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
				}
				
				if(lObjTrnPensionArrearDtlsVO == null || (lObjTrnPensionArrearDtlsVO.getBillNo() != null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'Y') || isUpdate)
				{
					if(!isUpdate)
					{
						lObjTrnPensionArrearDtlsVO = new TrnPensionArrearDtls();
					}
					lObjTrnPensionArrearDtlsVO.setArrearFieldType(gObjRsrcBndle.getString("ARREARTYPE.OTHERDIFF"));
					
					if(!"".equals(lStrArrearAnyOtherDiff))
						lObjTrnPensionArrearDtlsVO.setTotalDifferenceAmt(new BigDecimal(lStrArrearAnyOtherDiff));
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), lObjTrnPensionArrearDtlsVO.getTotalDifferenceAmt(),
								"Any Other Difference Amount", 0);
					}
					if (!"".equals(lStrArrearAnyOtherDiffMonth) && !"".equals(lStrArrearAnyOtherDiffYear)) 
					{
						if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
							if (lObjPrvsTrnPensionArrearDtlsVO != null) {
								if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
									lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
									lStrYear = lStrMonthYear.substring(0, 4);
									lStrMonth = lStrMonthYear.substring(4, 6);
									lStrMonth = Integer.valueOf(lStrMonth).toString();
								}
							} else {
								lStrYear = null;
								lStrMonth = null;
							}
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, lStrArrearAnyOtherDiffMonth, "Any Other Difference Amount Pay in Month", 0);
							generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, lStrArrearAnyOtherDiffYear, "Any Other Difference Amount Pay in Year", 0);

						}
						if(Integer.parseInt(lStrArrearAnyOtherDiffMonth) < 10){
							lStrPayInMonthYear = lStrArrearAnyOtherDiffYear + "0" + lStrArrearAnyOtherDiffMonth;
						} else {
							lStrPayInMonthYear = lStrArrearAnyOtherDiffYear + lStrArrearAnyOtherDiffMonth;
						}
						lObjTrnPensionArrearDtlsVO.setPaymentFromYyyymm(Integer.parseInt(lStrPayInMonthYear));
						lObjTrnPensionArrearDtlsVO.setPaymentToYyyymm(Integer.parseInt(lStrPayInMonthYear));
					}
					
					if (isUpdate) {
						
						lObjTrnPensionArrearDtlsVO.setUpdatedDate(gCurrDate);
						lObjTrnPensionArrearDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
						lObjPhysicalCaseInwardDAO.update(lObjTrnPensionArrearDtlsVO);
						gMapArrearDtlsId.put("OtherArrearDtlsId", lObjTrnPensionArrearDtlsVO.getPensionArrearDtlsId());
					}
					else
					{
						lObjTrnPensionArrearDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
						lObjTrnPensionArrearDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
						lObjTrnPensionArrearDtlsVO.setCreatedDate(new Date());
						lObjTrnPensionArrearDtlsVO.setPaidFlag('N');
						lLstTrnPensionArrearDtlsVO.add(lObjTrnPensionArrearDtlsVO);
					}
				}
				isUpdate = false;
			}
			else
			{
				lObjTrnPensionArrearDtlsVO = null;
				TrnPensionArrearDtls lObjPrvsTrnPensionArrearDtlsVO  = new TrnPensionArrearDtls();
				if(!"".equals(lStrArrearDtlsIdForOther))
				{
					lObjTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjPhysicalCaseInwardDAO.read(Long.parseLong(lStrArrearDtlsIdForOther));
				}
				if(lObjTrnPensionArrearDtlsVO != null && lObjTrnPensionArrearDtlsVO.getBillNo() == null && lObjTrnPensionArrearDtlsVO.getPaidFlag() == 'N')
				{
					if ("Update".equalsIgnoreCase(lStrMode) && "Y".equalsIgnoreCase(lStrCorrectionFlag)) {
						
						lObjPrvsTrnPensionArrearDtlsVO = (TrnPensionArrearDtls) lObjTrnPensionArrearDtlsVO.clone();
						
						generateCorrectionDtlsVOForAmount(null, lObjPrvsTrnPensionArrearDtlsVO == null ? null : lObjPrvsTrnPensionArrearDtlsVO.getTotalDifferenceAmt(), "0",
								"Any Other Difference Amount", 0);
						
						if (lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm() != null) {
							lStrMonthYear = lObjPrvsTrnPensionArrearDtlsVO.getPaymentFromYyyymm().toString();
							lStrYear = lStrMonthYear.substring(0, 4);
							lStrMonth = lStrMonthYear.substring(4, 6);
							lStrMonth = Integer.valueOf(lStrMonth).toString();
						}
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrMonth, "", "Any Other Difference Amount Pay in Month", 0);
						generateCorrectionDtlsVO(null, lObjPrvsTrnPensionArrearDtlsVO == null ? "" : lStrYear, "", "Any Other Difference Amount Pay in Year", 0);

					}
					lObjPhysicalCaseInwardDAO.delete(lObjTrnPensionArrearDtlsVO);
				}
			}
			
		}
		catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}

		return lLstTrnPensionArrearDtlsVO;
	}

	
	

}
