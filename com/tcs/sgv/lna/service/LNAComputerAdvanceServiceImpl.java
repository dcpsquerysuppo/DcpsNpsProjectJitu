package com.tcs.sgv.lna.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAComputerAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNARequestProcessDAO;
import com.tcs.sgv.lna.dao.LNARequestProcessDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaDocChecklist;
import com.tcs.sgv.lna.valueobject.MstLnaRequest;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;

public class LNAComputerAdvanceServiceImpl extends ServiceImpl implements LNAComputerAdvanceService {
	Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurDate = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private void setSessionInfo(Map inputMap) {

		request = (HttpServletRequest) inputMap.get("requestObj");
		serv = (ServiceLocator) inputMap.get("serviceLocator");
		gLngPostId = SessionHelper.getPostId(inputMap);
		gStrPostId = gLngPostId.toString();
		gLngUserId = SessionHelper.getUserId(inputMap);	
		gDtCurDate = SessionHelper.getCurDate();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
	}

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public ResultObject saveComputerAdvance(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) inputMap.get("CompAdvance");
		Integer iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
		Boolean lBlFlag = false;
		String lStrSevaarthId = null;
		String lStrRequestType = null;
		String lStrUserType = null;
		Long lLngDocChecklistId = null;
		MstLnaDocChecklist lObjDocChecklist = null;
		LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
		LNARequestProcessDAO lObjCheckListDAO = new LNARequestProcessDAOImpl(MstLnaDocChecklist.class, serv.getSessionFactory());

		try {
			String lStrCheckBoxList = StringUtility.getParameter("CheckBoxList", request);
			String lStrCheckedUncheck = StringUtility.getParameter("CheckedUncheck", request);
			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			lStrRequestType = StringUtility.getParameter("hidRequestType", request);
			lStrUserType = StringUtility.getParameter("hidUserType", request);
			String lStrCompSubType = StringUtility.getParameter("cmbComputerSubType", request);

			List lCheckListPk = lObjCheckListDAO.getChecklistPk(lStrSevaarthId, Long.parseLong(lStrRequestType), Long.parseLong(lStrCompSubType));
			for (Integer lInt = 0; lInt < lCheckListPk.size(); lInt++) {
				Long lLngPkValue = (Long) lCheckListPk.get(lInt);
				lObjDocChecklist = (MstLnaDocChecklist) lObjCheckListDAO.read(lLngPkValue);
				lObjCheckListDAO.delete(lObjDocChecklist);
			}

			String lArrCheckBoxList[] = lStrCheckBoxList.split(",");
			String lArrCheckedUncheck[] = lStrCheckedUncheck.split(",");

			Long lLngPkCntMstLnaDocChecklist = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_doc_checklist", inputMap, lArrCheckBoxList.length);
			for (Integer lInt = 0; lInt < lArrCheckBoxList.length; lInt++) {
				lObjDocChecklist = new MstLnaDocChecklist();
				lLngDocChecklistId = ++lLngPkCntMstLnaDocChecklist;
				lLngDocChecklistId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngDocChecklistId, inputMap);
				lObjDocChecklist.setMstLnaDocChecklistId(lLngDocChecklistId);
				lObjDocChecklist.setChecklistName(lArrCheckBoxList[lInt]);
				lObjDocChecklist.setSevaarthID(lStrSevaarthId);
				lObjDocChecklist.setLnaReqType(Long.parseLong(lStrRequestType));
				lObjDocChecklist.setReqSubType(Long.parseLong(lStrCompSubType));
				lObjDocChecklist.setChecked(lArrCheckedUncheck[lInt]);
				lObjDocChecklist.setCreatedPostId(gLngPostId);
				lObjDocChecklist.setCreatedUserId(gLngUserId);
				lObjDocChecklist.setCreatedDate(gDtCurDate);
				lObjCheckListDAO.create(lObjDocChecklist);
			}
			Map attachMap = new HashMap();

			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);
			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			attachMap = (Map) resObj.getResultValue();

			Long lLngAttachId = 0L;
			//gLogger.info("Attcament Id Computer Advance::" + attachMap.get("AttachmentId_ProofCA"));
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_ProofCA") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_ProofCA")));
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lLngAttachId);
					lObjCompAdvance.setAttachmentId(lLngAttachId);
				}

			}
			lObjCompAdvance.setStatusFlag("D");
			lObjCompAdvance.setFromPostId(gLngPostId.toString());
			lObjCompAdvance.setToPostID(gLngPostId.toString());
			if (iSaveOrUpdate == 1) {
				Long lLngCompAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_comp_advance", inputMap);
				lObjCompAdvance.setComputerAdvanceId(lLngCompAdvanceId);
				lObjComputerAdvanceDAO.create(lObjCompAdvance);
			} else {
				lObjComputerAdvanceDAO.update(lObjCompAdvance);
			}
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getSaveResponseXMLDocForComAdvance(lBlFlag, lStrSevaarthId, lStrRequestType, lStrUserType).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject forwardComAdvanceToDEOVerifier(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Integer iSaveOrUpdate = 0;
		Boolean lBlFlag = false;
		Long lLngCompAdvanceId = null;
		Long lLngRequestId = null;
		String lStrTransId = "";
		String lStrSevaarthId = "";
		MstLnaDocChecklist lObjDocChecklist = null;
		Long lLngDocChecklistId = null;
		try {
			setSessionInfo(inputMap);
			MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) inputMap.get("CompAdvance");
			MstLnaRequest lObjLnaRequest = new MstLnaRequest();
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");

			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.DEOAPPROVER");

			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			String lStrRequestType = StringUtility.getParameter("hidRequestType", request);

			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			lStrTransId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, Long.parseLong(lStrRequestType));

			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			Map attachMap = (Map) resObj.getResultValue();
			//gLogger.info("Attcament Id Computer Advance::" + attachMap.get("AttachmentId_ProofCA"));
			Long lLngAttachId = 0L;
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_ProofCA") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_ProofCA")));
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lLngAttachId);
					lObjCompAdvance.setAttachmentId(lLngAttachId);
				}

			}
			lObjCompAdvance.setStatusFlag("F");
			lObjCompAdvance.setTransactionId(lStrTransId);
			lObjCompAdvance.setFrwrdToRHO("N");
			lObjCompAdvance.setFromPostId(gLngPostId.toString());
			lObjCompAdvance.setToPostID(toPost);
			LNARequestProcessDAO lObjCheckListDAO = new LNARequestProcessDAOImpl(MstLnaDocChecklist.class, serv.getSessionFactory());
			String lStrCheckBoxList = StringUtility.getParameter("CheckBoxList", request);
			String lStrCheckedUncheck = StringUtility.getParameter("CheckedUncheck", request);
			String lStrCompSubType = StringUtility.getParameter("cmbComputerSubType", request);

			List lCheckListPk = lObjCheckListDAO.getChecklistPk(lStrSevaarthId, Long.parseLong(lStrRequestType), Long.parseLong(lStrCompSubType));
			for (Integer lInt = 0; lInt < lCheckListPk.size(); lInt++) {
				Long lLngPkValue = (Long) lCheckListPk.get(lInt);
				lObjDocChecklist = (MstLnaDocChecklist) lObjCheckListDAO.read(lLngPkValue);
				lObjCheckListDAO.delete(lObjDocChecklist);
			}

			String lArrCheckBoxList[] = lStrCheckBoxList.split(",");
			String lArrCheckedUncheck[] = lStrCheckedUncheck.split(",");
			Long lLngPkCntMstLnaDocChecklist = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_doc_checklist", inputMap, lArrCheckBoxList.length);
			for (Integer lInt = 0; lInt < lArrCheckBoxList.length; lInt++) {
				lObjDocChecklist = new MstLnaDocChecklist();
				lLngDocChecklistId = ++lLngPkCntMstLnaDocChecklist;
				lLngDocChecklistId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngDocChecklistId, inputMap);
				lObjDocChecklist.setMstLnaDocChecklistId(lLngDocChecklistId);
				lObjDocChecklist.setChecklistName(lArrCheckBoxList[lInt]);
				lObjDocChecklist.setSevaarthID(lStrSevaarthId);
				lObjDocChecklist.setLnaReqType(Long.parseLong(lStrRequestType));
				lObjDocChecklist.setReqSubType(Long.parseLong(lStrCompSubType));
				lObjDocChecklist.setChecked(lArrCheckedUncheck[lInt]);
				lObjDocChecklist.setCreatedPostId(gLngPostId);
				lObjDocChecklist.setCreatedUserId(gLngUserId);
				lObjDocChecklist.setCreatedDate(gDtCurDate);
				lObjCheckListDAO.create(lObjDocChecklist);
			}

			if (iSaveOrUpdate == 1) {
				lLngCompAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_comp_advance", inputMap);
				lObjCompAdvance.setComputerAdvanceId(lLngCompAdvanceId);
				lObjComputerAdvanceDAO.create(lObjCompAdvance);
			} else {
				lLngCompAdvanceId = lObjCompAdvance.getComputerAdvanceId();
				lObjComputerAdvanceDAO.update(lObjCompAdvance);
			}
			lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
			lObjLnaRequest.setRequestId(lLngRequestId);
			lObjLnaRequest.setTransactionId(lStrTransId);
			lObjLnaRequest.setLoanAdvanceId(lLngCompAdvanceId);
			lObjLnaRequest.setCreatedPostId(gLngPostId);
			lObjLnaRequest.setCreatedUserId(gLngUserId);
			lObjLnaRequest.setCreatedDate(gDtCurDate);
			lObjComputerAdvanceDAO.create(lObjLnaRequest);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lLngCompAdvanceId);
			createWF(inputMap);
			WorkFlowDelegate.forward(inputMap);

			lBlFlag = true;

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lStrCurrDate = lObjSimpleDateFormat.format(gDtCurDate);
		String lSBStatus = getResponseXMLDocForForwardComAdvance(lBlFlag, lStrTransId, lStrSevaarthId, lStrCurrDate).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject forwardComAdvanceToHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.HO");

			String lStrPKValue = StringUtility.getParameter("hidComAdvanceId", request);
			String lStrDeoRemarks = StringUtility.getParameter("txtApproverRemarksCA", request);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.forward(inputMap);
			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(Long.parseLong(lStrPKValue));
			lObjCompAdvance.setStatusFlag("F");
			lObjCompAdvance.setApproverRemarks(lStrDeoRemarks);
			lObjCompAdvance.setUpdatedUserId(gLngUserId);
			lObjCompAdvance.setUpdatedPostId(gLngPostId);
			lObjCompAdvance.setUpdatedDate(gDtCurDate);
			
			lObjCompAdvance.setFromPostId(gLngPostId.toString());
			lObjCompAdvance.setToPostID(toPost);
			lObjCompAdvance.setFrwrdToRHO("N");
			
			lObjComputerAdvanceDAO.update(lObjCompAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForComAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

		/////RHO
	public ResultObject forwardComAdvanceToAsstHOD(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			//String toLocation = StringUtility.getParameter("toLocation", request).toString();
			//gLogger.info("toLocation "+toLocation);
			
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.ASSTHOD");
			String frwrdToHRO = StringUtility.getParameter("frwrdToHRO", request).toString();
			
			String lStrPKValue = StringUtility.getParameter("hidComAdvanceId", request);
			String lStrHoRemarks = StringUtility.getParameter("txtHORemarks", request);
			String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateCA", request);
			String lStrSancAmount = StringUtility.getParameter("txtSancAmountCA", request);
			String lStrSancInstallments = StringUtility.getParameter("txtSancInstallmentsCA", request);
			String lStrInstallmentAmount = StringUtility.getParameter("txtInstallmentAmountCA", request);
			String lStrOddInstallmentAmt = StringUtility.getParameter("txtOddInstallmentAmtCA", request);
			String lStrOddInstallNo = StringUtility.getParameter("cmbOddInstallNoCA", request);
			
			insertHROInWF(toPost,inputMap,gStrLocationCode);
			
			
			//inputMap.put("toLocation", toLocation);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.forward(inputMap);
			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(Long.parseLong(lStrPKValue));
			
			if(frwrdToHRO == null || !frwrdToHRO.equals("Yes") || frwrdToHRO.equals("")){
			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjCompAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrSancAmount.trim())) {
				lObjCompAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
			}
			if (!"".equals(lStrSancInstallments.trim())) {
				lObjCompAdvance.setSancInstallments(Integer.parseInt(lStrSancInstallments));
			}
			if (!"".equals(lStrInstallmentAmount.trim())) {
				lObjCompAdvance.setInstallmentAmount(Long.parseLong(lStrInstallmentAmount));
			}
			if (!"".equals(lStrOddInstallmentAmt.trim())) {
				lObjCompAdvance.setOddInstallment(Long.parseLong(lStrOddInstallmentAmt));
			} else {
				lObjCompAdvance.setOddInstallment(null);
			}
			if (!(lStrOddInstallNo.equals("-1"))) {
				lObjCompAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddInstallNo));
			}
			if (!"".equals(lStrHoRemarks.trim())) {
				lObjCompAdvance.setHoRemarks(lStrHoRemarks);
			}
			}
			lObjCompAdvance.setStatusFlag("F");
			lObjCompAdvance.setUpdatedUserId(gLngUserId);
			lObjCompAdvance.setUpdatedPostId(gLngPostId);
			lObjCompAdvance.setUpdatedDate(gDtCurDate);
			
			lObjCompAdvance.setFromPostId(gLngPostId.toString());
			lObjCompAdvance.setToPostID(toPost);
			
			lObjCompAdvance.setFrwrdToRHO("Y"); ///frewrded to RHO 
			lObjComputerAdvanceDAO.update(lObjCompAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForComAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject forwardComAdvanceToHOD(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			
			String toPost = null;
			String frmRHO = StringUtility.getParameter("frmRHO", request);
			if((frmRHO != "" || frmRHO != null ) && frmRHO.equals("Yes")){					
				String LNANextPostId = null;
				String  lStrRequestType = null;
					List UserList = new LNARequestProcessServiceImpl().getHierarchyUsers(inputMap,"800028");
					toPost = UserList.get(0).toString();				
			}
			else 	
			 toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String lStrPKValue = StringUtility.getParameter("hidComAdvanceId", request);
			String toLevel = gObjRsrcBndle.getString("LNA.HOD");

			if (!"".equals(lStrPKValue.trim())) {
				inputMap.put("toPost", toPost);
				inputMap.put("toPostId", toPost);
				inputMap.put("toLevel", toLevel);

				inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));
				inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
				inputMap.put("Pkvalue", lStrPKValue);
				WorkFlowDelegate.forward(inputMap);
				LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
				MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(Long.parseLong(lStrPKValue));

				lObjCompAdvance.setStatusFlag("F");
				lObjCompAdvance.setFrwrdToRHO("FromRHO");
				lObjCompAdvance.setUpdatedUserId(gLngUserId);
				lObjCompAdvance.setUpdatedPostId(gLngPostId);
				lObjCompAdvance.setUpdatedDate(gDtCurDate);
				
				lObjCompAdvance.setFromPostId(gLngPostId.toString());
				lObjCompAdvance.setToPostID(toPost);
				
				lObjComputerAdvanceDAO.update(lObjCompAdvance);
				lBlFlag = true;
			}

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForComAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject approveComputerAdvance(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String lStrPKValue = StringUtility.getParameter("hidComAdvanceId", request);
			String lStrHoRemarks = StringUtility.getParameter("txtHORemarks", request);
			String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateCA", request);
			String lStrSancAmount = StringUtility.getParameter("txtSancAmountCA", request);
			String lStrSancInstallments = StringUtility.getParameter("txtSancInstallmentsCA", request);
			String lStrInstallmentAmount = StringUtility.getParameter("txtInstallmentAmountCA", request);
			String lStrOddInstallmentAmt = StringUtility.getParameter("txtOddInstallmentAmtCA", request);
			String lStrOddInstallNo = StringUtility.getParameter("cmbOddInstallNoCA", request);
			String lStrOrderNo = StringUtility.getParameter("txtOrderNoCA", request);
			String lStrOrderDate = StringUtility.getParameter("txtOrderDateCA", request);
			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(Long.parseLong(lStrPKValue));

			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjCompAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrSancAmount.trim())) {
				lObjCompAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
			}
			if (!"".equals(lStrSancInstallments.trim())) {
				lObjCompAdvance.setSancInstallments(Integer.parseInt(lStrSancInstallments));
			}
			if (!"".equals(lStrInstallmentAmount.trim())) {
				lObjCompAdvance.setInstallmentAmount(Long.parseLong(lStrInstallmentAmount));
			}
			if (!"".equals(lStrOddInstallmentAmt.trim())) {
				lObjCompAdvance.setOddInstallment(Long.parseLong(lStrOddInstallmentAmt));
			} else {
				lObjCompAdvance.setOddInstallment(null);
			}
			if (!(lStrOddInstallNo.equals("-1"))) {
				lObjCompAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddInstallNo));
			}

			if (!"".equals(lStrOrderNo.trim())) {
				lObjCompAdvance.setOrderNo(lStrOrderNo);
			}
			if (!"".equals(lStrOrderDate.trim())) {
				lObjCompAdvance.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate));
			}
			if (!"".equals(lStrHoRemarks.trim())) {
				lObjCompAdvance.setHoRemarks(lStrHoRemarks);
			}
			if (!"".equals(lStrSancInstallments.trim())) {
				lObjCompAdvance.setInstallmentLeft(Integer.parseInt(lStrSancInstallments));
			}
			if (!"".equals(lStrSancAmount.trim())) {
				lObjCompAdvance.setOutstandingAmount(Long.parseLong(lStrSancAmount));
			}
			lObjCompAdvance.setStatusFlag("A");
			lObjCompAdvance.setRecoveryStatus(0);
			lObjCompAdvance.setRecoveredAmount(0L);
			lObjCompAdvance.setHodActionDate(gDtCurDate);
			lObjCompAdvance.setUpdatedUserId(gLngUserId);
			lObjCompAdvance.setUpdatedPostId(gLngPostId);
			lObjCompAdvance.setUpdatedDate(gDtCurDate);

			lObjComputerAdvanceDAO.update(lObjCompAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForComAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject rejectComAdvanceByDEOApprover(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String lStrPKValue = StringUtility.getParameter("ComAdvanceId", request);
			String lStrDeoRemarks = StringUtility.getParameter("DEORemarks", request);
			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrDeoRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.returnDoc(inputMap);
			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());

			MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(Long.parseLong(lStrPKValue));
			lObjCompAdvance.setStatusFlag("R");
			lObjCompAdvance.setToPostID(lObjCompAdvance.getFromPostId());
			lObjCompAdvance.setFromPostId(gStrPostId);
			
			lObjCompAdvance.setApproverRemarks(lStrDeoRemarks);
			lObjCompAdvance.setUpdatedUserId(gLngUserId);
			lObjCompAdvance.setUpdatedPostId(gLngPostId);
			lObjCompAdvance.setUpdatedDate(gDtCurDate);
			lObjComputerAdvanceDAO.update(lObjCompAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForComAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject rejectComAdvanceByHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("ComAdvanceId", request);
			String lStrHoRemarks = StringUtility.getParameter("HORemarks", request);
			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrHoRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.returnDoc(inputMap);
			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());

			MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(Long.parseLong(lStrPKValue));
			lObjCompAdvance.setHodActionDate(gDtCurDate);
			lObjCompAdvance.setStatusFlag("R");
			lObjCompAdvance.setHoRemarks(lStrHoRemarks);
			lObjCompAdvance.setToPostID(lObjCompAdvance.getFromPostId());
			lObjCompAdvance.setFromPostId(gStrPostId);
			lObjCompAdvance.setUpdatedUserId(gLngUserId);
			lObjCompAdvance.setUpdatedPostId(gLngPostId);
			lObjCompAdvance.setUpdatedDate(gDtCurDate);
			lObjComputerAdvanceDAO.update(lObjCompAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForComAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject rejectComAdvanceByRHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("ComAdvanceId", request);
			String lStrHoRemarks = StringUtility.getParameter("HORemarks", request);
			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrHoRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.returnDoc(inputMap);
			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());

			MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) lObjComputerAdvanceDAO.read(Long.parseLong(lStrPKValue));
			lObjCompAdvance.setHodActionDate(gDtCurDate);
			lObjCompAdvance.setStatusFlag("R");
			lObjCompAdvance.setRhoRemarks(lStrHoRemarks);
			lObjCompAdvance.setToPostID(lObjCompAdvance.getFromPostId());
			lObjCompAdvance.setFromPostId(gStrPostId);
			lObjCompAdvance.setUpdatedUserId(gLngUserId);
			lObjCompAdvance.setUpdatedPostId(gLngPostId);
			lObjCompAdvance.setUpdatedDate(gDtCurDate);
			lObjComputerAdvanceDAO.update(lObjCompAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForComAdvance(lBlFlag).toString();
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

			String subjectName = gObjRsrcBndle.getString("LNA.CompAdvanceOffline");
			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrPostId, subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	private StringBuilder getResponseXMLDocForComAdvance(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	private StringBuilder getSaveResponseXMLDocForComAdvance(Boolean flag, String lStrSevaarthID, String lStrUserType, String lStrReqType) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<SevaarthID>");
		lStrBldXML.append(lStrSevaarthID);
		lStrBldXML.append("</SevaarthID>");
		lStrBldXML.append("<ReqType>");
		lStrBldXML.append(lStrReqType);
		lStrBldXML.append("</ReqType>");
		lStrBldXML.append("<UserType>");
		lStrBldXML.append(lStrUserType);
		lStrBldXML.append("</UserType>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForForwardComAdvance(Boolean flag, String lStrTransId, String lStrOrgEmpId, String lStrCurrDate) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<lTransId>");
		lStrBldXML.append(lStrTransId);
		lStrBldXML.append("</lTransId>");
		lStrBldXML.append("<lSevaarthId>");
		lStrBldXML.append(lStrOrgEmpId);
		lStrBldXML.append("</lSevaarthId>");
		lStrBldXML.append("<lCurrDate>");
		lStrBldXML.append(lStrCurrDate);
		lStrBldXML.append("</lCurrDate>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject forwardOfflineEntryCAToHOD(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Integer iSaveOrUpdate = 0;
		Boolean lBlFlag = false;
		Long lLngCompAdvanceId = null;
		Long lLngRequestId = null;
		String lStrTransId = "";
		String lStrSevaarthId = "";
		MstLnaDocChecklist lObjDocChecklist = null;
		Long lLngDocChecklistId = null;
		try {
			setSessionInfo(inputMap);
			MstLnaCompAdvance lObjCompAdvance = (MstLnaCompAdvance) inputMap.get("CompAdvance");
			MstLnaRequest lObjLnaRequest = new MstLnaRequest();
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");

			LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.HOD2");

			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			String lStrRequestType = StringUtility.getParameter("hidRequestType", request);

			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
			lStrTransId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, Long.parseLong(lStrRequestType));

			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			Map attachMap = (Map) resObj.getResultValue();

			Long lLngAttachId = 0L;
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_ProofCA") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_ProofCA")));
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lLngAttachId);
					lObjCompAdvance.setAttachmentId(lLngAttachId);
				}

			}

			lObjCompAdvance.setStatusFlag("F");
			lObjCompAdvance.setTransactionId(lStrTransId);

			LNARequestProcessDAO lObjCheckListDAO = new LNARequestProcessDAOImpl(MstLnaDocChecklist.class, serv.getSessionFactory());
			String lStrCheckBoxList = StringUtility.getParameter("CheckBoxList", request);
			String lStrCheckedUncheck = StringUtility.getParameter("CheckedUncheck", request);
			String lStrCompSubType = StringUtility.getParameter("cmbComputerSubType", request);

			List lCheckListPk = lObjCheckListDAO.getChecklistPk(lStrSevaarthId, Long.parseLong(lStrRequestType), Long.parseLong(lStrCompSubType));
			for (Integer lInt = 0; lInt < lCheckListPk.size(); lInt++) {
				Long lLngPkValue = (Long) lCheckListPk.get(lInt);
				lObjDocChecklist = (MstLnaDocChecklist) lObjCheckListDAO.read(lLngPkValue);
				lObjCheckListDAO.delete(lObjDocChecklist);
			}

			String lArrCheckBoxList[] = lStrCheckBoxList.split(",");
			String lArrCheckedUncheck[] = lStrCheckedUncheck.split(",");
			Long lLngPkCntMstLnaDocChecklist = IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("mst_lna_doc_checklist", inputMap, lArrCheckBoxList.length);
			for (Integer lInt = 0; lInt < lArrCheckBoxList.length; lInt++) {
				lObjDocChecklist = new MstLnaDocChecklist();
				lLngDocChecklistId = ++lLngPkCntMstLnaDocChecklist;
				lLngDocChecklistId = IFMSCommonServiceImpl.getFormattedPrimaryKey(lLngDocChecklistId, inputMap);
				lObjDocChecklist.setMstLnaDocChecklistId(lLngDocChecklistId);
				lObjDocChecklist.setChecklistName(lArrCheckBoxList[lInt]);
				lObjDocChecklist.setSevaarthID(lStrSevaarthId);
				lObjDocChecklist.setLnaReqType(Long.parseLong(lStrRequestType));
				lObjDocChecklist.setReqSubType(Long.parseLong(lStrCompSubType));
				lObjDocChecklist.setChecked(lArrCheckedUncheck[lInt]);
				lObjDocChecklist.setCreatedPostId(gLngPostId);
				lObjDocChecklist.setCreatedUserId(gLngUserId);
				lObjDocChecklist.setCreatedDate(gDtCurDate);
				lObjCheckListDAO.create(lObjDocChecklist);
			}

			lObjCompAdvance.setHodasstActionDate(gDtCurDate);
			if (iSaveOrUpdate == 1) {
				lLngCompAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_comp_advance", inputMap);
				lObjCompAdvance.setComputerAdvanceId(lLngCompAdvanceId);
				lObjComputerAdvanceDAO.create(lObjCompAdvance);
			} else {
				lLngCompAdvanceId = lObjCompAdvance.getComputerAdvanceId();
				lObjComputerAdvanceDAO.update(lObjCompAdvance);
			}
			lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
			lObjLnaRequest.setRequestId(lLngRequestId);
			lObjLnaRequest.setTransactionId(lStrTransId);
			lObjLnaRequest.setLoanAdvanceId(lLngCompAdvanceId);
			lObjLnaRequest.setAdvanceType(lObjCompAdvance.getAdvanceType());
			lObjLnaRequest.setCreatedPostId(gLngPostId);
			lObjLnaRequest.setCreatedUserId(gLngUserId);
			lObjLnaRequest.setCreatedDate(gDtCurDate);
			lObjComputerAdvanceDAO.create(lObjLnaRequest);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.CompAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lLngCompAdvanceId);
			createWF(inputMap);
			WorkFlowDelegate.forward(inputMap);

			lBlFlag = true;

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lStrCurrDate = lObjSimpleDateFormat.format(gDtCurDate);
		String lSBStatus = getResponseXMLDocForForwardComAdvance(lBlFlag, lStrTransId, lStrSevaarthId, lStrCurrDate).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public void insertHROInWF(String toPost, Map inputMap, String toLocation){

		LNAComputerAdvanceDAO lObjComputerAdvanceDAO = new LNAComputerAdvanceDAOImpl(MstLnaCompAdvance.class, serv.getSessionFactory());
		List lListHierarchyRefIds  = lObjComputerAdvanceDAO.getAllHierarchyRefIdsForLocation(Long.parseLong(gStrLocationCode));
		
		Long lLongHierarchyRefId = null;
		Long lLongHierarchySeqId = null;
		Long lLongCreatedByUserId = null;
		
		try{
		NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(null, serv
				.getSessionFactory());
		lLongCreatedByUserId = lObjNewRegDdoDAO.getUserIdForPostId(gLngPostId);
		for(Integer k = 0; k < lListHierarchyRefIds.size() ; k++ )
		{
			lLongHierarchyRefId = Long.valueOf(lListHierarchyRefIds.get(k).toString());
			if(!(lObjComputerAdvanceDAO.checkEntryInWfHierachyPostMpg(lLongHierarchyRefId,Long.parseLong(toPost))))
			{
				lLongHierarchySeqId = IFMSCommonServiceImpl.getNextSeqNum("wf_hierachy_post_mpg",inputMap);
				lObjComputerAdvanceDAO.insertWfHierachyPostMpg(lLongHierarchySeqId,lLongHierarchyRefId,
						Long.parseLong(toPost),lLongCreatedByUserId,gDtCurDate,Long.parseLong(toLocation));
			}
			
		}
		
		IFMSCommonServiceImpl lObjIFMSCommonServiceImpl = new IFMSCommonServiceImpl();
		lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactory());
		lObjIFMSCommonServiceImpl.clearCacheForMapDDOAsst(serv.getSessionFactorySlave());
		}
		catch(Exception e){
			gLogger.error("error while inserting in wfHierchyPost "+e);
			 e.printStackTrace();
		}
	}
}
