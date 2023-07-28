package com.tcs.sgv.gpf.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;

public class GPFBroadsheet extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger("GPFReports");
	public static String newline = System.getProperty("line.separator");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Map lMapSeriesHeadCode = null;
	Session ghibSession = null;

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {
		Connection con = null;
		Statement smt = null;
		ResultSet rs = null;
		Statement smt1 = null;
		ResultSet rs1 = null;
		Statement smt2 = null;
		ResultSet rs2 = null;
		List lLstDataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		TabularData rptTd = null;
		ReportVO RptVo = null;
		ReportsDAO reportsDao = new ReportsDAOImpl();
		ArrayList tr = null;
		ArrayList td = null;
		ArrayList rptList1 = null;
		try {
			lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
			serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");
			gObjSessionFactory = serviceLocator.getSessionFactorySlave();
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			ghibSession = gObjSessionFactory.getCurrentSession();

			Map lServiceMap = (Map) lMapRequestAttributes.get("serviceMap");
			Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
			Long lLngPostId = (Long) lBaseLoginMap.get("loggedInPost");
			con = gObjSessionFactory.getCurrentSession().connection();

			smt = con.createStatement();
			smt1 = con.createStatement();
			smt2 = con.createStatement();
			String StrSqlQuery = "";
			String StrSqlQueryForOffice = "";
			String StrSqlQuery2 = "";

			StyleVO[] noBorder = new StyleVO[3];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			noBorder[1] = new StyleVO();
			noBorder[1].setStyleId(IReportConstants.REPORT_PAGINATION);
			noBorder[1].setStyleValue("NO");
			noBorder[2] = new StyleVO();
			noBorder[2].setStyleId(IReportConstants.LANDSCAPE);
			noBorder[2].setStyleValue("YES");

			StyleVO[] RightAlignVO = new StyleVO[2];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] boldVO = new StyleVO[2];
			boldVO[0] = new StyleVO();
			boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldVO[1] = new StyleVO();
			boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldVO[1].setStyleValue("10");

			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			if (lObjReport.getReportCode().equals("800005")) {

				Long lLngFinYear = Long.parseLong(lObjReport.getParameterValue("financialYear").toString());
				String lStrDdoCode = "";
				String lStrOfficeName = "";
				ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serviceLocator
						.getSessionFactory());
				List lLstDDOInfo = lObjScheduleGenerationDAO.getDdoInfoForPost(lLngPostId);
				if (lLstDDOInfo != null && lLstDDOInfo.size() > 0) {
					Object[] objArr = (Object[]) lLstDDOInfo.get(0);
					lStrDdoCode = objArr[0].toString();
					lStrOfficeName = objArr[1].toString();
				}
				String header1 = "<center>" + "<b><font size=\"1\"> " + "Broadsheet for the Year "
						+ getFinYearDesc(lLngFinYear) + "</font></b></center>";

				String additionalHeader = header1 + newline;
				lObjReport.setAdditionalHeader(additionalHeader);
				lObjReport.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();
				rowList.add("Name of the Office :  " + lStrOfficeName);
				lLstDataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				lLstDataList.add(rowList);

				StringBuilder SBQueryForOffice = new StringBuilder();

				SBQueryForOffice
						.append("SELECT MDO.dcps_ddo_office_mst_id,MDO.off_name FROM mst_dcpS_ddo_office MDO where MDO.ddo_code='"
								+ lStrDdoCode + "'");

				StrSqlQueryForOffice = SBQueryForOffice.toString();
				rs1 = smt1.executeQuery(StrSqlQueryForOffice);
				Long lLngDdoOfficeId = 0l;
				tr = new ArrayList();
				Integer flag = 0;
				while (rs1.next()) {

					lLngDdoOfficeId = Long.parseLong(rs1.getString(1));
					flag = 0;

					StringBuilder SBQuery = new StringBuilder();

					SBQuery.append("SELECT MGA.gpf_Acc_no,ME.emp_name,MGY.opening_balance"
							+ " FROM mst_dcps_emp ME,mst_emp_gpF_acc MGA,mst_gpf_yearly MGY"
							+ " WHERE MGY.gpf_acc_no= MGA.gpf_acc_no AND MGY.fin_year_id = " + lLngFinYear
							+ " AND ME.dcps_emp_id = MGA.mst_gpf_emp_id AND ME.dcps_or_gpf='N' AND ME.curr_off="
							+ lLngDdoOfficeId+ " AND ME.emp_group = 'D' ");

					StrSqlQuery = SBQuery.toString();
					rs = smt.executeQuery(StrSqlQuery);
					Integer counter = 1;

					// Double lDblMonthlyTotal = 0d;
					// Double lDblContribution = 0d;
					// Double lDblRecovery = 0d;
					// Double lDblArrear = 0d;
					// Double lDblWithdrawal = 0d;
					// Double lDblTotalReceipt = 0d;
					// Double lDblTotalWithdrawal = 0d;
					// Double lDblTotalForInterest = 0d;
					// String lStrArrear = null;
					// String lStrWithdraw = null;
					// String lStrRecovery = null;
					// String lStrContri = null;
					// Double lDblInterest = 0d;
					// Double lDblClosingBalance = 0d;
					Double lDblOpeningBal = 0d;
					Double lDblTotalReceiptNdWithdraw = 0d;
					Double lDblTotalYearly = 0d;
					Double lDblInterest = 0d;
					Double lDblClosing = 0d;
					Double lDblReceipt = 0d;

					Double[] lDblContri = new Double[] { 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d };
					Double[] lDblRecovery = new Double[] { 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d };
					Double[] lDblArrear = new Double[] { 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d };
					Double[] lDblWithdraw = new Double[] { 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d };

					while (rs.next()) {
						lDblOpeningBal = 0d;
						lDblTotalReceiptNdWithdraw = 0d;
						lDblTotalYearly = 0d;
						lDblInterest = 0d;
						lDblClosing = 0d;
						lDblReceipt = 0d;
						if (flag == 0) {
							td = new ArrayList();

							td.add(new StyledData(rs1.getString(2), boldVO));
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							tr.add(td);
							flag = 1;
						}
						td = new ArrayList();
						td.add(rs.getString(1));
						td.add(rs.getString(2));
						td.add(rs.getString(3));
						td.add("Receipt(A)" + newline + "Withdrawal(B)");

						StringBuilder SBQuery2 = new StringBuilder();

						SBQuery2
								.append("SELECT COALESCE(mgm1.regular_subscription,0),COALESCE(mgm1.advance_recovery,0),COALESCE(mgm1.arrear_amount,0),COALESCE(mgm1.advance_sanctioned,0)"
										+ " FROM mst_gpF_monthly mgm1"
										+ " WHERE (mgm1.fin_year_id = "
										+ lLngFinYear
										+ " AND month_id>3 OR mgm1.fin_year_id = "
										+ (lLngFinYear + 1)
										+ " AND month_id<=3) AND mgm1.gpf_acc_no='"
										+ rs.getString(1)
										+ "' AND mgm1.status='A' ORDER BY fin_year_id,month_id");

						StrSqlQuery2 = SBQuery2.toString();
						rs2 = smt2.executeQuery(StrSqlQuery2);
						Integer i = 0;
						while (rs2.next()) {

							lDblReceipt = Double.parseDouble(rs2.getString(1)) + Double.parseDouble(rs2.getString(2))
									+ Double.parseDouble(rs2.getString(3));
							td.add(lDblReceipt + newline + rs2.getString(4));
							lDblTotalReceiptNdWithdraw = lDblTotalReceiptNdWithdraw + lDblReceipt
									- Double.parseDouble(rs2.getString(4));
							lDblContri[i] = lDblContri[i] + Double.parseDouble(rs2.getString(1));
							lDblRecovery[i] = lDblRecovery[i] + Double.parseDouble(rs2.getString(2));
							lDblArrear[i] = lDblArrear[i] + Double.parseDouble(rs2.getString(3));
							lDblWithdraw[i] = lDblWithdraw[i] + Double.parseDouble(rs2.getString(4));
							i++;

						}
						// for (int i = 4, j = 0; i < 28; i = i + 2, j++) {
						// td.add(rs.getString(i) + newline + rs.getString(i +
						// 1));
						// lDblTotalReceiptNdWithdraw =
						// lDblTotalReceiptNdWithdraw +
						// Double.parseDouble(rs.getString(i))
						// - Double.parseDouble(rs.getString(i + 1));
						//
						// }
						if (i == 0) {
							td.add("0");
							td.add("0");
							td.add("0");
							td.add("0");
							td.add("0");
							td.add("0");
							td.add("0");
							td.add("0");
							td.add("0");
							td.add("0");
							td.add("0");
							td.add("0");
						}
						lDblOpeningBal = Double.parseDouble(rs.getString(3));
						lDblTotalYearly = lDblOpeningBal + lDblTotalReceiptNdWithdraw;

						td.add(lDblTotalReceiptNdWithdraw);
						td.add(lDblTotalYearly);
						lDblInterest = (lDblOpeningBal + lDblTotalReceiptNdWithdraw) * 8 / 100;
						td.add(lDblInterest);
						lDblClosing = lDblTotalYearly + lDblInterest;
						td.add(lDblClosing);
						tr.add(td);
						counter = counter + 1;

					}

					if (counter > 1) {
						td = new ArrayList();
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						td.add(newline);
						tr.add(td);

						td = new ArrayList();
						td.add("");
						td.add("");
						td.add("");
						td.add("Contribution");
						for (int i = 0; i < 12; i++) {
							td.add(lDblContri[i]);
						}

						td.add("");
						td.add("");
						td.add("");
						td.add("");
						tr.add(td);

						td = new ArrayList();
						td.add("");
						td.add("");
						td.add("");
						td.add("Recovery Amount");
						for (int i = 0; i < 12; i++) {
							td.add(lDblRecovery[i]);
						}
						td.add("");
						td.add("");
						td.add("");
						td.add("");
						tr.add(td);

						td = new ArrayList();
						td.add("");
						td.add("");
						td.add("");
						td.add("Arrears");
						for (int i = 0; i < 12; i++) {
							td.add(lDblArrear[i]);
						}
						td.add("");
						td.add("");
						td.add("");
						td.add("");
						tr.add(td);

						td = new ArrayList();
						td.add("");
						td.add("");
						td.add("");
						td.add("Total Receipt");
						for (int i = 0; i < 12; i++) {
							td.add(lDblContri[i] + lDblRecovery[i] + lDblArrear[i]);
						}
						td.add("");
						td.add("");
						td.add("");
						td.add("");
						tr.add(td);

						td = new ArrayList();
						td.add("");
						td.add("");
						td.add("");
						td.add("Withdrawal");
						for (int i = 0; i < 12; i++) {
							td.add(lDblWithdraw[i]);
						}
						td.add("");
						td.add("");
						td.add("");
						td.add("");
						tr.add(td);
					}
				}
				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("800006", lObjReport.getLangId(), lObjReport.getLocId());

				(rptTd).setRelatedReport(RptVo);

				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				lLstDataList.add(rptList1);

				rowList = new ArrayList();
				rowList.add(newline);
				lLstDataList.add(rowList);
				rowList = new ArrayList();
				rowList.add("Recorded By:");
				lLstDataList.add(rowList);
				rowList = new ArrayList();
				rowList.add("Verified By:");
				lLstDataList.add(rowList);
				rowList = new ArrayList();
				rowList.add("Approved By:");
				lLstDataList.add(rowList);
			}

		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		} finally {
			try {
				if (smt != null) {
					smt.close();
				}
				if (smt1 != null) {
					smt1.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (rs1 != null) {
					rs1.close();
				}
				if (con != null) {
					con.close();
				}

				smt = null;
				smt1 = null;
				rs = null;
				rs1 = null;
				con = null;
			} catch (Exception e1) {				
				gLogger.error("Exception :" + e1, e1);

			}
		}
		return lLstDataList;
	}

	public List<ComboValuesVO> getYearList(String lStrLangId, String lStrLocationCode) throws Exception {

		ArrayList<ComboValuesVO> arrYear = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;

		String fin_year_id = null;
		String fin_name = null;
		try {

			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer("select * from sgvc_fin_year_mst where lang_id ='" + lStrLangId
					+ "'  and fin_year_code between '2008' and '2013' order by fin_year_code ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				fin_year_id = lRs.getString("fin_year_id");
				fin_name = lRs.getString("fin_year_desc");
				vo.setId(fin_year_id);
				vo.setDesc(fin_name);
				arrYear.add(vo);
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
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrYear;
	}

	public List<ComboValuesVO> getDDOOfficeList(String lStrLangId, String lStrLocationCode) throws Exception {

		ArrayList<ComboValuesVO> arrYear = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;

		String fin_year_id = null;
		String fin_name = null;
		try {

			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					"select MDO.* from mst_dcps_ddo_office MDO,org_ddo_mst ODM where ODM.ddo_code =MDO.ddo_code and MDO.location_code="
							+ lStrLocationCode);

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				fin_year_id = lRs.getString("dcps_ddo_office_mst_id");
				fin_name = lRs.getString("off_name");
				vo.setId(fin_year_id);
				vo.setDesc(fin_name);
				arrYear.add(vo);
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
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrYear;
	}

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	private String getFinYearDesc(Long lLngFinYearId) {
		new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;

		String fin_name = null;
		try {

			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer("select fin_year_desc from sgvc_fin_year_mst where fin_year_id = " + lLngFinYearId);

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				fin_name = lRs.getString("fin_year_desc");
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
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return fin_name;
	}

}
