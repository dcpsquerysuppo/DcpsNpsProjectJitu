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
import com.tcs.sgv.dcps.valueobject.NoticeBoard;

public class NoticeBoardVOGenerator extends ServiceImpl implements
VOGeneratorService{

	public ResultObject generateMap(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		inputMap.get("requestObj");
		NoticeBoard objNoticeBoard = null;

		try {
			objNoticeBoard = generateVOMap(inputMap);
			inputMap.put("NoticeBoard", objNoticeBoard);
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

	public NoticeBoard generateVOMap(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		inputMap.get("serviceLocator");
		LogFactory.getLog(getClass());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		NoticeBoard objNoticeBoard = new NoticeBoard();

		try {
			Long LangId = SessionHelper.getLangId(inputMap);
			Long LocId = SessionHelper.getLocationId(inputMap);
			Long DbId = SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();
			
			Long lLngNoticeId = IFMSCommonServiceImpl.getNextSeqNum(
						"MST_DCPS_NOTICE_BOARD", inputMap);
			objNoticeBoard.setDcpsNoticeIdPk(lLngNoticeId);
			objNoticeBoard.setNoticeSubject(StringUtility.getParameter("subject", request));
			objNoticeBoard.setIssuedBy(StringUtility.getParameter("issuedBy", request));
			objNoticeBoard.setIssuedDate(simpleDateFormat.parse(StringUtility.getParameter("issuedDate", request)));
			objNoticeBoard.setExpiryDate(simpleDateFormat.parse(StringUtility.getParameter("expiryDate", request)));
			objNoticeBoard.setNoticeType(StringUtility.getParameter("noticeType", request));
			objNoticeBoard.setFieldDeptCode(StringUtility.getParameter("fieldDeptCode", request));
			objNoticeBoard.setFileName(StringUtility.getParameter("uploadFile", request));
			objNoticeBoard.setPostId(CreatedPostId);
			objNoticeBoard.setUserId(CreatedUserId);
			objNoticeBoard.setCreatedDate(CreatedDate);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objNoticeBoard;
	}
}
