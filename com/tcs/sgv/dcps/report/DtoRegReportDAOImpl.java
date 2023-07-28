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

public class DtoRegReportDAOImpl  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(DtoRegReportDAOImpl.class);
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
		
			
			
			if (report.getReportCode().equals("9000504")) 
			{
				
			
				List TableData4= getDtoGeneralReport();
				int month=0;
				int year=0;
				
				if(TableData4!=null && TableData4.size()>0)
				{
					for(int i=0; i<TableData4.size();i++ )
					{
						Object[] tuple1 = (Object[]) TableData4.get(i);
						td = new ArrayList();
						td.add(i+1);
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
						tabledata.add(td);
					}
				}
				
					else
					{
						td = new ArrayList();
						td.add("-");
						td.add("-");
						td.add("-");
						td.add("-");
						td.add("-");
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
	
	
	
	
public List getDtoGeneralReport(){
		
		List lLstResult = null;
		//Map fieldMap=new HashMap();
		StringBuffer lSBQuery = new StringBuffer();
		String dtoCnt="";
		try {		
			gLogger.info("Enter in getDtoGeneralReport ");
			
			
			lSBQuery.append(" SELECT loc.LOC_ID,loc.LOC_NAME,dto.DTO_REG_NO,to_char(dto.DATE_OF_REGISTRATION,'dd-MM-yyyy') FROM MST_DTO_REG dto inner join CMN_LOCATION_MST loc on  substr(loc.loc_id,1,2) = substr(dto.LOC_ID,1,2) and loc.department_id=100003 where dto.LOC_ID <> 1111 order by loc.LOC_ID ");
	
				
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		
			lLstResult = lQuery.list();
			
				
		 	    	 			 			 		
		
			}
		catch (Exception e) {
			gLogger.info("Exception occurred while retrieving data From getDtoGeneralReport(string) : " + e, e);
		}
		return lLstResult;
	}
	
	
	
	

}
