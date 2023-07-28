package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NsdlNpsDAOImpl;
import com.tcs.sgv.dcps.dao.UpdateFrnNoDAO;
import com.tcs.sgv.dcps.dao.UpdateFrnNoDAOImpl;
import com.tcs.sgv.dcps.dao.UpdateNSDLDetailsDAOImpl;
import com.tcs.sgv.dcps.dao.UploadPranDAO;
import com.tcs.sgv.dcps.dao.UploadPranDaoImpl;
import com.tcs.sgv.dcps.dao.updatePranNoDAOImpl;
import com.tcs.sgv.dcps.valueobject.UpdateFrnNo;
import com.tcs.sgv.dcps.valueobject.UploadPranNo;

public class UpdateFrnNoServiceImpl extends ServiceImpl {
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

	private HttpServletResponse response = null;
	private HttpServletResponse response1 = null;/* RESPONSE OBJECT */

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
	private ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/DCPSConstants");

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

	public ResultObject loadFileList(Map inputMap) throws Exception {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		logger.info(" oin loadFileList");
		String lStrFileId = null;

		String lStrSevaarthId = null;

		String lStrDcpsId = null;
		String lStrPpanNo = null;
		List lLstEmpDeselect = null;

		try {
			setSessionInfo(inputMap);
			gLngPostId = SessionHelper.getPostId(inputMap);
			UpdateFrnNoDAOImpl objUpdateDao = new UpdateFrnNoDAOImpl(null,
					serv.getSessionFactory());

			String locId = gStrLocationCode;
			logger.info("locId*******" + locId);

			if (StringUtility.getParameter("fromSearch", request).trim()
					.equals("Yes")) {
				lStrFileId = StringUtility.getParameter("txtFileId", request)
						.trim().toUpperCase();
				lLstEmpDeselect = objUpdateDao.getFileDetails(lStrFileId);
			}

			if (lLstEmpDeselect != null && lLstEmpDeselect.size() > 0) {
				inputMap.put("DESELECTEMPLIST", lLstEmpDeselect);
			}
			objRes.setResultValue(inputMap);
			objRes.setViewName("updateFrnnNo");

		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Exception is " + e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}

/*	public ResultObject checkFileId(Map inputMap) throws Exception {

		System.out.println("Inside checkFileId ");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		String lStrFileId = null;
		String NotExist = "";
		Integer digiStatus = 1;

		try {
			setSessionInfo(inputMap);
			UpdateFrnNoDAOImpl lObjcheck = new UpdateFrnNoDAOImpl(null,
					serv.getSessionFactory());
			lStrFileId = StringUtility.getParameter("txtFileId", request)
					.trim().toUpperCase();
			List existList = lObjcheck.checkfileIdExist(lStrFileId);


			System.out.println("existList  size::::::::::" + existList.size());

			Integer in = (Integer) digiStatusF.get(0);

			if (existList == null || existList.size() == 0) {
				NotExist = "NA2";
			} else if (in.equals("0")) {
				NotExist = "NA3";
			}

			logger.info("Notexist flag is " + NotExist);

			StringBuffer strbuflag = new StringBuffer();
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
			strbuflag.append(NotExist);
			strbuflag.append("</Flag>");
			strbuflag.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					strbuflag.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);

		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Exception is " + e);
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
*/
	public ResultObject updateFrnNo(Map inputMap) {
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String fileId = null;
		String frnNo = null;
		String flag = null;

		UploadPranNo objUploadPranNo = null;
		try {
			setSessionInfo(inputMap);
			System.out.println("inside update FRN no");
			fileId = StringUtility.getParameter("file_name", request).trim();
			frnNo = StringUtility.getParameter("frnNo", request).trim();
			UpdateFrnNoDAOImpl objUpdatePran = new UpdateFrnNoDAOImpl(
					UploadPranNo.class, serv.getSessionFactory());
			flag = objUpdatePran.updateFrnNo(fileId, frnNo);
			System.out.println("FLAG:" + flag);
			System.out.println("fileId" + fileId);
			System.out.println("frnNo" + frnNo);

			StringBuffer strbuflag = new StringBuffer();
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
			strbuflag.append(flag);
			strbuflag.append("</Flag>");
			strbuflag.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					strbuflag.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			objRes.setViewName("ajaxData");
			objRes.setResultValue(inputMap);
			logger.info("Flag is  ********" + flag);

			
			  //objRes.setResultValue(inputMap);
			//  objRes.setViewName("updateFrnnNo");
			 

		}

		catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
			// e.printStackTrace();
			// return objRes;
			// TODO: handle exception
		}
		return objRes;
	}

	public ResultObject updateFrnnNo(Map inputMap) throws Exception {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		try {
			setSessionInfo(inputMap);
			String month = StringUtility.getParameter("cmbMonth", request);
			String year = StringUtility.getParameter("cmbYear", request);
			Calendar cal = Calendar.getInstance();
			String currmonth = (new Integer((cal.get(Calendar.MONTH) + 1))).toString();
			String curryear = (new Integer(cal.get(Calendar.YEAR))).toString();
			Long currentyear=null;
			Long currentmonth=null;
			if(currmonth.equals("1")){
				currentmonth= 12L;
				currentyear=Long.parseLong(curryear)-1;
				
			}
			else{
				currentmonth= Long.parseLong(currmonth)-1;
				currentyear=Long.parseLong(curryear);

			}
			String trCode=gStrLocationCode;
			if(StringUtility.getParameter("cmbMonth", request)!=null && !(StringUtility.getParameter("cmbMonth", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbMonth", request))!=-1 
					&& StringUtility.getParameter("cmbYear", request)!=null && !(StringUtility.getParameter("cmbYear", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbYear", request))!=-1
					&& StringUtility.getParameter("trCode", request)!=null && !(StringUtility.getParameter("trCode", request).equals("")) && Long.parseLong(StringUtility.getParameter("trCode", request))!=-1){
				currentmonth= Long.parseLong(StringUtility.getParameter("cmbMonth", request));
				currentyear=Long.parseLong(StringUtility.getParameter("cmbYear", request));
				
				trCode=StringUtility.getParameter("trCode", request);
			}
			
			
			UpdateNSDLDetailsDAOImpl lObjNsdlDAO = new UpdateNSDLDetailsDAOImpl(null, serv.getSessionFactory());
	
			UpdateFrnNoDAOImpl lObjcheck = new UpdateFrnNoDAOImpl(null,
					serv.getSessionFactory());
				List subTr=lObjcheck.getAllSubTreasury();
			
			List nsdlDeatils=lObjcheck.getAllData(currentyear.toString(), currentmonth.toString(), trCode);
			List lLstYears = lObjcheck.getFinyear();
			
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			List lLstMonths=lObjDcpsCommonDAO.getMonths();
			
			inputMap.put("selMonth", currentmonth);
			inputMap.put("trCode", trCode);
			inputMap.put("selYear", currentyear);
			inputMap.put("subTr", subTr);
			inputMap.put("size", nsdlDeatils.size());
			inputMap.put("nsdlDeatils", nsdlDeatils);

			//gLogger.info("Month and year is "+lLstMonths.size());
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstMonths", lLstMonths);

			resObj.setResultValue(inputMap);
			resObj.setViewName("updateFrnnNo");

		} catch (Exception e) {
			e.printStackTrace();
			//gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}
	
	
	
	public ResultObject testFrnNO(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;
		List frnExist=null;

		try {

			setSessionInfo(inputMap);
			UpdateFrnNoDAOImpl lObjcheck = new UpdateFrnNoDAOImpl(null,
					serv.getSessionFactory());
			String frn_No = StringUtility.getParameter("frnNo",request).trim();


			if (!"".equals(frn_No)) 
			{
				frnExist = lObjcheck.testFrnNO(frn_No);
			}
			String filename="NA";
			
			if(frnExist.size()>0){
				Object[] tuple = (Object[]) frnExist.get(0);	
				lBlFlag=true;
				filename=tuple[0].toString();
				//dcpsId=tuple[1].toString();
			}
			
			StringBuffer strbuflag = new StringBuffer();
			String lSBStatus="";
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
			strbuflag.append(lBlFlag);
			strbuflag.append("</Flag>");
			strbuflag.append("<EmpName>");
			strbuflag.append(filename);
			strbuflag.append("</EmpName>");
			strbuflag.append("</XMLDOC>");
			
			lSBStatus = strbuflag.toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultCode(ErrorConstants.SUCCESS);
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
	
	public ResultObject updateFrn(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		 String flag= null;
		try {
			setSessionInfo(inputMap);
			String month = StringUtility.getParameter("cmbMonth", request);
			String year = StringUtility.getParameter("cmbYear", request);
			Calendar cal = Calendar.getInstance();
			String currmonth = (new Integer((cal.get(Calendar.MONTH) + 1))).toString();
			String curryear = (new Integer(cal.get(Calendar.YEAR))).toString();
			Long currentyear=null;
			Long currentmonth=null;
			if(currmonth.equals("1")){
				currentmonth= 12L;
				currentyear=Long.parseLong(curryear)-1;

			}
			else{
				currentmonth= Long.parseLong(currmonth)-1;
				currentyear=Long.parseLong(curryear);

			}
			String trCode=gStrLocationCode;
			if(StringUtility.getParameter("cmbMonth", request)!=null && !(StringUtility.getParameter("cmbMonth", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbMonth", request))!=-1 
					&& StringUtility.getParameter("cmbYear", request)!=null && !(StringUtility.getParameter("cmbYear", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbYear", request))!=-1
					&& StringUtility.getParameter("trCode", request)!=null && !(StringUtility.getParameter("trCode", request).equals("")) && Long.parseLong(StringUtility.getParameter("trCode", request))!=-1){
				currentmonth= Long.parseLong(StringUtility.getParameter("cmbMonth", request));
				currentyear=Long.parseLong(StringUtility.getParameter("cmbYear", request));
				trCode=StringUtility.getParameter("trCode", request);
			}
			
			
			UpdateNSDLDetailsDAOImpl lObjNsdlDAO = new UpdateNSDLDetailsDAOImpl(null, serv.getSessionFactory());
	
			UpdateFrnNoDAOImpl lObjcheck = new UpdateFrnNoDAOImpl(null,
					serv.getSessionFactory());
			
			UpdateFrnNoDAOImpl lObjUploadFrnNo = new UpdateFrnNoDAOImpl(UpdateFrnNo.class, serv.getSessionFactory());
			UpdateFrnNo  updateFrnNo= null;
			
			String fileIds=null;
			String frnNo=null;
			String emp=null;
			String empr=null;
			String remark=null;
			
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			    Date d=new Date();

			if((StringUtility.getParameter("fileIds", request)!=null)&&(StringUtility.getParameter("fileIds", request)!="") 
					&&(StringUtility.getParameter("emp", request)!="")&& (StringUtility.getParameter("empr", request)!="")
					&&(StringUtility.getParameter("remark", request)!="")){
				
				fileIds = StringUtility.getParameter("fileIds", request);
				frnNo = StringUtility.getParameter("frnNo", request);	
				emp = StringUtility.getParameter("emp", request);
				empr = StringUtility.getParameter("empr", request);
				remark = StringUtility.getParameter("remark", request);
				 
				
				String[] fileids = fileIds.split("~");
				String[] frnNos = frnNo.split("~");
				String[] emps = emp.split("~");
				String[] emprs = empr.split("~");
				String[] remarks = remark.split("~");
			 
				
				String[] fileArray = new String[fileids.length];
				String[] frnArr = new String[frnNos.length];
				String[] empArr = new String[emps.length];
				String[] emprArr = new String[emprs.length];
				String[] remarkArr = new String[remarks.length];
				 
				
				for (Integer lInt = 0; lInt < fileids.length; lInt++)
				{
					if (fileids[lInt] != null && !"".equals(fileids[lInt]))
					{
						fileArray[lInt] = fileids[lInt];
						frnArr[lInt] = frnNos[lInt];
						empArr[lInt] = emps[lInt];
						emprArr[lInt] = emprs[lInt];
						remarkArr[lInt] = remarks[lInt];
				
					}
				}

				for (Integer lInt = 0; lInt < fileids.length; lInt++)
				{
					updateFrnNo= new UpdateFrnNo();
					
					Long uploadId = IFMSCommonServiceImpl.getNextSeqNum("HST_FRN_DTLS", inputMap);
					System.out.println("file id:"+fileArray[lInt]);
					System.out.println("frn no:"+frnArr[lInt]);
					String oldfrn = null;
					
					updateFrnNo.setFrnhstid(uploadId);
					
					updateFrnNo.setCreatedDate(d);
					updateFrnNo.setUpdatedDate(null);
					updateFrnNo.setFileName(fileArray[lInt]);
					
					oldfrn = lObjcheck.oldFrn(fileArray[lInt]);
					updateFrnNo.setOldFrn(oldfrn);
					updateFrnNo.setNewFrnNo(frnArr[lInt]);
					updateFrnNo.setEmpAmount(empArr[lInt]);
					updateFrnNo.setEmplyrAmount(emprArr[lInt]);
					updateFrnNo.setRemarks(remarkArr[lInt]);
	
				
					 lObjcheck.updateFrnNo(fileArray[lInt],frnArr[lInt]);
					lObjUploadFrnNo.create(updateFrnNo);
					uploadId=null;
                 
                     
				}
				

			}
			flag = "Updated";
			
			StringBuffer strbuflag = new StringBuffer();
			logger.info("Flag is  ********"+flag);

			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
			strbuflag.append(flag);
			logger.info("Flag is  ********"+flag);

			strbuflag.append("</Flag>");
			strbuflag.append("</XMLDOC>");
			logger.info("Flag is  ********"+flag);

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
			logger.info("Flag is  ********"+flag);
		 
		/* 
		 objRes.setResultValue(inputMap);
		 objRes.setViewName("updatePranNo");*/
		 
	  }
	  
	 catch (Exception e) {
		 resObj.setResultValue(null);
		 resObj.setThrowable(e);
		 resObj.setResultCode(ErrorConstants.ERROR);
		 resObj.setViewName("errorPage");
			//e.printStackTrace();
			//return objRes;
		// TODO: handle exception
	}
	 return resObj;
	}

	}

