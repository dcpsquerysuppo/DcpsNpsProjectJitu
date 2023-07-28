package com.tcs.sgv.dcps.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.PUBLIC_MEMBER;

import com.tcs.sgv.common.dao.CmnLookupMstDAO;
import com.tcs.sgv.common.dao.CmnLookupMstDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.BankBranchMstDaoImpl;
import com.tcs.sgv.dcps.dao.EmpGPFDetailsDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.ViewReportDAO;
import com.tcs.sgv.dcps.dao.ViewReportDaoImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.eis.dao.EmpStatisticsDDOwiseDAOImpl;
import com.tcs.sgv.eis.dao.PayBillDAOImpl;
import com.tcs.sgv.eis.valueobject.HrPayGpfBalanceDtls;

public class EmpGPFDetailsServiceImpl extends ServiceImpl {
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

	private void setSessionInfo(Map inputMap) {
		try {
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
	
	public ResultObject viewEmpGPFSeriesReport(Map<String, Object> inputMap) throws Exception{
    	logger.info("Inside Get viewEmpGPFSeriesReport");
    	ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
    	ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
    	HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
    	Long lLngLocId = null;
    	OrgDdoMst lObjDdoMst = null;
    	String lStrDdocode = null;
    	String lFlag = null;
    	try{
    		PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());    		
    		Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
    		String locId=loginDetailsMap.get("locationId").toString();
    		
    		//lFlag = StringUtility.getParameter("empStat",request);
    		if(!"".equals(locId)){
    			lLngLocId = Long.parseLong(locId);
    		}
    		//if(lFlag.equals("Y")){
		    	List<OrgDdoMst> lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(lLngLocId);
		    	if(!lLstDDOList.isEmpty() && lLstDDOList != null){
		    		lObjDdoMst = lLstDDOList.get(0);
		    		lStrDdocode = lObjDdoMst.getDdoCode();
		    //	}
    		}else if(lFlag.equals("N")){
    			lStrDdocode = StringUtility.getParameter("Ddocode",request);
    		}
    		
    		logger.info("DDO Code is "+lStrDdocode);
    		List lLstEmpGPFDetails = null;
    		List lLstGPF= null;
    		EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	
    		lLstEmpGPFDetails = lObjEmpGPFDetailsDAOImpl.getEMpGPFDetails(lStrDdocode);
    		lLstGPF = lObjEmpGPFDetailsDAOImpl.getGPFSeriesList();
    		
    		Date lDtcurDate = SessionHelper.getCurDate();
			SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			inputMap.put("lDtCurDate", lObjDateFormat.format(lDtcurDate));
			
			inputMap.put("DDOCode", lStrDdocode);
    		inputMap.put("lLstGPF", lLstGPF);
	    	inputMap.put("lLstEmpGPFDetails", lLstEmpGPFDetails);
	    	inputMap.put("Ddocode", lStrDdocode);
	    	inputMap.put("lFlag", lFlag);
			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object
			resultObject.setViewName("EmpGPFSeriesReport");//set view name
			
    	}catch(Exception e){
    		resultObject = new ResultObject(ErrorConstants.ERROR);
    		resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in loadEmpDtlsDdoWise "+ e);
    	}
    	return resultObject;
   }
	
	public ResultObject savePFDetails(Map<String, Object> inputMap) throws Exception{
		logger.info("Inside Get viewEmpGPFSeriesReport");
    	ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
    	ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
    	HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
    	Long lLngLocId = null;
    	OrgDdoMst lObjDdoMst = null;
    	String lStrDdocode = null;
    	String lFlag = null;
    	try{
    		PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());    		
    		Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
    		String locId=loginDetailsMap.get("locationId").toString();
    		long userID = Long.parseLong(loginDetailsMap.get("userId").toString());
			long setPostId = Long.parseLong(loginDetailsMap.get("loggedInPost").toString());
			
    		String DDOCode = StringUtility.getParameter("DDOCode",request) != null ? StringUtility.getParameter("DDOCode",request).toString() :"";
    		String sevaarthId = StringUtility.getParameter("sevaarthId",request) != null ? StringUtility.getParameter("sevaarthId",request).toString() :"";
    		String empID = StringUtility.getParameter("empId",request) != null ? StringUtility.getParameter("empId",request).toString() :"";
    		String oldDOB = StringUtility.getParameter("oldDOB",request) != null ? StringUtility.getParameter("oldDOB",request).toString() :"";
    		//String newDOB = StringUtility.getParameter("newDOB",request) != null ? StringUtility.getParameter("newDOB",request).toString() :"";
    		String newGPFSeries = StringUtility.getParameter("newGPFSeries",request) != null ? StringUtility.getParameter("newGPFSeries",request).toString() :"";
    		String oldGPFSeries = StringUtility.getParameter("oldGPFSeries",request) != null ? StringUtility.getParameter("oldGPFSeries",request).toString() :"";
    		String newGPFAccountNo = StringUtility.getParameter("newGPFNo",request) != null ? StringUtility.getParameter("newGPFNo",request).toString() :"";
    		String oldGPFAcoountNo = StringUtility.getParameter("oldGPFNo",request) != null ? StringUtility.getParameter("oldGPFNo",request).toString() :"";
    		String remarks = StringUtility.getParameter("Remarks",request) != null ? StringUtility.getParameter("Remarks",request).toString() :"";
    		
    		String arrsevaarthID[] = null;
    		String arrEMPId[] = null;
    		String arroldDOB[] = null;
    		String arrnewDOB[] = null;
    		String arrnewGPFSeries[] = null;
    		String arroldGPFSeries[] = null;
    		String arrnewGPFAccountNo[] = null;
    		String arroldGPFAcoountNo[] = null;
    		String remarksArray[] = null;
    		
    		
    		if(sevaarthId != "")
    			arrsevaarthID = sevaarthId.split("~");
    		if(empID != "")
    			arrEMPId = empID.split("~");
    		if(oldDOB != "")
    			arroldDOB = oldDOB.split("~");
    		/*if(newDOB != "")
    			arrnewDOB = newDOB.split("~");*/
    		if(newGPFSeries != "")
    			arrnewGPFSeries = newGPFSeries.split("~");
    		if(oldGPFSeries != "")
    			arroldGPFSeries = oldGPFSeries.split("~");
    		if(newGPFAccountNo != "")
    			arrnewGPFAccountNo = newGPFAccountNo.split("~");
    		if(oldGPFAcoountNo != "")
    			arroldGPFAcoountNo = oldGPFAcoountNo.split("~");
    		if(remarks != "")
    			remarksArray = remarks.split("~");
    		
    		EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	
    		
    		int presentOrNot =  0;
    		int i = 0;
    		for( i =0; i < arrEMPId.length; i++){
    			presentOrNot = lObjEmpGPFDetailsDAOImpl.checkPresentOrNot( DDOCode,  arrEMPId[i],  arrsevaarthID[i]);
    			if(presentOrNot != 0){
    				/*if(arrnewDOB[i].equals("-1"))
    					arrnewDOB[i] = null;*/
    				if(arrnewGPFAccountNo[i].equals("-1"))
    					arrnewGPFAccountNo[i] = null;
    				lObjEmpGPFDetailsDAOImpl.updateGPFAndDOB( DDOCode,  arrEMPId[i],  arrsevaarthID[i],
    						arrnewGPFSeries[i],arroldGPFSeries[i],arrnewGPFAccountNo[i],arroldGPFAcoountNo[i],
    						arroldDOB[i],userID,remarksArray[i]);    				
    			}
    			if(presentOrNot == 0){
    				/*if(arrnewDOB[i].equals("-1"))
    					arrnewDOB[i] = null;*/
    				if(arrnewGPFAccountNo[i].equals("-1"))
    					arrnewGPFAccountNo[i] = null;
    				lObjEmpGPFDetailsDAOImpl.insertGpfBirthDtls( DDOCode,  arrEMPId[i],  arrsevaarthID[i],
    						arrnewGPFSeries[i],arroldGPFSeries[i],arrnewGPFAccountNo[i],arroldGPFAcoountNo[i],
    						arroldDOB[i],userID,remarksArray[i]);  
    			}
    		}
    		logger.info("i before  "+i);
    		String msg = "";
    		
    		logger.info("arrEMPId length "+arrEMPId.length);
    		if(i ==  arrEMPId.length)
    			msg = "Success";
    		else msg ="Failure";
    		
    		StringBuffer returnMsg = new StringBuffer();
			returnMsg.append("<msg>");
			returnMsg.append(msg);
			returnMsg.append("</msg>");
			logger.info("msg "+msg);
			Map result = new  HashMap();
			String stateNameIdStr = new AjaxXmlBuilder().addItem("ajax_key", returnMsg.toString()).toString();
			logger.info("stateNameIdStr "+stateNameIdStr);
			result.put("ajaxKey", stateNameIdStr);
			resultObject.setResultValue(result);
			resultObject.setViewName("ajaxData");
    	}
    	catch(Exception e){
    		resultObject = new ResultObject(ErrorConstants.ERROR);
    		resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
    		logger.error("Error in savePFDetails"+e);
    	}
    	return resultObject;
	}
	
	public ResultObject viewReportInMDC(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		logger.info("in mdc reports..");
		String treasuryLocCode="";
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);
			
		
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	
    		
			/* Get User(ATO or TO) and Use type */
			String lStrUserType = StringUtility.getParameter("User", request).trim();
			treasuryLocCode=StringUtility.getParameter("cmbAGName", request).trim();
			if(treasuryLocCode!=null && treasuryLocCode.equals(""))
				treasuryLocCode=gStrLocationCode;
			String lStrUseType = StringUtility.getParameter("Use", request).trim();

			String lStrContiType = null;

			if (!StringUtility.getParameter("Type", request).equalsIgnoreCase(
					"")
					&& StringUtility.getParameter("Type", request) != null) {
				lStrContiType = StringUtility.getParameter("Type", request);
			}

			

			List treasuries = null;
			
			treasuries = lObjEmpGPFDetailsDAOImpl.getAllTresasuriesList();
				
			
			//inputMap.put("AGNAME", gStrUserId);
			inputMap.put("TREASURIES", treasuries);			
			resObj.setResultValue(inputMap);
            resObj.setViewName("viewEmpGPFReport");
		
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		
		return resObj;
	}

	public ResultObject viewBBWiseReportInMDC(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		logger.info("in mdc reports..");
		String treasuryLocCode="";
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);
			
		
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	
    		
			/* Get User(ATO or TO) and Use type */
		/*	String lStrUserType = StringUtility.getParameter("User", request).trim();
			treasuryLocCode=StringUtility.getParameter("cmbAGName", request).trim();
			if(treasuryLocCode!=null && treasuryLocCode.equals(""))
				treasuryLocCode=gStrLocationCode;
			String lStrUseType = StringUtility.getParameter("Use", request).trim();

			String lStrContiType = null;

			if (!StringUtility.getParameter("Type", request).equalsIgnoreCase(
					"")
					&& StringUtility.getParameter("Type", request) != null) {
				lStrContiType = StringUtility.getParameter("Type", request);
			}

			*/
			

			List treasuries = null;
			treasuries = lObjEmpGPFDetailsDAOImpl.getAllTresasuriesList();
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			/* Get User(ATO or TO) and Use type */
			List lLstBankNames = lObjDcpsCommonDAO.getBankNames();
			inputMap.put("BANKNAMES", lLstBankNames);
			inputMap.put("Screen", "Bank");
			inputMap.put("User", "MDC");
			
			logger.info("lLstBankNames "+lLstBankNames.size());
			
			//inputMap.put("AGNAME", gStrUserId);
			inputMap.put("TREASURIES", treasuries);			
			resObj.setResultValue(inputMap);
            resObj.setViewName("viewEmpbankBranchwiseReport");
		
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		
		return resObj;
	}
	
	public ResultObject viewEmpBankBranchWiseReport(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		logger.info("in viewEmpBankBranchWiseReport reports..");
		String treasuryLocCode="";
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
    		String locId=loginDetailsMap.get("locationId").toString();
    		PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());
    		OrgDdoMst lObjDdoMst = null;
    		String lStrDdocode = "";
    		String ddoName = "";
    		String ddoOfcname = "";
    		List<OrgDdoMst> lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(Long.parseLong(locId));
			if(!lLstDDOList.isEmpty() && lLstDDOList != null){
				lObjDdoMst = lLstDDOList.get(0);
				lStrDdocode = lObjDdoMst.getDdoCode();
				ddoName = lObjDdoMst.getDdoName();
				ddoOfcname = lObjDdoMst.getDdoOffice();
			}
			
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	
		
			List bankDtls = lObjEmpGPFDetailsDAOImpl.getDDOWiseBankDtls(lStrDdocode);
			logger.info("lLstBankNames "+bankDtls.size());
			inputMap.put("BANKNAMES", bankDtls);
			inputMap.put("User", "DDO");
			resObj.setResultValue(inputMap);
            resObj.setViewName("viewEmpbankBranchwiseReport");
		
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		
		return resObj;
	}
	
	public ResultObject getBranchListForDDO(Map inputMap) throws Exception {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		String lStrDdoCode = null;
		logger.info("in viewEmpBankBranchWiseReport reports..");
		String treasuryLocCode="";
		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
    		String locId=loginDetailsMap.get("locationId").toString();
    		PayBillDAOImpl lObjBillDAO = new PayBillDAOImpl(null,serv.getSessionFactory());
    		OrgDdoMst lObjDdoMst = null;
    		String lStrDdocode = "";
    		String ddoName = "";
    		String ddoOfcname = "";
    		List<OrgDdoMst> lLstDDOList = lObjBillDAO.getDDOCodeByLoggedInlocId(Long.parseLong(locId));
			if(!lLstDDOList.isEmpty() && lLstDDOList != null){
				lObjDdoMst = lLstDDOList.get(0);
				lStrDdocode = lObjDdoMst.getDdoCode();
				ddoName = lObjDdoMst.getDdoName();
				ddoOfcname = lObjDdoMst.getDdoOffice();
			}
			String bankCode = StringUtility.getParameter("BankCode", request).trim();
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	
			
			List branchDtls = lObjEmpGPFDetailsDAOImpl.getDDOWiseBranchDtls(lStrDdocode,bankCode);
			logger.info("lLstBankNames "+branchDtls.size());
			inputMap.put("BANKNAMES", branchDtls);
			inputMap.put("User", "DDO");			
		
            logger.info("AJAXXXXX");
			String ajaxResult= null;
			
			ajaxResult = new AjaxXmlBuilder().addItems(branchDtls, "desc", "id").toString();
			logger.info("Ajax result:" +ajaxResult);	
			inputMap.put("ajaxKey", ajaxResult);
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("ajaxData");
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		
		return resObj;
	}
	
	public ResultObject getPaybillUpdateDetls(Map inputMap){
		ResultObject resObj = new ResultObject();
		try{
			setSessionInfo(inputMap);
			SimpleDateFormat sdfObj = new SimpleDateFormat("yyyy");
			SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd");

			Date dt = new Date();
			String Year=(sdfObj.format(dt)).toString();
			sdfObj = new SimpleDateFormat("MM");
			int Month= Integer.parseInt((sdfObj.format(dt)).toString());
			 CmnLookupMstDAO lookupDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
     		
			List monthList = lookupDAO.getAllChildrenByLookUpNameAndLang("Month", gLngLangId);
    		List yearList = lookupDAO.getAllChildrenByLookUpNameAndLang("Year", gLngLangId);
    		
			inputMap.put("monthList", monthList);
			inputMap.put("yearList", yearList);
			inputMap.put("curmonth", Month);
			inputMap.put("curyear", Year);
			inputMap.put("size", "-1");
			resObj.setResultValue(inputMap);
			resObj.setViewName("changePaybillStatus");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return resObj;
	}
	
	public ResultObject updatePaybillDetls(Map inputMap){
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try{
			setSessionInfo(inputMap);
			
			String selMonth = request.getParameter("Month");
			String selYear = request.getParameter("Year");
			String billNo = request.getParameter("billNo");
			String flag = request.getParameter("flag");
			String currStatus = request.getParameter("currStatus");
			String newStatus = request.getParameter("newStatus");
			int msg = 0;
			List paybillDtls = null;
			EmpGPFDetailsDAOImpl lObjEmpGPFDetailsDAOImpl = new EmpGPFDetailsDAOImpl(null ,serv.getSessionFactory());    	
			int size = 0;
			if(selMonth != null && selYear != null && billNo != null && flag != null){
				if(flag.equals("No")){
					paybillDtls = lObjEmpGPFDetailsDAOImpl.getPaybillDtls(selMonth,selYear,billNo);
					if(paybillDtls != null)
						size = paybillDtls.size();
				}
				if(flag.equals("Yes")){
					msg = lObjEmpGPFDetailsDAOImpl.updatePaybillStatus(selMonth,selYear,billNo,currStatus,newStatus);
					paybillDtls = lObjEmpGPFDetailsDAOImpl.getPaybillDtls(selMonth,selYear,billNo);
					if(paybillDtls != null)
						size = paybillDtls.size();
				}
			}
			SimpleDateFormat sdfObj = new SimpleDateFormat("yyyy");
			SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd");

			Date dt = new Date();
			String Year=(sdfObj.format(dt)).toString();
			sdfObj = new SimpleDateFormat("MM");
			int Month= Integer.parseInt((sdfObj.format(dt)).toString());
			 CmnLookupMstDAO lookupDAO = new CmnLookupMstDAOImpl(CmnLookupMst.class,serv.getSessionFactory());
     		
			List monthList = lookupDAO.getAllChildrenByLookUpNameAndLang("Month", gLngLangId);
    		List yearList = lookupDAO.getAllChildrenByLookUpNameAndLang("Year", gLngLangId);
    		
    		inputMap.put("selMonth",selMonth);
    		inputMap.put("selYear", selYear);
    		inputMap.put("billNo", billNo);
    		inputMap.put("size", size);
			inputMap.put("monthList", monthList);
			inputMap.put("yearList", yearList);
			inputMap.put("curmonth", Month);
			inputMap.put("curyear", Year);
			inputMap.put("paybillDtls", paybillDtls);
			inputMap.put("msg", msg);
			
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("changePaybillStatus");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return resObj;
	}
	

	public ResultObject checkEmpGPFAccDetails(Map inputMap){
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		
		String pfSeries=null;
		String pfAccNo=null;
		Long finalCheckFlag=null;
		String lStrResult = null;
		try {
			
			NewRegDdoDAO lObjNewRegDdoDAO = new NewRegDdoDAOImpl(MstEmp.class,
					serv.getSessionFactory());
			
			String res=lObjNewRegDdoDAO.checkPfDetails(pfSeries,pfAccNo);
			
			pfSeries=StringUtility.getParameter("pfSeries", request).trim();
			pfAccNo=StringUtility.getParameter("pfAcNo", request).trim();
			NewRegDdoDAOImpl objNewRegDdoDaoImpl=new NewRegDdoDAOImpl(HrPayGpfBalanceDtls.class,serv.getSessionFactory());
			finalCheckFlag=objNewRegDdoDaoImpl.checkPFAccountNumberForGPFDetails(pfSeries,pfAccNo);
			
			String status=null;
			if(finalCheckFlag>0){
				status="wrong";
			}

			else{
				status="correct";
			}
			String lSBStatus = getResponseXMLDocForDDOFwd(status).toString();
			lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();
			
			inputMap.put("ajaxKey", lStrResult);
			objRes.setResultValue(inputMap);
			objRes.setViewName("ajaxData");
			
		} catch (Exception e) {
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	private StringBuilder getResponseXMLDocForDDOFwd(String status) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(status);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	
	
}
