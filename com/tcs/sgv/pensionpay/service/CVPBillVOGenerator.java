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
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;


/**
 * CVP Bill VO Generator.
 * 
 * @author Soumya Adhikari
 * @version 1.0
 */

public class CVPBillVOGenerator extends ServiceImpl implements VOGeneratorService {

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
	 * Generates VO for insertion of CVP Bill Data
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
		String lStrBillType = bundleConst.getString("RECOVERY.CVP");
		String lStrStatus = bundleConst.getString("STATUS.CRTD");
		Object lObjDataVal = null;

		RltPensioncaseBillDAO lObjRltPensioncaseBillDAO = new RltPensioncaseBillDAOImpl(RltPensioncaseBill.class, servLoc.getSessionFactory());

		try {
			setSessionInfo(objArgs);
			String lStrPPONo = StringUtility.getParameter("txtPPONo", request).trim();
			String lStrPnsnrCode = StringUtility.getParameter("hidDPnsnrCode", request).trim();
			objArgs.put("lStrPnsnrCode", lStrPnsnrCode);
			if (lStrPPONo != null) {
				TrnPensionBillHdr lObjPensionBillHdr = generateTrnPensionBillHdrVO(objArgs);
				TrnPensionBillDtls lObjPensionBillDtls = generateTrnPensionBillDtlsVO(objArgs);
				HstCommutationDtls lObjHstCommutationDtls = generateHstCommutationDtlsVO(objArgs);
				objArgs.put("CVPTrnPensionBillHdrVO", lObjPensionBillHdr);
				objArgs.put("CVPTrnPensionBillDtlsVO", lObjPensionBillDtls);
				objArgs.put("CVPHstCommutationDtlsVO", lObjHstCommutationDtls);
				lLstRltPensioncaseBillId = lObjRltPensioncaseBillDAO.getPKForRltPensioncaseBill(lStrPPONo, lStrBillType, lStrStatus, gStrLocationCode);
				if (lLstRltPensioncaseBillId != null && !lLstRltPensioncaseBillId.isEmpty()) {
					lObjDataVal = lLstRltPensioncaseBillId.get(0);
					lLngRltPensioncaseBillId = Long.valueOf(lObjDataVal.toString());
				}
				objArgs.put("CVPRltPensioncaseBillId", lLngRltPensioncaseBillId);
			}
			objRes.setResultValue(objArgs);
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
	 * Generate TrnPensionBillHdrVO from JSP Data
	 * 
	 * @param Map
	 *            :lMapInput
	 * @return TrnPensionBillHdrVO
	 */
	private TrnPensionBillHdr generateTrnPensionBillHdrVO(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		TrnPensionBillHdr lObjPensionBillHdr = new TrnPensionBillHdr();

		String lStrCVPAmt = null;
		String lStrNetAmt = null;

		try {
			String lStrSchemeNo = StringUtility.getParameter("txtSchemeNo", request);
			lObjPensionBillHdr.setBillDate(gDtCurrDt);
			lObjPensionBillHdr.setForMonth(Integer.parseInt(new SimpleDateFormat("yyyyMM").format(gDtCurrDt)));
			lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
			lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
			lObjPensionBillHdr.setCreatedDate(gDtCurrDt);
			lObjPensionBillHdr.setLocationCode(gStrLocationCode);
			lObjPensionBillHdr.setTrnCounter(1);
			lObjPensionBillHdr.setRejected(0);
			lObjPensionBillHdr.setSchemeCode(lStrSchemeNo);
			if (StringUtility.getParameter("hidHeadcode", request).trim() != "") {
				lObjPensionBillHdr.setHeadCode(new BigDecimal(StringUtility.getParameter("hidHeadcode", request).trim()));
			}
			if (StringUtility.getParameter("hidScheme", request).trim() != "") {
				lObjPensionBillHdr.setScheme(StringUtility.getParameter("hidScheme", request).trim());
			}

			lStrCVPAmt = StringUtility.getParameter("txtCVPAmt", request).trim();
			StringUtility.getParameter("txtRecoveryAmt", request).trim();
			lStrNetAmt = StringUtility.getParameter("txtNetAmt", request).trim();

			lObjPensionBillHdr.setGrossAmount(new BigDecimal(lStrCVPAmt.length() > 0 ? lStrCVPAmt : "0"));
			// lObjPensionBillHdr.setDeductionA(new
			// BigDecimal(lStrRecoveryAmt.length() > 0 ? lStrRecoveryAmt :
			// "0"));
			lObjPensionBillHdr.setNetAmount(new BigDecimal(lStrNetAmt.length() > 0 ? lStrNetAmt : "0"));

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
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
		TrnPensionBillDtls lObjPensionBillDtls = new TrnPensionBillDtls();
		String lStrPPONo = null;
		String lStrNetAmt = null;
		String lStrPensionerCode = null;
		String lStrPensionerName = null;
		try {
			lStrPPONo = StringUtility.getParameter("txtPPONo", request).trim();
			StringUtility.getParameter("txtRecoveryAmt", request).trim();
			lStrNetAmt = StringUtility.getParameter("txtNetAmt", request).trim();
			lStrPensionerCode = StringUtility.getParameter("hidDPnsnrCode", request).trim();
			lStrPensionerName = StringUtility.getParameter("txtName", request).trim();

			if (lStrPPONo != null) {
				lObjPensionBillDtls.setReducedPension(new BigDecimal(lStrNetAmt));
				lObjPensionBillDtls.setNetAmount(new BigDecimal(lStrNetAmt));
				lObjPensionBillDtls.setPpoNo(lStrPPONo);
				lObjPensionBillDtls.setPensionerCode(lStrPensionerCode);
				// lObjPensionBillDtls.setRecoveryAmount(new
				// BigDecimal(lStrRecoveryAmt));
				lObjPensionBillDtls.setPensionerName(lStrPensionerName);
				lObjPensionBillDtls.setPayForMonth(Integer.parseInt(new SimpleDateFormat("yyyyMM").format(gDtCurrDt)));
				lObjPensionBillDtls.setTrnCounter(1);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lObjPensionBillDtls;
	}

	/**
	 * Generate HstCommutationDtlsVO from JSP Data
	 * 
	 * @param Map
	 *            :lMapInput
	 * @return HstCommutationDtlsVO
	 */
	private HstCommutationDtls generateHstCommutationDtlsVO(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		HstCommutationDtls lObjHstCommutationDtlsVO = new HstCommutationDtls();
		String lStrPensionerCode = null;
		String lStrCorrectionFlag = null;
		Integer lIntRowNumber = 1;
		String lStrPayableAmnt = null;
		String lStrCvpAmnt = null;
		String lStrOrderNo = null;
		String lStrOrderDate = null;
		String lStrVoucherNo = null;
		String lStrVoucherDate = null;
		String lStrNameOfPayee = null;
		String lStrBasicPensionAmt = "";
		String lStrCvpMonthlyAmt = "";
		String lStrReducedPensionAmt = "";
		String lStrCvpAuthority = "";
		String lStrCvpStartDate = "";
		String lStrCvpEndDate = "";
		List<RltBillParty> lLstRltBillParty = new ArrayList<RltBillParty>();
		try {
			setSessionInfo(lMapInput);

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
			
			lStrPensionerCode = StringUtility.getParameter("hidDPnsnrCode", request);
			lStrPayableAmnt = StringUtility.getParameter("txtNetAmt", request);
			lStrCvpAmnt = StringUtility.getParameter("txtCVPAmt", request);

			lStrOrderNo = StringUtility.getParameter("hidOrderNo", request);
			lStrOrderDate = StringUtility.getParameter("hidOrderDate", request);
			lStrVoucherNo = StringUtility.getParameter("hidVoucherNo", request);
			lStrVoucherDate = StringUtility.getParameter("hidVoucherDate", request);
			lStrBasicPensionAmt = StringUtility.getParameter("hidBasicPensionAmt", request);
			lStrCvpMonthlyAmt = StringUtility.getParameter("hidCvpMonthlyAmt", request);
			lStrReducedPensionAmt = StringUtility.getParameter("hidReducedPensionAmt", request);
			lStrCvpAuthority = StringUtility.getParameter("hidCvpAuthority", request);
			lStrCvpStartDate = StringUtility.getParameter("hidCvpStartDate", request);
			lStrCvpEndDate = StringUtility.getParameter("hidCvpEndDate", request);

			lObjHstCommutationDtlsVO.setDbId(SessionHelper.getDbId(lMapInput));
			lObjHstCommutationDtlsVO.setLocationCode(Long.valueOf(SessionHelper.getLocationCode(lMapInput)));
			lObjHstCommutationDtlsVO.setCreatedPostId(gLngPostId);
			lObjHstCommutationDtlsVO.setCreatedUserId(gLngUserId);
			lObjHstCommutationDtlsVO.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lObjHstCommutationDtlsVO.setUpdatedDate(DBUtility.getCurrentDateFromDB());
			lObjHstCommutationDtlsVO.setUpdatedPostId(gLngPostId);
			lObjHstCommutationDtlsVO.setUpdatedUserId(gLngUserId);
			lObjHstCommutationDtlsVO.setNameOfPayee(lStrNameOfPayee);
			lObjHstCommutationDtlsVO.setTrsryForPayment(SessionHelper.getLocationCode(lMapInput));
			
			if (!"".equals(lStrPensionerCode) && lStrPensionerCode.length() > 0) {
				lObjHstCommutationDtlsVO.setPensionerCode(lStrPensionerCode.trim());
			}
			if (!"".equals(lStrPayableAmnt) && lStrPayableAmnt.length() > 0) {
				lObjHstCommutationDtlsVO.setPaymentAmount(new BigDecimal(lStrPayableAmnt));
			}
			if (!"".equals(lStrCvpAmnt) && lStrCvpAmnt.length() > 0) {
				lObjHstCommutationDtlsVO.setTotalOrderAmount(new BigDecimal(lStrCvpAmnt));
			}
			if (!"".equals(lStrOrderNo) && lStrOrderNo.length() > 0) {
				lObjHstCommutationDtlsVO.setOrderNo(lStrOrderNo.trim());
			}
			if (!"".equals(lStrOrderDate) && lStrOrderDate.length() > 0) {
				lObjHstCommutationDtlsVO.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate));
			}
			if (!"".equals(lStrVoucherNo) && lStrVoucherNo.length() > 0) {
				lObjHstCommutationDtlsVO.setVoucherNo(lStrVoucherNo.trim());
			}
			if (!"".equals(lStrVoucherDate) && lStrVoucherDate.length() > 0) {
				lObjHstCommutationDtlsVO.setVoucherDate(lObjSimpleDateFormat.parse(lStrVoucherDate));
			}
			if (!"".equals(lStrBasicPensionAmt) && lStrBasicPensionAmt.length() > 0) {
				lObjHstCommutationDtlsVO.setBasicPensionAmt(new BigDecimal(lStrBasicPensionAmt.trim()));
			}
			if (!"".equals(lStrCvpMonthlyAmt) && lStrCvpMonthlyAmt.length() > 0) {
				lObjHstCommutationDtlsVO.setCvpMonthlyAmt(new BigDecimal(lStrCvpMonthlyAmt));
			}
			if (!"".equals(lStrReducedPensionAmt) && lStrReducedPensionAmt.length() > 0) {
				lObjHstCommutationDtlsVO.setReducedPensionAmt(new BigDecimal(lStrReducedPensionAmt));
			}
			if (!"".equals(lStrCvpAuthority) && lStrCvpAuthority.length() > 0) {
				lObjHstCommutationDtlsVO.setCvpAuthority(lStrCvpAuthority.trim());
			}
			if (!"".equals(lStrCvpStartDate) && lStrCvpStartDate.length() > 0) {
				lObjHstCommutationDtlsVO.setCvpStartDate(lObjSimpleDateFormat.parse(lStrCvpStartDate));
			}
			if (!"".equals(lStrCvpEndDate) && lStrCvpEndDate.length() > 0) {
				lObjHstCommutationDtlsVO.setCvpEndDate(lObjSimpleDateFormat.parse(lStrCvpEndDate));
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lObjHstCommutationDtlsVO;

	}

	private void setSessionInfo(Map inputMap) {

		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gDtCurrDt = DBUtility.getCurrentDateFromDB();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
	}
}
