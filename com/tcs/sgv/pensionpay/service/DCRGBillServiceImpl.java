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
import com.tcs.sgv.pensionpay.dao.TrnPensionRecoveryDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionRecoveryDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.web.jsp.tags.DateUtilities;


/**
 * DCRG Bill Specific Service Impl.
 * 
 * @version 1.0
 */
public class DCRGBillServiceImpl extends ServiceImpl implements DCRGBillService {

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

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	/**
	 * Get DCRG Bill data for displaying in bill
	 * 
	 * @param inputMap
	 * @return objRes ResultObject
	 */
	public ResultObject getDCRGBillData(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
		String lStrCaseStatus = "Identified";
		String lStrRcvryFlag = bundleConst.getString("RECOVERY.DCRG");
		String lStrPPONo = null;
		String lStrPnsnerName = "";
		String lStrPnsrCode = null;
		String lStrOfficeAddr = null;
		String lStrDesgDesc = null;
		Double lDCRGAmt = 0D;
		Double lNetAmt = 0D;
		Double lRecAmt = 0D;
		long lIntNetAmt = 0L;
		String lHeadcode = null;
		String lScheme = null;
		String lStrTrnRqsHdrID = null;
		Double lWithHeldAmnt = 0D;
		Double lAmntAftrWithHeld = 0D;
		String lStrOrderNo = null;
		Date lDtOrderDate = null;
		String lStrVoucherNo = null;
		Date lDtVoucherDate = null;
		String lStrPayingAuth = null;

		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, srvcLoc.getSessionFactory());
		try {
			setSessionInfo(inputMap);
			// (HttpServletRequest) inputMap.get("requestObj");
			lStrPPONo = inputMap.get("PPONo").toString();

			List lArrDtls = lObjTrnPensionRqstHdrDAO.getDtlsForCVPDCRGBill(lStrStatus, lStrCaseStatus, lStrRcvryFlag, lStrPPONo, gLangID, SessionHelper.getLocationCode(inputMap));

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
				// (String) lArrDtls.get(6);
				if (lArrDtls.get(7) != null) {
					lDCRGAmt = Double.parseDouble(((BigDecimal) lArrDtls.get(7)).toString());
				} else {
					lDCRGAmt = 0D;
				}
				if (lArrDtls.get(9) != null) {
					lRecAmt = Double.parseDouble(((BigDecimal) lArrDtls.get(9)).toString());
				}
			}

			// Recovery Amount and Net Amount Calculation.
			lNetAmt = lDCRGAmt;
			if (lRecAmt > 0D) {
				lNetAmt = lNetAmt - lRecAmt;
			}
			if (lArrDtls.get(14) != null) {
				lWithHeldAmnt = Double.parseDouble(((BigDecimal) lArrDtls.get(14)).toString());
			}
			if (lArrDtls.get(15) != null) {
				lAmntAftrWithHeld = Double.parseDouble(((BigDecimal) lArrDtls.get(15)).toString());
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
			if (!"".equals(lStrRcvryFlag) && lStrRcvryFlag.trim().equals("DCRG")) {
				if (lArrDtls.get(20) != null) {
					lStrPayingAuth = (String) lArrDtls.get(20);
				}
			}

			inputMap.put("PPONo", lStrPPONo);
			inputMap.put("Name", lStrPnsnerName);
			inputMap.put("Designation", lStrDesgDesc);
			inputMap.put("OfficeAddr", lStrOfficeAddr);
			inputMap.put("DCRGAmount", lDCRGAmt);
			inputMap.put("RecoveryAmount", lRecAmt);
			inputMap.put("NetAmount", lIntNetAmt);
			inputMap.put("BillDate", gDtCurrDt);
			inputMap.put("PnsnrCode", lStrPnsrCode);
			inputMap.put("Headcode", lHeadcode);
			inputMap.put("Scheme", lScheme);
			inputMap.put("TrnRqsHdrID", lStrTrnRqsHdrID);

			inputMap.put("WithHeldAmount", lWithHeldAmnt);
			inputMap.put("AmountAfterWithHeld", lAmntAftrWithHeld);
			inputMap.put("OrderNo", lStrOrderNo);
			inputMap.put("OrderDate", lDtOrderDate);
			inputMap.put("VoucherNo", lStrVoucherNo);
			inputMap.put("VoucherDate", lDtVoucherDate);
			inputMap.put("PayingAuth", lStrPayingAuth);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			resObj.setThrowable(e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	/**
	 * Saves DCRG specific Bill Data in TRN_PENSION_BILL_DTLS and
	 * RLT_PENSIONCASE_BILL
	 * 
	 * @param Map
	 *            :lMapInput
	 * @return ResultObject
	 */
	public ResultObject saveDCRGBill(Map<String, Object> inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, serv.getSessionFactory());
		TrnPensionBillDtlsDAO lObjTrnPensionBillDtlsDAO = new TrnPensionBillDtlsDAOImpl(TrnPensionBillDtls.class, serv.getSessionFactory());
		RltPensioncaseBillDAO lObjRltPensioncaseBillDAO = new RltPensioncaseBillDAOImpl(RltPensioncaseBill.class, serv.getSessionFactory());
		TrnPensionRecoveryDtlsDAO lObjTrnRecoveryDtls = new TrnPensionRecoveryDtlsDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());
		PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstPnsnPmntDcrgDtls.class, serv.getSessionFactory());
		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
		TrnPensionBillHdr lObjTrnPensionBillHdr = null;
		TrnPensionBillDtls lObjTrnPensionBillDtls = null;
		RltPensioncaseBill lObjRltPensioncaseBill = null;
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtls = null;
		Long lLngRltPensioncaseBillId = null;
		Long lLngHstPnsnPmntDcrgDtlsId = null;
		Long lLngBillNo = Long.parseLong(inputMap.get("billNo").toString());
		String lStrBillCntrlNo = (String) inputMap.get("PnsnBillCntrlNo");
		Long lPnsnTokenNo = (Long) inputMap.get("PnsnTokenNo");
		String lStrBillType = bundleConst.getString("RECOVERY.DCRG");
		String lStrTrnPensionRqstHdrId = null;
		try {
			setSessionInfo(inputMap);

			// Getting VO Object from inputMap with the key
			// "DCRGTrnPensionBillDtlsVO"

			lObjTrnPensionBillHdr = (TrnPensionBillHdr) inputMap.get("DCRGTrnPensionBillHdrVO");
			lObjTrnPensionBillDtls = (TrnPensionBillDtls) inputMap.get("DCRGTrnPensionBillDtlsVO");
			lObjHstPnsnPmntDcrgDtls = (HstPnsnPmntDcrgDtls) inputMap.get("DCRGHstPnsnPmntDcrgDtlsVO");
			if (lLngBillNo != null && lLngBillNo != null) {
				// insert Data into TrnPensionBillHdr.
				lObjTrnPensionBillHdr.setBillType(lStrBillType);
				lObjTrnPensionBillHdr.setBillNo(lLngBillNo);

				if (lPnsnTokenNo != null && lPnsnTokenNo.toString().length() > 0)

				// If
				// FP
				// Auditor
				// Not
				// Enter
				// TokenlObjTrnPensionBillHdr
				// No.
				{
					lObjTrnPensionBillHdr.setTokenNo(lPnsnTokenNo.toString());
				}
				lObjTrnPensionBillHdr.setTrnPensionBillHdrId(IFMSCommonServiceImpl.getNextSeqNum("trn_pension_bill_hdr", inputMap));
				lObjTrnPensionBillHdrDAO.create(lObjTrnPensionBillHdr);
				// Inserts new DCRG Bill specific data into
				// TRN_PENSION_BILL_DTLS
				// sets PK of TrnPensionBillDtls...
				lObjTrnPensionBillDtls.setTrnPensionBillDtlsId(IFMSCommonServiceImpl.getNextSeqNum("trn_pension_bill_dtls", inputMap));
				lObjTrnPensionBillDtls.setTrnPensionBillHdrId(lObjTrnPensionBillHdr.getTrnPensionBillHdrId());
				lObjTrnPensionBillDtlsDAO.create(lObjTrnPensionBillDtls);
			}
			// Getting PK of RltPensioncaseBill from inputMap with the key
			// "DCRGRltPensioncaseBillId"
			lLngRltPensioncaseBillId = (Long) inputMap.get("DCRGRltPensioncaseBillId");

			if (lLngRltPensioncaseBillId != null && lLngBillNo != null) {
				// Updates DCRG Bill specific data in RLT_PENSIONCASE_BILL
				lObjRltPensioncaseBill = lObjRltPensioncaseBillDAO.read(lLngRltPensioncaseBillId);
				lObjRltPensioncaseBill.setBillNo(lLngBillNo);
				lObjRltPensioncaseBill.setBillCntrlNo(lStrBillCntrlNo);
				if (lPnsnTokenNo != null && lPnsnTokenNo.toString().length() > 0) // If
				// FP
				// Auditor
				// Not
				// Enter
				// Token
				// No.
				{
					lObjRltPensioncaseBill.setTokenNo(lPnsnTokenNo);
				}
				lObjRltPensioncaseBillDAO.update(lObjRltPensioncaseBill);
			}

			// if(lPnsnTokenNo != null && lPnsnTokenNo.toString().length() > 0)
			// // If FP Auditor Enter Token No.
			// {
			// OrgTokenStatusDAO lObjTokenStatusDAO = new
			// OrgTokenStatusDAOImpl(OrgTokenStatus.class,serv.getSessionFactory());
			// Long lLngPnsnBillNo =lLngBillNo;
			// lObjTokenStatusDAO.updateTokenStatus(lPnsnTokenNo, gStrLocCode,
			// lLngPnsnBillNo, gLngUserId,
			// gLngPostId,BillProcConstants.INWARD_TYPE_BILL);
			//
			// ReportServiceImpl rptService = new ReportServiceImpl();
			// rptService.updateTokenIssue(inputMap, lPnsnTokenNo.toString(),
			// lLngPnsnBillNo, DBUtility.getCurrentDateFromDB(), null, "B");
			// }

			inputMap.put("DCRGPensionBillDtlsVO", lObjTrnPensionBillDtls);
			inputMap.put("DCRGRltPensioncaseBillVO", lObjRltPensioncaseBill);

			// insert bill no in recovery table
			String lStrPnsnrCode = inputMap.get("lStrPnsnrCode").toString();
			List<TrnPensionRecoveryDtls> lLstObjRecovery = lObjTrnRecoveryDtls.getTrnPensionRecoveryDtls(lStrPnsnrCode, lStrBillType);
			for (int i = 0; i < lLstObjRecovery.size(); i++) {
				TrnPensionRecoveryDtls lObjRecovery = new TrnPensionRecoveryDtls();
				lObjRecovery = lLstObjRecovery.get(i);
				lObjRecovery.setBillNo(lLngBillNo);
				lObjTrnRecoveryDtls.update(lObjRecovery);
			}
			// end of inserting bill no in recovery table

			StringUtility.getParameter("hidTrnRqsHdrID", request).trim();

			// insert data into dcrg history table
			if (lObjHstPnsnPmntDcrgDtls != null) {

				lLngHstPnsnPmntDcrgDtlsId = IFMSCommonServiceImpl.getNextSeqNum("hst_pnsnpmnt_dcrg_dtls", inputMap);
				lObjHstPnsnPmntDcrgDtls.setDcrgDtlsId(lLngHstPnsnPmntDcrgDtlsId);
				lObjHstPnsnPmntDcrgDtls.setBillNo(lLngBillNo);
				lObjPhysicalCaseInwardDAO.create(lObjHstPnsnPmntDcrgDtls);
			}

			// set all DCRG related fields to null in trn_pension_rqst_hdr
			// (Pension's master table)
			lStrTrnPensionRqstHdrId = StringUtility.getParameter("hidTrnRqsHdrID", request).trim();
			TrnPensionRqstHdr lObjTrnPensionRqstHdr = lObjTrnPensionRqstHdrDAO.read(Long.valueOf(lStrTrnPensionRqstHdrId.trim()));
			lObjTrnPensionRqstHdr.setDcrgAmount(null);
			// lObjTrnPensionRqstHdr.setDcrgDate(null);
			lObjTrnPensionRqstHdr.setPaidDate(null);
			lObjTrnPensionRqstHdr.setDcrgOrderNo(null);
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
			lObjTrnPensionRqstHdrDAO.update(lObjTrnPensionRqstHdr);

			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	/**
	 * View DCRG Bill data after saving the bill
	 * 
	 * @param inputMap
	 * @return objRes ResultObject
	 */
	public ResultObject viewDCRGBillData(Map<String, Object> inputMap) {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSmplDtFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstPensionerHdr lObjMstPensionerHdrVO = null;
		TrnPensionBillHdr lObjTrnPensionBillHdr = null;
		bundleConst.getString("STATUS.CONTINUE");
		bundleConst.getString("CMN.NEW");
		String lStrRcvryFlag = bundleConst.getString("RECOVERY.DCRG");
		Map lMapDCRGData = new HashMap();
		String lStrPPONo = null;
		String lStrPnsrCode = null;
		String lStrOfficeAddr = null;
		String lStrDesignation = null;
		String lStrCurrDate = null;
		String lStrTokenNo = null;
		String lStrDesgDesc = null;
		Double lDCRGAmt = 0D;
		Double lReducedAmt = 0D;
		Double lRecAmt = 0D;
		Double lGrossAmnt = 0D;
		Double lWithHeldAmnt = 0D;
		Double lAmntAftrWithHeld = 0D;
		Date lStrBillDate = null;
		Long lTrnPensionBillHdrPK = null;
		String lStrPnsnrName = "";
		String lStrPensionerName = "";
		List lLstBillDtlsElem = new ArrayList();
		StringBuffer lSbPrintString = new StringBuffer();
		List lLstDCRGBillDtls = null;
		String lStrOffAddr = null;
		String lStrAuthorityNo = null;
		BigDecimal lBDDcrgAmnt = BigDecimal.ZERO;
		String lStrNewLine = StringUtils.getLineSeparator();
		TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, srvcLoc.getSessionFactory());
		TrnPensionBillDtlsDAO lObjTrnPensionBillDtlsDAO = new TrnPensionBillDtlsDAOImpl(TrnPensionBillDtls.class, srvcLoc.getSessionFactory());
		new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, srvcLoc.getSessionFactory());
		MstPensionerHdrDAO lObjMstPensionerHdrDAO = new MstPensionerHdrDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		CVPRestorationLetterDAO lObjCVPRestorationLetterDAO = new CVPRestorationLetterDAOImpl(null, srvcLoc.getSessionFactory());
		PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		String lStrSchemeCode = null;
		CommonDAO lObjCommonDAO = new CommonDAOImpl(srvcLoc.getSessionFactory());
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtls = new HstPnsnPmntDcrgDtls();
		String lStrBankName = "";
		String lStrBranchName = "";
		String lStrAccountNo = "";
		String lStrOrderNo = "";
		Date lDtOrderDate = null;
		try {
			setSessionInfo(inputMap);
			lStrCurrDate = DateUtility.getCurrentDate();
			List<RltBillParty> lLstRltBillParty = (List<RltBillParty>) inputMap.get("RltBillParty");
			Long lStrBillNo = Long.parseLong(inputMap.get("billNo").toString());
			if (lStrBillNo != null) {
				// Getting the ObjectVo of TrnPensionBillDtlsVO
				lObjTrnPensionBillHdr = lObjTrnPensionBillHdrDAO.getTrnPensionBillHdr(lStrBillNo, lStrRcvryFlag);
				lStrBillDate = lObjTrnPensionBillHdr.getBillDate();
				lStrTokenNo = lObjTrnPensionBillHdr.getTokenNo();
				lStrSchemeCode = (lObjTrnPensionBillHdr.getSchemeCode() != null) ? lObjTrnPensionBillHdr.getSchemeCode() : "";
				lTrnPensionBillHdrPK = lObjTrnPensionBillHdr.getTrnPensionBillHdrId();
				lLstBillDtlsElem = lObjTrnPensionBillDtlsDAO.getTrnPensionBillDtls(lTrnPensionBillHdrPK);
				lObjHstPnsnPmntDcrgDtls =  lObjPensionBillDAO.getDcrgDtlsFromBillNo(lStrBillNo);
				if(lObjHstPnsnPmntDcrgDtls != null)
				{
					lStrOrderNo = lObjHstPnsnPmntDcrgDtls.getOrderNo();
					lDtOrderDate = lObjHstPnsnPmntDcrgDtls.getOrderDate();
				}
			}
			if (lLstRltBillParty != null && !lLstRltBillParty.isEmpty()) {
				for (RltBillParty lObjRltBillParty : lLstRltBillParty) {
					if (lObjRltBillParty.getBankCode() != null && lObjRltBillParty.getBranchCode() != null) {
						lStrBankName = lObjCommonDAO.getBankNameFromBankCode(lObjRltBillParty.getBankCode(), gLangID);
						lStrBranchName = lObjCommonDAO.getBranchNameFromBranchCode(lObjRltBillParty.getBankCode(), lObjRltBillParty.getBranchCode(), gLangID);
						lStrAccountNo = (lObjRltBillParty.getAccntNo() != null) ? lObjRltBillParty.getAccntNo() : "";
					}
					lStrPnsnrName = lStrPnsnrName + lObjRltBillParty.getPartyName() + " / " + lStrBankName + " / " + lStrBranchName + " / " + lStrAccountNo + "$";
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
					lStrPensionerName = lLstBillDtlsElem.get(4).toString();
				}
				if (lLstBillDtlsElem.get(3) != null) {
					lRecAmt = Double.parseDouble(lLstBillDtlsElem.get(3).toString());
				}
				if (lLstBillDtlsElem.get(9) != null) {
					lGrossAmnt = Double.parseDouble(lLstBillDtlsElem.get(9).toString());
				}
				if (lLstBillDtlsElem.get(10) != null) {
					lWithHeldAmnt = Double.parseDouble(lLstBillDtlsElem.get(10).toString());
				}
				// Getting the Value from TrnPensionBillDtls... End...
				lDCRGAmt = lReducedAmt + lRecAmt; // Total DCRG Amount
				lAmntAftrWithHeld = lDCRGAmt - lWithHeldAmnt; // Amount After
																// WithHeld
			}
			/************************************************** Get DCRG BILL DATA FOR PRINT***********STARTS ***********************/
			lStrOffAddr = lObjCVPRestorationLetterDAO.getOffiCeAddr(gStrLocCode);
			lLstDCRGBillDtls = lObjPensionBillDAO.getPrintBillDtls(lStrPnsrCode, gStrLocCode, lStrCurrDate, "DCRG");
			if (!lLstDCRGBillDtls.isEmpty() && lLstDCRGBillDtls.size() > 0) {
				Iterator lObjIterator = lLstDCRGBillDtls.iterator();
				Object[] lArrObj = null;
				while (lObjIterator.hasNext()) {
					lArrObj = (Object[]) lObjIterator.next();
					if (lArrObj[0] != null) {
						lStrAuthorityNo = (String) lArrObj[0];
					} else {
						lStrAuthorityNo = "";
					}
				}
			}
			/************************ Print DCRG bill start *************/
			ReportExportHelper objExport = new ReportExportHelper();
			StringBuilder lSbDcrgBill = new StringBuilder();
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

			lSbFooter.append("Demand No         : G6");
			lSbFooter.append("\r\n");
			lSbFooter.append("Major Head        : 2071 Pension & Other Retirement Benefits.");
			lSbFooter.append("\r\n");
			lSbFooter.append("Minor Head        : 101 Superannuation and Retirement Allowances.");
			lSbFooter.append("\r\n");
			lSbFooter.append("Sub   Head        : 104 DCRG");
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

			lSbDcrgBill.append("<div><pre>");
			lStrHeader = objExport.getReportFileForCvpBill(arrOuter, columnHeading, lSbHeader.toString(), lSbFooter.toString(), null, 79, true, 1);
			lSbDcrgBill.append(lStrHeader);

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
			lineList.add("Name/Bank/Branch/Ac.No : " + lStrPnsnrName);
			lineList.add("Non Plan");
			arrOuter.add(lineList);

			lineList = new ArrayList();
			lineList.add("Authorised by A.G.Mah.Mumbai.	");
			lineList.add("B DCRG");
			arrOuter.add(lineList);

			lineList = new ArrayList();
			lineList.add("Authority No. " +lStrOrderNo + "  "+ lObjSmplDtFormat.format(lDtOrderDate));
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
			lSbDcrgBill.append(lStrHeader);

			lSbDcrgBill.append("</pre></div>");

			/********** Print DCRG Bill End ***********/

			/**************** Print String Starts **************************/
			// lSbPrintString.append("<div><pre>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(space(55) + " <strong><font size='3px'> " +
			// " DCRG Bill " + " </font></strong> ");
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
			// "102 DCRG");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("DETAILED HEAD" + space(9) + ":" + space(2)
			// + "04");
			// lSbPrintString.append("<br>");
			// lSbPrintString.append("BILL TYPE" + space(13) + ":" + space(2) +
			// "11");
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
			// lStrPnsnrName + space(5) +
			// "(NON PLAN AUTHORISED BY A.G. MUMBAI/NAGPUR)");
			// lSbPrintString.append("<br>");
			// if (lStrAuthorityNo != null && lStrAuthorityNo.length() > 0) {
			//
			// lSbPrintString.append("AUTHORITY NO." + space(14) + ":" +
			// space(2) + lStrAuthorityNo);
			// lSbPrintString.append("<br>");
			// lSbPrintString.append(lStrNewLine);
			// }
			// lSbPrintString.append("BILL DATE." + space(17) + ":" + space(2) +
			// DateUtilities.stringFromDate(lStrBillDate));
			// lSbPrintString.append("<br>");
			// // lSbPrintString.append("COMMUTED VALUE OF PENSION"+space(2) +
			// ":"+
			// // space(2)+ String.valueOf(lBDDcrgAmnt.longValue()));
			// // lSbPrintString.append("<br>");
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

			/************************************************** Get DCRG BILL DATA FOR PRINT***********ENDS ***********************/
			/*
			 * if(lStrPnsrCode != null && lStrPnsrCode.length() > 0 ) { //
			 * Getting the ObjectVo of TrnPensionRqstHdr lObjTrnPensionRqstHdrVO
			 * =
			 * lObjTrnPensionRqstHdrDAO.getTrnPensionRqstHdrDtlsFromPnsnerCode(
			 * lStrPnsrCode, lStrStatus, lStrCaseStatus);
			 * if(lObjTrnPensionRqstHdrVO != null) {
			 * if(lObjTrnPensionRqstHdrVO.getDcrgAmount() != null) { lDCRGAmt =
			 * Double
			 * .parseDouble(lObjTrnPensionRqstHdrVO.getDcrgAmount().toString());
			 * } } if(lObjTrnPensionRqstHdrVO.getPpoNo() == null) {
			 * lObjTrnPensionRqstHdrVO =
			 * lObjTrnPensionRqstHdrDAO.getTrnPensionRqstHdrDtlsFromPnsnerCode
			 * (lStrPnsrCode, lStrStatus, "APPROVED"); lDCRGAmt = lReducedAmt -
			 * lRecAmt; } } if(lObjTrnPensionRqstHdrVO != null) { // Getting the
			 * Value from TrnPensionRqstHdr... Start... lStrPPONo =
			 * lObjTrnPensionRqstHdrVO.getPpoNo(); // Getting the Value from
			 * TrnPensionRqstHdr... End... }
			 */
			if (lStrPnsrCode != null && lStrPnsrCode.length() > 0) {
				// Getting the ObjectVo of MstPensionerHdr... Start...
				lObjMstPensionerHdrVO = lObjMstPensionerHdrDAO.getMstPensionerHdrDtls(lStrPnsrCode);
				// lStrFirstname = lObjMstPensionerHdrVO.getFirstName();
				// lStrMiddlename =
				// (lObjMstPensionerHdrVO.getMiddleName()!=null) ?
				// lObjMstPensionerHdrVO.getMiddleName() + " " :"";
				// lStrLastname = lObjMstPensionerHdrVO.getLastName();
				// lStrPnsnerName = lStrFirstname + ' ' + lStrMiddlename +
				// lStrLastname;
				lStrOfficeAddr = lObjMstPensionerHdrVO.getOfficeAddr();
				lStrDesignation = lObjMstPensionerHdrVO.getDesignation();
				lStrDesgDesc = lObjTrnPensionBillDtlsDAO.getDesgDesc(lStrDesignation);
				// Getting the ObjectVo of MstPensionerHdr... End...
			}
			inputMap.put("PPONo", lStrPPONo);
			inputMap.put("PnsnBillTokenNo", lStrTokenNo);
			inputMap.put("Name", lStrPensionerName);
			inputMap.put("Designation", lStrDesgDesc);
			inputMap.put("OfficeAddr", lStrOfficeAddr);
			inputMap.put("DCRGAmount", lDCRGAmt); // Gross Amount means total
			// dcrg amount
			// NEWLY ADDED FIELDS STARTS
			inputMap.put("WithHeldAmount", lWithHeldAmnt);
			inputMap.put("AmountAfterWithHeld", lAmntAftrWithHeld);
			inputMap.put("GrossAmount", lGrossAmnt);
			// NEWLY ADDED FIELDS ENDS
			inputMap.put("RecoveryAmount", lRecAmt);
			inputMap.put("NetAmount", lReducedAmt);
			inputMap.put("BillDate", lStrBillDate);
			inputMap.put("MapDCRGData", lMapDCRGData);
			inputMap.put("PnsnrCode", lStrPnsrCode);
			inputMap.put("PrintDCRGBillString", lSbDcrgBill);
			inputMap.put("schemeNo", lStrSchemeCode);
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			resObj.setThrowable(e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocCode = SessionHelper.getLocationCode(inputMap);
			gDtCurrDt = DBUtility.getCurrentDateFromDB();
			gLangID = SessionHelper.getLangId(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}

	}

	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	private String getChar(int count, String ele) {

		StringBuffer lSBSpace = new StringBuffer();
		for (int i = 0; i < count - 1; i++) {
			lSBSpace.append(ele);
		}
		return lSBSpace.toString();
	}
}