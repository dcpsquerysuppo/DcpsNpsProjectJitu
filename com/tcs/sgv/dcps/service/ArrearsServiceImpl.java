package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.ArrearsDAO;
import com.tcs.sgv.dcps.dao.ArrearsDAOImpl;
import com.tcs.sgv.dcps.dao.DdoProfileDAO;
import com.tcs.sgv.dcps.dao.DdoProfileDAOImpl;
import com.tcs.sgv.dcps.dao.MstDcpsTierICntrnbtnDAO;
import com.tcs.sgv.dcps.dao.MstDcpsTierICntrnbtnDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsTierICntrnbtn;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstSixPCArrears;
import com.tcs.sgv.dcps.valueobject.RltDcpsSixPCYearly;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;

public class ArrearsServiceImpl extends ServiceImpl implements ArrearsService {

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
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
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
		} catch (Exception e) {

		}

	}

	/**
	 * to load DCPS tier I
	 */
	public ResultObject loadDCPSTierI(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {
			setSessionInfo(inputMap);

			List lLstTypeOfArrear = null;
			List lLstDesignation = null;
			List lLstDepartment = null;
			String lStrDesignation = null;
			List lLstTierDtlsFromDesig = null;

			lLstTypeOfArrear = IFMSCommonServiceImpl
					.getLookupValues("TypeOfArrear", SessionHelper
							.getLangId(inputMap), inputMap);
			gLogger.info("here is the size of event list :"
					+ lLstTypeOfArrear.size());
			Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle
					.getString("DCPS.DEPARTMENTID"));
			ArrearsDAO lObjcmnDCPSArrearsDAO = new ArrearsDAOImpl(null, serv
					.getSessionFactory());
			lLstDepartment = lObjcmnDCPSArrearsDAO.getAllDepartment(
					lLngDepartmentId, SessionHelper.getLangId(inputMap));
			lLstDesignation = lObjcmnDCPSArrearsDAO
					.getAllDesignation(SessionHelper.getLangId(inputMap));

			if (StringUtility.getParameter("TierDraft", request) != null
					&& StringUtility.getParameter("TierDraft", request)
							.length() > 0) {
				StringUtility.getParameter("TierDraft", request);
				lStrDesignation = StringUtility.getParameter("Designation",
						request);
				lObjcmnDCPSArrearsDAO = new ArrearsDAOImpl(
						MstDcpsTierICntrnbtn.class, serv.getSessionFactory());
				lLstTierDtlsFromDesig = lObjcmnDCPSArrearsDAO
						.getTierDtlFromDesig(lStrDesignation);
				inputMap.put("TierDraftList", lLstTierDtlsFromDesig);

			}

			inputMap.put("lLstDepartment", lLstDepartment);
			inputMap.put("lLstDesignation", lLstDesignation);
			inputMap.put("lLstTypeOfArrear", lLstTypeOfArrear);
			gLogger.info("Load Sucessful");
		}

		catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("dcpsArrearsTierI");
		return resObj;
	}

	/**
	 * 
	 * <H3>To insert tier I details to database-</H3>
	 * 
	 * 
	 * 
	 * @author Sneha
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public ResultObject insertDCPSTierI(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		new SimpleDateFormat("dd/MM/yyyy");
		Long lngTierICntrnbtnId = null;

		ArrayList<MstDcpsTierICntrnbtn> lArryLstMstDcpsTierICntrnbtnVO = new ArrayList<MstDcpsTierICntrnbtn>();

		try {
			setSessionInfo(inputMap);
			ServiceLocator serv = (ServiceLocator) inputMap
					.get("serviceLocator");
			MstDcpsTierICntrnbtn lObjMstDcpsTierICntrnbtnVO = new MstDcpsTierICntrnbtn();
			lArryLstMstDcpsTierICntrnbtnVO = (ArrayList<MstDcpsTierICntrnbtn>) inputMap
					.get("lLstMstDcpsTierICntrnbtnVO");

			MstDcpsTierICntrnbtnDAO lObjMstDcpsTierICntrnbtnDAO = new MstDcpsTierICntrnbtnDAOImpl(
					MstDcpsTierICntrnbtn.class, serv.getSessionFactory());

			for (int i = 0; i < lArryLstMstDcpsTierICntrnbtnVO.size(); i++) {
				lngTierICntrnbtnId = IFMSCommonServiceImpl.getNextSeqNum(
						"mst_dcps_tieri_cntrnbtn", inputMap);

				lObjMstDcpsTierICntrnbtnVO = lArryLstMstDcpsTierICntrnbtnVO
						.get(i);
				lObjMstDcpsTierICntrnbtnVO
						.setTierICntrnbtnId(lngTierICntrnbtnId);
				lObjMstDcpsTierICntrnbtnDAO.create(lObjMstDcpsTierICntrnbtnVO);

				gLogger
						.info("Record Inserted in table trn_pnsnproc_eventdtls successfully");

			}

			StringBuilder lStrBldXML = getResponseXMLDoc(inputMap);

			gLogger.info(" lStrBldXML :: " + lStrBldXML);

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lStrBldXML.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		}

		catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;

	}

	/**
	 * 
	 * <H3>to get employee information based on emp id entered -</H3>
	 * 
	 * 
	 * 
	 * @author Sneha
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public ResultObject getDCPSTierI(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		new SimpleDateFormat("dd/MM/yyyy");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		MstDcpsTierICntrnbtnDAO lObjMstDcpsTierICntrnbtnDAO = new MstDcpsTierICntrnbtnDAOImpl(
				MstEmp.class, serv.getSessionFactory());

		try {
			List lLstEmpData = null;
			setSessionInfo(inputMap);
			HttpServletRequest request = (HttpServletRequest) inputMap
					.get("requestObj");

			Long lEmpId = Long.parseLong(StringUtility.getParameter("empId",
					request));
			Integer lIntCnt = Integer.parseInt(StringUtility.getParameter(
					"rowCnt", request));
			lLstEmpData = lObjMstDcpsTierICntrnbtnDAO.getEmpData(lEmpId);

			inputMap.put("lEmpId", lLstEmpData.get(0));
			inputMap.put("lEmpName", lLstEmpData.get(1));
			inputMap.put("lIntCnt", lIntCnt);
			StringBuilder lStrBldXML = getEmpDataResponseXMLDoc(inputMap);

			gLogger.info(" lStrBldXML :: " + lStrBldXML);

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lStrBldXML.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		}

		catch (Exception e) {
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		return resObj;

	}

	private StringBuilder getResponseXMLDoc(Map inputMap) {

		StringBuilder lStrHidPKs = new StringBuilder();

		lStrHidPKs.append("<XMLDOC>");
		lStrHidPKs.append("<MESSAGECODE>");
		lStrHidPKs.append("insert");
		lStrHidPKs.append("</MESSAGECODE>");
		lStrHidPKs.append("<MESSAGECODE2>");
		lStrHidPKs.append("update");
		lStrHidPKs.append("</MESSAGECODE2>");
		lStrHidPKs.append("</XMLDOC>");

		gLogger.info("lStrHidPKs : " + lStrHidPKs);

		return lStrHidPKs;
	}

	private StringBuilder getEmpDataResponseXMLDoc(Map inputMap) {

		String lEmpId = inputMap.get("lEmpId").toString();
		String lEmpName = inputMap.get("lEmpName").toString();
		String lRowCnt = inputMap.get("lIntCnt").toString();
		StringBuilder lStrHidPKs = new StringBuilder();
		lStrHidPKs.append("<XMLDOC>");
		lStrHidPKs.append("<EMPID>" + lEmpId);
		lStrHidPKs.append("</EMPID>");
		lStrHidPKs.append("<EMPNAME>" + lEmpName);
		lStrHidPKs.append("</EMPNAME>");
		lStrHidPKs.append("<ROWCNT>" + lRowCnt);
		lStrHidPKs.append("</ROWCNT>");
		lStrHidPKs.append("</XMLDOC>");

		gLogger.info("lStrHidPKs : " + lStrHidPKs);

		return lStrHidPKs;
	}

	public ResultObject loadsixthPCArrearsEntry(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		setSessionInfo(inputMap);
		List empList = null;
		List lLstUpperUsers = null;
		String lStrStatusFlag = null;
		String lStrUserType = null;
		try {
			setSessionInfo(inputMap);
			lStrStatusFlag = StringUtility.getParameter("StatusFlag", request);
			lStrUserType = StringUtility.getParameter("UserType", request);
			ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(MstEmp.class, serv
					.getSessionFactory());
			DdoProfileDAO ddoProfileDAO = new DdoProfileDAOImpl(null, serv
					.getSessionFactory());
			String lStrDdoCode = ddoProfileDAO.getDdoCode(gLngPostId);
			empList = ArrearsDAOObj.getEmpListForSixPCArrears(lStrDdoCode);
			lLstUpperUsers = getHierarchyUsers(inputMap);

			inputMap.put("UserType", lStrUserType);
			inputMap.put("DDOCODE", lStrDdoCode);
			inputMap.put("empList", empList);
			inputMap.put("UserList", lLstUpperUsers);
			inputMap.put("StatusFlag", lStrStatusFlag);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DcpsSixPCArrears");

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error("Error is:" + e, e);
		}

		return resObj;
	}

	public ResultObject loadsixthPCYearlyInstallment(
			Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		String lStrStatusFlag = null;
		String lStrUserType = null;
		List lListEmp = null;
		List lLstUpperUsers = null;
		try {
			setSessionInfo(inputMap);
			lStrUserType = StringUtility.getParameter("UserType", request);

			ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(MstEmp.class, serv
					.getSessionFactory());
			DdoProfileDAO ddoProfileDAO = new DdoProfileDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			lStrStatusFlag = StringUtility.getParameter("StatusFlag", request);
			System.out
					.println("lStrStatusFlag from loadsixthPCYearlyInstallment is "
							+ lStrStatusFlag);
			String lStrDdoCode = ddoProfileDAO.getDdoCode(gLngPostId);
			inputMap.put("DDOCODE", lStrDdoCode);

			List lListYears = ArrearsDAOObj.getYears();
			lLstUpperUsers = getHierarchyUsers(inputMap);

			if (StringUtility.getParameter("yearId", request) != null
					&& !(StringUtility.getParameter("yearId", request)
							.equalsIgnoreCase(""))) {
				Long yearId = Long.valueOf(StringUtility.getParameter("yearId",
						request));
				lListEmp = ArrearsDAOObj.getEmpListForSixPCArrearsYearly(
						lStrDdoCode, yearId);
			}
			inputMap.put("UserType", lStrUserType);
			inputMap.put("StatusFlag", lStrStatusFlag);
			inputMap.put("UserList", lLstUpperUsers);
			inputMap.put("lListYears", lListYears);
			inputMap.put("lListEmp", lListEmp);
			resObj.setResultValue(inputMap);
			resObj.setViewName("DcpsSixPCArrearsYearly");
		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error("Error is:" + e, e);
		}

		return resObj;
	}

	public ResultObject sixthPCYearlyInstallmentForGivenYear(
			Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		setSessionInfo(inputMap);
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		try {

			// List Arrears = IFMSCommonServiceImpl.getLookupValues("Arrears",
			// SessionHelper.getLangId(inputMap), inputMap);
			// inputMap.put("Arrears", Arrears);

			// List PcYear = IFMSCommonServiceImpl.getLookupValues("6PcYear",
			// SessionHelper.getLangId(inputMap), inputMap);
			// inputMap.put("PcYear", PcYear);

			ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(MstEmp.class, serv
					.getSessionFactory());
			List lListYears = null;
			List lListEmp = null;

			Long yearId = Long.valueOf(StringUtility.getParameter("yearId",
					request));

			DdoProfileDAO ddoProfileDAO = new DdoProfileDAOImpl(null, serv
					.getSessionFactory());
			String lStrDdoCode = ddoProfileDAO.getDdoCodeForDDO(gLngPostId);
			inputMap.put("DDOCODE", lStrDdoCode);

			lListYears = ArrearsDAOObj.getYears();
			inputMap.put("lListYears", lListYears);

			lListEmp = ArrearsDAOObj.getEmpListForSixPCArrearsYearly(
					lStrDdoCode, yearId);
			inputMap.put("lListEmp", lListEmp);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DcpsSixPCArrearsYearly");
		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error("Error is:" + e, e);
		}

		return resObj;
	}

	public ResultObject saveSixPCArrears(Map inputMap) {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		Boolean lBlFlag = false;
		setSessionInfo(inputMap);

		ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(MstSixPCArrears.class,
				serv.getSessionFactory());

		try {

			new ResultObject(ErrorConstants.SUCCESS, "FAIL");

			String lStrDcpsSixPCIds = StringUtility.getParameter(
					"dcpsSixPCIds", request);
			String[] lArrStrdcpsSixPCIds = lStrDcpsSixPCIds.split("~");
			Long[] lArrLongdcpsSixPCIds = new Long[lArrStrdcpsSixPCIds.length + 1];

			for (Integer lInt = 0; lInt < lArrStrdcpsSixPCIds.length; lInt++) {
				lArrLongdcpsSixPCIds[lInt] = Long
						.parseLong(lArrStrdcpsSixPCIds[lInt]);

			}

			String lStrAmounts = StringUtility.getParameter("amounts", request);
			String[] lArrStrAmounts = lStrAmounts.split("~");
			Long[] lArrLongAmounts = new Long[lArrStrAmounts.length + 1];

			for (Integer lInt = 0; lInt < lArrStrAmounts.length; lInt++) {
				lArrLongAmounts[lInt] = Long.parseLong(lArrStrAmounts[lInt]);

			}

			for (Integer lInt = 0; lInt < lArrStrdcpsSixPCIds.length; lInt++) {
				MstSixPCArrears MstSixPCArrearsObj = (MstSixPCArrears) ArrearsDAOObj
						.read(lArrLongdcpsSixPCIds[lInt]);
				MstSixPCArrearsObj.setTotalAmount(lArrLongAmounts[lInt]);
				lBlFlag = true;
			}

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
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	public ResultObject saveSixPCArrearsYearly(Map inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		boolean lBlFlag = false;
		setSessionInfo(inputMap);
		ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(MstSixPCArrears.class,
				servLoc.getSessionFactory());
		try {

			String lStrdcpsSixPCYearlyIds = StringUtility.getParameter(
					"dcpsSixPCYearlyIds", request);
			String[] lStrArrdcpsSixPCYearlyIds = lStrdcpsSixPCYearlyIds
					.split("~");
			Long[] lLongArrdcpsSixPCYearlyIds = new Long[lStrArrdcpsSixPCYearlyIds.length];

			RltDcpsSixPCYearly[] lArrRltDcpsSixPCYearly = (RltDcpsSixPCYearly[]) inputMap
					.get("lArrRltDcpsSixPCYearly");

			for (Integer lInt = 0; lInt < lStrArrdcpsSixPCYearlyIds.length; lInt++) {
				lLongArrdcpsSixPCYearlyIds[lInt] = Long
						.valueOf(lStrArrdcpsSixPCYearlyIds[lInt]);
			}

			for (Integer lInt = 0; lInt < lStrArrdcpsSixPCYearlyIds.length; lInt++) {
				if (lLongArrdcpsSixPCYearlyIds[lInt] == 0l) {
					Long lLongTempdcpsSixPCYearlyId = IFMSCommonServiceImpl
							.getNextSeqNum("rlt_dcps_sixpc_yearly", inputMap);
					lArrRltDcpsSixPCYearly[lInt]
							.setDcpsSixPCYearlyId(lLongTempdcpsSixPCYearlyId);
					ArrearsDAOObj.create(lArrRltDcpsSixPCYearly[lInt]);
					lBlFlag = true;
				} else {
					lArrRltDcpsSixPCYearly[lInt]
							.setDcpsSixPCYearlyId(lLongArrdcpsSixPCYearlyIds[lInt]);

					ArrearsDAOObj.update(lArrRltDcpsSixPCYearly[lInt]);
					lBlFlag = true;
				}

			}

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDoc(boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("  </Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject loadOfflineDCPSForm(Map inputMap) throws Exception {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		setSessionInfo(inputMap);

		new SimpleDateFormat("dd/MM/yyyy");

		// Create the object of the DAO class

		try {

			// get all bill groups

			ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(
					TrnDcpsContribution.class, servLoc.getSessionFactory());
			List lLstBillGroups = ArrearsDAOObj.getBillGroups();
			inputMap.put("BILLGROUPS", lLstBillGroups);

			// get months
			List lLstMonths = ArrearsDAOObj.getMonths();
			inputMap.put("MONTHS", lLstMonths);

			// get years
			List lLstYears = ArrearsDAOObj.getYears();
			inputMap.put("YEARS", lLstYears);

			String lStrUserType = StringUtility.getParameter("User", request);
			inputMap.put("lStrUser", lStrUserType);

			String lStrUseType = StringUtility.getParameter("Use", request);
			inputMap.put("lStrUse", lStrUseType);

			if (StringUtility.getParameter("cmbBillGroup", request) != null
					&& !StringUtility.getParameter("cmbBillGroup", request)
							.equalsIgnoreCase("")) {

				String lStrDDOCode = StringUtility.getParameter("cmbDDOCode",
						request);
				Long monthId = Long.parseLong(StringUtility.getParameter(
						"cmbMonth", request));
				Long yearId = Long.parseLong(StringUtility.getParameter(
						"cmbYear", request));
				Long lLongbillGroupId = Long.valueOf(StringUtility
						.getParameter("cmbBillGroup", request));
				Long treasuryCode = Long.valueOf(StringUtility.getParameter(
						"treasuryCode", request));
				String schemename = StringUtility.getParameter("schemeName",
						request);
				Long schemeCode = Long.valueOf(StringUtility.getParameter(
						"schemeCode", request));

				// Get the last day of the selected Month
				Integer lIntMonth = Integer.parseInt(monthId.toString());
				Integer lIntYear = Integer.parseInt(yearId.toString());
				Date lDtLastDate = getLastDate(lIntMonth - 1, lIntYear);
				Date lDtFirstDate = getFirstDate(lIntMonth - 1, lIntYear);

				if (lIntMonth == 1) {
					lIntYear--;
				}

				Date lDtDelFirstDate = getFirstDate(lIntMonth - 2, lIntYear);
				Date lDtDelLastDate = getLastDate(lIntMonth - 2, lIntYear);

				inputMap.put("lStrDDOCode", lStrDDOCode);
				inputMap.put("monthId", monthId);
				inputMap.put("yearId", yearId);
				inputMap.put("lLongbillGroupId", lLongbillGroupId);
				inputMap.put("treasuryCode", treasuryCode);
				inputMap.put("schemename", schemename);
				inputMap.put("schemeCode", schemeCode);
				inputMap.put("FirstDate", lDtFirstDate);
				inputMap.put("LastDate", lDtLastDate);
				inputMap.put("DelFirstDate", lDtDelFirstDate);
				inputMap.put("DelLastDate", lDtDelLastDate);

				List listPayCommission = IFMSCommonServiceImpl.getLookupValues(
						"PayCommissionDCPS", SessionHelper.getLangId(inputMap),
						inputMap);
				inputMap.put("listPayCommission", listPayCommission);

				List listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues(
						"TypeOfPaymentDCPS", SessionHelper.getLangId(inputMap),
						inputMap);
				inputMap.put("listTypeOfPayment", listTypeOfPayment);

				String lStrUser = StringUtility.getParameter("User", request);

				if (lStrUser.equals("ATO")) {
					inputMap.put("EditForm", "Y");
					List UserList = getHierarchyUsers(inputMap, lStrUser);
					inputMap.put("UserList", UserList);
				} else if (lStrUser.equals("TO")) {
					inputMap.put("EditForm", "N");
				}

				List empList = null;

				String lStrUse = StringUtility.getParameter("Use", request);

				empList = ArrearsDAOObj.getEmpListForContribution(lStrDDOCode,
						lLongbillGroupId, monthId, yearId, lStrUser, lStrUse,
						gStrPostId);
				inputMap.put("empList", empList);

			}

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSOfflineEntryForm");

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject forwardRequestToTO(Map objectArgs) {

		HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		setSessionInfo(objectArgs);
		Boolean lBlFlag = false;
		ServiceLocator servLoc = (ServiceLocator) objectArgs
				.get("serviceLocator");
		ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(
				TrnDcpsContribution.class, servLoc.getSessionFactory());

		try {

			String toPost = StringUtility
					.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("DCPS.TO");
			String strPKValue = StringUtility.getParameter(
					"dcpsContributionIds", request).toString().trim();

			// Split the array to get the ID of forms selected
			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("toPost", toPost);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("toLevel", toLevel);

			objectArgs.put("jobTitle", gObjRsrcBndle
					.getString("DCPS.Contribution"));
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.ContributionID")));

			// Iterates more than 1 time if more than 1 contributions are
			// selected

			TrnDcpsContribution lObjTrnDcpsContribution = null;

			for (int index = 0; index < strArrPKValue.length; index++) {
				objectArgs.put("Pkvalue", strArrPKValue[index]);
				createWF(objectArgs);
				WorkFlowDelegate.forward(objectArgs);

				lObjTrnDcpsContribution = new TrnDcpsContribution();
				lObjTrnDcpsContribution = (TrnDcpsContribution) ArrearsDAOObj
						.read(Long.valueOf(strArrPKValue[index]));
				lObjTrnDcpsContribution.setRegStatus(0l);

				lBlFlag = true;
			}

			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			objectArgs.put("ajaxKey", lStrResult);
			resObj.setResultValue(objectArgs);
			resObj.setViewName("ajaxData");

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}
		return resObj;
	}

	private void createWF(Map inputMap) {

		try {

			Long PKValue = Long.parseLong(inputMap.get("Pkvalue").toString());

			setSessionInfo(inputMap);
			String subjectName = gObjRsrcBndle.getString("DCPS.Contribution");
			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			Long lLngHierRefId = WorkFlowHelper
					.getHierarchyByPostIDAndDescription(lStrPostId,
							subjectName, inputMap);

			inputMap.put("Hierarchy_ref_id", lLngHierRefId);
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.ContributionID")));
			inputMap.put("Pkvalue", PKValue);
			inputMap.put("DisplayJobTitle", gObjRsrcBndle
					.getString("DCPS.Contribution"));

			WorkFlowDelegate.create(inputMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ResultObject loadSixPCArrearAmountSchedule(
			Map<String, Object> inputMap) {

		// TODO Auto-generated method stub

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		setSessionInfo(inputMap);
		try {

			List lLstEmp = null;

			ArrearsDAO lObjcmnDCPSArrearsDAO = new ArrearsDAOImpl(null, serv
					.getSessionFactory());

			String lStrDdoCode = lObjcmnDCPSArrearsDAO.getDdoCode(gLngPostId);

			String lStrUserType = StringUtility.getParameter("UserType",
					request);
			inputMap.put("UserType", lStrUserType);

			List lListYears = lObjcmnDCPSArrearsDAO.getYears();
			inputMap.put("lListYears", lListYears);

			if (StringUtility.getParameter("yearId", request) != null
					&& !(StringUtility.getParameter("yearId", request)
							.equalsIgnoreCase(""))) {
				Long yearId = Long.valueOf(StringUtility.getParameter("yearId",
						request));
				lLstEmp = lObjcmnDCPSArrearsDAO
						.getEmpListForSixPCArrearAmountSchedule(lStrDdoCode,
								yearId);

				inputMap.put("lLstEmp", lLstEmp);
			}

			resObj.setResultValue(inputMap);
			resObj.setViewName("dcpsSixPCArrearAmountSchedule");
			gLogger.info("Load Sucessful");
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}

		return resObj;
	}

	public ResultObject loadOfflineCorrectionForm(Map inputMap)
			throws Exception {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		setSessionInfo(inputMap);

		new SimpleDateFormat("dd/MM/yyyy");

		// Create the object of the DAO class

		try {

			// get all bill groups

			ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(
					TrnDcpsContribution.class, servLoc.getSessionFactory());
			List lLstBillGroups = ArrearsDAOObj.getBillGroups();
			inputMap.put("BILLGROUPS", lLstBillGroups);

			// get months
			List lLstMonths = ArrearsDAOObj.getMonths();
			inputMap.put("MONTHS", lLstMonths);

			// get years
			List lLstYears = ArrearsDAOObj.getYears();
			inputMap.put("YEARS", lLstYears);

			if (StringUtility.getParameter("cmbBillGroup", request) != null
					&& !StringUtility.getParameter("cmbBillGroup", request)
							.equalsIgnoreCase("")) {

				String lStrDDOCode = StringUtility.getParameter("cmbDDOCode",
						request);
				Long monthId = Long.parseLong(StringUtility.getParameter(
						"cmbMonth", request));
				Long yearId = Long.parseLong(StringUtility.getParameter(
						"cmbYear", request));
				Long lLongbillGroupId = Long.valueOf(StringUtility
						.getParameter("cmbBillGroup", request));
				Long treasuryCode = Long.valueOf(StringUtility.getParameter(
						"treasuryCode", request));
				String schemename = StringUtility.getParameter("schemeName",
						request);
				Long schemeCode = Long.valueOf(StringUtility.getParameter(
						"schemeCode", request));

				// Get the last day of the selected Month
				Integer lIntMonth = Integer.parseInt(monthId.toString());
				Integer lIntYear = Integer.parseInt(yearId.toString());
				Date lDtLastDate = getLastDate(lIntMonth - 1, lIntYear);
				Date lDtFirstDate = getFirstDate(lIntMonth - 1, lIntYear);

				if (lIntMonth == 1) {
					lIntYear--;
				}

				Date lDtDelFirstDate = getFirstDate(lIntMonth - 2, lIntYear);
				Date lDtDelLastDate = getLastDate(lIntMonth - 2, lIntYear);

				inputMap.put("lStrDDOCode", lStrDDOCode);
				inputMap.put("monthId", monthId);
				inputMap.put("yearId", yearId);
				inputMap.put("lLongbillGroupId", lLongbillGroupId);
				inputMap.put("treasuryCode", treasuryCode);
				inputMap.put("schemename", schemename);
				inputMap.put("schemeCode", schemeCode);
				inputMap.put("FirstDate", lDtFirstDate);
				inputMap.put("LastDate", lDtLastDate);
				inputMap.put("DelFirstDate", lDtDelFirstDate);
				inputMap.put("DelLastDate", lDtDelLastDate);

				List listPayCommission = IFMSCommonServiceImpl.getLookupValues(
						"PayCommissionDCPS", SessionHelper.getLangId(inputMap),
						inputMap);
				inputMap.put("listPayCommission", listPayCommission);

				List listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues(
						"TypeOfPaymentDCPS", SessionHelper.getLangId(inputMap),
						inputMap);
				inputMap.put("listTypeOfPayment", listTypeOfPayment);

				List empList = ArrearsDAOObj
						.getEmpListForContributionCorrection(lStrDDOCode,
								lLongbillGroupId, monthId, yearId);
				inputMap.put("empList", empList);

			}
			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSOfflineEntryCorrection");

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject getContributionDetails(Map<String, Object> inputMap)
			throws Exception {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		TrnDcpsContribution lObjTrnDcpsContribution = new TrnDcpsContribution();
		MstEmp lObjMstEmp = new MstEmp();

		try {

			Long lLongdcpsContributionId = Long.parseLong(StringUtility
					.getParameter("dcpsContributionId", request).trim());

			ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(
					TrnDcpsContribution.class, servLoc.getSessionFactory());
			NewRegDdoDAO dcpsRegisDAO = new NewRegDdoDAOImpl(MstEmp.class,
					servLoc.getSessionFactory());

			Long lLongdcpsEmpId = ArrearsDAOObj
					.getEmpIdforContributionId(lLongdcpsContributionId);

			if (!(StringUtility.getParameter("dcpsContributionId", request)
					.trim().equalsIgnoreCase(""))) {
				lObjTrnDcpsContribution = (TrnDcpsContribution) ArrearsDAOObj
						.read(lLongdcpsContributionId);
				lObjMstEmp = (MstEmp) dcpsRegisDAO.read(lLongdcpsEmpId);
			}

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			ex.printStackTrace();
			return objRes;
		}

		String lSBStatus = getResponseXMLDocForContriDetails(
				lObjTrnDcpsContribution, lObjMstEmp).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	private StringBuilder getResponseXMLDocForContriDetails(
			TrnDcpsContribution lObjTrnDcpsContribution, MstEmp lObjMstEmp) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder lStrBldXML = new StringBuilder();
		String lStrContriFrom = sdf.format(lObjTrnDcpsContribution
				.getStartDate());
		String lStrContriTo = sdf.format(lObjTrnDcpsContribution.getEndDate());

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <empName>");
		lStrBldXML.append(lObjMstEmp.getName());
		lStrBldXML.append("  </empName>");
		lStrBldXML.append("  <dcpsId>");
		lStrBldXML.append(lObjMstEmp.getDcpsId());
		lStrBldXML.append("  </dcpsId>");
		lStrBldXML.append("  <startDate>");
		lStrBldXML.append(lStrContriFrom);
		lStrBldXML.append("  </startDate>");
		lStrBldXML.append("  <endDate>");
		lStrBldXML.append(lStrContriTo);
		lStrBldXML.append("  </endDate>");
		lStrBldXML.append("  <payCommission>");
		lStrBldXML.append(lObjTrnDcpsContribution.getPayCommission());
		lStrBldXML.append("  </payCommission>");
		lStrBldXML.append("  <typeOfPayment>");
		lStrBldXML.append(lObjTrnDcpsContribution.getTypeOfPayment());
		lStrBldXML.append("  </typeOfPayment>");
		lStrBldXML.append("  <basic>");
		lStrBldXML.append(lObjTrnDcpsContribution.getBasicPay());
		lStrBldXML.append("  </basic>");
		lStrBldXML.append("  <DP>");
		lStrBldXML.append(lObjTrnDcpsContribution.getDP());
		lStrBldXML.append("  </DP>");
		lStrBldXML.append("  <DA>");
		lStrBldXML.append(lObjTrnDcpsContribution.getDA());
		lStrBldXML.append("  </DA>");
		lStrBldXML.append("  <contribution>");
		lStrBldXML.append(lObjTrnDcpsContribution.getContribution());
		lStrBldXML.append("  </contribution>");
		lStrBldXML.append("  <dcpsEmpId>");
		lStrBldXML.append(lObjMstEmp.getDcpsEmpId());
		lStrBldXML.append("  </dcpsEmpId>");
		lStrBldXML.append("  <dcpsContributionId>");
		lStrBldXML.append(lObjTrnDcpsContribution.getDcpsContributionId());
		lStrBldXML.append("  </dcpsContributionId>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject getSchemeforBillGroup(Map<String, Object> inputMap)
			throws Exception {

		Log logger = LogFactory.getLog(getClass());
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		try {
			ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(
					TrnDcpsContribution.class, serv.getSessionFactory());

			Long billgroupId = Long.valueOf(StringUtility.getParameter(
					"billGroupId", request));

			Object[] schemeNameAndCode = ArrearsDAOObj
					.getSchemeNameFromBillGroup(billgroupId);

			String schemeName = (String) schemeNameAndCode[0];
			Long schemeCode = (Long) schemeNameAndCode[1];

			String lSBScheme = getResponseXMLDocForSchemeFromBillGroup(
					schemeName, schemeCode).toString();

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBScheme).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");

		} catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			logger.error(" Error in getDigiSig " + e, e);
		}

		return resObj;
	}

	public ResultObject saveContributions(Map inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		boolean lBlFlag = false;
		setSessionInfo(inputMap);

		ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(
				TrnDcpsContribution.class, servLoc.getSessionFactory());

		try {

			String lStrdcpsContributionIds = StringUtility.getParameter(
					"dcpsContributionIds", request);
			String[] lStrArrdcpsContributionIds = lStrdcpsContributionIds
					.split("~");
			Long[] lLongArrdcpsContributionIds = new Long[lStrArrdcpsContributionIds.length];

			TrnDcpsContribution[] lArrTrnDcpsContributions = (TrnDcpsContribution[]) inputMap
					.get("lArrTrnDcpsContributions");

			for (Integer lInt = 0; lInt < lStrArrdcpsContributionIds.length; lInt++) {
				lLongArrdcpsContributionIds[lInt] = Long
						.valueOf(lStrArrdcpsContributionIds[lInt]);
			}

			for (Integer lInt = 0; lInt < lStrArrdcpsContributionIds.length; lInt++) {
				if (lLongArrdcpsContributionIds[lInt] == 0l) {
					Long lLongTempdcpsContributionId = IFMSCommonServiceImpl
							.getNextSeqNum("TRN_DCPS_CONTRIBUTION", inputMap);
					lArrTrnDcpsContributions[lInt]
							.setDcpsContributionId(lLongTempdcpsContributionId);
					ArrearsDAOObj.create(lArrTrnDcpsContributions[lInt]);
					lBlFlag = true;
				} else {
					lArrTrnDcpsContributions[lInt]
							.setDcpsContributionId(lLongArrdcpsContributionIds[lInt]);
					ArrearsDAOObj.update(lArrTrnDcpsContributions[lInt]);
					lBlFlag = true;
				}

			}

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject deleteContributions(Map inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");

		boolean lBlFlag = false;
		setSessionInfo(inputMap);

		ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(
				TrnDcpsContribution.class, servLoc.getSessionFactory());
		try {

			String lStrdcpsContributionIds = StringUtility.getParameter(
					"dcpsContributionIds", request);
			String[] lStrArrdcpsContributionIds = lStrdcpsContributionIds
					.split("~");
			Long[] lLongArrdcpsContributionIds = new Long[lStrArrdcpsContributionIds.length];

			for (Integer lInt = 0; lInt < lStrArrdcpsContributionIds.length; lInt++) {
				lLongArrdcpsContributionIds[lInt] = Long
						.valueOf(lStrArrdcpsContributionIds[lInt]);
			}

			for (Integer lInt = 0; lInt < lLongArrdcpsContributionIds.length; lInt++) {
				if (lLongArrdcpsContributionIds[lInt] != 0l) {
					ArrearsDAOObj.delete(ArrearsDAOObj
							.read(lLongArrdcpsContributionIds[lInt]));
					lBlFlag = true;
				}
			}

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private Date getLastDate(Integer month, Integer year) {

		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		Integer day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		date.setMonth(month);
		date.setYear(year - 1900);
		date.setDate(day);

		return date;
	}

	private Date getFirstDate(Integer month, Integer year) {

		Date date = new Date();

		date.setMonth(month);
		date.setYear(year - 1900);
		date.setDate(1);

		return date;
	}

	private StringBuilder getResponseXMLDocForSchemeFromBillGroup(
			String schemeName, Long schemeCode) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("  <schemename>");
		lStrBldXML.append(schemeName);
		lStrBldXML.append("  </schemename>");
		lStrBldXML.append("  <schemecode>");
		lStrBldXML.append(schemeCode);
		lStrBldXML.append("  </schemecode>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.ArrearsService#getEmpDtlsFromDDODesig(java.util
	 * .Map)
	 */
	public ResultObject getEmpDtlsFromDDODesig(Map inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		List<MstEmp> lLstEmpDtls = null;
		List lLstEmpId = new ArrayList();
		List lLstEmpName = new ArrayList();

		ArrearsDAO lObjArrearsDAO = null;
		String lStrDesigId = null;
		StringBuilder lStrBldXML = null;
		try {
			setSessionInfo(inputMap);
			lStrDesigId = StringUtility.getParameter("cmbDesignation", request);
			lObjArrearsDAO = new ArrearsDAOImpl(MstEmp.class, servLoc
					.getSessionFactory());
			lLstEmpDtls = lObjArrearsDAO.getEmpDtlsFromDDODesig(lStrDesigId,
					inputMap);
			Iterator it = lLstEmpDtls.iterator();
			Object[] tuple = null;
			int lIntLoopJ = 0;
			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				String id = tuple[0].toString();
				String name = tuple[1].toString();
				lLstEmpId.add(id);
				lLstEmpName.add(name);
				lIntLoopJ++;
			}

			lStrBldXML = getResponseXMLDoc(inputMap, lLstEmpId, lLstEmpName);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			e.printStackTrace();
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Map inputMap, List lLstEmpId,
			List lLstEmpName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC1>");
		lStrBldXML.append("  <LISTEMPIDS>");
		lStrBldXML.append(lLstEmpId);
		lStrBldXML.append("  </LISTEMPIDS>");
		lStrBldXML.append("  <LISTEMPNAMES>");
		lStrBldXML.append(lLstEmpName);
		lStrBldXML.append("  </LISTEMPNAMES>");
		lStrBldXML.append("  <LISTEMPDTLSSIZE>");
		lStrBldXML.append(lLstEmpName.size());
		lStrBldXML.append("  </LISTEMPDTLSSIZE>");
		lStrBldXML.append("</XMLDOC1>");

		return lStrBldXML;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.ArrearsService#getEmpNameFromId(java.util.Map)
	 */
	public ResultObject getEmpNameFromId(Map inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ArrearsDAO lObjArrearsDAO = null;
		StringBuilder lStrBldXML = null;
		Long lLngdcpsEmpId = null;
		String lStrEmpName = null;
		try {
			setSessionInfo(inputMap);
			if (StringUtility.getParameter("txtEmployeeId", request) != null
					&& StringUtility.getParameter("txtEmployeeId", request)
							.length() > 0) {
				lLngdcpsEmpId = Long.valueOf(StringUtility.getParameter(
						"txtEmployeeId", request));
			}
			lObjArrearsDAO = new ArrearsDAOImpl(MstEmp.class, servLoc
					.getSessionFactory());
			lStrEmpName = lObjArrearsDAO.getEmpNameFromId(lLngdcpsEmpId);
			lStrBldXML = getResponseXMLDoc(inputMap, lStrEmpName);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			e.printStackTrace();
		}

		return resObj;
	}

	private StringBuilder getResponseXMLDoc(Map inputMap, String lStrEmpName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC2>");
		lStrBldXML.append("<EMPNAME>");
		lStrBldXML.append(lStrEmpName);
		lStrBldXML.append("</EMPNAME>");
		lStrBldXML.append("</XMLDOC2>");

		return lStrBldXML;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.ArrearsService#loadSavedFormForTier(java.util
	 * .Map)
	 */
	public ResultObject loadSavedFormForTier(Map inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ArrearsDAO lObjArrearsDAO = null;
		List listSavedRequestsForTier = null;
		Integer lIntCriteria = null;
		try {
			lIntCriteria = Integer.parseInt(StringUtility.getParameter(
					"Criteria", request).trim());

			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			lObjArrearsDAO = new ArrearsDAOImpl(MstDcpsTierICntrnbtn.class,
					servLoc.getSessionFactory());
			listSavedRequestsForTier = lObjArrearsDAO
					.getAllSavedRequestsForTier(lIntCriteria, lStrPostId);
			List UserList = getHierarchyUsers(inputMap);

			inputMap.put("UserList", UserList);
			inputMap.put("Criteria", lIntCriteria);
			inputMap.put("CaseList", listSavedRequestsForTier);

			resObj.setViewName("DCPSTierForms");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;

	}

	private List getHierarchyUsers(Map inputMap, String lStrUser) {

		int llFromLevelId = 0;
		List UserList = new ArrayList<String>();
		try {
			setSessionInfo(inputMap);
			String subjectName = null;
			// Get the Subject Name
			if (lStrUser.equals("ATO")) {
				subjectName = gObjRsrcBndle.getString("DCPS.Contribution");
			}

			// Get the Hierarchy Id
			Long lLngHierRefId = WorkFlowHelper
					.getHierarchyByPostIDAndDescription(gStrPostId,
							subjectName, inputMap);

			// Get the From level Id
			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId,
					lLngHierRefId, inputMap);

			// Get the List of Post ID of the users at the next Level
			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId,
					lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (int i = 0; i < rsltList.size(); i++) {

				lObjNextPost = (Object[]) rsltList.get(i);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return UserList;
	}

	public ResultObject rejectRequestToATO(Map objectArgs) {

		objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");
		Boolean lBlFlag = false;
		setSessionInfo(objectArgs);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		ServiceLocator servLoc = (ServiceLocator) objectArgs
				.get("serviceLocator");
		ArrearsDAO ArrearsDAOObj = new ArrearsDAOImpl(
				TrnDcpsContribution.class, servLoc.getSessionFactory());

		try {
			String strPKValue = StringUtility.getParameter(
					"dcpsContributionIds", request).toString().trim();
			gObjRsrcBndle.getString("DCPS.ATO");

			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("FromPostId", gStrPostId);
			objectArgs.put("jobTitle", gObjRsrcBndle
					.getString("DCPS.Contribution"));
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.ContributionID")));

			TrnDcpsContribution lObjTrnDcpsContribution = null;

			for (int index = 0; index < strArrPKValue.length; index++) {
				objectArgs.put("Pkvalue", strArrPKValue[index]);
				WorkFlowDelegate.returnDoc(objectArgs);

				lObjTrnDcpsContribution = new TrnDcpsContribution();
				lObjTrnDcpsContribution = (TrnDcpsContribution) ArrearsDAOObj
						.read(Long.valueOf(strArrPKValue[index]));
				lObjTrnDcpsContribution.setRegStatus(-1l);

				lBlFlag = true;
			}

			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			objectArgs.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(objectArgs);

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}
		return resObj;
	}

	private List getHierarchyUsers(Map inputMap) {

		int llFromLevelId = 0;
		List UserList = new ArrayList<String>();
		try {
			setSessionInfo(inputMap);
			// Get the Subject Name
			String subjectName = gObjRsrcBndle.getString("DCPS.SixthPCArrears");

			// Get the Hierarchy Id
			Long lLngHierRefId = WorkFlowHelper
					.getHierarchyByPostIDAndDescription(gStrPostId,
							subjectName, inputMap);

			// Get the From level Id
			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId,
					lLngHierRefId, inputMap);

			// Get the List of Post ID of the users at the next Level
			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId,
					lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (int i = 0; i < rsltList.size(); i++) {

				lObjNextPost = (Object[]) rsltList.get(i);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return UserList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.ArrearsService#createandfrwrdWF(java.util.Map)
	 */
	public ResultObject createandfrwrdWF(Map objectArgs) {

		HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String sixthPCId = null;
		String amounts = null;
		String toPost = null;
		Long lLngHeirRefId = null;
		ArrearsDAO lObjArrearsDAO = null;
		String subjectName = null;
		try {

			setSessionInfo(objectArgs);
			toPost = StringUtility.getParameter("ForwardToPost", request)
					.toString();
			subjectName = gObjRsrcBndle.getString("DCPS.SixthPCArrears");
			lLngHeirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(
					gLngPostId.toString(), subjectName, objectArgs);
			String strPKValue = StringUtility.getParameter("SixthPC_Id",
					request).toString().trim();
			String[] strArrPKValue = strPKValue.split("~");
			String lStrAmounts = StringUtility.getParameter("amounts", request);
			String[] lArrStrAmounts = lStrAmounts.split("~");

			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.SixthPCArrearsID")));
			objectArgs.put("DisplayJobTitle", subjectName);
			objectArgs.put("HeirRefId", lLngHeirRefId);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("toPost", toPost);
			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("toLevel", gObjRsrcBndle.getString("DCPS.DDO"));

			lObjArrearsDAO = new ArrearsDAOImpl(MstSixPCArrears.class, serv
					.getSessionFactory());
			Long lLngArrAmount[] = new Long[lArrStrAmounts.length + 1];
			for (int index = 0; index < lArrStrAmounts.length; index++) {
				amounts = lArrStrAmounts[index];
				lLngArrAmount[index] = Long.parseLong(amounts);

			}

			for (int index = 0; index < strArrPKValue.length; index++) {
				sixthPCId = strArrPKValue[index];

				objectArgs.put("Pkvalue", sixthPCId);
				WorkFlowDelegate.create(objectArgs);
				WorkFlowDelegate.forward(objectArgs);
				Long lLongPKValue = Long.parseLong(strArrPKValue[index]);

				MstSixPCArrears lObjMstSixPCArrears = (MstSixPCArrears) lObjArrearsDAO
						.read(lLongPKValue);
				lObjMstSixPCArrears.setTotalAmount(lLngArrAmount[index]);
				lObjMstSixPCArrears.setStatusFlag('F');
				lObjMstSixPCArrears.setUpdatedUserId(gLngUserId);
				lObjMstSixPCArrears.setUpdatedPostId(gLngPostId);
				lObjMstSixPCArrears.setUpdatedDate(gDtCurDate);

			}

			objectArgs.put("ajaxKey", "Success");
			resObj.setViewName("ajaxData");
			resObj.setResultValue(objectArgs);

		} catch (Exception ex) {
			ex.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.ArrearsService#forwardDCPSArrearsYearly(java
	 * .util.Map)
	 */
	public ResultObject forwardDCPSArrearsYearly(Map objectArgs) {

		HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String sixthPCId = null;
		String toPost = null;
		Long lLngHeirRefId = null;
		ArrearsDAO lObjArrearsDAO = null;
		String subjectName = null;
		try {

			setSessionInfo(objectArgs);
			toPost = StringUtility.getParameter("ForwardToPost", request)
					.toString();
			subjectName = gObjRsrcBndle.getString("DCPS.SixthPCArrears");
			lLngHeirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(
					gLngPostId.toString(), subjectName, objectArgs);
			String strPKValue = StringUtility.getParameter(
					"dcpsSixPCYearlyIds", request).toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			lObjArrearsDAO = new ArrearsDAOImpl(RltDcpsSixPCYearly.class, serv
					.getSessionFactory());

			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.SixthPCArrearsID")));
			objectArgs.put("DisplayJobTitle", subjectName);
			objectArgs.put("HeirRefId", lLngHeirRefId);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("toPost", toPost);
			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("toLevel", gObjRsrcBndle.getString("DCPS.DDO"));

			for (int index = 0; index < strArrPKValue.length; index++) {
				sixthPCId = strArrPKValue[index];
				objectArgs.put("Pkvalue", sixthPCId);
				WorkFlowDelegate.create(objectArgs);
				WorkFlowDelegate.forward(objectArgs);

				Long lLongPKValue = Long.parseLong(strArrPKValue[index]);
				RltDcpsSixPCYearly lObjRltDcpsSixPCYearly = (RltDcpsSixPCYearly) lObjArrearsDAO
						.read(lLongPKValue);

				lObjRltDcpsSixPCYearly.setStatusFlag('F');
				lObjRltDcpsSixPCYearly.setUpdatedUserId(gLngUserId);
				lObjRltDcpsSixPCYearly.setUpdatedPostId(gLngPostId);
				lObjRltDcpsSixPCYearly.setUpdatedDate(gDtCurDate);

			}

			objectArgs.put("ajaxKey", "Success");
			resObj.setViewName("ajaxData");
			resObj.setResultValue(objectArgs);

		} catch (Exception ex) {
			ex.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	/**
	 * to load DCPS tier
	 */
	public ResultObject loadDCPSTier(Map<String, Object> inputMap) {

		// TODO Auto-generated method stub

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List lLstTypeOfArrear = null;
		List lLstDesignation = null;
		List lLstDepartment = null;
		String lStrDesignation = null;
		String lStrTierType = null;
		String lStrStatusFlag = null;
		List lLstTierDtlsFromDesig = null;
		List lLstUpperUsers = null;

		try {
			setSessionInfo(inputMap);

			lStrStatusFlag = StringUtility.getParameter("StatusFlag", request);
			lStrTierType = StringUtility.getParameter("TierType", request);
			lLstUpperUsers = getHierarchyUsers(inputMap);
			lLstTypeOfArrear = IFMSCommonServiceImpl
					.getLookupValues("TypeOfArrear", SessionHelper
							.getLangId(inputMap), inputMap);
			gLogger.info("here is the size of event list :"
					+ lLstTypeOfArrear.size());
			Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle
					.getString("DCPS.DEPARTMENTID"));
			ArrearsDAO lObjcmnDCPSArrearsDAO = new ArrearsDAOImpl(null, serv
					.getSessionFactory());
			lLstDepartment = lObjcmnDCPSArrearsDAO.getAllDepartment(
					lLngDepartmentId, SessionHelper.getLangId(inputMap));
			lLstDesignation = lObjcmnDCPSArrearsDAO
					.getAllDesignation(SessionHelper.getLangId(inputMap));

			if (StringUtility.getParameter("TierDraft", request) != null
					&& StringUtility.getParameter("TierDraft", request)
							.length() > 0) {
				StringUtility.getParameter("TierDraft", request);
				lStrDesignation = StringUtility.getParameter("Designation",
						request);
				lObjcmnDCPSArrearsDAO = new ArrearsDAOImpl(
						MstDcpsTierICntrnbtn.class, serv.getSessionFactory());
				lLstTierDtlsFromDesig = lObjcmnDCPSArrearsDAO
						.getTierDtlFromDesig(lStrDesignation);
				inputMap.put("TierDraftList", lLstTierDtlsFromDesig);

			}
			inputMap.put("StatusFlag", lStrStatusFlag);
			inputMap.put("UserList", lLstUpperUsers);
			inputMap.put("TierType", lStrTierType);
			inputMap.put("lLstDepartment", lLstDepartment);
			inputMap.put("lLstDesignation", lLstDesignation);
			inputMap.put("lLstTypeOfArrear", lLstTypeOfArrear);
			gLogger.info("Load Sucessful");
		}

		catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}
		resObj.setResultValue(inputMap);
		resObj.setViewName("dcpsArrearsTierI");
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.ArrearsService#forwardTierDDO(java.util.Map)
	 */
	public ResultObject forwardTierDDO(Map objectArgs) throws Exception {

		HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String sixthPCId = null;
		String toPost = null;
		Long lLngHeirRefId = null;
		ArrearsDAO lObjArrearsDAO = null;
		String subjectName = null;
		MstDcpsTierICntrnbtn lObjMstDcpsTierICntrnbtn = null;
		Long lLngTierICntrnbtnId = null;
		Long lLongPKValue = null;
		try {

			setSessionInfo(objectArgs);
			toPost = StringUtility.getParameter("ForwardToPost", request)
					.toString();
			subjectName = gObjRsrcBndle.getString("DCPS.TierCntrbtn");
			lLngHeirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(
					gLngPostId.toString(), subjectName, objectArgs);
			String strPKValue = StringUtility.getParameter("Emp_Id", request)
					.toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.TierCntrbtnFormID")));
			objectArgs.put("DisplayJobTitle", subjectName);
			objectArgs.put("HeirRefId", lLngHeirRefId);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("toPost", toPost);
			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("toLevel", gObjRsrcBndle.getString("DCPS.DDO"));

			lObjArrearsDAO = new ArrearsDAOImpl(MstDcpsTierICntrnbtn.class,
					serv.getSessionFactory());
			for (int index = 0; index < strArrPKValue.length; index++) {
				sixthPCId = strArrPKValue[index];
				objectArgs.put("Pkvalue", sixthPCId);
				WorkFlowDelegate.create(objectArgs);
				WorkFlowDelegate.forward(objectArgs);

				lLongPKValue = Long.parseLong(strArrPKValue[index]);
				lLngTierICntrnbtnId = lObjArrearsDAO
						.getMstDcpsTierICntrnbtnPk(lLongPKValue);
				//System.out.println("lLngTierICntrnbtnId is "
				//		+ lLngTierICntrnbtnId);
				lObjMstDcpsTierICntrnbtn = (MstDcpsTierICntrnbtn) lObjArrearsDAO
						.read(lLngTierICntrnbtnId);
				lObjMstDcpsTierICntrnbtn.getCreatedDate();
				lObjMstDcpsTierICntrnbtn.setStatusFlag('F');
				lObjMstDcpsTierICntrnbtn.setUpdatedUserId(gLngUserId);
				lObjMstDcpsTierICntrnbtn.setUpdatedPostId(gLngPostId);
				lObjMstDcpsTierICntrnbtn.setUpdatedDate(gDtCurDate);

			}

			objectArgs.put("ajaxKey", "Success");
			resObj.setViewName("ajaxData");
			resObj.setResultValue(objectArgs);

		} catch (Exception ex) {
			ex.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

}
