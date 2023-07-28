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
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.MissingCreditDAO;
import com.tcs.sgv.dcps.dao.MissingCreditDAOImpl;
import com.tcs.sgv.dcps.dao.R1_InputDAO;
import com.tcs.sgv.dcps.dao.R1_InputDAOImpl;


public class TestCodeServiceImpl extends ServiceImpl implements TestCodeService
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
	
	public ResultObject checkcode(Map<String, Object> inputMap)
	{
		gLogger.info("Inside checkcode ******** ");
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");		
		List lLstYears = null;
		List lLstAllDdo = null;
		List lLstAllTreasury = null;
		
		try{
			DcpsCommonDAO lObjCommonDAO = new DcpsCommonDAOImpl(OrgDdoMst.class, serv.getSessionFactory());	
			MissingCreditDAO lObjR1Form = new MissingCreditDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			String lStrUser = StringUtility.getParameter("user", request);
			
			lLstYears = lObjCommonDAO.getFinyears();
			
			if(lStrUser.trim().equals("TO")){
				lLstAllDdo = lObjR1Form.getAllDdoCode(gStrLocationCode);
			}
			lLstAllTreasury = lObjR1Form.getAllTreasury();
			
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("AllDDOList", lLstAllDdo);
			inputMap.put("AllTreasury", lLstAllTreasury);
			inputMap.put("userType", lStrUser);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("TestCodeSearch");
		}catch(Exception e){
			IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in loadGPFDataEntryForm");
		}
		return resObj;
	}
	
	public ResultObject getAllDdoCode(Map<String, Object> inputMap)
	{
		setSessionInfo(inputMap);
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS,"Fail");
		
		List lLstAllDdoCode = null;
		  try
		  {
			  MissingCreditDAOImpl lObjR1Form = new MissingCreditDAOImpl(OrgDdoMst.class, serv.getSessionFactory());
			  String locCode = StringUtility.getParameter("locCode",request);				  
				  
			  if(!locCode.equals("")){
				  lLstAllDdoCode = lObjR1Form.getAllDdoCode(locCode);
			  }
			  	
			  String lStrTempResult = null;
				if (lLstAllDdoCode != null) {
					lStrTempResult = new AjaxXmlBuilder().addItems(lLstAllDdoCode, "desc", "id", true).toString();
				}
				inputMap.put("ajaxKey", lStrTempResult);
				resObj.setResultValue(inputMap);
				resObj.setViewName("ajaxData");
		  }
		  catch(Exception e)
		  {
			  IFMSCommonServiceImpl.setErrorProperties(gLogger, resObj, e, "Error in getAllDdoCode");
		  }
		return resObj;
	}
}
