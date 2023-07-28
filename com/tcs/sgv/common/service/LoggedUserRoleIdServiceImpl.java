package com.tcs.sgv.common.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.retirement.dao.RetirementDAO;
import com.tcs.sgv.retirement.dao.RetirementDAOImpl;

public class LoggedUserRoleIdServiceImpl extends ServiceImpl
{
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */
	
	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	/* Global Variable for PostId */
	Long gLngPostId = null;

	/* Global Variable for UserId */
	Long gLngUserId = null;

	/* Global Variable for Current Date */
	Date gDtCurrDt = null;

	/* Global Variable for Location Code */
	String gStrLocationCode = null;

	/* Global Variable for User Loc Map */
	static HashMap sMapUserLoc = new HashMap();

	/* Global Variable for User Location */
	String gStrUserLocation = null;
	
	SessionFactory factory=null;
	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) 
	{

		try {
			gLogger.info("Setting the session for Load Retirement List");
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			} 
		catch (Exception e) 
		{}

	}
	
	public ResultObject getLoggedUserRoleId(Map inputMap) 
	{
		try
		{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		LoggedUserRoleIdDAOImpl dao = new LoggedUserRoleIdDAOImpl(null,serv.getSessionFactory());
		inputMap.put("UserRoleIdList", dao.getLoggedUserRoleIdList(gLngPostId));
		gLogger.info("Inside Logged user List");
		resObj.setResultValue(inputMap);
		resObj.setViewName("MenuOnPage");
		return resObj;
		}

		catch (Exception e) {e.printStackTrace();
		return null;}
		
	}

	

}
