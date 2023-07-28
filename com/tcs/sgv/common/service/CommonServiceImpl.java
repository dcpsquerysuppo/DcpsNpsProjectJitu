/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 28, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.common.service;

//com.tcs.sgv.common.service.CommonServiceImpl
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.log4j.Logger;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Apr 28, 2011
 */
public class CommonServiceImpl extends ServiceImpl implements CommonService {

	/* Global Variable for Logger Class */
	private final static Logger gLogger = Logger
			.getLogger(CommonServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.common.service.CommonService#getDistrictsFromState(java.util
	 * .Map)
	 */
	public ResultObject getDistrictsFromState(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrStateId = null;
		String lStrAjaxResult = null;
		List<ComboValuesVO> lLstDistrict = new ArrayList<ComboValuesVO>();
		Long lLngStateId = null;
		try {
			Long lLngLangId = SessionHelper.getLangId(inputMap);
			lStrStateId = StringUtility.getParameter("state", request)
					.toString();
			lLngStateId = Long.valueOf(lStrStateId);

			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv
					.getSessionFactory());

			if (lStrStateId != null && lStrStateId.length() > 0) {
				lLstDistrict = lObjCommonDAO.getDistrictsOfState(lLngStateId,
						lLngLangId);
				lStrAjaxResult = new AjaxXmlBuilder().addItems(lLstDistrict,
						"desc", "id").toString();
				inputMap.put("ajaxKey", lStrAjaxResult);
				resObj.setViewName("ajaxData");
			} else if (lLngStateId != null) {
				lLstDistrict = lObjCommonDAO.getDistrictsOfState(lLngStateId,
						lLngLangId);
				inputMap.put("lLstDistrict", lLstDistrict);
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.common.service.CommonService#getBranchListFromBankCode(java
	 * .util.Map)
	 */
	public ResultObject getBranchListFromBankCode(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrBankCode = null;
		String lStrAjaxResult = null;
		String lStrLocCode = null;
		List<ComboValuesVO> lLstBankBranch = new ArrayList<ComboValuesVO>();
		Long lLngBankCode = null;
		try {
			
			if (StringUtility.getParameter("locCode", request).toString() != null && StringUtility.getParameter("locCode", request).toString().trim().length() > 0 && !"-1".equals(StringUtility.getParameter("locCode", request).toString())) {
				lStrLocCode = StringUtility.getParameter("locCode", request).toString();	
			}else{
				lStrLocCode = SessionHelper.getLocationCode(inputMap);	
			}
			 
			Long lLngLangId = SessionHelper.getLangId(inputMap);
			lStrBankCode = StringUtility.getParameter("bankCode", request)
					.toString();
			lLngBankCode = Long.valueOf(lStrBankCode);

			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv
					.getSessionFactory());

			if (lStrBankCode != null && lStrBankCode.length() > 0) {
				lLstBankBranch = lObjCommonDAO.getBranchListFromBankCode(
						lLngBankCode, lStrLocCode, lLngLangId);
				lStrAjaxResult = new AjaxXmlBuilder().addItems(lLstBankBranch,
						"desc", "id").toString();
				inputMap.put("ajaxKey", lStrAjaxResult);
				resObj.setViewName("ajaxData");
			} else if (lStrBankCode != null) {
				lLstBankBranch = lObjCommonDAO.getBranchListFromBankCode(
						lLngBankCode, lStrLocCode, lLngLangId);
				inputMap.put("lLstBankBranch", lLstBankBranch);
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}
	public ResultObject getBranchesFromBankCodeLocCode(Map<String, Object> inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());

		List lLstBranches = new ArrayList();
		try {

			String lStrBankCode = StringUtility.getParameter("bankCode", request);
			String lStrLocCode = StringUtility.getParameter("locCode", request).toString();			
			Long lLngLangId = SessionHelper.getLangId(inputMap);
			if (lStrBankCode != null && lStrBankCode.length() > 0) {				
				lLstBranches = lObjCommonDAO.getBranchsOfBank(lStrBankCode, lLngLangId, lStrLocCode);
			}

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		String lSBStatus = getResponseXMLDocForBranch(lLstBranches).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");

		return resObj;
	}
	private StringBuilder getResponseXMLDocForBranch(List listBranches) {

		StringBuilder lStrBldXML = new StringBuilder();
		Iterator itr = listBranches.iterator();
		lStrBldXML.append("<XMLDOC>");
		while (itr.hasNext()) {
			Object[] obj = (Object[]) itr.next();
			lStrBldXML.append("<BranchCode>");
			lStrBldXML.append(obj[0].toString());
			lStrBldXML.append("</BranchCode>");
			lStrBldXML.append("<BranchName>");
			lStrBldXML.append(obj[1].toString());
			lStrBldXML.append("</BranchName>");

		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
	public ResultObject isIFSCCodeExsist(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrBranchCode = null;
		String lStrBranchCodeString = null;
		String[] lArrStrBranchCode = null;
		CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
		List lLstResultList = null;
		String lStrIsExsist = "Y";
		boolean flag = true;
		String lStrReturnBranchCode = null;
		try {

			lStrBranchCodeString = StringUtility.getParameter(
					"branchCodeString", request);
			lArrStrBranchCode = lStrBranchCodeString.split("~");
			for (int i = 0; i < lArrStrBranchCode.length; i++) {
				lStrBranchCode = lArrStrBranchCode[i].toString();
				lLstResultList = lObjCommonDAO.isIFSCCodeExsist(lStrBranchCode);
				if (lLstResultList.isEmpty()) {
					flag = false;
					lStrReturnBranchCode = lStrBranchCode;
					break;
				}
			}

			if (flag == false) {
				lStrIsExsist = "N";
			}

			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append(" <FLAG> ");
			lStrBldXML.append(lStrIsExsist);
			lStrBldXML.append(" </FLAG> ");

			if (flag == false) {
				lStrBldXML.append(" <BranchCode> ");
				lStrBldXML.append(lStrReturnBranchCode);
				lStrBldXML.append(" </BranchCode> ");
			}
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject displayDCPSContent(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		return resObj;

	}
	
	public static boolean isValidLongNumber(String lStrValue)
	{
		boolean isNumber = false;
		try {
			Long.parseLong(lStrValue.trim());
			isNumber = true;
		} catch (NumberFormatException e) {
			isNumber = false;
		}
		return isNumber;
	}

	public static boolean isValidDate(String lStrValue)
	{
		boolean isDate = false;
		SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			lSdf.setLenient(false);
			Date lDtVal = lSdf.parse(lStrValue.trim());
			if (lSdf.format(lDtVal).equals(lStrValue)) {
				isDate = true; break label52:
			}
			isDate = false;
		}
		catch (ParseException e)
		{
			isDate = false;
		}
		label52: return isDate;
	}


}
