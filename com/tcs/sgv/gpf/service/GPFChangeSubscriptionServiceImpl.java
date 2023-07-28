package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAO;
import com.tcs.sgv.gpf.dao.GPFChangeSubscriptionDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfEmpSubscription;
import com.tcs.sgv.gpf.valueobject.MstGpfReq;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;

public class GPFChangeSubscriptionServiceImpl extends ServiceImpl implements GPFChangeSubscriptionService {
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

	public ResultObject saveChangeSubscription(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstGpfEmpSubscription lObjGpfChangeSub = (MstGpfEmpSubscription) inputMap.get("mstGpfEmpSubscription");
		setSessionInfo(inputMap);
		boolean lBlFlag = false;
		Integer lIncreDecreFlag = 0;
		Integer iSaveOrUpdate = 0;
		GPFChangeSubscriptionDAO gpfChangeSubscriptionDAO = new GPFChangeSubscriptionDAOImpl(
				MstGpfEmpSubscription.class, serv.getSessionFactory());

		try {
			Integer lIntFinYearId = (Integer) inputMap.get("finYearId");
			Boolean flagChangeSub = (Boolean) inputMap.get("changeSubsType");
			String lStrGpfAccNo = StringUtility.getParameter("txtGpfAccNo", request);
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
			Long lLngYearCount = gpfChangeSubscriptionDAO.getFinancialYearCount(lStrGpfAccNo, lIntFinYearId);
			Long lLngIncrement = gpfChangeSubscriptionDAO.getFinancialYearDtlIncrement(lStrGpfAccNo, lIntFinYearId);
			Long lLngDecrement = gpfChangeSubscriptionDAO.getFinancialYearDtlDecrement(lStrGpfAccNo, lIntFinYearId);

			if (lLngYearCount >= 3) {
				lIncreDecreFlag = 1;
			} else if (flagChangeSub && lLngIncrement >= 2) {
				lIncreDecreFlag = 1;
			} else if (!flagChangeSub && lLngDecrement >= 1) {
				lIncreDecreFlag = 1;
			} else {
				if (iSaveOrUpdate == 1) {
					Long lLngGpfChangeSubId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_EMP_SUBSCRIPTION", inputMap);
					lObjGpfChangeSub.setGpfEmpSubscriptionId(lLngGpfChangeSubId);

					gpfChangeSubscriptionDAO.create(lObjGpfChangeSub);
				} else {
					gpfChangeSubscriptionDAO.update(lObjGpfChangeSub);
				}
				lBlFlag = true;
			}

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForChangeSubs(lBlFlag, lIncreDecreFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject forwardChangeSubToDEOApprover(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Integer iSaveOrUpdate = 0;
		boolean lBlFlag = false;
		String lStrGpfAccNo = "";
		String lStrTransId = "";
		String lStrSevaarthId = "";
		Integer lIncreDecreFlag = 0;
		try {
			setSessionInfo(inputMap);
			MstGpfEmpSubscription lObjGpfChangeSub = (MstGpfEmpSubscription) inputMap.get("mstGpfEmpSubscription");
			GPFChangeSubscriptionDAO gpfChangeSubscriptionDAO = new GPFChangeSubscriptionDAOImpl(
					MstGpfEmpSubscription.class, serv.getSessionFactory());
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("GPF.DEOAPPROVER");

			String lStrPKValue = StringUtility.getParameter("hidChangeSubID", request);
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv
					.getSessionFactory());
			Integer lIntFinYearId = lObjFinancialYearDAO.getFinYearIdByCurDate();
			Boolean flagChangeSub = (Boolean) inputMap.get("changeSubsType");
			lStrGpfAccNo = StringUtility.getParameter("txtGpfAccNo", request);
			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);

			GPFRequestProcessDAO lObjRequestProcessDAO = new GPFRequestProcessDAOImpl(MstGpfReq.class, serv
					.getSessionFactory());
			lStrTransId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId);
			// lStrTransId = StringUtility.getParameter("txtTID", request);
			Long lLngYearCount = gpfChangeSubscriptionDAO.getFinancialYearCount(lStrGpfAccNo, lIntFinYearId);
			Long lIntIncrement = gpfChangeSubscriptionDAO.getFinancialYearDtlIncrement(lStrGpfAccNo, lIntFinYearId);
			Long lIntDecrement = gpfChangeSubscriptionDAO.getFinancialYearDtlDecrement(lStrGpfAccNo, lIntFinYearId);
			if (lLngYearCount >= 3) {
				lIncreDecreFlag = 1;
			} else if (flagChangeSub && lIntIncrement >= 2) {
				lIncreDecreFlag = 2;
			} else if (!flagChangeSub && lIntDecrement >= 1) {
				lIncreDecreFlag = 3;
			} else {
				if (lStrPKValue.equals("")) {
					Long lLngGpfChangeSubId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_EMP_SUBSCRIPTION", inputMap);
					lObjGpfChangeSub.setGpfEmpSubscriptionId(lLngGpfChangeSubId);

					lStrPKValue = lLngGpfChangeSubId.toString();
					inputMap.put("toPost", toPost);
					inputMap.put("toPostId", toPost);
					inputMap.put("toLevel", toLevel);

					inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.ChangeSubRequest"));
					inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.ChangeSubID")));
					inputMap.put("Pkvalue", lStrPKValue);
					createWF(inputMap);
					WorkFlowDelegate.forward(inputMap);

				} else {
					inputMap.put("toPost", toPost);
					inputMap.put("toPostId", toPost);
					inputMap.put("toLevel", toLevel);

					inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.ChangeSubRequest"));
					inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.ChangeSubID")));
					inputMap.put("Pkvalue", lStrPKValue);
					createWF(inputMap);
					WorkFlowDelegate.forward(inputMap);
				}

				iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
				lObjGpfChangeSub.setTransactionId(lStrTransId);
				lObjGpfChangeSub.setApproverRemarks("");
				lObjGpfChangeSub.setHoRemarks("");
				if (iSaveOrUpdate == 1) {

					lObjGpfChangeSub.setStatusFlag("F1");
					lObjGpfChangeSub.setDeoActionDate(gDtCurDate);
					lObjGpfChangeSub.setUpdatedUserId(gLngUserId);
					lObjGpfChangeSub.setUpdatedPostId(gLngPostId);
					lObjGpfChangeSub.setUpdatedDate(gDtCurDate);
					gpfChangeSubscriptionDAO.create(lObjGpfChangeSub);

				} else {
					lObjGpfChangeSub = (MstGpfEmpSubscription) gpfChangeSubscriptionDAO.read(Long
							.parseLong(lStrPKValue));
					lObjGpfChangeSub.setStatusFlag("F1");
					lObjGpfChangeSub.setDeoActionDate(gDtCurDate);
					lObjGpfChangeSub.setUpdatedUserId(gLngUserId);
					lObjGpfChangeSub.setUpdatedPostId(gLngPostId);
					lObjGpfChangeSub.setUpdatedDate(gDtCurDate);
					gpfChangeSubscriptionDAO.update(lObjGpfChangeSub);

				}
				// insert record in mst_gpF_req
				MstGpfReq lObjMstGpfReq = new MstGpfReq();
				Long lLngGpfReqId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_REQ", inputMap);
				lObjMstGpfReq.setMstGpfReqId(lLngGpfReqId);
				lObjMstGpfReq.setTransactionId(lStrTransId);
				lObjMstGpfReq.setReqDtlId(lObjGpfChangeSub.getGpfEmpSubscriptionId());
				lObjMstGpfReq.setReqType("CS");
				lObjRequestProcessDAO.create(lObjMstGpfReq);
				lBlFlag = true;
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForForwardChangeSubs(lBlFlag, lIncreDecreFlag, lStrTransId, lStrSevaarthId,
				lStrGpfAccNo).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject forwardChangeSubToHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		Integer lIncreDecreFlag = 0;
		try {
			setSessionInfo(inputMap);
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("GPF.HO");

			String lStrPKValue = StringUtility.getParameter("ChangeSubId", request);
			String lStrDeoRemarks = StringUtility.getParameter("deoRemarks", request);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.ChangeSubRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.ChangeSubID")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.forward(inputMap);
			GPFChangeSubscriptionDAO gpfChangeSubscriptionDAO = new GPFChangeSubscriptionDAOImpl(
					MstGpfEmpSubscription.class, serv.getSessionFactory());

			MstGpfEmpSubscription lObjGpfChangeSub = (MstGpfEmpSubscription) gpfChangeSubscriptionDAO.read(Long
					.parseLong(lStrPKValue));
			lObjGpfChangeSub.setStatusFlag("F2");
			lObjGpfChangeSub.setApproverRemarks(lStrDeoRemarks);
			lObjGpfChangeSub.setHoReceiveDate(gDtCurDate);
			lObjGpfChangeSub.setUpdatedUserId(gLngUserId);
			lObjGpfChangeSub.setUpdatedPostId(gLngPostId);
			lObjGpfChangeSub.setUpdatedDate(gDtCurDate);
			gpfChangeSubscriptionDAO.update(lObjGpfChangeSub);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForChangeSubs(lBlFlag, lIncreDecreFlag).toString();
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

			String subjectName = gObjRsrcBndle.getString("GPF.ChangeSubRequest");
			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrPostId, subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.ChangeSubID")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle.getString("GPF.ChangeSubRequest"));

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			glogger.error("Error is: " + e, e);
		}
	}

	public ResultObject approveChangeSubByHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		Integer lIncreDecreFlag = 0;
		List lFutureMonthList = new ArrayList();
		MstGpfEmpSubscription lObjGpfChangeSub = null;
		try {
			new SimpleDateFormat("dd/MM/yyyy");
			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("hidChangeSubID", request);

			String lStrHoRemarks = StringUtility.getParameter("txtHORemarks", request);
			String lStrNewSubAmount = StringUtility.getParameter("txtNewSubscription", request);
			GPFChangeSubscriptionDAO gpfChangeSubscriptionDAO = new GPFChangeSubscriptionDAOImpl(
					MstGpfEmpSubscription.class, serv.getSessionFactory());
			GPFChangeSubscriptionDAO gpfChangeSubscriptionDAO2 = new GPFChangeSubscriptionDAOImpl(
					MstGpfEmpSubscription.class, serv.getSessionFactory());

			lObjGpfChangeSub = (MstGpfEmpSubscription) gpfChangeSubscriptionDAO.read(Long.parseLong(lStrPKValue));
			lObjGpfChangeSub.setStatusFlag("A");

			MstGpfEmpSubscription lObjGpfChangeSub2 = null;
			lFutureMonthList = gpfChangeSubscriptionDAO2.getFutureMonthDtlForNullify(lObjGpfChangeSub.getGpfAccNo(),
					lObjGpfChangeSub.getFinYearId(), lObjGpfChangeSub.getEffectFromMonth());
			if (lFutureMonthList != null && !lFutureMonthList.isEmpty()) {
				for (Integer lInt = 0; lInt < lFutureMonthList.size(); lInt++) {
					Long lLngTemp = (Long) lFutureMonthList.get(lInt);
					lObjGpfChangeSub2 = (MstGpfEmpSubscription) gpfChangeSubscriptionDAO.read(lLngTemp);
					lObjGpfChangeSub2.setActiveFlag(0);
					gpfChangeSubscriptionDAO2.update(lObjGpfChangeSub2);
				}
			}
			lObjGpfChangeSub.setActiveFlag(1);
			lObjGpfChangeSub.setMonthlySubscription(Double.parseDouble(lStrNewSubAmount));
			lObjGpfChangeSub.setHoRemarks(lStrHoRemarks);
			lObjGpfChangeSub.setHoActionDate(gDtCurDate);
			lObjGpfChangeSub.setUpdatedUserId(gLngUserId);
			lObjGpfChangeSub.setUpdatedPostId(gLngPostId);
			lObjGpfChangeSub.setUpdatedDate(gDtCurDate);

			gpfChangeSubscriptionDAO.update(lObjGpfChangeSub);

			// code to update subscription in Payroll table Starts
			// GPFChangeSubscriptionDAO gpfChangeSubscriptionDAO = new
			// GPFChangeSubscriptionDAOImpl(
			// HrPayDeductionDtls.class, serv.getSessionFactory());
			// Long lLngDeductionId =
			// gpfChangeSubscriptionDAO.getDeductionDtlsIdForEmp();
			// Object lObj = gpfChangeSubscriptionDAO.read(arg0);
			//			
			// gpfChangeSubscriptionDAO.update(lObj);
			// end payroll updation

			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForChangeSubs(lBlFlag, lIncreDecreFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject rejectChangeSubByDEOApprover(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		Integer lIncreDecreFlag = 0;

		try {

			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("ChangeSubId", request);

			String lStrRemarks = StringUtility.getParameter("txtDEORemarks", request);

			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.ChangeSubRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.ChangeSubID")));
			inputMap.put("Pkvalue", lStrPKValue);

			WorkFlowDelegate.returnDoc(inputMap);

			GPFChangeSubscriptionDAO gpfChangeSubscriptionDAO = new GPFChangeSubscriptionDAOImpl(
					MstGpfEmpSubscription.class, serv.getSessionFactory());

			MstGpfEmpSubscription lObjMstGpfChangeSub = (MstGpfEmpSubscription) gpfChangeSubscriptionDAO.read(Long
					.parseLong(lStrPKValue));
			lObjMstGpfChangeSub.setStatusFlag("R");
			lObjMstGpfChangeSub.setApproverRemarks(lStrRemarks);
			lObjMstGpfChangeSub.setVerifierActionDate(gDtCurDate);
			lObjMstGpfChangeSub.setUpdatedUserId(gLngUserId);
			lObjMstGpfChangeSub.setUpdatedPostId(gLngPostId);
			lObjMstGpfChangeSub.setUpdatedDate(gDtCurDate);
			gpfChangeSubscriptionDAO.update(lObjMstGpfChangeSub);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDocForChangeSubs(lBlFlag, lIncreDecreFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject rejectChangeSubByHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		try {

			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("ChangeSubId", request);

			String lStrRemarks = StringUtility.getParameter("txtHORemarks", request);

			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrRemarks);
			inputMap.put("jobTitle", gObjRsrcBndle.getString("GPF.ChangeSubRequest"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("GPF.ChangeSubID")));
			inputMap.put("Pkvalue", lStrPKValue);

			WorkFlowDelegate.returnDoc(inputMap);

			GPFChangeSubscriptionDAO gpfChangeSubscriptionDAO = new GPFChangeSubscriptionDAOImpl(
					MstGpfEmpSubscription.class, serv.getSessionFactory());

			MstGpfEmpSubscription lObjMstGpfChangeSub = (MstGpfEmpSubscription) gpfChangeSubscriptionDAO.read(Long
					.parseLong(lStrPKValue));
			lObjMstGpfChangeSub.setStatusFlag("R");
			lObjMstGpfChangeSub.setHoRemarks(lStrRemarks);
			lObjMstGpfChangeSub.setHoActionDate(gDtCurDate);
			lObjMstGpfChangeSub.setUpdatedUserId(gLngUserId);
			lObjMstGpfChangeSub.setUpdatedPostId(gLngPostId);
			lObjMstGpfChangeSub.setUpdatedDate(gDtCurDate);
			gpfChangeSubscriptionDAO.update(lObjMstGpfChangeSub);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	private StringBuilder getResponseXMLDocForForwardChangeSubs(boolean flag, int lIncreDFlag, String lStrTransId,
			String lStrOrgEmpId, String lStrGpfAccNo) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<lIncreDFlag>");
		lStrBldXML.append(lIncreDFlag);
		lStrBldXML.append("</lIncreDFlag>");
		lStrBldXML.append("<lTransId>");
		lStrBldXML.append(lStrTransId);
		lStrBldXML.append("</lTransId>");
		lStrBldXML.append("<lSevaarthId>");
		lStrBldXML.append(lStrOrgEmpId);
		lStrBldXML.append("</lSevaarthId>");
		lStrBldXML.append("<lGpfAccNo>");
		lStrBldXML.append(lStrGpfAccNo);
		lStrBldXML.append("</lGpfAccNo>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForChangeSubs(boolean flag, int lIncreDFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<lIncreDFlag>");
		lStrBldXML.append(lIncreDFlag);
		lStrBldXML.append("</lIncreDFlag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDoc(boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
