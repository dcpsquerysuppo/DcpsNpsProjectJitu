package com.tcs.sgv.common.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.dao.ChangeDCPSorGPFDAOImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.ess.valueobject.OrgUserMst;

public class ChangeDCPRorGPFServiceImpl extends ServiceImpl
{
	private final Log gLogger = LogFactory.getLog(getClass());
	
	public ResultObject getDojFromSevaarth(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		String lStrRes = "";		
		String lSBStatus = "";
		
		try {			
			ChangeDCPSorGPFDAOImpl lObjChangeDCPSorGPFDAOImpl = new ChangeDCPSorGPFDAOImpl(MstEmp.class,serv.getSessionFactory());
			
			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat lObjSimpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
			String lStrSevaarth = StringUtility.getParameter("sevaarthId", request);
			String lStrDOJ = lObjChangeDCPSorGPFDAOImpl.getDojFromSevaarth(lStrSevaarth);	
			
			if(lStrDOJ.equals("")){
				lSBStatus = getResponseXMLDoc("Invalid").toString();
			}else{
				String lObjDate = lObjSimpleDateFormat2.format(lObjSimpleDateFormat.parse(lStrDOJ.trim()));
				lSBStatus = getResponseXMLDoc(lObjDate).toString().trim();
			}
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {			
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getDojFromSevaarth");
		}
		return resObj;
	}
	
	public ResultObject updateGPForDCPS(Map<String, Object> inputMap) 
	{
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");			
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		String lStrRes = "";		
		String lSBStatus = "";
		
		try {			
			ChangeDCPSorGPFDAOImpl lObjChangeDCPSorGPFDAOImpl = new ChangeDCPSorGPFDAOImpl(MstEmp.class,serv.getSessionFactory());			
			String lStrSevaarth = StringUtility.getParameter("sevaarthId", request);
			String lStrDpValue = StringUtility.getParameter("dpValue", request);
			String lStrDojNew = StringUtility.getParameter("dojNew", request);
			
			lObjChangeDCPSorGPFDAOImpl.updateDPvalue(lStrSevaarth, lStrDpValue, lStrDojNew);
			
			lSBStatus = getResponseXMLDoc("true").toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		} catch (Exception e) {			
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getDojFromSevaarth");
		}
		return resObj;
	}
	
	private StringBuilder getResponseXMLDoc(String lStrDoj) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<DOJ>");
		lStrBldXML.append(lStrDoj);
		lStrBldXML.append("</DOJ>");		
		lStrBldXML.append("</XMLDOC>");
		return lStrBldXML;
	}
}
