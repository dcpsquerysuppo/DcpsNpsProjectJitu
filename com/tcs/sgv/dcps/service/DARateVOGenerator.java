package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.DARate;

public class DARateVOGenerator extends ServiceImpl implements
		VOGeneratorService {

	public ResultObject generateMap(Map inputMap) {

		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		inputMap.get("requestObj");
		DARate lObjDARate = null;

		try {
			inputMap.put("DCPSDARate", lObjDARate);
			lObjDARate = generateVOMap(inputMap);
			inputMap.put("DCPSDARate", lObjDARate);
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

	public DARate generateVOMap(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		DARate daratevo = null;
		try {

			String strDARate = StringUtility.getParameter("txtDARate", request);
			String strFromDate = StringUtility.getParameter("txtFromDate",
					request);
			String strPayComm = StringUtility.getParameter("cmbPayComm",
					request);
			Float lDaRate = null;
			Date FromDate = null;

			if (!(strDARate.equals(""))) {
				lDaRate = Float.parseFloat(strDARate);
			}

			if (!(strFromDate.equals(""))) {
				FromDate = simpleDateFormat.parse(strFromDate);
			}

			daratevo = new DARate();
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			daratevo.setPayCommission(strPayComm);
			daratevo.setDaRate(lDaRate);
			daratevo.setEffectiveFromDate(FromDate);
			daratevo.setStatus(1);
			daratevo.setPostId(CreatedPostId);
			daratevo.setUserId(CreatedUserId);
			daratevo.setCreatedDate(CreatedDate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return daratevo;
	}
}
