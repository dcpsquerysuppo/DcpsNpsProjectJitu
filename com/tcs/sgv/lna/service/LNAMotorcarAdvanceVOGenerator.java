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
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaMotorAdvance;

public class LNAMotorcarAdvanceVOGenerator extends ServiceImpl implements VOGeneratorService {
	Log glogger = LogFactory.getLog(getClass());

	public ResultObject generateMap(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstLnaMotorAdvance lObjMotorAdvance = null;
		try {
			lObjMotorAdvance = generateMotorAdvance(inputMap);
			inputMap.put("MotorAdvance", lObjMotorAdvance);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			objRes.setResultValue(null);
			glogger.error(" Error is : " + e, e);
		}
		return objRes;
	}

	private MstLnaMotorAdvance generateMotorAdvance(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		MstLnaMotorAdvance lObjMotorAdvance = null;

		LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Integer iSaveOrUpdateFlag = 0;

		String lStrMotorAdvanceId = StringUtility.getParameter("hidMotorAdvanceId", request);
		String lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
		String lStrRequestType = StringUtility.getParameter("hidRequestType", request);
		String lStrVehicleSubType = StringUtility.getParameter("cmbVehicleSubType", request);
		String lStrSubCategory = StringUtility.getParameter("rdoVehicleType", request);
		String lStrPayCommissionGR = StringUtility.getParameter("cmbPayCommissionMCA", request);
		String lStrAppDate = StringUtility.getParameter("txtAppDateMCA", request);
		String lStrRequestedAmount = StringUtility.getParameter("txtReqAmountMCA", request);
		String lStrExShowPrice = StringUtility.getParameter("txtExShowPriceMCA", request);
		String lStrReqDate = StringUtility.getParameter("txtReqDateMCA", request);
		String lStrInterestRate = StringUtility.getParameter("txtInterestRateMCA", request);
		String lStrUserRemarks = StringUtility.getParameter("txtUserRemarksMCA", request);
		String lStrSancAmount = StringUtility.getParameter("txtSancAmountMCA", request);
		String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountMCA", request);
		String lStrSancCapInstall = StringUtility.getParameter("txtSancPrincipalInstallMCA", request);
		String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallMCA", request);
		String lStrCapInstallmentAmount = StringUtility.getParameter("txtPrinInstallmentAmountMCA", request);
		String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountMCA", request);
		String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtMCA", request);
		String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtMCA", request);
		String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoMCA", request);
		String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoMCA", request);

		Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearId(lStrAppDate);

		if (!lStrMotorAdvanceId.equals("")) {
			lObjMotorAdvance = (MstLnaMotorAdvance) lObjMotorAdvanceDAO.read(Long.parseLong(lStrMotorAdvanceId));
			iSaveOrUpdateFlag = 2;

		} else {
			lObjMotorAdvance = new MstLnaMotorAdvance();
			iSaveOrUpdateFlag = 1;
		}

		inputMap.put("iSaveOrUpdateFlag", iSaveOrUpdateFlag);

		if (!"".equals(lStrSevaarthId.trim())) {
			lObjMotorAdvance.setSevaarthId(lStrSevaarthId);
		}
		if (!"".equals(lStrRequestType.trim())) {
			lObjMotorAdvance.setAdvanceType(Long.parseLong(lStrRequestType));
		}
		if (!"".equals(lStrVehicleSubType.trim())) {
			lObjMotorAdvance.setAdvanceSubType(Long.parseLong(lStrVehicleSubType));
		}
		if (!(lStrSubCategory.equals("-1"))) {
			lObjMotorAdvance.setSubCategory(lStrSubCategory);
		}
		if (!(lStrPayCommissionGR.equals("-1"))) {
			lObjMotorAdvance.setPayCommissionGR(Long.parseLong(lStrPayCommissionGR));
		}
		if (!"".equals(lStrAppDate.trim())) {
			lObjMotorAdvance.setApplicationDate(lObjSimpleDateFormat.parse(lStrAppDate));
		}
		if (!"".equals(lStrRequestedAmount.trim())) {
			lObjMotorAdvance.setAmountRequested(Long.parseLong(lStrRequestedAmount));
		}
		if (!"".equals(lStrExShowPrice.trim())) {
			lObjMotorAdvance.setExshowroomPrice(Long.parseLong(lStrExShowPrice));
		}
		if (!"".equals(lStrReqDate.trim())) {
			lObjMotorAdvance.setRequestDate(lObjSimpleDateFormat.parse(lStrReqDate));
		}
		if (!"".equals(lStrInterestRate.trim())) {
			lObjMotorAdvance.setInterestRate(Float.parseFloat(lStrInterestRate));
		}
		if (!"".equals(lStrSancAmount.trim())) {
			lObjMotorAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
		}
		if (!"".equals(lStrInterestAmount.trim())) {
			lObjMotorAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
		}
		if (!"".equals(lStrSancCapInstall.trim())) {
			lObjMotorAdvance.setSancCapitalInst(Integer.parseInt(lStrSancCapInstall));
		}

		if (!"".equals(lStrSancInterInstall.trim())) {
			lObjMotorAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
		}
		if (!"".equals(lStrCapInstallmentAmount.trim())) {
			lObjMotorAdvance.setCappitalInstAmtMonth(Long.parseLong(lStrCapInstallmentAmount));
		}
		if (!"".equals(lStrInterInstallmentAmount.trim())) {
			lObjMotorAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
		}
		if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
			lObjMotorAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
		} else {
			lObjMotorAdvance.setOddInstallment(null);
		}
		if (!"".equals(lStrUserRemarks.trim())) {
			lObjMotorAdvance.setUserRemarks(lStrUserRemarks);
		}
		if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
			lObjMotorAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
		}
		if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
			lObjMotorAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
		} else {
			lObjMotorAdvance.setOddInterestInstallment(null);
		}
		if (!(lStrOddInterestInstallNo.equals("-1"))) {
			lObjMotorAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
		}

		lObjMotorAdvance.setFinYearId(Long.parseLong(lIntFinYearId.toString()));
		if (iSaveOrUpdateFlag == 1) {
			lObjMotorAdvance.setCreatedPostId(gLngPostId);
			lObjMotorAdvance.setCreatedUserId(gLngUserId);
			lObjMotorAdvance.setCreatedDate(gDtCurrDt);
		}
		if (iSaveOrUpdateFlag == 2) {
			lObjMotorAdvance.setUpdatedUserId(gLngUserId);
			lObjMotorAdvance.setUpdatedPostId(gLngPostId);
			lObjMotorAdvance.setUpdatedDate(gDtCurrDt);
		}
		return lObjMotorAdvance;
	}
}
