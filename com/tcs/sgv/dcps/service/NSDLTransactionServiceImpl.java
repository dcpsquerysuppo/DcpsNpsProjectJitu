package com.tcs.sgv.dcps.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.AllIndiaServicesDAOImpl;
import com.tcs.sgv.dcps.dao.NSDLTransactionDAOImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.dao.PostEmpContriDAO;
import com.tcs.sgv.dcps.dao.PostEmpContriDAOImpl;
import com.tcs.sgv.dcps.dao.SancBudgetDAO;
import com.tcs.sgv.dcps.dao.SancBudgetDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;
import com.tcs.sgv.eis.valueobject.HrPayGpfBalanceDtls;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;


public class NSDLTransactionServiceImpl extends ServiceImpl 
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

	public ResultObject getAllNSDlTransaction(Map inputMap)throws Exception{
		logger.info("Inside Get getAllNSDlTransaction--------------------");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);


		List lstAisType = null;
		String aisType = null;
		//String monthid = null;
		//String yearid= null;
		//String createdfile = null;
		List lstAlIndiaSerEmp = null;
		List lstAlIndiaSerEmpChallan = null;
		List lstAlIndiaServices=null;
		try{
			setSessionInfo(inputMap);
			NSDLTransactionDAOImpl lObjAlIndSernsdl = new NSDLTransactionDAOImpl(null,serv.getSessionFactory()); 
			AllIndiaServicesDAOImpl lObjAlIndSer=new AllIndiaServicesDAOImpl(null,serv.getSessionFactory());
			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

			lstAisType  = lObjAlIndSernsdl.getAISlist();
			logger.info("Inside Get AIS types-------------"+lstAisType);

			inputMap.put("lstAisType",lstAisType);	

			//List lLstMonths = lObjAlIndSernsdl.getMonths();

			//inputMap.put("Months", lLstMonths);

			//logger.info("Months  list-------------------"+lLstMonths);

			//List lLstYears = lObjAlIndSernsdl.getFinyears();

			//inputMap.put("Year", lLstYears);

			//logger.info("Year list-------------------"+lLstYears);
			Boolean check=false;
			System.out.println("check"+check);


			if(StringUtility.getParameter("aisType", request) != null && StringUtility.getParameter("aisType", request) != "" )
			

			{	

				check=true;
				aisType =  StringUtility.getParameter("aisType", request).trim();
				//monthid = StringUtility.getParameter("monthid", request).trim();
				//yearid = StringUtility.getParameter("yearid", request).trim();
				String extn = StringUtility.getParameter("flag", request).trim();
				logger.info("Request object are----"+aisType);	

				//String lStrYearCOde = objDcpsCommonDAO.getFinYearCodeForYearId(Long.parseLong(yearid));
				lstAlIndiaSerEmp = lObjAlIndSernsdl.selectBatchIdList(aisType);
				lstAlIndiaSerEmpChallan=lObjAlIndSernsdl.selectBatchIdListChallan(aisType);
				//lstAlIndiaServices =lObjAlIndSer.getEmployeeList(aisType, monthid, yearid, lStrYearCOde);
				String status=null;
				Boolean flagg=true;
				/*if (lstAlIndiaSerEmp.size() != 0){
					status=lObjAlIndSer.getStatus(aisType,monthid,yearid);
				}*/
				logger.info("Transaction List-------------------"+lstAlIndiaSerEmp);
				/*		Long contribEmp=(Long) lstAlIndiaSerEmp.get(13);
				Long contribEmplr=(Long)lstAlIndiaSerEmp.get(14);
				Long Month=(Long)lstAlIndiaSerEmp.get(16);
				Long Year=(Long)lstAlIndiaSerEmp.get(17);*/

				System.out.println("lstAlIndiaSerEmp size  is"+lstAlIndiaSerEmp.size());
				logger.info("status-------------------"+status);
				if ((lstAlIndiaSerEmp==null || lstAlIndiaSerEmp.size() == 0) && (lstAlIndiaSerEmpChallan==null || lstAlIndiaSerEmpChallan.size() == 0))
				{
					inputMap.put("totalRecordsMstContri", 0);
					System.out.println("flagg"+flagg);
					inputMap.put("flagg", flagg);

				}
				else
				{
					inputMap.put("totalRecordsMstContri", lstAlIndiaSerEmp.size()+lstAlIndiaSerEmpChallan.size());
					flagg=false;
					System.out.println("flagg"+flagg);
					inputMap.put("flagg", flagg);
				}


				String	yearDesc=null;
				String monid=null;
				String yearDesc1=null;
				double emplyContri = 0;
				double emplyerContri = 0;
				String billNo=null;
				String trasacId=null;
                String batchIds=null;
                
				for (Iterator it = lstAlIndiaSerEmp.iterator(); it.hasNext();)				 
				{

					Object[] lObj = (Object[]) it.next(); 


					billNo = (lObj[0] != null) ? lObj[0].toString() : "";
					
					trasacId= (lObj[3] != null) ? lObj[3].toString() : "";  
					
			 	    batchIds= (lObj[2] != null) ? lObj[2].toString() : "";  
					

				}
				
				 if (lstAlIndiaSerEmp !=null && lstAlIndiaSerEmp.size() != 0)
				{
					 if (lstAlIndiaSerEmpChallan !=null && lstAlIndiaSerEmpChallan.size() != 0)
					{
						 lstAlIndiaSerEmp.addAll(lstAlIndiaSerEmpChallan);			
				     }
					 
				}
				 else if (lstAlIndiaSerEmpChallan !=null && lstAlIndiaSerEmpChallan.size() != 0)
				 {
					 lstAlIndiaSerEmp= new ArrayList();
					 lstAlIndiaSerEmp.addAll(lstAlIndiaSerEmpChallan);	
				 }


				if (lstAlIndiaSerEmp !=null && lstAlIndiaSerEmp.size() != 0){
					inputMap.put("lstAlIndiaSerEmp", lstAlIndiaSerEmp);
				}

				
				inputMap.put("aisType",aisType);

		

			}

			inputMap.put("check",check);
			/*else {
				resultObject.setViewName("ContributionList");//set view name
			}*/

			resultObject.setViewName("NSDLTranUpdate");//set view name
			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object
			//


		}catch(Exception e){
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in load NSDL Transaction List ");
			e.printStackTrace();
		}



		return resultObject;
	}

	public ResultObject checkTranNO(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;

		try {

			setSessionInfo(inputMap);
			NSDLTransactionDAOImpl lObjAlIndSer = new NSDLTransactionDAOImpl(null,serv.getSessionFactory());    	

			String tran_no = StringUtility.getParameter("tranno",request).trim();


			if (!"".equals(tran_no)) 
			{
				lBlFlag = lObjAlIndSer.checktranNO(tran_no);
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
	
	
	Long batchId=0L;
	String billno =null;
	String acMaintainedBy=null;
	String year=null;
	String  transacId=null;
	String acMaintainedBycode=null;
	
	public ResultObject insertTransactionId(Map inputMap){	
		logger.info("Inside Get insertTransactionId");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);
		String isDep="N";
		try{
			setSessionInfo(inputMap);
			//NewRegDdoDAOImpl newRegDDODaoImplObj = new NewRegDdoDAOImpl(MstEmp.class,serv.getSessionFactory());
			//NewRegTreasuryService treasuryServiceobj = new NewRegTreasuryServiceImpl();
			NSDLTransactionDAOImpl lObjAlIndSer = new NSDLTransactionDAOImpl(null,serv.getSessionFactory());
			 DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			transacId = request.getParameter("TransacId");
			 batchId = Long.parseLong(request.getParameter("batchId"));
			 logger.info("batchId is ***"+batchId);
			 billno = request.getParameter("billno").trim();
			 isDep= request.getParameter("isDep").trim();
			 acMaintainedBy= request.getParameter("acMiantainedBy").trim();
			 
			
			 
			if(acMaintainedBy.equals("A/c Maintained BY IFS"))
			{
				acMaintainedBycode="700242";
			}
			else if(acMaintainedBy.equals("A/c Maintained BY IPS"))
			{
				acMaintainedBycode="700241";
			}
			else
			{
				acMaintainedBycode="700240";
			}
			 logger.info("acMaintainedBy is ***"+acMaintainedBy);
			 logger.info("acMaintainedBycode is ***"+acMaintainedBycode);
			year=request.getParameter("year");
			 String finYrId=objDcpsCommonDAO.getFinYearIdForYearDesc(year);
			 logger.info("year is ***"+finYrId);
			 logger.info("inside UpdateTransactionId in service");
			 
			String msg = "Failed";

			
			Date current_date = new Date();
			

			if(batchId!= 0 && transacId!= null  && finYrId!= null && acMaintainedBycode!= null){
				
				lObjAlIndSer.updateTransactionIdNsdl(batchId,transacId,acMaintainedBycode,finYrId);
				if(isDep.equalsIgnoreCase("Y"))
				{
					lObjAlIndSer.updateTransactionIdTrnChallan(batchId,transacId,finYrId,billno);
				}
				else
				{
					
					lObjAlIndSer.updateTransactionIdTrn(batchId,transacId,finYrId,billno);
				}
				
				
				
				msg="Success";
			}
				

				
			StringBuffer returnMsg = new StringBuffer();
			returnMsg.append("<XMLDOC>");
			returnMsg.append("<msg>");
			returnMsg.append(msg);
			returnMsg.append("</msg>");
            returnMsg.append("</XMLDOC>");
			logger.info("msg "+msg);
			
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
	
	public ResultObject checkBillTranNO(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		Boolean lBlFlag = false;

		try {

			setSessionInfo(inputMap);
			NSDLTransactionDAOImpl lObjAlIndSer = new NSDLTransactionDAOImpl(null,serv.getSessionFactory());    	

			String tran_no = StringUtility.getParameter("tranno",request).trim();

			
			
			

			if (!"".equals(tran_no)) 
			{
				lBlFlag = lObjAlIndSer.checktranNO(tran_no);
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
	public ResultObject nsdlBillgen(Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
	

		try {
			
	       logger.info("inside nsdlBillgen service ******");
	     
			setSessionInfo(inputMap);
			  SancBudgetDAO lObjSancBudgetDAO = new SancBudgetDAOImpl(null,serv.getSessionFactory());
			PostEmpContriDAO objPostEmpContriDAO = new PostEmpContriDAOImpl(null,serv.getSessionFactory());
			NSDLTransactionDAOImpl lObjAlIndSer = new NSDLTransactionDAOImpl(null,serv.getSessionFactory());    	
			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			String batchId = StringUtility.getParameter("batchId",request).trim();
			 logger.info("batchId is ***"+batchId);
			String transacId = StringUtility.getParameter("TransacId",request).trim();
			 logger.info("TransacId is ***"+transacId);
			String acMaintainedBy = StringUtility.getParameter("acMiantainedBy",request).trim();
			 logger.info("acMiantainedBy is ***"+acMaintainedBy);
			String year = StringUtility.getParameter("year",request).trim();
			 logger.info("year is ***"+year);
			String BillNo = StringUtility.getParameter("BillNo",request).trim();
			 logger.info("BillNo is ***"+BillNo);
			String flag = StringUtility.getParameter("flag",request).trim();
			String isDep=StringUtility.getParameter("isDep",request).trim();
			 logger.info("flag is ***"+flag);
             Boolean status=false;
			String lStrCurrDate = null;
			Object[] tuple = null;
			
			Long billAmt=0L;
			Long lLongExpenditure=0L;
		
			
			Date lDtCurrdate = SessionHelper.getCurDate();
			//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String timeStamp =  new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			String acMaintainedBycode=null;
			if (lDtCurrdate != null) {
				lStrCurrDate = timeStamp;
			}
			

			if(acMaintainedBy.equals("A/c Maintained BY IFS"))
			{
				acMaintainedBycode="700242";
			}
			else if(acMaintainedBy.equals("A/c Maintained BY IPS"))
			{
				acMaintainedBycode="700241";
			}
			else
			{
				acMaintainedBycode="700240";
			}
		 
			
			String finYrId=objDcpsCommonDAO.getFinYearIdForYearDesc(year);
			 logger.info("year is ***"+finYrId);
			 
			 
			 if(isDep!=null && "Y".equalsIgnoreCase(isDep))
			 {
				 billAmt=lObjAlIndSer.CheckBillAmtforbillnoChallan(acMaintainedBycode,Long.parseLong(finYrId));
				 if(billAmt!=null || billAmt!=0){
					   lLongExpenditure=lObjAlIndSer.selectBillAmtforbillnoChallan(acMaintainedBycode,BillNo,Long.parseLong(finYrId));
					   lLongExpenditure=lLongExpenditure+billAmt;
					 }
					
				
					 else{
					 lLongExpenditure=lObjAlIndSer.selectBillAmtforbillnoChallan(acMaintainedBycode,BillNo,Long.parseLong(finYrId));
					 }
				 
				 status=lObjAlIndSer.checkbillgenChallan(Long.parseLong(batchId),transacId,BillNo);
				 logger.info("status is ***"+status);
					if(status==false){
					lObjAlIndSer.updateBillGenChallan(Long.parseLong(batchId),transacId,BillNo);
					}
			 }
			 else
			 {
				 billAmt=lObjAlIndSer.CheckBillAmtforbillno(acMaintainedBycode,Long.parseLong(finYrId));
				 if(billAmt!=null || billAmt!=0){
					   lLongExpenditure=lObjAlIndSer.selectBillAmtforbillno(acMaintainedBycode,BillNo,Long.parseLong(finYrId));
					   lLongExpenditure=lLongExpenditure+billAmt;
					 }
					
				
					 else{
					 lLongExpenditure=lObjAlIndSer.selectBillAmtforbillno(acMaintainedBycode,BillNo,Long.parseLong(finYrId));
					 }
				 
				 status=lObjAlIndSer.checkbillgen(Long.parseLong(batchId),transacId,BillNo);
				 logger.info("status is ***"+status);
					if(status==false){
					lObjAlIndSer.updateBillGen(Long.parseLong(batchId),transacId,BillNo);
					}
			 }
			  
			  logger.error("Previous billAmt is *****" + billAmt);
			 
			 
			 String lStrExpenditureInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(lLongExpenditure));
			 
			 
			
			
			 
			
		
			 
				
				
				
				
				
				String dcpsSancBudgetOrgId=null;
				
				 if(acMaintainedBycode!="" && !acMaintainedBycode.equals("") && Long.parseLong(acMaintainedBycode)==700240) 
					 {
						 dcpsSancBudgetOrgId="991000016";
					 }	 
					 else if(acMaintainedBycode!="" && !acMaintainedBycode.equals("") && Long.parseLong(acMaintainedBycode)==700241)	
					 {
						   dcpsSancBudgetOrgId="991000017";
					 }
					 else if(acMaintainedBycode!="" && !acMaintainedBycode.equals("") && Long.parseLong(acMaintainedBycode)==700242)	
					 {
						 dcpsSancBudgetOrgId="991000018";
					 }
						
					 else
					 {
						 dcpsSancBudgetOrgId="991000015";
					 }
					
				
				 Long lLongSancBudget = lObjSancBudgetDAO.getTotalBudget(Long.parseLong(finYrId),dcpsSancBudgetOrgId);
				 String lStrSancBudgetInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(lLongSancBudget));
				 
				
				
				 Long lLongBalance = lLongSancBudget - lLongExpenditure;
				 String lStrBalanceInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(lLongBalance));
				 Double lLongContribution=0d;
				 List dateMonyr=null;
				 if(isDep!=null && "Y".equalsIgnoreCase(isDep))
				 {
					 lLongContribution=lObjAlIndSer.selectTotalContrperbillnoChallan(Long.parseLong(batchId),BillNo);
					  dateMonyr=lObjAlIndSer.selectDatemonyrforbillnoChallan(Long.parseLong(batchId),BillNo);
				 }
				 else
				 {
					 lLongContribution=lObjAlIndSer.selectTotalContrperbillno(Long.parseLong(batchId),BillNo);
					  dateMonyr=lObjAlIndSer.selectDatemonyrforbillno(Long.parseLong(batchId),BillNo);
				 }
				 
				 
				 String lStrContributionInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(lLongContribution));
				 
				 String accMainName=objPostEmpContriDAO.accMain(acMaintainedBycode);
				
				
				 
				 String lStrEmplrSchemeCodee = lObjSancBudgetDAO.getSchemeCodeForOrgId(Long.parseLong(dcpsSancBudgetOrgId));
				 
				 
				 logger.info("dateMonyr****size is**"+dateMonyr.size());
				 String Date=null;
				 long monthid=0L;
				 long yearcode=0L;
				 
				 
				 
				 if (dateMonyr != null && !dateMonyr.isEmpty())
					 
				 {
					 Iterator it = dateMonyr.iterator();
					 
					 while (it.hasNext())
						 
					 {
						tuple = (Object[]) it.next();
						
						Date=tuple[0].toString();
						monthid=Long.parseLong(tuple[1].toString());
						 logger.info("monthid**** is**"+monthid);
						yearcode=Long.parseLong(tuple[2].toString());
						 logger.info("yearcode**** is**"+yearcode);
					}
						
				 
				 
				 } 
			 
			inputMap.put("batchId", batchId);
			inputMap.put("TransacId", transacId);
			inputMap.put("acMaintainedBycode",acMaintainedBycode);
			inputMap.put("finYrId", finYrId);
			inputMap.put("BillNo", BillNo);
			inputMap.put("flag", flag);
			inputMap.put("lLongSancBudget", lLongSancBudget);
			inputMap.put("lLongExpenditure", lLongExpenditure);
			inputMap.put("lLongBalance", lLongBalance);
			inputMap.put("lLongContribution",lLongContribution);
			inputMap.put("accMainName",accMainName);
			inputMap.put("lStrSancBudgetInWords",lStrSancBudgetInWords);
			inputMap.put("lStrExpenditureInWords",lStrExpenditureInWords);
			inputMap.put("lStrBalanceInWords",lStrBalanceInWords);
			inputMap.put("lStrContributionInWords",lStrContributionInWords);
			inputMap.put("Date",Date);
			inputMap.put("monthid",monthid);
			inputMap.put("yearcode",yearcode);
			inputMap.put("lStrEmplrSchemeCodee",lStrEmplrSchemeCodee);
			
			
			resObj.setViewName("NSDLBillGen");
			
			
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
	
	
}
