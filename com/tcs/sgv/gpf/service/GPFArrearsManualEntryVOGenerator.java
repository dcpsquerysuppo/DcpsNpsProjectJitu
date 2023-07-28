package com.tcs.sgv.gpf.service;

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
import com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfArrearsYearly;

public class GPFArrearsManualEntryVOGenerator extends ServiceImpl implements VOGeneratorService {

	private final Log gLogger = LogFactory.getLog(getClass());
	public ResultObject generateMap(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstGpfArrearsYearly lObjArrearsYearly = null;
		try {
			lObjArrearsYearly = generateArrearsYearlyData(inputMap);
			inputMap.put("ArrearsYearlyData", lObjArrearsYearly);
			objRes.setResultValue(inputMap);
		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, ex, "Error is: ");
		}
		return objRes;
	}

	private MstGpfArrearsYearly generateArrearsYearlyData(Map inputMap) throws Exception {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		new GPFArrearsManualEntryDAOImpl(null, serv.getSessionFactory());
		MstGpfArrearsYearly lObjArrearsYearly = null;

		SessionHelper.getPostId(inputMap);
		SessionHelper.getUserId(inputMap);
		DBUtility.getCurrentDateFromDB();

		StringUtility.getParameter("hidGpfAccNo", request);
		StringUtility.getParameter("txtInstallmentAmount", request);
		StringUtility.getParameter("txtCurrSubscription", request);
		StringUtility.getParameter("txtGpfAccNo", request);
		StringUtility.getParameter("hidChangeSubID", request);
		StringUtility.getParameter("txtPhyAppReceivedDate", request);
		StringUtility.getParameter("txtSysEntryDate", request);

		return lObjArrearsYearly;

	}
}
