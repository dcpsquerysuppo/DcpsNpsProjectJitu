/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	June 13, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
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

import com.tcs.sgv.common.dao.LocationDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.ChangeEmpDeptDAO;
import com.tcs.sgv.dcps.dao.ChangeEmpDeptDAOImpl;
import com.tcs.sgv.dcps.dao.DdoInfoDAO;
import com.tcs.sgv.dcps.dao.DdoInfoDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 June 13, 2011
 */
public class ChangeEmpDeptServiceImpl extends ServiceImpl implements
		ChangeEmpDeptService {
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

	private StringBuilder getResponseXMLDoc(boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject loadChangeEmpDept(Map<String, Object> inputMap)
			throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		try {
			setSessionInfo(inputMap);
			List listParentDept = IFMSCommonServiceImpl.getLookupValues(
					"ParentDeptList", SessionHelper.getLangId(inputMap),
					inputMap);
			inputMap.put("listParentDept", listParentDept);

			gLogger.info("Load Sucessful");

		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSChangeEmpDept");
		return resObj;
	}

	public ResultObject saveChangeEmpDept(Map<String, Object> inputMap)
			throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBFlag = false;

		try {
			setSessionInfo(inputMap);
			MstEmp lObjMstEmp = null;
			ChangeEmpDeptDAO objPostEmpContriDAO = new ChangeEmpDeptDAOImpl(
					MstEmp.class, serv.getSessionFactory());
			String strEmpId = StringUtility.getParameter("empId", request);
			String strNewParentDept = StringUtility.getParameter("deptName",
					request);
			Long lngEmpDCPSId = Long.parseLong(strEmpId);
			// NewRegDdoDAO lObjNewRegDdoDAO = new
			// NewRegDdoDAOImpl(MstEmp.class,serv.getSessionFactory());
			lObjMstEmp = (MstEmp) objPostEmpContriDAO.read(lngEmpDCPSId);
			lObjMstEmp.setParentDept(strNewParentDept);
			lObjMstEmp.setUpdatedUserId(gLngUserId);
			lObjMstEmp.setUpdatedPostId(gLngPostId);
			lObjMstEmp.setUpdatedDate(gDtCurDate);

			objPostEmpContriDAO.update(lObjMstEmp);

			lBFlag = true;
			String lStrUserType = StringUtility.getParameter("UserType",
					request);
			inputMap.put("UserType", lStrUserType);

		} catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDigiSig " + e, e);
		}
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject getEmpDetails(Map<String, Object> inputMap)
			throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS);

		List finalList = null;
		String strTreasury = "";
		Boolean dcpsIdValidOrNot = false;
		String lStrDdoCode = "" ;
		try {

			setSessionInfo(inputMap);
			ChangeEmpDeptDAO lObjChangeEmpDeptDAO = new ChangeEmpDeptDAOImpl(
					MstEmp.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			String strDcpsId = StringUtility.getParameter("dcpsId", request);

			finalList = lObjChangeEmpDeptDAO.getEmpDetails(strDcpsId);
			if(finalList.size()!=0 && finalList!=null)
			{
				Object obj[] = (Object[]) finalList.get(0);
				lStrDdoCode = obj[2].toString();
				strTreasury = lObjDcpsCommonDAO.getTreasuryNameForDDO(lStrDdoCode);
				dcpsIdValidOrNot = true;
			}

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			ex.printStackTrace();
		}

		String lSBStatus = getResponseXMLDocForEmpData(finalList, strTreasury,dcpsIdValidOrNot)
				.toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;
	}

	private StringBuilder getResponseXMLDocForEmpData(List finalList,
			String treasuryCode,Boolean dcpsIdValidOrNot) {

		StringBuilder lStrBldXML = new StringBuilder();
		
		if(dcpsIdValidOrNot)
		{
			Object obj[] = (Object[]) finalList.get(0);
	
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<dcpsIdValidOrNot>");
			lStrBldXML.append(dcpsIdValidOrNot);
			lStrBldXML.append("</dcpsIdValidOrNot>");
			lStrBldXML.append("<empId>");
			if (obj[0] != null) {
				lStrBldXML.append(obj[0].toString());
			} else {
				lStrBldXML.append("");
			}
			lStrBldXML.append("</empId>");
			lStrBldXML.append("<name>");
			if (obj[0] != null) {
				lStrBldXML.append(obj[1].toString());
			} else {
				lStrBldXML.append("");
			}
			lStrBldXML.append("</name>");
			lStrBldXML.append("<ddoCode>");
			if (obj[1] != null) {
				lStrBldXML.append(obj[2].toString());
			} else {
				lStrBldXML.append("");
			}
			lStrBldXML.append("</ddoCode>");
			lStrBldXML.append("<parentDept>");
			if (obj[2] != null) {
				lStrBldXML.append(obj[3].toString());
			} else {
				lStrBldXML.append("");
			}
			lStrBldXML.append("</parentDept>");
			lStrBldXML.append("<treasuryCode>");
			if (obj[2] != null) {
				lStrBldXML.append(treasuryCode.replace("&", "&amp;"));
			} else {
				lStrBldXML.append("");
			}
			lStrBldXML.append("</treasuryCode>");
			lStrBldXML.append("</XMLDOC>");
		}
		else
		{
			lStrBldXML.append("<XMLDOC>");
			lStrBldXML.append("<dcpsIdValidOrNot>");
			lStrBldXML.append(dcpsIdValidOrNot);
			lStrBldXML.append("</dcpsIdValidOrNot>");
			lStrBldXML.append("</XMLDOC>");
		}

		return lStrBldXML;
	}
	
	public ResultObject loadChangeParentDept (Map<String, Object> inputMap)
	throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		List lLstTreasury = null;
		String lStrViewPageFor = null;
		String lStrTreasuryCode = null;
		String lStrDdoCode = null;
		List empList = null;
		
		try {
			
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			ChangeEmpDeptDAO changeDeptDAO = new ChangeEmpDeptDAOImpl(null, serv.getSessionFactory());
			lLstTreasury = lObjDcpsCommonDAO.getAllTreasuries();
			inputMap.put("TreasuryList", lLstTreasury);
			
			lStrTreasuryCode = StringUtility.getParameter("treasuryCode", request);
			lStrDdoCode = StringUtility.getParameter("ddoCode", request);
			if(!"".equals(lStrTreasuryCode))
			{
				List lListDdoList = lObjDcpsCommonDAO.getAllDDOForTreasury(lStrTreasuryCode);
				inputMap.put("DdoList", lListDdoList);
			}
			inputMap.put("treasuryCode", lStrTreasuryCode);
			inputMap.put("ddoCode", lStrDdoCode);
			
			lStrViewPageFor = StringUtility.getParameter("viewPageFor", request);
			
			if(lStrViewPageFor.equals("DDO"))
			{
				DdoInfoDAO ddoInfoDAO = new DdoInfoDAOImpl(null, serv.getSessionFactory());
				OrgDdoMst lObjOrgDdoMst = ddoInfoDAO.getDdoInformation(lStrDdoCode);
				LocationDAOImpl lObjLocationDAO = new LocationDAOImpl(null, serv.getSessionFactory());
				
				if (lObjOrgDdoMst != null) {
					
					List lLstAdminDept = lObjDcpsCommonDAO.getAllDepartment(Long.parseLong(gObjRsrcBndle.getString("DCPS.DEPARTMENTID")), gLngLangId);
					inputMap.put("AdminDept", lLstAdminDept);
					
					if (lObjOrgDdoMst.getBankName() != null && !(lObjOrgDdoMst.getBankName().equals(""))) {
						String lStrBankName = lObjDcpsCommonDAO.getBankNameForBankCode(lObjOrgDdoMst.getBankName());
						inputMap.put("bankName", lStrBankName);
					}
					
					if (lObjOrgDdoMst.getBranchName() != null && !(lObjOrgDdoMst.getBranchName().equals(""))) {
						String lStrBranchName = lObjDcpsCommonDAO.getBranchNameForBranchCode(lObjOrgDdoMst.getBranchName());
						inputMap.put("branchName", lStrBranchName);
					}

					String lStrAdminDept = lObjLocationDAO.getDeptNameByLocCode(lObjOrgDdoMst.getDeptLocCode(), gLngLangId.toString());
					String lStrFieldDept = lObjLocationDAO.getDeptNameByLocCode(lObjOrgDdoMst.getHodLocCode(), gLngLangId.toString());
					inputMap.put("AdminDeptDesc", lStrAdminDept);
					inputMap.put("FieldDeptDesc", lStrFieldDept);
					inputMap.put("DdoDetails", lObjOrgDdoMst);
				}

				inputMap.put("DDOChecked", "Y");
			}
			if(lStrViewPageFor.equals("EMP"))
			{
				empList = changeDeptDAO.getAllEmpsUnderDDO(lStrDdoCode);
				inputMap.put("empList", empList);
				inputMap.put("EMPChecked", "Y");
			}
			if(lStrViewPageFor.equals("DDOCODE"))
			{
				empList = changeDeptDAO.getAllEmpsUnderDDO(lStrDdoCode);
				inputMap.put("empList", empList);
				inputMap.put("DDOCODEChecked", "Y");
			}
		
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}
		
			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSChangeParentDept");
			return resObj;
		}
	
	public ResultObject changeParentDeptOfDDO(Map<String, Object> inputMap) throws Exception {

			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
			Boolean lBFlag = false;
			
			try {
				
				setSessionInfo(inputMap);
				
				ChangeEmpDeptDAO changeDeptDAO = new ChangeEmpDeptDAOImpl(null, serv.getSessionFactory());
				
				String lStrDDOCode = StringUtility.getParameter("ddoCode", request);
				String lStrDeptLocCode = StringUtility.getParameter("newAdminDept",request);
				String lStrHODLocCode = StringUtility.getParameter("newFieldDept",request);
				
				if(!"".equals(lStrDDOCode) && !"".equals(lStrDeptLocCode) && !"".equals(lStrHODLocCode))
				{
					changeDeptDAO.updateParentDeptOfDDO(lStrDDOCode, lStrDeptLocCode, lStrHODLocCode);
					changeDeptDAO.updateParentDeptOfAllEmpsUnderTheDDO(lStrDDOCode, lStrHODLocCode);
				}
			
				lBFlag = true;
			
			} catch (Exception e) {
			
				e.printStackTrace();
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
			}
			
			String lSBStatus = getResponseXMLDoc(lBFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
					.toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;
		
		}
	
	public ResultObject changeDDOCode(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBFlag = false;
		
		try {
			
			setSessionInfo(inputMap);
			
			ChangeEmpDeptDAO changeDeptDAO = new ChangeEmpDeptDAOImpl(null, serv.getSessionFactory());
			
			String lStrDDOCode = StringUtility.getParameter("newDDOCode", request).trim();
			String lStrTreasuryCode = StringUtility.getParameter("newTreasuryCode",request);
			String lStrEmpIds = StringUtility.getParameter("empIds", request);
			
			String[] lStrArrdcpsEmpIds = lStrEmpIds.split("~");
			Long[] lLongArrdcpsEmpIds = new Long[lStrArrdcpsEmpIds.length];
			for (Integer lInt = 0; lInt < lStrArrdcpsEmpIds.length; lInt++) {
				lLongArrdcpsEmpIds[lInt] = Long.valueOf(lStrArrdcpsEmpIds[lInt]);
				changeDeptDAO.updateDDOCodeOfEmp(lStrDDOCode, lLongArrdcpsEmpIds[lInt]);
			}
		
			lBFlag = true;
		
		} catch (Exception e) {
		
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();
		
		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	
	}
	
	public ResultObject popUpEmpPFDChangeWndw (Map<String, Object> inputMap)
	throws Exception {
	
			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
			
			try {
				
				setSessionInfo(inputMap);
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
				ChangeEmpDeptDAO changeDeptDAO = new ChangeEmpDeptDAOImpl(null, serv.getSessionFactory());
				NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,serv.getSessionFactory());
				SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
				
				String lStrEmpId = StringUtility.getParameter("empId", request);
				Long lLongEmpId= Long.valueOf(lStrEmpId);
				
				MstEmp lObjMstEmp = (MstEmp) lObjNewRegDdoDAO.read(lLongEmpId);
				
				Date lDtcurDate = SessionHelper.getCurDate();
				inputMap.put("lDtCurDate", lObjDateFormat.format(lDtcurDate));
				
				String lStrTreasuryCode = StringUtility.getParameter("treasuryCode", request);
				String lStrDdoCode = StringUtility.getParameter("ddoCode", request);
				
				List lListFieldHODDept = changeDeptDAO.getFieldHODComboForEmp(lLongEmpId);
				List lListParentDept = changeDeptDAO.getParentDeptComboForEmp(lLongEmpId);
				
				List lLstAdminDept = lObjDcpsCommonDAO.getAllDepartment(Long.parseLong(gObjRsrcBndle.getString("DCPS.DEPARTMENTID")), gLngLangId);
				inputMap.put("lLstAdminDept", lLstAdminDept);
				inputMap.put("lListFieldHODDept", lListFieldHODDept);
				inputMap.put("lListParentDept", lListParentDept);
				inputMap.put("lObjMstEmp", lObjMstEmp);
				
				inputMap.put("lStrTreasuryCode", lStrTreasuryCode);
				inputMap.put("lStrDdoCode", lStrDdoCode);
				inputMap.put("lStrEmpId", lStrEmpId);
						
			
			} catch (Exception e) {
				gLogger.error("Error is;" + e, e);
				e.printStackTrace();
			}
			
				resObj.setResultValue(inputMap);
				resObj.setViewName("ChangeParentDeptEmp");
				return resObj;
		}
	
	public ResultObject changeParentDeptOfEmp(Map<String, Object> inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBFlag = false;
		
		try {
			
			setSessionInfo(inputMap);
			
			ChangeEmpDeptDAO changeDeptDAO = new ChangeEmpDeptDAOImpl(null, serv.getSessionFactory());
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,serv.getSessionFactory());
			
			String lStrEmpId = StringUtility.getParameter("empId", request);
			Long lLongEmpId = Long.valueOf(lStrEmpId);
			MstEmp lObjMstEmp = (MstEmp) lObjNewRegDdoDAO.read(lLongEmpId);
			
			String lStrDeptLocCode = StringUtility.getParameter("newAdminDept",request);
			String lStrHODLocCode = StringUtility.getParameter("newFieldDept",request);
			String lStrDOB = StringUtility.getParameter("txtBirthDate", request);
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date dtBirthDate = null;
			if (lStrDOB != null && !"".equals(lStrDOB.trim())) {
				dtBirthDate = simpleDateFormat.parse(lStrDOB);
			}
			String lStrUIDNo1 = StringUtility
					.getParameter("txtUIDNo1", request);
			String lStrUIDNo2 = StringUtility
					.getParameter("txtUIDNo2", request);
			String lStrUIDNo3 = StringUtility
					.getParameter("txtUIDNo3", request);
			
			String lStrUIDNo = lStrUIDNo1.concat(lStrUIDNo2.concat(lStrUIDNo3));
			
			if(!lStrUIDNo1.equals("") && !lStrUIDNo2.equals("") && !lStrUIDNo3.equals(""))
			{
				lObjMstEmp.setUIDNo(lStrUIDNo);
			}
			if(!lStrDOB.equals(""))
			{
				lObjMstEmp.setDob(dtBirthDate);
			}
			
			lObjNewRegDdoDAO.update(lObjMstEmp);
			
			if(!"".equals(lStrEmpId) && !"".equals(lStrHODLocCode))
			{
				changeDeptDAO.updateParentDeptOfEmp(lLongEmpId, lStrHODLocCode);
			}
		
			lBFlag = true;
		
		} catch (Exception e) {
		
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();
		
		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	
	}
	
	
}
