/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 06, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.ChallanEntryDAO;
import com.tcs.sgv.gpf.dao.ChallanEntryDAOImpl;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAOImpl;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstEmpGpfAcc;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;
import com.tcs.sgv.gpf.valueobject.MstGpfChallanDtls;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Sep 06, 2011
 */
public class ChallanEntryServiceImpl extends ServiceImpl implements ChallanEntryService {
	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/gpf/GPFConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");			
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.ChallanEntryService#loadGPFChallanEntry(java.
	 * util.Map)
	 */
	public ResultObject loadGPFChallanEntry(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List lLstOutstandingAdvance = null;
		try {
			setSessionInfo(inputMap);
			String lStrGpfAccNo = StringUtility.getParameter("txtGpfAcNo", request);
			String lStrChallanType = StringUtility.getParameter("cmbChallanType", request);
			String lStrName = StringUtility.getParameter("txtName", request);
			String lStrGroup = StringUtility.getParameter("txtGroup", request);

			// get pending advance for GPF account number, on 2nd time load
			ChallanEntryDAO lObjChallanEntryDAO = new ChallanEntryDAOImpl(null, serv.getSessionFactory());
			if (lStrGpfAccNo != null && !lStrGpfAccNo.equals("")) {
				lLstOutstandingAdvance = lObjChallanEntryDAO.getPendingAdvance(lStrGpfAccNo);
			}
			inputMap.put("RAdvance", lLstOutstandingAdvance);
			inputMap.put("ChallanType", lStrChallanType);
			inputMap.put("GpfAccNo", lStrGpfAccNo);
			inputMap.put("Name", lStrName);
			inputMap.put("Group", lStrGroup);
			// end

			// get challan type combo values
			List lLstChallanType = IFMSCommonServiceImpl.getLookupValues("GPF Challan Type", SessionHelper
					.getLangId(inputMap), inputMap);
			inputMap.put("lLstChallanType", lLstChallanType);
			// end

			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFChallanEntry");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getDigiSig");			
		}
		return resObj;

	}

	/**
	 * service method to approve the challan
	 * 
	 * @param inputMap
	 * @return
	 */
	public ResultObject approveGPFChallan(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Boolean lBlFlag = false;

		try {
			setSessionInfo(inputMap);
			String lStrGpfAccNo = StringUtility.getParameter("txtGpfAcNo", request);
			String lStrChallanNo = StringUtility.getParameter("txtChallanNo", request);
			Date lDtChallanDate = lObjDateFormat.parse(StringUtility.getParameter("txtChallanDate", request));
			Long lLngChallanType = Long.parseLong(StringUtility.getParameter("cmbChallanType", request));
			String lStrTID = null;
			Double lDblAmount = 0D;

			// in case the challan type is 'Pre-pay of advance'
			if (lLngChallanType.equals(gObjRsrcBndle.getString("GPF.PREPAYOFADVANCE"))) {
				lStrTID = StringUtility.getParameter("hidTID", request);
				lDblAmount = Double.parseDouble(StringUtility.getParameter("txtPayment", request));
			} // in case the challan type is 'deputation challan'
			else if (lLngChallanType.equals(gObjRsrcBndle.getString("GPF.DEPUTATIONCHALLAN"))) {
				lDblAmount = Double.parseDouble(StringUtility.getParameter("txtAmountDept", request));
			} // in case the challan type is 'excess payment'
			else if (lLngChallanType.equals(gObjRsrcBndle.getString("GPF.EXCESSPAYMENT"))) {
				lStrTID = StringUtility.getParameter("txtTrnId", request);
				lDblAmount = Double.parseDouble(StringUtility.getParameter("txtExcessPay", request));
			}

			GPFAdvanceProcessDAO gpfAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());
			MstGpfAdvance lObjMstGpfAdvance = null;
			Long lLngAdvanceReqPK = gpfAdvanceProcessDAO.getMstAdvanceIdForTransactionId(lStrTID);

			if (lLngAdvanceReqPK == null && lLngChallanType.equals(gObjRsrcBndle.getString("GPF.EXCESSPAYMENT"))) {
				lBlFlag = false;
			} else {
				// entry in Mst_gpf_challan_dtls entry
				ChallanEntryDAO lObjChallanEntryDAO = new ChallanEntryDAOImpl(MstGpfChallanDtls.class, serv
						.getSessionFactory());
				MstGpfChallanDtls lObjMstGpfChallanDtls = new MstGpfChallanDtls();
				Long lLngMstGpfChallanDtlsId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_CHALLAN_DTLS", inputMap);
				lObjMstGpfChallanDtls.setMstGpfChallanDtlsId(lLngMstGpfChallanDtlsId);
				lObjMstGpfChallanDtls.setChallanType(lLngChallanType);
				lObjMstGpfChallanDtls.setGpfAccNo(lStrGpfAccNo);
				lObjMstGpfChallanDtls.setChallanNo(lStrChallanNo);
				lObjMstGpfChallanDtls.setChallanDate(lDtChallanDate);
				lObjMstGpfChallanDtls.setAmount(lDblAmount);
				if (lLngChallanType == 1 || lLngChallanType == 3) {
					lObjMstGpfChallanDtls.setAdvanceTrnId(lStrTID);
				}
				lObjMstGpfChallanDtls.setCreatedDate(gDtCurDate);
				lObjMstGpfChallanDtls.setCreatedPostId(gLngPostId);
				lObjMstGpfChallanDtls.setCreatedUserId(gLngUserId);
				lObjChallanEntryDAO.create(lObjMstGpfChallanDtls);
				// end of mst_gpf_challan_dtls entry

				// update mst_gpF_advance
				if (lLngAdvanceReqPK != null && lLngChallanType.equals(gObjRsrcBndle.getString("GPF.PREPAYOFADVANCE"))) {
					lObjMstGpfAdvance = (MstGpfAdvance) gpfAdvanceProcessDAO.read(lLngAdvanceReqPK);
					Double lDblOutstandingAmt = lObjMstGpfAdvance.getOutstandingAmount();
					Double lDblRecoveredAmt = lObjMstGpfAdvance.getRecoveredAmount() + lDblAmount;
					Double lDblInstallmentAmt = lObjMstGpfAdvance.getInstallmentAmount();
					Integer lIntInstallmentLeft = lObjMstGpfAdvance.getInstallmentsLeft();
					if (lDblOutstandingAmt == lDblAmount) {
						lObjMstGpfAdvance.setInstallmentsLeft(0);
						lObjMstGpfAdvance.setRecoveryStatus(1);
					} else {
						Double lDblInstallmentNo = lDblAmount / lDblInstallmentAmt;
						lObjMstGpfAdvance.setInstallmentsLeft(lIntInstallmentLeft - lDblInstallmentNo.intValue());
					}
					lObjMstGpfAdvance.setRecoveredAmount(lDblRecoveredAmt);
					lObjMstGpfAdvance.setOutstandingAmount(lDblOutstandingAmt - lDblAmount);
					gpfAdvanceProcessDAO.update(lObjMstGpfAdvance);
				}
				// end of mst_gpF_advance updation

				// update mst_emp_gpf_acc
				if (lLngChallanType.equals(gObjRsrcBndle.getString("GPF.PREPAYOFADVANCE"))
						|| lLngChallanType.equals(gObjRsrcBndle.getString("GPF.DEPUTATIONCHALLAN"))) {
					GPFAdvanceProcessDAO gpfAdvanceProcessDAOForGpfBal = new GPFAdvanceProcessDAOImpl(
							MstEmpGpfAcc.class, serv.getSessionFactory());
					MstEmpGpfAcc lObjMstEmpGpfAcc = (MstEmpGpfAcc) (gpfAdvanceProcessDAOForGpfBal
							.getGPFAccountObjectForAccountNo(lStrGpfAccNo));
					Double lDblCurrBal = lObjMstEmpGpfAcc.getCurrentBalance();
					Double lDblNewBal = lDblCurrBal + lDblAmount;
					lObjMstEmpGpfAcc.setCurrentBalance(lDblNewBal);
					gpfAdvanceProcessDAOForGpfBal.update(lObjMstEmpGpfAcc);
				}
				// end of mst_emp_gpf_acc updation
				lBlFlag = true;
			}
		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, ex, "Error Is: ");
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	/**
	 * @param flag
	 * @return
	 */
	private StringBuilder getResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	/**
	 * service method to populate the employee data in jsp when user enters the
	 * GPF account number
	 * 
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public ResultObject populateEmployeeDataUsingAjax(Map<String, Object> inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String lStrEmpName = "";
		String lStrGroup = "";
		List lLstEmpList = null;
		Date lDtJoiningDate = null;
		Date lDtSuperAnnDate = null;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {

			setSessionInfo(inputMap);

			String lStrGpfAccNo = StringUtility.getParameter("gpfAccNo", request).trim();

			ChallanEntryDAO lObjChallanEntryDAO = new ChallanEntryDAOImpl(null, serv.getSessionFactory());
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serv
					.getSessionFactory());
			String lStrDdoCode = lObjScheduleGenerationDAO.getDdoCodeForDDO(gLngPostId);
			lLstEmpList = lObjChallanEntryDAO.getEmployeeData(lStrGpfAccNo, lStrDdoCode);

			if (lLstEmpList != null && !lLstEmpList.isEmpty()) {
				Object[] empData = (Object[]) lLstEmpList.get(0);
				lStrEmpName = empData[0].toString();
				lStrGroup = empData[1].toString();
				lDtJoiningDate = (Date) empData[2];
				lDtSuperAnnDate = (Date) empData[3];
			}
		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, ex, "Error Is: ");
			return objRes;
		}

		String lSBStatus = getResponseXMLDocForGroup(lStrEmpName, lStrGroup, lObjDateFormat.format(lDtJoiningDate),
				lObjDateFormat.format(lDtSuperAnnDate)).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	/**
	 * @param lStrEmpName
	 * @param lStrGroup
	 * @param lDtJoiningDate
	 * @param lDtSuperAnnDate
	 * @return
	 */
	private StringBuilder getResponseXMLDocForGroup(String lStrEmpName, String lStrGroup, String lDtJoiningDate,
			String lDtSuperAnnDate) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<txtName>");
		lStrBldXML.append(lStrEmpName);
		lStrBldXML.append("</txtName>");
		lStrBldXML.append("<txtGroup>");
		lStrBldXML.append(lStrGroup);
		lStrBldXML.append("</txtGroup>");
		lStrBldXML.append("<txtSuperAnnAge>");
		lStrBldXML.append(lDtJoiningDate);
		lStrBldXML.append("</txtSuperAnnAge>");
		lStrBldXML.append("<lStrRetiringYear>");
		lStrBldXML.append(lDtSuperAnnDate);
		lStrBldXML.append("</lStrRetiringYear>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
