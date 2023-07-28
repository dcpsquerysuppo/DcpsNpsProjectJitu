/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 1, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.BptmCommonServicesDAOImpl;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.AttachmentHelper;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.eis.dao.GradDesgScaleMapDAO;
import com.tcs.sgv.eis.valueobject.HrEisScaleMst;
import com.tcs.sgv.eis.valueobject.HrEisSgdMpg;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAO;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAOImpl;
import com.tcs.sgv.pensionproc.dao.CommonPensionDAO;
import com.tcs.sgv.pensionproc.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcPnsnrDtlsDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcPnsnrDtlsDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocFamilydtlsDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocFamilydtlsDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocNomineedtlsDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocNomineedtlsDAOImpl;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocPnsnrpayDAO;
import com.tcs.sgv.pensionproc.dao.TrnPnsnprocPnsnrpayDAOImpl;
import com.tcs.sgv.pensionproc.valueobject.SavedPensionCasesVO;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAdvnceBal;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAssesdDues;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcCheckList;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnrDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRecovery;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRevision;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAgDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAuthorityDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocAvgPayCalc;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocEventdtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocFamilydtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocForeignServ;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocNomineedtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrpay;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrservcbreak;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocProvisionalPaid;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 1, 2011
 */
public class PensionCaseServiceImpl extends ServiceImpl implements PensionCaseService {

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

	/* Global Variable for Current Date */
	Date gCurDate = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private Locale gLclLocale = null; /* LOCALE */
	private String gStrPostId = null; /* STRING POST ID */
	private String gStrUserId = null; /* STRING USER ID */
	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private HttpSession session = null; /* SESSION */
	private String gStrLocale = null; /* STRING LOCALE */
	private Date gDtCurDate = null; /* CURRENT DATE */

	private final static Logger gLogger = Logger.getLogger(PensionCaseServiceImpl.class);

	private static final Log logger = LogFactory.getLog(PensionCaseServiceImpl.class); /* LOGGER */

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionproc/PensionCaseConstants");

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gStrLocId = SessionHelper.getLocationId(inputMap).toString();

		} catch (Exception e) {
			logger.error("Error in setSessionInfo of PensionCaseServiceImpl ", e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.service.PensionCaseService#loadInwardPensionCase
	 * (java.util.Map)
	 */

	public ResultObject loadPensionCaseInwardForm(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {
			setSessionInfo(inputMap);
			List<CmnLookupMst> lLstTypeOfCase = null;
			List<CmnLookupMst> lLstPayCommission = null;
			List<CmnLookupMst> lLstClassOfPnsn = null;
			List<CmnLookupMst> lLstPensionerType = null;
			List<CmnLookupMst> lLstGroupList = null;
			List<CmnLookupMst> lLstEvents = null;
			List<CmnLookupMst> lLstPayScale = null;
			List<CmnLookupMst> lLstSrvcBrkType = null;
			//List<CmnLookupMst> lLstAdvnceBalType = null;
			List<CmnLookupMst> lLstAssesdTypeList = null;
			List<CmnLookupMst> lLstOfficeList = null;
			List<CmnLookupMst> lLstCertificate = null;
			List<CmnLookupMst> lLstRelation = null;
			List<ComboValuesVO> lLstState = null;
			List<ComboValuesVO> lLstDistricts = null;
			List<ComboValuesVO> lLstDepartment = null;
			List<ComboValuesVO> lLstHOOFrmDept = null;
			List<ComboValuesVO> lLstBankBranch = null;
			List<ComboValuesVO> lLstTreasury = null;
			List<ComboValuesVO> lLstBankNames = null;
			List<ComboValuesVO> lLstBanks = null;
			List<ComboValuesVO> lLstYears = null;
			List<ComboValuesVO> lLstMonths = null;
			List<Long> lLstTreasuryId = new ArrayList<Long>();
			//List lLstUpperUsers = null;
			//List lLstLowerUsers = null;
			List lLstDesignation = null;
			//String lStrRole = null;
			String lStrShowReadOnly = null;
			String lStrStatusLookupId = null;
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			CmnAttachmentMstDAO cmnAttachmentMstDAO = null;
			CmnAttachmentMst cmnAttachmentMst = null;
			Set<CmnAttachmentMpg> cmnAttachmentMpgs = null;
			Iterator<CmnAttachmentMpg> cmnAttachmentMpgIterator = null;
			CmnAttachmentMpg cmnAttachmentMpg = null;
			String lStrLocationCode = null;
			String lStrBankName = null;

			lLstRelation = IFMSCommonServiceImpl.getLookupValues("RelationShip List", gLngLangId, inputMap);

			lLstCertificate = IFMSCommonServiceImpl.getLookupValues("CertificateList", gLngLangId, inputMap);
			gLogger.info(" lstCertificate:" + lLstCertificate.size());

			lLstOfficeList = IFMSCommonServiceImpl.getLookupValues("OfficeList", gLngLangId, inputMap);
			gLogger.info("lstOfficeList :" + lLstOfficeList.size());

			lLstEvents = IFMSCommonServiceImpl.getLookupValues("EventList", gLngLangId, inputMap);
			gLogger.info("lstEvents :" + lLstEvents.size());

			lLstAssesdTypeList = IFMSCommonServiceImpl.getLookupValues("AssesdTypeList", gLngLangId, inputMap);
			gLogger.info("lstAssesdTypeList :" + lLstAssesdTypeList.size());

		/*	lLstAdvnceBalType = IFMSCommonServiceImpl.getLookupValues("AdvanceTypeList", gLngLangId, inputMap);
			gLogger.info("lstAdvnceBalType :" + lLstAdvnceBalType.size());*/

			lLstPayScale = IFMSCommonServiceImpl.getLookupValues("PayScale", gLngLangId, inputMap);
			gLogger.info("lLstPayScale :" + lLstPayScale.size());

			lLstSrvcBrkType = IFMSCommonServiceImpl.getLookupValues("SrvcBrkType", gLngLangId, inputMap);
			gLogger.info("lstSrvcBrkType :" + lLstSrvcBrkType.size());

			// lLstTypeOfCase = IFMSCommonServiceImpl.getLookupValues(
			// "Pension Case Type", gLngLangId, inputMap);
			// gLogger.info("lLstTypeOfCase :" + lLstTypeOfCase.size());

			// lLstPayCommission = IFMSCommonServiceImpl.getLookupValues(
			// "Pay Commission", gLngLangId, inputMap);
			// gLogger.info("lLstPayCommission :" + lLstPayCommission.size());

			lLstClassOfPnsn = IFMSCommonServiceImpl.getLookupValues("Class of Pension", gLngLangId, inputMap);
			gLogger.info("lLstClassOfPnsn :" + lLstClassOfPnsn.size());

			lLstPensionerType = IFMSCommonServiceImpl.getLookupValues("Class Type", gLngLangId, inputMap);

			lLstGroupList = IFMSCommonServiceImpl.getLookupValues("Group", gLngLangId, inputMap);
			gLogger.info("lLstGroupList :" + lLstGroupList.size());

			CommonPensionDAO lObjCmnPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			lLstState = lObjCmnPensionDAO.getAllState(gLngLangId);
			gLogger.info("lLstState :" + lLstState.size());

			lLstDistricts = lObjCmnPensionDAO.getDistrictsOfState(15L, gLngLangId);
			
			lLstDesignation = lObjCmnPensionDAO.getAllDesignation(gLngLangId);
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPROC.TREASURYID1")));
			lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPROC.TREASIRYID2")));
			lLstTreasury = lObjCmnPensionDAO.getAllTreasury(lLstTreasuryId, gLngLangId);

			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
			GradDesgScaleMapDAO gdDao = new GradDesgScaleMapDAO(HrEisSgdMpg.class, serv.getSessionFactory());

			lLstBankNames = lObjCmnPensionDAO.getBankNames(gLngLangId, gStrLocationCode);

			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);

			lLstMonths = lObjCommonDAO.getMonthList(SessionHelper.getLocale(request));
			lLstYears = lObjCommonDAO.getYearList(SessionHelper.getLocale(request));

			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lLstYears", lLstYears);

			
			Long isDdoOrDdoAsst = lObjTrnPnsnProcInwardPensionDAO.isDdoOrDdoAsst(gLngPostId);
			
			if(isDdoOrDdoAsst == 1L){
				inputMap.put("lStrRole", "700002");
			}else if(isDdoOrDdoAsst == 2L){
				inputMap.put("lStrRole", "700001");
			}
			
			//lStrRole = lObjTrnPnsnProcInwardPensionDAO.getRoleByPost(gLngPostId);

			List<HrEisScaleMst> scaleList = gdDao.getScalefromDsgnComm(2500340L);
			List<HrEisScaleMst> lLst6thPayScale = gdDao.getScalefromDsgnComm(2500341L);
			HrEisScaleMst hrEisScaleMst = null;
			long sgdId = 0L;

			/*String[] lStrArrRole = lStrRole.split(",");
			for (int lIntCnt = 0; lIntCnt < lStrArrRole.length; lIntCnt++) {
				if (gObjRsrcBndle.getString("PPROC.DEOROLE").equalsIgnoreCase(lStrArrRole[lIntCnt].trim())) {
					lStrRole = gObjRsrcBndle.getString("PPROC.DEOROLE");
				}

			}

			if (!gObjRsrcBndle.getString("PPROC.DDOROLE").equalsIgnoreCase(lStrRole)) {
				lLstUpperUsers = getHierarchyUsers(inputMap);
			}

			if (!gObjRsrcBndle.getString("PPROC.DEOROLE").equalsIgnoreCase(lStrRole)) {
				lLstLowerUsers = getLowerHierarchyUsers(inputMap);
			}
*/
			String lStrLegacyFlag = StringUtility.getParameter("legacyFlag", request);
			inputMap.put("LegacyFlag", lStrLegacyFlag);
			
			Date lDtCurDate = SessionHelper.getCurDate();

			inputMap.put("lLstRelation", lLstRelation);
			inputMap.put("lDtCurDate", lObjDateFormat.format(lDtCurDate));
			inputMap.put("lLstTypeOfCase", lLstTypeOfCase);
			inputMap.put("lLstPayCommission", lLstPayCommission);
			inputMap.put("lLstClassOfPnsn", lLstClassOfPnsn);
			inputMap.put("lLstPensionerType", lLstPensionerType);
			inputMap.put("lLstGroupList", lLstGroupList);
			inputMap.put("lLstState", lLstState);
			inputMap.put("lLstDistricts", lLstDistricts);
			inputMap.put("lLstTreasury", lLstTreasury);
			inputMap.put("lLstBankNames", lLstBankNames);
			

			inputMap.put("lLstDesignation", lLstDesignation);
			inputMap.put("lLstEvents", lLstEvents);
			inputMap.put("lLstPayScale", lLstPayScale);
			inputMap.put("lLstSrvcBrkType", lLstSrvcBrkType);
			//inputMap.put("lLstAdvnceBalType", lLstAdvnceBalType);
			inputMap.put("lLstAssesdTypeList", lLstAssesdTypeList);
			inputMap.put("lLstOfficeList", lLstOfficeList);
			inputMap.put("lLstCertificate", lLstCertificate);
			//inputMap.put("lLstUpperUsers", lLstUpperUsers);
			//inputMap.put("lLstLowerUsers", lLstLowerUsers);
			
			inputMap.put("lLstBanks", lLstBanks);
			gLogger.info("Load PensionCaseInwardForm Sucessfully");

			/****************************************** WHEN INWARD ID IS NOT NULL **************/

			if (StringUtility.getParameter("inwardId", request).length() > 0) {
				Long lLngInwardId = Long.valueOf(StringUtility.getParameter("inwardId", request).trim());
				TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = null;
				TrnPnsnProcPnsnrDtls lObjTrnPnsnProcPnsnrDtlsVO = null;
				TrnPnsnprocPnsnrpay lObjTrnPnsnprocPnsnrpayVO = null;
				TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalcVO = null;
				TrnPnsnProcRecovery lObjTrnPnsnProcRecoveryVO = null;
				TrnPnsnProcAdvnceBal lObjTrnPnsnProcAdvnceBalVO = null;
				TrnPnsnProcAssesdDues lObjTrnPnsnProcAssesdDuesVO = null;
				TrnPnsnProcCheckList lObjTrnPnsnProcCheckListVO = null;
				TrnPnsnProcRevision lObjTrnPnsnProcRevisionVO = null;
				TrnPnsnprocAgDtls lObjPnsnprocAgDtlsVO = null;
				List<TrnPnsnprocEventdtls> lLstTrnPnsnprocEventdtls = new ArrayList<TrnPnsnprocEventdtls>();
				List<TrnPnsnprocPnsnrservcbreak> lLstTrnPnsnprocPnsnrservcbreak = new ArrayList<TrnPnsnprocPnsnrservcbreak>();
				List<TrnPnsnprocAvgPayCalc> lLstTrnPnsnprocAvgPayCalcVO = new ArrayList<TrnPnsnprocAvgPayCalc>();
				List<TrnPnsnProcAdvnceBal> lLstTrnPnsnProcAdvnceBal = new ArrayList<TrnPnsnProcAdvnceBal>();
				List<TrnPnsnProcAssesdDues> lLstTrnPnsnProcAssesdDues = new ArrayList<TrnPnsnProcAssesdDues>();
				List<TrnPnsnProcCheckList> lLstTrnPnsnProcCheckList = new ArrayList<TrnPnsnProcCheckList>();
				List<TrnPnsnprocFamilydtls> lLstPnsnrFamilyDtls = new ArrayList<TrnPnsnprocFamilydtls>();
				List<TrnPnsnprocNomineedtls> lLstPnsnrNomineeDtls = new ArrayList<TrnPnsnprocNomineedtls>();
				List<TrnPnsnprocProvisionalPaid> lLstTrnPnsnprocProvisionalPaid = new ArrayList<TrnPnsnprocProvisionalPaid>();
				List<TrnPnsnprocForeignServ> lLstTrnPnsnprocForeignServ = new ArrayList<TrnPnsnprocForeignServ>();
				List<TrnPnsnprocAuthorityDtls> lLstTrnPnsnprocAuthorityDtls = new ArrayList<TrnPnsnprocAuthorityDtls>();
				Long lLngPhotoAttachmentId = 0l;
				Long lLngSignAttachmentId = 0l;
				List<ComboValuesVO> lLstPrDistricts = null;
				List<ComboValuesVO> lLstArDistricts = null;
				List<ComboValuesVO> lLstOffDistricts = null;

				lStrShowReadOnly = StringUtility.getParameter("showReadOnly", request);
				lStrStatusLookupId = StringUtility.getParameter("statusLookupId", request);
				
				lObjTrnPnsnProcInwardPensionVO = (TrnPnsnProcInwardPension) lObjTrnPnsnProcInwardPensionDAO.read(lLngInwardId);
				
				if("".equals(lStrStatusLookupId)){
					lStrStatusLookupId = lObjTrnPnsnProcInwardPensionVO.getCaseStatus();
				}
				
				inputMap.put("lObjTrnPnsnProcInwardPensionVO", lObjTrnPnsnProcInwardPensionVO);
				if (lObjTrnPnsnProcInwardPensionVO.getPayCommission().equals("5THPAYCOMSN")) {
					scaleList = gdDao.getScalefromDsgnComm(2500340L);
				} else {
					scaleList = gdDao.getScalefromDsgnComm(2500341L);
				}

				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcPnsnrDtls.class, serv.getSessionFactory());
				lObjTrnPnsnProcPnsnrDtlsVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrDtlsVO(lLngInwardId);
				inputMap.put("lObjTrnPnsnProcPnsnrDtlsVO", lObjTrnPnsnProcPnsnrDtlsVO);

				if (lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrStateCode() != null) {
					lLstPrDistricts = lObjCmnPensionDAO.getDistrictsOfState(lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrStateCode(), gLngLangId);
				}
				if (lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrRetStateCode() != null) {
					lLstArDistricts = lObjCmnPensionDAO.getDistrictsOfState(lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrRetStateCode(), gLngLangId);
				}
				if (lObjTrnPnsnProcPnsnrDtlsVO.getOfficeStateCode() != null) {
					lLstOffDistricts = lObjCmnPensionDAO.getDistrictsOfState(lObjTrnPnsnProcPnsnrDtlsVO.getOfficeStateCode(), gLngLangId);
				}

				// get photo and signature dtls
				if (lObjTrnPnsnProcPnsnrDtlsVO != null) {
					Long lLngSrNo = 0L;
					CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
					CmnAttachmentMst lObjCmnAttachmentMst = null;
					if (lObjTrnPnsnProcPnsnrDtlsVO.getPhotoAttachmentId() != null && lObjTrnPnsnProcPnsnrDtlsVO.getPhotoAttachmentId().doubleValue() > 0) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lObjTrnPnsnProcPnsnrDtlsVO.getPhotoAttachmentId().toString()));

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

					if (lObjTrnPnsnProcPnsnrDtlsVO.getSignatureAttachmentId() != null && lObjTrnPnsnProcPnsnrDtlsVO.getSignatureAttachmentId().doubleValue() > 0) {
						lObjCmnAttachmentMst = new CmnAttachmentMst();
						lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lObjTrnPnsnProcPnsnrDtlsVO.getSignatureAttachmentId().toString()));

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

				// read vo of family details

				TrnPnsnprocFamilydtlsDAO lObjTrnPnsnprocFamilydtlsDAO = new TrnPnsnprocFamilydtlsDAOImpl(TrnPnsnprocFamilydtls.class, serv.getSessionFactory());
				lLstPnsnrFamilyDtls = lObjTrnPnsnprocFamilydtlsDAO.getListOfPnsnrFamilyDtls(lLngInwardId);
				inputMap.put("lLstPnsnrFamilyDtls", lLstPnsnrFamilyDtls);

				// read vo of nominee details
				TrnPnsnprocNomineedtlsDAO lObjTrnPnsnprocNomineedtlsDAO = new TrnPnsnprocNomineedtlsDAOImpl(TrnPnsnprocNomineedtls.class, serv.getSessionFactory());
				lLstPnsnrNomineeDtls = lObjTrnPnsnprocNomineedtlsDAO.getListOfPnsnrNomineeDtls(lLngInwardId);
				inputMap.put("lLstPnsnrNomineeDtls", lLstPnsnrNomineeDtls);

				lObjTrnPnsnprocPnsnrpayVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrPayVO(lLngInwardId);

				inputMap.put("lObjTrnPnsnprocPnsnrpayVO", lObjTrnPnsnprocPnsnrpayVO);

				lLstTrnPnsnprocAvgPayCalcVO = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAvgPayCalcDtls(lLngInwardId);
				inputMap.put("lLstTrnPnsnprocAvgPayCalcVO", lLstTrnPnsnprocAvgPayCalcVO);

				lLstTrnPnsnprocEventdtls = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseEventDtls(lLngInwardId);
				inputMap.put("lLstTrnPnsnprocEventdtls", lLstTrnPnsnprocEventdtls);

				lLstTrnPnsnprocProvisionalPaid = lObjTrnPnsnProcInwardPensionDAO.getPnsnprocProvisionalPaidDtls(lLngInwardId);
				inputMap.put("lLstTrnPnsnprocProvisionalPaid", lLstTrnPnsnprocProvisionalPaid);
				
				lLstTrnPnsnprocForeignServ = lObjTrnPnsnProcInwardPensionDAO.getForeignServDtls(lLngInwardId);
				inputMap.put("lLstTrnPnsnprocForeignServ", lLstTrnPnsnprocForeignServ);

				lLstTrnPnsnprocPnsnrservcbreak = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseSrvcBrkDtls(lLngInwardId);
				inputMap.put("lLstTrnPnsnprocPnsnrservcbreak", lLstTrnPnsnprocPnsnrservcbreak);

				lObjTrnPnsnProcPnsnCalcVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnCalcVO(lLngInwardId);
				inputMap.put("lObjTrnPnsnProcPnsnCalcVO", lObjTrnPnsnProcPnsnCalcVO);

				lObjTrnPnsnProcRecoveryVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrRecoveryVO(lLngInwardId);
				inputMap.put("lObjTrnPnsnProcRecoveryVO", lObjTrnPnsnProcRecoveryVO);
				
				lLstTrnPnsnprocAuthorityDtls = lObjTrnPnsnProcInwardPensionDAO.getAuthorityDtls(lLngInwardId);
				inputMap.put("lLstTrnPnsnprocAuthorityDtls", lLstTrnPnsnprocAuthorityDtls);
				
				lObjPnsnprocAgDtlsVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnAgDtls(lLngInwardId);
				inputMap.put("lObjPnsnprocAgDtlsVO", lObjPnsnprocAgDtlsVO);
				
				Long lLngRevisionId = lObjTrnPnsnProcInwardPensionDAO.getPnsnRevisionId(lObjTrnPnsnProcInwardPensionVO.getSevaarthId());				
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcRevision.class, serv.getSessionFactory());
				lObjTrnPnsnProcRevisionVO = (TrnPnsnProcRevision) lObjTrnPnsnProcInwardPensionDAO.read(lLngRevisionId);
				inputMap.put("lObjTrnPnsnProcRevisionVO", lObjTrnPnsnProcRevisionVO);
				
				lLstTrnPnsnProcAdvnceBal = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAdvnceBalDtls(lLngInwardId);
				Iterator<TrnPnsnProcAdvnceBal> it1 = lLstTrnPnsnProcAdvnceBal.iterator();
				for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnProcAdvnceBal.size(); lIntCnt++) {
					lObjTrnPnsnProcAdvnceBalVO = it1.next();
					inputMap.put("lObjTrnPnsnProcAdvnceBalVO", lObjTrnPnsnProcAdvnceBalVO);
				}
				inputMap.put("lLstTrnPnsnProcAdvnceBal", lLstTrnPnsnProcAdvnceBal);

				lLstTrnPnsnProcAssesdDues = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAssesdDueDtls(lLngInwardId);
				Iterator<TrnPnsnProcAssesdDues> it2 = lLstTrnPnsnProcAssesdDues.iterator();
				for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnProcAssesdDues.size(); lIntCnt++) {
					lObjTrnPnsnProcAssesdDuesVO = it2.next();
					inputMap.put("lObjTrnPnsnProcAssesdDuesVO", lObjTrnPnsnProcAssesdDuesVO);
				}
				inputMap.put("lLstTrnPnsnProcAssesdDues", lLstTrnPnsnProcAssesdDues);

				lLstTrnPnsnProcCheckList = lObjTrnPnsnProcInwardPensionDAO.getPnsnrCheklistVO(lLngInwardId);
				Iterator<TrnPnsnProcCheckList> it3 = lLstTrnPnsnProcCheckList.iterator();
				for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnProcCheckList.size(); lIntCnt++) {
					lObjTrnPnsnProcCheckListVO = it3.next();
					inputMap.put("lObjTrnPnsnProcCheckListVO", lObjTrnPnsnProcCheckListVO);
				}
				if (lObjTrnPnsnProcCheckListVO != null) {
					cmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
					if (lObjTrnPnsnProcCheckListVO.getCertificateAttachId() != null && lObjTrnPnsnProcCheckListVO.getCertificateAttachId().doubleValue() > 0) {
						cmnAttachmentMst = new CmnAttachmentMst();
						cmnAttachmentMst = cmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lObjTrnPnsnProcCheckListVO.getCertificateAttachId().toString()));
						inputMap.put("scan", cmnAttachmentMst);
					}
				}
				inputMap.put("lLstTrnPnsnProcCheckList", lLstTrnPnsnProcCheckList);
				if (lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.APPROVEDBYDDOSTATUSID")) 
						|| lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.SENDTOAGSTATUSID"))
						//|| lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.MOVEFORDDOMISTAKESTATUSID")) 
						//|| lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.MOVEFORAGQUERYSTATUSID"))
						|| lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.APPROVEDBYAGSTATUSID"))) {
					lStrShowReadOnly = "Y";
					inputMap.put("lStrStatusLookupId", lStrStatusLookupId);
				}
				if (lStrShowReadOnly.equals("Y")) {
					inputMap.put("lStrShowReadOnly", lStrShowReadOnly);
				}
				lLstHOOFrmDept = lObjCmnPensionDAO.getAdminDept();
				
				Long lLngAdminDept =  lObjCmnPensionDAO.getAdminDeptNameFromFieldDept(lObjTrnPnsnProcPnsnrDtlsVO.getDepartmentId());
				
				lLstDepartment = lObjCmnPensionDAO.getFieldDeptFromAdmDept(lLngAdminDept);
				inputMap.put("lLstDepartment", lLstDepartment);
				
				lStrBankName = lObjTrnPnsnProcPnsnrDtlsVO.getBankName();

				lStrLocationCode = lObjTrnPnsnProcInwardPensionDAO.getLocCodeFromDDO(SessionHelper.getLocationCode(inputMap));
				if (lStrBankName != null) {
					lLstBankBranch = lObjCommonDAO.getBranchListFromBankCode(Long.parseLong(lStrBankName), lStrLocationCode, gLngLangId);
				}
				inputMap.put("lLstBankBranch", lLstBankBranch);
				inputMap.put("lLstHOOFrmDept", lLstHOOFrmDept);
				inputMap.put("lLstPrDistricts", lLstPrDistricts);
				inputMap.put("lLstArDistricts", lLstArDistricts);
				inputMap.put("lLstOffDistricts", lLstOffDistricts);

				resObj.setViewName("HeaderMenuPopup");

			} else {
				resObj.setViewName("pensionCaseInwardForm");
			}
			ComboValuesVO lObjComboValuesVO = null;
			List lLstReturnList = null;
			if ((scaleList != null) && (scaleList.size() > 0)) {
				lLstReturnList = new ArrayList();

				for (int k = 0; k < scaleList.size(); ++k) {
					hrEisScaleMst = (HrEisScaleMst) scaleList.get(k);

					if (hrEisScaleMst != null) {
						sgdId = hrEisScaleMst.getScaleId();

						StringBuffer scaleDisp = new StringBuffer("");

						if ((hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500341L) || (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500342L)
								|| (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500343L) || (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500344L)
								|| (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500345L)) {
							scaleDisp.append(hrEisScaleMst.getScaleStartAmt());

							scaleDisp.append("-");
							scaleDisp.append(hrEisScaleMst.getScaleEndAmt());
							scaleDisp.append(" (");
							scaleDisp.append(hrEisScaleMst.getScaleGradePay());
							scaleDisp.append(")");
						} else {
							scaleDisp.append(hrEisScaleMst.getScaleStartAmt());
							scaleDisp.append("-");
							scaleDisp.append(hrEisScaleMst.getScaleIncrAmt());
							scaleDisp.append("-");
							scaleDisp.append(hrEisScaleMst.getScaleEndAmt());
							if ((hrEisScaleMst.getScaleHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleHigherEndAmt() > 0L)) {
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleHigherIncrAmt());
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleHigherEndAmt());
								if ((hrEisScaleMst.getScaleSecondHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleSecondHigherEndAmt() > 0L)) {
									scaleDisp.append("-");
									scaleDisp.append(hrEisScaleMst.getScaleSecondHigherIncrAmt());
									scaleDisp.append("-");
									scaleDisp.append(hrEisScaleMst.getScaleSecondHigherEndAmt());

									if ((hrEisScaleMst.getScaleThirdHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleThirdHigherEndAmt() > 0L)) {
										scaleDisp.append("-");
										scaleDisp.append(hrEisScaleMst.getScaleThirdHigherIncrAmt());
										scaleDisp.append("-");
										scaleDisp.append(hrEisScaleMst.getScaleThirdHigherEndAmt());
									}
								}
							}
						}
						lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(Long.valueOf(hrEisScaleMst.getScaleId()).toString());
						lObjComboValuesVO.setDesc(scaleDisp.toString());
						lLstReturnList.add(lObjComboValuesVO);
					}
				}

				inputMap.put("PayScaleList", lLstReturnList);
			}
			if ((lLst6thPayScale != null) && (lLst6thPayScale.size() > 0)) {
				lLstReturnList = new ArrayList();

				for (int k = 0; k < lLst6thPayScale.size(); ++k) {
					hrEisScaleMst = (HrEisScaleMst) lLst6thPayScale.get(k);

					if (hrEisScaleMst != null) {
						sgdId = hrEisScaleMst.getScaleId();

						StringBuffer scaleDisp = new StringBuffer("");

						if ((hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500341L) || (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500342L)
								|| (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500343L) || (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500344L)
								|| (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500345L)) {
							scaleDisp.append(hrEisScaleMst.getScaleStartAmt());

							scaleDisp.append("-");
							scaleDisp.append(hrEisScaleMst.getScaleEndAmt());
							scaleDisp.append(" (");
							scaleDisp.append(hrEisScaleMst.getScaleGradePay());
							scaleDisp.append(")");
						} else {
							scaleDisp.append(hrEisScaleMst.getScaleStartAmt());
							scaleDisp.append("-");
							scaleDisp.append(hrEisScaleMst.getScaleIncrAmt());
							scaleDisp.append("-");
							scaleDisp.append(hrEisScaleMst.getScaleEndAmt());
							if ((hrEisScaleMst.getScaleHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleHigherEndAmt() > 0L)) {
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleHigherIncrAmt());
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleHigherEndAmt());
								if ((hrEisScaleMst.getScaleSecondHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleSecondHigherEndAmt() > 0L)) {
									scaleDisp.append("-");
									scaleDisp.append(hrEisScaleMst.getScaleSecondHigherIncrAmt());
									scaleDisp.append("-");
									scaleDisp.append(hrEisScaleMst.getScaleSecondHigherEndAmt());

									if ((hrEisScaleMst.getScaleThirdHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleThirdHigherEndAmt() > 0L)) {
										scaleDisp.append("-");
										scaleDisp.append(hrEisScaleMst.getScaleThirdHigherIncrAmt());
										scaleDisp.append("-");
										scaleDisp.append(hrEisScaleMst.getScaleThirdHigherEndAmt());
									}
								}
							}
						}
						lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(Long.valueOf(hrEisScaleMst.getScaleId()).toString());
						lObjComboValuesVO.setDesc(scaleDisp.toString());
						lLstReturnList.add(lObjComboValuesVO);
					}
				}

				inputMap.put("PayScaleList6th", lLstReturnList);
			}
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
		}

		catch (Exception e) {

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
	 * com.tcs.sgv.pensionproc.service.PensionCaseService#insertPensionCase(
	 * java.util.Map)
	 */
	
	public ResultObject insertPensionCase(Map inputMap) throws Exception {

		Long lLngInwardPensionId = 0l;
		Long lLngPensionerDtlId = 0l;
		Long lLngPensionerPayId = 0l;
		Long lLngPensionCalcId = 0l;
		Long lLngpnsnrRecoveryId = 0l;
		Long lLngpnsnrRevisionId = 0l;
		Long lLngPnsnrEventdtlId = 0l;
		Long lLngPnsnrProvisionalId = 0l;
		Long lLngPnsnrForeignServ = 0l;
		Long lLngadvanceBalId = 0l;
		Long lLngpnsnsrCheckListId = 0l;
		Long lLngassesdDuesDtlId = 0l;
		Long lLngPnsnrServcBreakId = 0l;
		Long lLngAvgPayCalcId = 0l;
		Long lLngPnsnrFamilyId = 0l;
		Long lLngPnsnrNomineeId = 0l;
		Long lLngCertAttachId = 0l;
		Long lLngPnsnsrAuthoId = 0l;
		Long lLngpnsnrAgId = 0l;
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = new TrnPnsnProcInwardPension();
		TrnPnsnProcPnsnrDtls lObjTrnPnsnProcPnsnrDtlsVO = new TrnPnsnProcPnsnrDtls();
		TrnPnsnprocPnsnrpay lObjTrnPnsnprocPnsnrpayVO = new TrnPnsnprocPnsnrpay();
		TrnPnsnprocEventdtls lObjTrnPnsnprocEventdtlsVO = new TrnPnsnprocEventdtls();
		TrnPnsnprocPnsnrservcbreak lObjTrnPnsnprocPnsnrservcbreakVO = new TrnPnsnprocPnsnrservcbreak();
		TrnPnsnprocAvgPayCalc lObjTrnPnsnprocAvgPayCalcVO = new TrnPnsnprocAvgPayCalc();
		TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalcVO = new TrnPnsnProcPnsnCalc();
		TrnPnsnprocFamilydtls lObjTrnPnsnprocFamilydtlsVO = new TrnPnsnprocFamilydtls();
		TrnPnsnprocNomineedtls lObjTrnPnsnprocNomineedtlsVO = new TrnPnsnprocNomineedtls();
		TrnPnsnProcRecovery lObjTrnPnsnProcRecoveryVO = new TrnPnsnProcRecovery();
		TrnPnsnProcAdvnceBal lObjTrnPnsnProcAdvnceBalVO = new TrnPnsnProcAdvnceBal();
		TrnPnsnProcAssesdDues lObjTrnPnsnProcAssesdDuesVO = new TrnPnsnProcAssesdDues();
		TrnPnsnProcCheckList lObjTrnPnsnProcCheckListVO = new TrnPnsnProcCheckList();
		TrnPnsnprocProvisionalPaid lObjPnsnprocProvisionalPaidVO = new TrnPnsnprocProvisionalPaid();
		TrnPnsnprocForeignServ lObjTrnPnsnprocForeignServVO = new TrnPnsnprocForeignServ();
		TrnPnsnProcRevision lObjTrnPnsnProcRevisionVO = new TrnPnsnProcRevision();
		TrnPnsnprocAuthorityDtls lObjAuthorityDtlsVO = new TrnPnsnprocAuthorityDtls();
		TrnPnsnprocAgDtls lObjPnsnprocAgDtlsVO = new TrnPnsnprocAgDtls();
		List<TrnPnsnprocEventdtls> lLstTrnPnsnprocEventdtlsVO = new ArrayList<TrnPnsnprocEventdtls>();
		List<TrnPnsnprocPnsnrservcbreak> lLstTrnPnsnprocPnsnrservcbreakVO = new ArrayList<TrnPnsnprocPnsnrservcbreak>();
		List<TrnPnsnprocAvgPayCalc> lLstTrnPnsnprocAvgPayCalcVO = new ArrayList<TrnPnsnprocAvgPayCalc>();
		List<TrnPnsnProcAdvnceBal> lLstTrnPnsnProcAdvnceBalVO = new ArrayList<TrnPnsnProcAdvnceBal>();
		List<TrnPnsnProcAssesdDues> lLstTrnPnsnProcAssesdDuesVO = new ArrayList<TrnPnsnProcAssesdDues>();
		List<TrnPnsnprocFamilydtls> lLstTrnPnsnprocFamilydtlsVO = new ArrayList<TrnPnsnprocFamilydtls>();
		List<TrnPnsnprocNomineedtls> lLstTrnPnsnprocNomineedtlsVO = new ArrayList<TrnPnsnprocNomineedtls>();
		List<TrnPnsnProcCheckList> lLstTrnPnsnProcCheckListVO = new ArrayList<TrnPnsnProcCheckList>();
		List<TrnPnsnprocProvisionalPaid> lLstTrnPnsnprocProvisionalPaidVO = new ArrayList<TrnPnsnprocProvisionalPaid>();
		List<TrnPnsnprocForeignServ> lLstTrnPnsnprocForeignServ = new ArrayList<TrnPnsnprocForeignServ>();
		List<TrnPnsnprocAuthorityDtls> lLstTrnPnsnprocAuthorityDtls = new ArrayList<TrnPnsnprocAuthorityDtls>();
		Map attachMap = null;
		StringBuilder lStrBldXML = null;

		try {
			setSessionInfo(inputMap);
			String strTransMode = (String) inputMap.get("Mode");
			lObjTrnPnsnProcInwardPensionVO = (TrnPnsnProcInwardPension) inputMap.get("lObjTrnPnsnProcInwardPensionVO");
			lObjTrnPnsnProcPnsnrDtlsVO = (TrnPnsnProcPnsnrDtls) inputMap.get("lObjTrnPnsnProcPnsnrDtlsVO");
			lObjTrnPnsnprocPnsnrpayVO = (TrnPnsnprocPnsnrpay) inputMap.get("lObjTrnPnsnprocPnsnrpayVO");
			lLstTrnPnsnprocEventdtlsVO = (List<TrnPnsnprocEventdtls>) inputMap.get("lLstTrnPnsnprocEventdtlsVO");
			lLstTrnPnsnprocPnsnrservcbreakVO = (List<TrnPnsnprocPnsnrservcbreak>) inputMap.get("lLstTrnPnsnprocPnsnrServcBreakVO");
			lLstTrnPnsnprocAvgPayCalcVO = (List<TrnPnsnprocAvgPayCalc>) inputMap.get("lLstTrnPnsnprocAvgPayCalcVO");
			lObjTrnPnsnProcPnsnCalcVO = (TrnPnsnProcPnsnCalc) inputMap.get("lObjTrnPnsnProcPnsnCalcVO");
			lLstTrnPnsnprocFamilydtlsVO = (List<TrnPnsnprocFamilydtls>) inputMap.get("lLstTrnPnsnprocFamilydtlsVO");
			lLstTrnPnsnprocNomineedtlsVO = (List<TrnPnsnprocNomineedtls>) inputMap.get("lLstTrnPnsnprocNomineedtlsVO");
			lObjTrnPnsnProcRecoveryVO = (TrnPnsnProcRecovery) inputMap.get("lObjTrnPnsnProcRecoveryVO");
			lLstTrnPnsnProcAdvnceBalVO = (List<TrnPnsnProcAdvnceBal>) inputMap.get("lLstTrnPnsnProcAdvnceBalVO");
			lLstTrnPnsnProcAssesdDuesVO = (List<TrnPnsnProcAssesdDues>) inputMap.get("lLstTrnPnsnProcAssesdDuesVO");
			lLstTrnPnsnProcCheckListVO = (List<TrnPnsnProcCheckList>) inputMap.get("lLstTrnPnsnProcCheckListVO");
			lLstTrnPnsnprocProvisionalPaidVO = (List<TrnPnsnprocProvisionalPaid>) inputMap.get("lLstTrnPnsnprocProvisionalPaidVO");
			lLstTrnPnsnprocForeignServ = (List<TrnPnsnprocForeignServ>) inputMap.get("lLstTrnPnsnprocForeignServ");
			lObjTrnPnsnProcRevisionVO = (TrnPnsnProcRevision) inputMap.get("lObjTrnPnsnProcRevisionVO");
			lLstTrnPnsnprocAuthorityDtls = (List<TrnPnsnprocAuthorityDtls>) inputMap.get("lLstTrnPnsnprocAuthorityDtls");
			lObjPnsnprocAgDtlsVO =  (TrnPnsnprocAgDtls) inputMap.get("lObjPnsnprocAgDtlsVO");

			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());

			if (strTransMode.equalsIgnoreCase("Update")) {
				// update inward pension vo
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());

				lObjTrnPnsnProcInwardPensionDAO.update(lObjTrnPnsnProcInwardPensionVO);
				lLngInwardPensionId = lObjTrnPnsnProcInwardPensionVO.getInwardPensionId();
				// update pensioner detail vo
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcPnsnrDtls.class, serv.getSessionFactory());

				objRes = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

				objRes = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

				attachMap = (Map) objRes.getResultValue();
				Long lLngAttachId = 0L;
				if (attachMap.get("AttachmentId_Photo") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Photo")));
					lObjTrnPnsnProcPnsnrDtlsVO.setPhotoAttachmentId(lLngAttachId);
				}

				if (attachMap.get("AttachmentId_Sign") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Sign")));
					lObjTrnPnsnProcPnsnrDtlsVO.setSignatureAttachmentId(lLngAttachId);
				}
				lObjTrnPnsnProcInwardPensionDAO.update(lObjTrnPnsnProcPnsnrDtlsVO);
				lLngPensionerDtlId = lObjTrnPnsnProcPnsnrDtlsVO.getPensionerDtlId();
				// update pensioner pay vo
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocPnsnrpay.class, serv.getSessionFactory());
				lObjTrnPnsnProcInwardPensionDAO.update(lObjTrnPnsnprocPnsnrpayVO);

				// update pension calculation vo
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcPnsnCalc.class, serv.getSessionFactory());
				lObjTrnPnsnProcInwardPensionDAO.update(lObjTrnPnsnProcPnsnCalcVO);

				// update pension recovery vo
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcRecovery.class, serv.getSessionFactory());
				lObjTrnPnsnProcInwardPensionDAO.update(lObjTrnPnsnProcRecoveryVO);
				
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocAgDtls.class, serv.getSessionFactory());
				lObjTrnPnsnProcInwardPensionDAO.update(lObjPnsnprocAgDtlsVO);
				
				// update pension Revision vo
				Long lLngRevisionId = (Long) inputMap.get("lLngRevisionId");
				if(lObjTrnPnsnProcInwardPensionVO.getCaseType().equals("REVISION")){
					if(lLngRevisionId != null && lLngRevisionId != 0l){
						lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcRevision.class, serv.getSessionFactory());				
						lObjTrnPnsnProcInwardPensionDAO.update(lObjTrnPnsnProcRevisionVO);
					}else{
						if (lObjTrnPnsnProcRevisionVO != null) {
							lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcRevision.class, serv.getSessionFactory());
							lLngpnsnrRevisionId = IFMSCommonServiceImpl.getNextSeqNum("TRN_PNSNPROC_REVISION", inputMap);
							lObjTrnPnsnProcRevisionVO.setRevisionId(lLngpnsnrRevisionId);
							lObjTrnPnsnProcRevisionVO.setInwardPensionId(lLngInwardPensionId);					
							lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnProcRevisionVO);
						}
					}
				}				
				gLogger.info(" .... Record Updated Successfully .... ");
				inputMap.put("lObjTrnPnsnProcInwardPensionVO", lObjTrnPnsnProcInwardPensionVO);

				lStrBldXML = getResponseXMLDoc(inputMap, "UPDATE");

			} else if (strTransMode.equalsIgnoreCase("Add")) {
				String lStrCnt = IDGenerateDelegate.getNextIdWODbidLocationId("Pension_inward_no", inputMap);
				Date dt = new Date();
				Long lLngCnt = Long.parseLong(lStrCnt);

				Integer igetMonth = dt.getMonth();
				igetMonth = igetMonth + 1;

				Integer igetYear = dt.getYear();
				igetYear = igetYear + 1900;
				String lStrInwardNo = "PNSN" + "/" + igetMonth + "/" + igetYear + "/" + lLngCnt;

				if (lObjTrnPnsnProcInwardPensionVO != null) {

					lLngInwardPensionId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_inwardpension", inputMap);

					lObjTrnPnsnProcInwardPensionVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnProcInwardPensionVO.setInwardNo(lStrInwardNo);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnProcInwardPensionVO);
				}

				objRes = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

				objRes = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

				attachMap = (Map) objRes.getResultValue();
				Long lLngAttachId = 0L;
				if (attachMap.get("AttachmentId_Photo") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Photo")));
					lObjTrnPnsnProcPnsnrDtlsVO.setPhotoAttachmentId(lLngAttachId);
				}

				if (attachMap.get("AttachmentId_Sign") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Sign")));
					lObjTrnPnsnProcPnsnrDtlsVO.setSignatureAttachmentId(lLngAttachId);
				}

				if (lObjTrnPnsnProcPnsnrDtlsVO != null) {
					lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcPnsnrDtls.class, serv.getSessionFactory());
					lLngPensionerDtlId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_pnsnrdtls", inputMap);

					lObjTrnPnsnProcPnsnrDtlsVO.setPensionerDtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcPnsnrDtlsVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnProcPnsnrDtlsVO);
				}

				if (lObjTrnPnsnprocPnsnrpayVO != null) {
					lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocPnsnrpay.class, serv.getSessionFactory());
					lLngPensionerPayId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_pnsnrpay", inputMap);
					lObjTrnPnsnprocPnsnrpayVO.setPensionerPayId(lLngPensionerPayId);
					lObjTrnPnsnprocPnsnrpayVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnprocPnsnrpayVO.setPensionerdtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnprocPnsnrpayVO);
				}

				if (lObjTrnPnsnProcPnsnCalcVO != null) {
					lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcPnsnCalc.class, serv.getSessionFactory());
					lLngPensionCalcId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_pnsncalc", inputMap);
					lObjTrnPnsnProcPnsnCalcVO.setPensionCalcId(lLngPensionCalcId);
					lObjTrnPnsnProcPnsnCalcVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnProcPnsnCalcVO.setPensionerdtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnProcPnsnCalcVO);
				}

				if (lObjTrnPnsnProcRecoveryVO != null) {
					lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcRecovery.class, serv.getSessionFactory());
					lLngpnsnrRecoveryId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_recovery", inputMap);
					lObjTrnPnsnProcRecoveryVO.setPnsnrRecoveryId(lLngpnsnrRecoveryId);
					lObjTrnPnsnProcRecoveryVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnProcRecoveryVO.setPensionerdtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnProcRecoveryVO);
				}
				if (lObjPnsnprocAgDtlsVO != null) {
					lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocAgDtls.class, serv.getSessionFactory());
					lLngpnsnrAgId = IFMSCommonServiceImpl.getNextSeqNum("TRN_PNSNPROC_AG_DTLS", inputMap);
					lObjPnsnprocAgDtlsVO.setAgDtlsId(lLngpnsnrAgId);
					lObjPnsnprocAgDtlsVO.setInwardPensionId(lLngInwardPensionId);					
					lObjPnsnprocAgDtlsVO.setLocationCode(gStrLocationCode);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjPnsnprocAgDtlsVO);
				}
				
				lStrBldXML = getResponseXMLDoc(inputMap, "Add");

			}
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocEventdtls.class, serv.getSessionFactory());
			if (lLstTrnPnsnprocEventdtlsVO != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPnsnprocEventdtlsVO.size(); lIntCount++) {
					lObjTrnPnsnprocEventdtlsVO = lLstTrnPnsnprocEventdtlsVO.get(lIntCount);
					lLngPnsnrEventdtlId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_eventdtls", inputMap);
					lObjTrnPnsnprocEventdtlsVO.setPnsnrEventdtlId(lLngPnsnrEventdtlId);
					lObjTrnPnsnprocEventdtlsVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnprocEventdtlsVO.setPensionerdtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnprocEventdtlsVO);
					gLogger.info("Record Inserted in table trn_pnsnproc_eventdtls successfully");

				}
			}

			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocProvisionalPaid.class, serv.getSessionFactory());
			if (lLstTrnPnsnprocProvisionalPaidVO != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPnsnprocProvisionalPaidVO.size(); lIntCount++) {
					lObjPnsnprocProvisionalPaidVO = lLstTrnPnsnprocProvisionalPaidVO.get(lIntCount);
					lLngPnsnrProvisionalId = IFMSCommonServiceImpl.getNextSeqNum("TRN_PNSNPROC_PROVISIONAL_PAID", inputMap);
					lObjPnsnprocProvisionalPaidVO.setProvisionalPaidId(lLngPnsnrProvisionalId);
					lObjPnsnprocProvisionalPaidVO.setInwardPensionId(lLngInwardPensionId);
					lObjPnsnprocProvisionalPaidVO.setPensionerdtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjPnsnprocProvisionalPaidVO);
					gLogger.info("Record Inserted in table TRN_PNSNPROC_PROVISIONAL_PAID successfully");

				}
			}
			
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocForeignServ.class, serv.getSessionFactory());
			if (lLstTrnPnsnprocForeignServ != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPnsnprocForeignServ.size(); lIntCount++) {
					lObjTrnPnsnprocForeignServVO = lLstTrnPnsnprocForeignServ.get(lIntCount);
					lLngPnsnrForeignServ = IFMSCommonServiceImpl.getNextSeqNum("TRN_PNSNPROC_FOREIGN_SERV", inputMap);
					lObjTrnPnsnprocForeignServVO.setForeignServId(lLngPnsnrForeignServ);
					lObjTrnPnsnprocForeignServVO.setInwardPensionId(lLngInwardPensionId);					
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnprocForeignServVO);
					gLogger.info("Record Inserted in table TRN_PNSNPROC_FOREIGN_SERV successfully");

				}
			}
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocPnsnrservcbreak.class, serv.getSessionFactory());
			if (lLstTrnPnsnprocPnsnrservcbreakVO != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPnsnprocPnsnrservcbreakVO.size(); lIntCount++) {
					lObjTrnPnsnprocPnsnrservcbreakVO = lLstTrnPnsnprocPnsnrservcbreakVO.get(lIntCount);
					lLngPnsnrServcBreakId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_pnsnrservcbreak", inputMap);
					lObjTrnPnsnprocPnsnrservcbreakVO.setPnsnrServcBreakId(lLngPnsnrServcBreakId);
					lObjTrnPnsnprocPnsnrservcbreakVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnprocPnsnrservcbreakVO.setPensionerdtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnprocPnsnrservcbreakVO);
					gLogger.info("Record Inserted in table trn_pnsnproc_pnsnrservcbreak successfully");
				}
			}

			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocAvgPayCalc.class, serv.getSessionFactory());
			if (lLstTrnPnsnprocAvgPayCalcVO != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPnsnprocAvgPayCalcVO.size(); lIntCount++) {
					lObjTrnPnsnprocAvgPayCalcVO = lLstTrnPnsnprocAvgPayCalcVO.get(lIntCount);
					lLngAvgPayCalcId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_avg_pay_calc", inputMap);

					lObjTrnPnsnprocAvgPayCalcVO.setAvgPayCalcId(lLngAvgPayCalcId);
					lObjTrnPnsnprocAvgPayCalcVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnprocAvgPayCalcVO.setPensionerdtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnprocAvgPayCalcVO);
					gLogger.info("Record Inserted in table trn_pnsnproc_avg_pay_calc successfully");

				}
			}
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocFamilydtls.class, serv.getSessionFactory());
			if (lLstTrnPnsnprocFamilydtlsVO != null) {
				for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocFamilydtlsVO.size(); lIntCnt++) {
					lObjTrnPnsnprocFamilydtlsVO = lLstTrnPnsnprocFamilydtlsVO.get(lIntCnt);
					lLngPnsnrFamilyId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_familydtls", inputMap);

					lObjTrnPnsnprocFamilydtlsVO.setPnsnrFamilyId(lLngPnsnrFamilyId);
					lObjTrnPnsnprocFamilydtlsVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnprocFamilydtlsVO.setPensionerDtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnprocFamilydtlsVO);
					gLogger.info("Record Inserted in table trn_pnsnproc_familydtls successfully");

				}
			}
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnprocNomineedtls.class, serv.getSessionFactory());
			if (lLstTrnPnsnprocNomineedtlsVO != null) {
				for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnprocNomineedtlsVO.size(); lIntCnt++) {

					lObjTrnPnsnprocNomineedtlsVO = lLstTrnPnsnprocNomineedtlsVO.get(lIntCnt);
					lLngPnsnrNomineeId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_nomineedtls", inputMap);

					lObjTrnPnsnprocNomineedtlsVO.setPnsnrNomineeId(lLngPnsnrNomineeId);
					lObjTrnPnsnprocNomineedtlsVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnprocNomineedtlsVO.setPensionerDtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnprocNomineedtlsVO);
					gLogger.info("Record Inserted in table trn_pnsnproc_nomineedtls successfully");

				}
			}
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcAdvnceBal.class, serv.getSessionFactory());
			if (lLstTrnPnsnProcAdvnceBalVO != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPnsnProcAdvnceBalVO.size(); lIntCount++) {
					lObjTrnPnsnProcAdvnceBalVO = lLstTrnPnsnProcAdvnceBalVO.get(lIntCount);
					lLngadvanceBalId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_advncebal", inputMap);

					lObjTrnPnsnProcAdvnceBalVO.setAdvanceBalId(lLngadvanceBalId);
					lObjTrnPnsnProcAdvnceBalVO.setPnsnrRecoveryId(lLngpnsnrRecoveryId);
					lObjTrnPnsnProcAdvnceBalVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnProcAdvnceBalVO.setPensionerdtlId(lLngPensionerDtlId);

					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnProcAdvnceBalVO);
					gLogger.info("Record Inserted in table trn_pnsnproc_advncebal successfully");

				}
			}
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcAssesdDues.class, serv.getSessionFactory());
			if (lLstTrnPnsnProcAssesdDuesVO != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPnsnProcAssesdDuesVO.size(); lIntCount++) {
					lObjTrnPnsnProcAssesdDuesVO = lLstTrnPnsnProcAssesdDuesVO.get(lIntCount);
					lLngassesdDuesDtlId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_assessed_dues", inputMap);

					lObjTrnPnsnProcAssesdDuesVO.setAssesdDuesDtlId(lLngassesdDuesDtlId);
					lObjTrnPnsnProcAssesdDuesVO.setPnsnrRecoveryId(lLngpnsnrRecoveryId);
					lObjTrnPnsnProcAssesdDuesVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnProcAssesdDuesVO.setPensionerdtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnProcAssesdDuesVO);
					gLogger.info("Record Inserted in table trn_pnsnproc_assessed_dues successfully");
				}
			}
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_scan") != null) {
					lLngCertAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_scan")));
				}
				gLogger.info("::lLngCertAttachId :: " + lLngCertAttachId);
				CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
				attachmentMst.setAttachmentId(lLngCertAttachId);
			}
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcCheckList.class, serv.getSessionFactory());

			if (lLstTrnPnsnProcCheckListVO != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPnsnProcCheckListVO.size(); lIntCount++) {
					lObjTrnPnsnProcCheckListVO = lLstTrnPnsnProcCheckListVO.get(lIntCount);
					lLngpnsnsrCheckListId = IFMSCommonServiceImpl.getNextSeqNum("trn_pnsnproc_checklist", inputMap);

					lObjTrnPnsnProcCheckListVO.setPnsnsrCheckListId(lLngpnsnsrCheckListId);
					lObjTrnPnsnProcCheckListVO.setInwardPensionId(lLngInwardPensionId);
					lObjTrnPnsnProcCheckListVO.setPensionerdtlId(lLngPensionerDtlId);
					lObjTrnPnsnProcCheckListVO.setCertificateAttachId(lLngCertAttachId);
					lObjTrnPnsnProcInwardPensionDAO.create(lObjTrnPnsnProcCheckListVO);
					gLogger.info("Record Inserted in table trn_pnsnproc_checklist successfully");
				}
			}
			if (lLstTrnPnsnprocAuthorityDtls != null) {
				for (int lIntCount = 0; lIntCount < lLstTrnPnsnprocAuthorityDtls.size(); lIntCount++) {
					lObjAuthorityDtlsVO = lLstTrnPnsnprocAuthorityDtls.get(lIntCount);
					lLngPnsnsrAuthoId = IFMSCommonServiceImpl.getNextSeqNum("TRN_PNSNPROC_AUTHORITY_DTLS", inputMap);

					lObjAuthorityDtlsVO.setAuthorityDtlsId(lLngPnsnsrAuthoId);
					lObjAuthorityDtlsVO.setInwardPensionId(lLngInwardPensionId);
					lObjAuthorityDtlsVO.setLocationCode(gStrLocationCode);
					
					lObjTrnPnsnProcInwardPensionDAO.create(lObjAuthorityDtlsVO);
					gLogger.info("Record Inserted in table trn_pnsnproc_checklist successfully");
				}
			}
			// workflow
			//String lStrtoPost = null;
			String lStrStatus = StringUtility.getParameter("status", request).trim();
			if (lObjTrnPnsnProcInwardPensionVO.getDraftFlag() != null) {
				if (lStrStatus.length() > 0 && lStrStatus.trim().equals(gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"))) {
					lStrBldXML = getResponseXMLDoc(inputMap, "UPDATE");
				} else {
					if ("F".equalsIgnoreCase(lObjTrnPnsnProcInwardPensionVO.getDraftFlag().toString())) {
						lObjTrnPnsnProcInwardPensionVO.setCaseStatus(gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"));
						lObjTrnPnsnProcInwardPensionVO.setDraftFlag('F');

						lLngInwardPensionId = lObjTrnPnsnProcInwardPensionVO.getInwardPensionId();
						lStrBldXML = getResponseXMLDoc(inputMap, "Forward");
						
					}
				}

			}
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);

			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);			
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}

		return objRes;
	}

	private StringBuilder getResponseXMLDoc(Map inputMap, String strMode) {
		TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = (TrnPnsnProcInwardPension) inputMap.get("lObjTrnPnsnProcInwardPensionVO");

		String mode = strMode;

		StringBuilder lStrHidPKs = new StringBuilder();
		if (mode.equalsIgnoreCase("Add")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<INWARDNO>" + lObjTrnPnsnProcInwardPensionVO.getInwardNo());
			lStrHidPKs.append("</INWARDNO>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("insert");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("<INWARDID>" + lObjTrnPnsnProcInwardPensionVO.getInwardPensionId());
			lStrHidPKs.append("</INWARDID>");
			lStrHidPKs.append("</XMLDOC>");
		} else if (mode.equals("UPDATE")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<INWARDNO>" + lObjTrnPnsnProcInwardPensionVO.getInwardNo());
			lStrHidPKs.append("</INWARDNO>");
			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("update");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("<INWARDID>" + lObjTrnPnsnProcInwardPensionVO.getInwardPensionId());
			lStrHidPKs.append("</INWARDID>");
			lStrHidPKs.append("<CASETYPE>");
			lStrHidPKs.append(lObjTrnPnsnProcInwardPensionVO.getCaseType());
			lStrHidPKs.append("</CASETYPE>");
			lStrHidPKs.append("<CASESTATUS>");
			lStrHidPKs.append(lObjTrnPnsnProcInwardPensionVO.getCaseStatus());
			lStrHidPKs.append("</CASESTATUS>");
			lStrHidPKs.append("</XMLDOC>");
		} else // Forward
		{

			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<INWARDNO>" + lObjTrnPnsnProcInwardPensionVO.getInwardNo());
			lStrHidPKs.append("</INWARDNO>");

			lStrHidPKs.append("<MESSAGECODE>");
			lStrHidPKs.append("forward");
			lStrHidPKs.append("</MESSAGECODE>");
			lStrHidPKs.append("<INWARDID>" + lObjTrnPnsnProcInwardPensionVO.getInwardPensionId());
			lStrHidPKs.append("</INWARDID>");
			lStrHidPKs.append("<CASETYPE>");
			lStrHidPKs.append(lObjTrnPnsnProcInwardPensionVO.getCaseType());
			lStrHidPKs.append("</CASETYPE>");
			lStrHidPKs.append("<CASESTATUS>");
			lStrHidPKs.append(lObjTrnPnsnProcInwardPensionVO.getCaseStatus());
			lStrHidPKs.append("</CASESTATUS>");
			lStrHidPKs.append("</XMLDOC>");

		}

		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;
	}

	public ResultObject loadViewOrAddAddressPopup(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		new SimpleDateFormat("dd/MM/yyyy");

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lLstState = null;
		List lLstDistricts = null;
		try {

			setSessionInfo(inputMap);
			CommonPensionDAO lObjCmnPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			lLstState = lObjCmnPensionDAO.getAllState(gLngLangId);

			if (!StringUtility.getParameter("state", request).equals("") && StringUtility.getParameter("state", request).length() > 0) {
				lLstDistricts = lObjCmnPensionDAO.getDistrictsOfState((Long.valueOf(StringUtility.getParameter("state", request))), gLngLangId);

			} else {
				lLstDistricts = lObjCmnPensionDAO.getDistrictsOfState(Long.valueOf(15), gLngLangId);
			}

			String lStrMode = StringUtility.getParameter("Mode", request);
			String lStrRowId = StringUtility.getParameter("RowId", request);
			String lStrAddress = StringUtility.getParameter("address", request);
			String lStrState = StringUtility.getParameter("state", request);
			String lStrDistrict = StringUtility.getParameter("district", request);
			String lStrArea = StringUtility.getParameter("area", request);
			String lStrRoad = StringUtility.getParameter("road", request);
			String lStrFlat = StringUtility.getParameter("flat", request);
			String lStrPincode = StringUtility.getParameter("pincode", request);
			String lStrLandLineNo = StringUtility.getParameter("landLineNo", request);
			String lStrMobileNo = StringUtility.getParameter("mobileNo", request);
			String lStrEmailId = StringUtility.getParameter("emailId", request);

			inputMap.put("lStrState", lStrState);
			inputMap.put("lStrDistrict", lStrDistrict);
			inputMap.put("lStrArea", lStrArea);
			inputMap.put("lStrRoad", lStrRoad);
			inputMap.put("lStrFlat", lStrFlat);
			inputMap.put("lStrPincode", lStrPincode);
			inputMap.put("lStrLandLineNo", lStrLandLineNo);
			inputMap.put("lStrMobileNo", lStrMobileNo);
			inputMap.put("lStrEmailId", lStrEmailId);
			inputMap.put("lStrAddress", lStrAddress);
			inputMap.put("lLstState", lLstState);
			inputMap.put("lLstDistricts", lLstDistricts);
			inputMap.put("lStrRowId", lStrRowId);
			inputMap.put("lStrMode", lStrMode);
			gLogger.info("ViewOrAddNomineeAddr load Sucessful");

			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("viewOrAddAddressPopup");

		}

		catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;

	}

	public ResultObject loadSearchPensionCase(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lLstTypeOfSearch = null;
		List lLstClassOfPnsn = null;
		List<SavedPensionCasesVO> lLstPensionCases = null;
		List lLstDepartment = null;
		String lStrRole = null;
		String lStrDraftFlag = null;
		String lStrCasesFrom = null;
		TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = null;
		Integer lIntTotalRecords = null;
		try {
			setSessionInfo(inputMap);

			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			CommonPensionDAO lObjcmnPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactorySlave());
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactorySlave());
			lLstTypeOfSearch = IFMSCommonServiceImpl.getLookupValues("Search By", gLngLangId, inputMap);
			lLstClassOfPnsn = IFMSCommonServiceImpl.getLookupValues("Class of Pension", gLngLangId, inputMap);
			Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle.getString("PPROC.FIELDDEPARTMENTID"));
			lLstDepartment = lObjcmnPensionDAO.getAllDepartment(lLngDepartmentId, gLngLangId);
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(PensionCaseServiceImpl.class, serv.getSessionFactory());
			if (StringUtility.getParameter("DraftFlag", request) != null) {
				lStrDraftFlag = StringUtility.getParameter("DraftFlag", request);
			}
			if (StringUtility.getParameter("CasesFrom", request).length() > 0) {
				lStrCasesFrom = StringUtility.getParameter("CasesFrom", request);
			}
			Long isDdoOrDdoAsst = lObjTrnPnsnProcInwardPensionDAO.isDdoOrDdoAsst(gLngPostId);
			String lStrDdoCode = "";
			if(isDdoOrDdoAsst == 1L){
				lStrDdoCode = lObjTrnPnsnProcInwardPensionDAO.getDdoCodeForDDO(gLngPostId);
			}else if(isDdoOrDdoAsst == 2L){
				lStrDdoCode = lObjTrnPnsnProcInwardPensionDAO.getDdoCodeForDDOAsst(gLngPostId);
			}
			Long lLngDdoCode = 0L;
			if(!"".equals(lStrDdoCode))
				lLngDdoCode = Long.parseLong(lStrDdoCode);
			
			lIntTotalRecords = lObjTrnPnsnProcInwardPensionDAO.getAllFrwdCasesCount(lStrCasesFrom, gStrPostId, lStrDraftFlag, displayTag,lLngDdoCode);
			lLstPensionCases = lObjTrnPnsnProcInwardPensionDAO.getAllFrwdCases(lStrCasesFrom, gStrPostId, lStrDraftFlag, displayTag,lLngDdoCode);

			lStrRole = lObjTrnPnsnProcInwardPensionDAO.getRoleByPost(gLngPostId);
			List lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);
			inputMap.put("lStrRole", lStrRole);
			inputMap.put("lStrCasesFrom", lStrCasesFrom);
			inputMap.put("DraftFlag", lStrDraftFlag);
			inputMap.put("lLstTypeOfSearch", lLstTypeOfSearch);
			inputMap.put("lLstClassOfPnsn", lLstClassOfPnsn);
			inputMap.put("lLstDepartment", lLstDepartment);
			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("lLstPensionCases", lLstPensionCases);
			inputMap.put("lLstBankNames", lLstBanks);

			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("pensionCaseSearch");

		}

		catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject showPensionCaseList(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<SavedPensionCasesVO> lLstPensionCases = null;	
		List lLstTypeOfSearch = null;
		//List lLstUpperUsers = new ArrayList();
		String lStrSearchby = null;
		String lStrSearchValue = null;
		String lStrBranchName = null;
		String lStrDraftFlag = null;
		Date lDtFrmDate = null;
		Date lDtToDate = null;
		String lStrSevaarthId = null;
		String lStrPPONo = null;
		String lStrInwardNo = null;
		Date lDtRetiredDate = null;
		String lStrName = null;
		Long lLngDeparmentTypeId = null;
		String lStrPensionTypeId = null;
		List lLstDepartment = null;
		Long lLngDepartmentId = null;
		List lLstClassOfPnsn = null;
		String lStrCasesFrom = null;
		Integer lIntTotalRecords = null;
		try {
			setSessionInfo(inputMap);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			CommonPensionDAO lObjcmnPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactorySlave());
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactorySlave());
			//lLstUpperUsers = getHierarchyUsers(inputMap);
			lLstClassOfPnsn = IFMSCommonServiceImpl.getLookupValues("Class of Pension", gLngLangId, inputMap);
			lLngDepartmentId = Long.valueOf(gObjRsrcBndle.getString("PPROC.FIELDDEPARTMENTID"));
			lLstDepartment = lObjcmnPensionDAO.getAllDepartment(lLngDepartmentId, gLngLangId);
			lLstTypeOfSearch = IFMSCommonServiceImpl.getLookupValues("Search By", gLngLangId, inputMap);
			if (StringUtility.getParameter("CasesFrom", request).length() > 0) {
				lStrCasesFrom = StringUtility.getParameter("CasesFrom", request);
			}

			if (StringUtility.getParameter("cmbSearchBy", request).length() > 0) {
				lStrSearchby = StringUtility.getParameter("cmbSearchBy", request);
			}

			if (StringUtility.getParameter("txtSearchVal", request) != null && StringUtility.getParameter("txtSearchVal", request).length() > 0) {
				lStrSearchValue = StringUtility.getParameter("txtSearchVal", request);
			}
			if (StringUtility.getParameter("txtBranchName", request) != null && StringUtility.getParameter("txtBranchName", request).length() > 0) {
				lStrBranchName = StringUtility.getParameter("txtBranchName", request);
			}
			if (StringUtility.getParameter("DraftFlag", request) != null) {
				lStrDraftFlag = StringUtility.getParameter("DraftFlag", request);
			}
			if (!StringUtility.getParameter("txtName", request).equals("")) {
				lStrName = StringUtility.getParameter("txtName", request).trim().toUpperCase();
			}
			if (StringUtility.getParameter("txtSearchFromDate", request) != null && StringUtility.getParameter("txtSearchFromDate", request).length() > 0) {
				lDtFrmDate = simpleDateFormat.parse(StringUtility.getParameter("txtSearchFromDate", request).trim());
			}
			if (StringUtility.getParameter("txtSearchToDate", request) != null && StringUtility.getParameter("txtSearchToDate", request).length() > 0) {
				lDtToDate = simpleDateFormat.parse(StringUtility.getParameter("txtSearchToDate", request).trim());
			}
			if (StringUtility.getParameter("txtSevaarthId", request).length() > 0) {
				lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
			}
			if (StringUtility.getParameter("txtPPONo", request).length() > 0) {
				lStrPPONo = StringUtility.getParameter("txtPPONo", request).trim().toUpperCase();
			}
			if (StringUtility.getParameter("txtInwardNo", request).length() > 0) {
				lStrInwardNo = StringUtility.getParameter("txtInwardNo", request).trim().toUpperCase();
			}
			if (StringUtility.getParameter("txtDateOfRetiremt", request) != null && StringUtility.getParameter("txtDateOfRetiremt", request).length() > 0) {
				lDtRetiredDate = simpleDateFormat.parse(StringUtility.getParameter("txtDateOfRetiremt", request).trim());
			}
			if (StringUtility.getParameter("cmbDepartment", request) != null && StringUtility.getParameter("cmbDepartment", request).length() > 0
					&& !StringUtility.getParameter("cmbDepartment", request).equals("-1")) {
				lLngDeparmentTypeId = Long.valueOf(StringUtility.getParameter("cmbDepartment", request).trim());
			}
			if (StringUtility.getParameter("cmbTypeOfPnsn", request) != null && StringUtility.getParameter("cmbTypeOfPnsn", request).length() > 0
					&& !StringUtility.getParameter("cmbTypeOfPnsn", request).equals("-1")) {
				lStrPensionTypeId = StringUtility.getParameter("cmbTypeOfPnsn", request).trim();
			}

			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(PensionCaseServiceImpl.class, serv.getSessionFactory());

			lIntTotalRecords = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseDtlsCount(lStrCasesFrom, lStrDraftFlag, gStrPostId, gLngLangId, lStrSearchby, lStrSearchValue, lDtFrmDate, lDtToDate,
					lStrSevaarthId, lStrPPONo, lStrInwardNo, lDtRetiredDate, lStrName, lLngDeparmentTypeId, lStrPensionTypeId, displayTag, lStrBranchName);

			lLstPensionCases = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseDtls(lStrCasesFrom, lStrDraftFlag, gStrPostId, gLngLangId, lStrSearchby, lStrSearchValue, lDtFrmDate, lDtToDate,
					lStrSevaarthId, lStrPPONo, lStrInwardNo, lDtRetiredDate, lStrName, lLngDeparmentTypeId, lStrPensionTypeId, displayTag, lStrBranchName);

			if (lLstPensionCases != null && lLstPensionCases.size() > 0) {

				gLogger.info("lLstPensionCases is not null.");
				inputMap.put("lLstPensionCases", lLstPensionCases);
			}
			String lStrRole = lObjTrnPnsnProcInwardPensionDAO.getRoleByPost(gLngPostId);
			List lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);
			inputMap.put("lStrRole", lStrRole);
			inputMap.put("lLstBankNames", lLstBanks);
			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("DraftFlag", lStrDraftFlag);
			inputMap.put("lStrCasesFrom", lStrCasesFrom);
			inputMap.put("lLstTypeOfSearch", lLstTypeOfSearch);
			//inputMap.put("lLstUpperUsers", lLstUpperUsers);
			inputMap.put("lLstDepartment", lLstDepartment);
			inputMap.put("lLstClassOfPnsn", lLstClassOfPnsn);
			inputMap.put("post", gStrPostId);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("pensionCaseSearchList");

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	/*private List getHierarchyUsers(Map inputMap) {

		Integer llFromLevelId = 0;
		List lLstUpperPostList = null;
		List lLstUserList = null;
		try {
			setSessionInfo(inputMap);
			// Get the Subject Name
			String lStrSubjectName = gObjRsrcBndle.getString("PPROC.REQFLOW");

			// Get the Hierarchy Id
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gStrPostId, lStrSubjectName, inputMap);
			// Get the From level Id
			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId, lLngHierRefId, inputMap);
			// Get the List of Post ID of the users at the next Level
			lLstUserList = WorkFlowHelper.getUpperPost(gStrPostId, lLngHierRefId, llFromLevelId, inputMap);

			lLstUpperPostList = getUserList(lLstUserList);

		} catch (Exception e) {

			gLogger.error("Error is;" + e, e);
		}
		return lLstUpperPostList;
	}*/

	/*private List getLowerHierarchyUsers(Map objectArgs) {

		List lLstLowerPostList = null;
		String lStrToLevel = null;
		String lStrRole = null;
		Long lLngHierRefId = null;
		try {
			setSessionInfo(objectArgs);
			// Get the Subject Name
			String subjectName = gObjRsrcBndle.getString("PPROC.REQFLOW");

			// Get the Hierarchy Id
			lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gStrPostId, subjectName, objectArgs);

			WorkFlowHelper.getLevelFromPostMpg(gStrPostId, lLngHierRefId, objectArgs);

			TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardDao = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
			lStrRole = lObjTrnPnsnProcInwardDao.getRoleByPost(gLngPostId);

			if ((gObjRsrcBndle.getString("PPROC.DDOROLE").equals(lStrRole))) {
				lLstLowerPostList = lObjTrnPnsnProcInwardDao.getLowerLevelUserList(gObjRsrcBndle.getString("PPROC.LOWLEVEL"), lLngHierRefId, gLngLangId);
			} else {
				lStrToLevel = lObjTrnPnsnProcInwardDao.getLowerLevelForReturn(Long.valueOf(gStrPostId), lLngHierRefId);
				if ((!("".equals(lStrToLevel)) || (lStrToLevel != null))) {
					lLstLowerPostList = lObjTrnPnsnProcInwardDao.getLowerLevelUserList(lStrToLevel, lLngHierRefId, gLngLangId);

				}
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
		}
		return lLstLowerPostList;
	}*/

	/*
	 * view pensioner photo and signature attachament
	 */
	public ResultObject viewAttachmentFromClientPath(Map mp) {
		ResultObject objRes = new ResultObject(-1, "FAIL");
		StringBuilder lStrData = new StringBuilder();

		try {
			byte allBytesInBlob[] = new byte[0];
			objRes = new ResultObject(0, "FAIL");
			HttpServletRequest request = (HttpServletRequest) mp.get("requestObj");
			Map fileItemArrayListMap = AttachmentHelper.fileItemArrayListMap;
			String key = "";
			String rowNumber = request.getParameter("rowNumber");
			String attachmentName = request.getParameter("attachmentUniqeName");
			int rowIndex = 0;
			if (request.getParameter("rowIndex") != null) {
				rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
			}
			int rowCount = 0;
			if (request.getParameter("rowCount") != null) {
				rowCount = Integer.parseInt(request.getParameter("rowCount"));
			}
			rowIndex -= rowCount;
			if (rowNumber != null && rowNumber.length() >= 1) {
				key = (new StringBuilder(String.valueOf(request.getSession().getAttribute("name")))).append(attachmentName).append(request.getSession().getId()).append(rowNumber).toString();
			} else {
				key = (new StringBuilder(String.valueOf(request.getSession().getAttribute("name")))).append(attachmentName).append(request.getSession().getId()).toString();
			}
			ArrayList attachmentList = (ArrayList) fileItemArrayListMap.get(key);
			if (attachmentList != null && !attachmentList.isEmpty()) {
				FileItem fileItem = (FileItem) attachmentList.get(attachmentList.size() - 1);
				allBytesInBlob = fileItem.get();
			}
			mp.put("buteArray", allBytesInBlob);
			lStrData.append("<XMLDOC>");
			lStrData.append("<HEADDESC>");
			lStrData.append(allBytesInBlob);
			lStrData.append("</HEADDESC>");
			lStrData.append("</XMLDOC>");
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(mp);
			objRes.setViewName("viewAttachment");

		} catch (Exception e) {
			objRes.setThrowable(e);
			objRes.setResultCode(-1);

		}
		return objRes;
	}

	public ResultObject forwardPensionCase(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrRole = null;
		Long lLngPensionCaseId = null;
		String lStrComments = null;
		TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = null;
		try {
			setSessionInfo(inputMap);
			String lStrPnsnCaseId = StringUtility.getParameter("pensionCaseId", request).trim();

			String lStrStatus = StringUtility.getParameter("status", request).toString();
			//String lStrToPost = StringUtility.getParameter("ForwardTo", request).toString();
			lStrComments = StringUtility.getParameter("comments", request);
			
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
				lStrRole = lObjTrnPnsnProcInwardPensionDAO.getRoleByPost(gLngPostId);
				String[] lStrArrRole = lStrRole.split(",");
				
				lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
				lLngPensionCaseId = Long.parseLong(lStrPnsnCaseId);
				TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPension = (TrnPnsnProcInwardPension) lObjTrnPnsnProcInwardPensionDAO.read(lLngPensionCaseId);

				if (lStrStatus.equalsIgnoreCase("Forward")) {

					for (int lIntCnt = 0; lIntCnt < lStrArrRole.length; lIntCnt++) {
						if (lStrArrRole[lIntCnt].trim().equals(gObjRsrcBndle.getString("PPROC.DEOROLE"))) {
							lObjTrnPnsnProcInwardPension.setCaseStatus(gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"));
							lObjTrnPnsnProcInwardPension.setDraftFlag('F');
						}
						if (lStrArrRole[lIntCnt].trim().equals(gObjRsrcBndle.getString("PPROC.VERIFIERROLE"))) {
							lObjTrnPnsnProcInwardPension.setDraftFlag('F');
							lObjTrnPnsnProcInwardPension.setCaseStatus(gObjRsrcBndle.getString("PPROC.FWDBYVERIFIERSTATUSID"));
						}

					}
				} else {
					for (int lIntCnt = 0; lIntCnt < lStrArrRole.length; lIntCnt++) {
						if ((gObjRsrcBndle.getString("PPROC.VERIFIERROLE").equals(lStrArrRole[lIntCnt].trim()))) {
							lObjTrnPnsnProcInwardPension.setDraftFlag('R');
							lObjTrnPnsnProcInwardPension.setCaseStatus(gObjRsrcBndle.getString("PPROC.RJCTBYVERIFIERSTATUSID"));
						}
						if ((gObjRsrcBndle.getString("PPROC.DDOROLE").equals(lStrArrRole[lIntCnt].trim()))) {
							lObjTrnPnsnProcInwardPension.setDraftFlag('R');
							lObjTrnPnsnProcInwardPension.setCaseStatus(gObjRsrcBndle.getString("PPROC.RJCTBYDDOSTATUSID"));
						}
					}

				}
				lObjTrnPnsnProcInwardPension.setComments(lStrComments);
				lObjTrnPnsnProcInwardPension.setUpdatedUserId(gLngUserId);
				lObjTrnPnsnProcInwardPension.setUpdatedPostId(gLngPostId);
				lObjTrnPnsnProcInwardPension.setUpdatedDate(gDtCurDate);

			

			inputMap.put("ajaxKey", "Success");
			resObj.setResultCode(ErrorConstants.SUCCESS);
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

	public ResultObject approvePensionCase(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardDAO = null;
		try {
			setSessionInfo(inputMap);
			lObjTrnPnsnProcInwardDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
			setSessionInfo(inputMap);
			String lStrPnsnCaseId = StringUtility.getParameter("pensionCaseId", request).toString().trim();
			String lStrComments = StringUtility.getParameter("comments", request);
			Long lLngPensionCaseId = Long.parseLong(lStrPnsnCaseId);
			TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = (TrnPnsnProcInwardPension) lObjTrnPnsnProcInwardDAO.read(lLngPensionCaseId);
			lObjTrnPnsnProcInwardPensionVO.setCaseStatus(gObjRsrcBndle.getString("PPROC.APPROVEDBYDDOSTATUSID"));
			lObjTrnPnsnProcInwardPensionVO.setDraftFlag('A');
			lObjTrnPnsnProcInwardPensionVO.setComments(lStrComments);
			lObjTrnPnsnProcInwardDAO.update(lObjTrnPnsnProcInwardPensionVO);
			inputMap.put("ajaxKey", "Success");
			resObj.setViewName("ajaxData");
			resObj.setResultCode(ErrorConstants.SUCCESS);
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

	public ResultObject viewPensionCaseStatus(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List lLstPnsnCaseStatus = new ArrayList();
		List<String> lLstStatus = new ArrayList<String>();
		TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = null;
		Integer lIntTotalRecords = null;
		try {
			setSessionInfo(inputMap);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			/*lLstStatus.add(gObjRsrcBndle.getString("PPROC.DRAFTSTATUSID"));
			lLstStatus.add(gObjRsrcBndle.getString("PPROC.FWDBYDEOSTATUSID"));
			lLstStatus.add(gObjRsrcBndle.getString("PPROC.FWDBYVERIFIERSTATUSID"));
			lLstStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYVERIFIERSTATUSID"));
			lLstStatus.add(gObjRsrcBndle.getString("PPROC.APPROVEDBYDDOSTATUSID"));
			lLstStatus.add(gObjRsrcBndle.getString("PPROC.RJCTBYDDOSTATUSID"));*/
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
			
			Long isDdoOrDdoAsst = lObjTrnPnsnProcInwardPensionDAO.isDdoOrDdoAsst(gLngPostId);
			String lStrDdoCode = "";
			if(isDdoOrDdoAsst == 1L){
				lStrDdoCode = lObjTrnPnsnProcInwardPensionDAO.getDdoCodeForDDO(gLngPostId);
			}else if(isDdoOrDdoAsst == 2L){
				lStrDdoCode = lObjTrnPnsnProcInwardPensionDAO.getDdoCodeForDDOAsst(gLngPostId);
			}
			Long lLngDdoCode = 0L;
			if(!"".equals(lStrDdoCode))
				lLngDdoCode = Long.parseLong(lStrDdoCode);
			
			lIntTotalRecords = lObjTrnPnsnProcInwardPensionDAO.displayPensionCaseStatusCount(lLstStatus, displayTag,lLngDdoCode);
			lLstPnsnCaseStatus = lObjTrnPnsnProcInwardPensionDAO.displayPensionCaseStatus(lLstStatus, displayTag,lLngDdoCode);

			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("lLstPnsnCaseStatus", lLstPnsnCaseStatus);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
			objRes.setViewName("pensionCaseStatus");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}

		return objRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.service.PensionCaseService#getBranchesOfBank(
	 * java.util.Map)
	 */

	public ResultObject getBranchesOfBank(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = null;
		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = null;
		List lLstBranches = new ArrayList();
		try {
			setSessionInfo(inputMap);
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(null, serv.getSessionFactory());
			lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());
			String lStrBankCode = StringUtility.getParameter("bankCode", request);
			String lStrLocationCode = lObjTrnPnsnProcInwardPensionDAO.getLocCodeFromDDO(gStrLocationCode);

			if (lStrBankCode != null && lStrBankCode.length() > 0) {
				lLstBranches = lObjOnlinePensionCaseDAO.getBranchsOfBank(lStrBankCode, gLngLangId, lStrLocationCode);
			}

			String lSBStatus = getResponseXMLDocForBranch(lLstBranches).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	private StringBuilder getResponseXMLDocForBranch(List listBranches) {

		StringBuilder lStrBldXML = new StringBuilder();
		Iterator itr = listBranches.iterator();
		lStrBldXML.append("<XMLDOC>");
		while (itr.hasNext()) {
			Object[] obj = (Object[]) itr.next();
			lStrBldXML.append("<BranchCode>");
			lStrBldXML.append(obj[0].toString());
			lStrBldXML.append("</BranchCode>");
			lStrBldXML.append("<BranchName>");
			lStrBldXML.append(obj[1].toString());
			lStrBldXML.append("</BranchName>");

		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject getValidEmoluments(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		new ArrayList();
		try {
			setSessionInfo(inputMap);
			new TrnPnsnProcInwardPensionDAOImpl(null, serv.getSessionFactory());
			StringUtility.getParameter("inwardPensionId", request);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	public ResultObject getCvpRate(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = null;
		String lStrAge = null;
		String lStrPayCmsn = null;
		BigDecimal lBDCvpRate = BigDecimal.ZERO;
		StringBuilder lSBStatus = new StringBuilder();
		String lStrResult = null;
		try {
			setSessionInfo(inputMap);
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(null, serv.getSessionFactory());
			lStrAge = StringUtility.getParameter("Age", request);
			lStrPayCmsn = StringUtility.getParameter("PayCommission", request);
			if (lStrAge.length() > 0 && lStrPayCmsn.length() > 0) {
				lBDCvpRate = lObjTrnPnsnProcInwardPensionDAO.getCvpRate(new BigDecimal(lStrAge), lStrPayCmsn);
			}

			lSBStatus.append("<XMLDOCCVPRATE>");
			lSBStatus.append("<CVPRATE>");
			lSBStatus.append(lBDCvpRate);
			lSBStatus.append("</CVPRATE>");

			lSBStatus.append("</XMLDOCCVPRATE>");
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
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

	public ResultObject getDesgListForPensionCase(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<ComboValuesVO> lLstDesignation = new ArrayList<ComboValuesVO>();
		TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = null;
		try {
			setSessionInfo(inputMap);
			lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcPnsnrDtls.class, serv.getSessionFactory());
			String lStrDesignation = StringUtility.getParameter("searchKey", request);

			if (!"".equals(lStrDesignation)) {
				lLstDesignation = lObjTrnPnsnProcInwardPensionDAO.getDesignationForPensionCase(lStrDesignation, gLngLangId);
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

	private List getUserList(List lLstUserList) throws Exception {

		List lLstUsers = null;

		try {

			BptmCommonServicesDAOImpl lObjCmnSrvcDAOImpl = new BptmCommonServicesDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());

			if (lLstUserList != null) {

				String[] postString = new String[lLstUserList.size()];
				String[] levelString = new String[lLstUserList.size()];
				Object[] lObjNextPost = null;

				for (int i = 0; i < lLstUserList.size(); i++) {
					lObjNextPost = (Object[]) lLstUserList.get(i);
					postString[i] = lObjNextPost[0].toString();
					levelString[i] = lObjNextPost[1].toString();
				}

				if (lLstUserList.size() > 0) {

					lLstUsers = lObjCmnSrvcDAOImpl.getUserFromPost(postString, levelString, gLngLangId);
				}
			}

		} catch (Exception e) {

			gLogger.error("Error occured while executing getUserList in pension case " + e, e);
			throw e;
		}
		return lLstUsers;
	}

	public ResultObject loadPenProcInwardForm(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			setSessionInfo(inputMap);
			List<CmnLookupMst> lLstTypeOfCase = null;
			List<CmnLookupMst> lLstPayCommission = null;
			List<CmnLookupMst> lLstClassOfPnsn = null;
			List<CmnLookupMst> lLstPensionerType = null;
			List<CmnLookupMst> lLstGroupList = null;
			List<CmnLookupMst> lLstEvents = null;
			List<CmnLookupMst> lLstPayScale = null;
			List<CmnLookupMst> lLstSrvcBrkType = null;
			//List<CmnLookupMst> lLstAdvnceBalType = null;
			List<CmnLookupMst> lLstAssesdTypeList = null;
			List<CmnLookupMst> lLstOfficeList = null;
			List<CmnLookupMst> lLstCertificate = null;
			List<CmnLookupMst> lLstRelation = null;
			List<ComboValuesVO> lLstState = null;
			List<ComboValuesVO> lLstDistricts = null;
			List<ComboValuesVO> lLstDepartment = null;
			List<ComboValuesVO> lLstHOOFrmDept = null;
			List<ComboValuesVO> lLstBankBranch = null;
			List<ComboValuesVO> lLstTreasury = null;
			List<ComboValuesVO> lLstBankNames = null;
			List<ComboValuesVO> lLstBanks = null;
			List<ComboValuesVO> lLstYears = null;
			List<ComboValuesVO> lLstMonths = null;
			List<ComboValuesVO> lLstAdminDept = null;
			List<Long> lLstTreasuryId = new ArrayList<Long>();
			//List lLstUpperUsers = null;
			//List lLstLowerUsers = null;
			List lLstDesignation = null;
			//String lStrRole = null;
			String lStrShowReadOnly = null;
			String lStrStatusLookupId = null;
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			CmnAttachmentMstDAO cmnAttachmentMstDAO = null;
			CmnAttachmentMst cmnAttachmentMst = null;
			Set<CmnAttachmentMpg> cmnAttachmentMpgs = null;
			Iterator<CmnAttachmentMpg> cmnAttachmentMpgIterator = null;
			CmnAttachmentMpg cmnAttachmentMpg = null;
			String lStrLocationCode = null;
			String lStrBankName = null;
			List<MstEmp> lLstempBasicDtls = null;

			String lStrSevaarthId = null;
			String lStrPensionType = null;
			String lStrDdoCodeOfLoggedId = null;
			Long lLngReqPending = null;

			TrnPnsnProcInwardPensionDAO lObjInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
			TrnPnsnProcPnsnrDtlsDAO lObjPnsnProcPnsnrDtlsDAO = new TrnPnsnProcPnsnrDtlsDAOImpl(TrnPnsnProcPnsnrDtls.class, serv.getSessionFactory());
			TrnPnsnprocPnsnrpayDAO lObjPnsnprocPnsnrpayDAO = new TrnPnsnprocPnsnrpayDAOImpl(TrnPnsnprocPnsnrpay.class, serv.getSessionFactory());

			TrnPnsnProcInwardPension lObjPnsnProcInwardPensionVO = new TrnPnsnProcInwardPension();
			TrnPnsnProcPnsnrDtls lObjPnsnProcPnsnrDtlsVO = new TrnPnsnProcPnsnrDtls();
			TrnPnsnprocPnsnrpay lObjPnsnprocPnsnrpayVO = new TrnPnsnprocPnsnrpay();

			
			lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request);
			lStrPensionType = StringUtility.getParameter("cmbClassOfPnsn", request);
			
			Long lLngInwardId = null;
			String lFlagForRevision = "N";
			
			lStrDdoCodeOfLoggedId = lObjInwardPensionDAO.getDdoCodeForDDOAsst(gLngPostId);
			
			if(lStrDdoCodeOfLoggedId != null && lStrDdoCodeOfLoggedId != "")
				lLngReqPending = lObjInwardPensionDAO.getReqPenddingStatus(lStrSevaarthId, Long.parseLong(lStrDdoCodeOfLoggedId));
			
			if(lLngReqPending == 0L){
				
				if (!"".equals(lStrSevaarthId)) {
					
					if(lStrDdoCodeOfLoggedId != null && lStrDdoCodeOfLoggedId != "")				
						lLngInwardId = lObjInwardPensionDAO.getApprovedCaseForRevision(lStrSevaarthId, Long.parseLong(lStrDdoCodeOfLoggedId));
					if(lLngInwardId != null && lLngInwardId != 0L){
						lFlagForRevision = "Y";
					}else{
						lLstempBasicDtls = lObjInwardPensionDAO.getEmpBasicDtls(lStrSevaarthId.toUpperCase().trim(), lStrDdoCodeOfLoggedId);
					}
				}
	
				lLstRelation = IFMSCommonServiceImpl.getLookupValues("RelationShip List", gLngLangId, inputMap);
	
				lLstCertificate = IFMSCommonServiceImpl.getLookupValues("CertificateList", gLngLangId, inputMap);
	
				lLstOfficeList = IFMSCommonServiceImpl.getLookupValues("OfficeList", gLngLangId, inputMap);
	
				lLstEvents = IFMSCommonServiceImpl.getLookupValues("EventList", gLngLangId, inputMap);
	
				lLstAssesdTypeList = IFMSCommonServiceImpl.getLookupValues("AssesdTypeList", gLngLangId, inputMap);
	
				//lLstAdvnceBalType = IFMSCommonServiceImpl.getLookupValues("AdvanceTypeList", gLngLangId, inputMap);
	
				lLstPayScale = IFMSCommonServiceImpl.getLookupValues("PayScale", gLngLangId, inputMap);
	
				lLstSrvcBrkType = IFMSCommonServiceImpl.getLookupValues("SrvcBrkType", gLngLangId, inputMap);
	
				lLstClassOfPnsn = IFMSCommonServiceImpl.getLookupValues("Class of Pension", gLngLangId, inputMap);
	
				lLstPensionerType = IFMSCommonServiceImpl.getLookupValues("Class Type", gLngLangId, inputMap);
	
				lLstGroupList = IFMSCommonServiceImpl.getLookupValues("Group", gLngLangId, inputMap);
	
				CommonPensionDAO lObjCmnPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
	
				lLstState = lObjCmnPensionDAO.getAllState(gLngLangId);
	
				Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle.getString("PPROC.FIELDDEPARTMENTID"));
	
				
	
				lLstDesignation = lObjCmnPensionDAO.getAllDesignation(gLngLangId);
				lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPROC.TREASURYID1")));
				lLstTreasuryId.add(Long.valueOf(gObjRsrcBndle.getString("PPROC.TREASIRYID2")));
				lLstTreasury = lObjCmnPensionDAO.getAllTreasury(lLstTreasuryId, gLngLangId);
				lLstAdminDept = lObjCmnPensionDAO.getAdminDept();
	
				TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
				GradDesgScaleMapDAO gdDao = new GradDesgScaleMapDAO(HrEisSgdMpg.class, serv.getSessionFactory());
	
				lLstBankNames = lObjCmnPensionDAO.getBankNames(gLngLangId, gStrLocationCode);
	
				lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);
	
				lLstMonths = lObjCommonDAO.getMonthList(SessionHelper.getLocale(request));
				lLstYears = lObjCommonDAO.getYearList(SessionHelper.getLocale(request));
	
				inputMap.put("lLstMonths", lLstMonths);
				inputMap.put("lLstYears", lLstYears);
	
				//lStrRole = lObjTrnPnsnProcInwardPensionDAO.getRoleByPost(gLngPostId);
				Long isDdoOrDdoAsst = lObjTrnPnsnProcInwardPensionDAO.isDdoOrDdoAsst(gLngPostId);
				
				if(isDdoOrDdoAsst == 1L){
					inputMap.put("lStrRole", "700002");
				}else if(isDdoOrDdoAsst == 2L){
					inputMap.put("lStrRole", "700001");
				}
				List<HrEisScaleMst> scaleList = gdDao.getScalefromDsgnComm(2500340L);
				List<HrEisScaleMst> lLst6thPayScale = gdDao.getScalefromDsgnComm(2500341L);
				HrEisScaleMst hrEisScaleMst = null;
				long sgdId = 0L;
	
				/*String[] lStrArrRole = lStrRole.split(",");
				for (int lIntCnt = 0; lIntCnt < lStrArrRole.length; lIntCnt++) {
					if (gObjRsrcBndle.getString("PPROC.DEOROLE").equalsIgnoreCase(lStrArrRole[lIntCnt].trim())) {
						lStrRole = gObjRsrcBndle.getString("PPROC.DEOROLE");
					}
	
				}
	
				if (!gObjRsrcBndle.getString("PPROC.DDOROLE").equalsIgnoreCase(lStrRole)) {
					lLstUpperUsers = getHierarchyUsers(inputMap);
				}
	
				if (!gObjRsrcBndle.getString("PPROC.DEOROLE").equalsIgnoreCase(lStrRole)) {
					lLstLowerUsers = getLowerHierarchyUsers(inputMap);
				}*/
	
				Date lDtCurDate = SessionHelper.getCurDate();
	
				inputMap.put("lLstRelation", lLstRelation);
				inputMap.put("lDtCurDate", lObjDateFormat.format(lDtCurDate));
				inputMap.put("lLstTypeOfCase", lLstTypeOfCase);
				inputMap.put("lLstPayCommission", lLstPayCommission);
				inputMap.put("lLstClassOfPnsn", lLstClassOfPnsn);
				inputMap.put("lLstPensionerType", lLstPensionerType);
				inputMap.put("lLstGroupList", lLstGroupList);
				inputMap.put("lLstState", lLstState);
				inputMap.put("lLstTreasury", lLstTreasury);
				inputMap.put("lLstBankNames", lLstBankNames);				
				inputMap.put("lLstHOOFrmDept", lLstAdminDept);
	
				inputMap.put("lLstDesignation", lLstDesignation);
				inputMap.put("lLstEvents", lLstEvents);
				inputMap.put("lLstPayScale", lLstPayScale);
				inputMap.put("lLstSrvcBrkType", lLstSrvcBrkType);
				//inputMap.put("lLstAdvnceBalType", lLstAdvnceBalType);
				inputMap.put("lLstAssesdTypeList", lLstAssesdTypeList);
				inputMap.put("lLstOfficeList", lLstOfficeList);
				inputMap.put("lLstCertificate", lLstCertificate);
				//inputMap.put("lLstUpperUsers", lLstUpperUsers);
				//inputMap.put("lLstLowerUsers", lLstLowerUsers);
				//inputMap.put("lStrRole", lStrRole);
				inputMap.put("lLstBanks", lLstBanks);
	
				if(lFlagForRevision.equals("N")){
				
					if (!lLstempBasicDtls.isEmpty()) {
		
						MstEmp lObjMstEmp = null;
						DdoOffice lObjDdoOfficeVO = null;
		
						String lStrDdoCode = "";
						String lStrCurrOffice = "";
						Long lLngFieldDept = 0L;
						List<ComboValuesVO> lLstOffDistricts = null;
						List<ComboValuesVO> lLstPrDistricts = null;
						Long lLngEmpId = 0L;
						Long lLngPayScaleId = null;
						Long lLngBasicPay = null;
						BigDecimal lBigDecBasicPay = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
						SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy");
						Date lDtRetirement = null;
						Calendar cal = Calendar.getInstance();
						List<MstEmpNmn> lLstEmpNomineeDtls = null;					
						List<Object[]> lLstPnsnprocNomineeDtls = new ArrayList<Object[]>();
		
						lObjMstEmp = lLstempBasicDtls.get(0);
						lLngEmpId = lObjMstEmp.getOrgEmpMstId();
						lStrDdoCode = lObjMstEmp.getDdoCode();
		
						if (lStrDdoCodeOfLoggedId.equals(lStrDdoCode)) {
		
							if (lObjMstEmp.getPayCommission().equals("700015")) {
								lObjPnsnProcInwardPensionVO.setPayCommission("5THPAYCOMSN");
							} else if (lObjMstEmp.getPayCommission().equals("700016")) {
								lObjPnsnProcInwardPensionVO.setPayCommission("6THPAYCOMSN");
								scaleList = gdDao.getScalefromDsgnComm(2500341L);
							}
		
							lObjPnsnProcInwardPensionVO.setPensionType(lStrPensionType);
							lObjPnsnProcInwardPensionVO.setSevaarthId(lStrSevaarthId);
							lObjPnsnProcInwardPensionVO.setCaseType("NEW");
							if (!"".equals(lStrDdoCode) && lStrDdoCode != null) {
								lObjPnsnProcInwardPensionVO.setDdoCode(Long.parseLong(lStrDdoCode));
							}
		
							inputMap.put("lObjTrnPnsnProcInwardPensionVO", lObjPnsnProcInwardPensionVO);
		
							lObjPnsnProcPnsnrDtlsVO.setPnsnrName(lObjMstEmp.getName());
							lObjPnsnProcPnsnrDtlsVO.setPnsnrNameInMarathi(lObjMstEmp.getName_marathi());
							lObjPnsnProcPnsnrDtlsVO.setGenderFlag(lObjMstEmp.getGender());
							lObjPnsnProcPnsnrDtlsVO.setBirthDate(lObjMstEmp.getDob());
							lObjPnsnProcPnsnrDtlsVO.setJoiningDate(lObjMstEmp.getDoj());
							lObjPnsnProcPnsnrDtlsVO.setPanNo(lObjMstEmp.getPANNo());
		
							String lStrEmpGroup = lObjMstEmp.getGroup();
							
							if(lStrEmpGroup.equals("A"))						
								lObjPnsnProcInwardPensionVO.setPensionerType("Group A");
							if(lStrEmpGroup.equals("B") || lStrEmpGroup.equals("BnGz"))
								lObjPnsnProcInwardPensionVO.setPensionerType("Group B");
							if(lStrEmpGroup.equals("C"))						
								lObjPnsnProcInwardPensionVO.setPensionerType("Group C");
							if(lStrEmpGroup.equals("D"))						
								lObjPnsnProcInwardPensionVO.setPensionerType("Group D");
							
								
							if (!lStrPensionType.equals("FAMILYPNSN") && !lStrPensionType.equals("VOLUNTARY64") && !lStrPensionType.equals("VOLUNTARY65")) {
								lDtRetirement = lObjMstEmp.getServEndDate();
							}
							lObjPnsnProcPnsnrDtlsVO.setRetirementDate(lDtRetirement);
		
							lObjPnsnProcPnsnrDtlsVO.setuId(lObjMstEmp.getUIDNo());
							lObjPnsnProcPnsnrDtlsVO.seteId(lObjMstEmp.getEIDNo());
		
							lObjPnsnProcPnsnrDtlsVO.setDesignation(lObjPnsnProcPnsnrDtlsDAO.getDesignation(lLngEmpId));
		
							//lLngFieldDept = lObjPnsnProcPnsnrDtlsDAO.getFieldDept(lStrDdoCode);
							
							if(lObjMstEmp.getParentDept() != null && lObjMstEmp.getParentDept() != "")
								lLngFieldDept = Long.parseLong(lObjMstEmp.getParentDept());
							
							lObjPnsnProcPnsnrDtlsVO.setDepartmentId(lLngFieldDept);
						
							lStrCurrOffice = lObjMstEmp.getCurrOff();
							if (!"".equals(lStrCurrOffice) && lStrCurrOffice != null) {								
								lObjDdoOfficeVO = lObjPnsnProcPnsnrDtlsDAO.getEmpOfficeDtls(Long.parseLong(lStrCurrOffice));
								
							}
							if (lObjDdoOfficeVO != null) {								
								lObjPnsnProcPnsnrDtlsVO.setDdoOfficeName(lObjDdoOfficeVO.getDcpsDdoOfficeName());
								lObjPnsnProcPnsnrDtlsVO.setOfficeRoad(lObjDdoOfficeVO.getDcpsDdoOfficeAddress1());
								lObjPnsnProcPnsnrDtlsVO.setOfficeLandLineNo((lObjDdoOfficeVO.getDcpsDdoOfficeTelNo1() == null) ? "" : lObjDdoOfficeVO.getDcpsDdoOfficeTelNo1().toString());
								lObjPnsnProcPnsnrDtlsVO.setOfficeMobileNo((lObjDdoOfficeVO.getDcpsDdoOfficeTelNo2() == null) ? "" : lObjDdoOfficeVO.getDcpsDdoOfficeTelNo2().toString());
								lObjPnsnProcPnsnrDtlsVO.setOfficePincode(lObjDdoOfficeVO.getDcpsDdoOfficePin());
								lObjPnsnProcPnsnrDtlsVO.setOfficeStateCode((lObjDdoOfficeVO.getDcpsDdoOfficeState() == null) ? 0L : Long.parseLong(lObjDdoOfficeVO.getDcpsDdoOfficeState()));
								lObjPnsnProcPnsnrDtlsVO.setOfficeDistCode(lObjDdoOfficeVO.getDcpsDdoOfficeDistrict());
								lObjPnsnProcPnsnrDtlsVO.setOfficeEmailAddr(lObjDdoOfficeVO.getDcpsDdoOfficeEmail());
		
								lLstOffDistricts = lObjCmnPensionDAO.getDistrictsOfState((lObjDdoOfficeVO.getDcpsDdoOfficeState() == null) ? 0L : Long.parseLong(lObjDdoOfficeVO.getDcpsDdoOfficeState()),
										gLngLangId);
							}
							inputMap.put("lLstOffDistricts", lLstOffDistricts);
							//lLstHOOFrmDept = lObjCmnPensionDAO.getHooFromDepartmentLoc(lLngFieldDept, gLngLangId);
							//inputMap.put("lLstHOOFrmDept", lLstHOOFrmDept);
							Long lLngAdminDept =  lObjCmnPensionDAO.getAdminDeptNameFromFieldDept(lLngFieldDept);
							
							lLstDepartment = lObjCmnPensionDAO.getFieldDeptFromAdmDept(lLngAdminDept);
							inputMap.put("lLstDepartment", lLstDepartment);
							
							lObjPnsnProcPnsnrDtlsVO.setHooId(lLngAdminDept);		
							lObjPnsnProcPnsnrDtlsVO.setPnsnrAddrFlat(lObjMstEmp.getBuilding_address());
							lObjPnsnProcPnsnrDtlsVO.setPnsnrAddrRoad(lObjMstEmp.getBuilding_street());
							lObjPnsnProcPnsnrDtlsVO.setPnsnrAddrArea(lObjMstEmp.getLocality());
							lObjPnsnProcPnsnrDtlsVO.setPnsnrAddrStateCode((lObjMstEmp.getState() == null) ? 0L : Long.parseLong(lObjMstEmp.getState()));
							lObjPnsnProcPnsnrDtlsVO.setPnsnrAddrDistCode(lObjMstEmp.getDistrict());
							lObjPnsnProcPnsnrDtlsVO.setPnsnrAddrPincode(lObjMstEmp.getPincode());
							lObjPnsnProcPnsnrDtlsVO.setPnsnrAddrMobileNo((lObjMstEmp.getCellNo() == null) ? "" : lObjMstEmp.getCellNo().toString());
		
							lLstPrDistricts = lObjCmnPensionDAO.getDistrictsOfState((lObjMstEmp.getState() == null) ? 0L : Long.parseLong(lObjMstEmp.getState()), gLngLangId);
							inputMap.put("lLstPrDistricts", lLstPrDistricts);
		
							lObjPnsnProcPnsnrDtlsVO.setBankAccountNo(lObjPnsnProcPnsnrDtlsDAO.getBankAccNo(lLngEmpId));
		
							inputMap.put("lObjTrnPnsnProcPnsnrDtlsVO", lObjPnsnProcPnsnrDtlsVO);
		
							lLngPayScaleId = lObjPnsnprocPnsnrpayDAO.getPayScaleDtls(lLngEmpId);
							lLngBasicPay = lObjPnsnprocPnsnrpayDAO.getBasicPay(lLngEmpId);
		
							if (lLngBasicPay != null && lLngBasicPay != 0L)
								lBigDecBasicPay = new BigDecimal(lLngBasicPay);
		
							lObjPnsnprocPnsnrpayVO.setLastPayScale(lLngPayScaleId);
							lObjPnsnprocPnsnrpayVO.setBasicPay(lBigDecBasicPay);
		
							String lStrFormYearMonth = null;
							String lStrToYearMonth = null;
							List<Object[]> lLstAvgPayDtls = new ArrayList();
							List<TrnPnsnprocAvgPayCalc> lLst = new ArrayList<TrnPnsnprocAvgPayCalc>();
							if (lDtRetirement != null) {
								cal.setTime(lDtRetirement);
								cal.add(Calendar.MONTH, -9);
								cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		
								if (lDtRetirement.getMonth() + 1 < 10) {
									lStrToYearMonth = sdfYYYY.format(lDtRetirement) + "0" + (lDtRetirement.getMonth() + 1);
								} else {
									lStrToYearMonth = sdfYYYY.format(lDtRetirement) + "" + (lDtRetirement.getMonth() + 1);
								}
		
								if (cal.get(Calendar.MONTH) + 1 < 10) {
									lStrFormYearMonth = cal.get(Calendar.YEAR) + "0" + (cal.get(Calendar.MONTH) + 1);
								} else {
									lStrFormYearMonth = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1);
								}
		
								lLstAvgPayDtls = lObjPnsnprocPnsnrpayDAO.getAvgPayDtls(lLngEmpId, lStrFormYearMonth, lStrToYearMonth, lObjMstEmp.getPayCommission());
								TrnPnsnprocAvgPayCalc lObjPnsnprocAvgPayCalcVO = null;
								Map<Integer, Object> lMapYearMonth = new LinkedHashMap<Integer, Object>();
								Map<Integer, Object[]> lMapResult = new LinkedHashMap<Integer, Object[]>();
								Calendar lCalFrom = Calendar.getInstance();
								String lStrDtMonth;
								String lStrDtYear;
								Integer lIntYear = cal.get(Calendar.YEAR);
								Integer lIntMonth = cal.get(Calendar.MONTH) + 1;
								String lArrStrYearMonth[] = new String[10];
								String lStrYearMonth = null;
								Object[] lArrObjFinal = null;
								Date lDtFromDate = null;
								
								if (lLstAvgPayDtls != null && !lLstAvgPayDtls.isEmpty()) {
									for (Integer lInt = 0; lInt < 10; lInt++) {
		
										if (lIntMonth == 13) {
											lIntYear = lIntYear + 1;
											lIntMonth = 1;
										}
		
										if (lIntMonth < 10) {
											lArrStrYearMonth[lInt] = lIntYear + "0" + lIntMonth;
										} else {
											lArrStrYearMonth[lInt] = lIntYear + "" + lIntMonth;
										}
										lMapYearMonth.put(Integer.parseInt(lArrStrYearMonth[lInt]), 0);
		
										lIntMonth++;
									}
		
									for (Object[] lArrObj : lLstAvgPayDtls) {
										lStrYearMonth = (String) lArrObj[0];
										lMapResult.put(Integer.parseInt(lStrYearMonth), lArrObj);
									}
									
									for (Integer lIntMapYearMonth : lMapYearMonth.keySet()) {
										lArrObjFinal = new Object[5];
										lArrObjFinal = lMapResult.get(lIntMapYearMonth);
										lObjPnsnprocAvgPayCalcVO = new TrnPnsnprocAvgPayCalc();
										
										lStrDtYear = lIntMapYearMonth.toString().substring(0, 4);
										lStrDtMonth = lIntMapYearMonth.toString().substring(4, 6);
										
										lCalFrom.set(Calendar.DATE, 1);
										lCalFrom.set(Calendar.MONTH, Integer.parseInt(lStrDtMonth) - 1);
										lCalFrom.set(Calendar.YEAR, Integer.parseInt(lStrDtYear));
		
										lDtFromDate = lCalFrom.getTime();
		
										if (lArrObjFinal != null) {
											BigDecimal lBdBasic = new BigDecimal((BigInteger) lArrObjFinal[1]);
											BigDecimal lBDDp = new BigDecimal((BigInteger) lArrObjFinal[2]);
											BigDecimal lBDNpa = new BigDecimal((BigInteger) lArrObjFinal[3]);
		
											Long lLngBasic = lBdBasic.longValue();
											Long lLngDp = lBDDp.longValue();
											Long lLngNpa = lBDNpa.longValue();
		
											BigDecimal lBDTotal = new BigDecimal(lLngBasic + lLngDp + lLngNpa);
		
											lObjPnsnprocAvgPayCalcVO.setFromDate(lDtFromDate);
											lObjPnsnprocAvgPayCalcVO.setBasic(lBdBasic);
											lObjPnsnprocAvgPayCalcVO.setDp(lBDDp);
											lObjPnsnprocAvgPayCalcVO.setNpa(lBDNpa);
											lObjPnsnprocAvgPayCalcVO.setTotal(lBDTotal);
		
										} else {
											lObjPnsnprocAvgPayCalcVO.setFromDate(lDtFromDate);
											lObjPnsnprocAvgPayCalcVO.setBasic(BigDecimal.ZERO);
											lObjPnsnprocAvgPayCalcVO.setDp(BigDecimal.ZERO);
											lObjPnsnprocAvgPayCalcVO.setNpa(BigDecimal.ZERO);
											lObjPnsnprocAvgPayCalcVO.setTotal(BigDecimal.ZERO);
										}
										lLst.add(lObjPnsnprocAvgPayCalcVO);
									}
								}else{
									for (Integer lInt = 0; lInt < 10; lInt++) {
		
										if (lIntMonth == 13) {
											lIntYear = lIntYear + 1;
											lIntMonth = 1;
										}
		
										if (lIntMonth < 10) {
											lArrStrYearMonth[lInt] = lIntYear + "0" + lIntMonth;
										} else {
											lArrStrYearMonth[lInt] = lIntYear + "" + lIntMonth;
										}
										lMapYearMonth.put(Integer.parseInt(lArrStrYearMonth[lInt]), 0);
		
										lIntMonth++;
									}
									
									for (Integer lIntMapYearMonth : lMapYearMonth.keySet()) {
										
										lObjPnsnprocAvgPayCalcVO = new TrnPnsnprocAvgPayCalc();
										lStrDtYear = lIntMapYearMonth.toString().substring(0, 4);
										lStrDtMonth = lIntMapYearMonth.toString().substring(4, 6);
										
										lCalFrom.set(Calendar.DATE, 1);
										lCalFrom.set(Calendar.MONTH, Integer.parseInt(lStrDtMonth) - 1);
										lCalFrom.set(Calendar.YEAR, Integer.parseInt(lStrDtYear));
										
										lDtFromDate = lCalFrom.getTime();
										lObjPnsnprocAvgPayCalcVO.setFromDate(lDtFromDate);
										lLst.add(lObjPnsnprocAvgPayCalcVO);
									}
									
								
									
								}
							}
							inputMap.put("lLstTrnPnsnprocAvgPayCalcVO", lLst);
							inputMap.put("lObjTrnPnsnprocPnsnrpayVO", lObjPnsnprocPnsnrpayVO);
							
							lLstEmpNomineeDtls = lObjTrnPnsnProcInwardPensionDAO.getEmpNomineeDtls(lObjMstEmp.getDcpsEmpId());
							Object lObjPnsnprocNomineedtls[] = null;
							MstEmpNmn lObjMstEmpNmn = new MstEmpNmn();
							String lStrRelation = null;
							if(!lLstEmpNomineeDtls.isEmpty()){
								for(Integer lInt= 0;lInt<lLstEmpNomineeDtls.size();lInt++){
									lObjPnsnprocNomineedtls = new Object[8];
									lObjMstEmpNmn = lLstEmpNomineeDtls.get(lInt);
									lObjPnsnprocNomineedtls[1] = lObjMstEmpNmn.getName();
									lObjPnsnprocNomineedtls[2] = lObjMstEmpNmn.getShare();								
									lStrRelation = lObjMstEmpNmn.getRlt();
									
									if(lStrRelation.equals("700011"))
										lObjPnsnprocNomineedtls[7] = "Father";
									else if(lStrRelation.equals("700012"))
										lObjPnsnprocNomineedtls[7] = "Mother";
									else if(lStrRelation.equals("700013"))
										lObjPnsnprocNomineedtls[7] = "Brother";
									else if(lStrRelation.equals("700226"))
										lObjPnsnprocNomineedtls[7] = "Husband";
									else if(lStrRelation.equals("700227"))
										lObjPnsnprocNomineedtls[7] = "Wife";
									else if(lStrRelation.equals("700228"))
										lObjPnsnprocNomineedtls[7] = "Son";
									else if(lStrRelation.equals("700229"))
										lObjPnsnprocNomineedtls[7] = "Daughter";
									else if(lStrRelation.equals("700230"))
										lObjPnsnprocNomineedtls[7] = "Other";								
									
									lLstPnsnprocNomineeDtls.add(lObjPnsnprocNomineedtls);
								}
								inputMap.put("lLstPnsnrNomineeDtls", lLstPnsnprocNomineeDtls);
							}
		
							resObj.setViewName("pensionCaseInwardForm");
		
						} else {
							inputMap.put("alertMessage", "InvalidEmp");
							inputMap.put("SevaarthId", lStrSevaarthId.toUpperCase());
							inputMap.put("pensionType", lStrPensionType);
							resObj.setViewName("PensionEmpSearch");
						}
					}else{
						lObjPnsnProcInwardPensionVO.setPensionType(lStrPensionType);
						lObjPnsnProcInwardPensionVO.setSevaarthId(lStrSevaarthId);
						lObjPnsnProcInwardPensionVO.setCaseType("NEW");
						inputMap.put("lObjTrnPnsnProcInwardPensionVO", lObjPnsnProcInwardPensionVO);
						resObj.setViewName("pensionCaseInwardForm");
					}
				}else if(lFlagForRevision.equals("Y")){				
					TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = null;
					TrnPnsnProcPnsnrDtls lObjTrnPnsnProcPnsnrDtlsVO = null;
					TrnPnsnprocPnsnrpay lObjTrnPnsnprocPnsnrpayVO = null;
					TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalcVO = null;
					TrnPnsnProcRecovery lObjTrnPnsnProcRecoveryVO = null;
					TrnPnsnProcAdvnceBal lObjTrnPnsnProcAdvnceBalVO = null;
					TrnPnsnProcAssesdDues lObjTrnPnsnProcAssesdDuesVO = null;
					TrnPnsnProcCheckList lObjTrnPnsnProcCheckListVO = null;
					List<TrnPnsnprocEventdtls> lLstTrnPnsnprocEventdtls = new ArrayList<TrnPnsnprocEventdtls>();
					List<TrnPnsnprocPnsnrservcbreak> lLstTrnPnsnprocPnsnrservcbreak = new ArrayList<TrnPnsnprocPnsnrservcbreak>();
					List<TrnPnsnprocAvgPayCalc> lLstTrnPnsnprocAvgPayCalcVO = new ArrayList<TrnPnsnprocAvgPayCalc>();
					List<TrnPnsnProcAdvnceBal> lLstTrnPnsnProcAdvnceBal = new ArrayList<TrnPnsnProcAdvnceBal>();
					List<TrnPnsnProcAssesdDues> lLstTrnPnsnProcAssesdDues = new ArrayList<TrnPnsnProcAssesdDues>();
					List<TrnPnsnProcCheckList> lLstTrnPnsnProcCheckList = new ArrayList<TrnPnsnProcCheckList>();
					List<TrnPnsnprocFamilydtls> lLstPnsnrFamilyDtls = new ArrayList<TrnPnsnprocFamilydtls>();
					List<TrnPnsnprocNomineedtls> lLstPnsnrNomineeDtls = new ArrayList<TrnPnsnprocNomineedtls>();
					List<TrnPnsnprocProvisionalPaid> lLstTrnPnsnprocProvisionalPaid = new ArrayList<TrnPnsnprocProvisionalPaid>();
					Long lLngPhotoAttachmentId = 0l;
					Long lLngSignAttachmentId = 0l;
					List<ComboValuesVO> lLstPrDistricts = null;
					List<ComboValuesVO> lLstArDistricts = null;
					List<ComboValuesVO> lLstOffDistricts = null;
	
					lStrShowReadOnly = StringUtility.getParameter("showReadOnly", request);
					lStrStatusLookupId = StringUtility.getParameter("statusLookupId", request);
	
					lObjTrnPnsnProcInwardPensionVO = (TrnPnsnProcInwardPension) lObjTrnPnsnProcInwardPensionDAO.read(lLngInwardId);
					lObjTrnPnsnProcInwardPensionVO.setCaseType("REVISION");
					inputMap.put("lObjTrnPnsnProcInwardPensionVO", lObjTrnPnsnProcInwardPensionVO);
					if (lObjTrnPnsnProcInwardPensionVO.getPayCommission().equals("5THPAYCOMSN")) {
						scaleList = gdDao.getScalefromDsgnComm(2500340L);
					} else {
						scaleList = gdDao.getScalefromDsgnComm(2500341L);
					}
	
					lObjTrnPnsnProcInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcPnsnrDtls.class, serv.getSessionFactory());
					lObjTrnPnsnProcPnsnrDtlsVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrDtlsVO(lLngInwardId);
					inputMap.put("lObjTrnPnsnProcPnsnrDtlsVO", lObjTrnPnsnProcPnsnrDtlsVO);
	
					if (lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrStateCode() != null) {
						lLstPrDistricts = lObjCmnPensionDAO.getDistrictsOfState(lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrStateCode(), gLngLangId);
					}
					if (lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrRetStateCode() != null) {
						lLstArDistricts = lObjCmnPensionDAO.getDistrictsOfState(lObjTrnPnsnProcPnsnrDtlsVO.getPnsnrAddrRetStateCode(), gLngLangId);
					}
					if (lObjTrnPnsnProcPnsnrDtlsVO.getOfficeStateCode() != null) {
						lLstOffDistricts = lObjCmnPensionDAO.getDistrictsOfState(lObjTrnPnsnProcPnsnrDtlsVO.getOfficeStateCode(), gLngLangId);
					}
	
					// get photo and signature dtls
					if (lObjTrnPnsnProcPnsnrDtlsVO != null) {
						Long lLngSrNo = 0L;
						CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
						CmnAttachmentMst lObjCmnAttachmentMst = null;
						if (lObjTrnPnsnProcPnsnrDtlsVO.getPhotoAttachmentId() != null && lObjTrnPnsnProcPnsnrDtlsVO.getPhotoAttachmentId().doubleValue() > 0) {
							lObjCmnAttachmentMst = new CmnAttachmentMst();
							lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lObjTrnPnsnProcPnsnrDtlsVO.getPhotoAttachmentId().toString()));
	
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
	
						if (lObjTrnPnsnProcPnsnrDtlsVO.getSignatureAttachmentId() != null && lObjTrnPnsnProcPnsnrDtlsVO.getSignatureAttachmentId().doubleValue() > 0) {
							lObjCmnAttachmentMst = new CmnAttachmentMst();
							lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lObjTrnPnsnProcPnsnrDtlsVO.getSignatureAttachmentId().toString()));
	
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
	
					// read vo of family details
	
					TrnPnsnprocFamilydtlsDAO lObjTrnPnsnprocFamilydtlsDAO = new TrnPnsnprocFamilydtlsDAOImpl(TrnPnsnprocFamilydtls.class, serv.getSessionFactory());
					lLstPnsnrFamilyDtls = lObjTrnPnsnprocFamilydtlsDAO.getListOfPnsnrFamilyDtls(lLngInwardId);
					inputMap.put("lLstPnsnrFamilyDtls", lLstPnsnrFamilyDtls);
	
					// read vo of nominee details
					TrnPnsnprocNomineedtlsDAO lObjTrnPnsnprocNomineedtlsDAO = new TrnPnsnprocNomineedtlsDAOImpl(TrnPnsnprocNomineedtls.class, serv.getSessionFactory());
					lLstPnsnrNomineeDtls = lObjTrnPnsnprocNomineedtlsDAO.getListOfPnsnrNomineeDtls(lLngInwardId);
					inputMap.put("lLstPnsnrNomineeDtls", lLstPnsnrNomineeDtls);
	
					lObjTrnPnsnprocPnsnrpayVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrPayVO(lLngInwardId);
	
					inputMap.put("lObjTrnPnsnprocPnsnrpayVO", lObjTrnPnsnprocPnsnrpayVO);
	
					lLstTrnPnsnprocAvgPayCalcVO = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAvgPayCalcDtls(lLngInwardId);
					inputMap.put("lLstTrnPnsnprocAvgPayCalcVO", lLstTrnPnsnprocAvgPayCalcVO);
	
					lLstTrnPnsnprocEventdtls = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseEventDtls(lLngInwardId);
					inputMap.put("lLstTrnPnsnprocEventdtls", lLstTrnPnsnprocEventdtls);
	
					lLstTrnPnsnprocProvisionalPaid = lObjTrnPnsnProcInwardPensionDAO.getPnsnprocProvisionalPaidDtls(lLngInwardId);
					inputMap.put("lLstTrnPnsnprocProvisionalPaid", lLstTrnPnsnprocProvisionalPaid);
	
					lLstTrnPnsnprocPnsnrservcbreak = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseSrvcBrkDtls(lLngInwardId);
					inputMap.put("lLstTrnPnsnprocPnsnrservcbreak", lLstTrnPnsnprocPnsnrservcbreak);
	
					lObjTrnPnsnProcPnsnCalcVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnCalcVO(lLngInwardId);
					inputMap.put("lObjTrnPnsnProcPnsnCalcVO", lObjTrnPnsnProcPnsnCalcVO);
	
					lObjTrnPnsnProcRecoveryVO = lObjTrnPnsnProcInwardPensionDAO.getPnsnrRecoveryVO(lLngInwardId);
					inputMap.put("lObjTrnPnsnProcRecoveryVO", lObjTrnPnsnProcRecoveryVO);
	
					lLstTrnPnsnProcAdvnceBal = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAdvnceBalDtls(lLngInwardId);
					Iterator<TrnPnsnProcAdvnceBal> it1 = lLstTrnPnsnProcAdvnceBal.iterator();
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnProcAdvnceBal.size(); lIntCnt++) {
						lObjTrnPnsnProcAdvnceBalVO = it1.next();
						inputMap.put("lObjTrnPnsnProcAdvnceBalVO", lObjTrnPnsnProcAdvnceBalVO);
					}
					inputMap.put("lLstTrnPnsnProcAdvnceBal", lLstTrnPnsnProcAdvnceBal);
	
					lLstTrnPnsnProcAssesdDues = lObjTrnPnsnProcInwardPensionDAO.getPensionCaseAssesdDueDtls(lLngInwardId);
					Iterator<TrnPnsnProcAssesdDues> it2 = lLstTrnPnsnProcAssesdDues.iterator();
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnProcAssesdDues.size(); lIntCnt++) {
						lObjTrnPnsnProcAssesdDuesVO = it2.next();
						inputMap.put("lObjTrnPnsnProcAssesdDuesVO", lObjTrnPnsnProcAssesdDuesVO);
					}
					inputMap.put("lLstTrnPnsnProcAssesdDues", lLstTrnPnsnProcAssesdDues);
	
					lLstTrnPnsnProcCheckList = lObjTrnPnsnProcInwardPensionDAO.getPnsnrCheklistVO(lLngInwardId);
					Iterator<TrnPnsnProcCheckList> it3 = lLstTrnPnsnProcCheckList.iterator();
					for (int lIntCnt = 0; lIntCnt < lLstTrnPnsnProcCheckList.size(); lIntCnt++) {
						lObjTrnPnsnProcCheckListVO = it3.next();
						inputMap.put("lObjTrnPnsnProcCheckListVO", lObjTrnPnsnProcCheckListVO);
					}
					if (lObjTrnPnsnProcCheckListVO != null) {
						cmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
						if (lObjTrnPnsnProcCheckListVO.getCertificateAttachId() != null && lObjTrnPnsnProcCheckListVO.getCertificateAttachId().doubleValue() > 0) {
							cmnAttachmentMst = new CmnAttachmentMst();
							cmnAttachmentMst = cmnAttachmentMstDAO.findByAttachmentId(Long.parseLong(lObjTrnPnsnProcCheckListVO.getCertificateAttachId().toString()));
							inputMap.put("scan", cmnAttachmentMst);
						}
					}
					inputMap.put("lLstTrnPnsnProcCheckList", lLstTrnPnsnProcCheckList);
					if (lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.APPROVEDBYDDOSTATUSID")) 
							|| lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.SENDTOAGSTATUSID"))
							|| lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.MOVEFORDDOMISTAKESTATUSID")) 
							|| lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.MOVEFORAGQUERYSTATUSID"))
							|| lStrStatusLookupId.equals(gObjRsrcBndle.getString("PPROC.APPROVEDBYAGSTATUSID"))) {
						lStrShowReadOnly = "Y";
						inputMap.put("lStrStatusLookupId", lStrStatusLookupId);
					}
					if (lStrShowReadOnly.equals("Y")) {
						inputMap.put("lStrShowReadOnly", lStrShowReadOnly);
					}
					lLstAdminDept = lObjCmnPensionDAO.getAdminDept();
					
					Long lLngAdminDept =  lObjCmnPensionDAO.getAdminDeptNameFromFieldDept(lObjTrnPnsnProcPnsnrDtlsVO.getDepartmentId());
					
					lLstDepartment = lObjCmnPensionDAO.getFieldDeptFromAdmDept(lLngAdminDept);
					inputMap.put("lLstDepartment", lLstDepartment);
					
					lStrBankName = lObjTrnPnsnProcPnsnrDtlsVO.getBankName();
	
					lStrLocationCode = lObjTrnPnsnProcInwardPensionDAO.getLocCodeFromDDO(SessionHelper.getLocationCode(inputMap));
					if (lStrBankName != null) {
						lLstBankBranch = lObjCommonDAO.getBranchListFromBankCode(Long.parseLong(lStrBankName), lStrLocationCode, gLngLangId);
					}
					inputMap.put("lLstBankBranch", lLstBankBranch);
					inputMap.put("lLstHOOFrmDept", lLstAdminDept);
					inputMap.put("lLstPrDistricts", lLstPrDistricts);
					inputMap.put("lLstArDistricts", lLstArDistricts);
					inputMap.put("lLstOffDistricts", lLstOffDistricts);
	
					resObj.setViewName("pensionCaseInwardForm");
	
				}
				ComboValuesVO lObjComboValuesVO = null;
				List lLstReturnList = null;
				if ((scaleList != null) && (scaleList.size() > 0)) {
					lLstReturnList = new ArrayList();
	
					for (int k = 0; k < scaleList.size(); ++k) {
						hrEisScaleMst = (HrEisScaleMst) scaleList.get(k);
	
						if (hrEisScaleMst != null) {
							sgdId = hrEisScaleMst.getScaleId();
	
							StringBuffer scaleDisp = new StringBuffer("");
	
							if ((hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500341L) || (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500342L)
									|| (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500343L) || (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500344L)
									|| (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500345L)) {
								scaleDisp.append(hrEisScaleMst.getScaleStartAmt());
	
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleEndAmt());
								scaleDisp.append(" (");
								scaleDisp.append(hrEisScaleMst.getScaleGradePay());
								scaleDisp.append(")");
							} else {
								scaleDisp.append(hrEisScaleMst.getScaleStartAmt());
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleIncrAmt());
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleEndAmt());
								if ((hrEisScaleMst.getScaleHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleHigherEndAmt() > 0L)) {
									scaleDisp.append("-");
									scaleDisp.append(hrEisScaleMst.getScaleHigherIncrAmt());
									scaleDisp.append("-");
									scaleDisp.append(hrEisScaleMst.getScaleHigherEndAmt());
									if ((hrEisScaleMst.getScaleSecondHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleSecondHigherEndAmt() > 0L)) {
										scaleDisp.append("-");
										scaleDisp.append(hrEisScaleMst.getScaleSecondHigherIncrAmt());
										scaleDisp.append("-");
										scaleDisp.append(hrEisScaleMst.getScaleSecondHigherEndAmt());
	
										if ((hrEisScaleMst.getScaleThirdHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleThirdHigherEndAmt() > 0L)) {
											scaleDisp.append("-");
											scaleDisp.append(hrEisScaleMst.getScaleThirdHigherIncrAmt());
											scaleDisp.append("-");
											scaleDisp.append(hrEisScaleMst.getScaleThirdHigherEndAmt());
										}
									}
								}
							}
							lObjComboValuesVO = new ComboValuesVO();
							lObjComboValuesVO.setId(Long.valueOf(hrEisScaleMst.getScaleId()).toString());
							lObjComboValuesVO.setDesc(scaleDisp.toString());
							lLstReturnList.add(lObjComboValuesVO);
						}
					}
	
					inputMap.put("PayScaleList", lLstReturnList);
				}
				if ((lLst6thPayScale != null) && (lLst6thPayScale.size() > 0)) {
					lLstReturnList = new ArrayList();
	
					for (int k = 0; k < lLst6thPayScale.size(); ++k) {
						hrEisScaleMst = (HrEisScaleMst) lLst6thPayScale.get(k);
	
						if (hrEisScaleMst != null) {
							sgdId = hrEisScaleMst.getScaleId();
	
							StringBuffer scaleDisp = new StringBuffer("");
	
							if ((hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500341L) || (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500342L)
									|| (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500343L) || (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500344L)
									|| (hrEisScaleMst.getHrPayCommissionMst().getId().longValue() == 2500345L)) {
								scaleDisp.append(hrEisScaleMst.getScaleStartAmt());
	
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleEndAmt());
								scaleDisp.append(" (");
								scaleDisp.append(hrEisScaleMst.getScaleGradePay());
								scaleDisp.append(")");
							} else {
								scaleDisp.append(hrEisScaleMst.getScaleStartAmt());
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleIncrAmt());
								scaleDisp.append("-");
								scaleDisp.append(hrEisScaleMst.getScaleEndAmt());
								if ((hrEisScaleMst.getScaleHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleHigherEndAmt() > 0L)) {
									scaleDisp.append("-");
									scaleDisp.append(hrEisScaleMst.getScaleHigherIncrAmt());
									scaleDisp.append("-");
									scaleDisp.append(hrEisScaleMst.getScaleHigherEndAmt());
									if ((hrEisScaleMst.getScaleSecondHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleSecondHigherEndAmt() > 0L)) {
										scaleDisp.append("-");
										scaleDisp.append(hrEisScaleMst.getScaleSecondHigherIncrAmt());
										scaleDisp.append("-");
										scaleDisp.append(hrEisScaleMst.getScaleSecondHigherEndAmt());
	
										if ((hrEisScaleMst.getScaleThirdHigherIncrAmt() > 0L) && (hrEisScaleMst.getScaleThirdHigherEndAmt() > 0L)) {
											scaleDisp.append("-");
											scaleDisp.append(hrEisScaleMst.getScaleThirdHigherIncrAmt());
											scaleDisp.append("-");
											scaleDisp.append(hrEisScaleMst.getScaleThirdHigherEndAmt());
										}
									}
								}
							}
							lObjComboValuesVO = new ComboValuesVO();
							lObjComboValuesVO.setId(Long.valueOf(hrEisScaleMst.getScaleId()).toString());
							lObjComboValuesVO.setDesc(scaleDisp.toString());
							lLstReturnList.add(lObjComboValuesVO);
						}
					}
	
					inputMap.put("PayScaleList6th", lLstReturnList);
				}
			}else{
				inputMap.put("alertMessage", "ReqPending");
				inputMap.put("SevaarthId", lStrSevaarthId.toUpperCase());
				inputMap.put("pensionType", lStrPensionType);
				resObj.setViewName("PensionEmpSearch");
			}
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
		}
		catch (Exception e) {

			gLogger.error("Error in loadPenProcInwardForm:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject getIfscCodeFromBranchCode(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		Long lLngBranchCode = null;
		String lStrIfscCode = null;
		StringBuilder lStrBldXML = new StringBuilder();

		try {
			setSessionInfo(inputMap);
			CommonPensionDAO lObjCommonDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			if (!StringUtility.getParameter("branchCode", request).equals("-1")) {
				lLngBranchCode = Long.parseLong(StringUtility.getParameter("branchCode", request).trim());
				lStrIfscCode = lObjCommonDAO.getIfscCodeFromBranchCode(lLngBranchCode, gStrLocId);
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

	
	public ResultObject updateCaseStatus(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		String lStrUpdateFlag = null;
		String lStrInwardId = null;
		String lStrOutwardNo = null;
		String lStrOutwardDate = null;
		String lStrFlag = "false";
		StringBuilder lStrBldXML = new StringBuilder();

		try {
			setSessionInfo(inputMap);
			
			TrnPnsnProcInwardPensionDAO lObjInwardPensionDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class,serv.getSessionFactory());
			TrnPnsnProcInwardPension lObjInwardPensionVO = new TrnPnsnProcInwardPension();
			
			lStrUpdateFlag = StringUtility.getParameter("updateFlag", request);
			lStrInwardId = StringUtility.getParameter("InwardId", request);
			lStrOutwardNo = StringUtility.getParameter("outwardNoForAg", request);
			lStrOutwardDate = StringUtility.getParameter("outwardDateForAg", request);
			
			String lStrSendToAg = gObjRsrcBndle.getString("PPROC.SENDTOAGSTATUSID");
			String lStrMistakeByDdo = gObjRsrcBndle.getString("PPROC.MOVEFORDDOMISTAKESTATUSID");
			String lStrQuery = gObjRsrcBndle.getString("PPROC.MOVEFORAGQUERYSTATUSID");
			
			String[] lStrArrInwardId = lStrInwardId.split(",");
			String[] lStrArrOutwardNo = lStrOutwardNo.split(",");
			String[] lStrArrOutwardDate = lStrOutwardDate.split(",");		
			
			if(lStrUpdateFlag.equals("S")){
				for(Integer lInt=0;lInt<lStrArrInwardId.length;lInt++){
					if(!"".equals(lStrArrInwardId[lInt])){
						lObjInwardPensionVO = (TrnPnsnProcInwardPension) lObjInwardPensionDAO.read(Long.parseLong(lStrArrInwardId[lInt]));
						
						lObjInwardPensionVO.setCaseStatus(lStrSendToAg);
						lObjInwardPensionVO.setOutwardNo(lStrArrOutwardNo[lInt]);
						if(!"".equals(lStrArrOutwardDate[lInt])){
							lObjInwardPensionVO.setOutwardDate(lObjDateFormat.parse(lStrArrOutwardDate[lInt]));
						}
						lObjInwardPensionDAO.update(lObjInwardPensionVO);
					}
				}
				lStrFlag = "true";
			}else if(lStrUpdateFlag.equals("M")){
				for(Integer lInt=0;lInt<lStrArrInwardId.length;lInt++){
					if(!"".equals(lStrArrInwardId[lInt])){
						lObjInwardPensionVO = (TrnPnsnProcInwardPension) lObjInwardPensionDAO.read(Long.parseLong(lStrArrInwardId[lInt]));
						lObjInwardPensionVO.setCaseStatus(lStrMistakeByDdo);
						lObjInwardPensionDAO.update(lObjInwardPensionVO);
					}
				}
				lStrFlag = "true";
			}else if(lStrUpdateFlag.equals("Q")){
				for(Integer lInt=0;lInt<lStrArrInwardId.length;lInt++){
					if(!"".equals(lStrArrInwardId[lInt])){
						lObjInwardPensionVO = (TrnPnsnProcInwardPension) lObjInwardPensionDAO.read(Long.parseLong(lStrArrInwardId[lInt]));
						lObjInwardPensionVO.setCaseStatus(lStrQuery);
						lObjInwardPensionDAO.update(lObjInwardPensionVO);
					}
				}
				lStrFlag = "true";
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<FLAG>" + lStrFlag);
			lStrBldXML.append("</FLAG>");			
			lStrBldXML.append("</XMLDOC>");
			
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
	public ResultObject approvePensionCaseForAg(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		TrnPnsnProcInwardPensionDAO lObjTrnPnsnProcInwardDAO = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			setSessionInfo(inputMap);
			lObjTrnPnsnProcInwardDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcInwardPension.class, serv.getSessionFactory());
			setSessionInfo(inputMap);			
			String lStrCPONo = "";
			String lStrGPONo = "";
			String lStrCPODate = "";
			String lStrGPODate = "";
			Date lDtPpoDate = null;
			String lStrPpoNo = "";
			String lStrPpoDate = "";
			
			String lStrPnsnCaseId = StringUtility.getParameter("pensionCaseId", request).toString().trim();			
			Long lLngPensionCaseId = Long.parseLong(lStrPnsnCaseId);
			
			TrnPnsnProcPnsnCalc lObjTrnPnsnProcPnsnCalcVO = lObjTrnPnsnProcInwardDAO.getPnsnCalcVO(lLngPensionCaseId);
			TrnPnsnProcInwardPension lObjTrnPnsnProcInwardPensionVO = (TrnPnsnProcInwardPension) lObjTrnPnsnProcInwardDAO.read(lLngPensionCaseId);
			TrnPnsnProcRevision lObjPnsnProcRevision = null;
			
			if(lObjTrnPnsnProcInwardPensionVO.getCaseType().equals("REVISION")){
				Long lLngRevisionId = lObjTrnPnsnProcInwardDAO.getPnsnRevisionId(lObjTrnPnsnProcInwardPensionVO.getSevaarthId());				
				lObjTrnPnsnProcInwardDAO = new TrnPnsnProcInwardPensionDAOImpl(TrnPnsnProcRevision.class, serv.getSessionFactory());
				lObjPnsnProcRevision = (TrnPnsnProcRevision) lObjTrnPnsnProcInwardDAO.read(lLngRevisionId);
							
				lObjTrnPnsnProcPnsnCalcVO.setCvpAmnt(lObjPnsnProcRevision.getCvpAmnt());
				lObjTrnPnsnProcPnsnCalcVO.setCvpMonthAmnt(lObjPnsnProcRevision.getCvpMonthAmnt());
				lObjTrnPnsnProcPnsnCalcVO.setPensionerTotalAmnt(lObjPnsnProcRevision.getPensionerTotalAmnt());
				lObjTrnPnsnProcPnsnCalcVO.setPensionerReducedAmnt(lObjPnsnProcRevision.getPensionerReducedAmnt());
				lObjTrnPnsnProcPnsnCalcVO.setSrvcGratuityAmnt(lObjPnsnProcRevision.getSrvcGratuityAmnt());
				lObjTrnPnsnProcPnsnCalcVO.setDcrgAmnt(lObjPnsnProcRevision.getDcrgAmnt());
				lObjTrnPnsnProcPnsnCalcVO.setFp1Amnt(lObjPnsnProcRevision.getFp1Amnt());
				lObjTrnPnsnProcPnsnCalcVO.setFp2Amnt(lObjPnsnProcRevision.getFp2Amnt());
				
				lObjPnsnProcRevision.setActiveFlag("N");
				lObjTrnPnsnProcInwardDAO.update(lObjPnsnProcRevision);			
			}
			lStrCPONo = StringUtility.getParameter("txtCpoNo", request);
			lStrGPONo = StringUtility.getParameter("txtGpoNo", request);
			
			lStrCPODate = StringUtility.getParameter("txtCpoDate", request);
			lStrGPODate = StringUtility.getParameter("txtGpoDate", request);
			
			lStrPpoNo = StringUtility.getParameter("txtPpoNo", request).trim();
			lStrPpoDate = StringUtility.getParameter("txtPpoDate", request).trim();			
			
			if (!"".equals(lStrPpoDate)) {
				lDtPpoDate = simpleDateFormat.parse(lStrPpoDate);
			}
			lObjTrnPnsnProcInwardPensionVO.setCaseStatus(gObjRsrcBndle.getString("PPROC.APPROVEDBYAGSTATUSID"));
			lObjTrnPnsnProcInwardPensionVO.setPpoNo(lStrPpoNo);
			lObjTrnPnsnProcInwardPensionVO.setPpoDate(lDtPpoDate);
			lObjTrnPnsnProcInwardDAO.update(lObjTrnPnsnProcInwardPensionVO);
			
			if (!"".equals(lStrCPONo) && lStrCPONo.length() > 0) {
				lObjTrnPnsnProcPnsnCalcVO.setCpoNo(lStrCPONo.trim());
			}
			if (!"".equals(lStrGPONo) && lStrGPONo.length() > 0) {
				lObjTrnPnsnProcPnsnCalcVO.setGpoNo(lStrGPONo.trim());
			}
			if (!"".equals(lStrCPODate) && lStrCPODate.length() > 0) {
				lObjTrnPnsnProcPnsnCalcVO.setCpoDate(simpleDateFormat.parse(lStrCPODate));
			}
			if (!"".equals(lStrGPODate) && lStrGPODate.length() > 0) {
				lObjTrnPnsnProcPnsnCalcVO.setGpoDate(simpleDateFormat.parse(lStrGPODate));
			}
			lObjTrnPnsnProcInwardDAO.update(lObjTrnPnsnProcPnsnCalcVO);
			
			
			inputMap.put("ajaxKey", "Success");
			resObj.setViewName("ajaxData");
			resObj.setResultCode(ErrorConstants.SUCCESS);
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
