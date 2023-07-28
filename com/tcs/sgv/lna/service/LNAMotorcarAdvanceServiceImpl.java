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
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAO;
import com.tcs.sgv.lna.dao.LNAMotorAdvanceDAOImpl;
import com.tcs.sgv.lna.dao.LNARequestProcessDAO;
import com.tcs.sgv.lna.dao.LNARequestProcessDAOImpl;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaDocChecklist;
import com.tcs.sgv.lna.valueobject.MstLnaMotorAdvance;
import com.tcs.sgv.lna.valueobject.MstLnaRequest;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;

public class LNAMotorcarAdvanceServiceImpl extends ServiceImpl implements LNAMotorcarAdvanceService {
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

	public ResultObject saveMotorcarAdvance(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		MstLnaMotorAdvance lObjMotorAdvance = (MstLnaMotorAdvance) inputMap.get("MotorAdvance");

		setSessionInfo(inputMap);
		Integer iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
		Boolean lBlFlag = false;
		String lStrSevaarthId = null;
		String lStrRequestType = null;
		String lStrUserType = null;
		MstLnaDocChecklist lObjDocChecklist = null;
		Long lLngDocChecklistId = null;

		LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
		LNARequestProcessDAO lObjCheckListDAO = new LNARequestProcessDAOImpl(MstLnaDocChecklist.class, serv.getSessionFactory());
		try {
			String lStrCheckBoxList = StringUtility.getParameter("CheckBoxList", request);
			String lStrCheckedUncheck = StringUtility.getParameter("CheckedUncheck", request);
			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			lStrRequestType = StringUtility.getParameter("hidRequestType", request);
			lStrUserType = StringUtility.getParameter("hidUserType", request);
			String lStrVehicleSubType = StringUtility.getParameter("cmbVehicleSubType", request);
			String lStrResult="";
			/*System.out.println("lStrVehicleSubType:" +lStrVehicleSubType);
			double VehicleSubType;
			VehicleSubType=Double.valueOf(lStrVehicleSubType);
			System.out.println("VehicleSubType:" +VehicleSubType);
			String alertMessage = "";
			String lStrResult="";
			Double subtype=lObjMotorAdvanceDAO.getMotorSubtype(lStrSevaarthId);
			if((subtype==800032 || subtype==800033 || subtype==800034) && (VehicleSubType==800032 || VehicleSubType==800033 || VehicleSubType==800034))
			{
				
				StringBuilder lStrBldXML = new StringBuilder();

				
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<isGenerated>");
				lStrBldXML.append("yes");
				lStrBldXML.append("</isGenerated>");
				lStrBldXML.append("</XMLDOC>");
				String lSBStatus1 = lStrBldXML.toString();
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus1).toString();
				System.out.println("********************************************" + lStrResult);
				inputMap.put("ajaxKey", lStrResult);
				resObj.setResultValue(inputMap);
				resObj.setViewName("ajaxData");
			}
			
			else if(subtype==800061 && VehicleSubType==800061)
			{
				StringBuilder lStrBldXML = new StringBuilder();

				
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<isGenerated>");
					lStrBldXML.append("car");
				lStrBldXML.append("</isGenerated>");
				lStrBldXML.append("</XMLDOC>");
				String lSBStatus1 = lStrBldXML.toString();
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus1).toString();
				System.out.println("********************************************" + lStrResult);
				inputMap.put("ajaxKey", lStrResult);
				resObj.setResultValue(inputMap);
				resObj.setViewName("ajaxData");
			}
			else
				{*/
				List lCheckListPk = lObjCheckListDAO.getChecklistPk(lStrSevaarthId, Long.parseLong(lStrRequestType), Long.parseLong(lStrVehicleSubType));
				
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
				lObjDocChecklist.setReqSubType(Long.parseLong(lStrVehicleSubType));
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

			//glogger.info("Attcament Id Motor Advance::" + attachMap.get("AttachmentId_ProofMCA"));
			Long lLngAttachId = 0L;
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_ProofMCA") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_ProofMCA")));
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lLngAttachId);
					lObjMotorAdvance.setAttachmentId(lLngAttachId);
				}

			}
			lObjMotorAdvance.setToPostID(gLngPostId.toString());
			lObjMotorAdvance.setFromPostId(gLngPostId.toString());
			lObjMotorAdvance.setStatusFlag("D");
			if (iSaveOrUpdate == 1) {
				Long lLngMotorAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_motor_advance", inputMap);
				lObjMotorAdvance.setMotorAdvanceId(lLngMotorAdvanceId);
				lObjMotorAdvanceDAO.create(lObjMotorAdvance);
			} else {
				lObjMotorAdvanceDAO.update(lObjMotorAdvance);
			}
			lBlFlag = true;
		
			String lSBStatus = getSaveResponseXMLDocForMotorAdvance(lBlFlag, lStrSevaarthId, lStrRequestType, lStrUserType).toString();
			lStrResult= new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		
		/*inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");*/
		}
		
		catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		

		
		
		return resObj;
	}

	public ResultObject forwardMotorAdvanceToDEOVerifier(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Integer iSaveOrUpdate = 0;
		Boolean lBlFlag = false;
		String lStrTransId = "";
		String lStrSevaarthId = "";
		Long lLngRequestId = null;
		MstLnaDocChecklist lObjDocChecklist = null;
		Long lLngDocChecklistId = null;

		try {
			setSessionInfo(inputMap);
			MstLnaMotorAdvance lObjMotorAdvance = (MstLnaMotorAdvance) inputMap.get("MotorAdvance");
			MstLnaRequest lObjLnaRequest = new MstLnaRequest();
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
			Long lLngMotorAdvanceId = null;
			LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.DEOAPPROVER");

			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			String lStrRequestType = StringUtility.getParameter("hidRequestType", request);
			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			lStrTransId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, Long.parseLong(lStrRequestType));

			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			Map attachMap = (Map) resObj.getResultValue();

			Long lLngAttachId = 0L;
			//	glogger.info("Attcament Id Motor Advance::" + attachMap.get("AttachmentId_ProofMCA"));
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_ProofMCA") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_ProofMCA")));
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lLngAttachId);
					lObjMotorAdvance.setAttachmentId(lLngAttachId);
				}

			}

			lObjMotorAdvance.setFromPostId(gLngPostId.toString());
			lObjMotorAdvance.setToPostID(toPost);
			lObjMotorAdvance.setFrwrdToRHO("N");
			lObjMotorAdvance.setStatusFlag("F");
			lObjMotorAdvance.setTransactionId(lStrTransId);

			LNARequestProcessDAO lObjCheckListDAO = new LNARequestProcessDAOImpl(MstLnaDocChecklist.class, serv.getSessionFactory());
			String lStrCheckBoxList = StringUtility.getParameter("CheckBoxList", request);
			String lStrCheckedUncheck = StringUtility.getParameter("CheckedUncheck", request);
			String lStrVehicleSubType = StringUtility.getParameter("cmbVehicleSubType", request);
			List lCheckListPk = lObjCheckListDAO.getChecklistPk(lStrSevaarthId, Long.parseLong(lStrRequestType), Long.parseLong(lStrVehicleSubType));
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
				lObjDocChecklist.setReqSubType(Long.parseLong(lStrVehicleSubType));
				lObjDocChecklist.setChecked(lArrCheckedUncheck[lInt]);
				lObjDocChecklist.setCreatedPostId(gLngPostId);
				lObjDocChecklist.setCreatedUserId(gLngUserId);
				lObjDocChecklist.setCreatedDate(gDtCurDate);
				lObjCheckListDAO.create(lObjDocChecklist);
			}
			if (iSaveOrUpdate == 1) {
				lLngMotorAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_motor_advance", inputMap);
				lObjMotorAdvance.setMotorAdvanceId(lLngMotorAdvanceId);
				lObjMotorAdvanceDAO.create(lObjMotorAdvance);
			} else {
				lLngMotorAdvanceId = lObjMotorAdvance.getMotorAdvanceId();
				lObjMotorAdvanceDAO.update(lObjMotorAdvance);
			}
			lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
			lObjLnaRequest.setRequestId(lLngRequestId);
			lObjLnaRequest.setTransactionId(lStrTransId);
			lObjLnaRequest.setLoanAdvanceId(lLngMotorAdvanceId);
			lObjLnaRequest.setAdvanceType(lObjMotorAdvance.getAdvanceType());
			lObjLnaRequest.setCreatedPostId(gLngPostId);
			lObjLnaRequest.setCreatedUserId(gLngUserId);
			lObjLnaRequest.setCreatedDate(gDtCurDate);
			lObjMotorAdvanceDAO.create(lObjLnaRequest);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.MotorAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lLngMotorAdvanceId);
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

	public ResultObject forwardMotorAdvanceToHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.HO");

			String lStrPKValue = StringUtility.getParameter("hidMotorAdvanceId", request);
			String lStrDeoRemarks = StringUtility.getParameter("txtApproverRemarksMCA", request);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.MotorAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.forward(inputMap);

			LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			MstLnaMotorAdvance lObjMotorAdvance = (MstLnaMotorAdvance) lObjMotorAdvanceDAO.read(Long.parseLong(lStrPKValue));

			lObjMotorAdvance.setFromPostId(gLngPostId.toString());
			lObjMotorAdvance.setToPostID(toPost);
			lObjMotorAdvance.setStatusFlag("F");
			lObjMotorAdvance.setFrwrdToRHO("N");
			lObjMotorAdvance.setApproverRemarks(lStrDeoRemarks);
			lObjMotorAdvance.setUpdatedUserId(gLngUserId);
			lObjMotorAdvance.setUpdatedPostId(gLngPostId);
			lObjMotorAdvance.setUpdatedDate(gDtCurDate);
			lObjMotorAdvanceDAO.update(lObjMotorAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForMotorAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}
////METHOD FOR rho
	public ResultObject forwardMotorAdvanceToAsstHOD(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.ASSTHOD");
			String frwrdToHRO = StringUtility.getParameter("frwrdToHRO", request).toString();
			
			String lStrPKValue = StringUtility.getParameter("hidMotorAdvanceId", request);
			String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateMCA", request);
			String lStrSancAmount = StringUtility.getParameter("txtSancAmountMCA", request);
			String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountMCA", request);
			String lStrSancCapInstall = StringUtility.getParameter("txtSancPrincipalInstallMCA", request);
			String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallMCA", request);
			String lStrCapInstallmentAmount = StringUtility.getParameter("txtPrinInstallmentAmountMCA", request);
			String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountMCA", request);
			String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtMCA", request);
			String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtMCA", request);
			String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoMCA", request);
			String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoMCA", request);
			String lStrHoRemarks = StringUtility.getParameter("txtHORemarks", request);

			insertHROInWF(toPost,inputMap,gStrLocationCode);
			
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.MotorAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.forward(inputMap);

			LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			MstLnaMotorAdvance lObjMotorAdvance = (MstLnaMotorAdvance) lObjMotorAdvanceDAO.read(Long.parseLong(lStrPKValue));
			if(frwrdToHRO == null || !frwrdToHRO.equals("Yes") || frwrdToHRO.equals("")){
			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjMotorAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrSancAmount.trim())) {
				lObjMotorAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
			}
			if (!"".equals(lStrInterestAmount.trim())) {
				lObjMotorAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
			}
			if (!"".equals(lStrSancCapInstall.trim())) {
				lObjMotorAdvance.setSancCapitalInst(Integer.parseInt(lStrSancCapInstall));
			}
			if (!"".equals(lStrSancInterInstall.trim())) {
				lObjMotorAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
			}
			if (!"".equals(lStrCapInstallmentAmount.trim())) {
				lObjMotorAdvance.setCappitalInstAmtMonth(Long.parseLong(lStrCapInstallmentAmount));
			}
			if (!"".equals(lStrInterInstallmentAmount.trim())) {
				lObjMotorAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
			}
			if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
				lObjMotorAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
			}
			if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
				lObjMotorAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
			}
			if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
				lObjMotorAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
			}
			if (!(lStrOddInterestInstallNo.equals("-1"))) {
				lObjMotorAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
			}
			if (!"".equals(lStrHoRemarks.trim())) {
				lObjMotorAdvance.setHoRemarks(lStrHoRemarks);
			}
			}
			
			lObjMotorAdvance.setFromPostId(gLngPostId.toString());
			lObjMotorAdvance.setToPostID(toPost);
			lObjMotorAdvance.setFrwrdToRHO("Y");
			lObjMotorAdvance.setStatusFlag("F");
			lObjMotorAdvance.setUpdatedUserId(gLngUserId);
			lObjMotorAdvance.setUpdatedPostId(gLngPostId);
			lObjMotorAdvance.setUpdatedDate(gDtCurDate);
			lObjMotorAdvanceDAO.update(lObjMotorAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForMotorAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject forwardMotorAdvanceToHOD(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			
			String toPost = null;
			String frmRHO = StringUtility.getParameter("frmRHO", request);
			if((frmRHO != "" || frmRHO != null ) && frmRHO.equals("Yes")){					
				String LNANextPostId = null;
				String  lStrRequestType = null;
					List UserList = new LNARequestProcessServiceImpl().getHierarchyUsers(inputMap,"800030");
					toPost = UserList.get(0).toString();				
			}
			else 
				toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.HOD");

			String lStrPKValue = StringUtility.getParameter("hidMotorAdvanceId", request);
			if (!"".equals(lStrPKValue.trim())) {
				inputMap.put("toPost", toPost);
				inputMap.put("toPostId", toPost);
				inputMap.put("toLevel", toLevel);

				inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.MotorAdvanceOffline"));
				inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
				inputMap.put("Pkvalue", lStrPKValue);
				WorkFlowDelegate.forward(inputMap);

				LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
				MstLnaMotorAdvance lObjMotorAdvance = (MstLnaMotorAdvance) lObjMotorAdvanceDAO.read(Long.parseLong(lStrPKValue));

				lObjMotorAdvance.setFromPostId(gLngPostId.toString());
				lObjMotorAdvance.setToPostID(toPost);
				lObjMotorAdvance.setFrwrdToRHO("FromRHO");
				lObjMotorAdvance.setStatusFlag("F");
				
				lObjMotorAdvance.setUpdatedUserId(gLngUserId);
				lObjMotorAdvance.setUpdatedPostId(gLngPostId);
				lObjMotorAdvance.setUpdatedDate(gDtCurDate);
				lObjMotorAdvanceDAO.update(lObjMotorAdvance);
				lBlFlag = true;
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForMotorAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject rejectMotorAdvanceByDEOApprover(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String lStrPKValue = StringUtility.getParameter("MotorAdvanceId", request);
			String lStrDeoRemarks = StringUtility.getParameter("DEORemarks", request);
			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrDeoRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.MotorAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.returnDoc(inputMap);

			LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			MstLnaMotorAdvance lObjMotorAdvance = (MstLnaMotorAdvance) lObjMotorAdvanceDAO.read(Long.parseLong(lStrPKValue));

			lObjMotorAdvance.setStatusFlag("R");
			lObjMotorAdvance.setApproverRemarks(lStrDeoRemarks);
			lObjMotorAdvance.setToPostID(lObjMotorAdvance.getFromPostId());
			lObjMotorAdvance.setFromPostId(gStrPostId);
			lObjMotorAdvance.setUpdatedUserId(gLngUserId);
			lObjMotorAdvance.setUpdatedPostId(gLngPostId);
			lObjMotorAdvance.setUpdatedDate(gDtCurDate);
			lObjMotorAdvanceDAO.update(lObjMotorAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForMotorAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject rejectMotorAdvanceByHO(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);

			String lStrPKValue = StringUtility.getParameter("MotorAdvanceId", request);
			String lStrHoRemarks = StringUtility.getParameter("HORemarks", request);
			inputMap.put("FromPostId", gStrPostId);
			inputMap.put("SendNotification", lStrHoRemarks);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.MotorAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lStrPKValue);
			WorkFlowDelegate.returnDoc(inputMap);

			LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			MstLnaMotorAdvance lObjMotorAdvance = (MstLnaMotorAdvance) lObjMotorAdvanceDAO.read(Long.parseLong(lStrPKValue));

			lObjMotorAdvance.setHodActionDate(gDtCurDate);
			lObjMotorAdvance.setStatusFlag("R");
			lObjMotorAdvance.setHoRemarks(lStrHoRemarks);
			lObjMotorAdvance.setToPostID(lObjMotorAdvance.getFromPostId());
			lObjMotorAdvance.setFromPostId(gStrPostId);
			lObjMotorAdvance.setUpdatedUserId(gLngUserId);
			lObjMotorAdvance.setUpdatedPostId(gLngPostId);
			lObjMotorAdvance.setUpdatedDate(gDtCurDate);
			lObjMotorAdvanceDAO.update(lObjMotorAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForMotorAdvance(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject approveMotorAdvance(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Boolean lBlFlag = false;
		try {
			setSessionInfo(inputMap);
			String lStrPKValue = StringUtility.getParameter("hidMotorAdvanceId", request);
			String lStrSanctionedDate = StringUtility.getParameter("txtSanctionedDateMCA", request);
			String lStrSancAmount = StringUtility.getParameter("txtSancAmountMCA", request);
			String lStrInterestAmount = StringUtility.getParameter("txtInterestAmountMCA", request);
			String lStrSancCapInstall = StringUtility.getParameter("txtSancPrincipalInstallMCA", request);
			String lStrSancInterInstall = StringUtility.getParameter("txtSancInterInstallMCA", request);
			String lStrCapInstallmentAmount = StringUtility.getParameter("txtPrinInstallmentAmountMCA", request);
			String lStrInterInstallmentAmount = StringUtility.getParameter("txtInterInstallmentAmountMCA", request);
			String lStrOddPrincipalInstallmentAmt = StringUtility.getParameter("txtOddPrincipalInstallmentAmtMCA", request);
			String lStrOddInterestInstallmentAmt = StringUtility.getParameter("txtOddInterestInstallmentAmtMCA", request);
			String lStrOddPrincipalInstallNo = StringUtility.getParameter("cmbOddPrincipalInstallNoMCA", request);
			String lStrOddInterestInstallNo = StringUtility.getParameter("cmbOddInterestInstallNoMCA", request);
			String lStrOrderNo = StringUtility.getParameter("txtOrderNoHBA", request);
			String lStrOrderDate = StringUtility.getParameter("txtOrderDateHBA", request);

			LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			MstLnaMotorAdvance lObjMotorAdvance = (MstLnaMotorAdvance) lObjMotorAdvanceDAO.read(Long.parseLong(lStrPKValue));

			if (!"".equals(lStrSanctionedDate.trim())) {
				lObjMotorAdvance.setSanctionedDate(lObjSimpleDateFormat.parse(lStrSanctionedDate));
			}
			if (!"".equals(lStrSancAmount.trim())) {
				lObjMotorAdvance.setAmountSanctioned(Long.parseLong(lStrSancAmount));
			}
			if (!"".equals(lStrInterestAmount.trim())) {
				lObjMotorAdvance.setInterestAmount(Double.parseDouble(lStrInterestAmount));
			}
			if (!"".equals(lStrSancCapInstall.trim())) {
				lObjMotorAdvance.setSancCapitalInst(Integer.parseInt(lStrSancCapInstall));
			}
			if (!"".equals(lStrSancInterInstall.trim())) {
				lObjMotorAdvance.setSancInterestInst(Integer.parseInt(lStrSancInterInstall));
			}
			if (!"".equals(lStrCapInstallmentAmount.trim())) {
				lObjMotorAdvance.setCappitalInstAmtMonth(Long.parseLong(lStrCapInstallmentAmount));
			}
			if (!"".equals(lStrInterInstallmentAmount.trim())) {
				lObjMotorAdvance.setInterestInstAmtMonth(Double.parseDouble(lStrInterInstallmentAmount));
			}
			if (!"".equals(lStrOddPrincipalInstallmentAmt.trim())) {
				lObjMotorAdvance.setOddInstallment(Long.parseLong(lStrOddPrincipalInstallmentAmt));
			}
			if (!(lStrOddPrincipalInstallNo.equals("-1"))) {
				lObjMotorAdvance.setOddInstallmentNumber(Long.parseLong(lStrOddPrincipalInstallNo));
			}
			if (!"".equals(lStrOddInterestInstallmentAmt.trim())) {
				lObjMotorAdvance.setOddInterestInstallment(Long.parseLong(lStrOddInterestInstallmentAmt));
			}
			if (!(lStrOddInterestInstallNo.equals("-1"))) {
				lObjMotorAdvance.setOddInterestInstallmentNo(Long.parseLong(lStrOddInterestInstallNo));
			}
			if (!"".equals(lStrOrderNo.trim())) {
				lObjMotorAdvance.setOrderNo(lStrOrderNo);
			}
			if (!"".equals(lStrOrderDate.trim())) {
				lObjMotorAdvance.setOrderDate(lObjSimpleDateFormat.parse(lStrOrderDate));
			}
			if (!"".equals(lStrSancCapInstall.trim())) {
				lObjMotorAdvance.setCapitalInstLeft(Integer.parseInt(lStrSancCapInstall));
			}
			if (!"".equals(lStrSancInterInstall.trim())) {
				lObjMotorAdvance.setInterestInstLeft(Integer.parseInt(lStrSancInterInstall));
			}
			if (!"".equals(lStrSancAmount.trim()) && !"".equals(lStrInterestAmount.trim())) {
				Double lDblInterestAmount = Double.parseDouble(lStrInterestAmount);
				lObjMotorAdvance.setOutstandingAmount(Long.parseLong(lStrSancAmount) + lDblInterestAmount.longValue());
			}
			lObjMotorAdvance.setHodActionDate(gDtCurDate);
			lObjMotorAdvance.setStatusFlag("A");
			lObjMotorAdvance.setRecoveryStatus(0);
			lObjMotorAdvance.setRecoveredAmount(0L);
			lObjMotorAdvance.setUpdatedUserId(gLngUserId);
			lObjMotorAdvance.setUpdatedPostId(gLngPostId);
			lObjMotorAdvance.setUpdatedDate(gDtCurDate);

			lObjMotorAdvanceDAO.update(lObjMotorAdvance);
			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is : ");
		}
		String lSBStatus = getResponseXMLDocForMotorAdvance(lBlFlag).toString();
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

			String subjectName = gObjRsrcBndle.getString("LNA.MotorAdvanceOffline");
			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(lStrPostId, subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle.getString("LNA.MotorAdvanceOffline"));

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	private StringBuilder getResponseXMLDocForMotorAdvance(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}

	private StringBuilder getSaveResponseXMLDocForMotorAdvance(Boolean flag, String lStrSevaarthID, String lStrUserType, String lStrReqType) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		/*lStrBldXML.append("<isGenerated>");
		lStrBldXML.append("no");
	    lStrBldXML.append("</isGenerated>");*/
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

	public ResultObject forwardOfflineEntryMCAToHOD(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Integer iSaveOrUpdate = 0;
		Boolean lBlFlag = false;
		String lStrTransId = "";
		String lStrSevaarthId = "";
		Long lLngRequestId = null;
		MstLnaDocChecklist lObjDocChecklist = null;
		Long lLngDocChecklistId = null;

		try {
			setSessionInfo(inputMap);
			MstLnaMotorAdvance lObjMotorAdvance = (MstLnaMotorAdvance) inputMap.get("MotorAdvance");
			MstLnaRequest lObjLnaRequest = new MstLnaRequest();
			iSaveOrUpdate = (Integer) inputMap.get("iSaveOrUpdateFlag");
			Long lLngMotorAdvanceId = null;
			LNAMotorAdvanceDAO lObjMotorAdvanceDAO = new LNAMotorAdvanceDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("LNA.HOD2");

			lStrSevaarthId = StringUtility.getParameter("hidSevaarthId", request);
			String lStrRequestType = StringUtility.getParameter("hidRequestType", request);
			LNARequestProcessDAO lObjRequestProcessDAO = new LNARequestProcessDAOImpl(MstLnaMotorAdvance.class, serv.getSessionFactory());
			lStrTransId = lObjRequestProcessDAO.getNewTransactionId(lStrSevaarthId, Long.parseLong(lStrRequestType));

			resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

			resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			Map attachMap = (Map) resObj.getResultValue();

			Long lLngAttachId = 0L;
			if (attachMap != null) {
				if (attachMap.get("AttachmentId_ProofMCA") != null) {
					lLngAttachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_ProofMCA")));
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lLngAttachId);
					lObjMotorAdvance.setAttachmentId(lLngAttachId);
				}

			}
			lObjMotorAdvance.setStatusFlag("F");
			lObjMotorAdvance.setTransactionId(lStrTransId);

			LNARequestProcessDAO lObjCheckListDAO = new LNARequestProcessDAOImpl(MstLnaDocChecklist.class, serv.getSessionFactory());
			String lStrCheckBoxList = StringUtility.getParameter("CheckBoxList", request);
			String lStrCheckedUncheck = StringUtility.getParameter("CheckedUncheck", request);
			String lStrVehicleSubType = StringUtility.getParameter("cmbVehicleSubType", request);
			List lCheckListPk = lObjCheckListDAO.getChecklistPk(lStrSevaarthId, Long.parseLong(lStrRequestType), Long.parseLong(lStrVehicleSubType));
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
				lObjDocChecklist.setReqSubType(Long.parseLong(lStrVehicleSubType));
				lObjDocChecklist.setChecked(lArrCheckedUncheck[lInt]);
				lObjDocChecklist.setCreatedPostId(gLngPostId);
				lObjDocChecklist.setCreatedUserId(gLngUserId);
				lObjDocChecklist.setCreatedDate(gDtCurDate);
				lObjCheckListDAO.create(lObjDocChecklist);
			}

			lObjMotorAdvance.setHodasstActionDate(gDtCurDate);
			if (iSaveOrUpdate == 1) {
				lLngMotorAdvanceId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_motor_advance", inputMap);
				lObjMotorAdvance.setMotorAdvanceId(lLngMotorAdvanceId);
				lObjMotorAdvanceDAO.create(lObjMotorAdvance);
			} else {
				lLngMotorAdvanceId = lObjMotorAdvance.getMotorAdvanceId();
				lObjMotorAdvanceDAO.update(lObjMotorAdvance);
			}
			lLngRequestId = IFMSCommonServiceImpl.getNextSeqNum("mst_lna_request", inputMap);
			lObjLnaRequest.setRequestId(lLngRequestId);
			lObjLnaRequest.setTransactionId(lStrTransId);
			lObjLnaRequest.setLoanAdvanceId(lLngMotorAdvanceId);
			lObjLnaRequest.setAdvanceType(lObjMotorAdvance.getAdvanceType());
			lObjLnaRequest.setCreatedPostId(gLngPostId);
			lObjLnaRequest.setCreatedUserId(gLngUserId);
			lObjLnaRequest.setCreatedDate(gDtCurDate);
			lObjMotorAdvanceDAO.create(lObjLnaRequest);
			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("LNA.MotorAdvanceOffline"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
			inputMap.put("Pkvalue", lLngMotorAdvanceId);
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
