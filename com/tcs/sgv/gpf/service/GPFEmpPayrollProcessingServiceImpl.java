package com.tcs.sgv.gpf.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAOImpl;
import com.tcs.sgv.gpf.dao.GPFEmpPayrollProcessingDAO;
import com.tcs.sgv.gpf.dao.GPFEmpPayrollProcessingDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstEmpGpfAcc;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;
import com.tcs.sgv.gpf.valueobject.MstGpfMonthly;

public class GPFEmpPayrollProcessingServiceImpl extends ServiceImpl implements GPFEmpPayrollProcessingService {

	Log gLogger = LogFactory.getLog(getClass());	

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	private void setSessionInfo(Map inputMap) {

		try {			
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}

	}

	public ResultObject getEmpSubscriptionForBillgroup(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long lLngBillgroupId = null;
		Long lLngYearId = null;
		Long lLngMonthId = null;
		try {
			setSessionInfo(inputMap);
			String lStrBillGroup = (String) inputMap.get("billgroupId");
			String lStrYearId = (String) inputMap.get("yearId");
			String lStrMonthId = (String) inputMap.get("monthId");
			if (lStrBillGroup != null && !lStrBillGroup.equals("")) {
				lLngBillgroupId = Long.parseLong(lStrBillGroup);
			}
			if (lStrYearId != null && !lStrYearId.equals("")) {
				lLngYearId = Long.parseLong(lStrYearId);
			}
			if (lStrMonthId != null && !lStrMonthId.equals("")) {
				lLngMonthId = Long.parseLong(lStrMonthId);
			}
			GPFEmpPayrollProcessingDAO lObjGPFEmpPayrollProcessingDAO = new GPFEmpPayrollProcessingDAOImpl(null, serv
					.getSessionFactory());
			List lLstEmpMonthlyGPF = lObjGPFEmpPayrollProcessingDAO.getMonthlySubscriptionForBillgroup(lLngBillgroupId,
					lLngYearId, lLngMonthId);
			inputMap.put("MonthlySubsList", lLstEmpMonthlyGPF);
			// inputMap.put("AdvanceRecovery", lLstAdvRecovery);

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, ex, "Error is: ");
			return resObj;
		}
		resObj.setResultValue(inputMap);
		return resObj;
	}

	public ResultObject updateGPFDataOnPaybillGeneration(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long lLngBillgroupId = null;
		Long lLngYearId = null;
		Long lLngMonthId = null;
		Double lDblMonthlySubs = null;
		Double lDblAdvanceRecovery = null;
		String lStrGPFAccNo = null;
		Double lDblTotalDeduction = null;
		Double lDblOldBalance = null;
		MstGpfMonthly lObjMstGpfMonthly = null;
		try {
			setSessionInfo(inputMap);
			String lStrBillGroup = (String) inputMap.get("billgroupId");
			String lStrYearId = (String) inputMap.get("yearId");
			String lStrMonthId = (String) inputMap.get("monthId");
			if (lStrBillGroup != null && !lStrBillGroup.equals("")) {
				lLngBillgroupId = Long.parseLong(lStrBillGroup);
			}
			if (lStrYearId != null && !lStrYearId.equals("")) {
				lLngYearId = Long.parseLong(lStrYearId);
			}
			if (lStrMonthId != null && !lStrMonthId.equals("")) {
				lLngMonthId = Long.parseLong(lStrMonthId);
			}
			GPFEmpPayrollProcessingDAO lObjGPFEmpPayrollProcessingDAO = new GPFEmpPayrollProcessingDAOImpl(null, serv
					.getSessionFactory());
			String[] lArrColumnName = new String[] { "billgroupId", "finYearId", "monthId" };
			Object[] lArrValues = new Object[] { lLngBillgroupId, lLngYearId, lLngMonthId };
			List lLstEmpMonthlyData = lObjGPFEmpPayrollProcessingDAO
					.getListByColumnAndValue(lArrColumnName, lArrValues);

			if (lLstEmpMonthlyData != null && lLstEmpMonthlyData.size() > 0) {
				for (int index = 0; index < lLstEmpMonthlyData.size(); index++) {

					lObjMstGpfMonthly = (MstGpfMonthly) lLstEmpMonthlyData.get(index);
					lStrGPFAccNo = lObjMstGpfMonthly.getGpfAccNo();
					lDblAdvanceRecovery = lObjMstGpfMonthly.getAdvanceRecovery();

					// update monthly subscription and account balance in
					// MstEmpGpfAcc

					lDblMonthlySubs = lObjMstGpfMonthly.getRegularSubscription();
					lDblTotalDeduction = lDblMonthlySubs + lDblAdvanceRecovery;
					GPFAdvanceProcessDAO gpfAdvanceProcessDAOForGpfBal = new GPFAdvanceProcessDAOImpl(
							MstEmpGpfAcc.class, serv.getSessionFactory());
					MstEmpGpfAcc lObjMstEmpGpfAcc = (MstEmpGpfAcc) gpfAdvanceProcessDAOForGpfBal
							.getGPFAccountObjectForAccountNo(lStrGPFAccNo);
					lDblOldBalance = lObjMstEmpGpfAcc.getCurrentBalance();
					lObjMstEmpGpfAcc.setMonthlySubscription(lDblMonthlySubs);
					lObjMstEmpGpfAcc.setCurrentBalance(lDblOldBalance + lDblTotalDeduction);
					gpfAdvanceProcessDAOForGpfBal.update(lObjMstEmpGpfAcc);

					// update mst_gpf_advance installment details
					String lStrAdvanceTrnId = lObjMstGpfMonthly.getAdvanceTrnId();

					if (lStrAdvanceTrnId != null) {
						GPFAdvanceProcessDAO lObjGPFAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(
								MstGpfAdvance.class, serv.getSessionFactory());
						Long lLngAdvanceId = lObjGPFAdvanceProcessDAO.getMstAdvanceIdForTransactionId(lStrAdvanceTrnId);
						MstGpfAdvance lObjMstGpfAdvance = (MstGpfAdvance) lObjGPFAdvanceProcessDAO.read(lLngAdvanceId);
						Integer lIntInstallmentsLeft = lObjMstGpfAdvance.getInstallmentsLeft() - 1;
						lObjMstGpfAdvance.setInstallmentsLeft(lIntInstallmentsLeft);
						if (lIntInstallmentsLeft == 0) {
							lObjMstGpfAdvance.setRecoveryStatus(1);
						}
						Double lDblRecoveredAmount = lObjMstGpfAdvance.getRecoveredAmount() + lDblAdvanceRecovery;
						Double lDblOutstandingAmount = lObjMstGpfAdvance.getOutstandingAmount() - lDblAdvanceRecovery;
						lObjMstGpfAdvance.setRecoveredAmount(lDblRecoveredAmount);
						lObjMstGpfAdvance.setOutstandingAmount(lDblOutstandingAmount);
						lObjGPFAdvanceProcessDAO.update(lObjMstGpfAdvance);
					}
				}
			}
		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, ex, "Error is: ");
			return resObj;
		}

		resObj.setResultValue(inputMap);
		return resObj;
	}

}
