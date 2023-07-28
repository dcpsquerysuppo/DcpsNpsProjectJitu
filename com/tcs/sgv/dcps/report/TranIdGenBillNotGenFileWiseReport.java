package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

public class TranIdGenBillNotGenFileWiseReport extends DefaultReportDataFinder {

    private static final Logger gLogger = Logger
    .getLogger(NsdlDdoWiseReport.class);
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
        CenterAlignVO[0]
                      .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
        CenterAlignVO[1] = new StyleVO();
        CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
        CenterAlignVO[1]
                      .setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

        StyleVO[] noBorder = new StyleVO[1];
        noBorder[0] = new StyleVO();
        noBorder[0].setStyleId(IReportConstants.BORDER);
        noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);


        StyleVO[] leftboldStyleVO  = new StyleVO[3];
        leftboldStyleVO[0] = new StyleVO();
        leftboldStyleVO[0]
                        .setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
        leftboldStyleVO[0]
                        .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_NORMAL);
        leftboldStyleVO[1] = new StyleVO();
        leftboldStyleVO[1]
                        .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
        leftboldStyleVO[1].setStyleValue("Left");
        leftboldStyleVO[2] = new StyleVO();
        leftboldStyleVO[2]
                        .setStyleId(IReportConstants.BORDER);
        leftboldStyleVO[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);



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

            String StrSqlQuery = "";

            if (report.getReportCode().equals("9000241")) {

                // report.setStyleList(noBorder);

                ArrayList rowList = new ArrayList();

                
                String year = ((String) report.getParameterValue("year"));
                String month = ((String) report.getParameterValue("month"));
                String treasury = ((String) report.getParameterValue("treasuryCode"));
                
               
                String lStrDDOCode = null;
                String lStrDDOName = "";

                //gLogger.info("$$$$$$$$$$$$pranWise"+pranWise);
                DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
                String url = "";


              url = "ifms.htm?actionFlag=reportService&reportCode=9000240&action=generateReport&DirectReport=TRUE&displayOK=TRUE&year="+year+"&month="+month;



                StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
                lObjStyleVO = report.getStyleList();
                for (Integer lInt = 0; lInt < report.getStyleList().length; lInt++) {
                    if (lObjStyleVO[lInt].getStyleId() == 26) {
                        lObjStyleVO[lInt].setStyleValue(url);
                    }
                }


                StringBuilder SBQueryQuery = new StringBuilder();

/*
                SBQueryQueryQuery.append(" SELECT emp.emp_name,emp.SEVARTH_ID,emp.DCPS_ID,emp.PRAN_NO,sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as employye, ");
                SBQueryQueryQuery.append(" sum(paybill.DED_ADJUST),bill.DESCRIPTION FROM PAYBILL_HEAD_MPG head ");
                SBQueryQueryQuery.append(" inner join HR_PAY_PAYBILL paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
                SBQueryQueryQuery.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
                SBQueryQueryQuery.append(" inner join HR_EIS_EMP_MST mst on mst.EMP_ID=paybill.EMP_ID ");
                SBQueryQueryQuery.append(" inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID=mst.EMP_MPG_ID ");
                SBQueryQueryQuery.append(" inner join MST_DCPS_BILL_GROUP bill on bill.BILL_GROUP_ID=head.BILL_NO ");
                SBQueryQueryQuery.append(" where head.PAYBILL_YEAR= '"+year+"' and head.PAYBILL_MONTH='"+month+"'  and ddo.ddo_code='"+ddoCode+"' and emp.reg_status=1 and emp.PRAN_NO is not null and emp.AC_DCPS_MAINTAINED_BY=700174 ");
                SBQueryQueryQuery.append(" and  head.APPROVE_FLAG=1  and emp.PRAN_no not in (SELECT sd.SD_PRAN_NO FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 and bh.YEAR=2015 and bh.MONTH=3) ");
                SBQueryQueryQuery.append(" group by  emp.emp_name,emp.SEVARTH_ID,emp.DCPS_ID,emp.PRAN_NO,bill.DESCRIPTION ");
*/
                /*SELECT loc.loc_id,loc.loc_name,dto.dto_reg_no,(SELECT nvl(count(bh.FILE_NAME),0) as Files_Generated FROM nsdl_bh_dtls bh
inner join CMN_LOCATION_MST loc on substr(loc.loc_id,1,2) = substr(bh.file_name,1,2) and loc.DEPARTMENT_ID=100003
where bh.status <> -1 and bh.file_status=0 and bh.month>3 and bh.month<8 
),(SELECT  nvl(count(bh.FILE_NAME),0) as Files_Validated_Transaction_Id_Null FROM nsdl_bh_dtls bh
inner join CMN_LOCATION_MST loc on substr(loc.loc_id,1,2) = substr(bh.file_name,1,2) and loc.DEPARTMENT_ID=100003
where bh.status <> -1 and bh.file_status=1 and bh.month>3 and bh.month<8 and bh.TRANSACTION_ID is null 
group by  loc.loc_name,loc.loc_id,bh.MONTH,bh.YEAR
) FROM nsdl_bh_Dtls bh
inner join cmn_location_mst loc on loc.loc_id=substr(bh.file_name,1,4)
inner join mst_dto_reg dto on dto.loc_id=loc.loc_id
                
                Query for tran id gen bill not gen report*/
                

                SBQueryQuery.append(" SELECT  loc.loc_id,loc.loc_name,dto.dto_reg_no,bh.file_name,bh.bh_total_amt,bh.transaction_id,decode(bh.file_status,0,'File not Validated',1,'File is validated',2,'File is rejected',3,'File has been modified by User',5,'File has been sent to NSDL',7,'Wrong_Transaction_Id',8,'Lapsed_Transaction_Id',11,'Transaction Id Updated') ");
                SBQueryQuery.append(" FROM nsdl_bh_dtls bh ");
                SBQueryQuery.append(" inner join CMN_LOCATION_MST loc on substr(loc.loc_id,1,2) = substr(bh.file_name,1,2) and loc.DEPARTMENT_ID=100003 ");
                SBQueryQuery.append(" inner join mst_dto_reg dto on dto.loc_id = loc.loc_id ");
                SBQueryQuery.append(" where bh.status <> -1 and bh.month="+month+" and bh.year="+year+" and loc.loc_id="+treasury+" ");
                SBQueryQuery.append(" group by loc.loc_id,loc.loc_name,dto.dto_reg_no,bh.file_name,bh.bh_total_amt,bh.transaction_id,bh.file_status ");


        		
                gLogger.info("StrSqlQuery***********"+SBQueryQuery.toString());

            //	String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=9000181&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
           // String urlPrefix1 = "ifms.htm?actionFlag=reportService&reportCode=9000241&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
                StrSqlQuery = SBQueryQuery.toString();
                rs = smt.executeQuery(StrSqlQuery);
                Integer counter = 1;
               Long lLongEmployeeAmt=0l;
               Long lLongEmployerAmt=0l;
               Long TotalAmt=0l;
               String amount=null;
                while (rs.next()) {

                    rowList = new ArrayList();

                 /*   lLongEmployeeAmt = Long.parseLong(rs.getString(4));
                    lLongEmployerAmt = Long.parseLong(rs.getString(5));
                    TotalAmt=lLongEmployeeAmt+lLongEmployerAmt;
                    amount=  TotalAmt.toString();*/
                    
                    rowList.add(new StyledData(counter, CenterAlignVO));
                  //  rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
                 /*   rowList.add(new URLData(rs.getString(1), urlPrefix+ "&ddoCode="+rs.getString(1)
                    				+"&year="+year+"&month="+month+"&pranWise="+pranWise
					)); */
                  //  rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
                  //  rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
                  //  rowList.add(new StyledData(rs.getString(8), CenterAlignVO));
                    
                 
                //  rowList.add(new URLData(rs.getString(8), urlPrefix1+"&year="+year+"&month="+month));
                    
                   // rowList.add(new StyledData(amount, CenterAlignVO));
                     dataList.add(rowList);

                    counter = counter + 1;
                }
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

    public String space(int noOfSpace) {
        String blank = "";
        for (int i = 0; i < noOfSpace; i++) {
            blank += "\u00a0";
        }
        return blank;
    }

}
