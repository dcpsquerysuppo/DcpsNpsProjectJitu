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
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAO;
import com.tcs.sgv.dcps.dao.DdoBillGroupDAOImpl;
import com.tcs.sgv.dcps.dao.SrkaMasterDAO;
import com.tcs.sgv.dcps.dao.SrkaMasterDAOImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;
import com.tcs.sgv.dcps.valueobject.MstDcpsOrganization;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Apr 8, 2011
 */
public class SrkaMasterServiceImpl extends ServiceImpl implements SrkaMasterService {

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
	 * com.tcs.sgv.dcps.service.SrkaMasterService#insertCadre(java.util.Map)
	 */
	public ResultObject insertCadre(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		SrkaMasterDAO lObjSrkaMasterDAO = null;
		Boolean lBlFlag = false;
		
		try {
			setSessionInfo(inputMap);
			lObjSrkaMasterDAO = new SrkaMasterDAOImpl(DcpsCadreMst.class, serv.getSessionFactory());
			DcpsCadreMst DcpsDcpsCadreMstVO = (DcpsCadreMst) inputMap.get("DcpsCadreMstVO");
			lObjSrkaMasterDAO.create(DcpsDcpsCadreMstVO);
			
			lBlFlag = true ;
			
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}
		
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setViewName("ajaxData");
		resObj.setResultValue(inputMap);
		
		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.SrkaMasterService#insertDesignation(java.util
	 * .Map)
	 */
	public ResultObject insertDesignation(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long lLngDesigId = null;
		MstDcpsDesignation lObjMstDcpsDesignationVO = new MstDcpsDesignation();
		Boolean lBlFlag = false;
		Long lOrgDesigId = null;

		try {
			setSessionInfo(inputMap);
			SrkaMasterDAO lObjSrkaMasterDAO = new SrkaMasterDAOImpl(MstDcpsDesignation.class, serv.getSessionFactory());
			String strTransMode = (String) inputMap.get("Mode");
			if (strTransMode.equalsIgnoreCase("Add")) {
				if (lObjMstDcpsDesignationVO != null) {
					
					OrgDesignationMst lObjDesignationMst = (OrgDesignationMst) inputMap.get("lObjOrgDesignationMst");
					
					if(lObjSrkaMasterDAO.checkDesigExistsInOrgDesigMstOrNot(lObjDesignationMst.getDsgnName()))
					{
						lOrgDesigId = lObjSrkaMasterDAO.getDesigIdForDesigName(lObjDesignationMst.getDsgnName());
					}
					else
					{
						lOrgDesigId = IFMSCommonServiceImpl.getNextSeqNum("org_designation_mst", inputMap);
						lObjDesignationMst.setDsgnId(lOrgDesigId);
						lObjDesignationMst.setDsgnCode(lOrgDesigId.toString());
						lObjSrkaMasterDAO.create(lObjDesignationMst);
					}
					
					lObjMstDcpsDesignationVO = (MstDcpsDesignation) inputMap.get("lObjMstDcpsDesignationVO");
					lLngDesigId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_designation", inputMap);
					lObjMstDcpsDesignationVO.setDesigId(lLngDesigId);
					lObjMstDcpsDesignationVO.setOrgDesignationId(lOrgDesigId);
					lObjSrkaMasterDAO = new SrkaMasterDAOImpl(MstDcpsDesignation.class, serv.getSessionFactory());
					lObjSrkaMasterDAO.create(lObjMstDcpsDesignationVO);
					
					
			// Inserting Designation at payroll side starts		
					
					inputMap.put("mstDcpsDesignation", lObjMstDcpsDesignationVO);
					
					ResultObject resObj = serv.executeService("insertPayrollDesignation", inputMap);

					if (resObj.getResultCode() == ErrorConstants.ERROR) {

						throw new Exception();
					}
			 
			//Inserting Designation at payroll side ends
					
					lBlFlag = true;
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		
		String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		objRes.setViewName("ajaxData");
		objRes.setResultValue(inputMap);
		
		return objRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.SrkaMasterService#insertOrganization(java.util
	 * .Map)
	 */
	public ResultObject insertOrganization(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Long lLngOrgId = null;
		SrkaMasterDAO lObjSrkaMasterDAO = null;
		MstDcpsOrganization lObjMstDcpsOrganizationVO = new MstDcpsOrganization();
		Boolean lBlFlag = false;

		try {
			
			setSessionInfo(inputMap);
			String strTransMode = (String) inputMap.get("Mode");
			if (strTransMode.equalsIgnoreCase("Add")) {
				if (lObjMstDcpsOrganizationVO != null) {
					lObjMstDcpsOrganizationVO = (MstDcpsOrganization) inputMap.get("lObjMstDcpsOrganizationVO");
					lLngOrgId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_orgnaization", inputMap);
					lObjMstDcpsOrganizationVO.setOrgId(lLngOrgId);
					lObjSrkaMasterDAO = new SrkaMasterDAOImpl(MstDcpsOrganization.class, serv.getSessionFactory());
					lObjSrkaMasterDAO.create(lObjMstDcpsOrganizationVO);
					lBlFlag = true ;
				}
			}

			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
					.toString();

			inputMap.put("ajaxKey", lStrResult);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.SrkaMasterService#loadCadreMaster(java.util.Map)
	 */
	public ResultObject loadCadreMaster(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		SrkaMasterDAO lObjSrkaMasterDAO = null;
		List cadreList = null;
		Long lLongCurrFieldDeptId = null;
		Long lLongMaxCadreInFieldDept = 0l;
		String cadreCode = null;

		try {

			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(DcpsCadreMst.class, serv.getSessionFactory());

			List FieldDeptList = lObjDcpsCommonDAO.getAllDepartment(Long.parseLong(gObjRsrcBndle.getString("DCPS.CurrentFieldDeptID")), gLngLangId);
			List GroupList = IFMSCommonServiceImpl.getLookupValues("GroupList", SessionHelper.getLangId(inputMap), inputMap);
			lObjSrkaMasterDAO = new SrkaMasterDAOImpl(DcpsCadreMst.class, serv.getSessionFactory());

			if (!StringUtility.getParameter("currFieldDeptId", request).equalsIgnoreCase("") && StringUtility.getParameter("currFieldDeptId", request) != null) {
				lLongCurrFieldDeptId = Long.valueOf(StringUtility.getParameter("currFieldDeptId", request));
				cadreList = lObjSrkaMasterDAO.getCadreList(lLongCurrFieldDeptId);
				lLongMaxCadreInFieldDept = lObjSrkaMasterDAO.getMaxCadreInFieldDept(lLongCurrFieldDeptId);
				lLongMaxCadreInFieldDept = lLongMaxCadreInFieldDept + 1l;
				cadreCode = lLongMaxCadreInFieldDept.toString();
				inputMap.put("CurrFieldDeptId", lLongCurrFieldDeptId);
				inputMap.put("cadreCode", cadreCode);
				inputMap.put("cadreList", cadreList);
			}

			inputMap.put("Group", GroupList);
			inputMap.put("FieldDeptList", FieldDeptList);

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("DCPSCadreInfo");

		return resObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.SrkaMasterService#loadDesigInfo(java.util.Map)
	 */
	public ResultObject loadDesigInfo(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List FieldDeptList = null;
		List<CmnLookupMst> lLstPayCmsn = null;
		List lLstDesigDtls = null;
		Long lLongCurrFieldDeptId = null;
		Long lLongMaxDesigInFieldDept = null;
		String designationCode = null;
		List CadreList = null;

		try {

			setSessionInfo(inputMap);

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(DcpsCadreMst.class, serv.getSessionFactory());
			SrkaMasterDAO lObjSrkaMasterDAO = null;
			lObjSrkaMasterDAO = new SrkaMasterDAOImpl(DCPSDesignationServiceImpl.class, serv.getSessionFactory());

			FieldDeptList = lObjDcpsCommonDAO.getAllDepartment(Long.parseLong(gObjRsrcBndle.getString("DCPS.CurrentFieldDeptID")), gLngLangId);
			lLstPayCmsn = IFMSCommonServiceImpl.getLookupValues("PayCommissionDCPS", SessionHelper.getLangId(inputMap), inputMap);

			if (!StringUtility.getParameter("currFieldDeptId", request).equalsIgnoreCase("") && StringUtility.getParameter("currFieldDeptId", request) != null) {
				lLongCurrFieldDeptId = Long.valueOf(StringUtility.getParameter("currFieldDeptId", request));
				lLstDesigDtls = lObjSrkaMasterDAO.getDesigDsplyDtls(lLongCurrFieldDeptId);
				lLongMaxDesigInFieldDept = lObjSrkaMasterDAO.getMaxDesigInFieldDept(lLongCurrFieldDeptId);
				lLongMaxDesigInFieldDept = lLongMaxDesigInFieldDept + 1l;
				designationCode = lLongMaxDesigInFieldDept.toString();
				CadreList = lObjDcpsCommonDAO.getCadreForDept(lLongCurrFieldDeptId);
				inputMap.put("CurrFieldDeptId", lLongCurrFieldDeptId);
				inputMap.put("designationCode", designationCode);
				inputMap.put("DesigDsplyDtls", lLstDesigDtls);
				inputMap.put("CadreList", CadreList);
			}

			inputMap.put("FieldDeptList", FieldDeptList);
			inputMap.put("PayCommissionDCPS", lLstPayCmsn);

			objRes.setResultValue(inputMap);
			objRes.setViewName("DCPSDesignationInfo");

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
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
	 * com.tcs.sgv.dcps.service.SrkaMasterService#loadOrgInfo(java.util.Map)
	 */
	public ResultObject loadOrgInfo(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List<MstDcpsOrganization> lLstOrgDtls = null;
		SrkaMasterDAO lObjSrkaMasterDAO = null;

		try {
			setSessionInfo(inputMap);
			lObjSrkaMasterDAO = new SrkaMasterDAOImpl(MstDcpsOrganization.class, serv.getSessionFactory());
			lLstOrgDtls = lObjSrkaMasterDAO.getOrgDsplyDtls();
			inputMap.put("OrgDsplyDtls", lLstOrgDtls);
			objRes.setResultValue(inputMap);
			objRes.setViewName("DCPSOrganizationInfo");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;
	}

	public ResultObject loadDdoInfo(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstAdminDept = null;
		List lLstTreasury = null;

		try {
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			new SrkaMasterDAOImpl(MstDcpsOrganization.class, serv.getSessionFactory());

			lLstAdminDept = lObjDcpsCommonDAO.getAllDepartment(Long.parseLong(gObjRsrcBndle.getString("DCPS.DEPARTMENTID")), gLngLangId);
			inputMap.put("AdminDept", lLstAdminDept);

			// lLstDdoCode = lObjSrkaMasterDAO.getDdoCodes();
			// inputMap.put("DdoList", lLstDdoCode);

			lLstTreasury = lObjDcpsCommonDAO.getAllTreasuriesAndSubTreasuries();
			inputMap.put("TreasuryList", lLstTreasury);

			objRes.setResultValue(inputMap);
			objRes.setViewName("SrkaDdoInfo");
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;
	}

	public ResultObject popDdoCode(Map<String, Object> inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SrkaMasterDAO lObjSrkaMasterDAO = null;
		List lLstDdoCode = null;

		try {
			setSessionInfo(inputMap);

			lObjSrkaMasterDAO = new SrkaMasterDAOImpl(MstDcpsOrganization.class, serv.getSessionFactory());

			String lStrTreasuryCode = StringUtility.getParameter("cmbTreasury", request);

			lLstDdoCode = lObjSrkaMasterDAO.getDdoCodes(lStrTreasuryCode);

			String lStrTempResult = null;
			if (lLstDdoCode != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lLstDdoCode, "desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;

	}

	public ResultObject getFieldDept(Map inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		SrkaMasterDAO lObjSrkaMasterDAO = null;
		List lLstFieldDept = null;

		try {
			setSessionInfo(inputMap);

			lObjSrkaMasterDAO = new SrkaMasterDAOImpl(MstDcpsOrganization.class, serv.getSessionFactory());

			Long lLngDeptCode = Long.parseLong(StringUtility.getParameter("cmbAdminDept", request));

			lLstFieldDept = lObjSrkaMasterDAO.getFieldDept(lLngDeptCode);

			String lStrTempResult = null;
			if (lLstFieldDept != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lLstFieldDept, "desc", "id", true).toString();
			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}

		return objRes;
	}

	public ResultObject populateCadres(Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			/* Sets the Session Information */
			setSessionInfo(lMapInputMap);

			/* Initializes the DAO */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			/* Gets the bank name from request */
			Long lLngDeptCode = Long.parseLong(StringUtility.getParameter("cmbFieldDepartment", request));

			/*
			 * Gets the branch names from the bank name and sends them using
			 * AJAX
			 */
			List lListCadres = lObjDcpsCommonDAO.getCadreForDept(lLngDeptCode);

			String lStrTempResult = null;
			if (lListCadres != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lListCadres, "desc", "id", true).toString();
			}
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}
	
	private StringBuilder getResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	public ResultObject checkDesigExistInCadreAndFieldDept(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		String lStrDesig = null;
		String lStrFieldDept = null;
		Long lLongFieldDept = null;

		try {

			setSessionInfo(inputMap);
			SrkaMasterDAO lObjSrkaMasterDAO = new SrkaMasterDAOImpl(MstDcpsOrganization.class, serv.getSessionFactory());

			lStrDesig = StringUtility.getParameter("txtDesig", request).trim();
			lStrFieldDept = StringUtility.getParameter("cmbFieldDepartment", request).trim();
			
			if(!"".equals(lStrFieldDept))
			{
				lLongFieldDept = Long.valueOf(lStrFieldDept);
			}
			
			if(lObjSrkaMasterDAO.checkDesigExistInCadreAndFieldDept(lStrDesig, lLongFieldDept))
			{
				lBlFlag = true;
			}
			
			String lSBStatus = getResponseXMLDoc(lBlFlag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}
	
	public ResultObject getDDOAdminDept(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		OrgDdoMst lObjOrgDdoMst = null;
		String lStrAdminDept = null;

		try {

			setSessionInfo(inputMap);

			String lStrDDOCode = StringUtility.getParameter("cmbDdoCode", request).trim();
			
			DcpsCommonDAO DcpsCommonDAOObj = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			lObjOrgDdoMst =  (OrgDdoMst) DcpsCommonDAOObj.getDdoVOForDdoCode(lStrDDOCode);
			lStrAdminDept = lObjOrgDdoMst.getDeptLocCode();

			String lSBStatus = getResponseXMLDocForGetAdminDeptofDDO(lStrAdminDept).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error in getDDOAdminDept " + e, e);
		}

		return resObj;

	}
	
	private StringBuilder getResponseXMLDocForGetAdminDeptofDDO(String lStrAdminDept) {

		StringBuilder lStrBldXML = new StringBuilder();
		
		if(lStrAdminDept == null)
		{
			lStrAdminDept = "";
		}

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<AdminDept>");
		lStrBldXML.append(lStrAdminDept.trim());
		lStrBldXML.append("</AdminDept>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

}
