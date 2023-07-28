/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 18, 2011		Vihan Khatri								
 *******************************************************************************
 */
/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 18, 2011
 */

package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;


public class ConsolidateDcpsEmployeeReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(ConsolidateDcpsEmployeeReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;

	SessionFactory lObjSessionFactory = null;

	ServiceLocator serviceLocator = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		// setSessionInfo((Map) criteria);

		report.getLocId();
		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		ArrayList dataList = new ArrayList();

		// for Center Alignment format
		StyleVO[] CenterAlignVO = new StyleVO[2];
		CenterAlignVO[0] = new StyleVO();
		CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		CenterAlignVO[1] = new StyleVO();
		CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
		CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		try {

			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();

			Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);

			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");

			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			loginDetail.get("locationId");

			String StrSqlQuery = "";
	

			if (report.getReportCode().equals("8000152")) {

				// report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				Long treasuryCode = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				
				String monthName =report.getParameterValue("monthName").toString();
				Long acMaintdBy = Long.valueOf((String)  report.getParameterValue("acmaintBy"));
				Long yearId = Long.valueOf((String) report.getParameterValue("yearId"));
				
				gLogger.info("TreasuryCode is*****"+treasuryCode);
				gLogger.info("monthName is"+monthName);
				gLogger.info("acMaintdBy is"+acMaintdBy);
				gLogger.info("yearId is"+yearId);

				new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());

				StringBuilder SBQuery = new StringBuilder();

			

				
				SBQuery.append(" SELECT emp.EMP_NAME,emp.DCPS_ID,trn.ddo_code,look.lookup_name,cast(SUM(trn.CONTRIBUTION) as DECIMAL(20,0)) as contribution,cast(SUM(trn.CONTRIBUTION) as DECIMAL(20,0)) as contribaution,trn.voucher_no,to_char(trn.voucher_date,'dd-MM-yyyy'),trn.scheme_code,mon.MONTH_NAME,(case when trn.MONTH_ID>3 then cast(fin.FIN_YEAR_CODE as bigint) else cast ");
				SBQuery.append(" (fin.FIN_YEAR_CODE as bigint)+1 end) FROM  mst_dcps_emp emp   ");
				SBQuery.append(" inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
				SBQuery.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS dtls on dtls.MST_DCPS_CONTRI_VOUCHER_DTLS=trn.RLT_CONTRI_VOUCHER_ID  ");
				SBQuery.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=trn.TREASURY_CODE   ");
				SBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=trn.type_of_payment   ");
				SBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID  ");
				SBQuery.append(" inner join SGVA_MONTH_MST mon on mon.MONTH_ID=trn.MONTH_ID  ");
				SBQuery.append(" where trn.REG_STATUS=1 and emp.REG_STATUS=1 and emp.DCPS_ID is not null ");
				SBQuery.append(" and loc.DEPARTMENT_ID in (100003,100006) and trn.TREASURY_CODE ="+treasuryCode+" and mon.month_name='"+monthName+"' and emp.AC_DCPS_MAINTAINED_BY="+acMaintdBy+" and trn.FIN_YEAR_ID="+yearId+"   ");
				SBQuery.append(" group by emp.EMP_NAME,emp.DCPS_ID,trn.voucher_no,trn.voucher_date,trn.scheme_code,mon.MONTH_NAME ,trn.MONTH_ID,fin.FIN_YEAR_CODE,trn.DDO_CODE,look.lookup_name ");
				SBQuery.append(" order by trn.ddo_code ");
			

				
				
				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);
				Integer counter = 1;

				while (rs.next()) {

					rowList = new ArrayList();

					rowList.add(new StyledData(counter, CenterAlignVO));
					rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(8), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(9), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(10), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(11), CenterAlignVO));
					
					dataList.add(rowList);

					counter = counter + 1;
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

			} catch (Exception e) {
				e.printStackTrace();
				gLogger.error("Exception :" + e, e);
				e.printStackTrace();
			}
		}
		return dataList;
	}

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

}
