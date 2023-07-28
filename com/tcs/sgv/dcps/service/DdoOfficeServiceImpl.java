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

import sun.util.logging.resources.logging;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.DdoOfficeDAO;
import com.tcs.sgv.dcps.dao.DdoOfficeDAOImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 26, 2011
 */
public class DdoOfficeServiceImpl extends ServiceImpl implements
		DdoOfficeService {

	/* Global Variable for PostId */
	Long gLngPostId = null;
	String gStrPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */
	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	private final static Logger gLogger = Logger
			.getLogger(DdoOfficeServiceImpl.class);

	private void setSessionInfo(Map inputMap) throws Exception {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);

			gStrPostId = gLngPostId.toString();

		} catch (Exception e) {
			gLogger
					.error("Error in setsessionifo of DDOOfficeServiceImpl  ",
							e);
			throw e;
		}
	}

	public ResultObject loadDdoOffice(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoOffice = "Yes";

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			DdoOfficeDAO lObjDdoOfficeDAO = new DdoOfficeDAOImpl(null, serv
					.getSessionFactory());

			/* Gets List of all states */
			List lLstState = lObjDcpsCommonDAO.getStateNames(1L);
			List lLstDistricts = lObjDcpsCommonDAO.getDistricts(15L);

			/* Gets Office Class List */
			List lstOfficeClass = IFMSCommonServiceImpl.getLookupValues(
					"DCPS_OFFICE_CLASS", SessionHelper.getLangId(inputMap),
					inputMap);

			/* Gets DDO Code from Post Id */
			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);

			/* Gets All DDO offices for that DDO */
			List lLstSavedOffices = lObjDdoOfficeDAO.getAllOffices(lStrDdoCode);

			if (lLstSavedOffices != null) {
				for (Integer lInt = 0; lInt < lLstSavedOffices.size(); lInt++) {
					Object[] lObjListDdoOffice = (Object[]) lLstSavedOffices
							.get(lInt);
					if (lObjListDdoOffice[2].equals("Yes")) {
						lStrDdoOffice = "No";
					}
				}
			}

			if (lStrDdoOffice.equals("Yes")) {
				String lStrDdoOfficeName = lObjDdoOfficeDAO
						.getDefaultDdoOffice(lStrDdoCode);
				inputMap.put("DefaultOffice", lStrDdoOfficeName);

			}

			/* Puts all above values in the Map */
			inputMap.put("lLstDistricts", lLstDistricts);
			inputMap.put("DdoOfficeOrNot", lStrDdoOffice);
			inputMap.put("STATENAMES", lLstState);
			inputMap.put("OFFICECLASSLIST", lstOfficeClass);
			inputMap.put("DDOCODE", lStrDdoCode);
			inputMap.put("OfficeList", lLstSavedOffices);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSDDOOffice");
			
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setViewName("errorPage");
		}
		
		return resObj;
	}

	public ResultObject saveDdoOffice(Map<String, Object> inputMap)
			throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		Boolean lBFlag = null;
		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			DdoOfficeDAO lObjDdoOfficeDAO = new DdoOfficeDAOImpl(
					DdoOffice.class, serv.getSessionFactory());
			DdoOffice dcpsddoofficevo = (DdoOffice) inputMap
					.get("DCPSOfficeData");
			CmnLocationMst CmnLocationMstvo = (CmnLocationMst) inputMap
					.get("CmnLocationMst");
			lBFlag = false;

			Integer.parseInt(StringUtility.getParameter("saveOrUpdateFlag",
					request));

			/* Creates Office */

			/* Insert the office data in cmn_location_mst */
			Long lLngLocId = IFMSCommonServiceImpl.getNextSeqNumByCount(
					"cmn_location_mst", inputMap);
			String lLngLocCode = lLngLocId.toString();
			CmnLocationMstvo.setLocId(lLngLocId);
			CmnLocationMstvo.setLocationCode(lLngLocCode);
			lObjDdoOfficeDAO.create(CmnLocationMstvo);
			/* END:Insert the office data in cmn_location_mst */

			Long lLngOfficeId = IFMSCommonServiceImpl.getNextSeqNum(
					"MST_DCPS_DDO_OFFICE", inputMap);
			dcpsddoofficevo.setDcpsDdoOfficeIdPk(lLngOfficeId);

			/* Inserting the logged in location for payroll */
			Long lLngLoggedLoc = SessionHelper.getLocationId(inputMap);
			dcpsddoofficevo.setLocId(lLngLoggedLoc);
			lObjDdoOfficeDAO.create(dcpsddoofficevo);
			lBFlag = true;

			String lStrDdoOffice = dcpsddoofficevo.getDcpsDdoOfficeDdoFlag();
			String lStrDdoCode = dcpsddoofficevo.getDcpsDdoCode();
			String lStrOfficeName = dcpsddoofficevo.getDcpsDdoOfficeName();
			if (lStrDdoOffice.equalsIgnoreCase("YES")) {
				lObjDdoOfficeDAO.updateDdoOffice(lStrOfficeName, lStrDdoCode);
				lObjDdoOfficeDAO.updateOtherOfficeToNO(lLngOfficeId,lStrDdoCode);
			}

		} catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is " + e, e);
		}

		/* Generates the XML response and sends the success flag */
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject updateDdoOffice(Map<String, Object> inputMap)
			throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		Boolean lBFlag = null;
		Long lLongDdoOfficeId = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			DdoOfficeDAO lObjDdoOfficeDAO = new DdoOfficeDAOImpl(
					DdoOffice.class, serv.getSessionFactory());
			DdoOffice dcpsddoofficevo = (DdoOffice) inputMap
					.get("DCPSOfficeData");
			lBFlag = false;
			
			lLongDdoOfficeId = dcpsddoofficevo.getDcpsDdoOfficeIdPk();

			lObjDdoOfficeDAO.update(dcpsddoofficevo);
			
			lBFlag = true;

			String lStrDdoOffice = dcpsddoofficevo.getDcpsDdoOfficeDdoFlag();
			String lStrDdoCode = dcpsddoofficevo.getDcpsDdoCode();
			String lStrOfficeName = dcpsddoofficevo.getDcpsDdoOfficeName();
			if (lStrDdoOffice.equalsIgnoreCase("YES")) {
				lObjDdoOfficeDAO.updateDdoOffice(lStrOfficeName, lStrDdoCode);
				lObjDdoOfficeDAO.updateOtherOfficeToNO(lLongDdoOfficeId,lStrDdoCode);

			}
		} catch (Exception e) {

			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			gLogger.error(" Error is " + e, e);
		}

		/* Generates the XML response and sends the success flag */
		String lSBStatus = getResponseXMLDoc(lBFlag).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus)
				.toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	public ResultObject popUpDdoOfficeDtls(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		Long lLongDdoOfficeId = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs and variables */
			DdoOfficeDAO lObjDdoOfficeDAO = new DdoOfficeDAOImpl(
					DdoOffice.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(
					DdoOffice.class, serv.getSessionFactory());

			Long lStrCurrState = -1l;
			Long lLongCurrDst = -1l;
			List lLstTalukas = null;

			/*
			 * Gets DDO Office Id from request and gets details of that DDO
			 * Office
			 */
			lLongDdoOfficeId = Long.valueOf(StringUtility.getParameter(
					"ddoOfficeId", request));
			DdoOffice DdoOfficeVO = lObjDdoOfficeDAO
					.getDdoOfficeDtls(lLongDdoOfficeId);

			/* Gets all states */
			List lLstState = lObjDcpsCommonDAO.getStateNames(1L);

			/* Gets State Id and district Id from VO */
			if(DdoOfficeVO.getDcpsDdoOfficeState()!=null && !(DdoOfficeVO.getDcpsDdoOfficeState().equals("")))
				lStrCurrState = Long.valueOf(DdoOfficeVO.getDcpsDdoOfficeState());
			
			if(DdoOfficeVO.getDcpsDdoOfficeDistrict() != null && !(DdoOfficeVO.getDcpsDdoOfficeDistrict().equals("")))
			{
				lLongCurrDst = Long.valueOf(DdoOfficeVO.getDcpsDdoOfficeDistrict());
			}

			/* Gets the List of All districts for given State */
			List lLstDistricts = lObjDcpsCommonDAO.getDistricts(lStrCurrState);

			/* Gets All Talukas for given district */
			if(lLongCurrDst != -1)
			{
				lLstTalukas = lObjDcpsCommonDAO.getTaluka(lLongCurrDst);
			}

			/* Gets Office Class List */
			List lstOfficeClass = IFMSCommonServiceImpl.getLookupValues(
					"DCPS_OFFICE_CLASS", SessionHelper.getLangId(inputMap),
					inputMap);

			/* Gets the DDO Code from Post Id */
			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);

			/* Gets All DDO Offices for that DDO */
			List lLstSavedOffices = lObjDdoOfficeDAO.getAllOffices(lStrDdoCode);

			inputMap.put("STATENAMES", lLstState);
			inputMap.put("lLstDistricts", lLstDistricts);
			inputMap.put("lLstTalukas", lLstTalukas);
			inputMap.put("OFFICECLASSLIST", lstOfficeClass);
			inputMap.put("DDOCODE", lStrDdoCode);
			inputMap.put("OfficeList", lLstSavedOffices);
			inputMap.put("DdoOfficeVO", DdoOfficeVO);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSDDOOffice");

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is:" + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
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

}
