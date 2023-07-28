package com.tcs.sgv.dcps.report;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;











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
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

public class PendingLessEmployerConribution   extends DefaultReportDataFinder implements ReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(PendingLessEmployerConribution.class);
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
			if (report.getReportCode().equals("9000522")) 
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
				 String SubTreasury = (String) report.getParameterValue("subtreasuryCode");
			
				List TableData1= getTreasuryWisePendingDtls(locationId,lStrYearId,SubTreasury,Location_code);
				for (int i=0;i<TableData1.size();i++)
				{
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
					td.add(i+1);
					if (tupleSub[0] != null) 
					{
							td.add(SubTreasury);
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
							td.add(tupleSub[2].toString());
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[3] != null) 
					{

						td.add(new URLData(tupleSub[3] ,"hrms.htm?actionFlag=reportService&reportCode=9000523&action=generateReport&DirectReport=TRUE&displayOK=FALSE&finYear="+lStrYearId+"&treasuryCode="+SubTreasury));

					}
					else
					{
							td.add("-");
					}
				
					tabledata.add(td);
				}
				
				
				
	          }
			 if (report.getReportCode().equals("9000523")) {

				SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");	
	            Date date = new Date();	
	            String curDate = dateFormat.format(date);	
	            gLogger.info("=====Innside parent report=====");	
	            String header5 = this.space(5) + "Date :- " + this.space(2) + curDate;	
	            report.setAdditionalHeader(header5);
	            
	            
	            
	            
	            
				 
				List TableData = null;
			
				StringBuilder SBQuery = new StringBuilder();
				 String lStrYearId = (String) report.getParameterValue("finYear");
				 String SubTreasury = (String) report.getParameterValue("treasuryCode");
			
				List TableData1= getDDOWisePendingDtls(SubTreasury,lStrYearId);
				for (int i=0;i<TableData1.size();i++)
				{
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
					td.add(i+1);
					if (tupleSub[0] != null) 
					{
							td.add(SubTreasury);
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
							td.add(tupleSub[2].toString());
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[3] != null) 
					{
						
						td.add(tupleSub[3].toString());

					}
					else
					{
							td.add("-");
					}
					
					if (tupleSub[4] != null) 
					{
						
						td.add(tupleSub[4].toString());


					}
					else
					{
							td.add("-");
					}
					
					if (tupleSub[5] != null) 
					{
						
						td.add(new URLData(tupleSub[5] ,"hrms.htm?actionFlag=reportService&reportCode=9000524&action=generateReport&DirectReport=TRUE&displayOK=FALSE&finYear="+lStrYearId+"&ddoCode="+tupleSub[1]));


					}
					else
					{
							td.add("-");
					}
				
					tabledata.add(td);
				}
	          
			}
			 
			 if (report.getReportCode().equals("9000524")) {

				SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");	
	            Date date = new Date();	
	            String curDate = dateFormat.format(date);	
	            gLogger.info("=====Innside parent report=====");	
	            String header5 = this.space(5) + "Date :- " + this.space(2) + curDate;	
//	            report.setAdditionalHeader(header5);
	            
	            
	            
	            StyleVO[] CenterBoldAlignVO = new StyleVO[5];
  				CenterBoldAlignVO[0] = new StyleVO();
  				CenterBoldAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
  				CenterBoldAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
  				CenterBoldAlignVO[1] = new StyleVO();
  				CenterBoldAlignVO[1].setStyleId(IReportConstants.BORDER);
  				CenterBoldAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
  				CenterBoldAlignVO[2] = new StyleVO();
  				CenterBoldAlignVO[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
  				CenterBoldAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
  				CenterBoldAlignVO[3] = new StyleVO();
  				CenterBoldAlignVO[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
  				CenterBoldAlignVO[3].setStyleValue("15");
  				CenterBoldAlignVO[4] = new StyleVO();
  				CenterBoldAlignVO[4].setStyleId(IReportConstants.STYLE_FONT_COLOR);
  				CenterBoldAlignVO[4].setStyleValue("red");
  				
  				
  				StyleVO[] leftlignVO11 = new StyleVO[4];
  				leftlignVO11[0] = new StyleVO();
  				leftlignVO11[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
  				leftlignVO11[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
  				leftlignVO11[1] = new StyleVO();
  				leftlignVO11[1].setStyleId(IReportConstants.BORDER);
  				leftlignVO11[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
  				leftlignVO11[2] = new StyleVO();
  				leftlignVO11[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_NORMAL);
  				leftlignVO11[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
  				leftlignVO11[3] = new StyleVO();
  				leftlignVO11[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
  				leftlignVO11[3].setStyleValue("14");
	            
					ArrayList<Object> dataList11 = new ArrayList<Object>();
		  			 ArrayList<Object> rowList = new ArrayList<Object>();

  				 rowList = new ArrayList<Object>();
   				rowList.add(new StyledData(header5,leftlignVO11));
   				dataList11.add(rowList);
   				
   				rowList = new ArrayList<Object>();
   				rowList.add(new StyledData("(Note: Please prepare a single supplementary Nil bill for appears employees as per amount displayed in 'Adjustable Employer Contribution' column)",CenterBoldAlignVO));
   				dataList11.add(rowList);
   				
   				TabularData tData  = new TabularData(dataList11);				
  				tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
  				tData.addStyle(IReportConstants.BORDER, "No");


  				report.setAdditionalHeader(tData);
	            
				 
				List TableData = null;
			
				StringBuilder SBQuery = new StringBuilder();
				 String lStrYearId = (String) report.getParameterValue("finYear");
				 String SubTreasury = (String) report.getParameterValue("ddoCode");
				 String temp="NA";
				 int j=1;
			
				List TableData1= getEmpWisePendingDtls(SubTreasury,lStrYearId);
				Double remaining=0D;
				
				for (int i=0;i<TableData1.size();i++)
				{
//					String fourPercent="0";
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
//					td.add(i+1);
					
					if(tupleSub[19].toString().equalsIgnoreCase("DGPMRRM8101"))
					{
						int a=100;
					}
					
					//for sr.no
					if(!temp.equalsIgnoreCase(tupleSub[14].toString()+tupleSub[17].toString())  ||  temp.equalsIgnoreCase("N/A") )
					{
					 j=1;
					td.add(j);
					j++;
					remaining=0D;
					//for remaining first iteration
                      String FromDate  =tupleSub[5].toString();
                      int FromDateyear=Integer.parseInt(FromDate.substring(6, 10));
                      int FromDateMonth=Integer.parseInt(FromDate.substring(3, 5));
	        		
	        		  String ToDate  =tupleSub[6].toString();
		        		int ToDateyear=Integer.parseInt(ToDate.substring(6, 10));
		        		int ToDateMonth=Integer.parseInt(ToDate.substring(3, 5));
					
					
					Long contribution=Long.parseLong(tupleSub[7].toString())+Long.parseLong(tupleSub[8].toString())+Long.parseLong(tupleSub[9].toString())+Long.parseLong(tupleSub[10].toString());
					
					//for pay arrear 
					if("Pay Arrear".equalsIgnoreCase(tupleSub[4].toString()) && !tupleSub[22].toString().equalsIgnoreCase("1") )
					{
						Double Diff=Double.parseDouble(tupleSub[22].toString());
//						if(Diff>=2)
//							Diff=Double.parseDouble(tupleSub[22].toString())-1;
						Double Newcontribution=(double) (contribution/Diff);
						Double Per=14D;
						
						if(FromDateyear<=2018 || (FromDateyear==2019 && FromDateMonth>11 )  )
							Per=10D;
						
						String Flag="T";
						if(Integer.parseInt(FromDate.substring(6, 10))<=2018 || (Integer.parseInt(FromDate.substring(6, 10))==2019 && Integer.parseInt(FromDate.substring(3, 5))<=10 )  )
						{
							remaining=(double) (contribution)+remaining;
							Flag="F";
						}
						
						if(Flag.equalsIgnoreCase("T"))
						{
						for(int D=0;D<Diff;D++)
						{
							
							
							if(FromDateyear<=2018 )  // for  10% 
								remaining=Math.ceil(remaining+(Newcontribution*10)/10);
							else if(FromDateyear>=2020 )  // for  14% 
								remaining=Math.ceil(remaining+(Newcontribution*14)/10);
							else if((FromDateyear==2019 && FromDateMonth<=3 ) )  // for  14% 
								remaining=Math.ceil(remaining+(Newcontribution*10)/10);
							else if((FromDateyear==2019 && FromDateMonth>=11 ) )  // for  14% 
								remaining=Math.ceil(remaining+(Newcontribution*Per)/10);
							else if((FromDateyear==2019 && FromDateMonth>=4 ) )  // for  14% 
								remaining=Math.ceil(remaining+(Newcontribution*Per)/10);
							FromDateMonth++;
							
							if(FromDateMonth==13)
							{
							FromDateMonth=01;
							FromDateyear= FromDateyear+1;
							}
							
						}
						}
						
						 Flag="T";
						
					}
					else
					{
						Double contribution1=(double)contribution;
					if(FromDateyear<=2018 && ToDateyear<=2018)  // for  10% 
						remaining=Math.ceil(remaining+(contribution1*10)/10);
					else if(FromDateyear>=2020 && ToDateyear>=2020)  // for  14% 
						remaining=Math.ceil(remaining+(contribution1*14)/10);
					else if((FromDateyear==2019 && FromDateMonth<=4 ) )  // for  14% 
						remaining=Math.ceil(remaining+(contribution1*10)/10);
					else if((FromDateyear==2019 && FromDateMonth>=11 ) )  // for  14% 
						remaining=Math.ceil(remaining+(contribution1*14)/10);
					else if((FromDateyear==2019 && FromDateMonth>=5 ) )  // for  14% 
						remaining=Math.ceil(remaining+(contribution1*10)/10);
					//else if 
					}
					}
					else
					{
						td.add(j);
//						td.add("0");
						j++;
						
						
						String FromDate  =tupleSub[5].toString();
	                      int FromDateyear=Integer.parseInt(FromDate.substring(6, 10));
	                      int FromDateMonth=Integer.parseInt(FromDate.substring(3, 5));
		        		
		        		  String ToDate  =tupleSub[6].toString();
			        		int ToDateyear=Integer.parseInt(ToDate.substring(6, 10));
			        		int ToDateMonth=Integer.parseInt(ToDate.substring(3, 5));
						
						
						Long contribution=Long.parseLong(tupleSub[7].toString())+Long.parseLong(tupleSub[8].toString())+Long.parseLong(tupleSub[9].toString())+Long.parseLong(tupleSub[10].toString());
						if("Pay Arrear".equalsIgnoreCase(tupleSub[4].toString()) && !tupleSub[22].toString().equalsIgnoreCase("1") )
						{
							Double Diff=Double.parseDouble(tupleSub[22].toString());
//							if(Diff>=2)
//								Diff=Double.parseDouble(tupleSub[22].toString())-1;
							Double Newcontribution=(double) (contribution/Diff);
							String Flag="T";
							Double Per=14D;
							if(Integer.parseInt(FromDate.substring(6, 10))<=2018 || (Integer.parseInt(FromDate.substring(6, 10))==2019 && Integer.parseInt(FromDate.substring(3, 5))<=10 )  )
							{
								remaining=(double) (contribution)+remaining;
								Flag="F";
							}
							
							if(Flag.equalsIgnoreCase("T"))
							{
							for(int D=0;D<Diff;D++)
							{
								
								
								if(FromDateyear<=2018 )  // for  10% 
									remaining=Math.ceil(remaining+(Newcontribution*10)/10);
								else if(FromDateyear>=2020 )  // for  14% 
									remaining=Math.ceil(remaining+(Newcontribution*14)/10);
								else if((FromDateyear==2019 && FromDateMonth<=3 ) )  // for  14% 
									remaining=Math.ceil(remaining+(Newcontribution*10)/10);
								else if((FromDateyear==2019 && FromDateMonth>=11 ) )  // for  14% 
									remaining=Math.ceil(remaining+(Newcontribution*14)/10);
								else if((FromDateyear==2019 && FromDateMonth>=4 ) )  // for  14% 
									remaining=Math.ceil(remaining+(Newcontribution*14)/10);
								FromDateMonth++;
								
								if(FromDateMonth==13)
								{
								FromDateMonth=01;
								FromDateyear= FromDateyear+1;
								}
								
							}
						}
							Flag="T";	
							
						}
							else
							{
								Double contribution1=(double)contribution;

						if(FromDateyear<=2018 && ToDateyear<=2018)  // for  10% 
							remaining=Math.ceil(remaining+(contribution1*10)/10);
						else if(FromDateyear>=2020 && ToDateyear>=2020)  // for  14% 
							remaining=Math.ceil(remaining+(contribution1*14)/10);
						else if((FromDateyear==2019 && FromDateMonth<=3 ) )  // for  14% 
							remaining=Math.ceil(remaining+(contribution1*10)/10);
						else if((FromDateyear==2019 && FromDateMonth>=11 ) )  // for  14% 
							remaining=Math.ceil(remaining+(contribution1*14)/10);
						else if((FromDateyear==2019 && FromDateMonth>=4 ) )  // for  14% 
							remaining=Math.ceil(remaining+(contribution1*10)/10);
							}
						
						
						
					}
					//for Year
					if (tupleSub[0] != null) 
					{
						td.add(tupleSub[0].toString());
						
					}
					else
					{
							td.add("-");
					}
					//for Month
					if (tupleSub[1] != null) 
					{
							td.add(tupleSub[1]);
					}
					else
					{
							td.add("-");
					}
					//for Bill NO
//					if (tupleSub[18] != null) 
//					{
//							td.add(tupleSub[18]);
//					}
//					else
//					{
//							td.add("-");
//					}
					
					//paybill no
					if (tupleSub[20] != null) 
					{
							td.add(tupleSub[20].toString());
					}
					else
					{
							td.add("-");
					}
					//paybill year
					if (tupleSub[21] != null) 
					{
							td.add(tupleSub[21].toString());
					}
					else
					{
							td.add("-");
					}
					
					if (tupleSub[16] != null) 
					{
							td.add(tupleSub[16].toString());
					}
					else
					{
							td.add("-");
					}
					
					
					if (tupleSub[2] != null) 
					{
							td.add(tupleSub[2].toString());
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[3] != null) 
					{
						
						td.add(tupleSub[3].toString());

					}
					else
					{
							td.add("-");
					}
					
					if (tupleSub[4] != null) 
					{
						
						
						td.add(tupleSub[4].toString());

					}
					else
					{
							td.add("-");
					}
					//From Date
					if (tupleSub[5] != null) 
					{
						
						td.add(tupleSub[5].toString());

					}
					else
					{
							td.add("-");
					}
					//to date
					if (tupleSub[6] != null) 
					{
						
						td.add(tupleSub[6].toString());

					}
					else
					{
							td.add("-");
					}
					//DCPS
					if (tupleSub[7] != null) 
					{
						
						td.add(tupleSub[7].toString());

					}
					else
					{
							td.add("-");
					}
					//Delayed
					if (tupleSub[8] != null) 
					{
						
						td.add(tupleSub[8].toString());

					}
					else
					{
							td.add("-");
					}
					//Pay Arrear 
					if (tupleSub[9] != null) 
					{
						
						td.add(tupleSub[9].toString());

					}
					else
					{
							td.add("-");
					}
					//DA Arrear 
					if (tupleSub[10] != null) 
					{
						
						td.add(tupleSub[10].toString());

					}
					else
					{
							td.add("-");
					}
					if (tupleSub[12] != null) 
					{
						//employee Contribution
						td.add( tupleSub[15].toString() );
					}
					
					if (tupleSub[13] != null) 
					{
						//employee Contribution
						
						if(!temp.equalsIgnoreCase(tupleSub[14].toString()+tupleSub[17].toString())  ||  temp.equalsIgnoreCase("N/A") )
						{
						td.add(  tupleSub[13].toString());
						temp=tupleSub[14].toString()+tupleSub[17].toString();
						}
						else
						{
							td.add("0");	
						}
						
					}

					else
					{
							td.add("-");
					}
					
					if (tupleSub[14] != null) 
					{
						
						//for last entry
//						List TableData11= getEmpWisePendingDtlsCount(SubTreasury,lStrYearId,tupleSub[19].toString()); 
						Long Pending=0L;
//						if(TableData11.size()==j)
						{
//						 Pending =remaining- Long.parseLong(tupleSub[13].toString());
//							td.add(tupleSub[14].toString()+ space(2) + space(2) + space(2) +"<br><font style='color:red;'>"+  (tupleSub[17].toString())+" Reamining Contribution : "+Pending+"</font>");

						}
						td.add(tupleSub[14].toString()+ space(2) + space(2) + space(2) +"<br><font style='color:red;'>"+  (tupleSub[17].toString())+"</font>");
						
						
					}
					else
					{
							td.add("-");
					}
					
//					td.add(tupleSub[14].toString());
					
//					getFourPercentData
				
						System.out.println("Five "+tupleSub[5] +"Six "+tupleSub[6]+"Type of Payment"+tupleSub[4]);
						
						
						String Month=tupleSub[5].toString().substring(3, 5);
						String Year=tupleSub[6].toString().substring(6, 10);
						System.out.println(tupleSub[4].toString());

						
						if("Regular".equalsIgnoreCase(tupleSub[4].toString())  && Year.equalsIgnoreCase("2019")  && (Month.equalsIgnoreCase("04") || Month.equalsIgnoreCase("05")||Month.equalsIgnoreCase("06") || Month.equalsIgnoreCase("07")||Month.equalsIgnoreCase("08") || Month.equalsIgnoreCase("09")) )
						{ 
							String PassedMOnth="APRIL";
							
							if(Month.equalsIgnoreCase("04"))
								PassedMOnth="APRIL";
							else if(Month.equalsIgnoreCase("05"))
								PassedMOnth="MAY";
							else if(Month.equalsIgnoreCase("06"))
								PassedMOnth="JUNE";
							else if(Month.equalsIgnoreCase("07"))
								PassedMOnth="JULY";
							else if(Month.equalsIgnoreCase("08"))
								PassedMOnth="AUGUST";
							else if(Month.equalsIgnoreCase("09"))
								PassedMOnth="SEPTEMBER";
							else
								PassedMOnth="OCTOBER";

								
								
//							 fourPercent= getFourPercentData(tupleSub[19].toString(),PassedMOnth);
//							td.add(fourPercent);

						}
					else
					{
//						td.add("-");
					}
						
						List TableData11= getEmpWisePendingDtlsCount(SubTreasury,lStrYearId,tupleSub[19].toString(),tupleSub[1].toString()); 
						Double Pending=0D;
						if(tupleSub[19].toString().equalsIgnoreCase("DGPMRRM8101"))
						{
							int a=100;
						}
						
						if(TableData11.size()+1==j)
						{
						 Pending =remaining- Double.parseDouble(tupleSub[13].toString());
						 if("Regular".equalsIgnoreCase(tupleSub[4].toString())  && Year.equalsIgnoreCase("2019")  && (Month.equalsIgnoreCase("04") || Month.equalsIgnoreCase("05")||Month.equalsIgnoreCase("06") || Month.equalsIgnoreCase("07")||Month.equalsIgnoreCase("08") || Month.equalsIgnoreCase("09")) )
							{
//							 Pending=Pending-Long.parseLong(fourPercent);
							}
						 Pending= (double) Math.round(Pending);
						 NumberFormat formatter = new DecimalFormat("#0");     
						 td.add(new BigInteger(formatter.format(Pending).toString()));
//						 td.add(40964);
						}
						else
						{
							
							td.add("0");
						}
						/*////
						for (int k = 0; k < tupleSub.length; k++) {
							System.out.println("sys-->"+" "+k+" "+tupleSub[k]);
						}*/
						
						String dcpsFlag;
						if(Pending != 0)
						dcpsFlag="Y";
						else
						dcpsFlag="N";
						
						
						
						String P_Ddo_name=getDdoNameFromDdoCode(SubTreasury);
						
						String C_Ddo_name;
						if(tupleSub[25].toString().equals("NA")){
						C_Ddo_name="";
						}else{
						C_Ddo_name=getDdoNameFromDdoCode(tupleSub[25].toString());
						}
							
						List lstValidation=getInsertQuryValidation(tupleSub[23].toString(),tupleSub[1].toString(),tupleSub[0].toString(),tupleSub[6].toString(),dcpsFlag,tupleSub[4].toString(),tupleSub[5].toString());
						if(lstValidation.size()==0){
							InsertQuery(tupleSub[23].toString(),tupleSub[24].toString(),tupleSub[1].toString(),tupleSub[0].toString(),Pending,SubTreasury,
									tupleSub[20],tupleSub[21].toString(),tupleSub[16].toString(),tupleSub[2],tupleSub[3].toString(),
									tupleSub[4].toString(),tupleSub[5].toString(),tupleSub[6].toString(),tupleSub[7],tupleSub[8],tupleSub[9],
									tupleSub[10],tupleSub[12],tupleSub[13],dcpsFlag,tupleSub[25].toString(),SubTreasury,C_Ddo_name,P_Ddo_name);
					    }
						tabledata.add(td);
				}
	          
			}
			
		}
		catch (Exception e) 
		{
			gLogger.info("=====findReportData(): Exception is=====" + e, e);
		}
		return tabledata;
	}
	
	
//get Treasury Wise Pending Details

	private List getTreasuryWisePendingDtls(Long locationId, String lStrYearId,String subTreasury, String location_code) {
		StringBuilder sb = new StringBuilder();
		List SevarthIdList=null;
		try
		{
			/*sb.append("select  n.PAYBILL_YEAR,sum(n.dcps+n.dcps_pay+n.dcps_delay+n.dcps_da),sum(n.DED_ADJUST),count(n.EMP_ID ) from NSDL_PAYBILL_DATA n "); 
			sb.append("join PAYBILL_HEAD_MPG head on "); 
			sb.append("n.PAYBILL_GRP_ID =head.PAYBILL_ID  and head.APPROVE_FLAG=1 and (n.dcps+n.dcps_pay+n.dcps_delay+n.dcps_da)>n.DED_ADJUST  and n.PAYBILL_YEAR='"+lStrYearId+"' "); 
			sb.append("join ORG_DDO_MST ddo on ddo.LOCATION_CODE=n.LOC_ID and substr(ddo.DDO_CODE,0,2 )=substr('"+subTreasury+"',0,2) "); 
			sb.append("group by n.PAYBILL_YEAR ");*/
			
			sb.append(" SELECT "); 
			sb.append(" year,sum(TOTAL_EMPLOYEE_CONTRIBUTION),sum(TOTAL_EMPLOYER_CONTRIBUTION),count(sevaarthid) "); 
			sb.append(" FROM LESSEMPLOYER "); 
			sb.append(" where year='"+lStrYearId+"' "); 
			sb.append(" and substr(treasury,0,2 )=substr('"+subTreasury+"',0,2) "); 
			sb.append(" and (TOTAL_EMPLOYEE_CONTRIBUTION)>TOTAL_EMPLOYER_CONTRIBUTION "); 
			sb.append(" and dcps_flag='Y' "); 
			sb.append(" group by year ");
			
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
	
	private List getDDOWisePendingDtls(String locationId, String lStrYearId) {
		StringBuilder sb = new StringBuilder();
		List SevarthIdList=null;
		try
		{
			/*sb.append("select loc.loc_name,ddo.DDO_CODE ,ddo.DDO_OFFICE ,sum(n.dcps+n.dcps_pay+n.dcps_delay+n.dcps_da),sum(n.DED_ADJUST),count(n.EMP_ID ) from NSDL_PAYBILL_DATA n "); 
			sb.append("join PAYBILL_HEAD_MPG head on "); 
			sb.append("n.PAYBILL_GRP_ID =head.PAYBILL_ID  and head.APPROVE_FLAG=1 and (n.dcps+n.dcps_pay+n.dcps_delay+n.dcps_da)>n.DED_ADJUST  and n.PAYBILL_YEAR='"+lStrYearId+"' "); 
			sb.append("join ORG_DDO_MST ddo on ddo.LOCATION_CODE=n.LOC_ID and substr(ddo.DDO_CODE,0,2 )=substr('"+locationId+"',0,2) "); 
			sb.append("join CMN_LOCATION_MST loc on loc.location_code=substr(ddo.DDO_CODE,0,4 ) "); 
			sb.append("group by substr(ddo.DDO_CODE,0,2 ),ddo.DDO_CODE,ddo.DDO_OFFICE ,loc.loc_name ");*/

			sb.append(" SELECT ");
			sb.append(" treasury,P_ddocode,P_DDO_NAME,sum(TOTAL_EMPLOYEE_CONTRIBUTION),sum(TOTAL_EMPLOYER_CONTRIBUTION),count(sevaarthid) ");
			sb.append(" FROM LESSEMPLOYER ");
			sb.append(" where year='"+lStrYearId+"' ");
			sb.append(" and substr(treasury,0,2 )=substr('"+locationId+"',0,2) ");
			sb.append(" and (TOTAL_EMPLOYEE_CONTRIBUTION)>(TOTAL_EMPLOYER_CONTRIBUTION) ");
			sb.append(" and dcps_flag='Y' ");
			sb.append(" group by substr(treasury,0,2 ),P_ddocode,P_DDO_NAME,treasury ");
			
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
	
	
	private List getEmpWisePendingDtls(String locationId, String lStrYearId) {
		StringBuilder sb = new StringBuilder();
		List SevarthIdList=null;
		try
		{
			/*sb.append("select n.PAYBILL_YEAR,n.PAYBILL_MONTH,head.VOUCHER_NO,varchar_format(head.VOUCHER_DATE,'DD/MM/YYYY'),look.LOOKUP_NAME,varchar_format(t.STARTDATE,'DD/MM/YYYY'),varchar_format(t.ENDDATE,'DD/MM/YYYY'), "); 
			sb.append("cast(case when t.TYPE_OF_PAYMENT= 700046 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700047 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700049 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700048 then t.CONTRIBUTION else 0 end as bigint), cast(nvl( t.CONTRIBUTION_EMPLR,0)as bigint), "); 
			sb.append("n.DCPS +n.DCPS_PAY+n.DCPS_DA+n.DCPS_DELAY,n.DED_ADJUST,' Paybill Month :'||n.PAYBILL_MONTH||' EMP NAME :'||' '|| em.EMP_NAME ||' PRAN NO :  '||em.pran_no ||' SEVAARTH ID : '||em.sevarth_id  "); 
			sb.append(" ,cast(t.CONTRIBUTION as bigint),decode(head.BILL_CATEGORY,2,'Regular',3,'Supplementary',15,'Suspension',16,'Arrears',99,'Nill Bill' ),' Employee Contri(AS Per paybill):'|| (n.DCPS +n.DCPS_PAY+n.DCPS_DA+n.DCPS_DELAY) ||' Employeer Contri(AS Per paybill):'||n.DED_ADJUST,n.PAYBILL_GRP_ID,em.sevarth_id,head.BILL_NO,varchar_format(head.CREATED_DATE,'DD/MM/YYYY'),cast(round(MONTHS_BETWEEN(T.ENDDATE,T.STARTDATE)) AS BIGINT),em.sevarth_id,em.pran_no,case when em.ddo_code is null then 'NA' else em.ddo_code end from TRN_DCPS_CONTRIBUTION t "); 
			sb.append("join PAYBILL_HEAD_MPG head on head.BILL_NO = t.BILL_GROUP_ID  and head.APPROVE_FLAG=1 and head.PAYBILL_YEAR = '"+lStrYearId+"' "); 
			sb.append("join NSDL_PAYBILL_DATA n on n.PAYBILL_GRP_ID =head.PAYBILL_ID and n.PAYBILL_YEAR ='"+lStrYearId+"' "); 
			sb.append("join MST_DCPS_EMP em on  em.DCPS_EMP_ID =t.DCPS_EMP_ID   join HR_EIS_EMP_MST hr on hr.emp_mpg_id=em.ORG_EMP_MST_ID and hr.EMP_ID =n.EMP_ID  "); 
			sb.append("join ORG_DDO_MST ddo on   head.loc_id =ddo.location_code  and  ddo.DDO_CODE='"+locationId+"'  "); 
			sb.append("join CMN_LOOKUP_MST look on look.LOOKUP_ID =t.TYPE_OF_PAYMENT join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_CODE='"+lStrYearId+"' and ((t.MONTH_ID  in (1,2,3) and t.FIN_YEAR_ID=fin.FIN_YEAR_ID-1) or (t.MONTH_ID not in (1,2,3) and t.FIN_YEAR_ID=fin.FIN_YEAR_ID)) "); 
			sb.append("where   t.CONTRIBUTION <>0 and   (n.dcps+n.dcps_pay+n.dcps_delay+n.dcps_da)>(n.DED_ADJUST) and head.PAYBILL_MONTH=n.PAYBILL_MONTH and n.PAYBILL_MONTH =t.MONTH_ID order by n.PAYBILL_MONTH ,n.EMP_ID,n.PAYBILL_GRP_ID ");*/

			sb.append(" SELECT ");
			sb.append(" less.year, ");
			sb.append(" less.month, ");
			sb.append(" less.P_VOUCHER_NO, ");
			sb.append(" less.P_VOUCHER_DATE, ");
			sb.append(" less.TYPE_OF_PAYMENT, ");
			sb.append(" less.START_DATE, ");
			sb.append(" less.END_DATE, ");
			sb.append(" less.DCPS, ");
			sb.append(" less.DELAYED, ");
			sb.append(" less.PAY_ARREAR, ");
			sb.append(" less.DA_ARREAR, ");
			sb.append(" less.TOTAL_EMPLOYER_CONTRIBUTION, ");
			sb.append(" (less.DCPS+less.PAY_arrear+less.DA_arrear+less.DELAYED), ");
			sb.append(" less.TOTAL_EMPLOYER_CONTRIBUTION, ");
			sb.append(" ' Paybill Month :'||less.YEAR||' EMP NAME :'||' '||m.emp_name||' PRAN NO :  '||less.PRANNO ||' SEVAARTH ID : '||less.SEVAARTHID, ");
			sb.append(" (less.DCPS+less.PAY_arrear+less.DA_arrear+less.DELAYED), ");
			sb.append(" less.TYPE_OF_BILL, ");
			sb.append(" ' Employee Contri(AS Per paybill):'|| less.TOTAL_EMPLOYEE_CONTRIBUTION||' Employeer Contri(AS Per paybill):'||less.TOTAL_EMPLOYER_CONTRIBUTION, ");
			sb.append(" less.BILL_NO, ");
			sb.append(" less.SEVAARTHID, ");
			sb.append(" less.BILL_NO, ");
			sb.append(" less.BILL_CREATED_DATE, ");
			sb.append(" cast(round(MONTHS_BETWEEN (date(to_date(END_DATE,'DD/MM/YYYY')), date(to_date(start_date,'DD/MM/YYYY')))) AS BIGINT), ");
			sb.append(" less.SEVAARTHID, ");
			sb.append(" less.pranno, ");
			sb.append(" less.C_ddocode ");
			sb.append(" from LESSEMPLOYER less join mst_Dcps_Emp m on less.sevaarthid=m.sevarth_id ");
			sb.append(" where less.year='"+lStrYearId+"'  ");
			sb.append(" and less.P_DDOCODE='"+locationId+"' ");
			sb.append(" order by less.MONTH,less.sevaarthid,less.BILL_NO ");
			
			
			
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(sb.toString());
			SevarthIdList= lQuery.list();
			
			/* int i = 0;
			 for (Iterator it = SevarthIdList.iterator(); it.hasNext();)
			{
			 Object[] lObj = (Object[])it.next();
			 System.out.println("list-->"+i+" - "+lObj[i++].toString());
			 }*/
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
			return SevarthIdList;
	}
	
	
	private List getEmpWisePendingDtlsCount(String locationId, String lStrYearId,String sevaarthID,String Month) {
		StringBuilder sb = new StringBuilder();
		List SevarthIdList=null;
		try
		{
			/*sb.append("select n.PAYBILL_YEAR,n.PAYBILL_MONTH,head.VOUCHER_NO,varchar_format(head.VOUCHER_DATE,'DD/MM/YYYY'),look.LOOKUP_NAME,varchar_format(t.STARTDATE,'DD/MM/YYYY'),varchar_format(t.ENDDATE,'DD/MM/YYYY'), "); 
			sb.append("cast(case when t.TYPE_OF_PAYMENT= 700046 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700047 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700049 then t.CONTRIBUTION else 0 end as bigint), cast(case when t.TYPE_OF_PAYMENT= 700048 then t.CONTRIBUTION else 0 end as bigint), cast(nvl( t.CONTRIBUTION_EMPLR,0)as bigint), "); 
			sb.append("n.DCPS +n.DCPS_PAY+n.DCPS_DA+n.DCPS_DELAY,n.DED_ADJUST,' Paybill Month :'||n.PAYBILL_MONTH||' EMP NAME :'||' '|| em.EMP_NAME ||' PRAN NO :  '||em.pran_no ||' SEVAARTH ID : '||em.sevarth_id  "); 
			sb.append(" ,cast(t.CONTRIBUTION as bigint),head.BILL_CATEGORY,' Employee Contri(AS Per paybill):'|| (n.DCPS +n.DCPS_PAY+n.DCPS_DA+n.DCPS_DELAY) ||' Employeer Contri(AS Per paybill):'||n.DED_ADJUST,n.PAYBILL_GRP_ID,em.sevarth_id,head.paybill_id,varchar_format(head.CREATED_DATE,'DD/MM/YYYY')  from TRN_DCPS_CONTRIBUTION t "); 
			sb.append("join PAYBILL_HEAD_MPG head on head.BILL_NO = t.BILL_GROUP_ID  and head.APPROVE_FLAG=1 and head.PAYBILL_YEAR = '"+lStrYearId+"' "); 
			sb.append("join NSDL_PAYBILL_DATA n on n.PAYBILL_GRP_ID =head.PAYBILL_ID and n.PAYBILL_YEAR ='"+lStrYearId+"' "); 
			sb.append("join MST_DCPS_EMP em on em.sevarth_id='"+sevaarthID+"' and  em.DCPS_EMP_ID =t.DCPS_EMP_ID   join HR_EIS_EMP_MST hr on hr.emp_mpg_id=em.ORG_EMP_MST_ID and hr.EMP_ID =n.EMP_ID  "); 
			sb.append("join ORG_DDO_MST ddo on   head.loc_id =ddo.location_code  and  ddo.DDO_CODE='"+locationId+"'  "); 
			sb.append("join CMN_LOOKUP_MST look on look.LOOKUP_ID =t.TYPE_OF_PAYMENT join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_CODE='"+lStrYearId+"' and ((t.MONTH_ID  in (1,2,3) and t.FIN_YEAR_ID=fin.FIN_YEAR_ID-1) or (t.MONTH_ID not in (1,2,3) and t.FIN_YEAR_ID=fin.FIN_YEAR_ID)) "); 
			sb.append("where n.PAYBILL_MONTH='"+Month+"' and head.PAYBILL_MONTH='"+Month+"' and   t.CONTRIBUTION <>0  and   (n.dcps+n.dcps_pay+n.dcps_delay+n.dcps_da)>(n.DED_ADJUST) and head.PAYBILL_MONTH=n.PAYBILL_MONTH and n.PAYBILL_MONTH =t.MONTH_ID order by n.PAYBILL_MONTH ,n.EMP_ID,n.PAYBILL_GRP_ID ");*/

			sb.append(" SELECT ");
			sb.append(" less.year, ");
			sb.append(" less.month, ");
			sb.append(" less.P_VOUCHER_NO, ");
			sb.append(" less.P_VOUCHER_DATE, ");
			sb.append(" less.TYPE_OF_PAYMENT, ");
			sb.append(" less.START_DATE, ");
			sb.append(" less.END_DATE, ");
			sb.append(" less.DCPS, ");
			sb.append(" less.DELAYED, ");
			sb.append(" less.PAY_ARREAR, ");
			sb.append(" less.DA_ARREAR, ");
			sb.append(" less.TOTAL_EMPLOYER_CONTRIBUTION, ");
			sb.append(" less.TOTAL_EMPLOYEE_CONTRIBUTION, ");
			sb.append(" less.TOTAL_EMPLOYER_CONTRIBUTION, ");
			sb.append(" ' Paybill Month :'||less.YEAR||' EMP NAME :'||' '||m.emp_name||' PRAN NO :  '||less.PRANNO ||' SEVAARTH ID : '||less.SEVAARTHID, ");
			sb.append(" less.TOTAL_EMPLOYEE_CONTRIBUTION, ");
			sb.append(" less.TYPE_OF_BILL, ");
			sb.append(" ' Employee Contri(AS Per paybill):'|| less.TOTAL_EMPLOYEE_CONTRIBUTION||' Employeer Contri(AS Per paybill):'||less.TOTAL_EMPLOYER_CONTRIBUTION, ");
			sb.append(" less.BILL_NO, ");
			sb.append(" less.SEVAARTHID, ");
			sb.append(" less.BILL_NO, ");
			sb.append(" less.BILL_CREATED_DATE ");
			sb.append(" from LESSEMPLOYER less join mst_Dcps_Emp m on less.sevaarthid=m.sevarth_id ");
			sb.append(" where less.year='"+lStrYearId+"' ");
			sb.append(" and less.month='"+Month+"' ");
			sb.append(" and less.sevaarthid='"+sevaarthID+"' ");
			sb.append(" and less.P_DDOCODE='"+locationId+"' ");
			sb.append(" order by less.MONTH,less.sevaarthid,less.BILL_NO ");
			
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
		String ddoCode="0";
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

//to insert query
public void InsertQuery(String SevaarthId, String PranNo,String Month,String Year,Double AMount,String SubTreasury,
		Object tupleSub, String BILL_CREATED_DATE, String TYPE_OF_BILL, Object tupleSub2, String P_VOUCHER_DATE, String TYPE_OF_PAYMENT, String START_DATE,
		String END_DATE, Object tupleSub3, Object tupleSub4, Object tupleSub5, Object tupleSub6, Object tupleSub7, Object tupleSub8,String DCPS_FLAG, String C_DDOCode, String P_DDOCode, String c_Ddo_name, String p_Ddo_name) {
	// TODO Auto-generated method stub
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" insert into lessemployer values ((select max(id)+1 from lessemployer),'"+SevaarthId+"','"+PranNo+"','"+Month+"','"+Year+"',"+AMount+", "
				+ " null,null,'N',null,null,substr("+SubTreasury+",1,4),'"+tupleSub+"','"+BILL_CREATED_DATE+"','"+TYPE_OF_BILL+"','"+tupleSub2+"','"+P_VOUCHER_DATE+"', "
				+ " '"+TYPE_OF_PAYMENT+"','"+START_DATE+"','"+END_DATE+"','"+tupleSub3+"','"+tupleSub4+"','"+tupleSub5+"','"+tupleSub6+"','"+tupleSub7+"', "
				+ " '"+tupleSub8+"','"+DCPS_FLAG+"' ,'"+C_DDOCode+"','"+P_DDOCode+"','"+c_Ddo_name+"','"+p_Ddo_name+"' )");
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lSBQuery = new StringBuilder();
		lQuery.executeUpdate();
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



public List getInsertQuryValidation(String SevaarthId,String Month,String year,String EndDate,String DCPSFlag, String TypeOfPayment,String StartDate) throws Exception{

	
	StringBuilder lSBQuery = new StringBuilder();
	List SevarthIdList=null;
	
	try
	{
        /*String [] a=EndDate.split("/");
    	String f=a[2].concat("-").concat(a[1]).concat("-").concat(a[0]);*/
		
		lSBQuery.append(" select * from lessemployer where SEVAARTHID ='"+SevaarthId+"' and MONTH ='"+Month+"' and year='"+year+"' "
				+ "  and END_DATE like '%"+EndDate+"%' and START_DATE like '%"+StartDate+"%'  and TYPE_OF_PAYMENT='"+TypeOfPayment.trim()+"' ");////and DCPS_FLAG='"+DCPSFlag+"'
		
		Session ghibSession = ServiceLocator.getServiceLocator()
		.getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		
		SevarthIdList= lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		
		
		return SevarthIdList;

	}

public String space(int noOfSpace) {	
    String blank = "";	
    for(int i = 0; i < noOfSpace; ++i) {	
       blank = blank + " ";	
    }	
    return blank;
}

public String getDdoNameFromDdoCode(String lStrDdoCode) {
	
	StringBuilder sb = new StringBuilder();
    String ddoName = null; 
	
	sb.append("SELECT ddo_Name FROM  Org_Ddo_Mst WHERE  ddo_Code = '"+lStrDdoCode+"' ");
	
	Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
	Query lQuery = ghibSession.createSQLQuery(sb.toString());
	
	List lLstResult = lQuery.list();
	
	if (lLstResult != null && !lLstResult.isEmpty()) {
		ddoName=lLstResult.get(0).toString();
	}	
	return ddoName;
}

}//class
