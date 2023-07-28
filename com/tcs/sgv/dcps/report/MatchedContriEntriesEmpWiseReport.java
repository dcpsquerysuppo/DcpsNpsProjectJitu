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
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Feb 9, 2011
 */
public class MatchedContriEntriesEmpWiseReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(DCPSEmployeeAcknowledgementReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;

	SessionFactory lObjSessionFactory = null;

	ServiceLocator serviceLocator = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		// setSessionInfo((Map) criteria);

		report.getLocId();
		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		ArrayList dataList = new ArrayList();

		// for Center Alignment format
		StyleVO[] CenterAlignVO = new StyleVO[2];
		CenterAlignVO[0] = new StyleVO();
		CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		CenterAlignVO[1] = new StyleVO();
		CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
		CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		try {

			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();

			Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);

			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");

			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			loginDetail.get("locationId");

			String StrSqlQuery = "";
	

			if (report.getReportCode().equals("700031")) {

				// report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				Long treasuryCode = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				Long voucherNo = Long.valueOf((String) report.getParameterValue("voucherNo"));
				String schemeCode = (String) report.getParameterValue("schemeCode");
				String yearDesc = ((String) report.getParameterValue("yearDesc"));
				Long yearId = Long.valueOf((String) report.getParameterValue("yearId"));
				Long lLongRltContriVoucherId = Long.valueOf((String) report.getParameterValue("RltContriVoucherId"));
				Long acmaintBy = Long.valueOf((String) report.getParameterValue("acmaintBy"));
				new MatchContriEntryDAOImpl(null, serviceLocator.getSessionFactory());

				new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());

				StringBuilder SBQuery = new StringBuilder();

				String MatchedOrUnMatched = (String) report.getParameterValue("MatchedOrUnMatched");

				/*
				SBQuery.append(" SELECT TN.bill_no,EM.dcps_id,EM.emp_name,DC.basic_pay,DC.dp,DC.da,(DC.basic_pay+DC.dp+DC.da),DC.contribution,FY.fin_year_code,FM.month_id,CM.lookup_short_name");
				SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_treasurynet_data TN, trn_dcps_contribution DC, mst_dcps_emp EM, ");
				SBQuery.append(" sgvc_fin_year_mst FY,sgva_month_mst FM,cmn_lookup_mst CM ");
				SBQuery.append(" WHERE CV.voucher_no = TN.voucher_no ");
				SBQuery.append(" AND CV.voucher_date = TN.voucher_date");
				//SBQuery.append(" AND CV.treasury_code = DC.treasury_code");
				SBQuery.append(" AND CV.MST_DCPS_CONTRI_VOUCHER_DTLS=DC.RLT_CONTRI_VOUCHER_ID");

				if (MatchedOrUnMatched.equals("Matched")) {
					SBQuery.append(" AND TN.dcps_amount = CV.voucher_amount");
				}
				if (MatchedOrUnMatched.equals("UnMatched")) {
					SBQuery.append(" AND TN.dcps_amount <> CV.voucher_amount");
				}

				SBQuery.append(" AND EM.dcps_emp_id = DC.dcps_emp_id");
				SBQuery.append(" AND FY.fin_year_desc = TN.year_desc");
				SBQuery.append(" AND FY.fin_year_id = CV.year_id ");
				//SBQuery.append(" AND DC.fin_year_id = CV.year_id ");
				//SBQuery.append(" AND DC.month_id = CV.month_id");
				SBQuery.append(" AND CV.SCHEME_CODE = TN.from_scheme ");
				//SBQuery.append(" AND FY.fin_year_id = DC.fin_year_id ");
				SBQuery.append(" AND FM.month_id = CV.month_id ");
				SBQuery.append(" AND CM.lookup_id = DC.type_of_payment");
				SBQuery.append(" AND CV.treasury_code = " + treasuryCode);
				SBQuery.append(" AND CV.voucher_no = " + voucherNo);
				*/
				
				// Treasurynet Join removed from above query
				
				SBQuery.append(" SELECT CV.voucher_no,EM.dcps_id,EM.emp_name,DC.basic_pay,DC.dp,DC.da,(DC.basic_pay+DC.dp+DC.da),DC.contribution,FY.fin_year_code,FM.MONTH_NAME,CM.lookup_short_name,COALESCE(CV.MANUAL_REMARK,'-'),FM.month_id ");
				SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,trn_dcps_contribution DC, mst_dcps_emp EM, ");
				SBQuery.append(" sgvc_fin_year_mst FY,sgva_month_mst FM,cmn_lookup_mst CM ");
				SBQuery.append(" WHERE ");
				SBQuery.append(" CV.MST_DCPS_CONTRI_VOUCHER_DTLS = DC.RLT_CONTRI_VOUCHER_ID ");
				SBQuery.append(" AND EM.dcps_emp_id = DC.dcps_emp_id ");
				SBQuery.append(" AND FY.fin_year_id = CV.year_id  and em.AC_DCPS_MAINTAINED_BY="+acmaintBy );
				SBQuery.append(" AND FM.month_id = DC.month_id ");
				SBQuery.append(" AND CM.lookup_id = DC.type_of_payment");
				/*
				SBQuery.append(" AND CV.treasury_code = " + treasuryCode);
				SBQuery.append(" AND CV.voucher_no = " + voucherNo);
				SBQuery.append(" AND CV.year_id = " + yearId);
				SBQuery.append(" AND CV.scheme_code = '" + schemeCode + "'");
				SBQuery.append(" AND FY.FIN_YEAR_DESC = '" + yearDesc + "'");
				*/
				SBQuery.append(" AND DC.RLT_CONTRI_VOUCHER_ID = "+lLongRltContriVoucherId);
				
				if (MatchedOrUnMatched.equals("Matched")) 
				{
					SBQuery.append(" AND CV.status in ('F')");
				}
				if (MatchedOrUnMatched.equals("UnMatched")) 
				{
					SBQuery.append(" AND CV.status in ('A','B','E')");
				}

				
			
				
				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);
				Integer counter = 1;

				while (rs.next()) {
					long year=Long.parseLong(rs.getString(9));
					long month=Long.parseLong(rs.getString(13));
					
					if(month<4){
						year=year+1;
					}
					rowList = new ArrayList();

					rowList.add(new StyledData(counter, CenterAlignVO));
					rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(8), CenterAlignVO));
					rowList.add(new StyledData(year, CenterAlignVO));
					rowList.add(new StyledData(rs.getString(10), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(11), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(12), CenterAlignVO));
					dataList.add(rowList);

					counter = counter + 1;
				}
			}
			else if (report.getReportCode().equals("8000078"))
			{


				// report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				Long treasuryCode = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				Long voucherNo = Long.valueOf((String) report.getParameterValue("voucherNo"));
				//String schemeCode = (String) report.getParameterValue("schemeCode");
				String yearDesc = ((String) report.getParameterValue("yearDesc"));
				Long yearId = Long.valueOf((String) report.getParameterValue("yearId"));
				Long lLongRltContriVoucherId = Long.valueOf((String) report.getParameterValue("RltContriVoucherId"));
				String fromDate = ((String) report.getParameterValue("fromDate"));
				String toDate = ((String) report.getParameterValue("toDate"));
				
				new MatchContriEntryDAOImpl(null, serviceLocator.getSessionFactory());

				new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());
				
				String MatchedOrUnMatched = (String) report.getParameterValue("MatchedOrUnMatched");

			
				
			
				StringBuilder SBQuery = new StringBuilder();

			
				
				
	// Added by ashish for Ok button utility
				
/*String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=8000080&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
				
				
				
				
				StyleVO[] newLink = new StyleVO[1];
				newLink[0] = new StyleVO();
				newLink[0].setStyleId(IReportConstants.REPORT_PAGE_OK_BTN_URL);
				newLink[0].setStyleValue(urlPrefix+"&postEmpContriPkId="+lStrPostEmpContriId+ "&billNo=" +lObjPostEmpContri.getBillNo()+"&dcpsAcntMntndBy="+strAcDcpsMntndBy+"&finYear="+finYear );
				report.setStyleList(newLink); */
				
				
				
// ended by ashish for Ok button utility
				
				
				/*
				SBQuery.append(" SELECT TN.bill_no,EM.dcps_id,EM.emp_name,DC.basic_pay,DC.dp,DC.da,(DC.basic_pay+DC.dp+DC.da),DC.contribution,FY.fin_year_code,FM.month_id,CM.lookup_short_name");
				SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_treasurynet_data TN, trn_dcps_contribution DC, mst_dcps_emp EM, ");
				SBQuery.append(" sgvc_fin_year_mst FY,sgva_month_mst FM,cmn_lookup_mst CM ");
				SBQuery.append(" WHERE CV.voucher_no = TN.voucher_no ");
				SBQuery.append(" AND CV.voucher_date = TN.voucher_date");
				//SBQuery.append(" AND CV.treasury_code = DC.treasury_code");
				SBQuery.append(" AND CV.MST_DCPS_CONTRI_VOUCHER_DTLS=DC.RLT_CONTRI_VOUCHER_ID");

				if (MatchedOrUnMatched.equals("Matched")) {
					SBQuery.append(" AND TN.dcps_amount = CV.voucher_amount");
				}
				if (MatchedOrUnMatched.equals("UnMatched")) {
					SBQuery.append(" AND TN.dcps_amount <> CV.voucher_amount");
				}

				SBQuery.append(" AND EM.dcps_emp_id = DC.dcps_emp_id");
				SBQuery.append(" AND FY.fin_year_desc = TN.year_desc");
				SBQuery.append(" AND FY.fin_year_id = CV.year_id ");
				//SBQuery.append(" AND DC.fin_year_id = CV.year_id ");
				//SBQuery.append(" AND DC.month_id = CV.month_id");
				SBQuery.append(" AND CV.SCHEME_CODE = TN.from_scheme ");
				//SBQuery.append(" AND FY.fin_year_id = DC.fin_year_id ");
				SBQuery.append(" AND FM.month_id = CV.month_id ");
				SBQuery.append(" AND CM.lookup_id = DC.type_of_payment");
				SBQuery.append(" AND CV.treasury_code = " + treasuryCode);
				SBQuery.append(" AND CV.voucher_no = " + voucherNo);
				*/
				
				// Treasurynet Join removed from above query
				
				SBQuery.append(" SELECT CV.voucher_no,EM.dcps_id,EM.emp_name,DC.basic_pay,DC.dp,DC.da,(DC.basic_pay+DC.dp+DC.da),DC.contribution,FY.fin_year_code,FM.month_id,CM.lookup_short_name,COALESCE(CV.MANUAL_REMARK,'-'),FM.month_id ");
				SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,trn_dcps_contribution DC, mst_dcps_emp EM, ");
				SBQuery.append(" sgvc_fin_year_mst FY,sgva_month_mst FM,cmn_lookup_mst CM ");
				SBQuery.append(" WHERE ");
				SBQuery.append(" CV.MST_DCPS_CONTRI_VOUCHER_DTLS = DC.RLT_CONTRI_VOUCHER_ID");
				SBQuery.append(" AND EM.dcps_emp_id = DC.dcps_emp_id");
				SBQuery.append(" AND FY.fin_year_id = CV.year_id ");
				SBQuery.append(" AND FM.month_id = DC.month_id ");
				SBQuery.append(" AND CM.lookup_id = DC.type_of_payment");
				/*
				SBQuery.append(" AND CV.treasury_code = " + treasuryCode);
				SBQuery.append(" AND CV.voucher_no = " + voucherNo);
				SBQuery.append(" AND CV.year_id = " + yearId);
				SBQuery.append(" AND CV.scheme_code = '" + schemeCode + "'");
				SBQuery.append(" AND FY.FIN_YEAR_DESC = '" + yearDesc + "'");
				*/
				SBQuery.append(" AND DC.RLT_CONTRI_VOUCHER_ID = "+lLongRltContriVoucherId);
				
				if (MatchedOrUnMatched.equals("Matched")) 
				{
					SBQuery.append(" AND CV.status in ('F')");
				}
				if (MatchedOrUnMatched.equals("UnMatched")) 
				{
					SBQuery.append(" AND CV.status in ('A','B','E')");
				}

				
				    
				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);
				Integer counter = 1;

				while (rs.next()) {
					long year=Long.parseLong(rs.getString(9));
					long month=Long.parseLong(rs.getString(13));
					
					if(month<4){
						year=year+1;
					}
					rowList = new ArrayList();

					rowList.add(new StyledData(counter, CenterAlignVO));
					rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(8), CenterAlignVO));
					rowList.add(new StyledData(year, CenterAlignVO));
					rowList.add(new StyledData(rs.getString(10), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(11), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(12), CenterAlignVO));
					dataList.add(rowList);

					counter = counter + 1;
				}
			
			}
		  if(report.getReportCode().equals("8000081"))
			{


				// report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				Long treasuryCode = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				Long voucherNo = Long.valueOf((String) report.getParameterValue("voucherNo"));
				//String schemeCode = (String) report.getParameterValue("schemeCode");
				String yearDesc = ((String) report.getParameterValue("yearDesc"));
				Long yearId = Long.valueOf((String) report.getParameterValue("yearId"));
				Long lLongRltContriVoucherId = Long.valueOf((String) report.getParameterValue("RltContriVoucherId"));
				String fromDate = ((String) report.getParameterValue("fromDate"));
				gLogger.info("fromDate is ****"+fromDate);
				
				String toDate = ((String) report.getParameterValue("toDate"));
				gLogger.info("toDate is ****"+toDate);
				new MatchContriEntryDAOImpl(null, serviceLocator.getSessionFactory());

				new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());
				String MatchedOrUnMatched = (String) report.getParameterValue("MatchedOrUnMatched");

				
				
				
				
	               String url = "";

				
				if (MatchedOrUnMatched.equals("Manual")) {
					url = "ifms.htm?actionFlag=loadManualMatch&requestForMatching=Yes"
						+ "&yearId="
						+ yearId
						+ "&treasuryCode="
						+ treasuryCode
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
						System.out.println("url is *****"+url);
					}
				}

				
			
				StringBuilder SBQuery = new StringBuilder();

			
				
				/*
				SBQuery.append(" SELECT TN.bill_no,EM.dcps_id,EM.emp_name,DC.basic_pay,DC.dp,DC.da,(DC.basic_pay+DC.dp+DC.da),DC.contribution,FY.fin_year_code,FM.month_id,CM.lookup_short_name");
				SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,mst_dcps_treasurynet_data TN, trn_dcps_contribution DC, mst_dcps_emp EM, ");
				SBQuery.append(" sgvc_fin_year_mst FY,sgva_month_mst FM,cmn_lookup_mst CM ");
				SBQuery.append(" WHERE CV.voucher_no = TN.voucher_no ");
				SBQuery.append(" AND CV.voucher_date = TN.voucher_date");
				//SBQuery.append(" AND CV.treasury_code = DC.treasury_code");
				SBQuery.append(" AND CV.MST_DCPS_CONTRI_VOUCHER_DTLS=DC.RLT_CONTRI_VOUCHER_ID");

				if (MatchedOrUnMatched.equals("Matched")) {
					SBQuery.append(" AND TN.dcps_amount = CV.voucher_amount");
				}
				if (MatchedOrUnMatched.equals("UnMatched")) {
					SBQuery.append(" AND TN.dcps_amount <> CV.voucher_amount");
				}

				SBQuery.append(" AND EM.dcps_emp_id = DC.dcps_emp_id");
				SBQuery.append(" AND FY.fin_year_desc = TN.year_desc");
				SBQuery.append(" AND FY.fin_year_id = CV.year_id ");
				//SBQuery.append(" AND DC.fin_year_id = CV.year_id ");
				//SBQuery.append(" AND DC.month_id = CV.month_id");
				SBQuery.append(" AND CV.SCHEME_CODE = TN.from_scheme ");
				//SBQuery.append(" AND FY.fin_year_id = DC.fin_year_id ");
				SBQuery.append(" AND FM.month_id = CV.month_id ");
				SBQuery.append(" AND CM.lookup_id = DC.type_of_payment");
				SBQuery.append(" AND CV.treasury_code = " + treasuryCode);
				SBQuery.append(" AND CV.voucher_no = " + voucherNo);
				*/
				
				// Treasurynet Join removed from above query
				
				SBQuery.append(" SELECT CV.voucher_no,EM.dcps_id,EM.emp_name,DC.basic_pay,DC.dp,DC.da,(DC.basic_pay+DC.dp+DC.da),DC.contribution,FY.fin_year_code,FM.month_name,CM.lookup_short_name,COALESCE(CV.MANUAL_REMARK,'-'),FM.month_id ");
				SBQuery.append(" FROM mst_dcps_contri_voucher_dtls CV,trn_dcps_contribution DC, mst_dcps_emp EM, ");
				SBQuery.append(" sgvc_fin_year_mst FY,sgva_month_mst FM,cmn_lookup_mst CM ");
				SBQuery.append(" WHERE ");
				SBQuery.append(" CV.MST_DCPS_CONTRI_VOUCHER_DTLS = DC.RLT_CONTRI_VOUCHER_ID");
				SBQuery.append(" AND EM.dcps_emp_id = DC.dcps_emp_id");
				SBQuery.append(" AND FY.fin_year_id = CV.year_id ");
				SBQuery.append(" AND FM.month_id = DC.month_id ");
				SBQuery.append(" AND CM.lookup_id = DC.type_of_payment");
				/*
				SBQuery.append(" AND CV.treasury_code = " + treasuryCode);
				SBQuery.append(" AND CV.voucher_no = " + voucherNo);
				SBQuery.append(" AND CV.year_id = " + yearId);
				SBQuery.append(" AND CV.scheme_code = '" + schemeCode + "'");
				SBQuery.append(" AND FY.FIN_YEAR_DESC = '" + yearDesc + "'");
				*/
				SBQuery.append(" AND DC.RLT_CONTRI_VOUCHER_ID = "+lLongRltContriVoucherId);
				
				if (MatchedOrUnMatched.equals("Matched")) 
				{
					SBQuery.append(" AND CV.status in ('F')");
				}
				if (MatchedOrUnMatched.equals("UnMatched")) 
				{
					SBQuery.append(" AND CV.status in ('A','B','E')");
				}


				
				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);
				Integer counter = 1;

				while (rs.next()) {
					long year=Long.parseLong(rs.getString(9));
					long month=Long.parseLong(rs.getString(13));
					gLogger.info("Year is ********"+year);
					gLogger.info("month is ********"+month);
					if(month<4){
						year=year+1;
					}
					gLogger.info("Year is after changes ********"+year);
			
					rowList = new ArrayList();

					rowList.add(new StyledData(counter, CenterAlignVO));
					rowList.add(new StyledData(rs.getString(1), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(2), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(3), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(4), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(5), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(6), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(7), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(8), CenterAlignVO));
					rowList.add(new StyledData(year, CenterAlignVO));
					rowList.add(new StyledData(rs.getString(10), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(11), CenterAlignVO));
					rowList.add(new StyledData(rs.getString(12), CenterAlignVO));
					dataList.add(rowList);

					counter = counter + 1;
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
				e.printStackTrace();
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
