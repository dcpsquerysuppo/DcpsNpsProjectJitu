package com.tcs.sgv.dcps.report;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
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

public class DcpsEmpStatDetailReport extends DefaultReportDataFinder {
   private static final Logger gLogger = Logger.getLogger(NsdlDdoWiseReport.class);
   String Lang_Id = "en_US";
   String Loc_Id = "LC1";
   Map requestAttributes = null;
   SessionFactory lObjSessionFactory = null;
   ServiceLocator serviceLocator = null;

   public Collection findReportData(ReportVO report, Object criteria) throws ReportException {
      String locId = report.getLocId();
      Connection con = null;
      criteria.getClass();
      Statement smt = null;
      ResultSet rs = null;
      ArrayList dataList = new ArrayList();
      new SimpleDateFormat("dd/MM/yyyy");
      new SimpleDateFormat("yyyy-MM-dd");
      new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      StyleVO[] CenterAlignVO = new StyleVO[]{new StyleVO(), null};
      CenterAlignVO[0].setStyleId(40);
      CenterAlignVO[0].setStyleValue("center");
      CenterAlignVO[1] = new StyleVO();
      CenterAlignVO[1].setStyleId(1);
      CenterAlignVO[1].setStyleValue("none");
      StyleVO[] noBorder = new StyleVO[]{new StyleVO()};
      noBorder[0].setStyleId(1);
      noBorder[0].setStyleValue("none");
      StyleVO[] leftboldStyleVO = new StyleVO[]{new StyleVO(), null, null};
      leftboldStyleVO[0].setStyleId(37);
      leftboldStyleVO[0].setStyleValue("normal");
      leftboldStyleVO[1] = new StyleVO();
      leftboldStyleVO[1].setStyleId(40);
      leftboldStyleVO[1].setStyleValue("Left");
      leftboldStyleVO[2] = new StyleVO();
      leftboldStyleVO[2].setStyleId(1);
      leftboldStyleVO[2].setStyleValue("none");

      try {
         this.requestAttributes = (Map)((Map)criteria).get("REQUEST_ATTRIBUTES");
         this.serviceLocator = (ServiceLocator)this.requestAttributes.get("serviceLocator");
         this.lObjSessionFactory = this.serviceLocator.getSessionFactorySlave();
         Map requestAttributes = (Map)((Map)criteria).get("REQUEST_ATTRIBUTES");
         ServiceLocator serviceLocator = (ServiceLocator)requestAttributes.get("serviceLocator");
         ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/dcps/dcpsLabels");
         SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
         con = lObjSessionFactory.getCurrentSession().connection();
         smt = con.createStatement();
         Map sessionKeys = (Map)((Map)criteria).get("SESSION_KEYS");
         Map loginDetail = (HashMap)sessionKeys.get("loginDetailsMap");
         Long locationId = (Long)loginDetail.get("locationId");
         String StrSqlQuery = "";
         if (report.getReportCode().equals("9000293")) {
            new ArrayList();
            String treasuryCode = (String)report.getParameterValue("treasuryCode");
            String rptCd = (String)report.getParameterValue("rptCd");
            gLogger.info("$$$$$$$$$$$$treasuryCode" + treasuryCode);
            gLogger.info("$$$$$$$$$$$$rptCd" + rptCd);
            gLogger.info("$$$$$$$$$$$$locationId" + locationId);
            String lStrDDOCode = null;
            String lStrDDOName = "";
            new DcpsCommonDAOImpl((Class)null, serviceLocator.getSessionFactory());
            String url = "";
            if (Integer.parseInt(rptCd) >= 6 && Integer.parseInt(rptCd) <= 10) {
               url = "ifms.htm?actionFlag=reportService&reportCode=9000294&action=generateReport&rptCd=2";
            }

            if (Integer.parseInt(rptCd) >= 1 && Integer.parseInt(rptCd) <= 5) {
               url = "ifms.htm?actionFlag=reportService&reportCode=9000294&action=generateReport&rptCd=1";
            }

            StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
            lObjStyleVO = report.getStyleList();

            for(Integer lInt = 0; lInt < report.getStyleList().length; lInt = lInt + 1) {
               if (lObjStyleVO[lInt].getStyleId() == 26) {
                  lObjStyleVO[lInt].setStyleValue(url);
               }
            }

            StringBuilder SBQueryQuery = new StringBuilder();
            if (Integer.parseInt(rptCd) == 1) {
               gLogger.info("in########1");
               SBQueryQuery.append(" SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp ");
               SBQueryQuery.append(" left join cmn_location_mst loc on substr(emp.ddo_code,1,2)=substr(loc.loc_id,1,2) and loc.department_id=100003   ");
               SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
               SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id  ");
               SBQueryQuery.append(" left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by  ");
               SBQueryQuery.append(" where emp.reg_status=1 and emp.form_status=1  and emp.ddo_code <> '1111222222' and post.activate_flag=1 and loc.loc_id=" + treasuryCode + "  and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate ");
            } else if (Integer.parseInt(rptCd) == 2) {
               SBQueryQuery.append(" SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp   ");
               SBQueryQuery.append(" left join cmn_location_mst loc on substr(emp.ddo_code,1,2)=substr(loc.loc_id,1,2) and loc.department_id=100003   ");
               SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
               SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id  ");
               SBQueryQuery.append(" left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by  ");
               SBQueryQuery.append(" where emp.reg_status=1 and emp.ddo_code <> '1111222222' and post.activate_flag=1 and emp.doj <= '2015-03-31' and loc.loc_id=" + treasuryCode + " and emp.form_status=1 and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate ");
            } else if (Integer.parseInt(rptCd) == 3) {
               SBQueryQuery.append(" SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp ");
               SBQueryQuery.append(" left join cmn_location_mst loc on substr(emp.ddo_code,1,2)=substr(loc.loc_id,1,2) and loc.department_id=100003   ");
               SBQueryQuery.append("  left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
               SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id  ");
               SBQueryQuery.append(" left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by  ");
               SBQueryQuery.append("  where emp.reg_status=1  and emp.ddo_code <> '1111222222' and post.activate_flag=1 and emp.doj >= '2015-04-01' and loc.loc_id=" + treasuryCode + " and emp.form_status=1 and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate ");
            } else if (Integer.parseInt(rptCd) == 4) {
               SBQueryQuery.append(" SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp ");
               SBQueryQuery.append("  left join cmn_location_mst loc on substr(emp.ddo_code,1,2)=substr(loc.loc_id,1,2) and loc.department_id=100003   ");
               SBQueryQuery.append("  left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
               SBQueryQuery.append("  left join org_userpost_rlt post on post.user_id=org.user_id  ");
               SBQueryQuery.append("  left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by  ");
               SBQueryQuery.append("  where emp.reg_status=1   and emp.ddo_code <> '1111222222' and post.activate_flag=1 and emp.pran_no is not null and emp.doj <= '2015-03-31' and loc.loc_id=" + treasuryCode + " and emp.form_status=1 and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate ");
            } else if (Integer.parseInt(rptCd) == 5) {
               SBQueryQuery.append(" SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp");
               SBQueryQuery.append(" left join cmn_location_mst loc on substr(emp.ddo_code,1,2)=substr(loc.loc_id,1,2) and loc.department_id=100003  ");
               SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID ");
               SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id ");
               SBQueryQuery.append(" left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by ");
               SBQueryQuery.append(" where emp.reg_status=1   and emp.ddo_code <> '1111222222' and post.activate_flag=1 and emp.pran_no is not null and emp.doj >= '2015-04-01' and loc.loc_id=" + treasuryCode + " and emp.form_status=1 and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate");
            } else if (Integer.parseInt(rptCd) == 6) {
               SBQueryQuery.append(" SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp ");
               SBQueryQuery.append(" left join cmn_location_mst loc on substr(emp.ddo_code,1,4)=substr(loc.loc_id,1,4) ");
               SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
               SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id  ");
               SBQueryQuery.append(" left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by  ");
               SBQueryQuery.append("  where emp.reg_status=1  and emp.form_status=1 and emp.ddo_code <> '1111222222' and post.activate_flag=1 and loc.loc_id=" + treasuryCode + " and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate ");
            } else if (Integer.parseInt(rptCd) == 7) {
               gLogger.info("in########7");
               SBQueryQuery.append(" SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp ");
               SBQueryQuery.append(" left join cmn_location_mst loc on substr(emp.ddo_code,1,4)=substr(loc.loc_id,1,4) ");
               SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
               SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id  ");
               SBQueryQuery.append(" left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by  ");
               SBQueryQuery.append(" where emp.reg_status=1    and emp.form_status=1 and emp.ddo_code <> '1111222222' and post.activate_flag=1 and emp.doj <= '2015-03-31' and loc.loc_id=" + treasuryCode + " and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate ");
            } else if (Integer.parseInt(rptCd) == 8) {
               SBQueryQuery.append("  SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp ");
               SBQueryQuery.append(" left join cmn_location_mst loc on substr(emp.ddo_code,1,4)=substr(loc.loc_id,1,4)  ");
               SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
               SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id  ");
               SBQueryQuery.append(" left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by  ");
               SBQueryQuery.append(" where emp.reg_status=1  and emp.form_status=1 and emp.ddo_code <> '1111222222' and post.activate_flag=1 and emp.doj >= '2015-04-01' and loc.loc_id=" + treasuryCode + " and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate ");
            } else if (Integer.parseInt(rptCd) == 9) {
               SBQueryQuery.append(" SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp ");
               SBQueryQuery.append(" left join cmn_location_mst loc on substr(emp.ddo_code,1,4)=substr(loc.loc_id,1,4)  ");
               SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
               SBQueryQuery.append("  left join org_userpost_rlt post on post.user_id=org.user_id  ");
               SBQueryQuery.append("  left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by  ");
               SBQueryQuery.append("   where emp.reg_status=1  and emp.form_status=1 and emp.ddo_code <> '1111222222' and post.activate_flag=1 and emp.pran_no is not null and emp.doj <= '2015-03-31' and loc.loc_id=" + treasuryCode + " and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate ");
            } else if (Integer.parseInt(rptCd) == 10) {
               SBQueryQuery.append(" SELECT emp.emp_name,nvl(emp.dcps_id,' '),emp.sevarth_id,loc.loc_name,loc.loc_id,emp.ddo_code,nvl(emp.ppan,' '),nvl(emp.pran_no,' '),to_char(emp.dob,'dd-MM-YYYY'),to_char(emp.doj,'dd-MM-YYYY'),look.lookup_name FROM mst_dcps_emp emp ");
               SBQueryQuery.append(" left join cmn_location_mst loc on substr(emp.ddo_code,1,4)=substr(loc.loc_id,1,4) ");
               SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
               SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id  ");
               SBQueryQuery.append(" left join cmn_lookup_mst look on look.lookup_id=emp.ac_dcps_maintained_by  ");
               SBQueryQuery.append(" where emp.reg_status=1  and emp.form_status=1 and emp.ddo_code <> '1111222222' and post.activate_flag=1 and emp.pran_no is not null and emp.doj >= '2015-04-01' and loc.loc_id=" + treasuryCode + " and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate ");
            }

            gLogger.info("StrSqlQuery***********" + SBQueryQuery.toString());
            StrSqlQuery = SBQueryQuery.toString();
            rs = smt.executeQuery(StrSqlQuery);
            Integer counter = 1;
            Long lLongEmployeeAmt = 0L;
            Long lLongEmployerAmt = 0L;
            Long TotalAmt = 0L;

            for(Object var35 = null; rs.next(); counter = counter + 1) {
               ArrayList rowList = new ArrayList();
               rowList.add(new StyledData(counter, CenterAlignVO));
               rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(8), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(9), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(10), CenterAlignVO));
               rowList.add(new StyledData(rs.getString(11), CenterAlignVO));
               dataList.add(rowList);
            }
         }
      } catch (Exception var44) {
         var44.printStackTrace();
         gLogger.error("Exception :" + var44, var44);
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
         } catch (Exception var43) {
            var43.printStackTrace();
            gLogger.error("Exception :" + var43, var43);
         }

      }

      return dataList;
   }

   public String space(int noOfSpace) {
      String blank = "";

      for(int i = 0; i < noOfSpace; ++i) {
         blank = blank + " ";
      }

      return blank;
   }
}