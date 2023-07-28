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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
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

public class PendencyForm1Report extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(PendencyForm1Report.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		gLogger.info("inside reports function");
		gLogger.info("***********************Outward Register****************************************");

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
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();

			Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
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

			gLogger.info("***********************11****************************************");
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			gLogger.info("Logindetails contents of map :" + loginDetail);
			gLogger.info("***********************loginDetail****************************************" + loginDetail);
			// LoginDetails objLoginDetails =
			// (LoginDetails)loginDetail.get("baseLoginVO");

			Long locationId = (Long) loginDetail.get("locationId");
			gLogger.info("Location id from Map :" + locationId);

			new StringBuffer();
			gLogger.info("***********************22****************************************");

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
			if (report.getReportCode().equals("700013")) {

				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				DateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				Date date = new Date();
				String curDate = dateFormat.format(date);
				String dateBeforeAWeek = sqlDateFormat.format(new Date(date.getTime() - 604800000L));
				String dateBeforeAWeekAndADay = sqlDateFormat.format(new Date(date.getTime() - 691200000L));
				String dateBeforeTwoWeeks = sqlDateFormat.format(new Date(date.getTime() - 1209600000L));

				report.setStyleList(rowsFontsVO);

				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());
				OfflineContriDAO objOfflineContriDAO = new OfflineContriDAOImpl(null, serviceLocator.getSessionFactory());
				Long treasuryId = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				//System.out.println(treasuryId);
				String treasuryName = lObjDcpsCommonDAO.getTreasuryNameForTreasuryId(treasuryId);

				String header1 = "<center><b>" + "<font size=\"2px\"> " + "GOVT. of Maharashtra" + "</font></b></center>";
				String header2 = "<b>" + "<font size=\"1px\"> " + curDate + "</font></b>";
				String header3 = "<left><b><div>" + "<font size=\"1px\"> " + "Treasury : " + treasuryName + "(" + treasuryId + ")" + "</font></div></b></left>";
				String header4 = "<b><div>" + "<h2 style=\"float: left; width: 34%; text-align: left;\">" + "<font size=\"2px\">" + "(Treasury,DDO-wise)" + "</font></h2>"
						+ "<p style=\"float: left; width: 33%; text-align: center;\">" + "<font size=\"2px\">" + "Pendency of Form1 entries as on " + curDate + "</font></p>"
						+ "</div></b>";

				String additionalHeader = header1 + header2 + header4 + header3;
				report.setAdditionalHeader(additionalHeader);

				List ddoList = objOfflineContriDAO.getAllDDO(treasuryId.toString());
				ComboValuesVO objDDO = null;
				String lStrDdoCode = null;
				for (int i = 1; i < ddoList.size(); i++) {
					objDDO = (ComboValuesVO) ddoList.get(i);
					lStrDdoCode = objDDO.getId();
					StringBuilder SBQuery = new StringBuilder();
					SBQuery.append("SELECT COUNT(dcps_emp.dcps_emp_id) AS count FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE ddo_mst.ddo_code='" + lStrDdoCode
							+ "' AND dcps_emp.ddo_code = ddo_mst.ddo_code GROUP BY dcps_emp.ddo_code ");
					StrSqlQuery = SBQuery.toString();
					rs = smt.executeQuery(StrSqlQuery);

					SBQuery = new StringBuilder();
					SBQuery.append("SELECT COUNT(ddo_asst_count.dcps_emp_id) as count FROM org_ddo_mst ddo_mst,mst_dcps_emp ddo_asst_count WHERE ddo_mst.ddo_code='" + lStrDdoCode
							+ "' AND ddo_asst_count.ddo_code= ddo_mst.ddo_code AND ddo_asst_count.reg_status NOT IN (1,2) GROUP BY ddo_asst_count.ddo_code");
					StrSqlQuery = SBQuery.toString();
					rs1 = smt1.executeQuery(StrSqlQuery);

					SBQuery = new StringBuilder();
					SBQuery
							.append("SELECT COUNT(dcps_emp.dcps_emp_id) as count FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE ddo_mst.ddo_code='"
									+ lStrDdoCode
									+ "' AND dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status IN (0,1) AND	dcps_emp.phy_rcvd_status IN (0,1) GROUP BY dcps_emp.ddo_code");
					StrSqlQuery = SBQuery.toString();
					rs2 = smt2.executeQuery(StrSqlQuery);

					SBQuery = new StringBuilder();
					SBQuery.append("SELECT COUNT(dcps_emp.dcps_emp_id) as count FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE ddo_mst.ddo_code='" + lStrDdoCode
							+ "' AND dcps_emp.ddo_code= ddo_mst.ddo_code  AND	dcps_emp.reg_status = -1 AND dcps_emp.APPROVAL_BY_DDO_DATE IS NOT NULL GROUP BY dcps_emp.ddo_code");
					StrSqlQuery = SBQuery.toString();
					rs3 = smt3.executeQuery(StrSqlQuery);

					SBQuery = new StringBuilder();
					SBQuery
							.append("SELECT COUNT(dcps_emp.dcps_emp_id) as count FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE ddo_mst.ddo_code='"
									+ lStrDdoCode
									+ "' AND dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND	dcps_emp.reg_status = 0 AND	dcps_emp.phy_rcvd_status = 1 GROUP BY dcps_emp.ddo_code");
					StrSqlQuery = SBQuery.toString();
					rs4 = smt4.executeQuery(StrSqlQuery);

					SBQuery = new StringBuilder();
					SBQuery
							.append("SELECT COUNT(dcps_emp.dcps_emp_id) as count FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE ddo_mst.ddo_code='"
									+ lStrDdoCode
									+ "' AND dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status = 1 AND dcps_emp.phy_rcvd_status = 1 GROUP BY dcps_emp.ddo_code");
					StrSqlQuery = SBQuery.toString();
					rs5 = smt5.executeQuery(StrSqlQuery);

					SBQuery = new StringBuilder();
					SBQuery
							.append("SELECT COUNT(dcps_emp.dcps_emp_id) as count FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE ddo_mst.ddo_code='"
									+ lStrDdoCode
									+ "' AND dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status = 0 AND dcps_emp.phy_rcvd_status = 1 AND dcps_emp.PHY_RCVD_DATE > '"
									+ dateBeforeAWeek + "' GROUP BY dcps_emp.ddo_code");
					StrSqlQuery = SBQuery.toString();
					rs6 = smt6.executeQuery(StrSqlQuery);

					SBQuery = new StringBuilder();
					SBQuery
							.append("SELECT COUNT(dcps_emp.dcps_emp_id) as count FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE ddo_mst.ddo_code='"
									+ lStrDdoCode
									+ "' AND dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status = 0 AND dcps_emp.phy_rcvd_status = 1 AND dcps_emp.PHY_RCVD_DATE BETWEEN '"
									+ dateBeforeTwoWeeks + "' AND '" + dateBeforeAWeekAndADay + "' GROUP BY dcps_emp.ddo_code");
					StrSqlQuery = SBQuery.toString();
					rs7 = smt7.executeQuery(StrSqlQuery);

					SBQuery = new StringBuilder();
					SBQuery
							.append("SELECT COUNT(dcps_emp.dcps_emp_id) as count FROM org_ddo_mst ddo_mst, mst_dcps_emp dcps_emp WHERE ddo_mst.ddo_code='"
									+ lStrDdoCode
									+ "' AND dcps_emp.ddo_code= ddo_mst.ddo_code  AND dcps_emp.form_status = 1 AND dcps_emp.reg_status = 0 AND dcps_emp.phy_rcvd_status = 1 AND dcps_emp.PHY_RCVD_DATE < '"
									+ dateBeforeTwoWeeks + "' GROUP BY dcps_emp.ddo_code");
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
					td.add(i);
					td.add(lStrDdoCode + "-" + objDDO.getDesc());

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

			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Exception :" + e, e);
		} finally {
			try {
				if (smt != null) {
					smt.close();
				}
				if (smt1 != null) {
					smt1.close();
				}
				
				if (smt2 != null) {
					smt2.close();
				}
				
				if (smt3 != null) {
					smt3.close();
				}
			
				if (smt4 != null) {
					smt4.close();
				}
				
				if (smt5 != null) {
					smt5.close();
				}
				
				if (smt6 != null) {
					smt6.close();
				}
				
				if (smt7 != null) {
					smt7.close();
				}
				
				if (smt8 != null) {
					smt8.close();
				}
				
				

				if (rs != null) {
					rs.close();
				}
				if (rs1 != null) {
					rs1.close();
				}
				if (rs2 != null) {
					rs2.close();
				}
				if (rs3 != null) {
					rs3.close();
				}
				if (rs4 != null) {
					rs4.close();
				}
				if (rs5 != null) {
					rs5.close();
				}
				if (rs6 != null) {
					rs6.close();
				}
				if (rs7 != null) {
					rs7.close();
				}
				if (rs8 != null) {
					rs8.close();
				}

				if (con != null) {
					con.close();
				}

				smt = null;
				smt1 = null;
				smt2 = null;
				smt3 = null;
				smt4 = null;
				smt5 = null;
				smt6 = null;
				smt7 = null;
				smt8 = null;
				rs = null;
				rs1 = null;
				rs2 = null;
				rs3 = null;
				rs4 = null;
				rs5 = null;
				rs6 = null;
				rs7 = null;
				rs8 = null;
				con = null;

			} catch (Exception e1) {
				e1.printStackTrace();
				gLogger.error("Exception :" + e1, e1);

			}
		}

		gLogger.info("DATALIST SIZE IS" + dataList.size());

		return dataList;

	}

	public List getAllTreasuries(String lStrLangId, String lStrLocId) {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {
			gLogger.info("In ReportQueryDAOImpl lStrLangId : " + lStrLangId);
			gLogger.info("In ReportQueryDAOImpl lStrLocId : " + lStrLocId);
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer("select loc_Id , loc_Name from Cmn_Location_Mst CM where department_Id = 100003");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_name);
				arrTreasury.add(vo);
			}

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
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	}
}
