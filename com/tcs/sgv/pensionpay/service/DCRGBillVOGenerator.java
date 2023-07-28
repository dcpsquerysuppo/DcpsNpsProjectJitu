package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.RltBillParty;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAO;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;


/**
 * DCRG Bill VO Generator.
 * 
 * @author Aparna Kansara
 * @version 1.0
 */

public class DCRGBillVOGenerator extends ServiceImpl implements VOGeneratorService {

	/* Global Variable for PostId */
	private Long gLngPostId = null;

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Global Variable for Current Date */
	private Date gDtCurrDt = null;

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for Location Code */
	private String gStrLocationCode = null;

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	/**
	 * Generates VO for insertion of DCRG Bill Data
	 * 
	 * @param Map
	 *            :objArgs
	 * @return ResultObject
	 */
	public ResultObject generateMap(Map objArgs) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) objArgs.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) objArgs.get("serviceLocator");
		List lLstRltPensioncaseBillId = null;
		Long lLngRltPensioncaseBillId = null;
		String lStrBillType = bundleConst.getString("RECOVERY.DCRG");
		String lStrStatus = bundleConst.getString("STATUS.CRTD");
		TrnPensionBillHdr lObjPensionBillHdr = null;
		TrnPensionBillDtls lObjPensionBillDtls = null;
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtls = null;
		Object lObjDataVal = null;
		RltPensioncaseBillDAO lObjRltPensioncaseBillDAO = new RltPensioncaseBillDAOImpl(RltPensioncaseBill.class, servLoc.getSessionFactory());
		try {
			setSessionInfo(objArgs);
			String lStrPPONo = StringUtility.getParameter("txtPPONo", request).trim();
			String lStrPnsnrCode = StringUtility.getParameter("hidDPnsnrCode", request).trim();
			objArgs.put("lStrPnsnrCode", lStrPnsnrCode);
			if (lStrPPONo != null) {
				lObjPensionBillHdr = generateTrnPensionBillHdrVO(objArgs);
				lObjPensionBillDtls = generateTrnPensionBillDtlsVO(objArgs);
				lObjHstPnsnPmntDcrgDtls = generateHstPnsnPmntDcrgDtlsVO(objArgs);
				objArgs.put("DCRGTrnPensionBillHdrVO", lObjPensionBillHdr);
				objArgs.put("DCRGTrnPensionBillDtlsVO", lObjPensionBillDtls);
				objArgs.put("DCRGHstPnsnPmntDcrgDtlsVO", lObjHstPnsnPmntDcrgDtls);
				lLstRltPensioncaseBillId = lObjRltPensioncaseBillDAO.getPKForRltPensioncaseBill(lStrPPONo, lStrBillType, lStrStatus, gStrLocationCode);
				if (lLstRltPensioncaseBillId != null && !lLstRltPensioncaseBillId.isEmpty()) {
					lObjDataVal = lLstRltPensioncaseBillId.get(0);
					lLngRltPensioncaseBillId = Long.valueOf(lObjDataVal.toString());
				}
				objArgs.put("DCRGRltPensioncaseBillId", lLngRltPensioncaseBillId);
			}
			objRes.setResultValue(objArgs);
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
	 * Generate TrnPensionBillHdrVO from JSP Data
	 * 
	 * @param Map
	 *            :lMapInput
	 * @return TrnPensionBillHdrVO
	 */
	private TrnPensionBillHdr generateTrnPensionBillHdrVO(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		TrnPensionBillHdr lObjPensionBillHdr = new TrnPensionBillHdr();

		String lStrDCRGAmt = null;
		String lStrRecoveryAmt = null;
		String lStrNetAmt = null;
		String lStrSchemeNo = null;
		try {
			lStrSchemeNo = StringUtility.getParameter("txtSchemeNo", request);
			lObjPensionBillHdr.setBillDate(gDtCurrDt);
			lObjPensionBillHdr.setForMonth(Integer.parseInt(new SimpleDateFormat("yyyyMM").format(gDtCurrDt)));
			lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
			lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
			lObjPensionBillHdr.setCreatedDate(gDtCurrDt);
			lObjPensionBillHdr.setLocationCode(gStrLocationCode);
			lObjPensionBillHdr.setSchemeCode(lStrSchemeNo);
			if (!StringUtility.getParameter("hidHeadcode", request).trim().equals("")) {
				lObjPensionBillHdr.setHeadCode(new BigDecimal(StringUtility.getParameter("hidHeadcode", request).trim()));
			}
			if (!StringUtility.getParameter("hidScheme", request).trim().equals("")) {
				lObjPensionBillHdr.setScheme(StringUtility.getParameter("hidScheme", request).trim());
			}
			lObjPensionBillHdr.setTrnCounter(1);
			lObjPensionBillHdr.setRejected(0);

			lStrDCRGAmt = StringUtility.getParameter("txtDCRGAmt", request).trim();
			lStrRecoveryAmt = StringUtility.getParameter("txtRecAmt", request).trim();
			lStrNetAmt = StringUtility.getParameter("txtNetAmt", request).trim();

			lObjPensionBillHdr.setGrossAmount(new BigDecimal(lStrDCRGAmt.length() > 0 ? lStrDCRGAmt : "0"));
			lObjPensionBillHdr.setDeductionA(new BigDecimal(lStrRecoveryAmt.length() > 0 ? lStrRecoveryAmt : "0"));
			lObjPensionBillHdr.setNetAmount(new BigDecimal(lStrNetAmt.length() > 0 ? lStrNetAmt : "0"));

		} catch (Exception e) {
			gLogger.error("Error in : " + e, e);
			throw (e);
		}
		return lObjPensionBillHdr;
	}

	/**
	 * Generate TrnPensionBillDtlsVO from JSP Data
	 * 
	 * @param Map
	 *            :lMapInput
	 * @return TrnPensionBillDtlsVO
	 */
	private TrnPensionBillDtls generateTrnPensionBillDtlsVO(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		String lStrPPONo = null;
		String lStrRecoveryAmt = null;
		String lStrNetAmt = null;
		TrnPensionBillDtls lObjPensionBillDtls = new TrnPensionBillDtls();
		String lStrPensionerCode = "";
		String lStrPensionerName = null;
		String lStrWithHeldAmnt = null;
		try {
			lStrPPONo = StringUtility.getParameter("txtPPONo", request).trim();
			lStrRecoveryAmt = StringUtility.getParameter("txtRecAmt", request).trim();
			lStrNetAmt = StringUtility.getParameter("txtNetAmt", request).trim();
			lStrPensionerCode = StringUtility.getParameter("hidDPnsnrCode", request).trim();
			lStrPensionerName = StringUtility.getParameter("txtName", request).trim();
			lStrWithHeldAmnt = StringUtility.getParameter("txtWithheldAmnt", request).trim();
			if (lStrPPONo != null) {
				lObjPensionBillDtls.setReducedPension(new BigDecimal(lStrNetAmt));
				lObjPensionBillDtls.setNetAmount(new BigDecimal(lStrNetAmt));
				lObjPensionBillDtls.setPpoNo(lStrPPONo);
				lObjPensionBillDtls.setPensionerCode(lStrPensionerCode);
				lObjPensionBillDtls.setRecoveryAmount(new BigDecimal(lStrRecoveryAmt));
				lObjPensionBillDtls.setPensionerName(lStrPensionerName);
				lObjPensionBillDtls.setPayForMonth(Integer.parseInt(new SimpleDateFormat("yyyyMM").format(gDtCurrDt)));
				lObjPensionBillDtls.setTrnCounter(1);
				lObjPensionBillDtls.setWithHeldAmnt(new BigDecimal(lStrWithHeldAmnt));
			}
		} catch (Exception e) {
			gLogger.error("Error in : " + e, e);
			throw (e);
		}
		return lObjPensionBillDtls;
	}

	/**
	 * Generate HstPnsnPmntDcrgDtlsVO from JSP Data
	 * 
	 * @param Map
	 *            :lMapInput
	 * @return HstPnsnPmntDcrgDtlsVO
	 */
	private HstPnsnPmntDcrgDtls generateHstPnsnPmntDcrgDtlsVO(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String lStrPPONo = null;
		String lStrRecoveryAmt = null;
		String lStrNetAmt = null;
		String lStrWithHeldAmnt = null;
		String lStrAmntAfterWithHeld = null;
		String lStrTotalOrderAmnt = null; // Total DCRG Amount or Total Gratuity
											// Amount
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtls = new HstPnsnPmntDcrgDtls();
		String lStrPensionerCode = null;
		String lStrOrderNo = null;
		String lStrOrderDate = null;
		String lStrVoucherNo = null;
		String lStrVoucherDate = null;
		String lStrPayingAuth = null;
		String lStrNameOfPayee = null;
		List<RltBillParty> lLstRltBillParty = new ArrayList<RltBillParty>();
		try {
			lLstRltBillParty = (List<RltBillParty>) lMapInput.get("BillPartyDtls");
			if(!lLstRltBillParty.isEmpty())
			{
				for(RltBillParty lObjRltBillParty : lLstRltBillParty)
				{
					if(lStrNameOfPayee != null)
						lStrNameOfPayee = lStrNameOfPayee +" / " +lObjRltBillParty.getPartyName();
					else
						lStrNameOfPayee = lObjRltBillParty.getPartyName();
				}
			}
			lStrRecoveryAmt = StringUtility.getParameter("txtRecAmt", request).trim();
			lStrNetAmt = StringUtility.getParameter("txtNetAmt", request).trim();
			lStrPensionerCode = StringUtility.getParameter("hidDPnsnrCode", request).trim();
			lStrWithHeldAmnt = StringUtility.getParameter("txtWithheldAmnt", request).trim();
			lStrAmntAfterWithHeld = StringUtility.getParameter("txtAmntAfterWithHeld", request).trim();
			lStrTotalOrderAmnt = StringUtility.getParameter("txtDCRGAmt", request).trim();

			lStrOrderNo = StringUtility.getParameter("hidOrderNo", request);
			lStrOrderDate = StringUtility.getParameter("hidOrderDate", request);
			lStrVoucherNo = StringUtility.getParameter("hidVoucherNo", request);
			lStrVoucherDate = StringUtility.getParameter("hidVoucherDate", request);
			lStrPayingAuth = StringUtility.getParameter("hidPayingAuth", request);

			lObjHstPnsnPmntDcrgDtls.setDbId(SessionHelper.getDbId(lMapInput));
			lObjHstPnsnPmntDcrgDtls.setLocationCode(Long.valueOf(SessionHelper.getLocationCode(lMapInput)));
			lObjHstPnsnPmntDcrgDtls.setCreatedPostId(gLngPostId);
			lObjHstPnsnPmntDcrgDtls.setCreatedUserId(gLngUserId);
			lObjHstPnsnPmntDcrgDtls.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lObjHstPnsnPmntDcrgDtls.setUpdatedDate(DBUtility.getCurrentDateFromDB());
			lObjHstPnsnPmntDcrgDtls.setUpdatedPostId(gLngPostId);
			lObjHstPnsnPmntDcrgDtls.setUpdatedUserId(gLngUserId);
			lObjHstPnsnPmntDcrgDtls.setNameOfPayee(lStrNameOfPayee);
			lObjHstPnsnPmntDcrgDtls.setTrsryForPayment(SessionHelper.getLocationCode(lMapInput));
			
			if (!"".equals(lStrPensionerCode) && lStrPensionerCode.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setPensionerCode(lStrPensionerCode);
			}
			if (!"".equals(lStrNetAmt) && lStrNetAmt.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setPaidAmount(new BigDecimal(lStrNetAmt)); // PAID_AMOUNT
			}
			if (!"".equals(lStrRecoveryAmt) && lStrRecoveryAmt.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setTotalRecoveryAmnt(new BigDecimal(lStrRecoveryAmt)); // TOTAL_RECOVERY_AMOUNT
			}
			if (!"".equals(lStrTotalOrderAmnt) && lStrTotalOrderAmnt.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setTotalOrderAmount(new BigDecimal(lStrTotalOrderAmnt));
			}
			if (!"".equals(lStrWithHeldAmnt) && lStrWithHeldAmnt.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setWithHeldAmnt(new BigDecimal(lStrWithHeldAmnt));
			}
			if (!"".equals(lStrAmntAfterWithHeld) && lStrAmntAfterWithHeld.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setAmntAfterWithHeld(new BigDecimal(lStrAmntAfterWithHeld));
			}
			if (!"".equals(lStrOrderNo) && lStrOrderNo.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setOrderNo(lStrOrderNo.trim());
			}
			if (!"".equals(lStrOrderDate) && lStrOrderDate.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate));
			}
			if (!"".equals(lStrVoucherNo) && lStrVoucherNo.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setVoucherNo(lStrVoucherNo.trim());
			}
			if (!"".equals(lStrVoucherDate) && lStrVoucherDate.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setVoucherDate(lObjSimpleDateFormat.parse(lStrVoucherDate));
			}
			if (!"".equals(lStrPayingAuth) && lStrPayingAuth.length() > 0) {
				lObjHstPnsnPmntDcrgDtls.setPaymentAuthority(lStrPayingAuth.trim());
			}

		} catch (Exception e) {
			gLogger.error("Error in : " + e, e);
			throw (e);
		}
		return lObjHstPnsnPmntDcrgDtls;
	}

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gDtCurrDt = DBUtility.getCurrentDateFromDB();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is: " + e, e);
			throw (e);
		}
	}
}
