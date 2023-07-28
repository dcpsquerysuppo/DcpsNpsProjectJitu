package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.tcs.sgv.common.dao.FinancialYearDAO;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.InterestCalculationDAO;
import com.tcs.sgv.dcps.dao.InterestCalculationDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.PostEmpContriDAO;
import com.tcs.sgv.dcps.dao.PostEmpContriDAOImpl;
import com.tcs.sgv.dcps.dao.TerminalRequestDAO;
import com.tcs.sgv.dcps.dao.TerminalRequestDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionMonthly;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionYearly;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.dcps.valueobject.TrnDcpsMissingCreditsDtls;
import com.tcs.sgv.dcps.valueobject.TrnDcpsTerminalDtls;
import com.tcs.sgv.eis.service.IdGenerator;


public class TerminalRequestServiceImpl extends ServiceImpl implements TerminalRequestService {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Old code commented
	public ResultObject loadTerminalRequest(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrUser = null;
		String lStrUse = null;
		String lStrGoPressed = null;
		List listReasonsForTermination = null;
		List listMissingCredits = null;
		String lStrName = null;
		String lStrEmpId = null;
		String lStrDcpsId = null;
		Long lLongEmpId = null;
		String lStrDOJ = null;
		Date lDtDOJ = null;
		String lStrTerminationDate = null;
		Date lDtTerminationDate = null;
		String lStrReasonForTermination = null;
		String TerminalRequestAlreadyRaised = null;

		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			listReasonsForTermination = IFMSCommonServiceImpl.getLookupValues("ReasonsForTermination", SessionHelper.getLangId(inputMap), inputMap);
			inputMap.put("listReasonsForTermination", listReasonsForTermination);

			lStrUser = StringUtility.getParameter("User", request).trim();
			lStrUse = StringUtility.getParameter("Use", request).trim();
			lStrGoPressed = StringUtility.getParameter("GoPressed", request).trim();

			if (lStrGoPressed.equals("Yes") && lStrUser.equals("DDO") && lStrUse.equals("NewRequest")) {
				lStrEmpId = StringUtility.getParameter("hidDcpsEmpId", request).trim();
				lLongEmpId = Long.valueOf(lStrEmpId);
				lStrName = StringUtility.getParameter("txtEmployeeName", request).trim();
				lStrDOJ = StringUtility.getParameter("hidJoiningDate", request).trim();
				lStrTerminationDate = StringUtility.getParameter("txtTerminationDate", request).trim();
				lStrDcpsId = StringUtility.getParameter("txtDCPSId", request).trim();
				lStrReasonForTermination = StringUtility.getParameter("cmbReasonForTermination", request).trim();
				lDtDOJ = sdf.parse(lStrDOJ);
				lDtTerminationDate = sdf.parse(lStrTerminationDate);

				if (lObjTerminalRequestDAO.checkTerminalRequestRaisedOrNot(lLongEmpId)) {
					TerminalRequestAlreadyRaised = "Yes";
				} else {
					TerminalRequestAlreadyRaised = "No";
					listMissingCredits = lObjTerminalRequestDAO.getAllMissingCreditsForEmp(lLongEmpId, lDtDOJ, lDtTerminationDate);
				}
				inputMap.put("TerminalRequestAlreadyRaised", TerminalRequestAlreadyRaised);
				inputMap.put("listMissingCredits", listMissingCredits);
				inputMap.put("lStrName", lStrName);
				inputMap.put("lStrEmpId", lStrEmpId);
				inputMap.put("lStrDcpsId", lStrDcpsId);
				inputMap.put("lStrDOJ", lStrDOJ);
				inputMap.put("lStrTerminationDate", lStrTerminationDate);
				inputMap.put("lStrReasonForTermination", lStrReasonForTermination);

				// Value 0 indicates that it is first time load for the page so
				// from save/update, save will be there.
				inputMap.put("hidTerminalId", 0);
			}
			inputMap.put("hidUser", lStrUser);
			inputMap.put("hidUse", lStrUse);
			inputMap.put("GoPressed", lStrGoPressed);

			resObj.setViewName("TerminalRequest");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}

	public ResultObject getEmpDtlsForName(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lListEmpDetails = null;

		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			new SimpleDateFormat("dd/MM/yyyy");

			String lStrEmpName = StringUtility.getParameter("empName", request).trim();
			String lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);

			if (!"".equals(lStrEmpName)) {
				lListEmpDetails = lObjTerminalRequestDAO.getEmpDtlsForName(lStrEmpName, lStrDDOCode);
			}

			String lSBStatus = getResponseXMLDocForEmpDtls(lListEmpDetails).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	public ResultObject viewTerminalRequests(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrUser = null;
		String lStrUse = null;
		List listTerminalRequests = null;
		String lStrDDOCode = null;

		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			new SimpleDateFormat("dd/MM/yyyy");

			lStrUser = StringUtility.getParameter("User", request).trim();
			lStrUse = StringUtility.getParameter("Use", request).trim();

			if (lStrUser.equals("DDO")) {
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId).trim();
			}

			listTerminalRequests = lObjTerminalRequestDAO.getAllTerminalRequests(lStrDDOCode, gStrLocationCode, lStrUser, lStrUse);

			inputMap.put("listTerminalRequests", listTerminalRequests);
			inputMap.put("hidUser", lStrUser);
			inputMap.put("hidUse", lStrUse);

			resObj.setViewName("TerminalRequestList");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDocForEmpDtls(List finalList) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();
		SimpleDateFormat lObjSimpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat lObjSimpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
		Object[] obj = null;

		if (finalList != null) {
			obj = (Object[]) finalList.get(0);
		}

		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<dcpsEmpId>");
		if (obj[0] != null) {
			lStrBldXML.append(obj[0].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</dcpsEmpId>");

		lStrBldXML.append("<dcpsId>");
		if (obj[1] != null) {
			lStrBldXML.append(obj[1].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</dcpsId>");

		lStrBldXML.append("<doj>");
		if (obj[2] != null) {
			lStrBldXML.append(lObjSimpleDateFormat2.format(lObjSimpleDateFormat1.parse(obj[2].toString().trim())).trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</doj>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject saveOrForwardTerminalRequest(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		Long lLongTerminalId = null;
		String lStrUser = null;
		String lStrUse = null;
		String lStrSaveOrForwardFlag = null;
		String lStrDDOCode = null;
		String lStrTreasuryCode = null;
		Long lLongTreasuryCode = null;
		Long lLongTerminalIdPassed = null;
		Map attachMap = null;
		Long formA_attachId = null;
		Long deathCerti_attachId = null;
		Long r3_attachId = null;

		try {
			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, serv.getSessionFactory());

			TerminalRequestDAO lObjTerminalRequestMissingCreditsDAO = new TerminalRequestDAOImpl(TrnDcpsMissingCreditsDtls.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			new ResultObject(ErrorConstants.SUCCESS, "FAIL");

			lStrUser = StringUtility.getParameter("hidUser", request).trim();
			lStrUse = StringUtility.getParameter("hidUse", request).trim();
			lStrSaveOrForwardFlag = StringUtility.getParameter("saveOrForwardFlag", request).trim();

			// Gets DDO Code and Treasury Code
			if (lStrUser.equals("DDO")) {
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId).trim();
				lStrTreasuryCode = lObjDcpsCommonDAO.getTreasuryCodeForDDO(lStrDDOCode);
				if (!"".equals(lStrTreasuryCode) && lStrTreasuryCode != null) {
					lLongTreasuryCode = Long.valueOf(lStrTreasuryCode);
				}
			}

			TrnDcpsTerminalDtls lObjTrnDcpsTerminalDtls = (TrnDcpsTerminalDtls) inputMap.get("lObjTrnDcpsTerminalDtls");

			// Sets Attachment Ids in TrnDcpsTerminalDtls for User - DDO and Use
			// - NewRequest or FromDraft
			if (lStrUser.equals("DDO") && (lStrUse.equals("NewRequest") || lStrUse.equals("FromDraft"))) {
				resObj = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

				resObj = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

				attachMap = (Map) resObj.getResultValue();

				if (attachMap != null) {
					if (attachMap.get("AttachmentId_FormA") != null) {
						formA_attachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_FormA")));
						CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
						attachmentMst.setAttachmentId(formA_attachId);
						lObjTrnDcpsTerminalDtls.setFormA_attachId(formA_attachId);
					}
					if (attachMap.get("AttachmentId_DeathCertificate") != null) {
						deathCerti_attachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_DeathCertificate")));
						CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
						attachmentMst.setAttachmentId(deathCerti_attachId);
						lObjTrnDcpsTerminalDtls.setDeathCerti_attachId(deathCerti_attachId);
					}
					if (attachMap.get("AttachmentId_R3Report") != null) {
						r3_attachId = Long.parseLong(String.valueOf(attachMap.get("AttachmentId_R3Report")));
						CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
						attachmentMst.setAttachmentId(r3_attachId);
						lObjTrnDcpsTerminalDtls.setR3_attachId(r3_attachId);
					}
				}
			}

			if (lStrUser.equals("DDO") && lStrUse.equals("NewRequest")) {
				lLongTerminalId = IFMSCommonServiceImpl.getNextSeqNum("trn_dcps_terminal_dtls", inputMap);
				lObjTrnDcpsTerminalDtls.setTerminalId(lLongTerminalId);

				if (lStrSaveOrForwardFlag.equals("1")) // save by DDO
				{
					lObjTrnDcpsTerminalDtls.setStatusFlag(0l);
				} else if (lStrSaveOrForwardFlag.equals("2")) // forward by DDO
					// to TO
				{
					lObjTrnDcpsTerminalDtls.setStatusFlag(1l);
				}

				lObjTrnDcpsTerminalDtls.setDdoCode(lStrDDOCode);
				lObjTrnDcpsTerminalDtls.setTreasuryCode(lLongTreasuryCode);

				lObjTrnDcpsTerminalDtls.setCreatedPostId(gLngPostId);
				lObjTrnDcpsTerminalDtls.setCreatedUserId(gLngUserId);
				lObjTrnDcpsTerminalDtls.setCreatedDate(gDtCurrDt);
				lObjTerminalRequestDAO.create(lObjTrnDcpsTerminalDtls);
			} else if (lStrUser.equals("DDO") && lStrUse.equals("FromDraft")) {
				if (lStrSaveOrForwardFlag.equals("1")) // save by DDO
				{
					lObjTrnDcpsTerminalDtls.setStatusFlag(0l);
				} else if (lStrSaveOrForwardFlag.equals("2")) // forward by DDO
					// to TO
				{
					lObjTrnDcpsTerminalDtls.setStatusFlag(1l);
				}
				lObjTrnDcpsTerminalDtls.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsTerminalDtls.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsTerminalDtls.setUpdatedDate(gDtCurrDt);
				lObjTerminalRequestDAO.update(lObjTrnDcpsTerminalDtls);
			} else if (lStrUser.equals("TO")) {
				if (lStrSaveOrForwardFlag.equals("3")) // save by TO
				{
					lObjTrnDcpsTerminalDtls.setStatusFlag(1l);
				} else if (lStrSaveOrForwardFlag.equals("4")) // forward by TO
					// to DDO
				{
					lObjTrnDcpsTerminalDtls.setStatusFlag(2l);
				}
				lObjTrnDcpsTerminalDtls.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsTerminalDtls.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsTerminalDtls.setUpdatedDate(gDtCurrDt);
				lObjTerminalRequestDAO.update(lObjTrnDcpsTerminalDtls);
			} else if (lStrUser.equals("DDO") && lStrUse.equals("FromTO")) {
				if (lStrSaveOrForwardFlag.equals("5")) // Forward to SRKA by DDO
					// after getting from TO
				{
					lObjTrnDcpsTerminalDtls.setStatusFlag(3l);
				}
				lObjTrnDcpsTerminalDtls.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsTerminalDtls.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsTerminalDtls.setUpdatedDate(gDtCurrDt);
				lObjTerminalRequestDAO.update(lObjTrnDcpsTerminalDtls);
			} else if (lStrUser.equals("SRKA")) {
				lObjTrnDcpsTerminalDtls.setStatusFlag(4l);

				lObjTrnDcpsTerminalDtls.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsTerminalDtls.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsTerminalDtls.setUpdatedDate(gDtCurrDt);
				lObjTerminalRequestDAO.update(lObjTrnDcpsTerminalDtls);

				// Entry in contribution yearly table

				TerminalRequestDAO lObjTerminalRequestDAOForYearly = new TerminalRequestDAOImpl(MstDcpsContributionYearly.class, serv.getSessionFactory());
				MstDcpsContributionYearly lObjMstDcpsContributionYearly = (MstDcpsContributionYearly) inputMap.get("lObjMstDcpsContributionYearly");
				lObjMstDcpsContributionYearly.setCreatedDate(gDtCurDate);
				lObjMstDcpsContributionYearly.setCreatedPostId(gLngPostId);
				lObjMstDcpsContributionYearly.setCreatedUserId(gLngUserId);

				lObjTerminalRequestDAOForYearly.create(lObjMstDcpsContributionYearly);
				// end of Entry in contribution yearly table
			}

			if ((lStrUser.equals("DDO") && (lStrUse.equals("NewRequest") || lStrUse.equals("FromDraft"))) || (lStrUser.equals("TO")))

			{
				List<TrnDcpsMissingCreditsDtls> lListTrnDcpsMissingCredits = (List<TrnDcpsMissingCreditsDtls>) inputMap.get("lListTrnDcpsMissingCredits");
				TrnDcpsMissingCreditsDtls lObjTrnDcpsMissingCreditsDtls = null;
				Long lLongMissingCreditId = null;
				String lStrHidMissingCreditSPK = null;

				lLongTerminalIdPassed = Long.valueOf(StringUtility.getParameter("hidTerminalId", request).trim());

				Integer lIntTotalMissingCredits = lListTrnDcpsMissingCredits.size();
				for (Integer lInt = 0; lInt < lIntTotalMissingCredits; lInt++) {
					lObjTrnDcpsMissingCreditsDtls = lListTrnDcpsMissingCredits.get(lInt);
					lStrHidMissingCreditSPK = StringUtility.getParameter("hidMissingCreditPK" + (lInt + 1), request).trim();

					if (lStrHidMissingCreditSPK.equals("0")) {
						lLongMissingCreditId = IFMSCommonServiceImpl.getNextSeqNum("trn_dcps_missing_credits_dtls", inputMap);
						lObjTrnDcpsMissingCreditsDtls.setMissingCreditId(lLongMissingCreditId);

						// Below 3 lines set terminal Id for the records in
						// Missing Credit table in draft which were not saved
						// earlier
						if (lLongTerminalId == null && lStrUser.equals("DDO") && lStrUse.equals("FromDraft")) {
							lLongTerminalId = lLongTerminalIdPassed;
						}

						lObjTrnDcpsMissingCreditsDtls.setRltTerminalId(lLongTerminalId);

						lObjTrnDcpsMissingCreditsDtls.setDdoCode(lStrDDOCode);
						lObjTrnDcpsMissingCreditsDtls.setTreasuryCode(lLongTreasuryCode);

						lObjTrnDcpsMissingCreditsDtls.setCreatedPostId(gLngPostId);
						lObjTrnDcpsMissingCreditsDtls.setCreatedUserId(gLngUserId);
						lObjTrnDcpsMissingCreditsDtls.setCreatedDate(gDtCurrDt);
						lObjTerminalRequestMissingCreditsDAO.create(lObjTrnDcpsMissingCreditsDtls);
					} else {
						lObjTrnDcpsMissingCreditsDtls.setUpdatedPostId(gLngPostId);
						lObjTrnDcpsMissingCreditsDtls.setUpdatedUserId(gLngUserId);
						lObjTrnDcpsMissingCreditsDtls.setUpdatedDate(gDtCurrDt);
						lObjTerminalRequestMissingCreditsDAO.update(lObjTrnDcpsMissingCreditsDtls);
					}
				}

			}


			/*  objRes = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);

			  objRes = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			 * Map attachMap = (Map) objRes.getResultValue();
			 * 
			 * Long lLngAttachId = 0L; if (attachMap.get("AttachmentId_Photo")
			 * != null) { lLngAttachId = Long.parseLong(String.valueOf(attachMap
			 * .get("AttachmentId_Photo")));
			 * lObjEmpData.setPhotoAttachmentID(lLngAttachId); }
			 * 
			 * if (attachMap.get("AttachmentId_Sign") != null) { lLngAttachId =
			 * Long.parseLong(String.valueOf(attachMap
			 * .get("AttachmentId_Sign")));
			 * lObjEmpData.setSignatureAttachmentID(lLngAttachId); }
			 * 
			 * lLngEmpId = IFMSCommonServiceImpl.getNextSeqNum("MST_DCPS_EMP",
			 * inputMap); lObjEmpData.setDcpsEmpId(lLngEmpId);
			 * lObjNewRegDdoDAO.create(lObjEmpData);
			 * 
			 * lObjRltDcpsPayrollEmp = new RltDcpsPayrollEmp();
			 * lObjRltDcpsPayrollEmp = (RltDcpsPayrollEmp) inputMap
			 * .get("DCPSEmpPayrollData"); lLngDcpsPayrollEmpId =
			 * IFMSCommonServiceImpl.getNextSeqNum( "RLT_DCPS_PAYROLL_EMP",
			 * inputMap); lObjRltDcpsPayrollEmp.setDcpsEmpId(lLngEmpId);
			 * lObjRltDcpsPayrollEmp.setDcpsPayrollEmpId(lLngDcpsPayrollEmpId);
			 * lObjNewRegDdoDAO.create(lObjRltDcpsPayrollEmp);
			 */

			lBlFlag = true;

		} catch (Exception ex) {
			resObj.setResultValue(null);
			gLogger.error(" Error is : " + ex, ex);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject popUpTerminalDetails(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrUser = null;
		String lStrUse = null;
		List listMissingCredits = null;
		List listReasonsForTermination = null;
		Long lLongTerminalId = null;
		Long lLongDcpsEmpId = null;
		TrnDcpsTerminalDtls lObjTrnDcpsTerminalDtls = null;
		MstEmp lObjMstEmp = null;
		String lStrGoPressed = null;
		String lStrName = null;
		String lStrEmpId = null;
		String lStrDcpsId = null;
		String lStrDOJ = null;
		Date lDtDOJ = null;
		String lStrTerminationDate = null;
		Date lDtTerminationDate = null;
		String lStrReasonForTermination = null;

		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, serv.getSessionFactory());
			TerminalRequestDAO lObjTerminalMstEmpDAO = new TerminalRequestDAOImpl(MstEmp.class, serv.getSessionFactory());

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			listReasonsForTermination = IFMSCommonServiceImpl.getLookupValues("ReasonsForTermination", SessionHelper.getLangId(inputMap), inputMap);
			inputMap.put("listReasonsForTermination", listReasonsForTermination);

			lStrUser = StringUtility.getParameter("User", request).trim();
			lStrUse = StringUtility.getParameter("Use", request).trim();
			lLongTerminalId = Long.valueOf(StringUtility.getParameter("terminalId", request).trim());

			lObjTrnDcpsTerminalDtls = (TrnDcpsTerminalDtls) lObjTerminalRequestDAO.read(lLongTerminalId);
			lLongDcpsEmpId = lObjTrnDcpsTerminalDtls.getDcpsEmpId();
			lObjMstEmp = (MstEmp) lObjTerminalMstEmpDAO.read(lLongDcpsEmpId);
			lStrName = lObjMstEmp.getName().trim();
			lStrEmpId = lLongDcpsEmpId.toString();
			lStrDcpsId = lObjMstEmp.getDcpsId();
			lStrDOJ = sdf.format(lObjMstEmp.getDoj());

			lStrGoPressed = StringUtility.getParameter("GoPressed", request).trim();

			if (lStrGoPressed.equals("Yes") && lStrUser.equals("DDO")) {
				lStrTerminationDate = StringUtility.getParameter("txtTerminationDate", request).trim();
				lDtTerminationDate = sdf.parse(lStrTerminationDate);
				lDtDOJ = sdf.parse(lStrDOJ);
				lStrReasonForTermination = StringUtility.getParameter("cmbReasonForTermination", request).trim();
				listMissingCredits = lObjTerminalRequestDAO.getAllMissingCreditsForEmp(lLongDcpsEmpId, lDtDOJ, lDtTerminationDate);
			} else {
				listMissingCredits = lObjTerminalRequestDAO.getAllMissingCreditsSavedForTerminalId(lLongTerminalId);
				lStrTerminationDate = sdf.format(lObjTrnDcpsTerminalDtls.getDateOfTermination());
				lStrReasonForTermination = lObjTrnDcpsTerminalDtls.getReasonOfTermination().toString();
			}

			CmnAttachmentMstDAO lObjCmnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
			CmnAttachmentMst lObjCmnAttachmentMst = null;
			if (lObjTrnDcpsTerminalDtls.getFormA_attachId() != null) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(lObjTrnDcpsTerminalDtls.getFormA_attachId());
				inputMap.put("FormA", lObjCmnAttachmentMst);
			}
			if (lObjTrnDcpsTerminalDtls.getDeathCerti_attachId() != null) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(lObjTrnDcpsTerminalDtls.getDeathCerti_attachId());
				inputMap.put("DeathCertificate", lObjCmnAttachmentMst);
			}
			if (lObjTrnDcpsTerminalDtls.getR3_attachId() != null) {
				lObjCmnAttachmentMst = new CmnAttachmentMst();
				lObjCmnAttachmentMst = lObjCmnAttachmentMstDAO.findByAttachmentId(lObjTrnDcpsTerminalDtls.getR3_attachId());
				inputMap.put("R3Report", lObjCmnAttachmentMst);
			}

			inputMap.put("lStrName", lStrName);
			inputMap.put("lStrEmpId", lStrEmpId);
			inputMap.put("lStrDcpsId", lStrDcpsId);
			inputMap.put("lStrDOJ", lStrDOJ);
			inputMap.put("lStrTerminationDate", lStrTerminationDate);
			inputMap.put("lStrReasonForTermination", lStrReasonForTermination);

			inputMap.put("listMissingCredits", listMissingCredits);
			inputMap.put("hidUser", lStrUser);
			inputMap.put("hidUse", lStrUse);
			inputMap.put("hidTerminalId", lLongTerminalId);

			if (lStrUser.equals("SRKA")) {
				FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
				Long lLngFinYearId = new Long(lObjFinancialYearDAO.getFinYearIdByCurDate());
				lDtTerminationDate = sdf.parse(lStrTerminationDate);
				Double lDblOpeningBal = lObjTerminalRequestDAO.getOpeningBalanceForDcpsId(lStrDcpsId, lLngFinYearId);

				Double lDblEmplrContriPaid = lObjTerminalRequestDAO.getPaidEmployerContributionForYear(lStrDcpsId, lLngFinYearId);
				Double lDblEmplrContriPending = lObjTerminalRequestDAO.getPendingEmployerContributionForYear(lStrDcpsId, lLngFinYearId);
				Double lDblMissingCredits = lObjTerminalRequestDAO.getTotalMissingCreditsForEmp(lStrDcpsId);
				Double lDblTier2Contri = lObjTerminalRequestDAO.getTier2ContributionForYear(lStrDcpsId, lLngFinYearId);

				Date lDtPreYearEndDate = lObjTerminalRequestDAO.getEndDateForFinYear(lLngFinYearId - 1);
				Long lLngDaysDiff = (lDtTerminationDate.getTime() - lDtPreYearEndDate.getTime()) / (1000 * 60 * 60 * 24);
				Double lDblDays = lLngDaysDiff.doubleValue() / 365;
				Double lDblTier2ContriInterest = lDblTier2Contri * lDblDays * 0.08;

				// interest on opening balance
				lLngDaysDiff = (lDtTerminationDate.getTime() - lDtPreYearEndDate.getTime()) / (1000 * 60 * 60 * 24);
				lDblDays = lLngDaysDiff.doubleValue() / 365;
				Double lDblInterestOpenBal = lDblOpeningBal * lDblDays * 0.08;

				// end interest on opening balance

				// interest calculation for current year contribution
				Double lDblInterestForContribution = 0d;
				List lLstCurrYearContribution = lObjTerminalRequestDAO.getContributionTillDate(lStrDcpsId, lLngFinYearId);
				Double lDblAmount = 0d;
				Date lDtVoucherDate = null;
				Double lDblInterest = 0d;
				if (lLstCurrYearContribution != null && lLstCurrYearContribution.size() > 0) {
					for (Integer index = 0; index < lLstCurrYearContribution.size(); index++) {
						Object[] lArrObj = (Object[]) lLstCurrYearContribution.get(index);
						lDblAmount = (Double) lArrObj[0];
						lDtVoucherDate = (Date) lArrObj[1];
						if (lDtVoucherDate != null) {
							lLngDaysDiff = (lDtTerminationDate.getTime() - lDtVoucherDate.getTime()) / (1000 * 60 * 60 * 24);
							lDblDays = lLngDaysDiff.doubleValue() / 365;
							lDblInterest = lDblAmount * lDblDays * 0.08;
							lDblInterestForContribution = lDblInterestForContribution + lDblInterest;
						}
					}
				}
				// end interest calculation for current year contribution

				// interest calculation for missing credits
				lDblAmount = 0d;
				lDtVoucherDate = null;
				Double lDblInterestForMissingCredits = 0d;
				Integer lIntMissingCreditYear = 0;
				Integer lIntCurrYear = lLngFinYearId.intValue();
				Character lCharMissingCreditsFlag = lObjTrnDcpsTerminalDtls.getMissingCreditEmployerContriFlag();
				List lLstMissingCredits = lObjTerminalRequestDAO.getMissingCreditsForDcpsId(lStrDcpsId);
				Object[] lArrObjMissingCredit = null;
				if (lLstMissingCredits != null && lLstMissingCredits.size() > 0) {
					Boolean lBlFirstYear = true;
					Double lDblInterestPerCredit = 0d;
					Date lDtYearEndDate = null;
					Date lDtYearStartDate = null;
					for (Integer index2 = 0; index2 < lLstMissingCredits.size(); index2++) {
						lArrObjMissingCredit = (Object[]) lLstMissingCredits.get(index2);
						lDblAmount = (Double) lArrObjMissingCredit[0];
						lDtVoucherDate = (Date) lArrObjMissingCredit[1];

						lIntMissingCreditYear = lObjFinancialYearDAO.getFinYearId(sdf.format(lDtVoucherDate));
						lBlFirstYear = true;
						lDblInterestPerCredit = 0d;
						for (Integer index3 = lIntMissingCreditYear; index3 <= lIntCurrYear; index3++) {

							if (index3 == lIntCurrYear) {
								lDtYearStartDate = lObjTerminalRequestDAO.getStartDateForFinYear(index3.longValue());
								if (index3 == lIntMissingCreditYear) {
									lDtYearStartDate = lDtVoucherDate;
								}
								lLngDaysDiff = (lDtTerminationDate.getTime() - lDtYearStartDate.getTime()) / (1000 * 60 * 60 * 24);
								lDblDays = lLngDaysDiff.doubleValue() / 365;
								lDblInterest = lDblAmount * lDblDays * 0.08;
								lDblInterestPerCredit = lDblInterestPerCredit + lDblInterest;

							} else {
								if (lBlFirstYear) {
									lDtYearEndDate = lObjTerminalRequestDAO.getEndDateForFinYear(index3.longValue());
									if (lDtVoucherDate != null) {
										lLngDaysDiff = (lDtYearEndDate.getTime() - lDtVoucherDate.getTime()) / (1000 * 60 * 60 * 24);
										lDblDays = lLngDaysDiff.doubleValue() / 365;
										lDblInterest = lDblAmount * lDblDays * 0.08;
										lDblInterestPerCredit = lDblInterestPerCredit + lDblInterest;
									}
									lBlFirstYear = false;
								} else {
									lDblInterest = lDblAmount * 0.08;
									lDblInterestPerCredit = lDblInterestPerCredit + lDblInterest;
								}
							}
						}
						lDblInterestForMissingCredits = lDblInterestForMissingCredits + lDblInterestPerCredit;
					}
				}
				if (lCharMissingCreditsFlag == null) {
					lDblEmplrContriPending = lDblEmplrContriPending + lDblMissingCredits;
				} else {
					lDblEmplrContriPaid = lDblEmplrContriPaid + lDblMissingCredits;
				}

				inputMap.put("lDblOpeningBal", lDblOpeningBal);
				inputMap.put("lDblCurrYearContriPaid", lDblEmplrContriPaid);
				inputMap.put("lDblCurrYearContriPending", lDblEmplrContriPending);

				inputMap.put("lDblInterestOpenBal", lDblInterestOpenBal);
				inputMap.put("lDblInterestForContribution", lDblInterestForContribution * 2);
				inputMap.put("lDblInterestForMissingCredits", lDblInterestForMissingCredits);
				inputMap.put("lDblTier2Contri", lDblTier2Contri);
				inputMap.put("lDblTier2ContriInterest", lDblTier2ContriInterest);

				resObj.setViewName("TerminalRequestSRKA");
			} else {
				resObj.setViewName("TerminalRequest");
			}
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject deleteTerminalRequest(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String lStrTerminalId = null;
		Long lLongTerminalId = null;
		Boolean lBlFlag = false;

		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, serv.getSessionFactory());
			lStrTerminalId = StringUtility.getParameter("terminalId", request).trim();
			lLongTerminalId = Long.valueOf(lStrTerminalId);

			TrnDcpsTerminalDtls lObjDcpsTerminalDtls = (TrnDcpsTerminalDtls) lObjTerminalRequestDAO.read(lLongTerminalId);
			lObjTerminalRequestDAO.deleteMissingCreditsSavedForTerminalId(lLongTerminalId);
			lObjTerminalRequestDAO.delete(lObjDcpsTerminalDtls);

			lBlFlag = true;
			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;
	}

	public ResultObject insertEmployerContribution(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBFlag = false;
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		try {
			setSessionInfo(inputMap);
			PostEmpContriDAO lObjPostEmpContriDAO = new PostEmpContriDAOImpl(PostEmpContri.class, serv.getSessionFactory());
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(TrnDcpsContribution.class, serv.getSessionFactory());
			TerminalRequestDAO lObjTerminalRequestDAOForTerminal = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, serv.getSessionFactory());
			FinancialYearDAO lObjFinancialYearDAO = new FinancialYearDAOImpl(null, serv.getSessionFactory());

			Long lLngFinYear = new Long(lObjFinancialYearDAO.getFinYearIdByCurDate());
			String lStrMonthId = sdf.format(gDtCurDate);
			String lStrBillAmount = StringUtility.getParameter("billAmount", request);
			String lStrDcpsId = StringUtility.getParameter("dcpsId", request);
			String lStrTerminalId = StringUtility.getParameter("terminalId", request);
			Long lLngTerminalId = 0l;
			if (lStrTerminalId != null && !lStrTerminalId.equals("")) {
				lLngTerminalId = Long.parseLong(lStrTerminalId);
			}
			TrnDcpsTerminalDtls lObjTrnDcpsTerminalDtls = (TrnDcpsTerminalDtls) lObjTerminalRequestDAOForTerminal.read(lLngTerminalId);
			lObjTrnDcpsTerminalDtls.setMissingCreditEmployerContriFlag('Y');
			lObjTerminalRequestDAOForTerminal.update(lObjTrnDcpsTerminalDtls);

			Double lDblBillAmount = 0d;
			String lStrAcDcpsMntndBy = StringUtility.getParameter("dcpsAcntMntndBy", request).trim();
			if (lStrBillAmount != null && !lStrBillAmount.equals("")) {
				lDblBillAmount = Double.parseDouble(lStrBillAmount);
			}
			String lStrBillNo = lObjPostEmpContriDAO.getBillNumber(lLngFinYear, lStrAcDcpsMntndBy);

			Long lLngExpenditureTillDate = lObjPostEmpContriDAO.getExpenditure(lLngFinYear, lStrAcDcpsMntndBy) + lDblBillAmount.longValue();
			Long lLngSanctionedBudget = lObjPostEmpContriDAO.getSancBudget(lLngFinYear);
			Long lLngExcessExpenditure = 0l;

			if (lLngExpenditureTillDate > lLngSanctionedBudget) {
				lLngExcessExpenditure = lLngExpenditureTillDate - lLngSanctionedBudget;
			}

			Long lLngDcpsEmpId = lObjTerminalRequestDAO.getDcpsEmpIdForDcpsId(lStrDcpsId);
			TrnDcpsContribution lObjTrnDcpsContribution = null;
			String[] lArrStrColumns = new String[]{"dcpsEmpId", "finYearId", "employerContriFlag"};
			Object[] lArrObjColumns = new Object[]{lLngDcpsEmpId, lLngFinYear, 'N'};
			List lLstContri = lObjTerminalRequestDAO.getListByColumnAndValue(lArrStrColumns, lArrObjColumns);
			if (lLstContri != null && lLstContri.size() > 0) {
				for (Integer index = 0; index < lLstContri.size(); index++) {
					lObjTrnDcpsContribution = (TrnDcpsContribution) lLstContri.get(0);
					lObjTrnDcpsContribution.setEmployerContriFlag('Y');
					lObjTerminalRequestDAO.update(lObjTrnDcpsContribution);
				}
			}
			PostEmpContri lObjPostEmpContri = new PostEmpContri();
			Long lLngPostEmpContriId = IFMSCommonServiceImpl.getNextSeqNum("MST_DCPS_POST_EMPLOYER_CONTRI", inputMap);

			lObjPostEmpContri.setDcpsPostEmpContriIdPk(lLngPostEmpContriId);

			lObjPostEmpContri.setFinYear(lLngFinYear);
			lObjPostEmpContri.setContriMonth(lStrMonthId);
			lObjPostEmpContri.setBillNo(lStrBillNo);
			lObjPostEmpContri.setExpenditureTillDate(lLngExpenditureTillDate);
			lObjPostEmpContri.setExcessExpenditure(lLngExcessExpenditure);
			lObjPostEmpContri.setStatusFlag('A');
			lObjPostEmpContri.setBillAmount(lDblBillAmount.longValue());
			lObjPostEmpContri.setPostId(gLngPostId);
			lObjPostEmpContri.setUserId(gLngUserId);
			lObjPostEmpContri.setCreatedDate(gDtCurDate);
			lObjPostEmpContriDAO.create(lObjPostEmpContri);

			lBFlag = true;

		} catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	//New Termination Code added by ashish

	//Load details for first screen 
	public ResultObject loadNewTerminalRequest(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List treasuryCodes = null;
		Long treasuryCode = null;
		String ddoCode = null;
		String EmpId = null;
		List empDetails = null;
		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());
			treasuryCodes = lObjTerminalRequestDAO.getAllTreasuries();
			inputMap.put("TREASURIES", treasuryCodes);

			if (!StringUtility.getParameter("treasuryCode", request).equalsIgnoreCase("") && StringUtility.getParameter("ddoCode", request) != null
					&& !StringUtility.getParameter("EmpName", request).equalsIgnoreCase("")) {
				treasuryCode = Long.valueOf(StringUtility.getParameter("treasuryCode", request));
				ddoCode = StringUtility.getParameter("ddoCode", request);
				EmpId = StringUtility.getParameter("EmpName", request);
				empDetails = lObjTerminalRequestDAO.getTerminationDetailsOfEmp(ddoCode, EmpId);
				String lSBStatus = getResponseXMLDocForEmpDtlsTermination(empDetails).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			} else {
				resObj.setViewName("NEWTerminalRequest");
				resObj.setResultValue(inputMap);
			}

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	//ddo from treasury
	public ResultObject getDDOForTreasury(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List ddoCodes = null;
		String selectedTreasuryCode = null;
		try {

			setSessionInfo(inputMap);
			selectedTreasuryCode = StringUtility.getParameter("treasuryCode", request);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());

			ddoCodes = lObjTerminalRequestDAO.getAllDDO(selectedTreasuryCode);

			gLogger.info("ddoCodes.size************" + ddoCodes.size());
			gLogger.info("ddoCodes content ************" + ddoCodes);
			String lSBStatus = getResponseXMLDocForDDOsForTreasury(ddoCodes).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDocForDDOsForTreasury(List finalList) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();

		Object[] obj = null;

		if (finalList != null) {
			obj = (Object[]) finalList.get(0);
		}
		gLogger.info("finalList.size************" + finalList.size());

		for (int i = 0; i < finalList.size(); i++) {
			obj = (Object[]) finalList.get(i);
			lStrBldXML.append("<XMLDOC>");

			lStrBldXML.append("<ddoCodes>");
			if (obj[0] != null) {
				lStrBldXML.append(obj[0].toString().trim());
			} else {
				lStrBldXML.append("");
			}

			lStrBldXML.append("</ddoCodes>");

			lStrBldXML.append("<ddoNames>");
			if (obj[1] != null) {
				lStrBldXML.append(obj[1].toString().trim());
			} else {
				lStrBldXML.append("");
			}
			lStrBldXML.append("</ddoNames>");

			lStrBldXML.append("</XMLDOC>");
		}

		gLogger.info("lStrBldXML************" + lStrBldXML);
		return lStrBldXML;
	}
	//Employee from DDO with partial terminate also(status flag=0)
	public ResultObject getEmpNamesForDdo(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List empInfo = null;
		String selectedTreasuryCode = null;
		String selectedDDocode = null;
		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());
			selectedTreasuryCode = StringUtility.getParameter("treasuryCode", request);
			selectedDDocode = StringUtility.getParameter("ddoCode", request);
			gLogger.info("Treasury code is " + selectedTreasuryCode);
			gLogger.info("DDO code is " + selectedDDocode);
			empInfo = lObjTerminalRequestDAO.getAllEmpsUnderDDO(selectedDDocode);
			gLogger.info("empInfo.size************" + empInfo.size());
			gLogger.info("empInfo content ************" + empInfo);
			String lSBStatus = getResponseXMLDocForEmpsForDDOs(empInfo).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDocForEmpsForDDOs(List finalList) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();

		Object[] obj = null;

		if (finalList != null) {
			obj = (Object[]) finalList.get(0);
		}
		gLogger.info("finalList.size************" + finalList.size());

		for (int i = 0; i < finalList.size(); i++) {
			obj = (Object[]) finalList.get(i);
			lStrBldXML.append("<XMLDOC>");

			lStrBldXML.append("<empName>");
			if (obj[0] != null) {
				lStrBldXML.append(obj[0].toString().trim());
			} else {
				lStrBldXML.append("");
			}
			lStrBldXML.append("</empName>");

			lStrBldXML.append("<dcpsid>");
			if (obj[1] != null) {
				lStrBldXML.append(obj[1].toString().trim());
			} else {
				lStrBldXML.append("");
			}
			lStrBldXML.append("</dcpsid>");

			lStrBldXML.append("</XMLDOC>");
		}

		gLogger.info("lStrBldXML************" + lStrBldXML);
		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForEmpDtlsTermination(List finalList) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();
		SimpleDateFormat lObjSimpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat lObjSimpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
		Object[] obj = null;

		if (finalList != null) {
			obj = (Object[]) finalList.get(0);
		}

		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<empName>");
		if (obj[0] != null) {
			lStrBldXML.append(obj[0].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</empName>");

		lStrBldXML.append("<dcpsId>");
		if (obj[1] != null) {
			lStrBldXML.append(obj[1].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</dcpsId>");

		lStrBldXML.append("<sevaarthId>");
		if (obj[2] != null) {
			lStrBldXML.append(obj[2].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</sevaarthId>");

		lStrBldXML.append("<ddoName>");
		if (obj[3] != null) {
			lStrBldXML.append(obj[3].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</ddoName>");

		lStrBldXML.append("<desgName>");
		if (obj[4] != null) {
			lStrBldXML.append(obj[4].toString().trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</desgName>");

		lStrBldXML.append("<doj>");
		if (obj[5] != null) {
			lStrBldXML.append(lObjSimpleDateFormat2.format(lObjSimpleDateFormat1.parse(obj[5].toString().trim())).trim());
		} else {
			lStrBldXML.append("");
		}
		lStrBldXML.append("</doj>");

		lStrBldXML.append("</XMLDOC>");
		gLogger.info("lStrBldXML************" + lStrBldXML);
		return lStrBldXML;
	}
	//Details of termination
	public ResultObject loadNewConfirmTerminalRequest(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List treasuryCodes = null;
		Long treasuryCode = null;
		String ddoCode = null;
		String empId = null;
		String empName = null;
		String sevaarthId = null;
		String officeName = null;
		String designId = null;
		String doj = null;
		String serEndDate = null;
		List listReasonsForTermination = null;
		MstEmp lObjMstEmp = null;
		String lStrDOJ = null;
		Date lDtDOJ = null;
		List partialTerminateDtls=null;
		Object [] lObjTerm=null;
		String orderNo=null;
		String orderDate=null;
		String terDate=null;
		String authNo=null;
		String reasonOfTer=null;

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());

			if (!StringUtility.getParameter("treasuryCode", request).equalsIgnoreCase("") && StringUtility.getParameter("ddoCode", request) != null
					&& !StringUtility.getParameter("empName", request).equalsIgnoreCase("")) {
				treasuryCode = Long.valueOf(StringUtility.getParameter("treasuryCode", request));
				ddoCode = StringUtility.getParameter("ddoCode", request);
				empId = StringUtility.getParameter("dcpsId", request);
				empName = StringUtility.getParameter("empName", request);
				sevaarthId = StringUtility.getParameter("sevaarthId", request);
				officeName = StringUtility.getParameter("officeName", request);
				designId = StringUtility.getParameter("designId", request);
				lStrDOJ = StringUtility.getParameter("doj", request);
				lDtDOJ = sdf.parse(lStrDOJ);

				serEndDate = (lObjTerminalRequestDAO.getSuperAnnDateexist(sevaarthId) != null) ? lObjTerminalRequestDAO.getSuperAnnDateexist(sevaarthId).toString() : "blank";
				gLogger.info("serEndDate *************" + serEndDate);
				partialTerminateDtls=lObjTerminalRequestDAO.getTerminationDetailsForPartialTerminate(empId);
				listReasonsForTermination = IFMSCommonServiceImpl.getLookupValues("Reason For Termination", gLngLangId, inputMap);
				if(partialTerminateDtls!=null && partialTerminateDtls.size()>0){

					lObjTerm = (Object[]) partialTerminateDtls.get(0);
					orderNo=lObjTerm[0].toString();
					orderDate=lObjTerm[1].toString();
					terDate=lObjTerm[2].toString();
					reasonOfTer=lObjTerm[3].toString();
					authNo=lObjTerm[4].toString();
					gLogger.info("Details for partail terminate employees are :::"+orderNo+":::"+orderDate+":::"+terDate+":::"+authNo+":::"+reasonOfTer);
					gLogger.info("listReasonsForTermination size is "+listReasonsForTermination.size());
				}
				inputMap.put("orderNo", orderNo);
				inputMap.put("orderDate", orderDate);
				inputMap.put("terDate", terDate);
				inputMap.put("authNo", authNo);
				inputMap.put("reasonOfTer", reasonOfTer);
				inputMap.put("listReasonsForTermination", listReasonsForTermination);
				inputMap.put("serEndDate", serEndDate);
				inputMap.put("treasuryCode", treasuryCode);
				inputMap.put("ddoCode", ddoCode);
				inputMap.put("empId", empId);
				inputMap.put("empName", empName);
				inputMap.put("sevaarthId", sevaarthId);
				inputMap.put("officeName", officeName);
				inputMap.put("designId", designId);
				inputMap.put("doj", doj);
				inputMap.put("lStrDOJ", lStrDOJ);

				resObj.setViewName("TERMINATIONCONFIRM");
				resObj.setResultValue(inputMap);
			}

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	//Check eligiblity
	public ResultObject checkTerminationEligiblity(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Long yearId = 0l;
		String terminationDate = null;
		String dcpsId=null;
		String selectedDDocode = null;
		boolean checkStatus=false;
		String arrDt [] =null;
		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());
			terminationDate = StringUtility.getParameter("termDate", request);
			gLogger.info("term Date is  before parse" + terminationDate);
			dcpsId = StringUtility.getParameter("dcpsId", request);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date date1 = sdf.parse(terminationDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			arrDt=terminationDate.split("/");
			int day=Integer.parseInt(arrDt[0].toString());
			int month=Integer.parseInt(arrDt[1].toString());
			int year=Integer.parseInt(arrDt[2].toString());
			gLogger.info("year is " +year);
			/*int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int year = cal.get(Calendar.YEAR);*/
			yearId = lObjTerminalRequestDAO.getfinYearIdFromYr(year);
			if(month<4)
			{
				yearId=yearId-1;
			}
			gLogger.info("month is " + month);
			gLogger.info("dcpsId is " +dcpsId);
			gLogger.info("yearId is " +yearId);
			checkStatus = lObjTerminalRequestDAO.checkPrvInterestCal(dcpsId,yearId);
			gLogger.info("status is ************" + checkStatus);

			String lSBStatus = getResponseXMLDocForTerminationElig(checkStatus).toString();
			gLogger.info("final status is "+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDocForTerminationElig(boolean status) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();

		gLogger.info("status is AJAX************" + status);



		lStrBldXML.append("<XMLDOC>"); 

		lStrBldXML.append("<MESSAGE>");

		lStrBldXML.append(status);

		lStrBldXML.append("</MESSAGE>");

		lStrBldXML.append("</XMLDOC>");

		gLogger.info("lStrBldXML************" + lStrBldXML);
		return lStrBldXML;
	}

	//generate prev R3
	public ResultObject generatePrvR3(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List treasuryCodes = null;
		Long treasuryCode = null;
		String ddoCode = null;
		String EmpId = null;
		List empDetails = null;
		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());


			String terminationDate = StringUtility.getParameter("termDate", request);
			gLogger.info("term Date is  before parse" + terminationDate);
			String dcpsId = StringUtility.getParameter("dcpsId", request);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date date1 = sdf.parse(terminationDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			String[] arrDt = terminationDate.split("/");
			int day=Integer.parseInt(arrDt[0].toString());
			int month=Integer.parseInt(arrDt[1].toString());
			int year=Integer.parseInt(arrDt[2].toString());
			gLogger.info("year is " +year);
			/*int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int year = cal.get(Calendar.YEAR);*/

			long dcpsEmpId=lObjTerminalRequestDAO.getDcpsEmpIdForDcpsId(dcpsId);

			long yearId = lObjTerminalRequestDAO.getfinYearIdFromYr(year);

			if(month<4)
			{
				yearId=yearId-1;
			}

			Object [] lObj1=null;
			List Date=null;
			String fromDate=null;
			String toDate=null;
			Date=lObjTerminalRequestDAO.getFromToDate(yearId);
			lObj1 = (Object[]) Date.get(0);
			if(lObj1[0]!=null){
				fromDate=lObj1[0].toString();

			}

			if(lObj1[1]!=null){
				toDate=lObj1[1].toString();
			}
			yearId=yearId-1;
			gLogger.info("yearId is " +yearId);
			Double openEmp=lObjTerminalRequestDAO.getOpeningBalanceTier1(dcpsId,yearId);
			gLogger.info("openEmp is " +openEmp);
			Double empContri=(lObjTerminalRequestDAO.getEmployeeContributionNonMissingTier1(dcpsEmpId+"",yearId))+(lObjTerminalRequestDAO.getEmployeeContributionWithMissingTier1(dcpsEmpId+"",fromDate,toDate));
			gLogger.info("empContri is " +empContri);
			Double getIntrst=lObjTerminalRequestDAO.getInterestTier1(dcpsId,yearId);

			gLogger.info("getIntrst is " +getIntrst);
			List getTier2Contri=lObjTerminalRequestDAO.getContributionAndIntTier2(dcpsId,yearId);
			Object [] lObj2=null;

			Double tier2Contri=null;
			Double tier2Int=null;
			Double empIntrstT2=null;
			Double emplrIntrstT2=null;
			Double lInterestTier1=null;
			Double totalInt=null;
			Double lOpeningEmpTier1=null;
			Long totalAmtCredit=null;


			if(getTier2Contri!=null && getTier2Contri.size()>0){
				lObj2 = (Object[]) getTier2Contri.get(0);
				tier2Contri=Double.parseDouble(lObj2[1].toString());
				tier2Int=Double.parseDouble(lObj2[3].toString());
				empIntrstT2=Double.parseDouble(lObj2[4].toString());
				emplrIntrstT2=Double.parseDouble(lObj2[5].toString());
				lInterestTier1=Double.parseDouble(lObj2[8].toString());
				totalInt=Double.valueOf(tier2Int.doubleValue() + empIntrstT2.doubleValue() );
				lOpeningEmpTier1=Double.parseDouble(lObj2[6].toString());


				gLogger.info("tier2Contri is " +tier2Contri);
				gLogger.info("tier2Int is " +tier2Int);
				gLogger.info("empIntrstT2 is " +empIntrstT2);
				gLogger.info("emplrIntrstT2 is " +emplrIntrstT2);
				gLogger.info("lInterestTier1 is " +lInterestTier1);
				gLogger.info("totalInt is " +totalInt);
				gLogger.info("lOpeningEmpTier1 is " +lOpeningEmpTier1);

				Double totalContr=Double.valueOf(empContri.doubleValue()+ tier2Contri.doubleValue());
				gLogger.info("totalContr is " +totalContr);

				Double totalEmpAmt=Double.valueOf( lOpeningEmpTier1.doubleValue() + totalInt.doubleValue() + totalContr.doubleValue() );
				gLogger.info("totalEmpAmt is " +totalEmpAmt);

				Double totalEmplyrAmt=Double.valueOf(totalEmpAmt.doubleValue() + lOpeningEmpTier1.doubleValue() + emplrIntrstT2.doubleValue() ); 
				gLogger.info("totalEmplyrAmt is " +totalEmplyrAmt);

				totalAmtCredit=Math.round(Double.valueOf(totalEmpAmt.doubleValue() + totalEmplyrAmt.doubleValue() + lInterestTier1.doubleValue() )) ;
				gLogger.info("totalAmtCredit is " +totalAmtCredit);



			}
			String lSBStatus = getResponseXMLDocForgeneratePrvR3(openEmp,empContri,empContri,getIntrst,tier2Contri,tier2Int,totalAmtCredit).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);


		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private Object getResponseXMLDocForgeneratePrvR3(Double openEmp, Double empContri, Double empContri2, Double getIntrst, Double tier2Contri, Double tier2Int, Long totalAmtCredit) {

		StringBuilder lStrBldXML = new StringBuilder();



		lStrBldXML.append("<XMLDOC>"); 

		lStrBldXML.append("<openEmp>");

		lStrBldXML.append(openEmp);

		lStrBldXML.append("</openEmp>");
		lStrBldXML.append("<empContri>");
         gLogger.info("Employee contri is "+empContri);
		lStrBldXML.append(empContri);

		lStrBldXML.append("</empContri>");
		lStrBldXML.append("<empContri2>");

		lStrBldXML.append(empContri2);

		lStrBldXML.append("</empContri2>");

		lStrBldXML.append("<getIntrst>");

		lStrBldXML.append(getIntrst);

		lStrBldXML.append("</getIntrst>");
		lStrBldXML.append("<tier2Contri>");

		lStrBldXML.append(tier2Contri);

		lStrBldXML.append("</tier2Contri>");
		lStrBldXML.append("<tier2Int>");

		lStrBldXML.append(tier2Int);

		lStrBldXML.append("</tier2Int>");
		lStrBldXML.append("<totalAmtCredit>");

		lStrBldXML.append(totalAmtCredit);

		lStrBldXML.append("</totalAmtCredit>");

		lStrBldXML.append("</XMLDOC>");

		gLogger.info("lStrBldXML************" + lStrBldXML);
		return lStrBldXML;
	}

	//saveTerminationDtls
	public ResultObject saveTerminationDtls(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		long lLongTerminalId=0l;
         String genStatus="No";
		try {
			setSessionInfo(inputMap);

			String  openEmp= StringUtility.getParameter("openEmp", request);
			gLogger.info("openEmp:::::"+openEmp);
			String empContri= StringUtility.getParameter("empContri", request);
			String empContri2= StringUtility.getParameter("empContri2", request);
			String getIntrst= StringUtility.getParameter("getIntrst", request);
			String tier2Contri= StringUtility.getParameter("tier2Contri", request);
			String tier2Int= StringUtility.getParameter("tier2Int", request);
			String totalAmtCredit= StringUtility.getParameter("totalAmtCredit", request);
			String txtOrderNo= StringUtility.getParameter("txtOrderNo", request);
			String txtOrderDate= StringUtility.getParameter("txtOrderDate", request);
			String cmbReasonForTermination= StringUtility.getParameter("cmbReasonForTermination", request);
			String txtTerminationDate=StringUtility.getParameter("txtTerminationDate", request);
			gLogger.info("txtTerminationDate:::::"+txtTerminationDate);
			String dcpsId= StringUtility.getParameter("dcpsId", request);
			String treasuryCode= StringUtility.getParameter("treasuryCode", request);
			String ddoCode= StringUtility.getParameter("ddoCode", request);
			String txtLetterNo= StringUtility.getParameter("txtLetterNo", request);
			String empName= StringUtility.getParameter("empName", request);
			String offName= StringUtility.getParameter("offName", request);
			String txtsevaarthId= StringUtility.getParameter("txtsevaarthId", request);

			Long termId=null;


			Object [] lObjterdtls=null;
			TrnDcpsTerminalDtls lObjTrnDcpsTerminalDtls = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			TerminalRequestDAO lObjTerminalRequestDAOImpl = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, serv.getSessionFactory());
			//TerminalRequestDAO lObjTrnDcpsTerminalDtlsDAOImpl = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, serv.getSessionFactory());
			String reasonTerm=lObjTerminalRequestDAOImpl.getReasonOfTermination(cmbReasonForTermination);
			long dcpsEmpId=lObjTerminalRequestDAOImpl.getDcpsEmpIdForDcpsId(dcpsId);
			gLogger.info("dcpsEmpId:::::"+dcpsEmpId);
			List empListInterm=lObjTerminalRequestDAOImpl.checkEmployeeInPartialTermination(dcpsId);
			if(empListInterm!=null && empListInterm.size()>0){
				lObjterdtls = (Object[]) empListInterm.get(0);
				termId=Long.parseLong(lObjterdtls[0].toString());

				lObjTrnDcpsTerminalDtls =(TrnDcpsTerminalDtls) lObjTerminalRequestDAOImpl.read(Long.valueOf(termId));
				lObjTrnDcpsTerminalDtls.setDcpsEmpId(dcpsEmpId);
				lObjTrnDcpsTerminalDtls.setDateOfTermination(sdf.parse(txtTerminationDate));
				lObjTrnDcpsTerminalDtls.setReasonOfTermination(Long.parseLong(cmbReasonForTermination));
				lObjTrnDcpsTerminalDtls.setStatusFlag(0l);
				lObjTrnDcpsTerminalDtls.setDdoCode(ddoCode);
				lObjTrnDcpsTerminalDtls.setTreasuryCode(Long.parseLong(treasuryCode));
				lObjTrnDcpsTerminalDtls.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsTerminalDtls.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsTerminalDtls.setUpdatedDate(gDtCurDate);
				lObjTrnDcpsTerminalDtls.setOrderNo(txtOrderNo);
				lObjTrnDcpsTerminalDtls.setOrderDate(sdf.parse(txtOrderDate));
				lObjTrnDcpsTerminalDtls.setAuthorityLetterNo(txtLetterNo);
				lObjTrnDcpsTerminalDtls.setOpeningBalance(Double.parseDouble(openEmp));
				lObjTrnDcpsTerminalDtls.setEmpContriCurrYear(Double.parseDouble(empContri));
				lObjTrnDcpsTerminalDtls.setEmplrContriCurrYear(Double.parseDouble(empContri2));
				lObjTrnDcpsTerminalDtls.setInterestContri(Double.parseDouble(getIntrst));
				lObjTrnDcpsTerminalDtls.setTier2Contri(Double.parseDouble(tier2Contri));
				lObjTrnDcpsTerminalDtls.setTier2Interest(Double.parseDouble(tier2Int));
				lObjTrnDcpsTerminalDtls.setPayableAmount(Long.parseLong(totalAmtCredit));
				lObjTerminalRequestDAOImpl.update(lObjTrnDcpsTerminalDtls);


			}

			else{
				lObjTrnDcpsTerminalDtls=new TrnDcpsTerminalDtls();

				lLongTerminalId = lObjTerminalRequestDAOImpl.getNextSeqNum("TRN_DCPS_TERMINAL_DTLS");
				gLogger.info("lLongTerminalId:::::"+lLongTerminalId);
				lObjTrnDcpsTerminalDtls.setTerminalId(lLongTerminalId);
				lObjTrnDcpsTerminalDtls.setDcpsEmpId(dcpsEmpId);
				lObjTrnDcpsTerminalDtls.setDateOfTermination(sdf.parse(txtTerminationDate));
				lObjTrnDcpsTerminalDtls.setReasonOfTermination(Long.parseLong(cmbReasonForTermination));
				lObjTrnDcpsTerminalDtls.setStatusFlag(0l);
				lObjTrnDcpsTerminalDtls.setDdoCode(ddoCode);
				lObjTrnDcpsTerminalDtls.setTreasuryCode(Long.parseLong(treasuryCode));
				lObjTrnDcpsTerminalDtls.setLangId(gLngLangId);
				lObjTrnDcpsTerminalDtls.setDbId(99l);
				lObjTrnDcpsTerminalDtls.setLocId(Long.parseLong(gStrLocationCode));
				lObjTrnDcpsTerminalDtls.setCreatedPostId(gLngPostId);
				lObjTrnDcpsTerminalDtls.setCreatedUserId(gLngUserId);
				lObjTrnDcpsTerminalDtls.setCreatedDate(gDtCurDate);
				lObjTrnDcpsTerminalDtls.setUpdatedPostId(gLngPostId);
				lObjTrnDcpsTerminalDtls.setUpdatedUserId(gLngUserId);
				lObjTrnDcpsTerminalDtls.setUpdatedDate(gDtCurDate);
				lObjTrnDcpsTerminalDtls.setOrderNo(txtOrderNo);
				lObjTrnDcpsTerminalDtls.setOrderDate(sdf.parse(txtOrderDate));
				lObjTrnDcpsTerminalDtls.setAuthorityLetterNo(txtLetterNo);
				lObjTrnDcpsTerminalDtls.setOpeningBalance(Double.parseDouble(openEmp));
				lObjTrnDcpsTerminalDtls.setEmpContriCurrYear(Double.parseDouble(empContri));
				lObjTrnDcpsTerminalDtls.setEmplrContriCurrYear(Double.parseDouble(empContri2));
				lObjTrnDcpsTerminalDtls.setInterestContri(Double.parseDouble(getIntrst));
				lObjTrnDcpsTerminalDtls.setTier2Contri(Double.parseDouble(tier2Contri));
				lObjTrnDcpsTerminalDtls.setTier2Interest(Double.parseDouble(tier2Int));
				lObjTrnDcpsTerminalDtls.setPayableAmount(Long.parseLong(totalAmtCredit));
				lObjTerminalRequestDAOImpl.create(lObjTrnDcpsTerminalDtls);
				
				Long lLongTerminalIdForUpdate = lLongTerminalId+Long.parseLong(txtOrderNo)+1;
				lObjTerminalRequestDAOImpl.updateGeneratedId(Long.valueOf(lLongTerminalIdForUpdate.toString()),"TRN_DCPS_TERMINAL_DTLS");
			}
		
			genStatus=lObjTerminalRequestDAOImpl.checkEmployeePrvR3gen(dcpsEmpId);

			inputMap.put("treasuryCode", treasuryCode);
			inputMap.put("ddoCode", ddoCode);
			inputMap.put("dcpsId", dcpsId);
			inputMap.put("txtOrderNo", txtOrderNo);
			inputMap.put("txtOrderDate", txtOrderDate);
			inputMap.put("txtTerminationDate", txtTerminationDate);
			inputMap.put("empName", empName);
			inputMap.put("offName", offName);
			inputMap.put("txtsevaarthId", txtsevaarthId);
			inputMap.put("reasonTerm", reasonTerm);
			inputMap.put("genStatus", genStatus);
			resObj.setViewName("TERMINATIONCONFIRMFINAL");
			resObj.setResultValue(inputMap);


		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}
	//Interest Calculation


	public ResultObject reCalculateDCPSInterest(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlFlag = false;
		String listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
		String listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = null;
		List dcpsEmpIdForSixPc = null;
		Long yearId = null;
		Long treasuryCode = null;
		String lStrFinYearStartDate = null;
		String lStrFinYearEndDate = null;
		SgvcFinYearMst lObjSgvcFinYearMst = null;
		SgvcFinYearMst lObjSgvcFinYearMstForVoucher = null;
		String lStrFromDatePassed=null;
		String lStrToDatePassed=null;
		String lStrArrearsLimitDate = null;

		Integer NumberOfDaysInGivenYear = null;


		MstDcpsContributionMonthly mstDcpsContributionMonthlyForInsertion = null;
		List<MstDcpsContributionMonthly> lstMstContriMonth = new ArrayList<MstDcpsContributionMonthly>();

		// No of samples decided here so as to restrict the no of values pass into IN clause of HQL query
		try {

			setSessionInfo(inputMap);

			HibernateTemplate hitStg = new HibernateTemplate(serv.getSessionFactory());

			InterestCalculationDAO lObjInterestCalculationDAO = new InterestCalculationDAOImpl(MstDcpsContributionYearly.class, serv.getSessionFactory());
			InterestCalculationDAO lObjInterestCalculationMonthlyDAO = new InterestCalculationDAOImpl(MstDcpsContributionMonthly.class, serv.getSessionFactory());
			TerminalRequestDAO lObjTerminalRequestDAOImpl = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(
					SgvcFinYearMst.class, serv.getSessionFactory());
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

			if (!StringUtility.getParameter("yearId", request).trim().equalsIgnoreCase("")) {
				yearId = Long.valueOf(StringUtility.getParameter("yearId",request).trim());
			}
			if (!StringUtility.getParameter("treasuryCode", request).trim().equalsIgnoreCase("")) {
				treasuryCode = Long.valueOf(StringUtility.getParameter("treasuryCode", request).trim());
			}
			String empName = StringUtility.getParameter("empName", request).trim();

			String ddoCode = StringUtility.getParameter("ddoCode", request).trim();
              gLogger.info("yearId is********"+yearId );
              
			lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(yearId);

			lStrFinYearStartDate = lObjSgvcFinYearMst.getFromDate().toString().trim();
			lStrFinYearEndDate = lObjSgvcFinYearMst.getToDate().toString().trim();



			List lListDt=lObjTerminalRequestDAOImpl.getFromToDt(yearId);


			Object [] lObj1=null;

			String fromDate=null;
			String toDate=null;

			lObj1 = (Object[]) lListDt.get(0);
			if(lObj1[0]!=null){
				lStrFromDatePassed=lObj1[0].toString();

			}

			if(lObj1[1]!=null){
				lStrToDatePassed=lObj1[1].toString();
			}



			String	terminationDate=StringUtility.getParameter("terDate", request).trim();

			/*		

		 String lStrFromDatePassed = StringUtility.getParameter("fromDate", request).trim();
			String lStrToDatePassed = StringUtility.getParameter("toDate", request).trim();*/

			if(!"".equals(lStrFromDatePassed))
			{
				lStrFromDatePassed = sdf2.format(sdf1.parse(lStrFromDatePassed));
			}
			if(!"".equals(lStrToDatePassed))
			{
				lStrToDatePassed = sdf2.format(sdf1.parse(lStrToDatePassed));
			}

			lStrArrearsLimitDate =  gObjRsrcBndle.getString("DCPS.ArrearsLimitDateForInterestCalc");
			lStrArrearsLimitDate = lStrArrearsLimitDate.trim().concat(lObjSgvcFinYearMst.getFinYearCode()).trim();
			Date lDateArrearsLimitDate = sdf1.parse(lStrArrearsLimitDate);

			Long previousFinYearId = lObjSgvcFinYearMst.getPrevFinYearId();

			GregorianCalendar grgcal = new GregorianCalendar();

			if (grgcal.isLeapYear(Integer.parseInt(lObjSgvcFinYearMst.getFinYearCode()) + 1)) {
				NumberOfDaysInGivenYear = 366;
			} else {
				NumberOfDaysInGivenYear = 365;
			}

			/*	String lStrInterestFor = StringUtility.getParameter("interestFor",request).trim();*/

			String totalDcpsEmpIds = null;



			/*	if(lStrInterestFor.equals("Treasury") || lStrInterestFor.equals("DDO"))
			{
				// Gets all DCPS_EMP_IDs which are eligible for Regular Contri
				listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalc(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);
				//listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = lObjInterestCalculationDAO.getEmpListForRegularConti(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);


				// Gets all DCPS_EMP_IDs which are eligible for Missing Credits
				listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = lObjInterestCalculationDAO.getAllDCPSEmployeesForIntrstCalcForMissingCredits(treasuryCode,ddoCode,lStrFromDatePassed, lStrToDatePassed);

				if(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits!=null && !listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.equals("") && listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc!=null && !listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.equals("")){
					totalDcpsEmpIds = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc +","+listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits;
				}
				else if (listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits!=null && !listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.equals("") && listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc==null && listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc.equals("") )
				{
					totalDcpsEmpIds = listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits;
				}

				else{
					totalDcpsEmpIds = listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc;
				}

			}*/
			/*	else if(lStrInterestFor.equals("Emp"))*/
			//{
			// Added by ashish to display only eligible employee list
			String dcpsIdd =StringUtility.getParameter("empId", request);

			String lStrEmpIds=lObjTerminalRequestDAOImpl.getDcpsEmpIdForDcpsId(dcpsIdd).toString();	
			gLogger.info("Employee Id  is"+lStrEmpIds);
			totalDcpsEmpIds = lObjTerminalRequestDAOImpl.checkEmployeeEligibleForIntrstCalc(lStrEmpIds, lStrFromDatePassed, lStrToDatePassed,yearId);
			String termDate=lObjTerminalRequestDAOImpl.getTerminationDt(lStrEmpIds);
			lStrEmpIds = null;



			//}

			List empDataForInterestCalcRegular = null;

			List empDataForInterestCalcMissingCredit = null;

			List dcpsIdForDeletion = new ArrayList();

			if(totalDcpsEmpIds!=null && !totalDcpsEmpIds.equals("")){
				/*	if(lStrInterestFor.equals("Treasury") || lStrInterestFor.equals("DDO")){
					empDataForInterestCalcRegular = lObjInterestCalculationDAO.getEmpListForRegularConti(listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc,lStrFromDatePassed, lStrToDatePassed,yearId.toString());

					if(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits!=null && !listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits.equals("")){
						empDataForInterestCalcMissingCredit = lObjInterestCalculationDAO.getEmpListForMissingCredit(listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
					}
				}*/

				/*	else if(lStrInterestFor.equals("Emp"))
				{
				 */
				empDataForInterestCalcRegular = lObjTerminalRequestDAOImpl.getEmpListForRegularConti(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
				empDataForInterestCalcMissingCredit = lObjTerminalRequestDAOImpl.getEmpListForMissingCredit(totalDcpsEmpIds,lStrFromDatePassed, lStrToDatePassed,yearId.toString());
				/*}*/
			}

			if(totalDcpsEmpIds!=null && !totalDcpsEmpIds.equals("")){
				dcpsEmpIdForSixPc = lObjTerminalRequestDAOImpl.getArrearsDtlsForAllEmps(totalDcpsEmpIds,yearId);
			}



			//Long dcpsContributionMonthlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_monthly",inputMap);

			Long dcpsContributionMonthlyId = lObjTerminalRequestDAOImpl.getNextSeqNum("MST_DCPS_CONTRIBUTION_MONTHLY");
			Long regularSize = 0l;
			Long missingCreditSize = 0l;
			Long sixPcSize = 0l;

			if(empDataForInterestCalcRegular != null){
				regularSize = Long.valueOf(empDataForInterestCalcRegular.size());
			}

			if(empDataForInterestCalcMissingCredit != null){
				missingCreditSize = Long.valueOf(empDataForInterestCalcMissingCredit.size());
			}

			if(dcpsEmpIdForSixPc != null){
				sixPcSize = Long.valueOf(dcpsEmpIdForSixPc.size());
				gLogger.info("sixPcSize is"+sixPcSize);
			}


			Long dcpsContributionMonthlyIdForUpdate = dcpsContributionMonthlyId + regularSize+missingCreditSize+sixPcSize+1;

			//lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionMonthlyIdForUpdate.toString().substring(6, dcpsContributionMonthlyIdForUpdate.toString().length())),"MST_DCPS_CONTRIBUTION_MONTHLY");
			lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionMonthlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_MONTHLY");
			listAllEmpsEmpIdsAndDCPSIdsForIntrstCalc = null;
			listAllEmpsEmpIdsAndDcpsIdsForIntrstCalcForMissingCredits = null;

			String voucherDate = null;
			int countMonth=0;
			Double contribution = null;
			String typeOfPayment = null;
			Long monthId = 0l;
			Long finYearCode = 0l;
			Long dcpsContriId = 0l;
			String voucherNo = null;
			Long voucherMonth = 0l;
			Long voucherYear = 0l;
			Long dcpsEmpId = null;
			String dcpsId = null;
			String contriDDOCode = null;
			String contriTresuryCode = null;
			String currDDOCode = null;
			String currTreasuryCode = null;
			//Long mstContriYearlyId = null;
			Double yearlyAmtSixpc=0d;
			Character isMissingCredit = null;
			Character isChalan = null;
			List interestRatesForVoucherNo = null;
			List interestRatesForMissingCredit=null;
			Iterator it = null;
			Iterator itr = null;
			Double interestForMonthlyContri = 0d;
			String payCommision=null;
			Double openEmployeeInt=0d; 
			Double  openEmployerInt=0d;
			Double closeEmpPrv=0d; 
			Double  closeEmplrPrv=0d;
			Double  closeTier2Prv=0d;
			Integer termDaysBetween=null;
			List lstDcpsContriId = new ArrayList();
			Integer terDaysBetween=null;
			Integer terVouDaysBetween=null;
			
			// Calculation of interest for Regular Month Contributions
			if(empDataForInterestCalcRegular!=null && empDataForInterestCalcRegular.size()>0){
				it = empDataForInterestCalcRegular.iterator();
				interestRatesForVoucherNo =  lObjTerminalRequestDAOImpl.getInterestRatesForVoucherNo(yearId);

				while(it.hasNext()){
					Object[] obj = (Object[]) it.next();
					voucherDate = obj[0]!=null ? obj[0].toString():"";
					contribution = obj[1]!=null ? Double.parseDouble(obj[1].toString()):0d;
					typeOfPayment = obj[2]!=null ? obj[2].toString():"";
					monthId = obj[3]!=null ? Long.parseLong(obj[3].toString()):0;
					finYearCode = obj[4]!=null ? Long.parseLong(obj[4].toString()):0;
					dcpsContriId = obj[5]!=null ? Long.parseLong(obj[5].toString()):0l;
					voucherNo = obj[6]!=null ? obj[6].toString():"";
					voucherMonth = obj[7]!=null ? Long.parseLong(obj[7].toString()):0;
					voucherYear = obj[8]!=null ? Long.parseLong(obj[8].toString()):0;
					dcpsEmpId = obj[9]!=null ? Long.parseLong(obj[9].toString()):0l;
					dcpsId = obj[10]!=null ? obj[10].toString():"";
					contriDDOCode = obj[11]!=null ? obj[11].toString():"";
					contriTresuryCode = obj[12]!=null ? obj[12].toString():"";
					currDDOCode = obj[13]!=null && !obj[13].equals("")? obj[13].toString():"";
					closeEmpPrv = obj[16]!=null && !obj[16].equals("")? Double.parseDouble(obj[16].toString()):0d;
					closeEmplrPrv = obj[17]!=null && !obj[17].equals("")? Double.parseDouble(obj[17].toString()):0d;
					closeTier2Prv = obj[19]!=null && !obj[19].equals("")? Double.parseDouble(obj[19].toString()):0d;
					lstDcpsContriId.add(dcpsContriId);
					currTreasuryCode = obj[14]!=null && !obj[14].equals("")? obj[14].toString():"0";
					//	mstContriYearlyId = obj[15]!=null ? Long.parseLong(obj[15].toString()):0l;

					itr = interestRatesForVoucherNo.iterator();

					termBreak:	while(itr.hasNext()){
						Object[] objRates = (Object[])itr.next();
						Double intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;
						String effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
						String applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";
						gLogger.info("Applicable to date is"+applicableTo);
						/*Integer tempDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(effectiveFrom));

						if(tempDaysBetween!=0){
							voucherDate = effectiveFrom;
						}

						if(applicableTo!=null && !applicableTo.equals("")){
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(applicableTo)); 

							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}

						else{
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(lStrToDatePassed)); 

							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}*/

						//Interest Cal For termination starts for daysbetween 


						Integer tempDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(effectiveFrom));
						terVouDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(termDate));
						if(tempDaysBetween!=0){
							voucherDate = effectiveFrom;
							gLogger.info("Inside tempDaysBetween");
						}

						if(applicableTo!=null && !applicableTo.equals("")){
							terDaysBetween = daysBetween(sdf2.parse(termDate), sdf2.parse(applicableTo));
							
							gLogger.info("Inside terDaysBetween");
						}
						

						if(applicableTo!=null && !applicableTo.equals("") && terDaysBetween!=0 && terVouDaysBetween!=0){
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(termDate)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							gLogger.info("Days Between regular"+daysBetween);
							gLogger.info("Inside terDaysBetween!=0");
							break termBreak;
						}

						else if (applicableTo!=null && !applicableTo.equals("") && terDaysBetween==0 && terVouDaysBetween!=0){
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(applicableTo)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							gLogger.info("Days Between regular"+daysBetween);
							gLogger.info("Inside terDaysBetween==0");
						}

						else if(applicableTo==null && applicableTo.equals("") && terVouDaysBetween!=0 ) {
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(termDate)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							gLogger.info("Days Between regular"+daysBetween);
							gLogger.info("Inside applicableTo==null ");
							break termBreak;
						}

						else {
							interestForMonthlyContri =0d;
							gLogger.info("Inside 0 ");
							break termBreak;
						}

						objRates = null;
						intRate = null;
						effectiveFrom = null;
						applicableTo = null;
						tempDaysBetween = null;
						terDaysBetween=null;
					}

					//Interest Cal For termination Ends for daysbetween 

					mstDcpsContributionMonthlyForInsertion = new MstDcpsContributionMonthly();
					mstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
					gLogger.info("dcpsContributionMonthlyId *******going to set is"+dcpsContributionMonthlyId);
					mstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
					mstDcpsContributionMonthlyForInsertion.setYearId(yearId);
					mstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(Long.parseLong(currTreasuryCode));
					mstDcpsContributionMonthlyForInsertion.setCurDdoCD(currDDOCode);
					mstDcpsContributionMonthlyForInsertion.setvMonth(voucherMonth);
					mstDcpsContributionMonthlyForInsertion.setvYear(voucherYear);
					mstDcpsContributionMonthlyForInsertion.setPayYear(finYearCode);
					mstDcpsContributionMonthlyForInsertion.setPayMonth(monthId);
					mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
					mstDcpsContributionMonthlyForInsertion.setVorcDate(sdf.parse(voucherDate));
					mstDcpsContributionMonthlyForInsertion.setContribEmp(Round(contribution,2));
					mstDcpsContributionMonthlyForInsertion.setContribEmplr(Round(contribution,2));
					mstDcpsContributionMonthlyForInsertion.setContribTier2(0d);
					mstDcpsContributionMonthlyForInsertion.setIntContribEmp(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribEmplr(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribTier2(0d);
					mstDcpsContributionMonthlyForInsertion.setCreatedPostId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedUserId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedDate(DBUtility.getCurrentDateFromDB());
					mstDcpsContributionMonthlyForInsertion.setUpdatedPostId(null);
					mstDcpsContributionMonthlyForInsertion.setUpdatedUserId(null);
					mstDcpsContributionMonthlyForInsertion.setUpdatedDate(null);
					mstDcpsContributionMonthlyForInsertion.setTypeOfPayment(typeOfPayment);
					mstDcpsContributionMonthlyForInsertion.setIsMissingCredit(null);
					mstDcpsContributionMonthlyForInsertion.setIsChallan(null);
					mstDcpsContributionMonthlyForInsertion.setCloseEmpPrv(closeEmpPrv);
					mstDcpsContributionMonthlyForInsertion.setCloseEmplrPrv(closeEmplrPrv);
					mstDcpsContributionMonthlyForInsertion.setCloseTier2Prv(closeTier2Prv);
					lstMstContriMonth.add(mstDcpsContributionMonthlyForInsertion);

					mstDcpsContributionMonthlyForInsertion = null;

					++dcpsContributionMonthlyId;
					++countMonth;

					dcpsIdForDeletion.add(dcpsId);
					gLogger.info("dcpsId for deletion is first "+dcpsId);
					interestForMonthlyContri = 0d;
					obj = null;


				}
			}

			it = null;
			itr = null;
			empDataForInterestCalcRegular = null;

			// Calculation of interest for Regular Month Contributions
			if(empDataForInterestCalcMissingCredit!=null){
				it = empDataForInterestCalcMissingCredit.iterator();

				while(it.hasNext()){
					Object[] obj = (Object[]) it.next();
					voucherDate = obj[0]!=null ? obj[0].toString():"";
					contribution = obj[1]!=null ? Double.parseDouble(obj[1].toString()):0d;
					typeOfPayment = obj[2]!=null ? obj[2].toString():"";
					monthId = obj[3]!=null ? Long.parseLong(obj[3].toString()):0;
					finYearCode = obj[4]!=null ? Long.parseLong(obj[4].toString()):0;
					dcpsContriId = obj[5]!=null ? Long.parseLong(obj[5].toString()):0l;
					voucherNo = obj[6]!=null ? obj[6].toString():"";
					voucherMonth = obj[7]!=null ? Long.parseLong(obj[7].toString()):0;
					voucherYear = obj[8]!=null ? Long.parseLong(obj[8].toString()):0;
					dcpsEmpId = obj[9]!=null ? Long.parseLong(obj[9].toString()):0l;
					dcpsId = obj[10]!=null ? obj[10].toString():"";
					contriDDOCode = obj[11]!=null ? obj[11].toString():"";
					contriTresuryCode = obj[12]!=null ? obj[12].toString():"";
					currDDOCode = obj[13]!=null && !obj[13].equals("")? obj[13].toString():"";
					currTreasuryCode = obj[14]!=null && !obj[14].equals("")? obj[14].toString():"0";
					//mstContriYearlyId = obj[15]!=null ? Long.parseLong(obj[15].toString()):0l;
					isMissingCredit = obj[15]!=null && obj[15].toString().equals("Y") ? 'Y':null;

					isChalan = obj[16]!=null && obj[16].toString().equals("Y") ? 'Y':null;
					closeEmpPrv = obj[18]!=null && !obj[18].equals("")? Double.parseDouble(obj[18].toString()):0d;
					closeEmplrPrv = obj[19]!=null && !obj[18].equals("")? Double.parseDouble(obj[19].toString()):0d;
					closeTier2Prv = obj[21]!=null && !obj[21].equals("")? Double.parseDouble(obj[21].toString()):0d;

					interestRatesForMissingCredit =  lObjTerminalRequestDAOImpl.getInterestRatesForMissingCredit(lStrToDatePassed,voucherDate,yearId);

					itr = interestRatesForMissingCredit.iterator();

					lstDcpsContriId.add(dcpsContriId);

					termTier2Break:	while(itr.hasNext()){
						Object[] objRates = (Object[])itr.next();
						Double intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;
						String effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
						String applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";
						Long finYearId = objRates[3]!=null ? Long.parseLong(objRates[3].toString()) : 0l;
						lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(finYearId);

						lStrFinYearStartDate = lObjSgvcFinYearMst.getFromDate().toString().trim();
						lStrFinYearEndDate = lObjSgvcFinYearMst.getToDate().toString().trim();
						grgcal = new GregorianCalendar();

						if (grgcal.isLeapYear(Integer.parseInt(lObjSgvcFinYearMst.getFinYearCode()) + 1)) {
							NumberOfDaysInGivenYear = 366;
						} else {
							NumberOfDaysInGivenYear = 365;
						}




						/*
						Integer tempDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(effectiveFrom));

						if(tempDaysBetween!=0){
							voucherDate = effectiveFrom;
						}

						if(applicableTo!=null && !applicableTo.equals("")){
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(applicableTo)); 

							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}

						else{
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(lStrToDatePassed)); 

							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}
						 */

						Integer tempDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(effectiveFrom));
						terVouDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(termDate));
						if(tempDaysBetween!=0){
							voucherDate = effectiveFrom;
							gLogger.info("Inside tempDaysBetween");
						}

						if(applicableTo!=null && !applicableTo.equals("")){
							terDaysBetween = daysBetween(sdf2.parse(termDate), sdf2.parse(applicableTo));
							gLogger.info("Inside terDaysBetween");
						}

						if(applicableTo!=null && !applicableTo.equals("") && terDaysBetween!=0 && terVouDaysBetween!=0 ){
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(termDate)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							gLogger.info("Inside terDaysBetween!=0");
							gLogger.info("Days Between  Missing credit"+daysBetween);
							break termTier2Break;
						}

						else if (applicableTo!=null && !applicableTo.equals("") && terDaysBetween==0 && terVouDaysBetween!=0){
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(applicableTo)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							gLogger.info("Days Between  Missing credit"+daysBetween);
							gLogger.info("Inside terDaysBetween==0");
						}

						else if(applicableTo==null && applicableTo.equals("") && terVouDaysBetween!=0 ) {
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(termDate)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							gLogger.info("Days Between  Missing credit"+daysBetween);
							gLogger.info("Inside applicableTo==null ");
							break termTier2Break;
						}

						else {
							interestForMonthlyContri =0d;
							gLogger.info("Inside 0 ");
							break termTier2Break;
						}


						tempDaysBetween = null;
						terDaysBetween=null;
						intRate = null;
						effectiveFrom = null;
						applicableTo = null;
						finYearId = null;
						lObjSgvcFinYearMst = null;
						lStrFinYearStartDate = null;
						lStrFinYearEndDate = null;
						grgcal = null;
						tempDaysBetween = null;
						objRates = null;

					}

					//Interest Cal For termination Ends for daysbetween 



					interestRatesForMissingCredit = null;
					itr = null;

					mstDcpsContributionMonthlyForInsertion = new MstDcpsContributionMonthly();
					mstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
					gLogger.info("dcpsContributionMonthlyId *******going to set is"+dcpsContributionMonthlyId);
					mstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
					mstDcpsContributionMonthlyForInsertion.setYearId(yearId);
					mstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(Long.parseLong(currTreasuryCode));
					mstDcpsContributionMonthlyForInsertion.setCurDdoCD(currDDOCode);
					mstDcpsContributionMonthlyForInsertion.setvMonth(voucherMonth);
					mstDcpsContributionMonthlyForInsertion.setvYear(voucherYear);
					mstDcpsContributionMonthlyForInsertion.setPayYear(finYearCode);

					mstDcpsContributionMonthlyForInsertion.setPayMonth(monthId);
					mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
					mstDcpsContributionMonthlyForInsertion.setVorcDate(sdf.parse(voucherDate));
					mstDcpsContributionMonthlyForInsertion.setContribEmp(Round(contribution,2));
					mstDcpsContributionMonthlyForInsertion.setContribEmplr(Round(contribution,2));
					mstDcpsContributionMonthlyForInsertion.setContribTier2(0d);
					mstDcpsContributionMonthlyForInsertion.setIntContribEmp(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribEmplr(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribTier2(0d);
					mstDcpsContributionMonthlyForInsertion.setCreatedPostId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedUserId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedDate(DBUtility.getCurrentDateFromDB());
					mstDcpsContributionMonthlyForInsertion.setUpdatedPostId(gLngPostId);
					mstDcpsContributionMonthlyForInsertion.setUpdatedUserId(gLngUserId);
					mstDcpsContributionMonthlyForInsertion.setUpdatedDate(gDtCurDate);
					mstDcpsContributionMonthlyForInsertion.setTypeOfPayment(typeOfPayment);
					mstDcpsContributionMonthlyForInsertion.setIsMissingCredit(isMissingCredit);
					mstDcpsContributionMonthlyForInsertion.setIsChallan(isChalan);
					mstDcpsContributionMonthlyForInsertion.setCloseEmpPrv(closeEmpPrv);
					mstDcpsContributionMonthlyForInsertion.setCloseEmplrPrv(closeEmplrPrv);
					mstDcpsContributionMonthlyForInsertion.setCloseTier2Prv(closeTier2Prv);
					lstMstContriMonth.add(mstDcpsContributionMonthlyForInsertion);

					mstDcpsContributionMonthlyForInsertion = null;
					obj = null;

					interestForMonthlyContri = 0d;

					++dcpsContributionMonthlyId;
					++countMonth;
					dcpsIdForDeletion.add(dcpsId);
					gLogger.info("dcpsId for deletion is second "+dcpsId);
				}
			}

			it = null;
			empDataForInterestCalcMissingCredit = null;


			// Gets all DCPS_EMP_IDs which are eligible for Six PC Arrrears
			if(totalDcpsEmpIds!=null && !totalDcpsEmpIds.equals("")){

				it = dcpsEmpIdForSixPc.iterator();
				interestRatesForVoucherNo =  lObjTerminalRequestDAOImpl.getInterestRatesForVoucherNo(yearId);

				while(it.hasNext()){
					Object[] objSixpc = (Object[])it.next();
					yearlyAmtSixpc = objSixpc[0]!=null ? Double.parseDouble(objSixpc[0].toString()):0d;
					dcpsEmpId = objSixpc[1]!=null ? Long.parseLong(objSixpc[1].toString()):0l;
					voucherNo = objSixpc[2]!=null ? objSixpc[2].toString():"";
					voucherDate = objSixpc[3]!=null ? objSixpc[3].toString():"";
					finYearCode = objSixpc[4]!=null ? Long.parseLong(objSixpc[4].toString()):0l;
					payCommision = objSixpc[5]!=null ?  objSixpc[5].toString():"";
					dcpsId = objSixpc[6]!=null ?  objSixpc[6].toString():"";

					//Interest Cal For termination starts for daysbetween for sixpc
					itr = interestRatesForVoucherNo.iterator();
					termSixpcBreak:	while(itr.hasNext()){
						Object[] objRates = (Object[])itr.next();
						Double intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;
						String effectiveFrom = objRates[1]!=null ? objRates[1].toString() : "";
						String applicableTo  = objRates[2]!=null ? objRates[2].toString() : "";
						/*
						Integer tempDaysBetween = daysBetween(sdf1.parse(lStrArrearsLimitDate), sdf2.parse(effectiveFrom));

						if(tempDaysBetween!=0){
							lStrArrearsLimitDate = effectiveFrom;
						}

						if(applicableTo!=null && !applicableTo.equals("")){
							Integer daysBetween = daysBetween(sdf1.parse(lStrArrearsLimitDate), sdf2.parse(applicableTo)); 

							interestForMonthlyContri = interestForMonthlyContri + ((yearlyAmtSixpc*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}

						else{
							Integer daysBetween = daysBetween(sdf1.parse(lStrArrearsLimitDate), sdf2.parse(lStrToDatePassed)); 

							interestForMonthlyContri = interestForMonthlyContri + ((yearlyAmtSixpc*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);

							daysBetween = null;
						}
						objRates = null;
						intRate = null;
						effectiveFrom = null;
						applicableTo = null;
						tempDaysBetween = null;
						 */




						Integer tempDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(effectiveFrom));
						terVouDaysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(termDate));
						if(tempDaysBetween!=0){
							voucherDate = effectiveFrom;
						}

						if(applicableTo!=null && !applicableTo.equals("") ){
							terDaysBetween = daysBetween(sdf2.parse(termDate), sdf2.parse(applicableTo));
						}

						if(applicableTo!=null && !applicableTo.equals("") && terDaysBetween!=0 && terVouDaysBetween!=0 ){
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(termDate)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							gLogger.info("Days Between six pc"+daysBetween);
							break termSixpcBreak;
						}

						else if (applicableTo!=null && !applicableTo.equals("") && terDaysBetween==0 && terVouDaysBetween!=0){
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(applicableTo)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							gLogger.info("Days Between six pc"+daysBetween);
						}

						else if(applicableTo==null && applicableTo.equals("") && terVouDaysBetween!=0 ) {
							Integer daysBetween = daysBetween(sdf2.parse(voucherDate), sdf2.parse(termDate)); 
							interestForMonthlyContri = interestForMonthlyContri + ((contribution*intRate*0.01*daysBetween)/NumberOfDaysInGivenYear);
							gLogger.info("Days Between six pc"+daysBetween);
							break termSixpcBreak;
						}

						else {
							interestForMonthlyContri =0d;
							break termSixpcBreak;
						}
						
						objRates = null;
						intRate = null;
						effectiveFrom = null;
						applicableTo = null;
						termDaysBetween = null;
						terDaysBetween=null;
					}

					//Interest Cal For termination starts for daysbetween for sixpc end
					gLogger.info("Year id in monthly table is"+yearId);
					mstDcpsContributionMonthlyForInsertion = new MstDcpsContributionMonthly();
					mstDcpsContributionMonthlyForInsertion.setDcpsContributionMonthlyId(dcpsContributionMonthlyId);
					gLogger.info("dcpsContributionMonthlyId *******going to set is"+dcpsContributionMonthlyId);
					mstDcpsContributionMonthlyForInsertion.setDcpsId(dcpsId);
					mstDcpsContributionMonthlyForInsertion.setYearId(yearId);
					mstDcpsContributionMonthlyForInsertion.setCurTreasuryCD(Long.parseLong(currTreasuryCode));
					mstDcpsContributionMonthlyForInsertion.setCurDdoCD(currDDOCode);
					mstDcpsContributionMonthlyForInsertion.setvMonth(voucherMonth);
					mstDcpsContributionMonthlyForInsertion.setvYear(voucherYear);
					if(payCommision.equals("700339"))
					{
						mstDcpsContributionMonthlyForInsertion.setPayMonth(5l);
						mstDcpsContributionMonthlyForInsertion.setPayYear(2010l);
					}
					else
					{
						mstDcpsContributionMonthlyForInsertion.setPayMonth(6l);
						mstDcpsContributionMonthlyForInsertion.setPayYear(2009l);
					}



					// Voucher no if not there then 0 entered as for some yearly installments in the beginning it was not captured.
					if(!"".equals(voucherNo))
					{
						mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
					}
					else
					{
						mstDcpsContributionMonthlyForInsertion.setVorcNo("NOT AVAILABLE");
					}
					Date lDateVoucherDateTier2 = null;
					if(voucherDate != null && !voucherDate.equals(""))
					{
						lDateVoucherDateTier2 = sdf.parse(voucherDate.toString().trim());

						Calendar lObjCalendarTier2 = Calendar.getInstance();
						lObjCalendarTier2.setTime(lDateVoucherDateTier2);

						Integer lIntVMonthTier2 = lObjCalendarTier2.get(Calendar.MONTH) + 1;
						monthId = Long.parseLong(lIntVMonthTier2.toString());

						Integer lIntVYearTier2 = lObjCalendarTier2.get(Calendar.YEAR) ;
						finYearCode = Long.parseLong(lIntVYearTier2.toString());

					}
					if("".equals(voucherDate))
					{
						voucherNo="9999";
						voucherDate = "9999-01-01";
						lDateVoucherDateTier2 = sdf2.parse(voucherDate);
						monthId = 0l;
						finYearCode = 0l;
					}

					mstDcpsContributionMonthlyForInsertion.setVorcDate(lDateVoucherDateTier2);

					mstDcpsContributionMonthlyForInsertion.setvYear(finYearCode);
					mstDcpsContributionMonthlyForInsertion.setvMonth(monthId);

					mstDcpsContributionMonthlyForInsertion.setVorcNo(voucherNo);
					mstDcpsContributionMonthlyForInsertion.setVorcDate(sdf2.parse(voucherDate));
					mstDcpsContributionMonthlyForInsertion.setContribEmp(0d);
					mstDcpsContributionMonthlyForInsertion.setContribEmplr(0d);
					mstDcpsContributionMonthlyForInsertion.setContribTier2(Round(yearlyAmtSixpc,2));
					mstDcpsContributionMonthlyForInsertion.setIntContribEmp(0d);
					mstDcpsContributionMonthlyForInsertion.setIntContribEmplr(0d);
					mstDcpsContributionMonthlyForInsertion.setIntContribTier2(Round(interestForMonthlyContri,2));
					mstDcpsContributionMonthlyForInsertion.setCreatedPostId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedUserId(1l);
					mstDcpsContributionMonthlyForInsertion.setCreatedDate(DBUtility.getCurrentDateFromDB());
					mstDcpsContributionMonthlyForInsertion.setUpdatedPostId(gLngPostId);
					mstDcpsContributionMonthlyForInsertion.setUpdatedUserId(gLngUserId);
					mstDcpsContributionMonthlyForInsertion.setUpdatedDate(gDtCurDate);
					mstDcpsContributionMonthlyForInsertion.setTypeOfPayment(null);
					mstDcpsContributionMonthlyForInsertion.setIsMissingCredit(null);
					mstDcpsContributionMonthlyForInsertion.setIsChallan(null);
					mstDcpsContributionMonthlyForInsertion.setCloseEmpPrv(0d);
					mstDcpsContributionMonthlyForInsertion.setCloseEmplrPrv(0d);
					mstDcpsContributionMonthlyForInsertion.setCloseTier2Prv(0d);
					lstMstContriMonth.add(mstDcpsContributionMonthlyForInsertion);

					mstDcpsContributionMonthlyForInsertion = null;
					objSixpc = null;

					interestForMonthlyContri = 0d;

					++dcpsContributionMonthlyId;
					++countMonth;
					dcpsIdForDeletion.add(dcpsId);
					gLogger.info("dcpsId for deletion is third "+dcpsId);
				}
			
			}
			it = null;
			itr = null;
			dcpsEmpIdForSixPc = null;
			totalDcpsEmpIds = null;

			//	Long dcpsContributionMonthlyIdForUpdate = dcpsContributionMonthlyId+countMonth + 1;

			//lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionMonthlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_MONTHLY");
			//Delete Records From Monthly Contribution Table
			if(dcpsIdForDeletion!=null && dcpsIdForDeletion.size()>0){

				String delDcpsIds=dcpsIdForDeletion.get(0).toString();
				lObjTerminalRequestDAOImpl.deleteMonthlyIntrstsForGivenEmpList(delDcpsIds, yearId);

			}


			Double openEmp = 0d;
			Double openEmplr = 0d;
			Double openBalanceTier2 = 0d;
			Double openNetBalance = 0d;
			Double openIntBalance = 0d;
			Double closeEmp = null;
			Double closeEmplr = null;
			Double openIntPrv = null;
			Double closeTier2 = null;

			Map<String,MstDcpsContributionYearly> yearlyDetails = new HashMap<String, MstDcpsContributionYearly>();

			for(MstDcpsContributionMonthly mstDcpsContributionMonthly : lstMstContriMonth){
				MstDcpsContributionYearly mstDcpsContributionYearlyOld = new MstDcpsContributionYearly();
				MstDcpsContributionYearly mstDcpsContributionYearlyOldHistory = new MstDcpsContributionYearly();

				if(yearlyDetails.containsKey(mstDcpsContributionMonthly.getDcpsId())){
					mstDcpsContributionYearlyOldHistory = yearlyDetails.get(mstDcpsContributionMonthly.getDcpsId());

					mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
					mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
					mstDcpsContributionYearlyOldHistory.setOpenEmp(mstDcpsContributionYearlyOldHistory.getOpenEmp());
					mstDcpsContributionYearlyOldHistory.setOpenEmplr(mstDcpsContributionYearlyOldHistory.getOpenEmplr());
					mstDcpsContributionYearlyOldHistory.setOpenNet(mstDcpsContributionYearlyOldHistory.getOpenNet());
					mstDcpsContributionYearlyOldHistory.setOpenTier2(mstDcpsContributionYearlyOldHistory.getCloseTier2());
					mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionYearlyOldHistory.getContribEmp()+mstDcpsContributionMonthly.getContribEmp());
					mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionYearlyOldHistory.getContribEmplr()+mstDcpsContributionMonthly.getContribEmplr());
					mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionYearlyOldHistory.getContribTier2()+mstDcpsContributionMonthly.getContribTier2());
					mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionYearlyOldHistory.getIntContribEmp()+mstDcpsContributionMonthly.getIntContribEmp());
					mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionYearlyOldHistory.getIntContribEmplr()+mstDcpsContributionMonthly.getIntContribEmplr());
					mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionYearlyOldHistory.getIntContribTier2()+mstDcpsContributionMonthly.getIntContribTier2());

					yearlyDetails.put(mstDcpsContributionMonthly.getDcpsId(), mstDcpsContributionYearlyOldHistory);
					mstDcpsContributionYearlyOldHistory = null;
				}
				else{
					//Long dcpsContributionYearlyId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contribution_yearly",inputMap);
					//mstDcpsContributionYearlyOld = lObjInterestCalculationDAO.getContriYearlyVOForYear(mstDcpsContributionMonthly.getDcpsId(),previousFinYearId);
					/*if(mstDcpsContributionYearlyOld==null){
						mstDcpsContributionYearlyOldHistory.setDcpsId(mstDcpsContributionMonthly.getDcpsId());
						mstDcpsContributionYearlyOldHistory.setYearId(yearId);
						mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
						mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
						mstDcpsContributionYearlyOldHistory.setOpenEmp(0d);
						mstDcpsContributionYearlyOldHistory.setOpenEmplr(0d);
						mstDcpsContributionYearlyOldHistory.setOpenNet(0d);
						mstDcpsContributionYearlyOldHistory.setOpenTier2(0d);
						mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionMonthly.getContribEmp());
						mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionMonthly.getContribEmplr());
						mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionMonthly.getContribTier2());
						mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionMonthly.getIntContribEmp());
						mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionMonthly.getIntContribEmplr());
						mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionMonthly.getIntContribTier2());
						mstDcpsContributionYearlyOldHistory.setCloseEmp(0d);
						mstDcpsContributionYearlyOldHistory.setCloseEmplr(0d);
						mstDcpsContributionYearlyOldHistory.setCloseNet(0d);
						mstDcpsContributionYearlyOldHistory.setCloseTier2(0d);

					}
					else{
						mstDcpsContributionYearlyOldHistory.setDcpsId(mstDcpsContributionMonthly.getDcpsId());
						mstDcpsContributionYearlyOldHistory.setYearId(yearId);
						mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
						mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
						mstDcpsContributionYearlyOldHistory.setOpenEmp(mstDcpsContributionYearlyOld.getCloseEmp());
						mstDcpsContributionYearlyOldHistory.setOpenEmplr(mstDcpsContributionYearlyOld.getCloseEmplr());
						mstDcpsContributionYearlyOldHistory.setOpenNet(mstDcpsContributionYearlyOld.getCloseNet());
						mstDcpsContributionYearlyOldHistory.setOpenTier2(mstDcpsContributionYearlyOld.getCloseTier2());
						mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionMonthly.getContribEmp());
						mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionMonthly.getContribEmplr());
						mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionMonthly.getContribTier2());
						mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionMonthly.getIntContribEmp());
						mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionMonthly.getIntContribEmplr());
						mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionMonthly.getIntContribTier2());
						mstDcpsContributionYearlyOldHistory.setCloseEmp(mstDcpsContributionYearlyOld.getCloseEmp());
						mstDcpsContributionYearlyOldHistory.setCloseEmplr(mstDcpsContributionYearlyOld.getCloseEmplr());
						mstDcpsContributionYearlyOldHistory.setCloseNet(mstDcpsContributionYearlyOld.getCloseNet());
						mstDcpsContributionYearlyOldHistory.setCloseTier2(mstDcpsContributionYearlyOld.getCloseTier2());
					}*/

					mstDcpsContributionYearlyOldHistory.setDcpsId(mstDcpsContributionMonthly.getDcpsId());
					mstDcpsContributionYearlyOldHistory.setYearId(yearId);
					mstDcpsContributionYearlyOldHistory.setCurTreasuryCD(mstDcpsContributionMonthly.getCurTreasuryCD());
					mstDcpsContributionYearlyOldHistory.setCurDdoCD(mstDcpsContributionMonthly.getCurDdoCD());
					mstDcpsContributionYearlyOldHistory.setOpenEmp(mstDcpsContributionMonthly.getCloseEmpPrv());
					mstDcpsContributionYearlyOldHistory.setOpenEmplr(mstDcpsContributionMonthly.getCloseEmpPrv());
					mstDcpsContributionYearlyOldHistory.setOpenNet(mstDcpsContributionMonthly.getCloseEmpPrv() + mstDcpsContributionMonthly.getCloseEmpPrv());
					mstDcpsContributionYearlyOldHistory.setOpenTier2(mstDcpsContributionMonthly.getCloseTier2Prv());
					mstDcpsContributionYearlyOldHistory.setContribEmp(mstDcpsContributionMonthly.getContribEmp());
					mstDcpsContributionYearlyOldHistory.setContribEmplr(mstDcpsContributionMonthly.getContribEmplr());
					mstDcpsContributionYearlyOldHistory.setContribTier2(mstDcpsContributionMonthly.getContribTier2());
					mstDcpsContributionYearlyOldHistory.setIntContribEmp(mstDcpsContributionMonthly.getIntContribEmp());
					mstDcpsContributionYearlyOldHistory.setIntContribEmplr(mstDcpsContributionMonthly.getIntContribEmplr());
					mstDcpsContributionYearlyOldHistory.setIntContribTier2(mstDcpsContributionMonthly.getIntContribTier2());
					mstDcpsContributionYearlyOldHistory.setCloseEmp(0d);
					mstDcpsContributionYearlyOldHistory.setCloseEmplr(0d);
					mstDcpsContributionYearlyOldHistory.setCloseNet(0d);
					mstDcpsContributionYearlyOldHistory.setCloseTier2(0d);
					yearlyDetails.put(mstDcpsContributionMonthly.getDcpsId(), mstDcpsContributionYearlyOldHistory);

					mstDcpsContributionYearlyOldHistory = null;
					mstDcpsContributionYearlyOld = null;
				}
				if(mstDcpsContributionMonthly.getVorcNo()==null){
					mstDcpsContributionMonthly.setVorcNo("9999");
				}
				hitStg.save(mstDcpsContributionMonthly);
			}
			gLogger.info("yearlyDetails null or not"+yearlyDetails);
			lstMstContriMonth = null;

			InterestCalculationDAO interestCalculationDAO = new InterestCalculationDAOImpl(MstDcpsContributionYearly.class, serv.getSessionFactory());
			//Delete Records From Yearly Contribution Table


			if(dcpsIdForDeletion!=null && dcpsIdForDeletion.size()>0){

				String delDcpsIds=dcpsIdForDeletion.get(0).toString();
				lObjTerminalRequestDAOImpl.deleteYearlyIntrstsForGivenEmpList(delDcpsIds, yearId);


			}

			dcpsIdForDeletion = null;

			//Inserting Yearly Contributions In MstDcpsContributionYearly

			IdGenerator idGen = new IdGenerator();
			//Long dcpsContributionYearlyId = idGen.PKGenerator("mst_dcps_contribution_yearly", inputMap);
			Long dcpsContributionYearlyId = lObjInterestCalculationDAO.getNextSeqNum("MST_DCPS_CONTRIBUTION_YEARLY");
			Long dcpsContributionYearlyIdForUpdate = 0l;
			gLogger.info("yearlyDetails null or not"+yearlyDetails.size());
			if(yearlyDetails!=null && yearlyDetails.size()>0){
				gLogger.info("inside the loop************************!@#*!&*@#^&*!^@&!^@#!");
				dcpsContributionYearlyIdForUpdate = dcpsContributionYearlyId + yearlyDetails.size() + 1;
			}


			if(dcpsContributionYearlyIdForUpdate!=0){
				lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionYearlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_YEARLY");
			}

			Iterator itYearltContri = yearlyDetails.keySet().iterator();
			List<MstDcpsContributionYearly> lstMstContriYear = new ArrayList<MstDcpsContributionYearly>();
			interestRatesForVoucherNo =  lObjInterestCalculationDAO.getInterestRatesForVoucherNo(yearId);
			while(itYearltContri.hasNext()){
				MstDcpsContributionYearly mstDcpsContributionYearly = new MstDcpsContributionYearly();
				String dcpsIdEmp = (String)itYearltContri.next();
				gLogger.info("dcpsIdEmp  *******going to set is "+dcpsIdEmp);
				MstDcpsContributionYearly mstDcpsContributionYearlyOld= new MstDcpsContributionYearly();

				gLogger.info("yearlyDetails null or not"+yearlyDetails.get(dcpsIdEmp).getDcpsId());
				mstDcpsContributionYearlyOld=  (MstDcpsContributionYearly)(yearlyDetails!=null ? yearlyDetails.get(dcpsIdEmp):null);

				dcpsIdEmp = null;

				itr = interestRatesForVoucherNo.iterator();
				while(itr.hasNext()){
					Object[] objRates = (Object[])itr.next();
					Double intRate = objRates[0]!=null ? Double.parseDouble(objRates[0].toString()) : 0d;


					openEmployeeInt= openEmployeeInt+(intRate * mstDcpsContributionYearlyOld.getOpenEmp()  / 100);
					openEmployerInt= openEmployerInt+(intRate * mstDcpsContributionYearlyOld.getOpenEmplr() / 100);

					intRate = null;
					objRates = null;
				}

				openIntBalance=openEmployeeInt+openEmployerInt;
				closeEmp=mstDcpsContributionYearlyOld.getOpenEmp() + mstDcpsContributionYearlyOld.getContribEmp() + mstDcpsContributionYearlyOld.getIntContribEmp() + openEmployeeInt + mstDcpsContributionYearlyOld.getContribTier2() + mstDcpsContributionYearlyOld.getIntContribTier2() ;
				closeEmplr=mstDcpsContributionYearlyOld.getOpenEmplr() + mstDcpsContributionYearlyOld.getContribEmplr() + mstDcpsContributionYearlyOld.getIntContribEmplr() + openEmployerInt ;
				closeTier2=mstDcpsContributionYearlyOld.getContribTier2() + mstDcpsContributionYearlyOld.getIntContribTier2() ;

				mstDcpsContributionYearly.setDcpsContributionYearlyId(dcpsContributionYearlyId);
				gLogger.info("dcpsContributionYearlyId *******going to set is"+dcpsContributionYearlyId);
				mstDcpsContributionYearly.setDcpsId(mstDcpsContributionYearlyOld.getDcpsId());
				mstDcpsContributionYearly.setYearId(mstDcpsContributionYearlyOld.getYearId());
				gLogger.info("mstDcpsContributionYearlyOld sdcps id *******going to set is"+mstDcpsContributionYearlyOld.getDcpsId());
				gLogger.info("mstDcpsContributionYearlyOld.getYearId() *******going to set is"+mstDcpsContributionYearlyOld.getYearId());
				mstDcpsContributionYearly.setCurTreasuryCD(mstDcpsContributionYearlyOld.getCurTreasuryCD());
				mstDcpsContributionYearly.setCurDdoCD(mstDcpsContributionYearlyOld.getCurDdoCD());
				mstDcpsContributionYearly.setOpenNet(mstDcpsContributionYearlyOld.getOpenNet());
				mstDcpsContributionYearly.setOpenEmp(mstDcpsContributionYearlyOld.getOpenEmp());
				mstDcpsContributionYearly.setOpenEmplr(mstDcpsContributionYearlyOld.getOpenEmplr());
				mstDcpsContributionYearly.setOpenTier2(mstDcpsContributionYearlyOld.getOpenTier2());
				mstDcpsContributionYearly.setContribEmp(mstDcpsContributionYearlyOld.getContribEmp());
				mstDcpsContributionYearly.setContribEmplr(mstDcpsContributionYearlyOld.getContribEmplr());
				mstDcpsContributionYearly.setContribTier2(mstDcpsContributionYearlyOld.getContribTier2());
				mstDcpsContributionYearly.setIntContribEmp(mstDcpsContributionYearlyOld.getIntContribEmp());
				mstDcpsContributionYearly.setIntContribEmplr(mstDcpsContributionYearlyOld.getIntContribEmplr());
				mstDcpsContributionYearly.setIntContribTier2(mstDcpsContributionYearlyOld.getIntContribTier2());
				mstDcpsContributionYearly.setOpenInt(Round(openIntBalance,2));
				mstDcpsContributionYearly.setCloseEmp(Round(closeEmp,2));
				mstDcpsContributionYearly.setCloseEmplr(Round(closeEmplr,2));
				mstDcpsContributionYearly.setCloseTier2(Round(closeTier2,2));
				mstDcpsContributionYearly.setCloseNet(Round((closeEmp+closeEmplr),2));
				mstDcpsContributionYearly.setCreatedPostId(1l);
				mstDcpsContributionYearly.setCreatedUserId(1l);
				mstDcpsContributionYearly.setCreatedDate(DBUtility.getCurrentDateFromDB());
				mstDcpsContributionYearly.setUpdatedPostId(null);
				mstDcpsContributionYearly.setUpdatedUserId(null);
				mstDcpsContributionYearly.setUpdatedDate(null);
				lstMstContriYear.add(mstDcpsContributionYearly);

				mstDcpsContributionYearly = null;
				mstDcpsContributionYearlyOld = null;
				++dcpsContributionYearlyId;
				openEmployeeInt = 0d;
				openEmployerInt = 0d;
				openIntBalance = 0d;
				//interestCalculationDAO.create(mstDcpsContributionYearly);
			}
			idGen = null;
			itYearltContri = null;
			itr = null;


			/*Long dcpsContributionYearlyIdForUpdate = 0l;
			if(yearlyDetails!=null && yearlyDetails.size()>0){
				dcpsContributionYearlyIdForUpdate = dcpsContributionYearlyId + countYear + 1;
			}


			if(dcpsContributionYearlyIdForUpdate!=0){
				lObjInterestCalculationDAO.updateGeneratedId(Long.valueOf(dcpsContributionYearlyIdForUpdate.toString()),"MST_DCPS_CONTRIBUTION_YEARLY");
			}
			 */

			for(MstDcpsContributionYearly objMstDcpsContributionYearly : lstMstContriYear){

				hitStg.save(objMstDcpsContributionYearly);
			}

			List tempDcpsContriId = new ArrayList();

			if(lstDcpsContriId!=null && lstDcpsContriId.size()>0){
				for(int i=0 ; i<lstDcpsContriId.size() ; i++){
					if(i==0 || i%5000!=0){
						tempDcpsContriId.add(lstDcpsContriId.get(i));
					}
					else{
						tempDcpsContriId.add(lstDcpsContriId.get(i));
						lObjInterestCalculationDAO.updateTrnDcpsContriIntCalculated(tempDcpsContriId);
						tempDcpsContriId = null;
						tempDcpsContriId = new ArrayList();
					}
				}
				lObjTerminalRequestDAOImpl.updateTrnDcpsContriIntCalculated(tempDcpsContriId);
			}

			tempDcpsContriId = null;
			lstMstContriYear = null;
			lObjInterestCalculationDAO = null;
			lstDcpsContriId = null;

			lBlFlag = true;

			String lSBStatus = getResponseXMLDocForInt(lBlFlag,empName).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in Interest Calculation " + e, e);
			return resObj;
		}

		return resObj;

	}



	public static Integer daysBetween(Date sPassedDate, Date ePassedDate) {

		Calendar sDate = Calendar.getInstance();
		sDate.setTime(sPassedDate);
		Calendar eDate = Calendar.getInstance();
		eDate.setTime(ePassedDate);

		Calendar d = (Calendar) sDate.clone();
		Integer dBetween = 0;
		while (d.before(eDate)) {
			d.add(Calendar.DAY_OF_MONTH, 1);
			dBetween++;
		}
		if(dBetween==0 && d.equals(eDate)){
			dBetween = 1;
		}

		else if(dBetween==0 && d.after(eDate)){
			dBetween = 0;
		}
		return dBetween;
	}

	//days between with termination date
	/*public static Integer daysBetweenWdTermination(Date sPassedDate, Date ePassedDate,Date terminationDate) {

		Integer dBetween = 0,dBetween1=0,x=0;

		dBetween=daysBetween(sPassedDate,ePassedDate);
		dBetween1=daysBetween(terminationDate,ePassedDate);
		if(dBetween==0 && dBetween1==1)
		x=0;
		else x=1;

		return x;
	}*/
	private StringBuilder getResponseXMLDocForInt(Boolean flag,String Empname) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<InterestFor>");
		lStrBldXML.append(Empname);
		lStrBldXML.append("</InterestFor>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public static double Round(double Rval, int Rpl) {
		double p = (double)Math.pow(10,Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return (double)tmp/p;
	}

	public ResultObject saveCurrentR3Dtls(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List treasuryCodes = null;
		Long treasuryCode = null;
		String ddoCode = null;
		String EmpId = null;
		List empDetails = null;
		Double openEmpContr1 = 0d;
		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());


			String terminationDate = StringUtility.getParameter("termDate", request);
			gLogger.info("term Date is  before parse" + terminationDate);
			String dcpsId = StringUtility.getParameter("dcpsId", request);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date date1 = sdf.parse(terminationDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			String[] arrDt = terminationDate.split("/");
			int day=Integer.parseInt(arrDt[0].toString());
			int month=Integer.parseInt(arrDt[1].toString());
			int year=Integer.parseInt(arrDt[2].toString());
			gLogger.info("year is " +year);
			/*int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int year = cal.get(Calendar.YEAR);*/

			long dcpsEmpId=lObjTerminalRequestDAO.getDcpsEmpIdForDcpsId(dcpsId);

			long yearId = lObjTerminalRequestDAO.getfinYearIdFromYr(year);

			if(month<4)
			{
				yearId=yearId-1;
			}

			Object [] lObj1=null;
			List Date=null;
			String fromDate=null;
			String toDate=null;
			Date=lObjTerminalRequestDAO.getFromToDate(yearId);
			lObj1 = (Object[]) Date.get(0);
			if(lObj1[0]!=null){
				fromDate=lObj1[0].toString();

			}

			if(lObj1[1]!=null){
				toDate=lObj1[1].toString();
			}

			gLogger.info("yearId is " +yearId);
			openEmpContr1=lObjTerminalRequestDAO.getOpeningBalanceTier1(dcpsId,yearId);
			gLogger.info("openEmp is " +openEmpContr1);
			Double empContri=(lObjTerminalRequestDAO.getEmployeeContributionNonMissingTier1(dcpsEmpId+"",yearId))+(lObjTerminalRequestDAO.getEmployeeContributionWithMissingTier1(dcpsEmpId+"",fromDate,toDate));
			gLogger.info("empContri is " +empContri);
			Double getIntrst=lObjTerminalRequestDAO.getInterestTier1(dcpsId,yearId);

			gLogger.info("getIntrst is " +getIntrst);
			List getTier2Contri=lObjTerminalRequestDAO.getContributionAndIntTier2(dcpsId,yearId);
			Object [] lObj2=null;
			boolean checkStatus=false;
			Double tier2Contri=null;
			Double tier2Int=null;
			Double empIntrstT2=null;
			Double emplrIntrstT2=null;
			Double lInterestTier1=null;
			Double totalInt=null;
			Double lOpeningEmpTier1=null;
			Double totalAmtCredit=null;
			Long termId=null;
			Object [] lObjterdtls=null;
			if(getTier2Contri!=null && getTier2Contri.size()>0){
				lObj2 = (Object[]) getTier2Contri.get(0);
				tier2Contri=Double.parseDouble(lObj2[1].toString());
				tier2Int=Double.parseDouble(lObj2[3].toString());
				empIntrstT2=Double.parseDouble(lObj2[4].toString());
				emplrIntrstT2=Double.parseDouble(lObj2[5].toString());
				lInterestTier1=Double.parseDouble(lObj2[8].toString());
				totalInt=Double.valueOf(tier2Int.doubleValue() + empIntrstT2.doubleValue() );
				lOpeningEmpTier1=Double.parseDouble(lObj2[6].toString());


				gLogger.info("tier2Contri is " +tier2Contri);
				gLogger.info("tier2Int is " +tier2Int);
				gLogger.info("empIntrstT2 is " +empIntrstT2);
				gLogger.info("emplrIntrstT2 is " +emplrIntrstT2);
				gLogger.info("lInterestTier1 is " +lInterestTier1);
				gLogger.info("totalInt is " +totalInt);
				gLogger.info("lOpeningEmpTier1 is " +lOpeningEmpTier1);

				Double totalContr=Double.valueOf(empContri.doubleValue()+ tier2Contri.doubleValue());
				gLogger.info("totalContr is " +totalContr);

				Double totalEmpAmt=Double.valueOf( lOpeningEmpTier1.doubleValue() + totalInt.doubleValue() + totalContr.doubleValue() );
				gLogger.info("totalEmpAmt is " +totalEmpAmt);

				Double totalEmplyrAmt=Double.valueOf(totalEmpAmt.doubleValue() + lOpeningEmpTier1.doubleValue() + emplrIntrstT2.doubleValue() ); 
				gLogger.info("totalEmplyrAmt is " +totalEmplyrAmt);

				totalAmtCredit=Double.valueOf(totalEmpAmt.doubleValue() + totalEmplyrAmt.doubleValue() + lInterestTier1.doubleValue() ) ;
				gLogger.info("totalAmtCredit is " +totalAmtCredit);



				TrnDcpsTerminalDtls lObjTrnDcpsTerminalDtls = null;
				TerminalRequestDAO lObjTerminalRequestDAOImpl = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, serv.getSessionFactory());

				List empListInterm=lObjTerminalRequestDAO.checkEmployeeInPartialTermination(dcpsId);
				if(empListInterm!=null && empListInterm.size()>0){
					lObjterdtls = (Object[]) empListInterm.get(0);
					termId=Long.parseLong(lObjterdtls[0].toString());

					lObjTrnDcpsTerminalDtls =(TrnDcpsTerminalDtls) lObjTerminalRequestDAOImpl.read(Long.valueOf(termId));
					lObjTrnDcpsTerminalDtls.setDcpsEmpId(dcpsEmpId);
					lObjTrnDcpsTerminalDtls.setDateOfTermination(date1);
					lObjTrnDcpsTerminalDtls.setLangId(gLngLangId);
					lObjTrnDcpsTerminalDtls.setDbId(99l);
					lObjTrnDcpsTerminalDtls.setLocId(Long.parseLong(gStrLocationCode));
					lObjTrnDcpsTerminalDtls.setUpdatedPostId(gLngPostId);
					lObjTrnDcpsTerminalDtls.setUpdatedUserId(gLngUserId);
					lObjTrnDcpsTerminalDtls.setUpdatedDate(gDtCurDate);
					lObjTrnDcpsTerminalDtls.setCurropeningBalance(openEmpContr1);
					lObjTrnDcpsTerminalDtls.setCurrempContriCurrYear(empContri);
					lObjTrnDcpsTerminalDtls.setCurremplrContriCurrYear(empContri);
					lObjTrnDcpsTerminalDtls.setCurrinterestContri(getIntrst);
					lObjTrnDcpsTerminalDtls.setCurrtier2Contri(tier2Contri);
					lObjTrnDcpsTerminalDtls.setCurrtier2Interest(tier2Int);
					lObjTrnDcpsTerminalDtls.setCurrpayableAmount(Math.round(totalAmtCredit));
					gLogger.info("generation flag before set is"+lObjTrnDcpsTerminalDtls.getPrvr3gen());
					lObjTrnDcpsTerminalDtls.setPrvr3gen('Y');
					gLogger.info("generation flag after set is"+lObjTrnDcpsTerminalDtls.getPrvr3gen());
					lObjTerminalRequestDAOImpl.update(lObjTrnDcpsTerminalDtls);

					checkStatus=true;
					String lSBStatus = getResponseXMLDocForcurrentR3(checkStatus,yearId).toString();
					gLogger.info("final status is "+lSBStatus);
					String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
					inputMap.put("ajaxKey", lStrResult);
					resObj.setViewName("ajaxData");
					resObj.setResultValue(inputMap);

				}

			}


		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}


	private StringBuilder getResponseXMLDocForcurrentR3(boolean status,Long yearId) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();

		gLogger.info("status is AJAX************" + status);



		lStrBldXML.append("<XMLDOC>"); 

		lStrBldXML.append("<MESSAGE>");

		lStrBldXML.append(status);

		lStrBldXML.append("</MESSAGE>");

		lStrBldXML.append("<YEARID>");

		lStrBldXML.append(yearId);

		lStrBldXML.append("</YEARID>");

		lStrBldXML.append("</XMLDOC>");

		gLogger.info("lStrBldXML************" + lStrBldXML);
		return lStrBldXML;
	}


	public ResultObject saveFinalDtlsAfterIntCalculation(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List treasuryCodes = null;
		Long treasuryCode = null;
		String ddoCode = null;
		String EmpId = null;
		List empDetails = null;
		Double openEmpContr1 = 0d;
		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());


			Long yearId = Long.parseLong(StringUtility.getParameter("yearId", request));
			String dcpsId = StringUtility.getParameter("dcpsId", request);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			long dcpsEmpId=lObjTerminalRequestDAO.getDcpsEmpIdForDcpsId(dcpsId);

			Object [] lObj1=null;
			List Date=null;
			String fromDate=null;
			String toDate=null;
			Date=lObjTerminalRequestDAO.getFromToDate(yearId);
			lObj1 = (Object[]) Date.get(0);
			if(lObj1[0]!=null){
				fromDate=lObj1[0].toString();

			}

			if(lObj1[1]!=null){
				toDate=lObj1[1].toString();
			}

			gLogger.info("yearId is " +yearId);
			openEmpContr1=lObjTerminalRequestDAO.getOpeningBalanceTier1(dcpsId,yearId);
			gLogger.info("openEmp is " +openEmpContr1);
			Double empContri=(lObjTerminalRequestDAO.getEmployeeContributionNonMissingTier1(dcpsEmpId+"",yearId))+(lObjTerminalRequestDAO.getEmployeeContributionWithMissingTier1(dcpsEmpId+"",fromDate,toDate));
			gLogger.info("empContri is " +empContri);
			Double getIntrst=lObjTerminalRequestDAO.getInterestTier1(dcpsId,yearId);

			gLogger.info("getIntrst is " +getIntrst);
			List getTier2Contri=lObjTerminalRequestDAO.getContributionAndIntTier2(dcpsId,yearId);
			Object [] lObj2=null;
			boolean checkStatus=false;
			Double tier2Contri=null;
			Double tier2Int=null;
			Double empIntrstT2=null;
			Double emplrIntrstT2=null;
			Double lInterestTier1=null;
			Double totalInt=null;
			Double lOpeningEmpTier1=null;
			Double totalAmtCredit=null;
			Long termId=null;
			List prvR3Dtls=null;
			Object [] lObjterdtls=null;
			Object [] lObjPrvR3=null;
			Double openprv=null;
			Double prvEmpContr=null;
			Double prvEmplrContr=null;
			Double prvInt=null;
			Double prvTier2Contri=null;
			Double prvTier2Int=null;
			Long prvtotalAmtCredit=null;


			if(getTier2Contri!=null && getTier2Contri.size()>0){
				lObj2 = (Object[]) getTier2Contri.get(0);
				tier2Contri=Double.parseDouble(lObj2[1].toString());
				tier2Int=Double.parseDouble(lObj2[3].toString());
				empIntrstT2=Double.parseDouble(lObj2[4].toString());
				emplrIntrstT2=Double.parseDouble(lObj2[5].toString());
				lInterestTier1=Double.parseDouble(lObj2[8].toString());
				totalInt=Double.valueOf(tier2Int.doubleValue() + empIntrstT2.doubleValue() );
				lOpeningEmpTier1=Double.parseDouble(lObj2[6].toString());


				gLogger.info("tier2Contri is " +tier2Contri);
				gLogger.info("tier2Int is " +tier2Int);
				gLogger.info("empIntrstT2 is " +empIntrstT2);
				gLogger.info("emplrIntrstT2 is " +emplrIntrstT2);
				gLogger.info("lInterestTier1 is " +lInterestTier1);
				gLogger.info("totalInt is " +totalInt);
				gLogger.info("lOpeningEmpTier1 is " +lOpeningEmpTier1);

				Double totalContr=Double.valueOf(empContri.doubleValue()+ tier2Contri.doubleValue());
				gLogger.info("totalContr is " +totalContr);

				Double totalEmpAmt=Double.valueOf( lOpeningEmpTier1.doubleValue() + totalInt.doubleValue() + totalContr.doubleValue() );
				gLogger.info("totalEmpAmt is " +totalEmpAmt);

				Double totalEmplyrAmt=Double.valueOf(totalEmpAmt.doubleValue() + lOpeningEmpTier1.doubleValue() + emplrIntrstT2.doubleValue() ); 
				gLogger.info("totalEmplyrAmt is " +totalEmplyrAmt);

				totalAmtCredit=Double.valueOf(totalEmpAmt.doubleValue() + totalEmplyrAmt.doubleValue() + lInterestTier1.doubleValue() ) ;
				gLogger.info("totalAmtCredit is " +totalAmtCredit);



				TrnDcpsTerminalDtls lObjTrnDcpsTerminalDtls = null;
				TerminalRequestDAO lObjTerminalRequestDAOImpl = new TerminalRequestDAOImpl(TrnDcpsTerminalDtls.class, serv.getSessionFactory());

				List empListInterm=lObjTerminalRequestDAO.checkEmployeeInPartialTermination(dcpsId);
				if(empListInterm!=null && empListInterm.size()>0){
					lObjterdtls = (Object[]) empListInterm.get(0);
					termId=Long.parseLong(lObjterdtls[0].toString());
                   gLogger.info("Terminal ID***************"+termId);
					lObjTrnDcpsTerminalDtls =(TrnDcpsTerminalDtls) lObjTerminalRequestDAOImpl.read(Long.valueOf(termId));
					   gLogger.info("openEmpContr1 ***************"+openEmpContr1);
					lObjTrnDcpsTerminalDtls.setOpeningBalance(openEmpContr1);
					 gLogger.info("empContri ***************"+empContri);
					lObjTrnDcpsTerminalDtls.setEmpContriCurrYear(empContri);
					 gLogger.info("openEmpContr1 ***************"+openEmpContr1);
					lObjTrnDcpsTerminalDtls.setEmplrContriCurrYear(empContri);
					 gLogger.info("getIntrst ***************"+getIntrst);
					lObjTrnDcpsTerminalDtls.setInterestContri(getIntrst);
					 gLogger.info("tier2Contri ***************"+tier2Contri);
					lObjTrnDcpsTerminalDtls.setTier2Contri(tier2Contri);
					 gLogger.info("tier2Int ***************"+tier2Int);
					lObjTrnDcpsTerminalDtls.setTier2Interest(tier2Int);
					 gLogger.info("totalAmtCredit ***************"+totalAmtCredit);
					lObjTrnDcpsTerminalDtls.setPayableAmount(Math.round(totalAmtCredit));
			
					lObjTrnDcpsTerminalDtls.setUpdatedDate(gDtCurDate);
					lObjTerminalRequestDAOImpl.update(lObjTrnDcpsTerminalDtls);

					prvR3Dtls=lObjTerminalRequestDAO.getR3DtlsBeforeIntCal(dcpsEmpId);
					lObjPrvR3 = (Object[]) prvR3Dtls.get(0);

					if(lObjPrvR3[0]!=null){
						openprv=Double.parseDouble(lObjPrvR3[0].toString());
					}
					if(lObjPrvR3[1]!=null){
						prvEmpContr=Double.parseDouble(lObjPrvR3[1].toString());
					}
					if(lObjPrvR3[2]!=null){
						prvEmplrContr=Double.parseDouble(lObjPrvR3[2].toString());
					}
					if(lObjPrvR3[3]!=null){
						prvInt=Double.parseDouble(lObjPrvR3[3].toString());
					}
					if(lObjPrvR3[4]!=null){
						prvTier2Contri=Double.parseDouble(lObjPrvR3[4].toString());
					}
					if(lObjPrvR3[5]!=null){
						prvTier2Int=Double.parseDouble(lObjPrvR3[5].toString());
					}
					if(lObjPrvR3[6]!=null){
						prvtotalAmtCredit=Long.parseLong(lObjPrvR3[6].toString());
					}

					checkStatus=true;
					String lSBStatus = saveFinalDtlsAfterIntCalculation(openprv,prvEmpContr,prvEmplrContr,Round(prvInt,2),prvTier2Contri,prvTier2Int,prvtotalAmtCredit,openEmpContr1,empContri,empContri,Round(getIntrst,2),tier2Contri,tier2Int,Math.round(totalAmtCredit)).toString();
					String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
					inputMap.put("ajaxKey", lStrResult);
					resObj.setViewName("ajaxData");
					resObj.setResultValue(inputMap);

				}

			}


		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}


	private StringBuilder saveFinalDtlsAfterIntCalculation(Double PrvOpenEmp, Double PrvEmpContri, Double PrvEmpContri2, Double PrvGetIntrst, Double PrvTier2Contri, Double PrvTier2Int, Long PrvTotalAmtCredit,Double openEmp, Double empContri, Double empContri2, Double getIntrst, Double tier2Contri, Double tier2Int, Long totalAmtCredit) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>"); 
		
		lStrBldXML.append("<PrvOpenEmp>");
        lStrBldXML.append(PrvOpenEmp);
		lStrBldXML.append("</PrvOpenEmp>");
		
		lStrBldXML.append("<PrvEmpContri>");
		lStrBldXML.append(PrvEmpContri);
		lStrBldXML.append("</PrvEmpContri>");
		
		lStrBldXML.append("<PrvEmpContri2>");
		lStrBldXML.append(PrvEmpContri2);
		lStrBldXML.append("</PrvEmpContri2>");

		lStrBldXML.append("<PrvGetIntrst>");
		lStrBldXML.append(PrvGetIntrst);
		lStrBldXML.append("</PrvGetIntrst>");
		
		lStrBldXML.append("<PrvTier2Contri>");
		lStrBldXML.append(PrvTier2Contri);
		lStrBldXML.append("</PrvTier2Contri>");
		
		lStrBldXML.append("<PrvTier2Int>");
		lStrBldXML.append(PrvTier2Int);
		lStrBldXML.append("</PrvTier2Int>");
		
		
		lStrBldXML.append("<PrvTotalAmtCredit>");
		lStrBldXML.append(PrvTotalAmtCredit);
		lStrBldXML.append("</PrvTotalAmtCredit>");


		lStrBldXML.append("<openEmp>");
		lStrBldXML.append(openEmp);
		lStrBldXML.append("</openEmp>");
		
		lStrBldXML.append("<empContri>");
		lStrBldXML.append(empContri);
		lStrBldXML.append("</empContri>");
		
		lStrBldXML.append("<empContri2>");
		lStrBldXML.append(empContri2);
		lStrBldXML.append("</empContri2>");
		
		lStrBldXML.append("<getIntrst>");
		lStrBldXML.append(getIntrst);
		lStrBldXML.append("</getIntrst>");
		
		lStrBldXML.append("<tier2Contri>");
		lStrBldXML.append(tier2Contri);
		lStrBldXML.append("</tier2Contri>");
		
		lStrBldXML.append("<tier2Int>");
		lStrBldXML.append(tier2Int);
		lStrBldXML.append("</tier2Int>");
		
		lStrBldXML.append("<totalAmtCredit>");
		lStrBldXML.append(totalAmtCredit);
		lStrBldXML.append("</totalAmtCredit>");

		lStrBldXML.append("</XMLDOC>");

		gLogger.info("lStrBldXML************" + lStrBldXML);
		return lStrBldXML;
	}

	public ResultObject finalterminateEmpdtls(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Long yearId = 0l;
		String terminationDate = null;
		String empId=null;
		String selectedDDocode = null;
		int checkStatus=0;
		
		String arrDt [] =null;
		try {

			setSessionInfo(inputMap);
			TerminalRequestDAO lObjTerminalRequestDAO = new TerminalRequestDAOImpl(null, serv.getSessionFactory());
		
		
			empId = StringUtility.getParameter("empId", request);
			long dcpsEmpId=lObjTerminalRequestDAO.getDcpsEmpIdForDcpsId(empId);
		    checkStatus=lObjTerminalRequestDAO.finalterminationFlagUpdation(dcpsEmpId);
		
			String lSBStatus = getResponseXMLDocForfinalterminateEmpdtls(checkStatus).toString();
			gLogger.info("final status is "+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDocForfinalterminateEmpdtls(int status) throws Exception {

		StringBuilder lStrBldXML = new StringBuilder();

		gLogger.info("status in AJAX************" + status);
		
	

		lStrBldXML.append("<XMLDOC>"); 

		lStrBldXML.append("<MESSAGE>");

		lStrBldXML.append(status);

		lStrBldXML.append("</MESSAGE>");

		lStrBldXML.append("</XMLDOC>");

		gLogger.info("lStrBldXML************" + lStrBldXML);
	
	
	
		return lStrBldXML;
	}
	

}



