/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	March 17, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
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
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO;
import com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 March 17, 2011
 */

public class SixPayArrearAmountDepositionReportSchedule extends
		DefaultReportDataFinder {

	private static final Logger gLogger = Logger
			.getLogger(SixPayArrearAmountDepositionReportSchedule.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;

	public Collection findReportData(ReportVO report, Object criteria)
			throws ReportException {

		String langId = report.getLangId();
		String locId = report.getLocId();
		
		Connection con = null;

		String lStrDdoCode = null;
		String lStrUserType = null;
		Long lLngYearId = null;
		String lStrDdoName = null;
		String lStrDdoOffice = null;
		String lStrTreasuryName = null;
		String lStrTreasuryCode = null;
		OrgDdoMst lObjOrgDdoMst = null;

		requestAttributes = (Map) ((Map) criteria)
				.get(IReportConstants.REQUEST_ATTRIBUTES);
		serviceLocator = (ServiceLocator) requestAttributes
				.get("serviceLocator");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();

		Map lServiceMap = (Map) requestAttributes.get("serviceMap");
		Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
		Long postId = (Long) lBaseLoginMap.get("loggedInPost");
		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		ReportsDAO reportsDao = new ReportsDAOImpl();
		ArrayList dataList = new ArrayList();
		ArrayList tr = null;
		ArrayList td = null;
		ArrayList rptList1 = null;
		TabularData rptTd = null;
		ReportVO RptVo = null;

		try {

			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria)
					.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			Long locationId = (Long) loginDetail.get("locationId");

			StyleVO[] rowsFontsVO = new StyleVO[6];
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
			rowsFontsVO[4]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			rowsFontsVO[5] = new StyleVO();
			rowsFontsVO[5].setStyleId(26);
			rowsFontsVO[5].setStyleValue("JavaScript:self.close()");

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();
			report.setStyleList(rowsFontsVO);

	
			StyleVO[] centerUnderlineBoldBig = new StyleVO[4];
			centerUnderlineBoldBig[0] = new StyleVO();
			centerUnderlineBoldBig[0]
					.setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerUnderlineBoldBig[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			centerUnderlineBoldBig[1] = new StyleVO();
			centerUnderlineBoldBig[1]
					.setStyleId(IReportConstants.STYLE_TEXT_DECORATION);
			centerUnderlineBoldBig[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_TEXT_DECORATION_UNDERLINE);
			centerUnderlineBoldBig[2] = new StyleVO();
			centerUnderlineBoldBig[2]
					.setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			centerUnderlineBoldBig[2]
					.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			centerUnderlineBoldBig[3] = new StyleVO();
			centerUnderlineBoldBig[3]
					.setStyleId(IReportConstants.STYLE_FONT_SIZE);
			centerUnderlineBoldBig[3]
					.setStyleValue(IReportConstants.VALUE_FONT_SIZE_LARGE);

			StyleVO[] rightAlign = new StyleVO[2];
			rightAlign[0] = new StyleVO();
			rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightAlign[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			rightAlign[1] = new StyleVO();
			rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightAlign[1].setStyleValue("14");

			StyleVO[] boldVO = new StyleVO[2];
			boldVO[0] = new StyleVO();
			boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldVO[1] = new StyleVO();
			boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldVO[1].setStyleValue("14");

			// for Center Alignment format
			StyleVO[] CenterAlignVO = new StyleVO[2];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			if (report.getReportCode().equals("700012")) {

				report.setStyleList(noBorder);
				report.setStyleList(rowsFontsVO);
				ArrayList rowList = new ArrayList();

				SixPCArrearsYearlyDAO lObjSixPCArrearsYearlyDAO = new SixPCArrearsYearlyDAOImpl(
						SixPCArrearsYearlyDepositionReport.class,
						serviceLocator.getSessionFactory());

				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
						serviceLocator.getSessionFactory());

				lLngYearId = Long.parseLong(report.getParameterValue("yearId")
						.toString());

				lStrUserType = report.getParameterValue("UserType").toString();
				if (lStrUserType != null && lStrUserType.length() > 0) {
					if (lStrUserType.equals("DDOAsst")) {
						lStrDdoCode = lObjSixPCArrearsYearlyDAO
								.getDdoCode(postId);
					}

					if (lStrUserType.equals("DDO")) {
						lStrDdoCode = lObjSixPCArrearsYearlyDAO
								.getDdoCodeForDDO(postId);
					}
				}

				lObjOrgDdoMst = lObjDcpsCommonDAO
						.getDdoVOForDdoCode(lStrDdoCode);

				lStrDdoName = lObjOrgDdoMst.getDdoName();
				lStrDdoOffice = lObjOrgDdoMst.getDdoOffice();

				lStrTreasuryName = lObjDcpsCommonDAO
						.getTreasuryNameForDDO(lStrDdoCode);
				lStrTreasuryCode = lObjDcpsCommonDAO
						.getTreasuryCodeForDDO(lStrDdoCode);

				rowList.add("" + space(1));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Name of Office: " + lStrDdoOffice,
						boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Name of DDO/Code No. : "
						+ lStrDdoName + space(3) + "( " + lStrDdoCode + ") ",
						boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"Region / Treasury / Sub-Treasury Code : "
								+ lStrTreasuryName + space(3) + "( "
								+ lStrTreasuryCode + ") ", boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);

				// Employee List and corresponding values display

				String lStrEmpId = (String) report.getParameterValue("empid");
				String[] lArrStrEmpId = lStrEmpId.split("~");
				Long[] lArrLongEmpId = new Long[lArrStrEmpId.length];

				for (Integer lInt = 0; lInt < lArrLongEmpId.length; lInt++) {
					lArrLongEmpId[lInt] = Long.valueOf(lArrStrEmpId[lInt]);
				}

				Long grossYearlyAmount = 0l;

				tr = new ArrayList();

				for (Integer lInt = 0; lInt < lArrLongEmpId.length; lInt++) {

					String StrSqlQuery = "";
					StringBuffer sql = new StringBuffer("select em.EMP_NAME as name,");
					sql.append(" em.DCPS_ID as dcpsId,");
					sql.append(" pc.YEARLY_AMOUNT as yearlyArrearAmount");
					sql.append(" from mst_dcps_emp em join rlt_dcps_sixpc_yearly pc on em.DCPS_EMP_ID=pc.DCPS_EMP_ID where em.DDO_CODE = '"+ lStrDdoCode + "'");
					sql.append(" and pc.fin_year_id = '" + lLngYearId + "'" );
					sql.append(" and em.DCPS_EMP_ID = " + lArrLongEmpId[lInt] );
					sql.append(" and pc.status_flag in ('A','G')");

					StrSqlQuery = sql.toString();
					rs = smt.executeQuery(StrSqlQuery);

					while (rs.next()) {

						td = new ArrayList();
						td.add(lInt + 1);

						if (rs.getString("name") != null) {
							td.add(new StyledData(rs.getString("name"),
									CenterAlignVO));
						} else {
							td.add("");
						}

						if (rs.getString("dcpsId") != null) {
							td.add(new StyledData(rs.getString("dcpsId"),
									CenterAlignVO));
						} else {
							td.add("");
						}

						if (rs.getString("yearlyArrearAmount") != null) {
							td.add(new StyledData(rs
									.getString("yearlyArrearAmount"),
									rightAlign));
						} else {
							td.add("");
						}

						tr.add(td);

						grossYearlyAmount = grossYearlyAmount
								+ Long.valueOf(rs
										.getString("yearlyArrearAmount"));
					}

				}

				td = new ArrayList();
				td.add("");
				td.add("");
				td.add(new StyledData("Grand Total :", rightAlign));
				td.add(new StyledData(grossYearlyAmount, rightAlign));
				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("700011", report.getLangId(),
						report.getLocId());
				(rptTd).setRelatedReport(RptVo);
				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);

				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Total amount Rupees: "
						+ grossYearlyAmount, boldVO));
				dataList.add(rowList);

				String lStrTotAmtInWords = EnglishDecimalFormat
						.convertWithSpace(new BigDecimal(grossYearlyAmount));

				rowList = new ArrayList();
				rowList.add(new StyledData("In words Rupees : "
						+ lStrTotAmtInWords, boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("Gross Amount of the bill / challan Rs.= "
						+ grossYearlyAmount);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("Net Amount of the bill / challan Rs.= "
						+ grossYearlyAmount);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("Challan No. ________ & Date. __________");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("CERTIFICATE",
						centerUnderlineBoldBig));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add("Certified that I have personally verified the correctness of the details in this schedule and they are found to be correct.");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add("Date: ___________ Date of Encashment: _____________________");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Signature", rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Deputy Director", rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("A - 411018", rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("For use of Audit Officer:");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add("1: Certified that the name amount of individual's deduction and the total shown in column (8) have been checked with reference to the bill, vide, paragraph 224 of the Audit Manual");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add("2: Certified that the rate of pay as shown in column (5) has been verified with the amount actually drawn in the bill");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add("3: Certified that challan for Rs. ------------- is attached to this schedule.");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Signature", rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Treasury Officer", rightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Demo Treasury", rightAlign));
				dataList.add(rowList);
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

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

}
