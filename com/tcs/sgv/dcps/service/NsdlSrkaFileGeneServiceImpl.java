package com.tcs.sgv.dcps.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NsdlSrkaFileGeneDAOImpl;


public class NsdlSrkaFileGeneServiceImpl extends ServiceImpl 
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

	//Added By Ashish for 
	public ResultObject getSRKAEmpsContri(Map inputMap)throws Exception{
		logger.info("Inside Get getAISEmpsContri--------------------");
		ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);


		List lstAisType = null;
		String aisType = null;
		String finType=null;
		String billno = null;
		List lstYear=null;
		String treasuryyno=null;
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
			NsdlSrkaFileGeneDAOImpl lObjAlIndSer = new NsdlSrkaFileGeneDAOImpl(null,serv.getSessionFactory()); 
			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

			//	lstAisType  = lObjAlIndSer.getAISlist();
			lstYear=lObjAlIndSer.getFinyeardesc();
			List treasury=lObjAlIndSer.getAllTreasuries();
			
			//	inputMap.put("lstAisType",lstAisType);	
			inputMap.put("lstYear",lstYear);	
			inputMap.put("treasury",treasury);	
			Boolean check=false;
			System.out.println("check"+check);
			String treasuryno=null;
			if(StringUtility.getParameter("fintype", request) != null  && StringUtility.getParameter("fintype", request) != ""
			&& StringUtility.getParameter("treasno", request) != null  && StringUtility.getParameter("treasno", request) != "")
			{
				
				
				//aisTypeSelected=StringUtility.getParameter("aisType", request);
				finTypeSelected=StringUtility.getParameter("fintype", request);
				treasuryno=StringUtility.getParameter("treasno", request);
				//	logger.info("aisTypeSelected-------------------"+aisTypeSelected);
				
				//	lstbillNo = lObjAlIndSer.selectBillNo(aisTypeSelected,finTypeSelected);
				//	logger.info("lstbillNo-------------------"+lstbillNo.size());
				//	inputMap.put("lstbillNo", lstbillNo);
				//	inputMap.put("aisTypeSelected",aisTypeSelected);	
				inputMap.put("finTypeSelected",finTypeSelected);	
					inputMap.put("treasuryno",treasuryno);

				//inputMap.put("billSel", lstbillNo);
			}

			//logger.info("aisTypeSelected-------------------"+aisTypeSelected);

			if(StringUtility.getParameter("fintype", request) != null  && StringUtility.getParameter("fintype", request) != ""
				&& StringUtility.getParameter("treasno", request) != null  && StringUtility.getParameter("treasno", request) != "")

			{	
				check=true;
				//aisType =  StringUtility.getParameter("aisType", request).trim();
				finType=StringUtility.getParameter("fintype", request).trim();
				treasuryyno=StringUtility.getParameter("treasno", request).trim();
				//	logger.info("aisTypeSelected-------------------"+aisType);
				
				//inputMap.put("aisTypeSelected",aisType);	
				inputMap.put("finTypeSelected",finType);
				//billno = StringUtility.getParameter("billno", request).trim();
				//inputMap.put("billno",billno);
				String extn = StringUtility.getParameter("flag", request).trim();
				//	logger.info("Request object are----"+aisType+"----"+finType+"---"+billno+"----"+" extn "+extn);	

				List lFromToDate=lObjAlIndSer.selectFromToDate(finType);
				for (int liCtr = 0; liCtr < lFromToDate.size(); liCtr++) {
					obj1 = (Object[]) lFromToDate.get(liCtr);
					fromDate=obj1[0].toString();
					toDate=obj1[1].toString();
				}

				String finYr=lObjAlIndSer.getFinyrdesc(Long.parseLong(finType));
				lstAlIndiaSerEmp = lObjAlIndSer.getEmployeeList(finYr,treasuryyno);

				Boolean flagg=true;

				

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
				Long emplyContri = 0l;
				Long emplyerContri = 0l;
				Double intEmpTotal=0.0;
				Double intEmplrTotal=0.0;
				Double intOpenEmp=0d;
				Double intOpenEmplr=0d;
				String [] intEmpnsdl=null;
				String intFinalEmplrTotal=null;
				for (Iterator it = lstAlIndiaSerEmp.iterator(); it.hasNext();)				 
				{

					Object[] lObj = (Object[]) it.next();
					emplyContri = emplyContri + Long.parseLong(lObj[3] != null ? lObj[3].toString() : "0");
					emplyerContri = emplyerContri + Long.parseLong(lObj[4] != null ? lObj[4].toString() : "0");
					intEmpTotal=intEmpTotal+Double.parseDouble(lObj[5] != null ? lObj[5].toString() : "0");
					intEmplrTotal=intEmplrTotal+Double.parseDouble(lObj[5] != null ? lObj[6].toString() : "0");
					intOpenEmp= intOpenEmp + Double.parseDouble(lObj[10] != null ? lObj[10].toString() : "0");
					intOpenEmplr= intOpenEmplr + Double.parseDouble(lObj[11] != null ? lObj[11].toString() : "0");
				}
				/*String lstIntemployee=lObjAlIndSer.getEmployeeIntList(finYr,treasuryyno);
				if(lstIntemployee!=null)			 
				{

					
					intEmpTotal =Double.parseDouble(lstIntemployee);
					intEmplrTotal =  Double.parseDouble(lstIntemployee);
				
				}*/
				
				
				
				
				Object[] obj = new Object[18];
				obj[0] =  "Total";
				obj[3] =  round(emplyContri);
				obj[4] =  round(emplyerContri);
				obj[5] =  intEmpTotal;
				obj[6] =  intEmplrTotal;
				obj[10] =  round(intOpenEmp);
				obj[11] =  round(intOpenEmplr);
				
				lstAlIndiaSerEmp.add(obj);

				if (lstAlIndiaSerEmp.size() != 0){
					inputMap.put("lstAlIndiaSerEmp", lstAlIndiaSerEmp);
					System.out.println("lstAlIndiaSerEmp"+lstAlIndiaSerEmp.get(0).toString());
				}

			}

			inputMap.put("check",check);

			resultObject.setViewName("ContributionListSRKA");//set view name
			resultObject.setResultCode(ErrorConstants.SUCCESS);
			resultObject.setResultValue(inputMap);//put in result object

		  }catch(Exception e){
			resultObject = new ResultObject(ErrorConstants.ERROR);
			resultObject.setResultCode(-1);
			resultObject.setViewName("errorPage");
			logger.error("Error in load employee lists of SRKA on getSRKAEmpsContri () "+ e);
			e.printStackTrace();
		}



		return resultObject;
	}

	public static double round(double d) {
	    BigDecimal bd = new BigDecimal(d);
	    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}

	@SuppressWarnings("null")


	public ResultObject createFilesForSRKANSDL(
			Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		logger.info("in createTxtFile---------------------- ");

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
	    String extn=null;
		String aisType=null;
	

		StringBuilder Strbr = new StringBuilder();
		StringBuilder Strbr1 = new StringBuilder();
		String extnFlag=null;
		
	
		Object obj1[];
		String fromDate=null;
		String toDate=null;
		String dtoRegNo=null;
		String ddoRegNo=null;
		String prvDdoReg="AAA";
		String treasuryyno=null;
		try {

			setSessionInfo(inputMap);
			/*	HttpServletRequest request = (HttpServletRequest) inputMap
			.get("requestObj");
			 */
			   treasuryyno=StringUtility.getParameter("treasno", request).trim();
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

			if(StringUtility.getParameter("fintype", request) != null  && StringUtility.getParameter("finType", request) != null)
			
	
			{	
				extn = StringUtility.getParameter("flag", request).trim();
				extnFlag = StringUtility.getParameter("flagFile", request).trim();
			}
			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
			String finType=StringUtility.getParameter("FINtype", request).trim();
		    treasuryyno=StringUtility.getParameter("treasno", request).trim();
			NsdlSrkaFileGeneDAOImpl lObjAlIndSer = new NsdlSrkaFileGeneDAOImpl(null,serv.getSessionFactory());
			
			Object[] lyrObj = null;
			List lFromToDate=lObjAlIndSer.selectFromToDate(finType);
			String finYr=lObjAlIndSer.getFinyrdesc(Long.parseLong(finType));
			String [] finyr=finYr.split("-");
			String finyr1=finyr[0];
			String finyr12=finyr[1];

			for (int liCtr = 0; liCtr < lFromToDate.size(); liCtr++) {
				obj1 = (Object[]) lFromToDate.get(liCtr);
				fromDate=obj1[0].toString();
				toDate=obj1[1].toString();
			}



			lstemployee=lObjAlIndSer.getEmployeeListNsdl(finYr,treasuryyno);
			//Long countReg=lObjAlIndSer.getDDoRegCount(finYr,treasuryyno);
			//---------- Added by Ashish--------


			//boolean Batch=false;
			List nsdlData=null;
			String BatchId=null;
			Long nwbatchId=null;
			String nwTranBatchId=null;
			String [] fyYrsplit=null;
			String tranId=null;
		
			//List lBatchTransId=null;
		/*	nsdlData=lObjAlIndSer.selectDataForNSDLGen(finYr,treasuryyno);
		
			   if(lstemployee.size()!=0 && nsdlData!=null && nsdlData.size()>0){
				  
				   BatchId= lObjAlIndSer.getbatchIdForNsdl(finYr,treasuryyno);
				   if(BatchId==null && BatchId.equals(""))
					   nwTranBatchId= lObjAlIndSer.getTranbatchIdForNsdl(finYr,treasuryyno);
				   if(nwTranBatchId!=null && !nwTranBatchId.equals("")){
					   nwbatchId=Long.parseLong(nwTranBatchId)+1;
					   BatchId=nwbatchId.toString();
					   lObjAlIndSer.insertDataForNSDLRepo(BatchId,finYr,treasuryyno);
				   }
			   
				   }*/
				  
			
			   if(lstemployee.size()!=0){
				// BatchId = lObjAlIndSer.getNextSeqNum("MST_NSDL_SRKA_GEN");
				  
				   //BatchId=finyr1+finyr12+treasuryyno+10;
				   BatchId=finyr1+(finyr12.substring(1, 4))+treasuryyno+12;
			    lObjAlIndSer.insertDataForNSDLRepo(BatchId,finYr,treasuryyno);
			    
				 
					 nsdlData=lObjAlIndSer.selectDataForNSDLGen(finYr,treasuryyno);
					 if(nsdlData!=null && nsdlData.size()>0)
					 {
						 lObjAlIndSer.updateDataForNSDLRepo(BatchId,finYr,treasuryyno);
					 }
					 else
					 {
						 lObjAlIndSer.insertDataForNSDLRepo(BatchId,finYr,treasuryyno);
					 }
					 
					 
			    /*Long dcpsbatchId=BatchId+1;
				lObjAlIndSer.updateGeneratedId(Long.valueOf(dcpsbatchId.toString()),"MST_NSDL_SRKA_GEN");*/
			}
			


			logger.info("BatchId Is **********"+BatchId);
			//---------- ended by Ashish--------




			int totalsize = lstemployee.size();

			if (lstemployee != null && !lstemployee.isEmpty()) 

			{			

				int count = 0;	
				int  i = 2;
				int j=0;
				int empCount=1;
				
				String lEmpTotalContri=lObjAlIndSer.getEmployeeContriTotalList(finYr,treasuryyno);
				String lEmpTotalContriInt=lObjAlIndSer.getEmployeeContriTotalListInterest(finYr,treasuryyno);
				String lEmpTotalOpenContriInt="0.0";
				String lEmpyerTotalOpenContriInt=lObjAlIndSer.getEmployerContriOpenTotalList(finYr,treasuryyno);
				
				
				lEmpTotalOpenContriInt=lEmpyerTotalOpenContriInt;
				
				HashMap ddoWiseCntMap=lObjAlIndSer.getEmployeeRecordCountDdoregNsdlMap(finYr,treasuryyno);
				HashMap ddoWiseEmpContribMap=lObjAlIndSer.getEmployeeListDdoregNsdlMap(finYr,treasuryyno);
				HashMap ddoWiseEmpIntMap=lObjAlIndSer.getEmployeeListDdoregNsdlIntMap(finYr,treasuryyno);
				HashMap ddoWiseOpenIntMap=lObjAlIndSer.getEmployerOpenIntListDdoregNsdlMap(finYr,treasuryyno);
				
				
				if(lEmpTotalContriInt==null)
				{
					lEmpTotalContriInt="0.0";
				}
				if(lEmpTotalContri==null)
				{
					lEmpTotalContri="0.0";
				}
				if(lEmpTotalOpenContriInt==null)
				{
					lEmpTotalOpenContriInt="0.0";
				}
				if(lEmpyerTotalOpenContriInt==null)
				{
					lEmpyerTotalOpenContriInt="0.0";
				}
				String totalEmplyContri=null;
				Double EmpleeContri=null;
				String totalEmplyerContri=null;
				Double	 totalEContri=null;
				String intEmpl=null;
				String intEmplr=null;
				String openIntEmpl=null;
				String openIntEmplr=null;
				String [] intEmp=null;
				String [] intOpenEmp=null;
				String [] intEmployee=null;
				String [] intOpenEmployee=null;
				String [] intEmployer=null;
				String [] intOpenEmployer=null;
				String [] contriEmployee=null;
				String [] emplrContriintEmployer=null;
				String [] totalDhsumsplit=null;
				Double totalContribution=null;
				Double GovContributionSum=null;
				Double subcontributionSum=null;
				Double TotalContributionsum=null;
				Double totalEmpContribution=0.0;
				Double totalEmplrContribution=0.0;
				String [] overallAmt=null;
				String [] totalempoverallAmt=null;
				String [] totalemplroverallAmt=null;
			//	Map<String, Long> lMapEmpeeCountDtls = null;
				if(Double.parseDouble(lEmpTotalOpenContriInt) > Double.parseDouble(lEmpyerTotalOpenContriInt))
				{
					lEmpTotalOpenContriInt=lEmpyerTotalOpenContriInt;
				}
				if(lEmpTotalContri!=null && lEmpTotalContriInt!=null && lEmpTotalOpenContriInt!=null)			 
				{

					EmpleeContri=Double.parseDouble(lEmpTotalContri)+Double.parseDouble(lEmpTotalContriInt)+Double.parseDouble(lEmpTotalOpenContriInt);
					totalEmplyContri=nosci(EmpleeContri);
					logger.info("totalEmplyContri*********"+totalEmplyContri);
					//totalEmplyerContri = totalEmplyContri;
					//totalEContri = Double.parseDouble(totalEmplyerContri)*2;
					
				}
				if(lEmpTotalContri!=null && lEmpTotalContriInt!=null && lEmpyerTotalOpenContriInt!=null)			 
				{

					EmpleeContri=Double.parseDouble(lEmpTotalContri)+Double.parseDouble(lEmpTotalContriInt)+Double.parseDouble(lEmpyerTotalOpenContriInt);
					totalEmplyerContri=nosci(EmpleeContri);
					logger.info("totalEmplyContri*********"+totalEmplyerContri);
					
					//totalEContri = Double.parseDouble(totalEmplyerContri)*2;
					
				}
				
				totalEContri=Double.parseDouble(totalEmplyerContri)+Double.parseDouble(totalEmplyContri);
			//total
				String totaloverallAmt=nosci(totalEContri);
				
				overallAmt=totaloverallAmt.toString().split("\\.");
				
				
				 if(totaloverallAmt.equals("0")){
					 totaloverallAmt="0.00";
				}
				
				 else if(overallAmt[1].length()==1)
				{
					 totaloverallAmt=totaloverallAmt+"0";
				}
				 
				else if(overallAmt[1].length()>2)
					{
					 totaloverallAmt= decRoundOff(totaloverallAmt);
					 
					}
				 
				
			//emp emplr	 
				 
				 
				 totalempoverallAmt=totalEmplyContri.toString().split("\\.");
					
					
				 if(totalEmplyContri.equals("0")){
					 totalEmplyContri="0.00";
				}
				
				 else if(totalempoverallAmt[1].length()==1)
				{
					 totalEmplyContri=totalEmplyContri+"0";
				}
				 
				 else if(overallAmt[1].length()>2)
					{
					
					totalEmplyContri=  decRoundOff(totalEmplyContri);
					 
					}
				 
				 
				 totalemplroverallAmt=totalEmplyerContri.toString().split("\\.");
					
					
				 if(totalEmplyerContri.equals("0")){
					 totalEmplyerContri="0.00";
				}
				
				 else if(totalemplroverallAmt[1].length()==1)
				{
					 totalEmplyerContri=totalEmplyerContri+"0";
				}
				 else if(overallAmt[1].length()>2)
					{
					
					 totalEmplyerContri=  decRoundOff(totalEmplyerContri);
					}
				 
				
				int temp=0;
				int emprecCount=0;
				//String[] lEmpTotalContrDdo=lObjAlIndSer.getEmployeeListDdoregNsdl(finYr,treasuryyno);
				//String[] lEmpCountContrDdo=lObjAlIndSer.getEmployeeCountDdoregNsdl(finYr,treasuryyno);
				
				 
				//lMapEmpeeCountDtls=lObjAlIndSer.getEmpCountZeroDdoregNsdl(finYr);
				//logger.info("lMapEmpeeCountDtls size is *********"+lMapEmpeeCountDtls.size());
				
				PrintWriter outputfile =response.getWriter();
				boolean isContriZero=false;
				boolean isInterestZero=false;
				boolean isOpenInterestZero=false;
				int ddoCount=0;
				int totalContriCount=0;
				for (Iterator it = lstemployee.iterator(); it.hasNext();)				 
				{
					count++;
					Object[] lObj = (Object[]) it.next();
						

					empname = (lObj[0] != null) ? lObj[0].toString(): "NA";


					dcpsid = ((lObj[1] != null)? lObj[1].toString() :"");


					pranno = ((lObj[2] != null)? lObj[2].toString() :"");

					dtoRegNo = (lObj[8] != null) ? lObj[8].toString() : "";						

					ddoRegNo = (lObj[9] != null) ? lObj[9].toString() : "";		

					govEmpContri = (lObj[3] != null) ? lObj[3].toString() : "";		
					logger.info("govEmpContri*****************"+govEmpContri);
					subempContri = (lObj[4] != null) ? lObj[4].toString() : "";	
					
					//subempContri = (Float) ((lObj[14] != null)? Float.parseFloat( new DecimalFormat("##.##").format(Float.parseFloat(lObj[14].toString()))):new Float(new DecimalFormat("##.##").format(1000.00)));

					intEmpl = (lObj[5] != null) ? lObj[5].toString() : "";		
					
					intEmplr = (lObj[6] != null) ? lObj[6].toString() : "";	
					
					openIntEmpl = (lObj[10] != null) ? lObj[10].toString() : "";		
					
					openIntEmplr = (lObj[11] != null) ? lObj[11].toString() : "";	
				
					if( Double.parseDouble(openIntEmpl) > Double.parseDouble(openIntEmplr))
					{
						openIntEmpl=openIntEmplr;
					}
					
					// for Opening String TotalIntContri=null;
					String TotalIntOpenContri=null;
					Double totalIntOpenContribution =  Double.parseDouble(openIntEmpl) + Double.parseDouble(openIntEmplr);
					//Double totalContribution = Double.parseDouble(govEmpContri) +Double.parseDouble(subempContri);
					TotalIntOpenContri=totalIntOpenContribution.toString();

					intOpenEmp=TotalIntOpenContri.toString().split("\\.");
					
					
					 if(TotalIntOpenContri.equals("0")){
						 TotalIntOpenContri="0.00";
					}
					
					 else if(intOpenEmp[1].length()==1)
					{
						 TotalIntOpenContri=TotalIntOpenContri+"0";
					}
					 
					 
						intOpenEmployee=openIntEmpl.toString().split("\\.");
						if(openIntEmpl!=null && !"".equalsIgnoreCase(openIntEmpl))
						{
							if(Double.parseDouble(openIntEmpl) > 0)
							{
								isOpenInterestZero=false;
							}
							else
							{
								isOpenInterestZero=true;
							}
						}
						
						 if(openIntEmpl.equals("0")){
							 openIntEmpl="0.00";
						}
						
						 else if( intOpenEmployee.length>1)
							{
							 if(intOpenEmployee[1].length()==1)
								 openIntEmpl=openIntEmpl+"0";
							
							}
						 else	if(intOpenEmployee.length==1)
							{
							 openIntEmpl=openIntEmpl+".00";
							}
						 
						 
						 intOpenEmployer=openIntEmplr.toString().split("\\.");
							if(openIntEmplr!=null && !"".equalsIgnoreCase(openIntEmplr))
							{
								if(Double.parseDouble(openIntEmplr) > 0)
								{
									isOpenInterestZero=false;
								}
								else
								{
									isOpenInterestZero=true;
								}
							}
							
							 if(openIntEmplr.equals("0")){
								 openIntEmplr="0.00";
							}
							 else	if( intOpenEmployer.length>1)
								{
								 if(intOpenEmployer[1].length()==1)
									 openIntEmplr=openIntEmplr+"0";
								}

							 else	if(intOpenEmployer.length==1)
								{
								 openIntEmplr=openIntEmplr+".00";
								}
							 
				
					// For normal
					String TotalIntContri=null;
					
					Double totalIntContribution =  Double.parseDouble(intEmpl) + Double.parseDouble(intEmplr);
					//Double totalContribution = Double.parseDouble(govEmpContri) +Double.parseDouble(subempContri);
					TotalIntContri=totalIntContribution.toString();

					intEmp=TotalIntContri.toString().split("\\.");
					
					
					 if(TotalIntContri.equals("0")){
						TotalIntContri="0.00";
					}
					
					 else if(intEmp[1].length()==1)
					{
						TotalIntContri=TotalIntContri+"0";
					}
					
					
					
					
					intEmployee=intEmpl.toString().split("\\.");
					if(intEmpl!=null && !"".equalsIgnoreCase(intEmpl))
					{
						if(Double.parseDouble(intEmpl) > 0)
						{
							isInterestZero=false;
						}
						else
						{
							isInterestZero=true;
						}
					}
					
					 if(intEmpl.equals("0")){
						intEmpl="0.00";
					}
					
					 else if( intEmployee.length>1)
						{
						 if(intEmployee[1].length()==1)
							intEmpl=intEmpl+"0";
						
						}
					 else	if(intEmployee.length==1)
						{
						 intEmpl=intEmpl+".00";
						}
					
					
					
					intEmployer=intEmplr.toString().split("\\.");
					if(intEmplr!=null && !"".equalsIgnoreCase(intEmplr))
					{
						if(Double.parseDouble(intEmplr) > 0)
						{
							isInterestZero=false;
						}
						else
						{
							isInterestZero=true;
						}
					}
					
					 if(intEmplr.equals("0")){
						intEmplr="0.00";
					}
					 else	if( intEmployer.length>1)
						{
						 if(intEmployer[1].length()==1)
							intEmplr=intEmplr+"0";
						}

					 else	if(intEmployer.length==1)
						{
							intEmplr=intEmplr+".00";
						}
					 
					
					 
				//gov	 
					 
					 contriEmployee=govEmpContri.toString().split("\\.");
					 if(govEmpContri!=null && !"".equalsIgnoreCase(govEmpContri))
						{
							if(Double.parseDouble(govEmpContri) > 0 || Double.parseDouble(govEmpContri) <0)
							{
								isContriZero=false;
							}
							else
							{
								isContriZero=true;
							}
						}
						
					 if(govEmpContri.equals("0")){
						 govEmpContri="0.00";
					}
					 else	if( contriEmployee.length>1)
						{
						 if(contriEmployee[1].length()==1)
							 govEmpContri=govEmpContri+"0";
						}

					 else	if(contriEmployee.length==1)
						{
						 govEmpContri=govEmpContri+".00";
						}
					 
					//sub 
					 
					 emplrContriintEmployer=subempContri.toString().split("\\.");
					 if(subempContri!=null && !"".equalsIgnoreCase(subempContri))
						{
							if(Double.parseDouble(subempContri) > 0 || Double.parseDouble(subempContri) <0)
							{
								isContriZero=false;
							}
							else
							{
								isContriZero=true;
							}
						}
						
					 if(subempContri.equals("0")){
						 subempContri="0.00";
					}
					 else	if( emplrContriintEmployer.length>1)
						{
						 if(emplrContriintEmployer[1].length()==1)
							 subempContri=subempContri+"0";
						}

					 else	if(emplrContriintEmployer.length==1)
						{
						 subempContri=subempContri+".00";
						}
					 
					 
					 
					 
					totalContribution = (govEmpContri != null) ? Double.parseDouble(govEmpContri) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri) ;
				
					TotalContri=totalContribution.toString();


					countsum = countsum + count;


					 GovContributionSum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(govEmpContri) : 0.00 + Double.parseDouble(govEmpContri);
					
					govcontiSum=GovContributionSum.toString();

					 subcontributionSum = (subcontiSum != null) ? Double.parseDouble(subcontiSum) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri);
					//Double	subcontributionSum =  Double.parseDouble(subcontiSum) + Double.parseDouble(subempContri);
					subcontiSum=subcontributionSum.toString();

					 TotalContributionsum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(subcontiSum) : 0.00 +Double.parseDouble(subcontiSum);
					//Double TotalContributionsum = Double.parseDouble(govcontiSum) + Double.parseDouble(subcontiSum);
					TotalContrisum=TotalContributionsum.toString();

					//totalEmpContribution = totalEmpContribution + Double.parseDouble(govEmpContri)+ Double.parseDouble(intEmpl);
					//totalEmplrContribution = totalEmplrContribution +Double.parseDouble(subempContri)+Double.parseDouble(intEmplr);
					
					
					if((!prvDdoReg.equals(ddoRegNo) && !isContriZero) || (!prvDdoReg.equals(ddoRegNo) && !isInterestZero) || (!prvDdoReg.equals(ddoRegNo) && !isOpenInterestZero)){
						
						i=i+1;
						j=j+1;
						empCount=1;
						ddoCount=ddoCount+1;
					Strbr.append(i+"^");		
					Strbr.append("DH"+"^"); 	
					Strbr.append("1"+"^"); 	
					Strbr.append(j+"^"); 	
					Strbr.append(ddoRegNo+"^");
					//String lEmpRowContrDdo=lObjAlIndSer.getEmployeeRecordCountDdoregNsdl(finYr,treasuryyno,ddoRegNo);
					String lEmpRowContrDdo="0";
					
					if(ddoWiseCntMap!=null && ddoWiseCntMap.containsKey(ddoRegNo))
					{
						lEmpRowContrDdo=ddoWiseCntMap.get(ddoRegNo).toString();
						
					}
					Long rowCnt=0l;
					if(lEmpRowContrDdo!=null)
					{
						 rowCnt=Long.parseLong(lEmpRowContrDdo);
						 
					}
					//Long EmpCount=Long.parseLong(lEmpCountContrDdo[emprecCount]);
					/*if(lMapEmpeeCountDtls.get(ddoRegNo)!=null){
						logger.info("inside if********ddo reg **"+lMapEmpeeCountDtls.get(ddoRegNo));
					EmpCount=EmpCount*2- lMapEmpeeCountDtls.get(ddoRegNo);
					}
					else{*/
						//EmpCount=EmpCount*2;
				//	}
					Strbr.append(rowCnt+"^"); 
					
				//String lEmpTotalContrDdo=lObjAlIndSer.getEmployeeListDdoregNsdl(finYr,treasuryyno,ddoRegNo);
				//String lEmpTotalContrDdoInt=lObjAlIndSer.getEmployeeListDdoregNsdlInt(finYr,treasuryyno,ddoRegNo);
				//String lEmpTotalOpenContrDdo=lObjAlIndSer.getEmployeeOpenIntListDdoregNsdl(finYr,treasuryyno,ddoRegNo);
				//String lEmplrTotalOpenContrDdo=lObjAlIndSer.getEmployerOpenIntListDdoregNsdl(finYr,treasuryyno,ddoRegNo);
				String lEmpTotalContrDdo="0";
				String lEmpTotalContrDdoInt="0";
				String lEmpTotalOpenContrDdo="0";
				String lEmplrTotalOpenContrDdo="0";
				
				if(ddoWiseEmpContribMap!=null && ddoWiseEmpContribMap.containsKey(ddoRegNo))
				{
					lEmpTotalContrDdo=ddoWiseEmpContribMap.get(ddoRegNo).toString();
				}
				if(ddoWiseEmpIntMap!=null && ddoWiseEmpIntMap.containsKey(ddoRegNo))
				{
					lEmpTotalContrDdoInt=ddoWiseEmpIntMap.get(ddoRegNo).toString();
				}
				if(ddoWiseOpenIntMap!=null && ddoWiseOpenIntMap.containsKey(ddoRegNo))
				{
					lEmpTotalOpenContrDdo=ddoWiseOpenIntMap.get(ddoRegNo).toString();
					lEmplrTotalOpenContrDdo=ddoWiseOpenIntMap.get(ddoRegNo).toString();
				}
					
				if(lEmpTotalContrDdo==null)
				{
					lEmpTotalContrDdo="0.0";
				}
				if(lEmpTotalContrDdoInt==null)
				{
					lEmpTotalContrDdoInt="0.0";
				}
				if(lEmpTotalOpenContrDdo==null)
				{
					lEmpTotalOpenContrDdo="0.0";
				}
				if(lEmplrTotalOpenContrDdo==null)
				{
					lEmplrTotalOpenContrDdo="0.0";
				}
				
				if(Double.parseDouble(lEmpTotalOpenContrDdo) > Double.parseDouble(lEmplrTotalOpenContrDdo))
				{
					lEmpTotalOpenContrDdo=lEmplrTotalOpenContrDdo;
				}
				
				
				String totalDhsum="";
				if(lEmpTotalContrDdo!=null && lEmpTotalContrDdoInt!=null && lEmpTotalOpenContrDdo!=null)
				{
					Double total=Double.parseDouble(lEmpTotalContrDdo) + Double.parseDouble(lEmpTotalContrDdoInt)+Double.parseDouble(lEmpTotalOpenContrDdo);
					lEmpTotalContrDdo=total.toString();
					 totalDhsum=lEmpTotalContrDdo;
				}
				 
				
				
				
				  if (totalDhsum!=null)
				  {
					  totalDhsumsplit=totalDhsum.toString().split("\\.");
						
						 if(totalDhsum.equals("0")){
							 totalDhsum="0.00";
						}
						 else	if( totalDhsumsplit.length>1)
							{
							 if(totalDhsumsplit[1].length()==1)
								 totalDhsum=totalDhsum+"0";
							}

						 else	if(totalDhsumsplit.length==1)
							{
							 totalDhsum=totalDhsum+".00";
							}
				  }
				  else
				  {
					  totalDhsum="0";
				  }
					
				  
					 
					
					 String temp1=nosci(Double.parseDouble(totalDhsum));
					 String temp2=nosci(Double.parseDouble(totalDhsum));
					Strbr.append(decRoundOff(temp1)+"^"); 	
					Strbr.append(decRoundOff(temp2)+"^"); 	
					Strbr.append("^"); 		 
					//Strbr.append("\n");	
					Strbr.append("\r\n");
					temp++;
					emprecCount++;
					prvDdoReg=ddoRegNo;
					}
					else if (!prvDdoReg.equals(ddoRegNo) && (isContriZero && isInterestZero && isOpenInterestZero))
					{
						temp++;
						emprecCount++;
					}
					else 
					{
						prvDdoReg=ddoRegNo;
					}
					
					
					
					if(lstemployee!=null && !lstemployee.equals(""))
					{
						if(!isContriZero)
						{
							i=i+1;
							Strbr.append(i+"^");
							Strbr.append("SD"+"^");
							Strbr.append("1"+"^");
							Strbr.append(j+"^");
							Strbr.append(empCount+"^");
							Strbr.append(pranno +"^");
							Strbr.append(govEmpContri+"^");
							Strbr.append(subempContri+"^");
							Strbr.append("^");
							Strbr.append(TotalContri+"0"+"^");
							Strbr.append("A"+"^^^");
							//Strbr.append(monid+"^");
							//Strbr.append(yearDesc+"^");
							Strbr.append(" Arrear Contribution for Fin-Year "+finYr+"^");
							++empCount;
							totalContriCount=totalContriCount+1;
							Strbr.append("\r\n");
						}
						if(!isInterestZero)
						{
							i=i+1;
							Strbr.append(i+"^");
							Strbr.append("SD"+"^");
							Strbr.append("1"+"^");
							Strbr.append(j+"^");
							Strbr.append(empCount+"^");
							Strbr.append(pranno +"^");
							Strbr.append(intEmpl+"^");
							Strbr.append(intEmplr+"^");
							Strbr.append("^");
							Strbr.append(TotalIntContri+"^");
							Strbr.append("A"+"^^^");
							//Strbr.append(monid+"^");
							//Strbr.append(yearDesc+"^");
							Strbr.append(" Arrear Interest for Fin-Year "+finYr+"^");
							totalContriCount=totalContriCount+1;
							Strbr.append("\r\n");
							++empCount;
						}
						if(!isOpenInterestZero)
						{
							i=i+1;
							Strbr.append(i+"^");
							Strbr.append("SD"+"^");
							Strbr.append("1"+"^");
							Strbr.append(j+"^");
							Strbr.append(empCount+"^");
							Strbr.append(pranno +"^");
							Strbr.append(openIntEmpl+"^");
							Strbr.append(openIntEmplr+"^");
							Strbr.append("^");
							Strbr.append(TotalIntOpenContri+"^");
							Strbr.append("A"+"^^^");
							//Strbr.append(monid+"^");
							//Strbr.append(yearDesc+"^");
							Strbr.append(" Interest on opening balance for Fin-Year "+finYr+"^");
							totalContriCount=totalContriCount+1;
							Strbr.append("\r\n");
							++empCount;
						}
						
					}

				}

			
				
				List dcpsContriIdPk=lObjAlIndSer.selectTrnPk(finYr,treasuryyno);

				if(dcpsContriIdPk != null && !dcpsContriIdPk.isEmpty())
				{
					for(int k=0;k<dcpsContriIdPk.size();k++){
						String dcpsContriIdPks=dcpsContriIdPk.get(k).toString();
						
						lObjAlIndSer.updateRepStatus(dcpsContriIdPks,BatchId,finYr,treasuryyno);
					}
				}
				
				List dcpsIdPk=lObjAlIndSer.selectTrnPkForOpen(finYr,treasuryyno);

				if(dcpsIdPk != null && !dcpsIdPk.isEmpty())
				{
					for(int k=0;k<dcpsIdPk.size();k++){
						String dcpsContriIdPks=dcpsIdPk.get(k).toString();
						
						lObjAlIndSer.updateRepOpenStatus(dcpsContriIdPks,BatchId,finYr,treasuryyno);
					}
				}

			
					//new line 
				String lineSeperator ="\r\n"; 
				//System.getProperty("line.separator");.....not working


				String os = System.getProperty("os.name");


				if (os.toLowerCase().indexOf("unix") > 0){
					lineSeperator="\n";

				} else if (os.toLowerCase().indexOf("windows") > 0){
					lineSeperator ="\r\n"; 

				} else {

				}

				HttpServletResponse response = (HttpServletResponse) inputMap
				.get("responseObj");
		
				PrintWriter outputfile1 =response.getWriter();
				
				
				
				
				if(extnFlag!=null && !extnFlag.equals("") && Long.parseLong(extnFlag)==1)
				{

					getFileHeader(outputfile,dtoRegNo);
				
					//getBatchHeader(outputfile,totalsize,countReg,totalEmplyContri,totalEmplyerContri,totaloverallAmt,BatchId);
					getBatchHeader(outputfile,totalContriCount,ddoCount,totalEmplyContri,totalEmplyerContri,totaloverallAmt,BatchId,dtoRegNo);
					
				
				//	getDTOHeader( outputfile,totalsize,dtoRegNo,totalEmplyContri,totalEmplyerContri);	
				


				}
			

				

				String lStrFileName=BatchId;

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
		
		resObj.setViewName("ContributionListSRKA");
		return resObj;

	}

	private void getFileHeader(PrintWriter br,String dtoRegNo ) throws IOException
	{
		br.write("1"+"^");   		 br.write("FH"+"^"); 		 br.write("P"+"^"); 		 br.write(dtoRegNo+"^");		 br.write("1"+"^");
		br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^"); 
		br.write("\r\n");   //br.write("\n");
	}

	private void getBatchHeader(PrintWriter br , int count,long ddoCount,String govContri,String SubContri, String Total,String batchId,String dtoRegNo) throws IOException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());	
		String date = "ddMMyyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(date);
		sdf.setTimeZone(TimeZone.getDefault());
		String currentdate = sdf.format(cal.getTime());
	
		br.write("2"+"^");  		 br.write("BH"+"^"); 		 br.write("1"+"^"); 		 br.write("R"+"^");
		br.write(dtoRegNo+"^");  		 br.write(currentdate+"^"); 		 br.write(dtoRegNo+batchId); 
		br.write("^^"); 	  br.write(ddoCount+"^");		 br.write(count+"^"); 		 br.write(govContri+"^");
		br.write(SubContri+"^"); 		 br.write("^"); 		 br.write(Total+"^"); 	br.write("\r\n");	 //br.write("\n");
	}
	/*private void getDTOHeader(int i,PrintWriter br ,int count,String ddoregNo, String govContri,String SubContri,int j,int empcount) throws IOException
	{

		br.write(i+"^");		 br.write("DH"+"^"); 		 br.write("1"+"^"); 		 br.write(j+"^"); 		 br.write(ddoregNo+"^");
		br.write(empcount+"^"); 		 br.write(govContri+"0.00"+"^"); 		 br.write(SubContri+"0.00"+"^"); 		 br.write("^"); 		 
		br.write("\n");		 

	}*/

	private void getFileHeaderforfpu(PrintWriter br ) throws IOException
	{

		br.write("1"+"^");   		 br.write("FH"+"^"); 		 br.write("O"+"^"); 		 br.write("3100812"+"^");		 br.write("1"+"^");
		br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^"); 
		br.write("\n");

	}

	private void getBatchHeaderforfpu(PrintWriter br ,int count,String govContri,String SubContri, String Total) throws IOException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());	
		String date = "ddMMyyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(date);
		sdf.setTimeZone(TimeZone.getDefault());
		String currentdate = sdf.format(cal.getTime());

		br.write("2"+"^");  		 br.write("BH"+"^"); 		 br.write("1"+"^"); 		 br.write("R"+"^");
		br.write("3100812"+"^");  		 br.write(currentdate+"^"); 		 br.write(1111+"^"); 
		br.write("^"); 	 br.write("1"+"^"); 		 br.write(count+"^"); 		 br.write(govContri+"^");
		br.write(SubContri+"^"); 		 br.write("^"); 		 br.write(Total+"^"); 		 br.write("\n");
	}

	private void getDTOHeaderforfpu( PrintWriter br ,int count,String govContri,String SubContri) throws IOException
	{

		br.write("3"+"^");		 br.write("DH"+"^"); 		 br.write("1"+"^"); 		 br.write("1"+"^"); 		 br.write("1"+"^");
		br.write(count+"^"); 		 br.write(govContri+"^"); 		 br.write(SubContri+"^"); 		 br.write("^"); 		 
		br.write("\n");		 

	}
	private static String nosci(double d) {
		if (d < 0) {
			return "-" + nosci(-d);
		}
		String javaString = String.valueOf(d);
		int indexOfE = javaString.indexOf("E");
		if (indexOfE == -1) {
			return javaString;
		}
		StringBuffer sb = new StringBuffer();
		if (d > 1) {// big number
			int exp = Integer.parseInt(javaString.substring(indexOfE + 1));
			String sciDecimal = javaString.substring(2, indexOfE);
			int sciDecimalLength = sciDecimal.length();
			if (exp == sciDecimalLength) {
				sb.append(javaString.charAt(0));
				sb.append(sciDecimal);
			} else if (exp > sciDecimalLength) {
				sb.append(javaString.charAt(0));
				sb.append(sciDecimal);
				for (int i = 0; i < exp - sciDecimalLength; i++) {
					sb.append('0');
				}
			} else if (exp < sciDecimalLength) {
				sb.append(javaString.charAt(0));
				sb.append(sciDecimal.substring(0, exp));
				sb.append('.');
				for (int i = exp; i < sciDecimalLength; i++) {
					sb.append(sciDecimal.charAt(i));
				}
			}
			
			return sb.toString();
		} else {
			// for little numbers use the default or you will
			// loose accuracy
			
			return 	javaString;
		}

	}
 public static String decRoundOff(String number){
	 String s = String.format(("%.2f"), Double.parseDouble(number)); 
	 return s;
 }

}
