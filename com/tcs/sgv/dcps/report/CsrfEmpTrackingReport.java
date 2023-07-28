package com.tcs.sgv.dcps.report;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.core.service.ServiceLocator;

public class CsrfEmpTrackingReport   extends DefaultReportDataFinder implements ReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(CsrfEmpTrackingReport.class);
	ServiceLocator serviceLocator = null;
	Map requestAttributes = null;
	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException 
	{
		ArrayList tabledata = new ArrayList();
		String langId = report.getLangId();
		String locId = report.getLocId();
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
			if (report.getReportCode().equals("9000531")) 
			{
				SimpleDateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");	
	            Date date = new Date();	
	            String curDate = dateFormat.format(date);	
	            gLogger.info("=====Innside parent report=====");	
	            String header5 = this.space(5) + "Date :- " + this.space(2) + curDate;	
	            report.setAdditionalHeader(header5);
				List TableData = null;
			
				StringBuilder SBQuery = new StringBuilder();
				
				String lStrSevarthId = (String) report.getParameterValue("sevarthId");
			
				List TableData1=getEmpDtls(lStrSevarthId,"one");
				if(TableData1.size()>0){
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
					if (tupleSub[3] != null) 
					{
						td.add(tupleSub[3]);
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
					if (tupleSub[5] == null) 
					{	
			            //tupleSub[7]=tupleSub[7].toString();
						if(tupleSub[7]==null){
						td.add("Available at DDO login for print form");
						}else if((tupleSub[7].toString()).equals("1")){
						td.add("Available at DDO login for print form and forward to Treasury");	
						}else if((tupleSub[7].toString()).equals("2")){
						td.add("Available at TO login for generating OPGM file");	
						}else if((tupleSub[7].toString()).equals("3")){
						td.add("OPGM file is generated");	
						}
					}
					else
					{
						//tupleSub[7]=tupleSub[7].toString();
						if(tupleSub[7]==null){
						td.add("PRAN No. is generated through manual process and available at DDO login for print only");
						}else if((tupleSub[7].toString()).equals("1")){
						td.add("PRAN No. is generated through manual process and available at DDO login for print only");	
						}else if((tupleSub[7].toString()).equals("2")){
						td.add("PRAN No. is generated through manual process and available at TO login for print only");	
						}else if((tupleSub[7].toString()).equals("5")){
						td.add("PRAN No. is generated through manual process and available at DDO login for print only");	
						}else if((tupleSub[7].toString()).equals("3")){
						td.add("PRAN No. is generated through OPGM module and available at DDO login for print only");	
						}
					}
					tabledata.add(td);
				}
			   }else{  
				   TableData1=getEmpDtls(lStrSevarthId,"two");
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
							if (tupleSub[3] != null) 
							{
								td.add(tupleSub[3]);
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
							if (tupleSub[5] != null) 
							{
								td.add(tupleSub[5]);
								
							}
							else
							{
									td.add("-");
							}
							if (tupleSub[5] != null) 
							{
								td.add("-");
								
							}
							else
							{
									td.add("-");
							}
							if (tupleSub[3] != null) 
							{
								td.add("Available at DDO login for updating CSRF form");
								
							}
							else
							{
									td.add("-");
							}
							
                            tabledata.add(td);
				 }
			   }
	          }
		   }catch (Exception e) 
			{
				gLogger.info("=====findReportData(): Exception is=====" + e, e);
				e.printStackTrace();
			}
			return tabledata;
		}


private List getEmpDtls(String lStrSevarthId, String chk) {
	StringBuilder sb = new StringBuilder();
	List empDtls=null;
	Session ghibSession;
	Query lQuery;
	try
	{	
		if(chk.equals("one")){
		sb.append(" SELECT e.emp_name,e.dcps_id,e.SEVARTH_ID,e.doj,e.ddo_code,e.PRAN_NO,f.opgm_id,f.stage ");
		sb.append(" FROM mst_dcps_emp e join FRM_FORM_S1_DTLS f on e.SEVARTH_ID=f.SEVARTH_ID ");
		sb.append(" where e.sevarth_ID='"+lStrSevarthId+"' and e.DCPS_OR_GPF='Y' and e.REG_STATUS='1' ");
		
		ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		lQuery = ghibSession.createSQLQuery(sb.toString());
		empDtls= lQuery.list();
		}else{	
			sb.append(" SELECT e.emp_name,e.dcps_id,e.SEVARTH_ID,e.doj,e.ddo_code,e.PRAN_NO ");
			sb.append(" FROM mst_dcps_emp e ");
			sb.append(" where e.sevarth_ID='"+lStrSevarthId+"' and e.DCPS_OR_GPF='Y' and e.REG_STATUS='1' ");
			
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			lQuery = ghibSession.createSQLQuery(sb.toString());
			empDtls= lQuery.list();
		}
	}
	catch(Exception e){
		gLogger.error(" Error in getEmpDtls " + e, e);
		e.printStackTrace();
		throw e;
	}
		return empDtls;
	}


public String space(int noOfSpace) {	
    String blank = "";	
    for(int i = 0; i < noOfSpace; ++i) {	
       blank = blank + " ";	
    }	
    return blank;
}
}//class
