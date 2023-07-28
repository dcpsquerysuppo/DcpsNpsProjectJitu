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
import com.tcs.sgv.common.valueobject.CmnLanguageMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.SrkaMasterDAOImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.ess.valueobject.OrgUserMst;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 22, 2011
 */
public class DesignationVOGeneratorImpl extends ServiceImpl implements DesignationVOGenerator {

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

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		inputMap.get("requestObj");
		inputMap.get("serviceLocator");
		try {

			MstDcpsDesignation lObjMstDcpsDesignationVO = new MstDcpsDesignation();
			lObjMstDcpsDesignationVO = generateMstDcpsDesignationVO(inputMap);
			inputMap.put("Mode", "Add");
			inputMap.put("lObjMstDcpsDesignationVO", lObjMstDcpsDesignationVO);
			OrgDesignationMst lObjOrgDesignationMst = new OrgDesignationMst();
			lObjOrgDesignationMst = generateOrgDesignationVO(inputMap);
			inputMap.put("lObjOrgDesignationMst", lObjOrgDesignationMst);
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

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		MstDcpsDesignation lObjMstDcpsDesignationVO = new MstDcpsDesignation();
		ServiceLocator servLoc = (ServiceLocator) inputMap.get("serviceLocator");
		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(DcpsCadreMst.class, servLoc.getSessionFactory());
		
		try {
			setSessionInfo(inputMap);
			// Long desigId ;
			Long lLngfieldDeptId = null;
			String lStrdesigCode = null;
			String lStrdesigDesc = null;
			Long lLngcadreId = null;
			Long lLngpayComsnId = null;
			Long lLngCadreTypeId = null;

			lStrdesigDesc = (StringUtility.getParameter("txtDesig", request).trim().length() > 0) ? StringUtility.getParameter("txtDesig", request).trim() : null;

			lLngfieldDeptId = (StringUtility.getParameter("cmbFieldDepartment", request).trim().length() > 0) ? Long.valueOf(StringUtility.getParameter("cmbFieldDepartment", request).trim()) : null;

			lStrdesigCode = StringUtility.getParameter("txtDesigCode", request);

			lLngcadreId = (StringUtility.getParameter("cmbCadre", request).trim().length() > 0) ? Long.valueOf(StringUtility.getParameter("cmbCadre", request).trim()) : null;
			
			if(lLngcadreId!=0 && lLngcadreId!=null)
			{
				lLngCadreTypeId = Long.valueOf(lObjDcpsCommonDAO.getGroupIdforCadreId(lLngcadreId));
			}

			lLngpayComsnId = (StringUtility.getParameter("cmbPayCommission", request).trim().length() > 0) ? Long.valueOf(StringUtility.getParameter("cmbPayCommission", request).trim()) : null;

			// set primary key in service

			lObjMstDcpsDesignationVO.setDesigDesc(lStrdesigDesc);
			lObjMstDcpsDesignationVO.setFieldDeptId(lLngfieldDeptId);
			if(!lStrdesigCode.equalsIgnoreCase("") && lStrdesigCode!=null)
			{
				lObjMstDcpsDesignationVO.setDesigCode(Long.valueOf(lStrdesigCode));
			}
			lObjMstDcpsDesignationVO.setCadreTypeId(lLngCadreTypeId);
			lObjMstDcpsDesignationVO.setPayComsnId(lLngpayComsnId);
			lObjMstDcpsDesignationVO.setLocationCode(Long.valueOf(gStrLocId));
			lObjMstDcpsDesignationVO.setLangId(gLngLangId);
			lObjMstDcpsDesignationVO.setDbId(gLngDBId);
			lObjMstDcpsDesignationVO.setCreatedUserId(gLngUserId);
			lObjMstDcpsDesignationVO.setCreatedPostId(gLngPostId);
			lObjMstDcpsDesignationVO.setCreatedDate(gDateDBDate);

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error in generateDcpsDdoInfoVO method is :" + e, e);
		}
		return lObjMstDcpsDesignationVO;
	}

	public OrgDesignationMst generateOrgDesignationVO(Map inputMap) {

		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		OrgDesignationMst lObjOrgDesignationVO = new OrgDesignationMst();
		try {
			setSessionInfo(inputMap);
			String lStrdesigDesc = null;
			lStrdesigDesc = (StringUtility.getParameter("txtDesig", request).trim().length() > 0) ? StringUtility.getParameter("txtDesig", request).trim() : null;

			StringUtility.getParameter("txtDesigCode", request);
			String lStrDesignationShort = StringUtility.getParameter("txtDesigShort", request);
			CmnLanguageMst lObjCmnLanguageMst = new CmnLanguageMst();
			lObjCmnLanguageMst.setLangId(gLngLangId);

			OrgPostMst lObjOrgPostMst = new OrgPostMst();
			lObjOrgPostMst.setPostId(gLngPostId);

			OrgUserMst lObjUserMst = new OrgUserMst();
			lObjUserMst.setUserId(gLngUserId);

			// set primary key in service
			lObjOrgDesignationVO.setDsgnName(lStrdesigDesc);
			lObjOrgDesignationVO.setDsgnShrtName(lStrDesignationShort);
			lObjOrgDesignationVO.setCmnLanguageMst(lObjCmnLanguageMst);
			lObjOrgDesignationVO.setStartDate(gDateDBDate);
			lObjOrgDesignationVO.setActivateFlag(1L);

			lObjOrgDesignationVO.setCreatedDate(gDateDBDate);
			lObjOrgDesignationVO.setOrgPostMstByCreatedByPost(lObjOrgPostMst);
			lObjOrgDesignationVO.setOrgUserMstByCreatedBy(lObjUserMst);

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error in generateDcpsDdoInfoVO method is :" + e, e);
		}
		return lObjOrgDesignationVO;
	}
}
