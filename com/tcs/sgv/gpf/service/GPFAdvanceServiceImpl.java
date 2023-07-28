/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	July 08, 2011		Meeta Thacker								
 *******************************************************************************
 */

package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAOImpl;
import com.tcs.sgv.gpf.dao.GPFApprovedRequestDAO;
import com.tcs.sgv.gpf.dao.GPFApprovedRequestDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstEmpGpfAcc;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;
import com.tcs.sgv.gpf.valueobject.MstGpfReq;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;

/**
 * Class Description - contains the services for GPF Advances processes
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 July 08, 2011
 */
public class GPFAdvanceServiceImpl extends ServiceImpl implements GPFAdvanceService {
	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

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
			gStrPostId = gLngPostId.toString();
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
	 * com.tcs.sgv.gpf.service.GPFAdvanceService#forwardAdvanceToDEOApprover
	 * (java.util.Map)
	 */
	public ResultObject forwardAdvanceToDEOApprover(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstGpfAdvance lObjMstGpfAdvance = null;
		Integer iSaveOrUpdate = 0;
		Boolean lBFlag = false;
		Long lLngAdvanceId = null;
		String lStrSevaarthId = null;
		String lStrGpfAccNo = null;
		String lStrTransactionId = null;
		try {
			setSessionInfo(inputMap);
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("GPF.DEOAPPROVER");

			// get the VO created in VOGenerator
			lObjMstGpfAdvance = (MstGpfAdvance) inputMap.get("GPFAdvanceData");
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
			String lStrRequestName = StringUtility.getParameter("requestName", request);
			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			lStrGpfAccNo = lObjMstGpfAdvance.getGpfAccNo();

			GPFAdvanceProcessDAO lObjAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());
			GPFRequestProcessDAO lObjRequestProcessDAO = new GPFRequestProcessDAOImpl(MstGpfReq.class, serv
					.getSessionFactory());
			// get new transaction Id and set it to the VO
			lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
			lObjMstGpfAdvance.setTransactionId(lStrTransactionId);

			// Code For Attachement starts
			Long lObjAttachmentId = null;
			Map attachMap = new HashMap();
			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);
			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			attachMap = (Map) resObj.getResultValue();

			if (attachMap != null) {
				if (lStrRequestName.equals("RA")) {
					if (attachMap.get("AttachmentId_Proof") != null) {
						lObjAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Proof")));
					}
				} else if (lStrRequestName.equals("NRA")) {
					if (attachMap.get("AttachmentId_Proof1") != null) {
						lObjAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Proof1")));
					}
				}

			}
			// Code For Attachement ends

			lObjMstGpfAdvance.setAttachmentId(lObjAttachmentId);
			lObjMstGpfAdvance.setStatusFlag("F1");
			lObjMstGpfAdvance.setDeoActionDate(gDtCurDate);
			lObjMstGpfAdvance.setApproverRemarks("");
			lObjMstGpfAdvance.setVerifierRemarks("");
			if (iSaveOrUpdate == 1) {
				lLngAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ADVANCE", inputMap);
				lObjMstGpfAdvance.setMstGpfAdvanceId(lLngAdvanceId);
				lObjMstGpfAdvance.setSevaarthId(lStrSevaarthId);
				lObjAdvanceProcessDAO.create(lObjMstGpfAdvance);
			} else {
				lLngAdvanceId = lObjMstGpfAdvance.getMstGpfAdvanceId();
				lObjAdvanceProcessDAO.update(lObjMstGpfAdvance);
			}
			//edited by aditya
			inputMap.put("toPost", toPost);
			//inputMap.put("toPost", gStrPostId);
			inputMap.put("toPostId", toPost);
			//inputMap.put("toPostId", gStrPostId);
			
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.AdvanceRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.AdvanceID")));
			inputMap.put("Pkvalue", lLngAdvanceId);

			// create and forward the workflow
			createWF(inputMap);
			WorkFlowDelegate.forward(inputMap);

			// insert record in mst_gpF_req
			MstGpfReq lObjMstGpfReq = new MstGpfReq();
			Long lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
			lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
			lObjMstGpfReq.setTransactionId(lStrTransactionId);
			lObjMstGpfReq.setReqDtlId(lObjMstGpfAdvance.getMstGpfAdvanceId());
			lObjMstGpfReq.setReqType(lObjMstGpfAdvance.getAdvanceType());
			lObjRequestProcessDAO.create(lObjMstGpfReq);
			// end mst_gpf_req entry

			lBFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocWithTransactionId(lBFlag, lStrTransactionId, lStrSevaarthId, lStrGpfAccNo)
				.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.GPFAdvanceService#approveAdvance(java.util.Map)
	 */
	public ResultObject approveAdvance(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBFlag = false;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String lStrGpfAccNo = null;
		Double lDblCurrBal = null;
		Double lDblNewBal = null;
		Double advanceAmount = null;
		String lStrClubbedReqTrnId = null;
		Long lLngClubbedReqPK = null;
		MstGpfAdvance lObjClubbedAdvance = null;
		Integer lIntInstallments = 0;
		String lStrSancAuthorityName = null;
		String lStrGpfReqId = "";
		new MstGpfReq();
		try {
			setSessionInfo(inputMap);

			// get the remarks and order details
			String strRemarks = StringUtility.getParameter("txtHORemarks", request);
			String strOrderNo = StringUtility.getParameter("txtOrderNo", request);
			String strOrderDate = StringUtility.getParameter("txtOrderDate", request);
			Double lDblSancAmount = null;
			Integer lIntSancInst = null;
			Double lDblInstAmount = null;
			Double lDblOddInst = null;
			Double lDblPayableAmt = null;
			Double lDblOutstandingAmt = null;
			String lStrTransactionId = null;

			// get the release installments for HBA non-refundable advance
			String lStrInstallment1 = StringUtility.getParameter("txtInstallment1", request);
			String lStrInstallment2 = StringUtility.getParameter("txtInstallment2", request);
			String lStrInstallment3 = StringUtility.getParameter("txtInstallment3", request);
			String lStrInstallment4 = StringUtility.getParameter("txtInstallment4", request);
			String lStrReleaseDate1 = StringUtility.getParameter("txtReleaseDate1", request);
			String lStrReleaseDate2 = StringUtility.getParameter("txtReleaseDate2", request);
			String lStrReleaseDate3 = StringUtility.getParameter("txtReleaseDate3", request);
			String lStrReleaseDate4 = StringUtility.getParameter("txtReleaseDate4", request);
			String lStrRequestName = StringUtility.getParameter("requestName", request);
			String lStrSancInst = "";

			// get the sanction details for Refundable and non-refundable
			// advance
			if (lStrRequestName.equals("RA")) {
				lStrTransactionId = StringUtility.getParameter("hidTIDRA", request);
				lDblSancAmount = Double.parseDouble(StringUtility.getParameter("txtSancAmount", request));
				lIntSancInst = Integer.parseInt(StringUtility.getParameter("txtSancInstallments", request));
				lDblInstAmount = Double.parseDouble(StringUtility.getParameter("txtInstallmentAmount", request));
				lDblOddInst = Double.parseDouble(StringUtility.getParameter("txtOddInstallmentAmt", request));
				lDblPayableAmt = Double.parseDouble(StringUtility.getParameter("txtPayableAmount", request));
				lDblOutstandingAmt = Double.parseDouble(StringUtility.getParameter("txtOutstandingBalance", request));
			} else if (lStrRequestName.equals("NRA")) {
				lStrTransactionId = StringUtility.getParameter("hidTIDNRA", request);
				lDblSancAmount = Double.parseDouble(StringUtility.getParameter("txtSancAmountNRA", request));
				lStrSancInst = StringUtility.getParameter("txtSancPayInstNo", request);
				if (!lStrSancInst.equalsIgnoreCase("") && lStrSancInst != null) {
					lIntSancInst = Integer.parseInt(lStrSancInst);
				}
				lDblOutstandingAmt = Double
						.parseDouble(StringUtility.getParameter("txtOutstandingBalanceNRA", request));
			}
			// end of sanction details
			GPFAdvanceProcessDAOImpl gpfAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());

			Long lngAdvanceId = gpfAdvanceProcessDAO.getMstAdvanceIdForTransactionId(lStrTransactionId);
			MstGpfAdvance lObjMstGpfAdvance = (MstGpfAdvance) gpfAdvanceProcessDAO.read(lngAdvanceId);
			lObjMstGpfAdvance.setAmountSanctioned(lDblSancAmount);
			lObjMstGpfAdvance.setNoOfInstallments(lIntSancInst);
			lObjMstGpfAdvance.setInstallmentAmount(lDblInstAmount);
			lObjMstGpfAdvance.setOddInstallment(lDblOddInst);
			lObjMstGpfAdvance.setPayableAmount(lDblPayableAmt);
		//	lObjMstGpfAdvance.setOutstandingAmount(lDblOutstandingAmt);
			lObjMstGpfAdvance.setBalance(lDblOutstandingAmt);
			if (!lStrInstallment1.equalsIgnoreCase("") && lStrInstallment1 != null) {
				lObjMstGpfAdvance.setInstallment1(Double.parseDouble(lStrInstallment1));
			}
			if (!lStrInstallment2.equalsIgnoreCase("") && lStrInstallment2 != null) {
				lObjMstGpfAdvance.setInstallment2(Double.parseDouble(lStrInstallment2));
			}
			if (!lStrInstallment3.equalsIgnoreCase("") && lStrInstallment3 != null) {
				lObjMstGpfAdvance.setInstallment3(Double.parseDouble(lStrInstallment3));
			}
			if (!lStrInstallment4.equalsIgnoreCase("") && lStrInstallment4 != null) {
				lObjMstGpfAdvance.setInstallment4(Double.parseDouble(lStrInstallment4));
			}

			if (!lStrReleaseDate1.equalsIgnoreCase("") && lStrReleaseDate1 != null) {
				lObjMstGpfAdvance.setReleaseDate1(simpleDateFormat.parse(lStrReleaseDate1));
			}
			if (!lStrReleaseDate2.equalsIgnoreCase("") && lStrReleaseDate2 != null) {
				lObjMstGpfAdvance.setReleaseDate2(simpleDateFormat.parse(lStrReleaseDate2));
			}
			if (!lStrReleaseDate3.equalsIgnoreCase("") && lStrReleaseDate3 != null) {
				lObjMstGpfAdvance.setReleaseDate3(simpleDateFormat.parse(lStrReleaseDate3));
			}
			if (!lStrReleaseDate4.equalsIgnoreCase("") && lStrReleaseDate4 != null) {
				lObjMstGpfAdvance.setReleaseDate4(simpleDateFormat.parse(lStrReleaseDate4));
			}
			if (!strRemarks.equalsIgnoreCase("") && strRemarks != null) {
				lObjMstGpfAdvance.setApproverRemarks(strRemarks);
			}
			if (!strOrderNo.equalsIgnoreCase("") && strOrderNo != null) {
				lObjMstGpfAdvance.setOrderNo(strOrderNo);
			}
			if (!strOrderDate.equalsIgnoreCase("") && strOrderDate != null) {
				lObjMstGpfAdvance.setOrderDate(simpleDateFormat.parse(strOrderDate));
			}

			lStrGpfAccNo = lObjMstGpfAdvance.getGpfAccNo();
			advanceAmount = lObjMstGpfAdvance.getAmountSanctioned();
			lStrClubbedReqTrnId = lObjMstGpfAdvance.getClubbedReqTrnId();

			// in case of clubbing, settle the clubbed request
			if (lStrClubbedReqTrnId != null) {
				lLngClubbedReqPK = gpfAdvanceProcessDAO.getMstAdvanceIdForTransactionId(lStrClubbedReqTrnId);
				lObjClubbedAdvance = (MstGpfAdvance) gpfAdvanceProcessDAO.read(lLngClubbedReqPK);
				
				///added for outstanding amount
				
				lObjMstGpfAdvance.setOutstandingAmount(lObjClubbedAdvance.getOutstandingAmount());
				lObjMstGpfAdvance.setVoucherDate(lObjClubbedAdvance.getVoucherDate());				
				lObjMstGpfAdvance.setVoucherNo(lObjClubbedAdvance.getVoucherNo());
				
				lObjClubbedAdvance.setInstallmentsLeft(0);
				lObjClubbedAdvance.setRecoveryStatus(1);
				lObjClubbedAdvance.setRecoveredAmount(lObjClubbedAdvance.getAmountSanctioned());
				lObjClubbedAdvance.setOutstandingAmount(0D);
				gpfAdvanceProcessDAO.update(lObjClubbedAdvance);
			}
			// end of clubbing

			// set the recovery details for newly created Refundable advance(RA)
			if (lStrRequestName.equals("RA")) {
				lIntInstallments = lObjMstGpfAdvance.getNoOfInstallments();
				lObjMstGpfAdvance.setInstallmentsLeft(lIntInstallments);
				lObjMstGpfAdvance.setRecoveryStatus(0);
				
				//added by shailesh 
				//lObjMstGpfAdvance.setOutstandingAmount(lObjMstGpfAdvance.getAmountSanctioned());
				//lObjMstGpfAdvance.setOutstandingAmount(lObjMstGpfAdvance.getOutstandingAmount());				lObjMstGpfAdvance.setRecoveredAmount(0D);
			}
			// end of recovery details

			lObjMstGpfAdvance.setStatusFlag("A");
			lObjMstGpfAdvance.setHoActionDate(gDtCurDate);
			lStrSancAuthorityName = gpfAdvanceProcessDAO.getDesignationAndOfficeName(gLngPostId, gStrLocationCode);
			lObjMstGpfAdvance.setSancAuthorityName(lStrSancAuthorityName);
			lObjMstGpfAdvance.setSanctionedDate(gDtCurDate);
			lObjMstGpfAdvance.setUpdatedUserId(gLngUserId);
			lObjMstGpfAdvance.setUpdatedPostId(gLngPostId);
			lObjMstGpfAdvance.setUpdatedDate(gDtCurDate);
			gpfAdvanceProcessDAO.update(lObjMstGpfAdvance);

			// create advance data in payroll module
			/*Long lLngVoucherNo = 123456l;
			Date lDtVoucherDate = gDtCurDate;
			Long lLngAdvanceAccountNo = 212121l;
			if (lStrRequestName.equals("RA")) {
				Map payrollServiceMap = new HashMap();
				payrollServiceMap.put("advTypeId", 55);
				payrollServiceMap.put("advDate", simpleDateFormat.format(gDtCurDate));
				payrollServiceMap.put("advSancOrderNo", strOrderNo);
				payrollServiceMap.put("advSancOrderDate", strOrderDate);
				payrollServiceMap.put("prinIntFlag", "P");
				payrollServiceMap.put("principalAmount", advanceAmount.longValue());
				payrollServiceMap.put("installmentNo", lIntInstallments);
				payrollServiceMap.put("installmentEMI", lDblInstAmount.intValue());
				payrollServiceMap.put("recoveredAmount", 0);
				payrollServiceMap.put("recoveredInstallment", 0);
				payrollServiceMap.put("oddInstallment", lIntInstallments);
				payrollServiceMap.put("oddInstallmentAmount", lDblOddInst.intValue());
				GPFAdvanceProcessDAO lObjGPFAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(null, serv
						.getSessionFactory());
				Long lLngOrgEmpId = lObjGPFAdvanceProcessDAO.getOrgEmpIdForGpfAccNo(lStrGpfAccNo);
				payrollServiceMap.put("orgEmpId", lLngOrgEmpId);
				if (lStrClubbedReqTrnId != null) {
					payrollServiceMap.put("advExistFlag", true);
				} else {
					payrollServiceMap.put("advExistFlag", false);
				}
				payrollServiceMap.put("advVoucherNo", lLngVoucherNo);
				payrollServiceMap.put("advVoucherDate", simpleDateFormat.format(lDtVoucherDate));
				payrollServiceMap.put("advAccountNo", lLngAdvanceAccountNo);

				inputMap.put("payrollServiceMap", payrollServiceMap);

				serv.executeService("insertIntegratedPFAdvances", inputMap);

			}*/
			// end of payroll module entry

			// update current balance of GPF account table mst_emp_gpf_acc
			GPFAdvanceProcessDAO gpfAdvanceProcessDAOForGpfBal = new GPFAdvanceProcessDAOImpl(MstEmpGpfAcc.class, serv
					.getSessionFactory());
			MstEmpGpfAcc lObjMstEmpGpfAcc = (MstEmpGpfAcc) (gpfAdvanceProcessDAOForGpfBal
					.getGPFAccountObjectForAccountNo(lStrGpfAccNo));
			lDblCurrBal = lObjMstEmpGpfAcc.getCurrentBalance();
			lDblNewBal = lDblCurrBal - advanceAmount;
			lObjMstEmpGpfAcc.setCurrentBalance(lDblNewBal);
			gpfAdvanceProcessDAOForGpfBal.update(lObjMstEmpGpfAcc);
			// end of mst_emp_gpf_acc entry

			// set the order number in mst_req_gpf
			GPFApprovedRequestDAO lObjGPFApprovedRequestDAO = new GPFApprovedRequestDAOImpl(null, serv
					.getSessionFactory());
			new GPFRequestProcessDAOImpl(MstGpfReq.class, serv.getSessionFactory());

			lStrGpfReqId = lObjGPFApprovedRequestDAO.getGpfReqID(lObjMstGpfAdvance.getTransactionId());
			/*
			 * lObjMstGpfReq = (MstGpfReq)
			 * lObjRequestProcessDAO.read(Long.parseLong(lStrGpfReqId));
			 * lObjMstGpfReq.setOrderNo(strOrderNo);
			 * lObjRequestProcessDAO.update(lObjMstGpfReq);
			 */

			gpfAdvanceProcessDAO.updateOrderNo(lStrGpfReqId, strOrderNo);
			// end of mst_req_gpf

			lBFlag = true;

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.GPFAdvanceService#forwardAdvanceToHO(java.util
	 * .Map)
	 */
	public ResultObject forwardAdvanceToHO(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBFlag = false;
		try {
			setSessionInfo(inputMap);
			String strDEOComments = StringUtility.getParameter("txtApproverRemarks", request);
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("GPF.HO");
			String lStrRequestName = StringUtility.getParameter("requestName", request);
			String lStrTransactionId = null;
			if (lStrRequestName.equals("RA")) {
				lStrTransactionId = StringUtility.getParameter("hidTIDRA", request);
			} else if (lStrRequestName.equals("NRA")) {
				lStrTransactionId = StringUtility.getParameter("hidTIDNRA", request);
			}

			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.AdvanceRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.AdvanceID")));

			GPFAdvanceProcessDAOImpl gpfRequestProcessDAO = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());

			Long lngAdvanceId = gpfRequestProcessDAO.getMstAdvanceIdForTransactionId(lStrTransactionId);

			inputMap.put("Pkvalue", lngAdvanceId);
			WorkFlowDelegate.forward(inputMap);

			// update verifier remarks and status flag in mst_gpF_advance
			MstGpfAdvance lObjMstGpfAdvance = (MstGpfAdvance) gpfRequestProcessDAO.read(lngAdvanceId);
			if (!strDEOComments.equalsIgnoreCase("") && strDEOComments != null) {
				lObjMstGpfAdvance.setVerifierRemarks(strDEOComments);

			}
			lObjMstGpfAdvance.setStatusFlag("F2");
			lObjMstGpfAdvance.setHoReceiveDate(gDtCurDate);
			lObjMstGpfAdvance.setUpdatedUserId(gLngUserId);
			lObjMstGpfAdvance.setUpdatedUserId(gLngPostId);
			lObjMstGpfAdvance.setUpdatedDate(gDtCurDate);
			gpfRequestProcessDAO.update(lObjMstGpfAdvance);
			// end of mst_gpF_advance updation

			lBFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	/**
	 * @param inputMap
	 */
	private void createWF(Map<String, Object> inputMap) {

		try {

			Long PKValue = Long.parseLong(inputMap.get("Pkvalue").toString());
			setSessionInfo(inputMap);

			String subjectName = gObjRsrcBndle.getString("GPF.AdvanceRequest");
			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrPostId, subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.AdvanceID")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle.getString("GPF.AdvanceRequest"));

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.gpf.service.GPFAdvanceService#saveAdvance(java.util.Map)
	 */
	public ResultObject saveAdvance(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstGpfAdvance lObjMstGpfAdvance = null;
		Boolean lBlFlag = false;
		Integer iSaveOrUpdate = 0;
		try {
			setSessionInfo(inputMap);

			lObjMstGpfAdvance = (MstGpfAdvance) inputMap.get("GPFAdvanceData");
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
			String lStrRequestName = StringUtility.getParameter("requestName", request);
			String lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			GPFAdvanceProcessDAO lObjRequestProcessDAO = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());

			// Code For Attachement starts
			Long lObjAttachmentId = null;
			Map attachMap = new HashMap();
			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);
			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			attachMap = (Map) resObj.getResultValue();

			if (attachMap != null) {
				if (lStrRequestName.equals("RA")) {
					if (attachMap.get("AttachmentId_Proof") != null) {
						lObjAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Proof")));
					}
				} else if (lStrRequestName.equals("NRA")) {
					if (attachMap.get("AttachmentId_Proof1") != null) {
						lObjAttachmentId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Proof1")));
					}
				}
			}
			// Code For Attachement ends

			lObjMstGpfAdvance.setStatusFlag("D");
			lObjMstGpfAdvance.setAttachmentId(lObjAttachmentId);

			if (iSaveOrUpdate == 1) {
				Long lLngAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ADVANCE", inputMap);
				lObjMstGpfAdvance.setMstGpfAdvanceId(lLngAdvanceId);
				lObjMstGpfAdvance.setSevaarthId(lStrSevaarthId);
				
				lObjRequestProcessDAO.create(lObjMstGpfAdvance);
				
			} else {
				lObjRequestProcessDAO.update(lObjMstGpfAdvance);
			}
			lBlFlag = true;

		} catch (Exception ex) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, ex, "Error is: ");
			return resObj;
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
	 * @param inputMap
	 * @return
	 */
	public ResultObject rejectAdvanceByDEOApprover(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBFlag = false;
		try {

			setSessionInfo(inputMap);

			String lStrRequestName = StringUtility.getParameter("requestName", request);
			String lStrTransactionId = null;
			if (lStrRequestName.equals("RA")) {
				lStrTransactionId = StringUtility.getParameter("hidTIDRA", request);
			} else if (lStrRequestName.equals("NRA")) {
				lStrTransactionId = StringUtility.getParameter("hidTIDNRA", request);
			}

			String strDEOComments = StringUtility.getParameter("txtApproverRemarks", request);

			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", strDEOComments);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.AdvanceRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.AdvanceID")));

			// update the mst_gpF_advance entry
			GPFAdvanceProcessDAOImpl gpfRequestProcessDAO = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());

			Long lngAdvanceId = gpfRequestProcessDAO.getMstAdvanceIdForTransactionId(lStrTransactionId);
			inputMap.put("Pkvalue", lngAdvanceId);

			WorkFlowDelegate.returnDoc(inputMap);

			MstGpfAdvance lObjMstGpfAdvance = (MstGpfAdvance) gpfRequestProcessDAO.read(lngAdvanceId);
			if (!strDEOComments.equalsIgnoreCase("") && strDEOComments != null) {
				lObjMstGpfAdvance.setVerifierRemarks(strDEOComments);
			}

			lObjMstGpfAdvance.setStatusFlag("R");
			lObjMstGpfAdvance.setVerifierActionDate(gDtCurDate);
			lObjMstGpfAdvance.setUpdatedUserId(gLngUserId);
			lObjMstGpfAdvance.setUpdatedUserId(gLngPostId);
			lObjMstGpfAdvance.setUpdatedDate(gDtCurDate);
			gpfRequestProcessDAO.update(lObjMstGpfAdvance);
			// end of mst_gpF_advance updation

			lBFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	/**
	 * @param inputMap
	 * @return
	 */
	public ResultObject rejectAdvanceByHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBFlag = false;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {

			setSessionInfo(inputMap);

			String strRemarks = StringUtility.getParameter("txtHORemarks", request);
			String strOrderNo = StringUtility.getParameter("txtOrderNo", request);
			String strOrderDate = StringUtility.getParameter("txtOrderDate", request);
			String lStrRequestName = StringUtility.getParameter("requestName", request);
			String lStrTransactionId = null;
			if (lStrRequestName.equals("RA")) {
				lStrTransactionId = StringUtility.getParameter("hidTIDRA", request);
			} else if (lStrRequestName.equals("NRA")) {
				lStrTransactionId = StringUtility.getParameter("hidTIDNRA", request);
			}

			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", strRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.AdvanceRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.AdvanceID")));

			// update mst_gpF_advance entry
			GPFAdvanceProcessDAOImpl gpfRequestProcessDAO = new GPFAdvanceProcessDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());
			Long lngAdvanceId = gpfRequestProcessDAO.getMstAdvanceIdForTransactionId(lStrTransactionId);
			inputMap.put("Pkvalue", lngAdvanceId);

			WorkFlowDelegate.returnDoc(inputMap);
			MstGpfAdvance lObjMstGpfAdvance = (MstGpfAdvance) gpfRequestProcessDAO.read(lngAdvanceId);
			if (!strRemarks.equalsIgnoreCase("") && strRemarks != null) {
				lObjMstGpfAdvance.setApproverRemarks(strRemarks);
			}
			if (!strOrderNo.equalsIgnoreCase("") && strOrderNo != null) {
				lObjMstGpfAdvance.setOrderNo(strOrderNo);
			}
			if (!strOrderDate.equalsIgnoreCase("") && strOrderDate != null) {
				lObjMstGpfAdvance.setOrderDate(simpleDateFormat.parse(strOrderDate));
			}

			lObjMstGpfAdvance.setStatusFlag("R");
			lObjMstGpfAdvance.setHoActionDate(gDtCurDate);
			lObjMstGpfAdvance.setUpdatedUserId(gLngUserId);
			lObjMstGpfAdvance.setUpdatedPostId(gLngPostId);
			lObjMstGpfAdvance.setUpdatedDate(gDtCurDate);
			gpfRequestProcessDAO.update(lObjMstGpfAdvance);
			// end of mst_gpF_advance updation

			lBFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	/**
	 * @param flag
	 * @param lStrTransId
	 * @param lStrSevaarthId
	 * @param lStrGpfAccNo
	 * @return
	 */
	private StringBuilder getResponseXMLDocWithTransactionId(boolean flag, String lStrTransId, String lStrSevaarthId,
			String lStrGpfAccNo) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<lTransId>");
		lStrBldXML.append(lStrTransId);
		lStrBldXML.append("</lTransId>");
		lStrBldXML.append("<lSevaarthId>");
		lStrBldXML.append(lStrSevaarthId);
		lStrBldXML.append("</lSevaarthId>");
		lStrBldXML.append("<lGpfAccNo>");
		lStrBldXML.append(lStrGpfAccNo);
		lStrBldXML.append("</lGpfAccNo>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}