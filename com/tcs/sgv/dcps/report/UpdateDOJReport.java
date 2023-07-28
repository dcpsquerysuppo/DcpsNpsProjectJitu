package com.tcs.sgv.dcps.report;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
import org.hibernate.SessionFactory;



import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
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

public class UpdateDOJReport extends DefaultReportDataFinder {

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
        	System.out.println("in try");
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

            String fromDate = null;
            String toDate = null;
            SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
           
            fromDate = report.getParameterValue("Datefrom").toString();
            toDate = report.getParameterValue("Dateto").toString();

            Date fDate = lObjDateFormat.parse(fromDate);
            Date tDate = lObjDateFormat.parse(toDate);

            System.out.println("fromDate*****" + fromDate);
            System.out.println("toDate*****" + toDate);
            
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
            String newFDate = null;
            String newTDate = null;

            try {

            	newFDate = myFormat.format(lObjDateFormat.parse(fromDate));
            	newTDate = myFormat.format(lObjDateFormat.parse(toDate));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("newFDate ########"+newFDate);
            System.out.println("newTDate ########"+newTDate);

            
            gLogger.info("fromDate*****" + fDate);
            gLogger.info("toDate*****" + tDate);
                 
                        
            String StrSqlQuery = "";

            if (report.getReportCode().equals("80000713")) {	// # REPORT ID 

                // report.setStyleList(noBorder);

                ArrayList rowList = new ArrayList();
           
                String SevarthId = ((String) report.getParameterValue("SevarthId")); //  # PARAMETER VALUE FROM JSP
              
                gLogger.info("SevarthId is "+SevarthId);
               
                DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
                String url = "";

                StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
                lObjStyleVO = report.getStyleList();
                for (Integer lInt = 0; lInt < report.getStyleList().length; lInt++) {
                    if (lObjStyleVO[lInt].getStyleId() == 26) {
                        lObjStyleVO[lInt].setStyleValue(url);
                    }
                }

                StringBuilder SBQueryQuery = new StringBuilder();
            	          	
                SBQueryQuery.append(" SELECT SEVARTH_ID, EMP_NAME,to_char(OLD_DOJ,'dd/MM/yyyy'),to_char(NEW_DOJ,'dd/MM/yyyy'),to_char(DOJ_UPDATION_DATE,'dd/MM/yyyy'),DOJ_UPDATION_REMARKS FROM IFMS.MST_DOJ_UPDATED_DTLS  where  to_char(DOJ_UPDATION_DATE,'yyyy-MM-dd') between '" +newFDate+"' and '"+newTDate+"' ");  // # QUERY WILL FILL DATA IN TABLE
                if (SevarthId != null && !"".equals(SevarthId)) {
                	SBQueryQuery.append(" AND UPPER(SEVARTH_ID) = '"+SevarthId+"' ");
        		}
                gLogger.info("StrSqlQuery***********"+SBQueryQuery.toString());

            //	String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=9000181&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
            String urlPrefix1 = "ifms.htm?actionFlag=reportService&reportCode=80000713&action=generateReport&DirectReport=TRUE";
                StrSqlQuery = SBQueryQuery.toString();
                rs = smt.executeQuery(StrSqlQuery);
                Integer counter = 1;
               Long lLongEmployeeAmt=0l;
               Long lLongEmployerAmt=0l;
               Long TotalAmt=0l;
               String amount=null;
                while (rs.next()) {

                    rowList = new ArrayList();

                    rowList.add(new StyledData(counter, CenterAlignVO));  					// # SERIAL NUMBER
                    
                    rowList.add(new StyledData(rs.getString(1), CenterAlignVO)); 			// # VALUE AFTER SERIAL NUMBER
                    rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
                    rowList.add(new StyledData(rs.getString(5), CenterAlignVO));                   
                    rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
                    
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
