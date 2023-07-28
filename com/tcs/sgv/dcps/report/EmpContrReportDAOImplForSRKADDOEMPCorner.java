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

public class EmpContrReportDAOImplForSRKADDOEMPCorner  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(EmpContrReportDAOImplForSRKADDOEMPCorner.class);
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
			String yearId="";
			String finYear1="";
			String finYear2="";
			
			
			if (report.getReportCode().equals("800009649")) 
			{
				gLogger.info("=====report for DDO SRKA Emp Corner report=====:"+lStrSevaarthId);
				if(report.getParameterValue("sevarthId")!=null && !"".equalsIgnoreCase(report.getParameterValue("sevarthId").toString()))
				{
					lStrSevaarthId = ((String) report
							.getParameterValue("sevarthId")).trim();
					gLogger.info("lStrSevaarthId" + lStrSevaarthId);
				}				
				if(report.getParameterValue("empName")!=null && !"".equalsIgnoreCase(report.getParameterValue("empName").toString()))
				{
					lStrEmpName = ((String) report
							.getParameterValue("empName"));
					gLogger.info("lStrEmpName" + lStrEmpName);

				}
				if(report.getParameterValue("pranNo")!=null && !"".equalsIgnoreCase(report.getParameterValue("pranNo").toString()))
				{
					pranNo = (String) report
					.getParameterValue("pranNo");
					gLogger.info("pranNo" + pranNo);
				}
				if(report.getParameterValue("yearId")!=null && !"".equalsIgnoreCase(report.getParameterValue("yearId").toString()))
				{
					List TableData3=null;
					yearId = (String) report.getParameterValue("yearId");
					if(!yearId.equals("")&& yearId!=null){
						List FinacialYearList=null;
						StringBuilder yearString = new StringBuilder();
						yearString.append("SELECT YEAR(FROM_DATE),year(TO_DATE) "); 
						yearString.append(" FROM SGVC_FIN_YEAR_MST WHERE FIN_YEAR_ID="+yearId);
						Session ghibSession1 = ServiceLocator.getServiceLocator()
								.getSessionFactorySlave().getCurrentSession();
						Query lQuerySub1 = ghibSession1.createSQLQuery(yearString
								.toString());
						
						
						TableData3= lQuerySub1.list();
						if(TableData3!=null && TableData3.size() >0)
						{
							for (int i=0;i<TableData3.size();i++)
							{
							Object[] tuple2 = (Object[]) TableData3.get(0);
							finYear1=tuple2[0].toString();
							finYear2=tuple2[1].toString();
							}
						}
						
					}
					gLogger.info("yearId" + yearId);
				}
				List TableDataSub = null;
				StringBuilder SBQuerySub = new StringBuilder();
				//String test=report.getParameterValue("UserType").toString();
				/*if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "EMPCOR".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					lStrSevaarthId=getSevarthId(userId); 
				
				}*/
				SBQuerySub
				.append(" SELECT emp.EMP_NAME,emp.PPAN,a.DDO_REG_NO,cast(dto.DTO_REG_NO as varchar) ,a.SD_EMP_AMOUNT,a.SD_EMPLR_AMOUNT,a.MONTH_NAME,a.YEAR,a.SD_REMARK,a.SD_TOTAL_AMT,a.month_id from MST_DCPS_EMP emp left outer join (  SELECT sd.SD_PRAN_NO,sd.DDO_REG_NO,sd.SD_EMP_AMOUNT, sd.SD_EMPLR_AMOUNT,sd.SD_TOTAL_AMT,sd.SD_REMARK,bh.YEAR, mon.MONTH_NAME,mon.month_id FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.status<>-1 inner join SGVA_MONTH_MST mon on bh.MONTH=mon.MONTH_ID and mon.LANG_ID='en_US'  and (bh.YEAR ='"+finYear1+"' and bh.MONTH in(4,5,6,7,8,9,10,11,12) OR (bh.YEAR = '"+finYear2+"' and bh.MONTH in(1,2,3)) )) a on emp.PRAN_NO=a.SD_PRAN_NO  left outer join  CMN_LOCATION_MST loc  on substr(loc.LOCATION_CODE,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003  left outer join MST_DTO_REG dto on substr(dto.DTO_CD,1,2) = substr(loc.LOCATION_CODE,1,2) where 1=1 ");
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "TO".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					SBQuerySub
					.append(" and substr(emp.DDO_CODE,1,4)='"); 
					SBQuerySub
					.append(locationId.toString()); 
					SBQuerySub
					.append("' "); 
				}
				if(lStrEmpName!=null && !"".equalsIgnoreCase(lStrEmpName))
				{

					SBQuerySub
					.append(" and upper(emp.emp_name) like upper('%"); 
					SBQuerySub
					.append(lStrEmpName); 
					SBQuerySub
					.append("%') "); 
							
				}
				if(pranNo!=null && !"".equalsIgnoreCase(pranNo))
				{
					SBQuerySub
					.append(" and a.SD_PRAN_NO='"); 
					SBQuerySub
					.append(pranNo); 
					SBQuerySub
					.append("' "); 
							
				}
				if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
				{
					SBQuerySub
					.append(" and emp.sevarth_id='"); 
					SBQuerySub
					.append(lStrSevaarthId); 
					SBQuerySub
					.append("' "); 
							
				}
				SBQuerySub
				.append(" order by a.YEAR,a.month_id"); 

				Session ghibSession = ServiceLocator.getServiceLocator()
						.getSessionFactorySlave().getCurrentSession();
				Query lQuerySub = ghibSession.createSQLQuery(SBQuerySub
						.toString());
				TableDataSub= lQuerySub.list();
				if(TableDataSub!=null && TableDataSub.size() >0)
				{
					for (int i=0;i<TableDataSub.size();i++)
					{
						Object[] tupleSub = (Object[]) TableDataSub.get(i);
						td = new ArrayList();
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
								td.add(0);
						}
						if (tupleSub[5] != null) 
						{
								td.add(tupleSub[5]);
						}
						else
						{
								td.add(0);
						}
						if (tupleSub[9] != null) 
						{
								td.add(tupleSub[9]);
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
			}
			//for emp corner
			/*if (report.getReportCode().equals("800009649")) 
			{*/
				if (report.getReportCode().equals("800009652")) 
				{
				gLogger.info("=====report for Emp Corner report=====:"+lStrSevaarthId);				
				if(report.getParameterValue("yearId")!=null && !"".equalsIgnoreCase(report.getParameterValue("yearId").toString()))
				{
					List TableData3=null;
					yearId = (String) report.getParameterValue("yearId");
					if(!yearId.equals("")&& yearId!=null){
						List FinacialYearList=null;
						StringBuilder yearString = new StringBuilder();
						yearString.append("SELECT YEAR(FROM_DATE),year(TO_DATE) "); 
						yearString.append(" FROM SGVC_FIN_YEAR_MST WHERE FIN_YEAR_ID="+yearId);
						Session ghibSession1 = ServiceLocator.getServiceLocator()
								.getSessionFactorySlave().getCurrentSession();
						Query lQuerySub1 = ghibSession1.createSQLQuery(yearString
								.toString());
						
						
						TableData3= lQuerySub1.list();
						if(TableData3!=null && TableData3.size() >0)
						{
							for (int i=0;i<TableData3.size();i++)
							{
							Object[] tuple2 = (Object[]) TableData3.get(0);
							finYear1=tuple2[0].toString();
							finYear2=tuple2[1].toString();
							}
						}
						
					}
					gLogger.info("yearId" + yearId);
				}
				lStrSevaarthId=getSevarthId(userId); 
				List employeeinfo = getEmpDetails(lStrSevaarthId);
				
				String empName="";
				String empPPAN="";
				
				if(employeeinfo!=null && employeeinfo.size() >0)
				{
					for (int i=0;i<employeeinfo.size();i++)
					{
					Object[] tuple2 = (Object[]) employeeinfo.get(0);
					empName=tuple2[0].toString();
					empPPAN=tuple2[2].toString();
					}
				}
				String header1 = "<b>" + "<font size=\"2px\"> "
						+ "Employee Name  : " +empName+ "</font></b><br>";
				String header2 = "<b>" + "<font size=\"2px\"> "
						+ "SEVARTH_ID  : " +lStrSevaarthId+ "</font></b><br>";
				String header3 = "<b>" + "<font size=\"2px\"> "
						+ "PPAN NO  : " +empPPAN+ "</font></b>";
				report.setAdditionalHeader(header1+header2+header3);			
				List TableDataSub = null;
				StringBuilder SBQuerySub = new StringBuilder();			
				
				
				//SBQuerySub.append(" SELECT emp.EMP_NAME,emp.PPAN,a.DDO_REG_NO,cast(dto.DTO_REG_NO as varchar) ,a.SD_EMP_AMOUNT,a.SD_EMPLR_AMOUNT,a.MONTH_NAME,a.YEAR,a.SD_REMARK,a.SD_TOTAL_AMT,a.month_id from MST_DCPS_EMP emp left outer join (  SELECT sd.SD_PRAN_NO,sd.DDO_REG_NO,sd.SD_EMP_AMOUNT, sd.SD_EMPLR_AMOUNT,sd.SD_TOTAL_AMT,sd.SD_REMARK,bh.YEAR, mon.MONTH_NAME,mon.month_id FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.status<>-1 inner join SGVA_MONTH_MST mon on bh.MONTH=mon.MONTH_ID and mon.LANG_ID='en_US'  and (bh.YEAR ='"+finYear1+"' and bh.MONTH in(4,5,6,7,8,9,10,11,12) OR (bh.YEAR = '"+finYear2+"' and bh.MONTH in(1,2,3)) )) a on emp.PRAN_NO=a.SD_PRAN_NO  left outer join  CMN_LOCATION_MST loc  on substr(loc.LOCATION_CODE,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003  left outer join MST_DTO_REG dto on substr(dto.DTO_CD,1,2) = substr(loc.LOCATION_CODE,1,2) where 1=1 ");
				
				SBQuerySub.append("SELECT "); 
				SBQuerySub.append("emp.EMP_NAME,emp.PPAN,a.DDO_REG_NO,cast(dto.DTO_REG_NO as varchar),a.SD_EMP_AMOUNT,a.SD_EMPLR_AMOUNT,a.MONTH_NAME,a.YEAR,a.SD_REMARK,a.SD_TOTAL_AMT, "); 
				SBQuerySub.append("a.month_id "); 
				SBQuerySub.append("from MST_DCPS_EMP emp "); 
				SBQuerySub.append("left outer join "); 
				SBQuerySub.append("(  SELECT  sd.SD_PRAN_NO,sd.DDO_REG_NO,sd.SD_EMP_AMOUNT,sd.SD_EMPLR_AMOUNT,sd.SD_TOTAL_AMT,sd.SD_REMARK,bh.YEAR,mon.MONTH_NAME,mon.month_id "); 
				SBQuerySub.append("   FROM NSDL_SD_DTLS sd "); 
				SBQuerySub.append("   inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME "); 
				SBQuerySub.append("   and bh.status<>-1 "); 
				SBQuerySub.append("   inner join SGVA_MONTH_MST mon on bh.MONTH=mon.MONTH_ID "); 
				SBQuerySub.append("   and mon.LANG_ID='en_US' "); 
				SBQuerySub.append("   and ( bh.YEAR ='"+finYear1+"'and bh.MONTH in(4,5,6,7,8,9,10,11,12)OR(bh.YEAR = '"+finYear2+"' and bh.MONTH in(1,2,3))) "); 
				SBQuerySub.append(")a on emp.PRAN_NO=a.SD_PRAN_NO "); 
				SBQuerySub.append("left outer join CMN_LOCATION_MST loc on substr "); 
				SBQuerySub.append("( "); 
				SBQuerySub.append("   loc.LOCATION_CODE,1,2 "); 
				SBQuerySub.append(") "); 
				SBQuerySub.append("= substr(emp.DDO_CODE,1,2) "); 
				SBQuerySub.append("and loc.DEPARTMENT_ID=100003 "); 
				SBQuerySub.append("left outer join MST_DTO_REG dto on substr(dto.DTO_CD,1,2)=substr(loc.LOCATION_CODE,1,2) where 1=1 ");
				if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
				{
					SBQuerySub
					.append(" and emp.sevarth_id='"); 
					SBQuerySub
					.append(lStrSevaarthId); 
					SBQuerySub
					.append("' "); 							
				}
				SBQuerySub.append("and (a.YEAR ='2020' and a.month_id in (4,5,6,7,8,9,10,11,12) OR (a.YEAR = '2021' and a.month_id in(1,2,3))) "); 
				SBQuerySub.append("order by a.YEAR,a.month_id "); 

				Session ghibSession = ServiceLocator.getServiceLocator()
						.getSessionFactorySlave().getCurrentSession();
				Query lQuerySub = ghibSession.createSQLQuery(SBQuerySub
						.toString());
				TableDataSub= lQuerySub.list();
				if(TableDataSub!=null && TableDataSub.size() >0)
				{
					for (int i=0;i<TableDataSub.size();i++)
					{
						Object[] tupleSub = (Object[]) TableDataSub.get(i);
						td = new ArrayList();
						/*if (tupleSub[0] != null) 
						{
								td.add(tupleSub[0]);
						}
						else
						{
								td.add("-");
						}*/
						/*if (tupleSub[1] != null) 
						{
								td.add(tupleSub[1]);
						}
						else
						{
								td.add("-");
						}*/
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
								td.add(0);
						}
						if (tupleSub[5] != null) 
						{
								td.add(tupleSub[5]);
						}
						else
						{
								td.add(0);
						}
						if (tupleSub[9] != null) 
						{
								td.add(tupleSub[9]);
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
		
		
	public String getDdoCode(String lStrEmpName, String pranNo,String lStrSevaarthId) throws Exception {

		/*Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}*/
		String ddoCode=null;

		List<Object> lLstAMBList = new ArrayList<Object>();
		try
		{
			Session lObjSession = serviceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			StringBuilder sblist = new StringBuilder();
			sblist.append("SELECT emp.DDO_CODE from MST_DCPS_EMP emp where 1=1  ");
			if(lStrEmpName!=null && !"".equalsIgnoreCase(lStrEmpName))
			{
				sblist
				.append(" and upper(emp.emp_name) like upper('%"); 
				sblist
				.append(lStrEmpName); 
				sblist
				.append("%') "); 
						
			}
			if(pranNo!=null && !"".equalsIgnoreCase(pranNo))
			{
				sblist
				.append(" and emp.PRAN_NO='"); 
				sblist
				.append(pranNo); 
				sblist
				.append("' "); 
						
			}
			if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
			{
				sblist
				.append(" and emp.sevarth_id='"); 
				sblist
				.append(lStrSevaarthId); 
				sblist
				.append("' "); 
						
			}		 
			Query lQuery = lObjSession.createSQLQuery(sblist.toString());

			
		
			List lLstList = lQuery.list();
			

			if(lLstList!= null && !lLstList.isEmpty() && lLstList.size()!=0 && lLstList.get(0)!=null)
			{				 
				ddoCode=lLstList.get(0).toString();
			}
		}			 			 

		catch(Exception e){
			gLogger.error(" Error in getDdoCode method :////////////////////// " + e, e);
			throw e;
		}
		return ddoCode;

	}
	
	public List getEmpDetails(String sevarthId){
		StringBuilder SBemployee = new StringBuilder();		
		Query lQuerySub2 =null;
		try {
			SBemployee.append("SELECT EMP_NAME,SEVARTH_ID,PPAN FROM mst_dcps_emp "); 
			SBemployee.append("WHERE SEVARTH_ID='"+sevarthId+"'");
			Session ghibSession2 = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			lQuerySub2 = ghibSession2.createSQLQuery(SBemployee.toString());
		} catch (Exception e) {
			gLogger.error(" Employee Datails not found " + e, e);
		}
		
		return lQuerySub2.list();
		
		
	}
}
