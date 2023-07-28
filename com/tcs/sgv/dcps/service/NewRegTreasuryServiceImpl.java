/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 11, 2011		Vihan Khatri								
 *******************************************************************************
 */
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

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.dao.CommonDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.helper.WorkFlowHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAO;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAOImpl;
import com.tcs.sgv.dcps.valueobject.HstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.wf.delegate.WorkFlowDelegate;


/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 11, 2011
 */
public class NewRegTreasuryServiceImpl extends ServiceImpl implements NewRegTreasuryService {

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

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

	/* Global Variable for User Location */
	String gStrUserLocation = null;

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

		}

	}

	/**
	 * Method to load the DDO Codes and DDO Names respective to the Treasury
	 * 
	 * @param inputMap
	 * @return ResultObject
	 */

	public ResultObject loadTOPhyFormRcvdList(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			setSessionInfo(inputMap);

			String lStrPostId = SessionHelper.getPostId(inputMap).toString();
			String lStrUserType = StringUtility.getParameter("UserType", request);

			lStrUserType = "";

			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(null, serv.getSessionFactory());

			List lListFormsForATORejection = lObjNewRegTreasuryDAO.getApprovalByDDODatesforAll(gStrPostId);

			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Object[] lObjArr = null;
			List lLongListEmpIds = new ArrayList();
			List lDateListApprovalByDDO = new ArrayList();
			List lLongListEmpIdsForRejection = new ArrayList();
			Integer lIntDaysDifference = null;

			for (Integer lInt = 0; lInt < lListFormsForATORejection.size(); lInt++) {
				lObjArr = (Object[]) lListFormsForATORejection.get(lInt);
				if (lObjArr[1] == null) {
					continue;
				}
				lLongListEmpIds.add(Long.valueOf(lObjArr[0].toString()));
				lDateListApprovalByDDO.add(lObjArr[1]);
				lIntDaysDifference = daysBetween(lObjSimpleDateFormat.parse(lObjArr[1].toString()), gDtCurDate);
				if (lIntDaysDifference > 30) {
					lLongListEmpIdsForRejection.add(Long.valueOf(lObjArr[0].toString()));
				}
			}

			inputMap.put("empIdsForPhysicalFormsNotReceived", lLongListEmpIdsForRejection);
			resObj = serv.executeService("rejectRequestForPhyFormNotRcvd", inputMap);

			List lListForms = lObjNewRegTreasuryDAO.getAllDDOListForPhyFormRcvd(lStrPostId, lStrUserType);
			inputMap.put("formList", lListForms);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSPhyFormRcvdList");

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	/**
	 * Method to load the employee forms according to the DDO code selected
	 * 
	 * @param inputMap
	 * @return ResultObject
	 */

	public ResultObject loadTOPhyFormRcvd(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(null, serv.getSessionFactory());

			String lStrDDOCode = StringUtility.getParameter("ddoCode", request);

			List lListForms = lObjNewRegTreasuryDAO.getAllFormsForDDO(lStrDDOCode, gStrPostId);

			inputMap.put("formPhyList", lListForms);

			List UserList = getHierarchyUsers(inputMap);

			inputMap.put("UserList", UserList);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSPhyFormRcvd");

		} catch (Exception e) {
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	public ResultObject loadFormStatusList(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			setSessionInfo(inputMap);

			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(null, serv.getSessionFactory());

			String lStrATOPostId = SessionHelper.getPostId(inputMap).toString();
			List UserList = getHierarchyUsers(inputMap);
			String lStrTOPostId = UserList.get(0).toString();

			List AllFormsStatusList = lObjNewRegTreasuryDAO.getAllFormsStatusList(lStrATOPostId, lStrTOPostId);
			inputMap.put("AllFormsStatusList", AllFormsStatusList);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSFormStatusList");

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;

	}

	/**
	 * Method to submit the physical form received
	 * 
	 * @param inputMap
	 * @return ResultObject
	 */

	public ResultObject submitPhyFormRcvdStatus(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);

		Boolean lStrflag;

		try {
			setSessionInfo(inputMap);
			lStrflag = false;
			MstEmp lObjDcpsEmpMst;
			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());

			String lStrNos = StringUtility.getParameter("empNos", request);
			String[] lArrdcpsPhyFormRcvdStatusIds = lStrNos.split("~");

			String toPost = StringUtility.getParameter("ForwardToPost", request).toString();
			String toLevel = gObjRsrcBndle.getString("DCPS.TREASURY");

			inputMap.put("toPost", toPost);
			inputMap.put("toPostId", toPost);
			inputMap.put("toLevel", toLevel);

			inputMap.put("jobTitle", gObjRsrcBndle.getString("DCPS.RegistrationForm"));
			inputMap.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.RegistrationFormID")));

			for (Integer i = 0; i < lArrdcpsPhyFormRcvdStatusIds.length; i++) {

				inputMap.put("Pkvalue", lArrdcpsPhyFormRcvdStatusIds[i].trim());

				WorkFlowDelegate.forward(inputMap);
				lObjDcpsEmpMst = (MstEmp) lObjNewRegTreasuryDAO.read(Long.valueOf(lArrdcpsPhyFormRcvdStatusIds[i].trim()));
				lObjDcpsEmpMst.setPhyRcvdFormStatus(1L);
				lObjDcpsEmpMst.setPhyRcvdDate(gDtCurrDt);
				lObjNewRegTreasuryDAO.update(lObjDcpsEmpMst);
				lStrflag = true;

			}
		} catch (Exception e) {
			//e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			return objRes;
		}

		String lSBStatus = getResponseXMLDoc(lStrflag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	/**
	 * 
	 * <H3>Description -</H3>
	 * 
	 * 
	 * @author Kapil Devani
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public ResultObject ApproveForm(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlflag = null;
		Long lLngEmpId = null;
		String lStrDcpsId = null;
		Boolean lBlBillGroupFlag = false;
		String ppan=null;
		try {
			setSessionInfo(inputMap);

			MstEmp lObjDcpsEmpMst;
			lBlflag = false;

			String lStrNos = StringUtility.getParameter("Emp_Id", request).trim();
			String[] lArrEmpNo = lStrNos.split("~");
		
			String lStrEmpId = null;
			String lStrDdoCode = null;
			Long lLongHstEmpIdPk = null;
			Date lDateDOJ = null;
			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());

			for (Integer lInt = 0; lInt < lArrEmpNo.length; lInt++) {

				lStrEmpId = lArrEmpNo[lInt].trim();
				lLngEmpId = Long.parseLong(lStrEmpId);

				// Set the value in Read VO
				lStrDcpsId = generateDCPSId(inputMap, lLngEmpId);
				
				//NPS
				ppan = generatePPAN(inputMap, lLngEmpId);
				System.out.println("ppan is****"+ppan);
				
				//END
				
				
				inputMap.put("DCPSId", lStrDcpsId);

				// //Added code japen
				inputMap.put("dcpsEmpId", lLngEmpId);
				
				//inputMap.put("ddoCode",);
				
				lObjDcpsEmpMst = (MstEmp) lObjNewRegTreasuryDAO.read(lLngEmpId);
				lStrDdoCode = lObjDcpsEmpMst.getDdoCode();
				
				inputMap.put("ddoCode", lStrDdoCode);
				
				lDateDOJ = lObjDcpsEmpMst.getDoj();

				ResultObject resObj = serv.executeService("createEmployee", inputMap);

				if (resObj.getResultCode() == ErrorConstants.ERROR) {

					throw new Exception();
				}
				
				Map inputMapFromPayroll = (Map)resObj.getResultValue();
				
				Long lLngOrgEmpMstId = Long.parseLong(inputMapFromPayroll.get("orgEmpMstId").toString());
				String lStrBillGroupId = "";
				
				if(inputMapFromPayroll.containsKey("billNo"))
				{
					if(inputMapFromPayroll.get("billNo") != null)
					{
						if(!inputMapFromPayroll.get("billNo").toString().equals(""))
						{
							lStrBillGroupId = inputMapFromPayroll.get("billNo").toString();
							lBlBillGroupFlag = true;
						}
					}
				}
				
				String lStrSevarthId = inputMapFromPayroll.get("sevarthId").toString();
				Long lLongBillGroupId = null;

				if (!lStrBillGroupId.equals("")) {
					lLongBillGroupId = Long.valueOf(lStrBillGroupId);
				}
				//NPS
				lObjDcpsEmpMst.setPpan(ppan);
				//END
				lObjDcpsEmpMst.setDcpsId(lStrDcpsId);
				if(lObjDcpsEmpMst.getDcpsOrGpf().equals('N'))
					lObjDcpsEmpMst.setRegStatus(2L);
				else
					lObjDcpsEmpMst.setRegStatus(1L);
				lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
				lObjDcpsEmpMst.setBillGroupId(lLongBillGroupId);
				lObjDcpsEmpMst.setSevarthId(lStrSevarthId);
				lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
				lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
				lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);
				lObjDcpsEmpMst.setOrgEmpMstId(lLngOrgEmpMstId);
				lObjNewRegTreasuryDAO.update(lObjDcpsEmpMst);
				
				HstEmp lObjHstEmp = new HstEmp();

				lLongHstEmpIdPk = IFMSCommonServiceImpl.getNextSeqNum("hst_dcps_emp_details", inputMap);

				lObjHstEmp.setHstdcpsId(lLongHstEmpIdPk);

				lObjHstEmp.setDbId(gLngDBId);
				lObjHstEmp.setDcpsEmpId(lLngEmpId);
				lObjHstEmp.setLocId(Long.parseLong(gStrLocationCode));
				lObjHstEmp.setDdoCode(lStrDdoCode);
				//lObjHstEmp.setStartDate(gDtCurDate);
				lObjHstEmp.setStartDate(lDateDOJ);
				lObjHstEmp.setCreatedUserId(gLngUserId);
				lObjHstEmp.setCreatedPostId(gLngPostId);
				lObjHstEmp.setCreatedDate(gDtCurDate);

				lObjNewRegTreasuryDAO.create(lObjHstEmp);

				// Archive Form 1 for DCPS Employee

				lObjNewRegTreasuryDAO.ArchiveNewRegForm(lObjDcpsEmpMst, serv);

			}

			objRes = serv.executeService("SIXPC_ARREARS_VOGEN", inputMap);

			objRes = serv.executeService("SIXPC_ARREARS_SRVC", inputMap);

			lBlflag = true;

			String lSBStatus = getResponseXMLDoc(lBlflag, lStrDcpsId, lLngEmpId,lBlBillGroupFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error(" Error Approving form is : " + e, e);
			//e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			return objRes;
		}

		return objRes;

	}

	/**
	 * 
	 * Function used to generate the DCPS ID by the treasury
	 * 
	 * 
	 * @author Kapil Devani
	 * @param inputMap
	 * @return
	 */

	public String generateDCPSId(Map inputMap, Long lLngEmpId) throws Exception{

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		MstEmp lObjEmpData = null;
		String DCPSID = "";

		try {
			setSessionInfo(inputMap);

			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());

			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			lObjEmpData = new MstEmp();
			lObjEmpData = (MstEmp) lObjNewRegTreasuryDAO.read(lLngEmpId);

			// Code for DCPS ID Generation

			String lLongDDOCode = lObjEmpData.getDdoCode();
			String lStrDDOCode = String.valueOf(lLongDDOCode);
			String lStrEmpFname = lObjEmpData.getName();
			Character lCharGender = lObjEmpData.getGender();
			Date lDateBirthDate = lObjEmpData.getDob();
			lObjEmpData.getDesignation();
			lObjEmpData.getParentDept();
			String lStrBirthDate = lObjDateFormat.format(lDateBirthDate);

			// 1st digit based on state or Zonal
			DCPSID = DCPSID + "1";

			DCPSID = DCPSID + lStrDDOCode;

			lStrEmpFname = lStrEmpFname.replace('.', ' ');
			lStrEmpFname = lStrEmpFname.replaceAll("\\s+", " ");

			String Names[] = lStrEmpFname.split(" ");
			String Fname = null;
			String Mname = null;
			String Lname = null;

			if (Names.length >= 3) {
				Fname = Names[0].substring(0, 1);
				Mname = Names[1].substring(0, 1);
				Lname = Names[2].substring(0, 1);

			}

			else if (Names.length == 2) {
				Fname = Names[0].substring(0, 1);
				Mname = Names[1].substring(0, 1);
				//Lname = ".";
				if(Names[1].length() > 1)
				Lname = Names[1].substring(1, 2);
				else 
				{
				Lname = Names[0].substring(Names[0].length()-1);				
				}

			}

			else if (Names.length == 1) {
				Fname = Names[0].substring(0, 1);
				//Mname = ".";
				//Lname = ".";
				
				Mname = Names[0].substring(1,2);
				Lname = Names[0].substring(2,3);
			}

			DCPSID = DCPSID + Fname + Mname + Lname;
			
			if (lCharGender == 'M') {
				DCPSID = DCPSID + "M";
			}
			if (lCharGender == 'F') {
				DCPSID = DCPSID + "F";
			}
			if (lCharGender == 'T') {
				DCPSID = DCPSID + "T";
			}

			DCPSID = DCPSID + lStrBirthDate.substring(8, 10);

			Long count = lObjNewRegTreasuryDAO.getCountOfEmpOfSameName(DCPSID);

			count = count + 1;
			String countForNewEmployee = "";

			if (String.valueOf(count).length() == 1) {
				countForNewEmployee = "0" + String.valueOf(count);
			} else {
				countForNewEmployee = String.valueOf(count);
			}

			DCPSID = DCPSID + countForNewEmployee;

			Character LastDigit = getLastDigit(DCPSID);

			DCPSID = DCPSID + LastDigit;

			// Code for DCPS ID Generation

		} catch (Exception ex) {
			gLogger.error(" Error generating DCPS Id is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			throw ex;
			//ex.printStackTrace();

		}

		return DCPSID;

	}

	public static Character getLastDigit(String lStrDCPSId) {

		Character LastDigit = null;
		Map<String, Integer> MappingData = getMappingData();

		char[] lArrStrDcpsId = lStrDCPSId.toCharArray();

		Integer[] lArrIntMultiplication = {1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5};

		Integer lIntTotal = 0;

		for (Integer index = 0; index < 19; index++) {
			String lStrMappedKey = Character.toString(lArrStrDcpsId[index]);
			Integer lIntMap = MappingData.get(lStrMappedKey);
			lIntTotal = lIntTotal + (lIntMap * lArrIntMultiplication[index]);
		}

		Integer lIntLastDigit = 26 - (lIntTotal % 26);
		LastDigit = (char) (64 + lIntLastDigit);

		return LastDigit;
	}

	public static Map getMappingData() {

		Map<String, Integer> MappingData = new HashMap<String, Integer>();

		MappingData.put("0", 0);
		MappingData.put("1", 1);
		MappingData.put("2", 2);
		MappingData.put("3", 3);
		MappingData.put("4", 4);
		MappingData.put("5", 5);
		MappingData.put("6", 6);
		MappingData.put("7", 7);
		MappingData.put("8", 8);
		MappingData.put("9", 9);
		MappingData.put("A", 10);
		MappingData.put("B", 11);
		MappingData.put("C", 12);
		MappingData.put("D", 13);
		MappingData.put("E", 14);
		MappingData.put("F", 15);
		MappingData.put("G", 16);
		MappingData.put("H", 17);
		MappingData.put("I", 18);
		MappingData.put("J", 19);
		MappingData.put("K", 20);
		MappingData.put("L", 21);
		MappingData.put("M", 22);
		MappingData.put("N", 23);
		MappingData.put("O", 24);
		MappingData.put("P", 25);
		MappingData.put("Q", 26);
		MappingData.put("R", 27);
		MappingData.put("S", 28);
		MappingData.put("T", 29);
		MappingData.put("U", 30);
		MappingData.put("V", 31);
		MappingData.put("W", 32);
		MappingData.put("X", 33);
		MappingData.put("Y", 34);
		MappingData.put("Z", 35);
		MappingData.put(".", 36);

		return MappingData;
	}

	public ResultObject rejectRequest(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			setSessionInfo(objectArgs);

			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());
			String strPKValue = StringUtility.getParameter("Emp_Id", request).toString().trim();

			String[] strArrPKValue = strPKValue.split("~");

			String toPost = lObjNewRegTreasuryDAO.getDDOAsstPostIdForEmpId(strArrPKValue[0]);
			String toLevel = gObjRsrcBndle.getString("DCPS.DDOASST");

			objectArgs.put("toPost", toPost);
			objectArgs.put("toPostId", toPost);
			objectArgs.put("toLevel", toLevel);

			objectArgs.put("jobTitle", gObjRsrcBndle.getString("DCPS.RegistrationForm"));
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.RegistrationFormID")));
			
			String strRemarksForRejection = StringUtility.getParameter("remarks", request).toString().trim();

			String[] strArrRemarksForRejection = strRemarksForRejection.split("~");

			for (Integer index = 0; index < strArrPKValue.length; index++) {
				objectArgs.put("Pkvalue", strArrPKValue[index]);

				WorkFlowDelegate.forward(objectArgs);

				// Update the Registration form status to -1 suggesting it is
				// rejected

				Long lLongPKValue = Long.parseLong(strArrPKValue[index]);
				MstEmp lObjDcpsEmpMst = (MstEmp) lObjNewRegTreasuryDAO.read(lLongPKValue);

				// Set the value in Read VO
				lObjDcpsEmpMst.setRegStatus(-1L);
				lObjDcpsEmpMst.setPhyRcvdFormStatus(null);
				lObjDcpsEmpMst.setPhyRcvdDate(null);

				String lStrRejectionFrom = StringUtility.getParameter("rejectionFrom", request);

				if (lStrRejectionFrom.equals("ATO")) {
					if(!"".equals(strArrRemarksForRejection[index].trim()))
					{
						lObjDcpsEmpMst.setSentBackRemarks(strArrRemarksForRejection[index].trim());
					}
					else
					{
						lObjDcpsEmpMst.setSentBackRemarks("Physical Form Not Received");
					}
				}
				if (lStrRejectionFrom.equals("TO")) {
					lObjDcpsEmpMst.setSentBackRemarks("Form Rejected By Treasury Officer");
				}

				lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
				lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
				lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
				lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);
				lObjDcpsEmpMst.setFormStatus(0L);

			}

			objectArgs.put("ajaxKey", "Success");
			resObj.setViewName("ajaxData");
			resObj.setResultValue(objectArgs);
		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			return resObj;
		}
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#loadEmpDeputation(java.util.Map)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.TreasuryService#dcpsEmpDeputation(java.util.Map)
	 */

	private List getHierarchyUsers(Map inputMap) {

		List UserList = null;

		try {
			setSessionInfo(inputMap);

			Integer llFromLevelId = 0;
			UserList = new ArrayList<String>();
			// Get the Subject Name
			String subjectName = gObjRsrcBndle.getString("DCPS.RegistrationForm");

			// Get the Hierarchy Id
			Long lLngHierRefId = WorkFlowHelper.getHierarchyByPostIDAndDescription(gStrPostId, subjectName, inputMap);

			// Get the From level Id
			llFromLevelId = WorkFlowHelper.getLevelFromPostMpg(gStrPostId, lLngHierRefId, inputMap);

			// Get the List of Post ID of the users at the next Level
			List rsltList = WorkFlowHelper.getUpperPost(gStrPostId, lLngHierRefId, llFromLevelId, inputMap);

			Object[] lObjNextPost = null;

			for (Integer lInt = 0; lInt < rsltList.size(); lInt++) {

				lObjNextPost = (Object[]) rsltList.get(lInt);

				if (!(lObjNextPost.equals(null))) {
					UserList.add(lObjNextPost[0].toString());
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error while getting hierarchy is : " + e, e);
		}
		return UserList;
	}

	public ResultObject loadAllSavedCases(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		List listSavedRequests = null;
		List finalList = new ArrayList();
		Integer lIntCriteria;
		Integer lIntDaysDifference = null;

		try {
			setSessionInfo(inputMap);

			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());

			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			lIntCriteria = Integer.parseInt(StringUtility.getParameter("Criteria", request).trim());

			listSavedRequests = lObjNewRegTreasuryDAO.getAllSavedRequests(lIntCriteria, gStrPostId);

			List UserList = null;

			for (Iterator iterator = listSavedRequests.iterator(); iterator.hasNext();) {

				Integer lInt = 0;
				Object[] objectArr = (Object[]) iterator.next();
				Object[] newList = new Object[objectArr.length + 1];
				for (lInt = 0; lInt < objectArr.length; lInt++) {
					newList[lInt] = objectArr[lInt];
				}
				lIntDaysDifference = daysBetween(lObjSimpleDateFormat.parse(newList[12].toString()), gDtCurDate);
				newList[lInt] = lIntDaysDifference;

				finalList.add(newList);
			}

			if (lIntCriteria != 4) {
				UserList = getHierarchyUsers(inputMap);
			}

			inputMap.put("UserList", UserList);
			inputMap.put("Criteria", lIntCriteria);
			inputMap.put("CaseList", finalList);

			resObj.setViewName("DCPSCompletedForms");
			resObj.setResultValue(inputMap);

		} catch (Exception ex) {
			//ex.printStackTrace();
			gLogger.error(" Error showing approved form is : " + ex, ex);
		}

		return resObj;
	}

	public ResultObject rejectRequestForPhyFormNotRcvd(Map objectArgs) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List empIdsForPhysicalFormsNotReceived = null;
		Long dcpsEmpId = null;

		try {

			setSessionInfo(objectArgs);

			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());
			empIdsForPhysicalFormsNotReceived = (List) objectArgs.get("empIdsForPhysicalFormsNotReceived");

			objectArgs.put("FromPostId", gStrPostId);
			objectArgs.put("SendNotification", "Physical form not received");
			objectArgs.put("jobTitle", gObjRsrcBndle.getString("DCPS.RegistrationForm"));
			objectArgs.put("Docid", Long.parseLong(gObjRsrcBndle.getString("DCPS.RegistrationFormID")));

			for (Integer index = 0; index < empIdsForPhysicalFormsNotReceived.size(); index++) {

				dcpsEmpId = Long.valueOf(empIdsForPhysicalFormsNotReceived.get(index).toString());
				objectArgs.put("Pkvalue", dcpsEmpId);
				WorkFlowDelegate.returnDoc(objectArgs);

				MstEmp lObjDcpsEmpMst = (MstEmp) lObjNewRegTreasuryDAO.read(dcpsEmpId);

				// Set the value in Read VO
				lObjDcpsEmpMst.setRegStatus(0L);
				lObjDcpsEmpMst.setRegStatusUpdtdDate(gDtCurrDt);
				lObjDcpsEmpMst.setApprovalByDDODate(null);
				lObjDcpsEmpMst.setUpdatedUserId(gLngUserId);
				lObjDcpsEmpMst.setUpdatedPostId(gLngPostId);
				lObjDcpsEmpMst.setUpdatedDate(gDtCurrDt);
				lObjDcpsEmpMst.setSentBackRemarks("Physical Form Not Received");
				lObjDcpsEmpMst.setFormStatus(1L); // 1 since sent to DDO
				lObjDcpsEmpMst.setPhyRcvdFormStatus(null);
				lObjDcpsEmpMst.setPhyRcvdDate(null);
				lObjNewRegTreasuryDAO.update(lObjDcpsEmpMst);
			}

			objectArgs.put("ajaxKey", "Success");
			resObj.setViewName("ajaxData");
			resObj.setResultValue(objectArgs);
		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			//ex.printStackTrace();
			return resObj;
		}
		return resObj;
	}

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
	private StringBuilder getResponseXMLDoc(boolean flag, String lStrDcpsId, long empID,boolean lBlBillGroupFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("  </Flag>");
		lStrBldXML.append("  <DcpsId>");
		lStrBldXML.append(lStrDcpsId);
		lStrBldXML.append("  </DcpsId>");
		lStrBldXML.append("  <EMPID>");
		lStrBldXML.append(empID);
		lStrBldXML.append("  </EMPID>");
		lStrBldXML.append("  <BillGroupFlag>");
		lStrBldXML.append(lBlBillGroupFlag);
		lStrBldXML.append("  </BillGroupFlag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
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
		dBetween = dBetween - 1;
		return dBetween;
	}

	public ResultObject loadNewPensionAccList(Map<String, Object> inputMap) {

		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try {

			// lStrUserType = StringUtility.getParameter("UserType", request);
			List<ComboValuesVO> lLstTreasuries = new ArrayList<ComboValuesVO>();

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			lLstTreasuries = lObjDcpsCommonDAO.getAllTreasuries();

			inputMap.put("TreasuryList", lLstTreasuries);
			// inputMap.put("UserType", lStrUserType);
			resObj.setViewName("newPensionAcListReport");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		return resObj;
	}
	
	public ResultObject getDDOsForTreasuryInclAll(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;

		try {

			setSessionInfo(inputMap);

			String lStrTreasuryCode = StringUtility.getParameter(
					"cmbTreasuryCode", request).trim();

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			
			finalList = lObjDcpsCommonDAO.getAllDDOInclAll(lStrTreasuryCode);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
		} catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return objRes;
	}
	//NPS
	public String generatePPAN(Map inputMap, Long lLngEmpId) throws Exception{

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		MstEmp lObjEmpData = null;
		String PPAN = "";

		try {
			setSessionInfo(inputMap);

			NewRegTreasuryDAO lObjNewRegTreasuryDAO = new NewRegTreasuryDAOImpl(MstEmp.class, serv.getSessionFactory());

			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			lObjEmpData = new MstEmp();
			lObjEmpData = (MstEmp) lObjNewRegTreasuryDAO.read(lLngEmpId);

			// Code for PPAN Generation

			String lLongDDOCode = lObjEmpData.getDdoCode();
			String lStrDDOCode = String.valueOf(lLongDDOCode);
			String lStrEmpFname = lObjEmpData.getName();
			Character lCharGender = lObjEmpData.getGender();
			Date lDateBirthDate = lObjEmpData.getDob();
			lObjEmpData.getDesignation();
			lObjEmpData.getParentDept();
			String lStrBirthDate = lObjDateFormat.format(lDateBirthDate);

			
			

			PPAN = PPAN + lStrDDOCode;

			lStrEmpFname = lStrEmpFname.replace('.', ' ');
			lStrEmpFname = lStrEmpFname.replaceAll("\\s+", " ");

			String Names[] = lStrEmpFname.split(" ");
			String Fname = null;
			String Mname = null;
			String Lname = null;

			if (Names.length >= 3) {
				Fname = Names[0].substring(0, 1);
				Mname = Names[1].substring(0, 1);
				Lname = Names[2].substring(0, 1);

			}

			else if (Names.length == 2) {
				Fname = Names[0].substring(0, 1);
				Mname = Names[1].substring(0, 1);
				//Lname = ".";
				if(Names[1].length() > 1)
				Lname = Names[1].substring(1, 2);
				else 
				{
				Lname = Names[0].substring(Names[0].length()-1);				
				}

			}

			else if (Names.length == 1) {
				Fname = Names[0].substring(0, 1);
				//Mname = ".";
				//Lname = ".";
				
				Mname = Names[0].substring(1,2);
				Lname = Names[0].substring(2,3);
			}

			PPAN = PPAN + Fname + Mname + Lname;
			
			if (lCharGender == 'M') {
				PPAN = PPAN + "M";
			}
			if (lCharGender == 'F') {
				PPAN = PPAN + "F";
			}
			if (lCharGender == 'T') {
				PPAN = PPAN + "T";
			}

		

			Long count = lObjNewRegTreasuryDAO.getCountOfEmpOfSameNameForNps(PPAN);

			count = count + 1;
			String countForNewEmployee = "";

			if (String.valueOf(count).length() == 1) {
				countForNewEmployee = "0" + String.valueOf(count);
			} else {
				countForNewEmployee = String.valueOf(count);
			}

			PPAN = PPAN + countForNewEmployee;

		

	

			// Code for PPAN Generation for NPS

		} catch (Exception ex) {
			gLogger.error(" Error generating PPAN Id is : " + ex, ex);
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			throw ex;
			//ex.printStackTrace();

		}

		return PPAN;

	}
}