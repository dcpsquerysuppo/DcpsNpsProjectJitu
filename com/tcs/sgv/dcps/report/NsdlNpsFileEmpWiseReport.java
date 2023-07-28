package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.dao.TierIISixPcArrearDao;
import com.tcs.sgv.dcps.dao.TierIISixPcArrearDaoImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.ess.valueobject.OrgPostMst;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.Style;

public class NsdlNpsFileEmpWiseReport extends DefaultReportDataFinder {

    private static final Logger gLogger = Logger
    .getLogger(NsdlNpsFileEmpWiseReport.class);
    String Lang_Id = "en_US";
    String Loc_Id = "LC1";

    Map requestAttributes = null;

    SessionFactory lObjSessionFactory = null;

    ServiceLocator serviceLocator = null;

    public Collection findReportData(ReportVO report, Object criteria)
    throws ReportException {


        String locId = report.getLocId();

        Connection con = null;

        criteria.getClass();

        Statement smt = null;
        ResultSet rs = null;
        ArrayList dataList = new ArrayList();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        // for Center Alignment format
        StyleVO[] CenterAlignVO = new StyleVO[2];
        CenterAlignVO[0] = new StyleVO();
        CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
        CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
        CenterAlignVO[1] = new StyleVO();
        CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
        CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

        StyleVO[] noBorder = new StyleVO[1];
        noBorder[0] = new StyleVO();
        noBorder[0].setStyleId(IReportConstants.BORDER);
        noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);


        StyleVO[] leftboldStyleVO  = new StyleVO[3];
        leftboldStyleVO[0] = new StyleVO();
        leftboldStyleVO[0] .setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
        leftboldStyleVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_NORMAL);
        leftboldStyleVO[1] = new StyleVO();
        leftboldStyleVO[1] .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
        leftboldStyleVO[1].setStyleValue("Left");
        leftboldStyleVO[2] = new StyleVO();
        leftboldStyleVO[2].setStyleId(IReportConstants.BORDER);
        leftboldStyleVO[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
        

        StyleVO[] boldVO = new StyleVO[2];
		boldVO[0] = new StyleVO();
		boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_NORMAL);
		boldVO[1] = new StyleVO();
		boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		boldVO[1].setStyleValue("12");

        try {

            requestAttributes = (Map) ((Map) criteria)
            .get(IReportConstants.REQUEST_ATTRIBUTES);
            serviceLocator = (ServiceLocator) requestAttributes
            .get("serviceLocator");
            lObjSessionFactory = serviceLocator.getSessionFactorySlave();

            Map requestAttributes = (Map) ((Map) criteria)
            .get(IReportConstants.REQUEST_ATTRIBUTES);

            ServiceLocator serviceLocator = (ServiceLocator) requestAttributes
            .get("serviceLocator");

            ResourceBundle gObjRsrcBndle = ResourceBundle
            .getBundle("resources/dcps/dcpsLabels");


            SessionFactory lObjSessionFactory = serviceLocator
            .getSessionFactorySlave();
            con = lObjSessionFactory.getCurrentSession().connection();
            smt = con.createStatement();
            Map sessionKeys = (Map) ((Map) criteria)
            .get(IReportConstants.SESSION_KEYS);
            Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

            Long locationId = (Long) loginDetail.get("locationId");
            
            List<OrgPostMst> lLngPostIdList = (List) loginDetail.get("postIdList");
    		OrgPostMst lObjPostMst = lLngPostIdList.get(0);
    		Long lLngPostId = lObjPostMst.getPostId();
            

            String StrSqlQuery = "";

            if (report.getReportCode().equals("9000171")) {

                // report.setStyleList(noBorder);

                ArrayList rowList = new ArrayList();

                String fileNo = report.getParameterValue("fileNo").toString();
                String year = ((String) report.getParameterValue("year"));
                String month = ((String) report.getParameterValue("month"));

                String lStrDDOCode = null;
                String lStrDDOName = "";


                DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
                String url = "";


                url = "ifms.htm?actionFlag=loadNPSNSDLGenFiles&cmbYear="+year+"&cmbMonth="+month;



                StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
                lObjStyleVO = report.getStyleList();
                for (Integer lInt = 0; lInt < report.getStyleList().length; lInt++) {
                    if (lObjStyleVO[lInt].getStyleId() == 26) {
                        lObjStyleVO[lInt].setStyleValue(url);
                    }
                }


                StringBuilder SBQuery = new StringBuilder();


                /*SBQuery.append(" SELECT distinct sd.SD_PRAN_NO,sd.DDO_REG_NO,sd.SD_EMP_AMOUNT,sd.SD_EMPLR_AMOUNT,sd.SD_TOTAL_AMT FROM NSDL_SD_DTLS sd ");
                SBQuery.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME = sd.FILE_NAME and bh.STATUS <> '-1' and bh.FILE_NAME ='"+fileNo+"'");*/
                
                SBQuery.append(" SELECT distinct sd.SD_PRAN_NO,m.ddo_code,sd.DDO_REG_NO,sd.SD_EMP_AMOUNT,sd.SD_EMPLR_AMOUNT,sd.SD_TOTAL_AMT FROM NSDL_SD_DTLS sd ");////m.ddo_code,
                SBQuery.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME = sd.FILE_NAME inner join MST_DDO_REG  m  on sd.DDO_REG_NO = m.DDO_REG_NO ");
                SBQuery.append(" and bh.STATUS <> '-1' and bh.FILE_NAME ='"+fileNo+"' order by m.ddo_code ");/////$t KR 
             
              
                gLogger.info("StrSqlQuery***********"+SBQuery.toString());

                StrSqlQuery = SBQuery.toString();
                rs = smt.executeQuery(StrSqlQuery);
                Integer counter = 1;
              
                while (rs.next()) {

                    rowList = new ArrayList();
                    
                    rowList.add(new StyledData(counter, CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
                    
             
                    dataList.add(rowList);

                    counter = counter + 1;
                }
            }
            
            if (report.getReportCode().equals("80000964")) {
                // report.setStyleList(noBorder);
                ArrayList rowList = new ArrayList();
                String opgm_file = report.getParameterValue("opgmfile").toString();
                String year = ((String) report.getParameterValue("year"));
                String month = ((String) report.getParameterValue("month"));
                String lStrDDOCode = null;
                String lStrDDOName = "";
                DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
                String url = "";
                url = "ifms.htm?actionFlag=getOpgmFileList";
                StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
                lObjStyleVO = report.getStyleList();
                for (Integer lInt = 0; lInt < report.getStyleList().length; lInt++) {
                    if (lObjStyleVO[lInt].getStyleId() == 26) {
                        lObjStyleVO[lInt].setStyleValue(url);
                    }
                }
                report.setAdditionalHeader("Year :  "+year +"    Month : "+month +"  OPGM FILE ID :"+opgm_file);
                StringBuilder sb = new StringBuilder();
                sb.append("select f1.EMP_NAME,f1.SEVARTH_ID,f1.DCPS_ID,ddo.ddo_name,f1.DDO_CODE from FRM_FORM_S1_DTLS f1 "); 
                sb.append("join MST_DCPS_EMP em on em.SEVARTH_ID=f1.SEVARTH_ID and em.DCPS_ID = f1.DCPS_ID "); 
                sb.append("left join Org_Ddo_Mst ddo on ddo.DDO_CODE = f1.DDO_CODE "); 
                sb.append("where f1.OPGM_ID = '"+opgm_file+"' order by f1.ddo_code ");
                StrSqlQuery = sb.toString();
                rs = smt.executeQuery(StrSqlQuery);
                Integer counter = 1;
              
                while (rs.next()) {
                    rowList = new ArrayList();
                    rowList.add(new StyledData(counter, CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
                    dataList.add(rowList);
                    counter = counter + 1;
                }
            }
            
            
            /*else {
                 ArrayList rowList;
                 String billID;
                 String year;
                 String month;
                 Object lStrDDOCode;
                 String lStrDDOName;
                 String url;
                 StyleVO[] lObjStyleVO;
                 Integer lInt;
                 Integer counter;
                 StringBuilder sb;
                new ArrayList();
                billID = report.getParameterValue("billId").toString();
                year = (String)report.getParameterValue("year");
                month = (String)report.getParameterValue("month");
                lStrDDOCode = null;
                lStrDDOName = "";
                new DcpsCommonDAOImpl((Class)null, serviceLocator.getSessionFactory());
                url = "";
                url = "ifms.htm?actionFlag=getEmpViewBill&cmbYear=" + year + "&cmbMonth=" + month;
                lObjStyleVO = new StyleVO[report.getStyleList().length];
                lObjStyleVO = report.getStyleList();

                for(lInt = 0; lInt < report.getStyleList().length; lInt = lInt + 1) {
                   if (lObjStyleVO[lInt].getStyleId() == 26) {
                      lObjStyleVO[lInt].setStyleValue(url);
                   }
                }

                sb = new StringBuilder();
                sb.append(" select ddo_name from Org_Ddo_Mst where DDO_CODE =substr('" + billID + "',0,10) ");
                gLogger.info("StrSqlQuery***********" + sb.toString());
                StrSqlQuery = sb.toString();

                for(rs = smt.executeQuery(StrSqlQuery); rs.next(); lStrDDOName = rs.getString(1)) {
                }

                report.setAdditionalHeader("Year :  " + year + "          Month : " + month + "                  DDO Name :" + lStrDDOName);
                sb = new StringBuilder();
                sb.append("select  empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end ,sum(t.YEARLY_AMOUNT),sum(t.INTEREST),sum(t.YEARLY_AMOUNT)+sum(t.INTEREST) from  RLT_DCPS_SIXPC_YEARLY t ");
                sb.append("inner join MST_DCPS_EMP empmst on t.dcps_emp_id=empmst.dcps_emp_id ");
                sb.append(" join TIERTWO_BILL_DTLS tierII on tierII.DDO_CODE=t.DDO_CODE and tierII.BILL_ID=t.BILL_NO ");
                sb.append(" where t.status_flag='A'  and t.state_flag='3'  and t.BILL_NO='" + billID + "' group by empmst.EMP_NAME,empmst.SEVARTH_ID, empmst.PRAN_NO,empmst.DCPS_ID ");
                sb.append(" order by empmst.EMP_NAME ");
                StrSqlQuery = sb.toString();
                rs = smt.executeQuery(StrSqlQuery);

                for( counter = 1; rs.next(); counter = counter + 1) {
                   rowList = new ArrayList();
                   rowList.add(new StyledData(counter, CenterAlignVO));
                   rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
                   rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
                   rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
                   rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
                   rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
                   rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
                   rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
                   dataList.add(rowList);
                }
            }
*/            
            
            
            if (report.getReportCode().equals("80000963")) {
               new ArrayList();
               String billID = report.getParameterValue("billId").toString();
               String DdoCOde = (String)report.getParameterValue("year");
               String orderId = (String)report.getParameterValue("month");
               String year = (String)report.getParameterValue("from");
               String IsDeputation = (String)report.getParameterValue("deputation");
               Object month = null;
               String lStrDDOName = "";
               String Type = "";
               Type = billID.substring(0, 9);
               new DcpsCommonDAOImpl((Class)null, serviceLocator.getSessionFactory());
               String url;
               if (IsDeputation.equalsIgnoreCase("Y")) {
            	   url = "ifms.htm?actionFlag=getEmpViewBillDept";
                   if (year.equalsIgnoreCase("SRKA")) {
                	  if (!IsDeputation.equals("Y"))
                      url = "hrms.htm?actionFlag=ViewSrkaGrantApprove&asPopup=FALSE&locID=" + Type.substring(0, 4);
                	  else
                	  url = "hrms.htm?actionFlag=ViewSrkaGrantApprove&deputation=Y&asPopup=FALSE&locID=" + Type.substring(0, 4);
                   }
               }else{
            	   url = "ifms.htm?actionFlag=getEmpViewBill";
                   if (year.equalsIgnoreCase("SRKA")) {
                      url = "hrms.htm?actionFlag=ViewSrkaGrantApprove&asPopup=FALSE&locID=" + Type.substring(0, 4);
                   }  
               }
              

               StyleVO[] CenterBoldAlignVO = new StyleVO[report.getStyleList().length];
               CenterBoldAlignVO = report.getStyleList();

               for(Integer lInt = 0; lInt < report.getStyleList().length; lInt = lInt + 1) {
                  if (CenterBoldAlignVO[lInt].getStyleId() == 26) {
                     CenterBoldAlignVO[lInt].setStyleValue(url);
                  }
               }

               Type = billID.substring(0, 9);
               StringBuilder SBQuery = new StringBuilder();
               SBQuery.append(" select ddo_name from Org_Ddo_Mst where DDO_CODE =substr('" + billID + "',0,10) ");
               gLogger.info("StrSqlQuery***********" + SBQuery.toString());
               StrSqlQuery = SBQuery.toString();

               for(rs = smt.executeQuery(StrSqlQuery); rs.next(); lStrDDOName = rs.getString(1)) {
               }

               report.setAdditionalHeader("Year :  " + DdoCOde + "    Month : " + orderId + "  DDO Name :" + lStrDDOName);
               StringBuilder sb = new StringBuilder();
               sb.append("select empmst.EMP_NAME, empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end,sum(t.INST_AMOUNT), ");
               sb.append("sum(t.INT_AMOUNT),sum(t.INT_AMOUNT)+sum(t.INST_AMOUNT)from TierII_emp_details t inner join MST_DCPS_EMP empmst on t.dcps_id=empmst.dcps_emp_id ");
               
               if (IsDeputation.equalsIgnoreCase("Y"))
               sb.append("join TIERTWO_BILL_DTLS tierII on tierII.DDO_CODE=t.DDO_CODE and tierII.BILL_ID=t.billl_id where t.billl_id='" + billID + "' and tierII.BILL_STATUS in('12','14','15','16','17') ");
               else
               sb.append("join TIERTWO_BILL_DTLS tierII on tierII.DDO_CODE=t.DDO_CODE and tierII.BILL_ID=t.billl_id where t.billl_id='" + billID + "' and tierII.BILL_STATUS in('2','4','5','6','7') ");/////$t1Sept2022 
               
               sb.append("group by empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.PRAN_NO,empmst.DCPS_ID order by empmst.EMP_NAME ");
               StrSqlQuery = sb.toString();
               rs = smt.executeQuery(StrSqlQuery);
               int tierIIBillList = 1;

               while(rs.next()) {
                  ArrayList rowList = new ArrayList();
                  rowList.add(new StyledData(tierIIBillList++, CenterAlignVO));
                  rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
                  rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
                  rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
                  rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
                  rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
                  rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
                  rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
                  dataList.add(rowList);
                  new ArrayList();
                  url = null;
                  report.setAdditionalHeader("    DDO Code : " + Type + "  DDO Name :" + lStrDDOName);
               }
            }else if (report.getReportCode().equals("80000965")) {
                new ArrayList();
                String billID = (String)report.getParameterValue("ddoCode");
                String deputation = (String)report.getParameterValue("deputation");
                List lstDetailsG = this.getPendingData(billID,deputation);
                int counter = 1;

                for(int J1 = 0; J1 < lstDetailsG.size(); ++J1) {
                   Object[] tupleSub1 = (Object[])lstDetailsG.get(J1);
                   if (!tupleSub1[5].toString().equalsIgnoreCase("APPROVE") || !tupleSub1[7].toString().equalsIgnoreCase("APPROVE") || !tupleSub1[9].toString().equalsIgnoreCase("APPROVE") || !tupleSub1[11].toString().equalsIgnoreCase("APPROVE") || !tupleSub1[13].toString().equalsIgnoreCase("APPROVE")) {
                      ArrayList rowList = new ArrayList();
                      rowList.add(counter);
                      rowList.add(tupleSub1[1].toString());
                      rowList.add(tupleSub1[2].toString());
                      rowList.add(tupleSub1[3].toString());
                      if (tupleSub1[4] != null) {
                         rowList.add(tupleSub1[4].toString());
                      } else {
                         rowList.add("NA");
                      }

                      rowList.add(tupleSub1[5].toString());
                      if (tupleSub1[6] != null) {
                         rowList.add(tupleSub1[6].toString());
                      } else {
                         rowList.add("0");
                      }

                      rowList.add(tupleSub1[7].toString());
                      if (tupleSub1[8] != null) {
                         rowList.add(tupleSub1[8].toString());
                      } else {
                         rowList.add("0");
                      }

                      rowList.add(tupleSub1[9].toString());
                      if (tupleSub1[10] != null) {
                         rowList.add(tupleSub1[10].toString());
                      } else {
                         rowList.add("0");
                      }

                      rowList.add(tupleSub1[11].toString());
                      if (tupleSub1[12] != null) {
                         rowList.add(tupleSub1[12].toString());
                      } else {
                         rowList.add("0");
                      }

                      rowList.add(tupleSub1[13].toString());
                      dataList.add(rowList);
                      ++counter;
                   }
                }

                String year = "";
                if(deputation.equals("Y"))
                year = "ifms.htm?actionFlag=getEmpListForFiveInstApproveDept&noData=Y&deputation=Y";
                else
                year = "ifms.htm?actionFlag=getEmpListForFiveInstApprove";
                
                StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
                lObjStyleVO = report.getStyleList();

                for(Integer lInt = 0; lInt < report.getStyleList().length; lInt = lInt + 1) {
                   if (lObjStyleVO[lInt].getStyleId() == 26) {
                      lObjStyleVO[lInt].setStyleValue(year);
                   }
                }
             }else if  (report.getReportCode().equals("80000700")) {
	
            }else if (report.getReportCode().equals("80000978")) {
                final ResourceBundle gObjRsrcBndleForSPL90 = ResourceBundle.getBundle("resources/dcps/dcpsLabels");
                final StringBuilder SBQuery3 = new StringBuilder();
                final String DdoCOde2 = (String)report.getParameterValue("ddoCode");
                final String orderId;
                final String year2 = (String)report.getParameterValue("year");
                final String month2 = (String)report.getParameterValue("month");
                final String Type = (String)report.getParameterValue("type");
                final String from = (String)report.getParameterValue("from");
                final String deputation = (String)report.getParameterValue("deputation");
                
                if(Type.equals("700002")||Type.equals("100018"))
                orderId = (String)report.getParameterValue("OrderId");
                else
                orderId= report.getParameterValue("billId").toString();	
                
                
                ArrayList<Object> rowList4 = new ArrayList<Object>();
                final ArrayList<Object> dataList3 = new ArrayList<Object>();
                final StyleVO[] CenterBoldAlignVO2 = new StyleVO[4];
                (CenterBoldAlignVO2[0] = new StyleVO()).setStyleId(40);
                CenterBoldAlignVO2[0].setStyleValue("center");
                (CenterBoldAlignVO2[1] = new StyleVO()).setStyleId(1);
                CenterBoldAlignVO2[1].setStyleValue("none");
                (CenterBoldAlignVO2[2] = new StyleVO()).setStyleValue("bold");
                CenterBoldAlignVO2[2].setStyleId(37);
                (CenterBoldAlignVO2[3] = new StyleVO()).setStyleId(38);
                CenterBoldAlignVO2[3].setStyleValue("15");
                final StyleVO[] rightlignVO2 = new StyleVO[4];
                (rightlignVO2[0] = new StyleVO()).setStyleId(40);
                rightlignVO2[0].setStyleValue("right");
                (rightlignVO2[1] = new StyleVO()).setStyleId(1);
                rightlignVO2[1].setStyleValue("none");
                (rightlignVO2[2] = new StyleVO()).setStyleValue("normal");
                rightlignVO2[2].setStyleId(37);
                (rightlignVO2[3] = new StyleVO()).setStyleId(38);
                rightlignVO2[3].setStyleValue("14");
                
                if(Type.equals("700002")||Type.equals("100018")){
                    rowList4 = new ArrayList<Object>();
                    rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("J.HEADER1"), CenterBoldAlignVO2));
                    dataList3.add(rowList4);
                    rowList4 = new ArrayList<Object>();
                    if (!deputation.equals("Y"))
                    rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("J.HEADER"), CenterBoldAlignVO2));
                    else
                    rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("DJ.HEADER"), CenterBoldAlignVO2));
                    dataList3.add(rowList4);
                }
                else if (Type.equalsIgnoreCase("700003")) {
                    rowList4 = new ArrayList<Object>();
                    rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("Y.HEADER1"), CenterBoldAlignVO2));
                    dataList3.add(rowList4);
                }else if(from.equals("DDO")) {
                	rowList4 = new ArrayList<Object>();
                    rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("X.HEADER1"), CenterBoldAlignVO2));
                    dataList3.add(rowList4);
                    rowList4 = new ArrayList<Object>();
                    if (!deputation.equals("Y"))
                    rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("X.HEADER"), CenterBoldAlignVO2));
                    else
                    rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("DX.HEADER"), CenterBoldAlignVO2));	
                    dataList3.add(rowList4);
                }
                
                rowList4 = new ArrayList<Object>();
                if (!deputation.equals("Y"))
                rowList4.add(new StyledData((Object)(String.valueOf(gObjRsrcBndleForSPL90.getString("J.LETTER")) + orderId), CenterBoldAlignVO2));
                dataList3.add(rowList4);
                final TabularData tData3 = new TabularData((Collection)dataList3);
                tData3.addStyle(40, "center");
                tData3.addStyle(1, "No");
                report.setAdditionalHeader((Object)tData3);
                List tierIIBillList = null;
                if(Type.equals("700002")||Type.equals("100018"))
                    tierIIBillList = this.getOrderDetailsG(orderId, month2, year2,deputation);
                else
                    tierIIBillList = this.getOrderDetailsY(orderId, month2, year2,deputation);
                
                String sevaarthId = "";
                for (int J4 = 0; J4 < tierIIBillList.size(); ++J4) {
                    sevaarthId = null;
                    String EmployeeDetails = null;
                    final Object[] tupleSub4 = (Object[]) tierIIBillList.get(J4);
                    sevaarthId = tupleSub4[0].toString();
                    EmployeeDetails = tupleSub4[1].toString();
                    Double installment1 = 0.0;
                    Double installment2 = 0.0;
                    Double installment3 = 0.0;
                    Double installment4 = 0.0;
                    Double installment5 = 0.0;
                    Double TotalInst = 0.0;
                    String employee_name = "";
                    String Sevaarth_id = "";
                    String dpcs_emp_id = "";
                    Object[] tupleSub5 = null;
                    final List lstInstallment = this.getTierIIInterest(sevaarthId);
                    for (int i = 0; i < lstInstallment.size(); ++i) {
                        tupleSub5 = (Object[]) lstInstallment.get(i);
                        installment1 = Double.parseDouble(tupleSub5[1].toString());
                        installment2 = Double.parseDouble(tupleSub5[2].toString());
                        installment3 = Double.parseDouble(tupleSub5[3].toString());
                        installment4 = Double.parseDouble(tupleSub5[4].toString());
                        installment5 = Double.parseDouble(tupleSub5[5].toString());
                        TotalInst = installment1 + installment2 + installment3 + installment4 + installment5;
                        employee_name = (String)tupleSub5[7];
                        Sevaarth_id = (String)tupleSub5[8];
                        dpcs_emp_id = tupleSub5[0].toString();
                    }
                    Double openingBalance = 0.0;
                    Double closingBalance = 0.0;
                    Double Total = 0.0;
                    Double Interest = 0.0;
                    final Double yeadDays = 365.0;
                    final Double leapYeadDays = 366.0;
                    final List lstLoopingForTierII = this.getLoopingFOrIntereseCalculations();
                    
                    if(!deputation.equals("Y"))
                    	lstLoopingForTierII.remove(13);
                    	
                    Double PayableTotal2 = 0.0;
                    List td = new ArrayList();
                    for (int j = 0; j < lstLoopingForTierII.size(); ++j) {
                        final Object[] tupleSub6 = (Object[]) lstLoopingForTierII.get(j);
                        td = new ArrayList();
                        td.add(new StyledData(String.valueOf(j + 1),boldVO));
                        //td.add(new StyledData(nosci(balanceEmplr).toString(),boldVO));
                        td.add(new StyledData(tupleSub6[1].toString(),boldVO));
                        td.add(new StyledData( (double) Math.round(openingBalance),boldVO));
                        if (j == 0) {
                            td.add(new StyledData(installment1,boldVO));
                            Total = openingBalance + installment1;
                            Interest = this.CalculateInterest(tupleSub6[0].toString(), openingBalance, installment1, yeadDays,orderId,dpcs_emp_id,from,deputation);
                        }
                        else {
                            td.add(new StyledData("0",boldVO));
                        }
                        if (j == 1) {
                            td.add(new StyledData(installment2,boldVO));
                            Total = openingBalance + installment2;
                            Interest = this.CalculateInterest(tupleSub6[0].toString(), openingBalance, installment2, yeadDays,orderId,dpcs_emp_id,from,deputation);
                        }
                        else {
                            td.add(new StyledData("0",boldVO));
                        }
                        if (j == 2) {
                            td.add(new StyledData(installment3,boldVO));
                            Total = openingBalance + installment3;
                            Interest = this.CalculateInterest(tupleSub6[0].toString(), openingBalance, installment3, yeadDays,orderId,dpcs_emp_id,from,deputation);
                        }
                        else {
                            td.add(new StyledData("0",boldVO));
                        }
                        if (j == 3) {
                            td.add(new StyledData(installment4,boldVO));
                            Total = openingBalance + installment4;
                            Interest = this.CalculateInterest(tupleSub6[0].toString(), openingBalance, installment4, yeadDays,orderId,dpcs_emp_id,from,deputation);
                        }
                        else {
                            td.add(new StyledData("0",boldVO));
                        }
                        if (j == 4) {
                            td.add(new StyledData(installment5,boldVO));
                            Total = openingBalance + installment5;
                            Interest = this.CalculateInterest(tupleSub6[0].toString(), openingBalance, installment5, yeadDays,orderId,dpcs_emp_id,from,deputation);
                        }
                        else {
                            td.add(new StyledData("0",boldVO));
                        }
                        if (j > 4) {
                            Interest = this.CalculateInterest(tupleSub6[0].toString(), openingBalance, 0.0, yeadDays,orderId,dpcs_emp_id,from,deputation);
                            Total = openingBalance;
                            Total = Double.parseDouble(new DecimalFormat("##.##").format(openingBalance));
                            Interest=Double.parseDouble(new DecimalFormat("##.##").format(Interest));
                        }
                       
                       if(!deputation.equals("Y")){ 
                       if(tupleSub6[0].toString().equals("34")){
                        td.add(new StyledData(Math.round(Total),boldVO));
                        td.add(new StyledData(Math.round(Interest),boldVO));
                        PayableTotal2 += Math.round(Interest);
                        closingBalance = (double) (Math.round(Interest) + Math.round(Total));
                        td.add(new StyledData(EmployeeDetails,boldVO));
                        //td.add(EmployeeDetails);
                        td.add(new StyledData(Math.round(closingBalance),boldVO));
                        openingBalance = (double) Math.round(closingBalance);
                        dataList.add(td);
                        }else{
                        	 td.add(new StyledData(Total,boldVO));
                             td.add(new StyledData(Interest,boldVO));
                             PayableTotal2 += Interest;
                             closingBalance = Interest + Total;
                             td.add(new StyledData(EmployeeDetails,boldVO));
                             //td.add(EmployeeDetails);
                             td.add(new StyledData(closingBalance,boldVO));
                             openingBalance = closingBalance;
                             dataList.add(td);
                        }
                      }else{
                    	  if(tupleSub6[0].toString().equals("35")){
                              td.add(new StyledData(Math.round(Total),boldVO));
                              td.add(new StyledData(Math.round(Interest),boldVO));
                              PayableTotal2 += Math.round(Interest);
                              closingBalance = (double) (Math.round(Interest) + Math.round(Total));
                              td.add(new StyledData(EmployeeDetails,boldVO));
                              //td.add(EmployeeDetails);
                              td.add(new StyledData(Math.round(closingBalance),boldVO));
                              openingBalance = (double) Math.round(closingBalance);
                              dataList.add(td);
                              }else{
                              	 td.add(new StyledData(Total,boldVO));
                                   td.add(new StyledData(Interest,boldVO));
                                   PayableTotal2 += Interest;
                                   closingBalance = Interest + Total;
                                   td.add(new StyledData(EmployeeDetails,boldVO));
                                   //td.add(EmployeeDetails);
                                   td.add(new StyledData(closingBalance,boldVO));
                                   openingBalance = closingBalance;
                                   dataList.add(td);
                         } 
                      }
                    }
                }
                String url6 = ""; 
                if(Type.equals("100018")){
                	url6 = "ifms.htm?actionFlag=getNamunaFDept&login=HOD";
                }else{
                url6 = "ifms.htm?actionFlag=getNamunaF";
                if (Type.equalsIgnoreCase("700003")) {
                    url6 = "ifms.htm?actionFlag=getNamunaF&DddoCode=" + DdoCOde2;
                }else if (Type.equals("") && deputation.equals("Y")) {/////$t
                    url6 = "ifms.htm?actionFlag=getEmpViewBillDept";
                }else if (Type.equals("") && !deputation.equals("Y")) {/////$t
                    url6 = "ifms.htm?actionFlag=getEmpViewBill";
                }else if (Type.equalsIgnoreCase("700004")){
                	if (!deputation.equals("Y"))
                	url6 = "ifms.htm?actionFlag=ViewSrkaGrantApprove&locID="+DdoCOde2.substring(0, 4);
                	else
                	url6 = "ifms.htm?actionFlag=ViewSrkaGrantApprove&deputation=Y&locID="+DdoCOde2.substring(0, 4);
                }
            }
                StyleVO[] lObjStyleVO6 = new StyleVO[report.getStyleList().length];
                lObjStyleVO6 = report.getStyleList();
                for (Integer lInt6 = 0; lInt6 < report.getStyleList().length; ++lInt6) {
                    if (lObjStyleVO6[lInt6].getStyleId() == 26) {
                        lObjStyleVO6[lInt6].setStyleValue(url6);
                    }
                }
                
                String DesignationName2 = "";
                final Long userId = Long.parseLong(loginDetail.get("userId").toString());
                List lstDDoDetails2 = null;
                if (Type.equalsIgnoreCase("700002")) {
                    lstDDoDetails2 = this.getDDODetails(DdoCOde2);
                    for (int J5 = 0; J5 < lstDDoDetails2.size(); ++J5) {
                        final Object[] tupleSub7 = (Object[]) lstDDoDetails2.get(J5);
                        if (tupleSub7[3] != null) {
                            DesignationName2 = tupleSub7[3].toString();
                        }
                    }
                }else {
                    DesignationName2 = this.getTotails(userId);
                }
                
                /*String DesignationName2 = "";
                if((Type.equals("100018")|| Type.equals("700004"))|| (Type.equals("") && deputation.equals("Y"))){
                DesignationName2 = this.getHODDetails(lLngPostId);
               }else{
                final Long userId = Long.parseLong(loginDetail.get("userId").toString());
                List lstDDoDetails2 = null;
                if (Type.equalsIgnoreCase("700002")) {
                    lstDDoDetails2 = this.getDDODetails(DdoCOde2);
                    for (int J5 = 0; J5 < lstDDoDetails2.size(); ++J5) {
                        final Object[] tupleSub7 = (Object[]) lstDDoDetails2.get(J5);
                        if (tupleSub7[3] != null) {
                            DesignationName2 = tupleSub7[3].toString();
                        }
                    }
                }else {
                    DesignationName2 = this.getTotails(userId);
                }
               }*/ 
                
                final ReportAttributeVO[] lArrrReportAttributeVO2 = new ReportAttributeVO[7];
                (lArrrReportAttributeVO2[0] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO2[0].setLocation(2);
                lArrrReportAttributeVO2[0].setAlignment(4);
                lArrrReportAttributeVO2[0].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.NEWLINE"), CenterBoldAlignVO2) + "<br> <br>"));
                (lArrrReportAttributeVO2[1] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO2[1].setLocation(2);
                lArrrReportAttributeVO2[1].setAlignment(4);
                lArrrReportAttributeVO2[1].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.NEWLINE"), CenterBoldAlignVO2) + "<br> <br>"));
                (lArrrReportAttributeVO2[2] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO2[2].setLocation(2);
                lArrrReportAttributeVO2[2].setAlignment(4);
                lArrrReportAttributeVO2[2].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.BOTTOM1"), CenterBoldAlignVO2) + "<br> <br>  "));
                (lArrrReportAttributeVO2[3] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO2[3].setLocation(2);
                lArrrReportAttributeVO2[3].setAlignment(4);
                lArrrReportAttributeVO2[3].setAttributeValue((Object)(new StyledData((Object)DesignationName2, CenterBoldAlignVO2) + "<br> <br>"));
                (lArrrReportAttributeVO2[4] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO2[4].setLocation(2);
                lArrrReportAttributeVO2[4].setAlignment(4);
                lArrrReportAttributeVO2[4].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.BOTTOM2"), CenterBoldAlignVO2) + "<br> <br>  "));
                (lArrrReportAttributeVO2[5] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO2[5].setLocation(2);
                lArrrReportAttributeVO2[5].setAlignment(4);
                lArrrReportAttributeVO2[5].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.BOTTOM3"), CenterBoldAlignVO2) + "<br> <br>  "));
                
                String DDOCODE3 ="( DDO CODE :" + DdoCOde2 + " )";
                
                /*String DDOCODE3 = "";
                if(Type.equals("100018")||(Type.equals("") && deputation.equals("Y")))
                	DDOCODE3 = "( DDO CODE : "+space(30)+")";
                    else 
                    DDOCODE3 = "( DDO CODE :" + DdoCOde2 + " )";*/
                
                (lArrrReportAttributeVO2[6] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO2[6].setLocation(2);
                lArrrReportAttributeVO2[6].setAlignment(4);
                lArrrReportAttributeVO2[6].setAttributeValue((Object)(new StyledData((Object)DDOCODE3, CenterBoldAlignVO2) + "<br> <br>  "));
                final String lStrRptName2 = report.getReportName();
                report.setReportAttributes(lArrrReportAttributeVO2);
            }
            else if (report.getReportCode().equals("80000701")) {
                final ResourceBundle gObjRsrcBndleForSPL90 = ResourceBundle.getBundle("resources/dcps/dcpsLabels");
                final StringBuilder SBQuery3 = new StringBuilder();
                final String DdoCOde2 = (String)report.getParameterValue("ddoCode");
                final String orderId = (String)report.getParameterValue("OrderId");
                final String year2 = (String)report.getParameterValue("year");
                final String month2 = (String)report.getParameterValue("month");
                final Double PayableTotal1 = Double.parseDouble((String)report.getParameterValue("amount"));
                final String Type = (String)report.getParameterValue("type");
                String deputation = (String) report.getParameterValue("deputation");
                
                ArrayList<Object> rowList4 = new ArrayList<Object>();
                final StyleVO[] CenterBoldAlignVO = new StyleVO[4];
                (CenterBoldAlignVO[0] = new StyleVO()).setStyleId(40);
                CenterBoldAlignVO[0].setStyleValue("center");
                (CenterBoldAlignVO[1] = new StyleVO()).setStyleId(1);
                CenterBoldAlignVO[1].setStyleValue("none");
                (CenterBoldAlignVO[2] = new StyleVO()).setStyleValue("bold");
                CenterBoldAlignVO[2].setStyleId(37);
                (CenterBoldAlignVO[3] = new StyleVO()).setStyleId(38);
                CenterBoldAlignVO[3].setStyleValue("15");
                final StyleVO[] rightlignVO = new StyleVO[4];
                (rightlignVO[0] = new StyleVO()).setStyleId(40);
                rightlignVO[0].setStyleValue("right");
                (rightlignVO[1] = new StyleVO()).setStyleId(1);
                rightlignVO[1].setStyleValue("none");
                (rightlignVO[2] = new StyleVO()).setStyleValue("normal");
                rightlignVO[2].setStyleId(37);
                (rightlignVO[3] = new StyleVO()).setStyleId(38);
                rightlignVO[3].setStyleValue("14");
                final StyleVO[] leftlignVO11 = new StyleVO[4];
                (leftlignVO11[0] = new StyleVO()).setStyleId(40);
                leftlignVO11[0].setStyleValue("left");
                (leftlignVO11[1] = new StyleVO()).setStyleId(1);
                leftlignVO11[1].setStyleValue("none");
                (leftlignVO11[2] = new StyleVO()).setStyleValue("normal");
                leftlignVO11[2].setStyleId(37);
                (leftlignVO11[3] = new StyleVO()).setStyleId(38);
                leftlignVO11[3].setStyleValue("14");
                final StyleVO[] centerWithoutBoldVO = new StyleVO[4];
                (centerWithoutBoldVO[0] = new StyleVO()).setStyleId(40);
                centerWithoutBoldVO[0].setStyleValue("center");
                (centerWithoutBoldVO[1] = new StyleVO()).setStyleId(1);
                centerWithoutBoldVO[1].setStyleValue("none");
                (centerWithoutBoldVO[2] = new StyleVO()).setStyleValue("normal");
                centerWithoutBoldVO[2].setStyleId(37);
                (centerWithoutBoldVO[3] = new StyleVO()).setStyleId(38);
                centerWithoutBoldVO[3].setStyleValue("16");
                final String amountWord = EnglishDecimalFormat.convertWithSpace(new BigDecimal(PayableTotal1));
                String LocationName = "NA";
                String DdoOfficeName = "NA";
                String DdoAddress = "NA";
                String DesignationName = "NA";
                final List lstDDoDetails = this.getDDODetails(DdoCOde2);
                for (int J2 = 0; J2 < lstDDoDetails.size(); ++J2) {
                    final Object[] tupleSub2 = (Object[]) lstDDoDetails.get(J2);
                    if (tupleSub2[0] != null) {
                        DdoOfficeName = tupleSub2[0].toString();
                    }
                    if (tupleSub2[1] != null) {
                        DdoAddress = tupleSub2[1].toString();
                    }
                    if (tupleSub2[2] != null) {
                        LocationName = tupleSub2[2].toString();
                    }
                    if (tupleSub2[3] != null) {
                        DesignationName = tupleSub2[3].toString();
                    }
                }
                
                
            	/*String hodDSGN="NA";
                if(Type.equals("100018")){
                	hodDSGN = this.getHODDetails(lLngPostId);
                }*/
                
                
                final Date date = new Date();
                final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                final String strDate = formatter.format(date);
                final ArrayList<Object> dataList2 = new ArrayList<Object>();
                
                if(Type.equals("700002")||Type.equals("100018")){////ddo login
                rowList4 = new ArrayList<Object>();
                rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("GG.header1"), CenterBoldAlignVO));
                dataList2.add(rowList4);
                rowList4 = new ArrayList<Object>();
                if(!deputation.equals("Y")){
                rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("GG.header2"), CenterBoldAlignVO));
                }else{
                rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("DGG.header2"), CenterBoldAlignVO));
                dataList2.add(rowList4);
                rowList4 = new ArrayList<Object>();
                rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("DGG.header3"), CenterBoldAlignVO));
                dataList2.add(rowList4);
                }
                }else{
                rowList4 = new ArrayList<Object>();
                rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("GG.header5TO"), CenterBoldAlignVO));
                dataList2.add(rowList4);
                rowList4 = new ArrayList<Object>();
                if (!deputation.equals("Y"))
                rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("GG.header51TO"), CenterBoldAlignVO));
                else
                rowList4.add(new StyledData((Object)gObjRsrcBndleForSPL90.getString("DGG.header51TO"), CenterBoldAlignVO));
                dataList2.add(rowList4);
                }
                final TabularData tData = new TabularData((Collection)dataList2);
                tData.addStyle(40, "center");
                tData.addStyle(1, "No");
                report.setAdditionalHeader((Object)tData);
                final List lstDetailsG2 = this.getDetailsForG(orderId,Type,deputation);
                int counter4 = 1;
                for (int J3 = 0; J3 < lstDetailsG2.size(); ++J3) {
                    final Object[] tupleSub3 = (Object[]) lstDetailsG2.get(J3);
                    if (tupleSub3[0] != null) {
                        DdoOfficeName = tupleSub3[0].toString();
                    }
                    rowList4 = new ArrayList<Object>();
                    rowList4.add(counter4);
                    rowList4.add(tupleSub3[0].toString());
                    rowList4.add(tupleSub3[1].toString());
                    rowList4.add(tupleSub3[2].toString());
                    if (tupleSub3[3] != null) {
                        rowList4.add(tupleSub3[3].toString());
                    }
                    else {
                        rowList4.add("NA");
                    }
                    rowList4.add(tupleSub3[4].toString());
                    rowList4.add(tupleSub3[5].toString());
                    rowList4.add(tupleSub3[6].toString());
                    dataList.add(rowList4);
                    ++counter4;
                }
                String url4 = "";
                if(Type.equals("100018"))
                url4 = "ifms.htm?actionFlag=getNamunaFDept&login=HOD";
                else
                url4 = "ifms.htm?actionFlag=getNamunaF";
                
                if (Type.equalsIgnoreCase("700003")) {
                    //url4 = "ifms.htm?actionFlag=getNamunaF&DddoCode=" + DdoCOde2;
                	if(deputation.equals("Y"))
                    url4 = "ifms.htm?actionFlag=getNamunaFDept&login=Treasury&DddoCode="+DdoCOde2;
                	else
                	url4 = "ifms.htm?actionFlag=getNamunaF&login=Treasury&DddoCode="+DdoCOde2;	
                }
                StyleVO[] lObjStyleVO4 = new StyleVO[report.getStyleList().length];
                lObjStyleVO4 = report.getStyleList();
                for (Integer lInt4 = 0; lInt4 < report.getStyleList().length; ++lInt4) {
                    if (lObjStyleVO4[lInt4].getStyleId() == 26) {
                        lObjStyleVO4[lInt4].setStyleValue(url4);
                    }
                }
                
                /*lArrrReportAttributeVO[1].setLocation(IReportConstants.LOCATION_FOOTER);
				lArrrReportAttributeVO[1].setAlignment(IReportConstants.ALIGN_RIGHT);*/
                
                if(Type.equals("700002")||Type.equals("100018")){////ddo login
                final ReportAttributeVO[] lArrrReportAttributeVO = new ReportAttributeVO[7];
                (lArrrReportAttributeVO[0] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO[0].setLocation(2);
                lArrrReportAttributeVO[0].setAlignment(4);
                lArrrReportAttributeVO[0].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.NEWLINE"), CenterBoldAlignVO) + "<br> <br>"));
                (lArrrReportAttributeVO[1] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO[1].setLocation(2);
                lArrrReportAttributeVO[1].setAlignment(4);
                lArrrReportAttributeVO[1].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.NEWLINE"), CenterBoldAlignVO) + "<br> <br>"));
                (lArrrReportAttributeVO[2] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO[2].setLocation(2);
                lArrrReportAttributeVO[2].setAlignment(4);
                lArrrReportAttributeVO[2].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.BOTTOM1"), CenterBoldAlignVO) + "<br> <br>  "));
                (lArrrReportAttributeVO[3] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO[3].setLocation(2);
                lArrrReportAttributeVO[3].setAlignment(4);
                /*if(Type.equals("100018"))
                lArrrReportAttributeVO[3].setAttributeValue((Object)(new StyledData((Object)hodDSGN, CenterBoldAlignVO) + "<br> <br>"));
                else*/
                lArrrReportAttributeVO[3].setAttributeValue((Object)(new StyledData((Object)DesignationName, CenterBoldAlignVO) + "<br> <br>"));
                
                (lArrrReportAttributeVO[4] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO[4].setLocation(2);
                lArrrReportAttributeVO[4].setAlignment(4);
                lArrrReportAttributeVO[4].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.BOTTOM2"), CenterBoldAlignVO) + "<br> <br>  "));
                (lArrrReportAttributeVO[5] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO[5].setLocation(2);
                lArrrReportAttributeVO[5].setAlignment(4);
                lArrrReportAttributeVO[5].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.BOTTOM3"), CenterBoldAlignVO) + "<br> <br>  "));
                final String DDOCODE;
                /*if(Type.equals("100018"))
                DDOCODE = "( DDO CODE : "+space(30)+")";
                else */
                DDOCODE = "( DDO CODE :" + DdoCOde2 + " )";
                
                (lArrrReportAttributeVO[6] = new ReportAttributeVO()).setAttributeType(7);
                lArrrReportAttributeVO[6].setLocation(2);
                lArrrReportAttributeVO[6].setAlignment(4);
                lArrrReportAttributeVO[6].setAttributeValue((Object)(new StyledData((Object)DDOCODE, CenterBoldAlignVO) + "<br> <br>  "));
                final String lStrRptName = report.getReportName();
                report.setReportAttributes(lArrrReportAttributeVO);
                }else{
                	final ReportAttributeVO[] lArrrReportAttributeVO = new ReportAttributeVO[5];
                    (lArrrReportAttributeVO[0] = new ReportAttributeVO()).setAttributeType(7);
                    lArrrReportAttributeVO[0].setLocation(2);
                    lArrrReportAttributeVO[0].setAlignment(4);
                    lArrrReportAttributeVO[0].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.NEWLINE"), CenterBoldAlignVO) + "<br> <br>"));
                    (lArrrReportAttributeVO[1] = new ReportAttributeVO()).setAttributeType(7);
                    lArrrReportAttributeVO[1].setLocation(2);
                    lArrrReportAttributeVO[1].setAlignment(4);
                    lArrrReportAttributeVO[1].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.NEWLINE"), CenterBoldAlignVO) + "<br> <br>"));
                    (lArrrReportAttributeVO[2] = new ReportAttributeVO()).setAttributeType(7);
                    lArrrReportAttributeVO[2].setLocation(2);
                    lArrrReportAttributeVO[2].setAlignment(4);
                    lArrrReportAttributeVO[2].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.BOTTOM1"), CenterBoldAlignVO) + "<br> <br>  "));
                    (lArrrReportAttributeVO[3] = new ReportAttributeVO()).setAttributeType(7);
                    lArrrReportAttributeVO[3].setLocation(2);
                    lArrrReportAttributeVO[3].setAlignment(4);
                    lArrrReportAttributeVO[3].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("GG.headerB2TO"), CenterBoldAlignVO) + "<br> <br>  "));
                    (lArrrReportAttributeVO[4] = new ReportAttributeVO()).setAttributeType(7);
                    lArrrReportAttributeVO[4].setLocation(2);
                    lArrrReportAttributeVO[4].setAlignment(4);
                    lArrrReportAttributeVO[4].setAttributeValue((Object)(new StyledData((Object)gObjRsrcBndleForSPL90.getString("FF.BOTTOM3"), CenterBoldAlignVO) + "<br> <br>  "));
                    
                    final String lStrRptName = report.getReportName();
                    report.setReportAttributes(lArrrReportAttributeVO);
                }
            }else if(report.getReportCode().equals("80000703")) {
            
                   
            }
        } catch (Exception e) {
            e.printStackTrace();
            gLogger.error("Exception :" + e, e);
        } finally {
            try {
                if (smt != null) {
                    smt.close();
                }

                if (rs != null) {
                    rs.close();
                }

                if (con != null) {
                    con.close();
                }

                smt = null;
                rs = null;
                con = null;

            } catch (Exception e) {
                e.printStackTrace();
                gLogger.error("Exception :" + e, e);
            }
        }
        return dataList;
    }
    public List getTierIIInterest(final String SevaarthID) throws Exception {
        StringBuilder lSBQuery = new StringBuilder();
        List SevarthIdList = null;
        try {
            lSBQuery = new StringBuilder();
            lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,cast(t8.yearly_amount as varchar) as TimeA,cast(t2.yearly_amount as varchar) as TimeB,cast(t3.yearly_amount as varchar) as TimeC,cast(t4.yearly_amount as varchar) as TimeD,cast(t5.yearly_amount as varchar) as TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end  ");
            lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
            lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 ");
            lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 ");
            lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 ");
            lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 ");
            lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 ");
            lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
            lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
            lSBQuery.append(" where t.status_flag='A' and empmst.SEVARTH_ID ='" + SevaarthID + "' ");
            lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            final Query lQuery = (Query)ghibSession.createSQLQuery(lSBQuery.toString());
            SevarthIdList = lQuery.list();
        }
        catch (Exception e) {
            NsdlNpsFileEmpWiseReport.gLogger.error((Object)(" Error in getEmpDtls " + e), (Throwable)e);
            e.printStackTrace();
            throw e;
        }
        return SevarthIdList;
    }
    
    public List getLoopingFOrIntereseCalculations() throws Exception {
        StringBuilder sb = new StringBuilder();
        List SevarthIdList = null;
        try {
            sb = new StringBuilder();
            sb.append("select distinct(r.FIN_YEAR_ID) ,fin.FIN_YEAR_DESC    from MST_DCPS_INTEREST_RATE r ");
            sb.append("join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =r.FIN_YEAR_ID ");
            sb.append("where r.FIN_YEAR_ID >21 order by r.FIN_YEAR_ID ");
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            final Query lQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            SevarthIdList = lQuery.list();
        }
        catch (Exception e) {
            NsdlNpsFileEmpWiseReport.gLogger.error((Object)(" Error in getEmpDtls " + e), (Throwable)e);
            e.printStackTrace();
            throw e;
        }
        return SevarthIdList;
    }
    
	////$t 7Dec2021
	private Double CalculateInterest(String financialYear,Double openingBalance,Double installmentAmt,Double days,String orderId,String dcps_emp_id,String from, String deputation) {
		
		Double finslInt=0D;
		try {
			 final List lstRateOfInterest = getInterestCalculation(financialYear);
			
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
				if(!deputation.equals("Y")){
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
					
					final String letterDate = getLetterCreatedDate(orderId,dcps_emp_id,from);
					//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					//String endDate = sdf.format(date);
					String [] val=letterDate.split("/");
					/*System.out.print("-->"+val[1]);
					System.out.print("-->"+map.get("12"));
					System.out.print("-->"+map.get(val[1]));*/
					int months=Integer.parseInt(map.get(val[1]));
					//finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-4))/12));//apr-july 4
					
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
				 }
				 }else{
						if(financialYear.equals("35")){
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
							
							final String letterDate = getLetterCreatedDate(orderId,dcps_emp_id,from);
							//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							//String endDate = sdf.format(date);
							String [] val=letterDate.split("/");
							/*System.out.print("-->"+val[1]);
							System.out.print("-->"+map.get("12"));
							System.out.print("-->"+map.get(val[1]));*/
							int months=Integer.parseInt(map.get(val[1]));
							
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
						 }
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
		//return (double) Math.round(finslInt);
		return (double) finslInt;
	}

	/* private Double CalculateInterest(final String financialYear, final Double openingBalance, final Double installmentAmt, final Double days) {
        Double finslInt = 0.0;
        try {
            final List lstRateOfInterest = getInterestCalculation(financialYear);
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
						
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((interestDays1*interest*installmentAmt)/days));

					}
					else
					{
						if(installmentAmt!=0)
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((304*interest*installmentAmt)/days));

					}
					
				}
				
				if(openingBalance!=0)
				finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((interestDays*interest*openingBalance)/days));
				
				
				
				
			}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Double.valueOf(Math.round(finslInt));
    }
*/    
    public List getInterestCalculation(final String finYearId) throws Exception {
        StringBuilder sb = new StringBuilder();
        List SevarthIdList = null;
        try {
            sb = new StringBuilder();
            if (finYearId.equalsIgnoreCase("32")) {
                System.out.println("admin");
            }
            sb.append("select r.INTEREST/100, DAYS (DATE(r.APPLICABLE_TO)) -DAYS (DATE(r.EFFECTIVE_FROM))+1    from MST_DCPS_INTEREST_RATE r ");
            sb.append("join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =r.FIN_YEAR_ID ");
            sb.append("where r.FIN_YEAR_ID >21 and r.FIN_YEAR_ID=" + finYearId + " order by r.EFFECTIVE_FROM ");
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            final Query lQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            SevarthIdList = lQuery.list();
        }
        catch (Exception e) {
            NsdlNpsFileEmpWiseReport.gLogger.error((Object)(" Error in getEmpDtls " + e), (Throwable)e);
            e.printStackTrace();
            throw e;
        }
        return SevarthIdList;
    }
    
    public List getOrderDetailsY(final String BillNO, final String Month, final String Year, final String deputation) {
        List lLstEmpFiveInst = null;
        StringBuilder lSBQuery = null;
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            lSBQuery = new StringBuilder();
            lSBQuery.append("\t select em.sevarth_id,'Employee Name : '||em.emp_name||' Sevaarth id : '||em.sevarth_id||' Dcps Id : '||em.dcps_id||' Pran No : '|| em.PRAN_NO||' Total Sixth Pay Arrear Amount : '||f.INST_AMOUNT  from TIERII_EMP_DETAILS f ");
            if(!deputation.equals("Y"))
            lSBQuery.append("\t join TIERTWO_BILL_DTLS bill on f.BILLL_ID=bill.BILL_ID join MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='" + BillNO + "' and bill.BILL_STATUS <>'-2' ");////$t28Dec2021
            else
            lSBQuery.append("\t join TIERTWO_BILL_DTLS bill on f.BILLL_ID=bill.BILL_ID join MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='" + BillNO + "' and bill.BILL_STATUS <>'-12' ");////$t28Dec2021
            
            final Query stQuery = (Query)ghibSession.createSQLQuery(lSBQuery.toString());
            lLstEmpFiveInst = stQuery.list();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lLstEmpFiveInst;
    }
    
    public List getOrderDetailsG(final String BillNO, final String Month, final String Year, final String deputation) {
        List lLstEmpFiveInst = null;
        StringBuilder lSBQuery = null;
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            lSBQuery = new StringBuilder();
            lSBQuery.append("\t select em.sevarth_id,'Employee Name : '||em.emp_name||' Sevaarth id : '||em.sevarth_id||' Dcps Id : '||em.dcps_id||' Pran No : '|| em.PRAN_NO||' Total Sixth Pay Arrear Amount : '||f.INST_AMOUNT  from TIERII_NAMUMNA_F_EMP_DETAILS f ");
            lSBQuery.append("\tjoin  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='" + BillNO + "' ");
            final Query stQuery = (Query)ghibSession.createSQLQuery(lSBQuery.toString());
            lLstEmpFiveInst = stQuery.list();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lLstEmpFiveInst;
    }
    
    public List getDetailsForG(final String BillId, String type, String deputation) {
        List lLstEmpFiveInst = null;
        StringBuilder sb = null;
        String check;
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            sb = new StringBuilder();
            if(deputation.equals("Y"))
            sb.append(" select status from TIERII_NAMUMNA_F_EMP_DETAILS where BILLL_ID='"+BillId+"' and status='12' ");
            else
            sb.append(" select status from TIERII_NAMUMNA_F_EMP_DETAILS where BILLL_ID='"+BillId+"' and status='2' ");
            
            Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            lLstEmpFiveInst = stQuery.list();
           
            sb = new StringBuilder();
            sb.append("\tselect em.emp_name,em.sevarth_id,em.dcps_id,em.pran_no,f.INST_AMOUNT,int_amount,f.INST_AMOUNT+int_amount  from TIERII_NAMUMNA_F_EMP_DETAILS f ");
            
            if(type.equals("700003")&& deputation.equals("Y")){
            	if(lLstEmpFiveInst.size()<=0)
            	sb.append("\t join  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='" + BillId + "' and f.status='10' ");
            	else
                sb.append("\t join  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='" + BillId + "' and f.status='12' ");
            }else if(type.equals("700003")){
                if(lLstEmpFiveInst.size()<=0)
                sb.append("\t join  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='" + BillId + "' and f.status='0' ");
                else
                sb.append("\t join  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='" + BillId + "' and f.status='2' ");
            }else{
            sb.append("\t join  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='" + BillId + "' ");
            }
            
            stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            lLstEmpFiveInst = stQuery.list();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lLstEmpFiveInst;
    }
    
    public List getPendingData(final String ddoCode,final String deputation) {
        List lLstEmpFiveInst = null;
        StringBuilder sb = null;
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            sb = new StringBuilder();
            sb.append("SELECT ");
            sb.append("diSTINCT t.DCPS_EMP_ID,empmst.EMP_NAME, ");
            sb.append("empmst.SEVARTH_ID, ");
            sb.append("empmst.DCPS_ID, ");
            sb.append("cast(t8.yearly_amount as varchar) as TimeA, ");
            sb.append("decode(t8.STATUS_FLAG,'D','DRAFT','A','APPROVE','G','in TO Login','R','Rejected','F','PENDING FOR APPROVAL','B','B'), ");
            sb.append("cast(t2.yearly_amount as varchar) as TimeB, ");
            sb.append("decode(t2.STATUS_FLAG,'D','DRAFT','A','APPROVE','G','in TO Login','R','Rejected','F','PENDING FOR APPROVAL','B','B'), ");
            sb.append("cast(t3.yearly_amount as varchar) as TimeC, ");
            sb.append("decode(t3.STATUS_FLAG,'D','DRAFT','A','APPROVE','G','in TO Login','R','Rejected','F','PENDING FOR APPROVAL','B','B'), ");
            sb.append("cast(t4.yearly_amount as varchar) as TimeD, ");
            sb.append("decode(t4.STATUS_FLAG,'D','DRAFT','A','APPROVE','G','in TO Login','R','Rejected','F','PENDING FOR APPROVAL','B','B'), ");
            sb.append("cast(t5.yearly_amount as varchar) as TimeF, ");
            sb.append("decode(t5.STATUS_FLAG,'D','DRAFT','A','APPROVE','G','in TO Login','R','Rejected','F','PENDING FOR APPROVAL','B','B'),t5.STATUS_FLAG ");
            sb.append("FROM RLT_DCPS_SIXPC_YEARLY t ");
            sb.append("JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID ");
            sb.append("AND t8.fin_year_id=22 ");
            sb.append("JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID ");
            sb.append("AND t2.fin_year_id=23 ");
            sb.append("JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID ");
            sb.append("AND t3.fin_year_id=24 ");
            sb.append("JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID ");
            sb.append("AND t4.fin_year_id=25 ");
            sb.append("JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID ");
            sb.append("AND t5.fin_year_id=26 ");
            sb.append("inner join MST_DCPS_EMP empmst on t.dcps_emp_id=empmst.dcps_emp_id ");
            if(deputation.equals("Y"))
            sb.append(" inner join TIERII_ATTACH_DETACH_DTLS attach on empmst.dcps_emp_id=attach.dcps_emp_id ");
            
            sb.append("where ");
            sb.append(" t.ddo_code='" + ddoCode + "' ");
            if(deputation.equals("Y"))
            sb.append("and (t.state_flag='10' or t.state_flag is null) ");
            else
            sb.append("and (t.state_flag='0' or t.state_flag is null) ");            	
            
            sb.append("ORDER BY t.DCPS_EMP_ID ASC ");
            final Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            lLstEmpFiveInst = stQuery.list();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lLstEmpFiveInst;
    }
    
    public List getDDODetails(final String DDOCode) {
        List lLstEmpFiveInst = null;
        StringBuilder sb = null;
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            sb = new StringBuilder();
            sb.append("select off.OFF_name,case when off.address1 is null then off.ADDRESS1_NEW||','|| off.ADDRESS2_NEW||','|| off.ADDRESS3_NEW else address1 end ,loc.loc_name ,design.DSGN_NAME ");
            sb.append("from MST_DCPS_DDO_OFFICE off ");
            sb.append("join cmn_location_mst loc on substr(loc.location_code,0,2)=substr(off.ddo_code,0,2) and loc.department_id=100003 ");
            sb.append("join ORG_DDO_MST ddo on ddo.ddo_code=off.ddo_code ");
            sb.append("join ORG_POST_DETAILS_RLT post on post.POST_ID=ddo.post_id ");
            sb.append("join ORG_DESIGNATION_MST design on post.DSGN_ID=design.DSGN_ID ");
            sb.append("where off.DDO_CODE = '" + DDOCode + "' and off.ddo_office='Yes' ");
            final Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            lLstEmpFiveInst = stQuery.list();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lLstEmpFiveInst;
    }
    
    public String getTotails(final Long userId) {
        final List lLstEmpFiveInst = null;
        String designation = "";
        StringBuilder sb = null;
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            sb = new StringBuilder();
            sb.append("select design.DSGN_NAME from ORG_USER_MST user ");
            sb.append("left join ORG_USERPOST_RLT post1 on post1.user_id=user.user_id ");
            sb.append("join ORG_POST_DETAILS_RLT post on post.POST_ID=post1.post_id ");
            sb.append("join ORG_DESIGNATION_MST design on post.DSGN_ID=design.DSGN_ID ");
            sb.append("where user.user_id ='" + userId + "' ");
            final Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            designation = stQuery.list().get(0).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return designation;
    }
    public String space(int noOfSpace) {
        String blank = "";
        for (int i = 0; i < noOfSpace; i++) {
            blank += "\u00a0";
        }
        return blank;
    }
    public String getLetterCreatedDate(final String orderId,String dcps_Emp_id, String from) throws Exception {
        /*StringBuilder sb = new StringBuilder();
        List lLstDate = null;
        String letterDate = null;
        try {
            sb = new StringBuilder();
            sb.append(" select to_char(CREATED_DATE,'dd/MM/yyyy') from TIERII_NAMUMNA_F_EMP_DETAILS where BILLL_ID = '"+orderId+"'  FETCH first ROW only ");
            Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            Query lQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            lLstDate = lQuery.list();
            if(lLstDate.size()>0){
            letterDate = (String) lQuery.list().get(0);
            }else{
            	sb = new StringBuilder();
                sb.append(" SELECT empT.dcps_id FROM TIERII_EMP_DETAILS empT join TIERTWO_BILL_DTLS bill on empT.BILLL_ID=bill.BILL_ID ");
                sb.append(" where empT.BILLL_ID = '"+orderId+"' and bill.BILL_STATUS <> '-2' FETCH first ROW only ");
                ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
                lQuery = (Query)ghibSession.createSQLQuery(sb.toString());
                letterDate = (String) lQuery.list().get(0);
                
                sb = new StringBuilder();
                sb.append(" select to_char(CREATED_DATE,'dd/MM/yyyy') from TIERII_NAMUMNA_F_EMP_DETAILS ");
                sb.append(" where BILLL_ID like '" + orderId.substring(0, 14) + "%' and DCPS_ID='"+letterDate+"' FETCH first ROW only ");
                ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
                lQuery = (Query)ghibSession.createSQLQuery(sb.toString());
                letterDate = (String) lQuery.list().get(0);
            }	
        }
        catch (Exception e) {
            NsdlNpsFileEmpWiseReport.gLogger.error((Object)(" Error in getEmpDtls " + e), (Throwable)e);
            e.printStackTrace();
            throw e;
        }
        return letterDate;*/
    	
    	StringBuilder sb = new StringBuilder();
        List lLstDate = null;
        String letterDate = null;
        Session ghibSession;
        Query lQuery;
        try {
            	if(from.length()<1){
            	sb = new StringBuilder();
                sb.append(" select to_char(CREATED_DATE,'dd/MM/yyyy') from TIERII_NAMUMNA_F_EMP_DETAILS where BILLL_ID = '"+orderId+"' and DCPS_ID='"+dcps_Emp_id+"' and STATUS <> '-1' order by CREATED_DATE desc FETCH first ROW only ");
                ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
                lQuery = (Query)ghibSession.createSQLQuery(sb.toString());
                lLstDate = lQuery.list();
                letterDate = (String) lQuery.list().get(0);	
            	}else{
            	sb = new StringBuilder();
                sb.append(" SELECT empT.dcps_id FROM TIERII_EMP_DETAILS empT join TIERTWO_BILL_DTLS bill on empT.BILLL_ID=bill.BILL_ID ");
                sb.append(" where empT.BILLL_ID = '"+orderId+"' and empT.dcps_id='"+dcps_Emp_id+"' and bill.BILL_STATUS <> '-2' order by CREATED_DATE desc FETCH first ROW only ");
                ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
                lQuery = (Query)ghibSession.createSQLQuery(sb.toString());
                letterDate = (String) lQuery.list().get(0);
                
                sb = new StringBuilder();
                sb.append(" select to_char(CREATED_DATE,'dd/MM/yyyy') from TIERII_NAMUMNA_F_EMP_DETAILS ");
                sb.append(" where BILLL_ID like '" + orderId.substring(0, 14) + "%' and DCPS_ID='"+letterDate+"' and STATUS <> '-1' order by CREATED_DATE desc FETCH first ROW only ");
                ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
                lQuery = (Query)ghibSession.createSQLQuery(sb.toString());
                letterDate = (String) lQuery.list().get(0);
            	}
        }
        catch (Exception e) {
            NsdlNpsFileEmpWiseReport.gLogger.error((Object)(" Error in getEmpDtls " + e), (Throwable)e);
            e.printStackTrace();
            throw e;
        }
        return letterDate;
    }
    
    private String  getHODDetails(Long lLngPostId) {
    	String lLstEmpFiveInst = null;
        StringBuilder sb = null;
        try {
            final Session ghibSession = (Session)ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
            sb = new StringBuilder();
            sb.append(" SELECT lna.HOD_DSGN FROM ORG_USERPOST_RLT user join ORG_POST_DETAILS_RLT post on user.USER_ID=post.POST_ID ");
            sb.append(" join CMN_LOCATION_MST cmn on post.LOC_ID=cmn.LOC_ID join ORG_USER_MST mst on user.user_id=mst.USER_ID join TIERII_HOD_PROFILE lna on user.POST_ID=lna.CREATED_POST_ID ");
            sb.append(" WHERE user.POST_ID='"+lLngPostId+"' and user.ACTIVATE_FLAG = 1 ");
            
            final Query stQuery = (Query)ghibSession.createSQLQuery(sb.toString());
            lLstEmpFiveInst = (String) stQuery.list().get(0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lLstEmpFiveInst;
	}
    
}//class
