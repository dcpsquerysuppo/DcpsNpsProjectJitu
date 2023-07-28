package com.tcs.sgv.dcps.report;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DateUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportParameterVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

public class NpsPendencyTrackingReport   extends DefaultReportDataFinder implements ReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(NpsPendencyTrackingReport.class);
	ServiceLocator serviceLocator = null;
	Map requestAttributes = null;
	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException 
	{
		ArrayList tabledata = new ArrayList();
		String langId = report.getLangId();
		String locId = report.getLocId();
		String lStrSevaarthId="";
		String lStrEmpName="";
		String pranNo="";
		ArrayList td;
		try
		{
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap"); 
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			Long locationId=0l;
			Long userId=0l;
			String userType="";
			String sevaarthId="",EmpName="";
			String Location_code="";	
			
			if (loginDetail.containsKey("locationId")) {
				Location_code = loginDetail.get("locationId").toString();
			}
			
			if (loginDetail.containsKey("loginName")) {
				sevaarthId = loginDetail.get("loginName").toString();
//				EmpName=loginDetail.get("empFname").toString()+"  "+loginDetail.get("empLname").toString() ;
				EmpName=sessionKeys.get("name").toString();
			}
			
			
			if (loginDetail.get("locationId")!=null && !"".equalsIgnoreCase(loginDetail.get("locationId").toString()))
			{
				locationId=Long.parseLong(loginDetail.get("locationId").toString());
				
			}
			
		if (loginDetail.get("userId")!=null && !"".equalsIgnoreCase(loginDetail.get("userId").toString()))
			{
				userId =Long.parseLong(loginDetail.get("userId").toString());

			}  
			gLogger.info("user id is ###########################"+userId);  
			if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
			{

				 userType=report.getParameterValue("UserType").toString();
			}
			
			if (report.getParameterValue("locationId")!=null && !"".equalsIgnoreCase(report.getParameterValue("locationId").toString()) )
			{
				locationId=Long.parseLong(report.getParameterValue("locationId").toString());
				
			}
			StyleVO[] rowsFontsVO = new StyleVO[4];
			rowsFontsVO[0] = new StyleVO();
			rowsFontsVO[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
			rowsFontsVO[0].setStyleValue("26");
			rowsFontsVO[1] = new StyleVO();
			rowsFontsVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rowsFontsVO[1].setStyleValue("10");
			rowsFontsVO[2] = new StyleVO();
			rowsFontsVO[2].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
			rowsFontsVO[2].setStyleValue("Shruti");
			rowsFontsVO[3] = new StyleVO();
			rowsFontsVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			rowsFontsVO[3].setStyleValue("white");

			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();
			String ddoCode="";
			String dcpsEmpId="";
			String treasuryCode="";
			if (report.getReportCode().equals("9000526")) 
			{
				SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");	
	            Date date = new Date();	
	            String curDate = dateFormat.format(date);	
	            gLogger.info("=====Innside parent report=====");	
	            String header5 = this.space(5) + "Date :- " + this.space(2) + curDate;	
	            report.setAdditionalHeader(header5);
				 
				List TableData = null;
			
				StringBuilder SBQuery = new StringBuilder();
				 String lStrYearId = (String) report.getParameterValue("yearId");
				 String lStrMonthId = (String) report.getParameterValue("monthId");
				 String SubTreasury = (String) report.getParameterValue("subtreasuryCode");
				 
				 
				 Date monthId = new SimpleDateFormat("MMMM").parse(lStrMonthId);
			    	Calendar cal = Calendar.getInstance();
			    	cal.setTime(monthId);
			    	//System.out.println(cal.get(Calendar.MONTH)+1);
			    	lStrMonthId=String.valueOf(cal.get(Calendar.MONTH)+1);
			    	//lStrMonthId=cal.get(Calendar.MONTH)+1;
				 
				 
			
				List TableData1= getTreasuryWisePendingDtls(lStrYearId,SubTreasury,lStrMonthId);
				for (int i=0;i<TableData1.size();i++)
				{
					int countTreApprove = 0;
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
					td.add(i+1);
					if (tupleSub[1] != null) 
					{
							td.add(tupleSub[1]);
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[0] != null) 
					{
						countTreApprove=getTreasuryWisePaybillsCoveredNps(SubTreasury,lStrYearId,lStrMonthId);
						td.add(countTreApprove);
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[0] != null) 
					{          						
							td.add((((int) tupleSub[2])-countTreApprove));
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[2] != null) 
					{
						String	monthString = new DateFormatSymbols().getMonths()[Integer. parseInt(lStrMonthId)-1];
						List getTreasuryName= getTreasuryName(SubTreasury);
						td.add(new URLData(tupleSub[2] ,"hrms.htm?actionFlag=reportService&reportCode=9000527&action=generateReport&DirectReport=TRUE&displayOK=FALSE&finYear="+lStrYearId+"&treasuryCode="+getTreasuryName.get(0)+"&finMonth="+monthString));
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[2] != null) 
					{

						int countTreNpsGenerated=getTreasuryWisePaybillsGeneratedNps(SubTreasury,lStrYearId,lStrMonthId);
						td.add(countTreNpsGenerated);

					}
					else
					{
							td.add("-");
					}
					if (tupleSub[2] != null) 
					{

						int countTreNpsApprove=getTreasuryWisePaybillsApproveNps(SubTreasury,lStrYearId,lStrMonthId);
						td.add(countTreNpsApprove);

					}
					else
					{
							td.add("-");
					}

				
					tabledata.add(td);
				}
	          }
			 if (report.getReportCode().equals("9000527")) {

				/*SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");	
	            Date date = new Date();	
	            String curDate = dateFormat.format(date);	
	            gLogger.info("=====Innside parent report=====");	
	            String header5 = this.space(5) + "Date :- " + this.space(2) + curDate;	
	            report.setAdditionalHeader(header5);*/
				 
				List TableData = null;
			
				 StringBuilder SBQuery = new StringBuilder();
				 String lStrYearId = (String) report.getParameterValue("finYear");
				 String lStrMonthId = (String) report.getParameterValue("finMonth");
				 String SubTreasury = (String) report.getParameterValue("treasuryCode");
				 
				 SubTreasury= getTreasuryId(SubTreasury);
				 
				 List getTreasuryFullName= getTreasuryFullName(SubTreasury);
				
		         gLogger.info("=====Innside parent report=====");	
		         String header5 = this.space(5) + "Treasury :- " + this.space(2) + getTreasuryFullName.get(0).toString();	
		         report.setAdditionalHeader(header5);
				 
				 
				 
				    Date monthId = new SimpleDateFormat("MMMM").parse(lStrMonthId);
			    	Calendar cal = Calendar.getInstance();
			    	cal.setTime(monthId);
			    	//System.out.println(cal.get(Calendar.MONTH)+1);
			    	lStrMonthId=String.valueOf(cal.get(Calendar.MONTH)+1);
			    	//lStrMonthId=cal.get(Calendar.MONTH)+1;
			    	
			    	
				List TableData1= getDDOWiseTotalApprovedPaybills(SubTreasury,lStrYearId,lStrMonthId);
				
				for (int i=0;i<TableData1.size();i++)
				{
					int countApprove = 0;
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
					td.add(i+1);
					if (tupleSub[0] != null) 
					{
							td.add(tupleSub[0]);
							
							ddoCode=((String) tupleSub[0]).substring(0, 10).trim();
					}
					else
					{
							td.add("-");
					}
					String	monthString = null;
					List getTreasuryName=null;
					if (tupleSub[1] != null)
					{
						countApprove=getDDOWisePaybillsCoveredNps(ddoCode,lStrYearId,lStrMonthId);
						monthString = new DateFormatSymbols().getMonths()[Integer. parseInt(lStrMonthId)-1];
						getTreasuryName= getTreasuryName(SubTreasury);
						td.add(new URLData(countApprove ,"hrms.htm?actionFlag=reportService&reportCode=9000528&action=generateReport&DirectReport=TRUE&displayOK=FALSE&finYear="+lStrYearId+"&treasuryCode="+getTreasuryName.get(0)+"&finMonth="+monthString+"&ddoCode="+ddoCode));
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[2] != null) 
					{	
						td.add(new URLData(( ((int) tupleSub[1]) - countApprove) ,"hrms.htm?actionFlag=reportService&reportCode=9000529&action=generateReport&DirectReport=TRUE&displayOK=FALSE&finYear="+lStrYearId+"&treasuryCode="+getTreasuryName.get(0)+"&finMonth="+monthString+"&ddoCode="+ddoCode));
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[1] != null) 
					{
						
						td.add(tupleSub[1].toString());
					}
					else
					{
							td.add("-");
					}
								
					tabledata.add(td);
				}
	          
			}
			 
			 if (report.getReportCode().equals("9000528")) {

				 List TableData = null;
			
				 StringBuilder SBQuery = new StringBuilder();
				 String lStrYearId = (String) report.getParameterValue("finYear");
				 String SubTreasury = (String) report.getParameterValue("treasuryCode");
				 String lStrMonthId = (String) report.getParameterValue("finMonth");
				 String lstrddoCode = (String) report.getParameterValue("ddoCode");
				 
				 SubTreasury= getTreasuryId(SubTreasury);
				 
				 Date monthId = new SimpleDateFormat("MMMM").parse(lStrMonthId);
			    	Calendar cal = Calendar.getInstance();
			    	cal.setTime(monthId);
			    	//System.out.println(cal.get(Calendar.MONTH)+1);
			    	lStrMonthId=String.valueOf(cal.get(Calendar.MONTH)+1);
			    	//lStrMonthId=cal.get(Calendar.MONTH)+1;
			    	
			    	
			    	 List getTreasuryFullName= getTreasuryFullName(SubTreasury);
						
			         gLogger.info("=====Innside parent report=====");
				 
				 List getDdoName= getDdoName(lstrddoCode);
				 
				 String header5 = this.space(5) +"-Treasury :- " + this.space(2) + getTreasuryFullName.get(0).toString()+" "+ this.space(5) +"-DDO Code and Office Name:- " + this.space(2) + getDdoName.get(0);	
		         report.setAdditionalHeader(header5);
				 
				 String temp="NA";
				 int j=1;
			
				 List TableData1= getDDOWiseApprovedPaybillsDetails(SubTreasury,lStrYearId,lStrMonthId,lstrddoCode); 
				
				for (int i=0;i<TableData1.size();i++)
				{	
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
					td.add(i+1);
					if (tupleSub[0] != null) 
					{		
							td.add(tupleSub[5]);
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[1] != null)
					{
						td.add(tupleSub[1]);
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[2] != null) 
					{
						td.add(tupleSub[2]);
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[3] != null)//4
					{
						
						td.add(tupleSub[3]);

					}
					else
					{
							td.add("-");
					}
					if (tupleSub[4] != null)//5
					{
						
						td.add(tupleSub[4]);

					}
					else
					{
							td.add("-");
					}
					if (tupleSub[4] != null)//6
					{
						
						td.add((int)tupleSub[3]+(int)tupleSub[4]);

					}
					else
					{
							td.add("-");
					}				


					gLogger.info("=====tupleSub[0]=====" +tupleSub[0]);
					List Npsdtls=getDDOWiseNpsDetails(tupleSub[0],lStrYearId,lStrMonthId,lstrddoCode,SubTreasury,tupleSub[2]);
					
					Object[] tupleSub1 = null;
					
					if(Npsdtls.size()>0){	
					
					tupleSub1 = (Object[]) Npsdtls.get(0);
					
					if (tupleSub1[0] != null)//6
					{
						
						td.add(tupleSub1[0]);

					}
					else
					{
							td.add("-");
					}
					if (tupleSub1[1] != null)//7
					{
						
						td.add(tupleSub1[1]);

					}
					else
					{
							td.add("-");
					}
					gLogger.info("=====tupleSub1[0]=====" +tupleSub1[0]);
					List NpsBilldtls=getDDOWiseNpsBillDetails(tupleSub1[0],lStrYearId,lStrMonthId);
                    Object[] tupleSub2 = null;
                    String jump = "IN";
                    
                    if(NpsBilldtls.size()>0){	
    					
    					tupleSub2 = (Object[]) NpsBilldtls.get(0);
    					
    					if (tupleSub2[0] != null)//8
    					{
    						
    						td.add(tupleSub2[0]);

    					}
    					if (tupleSub2[1] != null)//9
    					{
    						
    						td.add(tupleSub2[1]);

    					}
    				}else{
    					    jump="JUMP";
    						td.add("-");

    						td.add("-");
    				}
                    List NpsBeamsdtls=getDDOWiseNpsBeamsDetails(tupleSub1[0],lStrYearId,lStrMonthId);
                    Object[] tupleSub3 = null;
                    
                    if(NpsBeamsdtls.size()>0){	
    					
                    	tupleSub3 = (Object[]) NpsBeamsdtls.get(0);
    					
                    	if (tupleSub3[0] != null)//10
    					{
    						
    						td.add(tupleSub3[0]);

    					}
    					if (tupleSub3[1] != null)//11
    					{
    						
    						//td.add(tupleSub3[1]);
    						td.add("-");

    					}
    				}else{	
    						td.add("-");
    						
    						td.add("-");
    				}
					
					if (tupleSub1[6] != null)//12
					{	
						SimpleDateFormat obj = new SimpleDateFormat("dd/MM/yyyy");
						
						 Date date1 = obj.parse((String) tupleSub[2]);   
					     Date date2 = obj.parse((String) tupleSub1[1]); 
				         long diff = date2.getTime() - date1.getTime();
				         long days= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
						 
				         td.add(days);

					}
					else
					{
							td.add("-");
					}				
					if (tupleSub1[7] != null)//13
					{	
						td.add(tupleSub1[7]);
					}
					else
					{
							td.add("-");
					}
					if (!jump.equals("JUMP"))//14
					{
						//System.out.println("G-->"+tupleSub1[1]);
						//System.out.println("H-->"+tupleSub2[0]);
						SimpleDateFormat obj = new SimpleDateFormat("dd/MM/yyyy");
						
						 Date date1 = obj.parse((String) tupleSub1[1]);  
					     Date date2 = obj.parse((String) tupleSub2[0]); 
				         long diff = date2.getTime() - date1.getTime();
				         long days= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
						 
				        td.add(days);
					}
					else
					{
							td.add("-");
					}
					if (tupleSub1[9] != null)//15
					{	
						td.add(tupleSub1[9]);

					}
					else
					{
							td.add("-");
					}
					if (!jump.equals("JUMP"))//16
					{
						//System.out.println("I-->"+tupleSub2[0]);
						//System.out.println("J-->"+tupleSub3[0]);
						SimpleDateFormat obj = new SimpleDateFormat("dd/MM/yyyy");
						
						 Date date1 = obj.parse((String) tupleSub2[0]);  
					     Date date2 = obj.parse((String) tupleSub3[0]); 
				         long diff = date2.getTime() - date1.getTime();
				         long days= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
						 
				         td.add(days);
					}
					else
					{
							td.add("-");
					}
					if (tupleSub1[11] != null)//17
					{
						
						td.add(tupleSub1[11]);

					}
					else
					{
							td.add("-");
					}
					
					if (!jump.equals("JUMP"))//18
					{
						//System.out.println("A-->"+tupleSub[0]);
						//System.out.println("K-->"+tupleSub1[5]);
						/*int c=(int) tupleSub1[5];
						if((int) tupleSub1[5]>0){*/
						//String []paybillId=tupleSub[0].toString().split("-");
					    //String A=paybillId[1];
					    
						SimpleDateFormat obj = new SimpleDateFormat("dd/MM/yyyy");
						
						 //Date date1 = obj.parse(A.substring(2,12).trim());
						 
						 Date date1 = obj.parse((String)tupleSub[2]);
					     Date date2 = obj.parse((String) tupleSub3[0]);
				         long diff = date2.getTime() - date1.getTime();
				         long days= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
						
						td.add(days);
					}
					else
					{
							td.add("-");
					}
					
					tabledata.add(td);
				 }
			   }	          
			}
			 if (report.getReportCode().equals("9000529")) {
					 
					List TableData = null;
				
					StringBuilder SBQuery = new StringBuilder();
					 String lStrYearId = (String) report.getParameterValue("finYear");
					 String SubTreasury = (String) report.getParameterValue("treasuryCode");
					 String lStrMonthId = (String) report.getParameterValue("finMonth");
					 String lstrddoCode = (String) report.getParameterValue("ddoCode");
					 
					 SubTreasury= getTreasuryId(SubTreasury);
					 
					 Date monthId = new SimpleDateFormat("MMMM").parse(lStrMonthId);
				    	Calendar cal = Calendar.getInstance();
				    	cal.setTime(monthId);
				    	//System.out.println(cal.get(Calendar.MONTH)+1);
				    	lStrMonthId=String.valueOf(cal.get(Calendar.MONTH)+1);
				    	//lStrMonthId=cal.get(Calendar.MONTH)+1;
				    	
				    	
				    	 List getTreasuryFullName= getTreasuryFullName(SubTreasury);
							
				         gLogger.info("=====Innside parent report=====");
					 
					 List getDdoName= getDdoName(lstrddoCode);
					 
					 String header5 = this.space(5) +"-Treasury :- " + this.space(2) + getTreasuryFullName.get(0).toString()+" "+ this.space(5) +"-DDO Code and Office Name:- " + this.space(2) + getDdoName.get(0);	
			         report.setAdditionalHeader(header5);
					 
					 String temp="NA";
					 int j=1;
				
					 List TableData1= getDDOWiseNotApprovedPaybillsDetails(SubTreasury,lStrYearId,lStrMonthId,lstrddoCode); 
					
					for (int i=0;i<TableData1.size();i++)
					{	
						Object[] tupleSub = (Object[]) TableData1.get(i);
						td = new ArrayList();
						td.add(i+1);
						if (tupleSub[0] != null) 
						{
								td.add(tupleSub[6]);
						}
						else
						{
								td.add("-");
						}
						if (tupleSub[1] != null)
						{
							td.add(tupleSub[1]);
						}
						else
						{
								td.add("-");
						}
						if (tupleSub[2] != null) 
						{
							td.add(tupleSub[2]);
						}
						else
						{
								td.add("-");
						}
						if (tupleSub[3] != null)//4
						{
							
							td.add(tupleSub[3]);

						}
						else
						{
								td.add("-");
						}
						if (tupleSub[4] != null)//5
						{
							
							td.add(tupleSub[4]);

						}
						else
						{
								td.add("-");
						}
						if (tupleSub[4] != null)//6
						{
							
							td.add((int)tupleSub[3]+(int)tupleSub[4]);

						}
						else
						{
								td.add("-");
						}

						if (tupleSub[5] != null)//7
						{
							
							td.add(tupleSub[5]);

						}
						else
						{
								td.add("-");
						}
						if (tupleSub[5] != null)//8
						{
							/*String []paybillId=tupleSub[0].toString().split("-");
						    String A=paybillId[1];*/
						    
							SimpleDateFormat obj = new SimpleDateFormat("dd/MM/yyyy");
							//System.out.println("-->"+tupleSub[2]);
							//System.out.println("-->"+tupleSub[5]);
							 //Date date1 = obj.parse(A.substring(2,12).trim());
							 
							 /*Date date1 = obj.parse(obj.format(tupleSub[2]).toString());
						     Date date2 = obj.parse(obj.format(tupleSub[5]).toString()); //tupleSub[2]*/
							
							
							 Date date1 = obj.parse((String)tupleSub[2]);
							 Date date2 = obj.parse(obj.format(tupleSub[5]).toString());
					         long diff = date2.getTime() - date1.getTime();
					         long days= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
							
							td.add(days);
							//td.add(tupleSub[5]);

						}
						else
						{
								td.add("-");
						}
						
						gLogger.info("=====tupleSub[0]=====" +tupleSub[0]);
						List Npsdtls=getDDOWiseNpsDetails(tupleSub[0],lStrYearId,lStrMonthId,lstrddoCode,SubTreasury,tupleSub[2]);
						
						if(Npsdtls.size()<=0){
						tabledata.add(td);
						}
				   }//for          
				}//if
		}
		catch (Exception e) 
		{
			gLogger.info("=====findReportData(): Exception is=====" + e, e);
		}
		return tabledata;
	}
	
	
private List getTreasuryFullName(String subTreasury) {
	StringBuilder sb = new StringBuilder();
	List ApprovedPaybills=null;
	
	try
	{	
		sb.append("SELECT LOC_NAME FROM CMN_LOCATION_MST where LOC_ID='"+subTreasury+"' ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		ApprovedPaybills= lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return ApprovedPaybills;
	}


private String getTreasuryId(String subTreasury) {
	StringBuilder sb = new StringBuilder();
	int ApprovedPaybills;
	
	try
	{	
		sb.append("SELECT LOC_ID FROM CMN_LOCATION_MST where LOC_SHORT_NAME like '%"+subTreasury+"%' and DEPARTMENT_ID=100003 ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		ApprovedPaybills= (int) lQuery.list().get(0);
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return String.valueOf(ApprovedPaybills);
	}


private List getTreasuryName(String subTreasury) {
	StringBuilder sb = new StringBuilder();
	List ApprovedPaybills=null;
	
	try
	{	
		sb.append("SELECT case when LOC_SHORT_NAME ='PAO, MUMBAI' then 'PAO' else  LOC_SHORT_NAME end FROM CMN_LOCATION_MST where LOC_ID='"+subTreasury+"' ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		ApprovedPaybills= lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return ApprovedPaybills;
	}


private List getDdoName(String lstrddoCode) {
	StringBuilder sb = new StringBuilder();
	List ApprovedPaybills=null;
	
	try
	{	
		sb.append("SELECT ddo_code||'--'||ddo_name FROM ORG_DDO_MST where DDO_CODE='"+lstrddoCode+"' ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		ApprovedPaybills= lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return ApprovedPaybills;
	}


private List getDDOWiseNpsBeamsDetails(Object object, String lStrYearId,
			String lStrMonthId) {
	StringBuilder sb = new StringBuilder();
	List ApprovedPaybills=null;
	String []paybillId=object.toString().split("-");
	try
	{	
		//sb.append("SELECT VARCHAR_FORMAT(trn.CREATED_DATE,'DD/MM/YYYY'),VARCHAR_FORMAT(trn.CREATED_DATE,'DD/MM/YYYY') "
				sb.append("SELECT VARCHAR_FORMAT(trn.CREATED_DATE,'DD/MM/YYYY'),VARCHAR_FORMAT(trn.CREATED_DATE,'DD/MM/YYYY') "
				+ "FROM TRN_NPS_BEAMS_INTEGRATION trn join NSDL_BILL_DTLS bill on trn.bill_no=bill.FILE_NAME"
				+ " where bill.FILE_NAME='"+paybillId[0].trim()+"' and bill.BILL_STATUS=3 and bill.BILL_YEAR='"+lStrYearId+"' and bill.BILL_MONTH='"+lStrMonthId+"' ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		ApprovedPaybills= lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return ApprovedPaybills;
	}


private List getDDOWiseNpsBillDetails(Object object, String lStrYearId,String lStrMonthId) {
	StringBuilder sb = new StringBuilder();
	List ApprovedPaybills=null;
	String []paybillId=object.toString().split("-");
	try
	{	
		sb.append("SELECT VARCHAR_FORMAT(bill.BILL_GENERATION_DATE,'DD/MM/YYYY'),VARCHAR_FORMAT(bill.BILL_GENERATION_DATE,'DD/MM/YYYY') "
				+ "FROM NSDL_BILL_DTLS bill where FILE_NAME='"+paybillId[0].trim()+"' and BILL_STATUS=3 and BILL_YEAR='"+lStrYearId+"' and BILL_MONTH='"+lStrMonthId+"' ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		ApprovedPaybills= lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return ApprovedPaybills;
	}


private int getTreasuryWisePaybillsApproveNps(String subTreasury,
			String lStrYearId, String lStrMonthId) {
	StringBuilder sb = new StringBuilder();
	List paybillCoveredNps=null;
	int appCount = 0;
	try
	{	
		sb.append("SELECT count(*) FROM nsdl_bh_dtls bh join NSDL_BILL_DTLS bill on bh.FILE_NAME=bill.FILE_NAME "); 
		sb.append("where substr(bh.file_name,1,2)=substr('"+subTreasury+"',1,2) and bill.bill_status=3 and bh.file_name not like'%D' "
				+ "and bh.YEAR='"+lStrYearId+"' AND bh.MONTH='"+lStrMonthId+"' and bh.STATUS <>-1 ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		paybillCoveredNps= lQuery.list();

          if ((paybillCoveredNps != null) && (paybillCoveredNps.size() > 0)) {

        	  appCount = (int) paybillCoveredNps.get(0);
          }
	}catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
	
	return appCount;
	}


private int getTreasuryWisePaybillsGeneratedNps(String subTreasury,
			String lStrYearId, String lStrMonthId) {
	StringBuilder sb = new StringBuilder();
	List paybillCoveredNps=null;
	int genCount = 0;
	try
	{	
		sb.append("SELECT count(*) FROM nsdl_bh_dtls bh "); 
		sb.append("where substr(bh.file_name,1,2)=substr('"+subTreasury+"',1,2) and bh.STATUS <>-1 and bh.file_name not like'%D' "
				+ "and bh.YEAR='"+lStrYearId+"' AND bh.MONTH='"+lStrMonthId+"' ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		paybillCoveredNps= lQuery.list();

          if ((paybillCoveredNps != null) && (paybillCoveredNps.size() > 0)) {

        	  genCount = (int) paybillCoveredNps.get(0);
          }
	}catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
	
	return genCount;
}
private List getDDOWiseNotApprovedPaybillsDetails(String subTreasury,
			String lStrYearId, String lStrMonthId, String lstrddoCode) {
	
	StringBuilder sb = new StringBuilder();
	List notApprovedPaybills=null;
	try
	{
		sb.append("SELECT head.PAYBILL_ID||'- ('||VARCHAR_FORMAT(head.CREATED_DATE, 'DD/MM/YYYY')||')',VARCHAR_FORMAT(trn.CREATED_DATE, 'DD/MM/YYYY'),"
				+ " case when temp.ACCOUNTING_DATE is null then VARCHAR_FORMAT(temp1.ACCOUNTING_DATE,'DD/MM/YYYY') else VARCHAR_FORMAT(temp.ACCOUNTING_DATE,'DD/MM/YYYY') end ,LEAST(180, DAYS(trn.CREATED_DATE) - DAYS(head.CREATED_DATE)) as D,case when temp.ACCOUNTING_DATE is null then LEAST(180,DAYS(temp1.ACCOUNTING_DATE) - DAYS(trn.CREATED_DATE)) else LEAST(180,DAYS(temp.ACCOUNTING_DATE) - DAYS(trn.CREATED_DATE)) end as E,sysdate,head.bill_no||decode(head.BILL_CATEGORY,'2','-Regular','3','-Supplementary',15,'-Suspension',16,'-Arrears',99,'-Nill Bill')||'- ('||VARCHAR_FORMAT(head.CREATED_DATE, 'DD/MM/YYYY')||')' FROM "); 
		sb.append("PAYBILL_HEAD_MPG head left join HR_PAY_VOUCHER_DTLS_TEMP temp on head.AUTH_NO=temp.AUTHORIZATION_NO left join HR_PAY_VOUCHER_DTLS_TEMP_HIST temp1 on head.AUTH_NO=temp1.AUTHORIZATION_NO inner join TRN_IFMS_BEAMS_INTEGRATION trn on head.paybill_id=trn.paybill_id "); 
		sb.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=head.LOC_ID inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code "); 
		sb.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)"); 
		sb.append("where head.PAYBILL_YEAR='"+lStrYearId+"' and head.PAYBILL_MONTH='"+lStrMonthId+"' and substr(ddo.ddo_code,1,2)=substr('"+subTreasury+"',1,2) and head.APPROVE_FLAG=1 and ddo.DDO_CODE='"+lstrddoCode+"' "); 
		sb.append("and  head.PAYBILL_ID in(SELECT distinct(paybill.paybill_grp_id) FROM NSDL_PAYBILL_DATA Paybill join org_ddo_mst org on Paybill.LOC_ID=org.LOCATION_CODE"
				+ " where paybill.PAYBILL_YEAR='"+lStrYearId+"' AND paybill.PAYBILL_MONTH='"+lStrMonthId+"' and org.DDO_CODE='"+lstrddoCode+"' and paybill.dcps+paybill.dcps_pay+paybill.dcps_da+paybill.dcps_delay+paybill.ded_adjust+paybill.EMPLR_CONTRI_ARREARS+paybill.DCPS_PAY_DIFF >0 ) ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		notApprovedPaybills= lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return notApprovedPaybills;
	}


private int getTreasuryWisePaybillsCoveredNps(String ddoCode,
			String lStrYearId, String lStrMonthId) {
	StringBuilder sb = new StringBuilder();
	List paybillCoveredNps=null;
	HashMap chk;
	try
	{
		
		sb.append("SELECT paybill.PAYBILL_GRP_ID,emp.pran_no,sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt "); 
		sb.append("FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID "); 
		sb.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID inner join PAYBILL_HEAD_MPG head on paybill.PAYBILL_GRP_ID=head.PAYBILL_ID "); 
		sb.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=head.LOC_ID inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code "); 
		sb.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
		sb.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4) ");
		sb.append("where head.PAYBILL_YEAR='"+lStrYearId+"' and head.PAYBILL_MONTH='"+lStrMonthId+"' and substr(ddo.ddo_code,1,4)=substr('"+ddoCode+"',1,4) and head.APPROVE_FLAG=1 and emp.pran_no is not null and paybill.dcps+paybill.dcps_pay+paybill.dcps_da+paybill.dcps_delay+paybill.ded_adjust+paybill.EMPLR_CONTRI_ARREARS+paybill.DCPS_PAY_DIFF >0 ");
		sb.append("and reg.DDO_REG_NO in(SELECT DDO_REG_NO FROM mst_ddo_reg where substr(ddo_code,1,4)=substr('"+ddoCode+"',1,4)) ");
		sb.append("group by paybill.PAYBILL_GRP_ID,emp.pran_no order by paybill.PAYBILL_GRP_ID ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		paybillCoveredNps= lQuery.list();
		
		String prevPaybillId="AAA";
		String paybillId = null;
		chk=new HashMap();
		 
		        for (Iterator it = paybillCoveredNps.iterator(); it.hasNext();)
		        { 
		          Object[] lObj = (Object[])it.next();
		          paybillId = lObj[0].toString();
		          
		          if (!prevPaybillId.equals(paybillId)){
		        	    sb = new StringBuilder();
			        	List checkNps=null;
			        	
			        	sb.append("SELECT cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,sd.SD_PRAN_NO,bh.file_name "); 
			      		sb.append("FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME "); 
			      		sb.append(" and bh.STATUS <>-1 and bh.YEAR='"+lStrYearId+"' and bh.MONTH='"+lStrMonthId+"' and sd.SD_PRAN_NO='"+lObj[1].toString()+"' "); 
			      		sb.append("and bh.file_name like '" + ddoCode.substring(0, 2) + "%' and bh.file_name not like '%D' "); 
			      		//sb.append("and cast(sd.SD_EMP_AMOUNT as bigint) <= cast(sd.SD_EMPlr_AMOUNT as bigint) ");
			      		sb.append("group by sd.SD_PRAN_NO,bh.file_name ");
			      		
			      	    ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			      		lQuery = ghibSession.createSQLQuery(sb.toString());
			      		checkNps= lQuery.list();
			      		
			      		if(checkNps.size() > 0)
				      	prevPaybillId=paybillId;
			      		
		        	  if ((paybillId != null) && !(chk.containsKey(paybillId))&&(checkNps != null && checkNps.size() > 0)) {
		        		  
		  				for (int i = 0; i < checkNps.size(); i++) {
		  					Object[] objTest1 = (Object[]) checkNps.get(i);
		  					//System.out.println("filename-->"+objTest1[2]);
				        	  chk.put(paybillId, objTest1[2]);
		  				}  
			          }
		          }//if
		        }//iterator
	}catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return chk.size();
	}


private List getDDOWiseNpsDetails(Object object, String lStrYearId,String lStrMonthId, String lstrddoCode,String SubTreasury, Object tupleSub) {
	StringBuilder sb = new StringBuilder();
	List ApprovedPaybills=null;
	String []paybillId=object.toString().split("-");
	String paybillAprroved=tupleSub.toString();
	paybillAprroved=(paybillAprroved.substring(6, 10))+paybillAprroved.substring(3, 5)+paybillAprroved.substring(0, 2);
	System.out.println("day-->"+paybillAprroved);
	/*System.out.println("day-->"+paybillAprroved.substring(0, 2));
	System.out.println("month-->"+paybillAprroved.substring(3, 5));
	System.out.println("year-->"+paybillAprroved.substring(6, 10));*/
	
	try
	{
		sb.append("SELECT (bh.file_name)||'- ('||substr(bh.bh_date,1,2)||'/'||substr(bh.bh_date,3,2)||'/'||substr(bh.bh_date,5,4)||')'"
				+ ",substr(bh.bh_date,1,2)||'/'||substr(bh.bh_date,3,2)||'/'||substr(bh.bh_date,5,4), "
				+ " 0,0,0,0,0,0,0,0,0,0 FROM NSDL_SD_DTLS sd "); 
		sb.append("inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME "); 
		sb.append("and bh.STATUS <>-1 and bh.YEAR='"+lStrYearId+"' and bh.MONTH='"+lStrMonthId+"' and cast(substr(bh.BH_DATE,5,4)||''||substr(bh.BH_DATE,3,2)||''||substr(bh.BH_DATE,1,2)as bigint) >='"+paybillAprroved+"' "); 
		sb.append(" and bh.file_name like '"+SubTreasury.substring(0, 2)+"%' and bh.file_name not like '"+SubTreasury.substring(0, 2)+"%D' "); 
		sb.append("and sd.sd_pran_no in "); 
		sb.append("((SELECT (emp.pran_no) FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
		sb.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID=paybill.PAYBILL_GRP_ID ");
		sb.append("inner join org_ddo_mst org on head.LOC_ID=org.LOCATION_CODE ");
		sb.append("where paybill.PAYBILL_YEAR='"+lStrYearId+"' and paybill.PAYBILL_GRP_ID='"+paybillId[0].trim()+"' AND paybill.PAYBILL_MONTH='"+lStrMonthId+"' and org.DDO_CODE='"+lstrddoCode+"' and paybill.dcps+paybill.dcps_pay+paybill.dcps_da+paybill.dcps_delay+paybill.ded_adjust+paybill.EMPLR_CONTRI_ARREARS+paybill.DCPS_PAY_DIFF >0 ");
		sb.append("and emp.PRAN_NO is not NULL and emp.PRAN_ACTIVE=1 and emp.REG_STATUS=1 and head.APPROVE_FLAG=1)FETCH first 50 ROW only)order by substr(bh.bh_date,5,4),substr(bh.bh_date,3,2),substr(bh.bh_date,0,2) FETCH first ROW only ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		ApprovedPaybills= lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return ApprovedPaybills;
	}


private List getDDOWiseApprovedPaybillsDetails(String subTreasury,
			String lStrYearId, String lStrMonthId, String lstrddoCode) {
	StringBuilder sb = new StringBuilder();
	List ApprovedPaybills=null;
	try
	{
		sb.append("SELECT head.PAYBILL_ID||'- ('||VARCHAR_FORMAT(head.CREATED_DATE, 'DD/MM/YYYY')||')',VARCHAR_FORMAT(trn.CREATED_DATE, 'DD/MM/YYYY'),"
				+ " case when temp.ACCOUNTING_DATE is null then VARCHAR_FORMAT(temp1.ACCOUNTING_DATE,'DD/MM/YYYY') else VARCHAR_FORMAT(temp.ACCOUNTING_DATE,'DD/MM/YYYY') end,LEAST(180, DAYS(trn.CREATED_DATE) - DAYS(head.CREATED_DATE)) as D,case when temp.ACCOUNTING_DATE is null then LEAST(180,DAYS(temp1.ACCOUNTING_DATE) - DAYS(trn.CREATED_DATE)) else LEAST(180,DAYS(temp.ACCOUNTING_DATE) - DAYS(trn.CREATED_DATE)) end as E ,head.bill_no||decode(head.BILL_CATEGORY,'2','-Regular','3','-Supplementary',15,'-Suspension',16,'-Arrears',99,'-Nill Bill')||'- ('||VARCHAR_FORMAT(head.CREATED_DATE, 'DD/MM/YYYY')||')' FROM "); 
		sb.append("PAYBILL_HEAD_MPG head left join HR_PAY_VOUCHER_DTLS_TEMP temp on head.AUTH_NO=temp.AUTHORIZATION_NO left join HR_PAY_VOUCHER_DTLS_TEMP_HIST temp1 on head.AUTH_NO=temp1.AUTHORIZATION_NO inner join TRN_IFMS_BEAMS_INTEGRATION trn on head.paybill_id=trn.paybill_id "); 
		sb.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=head.LOC_ID inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code "); 
		sb.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)"); 
		sb.append("where head.PAYBILL_YEAR='"+lStrYearId+"' and head.PAYBILL_MONTH='"+lStrMonthId+"' and substr(ddo.ddo_code,1,2)=substr('"+subTreasury+"',1,2) and head.APPROVE_FLAG=1 and ddo.DDO_CODE='"+lstrddoCode+"' "); 
		sb.append("and  head.PAYBILL_ID in(SELECT distinct(paybill.paybill_grp_id) FROM NSDL_PAYBILL_DATA Paybill join org_ddo_mst org on Paybill.LOC_ID=org.LOCATION_CODE"
				+ " where paybill.PAYBILL_YEAR='"+lStrYearId+"' AND paybill.PAYBILL_MONTH='"+lStrMonthId+"' and org.DDO_CODE='"+lstrddoCode+"' and paybill.dcps+paybill.dcps_pay+paybill.dcps_da+paybill.dcps_delay+paybill.ded_adjust+paybill.EMPLR_CONTRI_ARREARS+paybill.DCPS_PAY_DIFF >0 ) ");
		//sb.append("group by reg.ddo_code,reg.DDO_OFFICE_NAME,reg.ddo_reg_no ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		ApprovedPaybills= lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return ApprovedPaybills;
	}


private int getDDOWisePaybillsCoveredNps(String ddoCode, String lStrYearId,
			String lStrMonthId) {
	StringBuilder sb = new StringBuilder();
	List paybillCoveredNps=null;
	HashMap chk;
	try
	{
		
		sb.append("SELECT paybill.PAYBILL_GRP_ID,emp.pran_no,sum(paybill.DCPS)+sum(paybill.DCPS_PAY)+sum(paybill.DCPS_DELAY)+sum(paybill.DCPS_DA)+sum(paybill.DCPS_PAY_DIFF) as emp_amt "); 
		sb.append("FROM mst_dcps_emp emp inner join HR_EIS_EMP_MST eis on eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID "); 
		sb.append("inner join NSDL_PAYBILL_DATA paybill on paybill.EMP_ID=eis.EMP_ID inner join PAYBILL_HEAD_MPG head on paybill.PAYBILL_GRP_ID=head.PAYBILL_ID "); 
		sb.append("inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=head.LOC_ID inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code "); 
		sb.append("inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) ");
		sb.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4) ");
		//sb.append("where head.PAYBILL_YEAR='"+lStrYearId+"' and head.PAYBILL_MONTH='"+lStrMonthId+"' and substr(ddo.ddo_code,1,2)=substr('"+ddoCode+"',1,2) and head.APPROVE_FLAG=1 and emp.pran_no is not null ");
		sb.append("where head.PAYBILL_YEAR='"+lStrYearId+"' and head.PAYBILL_MONTH='"+lStrMonthId+"' and ddo.ddo_code='"+ddoCode+"' and head.APPROVE_FLAG=1 and emp.pran_no is not null and paybill.dcps+paybill.dcps_pay+paybill.dcps_da+paybill.dcps_delay+paybill.ded_adjust+paybill.EMPLR_CONTRI_ARREARS+paybill.DCPS_PAY_DIFF >0 ");
		sb.append("and reg.DDO_REG_NO =(SELECT DDO_REG_NO FROM mst_ddo_reg where ddo_code='"+ddoCode+"')  ");//2201000343
		//sb.append("and reg.DDO_REG_NO in('SGV194513G')  ");
		sb.append("group by paybill.PAYBILL_GRP_ID,emp.pran_no order by paybill.PAYBILL_GRP_ID ");
		
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(sb.toString());
		paybillCoveredNps= lQuery.list();
		
		String prevPaybillId="AAA";
		String paybillId = null;
		chk=new HashMap();
		 
		        for (Iterator it = paybillCoveredNps.iterator(); it.hasNext();)
		        { 
		          Object[] lObj = (Object[])it.next();
		          paybillId = lObj[0].toString();
		          
		          if (!prevPaybillId.equals(paybillId)){  
		        	    sb = new StringBuilder();
			        	List checkNps=null;

			        	sb.append("SELECT cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt,sd.SD_PRAN_NO,bh.file_name "); 
			      		sb.append("FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME "); 
			      		sb.append(" and bh.STATUS <>-1 and bh.YEAR='"+lStrYearId+"' and bh.MONTH='"+lStrMonthId+"' and sd.SD_PRAN_NO='"+lObj[1].toString()+"' "); 
			      		sb.append("and bh.file_name like '" + ddoCode.substring(0, 2) + "%' and bh.file_name not like '%D' "); 
			      		//sb.append("and cast(sd.SD_EMP_AMOUNT as bigint) <= cast(sd.SD_EMPlr_AMOUNT as bigint) ");
			      		sb.append("group by sd.SD_PRAN_NO,bh.file_name ");
			      		
			      	    ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			      		lQuery = ghibSession.createSQLQuery(sb.toString());
			      		checkNps= lQuery.list();
			      		
			      		if(checkNps.size() > 0)
			      		prevPaybillId=paybillId;
			      		
			      		if ((paybillId != null) && !(chk.containsKey(paybillId))&&(checkNps != null && checkNps.size() > 0)) {
		  				for (int i = 0; i < checkNps.size(); i++) {
		  					Object[] objTest1 = (Object[]) checkNps.get(i);
		  					//System.out.println("filename-->"+objTest1[2]);
				        	  chk.put(paybillId, objTest1[2]);
		  				}  
			          }
		          }//if
		        }//for iterator
	}catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return chk.size();
		
}


//get Treasury Wise Pending Details

	private List getTreasuryWisePendingDtls(String lStrYearId,String subTreasury,String lStrMonthId) {
		StringBuilder sb = new StringBuilder();
		List SevarthIdList=null;
		try
		{
			sb.append(" SELECT ");
			sb.append(" loc.loc_id,loc.loc_name,count(head.PAYBILL_ID) ");
			sb.append(" FROM PAYBILL_HEAD_MPG head ");
			sb.append(" inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=head.LOC_ID ");
			sb.append(" inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			sb.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4) ");
			sb.append(" where head.PAYBILL_YEAR='"+lStrYearId+"' ");
			sb.append(" and head.PAYBILL_MONTH='"+lStrMonthId+"' ");
			sb.append(" and loc.loc_id='"+subTreasury+"' ");
			sb.append(" and head.APPROVE_FLAG=1 ");
			sb.append(" and head.PAYBILL_ID in ");
			sb.append(" (SELECT ");
			sb.append(" distinct(paybill.paybill_grp_id) ");
			sb.append(" FROM NSDL_PAYBILL_DATA Paybill ");
			sb.append(" where paybill.PAYBILL_YEAR='"+lStrYearId+"' ");
			sb.append(" AND paybill.PAYBILL_MONTH='"+lStrMonthId+"' and paybill.dcps+paybill.dcps_pay+paybill.dcps_da+paybill.dcps_delay+paybill.ded_adjust+paybill.EMPLR_CONTRI_ARREARS+paybill.DCPS_PAY_DIFF >0   ) ");
			sb.append(" group by loc.loc_id,loc.loc_name ");
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(sb.toString());
			SevarthIdList= lQuery.list();
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
			return SevarthIdList;
	}
	
	private List getDDOWiseTotalApprovedPaybills(String locationId, String lStrYearId, String lStrMonthId) {
		StringBuilder sb = new StringBuilder();
		List ApprovedPaybills=null;
		try
		{
			sb.append("SELECT reg.ddo_code ||'-'||reg.DDO_OFFICE_NAME,count(head.PAYBILL_ID),reg.ddo_reg_no FROM "); 
			sb.append("PAYBILL_HEAD_MPG head inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=head.LOC_ID "); 
			sb.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(ddo.ddo_code,1,2) "); 
			sb.append("inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4) "); 
			sb.append("where head.PAYBILL_YEAR='"+lStrYearId+"' and head.PAYBILL_MONTH='"+lStrMonthId+"' and substr(ddo.ddo_code,1,4)=substr('"+locationId+"',1,4) and head.APPROVE_FLAG=1  "); //and reg.ddo_reg_no in('SGV200230G','SGV194513G')
			sb.append("and  head.PAYBILL_ID in(SELECT distinct(paybill.paybill_grp_id) FROM NSDL_PAYBILL_DATA Paybill where paybill.PAYBILL_YEAR='"+lStrYearId+"' AND paybill.PAYBILL_MONTH='"+lStrMonthId+"' and paybill.dcps+paybill.dcps_pay+paybill.dcps_da+paybill.dcps_delay+paybill.ded_adjust+paybill.EMPLR_CONTRI_ARREARS+paybill.DCPS_PAY_DIFF >0 ) ");
			sb.append("group by reg.ddo_code,reg.DDO_OFFICE_NAME,reg.ddo_reg_no ");
			
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(sb.toString());
			ApprovedPaybills= lQuery.list();
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
			return ApprovedPaybills;
	}
	
	
	private List getEmpWisePendingDtls(String locationId, String lStrYearId) {
		StringBuilder sb = new StringBuilder();
		List SevarthIdList=null;
		try
		{
			sb.append("select n.PAYBILL_YEAR,n.PAYBILL_MONTH,head.VOUCHER_NO,varchar_format(head.VOUCHER_DATE,'DD/MM/YYYY'),look.LOOKUP_NAME,varchar_format(t.STARTDATE,'DD/MM/YYYY'),varchar_format(t.ENDDATE,'DD/MM/YYYY'), "); 
			sb.append("cast(case when t.TYPE_OF_PAYMENT= 700046 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700047 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700049 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700048 then t.CONTRIBUTION else 0 end as bigint), cast(nvl( t.CONTRIBUTION_EMPLR,0)as bigint), "); 
			sb.append("n.DCPS +n.DCPS_PAY+n.DCPS_DA+n.DCPS_DELAY,n.DED_ADJUST,' Paybill Month :'||n.PAYBILL_MONTH||' EMP NAME :'||' '|| em.EMP_NAME ||' PRAN NO :  '||em.pran_no ||' SEVAARTH ID : '||em.sevarth_id  "); 
			sb.append(" ,cast(t.CONTRIBUTION as bigint),decode(head.BILL_CATEGORY,2,'Regular',3,'Supplimentory',15,'Suspension',16,'Arrearas',99,'Nill Bill' ),' Employee Contri(AS Per paybill):'|| (n.DCPS +n.DCPS_PAY+n.DCPS_DA+n.DCPS_DELAY) ||' Employeer Contri(AS Per paybill):'||n.DED_ADJUST,n.PAYBILL_GRP_ID,em.sevarth_id,head.BILL_NO,varchar_format(head.CREATED_DATE,'DD/MM/YYYY')  from TRN_DCPS_CONTRIBUTION t "); 
			sb.append("join PAYBILL_HEAD_MPG head on head.BILL_NO = t.BILL_GROUP_ID  and head.APPROVE_FLAG=1 and head.PAYBILL_YEAR = '"+lStrYearId+"' "); 
			sb.append("join NSDL_PAYBILL_DATA n on n.PAYBILL_GRP_ID =head.PAYBILL_ID and n.PAYBILL_YEAR ='"+lStrYearId+"' "); 
			sb.append("join MST_DCPS_EMP em on em.DCPS_EMP_ID =t.DCPS_EMP_ID   join HR_EIS_EMP_MST hr on hr.emp_mpg_id=em.ORG_EMP_MST_ID and hr.EMP_ID =n.EMP_ID  "); 
			sb.append("join ORG_DDO_MST ddo on   head.loc_id =ddo.location_code  and  ddo.DDO_CODE='"+locationId+"'  "); 
			sb.append("join CMN_LOOKUP_MST look on look.LOOKUP_ID =t.TYPE_OF_PAYMENT join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_CODE='"+lStrYearId+"' and ((t.MONTH_ID  in (1,2,3) and t.FIN_YEAR_ID=fin.FIN_YEAR_ID-1) or (t.MONTH_ID not in (1,2,3) and t.FIN_YEAR_ID=fin.FIN_YEAR_ID)) "); 
			sb.append("where   t.CONTRIBUTION <>0  and   (n.dcps+n.dcps_pay+n.dcps_delay+n.dcps_da)>(n.DED_ADJUST) and head.PAYBILL_MONTH=n.PAYBILL_MONTH and n.PAYBILL_MONTH =t.MONTH_ID order by n.PAYBILL_MONTH ,n.EMP_ID,n.PAYBILL_GRP_ID ");

			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(sb.toString());
			SevarthIdList= lQuery.list();
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
			return SevarthIdList;
	}
	
	
	private List getEmpWisePendingDtlsCount(String locationId, String lStrYearId,String sevaarthID) {
		StringBuilder sb = new StringBuilder();
		List SevarthIdList=null;
		try
		{
			sb.append("select n.PAYBILL_YEAR,n.PAYBILL_MONTH,head.VOUCHER_NO,varchar_format(head.VOUCHER_DATE,'DD/MM/YYYY'),look.LOOKUP_NAME,varchar_format(t.STARTDATE,'DD/MM/YYYY'),varchar_format(t.ENDDATE,'DD/MM/YYYY'), "); 
			sb.append("cast(case when t.TYPE_OF_PAYMENT= 700046 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700047 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700049 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700048 then t.CONTRIBUTION else 0 end as bigint), cast(nvl( t.CONTRIBUTION_EMPLR,0)as bigint), "); 
			sb.append("n.DCPS +n.DCPS_PAY+n.DCPS_DA+n.DCPS_DELAY,n.DED_ADJUST,' Paybill Month :'||n.PAYBILL_MONTH||' EMP NAME :'||' '|| em.EMP_NAME ||' PRAN NO :  '||em.pran_no ||' SEVAARTH ID : '||em.sevarth_id  "); 
			sb.append(" ,cast(t.CONTRIBUTION as bigint),head.BILL_CATEGORY,' Employee Contri(AS Per paybill):'|| (n.DCPS +n.DCPS_PAY+n.DCPS_DA+n.DCPS_DELAY) ||' Employeer Contri(AS Per paybill):'||n.DED_ADJUST,n.PAYBILL_GRP_ID,em.sevarth_id,head.paybill_id,varchar_format(head.CREATED_DATE,'DD/MM/YYYY')  from TRN_DCPS_CONTRIBUTION t "); 
			sb.append("join PAYBILL_HEAD_MPG head on head.BILL_NO = t.BILL_GROUP_ID  and head.APPROVE_FLAG=1 and head.PAYBILL_YEAR = '"+lStrYearId+"' "); 
			sb.append("join NSDL_PAYBILL_DATA n on n.PAYBILL_GRP_ID =head.PAYBILL_ID and n.PAYBILL_YEAR ='"+lStrYearId+"' "); 
			sb.append("join MST_DCPS_EMP em on em.sevarth_id='"+sevaarthID+"' and  em.DCPS_EMP_ID =t.DCPS_EMP_ID   join HR_EIS_EMP_MST hr on hr.emp_mpg_id=em.ORG_EMP_MST_ID and hr.EMP_ID =n.EMP_ID  "); 
			sb.append("join ORG_DDO_MST ddo on   head.loc_id =ddo.location_code  and  ddo.DDO_CODE='"+locationId+"'  "); 
			sb.append("join CMN_LOOKUP_MST look on look.LOOKUP_ID =t.TYPE_OF_PAYMENT join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_CODE='"+lStrYearId+"' and ((t.MONTH_ID  in (1,2,3) and t.FIN_YEAR_ID=fin.FIN_YEAR_ID-1) or (t.MONTH_ID not in (1,2,3) and t.FIN_YEAR_ID=fin.FIN_YEAR_ID)) "); 
			sb.append("where   t.CONTRIBUTION <>0  and   (n.dcps+n.dcps_pay+n.dcps_delay+n.dcps_da)>(n.DED_ADJUST) and head.PAYBILL_MONTH=n.PAYBILL_MONTH and n.PAYBILL_MONTH =t.MONTH_ID order by n.PAYBILL_MONTH ,n.EMP_ID,n.PAYBILL_GRP_ID ");

			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(sb.toString());
			SevarthIdList= lQuery.list();
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
			return SevarthIdList;
	}
	
	
	

	public List getTreasuryWiseDtls(Long locationId) throws Exception{

		
		StringBuilder lSBQuery = new StringBuilder();
		List SevarthIdList=null;
		
		try
		{
			lSBQuery.append(" SELECT case when l.DEPARTMENT_ID=100006 then l1.LOC_NAME  else m.loc_name end,m.loc_name,cast(l.loc_id as integer),cast(m.PAYBILL_YEAR as integer),month.month_name, ");
			lSBQuery.append(" cast(m.EMPLOYEE_TOTAL as bigint),cast(m.DEDUCT_ADJUST as bigint),cast(m.EMPLOYEE_TOTAL+m.DEDUCT_ADJUST as bigint),to_char(m.REFRESH_DATE,'dd-MM-yyyy')  FROM nps_mqt m ");
			lSBQuery.append(" inner join CMN_LOCATION_MST l on l.LOC_ID=m.loc_ID inner join CMN_LOCATION_MST l1 on l1.LOC_ID=l.PARENT_LOC_ID "); 
			lSBQuery.append(" inner join SGVA_MONTH_MST month on month.month_id=m.PAYBILL_MONTH and month.lang_id='en_US' "); 
			
				lSBQuery
				.append(" and (l.LOC_ID= "); 
				lSBQuery
				.append(locationId); 
				lSBQuery
				.append(" or l.PARENT_LOC_ID= "); 
				lSBQuery
				.append(locationId); 
				lSBQuery
				.append(" ) order by m.PAYBILL_YEAR,m.paybill_month "); 
						
			
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery
			.toString());
			SevarthIdList= lQuery.list();
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
			
			
			return SevarthIdList;
	
		}

			
			
public String getFourPercentData(String  SEVAARTH_ID,String month) throws Exception{

//	 APRIL MAY  JUNE  JULY AUGUST SEPTEMBER,OCTOBER 
		StringBuilder lSBQuery = new StringBuilder();
		List SevarthIdList=null;
		String ddoCode="-";
		try
		{
			lSBQuery.append(" select "  +month+" from MST_DCPS_EMPLR_CONTRI_ARREARS where SEVAARTH_ID = '"+SEVAARTH_ID+"' "); 
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery
			.toString());
			SevarthIdList= lQuery.list();
			if(SevarthIdList!=null && SevarthIdList.size()>0 && SevarthIdList.get(0)!=null)
			{
				ddoCode=SevarthIdList.get(0).toString();
			}
		}
		catch(Exception e){
			gLogger.error(" Error in getDDOCode " + e, e);
			e.printStackTrace();
			throw e;
		}
			
			
			return ddoCode;
	
		}
	
			
	
public String getDDOCode(Long userId) throws Exception{

		
		StringBuilder lSBQuery = new StringBuilder();
		List SevarthIdList=null;
		String ddoCode=null;
		try
		{
			lSBQuery.append(" SELECT mst.DDO_CODE FROM ORG_DDO_MST mst inner join ORG_USERPOST_RLT u on u.POST_ID=mst.POST_ID "); 
			
				lSBQuery
				.append(" where u.USER_ID="); 
				lSBQuery
				.append(userId); 
				
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery
			.toString());
			SevarthIdList= lQuery.list();
			if(SevarthIdList!=null && SevarthIdList.size()>0 && SevarthIdList.get(0)!=null)
			{
				ddoCode=SevarthIdList.get(0).toString();
			}
		}
		catch(Exception e){
			gLogger.error(" Error in getDDOCode " + e, e);
			e.printStackTrace();
			throw e;
		}
			
			
			return ddoCode;
	
		}
	


//for Parameters
public List getAllSubTreasury(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

	Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
	Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
	String lStrLocationId = null;
	if (loginMap.containsKey("locationId")) {
		lStrLocationId = loginMap.get("locationId").toString();
	}
	List<Object> lLstReturnList = null;
	SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    Session neardrSession = sessionFactory1.openSession();
	try {
		StringBuilder sb = new StringBuilder();
		Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
		sb.append(" select  LOCATION_CODE ,LOCATION_CODE||','||LOC_NAME FROM CMN_LOCATION_MST where LOCATION_CODE= '"+lStrLocationId+"'  order by LOCATION_CODE ");
		Query selectQuery = neardrSession.createSQLQuery(sb.toString());
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		}
	} catch (Exception e) {
		gLogger.error("Error is : " + e, e);
	}
	finally{
		neardrSession.close();
	}
	return lLstReturnList;
}



//for second para
public List getYear(String YearId, String lStrLocId,String Year) {

	List<Object> lArrYears = new ArrayList<Object>();
	SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    Session neardrSession = sessionFactory1.openSession();
	try {
		String lStrBufLang = "select FIN_YEAR_CODE,FIN_YEAR_CODE from SGVC_FIN_YEAR_MST where LANG_ID='en_US' and FIN_YEAR_CODE between '2015' and '2021' order by FIN_YEAR_CODE";

		Query lObjQuery = neardrSession.createSQLQuery(lStrBufLang);

		List lLstResult = lObjQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		Object[] lArrData = null;

		if (lLstResult != null && !lLstResult.isEmpty()) {
			for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
				
				lObjComboValuesVO = new ComboValuesVO();
				lArrData = (Object[]) lLstResult.get(lIntCtr);
				lObjComboValuesVO.setId(lArrData[0].toString());
				lObjComboValuesVO.setDesc(lArrData[1].toString());
				lArrYears.add(lObjComboValuesVO);
			}
		}
	} catch (Exception e) {
		gLogger.error("Error is : " + e, e);
	}
	finally{
		neardrSession.close();
	}

	return lArrYears;
}

public List getMonth(String YearId, String lStrLocId,String Year) {

	List<Object> lArrYears = new ArrayList<Object>();
	SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    Session neardrSession = sessionFactory1.openSession();
	try {
		String lStrBufLang = "select month_Id,month_Name from Sgva_Month_Mst where month_Id < 13";

		Query lObjQuery = neardrSession.createSQLQuery(lStrBufLang);

		List lLstResult = lObjQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		Object[] lArrData = null;

		if (lLstResult != null && !lLstResult.isEmpty()) {
			for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
				
				lObjComboValuesVO = new ComboValuesVO();
				lArrData = (Object[]) lLstResult.get(lIntCtr);
				lObjComboValuesVO.setId(lArrData[0].toString());
				lObjComboValuesVO.setDesc(lArrData[1].toString());
				lArrYears.add(lObjComboValuesVO);
			}
		}
	} catch (Exception e) {
		gLogger.error("Error is : " + e, e);
	}
	finally{
		neardrSession.close();
	}

	return lArrYears;
}


public String space(int noOfSpace) {	
    String blank = "";	
    for(int i = 0; i < noOfSpace; ++i) {	
       blank = blank + " ";	
    }	
    return blank;
}
}
