package com.tcs.sgv.gpf.service;

import java.util.Date;
import java.util.Iterator;
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
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAO;
import com.tcs.sgv.gpf.dao.GPFArrearsManualEntryDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfArrearsYearly;

public class GPFArrearsManualEntryServiceImpl extends ServiceImpl implements GPFArrearsManualEntryService {
	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Location */
	String gStrUserLocation = null;

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}

	}

	public ResultObject popUpEmpDtlsGroup(Map inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lEmpArrearsList = null;
		Object[] lObjArrPrvValue = null;

		try {
			String lStrEmpGroup = StringUtility.getParameter("selectedGroup", request);
			String[] lStrArrEmpGroup = lStrEmpGroup.split("~");
			GPFArrearsManualEntryDAO lObjArrearsManualEntryDAO = new GPFArrearsManualEntryDAOImpl(null, serv
					.getSessionFactory());
			List lLstMonth = lObjArrearsManualEntryDAO.getMonths();
			List lLstYear = lObjArrearsManualEntryDAO.getYears();
			GPFRequestProcessDAO lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			String lStrDdoCode = lObjGPFRequestProcessDAO.getDdoCodeForDDO(gLngPostId);
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serv
					.getSessionFactory());

			if (!"".equals(lStrEmpGroup.trim())) {
				lEmpArrearsList = lObjArrearsManualEntryDAO.getEmpArrearsDtls(lStrArrEmpGroup, lStrDdoCode);
				inputMap.put("EmpGroup", lStrArrEmpGroup);
				inputMap.put("EmpArrearsList", lEmpArrearsList);
				inputMap.put("totalRecords", lEmpArrearsList.size());
			}

			if (lEmpArrearsList != null && !lEmpArrearsList.isEmpty()) {
				lObjArrPrvValue = new Object[lEmpArrearsList.size()];
				for (Integer lIntCnt = 0; lIntCnt < lEmpArrearsList.size(); lIntCnt++) {
					Object[] lArrObjEmp = (Object[]) lEmpArrearsList.get(lIntCnt);
					String lStrAccNo = (String) lArrObjEmp[2];
					List lLstLastScheduleGenData = lObjScheduleGenerationDAO.getLastScheduleData(lStrAccNo);
					if (!lLstLastScheduleGenData.isEmpty()) {
						lObjArrPrvValue[lIntCnt] = lLstLastScheduleGenData.get(0);
					} else {
						lObjArrPrvValue[lIntCnt] = null;
					}
				}
			}

			inputMap.put("PrvValue", lObjArrPrvValue);
			inputMap.put("EntryType", 'A');
			inputMap.put("monthList", lLstMonth);
			inputMap.put("yearList", lLstYear);
			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFArrearsManualEntry");

		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getDigiSig: ");			
		}
		return resObj;
	}

	public ResultObject popUpEmpArrearsDtls(Map inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			String lStrSevaarthID = StringUtility.getParameter("sevaarthId", request);
			String alertMessage = "";
			GPFArrearsManualEntryDAO lObjArrearsManualEntryDAO = new GPFArrearsManualEntryDAOImpl(null, serv
					.getSessionFactory());
			GPFRequestProcessDAO lObjGPFRequestProcessDAO = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			String lStrDdoCode = lObjGPFRequestProcessDAO.getDdoCodeForDDO(gLngPostId);
			List lEmpDtlsList = lObjArrearsManualEntryDAO.getEmpDtls(lStrSevaarthID, lStrDdoCode);
			if (lEmpDtlsList != null && !lEmpDtlsList.isEmpty()) {
				String lStrEmpName = "";
				String lStrGpfAccNo = "";
				String lStrOfficeName = "";
				Iterator it = lEmpDtlsList.iterator();
				Object lObj[] = new Object[lEmpDtlsList.size()];
				while (it.hasNext()) {
					lObj = (Object[]) it.next();
				}
				if (lObj[0] != null) {
					lStrEmpName = (String) lObj[0];
				}
				if (lObj[1] != null) {
					lStrGpfAccNo = (String) lObj[1];
				}
				if (lObj[2] != null) {
					lStrOfficeName = (String) lObj[2];
				}
				List lLstMonth = lObjArrearsManualEntryDAO.getMonths();
				List lLstYear = lObjArrearsManualEntryDAO.getYears();
				Integer lIntInstNo = 0;
				List lEmpArrearsList = lObjArrearsManualEntryDAO.getNextInstallmentNo(lStrGpfAccNo);
				if (lEmpArrearsList != null && !lEmpArrearsList.isEmpty() && lEmpArrearsList.get(0) != null) {
					Object[] lArrObj = (Object[]) lEmpArrearsList.get(0);
					lIntInstNo = (Integer) lArrObj[0];
					// Long lLngPreYearId = (Long) lArrObj[1];

					// inputMap.put("PreYearId", lLngPreYearId);
				}
				inputMap.put("InstallmentNo", lIntInstNo + 1);
				Long lLngLastScheduleGenMonth = 0l;
				Long lLngPreYearId = 0l;
				ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serv
						.getSessionFactory());
				List lLstLastScheduleGenData = lObjScheduleGenerationDAO.getLastScheduleData(lStrGpfAccNo);
				if (lLstLastScheduleGenData != null && !lLstLastScheduleGenData.isEmpty()) {
					Object[] lArrObj = (Object[]) lLstLastScheduleGenData.get(0);
					lLngLastScheduleGenMonth = (Long) lArrObj[0];
					lLngPreYearId = (Long) lArrObj[1];
				}
				inputMap.put("PreYearId", lLngPreYearId);
				inputMap.put("PreMonthId", lLngLastScheduleGenMonth);
				inputMap.put("SevaarthID", lStrSevaarthID);
				inputMap.put("EmpName", lStrEmpName);
				inputMap.put("GpfAccNo", lStrGpfAccNo);
				inputMap.put("OfficeName", lStrOfficeName);

				inputMap.put("monthList", lLstMonth);
				inputMap.put("yearList", lLstYear);
				inputMap.put("EntryType", 'S');
				resObj.setResultValue(inputMap);
				resObj.setViewName("GPFArrearsManualEntry");
			} else {
				alertMessage = "InvalidSevaarthId";
				inputMap.put("alertMessage", alertMessage);
				resObj.setResultValue(inputMap);
				resObj.setViewName("GPFArrearsEntryForm");
			}
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getDigiSig: ");
		}
		return resObj;
	}

	public ResultObject approveGPFArrears(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		boolean lBlFlag = false;
		StringBuilder lStrErrorEmps = new StringBuilder();
		try {
			setSessionInfo(inputMap);
			new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			GPFArrearsManualEntryDAO lObjArrearsYearlyDAO = new GPFArrearsManualEntryDAOImpl(MstGpfArrearsYearly.class,
					serv.getSessionFactory());
			String lStrEntryType = StringUtility.getParameter("entryType", request);

			if (lStrEntryType.equals("multiple")) {
				String lStrGpfAccNo = StringUtility.getParameter("gpfAccNo", request);
				String lStrYearId = StringUtility.getParameter("yearId", request);
				String lStrMonthId = StringUtility.getParameter("monthId", request);
				String lStrAmount = StringUtility.getParameter("amount", request);
				String lStrInstAmount = StringUtility.getParameter("instAmount", request);
				String[] lStrArrYearId = lStrYearId.split("~");
				String[] lStrArrGpfAccNo = lStrGpfAccNo.split("~");
				String[] lStrArrMonthId = lStrMonthId.split("~");
				String[] lStrArrAmount = lStrAmount.split("~");
				String[] lStrArrInstAmount = lStrInstAmount.split("~");
				Long lLngYearId = 0l;
				Long lLngMonthId = 0l;
				Double lDblPreCloseBal = 0d;
				Integer lIntInstNo = 0;
				Long lLngPreYearId = 0l;
				List lEmpArrearsList = null;

				for (int index = 0; index < lStrArrYearId.length; index++) {

					lLngYearId = Long.parseLong(lStrArrYearId[index]);
					lLngMonthId = Long.parseLong(lStrArrMonthId[index]);
					lEmpArrearsList = lObjArrearsYearlyDAO.getNextInstallmentNo(lStrArrGpfAccNo[index]);
					if (lEmpArrearsList != null && !lEmpArrearsList.isEmpty() && lEmpArrearsList.get(0) != null) {
						Object[] lArrObj = (Object[]) lEmpArrearsList.get(0);
						lIntInstNo = (Integer) lArrObj[0];
						lLngPreYearId = (Long) lArrObj[1];
					}
					if (lLngPreYearId != 0 && lLngYearId <= lLngPreYearId) {
						lStrErrorEmps.append(lStrArrGpfAccNo[index] + ",");
						continue;
					}
					MstGpfArrearsYearly lObjArrearsYearly = new MstGpfArrearsYearly();
					Long lLngGpfArrearsYearlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ARREARS_YEARLY",
							inputMap);
					lObjArrearsYearly.setGpfArrearsYearlyId(lLngGpfArrearsYearlyId);
					lObjArrearsYearly.setGpfAccNo(lStrArrGpfAccNo[index]);
					lObjArrearsYearly.setYearId(lLngYearId);
					lObjArrearsYearly.setMonthId(lLngMonthId);
					// lObjArrearsYearly.setDdoCode(lStrDdoCode);
					lDblPreCloseBal = lObjArrearsYearlyDAO.getPreviousCloseBal(lStrArrGpfAccNo[index], lIntInstNo);
					lObjArrearsYearly.setOpenBalance(lDblPreCloseBal);
					lObjArrearsYearly.setTotalAmount(Double.parseDouble(lStrArrAmount[index]));
					lObjArrearsYearly.setArrearAmount(Double.parseDouble(lStrArrInstAmount[index]));
					lObjArrearsYearly.setInstallmentNo(lIntInstNo + 1);
					lObjArrearsYearly.setCreatedDate(gDtCurDate);
					lObjArrearsYearly.setCreatedPostId(gLngPostId);
					lObjArrearsYearly.setCreatedUserId(gLngUserId);
					lObjArrearsYearlyDAO.create(lObjArrearsYearly);
				}
			} else if (lStrEntryType.equals("single")) {
				String lStrGpfAccNo = StringUtility.getParameter("txtGpfAccNo", request);
				String lStrYearId = StringUtility.getParameter("cmbYearForEmp", request);
				String lStrMonthId = StringUtility.getParameter("cmbMonthForEmp", request);
				String lStrTotalAmount = StringUtility.getParameter("txtTotalAmountForEmp", request);
				String lStrInstallmentAmount = StringUtility.getParameter("txtInstallmentAmountForEmp", request);
				Integer lIntInstallmentNo = Integer.parseInt(StringUtility.getParameter("txtInstallmentNo", request));

				Double lDblPreCloseBal = 0d;
				if (lIntInstallmentNo > 1) {
					lObjArrearsYearlyDAO.getPreviousCloseBal(lStrGpfAccNo, lIntInstallmentNo - 1);
				}
				Long lLngMonthId = Long.parseLong(lStrMonthId);
				Long lLngYearId = Long.parseLong(lStrYearId);

				MstGpfArrearsYearly lObjArrearsYearly = new MstGpfArrearsYearly();
				Long lLngGpfArrearsYearlyId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_ARREARS_YEARLY", inputMap);
				lObjArrearsYearly.setGpfArrearsYearlyId(lLngGpfArrearsYearlyId);
				lObjArrearsYearly.setGpfAccNo(lStrGpfAccNo);
				lObjArrearsYearly.setYearId(lLngYearId);
				lObjArrearsYearly.setMonthId(lLngMonthId);
				// lObjArrearsYearly.setDdoCode(lStrDdoCode);
				lObjArrearsYearly.setTotalAmount(Double.parseDouble(lStrTotalAmount));
				lObjArrearsYearly.setOpenBalance(lDblPreCloseBal);
				lObjArrearsYearly.setArrearAmount(Double.parseDouble(lStrInstallmentAmount));
				lObjArrearsYearly.setInstallmentNo(lIntInstallmentNo);
				// lObjArrearsYearly.setInterestrate(8F);
				// lObjArrearsYearly.setCloseBalance(lLngCloseBalance +
				// Long.parseLong(lStrInstallmentAmount));
				lObjArrearsYearly.setCreatedDate(gDtCurDate);
				lObjArrearsYearly.setCreatedPostId(gLngPostId);
				lObjArrearsYearly.setCreatedUserId(gLngUserId);
				lObjArrearsYearlyDAO.create(lObjArrearsYearly);
			}

			lBlFlag = true;
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error is: ");
			return resObj;
		}
		String lSBStatus = getResponseXMLDoc(lBlFlag, lStrErrorEmps).toString();
		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

		inputMap.put("ajaxKey", lStrResult);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ajaxData");
		return resObj;
	}

	private StringBuilder getResponseXMLDoc(boolean flag, StringBuilder lStrErrorEmps) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("  <FLAG>");
		lStrBldXML.append(flag);
		lStrBldXML.append("  </FLAG>");
		lStrBldXML.append("  <ERREMP>");
		lStrBldXML.append(lStrErrorEmps);
		lStrBldXML.append("  </ERREMP>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

}
