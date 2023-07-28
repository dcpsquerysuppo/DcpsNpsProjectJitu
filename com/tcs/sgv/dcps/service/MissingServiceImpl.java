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
import com.tcs.sgv.dcps.dao.MissingCreditDAOImpl;

public class MissingServiceImpl extends ServiceImpl {
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
