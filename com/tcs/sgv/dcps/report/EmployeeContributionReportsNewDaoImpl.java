package com.tcs.sgv.dcps.report;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.tcs.sgv.common.helper.SessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

public class EmployeeContributionReportsNewDaoImpl  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(EmployeeContributionReportsNewDaoImpl.class);
	

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
			Long locationId=0l;
			if (loginDetail.get("locationId")!=null && !"".equalsIgnoreCase(loginDetail.get("locationId").toString()))
			{
				locationId=Long.parseLong(loginDetail.get("locationId").toString());
				
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
			Long empId=SessionHelper.getEmpId(loginDetail);
			
			if (report.getReportCode().equals("9000312")) 
			{
				gLogger.info("=====Innside parent report=====");
				// parameters from jsp
				
				
				List TableData = null;
				StringBuilder SBQuery = new StringBuilder();
				SBQuery
				.append("SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID,emp.DOB,emp.DOJ,emp.PPAN,emp.PRAN_NO,emp.DDO_CODE,loc.LOC_NAME as TREASURAY_NAME,cast(dto.DTO_REG_NO as varchar) as DTO_NO, substr(loc.LOC_ID,1,4) as treasury_code  FROM mst_dcps_emp emp left outer join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 left outer join MST_DTO_REG dto on substr(dto.DTO_CODE,1,2) = substr(loc.LOC_ID,1,2) where 1=1");
				if(lStrEmpName!=null && !"".equalsIgnoreCase(lStrEmpName))
				{
					SBQuery
					.append(" and upper(emp.emp_name) like upper('%"); 
					SBQuery
					.append(lStrEmpName); 
					SBQuery
					.append("%') "); 
							
				}
				if(pranNo!=null && !"".equalsIgnoreCase(pranNo))
				{
					SBQuery
					.append(" and emp.PRAN_NO='"); 
					SBQuery
					.append(pranNo); 
					SBQuery
					.append("' "); 
							
				}
				if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
				{
					SBQuery
					.append(" and emp.sevarth_id='"); 
					SBQuery
					.append(lStrSevaarthId); 
					SBQuery
					.append("' "); 
							
				}
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "TO".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					SBQuery
					.append(" and substr(emp.DDO_CODE,1,4)='"); 
					SBQuery
					.append(locationId.toString()); 
					SBQuery
					.append("' "); 
				}
				Session ghibSession = ServiceLocator.getServiceLocator()
						.getSessionFactorySlave().getCurrentSession();
				Query lQuery = ghibSession.createSQLQuery(SBQuery
						.toString());
				TableData= lQuery.list();
				if(TableData!=null && TableData.size() >0)
				{
					Object[] tuple = (Object[]) TableData.get(0);
					td = new ArrayList();
					if (tuple[0] != null) 
					{
							td.add(tuple[0]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[1] != null) 
					{
							td.add(tuple[1]);
							dcpsEmpId=tuple[1].toString();
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
							ddoCode=tuple[7].toString();
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
					
					tabledata.add(td);
					if (tuple[10] != null) 
					{
						treasuryCode=tuple[10].toString();
					}
				}
				
			}
			if (report.getReportCode().equals("9000313")) 
			{
				gLogger.info("=====Innside child report=====");
		
				List TableDataSub = null;
				StringBuilder SBQuerySub = new StringBuilder();
				SBQuerySub
				.append("SELECT emp.EMP_NAME,emp.PPAN,a.DDO_REG_NO,cast(dto.DTO_REG_NO as varchar) as DTO_NO,a.SD_EMP_AMOUNT,a.SD_EMPLR_AMOUNT,a.MONTH_NAME,a.YEAR, a.SD_REMARK,a.SD_TOTAL_AMT,a.month_id from MST_DCPS_EMP emp left outer join (  SELECT sd.SD_PRAN_NO,sd.DDO_REG_NO,sd.SD_EMP_AMOUNT, sd.SD_EMPLR_AMOUNT,sd.SD_TOTAL_AMT,sd.SD_REMARK,bh.YEAR, mon.MONTH_NAME,mon.month_id FROM NSDL_SD_DTLS sd,NSDL_BH_DTLS bh, SGVA_MONTH_MST mon where  bh.FILE_NAME=sd.FILE_NAME and bh.MONTH=mon.MONTH_ID and mon.LANG_ID='en_US' and bh.status<>-1) a on emp.PRAN_NO=a.SD_PRAN_NO left outer join  CMN_LOCATION_MST loc  on substr(loc.LOC_ID,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 left outer join MST_DTO_REG dto on substr(dto.DTO_CODE,1,2) = substr(loc.LOC_ID,1,2) where 1=1");
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
						if (tupleSub[9] != null) 
						{
								td.add(tupleSub[9]);
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
			if (report.getReportCode().equals("9000314")) 
			{
				
				
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
				StringBuilder SBQuery1 = new StringBuilder();
				SBQuery1
				.append("SELECT emp.EMP_NAME,emp.DCPS_EMP_ID,emp.SEVARTH_ID,emp.DOB,emp.DOJ,emp.PPAN,emp.PRAN_NO,emp.DDO_CODE,loc.LOC_NAME as TREASURAY_NAME,dto.DTO_REG_NO as DTO_NO, substr(loc.LOC_ID,1,4) as treasury_code  FROM mst_dcps_emp emp left outer join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 left outer join MST_DTO_REG dto on substr(dto.DTO_CODE,1,2) = substr(loc.LOC_ID,1,2) where 1=1");
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "TO".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					SBQuery1
					.append(" and substr(emp.DDO_CODE,1,4)='"); 
					SBQuery1
					.append(locationId.toString()); 
					SBQuery1
					.append("' "); 
				}
				if(lStrEmpName!=null && !"".equalsIgnoreCase(lStrEmpName))
				{
					SBQuery1
					.append(" and upper(emp.emp_name) like upper('%"); 
					SBQuery1
					.append(lStrEmpName); 
					SBQuery1
					.append("%') "); 
							
				}
				if(pranNo!=null && !"".equalsIgnoreCase(pranNo))
				{
					SBQuery1
					.append(" and emp.PRAN_NO='"); 
					SBQuery1
					.append(pranNo); 
					SBQuery1
					.append("' "); 
							
				}
				if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
				{
					SBQuery1
					.append(" and emp.sevarth_id='"); 
					SBQuery1
					.append(lStrSevaarthId); 
					SBQuery1
					.append("' "); 
							
				}
				List TableData1=null;
				Session ghibSession1 = ServiceLocator.getServiceLocator()
						.getSessionFactorySlave().getCurrentSession();
				Query lQuery1 = ghibSession1.createSQLQuery(SBQuery1
						.toString());
				TableData1= lQuery1.list();
				if(TableData1!=null && TableData1.size() >0)
				{
					Object[] tuple1 = (Object[]) TableData1.get(0);
					if (tuple1[1] != null) 
					{
						dcpsEmpId=tuple1[1].toString();
					}


					if (tuple1[7] != null) 
					{
						ddoCode=tuple1[7].toString();
					}

					if (tuple1[10] != null) 
					{
						treasuryCode=tuple1[10].toString();
					}

					
				}
				
				td = new ArrayList();
				if(!"".equalsIgnoreCase(dcpsEmpId))
				{
					td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700016&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=20"));
					td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700016&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=21"));
					td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700016&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=22"));
					td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700016&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=23"));
					td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700016&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=24"));
					
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
			if (report.getReportCode().equals("9000315")) 
			{
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
				
				StringBuilder SBQuery2 = new StringBuilder();
				SBQuery2
				.append("SELECT emp.EMP_NAME,emp.DCPS_EMP_ID,emp.SEVARTH_ID,emp.DOB,emp.DOJ,emp.PPAN,emp.PRAN_NO,emp.DDO_CODE,loc.LOC_NAME as TREASURAY_NAME,dto.DTO_REG_NO as DTO_NO, substr(loc.LOC_ID,1,4) as treasury_code  FROM mst_dcps_emp emp left outer join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 left outer join MST_DTO_REG dto on substr(dto.DTO_CODE,1,2) = substr(loc.LOC_ID,1,2) where 1=1");
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "TO".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					SBQuery2
					.append(" and substr(emp.DDO_CODE,1,4)='"); 
					SBQuery2
					.append(locationId.toString()); 
					SBQuery2
					.append("' "); 
				}
				if(lStrEmpName!=null && !"".equalsIgnoreCase(lStrEmpName))
				{
					SBQuery2
					.append(" and upper(emp.emp_name) like upper('%"); 
					SBQuery2
					.append(lStrEmpName); 
					SBQuery2
					.append("%') "); 
							
				}
				if(pranNo!=null && !"".equalsIgnoreCase(pranNo))
				{
					SBQuery2
					.append(" and emp.PRAN_NO='"); 
					SBQuery2
					.append(pranNo); 
					SBQuery2
					.append("' "); 
							
				}
			if(lStrSevaarthId!=null && !"".equalsIgnoreCase(lStrSevaarthId))
			{
				SBQuery2
				.append(" and emp.sevarth_id='"); 
				SBQuery2
				.append(lStrSevaarthId); 
				SBQuery2
				.append("' "); 
						
			}
			List TableData2=null;
			Session ghibSession2 = ServiceLocator.getServiceLocator()
					.getSessionFactorySlave().getCurrentSession();
			Query lQuery2 = ghibSession2.createSQLQuery(SBQuery2
					.toString());
			TableData2= lQuery2.list();
			if(TableData2!=null && TableData2.size() >0)
			{
				Object[] tuple2 = (Object[]) TableData2.get(0);
				if (tuple2[1] != null) 
				{
					dcpsEmpId=tuple2[1].toString();
				}


				if (tuple2[7] != null) 
				{
					ddoCode=tuple2[7].toString();
				}

				if (tuple2[10] != null) 
				{
					treasuryCode=tuple2[10].toString();
				}

				
			}
			
			td = new ArrayList();
			if(!"".equalsIgnoreCase(dcpsEmpId))
			{
				td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700097&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=25"));
				td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700097&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=26"));
				td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700097&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=27"));
				td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700097&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=28"));
					
			}
			else
			{
				td.add("-");
				td.add("-");
				td.add("-");
				td.add("-");
			
			
			}
			
			tabledata.add(td);}
			
		}
		catch (Exception e) 
		{
			gLogger.info("=====findReportData(): Exception is=====" + e, e);
		}
		return tabledata;
	}

}
