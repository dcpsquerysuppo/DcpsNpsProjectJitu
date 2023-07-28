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
import com.tcs.sgv.dcps.dao.SixPCArrearsDAO;
import com.tcs.sgv.dcps.dao.SixPCArrearsDAOImpl;
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
public class SixPCArrearsServiceImpl extends ServiceImpl implements
		SixPCArrearsService {

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
	 * com.tcs.sgv.dcps.service.sixPCArrearsService#approveSixthPCArrears(java
	 * .util.Map)
	 */
	public ResultObject approveSixthPCArrears(Map objectArgs) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrsixthPCId = null;
		SixPCArrearsDAO lObjSixPCArrearsDAO = null;
		String subjectName = null;
		Boolean lBlFlag = false ;
		try {

			setSessionInfo(objectArgs);
			
			//subjectName = gObjRsrcBndle.getString("DCPS.SixthPCArrears");
			String strPKValue = StringUtility.getParameter("SixthPC_Id",request).toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			/*
			objectArgs.put("FromPostId", gStrPostId);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.SixthPCArrearsID")));
			*/

			lObjSixPCArrearsDAO = new SixPCArrearsDAOImpl(
					MstSixPCArrears.class, serv.getSessionFactory());
			for (int index = 0; index < strArrPKValue.length; index++) {
				lStrsixthPCId = strArrPKValue[index];
				objectArgs.put("Pkvalue", lStrsixthPCId);
				Long lLongPKValue = Long.parseLong(strArrPKValue[index]);
				MstSixPCArrears lObjMstSixPCArrears = (MstSixPCArrears) lObjSixPCArrearsDAO
						.read(lLongPKValue);

				lObjMstSixPCArrears.setStatusFlag('A');
				lObjMstSixPCArrears.setRemarks(null);
				lObjMstSixPCArrears.setUpdatedUserId(gLngUserId);
				lObjMstSixPCArrears.setUpdatedPostId(gLngPostId);
				lObjMstSixPCArrears.setUpdatedDate(gDtCurDate);

			}

			resObj = serv.executeService("SIXPC_ARREARS_YEARLY_VOGEN",
					objectArgs);

			resObj = serv.executeService("SIXPC_ARREARS_YEARLY_SRVC",
					objectArgs);
			
			lBlFlag = true ;
			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
					.toString();

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
	 * com.tcs.sgv.dcps.service.sixPCArrearsService#createandfrwrdWF(java.util
	 * .Map)
	 */
	public ResultObject createandfrwrdWF(Map objectArgs) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SixPCArrearsDAO lObjSixPCArrearsDAO = null;
		String lStrsixthPCId = null;
		String amounts = null;
		String toPost = null;
		Long lLngHeirRefId = null;
		String subjectName = null;
		Boolean lBlFlag = false;
		
		try {

			setSessionInfo(objectArgs);
			lObjSixPCArrearsDAO = new SixPCArrearsDAOImpl(
					MstSixPCArrears.class, serv.getSessionFactory());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			/*
			
			toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			subjectName = gObjRsrcBndle.getString("DCPS.SixthPCArrears");
			lLngHeirRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gLngPostId.toString(), subjectName, objectArgs);
			
			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.SixthPCArrearsID")));
			objectArgs.put("DisplayJobTitle", subjectName);
			objectArgs.put("HeirRefId", lLngHeirRefId);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("toPost", toPost);
			objectArgs.put("Hierarchy_ref_id", lLngHeirRefId);
			objectArgs.put("toLevel", gObjRsrcBndle.getString("DCPS.DDO"));
			
			*/
			
			String strPKValue = StringUtility.getParameter("SixthPC_Id",
					request).toString().trim();
			String[] strArrPKValue = strPKValue.split("~");
			String lStrAmounts = StringUtility.getParameter("amounts", request);
			String[] lArrStrAmounts = lStrAmounts.split("~");

			
			Long lLngArrAmount[] = new Long[lArrStrAmounts.length + 1];
			for (int index = 0; index < lArrStrAmounts.length; index++) {
				amounts = lArrStrAmounts[index];
				lLngArrAmount[index] = Long.parseLong(amounts);

			}
			
			String lStrFromDates = null;
			if(!StringUtility.getParameter("fromDates", request).equalsIgnoreCase(""))
			{
				lStrFromDates = StringUtility.getParameter("fromDates", request);
			}
			
			String[] lStrArrFromDates = lStrFromDates.split("~");
			Date[] lDateArrFromDates = new Date[lStrArrFromDates.length];
			
			for (Integer lInt = 0; lInt < lStrArrFromDates.length; lInt++) {
				if (lStrArrFromDates[lInt] != null
						&& !"".equals(lStrArrFromDates[lInt].trim())) {
					lDateArrFromDates[lInt] = sdf.parse(lStrArrFromDates[lInt]);
				}
			}
			
			String lStrToDates = null;
			if(!StringUtility.getParameter("toDates", request).equalsIgnoreCase(""))
			{
				lStrToDates = StringUtility.getParameter("toDates", request);
			}
			
			String[] lStrArrToDates = lStrToDates.split("~");
			Date[] lDateArrToDates = new Date[lStrArrToDates.length];
			
			for (Integer lInt = 0; lInt < lStrArrToDates.length; lInt++) {
				if (lStrArrToDates[lInt] != null
						&& !"".equals(lStrArrToDates[lInt].trim())) {
					lDateArrToDates[lInt] = sdf.parse(lStrArrToDates[lInt]);
				}
			}

			for (int index = 0; index < strArrPKValue.length; index++) {
				lStrsixthPCId = strArrPKValue[index];

				objectArgs.put("Pkvalue", lStrsixthPCId);
				//WorkFlowDelegate.create(objectArgs);
				//WorkFlowDelegate.forward(objectArgs);
				Long lLongPKValue = Long.parseLong(strArrPKValue[index]);

				MstSixPCArrears lObjMstSixPCArrears = (MstSixPCArrears) lObjSixPCArrearsDAO
						.read(lLongPKValue);
				lObjMstSixPCArrears.setTotalAmount(lLngArrAmount[index]);
				lObjMstSixPCArrears.setStatusFlag('F');
				lObjMstSixPCArrears.setFromDate(lDateArrFromDates[index]);
				lObjMstSixPCArrears.setToDate(lDateArrToDates[index]);
				lObjMstSixPCArrears.setUpdatedUserId(gLngUserId);
				lObjMstSixPCArrears.setUpdatedPostId(gLngPostId);
				lObjMstSixPCArrears.setUpdatedDate(gDtCurDate);
				lObjSixPCArrearsDAO.update(lObjMstSixPCArrears);

			}
			
			lBlFlag = true ;
			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
					.toString();
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
	 * com.tcs.sgv.dcps.service.sixPCArrearsService#loadSixthPCArrearsDDO(java
	 * .util.Map)
	 */
	public ResultObject loadSixthPCArrearsDDO(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrStatusFlag = null;
		List empList = null;
		List lLstUpperUsers = null;
		SixPCArrearsDAO lObjSixPCArrearsDAO = null;
		String lStrDesignation = null;
		Integer totalRecords = null;
		List lLstDesignation = null;

		try {

			setSessionInfo(inputMap);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
			lObjSixPCArrearsDAO = new SixPCArrearsDAOImpl(SixPCArrearsServiceImpl.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			
			String lStrDdoCode = lObjSixPCArrearsDAO
					.getDdoCodeForDDO(gLngPostId);
			String lStrUserType = null;
			
			lStrDesignation = StringUtility.getParameter("cmbDesignation", request) ;
			lStrStatusFlag = StringUtility.getParameter("StatusFlag",request);
			totalRecords = lObjSixPCArrearsDAO.getEmpListForSixPCArrearsDDOCount(lStrDdoCode,lStrDesignation);
			lStrUserType = "DDO";
			
			empList = lObjSixPCArrearsDAO.getEmpListForSixPCArrearsDDO(lStrDdoCode, lStrDesignation, displayTag);
			
			List lLstParentDept = lObjDcpsCommonDAO.getParentDeptForDDO(lStrDdoCode);
			Object[] objParentDept = (Object[]) lLstParentDept.get(0);
			lLstDesignation = lObjDcpsCommonDAO.getAllDesignation((Long) objParentDept[0], gLngLangId);
			inputMap.put("lLstDesignation", lLstDesignation);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String lStrArrearsDateLimit = "01/01/2006" ;
			Date lDateArrearsDateLimit = sdf.parse(lStrArrearsDateLimit);
			inputMap.put("ArrearsDateLimit", lDateArrearsDateLimit);
			
			lLstUpperUsers = getHierarchyUsers(inputMap);
			inputMap.put("UserType", lStrUserType);
			inputMap.put("totalRecords", totalRecords);
			inputMap.put("DDOCODE", lStrDdoCode);
			inputMap.put("DesignationId", lStrDesignation);
			inputMap.put("empList", empList);
			inputMap.put("UserList", lLstUpperUsers);
			inputMap.put("StatusFlag", lStrStatusFlag);

			if (request != null) {
				inputMap.put("REQUEST", request);
			}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.SixPCArrearsService#loadsixthPCArrearsEntry(
	 * java.util.Map)
	 */
	public ResultObject loadsixthPCArrearsEntry(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		
		SixPCArrearsDAO lObjSixPCArrearsDAO = null;
		List empList = null;
		List lLstUpperUsers = null;
		String lStrStatusFlag = null;
		String lStrUserType = null;
		String lStrDesignation = null;
		Integer totalRecords = null;
		List lLstDesignation = null;
		
		try {
			setSessionInfo(inputMap);
			Map displayTag = IFMSCommonServiceImpl.getDisplayPara(request);
	
			lObjSixPCArrearsDAO = new SixPCArrearsDAOImpl(MstEmp.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			
			String lStrDdoCode = lObjSixPCArrearsDAO.getDdoCode(gLngPostId);
			lStrDesignation = StringUtility.getParameter("cmbDesignation", request) ;
			lStrStatusFlag = StringUtility.getParameter("StatusFlag",request);
			lStrUserType = "DDOAsst";
			totalRecords = lObjSixPCArrearsDAO.getEmpListForSixPCArrearsCount(lStrDdoCode,lStrDesignation);
			empList = lObjSixPCArrearsDAO.getEmpListForSixPCArrears(lStrDdoCode, lStrDesignation, displayTag);
			
			List lLstParentDept = lObjDcpsCommonDAO.getParentDeptForDDO(lStrDdoCode);
			Object[] objParentDept = (Object[]) lLstParentDept.get(0);
			lLstDesignation = lObjDcpsCommonDAO.getAllDesignation((Long) objParentDept[0], gLngLangId);
			inputMap.put("lLstDesignation", lLstDesignation);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String lStrArrearsDateLimit = "01/01/2006" ;
			Date lDateArrearsDateLimit = sdf.parse(lStrArrearsDateLimit);
			inputMap.put("ArrearsDateLimit", lDateArrearsDateLimit);
			
			lLstUpperUsers = getHierarchyUsers(inputMap);
			inputMap.put("totalRecords", totalRecords);
			inputMap.put("DesignationId", lStrDesignation);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.sixPCArrearsService#rejectSixthPCArrears(java
	 * .util.Map)
	 */
	public ResultObject rejectSixthPCArrears(Map objectArgs) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String sixthPCId = null;
		String subjectName = null;
		String lStrRemarks = null;
		SixPCArrearsDAO lObjSixPCArrearsDAO = null;
		Boolean lBlFlag = false ;
		try {

			setSessionInfo(objectArgs);
			//subjectName = gObjRsrcBndle.getString("DCPS.SixthPCArrears");
			lStrRemarks = StringUtility.getParameter("remarks", request).toString();
			String strPKValue = StringUtility.getParameter("SixthPC_Id",request).toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			/*
			objectArgs.put("FromPostId", gStrPostId);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.SixthPCArrearsID")));
			*/

			lObjSixPCArrearsDAO = new SixPCArrearsDAOImpl(
					MstSixPCArrears.class, serv.getSessionFactory());
			for (int index = 0; index < strArrPKValue.length; index++) {
				sixthPCId = strArrPKValue[index];
				objectArgs.put("Pkvalue", sixthPCId);
				//WorkFlowDelegate.returnDoc(objectArgs);

				Long lLongPKValue = Long.parseLong(strArrPKValue[index]);
				MstSixPCArrears lObjMstSixPCArrears = (MstSixPCArrears) lObjSixPCArrearsDAO
						.read(lLongPKValue);

				lObjMstSixPCArrears.setStatusFlag('R');
				lObjMstSixPCArrears.setRemarks(lStrRemarks);
				lObjMstSixPCArrears.setUpdatedUserId(gLngUserId);
				lObjMstSixPCArrears.setUpdatedPostId(gLngPostId);
				lObjMstSixPCArrears.setUpdatedDate(gDtCurDate);

			}
			
			lBlFlag = true ;
			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
					.toString();
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
	 * com.tcs.sgv.dcps.service.sixPCArrearsService#saveSixPCArrears(java.util
	 * .Map)
	 */
	public ResultObject saveSixPCArrears(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SixPCArrearsDAO lObjSixPCArrearsDAO = null;
		Boolean lBlFlag = false;
		try {

			setSessionInfo(inputMap);
			lObjSixPCArrearsDAO = new SixPCArrearsDAOImpl(
					MstSixPCArrears.class, serv.getSessionFactory());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
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
			
			String lStrFromDates = null;
			if(!StringUtility.getParameter("fromDates", request).equalsIgnoreCase(""))
			{
				lStrFromDates = StringUtility.getParameter("fromDates", request);
			}
			
			String[] lStrArrFromDates = lStrFromDates.split("~");
			Date[] lDateArrFromDates = new Date[lStrArrFromDates.length];
			
			for (Integer lInt = 0; lInt < lStrArrFromDates.length; lInt++) {
				if (lStrArrFromDates[lInt] != null
						&& !"".equals(lStrArrFromDates[lInt].trim())) {
					lDateArrFromDates[lInt] = sdf.parse(lStrArrFromDates[lInt]);
				}
			}
			
			String lStrToDates = null;
			if(!StringUtility.getParameter("toDates", request).equalsIgnoreCase(""))
			{
				lStrToDates = StringUtility.getParameter("toDates", request);
			}
			
			String[] lStrArrToDates = lStrToDates.split("~");
			Date[] lDateArrToDates = new Date[lStrArrToDates.length];
			
			for (Integer lInt = 0; lInt < lStrArrToDates.length; lInt++) {
				if (lStrArrToDates[lInt] != null
						&& !"".equals(lStrArrToDates[lInt].trim())) {
					lDateArrToDates[lInt] = sdf.parse(lStrArrToDates[lInt]);
				}
			}

			for (Integer lInt = 0; lInt < lArrStrdcpsSixPCIds.length; lInt++) {
				MstSixPCArrears MstSixPCArrearsObj = (MstSixPCArrears) lObjSixPCArrearsDAO
						.read(lArrLongdcpsSixPCIds[lInt]);
				MstSixPCArrearsObj.setTotalAmount(lArrLongAmounts[lInt]);
				MstSixPCArrearsObj.setFromDate(lDateArrFromDates[lInt]);
				MstSixPCArrearsObj.setToDate(lDateArrToDates[lInt]);
				lObjSixPCArrearsDAO.update(MstSixPCArrearsObj);
			
			}

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
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
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
	 * com.tcs.sgv.dcps.service.SixPCArrearsService#insertSixPCArrears(java.
	 * util.Map)
	 */
	public ResultObject insertSixPCArrears(Map inputMap) throws Exception {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SixPCArrearsDAO lObjSixPCArrearsDAO = null;
		Long lLngDcpsSixPCId = null;

		MstSixPCArrears lObjMstSixPCArrearsVO = null;
		
		try {
			
				setSessionInfo(inputMap);
		
				List<MstSixPCArrears> lListMstSixPCArrears = new ArrayList<MstSixPCArrears>();
				lObjMstSixPCArrearsVO = new MstSixPCArrears();
	
				lListMstSixPCArrears = (List<MstSixPCArrears>) inputMap.get("lListMstSixPCArrears");
				lObjSixPCArrearsDAO = new SixPCArrearsDAOImpl(
						MstSixPCArrears.class, serv.getSessionFactory());
	
				if (lListMstSixPCArrears != null) {
						
						for (int lIntCnt = 0; lIntCnt < lListMstSixPCArrears.size(); lIntCnt++) {
							
							lObjMstSixPCArrearsVO = new MstSixPCArrears();
							lObjMstSixPCArrearsVO = lListMstSixPCArrears
									.get(lIntCnt);
							lLngDcpsSixPCId = IFMSCommonServiceImpl.getNextSeqNum(
									"mst_dcps_sixpc", inputMap);
							lObjMstSixPCArrearsVO.setDcpsSixPCId(lLngDcpsSixPCId);
							lObjSixPCArrearsDAO.create(lObjMstSixPCArrearsVO);
						}
				}
			} 
		
		catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
}
