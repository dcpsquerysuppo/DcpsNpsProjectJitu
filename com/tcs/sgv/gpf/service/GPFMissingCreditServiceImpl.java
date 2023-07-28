package com.tcs.sgv.gpf.service;

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
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.gpf.dao.GPFDataEntryFormDAO;
import com.tcs.sgv.gpf.dao.GPFDataEntryFormDAOImpl;
import com.tcs.sgv.gpf.dao.GPFMissingCreditDAO;
import com.tcs.sgv.gpf.dao.GPFMissingCreditDAOImpl;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfMissingCredit;
import com.tcs.sgv.gpf.valueobject.MstGpfMonthly;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 18, 2012
 */

public class GPFMissingCreditServiceImpl extends ServiceImpl implements GPFMissingCreditService
{
private final Log gLogger = LogFactory.getLog(getClass());
	
	private HttpServletRequest request = null;
	
	private ServiceLocator serv = null;
	
	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;		

	private void setSessionInfo(Map inputMap) {
		try {
			request = (HttpServletRequest) inputMap.get("requestObj");			
			serv = (ServiceLocator) inputMap.get("serviceLocator");									
			gLngPostId = SessionHelper.getPostId(inputMap);			
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is: "+ e, e);
		}
	}
	
	public ResultObject loadMissingCredit(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		List lLstMonths = null;
		List lLstYears = null;
		List lLstPurposeCatRA = null;
		List lLstDraftData = null;
		String lStrGpfAccNo = "";
		Long lLngBillGroupID = null;
		List lLstRADtls = null;
		String lStrTranId = "";
		Double lDblInstlmntAmt = 0d;
		
		try{
			DcpsCommonDAO lObjCommomDao = new DcpsCommonDAOImpl(SgvcFinYearMst.class,serv.getSessionFactory());
			GPFMissingCreditDAO lObjMissingCreditDAO = new GPFMissingCreditDAOImpl(MstGpfMissingCredit.class,serv.getSessionFactory());
			
			String lStrType = StringUtility.getParameter("type", request);
			
			lLstMonths = lObjCommomDao.getMonths();
			lLstYears = lObjCommomDao.getYears();
			lLstPurposeCatRA = IFMSCommonServiceImpl.getLookupValues("Purpose Category", SessionHelper
					.getLangId(inputMap), inputMap);
			
			if(lStrType.equals("Draft")){
				String lStrEmpCode = StringUtility.getParameter("empCode", request);
				lStrGpfAccNo = lObjMissingCreditDAO.getGpfAccNo(lStrEmpCode);
				lLngBillGroupID = Long.parseLong(lObjMissingCreditDAO.getBillGroupId(lStrEmpCode));
				
				lLstDraftData = lObjMissingCreditDAO.getDraftData(lStrGpfAccNo, lLngBillGroupID);
				
				lLstRADtls = lObjMissingCreditDAO.getInstAmountTrnIdForCurrentRA(lStrGpfAccNo);
				
				if(lLstRADtls!= null && lLstRADtls.size() > 0){
					Object []lObjData = (Object[]) lLstRADtls.get(0);
					lStrTranId = lObjData[0].toString();
					lDblInstlmntAmt = Double.parseDouble(lObjData[1].toString());
				}
				
				inputMap.put("EmpCode", lStrEmpCode);
				inputMap.put("DraftData", lLstDraftData);
				inputMap.put("InstallmentAmount", lDblInstlmntAmt);
			}
						
			inputMap.put("Purpose", lLstPurposeCatRA);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("lLstYears", lLstYears);
			resObj.setResultValue(inputMap);
			resObj.setViewName("MissingCredit");
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadMissingCredit");
		}
		return resObj;
	}
	
	public ResultObject loadMissingCreditWorkList(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		List lLstDraftData = null;
		
		try{
			GPFMissingCreditDAO lObjMissingCreditDAO = new GPFMissingCreditDAOImpl(MstGpfMissingCredit.class,serv.getSessionFactory());
			
			lLstDraftData = lObjMissingCreditDAO.loadDraftDataWorkList(gLngPostId);
			inputMap.put("DraftList", lLstDraftData);
			resObj.setResultValue(inputMap);
			resObj.setViewName("MissingCreditWorkList");
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadMissingCreditWorkList");
		}
		return resObj;
	}
	
	public ResultObject saveMissingCredits(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		
		String lStrSevaarthId = "";
		String lStrGpfAccNo = "";
		String lStrType = "";
		String lStrStatus = "";		
		String lStrCreateOrUpdate = "";
		String lStrChkData = "";
		String lSBStatus = "";
		String lStrMonth = "";
		String lStrYear = "";
		String lStrFinYearCode = "";
		String lStrMonthName = "";
		String []lArrStrChkData = null;
		
		MstGpfMissingCredit lObjMstGpfMissingCredit = null;
		
		Long lLngMstGpfMissingCreditId = null;
		Long lLngBillGroupID = null;
		
		List lLstAllData = null;
		
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		try{
			GPFMissingCreditDAO lObjMissingCreditDAO = new GPFMissingCreditDAOImpl(MstGpfMissingCredit.class,serv.getSessionFactory());
			ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(MstGpfMissingCredit.class, serv.getSessionFactory());
			
			String []lArrStrAmount = StringUtility.getParameterValues("txtAmount", request);
			String []lArrStrSubscription = StringUtility.getParameterValues("txtSubscription", request);
			String []lArrStrMonth = StringUtility.getParameterValues("cmbMonth", request);
			String []lArrStrYear = StringUtility.getParameterValues("cmbYear", request);
			String []lArrStrVoucherNo = StringUtility.getParameterValues("txtVoucherNo", request);
			String []lArrStrVoucherDate = StringUtility.getParameterValues("txtVoucherDate", request);
			String []lArrStrMonthlyId = StringUtility.getParameterValues("hidMonthlyId", request);
			String lStrTranID = StringUtility.getParameter("hidTransactionId", request);
			lStrSevaarthId = StringUtility.getParameter("empCode", request);
			lStrType = StringUtility.getParameter("type", request);
			
			lStrGpfAccNo = lObjMissingCreditDAO.getGpfAccNo(lStrSevaarthId);
			
			lLngBillGroupID = Long.parseLong(lObjMissingCreditDAO.getBillGroupId(lStrSevaarthId));
			
			if(lStrType.equals("Draft")){
				lStrStatus = "D";
			}else if(lStrType.equals("Save")){
				lStrStatus = "S";
			}
			
			lStrChkData = lObjMissingCreditDAO.chkForApprovedReq(lStrGpfAccNo, lArrStrYear, lArrStrMonth, lLngBillGroupID);
			
			if(lStrChkData.equals("Y")){
				
				lLstAllData = lObjMissingCreditDAO.getDraftData(lStrGpfAccNo, lLngBillGroupID);
				for(Integer lIntCnt=0; lIntCnt < lLstAllData.size(); lIntCnt++)
				{
					Object []lobj = (Object[]) lLstAllData.get(lIntCnt);
					lObjMstGpfMissingCredit = new MstGpfMissingCredit();
					lObjMstGpfMissingCredit = (MstGpfMissingCredit) lObjMissingCreditDAO.read(Long.parseLong(lobj[7].toString()));
					lObjMstGpfMissingCredit.setStatus("X");
					lObjMissingCreditDAO.update(lObjMstGpfMissingCredit);
				}
				
				for(int lIntCnt=0;lIntCnt<lArrStrAmount.length;lIntCnt++)
				{
					lObjMstGpfMissingCredit = new MstGpfMissingCredit();
					lStrCreateOrUpdate = "";
					
					//lLngMstGpfMonthlyId = lObjMissingCreditDAO.getMonthlyId(lStrGpfAccNo, lArrStrYear[lIntCnt], lArrStrMonth[lIntCnt], lLngBillGroupID, "MD");
					
					if(lArrStrMonthlyId[lIntCnt] != null && lArrStrMonthlyId[lIntCnt].trim().length() > 0){
						lLngMstGpfMissingCreditId = Long.parseLong(lArrStrMonthlyId[lIntCnt]);
					}else{
						lLngMstGpfMissingCreditId = null;
					}
					
					if(lLngMstGpfMissingCreditId == null){
						lLngMstGpfMissingCreditId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_MISSING_CREDIT", inputMap);
						lObjMstGpfMissingCredit.setMstGpfMissingCreditId(lLngMstGpfMissingCreditId);
						lObjMstGpfMissingCredit.setBillgroupId(lLngBillGroupID);
						lObjMstGpfMissingCredit.setGpfAccNo(lStrGpfAccNo);
						lStrCreateOrUpdate = "create";
					}else{
						lObjMstGpfMissingCredit = (MstGpfMissingCredit) lObjMissingCreditDAO.read(lLngMstGpfMissingCreditId);
						lStrCreateOrUpdate = "update";
					}	
					if(lArrStrYear[lIntCnt] != null && lArrStrYear[lIntCnt].trim().length() > 0)
					{
						lObjMstGpfMissingCredit.setFinYearId(Long.parseLong(lArrStrYear[lIntCnt]));
					}
					if(lArrStrMonth[lIntCnt] != null && lArrStrMonth[lIntCnt].trim().length() > 0)
					{
						lObjMstGpfMissingCredit.setMonthId(Long.parseLong(lArrStrMonth[lIntCnt]));
					}
					if(lArrStrAmount[lIntCnt] != null && lArrStrAmount[lIntCnt].trim().length() > 0)
					{
						lObjMstGpfMissingCredit.setAdvanceRecovery(Double.parseDouble(lArrStrAmount[lIntCnt]));
						lObjMstGpfMissingCredit.setAdvanceTrnId(lStrTranID);
					}else{
						lObjMstGpfMissingCredit.setAdvanceRecovery(null);
						lObjMstGpfMissingCredit.setAdvanceTrnId(null);
					}
					if(lArrStrSubscription[lIntCnt] != null && lArrStrSubscription[lIntCnt].trim().length() > 0)
					{
						lObjMstGpfMissingCredit.setRegularSubscription(Double.parseDouble(lArrStrSubscription[lIntCnt]));
					}else{
						lObjMstGpfMissingCredit.setRegularSubscription(null);
					}
					if(lArrStrVoucherNo[lIntCnt] != null && lArrStrVoucherNo[lIntCnt].trim().length() > 0)
					{
						lObjMstGpfMissingCredit.setVoucherNo(lArrStrVoucherNo[lIntCnt]);
					}
					if(lArrStrVoucherDate[lIntCnt] != null && lArrStrVoucherDate[lIntCnt].trim().length() > 0)
					{
						lObjMstGpfMissingCredit.setVoucherDate(lObjDateFormat.parse(lArrStrVoucherDate[lIntCnt]));
					}
					
					lObjMstGpfMissingCredit.setStatus(lStrStatus);
					lObjMstGpfMissingCredit.setCreatedUserId(gLngUserId);
					lObjMstGpfMissingCredit.setCreatedPostId(gLngPostId);
					lObjMstGpfMissingCredit.setCreatedDate(DBUtility.getCurrentDateFromDB());
					
					if(lStrCreateOrUpdate.equals("create")){
						lObjMissingCreditDAO.create(lObjMstGpfMissingCredit);
					}else if(lStrCreateOrUpdate.equals("update")){
						lObjMissingCreditDAO.update(lObjMstGpfMissingCredit);
					}
				}
				lSBStatus = getResponseXMLDoc(lStrType).toString();
				
			}else{
				lArrStrChkData = lStrChkData.split(",");
				lStrMonth = lArrStrChkData[0];
				lStrYear = lArrStrChkData[1];
				
				lStrMonthName = lObjMissingCreditDAO.getMonthNameFromID(lStrMonth);
				lStrFinYearCode = lObjScheduleGenerationDAO.getFinYearCodeForFinYearId(Long.parseLong(lStrYear));
				lSBStatus = getResponseXMLDoc(lStrMonthName+","+lStrFinYearCode).toString();
			}
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;
			
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadPurposeCongif");
		}
		return resObj;
	}
	
	public ResultObject getEmployeeNameFromEmpCode(Map<String, Object> inputMap) {
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"FAIL");
		
		String lStrName = "";		
		String lStrEmpCode="";
		String lSBStatus="";
		String lStrStatusFlag="";
		String lStrGpfAccNo = "";
		List lLstEmpDtls = null;
		List lLstRADtls = null;
		Double lDblInstlmntAmt = null;
		String lStrTranId = "";
		
		GPFDataEntryFormDAO lObjGpfDataEntryFormDAO = new GPFDataEntryFormDAOImpl(MstEmp.class, serv.getSessionFactory());
		GPFMissingCreditDAO lObjMissingCreditDAO = new GPFMissingCreditDAOImpl(MstGpfMonthly.class,serv.getSessionFactory());
		
		try{
			lStrEmpCode = StringUtility.getParameter("EmpCode", request);
			
			if(lStrEmpCode != ""){
				lLstEmpDtls = lObjGpfDataEntryFormDAO.getEmployeeNameAndPay(lStrEmpCode, gStrLocationCode);
				if(lLstEmpDtls != null && lLstEmpDtls.size() > 0){
					Object [] lObj = (Object[]) lLstEmpDtls.get(0);
					lStrName = lObj[0].toString();					
					
					lStrStatusFlag = lObjGpfDataEntryFormDAO.chkEntryForSevaarthId(lStrEmpCode);
					
					lStrGpfAccNo = lObjGpfDataEntryFormDAO.getGpfAccNo(lStrEmpCode);
					
					lLstRADtls = lObjMissingCreditDAO.getInstAmountTrnIdForCurrentRA(lStrGpfAccNo);
					
					if(lLstRADtls!= null && lLstRADtls.size() > 0){
						Object []lObjData = (Object[]) lLstRADtls.get(0);
						lStrTranId = lObjData[0].toString();
						lDblInstlmntAmt = Double.parseDouble(lObjData[1].toString());
					}
				}else{
					lStrName = "Invalid";
				}
			}
		}
		catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getEmployeeCode");			
		}
		
			lSBStatus = getResponseXMLDocEmployeeCode(lStrName,lDblInstlmntAmt,lStrStatusFlag,lStrTranId).toString();
		
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append("<![CDATA[");
		lStrBldXML.append(lStrFlag);
		lStrBldXML.append("]]>");
		lStrBldXML.append("</FLAG>");		
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	private StringBuilder getResponseXMLDocEmployeeCode(String lStrEmployeeCode, Double lDblBasicPay, 
			String lStrStatusFlag, String lStrTranId) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<EMPCODE>");
		lStrBldXML.append(lStrEmployeeCode);
		lStrBldXML.append("</EMPCODE>");
		lStrBldXML.append("<BASICPAY>");
		lStrBldXML.append(lDblBasicPay);
		lStrBldXML.append("</BASICPAY>");
		lStrBldXML.append("<STATUS>");
		lStrBldXML.append(lStrStatusFlag);
		lStrBldXML.append("</STATUS>");
		lStrBldXML.append("<TRNID>");
		lStrBldXML.append(lStrTranId);
		lStrBldXML.append("</TRNID>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
