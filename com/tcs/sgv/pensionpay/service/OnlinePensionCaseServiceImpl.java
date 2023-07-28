/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

//com.tcs.sgv.pensionpay.service.OnlinePensionCaseServiceImpl
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.MstPensionerDtlsDAO;
import com.tcs.sgv.pensionpay.dao.MstPensionerDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.MstPnsnpmntSlotsDAO;
import com.tcs.sgv.pensionpay.dao.MstPnsnpmntSlotsDAOImpl;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAO;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPnsnpmntSlots;
import com.tcs.sgv.pensionpay.valueobject.SavedCasesVO;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Mar 25, 2011
 */
public class OnlinePensionCaseServiceImpl extends ServiceImpl implements OnlinePensionCaseService {

	private Long gLngPostId = null;

	private Long gLngUserId = null;

	private Long gLngLangId = null;

	private Log gLogger = LogFactory.getLog(getClass());

	private Date gCurrDate = null;

	private String gStrLocCode = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public ResultObject loadOnlinePensionCasesFromAG(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		List<SavedCasesVO> lLstSavedCaseVO = new ArrayList<SavedCasesVO>();
		List<ComboValuesVO> lLstBanks = new ArrayList();
		List<ComboValuesVO> lLstAuditorDtls = new ArrayList();
		String lStrToRole = null;
		String lStrShowCaseFor = null;
		String lStrSearchCrt = null;
		String lStrSearchVal = null;
		String lStrElementCode = null;
		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
		MstPnsnpmntSlotsDAO lObjMstPnsnpmntSlotsDAO = new MstPnsnpmntSlotsDAOImpl(MstPnsnpmntSlots.class, serv.getSessionFactory());
		Map<Integer, MstPnsnpmntSlots> lMapSlots = new HashMap<Integer, MstPnsnpmntSlots>();
		List<MstPnsnpmntSlots> lLstSlotDtls = null;
		List<String> caseStatusList = new ArrayList();
		int totalRecords = 0;
		String lStrViewName = "";
		Integer lIntMaxSlotNo = null;
		try {
			// lStrToRole = lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			lStrShowCaseFor = StringUtility.getParameter("showCaseFor", request);
			lStrSearchCrt = StringUtility.getParameter("lStrSearchCrt", request);
			lStrSearchVal = StringUtility.getParameter("lStrSearchVal", request).trim();
			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			lStrToRole = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);

			// -----Setting of case status for current screen starts <<<<<
			if (bundleConst.getString("CASEFOR.RCVONL").equals(lStrShowCaseFor)) {
				caseStatusList.add(bundleConst.getString("STATFLG.NEW"));
			} else if (bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor)) {
				caseStatusList.add(bundleConst.getString("STATFLG.REGISTERED"));
			} else if (bundleConst.getString("CASEFOR.FIRSTPENSION").equals(lStrShowCaseFor)) {
				caseStatusList.add(bundleConst.getString("STATFLG.IDENTIFIED"));
			} else if (bundleConst.getString("CASEFOR.RECORDROOM").equals(lStrShowCaseFor)) {
				caseStatusList.add(bundleConst.getString("STATFLG.APPROVED"));
				caseStatusList.add(bundleConst.getString("STATFLG.REJECTED"));
				caseStatusList.add(bundleConst.getString("STATFLG.REVISED"));
				caseStatusList.add(bundleConst.getString("STATFLG.MODIFIEDBYAUDITOR"));
			} else if (bundleConst.getString("CASEFOR.MODIFICATIONCASE").equals(lStrShowCaseFor)) {
				caseStatusList.add(bundleConst.getString("STATFLG.MODIFIED"));
			} else if (bundleConst.getString("CASEFOR.DRAFTCASE").equals(lStrShowCaseFor)) {
				caseStatusList.add(bundleConst.getString("STATFLG.INWARDNEW"));
			} else if (bundleConst.getString("CASEFOR.VIEWNEWCASE").equals(lStrShowCaseFor)) {
				caseStatusList.add(bundleConst.getString("STATFLG.INWARDAPPROVAL"));
			}
			// -----Setting of case status for current screen ends >>>>>>
			totalRecords = lObjOnlinePensionCaseDAO.getPensionerDetailsCount(lStrShowCaseFor, inputMap, gLngPostId, lStrToRole, caseStatusList, gStrLocCode, lStrSearchCrt, lStrSearchVal);
			if (totalRecords > 0) {
				lLstSavedCaseVO = lObjOnlinePensionCaseDAO
						.getPensionerDetails(lStrShowCaseFor, inputMap, gLngPostId, lStrToRole, caseStatusList, gStrLocCode, displayTag, lStrSearchCrt, lStrSearchVal);
			}

			lLstBanks = lObjOnlinePensionCaseDAO.getBankNames(inputMap, gLngLangId);

			lLstAuditorDtls = lObjOnlinePensionCaseDAO.getAuditorsListFromRoleID(gStrLocCode, gLngLangId);

			// ----Preparing list of master scheduler objects starts <<<<<
			if (bundleConst.getString("CASEFOR.IDENTIFICATION").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor)) {
				lMapSlots = lObjMstPnsnpmntSlotsDAO.getMaxSlotNumber();
				Object[] lArrSlotNos = lMapSlots.keySet().toArray();
				lLstSlotDtls = new ArrayList<MstPnsnpmntSlots>();
				lIntMaxSlotNo = (Integer) lArrSlotNos[(lArrSlotNos.length - 1)];
				for (int slotIndex = 1; slotIndex <= lIntMaxSlotNo.intValue(); slotIndex++) {
					lLstSlotDtls.add(lMapSlots.get(slotIndex));
				}
				inputMap.put("lLstSlotDtls", lLstSlotDtls);
			}
			// -----Preparing list of master scheduler objects ends >>>>>>>

			inputMap.put("lLstSavedCaseVO", lLstSavedCaseVO);
			inputMap.put("lStrToRole", lStrToRole);
			inputMap.put("BankList", lLstBanks);
			inputMap.put("lLstAuditorDtls", lLstAuditorDtls);
			inputMap.put("totalRecords", totalRecords);
			inputMap.put("showCaseFor", lStrShowCaseFor);
			inputMap.put("lStrSearchCrt", lStrSearchCrt);
			inputMap.put("lStrSearchVal", lStrSearchVal);
			inputMap.put("elementCode", lStrElementCode);
			resObj.setResultValue(inputMap);

			if (bundleConst.getString("CASEFOR.DRAFTCASE").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.VIEWNEWCASE").equals(lStrShowCaseFor)) {
				lStrViewName = "draftPnsnCases";
			} else if (bundleConst.getString("CASEFOR.FIRSTPENSION").equals(lStrShowCaseFor)) {
				lStrViewName = "FirstPayBill";
			} else if (bundleConst.getString("CASEFOR.RECORDROOM").equals(lStrShowCaseFor) || bundleConst.getString("CASEFOR.MODIFICATIONCASE").equals(lStrShowCaseFor)) {
				lStrViewName = "RecordRoom";
			}/*
			 * else if
			 * (bundleConst.getString("CASEFOR.IDENTAUDITOR").equals(lStrShowCaseFor
			 * )) { lStrViewName = "IdentificationSchedule"; }
			 */else {
				lStrViewName = "ReceiveOLCases";
			}
			resObj.setViewName(lStrViewName);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//e.printStackTrace();
		}

		return resObj;
	}

	public ResultObject genRegNoSaveBnkDtls(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String[] lArrPpoNo = null;
		String lSBStatus = null;
		String lStrRegistrationNo = null;
		String lStrPensionerDtlId = null;
		String lStrPensionerId = null;
		List<String> lLstRegNo = new ArrayList<String>();
		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;
		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
		MstPensionerDtlsDAO lObjMstPensionerDtlsDAO = new MstPensionerDtlsDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
		SimpleDateFormat lObjSdf = new SimpleDateFormat("dd/MM/yyyy");
		String lStrBankBranchCode = null;
		String lStrPnsnRqstId = null;
		try {
			MstPensionerDtls lObjMstPensionerDtls = new MstPensionerDtls();

			String lStrCurrentDate = lObjSdf.format(gCurrDate);
			lArrPpoNo = request.getParameterValues("chkbxPesnionerNo");
			lStrPensionerDtlId = StringUtility.getParameter("pensionerDtlId", request);
			lStrPensionerId = StringUtility.getParameter("pensionerId", request);

			String[] lArrPensionerDtlId = lStrPensionerDtlId.split("~");
			String[] lArrPensionerId = lStrPensionerId.split("~");
			String[] lArrRowNums = new String[(lArrPpoNo.length)];

			for (int i = 0; i < (lArrPpoNo.length); i++) {
				String lStrPpoNo = lArrPpoNo[i];
				lArrRowNums[i] = lStrPpoNo.substring(lStrPpoNo.lastIndexOf("_") + 1).toString();
				lStrPnsnRqstId = StringUtility.getParameter("hdnpnsnrqstid" + lArrRowNums[i], request).trim();
				lStrRegistrationNo = generateRegNo(inputMap);
				lLstRegNo.add(lStrRegistrationNo);

				lObjMstPensionerDtls = (MstPensionerDtls) lObjMstPensionerDtlsDAO.read(Long.parseLong(lArrPensionerDtlId[i]));
				lObjMstPensionerDtls.setRegistrationNo(lStrRegistrationNo);
				lObjMstPensionerDtls.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjMstPensionerDtls.setUpdatedUserId(new BigDecimal(gLngUserId));
				lObjMstPensionerDtls.setUpdatedDate(gCurrDate);
				lObjMstPensionerDtlsDAO.update(lObjMstPensionerDtls);

				lObjTrnPensionRqstHdr = lObjTrnPensionRqstHdrDAO.read(Long.valueOf(lStrPnsnRqstId));
				lObjTrnPensionRqstHdr.setPpoRegDate(gCurrDate);
				lObjTrnPensionRqstHdr.setUpdatedDate(gCurrDate);
				lObjTrnPensionRqstHdr.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjTrnPensionRqstHdr.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjTrnPensionRqstHdrDAO.update(lObjTrnPensionRqstHdr);

			}
			lSBStatus = getResponseXMLDocForRegNo(lLstRegNo, lArrRowNums, lStrCurrentDate).toString();

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//e.printStackTrace();
		}
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");

		return resObj;
	}

	public ResultObject savePnsnrBankBranchDtls(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String[] lArrPpoNo = null;
		String lSBStatus = null;
		String lStrRegistrationNo = null;
		String lStrPensionerDtlId = null;

		MstPensionerDtlsDAO lObjMstPensionerDtlsDAO = new MstPensionerDtlsDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
		String lStrBankCode = null;
		String lStrBankBranchCode = null;
		String lStrAccountNo = null;
		String lStrAudiPostId = null;
		String lStrPnsnRqstId = null;
		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;
		try {
			MstPensionerDtls lObjMstPensionerDtls = new MstPensionerDtls();

			lArrPpoNo = request.getParameterValues("chkbxPesnionerNo");
			lStrPensionerDtlId = StringUtility.getParameter("pensionerDtlId", request);

			String[] lArrPensionerDtlId = lStrPensionerDtlId.split("~");
			String[] lArrRowNums = new String[lArrPpoNo.length];

			for (int i = 0; i < lArrPpoNo.length; i++) {
				lObjMstPensionerDtls = new MstPensionerDtls();
				lObjMstPensionerDtls = (MstPensionerDtls) lObjMstPensionerDtlsDAO.read(Long.parseLong(lArrPensionerDtlId[i]));

				String lStrPpoNo = lArrPpoNo[i];
				lArrRowNums[i] = lStrPpoNo.substring(lStrPpoNo.lastIndexOf("_") + 1).toString();

				lStrRegistrationNo = ((StringUtility.getParameter("txtRegNo" + lArrRowNums[i], request).trim()).length() > 0)
						? StringUtility.getParameter("txtRegNo" + lArrRowNums[i], request).trim()
						: null;
				lObjMstPensionerDtls.setRegistrationNo(lStrRegistrationNo);

				if (!StringUtility.getParameter("cmbBankName" + lArrRowNums[i], request).equals("-1") && StringUtility.getParameter("cmbBankName" + lArrRowNums[i], request) != null) {
					lStrBankCode = StringUtility.getParameter("cmbBankName" + lArrRowNums[i], request).trim();
					lObjMstPensionerDtls.setBankCode(lStrBankCode);
				}
				if (!StringUtility.getParameter("cmbBankBrnchName" + lArrRowNums[i], request).equals("-1") && StringUtility.getParameter("cmbBankBrnchName" + lArrRowNums[i], request) != null) {
					lStrBankBranchCode = StringUtility.getParameter("cmbBankBrnchName" + lArrRowNums[i], request).trim();
					lObjMstPensionerDtls.setBranchCode(lStrBankBranchCode);
				}

				lStrAccountNo = ((StringUtility.getParameter("txtAccountNo" + lArrRowNums[i], request).trim()).length() > 0) ? StringUtility.getParameter("txtAccountNo" + lArrRowNums[i], request)
						.trim() : null;

				lObjMstPensionerDtls.setAccountNo(lStrAccountNo);
				lObjMstPensionerDtls.setUpdatedPostId(new BigDecimal(gLngPostId));
				lObjMstPensionerDtls.setUpdatedUserId(new BigDecimal(gLngUserId));
				lObjMstPensionerDtls.setUpdatedDate(gCurrDate);
				lObjMstPensionerDtlsDAO.update(lObjMstPensionerDtls);

				/*
				 * To save case_owner as auditor post id in trn_pension_rqst_hdr
				 */
				lStrPnsnRqstId = StringUtility.getParameter("hdnpnsnrqstid" + lArrRowNums[i], request).trim();
				lStrAudiPostId = StringUtility.getParameter("hdnAuditorPostId" + lArrRowNums[i], request).trim();
				if (lStrPnsnRqstId != null && !lStrPnsnRqstId.equals("")) {
					lObjTrnPensionRqstHdr = lObjTrnPensionRqstHdrDAO.read(Long.valueOf(lStrPnsnRqstId));
					if (lStrAudiPostId != null && !lStrAudiPostId.equals("")) {
						Long lLngAudiPostId = Long.valueOf(lStrAudiPostId);
						lObjTrnPensionRqstHdr.setCaseOwner(BigDecimal.valueOf(lLngAudiPostId));
					}

					lObjTrnPensionRqstHdr.setUpdatedDate(gCurrDate);
					lObjTrnPensionRqstHdr.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionRqstHdr.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjTrnPensionRqstHdrDAO.update(lObjTrnPensionRqstHdr);

				}

			}
			lSBStatus = getResponseXMLDocForSaveBankDtls(inputMap).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	private StringBuilder getResponseXMLDocForSaveBankDtls(Map<String, Object> inputMap) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <UPDATE>");
		lStrBldXML.append("Saved Successfully.");
		lStrBldXML.append("  </UPDATE>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private String generateRegNo(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());
		String lStrRegistrationNo = null;
		new Date();

		Calendar cal = Calendar.getInstance();

		try {
			String lStrSerialNo = IDGenerateDelegate.getNextIdWODbidLocationId("pen_reg_no", inputMap);
			Integer lIntYear = cal.get(Calendar.YEAR);
			Integer lIntMonth = cal.get(Calendar.MONTH) + 1;
			Integer lIntDay = cal.get(Calendar.DATE);
			String lStrMonth = null;
			String lStrDay = null;
			lStrRegistrationNo = lObjOnlinePensionCaseDAO.getLocationShortNameFromCode(gStrLocCode) + "/" + lIntYear.toString();
			if (lIntMonth < 10) {
				lStrMonth = "0" + lIntMonth.toString();
			} else {
				lStrMonth = lIntMonth.toString();
			}
			if (lIntDay < 10) {
				lStrDay = "0" + lIntDay.toString();
			} else {
				lStrDay = lIntDay.toString();
			}
			lStrRegistrationNo = lStrRegistrationNo + lStrMonth + lStrDay + "/" + lStrSerialNo;

		} catch (Exception e) {

			//e.printStackTrace();
		}
		return lStrRegistrationNo;
	}

	public ResultObject getBranchNamesFromBankCode(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());

		List lLstBranches = new ArrayList();
		try {

			String lStrBankCode = StringUtility.getParameter("bankCode", request);

			Long.valueOf(lStrBankCode);

			if (lStrBankCode != null && lStrBankCode.length() > 0) {
				lLstBranches = lObjOnlinePensionCaseDAO.getBranchsOfBank(lStrBankCode, gLngLangId, gStrLocCode);

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

	public ResultObject getAudiNameFrmBrchCdUsingAjax(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());
		List lLstAuditors = new ArrayList();
		Iterator itr;

		String lStrBranchCode;

		String auditorName = "";
		Long auditorPostId = null;
		String lStrLocCode = null;
		try {
			lStrBranchCode = StringUtility.getParameter("branchCode", request);
			
			if (StringUtility.getParameter("locCode", request).toString() != null && StringUtility.getParameter("locCode", request).toString().trim().length() > 0 && !"-1".equals(StringUtility.getParameter("locCode", request).toString())) {
				lStrLocCode = StringUtility.getParameter("locCode", request).toString();	
			}else{
				lStrLocCode = gStrLocCode;	
			}
			
			lLstAuditors = lObjOnlinePensionCaseDAO.getAuditorName(inputMap, lStrLocCode, lStrBranchCode);
			itr = lLstAuditors.iterator();
			while (itr.hasNext()) {
				Object[] lObj = (Object[]) itr.next();
				auditorName = lObj[1].toString();
				auditorPostId = Long.parseLong(lObj[0].toString());
			}

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		String lSBStatus = getResponseXMLDoc(auditorName, auditorPostId).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	private StringBuilder getResponseXMLDoc(String auditorName, Long auditorPostID) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<AuditorName>");
		lStrBldXML.append(auditorName);
		lStrBldXML.append("</AuditorName>");
		lStrBldXML.append("<AuditorPostID>");
		lStrBldXML.append((auditorPostID != null) ? auditorPostID : "");
		lStrBldXML.append("</AuditorPostID>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
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

	private StringBuilder getResponseXMLDocForRegNo(List<String> lLstRegNo, String[] lArrRowNums, String lStrCurrentDate) {

		StringBuilder lStrBldXML = new StringBuilder();
		Iterator itr = lLstRegNo.iterator();

		lStrBldXML.append("<XMLDOC>");
		while (itr.hasNext()) {
			lStrBldXML.append("<RegNO>");
			lStrBldXML.append(itr.next().toString());
			lStrBldXML.append("</RegNO>");
		}
		for (String s : lArrRowNums) {
			lStrBldXML.append("<RowNo>");
			lStrBldXML.append(s);
			lStrBldXML.append("</RowNo>");
		}
		lStrBldXML.append("<RegDate>");
		lStrBldXML.append(lStrCurrentDate);
		lStrBldXML.append("</RegDate>");
		/*
		 * lStrBldXML.append("<Date>"); lStrBldXML.append(lStrCurrentDate);
		 * lStrBldXML.append("</Date>");
		 */
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private void setSessionInfo(Map<String, Object> inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocCode = SessionHelper.getLocationCode(inputMap);
		gLngLangId = SessionHelper.getLangId(inputMap);
		gCurrDate = DBUtility.getCurrentDateFromDB();
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	public ResultObject getBnkBrnchAudiNmeFrmBnkCode(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);

		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());

		List resultList = new ArrayList();
		try {

			String lStrBranchCode = StringUtility.getParameter("branchCode", request);

			Long lLngBranchCode = Long.valueOf(lStrBranchCode);

			if (lLngBranchCode != null && lLngBranchCode > 0) {
				resultList = lObjOnlinePensionCaseDAO.getBnkBrnchAudiNmeFrmBnkCode(inputMap, gStrLocCode, lLngBranchCode);

			}

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		String lSBStatus = getResponseXMLDocFromBankCode(resultList).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");

		return resObj;
	}

	private StringBuilder getResponseXMLDocFromBankCode(List resultList) {

		StringBuilder lStrBldXML = new StringBuilder();
		Iterator itr = resultList.iterator();
		lStrBldXML.append("<XMLDOC>");
		while (itr.hasNext()) {
			Object[] obj = (Object[]) itr.next();
			lStrBldXML.append("<BranchName>");
			lStrBldXML.append(obj[0].toString());
			lStrBldXML.append("</BranchName>");
			lStrBldXML.append("<BankCode>");
			lStrBldXML.append(obj[1].toString());
			lStrBldXML.append("</BankCode>");
			lStrBldXML.append("<BankName>");
			lStrBldXML.append(obj[2].toString());
			lStrBldXML.append("</BankName>");
			lStrBldXML.append("<AudiName>");
			lStrBldXML.append(obj[3].toString());
			lStrBldXML.append("</AudiName>");
			lStrBldXML.append("<AudiPostId>");
			lStrBldXML.append(obj[4].toString());
			lStrBldXML.append("</AudiPostId>");
		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	public ResultObject forwardPensionCase(Map<String, Object> inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrShowCaseFor = null;
		String lStrApprovalStatus = null;
		String caseStatusTo = null;
		TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = null;
		MstPensionerDtlsDAO lObjMstPensionerDtlsDAO = null;
		TrnPensionRqstHdr lObjTrnPensionRqstHdr = null;
		MstPensionerDtls lObjMstPensionerDtls = null;
		BigDecimal lBgDcmlCaseOwner = BigDecimal.ZERO;
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		int lIntCntCases = 0;
		String lStrFrwdToRole = "";
		String lStrBankCode = "";
		String lStrBranchCode = "";
		String lStrLibraryNo = "";
		try {
			setSessionInfo(inputMap);
			OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());
			CommonPensionService lObjCommonPensionService = new CommonPensionServiceImpl();
			String lStrPnsnRqstId = StringUtility.getParameter("pnsnRqstId", request);
			String lStrPensionerDtlId = StringUtility.getParameter("pensionerDtlId", request);
			lStrShowCaseFor = StringUtility.getParameter("showCaseFor", request);// ----lStrShowCaseFor
			// represents
			// current
			// worklist
			// screen
			lStrApprovalStatus = StringUtility.getParameter("approvalStatus", request).trim();
			if (bundleConst.getString("CASEFOR.RCVONL").equals(lStrShowCaseFor)) {
				caseStatusTo = bundleConst.getString("STATFLG.REGISTERED");
				lStrFrwdToRole = "Pension Clerk";
			} else if (bundleConst.getString("CASEFOR.FIRSTPENSION").equals(lStrShowCaseFor)) {
				caseStatusTo = bundleConst.getString("STATFLG.APPROVED");
				lStrFrwdToRole = "Pension Auditor";
			} else if (bundleConst.getString("CASEFOR.RECORDROOM").equals(lStrShowCaseFor)) {
				caseStatusTo = bundleConst.getString("STATFLG.MODIFIED");
				lStrFrwdToRole = "Pension ATO";
			} else if (bundleConst.getString("CASEFOR.MODIFICATIONCASE").equals(lStrShowCaseFor)) {
				if (lStrApprovalStatus != null && !lStrApprovalStatus.equals("")) {
					caseStatusTo = lStrApprovalStatus;
					lStrFrwdToRole = "Pension Auditor";
				} else {
					caseStatusTo = bundleConst.getString("STATFLG.MODIFIED");
				}
			} else if (bundleConst.getString("CASEFOR.DRAFTCASE").equals(lStrShowCaseFor)) {
				caseStatusTo = bundleConst.getString("STATFLG.INWARDAPPROVAL");
				lStrFrwdToRole = "Pension ATO";
			} else if (bundleConst.getString("CASEFOR.VIEWNEWCASE").equals(lStrShowCaseFor)) {
				caseStatusTo = bundleConst.getString("STATFLG.NEW");
				lStrFrwdToRole = "Inward Clerk";
			}
			String[] lArrPnsnRqstId = lStrPnsnRqstId.split("~");
			String[] lArrPensionerDtlId = lStrPensionerDtlId.split("~");
			lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			lObjMstPensionerDtlsDAO = new MstPensionerDtlsDAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
			for (Integer lIntCnt = 0; lIntCnt < lArrPnsnRqstId.length; lIntCnt++) {
				lBgDcmlCaseOwner = null;
				if (lArrPensionerDtlId[lIntCnt] != null && !lArrPensionerDtlId[lIntCnt].equals("")) {
					lObjMstPensionerDtls = (MstPensionerDtls) lObjMstPensionerDtlsDAO.read(Long.valueOf(lArrPensionerDtlId[lIntCnt]));
					lStrBranchCode = lObjMstPensionerDtls.getBranchCode();
					lStrBankCode = lObjMstPensionerDtls.getBankCode();
					List lLstResult = lObjOnlinePensionCaseDAO.getAuditorName(inputMap, gStrLocCode, lStrBranchCode);
					if (lLstResult != null && !lLstResult.isEmpty() && lLstResult.get(0) != null) {
						Object[] obj = (Object[]) lLstResult.get(0);
						if (obj[0] != null) {
							lBgDcmlCaseOwner = new BigDecimal(obj[0].toString());
						}
					}

					lObjMstPensionerDtls.setCaseStatus(caseStatusTo);
					lObjMstPensionerDtls.setUpdatedDate(gCurrDate);
					lObjMstPensionerDtls.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjMstPensionerDtls.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjMstPensionerDtlsDAO.update(lObjMstPensionerDtls);
				}
				if (lArrPnsnRqstId[lIntCnt] != null && !lArrPnsnRqstId[lIntCnt].equals("")) {
					lObjTrnPensionRqstHdr = lObjTrnPensionRqstHdrDAO.read(Long.valueOf(lArrPnsnRqstId[lIntCnt]));
					lObjTrnPensionRqstHdr.setCaseStatus(caseStatusTo);
					if (lBgDcmlCaseOwner != null) {
						lObjTrnPensionRqstHdr.setCaseOwner(lBgDcmlCaseOwner);
					}
					lObjTrnPensionRqstHdr.setUpdatedDate(gCurrDate);
					lObjTrnPensionRqstHdr.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionRqstHdr.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjTrnPensionRqstHdrDAO.update(lObjTrnPensionRqstHdr);
				}
				if (bundleConst.getString("CASEFOR.FIRSTPENSION").equals(lStrShowCaseFor)) {
					inputMap.put("pensionerCode", lObjTrnPensionRqstHdr.getPensionerCode());
					lStrLibraryNo = lObjCommonPensionService.generateLibraryNo(inputMap);
				}	
				lIntCntCases++;
			}

			String lSBStatus = getResponseXMLDocForForward(lIntCntCases, lArrPnsnRqstId.length, lStrShowCaseFor, caseStatusTo, lStrFrwdToRole,lStrLibraryNo).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			//ex.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;

	}

	private StringBuilder getResponseXMLDocForForward(int lIntCntCases, int lIntPnsnRqstId, String lStrShowCaseFor, String lStrCaseStatusTo, String lStrFrwdToRole,String lStrLibraryNo) {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOC>");
		// ---Checking selected cases are forwarded or not..
		if (lIntCntCases == lIntPnsnRqstId) {
			lStrBldXML.append("<MESSAGE>");
			if (lStrShowCaseFor.equals(bundleConst.getString("CASEFOR.VIEWNEWCASE"))) {
				lStrBldXML.append("Cases are approved successfully and forwarded to " + lStrFrwdToRole);
			} else if (lStrShowCaseFor.equals(bundleConst.getString("CASEFOR.MODIFICATIONCASE"))) {
				lStrBldXML.append("Cases are " + lStrCaseStatusTo + " successfully and forwarded to " + lStrFrwdToRole);
			} else if (bundleConst.getString("CASEFOR.FIRSTPENSION").equals(lStrShowCaseFor)) {
				lStrBldXML.append("Pension case is moved to Library successfully. Library No is "+lStrLibraryNo);
			} else {
				lStrBldXML.append("Cases are forwarded to " + lStrFrwdToRole + " successfully");
			}
			lStrBldXML.append("</MESSAGE>");
		}
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	
	public ResultObject validatePPOBeforeArchive(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrPPONo = null;
		List lLstResult = new ArrayList();
		List lLstBillDtls = new ArrayList();
		String lStrPensionerCode = "";
		String lStrPensionerName = "";
		StringBuilder lStrBldXML = new StringBuilder();
		try {
			setSessionInfo(inputMap);
			OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());
			
			lStrPPONo = StringUtility.getParameter("PpoNo", request);
			if (!"".equals(lStrPPONo)) {
				lLstResult = lObjOnlinePensionCaseDAO.validatePPOBeforeArchive(lStrPPONo.toUpperCase(), gStrLocCode);
				if(!lLstResult.isEmpty())
				{
					Object[] obj=(Object[])lLstResult.get(0);
					lStrPensionerCode = obj[0].toString();
					lStrPensionerName = obj[2].toString();
					
					lLstBillDtls = lObjOnlinePensionCaseDAO.validatePPOBeforeArchiveWithBill(lStrPensionerCode);
				}
			}
			
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<ISEXISTS>");
			if(lLstResult.isEmpty() || (!lLstResult.isEmpty() && !lLstBillDtls.isEmpty())) {
				lStrBldXML.append("N");
				lStrBldXML.append("</ISEXISTS>");
			} else {
				lStrBldXML.append("Y");
				lStrBldXML.append("</ISEXISTS>");
				lStrBldXML.append("<PNSNRCODE>"+lStrPensionerCode);
				lStrBldXML.append("</PNSNRCODE>");
				lStrBldXML.append("<PNSNRNAME><![CDATA[" + lStrPensionerName);
				lStrBldXML.append("]]></PNSNRNAME>");
			}
						
			lStrBldXML.append("</XMLDOC>");

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
	
	public ResultObject sendCaseBackToPensionClerk(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrPensionerCode = "";
		StringBuilder lStrBldXML = new StringBuilder();
		
		try {
			setSessionInfo(inputMap);
			OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(null, serv.getSessionFactory());
			
			lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
			if (!"".equals(lStrPensionerCode)) {
				 lObjOnlinePensionCaseDAO.updateCaseStatusAndSendBackToClerk(lStrPensionerCode,gStrLocCode,new BigDecimal(gLngPostId),new BigDecimal(gLngUserId),gCurrDate);
				
			}
			
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append("PPO send back to pension clerk successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

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
	
}
