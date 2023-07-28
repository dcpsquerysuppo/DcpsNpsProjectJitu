package com.tcs.sgv.dcps.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.updatePranNoDAOImpl;
import com.tcs.sgv.dcps.valueobject.EmployeeInfoForm;

public class EmployeeInfoFormServiceImpl extends ServiceImpl {
	/* Global Variable for Logger Class */
	private final Log logger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrUserId = null; /* STRING USER ID */

	private String gStrLocale = null; /* STRING LOCALE */

	private Locale gLclLocale = null; /* LOCALE */

	private Long gLngLangId = null; /* LANG ID */

	private Long gLngDBId = null; /* DB ID */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private HttpServletResponse response= null;
	private HttpServletResponse response1= null;/* RESPONSE OBJECT*/

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
	List lstemployee = null;
	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");

	private void setSessionInfo(Map inputMap) {
		try {
			response = (HttpServletResponse) inputMap.get("responseObj");
			response1 = (HttpServletResponse) inputMap.get("responseObj");
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
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {

		}

	}
	
	public ResultObject updateEmpInfo(Map inputMap)
	{
	  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	  String empName = null;
	  Long id = 0l;
	  String country= null;
//	  UploadPranNo objUploadPranNo=null;
	  try
	  {
		 setSessionInfo(inputMap);
		 logger.info("inside updateEmpInfo");
		 empName = StringUtility.getParameter("sevarthId", request).trim();
		 id = Long.parseLong(StringUtility.getParameter("pranNo",request).trim());
		 country = StringUtility.getParameter("pranNo",request).trim();
		
//		 flag= objUpdatePran.updatePranNo(sevarthId, pranNo);
		 logger.info("empName  "+empName);
		 logger.info("id  "+id);
		 logger.info("country  "+country);

		 EmployeeInfoForm lObjEmpInfo = new EmployeeInfoForm();
		 lObjEmpInfo.setPrimaryKey(1L);
		 lObjEmpInfo.setEmpName(empName);
		 lObjEmpInfo.setIdentity(id);
		 lObjEmpInfo.setCountry(country);
		 
		 EmployeeInfoFormServiceImpl lObjSrv = new EmployeeInfoFormServiceImpl();
		 
			StringBuffer strbuflag = new StringBuffer();
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
//			strbuflag.append(flag);
			strbuflag.append("</Flag>");
			strbuflag.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
//			logger.info("Flag is  ********"+flag);
		 
		/* 
		 objRes.setResultValue(inputMap);
		 objRes.setViewName("updatePranNo");*/
		 
	  }
	  
	 catch (Exception e) {
		 objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//e.printStackTrace();
			//return objRes;
		// TODO: handle exception
	}
	 return objRes;
	}
	
		
}