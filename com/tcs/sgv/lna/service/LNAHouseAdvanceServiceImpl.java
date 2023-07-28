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

import au.id.jericho.lib.html.Logger;

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
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAHouseAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNARequestProcessDAO;
import com.tcs.sgv.lna.dao.LNARequestProcessDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaDocChecklist;
import com.tcs.sgv.lna.valueobject.MstLnaHouseAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaRequest;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;

public class LNAHouseAdvanceServiceImpl extends ServiceImpl implements LNAHouseAdvanceService {
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
		gLngUserId.toString();
		gDtCurDate = SessionHelper.getCurDate();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		
	}

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public ResultObject saveHouseAdvance(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		MstLnaHouseAdvance lObjHouseAdvance = (MstLnaHouseAdvance) inputMap.get("HouseAdvance");

		setSessionInfo(inputMap);
		Integer iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
		Boolean lBlFlag = false;
		String lStrSevaarthId = null;
		String lStrRequestType = null;
		String lStrUserType = null;
		MstLnaDocChecklist lObjDocChecklist = null;
		Long lLngDocChecklistId = null;

		LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
		LNARequestProcessDAO lObjCheckListDAO = new LNARequestProcessDAOImpl(MstLnaDocChecklist.class, serv.getSessionFactory());
		try {
			String lStrCheckBoxList = StringUtility.getParameter("CheckBoxList", request);
			String lStrCheckedUncheck = StringUtility.getParameter("CheckedUncheck", request);
			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			lStrRequestType = StringUtility.getParameter("hidRequestType", request);
			lStrUserType = StringUtility.getParameter("hidUserType", request);
			String lStrHouseSubType = StringUtility.getParameter("cmbHBAType", request);

			List lCheckListPk = lObjCheckListDAO.getChecklistPk(lStrSevaarthId, Long.parseLong(lStrRequestType), Long.parseLong(lStrHouseSubType));
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
				lObjDocChecklist.setReqSubType(Long.parseLong(lStrHouseSubType));
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
			//glogger.info("Attachment id House Advance" + attachMap.get("AttachmentId_ProofHBA"));
			Long lLngAttachId = 0L;
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_ProofHBA") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_ProofHBA")));
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lLngAttachId);
					lObjHouseAdvance.setAttachmentId(lLngAttachId);
				}

			}
			lObjHouseAdvance.setToPostID(gStrPostId);
			lObjHouseAdvance.setFromPostId(gStrPostId);
			lObjHouseAdvance.setStatusFlag("D");
			if (iSaveOrUpdate == 1) {
				Long lLngHouseAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_house_advance", inputMap);
				lObjHouseAdvance.setHouseAdvanceId(lLngHouseAdvanceId);
				lObjHouseAdvanceDAO.create(lObjHouseAdvance);
			} else {
				lObjHouseAdvanceDAO.update(lObjHouseAdvance);
			}
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getSaveResponseXMLDocForHouseAdvance(lBlFlag, lStrSevaarthId, lStrRequestType, lStrUserType).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject forwardHouseAdvanceToDEOVerifier(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Integer iSaveOrUpdate = 0;
		Boolean lBlFlag = false;
		String lStrTransId = "";
		String lStrSevaarthId = "";
		Long lLngRequestId = null;
		Long lLngHouseAdvanceId = null;
		MstLnaDocChecklist lObjDocChecklist = null;
		Long lLngDocChecklistId = null;

		try {
			setSessionInfo(inputMap);
			MstLnaHouseAdvance lObjHouseAdvance = (MstLnaHouseAdvance) inputMap.get("HouseAdvance");
			MstLnaRequest lObjLnaRequest = new MstLnaRequest();
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
			LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.DEOAPPROVER");

			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			String lStrRequestType = StringUtility.getParameter("hidRequestType", request);
			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			lStrTransId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, Long.parseLong(lStrRequestType));

			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			Map attachMap = (Map) resObj.getResultValue();
			//glogger.info("Attachment id House Advance" + attachMap.get("AttachmentId_ProofHBA"));
			Long lLngAttachId = 0L;
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_ProofHBA") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_ProofHBA")));
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lLngAttachId);
					lObjHouseAdvance.setAttachmentId(lLngAttachId);
				}

			}

			lObjHouseAdvance.setFromPostId(gStrPostId);
			lObjHouseAdvance.setToPostID(toPost);
			lObjHouseAdvance.setFrwrdToRHO("N");
			lObjHouseAdvance.setStatusFlag("F");
			lObjHouseAdvance.setTransactionId(lStrTransId);

			LNARequestProcessDAO lObjCheckListDAO = new LNARequestProcessDAOImpl(MstLnaDocChecklist.class, serv.getSessionFactory());
			String lStrCheckBoxList = StringUtility.getParameter("CheckBoxList", request);
			String lStrCheckedUncheck = StringUtility.getParameter("CheckedUncheck", request);
			String lStrHouseSubType = StringUtility.getParameter("cmbHBAType", request);

			List lCheckListPk = lObjCheckListDAO.getChecklistPk(lStrSevaarthId, Long.parseLong(lStrRequestType), Long.parseLong(lStrHouseSubType));
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
				lObjDocChecklist.setReqSubType(Long.parseLong(lStrHouseSubType));
				lObjDocChecklist.setChecked(lArrCheckedUncheck[lInt]);
				lObjDocChecklist.setCreatedPostId(gLngPostId);
				lObjDocChecklist.setCreatedUserId(gLngUserId);
				lObjDocChecklist.setCreatedDate(gDtCurDate);
				lObjCheckListDAO.create(lObjDocChecklist);
			}

			if (iSaveOrUpdate == 1) {
				lLngHouseAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_house_advance", inputMap);
				lObjHouseAdvance.setHouseAdvanceId(lLngHouseAdvanceId);
				lObjHouseAdvanceDAO.create(lObjHouseAdvance);
			} else {
				lLngHouseAdvanceId = lObjHouseAdvance.getHouseAdvanceId();
				lObjHouseAdvanceDAO.update(lObjHouseAdvance);
			}
			lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
			lObjLnaRequest.setRequestId(lLngRequestId);
			lObjLnaRequest.setTransactionId(lStrTransId);
			lObjLnaRequest.setLoanAdvanceId(lLngHouseAdvanceId);
			lObjLnaRequest.setAdvanceType(lObjHouseAdvance.getAdvanceType());
			lObjLnaRequest.setCreatedPostId(gLngPostId);
			lObjLnaRequest.setCreatedUserId(gLngUserId);
			lObjLnaRequest.setCreatedDate(gDtCurDate);
			lObjRequestProcessDAO.create(lObjLnaRequest);

			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.HouseAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lLngHouseAdvanceId);
			createWF(inputMap);
			WorkFlowDelegate.forward(inputMap);

			lBlFlag = true;

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lStrCurrDate = lObjSimpleDateFormat.format(gDtCurDate);
		String lSBStatus = getResponseXMLDocForForwardHouseAdvance(lBlFlag, lStrTransId, lStrSevaarthId, lStrCurrDate).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject forwardHouseAdvanceToHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.HO");

			String lStrPKValue = StringUtility.getParameter("hidHouseAdvanceId", request);
			String lStrDeoRemarks = StringUtility.getParameter("txtApproverRemarksHBA", request);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.HouseAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.forward(inputMap);

			LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			MstLnaHouseAdvance lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(Long.parseLong(lStrPKValue));

			lObjHouseAdvance.setFromPostId(gStrPostId);
			lObjHouseAdvance.setToPostID(toPost);
			lObjHouseAdvance.setStatusFlag("F");
			lObjHouseAdvance.setFrwrdToRHO("N");
			lObjHouseAdvance.setApproverRemarks(lStrDeoRemarks);
			lObjHouseAdvance.setUpdatedUserId(gLngUserId);
			lObjHouseAdvance.setUpdatedPostId(gLngPostId);
			lObjHouseAdvance.setUpdatedDate(gDtCurDate);
			lObjHouseAdvanceDAO.update(lObjHouseAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForHouseAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}
///method for RHO
	public ResultObject forwardHouseAdvanceToAsstHOD(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String lStrPKValue = StringUtility.getParameter("hidHouseAdvanceId", request);
			String lStrHoRemarks = StringUtility.getParameter("txtHORemarks", request);
			String lStrOrderNo = StringUtility.getParameter("txtOrderNoHBA", request);
			String lStrOrderDate = StringUtility.getParameter("txtOrderDateHBA", request);

			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String frwrdToHRO = StringUtility.getParameter("frwrdToHRO", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.ASSTHOD");
			
			gLogger.info("toPost "+toPost+" frwrdToHRO "+frwrdToHRO);
			insertHROInWF(toPost,inputMap,gStrLocationCode);
			
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.HouseAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.forward(inputMap);

			
			LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			MstLnaHouseAdvance lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(Long.parseLong(lStrPKValue));

			String lStrHouseSubType = lObjHouseAdvance.getAdvanceSubType().toString();
			if(frwrdToHRO == null || !frwrdToHRO.equals("Yes") || frwrdToHRO.equals("")){
			if (lStrHouseSubType.equals(gObjRsrcBndle.getString("LNA.CONSTRUCTIONOFFLAT"))) {

				String lStrSancAmount = StringUtility.getParameter("txtSanctionedAmountHBA2", request);
				String lStrPrincipalAmount = StringUtility.getParameter("txtPrincipalAmountHBA", request);
				String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountHBA2", request);

				String lStrDisbursement1 = StringUtility.getParameter("txtDisbursement1", request);
				String lStrDisbursement2 = StringUtility.getParameter("txtDisbursement2", request);
				String lStrDisbursement3 = StringUtility.getParameter("txtDisbursement3", request);

				String lStrSancPrinInstall = StringUtility.getParameter("txtSancPrinInstallHBA2", request);
				String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallHBA2", request);
				String lStrPrincipalInstallmentAmt = StringUtility.getParameter("txtPrinInstallmentAmountHBA", request);
				String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountHBA2", request);
				String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtHBA2", request);
				String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtHBA2", request);
				String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoHBA2", request);
				String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoHBA2", request);

				if (!"".equals(lStrSancAmount.trim())) {
					lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
				}
				if (!"".equals(lStrInterestAmount.trim())) {
					lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
				}
				if (!"".equals(lStrPrincipalAmount.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrPrincipalAmount));
				}

				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
				}
				if (!"".equals(lStrSancPrinInstall.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrSancPrinInstall));
				}
				if (!"".equals(lStrPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setPrincipalInstAmtMonth(Long.parseLong(lStrPrincipalInstallmentAmt));
				}
				if (!"".equals(lStrInterInstallmentAmount.trim())) {
					lObjHouseAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
				}
				if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
				}
				if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
				}
				if (!(lStrOddInterestInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
				}
				if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
				}
				if (!"".equals(lStrDisbursement1.trim())) {
					lObjHouseAdvance.setDisbursementOne(Float.parseFloat(lStrDisbursement1));
				}
				if (!"".equals(lStrDisbursement2.trim())) {
					lObjHouseAdvance.setDisbursementTwo(Float.parseFloat(lStrDisbursement2));
				}
				if (!"".equals(lStrDisbursement3.trim())) {
					lObjHouseAdvance.setDisbursementThree(Float.parseFloat(lStrDisbursement3));
				}

			} else if (lStrHouseSubType.equals(gObjRsrcBndle.getString("LNA.PLOATPURCHASE"))) {

				String lStrSancAmount = StringUtility.getParameter("txtSanctionedAmountHBA2", request);
				String lStrPrincipalAmount = StringUtility.getParameter("txtPrincipalAmountHBA", request);
				String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountHBA2", request);
				String lStrDisbursement1 = StringUtility.getParameter("txtDisbursement1", request);
				String lStrDisbursement2 = StringUtility.getParameter("txtDisbursement2", request);
				String lStrDisbursement3 = StringUtility.getParameter("txtDisbursement3", request);
				String lStrDisbursement4 = StringUtility.getParameter("txtDisbursement4", request);
				String lStrSancPrinInstall = StringUtility.getParameter("txtSancPrinInstallHBA2", request);
				String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallHBA2", request);
				String lStrPrincipalInstallmentAmt = StringUtility.getParameter("txtPrinInstallmentAmountHBA", request);
				String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountHBA2", request);
				String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtHBA2", request);
				String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtHBA2", request);
				String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoHBA2", request);
				String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoHBA2", request);

				if (!"".equals(lStrSancAmount.trim())) {
					lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
				}
				if (!"".equals(lStrInterestAmount.trim())) {
					lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
				}
				if (!"".equals(lStrPrincipalAmount.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrPrincipalAmount));
				}

				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
				}
				if (!"".equals(lStrSancPrinInstall.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrSancPrinInstall));
				}
				if (!"".equals(lStrPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setPrincipalInstAmtMonth(Long.parseLong(lStrPrincipalInstallmentAmt));
				}
				if (!"".equals(lStrInterInstallmentAmount.trim())) {
					lObjHouseAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
				}
				if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
				}
				if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
				}
				if (!(lStrOddInterestInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
				}
				if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
				}
				if (!"".equals(lStrDisbursement1.trim())) {
					lObjHouseAdvance.setDisbursementOne(Float.parseFloat(lStrDisbursement1));
				}
				if (!"".equals(lStrDisbursement2.trim())) {
					lObjHouseAdvance.setDisbursementTwo(Float.parseFloat(lStrDisbursement2));
				}
				if (!"".equals(lStrDisbursement3.trim())) {
					lObjHouseAdvance.setDisbursementThree(Float.parseFloat(lStrDisbursement3));
				}
				if (!"".equals(lStrDisbursement4.trim())) {
					lObjHouseAdvance.setDisbursementFour(Float.parseFloat(lStrDisbursement4));
				}
			} else {

				String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateHBA", request);
				String lStrSancAmount = StringUtility.getParameter("txtSanctionedAmountHBA", request);
				String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountHBA", request);
				String lStrSancPrinInstall = StringUtility.getParameter("txtSancPrinInstallHBA", request);
				String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallHBA", request);
				String lStrPrincipalInstallmentAmt = StringUtility.getParameter("txtPrincipalInstallmentAmtHBA", request);
				String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountHBA", request);
				String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtHBA", request);
				String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtHBA", request);
				String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoHBA", request);
				String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoHBA", request);

				if (!"".equals(lStrSanctionedDate.trim())) {
					lObjHouseAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
				}
				if (!"".equals(lStrSancAmount.trim())) {
					lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
				}
				if (!"".equals(lStrInterestAmount.trim())) {
					lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
				}
				if (!"".equals(lStrSancPrinInstall.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrSancPrinInstall));
				}
				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
				}
				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
				}
				if (!"".equals(lStrPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setPrincipalInstAmtMonth(Long.parseLong(lStrPrincipalInstallmentAmt));
				}
				if (!"".equals(lStrInterInstallmentAmount.trim())) {
					lObjHouseAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
				}
				if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
				}
				if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
				}
				if (!(lStrOddInterestInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
				}
				if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
				}

			}
			if (!"".equals(lStrOrderNo.trim())) {
				lObjHouseAdvance.setOrderNo(lStrOrderNo);
			}
			if (!"".equals(lStrOrderDate.trim())) {
				lObjHouseAdvance.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate));
			}
			if (!"".equals(lStrHoRemarks.trim())) {
				lObjHouseAdvance.setHoRemarks(lStrHoRemarks);
			}
			}
			lObjHouseAdvance.setFromPostId(gStrPostId);
			lObjHouseAdvance.setToPostID(toPost);
			
			lObjHouseAdvance.setFrwrdToRHO("Y");
			lObjHouseAdvance.setStatusFlag("F");
			lObjHouseAdvance.setUpdatedUserId(gLngUserId);
			lObjHouseAdvance.setUpdatedPostId(gLngPostId);
			lObjHouseAdvance.setUpdatedDate(gDtCurDate);

			lObjHouseAdvanceDAO.update(lObjHouseAdvance);
			
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForHouseAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject forwardHouseAdvanceToHOD(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			
			String toPost = null;
			String frmRHO = StringUtility.getParameter("frmRHO", request);
			if((frmRHO != "" || frmRHO != null ) && frmRHO.equals("Yes")){				
				String LNANextPostId = null;
				String  lStrRequestType = null;
					List UserList = new LNARequestProcessServiceImpl().getHierarchyUsers(inputMap,"800029");
					toPost = UserList.get(0).toString();				
			}
			else 			
			 toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.HOD");

			String lStrPKValue = StringUtility.getParameter("hidHouseAdvanceId", request);
			if (!"".equals(lStrPKValue.trim())) {
				inputMap.put("toPost", toPost);
				inputMap.put("toPostId", toPost);
				inputMap.put("toLevel", toLevel);

				inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.HouseAdvanceOffline"));
				inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
				inputMap.put("Pkvalue", lStrPKValue);
				WorkFlowDelegate.forward(inputMap);

				LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
				MstLnaHouseAdvance lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(Long.parseLong(lStrPKValue));

				lObjHouseAdvance.setFromPostId(gStrPostId);
				lObjHouseAdvance.setToPostID(toPost);
				lObjHouseAdvance.setFrwrdToRHO("FromHOD");
				lObjHouseAdvance.setStatusFlag("F");
				lObjHouseAdvance.setUpdatedUserId(gLngUserId);
				lObjHouseAdvance.setUpdatedPostId(gLngPostId);
				lObjHouseAdvance.setUpdatedDate(gDtCurDate);
				lObjHouseAdvanceDAO.update(lObjHouseAdvance);
				lBlFlag = true;
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForHouseAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject approveHouseAdvance(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String lStrPKValue = StringUtility.getParameter("hidHouseAdvanceId", request);
			String lStrHoRemarks = StringUtility.getParameter("txtHORemarks", request);
			String lStrOrderNo = StringUtility.getParameter("txtOrderNoHBA", request);
			String lStrOrderDate = StringUtility.getParameter("txtOrderDateHBA", request);
			LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			MstLnaHouseAdvance lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(Long.parseLong(lStrPKValue));

			String lStrHouseSubType = lObjHouseAdvance.getAdvanceSubType().toString();

			if (lStrHouseSubType.equals(gObjRsrcBndle.getString("LNA.CONSTRUCTIONOFFLAT"))) {

				String lStrSancAmount = StringUtility.getParameter("txtSanctionedAmountHBA2", request);
				String lStrPrincipalAmount = StringUtility.getParameter("txtPrincipalAmountHBA", request);
				String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountHBA2", request);

				String lStrDisbursement1 = StringUtility.getParameter("txtDisbursement1", request);
				String lStrDisbursement2 = StringUtility.getParameter("txtDisbursement2", request);
				String lStrDisbursement3 = StringUtility.getParameter("txtDisbursement3", request);

				String lStrReleaseDate1 = StringUtility.getParameter("txtReleaseDate1", request);
				String lStrReleaseDate2 = StringUtility.getParameter("txtReleaseDate2", request);
				String lStrReleaseDate3 = StringUtility.getParameter("txtReleaseDate3", request);

				String lStrSancPrinInstall = StringUtility.getParameter("txtSancPrinInstallHBA2", request);
				String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallHBA2", request);
				String lStrPrincipalInstallmentAmt = StringUtility.getParameter("txtPrinInstallmentAmountHBA", request);
				String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountHBA2", request);
				String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtHBA2", request);
				String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtHBA2", request);
				String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoHBA2", request);
				String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoHBA2", request);

				if (!"".equals(lStrSancAmount.trim())) {
					lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
				}
				if (!"".equals(lStrInterestAmount.trim())) {
					lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
				}
				if (!"".equals(lStrPrincipalAmount.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrPrincipalAmount));
				}

				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
				}
				if (!"".equals(lStrSancPrinInstall.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrSancPrinInstall));
				}
				if (!"".equals(lStrPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setPrincipalInstAmtMonth(Long.parseLong(lStrPrincipalInstallmentAmt));
				}
				if (!"".equals(lStrInterInstallmentAmount.trim())) {
					lObjHouseAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
				}
				if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
				}
				if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
				}
				if (!(lStrOddInterestInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
				}
				if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
				}
				if (!"".equals(lStrDisbursement1.trim())) {
					lObjHouseAdvance.setDisbursementOne(Float.parseFloat(lStrDisbursement1));
				}
				if (!"".equals(lStrDisbursement2.trim())) {
					lObjHouseAdvance.setDisbursementTwo(Float.parseFloat(lStrDisbursement2));
				}
				if (!"".equals(lStrDisbursement3.trim())) {
					lObjHouseAdvance.setDisbursementThree(Float.parseFloat(lStrDisbursement3));
				}
				if (!"".equals(lStrReleaseDate1.trim())) {
					lObjHouseAdvance.setReleaseDateOne(lObjSimpleDateFormat.parse(lStrReleaseDate1));
					lObjHouseAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrReleaseDate1));
				}
				if (!"".equals(lStrReleaseDate2.trim())) {
					lObjHouseAdvance.setReleaseDateTwo(lObjSimpleDateFormat.parse(lStrReleaseDate2));
				}
				if (!"".equals(lStrReleaseDate3.trim())) {
					lObjHouseAdvance.setReleaseDateThree(lObjSimpleDateFormat.parse(lStrReleaseDate3));
				}
				if (!"".equals(lStrReleaseDate3.trim())) {
					lObjHouseAdvance.setStatusFlag("A");
				} else if (!"".equals(lStrReleaseDate2.trim())) {
					if (Long.parseLong(lStrSancAmount) == Float.parseFloat(lStrDisbursement1) + Float.parseFloat(lStrDisbursement2)) {
						lObjHouseAdvance.setStatusFlag("A");
					} else {
						lObjHouseAdvance.setStatusFlag("A2");
					}
				} else if (!"".equals(lStrReleaseDate1.trim())) {
					lObjHouseAdvance.setStatusFlag("A1");
				}
				if (!"".equals(lStrSancPrinInstall.trim())) {
					lObjHouseAdvance.setPrincipalInstLeft(Integer.parseInt(lStrSancPrinInstall));
				}
				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setInterestInstLeft(Integer.parseInt(lStrSancInterInstall));
				}
				if ("".equals(lStrInterestAmount.trim())) {
					if (!"".equals(lStrSancAmount.trim())) {
						lObjHouseAdvance.setOutstandingAmount(Long.parseLong(lStrSancAmount));
					}
				} else {
					if (!"".equals(lStrSancAmount.trim())) {
						Double lDblInterestAmount = Double.parseDouble(lStrInterestAmount);
						lObjHouseAdvance.setOutstandingAmount(Long.parseLong(lStrSancAmount) + lDblInterestAmount.longValue());
					}
				}

			} else if (lStrHouseSubType.equals(gObjRsrcBndle.getString("LNA.PLOATPURCHASE"))) {

				String lStrSancAmount = StringUtility.getParameter("txtSanctionedAmountHBA2", request);
				String lStrPrincipalAmount = StringUtility.getParameter("txtPrincipalAmountHBA", request);
				String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountHBA2", request);
				String lStrDisbursement1 = StringUtility.getParameter("txtDisbursement1", request);
				String lStrDisbursement2 = StringUtility.getParameter("txtDisbursement2", request);
				String lStrDisbursement3 = StringUtility.getParameter("txtDisbursement3", request);
				String lStrDisbursement4 = StringUtility.getParameter("txtDisbursement4", request);
				String lStrReleaseDate1 = StringUtility.getParameter("txtReleaseDate1", request);
				String lStrReleaseDate2 = StringUtility.getParameter("txtReleaseDate2", request);
				String lStrReleaseDate3 = StringUtility.getParameter("txtReleaseDate3", request);
				String lStrReleaseDate4 = StringUtility.getParameter("txtReleaseDate4", request);
				String lStrSancPrinInstall = StringUtility.getParameter("txtSancPrinInstallHBA2", request);
				String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallHBA2", request);
				String lStrPrincipalInstallmentAmt = StringUtility.getParameter("txtPrinInstallmentAmountHBA", request);
				String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountHBA2", request);
				String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtHBA2", request);
				String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtHBA2", request);
				String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoHBA2", request);
				String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoHBA2", request);

				if (!"".equals(lStrSancAmount.trim())) {
					lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
				}
				if (!"".equals(lStrInterestAmount.trim())) {
					lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
				}
				if (!"".equals(lStrPrincipalAmount.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrPrincipalAmount));
				}

				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
				}
				if (!"".equals(lStrSancPrinInstall.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrSancPrinInstall));
				}
				if (!"".equals(lStrPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setPrincipalInstAmtMonth(Long.parseLong(lStrPrincipalInstallmentAmt));
				}
				if (!"".equals(lStrInterInstallmentAmount.trim())) {
					lObjHouseAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
				}
				if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
				}
				if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
				}
				if (!(lStrOddInterestInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
				}
				if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
				}
				if (!"".equals(lStrDisbursement1.trim())) {
					lObjHouseAdvance.setDisbursementOne(Float.parseFloat(lStrDisbursement1));
				}
				if (!"".equals(lStrDisbursement2.trim())) {
					lObjHouseAdvance.setDisbursementTwo(Float.parseFloat(lStrDisbursement2));
				}
				if (!"".equals(lStrDisbursement3.trim())) {
					lObjHouseAdvance.setDisbursementThree(Float.parseFloat(lStrDisbursement3));
				}
				if (!"".equals(lStrDisbursement4.trim())) {
					lObjHouseAdvance.setDisbursementFour(Float.parseFloat(lStrDisbursement4));
				}
				if (!"".equals(lStrReleaseDate1.trim())) {
					lObjHouseAdvance.setReleaseDateOne(lObjSimpleDateFormat.parse(lStrReleaseDate1));
					lObjHouseAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrReleaseDate1));
				}				
				if (!"".equals(lStrReleaseDate2.trim())) {
					lObjHouseAdvance.setReleaseDateTwo(lObjSimpleDateFormat.parse(lStrReleaseDate2));
				}
				if (!"".equals(lStrReleaseDate3.trim())) {
					lObjHouseAdvance.setReleaseDateThree(lObjSimpleDateFormat.parse(lStrReleaseDate3));
				}
				if (!"".equals(lStrReleaseDate4.trim())) {
					lObjHouseAdvance.setReleaseDateFour(lObjSimpleDateFormat.parse(lStrReleaseDate4));
				}
				if (!"".equals(lStrReleaseDate4.trim())) {
					lObjHouseAdvance.setStatusFlag("A");
				} else if (!"".equals(lStrReleaseDate3.trim())) {
					if (Long.parseLong(lStrSancAmount) == Float.parseFloat(lStrDisbursement1) + Float.parseFloat(lStrDisbursement2) + Float.parseFloat(lStrDisbursement3)) {
						lObjHouseAdvance.setStatusFlag("A");
					} else {
						lObjHouseAdvance.setStatusFlag("A3");
					}
				} else if (!"".equals(lStrReleaseDate2.trim())) {
					lObjHouseAdvance.setStatusFlag("A2");
				} else if (!"".equals(lStrReleaseDate1.trim())) {
					lObjHouseAdvance.setStatusFlag("A1");
				}
				if (!"".equals(lStrSancPrinInstall.trim())) {
					lObjHouseAdvance.setPrincipalInstLeft(Integer.parseInt(lStrSancPrinInstall));
				}
				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setInterestInstLeft(Integer.parseInt(lStrSancInterInstall));
				}
				if ("".equals(lStrInterestAmount.trim())) {
					if (!"".equals(lStrSancAmount.trim())) {
						lObjHouseAdvance.setOutstandingAmount(Long.parseLong(lStrSancAmount));
					}
				} else {
					if (!"".equals(lStrSancAmount.trim())) {
						Double lDblInterestAmount = Double.parseDouble(lStrInterestAmount);
						lObjHouseAdvance.setOutstandingAmount(Long.parseLong(lStrSancAmount) + lDblInterestAmount.longValue());
					}
				}

			} else {

				String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateHBA", request);
				String lStrSancAmount = StringUtility.getParameter("txtSanctionedAmountHBA", request);
				String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountHBA", request);
				String lStrSancPrinInstall = StringUtility.getParameter("txtSancPrinInstallHBA", request);
				String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallHBA", request);
				String lStrPrincipalInstallmentAmt = StringUtility.getParameter("txtPrincipalInstallmentAmtHBA", request);
				String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountHBA", request);
				String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtHBA", request);
				String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtHBA", request);
				String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoHBA", request);
				String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoHBA", request);

				if (!"".equals(lStrSanctionedDate.trim())) {
					lObjHouseAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
				}
				if (!"".equals(lStrSancAmount.trim())) {
					lObjHouseAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
				}
				if (!"".equals(lStrInterestAmount.trim())) {
					lObjHouseAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
				}
				if (!"".equals(lStrSancPrinInstall.trim())) {
					lObjHouseAdvance.setSancPrinInst(Integer.parseInt(lStrSancPrinInstall));
				}
				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
				}
				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
				}
				if (!"".equals(lStrPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setPrincipalInstAmtMonth(Long.parseLong(lStrPrincipalInstallmentAmt));
				}
				if (!"".equals(lStrInterInstallmentAmount.trim())) {
					lObjHouseAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
				}
				if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
				}
				if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
				}
				if (!(lStrOddInterestInstallNo.equals("-1"))) {
					lObjHouseAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
				}
				if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
					lObjHouseAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
				}
				if (!"".equals(lStrSancPrinInstall.trim())) {
					lObjHouseAdvance.setPrincipalInstLeft(Integer.parseInt(lStrSancPrinInstall));
				}
				if (!"".equals(lStrSancInterInstall.trim())) {
					lObjHouseAdvance.setInterestInstLeft(Integer.parseInt(lStrSancInterInstall));
				}
				if (!"".equals(lStrSancAmount.trim()) && !"".equals(lStrInterestAmount.trim())) {
					Double lDblInterestAmount = Double.parseDouble(lStrInterestAmount);
					lObjHouseAdvance.setOutstandingAmount(Long.parseLong(lStrSancAmount) + lDblInterestAmount.longValue());
				}

				lObjHouseAdvance.setStatusFlag("A");
			}
			if (!"".equals(lStrOrderNo.trim())) {
				lObjHouseAdvance.setOrderNo(lStrOrderNo);
			}
			if (!"".equals(lStrOrderDate.trim())) {
				lObjHouseAdvance.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate));
			}
			if (!"".equals(lStrHoRemarks.trim())) {
				lObjHouseAdvance.setHoRemarks(lStrHoRemarks);
			}
			lObjHouseAdvance.setRecoveryStatus(0);
			lObjHouseAdvance.setRecoveredAmount(0L);
			lObjHouseAdvance.setHodActionDate(gDtCurDate);
			lObjHouseAdvance.setUpdatedUserId(gLngUserId);
			lObjHouseAdvance.setUpdatedPostId(gLngPostId);
			lObjHouseAdvance.setUpdatedDate(gDtCurDate);

			lObjHouseAdvanceDAO.update(lObjHouseAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForHouseAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject rejectHouseAdvanceByDEOApprover(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String lStrPKValue = StringUtility.getParameter("HouseAdvanceId", request);
			String lStrDeoRemarks = StringUtility.getParameter("DEORemarks", request);
			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrDeoRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.HouseAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.returnDoc(inputMap);

			LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			MstLnaHouseAdvance lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(Long.parseLong(lStrPKValue));

			lObjHouseAdvance.setStatusFlag("R");
			lObjHouseAdvance.setApproverRemarks(lStrDeoRemarks);
			
			lObjHouseAdvance.setToPostID(lObjHouseAdvance.getFromPostId());
			lObjHouseAdvance.setFromPostId(gStrPostId);
			
			lObjHouseAdvance.setUpdatedUserId(gLngUserId);
			lObjHouseAdvance.setUpdatedPostId(gLngPostId);
			lObjHouseAdvance.setUpdatedDate(gDtCurDate);
			lObjHouseAdvanceDAO.update(lObjHouseAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForHouseAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject rejectHouseAdvanceByHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("HouseAdvanceId", request);
			String lStrHoRemarks = StringUtility.getParameter("HORemarks", request);
			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrHoRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.HouseAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.returnDoc(inputMap);

			LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			MstLnaHouseAdvance lObjHouseAdvance = (MstLnaHouseAdvance) lObjHouseAdvanceDAO.read(Long.parseLong(lStrPKValue));

			lObjHouseAdvance.setHodActionDate(gDtCurDate);
			lObjHouseAdvance.setStatusFlag("R");
			lObjHouseAdvance.setToPostID(lObjHouseAdvance.getFromPostId());
			lObjHouseAdvance.setFromPostId(gStrPostId);
			lObjHouseAdvance.setHoRemarks(lStrHoRemarks);
			lObjHouseAdvance.setUpdatedUserId(gLngUserId);
			lObjHouseAdvance.setUpdatedPostId(gLngPostId);
			lObjHouseAdvance.setUpdatedDate(gDtCurDate);
			lObjHouseAdvanceDAO.update(lObjHouseAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForHouseAdvance(lBlFlag).toString();
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

			String subjectName = gObjRsrcBndle.getString("LNA.HouseAdvanceOffline");
			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrPostId, subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle.getString("LNA.HouseAdvanceOffline"));

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	private StringBuilder getResponseXMLDocForHouseAdvance(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	private StringBuilder getSaveResponseXMLDocForHouseAdvance(Boolean flag, String lStrSevaarthID, String lStrUserType, String lStrReqType) {

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

	private StringBuilder getResponseXMLDocForForwardHouseAdvance(Boolean flag, String lStrTransId, String lStrOrgEmpId, String lStrCurrDate) {

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

	public ResultObject forwardOfflineEntryHBAToHOD(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Integer iSaveOrUpdate = 0;
		Boolean lBlFlag = false;
		String lStrTransId = "";
		String lStrOldTransId = "";
		String lStrSevaarthId = "";
		Long lLngRequestId = null;
		Long lLngHouseAdvanceId = null;
		MstLnaDocChecklist lObjDocChecklist = null;
		Long lLngDocChecklistId = null;

		try {
			setSessionInfo(inputMap);
			MstLnaHouseAdvance lObjHouseAdvance = (MstLnaHouseAdvance) inputMap.get("HouseAdvance");
			MstLnaRequest lObjLnaRequest = new MstLnaRequest();
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
			LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.HOD2");

			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			String lStrRequestType = StringUtility.getParameter("hidRequestType", request);
			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
			lStrOldTransId = lObjHouseAdvance.getTransactionId();
			if (lStrOldTransId == null || "".equals(lStrOldTransId)) {
				lStrTransId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, Long.parseLong(lStrRequestType));
			} else {
				lStrTransId = lStrOldTransId;
			}
			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);
			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			Map attachMap = (Map) resObj.getResultValue();

			Long lLngAttachId = 0L;
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_ProofHBA") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_ProofHBA")));
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lLngAttachId);
					lObjHouseAdvance.setAttachmentId(lLngAttachId);
				}

			}

			lObjHouseAdvance.setStatusFlag("F");
			lObjHouseAdvance.setTransactionId(lStrTransId);

			LNARequestProcessDAO lObjCheckListDAO = new LNARequestProcessDAOImpl(MstLnaDocChecklist.class, serv.getSessionFactory());
			String lStrCheckBoxList = StringUtility.getParameter("CheckBoxList", request);
			String lStrCheckedUncheck = StringUtility.getParameter("CheckedUncheck", request);
			String lStrHouseSubType = StringUtility.getParameter("cmbHBAType", request);

			List lCheckListPk = lObjCheckListDAO.getChecklistPk(lStrSevaarthId, Long.parseLong(lStrRequestType), Long.parseLong(lStrHouseSubType));
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
				lObjDocChecklist.setReqSubType(Long.parseLong(lStrHouseSubType));
				lObjDocChecklist.setChecked(lArrCheckedUncheck[lInt]);
				lObjDocChecklist.setCreatedPostId(gLngPostId);
				lObjDocChecklist.setCreatedUserId(gLngUserId);
				lObjDocChecklist.setCreatedDate(gDtCurDate);
				lObjCheckListDAO.create(lObjDocChecklist);
			}
			lObjHouseAdvance.setHodasstActionDate(gDtCurDate);
			if (iSaveOrUpdate == 1) {
				lLngHouseAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_house_advance", inputMap);
				lObjHouseAdvance.setHouseAdvanceId(lLngHouseAdvanceId);
				lObjHouseAdvanceDAO.create(lObjHouseAdvance);
			} else {
				lLngHouseAdvanceId = lObjHouseAdvance.getHouseAdvanceId();
				lObjHouseAdvanceDAO.update(lObjHouseAdvance);
			}
			if (lStrOldTransId == null || "".equals(lStrOldTransId)) {
				lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
				lObjLnaRequest.setRequestId(lLngRequestId);
				lObjLnaRequest.setTransactionId(lStrTransId);
				lObjLnaRequest.setLoanAdvanceId(lLngHouseAdvanceId);
				lObjLnaRequest.setAdvanceType(Long.parseLong(gObjRsrcBndle.getString("LNA.HOUSEADVANCE")));
				lObjLnaRequest.setCreatedPostId(gLngPostId);
				lObjLnaRequest.setCreatedUserId(gLngUserId);
				lObjLnaRequest.setCreatedDate(gDtCurDate);
				lObjRequestProcessDAO.create(lObjLnaRequest);
			}
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.HouseAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lLngHouseAdvanceId);
			createWF(inputMap);
			WorkFlowDelegate.forward(inputMap);

			lBlFlag = true;

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lStrCurrDate = lObjSimpleDateFormat.format(gDtCurDate);
		String lSBStatus = getResponseXMLDocForForwardHouseAdvance(lBlFlag, lStrTransId, lStrSevaarthId, lStrCurrDate).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject popUpGuarantorDtls(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);

		Boolean lBlFlag = false;
		String lStrEmpCode = null;
		List lLstGuarantorDtls = null;
		String lStrEmpName = null;
		String lStrDdocode = null;
		String lStrDdoName = null;
		String lStrBasicPay = null;
		LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
		try {
			lStrEmpCode = StringUtility.getParameter("empCode", request);
			lLstGuarantorDtls = lObjHouseAdvanceDAO.getGuarantorDtls(lStrEmpCode);
			if (!lLstGuarantorDtls.isEmpty()) {
				Object[] lObjGuarantorDtls = (Object[]) lLstGuarantorDtls.get(0);
				lStrEmpName = lObjGuarantorDtls[0].toString();
				lStrDdocode = lObjGuarantorDtls[1].toString();
				lStrDdoName = lObjGuarantorDtls[2].toString();
				lStrBasicPay = lObjGuarantorDtls[3].toString();
				lBlFlag = true;
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForGuarantor(lBlFlag, lStrEmpName, lStrDdocode, lStrDdoName, lStrBasicPay).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject chckEligibilityForExtensionOfRoom(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		LNAHouseAdvanceDAO lObjHouseAdvanceDAO = new LNAHouseAdvanceDAOImpl(MstLnaHouseAdvance.class, serv.getSessionFactory());
		Boolean lBlStatus = false;
		List lLstEmpStaus = null;
		Long lLngSancAmount = 0L;
		try {
			String lStrSevaarthId = StringUtility.getParameter("SevaarthId", request);
			lLstEmpStaus = lObjHouseAdvanceDAO.getEligibleStatusForExtnOfRoom(lStrSevaarthId);
			if (!lLstEmpStaus.isEmpty()) {
				lBlStatus = true;
				lLngSancAmount = (Long) lLstEmpStaus.get(0);
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocExtnOfRoom(lBlStatus, lLngSancAmount).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDocForGuarantor(Boolean flag, String lStrEmpName, String lStrDdocode, String lStrDdoName, String lStrBasicPay) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<lEmpName>");
		lStrBldXML.append(lStrEmpName);
		lStrBldXML.append("</lEmpName>");
		lStrBldXML.append("<lDdocode>");
		lStrBldXML.append(lStrDdocode);
		lStrBldXML.append("</lDdocode>");
		lStrBldXML.append("<lDdoName>");
		lStrBldXML.append(lStrDdoName);
		lStrBldXML.append("</lDdoName>");
		lStrBldXML.append("<lBasicPay>");
		lStrBldXML.append(lStrBasicPay);
		lStrBldXML.append("</lBasicPay>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocExtnOfRoom(Boolean flag, Long lLngSancAmount) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("<SancAmount>");
		lStrBldXML.append(lLngSancAmount);
		lStrBldXML.append("</SancAmount>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
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
