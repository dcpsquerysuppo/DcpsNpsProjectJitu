/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 19, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.common.valueobject.RltBankBranch;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pension.valueobject.TrnPrvosionalPensionDtls;
import com.tcs.sgv.pensionpay.dao.AdminRateMstDAO;
import com.tcs.sgv.pensionpay.dao.AdminRateMstDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.IdentificationSchedularDAO;
import com.tcs.sgv.pensionpay.dao.IdentificationSchedularDAOImpl;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.MstPensionerDtlsDAO;
import com.tcs.sgv.pensionpay.dao.MstPensionerDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAO;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionBillDAO;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAO;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.HstReEmploymentDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerNomineeDtls;
import com.tcs.sgv.pensionpay.valueobject.PensionAuditRegisterVO;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.TrnCvpRestorationDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCaseMvmnt;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCorrectionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCutDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsnpmntSchedular;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalPensionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalVoucherDtls;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Apr 19, 2011
 */
public class PhysicalCaseInwardServiceImpl extends ServiceImpl implements PhysicalCaseInwardService {

	private Long gLngPostId = null;

	private Long gLngUserId = null;

	private Long gLngLangId = null;

	private Log gLogger = LogFactory.getLog(getClass());

	private Date gCurrDate = null;

	private String gStrLocCode = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private ResourceBundle gObjLblRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels");

	private void setSessionInfo(Map<String, Object> inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gCurrDate = DBUtility.getCurrentDateFromDB();
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	public ResultObject loadPhysicalPensionCase(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = new TrnPensionRqstHdr();
		MstPensionerHdr lObjMstPensionerHdrVO = new MstPensionerHdr();
		MstPensionerDtls lObjMstPensionerDtlsVO = new MstPensionerDtls();
		TrnPensionArrearDtls lObjTrnPnsnArrearDtlsForDA = new TrnPensionArrearDtls();
		TrnPensionArrearDtls lObjTrnPnsnArrearDtlsFor6PC = new TrnPensionArrearDtls();
		TrnPensionArrearDtls lObjTrnPnsnArrearDtlsForPnsn = new TrnPensionArrearDtls();
		TrnPensionArrearDtls lObjTrnPnsnArrearDtlsForComtPnsn = new TrnPensionArrearDtls();
		TrnPensionArrearDtls lObjTrnPnsnArrearDtlsForOther = new TrnPensionArrearDtls();
		List<TrnProvisionalPensionDtls> lLstTrnProvisionalPensionDtlsVO = new ArrayList<TrnProvisionalPensionDtls>();
		TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
		List<MstPensionerFamilyDtls> lLstMstPensionerFamilyDtls = new ArrayList<MstPensionerFamilyDtls>();
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtlsVO = new HstPnsnPmntDcrgDtls();
		List<MstPensionerNomineeDtls> lLstMstPensionerNomineeDtls = new ArrayList<MstPensionerNomineeDtls>();
		List<List> lLstPensionRecoveryDtls = new ArrayList<List>();
		List<TrnPensionRecoveryDtls> lLstAdvanceDtls = new ArrayList<TrnPensionRecoveryDtls>();
		List<TrnPensionRecoveryDtls> lLstRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
		List<TrnPensionRecoveryDtls> lLstAssessedDuesDtls = new ArrayList<TrnPensionRecoveryDtls>();
		List<TrnPensionRecoveryDtls> lLstDCRGRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
		List<TrnProvisionalVoucherDtls> lLstTrnProvisionalVoucherDtls = new ArrayList<TrnProvisionalVoucherDtls>();
		List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtlsVO = new ArrayList<TrnCvpRestorationDtls>();
		List<HstReEmploymentDtls> lLstHstReEmploymentDtlsVO = new ArrayList<HstReEmploymentDtls>();
		List<TrnPensionCutDtls> lLstTrnPensionCutDtlsVO = new ArrayList<TrnPensionCutDtls>();

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<ComboValuesVO> lLstDesignation = null;
		List<ComboValuesVO> lLstDepartment = null;
		List<ComboValuesVO> lLstTreasury = null;
		List<ComboValuesVO> lLstState = null;
		List<ComboValuesVO> lLstDistricts = null;
		List<ComboValuesVO> lLstHeadCode = null;
		List<ComboValuesVO> lLstBanks = null;
		List<ComboValuesVO> lLstMonths = null;
		List<ComboValuesVO> lLstYears = null;
		List<CmnLookupMst> lLstClassType = null;
		List<CmnLookupMst> lLstRelation = null;
		List<CmnLookupMst> lLstTypeOfAdvance = null;
		List<ComboValuesVO> lLstPnsnrDistricts = null;
		List<ComboValuesVO> lLstGuardianDistricts = null;
		List<ComboValuesVO> lLstBankBranch = null;
		String lStrHeadCodeDesc = null;
		String lStrIfscCode = null;
		BigDecimal lBgDcmlOverPayAmt = BigDecimal.ZERO;
		Integer lIntOverPayFromMonth = null;
		String lStrOverPaySchemeCode = null;
		BigDecimal lBgDcmlArrearOfFee = BigDecimal.ZERO;
		Integer lIntArrearOfFeeFromMonth = null;
		String lStrArrearSchemeCode = null;
		BigDecimal lBgDcmlAmtOfLicenceFee = BigDecimal.ZERO;
		Integer lIntAmtOfLicenceFeeFromMonth = null;
		String lStrLicFeeShemeCode = null;
		Set<CmnAttachmentMpg> cmnAttachmentMpgs = null;
		Iterator<CmnAttachmentMpg> cmnAttachmentMpgIterator = null;
		CmnAttachmentMpg cmnAttachmentMpg = null;
		Long lLngPhotoAttachmentId = 0l;
		Long lLngSignAttachmentId = 0l;
		List<Long> lLstTreasuryId = new ArrayList<Long>();
		String lStrTreasuryName = null;
		String lStrToRole = "";
		String lStrShowReadOnly = null;
		String lStrShowCaseFor = null;
		String lStrShowApprove = null;
		String lStrCallDate = null;
		String lStrCallSlotNo = null;
		String lStrShowHistoryBtn = "";
		AdminRateMstDAO lObjAdminRateMstDAO = null;
		List lLstStateDept = null;
		String lStrSupplFlag = "";
		try {
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASURYID1")));
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASIRYID2")));
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lObjAdminRateMstDAO = new AdminRateMstDAOImpl(serv.getSessionFactory());
			lStrToRole = StringUtility.getParameter("currRole", request).trim();
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			lLstStateDept = lObjAdminRateMstDAO.getAllStateDept(gLngLangId);
			lStrShowReadOnly = StringUtility.getParameter("showReadOnly", request);
			lStrShowCaseFor = StringUtility.getParameter("showCaseFor", request);
			lStrShowHistoryBtn = StringUtility.getParameter("showHistoryBtn", request);
			lStrSupplFlag = StringUtility.getParameter("supplFlag", request);
			// lStrToRole = lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			// lLstDesignation = lObjCommonDAO.getAllDesignation(gLngLangId);
			lLstTreasury = lObjPhysicalCaseInwardDAO.getAllTreasury(lLstTreasuryId, gLngLangId);
			Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle.getString("PPMT.DEPARTMENTID"));
			lLstDepartment = lObjCommonDAO.getAllDepartment(lLngDepartmentId, gLngLangId);
			lLstState = lObjCommonDAO.getAllState(gLngLangId);
			lLstDistricts = lObjCommonDAO.getDistrictsOfState(15L, gLngLangId);
			lLstClassType = IFMSCommonServiceImpl.getLookupValues("Class Type", gLngLangId, inputMap);
			lLstHeadCode = lObjCommonPensionDAO.getAllHeadCode();
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);
			lLstRelation = IFMSCommonServiceImpl.getLookupValues("RelationShip List", gLngLangId, inputMap);
			//lLstTypeOfAdvance = IFMSCommonServiceImpl.getLookupValues("Advance Type", gLngLangId, inputMap);
			lLstMonths = lObjCommonDAO.getMonthList(SessionHelper.getLocale(request));
			lLstYears = lObjCommonDAO.getYearList(SessionHelper.getLocale(request));
			lStrTreasuryName = lObjPhysicalCaseInwardDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocCode));
			lStrShowApprove = StringUtility.getParameter("showApprove", request);
			lStrCallDate = StringUtility.getParameter("callDate", request);
			lStrCallSlotNo = StringUtility.getParameter("callSlotNo", request);

			// inputMap.put("lLstDesignation", lLstDesignation);
			inputMap.put("lLstStateDept", lLstStateDept);
			inputMap.put("lLstDepartment", lLstDepartment);
			inputMap.put("lLstTreasury", lLstTreasury);
			inputMap.put("lLstState", lLstState);
			inputMap.put("lLstDistricts", lLstDistricts);
			inputMap.put("lLstClassType", lLstClassType);
			inputMap.put("lLstHeadCode", lLstHeadCode);
			inputMap.put("lLstBanks", lLstBanks);
			inputMap.put("lLstRelation", lLstRelation);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstTypeOfAdvance", lLstTypeOfAdvance);
			inputMap.put("CurrDate", lObjSimpleDateFormat.format(gCurrDate));
			inputMap.put("TreasuryName", lStrTreasuryName);
			inputMap.put("lStrToRole", lStrToRole);
			inputMap.put("lStrShowReadOnly", lStrShowReadOnly);
			inputMap.put("lStrShowApprove", lStrShowApprove);
			inputMap.put("lStrShowCaseFor", lStrShowCaseFor);
			inputMap.put("lStrCallDate", lStrCallDate);
			inputMap.put("lStrCallSlotNo", lStrCallSlotNo);
			inputMap.put("lStrShowHistoryBtn", lStrShowHistoryBtn);
			inputMap.put("SupplFlag", lStrSupplFlag);
			
			resObj.setViewName("PhysicalCaseInwardForm");
			// when pensioner id is not null
			if (StringUtility.getParameter("pensionerId", request).length() > 0) {
				String lStrPensionerId = StringUtility.getParameter("pensionerId", request);
				List<String> lLstRecoveryFrom = new ArrayList<String>();
				lLstRecoveryFrom.add(gObjRsrcBndle.getString("PPMT.MONTHLY"));
				lLstRecoveryFrom.add(gObjRsrcBndle.getString("PPMT.DCRG"));
				lObjMstPensionerHdrVO = lObjPhysicalCaseInwardDAO.getMstPensionerHdrVO(lStrPensionerId);

				// get photo and signature dtls
				if (lObjMstPensionerHdrVO != null) {
					Long lLngSrNo = 0L;
					CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
					CmnAttachmentMst lObjCmnAttachmentMst = null;

					if (lObjMstPensionerHdrVO.getPensionerPhotoId() != null && lObjMstPensionerHdrVO.getPensionerPhotoId().doubleValue() > 0) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lObjMstPensionerHdrVO.getPensionerPhotoId().toString()));

						cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

						if (lObjCmnAttachmentMst != null) {
							lLngPhotoAttachmentId = lObjCmnAttachmentMst.getAttachmentId();
						}
						if (lObjCmnAttachmentMst != null && lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
							cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
						}
						cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();

						for (int j = 0; j < cmnAttachmentMpgs.size(); j++) {
							cmnAttachmentMpg = cmnAttachmentMpgIterator.next();
							if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("photo")) {
								lLngSrNo = cmnAttachmentMpg.getSrNo();
								inputMap.put("Photo", lObjCmnAttachmentMst);
								inputMap.put("PhotoId", lLngPhotoAttachmentId);
								inputMap.put("PhotosrNo", lLngSrNo);
							}
						}
					}

					if (lObjMstPensionerHdrVO.getPensionerSignId() != null && lObjMstPensionerHdrVO.getPensionerSignId().doubleValue() > 0) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lObjMstPensionerHdrVO.getPensionerSignId().toString()));

						cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

						if (lObjCmnAttachmentMst != null) {
							lLngSignAttachmentId = lObjCmnAttachmentMst.getAttachmentId();
						}
						if (lObjCmnAttachmentMst != null && lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
							cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
						}

						cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();

						for (int j = 0; j < cmnAttachmentMpgs.size(); j++) {
							cmnAttachmentMpg = cmnAttachmentMpgIterator.next();
							if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("signature")) {
								lLngSrNo = cmnAttachmentMpg.getSrNo();
								inputMap.put("Sign", lObjCmnAttachmentMst);
								inputMap.put("SignId", lLngSignAttachmentId);
								inputMap.put("SignsrNo", lLngSrNo);
							}
						}
					}
					lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
					if (lObjMstPensionerHdrVO.getPpoScanId() != null) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(lObjMstPensionerHdrVO.getPpoScanId());
						inputMap.put("scan", lObjCmnAttachmentMst);
					}
				}

				lObjTrnPensionRqstHdrVO = lObjPhysicalCaseInwardDAO.getTrnPensionRqstHdrVO(lStrPensionerId);
				lStrTreasuryName = lObjPhysicalCaseInwardDAO.getTreasuryName(gLngLangId, Long.parseLong(lObjTrnPensionRqstHdrVO.getLocationCode()));
				if (lObjTrnPensionRqstHdrVO.getHeadCode() != null) {
					lStrHeadCodeDesc = lObjPhysicalCaseInwardDAO.getHeadCodeDesc(lObjTrnPensionRqstHdrVO.getHeadCode().toString(), gLngLangId);
				}

				lObjMstPensionerDtlsVO = lObjPhysicalCaseInwardDAO.getMstPensionerDtlsVO(lStrPensionerId);
				if (lObjMstPensionerDtlsVO.getBankCode() != null) {
					lLstBankBranch = lObjCommonDAO.getBranchListFromBankCode(Long.parseLong(lObjMstPensionerDtlsVO.getBankCode()), lObjMstPensionerDtlsVO.getLocationCode(), gLngLangId);
					if (lObjMstPensionerDtlsVO.getBranchCode() != null) {
						lStrIfscCode = lObjPhysicalCaseInwardDAO.getIfscCodeFromBranchCode(Long.parseLong(lObjMstPensionerDtlsVO.getBranchCode()), gStrLocCode);
					}
				}

				lLstMstPensionerFamilyDtls = lObjPhysicalCaseInwardDAO.getMstPensionerFamilyDtlsVO(lStrPensionerId);
				lLstMstPensionerNomineeDtls = lObjPhysicalCaseInwardDAO.getMstPensionerNomineeDtlsVO(lStrPensionerId);
				lLstTrnProvisionalPensionDtlsVO = lObjPhysicalCaseInwardDAO.getTrnProvisionalPensionDtlsVO(lStrPensionerId);
				lLstPensionRecoveryDtls = lObjPhysicalCaseInwardDAO.getTrnPensionRecoveryDtlsVO(lStrPensionerId, lLstRecoveryFrom);
				lLstAdvanceDtls = lLstPensionRecoveryDtls.get(0);
//				lLstRecoveryDtls = lLstPensionRecoveryDtls.get(1);
//				lLstAssessedDuesDtls = lLstPensionRecoveryDtls.get(2);
				lLstDCRGRecoveryDtls = lLstPensionRecoveryDtls.get(1);
				if (lObjMstPensionerHdrVO.getStateCode() != null) {
					lLstPnsnrDistricts = lObjCommonDAO.getDistrictsOfState(Long.parseLong(lObjMstPensionerHdrVO.getStateCode().toString()), gLngLangId);
				}
				if (lObjMstPensionerHdrVO.getGuardianAddrState() != null) {
					lLstGuardianDistricts = lObjCommonDAO.getDistrictsOfState(Long.parseLong(lObjMstPensionerHdrVO.getGuardianAddrState().toString()), gLngLangId);
				}
				lLstTrnProvisionalVoucherDtls = lObjPhysicalCaseInwardDAO.getTrnProvisionalVoucherDtlsVO(lStrPensionerId);
//				for (int lIntCnt = 0; lIntCnt < lLstRecoveryDtls.size(); lIntCnt++) {
//					lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
//					lObjTrnPensionRecoveryDtlsVO = lLstRecoveryDtls.get(lIntCnt);
//					if (gObjRsrcBndle.getString("PPMT.OVERPAY").equalsIgnoreCase(lObjTrnPensionRecoveryDtlsVO.getRecoveryType())) {
//						lBgDcmlOverPayAmt = lObjTrnPensionRecoveryDtlsVO.getAmount();
//						lIntOverPayFromMonth = lObjTrnPensionRecoveryDtlsVO.getFromMonth();
//						lStrOverPaySchemeCode = lObjTrnPensionRecoveryDtlsVO.getSchemeCode();
//					} else if (gObjRsrcBndle.getString("PPMT.ARREAR").equalsIgnoreCase(lObjTrnPensionRecoveryDtlsVO.getRecoveryType())) {
//						lBgDcmlArrearOfFee = lObjTrnPensionRecoveryDtlsVO.getAmount();
//						lIntArrearOfFeeFromMonth = lObjTrnPensionRecoveryDtlsVO.getFromMonth();
//						lStrArrearSchemeCode = lObjTrnPensionRecoveryDtlsVO.getSchemeCode();
//					} else {
//						lBgDcmlAmtOfLicenceFee = lObjTrnPensionRecoveryDtlsVO.getAmount();
//						lIntAmtOfLicenceFeeFromMonth = lObjTrnPensionRecoveryDtlsVO.getFromMonth();
//						lStrLicFeeShemeCode = lObjTrnPensionRecoveryDtlsVO.getSchemeCode();
//					}
//				}
				lLstTrnCvpRestorationDtlsVO = lObjPhysicalCaseInwardDAO.getTrnCvpRestorationDtlsVO(lStrPensionerId);
				lLstHstReEmploymentDtlsVO = lObjPhysicalCaseInwardDAO.getHstReEmploymentDtlsVO(lStrPensionerId);
				lLstTrnPensionCutDtlsVO = lObjPhysicalCaseInwardDAO.getPensionCutDtls(lStrPensionerId);
				
				lObjTrnPnsnArrearDtlsForDA = lObjPhysicalCaseInwardDAO.getTrnPensionArrearDtlsVO(lStrPensionerId, gObjRsrcBndle.getString("ARREARTYPE.DA"));
				lObjTrnPnsnArrearDtlsFor6PC = lObjPhysicalCaseInwardDAO.getTrnPensionArrearDtlsVO(lStrPensionerId, gObjRsrcBndle.getString("ARREARTYPE.6PC"));
				lObjTrnPnsnArrearDtlsForPnsn = lObjPhysicalCaseInwardDAO.getTrnPensionArrearDtlsVO(lStrPensionerId, gObjRsrcBndle.getString("ARREARTYPE.PENSION"));
				lObjTrnPnsnArrearDtlsForComtPnsn= lObjPhysicalCaseInwardDAO.getTrnPensionArrearDtlsVO(lStrPensionerId, gObjRsrcBndle.getString("ARREARTYPE.COMPNSN"));
				lObjTrnPnsnArrearDtlsForOther = lObjPhysicalCaseInwardDAO.getTrnPensionArrearDtlsVO(lStrPensionerId, gObjRsrcBndle.getString("ARREARTYPE.OTHERDIFF"));
				

				String[] lArrStrHeight = null;
				String lStrHeightIn = null;
				String lStrHeightInCms = null;
				String lStrHeightInFt = null;
				String lStrHeightInInch = null;
				if (lObjMstPensionerHdrVO.getHeight() != null) {
					lArrStrHeight = lObjMstPensionerHdrVO.getHeight().split("~");

					if (gObjRsrcBndle.getString("HEIGHT.CMS").equals(lArrStrHeight[0])) {
						if (lArrStrHeight.length < 2) {

							lStrHeightIn = lArrStrHeight[0];
							lStrHeightInCms = " ";

						} else if (lArrStrHeight.length < 3) {
							lStrHeightIn = lArrStrHeight[0];
							lStrHeightInCms = lArrStrHeight[1];

						}
						inputMap.put("HeightIn", lStrHeightIn);
						inputMap.put("HeightInCms", lStrHeightInCms);

					}
					if (gObjRsrcBndle.getString("HEIGHT.FTINCHES").equals(lArrStrHeight[0])) {
						if (lArrStrHeight.length < 2) {

							lStrHeightIn = lArrStrHeight[0];
							lStrHeightInFt = " ";
							lStrHeightInInch = " ";

						} else if (lArrStrHeight.length < 3) {
							lStrHeightIn = lArrStrHeight[0];
							lStrHeightInFt = lArrStrHeight[1];
							lStrHeightInInch = " ";
						} else if (lArrStrHeight.length == 3) {
							lStrHeightIn = lArrStrHeight[0];
							lStrHeightInFt = lArrStrHeight[1];
							lStrHeightInInch = lArrStrHeight[2];
						}
						inputMap.put("HeightIn", lStrHeightIn);
						inputMap.put("HeightInFt", lStrHeightInFt);
						inputMap.put("HeightInInch", lStrHeightInInch);
					}
				}
				// to get data from dcrg history
				lObjHstPnsnPmntDcrgDtlsVO = lObjPhysicalCaseInwardDAO.getHstPnsnPmntDcrgDtlsVO(lStrPensionerId);

				inputMap.put("TrnPensionRqstHdrVO", lObjTrnPensionRqstHdrVO);
				inputMap.put("MstPensionerHdrVO", lObjMstPensionerHdrVO);
				inputMap.put("lLstPnsnrDistricts", lLstPnsnrDistricts);
				inputMap.put("lLstGuardianDistricts", lLstGuardianDistricts);
				inputMap.put("HstPnsnPmntDcrgDtls", lObjHstPnsnPmntDcrgDtlsVO);
				inputMap.put("MstPensionerDtlsVO", lObjMstPensionerDtlsVO);
				inputMap.put("lLstPnsnrBankBranch", lLstBankBranch);
				inputMap.put("MstPensionerFamilyDtlsVO", lLstMstPensionerFamilyDtls);
				inputMap.put("MstPensionerNomineeDtlsVO", lLstMstPensionerNomineeDtls);
				inputMap.put("TrnProvisionalPensionDtlsVO", lLstTrnProvisionalPensionDtlsVO);
				inputMap.put("TrnPensionCutDtlsVO", lLstTrnPensionCutDtlsVO);
				inputMap.put("AdvanceDtls", lLstAdvanceDtls);
				inputMap.put("RecoveryDtls", lLstRecoveryDtls);
				inputMap.put("AssessedDuesDtls", lLstAssessedDuesDtls);
				inputMap.put("DCRGRecoveryDtls", lLstDCRGRecoveryDtls);
				inputMap.put("HeadCodeDesc", lStrHeadCodeDesc);
				inputMap.put("IfscCode", lStrIfscCode);
				inputMap.put("OverPayAmt", lBgDcmlOverPayAmt);
				inputMap.put("OverPayFromMonth", lIntOverPayFromMonth);
				inputMap.put("OverPaySchemeCode", lStrOverPaySchemeCode);
				inputMap.put("ArrearOfFee", lBgDcmlArrearOfFee);
				inputMap.put("ArrearOfFeeFromMonth", lIntArrearOfFeeFromMonth);
				inputMap.put("ArrearSchemeCode", lStrArrearSchemeCode);
				inputMap.put("AmtOfLicenceFee", lBgDcmlAmtOfLicenceFee);
				inputMap.put("AmtOfLicenceFeeFromMonth", lIntAmtOfLicenceFeeFromMonth);
				inputMap.put("TrnProvisionalVoucherDtls", lLstTrnProvisionalVoucherDtls);
				inputMap.put("LicFeeShemeCode", lStrLicFeeShemeCode);
				inputMap.put("TrnCvpRestorationDtls", lLstTrnCvpRestorationDtlsVO);
				inputMap.put("HstReEmploymentDtls", lLstHstReEmploymentDtlsVO);
				inputMap.put("TrnPnsnArrearDtlsForDA", lObjTrnPnsnArrearDtlsForDA);
				inputMap.put("TrnPnsnArrearDtlsFor6PC", lObjTrnPnsnArrearDtlsFor6PC);
				inputMap.put("TrnPnsnArrearDtlsForPnsn", lObjTrnPnsnArrearDtlsForPnsn);
				inputMap.put("TrnPnsnArrearDtlsForComtPnsn", lObjTrnPnsnArrearDtlsForComtPnsn);
				inputMap.put("TrnPnsnArrearDtlsForOther", lObjTrnPnsnArrearDtlsForOther);
				inputMap.put("TreasuryName", lStrTreasuryName);
				resObj.setViewName("HeaderField");
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#
	 * insertPhysicalPensionCase(java.util.Map)
	 */
	public ResultObject insertPhysicalPensionCase(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		StringBuilder lStrBldXML = null;
		Long lLngPensionerId = null;
		Long lLngTrnPensionerHdrId = null;
		Long lLngMstPensionerDtlsId = null;
		Long lLngProvisionalPensionDtlsId = null;
		Long lLngProvVoucherDtlsId = null;
		Long lLngFamilyMemberId = null;
		Long lLngNomineeDtlId = null;
		Long lLngPensionRecoveryDtlsId = null;
		Long lLngCvpRestnDtlsId = null;
		Long lLngReEmploymentDtlsId = null;
		Long lLngCommutationDtlsId = null;
		Long lLngTrnPnsnCaseMvmntId = null;
		String lStrCorrectionFlag = null;
		boolean lValidPpoFlag = true;
		Map attachMap = null;
		MstPensionerHdr lObjMstPensionerHdrVO = new MstPensionerHdr();
		TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = new TrnPensionRqstHdr();
		MstPensionerDtls lObjMstPensionerDtlsVO = new MstPensionerDtls();
		List<TrnProvisionalPensionDtls> lLstTrnProvisionalPensionDtlsVO = new ArrayList<TrnProvisionalPensionDtls>();
		TrnProvisionalVoucherDtls lObjTrnProvisionalVoucherDtlsVO = new TrnProvisionalVoucherDtls();
		TrnProvisionalPensionDtls lObjTrnProvisionalPensionDtlsVO = new TrnProvisionalPensionDtls();
		MstPensionerFamilyDtls lObjMstPensionerFamilyDtlsVO = new MstPensionerFamilyDtls();
		MstPensionerNomineeDtls lObjMstPensionerNomineeDtlsVO = new MstPensionerNomineeDtls();
		TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
		TrnCvpRestorationDtls lObjTrnCvpRestorationDtlsVO = new TrnCvpRestorationDtls();
		HstReEmploymentDtls lObjHstReEmploymentDtlsVO = new HstReEmploymentDtls();
		HstCommutationDtls lObjHstCommutationDtlsVO = new HstCommutationDtls();
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtlsVO = new HstPnsnPmntDcrgDtls();
		TrnPensionCaseMvmnt lObjTrnPensionCaseMvmntVO = new TrnPensionCaseMvmnt();
		TrnPensionCutDtls lObjTrnPensionCutDtlsVO = new TrnPensionCutDtls();
		TrnPensionArrearDtls lObjTrnPensionArrearDtlsVO = new TrnPensionArrearDtls();
		List<TrnPensionCutDtls> lLstTrnPensionCutDtlsVO = new ArrayList<TrnPensionCutDtls>();
		// List<TrnProvisionalVoucherDtls> lLstTrnProvisionalVoucherDtlsVO = new
		// ArrayList<TrnProvisionalVoucherDtls>();
		List<MstPensionerFamilyDtls> lLstMstPensionerFamilyDtlsVO = new ArrayList<MstPensionerFamilyDtls>();
		List<MstPensionerNomineeDtls> lLstMstPensionerNomineeDtlsVO = new ArrayList<MstPensionerNomineeDtls>();
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsVO = new ArrayList<TrnPensionRecoveryDtls>();
		List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtlsVO = new ArrayList<TrnCvpRestorationDtls>();
		List<HstReEmploymentDtls> lLstHstReEmploymentDtlsVO = new ArrayList<HstReEmploymentDtls>();
		// List<HstCommutationDtls> lLstHstCommutationDtlsVO = new
		// ArrayList<HstCommutationDtls>();
		List<HstPnsnPmntDcrgDtls> lLstHstPnsnPmntDcrgDtlsVO = new ArrayList<HstPnsnPmntDcrgDtls>();
		List<TrnPensionCaseMvmnt> lLstTrnPensionCaseMvmntVO = new ArrayList<TrnPensionCaseMvmnt>();
		List<TrnPensionArrearDtls> lLstTrnPensionArrearDtlsVO = new ArrayList<TrnPensionArrearDtls>();
		Map<String,Long> lMapArrearDtlsId = new HashMap<String,Long>();
		try {
			String lStrMode = (String) inputMap.get("Mode");
			lValidPpoFlag = (Boolean) inputMap.get("ValidPpoFlag");
			lStrBldXML = (StringBuilder) inputMap.get("lStrBldXML");
			String lStrStatus = (String) inputMap.get("Status");
			String lStrPpoNo = (String) inputMap.get("PpoNo");
			String lStrPensionerCode = (String) inputMap.get("PensionerCode");
			String lIsCallFromApprove = StringUtility.getParameter("callFromApprove", request);
			lObjMstPensionerHdrVO = (MstPensionerHdr) inputMap.get("MstPensionerHdrVO");
			lObjTrnPensionRqstHdrVO = (TrnPensionRqstHdr) inputMap.get("TrnPensionRqstHdrVO");
			lObjMstPensionerDtlsVO = (MstPensionerDtls) inputMap.get("MstPensionerDtlsVO");
			lLstTrnProvisionalPensionDtlsVO = (List<TrnProvisionalPensionDtls>) inputMap.get("lLstTrnProvisionalPensionDtlsVO");
			// lLstTrnProvisionalVoucherDtlsVO =
			// (List<TrnProvisionalVoucherDtls>)
			// inputMap.get("lLstTrnProvisionalVoucherDtlsVO");
			lLstMstPensionerFamilyDtlsVO = (List<MstPensionerFamilyDtls>) inputMap.get("lLstMstPensionerFamilyDtlsVO");
			lLstMstPensionerNomineeDtlsVO = (List<MstPensionerNomineeDtls>) inputMap.get("lLstMstPensionerNomineeDtlsVO");
			lLstTrnPensionRecoveryDtlsVO = (List<TrnPensionRecoveryDtls>) inputMap.get("lLstTrnPensionRecoveryDtlsVO");
			lLstTrnCvpRestorationDtlsVO = (List<TrnCvpRestorationDtls>) inputMap.get("lLstTrnCvpRestorationDtlsVO");
			lLstHstReEmploymentDtlsVO = (List<HstReEmploymentDtls>) inputMap.get("lLstHstReEmploymentDtlsVO");
			// lLstHstCommutationDtlsVO = (List<HstCommutationDtls>)
			// inputMap.get("lLstHstCommutationDtlsVO");
			// lLstHstPnsnPmntDcrgDtlsVO =
			// (List<HstPnsnPmntDcrgDtls>)inputMap.get("lLstHstPnsnPmntDcrgDtlsVO");
			lLstTrnPensionCaseMvmntVO = (List<TrnPensionCaseMvmnt>) inputMap.get("lLstTrnPensionCaseMvmntVO");
			lLstTrnPensionCutDtlsVO = (List<TrnPensionCutDtls>) inputMap.get("lLstTrnPensionCutDtlsVO");
			lLstTrnPensionArrearDtlsVO = (List<TrnPensionArrearDtls>) inputMap.get("lLstTrnPensionArrearDtlsVO");
			lMapArrearDtlsId = (Map<String, Long>) inputMap.get("MapArrearDtlsId");
			List<String> lLstRecoveryFrom = new ArrayList<String>();
			lLstRecoveryFrom.add(gObjRsrcBndle.getString("PPMT.MONTHLY"));
			lLstRecoveryFrom.add(gObjRsrcBndle.getString("PPMT.DCRG"));

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCorrectionDtls.class, serv.getSessionFactory());

			lStrCorrectionFlag = (String) inputMap.get("CorrectionFlag");
			if (("Update").equalsIgnoreCase(lStrMode) && lValidPpoFlag == true) {
				resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

				resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

				attachMap = (Map) resObj.getResultValue();
				BigDecimal lBgDcmlAttachId = BigDecimal.ZERO;
				if (attachMap.get("AttachmentId_Photo") != null) {
					lBgDcmlAttachId = new BigDecimal(String.valueOf(attachMap.get("AttachmentId_Photo")));
					lObjMstPensionerHdrVO.setPensionerPhotoId(lBgDcmlAttachId);
				}

				if (attachMap.get("AttachmentId_Sign") != null) {
					lBgDcmlAttachId = new BigDecimal(String.valueOf(attachMap.get("AttachmentId_Sign")));
					lObjMstPensionerHdrVO.setPensionerSignId(lBgDcmlAttachId);
				}
				Long lLngAttachId = null;
				if (attachMap != null) {
					if (attachMap.get("AttachmentId_scan") != null) {
						lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_scan")));
						CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
						attachmentMst.setAttachmentId(lLngAttachId);
						lObjMstPensionerHdrVO.setPpoScanId(lLngAttachId);
					}

				}

				lObjPhysicalCaseInwardDAO.update(lObjMstPensionerHdrVO);

				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());

				lObjPhysicalCaseInwardDAO.update(lObjTrnPensionRqstHdrVO);

				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
				lObjPhysicalCaseInwardDAO.update(lObjMstPensionerDtlsVO);

				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnProvisionalPensionDtls.class, serv.getSessionFactory());

				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCorrectionDtls.class, serv.getSessionFactory());
				List<TrnPensionCorrectionDtls> lLstTrnPensionCorrectionDtlsVO = new ArrayList<TrnPensionCorrectionDtls>();
				lLstTrnPensionCorrectionDtlsVO = (List<TrnPensionCorrectionDtls>) inputMap.get("CorrectionDtlsList");
				if (lLstTrnPensionCorrectionDtlsVO != null && !lLstTrnPensionCorrectionDtlsVO.isEmpty()) {
					Long lLngCorrectionDtlsID = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_pension_correction_dtls", inputMap, lLstTrnPensionCorrectionDtlsVO.size());
					TrnPensionCorrectionDtls lObjCorrectionVo = null;

					for (int lIntCnt = 0; lIntCnt < lLstTrnPensionCorrectionDtlsVO.size(); lIntCnt++) {
						lObjCorrectionVo = lLstTrnPensionCorrectionDtlsVO.get(lIntCnt);
						lObjCorrectionVo.setPensionerCode(Long.valueOf(lStrPensionerCode));
						lObjCorrectionVo.setCreatedDate(gCurrDate);
						lObjCorrectionVo.setCreatedPostId(gLngPostId);
						lObjCorrectionVo.setCreatedUserId(gLngUserId);
						lObjCorrectionVo.setCorrectionId(IFMSCommonServiceImpl.getFormattedPrimaryKey(++lLngCorrectionDtlsID, inputMap));
						lObjPhysicalCaseInwardDAO.create(lObjCorrectionVo);

					}
				}

				// inputMap.put("lObjMstPensionerHdrVO", lObjMstPensionerHdrVO);
				

			} else if ("Add".equalsIgnoreCase(lStrMode) && lValidPpoFlag == true) {
				
				lLngPensionerId = IFMSCommonServiceImpl.getNextSeqNum("mst_pensioner_hdr", inputMap);
				lObjMstPensionerHdrVO.setPensionerId(lLngPensionerId);
				lObjMstPensionerHdrVO.setPensionerCode(lLngPensionerId.toString());

				resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

				resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

				attachMap = (Map) resObj.getResultValue();
				BigDecimal lBgDcmlAttachId = BigDecimal.ZERO;
				if (attachMap.get("AttachmentId_Photo") != null) {
					lBgDcmlAttachId = new BigDecimal(String.valueOf(attachMap.get("AttachmentId_Photo")));
					lObjMstPensionerHdrVO.setPensionerPhotoId(lBgDcmlAttachId);
				}

				if (attachMap.get("AttachmentId_Sign") != null) {
					lBgDcmlAttachId = new BigDecimal(String.valueOf(attachMap.get("AttachmentId_Sign")));
					lObjMstPensionerHdrVO.setPensionerSignId(lBgDcmlAttachId);
				}
				Long lLngAttachId = null;
				if (attachMap != null) {
					if (attachMap.get("AttachmentId_scan") != null) {
						lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_scan")));
						CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
						attachmentMst.setAttachmentId(lLngAttachId);
						lObjMstPensionerHdrVO.setPpoScanId(lLngAttachId);
					}

				}

				lObjPhysicalCaseInwardDAO.create(lObjMstPensionerHdrVO);

				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
				lLngTrnPensionerHdrId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_rqst_hdr", inputMap);

				lObjTrnPensionRqstHdrVO.setPensionRequestId(lLngTrnPensionerHdrId);
				lObjTrnPensionRqstHdrVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
				lObjPhysicalCaseInwardDAO.create(lObjTrnPensionRqstHdrVO);

				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
				lLngMstPensionerDtlsId = IFMSCommonServiceImpl.getNextSeqNum("mst_pensioner_dtls", inputMap);

				lObjMstPensionerDtlsVO.setPensionerDtlsId(lLngMstPensionerDtlsId);
				lObjMstPensionerDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
				lObjPhysicalCaseInwardDAO.create(lObjMstPensionerDtlsVO);

				inputMap.put("Status", lObjTrnPensionRqstHdrVO.getCaseStatus());
				inputMap.put("PensionerCode", lObjMstPensionerHdrVO.getPensionerCode());
				inputMap.put("PpoNo", lObjTrnPensionRqstHdrVO.getPpoNo());
				lStrBldXML = getResponseXMLDoc(inputMap, "Add",lMapArrearDtlsId);
			}
			if(lValidPpoFlag)
			{
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnProvisionalPensionDtls.class, serv.getSessionFactory());
				List<TrnProvisionalPensionDtls> lLstPrvTrnProvisionalPensionDtls = lObjPhysicalCaseInwardDAO.getTrnProvisionalPensionDtlsVO(lStrPensionerCode);
				if (lLstPrvTrnProvisionalPensionDtls != null && !lLstPrvTrnProvisionalPensionDtls.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstPrvTrnProvisionalPensionDtls.size(); lIntCnt++) {
						lObjTrnProvisionalPensionDtlsVO = lLstPrvTrnProvisionalPensionDtls.get(lIntCnt);
						lObjPhysicalCaseInwardDAO.delete(lObjTrnProvisionalPensionDtlsVO);
					}
				}
	
				if (lLstTrnProvisionalPensionDtlsVO != null && !lLstTrnProvisionalPensionDtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnProvisionalPensionDtlsVO.size(); lIntCnt++) {
						lObjTrnProvisionalPensionDtlsVO = lLstTrnProvisionalPensionDtlsVO.get(lIntCnt);
						lLngProvisionalPensionDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_prvosional_pension_dtls", inputMap);
						lObjTrnProvisionalPensionDtlsVO.setProvisionalPensionDtlsId(lLngProvisionalPensionDtlsId);
						lObjTrnProvisionalPensionDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
						lObjTrnProvisionalPensionDtlsVO.setPensionRequestId(new BigDecimal(lObjTrnPensionRqstHdrVO.getPensionRequestId()));
						lObjPhysicalCaseInwardDAO.create(lObjTrnProvisionalPensionDtlsVO);
					}
				}
				// lObjPhysicalCaseInwardDAO = new
				// PhysicalCaseInwardDAOImpl(TrnProvisionalVoucherDtls.class,
				// serv.getSessionFactory());
				//
				// List<TrnProvisionalVoucherDtls> lLstProvisionalVoucherDtls =
				// lObjPhysicalCaseInwardDAO.getTrnProvisionalVoucherDtlsVO(lObjMstPensionerHdrVO.getPensionerCode());
				// if (lLstProvisionalVoucherDtls != null &&
				// !lLstProvisionalVoucherDtls.isEmpty()) {
				// for (int lIntCnt = 0; lIntCnt <
				// lLstProvisionalVoucherDtls.size(); lIntCnt++) {
				// lObjTrnProvisionalVoucherDtlsVO =
				// lLstProvisionalVoucherDtls.get(lIntCnt);
				// lObjPhysicalCaseInwardDAO.delete(lObjTrnProvisionalVoucherDtlsVO);
				// }
				// }
				// if (lLstTrnProvisionalVoucherDtlsVO != null) {
				// for (int lIntCount = 0; lIntCount <
				// lLstTrnProvisionalVoucherDtlsVO.size(); lIntCount++) {
				// lObjTrnProvisionalVoucherDtlsVO =
				// lLstTrnProvisionalVoucherDtlsVO.get(lIntCount);
				// lLngProvVoucherDtlsId =
				// IFMSCommonServiceImpl.getNextSeqNum("trn_provisional_voucher_dtls",
				// inputMap);
				// lObjTrnProvisionalVoucherDtlsVO.setProvVoucherDtlsId(lLngProvVoucherDtlsId);
				// lObjTrnProvisionalVoucherDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
				// lObjPhysicalCaseInwardDAO.create(lObjTrnProvisionalVoucherDtlsVO);
				// }
				// }
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerFamilyDtls.class, serv.getSessionFactory());
				List<MstPensionerFamilyDtls> lLstFamilyDtlsVO = lObjPhysicalCaseInwardDAO.getMstPensionerFamilyDtlsVO(lObjMstPensionerHdrVO.getPensionerCode());
	
				if (lLstFamilyDtlsVO != null && !lLstFamilyDtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstFamilyDtlsVO.size(); lIntCnt++) {
						lObjMstPensionerFamilyDtlsVO = lLstFamilyDtlsVO.get(lIntCnt);
						lObjPhysicalCaseInwardDAO.delete(lObjMstPensionerFamilyDtlsVO);
					}
				}
	
				if (lLstMstPensionerFamilyDtlsVO != null) {
					for (int lIntCount = 0; lIntCount < lLstMstPensionerFamilyDtlsVO.size(); lIntCount++) {
						lObjMstPensionerFamilyDtlsVO = lLstMstPensionerFamilyDtlsVO.get(lIntCount);
						lLngFamilyMemberId = IFMSCommonServiceImpl.getNextSeqNum("mst_pensioner_family_dtls", inputMap);
						lObjMstPensionerFamilyDtlsVO.setFamilyMemberId(lLngFamilyMemberId);
						lObjMstPensionerFamilyDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
						lObjPhysicalCaseInwardDAO.create(lObjMstPensionerFamilyDtlsVO);
					}
				}
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerNomineeDtls.class, serv.getSessionFactory());
				List<MstPensionerNomineeDtls> lLstNomineeDtlsVO = lObjPhysicalCaseInwardDAO.getMstPensionerNomDtlsVO(lObjMstPensionerHdrVO.getPensionerCode());
	
				if (lLstNomineeDtlsVO != null && !lLstNomineeDtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstNomineeDtlsVO.size(); lIntCnt++) {
						lObjMstPensionerNomineeDtlsVO = lLstNomineeDtlsVO.get(lIntCnt);
						lObjPhysicalCaseInwardDAO.delete(lObjMstPensionerNomineeDtlsVO);
					}
				}
				if (lLstMstPensionerNomineeDtlsVO != null) {
					for (int lIntCount = 0; lIntCount < lLstMstPensionerNomineeDtlsVO.size(); lIntCount++) {
						lObjMstPensionerNomineeDtlsVO = lLstMstPensionerNomineeDtlsVO.get(lIntCount);
						lLngNomineeDtlId = IFMSCommonServiceImpl.getNextSeqNum("mst_pensioner_nominee_dtls", inputMap);
						lObjMstPensionerNomineeDtlsVO.setNomineeId(lLngNomineeDtlId);
						lObjMstPensionerNomineeDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
						lObjPhysicalCaseInwardDAO.create(lObjMstPensionerNomineeDtlsVO);
					}
				}
	
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());
				List<TrnPensionRecoveryDtls> lLstPensionRecoveryDtlsVO = lObjPhysicalCaseInwardDAO.getTrnPensionRecoveryDtlsForDelete(lObjMstPensionerHdrVO.getPensionerCode(), lLstRecoveryFrom);
				if (lLstPensionRecoveryDtlsVO != null && !lLstPensionRecoveryDtlsVO.isEmpty()) {
						for (int lIntCount = 0; lIntCount < lLstPensionRecoveryDtlsVO.size(); lIntCount++) {
							lObjTrnPensionRecoveryDtlsVO = lLstPensionRecoveryDtlsVO.get(lIntCount);
							lObjPhysicalCaseInwardDAO.delete(lObjTrnPensionRecoveryDtlsVO);
						}
					
				}
	
				if (lLstTrnPensionRecoveryDtlsVO != null) {
					for (int lIntCount = 0; lIntCount < lLstTrnPensionRecoveryDtlsVO.size(); lIntCount++) {
						lObjTrnPensionRecoveryDtlsVO = lLstTrnPensionRecoveryDtlsVO.get(lIntCount);
						lLngPensionRecoveryDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_recovery_dtls", inputMap);
						lObjTrnPensionRecoveryDtlsVO.setTrnPensionRecoveryDtlsId(lLngPensionRecoveryDtlsId);
						lObjTrnPensionRecoveryDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
						lObjPhysicalCaseInwardDAO.create(lObjTrnPensionRecoveryDtlsVO);
					}
				}
	
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnCvpRestorationDtls.class, serv.getSessionFactory());
				List<TrnCvpRestorationDtls> lLstCvpRestnDtlsVO = lObjPhysicalCaseInwardDAO.getTrnCvpRestorationDtlsVO(lObjMstPensionerHdrVO.getPensionerCode());
	
				if (lLstCvpRestnDtlsVO != null && !lLstCvpRestnDtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstCvpRestnDtlsVO.size(); lIntCnt++) {
						lObjTrnCvpRestorationDtlsVO = lLstCvpRestnDtlsVO.get(lIntCnt);
						lObjPhysicalCaseInwardDAO.delete(lObjTrnCvpRestorationDtlsVO);
					}
				}
				if (lLstTrnCvpRestorationDtlsVO != null) {
					for (int lIntCount = 0; lIntCount < lLstTrnCvpRestorationDtlsVO.size(); lIntCount++) {
						lObjTrnCvpRestorationDtlsVO = lLstTrnCvpRestorationDtlsVO.get(lIntCount);
						lLngCvpRestnDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_cvp_restoration_dtls", inputMap);
						lObjTrnCvpRestorationDtlsVO.setCvpRestorationDtlsId(lLngCvpRestnDtlsId);
						lObjTrnCvpRestorationDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
						lObjPhysicalCaseInwardDAO.create(lObjTrnCvpRestorationDtlsVO);
					}
				}
	
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnCvpRestorationDtls.class, serv.getSessionFactory());
				List<HstReEmploymentDtls> lLstReEmploymentDtlsVO = lObjPhysicalCaseInwardDAO.getHstReEmploymentDtlsVO(lObjMstPensionerHdrVO.getPensionerCode());
	
				if (lLstReEmploymentDtlsVO != null && !lLstReEmploymentDtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstReEmploymentDtlsVO.size(); lIntCnt++) {
						lObjHstReEmploymentDtlsVO = lLstReEmploymentDtlsVO.get(lIntCnt);
						lObjPhysicalCaseInwardDAO.delete(lObjHstReEmploymentDtlsVO);
					}
				}
				if (lLstHstReEmploymentDtlsVO != null) {
					for (int lIntCount = 0; lIntCount < lLstHstReEmploymentDtlsVO.size(); lIntCount++) {
						lObjHstReEmploymentDtlsVO = lLstHstReEmploymentDtlsVO.get(lIntCount);
						lLngReEmploymentDtlsId = IFMSCommonServiceImpl.getNextSeqNum("hst_re_employment_dtls", inputMap);
						lObjHstReEmploymentDtlsVO.setReEmploymentId(lLngReEmploymentDtlsId);
						lObjHstReEmploymentDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
						lObjPhysicalCaseInwardDAO.create(lObjHstReEmploymentDtlsVO);
					}
				}
	
				// lObjPhysicalCaseInwardDAO = new
				// PhysicalCaseInwardDAOImpl(TrnCvpRestorationDtls.class,
				// serv.getSessionFactory());
				// List<HstCommutationDtls> lLstCommutationDtlsVO =
				// lObjPhysicalCaseInwardDAO.getHstCommutationDtlsVO(lObjMstPensionerHdrVO.getPensionerCode());
	
				// if (lLstCommutationDtlsVO != null &&
				// !lLstCommutationDtlsVO.isEmpty()) {
				// for (int lIntCnt = 0; lIntCnt < lLstCommutationDtlsVO.size();
				// lIntCnt++) {
				// lObjHstCommutationDtlsVO = lLstCommutationDtlsVO.get(lIntCnt);
				// lObjPhysicalCaseInwardDAO.delete(lObjHstCommutationDtlsVO);
				// }
				// }
				// if (lLstHstCommutationDtlsVO != null) {
				// for (int lIntCount = 0; lIntCount <
				// lLstHstCommutationDtlsVO.size(); lIntCount++) {
				// lObjHstCommutationDtlsVO =
				// lLstHstCommutationDtlsVO.get(lIntCount);
				// lLngCommutationDtlsId =
				// IFMSCommonServiceImpl.getNextSeqNum("hst_commutation_dtls",
				// inputMap);
				// lObjHstCommutationDtlsVO.setCvpDtlsId(lLngCommutationDtlsId);
				// lObjHstCommutationDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
				// lObjPhysicalCaseInwardDAO.create(lObjHstCommutationDtlsVO);
				// }
				// }
	
				/*
				 * lObjPhysicalCaseInwardDAO = new
				 * PhysicalCaseInwardDAOImpl(HstPnsnPmntDcrgDtls.class,
				 * serv.getSessionFactory()); List<HstPnsnPmntDcrgDtls>
				 * lLstHstDcrgDtls =
				 * lObjPhysicalCaseInwardDAO.getDcrgHistory(lObjMstPensionerHdrVO
				 * .getPensionerCode()); if (lLstHstDcrgDtls != null &&
				 * !lLstHstDcrgDtls.isEmpty()) { for(int
				 * lIntCnt=0;lIntCnt<lLstHstDcrgDtls.size();lIntCnt++) {
				 * lObjHstPnsnPmntDcrgDtlsVO = lLstHstDcrgDtls.get(lIntCnt);
				 * lObjPhysicalCaseInwardDAO.delete(lObjHstPnsnPmntDcrgDtlsVO); } }
				 * if (lLstHstPnsnPmntDcrgDtlsVO != null &&
				 * !lLstHstPnsnPmntDcrgDtlsVO.isEmpty()) { for(int
				 * lIntCnt=0;lIntCnt<lLstHstPnsnPmntDcrgDtlsVO.size();lIntCnt++) {
				 * lObjHstPnsnPmntDcrgDtlsVO =
				 * lLstHstPnsnPmntDcrgDtlsVO.get(lIntCnt); Long dcrgDtlsId =
				 * IFMSCommonServiceImpl.getNextSeqNum("hst_pnsnpmnt_dcrg_dtls",
				 * inputMap); lObjHstPnsnPmntDcrgDtlsVO.setDcrgDtlsId(dcrgDtlsId);
				 * lObjHstPnsnPmntDcrgDtlsVO
				 * .setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
				 * lObjPhysicalCaseInwardDAO.create(lObjHstPnsnPmntDcrgDtlsVO); } }
				 */
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCaseMvmnt.class, serv.getSessionFactory());
	
				if (lLstTrnPensionCaseMvmntVO != null && !lLstTrnPensionCaseMvmntVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPensionCaseMvmntVO.size(); lIntCnt++) {
						lObjTrnPensionCaseMvmntVO = lLstTrnPensionCaseMvmntVO.get(lIntCnt);
						lLngTrnPnsnCaseMvmntId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_case_mvmnt", inputMap);
						lObjTrnPensionCaseMvmntVO.setTrnPenionCaseMvmntId((lLngTrnPnsnCaseMvmntId));
						lObjTrnPensionCaseMvmntVO.setPpoNo(lObjTrnPensionRqstHdrVO.getPpoNo());
						lObjTrnPensionCaseMvmntVO.setPensionerCode(lObjTrnPensionRqstHdrVO.getPensionerCode());
						lObjPhysicalCaseInwardDAO.create(lObjTrnPensionCaseMvmntVO);
					}
				}
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCutDtls.class, serv.getSessionFactory());
				List<TrnPensionCutDtls> lLstPenCutDtls = lObjPhysicalCaseInwardDAO.getPensionCutDtls(lObjMstPensionerHdrVO.getPensionerCode());
				if (lLstPenCutDtls != null && !lLstPenCutDtls.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstPenCutDtls.size(); lIntCnt++) {
						lObjTrnPensionCutDtlsVO = lLstPenCutDtls.get(lIntCnt);
						lObjPhysicalCaseInwardDAO.delete(lObjTrnPensionCutDtlsVO);
					}
				}
				if (lLstTrnPensionCutDtlsVO != null && !lLstTrnPensionCutDtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPensionCutDtlsVO.size(); lIntCnt++) {
						lObjTrnPensionCutDtlsVO = lLstTrnPensionCutDtlsVO.get(lIntCnt);
						Long pensionCutDtlsId = IFMSCommonServiceImpl.getNextSeqNum("TRN_PENSION_CUT_DTLS", inputMap);
						lObjTrnPensionCutDtlsVO.setPensionCutDtlsId(pensionCutDtlsId);
						lObjTrnPensionCutDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
						lObjPhysicalCaseInwardDAO.create(lObjTrnPensionCutDtlsVO);
					}
				}
				
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionArrearDtls.class, serv.getSessionFactory());
				if (lLstTrnPensionArrearDtlsVO != null && !lLstTrnPensionArrearDtlsVO.isEmpty()) {
					for (int lIntCnt = 0; lIntCnt < lLstTrnPensionArrearDtlsVO.size(); lIntCnt++) {
						lObjTrnPensionArrearDtlsVO = lLstTrnPensionArrearDtlsVO.get(lIntCnt);
						Long pensionArrearDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_arrear_dtls", inputMap);
						lObjTrnPensionArrearDtlsVO.setPensionArrearDtlsId(pensionArrearDtlsId);
						lObjTrnPensionArrearDtlsVO.setPensionerCode(lObjMstPensionerHdrVO.getPensionerCode());
						lObjTrnPensionArrearDtlsVO.setPensionRequestId(lObjTrnPensionRqstHdrVO.getPensionRequestId());
						lObjPhysicalCaseInwardDAO.create(lObjTrnPensionArrearDtlsVO);
						if(gObjRsrcBndle.getString("ARREARTYPE.DA").equals(lObjTrnPensionArrearDtlsVO.getArrearFieldType()))
						{
							lMapArrearDtlsId.put("DAArrearDtlsId", pensionArrearDtlsId);
						}
						else if(gObjRsrcBndle.getString("ARREARTYPE.6PC").equals(lObjTrnPensionArrearDtlsVO.getArrearFieldType()))
						{
							lMapArrearDtlsId.put("6PCArrearDtlsId", pensionArrearDtlsId);
						}
						else if(gObjRsrcBndle.getString("ARREARTYPE.PENSION").equals(lObjTrnPensionArrearDtlsVO.getArrearFieldType()))
						{
							lMapArrearDtlsId.put("PensionArrearDtlsId", pensionArrearDtlsId);
						}
						else if(gObjRsrcBndle.getString("ARREARTYPE.COMPNSN").equals(lObjTrnPensionArrearDtlsVO.getArrearFieldType()))
						{
							lMapArrearDtlsId.put("ComtPnsnArrearDtlsId", pensionArrearDtlsId);
						}
						else if(gObjRsrcBndle.getString("ARREARTYPE.OTHERDIFF").equals(lObjTrnPensionArrearDtlsVO.getArrearFieldType()))
						{
							lMapArrearDtlsId.put("OtherArrearDtlsId", pensionArrearDtlsId);
						}
					}
				}
				if (("Update").equalsIgnoreCase(lStrMode)) {
					// -----If update is done on approve action then no need to
					// display any message.
					if (lIsCallFromApprove.equals("N")) {
						inputMap.put("Status", lObjTrnPensionRqstHdrVO.getCaseStatus());
						inputMap.put("PpoNo", lObjTrnPensionRqstHdrVO.getPpoNo());
						inputMap.put("PensionerCode", lObjMstPensionerHdrVO.getPensionerCode());
						lStrBldXML = getResponseXMLDoc(inputMap, "UPDATE", lMapArrearDtlsId);
	
					} else {
						inputMap.put("Status", lObjTrnPensionRqstHdrVO.getCaseStatus());
						inputMap.put("PpoNo", lObjTrnPensionRqstHdrVO.getPpoNo());
						inputMap.put("PensionerCode", lObjMstPensionerHdrVO.getPensionerCode());
						lStrBldXML = getResponseXMLDoc(inputMap, "UpdateOnApprove",lMapArrearDtlsId);
					}
				}
			}
			
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Map inputMap, String strMode,Map<String,Long> lMapArrearDtlsId) {

		String lStrPensionerCode = (String) inputMap.get("PensionerCode");
		String lStrPpoNo = (String) inputMap.get("PpoNo");
		String lStrCaseStatus = (String) inputMap.get("Status");
		String mode = strMode;

		StringBuilder lStrHidPKs = new StringBuilder();
		if (mode.equalsIgnoreCase("Add")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<PPONO>" + lStrPpoNo);
			lStrHidPKs.append("</PPONO>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("insert");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("<PNSNRCODE>" + lStrPensionerCode);
			lStrHidPKs.append("</PNSNRCODE>");
			lStrHidPKs.append("</XMLDOC>");
		} else if (mode.equals("UPDATE")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<PPONO>" + lStrPpoNo);
			lStrHidPKs.append("</PPONO>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("update");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("<PNSNRCODE>" + lStrPensionerCode);
			lStrHidPKs.append("</PNSNRCODE>");
			lStrHidPKs.append("<STATUS>" + lStrCaseStatus);
			lStrHidPKs.append("</STATUS>");
			lStrHidPKs.append("<ARREARDTLS>");
			lStrHidPKs.append("<DAARREARID>" + (lMapArrearDtlsId.get("DAArrearDtlsId") != null ?lMapArrearDtlsId.get("DAArrearDtlsId"):""));
			lStrHidPKs.append("</DAARREARID>");
			lStrHidPKs.append("<SIXTHPCARREARID>" + (lMapArrearDtlsId.get("6PCArrearDtlsId") != null ?lMapArrearDtlsId.get("6PCArrearDtlsId"):""));
			lStrHidPKs.append("</SIXTHPCARREARID>");
			lStrHidPKs.append("<PNSNARREARID>" + (lMapArrearDtlsId.get("PensionArrearDtlsId") != null ?lMapArrearDtlsId.get("PensionArrearDtlsId"):""));
			lStrHidPKs.append("</PNSNARREARID>");
			lStrHidPKs.append("<COMTPNSNARREARID>" + (lMapArrearDtlsId.get("ComtPnsnArrearDtlsId") != null ?lMapArrearDtlsId.get("ComtPnsnArrearDtlsId"):""));
			lStrHidPKs.append("</COMTPNSNARREARID>");
			lStrHidPKs.append("<OTHERARREARID>" + (lMapArrearDtlsId.get("OtherArrearDtlsId") != null ?lMapArrearDtlsId.get("OtherArrearDtlsId"):""));
			lStrHidPKs.append("</OTHERARREARID>");
			lStrHidPKs.append("</ARREARDTLS>");
			lStrHidPKs.append("</XMLDOC>");
		} else {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<PPONO>" + lStrPpoNo);
			lStrHidPKs.append("</PPONO>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("updateonapprove");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("<PNSNRCODE>" + lStrPensionerCode);
			lStrHidPKs.append("</PNSNRCODE>");
			lStrHidPKs.append("</XMLDOC>");
		}
		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#getHeadCodeDesc
	 * (java.util.Map)
	 */
	public ResultObject getHeadCodeDesc(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrHeadCode = null;
		String lStrStateCode = null;
		String lStrHeadCodeDesc = null;
		StringBuilder lStrBldXML = null;
		BigDecimal lBgDcmlDPRate = BigDecimal.ZERO;
		setSessionInfo(inputMap);
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
		try {
			lStrHeadCode = StringUtility.getParameter("headCode", request).trim();
			lStrStateCode = StringUtility.getParameter("stateCode", request).trim();
			if (!"".equals(lStrHeadCode)) {
				lStrHeadCodeDesc = lObjPhysicalCaseInwardDAO.getHeadCodeDesc(lStrHeadCode, gLngLangId);
			}

			if (!"".equals(lStrStateCode) && !"-1".equals(lStrStateCode) && lStrStateCode.length() > 0) {
				lBgDcmlDPRate = lObjPhysicalCaseInwardDAO.getRltPensionHeadcodeRate(lStrStateCode, "DP");
			}
			lStrBldXML = getResponseHeadCodeDescXMLDoc(lStrHeadCode, lStrHeadCodeDesc, lBgDcmlDPRate);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	private StringBuilder getResponseHeadCodeDescXMLDoc(String lStrHeadCode, String lStrHeadCodeDesc, BigDecimal lBgDcmlDPRate) {

		StringBuilder lStrHidPKs = new StringBuilder();

		lStrHidPKs.append("<XMLDOC>");
		lStrHidPKs.append("<HEADCODE>" + lStrHeadCode);
		lStrHidPKs.append("</HEADCODE>");
		lStrHidPKs.append("<HEADCODEDESC>" + lStrHeadCodeDesc);
		lStrHidPKs.append("</HEADCODEDESC>");
		lStrHidPKs.append("<DPRATE>" + lBgDcmlDPRate);
		lStrHidPKs.append("</DPRATE>");
		lStrHidPKs.append("</XMLDOC>");

		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#getDARateFromROPType
	 * (java.util.Map)
	 */
	public ResultObject getDARateFromROPType(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrROPType = null;
		BigDecimal lBgDcmlDARate = null;
		String lStrDPMerged = null;
		String lStrHeadCode = null;
		StringBuilder lStrBldXML = null;

		setSessionInfo(inputMap);
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
		try {
			lStrHeadCode = StringUtility.getParameter("headCode", request).trim();
			if (!StringUtility.getParameter("ROPType", request).equals("-1")) {
				lStrROPType = StringUtility.getParameter("ROPType", request).trim();
			}
			if (!"".equals(StringUtility.getParameter("DpMerged", request))) {
				lStrDPMerged = StringUtility.getParameter("DpMerged", request).trim();
			}
			if (!("-1").equals(lStrHeadCode)) {
				if ((gObjRsrcBndle.getString("PPMT.ROPTYPE1996")).equalsIgnoreCase(lStrROPType) && "Y".equalsIgnoreCase(lStrDPMerged)) {
					lBgDcmlDARate = lObjPhysicalCaseInwardDAO.getRltPensionHeadcodeRate(lStrHeadCode, gObjRsrcBndle.getString("PPMT.DA1996DPMERGED"));
				}
				if ((gObjRsrcBndle.getString("PPMT.ROPTYPE1996")).equalsIgnoreCase(lStrROPType) && "N".equalsIgnoreCase(lStrDPMerged)) {
					lBgDcmlDARate = lObjPhysicalCaseInwardDAO.getRltPensionHeadcodeRate(lStrHeadCode, gObjRsrcBndle.getString("PPMT.DA1996"));
				}
				if ((gObjRsrcBndle.getString("PPMT.ROPTYPE2006")).equalsIgnoreCase(lStrROPType)) {
					lBgDcmlDARate = lObjPhysicalCaseInwardDAO.getRltPensionHeadcodeRate(lStrHeadCode, gObjRsrcBndle.getString("PPMT.DA2006"));
				}
			}
			lStrBldXML = getResponseDARateXMLDoc(lStrROPType, lBgDcmlDARate);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	private StringBuilder getResponseDARateXMLDoc(String lStrROPType, BigDecimal lBgDcmlDARate) {

		StringBuilder lStrHidPKs = new StringBuilder();

		lStrHidPKs.append("<XMLDOC>");
		lStrHidPKs.append("<ROPTYPE>" + lStrROPType);
		lStrHidPKs.append("</ROPTYPE>");
		lStrHidPKs.append("<DARATE>" + lBgDcmlDARate);
		lStrHidPKs.append("</DARATE>");
		lStrHidPKs.append("</XMLDOC>");

		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#
	 * getIfscCodeFromBrachCode(java.util.Map)
	 */
	public ResultObject getIfscCodeFromBranchCode(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Long lLngBranchCode = null;
		String lStrIfscCode = null;
		StringBuilder lStrBldXML = new StringBuilder();
		setSessionInfo(inputMap);
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
		try {
			if (!StringUtility.getParameter("branchCode", request).equals("-1")) {
				lLngBranchCode = Long.parseLong(StringUtility.getParameter("branchCode", request).trim());
				lStrIfscCode = lObjPhysicalCaseInwardDAO.getIfscCodeFromBranchCode(lLngBranchCode, gStrLocCode);
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<BRANCHCODE>" + lLngBranchCode);
			lStrBldXML.append("</BRANCHCODE>");
			lStrBldXML.append("<IFSCCODE>" + lStrIfscCode);
			lStrBldXML.append("</IFSCCODE>");
			lStrBldXML.append("</XMLDOC>");
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#validatePPONo
	 * (java.util.Map)
	 */
	public ResultObject validatePPONo(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
		String lStrPPONo = null;
		List lLstResult = null;
		String lStrTreasuryName = null;
		String lStrPensionerName = "";
		String lStrBankName = ""; 
		String lStrBranchName = "";
		boolean lValidPpoFlag = true;
		List<String> lLstLocCode = new ArrayList<String>();
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			lStrPPONo = StringUtility.getParameter("PpoNo", request);
			
			if (lStrPPONo == null || "".equals(lStrPPONo.trim())) {
				lStrPPONo = (String) inputMap.get("ppoNo");
			}
			lLstLocCode = lObjCommonPensionDAO.getLocationListOfAgCircle(gStrLocCode);
			if (lStrPPONo != null && !"".equals(lStrPPONo)) {
				//lStrPPONo = StringUtility.getParameter("PpoNo", request);
				lLstResult = lObjPhysicalCaseInwardDAO.isValidPPONo(lStrPPONo.trim(),lLstLocCode);
			}
			if (lLstResult != null && !lLstResult.isEmpty()) {
				Object[] obj = (Object[]) lLstResult.get(0);
				if (obj[0] != null) {
					lStrPPONo = obj[0].toString();
				}
				if (obj[1] != null) {
					lStrTreasuryName = obj[1].toString();
				}
				if (obj[2] != null) {
					lStrPensionerName = obj[2].toString();
				}
				if (obj[3] != null) {
					lStrBankName = obj[3].toString();
				}
				if (obj[4] != null) {
					lStrBranchName = obj[4].toString();
				}
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<PPONO><![CDATA[" + lStrPPONo);
			lStrBldXML.append("]]></PPONO>");
			lStrBldXML.append("<ISEXISTS>");
			if (lLstResult.isEmpty()) {
				lStrBldXML.append("N");
			} else {
				lStrBldXML.append("Y");
			}
			lStrBldXML.append("</ISEXISTS>");
			if (lStrTreasuryName != null) {
				lStrBldXML.append("<TREASURYNAME><![CDATA[" + lStrTreasuryName);
				lStrBldXML.append("]]></TREASURYNAME>");
				lStrBldXML.append("<PENSIONERNAME>" + lStrPensionerName);
				lStrBldXML.append("</PENSIONERNAME>");
				lStrBldXML.append("<BANKNAME><![CDATA[" + lStrBankName);
				lStrBldXML.append("]]></BANKNAME>");
				lStrBldXML.append("<BRANCHNAME><![CDATA[" + lStrBranchName);
				lStrBldXML.append("]]></BRANCHNAME>");
			}
			lStrBldXML.append("</XMLDOC>");

			if (!lLstResult.isEmpty()) {
				lValidPpoFlag = false;
			}
			inputMap.put("ValidPpoFlag",lValidPpoFlag);
			inputMap.put("lStrBldXML",lStrBldXML);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;

	}

	public ResultObject identifyAndApprovePensionCase(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		StringBuilder lSBResp = null;
		String lStrPnsrCode = null;
		String lStrPnsnRqstId = null;
		IdentificationSchedularDAO lObjIdentificationSchedularDAO = null;
		List lLstTrnPnsnpmntSchedularObj = null;
		TrnPnsnpmntSchedular lObjTrnPnsnpmntSchedular = null;
		MstPensionerDtlsDAO lObjMstPensionerDtlsDAO = null;
		MstPensionerDtls lObjMstPensionerDtls = null;
		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = null;
		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;
		Long lRltPensioncaseBillId = null;
		RltPensioncaseBill lObjRltPensioncaseBill = null;
		RltPensioncaseBillDAO lObjBillCasDao = null;
		Date lDtCurDate = new Date();
		Timestamp lTSCurrent = new Timestamp(lDtCurDate.getTime());

		try {
			setSessionInfo(inputMap);
			lStrPnsrCode = StringUtility.getParameter("pnsrCode", request);
			lStrPnsnRqstId = StringUtility.getParameter("pnsnRqstId", request);

			if (!"".equals(lStrPnsrCode)) {
				Long lLngPnsrCode = Long.valueOf(lStrPnsrCode);

				/*
				 * Updating scheduleStatus to identified
				 */
				lObjIdentificationSchedularDAO = new IdentificationSchedularDAOImpl(TrnPnsnpmntSchedular.class, serv.getSessionFactory());
				lLstTrnPnsnpmntSchedularObj = lObjIdentificationSchedularDAO.getSchedularObjFromPsnrCode(Long.valueOf(lStrPnsrCode), Long.valueOf(gStrLocCode));
				if (lLstTrnPnsnpmntSchedularObj != null && lLstTrnPnsnpmntSchedularObj.size() > 0) {
					lObjTrnPnsnpmntSchedular = (TrnPnsnpmntSchedular) lLstTrnPnsnpmntSchedularObj.get(0);
					lObjTrnPnsnpmntSchedular.setScheduleStatus(gObjRsrcBndle.getString("IDENT.IDENTIFIED"));
					lObjTrnPnsnpmntSchedular.setUpdatedDate(gCurrDate);
					lObjTrnPnsnpmntSchedular.setUpdatedPostId(gLngPostId);
					lObjTrnPnsnpmntSchedular.setUpdatedUserId(gLngUserId);
					lObjIdentificationSchedularDAO.update(lObjTrnPnsnpmntSchedular);
				}

				/*
				 * Updating identification flag to Y and inserting
				 * identification date and time.
				 */
				lObjMstPensionerDtlsDAO = new MstPensionerDtlsDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
				lObjMstPensionerDtls = lObjMstPensionerDtlsDAO.getMstPensionerDtls(lStrPnsrCode);
				lObjMstPensionerDtls.setIdentificationFlag("Y");
				lObjMstPensionerDtls.setCaseStatus(gObjRsrcBndle.getString("STATFLG.IDENTIFIED"));
				lObjMstPensionerDtls.setIdentificationDate(lTSCurrent);
				lObjMstPensionerDtls.setUpdatedDate(gCurrDate);
				lObjMstPensionerDtls.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjMstPensionerDtls.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjMstPensionerDtlsDAO.update(lObjMstPensionerDtls);

				/*
				 * Updating Case Status to Identified in Trn_pension_rqst_hdr
				 */
				lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
				lObjTrnPensionRqstHdr = lObjTrnPensionRqstHdrDAO.read(Long.valueOf(lStrPnsnRqstId));
				lObjTrnPensionRqstHdr.setCaseStatus(gObjRsrcBndle.getString("STATFLG.IDENTIFIED"));
				lObjTrnPensionRqstHdr.setSeenFlag("Y");
				lObjTrnPensionRqstHdr.setUpdatedDate(gCurrDate);
				lObjTrnPensionRqstHdr.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjTrnPensionRqstHdr.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjTrnPensionRqstHdrDAO.update(lObjTrnPensionRqstHdr);

				/*
				 * Creating Entries of CVP,DCRG,PENSION bills in
				 * rlt_pensioncase_bill
				 */
				lObjBillCasDao = new RltPensioncaseBillDAOImpl(RltPensioncaseBill.class, serv.getSessionFactory());
				lObjRltPensioncaseBill = new RltPensioncaseBill();
				lRltPensioncaseBillId = IFMSCommonServiceImpl.getNextSeqNum("rlt_pensioncase_bill", inputMap);
				lObjRltPensioncaseBill.setRltPensioncaseBillId(lRltPensioncaseBillId);
				lObjRltPensioncaseBill.setPpoNo(lObjTrnPensionRqstHdr.getPpoNo());
				lObjRltPensioncaseBill.setBillType("CVP");
				lObjRltPensioncaseBill.setStatus("N");
				lObjRltPensioncaseBill.setCreatedDate(gCurrDate);
				lObjRltPensioncaseBill.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjRltPensioncaseBill.setCreatedUserId(new BigDecimal(gLngUserId));
				lObjRltPensioncaseBill.setLocationCode(gStrLocCode);
				lObjBillCasDao.create(lObjRltPensioncaseBill);

				lObjRltPensioncaseBill = new RltPensioncaseBill();
				lRltPensioncaseBillId = IFMSCommonServiceImpl.getNextSeqNum("rlt_pensioncase_bill", inputMap);
				lObjRltPensioncaseBill.setRltPensioncaseBillId(lRltPensioncaseBillId);
				lObjRltPensioncaseBill.setPpoNo(lObjTrnPensionRqstHdr.getPpoNo());
				lObjRltPensioncaseBill.setBillType("DCRG");
				lObjRltPensioncaseBill.setStatus("N");
				lObjRltPensioncaseBill.setCreatedDate(gCurrDate);
				lObjRltPensioncaseBill.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjRltPensioncaseBill.setCreatedUserId(new BigDecimal(gLngUserId));
				lObjRltPensioncaseBill.setLocationCode(gStrLocCode);
				lObjBillCasDao.create(lObjRltPensioncaseBill);

				lObjRltPensioncaseBill = new RltPensioncaseBill();
				lRltPensioncaseBillId = IFMSCommonServiceImpl.getNextSeqNum("rlt_pensioncase_bill", inputMap);
				lObjRltPensioncaseBill.setRltPensioncaseBillId(lRltPensioncaseBillId);
				lObjRltPensioncaseBill.setPpoNo(lObjTrnPensionRqstHdr.getPpoNo());
				lObjRltPensioncaseBill.setBillType("PENSION");
				lObjRltPensioncaseBill.setStatus("N");
				lObjRltPensioncaseBill.setCreatedDate(gCurrDate);
				lObjRltPensioncaseBill.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjRltPensioncaseBill.setCreatedUserId(new BigDecimal(gLngUserId));
				lObjRltPensioncaseBill.setLocationCode(gStrLocCode);
				lObjBillCasDao.create(lObjRltPensioncaseBill);

			}
			lSBResp = getResponseXMLDocForApprove("Approved");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBResp.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error in validateCallDate :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private StringBuilder getResponseXMLDocForApprove(String messageCode) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		if (messageCode != null) {
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append(messageCode);
			lStrBldXML.append("</MESSAGE>");
		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject showModificationByAuditor(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			List<String> lLstFieldType = new ArrayList<String>();
			lLstFieldType.add("Designation");
			List<TrnPensionCorrectionDtls> lLstResult = new ArrayList<TrnPensionCorrectionDtls>();
			TrnPensionCorrectionDtls lObjTrnPensionCorrectionDtlsVO = new TrnPensionCorrectionDtls();
			TrnPensionCorrectionDtls lObjPensionCorrectionDtlsVO = new TrnPensionCorrectionDtls();
			List<TrnPensionCorrectionDtls> lLstTrnPensionCorrectionDtls = new ArrayList<TrnPensionCorrectionDtls>();
			String lStrPreviousValue = null;
			setSessionInfo(inputMap);
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCorrectionDtls.class, serv.getSessionFactory());
			String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			String lStrPpoNo = StringUtility.getParameter("ppoNo", request);
			if (!"".equals(lStrPensionerCode)) {
				lLstResult = lObjPhysicalCaseInwardDAO.getCorrectedValuesByPensionerCode(lStrPensionerCode, gStrLocCode);
			}
			if (lLstResult != null && !lLstResult.isEmpty()) {
				lLstResult.iterator();
				for (Integer lIntCnt = 0; lIntCnt < lLstResult.size(); lIntCnt++) {
					lObjTrnPensionCorrectionDtlsVO = lLstResult.get(lIntCnt);
					Integer lIntFlag = 0;
					if (!lLstTrnPensionCorrectionDtls.isEmpty() && lLstTrnPensionCorrectionDtls != null) {

						for (Integer lIntCount = 0; lIntCount < lLstTrnPensionCorrectionDtls.size(); lIntCount++) {
							lObjPensionCorrectionDtlsVO = new TrnPensionCorrectionDtls();
							lObjPensionCorrectionDtlsVO = lLstTrnPensionCorrectionDtls.get(lIntCount);

							if (lObjPensionCorrectionDtlsVO.getFieldType() != null && lObjTrnPensionCorrectionDtlsVO.getFieldType() != null) {
								if (lIntFlag == 0) {
									lStrPreviousValue = lObjPensionCorrectionDtlsVO.getPrvsFieldValue();
								}
								if (lObjPensionCorrectionDtlsVO.getFieldType().equals(lObjTrnPensionCorrectionDtlsVO.getFieldType())) {
									lObjPensionCorrectionDtlsVO.setPrvsFieldValue(lStrPreviousValue);
									lObjPensionCorrectionDtlsVO.setCrntFieldValue(lObjTrnPensionCorrectionDtlsVO.getCrntFieldValue());
									lIntFlag = 1;
								}

							}
						}
						if (lIntFlag == 0) {
							lLstTrnPensionCorrectionDtls.add(lObjTrnPensionCorrectionDtlsVO);
						}
					} else {
						lLstTrnPensionCorrectionDtls.add(lObjTrnPensionCorrectionDtlsVO);
					}
				}
			}
			Long lLngDesgCode = null;
			String lStrDesgName = null;
			Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle.getString("PPMT.DEPARTMENTID"));
			String lStrDeptName = "";
			String lStrLocCode = null;
			String lStrStateCode = null;
			String lStrStateName = "";
			String lStrDistrictCode = null;
			String lStrDistrictName = "";
			String lStrBankCode = null;
			String lStrBankName = "";
			String lStrBranchCode = null;
			String lStrBranchName = null;
			String lStrFieldType = "";
			String lStrChargedVotedFlag = null;
			String lStrHeadCode = null;
			String lStrHeadCodeDesc = null;
			String lStrHeadCodeSeries = null;
			String lStrPensionType = null;
			String lStrPaymentScheme = null;
			String lStrPensionTypeDesc = null;
			String lStrPensionCaseStatus = "";
			String lStrRopType = null;
			String lStrTrsryCode = "";
			String lStrTrsryName = "";
			String lStrMonth = "";
			Integer lIntMonth = 0;
			Map<BigDecimal, String> lMapHeadCodeSeries = new HashMap<BigDecimal, String>();
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lMapHeadCodeSeries = lObjCommonPensionDAO.getAllHeadCodeSeriesMap();

			for (TrnPensionCorrectionDtls lObjCorrectionDtls : lLstTrnPensionCorrectionDtls) {
				// if ("Designation".equals(lObjCorrectionDtls.getFieldType()))
				// {
				// lLngDesgCode =
				// Long.parseLong(lObjCorrectionDtls.getPrvsFieldValue());
				// lStrDesgName =
				// lObjPhysicalCaseInwardDAO.getDesignationName(lLngDesgCode,
				// gLngLangId);
				// lObjCorrectionDtls.setPrvsFieldValue(lStrDesgName);
				// lLngDesgCode =
				// Long.parseLong(lObjCorrectionDtls.getCrntFieldValue());
				// lStrDesgName =
				// lObjPhysicalCaseInwardDAO.getDesignationName(lLngDesgCode,
				// gLngLangId);
				// lObjCorrectionDtls.setCrntFieldValue(lStrDesgName);
				// }
				if ("Pension Type".equals(lObjCorrectionDtls.getFieldType())) {
					lStrPensionType = lObjCorrectionDtls.getPrvsFieldValue();
					if (!"".equals(lStrPensionType) && lStrPensionType != null) {
						lObjCorrectionDtls.setPrvsFieldValue(gObjLblRsrcBndle.getString("PNSNTYPE." + lStrPensionType));
					}

					lStrPensionType = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrPensionType) && lStrPensionType != null) {
						lObjCorrectionDtls.setCrntFieldValue(gObjLblRsrcBndle.getString("PNSNTYPE." + lStrPensionType));
					}

				}
				else if ("Pension Case Status".equals(lObjCorrectionDtls.getFieldType())) {
					lStrPensionCaseStatus = lObjCorrectionDtls.getPrvsFieldValue();
					if (!"".equals(lStrPensionCaseStatus) && lStrPensionCaseStatus != null) {
						if(gObjRsrcBndle.getString("STATUS.ENDOFPNSN").equals(lStrPensionCaseStatus))
							lObjCorrectionDtls.setPrvsFieldValue(gObjLblRsrcBndle.getString("PPMT.STATUSENDOFPNSN"));
					}

					lStrPensionCaseStatus = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrPensionCaseStatus) && lStrPensionCaseStatus != null) {
						if(gObjRsrcBndle.getString("STATUS.ENDOFPNSN").equals(lStrPensionCaseStatus))
							lObjCorrectionDtls.setCrntFieldValue(gObjLblRsrcBndle.getString("PPMT.STATUSENDOFPNSN"));
					}

				} 
				else if ("Payment Scheme".equals(lObjCorrectionDtls.getFieldType())) {
					lStrPaymentScheme = lObjCorrectionDtls.getPrvsFieldValue();
					if (!"".equals(lStrPaymentScheme) && lStrPaymentScheme != null) {
						if (gObjRsrcBndle.getString("PAYSCHEME.MONEYORDER").equals(lStrPaymentScheme)) {
							lObjCorrectionDtls.setPrvsFieldValue(gObjLblRsrcBndle.getString("PPMT.MONEYORDER"));
						} else if (gObjRsrcBndle.getString("PAYSCHEME.REGULAR").equals(lStrPaymentScheme)) {
							lObjCorrectionDtls.setPrvsFieldValue(gObjLblRsrcBndle.getString("PPMT.REGULAR"));
						} else if (gObjRsrcBndle.getString("PAYSCHEME.CASH").equals(lStrPaymentScheme)) {
							lObjCorrectionDtls.setPrvsFieldValue(gObjLblRsrcBndle.getString("PPMT.CASH"));
						}
					}
					lStrPaymentScheme = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrPaymentScheme) && lStrPaymentScheme != null) {
						if (gObjRsrcBndle.getString("PAYSCHEME.MONEYORDER").equals(lStrPaymentScheme)) {
							lObjCorrectionDtls.setCrntFieldValue(gObjLblRsrcBndle.getString("PPMT.MONEYORDER"));
						} else if (gObjRsrcBndle.getString("PAYSCHEME.REGULAR").equals(lStrPaymentScheme)) {
							lObjCorrectionDtls.setCrntFieldValue(gObjLblRsrcBndle.getString("PPMT.REGULAR"));
						} else if (gObjRsrcBndle.getString("PAYSCHEME.CASH").equals(lStrPaymentScheme)) {
							lObjCorrectionDtls.setCrntFieldValue(gObjLblRsrcBndle.getString("PPMT.CASH"));
						}
					}

				} else if ("Pensioner's Category".equals(lObjCorrectionDtls.getFieldType())) {
					lStrHeadCode = lObjCorrectionDtls.getPrvsFieldValue();
					if (!"".equals(lStrHeadCode) && lStrHeadCode != null) {
						lStrHeadCodeDesc = lObjPhysicalCaseInwardDAO.getHeadCodeDesc(lStrHeadCode, gLngLangId);
						// lStrHeadCodeSeries=lMapHeadCodeSeries.get(new
						// BigDecimal(lStrHeadCode));
					}
					lObjCorrectionDtls.setPrvsFieldValue(lStrHeadCodeDesc);
					lStrHeadCode = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrHeadCode) && lStrHeadCode != null) {
						lStrHeadCodeDesc = lObjPhysicalCaseInwardDAO.getHeadCodeDesc(lStrHeadCode, gLngLangId);
						// lStrHeadCodeSeries=lMapHeadCodeSeries.get(new
						// BigDecimal(lStrHeadCode));
					}
					lObjCorrectionDtls.setCrntFieldValue(lStrHeadCodeDesc);
				} else if ("Pay Commission Revision".equals(lObjCorrectionDtls.getFieldType())) {
					lStrRopType = lObjCorrectionDtls.getPrvsFieldValue();
					if (!"".equals(lStrRopType) && lStrRopType != null) {
						if (gObjRsrcBndle.getString("PPMT.ROPTYPE1986").equals(lStrRopType)) {
							lObjCorrectionDtls.setPrvsFieldValue(gObjLblRsrcBndle.getString("PPMT.1986"));
						} else if (gObjRsrcBndle.getString("PPMT.ROPTYPE1996").equals(lStrRopType)) {
							lObjCorrectionDtls.setPrvsFieldValue(gObjLblRsrcBndle.getString("PPMT.1996"));
						} else if (gObjRsrcBndle.getString("PPMT.ROPTYPE2006").equals(lStrRopType)) {
							lObjCorrectionDtls.setPrvsFieldValue(gObjLblRsrcBndle.getString("PPMT.2006"));
						}
					}

					lStrRopType = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrRopType) && lStrRopType != null) {
						if (gObjRsrcBndle.getString("PPMT.ROPTYPE1986").equals(lStrRopType)) {
							lObjCorrectionDtls.setCrntFieldValue(gObjLblRsrcBndle.getString("PPMT.1986"));
						} else if (gObjRsrcBndle.getString("PPMT.ROPTYPE1996").equals(lStrRopType)) {
							lObjCorrectionDtls.setCrntFieldValue(gObjLblRsrcBndle.getString("PPMT.1996"));
						} else if (gObjRsrcBndle.getString("PPMT.ROPTYPE2006").equals(lStrRopType)) {
							lObjCorrectionDtls.setCrntFieldValue(gObjLblRsrcBndle.getString("PPMT.2006"));
						}
					}

				} else if ("Retiring Department".equals(lObjCorrectionDtls.getFieldType())) {
					lStrLocCode = lObjCorrectionDtls.getPrvsFieldValue();

					if (!"".equals(lStrLocCode) && lStrLocCode != null) {
						if ("-2".equals(lStrLocCode)) {
							lStrDeptName = "Unknown";
						} else {
							lStrDeptName = lObjPhysicalCaseInwardDAO.getDepartmentName(lLngDepartmentId, Long.parseLong(lStrLocCode), gLngLangId);
						}
					}
					lObjCorrectionDtls.setPrvsFieldValue(lStrDeptName);
					lStrLocCode = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrLocCode) && lStrLocCode != null) {
						if ("-2".equals(lStrLocCode)) {
							lStrDeptName = "Unknown";
						} else {
							lStrDeptName = lObjPhysicalCaseInwardDAO.getDepartmentName(lLngDepartmentId, Long.parseLong(lStrLocCode), gLngLangId);
						}
					}
					lObjCorrectionDtls.setCrntFieldValue(lStrDeptName);
				} else if ("Pensioner State".equals(lObjCorrectionDtls.getFieldType()) || "Guardian State".equals(lObjCorrectionDtls.getFieldType())) {
					lStrStateCode = lObjCorrectionDtls.getPrvsFieldValue();
					if (!"".equals(lStrStateCode) && lStrStateCode != null) {
						lStrStateName = lObjPhysicalCaseInwardDAO.getStateName(Long.parseLong(lStrStateCode), gLngLangId);
					}
					lObjCorrectionDtls.setPrvsFieldValue(lStrStateName);
					lStrStateCode = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrStateCode) && lStrStateCode != null) {
						lStrStateName = lObjPhysicalCaseInwardDAO.getStateName(Long.parseLong(lStrStateCode), gLngLangId);
					}
					lObjCorrectionDtls.setCrntFieldValue(lStrStateName);
				} else if ("Pensioner District".equals(lObjCorrectionDtls.getFieldType()) || "Guardian District".equals(lObjCorrectionDtls.getFieldType())) {
					lStrDistrictCode = lObjCorrectionDtls.getPrvsFieldValue();
					if (!"".equals(lStrDistrictCode) && lStrDistrictCode != null) {
						lStrDistrictName = lObjPhysicalCaseInwardDAO.getDistrictName(lStrDistrictCode, gLngLangId);
					}
					lObjCorrectionDtls.setPrvsFieldValue(lStrDistrictName);
					lStrDistrictCode = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrDistrictCode) && lStrDistrictCode != null) {
						lStrDistrictName = lObjPhysicalCaseInwardDAO.getDistrictName(lStrDistrictCode, gLngLangId);
					}
					lObjCorrectionDtls.setCrntFieldValue(lStrDistrictName);
				} else if ("Pensioner Bank".equals(lObjCorrectionDtls.getFieldType())) {
					lStrBankCode = lObjCorrectionDtls.getPrvsFieldValue();
					if (!"".equals(lStrBankCode) && lStrBankCode != null) {
						lStrBankName = lObjPhysicalCaseInwardDAO.getBankName(Long.parseLong(lStrBankCode));
					}
					lObjCorrectionDtls.setPrvsFieldValue(lStrBankName);
					lStrBankCode = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrBankCode) && lStrBankCode != null) {
						lStrBankName = lObjPhysicalCaseInwardDAO.getBankName(Long.parseLong(lStrBankCode));
					}
					lObjCorrectionDtls.setCrntFieldValue(lStrBankName);
				} else if ("Pensioner Branch".equals(lObjCorrectionDtls.getFieldType())) {
					lStrBranchCode = lObjCorrectionDtls.getPrvsFieldValue();
					if (!"".equals(lStrBranchCode) && lStrBranchCode != null) {
						lStrBranchName = lObjPhysicalCaseInwardDAO.getBranchName(Long.parseLong(lStrBranchCode), gStrLocCode);
					}
					lObjCorrectionDtls.setPrvsFieldValue(lStrBranchName);
					lStrBranchCode = lObjCorrectionDtls.getCrntFieldValue();
					if (!"".equals(lStrBranchCode) && lStrBranchCode != null) {
						lStrBranchName = lObjPhysicalCaseInwardDAO.getBranchName(Long.parseLong(lStrBranchCode), gStrLocCode);
					}
					lObjCorrectionDtls.setCrntFieldValue(lStrBranchName);
				} else if ("Over Payment For Month".equalsIgnoreCase(lObjCorrectionDtls.getFieldType())
						|| "Arrears of license fee for Government Accommodation For Month".equalsIgnoreCase(lObjCorrectionDtls.getFieldType())
						|| "License fee for the retention of Government For Month".equalsIgnoreCase(lObjCorrectionDtls.getFieldType())
						|| "Arrear Payment of DA Pay in Month".equalsIgnoreCase(lObjCorrectionDtls.getFieldType())
						|| "Arrear Payment of 6 PC Pay in Month".equalsIgnoreCase(lObjCorrectionDtls.getFieldType())
						|| "Arrear Payment of Pension Pay in Month".equalsIgnoreCase(lObjCorrectionDtls.getFieldType())
						|| "Difference of Commuted Pension Pay in Month".equalsIgnoreCase(lObjCorrectionDtls.getFieldType())
						|| "Any Other Difference Amount Pay in Month".equalsIgnoreCase(lObjCorrectionDtls.getFieldType())
						|| "Pension Cut Month".equalsIgnoreCase(lObjCorrectionDtls.getFieldType())) {
					if (!"".equals(lObjCorrectionDtls.getPrvsFieldValue()) && lObjCorrectionDtls.getPrvsFieldValue() != null) {
						lIntMonth = Integer.parseInt(lObjCorrectionDtls.getPrvsFieldValue());
						lStrMonth = getMonthName(lIntMonth);
						lObjCorrectionDtls.setPrvsFieldValue(lStrMonth);
					}
					if (!"".equals(lObjCorrectionDtls.getCrntFieldValue()) && lObjCorrectionDtls.getCrntFieldValue() != null) {
						lIntMonth = Integer.parseInt(lObjCorrectionDtls.getCrntFieldValue());
						lStrMonth = getMonthName(lIntMonth);
						lObjCorrectionDtls.setCrntFieldValue(lStrMonth);
					}
				} else if ("Commutation Treasury for Payment".equals(lObjCorrectionDtls.getFieldType()) ||
						"DCRG Treasury for Payment".equals(lObjCorrectionDtls.getFieldType())) {
					lStrTrsryCode = lObjCorrectionDtls.getPrvsFieldValue();
					if (lStrTrsryCode != null && !"".equals(lStrTrsryCode)) {
						lStrTrsryName = lObjPhysicalCaseInwardDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrTrsryCode));
					}
					lObjCorrectionDtls.setPrvsFieldValue(lStrBranchName);
					lStrTrsryCode = lObjCorrectionDtls.getCrntFieldValue();
					if (lStrTrsryCode != null && !"".equals(lStrTrsryCode)) {
						lStrTrsryName = lObjPhysicalCaseInwardDAO.getTreasuryName(gLngLangId, Long.parseLong(lStrTrsryCode));
					}
					lObjCorrectionDtls.setCrntFieldValue(lStrTrsryName);
				}
				else if (lObjCorrectionDtls.getFieldType().indexOf("~") != -1) {
					lStrFieldType = lObjCorrectionDtls.getFieldType().substring(0, lObjCorrectionDtls.getFieldType().indexOf("~"));
					if ("Provisional Pension Charged Voted Flag".equals(lStrFieldType)) {
						lStrChargedVotedFlag = lObjCorrectionDtls.getPrvsFieldValue();
						if (!"".equals(lStrChargedVotedFlag) && lStrChargedVotedFlag != null) {
							if ("C".equals(lStrChargedVotedFlag)) {
								lObjCorrectionDtls.setPrvsFieldValue(gObjRsrcBndle.getString("PPMT.CHARGED"));
							} else if ("V".equals(lStrChargedVotedFlag)) {
								lObjCorrectionDtls.setPrvsFieldValue(gObjRsrcBndle.getString("PPMT.VOTED"));
							}
						}

						lStrChargedVotedFlag = lObjCorrectionDtls.getCrntFieldValue();
						if (!"".equals(lStrChargedVotedFlag) && lStrChargedVotedFlag != null) {
							if ("C".equals(lStrChargedVotedFlag)) {
								lObjCorrectionDtls.setCrntFieldValue(gObjRsrcBndle.getString("PPMT.CHARGED"));
							} else if ("V".equals(lStrChargedVotedFlag)) {
								lObjCorrectionDtls.setCrntFieldValue(gObjRsrcBndle.getString("PPMT.VOTED"));
							}
						}

					} else if ("Nominee Bank".equals(lStrFieldType)) {
						lStrBankCode = lObjCorrectionDtls.getPrvsFieldValue();
						if (!"".equals(lStrBankCode) && lStrBankCode != null) {
							lStrBankName = lObjPhysicalCaseInwardDAO.getBankName(Long.parseLong(lStrBankCode));
						}
						lObjCorrectionDtls.setPrvsFieldValue(lStrBankName);
						lStrBankCode = lObjCorrectionDtls.getCrntFieldValue();
						if (!"".equals(lStrBankCode) && lStrBankCode != null) {
							lStrBankName = lObjPhysicalCaseInwardDAO.getBankName(Long.parseLong(lStrBankCode));
						}
						lObjCorrectionDtls.setCrntFieldValue(lStrBankName);
					} else if ("Nominee Branch".equals(lStrFieldType)) {
						lStrBranchCode = lObjCorrectionDtls.getPrvsFieldValue();
						if (!"".equals(lStrBranchCode) && lStrBranchCode != null) {
							lStrBranchName = lObjPhysicalCaseInwardDAO.getBranchName(Long.parseLong(lStrBranchCode), gStrLocCode);
						}
						lObjCorrectionDtls.setPrvsFieldValue(lStrBranchName);
						lStrBranchCode = lObjCorrectionDtls.getCrntFieldValue();
						if (!"".equals(lStrBranchCode) && lStrBranchCode != null) {
							lStrBranchName = lObjPhysicalCaseInwardDAO.getBranchName(Long.parseLong(lStrBranchCode), gStrLocCode);
						}
						lObjCorrectionDtls.setCrntFieldValue(lStrBranchName);
					} else if ("Provisional Voucher Month".equals(lStrFieldType) || "Advance Detail Start Month".equalsIgnoreCase(lStrFieldType)
							|| "Advance Detail End Month".equalsIgnoreCase(lStrFieldType) || "Other Recovery Start Month".equalsIgnoreCase(lStrFieldType)
							|| "Other Recovery End Month".equalsIgnoreCase(lStrFieldType)) {

						if (!"".equals(lObjCorrectionDtls.getPrvsFieldValue()) && lObjCorrectionDtls.getPrvsFieldValue() != null) {
							lIntMonth = Integer.parseInt(lObjCorrectionDtls.getPrvsFieldValue());
							lStrMonth = getMonthName(lIntMonth);
							lObjCorrectionDtls.setPrvsFieldValue(lStrMonth);
						}
						if (!"".equals(lObjCorrectionDtls.getCrntFieldValue()) && lObjCorrectionDtls.getCrntFieldValue() != null) {
							lIntMonth = Integer.parseInt(lObjCorrectionDtls.getCrntFieldValue());
							lStrMonth = getMonthName(lIntMonth);
							lObjCorrectionDtls.setCrntFieldValue(lStrMonth);
						}

					}
				}
			}
			inputMap.put("CorrectionDtls", lLstTrnPensionCorrectionDtls);
			inputMap.put("PpoNo", lStrPpoNo);
			resObj.setResultValue(inputMap);
			resObj.setViewName("CorrectionPopUp");
		} catch (Exception e) {
			
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	private String getMonthName(Integer lIntMonth) {

		String lStrMonthName = "";
		switch (lIntMonth) {

			case 1 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.JANUARY");
				break;
			case 2 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.FEBRUARY");
				break;
			case 3 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.MARCH");
				break;
			case 4 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.APRIL");
				break;
			case 5 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.MAY");
				break;
			case 6 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.JUNE");
				break;
			case 7 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.JULY");
				break;
			case 8 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.AUGUST");
				break;
			case 9 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.SEPTEMBER");
				break;
			case 10 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.OCTOBER");
				break;
			case 11 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.NOVEMBER");
				break;
			case 12 :
				lStrMonthName = gObjRsrcBndle.getString("MONTH.DECEMBER");
				break;

		}

		return lStrMonthName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#approveModification
	 * (java.util.Map)
	 */
	public ResultObject approveModification(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		StringBuilder lStrBldXML = new StringBuilder();
		try {

			setSessionInfo(inputMap);
			String lStrPensionerCode = null;
			List<Long> lLstPensionerCode = new ArrayList<Long>();
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCorrectionDtls.class, serv.getSessionFactory());
			lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			if (!"".equals(lStrPensionerCode)) {
				String[] lArrStrPensionerCode = lStrPensionerCode.split("~");
				for (int lIntCnt = 0; lIntCnt < lArrStrPensionerCode.length; lIntCnt++) {
					if (lArrStrPensionerCode[lIntCnt] != null && !lArrStrPensionerCode[lIntCnt].equals("")) {
						lLstPensionerCode.add(Long.parseLong(lArrStrPensionerCode[lIntCnt]));

					}
				}
				if (!lLstPensionerCode.isEmpty()) {
					lObjPhysicalCaseInwardDAO.updateCorrectionDtlsByPensionerCode(lLstPensionerCode, "A", gLngPostId, gLngUserId, gCurrDate);
				}
				lStrBldXML = getResponseXMLDocForApprove("Approved");
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setResultValue(inputMap);
				resObj.setViewName("ajaxData");
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#
	 * getPensionerDetailsFrmPpoNo(java.util.Map)
	 */
	public ResultObject getPensionerDetailsFrmPpoNo(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String lStrPensionerCode = null;
		String lStrPpoNo = null;
		List lLstResult = new ArrayList();
		BigDecimal lBgDcmlTotalAmt = BigDecimal.ZERO;
		PensionAuditRegisterVO lObjPensionAuditRegisterVO = new PensionAuditRegisterVO();
		List<TrnPrvosionalPensionDtls> lLstTrnProvisionalPensionDtls = new ArrayList<TrnPrvosionalPensionDtls>();
		List<MstPensionerNomineeDtls> lLstPensionerNomineeDtls = new ArrayList<MstPensionerNomineeDtls>();
		Set<CmnAttachmentMpg> cmnAttachmentMpgs = null;
		Iterator<CmnAttachmentMpg> cmnAttachmentMpgIterator = null;
		CmnAttachmentMpg cmnAttachmentMpg = null;
		Long lLngPhotoAttachmentId = null;
		Long lLngSignAttachmentId = null;

		List lReturnList = null;
		Iterator lObjIterator = null;
		List lLstDataList = null;
		String lStrYear = null;
		String lStrMonth = null;
		BigDecimal lBgDcmlNetAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlDiffAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlRecvAmt = BigDecimal.ZERO;
		Long lLngNetAmt = null;
		Long lLngDiffAmt = null;
		Long lLngRecvAmt = null;
		try {

			setSessionInfo(inputMap);
			lStrPpoNo = StringUtility.getParameter("ppoNo", request);
			PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			if (!"".equals(lStrPpoNo)) {
				lStrPensionerCode = lObjPensionBillDAO.getPnsnCodeFromPPONo(lStrPpoNo, gObjRsrcBndle.getString("STATUS.CONTINUE"),gStrLocCode);
			}
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCorrectionDtls.class, serv.getSessionFactory());

			lLstResult = lObjPhysicalCaseInwardDAO.getPnsnrDtlsFromPnsnrCode(lStrPensionerCode, gStrLocCode, gLngLangId);

			if (!lLstResult.isEmpty()) {
				Object[] obj = (Object[]) lLstResult.get(0);
				if (obj[0] != null) {
					lObjPensionAuditRegisterVO.setPensionerCode(obj[0].toString());
				}
				if (obj[1] != null) {
					lObjPensionAuditRegisterVO.setPpoNo(obj[1].toString());
				}
				if (obj[2] != null) {
					lObjPensionAuditRegisterVO.setPensionerName(obj[2].toString());
				}
				if (obj[3] != null) {
					lObjPensionAuditRegisterVO.setLedgerNo(obj[3].toString());
				}
				if (obj[4] != null) {
					lObjPensionAuditRegisterVO.setPageNo(obj[4].toString());
				}
				if (obj[5] != null) {
					lObjPensionAuditRegisterVO.setApplicationNo(obj[5].toString());
				}
				if (obj[6] != null) {
					lObjPensionAuditRegisterVO.setPnsrFileNo(obj[6].toString());
				}
				if (obj[7] != null) {
					lObjPensionAuditRegisterVO.setGuardianName(obj[7].toString());
				}
				if (obj[8] != null) {
					lObjPensionAuditRegisterVO.setBankName(obj[8].toString());
				}
				if (obj[9] != null) {
					lObjPensionAuditRegisterVO.setBranchName(obj[9].toString());
				}
				if (obj[10] != null) {
					lObjPensionAuditRegisterVO.setPensionType(obj[10].toString());
				}
				if (obj[11] != null) {
					lObjPensionAuditRegisterVO.setAccountNo(obj[11].toString());
				}
				if (obj[12] != null) {
					lObjPensionAuditRegisterVO.setDesignation(obj[12].toString());
				}
				if (obj[13] != null) {
					lObjPensionAuditRegisterVO.setJoiningDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[13])));
				}
				if (obj[14] != null) {
					lObjPensionAuditRegisterVO.setRetirementDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[14])));
				}
				if (obj[15] != null) {
					lObjPensionAuditRegisterVO.setBirthDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[15])));
				}
				if (obj[16] != null) {
					lObjPensionAuditRegisterVO.setIdentificationDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[16])));
				}
				if (obj[17] != null) {
					lObjPensionAuditRegisterVO.setIdentityMark(obj[17].toString());
				}
				if (obj[18] != null) {
					lObjPensionAuditRegisterVO.setPnsnrAddr1(obj[18].toString());
				}
				if (obj[19] != null) {
					lObjPensionAuditRegisterVO.setPnsnrAddr2(obj[19].toString());
				}
				if (obj[20] != null) {
					lObjPensionAuditRegisterVO.setPnsnrAddrTown(obj[20].toString());
				}
				if (obj[21] != null) {
					lObjPensionAuditRegisterVO.setPinCode(Integer.parseInt(obj[21].toString()));
				}
				if (obj[22] != null) {
					lObjPensionAuditRegisterVO.setStateName(obj[22].toString());
				}
				if (obj[23] != null) {
					lObjPensionAuditRegisterVO.setDistrictName(obj[23].toString());
				}
				if (obj[24] != null) {
					lObjPensionAuditRegisterVO.setPanNo(obj[24].toString());
				}
				if (obj[25] != null) {
					lObjPensionAuditRegisterVO.setMobileNo(obj[25].toString());
				}
				if (obj[26] != null) {
					lObjPensionAuditRegisterVO.setTeleNo(obj[26].toString());
				}
				if (obj[27] != null) {
					lObjPensionAuditRegisterVO.setPnsnrEmailId(obj[27].toString());
				}
				if (obj[28] != null) {
					lObjPensionAuditRegisterVO.setBasicPensionAmount(new BigDecimal(obj[28].toString()));
				}
				if (obj[29] != null) {
					lObjPensionAuditRegisterVO.setCommencementDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[29])));
				}
				if (obj[30] != null) {
					lObjPensionAuditRegisterVO.setCvpAmount(new BigDecimal(obj[30].toString()));
				}
				if (obj[31] != null) {
					lObjPensionAuditRegisterVO.setDcrgOrderNo(obj[31].toString());
				}
				if (obj[32] != null) {
					lObjPensionAuditRegisterVO.setGratuityAmount(new BigDecimal(obj[32].toString()));
				}
				if (obj[33] != null) {
					lObjPensionAuditRegisterVO.setFamilyMemName(obj[33].toString());
				}
				if (obj[34] != null) {
					lObjPensionAuditRegisterVO.setFp1Amount(new BigDecimal(obj[34].toString()));
				}
				if (obj[35] != null) {
					lObjPensionAuditRegisterVO.setFp1Date(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[35])));
				}
				if (obj[36] != null) {
					lObjPensionAuditRegisterVO.setFp2Amount(new BigDecimal(obj[36].toString()));
				}
				if (obj[37] != null) {
					lObjPensionAuditRegisterVO.setFp2Date(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[37])));
				}
				if (obj[38] != null) {
					lObjPensionAuditRegisterVO.setOrgBf1436(new BigDecimal(obj[38].toString()));
				}
				if (obj[39] != null) {
					lObjPensionAuditRegisterVO.setOrgAf1436(new BigDecimal(obj[39].toString()));
				}
				if (obj[40] != null) {
					lObjPensionAuditRegisterVO.setOrgAf1156(new BigDecimal(obj[40].toString()));
				}
				if (obj[41] != null) {
					lObjPensionAuditRegisterVO.setOrgBf1560(new BigDecimal(obj[41].toString()));
				}
				if (obj[42] != null) {
					lObjPensionAuditRegisterVO.setOrgAfZp(new BigDecimal(obj[42].toString()));
				}

				// get photo and signature dtls
				if (obj[43] != null) {
					Long lLngSrNo = 0L;
					CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
					CmnAttachmentMst lObjCmnAttachmentMst = null;
					if (obj[43] != null) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(obj[43].toString()));

						cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

						if (lObjCmnAttachmentMst != null) {
							lLngPhotoAttachmentId = lObjCmnAttachmentMst.getAttachmentId();
						}
						if (lObjCmnAttachmentMst != null && lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
							cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
						}
						cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();

						for (int j = 0; j < cmnAttachmentMpgs.size(); j++) {
							cmnAttachmentMpg = cmnAttachmentMpgIterator.next();
							if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("photo")) {
								lLngSrNo = cmnAttachmentMpg.getSrNo();
								inputMap.put("Photo", lObjCmnAttachmentMst);
								inputMap.put("PhotoId", lLngPhotoAttachmentId);
								inputMap.put("PhotosrNo", lLngSrNo);
							}
						}
					}

					if (obj[44] != null) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(obj[44].toString()));

						cmnAttachmentMpgs = new HashSet<CmnAttachmentMpg>();

						if (lObjCmnAttachmentMst != null) {
							lLngSignAttachmentId = lObjCmnAttachmentMst.getAttachmentId();
						}
						if (lObjCmnAttachmentMst != null && lObjCmnAttachmentMst.getCmnAttachmentMpgs() != null) {
							cmnAttachmentMpgs = lObjCmnAttachmentMst.getCmnAttachmentMpgs();
						}

						cmnAttachmentMpgIterator = cmnAttachmentMpgs.iterator();

						for (int j = 0; j < cmnAttachmentMpgs.size(); j++) {
							cmnAttachmentMpg = cmnAttachmentMpgIterator.next();
							if (cmnAttachmentMpg.getAttachmentDesc().equalsIgnoreCase("signature")) {
								lLngSrNo = cmnAttachmentMpg.getSrNo();
								inputMap.put("Sign", lObjCmnAttachmentMst);
								inputMap.put("SignId", lLngSignAttachmentId);
								inputMap.put("SignsrNo", lLngSrNo);
							}
						}
					}
				}
				if (obj[45] != null) {
					lObjPensionAuditRegisterVO.setReducedPension(new BigDecimal(obj[45].toString()));
				}
				if (obj[46] != null) {
					lObjPensionAuditRegisterVO.setPnsnSanctionAuthority(obj[46].toString());
				}
				if (obj[47] != null) {
					lObjPensionAuditRegisterVO.setCommutationAuthority(obj[47].toString());
				}

			}
			lLstTrnProvisionalPensionDtls = lObjPhysicalCaseInwardDAO.getProvisionalPensionDtls(lStrPensionerCode);
			lLstPensionerNomineeDtls = lObjPhysicalCaseInwardDAO.getMstPensionerNomineeDtlsVO(lStrPensionerCode);
			lLstDataList = new ArrayList();
			MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			lReturnList = lObjMonthlyPensionBillDAO.getMonthlyDataForAuditRegister(lStrPensionerCode, gStrLocCode);
			lObjIterator = lReturnList.iterator();
			Map lMpYearlyData = new LinkedHashMap();
			Map lMpAmount = new HashMap();
			String[] lArrNetAmt = new String[12];
			String[] lArrDiffAmt = new String[12];
			String[] lArrRecvAmt = new String[12];

			while (lObjIterator.hasNext()) {

				Object[] lArrObj = (Object[]) lObjIterator.next();
				lStrYear = lArrObj[0].toString().substring(0, 4);
				lStrMonth = lArrObj[0].toString().substring(4);

				lBgDcmlNetAmt = (BigDecimal) lArrObj[1];
				lBgDcmlDiffAmt = (BigDecimal) lArrObj[2];
				lBgDcmlRecvAmt = (BigDecimal) lArrObj[3];

				lLngNetAmt = lBgDcmlNetAmt.longValue();
				lLngDiffAmt = lBgDcmlDiffAmt.longValue();
				lLngRecvAmt = lBgDcmlRecvAmt.longValue();

				if (!lMpYearlyData.containsKey(lStrYear)) {
					lMpAmount = new HashMap();
					lArrNetAmt = new String[12];
					lArrDiffAmt = new String[12];
					lArrRecvAmt = new String[12];
				}
				lMpAmount = getMapOfAmounts(lStrYear, lStrMonth, lLngNetAmt.toString(), lLngDiffAmt.toString(), lLngRecvAmt.toString(), lMpAmount, lArrNetAmt, lArrDiffAmt, lArrRecvAmt);
				lMpYearlyData.put(lStrYear, lMpAmount);
			}
			Set<String> lKeySet = lMpYearlyData.keySet();

			List lLstRowList = new ArrayList();

			for (String lStrTempYear : lKeySet) {
				lLstDataList = new ArrayList();
				lLstDataList.add("");
				lLstDataList.add("Net Pen");
				lMpAmount = new HashMap();
				lMpAmount = (Map) lMpYearlyData.get(lStrTempYear);
				String[] lArrNetAmount = (String[]) lMpAmount.get("NetAmt_" + lStrTempYear);
				for (String lNetAmt : lArrNetAmount) {
					lLstDataList.add(lNetAmt);// .substring(0,lNetAmt.indexOf(".")));
				}
				lLstRowList.add(lLstDataList);
				lLstDataList = new ArrayList();
				lLstDataList.add(lStrTempYear);
				lLstDataList.add("Diff Amt");

				String[] lArrDiffAmount = (String[]) lMpAmount.get("DiffAmt_" + lStrTempYear);
				for (String lDiffAmt : lArrDiffAmount) {
					lLstDataList.add(lDiffAmt);// .substring(0,
					// lDiffAmt.indexOf(".")));
				}
				lLstRowList.add(lLstDataList);
				lLstDataList = new ArrayList();
				lLstDataList.add("");
				lLstDataList.add("Recv Amt");

				String[] lArrRecvAmount = (String[]) lMpAmount.get("RecvAmt_" + lStrTempYear);
				for (String lRecvAmt : lArrRecvAmount) {
					lLstDataList.add(lRecvAmt);// .substring(0,
					// lRecvAmt.indexOf(".")));
				}
				lLstRowList.add(lLstDataList);

			}

			inputMap.put("RowList", lLstRowList);
			inputMap.put("PensionAuditRegisterVO", lObjPensionAuditRegisterVO);
			inputMap.put("TrnProvisionalPensionDtls", lLstTrnProvisionalPensionDtls);
			inputMap.put("PensionerNomineeDtls", lLstPensionerNomineeDtls);
			inputMap.put("PpoNo", lStrPpoNo);

			resObj.setResultValue(inputMap);
			resObj.setViewName("AuditPensionRegisterReport");
		} catch (Exception e) {
		
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	public ResultObject getMonthlyDataForAuditRegister(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		List lReturnList = null;
		String lStrPensionerCode = "991005515670";
		Iterator lObjIterator = null;
		List lLstDataList = null;
		String lStrYear = null;
		String lStrMonth = null;
		BigDecimal lBgDcmlNetAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlDiffAmt = BigDecimal.ZERO;
		BigDecimal lBgDcmlRecvAmt = BigDecimal.ZERO;
		Long lLngNetAmt = null;
		Long lLngDiffAmt = null;
		Long lLngRecvAmt = null;
		setSessionInfo(inputMap);
		try {
			lLstDataList = new ArrayList();
			lReturnList = lObjMonthlyPensionBillDAO.getMonthlyDataForAuditRegister(lStrPensionerCode, gStrLocCode);
			lObjIterator = lReturnList.iterator();
			Map lMpYearlyData = new LinkedHashMap();
			Map lMpAmount = new HashMap();
			String[] lArrNetAmt = new String[12];
			String[] lArrDiffAmt = new String[12];
			String[] lArrRecvAmt = new String[12];

			while (lObjIterator.hasNext()) {

				Object[] lArrObj = (Object[]) lObjIterator.next();
				lStrYear = lArrObj[0].toString().substring(0, 4);
				lStrMonth = lArrObj[0].toString().substring(4);

				lBgDcmlNetAmt = (BigDecimal) lArrObj[1];
				lBgDcmlDiffAmt = (BigDecimal) lArrObj[2];
				lBgDcmlRecvAmt = (BigDecimal) lArrObj[3];

				lLngNetAmt = lBgDcmlNetAmt.longValue();
				lLngDiffAmt = lBgDcmlDiffAmt.longValue();
				lLngRecvAmt = lBgDcmlRecvAmt.longValue();

				if (!lMpYearlyData.containsKey(lStrYear)) {
					lMpAmount = new HashMap();
					lArrNetAmt = new String[12];
					lArrDiffAmt = new String[12];
					lArrRecvAmt = new String[12];
				}
				lMpAmount = getMapOfAmounts(lStrYear, lStrMonth, lLngNetAmt.toString(), lLngDiffAmt.toString(), lLngRecvAmt.toString(), lMpAmount, lArrNetAmt, lArrDiffAmt, lArrRecvAmt);
				// lMpAmount = getMapOfAmounts(lStrYear, lStrMonth,
				// lArrObj[1].toString(), lArrObj[2].toString(),
				// lArrObj[3].toString(), lMpAmount);
				lMpYearlyData.put(lStrYear, lMpAmount);
			}
			Set<String> lKeySet = lMpYearlyData.keySet();

			List lLstRowList = new ArrayList();

			for (String lStrTempYear : lKeySet) {
				lLstDataList = new ArrayList();
				lLstDataList.add("");
				lLstDataList.add("Net Pen");
				lMpAmount = new HashMap();
				lMpAmount = (Map) lMpYearlyData.get(lStrTempYear);
				String[] lArrNetAmount = (String[]) lMpAmount.get("NetAmt_" + lStrTempYear);
				for (String lNetAmt : lArrNetAmount) {
					lLstDataList.add(lNetAmt);// .substring(0,lNetAmt.indexOf(".")));
				}
				lLstRowList.add(lLstDataList);
				lLstDataList = new ArrayList();
				lLstDataList.add(lStrTempYear);
				lLstDataList.add("Diff Amt");

				String[] lArrDiffAmount = (String[]) lMpAmount.get("DiffAmt_" + lStrTempYear);
				for (String lDiffAmt : lArrDiffAmount) {
					lLstDataList.add(lDiffAmt);// .substring(0,
					// lDiffAmt.indexOf(".")));
				}
				lLstRowList.add(lLstDataList);
				lLstDataList = new ArrayList();
				lLstDataList.add("");
				lLstDataList.add("Recv Amt");

				String[] lArrRecvAmount = (String[]) lMpAmount.get("RecvAmt_" + lStrTempYear);
				for (String lRecvAmt : lArrRecvAmount) {
					lLstDataList.add(lRecvAmt);// .substring(0,
					// lRecvAmt.indexOf(".")));
				}
				lLstRowList.add(lLstDataList);

			}

			inputMap.put("RowList", lLstRowList);
			objRes.setResultValue(inputMap);
			objRes.setViewName("monthlyPayDetails");
		} catch (Exception e) {
			gLogger.error("Error while getting list of change statement..." + e, e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			
		}

		return objRes;
	}

	private Map getMapOfAmounts(String lStrYear, String lStrMonth, String lStrNetAmt, String lStrDiffAmt, String lStrRecvAmt, Map lMpAmount, String[] lArrNetAmt, String[] lArrDiffAmt,
			String[] lArrRecvAmt) {

		if (!lMpAmount.isEmpty()) {
			lArrNetAmt = (String[]) lMpAmount.get("NetAmt_" + lStrYear);
			lArrDiffAmt = (String[]) lMpAmount.get("DiffAmt_" + lStrYear);
			lArrRecvAmt = (String[]) lMpAmount.get("RecvAmt_" + lStrYear);
		}

		if ("01".equals(lStrMonth)) {
			lArrNetAmt[0] = lStrNetAmt;
			lArrDiffAmt[0] = lStrDiffAmt;
			lArrRecvAmt[0] = lStrRecvAmt;

		} else {
			if (lArrNetAmt[0] == "" || lArrNetAmt[0] == null) {
				lArrNetAmt[0] = "0";
			}
			if (lArrDiffAmt[0] == "" || lArrDiffAmt[0] == null) {
				lArrDiffAmt[0] = "0";
			}
			if (lArrRecvAmt[0] == null || lArrRecvAmt[0] == "") {
				lArrRecvAmt[0] = "0";
			}
		}
		if ("02".equals(lStrMonth)) {
			lArrNetAmt[1] = lStrNetAmt;
			lArrDiffAmt[1] = lStrDiffAmt;
			lArrRecvAmt[1] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[1] == "" || lArrNetAmt[1] == null) {

				lArrNetAmt[1] = "0";
			}
			if (lArrDiffAmt[1] == "" || lArrDiffAmt[1] == null) {

				lArrDiffAmt[1] = "0";
			}
			if (lArrRecvAmt[1] == null || lArrRecvAmt[1] == "") {
				lArrRecvAmt[1] = "0";
			}
		}
		if ("03".equals(lStrMonth)) {
			lArrNetAmt[2] = lStrNetAmt;
			lArrDiffAmt[2] = lStrDiffAmt;
			lArrRecvAmt[2] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[2] == "" || lArrNetAmt[2] == null) {
				lArrNetAmt[2] = "0";
			}
			if (lArrDiffAmt[2] == "" || lArrDiffAmt[2] == null) {
				lArrDiffAmt[2] = "0";
			}
			if (lArrRecvAmt[2] == null || lArrRecvAmt[2] == "") {
				lArrRecvAmt[2] = "0";
			}
		}
		if ("04".equals(lStrMonth)) {

			lArrNetAmt[3] = lStrNetAmt;
			lArrDiffAmt[3] = lStrDiffAmt;
			lArrRecvAmt[3] = lStrRecvAmt;
		} else {

			if (lArrNetAmt[3] == "" || lArrNetAmt[3] == null) {
				lArrNetAmt[3] = "0";
			}
			if (lArrDiffAmt[3] == "" || lArrDiffAmt[3] == null) {
				lArrDiffAmt[3] = "0";
			}
			if (lArrRecvAmt[3] == null || lArrRecvAmt[3] == "") {
				lArrRecvAmt[3] = "0";
			}
		}
		if ("05".equals(lStrMonth)) {
			lArrNetAmt[4] = lStrNetAmt;
			lArrDiffAmt[4] = lStrDiffAmt;
			lArrRecvAmt[4] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[4] == "" || lArrNetAmt[4] == null) {
				lArrNetAmt[4] = "0";
			}
			if (lArrDiffAmt[4] == "" || lArrDiffAmt[4] == null) {
				lArrDiffAmt[4] = "0";
			}
			if (lArrRecvAmt[4] == null || lArrRecvAmt[4] == "") {
				lArrRecvAmt[4] = "0";
			}
		}
		if ("06".equals(lStrMonth)) {
			lArrNetAmt[5] = lStrNetAmt;
			lArrDiffAmt[5] = lStrDiffAmt;
			lArrRecvAmt[5] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[5] == "" || lArrNetAmt[5] == null) {
				lArrNetAmt[5] = "0";
			}
			if (lArrDiffAmt[5] == "" || lArrDiffAmt[5] == null) {
				lArrDiffAmt[5] = "0";
			}
			if (lArrRecvAmt[5] == null || lArrRecvAmt[5] == "") {
				lArrRecvAmt[5] = "0";
			}
		}
		if ("07".equals(lStrMonth)) {
			lArrNetAmt[6] = lStrNetAmt;
			lArrDiffAmt[6] = lStrDiffAmt;
			lArrRecvAmt[6] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[6] == "" || lArrNetAmt[6] == null) {
				lArrNetAmt[6] = "0";
			}
			if (lArrDiffAmt[6] == "" || lArrDiffAmt[6] == null) {
				lArrDiffAmt[6] = "0";
			}
			if (lArrRecvAmt[6] == null || lArrRecvAmt[6] == "") {
				lArrRecvAmt[6] = "0";
			}
		}
		if ("08".equals(lStrMonth)) {
			lArrNetAmt[7] = lStrNetAmt;
			lArrDiffAmt[7] = lStrDiffAmt;
			lArrRecvAmt[7] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[7] == "" || lArrNetAmt[7] == null) {
				lArrNetAmt[7] = "0";
			}
			if (lArrDiffAmt[7] == "" || lArrDiffAmt[7] == null) {
				lArrDiffAmt[7] = "0";
			}
			if (lArrRecvAmt[7] == null || lArrRecvAmt[7] == "") {
				lArrRecvAmt[7] = "0";
			}
		}
		if ("09".equals(lStrMonth)) {
			lArrNetAmt[8] = lStrNetAmt;
			lArrDiffAmt[8] = lStrDiffAmt;
			lArrRecvAmt[8] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[8] == "" || lArrNetAmt[8] == null) {
				lArrNetAmt[8] = "0";
			}
			if (lArrDiffAmt[8] == "" || lArrDiffAmt[8] == null) {
				lArrDiffAmt[8] = "0";
			}
			if (lArrRecvAmt[8] == null || lArrRecvAmt[8] == "") {
				lArrRecvAmt[8] = "0";
			}
		}
		if ("10".equals(lStrMonth)) {
			lArrNetAmt[9] = lStrNetAmt;
			lArrDiffAmt[9] = lStrDiffAmt;
			lArrRecvAmt[9] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[9] == "" || lArrNetAmt[9] == null) {
				lArrNetAmt[9] = "0";
			}
			if (lArrDiffAmt[9] == "" || lArrDiffAmt[9] == null) {
				lArrDiffAmt[9] = "0";
			}
			if (lArrRecvAmt[9] == null || lArrRecvAmt[9] == "") {
				lArrRecvAmt[9] = "0";
			}
		}
		if ("11".equals(lStrMonth)) {
			lArrNetAmt[10] = lStrNetAmt;
			lArrDiffAmt[10] = lStrDiffAmt;
			lArrRecvAmt[10] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[10] == "" || lArrNetAmt[10] == null) {
				lArrNetAmt[10] = "0";
			}
			if (lArrDiffAmt[10] == "" || lArrDiffAmt[10] == null) {
				lArrDiffAmt[10] = "0";
			}
			if (lArrRecvAmt[10] == null || lArrRecvAmt[10] == "") {
				lArrRecvAmt[10] = "0";
			}
		}
		if ("12".equals(lStrMonth)) {
			lArrNetAmt[11] = lStrNetAmt;
			lArrDiffAmt[11] = lStrDiffAmt;
			lArrRecvAmt[11] = lStrRecvAmt;
		} else {
			if (lArrNetAmt[11] == "" || lArrNetAmt[11] == null) {
				lArrNetAmt[11] = "0";
			}
			if (lArrDiffAmt[11] == "" || lArrDiffAmt[11] == null) {
				lArrDiffAmt[11] = "0";
			}
			if (lArrRecvAmt[11] == null || lArrRecvAmt[11] == "") {
				lArrRecvAmt[11] = "0";
			}

		}
		// if (lMpAmount.isEmpty()) {
		lMpAmount.put("NetAmt_" + lStrYear, lArrNetAmt);
		lMpAmount.put("DiffAmt_" + lStrYear, lArrDiffAmt);
		lMpAmount.put("RecvAmt_" + lStrYear, lArrRecvAmt);
		// }
		return lMpAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#validateSchemeCode
	 * (java.util.Map)
	 */
	public ResultObject validateSchemeCode(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
		String lStrSchemeCode = null;
		String lStrSchemeType = "";
		List lLstResult = null;
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			lStrSchemeCode = StringUtility.getParameter("SchemeCode", request);
			lStrSchemeType = StringUtility.getParameter("SchemeType", request);
			if (!"".equals(lStrSchemeCode)) {

				lLstResult = lObjPhysicalCaseInwardDAO.isValidSchemeCode(lStrSchemeCode,lStrSchemeType);
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ISEXISTS>");
			if (lLstResult.isEmpty()) {
				lStrBldXML.append("N");
			} else {
				lStrBldXML.append("Y");
			}
			lStrBldXML.append("</ISEXISTS>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#
	 * getOfficeListForAutoComplete(java.util.Map)
	 */
	public ResultObject getOfficeListForAutoComplete(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		setSessionInfo(inputMap);

		List<ComboValuesVO> lLstOfficeName = new ArrayList<ComboValuesVO>();
		try {
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			String lStrOfficeName = StringUtility.getParameter("searchKey", request);
			if (!"".equals(lStrOfficeName)) {
				lLstOfficeName = lObjPhysicalCaseInwardDAO.getOfficeListForAutoComplete(lStrOfficeName);
			}

			String lStrResult = new AjaxXmlBuilder().addItems(lLstOfficeName, "desc", "desc").toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#
	 * loadCommutationHistory(java.util.Map)
	 */
	public ResultObject loadCommutationHistory(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lLstHstCommutationDtlsVO = new ArrayList();
		setSessionInfo(inputMap);
		try {
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstCommutationDtls.class, serv.getSessionFactory());
			String lStrPensionerCode = StringUtility.getParameter("PensionerCode", request);
			if (!"".equals(lStrPensionerCode)) {
				lLstHstCommutationDtlsVO = lObjPhysicalCaseInwardDAO.getHstCommutationDtlsVO(lStrPensionerCode);
			}
			inputMap.put("lLstHstCommutationDtlsVO", lLstHstCommutationDtlsVO);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setViewName("CommutationHistoryPopUp");
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#loadDcrgHistory
	 * (java.util.Map)
	 */
	public ResultObject loadDcrgHistory(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lLstHstPnsnPmntDcrgDtlsVO = new ArrayList();
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsForDCRGHstryPopUp = new ArrayList<TrnPensionRecoveryDtls>();
		List<Long> lLstHstDcrgDtlsId = new ArrayList<Long>();
		setSessionInfo(inputMap);
		try {

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstPnsnPmntDcrgDtls.class, serv.getSessionFactory());
			String lStrPensionerCode = StringUtility.getParameter("PensionerCode", request);
			if (!"".equals(lStrPensionerCode)) {
				lLstHstPnsnPmntDcrgDtlsVO = lObjPhysicalCaseInwardDAO.getDcrgHistory(lStrPensionerCode);
			}
			if (!"".equals(lStrPensionerCode)) {
				lLstTrnPensionRecoveryDtlsForDCRGHstryPopUp = lObjPhysicalCaseInwardDAO.getTrnPensionRecoveryDtlsForDCRGHstryPopUp(lStrPensionerCode);
			}
			lLstHstDcrgDtlsId = lObjPhysicalCaseInwardDAO.getHstDcrgDtlsId(lStrPensionerCode);

			inputMap.put("Counter", lLstHstPnsnPmntDcrgDtlsVO.size());
			inputMap.put("lLstHstPnsnPmntDcrgDtlsVO", lLstHstPnsnPmntDcrgDtlsVO);
			inputMap.put("SubCounter", lLstTrnPensionRecoveryDtlsForDCRGHstryPopUp.size());
			inputMap.put("RecoveryDtlsForDCRGHstryPopUp", lLstTrnPensionRecoveryDtlsForDCRGHstryPopUp);
			inputMap.put("lLstHstDcrgDtlsId", lLstHstDcrgDtlsId);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {

			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setViewName("DcrgHistoryPopUp");
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#
	 * getDesgListForAutoComplete(java.util.Map)
	 */
	public ResultObject getDesgListForAutoComplete(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		setSessionInfo(inputMap);

		List<ComboValuesVO> lLstDesignation = new ArrayList<ComboValuesVO>();
		try {
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			String lStrDesignation = StringUtility.getParameter("searchKey", request);
			if (!"".equals(lStrDesignation)) {
				lLstDesignation = lObjPhysicalCaseInwardDAO.getDesignationForAutoComplete(lStrDesignation);
			}

			String lStrResult = new AjaxXmlBuilder().addItems(lLstDesignation, "desc", "desc").toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#loadSchemeCodePopUp
	 * (java.util.Map)
	 */
	public ResultObject loadSchemeCodePopUp(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<MstScheme> lLstMstSchemeVO = new ArrayList<MstScheme>();
		setSessionInfo(inputMap);
		Integer lIntTotalRecords = 0;
		try {
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstScheme.class, serv.getSessionFactory());
			String lStrElementId = StringUtility.getParameter("elementId", request);
			String lStrSchemeCode = StringUtility.getParameter("schemeCode", request);
			String lStrSchemeType = StringUtility.getParameter("schemeType", request);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lIntTotalRecords = lObjPhysicalCaseInwardDAO.getMstSchemeCount(gLngLangId,lStrSchemeCode,lStrSchemeType);
			if (lIntTotalRecords > 0) {
				lLstMstSchemeVO = lObjPhysicalCaseInwardDAO.getMstSchemeList(displayTag, gLngLangId,lStrSchemeCode,lStrSchemeType);
			}

			inputMap.put("lLstMstSchemeVO", lLstMstSchemeVO);
			inputMap.put("ElementId", lStrElementId);
			inputMap.put("SchemeCode", lStrSchemeCode);
			inputMap.put("SchemeType", lStrSchemeType);
			inputMap.put("totalRecords", lIntTotalRecords);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setViewName("SchemeCodePopUp");
		return resObj;
	}

	public ResultObject getPensionCaseRemarks(Map<String, Object> inputMap) {

		gLogger.info("In getPensionCaseRemarks method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = null;
		List lLstRemarks = null;
		String lStrPensionerCode = null;
		try {
			setSessionInfo(inputMap);
			lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);

			lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(null, serv.getSessionFactory());
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
			if (lStrPensionerCode.length() > 0) {
				lLstRemarks = lObjPhysicalCaseInwardDAO.getPensionCaseRemarks(lStrPensionerCode, gStrLocCode, gLngLangId);
				inputMap.put("lLstRemarks", lLstRemarks);
				objRes.setViewName("showPensionCaseRemarks");
			}
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}
		return objRes;

	}

	public ResultObject deleteCommutationHistory(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		setSessionInfo(inputMap);
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstCommutationDtls.class, serv.getSessionFactory());
			String lStrHstCvpDtlsId = StringUtility.getParameter("hstCvpDtlsId", request);

			String[] lArrStrHstCvpDtlsId = lStrHstCvpDtlsId.split("~");
			List<Long> lLstHstCvpDtlsId = new ArrayList<Long>();
			if (lArrStrHstCvpDtlsId.length > 0) {
				for (int lIntCnt = 0; lIntCnt < lArrStrHstCvpDtlsId.length; lIntCnt++) {
					lLstHstCvpDtlsId.add(Long.valueOf(lArrStrHstCvpDtlsId[lIntCnt]));
				}
			}

			if (!"".equals(lStrHstCvpDtlsId)) {
				lObjPhysicalCaseInwardDAO.deleteCommutationHistory(lLstHstCvpDtlsId);
			}
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append("Commutation History Saved Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject saveCommutationHistory(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		setSessionInfo(inputMap);
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstCommutationDtls.class, serv.getSessionFactory());
			HstCommutationDtls lObjHstCommutationDtls = new HstCommutationDtls();

			String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			String lStrCvpOrderNo = StringUtility.getParameter("cvpOrderNo", request);
			String lStrCvpOrderDate = StringUtility.getParameter("cvpOrderDate", request);
			//String lStrCvpTotalAmount = StringUtility.getParameter("cvpTotalAmount", request);
			String lStrPaymentAmount = StringUtility.getParameter("cvpPaymentAmount", request);
			String lStrVoucherNo = StringUtility.getParameter("voucherNo", request);
			String lStrVoucherDate = StringUtility.getParameter("voucherDate", request);
			String lStrNameOfPayee = StringUtility.getParameter("nameOfPayee", request);
			String lStrTrsryForPayment = StringUtility.getParameter("trsryForPayment", request);
			String lStrCvpAuthority = StringUtility.getParameter("cvpAuthority", request);
			String lStrCvpStartDate = StringUtility.getParameter("cvpStartDate", request);
			String lStrCvpEndDate = StringUtility.getParameter("cvpEndDate", request);
			String lStrBasicPensionAmt = StringUtility.getParameter("basicPensionAmt", request);
			String lStrCvpMonthlyAmt = StringUtility.getParameter("cvpMonthlyAmt", request);
			String lStrReducedPensionAmt = StringUtility.getParameter("reducedPensionAmt", request);
			Long lLngCvpDtlsId = IFMSCommonServiceImpl.getNextSeqNum("hst_commutation_dtls", inputMap);

			lObjHstCommutationDtls.setCvpDtlsId(lLngCvpDtlsId);
			lObjHstCommutationDtls.setDbId(gLngDBId);
			lObjHstCommutationDtls.setLocationCode(Long.parseLong(gStrLocCode));
			lObjHstCommutationDtls.setPensionerCode(lStrPensionerCode);
			if (!"".equals(lStrCvpOrderNo)) {
				lObjHstCommutationDtls.setOrderNo(lStrCvpOrderNo);
			}
			if (!"".equals(lStrCvpOrderDate)) {
				lObjHstCommutationDtls.setOrderDate(lObjSimpleDateFormat.parse(lStrCvpOrderDate));
			}
//			if (!"".equals(lStrCvpTotalAmount)) {
//				lObjHstCommutationDtls.setTotalOrderAmount(new BigDecimal(lStrCvpTotalAmount));
//			}
			if (!"".equals(lStrPaymentAmount)) {
				lObjHstCommutationDtls.setPaymentAmount(new BigDecimal(lStrPaymentAmount));
			}
			if (!"".equals(lStrVoucherNo)) {
				lObjHstCommutationDtls.setVoucherNo(lStrVoucherNo);
			}
			if (!"".equals(lStrVoucherDate)) {
				lObjHstCommutationDtls.setVoucherDate(lObjSimpleDateFormat.parse(lStrVoucherDate));
			}
			if(!"".equals(lStrNameOfPayee))	{
				lObjHstCommutationDtls.setNameOfPayee(lStrNameOfPayee);
			}
			if(!"".equals(lStrTrsryForPayment)) {
				lObjHstCommutationDtls.setTrsryForPayment(lStrTrsryForPayment);
			}
			if(!"".equals(lStrCvpAuthority)) {
				lObjHstCommutationDtls.setCvpAuthority(lStrCvpAuthority.trim());
			}
			if(!"".equals(lStrCvpStartDate)) {
				lObjHstCommutationDtls.setCvpStartDate(lObjSimpleDateFormat.parse(lStrCvpStartDate));
			}
			if(!"".equals(lStrCvpEndDate)) {
				lObjHstCommutationDtls.setCvpEndDate(lObjSimpleDateFormat.parse(lStrCvpEndDate));
			}
			if(!"".equals(lStrBasicPensionAmt)) {
				lObjHstCommutationDtls.setBasicPensionAmt(new BigDecimal(lStrBasicPensionAmt.trim()));
			}
			if(!"".equals(lStrCvpMonthlyAmt)) {
				lObjHstCommutationDtls.setCvpMonthlyAmt(new BigDecimal(lStrCvpMonthlyAmt.trim()));
			}
			if(!"".equals(lStrReducedPensionAmt)) {
				lObjHstCommutationDtls.setReducedPensionAmt(new BigDecimal(lStrReducedPensionAmt.trim()));
			}
			lObjHstCommutationDtls.setCreatedUserId(gLngUserId);
			lObjHstCommutationDtls.setCreatedPostId(gLngPostId);
			lObjHstCommutationDtls.setCreatedDate(gCurrDate);

			lObjPhysicalCaseInwardDAO.create(lObjHstCommutationDtls);

			lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			TrnPensionRqstHdr lObjTrnPensionRqstHdr = new TrnPensionRqstHdr();
			lObjTrnPensionRqstHdr = lObjPhysicalCaseInwardDAO.getTrnPensionRqstHdrVO(lStrPensionerCode);
			lObjTrnPensionRqstHdr.setCvpOrderNo(null);
			lObjTrnPensionRqstHdr.setCvpDate(null);
			lObjTrnPensionRqstHdr.setTotalCvpAmount(BigDecimal.ZERO);
			lObjTrnPensionRqstHdr.setCvpAmount(BigDecimal.ZERO);
			lObjTrnPensionRqstHdr.setCvpVoucherNo(null);
			lObjTrnPensionRqstHdr.setCvpVoucherDate(null);
			lObjTrnPensionRqstHdr.setCvpNameOfPayee(null);
			lObjTrnPensionRqstHdr.setCvpTrsryForPayment(null);
			lObjTrnPensionRqstHdr.setCvpAuthority(null);
			lObjTrnPensionRqstHdr.setCvpStartDate(null);
			lObjTrnPensionRqstHdr.setCvpEndDate(null);

			lObjPhysicalCaseInwardDAO.update(lObjTrnPensionRqstHdr);

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append("Commutation History Saved Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject saveDcrgHistoryDtls(Map<String, Object> inputMap) {

		gLogger.info("In saveDcrgHistoryDtls method.......");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = null;
		Long lLngDcrgDtlsId = 0l;
		Long lLngTrnPensionRecoveryDtlsId = 0l;
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtlsVO = null;
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		String lStrPensionerCode = null;
		String lStrMode = null;
		String lStrHstDcrgDtlsPKVal = null;
		String[] lStrArrHstDcrgDtlsPKVal = null;
		List<Long> lLstDcrgDtlsId = new ArrayList<Long>();
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsForDCRGHstry = new ArrayList<TrnPensionRecoveryDtls>();
		String lStrNature = null;
		String lStrAmount = null;
		String lStrSchemeCode = null;
		String[] lArrStrNature = null;
		String[] lArrStrAmount = null;
		String[] lArrStrSchemeCode = null;
		try {

			setSessionInfo(inputMap);
			lStrMode = (String) inputMap.get("Mode");
			lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstPnsnPmntDcrgDtls.class, serv.getSessionFactory());
			lStrHstDcrgDtlsPKVal = StringUtility.getParameter("hstDcrgDtlsPkArr", request);
			String lStrFlag = StringUtility.getParameter("flag", request);
			if (lStrFlag.equals("H")) // from view history screen :when save
										// button is called
			{
				if (!"".equals(lStrHstDcrgDtlsPKVal) && lStrHstDcrgDtlsPKVal.length() > 0) {

					lStrArrHstDcrgDtlsPKVal = lStrHstDcrgDtlsPKVal.split(",");
					for (int lIntCnt = 0; lIntCnt < lStrArrHstDcrgDtlsPKVal.length; lIntCnt++) {

						lLstDcrgDtlsId.add(Long.valueOf(lStrArrHstDcrgDtlsPKVal[lIntCnt].toString().trim()));
					}
					lObjPhysicalCaseInwardDAO.removeHstPnsnPmntDcrgDtlsAndRecoveryDtls(lLstDcrgDtlsId);
				}
			} else {

				lObjHstPnsnPmntDcrgDtlsVO = new HstPnsnPmntDcrgDtls();
				lObjHstPnsnPmntDcrgDtlsVO = (HstPnsnPmntDcrgDtls) inputMap.get("lObjHstPnsnPmntDcrgDtlsVO");
				if (lStrMode != null) {
					if (lStrMode.equalsIgnoreCase("Add")) {
						if (lObjHstPnsnPmntDcrgDtlsVO != null) {
							// insert data into History of DCRG table

							lLngDcrgDtlsId = IFMSCommonServiceImpl.getNextSeqNum("hst_pnsnpmnt_dcrg_dtls", inputMap);
							lObjHstPnsnPmntDcrgDtlsVO.setDcrgDtlsId(lLngDcrgDtlsId);
							lObjPhysicalCaseInwardDAO.create(lObjHstPnsnPmntDcrgDtlsVO);
							gLogger.info("Record Inserted in table hst_pnsnpmnt_dcrg_dtls successfully.");

							// update data into trn_pension_rqst_hdr (pension
							// case table)
							lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
							List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = lObjPhysicalCaseInwardDAO.getListByColumnAndValue("pensionerCode", lObjHstPnsnPmntDcrgDtlsVO.getPensionerCode().trim());
							if (!lLstTrnPensionRqstHdr.isEmpty()) {

								TrnPensionRqstHdr lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
								lObjTrnPensionRqstHdr.setDcrgAmount(null);
								// lObjTrnPensionRqstHdr.setDcrgDate(null);
								lObjTrnPensionRqstHdr.setDcrgOrderNo(null);
								lObjTrnPensionRqstHdr.setPaidDate(null);
								lObjTrnPensionRqstHdr.setTotalDcrgAmount(null);
								// lObjTrnPensionRqstHdr.setDcrgPaidFlag(dcrgPaidFlag);
								lObjTrnPensionRqstHdr.setDcrgPayingAuth(null);
								// lObjTrnPensionRqstHdr.setDcrgPayMode(lObjHstPnsnPmntDcrgDtls.getpa);
								lObjTrnPensionRqstHdr.setDcrgVoucherDate(null);
								lObjTrnPensionRqstHdr.setDcrgVoucherNo(null);
								lObjTrnPensionRqstHdr.setDcrgWithHeldAmnt(null);
								lObjTrnPensionRqstHdr.setDcrgAmntAfterWithHeld(null);
								lObjTrnPensionRqstHdr.setDcrgTotalRecoveryAmnt(null);
								lObjTrnPensionRqstHdr.setDcrgNameOfPayee(null);
								lObjTrnPensionRqstHdr.setDcrgTrsryForPayment(null);
								lObjPhysicalCaseInwardDAO.update(lObjTrnPensionRqstHdr);

							}

							// insert and delete data into
							// trn_pension_recovery_dtls (those rows for which
							// dcrg_dtls_hst_id is null)
							lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());
							lLstTrnPensionRecoveryDtlsForDCRGHstry = lObjPhysicalCaseInwardDAO.getTrnPensionRecoveryDtlsForDCRGHstry(lObjHstPnsnPmntDcrgDtlsVO.getPensionerCode());
							for (TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls : lLstTrnPensionRecoveryDtlsForDCRGHstry) {
								lObjPhysicalCaseInwardDAO.delete(lObjTrnPensionRecoveryDtls);
							}
							TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtlsVO = null;
							lStrNature = StringUtility.getParameter("txtDcrgNature", request);
							if(!"".equals(lStrNature))
								lArrStrNature = lStrNature.split("~");
							lStrAmount = StringUtility.getParameter("txtDcrgRecAmount", request);
							if(!"".equals(lStrAmount))
								lArrStrAmount = lStrAmount.split("~");
							lStrSchemeCode = StringUtility.getParameter("txtDcrgSchemeCode", request);
							if(!"".equals(lStrSchemeCode))
								lArrStrSchemeCode = lStrSchemeCode.split("~");
							lStrPensionerCode = lObjHstPnsnPmntDcrgDtlsVO.getPensionerCode();
							if(lArrStrNature != null)
							{
								if (lArrStrNature.length > 0) {
									for (Integer lIntCnt = 0; lIntCnt < lArrStrNature.length; lIntCnt++) {
										lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
										lObjTrnPensionRecoveryDtlsVO.setPensionerCode(lStrPensionerCode.trim());
										lObjTrnPensionRecoveryDtlsVO.setRecoveryFromFlag(gObjRsrcBndle.getString("PPMT.DCRGHISTORY"));
										lObjTrnPensionRecoveryDtlsVO.setRecoveryType(gObjRsrcBndle.getString("PPMT.DCRGRECOVERY"));
										lObjTrnPensionRecoveryDtlsVO.setAmount(new BigDecimal(lArrStrAmount[lIntCnt].trim()));
										lObjTrnPensionRecoveryDtlsVO.setNature(lArrStrNature[lIntCnt].trim());
										lObjTrnPensionRecoveryDtlsVO.setSchemeCode(lArrStrSchemeCode[lIntCnt].trim());
										lObjTrnPensionRecoveryDtlsVO.setDcrgDtlsId(lLngDcrgDtlsId);
										lObjTrnPensionRecoveryDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
										lObjTrnPensionRecoveryDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
										lObjTrnPensionRecoveryDtlsVO.setCreatedDate(gCurrDate);
										lLngTrnPensionRecoveryDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_recovery_dtls", inputMap);
										lObjTrnPensionRecoveryDtlsVO.setTrnPensionRecoveryDtlsId(lLngTrnPensionRecoveryDtlsId);
										lObjPhysicalCaseInwardDAO.create(lObjTrnPensionRecoveryDtlsVO);
									}
								}
							}
							/*
							 * for(TrnPensionRecoveryDtls
							 * lObjTrnPensionRecoveryDtls :
							 * lLstTrnPensionRecoveryDtlsForDCRGHstry) {
							 * lObjTrnPensionRecoveryDtls
							 * .setRecoveryFromFlag(gObjRsrcBndle
							 * .getString("PPMT.DCRGHISTORY"));
							 * lObjTrnPensionRecoveryDtls
							 * .setDcrgDtlsId(lLngDcrgDtlsId);
							 * lObjPhysicalCaseInwardDAO
							 * .update(lObjTrnPensionRecoveryDtls); }
							 */
						}
					}
				}
			}
			lStrBldXML = getResponseXMLDocForDCRGHistory(lStrMode);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}
		return objRes;
	}

	private StringBuilder getResponseXMLDocForDCRGHistory(String strMode) {

		StringBuilder lStrHidPKs = new StringBuilder();
		lStrHidPKs.append("<XMLDOC>");
		if (strMode != null && strMode.length() > 0) {
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append(strMode.trim());
			lStrHidPKs.append("</MESSAGECODE>");
		} else {
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append(strMode);
			lStrHidPKs.append("</MESSAGECODE>");
		}
		lStrHidPKs.append("</XMLDOC>");
		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#
	 * getBankBranchFromPaymentScheme(java.util.Map)
	 */
	public ResultObject getBankBranchFromPaymentScheme(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		StringBuilder lStrBldXML = null;
		RltBankBranch lObjRltBankBranch = new RltBankBranch();
		BigDecimal lBgDcmlDPRate = BigDecimal.ZERO;
		setSessionInfo(inputMap);
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
		try {

			lObjRltBankBranch = lObjPhysicalCaseInwardDAO.getBankBranchFrmPaymentScheme(gStrLocCode);
			lStrBldXML = new StringBuilder();
			if (lObjRltBankBranch != null) {
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<BRANCHCODE>" + lObjRltBankBranch.getBranchCode());
				lStrBldXML.append("</BRANCHCODE>");
				lStrBldXML.append("<BRANCHNAME>" + lObjRltBankBranch.getBranchName());
				lStrBldXML.append("</BRANCHNAME>");
				lStrBldXML.append("</XMLDOC>");
			}
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.service.PhysicalCaseInwardService#validatePensionArrearDtls(java.util.Map)
	 */
	public ResultObject validatePensionArrearDtls(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
		String lStrPayInMonth = "";
		String lStrPayInYear = "";
		String lStrArrearType = "";
		String lStrPayInMonthYear = "";
		String lStrPensionerCode = "";
		List<TrnPensionArrearDtls> lLstTrnPensionArrearDtls = new ArrayList<TrnPensionArrearDtls>();
		
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			lStrPensionerCode = StringUtility.getParameter("PensionerCode", request);
			lStrPayInMonth = StringUtility.getParameter("PayInMonth", request);
			lStrPayInYear = StringUtility.getParameter("PayInYear", request);
			lStrArrearType = StringUtility.getParameter("ArrearType", request);
			
			if (!"".equals(lStrPensionerCode) && !"".equals(lStrPayInMonth) && !"".equals(lStrPayInYear) && !"".equals(lStrArrearType)) {
				
				if(Integer.parseInt(lStrPayInMonth) < 10){
					lStrPayInMonthYear = lStrPayInYear + "0" + lStrPayInMonth;
				} else {
					lStrPayInMonthYear = lStrPayInYear + lStrPayInMonth;
				}
				
				lLstTrnPensionArrearDtls = lObjPhysicalCaseInwardDAO.validateArrearDtls(lStrPensionerCode, lStrArrearType, lStrPayInMonthYear);
			}
	
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ISEXISTS>");
			if (lLstTrnPensionArrearDtls.isEmpty()) {
				lStrBldXML.append("N");
			} else {
				lStrBldXML.append("Y");
			}
			lStrBldXML.append("</ISEXISTS>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	
	public ResultObject getSchemeCodeListFrmMajorHead(Map<String, Object> inputMap) {
		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrMajorHead = null;
		String lStrAjaxResult = null;
		List<MstScheme> lLstSchemeCode = new ArrayList<MstScheme>();
		Long lLngBankCode = null;
		StringBuilder lStrBldXML = null;
		try {
			setSessionInfo(inputMap);
			lStrMajorHead = StringUtility.getParameter("majorHead", request);
			
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			if (!"".equals(lStrMajorHead)) {
				lLstSchemeCode = lObjPhysicalCaseInwardDAO.getSchemeCodeListFrmMajorHead(lStrMajorHead, "R");
				lStrBldXML = new StringBuilder();
				if(!lLstSchemeCode.isEmpty()) {
					lStrBldXML.append("<XMLDOC>");
					for(MstScheme lObjMstScheme:lLstSchemeCode)
					{
						lStrBldXML.append("<SCHEMECODE>" + lObjMstScheme.getSchemeCode());
						lStrBldXML.append("</SCHEMECODE>");
						lStrBldXML.append("<SCHEMENAME><![CDATA[" + lObjMstScheme.getSchemeName());
						lStrBldXML.append("]]></SCHEMENAME>");
					}
					lStrBldXML.append("</XMLDOC>");
				}
				gLogger.info(" lStrBldXML :: " + lStrBldXML);
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	
	public ResultObject getSchemeNameFromSchemeCode(Map<String, Object> inputMap) {
		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrSchemeCode = null;
		String lStrAjaxResult = null;
		String lStrSchemeName ="";
		Long lLngBankCode = null;
		StringBuilder lStrBldXML = null;
		try {
			setSessionInfo(inputMap);
			lStrSchemeCode = StringUtility.getParameter("schemeCode", request);
			
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			if (!"".equals(lStrSchemeCode)) {
				lStrSchemeName = lObjPhysicalCaseInwardDAO.getSchemeNameFromSchemeCode(lStrSchemeCode);
				
				lStrBldXML = new StringBuilder();
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<SCHEMENAME><![CDATA[" + lStrSchemeName);
				lStrBldXML.append("]]></SCHEMENAME>");
				lStrBldXML.append("</XMLDOC>");
				
				gLogger.info(" lStrBldXML :: " + lStrBldXML);
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	
	public ResultObject validateMajorHead(Map<String, Object> inputMap) {
		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
		String lStrMajorHead = null;
		
		List lLstResult = null;
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			lStrMajorHead = StringUtility.getParameter("majorHead", request);
			
			if (!"".equals(lStrMajorHead)) {

				lLstResult = lObjPhysicalCaseInwardDAO.validateMajorHead(lStrMajorHead, "R");
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ISEXISTS>");
			if (lLstResult.isEmpty()) {
				lStrBldXML.append("N");
			} else {
				lStrBldXML.append("Y");
			}
			lStrBldXML.append("</ISEXISTS>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	
	public ResultObject loadUpdateBankBranchMigratedPpo(Map<String, Object> inputMap) {
		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<ComboValuesVO> lLstTreasury = null;
		List<Long> lLstTreasuryId = new ArrayList<Long>();
		List<ComboValuesVO> lLstBanks = null;
		try {
			setSessionInfo(inputMap);
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASURYID1")));
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPMT.TREASIRYID2")));
			
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstCommutationDtls.class, serv.getSessionFactory());
			lLstTreasury = lObjPhysicalCaseInwardDAO.getAllTreasury(lLstTreasuryId, gLngLangId);
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);
			
			inputMap.put("lLstTreasury",lLstTreasury);
			inputMap.put("lLstBanks",lLstBanks);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setViewName("UpdateBankBranchForMigratedPpo");
		return resObj;
	}

	
	public ResultObject validatePpoBeforeSetCaseOwner(Map<String, Object> inputMap) {
		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
		String lStrPpoNo = null;
		String lStrPensionerCode = "";
		List lLstBillDtls = null;
		List lLstPnsnrDtls = new ArrayList();
		String lStrBankCode = "";
		String lStrBranchCode = "";
		String lStrBranchName = "";
		String lStrLocationCode = "";
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			lStrPpoNo = StringUtility.getParameter("ppoNo", request);
			lStrLocationCode = StringUtility.getParameter("locationCode", request);
			
			if (!"".equals(lStrPpoNo)) {
				lStrPensionerCode = lObjPhysicalCaseInwardDAO.getPensionerCodeFromPpoNo(lStrPpoNo,lStrLocationCode, gObjRsrcBndle.getString("STATFLG.APPROVED"));
				if(lStrPensionerCode != null && !"".equals(lStrPensionerCode))
				{
					lLstBillDtls = lObjPhysicalCaseInwardDAO.getBillDtlsFromPensionerCode(lStrPensionerCode);
					if(lLstBillDtls == null || lLstBillDtls.isEmpty())
					{
						lLstPnsnrDtls = lObjPhysicalCaseInwardDAO.getPnsnrDtlsFromForUpdateBankDtls(lStrPensionerCode,lStrLocationCode, gObjRsrcBndle.getString("STATFLG.APPROVED"));
					}
				}
			}
			
			if(!lLstPnsnrDtls.isEmpty())
			{
				Object[] obj = (Object[]) lLstPnsnrDtls.get(0);
				if (obj[1] != null) {
					lStrBankCode = obj[1].toString();
				}
				if (obj[2] != null) {
					lStrBranchCode = obj[2].toString();
				}
				if (obj[3] != null) {
					lStrBranchName = obj[3].toString();
				}
				if (obj[4] != null) {
					lStrLocationCode = obj[4].toString();
				}
			}
			
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ISEXISTS>");
			if (lLstPnsnrDtls.isEmpty()) {
				lStrBldXML.append("N");
			} else {
				lStrBldXML.append("Y");
			}
			lStrBldXML.append("</ISEXISTS>");
			if (!lLstPnsnrDtls.isEmpty()) {
				lStrBldXML.append("<BANKCODE>" + lStrBankCode);
				lStrBldXML.append("</BANKCODE>");
				lStrBldXML.append("<BRANCHCODE>" + lStrBranchCode);
				lStrBldXML.append("</BRANCHCODE>");
				lStrBldXML.append("<BRANCHNAME><![CDATA[" + lStrBranchName);
				lStrBldXML.append("]]></BRANCHNAME>");
				lStrBldXML.append("<PENSIONERCODE>" + lStrPensionerCode);
				lStrBldXML.append("</PENSIONERCODE>");
			}
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	
	public ResultObject updateBankBranchForMigratedPpo(Map<String, Object> inputMap) {
		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lLstBillDtls = null;
		List lLstPnsnrDtls = new ArrayList();
		String lStrPpoNo = "";
		String lStrPensionerCode = "";
		String lStrBankCode = "";
		String lStrBranchCode = "";
		String lStrLocationCode = "";
		String lStrPrevBankCode = "";
		String lStrPrevBranchCode = "";
		BigDecimal lBgDcmlCaseOwner = BigDecimal.ZERO;
		MstPensionerDtls lObjMstPensionerDtls = new MstPensionerDtls();
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			setSessionInfo(inputMap);
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());

			lStrPpoNo = StringUtility.getParameter("ppoNo", request);
			lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			lStrBankCode = StringUtility.getParameter("bankCode", request);
			lStrBranchCode = StringUtility.getParameter("branchCode", request);
			lStrLocationCode = StringUtility.getParameter("locationCode", request);
			
			if (!"".equals(lStrPensionerCode) && !"".equals(lStrBankCode) && !"".equals(lStrBranchCode) && !"".equals(lStrLocationCode)) {
				lObjMstPensionerDtls = lObjPhysicalCaseInwardDAO.getMstPensionerDtlsVO(lStrPensionerCode);
				lStrPrevBankCode = lObjMstPensionerDtls.getBankCode();
				lStrPrevBranchCode = lObjMstPensionerDtls.getBranchCode();
				lObjMstPensionerDtls.setBankCode(lStrBankCode);
				lObjMstPensionerDtls.setBranchCode(lStrBranchCode);
				lObjMstPensionerDtls.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjMstPensionerDtls.setUpdatedUserId(new BigDecimal(gLngUserId));
				lObjMstPensionerDtls.setUpdatedDate(gCurrDate);
				lObjPhysicalCaseInwardDAO.update(lObjMstPensionerDtls);
				
				//lObjPhysicalCaseInwardDAO.updateBankBranchDtlsForMigratedPpo(lStrPensionerCode,lStrLocationCode,lStrBankCode,lStrBranchCode,
				//		new BigDecimal(gLngPostId),new BigDecimal(gLngUserId),gCurrDate);
				
				OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());
				List lLstResult = lObjOnlinePensionCaseDAO.getAuditorName(inputMap, lStrLocationCode, lStrBranchCode);
				if (lLstResult != null && !lLstResult.isEmpty() && lLstResult.get(0) != null) {
					Object[] obj = (Object[]) lLstResult.get(0);
					if (obj[0] != null) {
							lBgDcmlCaseOwner = new BigDecimal(obj[0].toString());
					}
				}
			
				lObjPhysicalCaseInwardDAO.updateCaseOwnerForMigratedPpo(lStrPensionerCode,lStrLocationCode,lBgDcmlCaseOwner,
						new BigDecimal(gLngPostId),new BigDecimal(gLngUserId),gCurrDate);
				
				lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionCaseMvmnt.class, serv.getSessionFactory());
				TrnPensionCaseMvmnt lObjTrnPensionCaseMvmntVO = new TrnPensionCaseMvmnt();
				Long lLngTrnPnsnCaseMvmntId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_case_mvmnt", inputMap);
				lObjTrnPensionCaseMvmntVO.setTrnPenionCaseMvmntId((lLngTrnPnsnCaseMvmntId));
				lObjTrnPensionCaseMvmntVO.setPpoNo(lStrPpoNo);
				lObjTrnPensionCaseMvmntVO.setPensionerCode(lStrPensionerCode);
				lObjTrnPensionCaseMvmntVO.setRemarks("Bank Branch details to be updated.Previous Details : Bank Code - "+lStrPrevBankCode +" Branch Code - " + lStrPrevBranchCode+" ,Current Details : Bank Code - "+lStrBankCode+" Branch Code - "+lStrBranchCode);
				lObjTrnPensionCaseMvmntVO.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjTrnPensionCaseMvmntVO.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjTrnPensionCaseMvmntVO.setCreatedDate(gCurrDate);
				lObjPhysicalCaseInwardDAO.create(lObjTrnPensionCaseMvmntVO);
				
			}
			
			
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>Updated Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

}
