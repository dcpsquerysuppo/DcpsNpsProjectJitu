package com.tcs.sgv.dcps.report;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import java.util.ResourceBundle;

public class NsdlEmpeeWiseReport extends DefaultReportDataFinder {

    private static final Logger gLogger = Logger
    .getLogger(NsdlEmpeeWiseReport.class);
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

            if (report.getReportCode().equals("9000181")) {

                // report.setStyleList(noBorder);

                ArrayList rowList = new ArrayList();

                Long ddoCode = Long.valueOf((String) report.getParameterValue("ddoCode"));
                String year = ((String) report.getParameterValue("year"));
                String month = ((String) report.getParameterValue("month"));
                String pranWise = ((String) report.getParameterValue("pranWise"));
                String locID=ddoCode.toString().substring(1,4);
                
                String lStrDDOCode = null;
                String lStrDDOName = "";


                DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
                String url = "";


               
                StringBuilder SBQuery = new StringBuilder();


                SBQuery.append(" SELECT emp.emp_name,emp.DCPS_ID,emp.PRAN_NO,sum(paybill.DED_ADJUST)  as employye, ");//sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA) as employye
                SBQuery.append(" sum(paybill.DED_ADJUST) FROM PAYBILL_HEAD_MPG head ");
                SBQuery.append(" inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
                SBQuery.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
                SBQuery.append(" inner join HR_EIS_EMP_MST mst on mst.EMP_ID=paybill.EMP_ID ");
                SBQuery.append(" inner join mst_dcps_emp emp on emp.ORG_EMP_MST_ID=mst.EMP_MPG_ID ");
                SBQuery.append(" inner join MST_DCPS_BILL_GROUP bill on bill.BILL_GROUP_ID=head.BILL_NO ");
               // SBQuery.append(" left outer join NSDL_BH_DTLS nsdl on substr(nsdl.file_name,1,4)=substr(ddo.ddo_code,1,4) and nsdl.TRANSACTION_ID is null and nsdl.year= "+year+" and nsdl.MONTH= "+month+"  ");
             
                SBQuery.append(" where head.PAYBILL_YEAR= '"+year+"' and head.PAYBILL_MONTH='"+month+"'  and ddo.ddo_code='"+ddoCode+"' and emp.reg_status=1 ");
                if(pranWise.equals("true1")){
                    SBQuery.append(" and emp.PRAN_NO not in (SELECT sd.SD_PRAN_NO FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.TRANSACTION_ID is not null and bh.STATUS <>-1 and bh.YEAR = "+year+" and bh.MONTH="+month+" and bh.file_name like '"+locID+"%')");
                    
                    SBQuery.append(" and emp.PRAN_NO is not null  ");
                    }else{
                    	SBQuery.append(" and ((emp.PRAN_NO not in (SELECT sd.SD_PRAN_NO FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.TRANSACTION_ID is not null and bh.STATUS <>-1 and bh.YEAR = "+year+" and bh.MONTH="+month+" and bh.file_name like '"+locID+"%')) or (emp.PRAN_NO is null))");	
                    }
                
                SBQuery.append(" and emp.AC_DCPS_MAINTAINED_BY=700174 and  head.APPROVE_FLAG=1 ");
                SBQuery.append(" group by  emp.emp_name,emp.SEVARTH_ID,emp.DCPS_ID,emp.PRAN_NO,bill.DESCRIPTION ");


                gLogger.info("StrSqlQuery***********"+SBQuery.toString());

                StrSqlQuery = SBQuery.toString();
                rs = smt.executeQuery(StrSqlQuery);
                Integer counter = 1;
               Long lLongEmployeeAmt=0l;
               Long lLongEmployerAmt=0l;
               Long TotalAmt=0l;
               String amount=null;
                while (rs.next()) {

                    rowList = new ArrayList();

                    lLongEmployeeAmt = Long.parseLong(rs.getString(4));
                    lLongEmployerAmt = Long.parseLong(rs.getString(5));
                    TotalAmt=lLongEmployeeAmt+lLongEmployerAmt;
                    amount=  TotalAmt.toString();
                    
                    rowList.add(new StyledData(counter, CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
                    rowList.add(new StyledData(amount, CenterAlignVO));
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
