package com.tcs.sgv.dcps.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.sql.rowset.serial.SerialBlob;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute.Use;

import bds.authorization.MapConverter;
import bds.authorization.PayrollBEAMSIntegrateWS;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.dao.GpfLnaDashboardDao;
import com.tcs.sgv.common.dao.GpfLnaDashboardDaoImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.common.valueobject.TrnNPSBeamsIntegration;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.Service;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.FormS1DAO;
import com.tcs.sgv.dcps.dao.FormS1DAOImpl;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAO;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.dao.NsdlNpsDAOImpl;
import com.tcs.sgv.dcps.dao.SearchEmployeeDAO;
import com.tcs.sgv.dcps.dao.SearchEmployeeDAOImpl;
import com.tcs.sgv.dcps.dao.TierIISixPcArrearDao;
import com.tcs.sgv.dcps.dao.TierIISixPcArrearDaoImpl;
import com.tcs.sgv.dcps.dao.TreasuryDAO;
import com.tcs.sgv.dcps.dao.TreasuryDAOImpl;
import com.tcs.sgv.dcps.report.PendingNPSContriReportDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.eis.service.IdGenerator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TierIISixPcArrearServiceImpl extends ServiceImpl   {
	
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
	/* Global Variable for Logger Class */

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

	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/DCPSConstants");
	
	private ResourceBundle integrationBundleConst = ResourceBundle.getBundle("resources/common/IFMSIntegration_en_US");


	/* Global Variable for User Location */
	String gStrUserLocation = null;

	private Long lLngLocId;

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
			lLngLocId = Long.valueOf(SessionHelper.getLocationCode(inputMap));
			gStrUserId = gLngUserId.toString();
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
			gDtCurrDt = SessionHelper.getCurDate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ResultObject getEmpListForFiveInstApprove(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstEmpForFiveInstApprove=null;
		List lstDdocodeList=null;
		List lstEmpForFiveInstApprove2=new ArrayList();
		List lstEmpForFiveInstApprove3=new ArrayList();
 		String strDDOCode=null;
		String strRoleId=null;
		int DepSize = 100;
		try
		{	
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
			strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			strRoleId=lObjSearchEmployeeDAO.getRoleId(gLngPostId);
			
			String DddoCode = StringUtility.getParameter("dcpsEmpId", request);  
			String DddoCode1 = StringUtility.getParameter("DddoCode", request); 
			String genbill = StringUtility.getParameter("genbill", request); 
			String interestFlag = StringUtility.getParameter("Interest", request).trim();
			String searchSeva = StringUtility.getParameter("searchSeva", request).trim();
			String searchEmp = StringUtility.getParameter("searchEmp", request).trim();
			String searchDDO = StringUtility.getParameter("searchDDO", request).trim();
			String UpdateInst = StringUtility.getParameter("UpdateInst", request).trim();
			/*String interestApproved="NO";*/
			
			if(UpdateInst.equals("YES")){////$t162022 updateinst new Screen
				lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApproveUpdateInst(gStrLocationCode,searchSeva,searchEmp);////$t29Oct2021
            	DepSize=lstEmpForFiveInstApprove.size();
            	
            	if(lstEmpForFiveInstApprove!=null && lstEmpForFiveInstApprove.size()>0){
            	  //for(int j=0;j<lstEmpForFiveInstApprove.size();j++){
					Object[] obj=(Object[]) lstEmpForFiveInstApprove.get(0);////obj[9].toString().trim()
					
					gLogger.info("##################DepSize"+obj[10]); 
					
					inputMap.put("EmpName", obj[1].toString().trim());
					inputMap.put("SevarthID", obj[2].toString().trim());
					inputMap.put("DcpsID", obj[3].toString().trim());
					inputMap.put("InstI", obj[5].toString().trim());
					inputMap.put("InstINo", obj[6].toString().trim());
					inputMap.put("InstIDate", obj[7]);
					inputMap.put("InstII", obj[9].toString().trim());
					inputMap.put("InstIINo", obj[10].toString().trim());
					inputMap.put("InstIIDate", obj[11]);
					inputMap.put("InstIII", obj[13].toString().trim());
					inputMap.put("InstIIINo", obj[14].toString().trim());
					inputMap.put("InstIIIDate", obj[15]);
					inputMap.put("InstIV", obj[17].toString().trim());
					inputMap.put("InstIVNo", obj[18].toString().trim());
					inputMap.put("InstIVDate", obj[19]);
					inputMap.put("InstV", obj[21].toString().trim());
					inputMap.put("InstVNo", obj[22].toString().trim());
					inputMap.put("InstVDate", obj[23]);
					inputMap.put("Reason", obj[24].toString().trim());
				  //}
			    }
            	
				gLogger.info("##################DepSize"+DepSize); 
				inputMap.put("strRoleId", strRoleId);
				inputMap.put("DepSize", DepSize);
				
				resObj.setResultValue(inputMap);
				resObj.setViewName("TierIIUpdateInst");	
			}else{
			if(strRoleId.equalsIgnoreCase("700002"))
			{	
				if(genbill.equalsIgnoreCase("bill"))
	            {
	            	lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApprove(strDDOCode,gStrLocationCode,"700008",interestFlag,searchSeva,searchEmp);////$t29Oct2021
	            	DepSize=lstEmpForFiveInstApprove.size();
					gLogger.info("##################DepSize"+DepSize); 
					inputMap.put("strRoleId", strRoleId);
					inputMap.put("DepSize", DepSize);
					inputMap.put("empList", lstEmpForFiveInstApprove);
					inputMap.put("DDOCode", strDDOCode); 
					inputMap.put("strBillId", "11");
					resObj.setResultValue(inputMap);
					resObj.setViewName("EmpListForFiveInstApprove");
	            }
				else
				{
				/*if(StringUtility.getParameter("Interest", request).trim().equals("Y")){////$t 29Oct2021
				interestApproved="YES";
				lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApprove(strDDOCode,gStrLocationCode,strRoleId,interestApproved);////$t29Oct2021
				}else{
				lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApprove(strDDOCode,gStrLocationCode,strRoleId,interestApproved);////$t29Oct2021
				}*/
					
					lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApprove(strDDOCode,gStrLocationCode,strRoleId,interestFlag,searchSeva,searchEmp);////$t29Oct2021

				//for iterator
				Iterator it =lstEmpForFiveInstApprove.iterator();
				Object[] tuple = null;
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					String A=tuple[0].toString();
					String B=tuple[1].toString();
					String C=tuple[2].toString();
					String D=tuple[3].toString();
					String E=tuple[4].toString();
					String F=tuple[5].toString();
					String G=tuple[6].toString();
					String H=tuple[7].toString();
					String I=tuple[8].toString();
					String J=tuple[9].toString();
					String K=tuple[10].toString();
					String L=tuple[11].toString();
					String M=tuple[12].toString();
					String N=tuple[13].toString();
					String O=tuple[14].toString();
					System.out.println("13-->"+O);
					System.out.println("13-->"+N);
					//String P=tuple[15].toString();
					//String Q=tuple[16].toString();
					
					/*Double Int = 0.0d;
					if(interestApproved.equals("NO"))*//////$t29Oct2021
					
					Double Int;
					if(StringUtility.getParameter("Interest", request).trim().equals("Y")){////$t 29Oct2021
					Int=InterestCalculation(Double.parseDouble(C),Double.parseDouble(E),Double.parseDouble(G),Double.parseDouble(I),Double.parseDouble(K),"Regular");
					}else{
						//String R=tuple[17].toString();
						//System.out.print("-->"+Double.parseDouble(R));
						if(tuple[15]==null)
						Int = 0d;
						else
						Int =Double.parseDouble(tuple[15].toString());
					}
					lstEmpForFiveInstApprove2=new ArrayList();
					lstEmpForFiveInstApprove2.add(A);
					lstEmpForFiveInstApprove2.add(B);
					lstEmpForFiveInstApprove2.add(C);
					lstEmpForFiveInstApprove2.add(D);
					lstEmpForFiveInstApprove2.add(E);
					lstEmpForFiveInstApprove2.add(F);
					lstEmpForFiveInstApprove2.add(G);
					lstEmpForFiveInstApprove2.add(H);
					lstEmpForFiveInstApprove2.add(I);
					lstEmpForFiveInstApprove2.add(J);
					lstEmpForFiveInstApprove2.add(K);
					lstEmpForFiveInstApprove2.add(L);
					lstEmpForFiveInstApprove2.add(M);
					lstEmpForFiveInstApprove2.add(N);
					lstEmpForFiveInstApprove2.add(O);
					//lstEmpForFiveInstApprove2.add(P);
					//lstEmpForFiveInstApprove2.add(Q);
					lstEmpForFiveInstApprove2.add(Int);
					lstEmpForFiveInstApprove2.add(tuple[16].toString());
					lstEmpForFiveInstApprove2.add(tuple[17].toString());
					lstEmpForFiveInstApprove3.add(lstEmpForFiveInstApprove2);
					System.out.println("size-->"+lstEmpForFiveInstApprove3.size());
				}
				//end
				DepSize=lstEmpForFiveInstApprove.size();
				gLogger.info("##################DepSize"+DepSize); 
				inputMap.put("strRoleId", strRoleId);
				inputMap.put("DepSize", DepSize);
				inputMap.put("empList", lstEmpForFiveInstApprove3);
				inputMap.put("DDOCode", strDDOCode);
				inputMap.put("interestFlag", interestFlag);
				resObj.setResultValue(inputMap);
				
				/*gLogger.info("##################Interest"+StringUtility.getParameter("Interest", request).trim());
				if(StringUtility.getParameter("Interest", request).trim().equals("Y"))////$t 29Oct2021
				resObj.setViewName("TierIIInterestCalculation");
				else
				resObj.setViewName("EmpListForFiveInstApprove");*/
				
				resObj.setViewName("EmpListForFiveInstApprove");
				}
			}else{
             if(DddoCode.equalsIgnoreCase("DDO"))/////$t11Feb22
              {
            	lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApprove(DddoCode1,genbill,strRoleId,interestFlag,searchSeva,searchEmp);////$t29Oct2021
            	DepSize=lstEmpForFiveInstApprove.size();
				gLogger.info("##################DepSize"+DepSize); 
				inputMap.put("strRoleId", strRoleId);
				inputMap.put("DepSize", DepSize);
				inputMap.put("empList", lstEmpForFiveInstApprove);
				inputMap.put("DDOCode", DddoCode1);
				inputMap.put("orderId", genbill);
				
				resObj.setResultValue(inputMap);
				resObj.setViewName("EmpListForFiveInstApprove");	
               }else{
				lstDdocodeList=lObjSearchEmployeeDAO.getDdoSearchList(gStrLocationCode,searchDDO);
				DepSize=lstDdocodeList.size();
				inputMap.put("DDOCodeList", lstDdocodeList);
				inputMap.put("DDOCode", DddoCode); 
				inputMap.put("DepSize", DepSize);
				resObj.setResultValue(inputMap);
				resObj.setViewName("TierIIDdoScreen");
            }
		  }
		}
	  }catch (Exception e){
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
	  }
	  return resObj;
	}
	
	
	//to generate bill 555
	public ResultObject generateTierTwoBill(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
		
		try {
			String status = "N";
			String dcpsEmpId = StringUtility.getParameter("dcpsEmpId", request);
			String Interest = StringUtility.getParameter("Interest", request);
			String GrandTotalAmount = StringUtility.getParameter("GrandTotalAmount", request);
			String TotalAMount = StringUtility.getParameter("TotalAMount", request);
			String dcpsEmp_Id[] = dcpsEmpId.split(",");
			String strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			
			String ArrayDcpsID = StringUtility.getParameter("ArrayDcpsID", request);
			String ArrayTotalAmount = StringUtility.getParameter("ArrayTotalAmount", request);
			String ArrayInterest = StringUtility.getParameter("ArrayInterest", request);
			String IsDeputation = StringUtility.getParameter("Deputation", request);
			
			String lSBStatus;
			if(IsDeputation.equals("Y")){
			//strDDOCode = StringUtility.getParameter("cmbOfficeCode", request).trim();
			lSBStatus = TierIISixPcArrearDao.generateTierTwoBill(strDDOCode,Interest,TotalAMount,GrandTotalAmount,dcpsEmp_Id.length,dcpsEmpId,ArrayDcpsID,ArrayTotalAmount,ArrayInterest,IsDeputation);
			}else{
			lSBStatus = TierIISixPcArrearDao.generateTierTwoBill(strDDOCode,Interest,TotalAMount,GrandTotalAmount,dcpsEmp_Id.length,dcpsEmpId,ArrayDcpsID,ArrayTotalAmount,ArrayInterest,IsDeputation);
			}
			
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	
	public ResultObject empListOfFiveInstUpdateTO(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
		String IsDeputation="";

		try {
			/*Namuna F*/
			
			String status = "N";
			String dcpsEmpId = StringUtility.getParameter("dcpsEmpId", request);
			String Interest = StringUtility.getParameter("Interest", request);
			String GrandTotalAmount = StringUtility.getParameter("GrandTotalAmount", request);
			String TotalAMount = StringUtility.getParameter("TotalAMount", request);
			String dcpsEmp_Id[] = dcpsEmpId.split(",");
			String strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			
			String ArrayDcpsID = StringUtility.getParameter("ArrayDcpsID", request);
			String ArrayTotalAmount = StringUtility.getParameter("ArrayTotalAmount", request);
			String ArrayInterest = StringUtility.getParameter("ArrayInterest", request);
			String Interest_split[] = ArrayInterest.split("~");

			
			
			/*End*/
			
			if (dcpsEmp_Id != null && dcpsEmp_Id.length > 0) {
				String BillNo=TierIISixPcArrearDao.generateOrderF(strDDOCode,Interest,TotalAMount,GrandTotalAmount,dcpsEmp_Id.length,dcpsEmpId,ArrayDcpsID,ArrayTotalAmount,ArrayInterest);

				for (Integer lInt = 0; lInt < dcpsEmp_Id.length; lInt++) {
					TierIISixPcArrearDao.empListOfFiveInstUpdateTO(dcpsEmp_Id[lInt],Interest_split[lInt],BillNo,IsDeputation);
					status = BillNo;
				}

				String lSBStatus = getResponseUpdatedTO(BillNo).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}

	
	public ResultObject empListOfFiveInstUpdateTOUpdateInterest(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());

		try {
			//Namuna F
			
			String status = "N";
			String dcpsEmpId = StringUtility.getParameter("dcpsEmpId", request);
			String dcpsEmp_Id[] = dcpsEmpId.split(",");
			String ArrayInterest = StringUtility.getParameter("ArrayInterest", request);
			String Interest_split[] = ArrayInterest.split("~");
			String ArrayGrandTotalAmount = StringUtility.getParameter("ArrayGrandTotalAmount", request);
			String ArrayTotalAmount_split[] = ArrayGrandTotalAmount.split("~");
			String IsDeputation = StringUtility.getParameter("Deputation", request);
			
			
	/*		String Interest = StringUtility.getParameter("Interest", request);
			
			String TotalAMount = StringUtility.getParameter("TotalAMount", request);
			
			String strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			
			String ArrayDcpsID = StringUtility.getParameter("ArrayDcpsID", request);
			String ArrayTotalAmount = StringUtility.getParameter("ArrayTotalAmount", request);*/
			
			
			
			if (dcpsEmp_Id != null && dcpsEmp_Id.length > 0) {
				int totalInterest = 0;
				for (Integer lInt = 0; lInt < dcpsEmp_Id.length; lInt++) {
					//totalInterest=totalInterest+Integer.parseInt(Interest_split[lInt]);
					TierIISixPcArrearDao.empListNewTierIIInterestCalculation(dcpsEmp_Id[lInt],Interest_split[lInt],ArrayTotalAmount_split[lInt],IsDeputation);
					//TierIISixPcArrearDao.empListNewTierIIInterestCalculationPart(dcpsEmp_Id[lInt]);
					status = "Y";
				}

				String lSBStatus = getResponseUpdatedTO(status).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}

	
	public ResultObject updateInstAmtTO(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);

		try {
			String status = "N";

			String dcpsEmpId = StringUtility.getParameter("dcpsEmpId", request);
			String orderId = StringUtility.getParameter("orderId", request);
			String Deputation = StringUtility.getParameter("Deputation", request);

//			String dueDrawnAmt = StringUtility.getParameter("dueDrawnAmt", request);
			String dcpsEmp_Id[] = dcpsEmpId.split("~");
			System.out.println("dcpsEmpId-->" + dcpsEmpId);
//			String dueDrawn_Amt[] = dueDrawnAmt.split("~");
//			System.out.println("dueDrawnAmt-->" + dueDrawnAmt);

//			if ((dcpsEmp_Id != null && dcpsEmp_Id.length > 0)&&(dueDrawn_Amt != null && dueDrawn_Amt.length > 0)) {
			if ((dcpsEmp_Id != null && dcpsEmp_Id.length > 0)) {

				for (Integer lInt = 0; lInt < dcpsEmp_Id.length; lInt++) {
					TierIISixPcArrearDao.updateInstAmtTO(dcpsEmp_Id[lInt],null,orderId,Deputation);
					status = "Y";
				}

				String lSBStatus = getResponseUpdatedTO(status).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}

	public ResultObject emprejectByTo(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);

		try {
			String status = "N";
			String dcpsEmpId = StringUtility.getParameter("dcpsEmpId", request);
			String dcpsEmp_Id[] = dcpsEmpId.split("~");
			String Reason = StringUtility.getParameter("Reason", request);
			String Reason_Id[] = Reason.split("~");
			String orderId = StringUtility.getParameter("orderId", request);
			String Deputation = StringUtility.getParameter("Deputation", request);

			if (dcpsEmp_Id != null && dcpsEmp_Id.length > 0) {

				for (Integer lInt = 0; lInt < dcpsEmp_Id.length; lInt++) {
					TierIISixPcArrearDao.emprejectByTo(dcpsEmp_Id[lInt],Reason_Id[lInt],orderId,Deputation);
					status = "Y";
				}

				String lSBStatus = getResponseUpdatedTO(status).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	
	public Double InterestCalculation( Double Inst1,Double inst2,Double inst3,Double inst4,Double inst5, String Type ) throws Exception{
        Double Amount=0.00;		
        Double openingBalance=0D;
		Double closingBalance=0D;
		Double Total=0D;
		Double Interest=0D;
		Double TotalInterest=0D;
		Double yeadDays=365D;
		Double leapYeadDays=366D;
		TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
		List lstLoopingForTierII=lObjSearchEmployeeDAO.getLoopingFOrIntereseCalculations();
		
		for (int i=0;i<lstLoopingForTierII.size();i++)
		{
			Object[] tupleSub = (Object[]) lstLoopingForTierII.get(i);
			
			if(i==0)
			{
			Total=openingBalance+Inst1;
			 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,Inst1, yeadDays,Type);
			}
				
			if(i==1)
			{
				Total=openingBalance+inst2;
				 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,inst2,yeadDays,Type);
			}	
			if(i==2)
			{
				Total=openingBalance+inst3;
				 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,inst3,yeadDays,Type);
			}
				
			if(i==3){
				Total=openingBalance+inst4;
				 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,inst4,yeadDays,Type);
			}
				
			if(i==4)
			{
				Total=openingBalance+inst5;
				 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,inst5,yeadDays,Type);
			}
				
			if(i>4)
			{
				 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,0D,yeadDays,Type);
				 Total=openingBalance;	 
			}
			
			
		       if(tupleSub[0].toString().equals("35") && Type.equals("de")){			
				closingBalance=(double) (Math.round(Interest)+Math.round(Total));
				TotalInterest=(double) (Math.round(Interest)+Math.round(TotalInterest));
				openingBalance=closingBalance;
               }else if(tupleSub[0].toString().equals("34") && Type.equals("Regular")){			
   				closingBalance=(double) (Math.round(Interest)+Math.round(Total));
   				TotalInterest=(double) (Math.round(Interest)+Math.round(TotalInterest));
   				openingBalance=closingBalance;
                }else{
            	closingBalance=Interest+Total;
   				TotalInterest=Interest+TotalInterest;
   				openingBalance=closingBalance;
               }
			
		}
      
/*      
      // TODO Auto-generated method stub
		
//		Double Inst1=3001.00,inst2=3000.00,inst3=3000.00,inst4=3000.00,inst5=3000.00;
		Double Total1=0.00;
		Double O_balance=0.00;
		//for firstiteration 2009-2010
		Double Interest2009_10=Double.parseDouble(new DecimalFormat("##.##").format((304*Inst1*0.08)/365));
		Total1=Total1+Interest2009_10;
		O_balance=O_balance+(Inst1+Interest2009_10);

		//for second iteration 2010-2011
		Double Interest2010_11OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format(((Inst1+Interest2009_10)*0.08)));
		Total1=Total1+Interest2010_11OpeningBal1;
		Double Interest2010_11OpeningBal2=Double.parseDouble(new DecimalFormat("##.##").format((304*inst2*0.08)/365));
		Total1=Total1+Interest2010_11OpeningBal2;
		O_balance=O_balance+(inst2+Interest2010_11OpeningBal1+Interest2010_11OpeningBal2);

		//for third iteration 2011-2012
		Double opening2011_12=(Inst1+Interest2009_10+inst2+Interest2010_11OpeningBal1+Interest2010_11OpeningBal2);
		System.out.println(Inst1+" "+Interest2009_10+" "+inst2+" "+Interest2010_11OpeningBal1+" "+Interest2010_11OpeningBal2);
		Double Interest2011_12OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format((244*opening2011_12*0.08)/366));
		Total1=Total1+Interest2011_12OpeningBal1;
		Double Interest2011_12OpeningBal2=Double.parseDouble(new DecimalFormat("##.##").format((122*opening2011_12*0.086)/366));
		Total1=Total1+Interest2011_12OpeningBal2;
		Double Interest2011_12OpeningBal3=Double.parseDouble(new DecimalFormat("##.##").format((182*inst3*0.08)/366));
		Total1=Total1+Interest2011_12OpeningBal3;
		Double Interest2011_12OpeningBal4=Double.parseDouble(new DecimalFormat("##.##").format((122*inst3*0.086)/366));
		Total1=Total1+Interest2011_12OpeningBal4;
		O_balance=O_balance+Interest2011_12OpeningBal1+Interest2011_12OpeningBal2+Interest2011_12OpeningBal3+Interest2011_12OpeningBal4+inst3;
		
		//for fourth iteration 2012-2013
		Double Interest2012_13OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format((365*O_balance*0.088)/365));
		Double Interest2012_13OpeningBal2=Double.parseDouble(new DecimalFormat("##.##").format((304*inst4*0.088)/365));
		O_balance=O_balance+Interest2012_13OpeningBal1+Interest2012_13OpeningBal2+inst4;
		Total1=Total1+Interest2012_13OpeningBal1+Interest2012_13OpeningBal2;
	
		//for fifth iteration 2013-2014
		Double Interest2013_14OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format((365*O_balance*0.087)/365));
		Double Interest2013_14OpeningBal2=Double.parseDouble(new DecimalFormat("##.##").format((304*inst5*0.087)/365));
		O_balance=O_balance+Interest2013_14OpeningBal1+Interest2013_14OpeningBal2+inst5;
		Total1=Total1+Interest2013_14OpeningBal1+Interest2013_14OpeningBal2;
		
		//for Sixth iteration 2014-2015
		Double Interest2014_15OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format((365*O_balance*0.087)/365));
		O_balance=O_balance+Interest2014_15OpeningBal1;
		Total1=Total1+Interest2014_15OpeningBal1;
		
		//for Seventh iteration 2015-2016
				Double Interest2015_16OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format((365*O_balance*0.087)/366));
				O_balance=O_balance+Interest2015_16OpeningBal1;
				Total1=Total1+Interest2015_16OpeningBal1;
				
				//for Eight iteration 2016-2017
				Double Interest2016_17OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format((183*O_balance*0.081)/365));
				Double Interest2016_17OpeningBal2=Double.parseDouble(new DecimalFormat("##.##").format((182*O_balance*0.08)/365));
				O_balance=O_balance+Interest2016_17OpeningBal1+Interest2016_17OpeningBal2;
				Total1=Total1+Interest2016_17OpeningBal1+Interest2016_17OpeningBal2;
				
				//for nine iteration 2017-2018
				Double Interest2017_18OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format((91*O_balance*0.079)/365));
				Double Interest2017_18OpeningBal2=Double.parseDouble(new DecimalFormat("##.##").format((184*O_balance*0.078)/365));
				Double Interest2017_18OpeningBal3=Double.parseDouble(new DecimalFormat("##.##").format((90*O_balance*0.076)/365));
				O_balance=O_balance+Interest2017_18OpeningBal1+Interest2017_18OpeningBal2+Interest2017_18OpeningBal3;
				Total1=Total1+Interest2017_18OpeningBal1+Interest2017_18OpeningBal2+Interest2017_18OpeningBal3;
				
				
				//for Ten iteration 2018-2019
				Double Interest2018_19OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format((183*O_balance*0.076)/365));
				Double Interest2018_19OpeningBal2=Double.parseDouble(new DecimalFormat("##.##").format((182*O_balance*0.08)/365));
				O_balance=O_balance+Interest2018_19OpeningBal1+Interest2018_19OpeningBal2;
				Total1=Total1+Interest2018_19OpeningBal1+Interest2018_19OpeningBal2;
				
				//for nine iteration 2019-2020
				Double Interest2019_20OpeningBal1=Double.parseDouble(new DecimalFormat("##.##").format((91*O_balance*0.08)/366));
				Double Interest2019_20OpeningBal2=Double.parseDouble(new DecimalFormat("##.##").format((92*O_balance*0.079)/366));
				Double Interest2019_20OpeningBal3=Double.parseDouble(new DecimalFormat("##.##").format((183*O_balance*0.076)/366));
				O_balance=O_balance+Interest2019_20OpeningBal1+Interest2019_20OpeningBal2+Interest2019_20OpeningBal3;
				Total1=Total1+Interest2019_20OpeningBal1+Interest2019_20OpeningBal2+Interest2019_20OpeningBal3;

	*/
		return (double) Math.round(TotalInterest);
		//return (double) TotalInterest;
	}
	
	/*//public ResultObject emprejectByTo(Map<String, Object> inputMap) {
		public ResultObject calTierIIInterest(Map inputMap) throws Exception{
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		try {
			String status = "N";
			String dcpsEmpId = StringUtility.getParameter("dcpsEmpId", request);
			String dcpsEmp_Id[] = dcpsEmpId.split("~");
			
			if (dcpsEmp_Id != null && dcpsEmp_Id.length > 0) {

				for (Integer lInt = 0; lInt < dcpsEmp_Id.length; lInt++) {
					empTierIIInterest(dcpsEmp_Id[lInt]);
                    status = "Y";
				}

				String lSBStatus = getResponseUpdatedTO(status).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}*/
		
	/*	public Double empTierIIInterest( String dcpsEmpid) throws Exception{
	        Double Amount=0.00;		
	        Double openingBalance=0D;
			Double closingBalance=0D;
			Double Total=0D;
			Double Interest=0D;
			Double TotalInterest=0D;
			Double yeadDays=365D;
			Double leapYeadDays=366D;
			TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
			
			//List lstInstalments=lObjSearchEmployeeDAO.getEmpInstalments(dcpsEmpid);
			
			List lstLoopingForTierII=lObjSearchEmployeeDAO.getLoopingFOrIntereseCalculations();
			
			for (int i=0;i<lstLoopingForTierII.size();i++)
			{
				Object[] tupleSub = (Object[]) lstLoopingForTierII.get(i);
				
				if(i==0)
				{
				Total=openingBalance+Inst1;
				 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,Inst1, yeadDays);
				}
					
				if(i==1)
				{
					Total=openingBalance+inst2;
					 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,inst2,yeadDays);
				}	
				if(i==2)
				{
					Total=openingBalance+inst3;
					 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,inst3,yeadDays);
				}
					
				if(i==3){
					Total=openingBalance+inst4;
					 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,inst4,yeadDays);
				}
					
				if(i==4)
				{
					Total=openingBalance+inst5;
					 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,inst5,yeadDays);
				}
					
				if(i>4)
				{
					 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,0D,yeadDays);
					 Total=openingBalance;	 
				}
				
				closingBalance=Interest+Total;
				TotalInterest=Interest+TotalInterest;
				openingBalance=closingBalance;
			}
			return (double) Math.round(TotalInterest);
		}*/
		
	
/*	private Double CalculateInterest(String financialYear,Double openingBalance,Double installmentAmt,Double days) {
		// TODO Auto-generated method stub
		Double finslInt=0D;
		
		List lstRateOfInterest;
		try {
			TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
			lstRateOfInterest = lObjSearchEmployeeDAO.getInterestCalculation(financialYear);
			for (Iterator iterator = lstRateOfInterest.iterator(); iterator.hasNext();) {
 				Object[] object = (Object[]) iterator.next();
				Double interest=Double.parseDouble(object[0].toString());
				Double interestDays=Double.parseDouble(object[1].toString());
				if(installmentAmt!=0)
				{
					
					if(financialYear.equalsIgnoreCase("24")) 
					{
						Double interestDays1=Double.parseDouble(object[1].toString());
						if(interestDays1==244)
							interestDays1=182D;
						
					System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->"+interestDays1+"interest-->"+interest+"installmentAmt-->"+installmentAmt+"/days-->"+days);
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((interestDays1*interest*installmentAmt)/days));
					}
					else
					{
						if(installmentAmt!=0){
						System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->304"+"interest-->"+interest+"installmentAmt-->"+installmentAmt+"/days-->"+days);
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((304*interest*installmentAmt)/days));
						}
					}
					
				}
				
				if(openingBalance!=0){
				if(financialYear.equals("34")){
					Date date = Calendar.getInstance().getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String endDate = sdf.format(date);
					Date fromDate = sdf.parse("01/04/2021" + " " + "11:00:01");
			        Date toDate = sdf.parse(endDate + " " + "22:15:10");
			        long diff = toDate.getTime() - fromDate.getTime();
			        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			        
			        System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->"+diffDays+"interest-->"+interest+"installmentAmt-->"+openingBalance+"/days-->"+days);
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((diffDays*interest*openingBalance)/days));
					
				}else{
					System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->"+interestDays+"interest-->"+interest+"installmentAmt-->"+openingBalance+"/days-->"+days);
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((interestDays*interest*openingBalance)/days));
				}
				}
				
				
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		return finslInt;
		return (double) Math.round(finslInt);
	}*/

	////$t 7Dec2021
	private Double CalculateInterest(String financialYear,Double openingBalance,Double installmentAmt,Double days, String Type) {
		// TODO Auto-generated method stub
		Double finslInt=0D;
		
		List lstRateOfInterest;
		try {
			TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
			lstRateOfInterest = lObjSearchEmployeeDAO.getInterestCalculation(financialYear);
			for (Iterator iterator = lstRateOfInterest.iterator(); iterator.hasNext();) {
 				Object[] object = (Object[]) iterator.next();
 				//System.out.println("financialYear-->"+lstRateOfInterest.length);
 				if(lstRateOfInterest.size()==1){
				Double interest=Double.parseDouble(object[0].toString());
				Double interestDays=Double.parseDouble(object[1].toString());
				//if(installmentAmt!=0)
				if(financialYear.equals("22")||financialYear.equals("23")||financialYear.equals("24")||financialYear.equals("25")||financialYear.equals("26"))
				{
					
					if(financialYear.equalsIgnoreCase("24")) 
					{
						Double interestDays1=Double.parseDouble(object[1].toString());
						if(interestDays1==244)
							interestDays1=182D;
						
					System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->"+interestDays1+"interest-->"+interest+"installmentAmt-->"+installmentAmt+"/days-->"+days);
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((interestDays1*interest*installmentAmt)/days));
					}else{
						//if(installmentAmt!=0 && openingBalance==0.0){
						if((financialYear.equals("22")||financialYear.equals("23")||financialYear.equals("24")||financialYear.equals("25")||financialYear.equals("26")) && openingBalance==0.0){
						System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->304"+"interest-->"+interest+"installmentAmt-->"+installmentAmt+"/days-->"+days);
						//finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((304*interest*installmentAmt)/days));
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((10*0.08*installmentAmt)/12));
						}else{
							if(financialYear.equals("22")){
							finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((2*0.08*openingBalance)/12));
							}else if(financialYear.equals("23")){
							finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((2*0.08*openingBalance)/12));
							}else if(financialYear.equals("25")){
							finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((2*0.088*openingBalance)/12));
							}else if(financialYear.equals("26")){
							finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((2*0.087*openingBalance)/12));
							}
						}
					}
					
				}
				
				if(openingBalance!=0){
				if(Type.equals("Regular")){
				if(financialYear.equals("34")){
					/*Date date = Calendar.getInstance().getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String endDate = sdf.format(date);
					Date fromDate = sdf.parse("01/04/2021" + " " + "11:00:01");
			        Date toDate = sdf.parse(endDate + " " + "22:15:10");
			        long diff = toDate.getTime() - fromDate.getTime();
			        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			        System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->"+diffDays+"interest-->"+interest+"installmentAmt-->"+openingBalance+"/days-->"+days);
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((diffDays*interest*openingBalance)/days));*/

					//$t logic till mar-2022*****
					//$t formula change for other years day wise*****
					HashMap<String,String> map=new HashMap();
					map.put("12","12");//dec
					map.put("01","13");//jan
					map.put("02","14");//feb
					map.put("03","15");//for march also 14 because till feb2022 we have to calculate interest
					map.put("04","15");
					map.put("05","15");
					map.put("06","15");
					map.put("07","15");
					map.put("08","15");
					map.put("09","15");
					map.put("10","15");
					map.put("11","15");
					map.put("12","15");
					
					Date date = Calendar.getInstance().getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String endDate = sdf.format(date);
					String [] val=endDate.split("/");
					/*System.out.print("-->"+val[1]);
					System.out.print("-->"+map.get("12"));
					System.out.print("-->"+map.get(val[1]));*/
					int months=Integer.parseInt(map.get(val[1]));
					
					//////$t 2Mar2022 
					/////map.put("04","15");
					/////after april all goes to the else
					if(Integer.valueOf(val[0])<15 )
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-3))/12));//apr-nov-8
						else
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-3))/12));//apr-dec-9 
				}else{
					if(interestDays==366)
				    interestDays=365d;
					
					System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->"+interestDays+"interest-->"+interest+"installmentAmt-->"+openingBalance+"/days-->"+days);
					//finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((interestDays*interest*(installmentAmt+openingBalance))/days));
					if(financialYear.equals("23")){
					 finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((10*0.08*(openingBalance+installmentAmt))/12));
					}else if(financialYear.equals("25")){
					 finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((10*0.088*(openingBalance+installmentAmt))/12));
					}else if(financialYear.equals("26")){
					 finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((10*0.087*(openingBalance+installmentAmt))/12));
					}else if(financialYear.equals("27")){
				    finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((12*0.087*(openingBalance+installmentAmt))/12));
				   }else if(financialYear.equals("28")){
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((12*0.087*(openingBalance+installmentAmt))/12));
				   }else if(financialYear.equals("33")){
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((12*0.071*(openingBalance+installmentAmt))/12));
				   }
				 }//else
				}else{
					if(financialYear.equals("35")){
						/*Date date = Calendar.getInstance().getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						String endDate = sdf.format(date);
						Date fromDate = sdf.parse("01/04/2021" + " " + "11:00:01");
				        Date toDate = sdf.parse(endDate + " " + "22:15:10");
				        long diff = toDate.getTime() - fromDate.getTime();
				        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
				        System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->"+diffDays+"interest-->"+interest+"installmentAmt-->"+openingBalance+"/days-->"+days);
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((diffDays*interest*openingBalance)/days));*/

						//$t logic till mar-2022*****
						//$t formula change for other years day wise*****
						HashMap<String,String> map=new HashMap();
						map.put("06","8");
						map.put("07","8");
						map.put("08","8");
						map.put("09","8");
						map.put("10","8");
						map.put("11","8");
						map.put("12","8");
						
						Date date = Calendar.getInstance().getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						String endDate = sdf.format(date);
						String [] val=endDate.split("/");
						/*System.out.print("-->"+val[1]);
						System.out.print("-->"+map.get("12"));
						System.out.print("-->"+map.get(val[1]));*/
						int months=Integer.parseInt(map.get(val[1]));
						
						//////$t 2Mar2022 
						/////map.put("04","15");
						/////after april all goes to the else
						if(Integer.valueOf(val[0])<15 )
							finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-4))/12));//apr-july 4
							else
							finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-4))/12));//apr-july 4
					}else{
						if(interestDays==366)
					    interestDays=365d;
						
						System.out.println("financialYear-->"+financialYear+"finslInt-->"+finslInt+"interestDays1-->"+interestDays+"interest-->"+interest+"installmentAmt-->"+openingBalance+"/days-->"+days);
						//finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((interestDays*interest*(installmentAmt+openingBalance))/days));
						if(financialYear.equals("23")){
						 finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((10*0.08*(openingBalance+installmentAmt))/12));
						}else if(financialYear.equals("25")){
						 finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((10*0.088*(openingBalance+installmentAmt))/12));
						}else if(financialYear.equals("26")){
						 finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((10*0.087*(openingBalance+installmentAmt))/12));
						}else if(financialYear.equals("27")){
					    finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((12*0.087*(openingBalance+installmentAmt))/12));
					   }else if(financialYear.equals("28")){
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((12*0.087*(openingBalance+installmentAmt))/12));
					   }else if(financialYear.equals("33")){
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((12*0.071*(openingBalance+installmentAmt))/12));
					   }else if(financialYear.equals("34")){
							finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((12*0.071*(openingBalance+installmentAmt))/12));
					   }
					 }//else
				   }
				}
			}else{
				if(financialYear.equals("24")){
					/*finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((61*0.08*openingBalance)/days));
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((182*0.08*(openingBalance+installmentAmt))/days));
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((122*0.086*(openingBalance+installmentAmt))/days));*/
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((2*0.08*openingBalance)/12));
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((6*0.08*(openingBalance+installmentAmt))/12));
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((4*0.086*(openingBalance+installmentAmt))/12));
					break;
					}else if(financialYear.equals("29")){
					/*finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((183*0.081*openingBalance)/days));
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((182*0.08*(openingBalance))/days));*/
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((6*0.081*openingBalance)/12));
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((6*0.08*(openingBalance))/12));
					break;
				  }else if(financialYear.equals("30")){
					/*finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((91*0.079*openingBalance)/days));
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((184*0.078*(openingBalance))/days));
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((90*0.076*(openingBalance))/days));*/
					  finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((3*0.079*openingBalance)/12));
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((6*0.078*(openingBalance))/12));
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((3*0.076*(openingBalance))/12));
					break;
				  }else if(financialYear.equals("31")){
					/*finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((183*0.076*openingBalance)/days));
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((182*0.08*(openingBalance))/days));*/
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((6*0.076*openingBalance)/12));
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((6*0.08*(openingBalance))/12));
					break;
				  }else if(financialYear.equals("32")){
					/*finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((91*0.08*openingBalance)/days));
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((274*0.079*(openingBalance))/days));*/
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((3*0.08*openingBalance)/12));
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((9*0.079*(openingBalance))/12));
					break;
				   }
				
			 }//else
			}//for
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		return finslInt;
		//return (double) Math.round(finslInt);
		return (double) finslInt;
	}
	
	
	//for View bill deleteTierIIBill
	public ResultObject getEmpViewBill(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List tierIIBillList=null;
		List lstDdocodeList=null;
		List lstEmpForFiveInstApprove2=new ArrayList();
		List lstEmpForFiveInstApprove3=new ArrayList();
		String strDDOCode=null;
		String strRoleId=null;
		int DepSize = 100;
		try
		{	
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
			strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			strRoleId=lObjSearchEmployeeDAO.getRoleId(gLngPostId);
			String locationCoe=gStrLocationCode;
			//for year
			 NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl( null, this.serv.getSessionFactory());
			  DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,this.serv.getSessionFactory());
		      List lLstYears = lObjNsdlDAO.getFinyear();
		      List lLstMonths = lObjDcpsCommonDAO.getMonths();
		      List lstDDOcode = lObjDcpsCommonDAO.getAllDDO(gStrLocationCode); 
		      List lstTreasurycode = lObjDcpsCommonDAO.getAllTreasury(gStrLocationCode,strRoleId);
		      String month = StringUtility.getParameter("cmbMonth", request);
			  String year = StringUtility.getParameter("cmbYear", request);
			  String searchSeva = StringUtility.getParameter("searchSeva", request).trim();////$t9Feb22
			  String searchEmp = StringUtility.getParameter("searchEmp", request).trim();
			  String searchDdo = StringUtility.getParameter("searchDdo", request).trim();
        	  String isLoad="N";
			  gLogger.info("##################DepSize"+DepSize); 
			  if(StringUtility.getParameter("isLoad", request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("isLoad", request)))
			  {
				isLoad=StringUtility.getParameter("isLoad", request);
			  }
			  
			String currmonth = null;
			String curryear = null;
			if(month.length()==0){
			Calendar cal = Calendar.getInstance();
			currmonth = (new Integer((cal.get(Calendar.MONTH) + 1))).toString();
			curryear = (new Integer(cal.get(Calendar.YEAR))).toString();
			}
//			Long currentyear=null;
//			Long currentmonth=null;
//			if(currmonth.equals("1")){
//				currentmonth= 12L;
//				currentyear=Long.parseLong(curryear)-1;
//
//			}
//			else{
				/*currentmonth= Long.parseLong(currmonth)-1;
				currentyear=Long.parseLong(curryear);*/////$t

//			}			
			
			int w=1;
			if(month.length()>=w)
			{
				inputMap.put("selMonth", month);
				inputMap.put("selYear", year);
				tierIIBillList=lObjSearchEmployeeDAO.getTierIIBillList(strDDOCode,month,year,searchSeva,searchEmp);
	        	DepSize=tierIIBillList.size();
				inputMap.put("DDOCode", strDDOCode);
			}
			else
			{
				inputMap.put("selMonth", currmonth);
				inputMap.put("selYear", curryear);
				tierIIBillList=lObjSearchEmployeeDAO.getTierIIBillList(strDDOCode,"","",searchSeva,searchEmp);
	        	DepSize=tierIIBillList.size();
				inputMap.put("DDOCode", strDDOCode);

			}
			
			inputMap.put("strRoleId", strRoleId);
			inputMap.put("size", DepSize);
			inputMap.put("tierIIBillList", tierIIBillList);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstMonths", lLstMonths);
			
			inputMap.put("lstDDOcode", lstDDOcode);
			inputMap.put("lstTreasurycode", lstTreasurycode);
			inputMap.put("gStrLocationCode", gStrLocationCode);
			
			resObj.setResultValue(inputMap);
			resObj.setViewName("TierIIViewBill");
		}
		catch (Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	
	
	
	public ResultObject ViewSrkaGrantApprove(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List tierIIBillList=null;
		List lstDdocodeList=null;
		List lstEmpForFiveInstApprove2=new ArrayList();
		List lstEmpForFiveInstApprove3=new ArrayList();
		String strDDOCode=null;
		String strRoleId=null;
		int DepSize = 100;
		try
		{	
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
		      String locID = StringUtility.getParameter("locID", request);
		      String searchSeva = StringUtility.getParameter("searchSeva", request).trim();////$t22Feb22
			  String searchEmp = StringUtility.getParameter("searchEmp", request).trim();
			  String searchDdo = StringUtility.getParameter("searchDDO", request).trim();
			  String IsDeputation = StringUtility.getParameter("deputation", request);
			  tierIIBillList=lObjSearchEmployeeDAO.getTierIIBillList(locID,searchSeva,searchEmp,searchDdo,IsDeputation,"");
			
			inputMap.put("size", tierIIBillList.size());
			inputMap.put("tierIIBillList", tierIIBillList);
			resObj.setResultValue(inputMap);
			if(IsDeputation.equals("Y"))
			resObj.setViewName("TierIIViewBillSRKADept");
			else
			resObj.setViewName("TierIIViewBillSRKA");
		}
		catch (Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	
	//get namuna f DAN
	public ResultObject getNamunaF(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List tierIIBillList=null;
		List lstDdocodeList=null;
		List lstEmpForFiveInstApprove2=new ArrayList();
		List lstEmpForFiveInstApprove3=new ArrayList();
		String strDDOCode=null;
		String strRoleId=null;
		int DepSize = 100;
		try
		{	
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
			strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			strRoleId=lObjSearchEmployeeDAO.getRoleId(gLngPostId);
			String locationCoe=gStrLocationCode;
			//for year
			 NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl( null, this.serv.getSessionFactory());
			  DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,this.serv.getSessionFactory());
		      List lLstYears = lObjNsdlDAO.getFinyear();
		      List lLstMonths = lObjDcpsCommonDAO.getMonths();
		      List lstDDOcode = lObjDcpsCommonDAO.getAllDDO(gStrLocationCode); 
		      List lstTreasurycode = lObjDcpsCommonDAO.getAllTreasury(gStrLocationCode,strRoleId);		      
		      String month = StringUtility.getParameter("cmbMonth", request);
			  String year = StringUtility.getParameter("cmbYear", request);
			  String login = StringUtility.getParameter("login", request);
			  String cmbDDOCode = StringUtility.getParameter("DddoCode", request);
			  String searchSeva = StringUtility.getParameter("searchSeva", request).trim();////$t9Feb22
			  String searchEmp = StringUtility.getParameter("searchEmp", request).trim();
			
        	String isLoad="N";
			gLogger.info("##################DepSize"+DepSize); 
			if(StringUtility.getParameter("isLoad", request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("isLoad", request)))
			{
				isLoad=StringUtility.getParameter("isLoad", request);
			}
			gLogger.info("##################month.length()-->"+month.length());
			
			String currmonth = null;
			String curryear = null;
			if(month.length()==0){
			Calendar cal = Calendar.getInstance();
			currmonth = (new Integer((cal.get(Calendar.MONTH) + 1))).toString();
			curryear = (new Integer(cal.get(Calendar.YEAR))).toString();
			}
//			Long currentyear=null;
//			Long currentmonth=null;
//			if(currmonth.equals("1")){
//				currentmonth= 12L;
//				currentyear=Long.parseLong(curryear)-1;
//
//			}
//			else{
//				currentmonth= Long.parseLong(currmonth)-1;
//				currentyear=Long.parseLong(curryear);
//			}
            
			int w=1;
			if(month.length()>=w)
			{
				inputMap.put("selMonth", month);
				inputMap.put("selYear", year);
				tierIIBillList=lObjSearchEmployeeDAO.getTierIIOrderF(strDDOCode,month,year,login,searchSeva,searchEmp,gStrLocationCode);////$t showP
	        	DepSize=tierIIBillList.size();
				inputMap.put("DDOCode", strDDOCode);

			}
			else if(cmbDDOCode.length()!=0 || ((searchSeva.length()>0||searchEmp.length()>0)&& strRoleId.equals("700003")))
			{
				inputMap.put("selMonth", currmonth);
				inputMap.put("selYear", curryear);
				tierIIBillList=lObjSearchEmployeeDAO.getTierIIOrderF(cmbDDOCode,currmonth,curryear,login,searchSeva,searchEmp,gStrLocationCode);////$t showP
	        	DepSize=tierIIBillList.size();
				inputMap.put("DDOCode", cmbDDOCode);
			}
			else
			{	
				inputMap.put("selMonth", currmonth);
				inputMap.put("selYear", curryear);
				tierIIBillList=lObjSearchEmployeeDAO.getTierIIOrderF(strDDOCode,"","",login,searchSeva,searchEmp,gStrLocationCode);////$t showP
	        	DepSize=tierIIBillList.size();
				inputMap.put("DDOCode", strDDOCode);

			}
			
			List Dashbordlist=new ArrayList();
			List DashbordlistDeatils=null;

			for (int J=0;J<tierIIBillList.size();J++)
			{
				DashbordlistDeatils=new ArrayList();
				Object[] tupleSub1 = (Object[]) tierIIBillList.get(J);
				
				DashbordlistDeatils.add(tupleSub1[0]);
				DashbordlistDeatils.add(tupleSub1[1]);
				DashbordlistDeatils.add(tupleSub1[2]);
				DashbordlistDeatils.add(tupleSub1[3]);
				DashbordlistDeatils.add(tupleSub1[4]);
				if(login.equals("DDO") || (cmbDDOCode !=null)||!cmbDDOCode.equals(""))/////$t showP
				DashbordlistDeatils.add(tupleSub1[5]);/////$t showP
				else 
				DashbordlistDeatils.add(tupleSub1[7]);/////$t showP
				DashbordlistDeatils.add(tupleSub1[6]);  // Total EMployee
				String Approved=lObjSearchEmployeeDAO.getTierIIApprovedOrderCount(tupleSub1[4].toString(),tupleSub1[3].toString(),tupleSub1[2].toString(),tupleSub1[0].toString(),"regular");
				DashbordlistDeatils.add(Approved);  // aproved cases
				String Pending=lObjSearchEmployeeDAO.getTierIIPendingOrderCount(tupleSub1[4].toString(),tupleSub1[3].toString(),tupleSub1[2].toString(),tupleSub1[0].toString(),"regular");
				DashbordlistDeatils.add(Pending);  // Pending case
				String Reamining=lObjSearchEmployeeDAO.getTierIIRemaingOrderCount(tupleSub1[4].toString(),tupleSub1[3].toString(),tupleSub1[2].toString(),tupleSub1[0].toString(),"regular");
				DashbordlistDeatils.add(Reamining);  // Remaining Cases

				Dashbordlist.add(DashbordlistDeatils);
			}
			
			
			
			inputMap.put("strRoleId", strRoleId);
			inputMap.put("size", DepSize);
			inputMap.put("tierIIBillList", Dashbordlist);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstMonths", lLstMonths);
			
			inputMap.put("lstDDOcode", lstDDOcode);
			inputMap.put("lstTreasurycode", lstTreasurycode);
			inputMap.put("gStrLocationCode", gStrLocationCode);
			resObj.setResultValue(inputMap);
			resObj.setViewName("TierIIOrderHeader");
		}
		catch (Exception e)
		{
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}
		return resObj;
	}
	
	//to delete bill
	public ResultObject deleteTierIIBill(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);

		try {
			String status = "N";
			String DDOCODE = StringUtility.getParameter("DDOCODE", request);
			String BillNo = StringUtility.getParameter("BillNo", request);
			String flag = StringUtility.getParameter("flag", request);
			String IsDeputation = StringUtility.getParameter("deputation", request);
		    
			TierIISixPcArrearDao.deleteTierIIBill(DDOCODE,BillNo,flag,IsDeputation);
			status = "Y";
			
			String lSBStatus ="D"; 
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	
	
	
	public ResultObject ApproveGrantSrkaTierIIBill(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);

		try {
			String status = "N";
			String DDOCODE = StringUtility.getParameter("DDOCODE", request);
			String BillNo = StringUtility.getParameter("BillNo", request);
			String IsDeputation = StringUtility.getParameter("deputation", request);		
		    TierIISixPcArrearDao.ApproveGrantSrkaTierIIBill(DDOCODE,BillNo,IsDeputation);
			status = "Y";
			
			String lSBStatus ="D"; 
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	
	public ResultObject ForwardTierIIBillToSRKA(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		try {
			String status = "N";
			String DDOCODE = StringUtility.getParameter("DDOCODE", request);
			String BillNo = StringUtility.getParameter("BillNo", request);
			String IsDeputation = StringUtility.getParameter("deputation", request);		
			TierIISixPcArrearDao.ForwardToSrkaTierIIBill(DDOCODE,BillNo,IsDeputation);
			status = "Y";
			
			String lSBStatus ="D"; 
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	
	public ResultObject GenearteIIBill(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);

		try {
			String status = "N";
			String DDOCODE = StringUtility.getParameter("DDOCODE", request);
			String BillNo = StringUtility.getParameter("BillNo", request);
			String flag = StringUtility.getParameter("flag", request);
			String IsDeputation = StringUtility.getParameter("deputation", request);
		
			TierIISixPcArrearDao.deleteTierIIBill(DDOCODE,BillNo,"BILL",IsDeputation);
			status = "Y";

		    String lSBStatus ="D"; 
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	

	
	
	
	//forward Beams Code
	 public ResultObject forwardBillDataToBEAMSForNPSTierII(Map objectArgs) {
		 gLogger.info("inside forwardBillDataToBEAMSForNPS");
		 HttpServletRequest request = (HttpServletRequest) objectArgs.get("requestObj");
			ServiceLocator serv = (ServiceLocator) objectArgs.get("serviceLocator");
			ResultObject resultObject = new ResultObject(0);
			TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
			List lstBill=new ArrayList();
			long paybillId=0l;
			Date billCreationDate = null;
			
			Object obj[] = null;
			long lLongFinYearIdPK=0l;
			long lLongCurrFinYear=0l;
			long lLongNextFinyear=0l;
			gDtCurDate = SessionHelper.getCurDate();
			
			
			//String SFTPHOST = "115.112.249.52";//ip for local/staging testing http://115.112.239.72:8080/BeamsWS1/services/AuthorizationService?wsdl
			String SFTPHOST= "10.34.82.225";
			int SFTPPORT = 8888;
			String SFTPUSER = "tcsadmin";
			String SFTPPASS = "Tcsadmin@123";
			String SFTPWORKINGDIR = "/home/sevarth";
			
	
			/*String SFTPHOST = "100.70.201.169";
			int SFTPPORT = 22;
			String SFTPUSER = "mahait";
			String SFTPPASS = "Mahait@99";
			String SFTPWORKINGDIR = "/upload/DCPS";*/

			Session session = null;

			Channel channel = null;

			ChannelSftp channelSftp = null;
			try {
			Map loginMap = (Map) objectArgs.get("baseLoginMap");
			long lLongLoggedInLocation = Long.valueOf(
					loginMap.get("locationId").toString()).longValue();
			long userId = StringUtility.convertToLong(
					loginMap.get("userId").toString()).longValue();
			long postId = StringUtility.convertToLong(
					loginMap.get("primaryPostId").toString()).longValue();
			long lLongGrossAmt = 0L;
			
			TierIISixPcArrearDaoImpl lObjNsdlDAO = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
			FinancialYearDAOImpl financialYearDAOImpl = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
			SgvcFinYearMst sgvcFinYearMst = new SgvcFinYearMst();
			HashMap lMapBillDetailsMap = new HashMap();
			String fileNumber = (!("".equals(StringUtility.getParameter("BillNo", request)))) ? StringUtility.getParameter("BillNo", request) : "";
			String DDOCODE = (!("".equals(StringUtility.getParameter("DDOCODE", request)))) ? StringUtility.getParameter("DDOCODE", request) : "";
			String IsDeputation = StringUtility.getParameter("deputation", request);

			Long lIntMonth = 0L;
			Long lIntYear = 0L;
			
			lstBill=lObjSearchEmployeeDAO.gteBillDetailsForBEAMS(fileNumber,DDOCODE,IsDeputation);
			//for iterator
			Iterator it =lstBill.iterator();
			Object[] tuple = null;
			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				 lIntMonth=Long.parseLong(tuple[0].toString());
				 lIntYear=Long.parseLong(tuple[1].toString());
				 paybillId=Long.parseLong(tuple[2].toString());
				 billCreationDate=(Date) tuple[3];
				 lLongGrossAmt=Long.parseLong(tuple[4].toString());
			}
			
			String strMonth = null;
			if(lIntMonth < 10)
				strMonth = "0"+lIntMonth;
			else strMonth = lIntMonth+"";
			
//			int benCount = lObjNsdlDAO.getCount(lIntMonth, lIntYear,paybillId);
			lLongFinYearIdPK = financialYearDAOImpl.getFinYearIdByCurDate();

			sgvcFinYearMst = (SgvcFinYearMst) financialYearDAOImpl.read(Long.valueOf(lLongFinYearIdPK));
			if (sgvcFinYearMst != null) {
				lLongCurrFinYear = Long
				.valueOf(sgvcFinYearMst.getFinYearCode()).longValue();
				lLongNextFinyear = lLongCurrFinYear + -2351041733208309759L;
			}
			BigDecimal grossAmt = new BigDecimal(lLongGrossAmt);
			Calendar cal = Calendar.getInstance();
			
			gLogger.info("lLongLoggedInLocation:"+ lLongLoggedInLocation);
			gLogger.info("lLongLoggedInLocation:"+ userId+"postid:"+postId); 

//			String DDOCode="";
			/*String paymentMode="";
			List DDOList = lObjNsdlDAO.getTreasuryDdoCode(lLongLoggedInLocation);
			if(DDOList!=null && DDOList.size()>0 && DDOList.get(0)!=null)
			{
				Object[] obj1=(Object[]) DDOList.get(0);
				if(obj1[0]!=null && obj1[1]!=null)
				{
					DDOCODE=obj1[0].toString();
					paymentMode=obj1[1].toString();
				}
			}*/
			if(DDOCODE.equals("1111222222")){
				DDOCODE="9101005555";
			}
			
			cal.setTime(gDtCurDate);
			int month = cal.get(Calendar.MONTH);
			gLogger.info("finyear1 month:"+ month);
			int year = cal.get(Calendar.YEAR);
			gLogger.info("finyear1 year:"+year);
			gLogger.info("gLngUserId in nsdl service:"+gLngUserId+"gLngUserId in nsdl service"+gLngPostId);
			String finYear = getFinYear((month + 1), year);
			gLogger.info("finYear 123:"+ String.valueOf(finYear));
			String finYearArray[] = finYear.split(",");
			gLogger.info("finyear1:"+ String.valueOf(finYearArray[0]));
			gLogger.info("finyear2:"+ String.valueOf(finYearArray[1]));
			Map BifurcatedPartyNameMap = null;
			lMapBillDetailsMap.put("PaybillId", String.valueOf(paybillId));
			gLogger.info("paybillId:"+ String.valueOf(paybillId));
			lMapBillDetailsMap.put("PayMonth", strMonth);
			gLogger.info("PayMonth:"+ strMonth);
			lMapBillDetailsMap.put("PayYear", lIntYear);
			gLogger.info("PayYear:"+ lIntYear);
			lMapBillDetailsMap.put("BeneficiaryCount", "1");
			lMapBillDetailsMap.put("GrossAmount", String.valueOf(lLongGrossAmt));
			gLogger.info("GrossAmount:"+ String.valueOf(lLongGrossAmt));
			lMapBillDetailsMap.put("FinYear1", String.valueOf(finYearArray[0]));
			lMapBillDetailsMap.put("FinYear2", String.valueOf(finYearArray[1]));
			lMapBillDetailsMap.put("FormId", "MTR45A");
			//lMapBillDetailsMap.put("PaymentMode", paymentMode);
			lMapBillDetailsMap.put("PaymentMode", "CMP");
			//gLogger.info("paymentMode:"+ paymentMode);
			lMapBillDetailsMap.put("TotalDeduction", "0");
			lMapBillDetailsMap.put("SchemeCode", "83420088");
			lMapBillDetailsMap.put("DetailHead", "50");
			lMapBillDetailsMap.put("DDOCode", DDOCODE);
			gLogger.info("DDOCode:"+ DDOCODE);
			lMapBillDetailsMap.put("BulkFlag", "N");
			lMapBillDetailsMap.put("PayeeType", "D");
			lMapBillDetailsMap.put("BillCreationDate", billCreationDate);
			gLogger.info("billCreationDate:"+ billCreationDate);
			lMapBillDetailsMap.put("PayeeCount", "1");
			////lMapBillDetailsMap.put("BillType", 29);
			lMapBillDetailsMap.put("BillType", 26);/////$t29Mar2022
			lMapBillDetailsMap.put("BillPortalName", "MAHAVETAN");
			XStream xStream = new XStream(new DomDriver("UTF-8"));
			xStream.alias("collection", Map.class);
			xStream.registerConverter(new MapConverter());
			this.gLogger.error("finalXML for forwardBillDataToBEAMSForNPS :: "+ xStream.toXML(lMapBillDetailsMap));
			
			this.gLogger.error("preparing the host information for sftp.");
			JSch jsch = new JSch();
			this.gLogger.error("before session:");
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			this.gLogger.error("After session:"+session);
			session.setPassword(SFTPPASS);

			java.util.Properties config = new java.util.Properties();

			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);
			this.gLogger.error("before Host connected");
			session.connect();

			this.gLogger.error("after Host connected.");

			channel = session.openChannel("sftp");

			channel.connect();

			this.gLogger.error("sftp channel opened and connected.");

			channelSftp = (ChannelSftp) channel;

			this.gLogger.error("FILE NAME4444444444 ::::::::::::");
			//added to check folder exist or not
			/*SftpATTRS attrs=null;
			 attrs = channelSftp.stat(SFTPWORKINGDIR);
				this.logger.error("attrs ::::::::::::"+attrs);
			 if (attrs != null) {
				    System.out.println("Directory exists IsDir="+attrs.isDir());
				} else {
				    System.out.println("Creating dir "+SFTPWORKINGDIR);
				    channelSftp.mkdir(SFTPWORKINGDIR);
				}*/
			String[] folders = SFTPWORKINGDIR.split( "/" );
			for ( String folder : folders ) {
			    if ( folder.length() > 0 ) {
			        try {
			        	this.gLogger.error("FILE folder:"+folder);
			        	channelSftp.cd( folder );
			        }
			        catch ( SftpException e ) {
			        	this.gLogger.error("FILE folder:"+folder);
			        	channelSftp.mkdir( folder );
			        	channelSftp.cd( folder );
			        }
			    }
			}
			 //ended
			channelSftp.cd(SFTPWORKINGDIR);
			
			PayrollBEAMSIntegrateWS payrollBEAMSIntegrateWSObj = new PayrollBEAMSIntegrateWS();
			HashMap resultMap =payrollBEAMSIntegrateWSObj.getBillApprvFrmBEAMSWS(lMapBillDetailsMap, "");
			this.gLogger.error("resultMap is ::: " + resultMap);
			String authNo = null;
			String statusCode = null;
			byte[] pdfData = (byte[]) null;
			long lLngTrnNPSBeamsIntegrationId = 0l;
			 
			 
			if (resultMap != null && !resultMap.isEmpty()) {
				authNo = resultMap.get("authNo") != null ? (String) resultMap.get("authNo") : null;
				statusCode = resultMap.get("statusCode") != null ? (String) resultMap.get("statusCode"): null;
				
				pdfData = resultMap.get("pdfData") != null ? (byte[]) resultMap.get("pdfData") : null;
				this.gLogger.error("authNo is ::: " + authNo);
				this.gLogger.error("statusCode is ::: " + statusCode);
				this.gLogger.error("pdfData is ::: " + pdfData);
				TrnNPSBeamsIntegration lObjTrnNPSBeamsIntegration = new TrnNPSBeamsIntegration();
				lLngTrnNPSBeamsIntegrationId = IFMSCommonServiceImpl
				.getNextSeqNum("TRN_NPS_BEAMS_INTEGRATION", objectArgs);
				lObjTrnNPSBeamsIntegration.setNpsBeamsIntegrationId(lLngTrnNPSBeamsIntegrationId);
				lObjTrnNPSBeamsIntegration.setBillNo(fileNumber);
				lObjTrnNPSBeamsIntegration.setPaybillId(paybillId);
				lObjTrnNPSBeamsIntegration.setAuthNo(authNo);
				lObjTrnNPSBeamsIntegration.setBillCreationDate(billCreationDate);
				lObjTrnNPSBeamsIntegration.setBillGrossAmt(grossAmt);
				////lObjTrnNPSBeamsIntegration.setBillType("29");/////$t29Mar2022
				lObjTrnNPSBeamsIntegration.setBillType("26");
				if (pdfData != null) {
					
					if(SFTPHOST.equals("100.70.201.169"))
					lObjTrnNPSBeamsIntegration.setAuthSlip(new SerialBlob(pdfData));
					
					this.gLogger.error("FILE NAME55555555 ::::::::::::");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						String fileName=authNo+".pdf";
						//String fileName="D:/TEST1234.pdf";
						byte[] lBytes = pdfData;
						//baos.write(lBytes);
						//baos=pdfData;
						baos.toByteArray();
						this.gLogger.error("lBytes length:"+lBytes.length);
						this.gLogger.error("pdfData length:"+pdfData.length);
						OutputStream out = new FileOutputStream(fileName);
						
						
						for(int i=0;i<2;i++){
							out.write(lBytes);
						}
						
						out.close();
					

						File f = new File(fileName.toString());

						this.gLogger.error("FILE NAME222222222 ::::::::::::" + fileName);
						this.gLogger.error("FILE NAME ::::::::::::" + f.getName());

						channelSftp.put(new FileInputStream(f), f.getName());

						this.gLogger.error("FILE NAME333333333 ::::::::::::" + fileName);
						
				}
				
				lObjTrnNPSBeamsIntegration.setBillNetAmt(grossAmt);
				lObjTrnNPSBeamsIntegration.setTotalRecoveryAmt(grossAmt);
				lObjTrnNPSBeamsIntegration.setSchemeCode("83420088");
				lObjTrnNPSBeamsIntegration.setDtlheadCode("50");
				lObjTrnNPSBeamsIntegration.setBeamsBillStatus(statusCode);
				lObjTrnNPSBeamsIntegration.setLocationCode(gStrLocationCode);
				lObjTrnNPSBeamsIntegration.setDbId(99);
				this.gLogger.error("Start status");
				if (statusCode != null && statusCode.length() > 0) {
					if ("00".equals(statusCode)) {
						lObjTrnNPSBeamsIntegration.setBillValidSatus("Y");
					} else {
						lObjTrnNPSBeamsIntegration.setBillValidSatus("N");
					}
				} else {
					lObjTrnNPSBeamsIntegration.setBillValidSatus("N");
				}

				lObjTrnNPSBeamsIntegration.setCreatedDate(gDtCurDate);
				lObjTrnNPSBeamsIntegration.setCreatedUserId(postId);
				lObjTrnNPSBeamsIntegration.setCreatedPostId(postId);
				lObjNsdlDAO.create(lObjTrnNPSBeamsIntegration);
				List finalMsg = new ArrayList();
				if ((statusCode != null) && (statusCode.length() > 0)
						&& (!("00".equals(statusCode)))) {

					finalMsg.add("Bill is rejected by BEAMS.Reason of rejection,");
					this.gLogger.error("Bill is rejected by BEAMS.Reason of rejection,");
					String[] stCode = statusCode.split("\\|");
					for (int cnt = 0; cnt < stCode.length; ++cnt) {
						String key = "Status" + stCode[cnt];
						String stMsg = String.valueOf(cnt + 1) + ") "
						+ integrationBundleConst.getString(key);
						finalMsg.add(stMsg);
						this.gLogger.error("Bill is rejected by BEAMS.Reason of rejection,"+stMsg);
					}
					objectArgs.put("finalMsg", finalMsg);
					this.gLogger.error("Bill is rejected by BEAMS.Reason of rejection,"+finalMsg);
				}
				objectArgs.put("authNo", authNo);
				objectArgs.put("statusCode", statusCode);
				String flag ="";
				if ("00".equals(statusCode)) {
					flag = "Y";
					lObjNsdlDAO.updateTierIIBillDetails(authNo,paybillId,flag);
					
				} else if ((!"00".equals(statusCode))  &&resultMap != null && !resultMap.isEmpty()) {
					flag = "N";
					this.gLogger.error("Bll rejection in db"+paybillId+flag);
					if(!"31".equals(statusCode))
					{
						lObjNsdlDAO.updateTierIIBillDetails(authNo,paybillId,flag);
					}
					
					this.gLogger.error("Bll rejection in db over");
				} else {
					String key = "Status19";
					String stMsg = integrationBundleConst.getString(key);
					finalMsg.add(stMsg);
					objectArgs.put("finalMsg", finalMsg);				
					objectArgs.put("statusCode", key);

				}
				objectArgs.put("beamsSucessMsg", "Autorization Number "+authNo+" has been Generated Successfully");
				resultObject.setResultValue(objectArgs);
                //resultObject.setViewName("NPSVALIDATE");
				resultObject.setViewName("TierIIViewBill");
				this.gLogger.error("over");
			}
			
			//BDSIntegrationDAOImpl lObjTreasuryIntegrationDAO = new BDSIntegrationDAOImpl(TrnIfmsBeamsIntegration.class, serv.getSessionFactory());
			
			}catch (Exception e) {
				gLogger.info(e.toString());
				e.printStackTrace();
				this.gLogger.error("Exception Occurrs in getData Method of DisplayOuterServiceImpl..Exception is "
						+ e.getMessage());
			}
			finally {

				channelSftp.exit();

				this.gLogger.error("sftp Channel exited.");

				channel.disconnect();

				this.gLogger.error("Channel disconnected.");

				session.disconnect();

				this.gLogger.error("Host Session disconnected.");

				}
			return resultObject;
	 }
	 
	 public ResultObject getAuthSlipForTierII(Map objectArgs) {
			ResultObject resultObject = new ResultObject(0);
			try {
				HttpServletRequest request = (HttpServletRequest) objectArgs
				.get("requestObj");
				ServiceLocator serv = (ServiceLocator) objectArgs
				.get("serviceLocator");
				HttpServletResponse response = (HttpServletResponse) objectArgs
				.get("responseObj");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());

				String authNo = (!("".equals(StringUtility.getParameter("authNo",
						request)))) ? StringUtility.getParameter("authNo", request)
								.toString() : "";
							gLogger.error("authNo is ::" + authNo);

								TrnNPSBeamsIntegration hrPayrollBeamsMpg = lObjNsdlDAO.getPayBillAuthSlipDtlsForNPS(authNo);
								byte[] lBytes = (byte[]) null;
								gLogger.error("hrPayrollBeamsMpg is ::; " + hrPayrollBeamsMpg);
								if (hrPayrollBeamsMpg != null && hrPayrollBeamsMpg.getAuthSlip()!=null) {
									Blob blob = hrPayrollBeamsMpg.getAuthSlip();
									gLogger.error("blob is ::; " + blob);
									int blobLength = (int) blob.length();
									gLogger.error("blobLength is ::; " + blobLength);
									lBytes = blob.getBytes(1, blobLength);
									gLogger.error("lBytes is ::; " + lBytes.length);
									baos.write(lBytes);

									response.setContentLength(lBytes.length);
									response.setContentType("application/pdf");
									response.addHeader("Content-Disposition",
									"inline;filename=authSlip.pdf");
									response.getOutputStream().write(lBytes);
									response.getOutputStream().flush();
									response.getOutputStream().close();

									resultObject.setResultValue(objectArgs);
									resultObject.setResultCode(0);
									resultObject.setViewName("authorizationSlip");
								}
								else
								{
                                  //For SFTP Code
									String SFTPHOST= "10.34.82.225";
									int SFTPPORT = 8888;
									String SFTPUSER = "tcsadmin";
									String SFTPPASS = "Tcsadmin@123";
									String SFTPWORKINGDIR = "home/sevarth";

									/*String SFTPHOST = "100.70.201.169";
									int SFTPPORT = 22;
									String SFTPUSER = "mahait";
									String SFTPPASS = "Mahait@99";
									String SFTPWORKINGDIR = "/upload/DCPS";*/
										Session session = null;

										Channel channel = null;

										ChannelSftp channelSftp = null;
										 byte[] data = null;
										 byte[] lBytes1=null;
										JSch jsch = new JSch();
										this.gLogger.error("Channel connected.");
										session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);

										session.setPassword(SFTPPASS);

										java.util.Properties config = new java.util.Properties();

										config.put("StrictHostKeyChecking", "no");

										session.setConfig(config);

										session.connect();

										byte[] buffer = new byte[1024];

										channel = session.openChannel("sftp");
										channelSftp = (ChannelSftp) channel;
										channel.connect();
										channelSftp.cd(SFTPWORKINGDIR);
										//String fileName = "55554913911940126_TEST.pdf"; 
										String fileName = authNo+".pdf";
										this.gLogger.error("Channel connected fileName:"+fileName);
										BufferedInputStream bis = new BufferedInputStream(channelSftp.get(fileName));	
										
										File newFile = new File(fileName);
										
										String filePath=request.getSession().getServletContext().getRealPath("/") + fileName;
							
											String home = System.getProperty("user.home");
											objectArgs.put("SFTPmsg","YES");
										//	channelSftp.get("/home/"+fileName, ""+home+"/Downloads/"+fileName); 
											//channelSftp.get("/home/"+fileName, filePath);
											//channelSftp.get("/home/"+fileName, "/tmp/");
											channelSftp.get("/home/sevarth/"+fileName, "/tmp/");
											//added one more solution on 9th feb
											 FileInputStream fis = new FileInputStream(new File("/tmp/"+fileName));

											 // Fast way to copy a bytearray from InputStream to OutputStream
											 org.apache.commons.io.IOUtils.copy(fis, response.getOutputStream());
											 response.setContentType("application/pdf");
											 response.setHeader("Content-Disposition", "attachment; filename=" + filePath);
											 response.flushBuffer();
											//ended solution
										/*	File pdfFile = new File(filePath);
											if (pdfFile.exists()) {

												if (Desktop.isDesktopSupported()) {
													Desktop.getDesktop().open(pdfFile);
												} else {
													System.out.println("Awt Desktop is not supported!");
												}
												objectArgs.put("SFTPSuccessmsg","PDF has been downloaded succesfully  through SFTP");
											} else {
												objectArgs.put("SFTPSuccessmsg","PDF doesnot exist in SFTP server");
											}*/
											
											this.gLogger.error(" above viewname");
											resultObject.setResultValue(objectArgs);
											resultObject.setResultCode(0);
											resultObject.setViewName("SFTPAuthSlip");
												//System.out.println("count1: " +  count1);
									}
								
			} catch (Exception e) {
				e.printStackTrace();
				gLogger
				.error("Exception Occurrs in getData Method of DisplayOuterServiceImpl..Exception is "
						+ e.getMessage());
			}
			return resultObject;
		}
	
	 private String getFinYear(int month, int year) {
			gLogger.info("month:"+month+"year:"+year);
			if (month >= 4 && month <= 12) {
				
				long nxtYear = year + 1;
				gLogger.info("year:"+year+"nxtYear:"+nxtYear);
				return year + "," + nxtYear;
			} else if (month >= 1 && month < 4) {
				long prevYear = year - 1;
				return prevYear + "," + year;
			}
			return "";
		}
	 
	 
	 
	 public ResultObject viewGenerateTierIIIner(Map inputMap) {

			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			String lStrTempFromDate = null;
			String lStrTempToDate = null;
			String lStrFromDate = null;
			String lStrToDate = null;
			Date lDateFromDate = null;
			Date lDateToDate = null;
			List lListTotalDdowiseEntries = null;
			Long yearId = null;
			Long monthId=null;
			Long lLongEmployeeAmt=0L;
			Long lLongEmployerAmt=0L;
			Long TotalAmt=0L;
			Long nsdl_paybill_pk=0L;
			String transactionId="";
			Long billNo=0l;
			List billDetails = null;
			String sYear = "";
			Object obj[];
			String billCreationDate = null;
			String billGeneratedMonth=null;
			String locId = SessionHelper.getLocationCode(inputMap);
			String scheme="(Non Plan)";
			try {
				setSessionInfo(inputMap);
				NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
				List lLstMonths=lObjDcpsCommonDAO.getMonths();
				String Month = StringUtility.getParameter("month", request);
				String Year = StringUtility.getParameter("year", request);
				String BillId = StringUtility.getParameter("billId", request);
				String Amount = StringUtility.getParameter("amount", request);
				String Date = StringUtility.getParameter("Date", request);
				String Noe = StringUtility.getParameter("Noe", request);
				String Inst = StringUtility.getParameter("Inst", request);
				String Interest = StringUtility.getParameter("Interest", request);
				String ddoCode= StringUtility.getParameter("ddoCode", request);
				String Deputation = StringUtility.getParameter("deputation", request);		
	     Map monthMap = new HashMap();
	     monthMap.put(1, "January");
	     monthMap.put(2, "February");
	     monthMap.put(3, "March");
	     monthMap.put(4, "April");
	     monthMap.put(5, "May");
	     monthMap.put(6, "June");
	     monthMap.put(7, "July");
	     monthMap.put(8, "August");
	     monthMap.put(9, "September");
	     monthMap.put(10, "October");
	     monthMap.put(11, "November");     
	     monthMap.put(12, "December");   
	     
	    String  stringMonth=(String) monthMap.get(Month);
		 String totalContributionAmountInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(Amount));
			TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());

			String strRoleId=lObjSearchEmployeeDAO.getRoleId(gLngPostId);
			String dsgn=lObjSearchEmployeeDAO.getDDODetails(ddoCode);
			
			/*if(!Deputation.equals("Y"))
			dsgn=lObjSearchEmployeeDAO.getDDODetails(ddoCode);
			else
			dsgn=lObjSearchEmployeeDAO.getHODDetails(gLngPostId);*/

				inputMap.put("totalContributionAmountInWords", totalContributionAmountInWords);
				inputMap.put("stringMonth", stringMonth);
				inputMap.put("sYear", sYear);
				inputMap.put("scheme", scheme);
				inputMap.put("Amount", Amount);
				inputMap.put("Noe", Noe);
				inputMap.put("Inst", Inst);
				inputMap.put("Interest", Interest);
				inputMap.put("billNo", BillId);
				inputMap.put("billDate", Date);
				inputMap.put("strRoleId", strRoleId);
				inputMap.put("ddoCode", ddoCode);
				inputMap.put("dsgn", dsgn);
				inputMap.put("IsDeputation", Deputation);
				resObj.setResultValue(inputMap);
				resObj.setViewName("mtr45innerTierII");
			} catch (Exception e) {
				e.printStackTrace();
				gLogger.error(" Error is : " + e, e);
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
			}

			return resObj;
		}
	 
	 /*
	  * FOr Print outer report of Tier II Bills
	  * 
	  */
	 public ResultObject viewGenerateTierIIOrder(Map inputMap) {

			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			String locId = SessionHelper.getLocationCode(inputMap);
			String scheme="(Non Plan)";
			try {
				setSessionInfo(inputMap);
				String Month = StringUtility.getParameter("month", request);
				String Year = StringUtility.getParameter("year", request);
				String BillId = StringUtility.getParameter("billId", request);
				String ddoCode= StringUtility.getParameter("ddoCode", request);
				String amount= StringUtility.getParameter("amount", request);
				TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
				String strRoleId=lObjSearchEmployeeDAO.getRoleId(gLngPostId);


				 String amountWord = EnglishDecimalFormat.convertWithSpace(new BigDecimal(amount));

				Date date = new Date();  
			    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
			    String strDate= formatter.format(date); 
				String orderId = StringUtility.getParameter("orderId", request);
				Map sessionKeys=inputMap;
	

					List tierIIBillList=null;
					tierIIBillList=lObjSearchEmployeeDAO.getOrderDetails(BillId,Month,Year);
                    String LocationName= lObjSearchEmployeeDAO.getLocationName(ddoCode.substring(0,2));
                    String DdoName= lObjSearchEmployeeDAO.getDDOName(ddoCode);
					
					
                    inputMap.put("locationName", LocationName);
					inputMap.put("tierIIBillList", tierIIBillList);
					inputMap.put("orderId", orderId);
					inputMap.put("billDate", strDate);
					inputMap.put("ddoName", DdoName);
					inputMap.put("amount", amount);
					inputMap.put("amountWord", amountWord);
					inputMap.put("strRoleId", strRoleId);
					inputMap.put("ddoCode", ddoCode);


					
					resObj.setResultValue(inputMap);
					resObj.setViewName("TierIIOrderReportRH");
					
					
//					resObj.setViewName("TierIIOrderReport");



			} catch (Exception e) {
				e.printStackTrace();
				gLogger.error(" Error is : " + e, e);
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
			}

			return resObj;
		}
	 
	// namun f
	 public ResultObject viewGenerateTierIINamunF(Map inputMap) {

			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			String locId = SessionHelper.getLocationCode(inputMap);
			String scheme="(Non Plan)";
			try {
				setSessionInfo(inputMap);
				String Month = StringUtility.getParameter("month", request);
				String Year = StringUtility.getParameter("year", request);
				String BillId = StringUtility.getParameter("OrderId", request);
				String ddoCode= StringUtility.getParameter("ddoCode", request);
				String amount= StringUtility.getParameter("amount", request);

				 String amountWord = EnglishDecimalFormat.convertWithSpace(new BigDecimal(amount));

				Date date = new Date();  
			    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
			    String strDate= formatter.format(date); 
				String orderId = StringUtility.getParameter("orderId", request);
				Map sessionKeys=inputMap;
	
					TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());

					List tierIIBillList=null;
					tierIIBillList=lObjSearchEmployeeDAO.getOrderDetailsF(BillId,Month,Year);
                 String LocationName= lObjSearchEmployeeDAO.getLocationName(ddoCode.substring(0,2));
                 String DdoName= lObjSearchEmployeeDAO.getDDOName(ddoCode);
					
					
                 inputMap.put("locationName", LocationName);
					inputMap.put("tierIIBillList", tierIIBillList);
					inputMap.put("orderId", orderId);
					inputMap.put("billDate", strDate);
					inputMap.put("ddoName", DdoName);
					inputMap.put("amount", amount);
					inputMap.put("amountWord", amountWord);
					inputMap.put("ddoCode", ddoCode);


					
					resObj.setResultValue(inputMap);
					resObj.setViewName("TierIIOrderReportF");



			} catch (Exception e) {
				e.printStackTrace();
				gLogger.error(" Error is : " + e, e);
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
			}

			return resObj;
		}
	 
	// namun f
		 public ResultObject viewGenerateTierIINamunG(Map inputMap) {

				ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
				String locId = SessionHelper.getLocationCode(inputMap);
				String scheme="(Non Plan)";
				try {
					setSessionInfo(inputMap);
					String Month = StringUtility.getParameter("month", request);
					String Year = StringUtility.getParameter("year", request);
					String BillId = StringUtility.getParameter("OrderId", request);
					String ddoCode= StringUtility.getParameter("ddoCode", request);
					String amount= StringUtility.getParameter("amount", request);

					 String amountWord = EnglishDecimalFormat.convertWithSpace(new BigDecimal(amount));

					Date date = new Date();  
				    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
				    String strDate= formatter.format(date); 
					String orderId = StringUtility.getParameter("orderId", request);
					Map sessionKeys=inputMap;
		
						TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());

						List tierIIBillList=null;
						tierIIBillList=lObjSearchEmployeeDAO.getOrderDetailsG(BillId,Month,Year);
	                 String LocationName= lObjSearchEmployeeDAO.getLocationName(ddoCode.substring(0,2));
	                 String DdoName= lObjSearchEmployeeDAO.getDDOName(ddoCode);
						
						
	                 inputMap.put("locationName", LocationName);
						inputMap.put("tierIIBillList", tierIIBillList);
						inputMap.put("orderId", orderId);
						inputMap.put("billDate", strDate);
						inputMap.put("ddoName", DdoName);
						inputMap.put("amount", amount);
						inputMap.put("amountWord", amountWord);
						inputMap.put("ddoCode", ddoCode);
						
						resObj.setResultValue(inputMap);
						resObj.setViewName("TierIIOrderReportG");

				} catch (Exception e) {
					e.printStackTrace();
					gLogger.error(" Error is : " + e, e);
					resObj.setResultValue(null);
					resObj.setThrowable(e);
					resObj.setResultCode(ErrorConstants.ERROR);
					resObj.setViewName("errorPage");
				}

				return resObj;
			}
		 
		 
		 public ResultObject viewGenerateTierIINamunRH(Map inputMap) {

				ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
				String locId = SessionHelper.getLocationCode(inputMap);
				String scheme="(Non Plan)";
				try {
					setSessionInfo(inputMap);
					String Month = StringUtility.getParameter("month", request);
					String Year = StringUtility.getParameter("year", request);
					String BillId = StringUtility.getParameter("OrderId", request);
					String ddoCode= StringUtility.getParameter("ddoCode", request);
					String amount= StringUtility.getParameter("amount", request);

					 String amountWord = EnglishDecimalFormat.convertWithSpace(new BigDecimal(amount));

					Date date = new Date();  
				    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
				    String strDate= formatter.format(date); 
				    String Sysdate= formatter.format(new Date()); 
					String orderId = StringUtility.getParameter("orderId", request);
					Map sessionKeys=inputMap;
		
						TierIISixPcArrearDao lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());

						List tierIIBillList=null;
						tierIIBillList=lObjSearchEmployeeDAO.getOrderDetailsG(BillId,Month,Year);
	                 String LocationName= lObjSearchEmployeeDAO.getLocationName(ddoCode.substring(0,2));
	                 String DdoName= lObjSearchEmployeeDAO.getDDOName(ddoCode);
						
						
	                 inputMap.put("locationName", LocationName);
						inputMap.put("tierIIBillList", tierIIBillList);
						inputMap.put("orderId", orderId);
						inputMap.put("billDate", strDate);
						inputMap.put("ddoName", DdoName);
						inputMap.put("amount", amount);
						inputMap.put("amountWord", amountWord);
						inputMap.put("ddoCode", ddoCode);
						inputMap.put("Sysdate", Sysdate);
						
						resObj.setResultValue(inputMap);
						resObj.setViewName("TierIIOrderReportRH");

				} catch (Exception e) {
					e.printStackTrace();
					gLogger.error(" Error is : " + e, e);
					resObj.setResultValue(null);
					resObj.setThrowable(e);
					resObj.setResultCode(ErrorConstants.ERROR);
					resObj.setViewName("errorPage");
				}

				return resObj;
			}
	 
	private StringBuilder getResponseUpdatedTO(String flag) {
		gLogger.info("Flag is  in AJAX********" + flag);
		StringBuilder lStrBldXML = new StringBuilder();
		
		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	
	public ResultObject approveTierIIVoucherBill(Map inputMap) {
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		
		try {
			setSessionInfo(inputMap);
			/*String month = StringUtility.getParameter("cmbMonth", request);			
			String year = StringUtility.getParameter("cmbYear", request);*/
			String voucherNo = StringUtility.getParameter("vouchrNo", request);
			String vouchedate = StringUtility.getParameter("vouchrDate", request);
			String authNO = StringUtility.getParameter("authNO", request);
			String BillNo = StringUtility.getParameter("BillNo", request);
			String type = StringUtility.getParameter("type", request);
			
			/*gLogger.info("year"+year);
			gLogger.info("month"+month);*/
			gLogger.info("voucherNo"+voucherNo);
			gLogger.info("vouchedate"+vouchedate);
			gLogger.info("BillNo"+BillNo);
			gLogger.info("authNO"+authNO);
			TierIISixPcArrearDaoImpl lObjNsdlDAO = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
			
			inputMap.put("vouchedate", vouchedate);
			inputMap.put("voucherNo", voucherNo);
			/*inputMap.put("month", month);
			inputMap.put("year", year);*/
			inputMap.put("BillNo", BillNo);
			inputMap.put("authNO", authNO);
			if(authNO!=null){
				lObjNsdlDAO.updateVoucherEntry(BillNo,authNO,voucherNo,vouchedate,type);	
				inputMap.put("msg", "Details Apporved Successfully");
			}

			resObj.setResultValue(inputMap);
			resObj.setViewName("approveTierIIBill");

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error(" Error is : " + e, e);
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
		}

		return resObj;
	}
	
	public ResultObject getEmpNameForAutoCompleteNPS(
			Map<String, Object> inputMap) {

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		List finalList = null;
		String lStrEmpName = null;
		String lStrSearchBy = null;
		String lStrDDOCode = null;
		String lStrSearchType = null;
		String deputation = null;

		try {
			setSessionInfo(inputMap);
			TierIISixPcArrearDaoImpl lObjTierIISixPcArrearDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			lStrEmpName = StringUtility.getParameter("searchKey", request).trim();

			lStrSearchBy = StringUtility.getParameter("searchBy", request).trim();

			lStrSearchType = StringUtility.getParameter("searchType", request);
			
			deputation = StringUtility.getParameter("deputation", request);
			
			if (lStrSearchBy.equals("searchByDDO")) {
				lStrDDOCode = lObjDcpsCommonDAO.getDdoCodeForDDO(gLngPostId);
			}else if (lStrSearchBy.equals("searchByTO")) {
				lStrDDOCode = gStrLocationCode;
			}else{
				lStrDDOCode = null;
			}

			finalList = lObjTierIISixPcArrearDAO.getEmpNameForAutoComplete(
					lStrEmpName.toUpperCase(), lStrSearchType, lStrDDOCode,lStrSearchBy,deputation);

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
	
	public ResultObject updateFiveInstAmt(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		try {
			String status = "N";
			String SevarthID = StringUtility.getParameter("SevarthID", request);
			String Reason = StringUtility.getParameter("Reason", request);
			String Total = StringUtility.getParameter("Total", request);
			String InstI = StringUtility.getParameter("InstI", request);
			String InstINo = StringUtility.getParameter("InstINo", request);
			String txtStartDateI = StringUtility.getParameter("txtStartDateI", request);
			String InstII = StringUtility.getParameter("InstII", request);
			String InstIINo = StringUtility.getParameter("InstIINo", request);
			String txtStartDateII = StringUtility.getParameter("txtStartDateII", request);
			String InstIII = StringUtility.getParameter("InstIII", request);
			String InstIIINo = StringUtility.getParameter("InstIIINo", request);
			String txtStartDateIII = StringUtility.getParameter("txtStartDateIII", request);
			String InstIV = StringUtility.getParameter("InstIV", request);
			String InstIVNo = StringUtility.getParameter("InstIVNo", request);
			String txtStartDateIV = StringUtility.getParameter("txtStartDateIV", request);
			String InstV = StringUtility.getParameter("InstV", request);
			String InstVNo = StringUtility.getParameter("InstVNo", request);
			String txtStartDateV = StringUtility.getParameter("txtStartDateV", request);
			String Deputation = StringUtility.getParameter("Deputation", request);
			String Treasury = StringUtility.getParameter("Deputation", request);
			String cmbInstDtlsI = StringUtility.getParameter("cmbInstDtlsI", request);
			String cmbInstDtlsII = StringUtility.getParameter("cmbInstDtlsII", request);
			String cmbInstDtlsIII = StringUtility.getParameter("cmbInstDtlsIII", request);
			String cmbInstDtlsIV = StringUtility.getParameter("cmbInstDtlsIV", request);
			String cmbInstDtlsV = StringUtility.getParameter("cmbInstDtlsV", request);
			String cmbVCDtlsI = StringUtility.getParameter("cmbVCDtlsI", request);
			String cmbVCDtlsII = StringUtility.getParameter("cmbVCDtlsII", request);
			String cmbVCDtlsIII = StringUtility.getParameter("cmbVCDtlsIII", request);
			String cmbVCDtlsIV = StringUtility.getParameter("cmbVCDtlsIV", request);
			String cmbVCDtlsV = StringUtility.getParameter("cmbVCDtlsV", request);
			String cmbTreasuryI = StringUtility.getParameter("cmbTreasuryI", request);
			String cmbTreasuryII = StringUtility.getParameter("cmbTreasuryII", request);
			String cmbTreasuryIII = StringUtility.getParameter("cmbTreasuryIII", request);
			String cmbTreasuryIV = StringUtility.getParameter("cmbTreasuryIV", request);
			String cmbTreasuryV = StringUtility.getParameter("cmbTreasuryV", request);
			String oldI = StringUtility.getParameter("oldI", request);
			String oldII = StringUtility.getParameter("oldII", request);
			String oldIII = StringUtility.getParameter("oldIII", request);
			String oldIV = StringUtility.getParameter("oldIV", request);
			String oldV = StringUtility.getParameter("oldV", request);
			String chanInstI="N";
			String chanInstII="N";
			String chanInstIII="N";
			String chanInstIV="N";
			String chanInstV="N";
			if(!InstI.equals(oldI) || Reason.equals("DeptNewRlt"))
				chanInstI="Y";
			
			if(!InstII.equals(oldII) || Reason.equals("DeptNewRlt"))
				chanInstII="Y";
			
			if(!InstIII.equals(oldIII)|| Reason.equals("DeptNewRlt"))
				chanInstIII="Y";
			
			if(!InstIV.equals(oldIV) || Reason.equals("DeptNewRlt"))
				chanInstIV="Y";
			
			if(!InstV.equals(oldV)|| Reason.equals("DeptNewRlt"))
				chanInstV="Y";
		
			if ((SevarthID != null && SevarthID.length() > 0)) {
				if (Deputation.equals("YNRlt")) {
				TierIISixPcArrearDao.insertFiveInstAmtTO(SevarthID,Reason,Total,InstI,InstINo,txtStartDateI,InstII,InstIINo,txtStartDateII,InstIII,InstIIINo,txtStartDateIII
							,InstIV,InstIVNo,txtStartDateIV,InstV,InstVNo,txtStartDateV,Deputation,gStrLocationCode,gStrPostId,gStrUserId,Treasury,cmbInstDtlsI,cmbInstDtlsII,cmbInstDtlsIII,cmbInstDtlsIV,cmbInstDtlsV,cmbVCDtlsI,cmbVCDtlsII,cmbVCDtlsIII,cmbVCDtlsIV,cmbVCDtlsV
							,cmbTreasuryI,cmbTreasuryII,cmbTreasuryIII,cmbTreasuryIV,cmbTreasuryV);
				}else{
				TierIISixPcArrearDao.updateFiveInstAmtTO(SevarthID,Reason,Total,InstI,InstINo,txtStartDateI,InstII,InstIINo,txtStartDateII,InstIII,InstIIINo,txtStartDateIII
							,InstIV,InstIVNo,txtStartDateIV,InstV,InstVNo,txtStartDateV,Deputation,cmbInstDtlsI,cmbInstDtlsII,cmbInstDtlsIII,cmbInstDtlsIV,cmbInstDtlsV,cmbVCDtlsI,cmbVCDtlsII,cmbVCDtlsIII,cmbVCDtlsIV,cmbVCDtlsV
							,cmbTreasuryI,cmbTreasuryII,cmbTreasuryIII,cmbTreasuryIV,cmbTreasuryV,chanInstI,chanInstII,chanInstIII,chanInstIV,chanInstV);
				}
			    status = "Y";
			}
			String lSBStatus = getResponseUpdatedTO(status).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	
	public ResultObject loadEmpDeputationTierII(Map inputMap) {////$t19Apr2022
		gLogger.info("in loadEmpDeputationTierII ################ method");

		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		try {
			setSessionInfo(inputMap);
			String Use = StringUtility.getParameter("Use", request);
			if(Use.equals("Attach")){
				
			}
			
			
			objRes.setResultValue(inputMap);
			if(Use.equals("updateInst")){ 
				inputMap.put("TierIIDeputation", "Y");
				objRes.setViewName("TierIIUpdateInst");
			}
			else{
				objRes.setViewName("TierIIAttachDeputation"); 
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	public ResultObject getEmpListForTierIIDeputation(Map inputMap) {////$t19Apr2022
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TierIISixPcArrearDaoImpl TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		List getEmpListTierIIDeputation=null;
		try {
			String searchSeva = StringUtility.getParameter("searchSeva", request).trim();
			String searchEmp = StringUtility.getParameter("searchEmp", request).trim();
			getEmpListTierIIDeputation=TierIISixPcArrearDao.getEmpListForUpdateInstDeputation(searchSeva,searchEmp,this.gStrPostId);////$t29Oct2021
			List TreasuryList = ((TierIISixPcArrearDaoImpl) TierIISixPcArrearDao).getTreasuryList();
        	
        	if(getEmpListTierIIDeputation!=null && getEmpListTierIIDeputation.size()>0){
				Object[] obj=(Object[]) getEmpListTierIIDeputation.get(0);
				gLogger.info("##################DepSize"+obj[10]);
				inputMap.put("EmpName", obj[1].toString().trim());
				inputMap.put("SevarthID", obj[2].toString().trim());
				inputMap.put("DcpsID", obj[3].toString().trim());
				inputMap.put("InstI", obj[5].toString().trim());
				inputMap.put("InstINo", obj[6].toString().trim());
				inputMap.put("InstIDate", obj[7]);
				inputMap.put("InstII", obj[9].toString().trim());
				inputMap.put("InstIINo", obj[10].toString().trim());
				inputMap.put("InstIIDate", obj[11]);
				inputMap.put("InstIII", obj[13].toString().trim());
				inputMap.put("InstIIINo", obj[14].toString().trim());
				inputMap.put("InstIIIDate", obj[15]);
				inputMap.put("InstIV", obj[17].toString().trim());
				inputMap.put("InstIVNo", obj[18].toString().trim());
				inputMap.put("InstIVDate", obj[19]);
				inputMap.put("InstV", obj[21].toString().trim());
				inputMap.put("InstVNo", obj[22].toString().trim());
				inputMap.put("InstVDate", obj[23]);
				inputMap.put("Reason", obj[24].toString().trim());
				inputMap.put("cmbInstDtlsI", obj[25].toString());
				inputMap.put("cmbInstDtlsII", obj[26].toString());
				inputMap.put("cmbInstDtlsIII", obj[27].toString());
				inputMap.put("cmbInstDtlsIV", obj[28].toString());
				inputMap.put("cmbInstDtlsV", obj[29].toString());
				inputMap.put("cmbVCDtlsI", obj[30].toString());
				inputMap.put("cmbVCDtlsII", obj[31].toString());
				inputMap.put("cmbVCDtlsIII", obj[32].toString());
				inputMap.put("cmbVCDtlsIV", obj[33].toString());
				inputMap.put("cmbVCDtlsV", obj[34].toString());
				inputMap.put("cmbTreasuryI", obj[35].toString());
				inputMap.put("cmbTreasuryII", obj[36].toString());
				inputMap.put("cmbTreasuryIII", obj[37].toString());
				inputMap.put("cmbTreasuryIV", obj[38].toString());
				inputMap.put("cmbTreasuryV", obj[39].toString());
				inputMap.put("oldReason", obj[40].toString());
				inputMap.put("TreasuryList", TreasuryList);
				inputMap.put("TierIIDeputation", "Y");
                objRes.setResultValue(inputMap);
                objRes.setViewName("TierIIUpdateInst");
        	}else{
        		getEmpListTierIIDeputation=TierIISixPcArrearDao.getEmpListForUpdateInstDeputationNoRlt(searchSeva,searchEmp,"noRlt",this.gStrPostId);////$t29Oct2021
        		
        		if(getEmpListTierIIDeputation==null || getEmpListTierIIDeputation.size()<1){
        		inputMap.put("TierIIDeputation", "Y");
                objRes.setResultValue(inputMap);
                objRes.setViewName("TierIIUpdateInst");
        		}else if(getEmpListTierIIDeputation.size()>0){
        		Object[] obj=(Object[]) getEmpListTierIIDeputation.get(0);
				inputMap.put("EmpName", obj[0].toString().trim());
				inputMap.put("SevarthID", obj[1].toString().trim());
				inputMap.put("DcpsID", obj[2].toString().trim());
				inputMap.put("InstINo", "0");
				inputMap.put("InstIINo", "0");
				inputMap.put("InstIIINo", "0");
				inputMap.put("InstIVNo", "0");
				inputMap.put("InstVNo", "0");
				inputMap.put("InstI", "0");
				inputMap.put("InstII", "0");
				inputMap.put("InstIII", "0");
				inputMap.put("InstIV", "0");
				inputMap.put("InstV", "0");
				inputMap.put("Reason", "NA");
				/*Date date = Calendar.getInstance().getTime();  
				DateFormat dateFormat = new SimpleDateFormat("DD/MM/YYYY");  
				String strDate = dateFormat.format(date);
				strDate="00/00/0000";
				System.out.println("-->"+strDate);*/
				String sDate1="00/00/0000";  
				Date date=new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);  
				System.out.println(sDate1+"\t"+date);
				date=null;
				inputMap.put("InstIDate", date);
				inputMap.put("InstIIDate", date);
				inputMap.put("InstIIIDate", date);
				inputMap.put("InstIVDate", date);
				inputMap.put("InstVDate", date);
				inputMap.put("cmbVCDtlsI", "NA");
				inputMap.put("cmbVCDtlsII", "NA");
				inputMap.put("cmbVCDtlsIII", "NA");
				inputMap.put("cmbVCDtlsIV", "NA");
				inputMap.put("cmbVCDtlsV", "NA");
				inputMap.put("cmbTreasuryI", "NA");
				inputMap.put("cmbTreasuryII", "NA");
				inputMap.put("cmbTreasuryIII", "NA");
				inputMap.put("cmbTreasuryIV", "NA");
				inputMap.put("cmbTreasuryV", "NA");
				inputMap.put("TreasuryList", TreasuryList);
				inputMap.put("TierIINoRlt", "Y");
				inputMap.put("TierIIDeputation", "Y");
	        	objRes.setResultValue(inputMap);
	        	objRes.setViewName("TierIIUpdateInst");	
        		}
        	}
		}catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	public ResultObject getEmpListForAttachTierIIDeputation(Map inputMap) {////$t19Apr2022
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject objRes = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		TierIISixPcArrearDaoImpl TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		List getEmpListTierIIDeputation;
		try {
			String searchSeva = StringUtility.getParameter("searchSeva", request).trim();
			String searchEmp = StringUtility.getParameter("searchEmp", request).trim();
			String ByPass = StringUtility.getParameter("ByPass", request).trim();
			
			  if(ByPass.equals("Y")){
			  objRes.setResultValue(inputMap);
		      objRes.setViewName("TierIIAttachDeputation"); 
			  }else{
			 getEmpListTierIIDeputation=TierIISixPcArrearDao.getEmpListForUpdateInstDeputationNoRlt(searchSeva,searchEmp,"Attach",this.gStrPostId);////$t29Oct2021
			 if(getEmpListTierIIDeputation!=null && getEmpListTierIIDeputation.size()>0){
	        		Object[] obj=(Object[]) getEmpListTierIIDeputation.get(0);
					inputMap.put("SevarthID", obj[1].toString().trim());
					inputMap.put("EmpName", obj[0].toString().trim());
					inputMap.put("DcpsID", obj[2].toString().trim());
					inputMap.put("DDOCode", obj[3].toString().trim());
					inputMap.put("DcpsEmpId", obj[4].toString().trim());
					//List lLstOffice = ((TierIISixPcArrearDaoImpl) TierIISixPcArrearDao).getTreasuryList();
					List lLstOffice = TierIISixPcArrearDao.getDummyOffices(gStrPostId);
					inputMap.put("OFFICELIST", lLstOffice);
					inputMap.put("empList", getEmpListTierIIDeputation);
			 }
					objRes.setResultValue(inputMap);
	        	    objRes.setViewName("TierIIAttachDeputation");
			  } 
		}catch (Exception e) {
			e.printStackTrace();
			objRes.setResultValue(null);
			objRes.setThrowable(e);
			objRes.setResultCode(ErrorConstants.ERROR);
			objRes.setViewName("errorPage");
		}
		return objRes;
	}
	
	public ResultObject attachTierIIEmp(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		TierIISixPcArrearDaoImpl TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		DcpsCommonDAOImpl lObjDcpsCommonDao = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		try {
			String status = "N";
			String SevarthID = StringUtility.getParameter("SevarthID", request);
			String txtAttachDate = StringUtility.getParameter("txtAttachDate", request);
			String cmbOfficeName = StringUtility.getParameter("cmbOfficeName", request);
			String cmbOfficeCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			String DcpsEmpId = StringUtility.getParameter("DcpsEmpId", request);
		
			if ((SevarthID != null && SevarthID.length() > 0)) {
			TierIISixPcArrearDao.insertAttachTierIIEmp(txtAttachDate,cmbOfficeCode,DcpsEmpId,gStrPostId,gStrUserId,cmbOfficeName);
			status = "Y";
			}
			String lSBStatus = getResponseUpdatedTO(status).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	
	public ResultObject chkEmpAlreadyAttach(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);
		TierIISixPcArrearDaoImpl TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		try {
			String status = "N";
			String searchSeva = StringUtility.getParameter("searchSeva", request);
			String searchEmp = StringUtility.getParameter("searchEmp", request);
			String chk=null;
			if ((searchSeva.length() > 0 || searchEmp.length() > 0)) {
			chk=TierIISixPcArrearDao.checkEmpExistInAttach(searchSeva,searchEmp);////$t29Oct2021
			if(chk.length()>2)
			    status = chk;
			else if(chk.length()==1)
				status = chk;
			}
			String lSBStatus = getResponseUpdatedTO(status).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	
	public ResultObject getEmpListForFiveInstApproveDept(Map inputMap) throws Exception
	{
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstEmpForFiveInstApprove=null;
		List lstDdocodeList=null;
		List lstEmpForFiveInstApprove2=new ArrayList();
		List lstEmpForFiveInstApprove3=new ArrayList();
		String strDDOCode=null;
		String strRoleId=null;
		int DepSize = 100;
		try
		{	
			setSessionInfo(inputMap);
			DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
			TierIISixPcArrearDaoImpl lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
			strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
			strRoleId=lObjSearchEmployeeDAO.getRoleId(gLngPostId);
			
			String DddoCode = StringUtility.getParameter("dcpsEmpId", request);  
			String DddoCode1 = StringUtility.getParameter("DddoCode", request); 
			String genbill = StringUtility.getParameter("genbill", request); 
			String interestFlag = StringUtility.getParameter("Interest", request).trim();
			String searchSeva = StringUtility.getParameter("searchSeva", request).trim();
			String searchEmp = StringUtility.getParameter("searchEmp", request).trim();
			String searchDDO = StringUtility.getParameter("searchDDO", request).trim();
			String UpdateInst = StringUtility.getParameter("UpdateInst", request).trim();
			String cmbOfficeCode = StringUtility.getParameter("cmbOfficeCode", request).trim();
			String noData = StringUtility.getParameter("noData", request).trim();
			String IsDeputation = StringUtility.getParameter("deputation", request);
			/*String interestApproved="NO";*/
			
			if(UpdateInst.equals("YES")){////$t162022 updateinst new Screen
				lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApproveUpdateInst(gStrLocationCode,searchSeva,searchEmp);////$t29Oct2021
            	DepSize=lstEmpForFiveInstApprove.size();
            	
            	if(lstEmpForFiveInstApprove!=null && lstEmpForFiveInstApprove.size()>0){
            	  //for(int j=0;j<lstEmpForFiveInstApprove.size();j++){
					Object[] obj=(Object[]) lstEmpForFiveInstApprove.get(0);////obj[9].toString().trim()
					
					gLogger.info("##################DepSize"+obj[10]); 
					
					inputMap.put("EmpName", obj[1].toString().trim());
					inputMap.put("SevarthID", obj[2].toString().trim());
					inputMap.put("DcpsID", obj[3].toString().trim());
					inputMap.put("InstI", obj[5].toString().trim());
					inputMap.put("InstINo", obj[6].toString().trim());
					inputMap.put("InstIDate", obj[7]);
					inputMap.put("InstII", obj[9].toString().trim());
					inputMap.put("InstIINo", obj[10].toString().trim());
					inputMap.put("InstIIDate", obj[11]);
					inputMap.put("InstIII", obj[13].toString().trim());
					inputMap.put("InstIIINo", obj[14].toString().trim());
					inputMap.put("InstIIIDate", obj[15]);
					inputMap.put("InstIV", obj[17].toString().trim());
					inputMap.put("InstIVNo", obj[18].toString().trim());
					inputMap.put("InstIVDate", obj[19]);
					inputMap.put("InstV", obj[21].toString().trim());
					inputMap.put("InstVNo", obj[22].toString().trim());
					inputMap.put("InstVDate", obj[23]);
					inputMap.put("Reason", obj[24].toString().trim());
				  //}
			    }
            	
				gLogger.info("##################DepSize"+DepSize); 
				inputMap.put("strRoleId", strRoleId);
				inputMap.put("DepSize", DepSize);
				
				resObj.setResultValue(inputMap);
				resObj.setViewName("TierIIUpdateInst");	
			}else{
			if(strRoleId.equalsIgnoreCase("700002") && IsDeputation.equalsIgnoreCase("Y")){
				if(genbill.equalsIgnoreCase("bill")&& strDDOCode.length()>2){
					List lLstOffice = lObjSearchEmployeeDAO.getTreasuryList();
					inputMap.put("OFFICELIST", lLstOffice);
	            	lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApproveDeputation(strDDOCode,gStrLocationCode,"700008",interestFlag,searchSeva,searchEmp,gStrPostId,noData);////$t29Oct2021
	            	DepSize=lstEmpForFiveInstApprove.size();
					gLogger.info("##################DepSize"+DepSize); 
					inputMap.put("strRoleId", strRoleId);
					inputMap.put("DepSize", DepSize);
					inputMap.put("empList", lstEmpForFiveInstApprove);
					inputMap.put("DDOCode", strDDOCode); 
					inputMap.put("strBillId", "11");
					inputMap.put("subTrCode", cmbOfficeCode);
					resObj.setResultValue(inputMap);
					resObj.setViewName("TierIIFiveInstApproveDept");
	            }else if(genbill.equalsIgnoreCase("bill")&& cmbOfficeCode.length()<2){
	            	List lLstOffice = lObjSearchEmployeeDAO.getTreasuryList();
					inputMap.put("OFFICELIST", lLstOffice);
					inputMap.put("strRoleId", strRoleId);
					inputMap.put("DepSize", DepSize);
					inputMap.put("empList", lstEmpForFiveInstApprove);
					inputMap.put("DDOCode", strDDOCode); 
					inputMap.put("strBillId", "11");
					resObj.setResultValue(inputMap);
					resObj.setViewName("TierIIFiveInstApproveDept");
	            }else{
				lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApproveDeputation(strDDOCode,gStrLocationCode,"100018",interestFlag,searchSeva,searchEmp,gStrPostId,noData);////$t29Oct2021

				//for iterator
				Iterator it =lstEmpForFiveInstApprove.iterator();
				Object[] tuple = null;
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					String A=tuple[0].toString();
					String B=tuple[1].toString();
					String C=tuple[2].toString();
					String D=tuple[3].toString();
					String E=tuple[4].toString();
					String F=tuple[5].toString();
					String G=tuple[6].toString();
					String H=tuple[7].toString();
					String I=tuple[8].toString();
					String J=tuple[9].toString();
					String K=tuple[10].toString();
					String L=tuple[11].toString();
					String M=tuple[12].toString();
					String N=tuple[13].toString();
					String O=tuple[14].toString();
					System.out.println("13-->"+O);
					System.out.println("13-->"+N);
					//String P=tuple[15].toString();
					//String Q=tuple[16].toString();
					
					/*Double Int = 0.0d;
					if(interestApproved.equals("NO"))*//////$t29Oct2021
					
					Double Int;
					if(StringUtility.getParameter("Interest", request).trim().equals("Y")){////$t 29Oct2021
					Int=InterestCalculation(Double.parseDouble(C),Double.parseDouble(E),Double.parseDouble(G),Double.parseDouble(I),Double.parseDouble(K),"Deputation");
					}else{
						//String R=tuple[17].toString();
						//System.out.print("-->"+Double.parseDouble(R));
						if(tuple[15]==null)
						Int = 0d;
						else
						Int =Double.parseDouble(tuple[15].toString());
					}
					lstEmpForFiveInstApprove2=new ArrayList();
					lstEmpForFiveInstApprove2.add(A);
					lstEmpForFiveInstApprove2.add(B);
					lstEmpForFiveInstApprove2.add(C);
					lstEmpForFiveInstApprove2.add(D);
					lstEmpForFiveInstApprove2.add(E);
					lstEmpForFiveInstApprove2.add(F);
					lstEmpForFiveInstApprove2.add(G);
					lstEmpForFiveInstApprove2.add(H);
					lstEmpForFiveInstApprove2.add(I);
					lstEmpForFiveInstApprove2.add(J);
					lstEmpForFiveInstApprove2.add(K);
					lstEmpForFiveInstApprove2.add(L);
					lstEmpForFiveInstApprove2.add(M);
					lstEmpForFiveInstApprove2.add(N);
					lstEmpForFiveInstApprove2.add(O);
					//lstEmpForFiveInstApprove2.add(P);
					//lstEmpForFiveInstApprove2.add(Q);
					lstEmpForFiveInstApprove2.add(Int);
					lstEmpForFiveInstApprove2.add(tuple[16].toString());
					lstEmpForFiveInstApprove2.add(tuple[17].toString());
					lstEmpForFiveInstApprove2.add(tuple[18].toString());
					lstEmpForFiveInstApprove2.add(tuple[19].toString());
					lstEmpForFiveInstApprove2.add(tuple[20].toString());
					lstEmpForFiveInstApprove2.add(tuple[21].toString());
					lstEmpForFiveInstApprove2.add(tuple[22].toString());
					lstEmpForFiveInstApprove3.add(lstEmpForFiveInstApprove2);
					System.out.println("size-->"+lstEmpForFiveInstApprove3.size());
				}
				//end
				
				
				List lLstOffice = lObjSearchEmployeeDAO.getTreasuryList();
				inputMap.put("OFFICELIST", lLstOffice);
				
				
				DepSize=lstEmpForFiveInstApprove.size();
				gLogger.info("##################DepSize"+DepSize); 
				inputMap.put("strRoleId", strRoleId);
				inputMap.put("DepSize", DepSize);
				inputMap.put("empList", lstEmpForFiveInstApprove3);
				inputMap.put("DDOCode", strDDOCode);
				inputMap.put("interestFlag", interestFlag);
				resObj.setResultValue(inputMap);
				
				/*gLogger.info("##################Interest"+StringUtility.getParameter("Interest", request).trim());
				if(StringUtility.getParameter("Interest", request).trim().equals("Y"))////$t 29Oct2021
				resObj.setViewName("TierIIInterestCalculation");
				else
				resObj.setViewName("EmpListForFiveInstApprove");*/
				
				resObj.setViewName("TierIIFiveInstApproveDept");
				}
			}else{
             if(DddoCode.equalsIgnoreCase("HOD"))/////$t11Feb22
              {
            	lstEmpForFiveInstApprove=lObjSearchEmployeeDAO.getEmpListForFiveInstApproveDeputation(DddoCode1,genbill,strRoleId,interestFlag,searchSeva,searchEmp,gStrPostId,noData);////$t29Oct2021
            	DepSize=lstEmpForFiveInstApprove.size();
				gLogger.info("##################DepSize"+DepSize); 
				inputMap.put("strRoleId", strRoleId);
				inputMap.put("DepSize", DepSize);
				inputMap.put("empList", lstEmpForFiveInstApprove);
				inputMap.put("DDOCode", DddoCode1);
				inputMap.put("orderId", genbill);
				
				resObj.setResultValue(inputMap);
				resObj.setViewName("TierIIFiveInstApproveDept");	
               }else{
				lstDdocodeList=lObjSearchEmployeeDAO.getDdoSearchListDept(gStrLocationCode,searchDDO);
				DepSize=lstDdocodeList.size();
				inputMap.put("DDOCodeList", lstDdocodeList);
				inputMap.put("DDOCode", DddoCode); 
				inputMap.put("DepSize", DepSize);
				resObj.setResultValue(inputMap);
				resObj.setViewName("TierIIDdoScreenDept");
            }
		  }
		}
	  }catch (Exception e){
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
	  }
	  return resObj;
	}
	
	public ResultObject empListOfFiveInstUpdateTODept(Map<String, Object> inputMap) {
		ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
		HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

		TierIISixPcArrearDao TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
		setSessionInfo(inputMap);
		try {
			String status = "N";
			String dcpsEmpId = StringUtility.getParameter("dcpsEmpId", request);
			String Interest = StringUtility.getParameter("Interest", request);
			String GrandTotalAmount = StringUtility.getParameter("GrandTotalAmount", request);
			String TotalAMount = StringUtility.getParameter("TotalAMount", request);
			String dcpsEmp_Id[] = dcpsEmpId.split(",");
			String strDDOCode=TierIISixPcArrearDao.getDdoCodeForDDO(dcpsEmp_Id[0]);////$t19Apr2022
			String IsDeputation = StringUtility.getParameter("Deputation", request);////$t19Apr2022
			
			String ArrayDcpsID = StringUtility.getParameter("ArrayDcpsID", request);
			String ArrayTotalAmount = StringUtility.getParameter("ArrayTotalAmount", request);
			String ArrayInterest = StringUtility.getParameter("ArrayInterest", request);
			String Interest_split[] = ArrayInterest.split("~");
			
			if (dcpsEmp_Id != null && dcpsEmp_Id.length > 0) {
				String BillNo=TierIISixPcArrearDao.generateOrderFDept(strDDOCode,Interest,TotalAMount,GrandTotalAmount,dcpsEmp_Id.length,dcpsEmpId,ArrayDcpsID,ArrayTotalAmount,ArrayInterest);

				for (Integer lInt = 0; lInt < dcpsEmp_Id.length; lInt++) {
					TierIISixPcArrearDao.empListOfFiveInstUpdateTO(dcpsEmp_Id[lInt],Interest_split[lInt],BillNo,IsDeputation);
					status = BillNo;
				}
				String lSBStatus = getResponseUpdatedTO(BillNo).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");
				resObj.setResultValue(inputMap);
			}
		} catch (Exception e) {
			gLogger.error("Error is;" + e, e);
			resObj.setResultValue(null);
		}
		return resObj;
	}
	
	//get namunaF
		public ResultObject getNamunaFDept(Map inputMap) throws Exception
		{
			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			List tierIIBillList=null;
			List lstDdocodeList=null;
			List lstEmpForFiveInstApprove2=new ArrayList();
			List lstEmpForFiveInstApprove3=new ArrayList();
			//String strDDOCode=null;
			String strRoleId=null;
			int DepSize = 100;
			try
			{	
				setSessionInfo(inputMap);
				DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
				TierIISixPcArrearDaoImpl lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
				//strDDOCode=lObjSearchEmployeeDAO.getDdoCodeForDDO(gLngPostId);
				strRoleId=lObjSearchEmployeeDAO.getRoleId(gLngPostId);
				String locationCoe=gStrLocationCode;
				//for year
				 NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl( null, this.serv.getSessionFactory());
				  DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,this.serv.getSessionFactory());
			      List lLstYears = lObjNsdlDAO.getFinyear();
			      List lLstMonths = lObjDcpsCommonDAO.getMonths();
			      //List lstDDOcode = lObjDcpsCommonDAO.getAllDDO(gStrLocationCode); 
			      List lstTreasurycode = lObjDcpsCommonDAO.getAllTreasury(gStrLocationCode,strRoleId);		      
			      String month = StringUtility.getParameter("cmbMonth", request);
				  String year = StringUtility.getParameter("cmbYear", request);
				  String login = StringUtility.getParameter("login", request);
				  String cmbDDOCode = StringUtility.getParameter("DddoCode", request);
				  String searchSeva = StringUtility.getParameter("searchSeva", request).trim();////$t9Feb22
				  String searchEmp = StringUtility.getParameter("searchEmp", request).trim();
				
	        	String isLoad="N";
				gLogger.info("##################DepSize"+DepSize); 
				if(StringUtility.getParameter("isLoad", request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("isLoad", request)))
				{
					isLoad=StringUtility.getParameter("isLoad", request);
				}
				gLogger.info("##################month.length()-->"+month.length());
				
				String currmonth = null;
				String curryear = null;
				if(month.length()==0){
				Calendar cal = Calendar.getInstance();
				currmonth = (new Integer((cal.get(Calendar.MONTH) + 1))).toString();
				curryear = (new Integer(cal.get(Calendar.YEAR))).toString();
				}	            
				int w=1;
				if(month.length()>=w)
				{
					inputMap.put("selMonth", month);
					inputMap.put("selYear", year);
					tierIIBillList=lObjSearchEmployeeDAO.getTierIIOrderFDept(cmbDDOCode,gStrPostId,month,year,login,searchSeva,searchEmp,gStrLocationCode);////$t showP
		        	DepSize=tierIIBillList.size();
					//inputMap.put("DDOCode", strDDOCode);

				}
				//else if(cmbDDOCode.length()!=0 || ((searchSeva.length()>0||searchEmp.length()>0)&& strRoleId.equals("700003")))
				else if(cmbDDOCode.length()!=0 || ((searchSeva.length()>0||searchEmp.length()>0)&& strRoleId.equals("700003")))
				{////$tDept
					inputMap.put("selMonth", currmonth);
					inputMap.put("selYear", curryear);
					tierIIBillList=lObjSearchEmployeeDAO.getTierIIOrderFDept(cmbDDOCode,gStrPostId,currmonth,curryear,login,searchSeva,searchEmp,gStrLocationCode);////$t showP
		        	DepSize=tierIIBillList.size();
					//inputMap.put("DDOCode", cmbDDOCode);
				}
				else
				{	
					inputMap.put("selMonth", currmonth);
					inputMap.put("selYear", curryear);
					tierIIBillList=lObjSearchEmployeeDAO.getTierIIOrderFDept(cmbDDOCode,gStrPostId,"","",login,searchSeva,searchEmp,gStrLocationCode);////$t showP
		        	DepSize=tierIIBillList.size();
					//inputMap.put("DDOCode", strDDOCode);

				}
				
				List Dashbordlist=new ArrayList();
				List DashbordlistDeatils=null;

				for (int J=0;J<tierIIBillList.size();J++)
				{
					DashbordlistDeatils=new ArrayList();
					Object[] tupleSub1 = (Object[]) tierIIBillList.get(J);
					
					DashbordlistDeatils.add(tupleSub1[0]);
					DashbordlistDeatils.add(tupleSub1[1]);
					DashbordlistDeatils.add(tupleSub1[2]);
					DashbordlistDeatils.add(tupleSub1[3]);
					DashbordlistDeatils.add(tupleSub1[4]);
					//if(login.equals("DDO") || (cmbDDOCode !=null)||!cmbDDOCode.equals(""))/////$t showP
					if(login.equals("HOD"))/////$t showP
					DashbordlistDeatils.add(tupleSub1[5]);/////$t showP
					else 
					DashbordlistDeatils.add(tupleSub1[7]);/////$t showP
					DashbordlistDeatils.add(tupleSub1[6]);  // Total EMployee
					String Approved=lObjSearchEmployeeDAO.getTierIIApprovedOrderCount(tupleSub1[4].toString(),tupleSub1[3].toString(),tupleSub1[2].toString(),tupleSub1[0].toString(),"deputation");
					DashbordlistDeatils.add(Approved);  // aproved cases
					String Pending=lObjSearchEmployeeDAO.getTierIIPendingOrderCount(tupleSub1[4].toString(),tupleSub1[3].toString(),tupleSub1[2].toString(),tupleSub1[0].toString(),"deputation");
					DashbordlistDeatils.add(Pending);  // Pending case
					String Reamining=lObjSearchEmployeeDAO.getTierIIRemaingOrderCount(tupleSub1[4].toString(),tupleSub1[3].toString(),tupleSub1[2].toString(),tupleSub1[0].toString(),"deputation");
					DashbordlistDeatils.add(Reamining);  // Remaining Cases

					Dashbordlist.add(DashbordlistDeatils);
				}
				
				inputMap.put("strRoleId", strRoleId);
				inputMap.put("size", DepSize);
				inputMap.put("tierIIBillList", Dashbordlist);
				inputMap.put("lLstYears", lLstYears);
				inputMap.put("lLstMonths", lLstMonths);
				
				//inputMap.put("lstDDOcode", lstDDOcode);
				inputMap.put("lstTreasurycode", lstTreasurycode);
				inputMap.put("gStrLocationCode", gStrLocationCode);
				resObj.setResultValue(inputMap);
				resObj.setViewName("TierIIOrderHeaderDept");
			}
			catch (Exception e)
			{
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
			}
			return resObj;
		}
		
		//for View bill deleteTierIIBill
		public ResultObject getEmpViewBillDept(Map inputMap) throws Exception
		{
			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
			List tierIIBillList=null;
			List lstDdocodeList=null;
			List lstEmpForFiveInstApprove2=new ArrayList();
			List lstEmpForFiveInstApprove3=new ArrayList();
			String strDDOCode=null;
			String strRoleId=null;
			int DepSize = 100;
			try
			{	
				setSessionInfo(inputMap);
				DcpsCommonDAO lObjDcpsCommonDao = new DcpsCommonDAOImpl(null,serv.getSessionFactory());
				TierIISixPcArrearDaoImpl lObjSearchEmployeeDAO = new TierIISixPcArrearDaoImpl(MstEmp.class, serv.getSessionFactory());
				strDDOCode=lObjDcpsCommonDao.getDdoCodeForDDO(gLngPostId);
				strRoleId=lObjSearchEmployeeDAO.getRoleId(gLngPostId);
				String locationCoe=gStrLocationCode;
				//for year
				 NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl( null, this.serv.getSessionFactory());
				  DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,this.serv.getSessionFactory());
			      List lLstYears = lObjNsdlDAO.getFinyear();
			      List lLstMonths = lObjDcpsCommonDAO.getMonths();
			      List lstDDOcode = lObjDcpsCommonDAO.getAllDDO(gStrLocationCode); 
			      List lstTreasurycode = lObjDcpsCommonDAO.getAllTreasury(gStrLocationCode,strRoleId);
			      String month = StringUtility.getParameter("cmbMonth", request);
				  String year = StringUtility.getParameter("cmbYear", request);
				  String searchSeva = StringUtility.getParameter("searchSeva", request).trim();////$t9Feb22
				  String searchEmp = StringUtility.getParameter("searchEmp", request).trim();
				  String searchDdo = StringUtility.getParameter("searchDdo", request).trim();
				  
	        	  String isLoad="N";
				  gLogger.info("##################DepSize"+DepSize); 
				  if(StringUtility.getParameter("isLoad", request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("isLoad", request)))
				  {
					isLoad=StringUtility.getParameter("isLoad", request);
				  }
				String currmonth = null;
				String curryear = null;
				if(month.length()==0){
				Calendar cal = Calendar.getInstance();
				currmonth = (new Integer((cal.get(Calendar.MONTH) + 1))).toString();
				curryear = (new Integer(cal.get(Calendar.YEAR))).toString();
				}			
				int w=1;
				if(month.length()>=w)
				{
					inputMap.put("selMonth", month);
					inputMap.put("selYear", year);
					tierIIBillList=lObjSearchEmployeeDAO.getTierIIBillListDept(strDDOCode,month,year,searchSeva,searchEmp);
		        	DepSize=tierIIBillList.size();
					inputMap.put("DDOCode", strDDOCode);
				}
				else
				{
					inputMap.put("selMonth", currmonth);
					inputMap.put("selYear", curryear);
					tierIIBillList=lObjSearchEmployeeDAO.getTierIIBillListDept(strDDOCode,"",gStrPostId,searchSeva,searchEmp);////here year replace as a post id
		        	DepSize=tierIIBillList.size();
					inputMap.put("DDOCode", strDDOCode);

				}
				
				inputMap.put("strRoleId", strRoleId);
				inputMap.put("size", DepSize);
				inputMap.put("tierIIBillList", tierIIBillList);
				inputMap.put("lLstYears", lLstYears);
				inputMap.put("lLstMonths", lLstMonths);
				
				inputMap.put("lstDDOcode", lstDDOcode);
				inputMap.put("lstTreasurycode", lstTreasurycode);
				inputMap.put("gStrLocationCode", gStrLocationCode);
				
				resObj.setResultValue(inputMap);
				resObj.setViewName("TierIIViewBillDept");
			}
			catch (Exception e)
			{
				resObj.setResultValue(null);
				resObj.setThrowable(e);
				resObj.setResultCode(ErrorConstants.ERROR);
				resObj.setViewName("errorPage");
			}
			return resObj;
		}
		
		public ResultObject saveHODProfile(Map inputMap) {
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			HttpServletRequest request = (HttpServletRequest) inputMap.get("requestObj");
			ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS);

			TierIISixPcArrearDaoImpl TierIISixPcArrearDao = new TierIISixPcArrearDaoImpl(null, serv.getSessionFactory());
			setSessionInfo(inputMap);
			try {
				String status = "N";
				String hodName = StringUtility.getParameter("hodName", request);
				String hodDsgn = StringUtility.getParameter("hodDsgn", request);
				String hodOffice = StringUtility.getParameter("hodOffice", request);
				String hodDDO = StringUtility.getParameter("hodDDO", request);
				
				if (hodName != null && hodName.length() > 0) {
					TierIISixPcArrearDao.saveHODDetails(hodName,hodDsgn,hodOffice,hodDDO,this.gStrUserId,this.gStrPostId,this.gStrLocationCode);					
					status = "Y";
					}
					String lSBStatus = getResponseUpdatedTO(status).toString();
					String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",lSBStatus.toString()).toString();
					inputMap.put("ajaxKey", lStrResult);
					resObj.setViewName("ajaxData");
					resObj.setResultValue(inputMap);
				
			} catch (Exception e) {
				gLogger.error("Error is;" + e, e);
				resObj.setResultValue(null);
			}
			return resObj;
		}
		
	
}//end class
