/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 26, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.log4j.Logger;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoSchemeDAO;
import com.tcs.sgv.dcps.dao.DdoSchemeDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.RltDcpsDdoScheme;


/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 26, 2011
 */
public class DdoSchemeServiceImpl extends ServiceImpl implements DdoSchemeService {

	/* Global Variables */
	Long gLngPostId = null;
	private String gStrPostId = null; /* STRING POST ID */
	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

	private final static Logger gLogger = Logger.getLogger(DdoInfoServiceImpl.class);

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();

		} catch (Exception e) {
			gLogger.error("Error in setSessionInfo of DDOSchemeServiceImpl ", e);
			throw e;
		}
	}

	public ResultObject loadDdoSchemesAndBillGroups(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			DdoSchemeDAO lObjDdoSchemeDAO = new DdoSchemeDAOImpl(RltDcpsDdoScheme.class, serv.getSessionFactory());
			List DcpsDdoSchemeList = null;

			/* Gets DDO Code from Post Id */
			String lStrDDOCode = lObjDcpsCommonDAO.getDdoCode(SessionHelper.getPostId(inputMap));

			/* Gets list of All schemes operated by the DDO */
			DcpsDdoSchemeList = lObjDdoSchemeDAO.getSchemeListForDDO(lStrDDOCode);

			/* Gets total of all schemes */
			Integer totalRecords = DcpsDdoSchemeList.size();

			inputMap.put("schemelist", DcpsDdoSchemeList);
			inputMap.put("totalRecords", totalRecords);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSDdoScheme");

		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is " + e, e);
		}

		return resObj;
	}

	public ResultObject displaySchemeNameForCode(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrSchemeCode = null;
		List<MstScheme> lListSchemes = null;
		Integer lNoOfSchemes = 0;

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			DdoSchemeDAO lObjDdoSchemeDAO = new DdoSchemeDAOImpl(RltDcpsDdoScheme.class, serv.getSessionFactory());

			/* Gets SchemeCode from request */
			lStrSchemeCode = StringUtility.getParameter("txtSchemeCode", request);

			/*
			 * Gets the all the schemeNames whose scheme code starts with given
			 * scheme code and finds total schemes
			 */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);

			lListSchemes = lObjDdoSchemeDAO.getSchemeNamesFromCode(lStrSchemeCode, lStrDdoCode);
			lNoOfSchemes = lListSchemes.size();

			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is " + e, e);
		}

		/*
		 * Generates XML response for all schemes found whose scheme code starts
		 * with given scheme code
		 */
		String lSBStatus = getResponseXMLDocToDisplaySchemeName(lNoOfSchemes, lListSchemes).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject addSchemesAndBillGroupsToDdo(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		Boolean lFlagSchemeAddedOrNot = true;

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			DdoSchemeDAO lObjDdoSchemeDAO = new DdoSchemeDAOImpl(RltDcpsDdoScheme.class, serv.getSessionFactory());
			RltDcpsDdoScheme lObjDcpsDdoSchemeVO = (RltDcpsDdoScheme) inputMap.get("DcpsDdoScheme");
			MstDcpsBillGroup lObjMstDcpsBillGroupVO = (MstDcpsBillGroup) inputMap.get("dcpsddobillgroup");
			lFlagSchemeAddedOrNot = (Boolean) inputMap.get("schemeAddedOrNot");

			/* Gets next Primary key for RltDcpsDdoScheme VO */
			Long lDDOSchemesId = IFMSCommonServiceImpl.getNextSeqNum("RLT_DCPS_DDO_SCHEMES", inputMap);

			/* Sets Primary key and creates RltDcpsDdoScheme VO in table */
			lObjDcpsDdoSchemeVO.setDcpsDdoSchemesId(lDDOSchemesId);
			if (lFlagSchemeAddedOrNot) {
				lObjDdoSchemeDAO.create(lObjDcpsDdoSchemeVO);
				lBlFlag = true;
			}

			/* Gets next Primary key for MstDcpsBillGroup VO */
			Long lLngBillGroupId = IFMSCommonServiceImpl.getNextSeqNum("mst_dcps_bill_group", inputMap);

			/* Sets Primary key and creates MstDcpsBillGroup VO in table */
			lObjMstDcpsBillGroupVO.setDcpsDdoBillGroupId(lLngBillGroupId);
			lObjMstDcpsBillGroupVO.setSubBGOrNot(0l);
			if (lFlagSchemeAddedOrNot) {
				lObjDdoSchemeDAO.create(lObjMstDcpsBillGroupVO);
				lBlFlag = true;
			}

			resObj.setResultValue(inputMap);

		} catch (Exception e) {

			e.printStackTrace();
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");

		}

		/*
		 * Generates the XML response and sends the success flag (if a new
		 * scheme added otherwise a flag showing the scheme is already added to
		 * the DDO or not)
		 */
		String lSBStatus = getResponseXMLDocForDdoScheme(lBlFlag, lFlagSchemeAddedOrNot).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDocToDisplaySchemeName(Integer lNoOfSchemes, List lListSchemes) {

		StringBuilder lStrBldXML = new StringBuilder();
		Object[] lObjSchemes = null;
		lStrBldXML.append("<XMLDOC>");

		lStrBldXML.append("<lNoOfSchemes>");
		lStrBldXML.append(lNoOfSchemes);
		lStrBldXML.append("</lNoOfSchemes>");
		
		for (int lInt = 0; lInt < lListSchemes.size(); lInt++) {
			lObjSchemes = (Object[]) lListSchemes.get(lInt);
			lStrBldXML.append("<SchemeName" + lInt + ">");
			lStrBldXML.append("<![CDATA[");
			lStrBldXML.append(lObjSchemes[1].toString().trim());
			lStrBldXML.append("]]>");

			lStrBldXML.append("</SchemeName" + lInt + ">");
			lStrBldXML.append("<SchemeCode" + lInt + ">");
			lStrBldXML.append(lObjSchemes[0].toString().trim());
			lStrBldXML.append("</SchemeCode" + lInt + ">");
		}

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	private StringBuilder getResponseXMLDocForDdoScheme(Boolean lBlFlag, Boolean lFlagSchemeAddedOrNot) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<lBlFlag>");
		lStrBldXML.append(lBlFlag);
		lStrBldXML.append("</lBlFlag>");
		lStrBldXML.append("<lFlagSchemeAddedOrNot>");
		lStrBldXML.append(lFlagSchemeAddedOrNot);
		lStrBldXML.append("</lFlagSchemeAddedOrNot>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
