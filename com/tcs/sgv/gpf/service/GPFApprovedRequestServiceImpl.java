package com.tcs.sgv.gpf.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAO;
import com.tcs.sgv.gpf.dao.GPFAdvanceProcessDAOImpl;
import com.tcs.sgv.gpf.dao.GPFApprovedRequestDAO;
import com.tcs.sgv.gpf.dao.GPFApprovedRequestDAOImpl;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfAdvance;
import com.tcs.sgv.gpf.valueobject.MstGpfBillDtls;

/**
 * @author 397138
 * 
 */
public class GPFApprovedRequestServiceImpl extends ServiceImpl implements GPFApprovedRequestService {
	Log glogger = LogFactory.getLog(getClass());

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
			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
		} catch (Exception e) {
			glogger.error("Error is :" + e, e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.service.GPFApprovedRequestService#gpfApprovedList(java
	 * .util.Map)
	 */
	public ResultObject gpfApprovedList(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

		try {
			setSessionInfo(inputMap);
			List lGpfApprovedList = new ArrayList();
			GPFRequestProcessDAO lObjGPFReqProcess = new GPFRequestProcessDAOImpl(null, serv.getSessionFactory());
			// String lStrDdoCode =
			// lObjGPFReqProcess.getDdoCodeForDDO(gLngPostId);
			String lStrLocationCode = lObjGPFReqProcess.getLocationCodeOfUser(gLngPostId);
			GPFApprovedRequestDAO lObjGPFApprovedRequestDAO = new GPFApprovedRequestDAOImpl(MstGpfAdvance.class, serv
					.getSessionFactory());
			lGpfApprovedList = lObjGPFApprovedRequestDAO.getGPFApprovedRequestList(lStrLocationCode);
			inputMap.put("gpfApprovedList", lGpfApprovedList);
			inputMap.put("totalRecords", lGpfApprovedList.size());
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
		}

		resObj.setResultValue(inputMap);
		resObj.setViewName("GPFApprovedRequest");

		return resObj;
	}
	
	
	public ResultObject GenerateBillData(Map<String, Object> inputMap)
	{	
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		
		Long lLngBillDtlsId = null;
		
		
		try{
			setSessionInfo(inputMap);
			GPFApprovedRequestDAO lObjGPFApprovedRequestDAO = new GPFApprovedRequestDAOImpl(MstGpfBillDtls.class, serv.getSessionFactory());			
			SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("yyyy-mm-dd");
			LoginDetails lObjLoginVO = (LoginDetails) inputMap.get("lObjLoginVO");
			
			String lStrGpfAccNo = (String) inputMap.get("gpfAccNo");
			String lStrTransactionId = (String) inputMap.get("transactionId");
			String lStrAdvanceType = (String) inputMap.get("AdvanceType");
			String lStrOrderNo = (String) inputMap.get("orderNo");
			String lStrOrderDate = (String) inputMap.get("orderDate");
			Double lDblOpeningBalc = (Double) inputMap.get("openingBalc");
			Double lDblRegularSub = (Double) inputMap.get("regularSub");
			Double lDblAdvanceRecovry = (Double) inputMap.get("advanceRecovery");
			Double lDblAdvanceSanctioned = (Double) inputMap.get("advanceSanctioned");
			
			int billNo = lObjGPFApprovedRequestDAO.getMaxOfBillNo();
			Map baseLoginMap = new HashMap();
			
			baseLoginMap.put("userId", lObjLoginVO.getUser().getUserId());
			baseLoginMap.put("langId", lObjLoginVO.getLangId());;
			inputMap.put("baseLoginMap", baseLoginMap);
			
			lLngBillDtlsId = IFMSCommonServiceImpl.getNextSeqNum("MST_GPF_BILL_DTLS", inputMap);
			
			MstGpfBillDtls lObjMstGpfBillDtls = new MstGpfBillDtls();
			lObjMstGpfBillDtls.setBillDtlsId(lLngBillDtlsId);
			lObjMstGpfBillDtls.setLocationCode(lObjLoginVO.getLocation().getLocationCode());
			lObjMstGpfBillDtls.setGpfAccNo(lStrGpfAccNo);
			lObjMstGpfBillDtls.setTransactionId(lStrTransactionId);
			lObjMstGpfBillDtls.setBillGeneratedDate(DBUtility.getCurrentDateFromDB());
			lObjMstGpfBillDtls.setAdvacnceType(lStrAdvanceType);
			lObjMstGpfBillDtls.setStatusFlag(0);
			lObjMstGpfBillDtls.setOrderNo(lStrOrderNo);
			if(lStrOrderDate != null && !lStrOrderDate.equals("")){
				lObjMstGpfBillDtls.setOrderDate(lObjSimpleDate.parse(lStrOrderDate.substring(0,10)));
			}
			lObjMstGpfBillDtls.setCreatedPostId(gLngPostId);
			lObjMstGpfBillDtls.setCreatedUserId(gLngUserId);
			lObjMstGpfBillDtls.setCreatedDate(DBUtility.getCurrentDateFromDB());
			lObjMstGpfBillDtls.setOpeningBalance(lDblOpeningBalc);
			lObjMstGpfBillDtls.setRegularSubscription(lDblRegularSub);
			lObjMstGpfBillDtls.setAdvanceRecovery(lDblAdvanceRecovry);
			lObjMstGpfBillDtls.setAdvanceSanctioned(lDblAdvanceSanctioned);
			String lStrBillNo = "";
			billNo  = billNo + 1;
			if(billNo < 10)
				lStrBillNo = "000" + billNo ;
			else if(billNo < 100)
				lStrBillNo = "00" + billNo ;
			else if(billNo < 1000)
				lStrBillNo = "0" + billNo ;
			else if(billNo < 10000)
				lStrBillNo = ""+ billNo ;
			lObjMstGpfBillDtls.setBillNo(lStrBillNo);
			lObjGPFApprovedRequestDAO.create(lObjMstGpfBillDtls);

			inputMap.put("ajaxKey", "Success");
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");			
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
		}

		return resObj;
	}
	
	
	public ResultObject LoadGpfBillWorkList(Map inputMap) 
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		List lLstBillData = null;
		
		try {
			setSessionInfo(inputMap);
			GPFApprovedRequestDAO lObjGPFApprovedRequestDAO = new GPFApprovedRequestDAOImpl(MstGpfBillDtls.class, serv.getSessionFactory());
			
			lLstBillData = lObjGPFApprovedRequestDAO.LoadBillWorkList(gStrLocationCode);
			
			inputMap.put("BillDetails", lLstBillData);
			resObj.setResultValue(inputMap);
			resObj.setViewName("GPFBillWorkList");
		} catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
		}
		
		return resObj;
	}
	
	
	public ResultObject ApproveBill(Map inputMap)
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		
		List lLstAdvanceDtls = null;
		
		try {
			setSessionInfo(inputMap);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			GPFApprovedRequestDAO lObjGPFApprovedRequestDAO = new GPFApprovedRequestDAOImpl(MstGpfBillDtls.class, serv.getSessionFactory());
			
			String lStrOrderNo = StringUtility.getParameter("orderNo", request);
			String lStrOrderDate = StringUtility.getParameter("orderDate", request);
			String lStrGpfAccNo = StringUtility.getParameter("gpfAccNo", request);
			String lStrRequestName = StringUtility.getParameter("reqType", request);
			String lStrVoucherNo = StringUtility.getParameter("voucherNo", request);
			String lStrVoucherDate = StringUtility.getParameter("voucherDate", request);
			String lStrAdvanceTranId = StringUtility.getParameter("transactionId", request);
			String lStrBillDtlId = StringUtility.getParameter("billDtlId", request);
			
			// create advance data in payroll module			
			Long lLngAdvanceAccountNo = 212121l;
			if (lStrRequestName.equals("RA")) {
				
				lLstAdvanceDtls = lObjGPFApprovedRequestDAO.getAdvanceDetails(lStrAdvanceTranId);
				Date lDtCurDate = DBUtility.getCurrentDateFromDB();
				
				if(lLstAdvanceDtls != null && lLstAdvanceDtls.size() > 0){
					
					Object []lObj = (Object[]) lLstAdvanceDtls.get(0);					
					
					Map payrollServiceMap = new HashMap();
					payrollServiceMap.put("advTypeId", 55);
					payrollServiceMap.put("advDate", simpleDateFormat.format(lDtCurDate));
					payrollServiceMap.put("advSancOrderNo", lStrOrderNo);
					payrollServiceMap.put("advSancOrderDate", lStrOrderDate);
					payrollServiceMap.put("prinIntFlag", "P");
					payrollServiceMap.put("principalAmount", Math.round(Double.parseDouble(lObj[0].toString())));
					payrollServiceMap.put("installmentNo", Integer.parseInt(Long.toString(Math.round(Double.parseDouble(lObj[1].toString())))));
					payrollServiceMap.put("installmentEMI", Integer.parseInt(Long.toString(Math.round(Double.parseDouble(lObj[2].toString())))));
					payrollServiceMap.put("recoveredAmount", 0);
					payrollServiceMap.put("recoveredInstallment", 0);
					payrollServiceMap.put("oddInstallment", Integer.parseInt(Long.toString(Math.round(Double.parseDouble(lObj[1].toString())))));
					payrollServiceMap.put("oddInstallmentAmount", Integer.parseInt(Long.toString(Math.round(Double.parseDouble(lObj[3].toString())))));
					GPFAdvanceProcessDAO lObjGPFAdvanceProcessDAO = new GPFAdvanceProcessDAOImpl(null, serv
							.getSessionFactory());
					Long lLngOrgEmpId = lObjGPFAdvanceProcessDAO.getOrgEmpIdForGpfAccNo(lStrGpfAccNo);
					payrollServiceMap.put("orgEmpId", lLngOrgEmpId);
					if (lObj[4] != null) {
						payrollServiceMap.put("advExistFlag", true);
					} else {
						payrollServiceMap.put("advExistFlag", false);
					}
					payrollServiceMap.put("advVoucherNo", Long.parseLong(lStrVoucherNo));
					payrollServiceMap.put("advVoucherDate", lStrVoucherDate);
					payrollServiceMap.put("advAccountNo", lLngAdvanceAccountNo);
					
					
					
					inputMap.put("payrollServiceMap", payrollServiceMap);
					
					
					
					resObj = serv.executeService("insertIntegratedPFAdvances", inputMap);
					
					if(resObj.getResultCode() == -1){						
						String lSBStatus = getResponseXMLDoc("Error").toString();
						String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

						inputMap.put("ajaxKey", lStrResult);
						resObj.setResultValue(inputMap);
						resObj.setViewName("ajaxData");
						return resObj;
					}
				}

			}
			// end of payroll module entry
			
			MstGpfBillDtls lObjMstGpfBillDtls = new MstGpfBillDtls();
			lObjMstGpfBillDtls = (MstGpfBillDtls) lObjGPFApprovedRequestDAO.read(Long.parseLong(lStrBillDtlId));
			lObjMstGpfBillDtls.setStatusFlag(1);
			lObjMstGpfBillDtls.setVoucherNo(lStrVoucherNo);
			lObjMstGpfBillDtls.setVoucherDate(simpleDateFormat.parse(lStrVoucherDate));
			lObjGPFApprovedRequestDAO.update(lObjMstGpfBillDtls);
						
			String lSBStatus = getResponseXMLDoc("Approved").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
			
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
			System.out.println("Error is:" +e);
		}
		
		return resObj;
	}
	
	
	public ResultObject RejectBill(Map inputMap)
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		
		List lLstAdvanceDtls = null;
		
		try {
			setSessionInfo(inputMap);
			GPFApprovedRequestDAO lObjGPFApprovedRequestDAO = new GPFApprovedRequestDAOImpl(MstGpfBillDtls.class, serv.getSessionFactory());
			
			String lStrBillDtlId = StringUtility.getParameter("billDtlId", request);
			
			MstGpfBillDtls lObjMstGpfBillDtls = new MstGpfBillDtls();
			lObjMstGpfBillDtls = (MstGpfBillDtls) lObjGPFApprovedRequestDAO.read(Long.parseLong(lStrBillDtlId));
			lObjMstGpfBillDtls.setStatusFlag(2);
			lObjGPFApprovedRequestDAO.update(lObjMstGpfBillDtls);
			
			String lSBStatus = getResponseXMLDoc("Rejected").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		}catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(glogger, resObj, e, "Error is: ");
		}
		
		return resObj;
	}
	
	
	private StringBuilder getResponseXMLDoc(String lStrFlag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<FLAG>");
		lStrBldXML.append(lStrFlag);
		lStrBldXML.append("</FLAG>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

}
