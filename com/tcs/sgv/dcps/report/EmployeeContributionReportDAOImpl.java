package com.tcs.sgv.dcps.report;
import java.util.ArrayList;
import java.util.Collection;
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

public class EmployeeContributionReportDAOImpl  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(EmployeeContributionReportDAOImpl.class);
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
			if (report.getReportCode().equals("9000312")) 
			{
				gLogger.info("=====Innside parent report=====");
				// parameters from jsp
				
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
				 if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "TO".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
					{
						 String ddoCd=getDdoCode(lStrEmpName, pranNo, lStrSevaarthId);
							if(ddoCd!=null && !"".equalsIgnoreCase(ddoCd))
							{
								ddoCd=ddoCd.trim();
								if(!ddoCd.substring(0, 4).equalsIgnoreCase(locationId.toString()))
								{
									String header1 = "<p style=\"text-align:center;color:red;\"><b> Employee does not belong to this Treasury.</b> </p>";
									report.setAdditionalHeader(header1);
								}
							
							}
							else
							{
								
								String header1 = "<p style=\"text-align:center;color:red;\"><b> Employee does not belong to this Treasury.</b> </p>";
								report.setAdditionalHeader(header1);
							}
					}
				 
				 
				 
				List TableData = null;
			
				StringBuilder SBQuery = new StringBuilder();
				
				
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "EMPCOR".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					lStrSevaarthId=getSevarthId(userId); 
				
				}
				
				
				SBQuery
				.append("SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID,emp.DOB,emp.DOJ,emp.PPAN,emp.PRAN_NO,emp.DDO_CODE,loc.LOC_NAME ,cast(dto.DTO_REG_NO as varchar) , substr(loc.LOC_ID,1,4)   FROM mst_dcps_emp emp left outer join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 left outer join MST_DTO_REG dto on substr(dto.DTO_CD,1,2) = substr(loc.LOCATION_CODE,1,2) where 1=1");
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
				/////////////////////////////////////
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
			if (report.getReportCode().equals("80000849")) 
			{
				gLogger.info("=====Innside child report=====:"+lStrSevaarthId);
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
				List TableDataSub = null;
				StringBuilder SBQuerySub = new StringBuilder();
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "EMPCOR".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					lStrSevaarthId=getSevarthId(userId); 
				
				}
				td = new ArrayList();
				if(!"".equalsIgnoreCase(lStrSevaarthId))
				{
					td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=9000313&action=generateReport&DirectReport=TRUE&displayOK=FALSE&sevarthId="+lStrSevaarthId+"&pranNo="+pranNo+"&empName="+lStrEmpName));
					
				}
				
				tabledata.add(td);
				System.out.println("hi--->");
				
			}			
			if (report.getReportCode().equals("9000313")) 
			{
				gLogger.info("=====Innside child report=====:"+lStrSevaarthId);
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
				List TableDataSub = null;
				StringBuilder SBQuerySub = new StringBuilder();
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "EMPCOR".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					lStrSevaarthId=getSevarthId(userId); 
				
				}
				SBQuerySub
				.append(" SELECT emp.EMP_NAME,emp.PPAN,a.DDO_REG_NO,cast(dto.DTO_REG_NO as varchar) ,a.SD_EMP_AMOUNT,a.SD_EMPLR_AMOUNT,a.MONTH_NAME,a.YEAR,a.SD_REMARK,a.SD_TOTAL_AMT,a.month_id from MST_DCPS_EMP emp left outer join (  SELECT sd.SD_PRAN_NO,sd.DDO_REG_NO,sd.SD_EMP_AMOUNT, sd.SD_EMPLR_AMOUNT,sd.SD_TOTAL_AMT,sd.SD_REMARK,bh.YEAR, mon.MONTH_NAME,mon.month_id FROM NSDL_SD_DTLS sd inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.status<>-1 inner join SGVA_MONTH_MST mon on bh.MONTH=mon.MONTH_ID and mon.LANG_ID='en_US') a on emp.PRAN_NO=a.SD_PRAN_NO  left outer join  CMN_LOCATION_MST loc  on substr(loc.LOCATION_CODE,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003  left outer join MST_DTO_REG dto on substr(dto.DTO_CD,1,2) = substr(loc.LOCATION_CODE,1,2) where 1=1 ");
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
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "EMPCOR".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					lStrSevaarthId=getSevarthId(userId); 
				
				}
				SBQuery1
				.append("SELECT emp.EMP_NAME,emp.DCPS_EMP_ID,emp.SEVARTH_ID,emp.DOB,emp.DOJ,emp.PPAN,emp.PRAN_NO,emp.DDO_CODE,loc.LOC_NAME ,dto.DTO_REG_NO , substr(loc.LOC_ID,1,4)  FROM mst_dcps_emp emp left outer join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 left outer join MST_DTO_REG dto on substr(dto.DTO_CD,1,2) = substr(loc.LOCATION_CODE,1,2) where 1=1");
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
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "EMPCOR".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					lStrSevaarthId=getSevarthId(userId); 
				
				}
				SBQuery2
				.append("SELECT emp.EMP_NAME,emp.DCPS_EMP_ID,emp.SEVARTH_ID,emp.DOB,emp.DOJ,emp.PPAN,emp.PRAN_NO,emp.DDO_CODE,loc.LOC_NAME ,dto.DTO_REG_NO , substr(loc.LOC_ID,1,4) FROM mst_dcps_emp emp left outer join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003 left outer join MST_DTO_REG dto on substr(dto.DTO_CD,1,2) = substr(loc.LOCATION_CODE,1,2) where 1=1");
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
				td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700097&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=29"));
				td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700097&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=30"));
				td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=700097&action=generateReport&DirectReport=TRUE&displayOK=FALSE&dcpsEmpId="+dcpsEmpId+"&ddoCode="+ddoCode+"&treasuryCode="+treasuryCode+"&yearId=31"));
			}
			else
			{
				td.add("-");
				td.add("-");
				td.add("-");
				td.add("-");
				td.add("-");
				td.add("-");
				td.add("-");
			
			
			}
			
			tabledata.add(td);}
		
			
			if (report.getReportCode().equals("80000848")) 
			{	
				
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "EMPCOR".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					lStrSevaarthId=getSevarthId(userId); 
					gLogger.info("lStrSevaarthId1111111^^^^^^^^^^^^^^^^^^^^^^" + lStrSevaarthId);

				
				}
				
				
			if(report.getParameterValue("sevarthId")!=null && !"".equalsIgnoreCase(report.getParameterValue("sevarthId").toString()))
			{
				lStrSevaarthId = ((String) report
						.getParameterValue("sevarthId")).trim();
				gLogger.info("lStrSevaarthId^^^^^^^^^^^^^^^^^^^^^^" + lStrSevaarthId);
			}
			
			td = new ArrayList();
			if(!"".equalsIgnoreCase(lStrSevaarthId))
			{
				td.add(new URLData("View","hrms.htm?actionFlag=reportService&reportCode=80000832&action=generateReport&DirectReport=TRUE&displayOK=FALSE&lStrSevaarthId="+lStrSevaarthId));
				
			}
			
			tabledata.add(td);
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

}
