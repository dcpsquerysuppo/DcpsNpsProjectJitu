package com.tcs.sgv.dcps.report;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportParameterVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.reports.dao.ReportParameterDAO;

public class ApprovedMissingCreditDtls  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(ApprovedMissingCreditDtls.class);
	ServiceLocator serviceLocator = null;
	Map requestAttributes = null;
	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException 
	{
		ArrayList tabledata = new ArrayList();
		String langId = report.getLangId();
		String locId = report.getLocId();
		String yearId="";
		String monthId="";
		
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
		
			
			
			if (report.getReportCode().equals("80000881")) 
			{
				
				if(report.getParameterValue("yearid")!=null && !"".equalsIgnoreCase(report.getParameterValue("yearid").toString()))
				{
					yearId = ((String) report
							.getParameterValue("yearid")).trim();
				
				}				
				if(report.getParameterValue("monthid")!=null && !"".equalsIgnoreCase(report.getParameterValue("monthid").toString()))
				{
					monthId = ((String) report
							.getParameterValue("monthid"));
				

				}
			
				List TableDataSub = null;
				StringBuilder SBQuerySub = new StringBuilder();
				
				SBQuerySub
				.append("SELECT loc.LOC_NAME,emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,l.LOOKUP_NAME,trn.CONTRIBUTION,trn.CONTRIBUTION,mon.month_name,");
			
				SBQuerySub
				.append(" case when trn.MONTH_ID > 3 then cast(f.fin_year_code as Integer)  else cast(f.fin_year_code as Integer) +1 end, to_char(trn.SRKA_APPROVAL_DATE,'dd-MM-yyyy hh:mm:ss') ");
			
				SBQuerySub
				.append(" FROM TRN_DCPS_CONTRIBUTION trn inner join mst_dcps_emp emp on emp.DCPS_EMP_ID=trn.DCPS_EMP_ID");
				SBQuerySub
				.append(" inner join CMN_LOOKUP_MST l on l.LOOKUP_ID=trn.TYPE_OF_PAYMENT inner join CMN_LOCATION_MST loc on substr(loc.LOCATION_CODE,1,2)=substr(trn.TREASURY_CODE,1,2) and loc.DEPARTMENT_ID=100003 ");
				SBQuerySub
				.append(" inner join SGVA_MONTH_MST mon on mon.month_id=trn.MONTH_ID and mon.lang_id ='en_US'  inner join SGVC_FIN_YEAR_MST f on f.fin_year_id=trn.FIN_YEAR_ID  where trn.APPROVED_BY_SRKA='Y' ");
				
				if(yearId!=null && !"".equalsIgnoreCase(yearId))
				{

					SBQuerySub
					.append(" and trn.FIN_YEAR_ID="); 
					SBQuerySub
					.append(yearId); 
				
							
				}
				if(monthId!=null && !"".equalsIgnoreCase(monthId))
				{
					SBQuerySub
					.append(" and trn.MONTH_ID="); 
					SBQuerySub
					.append(monthId); 
					
							
				}
				if(report.getParameterValue("UserType")!=null && !"".equalsIgnoreCase(report.getParameterValue("UserType").toString()) && "TO".equalsIgnoreCase(report.getParameterValue("UserType").toString()) )
				{
					String locCode=locationId.toString().substring(0, 2);
					SBQuerySub
					.append(" and substr(emp.ddo_code,1,2)='"); 
					SBQuerySub
					.append(locCode); 
					SBQuerySub
					.append("'");
				}
			

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
						if (tupleSub[9] != null) 
						{
								td.add(tupleSub[9]);
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
	public List getMonth(String lStrLangId, String lStrLocId) {
		ArrayList<ComboValuesVO> arrMonth = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		
		String month_id = null;
		String month_name = null;
		try {

			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					"select * from SGVA_MONTH_MST where lang_id ='en_US'  order by month_id ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				month_id = lRs.getString("month_id");
				month_name = lRs.getString("MONTH_NAME");
				vo.setId(month_id);
				vo.setDesc(month_name);
				arrMonth.add(vo);
			}

		} catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
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
		return arrMonth;
	}
	
	
		
		
	

}
