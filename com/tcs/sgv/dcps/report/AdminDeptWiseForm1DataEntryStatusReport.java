package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

public class AdminDeptWiseForm1DataEntryStatusReport extends DefaultReportDataFinder {
	private static final Logger gLogger = Logger.getLogger(AdminDeptWiseForm1DataEntryStatusReport.class);

	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		report.getLangId();

		report.getLocId();

		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;

		new ReportsDAOImpl();
		ArrayList dataList = new ArrayList();
		ArrayList td = null;
		try {
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();

			Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();

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
			rowsFontsVO[6].setStyleValue("ifms.htm?actionFlag=validateLogin");

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] leftAlignVO = new StyleVO[2];
			leftAlignVO[0] = new StyleVO();
			leftAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			leftAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
			leftAlignVO[1] = new StyleVO();
			leftAlignVO[1].setStyleId(IReportConstants.BORDER);
			leftAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			if (report.getReportCode().equals("700064")) {

				report.setStyleList(noBorder);
				report.setStyleList(rowsFontsVO);

				StringBuffer lSBQuery = new StringBuffer();

				lSBQuery.append(" SELECT DISTINCT CM.loc_id,CM.loc_name,");
				lSBQuery.append(" (");
				lSBQuery.append(" SELECT COUNT(*) FROM mst_dcps_emp EM ");
				lSBQuery.append(" JOIN org_ddo_mst ODI ON EM.ddo_code = ODI.ddo_code ");
				lSBQuery.append(" JOIN cmn_location_mst CMI ON ODI.dept_loc_code = CMI.loc_id ");
				lSBQuery.append(" WHERE CMI.loc_id = CM.loc_id ");
				lSBQuery.append(" ),");
				lSBQuery.append(" (");
				lSBQuery.append(" SELECT COUNT(*) FROM mst_dcps_emp EM");
				lSBQuery.append(" JOIN org_ddo_mst ODI ON EM.ddo_code = ODI.ddo_code");
				lSBQuery.append(" JOIN cmn_location_mst CMI ON ODI.dept_loc_code = CMI.loc_id");
				lSBQuery.append(" WHERE CMI.loc_id = CM.loc_id AND");
				lSBQuery.append(" EM.reg_status IN (1,2) ");
				lSBQuery.append(" )");
				lSBQuery.append(" FROM cmn_location_mst CM");
				lSBQuery.append(" JOIN org_ddo_mst OD ON OD.dept_loc_code = CM.loc_id ");
				lSBQuery.append(" WHERE CM.department_id = 100001");

				Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());

				List lLstFinal = Query.list();
				Long lLongSRNo = 1l;

				String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=700065&action=generateReport&DirectReport=TRUE&displayOK=TRUE";

				if (lLstFinal != null && !lLstFinal.isEmpty()) {
					Iterator it = lLstFinal.iterator();

					while (it.hasNext()) {
						Object[] tuple = (Object[]) it.next();
						td = new ArrayList();

						td.add(lLongSRNo); // SR No

						if (tuple[1] != null) // Department name
						{
							td.add(new URLData(tuple[1].toString(), urlPrefix + "&adminDept=" + tuple[0].toString(), leftAlignVO));
						} else {
							td.add("");
						}

						if (tuple[2] != null) // Entry by DDO Assistant
						{
							td.add(tuple[2].toString());
						} else {
							td.add("");
						}

						if (tuple[3] != null) // Approved by TO
						{
							td.add(tuple[3].toString());
						} else {
							td.add("");
						}

						lLongSRNo++;
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
