package com.tcs.sgv.dcps.service; 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bds.authorization.MapConverter;
import bds.authorization.PayrollBEAMSIntegrateWS;

import com.cra.common.util.CRAConfigReader;
import com.cra.common.util.encrypt.Hash;
import com.cra.common.util.file.CRABasicFileWriter;
import com.cra.pao.fvu.SubContrFileFormatValidator;
import com.cra.pao.vo.PAOContrErrorFileVO;
import com.cra.stp.core.webservice.PerformFileUpload;
import com.cra.stp.core.webservice.STPWebServicePOJO;
import com.cra.stp.core.webservice.STPWebServicePOJOService;
import com.ibm.icu.util.StringTokenizer;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.ncode.ctrl.pki.PKICtrl;
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.common.valueobject.TrnNPSBeamsIntegration;
import com.tcs.sgv.core.constant.ErrorConstants;
import com.tcs.sgv.core.service.ServiceImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.dcps.dao.NsdlNpsDAOImpl;
import com.tcs.sgv.dcps.dao.NsdlSrkaFileGeneDAOImpl;
import com.tcs.sgv.eis.service.IdGenerator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cra.standalone.paosubcontr.PAOFvu;

//import cra.standalone.paosubcontr.PAOFvu;






/////6 oct 2021 with deputation code
public class NsdlNpsServiceImpl  extends ServiceImpl 
{

	/* Global Variable for Logger Class */
	private final Log gLogger = LogFactory.getLog(getClass());

	private String gStrPostId = null; /* STRING POST ID */

	private String gStrLocationCode = null;

	private Long gLngPostId = null;

	private HttpServletRequest request = null; /* REQUEST OBJECT */

	private ServiceLocator serv = null; /* SERVICE LOCATOR */

	private HttpSession session = null; /* SESSION */

	private Date gDtCurDate = null; /* CURRENT DATE */

	private String gStrUserId = null; /* STRING USER ID */

	private HttpServletResponse response= null;/* RESPONSE OBJECT*/

	/* Global Variable for UserId */
	Long gLngUserId = null;

	private Long gLngDBId = null; /* DB ID */

	/* Resource bundle for the constants */
	private ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/dcps/DCPSConstants");
	//private ResourceBundle integrationBundleConst = ResourceBundle
	//.getBundle("resources/dcps/DCPSConstants");
	private ResourceBundle integrationBundleConst = ResourceBundle
	.getBundle("resources/common/IFMSIntegration_en_US");
	//private ResourceBundle integrationBundleConst = ResourceBundle
	//.getBundle("resources/dcps/DCPSConstants");
	/*
	 * Function to save the session specific details
	 */
	private void setSessionInfo(Map inputMap) {

		try {
			response = (HttpServletResponse) inputMap.get("responseObj");
			request = (HttpServletRequest) inputMap.get("requestObj");
			serv = (ServiceLocator) inputMap.get("serviceLocator");
			HttpSession session = request.getSession();
			session.setMaxInactiveInterval(35*60);
			session = request.getSession();
			gStrPostId = SessionHelper.getPostId(inputMap).toString();
			gLngPostId = SessionHelper.getPostId(inputMap);
			gStrLocationCode = SessionHelper.getLocationCode(inputMap);
			gLngUserId = SessionHelper.getUserId(inputMap);
			gLogger.info(" gLngUserId ssssss : " + gLngUserId);
			gStrUserId = gLngUserId.toString();
			gLngDBId = SessionHelper.getDbId(inputMap);
			gDtCurDate = SessionHelper.getCurDate();
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
		}
	}

	public ResultObject loadNPSNSDLForm(Map inputMap) {
		
	ResultObject resObj = new ResultObject(0, "FAIL");
    String lStrTempFromDate = null;
    String lStrTempToDate = null;
    String lStrFromDate = null;
    String lStrToDate = null;
    Date lDateFromDate = null;
    Date lDateToDate = null;
    List lListTotalDdowiseEntries = null;
    List lListTotalDdowiseEntriesFinal = null;
    Long yearId = null;
    Long monthId = null;
    Long finYearId = Long.valueOf(7340579277617758208L);
    Double lLongEmployeeAmt = Double.valueOf(0D);
    Double lLongEmployerAmt = Double.valueOf(0D);

    Double TotalAmt = Double.valueOf(0D);
    String finYearCode = "";
    try
    {
      setSessionInfo(inputMap);
      Map loginMap = (Map)inputMap.get("baseLoginMap");
      long langId = Long.parseLong(loginMap.get("langId").toString());//1
      NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, this.serv.getSessionFactory());
    //$t 2020 17-1
      List lLstYears = lObjNsdlDAO.getFinyear();

      DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, 
        this.serv.getSessionFactory());

      List lLstMonths = lObjDcpsCommonDAO.getMonths();
      SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
      String subTrCode = "";
      String ifDeputation = "2";
      String ddoCodeTr = "";
      String  trCode ="";
      int trFlag=0;
      int flag = 0;
      List subTr=null;
      
      String ifDeputationThane=  StringUtility.getParameter("ifDeputationThane", this.request);////$t KR 8-1-2021
   
      subTr = lObjNsdlDAO.getAllSubTreasury(this.gStrLocationCode);


      
      List subTr1 = lObjNsdlDAO.getAllSubTreasuryPAO(this.gStrLocationCode);

      if ((subTr1 != null) && (subTr1.size() > 0) && (subTr1.get(0) != null))
      {
        Object[] obj = (Object[])subTr1.get(0);
        if ((obj[0] != null) && (obj[1] != null))
        {
        	trCode = obj[0].toString();
        }
      }//((subTr1 != null) && (subTr1.size() > 0) && (subTr1.get(0) != null))
      
     /* if  (trCode.equals("7101"))
      {
    	  trFlag=1;
      }
      else {
    	  trFlag=2;
	}*/
      
      
      if ((!(StringUtility.getParameter("yearId", this.request).equalsIgnoreCase(""))) && (StringUtility.getParameter("yearId", this.request) != null) && 
        (!(StringUtility.getParameter("monthId", this.request).equalsIgnoreCase(""))) && (StringUtility.getParameter("monthId", this.request) != null) && 
        (!(StringUtility.getParameter("cmbTr", this.request).equalsIgnoreCase(""))) && (StringUtility.getParameter("cmbTr", this.request) != null))
      {
        yearId = Long.valueOf(StringUtility.getParameter("yearId", this.request));//2018
        monthId = Long.valueOf(StringUtility.getParameter("monthId", this.request));//6
        subTrCode = StringUtility.getParameter("cmbTr", this.request);//1111
       
        /*if (subTrCode.equals("1"))
        {
        	subTrCode="7101";
        	flag=1;
        }
        else if (subTrCode.equals("2")) {
        	subTrCode="7101";
        	flag=2;
		}*/
        
        
        
        if ((StringUtility.getParameter("ifDeputation", this.request) != null) && (!(StringUtility.getParameter("ifDeputation", this.request).equalsIgnoreCase(""))))
        {
          ifDeputation = StringUtility.getParameter("ifDeputation", this.request);
        }
        List DDOList = lObjNsdlDAO.getTreasuryDdoCode(Long.parseLong(this.gStrLocationCode));
        if ((DDOList != null) && (DDOList.size() > 0) && (DDOList.get(0) != null))
        {
          Object[] obj = (Object[])DDOList.get(0);
          if ((obj[0] != null) && (obj[1] != null))
          {
            ddoCodeTr = obj[0].toString();//11112222222
          }
        }

       /* if (ddoCodeTr.equals("1111222222")) {
          ddoCodeTr = "9101005555";
        }*/

        if ((ifDeputation != null) && (!("".equalsIgnoreCase(ifDeputation))) && ("2".equalsIgnoreCase(ifDeputation)))
        {  //$t 2019
          List totalList = lObjNsdlDAO.getDdoWiseTotalAmt(yearId, monthId, subTrCode,flag);
          List nsdlList = lObjNsdlDAO.getDdoWiseTotalAmtSentToNSDL(yearId, monthId, subTrCode,flag);
          if ((totalList != null) && (totalList.size() > 0))
          {
            lListTotalDdowiseEntries = new ArrayList();
            for (int j = 0; j < totalList.size(); ++j)
            {
              if ((totalList.get(j) != null) && (!("".equalsIgnoreCase(totalList.get(j).toString()))))
              {
                Object[] obj1 = (Object[])totalList.get(j);
                //System.out.println("pran no is-->"+(obj1[1].toString()));
                
                if ((nsdlList != null) && (nsdlList.size() > 0))
                {
                  for (int k = 0; k < nsdlList.size(); ++k)
                  {
                    if ((nsdlList.get(k) != null) && (!("".equalsIgnoreCase(nsdlList.get(k).toString()))))
                    {
                      Object[] obj2 = (Object[])nsdlList.get(k);
                      //System.out.println("obj2 nsdlList-->"+obj2.toString());
                      //obj1[6]=ddo_reg_no,obj2[4]=ddo_reg_no,obj2[0]&obj1[1]=PRAN_NO
                      if ((obj2[4].toString().equalsIgnoreCase(obj1[6].toString())) && (obj2[0].toString().equalsIgnoreCase(obj1[1].toString())))
                      {   
                    	 //obj1[5]=DED_ADJUST-sd_amnt
                    	//obj1[4]=employye-sd_amnt
                    	 //$t 2019 obj2[5]
                        obj1[5] = nosci(Double.parseDouble(obj1[5].toString()) - Double.parseDouble(obj2[5].toString()));
                        obj1[4] = nosci(Double.parseDouble(obj1[4].toString()) - Double.parseDouble(obj2[1].toString()));
                      }
                    }
                  }
                }
                
                if(obj1[5]!=null && Double.parseDouble(obj1[5].toString()) > 0  & obj1[4]!=null && Double.parseDouble(obj1[4].toString()) >= 0)////LessEmployer $t 20-6-2021//$t 2020 9-1 obj1[4]>=0 /////$t 11-11-2020 obj1[4]>=0 employee arrear contribution is zero.
                {
                	 lListTotalDdowiseEntries.add(obj1);
                	 //System.out.println("count-->"+j);
                	 
                	 //$t 2019
//                	 if(!lListTotalDdowiseEntries.isEmpty() && lListTotalDdowiseEntries.size() > 0) {
//         				Object []lObj = (Object[]) lListTotalDdowiseEntries.get(0);
//         				System.out.println("-->"+lObj[0]);
//         				System.out.println("-->"+lObj[1]);
//         				System.out.println("-->"+lObj[2]);
//         				System.out.println("-->"+lObj[3]);
//         				System.out.println("-->"+lObj[4]);
//         				System.out.println("-->"+lObj[5]);
//         				System.out.println("-->"+lObj[6]);
//                     }
                }
              }
            }
          }
          if ((lListTotalDdowiseEntries != null) && (lListTotalDdowiseEntries.size() > 0) && (lListTotalDdowiseEntries.get(0) != null) && (!("".equalsIgnoreCase(lListTotalDdowiseEntries.get(0).toString()))))
          {
            List prvDDo = new ArrayList();
            lListTotalDdowiseEntriesFinal = new ArrayList();
            for (int i = 0; i < lListTotalDdowiseEntries.size(); ++i)
            {
              Object[] obj1 = (Object[])lListTotalDdowiseEntries.get(i);
             
              

              for (int m = 0; m < lListTotalDdowiseEntries.size(); ++m)
              {
                if (m == i)
                {	
                  continue;
                }

                Object[] obj2 = (Object[])lListTotalDdowiseEntries.get(m);
//                if(obj2[1].toString().equalsIgnoreCase("110064436497"))
//                	System.out.println("a");
//                
//                if(obj1[0].toString().equalsIgnoreCase("4405011200") && obj2[0].toString().equalsIgnoreCase("4405011200") )
//              	  System.out.println("Hi....."+obj2[1].toString());
                
                if ((obj1[0].toString().equalsIgnoreCase(obj2[0].toString())) && 
                  (!(prvDDo.contains(obj1[0].toString()))))
                {//$t 
                  obj1[5] = nosci(Double.parseDouble(obj1[5].toString()) + Double.parseDouble(obj2[5].toString()));
                  obj1[4] = nosci(Double.parseDouble(obj1[4].toString()) + Double.parseDouble(obj2[4].toString()));
                  obj1[2] = nosci(Double.parseDouble(obj1[2].toString()) + Double.parseDouble(obj2[2].toString()));
                  obj1[3] = nosci(Double.parseDouble(obj1[3].toString()) + Double.parseDouble(obj2[3].toString()));
                }

              }

              if (!(prvDDo.contains(obj1[0].toString())))
              {
                lListTotalDdowiseEntriesFinal.add(obj1);
              }

              prvDDo.add(obj1[0].toString());
            }
          }
        }
        else
        {
          if (monthId.longValue() < 4L)
          {
            finYearCode = String.valueOf(yearId.longValue() - 1);
          }
          else
          {
            finYearCode = String.valueOf(yearId);
          }
          finYearId = lObjNsdlDAO.getFinYearId(finYearCode);
          lListTotalDdowiseEntriesFinal = lObjNsdlDAO.getDdoWiseTotalAmtForDeputation
            (finYearId, monthId, subTrCode, yearId.toString(), ddoCodeTr);
        }

        if (lListTotalDdowiseEntriesFinal != null)
        {
          Iterator it = lListTotalDdowiseEntriesFinal.iterator();

          Object[] tupleNps = (Object[])null;
          while (it.hasNext())
          {
            tupleNps = (Object[])it.next();
            lLongEmployeeAmt = Double.valueOf(tupleNps[3].toString());
            lLongEmployerAmt = Double.valueOf(tupleNps[4].toString());
            TotalAmt = Double.valueOf(lLongEmployeeAmt.doubleValue() + lLongEmployerAmt.doubleValue());

            inputMap.put("TotalAmt", TotalAmt);
          }
          String lLongEmployeeTotalAmt = "0";
          String lLongEmployerTotalAmt = "0";
          String lLongEmployeeGrossAmnt = "0";
          String lLongEmployeeNetAmnt = "0";
          Object[] obj = (Object[])null;
          if ((ifDeputation != null) && (!("".equalsIgnoreCase(ifDeputation))) && ("2".equalsIgnoreCase(ifDeputation)))
          {
            Iterator it1 = lListTotalDdowiseEntriesFinal.iterator();
            while (it1.hasNext())
            {
              tupleNps = (Object[])it1.next();
              lLongEmployeeTotalAmt = nosci(Double.parseDouble(lLongEmployeeTotalAmt) + Double.parseDouble(tupleNps[4].toString()));
              lLongEmployerTotalAmt = nosci(Double.parseDouble(lLongEmployerTotalAmt) + Double.parseDouble(tupleNps[5].toString()));
              lLongEmployeeGrossAmnt = nosci(Double.parseDouble(lLongEmployeeGrossAmnt) + Double.parseDouble(tupleNps[2].toString()));
              lLongEmployeeNetAmnt = nosci(Double.parseDouble(lLongEmployeeNetAmnt) + Double.parseDouble(tupleNps[3].toString()));
              obj = new Object[7];
              obj[0] = "A";
              obj[1] = "A";
              obj[2] = lLongEmployeeGrossAmnt;
              obj[3] = lLongEmployeeNetAmnt;
              obj[4] = lLongEmployeeTotalAmt;
              obj[5] = lLongEmployerTotalAmt;
              obj[6] = "A";
            }

            if (obj != null)
            {
              lListTotalDdowiseEntriesFinal.add(obj);
            }
          }
        }

        inputMap.put("selectedYear", yearId);
        inputMap.put("selectedMonth", monthId);
      }//((!(StringUtility.getParameter("yearId", this.request).equalsIgnoreCase(""))) && (StringUtility.getParameter("yearId", this.request) != null)
      
      
      if(ifDeputationThane.length()>0)////8-1-2021 $t KR Revised dan
      {
          //subTr = lObjNsdlDAO.getAllSubTreasury1(this.gStrLocationCode);
          ifDeputation="1";
      }
         
      
      
      inputMap.put("trflag", trFlag);
      
      inputMap.put("flag", flag);
      inputMap.put("subTrCode", subTrCode);
      inputMap.put("LOCATION_CODE",this.gStrLocationCode);

      inputMap.put("subTr", subTr);////$t KR Revised 8-1-2021 dan
      inputMap.put("YEARS", lLstYears);
      inputMap.put("MONTHS", lLstMonths);
      inputMap.put("lListTotalDdowiseEntries", lListTotalDdowiseEntriesFinal);
      inputMap.put("idDeputation", ifDeputation);

      resObj.setResultValue(inputMap);
      resObj.setViewName("NPSNSDL");
    }//try
    catch (Exception e)
    {
      e.printStackTrace();
      this.gLogger.error(" Error is : " + e, e);
      resObj.setResultValue(null);
      resObj.setThrowable(e);
      resObj.setResultCode(-1);
      resObj.setViewName("errorPage");
    }

    return resObj;
    
	}
	private void getAllSubTreasuryPAO(String gStrLocationCode2) {
		// TODO Auto-generated method stub
		
	}
	
	public ResultObject genNsdlTxtFile(Map<String, Object> inputMap)
	  {
	    ResultObject resObj = new ResultObject(0, "FAIL");
	    this.gLogger.info("in genNsdlTxtFile---------------------- ");
	    
	    Long Num = null;
	    BufferedReader br = null;
	    
	    String empname = null;
	    String dcpsid = null;
	    String pranno = null;
	    String govEmpContri = null;
	    String subempContri = null;
	    String Contritype = null;
	    
	    int countsum = 0;
	    String govcontiSum = null;
	    String subcontiSum = null;
	    String TotalContri = null;
	    String TotalContrisum = null;
	    String extn = null;
	    String aisType = null;
	    
	    StringBuilder Strbr = new StringBuilder();
	    StringBuilder Strbr1 = new StringBuilder();
	    String extnFlag = null;
	    
	    String fromDate = null;
	    String toDate = null;
	    String dtoRegNo = null;
	    String ddoRegNo = null;
	    String sdAmnt = null;
	    String prvDdoReg = "AAA";
	    String treasuryyno = null;
	    List lstemployee = null;
	    
	    String yrCode = null;
	    String month = null;
	    List nsdlData = null;
	    String BatchId = null;
	    Long nwbatchId = null;
	    String nwTranBatchId = null;
	    String[] fyYrsplit = null;
	    String tranId = null;
	    String subTrCode = "";
	    int flag = 0;
	    int digiActivate = 0;
	    try
	    {
	      setSessionInfo(inputMap);
	      
	      treasuryyno = this.gStrLocationCode;
	      ServiceLocator serv = (ServiceLocator)inputMap.get("serviceLocator");
	      if ((StringUtility.getParameter("yearId", this.request) != null) && (!StringUtility.getParameter("yearId", this.request).equals("")) && (Long.parseLong(StringUtility.getParameter("yearId", this.request)) != -1L) && 
	        (StringUtility.getParameter("monthId", this.request) != null) && (!StringUtility.getParameter("monthId", this.request).equals("")) && (Long.parseLong(StringUtility.getParameter("monthId", this.request)) != -1L) && 
	        (StringUtility.getParameter("subTr", this.request) != null) && (!StringUtility.getParameter("subTr", this.request).equals("")) && (Long.parseLong(StringUtility.getParameter("subTr", this.request)) != -1L))
	      {
	        extn = "txt";
	        
	        extnFlag = StringUtility.getParameter("flagFile", this.request).trim();
	        yrCode = StringUtility.getParameter("yearId", this.request);
	        month = StringUtility.getParameter("monthId", this.request);
	        treasuryyno = StringUtility.getParameter("subTr", this.request);
	      }
	      
	      
	      NsdlNpsDAOImpl lObjNsdlNps = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
	      digiActivate = lObjNsdlNps.getDigiActivationDtls(this.gStrLocationCode);
	      
	      String midFix = "";
	      String finalFix = "";
	      if (month.length() == 1) {
	        midFix = "0";
	      }
	      String batchIdPrefix = treasuryyno + yrCode + midFix + month;
	      String countBatchId = lObjNsdlNps.getbatchIdCount(batchIdPrefix);
	      if (countBatchId.length() == 1) {
	        finalFix = "00";
	      }
	      if (countBatchId.length() == 2) {
	        finalFix = "0";
	      }
	      BatchId = batchIdPrefix + finalFix + countBatchId;
	      
	      DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());
	      
	      NsdlSrkaFileGeneDAOImpl lObjAlIndSer = new NsdlSrkaFileGeneDAOImpl(null, serv.getSessionFactory());
	      
	      Object[] lyrObj = null;
	      
	      lstemployee = lObjNsdlNps.getEmployeeListNsdl(yrCode, month, treasuryyno, flag);
	      Long countReg = lObjNsdlNps.getDDoRegCount(yrCode, month, treasuryyno, flag);
	      
	      this.gLogger.info("BH DDO  REG count is " + countReg);
	      
	      int totalsize = lstemployee.size();
	      this.gLogger.info("BH  total employee size is " + totalsize);
	      if ((lstemployee != null) && (!lstemployee.isEmpty()))
	      {
	        int count = 0;
	        int i = 2;
	        int j = 0;
	        int empCount = 1;
	        
	        List lEmpTotalContri = lObjNsdlNps.getEmployeeContriTotalList(yrCode, month, treasuryyno, flag);
	        String totalEmplyContri = null;
	        Double EmpleeContri = null;
	        Double EmplrContri = null;
	        Double EmpleeDHContri = null;
	        Double EmplrDHContri = null;
	        String totalEmplyerContri = null;
	        String totalEmplyDHContri = null;
	        String totalEmplyerDHContri = null;
	        Double totalEContri = null;
	        Double totalEDHContri = null;
	        String intEmpl = null;
	        String intEmplr = null;
	        String[] intEmp = null;
	        String[] intEmployee = null;
	        String[] intEmployer = null;
	        String[] contriEmployee = null;
	        String[] emplrContriintEmployer = null;
	        String[] totalDhsumsplit = null;
	        Double totalContribution = null;
	        Double GovContributionSum = null;
	        Double subcontributionSum = null;
	        Double TotalContributionsum = null;
	        Double totalEmpContribution = Double.valueOf(0.0D);
	        Double totalEmplrContribution = Double.valueOf(0.0D);
	        String[] overallAmt = null;
	        String[] overallDHAmt = null;
	        String[] totalempoverallAmt = null;
	        String[] totalempoverallDHAmt = null;
	        String[] totalemplroverallAmt = null;
	        String[] totalemplroverallDHAmt = null;
	        String monthId = null;
	      //$t 2019 25-11
	      HashMap sinDdo = new HashMap();
	        
	        if ((lEmpTotalContri != null) && (lEmpTotalContri.size() > 0))
	        {
	          this.gLogger.info("In Loop  " + lEmpTotalContri.get(0).toString());
	          
	          String[] totalAmtBH = lEmpTotalContri.get(0).toString().split("#");
	          this.gLogger.info("In Loop1  " + lEmpTotalContri.get(0).toString());
	          EmpleeContri = Double.valueOf(Double.parseDouble(totalAmtBH[0]));
	          totalEmplyContri = nosci(EmpleeContri.doubleValue());
	          this.gLogger.info("In Loop 2 " + totalEmplyContri);
	          EmplrContri = Double.valueOf(Double.parseDouble(totalAmtBH[1]));
	          totalEmplyerContri = nosci(EmplrContri.doubleValue());
	          this.gLogger.info("In Loop3  " + EmplrContri);
	          totalEContri = Double.valueOf(Double.parseDouble(totalEmplyContri) + Double.parseDouble(totalEmplyerContri));
	          this.gLogger.info("In Loop 4 " + totalEContri);
	        }
	        String totaloverallAmt = nosci(totalEContri.doubleValue());
	        this.gLogger.info("Out Loop totaloverallAmt" + totaloverallAmt);
	        
	        overallAmt = totaloverallAmt.toString().split("\\.");
	        this.gLogger.info("Out Loop overallAmt" + overallAmt[0]);
	        if (overallAmt.length != 1)
	        {
	          if (totaloverallAmt.equals("0"))
	          {
	            totaloverallAmt = "0.00";
	            this.gLogger.info("Out Loop in if 1 " + totaloverallAmt);
	          }
	          else if (overallAmt[1].length() == 1)
	          {
	            this.gLogger.info("Out Loop in if 2 " + totaloverallAmt);
	            totaloverallAmt = totaloverallAmt + "0";
	            this.gLogger.info("Out Loop in if 2 " + totaloverallAmt);
	          }
	          else if (overallAmt[1].length() > 2)
	          {
	            this.gLogger.info("Out Loop in if 3 " + totaloverallAmt);
	            totaloverallAmt = decRoundOff(totaloverallAmt);
	            this.gLogger.info("Out Loop in if 3 " + totaloverallAmt);
	          }
	          this.gLogger.info("Out Loop totaloverallAmt" + totaloverallAmt);
	        }
	        else
	        {
	          totaloverallAmt = totaloverallAmt + ".00";
	        }
	        totalempoverallAmt = totalEmplyContri.toString().split("\\.");
	        
	        this.gLogger.info("Out Loop totalempoverallAmt" + totalempoverallAmt[0]);
	        if (totalempoverallAmt.length != 1)
	        {
	          if (totalEmplyContri.equals("0")) {
	            totalEmplyContri = "0.00";
	          } else if (totalempoverallAmt[1].length() == 1) {
	            totalEmplyContri = totalEmplyContri + "0";
	          } else if (overallAmt[1].length() > 2) {
	            totalEmplyContri = decRoundOff(totalEmplyContri);
	          }
	          this.gLogger.info("Out Loop totalempoverallAmt" + totalempoverallAmt[0]);
	        }
	        else
	        {
	          totalEmplyContri = totalEmplyContri + ".00";
	        }
	        this.gLogger.info("Amount is **********  " + totalEmplyerContri.toString());
	        totalemplroverallAmt = totalEmplyerContri.toString().split("\\.");
	        this.gLogger.info("Out Loop totalempoverallAmt" + totalemplroverallAmt[0]);
	        if (totalemplroverallAmt.length != 1)
	        {
	          if (totalEmplyerContri.equals("0")) {
	            totalEmplyerContri = "0.00";
	          } else if (totalemplroverallAmt[1].length() == 1) {
	            totalEmplyerContri = totalEmplyerContri + "0";
	          } else if (overallAmt[1].length() > 2) {
	            totalEmplyerContri = decRoundOff(totalEmplyerContri);
	          }
	        }
	        else {
	          totalEmplyerContri = totalEmplyerContri + ".00";
	        }
	        this.gLogger.info("BH Employee amount is " + totalEmplyContri);
	        this.gLogger.info("BH Employer amount is " + totalEmplyerContri);
	        this.gLogger.info("BH total  amount is " + totaloverallAmt);
	        int temp = 0;
	        int emprecCount = 0;
	        
	        String[] lEmpCountContrDdo = lObjNsdlNps.getEmployeeCountDdoregNsdl(yrCode, month, treasuryyno, flag);
	        String[] lEmpTotalContrDdo = lObjNsdlNps.getEmployeeListDdoregNsdl(yrCode, month, treasuryyno, flag);
	        
	        this.gLogger.info("lEmpCountContrDdo length" + lEmpCountContrDdo.length);
	        this.gLogger.info("lEmpTotalContrDdo length" + lEmpTotalContrDdo.length);
	        
	        PrintWriter outputfile = this.response.getWriter();
	        for (Iterator it = lstemployee.iterator(); it.hasNext();)
	        {
	          count++;
	          Object[] lObj = (Object[])it.next();
	          
	          empname = lObj[0] != null ? lObj[0].toString() : "NA";
	          
	          dcpsid = lObj[1] != null ? lObj[1].toString() : "";
	          
	          this.gLogger.info("dcps Id ********" + dcpsid);
	          pranno = lObj[2] != null ? lObj[2].toString() : "";
	          
	          dtoRegNo = lObj[6] != null ? lObj[6].toString() : "";
	          
	          ddoRegNo = lObj[7] != null ? lObj[7].toString() : "";
	          
	          govEmpContri = lObj[3] != null ? lObj[3].toString() : "";
	          
	          subempContri = lObj[4] != null ? lObj[4].toString() : "";
	          
	          sdAmnt = lObj[8] != null ? lObj[8].toString() : "";
	          
	      //$t 2019 25-11 new
	        String allow = "nDone";
	        if (govEmpContri.equals("0.0") || govEmpContri.equals("0.00") || govEmpContri.equals("0")) {
	        if ((ddoRegNo != null) && !(sinDdo.containsKey(ddoRegNo))) {
	        sinDdo.put(ddoRegNo, govEmpContri);
	        allow="Done";
	        }
	        }
	          
	          
	          contriEmployee = govEmpContri.toString().split("\\.");
	          if (contriEmployee.length != 1)
	          {
	            if (govEmpContri.equals("0")) {
	              govEmpContri = "0.00";
	            } else if (contriEmployee.length > 1)
	            {
	              if (contriEmployee[1].length() == 1) {
	                govEmpContri = govEmpContri + "0";
	              }
	            }
	            else if (contriEmployee.length == 1) {
	              govEmpContri = govEmpContri + ".00";
	            }
	          }
	          else {
	            govEmpContri = govEmpContri + ".00";
	          }
	          emplrContriintEmployer = subempContri.toString().split("\\.");
	          if (emplrContriintEmployer.length != 1)
	          {
	            if (subempContri.equals("0")) {
	              subempContri = "0.00";
	            } else if (emplrContriintEmployer.length > 1)
	            {
	              if (emplrContriintEmployer[1].length() == 1) {
	                subempContri = subempContri + "0";
	              }
	            }
	            else if (emplrContriintEmployer.length == 1) {
	              subempContri = subempContri + ".00";
	            }
	          }
	          else {
	            subempContri = subempContri + ".00";
	          }
	          totalContribution = Double.valueOf(govEmpContri != null ? Double.parseDouble(govEmpContri) + Double.parseDouble(subempContri) : 0.0D + Double.parseDouble(subempContri));
	          
	          TotalContri = totalContribution.toString();
	          
	          countsum += count;
	          
	          GovContributionSum = Double.valueOf(govcontiSum != null ? Double.parseDouble(govcontiSum) + Double.parseDouble(govEmpContri) : 0.0D + Double.parseDouble(govEmpContri));
	          
	          govcontiSum = GovContributionSum.toString();
	          
	          subcontributionSum = Double.valueOf(subcontiSum != null ? Double.parseDouble(subcontiSum) + Double.parseDouble(subempContri) : 0.0D + Double.parseDouble(subempContri));
	          
	          subcontiSum = subcontributionSum.toString();
	          
	          TotalContributionsum = Double.valueOf(govcontiSum != null ? Double.parseDouble(govcontiSum) + Double.parseDouble(subcontiSum) : 0.0D + Double.parseDouble(subcontiSum));
	          
	          TotalContrisum = TotalContributionsum.toString();
	          
	          this.gLogger.info("i " + i);
	          this.gLogger.info("totalsize" + totalsize);
	          this.gLogger.info("j " + j);
	          this.gLogger.info("lEmpCountContrDdo.length" + lEmpCountContrDdo.length);
//	          if ((!prvDdoReg.equals(ddoRegNo)) && (j <= lEmpCountContrDdo.length))//previous $t 2019 25-11
	          if ((!prvDdoReg.equals(ddoRegNo)) && (j <= lEmpCountContrDdo.length)|| allow.equals("Done"))
	          {
	            if ((lEmpTotalContrDdo != null) && (lEmpTotalContrDdo.length > 0))
	            {
	              this.gLogger.info("j is *******************" + j);
	              
	              String[] totalAmtDH = lEmpTotalContrDdo[j].toString().split("#");
	              
	              EmpleeDHContri = Double.valueOf(Double.parseDouble(totalAmtDH[0]));
	              this.gLogger.info("EmpleeDHContri is *********" + EmpleeDHContri);
	              
	              totalEmplyDHContri = nosci(EmpleeDHContri.doubleValue());
	              this.gLogger.info("totalEmplyDHContri is *********" + totalEmplyDHContri);
	              EmplrDHContri = Double.valueOf(Double.parseDouble(totalAmtDH[1]));
	              totalEmplyerDHContri = nosci(EmplrDHContri.doubleValue());
	              totalEDHContri = Double.valueOf(Double.parseDouble(totalEmplyDHContri) + Double.parseDouble(totalEmplyerDHContri));
	              this.gLogger.info("totalEDHContri is *********" + totalEDHContri);
	            }
	            String totaloverDHallAmt = nosci(totalEDHContri.doubleValue());
	            
	            this.gLogger.info("totaloverDHallAmt is *********" + totaloverDHallAmt);
	            
	            overallDHAmt = totaloverDHallAmt.toString().split("\\.");
	            this.gLogger.info("totaloverDHallAmt is *********" + totaloverDHallAmt);
	            if (overallDHAmt.length != 1)
	            {
	              if (totaloverDHallAmt.equals("0")) {
	                totaloverDHallAmt = "0.00";
	              } else if (overallDHAmt[1].length() == 1) {
	                totaloverDHallAmt = totaloverDHallAmt + "0";
	              } else if (overallDHAmt[1].length() > 2) {
	                totaloverDHallAmt = decRoundOff(totaloverDHallAmt);
	              }
	            }
	            else {
	              totaloverDHallAmt = totaloverDHallAmt + ".00";
	            }
	            totalempoverallDHAmt = totalEmplyDHContri.toString().split("\\.");
	            this.gLogger.info("totalEmplyDHContri is *********" + totalEmplyDHContri);
	            if (totalempoverallDHAmt.length != 1)
	            {
	              if (totalEmplyDHContri.equals("0")) {
	                totalEmplyDHContri = "0.00";
	              } else if (totalempoverallDHAmt[1].length() == 1) {
	                totalEmplyDHContri = totalEmplyDHContri + "0";
	              } else if (totalempoverallDHAmt[1].length() > 2) {
	                totalEmplyDHContri = decRoundOff(totalEmplyDHContri);
	              }
	            }
	            else {
	              totalEmplyDHContri = totalEmplyDHContri + ".00";
	            }
	            totalemplroverallDHAmt = totalEmplyerDHContri.toString().split("\\.");
	            this.gLogger.info("totalemplroverallDHAmt is *********" + totalemplroverallDHAmt);
	            if (totalemplroverallDHAmt.length != 1)
	            {
	              if (totalEmplyerDHContri.equals("0")) {
	                totalEmplyerDHContri = "0.00";
	              } else if (totalemplroverallDHAmt[1].length() == 1) {
	                totalEmplyerDHContri = totalEmplyerDHContri + "0";
	              } else if (totalemplroverallDHAmt[1].length() > 2) {
	                totalEmplyerDHContri = decRoundOff(totalEmplyerDHContri);
	              }
	            }
	            else {
	              totalEmplyerDHContri = totalEmplyerDHContri + ".00";
	            }
	            this.gLogger.info("All is well");
	            
	            i++;
	            this.gLogger.info("All is well i" + i);
	            j++;
	            empCount = 1;
	            
	            this.gLogger.info("All is well j" + j);
	            if (j <= lEmpCountContrDdo.length)
	            {
	              Strbr.append(i + "^");
	              Strbr.append("DH^");
	              Strbr.append("1^");
	              Strbr.append(j + "^");
	              Strbr.append(ddoRegNo + "^");
	              this.gLogger.info("All is well ddoRegNo" + ddoRegNo);
	              this.gLogger.info("All is well emprecCount" + emprecCount);
	              this.gLogger.info("All is well lEmpCountContrDdo length" + lEmpCountContrDdo.length);
	              this.gLogger.info("All is well lEmpCountContrDdo" + lEmpCountContrDdo);
	              this.gLogger.info("All is well lEmpCountContrDdo length" + lEmpCountContrDdo.length);
	              
	              Long EmpCount = Long.valueOf(Long.parseLong(lEmpCountContrDdo[emprecCount]));
	              this.gLogger.info("All is well lEmpCountContrDdo length" + lEmpCountContrDdo.length);
	              
	              Strbr.append(EmpCount + "^");
	              this.gLogger.info("All is well EmpCount " + EmpCount);
	              Strbr.append(totalEmplyDHContri + "^");
	              this.gLogger.info("All is well totalEmplyDHContri " + totalEmplyDHContri);
	              Strbr.append(totalEmplyerDHContri + "^");
	              this.gLogger.info("All is well totalEmplyerDHContri " + totalEmplyerDHContri);
	              Strbr.append("^");
	              
	              lObjNsdlNps.insertDHDetails(i, "DH", "1", j, ddoRegNo, EmpCount, totalEmplyDHContri, totalEmplyerDHContri, BatchId);
	              
	              Strbr.append("\r\n");
	              temp++;
	              emprecCount++;
	            }
	            this.gLogger.info("Strbr for DH is" + Strbr.toString());
	            allow="nDone";
	          }
	          prvDdoReg = ddoRegNo;
	          i++;
	          if ((lstemployee != null) && (!lstemployee.equals("")) && (j <= lEmpCountContrDdo.length))
	          {
	            Strbr.append(i + "^");
	            Strbr.append("SD^");
	            Strbr.append("1^");
	            Strbr.append(j + "^");
	            Strbr.append(empCount + "^");
	            Strbr.append(pranno + "^");
	            Strbr.append(govEmpContri + "^");
	            Strbr.append(subempContri + "^");
	            Strbr.append("^");
	            Strbr.append(TotalContri + "0" + "^");
	            
	            double conti1 = 0.0D;
	            double conti2 = 0.0D;
	            double conti3 = 0.0D;
	            conti1 = Double.parseDouble(govEmpContri.toString());
	            conti3 = Double.parseDouble(subempContri.toString());
	            
	            //conti2 = Math.round(conti3 - (conti1 * 0.4D + conti1));
	            
	            String contribType = lObjNsdlNps.getContribType(dcpsid);
	            if (conti1 == 0.0D) {/////////$t  25-3-2021 
	            	Strbr.append("A^" + month + "^" + yrCode + "^");
	            }else if ((sdAmnt != null) && (!"".equalsIgnoreCase(sdAmnt))) {
	              Strbr.append("A^" + month + "^" + yrCode + "^");
	            }else {
	              Strbr.append(contribType + "^" + month + "^" + yrCode + "^");
	            }
	            if (Long.parseLong(month) < 10L) {
	              monthId = "0" + month;
	            } else if (Long.parseLong(month) >= 10L) {
	              monthId = month;
	            }
	            String finyear = "";
	            if (Long.parseLong(month) > 2L) {
	              finyear = yrCode + "-" + (Long.parseLong(yrCode) + 1L);
	            } else {
	              finyear = Long.parseLong(yrCode) - 1L + "-" + yrCode;
	            }
	            Strbr.append("Contribution for " + month + "/" + yrCode + "^");
	            if ((sdAmnt != null) && (!"".equalsIgnoreCase(sdAmnt))) {
	              lObjNsdlNps.insertSDDetails(i, "SD", "1", j, empCount, pranno, govEmpContri, subempContri, TotalContri + "0", "A^" + monthId + "^" + yrCode, "Contribution fOr " + month + "/" + yrCode, BatchId, ddoRegNo);
	            }else {
	              lObjNsdlNps.insertSDDetails(i, "SD", "1", j, empCount, pranno, govEmpContri, subempContri, TotalContri + "0", contribType + "^" + monthId + "^" + yrCode, "Contribution fOr " + month + "/" + yrCode, BatchId, ddoRegNo);
	            }
	            Strbr.append("\r\n");
	          }
	          empCount++;
	        }
	        this.gLogger.info("All fine till nmow");
	        
	        String lineSeperator = "\r\n";
	        
	        String os = System.getProperty("os.name");
	        this.gLogger.info("All ok till now is ************os*******" + os);
	        if (os.toLowerCase().indexOf("unix") > 0) {
	          lineSeperator = "\n";
	        } else if (os.toLowerCase().indexOf("windows") > 0) {
	          lineSeperator = "\r\n";
	        }
	        this.gLogger.info("All ok till now is *******************");
	        HttpServletResponse response = 
	          (HttpServletResponse)inputMap.get("responseObj");
	        
	        NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
	        
	        dtoRegNo = lObjNsdlDAO.getDtoRegNo(treasuryyno.substring(0, 2));
	        
	        getFileHeader(outputfile, dtoRegNo);
	        
	        this.gLogger.info("outputfile is *******************" + outputfile);
	        this.gLogger.info("totalsize is *******************" + totalsize);
	        this.gLogger.info("countReg is *******************" + countReg);
	        this.gLogger.info("totalEmplyContri is *******************" + totalEmplyContri);
	        this.gLogger.info("totalEmplyerContri is *******************" + totalEmplyerContri);
	        this.gLogger.info("totaloverallAmt is *******************" + totaloverallAmt);
	        this.gLogger.info("BatchId is *******************" + BatchId);
	        this.gLogger.info("yrCode is *******************" + yrCode);
	        this.gLogger.info("month is *******************" + month);
	        
	        getBatchHeader(outputfile, totalsize, countReg.longValue(), totalEmplyContri, totalEmplyerContri, totaloverallAmt, BatchId, yrCode, month, dtoRegNo);
	        
	        List nsdlDeatils = lObjNsdlNps.getAllData(yrCode, month, treasuryyno);
	        
	        List lLstYears = lObjNsdlDAO.getFinyear();
	        DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
	          .getSessionFactory());
	        List lLstMonths = lObjDcpsCommonDAO.getMonths();
	        List subTr = lObjNsdlDAO.getAllSubTreasury(this.gStrLocationCode);
	        inputMap.put("size", Integer.valueOf(nsdlDeatils.size()));
	        inputMap.put("nsdlDeatils", nsdlDeatils);
	        inputMap.put("selMonth", month);
	        inputMap.put("selYear", yrCode);
	        inputMap.put("lLstYears", lLstYears);
	        inputMap.put("lLstMonths", lLstMonths);
	        inputMap.put("subTr", subTr);
	        inputMap.put("subTrCode", treasuryyno);
	        inputMap.put("digiActivate", Integer.valueOf(digiActivate));
	      }
	    }
	    catch (Exception e)
	    {
	      this.gLogger.info("Error occure in createTxtFile()" + e);
	      e.printStackTrace();
	      resObj.setResultCode(-1);
	    }
	    resObj.setResultCode(0);
	    resObj.setResultValue(inputMap);
	    
	    resObj.setViewName("NPSVALIDATE");
	    return resObj;
	  }
	
	
/*
 //$t 2019 dr changes 8 oct 
	public ResultObject genNsdlTxtFile( 
			Map<String, Object> inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		gLogger.info("in genNsdlTxtFile---------------------- ");

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
		String sdAmnt=null;
		String prvDdoReg="AAA";
		String treasuryyno=null;
		//$t 2019
		ResultSet lstemployee = null;
//		private ArrayList[] lstemployee = (ArrayList<Integer>[]) new Object[10];
		String yrCode=null;
		String month=null;
		List nsdlData=null;
		String BatchId=null;
		Long nwbatchId=null;
		String nwTranBatchId=null;
		String [] fyYrsplit=null;
		String tranId=null;
		String subTrCode="";
		int flag=0;
		int digiActivate=0;
		try {

			setSessionInfo(inputMap);
				HttpServletRequest request = (HttpServletRequest) inputMap
			.get("requestObj");
			 
			treasuryyno=gStrLocationCode;
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

			if(StringUtility.getParameter("yearId", request) != null  && !StringUtility.getParameter("yearId", request).equals("") && Long.parseLong(StringUtility.getParameter("yearId", request))!=-1 &&
					StringUtility.getParameter("monthId", request) != null  && !StringUtility.getParameter("monthId", request).equals("") && Long.parseLong(StringUtility.getParameter("monthId", request))!=-1
				&& StringUtility.getParameter("subTr", request) != null  && !StringUtility.getParameter("subTr", request).equals("") && Long.parseLong(StringUtility.getParameter("subTr", request))!=-1)

			{	
				extn = "txt";

				extnFlag = StringUtility.getParameter("flagFile", request).trim();
				yrCode=StringUtility.getParameter("yearId", request) ;
				month=StringUtility.getParameter("monthId", request) ;
				treasuryyno=StringUtility.getParameter("subTr", request) ;
				
			}
			if (treasuryyno.equals("1"))
	        {
				treasuryyno="7101";
	        	flag=1;
	        }
	        else if (treasuryyno.equals("2")) {
	        	treasuryyno="7101";
	        	flag=2;
			}
			System.out.println( extnFlag+".........."+yrCode+"........."+month+"..........."+treasuryyno);
			
			
			
			NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
			digiActivate=lObjNsdlNps.getDigiActivationDtls(gStrLocationCode);
			//code for batch Id.
			String midFix="";
			String finalFix="";
			if(month.length()==1){
				midFix="0";	
			}
			String batchIdPrefix=treasuryyno+yrCode+midFix+month;
			String countBatchId=lObjNsdlNps.getbatchIdCount(batchIdPrefix);
			if(countBatchId.length()==1){
				finalFix="00";	
			}
			if(countBatchId.length()==2){
				finalFix="0";	
			}

			BatchId=batchIdPrefix+finalFix+countBatchId;

			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			NsdlSrkaFileGeneDAOImpl lObjAlIndSer = new NsdlSrkaFileGeneDAOImpl(null,serv.getSessionFactory());

			Object[] lyrObj = null;



			lstemployee=(ResultSet) lObjNsdlNps.getEmployeeListNsdl(yrCode,month,treasuryyno,flag);//change
			Long countReg=lObjNsdlNps.getDDoRegCount(yrCode,month,treasuryyno,flag);//change
			//---------- Added by Ashish--------
			gLogger.info("BH DDO  REG count is "+countReg);

			//boolean Batch=false;





			//BH begin 		
			int totalsize = ((ByteArrayOutputStream) lstemployee).size();//$t 2019
			gLogger.info("BH  total employee size is "+totalsize);
			
			//$t 2019
			boolean b=lstemployee.next();
			
			
			if (b) //$t 2019

			{			

				int count = 0;	
				int  i = 2;
				int j=0;
				int empCount=1;

				List lEmpTotalContri=lObjNsdlNps.getEmployeeContriTotalList(yrCode,month,treasuryyno,flag);//change
				String totalEmplyContri=null;
				Double EmpleeContri=null;
				Double EmplrContri=null;
				Double EmpleeDHContri=null;
				Double EmplrDHContri=null;
				String totalEmplyerContri=null;
				String totalEmplyDHContri=null;
				String totalEmplyerDHContri=null;
				Double	 totalEContri=null;
				Double totalEDHContri=null;
				String intEmpl=null;
				String intEmplr=null;
				String [] intEmp=null;
				String [] intEmployee=null;
				String [] intEmployer=null;
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
				String [] overallDHAmt=null;
				String [] totalempoverallAmt=null;
				String [] totalempoverallDHAmt=null;
				String [] totalemplroverallAmt=null;
				String [] totalemplroverallDHAmt=null;
				String monthId=null;
				//	Map<String, Long> lMapEmpeeCountDtls = null;
				if(lEmpTotalContri!=null && lEmpTotalContri.size()>0)			 
				{

					gLogger.info("In Loop  "+lEmpTotalContri.get(0).toString());

					String[] totalAmtBH=lEmpTotalContri.get(0).toString().split("#");
					gLogger.info("In Loop1  "+lEmpTotalContri.get(0).toString());
					EmpleeContri=Double.parseDouble(totalAmtBH[0]);
					totalEmplyContri=nosci(EmpleeContri);
					gLogger.info("In Loop 2 "+totalEmplyContri);
					EmplrContri = Double.parseDouble(totalAmtBH[1]);
					totalEmplyerContri=nosci(EmplrContri);
					gLogger.info("In Loop3  "+EmplrContri);
					totalEContri =Double.parseDouble(totalEmplyContri)+Double.parseDouble(totalEmplyerContri);
					gLogger.info("In Loop 4 "+totalEContri);
				}


				//total
				String totaloverallAmt=nosci(totalEContri);
				gLogger.info("Out Loop totaloverallAmt"+totaloverallAmt);

				overallAmt=totaloverallAmt.toString().split("\\.");
				gLogger.info("Out Loop overallAmt"+overallAmt[0]);
				if (overallAmt.length !=1){
					if(totaloverallAmt.equals("0")){
						totaloverallAmt="0.00";
						gLogger.info("Out Loop in if 1 "+totaloverallAmt);
					}

					else if(overallAmt[1].length()==1)
					{
						gLogger.info("Out Loop in if 2 "+totaloverallAmt);
						totaloverallAmt=totaloverallAmt+"0";
						gLogger.info("Out Loop in if 2 "+totaloverallAmt);
					}

					else if(overallAmt[1].length()>2)
					{
						gLogger.info("Out Loop in if 3 "+totaloverallAmt);
						totaloverallAmt= decRoundOff(totaloverallAmt);
						gLogger.info("Out Loop in if 3 "+totaloverallAmt);

					}
					gLogger.info("Out Loop totaloverallAmt"+totaloverallAmt);
				}
				else{
					totaloverallAmt=totaloverallAmt+".00";

				}
				//emp 


				totalempoverallAmt=totalEmplyContri.toString().split("\\.");

				gLogger.info("Out Loop totalempoverallAmt"+totalempoverallAmt[0]);

				if(totalempoverallAmt.length!=1){
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

					gLogger.info("Out Loop totalempoverallAmt"+totalempoverallAmt[0]);
				}


				else{
					totalEmplyContri=totalEmplyContri+".00";

				}
				//emplr	 
				gLogger.info("Amount is **********  "+totalEmplyerContri.toString());
				totalemplroverallAmt=totalEmplyerContri.toString().split("\\.");
				gLogger.info("Out Loop totalempoverallAmt"+totalemplroverallAmt[0]);
				if(totalemplroverallAmt.length!=1){
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
				}
				else{

					totalEmplyerContri=totalEmplyerContri+".00";
				}

				gLogger.info("BH Employee amount is "+totalEmplyContri);
				gLogger.info("BH Employer amount is "+totalEmplyerContri);
				gLogger.info("BH total  amount is "+totaloverallAmt);
				int temp=0;
				int emprecCount=0;

				//query to insert the data in bh details table
				//BH end 		

				String[] lEmpCountContrDdo=lObjNsdlNps.getEmployeeCountDdoregNsdl(yrCode,month,treasuryyno,flag);
				String[] lEmpTotalContrDdo=lObjNsdlNps.getEmployeeListDdoregNsdl(yrCode,month,treasuryyno,flag);

				gLogger.info("lEmpCountContrDdo length"+lEmpCountContrDdo.length);
				gLogger.info("lEmpTotalContrDdo length"+lEmpTotalContrDdo.length);

				
//				while(lRs2.next()){
//					
//					String PPO_NO=String.valueOf(lRs2.getLong("PPO_NO"));
//					System.out.println("PPO NO "+PPO_NO);
//					String PENSIONER_CODE=String.valueOf(lRs2.getLong("PENSIONER_CODE"));
//					System.out.println("PENSIONER_CODE NO  "+PENSIONER_CODE);
//					String LOCATION_CODE=String.valueOf(lRs2.getLong("LOCATION_CODE"));
//					System.out.println("LOCATION_CODE NO "+LOCATION_CODE);
//				}
				

				PrintWriter outputfile =response.getWriter();
				while(lstemployee.next()){
					
					empname=String.valueOf(lstemployee.getString("EMP_NAME"));
					System.out.println("empname NO "+empname);
					dcpsid=String.valueOf(lstemployee.getString("DCPS_ID"));
					System.out.println("dcpsid NO  "+dcpsid);
					pranno=String.valueOf(lstemployee.getString("PRAN_NO"));
					System.out.println("pranno NO "+pranno);
					govEmpContri=String.valueOf(lstemployee.getDouble("EMP_AMOUNT"));
					System.out.println("govEmpContri NO "+govEmpContri);
					subempContri=String.valueOf(lstemployee.getDouble("EMPLR_AMOUNT"));
					System.out.println("subempContri NO  "+subempContri);
					//pranno=String.valueOf(lstemployee.getString("LOC_NAME"));
					//System.out.println("LOCATION_CODE NO "+pranno);
					dtoRegNo=String.valueOf(lstemployee.getInt("DTO_REG_NO"));
					System.out.println("dtoRegNo NO  "+dtoRegNo);
					ddoRegNo=String.valueOf(lstemployee.getString("DDO_REG_NO"));
					System.out.println("ddoRegNo NO "+ddoRegNo);
					sdAmnt=String.valueOf(lstemployee.getDouble("sd_amnt"));
					System.out.println("sdAmnt NO  "+sdAmnt);
					
				

				    	
					count++;
					
					
					
//					Object[] lObj = (Object[]) it.next();
//
//
//					empname = (lObj[0] != null) ? lObj[0].toString(): "NA";
//
//
//					dcpsid = ((lObj[1] != null)? lObj[1].toString() :"");
//
//					gLogger.info("dcps Id ********"+dcpsid);
//					pranno = ((lObj[2] != null)? lObj[2].toString() :"");
//
//					dtoRegNo = (lObj[6] != null) ? lObj[6].toString() : "";						
//
//					ddoRegNo = (lObj[7] != null) ? lObj[7].toString() : "";		
//
//					govEmpContri = (lObj[3] != null) ? lObj[3].toString() : "";		
//
//					subempContri = (lObj[4] != null) ? lObj[4].toString() : "";	
//					
//					sdAmnt=(lObj[8] != null) ? lObj[8].toString() : "";		
	 
					
					contriEmployee=govEmpContri.toString().split("\\.");
					if(contriEmployee.length!=1){
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
					}

					else{
						govEmpContri=govEmpContri+".00";	

					}

					emplrContriintEmployer=subempContri.toString().split("\\.");
					if(emplrContriintEmployer.length!=1){
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
					}
					else{
						subempContri=subempContri+".00";
					}



					totalContribution = (govEmpContri != null) ? Double.parseDouble(govEmpContri) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri) ;

					TotalContri=totalContribution.toString();


					countsum = countsum + count;


					GovContributionSum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(govEmpContri) : 0.00 + Double.parseDouble(govEmpContri);

					govcontiSum=GovContributionSum.toString();

					subcontributionSum = (subcontiSum != null) ? Double.parseDouble(subcontiSum) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri);
					
					subcontiSum=subcontributionSum.toString();

					TotalContributionsum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(subcontiSum) : 0.00 +Double.parseDouble(subcontiSum);
			
					TotalContrisum=TotalContributionsum.toString();


					gLogger.info("i "+i);
					gLogger.info("totalsize"+totalsize);
					gLogger.info("j "+j);
					gLogger.info("lEmpCountContrDdo.length"+lEmpCountContrDdo.length);
					
					
					
					
					if(!prvDdoReg.equals(ddoRegNo) && j <= lEmpCountContrDdo.length){

						if(lEmpTotalContrDdo!=null && lEmpTotalContrDdo.length>0)			 
						{
							gLogger.info("j is *******************"+j);

							String[] totalAmtDH=lEmpTotalContrDdo[j].toString().split("#");

							EmpleeDHContri=Double.parseDouble(totalAmtDH[0]);
							gLogger.info("EmpleeDHContri is *********"+EmpleeDHContri);	

							totalEmplyDHContri=nosci(EmpleeDHContri);
							gLogger.info("totalEmplyDHContri is *********"+totalEmplyDHContri);	
							EmplrDHContri = Double.parseDouble(totalAmtDH[1]);
							totalEmplyerDHContri=nosci(EmplrDHContri);
							totalEDHContri =Double.parseDouble(totalEmplyDHContri)+Double.parseDouble(totalEmplyerDHContri);
							gLogger.info("totalEDHContri is *********"+totalEDHContri);	

						}
						String totaloverDHallAmt=nosci(totalEDHContri);

						//total
						gLogger.info("totaloverDHallAmt is *********"+totaloverDHallAmt);	

						overallDHAmt=totaloverDHallAmt.toString().split("\\.");
						gLogger.info("totaloverDHallAmt is *********"+totaloverDHallAmt);	
						if(overallDHAmt.length!=1) {
							if(totaloverDHallAmt.equals("0")){
								totaloverDHallAmt="0.00";
							}

							else if(overallDHAmt[1].length()==1)
							{
								totaloverDHallAmt=totaloverDHallAmt+"0";
							}

							else if(overallDHAmt[1].length()>2)
							{
								totaloverDHallAmt= decRoundOff(totaloverDHallAmt);

							}
						}

						else{
							totaloverDHallAmt=totaloverDHallAmt+".00";

						}

						//emp 


						totalempoverallDHAmt=totalEmplyDHContri.toString().split("\\.");
						gLogger.info("totalEmplyDHContri is *********"+totalEmplyDHContri);	
						if(totalempoverallDHAmt.length!=1){
							if(totalEmplyDHContri.equals("0")){
								totalEmplyDHContri="0.00";
							}

							else if(totalempoverallDHAmt[1].length()==1)
							{
								totalEmplyDHContri=totalEmplyDHContri+"0";
							}

							else if(totalempoverallDHAmt[1].length()>2)
							{

								totalEmplyDHContri=  decRoundOff(totalEmplyDHContri);

							}
						}
						else{
							totalEmplyDHContri=totalEmplyDHContri+".00";

						}

						//emplr	 

						totalemplroverallDHAmt=totalEmplyerDHContri.toString().split("\\.");
						gLogger.info("totalemplroverallDHAmt is *********"+totalemplroverallDHAmt);
						if(totalemplroverallDHAmt.length!=1){
							if(totalEmplyerDHContri.equals("0")){
								totalEmplyerDHContri="0.00";
							}

							else if(totalemplroverallDHAmt[1].length()==1)
							{
								totalEmplyerDHContri=totalEmplyerDHContri+"0";
							}
							else if(totalemplroverallDHAmt[1].length()>2)
							{

								totalEmplyerDHContri=  decRoundOff(totalEmplyerDHContri);
							}

						}
						else{

							totalEmplyerDHContri=totalEmplyerDHContri+".00";
						}
						gLogger.info("All is well");	


						i=i+1;
						gLogger.info("All is well i"+i);
						j=j+1;
						empCount=1;

						gLogger.info("All is well j"+j);
						if(j<= lEmpCountContrDdo.length)
						{
						Strbr.append(i+"^");		
						Strbr.append("DH"+"^"); 	
						Strbr.append("1"+"^"); 	
						Strbr.append(j+"^"); 	
						Strbr.append(ddoRegNo+"^");
						gLogger.info("All is well ddoRegNo"+ddoRegNo);
						gLogger.info("All is well emprecCount"+emprecCount);
						gLogger.info("All is well lEmpCountContrDdo length"+lEmpCountContrDdo.length);
						gLogger.info("All is well lEmpCountContrDdo"+lEmpCountContrDdo);
						gLogger.info("All is well lEmpCountContrDdo length"+lEmpCountContrDdo.length);
				
						Long EmpCount=Long.parseLong(lEmpCountContrDdo[emprecCount]);
						gLogger.info("All is well lEmpCountContrDdo length"+lEmpCountContrDdo.length);
						
						Strbr.append(EmpCount+"^"); 
						gLogger.info("All is well EmpCount "+EmpCount );
						Strbr.append(totalEmplyDHContri+"^"); 	
						gLogger.info("All is well totalEmplyDHContri "+totalEmplyDHContri );
						Strbr.append(totalEmplyerDHContri+"^"); 
						gLogger.info("All is well totalEmplyerDHContri "+totalEmplyerDHContri );
						Strbr.append("^"); 		 

						lObjNsdlNps.insertDHDetails(i,"DH","1",j,ddoRegNo,EmpCount,totalEmplyDHContri,totalEmplyerDHContri,BatchId);
						//Strbr.append("\n");	
						Strbr.append("\r\n");
						temp++;
						emprecCount++;
						}
						gLogger.info("Strbr for DH is"+Strbr.toString());

					}
					prvDdoReg=ddoRegNo;
					++i;

					if(lstemployee!=null && !lstemployee.equals("") && j<= lEmpCountContrDdo.length) 
					{

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
					
						double conti1=0.0;
						double conti2=0.0;
						double conti3=0.0;
						conti1=Double.parseDouble(govEmpContri.toString());
						conti3=Double.parseDouble(subempContri.toString());
						
						conti2 = Math.round(conti3-((conti1*0.40)+conti1));
						
						
						
						System.out.println("conti1 emp-->"+conti1+"conti3 empr-->"+conti3+"conti2 arr-->"+conti2);
						
						//Changes done by ashish on 13 aug 2015 for Changes in SD "A" changes to "C"
						//Strbr.append("A"+"^^^");
						String contribType=lObjNsdlNps.getContribType(dcpsid);
						if(sdAmnt!=null && !"".equalsIgnoreCase(sdAmnt))
						{
							Strbr.append("A"+"^"+month+"^"+yrCode+"^");
						}
						else
						{
							if (conti2 > 0.0) {
								Strbr.append("A"+"^"+month+"^"+yrCode+"^");
							}
							else {
								Strbr.append(contribType+"^"+month+"^"+yrCode+"^");
							}
							
						}
						
						
						//end
						
						if(Long.parseLong(month) < 10){
							monthId="0"+month;
						}
						else if (Long.parseLong(month) >= 10){
							monthId=month;
						}
						
						String finyear="";
						if(Long.parseLong(month)>2)
							finyear= yrCode+"-"+(Long.parseLong(yrCode)+1);
						else
							finyear=(Long.parseLong(yrCode)-1) +"-"+yrCode ;

						Strbr.append("Contribution for "+month+"/"+yrCode+"^");
						//Changes done by ashish on 13 aug 2015 for Changes in SD "A" changes to "C"
						
						if(sdAmnt!=null && !"".equalsIgnoreCase(sdAmnt))
						{
							lObjNsdlNps.insertSDDetails(i,"SD","1",j,empCount,pranno,govEmpContri,subempContri,TotalContri+"0","A"+"^"+monthId+"^"+yrCode,"Contribution for "+month+"/"+yrCode+"",BatchId,ddoRegNo);
						}
						else
						{
							if (conti2>0.0) {
								lObjNsdlNps.insertSDDetails(i,"SD","1",j,empCount,pranno,govEmpContri,subempContri,TotalContri+"0","A"+"^"+monthId+"^"+yrCode,"4% Employer Arrears Contribution for "+month+"/"+yrCode+"",BatchId,ddoRegNo);
							} else {
								lObjNsdlNps.insertSDDetails(i,"SD","1",j,empCount,pranno,govEmpContri,subempContri,TotalContri+"0",contribType+"^"+monthId+"^"+yrCode,"Contribution for "+month+"/"+yrCode+"",BatchId,ddoRegNo);
							}
							
						}
						
							//	end 
						Strbr.append("\r\n");


					}


					++empCount;
					

				}



				gLogger.info("All fine till nmow");
				//new line 
				String lineSeperator ="\r\n"; 
				//System.getProperty("line.separator");.....not working


				String os = System.getProperty("os.name");
				gLogger.info("All ok till now is ************os*******"+os);

				if (os.toLowerCase().indexOf("unix") > 0){
					lineSeperator="\n";

				} else if (os.toLowerCase().indexOf("windows") > 0){
					lineSeperator ="\r\n"; 

				} else {

				}

				gLogger.info("All ok till now is *******************");
				HttpServletResponse response = (HttpServletResponse) inputMap
				.get("responseObj");
				
				NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
				System.out.println("treasury code is "+treasuryyno.substring(0, 2));
				 dtoRegNo=lObjNsdlDAO.getDtoRegNo(treasuryyno.substring(0, 2));
				 
				getFileHeader(outputfile,dtoRegNo);


				gLogger.info("outputfile is *******************"+outputfile);
				gLogger.info("totalsize is *******************"+totalsize);
				gLogger.info("countReg is *******************"+countReg);
				gLogger.info("totalEmplyContri is *******************"+totalEmplyContri);
				gLogger.info("totalEmplyerContri is *******************"+totalEmplyerContri);
				gLogger.info("totaloverallAmt is *******************"+totaloverallAmt);
				gLogger.info("BatchId is *******************"+BatchId);
				gLogger.info("yrCode is *******************"+yrCode);
				gLogger.info("month is *******************"+month);
				
				
			
				 getBatchHeader(outputfile,totalsize,countReg,totalEmplyContri,totalEmplyerContri,totaloverallAmt,BatchId,yrCode,month,dtoRegNo);

				List nsdlDeatils=lObjNsdlNps.getAllData(yrCode,month,treasuryyno);
				
				List lLstYears = lObjNsdlDAO.getFinyear();
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
						.getSessionFactory());
				List lLstMonths=lObjDcpsCommonDAO.getMonths();
				List subTr=lObjNsdlDAO.getAllSubTreasury(gStrLocationCode);
				inputMap.put("size", nsdlDeatils.size());
				inputMap.put("nsdlDeatils", nsdlDeatils);
				inputMap.put("selMonth", month);
				inputMap.put("selYear", yrCode);
				inputMap.put("lLstYears", lLstYears);
				inputMap.put("lLstMonths", lLstMonths);
				inputMap.put("subTr", subTr);
				inputMap.put("subTrCode", treasuryyno);
				inputMap.put("digiActivate", digiActivate);
				
				}
			}
					
	

		catch (Exception e)
		{
			gLogger.info("Error occure in createTxtFile()"+e);
			e.printStackTrace();
			resObj.setResultCode(ErrorConstants.ERROR);


		}
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);

		resObj.setViewName("NPSVALIDATE");
		return resObj;

	}

*/	
	
	public ResultObject genNsdlTxtFileForDeputation(
			Map<String, Object> inputMap)
	{


		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");
		gLogger.info("in genNsdlTxtFile---------------------- ");

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
		String sdamnt=null;
		String prvDdoReg="AAA";
		String treasuryyno=null;
		List lstemployee = null;
		String yrCode=null;
		Long yearId=0l;
		String month=null;
		List nsdlData=null;
		String BatchId=null;
		Long nwbatchId=null;
		String nwTranBatchId=null;
		String [] fyYrsplit=null;
		String tranId=null;
		String subTrCode="";
		String finYearCode="";
		Long finYearId=0l;
		long lmonthId=0l;
		String ddoCodeTr="";
		int digiActivate=0;
		try {

			setSessionInfo(inputMap);
			/*	HttpServletRequest request = (HttpServletRequest) inputMap
			.get("requestObj");
			 */
			gLogger.error("inside");
			NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
			treasuryyno=gStrLocationCode;
			digiActivate=lObjNsdlNps.getDigiActivationDtls(gStrLocationCode);
			ServiceLocator serv = (ServiceLocator) inputMap.get("serviceLocator");

			if(StringUtility.getParameter("yearId", request) != null  && !StringUtility.getParameter("yearId", request).equals("") && Long.parseLong(StringUtility.getParameter("yearId", request))!=-1 &&
					StringUtility.getParameter("monthId", request) != null  && !StringUtility.getParameter("monthId", request).equals("") && Long.parseLong(StringUtility.getParameter("monthId", request))!=-1
				&& StringUtility.getParameter("subTr", request) != null  && !StringUtility.getParameter("subTr", request).equals("") && Long.parseLong(StringUtility.getParameter("subTr", request))!=-1)

			{	
				extn = "txt";

				extnFlag = StringUtility.getParameter("flagFile", request).trim();
				yrCode=StringUtility.getParameter("yearId", request) ;
				yearId=Long.parseLong(StringUtility.getParameter("yearId", request) );
				month=StringUtility.getParameter("monthId", request) ;
				lmonthId=Long.parseLong(StringUtility.getParameter("monthId", request) );
				treasuryyno=StringUtility.getParameter("subTr", request) ;
				
				List DDOList = lObjNsdlNps.getTreasuryDdoCode(Long.parseLong(gStrLocationCode));
				if(DDOList!=null && DDOList.size()>0 && DDOList.get(0)!=null)
				{
					Object[] obj=(Object[]) DDOList.get(0);
					if(obj[0]!=null && obj[1]!=null)
					{
						ddoCodeTr=obj[0].toString();
					
					}
				}
				/*if(ddoCodeTr.equals("1111222222")){
					ddoCodeTr="9101005555";
				}*/
				
				if(lmonthId < 4)
				{
					finYearCode=String.valueOf(yearId-1);
					
				}
				else
				{
					finYearCode=String.valueOf(yearId);
				}
				finYearId=lObjNsdlNps.getFinYearId(finYearCode);
				gLogger.error("fin year");
			}
			
			//code for batch Id.
			String midFix="";
			String finalFix="";
			if(month.length()==1){
				midFix="0";	
			}
			String batchIdPrefix=treasuryyno+yrCode+midFix+month;
			String countBatchId=lObjNsdlNps.getbatchIdCount(batchIdPrefix);
			gLogger.error("batch");
			if(countBatchId.length()==1){
				finalFix="00";	
			}
			if(countBatchId.length()==2){
				finalFix="0";	
			}

			BatchId=batchIdPrefix+finalFix+countBatchId+'D';

			DcpsCommonDAO objDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv.getSessionFactory());

			NsdlSrkaFileGeneDAOImpl lObjAlIndSer = new NsdlSrkaFileGeneDAOImpl(null,serv.getSessionFactory());

			Object[] lyrObj = null;


			String trDdoReg=lObjNsdlNps.getDDORegNo(ddoCodeTr);
			lstemployee=lObjNsdlNps.getEmployeeListNsdlForDeputation(finYearId.toString(),month,treasuryyno,yrCode,ddoCodeTr,trDdoReg);//done
			//Long countReg=lObjNsdlNps.getDDoRegCount(yrCode,month,treasuryyno);
		
			Long countReg=lObjNsdlNps.getDDoRegCountDeputation(finYearId.toString(),month,treasuryyno,yrCode,ddoCodeTr);
			//---------- Added by Ashish--------
			gLogger.error("BH DDO  REG count is "+countReg);

			//boolean Batch=false;





			//BH begin 		
			int totalsize = lstemployee.size();
			gLogger.error("BH  total employee size is "+totalsize);
			if (lstemployee != null && !lstemployee.isEmpty()) 

			{			

				int count = 0;	
				int  i = 2;
				int j=0;
				int empCount=1;

				List lEmpTotalContri=lObjNsdlNps.getEmployeeContriTotalListForDeputation(finYearId.toString(),month,treasuryyno,yrCode,ddoCodeTr);
				
//				Object []ElObj = (Object[]) lEmpTotalContri.get(0);
//				System.out.println("-->"+ElObj[0]);
//				System.out.println("-->"+ElObj[1]);
//				System.out.println("-->"+ElObj[2]);
//				System.out.println("-->"+ElObj[3]);

				
				String totalEmplyContri=null;
				Double EmpleeContri=null;
				Double EmplrContri=null;
				Double EmpleeDHContri=null;
				Double EmplrDHContri=null;
				String totalEmplyerContri=null;
				String totalEmplyDHContri=null;
				String totalEmplyerDHContri=null;
				Double	 totalEContri=null;
				Double totalEDHContri=null;
				String intEmpl=null;
				String intEmplr=null;
				String [] intEmp=null;
				String [] intEmployee=null;
				String [] intEmployer=null;
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
				String [] overallDHAmt=null;
				String [] totalempoverallAmt=null;
				String [] totalempoverallDHAmt=null;
				String [] totalemplroverallAmt=null;
				String [] totalemplroverallDHAmt=null;
				String monthId=null;
				//	Map<String, Long> lMapEmpeeCountDtls = null;
				if(lEmpTotalContri!=null && lEmpTotalContri.size()>0)			 
				{
					Double amnt=0d;
					//$t 2019
					Double amntE=0d;
				
					for(int v=0;v<lEmpTotalContri.size();v++)
					{
						if(lEmpTotalContri.get(v)!=null && !"".equalsIgnoreCase(lEmpTotalContri.get(v).toString()))
						{
							Object []ElObj = (Object[]) lEmpTotalContri.get(v);
							
							if(ElObj[0]!=null){
							amnt=amnt+Double.parseDouble(ElObj[0].toString());
							
							amntE=amntE+Double.parseDouble(ElObj[1].toString());
							
							}
						}
						
					}

					String totalAmtBH=amnt.toString();
					//$t 2019
					String totalAmtBHE=amntE.toString();
					
					EmpleeContri=Double.parseDouble(totalAmtBH);
					totalEmplyContri=nosci(EmpleeContri);
					gLogger.error("In Loop 2 "+totalEmplyContri);
					EmplrContri = Double.parseDouble(totalAmtBHE);
					totalEmplyerContri=nosci(EmplrContri);
					gLogger.error("In Loop3  "+EmplrContri);
					totalEContri =Double.parseDouble(totalEmplyContri)+Double.parseDouble(totalEmplyerContri);
					gLogger.error("In Loop 4 "+totalEContri);
				}


				//total
				String totaloverallAmt=nosci(totalEContri);
				gLogger.info("Out Loop totaloverallAmt"+totaloverallAmt);

				overallAmt=totaloverallAmt.toString().split("\\.");
				gLogger.info("Out Loop overallAmt"+overallAmt[0]);
				if (overallAmt.length !=1){
					if(totaloverallAmt.equals("0")){
						totaloverallAmt="0.00";
						gLogger.error("Out Loop in if 1 "+totaloverallAmt);
					}

					else if(overallAmt[1].length()==1)
					{
						gLogger.error("Out Loop in if 2 "+totaloverallAmt);
						totaloverallAmt=totaloverallAmt+"0";
						gLogger.error("Out Loop in if 2 "+totaloverallAmt);
					}

					else if(overallAmt[1].length()>2)
					{
						gLogger.error("Out Loop in if 3 "+totaloverallAmt);
						totaloverallAmt= decRoundOff(totaloverallAmt);
						gLogger.error("Out Loop in if 3 "+totaloverallAmt);

					}
					gLogger.error("Out Loop totaloverallAmt"+totaloverallAmt);
				}
				else{
					totaloverallAmt=totaloverallAmt+".00";

				}
				//emp 


				totalempoverallAmt=totalEmplyContri.toString().split("\\.");

				gLogger.info("Out Loop totalempoverallAmt"+totalempoverallAmt[0]);

				if(totalempoverallAmt.length!=1){
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

					gLogger.error("Out Loop totalempoverallAmt"+totalempoverallAmt[0]);
				}


				else{
					totalEmplyContri=totalEmplyContri+".00";

				}
				//emplr	 
				gLogger.error("Amount is **********  "+totalEmplyerContri.toString());
				totalemplroverallAmt=totalEmplyerContri.toString().split("\\.");
				gLogger.error("Out Loop totalempoverallAmt"+totalemplroverallAmt[0]);
				if(totalemplroverallAmt.length!=1){
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
				}
				else{

					totalEmplyerContri=totalEmplyerContri+".00";
				}

				gLogger.error("BH Employee amount is "+totalEmplyContri);
				gLogger.error("BH Employer amount is "+totalEmplyerContri);
				gLogger.error("BH total  amount is "+totaloverallAmt);
				int temp=0;
				int emprecCount=0;

				//query to insert the data in bh details table
				//BH end 		

				String[] lEmpCountContrDdo=lObjNsdlNps.getEmployeeCountDdoregNsdlForDeputation(finYearId.toString(),month,treasuryyno,yrCode,ddoCodeTr);//done
				String[] lEmpTotalContrDdo=lObjNsdlNps.getEmployeeListDdoregNsdlDeputation(finYearId.toString(),month,treasuryyno,yrCode,ddoCodeTr);//done
				
				HashMap lEmpCountContrDdoMap=lObjNsdlNps.getEmployeeCountDdoregNsdlForDeputationMap(finYearId.toString(),month,treasuryyno,yrCode,ddoCodeTr);
				HashMap lEmpTotalContrDdoMap=lObjNsdlNps.getEmployeeListDdoregNsdlDeputationMap(finYearId.toString(),month,treasuryyno,yrCode,ddoCodeTr);

				gLogger.error("lEmpCountContrDdo length"+lEmpCountContrDdo.length);
			


				PrintWriter outputfile =response.getWriter();
				for (Iterator it = lstemployee.iterator(); it.hasNext();)				 
				{
					count++;
					Object[] lObj = (Object[]) it.next();


					empname = (lObj[0] != null) ? lObj[0].toString(): "NA";


					dcpsid = ((lObj[1] != null)? lObj[1].toString() :"");

					gLogger.error("dcps Id ********"+dcpsid);
					pranno = ((lObj[2] != null)? lObj[2].toString() :"");

					//dtoRegNo = (lObj[6] != null) ? lObj[6].toString() : "";						
//2019
					ddoRegNo = (lObj[5] != null) ? lObj[5].toString() : "";		

					govEmpContri = (lObj[3] != null) ? lObj[3].toString() : "";		

					subempContri = (lObj[4] != null) ? lObj[4].toString() : "";	
					
					sdamnt= (lObj[6] != null) ? lObj[6].toString() : "";	
	 
					contriEmployee=govEmpContri.toString().split("\\.");
					if(contriEmployee.length!=1){
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
					}

					else{
						govEmpContri=govEmpContri+".00";	

					}

					emplrContriintEmployer=subempContri.toString().split("\\.");
					if(emplrContriintEmployer.length!=1){
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
					}
					else{
						subempContri=subempContri+".00";
					}



					totalContribution = (govEmpContri != null) ? Double.parseDouble(govEmpContri) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri) ;

					TotalContri=totalContribution.toString();


					countsum = countsum + count;


					GovContributionSum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(govEmpContri) : 0.00 + Double.parseDouble(govEmpContri);

					govcontiSum=GovContributionSum.toString();

					subcontributionSum = (subcontiSum != null) ? Double.parseDouble(subcontiSum) + Double.parseDouble(subempContri) : 0.00 + Double.parseDouble(subempContri);
					
					subcontiSum=subcontributionSum.toString();

					TotalContributionsum = (govcontiSum != null) ? Double.parseDouble(govcontiSum) + Double.parseDouble(subcontiSum) : 0.00 +Double.parseDouble(subcontiSum);
			
					TotalContrisum=TotalContributionsum.toString();


					gLogger.error("i "+i);
					gLogger.error("totalsize"+totalsize);
					gLogger.error("j "+j);
					gLogger.error("lEmpCountContrDdo.length"+lEmpCountContrDdo.length);
					
					
					
					
					if(!prvDdoReg.equals(ddoRegNo) && j <= lEmpCountContrDdo.length)
					{
						
						
						if(lEmpTotalContrDdo!=null && lEmpTotalContrDdo.length>0)			 
						{
							gLogger.info("j is *******************"+j);

							

							EmpleeDHContri=0d;
							if(lEmpTotalContrDdoMap.containsKey(ddoRegNo))
							{
								//$t 2019
								String main=lEmpTotalContrDdoMap.get(ddoRegNo).toString();
								String[] values = main.split("-");
						        
								
								EmpleeDHContri=Double.parseDouble(values[0].toString());
								gLogger.info("EmpleeDHContri is *******************"+EmpleeDHContri);
								EmplrDHContri = Double.parseDouble(values[1].toString());
								gLogger.info("EmplrDHContri is *******************"+EmplrDHContri);
							}
								
							gLogger.info("EmpleeDHContri is *********"+EmpleeDHContri);	

							totalEmplyDHContri=nosci(EmpleeDHContri);
							gLogger.info("totalEmplyDHContri is *********"+totalEmplyDHContri);	
							
							totalEmplyerDHContri=nosci(EmplrDHContri);
							totalEDHContri =Double.parseDouble(totalEmplyDHContri)+Double.parseDouble(totalEmplyerDHContri);
							gLogger.info("totalEDHContri is *********"+totalEDHContri);	

						}

							
							String totaloverDHallAmt=nosci(totalEDHContri);

						//total
					

						overallDHAmt=totaloverDHallAmt.toString().split("\\.");
						gLogger.info("totaloverDHallAmt is *********"+totaloverDHallAmt);	
						if(overallDHAmt.length!=1) {
							if(totaloverDHallAmt.equals("0")){
								totaloverDHallAmt="0.00";
							}

							else if(overallDHAmt[1].length()==1)
							{
								totaloverDHallAmt=totaloverDHallAmt+"0";
							}

							else if(overallDHAmt[1].length()>2)
							{
								totaloverDHallAmt= decRoundOff(totaloverDHallAmt);

							}
						}

						else{
							totaloverDHallAmt=totaloverDHallAmt+".00";

						}

						//emp 


						totalempoverallDHAmt=totalEmplyDHContri.toString().split("\\.");
						gLogger.error("totalEmplyDHContri is *********"+totalEmplyDHContri);	
						if(totalempoverallDHAmt.length!=1){
							if(totalEmplyDHContri.equals("0")){
								totalEmplyDHContri="0.00";
							}

							else if(totalempoverallDHAmt[1].length()==1)
							{
								totalEmplyDHContri=totalEmplyDHContri+"0";
							}

							else if(totalempoverallDHAmt[1].length()>2)
							{

								totalEmplyDHContri=  decRoundOff(totalEmplyDHContri);

							}
						}
						else{
							totalEmplyDHContri=totalEmplyDHContri+".00";

						}

						//emplr	 

						totalemplroverallDHAmt=totalEmplyerDHContri.toString().split("\\.");
						gLogger.error("totalemplroverallDHAmt is *********"+totalemplroverallDHAmt);
						if(totalemplroverallDHAmt.length!=1){
							if(totalEmplyerDHContri.equals("0")){
								totalEmplyerDHContri="0.00";
							}

							else if(totalemplroverallDHAmt[1].length()==1)
							{
								totalEmplyerDHContri=totalEmplyerDHContri+"0";
							}
							else if(totalemplroverallDHAmt[1].length()>2)
							{

								totalEmplyerDHContri=  decRoundOff(totalEmplyerDHContri);
							}

						}
						else{

							totalEmplyerDHContri=totalEmplyerDHContri+".00";
						}
						gLogger.error("All is well");	


						i=i+1;
						gLogger.error("All is well i"+i);
						j=j+1;
						empCount=1;

						gLogger.error("All is well j"+j);
						if(j<= lEmpCountContrDdo.length)
						{
						Strbr.append(i+"^");		
						Strbr.append("DH"+"^"); 	
						Strbr.append("1"+"^"); 	
						Strbr.append(j+"^"); 	
						Strbr.append(ddoRegNo+"^");
						gLogger.error("All is well ddoRegNo"+ddoRegNo);
						gLogger.error("All is well emprecCount"+emprecCount);
						gLogger.error("All is well lEmpCountContrDdo length"+lEmpCountContrDdo.length);
						gLogger.error("All is well lEmpCountContrDdo"+lEmpCountContrDdo);
						gLogger.error("All is well lEmpCountContrDdo length"+lEmpCountContrDdo.length);
				
						Long EmpCount=0l;
						if(lEmpCountContrDdoMap.containsKey(ddoRegNo))
						{
							EmpCount=Long.parseLong(lEmpCountContrDdoMap.get(ddoRegNo).toString());
						}
						
							
							
						gLogger.error("All is well lEmpCountContrDdo length"+lEmpCountContrDdo.length);
						
						Strbr.append(EmpCount+"^"); 
						gLogger.error("All is well EmpCount "+EmpCount );
						Strbr.append(totalEmplyDHContri+"^"); 	
						gLogger.error("All is well totalEmplyDHContri "+totalEmplyDHContri );
						Strbr.append(totalEmplyerDHContri+"^"); 
						gLogger.error("All is well totalEmplyerDHContri "+totalEmplyerDHContri );
						Strbr.append("^"); 		 

						lObjNsdlNps.insertDHDetails(i,"DH","1",j,ddoRegNo,EmpCount,totalEmplyDHContri,totalEmplyerDHContri,BatchId);
						//Strbr.append("\n");	
						Strbr.append("\r\n");
						temp++;
						emprecCount++;
						}
						gLogger.error("Strbr for DH is"+Strbr.toString());

					}
					prvDdoReg=ddoRegNo;
					++i;

					if(lstemployee!=null && !lstemployee.equals("") && j<= lEmpCountContrDdo.length) 
					{

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
					   //$t 5-11 2019
						double conti1 = 0.0D;
						conti1 = Double.parseDouble(govEmpContri.toString());
						//
						//Changes done by ashish on 13 aug 2015 for Changes in SD "A" changes to "C"
						//Strbr.append("A"+"^^^");
						String contribType=lObjNsdlNps.getContribType(dcpsid);
						String contribTypeSd=lObjNsdlNps.getEmployeeContribType(month,pranno,yrCode);
						if(contribTypeSd!=null && "A".equalsIgnoreCase(contribTypeSd))
						{
							Strbr.append("A"+"^"+month+"^"+yrCode+"^");
						}else if (conti1 == 0.0D) {
			            	Strbr.append("A^" + month + "^" + yrCode + "^");
			            } 
						else
						{
							Strbr.append(contribType+"^"+month+"^"+yrCode+"^");
						}
						
						
						//end
						
						if(Long.parseLong(month) < 10){
							monthId="0"+month;
						}
						else if (Long.parseLong(month) >= 10){
							monthId=month;
						}
						
						String finyear="";
						if(Long.parseLong(month)>2)
							finyear= yrCode+"-"+(Long.parseLong(yrCode)+1);
						else
							finyear=(Long.parseLong(yrCode)-1) +"-"+yrCode ;

						Strbr.append("Contribution for "+month+"/"+yrCode+"^");
						//Changes done by ashish on 13 aug 2015 for Changes in SD "A" changes to "C"
						
						if(contribTypeSd!=null && "A".equalsIgnoreCase(contribTypeSd))
						{
							lObjNsdlNps.insertSDDetails(i,"SD","1",j,empCount,pranno,govEmpContri,subempContri,TotalContri+"0","A"+"^"+monthId+"^"+yrCode,"Contribution for "+month+"/"+yrCode+"",BatchId,ddoRegNo);
						}
						else if (conti1 == 0.0D) {//$t 5-11 2019
			                lObjNsdlNps.insertSDDetails(i, "SD", "1", j, empCount, pranno, govEmpContri, subempContri, TotalContri + "0", "A^" + monthId + "^" + yrCode, "4 Per Contribution for " + month + "/" + yrCode, BatchId, ddoRegNo);
			            } 
						else
						{
							lObjNsdlNps.insertSDDetails(i,"SD","1",j,empCount,pranno,govEmpContri,subempContri,TotalContri+"0",contribType+"^"+monthId+"^"+yrCode,"Contribution for "+month+"/"+yrCode+"",BatchId,ddoRegNo);
						}
						
							//	end 
						Strbr.append("\r\n");


					}


					++empCount;
					

				}



				gLogger.error("All fine till nmow");
				//new line 
				String lineSeperator ="\r\n"; 
				//System.getProperty("line.separator");.....not working


				String os = System.getProperty("os.name");
				gLogger.error("All ok till now is ************os*******"+os);

				if (os.toLowerCase().indexOf("unix") > 0){
					lineSeperator="\n";

				} else if (os.toLowerCase().indexOf("windows") > 0){
					lineSeperator ="\r\n"; 

				} else {

				}

				gLogger.error("All ok till now is *******************");
				HttpServletResponse response = (HttpServletResponse) inputMap
				.get("responseObj");
				
				NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
				
				 dtoRegNo=lObjNsdlDAO.getDtoRegNo(treasuryyno.substring(0, 2));
				 
				getFileHeader(outputfile,dtoRegNo);


				gLogger.error("outputfile is *******************"+outputfile);
				gLogger.error("totalsize is *******************"+totalsize);
				gLogger.error("countReg is *******************"+countReg);
				gLogger.error("totalEmplyContri is *******************"+totalEmplyContri);
				gLogger.error("totalEmplyerContri is *******************"+totalEmplyerContri);
				gLogger.error("totaloverallAmt is *******************"+totaloverallAmt);
				gLogger.error("BatchId is *******************"+BatchId);
				gLogger.error("yrCode is *******************"+yrCode);
				gLogger.error("month is *******************"+month);
				
				
			
				getBatchHeaderDeputation(outputfile,totalsize,countReg,totalEmplyContri,totalEmplyerContri,totaloverallAmt,BatchId,yrCode,month,dtoRegNo);

				List nsdlDeatils=lObjNsdlNps.getAllDataDeputation(yrCode,month,treasuryyno);
				
				List lLstYears = lObjNsdlDAO.getFinyear();
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
						.getSessionFactory());
				List lLstMonths=lObjDcpsCommonDAO.getMonths();
				List subTr=lObjNsdlDAO.getAllSubTreasury(gStrLocationCode);
				inputMap.put("size", nsdlDeatils.size());
				inputMap.put("nsdlDeatils", nsdlDeatils);
				inputMap.put("selMonth", month);
				inputMap.put("selYear", yrCode);
				inputMap.put("lLstYears", lLstYears);
				inputMap.put("lLstMonths", lLstMonths);
				inputMap.put("subTr", subTr);
				inputMap.put("subTrCode", treasuryyno);
				inputMap.put("idDeputation","1");
				inputMap.put("digiActivate",digiActivate);
				

			}
					
		}	

		catch (Exception e)
		{
			gLogger.info("Error occure in createTxtFile()"+e);
			e.printStackTrace();
			resObj.setResultCode(ErrorConstants.ERROR);


		}
		resObj.setResultCode(ErrorConstants.SUCCESS);
		resObj.setResultValue(inputMap);

		resObj.setViewName("NPSVALIDATE");
		return resObj;

	
		
	}
//$t 2019
	private void getFileHeader(PrintWriter br ,String dtoRegNo) throws IOException
	{
		br.write("1"+"^");   		 br.write("FH"+"^"); 		 br.write("P"+"^"); 		 br.write(dtoRegNo+"^");		 br.write("1"+"^");
		br.write("^"+"A");	     	 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^"); 
		br.write("\r\n");   //br.write("\n");
		gLogger.info(br);
	}

	private void getBatchHeader(PrintWriter br , int count,long ddoCount,String govContri,String SubContri, String Total,String batchId, String yrCode, String month,String dtoRegNo) throws IOException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());	
		String date = "ddMMyyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(date);
		sdf.setTimeZone(TimeZone.getDefault());
		String currentdate = sdf.format(cal.getTime());
//$t 2019 9
		br.write("2"+"^");  		 br.write("BH"+"^"); 		 br.write("1"+"^"); 		 br.write("R"+"^");
		br.write(dtoRegNo+"^");  		 br.write(currentdate+"^"); 		 br.write(dtoRegNo+batchId); 
		br.write("^^"); 	  br.write(ddoCount+"^");		 br.write(count+"^"); 		 br.write(SubContri+"^");
		br.write(govContri+"^"); 		 br.write("^"); 		 br.write(Total+"^"); 	br.write("\r\n");
		NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
		lObjNsdlNps.insertBatchHeader("2","BH","1","R",dtoRegNo,currentdate,dtoRegNo+batchId,ddoCount,count,SubContri,govContri,Total,batchId,yrCode,month);

		//br.write("\n");br.write(SubContri+"^");
	}
	private void getBatchHeaderDeputation(PrintWriter br , int count,long ddoCount,String govContri,String SubContri, String Total,String batchId, String yrCode, String month,String dtoRegNo) throws IOException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());	
		String date = "ddMMyyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(date);
		sdf.setTimeZone(TimeZone.getDefault());
		String currentdate = sdf.format(cal.getTime());
		String s=batchId;
		int l=s.length();
		String s1=s.substring(0, (l-1));
		br.write("2"+"^");  		 br.write("BH"+"^"); 		 br.write("1"+"^"); 		 br.write("R"+"^");
		br.write(dtoRegNo+"^");  		 br.write(currentdate+"^"); 		 br.write(dtoRegNo+s1); 
		br.write("^^"); 	  br.write(ddoCount+"^");		 br.write(count+"^"); 		 br.write(SubContri+"^");
		br.write(govContri+"^"); 		 br.write("^"); 		 br.write(Total+"^"); 	br.write("\r\n");
		NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
		
		lObjNsdlNps.insertBatchHeader("2","BH","1","R",dtoRegNo,currentdate,dtoRegNo+s1,ddoCount,count,SubContri,govContri,Total,batchId,yrCode,month);

		//br.write("\n");
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
		br.write("^"+"A");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^"); 
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
		br.write("^"); 	 br.write("1"+"^"); 		 br.write(count+"^"); 		 br.write(SubContri+"^");
		br.write(govContri+"^"); 		 br.write("^"); 		 br.write(Total+"^"); 		 br.write("\n");
	}

	private void getDTOHeaderforfpu( PrintWriter br ,int count,String govContri,String SubContri) throws IOException
	{

		br.write("3"+"^");		 br.write("DH"+"^"); 		 br.write("1"+"^"); 		 br.write("1"+"^"); 		 br.write("1"+"^");
		br.write(count+"^"); 		 br.write(SubContri+"^"); 		 br.write(govContri+"^"); 		 br.write("^"); 		 
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
	public ResultObject loadNPSNSDLGenFiles(Map inputMap) {

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
		String cmbDep="2";
		List nsdlDeatils=null;
		String isLoad="N";
		int digiActivate=0;
		try {
			setSessionInfo(inputMap);
			String month = StringUtility.getParameter("cmbMonth", request);
			String year = StringUtility.getParameter("cmbYear", request);
			if(StringUtility.getParameter("isLoad", request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("isLoad", request)))
			{
				isLoad=StringUtility.getParameter("isLoad", request);
			}
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
			
			gLogger.info("currentmonth is *******************"+currentmonth);
			gLogger.info("currentyear is *******************"+currentyear);
			 
			
			
			String trCode=gStrLocationCode;
			if(StringUtility.getParameter("cmbMonth", request)!=null && !(StringUtility.getParameter("cmbMonth", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbMonth", request))!=-1 
					&& StringUtility.getParameter("cmbYear", request)!=null && !(StringUtility.getParameter("cmbYear", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbYear", request))!=-1
					&& StringUtility.getParameter("trCode", request)!=null && !(StringUtility.getParameter("trCode", request).equals("")) && Long.parseLong(StringUtility.getParameter("trCode", request))!=-1
					 ){
				
	
				
				
				
				currentmonth= Long.parseLong(StringUtility.getParameter("cmbMonth", request));
				currentyear=Long.parseLong(StringUtility.getParameter("cmbYear", request));
				trCode=StringUtility.getParameter("trCode", request);
				if(StringUtility.getParameter("cmbDep", request)!=null && !(StringUtility.getParameter("cmbDep", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbDep", request))!=-1)
				{
					cmbDep=StringUtility.getParameter("cmbDep", request);
				}
				
				gLogger.info("currentmonth is *******************"+currentmonth);
				gLogger.info("currentyear is *******************"+currentyear);
				gLogger.info("trCode is *******************"+trCode);
				
				

			}

			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
			


			//String allowedOrNot=(String) lObjNsdlDAO.getStatusOfDist(gStrLocationCode);
   		// gLogger.info("allowedOrNot for paybill " + allowedOrNot);
   		//if(allowedOrNot.equals("1")){
			List subTr=lObjNsdlDAO.getAllSubTreasury1(gStrLocationCode);
			if(isLoad!=null && "Y".equalsIgnoreCase(isLoad))
			{
				if(cmbDep!=null && !"".equalsIgnoreCase(cmbDep) && "2".equalsIgnoreCase(cmbDep))
				{
					nsdlDeatils=lObjNsdlDAO.getAllData(year.toString(), month.toString(), trCode);
				}
				else
				{
					nsdlDeatils=lObjNsdlDAO.getAllDataDeputation(year.toString(), month.toString(), trCode);
				}
			}
			else
			{
				if(cmbDep!=null && !"".equalsIgnoreCase(cmbDep) && "2".equalsIgnoreCase(cmbDep))
				{
					nsdlDeatils=lObjNsdlDAO.getAllData(currentyear.toString(), currentmonth.toString(), trCode);
				}
				else
				{
					nsdlDeatils=lObjNsdlDAO.getAllDataDeputation(currentyear.toString(), currentmonth.toString(), trCode);
				}
			}
			
			
			List lLstYears = lObjNsdlDAO.getFinyear();
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			if(isLoad!=null && "Y".equalsIgnoreCase(isLoad))
			{
				inputMap.put("selMonth", month);
				inputMap.put("selYear", year);
			}
			else
			{
				inputMap.put("selMonth", currentmonth);
				inputMap.put("selYear", currentyear);
			}
			digiActivate=lObjNsdlDAO.getDigiActivationDtls(gStrLocationCode);
			inputMap.put("subTr", subTr);
			inputMap.put("digiActivate", digiActivate);

			List lLstMonths=lObjDcpsCommonDAO.getMonths();
			inputMap.put("size", nsdlDeatils.size());
			inputMap.put("nsdlDeatils", nsdlDeatils);
			inputMap.put("subTrCode", trCode);
			gLogger.info("Month and year is "+lLstMonths.size());
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("idDeputation",cmbDep);
			resObj.setResultValue(inputMap);
			resObj.setViewName("NPSVALIDATE");

			
   	/*	}
		else{
			inputMap.put("allowedOrNot", allowedOrNot);	
			inputMap.put("restrictmsg", "You are not allowed to use this functionality as on today. Kindly contact MDC for further action.");
			resObj.setResultCode(ErrorConstants.SUCCESS);
			resObj.setResultValue(inputMap);
			resObj.setViewName("NPSVALIDATE");
		}*/
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





	public ResultObject createTextFilesForNSDL(Map inputMap) {

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
		String BatchId=null;
		String  dhDtls="";
		String ddoRegNo="";
		
		try {
			setSessionInfo(inputMap);
			if(StringUtility.getParameter("fileNumber", request)!=null && !StringUtility.getParameter("fileNumber", request).equals(""))
			{
				String fileNumber = StringUtility.getParameter("fileNumber", request);
				NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
				BatchId=fileNumber;
				String dtoRegNo="";
				dtoRegNo=lObjNsdlNps.getDtoRegNo(fileNumber.substring(0, 2));
			    //$t 5-11 2019  
				String lastChar = (fileNumber.charAt(fileNumber.length() -1))+"";
				lastChar.trim();
			    gLogger.info("check file deputation is *********"+lastChar);
			    // 
				StringBuffer sb=new StringBuffer();
				PrintWriter outputfile =response.getWriter();
				//getFileHeader(outputfile);
				sb.append("1^FH^P^"+dtoRegNo+"^1^^A^^^^^");
				gLogger.info("All fine 1 is *********");
				String bhData=lObjNsdlNps.getBatchData(fileNumber);
				gLogger.info("All fine 2 is *********"+bhData);
				//outputfile.write(bhData);
				//outputfile.write("\r\n");
				sb.append("\r\n");
				sb.append(bhData);
				sb.append("\r\n");
				gLogger.info("All fine 3 is *********");
				//$t 5-11 2019
				if(!lastChar.equals("D")){
				List dhData1 = lObjNsdlNps.getDHData1(fileNumber);
				
				List dhData=lObjNsdlNps.getDHData(fileNumber);
				
				if (dhData != null && dhData.size() > 0)
				{
					Iterator IT = dhData.iterator();

					Object[] lObj = null;
					List sdDtls;
					while (IT.hasNext())
					{
						lObj = (Object[]) IT.next();
						dhDtls = lObj[0].toString();
						ddoRegNo = lObj[1].toString();
					//	outputfile.write(dhDtls);

						//outputfile.write("\r\n");

						sb.append(dhDtls);
						sb.append("\r\n");

						 sdDtls=lObjNsdlNps.getSDDtls(fileNumber,ddoRegNo);
						for(int i=0;i<sdDtls.size();i++){
							//outputfile.write(sdDtls.get(i).toString());
							//outputfile.write("\r\n");
							sb.append(sdDtls.get(i).toString());
							sb.append("\r\n");

						}
					}
				}
				//$t 5-11 2019
				if ((dhData1 != null) && (dhData1.size() > 0))
		        {
		          Iterator IT = dhData1.iterator();
		          
		          Object[] lObj = null;
		          List sdDtls1;
		          int i;
		          while (IT.hasNext())
					{
						lObj = (Object[]) IT.next();
						dhDtls = lObj[0].toString();
						ddoRegNo = lObj[1].toString();
					//	outputfile.write(dhDtls);

						//outputfile.write("\r\n");

						sb.append(dhDtls);
						sb.append("\r\n");

						 sdDtls1=lObjNsdlNps.getSDDtls1(fileNumber,ddoRegNo);
						for(i=0;i<sdDtls1.size();i++){
							//outputfile.write(sdDtls.get(i).toString());
							//outputfile.write("\r\n");
							sb.append(sdDtls1.get(i).toString());
							sb.append("\r\n");

						}
					}
		        }
			}else{
				//$t 6-11 2019
				List dhData=lObjNsdlNps.getDHDataDeputation(fileNumber);
				
				if (dhData != null && dhData.size() > 0)
				{
					Iterator IT = dhData.iterator();

					Object[] lObj = null;
					List sdDtls;
					while (IT.hasNext())
					{
						lObj = (Object[]) IT.next();
						dhDtls = lObj[0].toString();
						ddoRegNo = lObj[1].toString();
					//	outputfile.write(dhDtls);

						//outputfile.write("\r\n");

						sb.append(dhDtls);
						sb.append("\r\n");

						 sdDtls=lObjNsdlNps.getSDDtlsDeputation(fileNumber,ddoRegNo);
						for(int i=0;i<sdDtls.size();i++){
							//outputfile.write(sdDtls.get(i).toString());
							//outputfile.write("\r\n");
							sb.append(sdDtls.get(i).toString());
							sb.append("\r\n");

						}
					}
				}
			}	
				gLogger.info("All fine 4 is *********");
				//gLogger.info("stringBuifefer is *********"+sb.toString());

				//PasswordEncryption objPasswordEncryption = new PasswordEncryption();
				//gLogger.info("password encryption is ****************"+objPasswordEncryption.crypt(sb.toString()));
				//lObjNsdlNps.updateMD5hash(objPasswordEncryption.crypt(sb.toString()),fileNumber);   

				String lStrFileName=BatchId;

				try{
					String fileName = lStrFileName + ".txt";
					response.setContentType("text/plain;charset=UTF-8");

					response.addHeader("Content-disposition",
							"attachment; filename=" + fileName);
					response.setCharacterEncoding("UTF-8");


					outputfile.write(sb.toString());
					outputfile.flush();

				}
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					gLogger.info("All fine 7 is *********");
					if(outputfile!=null)
						outputfile.close();
				}


			}

			gLogger.info("All fine 8 is *********");
			resObj.setResultValue(inputMap);
			resObj.setViewName("ExportReportPage");
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


	public ResultObject deleteTextFilesForNSDL(Map inputMap) {

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
		boolean flag=false;

		try {

			/* Sets the Session Information */
			setSessionInfo(inputMap);
			Map loginMap = (Map) (Map) inputMap.get("baseLoginMap");
			long langId = Long.parseLong(loginMap.get("langId").toString());
			if(StringUtility.getParameter("fileNumber", request)!=null && !StringUtility.getParameter("fileNumber", request).equals(""))
			{
				String fileNumber = StringUtility.getParameter("fileNumber", request);
				NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
				lObjNsdlNps.deleteNsdlFile(fileNumber);
				flag=true;
			}

			String lSBStatus = getResponseXMLDoc(flag).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();

			inputMap.put("ajaxKey", lStrResult);
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);
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
	private StringBuilder getResponseXMLDoc(Boolean flag) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");

		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	private StringBuilder getResponseXMLDoc(Boolean flag,Long month,Long year,String dep,String trCode,String digiActivate) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<MONTH>");
		lStrBldXML.append(month);
		lStrBldXML.append("</MONTH>");
		lStrBldXML.append("<YEAR>");
		lStrBldXML.append(year);
		lStrBldXML.append("</YEAR>");
		lStrBldXML.append("<DEP>");
		lStrBldXML.append( dep);
		lStrBldXML.append("</DEP>");
		lStrBldXML.append("<TRCODE>");
		lStrBldXML.append( trCode);
		lStrBldXML.append("</TRCODE>");
		lStrBldXML.append("<digiActivate>");
		lStrBldXML.append( digiActivate);
		lStrBldXML.append("</digiActivate>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}
	private StringBuilder getResponseXMLDoc(Boolean flag,String errorDesc,Long month,Long year,String dep,String trCode,String digiActivate) {

		StringBuilder lStrBldXML = new StringBuilder();

		lStrBldXML.append("<XMLDOC>");
		lStrBldXML.append("<Flag>");
		lStrBldXML.append(flag);
		lStrBldXML.append("</Flag>");
		lStrBldXML.append("<ERROR>");
		lStrBldXML.append(errorDesc);
		lStrBldXML.append("</ERROR>");
		lStrBldXML.append("<MONTH>");
		lStrBldXML.append(month);
		lStrBldXML.append("</MONTH>");
		lStrBldXML.append("<YEAR>");
		lStrBldXML.append(year);
		lStrBldXML.append("</YEAR>");
		lStrBldXML.append("<DEP>");
		lStrBldXML.append( dep);
		lStrBldXML.append("</DEP>");
		lStrBldXML.append("<TRCODE>");
		lStrBldXML.append( trCode);
		lStrBldXML.append("</TRCODE>");
		lStrBldXML.append("<digiActivate>");
		lStrBldXML.append( digiActivate);
		lStrBldXML.append("</digiActivate>");
		lStrBldXML.append("</XMLDOC>");

		return lStrBldXML;
	}

	public ResultObject generateNSDLPaybill(Map inputMap) {
		gLogger.info("inside generateNSDLPaybill");
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
		Long billNo=0l;
		List billDetails = null;
		Object obj[];
		String billCreationDate = null;
		String billGeneratedMonth=null;

		try {
			setSessionInfo(inputMap);
			String month = StringUtility.getParameter("cmbMonth", request);


			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			List lLstMonths=lObjDcpsCommonDAO.getMonths();
			String Month = StringUtility.getParameter("Month", request);
			String Year = StringUtility.getParameter("Year", request);
			String fileNumber = StringUtility.getParameter("fileNumber", request);
			double employeeContribution = Double.parseDouble(StringUtility.getParameter("employeeContribution", request).toString());
			String empContri=nosci(employeeContribution);
			double employerContribution =  Double.parseDouble(StringUtility.getParameter("employerContribution", request).toString());
			String emplrContri=nosci(employerContribution);
			double totalContribution=employeeContribution+employerContribution;
			String totalContri=nosci(totalContribution);
			String totalContributionAmountInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(totalContribution));
			String employeeContributionInWords= EnglishDecimalFormat.convertWithSpace(new BigDecimal(employeeContribution));
			String employerContributionInWords= EnglishDecimalFormat.convertWithSpace(new BigDecimal(employerContribution));
			String stringMonth = "";
			int  paybillMonth=0;
			gLogger.info("totalContributionAmountInWords"+totalContributionAmountInWords);
			gLogger.info("Month"+Month);
			gLogger.info("Year"+Year);
			gLogger.info("fileNumber"+fileNumber);
			gLogger.info("employeeContribution"+employeeContribution);
			gLogger.info("employerContribution"+employerContribution);
			inputMap.put("totalContributionAmountInWords", totalContributionAmountInWords);
			inputMap.put("totalContribution", totalContri);
			//added for bill rejected by beams changes
			String billId="";
			String appendBillid="";
			String fileNameTODelete="";
			billId = StringUtility.getParameter("billId", request).toString();
			gLogger.info("billId"+billId);
			if(billId!=null && !billId.equals("") && !billId.equals("NA")){
				appendBillid = billId.substring(9);
				gLogger.info("appendBillid"+appendBillid);
				fileNameTODelete=fileNumber+"_"+appendBillid;
				lObjNsdlDAO.updateBillStatus(billId,fileNameTODelete);	
			}
			IdGenerator idGen = new IdGenerator();
			nsdl_paybill_pk = idGen.PKGenerator("NSDL_BILL_dtls", inputMap);
			String status=lObjNsdlDAO.getBillStatus(fileNumber);
			
			gLogger.info("status"+status);
			if(status !=null && Long.parseLong(status)==0){
				
				lObjNsdlDAO.createNSDLBillGenration(nsdl_paybill_pk,Year,Month,employeeContribution,employerContribution,totalContribution,fileNumber);
				billDetails=lObjNsdlDAO.getBillNoDate(fileNumber);
				if(billDetails!=null && !billDetails.isEmpty()){
					Iterator it =  billDetails.iterator();
					while(it.hasNext()){
						obj = (Object[]) it.next();
						billNo = Long.parseLong(obj[0].toString());
						gLogger.info("billNo"+billNo);
						billCreationDate=obj[1].toString();
						gLogger.info("billCreationDate"+billCreationDate);
						billGeneratedMonth=obj[2].toString();
						paybillMonth = Integer.parseInt(obj[2].toString());
					}

				}
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
			     stringMonth=(String) monthMap.get(paybillMonth);
				inputMap.put("billNo", billNo);				
				inputMap.put("billCreationDate", billCreationDate);
				inputMap.put("billGeneratedMonth", billGeneratedMonth);
				inputMap.put("employeeContribution", empContri);
				inputMap.put("employerContribution", emplrContri);
				inputMap.put("employeeContributionInWords", employeeContributionInWords);
				inputMap.put("employerContributionInWords", employerContributionInWords);
				inputMap.put("selectedMonth", stringMonth);
				inputMap.put("successmsg", "Bill  Generated Successfully");
				resObj.setResultValue(inputMap);
				resObj.setViewName("NPSVALIDATE");
			}else{
				inputMap.put("msg", "Bill is Already Generated");
				inputMap.put("billNo", billNo);
				inputMap.put("billCreationDate", billCreationDate);
				inputMap.put("billGeneratedMonth", billGeneratedMonth);
				resObj.setResultValue(inputMap);
				resObj.setViewName("NPSVALIDATE");
			}



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

	public ResultObject viewGenerateNSDLPaybill(Map inputMap) {

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
			String month = StringUtility.getParameter("cmbMonth", request);
			gLogger.info("month from jsp"+month);

			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			List lLstMonths=lObjDcpsCommonDAO.getMonths();
			String Month = StringUtility.getParameter("Month", request);
			String Year = StringUtility.getParameter("Year", request);
			
			String fileNumber = StringUtility.getParameter("fileNumber", request);
			Year=fileNumber.substring(4, 8);
			Month=fileNumber.substring(8, 10);
			
		
			
				if(Integer.parseInt(Year)==2017 && Integer.parseInt(Month) > 3)
				{
					scheme="(Committed)";
				}
			String stringMonth = "";
			double employeeContribution = Double.parseDouble(StringUtility.getParameter("employeeContribution", request).toString());
			String empContri=nosci(employeeContribution);
			double employerContribution =  Double.parseDouble(StringUtility.getParameter("employerContribution", request).toString());
			String emplrContri=nosci(employerContribution);
			double totalContribution=employeeContribution+employerContribution;
			String totalContri=nosci(totalContribution);
			String totalContributionAmountInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(totalContribution));
			String employeeContributionInWords= EnglishDecimalFormat.convertWithSpace(new BigDecimal(employeeContribution));
			String employerContributionInWords= EnglishDecimalFormat.convertWithSpace(new BigDecimal(employerContribution));
			gLogger.info("totalContributionAmountInWords"+totalContributionAmountInWords);
			int paybillMonth = lObjNsdlDAO.getMonthId(fileNumber); 
			gLogger.info("paybillMonth"+paybillMonth);
			gLogger.info("Year"+Year);
			inputMap.put("fileNumber", fileNumber);
			gLogger.info("fileNumber"+fileNumber);
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
     gLogger.info("stringMonth****"+monthMap.get(1));
     gLogger.info("stringMonth"+monthMap.get(paybillMonth));     
     
    
     stringMonth=(String) monthMap.get(paybillMonth);
     gLogger.info("stringMonth"+stringMonth);
			gLogger.info("employeeContribution"+employeeContribution);
			gLogger.info("employerContribution"+employerContribution);
			inputMap.put("totalContributionAmountInWords", totalContributionAmountInWords);
			inputMap.put("totalContribution", totalContri);
			String treasuryName = lObjNsdlDAO.getTreasuryName(locId);
			transactionId = lObjNsdlDAO.getTransactionId(fileNumber);
			sYear = lObjNsdlDAO.getFYear(fileNumber);
			inputMap.put("transactionId", transactionId);
			inputMap.put("sYear", sYear);
			inputMap.put("treasuryName", treasuryName);
			IdGenerator idGen = new IdGenerator();
			nsdl_paybill_pk = idGen.PKGenerator("NSDL_BILL_dtls", inputMap);
			String status=lObjNsdlDAO.getBillStatus(fileNumber);
			if(status!=null && Long.parseLong(status)==0){
				//lObjNsdlDAO.createNSDLBillGenration(nsdl_paybill_pk,Year,Month,employeeContribution,employerContribution,totalContribution,fileNumber);
				billDetails=lObjNsdlDAO.getBillNoDate(fileNumber);	

				inputMap.put("viewmsg", "bill is not yet generated");
				inputMap.put("billNo", billNo);
				inputMap.put("billCreationDate", billCreationDate);
				inputMap.put("billGeneratedMonth", billGeneratedMonth);

				resObj.setResultValue(inputMap);
				resObj.setViewName("NPSVALIDATE");
			}else{
				billDetails=lObjNsdlDAO.getBillNoDate(fileNumber);

				if(billDetails!=null && !billDetails.isEmpty()){
					Iterator it =  billDetails.iterator();
					while(it.hasNext()){
						obj = (Object[]) it.next();
						billNo = Long.parseLong(obj[0].toString());
						gLogger.info("billNo"+billNo);
						billCreationDate=obj[1].toString();
						gLogger.info("billCreationDate"+billCreationDate);
						billGeneratedMonth=obj[2].toString();
					}

				}
				inputMap.put("billNo", billNo);
				inputMap.put("billCreationDate", billCreationDate);
				inputMap.put("billGeneratedMonth", billGeneratedMonth);				
				inputMap.put("employeeContribution", empContri);
				inputMap.put("employerContribution", emplrContri);
				inputMap.put("employeeContributionInWords", employeeContributionInWords);
				inputMap.put("employerContributionInWords", employerContributionInWords);
				inputMap.put("selectedMonth", stringMonth);
				inputMap.put("scheme", scheme);
				resObj.setResultValue(inputMap);
				resObj.setViewName("mtr45inner");
			}



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
	public ResultObject voucherEntry(Map inputMap) {

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


		try {
			setSessionInfo(inputMap);
			String Month = StringUtility.getParameter("cmbMonth", request);
			String Year = StringUtility.getParameter("cmbYear", request);
			String fileNumber = StringUtility.getParameter("fileNumber", request);
			gLogger.info("Month"+Month);
			gLogger.info("Year"+Year);
			gLogger.info("fileNumber"+fileNumber);
			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			List lLstMonths=lObjDcpsCommonDAO.getMonths();
			inputMap.put("Month", Month);
			inputMap.put("Year", Year);
			inputMap.put("fileNumber", fileNumber);
			resObj.setResultValue(inputMap);
			resObj.setViewName("approveNSDLBill");

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
	public ResultObject approveNSDLPayBill(Map inputMap) {

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


		try {
			setSessionInfo(inputMap);
			String month = StringUtility.getParameter("Month", request);			
			String year = StringUtility.getParameter("Year", request);
			String voucherNo = StringUtility.getParameter("voucherNo", request);
			String vouchedate = StringUtility.getParameter("voucherDate", request);
			gLogger.info("year"+year);
			gLogger.info("month"+month);
			gLogger.info("voucherNo"+voucherNo);
			gLogger.info("vouchedate"+vouchedate);
			String fileNumber = StringUtility.getParameter("fileNumber", request);
			gLogger.info("fileNumber"+fileNumber);
			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());
			List lLstMonths=lObjDcpsCommonDAO.getMonths();
			inputMap.put("month", month);
			inputMap.put("year", year);
			inputMap.put("fileNumber", fileNumber);
			if(fileNumber!=null){
				lObjNsdlDAO.updateVoucherEntry(month,year,fileNumber,voucherNo,vouchedate);	
				inputMap.put("msg", "Details Apporved Successfully");
			}

			resObj.setResultValue(inputMap);
			resObj.setViewName("approveNSDLBill");

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
//$t 6-11 2019
	public ResultObject validateFileNSdl(Map inputMap) {

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
		String dhDtls="";
		String ddoRegNo="";
		StringBuilder sb11 = new StringBuilder();
		String errorData=" ";
		String ext="";
		String digiActivate="0";
		String cmbDep="2";
		List nsdlDeatils=null;
		
		try {
			setSessionInfo(inputMap);
			String Fileno = StringUtility.getParameter("Fileno", request);	
			String trCode=gStrLocationCode;
			if(StringUtility.getParameter("digiActivate", this.request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("digiActivate", this.request)))
		      {
		    	  digiActivate = StringUtility.getParameter("digiActivate", this.request);
		      }
			String month = StringUtility.getParameter("cmbMonth", request);
			String year = StringUtility.getParameter("cmbYear", request);
			trCode=StringUtility.getParameter("trCode", request);
			if(StringUtility.getParameter("cmbDep", request)!=null && !(StringUtility.getParameter("cmbDep", request).equals("")) && Long.parseLong(StringUtility.getParameter("cmbDep", request))!=-1)
				{
					cmbDep=StringUtility.getParameter("cmbDep", request);
				}

			//$t 6-11 2019  
			String lastChar = (Fileno.charAt(Fileno.length() -1))+"";
			lastChar.trim();
		    gLogger.info("check file deputation is *********"+lastChar);
			
			gLogger.info("Fileno****************"+Fileno);
			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
			File f4=null;
			f4=new File(Fileno.concat(".txt"));
			f4.delete();
			f4.createNewFile();
			gLogger.info("New file created");
			

			String filePath=request.getSession().getServletContext().getRealPath("/") +"/"+ Fileno.concat(".txt");

			NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
			
			String dtoRegNo=lObjNsdlNps.getDtoRegNo(Fileno.substring(0, 2));
			

			FileWriter fw = new FileWriter(filePath);
			BufferedWriter br = new BufferedWriter(fw);

			br.write("1"+"^");   		 br.write("FH"+"^"); 		 br.write("P"+"^"); 		 br.write(dtoRegNo+"^");		 br.write("1"+"^");
			br.write("^"+"A");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^"); 
			br.write("\r\n");


			String bhData=lObjNsdlDAO.getBatchData(Fileno);
			br.write(bhData);
			br.write("\r\n");


			if(!lastChar.equals("D")){
			List dhData=lObjNsdlDAO.getDHData(Fileno);
			List dhData1 = lObjNsdlNps.getDHData1(Fileno);
			
			if (dhData != null && dhData.size() > 0)
			{
				Iterator IT = dhData.iterator();

				Object[] lObj = null;
				while (IT.hasNext())
				{
					lObj = (Object[]) IT.next();
					dhDtls = lObj[0].toString();
					ddoRegNo = lObj[1].toString();
					br.write(dhDtls);

					br.write("\r\n");


					List sdDtls=lObjNsdlDAO.getSDDtls(Fileno,ddoRegNo);
					for(int i=0;i<sdDtls.size();i++){
						br.write(sdDtls.get(i).toString());
						br.write("\r\n");


					}

				}

			}
			if ((dhData1 != null) && (dhData1.size() > 0))
	        {
	          Iterator IT = dhData1.iterator();
	          
	          Object[] lObj = null;
	          List sdDtls1;
	          int i;
	          while (IT.hasNext())
				{
					lObj = (Object[]) IT.next();
					dhDtls = lObj[0].toString();
					ddoRegNo = lObj[1].toString();
				//	outputfile.write(dhDtls);

					//outputfile.write("\r\n");

					br.append(dhDtls);
					br.append("\r\n");

					 sdDtls1=lObjNsdlNps.getSDDtls1(Fileno,ddoRegNo);
					for(i=0;i<sdDtls1.size();i++){
						//outputfile.write(sdDtls.get(i).toString());
						//outputfile.write("\r\n");
						br.append(sdDtls1.get(i).toString());
						br.append("\r\n");

					}
				}
	        }
			
		}else{
			List dhData=lObjNsdlDAO.getDHDataDeputation(Fileno);
			if (dhData != null && dhData.size() > 0)
			{
				Iterator IT = dhData.iterator();

				Object[] lObj = null;
				while (IT.hasNext())
				{
					lObj = (Object[]) IT.next();
					dhDtls = lObj[0].toString();
					ddoRegNo = lObj[1].toString();
					br.write(dhDtls);

					br.write("\r\n");


					List sdDtls=lObjNsdlDAO.getSDDtlsDeputation(Fileno,ddoRegNo);
					for(int i=0;i<sdDtls.size();i++){
						br.write(sdDtls.get(i).toString());
						br.write("\r\n");

					}
				}
			}
		}
		//end	
			br.close();

			gLogger.info("filePath is***********"+filePath.toString());
			gLogger.info("path is  is***********"+fw.toString());
			gLogger.info("fw is***********"+br.toString());


			String fvuFilePtah=filePath.replace("txt", "fvu");
			String errFilePtah=filePath.replace("txt", "err");
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String[] args = {filePath,
					errFilePtah, 
					fvuFilePtah,"0","1.44"};
			gLogger.info("inputParametersis "+args[0]);
			gLogger.info("inputParametersis "+args[1]);
			gLogger.info("inputParametersis "+args[2]);
			
			
			
		
	

			 
			int fileStatus=0;
			PAOFvu.main(args);
			
			
			
			
			/////////////////////////////////////////Added By Roshan on 02/11/2015//////////////////////////////////////////
			
			
			

//		    System.out.println("start Time :: " + System.currentTimeMillis());
//		    if (args.length == 5)
//		    {
//		      String inFileNameSAM = args[0];
//		      String errorFileNameSAM = args[1];
//		      String outFileNameSAM = args[2];
//		      String utilityLevelSamScm = args[3];
//		      String samScmFvuVersion = args[4];
//
//		      File f1 = new File(inFileNameSAM);
//		      File f2 = new File(outFileNameSAM);
//
//		      int utilityLevelInt = Integer.parseInt(utilityLevelSamScm.trim());
//
//		      StringBuffer FH_obj_StringBuffer = new StringBuffer();
//		      ArrayList listErrorVO = new ArrayList();
//
//		      if ((((utilityLevelInt == 1) || (utilityLevelInt == 2))) && 
//		        (!(samScmFvuVersion.equals(CRAConfigReader.getConfigVal("CRACENTRAL.FVU.FVUVersion")))))
//		      {
//		        listErrorVO.add(new PAOContrErrorFileVO("-", 201019, "File Header Record", "-", "-", "-", "1", "-"));
//		        FH_obj_StringBuffer = convertArrayListtoBuffer(listErrorVO);
//		        try
//		        {
//		          CRABasicFileWriter.writeFileLocal(errorFileNameSAM, FH_obj_StringBuffer);
//		        }
//		        catch (IOException e)
//		        {
//		          e.printStackTrace();
//		        }
//		        System.exit(0);
//		      }
//
//		      SubContrFileFormatValidator obj_FormValidator = new SubContrFileFormatValidator();
//		      try
//		      {
//		        obj_FormValidator.readFile(inFileNameSAM, listErrorVO, utilityLevelInt);
//		      }
//		      catch (Exception e1)
//		      {
//		        e1.printStackTrace();
//		      }
//		      if (listErrorVO.size() == 0)
//		      {
//		        Hash hashObj = new Hash();
//		        int hashCode = hashObj.startProcessing(inFileNameSAM, outFileNameSAM, utilityLevelInt, 5);
//
//		        if (hashCode != 0)
//		        {
//		          int lineNumber = hashObj.getRecordNumber();
//		          if (hashCode == 3)
//		          {
//		            listErrorVO.add(new PAOContrErrorFileVO("-", 201012, "File Header Record", "-", "-", "-", "1", "-"));
//		          }
//		          else if (hashCode == 4)
//		          {
//		            listErrorVO.add(new PAOContrErrorFileVO("-", 201013, "File Header Record", "-", "-", "-", "1", "-"));
//		          }
//		          else if (hashCode == 5)
//		          {
//		            listErrorVO.add(new PAOContrErrorFileVO("-", 201014, "File Header Record", "-", "-", "-", "1", "-"));
//		          }
//		          else if (hashCode == 9)
//		          {
//		            listErrorVO.add(new PAOContrErrorFileVO("-", 201015, "File Header Record", "-", "-", "-", "1", "-"));
//		          }
//		          else if (hashCode == 10)
//		          {
//		            listErrorVO.add(new PAOContrErrorFileVO("-", 201016, "File Header Record", "-", "-", "-", "1", "-"));
//		          }
//		          else if (hashCode == 11)
//		          {
//		            listErrorVO.add(new PAOContrErrorFileVO("-", 201017, "File Header Record", "-", "-", "-", "1", "-"));
//		          }
//		          else
//		          {
//		            listErrorVO.add(new PAOContrErrorFileVO("-", 201018, "-", "-", "-", "-", lineNumber+"", "-"));
//		          }
//
//		          try
//		          {
//		            FH_obj_StringBuffer = convertArrayListtoBuffer(listErrorVO);
//		            CRABasicFileWriter.writeFileLocal(errorFileNameSAM, FH_obj_StringBuffer);
//		          }
//		          catch (IOException e)
//		          {
//		            e.printStackTrace();
//		          }
//		        }
//		        else if (hashCode == 0)
//		        {
//		          try
//		          {
//		            String dataRecord = null;
//
//		            String samScmString = obj_FormValidator.getControlSheetBuffer().toString();
//		            StringBuffer samScmStringBuffer = getRAWData(samScmString, "^");
//
//		            String samScmDataFlNm = outFileNameSAM + ".raw";
//
//		            CRABasicFileWriter.writeFileLocal(samScmDataFlNm, samScmStringBuffer);
//		          }
//		          catch (Exception e)
//		          {
//		            e.printStackTrace();
//		          }
//		        }
//		       // f1.renameTo(f2);
//		      }
//		      else
//		      {
//		        try
//		        {
//		          FH_obj_StringBuffer = convertArrayListtoBuffer(listErrorVO);
//		          CRABasicFileWriter.writeFileLocal(errorFileNameSAM, FH_obj_StringBuffer);
//		        }
//		        catch (IOException e)
//		        {
//		          e.printStackTrace();
//		        }
//
//		      }
//
//		    }
//		    else if (args.length == 0)
//		    {
//		      PAOFvu f = new PAOFvu();
//		      f.setVisible(true);
//		    }
		    
		    
		    
		    
		    
		    ////////////////////////////////////////////////////////////////////////////
			 
		 
			
			File f5=null;
			f5=new File(Fileno.concat(".txt"));
			System.out.println("File to be deleted"+f5);
			f5.deleteOnExit();

			
			
			File f = new File(new File(fvuFilePtah).getAbsolutePath());
			File f1 = new File(new File(errFilePtah).getAbsolutePath());
			
			if(f.exists() && !f.isDirectory()){
				BufferedReader br1 = new BufferedReader(new FileReader(new File(fvuFilePtah).getAbsolutePath()));
				
				ext=".fvu";
				String line = br1.readLine();
				fileStatus=1;
				while (line != null) {
					sb11.append(line);
					sb11.append("\r\n");
					gLogger.info(sb11.toString());
					line = br1.readLine();
				}
			}
			
			else if(f1.exists() && !f1.isDirectory()){
				BufferedReader br1 = new BufferedReader(new FileReader(new File(errFilePtah).getAbsolutePath()));
				
				ext=".err";
				String line = br1.readLine();
				fileStatus=2;
				while (line != null) {
					sb11.append(line);
					sb11.append("\r\n");
					gLogger.info(sb11.toString());
					line = br1.readLine();
					
				}
				
				errorData=sb11.toString();
				
			}
			

			lObjNsdlDAO.updateFileStatus(fileStatus,Fileno,errorData);
			if(digiActivate.equalsIgnoreCase("0"))
			{
				PrintWriter outputfile =response.getWriter();
				try{
					String fileName = Fileno.concat(ext);
					response.setContentType("text/plain;charset=UTF-8");

					response.addHeader("Content-disposition",
							"attachment; filename=" + fileName);
					response.setCharacterEncoding("UTF-8");


					outputfile.write(sb11.toString());
					outputfile.flush();

				}
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					gLogger.info("All fine 7 is *********");
					if(outputfile!=null)
						outputfile.close();
				}
				resObj.setResultValue(inputMap);
				resObj.setViewName("ExportReportPage");
			}
			else
			{
				if(cmbDep!=null && !"".equalsIgnoreCase(cmbDep) && "2".equalsIgnoreCase(cmbDep))
				{
					nsdlDeatils=lObjNsdlDAO.getAllData(year.toString(), month.toString(), trCode);
				}
				else
				{
					nsdlDeatils=lObjNsdlDAO.getAllDataDeputation(year.toString(), month.toString(), trCode);
				}


				inputMap.put("selMonth", month);
				inputMap.put("selYear", year);
				List subTr=lObjNsdlDAO.getAllSubTreasury(gStrLocationCode);
				inputMap.put("subTr", subTr);
			inputMap.put("digiActivate", digiActivate);
			List lLstYears = lObjNsdlDAO.getFinyear();
			
			List lLstMonths=lObjDcpsCommonDAO.getMonths();
			inputMap.put("size", nsdlDeatils.size());
			inputMap.put("nsdlDeatils", nsdlDeatils);
			inputMap.put("subTrCode", trCode);
			inputMap.put("lLstYears", lLstYears);
			inputMap.put("lLstMonths", lLstMonths);
			inputMap.put("idDeputation",cmbDep);
			resObj.setResultValue(inputMap);
			resObj.setViewName("NPSVALIDATE");
			}
			
			gLogger.info("All fine 8 is *********");
			
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

	private StringBuffer getRAWData(String samScmString, String string) {
		StringBuilder rawDataBuffer = new StringBuilder();
		StringTokenizer rawData = new StringTokenizer(samScmString, string);

		int counter = 0;
		while (rawData.hasMoreTokens())
		{
			String token = rawData.nextToken();
			if (counter == 0)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 1)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 5)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 6)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 7)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 8)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 9)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 10)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 11)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 12)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 13)
			{
				rawDataBuffer.append(token.trim() + "^");
			}
			else if (counter == 14)
			{
				rawDataBuffer.append(token.trim() + "^");
			}

			++counter;
		}
		return new StringBuffer(rawDataBuffer.toString());
	}

	private StringBuffer convertArrayListtoBuffer(ArrayList listErrorVO) {
		StringBuffer errorBuffer = new StringBuffer();

		String tagName = null;

		String ErrorDescription = null;
		String recordType = null;
		String serialNumberInDDO = null;
		String serialNumber = null;
		String lineNumber = null;

		for (int i = 0; i < listErrorVO.size(); ++i)
		{
			tagName = ((PAOContrErrorFileVO)listErrorVO.get(i)).getTagName();
			String errorCode = new Integer(((PAOContrErrorFileVO)listErrorVO.get(i)).getErrorCode()).toString();
			ErrorDescription = ((PAOContrErrorFileVO)listErrorVO.get(i)).getErrorDescription();
			recordType = ((PAOContrErrorFileVO)listErrorVO.get(i)).getRecordType();
			serialNumberInDDO = ((PAOContrErrorFileVO)listErrorVO.get(i)).getSerialNumberInDDO();
			serialNumber = ((PAOContrErrorFileVO)listErrorVO.get(i)).getSerialNumber();
			lineNumber = ((PAOContrErrorFileVO)listErrorVO.get(i)).getLineNumber();

			errorBuffer.append(tagName + "^" + errorCode + "^" + ErrorDescription + "^" + recordType + "^" + serialNumberInDDO + "^" + serialNumber + "^" + lineNumber);
			errorBuffer.append("\n");
		}

		return errorBuffer;
	}




	/*public ResultObject sendFileToNSDL(Map inputMap) {

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


		try {
			setSessionInfo(inputMap);
			String Fileno = StringUtility.getParameter("Fileno", request);			
			String filePath = StringUtility.getParameter("filePath", request);
			gLogger.info("filePath"+filePath);
			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
			String fvuFilePtah=filePath.replace("txt", "fvu");
			String errFilePtah=filePath.replace("txt", "err");
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
					.getSessionFactory());

			String[] args = {filePath,
					errFilePtah, 
					fvuFilePtah,"0","1.44"};
			gLogger.info("inputParametersis "+args[0]);
			gLogger.info("inputParametersis "+args[1]);
			gLogger.info("inputParametersis "+args[2]);
			gLogger.info("inputParametersis "+args);







			BufferedReader br = new BufferedReader(new FileReader(filePath));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}


			gLogger.info("file data is"+sb);

			PasswordEncryption objPasswordEncryption = new PasswordEncryption();
			gLogger.info("password encryption is ****************"+objPasswordEncryption.crypt(sb.toString()));
			int count=lObjNsdlDAO.checkForFileDtls(objPasswordEncryption.crypt(sb.toString()),Fileno);
			int fileStatus=0;

			String msg="";






			String exsistingFileName = "D:/user1000010700.sig";
			String zipFileName = "D:/opt/webnas/CRA/STP/Akki.txt";
			gLogger.info("till here 1");
			byte[] fileByte;
			com.cra.stp.core.webservice.STPWebServicePOJO_PortType client;
			gLogger.info("till here 12");
			QName serviceName = new QName("http://webservice.core.stp.cra.com/", "STPWebServicePOJOService");
			gLogger.info("till here 13");
			QName portName = new QName("http://webservice.core.stp.cra.com/", "STPWebServicePOJOPort");
			gLogger.info("till here 14");
			Service service = Service.create(serviceName);
			service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING,
			"http://121.240.246.24:8088/STPWebServicePOJOPort"); 
			gLogger.info("till here 15");
			client = service.getPort(portName,  com.cra.stp.core.webservice.STPWebServicePOJO_PortType.class);
			gLogger.info("till here 16");
			File zipFile = new File(exsistingFileName);
			gLogger.info("till here 17");
			@SuppressWarnings("resource")
			FileInputStream fis = new FileInputStream(exsistingFileName);
			fileByte = new byte[(int) zipFile.length()];
			gLogger.info("till here 18");
			fis.read(fileByte);
			uploadFile(client);
			fileStatus=5;
			lObjNsdlDAO.updateFileStatus(fileStatus,Fileno,"");

			inputMap.put("msg", msg);
			resObj.setResultValue(inputMap);
			resObj.setViewName("SendNSDLFiles");

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
*//*
	private void uploadFile(STPWebServicePOJO_PortType client) throws IOException {
		gLogger.info("till here 19");
		String exsistingFileName = "D:/user1000010700.sig";
		String zipFileName = "D:/Akki.txt";
		byte[] fileByte;

		File zipFile = new File(zipFileName);
		@SuppressWarnings("resource")
		FileInputStream fis = new FileInputStream(zipFileName);
		fileByte = new byte[(int) zipFile.length()];
		fis.read(fileByte);
		gLogger.info("till here 20");
		byte[][] inputParams = new byte[4][];
		inputParams[0] = "180001009".getBytes();
		inputParams[1] = "Upload SubscriberRegistration-PWD".getBytes();
		inputParams[2] = fileByte;
		inputParams[3] = "PAO".getBytes();

		File xmlOut = new File("/opt/webnas/CRA/STP/uploadFileRes.xml");
		FileOutputStream fout = new FileOutputStream(xmlOut);

		fout.write(client.performFileUpload(inputParams).getBytes());

		fout.close();


	}

*/


	public ResultObject createErrorFilesForNSDL(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		 
		String BatchId=null;
		 String dtoRegNo=null;
		try {
			setSessionInfo(inputMap);
			if(StringUtility.getParameter("fileNumber", request)!=null && !StringUtility.getParameter("fileNumber", request).equals(""))
			{
				String fileNumber = StringUtility.getParameter("fileNumber", request);
				NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
			
				 dtoRegNo=lObjNsdlNps.getDtoRegNo(fileNumber.substring(0, 2));
				BatchId=fileNumber;
				PrintWriter outputfile =response.getWriter();
				getFileHeader(outputfile,dtoRegNo);
				String bhData=lObjNsdlNps.getErrorData(fileNumber);
				outputfile.write(bhData);
				String lStrFileName=BatchId;
				try{
					String fileName = lStrFileName + ".err";
					response.setContentType("text/plain;charset=UTF-8");
					response.addHeader("Content-disposition", "attachment; filename=" + fileName);
					response.setCharacterEncoding("UTF-8");
					gLogger.info("File is"+outputfile);
					outputfile.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(outputfile!=null)
						outputfile.close();
				}
			}
			resObj.setResultValue(inputMap);
			resObj.setViewName("ExportReportPage");
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
	
	public ResultObject createStatusFiles(Map inputMap) {

		ResultObject resObj = new ResultObject(ErrorConstants.SUCCESS, "FAIL");

		 
		//String BatchId=null;
		 //String dtoRegNo=null;
		try {
			setSessionInfo(inputMap);
			if(StringUtility.getParameter("fileNumber", request)!=null && !StringUtility.getParameter("fileNumber", request).equals(""))
			{
				String fileNumber = StringUtility.getParameter("fileNumber", request);
				NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
			
				// dtoRegNo=lObjNsdlNps.getDtoRegNo(fileNumber.substring(0, 2));
				//BatchId=fileNumber;
				PrintWriter outputfile =response.getWriter();
				//getFileHeader(outputfile,dtoRegNo);
				String bhData=lObjNsdlNps.getErrorDataOfNSDL(fileNumber);
				outputfile.write(bhData);
				String lStrFileName="NSDL Response";
				try{
					String fileName = lStrFileName + ".err";
					response.setContentType("text/plain;charset=UTF-8");
					response.addHeader("Content-disposition", "attachment; filename=" + fileName);
					response.setCharacterEncoding("UTF-8");
					gLogger.info("File is"+outputfile);
					outputfile.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(outputfile!=null)
						outputfile.close();
				}
			}
			resObj.setResultValue(inputMap);
			resObj.setViewName("ExportReportPage");
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


	public ResultObject generateFVUorErrorFile(Map inputMap) {

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
		String BatchId=null;
		String  dhDtls="";
		String ddoRegNo="";
		
		try {
			setSessionInfo(inputMap);
			PrintWriter outputfile =response.getWriter();
			if(StringUtility.getParameter("filePath", request)!=null && !StringUtility.getParameter("filePath", request).equals(""))
			{
				String filePath = StringUtility.getParameter("filePath", request);
				
				String filename = StringUtility.getParameter("filename", request);
				
				String ext = StringUtility.getParameter("ext", request);
				 
				BufferedReader br1 = new BufferedReader(new FileReader(new File(filePath).getAbsolutePath()));
				
				StringBuilder sb1 = new StringBuilder();
				String line = br1.readLine();

				while (line != null) {
					sb1.append(line);
					sb1.append("\r\n");
					line = br1.readLine();
				}
				
				gLogger.info("All fine 4 is *********");
				//gLogger.info("stringBuifefer is *********"+sb.toString());

				//PasswordEncryption objPasswordEncryption = new PasswordEncryption();
				//gLogger.info("password encryption is ****************"+objPasswordEncryption.crypt(sb.toString()));
				//lObjNsdlNps.updateMD5hash(objPasswordEncryption.crypt(sb.toString()),fileNumber);   

				String lStrFileName=BatchId;

				try{
					String fileName = filename.concat(ext);
					response.setContentType("text/plain;charset=UTF-8");

					response.addHeader("Content-disposition",
							"attachment; filename=" + fileName);
					response.setCharacterEncoding("UTF-8");


					outputfile.write(sb1.toString());
					outputfile.flush();

				}
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					gLogger.info("All fine 7 is *********");
					if(outputfile!=null)
						outputfile.close();
				}


			}

			gLogger.info("All fine 8 is *********");
			resObj.setResultValue(inputMap);
			resObj.setViewName("ExportReportPage");
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

	
	public ResultObject sendFileToNSDL(Map inputMap) {
/////////////////////$t very imp for digital signature code changes as a 14% employer changes
///////////////////// createTextFile and validateTextFile we added dhData list two times in these methods
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
		String dhDtls="";
		String ddoRegNo="";
		StringBuilder sb11 = new StringBuilder();
		String errorData=" ";
		String ext="";
		String errorCode="";
		String errorDesc="";
		String ifDeputation="2";
		boolean flag=false;
		String trCode="";
		String userId="";
		String digiActivate="0";
		try {
			setSessionInfo(inputMap);
			String Fileno = StringUtility.getParameter("Fileno", request);	
			gLogger.info("Fileno****************"+Fileno);
			String treasury=Fileno.substring(0, 2);
			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
			  yearId = Long.valueOf(StringUtility.getParameter("Year", this.request));
		      monthId = Long.valueOf(StringUtility.getParameter("Month", this.request));
		      if(StringUtility.getParameter("cmbDep", this.request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("cmbDep", this.request)))
		      {
		    	  ifDeputation = StringUtility.getParameter("cmbDep", this.request);
		      }
		      if(StringUtility.getParameter("trCode", this.request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("trCode", this.request)))
		      {
		    	  trCode = StringUtility.getParameter("trCode", this.request);
		      }
		      if(StringUtility.getParameter("digiActivate", this.request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("digiActivate", this.request)))
		      {
		    	  digiActivate = StringUtility.getParameter("digiActivate", this.request);
		      }
		      if(digiActivate.equalsIgnoreCase("1"))
		      {
		    	  
						File f4=null;
						f4=new File(Fileno.concat(".txt"));
						f4.delete();
						f4.createNewFile();
						gLogger.info("New file created");

						String filePath=request.getSession().getServletContext().getRealPath("/") +"/"+ Fileno.concat(".txt");

						NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
						
						String dtoRegNo=lObjNsdlNps.getDtoRegNo(Fileno.substring(0, 2));

						FileWriter fw = new FileWriter(filePath);
						BufferedWriter br = new BufferedWriter(fw);

						br.write("1"+"^");   		 br.write("FH"+"^"); 		 br.write("P"+"^"); 		 br.write(dtoRegNo+"^");		 br.write("1"+"^");
						br.write("^"+"A");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^"); 
						br.write("\r\n");


						String bhData=lObjNsdlDAO.getBatchData(Fileno);
						br.write(bhData);
						br.write("\r\n");



						List dhData=lObjNsdlDAO.getDHData(Fileno);
						if (dhData != null && dhData.size() > 0)
						{
							Iterator IT = dhData.iterator();

							Object[] lObj = null;
							while (IT.hasNext())
							{
								lObj = (Object[]) IT.next();
								dhDtls = lObj[0].toString();
								ddoRegNo = lObj[1].toString();
								br.write(dhDtls);

								br.write("\r\n");


								List sdDtls=lObjNsdlDAO.getSDDtls(Fileno,ddoRegNo);
								for(int i=0;i<sdDtls.size();i++){
									br.write(sdDtls.get(i).toString());
									br.write("\r\n");


								}

							}

						}

						br.close();

						gLogger.info("filePath is***********"+filePath.toString());
						gLogger.info("path is  is***********"+fw.toString());
						gLogger.info("fw is***********"+br.toString());


						String fvuFilePtah=filePath.replace("txt", "pao");
						String errFilePtah=filePath.replace("txt", "err");
						DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
								.getSessionFactory());

						String[] args = {filePath,
								errFilePtah, 
								fvuFilePtah,"0","1.44"};
						gLogger.info("inputParametersis "+args[0]);
						gLogger.info("inputParametersis "+args[1]);
						gLogger.info("inputParametersis "+args[2]);
				

						 
						int fileStatus=5;
						PAOFvu.main(args);

						File f5=null;
						f5=new File(Fileno.concat(".txt"));
						System.out.println("File to be deleted"+f5);
						f5.deleteOnExit();

						File f = new File(new File(fvuFilePtah).getAbsolutePath());
				
						File f1 = new File(new File(errFilePtah).getAbsolutePath());
						String fileSentStatus="";
						String paoFilePath="";
						String digiSignPath="";
						if(f.exists() && !f.isDirectory()){
							BufferedReader br1 = new BufferedReader(new FileReader(new File(fvuFilePtah).getAbsolutePath()));
							//gLogger.info("path is*******BEFORE RENAIMING*****"+f.getAbsolutePath());
							//File file2 = new File(request.getSession().getServletContext().getRealPath("/") + Fileno.concat(".pao")); 
							//gLogger.info("path is******file2****"+file2.getAbsolutePath());
							//f.renameTo(file2);
							//gLogger.info("path is************"+f.getAbsolutePath());
							
							//to be revereted
							paoFilePath=	f.getAbsolutePath();
							//paoFilePath="D:/signedFiles/2007200812011.pao";
							//to be revereted
							digiSignPath=paoFilePath.replace(".pao", ".sig");
							String cerFilePath="";
							if(treasury.equalsIgnoreCase("22"))
							{
								 cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/DTO PUNE.pfx");
								 userId="1007889730";
							}
							else if (treasury.equalsIgnoreCase("31"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Aurangabad_pfx.pfx");
								 userId="1007867330";
							}
							else if (treasury.equalsIgnoreCase("25"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Solapur.pfx");
								 userId="1007880630";
							}
							else if (treasury.equalsIgnoreCase("13"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Raigad_pfx.pfx");
								 userId="1007877230";
								 
							}
							else if (treasury.equalsIgnoreCase("16"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/palghar_pfx.pfx");
								 userId="1007895430";
								 
							}
							/*        add by kavita             */
							else if (treasury.equalsIgnoreCase("36"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/jalna_pfx.pfx");
								 userId="1007893930";
								 
							}
							
							else if (treasury.equalsIgnoreCase("31"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/ahmednagar_pfx.pfx");
								 userId="1007866530"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("63"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/buldhana_pfx.pfx");
								 userId="1007868130"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("48"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/CHANDRAPUR_pfx.pfx");
								 userId="1007869930"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("49"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/gadchiroli_pfx.pfx");
								 userId="1007870730"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("44"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/gondia_pfx.pfx");
								 userId="1007871530"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("62"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Akola_pfx.pfx");
								 userId="1007872330"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("47"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Bhandara_pfx.pfx");
								 userId="1007873130"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("53"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/jalgaon_pfx.pfx");
								 userId="1007874930"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("46"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Nagpur_pfx.pfx");
								 userId="1007875630"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("55"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/nandurbar_pfx.pfx");
								 userId="1007876430"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("14"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Ratnagiri_pfx.pfx");
								 userId="1007878030"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("14"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Ratnagiri_pfx.pfx");
								 userId="1007878030"; 
								 
							}
							
							else if (treasury.equalsIgnoreCase("14"))
							{
								cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Ratnagiri_pfx.pfx");
								 userId="1007878030"; 
								 
							}
							

							
							gLogger.info("paoFilePath is************"+paoFilePath);
							gLogger.info("digiSignPath is************"+digiSignPath);
							gLogger.info("cerFilePath is************"+cerFilePath);
							
							
							String nom2_gur="";
								if(!nom2_gur.trim().equals("NA")){
						            //System.out.println( "hii ");
						        }
						        
								gLogger.info(nom2_gur.trim().toString());
						        
						        PKICtrl pkiCtrl=new PKICtrl();
						        String status="";
						        if(treasury.equalsIgnoreCase("22"))
								{
						        	status= pkiCtrl.signCMSDataWithCertificate(paoFilePath, digiSignPath, cerFilePath, "ifms123", true);
								}
								else 
								{
									status= pkiCtrl.signCMSDataWithCertificate(paoFilePath, digiSignPath, cerFilePath, "123", true);
								}
						        
						        gLogger.info("signing file startted**************"+status);
						        
						        String veriStatus = pkiCtrl.verifyCMSSignedData(digiSignPath,paoFilePath,  true); //$NON-NLS-1$
						        
						        gLogger.info("veirification completed*******"+veriStatus);

						        fileSentStatus="file Validated and Sent to NSDL..";
						}
						else{
							fileSentStatus="file not Validated Succeesfully.";
							
						}
						
						String[] args1=null;
						//STPWebServicePOJO_STPWebServicePOJOPort_Client.main(args1);
						//Added this code for file sending to NSDL
						
						 
						
						
						
						
						
						
						
					        List<String> files = new ArrayList<String>();
					        files.add(paoFilePath);
					        files.add(digiSignPath);
					        zipFiles(files,paoFilePath.replace("pao", "zip"));
					        
					      
						
			/*			 final QName SERVICE_NAME = new QName("http://webservice.core.stp.cra.com/", "STPWebServicePOJOService");

						 STPWebServicePOJOService ss = new STPWebServicePOJOService(new URL("http://121.240.64.237/STPWeb/STPWebServicePOJOPort?wsdl"), SERVICE_NAME);
					        STPWebServicePOJO port = ss.getSTPWebServicePOJOPort();  
					        
					        {
					        	 System.out.println("Invoking performFileUpload..from Sevaarth Service.....");
					             java.util.List<byte[]> _performFileUpload_arg0 = new java.util.ArrayList<byte[]>();
					             
					             String zipFileName = paoFilePath.replace("pao", "zip");
					             PerformFileUpload fileUpload = new PerformFileUpload();
					             byte[] fileByte;
					             File zipFile = new File(zipFileName);

					             FileInputStream fis = new FileInputStream(zipFileName);

					             fileByte = new byte[(int) zipFile.length()];
					             fis.read(fileByte);
					             
					             System.out.println(fileByte.toString());
					             
					             
					             byte[][] _performFileUpload_arg0Val1 = new byte[3][0];
					             _performFileUpload_arg0Val1[0] = "1008096803".getBytes();
					             _performFileUpload_arg0Val1[1] = "Upload SubscriberContribution-DSC".getBytes();
					             _performFileUpload_arg0Val1[2] = fileByte; // zip file byte
					             _performFileUpload_arg0.add(_performFileUpload_arg0Val1[0]);
					             _performFileUpload_arg0.add(_performFileUpload_arg0Val1[1]);
					             _performFileUpload_arg0.add(_performFileUpload_arg0Val1[2]);
					             java.lang.String _performFileUpload__return = port.performFileUpload(_performFileUpload_arg0);
					             System.out.println("performFileUpload.result=" + _performFileUpload__return);
					             System.out.println("performFileUpload.result=" + _performFileUpload__return);
					             System.out.println("performFileUpload.result=" + _performFileUpload__return);

					        }
					        
					        
			*/
					        /*System.out.println("creating ws");
							System.getProperties().put("https.proxyHost", "proxy.tcs.com");
						       System.getProperties().put("https.proxyPort", "8080");*/
						       
					  
					        final QName SERVICE_NAME = new QName("http://webservice.core.stp.cra.com/", "STPWebServicePOJOService");
					        URL wsdlURL = STPWebServicePOJOService.WSDL_LOCATION;
					        STPWebServicePOJOService ss = new STPWebServicePOJOService(wsdlURL, SERVICE_NAME);
					        
					        STPWebServicePOJO port = ss.getSTPWebServicePOJOPort();  
					        BindingProvider bindingProvider = (BindingProvider) port;
					       
					        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,"https://npscan-cra.com/STPWeb/STPWebServicePOJOPort");
					        
					       //bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,"http://testnpscan.nsdl.com/STPWeb/STPWebServicePOJOPort");
						      
					       
					        
					       // Set timeout until a connection is established 
					        //((BindingProvider) port).getRequestContext().
					                //put("com.sun.xml.internal.ws.request.timeout,", "300000");
					       // Set timeout until the response is received       
					       //((BindingProvider) port).getRequestContext().
					                //put("javax.xml.ws.client.receiveTimeout", "300000"); 
					        
					        

					     //   ((BindingProvider) port).getRequestContext().
				           //     put(BindingProviderProperties.REQUEST_TIMEOUT, 300000);
					        
					        
					      /*  Client client = ClientProxy.getClient(port);
				        	HTTPConduit http = (HTTPConduit) client.getConduit();
				        	HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
				        	httpClientPolicy.setConnectionTimeout(300000);
				        	httpClientPolicy.setReceiveTimeout(300000);
				        	
				        	httpClientPolicy.setAllowChunking(false);
				        	http.setClient(httpClientPolicy);*/
				        	
					        
					        
					        
					        java.lang.String _performFileUpload__return=null;
					        {
					        	gLogger.info("Invoking performFileUpload...");
					             java.util.List<byte[]> _performFileUpload_arg0 = new java.util.ArrayList<byte[]>();
					             
					            // String zipFileName = "D:/Desktop.zip";
					             String zipFileName=request.getSession().getServletContext().getRealPath("/").concat("/").concat(Fileno).concat(".zip");
					             PerformFileUpload fileUpload = new PerformFileUpload();
					             byte[] fileByte;
					             File zipFile = new File(zipFileName);

					             FileInputStream fis = new FileInputStream(zipFileName);

					             fileByte = new byte[(int) zipFile.length()];
					             fis.read(fileByte);
					             
					             System.out.println(fileByte.toString());
					             
					             
					             byte[][] _performFileUpload_arg0Val1 = new byte[3][0];
					             _performFileUpload_arg0Val1[0] = userId.getBytes();
					             _performFileUpload_arg0Val1[1] = "Upload SubscriberContribution-DSC".getBytes();
					             _performFileUpload_arg0Val1[2] = fileByte; // zip file byte
					             _performFileUpload_arg0.add(_performFileUpload_arg0Val1[0]);
					             _performFileUpload_arg0.add(_performFileUpload_arg0Val1[1]);
					             _performFileUpload_arg0.add(_performFileUpload_arg0Val1[2]);
					             _performFileUpload__return = port.performFileUpload(_performFileUpload_arg0);
					             gLogger.info("performFileUpload.result=" + _performFileUpload__return);
					             //System.out.println("performFileUpload.result=" + _performFileUpload__return);
					            // System.out.println("performFileUpload.result=" + _performFileUpload__return);

					        }
					        
					      
							String status=null;
							String frnNumber=null;
							if(_performFileUpload__return!=null && !"".equalsIgnoreCase(_performFileUpload__return))
							{
								
								DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
								DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
								ByteArrayInputStream bis = new ByteArrayInputStream(_performFileUpload__return.getBytes());
								Document doc = dBuilder.parse(bis);

								//optional, but recommended
								//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
								doc.getDocumentElement().normalize();

								

								NodeList nList = doc.getElementsByTagName("response");

								if(nList!=null)
								{
									Node nNode = nList.item(0);
									
									if (nNode.getNodeType() == Node.ELEMENT_NODE) 
									
									{
										Element eElement = (Element) nNode;
										if(eElement.getElementsByTagName("status")!=null && eElement.getElementsByTagName("status").item(0)!=null)
										{
											status=eElement.getElementsByTagName("status").item(0).getTextContent();
											//System.out.println("status : "+status);
											if(status!=null && !"".equalsIgnoreCase(status) && "Uploaded".equalsIgnoreCase(status))
											{
												if(eElement.getElementsByTagName("file-reference-number")!=null && eElement.getElementsByTagName("file-reference-number").item(0)!=null)
												{
													frnNumber=eElement.getElementsByTagName("file-reference-number").item(0).getTextContent();
													
													lObjNsdlDAO.updateFileStatusandFrnNo(fileStatus,Fileno,errorData,frnNumber);
													flag=true;
													
													
												}
											}
										}
										if(eElement.getElementsByTagName("error-code")!=null && eElement.getElementsByTagName("error-code").item(0)!=null)
										{
											errorCode=eElement.getElementsByTagName("error-code").item(0).getTextContent();
										}
										if(eElement.getElementsByTagName("error-description")!=null && eElement.getElementsByTagName("error-description").item(0)!=null)
										{
											errorDesc=eElement.getElementsByTagName("error-description").item(0).getTextContent();
										}
										if(errorCode!=null && !"".equalsIgnoreCase(errorCode) && errorDesc!=null && !"".equalsIgnoreCase(errorDesc))
										{
											lObjNsdlDAO.updateErrorCode(Fileno, errorCode, errorDesc);
										}
										
										
										
									}

							
									
							        
							        
								
								}
								
								
							}
					
		      }
			
			else
			{
				lObjNsdlDAO.updateFileStatusandFrnNo(1,Fileno,errorData,"0");
				flag=true;
				
				
			
			}
			
			
			
				
				
				/*gLogger.info("Invoking perform status enquiry...");
	            java.util.List<byte[]> _performStatusInquiry_arg0 = new java.util.ArrayList<byte[]>();
	            
	            String zipFileName = digiSignPath;
	            PerformFileUpload fileUpload = new PerformFileUpload();
	            byte[] fileByte;
	            File zipFile = new File(zipFileName);

	            FileInputStream fis = new FileInputStream(zipFileName);

	            fileByte = new byte[(int) zipFile.length()];
	            fis.read(fileByte);
	            
	            gLogger.info(fileByte.toString());

	        	byte[][] _performStatusInquiry_arg0Val1 = new byte[5][0];
	             _performStatusInquiry_arg0Val1[0] = "1007889730".getBytes();
	             _performStatusInquiry_arg0Val1[1] = frnNumber.toString().getBytes();
	             _performStatusInquiry_arg0Val1[2] = "10078897".getBytes(); // zip file byte
	             _performStatusInquiry_arg0Val1[3] = fileByte; // zip file byte
	             _performStatusInquiry_arg0Val1[4] = "File Status-SubscriberContribution".getBytes();; // zip file byte
	             
	             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[0]);
	             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[1]);
	             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[2]);
	             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[3]);
	             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[4]);
	             java.lang.String _performStatusInquiry__return = port.performStatusInquiry(_performStatusInquiry_arg0);
	             gLogger.info("performStatusInquiry.result=***************************************" + _performStatusInquiry__return);
	             */
	             
	             
				
				
				
				
				
			gLogger.info("All fine 8 is *********");
			String lSBStatus = getResponseXMLDoc(flag,errorDesc,monthId,yearId,ifDeputation,trCode,digiActivate).toString();
			String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
					lSBStatus).toString();	
			inputMap.put("ajaxKey", lStrResult);
		  
			resObj.setViewName("ajaxData");

			resObj.setResultValue(inputMap);
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
	
	public ResultObject updateTranId(Map inputMap) {

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
		String dhDtls="";
		String ddoRegNo="";
		StringBuilder sb11 = new StringBuilder();
		String errorData=" ";
		String ext="";
		boolean flag=false;
		String ifDeputation="N";
		String trCode="";
		String userId="";
		String entityId="";
		String digiActivate="0";
		try {
			setSessionInfo(inputMap);
			String Fileno = StringUtility.getParameter("Fileno", request);	
			gLogger.info("Fileno****************"+Fileno);
			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
			  yearId = Long.valueOf(StringUtility.getParameter("Year", this.request));
		      monthId = Long.valueOf(StringUtility.getParameter("Month", this.request));
		      if(StringUtility.getParameter("cmbDep", this.request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("cmbDep", this.request)))
		      {
		    	  ifDeputation = StringUtility.getParameter("cmbDep", this.request);
		      }
		      if(StringUtility.getParameter("trCode", this.request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("trCode", this.request)))
		      {
		    	  trCode = StringUtility.getParameter("trCode", this.request);
		      }
		      if(StringUtility.getParameter("digiActivate", this.request)!=null && !"".equalsIgnoreCase(StringUtility.getParameter("digiActivate", this.request)))
		      {
		    	  digiActivate = StringUtility.getParameter("digiActivate", this.request);
		      }
		     
			String treasury=Fileno.substring(0, 2);
			if(digiActivate.equalsIgnoreCase("1"))
			{
				
					File f4=null;
					f4=new File(Fileno.concat(".txt"));
					f4.delete();
					f4.createNewFile();
					gLogger.info("New file created");

					String filePath=request.getSession().getServletContext().getRealPath("/") +"/"+ Fileno.concat(".txt");

					NsdlNpsDAOImpl lObjNsdlNps =new NsdlNpsDAOImpl(null,serv.getSessionFactory());
					
					String dtoRegNo=lObjNsdlNps.getDtoRegNo(Fileno.substring(0, 2));

					FileWriter fw = new FileWriter(filePath);
					BufferedWriter br = new BufferedWriter(fw);

					br.write("1"+"^");   		 br.write("FH"+"^"); 		 br.write("P"+"^"); 		 br.write(dtoRegNo+"^");		 br.write("1"+"^");
					br.write("^"+"A");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^");		 br.write("^"); 
					br.write("\r\n");


					String bhData=lObjNsdlDAO.getBatchData(Fileno);
					br.write(bhData);
					br.write("\r\n");



					List dhData=lObjNsdlDAO.getDHData(Fileno);
					if (dhData != null && dhData.size() > 0)
					{
						Iterator IT = dhData.iterator();

						Object[] lObj = null;
						while (IT.hasNext())
						{
							lObj = (Object[]) IT.next();
							dhDtls = lObj[0].toString();
							ddoRegNo = lObj[1].toString();
							br.write(dhDtls);

							br.write("\r\n");


							List sdDtls=lObjNsdlDAO.getSDDtls(Fileno,ddoRegNo);
							for(int i=0;i<sdDtls.size();i++){
								br.write(sdDtls.get(i).toString());
								br.write("\r\n");


							}

						}

					}

					br.close();

					gLogger.info("filePath is***********"+filePath.toString());
					gLogger.info("path is  is***********"+fw.toString());
					gLogger.info("fw is***********"+br.toString());


					String fvuFilePtah=filePath.replace("txt", "pao");
					String errFilePtah=filePath.replace("txt", "err");
					DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serv
							.getSessionFactory());

					String[] args = {filePath,
							errFilePtah, 
							fvuFilePtah,"0","1.44"};
					gLogger.info("inputParametersis "+args[0]);
					gLogger.info("inputParametersis "+args[1]);
					gLogger.info("inputParametersis "+args[2]);
			

					 
					int fileStatus=11;
					PAOFvu.main(args);

					File f5=null;
					f5=new File(Fileno.concat(".txt"));
					System.out.println("File to be deleted"+f5);
					f5.deleteOnExit();

					File f = new File(new File(fvuFilePtah).getAbsolutePath());
			
					File f1 = new File(new File(errFilePtah).getAbsolutePath());
					String fileSentStatus="";
					String paoFilePath="";
					String digiSignPath="";
					if(f.exists() && !f.isDirectory()){
						BufferedReader br1 = new BufferedReader(new FileReader(new File(fvuFilePtah).getAbsolutePath()));
						//gLogger.info("path is*******BEFORE RENAIMING*****"+f.getAbsolutePath());
						//File file2 = new File(request.getSession().getServletContext().getRealPath("/") + Fileno.concat(".pao")); 
						//gLogger.info("path is******file2****"+file2.getAbsolutePath());
						//f.renameTo(file2);
						//gLogger.info("path is************"+f.getAbsolutePath());
						
						//to be revereted
						paoFilePath=	f.getAbsolutePath();
						//paoFilePath="D:/signedFiles/2007200812011.pao";
						//to be revereted
						digiSignPath=paoFilePath.replace(".pao", ".sig");
						String cerFilePath="";
						if(treasury.equalsIgnoreCase("22"))
						{
							 cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/DTO PUNE.pfx");
							 userId="1007889730";
							 entityId="10078897";
						}
						else if (treasury.equalsIgnoreCase("31"))
						{
							cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Aurangabad_pfx.pfx");
							 userId="1007867330";
							 entityId="10078673";
						}
						else if (treasury.equalsIgnoreCase("25"))
						{
							cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Solapur.pfx");
							 userId="1007880630";
							 entityId="10078806";
						}
						else if (treasury.equalsIgnoreCase("13"))
						{
							cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/Raigad_pfx.pfx");
							 userId="1007877230";
							 entityId="10078772";
						}
						else if (treasury.equalsIgnoreCase("16"))
						{
							cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/palghar_pfx.pfx");
							 userId="1007895430";
							 entityId="10078954";
						}
						
						else if (treasury.equalsIgnoreCase("36"))
						{
							cerFilePath=request.getSession().getServletContext().getRealPath("/").concat("/jalna_pfx.pfx");
							 userId="1007893930";
							 entityId="10078939";
						}
						
					
						gLogger.info("paoFilePath is************"+paoFilePath);
						gLogger.info("digiSignPath is************"+digiSignPath);
						gLogger.info("cerFilePath is************"+cerFilePath);
						
						
						String nom2_gur="";
							if(!nom2_gur.trim().equals("NA")){
					            //System.out.println( "hii ");
					        }
					        
							gLogger.info(nom2_gur.trim().toString());
					        
							PKICtrl pkiCtrl=new PKICtrl();
					        String status="";
					        if(treasury.equalsIgnoreCase("22"))
							{
					        	status= pkiCtrl.signCMSDataWithCertificate(paoFilePath, digiSignPath, cerFilePath, "ifms123", true);
							}
							else
							{
								status= pkiCtrl.signCMSDataWithCertificate(paoFilePath, digiSignPath, cerFilePath, "123", true);
							}
					         
					        
					        gLogger.info("signing file startted**************"+status);
					        
					        String veriStatus = pkiCtrl.verifyCMSSignedData(digiSignPath,paoFilePath,  true); //$NON-NLS-1$
					        
					        gLogger.info("veirification completed*******"+veriStatus);

					        fileSentStatus="file Validated and Sent to NSDL..";
					}
					else{
						fileSentStatus="file not Validated Succeesfully.";
						
					}
					
					String[] args1=null;
					//STPWebServicePOJO_STPWebServicePOJOPort_Client.main(args1);
					//Added this code for file sending to NSDL
					
					 
					
					
					
					
					
					
					
				        List<String> files = new ArrayList<String>();
				        files.add(paoFilePath);
				        files.add(digiSignPath);
				        zipFiles(files,paoFilePath.replace("pao", "zip"));
				        
				      /*System.out.println("creating ws");
						System.getProperties().put("https.proxyHost", "proxy.tcs.com");
					       System.getProperties().put("https.proxyPort", "8080");
					       */
					       
				        final QName SERVICE_NAME = new QName("http://webservice.core.stp.cra.com/", "STPWebServicePOJOService");
				        URL wsdlURL = STPWebServicePOJOService.WSDL_LOCATION;
				        STPWebServicePOJOService ss = new STPWebServicePOJOService(wsdlURL, SERVICE_NAME);
				        STPWebServicePOJO port = ss.getSTPWebServicePOJOPort();  
				        BindingProvider bindingProvider = (BindingProvider) port;
				       bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,"https://npscan-cra.com/STPWeb/STPWebServicePOJOPort");
				        //bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,"https://testnpscan.nsdl.com/STPWeb/STPWebServicePOJOPort");
				      
				       // ((BindingProvider) port).getRequestContext().
		               // put("com.sun.xml.internal.ws.request.timeout,", "300000");
				        
				     

				    //    ((BindingProvider) port).getRequestContext().
			        //        put(BindingProviderProperties.REQUEST_TIMEOUT, 300000);
				        
				        
				        
				        //Set timeout until a connection is established 
				   //     ((BindingProvider) port).getRequestContext().
				              //  put("javax.xml.ws.client.connectionTimeout", "300000");
				        //Set timeout until the response is received       
				    //   ((BindingProvider) port).getRequestContext().
				              //  put("javax.xml.ws.client.receiveTimeout", "300000"); 
				      
						
						//String frnNumber="1118880";
				        
				     /*   
				        Client client = ClientProxy.getClient(port);
			        	HTTPConduit http = (HTTPConduit) client.getConduit();
			        	HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
			        	httpClientPolicy.setConnectionTimeout(300000);
			        	httpClientPolicy.setReceiveTimeout(300000);
			        	
			        	httpClientPolicy.setAllowChunking(false);
			        	http.setClient(httpClientPolicy);
			        	*/
				        
				        
			        	
			        	
				        String frnNumber="";
						frnNumber=lObjNsdlNps.getFrnNo(Fileno);
						gLogger.info("frnNumber"+frnNumber);
						
						
						gLogger.info("Invoking perform status enquiry...");
			            java.util.List<byte[]> _performStatusInquiry_arg0 = new java.util.ArrayList<byte[]>();
			            
			            String zipFileName = digiSignPath;
			            PerformFileUpload fileUpload = new PerformFileUpload();
			            byte[] fileByte;
			            File zipFile = new File(zipFileName);

			            FileInputStream fis = new FileInputStream(zipFileName);

			            fileByte = new byte[(int) zipFile.length()];
			            fis.read(fileByte);
			            
			            gLogger.info(fileByte.toString());

			        	byte[][] _performStatusInquiry_arg0Val1 = new byte[5][0];
			             _performStatusInquiry_arg0Val1[0] = userId.getBytes();
			             _performStatusInquiry_arg0Val1[1] = frnNumber.toString().getBytes();
			             _performStatusInquiry_arg0Val1[2] = entityId.getBytes(); // zip file byte
			             _performStatusInquiry_arg0Val1[3] = fileByte; // zip file byte
			             _performStatusInquiry_arg0Val1[4] = "File Status-SubscriberContribution".getBytes();; // zip file byte
			             
			             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[0]);
			             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[1]);
			             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[2]);
			             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[3]);
			             _performStatusInquiry_arg0.add(_performStatusInquiry_arg0Val1[4]);
			             java.lang.String _performStatusInquiry__return = port.performStatusInquiry(_performStatusInquiry_arg0);
			             gLogger.info("performStatusInquiry.result=***************************************" + _performStatusInquiry__return);
			             
			             
			             DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			 			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			 			ByteArrayInputStream bis = new ByteArrayInputStream(_performStatusInquiry__return.getBytes());
			 			Document doc = dBuilder.parse(bis);

			 			//optional, but recommended
			 			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			 			doc.getDocumentElement().normalize();
			 			String response="";
			 			String contribForm="";

			 			NodeList nList = doc.getElementsByTagName("response");

			 			if(nList!=null)
			 			{
			 				Node nNode = nList.item(0);
			 				
			 				if (nNode.getNodeType() == Node.ELEMENT_NODE) 
			 				
			 				{
			 					Element eElement = (Element) nNode;
			 					if(eElement.getElementsByTagName("file_status")!=null && eElement.getElementsByTagName("file_status").item(0)!=null)
								{
			 						String fStatus=eElement.getElementsByTagName("file_status").item(0).getTextContent();
			 						gLogger.info("fStatus : "+fStatus);
			 						if(fStatus!=null && !"".equalsIgnoreCase(fStatus))
									{
			 							if(eElement.getElementsByTagName("transaction_id")!=null && eElement.getElementsByTagName("transaction_id").item(0)!=null)
					 					{
					 						String tranId=eElement.getElementsByTagName("transaction_id").item(0).getTextContent();
					 						gLogger.info("tranId : "+tranId);
					 						if(!tranId.equalsIgnoreCase("") && !tranId.equalsIgnoreCase("0") && tranId.length() >1)
					 						{
					 							lObjNsdlDAO.updateFileStatusandTranNo(fileStatus,Fileno,errorData,tranId);
						 						 flag=true;
					 						}
					 						else
					 						{
					 							fileStatus=1;
					 						}
					 						
					 						
					 					}
			 							else
			 							{
			 								fileStatus=1;
			 							}
			 							if(eElement.getElementsByTagName("response_html")!=null && eElement.getElementsByTagName("response_html").item(0)!=null)
					 					{
			 								response=eElement.getElementsByTagName("response_html").item(0).getTextContent();
			 								gLogger.info("response : "+response);
					 					}
			 							if(eElement.getElementsByTagName("contr_submission_form_html")!=null && eElement.getElementsByTagName("contr_submission_form_html").item(0)!=null)
			 							{
			 								contribForm=eElement.getElementsByTagName("contr_submission_form_html").item(0).getTextContent();
			 							}
			 							if(response.equalsIgnoreCase("null"))
		 								{
			 								response="";
			 								gLogger.info("response null : "+response);
		 								}
			 							if(contribForm.equalsIgnoreCase("null"))
			 							{
			 								contribForm="";
			 							}
			 							lObjNsdlDAO.updateNsdlStatus(fileStatus, Fileno, response, contribForm);
									}
								}
			 					}

			 			}
				
			}
			
			
			else
			{
				lObjNsdlDAO.updateFileStatusandTranNo(11,Fileno,errorData,null);
				 flag=true;
			}
				
				
			
		
				
				
	 			String lSBStatus = getResponseXMLDoc(flag,monthId,yearId,ifDeputation,trCode,digiActivate).toString();
				String lStrResult = new AjaxXmlBuilder().addItem("ajax_key",
						lSBStatus).toString();	
				inputMap.put("ajaxKey", lStrResult);
				resObj.setViewName("ajaxData");

				resObj.setResultValue(inputMap);
				
			gLogger.info("All fine 8 is *********");
		
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
	
	
	
	 public void zipFiles(List<String> files, String zipFilePath){
         
	        FileOutputStream fos = null;
	        ZipOutputStream zipOut = null;
	        FileInputStream fis = null;
	        try {
	            fos = new FileOutputStream(zipFilePath);
	            zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
	            for(String filePath:files){
	                File input = new File(filePath);
	                fis = new FileInputStream(input);
	                ZipEntry ze = new ZipEntry(input.getName());
	                System.out.println("Zipping the file: "+input.getName());
	                zipOut.putNextEntry(ze);
	                byte[] tmp = new byte[4*1024];
	                int size = 0;
	                while((size = fis.read(tmp)) != -1){
	                    zipOut.write(tmp, 0, size);
	                }
	                zipOut.flush();
	                fis.close();
	            }
	            zipOut.close();
	            System.out.println("Done... Zipped the files...");
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally{
	            try{
	                if(fos != null) fos.close();
	            } catch(Exception ex){
	                 
	            }
	        }
	    }
	 //added by sunitha for NPS 
	 public ResultObject forwardBillDataToBEAMSForNPS(Map objectArgs) {
		 gLogger.info("inside forwardBillDataToBEAMSForNPS");
		 HttpServletRequest request = (HttpServletRequest) objectArgs
			.get("requestObj");
			ServiceLocator serv = (ServiceLocator) objectArgs
			.get("serviceLocator");
			ResultObject resultObject = new ResultObject(0);
			long paybillId=0l;
			Date billCreationDate = null;
			List paybillDetailsList=null;
			Object obj[] = null;
			long lLongFinYearIdPK=0l;
			long lLongCurrFinYear=0l;
			long lLongNextFinyear=0l;
			gDtCurDate = SessionHelper.getCurDate();
			String SFTPHOST = "115.112.249.52";//ip for local/staging testing http://115.112.239.72:8080/BeamsWS1/services/AuthorizationService?wsdl
			//String SFTPHOST= "10.34.82.225";
			int SFTPPORT = 8888;
			String SFTPUSER = "tcsadmin";
			String SFTPPASS = "Tcsadmin@123";
			String SFTPWORKINGDIR = "/home/sevarth";
			
			/*//$t vm sftp
			String SFTPHOST = "100.70.201.169";
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
			//String lLongGrossAmt = "";
			NsdlNpsDAOImpl lObjNsdlDAO = new NsdlNpsDAOImpl(null, serv.getSessionFactory());
			FinancialYearDAOImpl financialYearDAOImpl = new FinancialYearDAOImpl(SgvcFinYearMst.class, serv.getSessionFactory());
			SgvcFinYearMst sgvcFinYearMst = new SgvcFinYearMst();
			HashMap lMapBillDetailsMap = new HashMap();
			String fileNumber = (!("".equals(StringUtility.getParameter("fileNumber", request)))) ? StringUtility.getParameter("fileNumber", request) : "";
			long lIntMonth = (!("".equals(StringUtility.getParameter("Month",request)))) ? Integer.parseInt(StringUtility.getParameter("Month", request)) : 0;
			long lIntYear = (!("".equals(StringUtility.getParameter("Year",request)))) ? Integer.parseInt(StringUtility.getParameter("Year", request)) : 0;
			paybillDetailsList=lObjNsdlDAO.getBillId(fileNumber,lIntMonth,lIntYear);
			String strMonth = null;
			if(lIntMonth < 10)
				strMonth = "0"+lIntMonth;
			else strMonth = lIntMonth+"";
			if(paybillDetailsList!=null && !paybillDetailsList.isEmpty()){
				Iterator it =  paybillDetailsList.iterator();
				while (it.hasNext()) {
					obj = (Object[]) it.next();
					paybillId=Long.parseLong(obj[0].toString());
					billCreationDate=(Date) obj[1];
				}
			}
			int benCount = lObjNsdlDAO.getCount(lIntMonth, lIntYear,paybillId);
			lLongGrossAmt = lObjNsdlDAO.getGrossAmount(lIntMonth, lIntYear,paybillId);
			lLongFinYearIdPK = financialYearDAOImpl.getFinYearIdByCurDate();

			sgvcFinYearMst = (SgvcFinYearMst) financialYearDAOImpl.read(Long
					.valueOf(lLongFinYearIdPK));
			if (sgvcFinYearMst != null) {
				lLongCurrFinYear = Long
				.valueOf(sgvcFinYearMst.getFinYearCode()).longValue();
				lLongNextFinyear = lLongCurrFinYear + -2351041733208309759L;
			}
			BigDecimal grossAmt = new BigDecimal(lLongGrossAmt);
			Calendar cal = Calendar.getInstance();
			//cal.setTime(lDateBillCreation);changed for finanical year issue
			gLogger.info("lLongLoggedInLocation:"+ lLongLoggedInLocation);
			gLogger.info("lLongLoggedInLocation:"+ userId+"postid:"+postId); 

			String DDOCode="";
			String paymentMode="";
			List DDOList = lObjNsdlDAO.getTreasuryDdoCode(lLongLoggedInLocation);
			if(DDOList!=null && DDOList.size()>0 && DDOList.get(0)!=null)
			{
				Object[] obj1=(Object[]) DDOList.get(0);
				if(obj1[0]!=null && obj1[1]!=null)
				{
					DDOCode=obj1[0].toString();
					paymentMode=obj1[1].toString();
				}
			}
			if(DDOCode.equals("1111222222")){
				DDOCode="9101005555";
			}
			/*else if(lLongLoggedInLocation==2201l || lLongLoggedInLocation==3101l || lLongLoggedInLocation==4601l){
				DDOCode = lLongLoggedInLocation+"003268";
			}
			else
				DDOCode = lLongLoggedInLocation+"003270";*/
			
			gLogger.info("DDOCode:"+ DDOCode); 
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
	/*	BifurcatedPartyNameMap = new HashMap();
		BifurcatedPartyNameMap.put("NAGESH__TALEKAR", "7150");*/
			lMapBillDetailsMap.put("PaybillId", String.valueOf(paybillId));
			gLogger.info("paybillId:"+ String.valueOf(paybillId));
		//lMapBillDetailsMap.put("PaybillId", "991000099137137");
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
			lMapBillDetailsMap.put("PaymentMode", paymentMode);
			gLogger.info("paymentMode:"+ paymentMode);
			lMapBillDetailsMap.put("TotalDeduction", "0");
			lMapBillDetailsMap.put("SchemeCode", "83420132");
			lMapBillDetailsMap.put("DetailHead", "50");
			lMapBillDetailsMap.put("DDOCode", DDOCode);
			gLogger.info("DDOCode:"+ DDOCode);
			lMapBillDetailsMap.put("BulkFlag", "N");
			lMapBillDetailsMap.put("PayeeType", "D");
			lMapBillDetailsMap.put("BillCreationDate", billCreationDate);
			gLogger.info("billCreationDate:"+ billCreationDate);
			lMapBillDetailsMap.put("PayeeCount", "1");
			lMapBillDetailsMap.put("BillType", 29);
			lMapBillDetailsMap.put("BillPortalName", "MAHAVETAN");
			/*////$t 29-7-2021
			if(DDOCode.equals("2201003268")){
			lMapBillDetailsMap.put("NPSPayment", "Y");
			lMapBillDetailsMap.put("NPSAmt", String.valueOf(lLongGrossAmt));
			}*/
			
			//lMapBillDetailsMap.put("BifurcatedPartyNameMap", BifurcatedPartyNameMap);
			//lMapBillDetailsMap.putAll(BifurcatedPartyNameMap);
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
				lObjTrnNPSBeamsIntegration.setBillType("29");
				if (pdfData != null) {
					//lObjTrnNPSBeamsIntegration.setAuthSlip(new SerialBlob(pdfData));
					

					

						

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
						//for (Byte b: lBytes) {  
							// out.write(lBytes);
					      // }
						
						for(int i=0;i<2;i++){
							out.write(lBytes);
						}
						
						out.close();
					/*	HttpServletResponse response = (HttpServletResponse) objectArgs
						.get("responseObj");
						response.setContentLength(lBytes.length);
						response.setContentType("application/pdf");
						response.addHeader("Content-Disposition",
						"inline;filename=authSlip.pdf");
						response.getOutputStream().write(lBytes);
						response.getOutputStream().flush();
						response.getOutputStream().close();
						
					response.getContentType();*/
						// BufferedInputStream bis = new

						// BufferedInputStream(channelSftp.get(fileName));

						//System.out.println("FILE NAME11111111111 ::::::::::::" + authSlip.pdf);

						File f = new File(fileName.toString());

						this.gLogger.error("FILE NAME222222222 ::::::::::::" + fileName);
						this.gLogger.error("FILE NAME ::::::::::::" + f.getName());

						channelSftp.put(new FileInputStream(f), f.getName());

						this.gLogger.error("FILE NAME333333333 ::::::::::::" + fileName);
						
				}
				
				lObjTrnNPSBeamsIntegration.setBillNetAmt(grossAmt);
				lObjTrnNPSBeamsIntegration.setTotalRecoveryAmt(grossAmt);
				lObjTrnNPSBeamsIntegration.setSchemeCode("83420132");
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
					lObjNsdlDAO.updateNsdlBillDetails(authNo,paybillId,flag);
					
				} else if ((!"00".equals(statusCode))  &&resultMap != null && !resultMap.isEmpty()) {
					flag = "N";
					this.gLogger.error("Bll rejection in db"+paybillId+flag);
					if(!"31".equals(statusCode))
					{
						lObjNsdlDAO.updateNsdlBillDetails(authNo,paybillId,flag);
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
				resultObject.setViewName("NPSVALIDATE");
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
	 
	 public ResultObject getAuthSlipForNPS(Map objectArgs) {
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
									////String SFTPHOST = "115.112.249.52"; //ip for local testing 115.112.249.52
									String SFTPHOST= "10.34.82.225";
									int SFTPPORT = 8888;
									String SFTPUSER = "tcsadmin";
									String SFTPPASS = "Tcsadmin@123";
									String SFTPWORKINGDIR = "home/sevarth";
									
									/*//$t vm sftp
									String SFTPHOST = "100.70.201.169";
									int SFTPPORT = 22;
									String SFTPUSER = "mahait";
									String SFTPPASS = "Mahait@99";
									String SFTPWORKINGDIR = "/upload/DCPS";*/
									
									 if(SFTPUSER.equals("tcsadmin")){
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
								    }else{
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
										String filePath=request.getSession().getServletContext().getRealPath("/") + "tmp/";//+ "/" + directoryFolder;//$there
										String home = System.getProperty("user.home");
										objectArgs.put("SFTPmsg","YES");
										channelSftp.get("/upload/DCPS/"+fileName, filePath);//$tVM
									    //added one more solution on 9th feb
										FileInputStream fis = new FileInputStream(new File(filePath+fileName));

											 // Fast way to copy a bytearray from InputStream to OutputStream
											 org.apache.commons.io.IOUtils.copy(fis, response.getOutputStream());
											 response.setContentType("application/pdf");
											 response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
											 response.flushBuffer();
											//ended solution
											/*File pdfFile = new File(filePath);
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

}
