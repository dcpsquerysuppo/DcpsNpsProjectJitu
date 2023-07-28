package com.tcs.sgv.gpf.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstEmpGpfAcc;

public class GPFNewRegistrationVOGenerator extends ServiceImpl implements VOGeneratorService {

	Log gLogger = LogFactory.getLog(getClass());

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private String gStrLocId = null; /* Location Code */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	private void setSessionInfo(Map inputMap) {

		try {
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurrDt = SessionHelper.getCurDate();
			gStrLocId = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {

		}

	}

	@Override
	public ResultObject generateMap(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		MstEmpGpfAcc lObjEmpGpfAcc = null;
		try {
			// lObjGpfEmpData = generateGPFEmpData(inputMap);
			lObjEmpGpfAcc = generateEmpGpfAcc(inputMap);
			// lObjGpfYearly = generateGpfYearly(inputMap);
			// lObjGpfMonthly = generateGpfMonthly(inputMap);
			// lObjGpfAdvance = generateGpfAdvance(inputMap);
			// inputMap.put("GPFEmpData", lObjGpfEmpData);
			inputMap.put("EmpGpfAccData", lObjEmpGpfAcc);
			// inputMap.put("GpfYearlyData", lObjGpfYearly);
			// inputMap.put("GpfMonthlyData", lObjGpfMonthly);
			// inputMap.put("GpfAdvanceData", lObjGpfAdvance);
			objRes.setResultValue(inputMap);
		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			ex.printStackTrace();
		}

		return objRes;
	}

	private MstEmpGpfAcc generateEmpGpfAcc(Map inputMap) {
		setSessionInfo(inputMap);
		String lStrEmpId = null;
		Long lLngDcpsEmpId = null;

		GPFRequestProcessDAO lObjRequestProcessDAO = null;
		GPFRequestProcessDAO lObjRequestProcessDAOForMstEmp = null;
		MstEmpGpfAcc lObjEmpGpfAcc = new MstEmpGpfAcc();
		try {

			lStrEmpId = inputMap.get("dcpsEmpId").toString();
			Double lDblRegSubs = 0d;
			String lStrPfAccNo = "";
			Double lDblOpenBal = 0d;
			String lStrDdoCode = null;
			String lStrSevaarthId = "";
			lObjRequestProcessDAO = new GPFRequestProcessDAOImpl(RltDcpsPayrollEmp.class, serv.getSessionFactory());
			lObjRequestProcessDAOForMstEmp = new GPFRequestProcessDAOImpl(MstEmp.class, serv.getSessionFactory());
			List<RltDcpsPayrollEmp> lLsRltDcpsPayrollEmpDtls = lObjRequestProcessDAO.getListByColumnAndValue(
					"dcpsEmpId", Long.valueOf(lStrEmpId));

			lLngDcpsEmpId = Long.valueOf(lStrEmpId);
			MstEmp lObjMstEmpVO = (MstEmp) lObjRequestProcessDAOForMstEmp.read(lLngDcpsEmpId);
			lStrDdoCode = lObjMstEmpVO.getDdoCode();
			lStrSevaarthId = lObjMstEmpVO.getSevarthId();

			if (!lLsRltDcpsPayrollEmpDtls.isEmpty()) {
				RltDcpsPayrollEmp lObjRltDcpsPayrollEmp = lLsRltDcpsPayrollEmpDtls.get(0);
				lStrPfAccNo = lObjRltDcpsPayrollEmp.getPfSeriesDesc() + "/" + lObjRltDcpsPayrollEmp.getPfAcNo();
			}
			lObjEmpGpfAcc.setMstGpfEmpId(lLngDcpsEmpId);
			if (lStrSevaarthId != null && !lStrSevaarthId.equals("")) {
				lObjEmpGpfAcc.setSevaarthId(lStrSevaarthId);
			}
			if (lStrPfAccNo != null && !lStrPfAccNo.equals("")) {
				lObjEmpGpfAcc.setGpfAccNo(lStrPfAccNo);
			}
			if (lDblRegSubs != null && !lDblRegSubs.equals("")) {
				lObjEmpGpfAcc.setMonthlySubscription(lDblRegSubs);
			}
			if (lDblOpenBal != null && !lDblOpenBal.equals("")) {
				lObjEmpGpfAcc.setCurrentBalance(lDblOpenBal);
			}
			if (lStrDdoCode != null && !lStrDdoCode.equals("")) {
				lObjEmpGpfAcc.setDdoCode(lStrDdoCode);
			}
			lObjEmpGpfAcc.setCreatedPostId(gLngPostId);
			lObjEmpGpfAcc.setCreatedUserId(gLngUserId);
			lObjEmpGpfAcc.setCreatedDate(gDtCurrDt);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lObjEmpGpfAcc;

	}

}
