package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.ibm.db2.jcc.a.e;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.DefaultComboItem;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.reports.dao.ReportParameterDAO;

public class ProcessContriforTreasury extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger
			.getLogger(ProcessContriforTreasury.class);
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

		StyleVO[] leftboldStyleVO = new StyleVO[3];
		leftboldStyleVO[0] = new StyleVO();
		leftboldStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		leftboldStyleVO[0]
				.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_NORMAL);
		leftboldStyleVO[1] = new StyleVO();
		leftboldStyleVO[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		leftboldStyleVO[1].setStyleValue("Left");
		leftboldStyleVO[2] = new StyleVO();
		leftboldStyleVO[2].setStyleId(IReportConstants.BORDER);
		leftboldStyleVO[2]
				.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		LocalDate currentdate = LocalDate.now();
		// Month currentMonth = currentdate.getMonth();
		int year = 0; //currentdate.getYear();
		int currentMonthNumber =0; //LocalDate.now().getMonthValue();

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

			if (report.getReportCode().equals("800009621")) {
//			if (report.getReportCode().equals("800009568")) {
			
				// report.setStyleList(noBorder);
				
				
			//	String treasuryCode = (String) report.getParameterValue("treasuryCode");
				String monthNPS = (String) report.getParameterValue("monthId");
				String yearNPS = (String) report.getParameterValue("Year");
				String IsDeputation = (String) report.getParameterValue("IsDeputation");
				year =Integer.parseInt(yearNPS);
				currentMonthNumber =Integer.parseInt(monthNPS);
				
				int noDays;
				
				Calendar cal = Calendar.getInstance();
			    System.out.println("Year = " + cal.get(Calendar.YEAR));
			    System.out.println("Month = " + (cal.get(Calendar.MONTH) + 1));
			    System.out.println("Date = " + cal.get(Calendar.DATE));
			    int sysYear=cal.get(Calendar.YEAR);
			    int sysMonth=(cal.get(Calendar.MONTH) + 1);
			    int sysDay=cal.get(Calendar.DATE);
			    
				if(year==sysYear && currentMonthNumber==sysMonth){
					noDays=sysDay;
				}else{
				////start/get count of days in month
				if((currentMonthNumber==2) && ((year%4==0) || ((year%100==0)&&(year%400==0))))
					noDays=29;

		        else if(currentMonthNumber==2)
		        	noDays=28;

		        else if(currentMonthNumber==1 || currentMonthNumber==3 || currentMonthNumber==5 || currentMonthNumber==7 || currentMonthNumber==8 || currentMonthNumber==10 || currentMonthNumber==12)
		        	noDays=31;

		        else
		        	noDays=30;
				}
			////end/get count of days in month

				ArrayList rowList = new ArrayList();
				gLogger.info("NPS process file");
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
						serviceLocator.getSessionFactory());
				String url = "";

				// url =
				// "ifms.htm?actionFlag=NsdlReport&finYear="+year+"&month="+month+"&pranWise="+pranWise;
				String urlPrefix1 = "ifms.htm?actionFlag=reportService&reportCode=800009622&action=generateReport&IsDeputation="+IsDeputation;
//				String urlPrefix1 = "ifms.htm?actionFlag=reportService&reportCode=800009571&action=generateReport&IsDeputation="+IsDeputation;
				//report.setAdditionalHeader("<b> MONTH : " + currentMonthNumber+" \n YEAR : " + year+ "</b>");
				StringBuilder sb = new StringBuilder();
				sb.append("select temp.location,temp.LOC_ID,sum(temp.count_1),sum(temp.count_2), ");
				sb.append("sum(temp.count_3),sum(temp.count_4), ");
				sb.append("sum(temp.count_5),sum(temp.count_6), ");
				sb.append("sum(temp.count_7),sum(temp.count_8), ");
				sb.append("sum(temp.count_9),sum(temp.count_10), ");
				sb.append("sum(temp.count_11),sum(temp.count_12), ");
				sb.append("sum(temp.count_13),sum(temp.count_14), ");
				sb.append("sum(temp.count_15),sum(temp.count_16), ");
				sb.append("sum(temp.count_17),sum(temp.count_18), ");
				sb.append("sum(temp.count_19),sum(temp.count_20), ");
				sb.append("sum(temp.count_21),sum(temp.count_22), ");
				sb.append("sum(temp.count_23),sum(temp.count_24), ");
				sb.append("sum(temp.count_25),sum(temp.count_26), ");
				sb.append("sum(temp.count_27),sum(temp.count_28), ");
				sb.append("sum(temp.count_29),sum(temp.count_30), ");
				sb.append("sum(temp.count_31) ");
				sb.append("FROM (SELECT cm.LOC_SHORT_NAME as location,cm.LOC_ID as LOC_ID, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=1 then  count(*) else 0 end  as count_1, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=2 then  count(*) else 0 end  as count_2, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=3 then  count(*) else 0 end  as count_3, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=4 then  count(*) else 0 end  as count_4, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=5 then  count(*) else 0 end  as count_5, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=6 then  count(*) else 0 end  as count_6, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=7 then  count(*) else 0 end  as count_7, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=8 then  count(*) else 0 end  as count_8, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=9 then  count(*) else 0 end  as count_9, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=10 then  count(*) else 0 end  as count_10, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=11 then  count(*) else 0 end  as count_11, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=12 then  count(*) else 0 end  as count_12, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=13 then  count(*) else 0 end  as count_13, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=14 then  count(*) else 0 end  as count_14, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=15 then  count(*) else 0 end  as count_15, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=16 then  count(*) else 0 end  as count_16, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=17 then  count(*) else 0 end  as count_17, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=18 then  count(*) else 0 end  as count_18, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=19 then  count(*) else 0 end  as count_19, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=20 then  count(*) else 0 end  as count_20, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=21 then  count(*) else 0 end  as count_21, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=22 then  count(*) else 0 end  as count_22, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=23 then  count(*) else 0 end  as count_23, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=24 then  count(*) else 0 end  as count_24, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=25 then  count(*) else 0 end  as count_25, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=26 then  count(*) else 0 end  as count_26, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=27 then  count(*) else 0 end  as count_27, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=28 then  count(*) else 0 end  as count_28, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=29 then  count(*) else 0 end  as count_29, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=30 then  count(*) else 0 end  as count_30, ");
				sb.append("case when substr(bh.BH_DATE,0,2)>0 and substr(bh.BH_DATE,0,2)=31 then  count(*) else 0 end  as count_31 ");
				sb.append("FROM NSDL_BH_DTLS bh INNER JOIN CMN_LOCATION_MST cm on cm.LOC_ID=substr(bh.FILE_NAME,0,4) ");
			    sb.append("WHERE bh.STATUS<>-1 AND substr(bh.BH_DATE,5,4)="+year+" and substr(bh.BH_DATE,3,2)="+currentMonthNumber+" and cm.DEPARTMENT_ID=100003 and LOC_ID<>1111 ");
			    if(IsDeputation.equals("Deputation"))
			    
			    sb.append(" and bh.FILE_NAME like'%D'");
			    if(IsDeputation.equals("Regular"))
			    sb.append(" and bh.FILE_NAME not like'%D'");
				//sb.append("WHERE bh.STATUS<>-1 AND substr(bh.BH_DATE,5,4)=2022 and substr(bh.BH_DATE,3,2)=3 and cm.DEPARTMENT_ID=100003 and LOC_ID<>1111 ");
				sb.append(" GROUP BY cm.LOC_SHORT_NAME,cm.LOC_ID,substr(bh.BH_DATE,0,2)  ) as temp group by temp.location,temp.LOC_ID  ORDER BY temp.LOC_ID ");

				gLogger.info("StrSqlQuery***********" + sb.toString());

				
				StrSqlQuery = sb.toString();
				rs = smt.executeQuery(StrSqlQuery);
				int sizeOfrecord = rs.getFetchSize();

				System.out.println(+sizeOfrecord);
				
				Long lLongEmployeeAmt = 0l;
				Long lLongEmployerAmt = 0l;
				Long TotalAmt = 0l;
				String amount = null;
				
				
				
			
				ReportColumnVO[] newReportColumns = new ReportColumnVO[noDays+2];
				
				newReportColumns[0] = new ReportColumnVO();
				newReportColumns[0].setColumnId(1); 
				newReportColumns[0].setColumnHeader("Name of the Treasury");
				newReportColumns[0].setDataType(2);
				newReportColumns[0].setAlignment(2); 
				newReportColumns[0].setTableName("a");
				newReportColumns[0].setColumnName("Name of the Treasury"); 
				newReportColumns[0].setDisplayTotal(0); 
				newReportColumns[0].setColumnLevel(1);
				report.addReportColumnItem(newReportColumns[0]);
				
				newReportColumns[1] = new ReportColumnVO();
				newReportColumns[1].setColumnId(2); 
				newReportColumns[1].setColumnHeader("Treasury Code");
				newReportColumns[1].setDataType(1);
				newReportColumns[1].setAlignment(2); 
				newReportColumns[1].setTableName("a");
				newReportColumns[1].setColumnName("Treasury Code"); 
				newReportColumns[1].setDisplayTotal(0); 
				newReportColumns[1].setColumnLevel(1);
				report.addReportColumnItem(newReportColumns[1]);
				for(int i=2;i<noDays+2;i++)
				{
				String colName=String.valueOf(i-1);	
				newReportColumns[i] = new ReportColumnVO();
				newReportColumns[i].setColumnId(i+3); 
				newReportColumns[i].setColumnHeader(colName);
				newReportColumns[i].setDataType(1);
				newReportColumns[i].setAlignment(2); 
				newReportColumns[i].setTableName("a");
				newReportColumns[i].setColumnName(colName); 
				newReportColumns[i].setDisplayTotal(1); 
				newReportColumns[i].setColumnLevel(1);
				report.addReportColumnItem(newReportColumns[i]);
				}

				report.initializeTreeModel();
				report.initializeDynamicTreeModel();
				Integer counter=0;
			   //for(int j=0;j<1;j++){
				 while(rs.next()){
				        //rs.next();
					    counter = 0;
				        if (noDays>=counter) {
				        	rowList = new ArrayList();
							rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
							rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
							System.out.println("data " + rs.getString(3));
						for (int i = 0; i < noDays; i++) {
							if (Integer.parseInt(rs.getString(i+3)) != 0) {
								String customDay;
								if((i+1)<10)
								customDay="0"+(i+1);
								else
								customDay=String.valueOf((i+1));
							    
									
								rowList.add(new URLData(rs.getString(i+3), urlPrefix1+ "&treasuryCode=" + rs.getString(2)+ "&month="+(customDay)+""+ currentMonthNumber + "&year="+ year));
							} else {
								rowList.add(new StyledData(rs.getString(i+3),CenterAlignVO));
							}
							counter = counter + 1;
					       }
						dataList.add(rowList);
						}	        
				   }
			
				
				if(counter==1) { rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); rowList.add(new StyledData("NA",
						  CenterAlignVO)); dataList.add(rowList); }
				
			}

			 if (report.getReportCode().equals("800009622")) {
//				if (report.getReportCode().equals("800009571")) {

				// report.setStyleList(noBorder);

				ArrayList rowList1 = new ArrayList();
				ResultSet rs1 = null;
				ResultSet rs2 = null;
				gLogger.info("Sub report");

 				String treasuryCode = (String) report
						.getParameterValue("treasuryCode");
				String daymonth = (String) report.getParameterValue("month");
				String year1 = (String) report.getParameterValue("year");
				String IsDeputation = (String) report.getParameterValue("IsDeputation");
				gLogger.info("$$$$$$$$$$$$TreasuryCode--" + treasuryCode);
				gLogger.info("$$$$$$$$$$$$month-- " + daymonth);
				gLogger.info("$$$$$$$$$$$$year1-- " + year1);
				gLogger.info("$$$$$$$$$$$$pranWise");
				int sizeOfrecord =0;
				int sizeOfrecord2=0;
				String treasuryName=null;
				String monthName=null;
				String monthid=null;
				String urlPrefix1 = "ifms.htm?actionFlag=reportService&reportCode=800009627&action=generateReport&IsDeputation="+IsDeputation;
				
				if(Integer.parseInt(daymonth.substring(2))<10){
					monthid="0"+daymonth.substring(2);
				}else{
					monthid=daymonth.substring(2);
				}
				
				StringBuilder sbTR = new StringBuilder();
				try {
					sbTR.append("SELECT LOC_NAME FROM CMN_LOCATION_MST WHERE DEPARTMENT_ID=100003 and LOC_ID="+treasuryCode);
					gLogger.info("StrSqlQueryFORTREASURY***********" + sbTR.toString());
					StrSqlQuery = sbTR.toString();
					rs = smt.executeQuery(StrSqlQuery);
					while (rs.next()){
						treasuryName = rs.getString(1);
					}
					
				} catch (Exception e) {
					gLogger.info(" Number of generated file" );
					e.getStackTrace();
				}
				
				StringBuilder sbMon = new StringBuilder();
				try {
					sbMon.append("SELECT lookup_name FROM cmn_lookup_mst WHERE PARENT_LOOKUP_ID=340000 and LOOKUP_SHORT_NAME="+monthid+" ");
					gLogger.info("StrSqlQuery FOR MONTH***********" + sbMon.toString());
					StrSqlQuery = sbMon.toString();
					rs = smt.executeQuery(StrSqlQuery);
					while (rs.next()){
						monthName = rs.getString(1);
					}
					
				} catch (Exception e) {
					gLogger.info(" Number of generated file" );
					e.getStackTrace();
				}				
				
			
				report.setAdditionalHeader("<b> Treasury Name  :   "+treasuryName.toUpperCase()+" \n MONTH  :  " + monthName.toUpperCase()+" \n YEAR : " + year1 + "</b>");
				StringBuilder sb1 = new StringBuilder();
				try {
					sb1.append("SELECT COUNT(*) as billprocess  FROM NSDL_BH_DTLS bh ");
					sb1.append("WHERE bh.FILE_NAME like'"+treasuryCode+"%' AND bh.BH_DATE="+(daymonth.substring(0,2))+monthid+year1+" and bh.STATUS<>-1 ");
					if(IsDeputation.equals("Deputation"))
					sb1.append(" and bh.FILE_NAME like'%D'");
					if(IsDeputation.equals("Regular"))
					sb1.append(" and bh.FILE_NAME not like'%D'");
//					sb1.append("WHERE bh.FILE_NAME like'7101202202%' AND bh.STATUS<>1 ");
					gLogger.info("StrSqlQuery***********" + sb1.toString());
					StrSqlQuery = sb1.toString();
					rs = smt.executeQuery(StrSqlQuery);
					while (rs.next()){
					sizeOfrecord = rs.getInt(1);
					}
					
				} catch (Exception e) {
					gLogger.info(" Number of generated file" );
					e.getStackTrace();
				}
				
			
				
				StringBuilder sb2 = new StringBuilder();
				try {
					sb2.append("SELECT COUNT(*) as billprocess  FROM NSDL_BH_DTLS bh ");
					sb2.append("INNER JOIN NSDL_BILL_DTLS bill on bh.FILE_NAME=bill.FILE_NAME ");
					sb2.append("WHERE bh.FILE_NAME like'"+treasuryCode+"%' AND bh.BH_DATE="+(daymonth.substring(0,2))+monthid+year1+" and bill.BILL_STATUS=3 and bh.STATUS<>-1");
					if(IsDeputation.equals("Deputation"))
					sb2.append(" and bh.FILE_NAME like'%D'");
					if(IsDeputation.equals("Regular"))
					sb2.append(" and bh.FILE_NAME not like'%D'");
//					sb2.append("WHERE bh.FILE_NAME like'7101202202%' AND bh.STATUS<>1 ");
					StrSqlQuery = sb2.toString();
					rs1 = smt.executeQuery(StrSqlQuery);
					gLogger.info("process file"+StrSqlQuery );
					while (rs1.next()){
						sizeOfrecord2 = rs1.getInt(1);
						}
					 
					
				} catch (Exception e) {
					gLogger.info(" Number of prcess file to BEAMS" );
					e.getStackTrace();
				}
				
				rowList1 = new ArrayList();
					
				
				if (sizeOfrecord != 0) {
					rowList1.add(new URLData(sizeOfrecord, urlPrefix1+ "&treasuryCode="+treasuryCode+"&month="+monthid+"&status=T&year="+year+"&date="+(daymonth.substring(0,2))+monthid+year1));
				} else {
					rowList1.add(new StyledData(sizeOfrecord,CenterAlignVO));
				}
				if (sizeOfrecord2 != 0) {
					rowList1.add(new URLData(sizeOfrecord2, urlPrefix1+ "&treasuryCode="+treasuryCode+"&month="+monthid+"&status=F&year="+year+"&date="+(daymonth.substring(0,2))+monthid+year1));
				} else {
					rowList1.add(new StyledData(sizeOfrecord2,CenterAlignVO));
				}
				if ((sizeOfrecord-sizeOfrecord2) != 0) {
					rowList1.add(new URLData((sizeOfrecord-sizeOfrecord2), urlPrefix1+ "&treasuryCode="+treasuryCode+"&month="+monthid+"&status=P&year="+year+"&date="+(daymonth.substring(0,2))+monthid+year1));
				} else {
					rowList1.add(new StyledData((sizeOfrecord-sizeOfrecord2),CenterAlignVO));
				}
					//rowList1.add(new StyledData(sizeOfrecord,CenterAlignVO));
					//rowList1.add(new StyledData(sizeOfrecord2,CenterAlignVO));
					//rowList1.add(new StyledData((sizeOfrecord-sizeOfrecord2),CenterAlignVO));
					dataList.add(rowList1);
			}
				if (report.getReportCode().equals("800009627")) {
					
					String treasuryCode = (String) report.getParameterValue("treasuryCode");
					String filemonth = (String) report.getParameterValue("month");
					String fileyear = (String) report.getParameterValue("year");
					String filedate = (String) report.getParameterValue("date");
					String status = (String) report.getParameterValue("status");
					String IsDeputation = (String) report.getParameterValue("IsDeputation");
					gLogger.info("IsDeputation  ***********" + IsDeputation);
					gLogger.info("treasuryCode  ***********" + treasuryCode);
					gLogger.info("file month  ***********" + filemonth);
					gLogger.info("file year  ***********" + fileyear);
					gLogger.info("file generated date  ***********" + filedate);
					
					StringBuilder strinBU = new StringBuilder();
					
					//String urlPrefix1 = "ifms.htm?actionFlag=reportService&reportCode=800009571&action=generateReport";
					if(status.equals("T"))
					{
						report.setAdditionalHeader("<center> <b> Total generated NPS file </b> </center> ");
						
						
						strinBU.append("SELECT bh.FILE_NAME,bh.MONTH,bh.YEAR,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT,bh.BH_TOTAL_AMT,substr(bh.BH_DATE,0,2)||'-'||substr(bh.BH_DATE,3,2)||'-'||substr(bh.BH_DATE,5,4),CASE WHEN bh.TRANSACTION_ID is null THEN '-' else bh.TRANSACTION_ID end "); 
						strinBU.append("FROM NSDL_BH_DTLS bh "); 
						strinBU.append("WHERE bh.FILE_NAME like'"+treasuryCode+"%' AND bh.BH_DATE='"+filedate+"' and bh.STATUS<>-1 ");
						if(IsDeputation.equals("Deputation"))
						strinBU.append(" and bh.FILE_NAME like'%D'");
						if(IsDeputation.equals("Regular"))
						strinBU.append(" and bh.FILE_NAME not like'%D'");
						
						strinBU.append(" ORDER BY bh.FILE_NAME "); 
							
					}else if(status.equals("F"))
					{
						report.setAdditionalHeader("<center> <b> Total Process NPS file </b> </center> ");
						strinBU.append("SELECT bh.FILE_NAME,bh.MONTH,bh.YEAR,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT,bh.BH_TOTAL_AMT,substr(bh.BH_DATE,0,2)||'-'||substr(bh.BH_DATE,3,2)||'-'||substr(bh.BH_DATE,5,4),CASE WHEN bh.TRANSACTION_ID is null THEN '-' else bh.TRANSACTION_ID end "); 
						strinBU.append("FROM NSDL_BH_DTLS bh "); 
						strinBU.append("INNER JOIN NSDL_BILL_DTLS bill on bh.FILE_NAME=bill.FILE_NAME "); 
						strinBU.append("WHERE bh.FILE_NAME like'"+treasuryCode+"%' AND bh.BH_DATE='"+filedate+"' and bill.BILL_STATUS=3 and bh.STATUS<>-1 ");
						if(IsDeputation.equals("Deputation"))
							strinBU.append(" and bh.FILE_NAME like'%D'");
						if(IsDeputation.equals("Regular"))
							strinBU.append(" and bh.FILE_NAME not like'%D'");
							
							strinBU.append(" ORDER BY bh.FILE_NAME "); 
						
					}else {
						report.setAdditionalHeader("<center> <b> Total Pending NPS file </b> </center> ");
						
						strinBU.append("SELECT bh.FILE_NAME,bh.MONTH,bh.YEAR,bh.BH_EMP_AMOUNT,bh.BH_EMPLR_AMOUNT,bh.BH_TOTAL_AMT,substr(bh.BH_DATE,0,2)||'-'||substr(bh.BH_DATE,3,2)||'-'||substr(bh.BH_DATE,5,4),CASE WHEN bh.TRANSACTION_ID is null THEN '-' else bh.TRANSACTION_ID end "); 
						strinBU.append("FROM NSDL_BH_DTLS bh "); 
						strinBU.append("WHERE bh.FILE_NAME like'"+treasuryCode+"%' AND bh.BH_DATE='"+filedate+"' and bh.STATUS<>-1 ");
						if(IsDeputation.equals("Deputation"))
						strinBU.append(" and bh.FILE_NAME like'%D'");
						if(IsDeputation.equals("Regular"))
						strinBU.append(" and bh.FILE_NAME not like'%D'");
						strinBU.append("and bh.FILE_NAME not in(SELECT bh.FILE_NAME FROM NSDL_BH_DTLS bh "); 
						strinBU.append("INNER JOIN NSDL_BILL_DTLS bill on bh.FILE_NAME=bill.FILE_NAME "); 
						strinBU.append("WHERE bh.FILE_NAME like'"+treasuryCode+"%' AND bh.BH_DATE='"+filedate+"' "); 
						strinBU.append("and bill.BILL_STATUS=3 and bh.STATUS<>-1 ");
						if(IsDeputation.equals("Deputation"))
						strinBU.append(" and bh.FILE_NAME like'%D'");
						if(IsDeputation.equals("Regular"))
						strinBU.append(" and bh.FILE_NAME not like'%D'");
							
						strinBU.append(" ORDER BY bh.FILE_NAME )"); 
					}
					gLogger.info("StrSqlQuery***********" + strinBU.toString());
					StrSqlQuery = strinBU.toString();
					rs = smt.executeQuery(StrSqlQuery);
					while (rs.next()) {
										
						List FilePendingList = new ArrayList();
						FilePendingList.add(new StyledData(rs.getString(1), CenterAlignVO));
						FilePendingList.add(new StyledData(rs.getString(2), CenterAlignVO));
						FilePendingList.add(new StyledData(rs.getString(3), CenterAlignVO));
						FilePendingList.add(new StyledData(rs.getString(4), CenterAlignVO));
						FilePendingList.add(new StyledData(rs.getString(5), CenterAlignVO));
						FilePendingList.add(new StyledData(rs.getString(6), CenterAlignVO));
						FilePendingList.add(new StyledData(rs.getString(7), CenterAlignVO));
						if(rs.getString(8)!="null"){
						FilePendingList.add(new StyledData(rs.getString(8), CenterAlignVO));
						}else{
							FilePendingList.add("-");
						}
						System.out.println("data " + FilePendingList);

						/*if (Integer.parseInt(rs.getString(1)) != 0) {
							FilePendingList.add(new URLData(rs.getString(1), urlPrefix1
									+ "&treasuryCode=" + rs.getString(1)
									+ "&month=01" + currentMonthNumber + "&year="
									+ year));
						} else {
							FilePendingList.add(new StyledData(rs.getString(1),
									CenterAlignVO));
						}
						if (Integer.parseInt(rs.getString(2)) != 0) {
							FilePendingList.add(new URLData(rs.getString(2), urlPrefix1
									+ "&treasuryCode=" + rs.getString(2)
									+ "&month=01" + currentMonthNumber + "&year="
									+ year));
						} else {
							FilePendingList.add(new StyledData(rs.getString(2),
									CenterAlignVO));
						}*/
						dataList.add(FilePendingList);
					
				}
					
				}
                StyleVO[] lObjStyleVO4 = new StyleVO[report.getStyleList().length];
                lObjStyleVO4 = report.getStyleList();
                for (Integer lInt4 = 0; lInt4 < report.getStyleList().length; ++lInt4) {
                    if (lObjStyleVO4[lInt4].getStyleId() == 26) {
                        //lObjStyleVO4[lInt4].setStyleValue("ifms.htm?actionFlag=reportService&reportCode=800009568&action=parameterPage");
                    	lObjStyleVO4[lInt4].setStyleValue("JavaScript:self.close();");
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
	
	public List getMonth(String lStrLangId, String lStrLocCode) {
		List<Object> lArrMonths = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator()
			.getSessionFactory().getCurrentSession();

			String lStrBufLang = "SELECT monthId, monthName FROM SgvaMonthMst WHERE langId = :langId ORDER BY monthNo";

			Query lObjQuery = lObjSession.createQuery(lStrBufLang);
			lObjQuery.setString("langId", lStrLangId);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[1].toString());
					lArrMonths.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}

		return lArrMonths;
	}
	public ArrayList getFileType(String lstrLangId, String lstrLocId) {
		
		 ArrayList fileType = new ArrayList();
		 DefaultComboItem ci11 = new DefaultComboItem( "ALL","ALL");
		 DefaultComboItem ci12 = new DefaultComboItem( "Regular","Regular");
		 DefaultComboItem ci13 = new DefaultComboItem( "Deputation","Deputation");
		 
		 fileType.add(ci11);
		 fileType.add(ci12);
		 fileType.add(ci13);
		 
		 
		return fileType;
	}
	public ArrayList getYear(String lstrLangId, String lstrLocId)
	{
		Connection lCon = null;
		Logger logger = Logger.getLogger(ReportParameterDAO.class);
		StringBuffer lSb = new StringBuffer();
        PreparedStatement lPStmt = null;
        ResultSet lRs = null;
        ArrayList lCmbList = new ArrayList();		
		try {	           
            lCon = DBConnection.getConnection();
        }
        catch(Exception e) {
            logger.error("Exception in Connecting to database " + e);
        }
        try {          
        	lSb.append(" select b.lookup_name "); 
        	lSb.append(" from cmn_lookup_mst a, cmn_lookup_mst b "); 
        	lSb.append(" where a.lookup_name = 'Year' and a.lookup_id = b.parent_lookup_id and "); 
        	lSb.append(" a.lang_id = 1 and b.lang_id = 1 and b.LOOKUP_NAME>=2015 "); 
        	lSb.append(" order by b.order_no,b.lookup_name ");
            lPStmt = lCon.prepareStatement(lSb.toString());
            logger.info(" The Executed Query is \n\t\t "+lSb.toString());
            lRs = lPStmt.executeQuery();
            while(lRs.next()) {
            	DefaultComboItem ci1 = new DefaultComboItem( lRs.getString("lookup_name"),lRs.getString("lookup_name")); 
            	lCmbList.add(ci1);           
            }          
        }
        catch(SQLException se)
        {
            logger.error("Error is: "+ se.getMessage());
            logger.error("Combo Details Not Found " + se);
        }
        finally {
            try {
                //close the resultset if not null
                if (lRs != null) {
                	lRs.close();
                }

                //close the statement if not null 
                if (lPStmt != null) {
                	lPStmt.close();
                }

                //close the connection if not null  
                if (lCon != null) {
                	lCon.close();
                }
            } catch (Exception e) {
                logger.error("Error in closing connection " + e);
            }
        }
        return lCmbList;	       
	}

}
