package com.tcs.sgv.dcps.report;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportParameterVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

public class NpsStatusReportDAOImpl  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(NpsStatusReportDAOImpl.class);
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

			if (loginDetail.get("locationId")!=null && !"".equalsIgnoreCase(loginDetail.get("locationId").toString()))
			{
				locationId=Long.parseLong(loginDetail.get("locationId").toString());
				
			}
			
		if (loginDetail.get("userId")!=null && !"".equalsIgnoreCase(loginDetail.get("userId").toString()))
			{
				userId =Long.parseLong(loginDetail.get("userId").toString());

			}  
			gLogger.info("user id is ###########################"+userId);  


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
			if (report.getReportCode().equals("9000500")) 
			{
				gLogger.info("=====Innside parent report=====");
				// parameters from jsp
				
			
				String header1 = "<p style=\"text-align:center;\"><b> DTO and DDO Registration </b> </p>";
				report.setAdditionalHeader(header1);
				 
				List TableData = null;
			
				StringBuilder SBQuery = new StringBuilder();
				
			
				List TableData1= getDdoGeneralReport();
				td = new ArrayList();
				String dtoCnt=getDtoGeneralReport();
				if(dtoCnt!=null)
				{
					td.add(dtoCnt);
				}
				else
				{
					td.add("-");
				}
				td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=9000504&action=generateReport&DirectReport=TRUE&displayOK=FALSE"));
				if(TableData1!=null && TableData1.size() >0)
				{
					Object[] tuple1 = (Object[]) TableData1.get(0);
					if (tuple1[0] != null) 
					{
						td.add(tuple1[0].toString());
					}
					else
					{
						td.add("-");
					}
					if (tuple1[1] != null) 
					{
						td.add(tuple1[1].toString());
					}
					else
					{
						td.add("-");
					}
				}
				else
				{
					td.add("-");
					td.add("-");
					
				}
				td.add(new URLData("View","ifms.htm?actionFlag=DDORegistrationReport"));
				//td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=9000294&action=generateReport&DirectReport=TRUE&displayOK=FALSE&rptCd=1"));
				tabledata.add(td);
	          }
			if (report.getReportCode().equals("9000501")) 
			{
				String header1 = "<p style=\"text-align:center;\"><b> Employee Registration </b> </p>";
				report.setAdditionalHeader(header1);
				
				
				List TableData2= getStatReport();
				td = new ArrayList();
				if(TableData2!=null && TableData2.size()>0)
				{
					Object[] tuple1 = (Object[]) TableData2.get(0);
					if (tuple1[0] != null) 
					{
						td.add(tuple1[0].toString());
					}
					else
					{
						td.add("-");
					}
					if (tuple1[1] != null) 
					{
						td.add(tuple1[1].toString());
					}
					else
					{
						td.add("-");
					}
					
					if (tuple1[2] != null) 
					{
						td.add(tuple1[2].toString());
					}
					else
					{
						td.add("-");
					}
					if (tuple1[3] != null) 
					{
						td.add(tuple1[3].toString());
					}
					else
					{
						td.add("-");
					}
					td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=9000294&action=generateReport&DirectReport=TRUE&displayOK=FALSE&rptCd=1"));
				}
				else
				{
					td.add("-");
					td.add("-");
					td.add("-");
					td.add("-");
					td.add("-");
				}
				tabledata.add(td);
				
			}
			if (report.getReportCode().equals("9000502")) 
			{
				
				String header1 = "<p style=\"text-align:center;\"><b> Legacy Amount Sent to NSDL </b> </p>";
				report.setAdditionalHeader(header1);
				 
				
				
				List TableData3= getLegacyData();
				
				
				if(TableData3!=null && TableData3.size()>0)
				{
					for(int i=0; i<TableData3.size();i++ )
					{
						Object[] tuple1 = (Object[]) TableData3.get(i);
						double d=0;
						td = new ArrayList();
						if (tuple1[0] != null) 
						{
							td.add(tuple1[0].toString());
						}
						else
						{
							td.add("-");
						}
						if (tuple1[1] != null) 
						{
							td.add(tuple1[1]);
							d=d+Double.parseDouble(tuple1[1].toString());
						}
						else
						{
							td.add(0);
						}
						
						if (tuple1[2] != null) 
						{
							td.add(tuple1[2]);
							d=d+Double.parseDouble(tuple1[2].toString());
						}
						else
						{
							td.add(0);
						}
						if (tuple1[3] != null) 
						{
							td.add(tuple1[3]);
							d=d+Double.parseDouble(tuple1[3].toString());
						}
						else
						{
							td.add(0);
						}
						if (tuple1[4] != null) 
						{
							td.add(tuple1[4]);
							d=d+Double.parseDouble(tuple1[4].toString());
						}
						else
						{
							td.add(0);
						}
						if (tuple1[5] != null) 
						{
							td.add(tuple1[5]);
							d=d+Double.parseDouble(tuple1[5].toString());
						}
						else
						{
							td.add(0);
						}
						if (tuple1[6] != null) 
						{
							td.add(tuple1[6]);
							d=d+Double.parseDouble(tuple1[6].toString());
						}
						else
						{
							td.add(0);
						}
						td.add(d);
						tabledata.add(td);
					}
					
				}
				else
				{
					td = new ArrayList();
					td.add("-");
					td.add(0);
					td.add(0);
					td.add(0);
					td.add(0);
					td.add(0);
					td.add(0);
					td.add(0);
					tabledata.add(td);
				}
				
				
				
				
			}
			if (report.getReportCode().equals("9000503")) 
			{
				
				String header1 = "<p style=\"text-align:center;\"><b> File Generation Status </b> </p>";
				report.setAdditionalHeader(header1);
				 
				
				
				List TableData4= getScfDetails();
				HashMap h=getMonthDtls();
				int month=0;
				int year=0;
				 Date d= new Date();
				int currYear=d.getYear()+1900;
			     int currMonth=d.getMonth()+1;
			     
			     
			     for(int j=0;j<5;j++)
			     {
			    	 if(j==0)
			    	 {
			    		 if(currMonth==1)
					     {
					    	 month=12;
					    	 year=currYear-1;
					     }
					     else
					     {
					    	 month=currMonth-1;
					    	 year=currYear;
					     }
			    	 }
			    	 else
			    	 {
			    		 if(month==1)
					     {
					    	 month=12;
					    	 year=year-1;
					     }
					     else
					     {
					    	 month=month-1;
					    	
					     }
			    	 }
			    	 
					
					if(TableData4!=null && TableData4.size()>0)
					{
						boolean flag=false;
						for(int i=0; i<TableData4.size();i++ )
						{
							Object[] tuple1 = (Object[]) TableData4.get(i);
							if(tuple1[6] != null && month==Integer.parseInt(tuple1[6].toString()))
							{
								flag=true;
								td = new ArrayList();
								if (tuple1[0] != null) 
								{
									td.add(tuple1[0].toString());
								}
								else
								{
									td.add("-");
								}
								if (tuple1[1] != null) 
								{
									td.add(tuple1[1].toString());
								}
								else
								{
									td.add("0");
								}
								if (tuple1[2] != null) 
								{
									td.add(tuple1[2].toString());
								}
								else
								{
									td.add("0");
								}
								if (tuple1[3] != null) 
								{
									td.add(tuple1[3]);
								}
								else
								{
									td.add(0);
								}
								if (tuple1[4] != null) 
								{
									td.add(tuple1[4]);
								}
								else
								{
									td.add(0);
								}
								if (tuple1[5] != null) 
								{
									td.add(tuple1[5]);
								}
								else
								{
									td.add(0);
								}
								if (tuple1[6] != null) 
								{
									month=Integer.parseInt(tuple1[6].toString());
								}
								if (tuple1[7] != null) 
								{
									year=Integer.parseInt(tuple1[7].toString());
								}
								td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=9000240&action=generateReport&DirectReport=TRUE&displayOK=FALSE&year="+year+"&month="+month));
								
								tabledata.add(td);
							}
							
						}
						if(!flag)
						{
							td = new ArrayList();
							if(h!=null && h.containsKey(month))
							{
								td.add(h.get(month)+"-"+year);
							}
							else
							{
								td.add("-");
							}
							
							td.add("0");
							td.add("0");
							td.add(0);
							td.add(0);
							td.add("0");
							td.add("-");
							tabledata.add(td);
						}
					}
					
						else
						{
							td = new ArrayList();
							if(h!=null && h.containsKey(month))
							{
								td.add(h.get(month)+"-"+year);
							}
							else
							{
								td.add("-");
							}
							
							td.add("0");
							td.add("0");
							td.add(0);
							td.add(0);
							td.add("0");
							td.add("-");
							tabledata.add(td);
							
						}
			     }
			     
			     
					
					
					
					
					
					}
					
				
			
			
			
			
		}
		catch (Exception e) 
		{
			gLogger.info("=====findReportData(): Exception is=====" + e, e);
		}
		return tabledata;
	}
	
	
	
	public List getDdoGeneralReport(){
		
		List lLstResult = null;
		//Map fieldMap=new HashMap();
		StringBuffer lSBQuery = new StringBuffer();
		
		try {		
			gLogger.info("Enter in getDdoGeneralReport ");
			
			
			lSBQuery.append(" SELECT count(ddo.ddo_code) as total_ddo,count(reg.ddo_code) as total_reg_ddo from org_ddo_mst ddo  ");
			lSBQuery.append(" inner join cmn_location_mst loc on substr(loc.loc_id,1,2)=substr(ddo.ddo_code,1,2) and loc.department_id=100003  ");
			lSBQuery.append(" left join mst_ddo_reg reg on reg.ddo_code=ddo.ddo_code  ");
		
				
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		
			lLstResult = lQuery.list();
				
		 	    	 			 			 		
		
			}
		catch (Exception e) {
			gLogger.info("Exception occurred while retrieving data From getDdoGeneralReport(string) : " + e, e);
		}
		return lLstResult;
	}
	
public List getScfDetails(){
		
		List lLstResult = null;
		//Map fieldMap=new HashMap();
		StringBuffer lSBQuery = new StringBuffer();
		int year1=0;
		int year2=0;
		int month1=0;
		int month2=0;
		int month3=0;
		int month4=0;
		int month5=0;
		
		try {		
			gLogger.info("Enter in getDdoGeneralReport ");
			
			 Date d= new Date();
		    
		     int currYear=d.getYear()+1900;
		     int currMonth=d.getMonth()+1;
		     
		     year1=currYear;
		     List month1List=new ArrayList();
		     List month2List=new ArrayList();
		     
		     if(currMonth==1)
		     {
		    	 month1=12;
		    	 month2List.add(month1);
		    	 year2=currYear-1;
		     }
		     else
		     {
		    	 month1=currMonth-1;
		    	 if(year2 >0)
		    	 {
		    		 month2List.add(month1);
		    	 }
		    	 else
		    	 {
		    		 month1List.add(month1);
		    	 }
		    	 
		    	
		     }
		     if(month1==1)
		     {
		    	 month2=12;
		    	 month2List.add(month2);
		    	 year2=currYear-1;
		     }
		     else
		     {
		    	 month2=month1-1;
		    	 
		    	 if(year2 >0)
		    	 {
		    		 month2List.add(month2);
		    	 }
		    	 else
		    	 {
		    		 month1List.add(month2);
		    	 }
		     }
		     if(month2==1)
		     {
		    	 month3=12;
		    	 month2List.add(month3);
		    	 year2=currYear-1;
		     }
		     else
		     {
		    	 month3=month2-1;
		    	
		    	 if(year2 >0)
		    	 {
		    		 month2List.add(month3);
		    	 }
		    	 else
		    	 {
		    		 month1List.add(month3);
		    	 }
		    	
		     }
		     if(month3==1)
		     {
		    	 month4=12;
		    	 month2List.add(month4);
		    	 year2=currYear-1;
		    	 
		     }
		     else
		     {
		    	 month4=month3-1;
		    	 
		    	 if(year2 >0)
		    	 {
		    		 month2List.add(month4);
		    	 }
		    	 else
		    	 {
		    		 month1List.add(month4);
		    	 }
		     }
		     if(month4==1)
		     {
		    	 month5=12;
		    	 month2List.add(month5);
		    	 year2=currYear-1;
		    	
		     }
		     else
		     {
		    	 month5=month4-1;
		    	
		    	 if(year2 >0)
		    	 {
		    		 month2List.add(month5);
		    	 }
		    	 else
		    	 {
		    		 month1List.add(month5);
		    	 }
		     }
			
		     lSBQuery.append(" select m.month_name ||' - '|| a.year,a.FILE_NAME,a.BH_PRAN_COUNT,a.BH_EMP_AMOUNT,a.BH_EMPLR_AMOUNT,a.BH_TOTAL_AMT,a.month,a.year from ( ");
			lSBQuery.append(" SELECT bh.MONTH,bh.YEAR,count(DISTINCT(bh.FILE_NAME)) as FILE_NAME,sum(cast(bh.BH_PRAN_COUNT as bigint)) as BH_PRAN_COUNT,sum(cast(bh.BH_EMP_AMOUNT as double)) as BH_EMP_AMOUNT,sum(cast(bh.BH_EMPLR_AMOUNT as double)) as BH_EMPLR_AMOUNT,sum(cast(bh.BH_TOTAL_AMT as double)) as BH_TOTAL_AMT  FROM NSDL_BH_DTLS bh ");
			lSBQuery.append("  where bh.TRANSACTION_ID is not null and status <> -1 ");
			if(month2List.size()>0 && month1List.size()>0)
			{
				lSBQuery.append(" and  ( (bh.YEAR="+year1+" and bh.MONTH in (:month1List) ) or (bh.YEAR="+year2+" and bh.MONTH in (:month2List) )) ");
			}
			else if(month1List.size()>0)
			{
				lSBQuery.append(" and   (bh.YEAR="+year1+" and bh.MONTH in (:month1List) )  ");
			}
			else if(month2List.size()>0)
			{
				lSBQuery.append(" and   (bh.YEAR="+year2+" and bh.MONTH in (:month2List) )  ");
			}
			lSBQuery.append("  group by bh.MONTH,bh.YEAR ) a ");
			lSBQuery.append(" inner join SGVA_MONTH_MST m on m.month_id=a.month and m.LANG_ID='en_US'  order by a.year,a.month asc ") ;
			gLogger.info("Query--->"+lSBQuery.toString());
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			if(month2List.size()>0 && month1List.size()>0)
			{
				lQuery.setParameterList("month1List", month1List);
				lQuery.setParameterList("month2List", month2List);
			}
			else if(month1List.size()>0)
			{
				lQuery.setParameterList("month1List", month1List);
			}
			else if(month2List.size()>0)
			{
				lQuery.setParameterList("month2List", month2List);
			}
		
			lLstResult = lQuery.list();
				
		 	    	 			 			 		
		
			}
		catch (Exception e) {
			gLogger.info("Exception occurred while retrieving data From getDdoGeneralReport(string) : " + e, e);
		}
		return lLstResult;
	}
	
      public List getStatReport(){
		
		List lLstResult = null;
		//Map fieldMap=new HashMap();
		StringBuffer SBQueryQuery = new StringBuffer();
		
		try {		
			gLogger.info("Enter in getStatReport ");
			
			
		   SBQueryQuery.append(" select count(DCPS_EMP_ID) as total_dcps_emp, count(case when  emp.pran_no is not null then 1 else null end) as pran_not_null,count(case when emp.doj <= '2015-03-31' and emp.pran_no is not null then 1 else null end) as doj_lt_2015_3_31_and_pran_not_null,  count(case when emp.doj >= '2015-04-01' and emp.pran_no is not null then 1 else null end) as doj_gt_2015_4_1_and_pran_not_null from mst_dcps_emp emp "); 
       	   SBQueryQuery.append(" left join cmn_location_mst loc on substr(loc.loc_id,1,2) = substr(emp.ddo_code,1,2) and loc.department_id=100003 ");
       	  // SBQueryQuery.append(" left join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID ");
       	  // SBQueryQuery.append(" left join org_userpost_rlt post on post.user_id=org.user_id and post.activate_flag=1 ");
       	   SBQueryQuery.append(" where emp.reg_status=1 and emp.dcps_or_gpf='Y' and emp.form_status=1 and emp.emp_servend_dt > sysdate  and emp.ddo_code <> '1111222222' ");
      
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(SBQueryQuery.toString());

		
			lLstResult = lQuery.list();
				
		 	    	 			 			 		
		
			}
		catch (Exception e) {
			gLogger.info("Exception occurred while retrieving data From getStatReport(string) : " + e, e);
		}
		return lLstResult;
	}
      
      public List getLegacyData(){
  		
  		List lLstResult = null;
  		//Map fieldMap=new HashMap();
  		StringBuffer SBQueryQuery = new StringBuffer();
  		
  		try {		
  			gLogger.info("Enter in getLegacyData ");
  			
  			
  		   SBQueryQuery.append("  SELECT FIN_YEAR, sum(cast (nvl(EMP_CONTRI,0) as double)) as EMP_CONTRI,  sum(cast (nvl(EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI,   sum(cast (nvl(EMP_INT,0) as double)) as EMP_INT,   sum(cast (nvl(EMPLR_INT,0) as double)) as EMPLR_INT,    sum(cast (nvl(OPEN_EMP,0) as double)) as OPEN_EMP,       sum(cast (nvl(OPEN_EMPLR,0) as double)) as OPEN_EMPLR  FROM DCPS_LEGACY_DATA  group  by FIN_YEAR "); 
         	  
  			Session ghibSession = ServiceLocator.getServiceLocator()
  			.getSessionFactorySlave().getCurrentSession();
  			Query lQuery = ghibSession.createSQLQuery(SBQueryQuery.toString());

  		
  			lLstResult = lQuery.list();
  				
  		 	    	 			 			 		
  		
  			}
  		catch (Exception e) {
  			gLogger.info("Exception occurred while retrieving data From getLegacyData(string) : " + e, e);
  		}
  		return lLstResult;
  	}
	
public String getDtoGeneralReport(){
		
		List lLstResult = null;
		//Map fieldMap=new HashMap();
		StringBuffer lSBQuery = new StringBuffer();
		String dtoCnt="";
		try {		
			gLogger.info("Enter in getDtoGeneralReport ");
			
			
			lSBQuery.append(" SELECT count(*) FROM MST_DTO_REG where LOC_ID <> 1111  ");
	
				
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		
			lLstResult = lQuery.list();
			if(lLstResult!=null && lLstResult.size()>0)
			{
				dtoCnt=lLstResult.get(0).toString();
			}
				
		 	    	 			 			 		
		
			}
		catch (Exception e) {
			gLogger.info("Exception occurred while retrieving data From getDtoGeneralReport(string) : " + e, e);
		}
		return dtoCnt;
	}

public HashMap getMonthDtls(){
	
	List lLstResult = null;
	//Map fieldMap=new HashMap();
	StringBuffer lSBQuery = new StringBuffer();
	String dtoCnt="";
	HashMap h=new HashMap();
	try {		
		gLogger.info("Enter in getDtoGeneralReport ");
		
		
		lSBQuery.append(" SELECT MONTH_ID,MONTH_NAME FROM SGVA_MONTH_MST where LANG_ID='en_US'    ");

			
		Session ghibSession = ServiceLocator.getServiceLocator()
		.getSessionFactorySlave().getCurrentSession();
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

	
		lLstResult = lQuery.list();
		if(lLstResult!=null && lLstResult.size()>0)
		{
			for(int i=0;i<lLstResult.size();i++)
			{
				Object[] tuple1 = (Object[]) lLstResult.get(i);
				if(tuple1!=null && tuple1.length>0)
				{
					h.put(Integer.parseInt(tuple1[0].toString()), tuple1[1].toString());
				}
			}
		}
			
	 	    	 			 			 		
	
		}
	catch (Exception e) {
		gLogger.info("Exception occurred while retrieving data From getDtoGeneralReport(string) : " + e, e);
	}
	return h;
}
	
	
	
	

}
