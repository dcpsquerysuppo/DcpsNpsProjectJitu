/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 27, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.log4j.Logger;
import org.jfree.util.StringUtils;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.idgenerator.delegate.IDGenerateDelegate;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DateUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAO;
import com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAO;
import com.tcs.sgv.pensionpay.dao.PensionConfigDAOImpl;
import com.tcs.sgv.pensionpay.dao.TransferPPODAO;
import com.tcs.sgv.pensionpay.dao.TransferPPODAOImpl;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntPpoNo;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerNomineeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnCvpRestorationDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionTransferDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalVoucherDtls;
import com.tcs.sgv.pensionproc.service.PensionCaseServiceImpl;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 27, 2011
 */
public class TransferPPOServiceImpl extends ServiceImpl implements TransferPPOService {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private Date gDtCurDate = null; /* CURRENT DATE */

	private final static Logger gLogger = Logger.getLogger(PensionCaseServiceImpl.class);

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private void setSessionInfo(Map<String, Object> inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error("Error in setSessionInfo of TransferPPOServiceImpl ", e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.PensionPaymentATOService#
	 * loadTransferToOtherTreasury(java.util.Map)
	 */
	public ResultObject loadTransferToOtherTreasury(Map<String, Object> inputMap) {

		gLogger.info("In loadTransferToOtherTreasury method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TransferPPODAO lObjTransferPPODAO = null;
		List lLstTreasury = null;
		List<String> lLstPnsnrCodes = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		String lStrRoleId = null;
		String lStrElementCode = null;
		try {
			setSessionInfo(inputMap);
			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lObjTransferPPODAO = new TransferPPODAOImpl(null, serv.getSessionFactory());
			lLstTreasury = lObjTransferPPODAO.getAllTreasuryName(gLngLangId);
			// lStrRoleId =
			// lObjCommonDAOImpl.getRoleByPost(SessionHelper.getPostId(inputMap));
			lStrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			inputMap.put("RoleId", lStrRoleId);
			inputMap.put("lLstTreasury", lLstTreasury);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("TransferToOtherTreasury");
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
	 * com.tcs.sgv.pensionpay.service.TransferPPOService#getNameAndTreasuryFromPPONo
	 * (java.util.Map)
	 */
	public ResultObject getNameAndTreasuryFromPPONo(Map<String, Object> inputMap) {

		gLogger.info("In getNameAndTreasuryFromPPONo method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		TransferPPODAO lObjTransferPPODAO = null;
		String lStrPPONo = null;
		List lLstNameAndTreasury = null;
		Object[] lArrObj = null;
		CommonPensionDAO lObjCommonPensionDAO = null;
		List<Object[]> lLstAGCircleDtl = null;
		String lStrAGCircleCode = null;
		String lStrAGCircleName = null;
		try {
			setSessionInfo(inputMap);
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lStrPPONo = StringUtility.getParameter("txtPpoNo", request);

			lObjTransferPPODAO = new TransferPPODAOImpl(TransferPPOServiceImpl.class, serv.getSessionFactory());
			if (lStrPPONo.length() > 0) {
				lLstNameAndTreasury = lObjTransferPPODAO.getNameAndTreasuryFromPPONo(lStrPPONo.trim(), gLngLangId, gStrLocationCode);
			}
			if (lLstNameAndTreasury != null && lLstNameAndTreasury.size() > 0) {
				lArrObj = (Object[]) lLstNameAndTreasury.get(0);
				if (lArrObj[6] != null) {
					lLstAGCircleDtl = lObjCommonPensionDAO.getAGCircleDtlFromLocationCode(lArrObj[6].toString());
					if (lLstAGCircleDtl != null && lLstAGCircleDtl.size() > 0) {
						Object[] lArrADDtl = lLstAGCircleDtl.get(0);
						lStrAGCircleCode = (lArrADDtl[0] != null) ? lArrADDtl[0].toString() : "";
						lStrAGCircleName = (lArrADDtl[1] != null) ? lArrADDtl[1].toString() : "";
					}
				}
			}
			String lSBStatus = getResponseXMLDocPPONo(lLstNameAndTreasury, lStrAGCircleCode, lStrAGCircleName).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//e.printStackTrace();
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDocPPONo(List lLstNameAndTreasury, String lStrAGCircleCode, String lStrAGCircleName) {

		StringBuilder lStrBldXML = new StringBuilder();
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		lStrBldXML.append("<XMLDOCPPONo>");
		if (lLstNameAndTreasury != null && lLstNameAndTreasury.size() > 0) {
			Iterator itr = lLstNameAndTreasury.iterator();

			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				lStrBldXML.append("<PnsnrName>");
				lStrBldXML.append(obj[0].toString());
				lStrBldXML.append("</PnsnrName>");
				lStrBldXML.append("<TrsryName>");
				lStrBldXML.append("<![CDATA[");
				lStrBldXML.append(obj[1].toString());
				lStrBldXML.append("]]>");
				lStrBldXML.append("</TrsryName>");
				lStrBldXML.append("<LocationCode>");
				lStrBldXML.append(obj[2].toString());
				lStrBldXML.append("</LocationCode>");
				lStrBldXML.append("<PnsnrCode>");
				lStrBldXML.append(obj[3].toString());
				lStrBldXML.append("</PnsnrCode>");
				lStrBldXML.append("<CaseStatus>");
				lStrBldXML.append(obj[4].toString());
				lStrBldXML.append("</CaseStatus>");
				lStrBldXML.append("<RequestId>");
				lStrBldXML.append((obj[5] != null) ? obj[5].toString() : "");
				lStrBldXML.append("</RequestId>");
				lStrBldXML.append("<ToLocation>");
				lStrBldXML.append((obj[6] != null) ? obj[6].toString() : "-1");
				lStrBldXML.append("</ToLocation>");
				lStrBldXML.append("<OfficeCode>");
				lStrBldXML.append((obj[8] != null) ? obj[8].toString() : "");
				lStrBldXML.append("</OfficeCode>");
				lStrBldXML.append("<AGCicleName>");
				lStrBldXML.append((lStrAGCircleName != null) ? lStrAGCircleName : "");
				lStrBldXML.append("</AGCicleName>");
				lStrBldXML.append("<AGCicleCode>");
				lStrBldXML.append((lStrAGCircleCode != null) ? lStrAGCircleCode : "");
				lStrBldXML.append("</AGCicleCode>");
				lStrBldXML.append("<ApplnReceiveDate>");
				lStrBldXML.append((obj[9] != null) ? lObjSimpleDateFormat.format(obj[9]) : "");
				lStrBldXML.append("</ApplnReceiveDate>");
			}
		}

		else {
			lStrBldXML.append("<EmptyList>");
			lStrBldXML.append("EmptyList");
			lStrBldXML.append("</EmptyList>");
		}

		lStrBldXML.append("</XMLDOCPPONo>");
		return lStrBldXML;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.TransferPPOService#insertTransferOfPPOCase
	 * (java.util.Map)
	 */
	public ResultObject insertTransferOfPPOCase(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long lLngTransferDtlsId = null;
		TransferPPODAO lObjTransferPPODAO = null;
		String lStrPensionerCode = null;
		String lStrFlagValue = null;
		TrnPensionTransferDtls lObjTrnPensionTransferDtlsVO = new TrnPensionTransferDtls();
		StringBuilder lStrBldXML = null;
		String lStrResult = null;
		Character lCharAGFlag = null;
		try {
			setSessionInfo(inputMap);
			String strTransMode = (String) inputMap.get("Mode");
			String lStrOtherStateName = StringUtility.getParameter("txtOtherState", request);
			lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionTransferDtls.class, serv.getSessionFactory());
			lObjTrnPensionTransferDtlsVO = (TrnPensionTransferDtls) inputMap.get("lObjTrnPensionTransferDtlsVO");
			lCharAGFlag = lObjTrnPensionTransferDtlsVO.getAgFlag();
			if (strTransMode.trim().equalsIgnoreCase("Add")) {
				if (lObjTrnPensionTransferDtlsVO != null) {
					lLngTransferDtlsId = IFMSCommonServiceImpl.getNextSeqNum("trn_pension_transfer_dtls", inputMap);
					lObjTrnPensionTransferDtlsVO.setTransferDtlsId(lLngTransferDtlsId);
					lObjTrnPensionTransferDtlsVO.setStatus(gObjRsrcBndle.getString("STATUS.TRANSFERED"));
					lObjTransferPPODAO.create(lObjTrnPensionTransferDtlsVO);
					gLogger.info("Record Inserted in table trn_pension_transfer_dtls successfully");
				}
				lStrBldXML = getResponseXMLDoc(strTransMode.trim(), lObjTrnPensionTransferDtlsVO.getRequestId());
			} else if (strTransMode.trim().equalsIgnoreCase("Reject")) {
				lObjTrnPensionTransferDtlsVO.setStatus(gObjRsrcBndle.getString("STATUS.RQSTREJECT"));
				lObjTrnPensionTransferDtlsVO.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
				lObjTrnPensionTransferDtlsVO.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
				lObjTrnPensionTransferDtlsVO.setUpdatedDate(gDtCurDate);
				lObjTransferPPODAO.update(lObjTrnPensionTransferDtlsVO);
				lStrBldXML = getResponseXMLDoc(strTransMode.trim(), null);
			} else {
				if (lObjTrnPensionTransferDtlsVO != null) {
					// ---If transfer to outside ag circle then moving case to
					// archival status and not showing it in receive transferred
					// cases worklist.
					if (lCharAGFlag == 'N') {
						lObjTrnPensionTransferDtlsVO.setStatus(gObjRsrcBndle.getString("STATUS.TRANSFERARCHIVED"));
					}
					lObjTrnPensionTransferDtlsVO.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjTrnPensionTransferDtlsVO.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionTransferDtlsVO.setUpdatedDate(gDtCurDate);
					lObjTransferPPODAO.update(lObjTrnPensionTransferDtlsVO);
					gLogger.info("Record Updated in table trn_pension_transfer_dtls successfully");
				}
				lStrPensionerCode = StringUtility.getParameter("txtPnsnrCode", request);
				if (lStrPensionerCode.length() > 0) {
					lObjTransferPPODAO.updateMstPensionerDtls(lStrPensionerCode.trim());
					lObjTransferPPODAO.updateTrnPensionRqstHdr(lStrPensionerCode.trim(), lStrFlagValue, null, gLngUserId, gLngPostId, gDtCurDate);
				}

				if (lCharAGFlag == 'O' || lCharAGFlag == 'N') {
					lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
					List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstTrnPensionRqstHdr.isEmpty()) {
						TrnPensionRqstHdr lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
						if (lCharAGFlag == 'O') {
							lObjTrnPensionRqstHdr.setCaseStatus(gObjRsrcBndle.getString("STATFLG.TRANSFERSTATE"));
						}
						if (lCharAGFlag == 'N') {
							lObjTrnPensionRqstHdr.setCaseStatus(gObjRsrcBndle.getString("STATFLG.ARCHIVED"));
						}
					}
				}
				lStrBldXML = getResponseXMLDoc(strTransMode.trim(), null);
			}

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);

			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;

	}

	private StringBuilder getResponseXMLDoc(String strMode, String RequestId) {

		StringBuilder lStrHidPKs = new StringBuilder();

		lStrHidPKs.append("<XMLDOC>");
		lStrHidPKs.append("<MESSAGECODE>");
		lStrHidPKs.append(strMode);
		lStrHidPKs.append("</MESSAGECODE>");
		if (RequestId != null && RequestId.length() > 0) {
			lStrHidPKs.append("<REQUESTID>");
			lStrHidPKs.append(RequestId);
			lStrHidPKs.append("</REQUESTID>");

		}
		lStrHidPKs.append("</XMLDOC>");

		gLogger.info("lStrHidPKs : " + lStrHidPKs);
		return lStrHidPKs;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.TransferPPOService#loadTransferToOtherTrsry
	 * (java.util.Map)
	 */
	public ResultObject loadReceiveTransferCase(Map<String, Object> inputMap) {

		gLogger.info("In loadReceiveTransferCase method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TransferPPODAO lObjTransferPPODAO = null;
		PensionConfigDAO lObjPensionConfigDAO = null;
		List lLstTransferCase = null;
		List lLstBankNames = null;
		String lStrTransferCase = ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.RECEIVE");
		Integer lIntTotalRecords = null;
		List lLstTypeOfSearch = null;
		String lStrCmbSearchBy = null;
		String lStrSearchVal = null;
		List lLstTreasury = null;
		List lLstBrnchCodes = null;
		List lLstPPONo = null;
		try {
			setSessionInfo(inputMap);
			lLstTypeOfSearch = IFMSCommonServiceImpl.getLookupValues("Search For Transfer", gLngLangId, inputMap);

			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lObjTransferPPODAO = new TransferPPODAOImpl(null, serv.getSessionFactory());
			lLstTreasury = lObjTransferPPODAO.getAllTreasuryName(gLngLangId);
			lLstBrnchCodes = lObjTransferPPODAO.getAllBrnchCodes(gStrLocationCode);
			lLstPPONo = lObjTransferPPODAO.getAllPPONo();
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			lLstBankNames = lObjPensionConfigDAO.getBankNames(gLngLangId, gStrLocationCode);
			lStrCmbSearchBy = StringUtility.getParameter("CmbSearchBy", request);
			if (lStrCmbSearchBy.length() > 0 && (StringUtility.getParameter("CmbNewTreasury", request).length() > 0 || StringUtility.getParameter("txtSearchValue", request).length() > 0)) {
				if (StringUtility.getParameter("txtSearchValue", request).length() > 0) {
					lStrSearchVal = StringUtility.getParameter("txtSearchValue", request);
				}
				if (StringUtility.getParameter("CmbNewTreasury", request).length() > 0) {
					lStrSearchVal = StringUtility.getParameter("CmbNewTreasury", request);
				}

				lIntTotalRecords = lObjTransferPPODAO.getTransferCasesCount(gLngLangId, gStrLocationCode, lStrCmbSearchBy, lStrSearchVal, lStrTransferCase, displayTag);
				lLstTransferCase = lObjTransferPPODAO.getTransferCases(gLngLangId, gStrLocationCode, lStrCmbSearchBy, lStrSearchVal, lStrTransferCase, displayTag);
			} else {

				lIntTotalRecords = lObjTransferPPODAO.getTransferCasesCount(gLngLangId, gStrLocationCode, lStrCmbSearchBy, lStrSearchVal, lStrTransferCase, displayTag);
				lLstTransferCase = lObjTransferPPODAO.getTransferCases(gLngLangId, gStrLocationCode, lStrCmbSearchBy, lStrSearchVal, lStrTransferCase, displayTag);
			}

			inputMap.put("lStrTransferCase", lStrTransferCase);
			inputMap.put("lLstBrnchCodes", lLstBrnchCodes);
			inputMap.put("lLstPPONo", lLstPPONo);
			inputMap.put("lLstTypeOfSearch", lLstTypeOfSearch);
			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("lLstTreasury", lLstTreasury);
			inputMap.put("lLstBankNames", lLstBankNames);
			inputMap.put("lLstReceiveTransferCase", lLstTransferCase);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ReceiveTransferCase");
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
	 * com.tcs.sgv.pensionpay.service.TransferPPOService#changeInPPOCase(java
	 * .util.Map)
	 */
	public ResultObject changeInPPOCase(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrTrnfrDtlsId = null;
		String lStrFlagValue = null;
		String lStrRemarks = null;
		TransferPPODAO lObjTransferPPODAO = null;
		String lStrPensionerCode = null;
		String lStrLocationCode = null;
		String lStrRegNo = null;
		Date lDtPpoInwardDate = null;
		Date lDtPpoRegDate = null;
		Date lDtCmnsnDate = null;
		Long lLngAuditorPostId = 0l;
		String lStrReceiveBrnchCode = null;
		String lStrReceiveBankCode = null;
		String lStrReceiveAccNo = null;
		Long lLngHstPnsnPmntId = 0l;
		List lLstTempList = null;
		Iterator lObjIterator = null;
		Object[] lArrObj = null;
		String lStrLibraryNo = null;
		try {

			setSessionInfo(inputMap);
			lStrFlagValue = StringUtility.getParameter("flag", request);
			String strPKValue = StringUtility.getParameter("TrnfrDtls_Id", request).toString().trim();
			String[] strArrPKValue = strPKValue.split("~");
			CommonPensionService lObjCommonPensionService = new CommonPensionServiceImpl();
			if (lStrFlagValue.equals("1")) // for received PPO case
			{
				String lStrBankCode = StringUtility.getParameter("bankCode", request).toString().trim();
				String[] lStrArrBankCode = lStrBankCode.split("~");
				String lStrBranchCode = StringUtility.getParameter("branchCode", request).toString().trim();
				String[] lStrArrBranchCode = lStrBranchCode.split("~");
				String lStrAccNo = StringUtility.getParameter("accNo", request).toString().trim();
				String[] lStrArrAccNo = lStrAccNo.split("~");
				String lStrPPONo = StringUtility.getParameter("ppoNo", request).toString().trim();
				String[] lStrArrPPONo = lStrPPONo.split("~");
				String lStrOldPPONo = StringUtility.getParameter("oldPpoNo", request).toString().trim();
				String[] lStrArrOldPPONo = lStrOldPPONo.split("~");

				for (int index = 0; index < strArrPKValue.length; index++) {

					lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionTransferDtls.class, serv.getSessionFactory());

					lStrTrnfrDtlsId = strArrPKValue[index];

					Long lLongPKValue = Long.parseLong(strArrPKValue[index]);

					TrnPensionTransferDtls lObjTrnPensionTransferDtls = (TrnPensionTransferDtls) lObjTransferPPODAO.read(lLongPKValue);

					lObjTrnPensionTransferDtls.setStatus(gObjRsrcBndle.getString("STATUS.RECEIVED"));
					lObjTrnPensionTransferDtls.setReceiveDate(gDtCurDate);
					lObjTrnPensionTransferDtls.setReceivePostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionTransferDtls.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjTrnPensionTransferDtls.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionTransferDtls.setUpdatedDate(gDtCurDate);

					lStrPensionerCode = lObjTrnPensionTransferDtls.getPensionerCode();
					lStrLocationCode = lObjTrnPensionTransferDtls.getToLocation();

					lStrRegNo = lObjTransferPPODAO.getRegNoFromMstPensionerDtls(lStrPensionerCode);
					lObjTrnPensionTransferDtls.setRegistrationNo(lStrRegNo);

					lLstTempList = lObjTransferPPODAO.getDatesFromTrnPensionRqstHdr(lStrPensionerCode);
					lObjIterator = lLstTempList.iterator();
					while (lObjIterator.hasNext()) {
						lArrObj = (Object[]) lObjIterator.next();
						if (lArrObj[0] != null) {
							lDtPpoInwardDate = (Date) lArrObj[0];
						}
						if (lArrObj[1] != null) {
							lDtPpoRegDate = (Date) lArrObj[1];
						}
						if (lArrObj[2] != null) {
							lDtCmnsnDate = (Date) lArrObj[2];
						}
					}
					lObjTrnPensionTransferDtls.setPpoInwardDate(lDtPpoInwardDate);
					lObjTrnPensionTransferDtls.setPpoRegDate(lDtPpoRegDate);
					lObjTrnPensionTransferDtls.setCommensionDate(lDtCmnsnDate);

					lObjTransferPPODAO.updateTrnPensionRqstHdr(lStrPensionerCode, lStrFlagValue, lStrLocationCode, gLngUserId, gLngPostId, gDtCurDate);

					lObjTransferPPODAO = new TransferPPODAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
					List<MstPensionerDtls> lLstMstPensionerDtls = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstMstPensionerDtls.isEmpty()) {
						MstPensionerDtls lObjMstPensionerDtls = lLstMstPensionerDtls.get(0);
						lObjMstPensionerDtls.setLocationCode(lStrLocationCode);
						lObjMstPensionerDtls.setIdentificationFlag("N");
					}

					lObjTransferPPODAO = new TransferPPODAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
					List<MstPensionerHdr> lLstMstPensionerHdr = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstMstPensionerHdr.isEmpty()) {
						MstPensionerHdr lObjMstPensionerHdr = lLstMstPensionerHdr.get(0);
						lObjMstPensionerHdr.setLocationCode(lStrLocationCode);
					}

					lObjTransferPPODAO = new TransferPPODAOImpl(MstPensionerNomineeDtls.class, serv.getSessionFactory());
					List<MstPensionerNomineeDtls> lLstMstPensionerNomineeDtls = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstMstPensionerNomineeDtls.isEmpty()) {
						MstPensionerNomineeDtls lObjMstPensionerNomineeDtls = lLstMstPensionerNomineeDtls.get(0);
						lObjMstPensionerNomineeDtls.setLocationCode(lStrLocationCode);
					}

					lObjTransferPPODAO = new TransferPPODAOImpl(TrnProvisionalVoucherDtls.class, serv.getSessionFactory());
					List<TrnProvisionalVoucherDtls> lLstTrnProvisionalVoucherDtls = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstTrnProvisionalVoucherDtls.isEmpty()) {
						TrnProvisionalVoucherDtls lObjTrnProvisionalVoucherDtls = lLstTrnProvisionalVoucherDtls.get(0);
						lObjTrnProvisionalVoucherDtls.setLocationCode(Long.valueOf(lStrLocationCode));
					}

					lObjTransferPPODAO = new TransferPPODAOImpl(TrnCvpRestorationDtls.class, serv.getSessionFactory());
					List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtls = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (lLstTrnCvpRestorationDtls.size() > 0 && !lLstTrnCvpRestorationDtls.isEmpty()) {
						TrnCvpRestorationDtls lObjTrnCvpRestorationDtls = lLstTrnCvpRestorationDtls.get(0);
						lObjTrnCvpRestorationDtls.setLocationCode(Long.valueOf(lStrLocationCode));
					}
					if (lStrArrBranchCode[index].trim().equals("null")) {
						lStrReceiveBrnchCode = null;
					} else {
						lStrReceiveBrnchCode = lStrArrBranchCode[index].trim();
					}
					if (lStrArrAccNo[index].trim().equals("null")) {
						lStrReceiveAccNo = null;
					} else {
						lStrReceiveAccNo = lStrArrAccNo[index].trim();
					}
					if (lStrArrBankCode[index].trim().equals("null")) {
						lStrReceiveBankCode = null;
					} else {
						lStrReceiveBankCode = lStrArrBankCode[index].trim();
					}

					lObjTransferPPODAO.updateMstPensionerDtls(lStrPensionerCode, lStrReceiveBankCode, lStrReceiveBrnchCode, lStrReceiveAccNo, lStrFlagValue);

					lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
					List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstTrnPensionRqstHdr.isEmpty()) {
						TrnPensionRqstHdr lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
						lObjTrnPensionRqstHdr.setSeenFlag("N");
						lObjTrnPensionRqstHdr.setPpoInwardDate(gDtCurDate);
						lObjTrnPensionRqstHdr.setPpoRegDate(null);
						// lObjTrnPensionRqstHdr.setCommensionDate(gDtCurDate);
						lLngAuditorPostId = lObjTransferPPODAO.getAuditorPostIdFromBnkBrnchCode(lStrReceiveBrnchCode);
						if (lLngAuditorPostId != 0L) {
							lObjTrnPensionRqstHdr.setCaseOwner(BigDecimal.valueOf(lLngAuditorPostId));
						} else {
							lObjTrnPensionRqstHdr.setCaseOwner(null);
						}

					}

				}
				// In case of trasnfer outside AG ::: update PPO No.
				if (lStrArrPPONo.length > 0) {
					for (int lIntCnt = 0; lIntCnt < lStrArrPPONo.length; lIntCnt++) {
						lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
						List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = lObjTransferPPODAO.getListByColumnAndValue("ppoNo", lStrArrOldPPONo[lIntCnt].trim());
						if (!lLstTrnPensionRqstHdr.isEmpty()) {
							TrnPensionRqstHdr lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
							lObjTrnPensionRqstHdr.setPpoNo(lStrArrPPONo[lIntCnt]);
						}
						// Maintain History of changed ppo number :-:-:
						HstPnsnPmntPpoNo lObjHstPnsnPmntPpoNo = new HstPnsnPmntPpoNo();
						lStrPensionerCode = lObjTransferPPODAO.getPensionerCodeFromPpoNo(lStrArrPPONo[lIntCnt].trim());
						lObjHstPnsnPmntPpoNo = lObjTransferPPODAO.setDtlsInHstPnsnPmntPpoNo(lObjHstPnsnPmntPpoNo, inputMap, gStrLocationCode, lStrArrPPONo[lIntCnt].trim(),
								lStrArrOldPPONo[lIntCnt].trim(), lStrPensionerCode);
						lLngHstPnsnPmntId = IFMSCommonServiceImpl.getNextSeqNum("hst_pnsnpmnt_ppono", inputMap);
						lObjHstPnsnPmntPpoNo.setHstpnsnpmntId(lLngHstPnsnPmntId);
						lObjTransferPPODAO.create(lObjHstPnsnPmntPpoNo);
					}
				}
				String lSBStatus = getResponseXMLDocFlagVal(lStrFlagValue).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}

			if (lStrFlagValue.equals("2")) // for rejected PPO case
			{
				lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionTransferDtls.class, serv.getSessionFactory());
				lStrRemarks = StringUtility.getParameter("txtRemarks", request);
				if (lStrRemarks.length() > 0) {
					for (int index = 0; index < strArrPKValue.length; index++) {

						lStrTrnfrDtlsId = strArrPKValue[index];

						Long lLongPKValue = Long.parseLong(strArrPKValue[index]);
						TrnPensionTransferDtls lObjTrnPensionTransferDtls = (TrnPensionTransferDtls) lObjTransferPPODAO.read(lLongPKValue);

						lObjTrnPensionTransferDtls.setStatus(gObjRsrcBndle.getString("STATUS.REJECTED"));
						lObjTrnPensionTransferDtls.setRemarks(lStrRemarks);
						lObjTrnPensionTransferDtls.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
						lObjTrnPensionTransferDtls.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
						lObjTrnPensionTransferDtls.setUpdatedDate(gDtCurDate);

						lStrPensionerCode = lObjTrnPensionTransferDtls.getPensionerCode();
						lStrLocationCode = lObjTrnPensionTransferDtls.getFromLocation();
						lObjTransferPPODAO.updateTrnPensionRqstHdr(lStrPensionerCode, lStrFlagValue, lStrLocationCode, gLngUserId, gLngPostId, gDtCurDate);

					}

					String lSBStatus = getResponseXMLDocFlagVal(lStrFlagValue).toString();
					String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

					inputMap.put("ajaxKey", lStrResult);
					resObj.setViewName("ajaxData");
					resObj.setResultValue(inputMap);
					;

				} else {
					if ((!strPKValue.endsWith("~")) && (strPKValue.length() > 0)) {
						TrnPensionTransferDtls lObjTrnPensionTransferDtlsVO = (TrnPensionTransferDtls) lObjTransferPPODAO.read(Long.valueOf(strPKValue));
						inputMap.put("lObjTrnPensionTransferDtlsVO", lObjTrnPensionTransferDtlsVO);
					} else {
						inputMap.put("TrnfrDtlsId", strPKValue);
						inputMap.put("FlagVal", lStrFlagValue);
					}
					resObj.setViewName("popUpRemarks");
					resObj.setResultCode(ErrorConstants.SUCCESS);
					resObj.setResultValue(inputMap);
				}
			}

			if (lStrFlagValue.equals("3")) // to move record into library
			{
				String lStrBankCode = StringUtility.getParameter("bankCode", request).toString().trim();
				String[] lStrArrBankCode = lStrBankCode.split("~");
				String lStrBranchCode = StringUtility.getParameter("branchCode", request).toString().trim();
				String[] lStrArrBranchCode = lStrBranchCode.split("~");
				String lStrAccNo = StringUtility.getParameter("accNo", request).toString().trim();
				String[] lStrArrAccNo = lStrAccNo.split("~");
				lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionTransferDtls.class, serv.getSessionFactory());
				for (int index = 0; index < strArrPKValue.length; index++) {
					lStrTrnfrDtlsId = strArrPKValue[index];
					lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionTransferDtls.class, serv.getSessionFactory());
					Long lLongPKValue = Long.parseLong(strArrPKValue[index]);
					TrnPensionTransferDtls lObjTrnPensionTransferDtls = (TrnPensionTransferDtls) lObjTransferPPODAO.read(lLongPKValue);

					lObjTrnPensionTransferDtls.setStatus(gObjRsrcBndle.getString("STATFLG.APPROVED"));
					lObjTrnPensionTransferDtls.setUpdatedUserId(BigDecimal.valueOf(gLngUserId));
					lObjTrnPensionTransferDtls.setUpdatedPostId(BigDecimal.valueOf(gLngPostId));
					lObjTrnPensionTransferDtls.setUpdatedDate(gDtCurDate);

					lStrPensionerCode = lObjTrnPensionTransferDtls.getPensionerCode();
					lStrLocationCode = lObjTrnPensionTransferDtls.getFromLocation();
					lObjTransferPPODAO.updateTrnPensionRqstHdr(lStrPensionerCode, lStrFlagValue, lStrLocationCode, gLngUserId, gLngPostId, gDtCurDate);
					lObjTransferPPODAO.updateMstPensionerDtls(lStrPensionerCode, lStrArrBankCode[index], lStrArrBranchCode[index], lStrArrAccNo[index], lStrFlagValue);

					lObjTransferPPODAO = new TransferPPODAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
					List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstTrnPensionRqstHdr.isEmpty()) {
						TrnPensionRqstHdr lObjTrnPensionRqstHdr = lLstTrnPensionRqstHdr.get(0);
						lObjTrnPensionRqstHdr.setSeenFlag("Y");
						lObjTrnPensionRqstHdr.setPpoInwardDate(gDtCurDate);
						lObjTrnPensionRqstHdr.setPpoRegDate(gDtCurDate);
						// lObjTrnPensionRqstHdr.setCommensionDate(gDtCurDate);
						lLngAuditorPostId = lObjTransferPPODAO.getAuditorPostIdFromBnkBrnchCode(lStrArrBranchCode[index]);
						lObjTrnPensionRqstHdr.setLocationCode(gStrLocationCode);
						if (lLngAuditorPostId != 0L) {
							lObjTrnPensionRqstHdr.setCaseOwner(BigDecimal.valueOf(lLngAuditorPostId));
						} else {
							lObjTrnPensionRqstHdr.setCaseOwner(null);
						}

					}
					lObjTransferPPODAO = new TransferPPODAOImpl(MstPensionerDtls.class, serv.getSessionFactory());
					List<MstPensionerDtls> lLstMstPensionerDtls = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstMstPensionerDtls.isEmpty()) {
						MstPensionerDtls lObjMstPensionerDtls = lLstMstPensionerDtls.get(0);
						lObjMstPensionerDtls.setLocationCode(gStrLocationCode);
						lObjMstPensionerDtls.setIdentificationFlag("Y");
					}

					lObjTransferPPODAO = new TransferPPODAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
					List<MstPensionerHdr> lLstMstPensionerHdr = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstMstPensionerHdr.isEmpty()) {
						MstPensionerHdr lObjMstPensionerHdr = lLstMstPensionerHdr.get(0);
						lObjMstPensionerHdr.setLocationCode(gStrLocationCode);
					}

					lObjTransferPPODAO = new TransferPPODAOImpl(MstPensionerNomineeDtls.class, serv.getSessionFactory());
					List<MstPensionerNomineeDtls> lLstMstPensionerNomineeDtls = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstMstPensionerNomineeDtls.isEmpty()) {
						MstPensionerNomineeDtls lObjMstPensionerNomineeDtls = lLstMstPensionerNomineeDtls.get(0);
						lObjMstPensionerNomineeDtls.setLocationCode(gStrLocationCode);
					}

					lObjTransferPPODAO = new TransferPPODAOImpl(TrnProvisionalVoucherDtls.class, serv.getSessionFactory());
					List<TrnProvisionalVoucherDtls> lLstTrnProvisionalVoucherDtls = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (!lLstTrnProvisionalVoucherDtls.isEmpty()) {
						TrnProvisionalVoucherDtls lObjTrnProvisionalVoucherDtls = lLstTrnProvisionalVoucherDtls.get(0);
						lObjTrnProvisionalVoucherDtls.setLocationCode(Long.valueOf(gStrLocationCode));
					}

					lObjTransferPPODAO = new TransferPPODAOImpl(TrnCvpRestorationDtls.class, serv.getSessionFactory());
					List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtls = lObjTransferPPODAO.getListByColumnAndValue("pensionerCode", lStrPensionerCode);
					if (lLstTrnCvpRestorationDtls.size() > 0 && !lLstTrnCvpRestorationDtls.isEmpty()) {
						TrnCvpRestorationDtls lObjTrnCvpRestorationDtls = lLstTrnCvpRestorationDtls.get(0);
						lObjTrnCvpRestorationDtls.setLocationCode(Long.valueOf(gStrLocationCode));
					}
					// Generate Library Number start

					inputMap.put("pensionerCode", lStrPensionerCode);
					lStrLibraryNo = lObjCommonPensionService.generateLibraryNo(inputMap);

					// Generate Library Number end
				}
				String lSBStatus = getResponseXMLDocFlagVal(lStrFlagValue).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}

		} catch (Exception ex) {
			//ex.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	private StringBuilder getResponseXMLDocFlagVal(String lStrFlagValue) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOCFlag>");
		lStrBldXML.append("<FlagVal>");
		lStrBldXML.append(lStrFlagValue);
		lStrBldXML.append("</FlagVal>");
		lStrBldXML.append("</XMLDOCFlag>");

		return lStrBldXML;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.TransferPPOService#loadRejectedTransferCase
	 * (java.util.Map)
	 */
	public ResultObject loadRejectedTransferCase(Map<String, Object> inputMap) {

		gLogger.info("In loadRejectedTransferCase method.......");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TransferPPODAO lObjTransferPPODAO = null;
		PensionConfigDAO lObjPensionConfigDAO = null;
		List lLstTransferCase = null;
		List lLstBankNames = null;
		List lLstAuditorNames = null;
		String lStrTransferCase = ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.REJECT");
		Integer lIntTotalRecords = null;
		List lLstTypeOfSearch = null;
		String lStrCmbSearchBy = null;
		String lStrSearchVal = null;
		List lLstTreasury = null;
		List lLstBrnchCodes = null;
		try {
			setSessionInfo(inputMap);
			lLstTypeOfSearch = IFMSCommonServiceImpl.getLookupValues("Search For Transfer", gLngLangId, inputMap);

			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lObjTransferPPODAO = new TransferPPODAOImpl(null, serv.getSessionFactory());
			lLstTreasury = lObjTransferPPODAO.getAllTreasuryName(gLngLangId);
			lLstBrnchCodes = lObjTransferPPODAO.getAllBrnchCodes(gStrLocationCode);
			lObjPensionConfigDAO = new PensionConfigDAOImpl(null, serv.getSessionFactory());
			lLstBankNames = lObjPensionConfigDAO.getBankNames(gLngLangId, gStrLocationCode);
			lLstAuditorNames = lObjPensionConfigDAO.getAuditorName(inputMap, gStrLocationCode);
			lStrCmbSearchBy = StringUtility.getParameter("CmbSearchBy", request);
			if (lStrCmbSearchBy.length() > 0 && (StringUtility.getParameter("CmbNewTreasury", request).length() > 0 || StringUtility.getParameter("txtSearchValue", request).length() > 0)) {
				if (StringUtility.getParameter("txtSearchValue", request).length() > 0) {
					lStrSearchVal = StringUtility.getParameter("txtSearchVal", request);
				}

				if (StringUtility.getParameter("CmbNewTreasury", request).length() > 0) {
					lStrSearchVal = StringUtility.getParameter("CmbNewTreasury", request);

				}

				lIntTotalRecords = lObjTransferPPODAO.getTransferCasesCount(gLngLangId, gStrLocationCode, lStrCmbSearchBy, lStrSearchVal, lStrTransferCase, displayTag);
				lLstTransferCase = lObjTransferPPODAO.getTransferCases(gLngLangId, gStrLocationCode, lStrCmbSearchBy, lStrSearchVal, lStrTransferCase, displayTag);
			} else {

				lIntTotalRecords = lObjTransferPPODAO.getTransferCasesCount(gLngLangId, gStrLocationCode, lStrCmbSearchBy, lStrSearchVal, lStrTransferCase, displayTag);
				lLstTransferCase = lObjTransferPPODAO.getTransferCases(gLngLangId, gStrLocationCode, lStrCmbSearchBy, lStrSearchVal, lStrTransferCase, displayTag);
			}

			inputMap.put("lStrTransferCase", lStrTransferCase);
			inputMap.put("lLstTypeOfSearch", lLstTypeOfSearch);
			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("lLstTreasury", lLstTreasury);
			inputMap.put("lLstBrnchCodes", lLstBrnchCodes);
			inputMap.put("lLstBankNames", lLstBankNames);
			inputMap.put("lLstRejectedTransferCase", lLstTransferCase);
			inputMap.put("lLstAuditorNames", lLstAuditorNames);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("RejectedTransferCases");
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
	 * com.tcs.sgv.pensionpay.service.TransferPPOService#generateTransferLetter
	 * (java.util.Map)
	 */

	public ResultObject generateTransferLetter(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		StringBuffer lSbDisplayString = new StringBuffer();
		StringBuffer lSbPrintString = new StringBuffer();
		String lStrNewLine = StringUtils.getLineSeparator();
		String lStrOffAddr = "";
		String lStrSrNo = null;
		String lStrUniqueLetterNo = null;
		String lStrPnsnrCode = null;
		CVPRestorationLetterDAO lObjCVPRestorationLetterDAO = null;
		List lLstTransferLetterDtls = null;
		TransferPPODAO lObjTransferPPODAO = null;
		String lStrPensionerName = "";
		String lStrPpoNo = "";
		String lStrRopType = null;
		BigDecimal lBDBasicPensionAmnt = BigDecimal.ZERO;
		String lStrPensionerAddr = "";
		String lStrDistrictName = "";
		List<TrnPensionBillDtls> lLstBillAmntDtls = new ArrayList<TrnPensionBillDtls>();
		BigDecimal lBDdaRate = BigDecimal.ZERO;
		BigDecimal lBDTiAmnt = BigDecimal.ZERO;
		BigDecimal lBDCvpMonthAmnt = BigDecimal.ZERO;
		String lStrPayForMonth = "";
		String lStrFromDate = "";
		String lStrToDate = "";
		String lStrSourceTreasuryName = "";
		String lStrTransferToLocCode = "";
		String lStrDestTreasuryName = "";
		String lStrPayCmsn = null;
		String lStrOtherStateName = "";
		String lStrTransferRqstId = "";
		String lStrAGAddr = "";
		String lStrAGCircleCode = "";
		CommonPensionDAO lObjCommonPensionDAO = null;
		List<Object[]> lLstArrAGDtls = null;
		String lStrAGCircleName = "";
		List<Short> lLstBillStatus = new ArrayList<Short>();
		TrnPensionBillDtls lObjTrnPensionBillDtls = new TrnPensionBillDtls();
		try {
			setSessionInfo(inputMap);
			lStrPnsnrCode = StringUtility.getParameter("PnsnrCode", request).trim();
			lStrTransferRqstId = StringUtility.getParameter("requestId", request).trim();
			// lStrAGCircleCode = StringUtility.getParameter("agCode",
			// request).trim();
			lObjCVPRestorationLetterDAO = new CVPRestorationLetterDAOImpl(null, serv.getSessionFactory());
			lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			// office Address

			lStrOffAddr = lObjCVPRestorationLetterDAO.getOffiCeAddr(gStrLocationCode);

			// lStrAGAddr =
			// lObjCVPRestorationLetterDAO.getOffiCeAddr(lStrAGCircleCode);

			// Unique letter no. generation starts

			lStrSrNo = IDGenerateDelegate.getNextIdWODbidLocationId("transferletter_sr_no", inputMap);
			lStrUniqueLetterNo = SessionHelper.getLocationVO(inputMap).getLocShortName().concat("/PPO Transfer Letter").concat("/" + DateUtility.getCurrentDate()).concat("/" + lStrSrNo);

			// Unique letter no. generation ends

			lObjTransferPPODAO = new TransferPPODAOImpl(null, serv.getSessionFactory());

			// check that pensioner code is transfer to other state or transfer
			// to other treasury
			lStrOtherStateName = lObjTransferPPODAO.checkTransferType(lStrPnsnrCode, gStrLocationCode, lStrTransferRqstId);

			// Prepare Transfer Letter Details like district name,ppo
			// no,pensioner name, basic amount etc. :: STARTS
			lLstTransferLetterDtls = lObjTransferPPODAO.getTransferLetterDetails(lStrPnsnrCode, gStrLocationCode, lStrOtherStateName);
			Iterator lObjIterator = lLstTransferLetterDtls.iterator();
			Object[] lArrObj = null;
			while (lObjIterator.hasNext()) {
				lArrObj = (Object[]) lObjIterator.next();
				if (lArrObj[0] != null) {
					lStrPensionerName = lArrObj[0].toString().trim();
				}
				if (lArrObj[1] != null) {
					lStrPpoNo = lArrObj[1].toString().trim();
				}
				if (lArrObj[2] != null) // destination treasury name
				{
					lStrDestTreasuryName = lArrObj[2].toString().trim();
				}
				if (lArrObj[3] != null) {
					lStrRopType = lArrObj[3].toString().trim();
					if (lStrRopType.trim().equals("2006")) {
						lStrPayCmsn = "Sixth Pay";
					}
					if (lStrRopType.trim().equals("1996")) {
						lStrPayCmsn = "Fifth Pay";
					}
					if (lStrRopType.trim().equals("1986")) {
						lStrPayCmsn = "Fourth Pay";
					}

				}
				if (lArrObj[4] != null) {
					lBDBasicPensionAmnt = (BigDecimal) lArrObj[4];
				}
				if (lArrObj[5] != null) // Address
				{
					lStrPensionerAddr = lArrObj[5].toString().trim();
				}
				if (lArrObj[6] != null) // source treasury name
				{
					lStrSourceTreasuryName = lArrObj[6].toString().trim();
				}
				if (lArrObj[7] != null) // Transfer To Location Code
				{
					lStrTransferToLocCode = lArrObj[7].toString().trim();
					lLstArrAGDtls = lObjCommonPensionDAO.getAGCircleDtlFromLocationCode(lStrTransferToLocCode);
					if (lLstArrAGDtls != null && lLstArrAGDtls.size() > 0) {
						Object[] lArrADDtl = lLstArrAGDtls.get(0);
						lStrAGCircleCode = (lArrADDtl[0] != null) ? lArrADDtl[0].toString() : "";
						lStrAGCircleName = (lArrADDtl[1] != null) ? lArrADDtl[1].toString() : "";
						lStrAGAddr = lObjCVPRestorationLetterDAO.getOffiCeAddr(lStrAGCircleCode);
					}
				}
			}

			// Prepare Transfer Letter Details like district name,ppo
			// no,pensioner name, basic amount etc. :: ENDS

			// district name (separate this value because of data configuration
			// problem in cmn_location_mst)
			lStrDistrictName = lObjTransferPPODAO.getDistrictName(lStrDestTreasuryName);

			lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
			lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
			lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
			lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
			lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
			
			lLstBillAmntDtls = lObjTransferPPODAO.getBillAmntDtls(lStrPnsnrCode,lLstBillStatus);

			if (lLstBillAmntDtls != null && !lLstBillAmntDtls.isEmpty() && lLstBillAmntDtls.size() > 0) {

				int lIntListIndex = lLstBillAmntDtls.size();
				Iterator it = lLstBillAmntDtls.iterator();
				Object[] lObjTempArr = null;
				//for (int lIntCnt = 0; lIntCnt < lLstBillAmntDtls.size(); lIntCnt++) {
				lObjTrnPensionBillDtls = (TrnPensionBillDtls) lLstBillAmntDtls.get(0);
//					if (lObjTempArr[0] != null) // da_rate
//					{
//						lBDdaRate = (BigDecimal) lObjTempArr[0];
//					}
//					if (lObjTempArr[1] != null) // ti_amount
//					{
//						lBDTiAmnt = (BigDecimal) lObjTempArr[1];
//					}
//					if (lObjTempArr[2] != null) // cvp_month_amount
//					{
//						lBDCvpMonthAmnt = (BigDecimal) lObjTempArr[2];
//					}
//					if (lObjTempArr[3] != null) // pay_for_month int
//					{
//						lStrPayForMonth = String.valueOf((lObjTempArr[3]));
//					}
					String lStrYear = lObjTrnPensionBillDtls.getPayForMonth().toString().substring(0, 4);
					String lStrMonth = lObjTrnPensionBillDtls.getPayForMonth().toString().substring(4);
					lStrFromDate = lStrMonth.concat("/".concat(lStrYear));
					if (Integer.valueOf(lStrMonth) == 12) {
						lStrToDate = "01/".concat("01/").concat(String.valueOf(Integer.valueOf(lStrYear) + 1));
					} else {
						if (Integer.valueOf(lStrMonth) < 10) {
							lStrToDate = "01/".concat("0" + String.valueOf(Integer.valueOf(lStrMonth) + 1)).concat("/" + lStrYear);
						} else {
							lStrToDate = "01/".concat(String.valueOf(Integer.valueOf(lStrMonth) + 1)).concat("/" + lStrYear);
						}
					}
					//break;
				//}

			}

			if (lLstTransferLetterDtls != null && lLstTransferLetterDtls.size() > 0) {
				/**************** Display String Starts ************************/

				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(45) + "Transfer Letter");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(space(39) + SessionHelper.getLocationName(inputMap));
				lSbDisplayString.append("\n");
				if (lStrOffAddr != null && lStrOffAddr.length() > 0) {
					lSbDisplayString.append(space(30) + lStrOffAddr);
				}
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append(lStrUniqueLetterNo);
				lSbDisplayString.append("\n");
				lSbDisplayString.append(gObjRsrcBndle.getString("PPMT.REQUESTID") + lStrTransferRqstId);
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("\n");
				lSbDisplayString.append("To ,");
				lSbDisplayString.append("\n");
				if (lStrOtherStateName != null && lStrOtherStateName.length() > 0) {
					lSbDisplayString.append("The Accounts Officer,");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("Accountant General Office,");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("( Accountants & Entitlement ) Maharashtra – I,");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("101, Maharshi Karve Road, ");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("Mumbai – 400 020");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(25) + "Sub :- Transfer of Pension in r/o " + space(2) + lStrPensionerName + space(2) + " From " + space(2));
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(32) + lStrSourceTreasuryName + space(2) + " to " + space(2) + lStrOtherStateName);
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(25) + "Pension Payment Order No." + space(2) + lStrPpoNo);
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("Sir,");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(20) + "I herewith forwarding the pensioner's both halves of P.P.O No." + space(2) + lStrPpoNo + space(2));
					lSbDisplayString.append("issued in favour of" + space(2) + lStrPensionerName + space(2));
					lSbDisplayString.append("to request you to arrange the transfer of his pension with effect from" + space(2) + lStrToDate + space(2));
					lSbDisplayString.append("at " + lStrOtherStateName + ".The pensioner in question has been last paid his pension" + space(2));
					lSbDisplayString.append("Basic Rs. " + String.valueOf(lBDBasicPensionAmnt.longValue() + "/-" + space(2)));
					lSbDisplayString.append("and the Temporary increase  " + space(2) + String.valueOf(lObjTrnPensionBillDtls.getDaRate().longValue()) + "%" + space(2));
					lSbDisplayString.append("Rs. " + String.valueOf(lObjTrnPensionBillDtls.getTiAmount().longValue() + "/- p.m.") + space(2));
					lSbDisplayString.append("Total Rs ." + String.valueOf(lBDBasicPensionAmnt.longValue()) + " - " + String.valueOf(lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + "(Commutation)" + " = "
							+ String.valueOf(lBDBasicPensionAmnt.longValue() - lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + " + " + String.valueOf(lObjTrnPensionBillDtls.getTiAmount().longValue()) + " = "
							+ ((lBDBasicPensionAmnt.longValue() - lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + lObjTrnPensionBillDtls.getTiAmount().longValue()) + " /- p.m.");
					lSbDisplayString.append("upto and for" + space(2) + lStrFromDate + " on " + space(2) + lStrToDate + space(2) + " at " + lStrSourceTreasuryName + ".");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("1.) She is entitled to Temporary increase in her pension.");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("2.) The other relevant information is appended to enable your office to prepare and issue the both halves of the P. P. O. No." + space(2) + lStrPpoNo);
					lSbDisplayString.append("\n");
					lSbDisplayString.append("3.) Personal marks of the Pensioner :- Details are enclosed with P. P. O .");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("4.) Height :- Details are enclose with the P. P. O.");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("5.) The pensioner Photo is also enclosed herewith. ");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("6.) The pensioner’s address is furnished below and she may please be informed of the transfer accordingly.");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(20) + "Please ackonowledge receipt.");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(69) + "Yours faithfully,");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(71) + "A.P.A.O/ ATO");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(lStrUniqueLetterNo);
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("Copy to" + space(2) + lStrPensionerName + space(2) + ", At & Post – " + space(2) + lStrPensionerAddr + space(2));
					lSbDisplayString.append("with reference to her application dated" + space(2) + DateUtility.getCurrentDate() + space(2) + "for information.");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(71) + "A.P.A.O/ ATO");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append((char) 12);
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");

				} else {

					lSbDisplayString.append("District Treasury Officer");
					lSbDisplayString.append("\n");
					if (lStrDistrictName != null && lStrDistrictName.length() > 0) {
						lSbDisplayString.append("District" + space(2) + lStrDistrictName);
					}
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(25) + "Sub :- Transfer of Pension in r/o " + space(2) + lStrPensionerName + space(2) + " P.P.O No." + space(2) + lStrPpoNo);
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(20) + "I am forwarding herewith both halves of P.P.O No." + space(2) + lStrPpoNo + space(2));
					lSbDisplayString.append("issued in favour of" + space(2) + lStrPensionerName + space(2) + lStrPayCmsn + space(2) + "Basic Rs.");
					lSbDisplayString.append(String.valueOf(lBDBasicPensionAmnt.longValue() + "/-" + space(2)));
					lSbDisplayString.append("requesting that the payment of the same may be permitted to be transferred from Presidency Treasury to" + space(2));
					lSbDisplayString.append(lStrDestTreasuryName + "," + space(2) + "District ," + space(2));
					if (lStrDistrictName != null && lStrDistrictName.length() > 0) {
						lSbDisplayString.append(lStrDistrictName);
					}
					lSbDisplayString.append(".The transfer of pension is permitted under Read Note No.2 under Rule 222 of the B. C. S. Rules. The question has been last paid his pension" + space(2));
					lSbDisplayString.append("Rs. " + String.valueOf(lBDBasicPensionAmnt.longValue() + "/-" + space(2)));
					lSbDisplayString.append("and the Temporary increase Relief " + space(2) + String.valueOf(lObjTrnPensionBillDtls.getDaRate().longValue()) + "%" + space(2));
					lSbDisplayString.append("Rs. " + String.valueOf(lObjTrnPensionBillDtls.getTiAmount().longValue() + "/- p.m."));
					lSbDisplayString.append("He has already commuted " + space(2) + "Rs. " + String.valueOf(lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + "/- and the total pension is ");
					lSbDisplayString.append("Rs ." + String.valueOf(lBDBasicPensionAmnt.longValue() - lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + "+" + String.valueOf(lObjTrnPensionBillDtls.getTiAmount().longValue()) + " = "
							+ ((lBDBasicPensionAmnt.longValue() - lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + lObjTrnPensionBillDtls.getTiAmount().longValue()) + " /- p.m.");
					lSbDisplayString.append("upto and for" + space(2) + lStrFromDate + " on " + space(2) + lStrToDate + space(2) + " at " + lStrSourceTreasuryName + ".");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("The following document are enclosed for necessary action.");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("1.Both halves of P.P.O No." + space(2) + lStrPpoNo);
					lSbDisplayString.append("\n");
					lSbDisplayString.append("2. A single/joint photograph with three specimen signature attested by gazzated officer.");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("The receipt of the documents in question may please be acknowledged. ");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(71) + "A.P.A.O/ ATO");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(lStrUniqueLetterNo);
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("Copy to 1.)" + lStrPensionerName + space(2) + ", At & Post – " + space(2) + lStrPensionerAddr + space(2) + "for information.");

					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("2.) " + space(2) + lStrAGAddr);
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append(space(71) + "A.P.A.O/ ATO");
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
					lSbDisplayString.append((char) 12);
					lSbDisplayString.append("\n");
					lSbDisplayString.append("\n");
				}

				/**************** Display String Ends ************************/

				/**************** Print String Starts ************************/
				lSbPrintString.append("<div><pre><font size='3px'>");
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(43) + " <strong> " + " Transfer Letter " + " </strong> ");
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(space(47) + SessionHelper.getLocationName(inputMap));
				lSbPrintString.append(lStrNewLine);
				if (lStrOffAddr != null && lStrOffAddr.length() > 0) {
					lSbPrintString.append(space(40) + lStrOffAddr);
				}
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrUniqueLetterNo);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(gObjRsrcBndle.getString("PPMT.REQUESTID") + lStrTransferRqstId);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append(lStrNewLine);
				lSbPrintString.append("To ,");
				lSbPrintString.append(lStrNewLine);
				if (lStrOtherStateName != null && lStrOtherStateName.length() > 0) {
					lSbPrintString.append("The Accounts Officer,");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("Accountant General Office,");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("( Accountants & Entitlement ) Maharashtra – I");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("101, Maharshi Karve Road, ");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("Mumbai – 400 020");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(30) + "Sub :- Transfer of Pension in r/o " + space(2) + lStrPensionerName + space(2) + " From " + space(2) + lStrSourceTreasuryName);
					lSbPrintString.append(" to " + space(2) + lStrOtherStateName);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(30) + "Pension Payment Order No." + space(2) + lStrPpoNo);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("Sir,");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(15) + "I herewith forwarding the pensioner's both halves of P.P.O No." + space(2) + lStrPpoNo + space(2));
					lSbPrintString.append("issued in favour of" + space(2) + lStrPensionerName + space(2));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("to request you to arrange the transfer of his pension with effect from" + space(2) + lStrToDate + space(2) + "at" + space(2) + lStrOtherStateName + ".");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("The pensioner in question has been last paid his pension" + space(2));
					lSbPrintString.append("Basic Rs. " + String.valueOf(lBDBasicPensionAmnt.longValue() + "/-" + space(2)) + "and the Temporary increase" + space(2));
					lSbPrintString.append(String.valueOf(lObjTrnPensionBillDtls.getDaRate().longValue()) + "%" + space(2));
					lSbPrintString.append("Rs. " + String.valueOf(lObjTrnPensionBillDtls.getTiAmount().longValue() + "/- p.m.") + space(2));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("Total Rs ." + String.valueOf(lBDBasicPensionAmnt.longValue()) + " - " + String.valueOf(lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + "(Commutation)" + " = "
							+ String.valueOf(lBDBasicPensionAmnt.longValue() - lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + " + " + String.valueOf(lObjTrnPensionBillDtls.getTiAmount().longValue()) + " = "
							+ ((lBDBasicPensionAmnt.longValue() - lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + lObjTrnPensionBillDtls.getTiAmount().longValue()) + " /- p.m.");
					lSbPrintString.append("upto and for");
					lSbPrintString.append(lStrFromDate + " on " + space(2) + lStrToDate + space(2));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(" at " + lStrSourceTreasuryName + ".");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("1.) She is entitled to Temporary increase in her pension.");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("2.) The other relevant information is appended to enable your office to prepare and issue the both halves of the P. P. O. No.");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(4) + lStrPpoNo);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("3.) Personal marks of the Pensioner :- Details are enclosed with P. P. O .");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("4.) Height :- Details are enclose with the P. P. O.");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("5.) The pensioner Photo is also enclosed herewith. ");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("6.) The pensioner’s address is furnished below and she may please be informed of the transfer accordingly.");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(15) + "Please ackonowledge receipt.");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(89) + "Yours faithfully,");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(94) + "A.P.A.O/ ATO");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrUniqueLetterNo);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("Copy to" + space(2) + lStrPensionerName + space(2) + ", At & Post – " + space(2) + lStrPensionerAddr + space(2));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("with reference to her application dated" + space(2) + DateUtility.getCurrentDate() + space(2) + "for information.");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(94) + "A.P.A.O/ ATO");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("</font></pre></div>");
					lSbPrintString.append((char) 12);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);

				} else {

					lSbPrintString.append("District Treasury Officer");
					lSbPrintString.append(lStrNewLine);
					if (lStrDistrictName != null && lStrDistrictName.length() > 0) {
						lSbPrintString.append("District" + space(2) + lStrDistrictName);
					}
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(30) + "Sub :- Transfer of Pension in r/o" + space(2) + lStrPensionerName + space(2) + " P.P.O No." + space(2) + lStrPpoNo);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(13) + "I am forwarding herewith both halves of P.P.O No." + space(2) + lStrPpoNo + space(2));
					lSbPrintString.append("issued in favour of" + space(2) + lStrPensionerName + space(2) + lStrPayCmsn + space(2) + "Basic Rs.");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(String.valueOf(lBDBasicPensionAmnt.longValue() + "/-" + space(2)));
					lSbPrintString.append("requesting that the payment of the same may be permitted to be transferred from Presidency Treasury to");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrDestTreasuryName + space(2) + "District ," + space(2));
					if (lStrDistrictName != null && lStrDistrictName.length() > 0) {
						lSbPrintString.append(lStrDistrictName);
					}
					lSbPrintString.append(".The transfer of pension is permitted under Read Note No.2 under Rule 222 of the ");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("B. C. S. Rules. The question has been last paid his pension ");

					lSbPrintString.append("Rs. " + String.valueOf(lBDBasicPensionAmnt.longValue() + "/-" + space(2)));
					lSbPrintString.append("and the Temporary increase Relief " + space(2) + String.valueOf(lObjTrnPensionBillDtls.getDaRate().longValue()) + "%" + space(2));
					lSbPrintString.append("Rs. " + String.valueOf(lObjTrnPensionBillDtls.getTiAmount().longValue() + "/- p.m."));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("He has already commuted " + space(2) + "Rs. " + String.valueOf(lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + "/- and the total pension is ");
					lSbPrintString.append("Rs ." + String.valueOf(lBDBasicPensionAmnt.longValue() - lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + "+" + String.valueOf(lObjTrnPensionBillDtls.getTiAmount().longValue()) + " = "
							+ ((lBDBasicPensionAmnt.longValue() - lObjTrnPensionBillDtls.getCvpMonthAmount().longValue()) + lObjTrnPensionBillDtls.getTiAmount().longValue()) + " /- p.m.");
					lSbPrintString.append("upto and for" + space(2) + lStrFromDate + " on " + space(2) + lStrToDate + space(2));
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(" at " + lStrSourceTreasuryName);

					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("The following document are enclosed for necessary action.");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("1.Both halves of P.P.O No." + space(2) + lStrPpoNo);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("2. A single/joint photograph with three specimen signature attested by gazzated officer.");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("The receipt of the documents in question may please be acknowledged. ");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(94) + "A.P.A.O/ ATO");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrUniqueLetterNo);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("Copy to 1.) " + lStrPensionerName + space(2) + ", At & Post – " + space(2) + lStrPensionerAddr + space(2) + "for information.");

					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("2.) " + space(2) + lStrAGAddr);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(space(94) + "A.P.A.O/ ATO");
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append("</font></pre></div>");
					lSbPrintString.append((char) 12);
					lSbPrintString.append(lStrNewLine);
					lSbPrintString.append(lStrNewLine);
				}

				/**************** Print String Ends ************************/

			}

			inputMap.put("DisplayTransferLetterString", lSbDisplayString);
			inputMap.put("PrintTransferLetterString", lSbPrintString);
			objRes.setResultValue(inputMap);
			objRes.setViewName("printTransferLetterPopUp");

		} catch (Exception e) {

			//e.printStackTrace();
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}

		return objRes;
	}

	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	public ResultObject getTransferDtlsFromReqId(Map<String, Object> inputMap) {

		gLogger.info("In getTransferDtlsFromReqId method.......");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TransferPPODAO lObjTransferPPODAO = null;
		String lStrRequestId = null;
		List lLstTransferDtls = null;
		String lStrOtherStateName = null;
		try {

			setSessionInfo(inputMap);
			lStrRequestId = StringUtility.getParameter("requestId", request);
			lObjTransferPPODAO = new TransferPPODAOImpl(null, serv.getSessionFactory());

			if (lStrRequestId.length() > 0) {
				// check that pensioner code is transfer to other state or
				// transfer to other treasury
				lStrOtherStateName = lObjTransferPPODAO.checkTransferType(null, gStrLocationCode, lStrRequestId);
				lLstTransferDtls = lObjTransferPPODAO.getTransferDtlsFromReqId(lStrRequestId, gStrLocationCode, lStrOtherStateName);
			}

			String lSBStatus = getResponseXMLDocTransferDtlsFromReqId(lLstTransferDtls, gStrLocationCode).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//e.printStackTrace();
		}
		return objRes;
	}

	private StringBuilder getResponseXMLDocTransferDtlsFromReqId(List lLstTransferDtls, String lStrLocationCode) {

		StringBuilder lStrBldXML = new StringBuilder();
		lStrBldXML.append("<XMLDOCTRANSFERDTLS>");
		if (lLstTransferDtls.size() > 0) {
			Iterator itr = lLstTransferDtls.iterator();
			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				lStrBldXML.append("<PnsnrCode>");
				lStrBldXML.append(obj[0].toString().trim());
				lStrBldXML.append("</PnsnrCode>");
				lStrBldXML.append("<PPONo>");
				lStrBldXML.append("<![CDATA[");
				lStrBldXML.append(obj[1].toString().trim());
				lStrBldXML.append("]]>");
				lStrBldXML.append("</PPONo>");
				lStrBldXML.append("<PnsnrName>");
				lStrBldXML.append(obj[2].toString().trim());
				lStrBldXML.append("</PnsnrName>");
				lStrBldXML.append("<LocName>");
				lStrBldXML.append(obj[3].toString().trim());
				lStrBldXML.append("</LocName>");
				lStrBldXML.append("<AgFlag>");
				lStrBldXML.append(obj[4].toString().trim());
				lStrBldXML.append("</AgFlag>");
				lStrBldXML.append("<OtherStateName>");
				if (obj[5] != null) {
					lStrBldXML.append(obj[5].toString().trim());
				} else {
					lStrBldXML.append("null");
				}
				lStrBldXML.append("</OtherStateName>");
				lStrBldXML.append("<ToLocation>");
				if (obj[6] != null) {
					lStrBldXML.append(obj[6].toString().trim());
				} else {
					lStrBldXML.append("null");
				}
				lStrBldXML.append("</ToLocation>");
				lStrBldXML.append("<CaseStatus>");
				lStrBldXML.append(obj[7].toString().trim());
				lStrBldXML.append("</CaseStatus>");
				lStrBldXML.append("<ToLocationName>");
				lStrBldXML.append(obj[8].toString().trim());
				lStrBldXML.append("</ToLocationName>");
				lStrBldXML.append("<TransferDtlsId>");
				lStrBldXML.append(obj[9].toString().trim());
				lStrBldXML.append("</TransferDtlsId>");
				lStrBldXML.append("<CurrLocation>");
				lStrBldXML.append(lStrLocationCode.trim());
				lStrBldXML.append("</CurrLocation>");
				lStrBldXML.append("<AGCircleName>");
				lStrBldXML.append((obj[10] != null) ? obj[10].toString() : "");
				lStrBldXML.append("</AGCircleName>");
				lStrBldXML.append("<AGCircleCode>");
				lStrBldXML.append((obj[11] != null) ? obj[11].toString() : "");
				lStrBldXML.append("</AGCircleCode>");
			}
		} else {
			lStrBldXML.append("<EmptyList>");
			lStrBldXML.append("EmptyList");
			lStrBldXML.append("</EmptyList>");
		}
		lStrBldXML.append("</XMLDOCTRANSFERDTLS>");
		return lStrBldXML;

	}
}
