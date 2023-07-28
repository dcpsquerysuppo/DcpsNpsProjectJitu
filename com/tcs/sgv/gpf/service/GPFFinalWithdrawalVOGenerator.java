package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFFinalWithdrawalDAO;
import com.tcs.sgv.gpf.dao.GPFFinalWithdrawalDAOImpl;
import com.tcs.sgv.gpf.valueobject.TrnGpfFinalWithdrawal;

public class GPFFinalWithdrawalVOGenerator extends ServiceImpl implements VOGeneratorService {

	private final Log gLogger = LogFactory.getLog(getClass());
	public ResultObject generateMap(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			TrnGpfFinalWithdrawal trnGpfFinalWithDrawl = generateFinalWithdrawalData(inputMap);
			inputMap.put("trnGpfFinalWithDrawal", trnGpfFinalWithDrawl);
			objRes.setResultValue(inputMap);
		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, ex, "Error is: ");
		}
		return objRes;
	}

	private TrnGpfFinalWithdrawal generateFinalWithdrawalData(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Integer iSaveOrUpdateFlag = 0;
		GPFFinalWithdrawalDAO gpfFinalWithdrawalDAO = new GPFFinalWithdrawalDAOImpl(TrnGpfFinalWithdrawal.class,
				servLoc.getSessionFactory());
		TrnGpfFinalWithdrawal lObjTrnGpfFinaWithdrawal = null;
		String lStrFinalWithId = StringUtility.getParameter("hidFinalWithdrawalID", request);

		if (!lStrFinalWithId.equals("")) {
			Long lLngFinalWithId = Long.parseLong(lStrFinalWithId);
			if (gpfFinalWithdrawalDAO.read(lLngFinalWithId) != null) {
				lObjTrnGpfFinaWithdrawal = (TrnGpfFinalWithdrawal) gpfFinalWithdrawalDAO.read(lLngFinalWithId);
			}
			iSaveOrUpdateFlag = 2;
		} else {
			lObjTrnGpfFinaWithdrawal = new TrnGpfFinalWithdrawal();
			iSaveOrUpdateFlag = 1;
		}
		String lStrGpfAccNo = StringUtility.getParameter("txtGpfAccNo", request);
		// String lStrTransId = StringUtility.getParameter("txtTIDFW", request);
		String lStrAppDate = StringUtility.getParameter("txtAppDateFW", request);
		String lStrFinalAmount = StringUtility.getParameter("txtFinalAmount", request);
		String lStrUserRemarks = StringUtility.getParameter("textareaUserRemarks", request);
		String lStrSancAmount = StringUtility.getParameter("txtSancAmountFW", request);
		String lStrOutstandingBalance = StringUtility.getParameter("txtOutstandingBalanceFW", request);
		String lStrReason = StringUtility.getParameter("cmblstReason", request);

		// lObjTrnGpfFinaWithdrawal.setTransactionId(lStrTransId);
		if (lStrGpfAccNo != null && !"".equals(lStrGpfAccNo.trim())) {
			lObjTrnGpfFinaWithdrawal.setGpfAccNo(lStrGpfAccNo);
		}
		if (lStrAppDate != null && !"".equals(lStrAppDate.trim())) {
			lObjTrnGpfFinaWithdrawal.setApplicationDate(lObjSimpleDateFormat.parse(lStrAppDate));
		}
		if (lStrFinalAmount != null && !"".equals(lStrFinalAmount.trim())) {
			lObjTrnGpfFinaWithdrawal.setFinalAmount(Math.round(Double.parseDouble(lStrFinalAmount)));
		}
		if (lStrSancAmount != null && !"".equals(lStrSancAmount.trim())) {
			lObjTrnGpfFinaWithdrawal.setAmountSanctioned(Double.parseDouble(lStrSancAmount));
		}
		if (lStrOutstandingBalance != null && !"".equals(lStrOutstandingBalance.trim())) {
			lObjTrnGpfFinaWithdrawal.setBalanceOutstanding(Double.parseDouble(lStrOutstandingBalance));
		}
		if(lStrReason != null && !lStrReason.equals("")){
			lObjTrnGpfFinaWithdrawal.setReason(Long.parseLong(lStrReason));
		}
		lObjTrnGpfFinaWithdrawal.setReqStatus("D");
		if (lStrUserRemarks != null && !"".equals(lStrUserRemarks.trim())) {
			lObjTrnGpfFinaWithdrawal.setUserRemarks(lStrUserRemarks);
		}
		if (iSaveOrUpdateFlag == 1) {
			lObjTrnGpfFinaWithdrawal.setCreatedPostId(gLngPostId);
			lObjTrnGpfFinaWithdrawal.setCreatedUserId(gLngUserId);
			lObjTrnGpfFinaWithdrawal.setCreatedDate(gDtCurrDt);
		}
		if (iSaveOrUpdateFlag == 2) {

			lObjTrnGpfFinaWithdrawal.setUpdatedPostId(gLngPostId);
			lObjTrnGpfFinaWithdrawal.setUpdatedUserId(gLngUserId);
			lObjTrnGpfFinaWithdrawal.setUpdatedDate(gDtCurrDt);
		}

		inputMap.put("iSaveOrUpdateFlag", iSaveOrUpdateFlag);
		return lObjTrnGpfFinaWithdrawal;

	}
}
