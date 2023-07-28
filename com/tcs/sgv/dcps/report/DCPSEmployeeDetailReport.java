/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 9, 2011		Vihan Khatri								
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.jfree.util.StringUtils;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Feb 9, 2011
 */
public class DCPSEmployeeDetailReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger
			.getLogger(DCPSEmployeeDetailReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	static ResourceBundle lBudConstantsBundle = ResourceBundle.getBundle(
			"resources/dcps/dcpsLabels", Locale.getDefault());

	public Collection findReportData(ReportVO report, Object criteria)
			throws ReportException {

		report.getLangId();

		report.getLocId();
		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		TabularData td1 = null;
		TabularData td2 = null;
		TabularData td3 = null;
		TabularData td4 = null;
		TabularData td5 = null;
		TabularData td6 = null;

		String empId = null;
		ArrayList dataList = new ArrayList();
		ReportVO RptVo = null;
		ReportsDAO reportsDao = null;
		reportsDao = new ReportsDAOImpl();

		StyleVO[] baseFont1 = new StyleVO[4];
		baseFont1[0] = new StyleVO();
		baseFont1[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		baseFont1[0].setStyleValue("16");
		baseFont1[1] = new StyleVO();
		baseFont1[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		baseFont1[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		baseFont1[2] = new StyleVO();
		baseFont1[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		baseFont1[2]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		baseFont1[3] = new StyleVO();
		baseFont1[3].setStyleId(IReportConstants.BORDER);
		baseFont1[3].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] baseFont2 = new StyleVO[4];
		baseFont2[0] = new StyleVO();
		baseFont2[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		baseFont2[0].setStyleValue("10");
		baseFont2[1] = new StyleVO();
		baseFont2[1].setStyleId(IReportConstants.FONT_WEIGHT);
		baseFont2[1].setStyleValue(IReportConstants.VALUE_FONT_STYLE_ITALIC);
		baseFont2[2] = new StyleVO();
		baseFont2[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		baseFont2[2]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		baseFont2[3] = new StyleVO();
		baseFont2[3].setStyleId(IReportConstants.BORDER);
		baseFont2[3].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] baseFont3 = new StyleVO[3];
		baseFont3[0] = new StyleVO();
		baseFont3[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		baseFont3[0].setStyleValue("10");
		baseFont3[1] = new StyleVO();
		baseFont3[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		baseFont3[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		baseFont3[2] = new StyleVO();
		baseFont3[2].setStyleId(IReportConstants.BORDER);
		baseFont3[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] baseFont4 = new StyleVO[5];
		baseFont4[0] = new StyleVO();
		baseFont4[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		baseFont4[0].setStyleValue("14");
		baseFont4[1] = new StyleVO();
		baseFont4[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		baseFont4[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		baseFont4[2] = new StyleVO();
		baseFont4[2].setStyleId(IReportConstants.STYLE_FONT_STYLE);
		baseFont4[2]
				.setStyleValue(IReportConstants.VALUE_STYLE_TEXT_DECORATION_UNDERLINE);
		baseFont4[3] = new StyleVO();
		baseFont4[3].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		baseFont4[3]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		baseFont4[4] = new StyleVO();
		baseFont4[4].setStyleId(IReportConstants.BORDER);
		baseFont4[4].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

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

		// for Right Alignment format
		StyleVO[] RightAlignVO = new StyleVO[2];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[0]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.BORDER);
		RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		// for Left Alignment format
		StyleVO[] LeftAlignVO = new StyleVO[2];
		LeftAlignVO[0] = new StyleVO();
		LeftAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		LeftAlignVO[0]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		LeftAlignVO[1] = new StyleVO();
		LeftAlignVO[1].setStyleId(IReportConstants.BORDER);
		LeftAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] rowsFontsVO = new StyleVO[2];
		rowsFontsVO[0] = new StyleVO();
		rowsFontsVO[0].setStyleId(IReportConstants.BORDER);
		rowsFontsVO[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		rowsFontsVO[1] = new StyleVO();
		rowsFontsVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rowsFontsVO[1].setStyleValue("13");

		StyleVO[] rowsFontsVOForPara = new StyleVO[2];
		rowsFontsVOForPara[0] = new StyleVO();
		rowsFontsVOForPara[0].setStyleId(IReportConstants.BORDER);
		rowsFontsVOForPara[0]
				.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		rowsFontsVOForPara[1] = new StyleVO();
		rowsFontsVOForPara[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rowsFontsVOForPara[1].setStyleValue("10");

		StyleVO[] noBorder = new StyleVO[4];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		noBorder[1] = new StyleVO();
		noBorder[1].setStyleId(IReportConstants.ROWS_PER_PAGE);
		noBorder[1].setStyleValue("40");
		noBorder[2] = new StyleVO();
		noBorder[2].setStyleId(IReportConstants.NO_HEADER);
		noBorder[2].setStyleValue(IReportConstants.VALUE_YES);
		noBorder[3] = new StyleVO();
		noBorder[3].setStyleId(IReportConstants.NO_FOOTER);
		noBorder[3].setStyleValue(IReportConstants.VALUE_YES);

		StyleVO[] noBorderAndSelfClose = new StyleVO[5];
		noBorderAndSelfClose[0] = new StyleVO();
		noBorderAndSelfClose[0].setStyleId(IReportConstants.BORDER);
		noBorderAndSelfClose[0]
				.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		noBorderAndSelfClose[1] = new StyleVO();
		noBorderAndSelfClose[1].setStyleId(IReportConstants.ROWS_PER_PAGE);
		noBorderAndSelfClose[1].setStyleValue("40");
		noBorderAndSelfClose[2] = new StyleVO();
		noBorderAndSelfClose[2].setStyleId(IReportConstants.NO_HEADER);
		noBorderAndSelfClose[2].setStyleValue(IReportConstants.VALUE_YES);
		noBorderAndSelfClose[3] = new StyleVO();
		noBorderAndSelfClose[3].setStyleId(IReportConstants.NO_FOOTER);
		noBorderAndSelfClose[3].setStyleValue(IReportConstants.VALUE_YES);
		noBorderAndSelfClose[4] = new StyleVO();
		noBorderAndSelfClose[4].setStyleId(26);
		noBorderAndSelfClose[4].setStyleValue("JavaScript:self.close()");

		String lStrNewLine = StringUtils.getLineSeparator();

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

			loginDetail.get("locationId");

			StringBuffer sql = new StringBuffer();
			String StrSqlQuery = "";

			if (report.getReportCode().equals("700001")) {

				report.setStyleList(noBorderAndSelfClose);

				ArrayList rowList = new ArrayList();

				rowList
						.add(new StyledData(
								"(As Referred to in para no. 8 Government Resolution,Finance Department,No.CPS 1007/18/SER-4, dated 7 July)",
								baseFont2));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add(new StyledData(
								"Employee Appointed on or after 01/11/2005",
								baseFont1));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("");
				dataList.add(rowList);

				empId = (String) report.getParameterValue("empid");

				sql = new StringBuffer("select EM.DCPS_EMP_ID,EM.DCPS_ID,");
				sql.append(" EM.EMP_NAME as Name,EM.BUCKLE_NO as buckleNo,EM.SALUTATION as SALUTATION,");
				sql
						.append(" EM.GENDER as GENDER,EM.DOB as DOB,EM.DOJ as DOJ,EM.PARENT_DEPT as PARENT_DEPT,DC.cadre_name as CADRE,EM.EMP_GROUP AS EMP_GROUP, ");
				sql
						.append(" DO.OFF_NAME as OFF_NAME,DO.ADDRESS1 as ADDRESS1,DO.ADDRESS2 as ADDRESS2,");
				sql
						.append(" DS.dsgn_name as DESIGNATION,concat(EM.ADDRESS_BUILDING,concat(' ',EM.ADDRESS_STREET)) as ResADDRESS1 ,");
				sql.append(" EM.pay_commission as PAYCOMMISSION,");
				sql
						.append(" concat(EM.LANDMARK,concat(' ',EM.LOCALITY)) as ResADDRESS2,concat(PS.scale_start_amt,concat('-',concat(PS.scale_end_amt,concat(' (',concat(PS.scale_incr_amt,')'))))) AS PAYSCALEFIFTHPC,EM.CNTCT_NO as CNTCT_NO, ");
				sql
						.append(" concat(PS.scale_start_amt,concat('-',concat(PS.scale_end_amt,concat(' (',concat(PS.scale_grade_pay,')'))))) AS PAYSCALESIXTHPC,");
				sql
						.append(" EM.CNTCT_NO as OFF_CNTCT_NO,EM.CELL_NO as CELL_NO,EM.DDO_CODE as DDO_CODE,RDO.location_code as TREASURY_CODE, EM.FORM_STATUS as FORM_STATUS,");
				sql.append(" EM.REG_STATUS as REG_STATUS");
				sql
						.append(" from mst_dcps_emp EM join org_designation_mst DS on EM.DESIGNATION=DS.dsgn_id ");
				sql
						.append(" join mst_dcps_cadre DC on EM.CADRE = DC.CADRE_ID ");
				sql
						.append(" JOIN hr_eis_scale_mst PS ON EM.PAYSCALE =PS.scale_id ");
				sql
						.append(" join mst_dcps_ddo_office DO on DO.DCPS_DDO_OFFICE_MST_ID = EM.CURR_OFF ");
				sql
						.append(" join rlt_ddo_org RDO on RDO.ddo_code = EM.ddo_code where DCPS_EMP_ID='"
								+ empId + "'");

				StrSqlQuery = sql.toString();
				rs = smt.executeQuery(StrSqlQuery);

				while (rs.next()) {

					ArrayList dataListforFirstFourRowsLeft = new ArrayList();

					rowList = new ArrayList();
					rowList.add(new StyledData("Sub / Treasury Code:"
							+ space(1) + rs.getString("TREASURY_CODE")
							+ space(30) + "DDO Code:" + space(1)
							+ rs.getString("DDO_CODE") + space(20)
							+ "Dept. Code" + ":" + space(1)
							+ rs.getString("PARENT_DEPT"), baseFont3));
					dataList.add(rowList);
					
					String lStrBuckleNo = "";
					if(rs.getString("buckleNo") != null)
					{
						lStrBuckleNo = " ( " + rs.getString("buckleNo") + " )";
					}

					rowList = new ArrayList();
					rowList.add(new StyledData("1.Name of Employee",
							rowsFontsVO));
					rowList.add(new StyledData(space(12) + ":" + space(1)
							+ rs.getString("Name") + lStrBuckleNo, rowsFontsVO));
					dataListforFirstFourRowsLeft.add(rowList);

					rowList = new ArrayList();
					rowList.add(new StyledData("2.Gender", rowsFontsVO));
					rowList.add(new StyledData(space(12) + ":" + space(1)
							+ rs.getString("GENDER"), rowsFontsVO));
					dataListforFirstFourRowsLeft.add(rowList);

					SimpleDateFormat lObjDateFormat1 = new SimpleDateFormat(
							"yyyy-MM-dd");

					SimpleDateFormat lObjDateFormat2 = new SimpleDateFormat(
							"dd/MM/yyyy");

					rowList = new ArrayList();
					rowList.add(new StyledData("3. Date Of Birth(DD/MM/YYYY)",
							rowsFontsVO));
					rowList.add(new StyledData(space(12)
							+ ":"
							+ space(1)
							+ lObjDateFormat2.format(lObjDateFormat1.parse(rs
									.getString("DOB"))), rowsFontsVO));
					dataListforFirstFourRowsLeft.add(rowList);

					rowList = new ArrayList();
					rowList.add(new StyledData(
							"4. Date Of Joining(DD/MM/YYYY)", rowsFontsVO));
					rowList.add(new StyledData(space(12)
							+ ":"
							+ space(1)
							+ lObjDateFormat2.format(lObjDateFormat1.parse(rs
									.getString("DOJ"))), rowsFontsVO));
					dataListforFirstFourRowsLeft.add(rowList);

					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataListforFirstFourRowsLeft.add(rowList);

					td1 = new TabularData(dataListforFirstFourRowsLeft);

					RptVo = reportsDao.getReport("700002", report.getLangId(),
							report.getLocId()); // A
					// 2-Column
					// Report
					(td1).setRelatedReport(RptVo);
					(td1).setStyles(noBorder);

					// ArrayList dataListForPhoto = new ArrayList();

					ArrayList rowListForPhoto = new ArrayList();
					rowList = new ArrayList();
					rowList.add(new StyledData(space(8) + "Photo" + lStrNewLine
							+ lStrNewLine + lStrNewLine + lStrNewLine
							+ lStrNewLine + lStrNewLine, rowsFontsVO));
					rowListForPhoto.add(rowList);

					/*
					 * rowList = new ArrayList(); rowList .add(new
					 * StyledData(space(8) + "Photo", rowsFontsVO));
					 * rowListForPhoto.add(rowList);
					 * 
					 * rowList = new ArrayList(); rowList.add("" + space(1));
					 * rowListForPhoto.add(rowList);
					 * 
					 * rowList = new ArrayList(); rowList.add(space(1)+"");
					 * rowListForPhoto.add(rowList);
					 * 
					 * rowList = new ArrayList(); rowList.add(""+space(1));
					 * rowListForPhoto.add(rowList);
					 * 
					 * rowList = new ArrayList(); rowList.add(""+space(1));
					 * rowListForPhoto.add(rowList);
					 * 
					 * rowList = new ArrayList(); rowList.add(""+space(1));
					 * rowListForPhoto.add(rowList);
					 * 
					 * rowList = new ArrayList(); rowList.add(""+space(1));
					 * rowListForPhoto.add(rowList);
					 */

					/*
					 * rowList = new ArrayList(); rowList.add(new
					 * StyledData(space(8) + "Photo" +
					 * lBudConstantsBundle.getString("NEWLINE") +
					 * lBudConstantsBundle.getString("NEWLINE") + space(6) +
					 * "Attested" + lBudConstantsBundle.getString("NEWLINE") +
					 * lBudConstantsBundle.getString("NEWLINE") + space(10) +
					 * "By" + lBudConstantsBundle.getString("NEWLINE") +
					 * lBudConstantsBundle.getString("NEWLINE") + space(9) +
					 * "DDO" + lBudConstantsBundle.getString("NEWLINE"),
					 * rowsFontsVO));
					 * 
					 * rowListForPhoto.add(rowList);
					 */
					/*
					 * rowListForPhoto = new ArrayList(); rowListForPhoto
					 * .add(new StyledData("Attested", rowsFontsVO));
					 * dataListForPhoto.add(rowListForPhoto);
					 * 
					 * rowListForPhoto = new ArrayList();
					 * rowListForPhoto.add(new StyledData(space(5) + "By",
					 * rowsFontsVO)); dataListForPhoto.add(rowListForPhoto);
					 * 
					 * rowListForPhoto = new ArrayList();
					 * rowListForPhoto.add(new StyledData(space(3) + "DDO",
					 * rowsFontsVO)); dataListForPhoto.add(rowListForPhoto);
					 */

					td2 = new TabularData(rowListForPhoto);
					// (td2).setStyles(noBorder);

					RptVo = reportsDao.getReport("700003", report.getLangId(),
							report.getLocId()); // A
					// 1-Column
					// report
					(td2).setRelatedReport(RptVo);

					ArrayList dataListNull = new ArrayList();
					dataListNull = new ArrayList();

					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataListNull.add(rowList);
					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataListNull.add(rowList);
					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataListNull.add(rowList);
					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataListNull.add(rowList);
					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataListNull.add(rowList);

					td6 = new TabularData(dataListNull);
					RptVo = reportsDao.getReport("700008", report.getLangId(),
							report.getLocId()); // A
					// 1-Column
					// report
					(td6).setRelatedReport(RptVo);
					(td6).setStyles(noBorder);

					ArrayList dataListForFirstFourRows = new ArrayList();
					rowList = new ArrayList();
					rowList.add(td1);
					rowList.add(td2);
					rowList.add(td6);
					dataListForFirstFourRows.add(rowList);

					td3 = new TabularData(dataListForFirstFourRows);

					RptVo = reportsDao.getReport("700004", report.getLangId(),
							report.getLocId()); // A
					// 3-Column
					// Report
					(td3).setRelatedReport(RptVo);
					(td3).setStyles(noBorder);

					rowList = new ArrayList();
					rowList.add(td3);
					dataList.add(rowList);

					rowList = new ArrayList();
					if (rs.getString("OFF_NAME") != null) {
						rowList.add(new StyledData(space(1)
								+ "5. Name and Full Address of office"
								+ space(10) + ":" + space(1)
								+ rs.getString("OFF_NAME"), rowsFontsVO));
					} else {
						rowList.add(new StyledData(space(1)
								+ "5. Name and Full Address of office"
								+ space(10) + ":" + space(1), rowsFontsVO));
					}
					dataList.add(rowList);

					rowList = new ArrayList();
					if (rs.getString("ADDRESS1") != null) {
						rowList.add(new StyledData(space(60) + ":" + space(1)
								+ rs.getString("ADDRESS1"), rowsFontsVO));
					} else {
						rowList.add(new StyledData(space(60) + ":" + space(1),
								rowsFontsVO));
					}
					if (rs.getString("ADDRESS2") != null) {
						rowList.add(new StyledData(", "
								+ rs.getString("ADDRESS2"), rowsFontsVO));
					} else {
						rowList.add(new StyledData(space(1), rowsFontsVO));
					}
					dataList.add(rowList);

					ArrayList dataListForMiddleRows = new ArrayList();

					rowList = new ArrayList();
					rowList.add(new StyledData(space(0) + "6. Post Cadre",
							rowsFontsVO));
					if (rs.getString("CADRE") != null) {
						rowList.add(new StyledData(space(4) + ":" + space(1)
								+ rs.getString("CADRE"), rowsFontsVO));
					} else {
						rowList.add(new StyledData(space(4) + ":" + space(1),
								rowsFontsVO));
					}
					if (rs.getString("EMP_GROUP") != null) {
						rowList.add(new StyledData("Group:" + space(1)
								+ rs.getString("EMP_GROUP"), rowsFontsVO));
					} else {
						rowList.add(new StyledData("Group:" + space(1),
								rowsFontsVO));
					}
					dataListForMiddleRows.add(rowList);

					rowList = new ArrayList();
					rowList.add(new StyledData(space(0)
							+ "7. Designation and Payscale", rowsFontsVO));
					if (rs.getString("DESIGNATION") != null) {
						rowList.add(new StyledData(space(4) + ":" + space(1)
								+ rs.getString("DESIGNATION"), rowsFontsVO));
					} else {
						rowList.add(new StyledData(space(4) + ":" + space(1),
								rowsFontsVO));
					}
					if (rs.getString("PAYCOMMISSION").toString().equals(
							"700015")) {
						if (rs.getString("PAYSCALEFIFTHPC") != null) {
							rowList.add(new StyledData("PayScale:" + space(1)
									+ rs.getString("PAYSCALEFIFTHPC"),
									rowsFontsVO));
						} else {
							rowList.add(new StyledData("PayScale:" + space(1),
									rowsFontsVO));
						}
					}
					if (rs.getString("PAYCOMMISSION").toString().equals(
							"700016")) {
						if (rs.getString("PAYSCALESIXTHPC") != null) {
							rowList.add(new StyledData("PayScale:" + space(1)
									+ rs.getString("PAYSCALESIXTHPC"),
									rowsFontsVO));
						} else {
							rowList.add(new StyledData("PayScale:" + space(1),
									rowsFontsVO));
						}
					}
					if (rs.getString("PAYCOMMISSION").toString().equals(
							"700016")) {
						if (rs.getString("PAYSCALESIXTHPC") != null) {
							rowList.add(new StyledData("PayScale:" + space(1)
									+ rs.getString("PAYSCALESIXTHPC"),
									rowsFontsVO));
						} else {
							rowList.add(new StyledData("PayScale:" + space(1),
									rowsFontsVO));
						}
					}
					
					// **************** added by amit bhattad
					if (rs.getString("PAYCOMMISSION").toString().equals(
							"700338")) {
						if (rs.getString("PAYSCALESIXTHPC") != null) {
							rowList.add(new StyledData("PayScale:" + space(1)
									+ rs.getString("PAYSCALESIXTHPC"),
									rowsFontsVO));
						} else {
							rowList.add(new StyledData("PayScale:" + space(1),
									rowsFontsVO));
						}
					}
					if (rs.getString("PAYCOMMISSION").toString().equals(
							"700339")) {
						if (rs.getString("PAYSCALESIXTHPC") != null) {
							rowList.add(new StyledData("PayScale:" + space(1)
									+ rs.getString("PAYSCALESIXTHPC"),
									rowsFontsVO));
						} else {
							rowList.add(new StyledData("PayScale:" + space(1),
									rowsFontsVO));
						}
					}
					
					if (rs.getString("PAYCOMMISSION").toString().equals(
							"700340")) {
						if (rs.getString("PAYSCALESIXTHPC") != null) {
							rowList.add(new StyledData("PayScale:" + space(1)
									+ rs.getString("PAYSCALESIXTHPC"),
									rowsFontsVO));
						} else {
							rowList.add(new StyledData("PayScale:" + space(1),
									rowsFontsVO));
						}
					}

					
					if (rs.getString("PAYCOMMISSION").toString().equals(
							"700345")) {
						if (rs.getString("PAYSCALESIXTHPC") != null) {
							rowList.add(new StyledData("PayScale:" + space(1)
									+ rs.getString("PAYSCALESIXTHPC"),
									rowsFontsVO));
						} else {
							rowList.add(new StyledData("PayScale:" + space(1),
									rowsFontsVO));
						}
					}
				// *************************end by amit bhattad

					dataListForMiddleRows.add(rowList);

					rowList = new ArrayList();
					rowList.add(new StyledData(space(0)
							+ "8. Residential Addr. with Phone No.",
							rowsFontsVO));
					if (rs.getString("ResADDRESS1") != null) {
						rowList.add(new StyledData(space(4) + ":" + space(1)
								+ rs.getString("ResADDRESS1"), rowsFontsVO));
					} else {
						rowList.add(new StyledData(space(4) + ":" + space(1),
								rowsFontsVO));
					}
					if (rs.getString("CNTCT_NO") != null) {
						rowList.add(new StyledData("Phone:" + space(1)
								+ rs.getString("CNTCT_NO"), rowsFontsVO));
					} else {
						rowList.add(new StyledData("Phone:" + space(1),
								rowsFontsVO));
					}
					dataListForMiddleRows.add(rowList);

					rowList = new ArrayList();
					rowList.add("");
					if (rs.getString("ResADDRESS2") != null) {
						rowList.add(new StyledData(space(4) + ":" + space(1)
								+ rs.getString("ResADDRESS2"), rowsFontsVO));
					} else {
						rowList
								.add(new StyledData(space(4) + ":", rowsFontsVO));
					}
					rowList.add("");
					dataListForMiddleRows.add(rowList);

					td4 = new TabularData(dataListForMiddleRows);

					RptVo = reportsDao.getReport("700005", report.getLangId(),
							report.getLocId()); // A
					// 3-Column
					// Report
					(td4).setRelatedReport(RptVo);
					(td4).setStyles(noBorder);

					rowList = new ArrayList();
					rowList.add(td4);
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList
							.add(new StyledData(
									space(1)
											+ "9. a) Whether previously working in Government office or any other Organization / Institution to which New D.C.P.S is applicable.",
									rowsFontsVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList
							.add(new StyledData(
									space(4)
											+ " b) If so, the Pension Account Number Allocated",
									rowsFontsVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList.add(new StyledData(space(0)
							+ "10. Details of Nominees", rowsFontsVO));
					dataList.add(rowList);

					// added

					NewRegDdoDAO dcpsRegisNomineeDAO = new NewRegDdoDAOImpl(
							MstEmp.class, serviceLocator.getSessionFactory());

					List<MstEmpNmn> lObjEmpNmnDataList = dcpsRegisNomineeDAO
							.getNominees(empId);

					Iterator<MstEmpNmn> itr = lObjEmpNmnDataList.iterator();
					int count = 1;

					ArrayList dataListForNominee = new ArrayList();

					if (lObjEmpNmnDataList.size() != 0) {
						while (itr.hasNext()) {

							MstEmpNmn vo1 = new MstEmpNmn();
							vo1 = itr.next();

							rowList = new ArrayList();
							rowList.add(new StyledData(count, CenterAlignVO));
							rowList.add(new StyledData(vo1.getName(),
									CenterAlignVO));

							rowList.add(new StyledData(calculateAge(vo1
									.getDob()), CenterAlignVO));
							rowList.add(new StyledData(vo1.getDob(),
									CenterAlignVO));
							rowList.add(new StyledData(vo1.getShare(),
									CenterAlignVO));
							rowList.add(new StyledData(vo1.getRlt(),
									CenterAlignVO));
							count++;

							dataListForNominee.add(rowList);
						}

						td5 = new TabularData(dataListForNominee);

						RptVo = reportsDao.getReport("700006", report
								.getLangId(), report.getLocId()); // A
						// 6-Column
						// Report
						(td5).setRelatedReport(RptVo);

						rowList = new ArrayList();
						rowList.add(td5);
						dataList.add(rowList);

					} else {
						rowList = new ArrayList();
						rowList.add(new StyledData(
								"Employee has not added any Nominees yet.",
								CenterAlignVO));
						dataList.add(rowList);
					}

					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);

					String shriOrSmt = "";
					if (rs.getString("SALUTATION").equals("700075")) {
						shriOrSmt = "Shri.";
					}
					if (rs.getString("SALUTATION").equals("700076")) {
						shriOrSmt = "Smt.";
					}
					if (rs.getString("SALUTATION").equals("700077")) {
						shriOrSmt = "Kum.";
					}

					rowList = new ArrayList();
					rowList
							.add(new StyledData(
									space(0)
											+ "11. I, "
											+ shriOrSmt
											+ space(1)
											+ rs.getString("Name")
											+ space(1)
											+ "am aware that till the Central Record Keeping Agency is appointed by the Central Government,"
											+ " any action/decision taken by the State Record Kepping Agency in consultation with government, will be binding on me. I also understand"
											+ " that after appointment of Central Record Keeping Agency, the total amount standing to my credit at that will be transferred to the said Agency",
									rowsFontsVOForPara));
					dataList.add(rowList);

					// Code to get DDO's district
					String lStrPlace = null;
					String lStrDdoCode = rs.getString("DDO_CODE");
					if (lStrDdoCode != null && !"".equals(lStrDdoCode)) {
						lStrPlace = dcpsRegisNomineeDAO
								.getDistrictForDDO(lStrDdoCode);
					} else {
						lStrPlace = "";
					}

					rowList = new ArrayList();
					rowList.add(new StyledData(space(1) + "Place - " + space(1)
							+ lStrPlace, rowsFontsVO));
					dataList.add(rowList);

					Date lcurrDate = SessionHelper.getCurDate();
					SimpleDateFormat lObjDateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					lObjDateFormat.format(lcurrDate);

					rowList = new ArrayList();
					rowList.add(new StyledData(space(1) + "Date - "
							+ lObjDateFormat.format(lcurrDate), rowsFontsVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList.add(new StyledData("Signature of Employee",
							RightAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList.add(new StyledData("To Be Furnished By DDO",
							baseFont4));
					dataList.add(rowList);
					
					String lStrOfficeName = "";
					if (rs.getString("OFF_NAME") != null)
					{
						lStrOfficeName = rs.getString("OFF_NAME");
					}

					rowList = new ArrayList();
					rowList
							.add(new StyledData(
									space(1)
											+ "Certified that "
											+ shriOrSmt
											+ rs.getString("Name")
											+ space(1)
											+ "has been appointed in " + lStrOfficeName + " ."
											+ "The Particulars given above are correct. I have also ascertained that he/she has not worked in government office or Zilla Parishad or aided secondary educational institution or"
											+ "non-agricultural university / college affiliated thereto or agricultural university or in any organization or institution to which the New Defined"
											+ "Contribution Pension scheme is applicable and that he/she has not been alloted the Pension Account Number previously.",
									rowsFontsVOForPara));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);
					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList
							.add(new StyledData("Signature Of DDO",
									RightAlignVO));
					dataList.add(rowList);

					SessionHelper.getCurDate();
					SimpleDateFormat myDate = (SimpleDateFormat) DateFormat
							.getDateTimeInstance();

					rowList = new ArrayList();
					rowList.add(new StyledData(space(1) + "Verified On"
							+ space(1)
							+ myDate.format(DBUtility.getCurrentDateFromDB()),
							LeftAlignVO));
					dataList.add(rowList);
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

	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	public int calculateAge(Date dob) {

		SimpleDateFormat simpleDateFormatObj = new SimpleDateFormat("yyyy");
		int age;
		int birthYear = Integer.parseInt(simpleDateFormatObj.format(dob));
		Date gDtCurrDt = DBUtility.getCurrentDateFromDB();
		String currYearStr = simpleDateFormatObj.format(gDtCurrDt);
		int currYear = Integer.parseInt(currYearStr);
		age = currYear - birthYear;
		return age;
	}

}
