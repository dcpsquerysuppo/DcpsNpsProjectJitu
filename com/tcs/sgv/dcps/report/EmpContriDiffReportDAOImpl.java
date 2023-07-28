package com.tcs.sgv.dcps.report;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

public class EmpContriDiffReportDAOImpl  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(EmpContriDiffReportDAOImpl.class);
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException 
	{
		ArrayList tabledata = new ArrayList();
		
		
		Long finYearId=0l;
		String treasuryCode="";
		
		
		ArrayList td;
		try
		{
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
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
			
				if(report.getParameterValue("yearId")!=null && !"".equalsIgnoreCase(report.getParameterValue("yearId").toString()))
				{
					finYearId = Long.parseLong(report.getParameterValue("yearId").toString());
				}
				if(report.getParameterValue("treasuryCode")!=null && !"".equalsIgnoreCase(report.getParameterValue("treasuryCode").toString()))
				{
					treasuryCode = report.getParameterValue("treasuryCode").toString();
					
				}
				if(treasuryCode!=null && !"".equalsIgnoreCase(treasuryCode))
				{
					if("CmnPool".equalsIgnoreCase(treasuryCode))
					{
						tabledata=getCmnPoolEmpDtlsWithDifference(finYearId);
					}
					else
					{
						tabledata=getEmpDtlsWithDifference(finYearId,treasuryCode);
					}
					
				}
				
			
		}
		catch (Exception e) 
		{
			gLogger.info("=====findReportData(): Exception is=====" + e, e);
		}
		return tabledata;
	}
	public ArrayList getCmnPoolEmpDtlsWithDifference(Long finYearId) 
	{

		ArrayList tabledata = new ArrayList();
		SgvcFinYearMst lObjSgvcFinYearMst = null;
		String lStrFinYearStartDate = null;
		String lStrFinYearEndDate = null;
		
		try
		{
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class, serviceLocator.getSessionFactory());
			lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(finYearId);
			lStrFinYearStartDate = lObjSgvcFinYearMst.getFromDate().toString().trim();
			lStrFinYearEndDate = lObjSgvcFinYearMst.getToDate().toString().trim();
		
			List TableData = null;
			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID,mst.month_id ,mst.MONTH_NAME ,case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) ");
			SBQuery.append(" else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,trn.contribution from TRN_DCPS_CONTRIBUTION trn  ");
			SBQuery.append("  inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = trn.FIN_YEAR_ID ");
			SBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =trn.MONTH_ID ");
			SBQuery.append(" inner join mst_dcps_emp emp on emp.DCPS_EMP_ID=trn.DCPS_EMP_ID and emp.DCPS_OR_GPF='Y' and emp.REG_STATUS=1 and emp.DDO_CODE is null ");
			SBQuery.append(" WHERE  trn.fin_year_id="+finYearId+" and  trn.TYPE_OF_PAYMENT=700046 ");
			SBQuery.append(" and trn.voucher_date between '" + lStrFinYearStartDate+ "' AND '" + lStrFinYearEndDate + "'  AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y' ");
			SBQuery.append(" and (trn.is_missing_credit is null and trn.IS_CHALLAN is null) order by emp.DCPS_ID,year, mst.month_id ");
			
			Session ghibSession = ServiceLocator.getServiceLocator()
					.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(SBQuery
					.toString());
			TableData= lQuery.list();
			boolean isSameEmployee=false;
			List l=null;
			String dcpsId="";
			ArrayList td;
			
			Long month=0l;
			if(TableData!=null && TableData.size() >0)
			{
				for(int i=0;i<TableData.size();i++)
				{
					Object[] obj = (Object[]) TableData.get(i);
					Object[] obj1=null;
					if(i < (TableData.size()-1))
					{
						obj1=(Object[]) TableData.get(i+1);
					}
					
					if(obj!=null && obj.length>0 && obj1!=null && obj1.length>0)
					{
						if(obj[1].toString().equalsIgnoreCase(obj1[1].toString()) && !obj[6].toString().equalsIgnoreCase(obj1[6].toString()))
						{
							td = new ArrayList();
							if(!obj[1].toString().equalsIgnoreCase(dcpsId))
							{
								if(obj[0]!=null)
								{
									td.add(obj[0]);
								}
								else
								{
									td.add("-");
								}
								if(obj[1]!=null)
								{
									td.add(obj[1]);
								}
								else
								{
									td.add("-");
								}
								if(obj[2]!=null)
								{
									td.add(obj[2]);
								}
								else
								{
									td.add("-");
								}
								
								td.add("-");
								
								td.add("-");
								if(obj[4]!=null)
								{
									td.add(obj[4]);
								}
								else
								{
									td.add("-");
								}
								if(obj[5]!=null)
								{
									td.add(obj[5].toString());
								}
								else
								{
									td.add("-");
								}
								if(obj[6]!=null)
								{
									td.add(obj[6]);
								}
								else
								{
									td.add("-");
								}
								tabledata.add(td);
								
								td = new ArrayList();
								StyledData dataStyle1 = new StyledData();
								dataStyle1.setData("");
								dataStyle1.setColspan(5);
								td.add(dataStyle1);
								td.add("");
								td.add("");
								td.add("");
								td.add("");
								month=Long.parseLong(obj1[3].toString());
								if(obj1[4]!=null)
								{
									td.add(obj1[4]);
								}
								else
								{
									td.add("-");
								}
								if(obj1[5]!=null)
								{
									td.add(obj1[5].toString());
								}
								else
								{
									td.add("-");
								}
								if(obj1[6]!=null)
								{
									td.add(obj1[6]);
								}
								else
								{
									td.add("-");
								}
								tabledata.add(td);
							}
							else
							{
								if(month!=Long.parseLong(obj[3].toString()))
								{
									td = new ArrayList();
									StyledData dataStyle1 = new StyledData();
									dataStyle1.setData("");
									dataStyle1.setColspan(5);
									td.add(dataStyle1);
									td.add("");
									td.add("");
									td.add("");
									td.add("");
									if(obj[4]!=null)
									{
										td.add(obj[4]);
									}
									else
									{
										td.add("-");
									}
									if(obj[5]!=null)
									{
										td.add(obj[5].toString());
									}
									else
									{
										td.add("-");
									}
									if(obj[6]!=null)
									{
										td.add(obj[6]);
									}
									else
									{
										td.add("-");
									}
									tabledata.add(td);
								}
								
								
								td = new ArrayList();
								StyledData dataStyle2 = new StyledData();
								dataStyle2.setData("");
								dataStyle2.setColspan(5);
								td.add(dataStyle2);
								td.add("");
								td.add("");
								td.add("");
								td.add("");
								month=Long.parseLong(obj1[3].toString());
								if(obj1[4]!=null)
								{
									td.add(obj1[4]);
								}
								else
								{
									td.add("-");
								}
								if(obj1[5]!=null)
								{
									td.add(obj1[5].toString());
								}
								else
								{
									td.add("-");
								}
								if(obj1[6]!=null)
								{
									td.add(obj1[6]);
								}
								else
								{
									td.add("-");
								}
								tabledata.add(td);
								
							}
							dcpsId=obj[1].toString();
						}
					}
				}
			}
			
		}
		catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return tabledata;
	
		
	}
	
	public ArrayList getEmpDtlsWithDifference(Long finYearId,String treasuryCode) 
	{
		ArrayList tabledata = new ArrayList();
		SgvcFinYearMst lObjSgvcFinYearMst = null;
		String lStrFinYearStartDate = null;
		String lStrFinYearEndDate = null;
		String subStrTreasury="";
		try
		{
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class, serviceLocator.getSessionFactory());
			lObjSgvcFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(finYearId);
			lStrFinYearStartDate = lObjSgvcFinYearMst.getFromDate().toString().trim();
			lStrFinYearEndDate = lObjSgvcFinYearMst.getToDate().toString().trim();
			List TableData = null;
			StringBuilder SBQuery = new StringBuilder();
			subStrTreasury=treasuryCode.substring(0, 2);
			SBQuery.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,emp.SEVARTH_ID,emp.DDO_CODE,mst.month_id ,mst.MONTH_NAME ,case when mst.month_id >3 then cast(fin.FIN_YEAR_CODE as bigint) ");
			SBQuery.append(" else cast((fin.FIN_YEAR_CODE)+1 as bigint) end as year,trn.contribution from TRN_DCPS_CONTRIBUTION trn  ");
			SBQuery.append("  inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID = trn.FIN_YEAR_ID ");
			SBQuery.append(" inner join SGVA_MONTH_MST mst on mst.MONTH_ID =trn.MONTH_ID ");
			SBQuery.append(" inner join mst_dcps_emp emp on emp.DCPS_EMP_ID=trn.DCPS_EMP_ID and emp.DCPS_OR_GPF='Y' and emp.REG_STATUS=1 and substr(emp.DDO_CODE,1,2)='" + subStrTreasury + "' ");
			SBQuery.append(" WHERE  trn.fin_year_id="+finYearId+"   and  trn.TYPE_OF_PAYMENT=700046 ");
			SBQuery.append(" and trn.voucher_date between '" + lStrFinYearStartDate+ "' AND '" + lStrFinYearEndDate + "' AND trn.REG_STATUS = 1  and  trn.EMPLOYER_CONTRI_FLAG = 'Y' ");
			SBQuery.append(" and (trn.is_missing_credit is null and trn.IS_CHALLAN is null) order by emp.DCPS_ID,year, mst.month_id ");
			
			Session ghibSession = ServiceLocator.getServiceLocator()
					.getSessionFactorySlave().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(SBQuery
					.toString());
			TableData= lQuery.list();
			boolean isSameEmployee=false;
			List l=null;
			String dcpsId="";
			ArrayList td;
			String treasuryName="";
			treasuryName=getTreasuryName(treasuryCode);
			Long month=0l;
			if(TableData!=null && TableData.size() >0)
			{
				for(int i=0;i<TableData.size();i++)
				{
					Object[] obj = (Object[]) TableData.get(i);
					Object[] obj1=null;
					if(i < (TableData.size()-1))
					{
						obj1=(Object[]) TableData.get(i+1);
					}
					
					if(obj!=null && obj.length>0 && obj1!=null && obj1.length>0)
					{
						if(obj[1].toString().equalsIgnoreCase(obj1[1].toString()) && !obj[7].toString().equalsIgnoreCase(obj1[7].toString()))
						{
							td = new ArrayList();
							if(!obj[1].toString().equalsIgnoreCase(dcpsId))
							{
								if(obj[0]!=null)
								{
									td.add(obj[0]);
								}
								else
								{
									td.add("-");
								}
								if(obj[1]!=null)
								{
									td.add(obj[1]);
								}
								else
								{
									td.add("-");
								}
								if(obj[2]!=null)
								{
									td.add(obj[2]);
								}
								else
								{
									td.add("-");
								}
								if(obj[3]!=null)
								{
									td.add(obj[3]);
								}
								else
								{
									td.add("-");
								}
								td.add(treasuryName);
								if(obj[5]!=null)
								{
									td.add(obj[5]);
								}
								else
								{
									td.add("-");
								}
								if(obj[6]!=null)
								{
									td.add(obj[6].toString());
								}
								else
								{
									td.add("-");
								}
								if(obj[7]!=null)
								{
									td.add(obj[7]);
								}
								else
								{
									td.add("-");
								}
								tabledata.add(td);
								
								td = new ArrayList();
								StyledData dataStyle1 = new StyledData();
								dataStyle1.setData("");
								dataStyle1.setColspan(5);
								td.add(dataStyle1);
								td.add("");
								td.add("");
								td.add("");
								td.add("");
								month=Long.parseLong(obj1[4].toString());
								if(obj1[5]!=null)
								{
									td.add(obj1[5]);
								}
								else
								{
									td.add("-");
								}
								if(obj1[6]!=null)
								{
									td.add(obj1[6].toString());
								}
								else
								{
									td.add("-");
								}
								if(obj1[7]!=null)
								{
									td.add(obj1[7]);
								}
								else
								{
									td.add("-");
								}
								tabledata.add(td);
							}
							else
							{
								if(month!=Long.parseLong(obj[4].toString()))
								{
									td = new ArrayList();
									StyledData dataStyle1 = new StyledData();
									dataStyle1.setData("");
									dataStyle1.setColspan(5);
									td.add(dataStyle1);
									td.add("");
									td.add("");
									td.add("");
									td.add("");
									if(obj[5]!=null)
									{
										td.add(obj[5]);
									}
									else
									{
										td.add("-");
									}
									if(obj[6]!=null)
									{
										td.add(obj[6].toString());
									}
									else
									{
										td.add("-");
									}
									if(obj[7]!=null)
									{
										td.add(obj[7]);
									}
									else
									{
										td.add("-");
									}
									tabledata.add(td);
								}
								
								
								td = new ArrayList();
								StyledData dataStyle2 = new StyledData();
								dataStyle2.setData("");
								dataStyle2.setColspan(5);
								td.add(dataStyle2);
								td.add("");
								td.add("");
								td.add("");
								td.add("");
								month=Long.parseLong(obj1[4].toString());
								if(obj1[5]!=null)
								{
									td.add(obj1[5]);
								}
								else
								{
									td.add("-");
								}
								if(obj1[6]!=null)
								{
									td.add(obj1[6].toString());
								}
								else
								{
									td.add("-");
								}
								if(obj1[7]!=null)
								{
									td.add(obj1[7]);
								}
								else
								{
									td.add("-");
								}
								tabledata.add(td);
								
							}
							dcpsId=obj[1].toString();
						}
					}
				}
			}
			
		}
		catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return tabledata;
	}
	public String getTreasuryName( String lStrLangId) 
	{
		String treasury_name = null;
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		try
		{
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where   CM.loc_Id="+lStrLangId+" and  department_Id in (100003)");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				treasury_name = lRs.getString("loc_Name");
				
			}
		}
		catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return treasury_name;
	}
	
	public List getAllTreasuries( String lStrLangId, String lStrLocId) 
	{


		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100003) and CM.LANG_ID = 1 and CM.LOC_ID not in(9991,1111) order by CM.loc_Id ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");				
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id +"-"+treasury_name);
				arrTreasury.add(vo);
			}
			ComboValuesVO vo = new ComboValuesVO();
			vo.setId("CmnPool");
			vo.setDesc("Common Pool Employees");
			arrTreasury.add(vo);
		} catch (Exception e) {

		} finally {
			try {
				if (lStmt != null) {
					lStmt.close();
				}
				if (lRs != null) {
					lRs.close();
				}
				if (lCon != null) {
					lCon.close();
				}

				lStmt = null;
				lRs = null;
				lCon = null;
			} catch (Exception e) {
				e.printStackTrace();
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	
	}

}
