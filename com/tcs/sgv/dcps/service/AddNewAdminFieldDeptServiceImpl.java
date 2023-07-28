package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.CmnLanguageMstDao;
import com.tcs.sgv.common.dao.CmnLanguageMstDaoImpl;
import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.AddNewAdminFieldDeptDAO;
import com.tcs.sgv.dcps.dao.AddNewAdminFieldDeptDAOImpl;
import com.tcs.sgv.dcps.valueobject.AdmDept;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Oct 30, 2012
 */

public class AddNewAdminFieldDeptServiceImpl extends ServiceImpl implements AddNewAdminFieldDeptService
{
	private final Log gLogger = LogFactory.getLog(getClass());

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is: " + e, e);
		}
	}
	
	public ResultObject loadAdminDeptForm(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		
		try{
			AddNewAdminFieldDeptDAO lObjAddNewAdminFieldDept = new AddNewAdminFieldDeptDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			List lLstAdminDept = lObjAddNewAdminFieldDept.getAllAdminData();
			
			inputMap.put("AdminDeptList", lLstAdminDept);
			resObj.setResultValue(inputMap);
			resObj.setViewName("AddNewAdminDept");
		}catch(Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadAdminDeptForm");
		}
		return resObj;
	}
	
	public ResultObject saveNewAdminDepartment(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		Long lLngLocId = null;
		Long lLngAdmDeptCd = null;
		Long lLngDeptId = 100001l;
		Long lLngParentLocId = -1l;
		CmnLocationMst lObjCmnLocationMst = null;
		AdmDept lObjAdmDept = null;
		
		try{
			AddNewAdminFieldDeptDAO lObjAddNewAdminFieldDept = new AddNewAdminFieldDeptDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			AddNewAdminFieldDeptDAO lObjAddNewAdminFieldDeptAdm = new AddNewAdminFieldDeptDAOImpl(AdmDept.class,serv.getSessionFactory());
			
			CmnLanguageMstDao lObjCmnLanguageDao = new CmnLanguageMstDaoImpl(CmnLanguageMst.class,serv.getSessionFactory());
			CmnLanguageMst lObjCmnLanguageMst = lObjCmnLanguageDao.read(1l);
			
			CmnLookupMstDAO lObjCmnLookUpMstDao = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
			CmnLookupMst lObjCmnLookupMst = lObjCmnLookUpMstDao.read(1l);
			
			String lStrAdminDeptName = StringUtility.getParameter("adminDeptName", request).trim();
			String lStrAlphaCode = StringUtility.getParameter("alphaCode", request).trim();
			String lStrAdminDeptShortName = StringUtility.getParameter("adminDeptShortName", request).trim();
			String lStrType = StringUtility.getParameter("type", request).trim();
			
			
			
			if(lStrType.equals("insert"))
			{
				//Add Entry in CmnLocationmst
				lLngLocId = lObjAddNewAdminFieldDept.getMaxLocIdAdmin();
				
				lObjCmnLocationMst = new CmnLocationMst();
				lObjCmnLocationMst.setLocId(lLngLocId);
				lObjCmnLocationMst.setLocName(lStrAdminDeptName);
				lObjCmnLocationMst.setLocShortName(lStrAdminDeptShortName);
				lObjCmnLocationMst.setCmnLanguageMst(lObjCmnLanguageMst);
				lObjCmnLocationMst.setDepartmentId(lLngDeptId);
				lObjCmnLocationMst.setParentLocId(lLngParentLocId);
				lObjCmnLocationMst.setLocDistrictId(2l);
				lObjCmnLocationMst.setLocStateId(15l);
				lObjCmnLocationMst.setLocPin("380001");
				lObjCmnLocationMst.setCmnLookupMst(lObjCmnLookupMst);
				lObjCmnLocationMst.setStartDate(DBUtility.getCurrentDateFromDB());
				lObjCmnLocationMst.setActivateFlag(1l);
				lObjCmnLocationMst.setCreatedBy(1l);
				lObjCmnLocationMst.setCreatedByPost(1l);
				lObjCmnLocationMst.setCreatedDate(DBUtility.getCurrentDateFromDB());
				lObjCmnLocationMst.setLocationCode(lLngLocId.toString());
				lObjAddNewAdminFieldDept.create(lObjCmnLocationMst);
				
				
				//Add Entry in AdmDept
				lLngAdmDeptCd = lObjAddNewAdminFieldDeptAdm.getMaxAdminCode();
				
				lObjAdmDept = new AdmDept();
				lObjAdmDept.setAdmDeptCd(lLngAdmDeptCd);
				lObjAdmDept.setAdmBdgtCd(lStrAlphaCode);
				lObjAdmDept.setLocId(lLngLocId);
				lObjAddNewAdminFieldDeptAdm.create(lObjAdmDept);
			}else if(lStrType.equals("update")){
				
				Long lLngLocationId = Long.parseLong(StringUtility.getParameter("locId", request).trim());
				lObjCmnLocationMst = (CmnLocationMst) lObjAddNewAdminFieldDept.read(lLngLocationId);
				lObjCmnLocationMst.setLocName(lStrAdminDeptName);
				lObjCmnLocationMst.setLocShortName(lStrAdminDeptShortName);
				lObjAddNewAdminFieldDept.update(lObjCmnLocationMst);
				
				lLngAdmDeptCd = lObjAddNewAdminFieldDept.getAdminDeptCode(lLngLocationId);
				lObjAdmDept = (AdmDept) lObjAddNewAdminFieldDeptAdm.read(lLngAdmDeptCd);
				lObjAdmDept.setAdmBdgtCd(lStrAlphaCode);
				lObjAddNewAdminFieldDeptAdm.update(lObjAdmDept);
			}
			
			
			String lSBStatus = getResponseXMLDoc("Save").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);			
		}catch(Exception e) {
			e.printStackTrace();
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in saveNewAdminDepartment");
		}
		return resObj;
	}
	
	public ResultObject editAdminDeptData(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		
		Object []lObj = null;
		
		try{
			AddNewAdminFieldDeptDAO lObjAddNewAdminFieldDept = new AddNewAdminFieldDeptDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			Long lLngLocId = Long.parseLong(StringUtility.getParameter("locId", request));
			
			List lLstAdminData = lObjAddNewAdminFieldDept.getAdminData(lLngLocId);
			if(lLstAdminData != null && lLstAdminData.size() > 0){
				lObj = (Object[]) lLstAdminData.get(0);
			}
			
			List lLstAdminDept = lObjAddNewAdminFieldDept.getAllAdminData();
			
			inputMap.put("AdminDeptList", lLstAdminDept);
			inputMap.put("adminDeptName", lObj[0]);
			inputMap.put("alphaCode", lObj[2]);
			inputMap.put("deptShortName", lObj[1]);
			inputMap.put("LocationId", lLngLocId);
			resObj.setResultValue(inputMap);
			resObj.setViewName("AddNewAdminDept");
		}catch(Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in editAdminDeptData");
		}
		return resObj;
	}
	
	public ResultObject loadFieldDeptForm(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		
		try{
			AddNewAdminFieldDeptDAO lObjAddNewAdminFieldDept = new AddNewAdminFieldDeptDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			List lLstAdminDept = lObjAddNewAdminFieldDept.getAllAdminDepartment();
			
			inputMap.put("AdminDept", lLstAdminDept);
			resObj.setResultValue(inputMap);
			resObj.setViewName("AddNewFieldDept");
		}catch(Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadFieldDeptForm");
		}
		return resObj;
	}
	
	public ResultObject getFieldDeptForAdminDept(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		
		try{
			AddNewAdminFieldDeptDAO lObjAddNewAdminFieldDept = new AddNewAdminFieldDeptDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			
			Long lLngAdminDeptId = Long.parseLong(StringUtility.getParameter("adminDept", request).trim());
			
			List lLstAdminDept = lObjAddNewAdminFieldDept.getAllAdminDepartment();
			List lLstFieldDept = lObjAddNewAdminFieldDept.getAllFieldData(lLngAdminDeptId);
			
			inputMap.put("AdminDept", lLstAdminDept);
			inputMap.put("adminId", lLngAdminDeptId);
			inputMap.put("FieldDeptList", lLstFieldDept);
			resObj.setResultValue(inputMap);
			resObj.setViewName("AddNewFieldDept");
		}catch(Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getFieldDeptForAdminDept");
		}
		return resObj;
	}
	
	public ResultObject editFieldDeptData(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		
		Object []lObj = null;
		
		try{
			AddNewAdminFieldDeptDAO lObjAddNewAdminFieldDept = new AddNewAdminFieldDeptDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			Long lLngAdminDeptId = Long.parseLong(StringUtility.getParameter("adminDept", request).trim());
			Long lLngFieldId = Long.parseLong(StringUtility.getParameter("fieldDept", request).trim());
			
			List lLstAdminDept = lObjAddNewAdminFieldDept.getAllAdminDepartment();
			List lLstFieldDept = lObjAddNewAdminFieldDept.getAllFieldData(lLngAdminDeptId);
			
			List lLstFieldData = lObjAddNewAdminFieldDept.getFieldData(lLngFieldId);
			if(lLstFieldData != null && lLstFieldData.size() > 0){
				lObj = (Object[]) lLstFieldData.get(0);
			}
			
			inputMap.put("adminId", lLngAdminDeptId);
			inputMap.put("AdminDept", lLstAdminDept);			
			inputMap.put("FieldDeptList", lLstFieldDept);
			inputMap.put("fieldDeptName", lObj[0]);			
			inputMap.put("deptShortName", lObj[1]);
			inputMap.put("fieldDeptCode", lLngFieldId);
			resObj.setResultValue(inputMap);
			resObj.setViewName("AddNewFieldDept");
		}catch(Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in editFieldDeptData");
		}
		return resObj;
	}
	
	public ResultObject saveNewFieldDepartment(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "Fail");
		Long lLngLocId = null;
		Long lLngAdmDeptCd = null;
		Long lLngDeptId = 100011l;		
		CmnLocationMst lObjCmnLocationMst = null;
		AdmDept lObjAdmDept = null;
		
		try{
			AddNewAdminFieldDeptDAO lObjAddNewAdminFieldDept = new AddNewAdminFieldDeptDAOImpl(CmnLocationMst.class,serv.getSessionFactory());
			
			CmnLanguageMstDao lObjCmnLanguageDao = new CmnLanguageMstDaoImpl(CmnLanguageMst.class,serv.getSessionFactory());
			CmnLanguageMst lObjCmnLanguageMst = lObjCmnLanguageDao.read(1l);
			
			CmnLookupMstDAO lObjCmnLookUpMstDao = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
			CmnLookupMst lObjCmnLookupMst = lObjCmnLookUpMstDao.read(100202l);
			
			String lStrFieldDeptName = StringUtility.getParameter("fieldDeptName", request).trim();			
			String lStrFieldDeptShortName = StringUtility.getParameter("fieldDeptShortName", request).trim();
			String lStrType = StringUtility.getParameter("type", request).trim();			
			Long lLngLocationAdmin = Long.parseLong(StringUtility.getParameter("adminDept", request).trim());
			
			
			if(lStrType.equals("insert"))
			{
				//Add Entry in CmnLocationmst
				lLngLocId = lObjAddNewAdminFieldDept.getMaxLocIdField();
				
				lObjCmnLocationMst = new CmnLocationMst();
				lObjCmnLocationMst.setLocId(lLngLocId);
				lObjCmnLocationMst.setLocName(lStrFieldDeptName);
				lObjCmnLocationMst.setLocShortName(lStrFieldDeptShortName);
				lObjCmnLocationMst.setCmnLanguageMst(lObjCmnLanguageMst);
				lObjCmnLocationMst.setDepartmentId(lLngDeptId);
				lObjCmnLocationMst.setParentLocId(lLngLocationAdmin);
				lObjCmnLocationMst.setLocDistrictId(2l);
				lObjCmnLocationMst.setLocStateId(15l);
				lObjCmnLocationMst.setLocPin("380001");
				lObjCmnLocationMst.setCmnLookupMst(lObjCmnLookupMst);
				lObjCmnLocationMst.setStartDate(DBUtility.getCurrentDateFromDB());
				lObjCmnLocationMst.setActivateFlag(1l);
				lObjCmnLocationMst.setCreatedBy(1l);
				lObjCmnLocationMst.setCreatedByPost(1l);
				lObjCmnLocationMst.setCreatedDate(DBUtility.getCurrentDateFromDB());
				lObjCmnLocationMst.setLocationCode(lLngLocId.toString());
				lObjAddNewAdminFieldDept.create(lObjCmnLocationMst);
			}else if(lStrType.equals("update")){
				Long lLngLocationField = Long.parseLong(StringUtility.getParameter("fieldDept", request).trim());
				lObjCmnLocationMst = (CmnLocationMst) lObjAddNewAdminFieldDept.read(lLngLocationField);
				lObjCmnLocationMst.setLocName(lStrFieldDeptName);
				lObjCmnLocationMst.setLocShortName(lStrFieldDeptShortName);
				lObjAddNewAdminFieldDept.update(lObjCmnLocationMst);				
			}
			
			
			String lSBStatus = getResponseXMLDoc("Save").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);			
		}catch(Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in saveNewAdminDepartment");
		}
		return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrResData) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DATA>");
		lStrBldXML.append(lStrResData);
		lStrBldXML.append("</DATA>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
		
}
