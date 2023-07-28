package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO;
import com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfEmpSubscription;

/**
 * @author 397138
 * 
 */
public class ChangeSubscriptionVOGenerator extends ServiceImpl implements VOGeneratorService {

	private final Log gLogger = LogFactory.getLog(getClass());
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.core.service.VOGeneratorService#generateMap(java.util.Map)
	 */
	public ResultObject generateMap(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstGpfEmpSubscription mstGpfEmpSubscription = null;
		try {
			// get the vo from private method and put in Map for service access
			mstGpfEmpSubscription = generateChangeSubData(inputMap);
			inputMap.put("mstGpfEmpSubscription", mstGpfEmpSubscription);
			objRes.setResultValue(inputMap);
		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, ex, "Error Is: ");
		}
		return objRes;
	}

	/**
	 * method to generate the VO Map from jsp values
	 * 
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	private MstGpfEmpSubscription generateChangeSubData(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		GPFChangeSubscriptionDAO lObjChangeSubscriptionDAO = new GPFChangeSubscriptionDAOImpl(
				MstGpfEmpSubscription.class, serv.getSessionFactory());
		FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());

		Long gLngPostId = SessionHelper.getPostId(inputMap);
		Long gLngUserId = SessionHelper.getUserId(inputMap);
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		Integer iSaveOrUpdateFlag = 0;

		String lStrTransId = StringUtility.getParameter("txtTID", request);
		String lStrEffectFromMonth = StringUtility.getParameter("cmblstEffectFromMonth", request);
		String lStrNewSubAmount = StringUtility.getParameter("txtNewSubscription", request);
		String lStrCurrSubAmount = StringUtility.getParameter("txtCurrSubscription", request);
		String lStrGpfAccNo = StringUtility.getParameter("txtGpfAccNo", request);
		String lStrChangeSubId = StringUtility.getParameter("hidChangeSubID", request);
		String lStrPhyAppReceivedDate = StringUtility.getParameter("txtPhyAppReceivedDate", request);

		Boolean flagChangeSubsType = false;
		Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();

		MstGpfEmpSubscription lObjMstGpfEmpSubs = null;
		if (!lStrChangeSubId.equals("")) {
			iSaveOrUpdateFlag = 2;
			lObjMstGpfEmpSubs = (MstGpfEmpSubscription) lObjChangeSubscriptionDAO.read(Long.parseLong(lStrChangeSubId));
		} else {
			lObjMstGpfEmpSubs = new MstGpfEmpSubscription();
			iSaveOrUpdateFlag = 1;
		}
		if (lStrTransId != null && !"".equals(lStrTransId.trim())) {
			lObjMstGpfEmpSubs.setTransactionId(lStrTransId);
		}
		if (lStrGpfAccNo != null && !"".equals(lStrGpfAccNo.trim())) {
			lObjMstGpfEmpSubs.setGpfAccNo(lStrGpfAccNo);
		}
		if (lStrNewSubAmount != null && !"".equals(lStrNewSubAmount.trim())) {
			lObjMstGpfEmpSubs.setMonthlySubscription(Double.parseDouble(lStrNewSubAmount));
			if (Double.parseDouble(lStrNewSubAmount) > Double.parseDouble(lStrCurrSubAmount)) {
				flagChangeSubsType = true;
				lObjMstGpfEmpSubs.setChangeType("I");
			} else {
				lObjMstGpfEmpSubs.setChangeType("D");
			}
		}
		lObjMstGpfEmpSubs.setStatusFlag("D");

		if (lStrPhyAppReceivedDate != null && !"".equals(lStrPhyAppReceivedDate.trim())) {
			lObjMstGpfEmpSubs.setApplicationDate(lObjSimpleDateFormat.parse(lStrPhyAppReceivedDate));
		}
		if (lStrEffectFromMonth != null && !"".equals(lStrEffectFromMonth.trim())) {
			lObjMstGpfEmpSubs.setEffectFromMonth(Integer.parseInt(lStrEffectFromMonth));
			if (Long.parseLong(lStrEffectFromMonth) <= 3) {
				lIntFinYearId = lIntFinYearId + 1;
			}
		}

		lObjMstGpfEmpSubs.setFinYearId(Long.parseLong(lIntFinYearId.toString()));

		if (iSaveOrUpdateFlag == 1) {
			lObjMstGpfEmpSubs.setCreatedPostId(gLngPostId);
			lObjMstGpfEmpSubs.setCreatedUserId(gLngUserId);
			lObjMstGpfEmpSubs.setCreatedDate(gDtCurrDt);
		}
		if (iSaveOrUpdateFlag == 2) {
			lObjMstGpfEmpSubs.setUpdatedUserId(gLngUserId);
			lObjMstGpfEmpSubs.setUpdatedPostId(gLngPostId);
			lObjMstGpfEmpSubs.setUpdatedDate(gDtCurrDt);
		}
		inputMap.put("changeSubsType", flagChangeSubsType);
		inputMap.put("finYearId", lIntFinYearId);
		inputMap.put("iSaveOrUpdateFlag", iSaveOrUpdateFlag);
		return lObjMstGpfEmpSubs;
	}
}
