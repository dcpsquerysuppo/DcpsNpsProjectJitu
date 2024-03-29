package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

public class DcpsEmpStatReport extends DefaultReportDataFinder {

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
            
            
            
        	Map lMapRequestAttributes = null;
			Map lMapSessionAttributes = null;
			List lArrReportData = null;
			LoginDetails lObjLoginVO = null;
			String gStrLocCode = null;
			Long gLngLangId = null;
			Long gLngPostId = null;

			lMapRequestAttributes = (Map) ((Map) criteria)
			.get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria)
			.get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes
			.get("loginDetails");
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			gLngPostId = lObjLoginVO.getLoggedInPost().getPostId();
			
            String StrSqlQuery = "";

            
            
            DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			//gLogger.info("###########date"+dateFormat1);
			//	String additionalHeader = "<i><font size=\"1px\"> dated";
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date date = new Date();
				String curDate = dateFormat.format(date);

				String header1 = "<center><B><font size=\"3px\"> Dated :" + space(1) +  curDate+"</font></b></center>";


				/*String additionalHeader = newline + header1;
				report.setAdditionalHeader(additionalHeader);
				report.setStyleList(noBorder);

				String header11 = "";
				String additionalHeader11 = header1 + newline + header11;
				report.setAdditionalHeader(additionalHeader11);
				report.setStyleList(LeftAlign);*/

				report.setAdditionalHeader(header1);

			gLogger.info("date"+header1);
			
			
			
			
            if (report.getReportCode().equals("9000294")) {

                // report.setStyleList(noBorder);

                ArrayList rowList = new ArrayList();
                //setSessionInfo(inputMap);
               
               // String treasuryCode = ((String) report.getParameterValue("treasuryCode"));
               String rptCd = ((String) report.getParameterValue("rptCd"));
                //String month = ((String) report.getParameterValue("month"));
               
              //  gLogger.info("$$$$$$$$$$$$treasuryCode"+treasuryCode);
                gLogger.info("$$$$$$$$$$$$rptCd"+rptCd);
                gLogger.info("$$$$$$$$$$$$locationId"+locationId);
                gLogger.info("#############gStrLocCode"+gStrLocCode);
                gLogger.info("#############gLngPostId"+gLngPostId);
                
                String lStrDDOCode = null;
                String lStrDDOName = "";

                
                DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
                String url = "";

             //   if(Integer.parseInt(rptCd) >= 6 && Integer.parseInt(rptCd) <= 10){
               // 	url = "ifms.htm?actionFlag=getDataOnLoadForTo";
               // }
               // if(Integer.parseInt(rptCd) >= 1 && Integer.parseInt(rptCd) <= 5){
               //     url = "ifms.htm?actionFlag=getDataOnLoad";
               // }



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
                
                
                
             /*   SBQueryQuery.append(" SELECT loc.loc_id,loc.loc_name,dto.dto_reg_no,count(case when bh.file_status=0 then 1 else null end) as  Gen_file_count,  ");
                SBQueryQuery.append(" count( case when bh.file_status=1 and bh.transaction_id is null then 1 else null end), ");
                SBQueryQuery.append(" count( case when bh.TRANSACTION_ID is not null and nsdl.BILL_STATUS=-1 then 1 else null end) as  tran_id_gen_bill_not_gen,  ");
                SBQueryQuery.append(" count( case when bh.TRANSACTION_ID is not null  then 1 else null end) as  tran_mtr45_file_count,  ");
                SBQueryQuery.append(" count( case when nsdl.BILL_STATUS=1 then 1 else null end) as approve_bill,  ");
                SBQueryQuery.append(" count(case when bh.file_status=0 then 1 else null end) +  ");
                SBQueryQuery.append(" count( case when bh.file_status=1 and bh.transaction_id is null then 1 else null end) +  ");
                SBQueryQuery.append(" count( case when bh.TRANSACTION_ID is not null and nsdl.BILL_STATUS=-1 then 1 else null end) +  ");
                SBQueryQuery.append(" count( case when bh.TRANSACTION_ID is not null  then 1 else null end) + ");
                SBQueryQuery.append(" count( case when nsdl.BILL_STATUS=1 then 1 else null end) as total FROM nsdl_bh_dtls bh  ");
                SBQueryQuery.append(" inner join nsdl_bill_dtls nsdl on nsdl.file_name=bh.file_name ");
                SBQueryQuery.append(" inner join CMN_LOCATION_MST loc on substr(loc.loc_id,1,2) = substr(bh.file_name,1,2) and loc.DEPARTMENT_ID=100003  ");
                SBQueryQuery.append(" inner join mst_dto_reg dto on dto.loc_id = loc.loc_id  ");
                SBQueryQuery.append(" inner join ORG_DDO_MST ddo on substr(ddo.DDO_CODE,1,4)=substr(loc.LOC_ID,1,4)  ");
           // --    inner join PAYBILL_HEAD_MPG head on head.LOC_ID=ddo.LOCATION_CODE 
            // --   inner join HR_PAY_PAYBILL paybill on paybill.PAYBILL_GRP_ID=head.PAYBILL_ID 
                SBQueryQuery.append(" where bh.status <> -1 ");//-- and head.APPROVE_FLAG = 1 and head.paybill_month="+month+" and head.paybill_year="+year+"  
            	SBQueryQuery.append(" and ddo.DDO_CODE <> '1111222222'  ");
            	SBQueryQuery.append(" and bh.month="+month+" and bh.year="+year+" ");
            	SBQueryQuery.append(" group by loc.loc_name,loc.loc_id,dto.dto_reg_no  ");
            	SBQueryQuery.append(" order by loc.loc_id ");*/
                
                if(Integer.parseInt(rptCd)==1){
                	
                	 SBQueryQuery.append(" select loc.loc_id,loc.loc_name,count(DCPS_EMP_ID) as total_dcps_emp, count(case when emp.doj <= '2015-03-31' then 1 else null end) as doj_lt_2015_3_31, count(case when emp.doj >= '2015-04-01' then 1 else null end) as doj_gt_2015_4_1,count(case when emp.doj <= '2015-03-31' and emp.pran_no is not null then 1 else null end) as doj_lt_2015_3_31_and_pran_not_null,  count(case when emp.doj >= '2015-04-01' and emp.pran_no is not null then 1 else null end) as doj_gt_2015_4_1_and_pran_not_null from mst_dcps_emp emp "); 
                	 SBQueryQuery.append(" left join cmn_location_mst loc on substr(loc.loc_id,1,2) = substr(emp.ddo_code,1,2) and loc.department_id=100003 ");
                	// SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID ");
                	// SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id and post.activate_flag=1 ");
                	 SBQueryQuery.append(" where emp.reg_status=1 and emp.dcps_or_gpf='Y' and emp.form_status=1 and emp.emp_servend_dt > sysdate  and emp.ddo_code <> '1111222222' ");
                	 SBQueryQuery.append(" group by loc.loc_id,loc.loc_name ");
                	 SBQueryQuery.append(" order by loc.loc_id ");

                }
                if(Integer.parseInt(rptCd)==2){
                	String loc_id=locationId.toString().substring(0,2);
                	gLogger.info("loc_id##### "+loc_id);
                	

                	SBQueryQuery.append(" select loc.loc_id,loc.loc_name,count(DCPS_EMP_ID) as total_dcps_emp, count(case when emp.doj <= '2015-03-31' then 1 else null end) as doj_lt_2015_3_31, count(case when emp.doj >= '2015-04-01' then 1 else null end) as doj_gt_2015_4_1,count(case when emp.doj <= '2015-03-31' and emp.pran_no is not null then 1 else null end) as doj_lt_2015_3_31_and_pran_not_null, count(case when emp.doj >= '2015-04-01' and emp.pran_no is not null then 1 else null end) as doj_gt_2015_4_1_and_pran_not_null from mst_dcps_emp emp "); 
                	SBQueryQuery.append(" left join cmn_location_mst loc on substr(loc.loc_id,1,4) = substr(emp.ddo_code,1,4)  ");
                //	SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID left join org_userpost_rlt post on post.user_id=org.user_id and post.activate_flag=1  ");
                	SBQueryQuery.append(" where emp.reg_status=1  and emp.ddo_code <> '1111222222' and loc.loc_id like '"+loc_id+"%' and emp.form_status=1 and emp.dcps_or_gpf='Y' and emp.emp_servend_dt > sysdate "); 
                	SBQueryQuery.append(" group by loc.loc_id,loc.loc_name  ");
                	SBQueryQuery.append(" order by loc.loc_id ");

                }
      
        		
                gLogger.info("StrSqlQuery***********"+SBQueryQuery.toString());

            //	String urlPrefix = "ifms.htm?actionFlag=reportService&rptCd=9000181&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
            String urlPrefix1 = "ifms.htm?actionFlag=reportService&reportCode=9000293&action=generateReport";
                StrSqlQuery = SBQueryQuery.toString();
                rs = smt.executeQuery(StrSqlQuery);
                Integer counter = 1;
               Long lLongEmployeeAmt=0l;
               Long lLongEmployerAmt=0l;
               Long TotalAmt=0l;
               String amount=null;
                while (rs.next()) {

                    rowList = new ArrayList();

           
                    rowList.add(new StyledData(counter, CenterAlignVO));
                
                    
                    rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
                    if(Integer.parseInt(rptCd)==1){
                    	
                    
                    		rowList.add(new URLData(rs.getString(3), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=1"));
            
                    		rowList.add(new URLData(rs.getString(4), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=2"));
                   
                    		rowList.add(new URLData(rs.getString(5), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=3"));
                  
                    		rowList.add(new URLData(rs.getString(6), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=4"));
                   
                    		rowList.add(new URLData(rs.getString(7), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=5"));
                  
                    }else if(Integer.parseInt(rptCd)==2){
                    	
                    	   rowList.add(new URLData(rs.getString(3), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=6"));
                           
                           rowList.add(new URLData(rs.getString(4), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=7"));
                          
                           rowList.add(new URLData(rs.getString(5), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=8"));
                         
                           rowList.add(new URLData(rs.getString(6), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=9"));
                          
                           rowList.add(new URLData(rs.getString(7), urlPrefix1+"&treasuryCode="+rs.getString(1)+"&rptCd=10"));
                         
                    }
                  
                    
                    
                 
                 // rowList.add(new URLData(rs.getString(9), urlPrefix1+"&year="+year+"&month="+month+"&treasuryCode="+loc));
                    
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
