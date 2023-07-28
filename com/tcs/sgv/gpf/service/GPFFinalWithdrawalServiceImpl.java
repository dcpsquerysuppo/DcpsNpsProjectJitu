package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.tcs.sgv.gpf.dao.GPFFinalWithdrawalDAO;
import com.tcs.sgv.gpf.dao.GPFFinalWithdrawalDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfReq;
import com.tcs.sgv.gpf.valueobject.TrnGpfFinalWithdrawal;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;

public class GPFFinalWithdrawalServiceImpl extends ServiceImpl implements GPFFinalWithdrawalService {
	Log glogger = LogFactory.getLog(getClass());

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

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/gpf/GPFConstants");

	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gLngUserId.toString();
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			glogger.error("Error is :" + e, e);
		}

	}

	public ResultObject saveFinalWithdrawal(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TrnGpfFinalWithdrawal lObjGpfFinalWithDrawal = (TrnGpfFinalWithdrawal) inputMap.get("trnGpfFinalWithDrawal");
		setSessionInfo(inputMap);
		boolean lBlFlag = false;
		Integer iSaveOrUpdate = 0;
		GPFFinalWithdrawalDAO gpfFinalWithdrawalDAO = new GPFFinalWithdrawalDAOImpl(TrnGpfFinalWithdrawal.class, serv
				.getSessionFactory());

		try {
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");

			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			Map attachMap = (Map) resObj.getResultValue();

			Long lLngAttachId = 0L;
			if (attachMap.get("AttachmentId_Proof2") != null) {
				lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Proof2")));
				lObjGpfFinalWithDrawal.setAttachmentId(lLngAttachId);
			}
			if (iSaveOrUpdate == 1) {
				Long lLngGpfFinalWithdrawalId = IFMSCommonServiceImpl.getNextSeqNum("TRN_GPF_FINAL_WITHDRAWAL",
						inputMap);
				lObjGpfFinalWithDrawal.setTrnGpfFinalWithdrawalId(lLngGpfFinalWithdrawalId);
				gpfFinalWithdrawalDAO.create(lObjGpfFinalWithDrawal);
			} else {
				gpfFinalWithdrawalDAO.update(lObjGpfFinalWithDrawal);
			}

			lBlFlag = true;

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForFinalWithdrawal(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject forwardFinalWithdrawalDEOApprover(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Integer iSaveOrUpdate = 0;
		Long lLngGpfFinalWithdrawalId = null;
		String lStrSevaarthId = null;
		String lStrGpfAccNo = null;
		String lStrTransactionId = null;
		boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			TrnGpfFinalWithdrawal lObjTrnGpfFinaWithdrawal = (TrnGpfFinalWithdrawal) inputMap
					.get("trnGpfFinalWithDrawal");
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("GPF.DEOAPPROVER");
			String lStrPKValue = StringUtility.getParameter("hidFinalWithdrawalID", request);
			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			lStrGpfAccNo = lObjTrnGpfFinaWithdrawal.getGpfAccNo();

			GPFRequestProcessDAO lObjRequestProcessDAO = new GPFRequestProcessDAOImpl(MstGpfReq.class, serv
					.getSessionFactory());
			lStrTransactionId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
			lObjTrnGpfFinaWithdrawal.setTransactionId(lStrTransactionId);
			lObjTrnGpfFinaWithdrawal.setApproverRemarks("");
			lObjTrnGpfFinaWithdrawal.setHoRemarks("");
			if (lStrPKValue.equals("")) {
				lLngGpfFinalWithdrawalId = IFMSCommonServiceImpl.getNextSeqNum("TRN_GPF_FINAL_WITHDRAWAL", inputMap);
				lStrPKValue = lLngGpfFinalWithdrawalId.toString();
			}
			String lStrUserRemarks = StringUtility.getParameter("textareaUserRemarks", request);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.FinalWithdrawalRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.FinalWithdrawalID")));
			inputMap.put("Pkvalue", lStrPKValue);
			createWF(inputMap);
			WorkFlowDelegate.forward(inputMap);

			GPFFinalWithdrawalDAO gpfFinalWithdrawalDAO = new GPFFinalWithdrawalDAOImpl(TrnGpfFinalWithdrawal.class,
					serv.getSessionFactory());

			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
			if (iSaveOrUpdate == 1) {
				resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

				resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

				Map attachMap = (Map) resObj.getResultValue();

				Long lLngAttachId = 0L;
				if (attachMap.get("AttachmentId_Proof2") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_Proof2")));
					lObjTrnGpfFinaWithdrawal.setAttachmentId(lLngAttachId);
				}
				lObjTrnGpfFinaWithdrawal.setTrnGpfFinalWithdrawalId(lLngGpfFinalWithdrawalId);
				lObjTrnGpfFinaWithdrawal.setReqStatus("F1");
				lObjTrnGpfFinaWithdrawal.setDeoActionDate(gDtCurDate);
				lObjTrnGpfFinaWithdrawal.setUpdatedUserId(gLngUserId);
				lObjTrnGpfFinaWithdrawal.setUpdatedPostId(gLngPostId);
				lObjTrnGpfFinaWithdrawal.setUpdatedDate(gDtCurDate);
				gpfFinalWithdrawalDAO.create(lObjTrnGpfFinaWithdrawal);

			} else {
				lObjTrnGpfFinaWithdrawal = (TrnGpfFinalWithdrawal) gpfFinalWithdrawalDAO.read(Long
						.parseLong(lStrPKValue));

				lObjTrnGpfFinaWithdrawal.setReqStatus("F1");
				lObjTrnGpfFinaWithdrawal.setUserRemarks(lStrUserRemarks);
				lObjTrnGpfFinaWithdrawal.setUpdatedUserId(gLngUserId);
				lObjTrnGpfFinaWithdrawal.setUpdatedPostId(gLngPostId);
				lObjTrnGpfFinaWithdrawal.setUpdatedDate(gDtCurDate);
				gpfFinalWithdrawalDAO.update(lObjTrnGpfFinaWithdrawal);
			}
			// insert record in mst_gpF_req
			MstGpfReq lObjMstGpfReq = new MstGpfReq();
			Long lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
			lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
			lObjMstGpfReq.setTransactionId(lStrTransactionId);
			lObjMstGpfReq.setReqDtlId(lObjTrnGpfFinaWithdrawal.getTrnGpfFinalWithdrawalId());
			lObjMstGpfReq.setReqType("FW");
			lObjRequestProcessDAO.create(lObjMstGpfReq);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocWithTransactionId(lBlFlag, lStrTransactionId, lStrSevaarthId, lStrGpfAccNo)
				.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject forwardFinalWithdrawalToHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("GPF.HO");

			String lStrPKValue = StringUtility.getParameter("hidFinalWithdrawalID", request);

			String lStrDeoRemarks = StringUtility.getParameter("txtApproverRemarks", request);

			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.FinalWithdrawalRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.FinalWithdrawalID")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.forward(inputMap);
			GPFFinalWithdrawalDAO gpfFinalWithdrawalDAO = new GPFFinalWithdrawalDAOImpl(TrnGpfFinalWithdrawal.class,
					serv.getSessionFactory());

			TrnGpfFinalWithdrawal lObjTrnGpfFinaWithdrawal = (TrnGpfFinalWithdrawal) gpfFinalWithdrawalDAO.read(Long
					.parseLong(lStrPKValue));
			lObjTrnGpfFinaWithdrawal.setApproverRemarks(lStrDeoRemarks);
			lObjTrnGpfFinaWithdrawal.setReqStatus("F2");
			lObjTrnGpfFinaWithdrawal.setHoReceiveDate(gDtCurDate);
			lObjTrnGpfFinaWithdrawal.setUpdatedUserId(gLngUserId);
			lObjTrnGpfFinaWithdrawal.setUpdatedPostId(gLngPostId);
			lObjTrnGpfFinaWithdrawal.setUpdatedDate(gDtCurDate);
			gpfFinalWithdrawalDAO.update(lObjTrnGpfFinaWithdrawal);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForFinalWithdrawal(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject rejectFinalWithdrawalByDEOApprover(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		try {

			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("hidFinalWithdrawalID", request);
			String lStrRemarks = StringUtility.getParameter("txtApproverRemarks", request);

			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.FinalWithdrawalRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.FinalWithdrawalID")));
			inputMap.put("Pkvalue", lStrPKValue);

			WorkFlowDelegate.returnDoc(inputMap);

			GPFFinalWithdrawalDAO gpfFinalWithdrawalDAO = new GPFFinalWithdrawalDAOImpl(TrnGpfFinalWithdrawal.class,
					serv.getSessionFactory());

			TrnGpfFinalWithdrawal lObjTrnGpfFinaWithdrawal = (TrnGpfFinalWithdrawal) gpfFinalWithdrawalDAO.read(Long
					.parseLong(lStrPKValue));
			lObjTrnGpfFinaWithdrawal.setReqStatus("R");
			lObjTrnGpfFinaWithdrawal.setApproverRemarks(lStrRemarks);
			lObjTrnGpfFinaWithdrawal.setVerifierActionDate(gDtCurDate);
			lObjTrnGpfFinaWithdrawal.setUpdatedUserId(gLngUserId);
			lObjTrnGpfFinaWithdrawal.setUpdatedPostId(gLngPostId);
			lObjTrnGpfFinaWithdrawal.setUpdatedDate(gDtCurDate);
			gpfFinalWithdrawalDAO.update(lObjTrnGpfFinaWithdrawal);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForFinalWithdrawal(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject rejectFinalWithdrawalByHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		try {

			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("hidFinalWithdrawalID", request);
			String lStrRemarks = StringUtility.getParameter("txtHORemarks", request);

			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrRemarks);
			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.FinalWithdrawalRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.FinalWithdrawalID")));
			inputMap.put("Pkvalue", lStrPKValue);

			WorkFlowDelegate.returnDoc(inputMap);

			GPFFinalWithdrawalDAO gpfFinalWithdrawalDAO = new GPFFinalWithdrawalDAOImpl(TrnGpfFinalWithdrawal.class,
					serv.getSessionFactory());

			TrnGpfFinalWithdrawal lObjTrnGpfFinaWithdrawal = (TrnGpfFinalWithdrawal) gpfFinalWithdrawalDAO.read(Long
					.parseLong(lStrPKValue));
			lObjTrnGpfFinaWithdrawal.setReqStatus("R");
			lObjTrnGpfFinaWithdrawal.setHoRemarks(lStrRemarks);
			lObjTrnGpfFinaWithdrawal.setHoActionDate(gDtCurDate);
			lObjTrnGpfFinaWithdrawal.setUpdatedUserId(gLngUserId);
			lObjTrnGpfFinaWithdrawal.setUpdatedPostId(gLngPostId);
			lObjTrnGpfFinaWithdrawal.setUpdatedDate(gDtCurDate);
			gpfFinalWithdrawalDAO.update(lObjTrnGpfFinaWithdrawal);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForFinalWithdrawal(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject approveFinalWithdrawalByHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		String lStrGpfReqId = "";
		MstGpfReq lObjMstGpfReq = new MstGpfReq();
		try {
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("hidFinalWithdrawalID", request);

			String lStrHoRemarks = StringUtility.getParameter("txtHORemarks", request);
			Long lLngOrderNo = Long.parseLong(StringUtility.getParameter("txtOrderNo", request));
			String strOrderDate = StringUtility.getParameter("txtOrderDate", request);

			GPFFinalWithdrawalDAO gpfFinalWithdrawalDAO = new GPFFinalWithdrawalDAOImpl(TrnGpfFinalWithdrawal.class,
					serv.getSessionFactory());
			GPFApprovedRequestDAO lObjGPFApprovedRequestDAO = new GPFApprovedRequestDAOImpl(null, serv
					.getSessionFactory());
			GPFAdvanceProcessDAO lObjAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(MstGpfReq.class, serv
					.getSessionFactory());

			TrnGpfFinalWithdrawal lObjTrnGpfFinaWithdrawal = (TrnGpfFinalWithdrawal) gpfFinalWithdrawalDAO.read(Long
					.parseLong(lStrPKValue));
			lObjTrnGpfFinaWithdrawal.setReqStatus("A");
			lObjTrnGpfFinaWithdrawal.setHoActionDate(gDtCurDate);
			lObjTrnGpfFinaWithdrawal.setHoRemarks(lStrHoRemarks);
			lObjTrnGpfFinaWithdrawal.setOrderNo(lLngOrderNo);
			lObjTrnGpfFinaWithdrawal.setOrderDate(lObjDateFormat.parse(strOrderDate));
			lObjTrnGpfFinaWithdrawal.setUpdatedUserId(gLngUserId);
			lObjTrnGpfFinaWithdrawal.setUpdatedPostId(gLngPostId);
			lObjTrnGpfFinaWithdrawal.setUpdatedDate(gDtCurDate);

			lStrGpfReqId = lObjGPFApprovedRequestDAO.getGpfReqID(lObjTrnGpfFinaWithdrawal.getTransactionId());
			/*lObjMstGpfReq = (MstGpfReq) lObjRequestProcessDAO.read(Long.parseLong(lStrGpfReqId));
			lObjMstGpfReq.setOrderNo(longOrderNo.toString());
			lObjRequestProcessDAO.update(lObjMstGpfReq);*/
			lObjAdvanceProcessDAO.updateOrderNo(lStrGpfReqId, lLngOrderNo.toString());

			gpfFinalWithdrawalDAO.update(lObjTrnGpfFinaWithdrawal);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForFinalWithdrawal(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private void createWF(Map<String, Object> inputMap) {

		try {

			Long PKValue = Long.parseLong(inputMap.get("Pkvalue").toString());
			setSessionInfo(inputMap);

			String subjectName = gObjRsrcBndle.getString("GPF.FinalWithdrawalRequest");
			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrPostId, subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.FinalWithdrawalID")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle.getString("GPF.FinalWithdrawalRequest"));

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			glogger.error("Error is :" + e, e);
		}
	}

	private StringBuilder getResponseXMLDocForFinalWithdrawal(boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

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
