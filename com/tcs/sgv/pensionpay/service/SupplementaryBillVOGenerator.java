/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 3, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;
//com.tcs.sgv.pensionpay.service.SupplementaryBillVOGenerator
/**
 * Class Description - 
 *
 *
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0
 * Jun 3, 2011
 */

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
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.PensionBillDAO;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionBillHdrDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRecoveryDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionRecoveryDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionSupplyBillDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionSupplyBillDtlsDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.HstReEmploymentDtls;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSupplyBillDtls;


/**
 * DCRG Bill VO Generator.
 * 
 * @author Aparna Kansara
 * @version 1.0
 */

public class SupplementaryBillVOGenerator extends ServiceImpl implements VOGeneratorService {

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
	
	private String gStrBillType = null;

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
		TrnPensionBillHdr lObjPensionBillHdr = null;
		TrnPensionBillDtls lObjPensionBillDtls = null;
		List<TrnPensionRecoveryDtls> lObjTrnPensionRecoveryDtls = null;
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = null;
		RltPensioncaseBill lObjRltPensioncaseBill = null;
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtls = null;
		HstCommutationDtls lObjHstCommutationDtls = null;
		try {
			setSessionInfo(objArgs);
			String lStrPPONo = StringUtility.getParameter("txtPPONo", request).trim();
			String lStrPnsnrCode = StringUtility.getParameter("hidDPnsnrCode", request).trim();
			gStrBillType = StringUtility.getParameter("radioSupBillType", request);
			
			objArgs.put("lStrPnsnrCode", lStrPnsnrCode);
			
			if (lStrPPONo != null) {
					lObjPensionBillHdr = generateTrnPensionBillHdrVO(objArgs);
					lObjPensionBillDtls = generateTrnPensionBillDtlsVO(objArgs);
					lObjTrnPensionRecoveryDtls = generateTrnPensionRecoveryDtls(objArgs);
					lObjTrnPensionSupplyBillDtls = generateSupplyBillDtls(objArgs);
					lObjRltPensioncaseBill = generateRltPensioncaseBill(objArgs);
					if(!"".equals(gStrBillType))
					{
						if("DCRG".equals(gStrBillType))
						{
							lObjHstPnsnPmntDcrgDtls = generateHstPnsnPmntDcrgDtlsVO(objArgs);
							objArgs.put("HstPnsnPmntDcrgDtls", lObjHstPnsnPmntDcrgDtls);
						}
						if("CVP".equals(gStrBillType))
						{
							lObjHstCommutationDtls = generateHstCommutationDtlsVO(objArgs);
							objArgs.put("HstCommutationDtls", lObjHstCommutationDtls);
						}
					}
					objArgs.put("SuppTrnPensionBillHdrVO", lObjPensionBillHdr);
					objArgs.put("SuppTrnPensionBillDtlsVO", lObjPensionBillDtls);
					objArgs.put("SuppTrnPensionRecoveryDtls", lObjTrnPensionRecoveryDtls);
					objArgs.put("SuppTrnPensionSupplyBillDtlsVO", lObjTrnPensionSupplyBillDtls);
					objArgs.put("SuppRltPensioncaseBillVO", lObjRltPensioncaseBill);
					objArgs.put("SuppBillType", gStrBillType);
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
		ServiceLocator srvcLoc = (ServiceLocator) lMapInput.get("serviceLocator");
		String lStrSuppAmt = null;
		String lStrRecoveryAmt = null;
		String lStrNetAmt = null;
		String lStrPensionBillHdrId = null;
		try {
			lStrPensionBillHdrId = StringUtility.getParameter("hidPensionBillHdrId", request);
			if (lStrPensionBillHdrId != null && !"".equalsIgnoreCase(lStrPensionBillHdrId)) {
				TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class, srvcLoc.getSessionFactory());
				lObjPensionBillHdr = lObjTrnPensionBillHdrDAO.read(Long.parseLong(lStrPensionBillHdrId.trim()));
				lObjPensionBillHdr.setUpdatedDate(gDtCurrDt);
				lObjPensionBillHdr.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjPensionBillHdr.setUpdatedUserId(new BigDecimal(gLngUserId));
			} else {
				lObjPensionBillHdr.setCreatedDate(gDtCurrDt);
				lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
			}
			lObjPensionBillHdr.setBillDate(gDtCurrDt);
			lObjPensionBillHdr.setForMonth(Integer.parseInt(new SimpleDateFormat("yyyyMM").format(gDtCurrDt)));
			lObjPensionBillHdr.setCreatedUserId(new BigDecimal(gLngUserId));
			lObjPensionBillHdr.setCreatedPostId(new BigDecimal(gLngPostId));
			lObjPensionBillHdr.setCreatedDate(gDtCurrDt);
			lObjPensionBillHdr.setLocationCode(gStrLocationCode);
			if (StringUtility.getParameter("hidHeadcode", request).trim() != "") {
				lObjPensionBillHdr.setHeadCode(new BigDecimal(StringUtility.getParameter("hidHeadcode", request).trim()));
			}
			if (StringUtility.getParameter("hidScheme", request).trim() != "") {
				lObjPensionBillHdr.setScheme(StringUtility.getParameter("hidScheme", request).trim());
			}
			lObjPensionBillHdr.setTrnCounter(1);
			lObjPensionBillHdr.setRejected(0);

			lStrSuppAmt = StringUtility.getParameter("txtGrossAmt", request).trim();
			lStrRecoveryAmt = StringUtility.getParameter("txtRecAmt", request).trim();
			lStrNetAmt = StringUtility.getParameter("txtNetAmt", request).trim();

			lObjPensionBillHdr.setGrossAmount(new BigDecimal(lStrSuppAmt.length() > 0 ? lStrSuppAmt : "0"));
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
		ServiceLocator servLoc = (ServiceLocator) lMapInput.get("serviceLocator");
		String lStrBillType="";
		String lStrPPONo = null;
		String lStrRecoveryAmt = null;
		String lStrNetAmt = null;
		TrnPensionBillDtls lObjPensionBillDtls = new TrnPensionBillDtls();
		String lStrPensionerCode = "";
		String lStrPensionerName = null;
		String lStrArrearAmount = null;
		String lStrPensionAmount = null;
		String lStrADPAmount = null;
		String lStrDPAmount = null;
		String lStrIR1Amount = null;
		String lStrIR2Amount = null;
		String lStrIR3Amount = null;
		String lStrDAAmount = null;
		String lStrCvpAmount = null;
		String lStrPeonAllowAmount = null;
		String lStrMedicalAllowAmount = null;
		String lStrGallantryAmount = null;
		String lStrOtherBenefit = null;
		String lStrOther1 = null;
		String lStrOther2 = null;
		String lStrOther3 = null;
		String lStrPensionBillHdrId = null;
		String lStrArrearDtls = null;
		String lStrArrear6PCAmt = "";
		
		try {
			lStrPensionBillHdrId = StringUtility.getParameter("hidPensionBillHdrId", request);
			if (lStrPensionBillHdrId != null && !"".equalsIgnoreCase(lStrPensionBillHdrId)) {
				TrnPensionBillDtlsDAO lObjTrnPensionBillDtlsDAO = new TrnPensionBillDtlsDAOImpl(TrnPensionBillDtls.class, servLoc.getSessionFactory());
				lObjPensionBillDtls = lObjTrnPensionBillDtlsDAO.getTrnPensionBillDtlsVo(Long.parseLong(lStrPensionBillHdrId));

			}
			//lStrBillType = StringUtility.getParameter("radioSupBillType", request);
			lStrPPONo = StringUtility.getParameter("txtPPONo", request).trim();
			if(!"".equals(gStrBillType))
			{
				if("PENSION".equals(gStrBillType))
				{
					lStrRecoveryAmt = StringUtility.getParameter("txtRecAmt", request).trim();
					lStrNetAmt = StringUtility.getParameter("txtNetAmt", request).trim();
					lStrOther1 = StringUtility.getParameter("txtOthArrearAmt", request).trim();
					lStrOther2 = StringUtility.getParameter("txtOthArrearAmt2", request).trim();
					lStrOther3 = StringUtility.getParameter("txtOthArrearAmt3", request).trim();
					lStrArrearDtls = StringUtility.getParameter("arrearDtlsStr", request).trim();
					
					lStrPensionAmount = StringUtility.getParameter("txtPensionAmt", request).trim();
					lStrADPAmount = StringUtility.getParameter("txtADPAmt", request).trim();
					lStrDPAmount = StringUtility.getParameter("txtDPAmt", request).trim();
					lStrIR1Amount = StringUtility.getParameter("txtIR1Amt", request).trim();
					lStrIR2Amount = StringUtility.getParameter("txtIR2Amt", request).trim();
					lStrIR3Amount = StringUtility.getParameter("txtIR3Amt", request).trim();
					lStrDAAmount = StringUtility.getParameter("txtDAAmt", request).trim();
					lStrCvpAmount = StringUtility.getParameter("txtCvpAmt", request).trim();
					lStrPeonAllowAmount = StringUtility.getParameter("txtPeonAllowAmt", request).trim();
					lStrMedicalAllowAmount = StringUtility.getParameter("txtMedicalAllowAmt", request).trim();
					lStrGallantryAmount = StringUtility.getParameter("txtGallantryAmt", request).trim();
					lStrOtherBenefit = StringUtility.getParameter("txtOtherBenefit", request).trim();
					lStrArrearAmount = StringUtility.getParameter("hdnArrearAmt", request).trim();
					lStrArrear6PCAmt = StringUtility.getParameter("txtArrearOf6PC", request).trim();
				}
				else if("DCRG".equals(gStrBillType))
				{
					lStrRecoveryAmt = StringUtility.getParameter("txtDcrgRecAmt", request).trim();
					lStrNetAmt = StringUtility.getParameter("txtDcrgNetAmt", request).trim();
				}
				else if("CVP".equals(gStrBillType))
				{
					lStrNetAmt = StringUtility.getParameter("txtCvpSanctionedAmt", request).trim();
				}
			}
			
			
			lStrPensionerCode = StringUtility.getParameter("hidDPnsnrCode", request).trim();
			lStrPensionerName = StringUtility.getParameter("txtName", request).trim();
			
			
			if (lStrPPONo != null) {
				if(!"".equals(gStrBillType))
				{
					if("PENSION".equals(gStrBillType))
					{
						lObjPensionBillDtls.setOther1(new BigDecimal(lStrOther1.length() > 0 ? lStrOther1 : "0"));
						lObjPensionBillDtls.setOther2(new BigDecimal(lStrOther2.length() > 0 ? lStrOther2 : "0"));
						lObjPensionBillDtls.setOther3(new BigDecimal(lStrOther3.length() > 0 ? lStrOther3 : "0"));
						lObjPensionBillDtls.setArrearDtls(lStrArrearDtls);
						lObjPensionBillDtls.setPensionAmount(new BigDecimal(lStrPensionAmount.length() > 0 ? lStrPensionAmount : "0"));
						lObjPensionBillDtls.setAdpAmount(new BigDecimal(lStrADPAmount.length() > 0 ? lStrADPAmount : "0"));
						lObjPensionBillDtls.setDpAmount(new BigDecimal(lStrDPAmount.length() > 0 ? lStrDPAmount : "0"));
						lObjPensionBillDtls.setIr1Amount(new BigDecimal(lStrIR1Amount.length() > 0 ? lStrIR1Amount : "0"));
						lObjPensionBillDtls.setIr2Amount(new BigDecimal(lStrIR2Amount.length() > 0 ? lStrIR2Amount : "0"));
						lObjPensionBillDtls.setIr3Amount(new BigDecimal(lStrIR3Amount.length() > 0 ? lStrIR3Amount : "0"));
						lObjPensionBillDtls.setTiAmount(new BigDecimal(lStrDAAmount.length() > 0 ? lStrDAAmount : "0"));
						lObjPensionBillDtls.setCvpMonthAmount(new BigDecimal(lStrCvpAmount.length() > 0 ? lStrCvpAmount : "0"));
						lObjPensionBillDtls.setPeonAllowance(new BigDecimal(lStrPeonAllowAmount.length() > 0 ? lStrPeonAllowAmount : "0"));
						lObjPensionBillDtls.setMedicalAllowenceAmount(new BigDecimal(lStrMedicalAllowAmount.length() > 0 ? lStrMedicalAllowAmount : "0"));
						lObjPensionBillDtls.setGallantryAmount(new BigDecimal(lStrGallantryAmount.length() > 0 ? lStrGallantryAmount : "0"));
						lObjPensionBillDtls.setOtherBenefits(new BigDecimal(lStrOtherBenefit.length() > 0 ? lStrOtherBenefit : "0"));
						lObjPensionBillDtls.setArrearAmount(new BigDecimal(lStrArrearAmount.length() > 0 ? lStrArrearAmount : "0"));
						lObjPensionBillDtls.setArrear6PC(new BigDecimal(lStrArrear6PCAmt.length() > 0 ? lStrArrear6PCAmt : "0"));
					}
					if("PENSION".equals(gStrBillType) || "DCRG".equals(gStrBillType))
					{
						lObjPensionBillDtls.setRecoveryAmount(new BigDecimal(lStrRecoveryAmt.length() > 0 ? lStrRecoveryAmt : "0"));
					}
					lObjPensionBillDtls.setReducedPension(new BigDecimal(lStrNetAmt));
					lObjPensionBillDtls.setPpoNo(lStrPPONo);
					lObjPensionBillDtls.setPensionerCode(lStrPensionerCode);
					lObjPensionBillDtls.setNetAmount(new BigDecimal(lStrNetAmt));
					
					lObjPensionBillDtls.setPensionerName(lStrPensionerName);
					lObjPensionBillDtls.setPayForMonth(Integer.parseInt(new SimpleDateFormat("yyyyMM").format(gDtCurrDt)));
					lObjPensionBillDtls.setTrnCounter(1);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error in : " + e, e);
			throw (e);
		}
		return lObjPensionBillDtls;
	}

	/**
	 * Generate TrnPensionBillDtlsVO from JSP Data
	 * 
	 * @param lMapInput
	 * @return TrnPensionBillDtls
	 */
	public TrnPensionSupplyBillDtls generateSupplyBillDtls(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) lMapInput.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		TrnPensionSupplyBillDtls lSupplyBillDtlsVo = new TrnPensionSupplyBillDtls();
		String lStrBillType = null;
		String lStrPnsnerCode = null;
		String lStrDiffAmt = null;
		String lStrGrossAmt = null;
		String lStrNetAmt = null;
		String lStrPartyName = null;
		String lStrPensionSupplyBillId = null;
		String lStrCvpOrderNo = null;
		String lStrCvpOrderDate = null;
		String lStrTotalCvpAmt = null;
		String lStrGpoNo = null;
		String lStrGpoDate = null;
		
		try {
			setSessionInfo(lMapInput);
			lStrPensionSupplyBillId = StringUtility.getParameter("hidPensionBillSupplyId", request);
			//lStrBillType = StringUtility.getParameter("radioSupBillType", request);
			lStrPnsnerCode = StringUtility.getParameter("hidDPnsnrCode", request);
			lStrDiffAmt = StringUtility.getParameter("txtGrossAmt", request);
			lStrNetAmt = StringUtility.getParameter("txtNetAmt", request);
			lStrGrossAmt = StringUtility.getParameter("txtGrossAmt", request);
			String lStrPaymentMode = StringUtility.getParameter("paymentMode", request);
			if(!"".equals(gStrBillType))
			{
				if("CVP".equals(gStrBillType))
				{
					lStrCvpOrderNo = StringUtility.getParameter("txtCPONumber", request);
					lStrCvpOrderDate = StringUtility.getParameter("txtCPODate", request);
					lStrTotalCvpAmt = StringUtility.getParameter("txtCommutationAmt", request);
				}
				else if("DCRG".equals(gStrBillType))
				{
					lStrGpoNo = StringUtility.getParameter("txtGPONumber", request);
					lStrGpoDate = StringUtility.getParameter("txtGPODate", request);
				}
				
			}
			if (lStrPensionSupplyBillId != null && !"".equalsIgnoreCase(lStrPensionSupplyBillId)) {
				TrnPensionSupplyBillDtlsDAO lObjTrnPensionSupplyBillDtlsDAO = new TrnPensionSupplyBillDtlsDAOImpl(TrnPensionSupplyBillDtls.class, servLoc.getSessionFactory());
				lSupplyBillDtlsVo = (TrnPensionSupplyBillDtls) lObjTrnPensionSupplyBillDtlsDAO.read(Long.parseLong(lStrPensionSupplyBillId));
				lSupplyBillDtlsVo.setUpdatedDate(gDtCurrDt);
				lSupplyBillDtlsVo.setUpdatedPostId(new BigDecimal(gLngPostId));
				lSupplyBillDtlsVo.setUpdatedUserId(new BigDecimal(gLngUserId));
			} else {
				lSupplyBillDtlsVo = new TrnPensionSupplyBillDtls();
				lSupplyBillDtlsVo.setCreatedUserId(new BigDecimal(gLngUserId));
				lSupplyBillDtlsVo.setCreatedPostId(new BigDecimal(gLngPostId));
				lSupplyBillDtlsVo.setCreatedDate(gDtCurrDt);
			}

			lSupplyBillDtlsVo.setBillType(gStrBillType.toUpperCase());
			lSupplyBillDtlsVo.setPensionerCode(lStrPnsnerCode);
			lSupplyBillDtlsVo.setPayMode(lStrPaymentMode);
			// lSupplyBillDtlsVo.setBillNo(billNo);
			lSupplyBillDtlsVo.setDifferenceAmount(new BigDecimal(lStrDiffAmt.length() != 0 ? lStrDiffAmt : "0"));
			lSupplyBillDtlsVo.setPartyName(lStrPartyName);
			lSupplyBillDtlsVo.setGrossAmount(new BigDecimal(lStrGrossAmt.length() != 0 ? lStrGrossAmt : "0"));
			lSupplyBillDtlsVo.setNetAmount(new BigDecimal(lStrNetAmt.length() != 0 ? lStrNetAmt : "0"));
			if(!"".equals(gStrBillType))
			{
				if("CVP".equals(gStrBillType))
				{
					if(!"".equals(lStrCvpOrderNo))
						lSupplyBillDtlsVo.setCvpOrderNo(lStrCvpOrderNo.trim());
					if(!"".equals(lStrCvpOrderDate))
						lSupplyBillDtlsVo.setCvpOrderDate(lObjSimpleDateFormat.parse(lStrCvpOrderDate.trim()));
					if(!"".equals(lStrTotalCvpAmt))
						lSupplyBillDtlsVo.setTotalCvpAmount(new BigDecimal(lStrTotalCvpAmt.trim()));
				}
				else if("DCRG".equals(gStrBillType))
				{
					if(!"".equals(lStrGpoNo))
						lSupplyBillDtlsVo.setGpoNo(lStrGpoNo.trim());
					if(!"".equals(lStrGpoDate))
						lSupplyBillDtlsVo.setGpoDate(lObjSimpleDateFormat.parse(lStrGpoDate.trim()));
					
				}
				
			}
			lSupplyBillDtlsVo.setLocationCode(gStrLocationCode);
		} catch (Exception e) {
			gLogger.error("Error in" + e, e);
			throw (e);
		}
		return lSupplyBillDtlsVo;
	}

	public List<TrnPensionRecoveryDtls> generateTrnPensionRecoveryDtls(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrRecoveryFlag = "";
		String[] lArrStrRecoveryType =null;
		//String lStrBillType = StringUtility.getParameter("radioSupBillType", request);
		if(!"".equals(gStrBillType))
		{
			if("DCRG".equals(gStrBillType))
			{
				 lStrRecoveryFlag = bundleConst.getString("RECOVERY.SUPPDCRG");
				 lArrStrRecoveryType = StringUtility.getParameterValues("txtNature", request);
			}
			else if("PENSION".equals(gStrBillType))
			{
				lStrRecoveryFlag = bundleConst.getString("RECOVERY.SUPPPNSN");
				lArrStrRecoveryType = StringUtility.getParameterValues("cmbRecoveryType", request);
			}
		}
		
		String[] lArrStrAmount = StringUtility.getParameterValues("txtAmount", request);
		String[] lArrStrSchemeCode = StringUtility.getParameterValues("txtSchemeCode", request);
		String lStrPnsnerCode = StringUtility.getParameter("hidDPnsnrCode", request);
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtls = null;
		TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtls = null;
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(servLoc.getSessionFactory());
		String lStrBillNo = null;
		try {
			lStrBillNo = ((StringUtility.getParameter("hidBillNo", request).trim()).length() > 0) ? StringUtility.getParameter("hidBillNo", request).trim() : null;
			if (lStrBillNo != null && lStrBillNo.length() > 0) {
				// inputMap.put("hidBillNo", lStrBillNo);

				Long lLngBillNo = Long.parseLong(StringUtility.getParameter("hidBillNo", request));
				lLstTrnPensionRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
				lLstTrnPensionRecoveryDtls = lObjPensionBillDAO.getTrnPensionRecoveryDtlsForSupp(lStrPnsnerCode, lStrRecoveryFlag, lLngBillNo);

				for (int i = 0; i < lLstTrnPensionRecoveryDtls.size(); i++) {
					TrnPensionRecoveryDtls lObjRecovery = new TrnPensionRecoveryDtls();
					lObjRecovery = lLstTrnPensionRecoveryDtls.get(i);
					TrnPensionRecoveryDtlsDAO lObjTrnPensionRecoveryDtlsDAO = new TrnPensionRecoveryDtlsDAOImpl(TrnPensionRecoveryDtls.class, servLoc.getSessionFactory());
					lObjTrnPensionRecoveryDtlsDAO.delete(lObjRecovery);
				}
			}
			lLstTrnPensionRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
			if (lArrStrAmount.length > 0) {

				for (int i = 0; i < lArrStrAmount.length; i++) {
					lObjTrnPensionRecoveryDtls = new TrnPensionRecoveryDtls();
					lObjTrnPensionRecoveryDtls.setPensionerCode(lStrPnsnerCode);
					lObjTrnPensionRecoveryDtls.setRecoveryFromFlag(lStrRecoveryFlag);
					if("DCRG".equals(gStrBillType))
					{
						lObjTrnPensionRecoveryDtls.setNature(lArrStrRecoveryType[i].toString());
					}
					else if("PENSION".equals(gStrBillType))
					{
						lObjTrnPensionRecoveryDtls.setRecoveryType(lArrStrRecoveryType[i].toString());
					}
					
					lObjTrnPensionRecoveryDtls.setAmount(new BigDecimal(lArrStrAmount[i]));
					lObjTrnPensionRecoveryDtls.setSchemeCode(lArrStrSchemeCode[i]);
					lObjTrnPensionRecoveryDtls.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjTrnPensionRecoveryDtls.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjTrnPensionRecoveryDtls.setCreatedDate(gDtCurrDt);
					lLstTrnPensionRecoveryDtls.add(lObjTrnPensionRecoveryDtls);
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is" + e, e);
			throw (e);
		}

		return lLstTrnPensionRecoveryDtls;
	}

	public HstPnsnPmntDcrgDtls generateHstPnsnPmntDcrgDtlsVO(Map<String, Object> inputMap) throws Exception 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");		
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtlsVO = null;
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		
		try
		{		
			setSessionInfo(inputMap);
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstPnsnPmntDcrgDtls.class,  servLoc.getSessionFactory());	
			String lStrPensionerCode = StringUtility.getParameter("hidDPnsnrCode", request);
			String lStrBillNo = StringUtility.getParameter("hidBillNo", request);
			if(!"".equals(lStrBillNo))
			{
				lObjHstPnsnPmntDcrgDtlsVO=lObjPhysicalCaseInwardDAO.getHstDcrgDtlsFrmBillNo(Long.parseLong(lStrBillNo), lStrPensionerCode);	
			}
			
			if(lObjHstPnsnPmntDcrgDtlsVO == null)
				lObjHstPnsnPmntDcrgDtlsVO = new HstPnsnPmntDcrgDtls();
		    String lStrOrderNo = StringUtility.getParameter("txtGPONumber", request);		    
		    String lStrOrderDate = StringUtility.getParameter("txtGPODate", request);
		    String lStrPaidAmount = StringUtility.getParameter("txtDcrgSanctionedAmt", request);
			String lStrTotalRecoveryAmnt = StringUtility.getParameter("txtDcrgRecAmt", request);
			String lStrAmntAfterWithheld = 	StringUtility.getParameter("txtDcrgNetAmt", request);		
			
									
			lObjHstPnsnPmntDcrgDtlsVO.setDbId(SessionHelper.getDbId(inputMap)); 
			lObjHstPnsnPmntDcrgDtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));
			lObjHstPnsnPmntDcrgDtlsVO.setCreatedPostId(gLngPostId);
			lObjHstPnsnPmntDcrgDtlsVO.setCreatedUserId(gLngUserId);
			lObjHstPnsnPmntDcrgDtlsVO.setCreatedDate(DBUtility.getCurrentDateFromDB());		
			lObjHstPnsnPmntDcrgDtlsVO.setWithHeldAmnt(BigDecimal.ZERO);				
			if(!"".equals(lStrPensionerCode))
					lObjHstPnsnPmntDcrgDtlsVO.setPensionerCode(lStrPensionerCode.trim());
			if(!"".equals(lStrOrderNo))
					lObjHstPnsnPmntDcrgDtlsVO.setOrderNo(lStrOrderNo.trim());
			if(!"".equals(lStrOrderDate))
					lObjHstPnsnPmntDcrgDtlsVO.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate.trim()));
			if(!"".equals(lStrPaidAmount))
					lObjHstPnsnPmntDcrgDtlsVO.setPaidAmount(new BigDecimal(lStrPaidAmount.trim()));
			if(!"".equals(lStrTotalRecoveryAmnt))
					lObjHstPnsnPmntDcrgDtlsVO.setTotalRecoveryAmnt(new BigDecimal(lStrTotalRecoveryAmnt.trim()));
			if(lStrAmntAfterWithheld.length() > 0)
					lObjHstPnsnPmntDcrgDtlsVO.setAmntAfterWithHeld(new BigDecimal(lStrAmntAfterWithheld.trim()));
				
		}
		catch(Exception e)
		{
			gLogger.error("Error is" + e, e);
			throw (e);
		}
		return lObjHstPnsnPmntDcrgDtlsVO;	
	}
	
	public HstCommutationDtls generateHstCommutationDtlsVO(Map<String, Object> inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		HstCommutationDtls lObjHstCommutationDtlsVO = null;
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(HstCommutationDtls.class,  servLoc.getSessionFactory());
			String lStrPensionerCode = StringUtility.getParameter("hidDPnsnrCode", request);
			String lStrBillNo = StringUtility.getParameter("hidBillNo", request);
			if(!"".equals(lStrBillNo))
			{
				lObjHstCommutationDtlsVO=lObjPhysicalCaseInwardDAO.getHstCommutationDtlsFrmBillNo(Long.parseLong(lStrBillNo), lStrPensionerCode);
			}
			if(lObjHstCommutationDtlsVO == null)
				lObjHstCommutationDtlsVO = new HstCommutationDtls();
			
			String lStrCvpOrderNo = StringUtility.getParameter("txtCPONumber", request);
			String lStrCvpOrderDate = StringUtility.getParameter("txtCPODate", request);
			String lStrPaymentAmt = StringUtility.getParameter("txtCvpSanctionedAmt", request);
			String lStrTotalOrderAmount = StringUtility.getParameter("txtCommutationAmt", request);
			
			lObjHstCommutationDtlsVO.setDbId(SessionHelper.getDbId(inputMap));
			lObjHstCommutationDtlsVO.setLocationCode(Long.parseLong(gStrLocationCode));
			lObjHstCommutationDtlsVO.setCreatedUserId(gLngUserId);
			lObjHstCommutationDtlsVO.setCreatedPostId(gLngPostId);
			lObjHstCommutationDtlsVO.setCreatedDate(DBUtility.getCurrentDateFromDB());
			if(!"".equals(lStrPensionerCode))
				lObjHstCommutationDtlsVO.setPensionerCode(lStrPensionerCode.trim());
			if(!"".equals(lStrCvpOrderNo))
				lObjHstCommutationDtlsVO.setOrderNo(lStrCvpOrderNo.trim());
			if(!"".equals(lStrCvpOrderDate))
				lObjHstCommutationDtlsVO.setOrderDate(lObjSimpleDateFormat.parse(lStrCvpOrderDate.trim()));
			if(!"".equals(lStrPaymentAmt))
				lObjHstCommutationDtlsVO.setPaymentAmount(new BigDecimal(lStrPaymentAmt.trim()));
			if(!"".equals(lStrTotalOrderAmount))
				lObjHstCommutationDtlsVO.setTotalOrderAmount(new BigDecimal(lStrTotalOrderAmount.trim()));
		
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;

		}
		return lObjHstCommutationDtlsVO;
	}
	
	public RltPensioncaseBill generateRltPensioncaseBill(Map inputMap) throws Exception {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		RltPensioncaseBill lObjRltPensioncaseBill = new RltPensioncaseBill();
		String lStrPPONo = StringUtility.getParameter("txtPPONo", request);
		String lStrBillType = bundleConst.getString("RECOVERY.SUPP");
		try {
			lObjRltPensioncaseBill = new RltPensioncaseBill();
			lObjRltPensioncaseBill.setPpoNo(lStrPPONo);
			lObjRltPensioncaseBill.setBillType(lStrBillType);
			lObjRltPensioncaseBill.setStatus("Y");
			lObjRltPensioncaseBill.setCreatedDate(gDtCurrDt);
			lObjRltPensioncaseBill.setCreatedPostId(new BigDecimal(gLngPostId));
			lObjRltPensioncaseBill.setCreatedUserId(new BigDecimal(gLngUserId));
			lObjRltPensioncaseBill.setLocationCode(gStrLocationCode);
		} catch (Exception e) {
			gLogger.error("Error is" + e, e);
			throw (e);
		}
		return lObjRltPensioncaseBill;
	}
	/**
	 * Generate TrnPensionBillDtlsVO from JSP Data
	 * 
	 * @param lMapInput
	 * @return TrnPensionBillDtls
	 */
	public List<TrnPensionSupplyBillDtls> generateSupplyBillDtlsVOList(Map<String, Object> lMapInput) throws Exception {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) lMapInput.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<TrnPensionSupplyBillDtls> lLstTrnPensionSupplyBillDtlsVO = new ArrayList<TrnPensionSupplyBillDtls>();
		TrnPensionSupplyBillDtls lObjTrnPensionSupplyBillDtls = new TrnPensionSupplyBillDtls();
		String lStrBillType = "";
		String lStrPensionerCode = "";
		String lStrPpoNo = "";
		String lStrSchemeNo = "";
		String lStrPensionAmt = "";
		String lStrPeonAllwAmt = "";
		String lStrADPAmt = "";
		String lStrMedicalAllwAmt = "";
		String lStrDPAmt = "";
		String lStrGallantryAmt = "";
		String lStrIR1Amt = "";
		String lStrOtherBenefit = "";
		String lStrIR2Amt = "";
		String lStrArrearPayOfPnsn = "";
		String lStrIR3Amt = "";
		String lStrArrearOfDA = "";
		String lStrDAAmt = "";
		String lStrArrearOf6PC = "";
		String lStrInstallmentAmt = "";
		String lStrInstallment5Amt = "";
		String lStrCashAmt = "";
		String lStrNoOfInstallment = "";
		String lStrSixPayCalcFlag = "";
		String lStrCvpAmt = "";
		String lStrArrearDiffOfComtPnsn = "";
		String lStrGrossAmt = "";
		String lStrArrearAnyOtherDiff = "";
		String lStrRecAmt = "";
		String lStrNetAmt = "";
		String lStrBankCode = "";
		String lStrBankBranchCode = "";
		String lStrAccountNo = "";
		String lStrChkAmt = "";
		
		String lStrDiffAmt = null;
		String lStrPartyName = null;
		String lStrPensionSupplyBillId = null;
		String[] lArrStrBillDtls = null;
		String[] lArrStrRecoveryDtls = null;
		String[] lArrStrPensionerName = null;
		String[] lArrStrPartyName = null;
		String[] lArrStrBillAmts = null;
		
		try {
			setSessionInfo(lMapInput);
			lArrStrBillDtls = StringUtility.getParameterValues("hdnBillDtls", request);
			lArrStrRecoveryDtls = StringUtility.getParameterValues("hdnRecoveryDtls", request);
			lArrStrPensionerName = StringUtility.getParameterValues("hdnPensionerName", request);
			lArrStrPartyName = StringUtility.getParameterValues("hdnPartyName", request);
					
			for(int lIntCnt=0;lIntCnt<lArrStrBillDtls.length;lIntCnt++)
			{
				lObjTrnPensionSupplyBillDtls = new TrnPensionSupplyBillDtls();
				lArrStrBillAmts = lArrStrBillDtls[lIntCnt].split("~");
				lStrPensionerCode = lArrStrBillAmts[0];
				lStrPpoNo = lArrStrBillAmts[1];
				lStrSchemeNo = lArrStrBillAmts[2];
				lStrPensionAmt = lArrStrBillAmts[3];
				lStrPeonAllwAmt = lArrStrBillAmts[4];
				lStrADPAmt = lArrStrBillAmts[5];
				lStrMedicalAllwAmt = lArrStrBillAmts[6];
				lStrDPAmt = lArrStrBillAmts[7];
				lStrGallantryAmt = lArrStrBillAmts[8];
				lStrIR1Amt = lArrStrBillAmts[9];
				lStrOtherBenefit = lArrStrBillAmts[10];
				lStrIR2Amt = lArrStrBillAmts[11];
				lStrArrearPayOfPnsn = lArrStrBillAmts[12];
				lStrIR3Amt = lArrStrBillAmts[13];
				lStrArrearOfDA = lArrStrBillAmts[14];
				lStrDAAmt = lArrStrBillAmts[15];
				lStrArrearOf6PC = lArrStrBillAmts[16];
				lStrInstallmentAmt = lArrStrBillAmts[17];
				lStrInstallment5Amt = lArrStrBillAmts[18];
				lStrCashAmt = lArrStrBillAmts[19];
				lStrNoOfInstallment = lArrStrBillAmts[20];
				lStrSixPayCalcFlag = lArrStrBillAmts[21];
				lStrCvpAmt = lArrStrBillAmts[22];
				lStrArrearDiffOfComtPnsn = lArrStrBillAmts[23];
				lStrGrossAmt = lArrStrBillAmts[24];
				lStrArrearAnyOtherDiff = lArrStrBillAmts[25];
				lStrRecAmt = lArrStrBillAmts[26];
				lStrNetAmt = lArrStrBillAmts[27];
				lStrBankCode = lArrStrBillAmts[28];
				lStrBankBranchCode = lArrStrBillAmts[29];
				lStrAccountNo = lArrStrBillAmts[30];
				lStrChkAmt = lArrStrBillAmts[31];
				
				lObjTrnPensionSupplyBillDtls.setBillType(gStrBillType.toUpperCase());
				lObjTrnPensionSupplyBillDtls.setPensionerCode(lStrPensionerCode);
				lObjTrnPensionSupplyBillDtls.setPpoNo(lStrPpoNo);
				lObjTrnPensionSupplyBillDtls.setPartyName(lArrStrPartyName[lIntCnt]);
				lObjTrnPensionSupplyBillDtls.setPensionerName(lArrStrPensionerName[lIntCnt]);
				lObjTrnPensionSupplyBillDtls.setSchemeCode(lStrSchemeNo);
				lObjTrnPensionSupplyBillDtls.setPensionAmount(new BigDecimal(lStrPensionAmt.length() > 0 ? lStrPensionAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setPeonAllowance(new BigDecimal(lStrPeonAllwAmt.length() > 0 ? lStrPeonAllwAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setAdpAmount(new BigDecimal(lStrADPAmt.length() > 0 ? lStrADPAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setMedicalAllowance(new BigDecimal(lStrMedicalAllwAmt.length() > 0 ? lStrMedicalAllwAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setDpAmount(new BigDecimal(lStrDPAmt.length() > 0 ? lStrDPAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setGallantryAmount(new BigDecimal(lStrGallantryAmt.length() > 0 ? lStrGallantryAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setIr1Amount(new BigDecimal(lStrIR1Amt.length() > 0 ? lStrIR1Amt : "0"));
				lObjTrnPensionSupplyBillDtls.setOtherBenefit(new BigDecimal(lStrOtherBenefit.length() > 0 ? lStrOtherBenefit : "0"));
				lObjTrnPensionSupplyBillDtls.setIr2Amount(new BigDecimal(lStrIR2Amt.length() > 0 ? lStrIR2Amt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearPension(new BigDecimal(lStrArrearPayOfPnsn.length() > 0 ? lStrArrearPayOfPnsn : "0"));
				lObjTrnPensionSupplyBillDtls.setIr3Amount(new BigDecimal(lStrIR3Amt.length() > 0 ? lStrIR3Amt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearDA(new BigDecimal(lStrArrearOfDA.length() > 0 ? lStrArrearOfDA : "0"));
				lObjTrnPensionSupplyBillDtls.setDaAmount(new BigDecimal(lStrDAAmt.length() > 0 ? lStrDAAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrear6PC(new BigDecimal(lStrArrearOf6PC.length() > 0 ? lStrArrearOf6PC : "0"));
				lObjTrnPensionSupplyBillDtls.setTotalCvpAmount(new BigDecimal(lStrCvpAmt.length() > 0 ? lStrCvpAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearDiffComtPnsn(new BigDecimal(lStrArrearDiffOfComtPnsn.length() > 0 ? lStrArrearDiffOfComtPnsn : "0"));
				lObjTrnPensionSupplyBillDtls.setGrossAmount(new BigDecimal(lStrGrossAmt.length() > 0 ? lStrGrossAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setArrearAnyOtherDiff(new BigDecimal(lStrArrearAnyOtherDiff.length() > 0 ? lStrArrearAnyOtherDiff : "0"));
				lObjTrnPensionSupplyBillDtls.setDifferenceAmount(new BigDecimal(lStrRecAmt.length() > 0 ? lStrRecAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setNetAmount(new BigDecimal(lStrNetAmt.length() > 0 ? lStrNetAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setBankCode(!"".equals(lStrBankCode) ? lStrBankCode : "");
				lObjTrnPensionSupplyBillDtls.setBranchCode(!"".equals(lStrBankBranchCode) ? lStrBankBranchCode : "");
				lObjTrnPensionSupplyBillDtls.setAccountNo(!"".equals(lStrAccountNo) ? lStrAccountNo : "");
				lObjTrnPensionSupplyBillDtls.setPaidAmount(new BigDecimal(lStrChkAmt.length() > 0 ? lStrChkAmt : "0"));
				lObjTrnPensionSupplyBillDtls.setLocationCode(gStrLocationCode);
				lObjTrnPensionSupplyBillDtls.setCreatedUserId(new BigDecimal(gLngUserId));
				lObjTrnPensionSupplyBillDtls.setCreatedPostId(new BigDecimal(gLngPostId));
				lObjTrnPensionSupplyBillDtls.setCreatedDate(gDtCurrDt);
				lLstTrnPensionSupplyBillDtlsVO.add(lObjTrnPensionSupplyBillDtls);
				
				if(!"".equals(lStrSixPayCalcFlag))
				{
					if("Y".equals(lStrSixPayCalcFlag))
					{
						
					}
				}
			}
			
//			if (lStrPensionSupplyBillId != null && !"".equalsIgnoreCase(lStrPensionSupplyBillId)) {
//				TrnPensionSupplyBillDtlsDAO lObjTrnPensionSupplyBillDtlsDAO = new TrnPensionSupplyBillDtlsDAOImpl(TrnPensionSupplyBillDtls.class, servLoc.getSessionFactory());
//				lSupplyBillDtlsVo = (TrnPensionSupplyBillDtls) lObjTrnPensionSupplyBillDtlsDAO.read(Long.parseLong(lStrPensionSupplyBillId));
//				lSupplyBillDtlsVo.setUpdatedDate(gDtCurrDt);
//				lSupplyBillDtlsVo.setUpdatedPostId(new BigDecimal(gLngPostId));
//				lSupplyBillDtlsVo.setUpdatedUserId(new BigDecimal(gLngUserId));
//			} else {
//				lSupplyBillDtlsVo = new TrnPensionSupplyBillDtls();
//				lSupplyBillDtlsVo.setCreatedUserId(new BigDecimal(gLngUserId));
//				lSupplyBillDtlsVo.setCreatedPostId(new BigDecimal(gLngPostId));
//				lSupplyBillDtlsVo.setCreatedDate(gDtCurrDt);
//			}
//
//			lSupplyBillDtlsVo.setBillType(gStrBillType.toUpperCase());
//			lSupplyBillDtlsVo.setPensionerCode(lStrPnsnerCode);
//			// lSupplyBillDtlsVo.setBillNo(billNo);
//			lSupplyBillDtlsVo.setDifferenceAmount(new BigDecimal(lStrDiffAmt.length() != 0 ? lStrDiffAmt : "0"));
//			lSupplyBillDtlsVo.setPartyName(lStrPartyName);
//			lSupplyBillDtlsVo.setGrossAmount(new BigDecimal(lStrGrossAmt.length() != 0 ? lStrGrossAmt : "0"));
//			lSupplyBillDtlsVo.setNetAmount(new BigDecimal(lStrNetAmt.length() != 0 ? lStrNetAmt : "0"));
//			
//			lSupplyBillDtlsVo.setLocationCode(gStrLocationCode);
		} catch (Exception e) {
			gLogger.error("Error in" + e, e);
			throw (e);
		}
		return lLstTrnPensionSupplyBillDtlsVO;
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
