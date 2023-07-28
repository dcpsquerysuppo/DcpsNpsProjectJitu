/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	March 21, 2011		Meeta Thacker								
 *******************************************************************************
 */
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
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 March 21, 2011
 */

public class PendencyForm1ReportForDDO extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger
			.getLogger(PendencyForm1Report.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;

	public Collection findReportData(ReportVO report, Object criteria)
			throws ReportException {

		gLogger.info("inside reports function");
		gLogger
				.info("***********************Outward Register****************************************");

		String langId = report.getLangId();
		gLogger.info("\n\n LangId is " + langId);

		String locId = report.getLocId();
		gLogger.info("\n\n LocationId is " + locId);
		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		Statement smt1 = null;
		Statement smt2 = null;
		Statement smt3 = null;
		Statement smt4 = null;
		Statement smt5 = null;
		Statement smt6 = null;
		Statement smt7 = null;
		Statement smt8 = null;

		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		ResultSet rs7 = null;
		ResultSet rs8 = null;

		new ReportsDAOImpl();
		ArrayList dataList = new ArrayList();
		ArrayList td = null;
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
			con.setAutoCommit(false);
			smt = con.createStatement();
			smt1 = con.createStatement();
			smt2 = con.createStatement();
			smt3 = con.createStatement();
			smt4 = con.createStatement();
			smt5 = con.createStatement();
			smt6 = con.createStatement();
			smt7 = con.createStatement();
			smt8 = con.createStatement();

			gLogger
					.info("***********************11****************************************");
			Map sessionKeys = (Map) ((Map) criteria)
					.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			gLogger.info("Logindetails contents of map :" + loginDetail);
			gLogger
					.info("***********************loginDetail****************************************"
							+ loginDetail);

			LoginDetails objLoginDetails = (LoginDetails) sessionKeys
					.get("loginDetails");

			Long locationId = (Long) loginDetail.get("locationId");

			gLogger.info("Location id from Map :" + locationId);

			new StringBuffer();
			gLogger
					.info("***********************22****************************************");

			StyleVO[] rowsFontsVO = new StyleVO[5];
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
			rowsFontsVO[4].setStyleId(IReportConstants.REPORT_PAGINATION);
			rowsFontsVO[4].setStyleValue("NO");

			String StrSqlQuery = "";
			if (report.getReportCode().equals("700043")) {

				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				DateFormat sqlDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				Date date = new Date();
				String curDate = dateFormat.format(date);
				String dateBeforeAWeek = sqlDateFormat.format(new Date(date
						.getTime() - 604800000L));
				String dateBeforeAWeekAndADay = sqlDateFormat.format(new Date(
						date.getTime() - 691200000L));
				String dateBeforeTwoWeeks = sqlDateFormat.format(new Date(date
						.getTime() - 1209600000L));

				report.setStyleList(rowsFontsVO);

				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
						serviceLocator.getSessionFactory());

				Long lLngPostId = objLoginDetails.getLoggedInPost().getPostId();
				String lStrDdoCode = lObjDcpsCommonDAO
						.getDdoCodeForDDO(lLngPostId);

				new OfflineContriDAOImpl(null, serviceLocator
						.getSessionFactory());
				Long treasuryId = Long.parseLong(lObjDcpsCommonDAO
						.getTreasuryCodeForDDO(lStrDdoCode));

				String treasuryName = lObjDcpsCommonDAO
						.getTreasuryNameForTreasuryId(treasuryId);

				String header1 = "<center><b>" + "<font size=\"2px\"> "
						+ "GOVT. of Maharashtra" + "</font></b></center>";
				String header2 = "<b>" + "<font size=\"1px\"> " + curDate
						+ "</font></b>";
				String header3 = "<left><b><div>" + "<font size=\"1px\"> "
						+ "Treasury : " + treasuryName + "(" + treasuryId + ")"
						+ "</font></div></b></left>";
				String header4 = "<b><div>"
						+ "<h2 style=\"float: left; width: 34%; text-align: left;\">"
						+ "<font size=\"2px\">"
						+ "(Treasury,DDO-wise)"
						+ "</font></h2>"
						+ "<p style=\"float: left; width: 33%; text-align: center;\">"
						+ "<font size=\"2px\">"
						+ "Pendency of Form1 entries as on " + curDate
						+ "</font></p>" + "</div></b>";

				String additionalHeader = header1 + header2 + header4 + header3;
				report.setAdditionalHeader(additionalHeader);

				StringBuilder SBQuery = new StringBuilder();
				SBQuery
						.append("SELECT COUNT(dcps_emp.dcps_emp_id) AS COUNT FROM org_ddo_mst ddo_mst,  mst_dcps_emp dcps_emp WHERE  dcps_emp.ddo_code = ddo_mst.ddo_code AND ddo_mst.ddo_code = '"
								+ lStrDdoCode + "'");
				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);

				SBQuery = new StringBuilder();
				SBQuery
						.append("SELECT COUNT(ddo_asst_count.dcps_emp_id) AS COUNT FROM org_ddo_mst ddo_mst, mst_dcps_emp ddo_asst_count WHERE ddo_asst_count.ddo_code= ddo_mst.ddo_code AND ddo_asst_count.reg_status NOT IN (1,2) AND  ddo_asst_count.ddo_code = '"
								+ lStrDdoCode + "'");
				StrSqlQuery = SBQuery.toString();
				rs1 = smt1.executeQuery(StrSqlQuery);

				SBQuery = new StringBuilder();
				SBQuery
						.append("SELECT COUNT(dcps_emp.dcps_emp_id) AS COUNT FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE dcps_emp.ddo_code = ddo_mst.ddo_code AND dcps_emp.form_status = 1 AND dcps_emp.reg_status IN (0,1) AND dcps_emp.phy_rcvd_status IN (0,1) AND dcps_emp.ddo_code = '"
								+ lStrDdoCode + "'");
				StrSqlQuery = SBQuery.toString();
				rs2 = smt2.executeQuery(StrSqlQuery);

				SBQuery = new StringBuilder();
				SBQuery
						.append("SELECT COUNT(dcps_emp.dcps_emp_id) AS COUNT FROM org_ddo_mst ddo_mst,mst_dcps_emp dcps_emp	WHERE dcps_emp.ddo_code= ddo_mst.ddo_code  AND	dcps_emp.reg_status = -1 AND dcps_emp.APPROVAL_BY_DDO_DATE IS NOT NULL AND dcps_emp.ddo_code = '"
								+ lStrDdoCode + "'");
				StrSqlQuery = SBQuery.toString();
				rs3 = smt3.executeQuery(StrSqlQuery);

				SBQuery = new StringBuilder();
				SBQuery
						.append("SELECT COUNT(dcps_emp.dcps_emp_id) AS COUNT FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status = 0 AND dcps_emp.phy_rcvd_status = 1 AND dcps_emp.ddo_code = '"
								+ lStrDdoCode + "'");
				StrSqlQuery = SBQuery.toString();
				rs4 = smt4.executeQuery(StrSqlQuery);

				SBQuery = new StringBuilder();
				SBQuery
						.append("SELECT COUNT(dcps_emp.dcps_emp_id) AS COUNT FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status = 1 AND dcps_emp.phy_rcvd_status = 1 AND dcps_emp.ddo_code = '"
								+ lStrDdoCode + "'");
				StrSqlQuery = SBQuery.toString();
				rs5 = smt5.executeQuery(StrSqlQuery);

				SBQuery = new StringBuilder();
				SBQuery
						.append("SELECT COUNT(dcps_emp.dcps_emp_id) AS COUNT FROM org_ddo_mst ddo_mst,  mst_dcps_emp dcps_emp WHERE dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status = 0 AND dcps_emp.phy_rcvd_status = 1 AND dcps_emp.PHY_RCVD_DATE > '"
								+ dateBeforeAWeek
								+ "' AND dcps_emp.ddo_code = '"
								+ lStrDdoCode
								+ "'");
				StrSqlQuery = SBQuery.toString();
				rs6 = smt6.executeQuery(StrSqlQuery);

				SBQuery = new StringBuilder();
				SBQuery
						.append("SELECT COUNT(dcps_emp.dcps_emp_id) AS COUNT FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status = 0 AND dcps_emp.phy_rcvd_status = 1 AND dcps_emp.PHY_RCVD_DATE BETWEEN '"
								+ dateBeforeTwoWeeks
								+ "' AND '"
								+ dateBeforeAWeekAndADay
								+ "' AND dcps_emp.ddo_code = '"
								+ lStrDdoCode
								+ "'");
				StrSqlQuery = SBQuery.toString();
				rs7 = smt7.executeQuery(StrSqlQuery);

				SBQuery = new StringBuilder();
				SBQuery
						.append("SELECT COUNT(dcps_emp.dcps_emp_id) AS COUNT FROM org_ddo_mst ddo_mst,  mst_dcps_emp dcps_emp WHERE dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status = 0 AND dcps_emp.phy_rcvd_status = 1 AND dcps_emp.PHY_RCVD_DATE < '"
								+ dateBeforeTwoWeeks
								+ "' AND dcps_emp.ddo_code = '"
								+ lStrDdoCode
								+ "'");
				StrSqlQuery = SBQuery.toString();
				rs8 = smt8.executeQuery(StrSqlQuery);
				int TotalBal = 0;
				int entryByDDOAsst = 0;
				int rejectedByTO = 0;
				int totalWithDDO = 0;
				int lessThan1Week = 0;
				int btwn1And2Week = 0;
				int greaterThan2Week = 0;
				int totalWithTO = 0;

				td = new ArrayList();
				td.add(1);

				String lStrDdoName = lObjDcpsCommonDAO
						.getDdoNameForCode(lStrDdoCode);
				td.add(lStrDdoCode + "-" + lStrDdoName);

				if (rs.next()) {
					TotalBal = Integer.parseInt(rs.getString("count"));
					//System.out.println(TotalBal);
				} else {
					TotalBal = 0;
				}
				if (rs1.next()) {
					entryByDDOAsst = Integer.parseInt(rs1.getString("count"));
				} else {
					entryByDDOAsst = 0;
				}

				if (rs2.next()) {
					td.add(rs2.getString("count"));
				} else {
					td.add("0");
				}
				if (rs3.next()) {
					rejectedByTO = Integer.parseInt(rs3.getString("count"));
					td.add(rs3.getString("count"));
				} else {
					rejectedByTO = 0;
					td.add("0");
				}
				totalWithDDO = rejectedByTO + entryByDDOAsst;
				td.add(totalWithDDO);
				if (rs4.next()) {
					td.add(rs4.getString("count"));
				} else {
					td.add("0");
				}
				if (rs5.next()) {
					td.add(rs5.getString("count"));
				} else {
					td.add("0");
				}
				if (rs6.next()) {
					lessThan1Week = Integer.parseInt(rs6.getString("count"));
				} else {
					lessThan1Week = 0;

				}
				if (rs7.next()) {
					btwn1And2Week = Integer.parseInt(rs7.getString("count"));
				} else {
					btwn1And2Week = 0;
				}
				if (rs8.next()) {
					greaterThan2Week = Integer.parseInt(rs8.getString("count"));
				} else {
					greaterThan2Week = 0;
				}
				totalWithTO = lessThan1Week + btwn1And2Week + greaterThan2Week;
				td.add(totalWithTO);
				td.add(totalWithTO + totalWithDDO);

				td.add(TotalBal - entryByDDOAsst);
				td.add(entryByDDOAsst);
				td.add(TotalBal);

				td.add(lessThan1Week);
				td.add(btwn1And2Week);
				td.add(greaterThan2Week);
				dataList.add(td);
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

		gLogger.info("DATALIST SIZE IS" + dataList.size());

		return dataList;

	}

}
