package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.VOGeneratorService;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.InterestRate;


public class InterestRateVOGenerator extends ServiceImpl implements VOGeneratorService {
	
	Log gLogger = LogFactory.getLog(getClass());

	public ResultObject generateMap(Map inputMap) {

		// TODO Auto-generated method stub

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		inputMap.get("requestObj");
		InterestRate interestRateVO = null;

		try {

			inputMap.put("DCPSInterestRate", interestRateVO);
			interestRateVO = generateVOMap(inputMap);
			inputMap.put("DCPSInterestRate", interestRateVO);
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			e.printStackTrace();
			gLogger.error(e);
		}
		return objRes;
	}

	public InterestRate generateVOMap(Map inputMap) throws Exception  {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		InterestRate interestRateVO = null;
		
		try {

			String strInterestRate = StringUtility.getParameter("txtInterestEntryInURI", request).trim();
			String strFromDate = StringUtility.getParameter("txtFromDate", request).trim();
			String strCmbYear = StringUtility.getParameter("cmbYear", request).trim();

			Float lInterestRate = null;
			Date FromDate = null;
			Long finYearId = null;

			if (!(strInterestRate.equals(""))) {
				lInterestRate = Float.parseFloat(strInterestRate);
			}

			if (!(strFromDate.equals(""))) {
				FromDate = simpleDateFormat.parse(strFromDate);
			}
			
			if(!strCmbYear.equals("") && !strCmbYear.equals("-1"))
			{
				finYearId = Long.valueOf(strCmbYear);
			}

			interestRateVO = new InterestRate();
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			interestRateVO.setFinYearId(finYearId);
			interestRateVO.setInterest(lInterestRate);
			interestRateVO.setEffectiveFromDate(FromDate);
			interestRateVO.setStatus(1L);
			interestRateVO.setPostId(CreatedPostId);
			interestRateVO.setUserId(CreatedUserId);
			interestRateVO.setCreatedDate(CreatedDate);
			
			

		} catch (Exception e) {
			
			e.printStackTrace();
			gLogger.error(e);
			throw e;
		}

		return interestRateVO;
	}
}
