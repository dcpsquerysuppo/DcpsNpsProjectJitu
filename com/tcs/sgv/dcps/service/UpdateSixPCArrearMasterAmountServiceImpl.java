package com.tcs.sgv.dcps.service;

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
import com.tcs.sgv.dcps.dao.UpdateSixPCArrearMasterAmountDAO;
import com.tcs.sgv.dcps.dao.UpdateSixPCArrearMasterAmountDAOImpl;
import com.tcs.sgv.dcps.valueobject.RltDcpsSixPCYearly;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 23, 2012
 */

public class UpdateSixPCArrearMasterAmountServiceImpl extends ServiceImpl implements UpdateSixPCArrearMasterAmountService
{
	private final Log gLogger = LogFactory.getLog(getClass());

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

	public ResultObject getSixPCArrearDtlsFromEmpCode(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"FAIL");
		
		String lStrName = "";
		Double lDblOldAmount = null;
		String lSBStatus="";
		Double lDblPaidAmount = null;
		Long lLngDcpsEmpId = null;
		List lLstEmpDtls = null;
		String lStrPayCommision = "";
		
		try{
			UpdateSixPCArrearMasterAmountDAO lObjUpdateSixPCArrear = new UpdateSixPCArrearMasterAmountDAOImpl(RltDcpsSixPCYearly.class, serv.getSessionFactory());
			
			String lStrEmpCode = StringUtility.getParameter("empCode", request).trim();
			
			lLstEmpDtls = lObjUpdateSixPCArrear.getDataFromEmpCode(lStrEmpCode);
			
			if(lLstEmpDtls != null && lLstEmpDtls.size() > 0){
				Object []lObj = (Object[]) lLstEmpDtls.get(0);
				lLngDcpsEmpId = Long.parseLong(lObj[0].toString());
				lStrPayCommision = lObj[1].toString();
				lStrName = lObj[2].toString();
				lDblOldAmount = Double.parseDouble(lObj[3].toString());
				lDblPaidAmount = Double.parseDouble(lObj[4].toString());
			}else{
				lStrName = "Invalid";
			}
			
			lSBStatus = getResponseXMLDocEmployeeCode(lStrName,lDblOldAmount,lDblPaidAmount,lStrPayCommision,lLngDcpsEmpId).toString();
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");			
		}
		catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getSixPCArrearDtlsFromEmpCode");			
		}
		return resObj;
	}
	
	private StringBuilder getResponseXMLDocEmployeeCode(String lStrEmployeeName, Double lDblOldAmount, Double lDblPaidAmount,
			String lStrPayCommision, Long lLngDcpsEmpId)
	{
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<EMPNAME>");
		lStrBldXML.append(lStrEmployeeName);
		lStrBldXML.append("</EMPNAME>");
		lStrBldXML.append("<OLDAMOUNT>");
		lStrBldXML.append(lDblOldAmount);
		lStrBldXML.append("</OLDAMOUNT>");
		lStrBldXML.append("<PAIDAMOUNT>");
		lStrBldXML.append(lDblPaidAmount);
		lStrBldXML.append("</PAIDAMOUNT>");
		lStrBldXML.append("<PAYCOMMISION>");
		lStrBldXML.append(lStrPayCommision);
		lStrBldXML.append("</PAYCOMMISION>");
		lStrBldXML.append("<DCPSID>");
		lStrBldXML.append(lLngDcpsEmpId);
		lStrBldXML.append("</DCPSID>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	
	public ResultObject updateSixPCArrearMsterAmount(Map<String, Object> inputMap) 
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"FAIL");		
		Long lLngMstDcpsEmpId = null;
		
		try{
			UpdateSixPCArrearMasterAmountDAO lObjUpdateSixPCArrear = new UpdateSixPCArrearMasterAmountDAOImpl(RltDcpsSixPCYearly.class, serv.getSessionFactory());
			
			lLngMstDcpsEmpId = Long.parseLong(StringUtility.getParameter("DcpsEmpId", request).trim());
			String lStrPayCommision = StringUtility.getParameter("PayComm", request).trim();
			Long lLngNewAmount = Long.parseLong(StringUtility.getParameter("NewAmount", request).trim());
			
			lObjUpdateSixPCArrear.updateMstDcpsSixPC(lLngMstDcpsEmpId, lLngNewAmount, gLngPostId, gLngUserId);
			
			lObjUpdateSixPCArrear.updateRltDcpsSixPCYearly(lLngMstDcpsEmpId, lLngNewAmount, lStrPayCommision, gLngPostId, gLngUserId);
			
			String lSBStatus = getResponseXMLDocUpdate("Update").toString();
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		}
		catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getSixPCArrearDtlsFromEmpCode");			
		}
		return resObj;
	}
	
	private StringBuilder getResponseXMLDocUpdate(String lStrData)
	{
		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<RESDATA>");
		lStrBldXML.append(lStrData);
		lStrBldXML.append("</RESDATA>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
}
