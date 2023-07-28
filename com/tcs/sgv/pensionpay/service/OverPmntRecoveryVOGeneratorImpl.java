/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jul 1, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
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
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO;
import com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionChallanDtls;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Jul 1, 2011
 */
public class OverPmntRecoveryVOGeneratorImpl extends ServiceImpl implements OverPmntRecoveryVOGenerator {

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
	 * com.tcs.sgv.pensionpay.service.OverPmntRecoveryVOGenerator#generateMap
	 * (java.util.Map)
	 */

	public ResultObject generateMap(Map inputMap) {

		gLogger.info("In generateMap of OverPmntRecoveryVOGeneratorImpl........");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {

			TrnPensionChallanDtls lObjTrnPensionChallanDtlsVO = new TrnPensionChallanDtls();
			lObjTrnPensionChallanDtlsVO = generateTrnPensionChallanDtlsVO(inputMap);
			inputMap.put("Mode", "Add");
			inputMap.put("lObjTrnPensionChallanDtlsVO", lObjTrnPensionChallanDtlsVO);
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
	 * @see com.tcs.sgv.pensionpay.service.OverPmntRecoveryVOGenerator#
	 * generateTrnPensionChallanDtlsVO(java.util.Map)
	 */

	public TrnPensionChallanDtls generateTrnPensionChallanDtlsVO(Map inputMap) {

		gLogger.info("In generateTrnPensionChallanDtlsVO of OverPmntRecoveryVOGeneratorImpl........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		TrnPensionChallanDtls lObjTrnPensionChallanDtlsVO = new TrnPensionChallanDtls();
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		OverPmntRecoveryDAO lObjOverPmntRecoveryDAO = null;
		try {
			setSessionInfo(inputMap);

			String lStrPPONo = null;
			String lStrPnsnrname = null;
			String lStrPensionerCode = null;
			String lStrTotalAmountToRecover = null;
			String lStrDeathDate = null;
			String lStrSchemeCode;
			Long llngRequestId = null;

			lObjOverPmntRecoveryDAO = new OverPmntRecoveryDAOImpl(null, serv.getSessionFactory());
			lStrPPONo = StringUtility.getParameter("txtPpoNo", request).trim();

			lStrPnsnrname = StringUtility.getParameter("txtPensrName", request).trim();

			lStrTotalAmountToRecover = StringUtility.getParameter("txtTotalRecvAmnt", request).trim();

			lStrDeathDate = StringUtility.getParameter("txtDateOfExpry", request).trim();

			lStrPensionerCode = StringUtility.getParameter("hidPnsnrCode", request).trim();

			lStrSchemeCode = StringUtility.getParameter("txtSchemeCode", request).trim();

			llngRequestId = lObjOverPmntRecoveryDAO.getMaxRequestId(gStrLocationCode);
			if (llngRequestId == 0l) {
				Random random = new Random(100);
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

			lObjTrnPensionChallanDtlsVO.setChallanNo(null);
			lObjTrnPensionChallanDtlsVO.setChallanDate(null);
			lObjTrnPensionChallanDtlsVO.setPayOrderDate(null);
			lObjTrnPensionChallanDtlsVO.setPayOrderNo(null);
			lObjTrnPensionChallanDtlsVO.setInwardDate(null);
			lObjTrnPensionChallanDtlsVO.setTrnCounter(1);
			lObjTrnPensionChallanDtlsVO.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
			lObjTrnPensionChallanDtlsVO.setCreatedPostId(BigDecimal.valueOf(gLngPostId));
			lObjTrnPensionChallanDtlsVO.setCreatedDate(gDateDBDate);
			lObjTrnPensionChallanDtlsVO.setUpdatedDate(null);
			lObjTrnPensionChallanDtlsVO.setUpdatedPostId(null);
			lObjTrnPensionChallanDtlsVO.setUpdatedUserId(null);
			lObjTrnPensionChallanDtlsVO.setLocationCode(gStrLocationCode);
			lObjTrnPensionChallanDtlsVO.setDbId(gLngDBId);
			lObjTrnPensionChallanDtlsVO.setRequestId(String.valueOf(llngRequestId));
			lObjTrnPensionChallanDtlsVO.setStatus(gObjRsrcBndle.getString("OPR.PENDING"));
			if (lStrPPONo.length() > 0) {
				lObjTrnPensionChallanDtlsVO.setPpoNo(lStrPPONo);
			}
			if (lStrPnsnrname.length() > 0) {
				lObjTrnPensionChallanDtlsVO.setName(lStrPnsnrname);
			}
			lObjTrnPensionChallanDtlsVO.setBank(null);
			lObjTrnPensionChallanDtlsVO.setBranch(null);
			lObjTrnPensionChallanDtlsVO.setAccountNumber(null);
			if (lStrPensionerCode.length() > 0) {
				lObjTrnPensionChallanDtlsVO.setPensionerCode(lStrPensionerCode);
			}
			if (lStrTotalAmountToRecover.length() > 0) {
				lObjTrnPensionChallanDtlsVO.setTotalAmountTorecover(BigDecimal.valueOf(Long.valueOf(lStrTotalAmountToRecover)));
			}
			lObjTrnPensionChallanDtlsVO.setAmountRecovered(BigDecimal.ZERO);
			lObjTrnPensionChallanDtlsVO.setPayOrderAmnt(BigDecimal.ZERO);
			if (lStrDeathDate.length() > 0) {
				lObjTrnPensionChallanDtlsVO.setDeathDate(lObjSimpleDateFormat.parse(lStrDeathDate));
			}
			lObjTrnPensionChallanDtlsVO.setRecoveryFromDate(null);
			lObjTrnPensionChallanDtlsVO.setRecoveryToDate(null);
			if (lStrSchemeCode.length() > 0) {
				lObjTrnPensionChallanDtlsVO.setSchemeCode(lStrSchemeCode);
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error in generateTrnPensionChallanDtlsVO is :" + e, e);
		}
		return lObjTrnPensionChallanDtlsVO;

	}

}
