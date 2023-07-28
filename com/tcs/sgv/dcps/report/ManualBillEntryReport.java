package com.tcs.sgv.dcps.report;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.core.service.ServiceLocator;

public class ManualBillEntryReport  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(ManualBillEntryReport.class);
	ServiceLocator serviceLocator = null;
	Map requestAttributes = null;
	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException 
	{
		ArrayList tabledata = new ArrayList();
		String langId = report.getLangId();
		String locId = report.getLocId();
		String lStrSevaarthId="";
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
			
			
			// ********* EMPLOYEE CONTRIBUTION FOR EMPLOYEE
			if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "EMPCOR".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
			{

//				ReportParameterVO[] r = report.getParameters();
//				
//				for(int i=0; i<=r.length;i++)
//				{
//					r[i].setParameterType(11);
//				}
//			
			}
			

			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();
			String ddoCode="";
			String dcpsEmpId="";
			String treasuryCode="";
			if (report.getReportCode().equals("80000900")) 
			{
				gLogger.info("=====Innside report=====");
				// parameters from jsp
				
				if(report.getParameterValue("sevarthId")!=null && !"".equalsIgnoreCase(report.getParameterValue("sevarthId").toString()))
				{
					lStrSevaarthId = ((String) report
							.getParameterValue("sevarthId")).trim();
					gLogger.info("lStrSevaarthId" + lStrSevaarthId);
				}
 		 			 
				List TableData = null;
			
				StringBuilder SBQuery = new StringBuilder();
				
				SBQuery.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID,emp.DOJ,emp.PPAN,emp.PRAN_NO,emp.DDO_CODE,loc.LOC_NAME ,fin.fin_year_desc,mon.MONTH_NAME,t.contribution,cmn.lookup_name FROM mst_dcps_emp emp ");
				SBQuery.append(" left outer join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 ");
				SBQuery.append(" left outer join MST_DTO_REG dto on substr(dto.DTO_CD,1,2) = substr(loc.LOCATION_CODE,1,2) ");
				SBQuery.append(" inner join TRN_DCPS_CONTRIBUTION t on t.dcps_emp_id =  emp.DCPS_EMP_ID ");
				SBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on  t.fin_year_id = fin.fin_year_id ");
				SBQuery.append(" inner join SGVA_MONTH_MST mon on mon.MONTH_ID = t.MONTH_ID ");
				SBQuery.append(" inner join CMN_LOOKUP_MST cmn on cmn.lookup_id = t.type_of_payment ");
				SBQuery.append(" where 1=1 and t.manual_bill_entry is not null ");
				if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
				{
					SBQuery
					.append(" and emp.sevarth_id='"); 
					SBQuery
					.append(lStrSevaarthId); 
					SBQuery
					.append("' "); 
							
				}

				Session ghibSession = ServiceLocator.getServiceLocator()
						.getSessionFactorySlave().getCurrentSession();
				Query lQuery = ghibSession.createSQLQuery(SBQuery
						.toString());
				TableData= lQuery.list();
				int i = TableData.size();
				int count =1;
				if(TableData!=null && TableData.size() >0)
				{	
					for(int z = 0; z<i; z++){
					
					Object[] tuple = (Object[]) TableData.get(z);
					td = new ArrayList();
					if (tuple[0] != null) 
					{
							td.add(count);
					}
					else
					{
							td.add(count);
					}
					if (tuple[0] != null) 
					{
							td.add(tuple[0]);
//							dcpsEmpId=tuple[1].toString();
					}
					else
					{
							td.add("-");
					}
					if (tuple[1] != null) 
					{
							td.add(tuple[1]);
//							dcpsEmpId=tuple[1].toString();
					}
					else
					{
							td.add("-");
					}
					if (tuple[2] != null) 
					{
							td.add(tuple[2]);
					}
					else
					{
							td.add("-");
					}
					
					if (tuple[3] != null) 
					{
							td.add(tuple[3]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[4] != null) 
					{
							td.add(tuple[4]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[5] != null) 
					{
							td.add(tuple[5]);
					}
					else
					{
							td.add("-");
					}
					
					if (tuple[6] != null) 
					{
							td.add(tuple[6]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[7] != null) 
					{
							td.add(tuple[7]);
//							ddoCode=tuple[7].toString();
					}
					else
					{
							td.add("-");
					}
					if (tuple[8] != null) 
					{
							td.add(tuple[8]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[9] != null) 
					{
							td.add(tuple[9]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[10] != null) 
					{
							td.add(tuple[10]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[11] != null) 
					{
							td.add(tuple[11]);
					}
					else
					{
							td.add("-");
					}
					tabledata.add(td);
					count++;
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
	
	
	public String getSevarthId(Long userId) {

		
		StringBuilder lSBQuery = new StringBuilder();
		String SevarthId=null;
		/////////////////
		
			lSBQuery.append("SELECT dcps.SEVARTH_ID FROM mst_dcps_emp dcps INNER JOIN org_emp_mst org ON dcps.ORG_EMP_MST_ID = org.EMP_ID where org.USER_ID ="+userId); 
		
			Session ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery
			.toString());
			SevarthId= lQuery.list().get(0).toString();
			
			return SevarthId;
	
		}
		
}
