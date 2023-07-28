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
import com.tcs.sgv.dcps.valueobject.TickerMessage;

public class TickerMessageVOGenerator extends ServiceImpl implements
VOGeneratorService{
	public ResultObject generateMap(Map inputMap) {
		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		inputMap.get("requestObj");
		TickerMessage objTickerMessage = null;
		HttpServletRequest request = (HttpServletRequest) inputMap
		.get("requestObj");
		try {
			objTickerMessage = generateVOMap(inputMap);
			inputMap.put("TickerMessage", objTickerMessage);
			objRes.setResultValue(inputMap);
			String 	editFlag = StringUtility.getParameter("editFlag", request) ;
			inputMap.put("editFlag", editFlag);
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			e.printStackTrace();
		}
		return objRes;
	}

	public TickerMessage generateVOMap(Map inputMap) {
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		inputMap.get("serviceLocator");
		LogFactory.getLog(getClass());

		TickerMessage objTickerMessage = new TickerMessage();

		try {
			Long LangId = SessionHelper.getLangId(inputMap);
			Long LocId = SessionHelper.getLocationId(inputMap);
			Long DbId = SessionHelper.getDbId(inputMap);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();
			
			Long lLngTickerMessageId = null;
			
			
			if(StringUtility.getParameter("selectedMessageId", request) != null &&
					StringUtility.getParameter("selectedMessageId", request) != "" &&
					!StringUtility.getParameter("selectedMessageId", request).equals("0")
				){				
					lLngTickerMessageId= Long.parseLong(StringUtility.getParameter("selectedMessageId", request));
					objTickerMessage.setUpdatedDate(CreatedDate);
			}
			else
			{
				lLngTickerMessageId=IFMSCommonServiceImpl.getNextSeqNum(
						"MST_DCPS_TICKER_MSG", inputMap);
				objTickerMessage.setPostId(CreatedPostId);
				objTickerMessage.setUserId(CreatedUserId);
				objTickerMessage.setCreatedDate(CreatedDate);
			}
			
			objTickerMessage.setDcpsTickerMessageIdPk(lLngTickerMessageId);
			objTickerMessage.setMessage1(StringUtility.getParameter("message1", request));
			objTickerMessage.setMessage2(StringUtility.getParameter("message2", request));
			objTickerMessage.setMessage3(StringUtility.getParameter("message3", request));
			
			String startDate =  StringUtility.getParameter("startDate", request) != null ?StringUtility.getParameter("startDate", request):"";
			String endDate =  StringUtility.getParameter("endDate", request) != null ?StringUtility.getParameter("endDate", request):"";
			
			SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			Date dObjStartDate = null;
			Date dObjEndDate = null;
			
			if(startDate != null && startDate != "")
			{
				//System.out.println(" frmt.format(startDate) "+ sdf2.format(frmt.parse(startDate)));
				dObjStartDate = frmt.parse(startDate);
			}
			if(endDate != null && endDate != "")
			{
				//System.out.println(" frmt.format(endDate) "+frmt.format(endDate));
				dObjEndDate = frmt.parse(endDate);
			}
			
			objTickerMessage.setStartDate(dObjStartDate);
			objTickerMessage.setEndDate(dObjEndDate);
			
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objTickerMessage;
	}
}
