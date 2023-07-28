/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Bhargav Trivedi								
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
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;
import com.tcs.sgv.dcps.valueobject.MstDcpsOrganization;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Apr 7, 2011
 */
public class OrganizationVOGeneratorImpl  extends ServiceImpl implements  OrganizationVOGenerator 
{

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

	//private final ResourceBundle gObjRsrcBndle = ResourceBundle
	//.getBundle("resources/pensionproc/PensionCaseConstants");
	
	// Sets session information in the global variables
	private void setSessionInfo(Map inputMap) 
	{

		gLngLangId = SessionHelper.getLangId(inputMap);
		gLngEmpId = SessionHelper.getEmpId(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		gLngUserId = SessionHelper.getUserId(inputMap);
		gStrLocId = SessionHelper.getLocationCode(inputMap);
		gLngDBId = SessionHelper.getDbId(inputMap);
		gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		gDateDBDate = DBUtility.getCurrentDateFromDB();
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.service.DCPSOrganizationVOGenerator#generateMap(java.util.Map)
	 */
	public ResultObject generateMap(Map inputMap) 
	{
		gLogger.info("In generateMap of DCPSOrganizationVOGeneratorImpl........");
		
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		try 
		{
			
			MstDcpsOrganization lObjMstDcpsOrganizationVO=new MstDcpsOrganization();
			lObjMstDcpsOrganizationVO = generateMstDcpsOrganizationVO(inputMap);
			inputMap.put("Mode", "Add");
			inputMap.put("lObjMstDcpsOrganizationVO",lObjMstDcpsOrganizationVO);
			objRes.setResultCode(ErrorConstants.SUCCESS);
			objRes.setResultValue(inputMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			gLogger.info("Error is  " + e);
		}
		
		
		return objRes;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.service.DCPSOrganizationVOGenerator#generateMstDcpsOrganizationVO(java.util.Map)
	 */
	public MstDcpsOrganization generateMstDcpsOrganizationVO(Map inputMap) 
	{
		gLogger.info("In generateMstDcpsOrganizationVO of DCPSOrganizationVOGeneratorImpl........");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		MstDcpsOrganization lObjMstDcpsOrganizationVO=new MstDcpsOrganization();
		try 
		{
			setSessionInfo(inputMap);
			String lStrOrgType  = null;
			String lStrOrgDesc  = null;
			String lStrEmpHeadAcc  = null;
			String lStrEmpSchemeCode  = null;
			String lStrEmplrHeadAcc  = null;
			String lStrEmplrSchemeCode  = null;
			String lStrDeptEmpHeadAcc  = null;
			String lStrDeptEmpSchemeCode  = null;
			String lStrDeptEmplrHeadAcc  = null;
			String lStrDeptEmplrSchemeCode  = null ;
			
		
			
			lStrOrgType = (StringUtility.getParameter("txtOrgType",request).trim().length() > 0) ?
					       StringUtility.getParameter("txtOrgType", request).trim(): null;
			gLogger.info("Type of Organization is  : " + lStrOrgType);
			
			lStrOrgDesc = (StringUtility.getParameter("txtOrgDesc",request).trim().length() > 0) ?
				       StringUtility.getParameter("txtOrgDesc", request).trim(): null;
			gLogger.info("Description of Organization is  : " + lStrOrgDesc);
			
			
			lStrEmpHeadAcc = (StringUtility.getParameter("txtEmpHeadAcc",request).trim().length() > 0) ?
					       StringUtility.getParameter("txtEmpHeadAcc", request).trim(): null;
			gLogger.info("Employee Head Account of Organization is  : " + lStrEmpHeadAcc);
			

			lStrEmpSchemeCode = (StringUtility.getParameter("txtEmpSchemeCode",request).trim().length() > 0) ?
					       StringUtility.getParameter("txtEmpSchemeCode", request).trim(): null;
			gLogger.info("Employee Scheme Code of Organization is  : " + lStrEmpSchemeCode);
			

			lStrEmplrHeadAcc = (StringUtility.getParameter("txtEmplrHeadAcc",request).trim().length() > 0) ?
					       StringUtility.getParameter("txtEmplrHeadAcc", request).trim(): null;
			gLogger.info("Employer Head Account of Organization is  : " + lStrEmplrHeadAcc);
			

			lStrEmplrSchemeCode = (StringUtility.getParameter("txtEmplrSchemeCode",request).trim().length() > 0) ?
					       StringUtility.getParameter("txtEmplrSchemeCode", request).trim(): null;
			gLogger.info("Employer Scheme Code of Organization is  : " + lStrEmplrSchemeCode);
			

			lStrDeptEmpHeadAcc = (StringUtility.getParameter("txtDeptEmpHeadAcc",request).trim().length() > 0) ?
					       StringUtility.getParameter("txtDeptEmpHeadAcc", request).trim(): null;
			gLogger.info("Deputation Employee Head Account of Organization is  : " + lStrDeptEmpHeadAcc);
			

			lStrDeptEmpSchemeCode = (StringUtility.getParameter("txtDeptEmpSchemeCode",request).trim().length() > 0) ?
					       StringUtility.getParameter("txtDeptEmpSchemeCode", request).trim(): null;
			gLogger.info("Deputation Employee Scheme Code of Organization is  : " + lStrDeptEmpSchemeCode);
			

			lStrDeptEmplrHeadAcc = (StringUtility.getParameter("txtDeptEmplrHeadAcc",request).trim().length() > 0) ?
					       StringUtility.getParameter("txtDeptEmplrHeadAcc", request).trim(): null;
			gLogger.info("Deputation Employer Head Account of Organization is  : " + lStrDeptEmplrHeadAcc);
			

			lStrDeptEmplrSchemeCode = (StringUtility.getParameter("txtDeptEmplrSchemeCode",request).trim().length() > 0) ?
					       StringUtility.getParameter("txtDeptEmplrSchemeCode", request).trim(): null;
			gLogger.info("Deputation Employer Scheme Code of Organization is  : " + lStrDeptEmplrSchemeCode);
		
			
			
			
			
			
			lObjMstDcpsOrganizationVO.setOrgType(lStrOrgType);
			lObjMstDcpsOrganizationVO.setOrgDesc(lStrOrgDesc);
			lObjMstDcpsOrganizationVO.setEmpHeadAcc(lStrEmpHeadAcc);
			lObjMstDcpsOrganizationVO.setEmpSchemeCode(lStrEmpSchemeCode);
			lObjMstDcpsOrganizationVO.setEmplrHeadAcc(lStrEmplrHeadAcc);
			lObjMstDcpsOrganizationVO.setEmplrSchemeCode(lStrEmplrSchemeCode);
			lObjMstDcpsOrganizationVO.setDeptEmpHeadAcc(lStrDeptEmpHeadAcc);
			lObjMstDcpsOrganizationVO.setDeptEmpSchemeCode(lStrDeptEmpSchemeCode);
			lObjMstDcpsOrganizationVO.setDeptEmplrHeadAcc(lStrDeptEmplrHeadAcc);
			lObjMstDcpsOrganizationVO.setDeptEmplrSchemeCode(lStrDeptEmplrSchemeCode);
			lObjMstDcpsOrganizationVO.setLocationCode(Long.valueOf(gStrLocId));
			lObjMstDcpsOrganizationVO.setLangId(gLngLangId);
			lObjMstDcpsOrganizationVO.setDbId(gLngDBId);
			lObjMstDcpsOrganizationVO.setCreatedUserId(gLngUserId);
			lObjMstDcpsOrganizationVO.setCreatedPostId(gLngPostId);
			lObjMstDcpsOrganizationVO.setCreatedDate(gDateDBDate);
			lObjMstDcpsOrganizationVO.setUpdatedDate(gDateDBDate);
			lObjMstDcpsOrganizationVO.setUpdatedPostId(gLngPostId);
			lObjMstDcpsOrganizationVO.setUpdatedUserId(gLngUserId);
						
				 
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			gLogger.error("Error in generateMstDcpsOrganizationVO method is :" + e, e);
		}
		return lObjMstDcpsOrganizationVO;
	}
	
}
