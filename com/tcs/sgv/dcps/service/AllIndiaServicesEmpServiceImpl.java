package com.tcs.sgv.dcps.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import java.util.TimeZone;

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
import com.tcs.sgv.dcps.dao.AllIndiaServicesDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.eis.valueobject.HrPayGpfBalanceDtls;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;


public class AllIndiaServicesEmpServiceImpl extends ServiceImpl 
{
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

	public ResultObject getAllIndiaServicesEmps(Map inputMap)throws Exception{
		logger.info("Inside Get getAllIndiaServicesEmps");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		/*Long lLngLocId = null;
    	OrgDdoMst lObjDdoMst = null;
    	String lStrDdocode = null;
    	String lFlag = null;*/
		List lstAlIndiaSerEmp = null;
		List lstAisType = null;
		String aisType = "-1";
		String extn=null;
		try{
			setSessionInfo(inputMap);
			AllIndiaServicesDAOImpl lObjAlIndSer = new AllIndiaServicesDAOImpl(null,serv.getSessionFactory());    		
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");
			if(request.getParameter("aisType") != null && !request.getParameter("aisType").equals(""))
				aisType = request.getParameter("aisType").toString();

			//lFlag = StringUtility.getParameter("empStat",request);
			Object[] ais = new Object[2];
			ais[0] = "700214";
			ais[1] = "I.A.S.";

			Object[] ais1 = new Object[2];
			ais1[0] = "700215";
			ais1[1] = "I.F.S."; 

			Object[] ais2 = new Object[2];
			ais2[0] = "700216";
			ais2[1] = "I.P.S.";
			lstAisType  = new ArrayList();
			lstAisType.add(ais);
			lstAisType.add(ais1);
			lstAisType.add(ais2);

			inputMap.put("lstAisType",lstAisType);
			inputMap.put("aisType",aisType);
			lstAlIndiaSerEmp = lObjAlIndSer.getAllIndiaServicesEmpList(aisType);

			logger.info("Inside Get getAllIndiaServicesEmps"+aisType);
			inputMap.put("lstAlIndiaSerEmp",lstAlIndiaSerEmp);

			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object
			resultObject.setViewName("allIndiaServicesEmp");//set view name

		}catch(Exception e){
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in loadEmpDtlsDdoWise "+ e);
		}
		return resultObject;
	}

	public ResultObject convertGPFToDCPS(Map inputMap){	
		logger.info("Inside Get convertGPFToDCPS");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		try{
			setSessionInfo(inputMap);
			NewRegDdoDAOImpl newRegDDODaoImplObj = new NewRegDdoDAOImpl(MstEmp.class,serv.getSessionFactory());
			NewRegTreasuryService treasuryServiceobj = new NewRegTreasuryServiceImpl();

			String empDcpsId = request.getParameter("empDcpsId");
			String pranNo = request.getParameter("pranNo");
			String dcpsID = request.getParameter("dcpsID");
			String gisapplicable = request.getParameter("GISApplicable");
			String generatedDcpsId = dcpsID;
			String msg = "Failed";

			//DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date current_date = new Date();
			//Date curDate = dateFormat.format(date);

			//System.out.println("Current date of system " + date);

			if(empDcpsId != null){

				MstEmp empObj = (MstEmp)newRegDDODaoImplObj.read(Long.parseLong(empDcpsId));
				if((empObj.getDcpsId() != null && empObj.getDcpsId().length() != 20) ||
						empObj.getDcpsId() == null){
					generatedDcpsId = treasuryServiceobj.generateDCPSId(inputMap, Long.parseLong(empDcpsId));
				}
				empObj.setDcpsId(generatedDcpsId);
				empObj.setDcpsOrGpf('Y');
				empObj.setRegStatus(1L);
				empObj.setAllindiaservoldemp('Y');
				empObj.setUpdatedDate(current_date);

				if(gisapplicable != null && !gisapplicable.isEmpty())
				{
					if(gisapplicable.equals("700214"))
					{
						empObj.setAcDcpsMaintainedBy("700240");
					}

					if(gisapplicable.equals("700216"))
					{
						empObj.setAcDcpsMaintainedBy("700241");
					}
					if(gisapplicable.equals("700215"))
					{
						empObj.setAcDcpsMaintainedBy("700242");
					}
				}
				///lookup id for A/c maintained by All India Services.
				empObj.setPranNo(pranNo);    
				newRegDDODaoImplObj.update(empObj);
				logger.info("generatedDcpsId "+generatedDcpsId);

				newRegDDODaoImplObj = new NewRegDdoDAOImpl(OrgEmpMst.class,serv.getSessionFactory());

				if(empObj.getOrgEmpMstId() != null){
					OrgEmpMst orgEmpMstObj = (OrgEmpMst)newRegDDODaoImplObj.read(empObj.getOrgEmpMstId());

					newRegDDODaoImplObj = new NewRegDdoDAOImpl(HrPayGpfBalanceDtls.class,serv.getSessionFactory());

					HrPayGpfBalanceDtls hrPayGpfBalanceDtlsObj = (HrPayGpfBalanceDtls)newRegDDODaoImplObj.read(orgEmpMstObj.getOrgUserMst().getUserId());
					hrPayGpfBalanceDtlsObj.setGpfAccNo(generatedDcpsId);
					hrPayGpfBalanceDtlsObj.setPfSeries("DCPS");
					newRegDDODaoImplObj.update(hrPayGpfBalanceDtlsObj);

					logger.info("orgEmpMstObj.getOrgUserMst().getUserId() "+orgEmpMstObj.getOrgUserMst().getUserId());
					msg = "Success";

					orgEmpMstObj = null;
					hrPayGpfBalanceDtlsObj = null;   
				}
				empObj = null;
				newRegDDODaoImplObj = null;				 			
			}
			StringBuffer returnMsg = new StringBuffer();
			returnMsg.append("<XMLDOC>");
			returnMsg.append("<msg>");
			returnMsg.append(msg);
			returnMsg.append("</msg>");

			returnMsg.append("<DCPSID>");
			returnMsg.append("<![CDATA[");
			returnMsg.append(generatedDcpsId.trim());
			returnMsg.append("]]>");
			returnMsg.append("</DCPSID>");

			returnMsg.append("</XMLDOC>");
			logger.info("msg "+msg);
			logger.info("DCPS_ID "+generatedDcpsId);
			Map result = new  HashMap();
			String stateNameIdStr = new AjaxXmlBuilder().addItem("ajax_key", returnMsg.toString()).toString();
			logger.info("stateNameIdStr "+stateNameIdStr);
			result.put("ajaxKey", stateNameIdStr);
			resultObject.setResultValue(result);
			resultObject.setViewName("ajaxData");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return resultObject;
	}
	//added by ashish

	public ResultObject getBillNo(Map<String, Object> inputMap) {

		/*	ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		List lstbillNo = null;

		try {

			setSessionInfo(inputMap);
			AllIndiaServicesDAOImpl lObjAlIndSer = new AllIndiaServicesDAOImpl(null,serv.getSessionFactory());    	



			String aisType = StringUtility.getParameter("aisType",request).trim();

			logger.info("aisType is *****"+aisType);

			if (aisType!=null)
			{
				lstbillNo = lObjAlIndSer.selectBillNo(aisType);
			}

			inputMap.put("lstbillNo", lstbillNo);

			resObj.setResultValue(inputMap);
			resObj.setViewName("")


		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			logger.error(" Error in getcheckpranno " + e, e);
		}

		return resObj;*/


		List lstbillNo=null;
		logger.info("Inside Get getAISEmpsContri--------------------");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);

		try{
			setSessionInfo(inputMap);
			AllIndiaServicesDAOImpl lObjAlIndSer = new AllIndiaServicesDAOImpl(null,serv.getSessionFactory()); 
			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

			String aisType = StringUtility.getParameter("aisType",request).trim();
			String finTypeSelected=StringUtility.getParameter("fintype", request);
			logger.info("aisType is *****"+aisType);

			if (aisType!=null)
			{
				lstbillNo = lObjAlIndSer.selectBillNo(aisType,finTypeSelected);
			}

			inputMap.put("lstbillNo", lstbillNo);


			resultObject.setViewName("ContributionList");//set view name
			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object



		}catch(Exception e){
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in load employee lists of AIS on getAISEmpsContri () "+ e);
		}



		return resultObject;


	}








	//ended by ashish






	public ResultObject checkPranNO(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;

		try {

			setSessionInfo(inputMap);
			AllIndiaServicesDAOImpl lObjAlIndSer = new AllIndiaServicesDAOImpl(null,serv.getSessionFactory());    	

			String Pran_no = StringUtility.getParameter("pranno",request).trim();


			if (!"".equals(Pran_no)) 
			{
				lBlFlag = lObjAlIndSer.checkPranNO(Pran_no);
			}

			StringBuffer strbuflag = new StringBuffer();
			strbuflag.append("<XMLDOC>");
			strbuflag.append("<Flag>");
			strbuflag.append(lBlFlag);
			strbuflag.append("</Flag>");
			strbuflag.append("</XMLDOC>");

			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",strbuflag.toString()).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");
			resObj.setResultValue(inputMap);

		} catch (Exception e) {
			//e.printStackTrace();
			resObj.setResultValue(null);
			resObj.setThrowable(e);
			resObj.setResultCode(ErrorConstants.ERROR);
			resObj.setViewName("errorPage");
			logger.error(" Error in getcheckpranno " + e, e);
		}

		return resObj;

	}

	public ResultObject getAISEmpsContri(Map inputMap)throws Exception{
		logger.info("Inside Get getAISEmpsContri--------------------");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);


		List lstAisType = null;
		String aisType = null;
		String finType=null;
		String billno = null;
		List lstYear=null;

		String createdfile = null;
		List lstAlIndiaSerEmp = null;
		List lstbillNo =null;
		String aisTypeSelected=null;
		String finTypeSelected=null;
		Object obj1[];
		String fromDate=null;
		String toDate=null;
		
		try{
			setSessionInfo(inputMap);
			AllIndiaServicesDAOImpl lObjAlIndSer = new AllIndiaServicesDAOImpl(null,serv.getSessionFactory()); 
			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

			lstAisType  = lObjAlIndSer.getAISlist();
			lstYear=lObjAlIndSer.getFinyeardesc();

			logger.info("Inside Get AIS types-------------"+lstAisType);
			logger.info("Inside Get fin types-------------"+lstYear);
			inputMap.put("lstAisType",lstAisType);	
			inputMap.put("lstYear",lstYear);	

			Boolean check=false;
			System.out.println("check"+check);

			if(StringUtility.getParameter("aisType", request) != null && StringUtility.getParameter("aisType", request) != "" &&
				StringUtility.getParameter("fintype", request) != null  && StringUtility.getParameter("fintype", request) != "")
				{
				aisTypeSelected=StringUtility.getParameter("aisType", request);
				finTypeSelected=StringUtility.getParameter("fintype", request);
				logger.info("aisTypeSelected-------------------"+aisTypeSelected);
				logger.info("finTypeSelected-------------------"+finTypeSelected);
				lstbillNo = lObjAlIndSer.selectBillNo(aisTypeSelected,finTypeSelected);
				logger.info("lstbillNo-------------------"+lstbillNo.size());
				inputMap.put("lstbillNo", lstbillNo);
				inputMap.put("aisTypeSelected",aisTypeSelected);	
				inputMap.put("finTypeSelected",finTypeSelected);	
				//inputMap.put("billSel", lstbillNo);
			}

			logger.info("aisTypeSelected-------------------"+aisTypeSelected);

			if(StringUtility.getParameter("aisType", request) != null && StringUtility.getParameter("aisType", request) != "" && 
					StringUtility.getParameter("billno", request) != null && StringUtility.getParameter("billno", request) != "" &&
					StringUtility.getParameter("fintype", request) != null  && StringUtility.getParameter("fintype", request) != ""
			)


			{	



				check=true;
				aisType =  StringUtility.getParameter("aisType", request).trim();
				finType=StringUtility.getParameter("fintype", request).trim();
				logger.info("aisTypeSelected-------------------"+aisType);
				logger.info("finTypeSelected-------------------"+finType);
				inputMap.put("aisTypeSelected",aisType);	
				inputMap.put("finTypeSelected",finType);
				billno = StringUtility.getParameter("billno", request).trim();
				inputMap.put("billno",billno);
				String extn = StringUtility.getParameter("flag", request).trim();
				logger.info("Request object are----"+aisType+"----"+finType+"---"+billno+"----"+" extn "+extn);	

				List lFromToDate=lObjAlIndSer.selectFromToDate(finType);
				for (int liCtr = 0; liCtr < lFromToDate.size(); liCtr++) {
					obj1 = (Object[]) lFromToDate.get(liCtr);
					 fromDate=obj1[0].toString();
					 toDate=obj1[1].toString();
				}
				
				logger.info("fromDate is ---------------"+fromDate);
				logger.info("toDate is ---------------"+toDate);
				lstAlIndiaSerEmp = lObjAlIndSer.getEmployeeList(aisType, billno,finType,fromDate,toDate);

				Boolean flagg=true;

				logger.info("EMployee List-------------------"+lstAlIndiaSerEmp);


				System.out.println("lstAlIndiaSerEmp size  is"+lstAlIndiaSerEmp.size());

				if (lstAlIndiaSerEmp.size() == 0 || lstAlIndiaSerEmp==null)
				{
					inputMap.put("totalRecordsMstContri", 0);
					System.out.println("flagg"+flagg);
					inputMap.put("flagg", flagg);

				}
				else
				{
					inputMap.put("totalRecordsMstContri", lstAlIndiaSerEmp.size());
					flagg=false;
					System.out.println("flagg"+flagg);
					inputMap.put("flagg", flagg);
				}


				String	yearDesc=null;
				String monid=null;
				String yearDesc1=null;
				double emplyContri = 0;
				double emplyerContri = 0;

				for (Iterator it = lstAlIndiaSerEmp.iterator(); it.hasNext();)				 
				{

					Object[] lObj = (Object[]) it.next();
					emplyContri = emplyContri + Double.parseDouble(lObj[8] != null ? lObj[8].toString() : "0");
					emplyerContri = emplyerContri + Double.parseDouble(lObj[9] != null ? lObj[9].toString() : "0");

					monid = (lObj[7] != null) ? lObj[7].toString() : "";						
					logger.info(" month id  ***********"+ monid);

					yearDesc = (lObj[6] != null) ? lObj[6].toString() : "";						
					logger.info(" yearDesc ***********"+ yearDesc);

					String[] Yeardesc1=yearDesc.split("-");

					String yeardesc=null;

					if(Long.parseLong(monid)>3 && Long.parseLong(monid)<=12)  
					{
						yearDesc1= Yeardesc1[0];

					}

					else if(Long.parseLong(monid)<=3 && Long.parseLong(monid)>=1)  
					{
						yearDesc1= Yeardesc1[1];
					}



					logger.info(" yearDesc after checking is ***********"+ yearDesc1);
					inputMap.put("yearDesc1",yearDesc1);



				}

				Object[] obj = new Object[18];
				obj[0] =  "Total";
				obj[8] =  emplyContri;
				obj[9] =  emplyerContri;
				lstAlIndiaSerEmp.add(obj);

				if (lstAlIndiaSerEmp.size() != 0){
					inputMap.put("lstAlIndiaSerEmp", lstAlIndiaSerEmp);

				}




			}

			inputMap.put("check",check);
			inputMap.put("type","1");


			resultObject.setViewName("ContributionList");//set view name
			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object



		}catch(Exception e){
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in load employee lists of AIS on getAISEmpsContri () "+ e);
		}



		return resultObject;
	}
	
	public ResultObject getAISEmpsContriForChallan(Map inputMap)throws Exception{
		logger.info("Inside Get getAISEmpsContri--------------------");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);


		List lstAisType = null;
		String aisType = null;
		String finType=null;
		//String billno = null;
		List lstYear=null;

		String createdfile = null;
		List lstAlIndiaSerEmp = null;
		//List lstbillNo =null;
		String aisTypeSelected=null;
		String finTypeSelected=null;
		Object obj1[];
		String fromDate=null;
		String toDate=null;
		
		try{
			setSessionInfo(inputMap);
			AllIndiaServicesDAOImpl lObjAlIndSer = new AllIndiaServicesDAOImpl(null,serv.getSessionFactory()); 
			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

			lstAisType  = lObjAlIndSer.getAISlist();
			lstYear=lObjAlIndSer.getFinyeardesc();

			logger.info("Inside Get AIS types-------------"+lstAisType);
			logger.info("Inside Get fin types-------------"+lstYear);
			inputMap.put("lstAisType",lstAisType);	
			inputMap.put("lstYear",lstYear);	

			Boolean check=false;
			System.out.println("check"+check);

			if(StringUtility.getParameter("aisType", request) != null && StringUtility.getParameter("aisType", request) != "" &&
				StringUtility.getParameter("fintype", request) != null  && StringUtility.getParameter("fintype", request) != "")
				{
				aisTypeSelected=StringUtility.getParameter("aisType", request);
				finTypeSelected=StringUtility.getParameter("fintype", request);
				logger.info("aisTypeSelected-------------------"+aisTypeSelected);
				logger.info("finTypeSelected-------------------"+finTypeSelected);
				inputMap.put("aisTypeSelected",aisTypeSelected);	
				inputMap.put("finTypeSelected",finTypeSelected);	
				//inputMap.put("billSel", lstbillNo);
			}

			logger.info("aisTypeSelected-------------------"+aisTypeSelected);

			if(StringUtility.getParameter("aisType", request) != null && StringUtility.getParameter("aisType", request) != "" && 
				StringUtility.getParameter("fintype", request) != null  && StringUtility.getParameter("fintype", request) != ""
			)


			{	



				check=true;
				aisType =  StringUtility.getParameter("aisType", request).trim();
				finType=StringUtility.getParameter("fintype", request).trim();
				logger.info("aisTypeSelected-------------------"+aisType);
				logger.info("finTypeSelected-------------------"+finType);
				inputMap.put("aisTypeSelected",aisType);	
				inputMap.put("finTypeSelected",finType);
				
				String extn = StringUtility.getParameter("flag", request).trim();
			

				List lFromToDate=lObjAlIndSer.selectFromToDate(finType);
				for (int liCtr = 0; liCtr < lFromToDate.size(); liCtr++) {
					obj1 = (Object[]) lFromToDate.get(liCtr);
					 fromDate=obj1[0].toString();
					 toDate=obj1[1].toString();
				}
				
				logger.info("fromDate is ---------------"+fromDate);
				logger.info("toDate is ---------------"+toDate);
				lstAlIndiaSerEmp = lObjAlIndSer.getEmployeeListForCahallan(aisType,finType,fromDate,toDate);

				Boolean flagg=true;

				logger.info("EMployee List-------------------"+lstAlIndiaSerEmp);


				System.out.println("lstAlIndiaSerEmp size  is"+lstAlIndiaSerEmp.size());

				if (lstAlIndiaSerEmp.size() == 0 || lstAlIndiaSerEmp==null)
				{
					inputMap.put("totalRecordsMstContri", 0);
					System.out.println("flagg"+flagg);
					inputMap.put("flagg", flagg);

				}
				else
				{
					inputMap.put("totalRecordsMstContri", lstAlIndiaSerEmp.size());
					flagg=false;
					System.out.println("flagg"+flagg);
					inputMap.put("flagg", flagg);
				}


				String	yearDesc=null;
				String monid=null;
				String yearDesc1=null;
				double emplyContri = 0;
				double emplyerContri = 0;

				for (Iterator it = lstAlIndiaSerEmp.iterator(); it.hasNext();)				 
				{

					Object[] lObj = (Object[]) it.next();
					emplyContri = emplyContri + Double.parseDouble(lObj[8] != null ? lObj[8].toString() : "0");
					emplyerContri = emplyerContri + Double.parseDouble(lObj[9] != null ? lObj[9].toString() : "0");

					monid = (lObj[7] != null) ? lObj[7].toString() : "";						
					logger.info(" month id  ***********"+ monid);

					yearDesc = (lObj[6] != null) ? lObj[6].toString() : "";						
					logger.info(" yearDesc ***********"+ yearDesc);

					String[] Yeardesc1=yearDesc.split("-");

					String yeardesc=null;

					if(Long.parseLong(monid)>3 && Long.parseLong(monid)<=12)  
					{
						yearDesc1= Yeardesc1[0];

					}

					else if(Long.parseLong(monid)<=3 && Long.parseLong(monid)>=1)  
					{
						yearDesc1= Yeardesc1[1];
					}



					logger.info(" yearDesc after checking is ***********"+ yearDesc1);
					inputMap.put("yearDesc1",yearDesc1);



				}

				Object[] obj = new Object[18];
				obj[0] =  "Total";
				obj[8] =  emplyContri;
				obj[9] =  emplyerContri;
				lstAlIndiaSerEmp.add(obj);

				if (lstAlIndiaSerEmp.size() != 0){
					inputMap.put("lstAlIndiaSerEmp", lstAlIndiaSerEmp);

				}




			}

			inputMap.put("check",check);
			inputMap.put("type","2");


			resultObject.setViewName("ContributionList");//set view name
			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object



		}catch(Exception e){
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in load employee lists of AIS on getAISEmpsContri () "+ e);
		}



		return resultObject;
	}


	@SuppressWarnings("null")
	public ResultObject createFilesForNSDL(
			Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		logger.info("in createTxtFile---------------------- ");
		List lstemployee = null;
		Long Num=null;
		BufferedReader br = null;

		String empname = null;
		String dcpsid=null;
		String pranno = null;
		String govEmpContri = null;
		String subempContri = null;
		String Contritype = null;

		int countsum = 0;
		String govcontiSum = null;
		String subcontiSum = null;
		String TotalContri =null;
		String TotalContrisum = null;
		String nonRmonth = null;
		String nonRyear = null;
		String Rmonth = null;
		String Ryear = null;
		String monid = null;
		String monthname =null;
		String yerid = null;
		String yearDesc =null;
		String Month = null;
		String Year =null;
		String Monthid =null;
		String Ename = null;

		String extn=null;
		String aisType=null;
		String billno=null;
		String yearid=null;
		StringBuilder Strbr = new StringBuilder();
		StringBuilder Strbr1 = new StringBuilder();
		String extnFlag=null;
		String Smonthid=null;
		String Syeardesc=null;
		String yearcode=null;
		Boolean update=false;
		String typeOfPayment=null;
		Object obj1[];
		String fromDate=null;
		String toDate=null;
		try {

			setSessionInfo(inputMap);
				HttpServletRequest request = (HttpServletRequest) inputMap
			.get("requestObj");
			 
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			if(StringUtility.getParameter("aisType", request) != null && StringUtility.getParameter("aisType", request) != "" && 
					StringUtility.getParameter("billno", request) != null && StringUtility.getParameter("billno", request) != "" &&	
				StringUtility.getParameter("FINtype", request) != null && StringUtility.getParameter("FINtype", request) != "" )	
			{	

				aisType =  StringUtility.getParameter("aisType", request).trim();
				billno =StringUtility.getParameter("billno", request).trim();
				extn = StringUtility.getParameter("flag", request).trim();

				extnFlag = StringUtility.getParameter("flagFile", request).trim();

			}

			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			logger.info("extnFlag Is *************"+extnFlag);
			String finType=StringUtility.getParameter("FINtype", request).trim();
			AllIndiaServicesDAOImpl lObjAlIndSer = new AllIndiaServicesDAOImpl(null,serv.getSessionFactory());
			logger.info("finType Is *************"+finType);
			List lFromToDate=lObjAlIndSer.selectFromToDate(finType);
			for (int liCtr = 0; liCtr < lFromToDate.size(); liCtr++) {
				obj1 = (Object[]) lFromToDate.get(liCtr);
				 fromDate=obj1[0].toString();
				 toDate=obj1[1].toString();
			}
			
			logger.info("fromDate is ---------------"+fromDate);
			logger.info("toDate is ---------------"+toDate);
			
			lstemployee=lObjAlIndSer.getEmployeeList(aisType, billno,finType,fromDate,toDate);
			logger.info("--------HEllo ----");
			//---------- Added by Ashish--------


			boolean Batch=false;
			String BatchId=null;
			Batch=lObjAlIndSer.selectDataForNSDLGen(aisType,billno,finType);
			logger.info("--------BatchId ----"+BatchId);

			if(lstemployee.size()!=0 && Batch==false  ){
				BatchId=lObjAlIndSer.selectDataForNSDLRepo(aisType,billno,finType);
				Long Batchnw=Long.parseLong(BatchId)+1;
				logger.info("Batchnw Is **********"+Batchnw);
				lObjAlIndSer.insertDataForNSDLRepo(Batchnw,aisType,billno);
				BatchId=lObjAlIndSer.selectDataForNSDLRepo(aisType,billno,finType);
				String finYear=lObjAlIndSer.selectfinYearforBill(aisType,billno,finType);
				lObjAlIndSer.updatefinYear(aisType,finYear,billno,BatchId);
				update=true;
			}
			else if(lstemployee.size()!=0 && Batch==true  )
			{
				BatchId=lObjAlIndSer.selectDataForNSDLRepo(aisType,billno,finType);
				Long NwbatchId=Long.parseLong(BatchId)+1;
				logger.info("NwbatchId Is **********"+NwbatchId);
				lObjAlIndSer.updateAgainNsdl(NwbatchId,aisType,billno,finType);
				BatchId=lObjAlIndSer.selectDataForNSDLRepo(aisType,billno,finType);
				
				update=false;
			}


			logger.info("BatchId Is **********"+BatchId);
			//---------- ended by Ashish--------




			int totalsize = lstemployee.size();
			logger.info("totalsize Is **********"+totalsize);
			if (lstemployee != null && !lstemployee.isEmpty()) 

			{			

				int count = 0;	
				int  i = 4;


				for (Iterator it = lstemployee.iterator(); it.hasNext();)				 
				{
					count++;
					Object[] lObj = (Object[]) it.next();
					//String	msg = "Success";			

					empname = (lObj[0] != null) ? lObj[0].toString(): "NA";
					logger.info("Employee name------------- "+empname);

					dcpsid = ((lObj[1] != null)? lObj[1].toString() :"");
					logger.info("dcpsid is ------------- "+dcpsid);

					pranno = ((lObj[2] != null)? lObj[2].toString() :"");
					logger.info("PranNO is ------------- "+pranno);

					Contritype = ((lObj[3] != null)?lObj[3].toString() :"");
					logger.info("Contribution Type------------- "+Contritype);


					yerid = (lObj[4]!= null) ? lObj[4].toString() : "";						
					logger.info(" year id   ***********"+ yerid);

					monthname = (lObj[5] != null) ? lObj[5].toString() : "";						
					logger.info(" month name ***********"+ monthname);

					yearDesc = (lObj[6] != null) ? lObj[6].toString() : "";						
					logger.info(" yearDesc ***********"+ yearDesc);


					monid = (lObj[7] != null) ? lObj[7].toString() : "";						
					logger.info(" month id  ***********"+ monid);


					govEmpContri = (lObj[8] != null) ? lObj[8].toString() : "";		
					//govEmpContri = (Float) ((lObj[13] != null)? Float.parseFloat(new DecimalFormat("##.##").format(Float.parseFloat(lObj[13].toString()))):new Float(new DecimalFormat("##.##").format(1000.00)));

					logger.info("Government Contribution ------------- "+govEmpContri);

					subempContri = (lObj[9] != null) ? lObj[9].toString() : "";	
					//subempContri = (Float) ((lObj[14] != null)? Float.parseFloat( new DecimalFormat("##.##").format(Float.parseFloat(lObj[14].toString()))):new Float(new DecimalFormat("##.##").format(1000.00)));
					logger.info("Employee Contribution------------- "+subempContri);


					Smonthid = (lObj[10] != null) ? lObj[10].toString() : "";						
					logger.info(" month id  ***********"+ Smonthid);


					Syeardesc = (lObj[11] != null) ? lObj[11].toString() : "";						
					logger.info(" month id  ***********"+ Syeardesc);

					typeOfPayment = (lObj[12] != null) ? lObj[12].toString() : "";						
					logger.info(" typeOfPayment   ***********"+ typeOfPayment);


					if(Long.parseLong(monid)< 10)
					{
						monid="0"+monid;
					}

					List dcpsContriIdPk=lObjAlIndSer.selectTrnPk(dcpsid,billno,fromDate,toDate);

					if(dcpsContriIdPk != null && !dcpsContriIdPk.isEmpty() && update==true)
					{
						for(int j=0;j<dcpsContriIdPk.size();j++){
							Long dcpsContriIdPks=Long.valueOf(dcpsContriIdPk.get(j).toString());
							logger.info("In IF BatchId Is **********"+BatchId);
							lObjAlIndSer.updateRepStatus(aisType,(dcpsContriIdPks),BatchId,billno,fromDate,toDate);
						}
					}
					else if(dcpsContriIdPk != null && !dcpsContriIdPk.isEmpty() && update==false){
						
						for(int j=0;j<dcpsContriIdPk.size();j++){
							Long dcpsContriIdPks=Long.valueOf(dcpsContriIdPk.get(j).toString());
							logger.info("In IF BatchId Is **********"+BatchId);
							lObjAlIndSer.updateRepStatus(aisType,(dcpsContriIdPks),BatchId,billno,fromDate,toDate);
						}
						
						
					}
					
				Double totalContribution = (govEmpContri != null) ? Double.parseDouble(govEmpContri) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri) ;
					//Double totalContribution = Double.parseDouble(govEmpContri) +Double.parseDouble(subempContri);
					TotalContri=totalContribution.toString();
					logger.info(" Total Contribution------------- "+TotalContri);	

					countsum = countsum + count;
					logger.info("Count of sum------------- "+countsum);	

					Double GovContributionSum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(govEmpContri) : 0.00 + Double.parseDouble(govEmpContri);
					//Double GovContributionSum =  Double.parseDouble(govcontiSum) + Double.parseDouble(govEmpContri);
					govcontiSum=GovContributionSum.toString();
					logger.info(" Gov Contribution------------- "+govcontiSum);	

					Double subcontributionSum = (subcontiSum != null) ? Double.parseDouble(subcontiSum) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri);
					//Double	subcontributionSum =  Double.parseDouble(subcontiSum) + Double.parseDouble(subempContri);
					subcontiSum=subcontributionSum.toString();
					logger.info("Sub Contribution------------- "+subcontiSum);	


					Double TotalContributionsum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(subcontiSum) : 0.00 +Double.parseDouble(subcontiSum);
					//Double TotalContributionsum = Double.parseDouble(govcontiSum) + Double.parseDouble(subcontiSum);
					TotalContrisum=TotalContributionsum.toString();
					logger.info("  sum of Total Contri ------------- "+TotalContrisum);			  



					logger.info("Contribution Type 1------------- "+Contritype);
					String [] amt=null;

					logger.info("govEmpContri +++++++++------------- "+govEmpContri);
					amt=govEmpContri.toString().split("\\.");
					logger.info("amt[1]------------- "+amt[1]);
					if(amt[1].length()==1)
					{
						govEmpContri=govEmpContri+"0";
					}
					amt=subempContri.split("\\.");
					if(amt[1].length()==1)
					{
						subempContri=subempContri+"0";
					}
					amt=TotalContri.split("\\.");
					if(amt[1].length()==1)
					{
						TotalContri=TotalContri+"0";
					}
					amt=govcontiSum.split("\\.");
					if(amt[1].length()==1)
					{
						govcontiSum=govcontiSum+"0";
					}

					amt=subcontiSum.split("\\.");
					if(amt[1].length()==1)
					{
						subcontiSum=subcontiSum+"0";
					}
					amt=TotalContrisum.split("\\.");
					if(amt[1].length()==1)
					{
						TotalContrisum=TotalContrisum+"0";
					}


					String Contricode;
					if(Contritype.equals("Regular") || Contritype.equals("Delayed"))
					{
						Contricode="C";
						Contritype="Regular";
					}
					else 
					{
						Contricode= "A";
					}

					if(extnFlag!=null && !extnFlag.equals("") && Long.parseLong(extnFlag)==1)
					{
						logger.info("hello in if");
						Strbr.append(i+"^");
						Strbr.append("SD"+"^");
						Strbr.append("1"+"^");
						Strbr.append("1"+"^");
						Strbr.append(count+"^");
						Strbr.append(pranno +"^");
						Strbr.append(govEmpContri+"^");
						Strbr.append(subempContri+"^");
						Strbr.append("^");
						Strbr.append(TotalContri+"^");
						Strbr.append(Contricode+"^");
						Strbr.append(monid+"^");
						Strbr.append(Syeardesc+"^");
						Strbr.append(Contritype +" Contribution for "+monthname+" "+Syeardesc+"^");


						Strbr.append("\r\n");
						logger.info(" String buffer value are ------------- "+Strbr.toString());

					}


					else
					{

						Strbr1.append(i+"^");
						Strbr1.append("SD"+"^");
						Strbr1.append("1"+"^");
						Strbr1.append("1"+"^");
						Strbr1.append(count+"^");
						Strbr1.append(pranno +"^");
						Strbr1.append(govEmpContri+"^");
						Strbr1.append(subempContri+"^");
						Strbr1.append("^");
						Strbr1.append(TotalContri+"^");
						Strbr1.append(Contricode+"^");
						Strbr1.append(monid+"^");
						Strbr1.append(Syeardesc+"^");
						Strbr1.append(Contritype +" Contribution for "+monthname+" "+Syeardesc+"^");
						Strbr1.append("\r\n");
						logger.info(" String buffer value are ------------- "+Strbr1.toString());


					}

					i++;




				}


				//new line 
				String lineSeperator ="\r\n"; 
				//System.getProperty("line.separator");.....not working


				String os = System.getProperty("os.name");

				System.out.println("os  "+os);
				if (os.toLowerCase().indexOf("unix") > 0){
					lineSeperator="\n";

				} else if (os.toLowerCase().indexOf("windows") > 0){
					lineSeperator ="\r\n"; 

				} else {

				}

				HttpServletResponse response = (HttpServletResponse) inputMap
				.get("responseObj");
				PrintWriter outputfile =response.getWriter();
				PrintWriter outputfile1 =response.getWriter();
				if(extnFlag!=null && !extnFlag.equals("") && Long.parseLong(extnFlag)==1)
				{
					logger.info("hello in if");
					getFileHeader(outputfile);
					//outputfile.append(lineSeperator);
					getBatchHeader(outputfile,totalsize,BatchId,govcontiSum,subcontiSum,TotalContrisum);
					//	outputfile.append(lineSeperator);
					getDTOHeader( outputfile,totalsize,govcontiSum,subcontiSum);	
					//	outputfile.append(lineSeperator);


				}
				else {
					getFileHeaderforfpu(outputfile1);
					//	outputfile1.append(lineSeperator);
					getBatchHeaderforfpu(outputfile1,totalsize,BatchId,govcontiSum,subcontiSum,TotalContrisum);
					//	outputfile1.append(lineSeperator);
					getDTOHeaderforfpu( outputfile1,totalsize,govcontiSum,subcontiSum);			
					//	outputfile1.append(lineSeperator);
				}

				String AcType=lObjAlIndSer.accMain(aisType);

				String lStrFileName=AcType+billno+count;

				if(extn.equals("txt"))
				{
					try{
						String fileName = lStrFileName + ".txt";
						response.setContentType("text/plain;charset=UTF-8");

						response.addHeader("Content-disposition",
								"attachment; filename=" + fileName);
						response.setCharacterEncoding("UTF-8");


						outputfile.write(Strbr.toString());
						outputfile.flush();

					}
					catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(outputfile!=null)
							outputfile.close();
					}



				}
				else if (extn.equals("fpu"))
				{
					try{



						String fileName = lStrFileName + ".fpu";
						response.setContentType("text/plain;charset=UTF-8");

						response.addHeader("Content-disposition",
								"attachment; filename=" + fileName);
						response.setCharacterEncoding("UTF-8");


						outputfile1.write(Strbr1.toString());
						 BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(new File("D:\\bin\\test123.txt")));
								
						outputfile1.flush();

					}
					catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(outputfile1!=null)
							outputfile1.close();

					}



				}






			}



		}	 
		catch (Exception e)
		{
			logger.info("Error occure in createTxtFile()"+e);
			e.printStackTrace();
			resObj.setResultCode(ErrorConstants.ERROR);


		}
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ContributionList");
		return resObj;

	}
	public ResultObject createFilesForNSDLForChallan(
			Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		logger.info("in createTxtFile---------------------- ");
		List lstemployee = null;
		Long Num=null;
		BufferedReader br = null;

		String empname = null;
		String dcpsid=null;
		String pranno = null;
		String govEmpContri = null;
		String subempContri = null;
		String Contritype = null;

		int countsum = 0;
		String govcontiSum = null;
		String subcontiSum = null;
		String TotalContri =null;
		String TotalContrisum = null;
		String nonRmonth = null;
		String nonRyear = null;
		String Rmonth = null;
		String Ryear = null;
		String monid = null;
		String monthname =null;
		String yerid = null;
		String yearDesc =null;
		String Month = null;
		String Year =null;
		String Monthid =null;
		String Ename = null;

		String extn=null;
		String aisType=null;
		//String billno=null;
		String yearid=null;
		StringBuilder Strbr = new StringBuilder();
		StringBuilder Strbr1 = new StringBuilder();
		String extnFlag=null;
		String Smonthid=null;
		String Syeardesc=null;
		String yearcode=null;
		Boolean update=false;
		String typeOfPayment=null;
		Object obj1[];
		String fromDate=null;
		String toDate=null;
		try {

			setSessionInfo(inputMap);
				HttpServletRequest request = (HttpServletRequest) inputMap
			.get("requestObj");
			 
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");
			if(StringUtility.getParameter("aisType", request) != null && StringUtility.getParameter("aisType", request) != "" && 
				StringUtility.getParameter("FINtype", request) != null && StringUtility.getParameter("FINtype", request) != "" )	
			{	

				aisType =  StringUtility.getParameter("aisType", request).trim();
				extn = StringUtility.getParameter("flag", request).trim();
				extnFlag = StringUtility.getParameter("flagFile", request).trim();

			}

			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			logger.info("extnFlag Is *************"+extnFlag);
			String finType=StringUtility.getParameter("FINtype", request).trim();
			AllIndiaServicesDAOImpl lObjAlIndSer = new AllIndiaServicesDAOImpl(null,serv.getSessionFactory());
			logger.info("finType Is *************"+finType);
			List lFromToDate=lObjAlIndSer.selectFromToDate(finType);
			for (int liCtr = 0; liCtr < lFromToDate.size(); liCtr++) {
				obj1 = (Object[]) lFromToDate.get(liCtr);
				 fromDate=obj1[0].toString();
				 toDate=obj1[1].toString();
			}
			
			String finDesc=lObjAlIndSer.getFinyrdesc(Long.parseLong(finType));
			String [] finyr=finDesc.split("-");
			String finyr1=finyr[0];
			String finyr2=finyr[1];
			
			logger.info("fromDate is ---------------"+fromDate);
			logger.info("toDate is ---------------"+toDate);
			
			lstemployee=lObjAlIndSer.getEmployeeListForCahallan(aisType,finType,fromDate,toDate);
			logger.info("--------HEllo ----");
			//---------- Added by Ashish--------


			boolean Batch=false;
			String BatchId=null;
			String fileId="";
			if(aisType!=null && "700240".equalsIgnoreCase(aisType))
			{
				fileId=fileId+"IAS";
			}
			else if(aisType!=null && "700241".equalsIgnoreCase(aisType))
			{
				fileId=fileId+"IPS";
			}
			else if (aisType!=null && "700242".equalsIgnoreCase(aisType))
			{
				fileId=fileId+"IFS";
			}
			fileId=fileId+finyr1+finyr2;
			Integer fileCount=lObjAlIndSer.selectFileIDForChallan(fileId);
			if(fileCount < 10)
			{
				fileId=fileId+"0"+fileCount.toString()+"D";
			}
			else
			{
				fileId=fileId+fileCount.toString()+"D";
			}
				
			Batch=lObjAlIndSer.selectDataForNSDLGenChallan(aisType,finType);
			logger.info("--------BatchId ----"+BatchId);

			if(lstemployee.size()!=0 && Batch==false  ){
				BatchId=lObjAlIndSer.selectDataForNSDLRepoChallan(aisType,finType);
				Long Batchnw=Long.parseLong(BatchId)+1;
				logger.info("Batchnw Is **********"+Batchnw);
				lObjAlIndSer.insertDataForNSDLRepoChallan(Batchnw,aisType,fileId);
				BatchId=lObjAlIndSer.selectDataForNSDLRepoChallan(aisType,finType);
				
				lObjAlIndSer.updatefinYearChallan(aisType,finType,BatchId);
				update=true;
			}
			else if(lstemployee.size()!=0 && Batch==true  )
			{
				BatchId=lObjAlIndSer.selectDataForNSDLRepoChallan(aisType,finType);
				Long NwbatchId=Long.parseLong(BatchId)+1;
				logger.info("NwbatchId Is **********"+NwbatchId);
				lObjAlIndSer.updateAgainNsdlChallan(NwbatchId,aisType,finType,fileId);
				BatchId=lObjAlIndSer.selectDataForNSDLRepoChallan(aisType,finType);
				
				update=false;
			}


			logger.info("BatchId Is **********"+BatchId);
			//---------- ended by Ashish--------




			int totalsize = lstemployee.size();
			logger.info("totalsize Is **********"+totalsize);
			if (lstemployee != null && !lstemployee.isEmpty()) 

			{			

				int count = 0;	
				int  i = 4;


				for (Iterator it = lstemployee.iterator(); it.hasNext();)				 
				{
					count++;
					Object[] lObj = (Object[]) it.next();
					//String	msg = "Success";			

					empname = (lObj[0] != null) ? lObj[0].toString(): "NA";
					logger.info("Employee name------------- "+empname);

					dcpsid = ((lObj[1] != null)? lObj[1].toString() :"");
					logger.info("dcpsid is ------------- "+dcpsid);

					pranno = ((lObj[2] != null)? lObj[2].toString() :"");
					logger.info("PranNO is ------------- "+pranno);

					Contritype = ((lObj[3] != null)?lObj[3].toString() :"");
					logger.info("Contribution Type------------- "+Contritype);


					yerid = (lObj[4]!= null) ? lObj[4].toString() : "";						
					logger.info(" year id   ***********"+ yerid);

					monthname = (lObj[5] != null) ? lObj[5].toString() : "";						
					logger.info(" month name ***********"+ monthname);

					yearDesc = (lObj[6] != null) ? lObj[6].toString() : "";						
					logger.info(" yearDesc ***********"+ yearDesc);


					monid = (lObj[7] != null) ? lObj[7].toString() : "";						
					logger.info(" month id  ***********"+ monid);


					govEmpContri = (lObj[8] != null) ? lObj[8].toString() : "";		
					//govEmpContri = (Float) ((lObj[13] != null)? Float.parseFloat(new DecimalFormat("##.##").format(Float.parseFloat(lObj[13].toString()))):new Float(new DecimalFormat("##.##").format(1000.00)));

					logger.info("Government Contribution ------------- "+govEmpContri);

					subempContri = (lObj[9] != null) ? lObj[9].toString() : "";	
					//subempContri = (Float) ((lObj[14] != null)? Float.parseFloat( new DecimalFormat("##.##").format(Float.parseFloat(lObj[14].toString()))):new Float(new DecimalFormat("##.##").format(1000.00)));
					logger.info("Employee Contribution------------- "+subempContri);


					Smonthid = (lObj[10] != null) ? lObj[10].toString() : "";						
					logger.info(" month id  ***********"+ Smonthid);


					Syeardesc = (lObj[11] != null) ? lObj[11].toString() : "";						
					logger.info(" month id  ***********"+ Syeardesc);

					typeOfPayment = (lObj[12] != null) ? lObj[12].toString() : "";						
					logger.info(" typeOfPayment   ***********"+ typeOfPayment);


					if(Long.parseLong(monid)< 10)
					{
						monid="0"+monid;
					}

					List dcpsContriIdPk=lObjAlIndSer.selectTrnPkForChallan(dcpsid,fromDate,toDate);

					if(dcpsContriIdPk != null && !dcpsContriIdPk.isEmpty() && update==true)
					{
						for(int j=0;j<dcpsContriIdPk.size();j++){
							Long dcpsContriIdPks=Long.valueOf(dcpsContriIdPk.get(j).toString());
							logger.info("In IF BatchId Is **********"+BatchId);
							lObjAlIndSer.updateRepStatusForChallan(aisType,(dcpsContriIdPks),BatchId,fromDate,toDate);
						}
					}
					else if(dcpsContriIdPk != null && !dcpsContriIdPk.isEmpty() && update==false){
						
						for(int j=0;j<dcpsContriIdPk.size();j++){
							Long dcpsContriIdPks=Long.valueOf(dcpsContriIdPk.get(j).toString());
							logger.info("In IF BatchId Is **********"+BatchId);
							lObjAlIndSer.updateRepStatusForChallan(aisType,(dcpsContriIdPks),BatchId,fromDate,toDate);
						}
						
						
					}
					
				Double totalContribution = (govEmpContri != null) ? Double.parseDouble(govEmpContri) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri) ;
					//Double totalContribution = Double.parseDouble(govEmpContri) +Double.parseDouble(subempContri);
					TotalContri=totalContribution.toString();
					logger.info(" Total Contribution------------- "+TotalContri);	

					countsum = countsum + count;
					logger.info("Count of sum------------- "+countsum);	

					Double GovContributionSum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(govEmpContri) : 0.00 + Double.parseDouble(govEmpContri);
					//Double GovContributionSum =  Double.parseDouble(govcontiSum) + Double.parseDouble(govEmpContri);
					govcontiSum=GovContributionSum.toString();
					logger.info(" Gov Contribution------------- "+govcontiSum);	

					Double subcontributionSum = (subcontiSum != null) ? Double.parseDouble(subcontiSum) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri);
					//Double	subcontributionSum =  Double.parseDouble(subcontiSum) + Double.parseDouble(subempContri);
					subcontiSum=subcontributionSum.toString();
					logger.info("Sub Contribution------------- "+subcontiSum);	


					Double TotalContributionsum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(subcontiSum) : 0.00 +Double.parseDouble(subcontiSum);
					//Double TotalContributionsum = Double.parseDouble(govcontiSum) + Double.parseDouble(subcontiSum);
					TotalContrisum=TotalContributionsum.toString();
					logger.info("  sum of Total Contri ------------- "+TotalContrisum);			  



					logger.info("Contribution Type 1------------- "+Contritype);
					String [] amt=null;

					logger.info("govEmpContri +++++++++------------- "+govEmpContri);
					amt=govEmpContri.toString().split("\\.");
					logger.info("amt[1]------------- "+amt[1]);
					if(amt[1].length()==1)
					{
						govEmpContri=govEmpContri+"0";
					}
					amt=subempContri.split("\\.");
					if(amt[1].length()==1)
					{
						subempContri=subempContri+"0";
					}
					amt=TotalContri.split("\\.");
					if(amt[1].length()==1)
					{
						TotalContri=TotalContri+"0";
					}
					amt=govcontiSum.split("\\.");
					if(amt[1].length()==1)
					{
						govcontiSum=govcontiSum+"0";
					}

					amt=subcontiSum.split("\\.");
					if(amt[1].length()==1)
					{
						subcontiSum=subcontiSum+"0";
					}
					amt=TotalContrisum.split("\\.");
					if(amt[1].length()==1)
					{
						TotalContrisum=TotalContrisum+"0";
					}


					String Contricode;
					if(Contritype.equals("Regular") || Contritype.equals("Delayed"))
					{
						Contricode="C";
						Contritype="Regular";
					}
					else 
					{
						Contricode= "A";
					}

					if(extnFlag!=null && !extnFlag.equals("") && Long.parseLong(extnFlag)==1)
					{
						logger.info("hello in if");
						Strbr.append(i+"^");
						Strbr.append("SD"+"^");
						Strbr.append("1"+"^");
						Strbr.append("1"+"^");
						Strbr.append(count+"^");
						Strbr.append(pranno +"^");
						Strbr.append(govEmpContri+"^");
						Strbr.append(subempContri+"^");
						Strbr.append("^");
						Strbr.append(TotalContri+"^");
						Strbr.append(Contricode+"^");
						Strbr.append(monid+"^");
						Strbr.append(Syeardesc+"^");
						Strbr.append(Contritype +" Contribution for "+monthname+" "+Syeardesc+"^");


						Strbr.append("\r\n");
						logger.info(" String buffer value are ------------- "+Strbr.toString());

					}


					else
					{

						Strbr1.append(i+"^");
						Strbr1.append("SD"+"^");
						Strbr1.append("1"+"^");
						Strbr1.append("1"+"^");
						Strbr1.append(count+"^");
						Strbr1.append(pranno +"^");
						Strbr1.append(govEmpContri+"^");
						Strbr1.append(subempContri+"^");
						Strbr1.append("^");
						Strbr1.append(TotalContri+"^");
						Strbr1.append(Contricode+"^");
						Strbr1.append(monid+"^");
						Strbr1.append(Syeardesc+"^");
						Strbr1.append(Contritype +" Contribution for "+monthname+" "+Syeardesc+"^");
						Strbr1.append("\r\n");
						logger.info(" String buffer value are ------------- "+Strbr1.toString());


					}

					i++;




				}
				
				

				//new line 
				String lineSeperator ="\r\n"; 
				//System.getProperty("line.separator");.....not working


				String os = System.getProperty("os.name");

				System.out.println("os  "+os);
				if (os.toLowerCase().indexOf("unix") > 0){
					lineSeperator="\n";

				} else if (os.toLowerCase().indexOf("windows") > 0){
					lineSeperator ="\r\n"; 

				} else {

				}

				HttpServletResponse response = (HttpServletResponse) inputMap
				.get("responseObj");
				PrintWriter outputfile =response.getWriter();
				PrintWriter outputfile1 =response.getWriter();
				if(extnFlag!=null && !extnFlag.equals("") && Long.parseLong(extnFlag)==1)
				{
					logger.info("hello in if");
					getFileHeader(outputfile);
					//outputfile.append(lineSeperator);
					getBatchHeader(outputfile,totalsize,BatchId,govcontiSum,subcontiSum,TotalContrisum);
					//	outputfile.append(lineSeperator);
					getDTOHeader( outputfile,totalsize,govcontiSum,subcontiSum);	
					//	outputfile.append(lineSeperator);


				}
				else {
					getFileHeaderforfpu(outputfile1);
					//	outputfile1.append(lineSeperator);
					getBatchHeaderforfpu(outputfile1,totalsize,BatchId,govcontiSum,subcontiSum,TotalContrisum);
					//	outputfile1.append(lineSeperator);
					getDTOHeaderforfpu( outputfile1,totalsize,govcontiSum,subcontiSum);			
					//	outputfile1.append(lineSeperator);
				}
				
				
				lObjAlIndSer.updateFileAmount(fileId,Double.parseDouble(TotalContrisum));

				String AcType=lObjAlIndSer.accMain(aisType);

				

				if(extn.equals("txt"))
				{
					try{
						String fileName = fileId + ".txt";
						response.setContentType("text/plain;charset=UTF-8");

						response.addHeader("Content-disposition",
								"attachment; filename=" + fileName);
						response.setCharacterEncoding("UTF-8");


						outputfile.write(Strbr.toString());
						outputfile.flush();

					}
					catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(outputfile!=null)
							outputfile.close();
					}



				}
				else if (extn.equals("fpu"))
				{
					try{



						String fileName = fileId + ".fpu";
						response.setContentType("text/plain;charset=UTF-8");

						response.addHeader("Content-disposition",
								"attachment; filename=" + fileName);
						response.setCharacterEncoding("UTF-8");


						outputfile1.write(Strbr1.toString());
						 BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(new File("D:\\bin\\test123.txt")));
								
						outputfile1.flush();

					}
					catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(outputfile1!=null)
							outputfile1.close();

					}



				}






			}



		}	 
		catch (Exception e)
		{
			logger.info("Error occure in createTxtFile()"+e);
			e.printStackTrace();
			resObj.setResultCode(ErrorConstants.ERROR);


		}
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);
		resObj.setViewName("ContributionList");
		return resObj;

	}




	private void getFileHeader(PrintWriter br ) throws IOException
	{

		br.write("1"+"^");   		 br.write("FH"+"^"); 		 br.write("O"+"^"); 		 br.write("4014721"+"^");		 br.write("1"+"^");
		br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^"); 
		br.write("\r\n");

	}


	private void getBatchHeader(PrintWriter br , int count,String batchId,String govContri,String SubContri, String Total) throws IOException
	{


		Calendar cal = Calendar.getInstance(TimeZone.getDefault());	
		String date = "ddMMyyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(date);
		sdf.setTimeZone(TimeZone.getDefault());
		String currentdate = sdf.format(cal.getTime());
		//logger.info("getBatchHeader**batchId**"+batchId);

		//	long time = System.currentTimeMillis();

		br.write("2"+"^");  		 br.write("BH"+"^"); 		 br.write("1"+"^"); 		 br.write("R"+"^");
		br.write("4014721"+"^");  		 br.write(currentdate+"^"); 		 br.write("4014721"+batchId+"^"); 
		br.write("^"); 	 br.write("1"+"^"); 		 br.write(count+"^"); 		 br.write(govContri+"^");
		br.write(SubContri+"^"); 		 br.write("^"); 		 br.write(Total+"^"); 		 br.write("\r\n");

	}

	private void getDTOHeader( PrintWriter br ,int count, String govContri,String SubContri) throws IOException
	{

		br.write("3"+"^");		 br.write("DH"+"^"); 		 br.write("1"+"^"); 		 br.write("1"+"^"); 		 br.write("SGV148099C"+"^");
		br.write(count+"^"); 		 br.write(govContri+"^"); 		 br.write(SubContri+"^"); 		 br.write("^"); 		 
		br.write("\r\n");		 

	}




	private void getFileHeaderforfpu(PrintWriter br ) throws IOException
	{

		br.write("1"+"^");   		 br.write("FH"+"^"); 		 br.write("O"+"^"); 		 br.write("4014721"+"^");		 br.write("1"+"^");
		br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^"); 
		br.write("\r\n");

	}



	private void getBatchHeaderforfpu(PrintWriter br ,int count,String TranId,String govContri,String SubContri, String Total) throws IOException
	{


		Calendar cal = Calendar.getInstance(TimeZone.getDefault());	
		String date = "ddMMyyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(date);
		sdf.setTimeZone(TimeZone.getDefault());
		String currentdate = sdf.format(cal.getTime());



		br.write("2"+"^");  		 br.write("BH"+"^"); 		 br.write("1"+"^"); 		 br.write("R"+"^");
		br.write("4014721"+"^");  		 br.write(currentdate+"^"); 		 br.write(TranId+"^"); 
		br.write("^"); 	 br.write("1"+"^"); 		 br.write(count+"^"); 		 br.write(govContri+"^");
		br.write(SubContri+"^"); 		 br.write("^"); 		 br.write(Total+"^"); 		 br.write("\r\n");

	}




	private void getDTOHeaderforfpu( PrintWriter br ,int count,String govContri,String SubContri) throws IOException
	{

		br.write("3"+"^");		 br.write("DH"+"^"); 		 br.write("1"+"^"); 		 br.write("1"+"^"); 		 br.write("SGV148099C"+"^");
		br.write(count+"^"); 		 br.write(govContri+"^"); 		 br.write(SubContri+"^"); 		 br.write("^"); 		 
		br.write("\r\n");	 

	}




}
