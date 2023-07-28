package com.tcs.sgv.lna.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;

public class LNAComputerAdvanceVOGenerator extends ServiceImpl implements VOGeneratorService {

	Log glogger = LogFactory.getLog(getClass());

	public ResultObject generateMap(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstLnaCompAdvance lObjCompAdvance = null;
		try {
			lObjCompAdvance = generateCompAdvance(inputMap);
			inputMap.put("CompAdvance", lObjCompAdvance);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			objRes.setResultValue(null);
			glogger.error(" Error is : " + e, e);
		}
		return objRes;
	}

	private MstLnaCompAdvance generateCompAdvance(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstLnaCompAdvance lObjCompAdvance = null;

		LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Integer iSaveOrUpdateFlag = 0;

		String lStrComAdvanceId = StringUtility.getParameter("hidComAdvanceId", request);
		String lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
		String lStrRequestType = StringUtility.getParameter("hidRequestType", request);
		String lStrCompSubType = StringUtility.getParameter("cmbComputerSubType", request);
		String lStrAppDate = StringUtility.getParameter("txtAppDateCA", request);
		String lStrRequestedAmount = StringUtility.getParameter("txtReqAmountCA", request);
		String lStrActualCost = StringUtility.getParameter("txtActualCostCA", request);
		String lStrReqDate = StringUtility.getParameter("txtReqDateCA", request);
		String lStrUserRemarks = StringUtility.getParameter("txtUserRemarksCA", request);
		String lStrSancAmount = StringUtility.getParameter("txtSancAmountCA", request);
		String lStrSancInstallments = StringUtility.getParameter("txtSancInstallmentsCA", request);
		String lStrInstallmentAmount = StringUtility.getParameter("txtInstallmentAmountCA", request);
		String lStrOddInstallmentAmt = StringUtility.getParameter("txtOddInstallmentAmtCA", request);
		String lStrOddInstallNo = StringUtility.getParameter("cmbOddInstallNoCA", request);
		String lStrInterestRate = StringUtility.getParameter("txtInterestRateCA", request);

		Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearId(lStrAppDate);

		if (!lStrComAdvanceId.equals("")) {
			lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(Long.parseLong(lStrComAdvanceId));
			iSaveOrUpdateFlag = 2;

		} else {
			lObjCompAdvance = new MstLnaCompAdvance();
			iSaveOrUpdateFlag = 1;
		}

		inputMap.put("iSaveOrUpdateFlag", iSaveOrUpdateFlag);

		if (!"".equals(lStrSevaarthId.trim())) {
			lObjCompAdvance.setSevaarthId(lStrSevaarthId);
		}
		if (!"".equals(lStrRequestType.trim())) {
			lObjCompAdvance.setAdvanceType(Long.parseLong(lStrRequestType));
		}
		if (!(lStrCompSubType.equals("-1"))) {
			lObjCompAdvance.setAdvanceSubType(Long.parseLong(lStrCompSubType));
		}
		if (!"".equals(lStrAppDate.trim())) {
			lObjCompAdvance.setApplicationDate(lObjSimpleDateFormat.parse(lStrAppDate));
		}
		if (!"".equals(lStrRequestedAmount.trim())) {
			lObjCompAdvance.setAmountRequested(Long.parseLong(lStrRequestedAmount));
		}
		if (!"".equals(lStrActualCost.trim())) {
			lObjCompAdvance.setActualCost(Long.parseLong(lStrActualCost));
		}
		if (!"".equals(lStrReqDate.trim())) {
			lObjCompAdvance.setRequestDate(lObjSimpleDateFormat.parse(lStrReqDate));
		}
		if (!"".equals(lStrSancAmount.trim())) {
			lObjCompAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
		}
		if (!"".equals(lStrSancInstallments.trim())) {
			lObjCompAdvance.setSancInstallments(Integer.parseInt(lStrSancInstallments));
		}

		if (!"".equals(lStrInstallmentAmount.trim())) {
			lObjCompAdvance.setInstallmentAmount(Long.parseLong(lStrInstallmentAmount));
		}
		if (!"".equals(lStrOddInstallmentAmt.trim())) {
			lObjCompAdvance.setOddInstallment(Long.parseLong(lStrOddInstallmentAmt));
		} else {
			lObjCompAdvance.setOddInstallment(null);
		}
		if (!(lStrOddInstallNo.equals("-1"))) {
			lObjCompAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddInstallNo));
		}
		if (!"".equals(lStrUserRemarks.trim())) {
			lObjCompAdvance.setUserRemarks(lStrUserRemarks);
		}
		lObjCompAdvance.setInterestRate(Float.parseFloat(lStrInterestRate));
		lObjCompAdvance.setFinYearId(Long.parseLong(lIntFinYearId.toString()));
		if (iSaveOrUpdateFlag == 1) {
			lObjCompAdvance.setCreatedPostId(gLngPostId);
			lObjCompAdvance.setCreatedUserId(gLngUserId);
			lObjCompAdvance.setCreatedDate(gDtCurrDt);
		}
		if (iSaveOrUpdateFlag == 2) {
			lObjCompAdvance.setUpdatedUserId(gLngUserId);
			lObjCompAdvance.setUpdatedPostId(gLngPostId);
			lObjCompAdvance.setUpdatedDate(gDtCurrDt);
		}
		return lObjCompAdvance;
	}

}
