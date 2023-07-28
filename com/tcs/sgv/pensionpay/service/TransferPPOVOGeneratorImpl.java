/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 30, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.TransferPPODAO;
import com.tcs.sgv.pensionpay.dao.TransferPPODAOImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionTransferDtls;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 30, 2011
 */
public class TransferPPOVOGeneratorImpl extends ServiceImpl implements TransferPPOVOGenerator {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	Log gLogger = LogFactory.getLog(getClass());

	Date gDateDBDate = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	// Sets session information in the global variables
	private void setSessionInfo(Map inputMap) {

		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gDateDBDate = DBUtility.getCurrentDateFromDB();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TransferPPOVOGenerator#generateMap(java.util
	 * .Map)
	 */
	public ResultObject generateMap(Map inputMap) {

		gLogger.info("In generateMap of TransferPPOVOGeneratorImpl........");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {

			TrnPensionTransferDtls lObjTrnPensionTransferDtlsVO = new TrnPensionTransferDtls();
			lObjTrnPensionTransferDtlsVO = generateTrnPensionTransferDtlsVO(inputMap);
			inputMap.put("lObjTrnPensionTransferDtlsVO", lObjTrnPensionTransferDtlsVO);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			//e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			gLogger.info("Error is  " + e);
		}

		return objRes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.service.TransferPPOVOGenerator#
	 * generateTrnPensionTransferDtlsVO(java.util.Map)
	 */
	public TrnPensionTransferDtls generateTrnPensionTransferDtlsVO(Map inputMap) {

		gLogger.info("In generateTrnPensionTransferDtlsVO of TransferPPOVOGeneratorImpl........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		TrnPensionTransferDtls lObjTrnPensionTransferDtlsVO = new TrnPensionTransferDtls();
		SimpleDateFormat lObjSmplDtFormat = new SimpleDateFormat("dd/MM/yyyy");
		TransferPPODAO lObjTransferPPODAO = null;
		List lLstBillDtls = new ArrayList();
		List<Short> lLstBillStatus = new ArrayList<Short>();
		TrnPensionBillDtls lObjTrnPensionBillDtls = new TrnPensionBillDtls();
		try {
			setSessionInfo(inputMap);
			String lStrFlag = StringUtility.getParameter("flag", request);
			String lStrToLocation = StringUtility.getParameter("cmbNewTreasury", request);
			String lStrPPONo = StringUtility.getParameter("txtPpoNo", request);
			String lStrFromLocation = StringUtility.getParameter("txtLocCode", request);
			String lStrPensionerCode = StringUtility.getParameter("txtPnsnrCode", request);
			String lStrAgFlag = StringUtility.getParameter("hidAgFlag", request);
			String lStrOtherStateName = StringUtility.getParameter("txtOtherState", request);
			String lStrApplnReceiveDate = StringUtility.getParameter("txtApplnReceiveDt", request);
			lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionTransferDtls.class, serv.getSessionFactory());

			lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
			
			lLstBillDtls = lObjTransferPPODAO.getBillAmntDtls(lStrPensionerCode,lLstBillStatus);
			if(lLstBillDtls != null && !lLstBillDtls.isEmpty())
			{
				lObjTrnPensionBillDtls = (TrnPensionBillDtls) lLstBillDtls.get(0);
			}
			if (!"".equals(lStrFlag) && lStrFlag.length() > 0) {
				if (lStrFlag.equals("1")) // for Auditor means save transfer
											// case
				{
					Long llngRequestId = null;
					llngRequestId = lObjTransferPPODAO.getMaxRequestId(gStrLocationCode);
					if (llngRequestId == 0l) {
						Random random = new Random(1000);
						llngRequestId = Long.valueOf(String.valueOf(random.nextInt()));
						for (int lIntCnt = 0; lIntCnt < 10; lIntCnt++) {
							if (llngRequestId < 0) {
								llngRequestId = Long.valueOf(String.valueOf(random.nextInt()));
							}
							if (llngRequestId > 0) {
								break;
							}
						}
					} else {
						llngRequestId = llngRequestId + 1L;
					}

					if (lStrPPONo.length() > 0) {
						lObjTrnPensionTransferDtlsVO.setPpoNo(lStrPPONo);
					}
					lObjTrnPensionTransferDtlsVO.setReceiveDate(null);
					if (lStrFromLocation.length() > 0) {
						lObjTrnPensionTransferDtlsVO.setFromLocation(lStrFromLocation);
					}

					lObjTrnPensionTransferDtlsVO.setRequestId(String.valueOf(llngRequestId));
					lObjTrnPensionTransferDtlsVO.setReceivePostId(null);
					if(!"".equals(lStrApplnReceiveDate) && lStrApplnReceiveDate.trim().length() > 0)
					{
						lObjTrnPensionTransferDtlsVO.setApplnReceiveDate(lObjSmplDtFormat.parse(lStrApplnReceiveDate.trim()));
					}
					lObjTrnPensionTransferDtlsVO.setLastPayMonthYear(lObjTrnPensionBillDtls.getPayForMonth());
					lObjTrnPensionTransferDtlsVO.setLastPayAmount(lObjTrnPensionBillDtls.getNetAmount());
					lObjTrnPensionTransferDtlsVO.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjTrnPensionTransferDtlsVO.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionTransferDtlsVO.setCreatedDate(gDateDBDate);
					lObjTrnPensionTransferDtlsVO.setUpdatedDate(null);
					lObjTrnPensionTransferDtlsVO.setUpdatedPostId(null);
					lObjTrnPensionTransferDtlsVO.setUpdatedUserId(null);
					if (lStrPensionerCode.length() > 0) {
						lObjTrnPensionTransferDtlsVO.setPensionerCode(lStrPensionerCode);
					}
					lObjTrnPensionTransferDtlsVO.setRegistrationNo(null);
					inputMap.put("Mode", "Add");

				} else // for ATO means update transfer case
				{
					String lStrTransferDtlsId = StringUtility.getParameter("hdnTransferDtlsId", request);
					if (!"".equals(lStrTransferDtlsId) && lStrTransferDtlsId.length() > 0) {
						lObjTrnPensionTransferDtlsVO = (TrnPensionTransferDtls) lObjTransferPPODAO.read(Long.valueOf(lStrTransferDtlsId.trim()));
					}
					if ("4".equals(lStrFlag)) {
						inputMap.put("Mode", "Reject");
					} else {
						inputMap.put("Mode", "Update");
					}
				}
				// --On reject by ato only status of trn_pension_transfer_dtls
				// to be modiefied.
				if (!"4".equals(lStrFlag)) {
					lObjTrnPensionTransferDtlsVO.setTransferDate(gDateDBDate);
					if (lStrToLocation.length() > 2 && !lStrToLocation.trim().equals("-1")) {
						lObjTrnPensionTransferDtlsVO.setToLocation(lStrToLocation);
					}
					lObjTrnPensionTransferDtlsVO.setTransferPostId(BigDecimal.valueOf(gLngPostId));
					if (lStrAgFlag == null) {
						lObjTrnPensionTransferDtlsVO.setAgFlag('N');
					}
					if (lStrAgFlag.length() > 0) {
						if (lStrAgFlag.equalsIgnoreCase("WithinAg")) {
							lObjTrnPensionTransferDtlsVO.setAgFlag('Y');
						}
						if (lStrAgFlag.equalsIgnoreCase("OutsideAg")) {
							lObjTrnPensionTransferDtlsVO.setAgFlag('N');
						}
						if (lStrAgFlag.equalsIgnoreCase("OtherState")) {
							lObjTrnPensionTransferDtlsVO.setRemarks("TRANSFER TO OTHER STATE");
							lObjTrnPensionTransferDtlsVO.setAgFlag('O');
						}
					}
					if (lStrOtherStateName.length() > 0) {
						lObjTrnPensionTransferDtlsVO.setOtherStateName(lStrOtherStateName);
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error in generateTrnPensionTransferDtlsVO is :" + e, e);
		}
		return lObjTrnPensionTransferDtlsVO;

	}

}
