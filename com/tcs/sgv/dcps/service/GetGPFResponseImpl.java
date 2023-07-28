package com.tcs.sgv.dcps.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.GetGPFSlipDAOImpl;
import com.tcs.sgv.eis.valueobject.HrEisBankDtls;

public class GetGPFResponseImpl extends ServiceImpl implements GetGPFResponse {
	/* Global Variable for Logger Class */
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

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");

			gLngPostId = SessionHelper.getPostId(inputMap);
			gLngUserId=SessionHelper.getUserId(inputMap);
			gStrPostId = gLngPostId.toString();

		} catch (Exception e) {

		}

	}
	
	public ResultObject getGPFResponse(Map objectArgs)throws Exception
	{
		gLogger.info("inside getGPFResponse");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		
		try {
			
			HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
			
			setSessionInfo(objectArgs);
			
			gLogger.info("gLngUserId"+gLngUserId);
		
		String year=null;
		String pfseries=null;
		String gpfaccno=null;
		
		//List yearList = new ArrayList();	
		year=StringUtility.getParameter("yearList", request).toString();
		gLogger.info("year"+year);
		GetGPFSlipDAOImpl gpfSlipDAO = new GetGPFSlipDAOImpl(null,serv.getSessionFactory());
		pfseries  = gpfSlipDAO.getGPFpfSeries(gLngUserId);
		gLogger.info("pfseries:"+pfseries);
		gpfaccno  = gpfSlipDAO.getGPFaccno(gLngUserId);
		gLogger.info("gpfaccno:"+gpfaccno);
		List creditDetails = gpfSlipDAO.getCreditBlockDetails(year,pfseries,gpfaccno);
		List debitDetails = gpfSlipDAO.getDebitBlockDetails(year,pfseries,gpfaccno);
		List empDetails = gpfSlipDAO.getemployeeBlockDetails(year,pfseries,gpfaccno);
		List SummaryBlock = gpfSlipDAO.getSummaryBlockBlockDetails(year,pfseries,gpfaccno);
		List missingCredit = gpfSlipDAO.missingCredit(year,pfseries,gpfaccno);
		List missingDebit = gpfSlipDAO.missingDebit(year,pfseries,gpfaccno);
		gLogger.info("getCreditBlockDetails size:"+creditDetails.size());
		gLogger.info("getDebitBlockDetails size:"+debitDetails.size());
		
		//gLogger.info("SummaryBlock size:"+SummaryBlock.size());
		if (creditDetails != null && creditDetails.size() > 0)
		{
			int size = creditDetails.size();
			Object[] row = null;
			Long empId = 0L;
			for (int i = 0; i < size; i++)
			{
				row = (Object[]) creditDetails.get(i);
				if (row != null)
				{
					gLogger.info("creditDetails.size() " + creditDetails.size());
				
					String  des= row[0].toString();
					gLogger.info("des " + des);
					String schemecode =row[1].toString();
					gLogger.info("schemecode" + schemecode);
					String month=row[2].toString();
					gLogger.info("month" + month);
					
				}	
			}
		}
		
		objectArgs.put("forYear", year);
		objectArgs.put("creditDetails", creditDetails);
		objectArgs.put("debitDetails", debitDetails);
		if(empDetails != null && empDetails.size() > 0)
		objectArgs.put("empDetails", empDetails.get(0));
		else 
			objectArgs.put("empDetails", empDetails);
		if(SummaryBlock != null && SummaryBlock.size() > 0)
		objectArgs.put("SummaryBlock", SummaryBlock.get(0));
		else 
			objectArgs.put("SummaryBlock", SummaryBlock);
		objectArgs.put("missingCredit", missingCredit);
		
		if(missingDebit != null && missingDebit.size() > 0)
		objectArgs.put("missingDebit", missingDebit.get(0));
		else 
			objectArgs.put("missingDebit", missingDebit);
		
		
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(objectArgs);		
		resObj.setViewName("GPFSlipResponse");
		
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			e.printStackTrace();
		}
		return resObj;
		
	}
}
