/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.OfflineContriCorrectionDAO;
import com.tcs.sgv.dcps.dao.OfflineContriCorrectionDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;


public class OfflineContriCorrectionServiceImpl extends ServiceImpl implements OfflineContriCorrectionService {

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	public ResultObject loadOfflineCorrectionForm(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAOs */
			OfflineContriCorrectionDAO OfflineContriCorrectionDAOImpl = new OfflineContriCorrectionDAOImpl(TrnDcpsContribution.class, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			/* Gets All the Bill Groups */
			List lLstBillGroups = lObjDcpsCommonDAO.getBillGroups();

			/* Get Months */
			List lLstMonths = lObjDcpsCommonDAO.getMonths();

			/* Get Years */
			List lLstYears = lObjDcpsCommonDAO.getYears();

			/*
			 * Checks if request is sent by click of GO button or the page is
			 * loaded for the first time.
			 */

			if (StringUtility.getParameter("cmbBillGroup", request) != null && !StringUtility.getParameter("cmbBillGroup", request).equalsIgnoreCase("")) {

				/* Get the DDO Code */
				String lStrDDOCode = StringUtility.getParameter("cmbDDOCode", request);

				/*
				 * Get Month Id,Year Id,Bill Group Id,Treasury Code,Scheme
				 * Name,Scheme Code from Request
				 */
				Long monthId = Long.parseLong(StringUtility.getParameter("cmbMonth", request));
				Long yearId = Long.parseLong(StringUtility.getParameter("cmbYear", request));
				Long lLongbillGroupId = Long.valueOf(StringUtility.getParameter("cmbBillGroup", request));
				Long treasuryCode = Long.valueOf(StringUtility.getParameter("treasuryCode", request));
				String schemename = StringUtility.getParameter("schemeName", request);
				Long schemeCode = Long.valueOf(StringUtility.getParameter("schemeCode", request));

				/*
				 * Get First Date and Last Date of Month For Selected Year and
				 * Month
				 */
				Integer lIntMonth = Integer.parseInt(monthId.toString());
				Integer lIntYear = Integer.parseInt(yearId.toString());
				Date lDtLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 1, lIntYear);
				Date lDtFirstDate = lObjDcpsCommonDAO.getFirstDate(lIntMonth - 1, lIntYear);

				if (lIntMonth == 1) {
					lIntYear--;
				}

				/*
				 * Get First Date and Last Date of Previous Month For Selected
				 * Year and Month
				 */

				Date lDtDelFirstDate = lObjDcpsCommonDAO.getFirstDate(lIntMonth - 2, lIntYear);
				Date lDtDelLastDate = lObjDcpsCommonDAO.getLastDate(lIntMonth - 2, lIntYear);

				/* Get All Pay Commissions from Lookup */
				List listPayCommission = IFMSCommonServiceImpl.getLookupValues("PayCommissionDCPS", SessionHelper.getLangId(inputMap), inputMap);
				inputMap.put("listPayCommission", listPayCommission);

				/* Get All Types Of Payment From Lookup */
				List listTypeOfPayment = IFMSCommonServiceImpl.getLookupValues("TypeOfPaymentDCPS", SessionHelper.getLangId(inputMap), inputMap);
				inputMap.put("listTypeOfPayment", listTypeOfPayment);

				/*
				 * Gets the Employees' contribution List for selected month and
				 * year and puts in the Map
				 */

				List empList = OfflineContriCorrectionDAOImpl.getEmpListForContributionCorrection(lStrDDOCode, lLongbillGroupId, monthId, yearId);

				/* Puts All Above Values in InputMap */

				inputMap.put("lStrDDOCode", lStrDDOCode);
				inputMap.put("monthId", monthId);
				inputMap.put("yearId", yearId);
				inputMap.put("lLongbillGroupId", lLongbillGroupId);
				inputMap.put("treasuryCode", treasuryCode);
				inputMap.put("schemename", schemename);
				inputMap.put("schemeCode", schemeCode);
				inputMap.put("FirstDate", lDtFirstDate);
				inputMap.put("LastDate", lDtLastDate);
				inputMap.put("DelFirstDate", lDtDelFirstDate);
				inputMap.put("DelLastDate", lDtDelLastDate);

				inputMap.put("empList", empList);

			}

			inputMap.put("MONTHS", lLstMonths);
			inputMap.put("YEARS", lLstYears);
			inputMap.put("BILLGROUPS", lLstBillGroups);

			resObj.setResultValue(inputMap);
			resObj.setViewName("DCPSOfflineEntryCorrection");

		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}

	public ResultObject getContributionDetails(Map<String, Object> inputMap) throws Exception {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TrnDcpsContribution lObjTrnDcpsContribution = null;
		MstEmp lObjMstEmp = null;

		try {
			/* Sets the Session Information */
			setSessionInfo(inputMap);

			/* Initializes the DAO and variables */
			OfflineContriCorrectionDAO OfflineContriCorrectionDAOImpl = new OfflineContriCorrectionDAOImpl(TrnDcpsContribution.class, serv.getSessionFactory());
			NewRegDdoDAO dcpsRegisDAO = new NewRegDdoDAOImpl(MstEmp.class, serv.getSessionFactory());
			lObjTrnDcpsContribution = new TrnDcpsContribution();
			lObjMstEmp = new MstEmp();

			/* Gets the Contribution Id */
			Long lLongdcpsContributionId = Long.parseLong(StringUtility.getParameter("dcpsContributionId", request).trim());

			/* Gets the DcpsEmpId from Contribution Id */
			Long lLongdcpsEmpId = OfflineContriCorrectionDAOImpl.getEmpIdforContributionId(lLongdcpsContributionId);

			/* Reads the VO for the found dcpsEmpId */
			if (!(StringUtility.getParameter("dcpsContributionId", request).trim().equalsIgnoreCase(""))) {
				lObjTrnDcpsContribution = (TrnDcpsContribution) OfflineContriCorrectionDAOImpl.read(lLongdcpsContributionId);
				lObjMstEmp = (MstEmp) dcpsRegisDAO.read(lLongdcpsEmpId);
			}

		} catch (Exception ex) {
			gLogger.error(" Error is : " + ex, ex);
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			ex.printStackTrace();
			return objRes;
		}

		/*
		 * Generates the XML response consisting of Employee details and and his
		 * contribution details
		 */
		String lSBStatus = getResponseXMLDocForContriDetails(lObjTrnDcpsContribution, lObjMstEmp).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		objRes.setResultValue(inputMap);
		objRes.setViewName("ajaxData");
		return objRes;

	}

	private StringBuilder getResponseXMLDocForContriDetails(TrnDcpsContribution lObjTrnDcpsContribution, MstEmp lObjMstEmp) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder lStrBldXML = new StringBuilder();
		String lStrContriFrom = sdf.format(lObjTrnDcpsContribution.getStartDate());
		String lStrContriTo = sdf.format(lObjTrnDcpsContribution.getEndDate());

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <empName>");
		lStrBldXML.append(lObjMstEmp.getName());
		lStrBldXML.append("  </empName>");
		lStrBldXML.append("  <dcpsId>");
		lStrBldXML.append(lObjMstEmp.getDcpsId());
		lStrBldXML.append("  </dcpsId>");
		lStrBldXML.append("  <startDate>");
		lStrBldXML.append(lStrContriFrom);
		lStrBldXML.append("  </startDate>");
		lStrBldXML.append("  <endDate>");
		lStrBldXML.append(lStrContriTo);
		lStrBldXML.append("  </endDate>");
		lStrBldXML.append("  <payCommission>");
		lStrBldXML.append(lObjTrnDcpsContribution.getPayCommission());
		lStrBldXML.append("  </payCommission>");
		lStrBldXML.append("  <typeOfPayment>");
		lStrBldXML.append(lObjTrnDcpsContribution.getTypeOfPayment());
		lStrBldXML.append("  </typeOfPayment>");
		lStrBldXML.append("  <basic>");
		lStrBldXML.append(lObjTrnDcpsContribution.getBasicPay());
		lStrBldXML.append("  </basic>");
		lStrBldXML.append("  <DP>");
		lStrBldXML.append(lObjTrnDcpsContribution.getDP());
		lStrBldXML.append("  </DP>");
		lStrBldXML.append("  <DA>");
		lStrBldXML.append(lObjTrnDcpsContribution.getDA());
		lStrBldXML.append("  </DA>");
		lStrBldXML.append("  <contribution>");
		lStrBldXML.append(lObjTrnDcpsContribution.getContribution());
		lStrBldXML.append("  </contribution>");
		lStrBldXML.append("  <dcpsEmpId>");
		lStrBldXML.append(lObjMstEmp.getDcpsEmpId());
		lStrBldXML.append("  </dcpsEmpId>");
		lStrBldXML.append("  <dcpsContributionId>");
		lStrBldXML.append(lObjTrnDcpsContribution.getDcpsContributionId());
		lStrBldXML.append("  </dcpsContributionId>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

}
