/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 12, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;

//com.tcs.sgv.pensionpay.report.GenerateMandateReport
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;
import com.tcs.sgv.pensionpay.valueobject.TrnEcsDtl;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 May 12, 2011
 */
public class GenerateMandateReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(GenerateMandateReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	private PensionpayQueryDAO gObjRptQueryDAO = null;
	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	ResourceBundle bundleConst = ResourceBundle.getBundle("resources/pensionpay/PensionLabels");

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		gLogger.info("inside reports function");
		gLogger.info("***********************Outward Register****************************************");

		String langId = report.getLangId();
		gLogger.info("\n\n LangId is " + langId);

		String locId = report.getLocId();
		gLogger.info("\n\n LocationId is " + locId);

		Connection con = null;
		Long lLngTotalAmount = null;
		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		ArrayList dataList = new ArrayList();
		ArrayList rowList = null;
		ArrayList tr = null;
		ArrayList td = null;
		ArrayList rptList1 = null;
		TabularData rptTd = null;
		ReportVO RptVo = null;
		ReportsDAO reportsDao = new ReportsDAOImpl();
		ReportColumnVO[] RptCol = null;
		// for Right Alignment format
		StyleVO[] RightAlignVO = new StyleVO[3];
		RightAlignVO[0] = new StyleVO();
		RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		RightAlignVO[1] = new StyleVO();
		RightAlignVO[1].setStyleId(IReportConstants.BORDER);
		RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		RightAlignVO[2] = new StyleVO();
		RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		RightAlignVO[2].setStyleValue("14");
		// for Left Alignment format
		StyleVO[] LeftAlignVO = new StyleVO[3];
		LeftAlignVO[0] = new StyleVO();
		LeftAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		LeftAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		LeftAlignVO[1] = new StyleVO();
		LeftAlignVO[1].setStyleId(IReportConstants.BORDER);
		LeftAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		LeftAlignVO[2] = new StyleVO();
		LeftAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		LeftAlignVO[2].setStyleValue("14");

		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] noPaginationBorderAndSelfClose = new StyleVO[3];
		noPaginationBorderAndSelfClose[0] = new StyleVO();
		noPaginationBorderAndSelfClose[0].setStyleId(IReportConstants.BORDER);
		noPaginationBorderAndSelfClose[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		noPaginationBorderAndSelfClose[1] = new StyleVO();
		noPaginationBorderAndSelfClose[1].setStyleId(26);
		noPaginationBorderAndSelfClose[1].setStyleValue("JavaScript:self.close();");
		noPaginationBorderAndSelfClose[2] = new StyleVO();
		noPaginationBorderAndSelfClose[2].setStyleId(IReportConstants.REPORT_PAGINATION);
		noPaginationBorderAndSelfClose[2].setStyleValue("NO");

		StyleVO[] baseFont3 = new StyleVO[3];
		baseFont3[0] = new StyleVO();
		baseFont3[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		baseFont3[0].setStyleValue("14");
		baseFont3[1] = new StyleVO();
		baseFont3[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		baseFont3[1].setStyleValue(IReportConstants.VALUE_FONT_STYLE_NORMAL);
		baseFont3[2] = new StyleVO();
		baseFont3[2].setStyleId(IReportConstants.BORDER);
		baseFont3[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] baseFont2 = new StyleVO[4];
		baseFont2[0] = new StyleVO();
		baseFont2[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		baseFont2[0].setStyleValue("18");
		baseFont2[1] = new StyleVO();
		baseFont2[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		baseFont2[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		baseFont2[2] = new StyleVO();
		baseFont2[2].setStyleId(IReportConstants.BORDER);
		baseFont2[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		baseFont2[3] = new StyleVO();
		baseFont2[3].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		baseFont2[3].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);

		String lStrNewLine = StringUtils.getLineSeparator();
		String lStrDesignationName = null;
		String lStrPlace = "";
		// System.out.println("The new linw is  " + lStrNewLine);
		requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
		lObjSessionFactory = serviceLocator.getSessionFactorySlave();

		try {

			con = lObjSessionFactory.getCurrentSession().connection();

			gLogger.info("***********************11****************************************");
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			gLogger.info("Logindetails contents of map :" + loginDetail);
			gLogger.info("***********************loginDetail****************************************" + loginDetail);
			// LoginDetails objLoginDetails =
			// (LoginDetails)loginDetail.get("baseLoginVO");
			SimpleDateFormat lObjSimpleFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date lcurrDate = SessionHelper.getCurDate();
			String lStrCurrDate = lObjSimpleFormat.format(lcurrDate);

			Long locationId = (Long) loginDetail.get("locationId");
			gLogger.info("Location id from Map :" + locationId);

			// Date lStrCurrDate = lObjSimpleFormat.parse((String) loginDetail
			// .get("currLoginDateTime"));

			OrgDesignationMst lObjOrgDesignationMst = (OrgDesignationMst) loginDetail.get("designationVO");
			if (lObjOrgDesignationMst != null) {
				lStrDesignationName = lObjOrgDesignationMst.getDsgnName();
				// System.out.println("The designation is " +
				// lObjOrgDesignationMst.getDsgnName());
			}
			if (lStrDesignationName == null) {
				lStrDesignationName = "";
			}
			String lStrEmpName = (String) loginDetail.get("empPrefix") + space(1) + (String) loginDetail.get("empFname") + space(1) + (String) loginDetail.get("empLname");

			StringBuffer sql = new StringBuffer();
			String StrSqlQuery = "";
			gLogger.info("***********************22****************************************");
			String ecsCode = (String) report.getParameterValue("ecsCode");

			if (report.getReportCode().equals("365450")) {

				report.setStyleList(noPaginationBorderAndSelfClose);
				rowList = new ArrayList();

				rowList.add(new StyledData("Mandate Serial No." + getMandateSerialNo(ecsCode) + " dt." + lStrCurrDate + lStrNewLine + lStrNewLine, RightAlignVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(lStrNewLine);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(lStrNewLine);
				dataList.add(rowList);

				String lStrLangId = "1";
				smt = con.createStatement();
				sql = new StringBuffer("SELECT clm.loc_name as LOC_NAME,clm.loc_addr_1 as LOC_ADDR1,clm.loc_addr_2 as LOC_ADDR2,ccm.city_name as CITY,clm.loc_pin as PIN_CODE "
						+ "FROM cmn_location_mst clm ,cmn_city_mst ccm  " + "WHERE clm.loc_city_id=ccm.city_id  " + "AND clm.loc_id=" + locationId + "" + " AND ccm.lang_id=" + lStrLangId);

				StrSqlQuery = sql.toString();
				rs = smt.executeQuery(StrSqlQuery);

				tr = new ArrayList();
				td = new ArrayList();
				String lStrAddr = "From: " + lStrNewLine;
				while (rs.next()) {
					lStrPlace = rs.getString("CITY");
					String lStrLocName = rs.getString("LOC_NAME");
					String lStrLocAddr1 = rs.getString("LOC_ADDR1");
					String lStrLocAddr2 = rs.getString("LOC_ADDR2");
					String lStrPinCode = rs.getString("PIN_CODE");

					if (lStrLocName != null) {
						lStrAddr += lStrLocName + "," + lStrNewLine;
					}

					if (lStrLocAddr1 != null) {
						lStrAddr += lStrLocAddr1 + "," + lStrNewLine;
					}
					if (lStrLocAddr2 != null) {
						lStrAddr += lStrLocAddr2 + "," + lStrNewLine;
					}
					if (lStrPlace != null) {
						lStrAddr += lStrPlace + "-";
					}
					if (lStrPinCode != null) {
						lStrAddr += lStrPinCode + lStrNewLine;
					}

				}
				td.add(new StyledData(lStrAddr, baseFont3));
				td.add(new StyledData("To," + lStrNewLine + "The Assistant General Manager " + lStrNewLine + "Public Accounts Department " + lStrNewLine + "Reserve Bank of India " + lStrNewLine
						+ "Mumbai Regional Office " + lStrNewLine + "Shahid Bhagatsingh Road,Fort, " + lStrNewLine + "Mumbai-400 051", baseFont3));

				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("365451", report.getLangId(), report.getLocId());
				RptCol = RptVo.getReportColumns();
				RptCol[0].setHidden("Y");
				RptVo.setReportColumns(RptCol);
				(rptTd).setRelatedReport(RptVo);
				(rptTd).setStyles(noBorder);
				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);

				rowList = new ArrayList();
				rowList.add(lStrNewLine);
				dataList.add(rowList);

				rowList = new ArrayList();

				rowList.add(new StyledData(space(10) + "Mandate for Debiting Government Account", baseFont2));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(lStrNewLine);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(lStrNewLine);
				dataList.add(rowList);

				rowList = new ArrayList();

				rowList.add(new StyledData(space(10) + "I hereby, authorize Reserve Bank of India to Debit our" + "Account No. <b> 6112512026 </b> maintained with RBI,PAD, Mumbai office for setting"
						+ " ECS/N.EFT transactions generated by us as detailed below:", LeftAlignVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(lStrNewLine);
				dataList.add(rowList);

				// Details--------5
				// Row 1 starts
				tr = new ArrayList();
				td = new ArrayList();

				td.add(new StyledData("1.", RightAlignVO));
				td.add(new StyledData("File Name", baseFont3));
				sql = new StringBuffer("SELECT ted.ecs_code ECS_CODE,mpg.org_file_name FILE_NAME " + "FROM trn_ecs_dtl ted,cmn_attdoc_mst doc,cmn_attachment_mpg mpg "
						+ "WHERE ted.ecs_attachment_id=doc.attdoc_id " + "AND mpg.sr_no = doc.sr_no " + "AND ted.ecs_code=" + ecsCode);

				StrSqlQuery = sql.toString();
				rs = smt.executeQuery(StrSqlQuery);
				String lStrFileName = "";
				while (rs.next()) {
					if (rs.getString("ECS_CODE") != null) {
						lStrFileName = rs.getString("ECS_CODE") + ".txt";
					}

				}
				td.add(new StyledData(":" + lStrFileName, baseFont3));
				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("365451", report.getLangId(), report.getLocId());

				(rptTd).setRelatedReport(RptVo);
				(rptTd).setStyles(noBorder);
				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);
				// Row 1 ends

				// Row 2 Starts
				tr = new ArrayList();
				td = new ArrayList();

				td.add(new StyledData("2.", RightAlignVO));
				td.add(new StyledData("Total Transaction Amount", baseFont3));

				sql = new StringBuffer("SELECT tcd.cheque_amt CHEQUE_AMT " + "FROM trn_cheque_dtls tcd" + " WHERE tcd.advice_no =" + ecsCode);

				StrSqlQuery = sql.toString();
				rs = smt.executeQuery(StrSqlQuery);
				while (rs.next()) {
					lLngTotalAmount = rs.getLong(("CHEQUE_AMT"));

				}
				td.add(new StyledData(":Rs," + lLngTotalAmount + "/-", baseFont3));
				String lStrTotAmtInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(lLngTotalAmount));
				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("365451", report.getLangId(), report.getLocId());

				(rptTd).setRelatedReport(RptVo);
				(rptTd).setStyles(noBorder);
				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);
				// Row 2 Ends
				// Middle Row starts
				tr = new ArrayList();
				td = new ArrayList();

				td.add(space(20));
				td.add(new StyledData("<b>(Rupees, " + lStrTotAmtInWords + " Only)</b>", baseFont3));

				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("365451", report.getLangId(), report.getLocId());
				RptCol = RptVo.getReportColumns();
				RptCol[0].setHidden("Y");
				RptVo.setReportColumns(RptCol);
				(rptTd).setRelatedReport(RptVo);
				(rptTd).setStyles(noBorder);
				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);
				// Middle Row ends

				// Row 3 Starts
				tr = new ArrayList();
				td = new ArrayList();

				td.add(new StyledData("3.", RightAlignVO));
				td.add(new StyledData("Total Number of Transactions", baseFont3));

				sql = new StringBuffer("SELECT COUNT(rbc.bill_cheque_id) TOT_COUNT " + "FROM trn_cheque_dtls tcd,rlt_bill_cheque rbc " + "WHERE tcd.cheque_id=rbc.cheque_id " + "AND tcd.advice_no ="
						+ ecsCode);

				StrSqlQuery = sql.toString();
				rs = smt.executeQuery(StrSqlQuery);
				String lStrTotCount = "";
				while (rs.next()) {
					lStrTotCount = rs.getString("TOT_COUNT");
					if (lStrTotCount == null) {
						lStrTotCount = "";
					}

				}
				td.add(new StyledData(":" + lStrTotCount, baseFont3));
				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("365451", report.getLangId(), report.getLocId());

				(rptTd).setRelatedReport(RptVo);
				(rptTd).setStyles(noBorder);
				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);
				// Row 3 Ends

				// Row 4 Starts
				tr = new ArrayList();
				td = new ArrayList();

				td.add(new StyledData("4.", RightAlignVO));
				td.add(new StyledData("Date of settlement", baseFont3));

				sql = new StringBuffer("SELECT authority_date AUTH_DATE " + "FROM trn_cheque_dtls " + "WHERE advice_no = " + ecsCode);

				StrSqlQuery = sql.toString();
				rs = smt.executeQuery(StrSqlQuery);
				String lStrAuthDate = "";
				while (rs.next()) {
					lStrAuthDate = lObjSimpleFormat.format(rs.getDate("AUTH_DATE"));
					if (lStrAuthDate == null) {
						lStrAuthDate = "";
					}

				}
				td.add(new StyledData(":" + lStrAuthDate, baseFont3));
				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("365451", report.getLangId(), report.getLocId());

				(rptTd).setRelatedReport(RptVo);
				(rptTd).setStyles(noBorder);
				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);
				// Row 4 Ends

				rowList = new ArrayList();
				rowList.add(lStrNewLine);
				dataList.add(rowList);

				rowList = new ArrayList();

				rowList.add(new StyledData("The input file name and settlement date may be amended by RBI as per their convinence and our confirmation thereon.", LeftAlignVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(lStrNewLine);
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(lStrNewLine);
				dataList.add(rowList);
				// Last report
				tr = new ArrayList();
				td = new ArrayList();

				td.add(new StyledData("Date:-" + lStrCurrDate + lStrNewLine + lStrNewLine + "Place:-" + lStrPlace, baseFont3));

				td.add(new StyledData("Authorized Signatory" + lStrNewLine + "Name:-" + lStrEmpName + lStrNewLine + "Designation:-" + lStrDesignationName, baseFont3));

				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("365451", report.getLangId(), report.getLocId());
				RptCol = RptVo.getReportColumns();
				RptCol[0].setHidden("Y");
				RptVo.setReportColumns(RptCol);

				(rptTd).setRelatedReport(RptVo);
				(rptTd).setStyles(noBorder);
				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);

			}

		} catch (Exception e) {
			// e.printStackTrace();
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
				// e1.printStackTrace();
				gLogger.error("Exception :" + e1, e1);

			}
		}
		gLogger.info("DATALIST SIZE IS" + dataList.size());

		return dataList;
	}

	public String space(int noOfSpace) {

		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	public String getMandateSerialNo(String ecsCode) {

		serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
		gObjRptQueryDAO = new PensionpayQueryDAOImpl(TrnEcsDtl.class, serviceLocator.getSessionFactory());
		String lStrMandateNo = null;
		BigDecimal lBgdcMandateNo = null;
		Calendar lObjCalendar = Calendar.getInstance();
		// Integer lIntCurrentMonth = lObjCalendar.get(Calendar.MONTH) + 1;
		// Integer lIntCurrentDate = lObjCalendar.get(Calendar.DATE);
		String lIntCurrentYear = String.valueOf(lObjCalendar.get(Calendar.YEAR));
		String lIntNextYear = String.valueOf(lObjCalendar.get(Calendar.YEAR) + 1);

		try {
			lBgdcMandateNo = gObjRptQueryDAO.getMandateSerialNo();
			lStrMandateNo = lBgdcMandateNo.toString() + "/" + lIntCurrentYear + "-" + lIntNextYear;
			// if (lIntCurrentMonth == 12 && lIntCurrentDate == 31) {
			// lObjCommonReportDAO.resetMandateSerialNo();
			// }

			gObjRptQueryDAO.setMandateSerialNo(ecsCode, lBgdcMandateNo);

		} catch (Exception ex) {
			// ex.printStackTrace();
			gLogger.error("Exception :" + ex, ex);

		}
		return lStrMandateNo;
	}
}
