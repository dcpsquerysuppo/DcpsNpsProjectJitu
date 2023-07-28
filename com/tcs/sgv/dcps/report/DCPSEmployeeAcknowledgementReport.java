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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.core.service.ServiceLocator;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Feb 9, 2011
 */
public class DCPSEmployeeAcknowledgementReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger
			.getLogger(DCPSEmployeeAcknowledgementReport.class);
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

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		String empId = null;
		ArrayList dataList = new ArrayList();

		
	
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

		StyleVO[] LeftAlignSmallVO = new StyleVO[2];
		LeftAlignSmallVO[0] = new StyleVO();
		LeftAlignSmallVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		LeftAlignSmallVO[0]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		LeftAlignSmallVO[1] = new StyleVO();
		LeftAlignSmallVO[1].setStyleId(IReportConstants.BORDER);
		LeftAlignSmallVO[1]
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

		StyleVO[] rowsFontsVO = new StyleVO[1];
		rowsFontsVO[0] = new StyleVO();
		rowsFontsVO[0].setStyleId(IReportConstants.BORDER);
		rowsFontsVO[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] noBorderAndSelfClose = new StyleVO[2];
		noBorderAndSelfClose[0] = new StyleVO();
		noBorderAndSelfClose[0].setStyleId(IReportConstants.BORDER);
		noBorderAndSelfClose[0]
				.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		noBorderAndSelfClose[1] = new StyleVO();
		noBorderAndSelfClose[1].setStyleId(26);
		noBorderAndSelfClose[1].setStyleValue("JavaScript:self.close()");
		
		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

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

			Long locationId = (Long) loginDetail.get("locationId");

			StringBuffer sql = new StringBuffer();
			String StrSqlQuery = "";

			if (report.getReportCode().equals("700007") || report.getReportCode().equals("700087")) {

				if(report.getReportCode().equals("700007")){
					report.setStyleList(noBorderAndSelfClose);
				}
				if(report.getReportCode().equals("700087")){
					report.setStyleList(noBorder);
				}

				ArrayList rowList = new ArrayList();

				rowList
						.add(new StyledData("<b>" + "<font size=\"5px\"> "
								+ "DCPS Acknowledgement" + "</font></b>",
								CenterAlignVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				
				/*
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				*/

				empId = (String) report.getParameterValue("empid");

				sql = new StringBuffer("select DCPS_ID as dcpsId,");
				sql.append(" EMP_NAME as Name,")	;	
				sql.append(" REG_STATUS_UPDTD_DATE as verifiedDate,");
				sql.append(" SEVARTH_ID as sevarthId");
				sql.append(" from MST_DCPS_EMP where DCPS_EMP_ID='" + empId	+ "'");
				
				StrSqlQuery = sql.toString();
				rs = smt.executeQuery(StrSqlQuery);

				while (rs.next()) {

					rowList = new ArrayList();
					rowList.add(new StyledData(
							"The application submitted by Shri./Smt" + space(4)
									+ "<b>" + rs.getString("Name") + "</b>",
							LeftAlignSmallVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList
							.add(new StyledData(
									"has been accepted and the following Pension Account Number is alloted to him/her",
									rowsFontsVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList.add(new StyledData("Pension Account Number :-",
							LeftAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);
					
					/*
					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);
					*/

					rowList = new ArrayList();
					rowList.add(new StyledData("<b>" + "<font size=\"5px\"> "
							+ rs.getString("dcpsId") + "</font></b>",
							CenterAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);
					
					rowList = new ArrayList();
					rowList.add(new StyledData("Sevaarth Id :-",
							LeftAlignVO));
					dataList.add(rowList);
					
					rowList = new ArrayList();
					rowList.add(new StyledData("<b>" + "<font size=\"5px\"> "
							+ rs.getString("sevarthId") + "</font></b>",
							CenterAlignVO));
					dataList.add(rowList);
					
					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);
					
					/*
					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);
					
					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);
					*/

					rowList = new ArrayList();
					rowList
							.add(new StyledData(
									"Signature of the Authorised Officer",
									RightAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList
							.add(new StyledData("(with full name)",
									RightAlignVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList
							.add(new StyledData(
									"(N.B. :- One copy should be retained by S.R.K.A. The second copy should be pasted to the service book of ",
									rowsFontsVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList
							.add(new StyledData(
									"the employee the third copy should be kept in personal file of the employee by the D.D.O.)",
									rowsFontsVO));
					dataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("" + space(1));
					dataList.add(rowList);

					SimpleDateFormat myDate = (SimpleDateFormat) DateFormat
							.getDateTimeInstance();
					SimpleDateFormat lObjDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

					rowList = new ArrayList();
					rowList.add("Approved/Verified On :"
							+ space(3)
							+ myDate.format(lObjDateFormat1.parse(rs
									.getString("verifiedDate"))));
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

}
