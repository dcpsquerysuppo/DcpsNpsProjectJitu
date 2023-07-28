/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 25, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.report;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.PageBreak;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAO;
import com.tcs.sgv.gpf.dao.ScheduleGenerationDAOImpl;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Aug 25, 2011
 */

public class GPFScheduleGenerationReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(GPFScheduleGenerationReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		Statement smt1 = null;
		Statement smt2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ReportsDAO reportsDao = new ReportsDAOImpl();
		ArrayList dataList = new ArrayList();
		ArrayList tr = null;
		ArrayList td = null;
		ArrayList rptList1 = null;
		TabularData rptTd = null;
		ReportVO RptVo = null;
		String StrSqlQuery = "";

		try {
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();

			Map lServiceMap = (Map) requestAttributes.get("serviceMap");
			Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
			Long lLngPostId = (Long) lBaseLoginMap.get("loggedInPost");
			con = lObjSessionFactory.getCurrentSession().connection();

			smt = con.createStatement();
			smt1 = con.createStatement();
			smt2 = con.createStatement();
			StyleVO[] rowsFontsVO = new StyleVO[4];
			rowsFontsVO[0] = new StyleVO();
			rowsFontsVO[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rowsFontsVO[0].setStyleValue("12");
			rowsFontsVO[1] = new StyleVO();
			rowsFontsVO[1].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			rowsFontsVO[1].setStyleValue("white");
			rowsFontsVO[2] = new StyleVO();
			rowsFontsVO[2].setStyleId(IReportConstants.BORDER);
			rowsFontsVO[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			rowsFontsVO[3] = new StyleVO();
			rowsFontsVO[3].setStyleId(26);
			rowsFontsVO[3].setStyleValue("JavaScript:self.close()");

			StyleVO[] rightAlign = new StyleVO[2];
			rightAlign[0] = new StyleVO();
			rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			rightAlign[1] = new StyleVO();
			rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightAlign[1].setStyleValue("12");

			StyleVO[] noBorderAndSelfClose = new StyleVO[3];
			noBorderAndSelfClose[0] = new StyleVO();
			noBorderAndSelfClose[0].setStyleId(IReportConstants.BORDER);
			noBorderAndSelfClose[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			noBorderAndSelfClose[1] = new StyleVO();
			noBorderAndSelfClose[1].setStyleId(26);
			noBorderAndSelfClose[1].setStyleValue("JavaScript:self.close()");
			noBorderAndSelfClose[2] = new StyleVO();
			noBorderAndSelfClose[2].setStyleId(IReportConstants.REPORT_PAGINATION);
			noBorderAndSelfClose[2].setStyleValue("NO");

			StyleVO[] withBorder = new StyleVO[1];
			withBorder[0] = new StyleVO();
			withBorder[0].setStyleId(IReportConstants.BORDER);
			withBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_MEDIUM);

			StyleVO[] boldVO = new StyleVO[2];
			boldVO[0] = new StyleVO();
			boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldVO[1] = new StyleVO();
			boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldVO[1].setStyleValue("10");

			StyleVO[] CenterAlignVO = new StyleVO[3];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			CenterAlignVO[2] = new StyleVO();
			CenterAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			CenterAlignVO[2].setStyleValue("12");

			StyleVO[] centerUnderlineBold = new StyleVO[4];
			centerUnderlineBold[0] = new StyleVO();
			centerUnderlineBold[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerUnderlineBold[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			centerUnderlineBold[1] = new StyleVO();
			centerUnderlineBold[1].setStyleId(IReportConstants.STYLE_TEXT_DECORATION);
			centerUnderlineBold[1].setStyleValue(IReportConstants.VALUE_STYLE_TEXT_DECORATION_UNDERLINE);
			centerUnderlineBold[2] = new StyleVO();
			centerUnderlineBold[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			centerUnderlineBold[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			centerUnderlineBold[3] = new StyleVO();
			centerUnderlineBold[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			centerUnderlineBold[3].setStyleValue("12");

			if (report.getReportCode().equals("800001")) {

				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date date = new Date();

				Long lLngBillGroupId = Long.valueOf((String) report.getParameterValue("billGroupId"));
				Long lLngMonthId = Long.valueOf((String) report.getParameterValue("monthId"));
				String lStrMonth = (String) report.getParameterValue("monthName");
				Long lLngYearId = Long.valueOf((String) report.getParameterValue("yearId"));
				String lStrYearCode = (String) report.getParameterValue("yearCode");
				String lStrDdoCode = "";
				String lStrOfficeName = "";
				String treasuryId = "";
				String treasuryName = "";
				ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serviceLocator
						.getSessionFactory());
				// List lLstOfficeDtls =
				// lObjScheduleGenerationDAO.getDdoOfficeNameForDdoCode(lLngBillGroupId);
				// if (lLstOfficeDtls != null && lLstOfficeDtls.size() > 0) {
				// Object[] lObjOffice = (Object[]) lLstOfficeDtls.get(0);
				// lStrOfficeName = lObjOffice[0].toString();
				// lStrDdoCode = lObjOffice[1].toString();
				// }
				List lLstDdoInfo = lObjScheduleGenerationDAO.getDdoInfoForPost(lLngPostId);
				if (lLstDdoInfo != null && lLstDdoInfo.size() > 0) {
					Object[] objArr = (Object[]) lLstDdoInfo.get(0);
					lStrDdoCode = objArr[0].toString();
					lStrOfficeName = objArr[1].toString();
				}
				treasuryId = lObjScheduleGenerationDAO.getTreasuryCodeForDDO(lStrDdoCode);
				treasuryName = lObjScheduleGenerationDAO.getTreasuryNameForDDO(lStrDdoCode);
				report.setStyleList(noBorderAndSelfClose);

				String header1 = "<center>" + "<font size=\"1\"> "
						+ "Schedule Showing the Subscriptions and Refund of the GPF for following Government Servants"
						+ "</font></center>";
				String header2 = "<center><b>" + "<font size=\"1\"> " + "From Major Head 8009" + "</font></b></center>";
				String additionalHeader = header1 + header2 + newline;
				report.setAdditionalHeader(additionalHeader);

				ArrayList rowList = new ArrayList();
				rowList.add(new StyledData("For the month of " + lStrMonth + " " + lStrYearCode, boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("AG Office:", boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Name of the Office: " + lStrOfficeName + "(" + lStrDdoCode.substring(4)
						+ ")" + space(10) + "Treasury: " + treasuryName + "(" + treasuryId + ")", boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				tr = new ArrayList();

				StringBuilder SBQuery = new StringBuilder();

				SBQuery
						.append("SELECT MGA.gpf_acc_no ,MGE.emp_name ,sevaarth_id,basic_Pay,COALESCE(MGM.regular_subscription,0) ,COALESCE(MGM.advance_recovery,0),COALESCE(MGM.inst_no,0),COALESCE(MGM.total_inst,0),MGM.arrear_amount"
								+ " FROM mst_emp_gpf_acc MGA,mst_dcps_emp MGE,mst_gpf_monthly MGM "
								+ " WHERE MGE.dcps_Emp_Id = MGA.mst_Gpf_Emp_Id AND MGE.dcps_or_gpf='N' AND MGE.billgroup_id = "
								+ lLngBillGroupId
								+ " AND MGM.status='P' AND MGM.gpf_acc_no= MGA.gpf_acc_no AND month_id = "
								+ lLngMonthId + " AND MGM.fin_year_id = " + lLngYearId + " AND MGE.emp_group = 'D' ");

				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);
				Integer counter = 1;
				String lStrSubscription = null;
				String lStrRecovery = null;
				String lStrArrear = null;
				Double lDblSubscription = 0d;
				Double lDblRecovery = 0d;
				Double lDblTotalSubs = 0d;
				Double lDblTotalRecovery = 0d;
				Double lDblArrearAmount = 0d;
				Double lDblTotalArrears = 0d;
				Double empTotalSubs = 0d;
				Double officeTotalSubs = 0d;
				while (rs.next()) {
					lStrSubscription = rs.getString(5);
					lStrRecovery = rs.getString(6);
					lStrArrear = rs.getString(9);
					if (!lStrSubscription.equals(null) && !lStrSubscription.equals("")) {
						lDblSubscription = Double.parseDouble(lStrSubscription);
					}
					if (!lStrRecovery.equals(null) && !lStrRecovery.equals("")) {
						lDblRecovery = Double.parseDouble(lStrRecovery);
					}
					if (!lStrArrear.equals(null) && !lStrArrear.equals("")) {
						lDblArrearAmount = Double.parseDouble(lStrArrear);
					}
					td = new ArrayList();
					td.add(counter);
					td.add(rs.getString(1));
					td.add(rs.getString(2) + " (" + rs.getString(3) + ")");
					td.add(rs.getString(4));
					td.add(lDblSubscription);
					td.add("0");
					td.add(lDblArrearAmount);
					td.add(lDblRecovery);
					td.add(rs.getString(7) + "/" + rs.getString(8));
					empTotalSubs = lDblSubscription + lDblRecovery + lDblArrearAmount;
					td.add(empTotalSubs);
					td.add("");

					tr.add(td);

					counter = counter + 1;
					lDblTotalSubs = lDblTotalSubs + lDblSubscription;
					lDblTotalRecovery = lDblTotalRecovery + lDblRecovery;
					lDblTotalArrears = lDblTotalArrears + lDblArrearAmount;
					officeTotalSubs = officeTotalSubs + empTotalSubs;
				}
				if (counter > 1) {
					rptTd = new TabularData(tr);
					RptVo = reportsDao.getReport("800002", report.getLangId(), report.getLocId());

					(rptTd).setRelatedReport(RptVo);

					rptList1 = new ArrayList();
					rptList1.add(rptTd);
					dataList.add(rptList1);
				}
				String lStrTotAmtInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(officeTotalSubs
						.toString()));

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Total Rupees : " + lStrTotAmtInWords, boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("CERTIFICATE", centerUnderlineBold));
				dataList.add(rowList);
				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add(new StyledData(
								"Certified that I have personally verified the correctness of the details in this schedule and the",
								rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Dated: " + dateFormat.format(date), rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Accounts Officer", rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(lStrOfficeName, rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add(new StyledData("For use of Audit Office" + space(50) + "Date of Encashment: ", rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add(new StyledData(
								"1. Certified that the name,amounts of individuals deductions & the total shown in column(7) have been checked by reference to the bill, vide.paragraph 224 of the Audit Manual.",
								rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add(new StyledData(
								"2. Certified that the rates of pay  as shown in column(3) have been verified with the amounts actually drawn in the bill.",
								rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Dated: " + dateFormat.format(date) + space(50) + "Initials of the Auditor",
						rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				// second page starts

				rowList = new ArrayList();
				rowList.add(new PageBreak());
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("FORM XII", centerUnderlineBold));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"Certificate Attached with the Paybill in respect of class IV Govt. Servants for the Month of "
								+ lStrMonth + " " + lStrYearCode, boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Paid in " + "for the office of Directorate Of Accounts and Treasuries",
						boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Certified that the sum of Rs. "
						+ " has been deducted from the salary for the month of " + lStrMonth + " " + lStrYearCode,
						boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("1.Subscription\t: " + lDblTotalSubs, rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("2.Refund of Withdrawal\t: " + lDblTotalRecovery, rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("3.DA Arrears\t: " + lDblTotalArrears, rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Total Rs.\t: " + officeTotalSubs, rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Total Rupees in words : " + lStrTotAmtInWords, boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("CERTIFICATE", centerUnderlineBold));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add(new StyledData(
								"Certified that the Total Recoveries shown above agrees with the amount actually recovered and shown in the body of the bill.",
								rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"Certified that the recoveries have been duly posted in Register of Advance (Form A).",
						rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Dated: " + dateFormat.format(date), rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Accounts Officer", rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(lStrOfficeName, rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Portion for Treasury Office", rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Treasury Voucher No and Date", rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Challan No and Date", rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Treasury Officer/ Pay & Accounts Officer", rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("VERIFICATION TIME: " + date, rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(newline);
				dataList.add(rowList);
			}
		} catch (Exception e) {			
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
				if (smt1 != null) {
					smt1.close();
				}

				if (rs1 != null) {
					rs1.close();
				}
				if (smt2 != null) {
					smt2.close();
				}

				if (rs2 != null) {
					rs2.close();
				}

				smt = null;
				rs = null;
				con = null;
				smt1 = null;
				rs1 = null;
				smt2 = null;
				rs2 = null;

			} catch (Exception e1) {				
				gLogger.error("Exception :" + e1, e1);

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
