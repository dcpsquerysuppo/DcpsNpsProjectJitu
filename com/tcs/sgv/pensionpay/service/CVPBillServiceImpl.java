package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.util.StringUtils;

import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ReportExportHelper;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.DateUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.RltBillParty;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAO;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAOImpl;
import com.tcs.sgv.pensionpay.dao.MstPensionerHdrDAO;
import com.tcs.sgv.pensionpay.dao.MstPensionerHdrDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionBillDAO;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAO;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.web.jsp.tags.DateUtilities;


/**
 * CVP Bill Specific Service Impl.
 * 
 * @author Soumya Adhikari
 * @version 1.0
 */
public class CVPBillServiceImpl extends ServiceImpl implements CVPBillService {

	/* Global Variable for PostId */
	private Long gLngPostId = null;

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Glonal Variable for Location Code */
	private String gStrLocCode = null;

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for Current Date */
	private Date gDtCurrDt = null;

	/* Global Variable for Lang ID */
	private Long gLangID = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	/**
	 * Shows CVP Bill Data in bill
	 * 
	 * @param Map
	 *            :lMapInput
	 * @return ResultObject
	 */
	public ResultObject getCVPBillData(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = null;
		String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
		String lStrCaseStatus = "Identified";
		String lStrRcvryFlag = bundleConst.getString("RECOVERY.CVP");
		String lStrPPONo = null;
		String lStrPnsnerName = "";
		String lStrPnsrCode = null;
		String lStrOfficeAddr = null;
		String lStrDesgDesc = null;
		Double lCVPAmt = 0D;
		Double lNetAmt = 0D;
		Double lRecAmt = 0D;

		String lHeadcode = null;
		String lScheme = null;
		String lTypeFlag = null;
		String lStrTrnRqsHdrID = null;

		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, srvcLoc.getSessionFactory());
		long lIntNetAmt = 0;

		String lStrOrderNo = null;
		Date lDtOrderDate = null;
		String lStrVoucherNo = null;
		Date lDtVoucherDate = null;
		BigDecimal lBgDcmlBasicPensionAmt = null;
		BigDecimal lBgDcmlCvpMonthlyAmt = null;
		BigDecimal lBgDcmlReducedPensionAmt = null;
		String lStrCvpAuthority = "";
		Date lDtCvpStartDate = null;
		Date lDtCvpEndDate = null;

		try {
			setSessionInfo(inputMap);
			request = (HttpServletRequest) inputMap.get("requestObj");
			lStrPPONo = inputMap.get("PPONo").toString();
			List lArrDtls = null;
			lArrDtls = lObjTrnPensionRqstHdrDAO.getDtlsForCVPDCRGBill(lStrStatus, lStrCaseStatus, lStrRcvryFlag, lStrPPONo, gLangID, SessionHelper.getLocationCode(inputMap));

			if (!lArrDtls.isEmpty()) {
				/*
				 * if (lArrDtls.get(0) != null) { lStrPnsnerName = (String)
				 * lArrDtls.get(0); }
				 */
				if (lArrDtls.get(10) != null) {
					lStrPnsnerName = (String) lArrDtls.get(10);
				}
				/*
				 * if (lArrDtls.get(11) != null) { lStrPnsnerName =
				 * lStrPnsnerName + " " + (String) lArrDtls.get(11); } if
				 * (lArrDtls.get(12) != null) { lStrPnsnerName = lStrPnsnerName
				 * + " " + (String) lArrDtls.get(12); }
				 */

				lStrTrnRqsHdrID = lArrDtls.get(13).toString();
				lStrOfficeAddr = (String) lArrDtls.get(1);
				lStrDesgDesc = (String) lArrDtls.get(2);
				lStrPnsrCode = (String) lArrDtls.get(3);
				lScheme = (String) lArrDtls.get(4);
				lHeadcode = (lArrDtls.get(5)).toString();
				lTypeFlag = (String) lArrDtls.get(6);
				lCVPAmt = Double.parseDouble(((BigDecimal) lArrDtls.get(8)).toString());
				if (lArrDtls.get(9) != null) {
					lRecAmt = Double.parseDouble(((BigDecimal) lArrDtls.get(9)).toString());
				}
			}
			/*
			 * if(request.getParameter("auditFlag") != null &&
			 * "Y".equalsIgnoreCase
			 * (request.getParameter("auditFlag").toString())) {
			 * RltPensioncaseBillDAO lObjRltPnsncaseBillDao = new
			 * RltPensioncaseBillDAOImpl
			 * (RltPensioncaseBill.class,srvcLoc.getSessionFactory()); lCVPAmt =
			 * lObjRltPnsncaseBillDao
			 * .getAmountFromRltCaseBillByBillTypeAndMode(lStrPPONo, "Y", "CVP",
			 * "C",gStrLocCode); if(request.getParameter("CVPAmt") != null &&
			 * request.getParameter("CVPAmt").length() > 0) { lCVPAmt =
			 * Double.parseDouble(request.getParameter("CVPAmt").toString()); }
			 * } else { if("S".equals(lTypeFlag) || "C".equals(lTypeFlag)) {
			 * TrnPrvosionalPensionDtlsDaoImpl lObjProvDao = new
			 * TrnPrvosionalPensionDtlsDaoImpl
			 * (TrnPrvosionalPensionDtls.class,srvcLoc.getSessionFactory());
			 * TrnPrvosionalPensionDtls lObjProvVo =
			 * lObjProvDao.getProvisionalVo(lStrPnsrCode); if(lObjProvVo != null
			 * && lObjProvVo.getCvpAmount() != null) { lCVPAmt = lCVPAmt -
			 * Double.parseDouble(lObjProvVo.getCvpAmount().toString()); }
			 * TrnPensionRqstHdr lObjRqstHdrVo =
			 * lObjTrnPensionRqstHdrDAO.getTrnPensionRqstHdrDtlsFromPnsnerCode
			 * (lStrPPONo, "CONVERTED"); if(lObjRqstHdrVo != null &&
			 * lObjRqstHdrVo.getCvpAmount() != null) { lCVPAmt = lCVPAmt -
			 * Double.parseDouble(lObjRqstHdrVo.getCvpAmount().toString()); }
			 * if(request.getParameter("CVPAmt") != null &&
			 * request.getParameter("CVPAmt").length() > 0) { lCVPAmt =
			 * Double.parseDouble(request.getParameter("CVPAmt").toString()); }
			 * } }
			 */
			// Recovery Amount and Net Amount Calculation.
			lNetAmt = lCVPAmt;
			if (lRecAmt > 0D) {
				lNetAmt = lNetAmt - lRecAmt;
			}
			lIntNetAmt = Math.round(lNetAmt);

			// hidden fields like order no,voucher no etc.
			// String,Date,String,Date
			if (lArrDtls.get(16) != null) {
				lStrOrderNo = (String) lArrDtls.get(16);
			}
			if (lArrDtls.get(17) != null) {
				lDtOrderDate = (Date) lArrDtls.get(17);
			}
			if (lArrDtls.get(18) != null) {
				lStrVoucherNo = (String) lArrDtls.get(18);
			}
			if (lArrDtls.get(19) != null) {
				lDtVoucherDate = (Date) lArrDtls.get(19);
			}
			if (lArrDtls.get(20) != null) {
				lBgDcmlBasicPensionAmt = (BigDecimal) lArrDtls.get(20);
			}
			if (lArrDtls.get(21) != null) {
				lBgDcmlCvpMonthlyAmt = (BigDecimal) lArrDtls.get(21);
			}
			if (lArrDtls.get(22) != null) {
				lBgDcmlReducedPensionAmt = (BigDecimal) lArrDtls.get(22);
			}
			if (lArrDtls.get(23) != null) {
				lStrCvpAuthority = (String) lArrDtls.get(23);
			}
			if (lArrDtls.get(24) != null) {
				lDtCvpStartDate = (Date) lArrDtls.get(24);
			}
			if (lArrDtls.get(25) != null) {
				lDtCvpEndDate = (Date) lArrDtls.get(25);
			}
			
			inputMap.put("PPONo", lStrPPONo);
			inputMap.put("Name", lStrPnsnerName);
			inputMap.put("Designation", lStrDesgDesc);
			inputMap.put("OfficeAddr", lStrOfficeAddr);
			inputMap.put("CVPAmount", lCVPAmt);
			inputMap.put("RecoveryAmount", lRecAmt);
			inputMap.put("NetAmount", lIntNetAmt);
			inputMap.put("BillDate", gDtCurrDt);
			inputMap.put("PnsnrCode", lStrPnsrCode);
			inputMap.put("Headcode", lHeadcode);
			inputMap.put("Scheme", lScheme);
			inputMap.put("TrnRqsHdrID", lStrTrnRqsHdrID);

			inputMap.put("OrderNo", lStrOrderNo);
			inputMap.put("OrderDate", lDtOrderDate);
			inputMap.put("VoucherNo", lStrVoucherNo);
			inputMap.put("VoucherDate", lDtVoucherDate);
			inputMap.put("BasicPensionAmt", lBgDcmlBasicPensionAmt);
			inputMap.put("CvpMonthlyAmt", lBgDcmlCvpMonthlyAmt);
			inputMap.put("ReducedPensionAmt", lBgDcmlReducedPensionAmt);
			inputMap.put("CvpAuthority", lStrCvpAuthority);
			inputMap.put("CvpStartDate", lDtCvpStartDate);
			inputMap.put("CvpEndDate", lDtCvpEndDate);
			
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

	/**
	 * Saves CVP specific Bill Data in TRN_PENSION_BILL_DTLS and
	 * RLT_PENSIONCASE_BILL
	 * 
	 * @param Map
	 *            :lMapInput
	 * @return ResultObject
	 */
	public ResultObject saveCVPBill(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		long lLngRltPensioncaseBillId = 0;
		Long lLngBillNo = Long.parseLong(inputMap.get("billNo").toString());
		String lStrBillCntrlNo = (String) inputMap.get("PnsnBillCntrlNo");
		Long lPnsnTokenNo = (Long) inputMap.get("PnsnTokenNo");
		String lStrBillType = bundleConst.getString("RECOVERY.CVP");

		Long lLngCvpDtlsId = null;
		String lStrTrnPensionRqstHdrId = null;

		TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, serv.getSessionFactory());
		TrnPensionBillDtlsDAO lObjTrnPensionBillDtlsDAO = new TrnPensionBillDtlsDAOImpl(TrnPensionBillDtls.class, serv.getSessionFactory());
		RltPensioncaseBillDAO lObjRltPensioncaseBillDAO = new RltPensioncaseBillDAOImpl(RltPensioncaseBill.class, serv.getSessionFactory());
		// TrnPensionRecoveryDtlsDAO lObjTrnRecoveryDtls = new
		// TrnPensionRecoveryDtlsDAOImpl(TrnPensionRecoveryDtls.class,serv.getSessionFactory());
		TrnPensionRqstHdrDAO lObjRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstCommutationDtls.class, serv.getSessionFactory());
		TrnPensionBillHdr lObjTrnPensionBillHdr = null;
		TrnPensionBillDtls lObjTrnPensionBillDtls = null;
		RltPensioncaseBill lObjRltPensioncaseBill = null;
		HstCommutationDtls lObjHstCommutationDtlsVO = null;
		try {
			setSessionInfo(inputMap);
			// Getting VO Object from inputMap with the key
			// "CVPTrnPensionBillDtlsVO"
			lObjTrnPensionBillHdr = (TrnPensionBillHdr) inputMap.get("CVPTrnPensionBillHdrVO");
			lObjTrnPensionBillDtls = (TrnPensionBillDtls) inputMap.get("CVPTrnPensionBillDtlsVO");
			// Insert date into TrnPensuionBillHdr.
			lObjTrnPensionBillHdr.setBillType(lStrBillType);
			lObjTrnPensionBillHdr.setBillNo(lLngBillNo);
			lObjHstCommutationDtlsVO = (HstCommutationDtls) inputMap.get("CVPHstCommutationDtlsVO");
			if (lPnsnTokenNo != null && lPnsnTokenNo.toString().length() > 0) // If
			// FP
			// Auditor
			// Not
			// Enter
			// Token
			// No.
			{
				lObjTrnPensionBillHdr.setTokenNo(lPnsnTokenNo.toString());
			}

			lObjTrnPensionBillHdr.setTrnPensionBillHdrId(IFMSCommonServiceImpl.getNextSeqNum("trn_pension_bill_hdr", inputMap));
			lObjTrnPensionBillHdrDAO.create(lObjTrnPensionBillHdr);

			// Inserts new CVP Bill specific data into TRN_PENSION_BILL_DTLS
			// sets PK of TrnPensionBillDtls...
			lObjTrnPensionBillDtls.setTrnPensionBillDtlsId(IFMSCommonServiceImpl.getNextSeqNum("trn_pension_bill_dtls", inputMap));
			lObjTrnPensionBillDtls.setTrnPensionBillHdrId(lObjTrnPensionBillHdr.getTrnPensionBillHdrId());
			lObjTrnPensionBillDtlsDAO.create(lObjTrnPensionBillDtls);
			// Getting PK of RltPensioncaseBill from inputMap with the key
			// "CVPRltPensioncaseBillId"
			/*
			 * lLngRltPensioncaseBillId =
			 * Long.parseLong(inputMap.get("CVPRltPensioncaseBillId"
			 * ).toString()); if(lLngRltPensioncaseBillId !=0 &&
			 * lLngBillNo!=null) { //Updates CVP Bill specific data in
			 * RLT_PENSIONCASE_BILL lObjRltPensioncaseBill =
			 * lObjRltPensioncaseBillDAO.read(lLngRltPensioncaseBillId); //sets
			 * updated data... lObjRltPensioncaseBill.setBillNo(lLngBillNo);
			 * lObjRltPensioncaseBill.setBillCntrlNo(lStrBillCntrlNo);
			 * 
			 * if(lPnsnTokenNo != null && lPnsnTokenNo.toString().length() > 0)
			 * // If FP Auditor Not Enter Token No. {
			 * lObjRltPensioncaseBill.setTokenNo(lPnsnTokenNo); }
			 * lObjRltPensioncaseBillDAO.update(lObjRltPensioncaseBill); }
			 */

			inputMap.put("CVPPensionBillDtlsVO", lObjTrnPensionBillDtls);
			inputMap.put("CVPRltPensioncaseBillVO", lObjRltPensioncaseBill);
			/*
			 * //insert bill no in recovery table String lStrPnsnrCode =
			 * inputMap.get("lStrPnsnrCode").toString();
			 * List<TrnPensionRecoveryDtls> lLstObjRecovery =
			 * lObjTrnRecoveryDtls.getTrnPensionRecoveryDtls(lStrPnsnrCode,
			 * lStrBillType); for(int i=0;i<lLstObjRecovery.size();i++) {
			 * TrnPensionRecoveryDtls lObjRecovery = new
			 * TrnPensionRecoveryDtls(); lObjRecovery = lLstObjRecovery.get(i);
			 * lObjRecovery.setBillNo(lLngBillNo);
			 * lObjTrnRecoveryDtls.update(lObjRecovery); }
			 */
			// end of inserting bill no in recovery table
			/*
			 * // Update TrnPensionRqstHdr with DCRG Paid Date. String
			 * lStrTrnRqstHdrID = StringUtility.getParameter("hidTrnRqsHdrID",
			 * request).trim(); TrnPensionRqstHdr lRqstHdrVo =
			 * lObjRqstHdrDAO.read(Long.valueOf(lStrTrnRqstHdrID));
			 * 
			 * Date lseenDate = new
			 * TrnPensionerSeenDtlsDaoImpl(TrnPensionerSeenDtls
			 * .class,serv.getSessionFactory
			 * ()).getPensionerSeenDate(lStrPnsnrCode, "Y");
			 * lRqstHdrVo.setCvpDate(lseenDate); Date cvpRestorationDate =
			 * (Date) lseenDate.clone();
			 * cvpRestorationDate.setYear(cvpRestorationDate.getYear() + 15);
			 * if(cvpRestorationDate.getMonth() == 12)
			 * cvpRestorationDate.setMonth(1); else
			 * cvpRestorationDate.setMonth(cvpRestorationDate.getMonth() + 1);
			 * cvpRestorationDate.setDate(1);
			 * lRqstHdrVo.setCvpRestorationDate(cvpRestorationDate);
			 * 
			 * Date lseenDate = new
			 * TrnPensionerSeenDtlsDaoImpl(TrnPensionerSeenDtls
			 * .class,serv.getSessionFactory
			 * ()).getPensionerSeenDate(lStrPnsnrCode, "Y"); Date
			 * cvpRestorationDate = null;
			 * 
			 * if(lRqstHdrVo.getCvpEffectiveMonth() != null) { Integer
			 * lCVPEffectedMnt = lRqstHdrVo.getCvpEffectiveMonth(); Date
			 * lCVPStartDate = new
			 * SimpleDateFormat("dd/MM/yyyy").parse("01/"+lCVPEffectedMnt
			 * .toString
			 * ().substring(4,6)+"/"+lCVPEffectedMnt.toString().substring(0,4));
			 * lRqstHdrVo.setCvpDate(lCVPStartDate); cvpRestorationDate = (Date)
			 * lCVPStartDate.clone();
			 * cvpRestorationDate.setYear(cvpRestorationDate.getYear() + 15);
			 * if(cvpRestorationDate.getMonth() == 12)
			 * cvpRestorationDate.setMonth(1); else
			 * cvpRestorationDate.setMonth(cvpRestorationDate.getMonth() + 1);
			 * cvpRestorationDate.setDate(1);
			 * lRqstHdrVo.setCvpRestorationDate(cvpRestorationDate); } else
			 * if(lRqstHdrVo.getPpoDate() != null) {
			 * lRqstHdrVo.setCvpDate(lseenDate); Integer lSeenYYYYMM =
			 * Integer.parseInt(new
			 * SimpleDateFormat("yyyyMM").format(lseenDate));
			 * 
			 * Integer lPPODtYYYYMM = 0 ; Date lPPODate =
			 * lRqstHdrVo.getPpoDate(); lPPODtYYYYMM = Integer.parseInt(new
			 * SimpleDateFormat("yyyyMM").format(lPPODate));
			 * 
			 * if(lPPODtYYYYMM != null && lPPODtYYYYMM != 0 && (lPPODtYYYYMM %
			 * 100) >= 10 && (lPPODtYYYYMM % 100) <= 12) lPPODtYYYYMM =
			 * lPPODtYYYYMM + 92; else lPPODtYYYYMM = lPPODtYYYYMM + 4;
			 * 
			 * Date lCVPStartDate = new
			 * SimpleDateFormat("dd/MM/yyyy").parse("01/"
			 * +lPPODtYYYYMM.toString()
			 * .substring(4,6)+"/"+lPPODtYYYYMM.toString().substring(0,4));
			 * 
			 * if(lPPODtYYYYMM <= lSeenYYYYMM) cvpRestorationDate = (Date)
			 * lCVPStartDate.clone(); else cvpRestorationDate = (Date)
			 * lseenDate.clone();
			 * 
			 * cvpRestorationDate.setYear(cvpRestorationDate.getYear() + 15);
			 * if(cvpRestorationDate.getMonth() == 12)
			 * cvpRestorationDate.setMonth(1); else
			 * cvpRestorationDate.setMonth(cvpRestorationDate.getMonth() + 1);
			 * cvpRestorationDate.setDate(1);
			 * 
			 * lRqstHdrVo.setCvpRestorationDate(cvpRestorationDate); } else {
			 * lRqstHdrVo.setCvpDate(lseenDate); cvpRestorationDate = (Date)
			 * lseenDate.clone();
			 * cvpRestorationDate.setYear(cvpRestorationDate.getYear() + 15);
			 * if(cvpRestorationDate.getMonth() == 12)
			 * cvpRestorationDate.setMonth(1); else
			 * cvpRestorationDate.setMonth(cvpRestorationDate.getMonth() + 1);
			 * cvpRestorationDate.setDate(1);
			 * lRqstHdrVo.setCvpRestorationDate(cvpRestorationDate); }
			 * 
			 * lObjRqstHdrDAO.update(lRqstHdrVo); // end Update
			 * TrnPensionRqstHdr with DCRG Paid Date.
			 */

			// insert data into cvp history table
			if (lObjHstCommutationDtlsVO != null) {

				lLngCvpDtlsId = IFMSCommonServiceImpl.getNextSeqNum("hst_commutation_dtls", inputMap);
				lObjHstCommutationDtlsVO.setCvpDtlsId(lLngCvpDtlsId);
				lObjHstCommutationDtlsVO.setBillNo(lLngBillNo);
				lObjPhysicalCaseInwardDAO.create(lObjHstCommutationDtlsVO);
			}

			// set all DCRG related fields to null in trn_pension_rqst_hdr
			// (Pension's master table)
			lStrTrnPensionRqstHdrId = StringUtility.getParameter("hidTrnRqsHdrID", request).trim();
			TrnPensionRqstHdr lObjTrnPensionRqstHdr = lObjRqstHdrDAO.read(Long.valueOf(lStrTrnPensionRqstHdrId.trim()));
			lObjTrnPensionRqstHdr.setCvpAmount(null);
			lObjTrnPensionRqstHdr.setCvpDate(null);
			lObjTrnPensionRqstHdr.setCvpVoucherDate(null);
			lObjTrnPensionRqstHdr.setCvpVoucherNo(null);
			lObjTrnPensionRqstHdr.setCvpOrderNo(null);
			lObjTrnPensionRqstHdr.setCvpNameOfPayee(null);
			lObjTrnPensionRqstHdr.setCvpTrsryForPayment(null);
			lObjTrnPensionRqstHdr.setCvpAuthority(null);
			lObjTrnPensionRqstHdr.setCvpStartDate(null);
			lObjTrnPensionRqstHdr.setCvpEndDate(null);
			// lObjTrnPensionRqstHdr.setCvpPaidFlag(null);
			// lObjTrnPensionRqstHdr.setCvpPayMode(null);

			lObjRqstHdrDAO.update(lObjTrnPensionRqstHdr);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	/**
	 * View CVP Bill data after saving the bill
	 * 
	 * @param inputMap
	 * @return objRes ResultObject
	 */
	public ResultObject viewCVPBillData(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSmplDtFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = null;
		MstPensionerHdr lObjMstPensionerHdrVO = null;
		TrnPensionBillHdr lObjTrnPensionBillHdr = null;
		String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
		String lStrCaseStatus = "Identified";// bundleConst.getString("STATUS.APPROVED");
		String lStrBillType = bundleConst.getString("RECOVERY.CVP");
		Map lMapCVPData = new HashMap();
		String lStrPPONo = null;
		String lStrPensionerName = "";
		String lStrPnsnrName = "";
		String lStrPnsrCode = null;
		String lStrOfficeAddr = null;
		String lStrDesignation = null;
		String lStrTokenNo = null;
		String lStrDesgDesc = null;
		String lStrFirstname = null;
		String lStrMiddlename = "";
		String lStrLastname = null;
		String lStrCurrDate = null;
		Double lCVPAmt = 0.0;
		Double lReducedAmt = 0.0;
		Double lRecAmt = 0.00;
		String lStrAuthorityNo = null;
		StringBuffer lSbPrintString = new StringBuffer();
		BigDecimal lBDCvpAmnt = BigDecimal.ZERO;
		String lStrNewLine = StringUtils.getLineSeparator();
		String lStrOffAddr = null;
		// String lBillType = null;
		Date lStrBillDate = null;
		long lTrnPensionBillHdrPK = 0;
		List lLstBillDtlsElem = new ArrayList();
		List lLstCVPBillDtls = null;
		TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, srvcLoc.getSessionFactory());
		TrnPensionBillDtlsDAO lObjTrnPensionBillDtlsDAO = new TrnPensionBillDtlsDAOImpl(TrnPensionBillDtls.class, srvcLoc.getSessionFactory());
		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, srvcLoc.getSessionFactory());
		MstPensionerHdrDAO lObjMstPensionerHdrDAO = new MstPensionerHdrDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		CVPRestorationLetterDAO lObjCVPRestorationLetterDAO = new CVPRestorationLetterDAOImpl(null, srvcLoc.getSessionFactory());
		PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		HstCommutationDtls lObjHstCommutationDtls = new HstCommutationDtls();
		String lStrSchemeCode = null;
		CommonDAO lObjCommonDAO = new CommonDAOImpl(srvcLoc.getSessionFactory());
		String lStrBankName = "";
		String lStrBranchName = "";
		String lStrAccountNo = "";
		String lStrOrderNo = "";
		Date lDtOrderDate = null;
		try {
			setSessionInfo(inputMap);
			List<RltBillParty> lLstRltBillParty = (List<RltBillParty>) inputMap.get("RltBillParty");
			lStrCurrDate = DateUtility.getCurrentDate();
			Long lLngBillNo = Long.parseLong(inputMap.get("billNo").toString());
			if (lLngBillNo != null) {
				// Getting the ObjectVo of TrnPensionBillDtlsVO
				lObjTrnPensionBillHdr = lObjTrnPensionBillHdrDAO.getTrnPensionBillHdr(lLngBillNo, lStrBillType);
				lStrBillDate = lObjTrnPensionBillHdr.getBillDate();
				lStrTokenNo = lObjTrnPensionBillHdr.getTokenNo();
				lTrnPensionBillHdrPK = lObjTrnPensionBillHdr.getTrnPensionBillHdrId();
				lStrSchemeCode = (lObjTrnPensionBillHdr.getSchemeCode() != null) ? lObjTrnPensionBillHdr.getSchemeCode() : "";
				lLstBillDtlsElem = lObjTrnPensionBillDtlsDAO.getTrnPensionBillDtls(lTrnPensionBillHdrPK);
				lObjHstCommutationDtls = lObjPensionBillDAO.getCvpDtlsFromBillNo(lLngBillNo);
				if(lObjHstCommutationDtls != null)
				{
					lStrOrderNo = lObjHstCommutationDtls.getOrderNo();
					lDtOrderDate = lObjHstCommutationDtls.getOrderDate();
				}
			}
			if (lLstRltBillParty != null && !lLstRltBillParty.isEmpty()) {
				for (RltBillParty lObjRltBillParty : lLstRltBillParty) {
					if (lObjRltBillParty.getBankCode() != null && lObjRltBillParty.getBranchCode() != null) {
						lStrBankName = lObjCommonDAO.getBankNameFromBankCode(lObjRltBillParty.getBankCode(), gLangID);
						lStrBranchName = lObjCommonDAO.getBranchNameFromBranchCode(lObjRltBillParty.getBankCode(), lObjRltBillParty.getBranchCode(), gLangID);
						lStrAccountNo = (lObjRltBillParty.getAccntNo() != null) ? lObjRltBillParty.getAccntNo() : "";
					}

					lStrPensionerName = lStrPensionerName + lObjRltBillParty.getPartyName() + " / " + lStrBankName + " / " + lStrBranchName + " / " + lStrAccountNo + "$";
				}
			}

			if (lLstBillDtlsElem != null && !lLstBillDtlsElem.isEmpty()) {
				// Getting the Value from TrnPensionBillDtls... Start...
				lStrPPONo = lLstBillDtlsElem.get(1).toString();
				lStrPnsrCode = lLstBillDtlsElem.get(0).toString();
				if (lLstBillDtlsElem.get(2) != null) {
					lReducedAmt = Double.parseDouble(lLstBillDtlsElem.get(2).toString());
				}
				if (lLstBillDtlsElem.get(4) != null) {
					lStrPnsnrName = lLstBillDtlsElem.get(4).toString();
				}
				if (lLstBillDtlsElem.get(3) != null) {
					lRecAmt = Double.parseDouble(lLstBillDtlsElem.get(3).toString());
				}
				lCVPAmt = lReducedAmt + lRecAmt;
			}

			/************************************************** Get CVP BILL DATA FOR PRINT***********STARTS ***********************/
			lStrOffAddr = lObjCVPRestorationLetterDAO.getOffiCeAddr(gStrLocCode);
			lLstCVPBillDtls = lObjPensionBillDAO.getPrintBillDtls(lStrPnsrCode, gStrLocCode, lStrCurrDate, "CVP");
			if (!lLstCVPBillDtls.isEmpty() && lLstCVPBillDtls.size() > 0) {
				Iterator lObjIterator = lLstCVPBillDtls.iterator();
				Object[] lArrObj = null;
				while (lObjIterator.hasNext()) {
					lArrObj = (Object[]) lObjIterator.next();
					if (lArrObj[0] != null) {
						lStrAuthorityNo = (String) lArrObj[0];
					} else {
						lStrAuthorityNo = "";
					}
					if (lArrObj[1] != null) {
						lBDCvpAmnt = (BigDecimal) lArrObj[1];
					}
				}
			}
			/********** Print Cvp Bill Start ***********/
			ReportExportHelper objExport = new ReportExportHelper();
			StringBuilder lSbCvpBill = new StringBuilder();
			StringBuilder lSbHeader = new StringBuilder();
			StringBuilder lSbFooter = new StringBuilder();
			List<List> arrOuter = new ArrayList<List>();
			ArrayList lineList = new ArrayList();
			String lStrTreasuryName = "";
			String lStrNameOfOffice = "";
			String lStrHeader = "";

			lSbHeader.append("Form M.T.R.45 A");
			lSbHeader.append("\r\n");
			lSbHeader.append("( See Rule 406 A )");
			lSbHeader.append("\r\n");
			lSbHeader.append("Simple Receipt");
			lSbHeader.append("\r\n");

			lStrTreasuryName = "Name of the Treasury:-" + space(25) + "$" + SessionHelper.getLocationName(inputMap) + space(48 - SessionHelper.getLocationName(inputMap).length()) + "$";

			// lStrTreasuryName = String.format("%50s",lStrTreasuryName)
			// +String.format("%50s","Token No.")
			// +String.format("%50s","Date")
			// +String.format("%50s","Voucher No.");

			lStrTreasuryName = lStrTreasuryName + "Token No." + space(39) + "$" + "Date :" + space(43) + "$" + "Voucher No. " + space(38);

			lStrNameOfOffice = "Name of the Office:- " + space(1) + lStrOffAddr;

			ColumnVo[] columnHeading = new ColumnVo[2];
			columnHeading[0] = new ColumnVo(lStrTreasuryName, 1, 50, 0, true, false, false);
			columnHeading[1] = new ColumnVo(lStrNameOfOffice, 1, 27, 0, true, false, false);

			// lineList.add(lStrTreasuryName);
			// lineList.add("Name of the Office:");
			// arrOuter.add(lineList);
			//
			// lineList = new ArrayList();
			// lineList.add("Token No.");
			// lineList.add(lStrOffAddr);
			// arrOuter.add(lineList);
			//
			// lineList = new ArrayList();
			// lineList.add("Date : "+DateUtilities.stringFromDate(lStrBillDate));
			// lineList.add("");
			// arrOuter.add(lineList);
			//
			// lineList = new ArrayList();
			// lineList.add("Voucher No.");
			// lineList.add("");
			// arrOuter.add(lineList);

			// lSbFooter.append(getChar(80, "-"));
			// lSbFooter.append("\r\n");
			lSbFooter.append("Demand No         : G6");
			lSbFooter.append("\r\n");
			lSbFooter.append("Major Head        : 2071 Pension & Other Retirement Benefits.");
			lSbFooter.append("\r\n");
			lSbFooter.append("Minor Head        : 101 Superannuation and Retirement Allowances.");
			lSbFooter.append("\r\n");
			lSbFooter.append("Sub   Head        : 102 Commuted Value of Pension.");
			lSbFooter.append("\r\n");
			lSbFooter.append("Detailed Head     : 04");
			lSbFooter.append("\r\n");
			lSbFooter.append("Bill Type         :");
			lSbFooter.append("\r\n");
			lSbFooter.append("Scheme Code       : " + lStrSchemeCode);
			lSbFooter.append("\r\n");
			lSbFooter.append("( Object Expenditure )");
			lSbFooter.append("\r\n");
			lSbFooter.append(getChar(80, "-"));
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append(getChar(80, "-"));
			lSbFooter.append("\r\n");
			String lStrReducedAmt = "Received Sum of(Rs. " + String.valueOf(lReducedAmt.longValue()) + " /-) Rs. " + EnglishDecimalFormat.convertWithSpace(new BigDecimal(lReducedAmt.doubleValue())) + " Only";
			lSbFooter.append(lStrReducedAmt);
			lSbFooter.append("\r\n");
			lSbFooter.append(getChar(80, "-"));
			lSbFooter.append("\r\n");

			lSbCvpBill.append("<div><pre>");
			lStrHeader = objExport.getReportFileForCvpBill(arrOuter, columnHeading, lSbHeader.toString(), lSbFooter.toString(), null, 79, true, 1);
			lSbCvpBill.append(lStrHeader);

			arrOuter = new ArrayList<List>();
			lineList = new ArrayList();
			columnHeading = new ColumnVo[2];
			columnHeading[0] = new ColumnVo("", 1, 50, 0, true, false, false);
			columnHeading[1] = new ColumnVo("", 1, 27, 0, true, false, false);

			lStrHeader = "";

			lineList = new ArrayList();
			lineList.add(getChar(51, "-"));
			lineList.add(getChar(27, "-"));
			arrOuter.add(lineList);

			lineList = new ArrayList();
			lineList.add("Name/Bank/Branch/Ac.No : " + lStrPensionerName);
			lineList.add("Non Plan");
			arrOuter.add(lineList);

			lineList = new ArrayList();
			lineList.add("Authorised by A.G.Mah.Mumbai.	");
			lineList.add("B Commuted Value of Pension");
			arrOuter.add(lineList);

			lineList = new ArrayList();
			lineList.add("Authority No. "+ lStrOrderNo + "  "+ lObjSmplDtFormat.format(lDtOrderDate));
			lineList.add("B 1-11-1956");
			arrOuter.add(lineList);

			lineList = new ArrayList();
			lineList.add(" ");
			lineList.add("A 1-11-1956");
			arrOuter.add(lineList);

			lineList = new ArrayList();
			lineList.add("Date : " + DateUtilities.stringFromDate(lStrBillDate));
			lineList.add("A 1-11-1956");
			arrOuter.add(lineList);

			lineList = new ArrayList();
			lineList.add("P.P.O. No.  " + lStrPPONo);
			lineList.add("");
			arrOuter.add(lineList);

			lineList = new ArrayList();
			lineList.add(getChar(51, "-"));
			lineList.add(getChar(27, "-"));
			arrOuter.add(lineList);

			lSbFooter = new StringBuilder();

			lSbFooter.append("Copy - enclosed                      Received Payment");
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append(String.format("%80s", "Signature & Designation"));
			lSbFooter.append("\r\n");
			lSbFooter.append(String.format("%80s", "A.P.A.O/ATO"));
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append(String.format("%50s", "For use in Treasury"));
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append(getChar(80, "-"));
			lSbFooter.append("\r\n");
			lSbFooter.append("Pay Rs.(  " + String.valueOf(lReducedAmt.longValue()) + " /-) Rupees : " + EnglishDecimalFormat.convertWithSpace(new BigDecimal(lReducedAmt.doubleValue())) + " Only");
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append("\r\n");
			lSbFooter.append(String.format("%70s", "Asstt.Pay and Accounts Officer/Asstt. Treasury Officer."));
			lStrHeader = objExport.getReportFileForCvpBill(arrOuter, columnHeading, "", lSbFooter.toString(), null, 79, true, 26);
			lSbCvpBill.append(lStrHeader);

			lSbCvpBill.append("</pre></div>");

			/********** Print Cvp Bill End ***********/

			/**************** Print String Starts **************************/
			// lSbPrintString.append("<div><pre>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(space(55) + " <strong><font size='3px'> " +
			// " CVP Bill " + " </font></strong> ");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append("<font size='2px'>");
			// //
			// lSbPrintString.append("<table align='center' width='98%' border='1' bordercolor='#eed0aa'>");
			// // lSbPrintString.append("<tr><td style='border:none'>");
			// lSbPrintString.append("NAME OF THE TREASUREY" + space(2) + ":" +
			// space(2) + SessionHelper.getLocationName(inputMap));
			// // lSbPrintString.append("</td><tr>");
			// // lSbPrintString.append("<tr><td>");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("NAME OF THE OFFICE" + space(5) + ":" +
			// space(2) + lStrOffAddr);
			// lSbPrintString.append("<br>");
			// // lSbPrintString.append("</td><tr>");
			// // lSbPrintString.append("<tr><td>");
			// lSbPrintString.append("TOKEN NO." + space(14) + ":");
			// lSbPrintString.append("<br>");
			// // lSbPrintString.append("</td><tr>");
			// // lSbPrintString.append("<tr><td>");
			// lSbPrintString.append("BILL DATE" + space(14) + ":" + space(2) +
			// DateUtilities.stringFromDate(lStrBillDate));
			// lSbPrintString.append("<br>");
			// // lSbPrintString.append("</td><tr>");
			// // lSbPrintString.append("<tr><td>");
			// lSbPrintString.append("VOUCHER NO." + space(12) + ":");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append("_______________________________________________________________________________________________________________________________");
			// // lSbPrintString.append("</td><tr>");
			// // lSbPrintString.append("</table>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// //
			// lSbPrintString.append("<table align='center' width='98%' border='1' bordercolor='#eed0aa'>");
			// // lSbPrintString.append("<tr><td style='border:none'>");
			// lSbPrintString.append("MAJOR HEAD" + space(12) + ":" + space(2) +
			// "2071 PENSION & OTHER RETIREMENT BENEFITS");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("MINOR HEAD" + space(12) + ":" + space(2) +
			// "101 SUPER ANNUATION & RETIREMENT ALLOWANCES");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("SUB HEAD" + space(14) + ":" + space(2) +
			// "102 COMMUTED VALUE OF PENSION");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("DETAILED HEAD" + space(9) + ":" + space(2)
			// + "04");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("BILL TYPE" + space(13) + ":" + space(2) +
			// "02");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("SCHEME CODE" + space(11) + ":" + space(2)
			// + lStrSchemeCode);
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("(OBJECT EXPENDITURE)");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append("RECEIVED SUM OF" + space(2) +
			// String.valueOf(lReducedAmt.doubleValue()) + space(2) + "("
			// + EnglishDecimalFormat.convertWithSpace(new
			// BigDecimal(lReducedAmt.doubleValue())) + ")" + space(2) +
			// "RUPEES.");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append("_______________________________________________________________________________________________________________________________");
			// // lSbPrintString.append("</td><tr>");
			// // lSbPrintString.append("</table>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// //
			// lSbPrintString.append("<table align='center' width='98%' border='1' bordercolor='#eed0aa'>");
			// // lSbPrintString.append("<tr><td style='border:none'>");
			// lSbPrintString.append("NAME" + space(23) + ":" + space(2) +
			// lStrPnsnerName + space(5) +
			// "(NON PLAN AUTHORISED BY A.G. MUMBAI/NAGPUR)");
			// lSbPrintString.append("<br>");
			//
			// if (lStrAuthorityNo != null && lStrAuthorityNo.length() > 0) {
			// lSbPrintString.append("AUTHORITY NO." + space(14) + ":" +
			// space(2) + lStrAuthorityNo);
			// lSbPrintString.append("<br>");
			// }
			// lSbPrintString.append("BILL DATE." + space(17) + ":" + space(2) +
			// DateUtilities.stringFromDate(lStrBillDate));
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("COMMUTED VALUE OF PENSION" + space(2) +
			// ":" + space(2) + String.valueOf(lBDCvpAmnt.longValue()));
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("P.P.O. NO." + space(17) + ":" + space(2) +
			// lStrPPONo);
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append("COPY ENCLOSED & RECEIVED PAYMENT.");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(space(84) + "SIGNATURE & DESIGNATION");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(space(92) + "A.P.A.O.");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append("_______________________________________________________________________________________________________________________________");
			// // lSbPrintString.append("</td><tr>");
			// // lSbPrintString.append("</table>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// //
			// lSbPrintString.append("<table align='center' width='98%' border='1' bordercolor='#eed0aa'>");
			// // lSbPrintString.append("<tr><td style='border:none'>");
			// lSbPrintString.append(space(47) + "FOR USE IN TREASURY");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("PAY RS." + space(2) +
			// String.valueOf(lReducedAmt.doubleValue()) + space(2) + "(" +
			// EnglishDecimalFormat.convertWithSpace(new
			// BigDecimal(lReducedAmt.doubleValue()))
			// + ")" + space(2) + "RUPEES.");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append("BILL DATE" + space(2) + ":" + space(2) +
			// DateUtilities.stringFromDate(lStrBillDate));
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(space(87) + "Assistant PAO/TAO,");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append("_______________________________________________________________________________________________________________________________");
			// // lSbPrintString.append("</td><tr>");
			// // lSbPrintString.append("</table>");
			// lSbPrintString.append("</font></pre></div>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append((char) 12);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(lStrNewLine);
			/**************** Print String Ends ***************************/

			/************************************************** Get CVP BILL DATA FOR PRINT***********ENDS ***********************/
			/*
			 * if (lStrPnsrCode != null && lStrPnsrCode.length() > 0) { //
			 * Getting the ObjectVo of TrnPensionRqstHdr lObjTrnPensionRqstHdrVO
			 * =
			 * lObjTrnPensionRqstHdrDAO.getTrnPensionRqstHdrDtlsFromPnsnerCode(
			 * lStrPnsrCode, lStrStatus, lStrCaseStatus,
			 * SessionHelper.getLocationCode(inputMap)); if
			 * (lObjTrnPensionRqstHdrVO.getCvpAmount() != null) { lCVPAmt =
			 * Double
			 * .parseDouble(lObjTrnPensionRqstHdrVO.getCvpAmount().toString());
			 * } if (lObjTrnPensionRqstHdrVO.getPpoNo() == null) {
			 * lObjTrnPensionRqstHdrVO =
			 * lObjTrnPensionRqstHdrDAO.getTrnPensionRqstHdrDtlsFromPnsnerCode
			 * (lStrPnsrCode, lStrStatus, "APPROVED",
			 * SessionHelper.getLocationCode(inputMap)); lCVPAmt = lReducedAmt -
			 * lRecAmt; } } if (lObjTrnPensionRqstHdrVO != null) { // Getting
			 * the Value from TrnPensionRqstHdr... Start... if
			 * (lObjTrnPensionRqstHdrVO.getPpoNo() != null) { lStrPPONo =
			 * lObjTrnPensionRqstHdrVO.getPpoNo(); } // Getting the Value from
			 * TrnPensionRqstHdr... End... }
			 */
			/*
			 * if (lStrPnsrCode != null && lStrPnsrCode.length() > 0) { //
			 * Getting the ObjectVo of MstPensionerHdr... Start...
			 * lObjMstPensionerHdrVO =
			 * lObjMstPensionerHdrDAO.getMstPensionerHdrDtls(lStrPnsrCode);
			 * 
			 * lStrFirstname = lObjMstPensionerHdrVO.getFirstName();
			 * lStrMiddlename = (lObjMstPensionerHdrVO.getMiddleName() != null)
			 * ? lObjMstPensionerHdrVO.getMiddleName() + " " : ""; lStrLastname
			 * = lObjMstPensionerHdrVO.getLastName(); lStrPnsnerName =
			 * lStrFirstname + ' ' + lStrMiddlename + lStrLastname;
			 * 
			 * lStrOfficeAddr = lObjMstPensionerHdrVO.getOfficeAddr();
			 * 
			 * lStrDesignation = lObjMstPensionerHdrVO.getDesignation();
			 * lStrDesgDesc =
			 * lObjTrnPensionBillDtlsDAO.getDesgDesc(lStrDesignation);
			 * 
			 * // Getting the ObjectVo of MstPensionerHdr... End... }
			 */
			inputMap.put("PPONo", lStrPPONo);
			inputMap.put("PnsnBillTokenNo", lStrTokenNo);
			inputMap.put("Name", lStrPnsnrName);
			inputMap.put("Designation", lStrDesgDesc);
			inputMap.put("OfficeAddr", lStrOfficeAddr);
			inputMap.put("CVPAmount", lCVPAmt);
			inputMap.put("RecoveryAmount", lRecAmt);
			inputMap.put("NetAmount", lReducedAmt);
			inputMap.put("BillDate", lStrBillDate);
			inputMap.put("MapCVPData", lMapCVPData);
			inputMap.put("PnsnrCode", lStrPnsrCode);
			inputMap.put("PrintCVPBillString", lSbCvpBill);
			inputMap.put("schemeNo", lStrSchemeCode);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		resObj.setResultValue(inputMap);
		return resObj;
	}

	private String getChar(int count, String ele) {

		StringBuffer lSBSpace = new StringBuffer();
		for (int i = 0; i < count - 1; i++) {
			lSBSpace.append(ele);
		}
		return lSBSpace.toString();
	}

	private void setSessionInfo(Map inputMap) {

		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gDtCurrDt = DBUtility.getCurrentDateFromDB();
		gLangID = SessionHelper.getLangId(inputMap);
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
}
