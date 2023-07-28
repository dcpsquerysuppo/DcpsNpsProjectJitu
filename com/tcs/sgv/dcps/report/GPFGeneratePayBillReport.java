package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.report.SixPayArrearAmountDepositionReportSchedule;
import com.tcs.sgv.ess.valueobject.OrgPostMst;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;
import com.tcs.sgv.lna.dao.LNAHealthInsuranceDAO;
import com.tcs.sgv.lna.dao.LNAHealthInsuranceDAOImpl;

public class GPFGeneratePayBillReport extends DefaultReportDataFinder implements ReportDataFinder 
{
    private static final Logger gLogger = Logger.getLogger(GPFGeneratePayBillReport.class);
    public static String newline = System.getProperty("line.separator");
    String Lang_Id = "en_US";
    String Loc_Id = "LC1";
    TabularData rptTd = null;
    ReportVO RptVo = null;
    ReportsDAO reportsDao = new ReportsDAOImpl();
    String gStrLocCode = null;
    Map requestAttributes = null;
    ServiceLocator serviceLocator = null;
    SessionFactory lObjSessionFactory = null;
    Session ghibSession = null;
    private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/gpf/GeneratePayBill");

    public Collection findReportData(ReportVO report, Object criteria) throws ReportException 
    {
        requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
        serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
        lObjSessionFactory = serviceLocator.getSessionFactorySlave();		

        Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
        Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

        ArrayList twoRows = null;
        ArrayList td = null;

       
        Map lMapRequestAttributes = null;
        Map lMapSessionAttributes = null;
        LoginDetails lObjLoginVO = null;
        lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
        lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
        lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
        gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
        gLogger.info("hiii the location Code is ******&&&&&&&#########******"+gStrLocCode);
      



        List<OrgPostMst> lLngPostIdList = (List) loginDetail.get("postIdList");
        OrgPostMst lObjPostMst = lLngPostIdList.get(0);
        Long lLngPostId = lObjPostMst.getPostId();

        StyleVO[] rowsFontsVO = new StyleVO[5];		
        rowsFontsVO[0] = new StyleVO();
        rowsFontsVO[0].setStyleId(IReportConstants.BACKGROUNDCOLOR);
        rowsFontsVO[0].setStyleValue("white");
        rowsFontsVO[1] = new StyleVO();
        rowsFontsVO[1].setStyleId(IReportConstants.BORDER);
        rowsFontsVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
        rowsFontsVO[2] = new StyleVO();
        rowsFontsVO[2].setStyleId(26);
        rowsFontsVO[2].setStyleValue("JavaScript:self.close()");
        rowsFontsVO[3] = new StyleVO();
        rowsFontsVO[3].setStyleId(IReportConstants.REPORT_PAGINATION);
        rowsFontsVO[3].setStyleValue("NO");
        rowsFontsVO[4] = new StyleVO();
        rowsFontsVO[4].setStyleId(IReportConstants.STYLE_FONT_SIZE);
        rowsFontsVO[4].setStyleValue(IReportConstants.VALUE_FONT_SIZE_MEDIUM);

        StyleVO[] rightAlign = new StyleVO[2];
        rightAlign[0] = new StyleVO();
        rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
        rightAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
        rightAlign[1] = new StyleVO();
        rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
        rightAlign[1].setStyleValue(IReportConstants.VALUE_FONT_SIZE_MEDIUM);

        StyleVO[] leftAlign = new StyleVO[2];
		leftAlign[0] = new StyleVO();
		leftAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		leftAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		leftAlign[1] = new StyleVO();
		leftAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		leftAlign[1].setStyleValue("12");
        
        
        
        StyleVO[] centerAlign = new StyleVO[2];
        centerAlign[0] = new StyleVO();
        centerAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
        centerAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
        centerAlign[1] = new StyleVO();
        centerAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
        centerAlign[1].setStyleValue(IReportConstants.VALUE_FONT_SIZE_LARGE);
        
        
        StyleVO[] centerAlign1 = new StyleVO[2];
        centerAlign1[0] = new StyleVO();
        centerAlign1[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
        centerAlign1[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
        centerAlign1[1] = new StyleVO();
        centerAlign1[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
        centerAlign1[1].setStyleValue("12");

        ArrayList<Object> dataList = new ArrayList<Object>();
        ArrayList<Object> dataList1 = new ArrayList<Object>();

        String lStrNameOfOffice = "";
        List lLstEmpDetails = null;		
        String lStrEmpDtls = "";
        Object []lObj = null;
        String lStrPANNumber = "";
        String lStrBankName = "";
        String lStrBranchName = "";
        String lStrName ="";
        String lStrDesignation ="";
        Double lStrSanctionedAmount =0.0;
        Double lStrSanctionedAmountCount =0.0;
       

        Date today = new Date();

        Integer currDate = today.getDate();
        Integer currMonth = today.getMonth() + 1;
        Integer currYear = today.getYear() + 1900;
        String lStrDDOLocName="";
        try{
            ghibSession = lObjSessionFactory.getCurrentSession();

            if(report.getReportCode().equals("800010"))
            {
                report.setStyleList(rowsFontsVO);
                String lStrGpfAccNo = (String) report.getParameterValue("gpfAccNo");
                String lStrMonth = (String) report.getParameterValue("month");
                String lStrYear = (String) report.getParameterValue("year");
                String lStrTransactionId = (String) report.getParameterValue("transactionId");
                String lStrreqType = (String) report.getParameterValue("reqType");

                gLogger.error("lStrreqType"+lStrreqType);



                SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");

                LNAHealthInsuranceDAO lObjGPFHealthIns = new LNAHealthInsuranceDAOImpl(null, serviceLocator.getSessionFactory());
                String lStrSevaarthId = lObjGPFHealthIns.getEmpsevaarthId(lStrGpfAccNo);


                GPFRequestProcessDAO lObjGPFRequestProcess = new GPFRequestProcessDAOImpl(null, serviceLocator.getSessionFactory());
                
             

				
                List lLstDsgnAndOfficeName = lObjGPFRequestProcess.getDsgnAndOfficeName(lStrSevaarthId);
                Object[] lObjDsgnAndOfficeName = null;

                if (!lLstDsgnAndOfficeName.isEmpty()) {
                    lObjDsgnAndOfficeName = (Object[]) lLstDsgnAndOfficeName.get(0);

                    lStrNameOfOffice = (String) lObjDsgnAndOfficeName[1];


                }
                
                
               lStrSanctionedAmount = lObjGPFRequestProcess.getSanctionedAmount(lStrTransactionId,lStrreqType);
                
               String lStrOrderNumber = lObjGPFRequestProcess.getOrderNumber(lStrTransactionId);
               gLogger.error("*****************"+lStrOrderNumber);
               lStrDDOLocName= lObjGPFRequestProcess.getDDOLocName(lStrTransactionId);
               gLogger.error("********lStrDDOLocName*********"+lStrDDOLocName);
                
                
                List lStrEmpDetails = lObjGPFRequestProcess.getNameAndDesignation(lStrSevaarthId);
                Object[] lObjEmpDetails = null;
                if(!lStrEmpDetails.isEmpty() && lStrEmpDetails!= null) {
                	lObjEmpDetails = (Object[]) lStrEmpDetails.get(0);
                	
                	
                	if(lObjEmpDetails[0] != null){
                		lStrName = lObjEmpDetails[0].toString();
					}
                	
                	if(lObjEmpDetails[1] != null){
                		lStrDesignation = lObjEmpDetails[1].toString();
                	}	
                }

                gLogger.info("lStrNameOfOffice****"+lStrNameOfOffice);





                ArrayList<Object> rowList = new ArrayList<Object>();

                lLstEmpDetails = getEmployeeDetails(lStrGpfAccNo, lStrTransactionId,lStrreqType);
                List lstOrderNo = getOrderNoFromGPFActNoAndTrsnID(lStrTransactionId);
                String loggedInDdoDtls=getDsgnAndFieldDept(gStrLocCode);
                String arrloggedInDdoDtls[]= loggedInDdoDtls.split("##");
               
                String dsgnLoggedDDO=arrloggedInDdoDtls[0];
                String fieldDept=arrloggedInDdoDtls[1];
                
               
                String lStrOrderNo = "";
                String ddoDesign = "";
                String tresName = "";
                String bilNo = "";
                Object lObj1[] = null;
                if(lLstEmpDetails != null && lLstEmpDetails.size() > 0){

                    if(lstOrderNo != null && lstOrderNo.size() > 0){

                        if(lstOrderNo.get(0) != null ){
                            Object obj2[] = (Object[])lstOrderNo.get(0); 
                            lStrOrderNo = obj2[0].toString();
                            if(obj2[1] != null)
                                bilNo = obj2[1].toString();
                        }
                    }

                    lObj = (Object[]) lLstEmpDetails.get(0);
                    lStrEmpDtls = lObj[0].toString() + ", " + lObj[1].toString() + ", "+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE29A") + lObj[2].toString()+", "+lStrOrderNo;

                    Date lDtSancDate = (Date) lObj[3];
                    lStrEmpDtls += ", " + lObjSimpleDate.format(lDtSancDate);
                    ddoDesign = lObj[5].toString();
                    tresName = lObj[6].toString();

                }

                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE1A"),centerAlign));
                dataList.add(rowList);

                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE2A"),centerAlign));
                dataList.add(rowList);	
                
              /*  rowList = new ArrayList<Object>();
                rowList.add(new StyledData("(Loans & Advances Amount Other than Renewed Advance Bill)",leftAlign));
                dataList.add(rowList);*/

                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE3A"),centerAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE4A"),centerAlign));
                dataList.add(rowList);
                
               
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(".........................................................................................................................................................................................................................................",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE5A")+space(30)+"]","[Treasury Code]"+space(20)+"[Treasury Name]"+space(25)+""),
						leftAlign));
				dataList.add(rowList);
                
				rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE6A")+space(28)+"]"+space(18)+""+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE6Aa")+space(5)+"["+space(30)+"]",leftAlign));
                dataList.add(rowList);
				
				 /*rowList.add(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE1") + space(200) + gObjRsrcBndle.getString("GPF.PAYBILLGENLINE2"));
	                dataList.add(rowList);*/
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE7A")+space(18)+"["+space(30)+"]"+space(18)+""+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE7Aa")+space(11)+"["+space(30)+"]",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE8A")+space(17)+"["+space(30)+"]"+space(18)+""+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE8Aa")+space(25)+"["+space(30)+"]",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE9A")+space(10)+"["+space(30)+"]"+space(18)+""+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE9Aa")+space(4)+"["+space(30)+"]",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE10A")+space(12)+"["+space(30)+"]",leftAlign));
                dataList.add(rowList);
                

                rowList = new ArrayList<Object>();
                rowList.add(new StyledData("_____________________________________________________________________________________________________________",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE11A"),leftAlign));
                dataList.add(rowList);
                

                rowList = new ArrayList<Object>();
                rowList.add(new StyledData("______________________________________________________________________________________________________________",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE12A"),centerAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE13A"),centerAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE14A"),centerAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE15A"),""+space(40)+"[Code]"+space(40)+"[description]"),
						leftAlign));
				dataList.add(rowList);
                
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE16A"),""+space(49)+"[Code]"+space(40)+"[description]"),
						leftAlign));
				dataList.add(rowList);
                
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE17A"),""+space(51)+"[Code]"+space(40)+"[description]"),
						leftAlign));
				dataList.add(rowList);
                
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE18A"),""+space(52)+"[Code]"+space(40)+"[description]"),
						leftAlign));
				dataList.add(rowList);
                
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE19A"),""+space(56)+"[Code]"+space(40)+"[description]"),
						leftAlign));
				dataList.add(rowList);
                
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE20A"),""+space(43)+"[Code]"+space(40)+"[description]"),
						leftAlign));
				dataList.add(rowList);
                
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE21A"),""+space(31)+"[Code]"+space(40)+"[description]"),
						leftAlign));
				dataList.add(rowList);
                
				rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE22A"),""+space(39)+"[Code]"+space(40)+"[description]"),
						leftAlign));
				dataList.add(rowList);
                
				rowList = new ArrayList<Object>();
                rowList.add(new StyledData("......................................................................................................................................................................................................................................",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE23A")+space(30)+"]"+space(25)+""+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE23Aa")+space(15)+"["+space(30)+"]",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE24A")+space(7)+"["+space(31)+"]"+space(25)+""+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE24Aa")+space(30)+"]",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE25A")+space(9)+"["+space(31)+"]"+space(25)+""+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE25Aa")+space(21)+"["+space(30)+"]",leftAlign));
                dataList.add(rowList);
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(".........................................................................................................................................................................................................................................",leftAlign));
                dataList.add(rowList);
                
                
                rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE26A"),lStrNameOfOffice),
						leftAlign));
				dataList.add(rowList);
				
				
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE38A"),leftAlign));
                dataList.add(rowList);
                
                
                
            	ArrayList styleList = new ArrayList();
				TabularData tData  = new TabularData(dataList);				
				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				tData.addStyle(IReportConstants.BORDER, "No");
				tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
				report.setAdditionalHeader(tData);
				
				ArrayList td1 = new ArrayList();
								
	                td1.add(new StyledData("1",centerAlign1));
	                td1.add(lStrName+" "+lStrDesignation);
	                td1.add(lStrSevaarthId);
	                td1.add(lStrGpfAccNo);
	                td1.add(lStrOrderNumber);	
	                td1.add(new StyledData(lStrSanctionedAmount,centerAlign1));
	                td1.add("");	
	            	dataList1.add(td1);    
	            	
	            	ArrayList td2 = new ArrayList();
					
	                td2.add("");
	                td2.add("");
	                td2.add("");
	                td2.add("");
	                td2.add("");	
	                td2.add("");
	                td2.add("");	
	            	dataList1.add(td2);
	            	
	            	
	            	
                    ArrayList td3 = new ArrayList();
					
	                td3.add("");
	                td3.add("");
	                td3.add("");
	                td3.add("");
	                td3.add("");	
	                td3.add("");
	                td3.add("");	
	            	dataList1.add(td3);  
	            	
	            	ArrayList td4 = new ArrayList();
					
	                td4.add("");
	                td4.add("");
	                td4.add("");
	                td4.add("");
	                td4.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE37A"),centerAlign1));	
	                td4.add(new StyledData(lStrSanctionedAmount,centerAlign1));
	                td4.add("");	
	            	dataList1.add(td4); 
                
            }
            
            if(report.getReportCode().equals("80000691"))
            {
                report.setStyleList(rowsFontsVO);
                String lStrGpfAccNo = (String) report.getParameterValue("gpfAccNo");
                String lStrMonth = (String) report.getParameterValue("month");
                String lStrYear = (String) report.getParameterValue("year");
                String lStrTransactionId = (String) report.getParameterValue("transactionId");
                String lStrreqType = (String) report.getParameterValue("reqType");

                gLogger.error("lStrreqType"+lStrreqType);



                SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");

                LNAHealthInsuranceDAO lObjGPFHealthIns = new LNAHealthInsuranceDAOImpl(null, serviceLocator.getSessionFactory());
                String lStrSevaarthId = lObjGPFHealthIns.getEmpsevaarthId(lStrGpfAccNo);


                GPFRequestProcessDAO lObjGPFRequestProcess = new GPFRequestProcessDAOImpl(null, serviceLocator.getSessionFactory());
           
                List lLstDsgnAndOfficeName = lObjGPFRequestProcess.getDsgnAndOfficeName(lStrSevaarthId);
                Object[] lObjDsgnAndOfficeName = null;
                if (!lLstDsgnAndOfficeName.isEmpty()) {
                    lObjDsgnAndOfficeName = (Object[]) lLstDsgnAndOfficeName.get(0);

                    lStrNameOfOffice = (String) lObjDsgnAndOfficeName[1];


                }
                List BankDetails = lObjGPFRequestProcess.getPanNumber(lStrSevaarthId);
                Object[] lObjBankDetails = null;
                if(!BankDetails.isEmpty() && BankDetails!= null) {
                	lObjBankDetails = (Object[]) BankDetails.get(0);
                	
                	
                	if(lObjBankDetails[0] != null){
                		lStrPANNumber = lObjBankDetails[0].toString();
					}
                	
                	if(lObjBankDetails[1] != null){
                		lStrBankName = lObjBankDetails[1].toString();
					}
                	if(lObjBankDetails[2] != null){
                		lStrBranchName = lObjBankDetails[2].toString();
					}
                	
                	
                }
                Long amount_sanctoned=0l;
                lStrSanctionedAmount = lObjGPFRequestProcess.getSanctionedAmount(lStrTransactionId,lStrreqType);
                
                gLogger.info("lStrSanctionedAmount****"+lStrSanctionedAmount);
               
                lStrSanctionedAmountCount=lStrSanctionedAmount +1;
               
                
                List lStrEmpDetails = lObjGPFRequestProcess.getNameAndDesignation(lStrSevaarthId);
                Object[] lObjEmpDetails = null;
                if(!lStrEmpDetails.isEmpty() && lStrEmpDetails!= null) {
                	lObjEmpDetails = (Object[]) lStrEmpDetails.get(0);
                	
                	
                	if(lObjEmpDetails[0] != null){
                		lStrName = lObjEmpDetails[0].toString();
					}
                	
                	if(lObjEmpDetails[1] != null){
                		lStrDesignation = lObjEmpDetails[1].toString();
                	}	
                }



                gLogger.info("lStrNameOfOffice****"+lStrNameOfOffice);





                ArrayList<Object> rowList = new ArrayList<Object>();

                lLstEmpDetails = getEmployeeDetails(lStrGpfAccNo, lStrTransactionId,lStrreqType);
                List lstOrderNo = getOrderNoFromGPFActNoAndTrsnID(lStrTransactionId);
                String loggedInDdoDtls=getDsgnAndFieldDept(gStrLocCode);
                String arrloggedInDdoDtls[]= loggedInDdoDtls.split("##");
               
                String dsgnLoggedDDO=arrloggedInDdoDtls[0];
                String fieldDept=arrloggedInDdoDtls[1];
                
               
                String lStrOrderNo = "";
                String ddoDesign = "";
                String tresName = "";
                String bilNo = "";
                Object lObj1[] = null;
                if(lLstEmpDetails != null && lLstEmpDetails.size() > 0){

                    if(lstOrderNo != null && lstOrderNo.size() > 0){

                        if(lstOrderNo.get(0) != null ){
                            Object obj2[] = (Object[])lstOrderNo.get(0); 
                            lStrOrderNo = obj2[0].toString();
                            if(obj2[1] != null)
                                bilNo = obj2[1].toString();
                        }
                    }

                    lObj = (Object[]) lLstEmpDetails.get(0);
                    lStrEmpDtls = lObj[0].toString() + ", " + lObj[1].toString() + ", "+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE29A") + lObj[2].toString()+", "+lStrOrderNo;

                    Date lDtSancDate = (Date) lObj[3];
                    lStrEmpDtls += ", " + lObjSimpleDate.format(lDtSancDate);
                    ddoDesign = lObj[5].toString();
                    tresName = lObj[6].toString();

                }
                
                /*rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE27"),leftAlign));
                dataList.add(rowList);*/
                
                rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE28A"),lStrSanctionedAmountCount),
						leftAlign));
				dataList.add(rowList);
                
				 rowList = new ArrayList<Object>();
					rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE29A"),lStrSanctionedAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(lStrSanctionedAmount))),
							leftAlign));
					dataList.add(rowList);
                rowList = new ArrayList<Object>();
                
                
                
                rowList = new ArrayList<Object>();
				rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE30A"),lStrSanctionedAmount,EnglishDecimalFormat.convertWithSpace(new BigDecimal(lStrSanctionedAmount))),
						leftAlign));
				dataList.add(rowList);
            rowList = new ArrayList<Object>();
                
                rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE31A"),leftAlign));
                dataList.add(rowList);
                
              /*  rowList = new ArrayList<Object>();
                rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE32"),leftAlign));
                dataList.add(rowList);*/
                
                
                ArrayList styleList = new ArrayList();
				TabularData tData  = new TabularData(dataList);				
				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				tData.addStyle(IReportConstants.BORDER, "No");
				tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
				report.setAdditionalHeader(tData);
				
				ArrayList td1 = new ArrayList();
				
							
	                td1.add(new StyledData("1",centerAlign1));
	                td1.add(lStrName+" "+lStrDesignation);
	                td1.add(new StyledData(lStrPANNumber,centerAlign1));
	                td1.add(new StyledData(lStrBankName,centerAlign1));
	                td1.add(new StyledData(lStrBranchName,centerAlign1));	
	                td1.add(new StyledData(lStrGpfAccNo,centerAlign1));
	                td1.add(new StyledData(lStrSanctionedAmount,centerAlign1));	
	            	dataList1.add(td1); 
        }
        if(report.getReportCode().equals("80000692"))
        {

            report.setStyleList(rowsFontsVO);
            String lStrGpfAccNo = (String) report.getParameterValue("gpfAccNo");
            String lStrMonth = (String) report.getParameterValue("month");
            String lStrYear = (String) report.getParameterValue("year");
            String lStrTransactionId = (String) report.getParameterValue("transactionId");
            String lStrreqType = (String) report.getParameterValue("reqType");

            gLogger.error("lStrreqType"+lStrreqType);



            SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");

            LNAHealthInsuranceDAO lObjGPFHealthIns = new LNAHealthInsuranceDAOImpl(null, serviceLocator.getSessionFactory());
            String lStrSevaarthId = lObjGPFHealthIns.getEmpsevaarthId(lStrGpfAccNo);


            GPFRequestProcessDAO lObjGPFRequestProcess = new GPFRequestProcessDAOImpl(null, serviceLocator.getSessionFactory());

            List lLstDsgnAndOfficeName = lObjGPFRequestProcess.getDsgnAndOfficeName(lStrSevaarthId);
            Object[] lObjDsgnAndOfficeName = null;

            if (!lLstDsgnAndOfficeName.isEmpty()) {
                lObjDsgnAndOfficeName = (Object[]) lLstDsgnAndOfficeName.get(0);

                lStrNameOfOffice = (String) lObjDsgnAndOfficeName[1];


            }
            
            String lStrBillDate = lObjGPFRequestProcess.getBillGenerationDate(lStrTransactionId);

            gLogger.info("lStrNameOfOffice****"+lStrNameOfOffice);





            ArrayList<Object> rowList = new ArrayList<Object>();

            lLstEmpDetails = getEmployeeDetails(lStrGpfAccNo, lStrTransactionId,lStrreqType);
            List lstOrderNo = getOrderNoFromGPFActNoAndTrsnID(lStrTransactionId);
            String loggedInDdoDtls=getDsgnAndFieldDept(gStrLocCode);
            String arrloggedInDdoDtls[]= loggedInDdoDtls.split("##");
           
            String dsgnLoggedDDO=arrloggedInDdoDtls[0];
            String fieldDept=arrloggedInDdoDtls[1];
            
           
            String lStrOrderNo = "";
            String ddoDesign = "";
            String tresName = "";
            String bilNo = "";
            Object lObj1[] = null;
            if(lLstEmpDetails != null && lLstEmpDetails.size() > 0){

                if(lstOrderNo != null && lstOrderNo.size() > 0){

                    if(lstOrderNo.get(0) != null ){
                        Object obj2[] = (Object[])lstOrderNo.get(0); 
                        lStrOrderNo = obj2[0].toString();
                        if(obj2[1] != null)
                            bilNo = obj2[1].toString();
                    }
                }

                lObj = (Object[]) lLstEmpDetails.get(0);
                lStrEmpDtls = lObj[0].toString() + ", " + lObj[1].toString() + ", "+gObjRsrcBndle.getString("GPF.PAYBILLGENLINE29A") + lObj[2].toString()+", "+lStrOrderNo;

                Date lDtSancDate = (Date) lObj[3];
                lStrEmpDtls += ", " + lObjSimpleDate.format(lDtSancDate);
                ddoDesign = lObj[5].toString();
                tresName = lObj[6].toString();

            }
            
            
           
            
            
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE33A"),leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE34A"),leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE35A"),leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("GPF.PAYBILLGENLINE36A"),leftAlign));
            dataList1.add(rowList);
            
           
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("PRAMANPATRA.LINE1A"),leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("PRAMANPATRA.LINE2A"),leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("PRAMANPATRA.LINE3A"),leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("PRAMANPATRA.LINE4A"),leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("PRAMANPATRA.LINE5A"),rightAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("PRAMANPATRA.LINE6A"),rightAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("PRAMANPATRA.LINE7A"),rightAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData(gObjRsrcBndle.getString("PRAMANPATRA.LINE8A"),rightAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
			rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("PRAMANPATRA.LINE9A"),lStrBillDate),
					leftAlign));
			dataList1.add(rowList);
			
			lStrDDOLocName= lObjGPFRequestProcess.getDDOLocName(lStrTransactionId);
            gLogger.error("********lStrDDOLocName*********"+lStrDDOLocName);
             
			gLogger.info("lStrDDOLocName:::::"+lStrDDOLocName);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData( MessageFormat.format(gObjRsrcBndle.getString("PRAMANPATRA.LINE10A"),lStrDDOLocName),
					leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData("...............................................................................................................................................................................",leftAlign));
            dataList.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData("For use of Treasury/Sub Treasury/Pay & Accounts Office",centerAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(("Pay Rs.  ------------------------/- (In words) Rupees -----------------------------------------------------------------------------------------------------"));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData("Pay by Transfer credit Rs. ------------------/-"+space(50)+"to (Scheme Code)",leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add("Auditor ---------------"+space(50)+"Supervisor ----------------"+space(50)+"STO/ATO/TO/APAO ------------------");
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData("................................................................................................................................................................................",leftAlign));
            dataList.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add("Cheque/e-Payment"+space(40)+"Date-------- "+space(50)+" Cheque/e-Payment Advise Slip ----------------------------");
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add("Payment Advise No.--------------------"+space(120)+"Delivered on date ----------------");
            dataList1.add(rowList);

            rowList = new ArrayList<Object>();
            rowList.add("STO/ATO/TO/APAO -----------------"+space(130)+"Delivery Clerk -----------------");
            dataList1.add(rowList);

            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData("....................................................................................................................................................................................",leftAlign));
            dataList.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add(new StyledData("For Audit use only ",centerAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add("Admitted for Rs. ------------/-"  +space(180) +"Objected for Rs. -------------/-");
            dataList1.add(rowList);

            rowList = new ArrayList<Object>();
            rowList.add(new StyledData("Reason for objection",leftAlign));
            dataList1.add(rowList);
            
            rowList = new ArrayList<Object>();
            rowList.add("Auditor"+space(75) + "Section Officer"   +space(75) +  "Accounts Officer");
            dataList1.add(rowList);
        }
            
        
        }
        catch (Exception e) {
            gLogger.error("Exception :" + e, e);
        }

        return dataList1;
    }
    
    
    
    

    private String getDsgnAndFieldDept(String strLocCode)
    {
        
        String ddoDtls = "-##-";
        List lLstData = null;

    gLogger.info("strLocCode******"+strLocCode);

    try{
        StringBuilder lSBQuery = new StringBuilder();
        lSBQuery.append(" select nvl(ddo.DSGN_NAME,'-') ||'##'||  nvl(loc.LOC_NAME,'-') from org_ddo_mst ddo inner join ");
        lSBQuery.append(" CMN_LOCATION_MST loc on loc.LOC_ID=ddo.HOD_LOC_CODE where ddo.LOCATION_CODE='"+strLocCode+"' ");

        Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
        lLstData=lQuery.list();

        if(lLstData != null && lLstData.size() > 0){
            ddoDtls = lLstData.get(0).toString();
        }
    }catch (Exception e) {
        gLogger.error("Exception :" + e, e);
    }

 
    return ddoDtls;
    
    }

    public String space(int noOfSpace) {
        String blank = "";
        for (int i = 0; i < noOfSpace; i++) {
            blank += "\u00a0";
        }
        return blank;
    }

    /*public String getNameOfOffice(String lStrGpfAccNo)
	{
		String lStrOfficeName = "";
		List lLstData = null;

		gLogger.info("lStrGpfAccNo******"+lStrGpfAccNo);

		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ODM.ddoOffice FROM OrgDdoMst ODM, MstEmpGpfAcc MEGA ");
			lSBQuery.append("WHERE MEGA.gpfAccNo = :gpfAccNo AND MEGA.ddoCode = ODM.ddoCode");

			lSBQuery.append( " SELECT o.OFF_NAME FROM MstEmp m,ORG_DESIGNATION_MST d,MST_DCPS_DDO_OFFICE o,HR_EIS_SCALE_MST s, MST_EMP_GPF_ACC gpf " ); 
			lSBQuery.append( " where m.designation = d.DSGN_ID and o.DCPS_DDO_OFFICE_MST_ID=m.currOff and s.SCALE_ID = m.payScale " ); 
			lSBQuery.append( " and gpf.SEVAARTH_ID=m.sevarthId and gpf.GPF_ACC_NO=:gpfAccNo " );


			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lLstData = lQuery.list();

			if(lLstData != null && lLstData.size() > 0){
				lStrOfficeName = lLstData.get(0).toString();
			}
		}catch (Exception e) {
			gLogger.error("Exception :" + e, e);
		}

		gLogger.info("lStrOfficeName****"+lStrOfficeName);
		return lStrOfficeName;
	}
     */
    /*public List getEmployeeDetails(String lStrGpfAccNo, String lStrTransactionId,String lStrreqType)
	{

		gLogger.error("lStrreqType"+lStrreqType);
		List lLstData = null;

		try{
			StringBuilder lSBQuery = new StringBuilder();

			if(lStrreqType.equalsIgnoreCase("RA")){
			lSBQuery.append("SELECT ME.EMP_NAME, ODM.DSGN_NAME, ME.BASIC_PAY, MGA.SANCTIONED_DATE, MGA.PAYABLE_AMOUNT , ddo.DSGN_NAME||' ',loc.LOC_NAME ");
			lSBQuery.append("FROM MST_DCPS_EMP ME JOIN MST_EMP_GPF_ACC MEGA ");
			lSBQuery.append("ON MEGA.MST_GPF_EMP_ID = ME.DCPS_EMP_ID AND MEGA.GPF_ACC_NO = :gpfAccNo ");
			lSBQuery.append("JOIN ORG_DESIGNATION_MST ODM ON ME.DESIGNATION = ODM.DSGN_ID ");
			lSBQuery.append("JOIN MST_GPF_ADVANCE MGA ON MEGA.GPF_ACC_NO = MGA.GPF_ACC_NO AND MGA.TRANSACTION_ID = :transactionId AND MGA.ADVANCE_TYPE=:reqtype ");
			lSBQuery.append(" join org_ddo_mst ddo on ddo.DDO_CODE = me.DDO_CODE  ");
			lSBQuery.append(" join CMN_LOCATION_MST loc on loc.LOC_ID = substr(me.ddo_Code,0,4) ");
		}


			if(lStrreqType.equalsIgnoreCase("NRA") || lStrreqType.equalsIgnoreCase("FW")){

				lSBQuery.append("SELECT ME.EMP_NAME, ODM.DSGN_NAME, ME.BASIC_PAY, MGA.SANCTIONED_DATE, MGA.AMOUNT_SANCTIONED , ddo.DSGN_NAME||' ',loc.LOC_NAME ");
				lSBQuery.append("FROM MST_DCPS_EMP ME JOIN MST_EMP_GPF_ACC MEGA ");
				lSBQuery.append("ON MEGA.MST_GPF_EMP_ID = ME.DCPS_EMP_ID AND MEGA.GPF_ACC_NO = :gpfAccNo ");
				lSBQuery.append("JOIN ORG_DESIGNATION_MST ODM ON ME.DESIGNATION = ODM.DSGN_ID ");
				lSBQuery.append("JOIN MST_GPF_ADVANCE MGA ON MEGA.GPF_ACC_NO = MGA.GPF_ACC_NO AND MGA.TRANSACTION_ID = :transactionId AND MGA.ADVANCE_TYPE=:reqtype ");
				lSBQuery.append(" join org_ddo_mst ddo on ddo.DDO_CODE = me.DDO_CODE  ");
				lSBQuery.append(" join CMN_LOCATION_MST loc on loc.LOC_ID = substr(me.ddo_Code,0,4) ");
		}



			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("transactionId", lStrTransactionId);
			lQuery.setParameter("reqtype", lStrreqType);

			lLstData = lQuery.list();
		}catch (Exception e) {
			gLogger.error("Exception :" + e, e);
		}

		return lLstData;
	}*/

    //GOKARNA
    public List getEmployeeDetails(String lStrGpfAccNo, String lStrTransactionId, String lStrreqType)
    {
        gLogger.error("lStrreqType" + lStrreqType);
        List lLstData = null;
        try
        {
            StringBuilder lSBQuery = new StringBuilder();

            if (lStrreqType.equalsIgnoreCase("RA")) {
                lSBQuery.append("SELECT ME.EMP_NAME, ODM.DSGN_NAME, ME.BASIC_PAY, MGA.SANCTIONED_DATE, MGA.PAYABLE_AMOUNT , ddo.DSGN_NAME||' ',loc.LOC_NAME ");
                lSBQuery.append(" , PAYABLE_AMOUNT+1 FROM MST_DCPS_EMP ME JOIN MST_EMP_GPF_ACC MEGA ");
                lSBQuery.append("ON MEGA.MST_GPF_EMP_ID = ME.DCPS_EMP_ID AND MEGA.GPF_ACC_NO = :gpfAccNo ");
                lSBQuery.append("JOIN ORG_DESIGNATION_MST ODM ON ME.DESIGNATION = ODM.DSGN_ID ");
                lSBQuery.append("JOIN MST_GPF_ADVANCE MGA ON MEGA.GPF_ACC_NO = MGA.GPF_ACC_NO AND MGA.TRANSACTION_ID = :transactionId AND MGA.ADVANCE_TYPE=:reqtype ");
                lSBQuery.append(" join org_ddo_mst ddo on ddo.DDO_CODE = me.DDO_CODE  ");
                lSBQuery.append(" join CMN_LOCATION_MST loc on loc.LOC_ID = substr(me.ddo_Code,0,4) ");
            }
            else if (lStrreqType.equalsIgnoreCase("NRA"))
            {
                lSBQuery.append("SELECT ME.EMP_NAME, ODM.DSGN_NAME, ME.BASIC_PAY, MGA.SANCTIONED_DATE, MGA.AMOUNT_SANCTIONED , ddo.DSGN_NAME||' ',loc.LOC_NAME ");
                lSBQuery.append(" ,PAYABLE_AMOUNT+1 FROM MST_DCPS_EMP ME JOIN MST_EMP_GPF_ACC MEGA ");
                lSBQuery.append("ON MEGA.MST_GPF_EMP_ID = ME.DCPS_EMP_ID AND MEGA.GPF_ACC_NO = :gpfAccNo ");
                lSBQuery.append("JOIN ORG_DESIGNATION_MST ODM ON ME.DESIGNATION = ODM.DSGN_ID ");
                lSBQuery.append("JOIN MST_GPF_ADVANCE MGA ON MEGA.GPF_ACC_NO = MGA.GPF_ACC_NO AND MGA.TRANSACTION_ID = :transactionId AND MGA.ADVANCE_TYPE=:reqtype ");
                lSBQuery.append(" join org_ddo_mst ddo on ddo.DDO_CODE = me.DDO_CODE  ");
                lSBQuery.append(" join CMN_LOCATION_MST loc on loc.LOC_ID = substr(me.ddo_Code,0,4) ");
            }
            else if (lStrreqType.equalsIgnoreCase("Final"))
            {
                gLogger.error("in FW" + lStrreqType);

                lSBQuery.append("SELECT ME.EMP_NAME, ODM.DSGN_NAME, ME.BASIC_PAY, MGA.HO_ACTION_DATE, MGA.AMOUNT_SANCTIONED , ddo.DSGN_NAME||' ',loc.LOC_NAME ");
                lSBQuery.append(" , PAYABLE_AMOUNT+1 FROM MST_DCPS_EMP ME JOIN MST_EMP_GPF_ACC MEGA ");
                lSBQuery.append("ON MEGA.MST_GPF_EMP_ID = ME.DCPS_EMP_ID AND MEGA.GPF_ACC_NO  = :gpfAccNo ");
                lSBQuery.append("JOIN ORG_DESIGNATION_MST ODM ON ME.DESIGNATION = ODM.DSGN_ID ");
                lSBQuery.append("JOIN TRN_GPF_FINAL_WITHDRAWAL MGA ON MEGA.GPF_ACC_NO = MGA.GPF_ACC_NO AND MGA.TRANSACTION_ID  = :transactionId ");
                lSBQuery.append("join org_ddo_mst ddo on ddo.DDO_CODE = me.DDO_CODE ");
                lSBQuery.append("join CMN_LOCATION_MST loc on loc.LOC_ID = substr(me.ddo_Code,0,4) ");
            }

            Query lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());

            lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
            lQuery.setParameter("transactionId", lStrTransactionId);

            if (!(lStrreqType.equalsIgnoreCase("FW"))) {
                lQuery.setParameter("reqtype", lStrreqType);
            }

            lLstData = lQuery.list();
        }
        catch (Exception e)
        {
            gLogger.error("Exception :" + e, e);
        }

        return lLstData;
    }



    public List getOrderNoFromGPFActNoAndTrsnID( String lStrTransactionId)
    {
        List lLstData = null;

        try{
            StringBuilder lSBQuery = new StringBuilder();
            lSBQuery.append("SELECT req.ORDER_No,bill.BILL_NO FROM mst_gpf_req req inner join MST_GPF_BILL_DTLS bill on req.TRANSACTION_ID = bill.TRANSACTION_ID where req.TRANSACTION_ID = :transactionId");
            Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
            gLogger.info("lQuery "+lStrTransactionId);
            //lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
            lQuery.setParameter("transactionId", lStrTransactionId);

            lLstData = lQuery.list();
        }catch (Exception e) {
            gLogger.error("Exception :" + e, e);
        }

        return lLstData;
    }

}




        