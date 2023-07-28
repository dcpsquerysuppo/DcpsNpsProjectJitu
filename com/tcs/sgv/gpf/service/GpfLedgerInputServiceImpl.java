/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 12, 2011		Chudasama Jayraj								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.service;

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
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.gpf.dao.GPFLedgerInputDAO;
import com.tcs.sgv.gpf.dao.GPFLedgerInputDAOImpl;

/**
 * Class Description - 
 *
 *
 * @author Chudasama Jayraj
 * @version 0.1
 * @since JDK 5.0
 * Dec 12, 2011
 */
public class GpfLedgerInputServiceImpl extends ServiceImpl implements GpfLedgerInputService
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

	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");			
			serv = (ServiceLocator) inputMap.get("serviceLocator");									
			gLngPostId = SessionHelper.getPostId(inputMap);			
			gLngUserId = SessionHelper.getUserId(inputMap);			
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}

	}
	
	public ResultObject loadLedgerInput(Map<String, Object> inputMap)
	{		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");		
		
		try{
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
			List lLstYears = lObjCommonDAO.getFinyears();
			String lStrUser = StringUtility.getParameter("userType", request);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("userType", lStrUser);
			resObj.setResultValue(inputMap);
			resObj.setViewName("LedgerReportInput");
		}
		catch (Exception e) {
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadLedgerInput");			
		}
		return resObj;		
	}
	
	public ResultObject loadUserDetails(Map<String, Object> inputMap)
	{			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");		
		
		String lStrEmpName="";
		String lSBStatus="";
		try{
			setSessionInfo(inputMap);
			GPFLedgerInputDAO lObjGpfLedgerInputDAO = new GPFLedgerInputDAOImpl(MstEmp.class, serv.getSessionFactory());
			
			lStrEmpName = lObjGpfLedgerInputDAO.getEmpNameFromUserId(gLngUserId);
			
			if(!lStrEmpName.equals("")){
				lSBStatus = getResponseXMLDocEmployeeName(lStrEmpName).toString();			
			}
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
				
				inputMap.put("ajaxKey", lStrResult);
				resObj.setResultValue(inputMap);
				resObj.setViewName("ajaxData");
				return resObj;
		}
		catch(Exception e)
		{
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadUserDetails");			
		}
		
		return null;
	}
	
	private StringBuilder getResponseXMLDocEmployeeName(String lStrEmpName) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<EMPCODE>");
		lStrBldXML.append(lStrEmpName);
		lStrBldXML.append("</EMPCODE>");		
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
}
