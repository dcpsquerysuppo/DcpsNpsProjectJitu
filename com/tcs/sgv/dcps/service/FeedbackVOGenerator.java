package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.Feedback;

public class FeedbackVOGenerator extends ServiceImpl implements
		VOGeneratorService {
	public ResultObject generateMap(Map inputMap) {
		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		inputMap.get("requestObj");
		Feedback objFeedback = null;

		try {
			objFeedback = generateVOMap(inputMap);
			inputMap.put("Feedback", objFeedback);
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

	public Feedback generateVOMap(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		inputMap.get("serviceLocator");
		LogFactory.getLog(getClass());

		Feedback objFeedback = new Feedback();

		try {
			Long LangId = SessionHelper.getLangId(inputMap);
			Long LocId = SessionHelper.getLocationId(inputMap);
			Long DbId = SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			Long lLngFeedbackId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_feedback", inputMap);
			objFeedback.setDcpsFeedbackIdPk(lLngFeedbackId);
			objFeedback.setName(StringUtility.getParameter("name", request).trim());
			objFeedback.setDdo(StringUtility.getParameter("ddoCode", request).trim());
			objFeedback.setTreasury(StringUtility.getParameter("treasuryCode",request).trim());
			objFeedback.setDeptName(StringUtility.getParameter("deptName",request).trim());
			objFeedback.setComments(StringUtility.getParameter("comments",request).trim());
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			if(!"".equals(StringUtility.getParameter("txtFeedBackDate",request).trim()))
			{
				objFeedback.setFeedBackDate(simpleDateFormat.parse(StringUtility.getParameter("txtFeedBackDate",request).trim()));
			}
			
			if(!"".equals(StringUtility.getParameter("txtOfficeEmailId",request).trim()))
			{
				objFeedback.setEmailAddress(StringUtility.getParameter("txtOfficeEmailId",request).trim());
			}
			
			if(!"".equals(StringUtility.getParameter("txtOfficeContactNo",request).trim()))
			{
				objFeedback.setContactNo(Long.valueOf(StringUtility.getParameter("txtOfficeContactNo",request).trim()));
			}
			
			if(!"".equals(StringUtility.getParameter("comments",request).trim()))
			{
				objFeedback.setComments(StringUtility.getParameter("comments",request).trim());
			}
			
			objFeedback.setLangId(LangId);
			objFeedback.setLocId(LocId);
			objFeedback.setDbId(DbId);
			objFeedback.setPostId(CreatedPostId);
			objFeedback.setUserId(CreatedUserId);
			objFeedback.setCreatedDate(CreatedDate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return objFeedback;
	}
}
