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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DCPSDesignationDAO;
import com.tcs.sgv.dcps.dao.DCPSDesignationDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 22, 2011
 */
public class DCPSDesignationServiceImpl extends ServiceImpl implements
		DCPSDesignationService {

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

	/* Global Variable for Current Date */
	Date gCurDate = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	private Locale gLclLocale = null; /* LOCALE */
	private String gStrPostId = null; /* STRING POST ID */
	private String gStrUserId = null; /* STRING USER ID */
	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */
	private HttpSession session = null; /* SESSION */
	private String gStrLocale = null; /* STRING LOCALE */
	private Date gDtCurDate = null; /* CURRENT DATE */

	private final static Logger gLogger = Logger
			.getLogger(DCPSDesignationServiceImpl.class);

	private static final Log logger = LogFactory
			.getLog(DCPSDesignationServiceImpl.class); /* LOGGER */

	// private final ResourceBundle gObjRsrcBndle = ResourceBundle
	// .getBundle("resources/pensionproc/PensionCaseConstants");

	private void setSessionInfo(Map inputMap) throws Exception {

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
			gStrLocId = SessionHelper.getLocationId(inputMap).toString();

		} catch (Exception e) {
			logger
					.error("Error in setSessionInfo of HBARequestServiceImpl ",
							e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.DCPSDesignationService#loadDesigInfo(java.util
	 * .Map)
	 */
	public ResultObject loadDesigInfo(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		inputMap.get("requestObj");
		List<CmnLookupMst> lLstFldDept = null;
		List<CmnLookupMst> lLstPayCmsn = null;
		List<ComboValuesVO> lLstCadre = null;
		DCPSDesignationDAO lObjDCPSDesignationDAO = null;
		List lLstDesigDtls = null;
		try {
			setSessionInfo(inputMap);
			lLstFldDept = IFMSCommonServiceImpl.getLookupValues(
					"ParentDeptList", SessionHelper.getLangId(inputMap),
					inputMap);
			lLstPayCmsn = IFMSCommonServiceImpl.getLookupValues(
					"PayCommissionDCPS", SessionHelper.getLangId(inputMap),
					inputMap);
			lObjDCPSDesignationDAO = new DCPSDesignationDAOImpl(
					DCPSDesignationServiceImpl.class, serv.getSessionFactory());
			lLstCadre = lObjDCPSDesignationDAO.getCadres(inputMap);
			lLstDesigDtls = lObjDCPSDesignationDAO.getDesigDsplyDtls(inputMap);

			inputMap.put("ParentDeptList", lLstFldDept);
			inputMap.put("PayCommissionDCPS", lLstPayCmsn);
			inputMap.put("CadreList", lLstCadre);
			inputMap.put("DesigDsplyDtls", lLstDesigDtls);
			objRes.setResultValue(inputMap);
			objRes.setViewName("DCPSDesignationInfo");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.service.DCPSDesignationService#insertDesignation(java
	 * .util.Map)
	 */
	public ResultObject insertDesignation(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		inputMap.get("requestObj");
		Long lLngDesigId = null;
		MstDcpsDesignation lObjMstDcpsDesignationVO = new MstDcpsDesignation();
		DCPSDesignationDAO lObjDCPSDesignationDAO = null;

		try {
			setSessionInfo(inputMap);
			String strTransMode = (String) inputMap.get("Mode");
			if (strTransMode.equalsIgnoreCase("Add")) {
				if (lObjMstDcpsDesignationVO != null) {
					lObjMstDcpsDesignationVO = (MstDcpsDesignation) inputMap
							.get("lObjMstDcpsDesignationVO");
					lLngDesigId = IFMSCommonServiceImpl.getNextSeqNum(
							"mst_dcps_designation", inputMap);
					//System.out.println("lLngDesigId is " + lLngDesigId);
					lObjMstDcpsDesignationVO.setDesigId(lLngDesigId);
					lObjDCPSDesignationDAO = new DCPSDesignationDAOImpl(
							MstDcpsDesignation.class, serv.getSessionFactory());
					lObjDCPSDesignationDAO.create(lObjMstDcpsDesignationVO);
					gLogger
							.info("Record Inserted in table mst_dcps_designation successfully");
				}

			}

			inputMap.put("ajaxKey", "Success");
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.info("errror is " + e);
		}
		return objRes;
	}

}
