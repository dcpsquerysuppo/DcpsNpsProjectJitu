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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import java.util.ResourceBundle;
/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Feb 9, 2011
 */
public class MatchedContriEntriesReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger
	.getLogger(DCPSEmployeeAcknowledgementReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;

	SessionFactory lObjSessionFactory = null;

	ServiceLocator serviceLocator = null;

	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException {


	  String locId = report.getLocId();

		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		ArrayList dataList = new ArrayList();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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

		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		
		
		StyleVO[] leftboldStyleVO  = new StyleVO[3];
		leftboldStyleVO[0] = new StyleVO();
		leftboldStyleVO[0]
		                .setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		leftboldStyleVO[0]
		                .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_NORMAL);
		leftboldStyleVO[1] = new StyleVO();
		leftboldStyleVO[1]
		                .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		leftboldStyleVO[1].setStyleValue("Left");
		leftboldStyleVO[2] = new StyleVO();
		leftboldStyleVO[2]
		                .setStyleId(IReportConstants.BORDER);
		leftboldStyleVO[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		
		
		
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
			
			ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/dcpsLabels");
			

			SessionFactory lObjSessionFactory = serviceLocator
			.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria)
			.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			Long locationId = (Long) loginDetail.get("locationId");

			String StrSqlQuery = "";

			if (report.getReportCode().equals("700030")) {

				// report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				Long treasuryCode = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				String fromDate = ((String) report.getParameterValue("fromDate"));
                String toDate = ((String) report.getParameterValue("toDate"));
				String yearDesc = ((String) report.getParameterValue("yearDesc"));
				Long yearId = Long.valueOf((String) report.getParameterValue("yearId"));
				Long acmaintBy = Long.valueOf((String) report.getParameterValue("acmaintBy"));
				String lStrDDOCode = null;
				String lStrDDOName = "";
				Long lLongRltContriVoucherId = 0l;
                int status=1;
				String MatchedOrUnMatched = (String) report.getParameterValue("MatchedOrUnMatched");
				String ddoCode = (String) report.getParameterValue("cmbDDO");
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
				String url = "";

				if (MatchedOrUnMatched.equals("Matched")) {
					url = "ifms.htm?actionFlag=loadMatchContriEntryForm&yearId="
						+ yearId
						+ "&fromDate="
						+ fromDate
						+ "&toDate="
						+ toDate
					    + "&acmaintBy="+acmaintBy;
				}
				if (MatchedOrUnMatched.equals("UnMatched")) {
					url = "ifms.htm?actionFlag=loadUnMatchContriEntryForm&yearId="
						+ yearId
						+ "&fromDate="
						+ fromDate
						+ "&toDate="
						+ toDate;
				}

				StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
				lObjStyleVO = report.getStyleList();
				for (Integer lInt = 0; lInt < report.getStyleList().length; lInt++) {
					if (lObjStyleVO[lInt].getStyleId() == 26) {
						lObjStyleVO[lInt].setStyleValue(url);
					}
				}

				report.setAdditionalHeader("**Note : Amount mismatch in  Treasury Side and sevaarth side due to 2 reasons-\r\n 1. Removal of tier 2 contribution provided through tier1.\r\n 2. Treasury net data can be  missing  for manually matched record.");
			
				
				String fromDateQuery = sdf2.format(sdf1.parse(fromDate));
				String toDateQuery = sdf2.format(sdf1.parse(toDate));

				StringBuilder SBQuery = new StringBuilder();

			
				if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
				{
					gLogger.info("Inside if");
					status=2;
				}

				if (MatchedOrUnMatched.equals("Matched")) {
					CallableStatement lClbStmnt = null;
			/*		SBQuery.append(" SELECT CV.treasury_code,CV.voucher_no,CV.voucher_date,CV.voucher_amount,CV.SCHEME_CODE,TN.bill_no, TN.bill_date,");
					SBQuery.append(" TN.voucher_no,TN.voucher_date,trim(TN.from_scheme),TN.dcps_amount,CV.ddo_code,CV.status AS VCStatus1 ,'' AS VCStatus2 ,CV.manually_matched,OD.ddo_name,CV.MST_DCPS_CONTRI_VOUCHER_DTLS ");
					SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_treasurynet_data TN, sgvc_fin_year_mst FY,org_ddo_mst OD  ");
					SBQuery.append(" WHERE ");
					SBQuery.append(" CV.treasury_code = TN.treasury_code AND CV.ddo_code = TN.ddo_code AND trim(CV.scheme_code) = trim(TN.FROM_SCHEME) AND CV.voucher_no = TN.voucher_no AND CV.voucher_amount = TN.dcps_amount AND CV.voucher_date=TN.voucher_date ");
					SBQuery.append(" AND FY.fin_year_desc = TN.year_desc ");
					SBQuery.append(" AND FY.fin_year_id = CV.year_id");
					SBQuery.append(" AND OD.ddo_code = CV.ddo_code");
					SBQuery.append(" AND CV.treasury_code = '" + treasuryCode+ "'");
					SBQuery.append(" AND CV.voucher_date BETWEEN '"+ fromDateQuery + "' AND '" + toDateQuery + "'");
					SBQuery.append(" AND CV.STATUS = 'F'");
					if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
						SBQuery.append(" AND OD.ddo_code = '"+ddoCode+"'");*/

					//SBQuery.append(" GROUP BY CV.voucher_no");
					
					SBQuery.append("{call getMatchReportData(?,?,?,?,?,?)}");
					lClbStmnt = con.prepareCall(SBQuery.toString());
					lClbStmnt.setString(1,ddoCode);
				    lClbStmnt.setString(2,fromDateQuery);
					lClbStmnt.setString(3,toDateQuery);
					lClbStmnt.setLong(4,treasuryCode);
					lClbStmnt.setLong(5,status);
					lClbStmnt.setLong(6,acmaintBy);
					rs= lClbStmnt.executeQuery();

				}
		/*		if (MatchedOrUnMatched.equals("UnMatched")) {

					SBQuery.append(" SELECT CV.treasury_code, cv.voucher_no, cv.voucher_date,cv.voucher_amount,CV.SCHEME_CODE,TN.bill_no,TN.bill_date,");
					SBQuery.append(" tn.voucher_no,tn.voucher_date,trim(TN.from_scheme),tn.dcps_amount,CV.ddo_code ,CV.STATUS AS VCStatus1 ,'' AS VCStatus2,'' AS VCStatus2,OD.ddo_name,CV.MST_DCPS_CONTRI_VOUCHER_DTLS  ");
					SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV");
					SBQuery.append(" JOIN sgvc_fin_year_mst FY on CV.YEAR_ID = FY.FIN_YEAR_ID ");
					SBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON cv.voucher_no = tn.voucher_no   AND tn.voucher_no IS NULL and CV.treasury_code = tn.treasury_code AND tn.ddo_code = CV.ddo_code AND trim(CV.scheme_code) = trim(tn.FROM_SCHEME) AND FY.fin_year_desc = tn.year_desc    " );
					SBQuery.append(" JOIN ORG_DDO_MST OD on OD.ddo_code = CV.ddo_code");
					//SBQuery.append(" WHERE tn.voucher_no IS NULL AND CV.treasury_code ='"+ treasuryCode + "' ");
					SBQuery.append(" WHERE  ");
					SBQuery.append("  CV.ddo_code like '"+treasuryCode+"%' ");
					SBQuery.append(" AND CV.voucher_date BETWEEN '"+ fromDateQuery + "' AND '" + toDateQuery + "'");
					SBQuery.append(" AND CV.STATUS in ('A','B','E')");
					SBQuery.append(" AND CV.BILL_GROUP_ID is not null ");
					//SBQuery.append(" AND CV.STATUS in ('B','E')");
					SBQuery.append(" AND CV.manually_matched <> 1");
					SBQuery.append(" AND cv.voucher_status=1 ");
					if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
						SBQuery.append(" AND OD.ddo_code = '"+ddoCode+"'");

					SBQuery.append(" UNION");
					SBQuery.append(" SELECT CV.treasury_code,cv.voucher_no,cv.voucher_date,cv.voucher_amount,CV.SCHEME_CODE,TN.bill_no,TN.bill_date,");
					SBQuery.append(" tn.voucher_no, tn.voucher_date,trim(TN.from_scheme),tn.dcps_amount,tn.ddo_code ,'' AS VCStatus1,tn.STATUS AS VCStatus2,tn.STATUS AS VCStatus2,OD.ddo_name,nvl(CV.MST_DCPS_CONTRI_VOUCHER_DTLS,0)");
					SBQuery.append(" FROM mst_dcps_treasurynet_data tn ");
					SBQuery.append(" JOIN sgvc_fin_year_mst FY on tn.year_desc = FY.FIN_YEAR_DESC ");
					SBQuery.append(" LEFT JOIN mst_dcps_contri_voucher_dtls cv ON cv.voucher_no = tn.voucher_no and CV.voucher_no IS NULL and CV.treasury_code = tn.treasury_code AND tn.ddo_code = CV.ddo_code AND trim(CV.scheme_code) = trim(tn.FROM_SCHEME) AND FY.fin_year_id = CV.year_id");
					SBQuery.append(" JOIN ORG_DDO_MST OD on OD.ddo_code = tn.ddo_code");
					//SBQuery.append(" WHERE CV.voucher_no IS NULL AND tn.treasury_code ='"+ treasuryCode + "' ");
					SBQuery.append(" WHERE ");
					SBQuery.append(" AND tn.ddo_code like '"+treasuryCode+"%' ");
					SBQuery.append(" AND tn.voucher_date BETWEEN '"+ fromDateQuery + "' AND '" + toDateQuery + "'");
					SBQuery.append(" AND tn.STATUS in ('A','B','E')");
					
					if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
						SBQuery.append(" AND OD.ddo_code = '"+ddoCode+"'");

					//SBQuery.append(" AND tn.STATUS in ('B','E')");
					//SBQuery.append(" AND CV.manually_matched <> 1");

					SBQuery.append(" UNION ");
					SBQuery.append(" SELECT CV.treasury_code, cv.voucher_no, cv.voucher_date,cv.voucher_amount,CV.SCHEME_CODE,TN.bill_no, TN.bill_date,");
					SBQuery.append(" tn.voucher_no, tn.voucher_date,trim(TN.from_scheme),tn.dcps_amount,cv.ddo_code,CV.STATUS AS VCStatus1,tn.STATUS AS VCStatus2,tn.STATUS AS VCStatus2,OD.ddo_name,CV.MST_DCPS_CONTRI_VOUCHER_DTLS");
					SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV , mst_dcps_treasurynet_data tn , sgvc_fin_year_mst FY,ORG_DDO_MST OD");
					//SBQuery.append(" WHERE CV.treasury_code = tn.treasury_code AND trim(CV.scheme_code) = trim(tn.FROM_SCHEME) AND CV.voucher_no = tn.voucher_no AND CV.treasury_code = '"+ treasuryCode + "'");
					SBQuery.append(" WHERE CV.treasury_code = tn.treasury_code AND CV.ddo_code=tn.ddo_code AND trim(CV.scheme_code) = trim(tn.FROM_SCHEME) AND CV.voucher_no = tn.voucher_no ");
					
					SBQuery.append(" and CV.ddo_Code like '"+treasuryCode+"%' ");
					SBQuery.append(" AND CV.ddo_code = OD.ddo_code ");
					SBQuery.append(" AND CV.voucher_date BETWEEN '"+ fromDateQuery + "' AND '" + toDateQuery + "'");
					SBQuery.append(" AND FY.fin_year_desc = TN.year_desc AND FY.fin_year_id = CV.year_id ");
					SBQuery.append(" AND CV.STATUS in ('A','B','E')");
					SBQuery.append(" AND tn.STATUS in ('A','B','E')");
					SBQuery.append(" AND CV.BILL_GROUP_ID is not null ");
					SBQuery.append(" AND CV.manually_matched <> 1");
					SBQuery.append(" AND cv.voucher_status=1 ");
					if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
						SBQuery.append(" AND OD.ddo_code = '"+ddoCode+"'");

					
					SBQuery.append(" UNION");
					SBQuery.append(" SELECT CV.treasury_code, cv.voucher_no, cv.voucher_date,cv.voucher_amount,CV.SCHEME_CODE,TN.bill_no, TN.bill_date,");
					SBQuery.append(" tn.voucher_no, tn.voucher_date,TN.from_scheme,tn.dcps_amount,cv.ddo_code ,'V-B' AS VCStatus1,'V-B' AS VCStatus2");
					SBQuery.append(" FROM mst_dcps_contri_voucher_dtls cv , mst_dcps_treasurynet_data tn , sgvc_fin_year_mst FY");
					SBQuery.append(" WHERE cv.voucher_no = tn.voucher_no AND cv.voucher_date = tn.voucher_date AND cv.voucher_amount <> tn.dcps_amount AND CV.treasury_code = '"
									+ treasuryCode + "'");
					SBQuery.append(" AND cv.voucher_date BETWEEN '"
							+ fromDateQuery + "' AND '" + toDateQuery + "'");
					SBQuery.append(" AND FY.fin_year_desc = TN.year_desc AND FY.fin_year_id = CV.year_id");

					SBQuery.append(" AND CV.manually_matched <> 1");
					 


					StrSqlQuery = SBQuery.toString();
					rs = smt.executeQuery(StrSqlQuery);
					
				}*/

				gLogger.info("StrSqlQuery***********"+StrSqlQuery.toString());
				Integer counter = 1;
				//	Integer counterDDOWise = 1;
				//	String lStrDDOCodePrvs = "";

				String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=700031&action=generateReport&DirectReport=TRUE&displayOK=TRUE";

				while (rs.next()) {

					rowList = new ArrayList();

					if (!(rs.getString(12).equals("") || rs.getString(12) == null)) {
						lStrDDOCode = rs.getString(12).toString().trim();
					}

					/*
					if(!lStrDDOCodePrvs.equals(lStrDDOCode))
					{
						counterDDOWise = 1;
					}
					 */
					//rowList.add(new StyledData(counter, CenterAlignVO));

					if (rs.getString("VCStatus1") != null  && !rs.getString("VCStatus1").equals("")) {

						if(rs.getString("VCStatus1").trim().equals("A"))
						{
							rowList.add(new StyledData("Approved",CenterAlignVO));
						}
						else if(rs.getString("VCStatus1").trim().equals("G"))
						{
							rowList.add(new StyledData("Approved and Employer Contribution given",CenterAlignVO));
						}
						else if(rs.getString("VCStatus1").trim().equals("B"))
						{
							rowList.add(new StyledData("Amount or Date Mismatch",CenterAlignVO));
						}
						else if(rs.getString("VCStatus1").trim().equals("E"))
						{
							rowList.add(new StyledData("DDO Code Mismatch",CenterAlignVO));
						}
						else if(rs.getString("VCStatus1").trim().equals("F"))
						{
							rowList.add(new StyledData("Matched",CenterAlignVO));
						}
						else
						{
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					lLongRltContriVoucherId = Long.valueOf(rs.getString(17).toString());

					rowList.add(new URLData(rs.getString(2), urlPrefix+ "&treasuryCode=" + rs.getString(1)+"&schemeCode="+rs.getString(5)
							+ "&voucherNo=" + rs.getString(2) + "&fromDate="
							+ fromDate + "&toDate=" + toDate + "&yearDesc="
							+ yearDesc + "&yearId=" + yearId
							+ "&MatchedOrUnMatched=" + MatchedOrUnMatched
							+ "&RltContriVoucherId="+lLongRltContriVoucherId+"&acmaintBy="+acmaintBy
					));

					if (!(rs.getString(3) == null)) {
						if (!(rs.getString(3).equals(""))) {
							rowList.add(new StyledData(sdf1.format(sdf3
									.parse(rs.getString(3).toString())),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(4) == null)) {
						if (!rs.getString(4).equals("")) {
							rowList.add(new StyledData(rs.getString(4),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(5) == null)) {
						if (!rs.getString(5).equals("")) {
							rowList.add(new StyledData(rs.getString(5),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (rs.getString("VCStatus2") != null  && !rs.getString("VCStatus2").equals("")) {

						if(rs.getString("VCStatus2").trim().equals("A"))
						{
							rowList.add(new StyledData("Approved",CenterAlignVO));
						}
						else if(rs.getString("VCStatus2").trim().equals("G"))
						{
							rowList.add(new StyledData("Approved and Employer Contribution given",CenterAlignVO));
						}
						else if(rs.getString("VCStatus2").trim().equals("B"))
						{
							rowList.add(new StyledData("Amount or Date Mismatch",CenterAlignVO));
						}
						else if(rs.getString("VCStatus2").trim().equals("E"))
						{
							rowList.add(new StyledData("DDO Code Mismatch",CenterAlignVO));
						}
						else if(rs.getString("VCStatus2").trim().equals("F"))
						{
							rowList.add(new StyledData("Matched",CenterAlignVO));
						}
						else
						{
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(6) == null)) {
						if (!rs.getString(6).equals("")) {
							rowList.add(new StyledData(rs.getString(6),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(7) == null)) {
						if (!(rs.getString(7).equals(""))) {
							rowList.add(new StyledData(sdf1.format(sdf3
									.parse(rs.getString(7).toString())),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(8) == null)) {
						if (!rs.getString(8).equals("")) {
							rowList.add(new StyledData(rs.getString(8),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(9) == null)) {
						if (!(rs.getString(9).equals(""))) {
							rowList.add(new StyledData(sdf1.format(sdf3
									.parse(rs.getString(9).toString())),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(10) == null)) {
						if (!rs.getString(10).equals("")) {
							rowList.add(new StyledData(rs.getString(10),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(11) == null)) {
						if (!rs.getString(11).equals("")) {
							rowList.add(new StyledData(rs.getString(11),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(12).equals("") || rs.getString(12) == null)) {
						lStrDDOCode = rs.getString(12).toString().trim();
						//lStrDDOCodePrvs = lStrDDOCode;
						//lStrDDOName = lObjDcpsCommonDAO.getDdoNameForCode(lStrDDOCode);
						if(rs.getString(16) != null)
						{
							lStrDDOName = rs.getString(16).toString().trim();
						}
						else
						{
							lStrDDOName = "";
						}
						rowList.add(new StyledData(rs.getString(12) + space(3)
								+ lStrDDOName, CenterAlignVO));
					} else {
						lStrDDOCode = "";
						lStrDDOName = "";

						rowList.add(new StyledData("", CenterAlignVO));
					}
					
					
					
					

					dataList.add(rowList);
					
					
				
					

					counter = counter + 1;
					
					
					
					
					//counterDDOWise = counterDDOWise + 1;
				}
				 
				

			/*	ArrayList row = new ArrayList();

				StyledData	dataStyle = new StyledData();
				dataStyle.setColspan(12);
				dataStyle.setStyles(leftboldStyleVO);
			System.out.println("Inside note");
				dataStyle.setData(gObjRsrcBndle.getString("CMN.Note"));
				row.add(dataStyle);
				dataList.add(row);*/
				
			}
			
		
			else if(report.getReportCode().equals("8000077"))
			{


				// report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				Long treasuryCode = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				String fromDate = ((String) report.getParameterValue("fromDate"));
				String toDate = ((String) report.getParameterValue("toDate"));
				String yearDesc = ((String) report.getParameterValue("yearDesc"));
				Long yearId = Long.valueOf((String) report.getParameterValue("yearId"));
				String lStrDDOCode = null;
				String lStrDDOName = "";
				Long lLongRltContriVoucherId = 0l;
			    int status=1;
				String MatchedOrUnMatched = (String) report
				.getParameterValue("MatchedOrUnMatched");

				String ddoCode = (String) report
				.getParameterValue("cmbDDO");


				
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
						serviceLocator.getSessionFactory());

				String url = "";

				if (MatchedOrUnMatched.equals("Matched")) {
					url = "ifms.htm?actionFlag=loadMatchContriEntryForm&yearId="
						+ yearId
						+ "&fromDate="
						+ fromDate
						+ "&toDate="
						+ toDate;
				}
				if (MatchedOrUnMatched.equals("UnMatched")) {
					url = "ifms.htm?actionFlag=loadUnMatchContriEntryForm&yearId="
						+ yearId
						+ "&fromDate="
						+ fromDate
						+ "&toDate="
						+ toDate;
				}

				StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
				lObjStyleVO = report.getStyleList();
				for (Integer lInt = 0; lInt < report.getStyleList().length; lInt++) {
					if (lObjStyleVO[lInt].getStyleId() == 26) {
						lObjStyleVO[lInt].setStyleValue(url);
					}
				}

				String fromDateQuery = sdf2.format(sdf1.parse(fromDate));
				String toDateQuery = sdf2.format(sdf1.parse(toDate));

				StringBuilder SBQuery = new StringBuilder();
				
				if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
				{
					gLogger.info("Inside if");
					status=2;
				}


			/*	if (MatchedOrUnMatched.equals("Matched")) {
					CallableStatement lClbStmnt = null;
							SBQuery.append(" SELECT CV.treasury_code,CV.voucher_no,CV.voucher_date,CV.voucher_amount,CV.SCHEME_CODE,TN.bill_no, TN.bill_date,");
							SBQuery.append(" TN.voucher_no,TN.voucher_date,trim(TN.from_scheme),TN.dcps_amount,CV.ddo_code,CV.status AS VCStatus1 ,'' AS VCStatus2 ,CV.manually_matched,OD.ddo_name,CV.MST_DCPS_CONTRI_VOUCHER_DTLS ");
							SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_treasurynet_data TN, sgvc_fin_year_mst FY,org_ddo_mst OD  ");
							SBQuery.append(" WHERE ");
							SBQuery.append(" CV.treasury_code = TN.treasury_code AND CV.ddo_code = TN.ddo_code AND trim(CV.scheme_code) = trim(TN.FROM_SCHEME) AND CV.voucher_no = TN.voucher_no AND CV.voucher_amount = TN.dcps_amount AND CV.voucher_date=TN.voucher_date ");
							SBQuery.append(" AND FY.fin_year_desc = TN.year_desc ");
							SBQuery.append(" AND FY.fin_year_id = CV.year_id");
							SBQuery.append(" AND OD.ddo_code = CV.ddo_code");
							SBQuery.append(" AND CV.treasury_code = '" + treasuryCode+ "'");
							SBQuery.append(" AND CV.voucher_date BETWEEN '"+ fromDateQuery + "' AND '" + toDateQuery + "'");
							SBQuery.append(" AND CV.STATUS = 'F'");
							if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
								SBQuery.append(" AND OD.ddo_code = '"+ddoCode+"'");

							//SBQuery.append(" GROUP BY CV.voucher_no");
							
							SBQuery.append("{call getMatchrecordData(?,?,?,?,?)}");
							lClbStmnt = con.prepareCall(SBQuery.toString());
							lClbStmnt.setString(1,ddoCode);
						    lClbStmnt.setString(2,fromDateQuery);
							lClbStmnt.setString(3,toDateQuery);
							lClbStmnt.setLong(4,treasuryCode);
							lClbStmnt.setLong(5,status);
							rs= lClbStmnt.executeQuery();
				}*/
				if (MatchedOrUnMatched.equals("UnMatched")) {
					
					SBQuery.append("SELECT * FROM ( ");
					SBQuery.append(" SELECT CV.treasury_code, cv.voucher_no vno, cv.voucher_date vdate,cv.voucher_amount,CV.SCHEME_CODE,TN.bill_no,TN.bill_date,");
					SBQuery.append(" tn.voucher_no,tn.voucher_date,trim(TN.from_scheme),tn.dcps_amount,CV.ddo_code ,CV.STATUS AS VCStatus1 ,'' AS VCStatus21,'' AS VCStatus22,OD.ddo_name,CV.MST_DCPS_CONTRI_VOUCHER_DTLS  ");
					SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV");
					SBQuery.append(" JOIN sgvc_fin_year_mst FY on CV.YEAR_ID = FY.FIN_YEAR_ID ");
					SBQuery.append(" LEFT JOIN mst_dcps_treasurynet_data tn ON cv.voucher_no = tn.voucher_no  and CV.treasury_code = tn.treasury_code AND tn.ddo_code = CV.ddo_code AND trim(CV.scheme_code) = trim(tn.FROM_SCHEME) AND FY.fin_year_desc = tn.year_desc    " );
					SBQuery.append(" JOIN ORG_DDO_MST OD on OD.ddo_code = CV.ddo_code");
					//SBQuery.append(" WHERE tn.voucher_no IS NULL AND CV.treasury_code ='"+ treasuryCode + "' ");
					SBQuery.append(" WHERE tn.voucher_no IS NULL ");
					SBQuery.append(" AND  CV.ddo_code like '"+treasuryCode+"%' ");
					SBQuery.append(" AND CV.BILL_GROUP_ID is not null ");
					SBQuery.append(" AND ((cv.voucher_date  BETWEEN '" + fromDateQuery + "' AND '"+ toDateQuery + "') or (CV.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + fromDateQuery + "' AND '"+ toDateQuery + "')) ");
					SBQuery.append(" AND CV.STATUS in ('A','B','E')");
					//SBQuery.append(" AND CV.STATUS in ('B','E')");
					SBQuery.append(" AND CV.manually_matched <> 1");
					SBQuery.append(" AND CV.VOUCHER_AMOUNT<>0 ");
					SBQuery.append(" AND cv.voucher_status=1 ");
					if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
						SBQuery.append(" AND OD.ddo_code = '"+ddoCode+"'");


					SBQuery.append(" UNION");
					SBQuery.append(" SELECT CV.treasury_code,cv.voucher_no vno,cv.voucher_date vdate,cv.voucher_amount,CV.SCHEME_CODE,TN.bill_no,TN.bill_date,");
					SBQuery.append(" tn.voucher_no, tn.voucher_date,trim(TN.from_scheme),tn.dcps_amount,tn.ddo_code ,'' AS VCStatus1,tn.STATUS||'' AS VCStatus21,tn.STATUS||'' AS VCStatus22,OD.ddo_name,nvl(CV.MST_DCPS_CONTRI_VOUCHER_DTLS,0)");
					SBQuery.append(" FROM mst_dcps_treasurynet_data tn ");
					SBQuery.append(" JOIN sgvc_fin_year_mst FY on tn.year_desc = FY.FIN_YEAR_DESC ");
					SBQuery.append(" LEFT JOIN mst_dcps_contri_voucher_dtls cv ON cv.voucher_no = tn.voucher_no   and CV.treasury_code = tn.treasury_code AND tn.ddo_code = CV.ddo_code AND trim(CV.scheme_code) = trim(tn.FROM_SCHEME) AND FY.fin_year_id = CV.year_id");
					SBQuery.append(" JOIN ORG_DDO_MST OD on OD.ddo_code = tn.ddo_code");
					//SBQuery.append(" WHERE CV.voucher_no IS NULL AND tn.treasury_code ='"+ treasuryCode + "' ");
					SBQuery.append(" WHERE CV.voucher_no IS NULL ");
					SBQuery.append(" AND tn.ddo_code like '"+treasuryCode+"%' ");
					SBQuery.append(" AND tn.voucher_date BETWEEN '"+ fromDateQuery + "' AND '" + toDateQuery + "'");
					SBQuery.append(" AND tn.STATUS in ('A','B','E')");
					
					//SBQuery.append(" AND tn.STATUS in ('B','E')");
					//SBQuery.append(" AND CV.manually_matched <> 1");
					if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
						SBQuery.append(" AND OD.ddo_code = '"+ddoCode+"'");


					SBQuery.append(" UNION ");
					SBQuery.append(" SELECT CV.treasury_code, cv.voucher_no vno,cv.voucher_date vdate,cv.voucher_amount,CV.SCHEME_CODE,TN.bill_no, TN.bill_date,");
					SBQuery.append(" tn.voucher_no, tn.voucher_date,trim(TN.from_scheme),tn.dcps_amount,cv.ddo_code,CV.STATUS AS VCStatus1,tn.STATUS||'' AS VCStatus21,tn.STATUS||'' AS VCStatus22,OD.ddo_name,CV.MST_DCPS_CONTRI_VOUCHER_DTLS");
					SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV , mst_dcps_treasurynet_data tn , sgvc_fin_year_mst FY,ORG_DDO_MST OD");
					//SBQuery.append(" WHERE CV.treasury_code = tn.treasury_code AND trim(CV.scheme_code) = trim(tn.FROM_SCHEME) AND CV.voucher_no = tn.voucher_no AND CV.treasury_code = '"+ treasuryCode + "'");
					SBQuery.append(" WHERE CV.treasury_code = tn.treasury_code AND CV.ddo_code=tn.ddo_code AND trim(CV.scheme_code) = trim(tn.FROM_SCHEME) AND CV.voucher_no = tn.voucher_no  ");
					SBQuery.append(" and CV.ddo_Code like '"+treasuryCode+"%' ");
					SBQuery.append(" AND CV.ddo_code = OD.ddo_code ");
					SBQuery.append(" AND ((cv.voucher_date  BETWEEN '" + fromDateQuery + "' AND '"+ toDateQuery + "') or (CV.MISSING_CREDIT_APPROVAL_DATE BETWEEN '" + fromDateQuery + "' AND '"+ toDateQuery + "')) ");
					SBQuery.append(" AND FY.fin_year_desc = TN.year_desc AND FY.fin_year_id = CV.year_id ");
					SBQuery.append(" AND CV.STATUS in ('A','B','E')");
					SBQuery.append(" AND tn.STATUS in ('A','B','E')") ;
					SBQuery.append(" AND cv.voucher_status=1 ");
					SBQuery.append(" AND CV.manually_matched <> 1");
					SBQuery.append(" AND CV.VOUCHER_AMOUNT<>0 ");
					SBQuery.append(" AND CV.BILL_GROUP_ID is not null ");
					if(ddoCode!=null && !ddoCode.equals("") && Long.parseLong(ddoCode)!=-1)
						SBQuery.append(" AND OD.ddo_code = '"+ddoCode+"'");
					SBQuery.append(" )rsl ");
					SBQuery.append("  order by  rsl.vno, rsl.vdate ");

					
					/*
				 	SBQuery.append(" UNION");
					SBQuery.append(" SELECT CV.treasury_code, cv.voucher_no, cv.voucher_date,cv.voucher_amount,CV.SCHEME_CODE,TN.bill_no, TN.bill_date,");
					SBQuery.append(" tn.voucher_no, tn.voucher_date,TN.from_scheme,tn.dcps_amount,cv.ddo_code ,'V-B' AS VCStatus1,'V-B' AS VCStatus2");
					SBQuery.append(" FROM mst_dcps_contri_voucher_dtls cv , mst_dcps_treasurynet_data tn , sgvc_fin_year_mst FY");
					SBQuery.append(" WHERE cv.voucher_no = tn.voucher_no AND cv.voucher_date = tn.voucher_date AND cv.voucher_amount <> tn.dcps_amount AND CV.treasury_code = '"
									+ treasuryCode + "'");
					SBQuery.append(" AND cv.voucher_date BETWEEN '"
							+ fromDateQuery + "' AND '" + toDateQuery + "'");
					SBQuery.append(" AND FY.fin_year_desc = TN.year_desc AND FY.fin_year_id = CV.year_id");

					SBQuery.append(" AND CV.manually_matched <> 1");
					 */

				}

				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);
				gLogger.info("StrSqlQuery***********"+StrSqlQuery.toString());
				Integer counter = 1;
				//	Integer counterDDOWise = 1;
				//	String lStrDDOCodePrvs = "";

				String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=8000078&action=generateReport&DirectReport=TRUE&displayOK=TRUE";



				String prvStatus="";
				String prvVoucNo="";
				String prvVoucDate="";
				String prvDDOAmt="";
				String prvSchemeCode="";
				boolean flagS=false;
				boolean flagVN=false;
				boolean flagVD=false;
				boolean flagDA=false;
				boolean flagSC=false;
				
				
				String prvStatus1="";
				String prvVoucNo1="";
				String prvVoucDate1="";
				String prvDDOAmt1="";
				String prvSchemeCode1="";
				String prvBillNo="";
				String prvBillDate="";
				boolean flagS1=false;
				boolean flagBN=false;
				boolean flagBD=false;
				boolean flagVN1=false;
				boolean flagVD1=false;
				boolean flagDA1=false;
				boolean flagSC1=false;


				while (rs.next()) {

					rowList = new ArrayList();

					

					/*
					if(!lStrDDOCodePrvs.equals(lStrDDOCode))
					{
						counterDDOWise = 1;
					}
					 */
					//rowList.add(new StyledData(counter, CenterAlignVO));


					if(rs.getString("VCStatus1")!=null && !prvStatus.equals(rs.getString("VCStatus1").toString())){
						flagS=true;
						
					}
					
					else{
						flagS=false;
					}

					if(rs.getString(2)!=null && !prvVoucNo.equals(rs.getString(2).toString())){
						flagVN=true;
					}
					
					else{
						flagVN=false;
					}

					if(rs.getString(3)!=null && !prvVoucDate.equals(rs.getString(3).toString())){
						flagVD=true;
					}
					
					else{
						flagVD=false;
					}

					if(rs.getString(4)!=null && !prvDDOAmt.equals(rs.getString(4).toString())){
						flagDA=true;
					}

					else{
						flagDA=false;
					}
					
					if(rs.getString(5)!=null && !prvSchemeCode.equals(rs.getString(5).toString())){
						flagSC=true;
					}
					
					else{
						flagSC=false;
					}

          
            gLogger.info("new Status is ***"+rs.getString("VCStatus1")+"Status is ***"+prvStatus );
            gLogger.info("new Voucher no is ***"+rs.getString(2)+"Voucher no is ***"+prvVoucNo );
            gLogger.info("new Voucher date is ***"+rs.getString(3)+"Voucher date is ***"+prvVoucDate );
            gLogger.info("new Amount is ***"+rs.getString(4)+"Amount is ***"+prvDDOAmt );
            gLogger.info("new Scheme code is ***"+rs.getString(5)+"Scheme code is ***"+prvSchemeCode );

					if(flagS || flagVN || flagVD || flagDA || flagSC){
						if (rs.getString("VCStatus1") != null  && !rs.getString("VCStatus1").equals("")) {

							if(rs.getString("VCStatus1").trim().equals("A"))
							{
								rowList.add(new StyledData("Approved",CenterAlignVO));
							}
							else if(rs.getString("VCStatus1").trim().equals("G"))
							{
								rowList.add(new StyledData("Approved and Employer Contribution given",CenterAlignVO));
							}
							else if(rs.getString("VCStatus1").trim().equals("B"))
							{
								rowList.add(new StyledData("Amount or Date Mismatch",CenterAlignVO));
							}
							else if(rs.getString("VCStatus1").trim().equals("E"))
							{
								rowList.add(new StyledData("DDO Code Mismatch",CenterAlignVO));
							}
							else if(rs.getString("VCStatus1").trim().equals("F"))
							{
								rowList.add(new StyledData("Matched",CenterAlignVO));
							}
							else
							{
								rowList.add(new StyledData("", CenterAlignVO));
							}
							
							prvStatus= rs.getString("VCStatus1");
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}

						lLongRltContriVoucherId = Long.valueOf(rs.getString(17).toString());

						rowList.add(new URLData(rs.getString(2), urlPrefix
								+ "&treasuryCode=" + rs.getString(1)+"&schemeCode="+rs.getString(5)
								+ "&voucherNo=" + rs.getString(2) + "&fromDate="
								+ fromDate + "&toDate=" + toDate + "&yearDesc="
								+ yearDesc + "&yearId=" + yearId
								+ "&MatchedOrUnMatched=" + MatchedOrUnMatched
								+ "&RltContriVoucherId="+lLongRltContriVoucherId
						));
						
						if(rs.getString(2)!=null){
							prvVoucNo= rs.getString(2);
						}

						if (!(rs.getString(3) == null)) {
							if (!(rs.getString(3).equals(""))) {
								rowList.add(new StyledData(sdf1.format(sdf3
										.parse(rs.getString(3).toString())),
										CenterAlignVO));
								prvVoucDate= rs.getString(3);
							} else {
								rowList.add(new StyledData("", CenterAlignVO));
							}
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}

						if (!(rs.getString(4) == null)) {
							if (!rs.getString(4).equals("")) {
								rowList.add(new StyledData(rs.getString(4),
										CenterAlignVO));
								prvDDOAmt = rs.getString(4);
							} else {
								rowList.add(new StyledData("", CenterAlignVO));
							}
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}

						if (!(rs.getString(5) == null)) {
							if (!rs.getString(5).equals("")) {
								rowList.add(new StyledData(rs.getString(5),
										CenterAlignVO));
								prvSchemeCode = rs.getString(5);
							} else {
								rowList.add(new StyledData("", CenterAlignVO));
							}
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}


					}

					else{
						rowList.add(new StyledData("",CenterAlignVO));
						rowList.add(new StyledData("",CenterAlignVO));
						rowList.add(new StyledData("",CenterAlignVO));
						rowList.add(new StyledData("",CenterAlignVO));
						rowList.add(new StyledData("",CenterAlignVO));
					 }


					// Second side: start
					
					

					if(rs.getString("VCStatus21")!=null && !prvStatus1.equals(rs.getString("VCStatus21").toString())){
						flagS1=true;
						
					}
					
					else{
						flagS1=false;
					}

					
					if(rs.getString(6)!=null && !prvBillNo.equals(rs.getString(6).toString())){
						flagBN=true;
					}
					
					else{
						flagBN=false;
					}
					
					if(rs.getString(7)!=null && !prvBillDate.equals(rs.getString(7).toString())){
						flagBD=true;
					}
					
					else{
						flagBD=false;
					}
					
					
					if(rs.getString(8)!=null && !prvVoucNo1.equals(rs.getString(8).toString())){
						flagVN1=true;
					}
					
					else{
						flagVN1=false;
					}

					if(rs.getString(9)!=null && !prvVoucDate1.equals(rs.getString(9).toString())){
						flagVD1=true;
					}
					
					else{
						flagVD1=false;
					}

					if(rs.getString(11)!=null && !prvDDOAmt1.equals(rs.getString(11).toString())){
						flagDA1=true;
					}

					else{
						flagDA1=false;
					}
					
					if(rs.getString(10)!=null && !prvSchemeCode1.equals(rs.getString(10).toString())){
						flagSC1=true;
					}
					
					else{
						flagSC1=false;
					}

          
            gLogger.info("new Status1 is ***"+rs.getString("VCStatus21")+"Status1 is ***"+prvStatus1 );
            gLogger.info("new Voucher no1 is ***"+rs.getString(8)+"Voucher no1 is ***"+prvVoucNo1 );
            gLogger.info("new Voucher date1 is ***"+rs.getString(9)+"Voucher date1 is ***"+prvVoucDate1 );
            gLogger.info("new Amount1 is ***"+rs.getString(11)+"Amount1 is ***"+prvDDOAmt1 );
            gLogger.info("new Scheme code1 is ***"+rs.getString(10)+"Scheme code1 is ***"+prvSchemeCode1 );

        	if(flagS1 || flagBN ||   flagBD ||  flagVN1 || flagVD1 || flagDA1 || flagSC1){
        		
        

					if (rs.getString("VCStatus21") != null  && !rs.getString("VCStatus21").equals("")) {

						if(rs.getString("VCStatus21").trim().equals("A"))
						{
							rowList.add(new StyledData("Approved",CenterAlignVO));
						}
						else if(rs.getString("VCStatus21").trim().equals("G"))
						{
							rowList.add(new StyledData("Approved and Employer Contribution given",CenterAlignVO));
						}
						else if(rs.getString("VCStatus21").trim().equals("B"))
						{
							rowList.add(new StyledData("Amount or Date Mismatch",CenterAlignVO));
						}
						else if(rs.getString("VCStatus21").trim().equals("E"))
						{
							rowList.add(new StyledData("DDO Code Mismatch",CenterAlignVO));
						}
						else if(rs.getString("VCStatus21").trim().equals("F"))
						{
							rowList.add(new StyledData("Matched",CenterAlignVO));
						}
						else
						{
							rowList.add(new StyledData("", CenterAlignVO));
						}
						
						prvStatus1= rs.getString("VCStatus21");
						
						
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					
					
					if (!(rs.getString(6) == null)) {
						if (!rs.getString(6).equals("")) {
							rowList.add(new StyledData(rs.getString(6),
									CenterAlignVO));
							prvBillNo=rs.getString(6);
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(7) == null)) {
						if (!(rs.getString(7).equals(""))) {
							rowList.add(new StyledData(sdf1.format(sdf3
									.parse(rs.getString(7).toString())),
									CenterAlignVO));
							prvBillDate=rs.getString(7);
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(8) == null)) {
						if (!rs.getString(8).equals("")) {
							rowList.add(new StyledData(rs.getString(8),
									CenterAlignVO));
							prvVoucNo1= rs.getString(8);
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(9) == null)) {
						if (!(rs.getString(9).equals(""))) {
							rowList.add(new StyledData(sdf1.format(sdf3
									.parse(rs.getString(9).toString())),
									CenterAlignVO));
							
							prvVoucDate1= rs.getString(9);
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(10) == null)) {
						if (!rs.getString(10).equals("")) {
							rowList.add(new StyledData(rs.getString(10),
									CenterAlignVO));
 							prvSchemeCode1 = rs.getString(10);	
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(11) == null)) {
						if (!rs.getString(11).equals("")) {
							rowList.add(new StyledData(rs.getString(11),
									CenterAlignVO));
							
							prvDDOAmt1 = rs.getString(11);
							
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					
        	}
        	else{
        		rowList.add(new StyledData("",CenterAlignVO));
        		rowList.add(new StyledData("",CenterAlignVO));
				rowList.add(new StyledData("",CenterAlignVO));
				rowList.add(new StyledData("",CenterAlignVO));
				rowList.add(new StyledData("",CenterAlignVO));
				rowList.add(new StyledData("",CenterAlignVO));
				rowList.add(new StyledData("",CenterAlignVO));
			 }
				
        	
        	
				if (!(rs.getString(12).equals("") || rs.getString(12) == null)) {
						lStrDDOCode = rs.getString(12).toString().trim();
						//lStrDDOCodePrvs = lStrDDOCode;
						//lStrDDOName = lObjDcpsCommonDAO.getDdoNameForCode(lStrDDOCode);
						if(rs.getString(16) != null)
						{
							lStrDDOName = rs.getString(16).toString().trim();
						}
						else
						{
							lStrDDOName = "";
						}
						rowList.add(new StyledData(rs.getString(12) + space(3)
								+ lStrDDOName, CenterAlignVO));
					} else {
						lStrDDOCode = "";
						lStrDDOName = "";

						rowList.add(new StyledData("", CenterAlignVO));
					}

					dataList.add(rowList);

					counter = counter + 1;
					//counterDDOWise = counterDDOWise + 1;
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
