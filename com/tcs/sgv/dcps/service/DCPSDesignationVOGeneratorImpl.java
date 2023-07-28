/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 22, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 22, 2011
 */
public class DCPSDesignationVOGeneratorImpl extends ServiceImpl implements DCPSDesignationVOGenerator {

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for LangId */
	Long gLngLangId = null;

	/* Global Variable for EmpId */
	Long gLngEmpId = null;

	/* Global Variable for Location Id */
	String gStrLocId = null;

	/* Global Variable for DB Id */
	Long gLngDBId = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	String gStrAuditorFlag = null;

	String gStrStatus = null;

	Long gLngAuditPostId = null;

	Long gLngAuditUserId = null;

	Log gLogger = LogFactory.getLog(getClass());

	Date gDateDBDate = null;

	// private final ResourceBundle gObjRsrcBndle = ResourceBundle
	// .getBundle("resources/pensionproc/PensionCaseConstants");

	// Sets session information in the global variables
	private void setSessionInfo(Map inputMap) {

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gDateDBDate = DBUtility.getCurrentDateFromDB();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.DCPSDesignationVOGenerator#generateMap(java.
	 * util.Map)
	 */
	public ResultObject generateMap(Map inputMap) {

		gLogger.info("In generateMap of DCPSDesignationVOGeneratorImpl........");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		inputMap.get("requestObj");
		inputMap.get("serviceLocator");
		try {

			MstDcpsDesignation lObjMstDcpsDesignationVO = new MstDcpsDesignation();
			lObjMstDcpsDesignationVO = generateMstDcpsDesignationVO(inputMap);
			inputMap.put("Mode", "Add");
			inputMap.put("lObjMstDcpsDesignationVO", lObjMstDcpsDesignationVO);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
		} catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			gLogger.info("Error is  " + e);
		}

		return objRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.dcps.service.DCPSDesignationVOGenerator#
	 * generateMstDcpsDesignationVO(java.util.Map)
	 */
	public MstDcpsDesignation generateMstDcpsDesignationVO(Map inputMap) {

		gLogger.info("In generateDcpsDdoInfoVO of DCPSDdoInfoVOGeneratorImpl........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		MstDcpsDesignation lObjMstDcpsDesignationVO = new MstDcpsDesignation();
		try {
			setSessionInfo(inputMap);
			// Long desigId ;
			Long lLngfieldDeptId = null;
			Long lLngdesigCode = null;
			String lStrdesigDesc = null;
			Long lLngcadreTypeId = null;
			Long lLngpayComsnId = null;

			lStrdesigDesc = (StringUtility.getParameter("txtDesig", request).trim().length() > 0) ? StringUtility.getParameter("txtDesig", request).trim() : null;
			gLogger.info("Designation is  : " + lStrdesigDesc);

			lLngfieldDeptId = (StringUtility.getParameter("cmbFieldDepartment", request).trim().length() > 0) ? Long.valueOf(StringUtility.getParameter("cmbFieldDepartment", request).trim()) : null;
			gLogger.info("Id of Field Department is  : " + lLngfieldDeptId);

			lLngdesigCode = (StringUtility.getParameter("txtDesigCode", request).trim().length() > 0) ? Long.valueOf(StringUtility.getParameter("txtDesigCode", request).trim()) : null;
			gLogger.info("Designation code  is  : " + lLngdesigCode);

			lLngcadreTypeId = (StringUtility.getParameter("cmbCadre", request).trim().length() > 0) ? Long.valueOf(StringUtility.getParameter("cmbCadre", request).trim()) : null;
			gLogger.info("Id of cadre  is   : " + lLngcadreTypeId);

			lLngpayComsnId = (StringUtility.getParameter("cmbPayCommission", request).trim().length() > 0) ? Long.valueOf(StringUtility.getParameter("cmbPayCommission", request).trim()) : null;
			gLogger.info("Id of pay commission   is   : " + lLngpayComsnId);

			// set primary key in service

			lObjMstDcpsDesignationVO.setDesigDesc(lStrdesigDesc);
			lObjMstDcpsDesignationVO.setFieldDeptId(lLngfieldDeptId);
			lObjMstDcpsDesignationVO.setDesigCode(lLngdesigCode);
			lObjMstDcpsDesignationVO.setCadreTypeId(lLngcadreTypeId);
			lObjMstDcpsDesignationVO.setPayComsnId(lLngpayComsnId);
			lObjMstDcpsDesignationVO.setLocationCode(Long.valueOf(gStrLocId));
			lObjMstDcpsDesignationVO.setLangId(gLngLangId);
			lObjMstDcpsDesignationVO.setDbId(gLngDBId);
			lObjMstDcpsDesignationVO.setCreatedUserId(gLngUserId);
			lObjMstDcpsDesignationVO.setCreatedPostId(gLngPostId);
			lObjMstDcpsDesignationVO.setCreatedDate(gDateDBDate);
			lObjMstDcpsDesignationVO.setUpdatedDate(gDateDBDate);
			lObjMstDcpsDesignationVO.setUpdatedPostId(gLngPostId);
			lObjMstDcpsDesignationVO.setUpdatedUserId(gLngUserId);

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error in generateDcpsDdoInfoVO method is :" + e, e);
		}
		return lObjMstDcpsDesignationVO;
	}

}
