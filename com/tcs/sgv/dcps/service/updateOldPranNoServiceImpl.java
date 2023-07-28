package com.tcs.sgv.dcps.service;

import java.io.BufferedOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.jfree.util.StackableException;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.AllIndiaServicesDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAO;
import com.tcs.sgv.dcps.dao.NewRegTreasuryDAOImpl;
import com.tcs.sgv.dcps.dao.updateOldPranNoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.UploadPranNo;

public class updateOldPranNoServiceImpl extends ServiceImpl {
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
			
			System.out.println(gLclLocale + gStrLocale +gLngLangId +gLngPostId +gStrPostId +gLngUserId +gStrUserId +gStrLocationCode );
			
		} catch (Exception e) {

		}

	}
	
	public ResultObject testPranNO(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		List pranExist=null;

		try {

			setSessionInfo(inputMap);
			updateOldPranNoDAOImpl lObjupdatePr = new updateOldPranNoDAOImpl(null,serv.getSessionFactory());    	

			String Pran_no = StringUtility.getParameter("pranno",request).trim();


			if (!"".equals(Pran_no)) 
			{
				pranExist = lObjupdatePr.testPranNO(Pran_no);
			}
			String empName="NA";
			String dcpsId="NA";
			if(pranExist!=null && pranExist.size()!=0){
				Object[] tuple = (Object[]) pranExist.get(0);	
				lBlFlag=true;
				empName=tuple[0].toString();
				if(tuple[1]!=null)
				{
					dcpsId=tuple[1].toString();
				}
				
			}
			
			StringBuffer strbuflag = new StringBuffer();
			String lSBStatus="";
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
			strbuflag.append(lBlFlag);
			strbuflag.append("</Flag>");
			strbuflag.append("<EmpName>");
			strbuflag.append(empName);
			strbuflag.append("</EmpName>");
			strbuflag.append("<DcpsId>");
			strbuflag.append(dcpsId);
			strbuflag.append("</DcpsId>");
			strbuflag.append("</XMLDOC>");
			lSBStatus = strbuflag.toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			logger.error(" Error in testpranno " + e, e);
		}

		return resObj;

	}
	
	public ResultObject loadEmpList(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrEmpName = null;
		String lStrSevaarthId = null;
		String ddoCode = null;
		String lStrDcpsId = null;
		String lStrPpanNo = null;
		List lLstEmpDeselect = null;
		
		try {
			setSessionInfo(inputMap);

			gLngPostId = SessionHelper.getPostId(inputMap);
			
			logger.info("############## lng id "+gLngPostId);
			updateOldPranNoDAOImpl objUpdateDao = new updateOldPranNoDAOImpl(null, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			//ddoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
		//	logger.info("ddoCode*******"+ddoCode);
			String locId=gStrLocationCode;
			
			
			if (StringUtility.getParameter("fromSearch", request).trim().equals("Yes")) {
				lStrEmpName = StringUtility.getParameter("txtEmployeeName", request).trim().toUpperCase();
				lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
				lStrDcpsId = StringUtility.getParameter("txtDcpsId", request).trim().toUpperCase();
				lStrPpanNo = StringUtility.getParameter("txtPpanNo", request).trim().toUpperCase();
				//lLstEmpDeselect = objUpdateDao.getAllEmp(lStrSevaarthId,lStrEmpName,lStrDcpsId,lStrPpanNo,ddoCode );
				lLstEmpDeselect = objUpdateDao.getAllEmp(lStrSevaarthId,lStrEmpName,lStrDcpsId,lStrPpanNo,locId );
				if(lLstEmpDeselect!=null && lLstEmpDeselect.size() > 0)
				{
					
				}
				else
				{
					lLstEmpDeselect = objUpdateDao.getAllEmpNew(lStrSevaarthId,lStrEmpName,lStrDcpsId,lStrPpanNo,locId );
				}
				}

		        String lSevarthId = "";
		        String postEndDate = "";

		        if (lLstEmpDeselect!=null && lLstEmpDeselect.size() > 0) {
		          Object[] empLst = (Object[])lLstEmpDeselect.get(0);
		          lSevarthId = empLst[2].toString().trim();
		       //   postEndDate = empLst[10].toString().trim();
		        }
				logger.info("lSevarthId  :  "+lSevarthId);
				logger.info("postEndDate  :  "+postEndDate);
				inputMap.put("lSevarthId", lSevarthId);
				inputMap.put("postEndDate", postEndDate);
				inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
				objRes.setResultValue(inputMap);
				objRes.setViewName("updateOldPran");				
			}
			catch (Exception e) {
				// TODO: handle exception
				logger.info("Exception is " + e);
				objRes.setResultValue(null);
				objRes.setThrowable(e);
				objRes.setResultCode(ErrorConstants.ERROR);
				objRes.setViewName("errorPage");
			}
			return objRes;
	}
	
	
	
	public ResultObject checkSevaarthId(Map inputMap)throws Exception {
		

		System.out.println("Inside checkSevaarthId ");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Object []  exist= null;
		String lStrSevaarthId = null;
		String lStrDDOCode = null;
		String regStatus=null;
		Date servEndDate=null;
		String accMain=null;
		String NotExist="";
		Date dateOfJoin=null;
		String pranNo= null;
		String ddoCode=null;
		
		String lStrEmpname = null;
		String lStrDcpsId = null;
		String lStrPpanNo = null;
		
		
		String treasuryCode=null;
		
		
		
		
		String dCode="";
		try {
		setSessionInfo(inputMap);
		gLngPostId = SessionHelper.getPostId(inputMap);
		updateOldPranNoDAOImpl lObjcheck = new updateOldPranNoDAOImpl(null,serv.getSessionFactory());
		lStrSevaarthId = StringUtility.getParameter("txtSevaarthId", request).trim().toUpperCase();
		lStrEmpname = StringUtility.getParameter("txtEmployeeName", request).trim().toUpperCase();
		lStrDcpsId = StringUtility.getParameter("txtDcpsId", request).trim().toUpperCase();
		lStrPpanNo = StringUtility.getParameter("txtPpanNo", request).trim().toUpperCase();
		

		
		DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
		lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date current=new Date();
		System.out.println(dateFormat.format(current));
		
		
		
		Date currentdt=dateFormat.parse(dateFormat.format(current));
		Date doj=dateFormat.parse("01-11-2005");
		System.out.println("doj*******"+doj);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		//sdf.format(date1).equals(sdf.format(date2));
		
		System.out.println("Inside checkSevaarthId ");
		System.out.println("currentdt:::"+currentdt);
		String trCode=gStrLocationCode.substring(0, 2);
		List existList = lObjcheck.checkSevaarthIdExist(lStrSevaarthId,lStrDDOCode,lStrEmpname,lStrDcpsId,lStrPpanNo,trCode);
		
			
		System.out.println("existList  size::::::::::"+existList.size());
	
		label:
		if(existList!= null && existList.size()>0)
		{
			
			System.out.println("0");
		Object[] tuple = (Object[]) existList.get(0);
				
			//System.out.println("LENGTH*****"+exist.length);
			regStatus= tuple[0].toString();
			servEndDate=sdf.parse(tuple[1].toString());
			System.out.print("########servEndDate"+servEndDate);
			accMain=tuple[2].toString();
			dateOfJoin=sdf.parse(tuple[3].toString());
			System.out.print("########dateOfJoin"+dateOfJoin);
			pranNo= tuple[4].toString();
			ddoCode= tuple[5].toString();
			treasuryCode= tuple[6].toString();

			System.out.println("days:::"+daysBetween(servEndDate,currentdt));
//			Integer days = servEndDate.compareTo(currentdt);
//			Integer days1 =daysBetween(doj,dateOfJoin); //daysBetween(dateOfJoin,doj); 
//			
			System.out.println("days:::"+daysBetween(servEndDate,currentdt));
			Integer days = servEndDate.compareTo(currentdt);
			Integer days1 =dateOfJoin.compareTo(doj); //daysBetween(dateOfJoin,doj); 
			
			
			System.out.println("days1****"+days1);
			System.out.println("ddocode1**********"+ddoCode);
			System.out.println("ddocode2**********"+lStrDDOCode);
		
			if(!ddoCode.equalsIgnoreCase("NA"))	
			{
			dCode=ddoCode.substring(0,4);
			System.out.print("dCode##"+dCode+"###loc_id##"+gStrLocationCode);
			System.out.println(1);
			}
			
			
		
		  if (!regStatus.equals("1"))
		  {
			  NotExist="NA1"; 
			  break label;
		  }
		  if(servEndDate.equals("NA"))
		  {
			  NotExist="NA2"; 
			  break label;
		  }
		 /* if (days>1 )
		  {
			  NotExist="NA2"; 
			  break label;
		  }*/
		  if (!accMain.equals("700174") && !accMain.equals("700240") && !accMain.equals("700241") && !accMain.equals("700242"))
		  { 
			  NotExist="NA3"; 
			  break label;
		  }
		  if(dateOfJoin.equals("NA"))
		  { 
			  NotExist="NA4"; 
			  break label;
		  }
		  if(days1 < 0)
		  {
			  NotExist="NA4"; 
			  break label;
		  }
		  if(days < 0)
		  {
			  NotExist="NA7"; 
			  break label;
		  }
		  if(!pranNo.equals("#"))
		  {
			  NotExist="NA5"; 
			  break label;
		  }
		  if(!ddoCode.equals("NA"))
		  {
			  NotExist="NA9"; 
			  break label;
		  }
		  if(treasuryCode.equals(gStrLocationCode))
			{     System.out.println("Hello!!");
			   //NotExist="NA6"; 
			 System.out.println("2");
			   //  break label;
			    
			}
		  
		  
		}
		else 
		{
			NotExist="NA";
		break label;
		
		}
		
		
		
		
		StringBuffer strbuflag = new StringBuffer();
		strbuflag.append("<XMLDOC>");
		strbuflag.append("<Flag>");
		strbuflag.append(NotExist);
		strbuflag.append("</Flag>");
		strbuflag.append("</XMLDOC>");

		String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();

		inputMap.put("ajaxKey", lStrResult);
		objRes.setViewName("ajaxData");
		objRes.setResultValue(inputMap);
		
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.info("Exception is " + e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
		}
	
	public static Integer daysBetween(Date sPassedDate, Date ePassedDate) {

		Calendar sDate = Calendar.getInstance();
		sDate.setTime(sPassedDate);
		Calendar eDate = Calendar.getInstance();
		eDate.setTime(ePassedDate);

		Calendar d = (Calendar) sDate.clone();
		Integer dBetween = 0;
		while (d.before(eDate)) {
			d.add(Calendar.DAY_OF_MONTH, 1);
			dBetween++;
		}
		if(dBetween==0 && d.equals(eDate)){
			dBetween = 1;
		}

		else if(dBetween==0 && d.after(eDate)){
			dBetween = 0;
		}
		return dBetween;
	}
	
	public ResultObject getEmpNameAutoComplete(
			Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String lStrEmpName = null;
		String lStrSearchBy = null;
		String lStrDDOCode = null;
		String lStrSearchType = null;

		try {
			setSessionInfo(inputMap);
			updateOldPranNoDAOImpl lObjupdateOldPranNo = new updateOldPranNoDAOImpl(
					MstEmp.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			lStrEmpName = StringUtility.getParameter("searchKey", request)
			.trim();

			lStrSearchBy = StringUtility.getParameter("searchBy", request)
			.trim();

			

			if (lStrSearchBy.equals("searchByDDOAsst")) {

				lStrDDOCode = lObjDcpsCommonDAO.getDdoCode(gLngPostId);
			}
			if (lStrSearchBy.equals("searchFromDDODeSelection")
					|| lStrSearchBy.equals("searchByDDO")) {
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			}
			if (lStrSearchBy.equals("searchBySRKA")) {
				lStrDDOCode = null;
			}

			finalList = lObjupdateOldPranNo.getEmpNameAutoComplete(
					lStrEmpName.toUpperCase(), lStrDDOCode,lStrSearchBy);

			String lStrTempResult = null;
			if (finalList != null) {
				lStrTempResult = new AjaxXmlBuilder().addItems(finalList,
						"desc", "id", true).toString();

			}
			inputMap.put("ajaxKey", lStrTempResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");

		} catch (Exception ex) {
			objRes.setResultValue(null);
			objRes.setThrowable(ex);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			//ex.printStackTrace();
			return objRes;
		}

		return objRes;

	}
	
	public ResultObject updatePranNo(Map inputMap)
	{
	  ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	  String sevarthId = null;
	  String pranNo = null;
	  String flag= null;
	  UploadPranNo objUploadPranNo=null;
	  Long uploadId=0l;
	  try
	  {
		 setSessionInfo(inputMap);
		 
		 System.out.println("inside update Old pran");
		 sevarthId = StringUtility.getParameter("sevarthId", request).trim();
		 pranNo = StringUtility.getParameter("pranNo",request).trim();
		 if(StringUtility.getParameter("uploadId",request)!=null)
		 {
			 uploadId=Long.parseLong(StringUtility.getParameter("uploadId",request));
		 }
		
		 updateOldPranNoDAOImpl objUpdatePran = new updateOldPranNoDAOImpl(UploadPranNo.class,serv.getSessionFactory());
		 flag= objUpdatePran.updatePranNo(sevarthId, pranNo);
		 System.out.println("FLAG:"+flag);
		 System.out.println("sevarthId"+sevarthId);
		 System.out.println("pranNo"+pranNo);
		 if(uploadId >0)
		 {
			 objUploadPranNo=(UploadPranNo) objUpdatePran.read(uploadId);
			 if(objUploadPranNo!=null)
			 {
				 objUploadPranNo.setIsPranUpdated(1);
				 objUploadPranNo.setUpdatedDate(gDtCurDate);
				 objUploadPranNo.setUpdatedPostId(gLngPostId);
				 objUpdatePran.update(objUploadPranNo);
			 }
		 }

			StringBuffer strbuflag = new StringBuffer();
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
			strbuflag.append(flag);
			strbuflag.append("</Flag>");
			strbuflag.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			logger.info("Flag is  ********"+flag);
		 
		/* 
		 objRes.setResultValue(inputMap);
		 objRes.setViewName("updateOldPranNo");*/
		 
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