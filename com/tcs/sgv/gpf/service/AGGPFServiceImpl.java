package com.tcs.sgv.gpf.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.gpf.dao.AGGPFSeriesDAO;
import com.tcs.sgv.gpf.dao.AGGPFSeriesDAOImpl;

public class AGGPFServiceImpl extends ServiceImpl implements AGGPFService {
	/* Global Variable for Logger Class */ 
	private final Log gLogger = LogFactory.getLog(getClass());
 
	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

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
	private void setSessionInfo(Map inputMap) {

		try {
			gLogger.info("Setting the session for AGGPFService");
			request = (HttpServletRequest) inputMap.get("requestObj");
			session = request.getSession();
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			gLclLocale = new Locale(SessionHelper.getLocale(request));
			gStrLocale = SessionHelper.getLocale(request);
			gLngLangId = SessionHelper.getLangId(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrPostId = gLngPostId.toString();
			gLngUserId = SessionHelper.getUserId(inputMap);
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public ResultObject activePFService(Map<String, Object> inputMap)
			throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
		gLogger.info("Inside method activePFService");
		try {
			int result=dao.activateAG(request.getParameter("pfLookupId"));
			if(result==1){
				gLogger.info("PF Series Successfully activated");
				inputMap.put("pfActivated", 1);
				}
			if(result==2){
				gLogger.info("PF Series not activated");
				inputMap.put("pfActivated", 2);
				}
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate2");
			inputMap.put("agLookupId", request.getParameter("agLookupId"));
			return resObj;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ResultObject obsoletePFService(Map<String, Object> inputMap)
			throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
		gLogger.info("Inside method obsoletePFService");
		try {
			int result=dao.obsolateAG(request.getParameter("pfLookupId"));
			if(result==1){
				gLogger.info("PF Series Successfully obsolated");
				inputMap.put("pfObsolated", 1);
				}
			if(result==2){
				gLogger.info("PF Series not obsolated");
				inputMap.put("pfObsolated", 2);
				}
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate2");
			inputMap.put("agLookupId", request.getParameter("agLookupId"));
			return resObj;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ResultObject activateAG(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
		gLogger.info("Inside method activate AG");
		try {
			int result=dao.activateAG(request.getParameter("agLookupId"));
			if(result==1){
				gLogger.info("AG Successfully activated");
				inputMap.put("agActivated", 1);
				}
			if(result==2){
				gLogger.info("AG not activated");
				inputMap.put("agActivated", 2);
				}
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate");
			return resObj;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public ResultObject obsolateAG(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
		gLogger.info("Inside method obsolate AG");
		try {
			int result=dao.obsolateAG(request.getParameter("agLookupId"));
			if(result==1){
				gLogger.info("AG Successfully obsolated");
				inputMap.put("agObsolated", 1);
				}
			if(result==2){
				gLogger.info("AG not obsolated");
				inputMap.put("agObsolated", 2);
				}
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate");
			return resObj;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	 
	@Override
	public ResultObject updateAGName(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
		gLogger.info("Inside method updateAGName");
		try {
			int result=0;
			result=dao.upDateAGName(request.getParameter("agLookupId"), request.getParameter("agName"));
			if(result==1){
			inputMap.put("agNameUpdated", 1);
			gLogger.info("AG Name Updated Successfully");
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate");
			return resObj;
			}
			else if(result==3){
			inputMap.put("agNameUpdated", 3);
			gLogger.info("AG Name is too Long");
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate");
			return resObj;
		} return null;
		} catch (Exception e) {
			e.printStackTrace();
			inputMap.put("agNameUpdated", -1);
			gLogger.info("AG Name Updation Failed");
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate");
			return null;
		}
	}
	
	@Override
	public ResultObject updatePFName(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
		gLogger.info("Inside method updatePFName");
		try {
			int result=0;
			result=dao.upDatePFName(request.getParameter("pfLookupId"), request.getParameter("pfName"));
			if(result==1){
			inputMap.put("pfNameUpdated", 1);
			gLogger.info("PF Name Updated Successfully");
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate2");
			inputMap.put("agLookupId", request.getParameter("agLookupId"));
			return resObj;
			}
			else if(result==3){
			inputMap.put("pfNameUpdated", 3);
			gLogger.info("PF Name is too Long");
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate2");
			inputMap.put("agLookupId", request.getParameter("agLookupId"));
			return resObj;
		} return null;
		} catch (Exception e) {
			e.printStackTrace();
			inputMap.put("pfNameUpdated", -1);
			gLogger.info("PF Name Updation Failed");
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate2");
			inputMap.put("agLookupId", request.getParameter("agLookupId"));
			return resObj;
		}
	}
	
	@Override
	public ResultObject loadPFList(Map<String, Object> inputMap) throws Exception {
		
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
		gLogger.info("******Executing method loadPFList");
		inputMap.put("activeAgList", dao.getActiveAGList());
		inputMap.put("pfList", dao.getPFSeriesFromAGLookupId(request.getParameter("agLookupId")));
		String agname=request.getParameter("agName");
		gLogger.info("AG Name is : "+agname);
		inputMap.put("agName", agname);
			gLogger.info("PF List loaded Sucessfully");
			inputMap.put("pfListAdded", 1);
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate2");
			inputMap.put("agLookupId", request.getParameter("agLookupId"));
			return resObj;

	}
	
	
	@Override
	public ResultObject loadAGList(Map<String, Object> inputMap) {
			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			setSessionInfo(inputMap);
			AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
			gLogger.info("Inside method loadAGList");
			try {
				inputMap.put("activeAgList", dao.getActiveAGList());
				gLogger.info("Active AG list loaded Successfully");
				inputMap.put("obsoleteAgList", dao.getObsoleteAGList());
				gLogger.info("Obsolete AG List loaded successfully");
				resObj.setResultValue(inputMap);
				resObj.setViewName(request.getParameter("setViewName"));
				return resObj;
  
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	}
	
	@Override
	public ResultObject loadPFEditForm(Map<String, Object> inputMap) throws Exception{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
	gLogger.info("******Executing method loadPFList");
		inputMap.put("activeAgList", dao.getActiveAGList());
	gLogger.info("*AG List Successfully loaded");
		if(request.getParameter("agLookupId").equals("none")){
			inputMap.put("pfListAdded", -1);
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate2");
			return resObj;
		}
		else{
		String agLookupId=request.getParameter("agLookupId");
	gLogger.info("AG Lookup ID is : "+agLookupId);
			inputMap.put("agLookupId",agLookupId);
			inputMap.put("activePFList", dao.getActivePFSeriesFromAGLookupId(agLookupId));
			inputMap.put("obsoletePFList", dao.getObsoletePFSeriesFromAGLookupId(agLookupId));
		String agname=request.getParameter("name");
	gLogger.info("AG Name is : "+agname);
			inputMap.put("agName", agname);
	gLogger.info("PF List loaded Sucessfully");
			inputMap.put("pfListAdded", 1);
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate2");
			return resObj;
		}
	}
	
	@Override
	public ResultObject addAG(Map<String, Object> inputMap){
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
		gLogger.info("Inside method addAG");
		try {
			int result=dao.addAG(request.getParameter("agName"));
			if(result==1){
				gLogger.info("AG Successfully Added");
				inputMap.put("agAdded", 1);
				}
			if(result==2){
				gLogger.info("AG Already Exist");
				inputMap.put("agAdded", 2);
				}
			if(result==3){
				gLogger.info("AG name is too long");
				inputMap.put("agAdded", 3);
				}
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate");
			return resObj;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public ResultObject addPFSeriesforAG(Map<String, Object> inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		setSessionInfo(inputMap);
		AGGPFSeriesDAO dao=new AGGPFSeriesDAOImpl(null,serv.getSessionFactory());
		gLogger.info("Inside method addPFSeriesforAG");
		try {
			
			int result=dao.addPFSeries(Long.parseLong(request.getParameter("agId")), request.getParameter("pfName"));
			if(result==1){
				gLogger.info("PF Series Successfully Added");
				inputMap.put("pFAdded", 1);
				}
			if(result==2){
				gLogger.info("PF Series Already Exist");
				inputMap.put("pFAdded", 2);
				}
			if(result==3){
				gLogger.info("PF Series name is too long");
				inputMap.put("pFAdded", 3);
				}
			resObj.setResultValue(inputMap);
			resObj.setViewName("AGGPFSeriesUpdate2");
			inputMap.put("agLookupId", request.getParameter("agLookupId"));
			return resObj;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
