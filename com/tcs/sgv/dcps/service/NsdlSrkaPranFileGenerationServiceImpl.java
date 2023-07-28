package com.tcs.sgv.dcps.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import au.id.jericho.lib.html.Logger;

import com.ibm.db2.jcc.am.g;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NsdlSrkaFileGeneDAOImpl;
import com.tcs.sgv.dcps.dao.NsdlSrkaPranFileGenerationDAO;
import com.tcs.sgv.dcps.dao.NsdlSrkaPranFileGenerationDAOImpl;


public class NsdlSrkaPranFileGenerationServiceImpl extends ServiceImpl{
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
    
    public ResultObject getTreasuryList(Map inputMap)throws Exception{
        logger.info("Inside Get getTreasuryList--------------------");
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
        List batch=null;
        try{
            setSessionInfo(inputMap);
            NsdlSrkaPranFileGenerationDAO lObjTreasury = new NsdlSrkaPranFileGenerationDAOImpl(null,serv.getSessionFactory()); 
            DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
            Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

            //      lstAisType  = lObjAlIndSer.getAISlist();
           
            List treasury=lObjTreasury.getAllTreasuries();

            //      inputMap.put("lstAisType",lstAisType);  
            inputMap.put("treasury",treasury);      
            Boolean check=false;
            System.out.println("check"+check);
            String treasuryno=null;
            
            if(StringUtility.getParameter("treasno", request) != null  && StringUtility.getParameter("treasno", request) != ""
            	&& StringUtility.getParameter("batch", request) != null  && StringUtility.getParameter("batch", request) != ""){
            	String treasurynos=StringUtility.getParameter("treasno", request);
            	String batchs=StringUtility.getParameter("batch", request);
            	logger.info(" batch= "+batchs);
            	logger.info(" treasury= "+treasurynos);
            	String [] endPoints=batchs.split("-");
            	logger.info(" endpoints= "+endPoints[0]+" "+endPoints[1]);
            	 List empList=lObjTreasury.getEmpList(treasurynos,endPoints[0],endPoints[1]);
            	logger.info("empList size="+empList.size());
            	if(empList.size()>0)
            	 inputMap.put("empList",empList);
            	else
            		 inputMap.put("empList",null);
            	    inputMap.put("treasuryno",treasurynos);
                  
                    inputMap.put("batchs",batchs);
                    batch =lObjTreasury.getBatch(treasurynos);
            	
             	
            }
            else if(StringUtility.getParameter("treasno", request) != null  && StringUtility.getParameter("treasno", request) != ""){

            	
                //aisTypeSelected=StringUtility.getParameter("aisType", request);
             
                treasuryno=StringUtility.getParameter("treasno", request);
                logger.info(" treasury= "+treasuryno);
                 batch =lObjTreasury.getBatch(treasuryno);
               
                
                //      logger.info("aisTypeSelected-------------------"+aisTypeSelected);

                //      lstbillNo = lObjAlIndSer.selectBillNo(aisTypeSelected,finTypeSelected);
                //      logger.info("lstbillNo-------------------"+lstbillNo.size());
                //      inputMap.put("lstbillNo", lstbillNo);
                //      inputMap.put("aisTypeSelected",aisTypeSelected);        

			/*	String lSBStatus = getResponseXMLDocForcurrentEmpGroupDtls(treasuryno,((int) Math.ceil(batch.size()/4999))).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key", lSBStatus).toString();*/
/*
				inputMap.put("ajaxKey", lStrResult);
				logger.info("lStrResult---------" + lStrResult);*/
                
                inputMap.put("treasuryno",treasuryno);
                
				resultObject.setResultValue(inputMap);
				resultObject.setResultCode(ErrorConstants.SUCCESS);
				
				
				
                  
                
                //inputMap.put("billSel", lstbillNo);
            }

            //logger.info("aisTypeSelected-------------------"+aisTypeSelected);

          logger.info("yes i am here-------------------");

            
            if(batch!=null){
                inputMap.put("batch",batch);
                logger.info("yes 1 i am here-------------------");

               }
            logger.info("yes 2 i am here-------------------");

            resultObject.setViewName("NsdlSrkaPranFileGeneration");//set view name
            resultObject.setResultCode(ErrorConstants.SUCCESS);
            resultObject.setResultValue(inputMap);//put in result object

        }catch(Exception e){
            resultObject = new ResultObject(ErrorConstants.ERROR);
            resultObject.setResultCode(-1);
            resultObject.setViewName("errorPage");
            //resultObject.setViewName("errorPage");
            logger.error("Error  "+ e);
        }



        return resultObject;
    }
    


    
    public ResultObject txtFileGen(Map inputMap)throws Exception{
        logger.info("Inside Get TxtfileGen--------------------");
        ResultObject resultObject = new ResultObject(ErrorConstants.SUCCESS);

        String empname=null;
        String dob=null;
        String doj=null;
        String ppan=null;
        String pran=null;
        String sevarth_id=null;
        String dto_code=null;
        String ddo_reg_no=null;
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
        List batch=null;
        String Date=null;
        String fatherName=null;
        String gendre = null;
        String pan = null;
        String corrAdd = null;
        String city = null;
        String dist = null;
        String state = null;
        String pin = null;
        String permAdd = null;
        String pcity = null;
        String pdist = null;
        String pstate = null;
        String ppin = null;
        String phone = null;
        String mobile = null;
        String email = null;
        String scale = null;
        String basic_pay = null;
        String ppans = null;
        String paddBlock= null;
        String paddBuilding= null;
        String servEndDt= null;
        String superAnnDt= null;
        String ddoOffName= null;
        String accNo= null;
        String bankName= null;
        String branchName= null;
        String branchAdd= null;
        String branchPin= null;
        String micrCode= null;
        String ifscCode= null;
        String gradeName= null;
        String parentDept= null;
        String fieldDept= null;
        String cadd_L1=null;
        String cadd_L2=null;
        String cadd_L3=null;
        String padd_L1=null;
        String padd_L2=null;
        String padd_L3=null;
        
    	String nom1_name=null;
    	String nom1_dob=null;
    	String nom1_rel=null;
    	String nom1_gur=null;
    	String nom1_invlcon=null;
    	
    	String nom2_name=null;
    	String nom2_dob=null;
    	String nom2_rel=null;
    	String nom2_gur=null;
    	String nom2_invlcon=null;
    	
    	String nom3_name=null;
    	String nom3_dob=null;
    	String nom3_rel=null;
    	String nom3_gur=null;
    	String nom3_invlcon=null;
    	String salutation=null;
    	String ack_no=null;
			
    	String form_s1_id="(";
        int empListSize=0;
        int i=2;
        int p=2;
        BufferedReader br = null;
        StringBuilder Strbr=new StringBuilder();
        
        try{
        	
        	Calendar cal = Calendar.getInstance(TimeZone.getDefault());	
        	String date = "yyyy-MM-dd";
        	SimpleDateFormat sdf = new SimpleDateFormat(date);
        	sdf.setTimeZone(TimeZone.getDefault());
        	Date = sdf.format(cal.getTime());
        	
        	
            setSessionInfo(inputMap);
            NsdlSrkaPranFileGenerationDAO lObjTreasury = new NsdlSrkaPranFileGenerationDAOImpl(null,serv.getSessionFactory()); 
            DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
            Map loginDetailsMap = (Map) inputMap.get("baseLoginMap");

            //      lstAisType  = lObjAlIndSer.getAISlist();
           
         

            //      inputMap.put("lstAisType",lstAisType);  
          
           
            
          
            	String treasurynos=StringUtility.getParameter("treasno", request);
            	String batchs=StringUtility.getParameter("batch", request);
            	logger.info(" batch= "+batchs);
            	logger.info(" treasury= "+treasurynos);
            	String [] endPoints=batchs.split("-");
            	logger.info(" endpoints= "+endPoints[0]+" "+endPoints[1]+" "+endPoints[2]);
            	String file_name=treasurynos+endPoints[2];
            	 List empList=lObjTreasury.getEmpListPrint(treasurynos,endPoints[0],endPoints[1],file_name);
            	logger.info("empListPrint size="+empList.size());
            	 empListSize=empList.size();
            	PrintWriter outputfile =response.getWriter();
            	int count=0;
            	
            	int File_ID = 0;
				int Seq_NO = 0;
				int cnt=0;
				
				String file_id=lObjTreasury.getFileId(Date);
				String [] fileid_sqno=file_id.split("#");
				File_ID=Integer.parseInt(fileid_sqno[0]);
				Seq_NO=Integer.parseInt(fileid_sqno[1]);
				logger.info("File_ID*****************"+File_ID+"********"+file_id);
				logger.info("Seq_NO*****************"+Seq_NO+"********"+Seq_NO);
				
				
				for (Iterator it = empList.iterator(); it.hasNext();)				 
				{
					count++;
					Object[] lObj = (Object[]) it.next();
						

					empname = (lObj[0] != null && lObj[0].toString().trim() != "") ? lObj[0].toString(): "NA";
					
					fatherName= (lObj[1] != null && lObj[1].toString().trim() != "") ? lObj[1].toString(): "NA";
					
					dob = ((lObj[2] != null && lObj[2].toString().trim() != "")? lObj[2].toString() :"");


					doj = ((lObj[3] != null && lObj[3].toString().trim() != "")? lObj[3].toString() :"");

					ppan = (lObj[4] != null && lObj[4].toString().trim() != "") ? lObj[4].toString() : "";						

					pran = (lObj[5] != null && lObj[5].toString().trim() != "") ? lObj[5].toString() : "";		

					sevarth_id = (lObj[6] != null && lObj[6].toString().trim() != "") ? lObj[6].toString() : "";		
					logger.info("sevarth_id*****************"+sevarth_id);
					dto_code = (lObj[7] != null && lObj[7].toString().trim() != "") ? lObj[7].toString() : "";	
					
					//subempContri = (Float) ((lObj[14] != null)? Float.parseFloat( new DecimalFormat("##.##").format(Float.parseFloat(lObj[14].toString()))):new Float(new DecimalFormat("##.##").format(1000.00)));

					ddo_reg_no = (lObj[8] != null && lObj[8].toString().trim() != "") ? lObj[8].toString().trim() : "";
					gendre = (lObj[9] != null && lObj[9].toString().trim() != "") ? lObj[9].toString().trim() : "";
					pan = (lObj[10] != null && lObj[10].toString().trim() != "") ? lObj[10].toString().trim() : "";
					corrAdd = (lObj[11] != null && lObj[11].toString().trim() != "") ? lObj[11].toString().trim() : "";
					city = (lObj[12] != null && lObj[12].toString().trim() != "") ? lObj[12].toString().trim() : "";
					dist = (lObj[13] != null && lObj[13].toString().trim() != "") ? lObj[13].toString().trim() : "";
					state = (lObj[14] != null && lObj[14].toString().trim() != "") ? lObj[14].toString().trim() : "";
					pin = (lObj[15] != null && lObj[15].toString().trim() != "" && lObj[15].toString().trim() != "0") ? lObj[15].toString().trim() : "";
					permAdd = (lObj[16] != null && lObj[16].toString().trim() != "") ? lObj[16].toString() : "";
					pcity = (lObj[17] != null && lObj[17].toString().trim() != "") ? lObj[17].toString().trim() : "";
					pdist = (lObj[18] != null && lObj[18].toString().trim() != "") ? lObj[18].toString().trim() : "";
					pstate = (lObj[19] != null && lObj[19].toString().trim() != "") ? lObj[19].toString().trim() : "";
					ppin = (lObj[20] != null && lObj[20].toString().trim() != "" && lObj[20].toString().trim() != "0") ? lObj[20].toString().trim() : "";
					phone = (lObj[21] != null && lObj[21].toString().trim() != "") ? lObj[21].toString() : "";
					mobile = (lObj[22] != null && lObj[22].toString().trim() != "") ? lObj[22].toString().trim() : "";
					email = (lObj[23] != null && lObj[23].toString().trim() != "") ? lObj[23].toString().trim() : "";
					scale = (lObj[24] != null && lObj[24].toString().trim() != "") ? lObj[24].toString() : "";
					basic_pay = (lObj[25] != null && lObj[25].toString().trim() != "") ? lObj[25].toString() : "";
					ppans = (lObj[26] != null && lObj[26].toString().trim() != "") ? lObj[26].toString() : "";
					paddBlock=(lObj[27] != null && lObj[27].toString().trim() != "") ? lObj[27].toString() : "";
					paddBuilding=(lObj[28] != null && lObj[28].toString().trim() != "") ? lObj[28].toString() : "";
					servEndDt=(lObj[29] != null && lObj[29].toString().trim() != "") ? lObj[29].toString() : "";
					superAnnDt=(lObj[30] != null && lObj[30].toString().trim() != "") ? lObj[30].toString() : "";
					ddoOffName=(lObj[31] != null && lObj[31].toString().trim() != "") ? lObj[31].toString() : "";
					accNo=(lObj[32] != null && lObj[32].toString().trim() != "") ? lObj[32].toString() : "";
					bankName=(lObj[33] != null && lObj[33].toString().trim() != "") ? lObj[33].toString() : "";
					branchName=(lObj[34] != null && lObj[34].toString().trim() != "") ? lObj[34].toString() : "";
					branchAdd=(lObj[35] != null && lObj[35].toString().trim() != "") ? lObj[35].toString() : "";
					branchPin=(lObj[36] != null && lObj[36].toString().trim() != "" && lObj[36].toString().trim() != "0") ? lObj[36].toString() : "";
					micrCode=(lObj[37] != null && lObj[37].toString().trim() != "") ? lObj[37].toString() : "";
					ifscCode=(lObj[38] != null && lObj[38].toString().trim() != "") ? lObj[38].toString() : "";
					gradeName=(lObj[39] != null && lObj[39].toString().trim() != "") ? lObj[39].toString() : "";
					parentDept=(lObj[40] != null && lObj[40].toString().trim() != "") ? lObj[40].toString().trim() : "";
					fieldDept=(lObj[41] != null && lObj[41].toString().trim() != "") ? lObj[41].toString().trim() : "";
					int rec_Seq=Integer.parseInt(lObj[42].toString().trim());
					int nom_1_per=Integer.parseInt(lObj[43].toString().trim());
					int nom_2_per=Integer.parseInt(lObj[44].toString().trim());
					int nom_3_per=Integer.parseInt(lObj[45].toString().trim());
					cadd_L1=(lObj[46] != null && lObj[46].toString().trim() != "") ? lObj[46].toString() : "";
					cadd_L2=(lObj[47] != null && lObj[47].toString().trim() != "") ? lObj[47].toString() : "";
					cadd_L3=(lObj[48] != null && lObj[48].toString().trim() != "") ? lObj[48].toString() : "";
					padd_L1=(lObj[49] != null && lObj[49].toString().trim() != "") ? lObj[49].toString() : "";
					padd_L2=(lObj[50] != null && lObj[50].toString().trim() != "") ? lObj[50].toString() : "";
					padd_L3=(lObj[51] != null && lObj[51].toString().trim() != "") ? lObj[51].toString() : "";
					
				// akshay modification here
					
					nom1_name=(lObj[52] != null && lObj[52].toString().trim() != "") ? lObj[52].toString() : "";
					nom1_dob=(lObj[53] != null && lObj[53].toString().trim() != "") ? lObj[53].toString() : "";
					nom1_rel=(lObj[54] != null && lObj[54].toString().trim() != "") ? lObj[54].toString() : "";
					nom1_gur=(lObj[55] != null && lObj[55].toString().trim() != "") ? lObj[55].toString() : "";
					nom1_invlcon=(lObj[56] != null && lObj[56].toString().trim() != "") ? lObj[56].toString() : "";
					
					nom2_name=(lObj[57] != null && lObj[57].toString().trim() != "") ? lObj[57].toString() : "";
					nom2_dob=(lObj[58] != null && lObj[58].toString().trim() != "") ? lObj[58].toString() : "";
					nom2_rel=(lObj[59] != null && lObj[59].toString().trim() != "") ? lObj[59].toString() : "";
					nom2_gur=(lObj[60] != null && lObj[60].toString().trim() != "") ? lObj[60].toString() : "";
					nom2_invlcon=(lObj[61] != null && lObj[61].toString().trim() != "") ? lObj[61].toString() : "";

					nom3_name=(lObj[62] != null && lObj[62].toString().trim() != "") ? lObj[62].toString() : "";
					nom3_dob=(lObj[63] != null && lObj[63].toString().trim() != "") ? lObj[63].toString() : "";
					nom3_rel=(lObj[64] != null && lObj[64].toString().trim() != "") ? lObj[64].toString() : "";
					nom3_gur=(lObj[65] != null && lObj[65].toString().trim() != "") ? lObj[65].toString() : "";
					nom3_invlcon=(lObj[66] != null && lObj[66].toString().trim() != "") ? lObj[66].toString() : "";
					salutation=(lObj[67] != null && lObj[67].toString().trim() != "") ? lObj[67].toString() : "";
					ack_no=(lObj[68] != null && lObj[68].toString().trim() != "") ? lObj[68].toString() : "";
					
					
					form_s1_id=form_s1_id+((lObj[69] != null && lObj[69].toString().trim() != "") ? lObj[69].toString() : "");
					form_s1_id=form_s1_id+",";
					
					
					int nom_count=0;
					
					if(nom_1_per==0 && nom_2_per==0 && nom_3_per==0)
						nom_count=0;
					
					else if(nom_1_per>0 && nom_2_per==0 && nom_3_per==0)
						nom_count=1;
					else if(nom_1_per==0 && nom_2_per>0 && nom_3_per==0)
						nom_count=1;
					else if(nom_1_per==0 && nom_2_per==0 && nom_3_per>0)
						nom_count=1;
					
					else if(nom_1_per>0 && nom_2_per>0 && nom_3_per==0)
						nom_count=2;
					else if(nom_1_per==0 && nom_2_per>0 && nom_3_per>0)
						nom_count=2;
					else if(nom_1_per>0 && nom_2_per==0 && nom_3_per>0)
						nom_count=2;
					
					else if(nom_1_per>0 && nom_2_per>0 && nom_3_per>0)
						nom_count=3;
					
					
					
					
					
					
					
					
					
					
					
					
					rec_Seq=rec_Seq+count;
					
				
					
					
					
					if(empList!=null  && !empList.equals("")){
						
						if(i<10)
							Strbr.append("00000");
						else if(i<100)
							Strbr.append("0000");
						else if(i<1000)
							Strbr.append("000");
						else if(i<10000)
							Strbr.append("00");
						else if(i<100000)
							Strbr.append("0");
						else if(i<1000000)
							Strbr.append("");
						
						Strbr.append(i+"^");
						Strbr.append("FD"+"^");
						Strbr.append("N"+"^");
						
						if(p-1<10)
							Strbr.append("00000");
						else if(p-1<100)
							Strbr.append("0000");
						else if(p-1<1000)
							Strbr.append("000");
						else if(p-1<10000)
							Strbr.append("00");
						else if(p-1<100000)
							Strbr.append("0");
						else if(p-1<1000000)
							Strbr.append("");
						
						Strbr.append(p-1+"^^");
						Strbr.append(ack_no+"^");
						
						/*if(rec_Seq<10)
						Strbr.append(dto_code+"100"+"000000"+rec_Seq+"^");
						else if(Seq_NO<100)
							Strbr.append(dto_code+"100"+"00000"+rec_Seq+"^");
						else if(Seq_NO<1000)
							Strbr.append(dto_code+"100"+"0000"+rec_Seq+"^");
						else if(Seq_NO<10000)
							Strbr.append(dto_code+"100"+"000"+rec_Seq+"^");
						else if(Seq_NO<100000)
							Strbr.append(dto_code+"100"+"00"+rec_Seq+"^");
						else if(Seq_NO<1000000)
							Strbr.append(dto_code+"100"+"0"+rec_Seq+"^");
						else if(Seq_NO<10000000)
							Strbr.append(dto_code+"100"+""+rec_Seq+"^");*/
						
						
						Strbr.append(salutation.trim()+"^");
						
						String empsubname="";
						if(empname.length()>30){
							String ename[]=empname.split(" ");
							for(int k=0;k<(ename.length);k++)
							{
								
								if(k>=1)
									ename[k]=" "+ename[k];
								if(k!=1){
								empsubname=empsubname+ename[k];
								}
							}
							if(empsubname.length()<=30){
								Strbr.append(empsubname+"^^^");
							}else{
								empsubname=empsubname.substring(0,29);
								Strbr.append(empsubname+"^^^");
							}
								
							
						}else{
							Strbr.append(empname+"^^^");
						}
						
						
						
						
						
						
						
						
						
						String fathersubname="";
						if(fatherName.length()>30){
							String fatehrnm[]=fatherName.split(" ");
							for(int k=0;k<(fatehrnm.length);k++)
							{
								if(k>=1)
									fatehrnm[k]=" "+fatehrnm[k];
								if(k!=1){
									fathersubname=fathersubname+fatehrnm[k];
								}
							}
							if(fathersubname.length()<=30){
								Strbr.append(fathersubname+"^^^");
							}else{
								fathersubname=fathersubname.substring(0,29);
								Strbr.append(fathersubname+"^^^");
							}
								
							
						}else{
							Strbr.append(fatherName+"^^^");
						}
						//Strbr.append(fatherName+"^^^");
						Strbr.append(dto_code.trim()+"^");
						Strbr.append(ddo_reg_no.trim()+"^");
						Strbr.append(gendre.trim()+"^");
						Strbr.append(dob.trim()+"^");
						//Strbr.append(pan.trim()+"^");
						Strbr.append("^"+"");
						//Strbr.append(corrAdd+"^^^");
						
						
					
					

						
						Strbr.append(cadd_L1+"^");
						Strbr.append(cadd_L2+"^");
						Strbr.append(cadd_L3+"^");
						
						Strbr.append(dist.trim()+"^^^");
						Strbr.append(""+"19^");
						//Strbr.append(state.trim()+"^");
						Strbr.append(""+"IN^");
						Strbr.append(pin.trim()+"^");
						
						Strbr.append(padd_L1+"^");
						Strbr.append(padd_L2+"^");
						Strbr.append(padd_L3+"^");
						
						Strbr.append(pdist.trim()+"^^^");
						Strbr.append(""+"19^");
						Strbr.append(""+"IN^");
						Strbr.append(ppin.trim()+"^");
						//Strbr.append(phone.trim()+"^");
						Strbr.append("^"+"^");
						//Strbr.append(mobile.trim()+"^^");
						Strbr.append("^"+"");
						//Strbr.append(email.trim()+"^N^N^");//^
						Strbr.append(""+"^N^N^");
						Strbr.append(doj.trim()+"^");
						Strbr.append(superAnnDt.trim()+"^");
						if(gradeName.trim().equals("BnGz")){
							gradeName="C";
						}
						Strbr.append(gradeName+"^");
						
						
					/*	logger.info("fieldDept_length"+fieldDept.length());
						logger.info("parentDept_length"+parentDept.length());
						logger.info("fieldDept_length_SUB"+fieldDept.substring(0,39));*/
						
						if(fieldDept.length()>39){
						Strbr.append(fieldDept.substring(0,39)+"^");
						logger.info("inside_check##");
						}else
						{
						Strbr.append(fieldDept+"^");
						}
						if(parentDept.length()>39){
						Strbr.append(parentDept.substring(0,39)+"^");
						}else
						{
						Strbr.append(parentDept+"^");
						}
						Strbr.append(ddoOffName+"^");
						
						logger.info(scale.trim());
					/*	if(scale.trim().equals("BnGz")){
							scale="C";
						}*/
						
						if(scale.trim().equals("")){
							scale="Not Available";
						}
						Strbr.append(scale+"^");
						Strbr.append(basic_pay.trim()+"^");
						//Strbr.append(ppan.trim()+"^Y^Saving^");
						Strbr.append(ppan.trim());
						Strbr.append("^Y^");
						//Strbr.append("Saving^");
						Strbr.append("^");
						
						//Strbr.append(accNo.trim()+"^");
						Strbr.append("^");
						//Strbr.append(bankName+"^");
						Strbr.append("^");
						//Strbr.append(branchName+"^");
						Strbr.append("^");
						//Strbr.append(branchAdd+"^^^");	//^^^0^4195 ^1^0
						Strbr.append("^^^");
						//Strbr.append(branchPin.trim()+"^");
						Strbr.append("^");
						//Strbr.append(micrCode.trim()+"^");
						Strbr.append("^");
						//Strbr.append(nom_count+"^0^^^^^^^^^N^^^^^^^"+ifscCode+"^N^^^^N^^^^O^^X^O^M^2^F^00^^^^^^^^^^^^^^^^^^^");
						Strbr.append(nom_count+"^0^^^^^^^^^N^^^^^^^^N^^^^N^^^^^O^S^");
						//to be disussed with akik
						Strbr.append("X^N^N^4^F^00^^^^^^^^^^^^^^^^^^^");
					//										^^^^^^^			 ^^^ ^^^					    				  ^^^^^^^^^^^^^^^^^^^
						
						
						
						
						



						
						
					
				/*		
						gendre
						pan
						corrAdd
						city
						dist
						state
						pin
						permAdd
						pcity
						pdist
						pstate
						ppin
						phone
						mobile
						email
						scale
						basic_pay
						ppans
						paddBlock=
						paddBuilding=
						servEndDt=
						superAnnDt=
						ddoOffName=
						accNo=
						bankName=
						branchName=
						branchAdd=
						branchPin=
						micrCode=
						ifscCode=
						gradeName=
						parentDept=
						fieldDept=

*/
					        
					        
						
					

						Strbr.append("\r\n");

	// code added by akshay for ND	
						if(nom_count>0){
						
						for(int k=1; k<=nom_count;k++)
						{	i++;
							if(i<10)
								Strbr.append("00000");
							else if(i<100)
								Strbr.append("0000");
							else if(i<1000)
								Strbr.append("000");
							else if(i<10000)
								Strbr.append("00");
							else if(i<100000)
								Strbr.append("0");
							else if(i<1000000)
								Strbr.append("");
							
							Strbr.append(i+"^");
							Strbr.append("ND"+"^");
							Strbr.append("N"+"^");
							Strbr.append("0"+k+"^");
							Strbr.append("0"+(k)+"^");
							if(k==1)
							{						
								Strbr.append(subs30dname(nom1_name));
								Strbr.append(nom1_dob+"^");

								Strbr.append(nom1_rel.trim()+"^");
								
								logger.info("nom_dob##"+nom1_dob);
								String N1dob=nom1_dob.substring(2,4)+"-"+nom1_dob.substring(0,2)+"-"+nom1_dob.substring(4,8);
								logger.info("N1dob##"+N1dob);
								DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
								Date compDate=dateFormat.parse(N1dob.trim());
								logger.info("compDate##"+compDate);
								Date Cdate = new Date();
								String curDate = dateFormat.format(Cdate);
								//int comparison1 = Cdate.compareTo(compDate);
								//logger.info("comparison1##"+comparison1);
								long diff=Cdate.getTime()-compDate.getTime();
								long diffDays = diff / (24 * 60 * 60 * 1000);
								long diffYear = diffDays / 365;
								logger.info("diff##"+diff);
								logger.info("diffDays##"+diffDays);
								logger.info("diffYear##"+diffYear);
								
								Boolean MinorFlag=false;
								
								if(diffYear>=18){
									MinorFlag=false;
								Strbr.append("N"+"^");
								logger.info("MajorMinorFlag##"+"N");
								}else{
								Strbr.append("Y"+"^");
								MinorFlag=true;
								logger.info("MajorMinorFlag##"+"Y");
								}
								
								/*if(nom1_gur.trim().equals("NA")){
									Strbr.append("N"+"^");
								}else{
									Strbr.append("Y"+"^");
								}*/
								//Strbr.append(nom1_gur+"^^^");
								
								
								if(MinorFlag==false)
								{
									Strbr.append("^^^");
								}else if((MinorFlag==true)&&(!nom1_gur.trim().equals("NA"))){
								Strbr.append(subs30dname(nom1_gur));
								}else if((MinorFlag==true)&&(nom1_gur.trim().equals("NA"))){
									Strbr.append("Not Available^^^");
								}
								
								Strbr.append(nom_1_per+"^");
								if(nom1_invlcon.trim().equals("NA")){
									Strbr.append("^");}
								else{
								Strbr.append(nom1_invlcon+"^");}
							}else if(k==2)
							{
								//Strbr.append(nom2_name+"^^^");
								Strbr.append(subs30dname(nom2_name));
								Strbr.append(nom2_dob+"^");
								Strbr.append(nom2_rel.trim()+"^");
								
								
								
								
								logger.info("nom_dob##"+nom2_dob);
								String N1dob=nom2_dob.substring(2,4)+"-"+nom2_dob.substring(0,2)+"-"+nom2_dob.substring(4,8);
								logger.info("N1dob##"+N1dob);
								DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
								Date compDate=dateFormat.parse(N1dob.trim());
								logger.info("compDate##"+compDate);
								Date Cdate = new Date();
								String curDate = dateFormat.format(Cdate);
								//int comparison1 = Cdate.compareTo(compDate);
								//logger.info("comparison1##"+comparison1);
								long diff=Cdate.getTime()-compDate.getTime();
								long diffDays = diff / (24 * 60 * 60 * 1000);
								long diffYear = diffDays / 365;
								logger.info("diff##"+diff);
								logger.info("diffDays##"+diffDays);
								logger.info("diffYear##"+diffYear);
								
								Boolean MinorFlag=false;
								
								if(diffYear>=18){
									MinorFlag=false;
								Strbr.append("N"+"^");
								logger.info("MajorMinorFlag##"+"N"+MinorFlag);
								}else{
								Strbr.append("Y"+"^");
								MinorFlag=true;
								logger.info("MajorMinorFlag##"+"Y"+MinorFlag);
								}
								
								/*if(nom1_gur.trim().equals("NA")){
									Strbr.append("N"+"^");
								}else{
									Strbr.append("Y"+"^");
								}*/
								//Strbr.append(nom1_gur+"^^^");
								
								
								if(MinorFlag==false)
								{
									Strbr.append("^^^");
								}else if((MinorFlag==true)&&(!nom2_gur.trim().equals("NA"))){
								Strbr.append(subs30dname(nom2_gur));
								}else if((MinorFlag==true)&&(nom2_gur.trim().equals("NA"))){
									Strbr.append("Not Available^^^");
								}
								
								
								
/*								
								if(nom2_gur.trim().equals("NA")){
									Strbr.append("N"+"^");
								}else{
									Strbr.append("Y"+"^");
								}
								//Strbr.append(nom2_gur+"^^^");
								if(nom2_gur.equals("NA"))
								{
									Strbr.append("^^^");
								}else{
								Strbr.append(subs30dname(nom2_gur));
								}*/
							
								
								Strbr.append(nom_2_per+"^");
								if(nom2_invlcon.trim().equals("NA")){
									Strbr.append("^");}
								else{
								Strbr.append(nom2_invlcon+"^");}
							}else if(k==3)
							{
								//Strbr.append(nom3_name+"^^^");
								Strbr.append(subs30dname(nom3_name));
								Strbr.append(nom3_dob+"^");
								Strbr.append(nom3_rel.trim()+"^");
								
								
								
								
								
								logger.info("nom_dob##"+nom3_dob);
								String N1dob=nom3_dob.substring(2,4)+"-"+nom3_dob.substring(0,2)+"-"+nom3_dob.substring(4,8);
								logger.info("N1dob##"+N1dob);
								DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
								Date compDate=dateFormat.parse(N1dob.trim());
								logger.info("compDate##"+compDate);
								Date Cdate = new Date();
								String curDate = dateFormat.format(Cdate);
								//int comparison1 = Cdate.compareTo(compDate);
								//logger.info("comparison1##"+comparison1);
								long diff=Cdate.getTime()-compDate.getTime();
								long diffDays = diff / (24 * 60 * 60 * 1000);
								long diffYear = diffDays / 365;
								logger.info("diff##"+diff);
								logger.info("diffDays##"+diffDays);
								logger.info("diffYear##"+diffYear);
								
								Boolean MinorFlag=false;
								
								if(diffYear>=18){
									MinorFlag=false;
								Strbr.append("N"+"^");
								logger.info("MajorMinorFlag##"+"N");
								}else{
								Strbr.append("Y"+"^");
								MinorFlag=true;
								logger.info("MajorMinorFlag##"+"Y");
								}
								
								/*if(nom1_gur.trim().equals("NA")){
									Strbr.append("N"+"^");
								}else{
									Strbr.append("Y"+"^");
								}*/
								//Strbr.append(nom1_gur+"^^^");
								
								
								if(MinorFlag==false)
								{
									Strbr.append("^^^");
								}else if((MinorFlag==true)&&(!nom3_gur.trim().equals("NA"))){
								Strbr.append(subs30dname(nom3_gur));
								}else if((MinorFlag==true)&&(nom3_gur.trim().equals("NA"))){
									Strbr.append("Not Available^^^");
								}
								
								
								
								
							/*	
								if(nom3_gur.trim().equals("NA")){
									Strbr.append("N"+"^");
								}else{
									Strbr.append("Y"+"^");
								}
							//	Strbr.append(nom3_gur+"^^^");
								if(nom3_gur.equals("NA"))
								{
									Strbr.append("^^^");
								}else{
								Strbr.append(subs30dname(nom3_gur));
								}*/
								
								Strbr.append(nom_3_per+"^");
								if(nom3_invlcon.trim().equals("NA")){
									Strbr.append("^");}
								else{
								Strbr.append(nom3_invlcon+"^");}
							}
							
							
							Strbr.append("ABC^^^^^^");
							Strbr.append("\r\n");
						}
						}
						
					}

					++i;
					++p;


				}
				
				
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
				
				
				
				
				/*if(extnFlag!=null && !extnFlag.equals("") && Long.parseLong(extnFlag)==1)
				{*/
				
			
				getFileHeader(outputfile,empListSize,File_ID);
				
				//	getBatchHeader(outputfile,totalsize,countReg,totalEmplyContri,totalEmplyerContri,totaloverallAmt,BatchId);
				
				//	getDTOHeader( outputfile,totalsize,dtoRegNo,totalEmplyContri,totalEmplyerContri);	
				


				/*}*/
				
	String lStrFileName=treasurynos+endPoints[2];		

				/*if(extn.equals("txt"))
				{*/
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
			
			
			form_s1_id=form_s1_id.substring(0,form_s1_id.length()-1);
			form_s1_id=form_s1_id+")";
		
			logger.info("form s1 ids are ################"+form_s1_id);
			lObjTreasury.updateFlagEmpListPrint(treasurynos,endPoints[0],endPoints[1],file_name,form_s1_id);
			
			
				/*}*/
			
			
		}	 
		catch (Exception e)
		{
			logger.info("Error occure in createTxtFile()"+e);
			e.printStackTrace();
			resultObject.setResultCode(ErrorConstants.ERROR);


		}
		resultObject.setResultCode(ErrorConstants.SUCCESS);
		resultObject.setResultValue(inputMap);
		
		resultObject.setViewName("NsdlSrkaPranFileGeneration");
		return resultObject;

	}
    
    
private void getFileHeader(PrintWriter br,int empListSize, int file_ID) throws IOException
{
	
	Calendar cal = Calendar.getInstance(TimeZone.getDefault());	
	String date = "MMddyyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(date);
	sdf.setTimeZone(TimeZone.getDefault());
	String Date = sdf.format(cal.getTime());
 
	
	 
	br.write("000001"+"^");   		 br.write("FH"+"^"); 		 br.write("PRAN"+"^"); 		 br.write("R"+"^");		 br.write(Date+"^");
	if(file_ID<10)
		br.write("00");	
	else if(file_ID<100)
		br.write("0");
	else if(file_ID<1000)
		br.write("");
	br.write(file_ID+"^");	
	
	if(empListSize<10)
		br.write("00000");
	else if(empListSize<100)
		br.write("0000");
	else if(empListSize<1000)
		br.write("000");
	else if(empListSize<10000)
		br.write("00");
	else if(empListSize<100000)
		br.write("0");
	else if(empListSize<1000000)
		br.write("");
	br.write(empListSize+"");		
	br.write("\r\n");   //br.write("\n");
}

/*private void getBatchHeader(PrintWriter br , int count,long ddoCount,String govContri,String SubContri, String Total,String batchId) throws IOException
{
	

	br.write("000002"+"^");  		 br.write("FD"+"^"); 		 br.write("1"+"^"); 		 br.write("R"+"^");
	br.write("3100812"+"^");  		 		 br.write("3100812"+batchId); 
	br.write("^^"); 	  br.write(ddoCount+"^");		 br.write(count*2+"^"); 		 br.write(govContri+"^");
	br.write(SubContri+"^"); 		 br.write("^"); 		 br.write(Total+"^"); 	br.write("\r\n");	 //br.write("\n");
}*/

public String subs30dname (String name){
	String return_name="";
	String fatherName=name;
	String fathersubname="";
	if(fatherName.length()>30){
		String fatehrnm[]=fatherName.split(" ");
		for(int k=0;k<(fatehrnm.length);k++)
		{
			if(k>=1)
				fatehrnm[k]=" "+fatehrnm[k];
			if(k!=1){
				fathersubname=fathersubname+fatehrnm[k];
			}
		}
		if(fathersubname.length()<=30){
			return_name=fathersubname+"^^^";
		}else{
			fathersubname=fathersubname.substring(0,29);
			return_name=fathersubname+"^^^";
		}
			
		
	}else{
		return_name=fatherName+"^^^";
	}
	
	
	return return_name;
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
    
}
