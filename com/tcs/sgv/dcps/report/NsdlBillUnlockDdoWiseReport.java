

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
import org.hibernate.Query;
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
import com.tcs.sgv.common.valuebeans.reports.URLData;

public class NsdlBillUnlockDdoWiseReport extends DefaultReportDataFinder {

    private static final Logger gLogger = Logger
    .getLogger(NsdlBillUnlockDdoWiseReport.class);
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

            if (report.getReportCode().equals("9000183")) {

                // report.setStyleList(noBorder);

                ArrayList rowList = new ArrayList();

                Long locID = Long.valueOf((String) report.getParameterValue("treasuryCode"));
                String year = ((String) report.getParameterValue("year"));
                String month = ((String) report.getParameterValue("month"));

                String lStrDDOCode = null;
                String lStrDDOName = "";


                DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
                String url = "";


                url = "ifms.htm?actionFlag=NsdlBillUnlock&finYear="+year+"&month="+month;



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
                
            	SBQueryQuery.append(" SELECT ddo.DDO_CODE,reg.ddo_reg_no,count(distinct hreis.emp_id),sum(paybill.DED_ADJUST),   ");
        		SBQueryQuery.append(" sum(paybill.DED_ADJUST),count(distinct head.PAYBILL_ID),sum(paybill.NET_TOTAL) FROM PAYBILL_HEAD_MPG head   inner join HR_PAY_PAYBILL paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID   ");
        		SBQueryQuery.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  ");
        		SBQueryQuery.append(" inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
        		SBQueryQuery.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  ");
        		SBQueryQuery.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
        		SBQueryQuery.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  ");
           		SBQueryQuery.append(" where head.PAYBILL_YEAR="+year+" and head.PAYBILL_MONTH="+month+"  and substr(ddo.ddo_code,1,4)='"+locID+"' and head.APPROVE_FLAG=5 ");
        		//SBQueryQuery.append("  and mstemp.DCPS_OR_GPF='Y'  and mstemp.AC_DCPS_MAINTAINED_BY=700174 ");
           		SBQueryQuery.append("  group by ddo.DDO_CODE,reg.ddo_reg_no having sum(paybill.DED_ADJUST) >0 order by reg.ddo_reg_no ");

        		
                gLogger.info("StrSqlQuery***********"+SBQueryQuery.toString());

            	String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=9000184&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
            	
                StrSqlQuery = SBQueryQuery.toString();
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
                  /* rowList.add(new StyledData(rs.getString(1), CenterAlignVO));*/
                   rowList.add(new URLData(rs.getString(1), urlPrefix+ "&ddoCode="+rs.getString(1)
                    				+"&year="+year+"&month="+month
					));
                    rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
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
