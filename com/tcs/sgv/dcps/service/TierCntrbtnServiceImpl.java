/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 8, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.tcs.sgv.dcps.dao.MstDcpsTierICntrnbtnDAO;
import com.tcs.sgv.dcps.dao.MstDcpsTierICntrnbtnDAOImpl;
import com.tcs.sgv.dcps.dao.SixPCArrearsDAO;
import com.tcs.sgv.dcps.dao.SixPCArrearsDAOImpl;
import com.tcs.sgv.dcps.dao.TierCntrbtnDAO;
import com.tcs.sgv.dcps.dao.TierCntrbtnDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsTierICntrnbtn;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstSixPCArrears;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Apr 8, 2011
 */
public class TierCntrbtnServiceImpl extends ServiceImpl implements TierCntrbtnService {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TierCntrbtnService#forwardTierDDO(java.util.Map)
	 */
	public ResultObject forwardTierDDO(Map objectArgs) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrsixthPCId = null;
		String jobRefId = null;
		String toPost = null;
		String toLevel = null;
		String jobMvmtSeqId = null;
		Long lLngHeirRefId = null;
		TierCntrbtnDAO lObjTierCntrbtnDAO = null;
		String subjectName = null;
		String lStrTierType = null;
		
		Long lLngTierICntrnbtnId = null;
		Long lLongPKValue = null;
		try {

			setSessionInfo(objectArgs);
			toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			lStrTierType = StringUtility.getParameter("TierType", request).toString();
			subjectName = gObjRsrcBndle.getString("DCPS.TierCntrbtn");
			lLngHeirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(
					gLngPostId.toString(), subjectName, objectArgs);
			String strPKValue = StringUtility.getParameter("Emp_Id", request)
					.toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.TierCntrbtnFormID")));
			objectArgs.put("DisplayJobTitle", subjectName);
			objectArgs.put("HeirRefId", lLngHeirRefId);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("toPost", toPost);
			objectArgs.put("toLevel", gObjRsrcBndle.getString("DCPS.DDO"));

			lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(null,
					serv.getSessionFactory());
			for (int index = 0; index < strArrPKValue.length; index++) {
				lStrsixthPCId = strArrPKValue[index].trim();
				objectArgs.put("Pkvalue", lStrsixthPCId);
				WorkFlowDelegate.create(objectArgs);
				WorkFlowDelegate.forward(objectArgs);

				lLongPKValue = Long.parseLong(lStrsixthPCId);
				lLngTierICntrnbtnId = lObjTierCntrbtnDAO.getMstDcpsTierICntrnbtnPk(lLongPKValue);
				
				lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(MstDcpsTierICntrnbtn.class,serv.getSessionFactory());
				MstDcpsTierICntrnbtn lObjMstDcpsTierICntrnbtn = (MstDcpsTierICntrnbtn) lObjTierCntrbtnDAO.read(lLngTierICntrnbtnId);
				
				
				lObjMstDcpsTierICntrnbtn.setStatusFlag('F');
				lObjMstDcpsTierICntrnbtn.setUpdatedUserId(gLngUserId);
				lObjMstDcpsTierICntrnbtn.setUpdatedPostId(gLngPostId);
				lObjMstDcpsTierICntrnbtn.setUpdatedDate(gDtCurDate);
				

			}
			StringBuilder lStrBldXML = tierTypeResponseXMLDoc(lStrTierType);

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lStrBldXML.toString()).toString();
			
			objectArgs.put("ajaxKey", lStrResult);
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
	 * com.tcs.sgv.dcps.service.TierCntrbtnService#getDCPSTierI(java.util.Map)
	 */
	public ResultObject getDCPSTierI(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstEmpData = null;

		try {

			setSessionInfo(inputMap);
			MstDcpsTierICntrnbtnDAO lObjMstDcpsTierICntrnbtnDAO = new MstDcpsTierICntrnbtnDAOImpl(MstEmp.class, serv.getSessionFactory());
		

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TierCntrbtnService#insertDCPSTierI(java.util
	 * .Map)
	 */
	public ResultObject insertDCPSTierI(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		
		Long lngTierICntrnbtnId = null;

		try {
			setSessionInfo(inputMap);
			ArrayList<MstDcpsTierICntrnbtn> lArryLstMstDcpsTierICntrnbtnVO = new ArrayList<MstDcpsTierICntrnbtn>();

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TierCntrbtnService#loadDCPSTier(java.util.Map)
	 */
	public ResultObject loadDCPSTier(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TierCntrbtnDAO lObjTierCntrbtnDAO = null;
		DcpsCommonService lObjDcpsCommonService = null;
		List lLstTypeOfArrear = null;
		List lLstDesignation = null;
		List lLstDepartment = null;
		String lStrTierDraft = null;
		String lStrDesignation = null;
		String lStrTierType = null;
		String lStrStatusFlag = null;
		String lStrUserType = null;
		List lLstTierDtlsFromDesig = null;
		List lLstUpperUsers = null;
		

		try {
			setSessionInfo(inputMap);

			lStrStatusFlag = StringUtility.getParameter("StatusFlag", request);
			lStrUserType = StringUtility.getParameter("UserType", request);
			lStrTierType = StringUtility.getParameter("TierType", request);
			lLstUpperUsers = getHierarchyUsers(inputMap);
			lLstTypeOfArrear = IFMSCommonServiceImpl
					.getLookupValues("TypeOfArrear", SessionHelper
							.getLangId(inputMap), inputMap);
			gLogger.info("here is the size of event list :"
					+ lLstTypeOfArrear.size());
			Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle
					.getString("DCPS.DEPARTMENTID"));
			lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(TierCntrbtnServiceImpl.class, serv.getSessionFactory());
			lObjDcpsCommonService = new DcpsCommonServiceImpl(TierCntrbtnServiceImpl.class,serv.getSessionFactory());
			lLstDepartment = lObjDcpsCommonService.getAllDepartment(
					lLngDepartmentId, SessionHelper.getLangId(inputMap));
			lLstDesignation = lObjDcpsCommonService
					.getAllDesignation(SessionHelper.getLangId(inputMap));

			if (StringUtility.getParameter("TierDraft", request) != null
					&& StringUtility.getParameter("TierDraft", request)
							.length() > 0) {
				lStrTierDraft = StringUtility
						.getParameter("TierDraft", request);
				lStrDesignation = StringUtility.getParameter("Designation",
						request);
				lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(
						MstDcpsTierICntrnbtn.class, serv.getSessionFactory());
				lLstTierDtlsFromDesig = lObjTierCntrbtnDAO
						.getTierDtlFromDesig(lStrDesignation);
				inputMap.put("TierDraftList", lLstTierDtlsFromDesig);

			}
			inputMap.put("StatusFlag", lStrStatusFlag);
			inputMap.put("UserType", lStrUserType);
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
	 * com.tcs.sgv.dcps.service.TierCntrbtnService#loadDCPSTierDDO(java.util
	 * .Map)
	 */
	public ResultObject loadDCPSTierDDO(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TierCntrbtnDAO lObjTierCntrbtnDAO = null;
		DcpsCommonService lObjDcpsCommonService = null;
		List lLstTypeOfArrear = null;
		List lLstDesignation = null;
		List lLstDepartment = null;
		String lStrTierDraft = null;
		String lStrDesignation = null;
		List lLstTierDtlsFromDesig = null;
		String lStrTierType = null;
		String lStrUserType = null;
		
		try {
			setSessionInfo(inputMap);

			lStrTierType = StringUtility.getParameter("TierType", request);
			lStrUserType = StringUtility.getParameter("UserType", request);
			lLstTypeOfArrear = IFMSCommonServiceImpl
					.getLookupValues("TypeOfArrear", SessionHelper
							.getLangId(inputMap), inputMap);
			gLogger.info("here is the size of event list :"
					+ lLstTypeOfArrear.size());
			Long lLngDepartmentId = Long.valueOf(gObjRsrcBndle
					.getString("DCPS.DEPARTMENTID"));
			lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(TierCntrbtnServiceImpl.class, serv.getSessionFactory());
			lObjDcpsCommonService = new DcpsCommonServiceImpl(TierCntrbtnServiceImpl.class,serv.getSessionFactory());
			lLstDepartment = lObjDcpsCommonService.getAllDepartment(
					lLngDepartmentId, SessionHelper.getLangId(inputMap));
			lLstDesignation = lObjDcpsCommonService
					.getAllDesignation(SessionHelper.getLangId(inputMap));

			if (StringUtility.getParameter("TierDraft", request) != null
					&& StringUtility.getParameter("TierDraft", request)
							.length() > 0) {
				lStrTierDraft = StringUtility
						.getParameter("TierDraft", request);
				lStrDesignation = StringUtility.getParameter("Designation",
						request);
				lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(
						MstDcpsTierICntrnbtn.class, serv.getSessionFactory());
				lLstTierDtlsFromDesig = lObjTierCntrbtnDAO
						.getTierDtlFromDesig(lStrDesignation);
				inputMap.put("TierDraftList", lLstTierDtlsFromDesig);

			}
			inputMap.put("TierType", lStrTierType);
			inputMap.put("UserType", lStrUserType);
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

	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.service.TierCntrbtnService#loadSavedFormForTier(java.util.Map)
	 */
	public ResultObject loadSavedFormForTier(Map inputMap) throws Exception 
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TierCntrbtnDAO lObjTierCntrbtnDAO = null;
		DcpsCommonService lObjDcpsCommonService = null;
		List lListSavedRequestsForTier = null;
		List lLstDesignation = null;
		Integer lIntCriteria = null;
		List UserList = null;
		String lStrUserType = null;
		try {
			setSessionInfo(inputMap);
			lIntCriteria = Integer.parseInt(StringUtility.getParameter("Criteria", request).trim());
			lStrUserType = StringUtility.getParameter("UserType", request);
			lObjDcpsCommonService = new DcpsCommonServiceImpl(MstDcpsTierICntrnbtn.class,serv.getSessionFactory());
			lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(MstDcpsTierICntrnbtn.class,serv.getSessionFactory());
			lLstDesignation = lObjDcpsCommonService.getAllDesignation(SessionHelper.getLangId(inputMap));
			lListSavedRequestsForTier = lObjTierCntrbtnDAO.getAllSavedRequestsForTier(lIntCriteria, gStrPostId);
			UserList = getHierarchyUsers(inputMap);

			inputMap.put("lLstDesignation", lLstDesignation);
			inputMap.put("UserType",lStrUserType);
			inputMap.put("UserList", UserList);
			inputMap.put("Criteria", lIntCriteria);
			inputMap.put("CaseList", lListSavedRequestsForTier);

			resObj.setViewName("DCPSTierForms");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resObj;

	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.service.TierCntrbtnService#approveTierCase(java.util.Map)
	 */
	public ResultObject approveTierCase(Map objectArgs) throws Exception 
	{

		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrsixthPCId = null;
		String jobRefId = null;
		String toPost = null;
		String toLevel = null;
		String jobMvmtSeqId = null;
		Long lLngHeirRefId = null;
		TierCntrbtnDAO lObjTierCntrbtnDAO = null;
		String subjectName = null;
		String lStrTierType  = null;
		
		
		Long lLngTierICntrnbtnId = null;
		Long lLongPKValue = null;
		try {

			setSessionInfo(objectArgs);
			toPost = StringUtility.getParameter("ForwardToPost", request)
					.toString();
			lStrTierType = StringUtility.getParameter("TierType", request).toString();
			subjectName = gObjRsrcBndle.getString("DCPS.TierCntrbtn");
			lLngHeirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(
					gLngPostId.toString(), subjectName, objectArgs);
			String strPKValue = StringUtility.getParameter("Emp_Id", request)
					.toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.TierCntrbtnFormID")));
			objectArgs.put("DisplayJobTitle", subjectName);
			objectArgs.put("HeirRefId", lLngHeirRefId);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("toPost", toPost);
			objectArgs.put("toLevel", gObjRsrcBndle.getString("DCPS.DDO"));

			lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(null,
					serv.getSessionFactory());
			for (int index = 0; index < strArrPKValue.length; index++) {
				lStrsixthPCId = strArrPKValue[index].trim();
				objectArgs.put("Pkvalue", lStrsixthPCId);
				
				lLongPKValue = Long.parseLong(lStrsixthPCId);
				lLngTierICntrnbtnId = lObjTierCntrbtnDAO.getMstDcpsTierICntrnbtnPk(lLongPKValue);
				
				lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(MstDcpsTierICntrnbtn.class,serv.getSessionFactory());
				MstDcpsTierICntrnbtn lObjMstDcpsTierICntrnbtn = (MstDcpsTierICntrnbtn) lObjTierCntrbtnDAO.read(lLngTierICntrnbtnId);
				
				
				lObjMstDcpsTierICntrnbtn.setStatusFlag('A');
				lObjMstDcpsTierICntrnbtn.setUpdatedUserId(gLngUserId);
				lObjMstDcpsTierICntrnbtn.setUpdatedPostId(gLngPostId);
				lObjMstDcpsTierICntrnbtn.setUpdatedDate(gDtCurDate);
				

			}

			StringBuilder lStrBldXML = tierTypeResponseXMLDoc(lStrTierType);

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lStrBldXML.toString()).toString();
			
			objectArgs.put("ajaxKey", lStrResult);
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

	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.service.TierCntrbtnService#rejectTierCase(java.util.Map)
	 */
	public ResultObject rejectTierCase(Map objectArgs) throws Exception 
	{
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrsixthPCId = null;
		String jobRefId = null;
		String toPost = null;
		String toLevel = null;
		String jobMvmtSeqId = null;
		Long lLngHeirRefId = null;
		TierCntrbtnDAO lObjTierCntrbtnDAO = null;
		String subjectName = null;
		String lStrRemarks = null;
		Long lLngTierICntrnbtnId = null;
		Long lLongPKValue = null;
		String lStrTierType = null;
		try {

			setSessionInfo(objectArgs);
			lStrTierType = StringUtility.getParameter("TierType", request).toString();
			lStrRemarks = StringUtility.getParameter("remarks", request).toString().trim();
			subjectName = gObjRsrcBndle.getString("DCPS.TierCntrbtn");
			lLngHeirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(
					gLngPostId.toString(), subjectName, objectArgs);
			String strPKValue = StringUtility.getParameter("Emp_Id", request)
					.toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			
			objectArgs.put("FromPostId",gStrPostId);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle",subjectName);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.TierCntrbtnFormID")));
			
			
			

			lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(null,
					serv.getSessionFactory());
			for (int index = 0; index < strArrPKValue.length; index++) {
				lStrsixthPCId = strArrPKValue[index].trim();
				objectArgs.put("Pkvalue", lStrsixthPCId);
				
				WorkFlowDelegate.returnDoc(objectArgs);

				lLongPKValue = Long.parseLong(lStrsixthPCId);
				lLngTierICntrnbtnId = lObjTierCntrbtnDAO.getMstDcpsTierICntrnbtnPk(lLongPKValue);
				
				lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(MstDcpsTierICntrnbtn.class,serv.getSessionFactory());
				MstDcpsTierICntrnbtn lObjMstDcpsTierICntrnbtn = (MstDcpsTierICntrnbtn) lObjTierCntrbtnDAO.read(lLngTierICntrnbtnId);
				
				lObjMstDcpsTierICntrnbtn.setStatusFlag('R');
				lObjMstDcpsTierICntrnbtn.setRemarks(lStrRemarks);
				lObjMstDcpsTierICntrnbtn.setUpdatedUserId(gLngUserId);
				lObjMstDcpsTierICntrnbtn.setUpdatedPostId(gLngPostId);
				lObjMstDcpsTierICntrnbtn.setUpdatedDate(gDtCurDate);

			}

			StringBuilder lStrBldXML = tierTypeResponseXMLDoc(lStrTierType);

			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lStrBldXML.toString()).toString();
			
			objectArgs.put("ajaxKey", lStrResult);
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
	private StringBuilder tierTypeResponseXMLDoc(String lStrTierType) {

		StringBuilder lStrHidPKs = new StringBuilder();

		lStrHidPKs.append("<XMLDOCTIER>");
		lStrHidPKs.append("<TIERTYPE>");
		lStrHidPKs.append(lStrTierType);
		lStrHidPKs.append("</TIERTYPE>");
		lStrHidPKs.append("</XMLDOCTIER>");

		gLogger.info("lStrTierType : " + lStrTierType);

		return lStrHidPKs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TierCntrbtnService#getEmpDtlsFromDDODesig(java.util
	 * .Map)
	 */
	
	public ResultObject getEmpDtlsFromDDODesig(Map inputMap) {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	
		List<MstEmp> lLstEmpDtls = null;
		List lLstEmpId = new ArrayList();
		List lLstEmpName = new ArrayList();

		TierCntrbtnDAO lObjTierCntrbtnDAO = null;
		String lStrDesigId = null;
		StringBuilder lStrBldXML = null;
		try 
		{
			setSessionInfo(inputMap);
			lStrDesigId = StringUtility.getParameter("cmbDesignation", request);
			lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(MstEmp.class, serv.getSessionFactory());
			lLstEmpDtls = lObjTierCntrbtnDAO.getEmpDtlsFromDDODesig(lStrDesigId,inputMap);
			Iterator it = lLstEmpDtls.iterator();
			Object[] tuple = null;
			int lIntLoopJ = 0;
			while (it.hasNext()) 
			{
				tuple = (Object[]) it.next();
				String id = tuple[0].toString();
				String name = tuple[1].toString();
				lLstEmpId.add(id);
				lLstEmpName.add(name);
				lIntLoopJ++;
			}

			lStrBldXML = getResponseXMLDoc(inputMap, lLstEmpId, lLstEmpName);
			gLogger.info(" lStrBldXML :: " + lStrBldXML);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lStrBldXML.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} 
		catch (Exception e) 
		{
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
	 * com.tcs.sgv.dcps.service.TierCntrbtnService#getEmpNameFromId(java.util.Map)
	 */
	public ResultObject getEmpNameFromId(Map inputMap) {

		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		
		TierCntrbtnDAO lObjTierCntrbtnDAO = null;
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
			lObjTierCntrbtnDAO = new TierCntrbtnDAOImpl(MstEmp.class, serv.getSessionFactory());
			lStrEmpName = lObjTierCntrbtnDAO.getEmpNameFromId(lLngdcpsEmpId);
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

	
	
}
