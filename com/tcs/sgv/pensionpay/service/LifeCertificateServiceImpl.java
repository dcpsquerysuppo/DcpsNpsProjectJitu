/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 30, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.util.StringUtils;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ReportExportHelper;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;

import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.LifeCertificateDAO;
import com.tcs.sgv.pensionpay.dao.LifeCertificateDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.LifeCertificateVO;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerSeenDtls;


/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 May 30, 2011
 */
public class LifeCertificateServiceImpl extends ServiceImpl implements LifeCertificateService {

	private Long gLngPostId = null;

	private Long gLngUserId = null;

	private Long gLngLangId = null;

	private Log gLogger = LogFactory.getLog(getClass());

	private Date gCurrDate = null;

	private String gStrLocCode = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private ResourceBundle gObjRsrcBndleMarathi = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants_ma");

	private void setSessionInfo(Map<String, Object> inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gLngLangId = SessionHelper.getLangId(inputMap);
		gCurrDate = DBUtility.getCurrentDateFromDB();
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.LifeCertificateService#getLifeCertificateList
	 * (java.util.Map)
	 */
	public ResultObject getLifeCertificateList(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List lLstResult = new ArrayList();
		List<LifeCertificateVO> lLstLifeCertificate = new ArrayList<LifeCertificateVO>();
		LifeCertificateVO lObjLifeCertificateVO = new LifeCertificateVO();
		setSessionInfo(inputMap);
		List<String> lLstCaseStaus = new ArrayList<String>();
		List<ComboValuesVO> lLstBanks = new ArrayList<ComboValuesVO>();
		List<ComboValuesVO> lLstBankBranch = new ArrayList<ComboValuesVO>();
		List<ComboValuesVO> lLstMonths = null;
		List<ComboValuesVO> lLstYears = null;

		Integer lIntTotalRecords = 0;
		String lStrSearchCrt = null;
		String lStrSearchType = null;
		String lStrSearchVal = null;
		String lStrPageNo = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrLifeCertFlag = "";

		// Mubeen 30 Nov 2012 - Start
		String lStrSortbyCrt = null;
		String lStrSortCrt = null;
		// Mubeen 30 Nov 2012 - End

		LifeCertificateDAO lObjLifeCertificateDAO = new LifeCertificateDAOImpl(LifeCertificateVO.class, serv.getSessionFactory());
		CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
		try {
			lLstCaseStaus.add(gObjRsrcBndle.getString("STATFLG.APPROVED"));
			lLstCaseStaus.add(gObjRsrcBndle.getString("STATFLG.MODIFIEDBYAUDITOR"));
			lLstCaseStaus.add(gObjRsrcBndle.getString("STATFLG.MODIFIED"));
			lLstCaseStaus.add(gObjRsrcBndle.getString("STATFLG.REJECTED"));
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);

			lLstMonths = lObjCommonDAO.getMonthList(SessionHelper.getLocale(request));
			lLstYears = lObjCommonDAO.getYearList(SessionHelper.getLocale(request));

			GregorianCalendar c = new GregorianCalendar();
			c.setTimeInMillis(new Date().getTime());

			Date lDtDate = new Date();
			lDtDate = lObjSimpleDateFormat.parse("01/01/" + (c.get(Calendar.YEAR)));

			// Mubeen 30 Nov 2012 - End

			// Mubeen 30 Nov 2012 - Start

			lStrSortbyCrt = StringUtility.getParameter("SortBy", request).trim();
			if (!("-1").equals(lStrSortbyCrt) && lStrSortbyCrt.length() > 0) {
				lStrSortCrt = lStrSortbyCrt;
			}

			if (!StringUtility.getParameter("SearchCrt", request).equals("-1")) {
				lStrSearchCrt = StringUtility.getParameter("SearchCrt", request).trim();
			}

			if (!StringUtility.getParameter("SearchType", request).equals("-1")) {
				lStrSearchType = StringUtility.getParameter("SearchType", request).trim();
			}

			lStrSearchVal = ((StringUtility.getParameter("SearchValue", request).trim()).length() > 0) ? StringUtility.getParameter("SearchValue", request).trim() : null;
			lStrPageNo = ((StringUtility.getParameter("PageNo", request).trim()).length() > 0) ? StringUtility.getParameter("PageNo", request).trim() : null;
			lStrBankCode = ((StringUtility.getParameter("BankCode", request).trim()).length() > 0) ? StringUtility.getParameter("BankCode", request).trim() : null;
			lStrBranchCode = ((StringUtility.getParameter("BranchCode", request).trim()).length() > 0) ? StringUtility.getParameter("BranchCode", request).trim() : null;
			lStrLifeCertFlag = ((StringUtility.getParameter("LifeCertFlag", request).trim()).length() > 0) ? StringUtility.getParameter("LifeCertFlag", request).trim() : null;

			/*
			 * 
			 * lStrSortbyCrt = StringUtility.getParameter("cmbSortCrt",
			 * request).trim(); if(!("-1").equals(lStrSortbyCrt) &&
			 * lStrSortbyCrt.length()>0){ lStrSortCrt=lStrSortbyCrt; }
			 * 
			 * if (!StringUtility.getParameter("cmbSearchCrt",
			 * request).equals("-1")) { lStrSearchCrt =
			 * StringUtility.getParameter("cmbSearchCrt", request).trim(); }
			 * 
			 * if (!StringUtility.getParameter("cmbSearchType",
			 * request).equals("-1")) { lStrSearchType =
			 * StringUtility.getParameter("cmbSearchType", request).trim(); }
			 * 
			 * 
			 * 
			 * /* // Mubeen Added on 04 Dec 2012 Start
			 * 
			 * 
			 * if (!StringUtility.getParameter("txtPageNo",
			 * request).equals("-1")) { lStrPageNo =
			 * StringUtility.getParameter("txtPageNo", request).trim(); }
			 * 
			 * if (!StringUtility.getParameter("cmbBankName",
			 * request).equals("-1")) { lStrBankCode =
			 * StringUtility.getParameter("cmbBankName", request).trim(); }
			 * 
			 * if (!StringUtility.getParameter("cmbBankBranch",
			 * request).equals("-1")) { lStrBranchCode =
			 * StringUtility.getParameter("cmbBankBranch", request).trim(); }
			 * 
			 * if (!StringUtility.getParameter("cmbSearchLifeCertFlag",
			 * request).equals("-1")) { lStrLifeCertFlag =
			 * StringUtility.getParameter("cmbSearchLifeCertFlag",
			 * request).trim(); } // Mubeen Added on 04 Dec 2012 End
			 * 
			 * 
			 * 
			 * 
			 * 
			 * // Mubeen commented on 04 Dec 2012 Start lStrSearchVal =
			 * ((StringUtility.getParameter("txtSeachValue",
			 * request).trim()).length() > 0) ?
			 * StringUtility.getParameter("txtSeachValue", request).trim() :
			 * null; lStrPageNo = ((StringUtility.getParameter("txtPageNo",
			 * request).trim()).length() > 0) ?
			 * StringUtility.getParameter("txtPageNo", request).trim() : null;
			 * lStrBankCode = ((StringUtility.getParameter("cmbBankName",
			 * request).trim()).length() > 0) ?
			 * StringUtility.getParameter("cmbBankName", request).trim() : null;
			 * lStrBranchCode = ((StringUtility.getParameter("cmbBankBranch",
			 * request).trim()).length() > 0) ?
			 * StringUtility.getParameter("cmbBankBranch", request).trim() :
			 * null; lStrLifeCertFlag =
			 * ((StringUtility.getParameter("cmbSearchLifeCertFlag",
			 * request).trim()).length() > 0) ?
			 * StringUtility.getParameter("cmbSearchLifeCertFlag",
			 * request).trim() : null; //Mubeen commented on 04 Dec 2012 End
			 */

			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lIntTotalRecords = lObjLifeCertificateDAO.getLifeCertificateCount(lLstCaseStaus, lDtDate, gStrLocCode, lStrSearchCrt, lStrSearchType, lStrSearchVal, lStrPageNo, lStrBankCode,
					lStrBranchCode, lStrLifeCertFlag, gLngPostId);
			if (lIntTotalRecords > 0) {
				lLstResult = lObjLifeCertificateDAO.getLifeCertificateList(displayTag, lLstCaseStaus, lDtDate, gStrLocCode, lStrSearchCrt, lStrSortCrt, lStrSearchType, lStrSearchVal, lStrPageNo,
						lStrBankCode, lStrBranchCode, lStrLifeCertFlag, gLngPostId);
			}

			if (!lLstResult.isEmpty()) {
				for (Integer lIntCnt = 0; lIntCnt < lLstResult.size(); lIntCnt++) {
					lObjLifeCertificateVO = new LifeCertificateVO();
					Object[] obj = (Object[]) lLstResult.get(lIntCnt);
					if (obj[0] != null) {
						lObjLifeCertificateVO.setPensionerCode(obj[0].toString());
					}
					if (obj[1] != null) {
						lObjLifeCertificateVO.setPpoNo(obj[1].toString());
					}
					if (obj[2] != null) {
						lObjLifeCertificateVO.setName(obj[2].toString());
					}
					if (obj[3] != null) {
						lObjLifeCertificateVO.setFamilyName(obj[3].toString());
					}
					if (obj[4] != null) {
						lObjLifeCertificateVO.setAliveFlag(obj[4].toString());
					}
					if (obj[5] != null) {
						lObjLifeCertificateVO.setLedgerNo(obj[5].toString());
					}
					if (obj[6] != null) {
						lObjLifeCertificateVO.setPageNo(obj[6].toString());
					}
					if (obj[7] != null) {
						lObjLifeCertificateVO.setBankName(obj[7].toString());
					}
					if (obj[8] != null) {
						lObjLifeCertificateVO.setBranchName(obj[8].toString());
					}
					if (obj[9] != null) {
						lObjLifeCertificateVO.setAccountNo(obj[9].toString());
					}
					if (obj[10] != null) {
						lObjLifeCertificateVO.setReceivedDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[10])));
					}
					if (obj[11] != null) {
						lObjLifeCertificateVO.setLifeCertFlag(obj[11].toString());
					}
					if (obj[12] != null) {
						lObjLifeCertificateVO.setArrearAmount(new BigDecimal(obj[12].toString()));
					}
					if (obj[13] != null) {
						lObjLifeCertificateVO.setPayMonthYear(Integer.parseInt(obj[13].toString()));
					}
					if (obj[14] != null) {
						lObjLifeCertificateVO.setPensionRqstId(Long.parseLong(obj[14].toString()));
					}
					lLstLifeCertificate.add(lObjLifeCertificateVO);
				}
			}
			if (lStrBankCode != null) {
				lLstBankBranch = lObjCommonDAO.getBranchListFromBankCode(Long.valueOf(lStrBankCode), gStrLocCode, gLngLangId);
			}
			if (lLstLifeCertificate != null && lLstLifeCertificate.size() > 0) {
				inputMap.put("LifeCertificateList", lLstLifeCertificate);
			}

			// Added by Mubeen 12 Dec 2012 - Start
			inputMap.put("SortCrt", lStrSortCrt);
			// Added by Mubeen 12 Dec 2012 - End

			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("SearchCrt", lStrSearchCrt);
			inputMap.put("SearchType", lStrSearchType);
			inputMap.put("SearchVal", lStrSearchVal);
			inputMap.put("PageNo", lStrPageNo);
			inputMap.put("lLstBanks", lLstBanks);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("BankCode", lStrBankCode);
			inputMap.put("lLstBankBranch", lLstBankBranch);
			inputMap.put("BranchCode", lStrBranchCode);
			inputMap.put("LifeCertFlag", lStrLifeCertFlag);
			inputMap.put("CurrentDate", lObjSimpleDateFormat.format(gCurrDate));
			resObj.setResultValue(inputMap);

			resObj.setViewName("LifeCertificate");

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	private String getChar(int count, String ele) {

		StringBuffer lSBSpace = new StringBuffer();
		for (int i = 0; i < count - 1; i++) {
			lSBSpace.append(ele);
		}
		return lSBSpace.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.LifeCertificateService#generateLifeCertificate
	 * (java.util.Map)
	 */
	public ResultObject generateLifeCertificateList(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List lLstResult = new ArrayList();
		LifeCertificateVO lObjLifeCertVO = new LifeCertificateVO();
		List<LifeCertificateVO> lLstLifeCertificate = new ArrayList<LifeCertificateVO>();
		StringBuffer lSbDisplayString = new StringBuffer();
		StringBuffer lSbPrintString = new StringBuffer();
		setSessionInfo(inputMap);
		String lStrNewLine = StringUtils.getLineSeparator();

		List<String> lLstCaseStaus = new ArrayList<String>();
		// Integer lIntSerialNo = 0;
		String lStrSearchCrt = null;
		String lStrSearchType = null;
		String lStrSearchVal = null;
		String lStrPageNo = null;
		String lStrPensionerCode = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		String lStrLifeCertFlag = "";
		List<String> lLstPensionerCode = new ArrayList<String>();
		LifeCertificateDAO lObjLifeCertificateDAO = new LifeCertificateDAOImpl(LifeCertificateVO.class, serv.getSessionFactory());
		try {
			lLstCaseStaus.add(gObjRsrcBndle.getString("STATFLG.APPROVED"));
			lLstCaseStaus.add(gObjRsrcBndle.getString("STATFLG.MODIFIEDBYAUDITOR"));
			lLstCaseStaus.add(gObjRsrcBndle.getString("STATFLG.MODIFIED"));
			lLstCaseStaus.add(gObjRsrcBndle.getString("STATFLG.REJECTED"));
			GregorianCalendar c = new GregorianCalendar();
			c.setTimeInMillis(new Date().getTime());
			Date lDtDate = new Date();
			lDtDate = lObjSimpleDateFormat.parse("01/01/" + (c.get(Calendar.YEAR)));
			if (!StringUtility.getParameter("SearchCrt", request).equals("-1")) {
				lStrSearchCrt = StringUtility.getParameter("SearchCrt", request).trim();
			}
			if (!StringUtility.getParameter("SearchType", request).equals("-1")) {
				lStrSearchType = StringUtility.getParameter("SearchType", request).trim();
			}

			if (StringUtility.getParameter("pensionerCode", request) != null) {
				lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
				String[] lArrStrPensionerCode = lStrPensionerCode.split("~");

				for (Integer lIntCnt = 0; lIntCnt < lArrStrPensionerCode.length; lIntCnt++) {
					if (lArrStrPensionerCode[lIntCnt] != null && !lArrStrPensionerCode[lIntCnt].equals("")) {
						lLstPensionerCode.add(lArrStrPensionerCode[lIntCnt]);
					}
				}
			}
			lStrSearchVal = ((StringUtility.getParameter("SearchValue", request).trim()).length() > 0) ? StringUtility.getParameter("SearchValue", request).trim() : null;
			lStrPageNo = ((StringUtility.getParameter("PageNo", request).trim()).length() > 0) ? StringUtility.getParameter("PageNo", request).trim() : null;
			lStrBankCode = ((StringUtility.getParameter("BankCode", request).trim()).length() > 0) ? StringUtility.getParameter("BankCode", request).trim() : null;
			lStrBranchCode = ((StringUtility.getParameter("BranchCode", request).trim()).length() > 0) ? StringUtility.getParameter("BranchCode", request).trim() : null;
			lStrLifeCertFlag = ((StringUtility.getParameter("LifeCertFlag", request).trim()).length() > 0) ? StringUtility.getParameter("LifeCertFlag", request).trim() : null;
			lLstResult = lObjLifeCertificateDAO.generateLifeCertificate(lLstCaseStaus, lDtDate, gStrLocCode, lStrSearchCrt, lStrSearchType, lStrSearchVal, lStrPageNo, lLstPensionerCode, lStrBankCode,
					lStrBranchCode, lStrLifeCertFlag, gLngPostId);

			if (!lLstResult.isEmpty()) {
				for (Integer lIntCnt = 0; lIntCnt < lLstResult.size(); lIntCnt++) {
					lObjLifeCertVO = new LifeCertificateVO();
					Object[] obj = (Object[]) lLstResult.get(lIntCnt);
					if (obj[0] != null) {
						lObjLifeCertVO.setPensionerCode(obj[0].toString());
					}
					if (obj[1] != null) {
						lObjLifeCertVO.setPpoNo(obj[1].toString());
					}
					if (obj[2] != null) {
						lObjLifeCertVO.setName(obj[2].toString());
					}
					// if(obj[3]!=null)
					// {
					// lObjLifeCertVO.setFamilyName(obj[3].toString());
					// }
					if (obj[3] != null) {
						lObjLifeCertVO.setAliveFlag(obj[3].toString());
					}
					if (obj[4] != null) {
						lObjLifeCertVO.setBankName(obj[4].toString());
					}
					if (obj[5] != null) {
						lObjLifeCertVO.setBranchName(obj[5].toString());
					}
					if (obj[6] != null) {
						lObjLifeCertVO.setAccountNo(obj[6].toString());
					}
					if (obj[7] != null) {
						lObjLifeCertVO.setBankCode(obj[7].toString());
					}
					if (obj[8] != null) {
						lObjLifeCertVO.setBranchCode(obj[8].toString());
					}
					lLstLifeCertificate.add(lObjLifeCertVO);
				}
			}

			inputMap.put("LifeCertificateList", lLstLifeCertificate);

			Map headingMap = new HashMap();
			Map<String, Map<String, List<LifeCertificateVO>>> lBankMap = new LinkedHashMap<String, Map<String, List<LifeCertificateVO>>>();
			Map<String, List<LifeCertificateVO>> lBranchMap = new LinkedHashMap<String, List<LifeCertificateVO>>();
			List<LifeCertificateVO> lLstLifeCertDtls = new ArrayList<LifeCertificateVO>();
			Map<String, String> lMapBankDtls = new HashMap<String, String>();
			Map<String, String> lMapBranchDtls = new HashMap<String, String>();
			for (LifeCertificateVO lObjLifeCertificateVO : lLstLifeCertificate) {
				String key = lObjLifeCertificateVO.getBankCode() + " - " + lObjLifeCertificateVO.getBranchCode();
				/********** heading map *************/
				if (!headingMap.containsKey(key))
					headingMap.put(key, "BANK NAME : " + lObjLifeCertificateVO.getBankName() + "     " + "BRANCH NAME : " + lObjLifeCertificateVO.getBranchName());

				lMapBankDtls.put(lObjLifeCertificateVO.getBankCode(), lObjLifeCertificateVO.getBankName());
				lMapBranchDtls.put(lObjLifeCertificateVO.getBranchCode(), lObjLifeCertificateVO.getBranchName());
				lBranchMap = lBankMap.get(lObjLifeCertificateVO.getBankCode());
				if (lBranchMap != null) {
					lLstLifeCertDtls = lBranchMap.get(key);
					if (lLstLifeCertDtls != null) {
						lLstLifeCertDtls.add(lObjLifeCertificateVO);
					} else {
						lLstLifeCertDtls = new ArrayList<LifeCertificateVO>();
						lLstLifeCertDtls.add(lObjLifeCertificateVO);
						lBranchMap.put(key, lLstLifeCertDtls);
					}
				} else {
					lBranchMap = new LinkedHashMap<String, List<LifeCertificateVO>>();
					lLstLifeCertDtls = new ArrayList<LifeCertificateVO>();
					lLstLifeCertDtls.add(lObjLifeCertificateVO);
					lBranchMap.put(key, lLstLifeCertDtls);
					lBankMap.put(lObjLifeCertificateVO.getBankCode(), lBranchMap);
				}
			}
			List<List<LifeCertificateVO>> arrOuter = new ArrayList<List<LifeCertificateVO>>();
			StringBuilder lSbHeader = new StringBuilder();
			String lStrDisplay = "";
			String lStrBranchDisplay = "";
			// String Line_Sep = System.getProperty("line.separator");
			lSbHeader.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT1"));
			lSbHeader.append("\r\n");
			lSbHeader.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT2"));
			lSbHeader.append("\r\n");
			lSbHeader.append("\r\n");
			lSbHeader.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT3"));
			lSbHeader.append("\r\n");
			lSbHeader.append("\r\n");
			lSbHeader.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT4"));
			lSbHeader.append("\r\n");
			lSbHeader.append("1. " + gObjRsrcBndleMarathi.getString("PPMT.LIFECERT5"));
			lSbHeader.append("\r\n");
			lSbHeader.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT6"));
			lSbHeader.append("\r\n");
			lSbHeader.append("2. " + gObjRsrcBndleMarathi.getString("PPMT.LIFECERT7"));
			lSbHeader.append("\r\n");
			lSbHeader.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT8"));
			lSbHeader.append("\r\n");
			lSbHeader.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT9"));
			lSbHeader.append("\r\n");
			lSbHeader.append("\r\n");
			String lStrHeader = "";
			// lStrHeader = lStrHeader +"\r\n";

			String strParameterString = " Bank : " + lStrBankCode;
			String lStrFooter = "";

			int nMode = 80;
			boolean pageBreakonGrouping = false;
			ReportExportHelper objExport = new ReportExportHelper();
			objExport.gStrAlign = "L";
			// Map returnMap = new HashMap();
			ArrayList lineList = new ArrayList();
			StringBuffer sbLine = new StringBuffer();

			for (int i = 0; i < 79; i++)
				sbLine.append("-");
			lineList.add(sbLine.toString());
			lineList.add("");
			lineList.add("");
			lineList.add("");
			lineList.add("");
			lineList.add("");

			ArrayList blankList = new ArrayList();
			blankList.add("");
			blankList.add("");
			blankList.add("");
			blankList.add("");
			// blankList.add("");
			blankList.add("");

			ColumnVo[] columnHeading = new ColumnVo[5];
			columnHeading[0] = new ColumnVo("Sr. No.", 1, 7, 0, false, false, true);
			columnHeading[1] = new ColumnVo("NAME OF PENSIONER", 1, 20, 0, true, false, true);
			columnHeading[2] = new ColumnVo("PPO NO.", 1, 15, 0, true, false, true);
			columnHeading[3] = new ColumnVo("A/C NO.", 2, 15, 0, false, false, true);
			// columnHeading[3] = new
			// ColumnVo("RE-EMPLOYED(YES/NO)",1,5,0,false,false,true);
			// columnHeading[4] = new
			// ColumnVo("RE-MARRIED(YES/NO)",2,5,0,false,false,true);
			columnHeading[4] = new ColumnVo("SIGNATURE OF PENSIONER", 1, 18, 0, false, false, true);

			for (String mainKey : lBankMap.keySet()) {
				Map<String, List<LifeCertificateVO>> dataMap = lBankMap.get(mainKey);

				boolean mHeadFlag = true;

				for (String key : dataMap.keySet()) {
					Integer lIntSerialNo = 0;
					arrOuter = new ArrayList<List<LifeCertificateVO>>();
					List<LifeCertificateVO> dataList = dataMap.get(key);
					for (LifeCertificateVO lObjLifeCertificateVO : dataList) {
						lIntSerialNo++;
						if (mHeadFlag) {
							lStrHeader = (String) headingMap.get(key);
							mHeadFlag = false;
						}

						ArrayList inList = new ArrayList();
						inList.add(lIntSerialNo);
						inList.add(lObjLifeCertificateVO.getName());
						// if("Y".equalsIgnoreCase(lObjLifeCertificateVO.getAliveFlag()))
						// {
						// inList.add(lObjLifeCertificateVO.getName());
						// }
						// else
						// {
						// inList.add(lObjLifeCertificateVO.getFamilyName());
						// }
						inList.add(lObjLifeCertificateVO.getPpoNo());
						inList.add(lObjLifeCertificateVO.getAccountNo());
						// inList.add("");
						// inList.add("");
						inList.add("");

						arrOuter.add(inList);
						arrOuter.add(blankList);
						arrOuter.add(blankList);
						arrOuter.add(blankList);
						// inList = new ArrayList();
						// inList.add("");
						// inList.add("");
						// inList.add(lObjLifeCertificateVO.getPpoNo());
						// inList.add("");
						// inList.add("");
						// inList.add("");
						// arrOuter.add(inList);

						inList = new ArrayList();
						inList.add(getChar(8, "-"));
						inList.add(getChar(21, "-"));
						inList.add(getChar(16, "-"));
						inList.add(getChar(16, "-"));
						// inList.add(getChar(6, "-"));
						inList.add(getChar(19, "-"));

						arrOuter.add(inList);
						lStrBranchDisplay = objExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString() + lStrHeader, lStrFooter, null, nMode, true, 1, true, "|");

						// lStrDisplay = lStrDisplay + lStrBranchDisplay;
					}

					lStrHeader = "";
					StringBuffer lStrFooterDtls = new StringBuffer();

					lStrFooterDtls.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT13"));
					lStrFooterDtls.append("\r\n");
					lStrFooterDtls.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT14"));
					lStrFooterDtls.append("\r\n");
					lStrFooterDtls.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT15"));
					lStrFooterDtls.append("\r\n");
					lStrFooterDtls.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT16"));
					lStrFooterDtls.append("\r\n");
					lStrFooterDtls.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT17"));
					lStrFooterDtls.append("\r\n");
					lStrFooterDtls.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT18"));
					lStrFooterDtls.append("\r\n");
					lStrFooterDtls.append(gObjRsrcBndleMarathi.getString("PPMT.LIFECERT19"));
					lStrFooterDtls.append("\r\n");
					// lStrFooterDtls.append("THE ABOVE PENSIONERS FROM SR.NO.   TO   SIGNED BEFORE ME ON OR AFTER ");
					// lStrFooterDtls.append("\r\n");
					// lStrFooterDtls.append("\r\n");
					// lStrFooterDtls.append("\r\n");
					// lStrFooterDtls.append("\r\n");
					// lStrFooterDtls.append(String.format("%-67s","SIGNATURE OF BANK MANAGER"));
					// lStrFooterDtls.append(String.format("%-13s","BANK SEAL"));
					// lStrFooterDtls.append("\r\n");
					// lStrFooterDtls.append(getChar(80, "-"));
					lStrFooterDtls.append((char) 12);

					lStrDisplay = lStrDisplay + lStrBranchDisplay + lStrFooterDtls.toString();
					mHeadFlag = true;

				}
			}
			Map lDetailMap = new HashMap();
			String lStrExportTo = DBConstants.DIS_ONSCREEN;
			if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo))
				lDetailMap.put(DBConstants.DIS_ONSCREEN, lStrDisplay);
			else if ((DBConstants.DIS_TEXTFILE).equals(lStrExportTo))
				lDetailMap.put(DBConstants.DIS_TEXTFILE, lStrDisplay);

				//	inputMap.put("sOutput4Print", lStrDisplay);
				//	resObj.setViewName("LifeCertificatePrintReport");

			ReportExportHelper rptExpHelper = new ReportExportHelper();
			rptExpHelper.getExportData(resObj,lStrExportTo,inputMap,lDetailMap,false);
			
			
			resObj.setResultValue(inputMap);

			/*
			 * StringBuilder sHeadingCol = new StringBuilder();
			 * sHeadingCol.append("\r\n"); sHeadingCol.append(getChar(80, "_"));
			 * sHeadingCol.append("\r\n"); sHeadingCol.append(
			 * "Sr. | NAME OF PENSIONER |  A/C NO./ |   RE-   |    RE-  |SIGNATURE OF PENSIONER"
			 * ); sHeadingCol.append("\r\n"); sHeadingCol.append(
			 * "No. |                   |  PPO NO   | EMPLOYED| MARRIED |");
			 * sHeadingCol.append("\r\n"); sHeadingCol.append(
			 * "    |                   |           | (YES/NO)| (YES/NO)|");
			 * sHeadingCol.append("\r\n"); sHeadingCol.append(getChar(80, "_"));
			 * 
			 * Set<String> lSetBank = new LinkedHashSet<String>(); Set<String>
			 * lSetBranch = new LinkedHashSet<String>(); lSetBank =
			 * lBankMap.keySet();
			 * 
			 * lSbDisplayString.append("<div><pre>"); for(String lStrBnkCode :
			 * lSetBank) { lIntSerialNo=0; lBranchMap =
			 * lBankMap.get(lStrBnkCode); lSetBranch = lBranchMap.keySet();
			 * //Integer lIntBranchFlag=0;
			 * 
			 * for(String lStrBrnchCode : lSetBranch) { lIntSerialNo=0;
			 * 
			 * lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append(String.format
			 * ("%45s","LIFE CERTIFICATE")); lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append("BANK NAME : ");
			 * lSbDisplayString.append(String.format("%-27s",
			 * lMapBankDtls.get(lStrBnkCode)));
			 * lSbDisplayString.append("BRANCH NAME : ");
			 * lSbDisplayString.append(String.format("%-26s",
			 * lMapBranchDtls.get(lStrBrnchCode)));
			 * //lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append(sHeadingCol);
			 * 
			 * lLstLifeCertDtls = lBranchMap.get(lStrBrnchCode); Integer
			 * lIntFlag=0; Integer lIntRowCnt =11; for(LifeCertificateVO
			 * lObjLifeCertificateVO : lLstLifeCertDtls) { lIntSerialNo++;
			 * lSbDisplayString.append("\r\n"); lIntRowCnt++; if ((lIntRowCnt !=
			 * 0) && (lIntRowCnt % 52 == 0)) {
			 * 
			 * lSbDisplayString.append((char) 12);
			 * lSbDisplayString.append(sHeadingCol); lIntRowCnt =11; } //
			 * if(lIntFlag == 0) // { //
			 * lSbDisplayString.append(String.format("%25s", "")); // } // else
			 * // { lSbDisplayString.append(String.format("%25s", "")); //}
			 * 
			 * lSbDisplayString.append(String.format("%-21s",
			 * lObjLifeCertificateVO.getAccountNo())); lIntRowCnt++; if
			 * ((lIntRowCnt != 0) && (lIntRowCnt % 52 == 0)) {
			 * 
			 * lSbDisplayString.append((char) 12);
			 * lSbDisplayString.append(sHeadingCol); lIntRowCnt =11; }
			 * 
			 * lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append(String.format("%-5s", lIntSerialNo));
			 * if("Y".equalsIgnoreCase(lObjLifeCertificateVO.getAliveFlag())) {
			 * lSbDisplayString.append(String.format("%-21s",
			 * lObjLifeCertificateVO.getName())); } else {
			 * lSbDisplayString.append(String.format("%-21s",
			 * lObjLifeCertificateVO.getFamilyName())); } lIntRowCnt++; if
			 * ((lIntRowCnt != 0) && (lIntRowCnt % 52 == 0)) {
			 * 
			 * lSbDisplayString.append((char) 12);
			 * lSbDisplayString.append(sHeadingCol); lIntRowCnt =11; }
			 * lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append(String.format("%25s", ""));
			 * lSbDisplayString.append(String.format("%-21s",
			 * lObjLifeCertificateVO.getPpoNo())); lIntRowCnt++; if ((lIntRowCnt
			 * != 0) && (lIntRowCnt % 52 == 0)) {
			 * 
			 * lSbDisplayString.append((char) 12);
			 * lSbDisplayString.append(sHeadingCol); lIntRowCnt =11; }
			 * lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append(getChar(80, "_")); lIntRowCnt++; if
			 * ((lIntRowCnt != 0) && (lIntRowCnt % 52 == 0)) {
			 * 
			 * lSbDisplayString.append((char) 12);
			 * lSbDisplayString.append(sHeadingCol); lIntRowCnt =11; }
			 * //lSbDisplayString.append("\r\n"); lIntFlag = 1; }
			 * lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append(getChar(80, "_"));
			 * lSbDisplayString.append("\r\n"); lSbDisplayString.append(
			 * "THE ABOVE PENSIONERS FROM SR.NO.   TO   SIGNED BEFORE ME ON OR AFTER "
			 * ); lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append("\r\n"); lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append(String.format
			 * ("%-67s","SIGNATURE OF BANK MANAGER"));
			 * lSbDisplayString.append(String.format("%-13s","BANK SEAL"));
			 * lSbDisplayString.append("\r\n");
			 * lSbDisplayString.append(getChar(80, "_"));
			 * lSbDisplayString.append((char) 12); }
			 * lSbDisplayString.append("\r\n"); lSbDisplayString.append("\r\n");
			 * 
			 * 
			 * } lSbDisplayString.append("</pre></div>");
			 */
			// Preapare list of list
			// List<List<List>> lLstBank = new ArrayList<List<List>>();
			// List<List> lLstBranch = new ArrayList<List>();
			// List lLstLifeCert =new ArrayList();
			// for(String lStrBnkCode : lSetBank)
			// {
			// lBranchMap = lBankMap.get(lStrBnkCode);
			// lSetBranch = lBranchMap.keySet();
			// //Integer lIntBranchFlag=0;
			// for(String lStrBrnchCode : lSetBranch)
			// {
			//
			// lLstLifeCert = lBranchMap.get(lStrBrnchCode);
			// lLstBranch.add(lLstLifeCert);
			//
			//
			// }
			// lLstBank.add(lLstBranch);
			// }
			//
			// inputMap.put("BankDtls", lLstBank);

			// lSbDisplayString.append("\n");
			// lSbDisplayString.append(space(31) +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR1")+ "\n");
			// lSbDisplayString.append(space(16) +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR2") + "\n");
			// lSbDisplayString.append("\n\n");
			// lSbDisplayString.append(lObjLifeCertificateVO.getTreasuryName() +
			// "\n");
			// if(lObjLifeCertificateVO.getTreasuryAddr1() != null)
			// {
			// lSbDisplayString.append(lObjLifeCertificateVO.getTreasuryAddr1());
			// }
			// if(lObjLifeCertificateVO.getTreasuryAddr2() != null)
			// {
			// lSbDisplayString.append("\n" +
			// lObjLifeCertificateVO.getTreasuryAddr2());
			// }
			// lSbDisplayString.append("\n\n\n" +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR3"));
			// lSbDisplayString.append("\n" +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR4"));
			// lSbDisplayString.append("\n" +
			// gObjRsrcBndle.getString("PPMT.PENSION"));
			// lSbDisplayString.append("\n\n\n\n");
			// lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.LIFECERTSTR5"));
			// if("Y".equalsIgnoreCase(lObjLifeCertificateVO.getAliveFlag()))
			// {
			// lSbDisplayString.append(space(1) +
			// lObjLifeCertificateVO.getName() + space(1) +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR6"));
			// }
			// else
			// {
			// lSbDisplayString.append(space(1) +
			// lObjLifeCertificateVO.getFamilyName() + space(1) +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR6"));
			// }
			// lSbDisplayString.append("\n" +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR7"));
			// lSbDisplayString.append(space(1) +
			// lObjLifeCertificateVO.getPpoNo());
			// lSbDisplayString.append(space(1) +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR8"));
			// lSbDisplayString.append("\n\n\n\n");
			// lSbDisplayString.append(space(48)+
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR10"));
			// lSbDisplayString.append("\n\n\n" + space(45) +
			// lObjLifeCertificateVO.getBankName());
			// lSbDisplayString.append("," +
			// lObjLifeCertificateVO.getBranchName());
			// lSbDisplayString.append("\n\n\n\n\n\n");
			// lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.LIFECERTSTR11"));
			// lSbDisplayString.append("\n" +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR12"));
			// lSbDisplayString.append("\n\n\n\n");
			//
			//
			// lSbPrintString.append("<div><pre>");
			// lSbPrintString.append(lStrNewLine);
			// lSbPrintString.append(space(31) +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR1")+ lStrNewLine);
			// lSbPrintString.append(space(16) +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR2") + lStrNewLine);
			// lSbPrintString.append(lStrNewLine + lStrNewLine);
			// lSbPrintString.append(lObjLifeCertificateVO.getTreasuryName() +
			// lStrNewLine);
			// if(lObjLifeCertificateVO.getTreasuryAddr1() != null)
			// {
			// lSbPrintString.append(lObjLifeCertificateVO.getTreasuryAddr1() +
			// lStrNewLine);
			// }
			// if(lObjLifeCertificateVO.getTreasuryAddr2() != null)
			// {
			// lSbPrintString.append("\n" +
			// lObjLifeCertificateVO.getTreasuryAddr2());
			// }
			//
			// lSbPrintString.append(lStrNewLine + lStrNewLine + lStrNewLine +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR3"));
			// lSbPrintString.append(lStrNewLine +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR4"));
			// lSbPrintString.append(lStrNewLine +
			// gObjRsrcBndle.getString("PPMT.PENSION"));
			// lSbPrintString.append(lStrNewLine +lStrNewLine + lStrNewLine
			// +lStrNewLine);
			// lSbPrintString.append(gObjRsrcBndle.getString("PPMT.LIFECERTSTR5"));
			// if("Y".equalsIgnoreCase(lObjLifeCertificateVO.getAliveFlag()))
			// {
			// lSbPrintString.append(space(1) + lObjLifeCertificateVO.getName()
			// + space(1) + gObjRsrcBndle.getString("PPMT.LIFECERTSTR6"));
			// }
			// else
			// {
			// lSbPrintString.append(space(1) +
			// lObjLifeCertificateVO.getFamilyName() + space(1) +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR6"));
			// }
			// lSbPrintString.append(lStrNewLine +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR7"));
			// lSbPrintString.append(space(1) +
			// lObjLifeCertificateVO.getPpoNo());
			// lSbPrintString.append(space(1) +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR8"));
			// lSbPrintString.append(lStrNewLine + lStrNewLine + lStrNewLine +
			// lStrNewLine);
			// lSbPrintString.append(space(48)+
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR10"));
			// lSbPrintString.append(lStrNewLine +lStrNewLine +lStrNewLine
			// +space(45) + lObjLifeCertificateVO.getBankName());
			// lSbPrintString.append("," +
			// lObjLifeCertificateVO.getBranchName());
			// lSbPrintString.append(lStrNewLine+lStrNewLine+lStrNewLine+lStrNewLine+lStrNewLine+lStrNewLine);
			// lSbPrintString.append(gObjRsrcBndle.getString("PPMT.LIFECERTSTR11"));
			// lSbPrintString.append(lStrNewLine +
			// gObjRsrcBndle.getString("PPMT.LIFECERTSTR12"));
			// lSbPrintString.append(lStrNewLine+lStrNewLine+lStrNewLine+lStrNewLine);
			// lSbPrintString.append("</pre></div>");

			// }

			/*
			 * inputMap.put("DisplayLifeCertificateString",
			 * lSbDisplayString.toString().replace("</pre></div>",
			 * "").replace("<div><pre>", ""));
			 * inputMap.put("PrintLifeCertificateString", lSbDisplayString);
			 * resObj.setResultValue(inputMap);
			 * 
			 * resObj.setViewName("PrintLifeCertificate");
			 */

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.LifeCertificateService#getPnsnrDtlsFromPpoNo
	 * (java.util.Map)
	 */
	public ResultObject getPnsnrDtlsFromPpoNo(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<LifeCertificateVO> lLstResult = new ArrayList<LifeCertificateVO>();
		List lLstPnsnrDtls = new ArrayList();
		setSessionInfo(inputMap);

		String lStrPpoNo = null;
		String lStrPensionerId = null;
		String lStrPnsnrName = null;
		String lStrFamilyMemName = null;
		String lStrLedgerNo = null;
		String lStrPageNo = null;
		String lStrAccountNo = null;
		String lStrAliveFlag = null;
		String lStrBankName = null;
		String lStrBranchName = null;
		Long lLngFinYearId = null;
		StringBuilder lStrBldXML = null;

		LifeCertificateDAO lObjLifeCertificateDAO = new LifeCertificateDAOImpl(LifeCertificateVO.class, serv.getSessionFactory());
		try {

			lStrPpoNo = StringUtility.getParameter("PpoNo", request).trim();
			lLngFinYearId = Long.valueOf(SessionHelper.getFinYrId(inputMap));
			if (!"".equals(lStrPpoNo)) {
				lLstResult = lObjLifeCertificateDAO.validatePpoWithReceivedCert(gStrLocCode, lStrPpoNo, lLngFinYearId);
			}
			if (lLstResult.isEmpty()) {

				if (!"".equals(lStrPpoNo)) {
					lLstPnsnrDtls = lObjLifeCertificateDAO.getPnsnrDtlsFromPpoNo(lStrPpoNo, gStrLocCode);
				}
				if (!lLstPnsnrDtls.isEmpty()) {
					Object[] obj = (Object[]) lLstPnsnrDtls.get(0);
					if (obj[0] != null) {
						lStrPensionerId = obj[0].toString();
					}
					if (obj[1] != null) {
						lStrPpoNo = obj[1].toString();
					}
					if (obj[2] != null) {
						lStrPnsnrName = obj[2].toString();
					}
					if (obj[3] != null) {
						lStrFamilyMemName = obj[3].toString();
					}
					if (obj[4] != null) {
						lStrLedgerNo = obj[4].toString();
					}
					if (obj[5] != null) {
						lStrPageNo = obj[5].toString();
					}
					if (obj[6] != null) {
						lStrAliveFlag = obj[6].toString();
					}
					if (obj[7] != null) {
						lStrBankName = obj[7].toString();
					}
					if (obj[8] != null) {
						lStrBranchName = obj[8].toString();
					}
					if (obj[9] != null) {
						lStrAccountNo = obj[9].toString();
					}
					inputMap.put("PensionerId", lStrPensionerId);
					inputMap.put("PpoNo", lStrPpoNo);
					inputMap.put("PnsnrName", lStrPnsnrName);
					inputMap.put("FamilyMemName", lStrFamilyMemName);
					inputMap.put("LedgerNo", lStrLedgerNo);
					inputMap.put("PageNo", lStrPageNo);
					inputMap.put("AccountNo", lStrAccountNo);
					inputMap.put("AliveFlag", lStrAliveFlag);
					inputMap.put("BankName", lStrBankName);
					inputMap.put("BranchName", lStrBranchName);

				}
			}
			resObj.setResultValue(inputMap);

			lStrBldXML = getResponseXMLDoc(inputMap);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Map<String, Object> inputMap) {

		StringBuilder lStrHidPKs = new StringBuilder();
		if (inputMap.containsKey("PensionerId")) {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<PENSIONERID>" + inputMap.get("PensionerId"));
			lStrHidPKs.append("</PENSIONERID>");
			lStrHidPKs.append("<PPONO>" + inputMap.get("PpoNo"));
			lStrHidPKs.append("</PPONO>");
			lStrHidPKs.append("<PENSIONERNAME>" + inputMap.get("PnsnrName"));
			lStrHidPKs.append("</PENSIONERNAME>");
			lStrHidPKs.append("<FAMILYMEMNAME>" + inputMap.get("FamilyMemName"));
			lStrHidPKs.append("</FAMILYMEMNAME>");
			lStrHidPKs.append("<LEDGERNO>" + inputMap.get("LedgerNo"));
			lStrHidPKs.append("</LEDGERNO>");
			lStrHidPKs.append("<PAGENO>" + inputMap.get("PageNo"));
			lStrHidPKs.append("</PAGENO>");
			lStrHidPKs.append("<ACCOUNTNO>" + inputMap.get("AccountNo"));
			lStrHidPKs.append("</ACCOUNTNO>");
			lStrHidPKs.append("<ALIVEFLAG>" + inputMap.get("AliveFlag"));
			lStrHidPKs.append("</ALIVEFLAG>");
			lStrHidPKs.append("<BANKNAME>" + inputMap.get("BankName"));
			lStrHidPKs.append("</BANKNAME>");
			lStrHidPKs.append("<BRANCHNAME>" + inputMap.get("BranchName"));
			lStrHidPKs.append("</BRANCHNAME>");
			lStrHidPKs.append("</XMLDOC>");
		} else {
			lStrHidPKs.append("<XMLDOC>");
			lStrHidPKs.append("<ISEXISTS>");
			lStrHidPKs.append("N");
			lStrHidPKs.append("</ISEXISTS>");
			lStrHidPKs.append("</XMLDOC>");
		}
		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.LifeCertificateService#insertPensionerSeenDtls
	 * (java.util.Map)
	 */

	public ResultObject insertPensionerSeenDtls(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		StringBuilder lStrBldXML = new StringBuilder();
		Long lLngSeenDtlId = null;

		TrnPensionerSeenDtls lObjTrnPensionerSeenDtlsVO = new TrnPensionerSeenDtls();
		try {
			LifeCertificateDAO lObjLifeCertificateDAO = new LifeCertificateDAOImpl(TrnPensionerSeenDtls.class, serv.getSessionFactory());
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = new TrnPensionRqstHdr();
			String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			String lStrReceivedDate = StringUtility.getParameter("receiveDate", request);
			String lStrLifeCertFlag = StringUtility.getParameter("lifeCertFlag", request);

			String[] lArrStrPensionerCode = lStrPensionerCode.split("~");

			for (int lIntCnt = 0; lIntCnt < lArrStrPensionerCode.length; lIntCnt++) {
				lObjTrnPensionerSeenDtlsVO = lObjLifeCertificateDAO.getTrnPensionerSeenDtls(lArrStrPensionerCode[lIntCnt]);
				if (lObjTrnPensionerSeenDtlsVO.getSeenDtlsId() == null) {
					lObjTrnPensionerSeenDtlsVO = new TrnPensionerSeenDtls();
					lObjTrnPensionerSeenDtlsVO.setCreatedUserId(new BigDecimal(gLngUserId));
					lObjTrnPensionerSeenDtlsVO.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjTrnPensionerSeenDtlsVO.setCreatedDate(gCurrDate);
				} else {
					lObjTrnPensionerSeenDtlsVO.setUpdatedUserId(new BigDecimal(gLngUserId));
					lObjTrnPensionerSeenDtlsVO.setUpdatedPostId(new BigDecimal(gLngPostId));
					lObjTrnPensionerSeenDtlsVO.setUpdatedDate(gCurrDate);
				}
				lObjTrnPensionerSeenDtlsVO.setSeenFlag(lStrLifeCertFlag);
				lObjTrnPensionerSeenDtlsVO.setPensionerCode(lArrStrPensionerCode[lIntCnt]);
				if (!"".equals(lStrReceivedDate))
					lObjTrnPensionerSeenDtlsVO.setSeenDate(lObjSimpleDateFormat.parse(lStrReceivedDate));
				lObjTrnPensionerSeenDtlsVO.setFinYearId(Long.valueOf(SessionHelper.getFinYrId(inputMap)));
				if (lObjTrnPensionerSeenDtlsVO.getSeenDtlsId() != null) {
					lObjLifeCertificateDAO.update(lObjTrnPensionerSeenDtlsVO);
				} else {
					lLngSeenDtlId = IFMSCommonServiceImpl.getNextSeqNum("trn_pensioner_seen_dtls", inputMap);
					lObjTrnPensionerSeenDtlsVO.setSeenDtlsId(lLngSeenDtlId);
					lObjLifeCertificateDAO.create(lObjTrnPensionerSeenDtlsVO);
				}
				lObjTrnPensionRqstHdrVO = lObjPhysicalCaseInwardDAO.getTrnPensionRqstHdrVO(lArrStrPensionerCode[lIntCnt]);

				lObjTrnPensionRqstHdrVO.setSeenFlag(lStrLifeCertFlag);
				lObjPhysicalCaseInwardDAO.update(lObjTrnPensionRqstHdrVO);
			}

			/*
			 * String[] lArrStrPensionerId =
			 * StringUtility.getParameterValues("txtPensionerId", request);
			 * String[] lArrStrReceivedDate =
			 * StringUtility.getParameterValues("txtLCReceivedDate", request);
			 * String[] lArrStrBundleNo =
			 * StringUtility.getParameterValues("txtBundleNumber", request);
			 * String[] lArrStrLifeCertFlag =
			 * StringUtility.getParameterValues("txtLifeCertFlag", request);
			 * 
			 * if (lArrStrPensionerId.length > 0) { for (Integer lIntCnt = 0;
			 * lIntCnt < lArrStrPensionerId.length; lIntCnt++) {
			 * lObjTrnPensionerSeenDtlsVO=new TrnPensionerSeenDtls();
			 * lLngSeenDtlId =
			 * IFMSCommonServiceImpl.getNextSeqNum("trn_pensioner_seen_dtls",
			 * inputMap);
			 * lObjTrnPensionerSeenDtlsVO.setSeenDtlsId(lLngSeenDtlId);
			 * lObjTrnPensionerSeenDtlsVO.setCreatedUserId(new
			 * BigDecimal(gLngUserId));
			 * lObjTrnPensionerSeenDtlsVO.setCreatedPostId(new
			 * BigDecimal(gLngPostId));
			 * lObjTrnPensionerSeenDtlsVO.setCreatedDate(gCurrDate);
			 * //lObjTrnPensionerSeenDtlsVO.setSeenFlag("N"); if
			 * (lArrStrPensionerId[lIntCnt] != null &&
			 * lArrStrPensionerId[lIntCnt].trim().length() > 0) {
			 * lObjTrnPensionerSeenDtlsVO
			 * .setPensionerCode(lArrStrPensionerId[lIntCnt]);
			 * 
			 * } if (lArrStrLifeCertFlag[lIntCnt] != null &&
			 * lArrStrLifeCertFlag[lIntCnt].trim().length() > 0) {
			 * lObjTrnPensionerSeenDtlsVO
			 * .setSeenFlag(lArrStrLifeCertFlag[lIntCnt]);
			 * 
			 * } if (lArrStrReceivedDate[lIntCnt] != null &&
			 * lArrStrReceivedDate[lIntCnt].trim().length() > 0) {
			 * lObjTrnPensionerSeenDtlsVO
			 * .setSeenDate(lObjSimpleDateFormat.parse(
			 * lArrStrReceivedDate[lIntCnt])); } if (lArrStrBundleNo[lIntCnt] !=
			 * null && lArrStrBundleNo[lIntCnt].trim().length() > 0) {
			 * lObjTrnPensionerSeenDtlsVO.setBundleNo(lArrStrBundleNo[lIntCnt]);
			 * }
			 * lObjTrnPensionerSeenDtlsVO.setLifeCertStatus(gObjRsrcBndle.getString
			 * ("LIFECERT.SAVED"));
			 * lObjTrnPensionerSeenDtlsVO.setFinYearId(Long.
			 * valueOf(SessionHelper.getFinYrId(inputMap)));
			 * lObjLifeCertificateDAO.create(lObjTrnPensionerSeenDtlsVO);
			 * 
			 * } }
			 */
			// resObj = getLifeCertificateList(inputMap);
			// resObj.setViewName("LifeCertificate");
			// resObj.setResultValue(inputMap);

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append("Updated Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");
			lStrBldXML = getResponseXMLDoc(inputMap);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
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
	 * com.tcs.sgv.pensionpay.service.LifeCertificateService#getReceivedLifeCertList
	 * (java.util.Map)
	 */
	public ResultObject getReceivedLifeCertList(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List lLstResult = new ArrayList();
		LifeCertificateVO lObjLifeCertificateVO = new LifeCertificateVO();
		List<LifeCertificateVO> lLstReceivedLifeCertificate = new ArrayList<LifeCertificateVO>();
		setSessionInfo(inputMap);

		Integer lIntTotalRecords = 0;

		LifeCertificateDAO lObjLifeCertificateDAO = new LifeCertificateDAOImpl(LifeCertificateVO.class, serv.getSessionFactory());
		try {

			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lIntTotalRecords = lObjLifeCertificateDAO.getReceivedLifeCertificateCount(gStrLocCode);
			if (lIntTotalRecords > 0) {
				lLstResult = lObjLifeCertificateDAO.getReceivedLifeCertificateList(displayTag, gStrLocCode);
			}

			if (!lLstResult.isEmpty()) {
				for (Integer lIntCnt = 0; lIntCnt < lLstResult.size(); lIntCnt++) {

					lObjLifeCertificateVO = new LifeCertificateVO();
					Object[] obj = (Object[]) lLstResult.get(lIntCnt);
					if (obj[0] != null) {
						lObjLifeCertificateVO.setPensionerCode(obj[0].toString());
					}
					if (obj[1] != null) {
						lObjLifeCertificateVO.setPpoNo(obj[1].toString());
					}
					if (obj[2] != null) {
						lObjLifeCertificateVO.setName(obj[2].toString());
					}
					if (obj[3] != null) {
						lObjLifeCertificateVO.setFamilyName(obj[3].toString());
					}
					if (obj[4] != null) {
						lObjLifeCertificateVO.setLedgerNo(obj[4].toString());
					}
					if (obj[5] != null) {
						lObjLifeCertificateVO.setPageNo(obj[5].toString());
					}
					if (obj[6] != null) {
						lObjLifeCertificateVO.setAliveFlag(obj[6].toString());
					}
					if (obj[7] != null) {
						lObjLifeCertificateVO.setBankName(obj[7].toString());
					}
					if (obj[8] != null) {
						lObjLifeCertificateVO.setBranchName(obj[8].toString());
					}
					if (obj[9] != null) {
						lObjLifeCertificateVO.setAccountNo(obj[9].toString());
					}
					if (obj[10] != null) {
						lObjLifeCertificateVO.setReceivedDate(lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(obj[10])));
					}
					if (obj[11] != null) {
						lObjLifeCertificateVO.setLifeCertFlag(obj[11].toString());
					}
					lLstReceivedLifeCertificate.add(lObjLifeCertificateVO);
				}
			}
			if (lLstReceivedLifeCertificate != null && lLstReceivedLifeCertificate.size() > 0) {
				inputMap.put("ReceivedLifeCertificateList", lLstReceivedLifeCertificate);
			}
			inputMap.put("totalRecords", lIntTotalRecords);

			resObj.setResultValue(inputMap);

			resObj.setViewName("ReceivedLifeCertificateList");

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
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
	 * com.tcs.sgv.pensionpay.service.LifeCertificateService#approveLifeCertificates
	 * (java.util.Map)
	 */
	public ResultObject approveLifeCertificates(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			StringBuilder lStrBldXML = new StringBuilder();
			setSessionInfo(inputMap);
			String lStrPnsnrCodeLifeCertFlag = null;
			List<String> lLstPensionerCode = new ArrayList<String>();
			LifeCertificateDAO lObjLifeCertificateDAO = new LifeCertificateDAOImpl(LifeCertificateVO.class, serv.getSessionFactory());
			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = new TrnPensionRqstHdr();
			String lStrPensionerCode = null;
			String lStrLifeCertFlag = null;
			Long lLngFinYearId = null;
			lStrPnsnrCodeLifeCertFlag = StringUtility.getParameter("pnsnrCodeLifeCertFlag", request);
			if (!"".equals(lStrPnsnrCodeLifeCertFlag)) {

				String[] lArrStrPnsnrCodeLifeCertFlag = lStrPnsnrCodeLifeCertFlag.split("~");

				for (Integer lIntCnt = 0; lIntCnt < lArrStrPnsnrCodeLifeCertFlag.length; lIntCnt++) {
					if (lArrStrPnsnrCodeLifeCertFlag[lIntCnt] != null && !lArrStrPnsnrCodeLifeCertFlag[lIntCnt].equals("")) {
						lObjTrnPensionRqstHdrVO = new TrnPensionRqstHdr();

						lStrPensionerCode = lArrStrPnsnrCodeLifeCertFlag[lIntCnt].substring(0, lArrStrPnsnrCodeLifeCertFlag[lIntCnt].indexOf("*"));
						lLstPensionerCode.add(lStrPensionerCode);
						lObjTrnPensionRqstHdrVO = lObjPhysicalCaseInwardDAO.getTrnPensionRqstHdrVO(lStrPensionerCode);
						lStrLifeCertFlag = lArrStrPnsnrCodeLifeCertFlag[lIntCnt].substring(lArrStrPnsnrCodeLifeCertFlag[lIntCnt].indexOf("*") + 1);
						lObjTrnPensionRqstHdrVO.setSeenFlag(lStrLifeCertFlag);
						lObjPhysicalCaseInwardDAO.update(lObjTrnPensionRqstHdrVO);
					}
				}
			}
			lLngFinYearId = Long.valueOf(SessionHelper.getFinYrId(inputMap));
			if (!lLstPensionerCode.isEmpty()) {
				lObjLifeCertificateDAO.updatePensionerSeenDtls(lLstPensionerCode, gLngPostId, gLngUserId, gCurrDate, lLngFinYearId);
			}
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append("Approved Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
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
	 * com.tcs.sgv.pensionpay.service.LifeCertificateService#saveArrearPaymentDtls
	 * (java.util.Map)
	 */
	public ResultObject saveArrearPaymentDtls(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		StringBuilder lStrBldXML = new StringBuilder();

		String lStrPensionRqstId = null;
		String lStrPensionerCode = null;
		String lStrPayInMonth = null;
		String lStrPayInYear = null;
		String lStrArrearAmt = null;
		String lStrPayInMonthYear = "";
		Long lLngArrearDtlsId = null;

		try {
			setSessionInfo(inputMap);
			TrnPensionArrearDtls lObjTrnPensionArrearDtls = new TrnPensionArrearDtls();
			LifeCertificateDAO lObjLifeCertificateDAO = new LifeCertificateDAOImpl(TrnPensionArrearDtls.class, serv.getSessionFactory());

			lStrPensionerCode = StringUtility.getParameter("PensionerCode", request);
			lStrPensionRqstId = StringUtility.getParameter("PensionRqstId", request);
			lStrPayInMonth = StringUtility.getParameter("PayInMonth", request);
			lStrPayInYear = StringUtility.getParameter("PayInYear", request);
			lStrArrearAmt = StringUtility.getParameter("ArrearAmt", request);

			String[] lArrStrPensionerCode = lStrPensionerCode.split("~");
			String[] lArrStrPensionRqstId = lStrPensionRqstId.split("~");
			String[] lArrStrPayInMonth = lStrPayInMonth.split("~");
			String[] lArrStrPayInYear = lStrPayInYear.split("~");
			String[] lArrStrArrearAmt = lStrArrearAmt.split("~");

			for (int lIntCnt = 0; lIntCnt < lArrStrPensionerCode.length; lIntCnt++) {
				lObjTrnPensionArrearDtls = lObjLifeCertificateDAO.getTrnPensionArrearDtls(lArrStrPensionerCode[lIntCnt]);

				if (!"".equals(lArrStrPayInMonth[lIntCnt]) && !"".equals(lArrStrPayInYear[lIntCnt])) {
					if (Integer.parseInt(lArrStrPayInMonth[lIntCnt]) < 10) {
						lStrPayInMonthYear = lArrStrPayInYear[lIntCnt].trim() + "0" + lArrStrPayInMonth[lIntCnt].trim();
					} else {
						lStrPayInMonthYear = lArrStrPayInYear[lIntCnt] + lArrStrPayInMonth[lIntCnt].trim();
					}
				}

				lObjTrnPensionArrearDtls.setPensionerCode(lArrStrPensionerCode[lIntCnt]);
				lObjTrnPensionArrearDtls.setPensionRequestId(Long.parseLong(lArrStrPensionRqstId[lIntCnt]));
				lObjTrnPensionArrearDtls.setArrearFieldType(gObjRsrcBndle.getString("ARREARTYPE.LC"));
				if (!"".equals(lStrPayInMonthYear)) {
					lObjTrnPensionArrearDtls.setPaymentFromYyyymm(Integer.parseInt(lStrPayInMonthYear));
					lObjTrnPensionArrearDtls.setPaymentToYyyymm(Integer.parseInt(lStrPayInMonthYear));
				}
				if (!"".equals(lStrArrearAmt))
					lObjTrnPensionArrearDtls.setTotalDifferenceAmt(new BigDecimal(lArrStrArrearAmt[lIntCnt]));
				lObjTrnPensionArrearDtls.setPaidFlag('N');
				if (lObjTrnPensionArrearDtls.getPensionArrearDtlsId() == null) {
					lLngArrearDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_arrear_dtls", inputMap);
					lObjTrnPensionArrearDtls.setPensionArrearDtlsId(lLngArrearDtlsId);
					lObjTrnPensionArrearDtls.setCreatedDate(gCurrDate);
					lObjTrnPensionArrearDtls.setCreatedPostId(new BigDecimal(gLngPostId));
					lObjTrnPensionArrearDtls.setCreatedUserId(new BigDecimal(gLngUserId));

					lObjLifeCertificateDAO.create(lObjTrnPensionArrearDtls);
				} else {
					lObjTrnPensionArrearDtls.setUpdatedDate(gCurrDate);
					lObjTrnPensionArrearDtls.setUpdatedPostId(new BigDecimal(gLngPostId));
					lObjTrnPensionArrearDtls.setUpdatedUserId(new BigDecimal(gLngUserId));
					lObjLifeCertificateDAO.update(lObjTrnPensionArrearDtls);
				}
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append("Arrear Payment Details Saved Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error("Error is :" + e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

}
