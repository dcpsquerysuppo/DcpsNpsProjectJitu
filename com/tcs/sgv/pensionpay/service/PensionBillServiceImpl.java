package com.tcs.sgv.pensionpay.service;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.dao.BillMovementDAOImpl;
import com.tcs.sgv.common.dao.BptmCommonServicesDAOImpl;
import com.tcs.sgv.common.dao.CmnDatabaseMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLocationMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.dao.CommonDAO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.dao.PostConfigurationDAOImpl;
import com.tcs.sgv.common.dao.RltBillPartyDAO;
import com.tcs.sgv.common.dao.RltBillPartyDAOImpl;
import com.tcs.sgv.common.helper.ColumnVo;
import com.tcs.sgv.common.helper.ReportExportHelper;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.BptmCommonServiceImpl;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMpgDAOImpl;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAO;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttdocMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.common.valueobject.CmnDatabaseMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.MstAuditorConfig;
import com.tcs.sgv.common.valueobject.RltBillParty;
import com.tcs.sgv.common.valueobject.SgvoDeptMst;
import com.tcs.sgv.common.valueobject.TrnBillMvmnt;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.dao.BudgetDtlsDAO;
import com.tcs.sgv.pensionpay.dao.BudgetDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAO;
import com.tcs.sgv.pensionpay.dao.CommonPensionDAOImpl;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO;
import com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAO;
import com.tcs.sgv.pensionpay.dao.OnlinePensionCaseDAOImpl;
import com.tcs.sgv.pensionpay.dao.PensionBillDAO;
import com.tcs.sgv.pensionpay.dao.PensionBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO;
import com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAOImpl;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAO;
import com.tcs.sgv.pensionpay.dao.RltPensioncaseBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.SupplementaryBillDAO;
import com.tcs.sgv.pensionpay.dao.SupplementaryBillDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnChequeDtlsDAO;
import com.tcs.sgv.pensionpay.dao.TrnChequeDtlsDAOImpl;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAO;
import com.tcs.sgv.pensionpay.dao.TrnPensionRqstHdrDAOImpl;
import com.tcs.sgv.pensionpay.valueobject.ConfigOnlineBill;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.ReturnedEcsDtls;
import com.tcs.sgv.pensionpay.valueobject.RltBillCheque;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.SavedPensionBillVO;
import com.tcs.sgv.pensionpay.valueobject.TrnChequeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnEcsDtl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.reports.service.ReportServiceImpl;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;
import com.tcs.sgv.wf.exception.AlternateHierarchyNotfoundException;
import com.tcs.sgv.wf.exception.HierarchyNotFoundException;
import com.tcs.sgv.wf.exception.UpperPostNotFoundException;


public class PensionBillServiceImpl extends ServiceImpl implements PensionBillService {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	/* Global Variable for Location Id */
	private String gStrLocId = null;

	/* Global Variable for PostId */
	private Long gLngPostId = null;

	/* Global Variable for UserId */
	private Long gLngUserId = null;

	/* Global Variable for LangId */
	private Long gLngLangId = null;

	/* Global Variable for DB Id */
	private Long gLngDBId = null;

	/* Global Variable for Location Code */
	private Long gLocId = null;

	/* Global Variable for current Date */
	private Date gDtCurDate = null;

	/* Global Variable for Location Code */
	private String gStrLocationCode = null;

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for ResourceBundle */
	private static ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");
	private ResourceBundle bundle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private Integer gIntECSFlag = 0;

	public ResultObject createPensionSpecificBill(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ResultObject lResultObj = null;
		Map lMapBillTypeDtls = null;
		String lStrCurrRoleId = null;
		String lStrTempVal = "BeforeSave";
		String lStrElementCode = null;
		try {
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			String lStrViewFlag = StringUtility.getParameter("ViewFlag", request);
			String lStrGenPnsnBillForFlag = StringUtility.getParameter("pnsnBillGenFor", request);
			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			inputMap.put("lStrGenPnsnBillForFlag", lStrGenPnsnBillForFlag);
			setSessionInfo(inputMap);
			String[] lStrRqstID = new String[1];
			lStrRqstID[0] = "-1";
			String[] lStrEmpType = new String[1];
			lStrEmpType[0] = "-1";
			CmnLookupMstDAO cmnDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());
			ConfigOnlineBill lObjConfigVO = populateConfigOnlineBillVO(Long.decode(request.getParameter("subjectId")), serv.getSessionFactory());
			CommonPensionDAOImpl lObjCommonPensionDAOImpl = new CommonPensionDAOImpl(serv.getSessionFactory());
			String lStrTcBill = bundleConst.getString("Lookup.TCBillType");
			List lBillType = cmnDAO.getAllChildrenByLookUpNameAndLang(lStrTcBill, gLngLangId);

			String lStrSubjectId = StringUtility.getParameter("subjectId", request);
			if ("9".equals(lStrSubjectId) || "10".equals(lStrSubjectId) || "11".equals(lStrSubjectId)) {
				inputMap.put("CaseAudit", StringUtility.getParameter("auditFlag", request));
				inputMap.put("isNewSavingBill", request.getParameter("isNewSavingBill"));
			}
			// To get current users Role
			// ------Start------
			// OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new
			// OnlinePensionCaseDAOImpl(TrnEcsDtl.class,
			// serv.getSessionFactory());
			// lStrCurrRoleId =
			// lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			lStrCurrRoleId = lObjCommonPensionDAOImpl.getRoleByElement(lStrElementCode);
			inputMap.put("currRole", lStrCurrRoleId);
			// ------End-----
			inputMap.put("ViewFlag", lStrViewFlag);
			inputMap.put("PPONo", StringUtility.getParameter("PPONo", request));
			inputMap.put("subjectId", request.getParameter("subjectId"));
			inputMap.put("PnsnHeadCode", request.getParameter("PnsnHeadCode"));
			inputMap.put("PensionType", StringUtility.getParameter("PensionType", request));
			inputMap.put("PensionScheme", StringUtility.getParameter("Scheme", request));

			// Calling bill specific method to get bill specific data
			if ("44".equals(StringUtility.getParameter("subjectId", request))) {
				inputMap.put("MonthlyPrint", "Y");
			}

			String lStrIsConfigStatus = lObjConfigVO.getIsConfigured();

			if (lObjConfigVO != null && lObjConfigVO.getIsConfigured().equals("Y")) {
				String lStrRqstServ = lObjConfigVO.getRqstSrvc();
				String lStrPagePath = lObjConfigVO.getJspPath();
				inputMap.put("AprdRqstIdArray", lStrRqstID);
				lResultObj = serv.executeService(lStrRqstServ, inputMap);
				Map lMapResult = (Map) lResultObj.getResultValue();
				inputMap.put("BillData", lMapResult);
				inputMap.put("PagePath", lStrPagePath);
			}

			/** Get HeadStructure Data */
			getHeadDetailsByBillType(inputMap);

			SgvoDeptMst lDeptMstVO = new SgvoDeptMst();

			inputMap.put("LangId", gLngLangId);
			// System.out.println("Data " + inputMap.get("TrnBillRegisterVO"));
			List lMonthList = lObjCommonPensionDAOImpl.getMonthDtls(gLngLangId);

			// Party Information For Specific Bill... Start...
			PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			List<RltBillParty> lLstRltBillPartyVO = null;

			// if(! "MR".equals( StringUtility.getParameter("PensionType",
			// request)))
			{
				lLstRltBillPartyVO = lObjPensionBillDAO.getPensionerPartyDtls(inputMap);
				inputMap.put("RltBillParty", lLstRltBillPartyVO);
			}
			// Party Information For Specific Bill... End...

			lMapBillTypeDtls = getCmnLookUpValueAndDescription(cmnDAO, "Regular");
			inputMap.put("Selected_BillType", lMapBillTypeDtls);
			inputMap.put("BillTypeId", request.getParameter("subjectId"));
			inputMap.put("BillType", lBillType);
			inputMap.put("EmpTypeSelectedByUser", lStrEmpType);
			inputMap.put("DeptVO", lDeptMstVO);
			inputMap.put("MonthList", lMonthList);
			inputMap.put("currRemarks", "");
			inputMap.put("isConfigStatus", lStrIsConfigStatus);
			inputMap.put("isShowObjection", "N");
			inputMap.put("billStatus", "0");
			inputMap.put("TempVal", lStrTempVal);

			objRes.setResultValue(inputMap);
			objRes.setViewName("createPensionBills");
		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;
	}

	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gLocId = SessionHelper.getLocationId(inputMap);
		gDtCurDate = DBUtility.getCurrentDateFromDB();
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		serv = (ServiceLocator) inputMap.get("serviceLocator");
	}

	public Map getCmnLookUpValueAndDescription(CmnLookupMstDAO lDAOCmnLkUpMst, String lStrLookupName) {

		CmnLookupMst lStrCmnLookUpDescVO = lDAOCmnLkUpMst.getLookUpVOByLookUpNameAndLang(lStrLookupName, gLngLangId);
		String lStrCmnLookUpDesc = lStrCmnLookUpDescVO.getLookupDesc();
		String lStrCmnLookUpShrtName = lStrCmnLookUpDescVO.getLookupShortName();

		Map<String, Object> lMapValueAndDesc = new HashMap<String, Object>();
		lMapValueAndDesc.put("Value", lStrLookupName);
		lMapValueAndDesc.put("Desc", lStrCmnLookUpDesc);
		lMapValueAndDesc.put("Label", lStrCmnLookUpShrtName);

		return lMapValueAndDesc;
	}

	public ResultObject isAllSpcificBillsCrtd(Map<String, Object> orgsMap) {

		ResultObject lObjResult = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) orgsMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) orgsMap.get("requestObj");
		StringBuilder lStrRes = new StringBuilder();
		try {
			setSessionInfo(orgsMap);
			PensionBillDAOImpl lObjPnsnBillDao = new PensionBillDAOImpl(serv.getSessionFactory());
			String lStrppoNo = request.getParameter("ppoNo").toString();
			String lStrReqStr = request.getParameter("ppoNo").toString();
			String[] chk = null;
			chk = lStrReqStr.split("~");
			lStrppoNo = chk[2];
			lStrRes.append(lObjPnsnBillDao.isAllBillsCreated(lStrppoNo, gStrLocId));
			orgsMap.put("ajaxKey", lStrRes.toString());
			lObjResult.setResultValue(orgsMap);
			lObjResult.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			lObjResult.setResultValue(null);
			lObjResult.setThrowable(e);
			lObjResult.setResultCode(ErrorConstants.ERROR);
			lObjResult.setViewName("errorPage");
		}
		return lObjResult;
	}

	public ResultObject savePensionSpecificBills(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrSubject = bundleConst.getString("OnlineBillSubject");
		String lStrAction = null;
		StringBuilder lStrBldXML = null;
		TrnBillRegister lObjBillReg = (TrnBillRegister) inputMap.get("BillRegVO");
		String lStrShowReadOnly = null;

		try {
			String lStrSubjectId = StringUtility.getParameter("hidBillTypeId", request);
			if (lStrSubjectId != null && lStrSubjectId.length() > 0) {
				inputMap.put("subjectId", lStrSubjectId);
				inputMap.put("SubjectId", lStrSubjectId);
			}
			String lStrBillNo = StringUtility.getParameter("hidBillNo", request);

			setSessionInfo(inputMap);

			// For ReadOnlyFlag
			lStrShowReadOnly = ((StringUtility.getParameter("showReadOnly", request).trim()).length() > 0) ? StringUtility.getParameter("showReadOnly", request).trim() : null;
			if (lStrShowReadOnly != null && lStrShowReadOnly.length() > 0) {
				inputMap.put("showReadOnly", lStrShowReadOnly);
			}

			if (lStrBillNo == null || lStrBillNo.length() == 0) // Call the
			// workflow at
			// time of
			// Create Bill ONLY
			{
				inputMap.put("subjectType", lStrSubject);

				insertPensionBillWF(inputMap);
				// createOnlineBillFromWF(inputMap);//
				// serv.executeService("CREATE_ONLINEBILL",
				// inputMap); //
				// createOnlineBillFromWF

			} else {
				inputMap = updatePensionBill(inputMap);
			}

			lStrAction = StringUtility.getParameter("userAction", request).trim();

			if (lObjBillReg != null) {
				Long lStrSubId = lObjBillReg.getSubjectId();
				if ("9".equalsIgnoreCase(lStrSubId.toString()) || "10".equalsIgnoreCase(lStrSubId.toString()) || "11".equalsIgnoreCase(lStrSubId.toString())
						|| "45".equalsIgnoreCase(lStrSubId.toString())) {
					String lStrPPONo = StringUtility.getParameter("hidDPPONO", request);

					String lStrBillType = null;
					if ("10".equalsIgnoreCase(lStrSubId.toString())) {
						lStrBillType = "DCRG";
					}
					if ("11".equalsIgnoreCase(lStrSubId.toString())) {
						lStrBillType = "CVP";
					}
					if ("9".equalsIgnoreCase(lStrSubId.toString())) {
						lStrBillType = "PENSION";
					}

					// For TcBill Field-Start
					if ("9".equalsIgnoreCase(lStrSubId.toString()) || "10".equalsIgnoreCase(lStrSubId.toString()) || "11".equalsIgnoreCase(lStrSubId.toString())) {
						lObjBillReg.setTcBill(bundleConst.getString("PPMT.FIRSTPAY"));
					} else if ("44".equalsIgnoreCase(lStrSubId.toString())) {
						lObjBillReg.setTcBill(bundleConst.getString("PPMT.MONTHLY"));
					} else if ("45".equalsIgnoreCase(lStrSubId.toString())) {
						lObjBillReg.setTcBill(bundleConst.getString("PPMT.SUPPLY"));
					}
					if ("9".equalsIgnoreCase(lStrSubId.toString()) || "10".equalsIgnoreCase(lStrSubId.toString()) || "11".equalsIgnoreCase(lStrSubId.toString())) {
						if (!"reject".equals(lStrAction)) {
							RltPensioncaseBillDAO lObjRltPnsnCaseBillDao = new RltPensioncaseBillDAOImpl(RltPensioncaseBill.class, serv.getSessionFactory());
							lObjRltPnsnCaseBillDao.updateRltPensioncaseBillStatusAndBillNoForByPPONo(lStrPPONo, "Y", null, lStrBillType, lObjBillReg.getBillNo(), gStrLocId, gLngPostId, gLngUserId,
									gDtCurDate);
						}
					}
				}
				inputMap.put("SubjectId", lObjBillReg.getSubjectId());
			}

			// Prepares an XML String containing PKs of all the common and
			// billspecific tables
			lStrBldXML = getResponseXMLDoc(inputMap);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			objRes.setThrowable(e);
			objRes.setResultValue(null);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;
	}

	/**
	 * Insert PensionSpecifics Bills...
	 * 
	 * @param objectArgs
	 * @return
	 */
	public Map insertPensionBillWF(Map<String, Object> inputMap) throws Exception {

		Long lLngBillNo = 0L;
		boolean lBoolIsNewBill = false;
		String lStrSubjectId = null;
		try {
			inputMap.put("IsNewBill", lBoolIsNewBill);
			ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			if (inputMap.containsKey("subjectId")) {
				lStrSubjectId = inputMap.get("subjectId").toString();
			}
			if (!"44".equals(lStrSubjectId)) {
				TrnBillRegister lObjBillReg = (TrnBillRegister) inputMap.get("BillRegVO");
				TrnBillMvmnt lObjBillMvmnt = (TrnBillMvmnt) inputMap.get("BillMvmntVO");
				RltBillParty lObjBillParty = null;
				List<RltBillParty> lLstBillParty = null;
				RltBillPartyDAO lObjBillPartyDAO = new RltBillPartyDAOImpl(RltBillParty.class, srvcLoc.getSessionFactory());

				saveBillRegister(lObjBillReg, inputMap);
				lLngBillNo = lObjBillReg.getBillNo();

				Long billNoOBPM = lLngBillNo;
				inputMap.put("billNoOBPM", billNoOBPM);

				/** Insert Bill Movement */
				lObjBillMvmnt.setBillNo(lLngBillNo);
				lObjBillMvmnt.setMovemntId(Long.parseLong("1"));
				lObjBillMvmnt.setMvmntStatus(DBConstants.ST_BAPRVD_DDO);
				// BptmCommonServiceImpl.insertMovement(lObjBillMvmnt,inputMap);
				saveBillMovement(lObjBillMvmnt, inputMap);

				inputMap.put("BillNo", lLngBillNo);

				inputMap.put("billNo", String.valueOf(lLngBillNo));

				// Deleting previous party Info
				lLstBillParty = lObjBillPartyDAO.getPartyByBill(lLngBillNo);
				Iterator<RltBillParty> lItrBillParty = lLstBillParty.iterator();
				while (lItrBillParty.hasNext()) {
					lObjBillPartyDAO.delete(lItrBillParty.next());
				}

				// Saving the Bill Party Details
				if (inputMap.get("BillPartyDtls") != null) {
					lLstBillParty = (List<RltBillParty>) inputMap.get("BillPartyDtls");
					lItrBillParty = lLstBillParty.iterator();

					while (lItrBillParty.hasNext()) {
						lObjBillParty = lItrBillParty.next();
						lObjBillParty.setBillNo(lLngBillNo);
						saveBillParty(lObjBillParty, inputMap);
					}
				}
			}
			if ("44".equals(StringUtility.getParameter("hidBillTypeId", request))) {
				inputMap.put("PensionType", StringUtility.getParameter("PensionType", request));
				inputMap.put("HeadCode", StringUtility.getParameter("PnsnHeadCode", request));
			}

			ConfigOnlineBill lObjConfigVO = populateConfigOnlineBillVO(Long.parseLong(lStrSubjectId), srvcLoc.getSessionFactory());
			ResultObject lbillResObj = srvcLoc.executeService(lObjConfigVO.getSaveBillSrvc(), inputMap);
			if (lbillResObj.getResultValue() == null && lbillResObj.getResultCode() == ErrorConstants.ERROR) {
				throw new Exception(lbillResObj.getThrowable().toString());
			}

			inputMap.put("postId", gLngPostId);
			inputMap.put("StateMsg", "Bill Saved Successfully");

		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			throw (e);
		}
		return inputMap;
	}

	public void createOnlineBillFromWF(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrAction = null;
		StringBuilder lStrBldXML = null;

		String lStrDocId = "210004";
		String lStrSubjectId = null;

		try {
			setSessionInfo(inputMap);
			if (inputMap.containsKey("subjectId")) {
				lStrSubjectId = inputMap.get("subjectId").toString();
			}
			if (!"44".equals(lStrSubjectId)) {
				TrnBillRegister lObjBillReg = (TrnBillRegister) inputMap.get("BillRegVO");
				if (lObjBillReg.getBillNo() == 0) // Call the workflow at time
				// of
				// Create Bill ONLY
				{
					Long lLngHeirRefId = 210009l;// WorkFlowHelper.getHierarchyByPostIDAndDescription(gLngPostId.toString(),
					// lStrSubject, inputMap);
					lObjBillReg.setHierarchyRefId(lLngHeirRefId);
					inputMap.put("NewBillFlag", "Y");

					inputMap.put("HeirRefId", lLngHeirRefId);

					inputMap.put("Docid", Long.parseLong(lStrDocId));
					inputMap.put("Pkvalue", lObjBillReg.getBillNo());
					inputMap.put("DisplayJobTitle", "OnlineBill");
					inputMap.put("Hierarchy_ref_id", lLngHeirRefId);
					WorkFlowDelegate.create(inputMap);
				}
			}
			lStrAction = StringUtility.getParameter("userAction", request);

			if (lStrAction != null && lStrAction.equalsIgnoreCase("save")) {
				inputMap.put("isEditable", "Y");
			} else {
				inputMap.put("isEditable", "N");
			}

			lStrBldXML = getResponseXMLDoc(inputMap);

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (HierarchyNotFoundException h) {
			StringBuilder lStrFwdStatus = new StringBuilder();
			lStrFwdStatus.append(" <HierarchyNotConfigured> ");
			lStrFwdStatus.append(" 	true ");
			lStrFwdStatus.append(" </HierarchyNotConfigured> ");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrFwdStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);

			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Update PensionSpecifics Bills Remarks & Objection...
	 * 
	 * @param objectArgs
	 * @return
	 */
	public Map updatePensionBill(Map<String, Object> inputMap) throws Exception {

		TrnBillRegister lObjBillReg = (TrnBillRegister) inputMap.get("BillRegVO");
		TrnBillMvmnt lObjBillMvmnt = (TrnBillMvmnt) inputMap.get("BillMvmntVO");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrUserAction = null;
		Short lSrtCurrBillStatus = null;
		String lStrShowReadOnly = null;
		List<Long> lLstBillNo = new ArrayList<Long>();
		Long lSubId = null;
		Long lLngFirstPnsnSubId = 9L;
		Long lLngMonthlySubId = 44L;
		Long lLngSupplySubId = 45L;
		// String lStrRemarks=null;
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		List<String> lLstPpoNo = new ArrayList<String>();
		String lStrCurrRoleId = "";
		Date lDtBillDate = null;
		Integer lIntPayForMonth = null;
		List<Object[]> lLstArrObj = null;
		String lStrElementCode = null;
		List<String> lLstPnsrCode = new ArrayList<String>();
		try {
			setSessionInfo(inputMap);
			Long lLngBillNo = lObjBillReg.getBillNo();
			lDtBillDate = lObjBillReg.getBillDate();
			if (lLngBillNo != null) {
				lLstBillNo.add(lLngBillNo);
			}
			lSubId = lObjBillReg.getSubjectId();

			lStrCurrRoleId = StringUtility.getParameter("currRole", request).trim();

			if ("".equals(lStrCurrRoleId.trim())) {
				lStrElementCode = StringUtility.getParameter("elementId", request).trim();
				// To get current users Role
				// ------Start------
				// OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new
				// OnlinePensionCaseDAOImpl(TrnEcsDtl.class,
				// serv.getSessionFactory());
				CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
				// lStrCurrRoleId =
				// lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
				lStrCurrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			}
			inputMap.put("currRole", lStrCurrRoleId);
			// ------End-----
			lStrUserAction = ((StringUtility.getParameter("userAction", request).trim()).length() > 0) ? StringUtility.getParameter("userAction", request).trim() : null;

			if ("approve".equals(lStrUserAction)) {
				lSrtCurrBillStatus = DBConstants.ST_BILL_APPROVED;
				lObjBillReg.setCurrBillStatus(lSrtCurrBillStatus);
				lObjBillMvmnt.setMvmntStatus(lSrtCurrBillStatus);

				/*
				 * ---Code For inserting last paid date in trnpensionrqsthdr for
				 * pension bill starts
				 */

				if (!lLstBillNo.isEmpty()) {
					if (lLngFirstPnsnSubId.equals(lSubId) || lLngMonthlySubId.equals(lSubId)) {
						// lLstPpoNo =
						// lObjPensionDao.getPPONoListFromPnsncaseRlt(lLstBillNo);
						lLstPpoNo = lObjPensionDao.getPpoNoByBillNo(lLstBillNo);
						lLstArrObj = lObjPensionDao.getPnsrCodeFromBillNo(lLstBillNo);
						for (Object[] lObjArr : lLstArrObj) {
							lLstPnsrCode.add(lObjArr[0].toString());
							lIntPayForMonth = (Integer) lObjArr[1];
						}
						// lObjPensionDao.updateBillNoForRecovery(lLstPnsrCode,
						// lIntPayForMonth, gStrLocationCode, lLstBillNo.get(0),
						// gLngUserId, gLngPostId, gDtCurDate);
						lObjPensionDao.updatePaidFlagInArrearDtlsOnApproveReject(lLstBillNo, "Y", gLngUserId, gLngPostId, gDtCurDate);
					}
					if (!lLstPpoNo.isEmpty()) {
						lObjPensionDao.updateLastPaidDateForPensionBills(lLstPpoNo, lDtBillDate);
					}
					/*
					 * --Updating six pay arrear paid flag to "Y"
					 */
					if (!lLstPnsrCode.isEmpty()) {
						lObjPensionDao.updateSixpayArrearPaidFlag(lLstPnsrCode, gLngUserId, gLngUserId, gDtCurDate, 'Y', lIntPayForMonth, lLstBillNo.get(0));
					}
					if (lLngSupplySubId.equals(lSubId)) {
						lObjPensionDao.updateSixpayArrearDtlBySupplementaryBill(lLstBillNo.get(0), 'Y', gLngUserId, gLngUserId, gDtCurDate);
					}
				}

				/*
				 * ---Code For inserting last paid date in trnpensionrqsthdr for
				 * pension bill ends
				 */
			} else if ("reject".equals(lStrUserAction)) {
				if (bundleConst.getString("PPMT.ATOROLE").equals(lStrCurrRoleId)) {
					lSrtCurrBillStatus = DBConstants.ST_BILL_REJECTED;
					/*
					 * Discarding change statement on reject of monthly bill
					 * starts <<<<<
					 */
					if (!lLstBillNo.isEmpty()) {
						if (!lLngMonthlySubId.equals(lSubId)) {
							lObjPensionDao.updateRltPensioncaseBill(lLstBillNo, gStrLocationCode, gLngPostId, gLngUserId, gDtCurDate);
						}
						if (lLngMonthlySubId.equals(lSubId)) {
							discardChngStmnt(lLstBillNo, inputMap);
							List<Long> lLstChngRqstId = lObjMonthlyPensionBillDAO.getChangeRqstIdFromBillNo(lLstBillNo);
							if (lLstChngRqstId != null && lLstChngRqstId.size() > 0) {
								lObjMonthlyPensionBillDAO.updateMonthlyPnsnRcryOnChngStmntApproval(lLstChngRqstId, bundleConst.getString("CHANGSTMNTSTATUS.DISCARDED"), gStrLocationCode, gLngUserId,
										gLngPostId, gDtCurDate);
							}

						}

						if (lLngFirstPnsnSubId.equals(lSubId) || lLngMonthlySubId.equals(lSubId)) {
							lLstArrObj = lObjPensionDao.getPnsrCodeFromBillNo(lLstBillNo);

							for (Object[] lObjArr : lLstArrObj) {
								lLstPnsrCode.add(lObjArr[0].toString());
								lIntPayForMonth = (Integer) lObjArr[1];
							}
							/*
							 * --On rejection of pension bill or monthly bill
							 * updating paid flag to N in all arrear details
							 * having bill no passed in argument.
							 */
							lObjPensionDao.updatePaidFlagInArrearDtlsOnApproveReject(lLstBillNo, "N", gLngUserId, gLngPostId, gDtCurDate);
							/*
							 * --Updating six pay arrear paid flag to "N"
							 */
							if (!lLstPnsrCode.isEmpty()) {
								lObjPensionDao.updateSixpayArrearPaidFlag(lLstPnsrCode, gLngUserId, gLngUserId, gDtCurDate, 'N', lIntPayForMonth, lLstBillNo.get(0));
							}
						}
						if (lLngSupplySubId.equals(lSubId)) {
							// lObjPensionDao.updateSixpayArrearDtlBySupplementaryBill(lLstBillNo.get(0),
							// 'N', gLngUserId, gLngUserId, gDtCurDate);
							lObjPensionDao.deleteSixpayArrearConfigBySuppleBill(lLstBillNo);
						}
					}
					/*
					 * Discarding change statement on reject of monthly bill
					 * ends >>>>>>
					 */
				}
				if (bundleConst.getString("PPMT.TRSRYAUDITORROLE").equals(lStrCurrRoleId)) {
					lSrtCurrBillStatus = DBConstants.ST_BILL_REJECTED_BY_TRSRY;
				}
				if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId)) {
					lSrtCurrBillStatus = DBConstants.ST_BILL_FORW_TO_TAUD;
				}
				lObjBillReg.setCurrBillStatus(lSrtCurrBillStatus);
				lObjBillMvmnt.setMvmntStatus(lSrtCurrBillStatus);
			}

			/** Save or Update the Remarks. */

			lObjBillMvmnt.setBillNo(lLngBillNo);
			saveBillMovement(lObjBillMvmnt, inputMap);

			if (inputMap.containsKey("showReadOnly")) {
				lStrShowReadOnly = inputMap.get("showReadOnly").toString().trim();
			}
			if ("N".equals(lStrShowReadOnly)) {

				inputMap.put("billNo", lObjBillReg.getBillNo());
				ConfigOnlineBill lObjConfigVO = populateConfigOnlineBillVO(lObjBillReg.getSubjectId(), srvcLoc.getSessionFactory());
				ResultObject lbillResObj = srvcLoc.executeService(lObjConfigVO.getSaveBillSrvc(), inputMap);
				if (lbillResObj.getResultValue() == null && lbillResObj.getResultCode() == ErrorConstants.ERROR) {
					throw new Exception(lbillResObj.getThrowable().toString());
				}
			}
			/*
			 * if (lStrTokenFlg != null && lStrTokenFlg.equalsIgnoreCase("N")) {
			 * if (lPnsnTokenNo != null && lPnsnTokenNo.toString().length() > 0)
			 * // If // FP // Auditor // Not Enter // Token No. {
			 * CommonPensionDAOImpl lObjCommonPensionDAO = new
			 * CommonPensionDAOImpl(srvcLoc.getSessionFactory()); Long
			 * lLngCaseId =
			 * lObjCommonPensionDAO.getPKForRltpensionCaseBill(lObjBillReg
			 * .getBillNo()); Long lLngHdrId =
			 * lObjCommonPensionDAO.getPKForBillHdr(lObjBillReg.getBillNo()); //
			 * Update Token No Into Bill Register.
			 * 
			 * lObjBillReg.setTokenNum(Long.valueOf(lPnsnTokenNo));
			 * phyBillDAOImpl.update(lObjBillReg);
			 * 
			 * // Update Token No Into TrnPensionBillHdr.
			 * 
			 * 
			 * TrnPensionBillHdrDAO lObjTrnPensionBillHdrDAO = new
			 * TrnPensionBillHdrDAOImpl
			 * (TrnPensionBillHdr.class,srvcLoc.getSessionFactory());
			 * TrnPensionBillHdr lObjTrnPensionBillHdr = null; if(lLngHdrId !=
			 * null && lLngHdrId != 0L) { lObjTrnPensionBillHdr =
			 * lObjTrnPensionBillHdrDAO.read(lLngHdrId);
			 * lObjTrnPensionBillHdr.setTokenNo(lPnsnTokenNo);
			 * lObjTrnPensionBillHdrDAO.update(lObjTrnPensionBillHdr); }
			 * 
			 * // Update Token No Into RltPensioncaseBill RltPensioncaseBillDAO
			 * lObjRltPensioncaseBillDAO = new
			 * RltPensioncaseBillDAOImpl(RltPensioncaseBill.class,
			 * srvcLoc.getSessionFactory()); RltPensioncaseBill
			 * lObjRltPensioncaseBill = null; if (lLngCaseId != null &&
			 * lLngCaseId != 0L) { lObjRltPensioncaseBill =
			 * lObjRltPensioncaseBillDAO.read(lLngCaseId);
			 * lObjRltPensioncaseBill.setTokenNo(Long.valueOf(lPnsnTokenNo));
			 * lObjRltPensioncaseBillDAO.update(lObjRltPensioncaseBill); }
			 * 
			 * // Update Token Status. OrgTokenStatusDAO lObjTokenStatusDAO =
			 * new OrgTokenStatusDAOImpl(OrgTokenStatus.class,
			 * srvcLoc.getSessionFactory());
			 * lObjTokenStatusDAO.updateTokenStatus(Long.valueOf(lPnsnTokenNo),
			 * gStrLocId, lLngBillNo, gLngUserId, gLngPostId,
			 * BillProcConstants.INWARD_TYPE_BILL);
			 * 
			 * ReportServiceImpl rptService = new ReportServiceImpl();
			 * rptService.updateTokenIssue(objectArgs, lPnsnTokenNo.toString(),
			 * lLngBillNo, DBUtility.getCurrentDateFromDB(), null, "B");
			 * 
			 * } }
			 */
			inputMap.put("BillNo", lLngBillNo);
			inputMap.put("jobId", lLngBillNo + "");
			inputMap.put("postId", gLngPostId);
			if ("N".equals(lStrShowReadOnly)) {
				inputMap.put("StateMsg", "Bill Updated Successfully.");
			} else {
				inputMap.put("StateMsg", "Bill Saved Successfully.");
			}

		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			throw (e);
		}
		return inputMap;
	}

	/**
	 * Saves the Bills data in TrnBillRegister. In case of Bill Creation it
	 * inserts the data else it updates the data.
	 * 
	 * @param lObjTrnBillRegVO
	 *            TrnBillRegisterVO
	 * @param inputMap
	 *            InputMap.
	 * @throws Exception
	 *             Exception
	 */
	public void saveBillRegister(TrnBillRegister lObjTrnBillRegVO, Map<String, Object> inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		BptmCommonServicesDAOImpl lObjBptmCommonServicesDAO = new BptmCommonServicesDAOImpl(TrnBillRegister.class, srvcLoc.getSessionFactory());
		PostConfigurationDAOImpl postConfigDAO = new PostConfigurationDAOImpl(MstAuditorConfig.class, srvcLoc.getSessionFactory());
		try {
			setSessionInfo(inputMap);
			if (lObjTrnBillRegVO.getBillNo() > 0) {
				lObjBptmCommonServicesDAO.update(lObjTrnBillRegVO);

				// delete entry in case of TC bill & multiple challans & if any
				// record is deleted from jsp - start
				if (lObjTrnBillRegVO.getTcBill().equalsIgnoreCase("TC")) {
					inputMap.put("billNoOBPM", lObjTrnBillRegVO.getBillNo());
					// deleteChallanRecord(inputMap);
				}
			} else {
				synchronized (this) {
					inputMap.put("DDOCode", lObjTrnBillRegVO.getDdoCode());
					Long lngHeirRefId = (Long) inputMap.get("HeirRefId");
					String lStrBillCntrlNo = BptmCommonServiceImpl.getBillControlNo(inputMap);
					// String lStrBillCntrlNo =
					// BptmCommonServiceImpl.getBillControlNumber(inputMap); //
					// getBillCntrlNumSeq
					lObjTrnBillRegVO.setBillCntrlNo(lStrBillCntrlNo);
					// lObjTrnBillRegVO.setInwardDt(lObjTrnBillRegVO.getBillDate());
					lObjTrnBillRegVO.setAudPostId(SessionHelper.getPostId(inputMap));
					lObjTrnBillRegVO.setAudUserId(SessionHelper.getUserId(inputMap));
					lObjTrnBillRegVO.setHierarchyRefId(lngHeirRefId); // Deep
					lObjTrnBillRegVO.setIsArchieved(new Short("0"));
					lObjTrnBillRegVO.setBranchCode(postConfigDAO.getBranchCodeFromPostId(lObjTrnBillRegVO.getAudPostId(), gLngLangId));
					Long lLngBillNo = IFMSCommonServiceImpl.getNextSeqNum("trn_bill_register", inputMap);
					lObjTrnBillRegVO.setBillNo(lLngBillNo);
					lObjBptmCommonServicesDAO.create(lObjTrnBillRegVO);
				}
			}

			inputMap.put("billNo", lObjTrnBillRegVO.getBillNo());
			inputMap.put("PnsnBillCntrlNo", lObjTrnBillRegVO.getBillCntrlNo());
			if (lObjTrnBillRegVO.getTokenNum() != null && lObjTrnBillRegVO.getTokenNum().toString().length() > 0) {
				inputMap.put("PnsnTokenNo", lObjTrnBillRegVO.getTokenNum());
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
	}

	/**
	 * Saves the Party data associated with the Bills in RltBillParty. the data.
	 * 
	 * @param RltBillParty
	 *            lObjBillParty, Map InputMap.
	 * @throws Exception
	 *             Exception
	 */

	public void saveBillParty(RltBillParty lObjBillParty, Map<String, Object> inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		RltBillPartyDAO lObjBillPartyDAO = new RltBillPartyDAOImpl(RltBillParty.class, srvcLoc.getSessionFactory());
		Long lLngBillPartyId = null;

		try {
			lLngBillPartyId = IFMSCommonServiceImpl.getNextSeqNum("rlt_bill_party", inputMap);
			lObjBillParty.setBillPartyId(lLngBillPartyId);
			lObjBillPartyDAO.create(lObjBillParty);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
	}

	/**
	 * Saves the Bill Movement Data in TrnBillMvmnt. This method will be called
	 * only at time of Bill Creation.
	 * 
	 * @param lObjBillMvmntVO
	 *            TrnBillMvmntVO
	 * @param inputMap
	 *            InputMap.
	 * @throws Exception
	 *             Exception
	 */
	public void saveBillMovement(TrnBillMvmnt lObjBillMvmntVO, Map<String, Object> inputMap) throws Exception {

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		BillMovementDAOImpl lObjMvmntDAO = new BillMovementDAOImpl(TrnBillMvmnt.class, srvcLoc.getSessionFactory());

		try {
			if (lObjBillMvmntVO.getBillMvmtId() > 0) {
				lObjMvmntDAO.update(lObjBillMvmntVO);
			} else {
				BptmCommonServiceImpl.insertMovement(lObjBillMvmntVO, inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
	}

	private StringBuilder getResponseXMLDoc(Map<String, Object> lMapInput) {

		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");
		StringBuilder lStrBldHidPKs = new StringBuilder();
		try {
			boolean lNewBillFlg = true;// (Boolean) lMapInput.get("IsNewBill");
			String lStrSubjectId = lMapInput.get("SubjectId").toString();

			String lStrTypeFlag = null;
			if (lMapInput.containsKey("PensionType")) {
				lStrTypeFlag = lMapInput.get("PensionType").toString();
			}
			lStrBldHidPKs.append("<XMLDOC>");
			lStrBldHidPKs.append(getCmnTabPKs(lMapInput));

			lStrBldHidPKs.append("<MESSAGECODE>");
			String lStrAction = StringUtility.getParameter("userAction", request).trim();

			if (lStrAction.equalsIgnoreCase("save")) {
				lStrBldHidPKs.append("Bill Saved Successfully.");
			} else if (lStrAction.equalsIgnoreCase("approve")) {
				lStrBldHidPKs.append("Bill Approved Successfully and Forwarded to Auditor.");
			} else if (lStrAction.equalsIgnoreCase("reject")) {
				String lStrCurrRoleId = lMapInput.get("currRole").toString();
				if (bundleConst.getString("PPMT.ATOROLE").equals(lStrCurrRoleId)) {
					lStrBldHidPKs.append("Bill Rejected Successfully and Forwarded to Auditor.");
				}
				if (bundleConst.getString("PPMT.TRSRYAUDITORROLE").equals(lStrCurrRoleId)) {
					lStrBldHidPKs.append("Bill Rejected Successfully and Forwarded to Pension ATO.");
				}
				if (bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId)) {
					lStrBldHidPKs.append("Bill Returned Successfully to Audit ATO.");
				}

			}

			lStrBldHidPKs.append("</MESSAGECODE>");
			lStrBldHidPKs.append("<NEWBILLFLAGE>");

			if (lNewBillFlg == false || "Monthly".equalsIgnoreCase(lStrTypeFlag) || "MR".equalsIgnoreCase(lStrTypeFlag)) {
				lStrBldHidPKs.append("false");
			} else {
				lStrBldHidPKs.append("true");
			}

			lStrBldHidPKs.append("</NEWBILLFLAGE>");
			lStrBldHidPKs.append("<SUBJECTID>");
			lStrBldHidPKs.append(lStrSubjectId);
			lStrBldHidPKs.append("</SUBJECTID>");
			lStrBldHidPKs.append("</XMLDOC>");
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
		}
		return lStrBldHidPKs;
	}

	private StringBuilder getCmnTabPKs(Map lMapInput) {

		StringBuilder lStrBldHidPKs = new StringBuilder();

		try {
			TrnBillRegister lObjTrnBillRegister = (TrnBillRegister) lMapInput.get("BillRegVO");

			lStrBldHidPKs.append("<TrnBillRegister>" + lObjTrnBillRegister.getBillNo());
			lStrBldHidPKs.append("</TrnBillRegister>");

			lStrBldHidPKs.append("<BillControlNumber><![CDATA[" + lObjTrnBillRegister.getBillCntrlNo());
			lStrBldHidPKs.append("]]></BillControlNumber>");

			lStrBldHidPKs.append("<BillTokenNumber>" + lObjTrnBillRegister.getTokenNum());
			lStrBldHidPKs.append("</BillTokenNumber>");
		} catch (Exception e) {
			gLogger.error("Error in execution of getHiddenPKsForUpdate. Error is : " + e, e);
		}

		return lStrBldHidPKs;
	}

	/**
	 * Fetches the saved Data for editing/viewing the Bill
	 * 
	 * @param Map
	 *            lMapInput
	 * @return lObjResult ResultObject
	 */
	public ResultObject getBillData(Map<String, Object> argsMap) {

		Map<String, Object> inputMap = argsMap;
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrSubjectId = null;
		String lStrBillType = "";
		try {
			setSessionInfo(inputMap);
			CommonPensionDAOImpl lObjCommonPensionDAOImpl = new CommonPensionDAOImpl(serv.getSessionFactory());
			ResultObject objOnlineRes = getActualBillData(inputMap);// serv.executeService("GET_BILL_DATA",
			// inputMap);
			inputMap = (Map) objOnlineRes.getResultValue();

			inputMap.put("LangId", gLngLangId);
			List lMonthList = lObjCommonPensionDAOImpl.getMonthDtls(gLngLangId);
			inputMap.put("MonthList", lMonthList);
			lStrSubjectId = inputMap.get("subjectId").toString();
			objRes.setResultValue(inputMap);
			if ("45".equals(lStrSubjectId)) {
				lStrBillType = (String) inputMap.get("BillType");
				if("PENSION".equals(lStrBillType)){
					objRes.setViewName("SchemewiseSupplBillDtls");
				}
				else {
				objRes.setViewName("SupplementaryBill");
				}
			} else {
				objRes.setViewName("createPensionBills");
			}

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}

		return objRes;
	}

	/**
	 * Getting a Calimant info regarding to current location and post.
	 * 
	 * @author Sagar Patel.
	 * 
	 */
	public List getClaimantInfo(Map inputMap) throws Exception {

		List lLstReturn = new ArrayList();
		try {
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			setSessionInfo(inputMap);
			Long lClaimantpostID = gLocId;
			PensionBillDAO objPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			lLstReturn = objPensionBillDAO.getClaimantDDOInfo(lClaimantpostID, SessionHelper.getLangId(inputMap));
		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			throw (e);
		}

		return lLstReturn;
	}

	/**
	 * Validate CVP , DCRG , Pension Bills Token No.
	 * 
	 * @author Nirav.
	 * 
	 */

	public ResultObject validatePensionBillsTokenNo(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		StringBuilder lStrGrant = new StringBuilder();

		try {
			setSessionInfo(inputMap);

			lStrGrant.append(" <TOKENRES> ");
			lStrGrant.append("true");
			lStrGrant.append(" </TOKENRES> ");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrGrant.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			lStrGrant.append(" <TOKENRES> ");
			lStrGrant.append("Error Processing Request...");
			lStrGrant.append(" </TOKENRES> ");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrGrant.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
		}
		return objRes;
	}

	public void validatePensionTokenOnSubmit(Map<String, Object> inputMap) throws Exception {

	}

	public void updateTokenDetails(Map<String, Object> inputMap) throws Exception {

	}

	/*
	 * public ResultObject getSavedPensionBills(Map<String, Object> lInputMap) {
	 * 
	 * ResultObject lObjResult = new ResultObject(ErrorConstants.SUCCESS,
	 * "FAIL"); ServiceLocator serv = (ServiceLocator)
	 * lInputMap.get("serviceLocator"); HttpServletRequest request =
	 * (HttpServletRequest) lInputMap.get("requestObj"); String
	 * lStrSubjectRegular = bundleConst.getString("OnlineBillSubject"); String
	 * lstrViewName = "";
	 * 
	 * BptmCommonServicesDAOImpl lObjCmnSrvcDAOImpl = new
	 * BptmCommonServicesDAOImpl(TrnBillRegister.class,
	 * serv.getSessionFactory());
	 * 
	 * try { setSessionInfo(lInputMap);
	 * 
	 * Short lStrCurStatus = null; if (request.getParameter("AprvdBills") !=
	 * null && request.getParameter("AprvdBills").equals("Y")) { lStrCurStatus =
	 * getStatusByLevel(Short.valueOf("70")); } else if
	 * (request.getParameter("BillFromlevel") != null) { lStrCurStatus =
	 * getStatusByLevel(Short.valueOf(request.getParameter("BillFromlevel"))); }
	 * lInputMap.put("savedBillsStatus", lStrCurStatus);
	 * 
	 * // lStrMyBills = getSavedPnsnBills(lInputMap);
	 * lObjResult.setResultValue(lInputMap); PensionBillDAOImpl lObjPnsnBillDao
	 * = new PensionBillDAOImpl(serv.getSessionFactory());
	 * 
	 * if ((request.getParameter("isCountr") != null &&
	 * request.getParameter("isCountr").equalsIgnoreCase("Y")) ||
	 * (lInputMap.get("isCountr") != null &&
	 * lInputMap.get("isCountr").equals("Y"))) { lInputMap.put("isCountr", "Y");
	 * } if ((request.getParameter("isAudit") != null &&
	 * request.getParameter("isAudit").equalsIgnoreCase("Y")) ||
	 * (lInputMap.get("isCountr") != null &&
	 * lInputMap.get("isCountr").equals("Y"))) { lInputMap.put("isAudit", "Y");
	 * } if ((request.getParameter("AprvdBills") != null &&
	 * request.getParameter("AprvdBills").equalsIgnoreCase("Y")) ||
	 * (lInputMap.get("AprvdBills") != null &&
	 * lInputMap.get("AprvdBills").equals("Y"))) {
	 * lInputMap.put("savedBillsStatus", DBConstants.ST_BAPRV_AUD);
	 * lInputMap.put("AprvdBills",
	 * request.getParameter("AprvdBills").toString()); }
	 * List<SavedPensionBillVO> dataList =
	 * lObjPnsnBillDao.getMyBills(gLngUserId, gLngLangId, lInputMap); if
	 * (request.getParameter("cmbSearch") != null) { lInputMap.put("srchStr",
	 * request.getParameter("cmbSearch")); } lInputMap.put("userList",
	 * lInputMap.get("userList")); if (!dataList.isEmpty()) {
	 * lInputMap.put("savedBills", dataList); lInputMap.put("ListSize",
	 * dataList.size()); } CommonPensionDAOImpl lObjCommonPensionDao = new
	 * CommonPensionDAOImpl(serv.getSessionFactory()); List lLstAuditors =
	 * lObjCommonPensionDao
	 * .getAuditorsForCaseAndBill(SessionHelper.getLocationCode(lInputMap),
	 * "210003", "50", SessionHelper.getLangId(lInputMap));
	 * 
	 * lInputMap.put("LstAuditors", lLstAuditors); if
	 * (lInputMap.get("savedBillsStatus") != null &&
	 * Integer.parseInt(lInputMap.get("savedBillsStatus").toString()) == 10) {
	 * // lObjResult.setViewName("savedPensionBillsCntr"); lstrViewName =
	 * "savedPensionBillsCntr"; } else { //
	 * lObjResult.setViewName("savedPensionBills"); lstrViewName =
	 * "savedPensionBills"; } getPensionHierarchyUsers(lInputMap,
	 * lStrSubjectRegular); lObjResult.setResultValue(lInputMap);
	 * lObjResult.setViewName(lstrViewName); } catch(HierarchyNotFoundException
	 * e) { gLogger.error(" Error occured while getting Hierarchy ..." + e, e);
	 * lInputMap.put("ErrorString", "Error occured while getting Hierarchy");
	 * lObjResult.setResultValue(lInputMap);
	 * 
	 * resObj.setThrowable(e); resObj.setResultCode(ErrorConstants.ERROR);
	 * 
	 * lObjResult.setViewName(lstrViewName); }
	 * catch(AlternateHierarchyNotfoundException e) {
	 * gLogger.error(" Error occured while getting alternate hierarchy ..." + e,
	 * e); lInputMap.put("ErrorString",
	 * "Error occured while getting alternate Hierarchy");
	 * lObjResult.setResultValue(lInputMap);
	 * 
	 * resObj.setThrowable(e); resObj.setResultCode(ErrorConstants.ERROR);
	 * 
	 * lObjResult.setViewName(lstrViewName); } catch(UpperPostNotFoundException
	 * e) { gLogger.error(" Error occured while getting upper post... " + e, e);
	 * lInputMap.put("ErrorString",
	 * " Error occured while getting upper post... ");
	 * lObjResult.setResultValue(lInputMap);
	 * 
	 * resObj.setThrowable(e); resObj.setResultCode(ErrorConstants.ERROR);
	 * 
	 * lObjResult.setViewName(lstrViewName); } catch(Exception e) {
	 * gLogger.error("Error is;" + e, e); lObjResult.setResultValue(null);
	 * lObjResult.setThrowable(e);
	 * lObjResult.setResultCode(ErrorConstants.ERROR);
	 * lObjResult.setViewName("errorPage"); } return lObjResult; }
	 */
	public void getPensionHierarchyUsers(Map<String, Object> objectArgs, String lStrSubjectRegular) throws Exception {

		int llFromLevelId = 0;
		String lStrFromLevel = "";
		List rsltLstReg = null;
		List nillUsrList = null;
		List listSameLvlUser = null;
		List nillBillUsrLsit = null;
		Long lLngHeirRefId = null;
		String MRFlag = "";
		String lStrMRFlowFlag = null;
		// String lStrSubjectRegular =
		// bundleConst.getString("OnlineBillSubject");
		ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		try {
			setSessionInfo(objectArgs);

			lLngHeirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gLngPostId.toString(), lStrSubjectRegular, objectArgs);
			if (request.getParameter("caseFromlevel") != null) {
				lStrFromLevel = request.getParameter("caseFromlevel").toString();
			} else if (request.getParameter("BillFromlevel") != null) {
				lStrFromLevel = request.getParameter("BillFromlevel").toString();
			} else if (request.getParameter("MRRequestFromlevel") != null) {
				lStrFromLevel = request.getParameter("MRRequestFromlevel").toString();
			} else if (objectArgs.get("caseFromlevel") != null) {
				lStrFromLevel = objectArgs.get("caseFromlevel").toString();
			}
			if (lStrFromLevel.length() > 0) {
				llFromLevelId = Integer.parseInt(lStrFromLevel);
			}
			int tempCndntn = 0;
			if (request.getParameter("AprvdBills") != null && request.getParameter("AprvdBills").equals("Y")) {
				rsltLstReg = WorkFlowHelper.getToNodeListFromAlterHir(gLngPostId.toString(), lLngHeirRefId, "Approve", objectArgs);
				llFromLevelId = 70;
				Long lLngNillBillHirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gLngPostId.toString(), "Nill PensionBill Processing", objectArgs);

				nillUsrList = WorkFlowHelper.getToNodeListFromAlterHir(gLngPostId.toString(), lLngNillBillHirRefId, "Approve", objectArgs);
				nillBillUsrLsit = getUserList(nillUsrList, serv, objectArgs, lStrSubjectRegular);
				objectArgs.put("AprvdBills", request.getParameter("AprvdBills").toString());
			} else {
				if (request.getParameter("MRRequestFromlevel") != null) // for
				// MR
				{
					PensionBillDAOImpl lObjPensnBilldao = new PensionBillDAOImpl(serv.getSessionFactory());
					List lLstTemp = lObjPensnBilldao.getOtherUsersList(String.valueOf(lStrFromLevel), lLngHeirRefId.toString(), "STOFRWD", gLngPostId.toString(), "MRCASE",
							bundleConst.getString("MR.CASESUBJECT"), lStrMRFlowFlag, gLngLangId);
					String[] result = null;
					Object[] tuple = null;
					listSameLvlUser = new ArrayList();
					for (int i = 0; i < lLstTemp.size(); i++) {
						result = new String[4];
						tuple = (Object[]) lLstTemp.get(i);
						result[0] = (String) tuple[3] + "[" + (String) tuple[1] + "]";
						result[1] = (String) tuple[2];
						result[2] = (String) tuple[0];
						result[3] = "";
						listSameLvlUser.add(result);
					}
					tempCndntn = 1;
				} else {
					if (llFromLevelId >= 50) {
						PensionBillDAOImpl lObjPensnBilldao = new PensionBillDAOImpl(serv.getSessionFactory());
						List lLstTemp = lObjPensnBilldao.getOtherUsersList(String.valueOf(llFromLevelId), lLngHeirRefId.toString(), "STOFRWD", gLngPostId.toString(), "PPO",
								bundleConst.getString("PENSION.BILLSUBJECT"), lStrMRFlowFlag, gLngLangId);
						String[] result = null;
						Object[] tuple = null;
						listSameLvlUser = new ArrayList();
						for (int i = 0; i < lLstTemp.size(); i++) {
							result = new String[4];
							tuple = (Object[]) lLstTemp.get(i);
							result[0] = (String) tuple[3] + "[" + (String) tuple[1] + "]";
							result[1] = (String) tuple[2];
							result[2] = (String) tuple[0];
							result[3] = "";
							listSameLvlUser.add(result);
						}
						tempCndntn = 1;
					} else {
						rsltLstReg = WorkFlowHelper.getUpperPost(gLngPostId.toString(), lLngHeirRefId, llFromLevelId, objectArgs);
					}
				}
			}
			objectArgs.put("svdBillCurLevel", llFromLevelId);
			if (lStrSubjectRegular.equalsIgnoreCase(bundleConst.getString("OnlineBillSubject").toString()) && tempCndntn != 1) {
				listSameLvlUser = getUserList(rsltLstReg, serv, objectArgs, lStrSubjectRegular);
			} else if (tempCndntn != 1) {
				if (request.getParameter("MRRequestFromlevel") != null) {
					MRFlag = "Y";
				} else {
					MRFlag = "N";
				}
				listSameLvlUser = getCaseUsersList(rsltLstReg, serv, objectArgs, MRFlag);
			}
			objectArgs.put("userList", listSameLvlUser);
			objectArgs.put("currentUserPost", gLngPostId.toString());
			if (nillBillUsrLsit != null && !nillBillUsrLsit.isEmpty()) {
				objectArgs.put("NillBillusersList", nillBillUsrLsit);
			}
		} catch (AlternateHierarchyNotfoundException e) {
			gLogger.error(" Error occured while getting alternate hierarchy ..." + e, e);
			throw e;
		} catch (UpperPostNotFoundException e) {
			gLogger.error(" Error occured while getting upper post... " + e, e);
			throw e;
		} catch (Exception e) {
			gLogger.error(" Error occured while getting upper post... " + e, e);
			throw e;
		}
	}

	public ResultObject getSendToOthrs(Map<String, Object> orgsMap) {

		int llFromLevelId = 0;
		ResultObject lObjResult = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrHirarchyRefId = null;
		ServiceLocator serv = (ServiceLocator) orgsMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) orgsMap.get("requestObj");
		PensionBillDAOImpl lObjPensnBilldao = new PensionBillDAOImpl(serv.getSessionFactory());
		String lStrOtherType = null;
		String lStrMRFlowFlag = null;
		try {
			setSessionInfo(orgsMap);
			if (request.getParameter("FrwdType") != null) {
				lStrOtherType = request.getParameter("FrwdType");
			}
			// changes by Soumya
			if (request.getParameter("MRFlag") != null && !request.getParameter("MRFlag").toString().equals("")) {
				lStrMRFlowFlag = request.getParameter("MRFlag");
			}
			// changes by Soumya

			Long lLngHeirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(SessionHelper.getPostId(orgsMap).toString(), bundleConst.getString("PENSION.BILLSUBJECT"), orgsMap);

			lStrHirarchyRefId = lLngHeirRefId.toString();

			String lStrFromLevel = StringUtility.getParameter("FromLevel", request);
			if (lStrFromLevel != null && lStrFromLevel.length() > 0) {
				llFromLevelId = Integer.parseInt(lStrFromLevel);
			}
			if (lStrOtherType != null && lStrOtherType.length() > 0) {
				List lLstTemp = lObjPensnBilldao.getOtherUsersList(String.valueOf(llFromLevelId), lStrHirarchyRefId, lStrOtherType, gLngPostId.toString(), "PPO",
						bundleConst.getString("PENSION.BILLSUBJECT"), lStrMRFlowFlag, gLngLangId);
				if (!lLstTemp.isEmpty()) {
					orgsMap.put("RegList", lLstTemp);
				}
			}
			lObjResult.setResultValue(orgsMap);
			lObjResult.setViewName("cmnCaseSelectionFrwd");
		} catch (Exception e) {
			gLogger.error(" Error occured while getting upper post... " + e, e);
		}
		return lObjResult;
	}

	private List getCaseUsersList(List rsltList, ServiceLocator serv, Map<String, Object> orgsMap, String MRFlag) throws Exception {

		List listUsers = null;
		try {
			PensionBillDAOImpl lObjPnsnCaseDao = new PensionBillDAOImpl(serv.getSessionFactory());
			if (rsltList != null) {
				String[] postString = new String[rsltList.size()];
				String[] levelString = new String[rsltList.size()];
				Object[] lObjNextPost = null;
				for (int i = 0; i < rsltList.size(); i++) {
					lObjNextPost = (Object[]) rsltList.get(i);
					postString[i] = lObjNextPost[0].toString();
					levelString[i] = lObjNextPost[1].toString();
				}
				if (!rsltList.isEmpty()) {
					listUsers = lObjPnsnCaseDao.getCaseUsersLsitgetUserFromPost(postString, levelString, gLngLangId, orgsMap, MRFlag);
				}
			}
		} catch (Exception e) {
			gLogger.error(" Error is " + e, e);
			throw e;
		}
		return listUsers;
	}

	private List getUserList(List rsltList, ServiceLocator serv, Map<String, Object> objectArgs, String description) throws Exception {

		List listUsers = null;
		String MRFlag = "N";
		try {
			if ("PensionBill Processing".equals(description)) {
				MRFlag = "B";
			}
			PensionBillDAOImpl lObjPnsnCaseDao = new PensionBillDAOImpl(serv.getSessionFactory());
			if (rsltList != null) {
				String[] postString = new String[rsltList.size()];
				String[] levelString = new String[rsltList.size()];
				Object[] lObjNextPost = null;
				for (int i = 0; i < rsltList.size(); i++) {
					lObjNextPost = (Object[]) rsltList.get(i);
					postString[i] = lObjNextPost[0].toString();
					levelString[i] = lObjNextPost[1].toString();
				}

				if (!rsltList.isEmpty()) {
					listUsers = lObjPnsnCaseDao.getCaseUsersLsitgetUserFromPost(postString, levelString, gLngLangId, objectArgs, MRFlag);
				}
			}
		} catch (Exception e) {
			gLogger.error(" Error is " + e, e);
			throw e;
		}
		return listUsers;
	}

	/*
	 * public ResultObject forwardPensionBills(Map<String, Object> argsMap) {
	 * 
	 * Map<String, Object> lInputMap = argsMap; ResultObject objRes = new
	 * ResultObject(ErrorConstants.SUCCESS, "FAIL"); HttpServletRequest request
	 * = (HttpServletRequest) lInputMap.get("requestObj"); ServiceLocator serv =
	 * (ServiceLocator) argsMap.get("serviceLocator"); String lStrIsVitoSucced =
	 * "Y"; String lStrMRFlowFlag = null; String lStrMRTokenNo = null;
	 * 
	 * try { if (request.getParameter("AprvdBills") != null &&
	 * request.getParameter("AprvdBills").equalsIgnoreCase("Y")) {
	 * lInputMap.put("AprvdBills", "Y"); } if
	 * (request.getParameter("RejectBills") != null &&
	 * request.getParameter("RejectBills").equalsIgnoreCase("Y")) {
	 * lInputMap.put("RejectBills", "Y"); } if (request.getParameter("isAudit")
	 * != null && request.getParameter("isAudit").equalsIgnoreCase("Y")) {
	 * lInputMap.put("isAudit", "Y"); } if (request.getParameter("isCountr") !=
	 * null && request.getParameter("isCountr").equalsIgnoreCase("Y")) {
	 * lInputMap.put("isCountr", "Y");
	 * validatePensionTokenOnSubmit(argsMap);//TOKEN_MSG KEY OF INPUT MAP IS SET
	 * IN THIS METHOD } if (!lInputMap.containsKey("TOKEN_MSG") &&
	 * request.getParameter("isVito") != null &&
	 * request.getParameter("isVito").toString().equalsIgnoreCase("Y")) { objRes
	 * = generatePensionVito(lInputMap); lInputMap = (Map)
	 * objRes.getResultValue(); if (lInputMap.get("vitoMessage") == null) {
	 * lStrIsVitoSucced = "N"; } }
	 * 
	 * // changes by Soumya if (request.getParameter("MRFlag") != null &&
	 * !request.getParameter("MRFlag").toString().equals("")) { lStrMRFlowFlag =
	 * request.getParameter("MRFlag"); lStrMRTokenNo =
	 * request.getParameter("MRTokenNo");
	 * 
	 * String[] lStrBillAndTokenNo = lStrMRTokenNo.split("~"); String[]
	 * lStrBillAndTokenNosplit = null;
	 * 
	 * OrgTokenStatusDAO lObjTokenStatusDAO = new
	 * OrgTokenStatusDAOImpl(OrgTokenStatus.class, serv.getSessionFactory());
	 * 
	 * if (request.getParameterValues("chkbox") != null) { String[] billsNo =
	 * request.getParameterValues("chkbox"); //String[] chk = null; // String
	 * lStrBillNo = null; ReportServiceImpl lObjReport = new
	 * ReportServiceImpl();
	 * 
	 * for (int i = 0; i < billsNo.length; i++) { lStrBillAndTokenNosplit =
	 * lStrBillAndTokenNo[i].split("/");
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * lObjTokenStatusDAO.updateTokenStatus(Long.parseLong(lStrBillAndTokenNosplit
	 * [1]), SessionHelper.getLocationCode(argsMap),
	 * Long.parseLong(lStrBillAndTokenNosplit[0]),
	 * SessionHelper.getUserId(argsMap), SessionHelper.getPostId(argsMap),
	 * BillProcConstants.INWARD_TYPE_BILL);
	 * 
	 * lObjReport.updateTokenIssue(argsMap, lStrBillAndTokenNosplit[1],
	 * Long.parseLong(lStrBillAndTokenNosplit[0]),
	 * DBUtility.getCurrentDateFromDB(), null, "B"); } } } // changes by Soumya
	 * 
	 * lInputMap.put("MRFlag", lStrMRFlowFlag); lInputMap.put("MRTokenNo",
	 * lStrMRTokenNo);
	 * 
	 * serv.getSessionFactory().getCurrentSession().flush(); objRes =
	 * getSavedPensionBills(lInputMap); Map temp = (Map)
	 * objRes.getResultValue(); objRes.setResultValue(temp); } catch(Exception
	 * e) { IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, e,
	 * "Error is \n "); } return objRes; }
	 */

	public ResultObject generatePensionVito(Map<String, Object> objectArgs) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
		try {
			setSessionInfo(objectArgs);
			new ReportServiceImpl();
			String vitoCode = "";
			String vitoMessage = "";
			String printString = "";
			Map printMap = new HashMap();
			printMap.put("vitoCode", "1000000");
			if (request.getParameter("forAudit") != null) {
				if ("Y".equalsIgnoreCase(request.getParameter("forAudit").toString())) {

					vitoCode = (String) printMap.get("vitoCode");
					if (vitoCode != null) {
						vitoMessage = "Vito Code " + vitoCode + " Generated Successfully";
						printString = (String) printMap.get("printString");
					}
				}
			} else {
				vitoCode = (String) printMap.get("vitoCode");
				if (vitoCode != null) {
					vitoMessage = "Vito Code " + vitoCode + " Generated Successfully";
					printString = (String) printMap.get("printString");
				}
			}
			objectArgs.put("vitoMessage", vitoMessage);
			objectArgs.put("printString", printString);
			objRes.setResultValue(objectArgs);
		} catch (Exception e) {
			gLogger.error("Error is" + e);
			throw e;
		}
		return objRes;
	}

	public short getStatusByLevel(Short lShrtlevel) {

		short lShortUpdStatus = Short.valueOf("-1");
		if (lShrtlevel == 20) {
			lShortUpdStatus = DBConstants.ST_BAPRVD_DDO;
		} else if (lShrtlevel == 30) {
			lShortUpdStatus = DBConstants.ST_BS_TO;
		} else if (lShrtlevel == 40) {
			lShortUpdStatus = DBConstants.ST_BCRDX;
		} else if (lShrtlevel == 50) {
			lShortUpdStatus = DBConstants.ST_BAUD;
		} else if (lShrtlevel == 60) {
			lShortUpdStatus = DBConstants.ST_BAUD_ACC;
		}

		else if (lShrtlevel == 70) {
			lShortUpdStatus = DBConstants.ST_BAUD_ATO;
		} else if (lShrtlevel == 80) {
			lShortUpdStatus = DBConstants.ST_BAUD_TO;
		} else if (lShrtlevel == 90) {
			lShortUpdStatus = DBConstants.ST_CHQ_WRTN;
		} else if (lShrtlevel == 100) {
			lShortUpdStatus = DBConstants.ST_CHQ_PRNT;
		} else if (lShrtlevel == 110) {
			lShortUpdStatus = DBConstants.ST_CHQ_CSTDN;
		} else if (lShrtlevel == 120) {
			lShortUpdStatus = DBConstants.ST_CHQ_CNTR;
		} else if (lShrtlevel == 130) {
			lShortUpdStatus = DBConstants.ST_CHQ_DSPTCH_DDO;
		} else if (lShrtlevel == 140) {
			lShortUpdStatus = DBConstants.ST_VCHR_GEN;
		}
		return lShortUpdStatus;
	}

	private void pensionBillBulkForwardMethod(Map objectArgs, List lLstWFMap, String toPostId, String toUserId, String toLevel) throws Exception {

		try {
			objectArgs.put("bulkForwardToPost", toPostId);
			objectArgs.put("bulkForwardToLevel", toLevel);
			objectArgs.put("bulkForwardToUser", toUserId);
			objectArgs.put("forwardJobList", lLstWFMap);
			objectArgs.remove("groupStatus");
			WorkFlowDelegate.bulkForwardToPost(objectArgs);
		} catch (Exception e) {
			gLogger.error("Error is " + e, e);
			throw e;
		}
	}

	/*
	 * public ResultObject forwardBillFromWF(Map<String, Object> inputMap) {
	 * ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
	 * ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
	 * HttpServletRequest request = (HttpServletRequest)
	 * inputMap.get("requestObj"); try { setSessionInfo(inputMap); String
	 * lStrBillNum = (String) inputMap.get("jobId"); TrnBillMvmnt
	 * lObjBillMvmntVO = new TrnBillMvmnt(); BillMovementServiceImpl
	 * lObjBillMvmntSrvcImpl = new BillMovementServiceImpl(); lObjBillMvmntVO =
	 * lObjBillMvmntSrvcImpl.getMovementInstance((TrnBillMvmnt)
	 * inputMap.get("BillMovementVO"), inputMap); BptmCommonServicesDAOImpl
	 * lObjCmnSrvcDAOImpl = new BptmCommonServicesDAOImpl(TrnBillRegister.class,
	 * serv.getSessionFactory()); long lDocId = 0; lDocId =
	 * Long.parseLong(bundleConst.getString("WF.DocId.PensionBill")); int
	 * lIntLevelId = 0; String toPostId = ""; if (inputMap.get("toPost") != null
	 * && (!inputMap.get("toPost").equals("-1"))) { toPostId = (String)
	 * inputMap.get("toPost"); String toLevelId = (String)
	 * inputMap.get("toLevel");
	 * lObjBillMvmntVO.setStatusUpdtPostid(Long.parseLong(toPostId));
	 * lObjBillMvmntVO
	 * .setStatusUpdtUserid(Long.parseLong(lObjCmnSrvcDAOImpl.getUserIdFromPost
	 * (toPostId, gLngLangId)));
	 * 
	 * if (toLevelId != null && !toLevelId.equals("")) { lIntLevelId =
	 * Integer.parseInt(toLevelId); } else { List jobMstList =
	 * WorkFlowHelper.getJobMstByJobRefID(lStrBillNum, lDocId,
	 * DBConstants.WF_ORDINARY_DOC_TYPE, inputMap); if (jobMstList != null &&
	 * !jobMstList.isEmpty()) { WfJobMstVO jobMstVO = (WfJobMstVO)
	 * jobMstList.get(0); List resultList =
	 * WorkFlowHelper.getUpperPost(gLngPostId.toString(),
	 * jobMstVO.getHierachyRefId(), jobMstVO.getLevelId(), inputMap); if
	 * (!resultList.isEmpty()) { Object[] obj = (Object[]) resultList.get(0);
	 * toLevelId = obj[1].toString(); lIntLevelId = Integer.parseInt(toLevelId);
	 * } } jobMstList = null; } } String CommonPolFlag = "N";
	 * if(inputMap.containsKey("CommonPolFlag")) { CommonPolFlag = (String)
	 * inputMap.get("CommonPolFlag"); } List lLstToRegPost = new ArrayList();
	 * String lStrToPost = ""; if("Y".equalsIgnoreCase(CommonPolFlag)) {
	 * inputMap.put("BillMovementVO", lObjBillMvmntVO); inputMap.put("jobRefId",
	 * Long.valueOf(lStrBillNum)); inputMap.put("Pkvalue",
	 * Long.valueOf(lStrBillNum)); inputMap.put("docId", Long.valueOf(lDocId));
	 * inputMap.put("Docid", Long.valueOf(lDocId)); inputMap.put("toPostId",
	 * toPostId); inputMap.put("jobTitle", "Bill"); lStrToPost =
	 * (String)inputMap.get("toPostList"); for(int
	 * i=0;i<lStrToPost.split("~").length;i++) {
	 * lLstToRegPost.add(lStrToPost.split("~")[i]); } inputMap.put("toLevel",
	 * lIntLevelId); if(lLstToRegPost.size() >1 ) {
	 * inputMap.put("groupPostList", lLstToRegPost);
	 * AssignToGroupHandler.assignItemToUserGroup(inputMap);
	 * BillMovementServiceImpl lObjBillserviceImpl = new
	 * BillMovementServiceImpl();
	 * lObjBillserviceImpl.insertBillMovement(inputMap); } else {
	 * WorkFlowDelegate.forward(inputMap); } } else {
	 * inputMap.put("BillMovementVO", lObjBillMvmntVO); inputMap.put("Pkvalue",
	 * Long.valueOf(lStrBillNum)); inputMap.put("toPostId", toPostId);
	 * inputMap.put("jobTitle", "OnlineBill"); inputMap.put("Docid",
	 * Long.valueOf(lDocId)); inputMap.put("toLevel", lIntLevelId);
	 * WorkFlowDelegate.forward(inputMap); }
	 * 
	 * 
	 * inputMap.put("MESSAGECODE", (long) 1038);
	 * objRes.setResultValue(inputMap); objRes.setViewName("ajaxData");
	 * 
	 * } catch(Exception ex) { gLogger.error("Error is;" + ex, ex);
	 * objRes.setResultValue(null); objRes.setThrowable(ex);
	 * objRes.setResultCode(ErrorConstants.ERROR);
	 * objRes.setViewName("errorPage"); } return objRes; }
	 */

	private String getToPostAndToLvl(String reqParaToPost) throws Exception {

		String toPost = null;
		String toLevel = null;
		String returnStr = null;

		try {
			if (reqParaToPost != null && !reqParaToPost.equals("-1")) {
				String[] lArray = reqParaToPost.split("~");
				toPost = lArray[0];

				if (lArray.length == 1) {
					toLevel = "-1";
				} else {
					toLevel = lArray[1];
				}
				returnStr = toPost + "~" + toLevel;
			}
		} catch (Exception e) {
			gLogger.error("Error is " + e, e);
			throw e;
		}
		return returnStr;
	}

	/**
	 * Split a Consolidated Bill for each HeadCode. Method is Call when bill
	 * send from SendToBook Worklist.
	 * 
	 * @param inputMap
	 * @author Sagar
	 * @return {@link ResultObject}
	 */
	/*
	 * public void splitConsolidatedBill(Map<String, Object> inputMap,
	 * List<TrnBillRegister> lstPensionBillVOs, List<Long> lListPensionBillNo)
	 * throws Exception {
	 * 
	 * ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
	 * 
	 * //TrnBillRegister lbillRegisterVO = null; TrnBillRegister
	 * lNewBillRegister = null; TrnBillEdpDtls lNewBillEdpDtls = null;
	 * List<TrnPensionBillReceipt> lBillRcptDtlsLst = null;
	 * List<TrnPensionBillHdr> lPnsnBillHdrLst = null;
	 * RltPensionHeadcodeChargable lHeadcodeChargable = null; Long
	 * StartbillEdpNoIDRcpt =null;
	 * 
	 * List<Long> lOLDBillNo = new ArrayList<Long>();
	 * 
	 * String lStrHeadCode = null; String lStrBillType = null;
	 * 
	 * TrnPensionBillHdrDAO lObjBillHdrDAO = new
	 * TrnPensionBillHdrDAOImpl(TrnPensionBillHdr.class,
	 * serv.getSessionFactory()); TrnPensionBillReceiptDAO lObjReceiptDAO = new
	 * TrnPensionBillReceiptDAOImpl(TrnPensionBillReceipt.class,
	 * serv.getSessionFactory()); TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO
	 * = new
	 * TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class,serv.getSessionFactory
	 * ());
	 * 
	 * MonthlyPensionBillDAO lPensionBillDAO = new
	 * MonthlyPensionBillDAOImpl(MstPensionerHdr.class,
	 * serv.getSessionFactory());
	 * 
	 * HibernateTemplate hibTemplate = new
	 * HibernateTemplate(serv.getSessionFactory()); IFMSCommonDAO commonDAO =
	 * new IFMSCommonDAOImpl(serv.getSessionFactory()); Map<String, Object>
	 * mapParam = new HashMap<String, Object>();
	 * 
	 * mapParam.put("BILL_LIST", lListPensionBillNo); List<Object[]> lstResult =
	 * commonDAO.findByNamedQuery("tbp.billchq.getBillNoAndChqIdByBillNos",
	 * mapParam);
	 * 
	 * Map<Long, Long> mapBillChq = new HashMap<Long, Long>(); for(Object[]
	 * objArr : lstResult){ mapBillChq.put((Long) objArr[0], (Long) objArr[1]);
	 * }
	 * 
	 * List lConsldtBillHdrLst = null; Map<Long, Object> mapBillHdr = new
	 * HashMap<Long, Object>(); Set<Long> lHeadCodeSet = new HashSet<Long>();
	 * Map<String,Object> lMapHdChagble = new HashMap<String, Object>();
	 * Map<Long, Object> mapBillReceipt = new HashMap<Long, Object>();
	 * 
	 * Session hibSession = serv.getSessionFactory().getCurrentSession();
	 * 
	 * try { setSessionInfo(inputMap);
	 * 
	 * lConsldtBillHdrLst =
	 * lPensionBillDAO.getConsolidatedBillHdrDtls(lListPensionBillNo);
	 * 
	 * if(lConsldtBillHdrLst != null && !lConsldtBillHdrLst.isEmpty()) { Long
	 * lStrOldBillNo = null; Long lStrNewBillNo = null;
	 * 
	 * List<TrnPensionBillHdr> BillOuterLst = null;
	 * 
	 * for (int i = 0; i < lConsldtBillHdrLst.size(); i++) { TrnPensionBillHdr
	 * lBillHdrVo = (TrnPensionBillHdr) lConsldtBillHdrLst.get(i);
	 * 
	 * lHeadCodeSet.add(lBillHdrVo.getHeadCode().longValue()); lStrNewBillNo =
	 * lBillHdrVo.getBillNo();
	 * 
	 * if (i == 0) { BillOuterLst = new ArrayList<TrnPensionBillHdr>(); } else
	 * if (!(lStrOldBillNo.equals(lStrNewBillNo))) {
	 * mapBillHdr.put(lStrOldBillNo, BillOuterLst); BillOuterLst = new
	 * ArrayList<TrnPensionBillHdr>(); }
	 * 
	 * BillOuterLst.add(lBillHdrVo); lStrOldBillNo = lBillHdrVo.getBillNo();
	 * 
	 * if (i == ((lConsldtBillHdrLst.size()) - 1)) {
	 * mapBillHdr.put(lStrOldBillNo, BillOuterLst); } } }
	 * 
	 * Long StartbillNoID =
	 * IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_bill_register",
	 * inputMap, lConsldtBillHdrLst.size()); Long StartbillEdpNoID =
	 * IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_bill_edp_dtls",
	 * inputMap, lConsldtBillHdrLst.size()); Long StartbillCheqNoID =
	 * IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("rlt_bill_cheque",
	 * inputMap, lConsldtBillHdrLst.size());
	 * 
	 * // Getting One times HeadChargable and put it in map. lMapHdChagble =
	 * lPensionBillDAO.getRltPensionHeadcodeChargable(lHeadCodeSet);
	 * mapBillReceipt =
	 * lObjReceiptDAO.getMapOfRcptDtlFromHdrIds(lListPensionBillNo,gStrLocId);
	 * 
	 * for(TrnBillRegister lbillRegisterVO : lstPensionBillVOs) {
	 * 
	 * if( (lbillRegisterVO.getDeductionA() != null &&
	 * lbillRegisterVO.getDeductionA().longValue() > 0L ) ||
	 * (lbillRegisterVO.getDeductionB() != null &&
	 * lbillRegisterVO.getDeductionB().longValue() > 0L ) ) { mapBillReceipt =
	 * lObjReceiptDAO.getMapOfRcptDtlFromHdrIds(lListPensionBillNo,gStrLocId); }
	 * 
	 * 
	 * Long lngChqId = mapBillChq.get(lbillRegisterVO.getBillNo());
	 * 
	 * //lbillRegisterVO = (TrnBillRegister) inputMap.get("billRegisterVO");
	 * lOLDBillNo.add(lbillRegisterVO.getBillNo());
	 * 
	 * //lPnsnBillHdrLst = lPensionBillDAO.getTrnPensionBillHdr(lOLDBillNo);
	 * lPnsnBillHdrLst = (List) mapBillHdr.get(lbillRegisterVO.getBillNo());
	 * 
	 * List<TrnBillRegister> lLstNewTrnBillRegister = new
	 * ArrayList<TrnBillRegister>(); List<TrnPensionBillHdr>
	 * lLstTrnPensionBillHdr = new ArrayList<TrnPensionBillHdr>();
	 * List<TrnBillEdpDtls> lLstTrnBillEdpDtls = new
	 * ArrayList<TrnBillEdpDtls>(); List<TrnBillEdpDtls> lLstTrnBillEdpDtlsInner
	 * = null;
	 * 
	 * for (TrnPensionBillHdr lBillHdrVo : lPnsnBillHdrLst) { // Save bills for
	 * each Headcode into TrnBillRegister.
	 * 
	 * lNewBillRegister = getNewBillRegisterVo(lbillRegisterVO);
	 * 
	 * // Getting a Head Structure For the Bill. lStrHeadCode =
	 * lBillHdrVo.getHeadCode().toString(); lStrBillType =
	 * lBillHdrVo.getBillType();
	 * 
	 * //lHeadcodeChargable =
	 * lPensionBillDAO.getRltPensionHeadcodeChargable(lStrHeadCode
	 * ,lStrBillType);
	 * 
	 * String lTempBillType = ("PENSION".equalsIgnoreCase(lStrBillType)) ?
	 * "Monthly" : lStrBillType; lHeadcodeChargable =
	 * (RltPensionHeadcodeChargable)lMapHdChagble.get(lStrHeadCode+"~"+
	 * lTempBillType);
	 * 
	 * Long lNewBillNo =
	 * IFMSCommonServiceImpl.getFormattedPrimaryKey(++StartbillNoID, inputMap);
	 * lNewBillRegister.setBillNo(lNewBillNo);
	 * 
	 * if ("CVP".equalsIgnoreCase(lStrBillType)) {
	 * lNewBillRegister.setSubjectId(Long.valueOf(PensionConstants.CVP_Id)); }
	 * else if ("DCRG".equalsIgnoreCase(lStrBillType)) {
	 * lNewBillRegister.setSubjectId(Long.valueOf(PensionConstants.DCRG_Id)); }
	 * else if ("PENSION".equalsIgnoreCase(lStrBillType)) {
	 * lNewBillRegister.setSubjectId(Long.valueOf(PensionConstants.PENSION_Id));
	 * }
	 * 
	 * lNewBillRegister.setDemandCode(lHeadcodeChargable.getDemandNo()); //
	 * DemandNo lNewBillRegister.setBudmjrHd(lHeadcodeChargable.getMjrhdCode());
	 * // MjrHd Code
	 * lNewBillRegister.setBudSubmjrHd(lHeadcodeChargable.getSubmjrhdCode()); //
	 * SubmjrHd Code
	 * lNewBillRegister.setBudMinHd(lHeadcodeChargable.getMinhdCode()); // MinHd
	 * Code lNewBillRegister.setBudSubHd(lHeadcodeChargable.getSubhdCode()); //
	 * SubHd Code
	 * lNewBillRegister.setBudDtlHd(lHeadcodeChargable.getDtlhdCode()); // DtlHd
	 * Code String lStrheadChrg = lHeadcodeChargable.getMjrhdCode() +
	 * lHeadcodeChargable.getSubmjrhdCode() + lHeadcodeChargable.getMinhdCode()
	 * + lHeadcodeChargable.getSubhdCode();
	 * lNewBillRegister.setBillGrossAmount(lBillHdrVo.getGrossAmount()); //
	 * Gross Amount lNewBillRegister.setDeductionA(lBillHdrVo.getDeductionA());
	 * // Deduction A
	 * lNewBillRegister.setDeductionB(lBillHdrVo.getDeductionB()); // Deduction
	 * B lNewBillRegister.setBillNetAmount(lBillHdrVo.getNetAmount()); // Net
	 * Amount
	 * 
	 * // voucher Entry.
	 * lNewBillRegister.setCurrBillStatus(DBConstants.ST_VCHR_GEN);
	 * lNewBillRegister.setCurrBillStatusDate(gDtCurDate);
	 * lNewBillRegister.setReceivedFlag(Short.valueOf("1"));
	 * lNewBillRegister.setIsUpdated(new Short("0"));
	 * 
	 * //phyBill.create(lNewBillRegister);
	 * //lLstNewTrnBillRegister.add(lNewBillRegister);
	 * hibSession.save(lNewBillRegister);
	 * 
	 * // Update New Bill into TrnPensionBillHdr Table.
	 * 
	 * lBillHdrVo.setBookBillNo(lNewBillNo);
	 * lBillHdrVo.setBookToken(lbillRegisterVO.getTokenNum() + "-" +
	 * lBillHdrVo.getHeadCode()); lBillHdrVo.setUpdatedUserId(new
	 * BigDecimal(gLngUserId)); lBillHdrVo.setUpdatedPostId(new
	 * BigDecimal(gLngPostId)); lBillHdrVo.setUpdatedDate(gDtCurDate);
	 * 
	 * //lObjBillHdrDAO.update(lBillHdrVo);
	 * //lLstTrnPensionBillHdr.add(lBillHdrVo); hibSession.save(lBillHdrVo);
	 * 
	 * // Save Edp Details for each headcode into TrnBillEdpDtls.
	 * 
	 * lNewBillEdpDtls = new TrnBillEdpDtls();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * lNewBillEdpDtls.setBillEdpId(IFMSCommonServiceImpl.getFormattedPrimaryKey(
	 * ++StartbillEdpNoID, inputMap));
	 * lNewBillEdpDtls.setBillNo(lNewBillRegister.getBillNo());
	 * lNewBillEdpDtls.setTypeEdpId(0L);
	 * lNewBillEdpDtls.setEdpAmt(lBillHdrVo.getGrossAmount());
	 * lNewBillEdpDtls.setExpRcpRec(DBConstants.EDP_EXP);
	 * lNewBillEdpDtls.setEdpCode(lHeadcodeChargable.getEdpCode());
	 * lNewBillEdpDtls.setAutoAdd("Y");
	 * lNewBillEdpDtls.setLocationCode(gStrLocId);
	 * lNewBillEdpDtls.setCreatedUserId(gLngUserId);
	 * lNewBillEdpDtls.setCreatedPostId(gLngPostId);
	 * lNewBillEdpDtls.setCreatedDate(gDtCurDate);
	 * lNewBillEdpDtls.setDbId(gLngDBId); lNewBillEdpDtls.setTrnCounter(1);
	 * 
	 * //New Code Soumya
	 * lNewBillEdpDtls.setBudobjhdCode(lObjTrnPensionRqstHdrDAO
	 * .getMstEdpVO(lHeadcodeChargable.getEdpCode(), gLngLangId,
	 * SessionHelper.getFinYrId(inputMap)).getBudobjhdCode());
	 * 
	 * //billEdpImpl.create(lNewBillEdpDtls);
	 * //lLstTrnBillEdpDtls.add(lNewBillEdpDtls);
	 * hibSession.save(lNewBillEdpDtls);
	 * 
	 * // Generate RltBillCheque Relation's for new bills with dummy bill
	 * Cheque_Id.
	 * 
	 * if (lNewBillRegister.getTcBill().equals(DBConstants.CAT_Regular_BILL)) {}
	 * 
	 * // insert into DSS Data for expenditure into rpt_expenditure_dtls
	 * inputMap.put("BillRegVO", lNewBillRegister);
	 * inputMap.put("PenionBillHdrVo", lBillHdrVo);
	 * 
	 * if (lNewBillRegister.getDemandCode().equalsIgnoreCase("999")) {
	 * inputMap.put("case", "999"); inputMap.put("999_EdpCode",
	 * lHeadcodeChargable.getEdpCode());
	 * //lObjReceiptDtlsDAO.create(lRptReceiptDtlsVO);
	 * lLstRptReceiptDtls.add(getRptRcptDtlsVO(inputMap,null));
	 * //hibSession.save(getRptRcptDtlsVO(inputMap,null)); } // Entry in
	 * Rpt_Expenditure_Dtls done only when demand is other than 999 else {
	 * //lObjExpenditureDtlsDAO.create(lObjRptHdr);
	 * lLstRptExpenditureDtls.add(getRptExpDtlsVO(inputMap));
	 * //hibSession.save(getRptExpDtlsVO(inputMap)); }
	 * 
	 * // Save Receipt Details for Each Headcode into TrnEdpDtls.
	 * 
	 * if ((lBillHdrVo.getDeductionA() != null &&
	 * lBillHdrVo.getDeductionA().longValue() > 0) ||
	 * (lBillHdrVo.getDeductionB() != null &&
	 * lBillHdrVo.getDeductionB().longValue() > 0)) { //lBillRcptDtlsLst =
	 * lObjReceiptDAO.getRcptDtlFromHdrId(lBillHdrVo.getTrnPensionBillHdrId());
	 * lBillRcptDtlsLst = (List<TrnPensionBillReceipt>)
	 * mapBillReceipt.get(lBillHdrVo.getTrnPensionBillHdrId());
	 * 
	 * if (lBillRcptDtlsLst != null && !lBillRcptDtlsLst.isEmpty()) {
	 * StartbillEdpNoIDRcpt =
	 * IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("trn_bill_edp_dtls",
	 * inputMap, lBillRcptDtlsLst.size()); lLstTrnBillEdpDtlsInner = new
	 * ArrayList<TrnBillEdpDtls>(); lLstRptReceiptDtlsInner = new
	 * ArrayList<RptReceiptDtls>();
	 * 
	 * for (TrnPensionBillReceipt lBillReceipt : lBillRcptDtlsLst) {
	 * 
	 * lNewBillEdpDtls = new TrnBillEdpDtls();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * lNewBillEdpDtls.setBillEdpId(IFMSCommonServiceImpl.getFormattedPrimaryKey(
	 * ++StartbillEdpNoIDRcpt, inputMap));
	 * lNewBillEdpDtls.setBillNo(lNewBillRegister.getBillNo());
	 * lNewBillEdpDtls.setTypeEdpId(0L);
	 * lNewBillEdpDtls.setEdpAmt(lBillReceipt.getAmount());
	 * lNewBillEdpDtls.setAddDedFlag("-");
	 * lNewBillEdpDtls.setEdpCategory(lBillReceipt.getDedType());
	 * lNewBillEdpDtls.setExpRcpRec(DBConstants.EDP_RCP);
	 * lNewBillEdpDtls.setEdpCode(lBillReceipt.getEdpCode());
	 * lNewBillEdpDtls.setAutoAdd("N");
	 * lNewBillEdpDtls.setLocationCode(gStrLocId);
	 * lNewBillEdpDtls.setBudmjrhdCode(lBillReceipt.getMajorHead());
	 * lNewBillEdpDtls.setBudsubmjrhdCode(lBillReceipt.getSubmajorHead());
	 * lNewBillEdpDtls.setBudminhdCode(lBillReceipt.getMinorHead());
	 * lNewBillEdpDtls.setBudsubhdCode(lBillReceipt.getSubhead());
	 * lNewBillEdpDtls.setBuddtlhdCode(lBillReceipt.getEdpCode());
	 * 
	 * //New Code Soumya
	 * lNewBillEdpDtls.setBudobjhdCode(lObjTrnPensionRqstHdrDAO
	 * .getMstEdpVO(lBillReceipt.getEdpCode(),
	 * gLngLangId,SessionHelper.getFinYrId(inputMap)).getBudobjhdCode());
	 * 
	 * lNewBillEdpDtls.setCreatedUserId(gLngUserId);
	 * lNewBillEdpDtls.setCreatedPostId(gLngPostId);
	 * lNewBillEdpDtls.setCreatedDate(gDtCurDate);
	 * lNewBillEdpDtls.setDbId(gLngDBId); lNewBillEdpDtls.setTrnCounter(1);
	 * 
	 * //billEdpImpl.create(lNewBillEdpDtls);
	 * //lLstTrnBillEdpDtlsInner.add(lNewBillEdpDtls);
	 * hibSession.save(lNewBillEdpDtls);
	 * 
	 * inputMap.put("BillReceipt", lBillReceipt);
	 * 
	 * inputMap.remove("case"); //lObjReceiptDtlsDAO.create(lRptReceiptDtlsVO);
	 * lLstRptReceiptDtls.add(getRptRcptDtlsVO(inputMap,lNewBillEdpDtls));
	 * //hibSession.save(getRptRcptDtlsVO(inputMap,lNewBillEdpDtls)); }
	 * if(!lLstTrnBillEdpDtlsInner.isEmpty() && lLstTrnBillEdpDtlsInner.size() >
	 * 0) hibTemplate.saveOrUpdateAll(lLstTrnBillEdpDtlsInner);
	 * if(!lLstRptReceiptDtlsInner.isEmpty() && lLstRptReceiptDtlsInner.size() >
	 * 0) hibTemplate.saveOrUpdateAll(lLstRptReceiptDtlsInner); } }
	 * 
	 * // save HeadCode wise bill into workflow Job mst. WorkFlowVO workFlowVO =
	 * new WorkFlowVO();
	 * 
	 * inputMap.put("isConsolidatedBill", "Y");
	 * inputMap.put("ConsolidateBillNo",
	 * String.valueOf(lNewBillRegister.getBillNo()));
	 * 
	 * workFlowVO = WorkFlowHelper.getDefaultWorkFlowVO(inputMap);
	 * 
	 * workFlowVO.setCrtEmpId(gLngUserId + "");
	 * workFlowVO.setFromPost(gLngPostId + ""); workFlowVO.setCrtUsr(gLngUserId
	 * + "");
	 * 
	 * workFlowVO.setCrtPost(inputMap.get("toPostId") + "");
	 * workFlowVO.setActId(
	 * Long.parseLong(bundleConst.getString("WF.ActionId.Create"))); // Id
	 * workFlowVO
	 * .setDocId(Long.parseLong(bundleConst.getString("WF.DocId.PensionBill")));
	 * // Id
	 * workFlowVO.setJobRefId(String.valueOf(lNewBillRegister.getBillNo())); //
	 * Default value for new bill workFlowVO.setJobTitle("OnlineBill");
	 * workFlowVO.setLangID(gLngLangId + "");
	 * workFlowVO.setHirRefId(lbillRegisterVO.getHierarchyRefId());
	 * WorkFlowUtility wfUtility = new WorkFlowUtility();
	 * wfUtility.invokeWorkFlow(workFlowVO);
	 * 
	 * }
	 * 
	 * if(!lLstNewTrnBillRegister.isEmpty() && lLstNewTrnBillRegister.size() >
	 * 0) hibTemplate.saveOrUpdateAll(lLstNewTrnBillRegister);
	 * if(!lLstTrnPensionBillHdr.isEmpty() && lLstTrnPensionBillHdr.size() > 0)
	 * hibTemplate.saveOrUpdateAll(lLstTrnPensionBillHdr);
	 * if(!lLstTrnBillEdpDtls.isEmpty() && lLstTrnBillEdpDtls.size() > 0)
	 * hibTemplate.saveOrUpdateAll(lLstTrnBillEdpDtls);
	 * if(!lLstRltBillCheque.isEmpty() && lLstRltBillCheque.size() > 0)
	 * hibTemplate.saveOrUpdateAll(lLstRltBillCheque);
	 * if(!lLstRptReceiptDtls.isEmpty() && lLstRptReceiptDtls.size() > 0)
	 * hibTemplate.saveOrUpdateAll(lLstRptReceiptDtls);
	 * if(!lLstRptExpenditureDtls.isEmpty() && lLstRptExpenditureDtls.size() >
	 * 0) hibTemplate.saveOrUpdateAll(lLstRptExpenditureDtls);
	 * 
	 * }
	 * 
	 * if(lLstRptReceiptDtls != null && !lLstRptReceiptDtls.isEmpty()){ Long
	 * lngRcptCounter =
	 * IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount("rpt_receipt_dtls",
	 * inputMap, lLstRptReceiptDtls.size()); for(RptReceiptDtls rcptVO :
	 * lLstRptReceiptDtls){ rcptVO.setRcptId(new
	 * BigDecimal(IFMSCommonServiceImpl.getFormattedPrimaryKey(++lngRcptCounter,
	 * inputMap))); hibSession.save(rcptVO); } }
	 * 
	 * if(lLstRptExpenditureDtls != null && !lLstRptExpenditureDtls.isEmpty()){
	 * Long lngExpCounter =
	 * IFMSCommonServiceImpl.getCurrentSeqNumAndUpdateCount(
	 * "rpt_expenditure_dtls", inputMap, lLstRptExpenditureDtls.size());
	 * for(RptExpenditureDtls expVO : lLstRptExpenditureDtls){
	 * expVO.setExpId(new
	 * BigDecimal(IFMSCommonServiceImpl.getFormattedPrimaryKey(++lngExpCounter,
	 * inputMap))); hibSession.save(expVO); } } }catch(Exception e){
	 * gLogger.error("Error is : ", e); throw e; } }
	 */
	private TrnBillRegister getNewBillRegisterVo(TrnBillRegister lbillRegisterVO) throws Exception {

		TrnBillRegister lNewBillRegister = new TrnBillRegister();
		try {
			lNewBillRegister.setBillCntrlNo(lbillRegisterVO.getBillCntrlNo());
			lNewBillRegister.setBillDate(lbillRegisterVO.getBillDate());
			lNewBillRegister.setSubjectId(lbillRegisterVO.getSubjectId());
			lNewBillRegister.setTokenNum(0L);
			lNewBillRegister.setTcBill(lbillRegisterVO.getTcBill());
			lNewBillRegister.setPhyBill(lbillRegisterVO.getPhyBill());
			lNewBillRegister.setInwardDt(lbillRegisterVO.getInwardDt());
			lNewBillRegister.setPrevBillNo(lbillRegisterVO.getPrevBillNo());
			lNewBillRegister.setPrinciple(lbillRegisterVO.getPrinciple());
			lNewBillRegister.setGrossInterest(lbillRegisterVO.getGrossInterest());
			lNewBillRegister.setIncomeTax(lbillRegisterVO.getIncomeTax());
			lNewBillRegister.setGrossInterest(lbillRegisterVO.getGrossInterest());
			lNewBillRegister.setCreatedUserId(lbillRegisterVO.getCreatedUserId());
			lNewBillRegister.setCreatedPostId(lbillRegisterVO.getCreatedPostId());
			lNewBillRegister.setCreatedDate(lbillRegisterVO.getCreatedDate());
			lNewBillRegister.setUpdatedUserId(gLngUserId);
			lNewBillRegister.setUpdatedPostId(gLngPostId);
			lNewBillRegister.setUpdatedDate(gDtCurDate);
			lNewBillRegister.setAudUserId(lbillRegisterVO.getAudUserId());
			lNewBillRegister.setDbId(lbillRegisterVO.getDbId());
			lNewBillRegister.setExempted(lbillRegisterVO.getExempted());
			lNewBillRegister.setBillCode(lbillRegisterVO.getBillCode());
			lNewBillRegister.setGoNgo(lbillRegisterVO.getGoNgo());
			lNewBillRegister.setGoNgo(lbillRegisterVO.getGoNgo());
			lNewBillRegister.setFinYearId(lbillRegisterVO.getFinYearId());
			lNewBillRegister.setAudPostId(lbillRegisterVO.getAudPostId());
			lNewBillRegister.setBranchCode(lbillRegisterVO.getBranchCode());
			lNewBillRegister.setAttachmentId(lbillRegisterVO.getAttachmentId());
			lNewBillRegister.setTrnCounter(1);
			lNewBillRegister.setDdoCode(lbillRegisterVO.getDdoCode());
			lNewBillRegister.setCurrBillStatusDate(lbillRegisterVO.getCurrBillStatusDate());
			lNewBillRegister.setGrantAmount(lbillRegisterVO.getGrantAmount());
			lNewBillRegister.setDeptCode(lbillRegisterVO.getDeptCode());
			lNewBillRegister.setTsryOfficeCode(lbillRegisterVO.getTsryOfficeCode());
			lNewBillRegister.setLocationCode(lbillRegisterVO.getLocationCode());
			lNewBillRegister.setRefNum(lbillRegisterVO.getRefNum());
			lNewBillRegister.setScannedDocId(lbillRegisterVO.getScannedDocId());
			lNewBillRegister.setAuditorNetAmount(lbillRegisterVO.getAuditorNetAmount());
			lNewBillRegister.setPpoNo(lbillRegisterVO.getPpoNo());
			lNewBillRegister.setReceivedFlag(lbillRegisterVO.getReceivedFlag());
			lNewBillRegister.setBpnNo(lbillRegisterVO.getBpnNo());
			lNewBillRegister.setPpoNo(lbillRegisterVO.getPpoNo());
			lNewBillRegister.setSchemeNo(lbillRegisterVO.getSchemeNo());
			lNewBillRegister.setPpoNo(lbillRegisterVO.getPpoNo());
			lNewBillRegister.setFund(lbillRegisterVO.getFund());
			lNewBillRegister.setClsExp(lbillRegisterVO.getClsExp());
			lNewBillRegister.setBudType(lbillRegisterVO.getBudType());
			lNewBillRegister.setVoucherNo(lbillRegisterVO.getVoucherNo());
			lNewBillRegister.setVoucherDate(lbillRegisterVO.getVoucherDate());
			lNewBillRegister.setAuditDate(lbillRegisterVO.getAuditDate());
			lNewBillRegister.setCardexNo(lbillRegisterVO.getCardexNo());
			lNewBillRegister.setCardexVer(lbillRegisterVO.getCardexVer());
			lNewBillRegister.setHierarchyRefId(lbillRegisterVO.getHierarchyRefId());
			lNewBillRegister.setAuditStatus(lbillRegisterVO.getAuditStatus());
			lNewBillRegister.setInwardedPost(lbillRegisterVO.getInwardedPost());
			lNewBillRegister.setInwardUserId(lbillRegisterVO.getInwardUserId());
			lNewBillRegister.setBillDispDate(lbillRegisterVO.getBillDispDate());
			lNewBillRegister.setTcAdvice(lbillRegisterVO.getTcAdvice());
			lNewBillRegister.setIsAudit(lbillRegisterVO.getIsAudit());
		} catch (Exception e) {
			gLogger.error("Error is " + e, e);
			throw e;
		}

		return lNewBillRegister;
	}

	/*
	 * //New Method Soumya for Bill Delete public ResultObject
	 * deleteBills(Map<String, Object> lInputMap) { ResultObject lObjResult =
	 * new ResultObject(ErrorConstants.SUCCESS, "FAIL"); ServiceLocator serv =
	 * (ServiceLocator) lInputMap.get("serviceLocator"); HttpServletRequest
	 * request = (HttpServletRequest) lInputMap.get("requestObj"); String
	 * lstrViewName = "";
	 * 
	 * Map lMapDeActiveWF = new HashMap(); List<Map> lLstDeActiveWFMap = new
	 * ArrayList(); try { setSessionInfo(lInputMap); PensionBillDAO
	 * lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
	 * 
	 * String lStrBillNoArr =
	 * request.getParameter("billNoArrString").toString(); String lStrSubIdArr =
	 * request.getParameter("subIdArrString").toString();
	 * 
	 * String[] lArrBillNo = lStrBillNoArr.split("~"); String[] lArrSubId =
	 * lStrSubIdArr.split("~");
	 * 
	 * String lStrPpoNo = ""; String lStrCaseApprStatusUpdateFlag = "";
	 * 
	 * for(int i=0;i<lArrBillNo.length;i++) { //bill deletion starts......
	 * if(lArrBillNo[i] != null && ! lArrBillNo[i].equals("")) {
	 * if(lArrSubId[i].equals("9") || lArrSubId[i].equals("44")) {
	 * lObjPensionBillDAO.deleteTrnPensionArrearDtls(lArrBillNo[i]); }
	 * 
	 * //common for all type of bills
	 * lObjPensionBillDAO.deleteRltBillParty(lArrBillNo[i],gStrLocId);
	 * lObjPensionBillDAO.deleteTrnOnlinebillEmp(lArrBillNo[i],gStrLocId);
	 * lObjPensionBillDAO.deleteTrnBillMvmnt(lArrBillNo[i],gStrLocId);
	 * lObjPensionBillDAO
	 * .deleteRptExpEdpDtls(lArrBillNo[i],gStrLocId,lArrSubId[i]);
	 * 
	 * lObjPensionBillDAO.deleteTrnBillEdpDtls(lArrBillNo[i],gStrLocId);
	 * lObjPensionBillDAO
	 * .deleteTrnPensionBillDtls(lArrBillNo[i],lArrSubId[i],gStrLocId
	 * ,gLngPostId,gLngUserId,gDtCurDate);
	 * lObjPensionBillDAO.deleteTrnPensionBillHdr(lArrBillNo[i],gStrLocId);
	 * lObjPensionBillDAO.deleteTrnBillRegister(lArrBillNo[i],gStrLocId);
	 * lObjPensionBillDAO.deleteOtherPartyPayment(lArrBillNo[i],gStrLocId);
	 * lObjPensionBillDAO.updateRecoveryDtls(lArrBillNo[i],
	 * gStrLocId,gLngPostId,gLngUserId,gDtCurDate);
	 * 
	 * if("9".equals(lArrSubId[i]) || "10".equals(lArrSubId[i]) ||
	 * "11".equals(lArrSubId[i])) { lStrPpoNo =
	 * lObjPensionBillDAO.getPPONoForBill(lArrBillNo[i],gStrLocId);
	 * lStrCaseApprStatusUpdateFlag =
	 * lObjPensionBillDAO.getCaseEditStatusLogic(lStrPpoNo, lArrSubId[i],
	 * gStrLocId);
	 * 
	 * if(lStrCaseApprStatusUpdateFlag != null) {
	 * if("false".equals(lStrCaseApprStatusUpdateFlag)) { //update case
	 * approvestatus i.e. Case Editable / Not
	 * lObjPensionBillDAO.updateCaseApprStatus
	 * (lStrPpoNo,gStrLocId,gLngPostId,gLngUserId,gDtCurDate); } } }
	 * lObjPensionBillDAO
	 * .deleteRltPensioncaseBill(lArrBillNo[i],gStrLocId,gLngPostId
	 * ,gLngUserId,gDtCurDate);
	 * 
	 * //For WF lMapDeActiveWF = new HashMap(); lMapDeActiveWF.put("JOB_STATUS",
	 * "DeActive"); lMapDeActiveWF.put("JOB_REF_ID", lArrBillNo[i]);
	 * lMapDeActiveWF.put("FROM_POST", gLngPostId); lMapDeActiveWF.put("DOC_ID",
	 * "210004"); lMapDeActiveWF.put("DOC_TYPE", WFConstants.DEFAULT_DOCTYPE);
	 * lLstDeActiveWFMap.add(lMapDeActiveWF);
	 * 
	 * //special for Supplementary Bill if(lArrSubId[i].equals("45")) {
	 * lObjPensionBillDAO
	 * .updateSuppBillDtls(lArrBillNo[i],gStrLocId,gLngPostId,gLngUserId
	 * ,gDtCurDate); } } //bill deletion ends...... }
	 * 
	 * if (lLstDeActiveWFMap != null && !lLstDeActiveWFMap.isEmpty() &&
	 * lLstDeActiveWFMap.size() > 0) { lInputMap.put("UPDATE_JOB_MAP_LIST",
	 * lLstDeActiveWFMap); WorkFlowHelper.updateBulkJobStatus(lInputMap); }
	 * 
	 * serv.getSessionFactory().getCurrentSession().flush();
	 * 
	 * lObjResult = getSavedPensionBills(lInputMap);
	 * lObjResult.setResultValue(lInputMap); } catch(Exception e) {
	 * gLogger.error("Error is;" + e, e); lObjResult.setResultValue(null);
	 * lObjResult.setThrowable(e);
	 * lObjResult.setResultCode(ErrorConstants.ERROR);
	 * lObjResult.setViewName("errorPage"); } return lObjResult; }
	 */

	private ConfigOnlineBill populateConfigOnlineBillVO(Long lLngSubjectId, SessionFactory lObjSessionFactory) {

		Session lObjSession = lObjSessionFactory.getCurrentSession();
		List<ConfigOnlineBill> lLstData = null;
		Criteria lObjCriteria = null;
		ConfigOnlineBill lObjConfigOnlineBill = null;
		try {
			lObjCriteria = lObjSession.createCriteria(ConfigOnlineBill.class);
			lObjCriteria.add(Restrictions.eq("subjectId", lLngSubjectId));

			// lObjCriteria.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstData = lObjCriteria.list();

			if (lLstData != null && !lLstData.isEmpty() && lLstData.get(0) != null) {
				lObjConfigOnlineBill = lLstData.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error in populateConfigOnlineBillVO. Error is : " + e, e);
		}
		return lObjConfigOnlineBill;
	}

	/**
	 * Fetches the saved Data for editing/viewing the Bill
	 * 
	 * @param Map
	 *            lMapInput
	 * @return ResultObject
	 */
	public ResultObject getActualBillData(Map lMapInput) {

		gLogger.info("Start");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) lMapInput.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) lMapInput.get("requestObj");

		String lStrBillSpecSrvcName = null;
		TrnBillRegister lObjTrnBillRegister = null;
		List<RltBillParty> lLstRltBillPartyVO = null;
		String lStrPagePath = null;
		Long lLngBillType = null;
		String lStrBillNo = null;
		String lStrIsEditable = null;
		Long lLngBillNo = null;
		String lStrIsNewFromRejected = null;
		String lStrIsConfigStatus = null;
		Short lShrBillStatus = null;
		String lStrBillStatus = null;
		BptmCommonServicesDAOImpl lObjTrnBillRegisterDAO = new BptmCommonServicesDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
		String lStrCurrRoleId = null;
		String lStrShowReadOnly = null;
		String lStrBillFlag = null;
		String lStrElementCode = null;
		try {
			setSessionInfo(lMapInput);

			lStrBillNo = StringUtility.getParameter("billNo", request);
			lStrBillFlag = StringUtility.getParameter("billFlag", request);

			lLngBillNo = Long.parseLong(lStrBillNo.trim());
			lObjTrnBillRegister = (TrnBillRegister) lObjTrnBillRegisterDAO.read(lLngBillNo);

			lStrIsNewFromRejected = StringUtility.getParameter("isNewFromRejected", request);
			StringUtility.getParameter("lineItemVal", request);
			lStrBillStatus = StringUtility.getParameter("billStatus", request);

			lMapInput.put("billNo", lStrBillNo.trim());

			if (lStrBillStatus != null && !"".equalsIgnoreCase(lStrBillStatus)) {
				lShrBillStatus = Short.parseShort(lStrBillStatus.trim());
				// Once the bill is approved, it cannot be edited
				if ((lShrBillStatus < DBConstants.ST_BAPRVD_DDO || lShrBillStatus == DBConstants.ST_BRJCT_AUD)) {
					lStrIsEditable = "Y";
				} else if (lShrBillStatus > DBConstants.ST_BCRTD) {
					lStrIsEditable = "N";
				}

				lMapInput.put("hasPrevUserFlag", "Y");
			}

			PensionBillDAOImpl lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			lLstRltBillPartyVO = lObjPensionBillDAO.getBillPartyRltInfo(lLngBillNo);
			lMapInput.put("RltBillParty", lLstRltBillPartyVO);

			lMapInput.put("billStatus", lObjTrnBillRegister.getCurrBillStatus());
			lLngBillType = lObjTrnBillRegister.getSubjectId();

			if (lLngBillType == 45L) {
				lStrShowReadOnly = ((StringUtility.getParameter("showReadOnly", request).trim()).length() > 0) ? StringUtility.getParameter("showReadOnly", request).trim() : null;
				if (lStrShowReadOnly != null && lStrShowReadOnly.length() > 0) {
					lMapInput.put("showReadOnly", lStrShowReadOnly);
				}
			}
			lMapInput.put("subjectId", lLngBillType);

			lLngBillType = lObjTrnBillRegister.getSubjectId();
			lMapInput.put("TrnBillRegister", lObjTrnBillRegister);

			BillMovementDAOImpl lObjMvmntDAO = new BillMovementDAOImpl(TrnBillMvmnt.class, serv.getSessionFactory());
			TrnBillMvmnt lObjTrnBillMvmnt = lObjMvmntDAO.getRmrksForCurrUser(lLngBillNo, gLngUserId);

			if (lObjTrnBillMvmnt != null) {
				lMapInput.put("currRemarks", lObjTrnBillMvmnt.getBillRemarks());
				lMapInput.put("lObjTrnBillMvmnt", lObjTrnBillMvmnt);
			}

			ConfigOnlineBill lObjConfigVO = populateConfigOnlineBillVO(lLngBillType, serv.getSessionFactory());
			lStrPagePath = lObjConfigVO.getJspPath();
			lStrBillSpecSrvcName = lObjConfigVO.getFetchBillDataSrvc();

			ResultObject resultObj = serv.executeService(lStrBillSpecSrvcName, lMapInput);

			// To get current users Role
			// ------Start------
			// OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new
			// OnlinePensionCaseDAOImpl(TrnEcsDtl.class,
			// serv.getSessionFactory());
			CommonPensionDAOImpl lObjCommonPensionDAOImpl = new CommonPensionDAOImpl(serv.getSessionFactory());
			// lStrCurrRoleId =
			// lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			lStrCurrRoleId = StringUtility.getParameter("currRole", request).trim();
			lMapInput.put("currRole", lStrCurrRoleId);
			// ------End-----
			lMapInput.put("BillDtls", resultObj.getResultValue());
			lMapInput.put("CurrentDate", new Date());
			lMapInput.put("TrnBillRegister", lObjTrnBillRegister);
			lMapInput.put("BillNo", lLngBillNo);
			lMapInput.put("PagePath", lStrPagePath);
			lMapInput.put("EditBill", lStrIsEditable);
			lMapInput.put("BillTypeId", lLngBillType);
			lMapInput.put("isNewFromRejected", lStrIsNewFromRejected.trim());
			lMapInput.put("isConfigStatus", lStrIsConfigStatus);
			lMapInput.put("billFlag", lStrBillFlag);
			if (lObjTrnBillRegister.getScannedDocId() != null) {
				getAttachments(lMapInput, lObjTrnBillRegister, serv);
			}
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(lMapInput);
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, objRes, e, "Error is : ");
		}
		return objRes;
	}

	public void getHeadDetailsByBillType(Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		Map<Long, RltPensionHeadcodeChargable> lMapHeadCodeChargable = null;
		RltPensionHeadcodeChargable lObjRltPensionHeadcodeChargable = null;
		String lStrsubjectId = null;
		String lStrPPONo = null;
		String lHeadCode = null;
		String lStrPensionFlag = null;
		String lStrDmndNo = null;
		String lStrbudMjrHd = null;
		String lStrbudSubmjrHd = null;
		String lStrbudMinHd = null;
		String lStrbudSubHd = null;
		String lStrbudDtlHd = null;
		String lStrSchemeNo = null;
		String lStrStatus = bundleConst.getString("STATUS.CONTINUE");
		String lStrPnsnBillType = bundleConst.getString("BILLTYPE.PENSION");
		String lStrCVPBillType = bundleConst.getString("BILLTYPE.CVP");
		String lStrDCRGBillType = bundleConst.getString("BILLTYPE.DCRG");
		try {
			setSessionInfo(inputMap);
			lStrsubjectId = (String) inputMap.get("subjectId");
			if (lStrsubjectId != null && lStrsubjectId.equalsIgnoreCase(lStrPnsnBillType)) {
				lStrPensionFlag = bundleConst.getString("RECOVERY.PENSION");
				lStrPPONo = (String) inputMap.get("PPONo");
			} else if (lStrsubjectId != null && lStrsubjectId.equalsIgnoreCase(lStrDCRGBillType)) {
				lStrPensionFlag = bundleConst.getString("RECOVERY.DCRG");
				lStrPPONo = (String) inputMap.get("PPONo");
			} else if (lStrsubjectId != null && lStrsubjectId.equalsIgnoreCase(lStrCVPBillType)) {
				lStrPensionFlag = bundleConst.getString("RECOVERY.CVP");
				lStrPPONo = (String) inputMap.get("PPONo");
			}

			List lRqstHdrElem = new ArrayList();
			BudgetDtlsDAO lObjBudgetDtlsDAO = new BudgetDtlsDAOImpl(srvcLoc.getSessionFactory());
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(srvcLoc.getSessionFactory());
			if (lStrPPONo != null && lStrPPONo.length() > 0) {
				// Getting the ObjectVo of TrnPensionRqstHdr
				lRqstHdrElem = lObjBudgetDtlsDAO.getTrnPensionRqstHdrDtls(lStrPPONo, lStrStatus, gStrLocationCode);
			}

			if (lRqstHdrElem != null && !lRqstHdrElem.isEmpty()) {
				lHeadCode = lRqstHdrElem.get(1).toString();
			}
			// Getting Head Structure Mapped with a HeadCode...
			Map<String, Object> MapTrnBillRegister = new HashMap<String, Object>();
			if (lHeadCode != null) {
				lMapHeadCodeChargable = lObjCommonPensionDAO.getRltPensionHeadcodeChargableDtls(lStrPensionFlag);
				lObjRltPensionHeadcodeChargable = lMapHeadCodeChargable.get(Long.valueOf(lHeadCode));
				if (lObjRltPensionHeadcodeChargable != null) {
					lStrDmndNo = lObjRltPensionHeadcodeChargable.getDemandNo();
					lStrbudMjrHd = lObjRltPensionHeadcodeChargable.getMjrhdCode();
					lStrbudSubmjrHd = lObjRltPensionHeadcodeChargable.getSubmjrhdCode();
					lStrbudMinHd = lObjRltPensionHeadcodeChargable.getMinhdCode();
					lStrbudSubHd = lObjRltPensionHeadcodeChargable.getSubhdCode();
					lStrbudDtlHd = lObjRltPensionHeadcodeChargable.getDtlhdCode();
					lStrSchemeNo = lObjRltPensionHeadcodeChargable.getSchemeCode();

					MapTrnBillRegister.put("schemeNo", "000000");
					MapTrnBillRegister.put("demandCode", lStrDmndNo);
					MapTrnBillRegister.put("budmjrHd", lStrbudMjrHd);
					MapTrnBillRegister.put("budSubmjrHd", lStrbudSubmjrHd);
					MapTrnBillRegister.put("budMinHd", lStrbudMinHd);
					MapTrnBillRegister.put("budSubHd", lStrbudSubHd);
					MapTrnBillRegister.put("budDtlHd", lStrbudDtlHd);
					String lStrHeadChrg = lStrbudMjrHd + lStrbudSubmjrHd + lStrbudMinHd + lStrbudSubHd;
					MapTrnBillRegister.put("headChrg", lStrHeadChrg);
				}
			} else {
				// For Consolidated Bill Fix Dummy Head structure

				if ("44".equals(lStrsubjectId)) // For Consolidated Bill Fix
				// Dummy Head structure
				{
					lStrbudSubHd = "01";
				} else if ("43".equals(lStrsubjectId)) // For Consolidated Bill
				// Fix Dummy Head
				// structure
				{
					lStrbudSubHd = "02";
				}
				MapTrnBillRegister.put("schemeNo", "000000");
				MapTrnBillRegister.put("demandCode", "018");
				MapTrnBillRegister.put("budmjrHd", "2071");
				MapTrnBillRegister.put("budSubmjrHd", "01");
				MapTrnBillRegister.put("budMinHd", "101");
				MapTrnBillRegister.put("budSubHd", lStrbudSubHd);
				MapTrnBillRegister.put("budDtlHd", "00");
				String lStrHeadChrg = "2071" + "01" + "101" + lStrbudSubHd;
				MapTrnBillRegister.put("headChrg", lStrHeadChrg);
			}

			inputMap.put("TrnBillRegister", MapTrnBillRegister);
			inputMap.put("schemeNo", lStrSchemeNo);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is : ", e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
	}

	public ResultObject getAllFirstPensionBillCases(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		try {
			List lLstPensionCases = lPensionDao.getAllFirstPensionBillCases(gStrLocationCode);
			inputMap.put("pensioncaselist", lLstPensionCases);

			objRes.setResultValue(inputMap);
			objRes.setViewName("FirstPayBill");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			// e.printStackTrace();
		}

		return objRes;
	}

	/**
	 * Fetches the saved attachments related to bill.
	 * 
	 * @param Map
	 *            , TrnBillRegister, ServiceLocator lMapInput,
	 *            lObjTrnBillRegister, serv
	 * @return void
	 */
	private void getAttachments(Map lMapInput, TrnBillRegister lObjTrnBillRegister, ServiceLocator serv) throws Exception {

		try {
			if (lObjTrnBillRegister.getScannedDocId() != null) {
				CmnAttachmentMstDAO mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
				CmnAttachmentMst cmnAttachmentMst = mnAttachmentMstDAO.findByAttachmentId(lObjTrnBillRegister.getScannedDocId());

				lMapInput.put("scan", cmnAttachmentMst);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PensionBillService#getDraftBills(java.
	 * util.Map)
	 */
	public ResultObject getDraftBills(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());

		List<SavedPensionBillVO> lLstBills = new ArrayList<SavedPensionBillVO>();
		List lLstAuditorList = new ArrayList();

		// OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new
		// OnlinePensionCaseDAOImpl(TrnEcsDtl.class,
		// srvcLoc.getSessionFactory());
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
		List<Long> lLstBillType = new ArrayList<Long>();
		lLstBillType.add(Long.parseLong(bundleConst.getString("BILLTYPE.PENSION")));
		lLstBillType.add(Long.parseLong(bundleConst.getString("BILLTYPE.DCRG")));
		lLstBillType.add(Long.parseLong(bundleConst.getString("BILLTYPE.CVP")));
		lLstBillType.add(Long.parseLong(bundleConst.getString("BILLTYPE.MONTHLY")));
		lLstBillType.add(Long.parseLong(bundleConst.getString("BILLTYPE.SUPP")));
		// lLstBillType.add(bundleConst.getString("RECOVERY.SUPP"));
		// lLstBillType.add(bundleConst.getString("PPMT.MONTHLY"));

		int totalRecords = 0;
		String lStrSearchStr = null;
		String lStrSearchValue = null;
		String lStrRoleId = bundleConst.getString("AUDITOR.ROLEID");
		String lStrCurrBillStatus = bundleConst.getString("BILL.CURRBILLSTATUS");
		Long lLngRoleId = null;
		String lStrCurrRoleId = null;
		String lStrBillFlag = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String lStrElementCode = null;
		String lStrVoucherMpgFlag = null;
		try {
			// To get current users Role
			// ------Start------
			// lStrCurrRoleId =
			// lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			lStrVoucherMpgFlag = StringUtility.getParameter("voucherMpgFlag", request);
			if (!"".equals(lStrElementCode)) {
				lStrCurrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			} else {
				lStrCurrRoleId = StringUtility.getParameter("currRole", request).trim();
			}
			inputMap.put("currRole", lStrCurrRoleId);
			// ------End-----

			inputMap.put("currdate", lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(gDtCurDate)));
			lStrBillFlag = ((StringUtility.getParameter("billFlag", request).trim()).length() > 0) ? StringUtility.getParameter("billFlag", request).trim() : null;
			if (lStrBillFlag != null && lStrBillFlag.length() > 0) {
				inputMap.put("billFlag", lStrBillFlag);
			}
			inputMap.put("currBillStatus", lStrCurrBillStatus);
			lLngRoleId = Long.parseLong(lStrRoleId);
			lLstAuditorList = lObjPensionDao.getAuditorsListFromRoleID(lLngRoleId, gStrLocationCode);
			inputMap.put("ListAuditors", lLstAuditorList);

			if (StringUtility.getParameter("cmbSearch", request) != null && StringUtility.getParameter("cmbSearch", request).length() > 0) {
				lStrSearchStr = StringUtility.getParameter("cmbSearch", request);
				inputMap.put("searchStr", lStrSearchStr);
			}
			if (lStrSearchStr != null && !"-1".equals(lStrSearchStr)) {
				if (lStrSearchStr.equals("tbr.subject_id")) {
					lStrSearchValue = StringUtility.getParameter("CmbBillType", request);
				} else if (lStrSearchStr.equals("tbr.tc_bill")) {
					lStrSearchValue = StringUtility.getParameter("CmbBillCtgry", request);
				} else if (lStrSearchStr.equals("our.post_id")) {
					lStrSearchValue = StringUtility.getParameter("CmbSerchAuditots", request);
				} else {
					lStrSearchValue = StringUtility.getParameter("txtSearch", request);
				}
				lStrSearchValue = lStrSearchValue.trim();
				inputMap.put("searchValue", lStrSearchValue);
			}
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			totalRecords = lObjPensionDao.getDraftBillCounts(gStrLocationCode, lStrSearchStr, lStrSearchValue, lLstBillType, lStrCurrRoleId, lStrBillFlag, gLngPostId);
			if (totalRecords > 0) {
				lLstBills = lObjPensionDao.getDraftBills(displayTag, gStrLocationCode, lStrSearchStr, lStrSearchValue, lLstBillType, lStrCurrRoleId, lStrBillFlag, gLngPostId);
			}
			if (lLstBills != null && lLstBills.size() > 0) {
				inputMap.put("savedPensionBillsList", lLstBills);
			}
			inputMap.put("totalRecords", totalRecords);
			objRes.setResultValue(inputMap);
			if (!"".equals(lStrVoucherMpgFlag)) {
				if ("Y".equals(lStrVoucherMpgFlag)) {
					objRes.setViewName("DraftBillsPopup");
				}
			} else {
				objRes.setViewName("DraftBills");
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			// e.printStackTrace();
		}
		return objRes;

	}

	public ResultObject forwardApproveBills(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");

		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(TrnEcsDtl.class, srvcLoc.getSessionFactory());
		String lStrBillNos = null;
		String[] lArrBillNo = null;
		List<String> lLstBillNo = new ArrayList<String>();
		List<Long> lLstLngBillNo = new ArrayList<Long>();
		String lStrCurrRoleId = null;
		Short lSrtStatus = null;
		String lStrBillFlag = null;
		String lStrRejectFlag = null;
		String lStrResultFlag = null;
		String lStrToFlag = "";
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
		String lStrElementCode = null;
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		Map<String, String> lMapSubIdBillNo = new HashMap<String, String>();
		try {

			lStrCurrRoleId = StringUtility.getParameter("currRole", request).trim();
			// To get current users Role
			// ------Start------
			// lStrCurrRoleId =
			// lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			// lStrCurrRoleId =
			// lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			inputMap.put("currRole", lStrCurrRoleId);
			// ------End-----

			lStrBillFlag = ((StringUtility.getParameter("billFlag", request).trim()).length() > 0) ? StringUtility.getParameter("billFlag", request).trim() : null;
			if (lStrBillFlag != null && lStrBillFlag.length() > 0) {
				inputMap.put("billFlag", lStrBillFlag);
			}

			lStrRejectFlag = ((StringUtility.getParameter("rejectFlag", request).trim()).length() > 0) ? StringUtility.getParameter("rejectFlag", request).trim() : null;
			if (lStrRejectFlag != null && lStrRejectFlag.length() > 0) {
				inputMap.put("rejectFlag", lStrRejectFlag);
			}

			lStrBillNos = StringUtility.getParameter("billNoString", request);
			lArrBillNo = lStrBillNos.split("~");
			for (int i = 0; i < lArrBillNo.length; i++) {
				String lStrBillNo = lArrBillNo[i].substring(0, lArrBillNo[i].indexOf("_"));
				lLstBillNo.add(lStrBillNo);
				if (!"".equals(lStrBillNo)) {
					lLstLngBillNo.add(Long.valueOf(lStrBillNo));
				}
			}

			if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && lStrBillFlag.equalsIgnoreCase("D") && lStrRejectFlag.equals("A")) {
				lSrtStatus = DBConstants.ST_BILL_FORW_TO_ATO;
				lStrResultFlag = "Forwarded";
				lStrToFlag = "to Pension ATO.";

			} else if (bundleConst.getString("PPMT.ATOROLE").equals(lStrCurrRoleId) && lStrRejectFlag.equals("A")) {
				lSrtStatus = DBConstants.ST_BILL_APPROVED;
				lStrResultFlag = "Approved";
				lStrToFlag = "and Fowarded to Auditor.";
				// --For inserting last paid date on approval of first pension
				// bill
				// if (!lLstLngBillNo.isEmpty()) {
				// // lLstPpoNo =
				// // lObjPensionDao.getPPONoListFromPnsncaseRlt(lLstLngBillNo);
				// lLstPpoNo = lObjPensionDao.getPpoNoByBillNo(lLstLngBillNo);
				// if (!lLstPpoNo.isEmpty()) {
				// lObjPensionDao.updateLastPaidDateForPensionBills(lLstPpoNo,
				// gDtCurDate);
				// }
				// }
			} else if (bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId) && lStrBillFlag.equalsIgnoreCase("A") && !lStrRejectFlag.equals("AR")) {
				lSrtStatus = DBConstants.ST_BILL_FORW_TO_TAUD;
				lStrResultFlag = "Forwarded";
				lStrToFlag = "to Audit ATO.";

			} else if (bundleConst.getString("PPMT.TRSRYAUDITORROLE").equals(lStrCurrRoleId) && lStrRejectFlag.equals("A")) {
				// lSrtStatus = DBConstants.ST_BILL_FORW_TO_USER;
				// lStrToFlag = "to Payment User.";
				lSrtStatus = DBConstants.ST_BILL_ECS_AUTH; // Forwarding bills
															// for bill voucher
															// mapping directly.
				lStrResultFlag = "Forwarded";
				lStrToFlag = "to Auditor for Bill Voucher Mapping.";
			} else if (lStrRejectFlag.equals("R") && bundleConst.getString("PPMT.ATOROLE").equals(lStrCurrRoleId)) {
				lSrtStatus = DBConstants.ST_BILL_REJECTED;
				lStrResultFlag = "Rejected";
				lStrToFlag = "and Fowarded to Auditor.";
			} else if (lStrRejectFlag.equals("R") && bundleConst.getString("PPMT.TRSRYAUDITORROLE").equals(lStrCurrRoleId)) {
				lSrtStatus = DBConstants.ST_BILL_REJECTED_BY_TRSRY;
				lStrResultFlag = "Rejected";
				lStrToFlag = "and Fowarded to Pension ATO.";
			} else if (lStrRejectFlag.equals("R") && bundleConst.getString("PPMT.PAYMENTUSER").equals(lStrCurrRoleId)) {
				lSrtStatus = DBConstants.ST_BILL_FORW_TO_TAUD;
				lStrResultFlag = "Returned";
				lStrToFlag = " and Forwarded to Audit ATO.";
			} else if (lStrRejectFlag.equals("AR") && bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId)) {
				lSrtStatus = DBConstants.ST_BILL_ARCHEIVED;
				lStrResultFlag = "Archeive";
				/*
				 * Discarding change statement on archive of monthly bill starts
				 * <<<<<
				 */
				if (!lLstLngBillNo.isEmpty()) {
					discardChngStmnt(lLstLngBillNo, inputMap);
					lObjPensionDao.updateRltPensioncaseBill(lLstLngBillNo, gStrLocationCode, gLngPostId, gLngUserId, gDtCurDate);
				}
				/*
				 * Discarding change statement on archive of monthly bill ends
				 * >>>>>>
				 */
				// for (String lStrBillNo : lLstBillNo) {
				// lObjPensionDao.deleteRltPensioncaseBill(lStrBillNo,
				// gStrLocationCode, gLngPostId, gLngUserId, gDtCurDate);
				// }
			} else if (lStrRejectFlag.equals("R") && bundleConst.getString("PPMT.AUDITORROLE").equals(lStrCurrRoleId)) {
				lSrtStatus = DBConstants.ST_BILL_DISCARD;
				lStrResultFlag = "Discarded";
				/*
				 * Discarding change statement on discard of monthly bill starts
				 * <<<<<
				 */
				if (!lLstLngBillNo.isEmpty()) {
					discardChngStmnt(lLstLngBillNo, inputMap);
					lObjPensionDao.updateRltPensioncaseBill(lLstLngBillNo, gStrLocationCode, gLngPostId, gLngUserId, gDtCurDate);
				}
				/*
				 * Discarding change statement on discard of monthly bill ends
				 * >>>>>>
				 */

				
				/*
				 * --On discard of pension bill or monthly bill updating paid
				 * flag to N in all arrear details having bill no passed in
				 * argument.
				 */
				if (!lLstLngBillNo.isEmpty()) {
					lObjPensionDao.updatePaidFlagInArrearDtlsOnApproveReject(lLstLngBillNo, "N", gLngUserId, gLngPostId, gDtCurDate);
				}
				// for (String lStrBillNo : lLstBillNo) {
				// lObjPensionDao.deleteRltPensioncaseBill(lStrBillNo,
				// gStrLocationCode, gLngPostId, gLngUserId, gDtCurDate);
				// }
			}
			/*
			 * update status in supplementary bill table start
			 */
			if((DBConstants.ST_BILL_DISCARD).equals(lSrtStatus) || (DBConstants.ST_BILL_REJECTED).equals(lSrtStatus) || (DBConstants.ST_BILL_APPROVED).equals(lSrtStatus)
					 || (DBConstants.ST_BILL_ARCHEIVED).equals(lSrtStatus) || (DBConstants.ST_BILL_FORW_TO_ATO).equals(lSrtStatus))
			{
				if (!lLstLngBillNo.isEmpty()) {
					lLstResult = lObjPensionDao.getSubjectIdFromBillNo(lLstLngBillNo, gStrLocationCode);
					List<Long> lLstSuppBillNo = new ArrayList<Long>();
					if (!lLstResult.isEmpty()) {
						for (int lIntCnt = 0; lIntCnt < lLstResult.size(); lIntCnt++) {
							Object[] obj = lLstResult.get(lIntCnt);
							lMapSubIdBillNo.put(obj[0].toString(), obj[1].toString());
						}
					}
					for (String lStrBillNo : lMapSubIdBillNo.keySet()) {
						String lStrSubId = lMapSubIdBillNo.get(lStrBillNo);
						if ("45".equals(lStrSubId)) {
							lLstSuppBillNo.add(Long.parseLong(lStrBillNo));
						}
					}
					if (!lLstSuppBillNo.isEmpty()) {
						lObjPensionDao.updateStatusInSupplyBillDtls(lLstSuppBillNo, lSrtStatus, gLngPostId, gLngUserId, gDtCurDate);
					}
				}
			}
			/*
			 * update status in supplementary bill table end
			 */
			lObjPensionDao.updateECSRqstHdrDtls(lLstBillNo, lSrtStatus, gLngPostId, gLngUserId, gDtCurDate);

			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append(" <FLAG> ");
			lStrBldXML.append(lStrResultFlag);
			lStrBldXML.append(" </FLAG> ");
			lStrBldXML.append("<TOROLE>");
			lStrBldXML.append(lStrToFlag);
			lStrBldXML.append("</TOROLE>");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}
		return resObj;
	}

	public ResultObject generateECS(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		String[] lArrBillNo = null;
		List<Long> lLstBillNos = null;
		String lStrTotalBillAmount = null;
		String lStrbillNos = null;
		Long lLngChequeId = null;
		BigDecimal lBdclChequeAmnt = null;
		Long lLngChequeAmnt = null;
		String lStrBillNo = null;
		Long lLngBillNo = null;
		Long lLngRltBillChqId = null;
		TrnChequeDtlsDAO lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(TrnChequeDtls.class, srvcLoc.getSessionFactory());
		BigDecimal ECSCode;
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		String lStrFilePath = "temp";
		String lStrBillNetAmt = null;
		String[] lArrNetAmnt = null;
		// PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new
		// PhysicalCaseInwardDAOImpl(
		// TrnChequeDtls.class, srvcLoc.getSessionFactory());
		try {
			lStrbillNos = request.getParameter("billNoString");
			lStrBillNetAmt = StringUtility.getParameter("billNetAmountString", request);
			lArrBillNo = lStrbillNos.split("~");
			lArrNetAmnt = lStrBillNetAmt.split("~");
			lStrTotalBillAmount = request.getParameter("totalBillAmnt");
			lLngChequeAmnt = Long.parseLong(lStrTotalBillAmount);
			lBdclChequeAmnt = new BigDecimal(Long.valueOf(lLngChequeAmnt));
			lLstBillNos = new ArrayList<Long>();
			TrnChequeDtls lObjTrnChequeDtls = new TrnChequeDtls();
			RltBillCheque lObjRltBillCheque = null;
			lLngChequeId = IFMSCommonServiceImpl.getNextSeqNum("trn_cheque_dtls", inputMap);
			lObjTrnChequeDtls.setChequeId(lLngChequeId);
			lObjTrnChequeDtls.setChequeAmt(lBdclChequeAmnt);
			lObjTrnChequeDtls.setPartyName("FOR ECS");
			lObjTrnChequeDtls.setStatus(DBConstants.ST_ECS_GEN);
			lObjTrnChequeDtls.setLocationCode(gStrLocationCode);
			lObjTrnChequeDtls.setCreatedDate(gDtCurDate);
			lObjTrnChequeDtls.setCreatedPostId(gLngPostId);
			lObjTrnChequeDtls.setCreatedUserId(gLngUserId);
			lObjTrnChequeDtls.setFromDt(gDtCurDate);
			lObjTrnChequeDtlsDAO.create(lObjTrnChequeDtls);
			// lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(
			// TrnChequeDtls.class, srvcLoc.getSessionFactory());
			for (int i = 0; i < lArrBillNo.length; i++) {

				lObjRltBillCheque = new RltBillCheque();
				Double lDbBillNetAmt = Double.parseDouble(lArrNetAmnt[i].toString());
				if (lDbBillNetAmt > 0) {
					lStrBillNo = lArrBillNo[i].substring(0, lArrBillNo[i].indexOf("_"));
					lLngRltBillChqId = IFMSCommonServiceImpl.getNextSeqNum("rlt_bill_cheque", inputMap);
					lLngBillNo = Long.parseLong(lStrBillNo);
					lLstBillNos.add(lLngBillNo);
					lObjRltBillCheque.setBillChequeId(lLngRltBillChqId);
					lObjRltBillCheque.setChequeId(lLngChequeId);
					lObjRltBillCheque.setBillNo(lLngBillNo);
					lObjRltBillCheque.setCreatedDate(gDtCurDate);
					lObjRltBillCheque.setCreatedPostId(gLngPostId);
					lObjRltBillCheque.setCreatedUserId(gLngUserId);
					lObjRltBillCheque.setLocationCode(gStrLocationCode);
					lObjRltBillCheque.setPartyAmt(lBdclChequeAmnt);// Check out
					// for
					// this=======
					lObjTrnChequeDtlsDAO.create(lObjRltBillCheque);
				} else {
					// For Bill with ZERO Net Amount
					List lLstBillNo = new ArrayList();
					lStrBillNo = lArrBillNo[i].substring(0, lArrBillNo[i].indexOf("_"));
					lLstBillNo.add(Long.parseLong(lStrBillNo));
					lObjPensionDao.updateECSRqstHdrDtls(lLstBillNo, DBConstants.ST_BILL_ECS_GENERTED, gLngPostId, gLngUserId, gDtCurDate);
				}
			}

			// System.out.println("The array of bill no is " + lLngBillNo);
			// System.out.println("The cheque id is " + lLngChequeId);
			// System.out.println("The bill_cheque id is " + lLngRltBillChqId);
			// System.out.println("The bill amount is " + lStrTotalBillAmount);

			ECSCode = lObjPensionDao.autoGeneratedECSCode(gStrLocId);
			generateECSFile(lLstBillNos, ECSCode, lStrFilePath, inputMap, lLngChequeId);

			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append(" <ECSCODE> ");
			lStrBldXML.append(ECSCode);
			lStrBldXML.append(" </ECSCODE> ");
			lStrBldXML.append(" <ECSFlag> ");
			lStrBldXML.append(gIntECSFlag);
			lStrBldXML.append(" </ECSFlag> ");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}
		return resObj;

	}

	public void generateECSFile(List strLst, BigDecimal ECSCode, String strFile, Map<String, Object> inputMap, Long lLngChequeId) {

		final String newLineStr = "\r\n";
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			File fFile = null;
			PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
			fFile = new File(strFile);
			Writer output = null;
			output = new BufferedWriter(new FileWriter(fFile));
			String str = null;
			List<String> lBillno = new ArrayList<String>();
			String strTemp = "";
			List lPensionerDtls = null;
			lPensionerDtls = lObjPensionDao.getPensionerDtls(strLst, gStrLocId);
			Iterator it = lPensionerDtls.iterator();
			int iSrNo = 0;
			while (it.hasNext()) {

				Object tuple[] = (Object[]) it.next();
				BigDecimal lBgDcmlBillAmnt = (BigDecimal) tuple[4];
				Long lLngBillAmt = lBgDcmlBillAmnt.longValue();
				if (lLngBillAmt > 0L) {
					iSrNo++;
					str = iSrNo + "," + tuple[0].toString() + "," + tuple[1].toString() + "," + tuple[3].toString() + "," + tuple[2].toString() + "," + tuple[4].toString();
					if (!tuple[5].toString().equals(strTemp)) {
						lBillno.add(tuple[5].toString());
						strTemp = tuple[5].toString();
					}
					output.write(str + newLineStr);
				}
			}
			output.close();
			exportToTextFile(fFile, inputMap, strLst, ECSCode, lLngChequeId);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			// e.printStackTrace();
		}

	}

	private void exportToTextFile(File fFile, Map<String, Object> inputMap, List<String> lBillno, BigDecimal ECSCode, Long lLngChequeId) {

		new ResultObject(ErrorConstants.SUCCESS);
		Long lLngECSId = 0L;
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			lLngECSId = (IFMSCommonServiceImpl.getNextSeqNum("cmn_attdoc_mst", inputMap));
			FileInputStream fileInuptStream = new FileInputStream(fFile);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInuptStream);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int start = 0;
			int length = 1024;
			int offset = -1;
			String lStrFileName = ECSCode.toString() + ".txt";
			byte[] buffer = new byte[length];
			while ((offset = bufferedInputStream.read(buffer, start, length)) != -1) {
				byteArrayOutputStream.write(buffer, start, offset);
			}
			bufferedInputStream.close();
			byteArrayOutputStream.flush();
			buffer = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
			CmnDatabaseMstDaoImpl cmnDatabaseMstDaoImpl = new CmnDatabaseMstDaoImpl(CmnDatabaseMst.class, serv.getSessionFactory());
			CmnLocationMstDaoImpl lObjLocationMstDaoImpl = new CmnLocationMstDaoImpl(CmnLocationMst.class, serv.getSessionFactory());
			CmnLookupMstDAOImpl lObjLookupMstDAOImpl = new CmnLookupMstDAOImpl(CmnLookupMst.class, serv.getSessionFactory());
			// Saving into Attachment Master.
			CmnAttachmentMst lObjAttachmentMst = new CmnAttachmentMst();
			CmnAttachmentMstDAOImpl lObjAttMstDAOImpl = new CmnAttachmentMstDAOImpl(CmnAttachmentMst.class, serv.getSessionFactory());
			Long lAttachmentID = IFMSCommonServiceImpl.getNextSeqNum("cmn_attachment_mst", inputMap);
			lObjAttachmentMst.setAttachmentId(lAttachmentID);
			lObjAttachmentMst.setCmnDatabaseMst(cmnDatabaseMstDaoImpl.read(gLngDBId));
			lObjAttachmentMst.setCmnLocationMst(lObjLocationMstDaoImpl.read(Long.parseLong(gStrLocationCode)));
			lObjAttachmentMst.setCreatedBy(gLngUserId);
			lObjAttachmentMst.setCreatedByPost(gLngPostId);
			lObjAttachmentMst.setCreatedDate(gDtCurDate);
			lObjAttachmentMst.setActivateFlag("Y");
			lObjAttachmentMst.setTrnCounter(1);
			lObjAttMstDAOImpl.create(lObjAttachmentMst);
			// Saving into cmn_attachment_mpg mapping table
			CmnAttachmentMpg lObjAttachmentMpg = new CmnAttachmentMpg();
			CmnAttachmentMpgDAOImpl lObjMpgDAOImpl = new CmnAttachmentMpgDAOImpl(CmnAttachmentMpg.class, serv.getSessionFactory());
			Long lAttDocSrNo = IFMSCommonServiceImpl.getNextSeqNum("cmn_attachment_mpg", inputMap);
			lObjAttachmentMpg.setSrNo(lAttDocSrNo);
			lObjAttachmentMpg.setCmnDatabaseMst(cmnDatabaseMstDaoImpl.read(gLngDBId));
			lObjAttachmentMpg.setCmnLocationMst(lObjLocationMstDaoImpl.read(Long.parseLong(gStrLocationCode)));
			lObjAttachmentMpg.setCmnAttachmentMst(lObjAttMstDAOImpl.read(lAttachmentID));
			lObjAttachmentMpg.setCmnLookupMst(lObjLookupMstDAOImpl.read(100062L));
			lObjAttachmentMpg.setAttachmentDesc("ECS FILE");
			lObjAttachmentMpg.setCreatedBy(gLngUserId);
			lObjAttachmentMpg.setCreatedByPost(gLngPostId);
			lObjAttachmentMpg.setCreatedDate(gDtCurDate);
			lObjAttachmentMpg.setActivateFlag("Y");
			lObjAttachmentMpg.setTrnCounter(1);
			lObjAttachmentMpg.set
			// added--------------------------------
			lObjAttachmentMpg.setOrgFileName(lStrFileName);
			// end------------------------------------
			lObjMpgDAOImpl.create(lObjAttachmentMpg);

			// Saving Actual ECS file into cmn_attdoc_mst table
			CmnAttdocMst lObjAttdocMst = new CmnAttdocMst();
			CmnAttdocMstDAOImpl lObjAttdocMstDAOImpl = new CmnAttdocMstDAOImpl(CmnAttdocMst.class, serv.getSessionFactory());
			lObjAttdocMst.setAttdocId(lLngECSId);
			lObjAttdocMst.setCmnDatabaseMst(cmnDatabaseMstDaoImpl.read(gLngDBId));
			lObjAttdocMst.setCmnLocationMst(lObjLocationMstDaoImpl.read(Long.parseLong(gStrLocationCode)));
			lObjAttdocMst.setCmnAttachmentMpg(lObjMpgDAOImpl.read(lAttDocSrNo));
			lObjAttdocMst.setFinalAttachment(buffer);
			lObjAttdocMst.setCreatedBy(gLngUserId);
			lObjAttdocMst.setCreatedByPost(gLngPostId);
			lObjAttdocMst.setCreatedDate(gDtCurDate);
			// lObjAttdocMst.setAttdocId(IFMSCommonServiceImpl.getNextSeqNum("cmn_attdoc_mst",
			// inputMap));
			lObjAttdocMst.setTrnCounter(1);
			lObjAttdocMstDAOImpl.create(lObjAttdocMst);

			// Update TrnBillRegister

			PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(serv.getSessionFactory());
			lObjPensionDao.updateECSRqstHdrDtls(lBillno, DBConstants.ST_BILL_ECS_GENERTED, gLngPostId, gLngUserId, gDtCurDate);

			TrnChequeDtls lobjTrnChequeDtls = null;
			TrnChequeDtlsDAO lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(TrnChequeDtls.class, serv.getSessionFactory());
			// lChqDtls = strlst.split(",");
			// while (lChqDtls.length != ilen) {
			lobjTrnChequeDtls = (TrnChequeDtls) lObjTrnChequeDtlsDAO.read(lLngChequeId);
			lobjTrnChequeDtls.setAdviceNo(ECSCode.longValue());
			lobjTrnChequeDtls.setStatus(DBConstants.ST_ECS_GEN);
			lobjTrnChequeDtls.setChequeType(DBConstants.CHEQ_TYPE_ECS);
			lobjTrnChequeDtls.setUpdatedDate(gDtCurDate);
			lobjTrnChequeDtls.setUpdatedPostId(gLngPostId);
			lobjTrnChequeDtls.setUpdatedUserId(gLngUserId);
			lObjTrnChequeDtlsDAO.update(lobjTrnChequeDtls);
			// ilen++;
			// insert Record into TrnECSDtl
			// }

			generateTrnECSDtlVO(inputMap, ECSCode, lLngECSId);

		} catch (Exception e) {
			gLogger.error("Error" + e, e);
			// e.printStackTrace();
		}
	}

	public ResultObject generateTrnECSDtlVO(Map<String, Object> inputMap, BigDecimal ECSCode, Long lLngECSId) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		TrnEcsDtl lobjTrnEcsDtl = new TrnEcsDtl();

		try {
			setSessionInfo(inputMap);
			TrnChequeDtlsDAO lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(TrnChequeDtls.class, serv.getSessionFactory());

			lobjTrnEcsDtl.setEcsDtlId(IFMSCommonServiceImpl.getNextSeqNum("trn_ecs_dtl", inputMap));
			lobjTrnEcsDtl.setEcsCode(ECSCode.longValue());
			lobjTrnEcsDtl.setEcsAttachmentId(BigDecimal.valueOf(lLngECSId));
			lobjTrnEcsDtl.setCreatedUserId(BigDecimal.valueOf(gLngUserId));
			lobjTrnEcsDtl.setCreatedPost(BigDecimal.valueOf(gLngPostId));
			lobjTrnEcsDtl.setCreatedDate(gDtCurDate);
			lobjTrnEcsDtl.setUpdatedDate(gDtCurDate);
			lobjTrnEcsDtl.setUpdatedPost(BigDecimal.valueOf(gLngPostId));
			lobjTrnEcsDtl.setUpdatedUser(BigDecimal.valueOf(gLngUserId));
			lObjTrnChequeDtlsDAO.create(lobjTrnEcsDtl);

			gIntECSFlag = 1;
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error in : " + e, e);
			throw (e);
		}

		return resObj;
	}

	public void showECSTxtFile(Map<String, Object> inputMap) {

		HttpServletResponse response = (HttpServletResponse) inputMap.get("responseObj");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);
			String ECSCode = request.getParameter("ECSCode");
			File fFile = new File(ECSCode + ".txt");
			PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(serv.getSessionFactory());
			CmnAttdocMst lObjAttdocMst = null;
			CmnAttdocMstDAOImpl lObjAttdocMstDAOImpl = new CmnAttdocMstDAOImpl(CmnAttdocMst.class, serv.getSessionFactory());
			long lAttDocId = Long.valueOf(lObjPensionDao.getAttDocId(ECSCode));
			lObjAttdocMst = lObjAttdocMstDAOImpl.read(lAttDocId);
			byte[] buffer = new byte[lObjAttdocMst.getFinalAttachment().length];
			buffer = lObjAttdocMst.getFinalAttachment();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byteArrayOutputStream.write(buffer, 0, buffer.length);
			buffer = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
			response.addHeader("Content-Disposition", "attachment;filename=\"" + fFile.getName() + "\"");
			response.addHeader("Content-Transfer-Encoding", "binary");
			response.setContentType("application/octet-stream");
			response.setContentLength(buffer.length);
			response.getOutputStream().write(buffer);
			response.getOutputStream().flush();

		} catch (Exception e) {
			gLogger.error("Error" + e, e);
			// e.printStackTrace();
		}
	}

	public ResultObject getAllECSForAuthorization(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(TrnEcsDtl.class, srvcLoc.getSessionFactory());
		List lLstECSFiles = new ArrayList();
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrRoleId = "";
		String lStrAuthFlag = null;
		int totalRecords = 0;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String lStrElementCode = null;
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
		try {
			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			inputMap.put("currdate", lObjSimpleDateFormat.parse(lObjSimpleDateFormat.format(gDtCurDate)));
			lStrAuthFlag = ((StringUtility.getParameter("authFlag", request).trim()).length() > 0) ? StringUtility.getParameter("authFlag", request).trim() : null;
			if (lStrAuthFlag != null && lStrAuthFlag.length() > 0) {
				inputMap.put("authFlag", lStrAuthFlag);
			}
			// lStrRoleId = lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			lStrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			inputMap.put("currRole", lStrRoleId);

			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			totalRecords = lObjPensionDao.getECSForAuthorizationCount(gStrLocationCode, lStrRoleId, lStrAuthFlag);

			if (totalRecords > 0) {
				lLstECSFiles = lObjPensionDao.getECSForAuthorization(displayTag, gStrLocationCode, lStrRoleId, lStrAuthFlag);
			}

			if (lLstECSFiles != null && lLstECSFiles.size() > 0) {
				inputMap.put("lLstECSFiles", lLstECSFiles);
			}

			inputMap.put("totalRecords", totalRecords);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ECSFiles");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}

		return resObj;
	}

	public ResultObject forwardECSForAuthorization(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrChequeNo = null;
		String[] lArrChequeNo = null;
		int flag = 0;
		List lLstChequeNo = new ArrayList();
		String lStrReturnECSCode = "";

		try {
			lStrChequeNo = StringUtility.getParameter("ecsCodeString", request);
			lArrChequeNo = lStrChequeNo.split("~");

			for (int i = 0; i < lArrChequeNo.length; i++) {
				lLstChequeNo.add(lArrChequeNo[i]);
				if (flag == 0) {
					lStrReturnECSCode = lStrReturnECSCode + lArrChequeNo[i];
					flag = 1;
				} else {
					lStrReturnECSCode = lStrReturnECSCode + "," + lArrChequeNo[i];
				}
			}

			lObjPensionDao.updateStatusInChqDtls(lLstChequeNo, DBConstants.ST_ECS_FRWD, gLngPostId, gLngUserId, gDtCurDate);

			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append(" <ECSCode> ");
			lStrBldXML.append(lStrReturnECSCode);
			lStrBldXML.append(" </ECSCode> ");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}

		return resObj;
	}

	public ResultObject authorizeECS(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrAdviceNo = null;
		String lStrAuthDate = null;
		String lStrChequeNo = null;
		String[] lArrAdviceNo = null;
		String[] lArrAuthDate = null;
		String[] lArrChequeNo = null;
		String[] lArrSettDate = null;
		new ArrayList();
		String lStrReturnECSCode = "";
		Date lDtAuthDate = null;
		Date lDtSettDate = null;
		String lStrSettDate = null;
		int flag = 0;

		try {
			lStrAdviceNo = StringUtility.getParameter("ecsCodeString", request);
			lStrAuthDate = StringUtility.getParameter("ecsAuthDateString", request);
			lStrChequeNo = StringUtility.getParameter("ecsChequeNoString", request);
			lStrSettDate = StringUtility.getParameter("ecsSettlementDate", request);
			lArrAdviceNo = lStrAdviceNo.split("~");
			lArrAuthDate = lStrAuthDate.split("~");
			lArrChequeNo = lStrChequeNo.split("~");
			lArrSettDate = lStrSettDate.split("~");
			for (int i = 0; i < lArrAdviceNo.length; i++) {

				lDtAuthDate = simpleDateFormat.parse(lArrAuthDate[i].toString().trim());
				lDtSettDate = simpleDateFormat.parse(lArrSettDate[i].toString().trim());
				lObjPensionDao.updateDateInChequeDtls(lArrAdviceNo[i].toString(), lDtAuthDate, lArrChequeNo[i].toString(), gLngPostId, gLngUserId, gDtCurDate, lDtSettDate);

				List<Long> lLstBillNo = new ArrayList<Long>();

				lLstBillNo = lObjPensionDao.getBillNosFromAdviceNo(lArrAdviceNo[i].toString());

				lObjPensionDao.updateECSRqstHdrDtls(lLstBillNo, DBConstants.ST_BILL_ECS_AUTH, gLngPostId, gLngUserId, gDtCurDate);

				if (flag == 0) {
					lStrReturnECSCode = lStrReturnECSCode + lArrAdviceNo[i];
					flag = 1;
				} else {
					lStrReturnECSCode = lStrReturnECSCode + "," + lArrAdviceNo[i];
				}

			}

			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append(" <ECSCode> ");
			lStrBldXML.append(lStrReturnECSCode);
			lStrBldXML.append(" </ECSCode> ");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}

		return resObj;
	}

	public ResultObject discardECS(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrECSCode = null;
		String lStrChequeId = null;
		String[] lArrECSCode = null;
		int flag = 0;
		String lStrReturnECSCode = "";
		new ArrayList();
		List lLstChequeNo = new ArrayList();
		List lLstBills = new ArrayList();
		try {
			lStrECSCode = StringUtility.getParameter("ecsCodeString", request);
			lStrChequeId = StringUtility.getParameter("chequeNoString", request);
			lArrECSCode = lStrECSCode.split("~");
			lStrChequeId.split("~");

			for (int i = 0; i < lArrECSCode.length; i++) {

				lLstBills = lObjPensionDao.getBillNosFromECSCode(lArrECSCode[i].toString(), gStrLocationCode);
				lObjPensionDao.updateECSRqstHdrDtls(lLstBills, DBConstants.ST_BILL_FORW_TO_USER, gLngPostId, gLngUserId, gDtCurDate);
				lLstChequeNo.add(lArrECSCode[i]);
				// lObjPensionDao.deleteTrnEcsDtl(lArrECSCode[i]);
				// lObjPensionDao.deleteRltBillCheque(lArrChequeId[i]);
				// lObjPensionDao.deleteTrnChequeDtls(lArrChequeId[i]);

				if (flag == 0) {
					lStrReturnECSCode = lStrReturnECSCode + lArrECSCode[i];
					flag = 1;
				} else {
					lStrReturnECSCode = lStrReturnECSCode + "," + lArrECSCode[i];
				}

			}
			lObjPensionDao.updateStatusInChqDtls(lLstChequeNo, DBConstants.ST_ECS_DISCARD, gLngPostId, gLngUserId, gDtCurDate);
			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append(" <ECSCode> ");
			lStrBldXML.append(lStrReturnECSCode);
			lStrBldXML.append(" </ECSCode> ");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}

		return resObj;
	}

	public ResultObject discardECS1(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrECSCode = null;
		String lStrChequeId = null;
		String[] lArrECSCode = null;
		String[] lArrChequeId = null;

		int flag = 0;
		String lStrReturnECSCode = null;
		List lLstBills = new ArrayList();

		try {
			lStrECSCode = StringUtility.getParameter("ecsCodeString", request);
			lStrChequeId = StringUtility.getParameter("chequeNoString", request);
			lArrECSCode = lStrECSCode.split("~");
			lArrChequeId = lStrChequeId.split("~");

			for (int i = 0; i < lArrECSCode.length; i++) {

				lLstBills = lObjPensionDao.getBillNosFromECSCode(lArrECSCode[i].toString(), gStrLocationCode);
				lObjPensionDao.updateECSRqstHdrDtls(lLstBills, DBConstants.ST_BILL_FORW_TO_USER, gLngPostId, gLngUserId, gDtCurDate);

				lObjPensionDao.deleteTrnEcsDtl(lArrECSCode[i]);
				lObjPensionDao.deleteRltBillCheque(lArrChequeId[i]);
				lObjPensionDao.deleteTrnChequeDtls(lArrChequeId[i]);

				if (flag == 0) {
					lStrReturnECSCode = lStrReturnECSCode + lArrECSCode[i];
					flag = 1;
				} else {
					lStrReturnECSCode = lStrReturnECSCode + "," + lArrECSCode[i];
				}

			}

			StringBuilder lStrBldXML = new StringBuilder();
			lStrBldXML.append(" <ECSCode> ");
			lStrBldXML.append(lStrReturnECSCode);
			lStrBldXML.append(" </ECSCode> ");
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}

		return resObj;
	}

	public ResultObject getRemarksOfBill(Map<String, Object> inputMap) {

		gLogger.info("In getRemarksOfBill method.......");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		PensionBillDAO lObjPensionDao = null;
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		List lLstTotalUsers = null;
		Long lLngBillNo = null;

		try {
			setSessionInfo(inputMap);
			lLngBillNo = Long.valueOf(StringUtility.getParameter("BillNo", request).trim());

			lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
			if (lLngBillNo != null) {
				lLstTotalUsers = lObjPensionDao.getRemarksOfBill(lLngBillNo, gStrLocationCode, gLngLangId);
				inputMap.put("lLngBillNo", lLngBillNo);
				inputMap.put("lLstTotalUsers", lLstTotalUsers);
				objRes.setViewName("ShowRemarksPopUp");
			} else {
				objRes.setViewName("ShowRemarks");
			}
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}
		return objRes;

	}

	public ResultObject loadSuppBillData(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List<CmnLookupMst> lLstRecoveryType = new ArrayList();
		List<ComboValuesVO> lLstBanks = null;
		String lStrCurrRoleId = null;
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrElementCode = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String lStrRoleId = "";
		String lStrRequestNo = "";
		String lStrBillType = "";
		String lStrUpdateReqFlag = "";
		String lStrCurrRole = "";
		try {
			setSessionInfo(inputMap);
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			lStrRequestNo = StringUtility.getParameter("requestNo", request).trim();
			lStrBillType = StringUtility.getParameter("billType", request).trim();
			lStrUpdateReqFlag = StringUtility.getParameter("updateReqFlag", request).trim(); 
			CommonDAO lObjCommonDAO = new CommonDAOImpl(serv.getSessionFactory());
			lLstRecoveryType = IFMSCommonServiceImpl.getLookupValues("RECVTYPE", gLngLangId, inputMap);
			lLstBanks = lObjCommonDAO.getBankList(inputMap, gLngLangId);

			OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(TrnEcsDtl.class, serv.getSessionFactory());
			if(!"".equals(lStrElementCode))
			{
				lStrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			}
			else
			{
				lStrRoleId = StringUtility.getParameter("CurrRole", request).trim();
			}
			// lStrCurrRoleId =
			// lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			inputMap.put("currRole", lStrRoleId);
			inputMap.put("RecoveryType", lLstRecoveryType);
			inputMap.put("BankList", lLstBanks);
			inputMap.put("RequestNo", lStrRequestNo);
			inputMap.put("SuppBillType", lStrBillType);
			inputMap.put("UpdateReqFlag", lStrUpdateReqFlag);
			inputMap.put("CurrentDate", lObjSimpleDateFormat.format(gDtCurDate));

			if(!"".equals(lStrRequestNo))
			{
				objRes.setViewName("SupplementaryBill");
			}else
			{
				objRes.setViewName("SupplementaryBills");
			}
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");

		}

		return objRes;
	}

	// validateChequeNo
	public ResultObject validateChequeNo(Map inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);
		String lStrChequeNo = null;
		String lStrRowNum = null;
		String[] lArrChequeNo = null;
		List<String> lLstChequeNo = null;
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		StringBuilder lStrBldXML = new StringBuilder();
		boolean flag = false;
		try {

			if (StringUtility.getParameter("chequeNoString", request) != null) {
				lStrChequeNo = StringUtility.getParameter("chequeNoString", request);
				lArrChequeNo = lStrChequeNo.split("~");

			}

			for (int i = 0; i < lArrChequeNo.length; i++) {
				lStrChequeNo = lArrChequeNo[i].substring(0, lArrChequeNo[i].indexOf("_"));
				lStrRowNum = lArrChequeNo[i].substring(lArrChequeNo[i].indexOf("_") + 1);
				lLstChequeNo = lObjPensionDao.isChequeNoExsist(lStrChequeNo, gStrLocationCode);
				if (!lLstChequeNo.isEmpty()) {
					flag = true;
					break;
				}
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<CHEQUENO>" + lStrChequeNo);
			lStrBldXML.append("</CHEQUENO>");
			lStrBldXML.append("<ROWNUM>" + lStrRowNum);
			lStrBldXML.append("</ROWNUM>");
			lStrBldXML.append("<ISEXISTS>");
			lStrBldXML.append(flag);
			lStrBldXML.append("</ISEXISTS>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			// e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	public ResultObject saveChequeNos(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrChequeBill = null;
		String[] lArrChequeBill = null;
		TrnChequeDtls lObjTrnChequeDtls = null;
		Long lLngChequeId = null;
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrBillNo = null;
		TrnChequeDtlsDAO lObjTrnChequeDtlsDAO = null;
		RltBillCheque lObjRltBillCheque = null;
		Long lLngBillChequeId = null;
		StringBuilder lStrBldXML = new StringBuilder();
		String lStrChequeDate = null;
		Date lDtChequeDate = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		PensionBillDAOImpl lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
		List lLstBillNo = null;
		String flag = "Y";
		String lStrRowNum = "";
		String[] lArrChequeNo = null;
		String[] lArrPartyName = null;
		String[] lArrPartyAmt = null;
		try {
			if (StringUtility.getParameter("billChequeString", request) != null) {
				lStrChequeBill = StringUtility.getParameter("billChequeString", request);
				lArrChequeBill = lStrChequeBill.split("~");
			}
			lLstBillNo = new ArrayList();
			for (int i = 0; i < lArrChequeBill.length; i++) {
				lObjTrnChequeDtls = new TrnChequeDtls();
				lObjRltBillCheque = new RltBillCheque();
				lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(TrnChequeDtls.class, serv.getSessionFactory());
				lStrBillNo = lArrChequeBill[i].substring(0, lArrChequeBill[i].indexOf("_"));
				lLstBillNo.add(lStrBillNo);
				lStrRowNum = lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("_") + 1);
				lArrChequeNo = StringUtility.getParameterValues("txtChequeNo_" + lStrRowNum, request);
				lArrPartyName = StringUtility.getParameterValues("hdPartyName_" + lStrRowNum, request);
				lArrPartyAmt = StringUtility.getParameterValues("hdChequeAmt_" + lStrRowNum, request);
				lStrChequeDate = StringUtility.getParameter("txtChqDate_" + lStrRowNum, request);
				if (lStrChequeDate != null && lStrChequeDate.length() > 0) {
					lDtChequeDate = lObjSimpleDateFormat.parse(lStrChequeDate);
				}
				for (int j = 0; j < lArrChequeNo.length; j++) {

					lObjTrnChequeDtls = new TrnChequeDtls();
					lObjRltBillCheque = new RltBillCheque();
					lLngChequeId = IFMSCommonServiceImpl.getNextSeqNum("trn_cheque_dtls", inputMap);
					lObjTrnChequeDtls.setChequeId(lLngChequeId);
					lObjTrnChequeDtls.setChequeAmt(new BigDecimal(lArrPartyAmt[j]));
					lObjTrnChequeDtls.setFromDt(lDtChequeDate);
					lObjTrnChequeDtls.setPartyName(lArrPartyName[j]);
					lObjTrnChequeDtls.setChequeNo(Long.parseLong(lArrChequeNo[j]));
					lObjTrnChequeDtls.setStatus(DBConstants.ST_ECS_AUTH);
					lObjTrnChequeDtls.setCreatedUserId(gLngUserId);
					lObjTrnChequeDtls.setCreatedPostId(gLngPostId);
					lObjTrnChequeDtls.setCreatedDate(gDtCurDate);
					lObjTrnChequeDtls.setLocationCode(gStrLocationCode);
					lObjTrnChequeDtls.setChequeType(bundle.getString("PPMT.PAYMODECHQ"));
					lObjTrnChequeDtlsDAO.create(lObjTrnChequeDtls);

					lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(RltBillCheque.class, serv.getSessionFactory());
					lLngBillChequeId = IFMSCommonServiceImpl.getNextSeqNum("rlt_bill_cheque", inputMap);
					lObjRltBillCheque.setBillChequeId(lLngBillChequeId);
					lObjRltBillCheque.setChequeId(lLngChequeId);
					lObjRltBillCheque.setBillNo(Long.parseLong(lStrBillNo));
					lObjRltBillCheque.setCreatedUserId(gLngUserId);
					lObjRltBillCheque.setCreatedPostId(gLngPostId);
					lObjRltBillCheque.setCreatedDate(gDtCurDate);
					lObjRltBillCheque.setLocationCode(gStrLocationCode);
					lObjRltBillCheque.setPartyAmt(new BigDecimal(lArrPartyAmt[j]));
					lObjTrnChequeDtlsDAO.create(lObjRltBillCheque);
				}

			}
			// for (int i = 0; i < lArrChequeBill.length; i++) {
			//
			// lObjTrnChequeDtls = new TrnChequeDtls();
			// lObjRltBillCheque = new RltBillCheque();
			// lObjTrnChequeDtlsDAO = new
			// TrnChequeDtlsDAOImpl(TrnChequeDtls.class,
			// serv.getSessionFactory());
			//
			// lStrBillNo = lArrChequeBill[i].substring(0,
			// lArrChequeBill[i].indexOf("_"));
			// lLstBillNo.add(lStrBillNo);
			// lStrChequeNo =
			// lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("_") + 1,
			// lArrChequeBill[i].indexOf("-"));
			// lStrPartyAmt =
			// lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("-") + 1,
			// lArrChequeBill[i].indexOf("*"));
			// lStrPartyName =
			// lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("*") + 1,
			// lArrChequeBill[i].indexOf("#"));
			// lStrChequeDate =
			// lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("#") + 1);
			// lDtChequeDate = lObjSimpleDateFormat.parse(lStrChequeDate);
			//
			// lLngChequeId =
			// IFMSCommonServiceImpl.getNextSeqNum("trn_cheque_dtls", inputMap);
			// lObjTrnChequeDtls.setChequeId(lLngChequeId);
			// lObjTrnChequeDtls.setChequeAmt(new BigDecimal(lStrPartyAmt));
			// lObjTrnChequeDtls.setFromDt(lDtChequeDate);
			// lObjTrnChequeDtls.setPartyName(lStrPartyName);
			// lObjTrnChequeDtls.setChequeNo(Long.parseLong(lStrChequeNo));
			// lObjTrnChequeDtls.setStatus(DBConstants.ST_ECS_AUTH);
			// lObjTrnChequeDtls.setCreatedUserId(gLngUserId);
			// lObjTrnChequeDtls.setCreatedPostId(gLngPostId);
			// lObjTrnChequeDtls.setCreatedDate(gDtCurDate);
			// lObjTrnChequeDtls.setLocationCode(gStrLocationCode);
			// lObjTrnChequeDtls.setChequeType(bundle.getString("PPMT.PAYMODECHQ"));
			// lObjTrnChequeDtlsDAO.create(lObjTrnChequeDtls);
			//
			// lObjTrnChequeDtlsDAO = new
			// TrnChequeDtlsDAOImpl(RltBillCheque.class,
			// serv.getSessionFactory());
			// lLngBillChequeId =
			// IFMSCommonServiceImpl.getNextSeqNum("rlt_bill_cheque", inputMap);
			// lObjRltBillCheque.setBillChequeId(lLngBillChequeId);
			// lObjRltBillCheque.setChequeId(lLngChequeId);
			// lObjRltBillCheque.setBillNo(Long.parseLong(lStrBillNo));
			// lObjRltBillCheque.setCreatedUserId(gLngUserId);
			// lObjRltBillCheque.setCreatedPostId(gLngPostId);
			// lObjRltBillCheque.setCreatedDate(gDtCurDate);
			// lObjRltBillCheque.setLocationCode(gStrLocationCode);
			// lObjRltBillCheque.setPartyAmt(new BigDecimal(lStrPartyAmt));
			// lObjTrnChequeDtlsDAO.create(lObjRltBillCheque);
			//
			// }

			lObjPensionBillDAO.updateECSRqstHdrDtls(lLstBillNo, DBConstants.ST_BILL_ECS_AUTH, gLngPostId, gLngUserId, gDtCurDate);
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<SAVED>");
			lStrBldXML.append(flag);
			lStrBldXML.append("</SAVED>");

			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			// System.out.println("The string is " + lStrResult);
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			// e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject saveCashPayment(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrChequeBill = null;
		String[] lArrChequeBill = null;
		TrnChequeDtls lObjTrnChequeDtls = null;
		Long lLngChequeId = null;
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrBillNo = null;
		TrnChequeDtlsDAO lObjTrnChequeDtlsDAO = null;
		RltBillCheque lObjRltBillCheque = null;
		Long lLngBillChequeId = null;
		StringBuilder lStrBldXML = new StringBuilder();
		String lStrPayDate = null;
		Date lDtChequeDate = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		PensionBillDAOImpl lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
		List lLstBillNo = null;
		String flag = "Y";
		String lStrRowNum = "";
		// String[] lArrChequeNo = null;
		String[] lArrPartyName = null;
		String[] lArrPartyAmt = null;
		try {
			if (StringUtility.getParameter("billChequeString", request) != null) {
				lStrChequeBill = StringUtility.getParameter("billChequeString", request);
				lArrChequeBill = lStrChequeBill.split("~");
			}
			lLstBillNo = new ArrayList();
			for (int i = 0; i < lArrChequeBill.length; i++) {
				lObjTrnChequeDtls = new TrnChequeDtls();
				lObjRltBillCheque = new RltBillCheque();
				lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(TrnChequeDtls.class, serv.getSessionFactory());
				lStrBillNo = lArrChequeBill[i].substring(0, lArrChequeBill[i].indexOf("_"));
				lLstBillNo.add(lStrBillNo);
				lStrRowNum = lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("_") + 1);
				lArrPartyName = StringUtility.getParameterValues("hdPartyName_" + lStrRowNum, request);
				lArrPartyAmt = StringUtility.getParameterValues("hdCashAmt_" + lStrRowNum, request);
				lStrPayDate = StringUtility.getParameter("txtPayDate_" + lStrRowNum, request);
				if (lStrPayDate != null && lStrPayDate.length() > 0) {
					lDtChequeDate = lObjSimpleDateFormat.parse(lStrPayDate);
				}
				for (int j = 0; j < lArrPartyAmt.length; j++) {

					lObjTrnChequeDtls = new TrnChequeDtls();
					lObjRltBillCheque = new RltBillCheque();
					lLngChequeId = IFMSCommonServiceImpl.getNextSeqNum("trn_cheque_dtls", inputMap);
					lObjTrnChequeDtls.setChequeId(lLngChequeId);
					lObjTrnChequeDtls.setChequeAmt(new BigDecimal(lArrPartyAmt[j]));
					lObjTrnChequeDtls.setPaymentDate(lDtChequeDate);
					lObjTrnChequeDtls.setPartyName(lArrPartyName[j]);
					// lObjTrnChequeDtls.setChequeNo(Long.parseLong(lArrChequeNo[j]));
					lObjTrnChequeDtls.setStatus(DBConstants.ST_ECS_AUTH);
					lObjTrnChequeDtls.setCreatedUserId(gLngUserId);
					lObjTrnChequeDtls.setCreatedPostId(gLngPostId);
					lObjTrnChequeDtls.setCreatedDate(gDtCurDate);
					lObjTrnChequeDtls.setLocationCode(gStrLocationCode);
					lObjTrnChequeDtls.setChequeType(bundle.getString("PPMT.PAYMODECASH"));
					lObjTrnChequeDtlsDAO.create(lObjTrnChequeDtls);

					lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(RltBillCheque.class, serv.getSessionFactory());
					lLngBillChequeId = IFMSCommonServiceImpl.getNextSeqNum("rlt_bill_cheque", inputMap);
					lObjRltBillCheque.setBillChequeId(lLngBillChequeId);
					lObjRltBillCheque.setChequeId(lLngChequeId);
					lObjRltBillCheque.setBillNo(Long.parseLong(lStrBillNo));
					lObjRltBillCheque.setCreatedUserId(gLngUserId);
					lObjRltBillCheque.setCreatedPostId(gLngPostId);
					lObjRltBillCheque.setCreatedDate(gDtCurDate);
					lObjRltBillCheque.setLocationCode(gStrLocationCode);
					lObjRltBillCheque.setPartyAmt(new BigDecimal(lArrPartyAmt[j]));
					lObjTrnChequeDtlsDAO.create(lObjRltBillCheque);
				}

			}
			// for (int i = 0; i < lArrChequeBill.length; i++) {
			//
			// lObjTrnChequeDtls = new TrnChequeDtls();
			// lObjRltBillCheque = new RltBillCheque();
			// lObjTrnChequeDtlsDAO = new
			// TrnChequeDtlsDAOImpl(TrnChequeDtls.class,
			// serv.getSessionFactory());
			//
			// lStrBillNo = lArrChequeBill[i].substring(0,
			// lArrChequeBill[i].indexOf("_"));
			// lLstBillNo.add(lStrBillNo);
			// lStrChequeNo =
			// lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("_") + 1,
			// lArrChequeBill[i].indexOf("-"));
			// lStrPartyAmt =
			// lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("-") + 1,
			// lArrChequeBill[i].indexOf("*"));
			// lStrPartyName =
			// lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("*") + 1,
			// lArrChequeBill[i].indexOf("#"));
			// lStrChequeDate =
			// lArrChequeBill[i].substring(lArrChequeBill[i].indexOf("#") + 1);
			// lDtChequeDate = lObjSimpleDateFormat.parse(lStrChequeDate);
			//
			// lLngChequeId =
			// IFMSCommonServiceImpl.getNextSeqNum("trn_cheque_dtls", inputMap);
			// lObjTrnChequeDtls.setChequeId(lLngChequeId);
			// lObjTrnChequeDtls.setChequeAmt(new BigDecimal(lStrPartyAmt));
			// lObjTrnChequeDtls.setFromDt(lDtChequeDate);
			// lObjTrnChequeDtls.setPartyName(lStrPartyName);
			// lObjTrnChequeDtls.setChequeNo(Long.parseLong(lStrChequeNo));
			// lObjTrnChequeDtls.setStatus(DBConstants.ST_ECS_AUTH);
			// lObjTrnChequeDtls.setCreatedUserId(gLngUserId);
			// lObjTrnChequeDtls.setCreatedPostId(gLngPostId);
			// lObjTrnChequeDtls.setCreatedDate(gDtCurDate);
			// lObjTrnChequeDtls.setLocationCode(gStrLocationCode);
			// lObjTrnChequeDtls.setChequeType(bundle.getString("PPMT.PAYMODECHQ"));
			// lObjTrnChequeDtlsDAO.create(lObjTrnChequeDtls);
			//
			// lObjTrnChequeDtlsDAO = new
			// TrnChequeDtlsDAOImpl(RltBillCheque.class,
			// serv.getSessionFactory());
			// lLngBillChequeId =
			// IFMSCommonServiceImpl.getNextSeqNum("rlt_bill_cheque", inputMap);
			// lObjRltBillCheque.setBillChequeId(lLngBillChequeId);
			// lObjRltBillCheque.setChequeId(lLngChequeId);
			// lObjRltBillCheque.setBillNo(Long.parseLong(lStrBillNo));
			// lObjRltBillCheque.setCreatedUserId(gLngUserId);
			// lObjRltBillCheque.setCreatedPostId(gLngPostId);
			// lObjRltBillCheque.setCreatedDate(gDtCurDate);
			// lObjRltBillCheque.setLocationCode(gStrLocationCode);
			// lObjRltBillCheque.setPartyAmt(new BigDecimal(lStrPartyAmt));
			// lObjTrnChequeDtlsDAO.create(lObjRltBillCheque);
			//
			// }

			lObjPensionBillDAO.updateECSRqstHdrDtls(lLstBillNo, DBConstants.ST_BILL_ECS_AUTH, gLngPostId, gLngUserId, gDtCurDate);
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<SAVED>");
			lStrBldXML.append(flag);
			lStrBldXML.append("</SAVED>");

			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			// System.out.println("The string is " + lStrResult);
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			// e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject getGeneratedCheques(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(TrnEcsDtl.class, srvcLoc.getSessionFactory());
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrCurrRoleId = null;
		List lLstCheque = new ArrayList();
		String lStrBillFlag = null;
		int totalRecords = 0;
		String lStrElementCode = null;
		CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
		String lStrRoleId = "";
		try {
			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			lStrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			// To get current users Role
			// ------Start------
			// lStrCurrRoleId =
			// lObjOnlinePensionCaseDAO.getRoleByPost(gLngPostId);
			inputMap.put("currRole", lStrRoleId);
			// ------End-----
			lStrBillFlag = ((StringUtility.getParameter("billFlag", request).trim()).length() > 0) ? StringUtility.getParameter("billFlag", request).trim() : null;
			if (lStrBillFlag != null && lStrBillFlag.length() > 0) {
				inputMap.put("billFlag", lStrBillFlag);
			}
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			totalRecords = lObjPensionDao.getAllChequesCount(gStrLocationCode);

			if (totalRecords > 0) {
				lLstCheque = lObjPensionDao.getAllCheques(displayTag, gStrLocationCode);
			}

			if (lLstCheque != null && lLstCheque.size() > 0) {
				inputMap.put("savedPensionBillsList", lLstCheque);
			}

			inputMap.put("totalRecords", totalRecords);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DraftBills");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}

		return resObj;
	}

	public ResultObject getSavedCashPayment(Map inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		OnlinePensionCaseDAO lObjOnlinePensionCaseDAO = new OnlinePensionCaseDAOImpl(TrnEcsDtl.class, srvcLoc.getSessionFactory());
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrCurrRoleId = null;
		List lLstCheque = new ArrayList();
		String lStrBillFlag = null;
		int totalRecords = 0;
		String lStrRoleId = "";
		String lStrElementCode = null;

		try {
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());
			// To get current users Role
			// ------Start------
			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			lStrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);

			inputMap.put("currRole", lStrRoleId);
			// ------End-----

			lStrBillFlag = ((StringUtility.getParameter("billFlag", request).trim()).length() > 0) ? StringUtility.getParameter("billFlag", request).trim() : null;
			if (lStrBillFlag != null && lStrBillFlag.length() > 0) {
				inputMap.put("billFlag", lStrBillFlag);
			}
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			totalRecords = lObjPensionDao.getAllCashPaymentCount(gStrLocationCode);

			if (totalRecords > 0) {
				lLstCheque = lObjPensionDao.getAllCashPayment(displayTag, gStrLocationCode);
			}

			if (lLstCheque != null && lLstCheque.size() > 0) {
				inputMap.put("savedPensionBillsList", lLstCheque);
			}

			inputMap.put("totalRecords", totalRecords);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DraftBills");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}

		return resObj;
	}

	public void discardChngStmnt(List<Long> lLstBillNo, Map inputMap) throws Exception {

		String lStrBankCode = "";
		String lStrBranchCode = "";
		Integer lIntForMonth = 0;
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, srvcLoc.getSessionFactory());
		try {
			List<Object[]> lLstBankDtlsOfMonthlyBill = lObjMonthlyDAO.getMonthlyBillDtlsByBillNo(lLstBillNo);
			for (Object[] lArrObj : lLstBankDtlsOfMonthlyBill) {
				lStrBankCode = (lArrObj[0] != null) ? lArrObj[0].toString() : "";
				lStrBranchCode = (lArrObj[1] != null) ? lArrObj[1].toString() : "";
				lIntForMonth = (lArrObj[2] != null) ? (Integer) (lArrObj[2]) : 0;
				if ((!"".equals(lStrBankCode) && !"".equals(lStrBranchCode)) && (lIntForMonth != 0)) {
					lObjMonthlyDAO.discardChngStmntOnMonthlyBillDiscard(lStrBankCode, lStrBranchCode, lIntForMonth);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			throw e;
		}
	}

	public ResultObject showVoucherDetails(Map inputMap) throws Exception {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrPPONo = StringUtility.getParameter("ppoNo", request);
		String lStrPnsnRqstId = StringUtility.getParameter("pnsnRqstId", request);
		String lStrPensionerDtlId = StringUtility.getParameter("pensionerDtlId", request);
		String lStrPensionerCode = StringUtility.getParameter("pensionerCode", request);
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		RltPensioncaseBillDAO lObjRltPensioncaseBillDAO = new RltPensioncaseBillDAOImpl(RltPensioncaseBill.class, srvcLoc.getSessionFactory());
		Long lLngBillNo = 0L;

		try {
			if (lStrPPONo.length() > 0) {
				lLngBillNo = lObjRltPensioncaseBillDAO.getBillNo(lStrPPONo, gStrLocId);
				inputMap.put("BillNo", lLngBillNo);
			}
			inputMap.put("pnsnRqstId", lStrPnsnRqstId);
			inputMap.put("pensionerDtlId", lStrPensionerDtlId);
			inputMap.put("PensionerCode", lStrPensionerCode);
			inputMap.put("currdate", lObjSimpleDateFormat.format(gDtCurDate));
			// lStrBldXML.append("<XMLDOC>");
			// lStrBldXML.append("<BILLNO>");
			// lStrBldXML.append(lLngBillNo);
			// lStrBldXML.append("</BILLNO>");
			// lStrBldXML.append("</XMLDOC>");
			//
			// gLogger.info(" lStrBldXML :: " + lStrBldXML);
			// String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
			// lStrBldXML.toString()).toString();
			// System.out.println("The string is " + lStrResult);
			// inputMap.put("ajaxKey", lStrResult);
			// resObj.setViewName("ajaxData");
			resObj.setViewName("VoucherPopup");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}
		return resObj;
	}

	public ResultObject saveVoucherDetails(Map inputMap) throws Exception {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String lStrVoucherNo = StringUtility.getParameter("voucherNo", request);
		String lStrVoucherDate = StringUtility.getParameter("voucherDate", request);
		String lStrBillNo = StringUtility.getParameter("billNo", request);
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		StringBuilder lStrBldXML = new StringBuilder();
		String flag = "Y";

		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {

			if (lStrVoucherNo.length() > 0 && lStrVoucherDate.length() > 0) {
				lObjPensionDao.saveVoucherDetails(Long.parseLong(lStrVoucherNo), lObjSimpleDateFormat.parse(lStrVoucherDate), gLngUserId, gLngPostId, gDtCurDate, Long.parseLong(lStrBillNo));
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<SAVED>");
			lStrBldXML.append(flag);
			lStrBldXML.append("</SAVED>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject validateAmntFrmMandate(Map inputMap) {

		// TODO Auto-generated method stub
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		setSessionInfo(inputMap);
		String lStrNetAmount = null;
		String lStrPPONo = null;
		String lStrMandateSerNo = null;
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		StringBuilder lStrBldXML = new StringBuilder();
		List lLstResultList = null;
		Double lDbNetAmount = null;
		Long lLngMandateSerialNo = null;
		String lStrValidationFlag = null;
		String[] lArrMandateNo = null;
		String[] lArrPPONo = null;
		String[] lArrNetAmount = null;
		String lStrNoOfRows = null;
		boolean flag = false;
		try {
			lStrValidationFlag = StringUtility.getParameter("validationFlag", request);
			if ("S".equals(lStrValidationFlag)) {
				lStrNetAmount = StringUtility.getParameter("txtAmount", request);
				lStrPPONo = StringUtility.getParameter("txtPPONo", request);
				lStrMandateSerNo = StringUtility.getParameter("txtMandateNo", request);
				if (!"".equals(lStrMandateSerNo)) {
					lLngMandateSerialNo = Long.parseLong(lStrMandateSerNo);
				}
				if (!"".equals(lStrNetAmount)) {
					lDbNetAmount = Double.parseDouble(lStrNetAmount);
				}

				lLstResultList = lObjPensionDao.getAllData(lLngMandateSerialNo, lDbNetAmount, lStrPPONo, gStrLocationCode);

				if (!lLstResultList.isEmpty()) {
					flag = true;
				}
				lStrBldXML.append("<XMLDOC>");
				lStrBldXML.append("<VALID>");
				lStrBldXML.append(flag);
				lStrBldXML.append("</VALID>");
				lStrBldXML.append("</XMLDOC>");
			} else if ("M".equals(lStrValidationFlag)) {

				lArrMandateNo = StringUtility.getParameterValues("txtMandateNo", request);
				lArrPPONo = StringUtility.getParameterValues("txtPPONo", request);
				lArrNetAmount = StringUtility.getParameterValues("txtAmount", request);
				lStrNoOfRows = StringUtility.getParameter("hidECSReturnDtlsGridSize", request);
				for (int i = 0; i < Integer.parseInt(lStrNoOfRows); i++) {
					lStrPPONo = lArrPPONo[i];
					if (!"".equals(lArrMandateNo[i])) {
						lLngMandateSerialNo = Long.parseLong(lArrMandateNo[i]);
					}
					if (!"".equals(lArrNetAmount[i])) {
						lDbNetAmount = Double.parseDouble(lArrNetAmount[i]);
					}

					lLstResultList = lObjPensionDao.getAllData(lLngMandateSerialNo, lDbNetAmount, lStrPPONo, gStrLocationCode);

					if (lLstResultList.isEmpty()) {
						flag = true;
						break;
					}

				}
				if (flag) {
					lStrBldXML.append("<XMLDOC>");
					lStrBldXML.append("<VALID>");
					lStrBldXML.append(false);
					lStrBldXML.append("</VALID>");
					lStrBldXML.append("<PPONO>");
					lStrBldXML.append(lStrPPONo);
					lStrBldXML.append("</PPONO>");
					lStrBldXML.append("</XMLDOC>");
				} else {
					lStrBldXML.append("<XMLDOC>");
					lStrBldXML.append("<VALID>");
					lStrBldXML.append(true);
					lStrBldXML.append("</VALID>");
					lStrBldXML.append("</XMLDOC>");
				}

			}
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			// e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}

	public ResultObject saveECSReturnedDtls(Map inputMap) throws Exception {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		String[] lArrMandateNo = null;
		String[] lArrPPONo = null;
		String[] lArrNetAmount = null;
		String[] lArrReason = null;
		String[] lArrPensionerName = null;
		String lStrNoOfRows = null;
		Double lDbNetAmount = null;
		Long lLngMandateSerialNo = null;
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		StringBuilder lStrBldXML = new StringBuilder();
		TrnChequeDtlsDAO lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(ReturnedEcsDtls.class, srvcLoc.getSessionFactory());
		List<Long> lLstHeaderIds = new ArrayList<Long>();
		List lLstResultList = null;
		PensionBillDAO lObjPensionDao = new PensionBillDAOImpl(srvcLoc.getSessionFactory());
		try {

			lArrMandateNo = StringUtility.getParameterValues("txtMandateNo", request);
			lArrPPONo = StringUtility.getParameterValues("txtPPONo", request);
			lArrNetAmount = StringUtility.getParameterValues("txtAmount", request);
			lArrReason = StringUtility.getParameterValues("cmbROF", request);
			lArrPensionerName = StringUtility.getParameterValues("txtPensionerName", request);

			lStrNoOfRows = StringUtility.getParameter("hidECSReturnDtlsGridSize", request);
			for (int i = 0; i < Integer.parseInt(lStrNoOfRows); i++) {
				ReturnedEcsDtls lObjReturnedEcsDtls = new ReturnedEcsDtls();
				Long returnedEcsDtlsId = IFMSCommonServiceImpl.getNextSeqNum("returned_ecs_dtls", inputMap);
				lObjReturnedEcsDtls.setReturnedEcsDtlsId(returnedEcsDtlsId);
				lObjReturnedEcsDtls.setPpoNo(lArrPPONo[i]);
				if (!"".equals(lArrMandateNo[i])) {
					lLngMandateSerialNo = Long.parseLong(lArrMandateNo[i]);
					lObjReturnedEcsDtls.setMandateSerialNo(lLngMandateSerialNo);
				}
				if (!"".equals(lArrNetAmount[i])) {
					lDbNetAmount = Double.parseDouble(lArrNetAmount[i]);
					lObjReturnedEcsDtls.setAmount(lDbNetAmount.longValue());
				}
				lObjReturnedEcsDtls.setPensionerName(lArrPensionerName[i]);
				lObjReturnedEcsDtls.setReason(lArrReason[i]);
				lObjReturnedEcsDtls.setLocationCode(gStrLocationCode);
				lObjReturnedEcsDtls.setCreatedUserId(gLngUserId);
				lObjReturnedEcsDtls.setCreatedPostId(gLngPostId);
				lObjReturnedEcsDtls.setCreatedDate(gDtCurDate);
				lObjReturnedEcsDtls.setDbId(gLngDBId);
				lObjTrnChequeDtlsDAO.create(lObjReturnedEcsDtls);

				lLstResultList = lObjPensionDao.getAllData(lLngMandateSerialNo, lDbNetAmount, lArrPPONo[i], gStrLocationCode);
				BigInteger lBgInt = (BigInteger) lLstResultList.get(0);
				lLstHeaderIds.add(lBgInt.longValue());

			}

			if (!lLstHeaderIds.isEmpty()) {
				lObjPensionDao.updateTrnPensionBillDtls(lLstHeaderIds);
			}
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<SAVED>");
			lStrBldXML.append("Y");
			lStrBldXML.append("</SAVED>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			// System.out.println("The string is " + lStrResult);
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}
		return resObj;
	}

	public ResultObject getPenNameFromPPONo(Map inputMap) throws Exception {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		StringBuilder lStrBldXML = new StringBuilder();
		String lStrPPONo = null;
		String lStrPensionerName = null;

		try {
			lStrPPONo = StringUtility.getParameter("PPONo", request);
			TrnPensionRqstHdrDAO lObjTrnPensionRqstHdrDAO = new TrnPensionRqstHdrDAOImpl(TrnPensionRqstHdr.class, srvcLoc.getSessionFactory());
			lStrPensionerName = lObjTrnPensionRqstHdrDAO.getPensionerName(lStrPPONo);
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<PENSIONERNAME>");
			lStrBldXML.append(lStrPensionerName);
			lStrBldXML.append("</PENSIONERNAME>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			// System.out.println("The string is " + lStrResult);
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			// e.printStackTrace();
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.PensionBillService#getBillVoucherMappingList
	 * (java.util.Map)
	 */
	public ResultObject getBillVoucherMappingList(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrElementCode = null;
		String lStrCurrRoleId = null;
		int lIntTotalRecords = 0;
		try {
			CommonPensionDAO lObjCommonPensionDAO = new CommonPensionDAOImpl(serv.getSessionFactory());

			lStrElementCode = StringUtility.getParameter("elementId", request).trim();
			if (!"".equals(lStrElementCode)) {
				lStrCurrRoleId = lObjCommonPensionDAO.getRoleByElement(lStrElementCode);
			} else {
				lStrCurrRoleId = StringUtility.getParameter("currRole", request).trim();
			}

			List<SavedPensionBillVO> lLstResult = new ArrayList<SavedPensionBillVO>();

			PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			lIntTotalRecords = lObjPensionBillDAO.getBillVoucherMappingCount(gStrLocationCode, gLngPostId);
			if (lIntTotalRecords > 0) {
				lLstResult = lObjPensionBillDAO.getBillVoucherMappingList(gStrLocationCode, gLngPostId);
			}
			inputMap.put("currRole", lStrCurrRoleId);
			inputMap.put("totalRecords", lIntTotalRecords);
			inputMap.put("lLstResult", lLstResult);

			resObj.setViewName("BillVoucherMapping");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is;" + e, e);
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
	 * com.tcs.sgv.pensionpay.service.PensionBillService#updateBillVoucherMpgDtls
	 * (java.util.Map)
	 */
	public ResultObject updateBillVoucherMpgDtls(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		StringBuilder lStrBldXML = new StringBuilder();
		String lStrBillVoucherString = null;
		TrnBillRegister lObjTrnBillRegister = null;
		RltBillParty lObjRltBillParty = null;
		HstCommutationDtls lObjHstCommutationDtls = null;
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtls = null;
		List lLstResult = new ArrayList();
		
		String lStrBillNo = null;
		String lStrVoucherNo = null;
		try {
			String[] lArrStrChkBillNo = StringUtility.getParameterValues("billNo", request);
			String[] lArrStrChequeNo = null;
			String[] lArrStrBillPartyId = null;
			String lStrRowCnt = "";
			TrnChequeDtlsDAO lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(TrnBillRegister.class, serv.getSessionFactory());

			for (int lIntCnt = 0; lIntCnt < lArrStrChkBillNo.length; lIntCnt++) {
				lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(TrnBillRegister.class, serv.getSessionFactory());
				lStrRowCnt = lArrStrChkBillNo[lIntCnt].substring(lArrStrChkBillNo[lIntCnt].indexOf("_"));
				String[] lArrStrVoucherNo = StringUtility.getParameterValues("txtVoucherNo" + lStrRowCnt, request);
				String[] lArrStrVoucherDate = StringUtility.getParameterValues("txtVoucherDate" + lStrRowCnt, request);
				lObjTrnBillRegister = new TrnBillRegister();
				lStrBillNo = lArrStrChkBillNo[lIntCnt].substring(0, lArrStrChkBillNo[lIntCnt].indexOf("_"));
				lObjTrnBillRegister = (TrnBillRegister) lObjTrnChequeDtlsDAO.read(Long.valueOf(lStrBillNo));
				if (!"".equals(lArrStrVoucherNo[0])) {
					lObjTrnBillRegister.setVoucherNo(Long.valueOf(lArrStrVoucherNo[0]));
				}
				if (!"".equals(lArrStrVoucherDate[0])) {
					lObjTrnBillRegister.setVoucherDate(lObjSimpleDateFormat.parse(lArrStrVoucherDate[0]));
				}
				lObjTrnChequeDtlsDAO.update(lObjTrnBillRegister);
				lArrStrChequeNo = StringUtility.getParameterValues("txtChequeNo" + lStrRowCnt, request);
				lArrStrBillPartyId = StringUtility.getParameterValues("hdnBillPartyId" + lStrRowCnt, request);
				lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(HstCommutationDtls.class, serv.getSessionFactory());
				Long lLngSubjectId = lObjTrnBillRegister.getSubjectId();
				if("11".equals(lLngSubjectId.toString()))
				{
					lLstResult = lObjTrnChequeDtlsDAO.getListByColumnAndValue("billNo",Long.valueOf(lStrBillNo));
					lObjHstCommutationDtls = (HstCommutationDtls) lLstResult.get(0);
					lObjHstCommutationDtls.setChequeNo(lArrStrChequeNo[0]);
					lObjHstCommutationDtls.setChequeDate(lObjSimpleDateFormat.parse(lArrStrVoucherDate[0]));
					lObjTrnChequeDtlsDAO.update(lObjHstCommutationDtls);
				}
				lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(HstPnsnPmntDcrgDtls.class, serv.getSessionFactory());
				if("10".equals(lLngSubjectId.toString()))
				{
					lLstResult = lObjTrnChequeDtlsDAO.getListByColumnAndValue("billNo",Long.valueOf(lStrBillNo));
					lObjHstPnsnPmntDcrgDtls = (HstPnsnPmntDcrgDtls) lLstResult.get(0);
					lObjHstPnsnPmntDcrgDtls.setChequeNo(lArrStrChequeNo[0]);
					lObjHstPnsnPmntDcrgDtls.setChequeDate(lObjSimpleDateFormat.parse(lArrStrVoucherDate[0]));
					lObjTrnChequeDtlsDAO.update(lObjHstPnsnPmntDcrgDtls);
				}
				lObjTrnChequeDtlsDAO = new TrnChequeDtlsDAOImpl(RltBillParty.class, serv.getSessionFactory());
				
				for (int lIntRow = 0; lIntRow < lArrStrBillPartyId.length; lIntRow++) {
					lObjRltBillParty = new RltBillParty();
					lObjRltBillParty = (RltBillParty) lObjTrnChequeDtlsDAO.read(Long.parseLong(lArrStrBillPartyId[lIntRow]));
					if (!"".equals(lArrStrChequeNo[lIntRow])) {
						lObjRltBillParty.setChequeNo(Long.parseLong(lArrStrChequeNo[lIntRow]));
					}
					if (!"".equals(lArrStrVoucherDate[0])) {
						lObjRltBillParty.setChequeDate(lObjSimpleDateFormat.parse(lArrStrVoucherDate[0]));
					}
					lObjTrnChequeDtlsDAO.update(lObjRltBillParty);
				}
			}

			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append("Bill - Voucher Mapped Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
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
	 * com.tcs.sgv.pensionpay.service.PensionBillService#archieveBillAfterVoucherMpg
	 * (java.util.Map)
	 */
	public ResultObject archieveBillAfterVoucherMpg(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
		StringBuilder lStrBldXML = new StringBuilder();
		String lStrBillNoString = null;
		String lStrBillNo = null;
		List<Long> lLstBillNo = new ArrayList<Long>();
		try {
			lStrBillNoString = StringUtility.getParameter("billNoString", request);

			PensionBillDAO lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());
			if (!"".equals(lStrBillNoString)) {

				String[] lArrStrBillNo = lStrBillNoString.split("~");

				for (int lIntCnt = 0; lIntCnt < lArrStrBillNo.length; lIntCnt++) {
					lLstBillNo.add(Long.valueOf(lArrStrBillNo[lIntCnt].substring(0, lArrStrBillNo[lIntCnt].indexOf("_"))));
				}
				lObjPensionBillDAO.archieveBill(lLstBillNo, gStrLocationCode);
			}
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<MESSAGE>");
			lStrBldXML.append("Bills Archieved Successfully.");
			lStrBldXML.append("</MESSAGE>");
			lStrBldXML.append("</XMLDOC>");

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject viewPensionOuterBill(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		PensionBillDAO lObjPensionBillDAO = null;
		List<String> lLstSchemeCodes = null;
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSmplDateFormat = new SimpleDateFormat("MMMM yyyy ");

		String lStrTreasuryName = null;
		List<Object[]> lLstPensionBillOuterDtls = null;
		Map<String, List<TrnPensionRecoveryDtls>> lMapRecoveryDtls = new HashMap<String, List<TrnPensionRecoveryDtls>>();
		List<Object[]> lLstRecoveryDtls = null;
		List<String> lLstPensionerCode = new ArrayList<String>();
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsVO = new ArrayList<TrnPensionRecoveryDtls>();
		StringBuilder lSbHeader = new StringBuilder();
		StringBuilder lSbFooter = new StringBuilder();
		String lStrPensionOuterBill = "";
		String lStrRecoveryDtls = "";
		String lStrMajorHead = "";
		String lStrSubMajorHead = "";
		String lStrMinorHead = "";
		String lStrSubMinorHead = "";
		String lStrSubHead = "";
		String lStrDemandCode = "";
		String lStrCharged = "";
		String lStrPlan = "";
		String lStrFooter = "";
		String lStrNetAmount = "";
		Double lDbNetBillAmt = 0D;
		Double lDbDeductionAmt = 0D;
		String lStrForMonthYear = "";
		String lStrBillDate = "";
		Date lDtBillDate = null;
		String lStrPensionerCode = "";
		String lStrBillFlag = "";
		String lineSeperator = "\r\n";
		Integer lIntNoOfLinesPrintedOnPage = 0;
		try {
			setSessionInfo(inputMap);
			String lStrBillNo = StringUtility.getParameter("billNo", request);
			lStrBillFlag = StringUtility.getParameter("billFlag", request);
			// String lStrForMonthYear =
			// StringUtility.getParameter("forMonthYear", request);
			// String lStrBillDate = StringUtility.getParameter("billDate",
			// request);
			// Date lDtBillDate = lObjSimpleDateFormat.parse(lStrBillDate);

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			MonthlyPensionBillDAOImpl lObjMonthlyDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());
			SupplementaryBillDAO lObjSupplementaryBillDAO = new SupplementaryBillDAOImpl(TrnPensionRecoveryDtls.class, serv.getSessionFactory());
			lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());

			lLstPensionBillOuterDtls = lObjPensionBillDAO.getPensionOuterBillData(lStrBillNo, gStrLocationCode);
			ArrayList inList = new ArrayList();

			if (!lLstPensionBillOuterDtls.isEmpty()) {
				lStrTreasuryName = lObjPhysicalCaseInwardDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocationCode));
				ColumnVo[] columnHeading = new ColumnVo[6];
				columnHeading[0] = new ColumnVo("Bank Name", 1, 32, 0, true, false, true);
				columnHeading[1] = new ColumnVo("Branch Name", 1, 32, 0, true, false, true);
				columnHeading[2] = new ColumnVo("No of    Pensioner", 1, 9, 0, true, false, true);
				columnHeading[3] = new ColumnVo("Gross", 2, 17, 1, false, false, true);
				columnHeading[4] = new ColumnVo("Deduction", 2, 17, 1, false, false, true);
				columnHeading[5] = new ColumnVo("Net", 2, 17, 1, false, false, true);

				List<List> arrOuter = new ArrayList<List>();
				ReportExportHelper lObjExport = new ReportExportHelper();

				for (int lIntCnt = 0; lIntCnt < lLstPensionBillOuterDtls.size(); lIntCnt++) {
					Object[] obj = lLstPensionBillOuterDtls.get(lIntCnt);
					if (lIntCnt == 0) {
						if (obj[17] != null) {
							lStrForMonthYear = obj[17].toString();
							lStrBillDate = "01/" + lStrForMonthYear.substring(4) + "/" + lStrForMonthYear.substring(0, 4);
							lDtBillDate = lObjSimpleDateFormat.parse(lStrBillDate);
						}
						lSbHeader.append("MTR 37");
						lSbHeader.append("\r\n");
						lSbHeader.append("2071 FIRST PAYMENT PENSION BILL");
						lSbHeader.append("\r\n");

						if (obj[9] != null) {
							lStrMajorHead = obj[9].toString();
						}
						if (obj[10] != null) {
							lStrSubMajorHead = obj[10].toString();
						}
						if (obj[11] != null) {
							lStrMinorHead = obj[11].toString();
						}
						if (obj[12] != null) {
							lStrSubMinorHead = obj[12].toString();
						}
						if (obj[13] != null) {
							lStrSubHead = obj[13].toString();
						}
						if (obj[14] != null) {
							lStrDemandCode = obj[14].toString();
						}
						if (obj[15] != null) {
							if ("C".equals(obj[15])) {
								lStrCharged = "Charged";
							} else if ("V".equals(obj[15])) {
								lStrCharged = "Voted";
							}
						}
						if (obj[16] != null) {
							if ("P".equals(obj[16])) {
								lStrPlan = "Plan";
							} else if ("N".equals(obj[16])) {
								lStrPlan = "Non Plan";
							}
						}

						lSbHeader.append(String.format("%16s", "Charged/Voted : " + lStrCharged + "   "));
						lSbHeader.append("Treasury Name : " + lStrTreasuryName);
						lSbHeader.append("\r\n");
						lSbHeader.append(String.format("%16s", "Plan/Non Plan : " + lStrPlan + "   "));
						lSbHeader.append(String.format("%-85s", "Scheme Descr. " + obj[3]));
						lSbHeader.append("\r\n");
						lSbHeader.append(String.format("%30s", "Bill No  : " + obj[1] + "    "));
						lSbHeader.append(String.format("%-95s", "Date   : " + lStrBillDate));
						lSbHeader.append("\r\n");
						lSbHeader.append(String.format("%40s", "Scheme Code : " + obj[2] + "  "));
						lSbHeader.append(String.format("%13s", "Demand No : " + lStrDemandCode + " "));
						lSbHeader.append(String.format("%20s", "Major Head : " + lStrMajorHead + "  "));
						lSbHeader.append(String.format("%20s", "Sub Major Head : " + lStrSubMajorHead + "  "));
						lSbHeader.append(String.format("%20s", "Minor Head : " + lStrMinorHead + "  "));
						lSbHeader.append(String.format("%16s", "Sub Minor Head : " + lStrSubMinorHead + "  "));
						lSbHeader.append(String.format("%15s", "Sub Head : " + lStrSubHead + "  "));
						lSbHeader.append("\r\n");

					}
					lLstPensionerCode.add(obj[18].toString());
					lStrPensionerCode = obj[18].toString();
					inList = new ArrayList();
					inList.add(obj[4]);
					inList.add(obj[5]);
					inList.add("1");
					inList.add(obj[6]);
					inList.add(obj[7]);
					inList.add(obj[8]);
					lDbNetBillAmt = lDbNetBillAmt + Double.parseDouble(obj[8].toString());
					lDbDeductionAmt = lDbDeductionAmt + Double.parseDouble(obj[7].toString());
					arrOuter.add(inList);

				}
				lStrPensionOuterBill = lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), lStrFooter, null, 132, true, 1, true, "|");

				lStrNetAmount = "Below Rs.  " + lDbNetBillAmt.longValue() + "   " + "(In Words  " + EnglishDecimalFormat.convert1WithSpace(lDbNetBillAmt.longValue()) + " Only)" + "\r\n\r\n";

				if (lStrPensionOuterBill != null) {
					lIntNoOfLinesPrintedOnPage = (lStrPensionOuterBill.split(lineSeperator)).length;
				}
				if ("S".equals(lStrBillFlag)) {
					lMapRecoveryDtls = lObjSupplementaryBillDAO.getRecoveryDtlsForSupplPensionBill(lStrPensionerCode, lStrForMonthYear);
				} else {
					lMapRecoveryDtls = lObjMonthlyDAO.getRecoveryDtlsForChngStmnt(lLstPensionerCode, lStrForMonthYear);
				}
				lLstTrnPensionRecoveryDtlsVO = lMapRecoveryDtls.get(lStrPensionerCode);
				if (lLstTrnPensionRecoveryDtlsVO != null && !lLstTrnPensionRecoveryDtlsVO.isEmpty()) {
					lSbHeader = new StringBuilder();
					arrOuter = new ArrayList<List>();
					columnHeading = new ColumnVo[2];
					columnHeading[0] = new ColumnVo("Deduction Scheme Code", 1, 28, 0, true, false, true);
					columnHeading[1] = new ColumnVo("Amount", 2, 20, 0, false, false, true);
					for (TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtlsVO : lLstTrnPensionRecoveryDtlsVO) {

						inList = new ArrayList();
						inList.add((lObjTrnPensionRecoveryDtlsVO.getSchemeCode() != null) ? lObjTrnPensionRecoveryDtlsVO.getSchemeCode() : "");
						inList.add(lObjTrnPensionRecoveryDtlsVO.getAmount());

						arrOuter.add(inList);

					}
					lStrRecoveryDtls = lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), lStrFooter, null, 50, true, lIntNoOfLinesPrintedOnPage, true, "|");
				}
				lSbHeader = new StringBuilder();
				lSbFooter.append("\r\n");
				lSbFooter.append(String.format("%121s", "Signature With Stamp"));
				lSbFooter.append("\r\n");
				lSbFooter.append(String.format("%120s", "D.D.O. Pension"));
				lSbFooter.append("\r\n");
				lSbFooter.append("\r\n");

				List blankList = new ArrayList();
				blankList.add("");
				blankList.add("");
				blankList.add("");
				lStrFooter = "------------------Used for Audit Section Only--------------------------------------Used for Cheque/Cash Section-------------------\r\n";

				columnHeading = new ColumnVo[3];
				columnHeading[0] = new ColumnVo("", 1, 60, 0, true, false, true);
				columnHeading[1] = new ColumnVo("", 1, 20, 0, true, false, true);
				columnHeading[2] = new ColumnVo("", 1, 40, 0, false, false, true);
				arrOuter = new ArrayList<List>();

				inList = new ArrayList();
				inList.add("PASSED FOR PAYMENT AND PAY RS. " + lDbNetBillAmt.longValue() + "/-");
				inList.add("");
				inList.add("CHEQUE NO/ECS/");
				arrOuter.add(inList);
				inList = new ArrayList();
				inList.add("IN WORDS RUPEES " + (EnglishDecimalFormat.convert1WithSpace(lDbNetBillAmt.longValue())).toUpperCase() + " Only");
				inList.add("");
				inList.add("EFT/NEFT Advice No");
				arrOuter.add(inList);
				inList = new ArrayList();
				inList.add("");
				inList.add("");
				inList.add("Date");
				arrOuter.add(inList);
				arrOuter.add(blankList);
				arrOuter.add(blankList);
				arrOuter.add(blankList);
				inList = new ArrayList();
				inList.add("ASST.PAY AND ACCOUNT OFFICER," + lStrTreasuryName + " :");
				inList.add("");
				inList.add("ASST.PAY AND ACCOUNT OFFICER," + lStrTreasuryName);
				arrOuter.add(inList);
				inList = new ArrayList();
				inList.add("ADDL.TREASURY OFFICER  :");
				inList.add("");
				inList.add("ADDL.TREASURY OFFICER  ");
				arrOuter.add(inList);

				lStrFooter = lStrFooter + lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), "", null, 132, true, 30, false, "");
				lStrFooter = lStrFooter + "------------------For Purpose Of A.G.---------------------------------------------------------------------------------------------\r\n";
				lStrFooter = lStrFooter + "Admitted for Rs.\r\n";
				lStrFooter = lStrFooter + "Objected for Rs.\r\n";
				lStrFooter = lStrFooter + "\r\n";
				lStrFooter = lStrFooter + "\r\n";

				lStrFooter = lStrFooter + String.format("%70s", "Audit Officer");
				lStrPensionOuterBill = lStrPensionOuterBill + lStrNetAmount + lStrRecoveryDtls + lSbFooter.toString() + lStrFooter;

				Map lDetailMap = new HashMap();
				String lStrExportTo = DBConstants.DIS_ONSCREEN;
				if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
					lDetailMap.put(DBConstants.DIS_ONSCREEN, lStrPensionOuterBill);
				} else if ((DBConstants.DIS_TEXTFILE).equals(lStrExportTo)) {
					lDetailMap.put(DBConstants.DIS_TEXTFILE, lStrPensionOuterBill);
				}
				ReportExportHelper rptExpHelper = new ReportExportHelper();
				rptExpHelper.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
				resObj.setResultValue(inputMap);
			}
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	public ResultObject viewPensionInnerBill(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		String lStrLangId = SessionHelper.getLangId(inputMap).toString();
		SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjSmplDateFormat = new SimpleDateFormat("MMMM yyyy ");
		MonthlyPensionBillDAO lObjMonthlyPensionBillDAO = null;
		PensionBillDAO lObjPensionBillDAO = null;

		List<Object[]> lLstPensionBillInnerDtls = null;
		List<Object[]> lLstCategoryWiseSummary = new ArrayList<Object[]>();
		String lStrTreasuryName = null;
		String lStrBillCntrlNo = null;
		String lStrSchemeCode = "";
		String lStrSchemeName = "";
		String lStrMajorHead = "";
		String lStrSubMajorHead = "";
		String lStrMinorHead = "";
		String lStrSubMinorHead = "";
		String lStrSubHead = "";
		String lStrDemandCode = "";
		String lStrCharged = "";
		String lStrPlan = "";
		String lStrPaymentMode = "";
		String lStrBankKey = null;
		String lStrBranchKey = null;
		String lStrBankName = null;
		String lStrBranchName = null;
		String lStrBankBranchKey = "";
		String lStrPensionInnerBill = "";
		String lStrFooter = "";
		String lStrForMonthYear = "";
		String lStrBillDate = "";
		Date lDtBillDate = null;
		StringBuilder lSbHeader = new StringBuilder();
		StringBuilder lSbFooter = new StringBuilder();
		Map<String, String> lBankMap = new HashMap<String, String>();
		Map<String, String> lBranchMap = new HashMap<String, String>();
		List<String> lLstBankBranchKey = new ArrayList<String>();
		Map<String, List> lBlllDetailMap = new HashMap<String, List>();
		List<Object[]> lLstBillDtls = new ArrayList<Object[]>();
		Map<String, List> lMapCategoryWiseSummary = new HashMap<String, List>();
		List<Object[]> lLstCategoryWiseDtls = new ArrayList<Object[]>();
		String lineSeperator = "\r\n";

		Integer lIntNoOfLinesPrintedOnPage = 0;
		try {
			setSessionInfo(inputMap);
			String lStrBillNo = StringUtility.getParameter("billNo", request);

			lObjPensionBillDAO = new PensionBillDAOImpl(serv.getSessionFactory());

			PhysicalCaseInwardDAO lObjPhysicalCaseInwardDAO = new PhysicalCaseInwardDAOImpl(TrnPensionRqstHdr.class, serv.getSessionFactory());
			lObjMonthlyPensionBillDAO = new MonthlyPensionBillDAOImpl(MstPensionerHdr.class, serv.getSessionFactory());

			lLstPensionBillInnerDtls = lObjPensionBillDAO.getPensionInnerBillData(lStrBillNo, gStrLocationCode);
			ReportExportHelper lObjExport = new ReportExportHelper();

			if (!lLstPensionBillInnerDtls.isEmpty()) {
				lStrTreasuryName = lObjPhysicalCaseInwardDAO.getTreasuryName(gLngLangId, Long.parseLong(gStrLocationCode));
				for (Object[] cols : lLstPensionBillInnerDtls) {

					if (cols[15] != null) {
						lStrForMonthYear = cols[15].toString();
						lStrBillDate = "01/" + lStrForMonthYear.substring(4) + "/" + lStrForMonthYear.substring(0, 4);
						lDtBillDate = lObjSimpleDateFormat.parse(lStrBillDate);
					}
					if (cols[1] != null) {
						lStrBillCntrlNo = cols[1].toString();
					}
					if (cols[12] != null) {
						lStrSchemeCode = cols[12].toString();
					}
					if (cols[13] != null) {
						lStrSchemeName = cols[13].toString();
					}
					if (cols[46] != null) {
						lStrMajorHead = cols[46].toString();
					}
					if (cols[47] != null) {
						lStrSubMajorHead = cols[47].toString();
					}
					if (cols[48] != null) {
						lStrMinorHead = cols[48].toString();
					}
					if (cols[49] != null) {
						lStrSubMinorHead = cols[49].toString();
					}
					if (cols[50] != null) {
						lStrSubHead = cols[50].toString();
					}
					if (cols[51] != null) {
						lStrDemandCode = cols[51].toString();
					}
					if (cols[52] != null) {
						if ("C".equals(cols[52])) {
							lStrCharged = "Charged";
						} else if ("V".equals(cols[52])) {
							lStrCharged = "Voted";
						}
					}
					if (cols[53] != null) {
						if ("P".equals(cols[53])) {
							lStrPlan = "Plan";
						} else if ("N".equals(cols[53])) {
							lStrPlan = "Non Plan";
						}
					}
					lSbHeader = new StringBuilder();
					lSbHeader.append("MTR 37");
					lSbHeader.append("\r\n");
					lSbHeader.append("2071 PENSION FOR THE MONTH OF " + lObjSmplDateFormat.format(lDtBillDate));
					lSbHeader.append("\r\n");

					lSbHeader.append(String.format("%16s", "Charged/Voted : " + lStrCharged + "   "));
					lSbHeader.append(String.format("%16s", "Plan/Non Plan : " + lStrPlan + "  "));
					lSbHeader.append("Treasury Name : " + lStrTreasuryName);
					lSbHeader.append("\r\n");

					lSbHeader.append(String.format("%-85s", "Scheme Descr. " + lStrSchemeName));
					lSbHeader.append("\r\n");
					lSbHeader.append(String.format("%40s", "Bank Name/Code : " + cols[4] + "/" + cols[2] + "    "));
					lSbHeader.append(String.format("%-85s", "Branch Name/Code : " + cols[5] + "/" + cols[3]));
					lSbHeader.append("\r\n");
					lSbHeader.append(String.format("%35s", "Bill No : " + lStrBillCntrlNo + "    "));
					lSbHeader.append(String.format("%30s", "Bill Date : " + lStrBillDate + "    "));
					lSbHeader.append(String.format("%40s", "IFSC Code : " + ((cols[6] != null) ? cols[6] : "") + "    "));
					lSbHeader.append("\r\n");
					lSbHeader.append(String.format("%40s", "Scheme Code : " + lStrSchemeCode + " "));
					lSbHeader.append(String.format("%13s", "Demand No. : " + lStrDemandCode + " "));
					lSbHeader.append(String.format("%20s", "Major Head : " + lStrMajorHead + "  "));
					lSbHeader.append(String.format("%20s", "Sub Major Head : " + lStrSubMajorHead + " "));
					lSbHeader.append(String.format("%20s", "Minor Head : " + lStrMinorHead + " "));
					lSbHeader.append(String.format("%16s", "Sub Minor Head : " + lStrSubMinorHead + "  "));
					lSbHeader.append(String.format("%15s", "Sub Head : " + lStrSubHead));
					lSbHeader.append("\r\n");

					ArrayList inList = new ArrayList();
					ArrayList blankList = new ArrayList();
					blankList.add("");
					blankList.add("");
					blankList.add("");
					Integer lIntSrNo = 1;
					Double lDbTotalIRAmount = 0D;
					Double lDbTotalAllocAmt = 0D;
					Double lDbTotalArrearAmt = 0D;
					List<List> arrOuter = new ArrayList<List>();
					ColumnVo[] columnHeading = new ColumnVo[3];
					columnHeading[0] = new ColumnVo("Sr No.", 1, 10, 0, true, false, true);
					columnHeading[1] = new ColumnVo("Name of Pensioner", 1, 32, 0, true, false, true);
					columnHeading[2] = new ColumnVo(cols[18].toString(), 1, 32, 0, true, false, true);

					lDbTotalIRAmount = Double.parseDouble(cols[30].toString()) + Double.parseDouble(cols[31].toString()) + Double.parseDouble(cols[32].toString());
					lDbTotalAllocAmt = Double.parseDouble(cols[25].toString()) + Double.parseDouble(cols[26].toString()) + Double.parseDouble(cols[27].toString())
							+ Double.parseDouble(cols[28].toString()) + Double.parseDouble(cols[29].toString());

					lDbTotalArrearAmt = Double.parseDouble(cols[39].toString()) + Double.parseDouble(cols[40].toString()) + Double.parseDouble(cols[41].toString())
							+ Double.parseDouble(cols[42].toString()) + Double.parseDouble(cols[43].toString());

					inList.add(lIntSrNo);
					inList.add("PPO NO");
					inList.add(cols[17]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Category");
					inList.add(cols[7]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Volume/Page No");
					inList.add(cols[10] + "/" + cols[11]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Account No");
					inList.add(cols[19]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Pay Com.");
					inList.add(cols[20]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Basic Pen Rate/Insury");
					inList.add(cols[21] + "/");
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Pen.Reduct/Gal");
					inList.add(cols[22] + "/" + cols[23]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Tot IR/DP");
					inList.add(lDbTotalIRAmount.longValue() + "/" + cols[33]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Peon Allw");
					inList.add(cols[35]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Med Allw");
					inList.add(cols[37]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Addl Pen");
					inList.add(cols[36]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Tot Alloc");
					inList.add(lDbTotalAllocAmt.longValue());
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Relief");
					inList.add(cols[34]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Gross");
					inList.add(cols[38]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Arrears");
					inList.add(lDbTotalArrearAmt.longValue());
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Recoveries");
					inList.add(cols[44]);
					arrOuter.add(inList);
					arrOuter.add(blankList);
					inList = new ArrayList();
					inList.add(++lIntSrNo);
					inList.add("Net Pension");
					inList.add(cols[45]);
					arrOuter.add(inList);
					arrOuter.add(blankList);

					// lStrPensionInnerBill = lStrPensionInnerBill + "\r\n\r\n"
					// + getChar(132,"-")+"\r\n";

					lSbFooter.append("IR1       " + cols[30]);
					lSbFooter.append("\r\n");
					lSbFooter.append("IR2       " + cols[31]);
					lSbFooter.append("\r\n");
					lSbFooter.append("IR3       " + cols[32]);
					lSbFooter.append("\r\n");
					lSbFooter.append("ALLOC1    " + cols[25]);
					lSbFooter.append("\r\n");
					lSbFooter.append("ALLOC2    " + cols[26]);
					lSbFooter.append("\r\n");
					lSbFooter.append("ALLOC3    " + cols[27]);
					lSbFooter.append("\r\n");
					lSbFooter.append("ALLOC4    " + cols[28]);
					lSbFooter.append("\r\n");
					lSbFooter.append("ALLOC5    " + cols[29]);
					lSbFooter.append("\r\n");

					lStrPensionInnerBill = lObjExport.getReportFile(arrOuter, columnHeading, lSbHeader.toString(), lSbFooter.toString(), null, 132, true, 1, true, " ");

					lSbFooter = new StringBuilder();
					lStrPensionInnerBill = lStrPensionInnerBill + "\r\nCategory Wise summery" + "\r\n";
					ColumnVo[] columnCtgryWiseSumry = new ColumnVo[3];
					columnCtgryWiseSumry[0] = new ColumnVo("No Of Pensioner", 2, 10, 1, false, false, true);
					columnCtgryWiseSumry[1] = new ColumnVo("Category Code Description", 1, 35, 0, true, false, true);
					columnCtgryWiseSumry[2] = new ColumnVo("Net Amount Rs.", 2, 14, 1, false, false, true);

					lSbHeader = new StringBuilder();
					Double lDbNetPension = 0D;
					lDbNetPension = Double.parseDouble(cols[45].toString());

					arrOuter = new ArrayList<List>();
					inList = new ArrayList();
					inList.add("1");
					inList.add(cols[9]);
					inList.add(cols[45]);
					arrOuter.add(inList);

					lSbFooter.append("Rs.       " + cols[45]);
					lSbFooter.append("\r\n");
					lSbFooter.append("Rs In word RUPEES  " + EnglishDecimalFormat.convert1WithSpace(lDbNetPension.longValue()) + " Only");
					lSbFooter.append("\r\n");
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%132s", "ASST. PAY AND ACCOUNT OFFICER, " + lStrTreasuryName));
					lSbFooter.append("\r\n");
					lSbFooter.append(String.format("%113s", "ADDL./TREASURY OFFICER"));

					lStrPensionInnerBill = lStrPensionInnerBill + lObjExport.getReportFile(arrOuter, columnCtgryWiseSumry, lSbHeader.toString(), lSbFooter.toString(), null, 61, true, 20, true, "|");
				}

			}

			Map lDetailMap = new HashMap();
			String lStrExportTo = DBConstants.DIS_ONSCREEN;
			if ((DBConstants.DIS_ONSCREEN).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_ONSCREEN, lStrPensionInnerBill);
			} else if ((DBConstants.DIS_TEXTFILE).equals(lStrExportTo)) {
				lDetailMap.put(DBConstants.DIS_TEXTFILE, lStrPensionInnerBill);
			}
			ReportExportHelper rptExpHelper = new ReportExportHelper();
			rptExpHelper.getExportData(resObj, lStrExportTo, inputMap, lDetailMap, false);
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}

	private String getChar(int count, String ele) {

		StringBuffer lSBSpace = new StringBuffer();
		for (int i = 0; i < count; i++) {
			lSBSpace.append(ele);
		}
		return lSBSpace.toString();
	}
	// public ResultObject getPPONoForDCRGUnpaid(Map inputMap) {
	//
	// ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	// ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
	//
	// PensionBillDAO lObjPensionBillDAO = new
	// PensionBillDAOImpl(srvcLoc.getSessionFactory());
	// List lLstPnsnrDCRGUnpaid = new ArrayList();
	// try {
	// lLstPnsnrDCRGUnpaid =
	// lObjPensionBillDAO.getDCRGUnpaidPensioner(gLngPostId.toString(),
	// gStrLocationCode);
	// inputMap.put("PnsnrListForDCRG", lLstPnsnrDCRGUnpaid);
	// } catch (Exception e) {
	// gLogger.error("Error is : " + e, e);
	// objRes.setThrowable(e);
	// objRes.setResultValue(null);
	// objRes.setResultCode(ErrorConstants.ERROR);
	// objRes.setViewName("errorPage");
	// }
	// return objRes;
	// }

	// public ResultObject getPPONoForCVPUnpaid(Map inputMap) {
	//
	// ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	// ServiceLocator srvcLoc = (ServiceLocator) inputMap.get("serviceLocator");
	//
	// PensionBillDAO lObjPensionBillDAO = new
	// PensionBillDAOImpl(srvcLoc.getSessionFactory());
	// List lLstPnsnrCVPUnpaid = new ArrayList();
	// try {
	// lLstPnsnrCVPUnpaid =
	// lObjPensionBillDAO.getCVPUnpaidPensioner(gLngPostId.toString(),
	// gStrLocationCode);
	// inputMap.put("PnsnrListForCVP", lLstPnsnrCVPUnpaid);
	// } catch (Exception e) {
	// gLogger.error("Error is : " + e, e);
	// objRes.setThrowable(e);
	// objRes.setResultValue(null);
	// objRes.setResultCode(ErrorConstants.ERROR);
	// objRes.setViewName("errorPage");
	// }
	// // return objRes;
	// return objRes;
	// }
}