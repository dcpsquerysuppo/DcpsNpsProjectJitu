package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

public class TreasuryWiseForm1DataEntryStatusReportForTreasury extends DefaultReportDataFinder{
	private static final Logger gLogger = Logger
		.getLogger(AdminDeptWiseForm1DataEntryStatusReport.class);
	
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");
	
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	
	public Collection findReportData(ReportVO report, Object criteria)
		throws ReportException {
	
	
	String langId = report.getLangId();
	
	String locId = report.getLocId();
	
	Connection con = null;
	
	criteria.getClass();
	
	Statement smt = null;
	ResultSet rs = null;
	
	
	ReportsDAO reportsDao = new ReportsDAOImpl();
	ArrayList dataList = new ArrayList();
	ArrayList td = null;
	ReportVO RptVo = null;
	
	
	try {
		requestAttributes = (Map) ((Map) criteria)
				.get(IReportConstants.REQUEST_ATTRIBUTES);
		serviceLocator = (ServiceLocator) requestAttributes
				.get("serviceLocator");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();
	
		Map requestAttributes = (Map) ((Map) criteria)
				.get(IReportConstants.REQUEST_ATTRIBUTES);
		ServiceLocator serviceLocator = (ServiceLocator) requestAttributes
				.get("serviceLocator");
		SessionFactory lObjSessionFactory = serviceLocator
				.getSessionFactorySlave();
		con = lObjSessionFactory.getCurrentSession().connection();
		smt = con.createStatement();
		
		Map sessionKeys = (Map) ((Map) criteria)
				.get(IReportConstants.SESSION_KEYS);
		Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");
	
		String lStrLocationCode = loginDetail.get("locationId").toString().trim();
	
		StyleVO[] rowsFontsVO = new StyleVO[7];
		rowsFontsVO[0] = new StyleVO();
		rowsFontsVO[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
		rowsFontsVO[0].setStyleValue("26");
		rowsFontsVO[1] = new StyleVO();
		rowsFontsVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rowsFontsVO[1].setStyleValue("14");
		rowsFontsVO[2] = new StyleVO();
		rowsFontsVO[2].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
		rowsFontsVO[2].setStyleValue("Shruti");
		rowsFontsVO[3] = new StyleVO();
		rowsFontsVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
		rowsFontsVO[3].setStyleValue("white");
		rowsFontsVO[4] = new StyleVO();
		rowsFontsVO[4].setStyleId(IReportConstants.BORDER);
		rowsFontsVO[4].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		rowsFontsVO[5] = new StyleVO();
		rowsFontsVO[5].setStyleId(28);
		rowsFontsVO[5].setStyleValue("NO");
		rowsFontsVO[6] = new StyleVO();
		rowsFontsVO[6].setStyleId(26);
		rowsFontsVO[6].setStyleValue("JavaScript:self.close()");
		
		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		
		if (report.getReportCode().equals("700069")) {
	
			report.setStyleList(noBorder);
			report.setStyleList(rowsFontsVO);
			
			StringBuffer lSBQuery = new StringBuffer();
			
	        /*
	         	
			SELECT CM.loc_id,CM.loc_name,
			SUM((
			SELECT COUNT(*) FROM mst_dcps_emp EM
			JOIN org_ddo_mst ODI ON EM.ddo_code = ODI.ddo_code
			JOIN rlt_ddo_org RDI ON ODI.ddo_code = RDI.ddo_code
			WHERE ODI.ddo_code = OD.ddo_code 
			)) AS 'Entries done by DDO Assistant',
			SUM((
			SELECT COUNT(*) FROM mst_dcps_emp EM
			JOIN org_ddo_mst ODI ON EM.ddo_code = ODI.ddo_code
			JOIN rlt_ddo_org RDI ON ODI.ddo_code = RDI.ddo_code
			WHERE ODI.ddo_code = OD.ddo_code AND EM.reg_status = 1 
			)) AS 'Approved by TO'
			FROM org_ddo_mst OD
			JOIN rlt_ddo_org RL ON OD.ddo_code = RL.ddo_code
			JOIN cmn_location_mst CM ON CM.loc_id = RL.location_code
			WHERE OD.dept_loc_code = 10007
			AND OD.ddo_name IS NOT NULL
			 GROUP BY cm.loc_id,cm.loc_name
	         
	         */
			
			/*
			
			lSBQuery.append(" SELECT CM.loc_id,CM.loc_name,");
			lSBQuery.append(" SUM((");
			lSBQuery.append(" SELECT COUNT(*) FROM mst_dcps_emp EM ");
			lSBQuery.append(" JOIN org_ddo_mst ODI ON EM.ddo_code = ODI.ddo_code ");
			lSBQuery.append(" JOIN rlt_ddo_org RDI ON ODI.ddo_code = RDI.ddo_code ");
			lSBQuery.append(" WHERE ODI.ddo_code = OD.ddo_code  ");
			lSBQuery.append(" AND RDI.LOCATION_CODE = '" + lStrLocationCode + "' ");
			lSBQuery.append(" )),");
			lSBQuery.append(" SUM((");
			lSBQuery.append(" SELECT COUNT(*) FROM mst_dcps_emp EM");
			lSBQuery.append(" JOIN org_ddo_mst ODI ON EM.ddo_code = ODI.ddo_code");
			lSBQuery.append(" JOIN rlt_ddo_org RDI ON ODI.ddo_code = RDI.ddo_code");
			lSBQuery.append(" WHERE ODI.ddo_code = OD.ddo_code");
			lSBQuery.append(" AND RDI.LOCATION_CODE = '" + lStrLocationCode + "' ");
			lSBQuery.append(" AND EM.reg_status = 1 ");
			lSBQuery.append(" )) ");
			lSBQuery.append(" FROM org_ddo_mst OD");
			lSBQuery.append(" JOIN rlt_ddo_org RL ON OD.ddo_code = RL.ddo_code");
			lSBQuery.append(" JOIN cmn_location_mst CM ON CM.loc_id = RL.location_code ");
			lSBQuery.append(" WHERE OD.dept_loc_code = '" +  lStrAdminDept.trim() + "' AND CM.loc_id = " + Long.parseLong(lStrLocationCode));
			lSBQuery.append(" GROUP BY cm.loc_id,cm.loc_name");
			
			*/
			
			String lStrAdminDept = (String) report.getParameterValue("adminDept");
			
			// New Query working on UAT DB2 version
			/*
	SELECT CM.loc_id,CM.loc_name,sum(EMP.EMP_CNT),sum(EMP.Apprv_EMP)
   FROM org_ddo_mst OD JOIN  rlt_ddo_org RL  ON OD.ddo_code = RL.ddo_code 
    JOIN  cmn_location_mst CM   ON CM.loc_id = RL.location_code  
    join (SELECT RDI.LOCATION_CODE, ODI.ddo_code, COUNT(*) EMP_CNT,Sum(case when EM.reg_status in (1,2) then 1 else 0 End) Apprv_EMP
        FROM   mst_dcps_emp EM  
        JOIN   org_ddo_mst ODI   ON EM.ddo_code = ODI.ddo_code  
        JOIN   rlt_ddo_org RDI   ON ODI.ddo_code = RDI.ddo_code 
           group by RDI.LOCATION_CODE, ODI.ddo_code) EMP 
           on EMP.ddo_code = OD.ddo_code   AND   EMP.LOCATION_CODE = '1201'
    WHERE OD.dept_loc_code = '10015' 
        AND CM.loc_id = 1201           
    GROUP BY cm.loc_id, cm.loc_name

			 */
			
			lSBQuery.append(" SELECT CM.loc_id,CM.loc_name,sum(EMP.EMP_CNT),sum(EMP.Apprv_EMP)");
			lSBQuery.append(" FROM org_ddo_mst OD JOIN  rlt_ddo_org RL  ON OD.ddo_code = RL.ddo_code");
			lSBQuery.append(" JOIN  cmn_location_mst CM   ON CM.loc_id = RL.location_code");
			lSBQuery.append(" join (SELECT RDI.LOCATION_CODE, ODI.ddo_code, COUNT(*) EMP_CNT,Sum(case when EM.reg_status in (1,2) then 1 else 0 End) Apprv_EMP");
			lSBQuery.append(" FROM   mst_dcps_emp EM");
			lSBQuery.append(" JOIN   org_ddo_mst ODI   ON EM.ddo_code = ODI.ddo_code");
			lSBQuery.append(" JOIN   rlt_ddo_org RDI   ON ODI.ddo_code = RDI.ddo_code");
			lSBQuery.append(" group by RDI.LOCATION_CODE, ODI.ddo_code) EMP");
			lSBQuery.append(" on EMP.ddo_code = OD.ddo_code AND EMP.LOCATION_CODE = " + Long.parseLong(lStrLocationCode));
			lSBQuery.append(" WHERE OD.dept_loc_code = '" +  lStrAdminDept.trim() + "'");
			lSBQuery.append(" AND CM.loc_id = " + Long.parseLong(lStrLocationCode));
			lSBQuery.append(" GROUP BY cm.loc_id, cm.loc_name");
			
			Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
			
			List lLstFinal = Query.list();
			Long lLongSRNo = 1l;
			
			String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=700070&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
			
			if (lLstFinal != null && !lLstFinal.isEmpty())
			{
				Iterator it = lLstFinal.iterator();
				
				while (it.hasNext())
				{
					Object[] tuple = (Object[]) it.next();
					td = new ArrayList();
					
					td.add(lLongSRNo);  // SR No
					
					if(tuple[1] != null) // Treasury name
					{
						td.add(new URLData(tuple[1].toString(),urlPrefix + "&adminDept="+ lStrAdminDept + "&treasury=" +tuple[0].toString() ));
					}
					else
					{
						td.add("");
					}
					
					if(tuple[2] != null) // Entry by DDO Assistant
					{
						td.add(tuple[2].toString());
					}
					else
					{
						td.add("");
					}
					
					if(tuple[3] != null) // Approved by TO
					{
						td.add(tuple[3].toString());
					}
					else
					{
						td.add("");
					}
					
					lLongSRNo ++;
					dataList.add(td);
				}
			}
			
		}
	
	} catch (Exception e) {
		
		e.printStackTrace();
		gLogger.error("Exception :" + e, e);
		
	} finally {
		try {
			if (smt != null) {
				smt.close();
			}
	
			if (rs != null) {
				rs.close();
			}
	
			if (con != null) {
				con.close();
			}
	
			smt = null;
			rs = null;
			con = null;
	
		} catch (Exception e1) {
			e1.printStackTrace();
			gLogger.error("Exception :" + e1, e1);
	
		}
	}
	
		return dataList;
	}

}
