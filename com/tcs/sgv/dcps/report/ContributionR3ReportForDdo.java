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

import java.math.BigDecimal;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 March 21, 2011
 */

public class ContributionR3ReportForDdo extends DefaultReportDataFinder {
	private static final Logger gLogger = Logger
			.getLogger(ContributionR3ReportForDdo.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;

	public Collection findReportData(ReportVO report, Object criteria)
			throws ReportException {

		report.getLangId();

		report.getLocId();
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
			smt1 = con.createStatement();
			smt2 = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria)
					.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			LoginDetails objLoginDetails = (LoginDetails) sessionKeys
					.get("loginDetails");
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
					serviceLocator.getSessionFactory());
			Long lLngPostId = objLoginDetails.getLoggedInPost().getPostId();
			String lStrDdoCode = lObjDcpsCommonDAO.getDdoCodeForDDO(lLngPostId);
			loginDetail.get("locationId");

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
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_MEDIUM);
			rowsFontsVO[5] = new StyleVO();
			rowsFontsVO[5].setStyleId(IReportConstants.REPORT_PAGINATION);
			rowsFontsVO[5].setStyleValue("NO");

			StyleVO[] RightAlignVO = new StyleVO[2];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] withBorder = new StyleVO[1];
			withBorder[0] = new StyleVO();
			withBorder[0].setStyleId(IReportConstants.BORDER);
			withBorder[0]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_MEDIUM);

			StyleVO[] boldVO = new StyleVO[2];
			boldVO[0] = new StyleVO();
			boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldVO[1] = new StyleVO();
			boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldVO[1].setStyleValue("10");

			String StrSqlQuery = "";

			if (report.getReportCode().equals("700044")) {

				DateFormat dateFormat = new SimpleDateFormat("dd MMM,yyyy");
				DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat dateFormat3 = new SimpleDateFormat("dd MMM yyyy");
				DateFormat dateFormat4 = new SimpleDateFormat("dd/MM/yy");
				DateFormat dateFormat5 = new SimpleDateFormat(
						"dd/MM/yy :hh:mm:ss");

				Date date = new Date();
				dateFormat.format(date);

				Long treasuryId = Long.parseLong(lObjDcpsCommonDAO
						.getTreasuryCodeForDDO(lStrDdoCode));

				String dcpsEmpId = (String) report
						.getParameterValue("dcpsEmpId");
				Long yearId = Long.valueOf((String) report
						.getParameterValue("yearId"));

				String treasuryName = lObjDcpsCommonDAO
						.getTreasuryShortNameForDDO(lStrDdoCode);
				String year = lObjDcpsCommonDAO.getFinYearForYearId(yearId);
				MstEmp objEmpData = lObjDcpsCommonDAO.getEmpVOForEmpId(Long
						.valueOf(dcpsEmpId));
				String dcpsId = objEmpData.getDcpsId().trim();
				String empName = objEmpData.getName();
				Long empId = objEmpData.getDcpsEmpId();
				Date DOB = objEmpData.getDob();
				String basicPay = objEmpData.getBasicPay().toString();
				String ddoName = lObjDcpsCommonDAO
						.getDdoNameForCode(lStrDdoCode);
				String payComm = objEmpData.getPayCommission();
				List lstYearDates = lObjDcpsCommonDAO
						.getDatesFromFinYearId(yearId);
				Object[] objDates = (Object[]) lstYearDates.get(0);
				Date startDate = (Date) objDates[0];
				Date endDate = (Date) objDates[1];
				String interestRate = lObjDcpsCommonDAO
						.getCurrentInterestRate();
				Float daRate = lObjDcpsCommonDAO.getCurrentDARate(payComm);
				Float DP = null;
				if (payComm.equals("700015")) {
					DP = new Float(Double.parseDouble(basicPay) * 50 / 100);
					daRate = new Float(Double.parseDouble(basicPay) * daRate
							/ 100);
				} else {
					DP = new Float(0);
					daRate = new Float((Double.parseDouble(basicPay) + DP)
							* daRate / 100);
				}

				report.setStyleList(rowsFontsVO);

				String header1 = "<center><b>" + "<font size=\"1.2px\"> "
						+ "FORM - R- 3" + "</font></b></center>";
				String header2 = "<center><b>"
						+ "<font size=\"0.8px\"> "
						+ "(As referred to in para no.18 & 23 of Government Resolution, Finance Department 1007/18/SER- 4, dated 7 July, 2007 )"
						+ "</font></b></center>";
				String header3 = "<center><b>" + "<font size=\"0.8px\"> "
						+ "OFFICE OF THE STATE RECORD KEEPING AGENCY, MUMBAI"
						+ "</font></b></center>";
				String header4 = "<center><b>"
						+ "<font size=\"1px\"> "
						+ "Statement of Account in respect of contributions under the D.C.P.Scheme For the Year "
						+ year + "</font></b></center>";

				String additionalHeader = header1 + header2 + header3 + header4;
				report.setAdditionalHeader(additionalHeader);

				ArrayList rowList = new ArrayList();

				rowList.add("<b>Name of the Employee : " + empName + space(15)
						+ " Pension Account Number : " + dcpsId + "</b>");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("<b>Birth Date : "
						+ dateFormat2.format(DOB).toString() + space(25)
						+ "Treasury : (" + treasuryId.toString() + ") "
						+ treasuryName + space(20) + "Rate of Interest : "
						+ interestRate + " %</b>");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("<b>DDO : " + ddoName + "</b>");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("<b>From (Month) : "
						+ dateFormat3.format(startDate).toString() + space(15)
						+ "To (Month) : "
						+ dateFormat3.format(endDate).toString() + "</b>");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("<b>Basic Pay :" + basicPay + space(15) + "D.P. :"
						+ DP + space(15) + "D.A. :" + Math.round(daRate)
						+ "</b>");
				dataList.add(rowList);

				tr = new ArrayList();

				
				
				if(yearId < 25)
				{
					StringBuilder SBQuery = new StringBuilder();

				//Added by Ashish for correcting R3 report from old table


				SBQuery.append(" SELECT temp.OPEN_EMPL_CONTRIB  as open_employee,temp.OPEN_EMPLR_CONTRIB as open_employer,temp.OPEN_INT as open_int FROM TEMPEMPR3 temp ");
				SBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC=temp.FIN_YEAR  ");
				SBQuery.append(" WHERE temp.EMP_ID_NO ='"+dcpsId+"' AND fin.FIN_YEAR_ID="+yearId+" ");
				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);

				//Ended by ashish for correcting R3 report from old table			

				String openBalanceEmp=null;
				String openBalanceEmplr=null;
				Double balanceEmp=0d;
				Double balanceEmplr=0d;
				Double contribEmplr=0d;
				Double contribEmp=0d;


				if (rs.next()) {

					openBalanceEmp = rs.getString("open_employee");
					openBalanceEmplr = rs.getString("open_employer");
					balanceEmp = Double.parseDouble(openBalanceEmp);
					balanceEmplr = Double.parseDouble(openBalanceEmplr);

					tr = new ArrayList();
					td = new ArrayList();
					td.add(new StyledData(year+"(O. B.)",boldVO));
					td.add(new StyledData(rs.getString("open_employee"),boldVO));
					td.add(new StyledData(rs.getString("open_employer"),boldVO));
					td.add(new StyledData("O.B.Int",boldVO));
					td.add(new StyledData( rs.getString("open_int"),boldVO));
					tr.add(td);

				}
				else
				{
					tr = new ArrayList();
					td = new ArrayList();
					td.add(new StyledData(year + "(O. B.)",boldVO));
					td.add(new StyledData( "0.0" ,boldVO));
					td.add(new StyledData( "0.0" , boldVO));
					td.add(new StyledData("O.B.Int",boldVO));
					td.add(new StyledData("0.0" ,boldVO));
					tr.add(td);


				}
				String[] arrYear = year.split("-");
				String[] arrMonths = new String[12];

				arrMonths[0] = "JAN-" + arrYear[1];
				arrMonths[1] = "FEB-" + arrYear[1];
				arrMonths[2] = "MAR-" + arrYear[1];
				arrMonths[3] = "APR-" + arrYear[0];
				arrMonths[4] = "MAY-" + arrYear[0];
				arrMonths[5] = "JUN-" + arrYear[0];
				arrMonths[6] = "JUL-" + arrYear[0];
				arrMonths[7] = "AUG-" + arrYear[0];
				arrMonths[8] = "SEP-" + arrYear[0];
				arrMonths[9] = "OCT-" + arrYear[0];
				arrMonths[10] = "NOV-" + arrYear[0];
				arrMonths[11] = "DEC-" + arrYear[0];


				//ashish start calculate april contri

				//Added by Ashish for correcting R3 report from old table

				SBQuery = new StringBuilder();
				SBQuery.append(" SELECT nvl(cast(sum(temp.CUR_EMPL_CONTRIB) as bigint),0) as EmpContribution,nvl(cast(sum(temp.CUR_EMPLR_CONTRIB)as bigint),0) as EmplrContribution  FROM TEMPR3 temp inner join SGVC_FIN_YEAR_MST fin" 
						+" on fin.FIN_YEAR_DESC=temp.FIN_YEAR" 
						+" where temp.EMP_ID_NO='"+dcpsId+"' "
						+" and temp.PAY_MONTH=4 and fin.FIN_YEAR_ID="+yearId+" ");


				double Pcm=0d;
				StrSqlQuery = SBQuery.toString();
				rs1 = smt1.executeQuery(StrSqlQuery);
				td = new ArrayList();
				td.add(arrMonths[3]);

				if (rs1.next()) {
					Double contribEmp1=0d;
					contribEmplr = 0d;

					ContributionR3Report cr3r=new ContributionR3Report();
					Pcm=cr3r.preContriMarch(empId,yearId,dcpsId);

					contribEmp1=Double.parseDouble(rs1.getString("EmpContribution"));

					//	lStrPostEmpContriDoneOrNot = rs1.getString("EMPLOYER_CONTRI_FLAG");
					//rs1.getString.("status")

					/*if(lStrPostEmpContriDoneOrNot != null)
					{
						if(lStrPostEmpContriDoneOrNot.trim().equals("Y"))
						{

						}
					}*/

					gLogger.info("  contri of april:"+rs1.getString("EmpContribution"));
					contribEmplr = Double.parseDouble(rs1.getString("EmplrContribution"))+Pcm;

					contribEmp=Double.parseDouble(rs1.getString("EmpContribution"))+Pcm;
					balanceEmp = balanceEmp + contribEmp;
					gLogger.info("Total  contri of april:"+contribEmp);
					balanceEmplr = balanceEmplr + contribEmplr;
					//td.add(rs1.getString("contribution"));
					td.add(nosci(contribEmp).toString());
					td.add(nosci(contribEmplr).toString());
					td.add(nosci(balanceEmp).toString());
					td.add(nosci(balanceEmplr).toString());

					tr.add(td);

				} else {
					td.add("0**");
					td.add("0");
					td.add(nosci(balanceEmp).toString());
					td.add(nosci(balanceEmplr).toString());
					tr.add(td);
				}

				//ashish end calulate april contri

				for (int i = 5; i <= 12; i++) {
					SBQuery = new StringBuilder();
					/*	SBQuery
					.append(" SELECT sum(contribution) as contribution,EMPLOYER_CONTRI_FLAG,status FROM trn_dcps_contribution WHERE dcps_emp_id = "
							+ empId 
							+ " AND REG_STATUS = 1"
							+ " AND STATUS in ('H','A','G') "
							+ " AND FIN_YEAR_ID = "
							+ yearId + " AND month_id = " + i
							+ " group by EMPLOYER_CONTRI_FLAG,MONTH_ID,status  ");*/


					SBQuery.append(" SELECT nvl(cast(sum(temp.CUR_EMPL_CONTRIB) as bigint),0) as EmpContribution,nvl(cast(sum(temp.CUR_EMPLR_CONTRIB)as bigint),0) as EmplrContribution  FROM TEMPR3 temp inner join SGVC_FIN_YEAR_MST fin" 
							+" on fin.FIN_YEAR_DESC=temp.FIN_YEAR" 
							+" where temp.EMP_ID_NO='"+dcpsId+"' "
							+" and temp.PAY_MONTH="+i 
							+" and fin.FIN_YEAR_ID="+yearId+" ");



					StrSqlQuery = SBQuery.toString();
					rs1 = smt1.executeQuery(StrSqlQuery);
					td = new ArrayList();
					td.add(arrMonths[i - 1]);

					if (rs1.next()) {

						contribEmplr = 0d;
						balanceEmp = balanceEmp	+ Float.parseFloat(rs1.getString("EmpContribution"));
						/*
						lStrPostEmpContriDoneOrNot = rs1.getString("EMPLOYER_CONTRI_FLAG");

						if(lStrPostEmpContriDoneOrNot != null)
						{
							if(lStrPostEmpContriDoneOrNot.trim().equals("Y"))
							{
								contribEmplr = Double.parseDouble(rs1.getString("contribution"));
							}
						}
						 */
						contribEmplr = Double.parseDouble(rs1.getString("EmplrContribution"));
						balanceEmplr = balanceEmplr + contribEmplr;
						td.add(rs1.getString("EmpContribution"));
						td.add(nosci(contribEmplr).toString());
						td.add(nosci(balanceEmp).toString());
						td.add(nosci(balanceEmplr).toString());

						tr.add(td);

					} else {
						td.add("0**");
						td.add("0");
						td.add(nosci(balanceEmp).toString());
						td.add(nosci(balanceEmplr).toString());
						tr.add(td);
					}
				}
				// Loop exe for starting two months and printing march contri as 0
				for (int i = 1; i <= 3; i++) {
					//run for jan and feb
					if(i!=3){
						SBQuery = new StringBuilder();
						/*	SBQuery
						.append("SELECT sum(contribution) as contribution,EMPLOYER_CONTRI_FLAG,status FROM trn_dcps_contribution WHERE dcps_emp_id = "
								+ empId
								+ " AND REG_STATUS = 1"
								+ " AND STATUS in ('H','A','G') "
								+ " AND FIN_YEAR_ID = "
								+ yearId + " AND month_id = " + i
								+ " group by EMPLOYER_CONTRI_FLAG,MONTH_ID,status  ");*/




						SBQuery.append(" SELECT nvl(cast(sum(temp.CUR_EMPL_CONTRIB) as bigint),0) as EmpContribution,nvl(cast(sum(temp.CUR_EMPLR_CONTRIB)as bigint),0) as EmplrContribution FROM TEMPR3 temp inner join SGVC_FIN_YEAR_MST fin" 
								+" on fin.FIN_YEAR_DESC=temp.FIN_YEAR" 
								+" where temp.EMP_ID_NO='"+dcpsId+"' "
								+" and temp.PAY_MONTH="+i 
								+" and fin.FIN_YEAR_ID="+yearId+" ");

						StrSqlQuery = SBQuery.toString();
						rs1 = smt1.executeQuery(StrSqlQuery);
						td = new ArrayList();
						td.add(arrMonths[i - 1]);
						if (rs1.next()) {

							contribEmplr = 0d;

							balanceEmp = balanceEmp + Float.parseFloat(rs1.getString("EmpContribution"));
							/*
							lStrPostEmpContriDoneOrNot = rs1.getString("EMPLOYER_CONTRI_FLAG");

							if(lStrPostEmpContriDoneOrNot != null)
							{
								if(lStrPostEmpContriDoneOrNot.trim().equals("Y"))
								{
									contribEmplr = Double.parseDouble(rs1.getString("contribution"));
								}
							}*/

							contribEmplr = Double.parseDouble(rs1.getString("EmplrContribution"));
							balanceEmplr = balanceEmplr + contribEmplr;
							td.add(rs1.getString("EmpContribution"));
							td.add(nosci(contribEmplr).toString());
							td.add(nosci(balanceEmp).toString());
							td.add(nosci(balanceEmplr).toString());

							tr.add(td);

						} else {
							td.add("0**");
							td.add("0");
							td.add(nosci(balanceEmp).toString());
							td.add(nosci(balanceEmplr).toString());
							tr.add(td);
						}

					}
					// run for march
					else
					{
						SBQuery = new StringBuilder();
						/*	SBQuery
						.append("SELECT sum(contribution) as contribution,EMPLOYER_CONTRI_FLAG,status FROM trn_dcps_contribution WHERE dcps_emp_id = "
								+ empId
								+ " AND REG_STATUS = 1"
								+ " AND STATUS in ('H','A','G') "
								+ " AND FIN_YEAR_ID = "
								+ yearId + " AND month_id = " + i
								+ " group by EMPLOYER_CONTRI_FLAG,MONTH_ID,status  ");*/


						SBQuery.append(" SELECT  nvl(cast(sum(temp.CUR_EMPL_CONTRIB) as bigint),0) as EmpContribution,nvl(cast(sum(temp.CUR_EMPLR_CONTRIB)as bigint),0) as EmplrContribution  FROM TEMPR3 temp inner join SGVC_FIN_YEAR_MST fin" 
								+" on fin.FIN_YEAR_DESC=temp.FIN_YEAR" 
								+" where temp.EMP_ID_NO='"+dcpsId+"' "
								+" and temp.PAY_MONTH="+i 
								+" and fin.FIN_YEAR_ID="+yearId+" ");



						StrSqlQuery = SBQuery.toString();
						rs1 = smt1.executeQuery(StrSqlQuery);
						td = new ArrayList();
						td.add(arrMonths[i - 1]);
						if (rs1.next()) {

							contribEmplr = 0d;

							//balanceEmp = balanceEmp + Float.parseFloat(rs1.getString("EmpContribution"));

							/*lStrPostEmpContriDoneOrNot = rs1.getString("EMPLOYER_CONTRI_FLAG");

							if(lStrPostEmpContriDoneOrNot != null)
							{
								if(lStrPostEmpContriDoneOrNot.trim().equals("Y"))
								{
									contribEmplr = Double.parseDouble(rs1.getString("contribution"));
								}
							}*/
							contribEmplr = Double.parseDouble(rs1.getString("EmplrContribution"));
							//	balanceEmplr = balanceEmplr + contribEmplr;
							td.add("0");
							td.add("0");
							td.add(nosci(balanceEmp).toString());
							td.add(nosci(balanceEmplr).toString());

							tr.add(td);

						} else {
							td.add("0**");
							td.add("0");
							td.add(nosci(balanceEmp).toString());
							td.add(nosci(balanceEmplr).toString());
							tr.add(td);
						}

					}
				}

				SBQuery = new StringBuilder();

				/*		SBQuery.append("SELECT contrib_employee,contrib_employer,contrib_tier2,int_contrb_employee, int_contrb_employer,close_employee,close_employer,close_net FROM mst_dcps_contribution_yearly WHERE dcps_id = '"
						+ dcpsId + "' AND year_id=" + yearId);
				StrSqlQuery = SBQuery.toString();*/

				//Added by Ashish for correcting R3 report from old table		

				SBQuery.append("SELECT cast(sum(temp.CUR_EMPL_CONTRIB) as DECIMAL(16,2)) as contrib_employee ,cast((tr3.OPEN_EMPLR_CONTRIB+sum(temp.CUR_EMPLR_CONTRIB)+tr3.INT_EMPL_CONTRIB) as DECIMAL(16,2))  as closeEmplrBal,cast((tr3.OPEN_EMPLR_CONTRIB+sum(temp.CUR_EMPLR_CONTRIB)+tr3.INT_EMPL_CONTRIB+tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB+tr3.OPEN_EMPL_CONTRIB+tr3.tier2 +tr3.OPEN_INT +sum(temp.CUR_EMPL_CONTRIB)) as DECIMAL(16,2)) as Closenet,cast(sum(temp.CUR_EMPLR_CONTRIB) as DECIMAL(16,2)) as contrib_employer, cast((tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB+tr3.OPEN_EMPL_CONTRIB+tr3.tier2 +sum(temp.CUR_EMPL_CONTRIB))as DECIMAL(16,2)) as  CloseBal, tr3.tier2 as tier2,cast((tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB) as DECIMAL(16,2)) as TotalEmpInt ,tr3.INT_TIER2_CONTRIB as INT_TIER2_CONTRIB,tr3.INT_EMPL_CONTRIB as int_contrb_employee,tr3.INT_EMPL_CONTRIB as int_contrb_employer,tr3.OPEN_INT as open_net  FROM TEMPR3 temp inner join TEMPEMPR3 tr3 on temp.emp_id_no=tr3.emp_id_no    "
						+ "   inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC=temp.fin_year and fin.FIN_YEAR_DESC=tr3.fin_year   "
						+ "  where fin.FIN_YEAR_ID="+yearId+" and temp.EMP_ID_NO='"+dcpsId+"' "
						+ "    group by tr3.INT_EMPL_CONTRIB,tr3.INT_EMPL_CONTRIB,tr3.TIER2,tr3.INT_TIER2_CONTRIB,tr3.OPEN_EMPLR_CONTRIB,tr3.OPEN_EMPL_CONTRIB,tr3.OPEN_INT  " );
				StrSqlQuery = SBQuery.toString();

				//Ended by Ashish for correcting R3 report from old table					


				rs2 = smt2.executeQuery(StrSqlQuery);
				//	rs2.next();

				if (rs2.next()) 
				{
					Double TotalEmp = Double.parseDouble(rs2
							.getString("contrib_employee"))
							+ Double
							.parseDouble(rs2.getString("tier2"));
					Double TotalEmplr = Double.parseDouble(rs2
							.getString("contrib_employer"));

					td = new ArrayList();
					td.add(new StyledData("Total Amount(A)",boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData(rs2.getString("contrib_employee"),boldVO));
					td.add(new StyledData(rs2.getString("contrib_employer"),boldVO));
					tr.add(td);

					td = new ArrayList();
					td.add(new StyledData("Tier2(B) :" ,boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData(rs2.getString("tier2") ,boldVO));
					td.add("");
					tr.add(td);




					td = new ArrayList();
					td.add(new StyledData("Total Amount(A+B)",boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData(nosci(TotalEmp).toString(),boldVO ));
					td.add(new StyledData(nosci(TotalEmplr).toString(),boldVO));
					tr.add(td);



					td = new ArrayList();
					td.add(new StyledData("Interest(C)",boldVO));
					td.add("");
					td.add("");
					td.add("<b>"+
							rs2.getString("TotalEmpInt")
							+ "</b>");
					td.add("<b>" + rs2.getString("int_contrb_employer")
							+ "</b>");
					tr.add(td);




					td = new ArrayList();
					td.add("<b>Closing Balance</b>");
					td.add("");
					td.add("");
					td.add("<b>" + rs2.getString("CloseBal") + "</b>");
					td.add("<b>" + rs2.getString("closeEmplrBal") + "</b>");
					tr.add(td);

					/*	rptTd = new TabularData(tr);
					RptVo = reportsDao.getReport("700017", report.getLangId(),
							report.getLocId());

					(rptTd).setRelatedReport(RptVo);

					rptList1 = new ArrayList();
					rptList1.add(rptTd);
					dataList.add(rptList1);


					//String Close_net=(rs2.getString("open_net"))+Double.parseDouble(rs2.getString("CloseBal"))+Double.parseDouble(rs2.getString("closeEmplrBal"));
					gLogger.info("Closenet amount is "+rs2.getString("Closenet"));

					String lStrTotAmtInWords = EnglishDecimalFormat.convertWithSpace(new BigDecimal(Math.round(Double.parseDouble(rs2.getString("Closenet")))));

					gLogger.info("Closenet amount in words is "+lStrTotAmtInWords);


					rowList.add("<b>Total Amount standing to credit : <font face = 'Rupee Foradian' size = '2px'>`</font> "
							+ Math.round(Double.parseDouble(rs2.getString("Closenet")))
									+ space(5)
									+ "( Rupees in Words : "
									+ lStrTotAmtInWords + "</b>)");
					dataList.add(rowList);*/





					rptTd = new TabularData(tr);
					RptVo = reportsDao.getReport("700017", report.getLangId(), 
							report.getLocId());

					rptTd.setRelatedReport(RptVo);

					rptList1 = new ArrayList();
					rptList1.add(rptTd);
					dataList.add(rptList1);

					String lStrTotAmtInWords = 
						EnglishDecimalFormat.convertWithSpace(new BigDecimal(Math.round(
								Double.parseDouble(rs2.getString("Closenet")))));
					rowList = new ArrayList();
					rowList.add
					("<b>Total Amount standing to credit : <font face = 'Rupee Foradian' size = '2px'>`</font> " + 
							Math.round(Double.parseDouble(rs2.getString
									("Closenet"))) + 
									space(5) + 
									"( Rupees in Words : " + 
									lStrTotAmtInWords + "</b>)");
					dataList.add(rowList);

				}
				else
				{
					td = new ArrayList();
					td.add(new StyledData("Total Amount(A)",boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData(nosci(balanceEmp).toString(),boldVO));
					td.add(new StyledData(nosci(balanceEmplr).toString(),boldVO));
					tr.add(td);

					td = new ArrayList();
					td.add(new StyledData("Tier2(B) :",boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData("0.0" ,boldVO));
					td.add("");
					tr.add(td);


					td = new ArrayList();
					td.add(new StyledData("Total Amount(A+B)",boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData(nosci(balanceEmp).toString() ,boldVO));
					td.add(new StyledData(nosci(balanceEmplr).toString() ,boldVO));
					tr.add(td);


					td = new ArrayList();
					td.add(new StyledData("Interest(C)",boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData("0",boldVO));
					//--------Remaning--------------
					td.add(new StyledData( "0",boldVO));
					tr.add(td);

					td = new ArrayList();
					td.add(new StyledData("Closing Balance",boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData(nosci(balanceEmp).toString(),boldVO));
					td.add(new StyledData(nosci(balanceEmplr).toString(),boldVO));
					tr.add(td);

					rptTd = new TabularData(tr);
					RptVo = reportsDao.getReport("700017", report.getLangId(),
							report.getLocId());

					(rptTd).setRelatedReport(RptVo);

					rptList1 = new ArrayList();
					rptList1.add(rptTd);
					dataList.add(rptList1);




					String lStrTotAmtInWords = EnglishDecimalFormat
					.convertWithSpace(new BigDecimal(Math.round(balanceEmp + balanceEmplr)));
					rowList = new ArrayList();
					rowList.add(new StyledData("Total Amount standing to credit : <font face = 'Rupee Foradian' size = '2px'>`</font> "
							+ Math.round((Math.round(balanceEmp + balanceEmplr)))
							+ space(5)
							+ "( Rupees in Words : "
							+ lStrTotAmtInWords+")",boldVO));
					dataList.add(rowList);



				}

				// changes not reflect
				rowList = new ArrayList();
				rowList.add("Certified that the details shown above are correct as per the information received in this office. However, the closing balance shown above will be subject to final adjustments on account of excess credit or excess interest, if any, which may come to the notice of this Office at a later date."
						+ newline
						+ newline
						+ "Place: Mumbai"
						+ newline
						+ "Date:   "
						+ dateFormat4.format(date)
						+ space(50)
						+ "**Shows Missing Credits" );

				//changes not reflect
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(" Office-in-Charge - Treasury Officer " 
						+ newline
						+ "(" + treasuryId + ") " + treasuryName , RightAlignVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
				.add("Please verify the details of Shri/Smt/Kum "
						+ empName
						+ ". If there is any discrepancy; please inform the Treasury Officer concerned with Vr.No.,Date, Amount of the bill alongwith a copy of the schedule.");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
				.add("This is a Computer Generated Statement, Needs No Signature");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(dateFormat5.format(date),
						RightAlignVO));
				dataList.add(rowList);}
				else
				{
					StringBuilder SBQuery = new StringBuilder();
					SBQuery
					.append("SELECT open_employee as open_employee,open_employer as open_employer,open_int as open_int FROM mst_dcps_contribution_yearly WHERE dcps_id = '"
							+ dcpsId + "' AND year_id=" + yearId);
			StrSqlQuery = SBQuery.toString();
			rs = smt.executeQuery(StrSqlQuery);
			if (rs.next()) {
				String openBalanceEmp = rs.getString("open_employee");
				String openBalanceEmplr = rs.getString("open_employer");
				Double balanceEmp = Double.parseDouble(openBalanceEmp);
				Double balanceEmplr = Double.parseDouble(openBalanceEmplr);
				
				Double contribEmplr = 0d;
				String lStrPostEmpContriDoneOrNot = null;

				tr = new ArrayList();
				td = new ArrayList();
				td.add("<b>" + year + "(O. B.)</b>");
				td.add("<b>" + rs.getString("open_employee") + "</b>");
				td.add("<b>" + rs.getString("open_employer") + "</b>");
				td.add("<b>" + "O.B.Int" + "</b>");
				td.add("<b>" + rs.getString("open_int") + "</b>");
				tr.add(td);

				String[] arrYear = year.split("-");
				String[] arrMonths = new String[12];

				arrMonths[0] = "JAN-" + arrYear[1];
				arrMonths[1] = "FEB-" + arrYear[1];
				arrMonths[2] = "MAR-" + arrYear[1];
				arrMonths[3] = "APR-" + arrYear[0];
				arrMonths[4] = "MAY-" + arrYear[0];
				arrMonths[5] = "JUN-" + arrYear[0];
				arrMonths[6] = "JUL-" + arrYear[0];
				arrMonths[7] = "AUG-" + arrYear[0];
				arrMonths[8] = "SEP-" + arrYear[0];
				arrMonths[9] = "OCT-" + arrYear[0];
				arrMonths[10] = "NOV-" + arrYear[0];
				arrMonths[11] = "DEC-" + arrYear[0];

				for (int i = 4; i <= 12; i++) {
					SBQuery = new StringBuilder();
					SBQuery
							.append("SELECT contribution,EMPLOYER_CONTRI_FLAG FROM trn_dcps_contribution WHERE dcps_emp_id = "
									+ empId
									+ " AND REG_STATUS = 1"
									+ " AND STATUS = 'H' "
									+ " AND FIN_YEAR_ID = "
									+ yearId + " AND month_id = " + i);

					StrSqlQuery = SBQuery.toString();
					rs1 = smt1.executeQuery(StrSqlQuery);
					td = new ArrayList();
					td.add(arrMonths[i - 1]);
					if (rs1.next()) {
						
						contribEmplr = 0d;
						balanceEmp = balanceEmp	+ Float.parseFloat(rs1.getString("contribution"));
						
						lStrPostEmpContriDoneOrNot = rs1.getString("EMPLOYER_CONTRI_FLAG");
						
						if(lStrPostEmpContriDoneOrNot != null)
						{
							if(lStrPostEmpContriDoneOrNot.trim().equals("Y"))
							{
								contribEmplr = Double.parseDouble(rs1.getString("contribution"));
							}
						}
						
						balanceEmplr = balanceEmplr + contribEmplr;
						
						td.add(rs1.getString("contribution"));
						td.add(contribEmplr.toString());
						td.add(balanceEmp.toString());
						td.add(balanceEmplr.toString());

						tr.add(td);

					} else {
						td.add("0**");
						td.add("0");
						td.add(balanceEmp.toString());
						td.add(balanceEmplr.toString());
						tr.add(td);
					}
				}

				for (int i = 1; i <= 3; i++) {
					
					SBQuery = new StringBuilder();
					SBQuery
							.append("SELECT contribution,EMPLOYER_CONTRI_FLAG FROM trn_dcps_contribution WHERE dcps_emp_id = "
									+ empId
									+ " AND REG_STATUS = 1"
									+ " AND STATUS = 'H' "
									+ " AND FIN_YEAR_ID = "
									+ yearId + " AND month_id = " + i);

					StrSqlQuery = SBQuery.toString();
					rs1 = smt1.executeQuery(StrSqlQuery);
					td = new ArrayList();
					td.add(arrMonths[i - 1]);
					
					if (rs1.next()) {
						
						contribEmplr = 0d;
						
						balanceEmp = balanceEmp + Float.parseFloat(rs1.getString("contribution"));
						
						lStrPostEmpContriDoneOrNot = rs1.getString("EMPLOYER_CONTRI_FLAG");
						
						if(lStrPostEmpContriDoneOrNot != null)
						{
							if(lStrPostEmpContriDoneOrNot.trim().equals("Y"))
							{
								contribEmplr = Double.parseDouble(rs1.getString("contribution"));
							}
						}
						
						balanceEmplr = balanceEmplr	+ contribEmplr;
						
						td.add(rs1.getString("contribution"));
						td.add(contribEmplr.toString());
						td.add(balanceEmp.toString());
						td.add(balanceEmplr.toString());

						tr.add(td);

					} else {
						td.add("0**");
						td.add("0");
						td.add(balanceEmp.toString());
						td.add(balanceEmplr.toString());
						tr.add(td);
					}
				}

				SBQuery = new StringBuilder();

				SBQuery
						.append("SELECT contrib_employee,contrib_employer,contrib_tier2,int_contrb_employee, int_contrb_employer,close_employee,close_employer,close_net FROM mst_dcps_contribution_yearly WHERE dcps_id = '"
								+ dcpsId + "' AND year_id=" + yearId);
				StrSqlQuery = SBQuery.toString();
				rs2 = smt2.executeQuery(StrSqlQuery);
				rs2.next();

				Double TotalEmp = Double.parseDouble(rs2
						.getString("contrib_employee"))
						+ Double
								.parseDouble(rs2.getString("contrib_tier2"));
				Double TotalEmplr = Double.parseDouble(rs2
						.getString("contrib_employer"));

				td = new ArrayList();
				td.add("<b>" + "Total Amount(A)" + "</b>");
				td.add("");
				td.add("");
				td.add("<b>" + rs2.getString("contrib_employee") + "</b>");
				td.add("<b>" + rs2.getString("contrib_employer") + "</b>");
				tr.add(td);

				td = new ArrayList();
				td.add("<b>" + "Tier2(B)" + "</b>");
				td.add("");
				td.add("");
				td.add("<b>" + rs2.getString("contrib_tier2") + "</b>");
				td.add("");
				tr.add(td);

				td = new ArrayList();
				td.add("<b>" + "Total Amount(A+B)" + "</b>");
				td.add("");
				td.add("");
				td.add("<b>" + TotalEmp.toString() + "</b>");
				td.add("<b>" + TotalEmplr.toString() + "</b>");
				tr.add(td);

				td = new ArrayList();
				td.add("<b>" + "Interest(C)" + "</b>");
				td.add("");
				td.add("");
				td.add("<b>" + rs2.getString("int_contrb_employee")
						+ "</b>");
				td.add("<b>" + rs2.getString("int_contrb_employer")
						+ "</b>");
				tr.add(td);

				td = new ArrayList();
				td.add("<b>" + "Closing Balance");
				td.add("");
				td.add("");
				td.add("<b>" + rs2.getString("close_employee") + "</b>");
				td.add("<b>" + rs2.getString("close_employer") + "</b>");
				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("700017", report.getLangId(),
						report.getLocId());

				(rptTd).setRelatedReport(RptVo);

				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				dataList.add(rptList1);

				String lStrTotAmtInWords = EnglishDecimalFormat
						.convertWithSpace(new BigDecimal(Math.round(Double
								.parseDouble(rs2.getString("close_net")))));
				rowList = new ArrayList();
				rowList
						.add("<b>Total Amount standing to credit : <font face = 'Rupee Foradian' size = '2px'>`</font> "
								+ Math.round(Double.parseDouble(rs2
										.getString("close_net")))
								+ space(5)
								+ "( Rupees in Words : "
								+ lStrTotAmtInWords + "</b>)");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add("Certified that the details shown above are correct as per the information received in this office. However, the closing balance shown above will be subject to final adjustments on account of excess credit or excess interest, if any, which may come to the notice of this Office at a later date."
								+ newline
								+ newline
								+ "<b>Place: Mumbai</b>"
								+ newline
								+ "<b>Date:   "
								+ dateFormat4.format(date) + space(50) + "<i>**Shows Missing Credits</i>" 
								+ "</b>");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"<b>Office-in-Charge - Treasury Officer" + newline
								+ "(" + treasuryId + ") " + treasuryName
								+ "</b>", RightAlignVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add("Please verify the details of Shri/Smt/Kum "
								+ empName
								+ ". If there is any discrepancy;please inform the Treasury Officer concerned with Vr.No.,Date,");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add("Amount of the bill alongwith a copy of the schedule.");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
						.add("This is a Computer Generated Statement, Needs No Signature");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(dateFormat5.format(date),
						RightAlignVO));
				dataList.add(rowList);

			}
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
				e1.printStackTrace();
				gLogger.error("Exception :" + e1, e1);

			}
		}

		return dataList;

	}

	private static String nosci(double d) {
		if (d < 0) {
			return "-" + nosci(-d);
		}
		String javaString = String.valueOf(d);
		int indexOfE = javaString.indexOf("E");
		if (indexOfE == -1) {
			return javaString;
		}
		StringBuffer sb = new StringBuffer();
		if (d > 1) {// big number
			int exp = Integer.parseInt(javaString.substring(indexOfE + 1));
			String sciDecimal = javaString.substring(2, indexOfE);
			int sciDecimalLength = sciDecimal.length();
			if (exp == sciDecimalLength) {
				sb.append(javaString.charAt(0));
				sb.append(sciDecimal);
			} else if (exp > sciDecimalLength) {
				sb.append(javaString.charAt(0));
				sb.append(sciDecimal);
				for (int i = 0; i < exp - sciDecimalLength; i++) {
					sb.append('0');
				}
			} else if (exp < sciDecimalLength) {
				sb.append(javaString.charAt(0));
				sb.append(sciDecimal.substring(0, exp));
				sb.append('.');
				for (int i = exp; i < sciDecimalLength; i++) {
					sb.append(sciDecimal.charAt(i));
				}
			}

			return sb.toString();
		} else {
			// for little numbers use the default or you will
			// loose accuracy

			return 	javaString;
		}

	}
	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	public List getYear(String lStrLangId, String lStrLocId) {
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
					"select * from sgvc_fin_year_mst where lang_id ='"
							+ lStrLangId
							+ "'  and fin_year_code between '2007' and '2011' order by fin_year_code ");

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
				e.printStackTrace();
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrYear;
	}

	public List getAllEmpsUnderDDO(Hashtable otherArgs, String lStrLangId,
			String lStrLocId) {

		List<Object> lLstReturnList = null;
		try {
			Hashtable sessionKeys = (Hashtable) (otherArgs)
					.get(IReportConstants.SESSION_KEYS);
			Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
			Long lLongPostId = null;
			if (loginMap.containsKey("loggedInPost")) {
				lLongPostId = (Long) loginMap.get("loggedInPost");
			}

			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
					ServiceLocator.getServiceLocator().getSessionFactory());
			String lStrddoCode = lObjDcpsCommonDAO
					.getDdoCodeForDDO(lLongPostId);

			if (!lStrddoCode.equals("-1")) {
				StringBuilder sb = new StringBuilder();
				Session lObjSession = ServiceLocator.getServiceLocator()
						.getSessionFactory().getCurrentSession();
				sb.append(" SELECT EM.dcpsEmpId,EM.name FROM MstEmp EM ");
				sb
						.append(" WHERE EM.ddoCode = :ddoCode and EM.dcpsOrGpf = 'Y' and regStatus = 1 order by EM.name");
				Query selectQuery = lObjSession.createQuery(sb.toString());
				selectQuery.setParameter("ddoCode", lStrddoCode);
				List lLstResult = selectQuery.list();
				ComboValuesVO lObjComboValuesVO = null;
				if (lLstResult != null && lLstResult.size() != 0) {
					lLstReturnList = new ArrayList<Object>();
					Object obj[];
					for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
						obj = (Object[]) lLstResult.get(liCtr);
						lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(obj[0].toString());
						lObjComboValuesVO.setDesc(obj[1].toString());
						lLstReturnList.add(lObjComboValuesVO);
					}
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("-- Select --");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is : " + e, e);
		}
		return lLstReturnList;
	}
}
