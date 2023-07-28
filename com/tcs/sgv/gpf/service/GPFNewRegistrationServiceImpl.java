package com.tcs.sgv.gpf.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstEmpGpfAcc;

public class GPFNewRegistrationServiceImpl extends ServiceImpl implements GPFNewRegistrationService {
	Log gLogger = LogFactory.getLog(getClass());

	@Override
	public ResultObject saveGPFEmpData(Map inputMap) {
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		// MstGpfEmp lObjEmpData = (MstGpfEmp) inputMap.get("GPFEmpData");
		MstEmpGpfAcc lObjEmpGpfAcc = (MstEmpGpfAcc) inputMap.get("EmpGpfAccData");
		// MstGpfYearly lObjGpfYearly = (MstGpfYearly)
		// inputMap.get("GpfYearlyData");
		// MstGpfMonthly lObjGpfMonthly = (MstGpfMonthly)
		// inputMap.get("GpfMonthlyData");
		// MstGpfAdvance lObjGpfAdvance = (MstGpfAdvance)
		// inputMap.get("GpfAdvanceData");

		GPFRequestProcessDAO gpfRequestProcessDAO = new GPFRequestProcessDAOImpl(null, servLoc.getSessionFactory());

		try {
			// Long lLngEmpId =
			// IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_EMP", inputMap);
			// lObjEmpData.setGpfEmpId(lLngEmpId);
			// gpfRequestProcessDAO.create(lObjEmpData);

			// String lStrTrnsId =
			// gpfRequestProcessDAO.getNewTransactionId(lLngEmpId.toString());

			lObjEmpGpfAcc.setMstEmpGpfAccId(lObjEmpGpfAcc.getMstGpfEmpId());
			gpfRequestProcessDAO.create(lObjEmpGpfAcc);

			// Long lLngGpfYearyId =
			// IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_YEARLY", inputMap);
			// lObjGpfYearly.setMstGpfYearlyId(lLngGpfYearyId);
			// gpfRequestProcessDAO.create(lObjGpfYearly);
			//
			// Long lLngGpfMonthlyId =
			// IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_MONTHLY", inputMap);
			// lObjGpfMonthly.setAdvanceTrnId(lStrTrnsId);
			// lObjGpfMonthly.setMstGpfMonthlyId(lLngGpfMonthlyId);
			// gpfRequestProcessDAO.create(lObjGpfMonthly);
			//
			// Long lLngGpfAdvanceId =
			// IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ADVANCE", inputMap);
			// lObjGpfAdvance.setTransactionId(lStrTrnsId);
			// lObjGpfAdvance.setMstGpfAdvanceId(lLngGpfAdvanceId);
			// gpfRequestProcessDAO.create(lObjGpfAdvance);

		} catch (Exception ex) {
			resObj.setResultValue(null);
			gLogger.error(" Error is : " + ex, ex);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		return resObj;

	}
}
