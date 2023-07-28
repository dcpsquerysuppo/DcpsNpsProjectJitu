package com.tcs.sgv.dcps.report;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DateUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

@SuppressWarnings("unused")
public class PendingNPSContriReportDAOImpl   extends DefaultReportDataFinder
implements ReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(PendingNPSContriReportDAOImpl.class);
	ServiceLocator serviceLocator = null;
	Map requestAttributes = null;
	String sevaarthId="";
//	SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
//	Date date = new Date();
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
			sevaarthId="";
			String EmpName="";
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
			StyleVO[] rowsFontsVO = new StyleVO[5];
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
			rowsFontsVO[4] = new StyleVO();
			rowsFontsVO[4].setStyleId(26);
			rowsFontsVO[4].setStyleValue("JavaScript:self.close()");
			
			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();
			String ddoCode="";
			String dcpsEmpId="";
			String treasuryCode="";
			if (report.getReportCode().equals("9000520")) 
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
			
				List TableData1= getTreasuryWiseDtls(locationId,lStrYearId,SubTreasury,Location_code);
				// List TableData1= getTreasuryWiseContriDtlsSrka1(locationId,lStrYearId,SubTreasury,Location_code);
				for (int i=0;i<TableData1.size();i++)
				{
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
					td.add(i+1);
					if (tupleSub[9] != null) 
					{
							td.add(tupleSub[9]);////$t31Jan22
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[10] != null) 
					{
							td.add(tupleSub[10]);////$t31Jan22
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
					if (tupleSub[4] != null) 
					{
							td.add(tupleSub[4]);
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
					
					if (tupleSub[5] != null) 
					{
							td.add(tupleSub[5]);
					}
					else
					{
							td.add(0);
					}
					if (tupleSub[6] != null) 
					{
							td.add(tupleSub[6]);
					}
					else
					{
							td.add(0);
					}
					if (tupleSub[7] != null) 
					{
							//td.add(tupleSub[7]);
						td.add(new URLData(tupleSub[7],"hrms.htm?actionFlag=reportService&reportCode=9000532&action=generateReport&DirectReport=TRUE&displayOK=FALSE&Year="+tupleSub[3]+"&Month="+tupleSub[4]+"&Ddo="+tupleSub[9]));
					}
					else
					{
							td.add(0);
					}
					if (tupleSub[8] != null) 
					{
							td.add(tupleSub[8]);
					}
					else
					{
							td.add("-");
					}
				
					tabledata.add(td);
				}
	          }
			//Jitu 22-nov-2022
			if (report.getReportCode().equals("800009594")) 
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
				 String tresuryCode = (String) report.getParameterValue("Tresury_code");
			
				//List TableData1= getTreasuryWiseDtls(locationId,lStrYearId,SubTreasury,Location_code);
				List TableData1= getTreasuryWiseContriDtlsSrka1(locationId,lStrYearId,tresuryCode,Location_code);
				for (int i=0;i<TableData1.size();i++)
				{
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
					td.add(i+1);
					if (tupleSub[0] != null) 
					{
							td.add(tupleSub[0]);////$t31Jan22
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[10] != null) 
					{
							td.add(tupleSub[10]);////$t31Jan22
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
					if (tupleSub[4] != null) 
					{
							td.add(tupleSub[4]);
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
					
					if (tupleSub[5] != null) 
					{
							td.add(tupleSub[5]);
					}
					else
					{
							td.add(0);
					}
					if (tupleSub[6] != null) 
					{
							td.add(tupleSub[6]);
					}
					else
					{
							td.add(0);
					}
					if (tupleSub[7] != null) 
					{
							//td.add(tupleSub[7]);
						td.add(new URLData(tupleSub[7],"hrms.htm?actionFlag=reportService&reportCode=9000532&action=generateReport&DirectReport=TRUE&displayOK=FALSE&Year="+tupleSub[3]+"&Month="+tupleSub[4]+"&Ddo="+tupleSub[9]));
					}
					else
					{
							td.add(0);
					}
					if (tupleSub[8] != null) 
					{
							td.add(tupleSub[8]);
					}
					else
					{
							td.add("-");
					}
				
					tabledata.add(td);
				}
	          }
			if (report.getReportCode().equals("9000521")) 
			{
				
				 
				List TableData = null;
			
				StringBuilder SBQuery = new StringBuilder();
				
			
				List TableData1= getTreasuryWiseDtlsSrka();
				for (int i=0;i<TableData1.size();i++)
				{
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
					td.add(i+1);
					if (tupleSub[0] != null) 
					{
							td.add(tupleSub[0]);
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
							td.add(tupleSub[3]);
					}
					else
					{
							td.add(0);
					}
					if (tupleSub[4] != null) 
					{
							td.add(tupleSub[4]);
					}
					else
					{
							td.add(0);
					}
					if (tupleSub[5] != null) 
					{
							
							td.add(new URLData(tupleSub[5],"hrms.htm?actionFlag=reportService&reportCode=9000520&action=generateReport&DirectReport=TRUE&displayOK=FALSE&locationId="+tupleSub[1]));
					}
					else
					{
							td.add(0);
					}
					
					tabledata.add(td);
				}
				
				
				
	          }
			
			// For r2 Report 10/nov/2020
			if (report.getReportCode().equals("80000977")) 
			{
				
				StringBuilder SBQuery = new StringBuilder();
				
				
				/* to get Intsallment details for Tier2*/
				Double installment1=0D,installment2=0D,installment3=0D,installment4=0D,installment5=0D,TotalInst=0D;
				String employee_name="", Sevaarth_id="";
				String deputation="";
				//System.out.println("sevaarthId-->"+sevaarthId.substring(0, 11));
				
				sevaarthId= getCorrectSevarth(sevaarthId);
				
				List lstInstallment= getTierIIInterest(sevaarthId);
				
				if(lstInstallment.size()>0){
				for (int i=0;i<lstInstallment.size();i++)
				{
					Object[] tupleSub = (Object[]) lstInstallment.get(i);
					installment1=Double.parseDouble(tupleSub[1].toString());
					installment2=Double.parseDouble(tupleSub[2].toString());
					installment3=Double.parseDouble(tupleSub[3].toString());
					installment4=Double.parseDouble(tupleSub[4].toString());
					installment5=Double.parseDouble(tupleSub[5].toString());
					TotalInst=installment1+installment2+installment3+installment4+installment5;
					employee_name=(String) tupleSub[7];
					Sevaarth_id=(String) tupleSub[8];
					deputation=(String) tupleSub[10];
				}
				
				/* for Header*/
				StyleVO[] centreAlign = new StyleVO[1];
				centreAlign[0] = new StyleVO();
				centreAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
				centreAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				
				System.out.println("DAN");
				SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");	
	            Date date = new Date();	
	            String curDate = dateFormat.format(date);	
	            gLogger.info("=====Innside parent report=====");	
	            String header5 =  "Employee Name :- " + this.space(2) + EmpName + this.space(2)  +this.space(5) +"SevaarthId :- " + this.space(2) + sevaarthId +this.space(5) +  "Total Sixth Pay Arrear Amount :- " + this.space(2) + (new Double(TotalInst)).longValue() +this.space(5)+this.space(5)+this.space(5)  ;	
	            report.setAdditionalHeader(header5);
	        	report.setStyleList(centreAlign);
	        	
	        	
				
				
			//get Total Fin Years for looping purpose
				
				Double openingBalance=0D;
				Double closingBalance=0D;
				Double Total=0D;
				Double Interest=0D;
				Double yeadDays=365D;
				Double leapYeadDays=366D;
				List lstLoopingForTierII=getLoopingFOrIntereseCalculations();
				Double PayableTotal=0D;
				//String year="";
				for (int i=0;i<lstLoopingForTierII.size();i++)
				{
					Object[] tupleSub = (Object[]) lstLoopingForTierII.get(i);
					td = new ArrayList();
					
					td.add(i+1);
//					td.add(Sevaarth_id);
//					td.add(employee_name);
					td.add(tupleSub[1].toString());
					td.add(openingBalance);
					
					
					//for leap days
					if(tupleSub[1].toString().equalsIgnoreCase("2011-2012") || tupleSub[1].toString().equalsIgnoreCase("2015-2016") ||tupleSub[1].toString().equalsIgnoreCase("2019-2020") )
						yeadDays=leapYeadDays;
					
					if(i==0)
					{
					td.add(installment1);
					Total=openingBalance+installment1;
					 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment1, yeadDays,deputation);
					}
					else
					td.add("0");	
					if(i==1)
					{
						td.add(installment2);
						Total=openingBalance+installment2;
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment2,yeadDays,deputation);
					}	else
						td.add("0");
					if(i==2)
					{
						td.add(installment3);
						Total=openingBalance+installment3;
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment3,yeadDays,deputation);
					}
						else
						td.add("0");
					if(i==3){
						td.add(installment4);
						Total=openingBalance+installment4;
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment4,yeadDays,deputation);
					}
						else
						td.add("0");
					if(i==4)
					{
						td.add(installment5);
						Total=openingBalance+installment5;
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment5,yeadDays,deputation);
					}
						else
						td.add("0");
					if(i>4)
					{
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,0D,yeadDays,deputation);
						 Total=openingBalance;	 
					}

					
					td.add(Total);//total
					
					td.add(Interest);//interest
					PayableTotal=PayableTotal+Interest;
					if(tupleSub[0].toString().equals("34")){
					closingBalance=(double) (Math.round(Interest)+Math.round(Total));
					//year=tupleSub[0].toString();
					}else{
					closingBalance=Interest+Total;	
					}
					
					td.add(closingBalance); //closing amount
					
					openingBalance=closingBalance;
					tabledata.add(td);	
				}

				td = new ArrayList();
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add("TOTAL");
				
				//if(year.equals("34"))
				PayableTotal=(double) (Math.round(Interest)+Math.round(Total));
				//else
				//PayableTotal=PayableTotal+TotalInst;

				td.add(PayableTotal.longValue());
				tabledata.add(td);
				
				/* for New Header */
				ReportAttributeVO[] lArrrReportAttributeVO = new ReportAttributeVO[4];
				lArrrReportAttributeVO[0] = new ReportAttributeVO();
				lArrrReportAttributeVO[0].setAttributeType(IReportConstants.ATTRIB_OTHER);
				lArrrReportAttributeVO[0].setLocation(IReportConstants.LOCATION_HEADER);
				lArrrReportAttributeVO[0].setAlignment(IReportConstants.ALIGN_RIGHT);
				lArrrReportAttributeVO[0].setAttributeValue("Date : " + DateUtility.getCurrentDateTime());

				lArrrReportAttributeVO[1] = new ReportAttributeVO();
				lArrrReportAttributeVO[1].setAttributeType(IReportConstants.ATTRIB_OTHER);
				lArrrReportAttributeVO[1].setLocation(IReportConstants.LOCATION_FOOTER);
				lArrrReportAttributeVO[1].setAlignment(IReportConstants.ALIGN_RIGHT);
				
				lArrrReportAttributeVO[2] = new ReportAttributeVO();
				lArrrReportAttributeVO[2].setAttributeType(IReportConstants.ATTRIB_OTHER);
				lArrrReportAttributeVO[2].setLocation(IReportConstants.LOCATION_FOOTER);
				lArrrReportAttributeVO[2].setAlignment(IReportConstants.ALIGN_LEFT);
				lArrrReportAttributeVO[2].setAttributeValue("<b>Note: Interest calculated are as per GPF interest rates.</b>" + "<br>" + "<br>" + "<br>"  + "\n शासन परिपत्रक क्र. : अंनियो १०१०/प्र. क्र. ६७/ सेवा- ४           दिनांक १६ नोव्हेंबर २०१२"+"<br>" );

		
				lArrrReportAttributeVO[3] = new ReportAttributeVO();
				lArrrReportAttributeVO[3].setAttributeType(IReportConstants.ATTRIB_OTHER);
				lArrrReportAttributeVO[3].setLocation(IReportConstants.LOCATION_FOOTER);
				lArrrReportAttributeVO[3].setAlignment(IReportConstants.ALIGN_RIGHT);
				String totalContributionAmountInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(PayableTotal));
				lArrrReportAttributeVO[3].setAttributeValue( "\n Net Payable Amount is Rs : "  + this.space(2) + PayableTotal.longValue() + "(" +totalContributionAmountInWords + ")" + "<br>"  );
				
				String lStrRptName = report.getReportName();
				report.setReportAttributes(lArrrReportAttributeVO);
				
				}else{
					String header1 = "<center><b>" + "<font style=\"color:red\" size=\"5px\"> " + "(Note: For this employee, all installments of 6th PC Arrears are not approved. Hence this report will not show any records.)" + "</font></b></center>";
					report.setAdditionalHeader(header1);
				}
			}
			
			if (report.getReportCode().equals("80000979")) 
			{
				StringBuilder SBQuery = new StringBuilder();
				 String DdoCOde = (String) report.getParameterValue("ddoCode");
				 String orderId = (String) report.getParameterValue("OrderId");
				 String year = (String) report.getParameterValue("year");
				 String month = (String) report.getParameterValue("month");
				 Double PayableTotal1 = Double.parseDouble((String) report.getParameterValue("amount"));
				 String deputation = (String) report.getParameterValue("deputation");
				 List tierIIBillList=null;
					tierIIBillList=getOrderDetails(orderId,month,year,deputation);
					for (int J=0;J<tierIIBillList.size();J++)
					{
						sevaarthId=null;
						String EmployeeDetails=null;
						Object[] tupleSub1 = (Object[]) tierIIBillList.get(J);
						
						td = new ArrayList();
						
						td.add(J+1);
						td.add(tupleSub1[0].toString());
						td.add(tupleSub1[1].toString());
						td.add(tupleSub1[2].toString());
						if(tupleSub1[3]!=null)
						td.add(tupleSub1[3].toString());
						else
						td.add("NA");
						if(tupleSub1[4]!=null)
						td.add(tupleSub1[4].toString());
						else
							td.add("NA");
						if(tupleSub1[5]!=null)
						td.add(tupleSub1[5].toString());
						else
							td.add("NA");
						if(tupleSub1[6]!=null)
						td.add(tupleSub1[6].toString());
						else
							td.add("NA");
						if(tupleSub1[7]!=null)
						td.add(tupleSub1[7].toString());
						else
							td.add("NA");
						if(tupleSub1[8]!=null)
						td.add(tupleSub1[8].toString());
						else
							td.add("NA");
						if(tupleSub1[9]!=null)
						td.add(tupleSub1[9].toString());
						else
							td.add("NA");
						if(tupleSub1[10]!=null)
						td.add(tupleSub1[10].toString());
						else
							td.add("NA");
						if(tupleSub1[11]!=null)
						td.add(tupleSub1[11].toString());
						else
							td.add("NA");
						if(tupleSub1[12]!=null)
						td.add(tupleSub1[12].toString());
						else
							td.add("NA");
						if(tupleSub1[13]!=null)
						td.add(tupleSub1[13].toString());
						else
							td.add("NA");
						
						/*if(tupleSub1[0]!=null)
						td.add(tupleSub1[0].toString());
						else
						td.add("NA");*/
						String url4;
						if(deputation.equals("Y"))
						url4 = "ifms.htm?actionFlag=getNamunaFDept&login=HOD";
						else 
						url4 = "ifms.htm?actionFlag=getNamunaF";	
							
						 StyleVO[] lObjStyleVO4 = new StyleVO[report.getStyleList().length];
			                lObjStyleVO4 = report.getStyleList();
			                for (Integer lInt4 = 0; lInt4 < report.getStyleList().length; ++lInt4) {
			                    if (lObjStyleVO4[lInt4].getStyleId() == 26) {
			                        lObjStyleVO4[lInt4].setStyleValue(url4);
			                    }
			                }      
						
						tabledata.add(td);
					}    
			}
/*			if (report.getReportCode().equals("80000978")) 
			{
				
				 ResourceBundle gObjRsrcBndleForSPL90 = ResourceBundle.getBundle("resources/dcps/dcpsLabels");
				StringBuilder SBQuery = new StringBuilder();
				 String DdoCOde = (String) report.getParameterValue("ddoCode");
				 String orderId = (String) report.getParameterValue("OrderId");
				 String year = (String) report.getParameterValue("year");
				 String month = (String) report.getParameterValue("month");
				 Double PayableTotal1 = Double.parseDouble((String) report.getParameterValue("amount"));
				 String Type = (String) report.getParameterValue("type");
					
				 ArrayList<Object> rowList = new ArrayList<Object>();
					ArrayList<Object> dataList = new ArrayList<Object>();
					DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
	               
	                
					 for allignment
					StyleVO[] CenterBoldAlignVO = new StyleVO[4];
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
					CenterBoldAlignVO[3].setStyleValue("12");
					
					
					 for allignment
					StyleVO[] CenterBoldAlignVO1 = new StyleVO[4];
					CenterBoldAlignVO1[0] = new StyleVO();
					CenterBoldAlignVO1[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
					CenterBoldAlignVO1[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
					CenterBoldAlignVO1[1] = new StyleVO();
					CenterBoldAlignVO1[1].setStyleId(IReportConstants.BORDER);
					CenterBoldAlignVO1[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
					CenterBoldAlignVO1[2] = new StyleVO();
					CenterBoldAlignVO1[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
					CenterBoldAlignVO1[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
					CenterBoldAlignVO1[3] = new StyleVO();
					CenterBoldAlignVO1[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
					CenterBoldAlignVO1[3].setStyleValue("16");

				   rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("J.HEADER"),CenterBoldAlignVO));
					dataList.add(rowList);
					
					
					
					if(Type.equalsIgnoreCase("J"))
					{
					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("J.HEADER1"),CenterBoldAlignVO1));
					dataList.add(rowList);
					}else if(Type.equalsIgnoreCase("Y"))
					{
					rowList = new ArrayList<Object>();
					rowList.add(new StyledData(gObjRsrcBndleForSPL90.getString("Y.HEADER1"),CenterBoldAlignVO1));
					dataList.add(rowList);
					}
					
					
					 
					
					
					
					TabularData tData  = new TabularData(dataList);				
					tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT, IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
					tData.addStyle(IReportConstants.BORDER, "No");
					tData.addStyle(IReportConstants.SHOW_REPORT_NAME, IReportConstants.VALUE_NO);
					report.setAdditionalHeader(tData);
				 
				 
				    List tierIIBillList=null;
				    
				    if(Type.equalsIgnoreCase("J"))
					{
						tierIIBillList=getOrderDetailsG(orderId,month,year);
				    	
					}
				    else
						tierIIBillList=getOrderDetailsY(orderId,month,year);

				    
					for (int J=0;J<tierIIBillList.size();J++)
					{
						sevaarthId=null;
						String EmployeeDetails=null;
						Object[] tupleSub1 = (Object[]) tierIIBillList.get(J);
						sevaarthId=tupleSub1[0].toString();
						EmployeeDetails=tupleSub1[1].toString();
				 to get Intsallment details for Tier2
				Double installment1=0D,installment2=0D,installment3=0D,installment4=0D,installment5=0D,TotalInst=0D;
				String employee_name="", Sevaarth_id="";
				List lstInstallment= getTierIIInterest(sevaarthId);
					
					
				for (int i=0;i<lstInstallment.size();i++)
				{
					Object[] tupleSub = (Object[]) lstInstallment.get(i);
					installment1=Double.parseDouble(tupleSub[1].toString());
					installment2=Double.parseDouble(tupleSub[2].toString());
					installment3=Double.parseDouble(tupleSub[3].toString());
					installment4=Double.parseDouble(tupleSub[4].toString());
					installment5=Double.parseDouble(tupleSub[5].toString());
					TotalInst=installment1+installment2+installment3+installment4+installment5;
					employee_name=(String) tupleSub[7];
					Sevaarth_id=(String) tupleSub[8];
				}
				
				
				
				
				 for Header
				StyleVO[] centreAlign = new StyleVO[1];
				centreAlign[0] = new StyleVO();
				centreAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
				centreAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
				
				System.out.println("DAN");
				SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");	
	            Date date = new Date();	
	            String curDate = dateFormat.format(date);	
	            gLogger.info("=====Innside parent report=====");	
	            String header5 =  "Employee Name :- " + this.space(2) + EmpName + this.space(2)  +this.space(5) +"SevaarthId :- " + this.space(2) + sevaarthId +this.space(5) +  "Total Sixth Pay Arrear Amount :- " + this.space(2) + (new Double(TotalInst)).longValue() +this.space(5)+this.space(5)+this.space(5)  ;	
//	            report.setAdditionalHeader(header5);
//	        	report.setStyleList(centreAlign);
	        	
	        	
				
				
			//get Total Fin Years for looping purpose
				
				Double openingBalance=0D;
				Double closingBalance=0D;
				Double Total=0D;
				Double Interest=0D;
				Double yeadDays=365D;
				Double leapYeadDays=366D;
				List lstLoopingForTierII=getLoopingFOrIntereseCalculations();
				Double PayableTotal=0D;
				for (int i=0;i<lstLoopingForTierII.size();i++)
				{
					Object[] tupleSub = (Object[]) lstLoopingForTierII.get(i);
					td = new ArrayList();
					
					td.add(i+1);
//					td.add(Sevaarth_id);
//					td.add(employee_name);
					td.add(tupleSub[1].toString());
					td.add(openingBalance);
					if(i==0)
					{
					td.add(installment1);
					Total=openingBalance+installment1;
					 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment1, yeadDays);
					}
					else
					td.add("0");	
					if(i==1)
					{
						td.add(installment2);
						Total=openingBalance+installment1;
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment2,yeadDays);
					}	else
						td.add("0");
					if(i==2)
					{
						td.add(installment3);
						Total=openingBalance+installment1;
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment3,yeadDays);
					}
						else
						td.add("0");
					if(i==3){
						td.add(installment4);
						Total=openingBalance+installment1;
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment4,yeadDays);
					}
						else
						td.add("0");
					if(i==4)
					{
						td.add(installment5);
						Total=openingBalance+installment1;
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,installment5,yeadDays);
					}
						else
						td.add("0");
					if(i>4)
					{
						 Interest= CalculateInterest(tupleSub[0].toString(),openingBalance,0D,yeadDays);
						 Total=openingBalance;	 
					}

					
					td.add(Total);//total
				
					
					td.add(Interest);//interest
					PayableTotal=PayableTotal+Interest;
					
					closingBalance=Interest+Total;
					td.add(EmployeeDetails);

					td.add(closingBalance); //closing amount
					

					openingBalance=closingBalance;
					tabledata.add(td);	
				}

				td = new ArrayList();
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add(" ");
				td.add("TOTAL");
				td.add(" ");
				PayableTotal=PayableTotal+TotalInst;

				td.add(PayableTotal.longValue());
				tabledata.add(td);
				td = new ArrayList();
					}
				
				
				
				
				 for New Header 
					ReportAttributeVO[] lArrrReportAttributeVO = new ReportAttributeVO[4];
				lArrrReportAttributeVO[0] = new ReportAttributeVO();
				lArrrReportAttributeVO[0].setAttributeType(IReportConstants.ATTRIB_OTHER);
				lArrrReportAttributeVO[0].setLocation(IReportConstants.LOCATION_FOOTER);
				lArrrReportAttributeVO[0].setAlignment(IReportConstants.ALIGN_RIGHT);
				lArrrReportAttributeVO[0].setAttributeValue(new StyledData(gObjRsrcBndleForSPL90.getString("J.FOOTER1"),CenterBoldAlignVO)+"<br> <br>");

				lArrrReportAttributeVO[1] = new ReportAttributeVO();
				lArrrReportAttributeVO[1].setAttributeType(IReportConstants.ATTRIB_OTHER);
				lArrrReportAttributeVO[1].setLocation(IReportConstants.LOCATION_FOOTER);
				lArrrReportAttributeVO[1].setAlignment(IReportConstants.ALIGN_RIGHT);
				lArrrReportAttributeVO[1].setAttributeValue(new StyledData(gObjRsrcBndleForSPL90.getString("J.FOOTER2"),CenterBoldAlignVO)+"<br> <br>");

				lArrrReportAttributeVO[2] = new ReportAttributeVO();
				lArrrReportAttributeVO[2].setAttributeType(IReportConstants.ATTRIB_OTHER);
				lArrrReportAttributeVO[2].setLocation(IReportConstants.LOCATION_FOOTER);
				lArrrReportAttributeVO[2].setAlignment(IReportConstants.ALIGN_RIGHT);
				lArrrReportAttributeVO[1].setAttributeValue(  new StyledData(gObjRsrcBndleForSPL90.getString("J.FOOTER3"),CenterBoldAlignVO) +"<br> <br>  ");
 
		
				lArrrReportAttributeVO[3] = new ReportAttributeVO();
				lArrrReportAttributeVO[3].setAttributeType(IReportConstants.ATTRIB_OTHER);
				lArrrReportAttributeVO[3].setLocation(IReportConstants.LOCATION_FOOTER);
				lArrrReportAttributeVO[3].setAlignment(IReportConstants.ALIGN_RIGHT);
				String totalContributionAmountInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(PayableTotal1));
				lArrrReportAttributeVO[3].setAttributeValue( "\n (DDO CODE : "  + this.space(2) +DdoCOde + ")" + "<br>"  );

				
				String lStrRptName = report.getReportName();
				report.setReportAttributes(lArrrReportAttributeVO);
				
				 String url = "ifms.htm?actionFlag=getEmpViewBill";
	               
	                StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
	                lObjStyleVO = report.getStyleList();
	                for (Integer lInt = 0; lInt < report.getStyleList().length; lInt++) {
	                	System.out.println("lObjStyleVO[lInt].getStyleId()->"+lObjStyleVO[lInt].getStyleId());
	                	System.out.println("lObjStyleVO[lInt].getStyleValue()->"+lObjStyleVO[lInt].getStyleValue());

	                    if (lObjStyleVO[lInt].getStyleId() == 26) {
	                        lObjStyleVO[lInt].setStyleValue(url);
	                    }
	                }
				
				
			}*/
			
			if (report.getReportCode().equals("80000980")) 
			{
				StringBuilder SBQuery = new StringBuilder();
				String deputation = (String) report.getParameterValue("deputation");
				 List tierIIBillList=null;
					tierIIBillList=getSrkaGrantReport(deputation);
					for (int J=0;J<tierIIBillList.size();J++)
					{
						sevaarthId=null;
						String EmployeeDetails=null;
						Object[] tupleSub1 = (Object[]) tierIIBillList.get(J);
						
						td = new ArrayList();
						td.add(J+1);
						td.add(tupleSub1[0].toString());
						
//						td.add(new URLData(tupleSub1[1].toString() ,"hrms.htm?actionFlag=ViewSrkaGrantApprove"));
						String url;
						if(deputation.equals("Y"))
						url="hrms.htm?actionFlag=ViewSrkaGrantApprove&asPopup=FALSE&deputation=Y&locID="+tupleSub1[2].toString();
						else
						url="hrms.htm?actionFlag=ViewSrkaGrantApprove&asPopup=FALSE&locID="+tupleSub1[2].toString();

						td.add(new URLData(tupleSub1[1].toString() ,url));
						tabledata.add(td);
					}
			}
			if (report.getReportCode().equals("9000532")) 
			{
				SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");	
	            Date date = new Date();	
	            String curDate = dateFormat.format(date);	
	            gLogger.info("=====Innside parent report=====");	
	            String header5 = this.space(5) + "Date :- " + this.space(2) + curDate;	
	            report.setAdditionalHeader(header5);
				 
				List TableData = null;
			
				StringBuilder SBQuery = new StringBuilder();
				String lStrYearId = (String) report.getParameterValue("Year");
				String lStrMonthId = (String) report.getParameterValue("Month");
				String lStrDdo = (String) report.getParameterValue("Ddo");
			
				List TableData1= getTreasuryWiseDtlsEmployeeWise(lStrYearId,lStrMonthId,lStrDdo);
				for (int i=0;i<TableData1.size();i++)
				{
					Object[] tupleSub = (Object[]) TableData1.get(i);
					td = new ArrayList();
					td.add(i+1);
					if (tupleSub[12] != null) 
					{
							td.add(tupleSub[12]);////$t31Jan22
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[13] != null) 
					{
							td.add(tupleSub[13]);////$t31Jan22
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[14] != null) 
					{
							td.add(tupleSub[14].toString());
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[11] != null) 
					{
							td.add(tupleSub[11]);////$t31Jan22
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[4] != null) 
					{
							td.add(tupleSub[4]);
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
					if (tupleSub[5] != null) 
					{
							td.add(tupleSub[5]);
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[6] != null) 
					{
							td.add(tupleSub[6]);
					}
					else
					{
							td.add("-");
					}
					if (tupleSub[7] != null) 
					{
							td.add(tupleSub[7]);
					}
					else
					{
							td.add("-");
					}			
					tabledata.add(td);
				}
	          }//report 9000532
		}//try
		catch (Exception e) 
		{
			gLogger.info("=====findReportData(): Exception is=====" + e, e);
		}
		return tabledata;
	}
	
	
	private List getTreasuryWiseDtlsEmployeeWise(String lStrYearId,String lStrMonthId, String ddo) {
		StringBuilder lSBQuery = new StringBuilder();// emplr_contri_arrears
		List SevarthIdList = null;

		SessionFactory sessionFactory1 = new Configuration().configure(
				"hibernate.cfg.xml").buildSessionFactory();
		Session neardrSession = sessionFactory1.openSession();

		try {
			lSBQuery.append(" ( ");
			lSBQuery.append("   select ");
			lSBQuery.append("   case when loc1.DEPARTMENT_ID=100006 then loc1.LOC_NAME else loc1.loc_name end, ");
			lSBQuery.append("   loc1.loc_name, ");
			lSBQuery.append("   loc1.loc_id, ");
			lSBQuery.append("   abc.PAYBILL_YEAR, ");
			lSBQuery.append("   abc.PAYBILL_MONTH, ");
			lSBQuery.append("   sum(abc.employee) as employee_total, ");
			lSBQuery.append("   sum(abc.DED_ADJUST) as deduct_adjust, ");
			lSBQuery.append("   sum(abc.DED_ADJUST+abc.employee) as Total, ");
			lSBQuery.append("   sysdate, ");
			lSBQuery.append("   abc.ddo_code, ");
			lSBQuery.append("   abc.ddo_name,abc.pran_no,abc.emp_name,abc.SEVARTH_ID,abc.DCPS_ID ");
			lSBQuery.append("   from ");
			lSBQuery.append("   ( ");
			lSBQuery.append("      select ");
			lSBQuery.append("      a.LOC_ID, ");
			lSBQuery.append("      a.PAYBILL_YEAR, ");
			lSBQuery.append("      a.PAYBILL_MONTH, ");
			lSBQuery.append("      a.pran_no, ");
			lSBQuery.append("      a.GROSS_AMT, ");
			lSBQuery.append("      a.NET_TOTAL, ");
			lSBQuery.append("      cast(a.employye-nvl(b.sd_amnt,0) as double) as employee, ");
			lSBQuery.append("      cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as DED_ADJUST, ");
			lSBQuery.append("      a.ddo_reg_no, ");
			lSBQuery.append("      a.ddo_code, ");
			lSBQuery.append("      a.ddo_name,a.emp_name,a.SEVARTH_ID,a.DCPS_ID ");
			lSBQuery.append("      from ");
			lSBQuery.append("      ( ");
			lSBQuery.append("         SELECT ");
			lSBQuery.append("         loc.LOC_ID, ");
			lSBQuery.append("         head.PAYBILL_YEAR, ");
			lSBQuery.append("         head.PAYBILL_MONTH, ");
			lSBQuery.append("         mstemp.PRAN_NO, ");
			lSBQuery.append("         sum(paybill.GROSS_AMT) as GROSS_AMT, ");
			lSBQuery.append("         sum(paybill.NET_TOTAL) as NET_TOTAL, ");
			lSBQuery.append("         sum(paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF) as employye, ");
			lSBQuery.append("         sum(paybill.DED_ADJUST+paybill.EMPLR_CONTRI_ARREARS+paybill.NPS_EMPLR_DIFFERENCE_ADJ) as DED_ADJUST, ");
			lSBQuery.append("         reg.ddo_reg_no, ");
			lSBQuery.append("         ddo.ddo_code, ");
			lSBQuery.append("         ddo.ddo_name,mstemp.emp_name,mstemp.SEVARTH_ID,mstemp.DCPS_ID ");
			lSBQuery.append("         FROM PAYBILL_HEAD_MPG head ");
			lSBQuery.append("         inner join NSDL_PAYBILL_DATA paybill on paybill.paybill_year=head.paybill_year ");
			lSBQuery.append("         and paybill.paybill_month=head.paybill_month ");
			lSBQuery.append("         and paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
			////lSBQuery.append("         and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF <=paybill.DED_ADJUST ");/////$t30Aug2022
			lSBQuery.append("         inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
			lSBQuery.append("         inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID ");
			lSBQuery.append("         inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
			lSBQuery.append("         inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			lSBQuery.append("         inner join MST_DTO_REG dto on left(dto.DTO_CD,2)=left(ddo.ddo_code,2) ");
			lSBQuery.append("         inner join CMN_LOCATION_MST loc on left(loc.LOCATION_CODE,2)=left(ddo.DDO_CODE,2) ");
			//lSBQuery.append("         where (loc.LOC_ID) =left('"+ddo+"',4) and ddo.ddo_code like '" + ddo.substring(0, 4) + "%' ");
			lSBQuery.append("         where (loc.LOC_ID) =left('"+ddo+"',4) and ddo.ddo_code like '" +ddo+ "%' ");
			lSBQuery.append("         and head.APPROVE_FLAG=1 ");
			lSBQuery.append("         and mstemp.PRAN_NO is not null ");
			lSBQuery.append("         and PRAN_ACTIVE=1 ");
			lSBQuery.append("         and mstemp.DCPS_OR_GPF='Y' ");
			lSBQuery.append("         and mstemp.REG_STATUS=1 ");
			lSBQuery.append("         and head.PAYBILL_YEAR= '"+lStrYearId+"' ");
			lSBQuery.append("         and head.PAYBILL_MONTH='"+lStrMonthId+"' ");
			lSBQuery.append("         group by loc.LOC_ID, ");
			lSBQuery.append("         head.PAYBILL_YEAR, ");
			lSBQuery.append("         head.PAYBILL_MONTH, ");
			lSBQuery.append("         mstemp.PRAN_NO, ");
			lSBQuery.append("         ddo.ddo_code, ");
			lSBQuery.append("         ddo.ddo_name, ");
			lSBQuery.append("         reg.ddo_reg_no,mstemp.emp_name,mstemp.SEVARTH_ID,mstemp.DCPS_ID ");
			lSBQuery.append("      ) ");
			lSBQuery.append("      a ");
			lSBQuery.append("      left outer join ");
			lSBQuery.append("      ( ");
			lSBQuery.append("         SELECT ");
			lSBQuery.append("         sd.SD_PRAN_NO, ");
			lSBQuery.append("         cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt, ");
			lSBQuery.append("         cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt, ");
			lSBQuery.append("         bh.YEAR, ");
			lSBQuery.append("         bh.MONTH, ");
			lSBQuery.append("         sd.ddo_reg_no, ");
			lSBQuery.append("         substr(bh.file_name,1,2) as treasury ");
			lSBQuery.append("         FROM NSDL_SD_DTLS sd ");
			lSBQuery.append("         inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			lSBQuery.append("         and bh.STATUS <>-1 ");
			//lSBQuery.append("         and substr(bh.file_name,1,2)=substr('"+ddo+"',1,2) and cast(sd.SD_EMP_AMOUNT as bigint) <= cast(sd.SD_EMPlr_AMOUNT as bigint) ");/////$t30Aug2022
			lSBQuery.append("         and substr(bh.file_name,1,2)=substr('"+ddo+"',1,2) ");/////$t30Aug2022
			lSBQuery.append("         and bh.file_name like '" + ddo.substring(0, 2) + "%' ");
			lSBQuery.append("         and bh.YEAR= '"+lStrYearId+"' ");
			lSBQuery.append("         and bh.MONTH= '"+lStrMonthId+"' ");
			lSBQuery.append("         group by sd.SD_PRAN_NO, ");
			lSBQuery.append("         bh.YEAR, ");
			lSBQuery.append("         bh.MONTH, ");
			lSBQuery.append("         sd.ddo_reg_no, ");
			lSBQuery.append("         substr(bh.file_name,1,2) ");
			lSBQuery.append("      ) ");
			lSBQuery.append("      b on b.SD_PRAN_NO=a.PRAN_NO ");
			lSBQuery.append("      and b.ddo_reg_no=a.ddo_reg_no ");
			lSBQuery.append("      and b.YEAR=a.PAYBILL_YEAR ");
			lSBQuery.append("      and b.MONTH=a.PAYBILL_MONTH ");
			lSBQuery.append("      where cast(a.employye-nvl(b.sd_amnt,0)as double) >0 ");
			lSBQuery.append("      and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 ");
			lSBQuery.append("   ) ");
			lSBQuery.append("   abc ");
			lSBQuery.append("   inner join CMN_LOCATION_MST loc1 on loc1.loc_id=abc.LOC_ID ");
			lSBQuery.append("   group by loc1.loc_name, ");
			lSBQuery.append("   loc1.loc_id, ");
			lSBQuery.append("   abc.PAYBILL_YEAR, ");
			lSBQuery.append("   abc.PAYBILL_MONTH, ");
			lSBQuery.append("   abc.ddo_code, ");
			lSBQuery.append("   abc.ddo_name, ");
			lSBQuery.append("   loc1.DEPARTMENT_ID,abc.pran_no,abc.emp_name,abc.SEVARTH_ID,abc.DCPS_ID ");
			lSBQuery.append("   order by abc.PAYBILL_YEAR,abc.paybill_month ");
			lSBQuery.append(" ) ");
			lSBQuery.append(" with ur ");
			
			Query lQuery =neardrSession.createSQLQuery(lSBQuery.toString());
			SevarthIdList= lQuery.list();
		} catch (Exception e) {
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}finally{
			gLogger.info("bf neardrSession.close();");
			neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}
	return SevarthIdList;
	}


	private String getCorrectSevarth(String sevaarthId) {
		StringBuilder lSBQuery = new StringBuilder();
		List SevarthIdList=null;
		try
		{
			lSBQuery = new StringBuilder();			
			lSBQuery.append(" SELECT mst.SEVARTH_ID ");
			lSBQuery.append("  FROM ORG_USER_MST user join ORG_EMP_MST emp on user.USER_id=emp.USER_ID ");
			lSBQuery.append("  join MST_DCPS_EMP mst on emp.USER_id=mst.ORG_EMP_MST_ID ");
			lSBQuery.append("  where user.USER_NAME='"+sevaarthId+"' ");
						
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			SevarthIdList= lQuery.list();
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}
		return (String) SevarthIdList.get(0);
	}

	
	private Double CalculateInterest(String financialYear,Double openingBalance,Double installmentAmt,Double days,String deputation) {
		// TODO Auto-generated method stub
		Double finslInt=0D;
		
		List lstRateOfInterest;
		try {
			if(deputation==null)
				deputation="N";
			
			lstRateOfInterest = getInterestCalculation(financialYear);
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
					String endDate= getLetterIdDate(sevaarthId);
					HashMap<String,String> map1=new HashMap();
					map1.put("01","13");//jan
					map1.put("02","14");//feb
					map1.put("03","15");//mar
					
					HashMap<String,String> map2=new HashMap();
					map2.put("01","15");//jan
					map2.put("02","15");//feb
					map2.put("03","15");//for march also 14 because till feb2022 we have to calculate interest
	                map2.put("04","15");
	                map2.put("05","15");
	                map2.put("06","15");
	                map2.put("07","15");
	                map2.put("08","15");
	                map2.put("09","15");
	                map2.put("10","15");
	                map2.put("11","15");
	                map2.put("12","15");
					Date date = Calendar.getInstance().getTime();
					SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
			        Date d1 = sdformat.parse(endDate);
			        Date d2 = sdformat.parse("15/03/2022");
			        
					String [] val=endDate.split("/");
					/*System.out.print("-->"+val[1]);
					System.out.print("-->"+map.get("12"));
					System.out.print("-->"+map.get(val[1]));*/
					int months;
					if(d1.compareTo(d2) >= 0){
					months=Integer.parseInt(map2.get(val[1]));
					if(Integer.valueOf(val[0])<15 )
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-3))/12));//apr-nov-8
					else
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-3))/12));//apr-dec-9
					}else{
					months=Integer.parseInt(map1.get(val[1]));
					if(Integer.valueOf(val[0])<15 )
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-4))/12));//apr-nov-8
					else
					finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-3))/12));//apr-dec-9
					} 
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
				 }//else
				}else{
					if(financialYear.equals("35")){						
						String endDate= getLetterIdDate(sevaarthId);
						
						HashMap<String,String> map=new HashMap();
						map.put("11","10");
						map.put("12","11");
						map.put("01","12");
						map.put("02","13");
						map.put("03","14");
						Date date = Calendar.getInstance().getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						
						if(endDate.equals(""))
						endDate = sdf.format(date);
						
						String [] val=endDate.split("/");
						/*System.out.print("-->"+val[1]);
						System.out.print("-->"+map.get("12"));
						System.out.print("-->"+map.get(val[1]));*/
						int months=Integer.parseInt(map.get(val[1]));
						
						if(Integer.valueOf(val[0])<15 )
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-3))/12));//apr-oct 7
						else
						finslInt=finslInt+Double.parseDouble(new DecimalFormat("##.##").format((0.071*openingBalance*(months-2))/12));//apr-nov 8
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
				    }//else	
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
//		return finslInt;
		//return (double) Math.round(finslInt);
		return (double) finslInt;
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

	
public List getTreasuryWiseDtls(Long locationId,String Year,String locationCode,String Location_code) throws Exception{
		
		StringBuilder lSBQuery = new StringBuilder();//emplr_contri_arrears
		List SevarthIdList=null;
		
		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session neardrSession = sessionFactory1.openSession();
		try
		{
			
			if( locationCode.equalsIgnoreCase("SELECT ALL") &&  Year.equalsIgnoreCase("SELECT ALL"))
			{
			}
			else
			{
			
			lSBQuery.append(" (select case when loc1.DEPARTMENT_ID=100006 then loc1.LOC_NAME  else loc1.loc_name end, ");
			lSBQuery.append("   loc1.loc_name, ");
			lSBQuery.append("   loc1.loc_id, ");////$t31Jan22
			lSBQuery.append("   abc.PAYBILL_YEAR, ");
			lSBQuery.append("   abc.PAYBILL_MONTH, ");
			//lSBQuery.append("   abc.pran_no, ");
			lSBQuery.append("   sum(abc.employee) as employee_total, ");
			lSBQuery.append("   sum(abc.DED_ADJUST) as deduct_adjust, ");
			lSBQuery.append("   sum(abc.DED_ADJUST+abc.employee) as Total, ");
			lSBQuery.append("   sysdate,abc.ddo_code,abc.ddo_name ");
			lSBQuery.append("   from ");
			lSBQuery.append(" ( ");
			lSBQuery.append("      select ");
			lSBQuery.append("      a.LOC_ID, ");
			lSBQuery.append("      a.PAYBILL_YEAR, ");
			lSBQuery.append("      a.PAYBILL_MONTH, ");
			lSBQuery.append("      a.pran_no, ");
			lSBQuery.append("      a.GROSS_AMT, ");
			lSBQuery.append("      a.NET_TOTAL, ");
			lSBQuery.append("      cast(a.employye-nvl(b.sd_amnt,0) as double) as employee, ");
			lSBQuery.append("      cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as DED_ADJUST, ");
			lSBQuery.append("      a.ddo_reg_no,a.ddo_code,a.ddo_name ");////$t31Jan22
			lSBQuery.append("      from ");
			lSBQuery.append("      ( ");
			lSBQuery.append("         SELECT ");
			lSBQuery.append("         loc.LOC_ID, ");
			lSBQuery.append("         head.PAYBILL_YEAR, ");
			lSBQuery.append("         head.PAYBILL_MONTH, ");
			lSBQuery.append("         mstemp.PRAN_NO, ");
			lSBQuery.append("         sum(paybill.GROSS_AMT) as GROSS_AMT, ");
			lSBQuery.append("         sum(paybill.NET_TOTAL) as NET_TOTAL, ");
			lSBQuery.append("         sum(paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF) as employye, ");////$t31Jan22
			lSBQuery.append("         sum(paybill.DED_ADJUST+paybill.EMPLR_CONTRI_ARREARS+paybill.NPS_EMPLR_DIFFERENCE_ADJ) as DED_ADJUST, ");////$t31Jan22
			/*lSBQuery.append("         sum(paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA) as employye, ");
            lSBQuery.append("         sum(paybill.DED_ADJUST+paybill.EMPLR_CONTRI_ARREARS) as DED_ADJUST, ");*/
			lSBQuery.append("         reg.ddo_reg_no,ddo.ddo_code,ddo.ddo_name ");////$t31Jan22
			lSBQuery.append("         FROM PAYBILL_HEAD_MPG head ");
			lSBQuery.append("         inner join NSDL_PAYBILL_DATA paybill on paybill.paybill_year=head.paybill_year ");
			lSBQuery.append("         and paybill.paybill_month=head.paybill_month ");
			lSBQuery.append("         and paybill.PAYBILL_GRP_ID =head.PAYBILL_ID ");
			////lSBQuery.append("         and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF <=paybill.DED_ADJUST ");/////$t30Aug2022
			lSBQuery.append("         inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
			lSBQuery.append("         inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID ");
			lSBQuery.append("         inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
			lSBQuery.append("         inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code ");
			lSBQuery.append("         inner join MST_DTO_REG dto on left(dto.DTO_CD,2)=left(ddo.ddo_code,2) ");
			lSBQuery.append("         inner join CMN_LOCATION_MST loc on left(loc.LOCATION_CODE,2)=left(ddo.DDO_CODE,2) ");
			lSBQuery.append("         where ");
			if(!locationCode.equalsIgnoreCase("SELECT ALL"))
			lSBQuery.append("          loc.LOC_ID="+locationCode+"  and ");
			else
				lSBQuery.append(" 	   (loc.LOC_ID)=("+Location_code+") and ");
			lSBQuery.append("          head.APPROVE_FLAG=1 ");
			lSBQuery.append("         and mstemp.PRAN_NO is not null ");
			lSBQuery.append("         and PRAN_ACTIVE=1 ");
			lSBQuery.append("         and mstemp.DCPS_OR_GPF='Y' ");
			lSBQuery.append("         and mstemp.REG_STATUS=1  ");
			if(!Year.equalsIgnoreCase("SELECT ALL"))
			lSBQuery.append("   and (( head.PAYBILL_YEAR=substr('"+Year+"',1,4) and head.PAYBILL_MONTH > 3 )   or  (head.PAYBILL_YEAR=substr('"+Year+"',6,9) and head.PAYBILL_MONTH <4)) ");
			lSBQuery.append("         group by loc.LOC_ID, ");
			lSBQuery.append("         head.PAYBILL_YEAR, ");
			lSBQuery.append("         head.PAYBILL_MONTH, ");
			lSBQuery.append("         mstemp.PRAN_NO,ddo.ddo_code,ddo.ddo_name, ");
			lSBQuery.append("         reg.ddo_reg_no ");
			lSBQuery.append("      ) ");
			lSBQuery.append("      a ");
			lSBQuery.append("      left outer join ");
			lSBQuery.append("      ( ");
			lSBQuery.append("         SELECT ");
			lSBQuery.append("         sd.SD_PRAN_NO, ");
			lSBQuery.append("         cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt, ");
			lSBQuery.append("         cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt, ");
			lSBQuery.append("         bh.YEAR, ");
			lSBQuery.append("         bh.MONTH, ");
			lSBQuery.append("         sd.ddo_reg_no, ");
			lSBQuery.append("          substr(bh.file_name,1,2) as treasury       ");
			lSBQuery.append("         FROM NSDL_SD_DTLS sd ");
			lSBQuery.append("         inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME ");
			//lSBQuery.append("         and bh.STATUS <>-1 and cast(sd.SD_EMP_AMOUNT as bigint) <= cast(sd.SD_EMPlr_AMOUNT as bigint) ");/////$t30Aug2022
			lSBQuery.append("         and bh.STATUS <>-1 ");/////$t30Aug2022
			if(!locationCode.equalsIgnoreCase("SELECT ALL"))
			lSBQuery.append("         and substr(bh.file_name,1,2)=substr("+Location_code+",1,2)  ");
			else
			lSBQuery.append("         and substr(bh.file_name,1,2)=substr("+Location_code+",1,2) ");
			if(!Year.equalsIgnoreCase("SELECT ALL"))
			lSBQuery.append("      and bh.file_name like '"+Location_code.substring(0,2)+"%'   and (( bh.YEAR=substr('"+Year+"',1,4) and bh.MONTH > 3 )   or  (bh.YEAR=substr('"+Year+"',6,9) and bh.MONTH <4)) ");
			lSBQuery.append("         group by sd.SD_PRAN_NO, ");
			lSBQuery.append("         bh.YEAR, ");
			lSBQuery.append("         bh.MONTH, ");
			lSBQuery.append("         sd.ddo_reg_no, ");
			lSBQuery.append("        substr(bh.file_name,1,2) ");
			lSBQuery.append("      ) ");
			lSBQuery.append("      b on b.SD_PRAN_NO=a.PRAN_NO ");
			lSBQuery.append("      and b.ddo_reg_no=a.ddo_reg_no ");
			lSBQuery.append("      and b.YEAR=a.PAYBILL_YEAR ");
			lSBQuery.append("      and b.MONTH=a.PAYBILL_MONTH ");
//			lSBQuery.append("      and b.treasury=a.LOC_ID ");
			lSBQuery.append("       where cast(a.employye-nvl(b.sd_amnt,0)as double) >0 ");
			lSBQuery.append("      and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 ");
			lSBQuery.append("   ) ");
			lSBQuery.append("   abc ");
			lSBQuery.append("   inner join CMN_LOCATION_MST loc1 on loc1.loc_id=abc.LOC_ID ");
			lSBQuery.append("   WHERE  ((abc.paybill_year=2015 and abc.paybill_month>=4) OR (abc.paybill_year>2015))");
			//lSBQuery.append("   group by loc1.loc_name,loc1.loc_id,abc.PAYBILL_YEAR,abc.PAYBILL_MONTH,loc1.DEPARTMENT_ID,abc.pran_no order by abc.PAYBILL_YEAR,abc.paybill_month,abc.pran_no ");
			lSBQuery.append("   group by loc1.loc_name,loc1.loc_id,abc.PAYBILL_YEAR,abc.PAYBILL_MONTH,abc.ddo_code,abc.ddo_name,loc1.DEPARTMENT_ID order by abc.PAYBILL_YEAR,abc.paybill_month ");/////$t31Jan22
			lSBQuery.append(" )  with ur ");

			Query lQuery =neardrSession.createSQLQuery(lSBQuery.toString());
			SevarthIdList= lQuery.list();
			}
		}
		catch(Exception e){
			gLogger.error(" Error in getEmpDtls " + e, e);
			e.printStackTrace();
			throw e;
		}finally{
			gLogger.info("bf neardrSession.close();");
			neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}
			return SevarthIdList;
	
		}
	
public List getTreasuryWiseDtlsSrka() throws Exception{

		
		StringBuilder lSBQuery = new StringBuilder();
		List SevarthIdList=null;
		
		try
		{
			lSBQuery.append(" SELECT loc.loc_name,loc.loc_id,dto.dto_reg_no,sum(cast(EMPLOYEE_TOTAL as bigint)),sum(cast(DEDUCT_ADJUST as bigint)),sum(cast(EMPLOYEE_TOTAL as bigint))+sum(cast(DEDUCT_ADJUST as bigint)) FROM  nps_mqt nps ");
			lSBQuery.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(nps.loc_id,1,2) and loc.DEPARTMENT_ID in (100003) and loc.loc_id <> 1111 ");
			lSBQuery.append(" inner join MST_DTO_REG dto on substr(dto.DTO_CODE,1,2)=substr(nps.loc_id,1,2) group by loc.loc_name,loc.loc_id,dto.dto_reg_no order by loc.loc_id "); 

			
		
						
			
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
	
public List getYear(String YearId, String lStrLocId,String Year) {
	
	Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    if(4>month+1){
        year = year-1;
    }

	List<Object> lArrYears = new ArrayList<Object>();
	SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    Session neardrSession = sessionFactory1.openSession();
	try {
		String lStrBufLang = "select FIN_YEAR_DESC,FIN_YEAR_DESC from SGVC_FIN_YEAR_MST where LANG_ID='en_US' and FIN_YEAR_CODE between '2007' and '"+year+"' order by FIN_YEAR_CODE";

		Query lObjQuery = neardrSession.createSQLQuery(lStrBufLang);

		List lLstResult = lObjQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		Object[] lArrData = null;

		if (lLstResult != null && !lLstResult.isEmpty()) {
			for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
				if(lIntCtr==0 && !YearId.equalsIgnoreCase("SELECT ALL"))
				{
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("SELECT ALL");
					lObjComboValuesVO.setDesc("SELECT ALL");
					lArrYears.add(lObjComboValuesVO);
				}
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
		sb.append(" select  LOCATION_CODE ,LOCATION_CODE||','||LOC_NAME FROM CMN_LOCATION_MST where LOCATION_CODE= '"+lStrLocationId+"' or PARENT_LOC_ID = '"+lStrLocationId+"' order by LOCATION_CODE ");
		Query selectQuery = neardrSession.createSQLQuery(sb.toString());
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				if(liCtr==0)
				{
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("SELECT ALL");
					lObjComboValuesVO.setDesc("SELECT ALL");
					lLstReturnList.add(lObjComboValuesVO);
				}
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

/* for tier II*/

public List getTierIIInterest(String SevaarthID) throws Exception{
	StringBuilder lSBQuery = new StringBuilder();
	List SevarthIdList=null;
	try
	{
		lSBQuery = new StringBuilder();
		lSBQuery.append(" SELECT diSTINCT t.DCPS_EMP_ID,cast(t8.yearly_amount as varchar) as TimeA,cast(t2.yearly_amount as varchar) as TimeB,cast(t3.yearly_amount as varchar) as TimeC,"
				+ "cast(t4.yearly_amount as varchar) as TimeD,cast(t5.yearly_amount as varchar) as TimeE ,empmst.EMP_NAME,empmst.SEVARTH_ID,empmst.DCPS_ID,case when empmst.PRAN_NO is null then 'N/A' else empmst.PRAN_NO end,  ");
		lSBQuery.append(" case when attach.dcps_emp_id is null then 'N' else 'Y' end ");
		lSBQuery.append(" FROM RLT_DCPS_SIXPC_YEARLY t ");
		lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and t8.status_flag='A' ");//$t 25Nov2021 
		lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and t2.status_flag='A' ");//$t 25Nov2021
		lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and t3.status_flag='A' ");//$t 25Nov2021
		lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and t4.status_flag='A' ");//$t 25Nov2021
		lSBQuery.append("  JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and t5.status_flag='A' ");//$t 25Nov2021
		lSBQuery.append(" inner join MST_DCPS_EMP empmst ");
		lSBQuery.append(" on t.dcps_emp_id=empmst.dcps_emp_id ");
		lSBQuery.append(" left outer join TIERII_ATTACH_DETACH_DTLS ATTACH on t.dcps_emp_id=attach.dcps_emp_id ");
		lSBQuery.append(" where t.status_flag='A' and t.YEARLY_AMOUNT <>0 and empmst.SEVARTH_ID ='"+SevaarthID+"' ");  //t.DCPS_EMP_ID = 9910000269 and (t.state_flag='0' or t.state_flag is null)
		lSBQuery.append(" ORDER BY t.DCPS_EMP_ID ASC ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		SevarthIdList= (ArrayList) lQuery.list();
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}	
		return SevarthIdList;
	}

public List getInterestCalculation(String finYearId) throws Exception{

	
	StringBuilder sb = new StringBuilder();
	List SevarthIdList=null;
	
	try
	{
		sb = new StringBuilder();
		
		if(finYearId.equalsIgnoreCase("32"))
			System.out.println("admin");

		sb.append("select r.INTEREST/100, DAYS (DATE(r.APPLICABLE_TO)) -DAYS (DATE(r.EFFECTIVE_FROM))+1    from MST_DCPS_INTEREST_RATE r "); 
		sb.append("join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =r.FIN_YEAR_ID "); 
		sb.append("where r.FIN_YEAR_ID >21 and r.FIN_YEAR_ID="+finYearId+" order by r.EFFECTIVE_FROM ");

		
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


public List getLoopingFOrIntereseCalculations() throws Exception{

	
	StringBuilder sb = new StringBuilder();
	List SevarthIdList=null;
	
	try
	{
		sb = new StringBuilder();

		sb.append("select distinct(r.FIN_YEAR_ID) ,fin.FIN_YEAR_DESC    from MST_DCPS_INTEREST_RATE r "); 
		sb.append("join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =r.FIN_YEAR_ID "); 
		sb.append("where r.FIN_YEAR_ID >21 order by r.FIN_YEAR_ID ");

		
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

//$t22Nov 
public List getOrderDetailsG(String BillNO,String Month,String Year) {
	List lLstEmpFiveInst = null;
	StringBuilder lSBQuery = null;
		try {
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append("	select em.sevarth_id,'Employee Name : '||em.emp_name||' Sevaarth id : '||em.sevarth_id||' Total Sixth Pay Arrear Amount : '||f.INST_AMOUNT || ' Total Amount Payable : '||(f.INST_AMOUNT+int_amount)  from TIERII_NAMUMNA_F_EMP_DETAILS f "); 
			lSBQuery.append("	join  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='"+BillNO+"' ");
			Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstEmpFiveInst = stQuery.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	return lLstEmpFiveInst;
}

public List getSrkaGrantReport(String deputation) {
	List lLstEmpFiveInst = null;
	StringBuilder lSBQuery = null;
		try {
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			lSBQuery = new StringBuilder();
			if(deputation.equals("Y"))
			lSBQuery.append(" select loc.LOC_NAME  ,count(s.BILL_ID ),loc.loc_id from TIERTWO_BILL_DTLS s join CMN_LOCATION_MST loc on loc.LOC_ID =substr(s.DDO_CODE,0,4) and s.BILL_STATUS =14 group by loc.LOC_NAME,loc.loc_id ");
			else
			lSBQuery.append(" select loc.LOC_NAME  ,count(s.BILL_ID ),loc.loc_id from TIERTWO_BILL_DTLS s join CMN_LOCATION_MST loc on loc.LOC_ID =substr(s.DDO_CODE,0,4) and s.BILL_STATUS =4 group by loc.LOC_NAME,loc.loc_id ");
 
			Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstEmpFiveInst = stQuery.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	return lLstEmpFiveInst;
}




public List getOrderDetailsY(String BillNO,String Month,String Year) {
	List lLstEmpFiveInst = null;
	StringBuilder lSBQuery = null;
		try {
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append("	select em.sevarth_id,'Employee Name : '||em.emp_name||' Sevaarth id : '||em.sevarth_id||' Total Sixth Pay Arrear Amount : '||f.INST_AMOUNT || ' Total Amount Payable : '||(f.INST_AMOUNT+int_amount)  from TIERII_NAMUMNA_F_EMP_DETAILS f "); 
			lSBQuery.append("	join  MST_DCPS_EMP em on em.DCPS_EMP_ID=f.DCPS_ID and f.BILLL_ID='"+BillNO+"' and f.status not in (-1) ");
			Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstEmpFiveInst = stQuery.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	return lLstEmpFiveInst;
}

public List getOrderDetails(String BillNO,String Month,String Year, String deputation) {
	List lLstEmpFiveInst = null;
	StringBuilder sb = null;
		try {
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			sb = new StringBuilder();
			
			sb.append(" SELECT DISTINCT em.emp_name,em.sevarth_id,em.dcps_id,em.pran_no, "); 
			sb.append(" cast(t8.yearly_amount as varchar) as TimeA,cast(t2.yearly_amount as varchar) as TimeB,cast(t3.yearly_amount as varchar) as TimeC, "); 
			sb.append(" cast(t4.yearly_amount as varchar) as TimeD,cast(t5.yearly_amount as varchar) as TimeE,f.INST_AMOUNT,f.int_amount,f.INST_AMOUNT+int_amount, ");
			
			if(deputation.equals("Y"))
			sb.append(" decode(f.status,'10','Pending','-11','Rejected','12','Approved'),f.REASON ");
			else
			sb.append(" decode(f.status,'0','Pending','-1','Rejected','2','Approved'),f.REASON ");
			
			sb.append(" FROM RLT_DCPS_SIXPC_YEARLY t JOIN RLT_DCPS_SIXPC_YEARLY t8 ON t.DCPS_EMP_ID=t8.DCPS_EMP_ID AND t8.fin_year_id=22 and t8.status_flag='A' "); 
			sb.append(" JOIN RLT_DCPS_SIXPC_YEARLY t2 ON t.DCPS_EMP_ID=t2.DCPS_EMP_ID AND t2.fin_year_id=23 and t2.status_flag='A' "); 
			sb.append(" JOIN RLT_DCPS_SIXPC_YEARLY t3 ON t.DCPS_EMP_ID=t3.DCPS_EMP_ID AND t3.fin_year_id=24 and t3.status_flag='A' "); 
			sb.append(" JOIN RLT_DCPS_SIXPC_YEARLY t4 ON t.DCPS_EMP_ID=t4.DCPS_EMP_ID AND t4.fin_year_id=25 and t4.status_flag='A' "); 
			sb.append(" JOIN RLT_DCPS_SIXPC_YEARLY t5 ON t.DCPS_EMP_ID=t5.DCPS_EMP_ID AND t5.fin_year_id=26 and t5.status_flag='A' "); 
			sb.append(" inner join MST_DCPS_EMP em on t.dcps_emp_id=em.dcps_emp_id inner join TIERII_NAMUMNA_F_EMP_DETAILS f on t.dcps_emp_id=f.DCPS_ID "); 
			sb.append(" where t.status_flag='A' ");
			sb.append(" and f.BILLL_ID='"+BillNO+"' and f.year='"+Year+"' and f.MONTH='"+Month+"' ");
			
			Query stQuery = ghibSession.createSQLQuery(sb.toString());
			lLstEmpFiveInst = stQuery.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	return lLstEmpFiveInst;
}

public String getLetterIdDate(String seva) {
	List lLstEmpFiveInst = null;
	String date = "";
	StringBuilder sb = null;
		try {
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			sb = new StringBuilder();
			
			sb.append(" SELECT to_char(t.CREATED_DATE,'dd/MM/yyyy') FROM TIERII_NAMUMNA_F_EMP_DETAILS t join mst_Dcps_emp m on t.dcps_id=m.dcps_emp_id ");
			sb.append(" where m.SEVARTH_ID='"+seva+"' and t.status in(0,2) ");
			
			Query stQuery = ghibSession.createSQLQuery(sb.toString());
			lLstEmpFiveInst = stQuery.list();
			
			if(lLstEmpFiveInst.size()>0)
			date=(String) lLstEmpFiveInst.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	return date;
}



public String space(int noOfSpace) {	
    String blank = "";	
    for(int i = 0; i < noOfSpace; ++i) {	
       blank = blank + " ";	
    }	
    return blank;
}
//jitu 15-Nov-2022
public List getAllTreasury(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

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
		//sb.append(" select  LOCATION_CODE ,LOCATION_CODE||','||LOC_NAME FROM CMN_LOCATION_MST where LOCATION_CODE= '"+lStrLocationId+"' or PARENT_LOC_ID = '"+lStrLocationId+"' order by LOCATION_CODE ");
		sb.append("SELECT LOCATION_CODE ,LOCATION_CODE||','||LOC_NAME FROM CMN_LOCATION_MST WHERE DEPARTMENT_ID=100003 and LOC_ID not in(9991,2028915,99991,1111) order by LOCATION_CODE ");
		Query selectQuery = neardrSession.createSQLQuery(sb.toString());
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				if(liCtr==0)
				{
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("SELECT ALL");
					lObjComboValuesVO.setDesc("SELECT ALL");
					lLstReturnList.add(lObjComboValuesVO);
				}
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

public List getTreasuryWiseContriDtlsSrka1(Long locationId,String Year,String locationCode,String Location_code) throws Exception{
	
	StringBuilder lSBQuery = new StringBuilder();//emplr_contri_arrears
	List SevarthIdList=null;
	String loc =Long.toString(locationId);
	SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
 Session neardrSession = sessionFactory1.openSession();
	try
	{
		

		lSBQuery.append("( "); 
lSBQuery.append("   ( "); 
lSBQuery.append("      select "); 
lSBQuery.append("      case when loc1.DEPARTMENT_ID=100006 then loc1.LOC_NAME else loc1.loc_name end, "); 
lSBQuery.append("      loc1.loc_name, "); 
lSBQuery.append("      loc1.loc_id, "); 
lSBQuery.append("      abc.PAYBILL_YEAR, "); 
lSBQuery.append("      abc.PAYBILL_MONTH, "); 
lSBQuery.append("      sum(abc.employee) as employee_total, "); 
lSBQuery.append("      sum(abc.DED_ADJUST) as deduct_adjust, "); 
lSBQuery.append("      sum(abc.DED_ADJUST+abc.employee) as Total, "); 
lSBQuery.append("      sysdate, "); 
lSBQuery.append("      abc.ddo_code, "); 
lSBQuery.append("      abc.ddo_name "); 
lSBQuery.append("      from "); 
lSBQuery.append("      ( "); 
lSBQuery.append("         select "); 
lSBQuery.append("         a.LOC_ID, "); 
lSBQuery.append("         a.PAYBILL_YEAR, "); 
lSBQuery.append("         a.PAYBILL_MONTH, "); 
lSBQuery.append("         a.pran_no, "); 
lSBQuery.append("         a.GROSS_AMT, "); 
lSBQuery.append("         a.NET_TOTAL, "); 
lSBQuery.append("         cast(a.employye-nvl(b.sd_amnt,0) as double) as employee, "); 
lSBQuery.append("         cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) as DED_ADJUST, "); 
lSBQuery.append("         a.ddo_reg_no, "); 
lSBQuery.append("         a.ddo_code, "); 
lSBQuery.append("         a.ddo_name "); 
lSBQuery.append("         from "); 
lSBQuery.append("         ( "); 
lSBQuery.append("            SELECT "); 
lSBQuery.append("            loc.LOC_ID, "); 
lSBQuery.append("            head.PAYBILL_YEAR, "); 
lSBQuery.append("            head.PAYBILL_MONTH, "); 
lSBQuery.append("            mstemp.PRAN_NO, "); 
lSBQuery.append("            sum(paybill.GROSS_AMT) as GROSS_AMT, "); 
lSBQuery.append("            sum(paybill.NET_TOTAL) as NET_TOTAL, "); 
lSBQuery.append("            sum(paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA+paybill.DCPS_PAY_DIFF) as employye, "); 
lSBQuery.append("            sum(paybill.DED_ADJUST+paybill.EMPLR_CONTRI_ARREARS+paybill.NPS_EMPLR_DIFFERENCE_ADJ) as DED_ADJUST, "); 
lSBQuery.append("            reg.ddo_reg_no, "); 
lSBQuery.append("            ddo.ddo_code, "); 
lSBQuery.append("            ddo.ddo_name "); 
lSBQuery.append("            FROM PAYBILL_HEAD_MPG head "); 
lSBQuery.append("            inner join NSDL_PAYBILL_DATA paybill on paybill.paybill_year=head.paybill_year "); 
lSBQuery.append("            and paybill.paybill_month=head.paybill_month "); 
lSBQuery.append("            and paybill.PAYBILL_GRP_ID =head.PAYBILL_ID "); 
lSBQuery.append("            inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID "); 
lSBQuery.append("            inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID "); 
lSBQuery.append("            inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID "); 
lSBQuery.append("            inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code "); 
lSBQuery.append("            inner join MST_DTO_REG dto on left(dto.DTO_CD,2)=left(ddo.ddo_code,2) "); 
lSBQuery.append("            inner join CMN_LOCATION_MST loc on left "); 
lSBQuery.append("            ( "); 
lSBQuery.append("               loc.LOCATION_CODE,2 "); 
lSBQuery.append("            ) "); 
lSBQuery.append("            =left(ddo.DDO_CODE,2) "); 
lSBQuery.append("            where loc.LOC_ID="+locationCode+"  "); 
lSBQuery.append("            and ((paybill.paybill_year=2015 and paybill.paybill_month>=4) OR (paybill.paybill_year>2015))");
lSBQuery.append("            and head.APPROVE_FLAG=1 "); 
lSBQuery.append("            and mstemp.PRAN_NO is not null "); 
lSBQuery.append("            and PRAN_ACTIVE=1 "); 
lSBQuery.append("            and mstemp.DCPS_OR_GPF='Y' "); 
lSBQuery.append("            and mstemp.REG_STATUS=1 "); 
lSBQuery.append("            group by loc.LOC_ID, "); 
lSBQuery.append("            head.PAYBILL_YEAR, "); 
lSBQuery.append("            head.PAYBILL_MONTH, "); 
lSBQuery.append("            mstemp.PRAN_NO, "); 
lSBQuery.append("            ddo.ddo_code, "); 
lSBQuery.append("            ddo.ddo_name, "); 
lSBQuery.append("            reg.ddo_reg_no "); 
lSBQuery.append("         ) "); 
lSBQuery.append("         a "); 
lSBQuery.append("         left outer join "); 
lSBQuery.append("         ( "); 
lSBQuery.append("            SELECT "); 
lSBQuery.append("            sd.SD_PRAN_NO, "); 
lSBQuery.append("            cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt, "); 
lSBQuery.append("            cast(sum(nvl(sd.SD_EMPlr_AMOUNT,0)) as double) as sd_emplr_amnt, "); 
lSBQuery.append("            bh.YEAR, "); 
lSBQuery.append("            bh.MONTH, "); 
lSBQuery.append("            sd.ddo_reg_no, "); 
lSBQuery.append("            substr(bh.file_name,1,2) as treasury "); 
lSBQuery.append("            FROM NSDL_SD_DTLS sd "); 
lSBQuery.append("            inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME "); 
lSBQuery.append("            and bh.STATUS <>-1 "); 
lSBQuery.append("            and substr(bh.file_name,1,2)=substr("+locationCode+",1,2) "); 
lSBQuery.append("            and bh.file_name like '"+locationCode.substring(0,2)+"%' "); 
lSBQuery.append("            and ((bh.YEAR =2015 and bh.MONTH>=4)OR (bh.YEAR >2015))"); 
lSBQuery.append("            group by sd.SD_PRAN_NO, "); 
lSBQuery.append("            bh.YEAR, "); 
lSBQuery.append("            bh.MONTH, "); 
lSBQuery.append("            sd.ddo_reg_no, "); 
lSBQuery.append("            substr(bh.file_name,1,2) "); 
lSBQuery.append("         ) "); 
lSBQuery.append("         b on b.SD_PRAN_NO=a.PRAN_NO "); 
lSBQuery.append("         and b.ddo_reg_no=a.ddo_reg_no "); 
lSBQuery.append("         and b.YEAR=a.PAYBILL_YEAR "); 
lSBQuery.append("         and b.MONTH=a.PAYBILL_MONTH "); 
lSBQuery.append("         where cast(a.employye-nvl(b.sd_amnt,0)as double) >0 "); 
lSBQuery.append("         and cast(a.DED_ADJUST-nvl(b.sd_emplr_amnt,0) as double) > 0 "); 
lSBQuery.append("      ) "); 
lSBQuery.append("      abc "); 
lSBQuery.append("      inner join CMN_LOCATION_MST loc1 on loc1.loc_id=abc.LOC_ID "); 
lSBQuery.append("      group by loc1.loc_name, "); 
lSBQuery.append("      loc1.loc_id, "); 
lSBQuery.append("      abc.PAYBILL_YEAR, "); 
lSBQuery.append("      abc.PAYBILL_MONTH, "); 
lSBQuery.append("      abc.ddo_code, "); 
lSBQuery.append("      abc.ddo_name, "); 
lSBQuery.append("      loc1.DEPARTMENT_ID "); 
lSBQuery.append("      order by abc.PAYBILL_YEAR,abc.paybill_month "); 
lSBQuery.append("   ) "); 
lSBQuery.append("   with ur "); 
lSBQuery.append(") ");
		Query lQuery =neardrSession.createSQLQuery(lSBQuery.toString());
		SevarthIdList= lQuery.list();
		
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}finally{
		gLogger.info("bf neardrSession.close();");
		neardrSession.close();
		gLogger.info("af neardrSession.close();");
	}
		return SevarthIdList;

	}
}
