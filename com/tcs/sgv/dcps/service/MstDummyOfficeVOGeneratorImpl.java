/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 12, 2011		Bhargav Trivedi								
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
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.TreasuryDAO;
import com.tcs.sgv.dcps.dao.TreasuryDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDummyOffice;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 12, 2011
 */
public class MstDummyOfficeVOGeneratorImpl extends ServiceImpl implements
		MstDummyOfficeVOGenerator {

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
	 * com.tcs.sgv.dcps.service.DCPSOrganizationVOGenerator#generateMap(java
	 * .util.Map)
	 */
	public ResultObject generateMap(Map inputMap) {
		gLogger
				.info("In generateMap of DCPSOrganizationVOGeneratorImpl........");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {

			MstDummyOffice lObjMstDummyOfficeVO = new MstDummyOffice();
			lObjMstDummyOfficeVO = generateMstDummyOfficeVO(inputMap);
			inputMap.put("Mode", "Add");
			inputMap.put("lObjMstDummyOfficeVO", lObjMstDummyOfficeVO);
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
	 * @see
	 * com.tcs.sgv.dcps.service.MstDummyOfficeVOGenerator#generateMstDummyOfficeVO
	 * (java.util.Map)
	 */
	public MstDummyOffice generateMstDummyOfficeVO(Map inputMap) {
		gLogger
				.info("In generateMstDummyOfficeVO of MstDummyOfficeVOGeneratorImpl........");
		HttpServletRequest request = (HttpServletRequest) inputMap
				.get("requestObj");
		
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		
		TreasuryDAO lObjTreasuryDAO = new TreasuryDAOImpl(MstDummyOffice.class,
				serv.getSessionFactory());
		
		MstDummyOffice lObjMstDummyOfficeVO = null;
		
		
		try { // listAdminDept
			setSessionInfo(inputMap);
			String lStrDummyOfficeId = null;
			String lStrDummyOfficeName = null;
			String lStrAdminDept = null;
			String lStrOffAddr1 = null;
			String lStrOffAddr2 = null;
			String lStrDistrict = null;
			String lStrTaluka = null;
			String lStrTown = null;
			String lStrVillage = null;
			String lStrPinCode = null;
			String lStrTelNo1 = null;
			String lStrTelNo2 = null;
			String lStrFaxNo = null;
			String lStrEmailAddr = null;
			String lStrStatusFlag = null;

			lStrDummyOfficeId = (StringUtility.getParameter("txtOffCode",
					request).trim().length() > 0) ? StringUtility.getParameter(
					"txtOffCode", request).trim() : null;
					
			Integer saveOrUpdateFlag = Integer.parseInt(StringUtility
					.getParameter("saveOrUpdateFlag", request).trim());
			
			if (saveOrUpdateFlag == 1) {
				lObjMstDummyOfficeVO = new MstDummyOffice();
			}
			if (saveOrUpdateFlag == 2) {
				lObjMstDummyOfficeVO = lObjTreasuryDAO.getDummyOfficeInfo(lStrDummyOfficeId,gStrLocationCode);
			}

			lStrAdminDept = (StringUtility.getParameter("listAdminDept",
					request).trim().length() > 0) ? StringUtility.getParameter(
					"listAdminDept", request).trim() : null;

			lStrDummyOfficeName = (StringUtility.getParameter("txtOffName",
					request).trim().length() > 0) ? StringUtility.getParameter(
					"txtOffName", request).trim() : null;

			lStrOffAddr1 = (StringUtility.getParameter("txtOffAddr1", request)
					.trim().length() > 0) ? StringUtility.getParameter(
					"txtOffAddr1", request).trim() : null;

			lStrOffAddr2 = (StringUtility.getParameter("txtOffAddr2", request)
					.trim().length() > 0) ? StringUtility.getParameter(
					"txtOffAddr2", request).trim() : null;

			lStrDistrict = (StringUtility.getParameter("cmbDistrict", request)
					.trim().length() > 0) ? StringUtility.getParameter(
					"cmbDistrict", request).trim() : null;

			lStrTaluka = (StringUtility.getParameter("cmbTaluka", request)
					.trim().length() > 0) ? StringUtility.getParameter(
					"cmbTaluka", request).trim() : null;

			lStrTown = (StringUtility.getParameter("cmbTown", request).trim()
					.length() > 0) ? StringUtility.getParameter("cmbTown",
					request).trim() : null;

			lStrVillage = (StringUtility.getParameter("txtVillage", request)
					.trim().length() > 0) ? StringUtility.getParameter(
					"txtVillage", request).trim() : null;

			lStrPinCode = (StringUtility.getParameter("txtOffPin", request)
					.trim().length() > 0) ? StringUtility.getParameter(
					"txtOffPin", request).trim() : null;

			lStrTelNo1 = (StringUtility.getParameter("txtTelno1", request)
					.trim().length() > 0) ? StringUtility.getParameter(
					"txtTelno1", request).trim() : null;

			lStrTelNo2 = (StringUtility.getParameter("txtTelno2", request)
					.trim().length() > 0) ? StringUtility.getParameter(
					"txtTelno2", request).trim() : null;

			lStrFaxNo = (StringUtility.getParameter("txtFax", request).trim()
					.length() > 0) ? StringUtility.getParameter("txtFax",
					request).trim() : null;

			lStrEmailAddr = (StringUtility.getParameter("txtEmail", request)
					.trim().length() > 0) ? StringUtility.getParameter(
					"txtEmail", request).trim() : null;

			lStrStatusFlag = ((StringUtility.getParameter("radioStatus",
					request).trim()).length() > 0) ? StringUtility
					.getParameter("radioStatus", request).trim().toUpperCase()
					: null;

			lObjMstDummyOfficeVO.setDummyOfficeId(lStrDummyOfficeId);
			lObjMstDummyOfficeVO.setAdminDept(Long.valueOf(lStrAdminDept));
			lObjMstDummyOfficeVO.setDummyOfficeName(lStrDummyOfficeName);
			lObjMstDummyOfficeVO.setOffAddr1(lStrOffAddr1);
			lObjMstDummyOfficeVO.setOffAddr2(lStrOffAddr2);
			lObjMstDummyOfficeVO.setDistrict(Long.valueOf(lStrDistrict));
			lObjMstDummyOfficeVO.setTaluka(Long.valueOf(lStrTaluka));
			lObjMstDummyOfficeVO.setTown(Long.valueOf(lStrTown));
			lObjMstDummyOfficeVO.setVillage(lStrVillage);
			
			if(!"".equals(lStrPinCode))
			{
				lObjMstDummyOfficeVO.setPinCode(Long.valueOf(lStrPinCode));
			}
			
			lObjMstDummyOfficeVO.setTelNo1(lStrTelNo1);
			lObjMstDummyOfficeVO.setTelNo2(lStrTelNo2);
			lObjMstDummyOfficeVO.setFaxNo(lStrFaxNo);
			lObjMstDummyOfficeVO.setEmailAddr(lStrEmailAddr);

			if (lStrStatusFlag == null) {
				lObjMstDummyOfficeVO.setStatusFlag('N');
			} else {
				lObjMstDummyOfficeVO.setStatusFlag(lStrStatusFlag.charAt(0));
			}
			
			lObjMstDummyOfficeVO.setTreasury(gStrLocationCode);

			lObjMstDummyOfficeVO.setLocId(Long.valueOf(gStrLocId));
			lObjMstDummyOfficeVO.setLangId(gLngLangId);
			lObjMstDummyOfficeVO.setDbId(gLngDBId);
			lObjMstDummyOfficeVO.setCreatedUserId(gLngUserId);
			lObjMstDummyOfficeVO.setCreatedPostId(gLngPostId);
			lObjMstDummyOfficeVO.setCreatedDate(gDateDBDate);
			lObjMstDummyOfficeVO.setUpdatedDate(gDateDBDate);
			lObjMstDummyOfficeVO.setUpdatedPostId(gLngPostId);
			lObjMstDummyOfficeVO.setUpdatedUserId(gLngUserId);

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error in generateMstDummyOfficeVO method is :" + e,
					e);

		}

		return lObjMstDummyOfficeVO;
	}

}
