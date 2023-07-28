/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jan 31, 2011		Shivani Rana								
 *******************************************************************************
 */


package com.tcs.sgv.dcps.service;

//com.tcs.sgv.dcps.service.DCPSTreasuryServiceImpl2

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
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

import com.tcs.sgv.allowance.service.IdGenerator;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.GpfLnaDashboardDao;
import com.tcs.sgv.common.dao.GpfLnaDashboardDaoImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.ExcelParser;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.utils.fileupload.dao.CmnAttachmentMstDAOImpl;
import com.tcs.sgv.common.valueobject.CmnAttachmentMpg;
import com.tcs.sgv.common.valueobject.CmnAttachmentMst;
import com.tcs.sgv.common.valueobject.CmnAttdocMst;
import com.tcs.sgv.common.valueobject.CmnDistrictMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoProfileDAO;
import com.tcs.sgv.dcps.dao.DdoProfileDAOImpl;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.dao.TreasuryDAO;
import com.tcs.sgv.dcps.dao.TreasuryDAOImpl;
import com.tcs.sgv.dcps.valueobject.HstEmpDeputation;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstDcpsTreasurynetData;
import com.tcs.sgv.dcps.valueobject.MstDummyOffice;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.dcps.valueobject.TrnDcpsDeputationContribution;
import com.tcs.sgv.payroll.util.PayrollConstants;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Jan 31, 2011
 */
public class TreasuryServiceImpl2 extends ServiceImpl implements TreasuryService {

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
	/* Global Variable for Logger Class */

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

	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	private Long lLngLocId;

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
			lLngLocId = Long.valueOf(SessionHelper.getLocationCode(inputMap));
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
			
			
		} catch (Exception e) {

		}

	}

	/**
	 * Method to load the DDO Codes and DDO Names respective to the Treasury
	 * 
	 * @param inputMap
	 * @return ResultObject
	 */

/*	public ResultObject loadTOPhyFormRcvdList(Map inputMap) {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);

		String lStrPostId = SessionHelper.getPostId(inputMap).toString();
		String lStrUserType = null;
		TreasuryDAO dcpsTreasuryDao = new TreasuryDAOImpl(null, servLoc
				.getSessionFactory());

		try {

			List lListForms = dcpsTreasuryDao.getAllDDOListForPhyFormRcvd(
					lStrPostId, lStrUserType);
			inputMap.put("formList", lListForms);
		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSPhyFormRcvdList");
		return resObj;

	}

	*//**
	 * Method to load the employee forms according to the DDO code selected
	 * 
	 * @param inputMap
	 * @return ResultObject
	 *//*

	public ResultObject loadTOPhyFormRcvd(Map inputMap) throws Exception {

		ServiceLocator servLoc = (ServiceLocator) inputMap
				.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		TreasuryDAO dcpsTreasuryDao = new TreasuryDAOImpl(null, servLoc
				.getSessionFactory());

		String lStrDDOCode = StringUtility.getParameter("ddoCode", request);
		Long.parseLong(lStrDDOCode);

		try {

			List lListForms = dcpsTreasuryDao.getAllFormsForDDO(lStrDDOCode,
					gStrPostId);
			inputMap.put("formPhyList", lListForms);
		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSPhyFormRcvd");
		return resObj;

	}

	*//**
	 * Method to submit the physical form received
	 * 
	 * @param inputMap
	 * @return ResultObject
	 *//*

	public ResultObject submitPhyFormRcvdStatus(Map inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		RltPhyFormStatus lObjDcpsPhyFormStatus;
		Boolean lStrflag = false;

		String lStrNos = StringUtility.getParameter("empNos", request);
		String[] lArrdcpsPhyFormRcvdStatusIds = lStrNos.split("~");
		setSessionInfo(inputMap);

		try {

			for (int i = 0; i < lArrdcpsPhyFormRcvdStatusIds.length; i++) {

				String lStrdcpsPhyFormRcvdStatusId = lArrdcpsPhyFormRcvdStatusIds[i]
						.trim();
				Long lLngdcpsPhyFormRcvdStatusId = Long
						.parseLong(lStrdcpsPhyFormRcvdStatusId);

				// Read the DcpsPhyFormStatus VO for changing status
				TreasuryDAO dcpsTreasuryDao = new TreasuryDAOImpl(
						RltPhyFormStatus.class, serv.getSessionFactory());
				lObjDcpsPhyFormStatus = (RltPhyFormStatus) dcpsTreasuryDao
						.read(lLngdcpsPhyFormRcvdStatusId);

				// Set the value in Read VO
				lObjDcpsPhyFormStatus.setPhyFormRcvd(1L);
				lObjDcpsPhyFormStatus.setUpdatedUserId(gLngUserId);
				lObjDcpsPhyFormStatus.setUpdatedPostId(gLngPostId);
				lObjDcpsPhyFormStatus.setUpdatedDate(gDtCurrDt);

				lStrflag = true;

			}
		} catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			return objRes;
		}

		String lSBStatus = getResponseXMLDoc(lStrflag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();
		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	*//**
	 * 
	 * <H3>Description -</H3>
	 * 
	 * 
	 * @author Kapil Devani
	 * @param inputMap
	 * @return
	 * @throws Exception
	 *//*
	public ResultObject ApproveForm(Map inputMap) throws Exception {

		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);
		MstEmp lObjDcpsEmpMst;
		Boolean lStrflag = false;

		String lStrNos = StringUtility.getParameter("Emp_Id", request);
		String[] lArrEmpNo = lStrNos.split("~");
		setSessionInfo(inputMap);

		String lStrEmpId = null;
		Long lLngEmpId = 0L;

		try {

			for (int i = 0; i < lArrEmpNo.length; i++) {

				lStrEmpId = lArrEmpNo[i].trim();
				lLngEmpId = Long.parseLong(lStrEmpId);

				// Read the DcpsPhyFormStatus VO for changing status
				TreasuryDAO dcpsTreasuryDao = new TreasuryDAOImpl(MstEmp.class,
						serv.getSessionFactory());
				lObjDcpsEmpMst = (MstEmp) dcpsTreasuryDao.read(lLngEmpId);

				// Set the value in Read VO
				// String lStrDcpsId = generateDCPDId(inputMap, lLngEmpId);

				// lObjDcpsEmpMst.setDcpsId(lStrDcpsId);
				lObjDcpsEmpMst.setRegStatus(1L);
				lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
				lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
				lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
				lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);
				lStrflag = true;

			}
		} catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			return objRes;
		}

		String lSBStatus = getResponseXMLDoc(lStrflag, lLngEmpId).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();
		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	*//**
	 * 
	 * Function used to generate the DCPS ID by the treasury
	 * 
	 * 
	 * @author Kapil Devani
	 * @param inputMap
	 * @return
	 *//*

	*//**
	 * 
	 * <H3>Description -</H3>
	 * 
	 * 
	 * 
	 * @author Bhargav Trivedi
	 * @param objectArgs
	 * @return
	 *//*
	public ResultObject rejectRequest(Map objectArgs) {

		objectArgs.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");
		objectArgs.get("serviceLocator");
		setSessionInfo(objectArgs);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {

			String strPKValue = StringUtility.getParameter("Emp_Id", request)
					.toString().trim();

			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("FromPostId", gStrPostId);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", gObjRsrcBndle
					.getString("DCPS.RegistrationForm"));
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.RegistrationFormID")));

			for (int index = 0; index < strArrPKValue.length; index++) {
				objectArgs.put("Pkvalue", strArrPKValue[index]);
				WorkFlowDelegate.returnDoc(objectArgs);

				// Update the Registration form status to -1 suggesting it is
				// rejected

				NewRegDdoDAO dcpsNewRegistrationDao = new NewRegDdoDAOImpl(
						MstEmp.class, serv.getSessionFactory());
				Long lLongPKValue = Long.parseLong(strArrPKValue[index]);
				MstEmp lObjDcpsEmpMst = (MstEmp) dcpsNewRegistrationDao
						.read(lLongPKValue);

				// Set the value in Read VO
				lObjDcpsEmpMst.setRegStatus(-1L);
				lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
				lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
				lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
				lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);

			}

			objectArgs.put("ajaxKey", "Success");
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
	}*/

	/**
	 * Method to generate the Ajax response
	 * 
	 * @param boolean flag
	 * @return StringBuilder
	 */
	private StringBuilder getResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("  </FLAG>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	/*
	 * Method to generate the xml response for Ajax
	 */
	private StringBuilder getResponseXMLDoc(Boolean flag, long empID) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("  </Flag>");
		lStrBldXML.append("  <EMPID>");
		lStrBldXML.append(empID);
		lStrBldXML.append("  </EMPID>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDoc(Boolean flag, String empID) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("  </Flag>");
		lStrBldXML.append("  <EMPID>");
		lStrBldXML.append(empID);
		lStrBldXML.append("  </EMPID>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#loadEmpDeputation(java.util.Map)
	 */
	public ResultObject loadEmpDeputation(Map inputMap) {
		gLogger.info("in loadEmpDeputation ################ method");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		TreasuryDAO lObjTreasuryDAO = null;
		List lLstOffice = null;
		List lLstReasons = null;
		List lLstEmpDeptn = null;
		String lStrQueryString = null;
		String loadDCPS=null;
		String lStrSevarthId = null;
		String lStrRequestForSearch = null;
		String lStrEmpName = null;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String ddoCode=""; //changed #here 
		boolean isBelongToTreasury=true; //changed #here
		
		try {
			setSessionInfo(inputMap);
			
			loadDCPS = StringUtility.getParameter("loadDCPS", request).trim();
			gLogger.info("#################### loadDCPS directly getting by page url : "+loadDCPS);
			
			lStrQueryString = StringUtility.getParameter("queryString", request).trim();
			gLogger.info("#################### collected queryString parameter");

			
			String lStrWithOrWithoutEmplrContri = StringUtility.getParameter("Use", request).trim() ;
			String allFlag = StringUtility.getParameter("allFalg", request).trim() ; // changed #here
			inputMap.put("WithOrWithoutEmplrContri", lStrWithOrWithoutEmplrContri);

			lObjTreasuryDAO = new TreasuryDAOImpl(TreasuryServiceImpl2.class,serv.getSessionFactory());

			lLstReasons = IFMSCommonServiceImpl.getLookupValues("Reason For Deslection", gLngLangId, inputMap);

			lLstOffice = lObjTreasuryDAO.getDummyOffices(gStrLocationCode,lStrWithOrWithoutEmplrContri);
			
			lStrRequestForSearch = StringUtility.getParameter("requestForSearch", request).trim();

			lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
		
			lStrEmpName = StringUtility.getParameter("txtEmployeeName",request).trim();
			
			
			gLogger.info("#################### collected all parameter");

//Added by ashish to get employee list of acc main by IAS,IPS and IFS
			
			
			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			
			Long lRoleOfLngUsr = lObjMatchEntryDAO.getRoleOfUserFrmPostId(gLngPostId);
			String userType = null;
			Boolean flag=true;
			List lLstYears=null;
			
			gLogger.info("#################### now create object");

			if(lRoleOfLngUsr != null && !lRoleOfLngUsr.equals("") && lRoleOfLngUsr == 700004l){
				userType = "srka";
				lLstYears = lObjDcpsCommonDAO.getFinyears();
			}
			if(lRoleOfLngUsr != null && !lRoleOfLngUsr.equals("") && lRoleOfLngUsr == 700003l){
				userType = "to";
				lLstYears = lObjMatchEntryDAO.getFinyears();
			}
			
			System.out.println("userType ***"+userType);
			
			
			/********************** changed start #here */
			if(allFlag!=null && !"".equalsIgnoreCase(allFlag) && "All".equalsIgnoreCase(allFlag))
			{
				gLogger.info("#################### in all flag");

			
					gLogger.info("#################### user type is srka");
					
					System.out.println("flag is***"+flag);
					
					gLogger.info("#################### the flag");
				lLstEmpDeptn = lObjTreasuryDAO.getEmpDeptnListAll(lStrQueryString,Long.valueOf(gStrLocationCode),flag,loadDCPS);
				
				
			}
			else if((lStrEmpName!=null || lStrSevarthId!=null) && (!"".equalsIgnoreCase(lStrEmpName)) || !"".equalsIgnoreCase(lStrSevarthId) )
				{
				
				gLogger.info("#################### in else if ");

				lLstEmpDeptn = lObjTreasuryDAO.getEmpDeptnList(lStrQueryString,lStrSevarthId,lStrEmpName,Long.valueOf(gStrLocationCode),flag,loadDCPS);
					
				}
			
			if(lStrQueryString!=null && lStrQueryString.equals("Attach") && lLstEmpDeptn!=null && lLstEmpDeptn.size()>0)
			{
				for(int j=0;j<lLstEmpDeptn.size();j++)
				{
					Object[] obj=(Object[]) lLstEmpDeptn.get(j);
					if(obj!=null && obj[4]!=null && !"0".equalsIgnoreCase(obj[4].toString().trim()))
					{
						if(obj!=null && (obj[9]==null || "".equalsIgnoreCase(obj[9].toString().trim())))
						{
							lLstEmpDeptn.remove(j);
						}
						
					}
				}
				
			}
			gLogger.info("#################### after if ");
				Date lDtcurDate = SessionHelper.getCurDate();
				//inputMap.put("lDtCurDate", lObjDateFormat.format(lDtcurDate));
				inputMap.put("lDtCurDate", lDtcurDate);

				inputMap.put("EMPDEPUTNLIST", lLstEmpDeptn);
				if (lLstEmpDeptn!=null && lLstEmpDeptn.size()==0)
				{
					inputMap.put("sizeOfList", 1);
				}
				else if  (lLstEmpDeptn!=null && lLstEmpDeptn.size() > 0)
				{
					inputMap.put("sizeOfList", lLstEmpDeptn.size());
				}
				else
				{
					inputMap.put("sizeOfList", 0);
				}
				gLogger.info("############loadDCPS"+loadDCPS);
				
				inputMap.put("loadDCPS", loadDCPS);
				inputMap.put("REASONLIST", lLstReasons);
				inputMap.put("OFFICELIST", lLstOffice);
				inputMap.put("QueryString", lStrQueryString);
			

				objRes.setResultValue(inputMap);
				objRes.setViewName("DCPSEmpDeputationInfo");
		}

		catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	
	/********************* changed end #here */
	public ResultObject showEmpDeputationList(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		TreasuryDAO lObjTreasuryDAO = null;
		List lLstOffice = null;
		List lLstReasons = null;
		List lLstEmpDeptn = null;
		String lStrQueryString = null;
		String lStrSevarthId = null;
		String lStrRequestForSearch = null;
		String lStrEmpName = null;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String ddoCode="";
       	String isBelongToTreasury="true";
		String isDdoCodeNull="true";
		Boolean flag=false;
		Boolean returnFlag=false;
		String hodFlag="false";
		String deptname="Dept";
		String officenull="false";
		String offName="";
		String officeName="office";
		String loadDCPS="";
		try {
			setSessionInfo(inputMap);
			lStrQueryString = StringUtility.getParameter("queryString", request).trim();
			loadDCPS = StringUtility.getParameter("loadDCPS", request).trim();
			gLogger.info("############loadDCPS"+loadDCPS);
			
			String lStrWithOrWithoutEmplrContri = StringUtility.getParameter("Use", request).trim() ;
			inputMap.put("WithOrWithoutEmplrContri", lStrWithOrWithoutEmplrContri);

			lObjTreasuryDAO = new TreasuryDAOImpl(TreasuryServiceImpl2.class,serv.getSessionFactory());

			lLstReasons = IFMSCommonServiceImpl.getLookupValues("Reason For Deslection", gLngLangId, inputMap);

			lLstOffice = lObjTreasuryDAO.getDummyOffices(gStrLocationCode,lStrWithOrWithoutEmplrContri);
			
			lStrRequestForSearch = StringUtility.getParameter("requestForSearch", request).trim();

			lStrSevarthId = StringUtility.getParameter("txtSevaarthId",request).trim();
		
			lStrEmpName = StringUtility.getParameter("txtEmployeeName",request).trim();
			
		
			
			
			//Added by ashish to get employee list of acc main by IAS,IPS and IFS
			MatchContriEntryDAO lObjMatchEntryDAO = new MatchContriEntryDAOImpl(
					null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
		
			if((lStrEmpName!=null || lStrSevarthId!=null) && (!"".equalsIgnoreCase(lStrEmpName)) || !"".equalsIgnoreCase(lStrSevarthId) )
			{
				ddoCode=lObjTreasuryDAO.getEmpDDOCode(lStrSevarthId, lStrEmpName, Long.valueOf(gStrLocationCode), flag);
				if(ddoCode!=null && !"".equalsIgnoreCase(ddoCode))
				{
					//isDdoCodeNull="false";
					if(!gStrLocationCode.substring(0, 2).equalsIgnoreCase(ddoCode.substring(0, 2)))
					{
						isBelongToTreasury="false";
						
					}
				
				}
				
				else{
					ddoCode = "false";
					
				}
				
				offName=lObjTreasuryDAO.getOfficeName(lStrSevarthId, lStrEmpName);
				if(offName!=null&&offName!="")
				{
					officeName=offName;
				}
				
				gLogger.info("############officeName"+officeName);
				returnFlag=lObjTreasuryDAO.chkIfDdoOfHodDefined(lStrSevarthId, lStrEmpName);
				if(returnFlag)
				{
					hodFlag="true";
				}
				else
				{
					deptname=lObjTreasuryDAO.getEmpDept(lStrSevarthId, lStrEmpName);
				}
				
				if(loadDCPS!="" && loadDCPS!=null){
					loadDCPS="Yes";
				}
				else {
					loadDCPS="No";
				}
			}
			gLogger.info("############deptname"+deptname);
			String lSBStatus = getResponseXMLDoc(isBelongToTreasury,hodFlag,deptname,officeName,ddoCode,loadDCPS).toString();
			gLogger.info("############"+lSBStatus);
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();


			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
			/********************* changed end #here */
		/*	objRes.setResultValue(inputMap);
			objRes.setViewName("DCPSEmpDeputationInfo");*/

		}

		catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

	/********************* changed start #here 
	 * @param ddocode */
	private StringBuilder getResponseXMLDoc(String lStrSevaarth,String hodFlag,String deptName, String officeName,String ddoname,String dcpsflag)
	{
		StringBuilder lStrBldXML = new StringBuilder();


		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<SEVAARTH>");
		lStrBldXML.append(lStrSevaarth);
		lStrBldXML.append("</SEVAARTH>");
		lStrBldXML.append("<HOD>");
		lStrBldXML.append(hodFlag);
		lStrBldXML.append("</HOD>");
		lStrBldXML.append("<DEPT>");
		lStrBldXML.append(deptName);
		lStrBldXML.append("</DEPT>");
		lStrBldXML.append("<OFFICENAME>");
		lStrBldXML.append(officeName);
		lStrBldXML.append("</OFFICENAME>");
		lStrBldXML.append("<OFFICE>");
		lStrBldXML.append(ddoname);
		lStrBldXML.append("</OFFICE>");
		lStrBldXML.append("<DCPSFLAG>");
		lStrBldXML.append(dcpsflag);
		lStrBldXML.append("</DCPSFLAG>");
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
		}

	/********************* changed end #here */
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#dcpsEmpDeputation(java.util.Map)
	 */
	public ResultObject dcpsEmpDeputation(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String lStrSevarthId = null;
		TreasuryDAO lObjTreasuryDAO = null;
		String lStrQueryString = null;
		String lStrRequestForSearch = null;
		StringBuilder lStrBldXML = null;
		String lStrEmpName = null;
		List lLstEmpDeptun = null;
		List<CmnLookupMst> lLstReasons = null;
		List lLstOffice = null;
		Boolean lBlSucceccFlag = null;
		MstEmp lObjMstEmp = null;
		HstEmpDeputation lObjHstEmpDeputation = null;
		Long lLongPKValue = null;
		String lStrUse = null;

		try {
			setSessionInfo(inputMap);
			lStrQueryString = StringUtility.getParameter("hidString", request).trim();
			lStrRequestForSearch = StringUtility.getParameter("requestForSearch", request).trim();
			
			lStrUse = StringUtility.getParameter("Use", request).trim();
			
			Integer lIntTotalEmps = Integer.parseInt(StringUtility.getParameter("totalEmployeesSelected", request).trim());
			
			String lStrRemarks = StringUtility.getParameter("remarks", request).trim();
			//String[] lStrArrRemarks = null;
			
			/*String[] lStrArrRemarks = new String[lIntTotalEmps];
			
			if (lStrQueryString.equalsIgnoreCase("Attach")) {
				lStrArrRemarks = lStrRemarks.split("~");
			}*/
			
			/*
			String lStrReasons = StringUtility.getParameter("cmbReasons",request).trim();
			//String[] lStrArrReason = null;
			
			String[] lStrArrReason = new String[lIntTotalEmps];
			
			if (lStrQueryString.equalsIgnoreCase("Attach")) {
				lStrArrReason = lStrReasons.split("~");
			}
			*/
			
			String lStrRemarksDetach = StringUtility.getParameter("remarks", request).trim();
			//String[] lStrArrRemarksDetach  = null;
			String[] lStrArrRemarksDetach = new String[lIntTotalEmps];
			
			if (lStrQueryString.equalsIgnoreCase("Detach")) {
				lStrArrRemarksDetach = lStrRemarksDetach.split("~");
			}

			String lStrReasonsDetach = StringUtility.getParameter("cmbReasons",request).trim();
			//String[] lStrArrReasonDetach = null;
			String[] lStrArrReasonDetach = new String[lIntTotalEmps];
			
			if (lStrQueryString.equalsIgnoreCase("Detach")) {
				lStrArrReasonDetach = lStrReasonsDetach.split("~");
			}

				if (lStrQueryString.equals("Attach")) {

					lObjTreasuryDAO = new TreasuryDAOImpl(null, serv.getSessionFactory());
					//NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());

					lObjMstEmp = null;
					Long lLngDeputationEmpId = null;
					
					String deputnIds = StringUtility.getParameter("deputnIds",request).trim();
					String[] strArrPKValue = deputnIds.split("~");

					List<HstEmpDeputation> lListHstEmpDeputationVO = (List<HstEmpDeputation>) inputMap.get("lLstHstEmpDeputationVO");

					for (Integer lInt = 0; lInt < strArrPKValue.length; lInt++) {
						
						lLongPKValue = Long.valueOf(strArrPKValue[lInt]);
						
						// PK = 0 means save, Else update.

						if(lLongPKValue == 0l)	
						{
							lLngDeputationEmpId = IFMSCommonServiceImpl.getNextSeqNum("hst_dcps_emp_deputation",inputMap);
							lListHstEmpDeputationVO.get(lInt).setHstdcpsEmpDeptnId(lLngDeputationEmpId);
						/*	if(lStrArrRemarks[lInt] != null)
							{
								if(!"".equals(lStrArrRemarks[lInt].trim()) && !"NULL".equals(lStrArrRemarks[lInt].trim()))
								{
									lListHstEmpDeputationVO.get(lInt).setRemarks(lStrArrRemarks[lInt].trim());
								}
							}*/
							//lListHstEmpDeputationVO.get(lInt).setReason(lStrArrReason[lInt].trim());
							lObjTreasuryDAO.create(lListHstEmpDeputationVO.get(lInt));
						}
						else
						{
							lLngDeputationEmpId = lLongPKValue;
							lListHstEmpDeputationVO.get(lInt).setHstdcpsEmpDeptnId(lLngDeputationEmpId);
						/*	if(lStrArrRemarks[lInt] != null)
							{
								if(!"".equals(lStrArrRemarks[lInt].trim()) && !"NULL".equals(lStrArrRemarks[lInt].trim()))
								{
										lListHstEmpDeputationVO.get(lInt).setRemarks(lStrArrRemarks[lInt].trim());
								}
							}*/
							//lListHstEmpDeputationVO.get(lInt).setReason(lStrArrReason[lInt].trim());
							lObjTreasuryDAO.update(lListHstEmpDeputationVO.get(lInt));
						}
						
						/*
						lObjMstEmp = (MstEmp) lObjNewRegDdoDAO.read(lListHstEmpDeputationVO.get(lInt).getDcpsEmpId().getDcpsEmpId());
						//lObjMstEmp.setBillGroupId(null);
						//lObjMstEmp.setDdoCode(null);
						lObjMstEmp.setCurrOff(lListHstEmpDeputationVO.get(lInt).getOfficeCode());
						lObjMstEmp.setEmpOnDeptn(1);
						lObjNewRegDdoDAO.update(lObjMstEmp);
						*/
					}
				}

				if (lStrQueryString.equals("Detach")) {

					lObjTreasuryDAO = new TreasuryDAOImpl(HstEmpDeputation.class, serv.getSessionFactory());
					String deputnIds = StringUtility.getParameter("deputnIds",request).trim();
					String detachDates = StringUtility.getParameter("detachDates", request).trim();
					String empIds = StringUtility.getParameter("Emp_Id",request).trim();
					String[] strArrPKValue = deputnIds.split("~");
					String[] strArrDetachDates = detachDates.split("~");
					String[] strArrEmpIds = empIds.split("~");
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					
					//NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());
					
					for (Integer index = 0; index < strArrPKValue.length; index++) {

						lLongPKValue = Long.parseLong(strArrPKValue[index]);
						lObjHstEmpDeputation = (HstEmpDeputation) lObjTreasuryDAO.read(lLongPKValue);
						//lObjHstEmpDeputation.setDetachDate(sdf.parse(strArrDetachDates[index]));
						lObjHstEmpDeputation.setUpdatedUserId(gLngUserId);
						lObjHstEmpDeputation.setUpdatedPostId(gLngPostId);
						lObjHstEmpDeputation.setUpdatedDate(gDtCurDate);
						
						if(lStrArrRemarksDetach[index] != null)
						{
							if(!"".equals(lStrArrRemarksDetach[index].trim()) && !"NULL".equals(lStrArrRemarksDetach[index].trim()))
							{
								lObjHstEmpDeputation.setRemarksDetach(lStrArrRemarksDetach[index].trim());
							}
						}
						
						lObjHstEmpDeputation.setReasonDetach(lStrArrReasonDetach[index].trim());
						
						lObjTreasuryDAO.update(lObjHstEmpDeputation);
						
						/*
						lObjMstEmp = (MstEmp) lObjNewRegDdoDAO.read(Long.parseLong(strArrEmpIds[index]));
						lObjMstEmp.setCurrOff(null);
						lObjMstEmp.setEmpOnDeptn(0);
						lObjNewRegDdoDAO.update(lObjMstEmp);
						*/
					}

				}

				lBlSucceccFlag = true;

				lStrBldXML = getResponseXMLDoc(lStrQueryString, lBlSucceccFlag,lStrUse);
				gLogger.info(" lStrBldXML :: " + lStrBldXML);
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lStrBldXML.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				objRes.setViewName("ajaxData");
				objRes.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;

	}

	private StringBuilder getResponseXMLDoc(String lStrQueryString,
			Boolean lBlSuccessFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC1>");
		lStrBldXML.append("  <QUERYSTRING>");
		lStrBldXML.append(lStrQueryString);
		lStrBldXML.append("  </QUERYSTRING>");
		lStrBldXML.append("  <SUCCESSFLAG>");
		lStrBldXML.append(lBlSuccessFlag);
		lStrBldXML.append("  </SUCCESSFLAG>");

		lStrBldXML.append("</XMLDOC1>");

		return lStrBldXML;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrQueryString,
			Boolean lBlSuccessFlag,String lStrUse) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC1>");
		lStrBldXML.append("  <QUERYSTRING>");
		lStrBldXML.append(lStrQueryString);
		lStrBldXML.append("  </QUERYSTRING>");
		lStrBldXML.append("  <SUCCESSFLAG>");
		lStrBldXML.append(lBlSuccessFlag);
		lStrBldXML.append("  </SUCCESSFLAG>");
		lStrBldXML.append("  <lSTRUSE>");
		lStrBldXML.append(lStrUse);
		lStrBldXML.append("  </lSTRUSE>");

		lStrBldXML.append("</XMLDOC1>");

		return lStrBldXML;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#loadSixthPCArrearsYearlyTO(java
	 * .util.Map)
	 */
	/*public ResultObject loadSixthPCArrearsYearlyTO(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List lListEmp = null;
		List lLstUpperUsers = null;
		String lStrStatusFlag = null;
		String lStrUserType = null;
		TreasuryDAO lObjTreasuryDAO = null;
		String lStrDdoCode = null;
		try {

			setSessionInfo(inputMap);
			lStrStatusFlag = StringUtility.getParameter("StatusFlag", request);
			lStrUserType = StringUtility.getParameter("UserType", request);
			lObjTreasuryDAO = new TreasuryDAOImpl(MstEmp.class, serv
					.getSessionFactory());
			List<ComboValuesVO> lLstAllForms = lObjTreasuryDAO
					.getAllDDOListForPhyFormRcvd(gStrPostId, lStrUserType);
			Iterator it = lLstAllForms.iterator();
			ComboValuesVO tuple = new ComboValuesVO();
			int lIntLoopJ = 0;
			while (it.hasNext()) {
				tuple = (ComboValuesVO) it.next();
				lStrDdoCode = tuple.getId();
				lIntLoopJ++;
			}

			List lListYears = lObjTreasuryDAO.getYears();
			lLstUpperUsers = getHierarchyUsers(inputMap);

			if (StringUtility.getParameter("yearId", request) != null
					&& !(StringUtility.getParameter("yearId", request)
							.equalsIgnoreCase(""))) {
				Long yearId = Long.valueOf(StringUtility.getParameter("yearId",
						request));
				lListEmp = lObjTreasuryDAO.getEmpListForSixPCArrearsYearlyTO(
						lStrDdoCode, yearId, gStrPostId);
			}
			inputMap.put("lLstAllForms", lLstAllForms);
			inputMap.put("DDOCODE", lStrDdoCode);
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

	
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#loadsixthPCYearlyInstallmentTO
	 * (java.util.Map)
	 
	public ResultObject loadsixthPCYearlyInstallmentTO(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		List lListEmp = null;
		List lLstUpperUsers = null;
		String lStrStatusFlag = null;
		String lStrUserType = null;
		TreasuryDAO lObjTreasuryDAO = null;
		String lStrDdoCode = null;
		try {

			setSessionInfo(inputMap);
			lObjTreasuryDAO = new TreasuryDAOImpl(MstEmp.class, serv
					.getSessionFactory());
			lStrStatusFlag = StringUtility.getParameter("StatusFlag", request);
			lStrUserType = StringUtility.getParameter("UserType", request);
			List<ComboValuesVO> lLstAllForms = lObjTreasuryDAO
					.getAllDDOListForPhyFormRcvd(gStrPostId, lStrUserType);
			Iterator it = lLstAllForms.iterator();
			ComboValuesVO tuple = new ComboValuesVO();
			int lIntLoopJ = 0;
			while (it.hasNext()) {
				tuple = (ComboValuesVO) it.next();
				lStrDdoCode = tuple.getId();
				lIntLoopJ++;
			}

			List lListYears = lObjTreasuryDAO.getYears();
			lLstUpperUsers = getHierarchyUsers(inputMap);

			if (StringUtility.getParameter("yearId", request) != null
					&& !(StringUtility.getParameter("yearId", request)
							.equalsIgnoreCase(""))) {
				Long yearId = Long.valueOf(StringUtility.getParameter("yearId",
						request));
				lListEmp = lObjTreasuryDAO.getEmpListForSixPCArrearsYearlyTO(
						lStrDdoCode, yearId, gStrPostId);
			}
			inputMap.put("lLstAllForms", lLstAllForms);
			inputMap.put("DDOCODE", lStrDdoCode);
			inputMap.put("StatusFlag", lStrStatusFlag);
			inputMap.put("UserType", lStrUserType);
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

	
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#approveSixthPCArrearsYearlyByTO
	 * (java.util.Map)
	 
	public ResultObject approveSixthPCArrearsYearlyByTO(Map objectArgs) {

		HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String sixthPCId = null;
		ArrearsDAO lObjArrearsDAO = null;
		String subjectName = null;
		try {

			setSessionInfo(objectArgs);
			subjectName = gObjRsrcBndle.getString("DCPS.SixthPCArrears");
			String strPKValue = StringUtility.getParameter("SixthPC_Id",
					request).toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("FromPostId", gStrPostId);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.SixthPCArrearsID")));

			lObjArrearsDAO = new ArrearsDAOImpl(RltDcpsSixPCYearly.class, serv
					.getSessionFactory());
			for (int index = 0; index < strArrPKValue.length; index++) {
				sixthPCId = strArrPKValue[index];
				objectArgs.put("Pkvalue", sixthPCId);
				Long lLongPKValue = Long.parseLong(strArrPKValue[index]);
				RltDcpsSixPCYearly lObjRltDcpsSixPCYearly = (RltDcpsSixPCYearly) lObjArrearsDAO
						.read(lLongPKValue);
				lObjRltDcpsSixPCYearly.setStatusFlag('A');
				lObjRltDcpsSixPCYearly.setUpdatedUserId(gLngUserId);
				lObjRltDcpsSixPCYearly.setUpdatedPostId(gLngPostId);
				lObjRltDcpsSixPCYearly.setUpdatedDate(gDtCurDate);
				// lObjRltDcpsSixPCYearly.set

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

	
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#rejectSixthPCArrearsYearlyByTO
	 * (java.util.Map)
	 
	public ResultObject rejectSixthPCArrearsYearlyByTO(Map objectArgs) {

		HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		String sixthPCId = null;
		ArrearsDAO lObjArrearsDAO = null;
		String subjectName = null;
		try {

			setSessionInfo(objectArgs);
			subjectName = gObjRsrcBndle.getString("DCPS.SixthPCArrears");
			String strPKValue = StringUtility.getParameter("SixthPC_Id",
					request).toString().trim();
			String[] strArrPKValue = strPKValue.split("~");

			objectArgs.put("FromPostId", gStrPostId);
			objectArgs.put("SendNotification", "This is not valid");
			objectArgs.put("jobTitle", subjectName);
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle
					.getString("DCPS.SixthPCArrearsID")));

			lObjArrearsDAO = new ArrearsDAOImpl(RltDcpsSixPCYearly.class, serv
					.getSessionFactory());
			for (int index = 0; index < strArrPKValue.length; index++) {
				sixthPCId = strArrPKValue[index];
				objectArgs.put("Pkvalue", sixthPCId);
				// WorkFlowDelegate.create(objectArgs);
				WorkFlowDelegate.returnDoc(objectArgs);
				Long lLongPKValue = Long.parseLong(strArrPKValue[index]);
				RltDcpsSixPCYearly lObjRltDcpsSixPCYearly = (RltDcpsSixPCYearly) lObjArrearsDAO
						.read(lLongPKValue);
				lObjRltDcpsSixPCYearly.setStatusFlag('R');
				lObjRltDcpsSixPCYearly.setUpdatedUserId(gLngUserId);
				lObjRltDcpsSixPCYearly.setUpdatedPostId(gLngPostId);
				lObjRltDcpsSixPCYearly.setUpdatedDate(gDtCurDate);
				// lObjRltDcpsSixPCYearly.set

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
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#loadDummyOffice(java.util.Map)
	 */
	public ResultObject loadDummyOffice(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TreasuryDAO lObjTreasuryDAO = null;
		List lLstDummyOfficeDtls = null;
		
		try {
			setSessionInfo(inputMap);

			lObjTreasuryDAO = new TreasuryDAOImpl(TreasuryServiceImpl2.class,serv.getSessionFactory());
			
			lLstDummyOfficeDtls = lObjTreasuryDAO.getDummyOfficesList(gStrLocationCode);
			
			inputMap.put("DummyOfficeList", lLstDummyOfficeDtls);
			
			String lStrWithOrWithoutEmplrContri = StringUtility.getParameter("Use", request).trim() ;
			inputMap.put("WithOrWithoutEmplrContri", lStrWithOrWithoutEmplrContri);

			objRes.setResultValue(inputMap);
			objRes.setViewName("DCPSDummyOffice");

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Exception occured in loadDummyOffice exception is "
					+ e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;
	}

	public ResultObject loadDummyOfficeEntry(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TreasuryDAO lObjTreasuryDAO = null;
		List listAdminDept = null;
		List lLstDistricts = null;
		MstDummyOffice lObjMstDummyOffice = null;
		Long lLongOfficeId = null;
		String lStrOfficeId = null;
		
		Long lLongDistrict = null;
		List lLstTaluka = null;
		List lLstTown = null;
		
		try {
			setSessionInfo(inputMap);

			lObjTreasuryDAO = new TreasuryDAOImpl(TreasuryServiceImpl2.class,
					serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			DdoProfileDAO ddoProfileDAO = new DdoProfileDAOImpl(CmnDistrictMst.class, serv.getSessionFactory());

			listAdminDept = lObjDcpsCommonDAO.getAllDepartment(Long
					.parseLong(gObjRsrcBndle.getString("DCPS.DEPARTMENTID")),
					gLngLangId);
			
			String lStrWithOrWithoutEmplrContri = StringUtility.getParameter("Use", request).trim() ;
			inputMap.put("WithOrWithoutEmplrContri", lStrWithOrWithoutEmplrContri);

			if (!StringUtility.getParameter("dummyOfficeId", request).equalsIgnoreCase("")	&& StringUtility.getParameter("dummyOfficeId", request) != null) {

				lStrOfficeId = StringUtility.getParameter("dummyOfficeId",request).trim();
				lObjMstDummyOffice = lObjTreasuryDAO.getDummyOfficeInfo(lStrOfficeId,gStrLocationCode);
				lLongDistrict = lObjMstDummyOffice.getDistrict();
				lLstTaluka = ddoProfileDAO.getTaluka(lLongDistrict);
				lLstTown = lObjTreasuryDAO.getTowns(lLongDistrict);
				
			} else {
				//lLongOfficeId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_dummy_office", inputMap);
				//Above code commented to get next sequence Id for a new office
				
				
				Long count = lObjTreasuryDAO.getCountofChallanOfficesForGivenTreasury(gStrLocationCode);
				count = count + 1;
				String lStrCountOfOffice = String.valueOf(count);
				
				if (lStrCountOfOffice.length() == 1) {
					lStrOfficeId = "X0000" + lStrCountOfOffice;
				} else if(lStrCountOfOffice.length() == 2) {
					lStrOfficeId = "X000" + lStrCountOfOffice;
				} else if(lStrCountOfOffice.length() == 3) {
						lStrOfficeId = "X00" + lStrCountOfOffice;
				} else if(lStrCountOfOffice.length() == 4) {
					 lStrOfficeId = "X0" + lStrCountOfOffice;
				} else if(lStrCountOfOffice.length() == 5) {
					 lStrOfficeId = "X" + lStrCountOfOffice;
				}else{
					throw new Exception();
				}
				
				//lStrOfficeId = lLongOfficeId.toString();
			}

			lLstDistricts = lObjDcpsCommonDAO.getDistricts(15L);

			// List lLstTalukas = lObjDcpsCommonDAO.getTaluka(lStrCurrDst);

			inputMap.put("lObjMstDummyOffice", lObjMstDummyOffice);
			inputMap.put("OfficeId", lStrOfficeId);
			inputMap.put("listAdminDept", listAdminDept);
			inputMap.put("lLstDistricts", lLstDistricts);
			inputMap.put("lLstTaluka", lLstTaluka);
			inputMap.put("lLstTown", lLstTown);

			objRes.setResultValue(inputMap);
			objRes.setViewName("DCPSDummyOfficeEntry");

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Exception occured in loadDummyOfficeEntry exception is "+ e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#insertDummyOffice(java.util.Map)
	 */
	public ResultObject insertDummyOffice(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TreasuryDAO lObjTreasuryDAO = null;
		MstDummyOffice lObjMstDummyOfficeVO = new MstDummyOffice();
		String lStrDummyOfficeId = null;
		String lStrWithOrWithoutEmplrContri = "";

		try {
			setSessionInfo(inputMap);
			String strTransMode = (String) inputMap.get("Mode");
			if (strTransMode.equalsIgnoreCase("Add")) {
				if (lObjMstDummyOfficeVO != null) {
					lObjMstDummyOfficeVO = (MstDummyOffice) inputMap
							.get("lObjMstDummyOfficeVO");

					lObjTreasuryDAO = new TreasuryDAOImpl(MstDummyOffice.class,
							serv.getSessionFactory());
					Integer saveOrUpdateFlag = Integer.parseInt(StringUtility
							.getParameter("saveOrUpdateFlag", request));
					
					lStrWithOrWithoutEmplrContri = StringUtility.getParameter("Use", request).trim();
					if(lStrWithOrWithoutEmplrContri.equals("WithoutEmplrContri"))
					{
						lObjMstDummyOfficeVO.setIsWithoutEmplrContri('Y');
					}
					
					if (saveOrUpdateFlag == 1) {
						lObjTreasuryDAO.create(lObjMstDummyOfficeVO);
					}
					if (saveOrUpdateFlag == 2) {
						//lObjTreasuryDAO.update(lObjMstDummyOfficeVO);
						// Above update cannot be done as PK is removed from the table. So updates are done as below from the VO.
						lObjTreasuryDAO.updateDummyOfficeDetails(lObjMstDummyOfficeVO, inputMap);
					}
				}

			}

			//String lSBStatus = getResponseXMLDoc(true,lObjMstDummyOfficeVO.getDummyOfficeId()).toString();
			String lSBStatus = getResponseXMLDoc(true,lStrWithOrWithoutEmplrContri).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			return objRes;
		}
		return objRes;
	}

	public ResultObject loadChallanForDummy(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TreasuryDAO lObjTreasuryDAO = null;
		DcpsCommonDAO lObjDcpsCommonDAO = null;
		List lLstOffice = null;
		String lStrDummyOfficeId = null;
		List lListEmployeesFromDummyOffice = null;
		Long monthId = null;
		Long yearId = null;
		String lStrSubmittedOrNot = "NO";
		String pc=null;
		
		Date lDtFirstDate = null;
		Date lDtLastDate = null;
		List<CmnLookupMst> listPayCommission = new ArrayList<CmnLookupMst>();
		List listTypeOfPayment = null;
		String lStrWithOrWithoutEmplrContri="";
		String isNPS="NO";

		try {
			setSessionInfo(inputMap);

			lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			lObjTreasuryDAO = new TreasuryDAOImpl(TreasuryServiceImpl2.class,serv.getSessionFactory());
			
			//String lStrUserType = StringUtility.getParameter("User", request).trim();

			String lStrUseType = StringUtility.getParameter("Use", request).trim();
			
			 GpfLnaDashboardDao lObjGpfLnaDashboardDao = new GpfLnaDashboardDaoImpl(null, this.serv.getSessionFactory());
			 String lStrUserType = lObjGpfLnaDashboardDao.getUserType(this.gLngUserId);
		      this.gLogger.info("lStrUserType" + lStrUserType);
		      
		      inputMap.put("lStrUserType..............", lStrUserType);
		      
			System.out.println("UserType -------------"+lStrUserType + "UseType -------------"+lStrUseType );
			
			
			System.out.println(gStrPostId+"locations -------------"+gLclLocale + "locations -------------"+gStrLocale +"locations -------------"+gLngLangId +"locations -------------"+gLngPostId +"locations -------------"+gStrPostId +"locations -------------"+gLngUserId +"locations -------------"+gStrUserId +"locations -------------"+gStrLocationCode );
			
			
			System.out.println("Location Code...................."+gStrLocationCode);
			
			
			
			if(StringUtility.getParameter("Use", request)!=null)
			{
				lStrWithOrWithoutEmplrContri = StringUtility.getParameter("Use", request).trim() ;
				System.out.println("Use...................."+lStrWithOrWithoutEmplrContri);
			}
			
			if(StringUtility.getParameter("isNPS", request)!=null)
			{
				isNPS = StringUtility.getParameter("isNPS", request).trim() ;
				System.out.println("isNPS...................."+isNPS);
			}
			inputMap.put("WithOrWithoutEmplrContri", lStrWithOrWithoutEmplrContri);


			if (lStrUserType.equals("DDO Assistant")) {

				lLstOffice = lObjTreasuryDAO.getDummyOfficesDdoAst(gStrLocationCode,lStrWithOrWithoutEmplrContri,gStrPostId);
			} else if (lStrUserType.equals("DDO")) {
				lLstOffice = lObjTreasuryDAO.getDummyOfficesDdo(gStrLocationCode,lStrWithOrWithoutEmplrContri,gStrPostId);

			} else {
				lLstOffice = lObjTreasuryDAO.getDummyOffices(gStrLocationCode,lStrWithOrWithoutEmplrContri);
				System.out.println("lLstOffice...................."+lLstOffice);
			}

			 if (!StringUtility.getParameter("dummyOfficeId", request).trim().equals("") && StringUtility.getParameter("dummyOfficeId", request) != null
					&&
					!StringUtility.getParameter("payMonth", request).trim().equals("") && StringUtility.getParameter("payMonth", request) != null
					&&
					!StringUtility.getParameter("payYear", request).trim().equals("") && StringUtility.getParameter("payYear", request) != null) {
				
				lStrDummyOfficeId = StringUtility.getParameter("dummyOfficeId",request).trim();

				monthId = Long.valueOf(StringUtility.getParameter("payMonth",request).trim());
				yearId = Long.valueOf(StringUtility.getParameter("payYear",request).trim());
				
				lStrSubmittedOrNot = StringUtility.getParameter("submittedOrNot",request).trim();
				
				
				System.out.println("here....................");
				//$t 2019
				if(isNPS!=null && !"".equalsIgnoreCase(isNPS) && "yes".equalsIgnoreCase(isNPS))
				{
					if (lStrUserType.equals("DDO Assistant")) {
						lListEmployeesFromDummyOffice = lObjTreasuryDAO.getEmployeesFromDummyOfficeAstNPS(lStrDummyOfficeId,monthId,yearId,gStrLocationCode,lStrSubmittedOrNot,lStrWithOrWithoutEmplrContri,gStrPostId);
					}
					else if (lStrUserType.equals("DDO")) {
						lListEmployeesFromDummyOffice = lObjTreasuryDAO.getEmployeesFromDummyOfficeDdoNPS(lStrDummyOfficeId,monthId,yearId,gStrLocationCode,lStrSubmittedOrNot,lStrWithOrWithoutEmplrContri,gStrPostId);

					}
					else {
						//$t 2019
						System.out.println("here 1....................");
						lListEmployeesFromDummyOffice = lObjTreasuryDAO.getEmployeesFromDummyOfficeNPSFour(lStrDummyOfficeId,monthId,yearId,gStrLocationCode,lStrSubmittedOrNot,lStrWithOrWithoutEmplrContri);
					}
					
				}
				/* $t hide becoz 4% contribution only for NPS
				 else
				{
					
					if (lStrUserType.equals("DDO Assistant")) {
						lListEmployeesFromDummyOffice = lObjTreasuryDAO.getEmployeesFromDummyOfficeAst(lStrDummyOfficeId,monthId,yearId,gStrLocationCode,lStrSubmittedOrNot,lStrWithOrWithoutEmplrContri,gStrPostId);
					}
					else if (lStrUserType.equals("DDO")) {
						lListEmployeesFromDummyOffice = lObjTreasuryDAO.getEmployeesFromDummyOfficeDdo(lStrDummyOfficeId,monthId,yearId,gStrLocationCode,lStrSubmittedOrNot,lStrWithOrWithoutEmplrContri,gStrPostId);

					}
					else {
						lListEmployeesFromDummyOffice = lObjTreasuryDAO.getEmployeesFromDummyOffice(lStrDummyOfficeId,monthId,yearId,gStrLocationCode,lStrSubmittedOrNot,lStrWithOrWithoutEmplrContri);
					}
					
				}
				*/
				
				
				String yearCode = lObjDcpsCommonDAO.getYearCodeForYearId(yearId);

				Integer lIntMonth = Integer.parseInt(monthId.toString());
				Integer lIntYear = Integer.parseInt(yearCode);
				
				if (lIntMonth == 1 || lIntMonth == 2 || lIntMonth == 3) {
					lIntYear += 1;
				}
				//for validation
				
				
				
				//end
				
				
				
				if(lListEmployeesFromDummyOffice!=null && lListEmployeesFromDummyOffice.size()>0)
				{
					Object obj[];
					for (int liCtr = 0; liCtr < lListEmployeesFromDummyOffice.size(); liCtr++) {
						obj = (Object[]) lListEmployeesFromDummyOffice.get(liCtr);
						
//						for (int liCtr1 = 0; liCtr1 < 40; liCtr1++) {
//							Object obj1[] = (Object[]) lListEmployeesFromDummyOffice.get(liCtr);
//							System.out.println("obj1-->"+liCtr1+" : "+obj1[liCtr1]);
//						}
						
						
						Long dcpsEmpId=Long.parseLong(obj[1].toString());
						BigDecimal Basic=BigDecimal.valueOf(Double.parseDouble(obj[14].toString()));
						String code=lObjTreasuryDAO.getEmpCmpCode(dcpsEmpId);
						BigDecimal finalAmount=BigDecimal.ZERO;
						String payCmsn=obj[12].toString();
						String acMainBy=obj[19].toString();
						BigDecimal SeventhPCBasic=BigDecimal.valueOf(Double.parseDouble(obj[18].toString()));
						if(Double.parseDouble(obj[18].toString()) >0)
						{
							payCmsn="700349";
							obj[12]=payCmsn;
							obj[14]=SeventhPCBasic;
						}
						else if("700240".equalsIgnoreCase(acMainBy) || "700241".equalsIgnoreCase(acMainBy) || "700242".equalsIgnoreCase(acMainBy))
						{
							payCmsn="700349";
							obj[12]=payCmsn;
						}
						if(code.equalsIgnoreCase("162"))
						{

							
							if(lIntYear <= 2012 ){
								if(lIntMonth < 11){
									finalAmount=(new BigDecimal(PayrollConstants.PAYROLL_DA_RATE_NEW)).multiply(Basic);
								    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
								}
								else{
									finalAmount=(new BigDecimal(72)).multiply(Basic);
								    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
								}
							
							
						}
						else if(lIntYear == 2013 ){
							if(lIntMonth < 7){
								finalAmount=(new BigDecimal(80)).multiply(Basic);
							    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
							}	
							if(lIntMonth >= 7){
								finalAmount=(new BigDecimal(90)).multiply(Basic);
							    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
							}
					}
						else if(lIntYear == 2014 ){
							if(lIntMonth < 7){
								finalAmount=(new BigDecimal(100)).multiply(Basic);
							    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
							}	
							if(lIntMonth >= 7){
								finalAmount=(new BigDecimal(107)).multiply(Basic);
							    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
							}
					}
						else if(lIntYear == 2015 ){
							if(lIntMonth < 7){
								finalAmount=(new BigDecimal(113)).multiply(Basic);
							    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
							}else if(lIntMonth >= 7){
								finalAmount=(new BigDecimal(119)).multiply(Basic);
							    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
							}
						}
						
					
				
						}
						else
						{
							//added by Mum dev for DA
						
							if(payCmsn.equals("700016")){
								if(lIntYear <= 2012 ){
									if(lIntMonth < 11){
										finalAmount=(new BigDecimal(PayrollConstants.PAYROLL_DA_RATE_NEW)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
									else{
										finalAmount=(new BigDecimal(72)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
										
										
								}
								else if(lIntYear == 2013 )
								{
									if(lIntMonth < 5){
										finalAmount=(new BigDecimal(72)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
									else if(lIntMonth > 4 && lIntMonth <10){
										finalAmount=(new BigDecimal(80)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
									else if(lIntMonth > 9 ){
										finalAmount=(new BigDecimal(90)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
								}
								else if(lIntYear == 2014 )
								{
									if(lIntMonth < 5){
										finalAmount=(new BigDecimal(90)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}	
									//added by arpan
									else
									{
										finalAmount=(new BigDecimal(100)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
									
								}
								else if(lIntYear == 2015 )
								{
									if(lIntMonth < 2){
										finalAmount=(new BigDecimal(100)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
									else if(lIntMonth >= 2 && lIntMonth <10){
										finalAmount=(new BigDecimal(107)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									} else if(lIntMonth >=10){
										finalAmount=(new BigDecimal(113)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
								}
								
								else if(lIntYear == 2016 && lIntMonth == 1 ){
									finalAmount=(new BigDecimal(113)).multiply(Basic);
								    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
								}
								//end
							}
							else if(payCmsn.equals("700015")){
								
								
								if(lIntYear <= 2012 ){
									if(lIntMonth < 12){
										finalAmount=(new BigDecimal(PayrollConstants.OLD_FIFTH_PAYCOMMISION_DA_RATE)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
									else{
										finalAmount=(new BigDecimal(151)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
										
								}
								
								if(lIntYear == 2013 ){
									if(lIntMonth < 5){
										finalAmount=(new BigDecimal(151)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
									
									else if(lIntMonth >= 5  && lIntMonth < 7){
										finalAmount=(new BigDecimal(166)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
								}//w.e.f from 01/07/2014 changed on 02/06/15
								if(lIntYear == 2014 ){
									if(lIntMonth < 7){
										finalAmount=(new BigDecimal(200)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
									
									if(lIntMonth >= 7){
										finalAmount=(new BigDecimal(212)).multiply(Basic);
									    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
									}
								}
								
								if((lIntYear == 2015) || (lIntYear == 2016) && (lIntMonth == 1) )
								{
									finalAmount=(new BigDecimal(223)).multiply(Basic);
								    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
								}
								
							
						
						}
						
					}
						
						if(Double.parseDouble(obj[18].toString()) >0)
						{
							
                           
                            if(((lIntYear==2016 && lIntMonth>=7) || (lIntYear>=2017)))
                            {
                             
                               
                                    if(lIntYear>=2017){
                                        finalAmount=(new BigDecimal(4)).multiply(SeventhPCBasic);
                                    }
                                    else
                                    finalAmount=(new BigDecimal(2)).multiply(SeventhPCBasic);
                                    
                                    finalAmount=finalAmount.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
                                    
                                
                                
                            } 

						}
						obj[15]=finalAmount;
						lListEmployeesFromDummyOffice.set(liCtr, obj);
				}
				
				}
				
				Iterator empItr=lListEmployeesFromDummyOffice.iterator();
	            
	            Object[] tupleEmp;
				/*while(empItr.hasNext()){
					tupleEmp = (Object[]) empItr.next();
					pc=tupleEmp[12].toString(); 
					
				}*/
				
				
				
				lDtLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 1,lIntYear);
				lDtFirstDate = lObjDcpsCommonDAO.getFirstDate(lIntMonth - 1,lIntYear);
				
				listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues("TypeOfPaymentDCPS", 1L,inputMap);
				//CmnLookupMst tempCmnLookupMst1 = new CmnLookupMst();
//				tempCmnLookupMst1.setLookupId(700026l);
//				tempCmnLookupMst1.setLookupDesc("6PCArrears");
				//listTypeOfPayment.add(tempCmnLookupMst1);
				//$t 2019
				CmnLookupMst tempCmnLookupMst2 = new CmnLookupMst();
				tempCmnLookupMst2.setLookupId(10001198430l);
				tempCmnLookupMst2.setLookupDesc("Arrear-4% Employer Contribution");
				listTypeOfPayment.add(tempCmnLookupMst2);
				// Code to get all pay commissions
				/* Get All Pay Commissions from Lookup */
				List<CmnLookupMst> listPayCommissionOld = IFMSCommonServiceImpl
						.getLookupValues("PayCommissionDCPS", SessionHelper
								.getLangId(inputMap), inputMap);

				CmnLookupMst tempCmnLookupMst = null;

				for (CmnLookupMst lObjCommonLookupMst : listPayCommissionOld) {

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
							"700015")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700015l);
						tempCmnLookupMst.setLookupDesc("5 PC");
						listPayCommission.add(tempCmnLookupMst);
					}
					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
							"700016")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700016l);
						tempCmnLookupMst.setLookupDesc("6 PC");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
							"700338")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700338l);
						tempCmnLookupMst.setLookupDesc("NonGovt");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
							"700339")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700339l);
						tempCmnLookupMst.setLookupDesc("Padmanabhan");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
							"700340")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700340l);
						tempCmnLookupMst.setLookupDesc("Fourth(IV)");
						listPayCommission.add(tempCmnLookupMst);
					}

					if (Long.valueOf(lObjCommonLookupMst.getLookupId()).toString().equals(
							"700345")) {
						tempCmnLookupMst = new CmnLookupMst();
						tempCmnLookupMst.setLookupId(700345l);
						tempCmnLookupMst.setLookupDesc("Shetty");
						listPayCommission.add(tempCmnLookupMst);
					}
					
				

				}
				tempCmnLookupMst = new CmnLookupMst();
				tempCmnLookupMst.setLookupId(700349l);
				tempCmnLookupMst.setLookupDesc("7th Pay Commission");
				listPayCommission.add(tempCmnLookupMst);
			

			}		
			//List lLstMonths = lObjDcpsCommonDAO.getMonths();
			//$t 2019
			List lLstMonths = lObjDcpsCommonDAO.getMonths1();
			List lLstYears=null;
			if(isNPS!=null && !"".equalsIgnoreCase(isNPS) && "yes".equalsIgnoreCase(isNPS))
			{
				//$t 2019
				lLstYears= lObjTreasuryDAO.getFinyearsForNPS1();
			}
			else
			{
				lLstYears= lObjTreasuryDAO.getFinyears();
			}
			
			//lLstYears= lObjTreasuryDAO.getFinyearsForNPS();

			 

			inputMap.put("lListEmployeesFromDummyOffice",lListEmployeesFromDummyOffice);
			if(lListEmployeesFromDummyOffice != null)
			{
				if(lListEmployeesFromDummyOffice.size() != 0)
				{
					inputMap.put("totalRecords", lListEmployeesFromDummyOffice.size());	
				}
				else
				{
					inputMap.put("totalRecords", 0);
				}
			}
			else
			{
				inputMap.put("totalRecords", 0);
			}
			
			
			inputMap.put("dummyOfficeId", lStrDummyOfficeId);
			inputMap.put("pc", pc);
			inputMap.put("monthId", monthId);
			inputMap.put("yearId", yearId);
			inputMap.put("MONTHS", lLstMonths);
			inputMap.put("YEARS", lLstYears);
			inputMap.put("OFFICELIST", lLstOffice);
			
			inputMap.put("FirstDate", lDtFirstDate);
			inputMap.put("LastDate", lDtLastDate);
			inputMap.put("listPayCommission", listPayCommission);
			inputMap.put("listTypeOfPayment", listTypeOfPayment);
			
			inputMap.put("IsSubmitted", lStrSubmittedOrNot);
			inputMap.put("isNPS", isNPS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSChallanEntry2");

		}

		catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject saveContriForDeptn(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlFlag = null;
		Long lLongDeptnContriId = null;
		Long lLongVoucherNo = null;
		Date lDateVoucherDate = null;
		Long lLongVoucherNoEmplr = null;
		Date lDateVoucherDateEmplr = null;
		Double lDoubleVoucherAmount = null;
		Double lDoubleVoucherAmountEmplr = null;
		String lStrUse = "";
		String isNPS="NO";
		String dcpsEmpIds= "";
		String txtStartDate= "";
		String txtEndDate= "";
		try {
			setSessionInfo(inputMap);

			TrnDcpsDeputationContribution[] lArrTrnDcpsDeputationContribution = (TrnDcpsDeputationContribution[]) inputMap
					.get("lArrTrnDcpsDeputationContribution");
           //$t 2019
			TreasuryDAO lObjTreasuryDAO = new TreasuryDAOImpl(TreasuryServiceImpl2.class, serv.getSessionFactory());
			
			lStrUse = StringUtility.getParameter("Use", request).trim();
			isNPS=StringUtility.getParameter("isNPS", request).trim();
			
			dcpsEmpIds=StringUtility.getParameter("dcpsEmpIds", request).trim();
			txtStartDate=StringUtility.getParameter("txtStartDate", request).trim();
			txtEndDate=StringUtility.getParameter("txtEndDate", request).trim();
			
			for (Integer lInt = 0; lInt < lArrTrnDcpsDeputationContribution.length; lInt++) {
				//$t 2019
				 Date vDate1=new SimpleDateFormat("dd/MM/yyyy").parse("00/00/0000"); 
			     lArrTrnDcpsDeputationContribution[lInt].setContribution(0D);
			     lArrTrnDcpsDeputationContribution[lInt].setChallanDate(vDate1);
			     lArrTrnDcpsDeputationContribution[lInt].setChallanNo("0");
			     lArrTrnDcpsDeputationContribution[lInt].setIsArrears('Y');

				lBlFlag = false;
				IdGenerator id=new IdGenerator();
				lLongDeptnContriId=id.PKGenerator("TRN_DCPS_DEPUTATION_CONTRIBUTION", inputMap);
				//lLongDeptnContriId = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_DEPUTATION_CONTRIBUTION", inputMap);
				gLogger.info("Generated ID is"+lLongDeptnContriId);
				lArrTrnDcpsDeputationContribution[lInt].setDcpsDeptnContriId(lLongDeptnContriId);
				lObjTreasuryDAO.create(lArrTrnDcpsDeputationContribution[lInt]);
				
				lBlFlag = true;
				if (lBlFlag) {
					inputMap.put("currentDeptnCntrbtnVO",lArrTrnDcpsDeputationContribution[lInt]);
					//$t 2019
					Date vDate=new SimpleDateFormat("dd/MM/yyyy").parse("00/00/0000");  
//					lLongVoucherNo = Long.valueOf(lArrTrnDcpsDeputationContribution[lInt].getChallanNo());
//					lDateVoucherDate = lArrTrnDcpsDeputationContribution[lInt].getChallanDate();
//					lDoubleVoucherAmount = lArrTrnDcpsDeputationContribution[lInt].getContribution();
					lLongVoucherNo = (long) 0;
					lDateVoucherDate = vDate;
					lDoubleVoucherAmount = 0d;
					
					if(lStrUse.equals("WithEmplrContri"))
					{
						lLongVoucherNoEmplr = Long.valueOf(lArrTrnDcpsDeputationContribution[lInt].getChallanNoEmplr());
						lDateVoucherDateEmplr =  lArrTrnDcpsDeputationContribution[lInt].getChallanDateEmplr();
						lDoubleVoucherAmountEmplr = lArrTrnDcpsDeputationContribution[lInt].getContributionEmplr();
						
					}
					else
					{
						lLongVoucherNoEmplr = null;
						lDateVoucherDateEmplr = null;
						lDoubleVoucherAmountEmplr = 0d;
					}
					//$t 2019
					inputMap.put("lLongVoucherNo", lLongVoucherNo);
					inputMap.put("lDateVoucherDate", lDateVoucherDate);
					inputMap.put("lLongVoucherNoEmplr", lLongVoucherNoEmplr);
					inputMap.put("lDateVoucherDateEmplr", lDateVoucherDateEmplr);
					inputMap.put("lDoubleVoucherAmount",lDoubleVoucherAmount);
					inputMap.put("lDoubleVoucherAmountEmplr",lDoubleVoucherAmountEmplr);
					inputMap.put("isNPS",isNPS);
					
					resObj = insertCntrbtnForDeptn(inputMap);
//					resObj = serv.executeService("insertCntrbtnForDeptn",inputMap);
					
					//resObj = serv.executeService("insertCntrbtnForDeptn",inputMap);
				}
			}

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			gLogger.error("Error is:" + ex, ex);
			return resObj;
		}

		String lSBStatus = getResponseXMLDoc(lBlFlag,lStrUse).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject insertCntrbtnForDeptn(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long dcpsEmpId = null;
		MstEmp lObjMstEmp = null;
		TrnDcpsContribution lObjTrnDcpsContribution = null;
		TrnDcpsDeputationContribution lObjTrnDcpsDeputationContribution = (TrnDcpsDeputationContribution) objectArgs
				.get("currentDeptnCntrbtnVO");
		MstDcpsContriVoucherDtls lObjPrvsContriVoucherVOForChallan = null;
		Long lLngDcpsContriVoucherId = null;
		Double lDoubleChallanAmount = 0d;
		Long lLongVoucherNo = null;
		Date lDateVoucherDate = null;
		String isNPS="NO";
		
		try {

			setSessionInfo(objectArgs);

			TreasuryDAO lObjTreasuryDAO = new TreasuryDAOImpl(MstDcpsContriVoucherDtls.class,serv.getSessionFactory());

			dcpsEmpId = Long.valueOf(lObjTrnDcpsDeputationContribution.getDcpsEmpId());
			
			// Code has to be added here where it checks for a unique combination of year,month,treasur_code,scheme_code,challan_no,challan_date,is_challan in mst_dcps_contri_voucher_dtls table.
			
			String lStrUse = StringUtility.getParameter("Use", request).trim();
			if(lStrUse.equals("WithoutEmplrContri"))
			{
				lLongVoucherNo = Long.valueOf(objectArgs.get("lLongVoucherNo").toString().trim());
				lDateVoucherDate = (Date) objectArgs.get("lDateVoucherDate");
			}
			else
			{
				lLongVoucherNo = Long.valueOf(objectArgs.get("lLongVoucherNoEmplr").toString().trim());
				lDateVoucherDate = (Date) objectArgs.get("lDateVoucherDateEmplr");
			}
			
			//Long lLongTreasuryCode = Long.valueOf(lObjTrnDcpsDeputationContribution.getTreasury());
			Long lLongTreasuryCode =lLngLocId;
			String lStrDdoCode = lObjTrnDcpsDeputationContribution.getDummyOfficeId();
			Long lLongYearId = lObjTrnDcpsDeputationContribution.getFinYearId();
			Long lLongMonthId = lObjTrnDcpsDeputationContribution.getMonthId();
			String lStrSchemeCode = null;
			//$t 2019
			lStrSchemeCode = objectArgs.get("lLongVoucherNoEmplr").toString().trim();//lLongVoucherNo
			isNPS = objectArgs.get("isNPS").toString().trim();
			// As conveyed by Vijayalaxmi, SCHEME_CD in MAIN_DCPS_CTR is EMP_CHNO of DCPS_CHALLAN_DETAIL, so in the same way it is set here.
			
			lObjPrvsContriVoucherVOForChallan = lObjTreasuryDAO.getContriVoucherVOForInputDtlsForChallan(lLongYearId, lLongMonthId,
					lStrDdoCode, lLongTreasuryCode,lStrSchemeCode,lLongVoucherNo,lDateVoucherDate);

			//lObjMstEmp = (MstEmp) lObjTreasuryDAO.read(dcpsEmpId);
			
			if(lObjPrvsContriVoucherVOForChallan == null)
			{	
				lLngDcpsContriVoucherId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_contri_voucher_dtls", objectArgs);
				MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = new MstDcpsContriVoucherDtls();
				lObjMstDcpsContriVoucherDtls.setDcpsContriVoucherDtlsId(lLngDcpsContriVoucherId);
				//lObjMstDcpsContriVoucherDtls.setBillGroupId(lObjMstEmp.getBillGroupId());
				lObjMstDcpsContriVoucherDtls.setDdoCode(lStrDdoCode);
				lObjMstDcpsContriVoucherDtls.setVoucherNo(lLongVoucherNo);
				lObjMstDcpsContriVoucherDtls.setVoucherDate(lDateVoucherDate);
				//lObjMstDcpsContriVoucherDtls.setEmplrVoucherNo(Long.valueOf(objectArgs.get("lLongVoucherNoEmplr").toString()));
				//lObjMstDcpsContriVoucherDtls.setEmplrVoucherDate((Date) objectArgs.get("lDateVoucherDateEmplr"));
				//lObjMstDcpsContriVoucherDtls.setEmplrVoucherAmount((Double) objectArgs.get("lDoubleVoucherAmountEmplr"));
			//	lObjMstDcpsContriVoucherDtls.setTreasuryCode(Long.valueOf(lObjTrnDcpsDeputationContribution.getTreasury()));
				lObjMstDcpsContriVoucherDtls.setTreasuryCode(lLongTreasuryCode);
				lObjMstDcpsContriVoucherDtls.setYearId(lObjTrnDcpsDeputationContribution.getFinYearId());
				lObjMstDcpsContriVoucherDtls.setMonthId(lObjTrnDcpsDeputationContribution.getMonthId());
				lObjMstDcpsContriVoucherDtls.setManuallyMatched(0l);
				
				if(lStrUse.equals("WithoutEmplrContri"))
				{
					lObjMstDcpsContriVoucherDtls.setStatus('F');
					lObjMstDcpsContriVoucherDtls.setPostEmplrContriStatus(0l);
				}
				if(lStrUse.equals("WithEmplrContri"))
				{
					lObjMstDcpsContriVoucherDtls.setStatus('G');
					lObjMstDcpsContriVoucherDtls.setPostEmplrContriStatus(1l);
				}
				//$t 2019
				//lObjMstDcpsContriVoucherDtls.setVoucherAmount((Double) objectArgs.get("lDoubleVoucherAmount"));
				//temporarily commented below line 
				//lDoubleChallanAmount = 0d;
				lDoubleChallanAmount = lObjTreasuryDAO.getTotalVoucherAmountForGivenChallan(lLongMonthId, lLongYearId, lStrSchemeCode, lStrDdoCode, lLongTreasuryCode, lLongVoucherNo, lDateVoucherDate,lStrUse);
				lObjMstDcpsContriVoucherDtls.setVoucherAmount(lDoubleChallanAmount);
				
				lObjMstDcpsContriVoucherDtls.setSchemeCode(lStrSchemeCode);
				lObjMstDcpsContriVoucherDtls.setIsChallan('Y');
				lObjMstDcpsContriVoucherDtls.setVoucherStatus(1l);
				
				lObjMstDcpsContriVoucherDtls.setCreatedDate(gDtCurrDt);
				lObjMstDcpsContriVoucherDtls.setPostId(gLngPostId);
				lObjMstDcpsContriVoucherDtls.setUserId(gLngUserId);
				
				lObjMstDcpsContriVoucherDtls.setMissingCreditApprovalDate(gDtCurrDt);
				
				lObjTreasuryDAO.create(lObjMstDcpsContriVoucherDtls);
				
			}
			else
			{
				lLngDcpsContriVoucherId = lObjPrvsContriVoucherVOForChallan.getDcpsContriVoucherDtlsId();
				lDoubleChallanAmount = lObjTreasuryDAO.getTotalVoucherAmountForGivenChallan(lLongMonthId, lLongYearId, lStrSchemeCode, lStrDdoCode, lLongTreasuryCode, lLongVoucherNo, lDateVoucherDate,lStrUse);
				lObjPrvsContriVoucherVOForChallan.setVoucherAmount(lDoubleChallanAmount);
				
				lObjPrvsContriVoucherVOForChallan.setUpdatedDate(gDtCurrDt);
				lObjPrvsContriVoucherVOForChallan.setUpdatedPostId(gLngPostId);
				lObjPrvsContriVoucherVOForChallan.setUpdatedUserId(gLngUserId);
				
				lObjPrvsContriVoucherVOForChallan.setMissingCreditApprovalDate(gDtCurrDt);
				
				lObjTreasuryDAO.update(lObjPrvsContriVoucherVOForChallan);
			}
			
			// Only Employee Contribution is saved. Employer contribution is not saved.
			
			lObjTrnDcpsContribution = new TrnDcpsContribution();
			Long lLongTempdcpsContributionId = IFMSCommonServiceImpl.getNextSeqNum("TRN_DCPS_CONTRIBUTION", objectArgs);
			lObjTrnDcpsContribution.setDcpsContributionId(lLongTempdcpsContributionId);
			lObjTrnDcpsContribution.setDcpsEmpId(dcpsEmpId);
			lObjTrnDcpsContribution.setRltContriVoucherId(lLngDcpsContriVoucherId);
			
			// Below 3 details are not stored for a deputed employee, so commented.
		/*	
		
			lObjTrnDcpsContribution.setDdoCode(lObjMstEmp.getDdoCode());
			lObjTrnDcpsContribution.setDcpsDdoBillGroupId(lObjMstEmp.getBillGroupId());
			lObjTrnDcpsContribution.setSchemeCode(lObjTreasuryDAO.getSchemeCodeForBillGroupId(lObjMstEmp.getBillGroupId()));
		*/
			//$t 2019
//			lLongVoucherNo = (long) 0;
//			lDateVoucherDate = vDate;
//			lDoubleVoucherAmount = 0d;
		
			Date vDate=new SimpleDateFormat("dd/MM/yyyy").parse("00/00/0000");
			lObjTrnDcpsContribution.setTreasuryCode(lLongTreasuryCode);
			lObjTrnDcpsContribution.setDdoCode(lObjTrnDcpsDeputationContribution.getDummyOfficeId());
			lObjTrnDcpsContribution.setTypeOfPayment(lObjTrnDcpsDeputationContribution.getTypeOfPayment()); 
			lObjTrnDcpsContribution.setPayCommission(lObjTrnDcpsDeputationContribution.getPayCommission());
			lObjTrnDcpsContribution.setFinYearId(lObjTrnDcpsDeputationContribution.getFinYearId());
			lObjTrnDcpsContribution.setMonthId(lObjTrnDcpsDeputationContribution.getMonthId());
			//$t 2019
			//lObjTrnDcpsContribution.setContribution(lObjTrnDcpsDeputationContribution.getContribution());
			lObjTrnDcpsContribution.setContribution(0d);
			lObjTrnDcpsContribution.setContributionEmplr(lObjTrnDcpsDeputationContribution.getContributionEmplr());
			//
			lObjTrnDcpsContribution.setStartDate(lObjTrnDcpsDeputationContribution.getStartDate());
			lObjTrnDcpsContribution.setEndDate(lObjTrnDcpsDeputationContribution.getEndDate());
			lObjTrnDcpsContribution.setBasicPay(lObjTrnDcpsDeputationContribution.getBasicPay());
			lObjTrnDcpsContribution.setDP(lObjTrnDcpsDeputationContribution.getDP());
			lObjTrnDcpsContribution.setDA(lObjTrnDcpsDeputationContribution.getDA());
			lObjTrnDcpsContribution.setLangId(gLngLangId);
			lObjTrnDcpsContribution.setLocId(lLngLocId);
			lObjTrnDcpsContribution.setDbId(gLngDBId);
			lObjTrnDcpsContribution.setCreatedPostId(gLngPostId);
			lObjTrnDcpsContribution.setCreatedUserId(gLngUserId);
			lObjTrnDcpsContribution.setCreatedDate(gDtCurrDt);
			lObjTrnDcpsContribution.setIsChallan('Y');
			//$t 2019
			lObjTrnDcpsContribution.setIsArrears('Y');
			//
			lObjTrnDcpsContribution.setSchemeCode(lStrSchemeCode);
			//$t 2019
			//lObjTrnDcpsContribution.setVoucherNo(lLongVoucherNo);
			//lObjTrnDcpsContribution.setVoucherDate(lDateVoucherDate);
			lObjTrnDcpsContribution.setVoucherNo((long) 0);
			lObjTrnDcpsContribution.setVoucherDate(vDate);
			
			lObjTrnDcpsContribution.setRegStatus(1l);
			
			if(isNPS!=null && !"".equalsIgnoreCase(isNPS) && "yes".equalsIgnoreCase(isNPS))
			{
				lObjTrnDcpsContribution.setStatus('H');
				lObjTrnDcpsContribution.setPostEmplrContriStatus(1l);
				lObjTrnDcpsContribution.setEmployerContriFlag('Y');
				lObjTrnDcpsContribution.setIsDeputation('Y');
			}
			else
			{
				if(lStrUse.equals("WithoutEmplrContri"))
				{
					lObjTrnDcpsContribution.setStatus('A');
					lObjTrnDcpsContribution.setPostEmplrContriStatus(0l);
					lObjTrnDcpsContribution.setEmployerContriFlag('N');
					lObjTrnDcpsContribution.setIsDeputation('Y');
				}
				if(lStrUse.equals("WithEmplrContri"))
				{
					lObjTrnDcpsContribution.setStatus('G');
					lObjTrnDcpsContribution.setPostEmplrContriStatus(1l);
					lObjTrnDcpsContribution.setEmployerContriFlag('Y');
					lObjTrnDcpsContribution.setIsDeputation('Y');
				}
			}
			
			
			
			lObjTrnDcpsContribution.setMissingCreditApprovalDate(gDtCurrDt);

			lObjTreasuryDAO.create(lObjTrnDcpsContribution);
			//$t 2019
			lObjTreasuryDAO.updateFlag(lObjTrnDcpsDeputationContribution.getDcpsEmpId(),lObjTrnDcpsDeputationContribution.getMonthId(),lObjTrnDcpsDeputationContribution.getFinYearId(),lObjTrnDcpsDeputationContribution.getStartDate(),lObjTrnDcpsDeputationContribution.getEndDate());
			
			objectArgs.put("ajaxKey", "Success");
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

	public ResultObject saveTreasuryNetExcel(Map<Object, Object> inputMap) {

		Object[] xlsData = null;
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			inputMap.put("lLngPostId", gLngPostId);
			Long CreatedPostId = SessionHelper.getPostId(inputMap);
			Long CreatedUserId = SessionHelper.getUserId(inputMap);
			Date CreatedDate = DBUtility.getCurrentDateFromDB();

			// Code For Attachement starts
			Long lObjAttachmentId = null;
			Map attachMap = new HashMap();
			objRes = serv.executeService("FILE_UPLOAD_VOGEN", inputMap);
			objRes = serv.executeService("FILE_UPLOAD_SRVC", inputMap);

			attachMap = (Map) objRes.getResultValue();

			if (attachMap != null) {
				if (attachMap.get("AttachmentId_scan") != null) {
					lObjAttachmentId = Long.parseLong(String.valueOf(attachMap
							.get("AttachmentId_scan")));
				}
				if (lObjAttachmentId != null) {
					CmnAttachmentMst attachmentMst = new CmnAttachmentMst();
					attachmentMst.setAttachmentId(lObjAttachmentId);
				}
			}

			// Code For Attachement ends

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			if (lObjAttachmentId != null) {
				CmnAttachmentMstDAOImpl mnAttachmentMstDAO = new CmnAttachmentMstDAOImpl(
						CmnAttachmentMst.class, serv.getSessionFactory());
				CmnAttachmentMst cmnAttachmentMst = mnAttachmentMstDAO
						.findByAttachmentId(lObjAttachmentId);
				Iterator lObjIterator = cmnAttachmentMst.getCmnAttachmentMpgs()
						.iterator();
				TreasuryDAO dcpsTreasuryDao = new TreasuryDAOImpl(
						MstDcpsTreasurynetData.class, serv.getSessionFactory());
				MstDcpsTreasurynetData objMstDcpsTreasurynetData = null;
				Long lLngTreasuryNetDataId = null;
				int ddoCodeLength = 0;
				String ddoCode = null;
				while (lObjIterator != null && lObjIterator.hasNext()) {
					CmnAttachmentMpg cmnAttachmentMpg = (CmnAttachmentMpg) lObjIterator
							.next();
					CmnAttdocMst cmnAttDocMst = (CmnAttdocMst) cmnAttachmentMpg
							.getCmnAttdocMsts().iterator().next();

					if (cmnAttDocMst != null) {
						List lObjSheetLst = getDocumentForXLS(cmnAttDocMst,
								request);

						if (lObjSheetLst != null && !lObjSheetLst.isEmpty()) {
							List lObjRowLst = (List) lObjSheetLst.get(0);
							if (lObjRowLst != null && !lObjRowLst.isEmpty()) {
								for (int i = 1; i < lObjRowLst.size(); ++i) {
									xlsData = ((List) lObjRowLst.get(i))
											.toArray();
									objMstDcpsTreasurynetData = new MstDcpsTreasurynetData();
									lLngTreasuryNetDataId = IFMSCommonServiceImpl
											.getNextSeqNum(
													"mst_dcps_treasurynet_Data",
													inputMap);
									ddoCode = xlsData[2].toString();
									ddoCodeLength = xlsData[2].toString()
											.length();
									if (ddoCodeLength > 0 && ddoCodeLength < 6) {
										for (int j = 0; j < (6 - ddoCodeLength); j++) {
											ddoCode = "0" + ddoCode;
										}
									}

									objMstDcpsTreasurynetData
											.setDcpsTreasurynetDataId(lLngTreasuryNetDataId);
									objMstDcpsTreasurynetData
											.setTreasuryCode(Long
													.parseLong((String) xlsData[1]));
									objMstDcpsTreasurynetData
											.setYearDesc((String) xlsData[0]);
									objMstDcpsTreasurynetData
											.setDdoCode(xlsData[1].toString()
													+ ddoCode);
									objMstDcpsTreasurynetData
											.setVorc(((String) xlsData[3])
													.charAt(0));
									objMstDcpsTreasurynetData.setBillNo(Long
											.parseLong((String) xlsData[4]));
									objMstDcpsTreasurynetData.setBillDate(sdf
											.parse((String) xlsData[5]));
									objMstDcpsTreasurynetData.setVoucherNo(Long
											.parseLong((String) xlsData[6]));
									objMstDcpsTreasurynetData
											.setVoucherDate(sdf
													.parse((String) xlsData[7]));
									objMstDcpsTreasurynetData
											.setBillAmount(Double
													.parseDouble((String) xlsData[8]));
									objMstDcpsTreasurynetData
											.setFromScheme((String) xlsData[9]);
									objMstDcpsTreasurynetData
											.setToScheme((String) xlsData[10]);
									objMstDcpsTreasurynetData
											.setDcpsAmount(Double
													.parseDouble((String) xlsData[11]));
									objMstDcpsTreasurynetData
											.setCreatedPostId(CreatedPostId);
									objMstDcpsTreasurynetData
											.setCreatedUserId(CreatedUserId);
									objMstDcpsTreasurynetData
											.setCreatedDate(CreatedDate);
									dcpsTreasuryDao
											.create(objMstDcpsTreasurynetData);
								}

							}
						}
					}
				}

			}
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {

			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;
	}

	private static List getDocumentForXLS(CmnAttdocMst cmnAttDocMst,
			HttpServletRequest request) throws Exception {

		List lLstReturn = null;
		File lObjFile = null;

		try {

			/** Getting path of temp directory in server. **/
			String serverPathStr = request.getSession().getServletContext()
					.getRealPath("UPLOADED-FILES");

			lObjFile = new File(serverPathStr);
			if (!lObjFile.exists()) {
				lObjFile.mkdir();
			}

			String tempFilePath = serverPathStr
					+ (lObjFile.separator.equals("\\") ? ("\\tempFile_")
							: "tempFile_") + System.currentTimeMillis();
			/** Made one file with name tempFile_currentTimeMillis(). **/
			lObjFile = new File(tempFilePath);
			/** Getting file as a output stream. **/
			FileOutputStream lobjFileOutputStream = new FileOutputStream(
					tempFilePath);
			/** write the attachment blob data into temp file. **/
			lobjFileOutputStream.write(cmnAttDocMst.getFinalAttachment(), 0,
					cmnAttDocMst.getFinalAttachment().length);
			lobjFileOutputStream.flush();
			/** Close the output stream. **/
			lobjFileOutputStream.close();
			/** Parse your Excel file, it will return list of list. **/
			lLstReturn = ExcelParser.parseExcel(lObjFile);

		} catch (Exception e) {
			throw (e);
		} finally {
			if (lObjFile != null) {
				/** Delete the temp file from temp directory in server. **/
				lObjFile.delete();
			}
		}

		return lLstReturn;
	}
	
	public ResultObject populateTownsUsingAjax(Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest lObjRequest = (HttpServletRequest) lMapInputMap.get("requestObj");
		ServiceLocator lObjServLoctr = (ServiceLocator) lMapInputMap.get("serviceLocator");

		try {
			
			setSessionInfo(lMapInputMap);
			TreasuryDAO lObjTreasuryDAO = new TreasuryDAOImpl(TreasuryServiceImpl2.class,serv.getSessionFactory());
			String lStrCurrDst = StringUtility.getParameter("cmbDist", lObjRequest);

			// Call the functions to get list of designations for the selected
			// office
			List lLstTaluka = lObjTreasuryDAO.getTowns(Long.parseLong(lStrCurrDst));

			String lStrTempResult = null;
			if (lLstTaluka != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lLstTaluka, "desc", "id", true).toString();
			}
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			// e.printStackTrace();
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}
	
	
	
	public ResultObject getDeptnInfo(Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest lObjRequest = (HttpServletRequest) lMapInputMap.get("requestObj");
		ServiceLocator lObjServLoctr = (ServiceLocator) lMapInputMap.get("serviceLocator");

		try {
			
			setSessionInfo(lMapInputMap);
			TreasuryDAO lObjTreasuryDAO = new TreasuryDAOImpl(TreasuryServiceImpl2.class,serv.getSessionFactory());
			String dcpsId = StringUtility.getParameter("dcpsId", lObjRequest);
			gLogger.info("DID"+dcpsId);
			// Call the functions to get list of designations for the selected
			// office
			
			
			String Flag = StringUtility.getParameter("Flag", lObjRequest);
			String TypeOfPayment = StringUtility.getParameter("TypeOfPayment", lObjRequest);
			String Flags = StringUtility.getParameter("Flags", lObjRequest);
			
			gLogger.info("########Flag"+Flag);
			String lStrResult=null;
			
			if(Flag.equals("DELREG")){
				gLogger.info("########Flags"+Flags);
				List DelRegExist = lObjTreasuryDAO.getDelRegInfo(dcpsId,TypeOfPayment);
				gLogger.info("DelRegExist"+DelRegExist.size());
				 gLogger.info("THEFLAG#####DELREG######");
				String lStrTempResult = null;
				String StartDate=null;
				String EndDate=null; 
				
				       gLogger.info("DelRegExist"+DelRegExist.size());
					 Object[] tuple = null;
				   	//Object[] tuple = (Object[]) DeptnInfo.get(0);
				   	Iterator it=DelRegExist.iterator();
				   
				   	StringBuilder lStrBldXMLs = new StringBuilder();

					lStrBldXMLs.append("<XMLDOCS>");

				while(it.hasNext()){
					tuple=(Object[]) it.next();
					StartDate=tuple[0].toString();
					EndDate=tuple[1].toString();
					getResponsesXMLDocs(StartDate,EndDate,DelRegExist.size(),lStrBldXMLs).toString();
				}
				lStrBldXMLs.append("</XMLDOCS>");
				gLogger.info("StartDate"+StartDate+" and EndDate "+EndDate);
				String lSBStatuss = lStrBldXMLs.toString();
					//String lSBStatus = getResponsesXMLDoc(AttachDate,DetachDate,DeptnInfo.size()).toString();
					lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatuss)
							.toString();
					lMapInputMap.put("ajaxKey", lStrResult);
			}else if(Flag.equalsIgnoreCase("DEPINFO")){
			gLogger.info("#####null part######");
			List DeptnInfo = lObjTreasuryDAO.getDeptnInfo(dcpsId);

			String lStrTempResult = null;
			String AttachDate=null;
			String DetachDate=null;
			
			       gLogger.info("DeptnInfo"+DeptnInfo.size());
			       gLogger.info("THEFLAG#####DEPINFO######");
				 Object[] tuple = null;
			   	//Object[] tuple = (Object[]) DeptnInfo.get(0);
			   	Iterator it=DeptnInfo.iterator();
			   
			   	StringBuilder lStrBldXML = new StringBuilder();

				lStrBldXML.append("<XMLDOC>");

			while(it.hasNext()){
				tuple=(Object[]) it.next();
				AttachDate=tuple[0].toString();
				DetachDate=tuple[1].toString();
				getResponsesXMLDoc(AttachDate,DetachDate,DeptnInfo.size(),lStrBldXML).toString();
			}
			lStrBldXML.append("</XMLDOC>");
			gLogger.info("attach"+AttachDate+" and detach "+DetachDate);
			String lSBStatus = lStrBldXML.toString();
				//String lSBStatus = getResponsesXMLDoc(AttachDate,DetachDate,DeptnInfo.size()).toString();
				lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
						.toString();
				lMapInputMap.put("ajaxKey", lStrResult);
			}else if(Flag.equals("TOP"))
			{
				List TOPExist = lObjTreasuryDAO.getTOPInfo(dcpsId,TypeOfPayment);
				gLogger.info("TOPExist"+TOPExist.size());
			
				String lStrTempResult = null;
				String StartDate=null;
				String EndDate=null;
				 gLogger.info("THEFLAG#####TOP######");
				       gLogger.info("DeptnInfo"+TOPExist.size());
					 Object[] tuple = null;
				   	//Object[] tuple = (Object[]) DeptnInfo.get(0);
				   	Iterator it=TOPExist.iterator();
				   
				   	StringBuilder lStrBldXML = new StringBuilder();

					lStrBldXML.append("<XMLDOC>");

				while(it.hasNext()){
					tuple=(Object[]) it.next();
					StartDate=tuple[0].toString();
					EndDate=tuple[1].toString();
					getResponsesXMLDoc(StartDate,EndDate,TOPExist.size(),lStrBldXML).toString();
				}
				lStrBldXML.append("</XMLDOC>");
				gLogger.info("StartDate"+StartDate+" and EndDate "+EndDate);
				String lSBStatus = lStrBldXML.toString();
					//String lSBStatus = getResponsesXMLDoc(AttachDate,DetachDate,DeptnInfo.size()).toString();
					lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
							.toString();
					lMapInputMap.put("ajaxKey", lStrResult);
			}
			
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		
		}catch (Exception e) {
			// e.printStackTrace();
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}
	
	
	private StringBuilder getResponsesXMLDoc(String AttachDate, String DetachDate,int DeptnInfoSize,StringBuilder lStrBldXML) {
		
		
		String Dispatcher=AttachDate+"#"+DetachDate+"#"+DeptnInfoSize;
		
		//StringBuilder lStrBldXML = new StringBuilder();

		//lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <Dispatcher>");
		lStrBldXML.append(Dispatcher);
		lStrBldXML.append("  </Dispatcher>");
		//lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	private StringBuilder getResponsesXMLDocs(String AttachDate, String DetachDate,int DeptnInfoSize,StringBuilder lStrBldXMLs) {
		
		
		String Dispatcher=AttachDate+"#"+DetachDate+"#"+DeptnInfoSize;
		
		//StringBuilder lStrBldXML = new StringBuilder();

		//lStrBldXML.append("<XMLDOC>");
		lStrBldXMLs.append("  <Dispatchers>");
		lStrBldXMLs.append(Dispatcher);
		lStrBldXMLs.append("  </Dispatchers>");
		//lStrBldXML.append("</XMLDOC>");

		return lStrBldXMLs;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public ResultObject getFieldDeptList(Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest lObjRequest = (HttpServletRequest) lMapInputMap.get("requestObj");
		ServiceLocator lObjServLoctr = (ServiceLocator) lMapInputMap.get("serviceLocator");

		try {
			
			setSessionInfo(lMapInputMap);
			TreasuryDAO lObjTreasuryDAO = new TreasuryDAOImpl(TreasuryServiceImpl2.class,serv.getSessionFactory());
			String lStrCurrDst = StringUtility.getParameter("cmbAdmin", lObjRequest);

			// Call the functions to get list of designations for the selected
			// office
			List listFieldDept = lObjTreasuryDAO.getFieldDept(Long.parseLong(lStrCurrDst));

			String lStrTempResult = null;
			if (listFieldDept != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(listFieldDept, "desc", "id", true).toString();
			}
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			// e.printStackTrace();
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}
	
	
	
	
	
	
	
	
	
	
}
