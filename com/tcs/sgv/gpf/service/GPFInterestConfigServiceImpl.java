package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFInterestCalculationDAO;
import com.tcs.sgv.gpf.dao.GPFInterestCalculationDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfInterestRate;

public class GPFInterestConfigServiceImpl extends ServiceImpl implements GPFInterestConfigService {
	Log glogger = LogFactory.getLog(getClass());

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gLngUserId.toString();
		} catch (Exception e) {
			glogger.error("Error is :" + e, e);
		}

	}

	public ResultObject loadInterestConfig(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstGroup = null;
		try {
			setSessionInfo(inputMap);
			lstGroup = IFMSCommonServiceImpl.getLookupValues("GroupList", SessionHelper.getLangId(inputMap), inputMap);
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		inputMap.put("groupList", lstGroup);
		resObj.setResultValue(inputMap);
		resObj.setViewName("GPFInterestConfiguration");
		return resObj;
	}

	public ResultObject saveInterestConfig(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		String lBlFlag = "";
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrIsDataExist = "";
		
		try {
			//MstGpfInterestRate lObjInterestRate = (MstGpfInterestRate) inputMap.get("InterestRateData");
			GPFInterestCalculationDAO lObjArrearsManualEntryDAO = new GPFInterestCalculationDAOImpl(MstGpfInterestRate.class, serv.getSessionFactory());
			SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
			
			String lStrEmpGroup = StringUtility.getParameter("cmbGroup", request);
			String lStrEffectiveFromDate = StringUtility.getParameter("txtEffeFromDate", request);
			String lStrApplicableToDate = StringUtility.getParameter("txtApplicableToDate", request);
			String lStrInterestRate = StringUtility.getParameter("txtInterestRate", request);
			
			lStrIsDataExist = lObjArrearsManualEntryDAO.isRateExistForPeriod(Long.parseLong(lStrEmpGroup), lStrEffectiveFromDate);
			
			if(lStrIsDataExist.equals("N")){
				MstGpfInterestRate lObjInterestRate = new MstGpfInterestRate();
				Long lLngGpfInterestRateId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_INTEREST_RATE", inputMap);
				lObjInterestRate.setGpfInterestRateId(lLngGpfInterestRateId);
				lObjInterestRate.setEmpGroup(Long.parseLong(lStrEmpGroup));
				lObjInterestRate.setInterest(Float.parseFloat(lStrInterestRate));
				lObjInterestRate.setEffectiveFromDate(lObjSimpleDate.parse(lStrEffectiveFromDate));
				lObjInterestRate.setApplicableToDate(lObjSimpleDate.parse(lStrApplicableToDate));
				lObjInterestRate.setCreatedUserId(gLngUserId);
				lObjInterestRate.setCreatedPostId(gLngPostId);
				lObjInterestRate.setCreatedDate(DBUtility.getCurrentDateFromDB());
				lObjArrearsManualEntryDAO.create(lObjInterestRate);
				lBlFlag = "true";
			}else{
				lBlFlag = "Exist";
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForInerConfig(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDocForInerConfig(String flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

}
