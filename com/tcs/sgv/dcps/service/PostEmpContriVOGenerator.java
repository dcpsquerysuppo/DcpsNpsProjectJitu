package com.tcs.sgv.dcps.service;

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
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;

public class PostEmpContriVOGenerator extends ServiceImpl implements
		VOGeneratorService {
	
	Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject generateMap(Map inputMap) {
		// TODO Auto-generated method stub

		inputMap.get("requestObj");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		inputMap.get("requestObj");
		PostEmpContri objPostEmpContri = null;

		try {
			objPostEmpContri = generateVOMap(inputMap);

			inputMap.put("PostEmpContri", objPostEmpContri);
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			e.printStackTrace();
		}
		return objRes;
	}

	public PostEmpContri generateVOMap(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		inputMap.get("serviceLocator");

		PostEmpContri objPostEmpContri = new PostEmpContri();

		try {
			SessionHelper.getLangId(inputMap);
			SessionHelper.getLocationId(inputMap);
			SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();
			if (StringUtility.getParameter("saveOrUpdateFlag", request) == "2") {
			} else {
				Long lLngPostEmpContriId = IFMSCommonServiceImpl.getNextSeqNum(
						"MST_DCPS_POST_EMPLOYER_CONTRI", inputMap);
				objPostEmpContri.setDcpsPostEmpContriIdPk(lLngPostEmpContriId);
			}
			objPostEmpContri.setFinYear(Long.parseLong(StringUtility
					.getParameter("finYear", request).trim()));
			objPostEmpContri.setContriMonth(StringUtility.getParameter(
					"contriMonth", request).trim());
			objPostEmpContri.setBillNo(StringUtility.getParameter("billNo",
					request).trim());
			objPostEmpContri.setExcessExpenditure(Long.parseLong(StringUtility
					.getParameter("txtExcessAmount", request).trim()));
			objPostEmpContri.setStatusFlag('D');
			objPostEmpContri.setBillAmount(Long.parseLong(StringUtility
					.getParameter("billAmount", request).trim()));
			objPostEmpContri.setPostId(CreatedPostId);
			objPostEmpContri.setUserId(CreatedUserId);
			objPostEmpContri.setCreatedDate(CreatedDate);

		} catch (Exception e) {
			
			gLogger.error("Error in PostEmpContriVoGen method is :" + e, e);
			e.printStackTrace();
		}
		return objPostEmpContri;
	}
}
