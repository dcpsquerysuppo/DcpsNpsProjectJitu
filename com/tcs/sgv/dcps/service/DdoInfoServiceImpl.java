/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 19, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.log4j.Logger;

import com.tcs.sgv.common.dao.LocationDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoInfoDAO;
import com.tcs.sgv.dcps.dao.DdoInfoDAOImpl;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 19, 2011
 */
public class DdoInfoServiceImpl extends ServiceImpl implements DdoInfoService {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	private Long gLngLangId = null; /* LANG ID */
	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private Date gDtCurDate = null; /* CURRENT DATE */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

	private final static Logger gLogger = Logger.getLogger(DdoInfoServiceImpl.class);

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gLngLangId = SessionHelper.getLangId(inputMap);

		} catch (Exception e) {
			gLogger.error("Error in setSessionInfo of DdoInfoServiceImpl ", e);
			throw e;
		}
	}

	public ResultObject loadDDOInfoPage(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			DdoInfoDAO ddoInfoDAO = new DdoInfoDAOImpl(null, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			LocationDAOImpl lObjLocationDAO = new LocationDAOImpl(null, serv.getSessionFactory());
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			/* Get the DDO Code corresponding to the DDO Asst logged in */
			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);

			/* Get the list of all the banks */
			List lLstBankNames = lObjDcpsCommonDAO.getBankNames();

			/* End : Get the list of all the Departments */

			/* Get the list of all the Designations */

			/*
			 * DDOInformationDetail lObjDDOInformation = ddoInfoDAO
			 * .getDdoInfo(lStrDdoCode);
			 */

			OrgDdoMst lObjOrgDdoMst = ddoInfoDAO.getDdoInformation(lStrDdoCode);

			/* If the data of the DDO exists, get the Bank and Branch name */
			if (lObjOrgDdoMst != null) {

				// Get Branch name for selected banks
				if (lObjOrgDdoMst.getBankName() != null && !(lObjOrgDdoMst.getBankName().equals(""))) {
					List lLstBrachNames = lObjDcpsCommonDAO.getBranchNamesWithBsrCodes(Long.valueOf(lObjOrgDdoMst.getBankName()));
					inputMap.put("BRANCHNAMES", lLstBrachNames);
				}

				String lStrAdminDept = lObjLocationDAO.getDeptNameByLocCode(lObjOrgDdoMst.getDeptLocCode(), gLngLangId.toString());
				String lStrFieldDept = lObjLocationDAO.getDeptNameByLocCode(lObjOrgDdoMst.getHodLocCode(), gLngLangId.toString());
				Long lLngFieldDept = null;
				if (!(lStrAdminDept.equals(""))) {
					if(lObjOrgDdoMst.getHodLocCode() != null)
					{
						lLngFieldDept = Long.parseLong(lObjOrgDdoMst.getHodLocCode());
					}
					List lLstDesignation = lObjDcpsCommonDAO.getAllDesignation(lLngFieldDept, gLngLangId);
					inputMap.put("lLstDesignation", lLstDesignation);
				}
				inputMap.put("AdminDept", lStrAdminDept);
				inputMap.put("FieldDept", lStrFieldDept);
				inputMap.put("DdoDetails", lObjOrgDdoMst);
				inputMap.put("cmbDdoCode", lObjOrgDdoMst.getDdoId());

			}

			inputMap.put("DdoCode", lStrDdoCode);

			inputMap.put("lDtCurDate", lObjDateFormat.format(gDtCurDate));
			inputMap.put("BANKNAMES", lLstBankNames);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSDDOInformation");

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error("Error In loading DDO Info Page " + e);
		}

		return resObj;
	}

	/**
	 * service method to used to save DDO Information into Databse
	 * 
	 * @param Map
	 *            <String,Object> inputMap
	 * @return ResultObject
	 */
	public ResultObject saveDDOInformation(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Boolean lBlFlag;
		Boolean lBlDdoExistsFlag;
		Long lLongLocIdOfDDO = null;
		String lStrDDOCode = null;
		
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			String UserType = StringUtility.getParameter("UserType", request).trim();
			String lStrDdoId = StringUtility.getParameter("cmbDdoCode", request).trim();

			/* Initializes the DAOs and variables */

			DdoInfoDAO ddoInfoDAO = new DdoInfoDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			lBlFlag = false;

			lBlDdoExistsFlag = false;
			OrgDdoMst lObjDDOInfo = (OrgDdoMst) ddoInfoDAO.read(Long.parseLong(lStrDdoId));

			/* Creates or updates the VO of DDOInformation */

			if (UserType.equals("SRKA")) {
				String lStrAdministrativeDept = StringUtility.getParameter("cmbAdminDept", request).trim();
				String lStrFieldHodDept = StringUtility.getParameter("cmbFieldDept", request).trim();
				lObjDDOInfo.setDeptLocCode(lStrAdministrativeDept);
				lObjDDOInfo.setHodLocCode(lStrFieldHodDept);
				
				lLongLocIdOfDDO = Long.valueOf(lObjDDOInfo.getLocationCode().trim());
				ddoInfoDAO.updateParentLocInCmnLocMstForDDO(lLongLocIdOfDDO, lStrFieldHodDept);
				
			} else {
				String lStrDdoPersonalName = StringUtility.getParameter("txtDDOName", request).trim();
				String lStrDdoDesignation = StringUtility.getParameter("cmbDesignation", request).trim();
				String lStrWefDate = StringUtility.getParameter("txtWEFDate", request).trim();
				String lStrTanNo = StringUtility.getParameter("txtTANNo", request).trim();
				String lStrItawardcircle = StringUtility.getParameter("txtITWardCircle", request).trim();
				String lStrBankName = StringUtility.getParameter("cmbBankName", request).trim();
				String lStrBranchName = StringUtility.getParameter("cmbBranchName", request).trim();
				String lStrAccountNo = StringUtility.getParameter("txtAccountNo", request).trim();
				String lStrRemarks = StringUtility.getParameter("txtRemarks", request).trim();
				String lStrIfscCode = StringUtility.getParameter("txtIFSCCode", request).trim();
				String lStrDesigName = StringUtility.getParameter("DesigName", request).trim();
				Date dtWEFDate = null;

				if (lStrWefDate != null && !"".equals(lStrWefDate.trim())) {
					dtWEFDate = IFMSCommonServiceImpl.getDateFromString(lStrWefDate);
				}

				lObjDDOInfo.setDdoPersonalName(lStrDdoPersonalName);
				lObjDDOInfo.setDesignCode(lStrDdoDesignation);
				lObjDDOInfo.setStartDate(dtWEFDate);
				lObjDDOInfo.setTanNo(lStrTanNo);
				lObjDDOInfo.setItaWardNo(lStrItawardcircle);
				lObjDDOInfo.setBankName(lStrBankName);
				lObjDDOInfo.setBranchName(lStrBranchName);
				lObjDDOInfo.setAccountNo(lStrAccountNo);
				lObjDDOInfo.setRemarks(lStrRemarks);
				lObjDDOInfo.setIfsCode(lStrIfscCode);
				lObjDDOInfo.setDesignName(lStrDesigName);

				/* For change in designation */
				String lStrDdoOfficeName = lObjDDOInfo.getDdoOffice();
				if (!(lStrDdoOfficeName == null)) {
					String lStrDdoName = null;
					lStrDdoName = lStrDesigName.concat(", ");
					lStrDdoName = lStrDdoName.concat(lStrDdoOfficeName);
					lObjDDOInfo.setDdoName(lStrDdoName);

				}
				
				ddoInfoDAO.updateDdoName(gLngPostId, lStrDdoPersonalName);
				
				lStrDDOCode = lObjDDOInfo.getDdoCode();
				ddoInfoDAO.updateDesigInOrgPost(lStrDDOCode,lStrDdoDesignation);
				ddoInfoDAO.updateDesigNameInOrgPostDtlRlt(lStrDDOCode,lStrDdoDesignation);
			}
			lObjDDOInfo.setUpdatedBy(gLngUserId);
			lObjDDOInfo.setUpdatedByPost(gLngPostId);
			lObjDDOInfo.setUpdatedDate(gDtCurDate);
			ddoInfoDAO.update(lObjDDOInfo);

			lBlFlag = true;

		} catch (Exception ex) {
			resObj.setResultValue(null);
			resObj.setThrowable(ex);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			ex.printStackTrace();
			return resObj;
		}

		/* Generates the XML response and sends the success flag */
		String lSBStatus = getResponseXMLDoc(lBlFlag,lBlDdoExistsFlag).toString();

		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;

	}

	/*
	 * 
	 */

	public ResultObject populateBranchNames(Map<String, Object> lMapInputMap) {

		ResultObject lObjResultObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lLstBranchNames = null;

		try {

			/* Sets the Session Information */
			setSessionInfo(lMapInputMap);

			/* Initializes the DAO */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			/* Gets the bank name from request */
			Long lLngBankName = Long.parseLong(StringUtility.getParameter("cmbBankName", request));

			/*
			 * Gets the branch names from the bank name and sends them using
			 * AJAX
			 */

			if (StringUtility.getParameter("requestFor", request).equals("bsrCodes")) {
				lLstBranchNames = lObjDcpsCommonDAO.getBranchNamesWithBsrCodes(lLngBankName);
			} else {
				lLstBranchNames = lObjDcpsCommonDAO.getBranchNames(lLngBankName);
			}

			String lStrTempResult = null;
			if (lLstBranchNames != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(lLstBranchNames, "desc", "id", true).toString();
			}
			lMapInputMap.put("ajaxKey", lStrTempResult);
			lObjResultObj.setResultValue(lMapInputMap);
			lObjResultObj.setViewName("ajaxData");
		} catch (Exception e) {
			lObjResultObj.setResultValue(null);
			lObjResultObj.setThrowable(e);
			lObjResultObj.setResultCode(ErrorConstants.ERROR);
			lObjResultObj.setViewName("errorPage");
			gLogger.error(" Error is : " + e, e);
		}
		return lObjResultObj;
	}

	/*
	 * 
	 */

	public ResultObject displayIFSCCodeForBranch(Map inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAO */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			/* Gets the branch name from request */
			Long lLongbranchName = Long.valueOf(StringUtility.getParameter("cmbBranchName", request).toString().trim());

			/* gets the MICR code for the selected branch */
			Long micrCode = lObjDcpsCommonDAO.getIFSCCodeForBranch(lLongbranchName);

			/* sends the MICR Code using AJAX */
			String lSBStatus = getResponseXMLDocforIFSCCode(micrCode).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		}

		catch (Exception e) {
			gLogger.error("Error is " + e, e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;

	}

	private StringBuilder getResponseXMLDocforIFSCCode(Long lLongIFSCCode) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<IFSCCode>");
		lStrBldXML.append(lLongIFSCCode);
		lStrBldXML.append("</IFSCCode>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;

	}

	private StringBuilder getResponseXMLDoc(Boolean flag, Boolean ddoExistsFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<ddoExistsFlag>");
		lStrBldXML.append(ddoExistsFlag);
		lStrBldXML.append("</ddoExistsFlag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

}
