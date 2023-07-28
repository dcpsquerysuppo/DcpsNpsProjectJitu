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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

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

public class ContributionR3ReportForR3Module extends DefaultReportDataFinder {
	private static final Logger gLogger = Logger
	.getLogger(ContributionR3ReportForR3Module.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");
	//private  StyleVO[] selfCloseVO=null;       

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;
	
	
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

	@SuppressWarnings("unchecked")
	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException {

		report.getLangId();

		report.getLocId();
		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		Statement smt1 = null;
		Statement smt2 = null;
		Statement smt3 = null;
		Statement smt4 = null;
		Statement smt5 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
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
			smt3 = con.createStatement();
			smt4 = con.createStatement();
			smt5 = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria)
			.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

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

			StyleVO[] RightAlignVO = new StyleVO[3];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0]
			             .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			//RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			RightAlignVO[2] = new StyleVO();
			RightAlignVO[2].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			RightAlignVO[2].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);




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


		/*	if (report.getParameterValue("displayOK")!=null && !"".equalsIgnoreCase(report.getParameterValue("displayOK").toString()))
			{
				
				selfCloseVO = new StyleVO[1];
				selfCloseVO[0] = new StyleVO();
				selfCloseVO[0].setStyleId(IReportConstants.REPORT_PAGE_OK_BTN_URL);
				selfCloseVO[0].setStyleValue("javascript:self.close()");
			}*/

			String StrSqlQuery = "";
			String StrSqlQuery1 = "";


			if (report.getReportCode().equals("80000844")) {

				DateFormat dateFormat = new SimpleDateFormat("dd MMM,yyyy");
				DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat dateFormat3 = new SimpleDateFormat("dd MMM yyyy");
				DateFormat dateFormat4 = new SimpleDateFormat("dd/MM/yy");
				DateFormat dateFormat5 = new SimpleDateFormat(
				"dd/MM/yy :hh:mm:ss");
				DateFormat dateFormat6 = new SimpleDateFormat("dd/MM/yyyy");

				Date date = new Date();
				dateFormat.format(date);
				Long treasuryId=0l;
				
				if (report.getParameterValue("treasuryCode")!=null && !"".equalsIgnoreCase(report.getParameterValue("treasuryCode").toString()))
				{
					 treasuryId = Long.parseLong((report.getParameterValue("treasuryCode").toString().trim()));
					System.out.println("Tresury Code is **** "+treasuryId);
				}
			
				
				String ddoCode = (String) report.getParameterValue("ddoCode");
				System.out.println("ddoCode  is **** "+ddoCode);
				String dcpsEmpId = (String) report
				.getParameterValue("dcpsEmpId");
				System.out.println("dcpsEmpId is **** "+dcpsEmpId);
				Long yearId = Long.valueOf((String) report
						.getParameterValue("yearId"));
				System.out.println("yearId is **** "+yearId);
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
						serviceLocator.getSessionFactory());
				String treasuryName="";
				if(ddoCode!=null && !"".equalsIgnoreCase(ddoCode))
				{
					treasuryName = lObjDcpsCommonDAO
					.getTreasuryShortNameForDDO(ddoCode);
				}
				 
				String year = lObjDcpsCommonDAO.getFinYearForYearId(yearId);
				MstEmp objEmpData = lObjDcpsCommonDAO.getEmpVOForEmpId(Long
						.valueOf(dcpsEmpId));
				String dcpsId = objEmpData.getDcpsId().trim();
				String empName = objEmpData.getName();
				Long empId = objEmpData.getDcpsEmpId();
				Date DOB = objEmpData.getDob();
				String basicPay = objEmpData.getBasicPay().toString();
				String ddoName="";
				if(ddoCode!=null && !"".equalsIgnoreCase(ddoCode))
				{
					 ddoName = lObjDcpsCommonDAO.getDdoNameForCode(ddoCode);
				}
				
				String payComm = objEmpData.getPayCommission();
				List lstYearDates = lObjDcpsCommonDAO
				.getDatesFromFinYearId(yearId);
				Object[] objDates = (Object[]) lstYearDates.get(0);
				Date startDate = (Date) objDates[0];
				Date endDate = (Date) objDates[1];
				List interestRates = getInterestRatesForGivenYear(yearId);
				Float daRate = lObjDcpsCommonDAO.getCurrentDARate(payComm);
				Float DP = null;
				if (payComm.equals("700015")) {
					DP = new Float(Double.parseDouble(basicPay) * 50 / 100);
					daRate = new Float(Double.parseDouble(basicPay) * daRate/ 100);
				} else {
					DP = new Float(0);
					daRate = new Float((Double.parseDouble(basicPay) + DP)* daRate / 100);
				}

				String lStrPostEmpContriDoneOrNot = null;

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

				rowList.add(new StyledData( "Name of the Employee : " + empName  ,boldVO));
				rowList.add(new StyledData(" Pension Account Number : " + dcpsId ,boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Birth Date : "
						+ dateFormat2.format(DOB).toString() + space(25)
						+ "Treasury : (" + treasuryId.toString() + ") "
						+ treasuryName ,boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();


				String lStrFromDateInt = "";
				String lStrToDateInt = "";
				Double lDoubleIntRate = null;
				Object[] lObjIntDtls = null;

				for(Integer lInt=0;lInt<interestRates.size();lInt++)
				{
					rowList = new ArrayList();
					lObjIntDtls = (Object[]) interestRates.get(lInt);
					lDoubleIntRate = Double.valueOf(lObjIntDtls[0].toString().trim());
					lDoubleIntRate = Round(lDoubleIntRate,2);
					lStrFromDateInt = dateFormat6.format(dateFormat2.parse(lObjIntDtls[1].toString().trim()));
					lStrToDateInt = dateFormat6.format(dateFormat2.parse(lObjIntDtls[2].toString().trim()));
//					rowList.add(lDoubleIntRate + " ( " + lStrFromDateInt + " to " + lStrToDateInt + " )");
//					dataList.add(rowList);
				}
				
				rowList.add(new StyledData("Interest Rate : "+ lDoubleIntRate + " ( " + lStrFromDateInt + " to " + lStrToDateInt + " )  ",boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("DDO : " + ddoName ,boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("From (Month) : "
						+ dateFormat3.format(startDate).toString() + space(15)
						+ "To (Month) : "
						+ dateFormat3.format(endDate).toString(),boldVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Basic Pay :" + basicPay + space(15) + "D.P. :"
						+ DP + space(15) + "D.A. :" + Math.round(daRate),boldVO));
				dataList.add(rowList);

				tr = new ArrayList();

				StringBuilder SBQuery = new StringBuilder();
				SBQuery.append(" SELECT temp.OPEN_EMPL_CONTRIB  as open_employee,temp.OPEN_EMPLR_CONTRIB as open_employer,temp.OPEN_INT as open_int FROM TEMPEMPR3 temp ");
				SBQuery.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC=temp.FIN_YEAR  ");
				SBQuery.append(" WHERE temp.EMP_ID_NO ='"+dcpsId+"' AND fin.FIN_YEAR_ID="+yearId+" ");
				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);
		

				StringBuilder SBQuery1 = new StringBuilder();
				SBQuery1.append(" SELECT cy.OPEN_EMPLOYEE as open_employee_New, cy.OPEN_EMPLOYER as open_employer_New, cy.OPEN_INT as open_int_New FROM MST_DCPS_CONTRIB_YEARLY_NEW cy ");
				SBQuery1.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID= cy.YEAR_ID  ");
				SBQuery1.append("  WHERE cy.DCPS_ID ='"+dcpsId+"' AND cy.YEAR_ID= "+yearId+" ");
				StrSqlQuery1 = SBQuery1.toString();
				rs3 = smt3.executeQuery(StrSqlQuery1);
				
				String openBalanceEmp=null;
				String openBalanceEmplr=null;
				Double balanceEmp=0d;
				Double balanceEmplr=0d;
				Double contribEmplr=0d;
				Double contribEmp=0d;
				Double openInt = 0d;

				
				String openBalanceEmpNew=null;
				String openBalanceEmplrNew=null;
				Double balanceEmpNew=0d;
				Double balanceEmplrNew=0d;
				Double contribEmplrNew=0d;
				Double contribEmpNew=0d;	
				Double openIntNew = 0d;
				Double contribEmpNeww = 0d;
				Double contribEmpNew1 = 0d;

				
				Double TotalEmpNew=0d;
				Double TotalEmplrNew=0d;
				Double contribEmployeeNew=0d;	
				Double contribEmployerNew = 0d;
				Double tier2New = 0d;

				Double TotalEmpIntNew = 0d;
				Double intContrbEmployerNew = 0d;
			
				Double closeEmplrBalNew = 0d;
				Double CloseBalNew = 0d;

				
				if (rs3.next()) {
					openBalanceEmpNew = rs3.getString("open_employee_New");
					openBalanceEmplrNew = rs3.getString("open_employer_New");
					openIntNew= Double.parseDouble(rs3.getString("open_int_New"));
					balanceEmpNew = Double.parseDouble(openBalanceEmpNew);
					balanceEmplrNew = Double.parseDouble(openBalanceEmplrNew);	
					
				}
				
				if (rs.next()) {

					openBalanceEmp = rs.getString("open_employee");
					openBalanceEmplr = rs.getString("open_employer");
					openInt= Double.parseDouble(rs.getString("open_int"));
					balanceEmp = Double.parseDouble(openBalanceEmp);
					balanceEmplr = Double.parseDouble(openBalanceEmplr);

				
					
					tr = new ArrayList();
					td = new ArrayList();
					td.add(new StyledData(year+"(O. B.)",boldVO));
					td.add(new StyledData(("<p>OLD:- "+openBalanceEmp+"<p>"+"<p> NEW:-  "+openBalanceEmpNew+"</p>"),boldVO));
					td.add(new StyledData(("<p>OLD:- "+openBalanceEmplr+"<p>"+"<p> NEW:-  "+openBalanceEmplrNew+"</p>"),boldVO));
					td.add(new StyledData("O.B.Int",boldVO));
					td.add(new StyledData( ("<p>OLD:- "+openInt+"<p>"+"<p> NEW:-  "+openIntNew+"</p>"),boldVO));
					tr.add(td);

				}
				else
				{
					tr = new ArrayList();
					td = new ArrayList();
					td.add(new StyledData(year + "(O. B.)",boldVO));
					td.add(new StyledData(("<p>OLD:- 0"+"<p>"+"<p> NEW:-  0"+"</p>"),boldVO));
					td.add(new StyledData(("<p>OLD:- 0"+"<p>"+"<p> NEW:-  0"+"</p>"),boldVO));
					td.add(new StyledData("O.B.Int",boldVO));
					td.add(new StyledData(("<p>OLD:- 0"+"<p>"+"<p> NEW:-  0"+"</p>"),boldVO));
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

				
				SBQuery1 = new StringBuilder();
				SBQuery1.append(" SELECT nvl(cast(sum(temp.CONTRIB_EMPLOYEE) as bigint),0) as EmpContributionNew,nvl(cast(sum(temp.CONTRIB_EMPLOYER)as bigint),0) as EmplrContributionNew " 
						+" FROM MST_DCPS_CONTRIB_MONTHLY_NEW temp inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =temp.YEAR_ID " 
						+" where temp.DCPS_ID  ='"+dcpsId+"' "
						+" and temp.PAY_MONTH=4 and fin.FIN_YEAR_ID="+yearId+" ");
				
				double PcmNew=0d;
				StrSqlQuery1 = SBQuery1.toString();
				rs4 = smt4.executeQuery(StrSqlQuery1);
	//			td = new ArrayList();
	//			td.add(arrMonths[3]);
				
				if (rs4.next()) {
					Double contribEmp1New=0d;
					contribEmplrNew = 0d;

					ContributionR3ReportForR3Module cr3rNew=new ContributionR3ReportForR3Module();
					PcmNew=cr3rNew.preContriMarchNew(empId,yearId,dcpsId);

					contribEmp1New=Double.parseDouble(rs4.getString("EmpContributionNew"));

					gLogger.info("  contri of april New : "+rs4.getString("EmpContributionNew"));
					contribEmplrNew = Double.parseDouble(rs4.getString("EmplrContributionNew"))+PcmNew;

					contribEmpNew=Double.parseDouble(rs4.getString("EmpContributionNew"))+PcmNew;
					balanceEmpNew = balanceEmpNew + contribEmpNew;
					gLogger.info("Total  contri of april Newwww : "+contribEmpNew);
					balanceEmplrNew = balanceEmplrNew + contribEmplrNew;
				}
				
				if (rs1.next()) {
					Double contribEmp1=0d;
					contribEmplr = 0d;

					ContributionR3ReportForR3Module cr3r=new ContributionR3ReportForR3Module();
					Pcm=cr3r.preContriMarch(empId,yearId,dcpsId);

					contribEmp1=Double.parseDouble(rs1.getString("EmpContribution"));

					gLogger.info("  contri of april:"+rs1.getString("EmpContribution"));
					contribEmplr = Double.parseDouble(rs1.getString("EmplrContribution"))+Pcm;

					contribEmp=Double.parseDouble(rs1.getString("EmpContribution"))+Pcm;
					balanceEmp = balanceEmp + contribEmp;
					gLogger.info("Total  contri of april:"+contribEmp);
					balanceEmplr = balanceEmplr + contribEmplr;
					//td.add(rs1.getString("contribution"));
					
					td.add(("<p>OLD:- "+(nosci(contribEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(contribEmpNew).toString())+"</p>"));
					td.add(("<p>OLD:- "+(nosci(contribEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(contribEmplrNew).toString())+"</p>"));	
					td.add(("<p>OLD:- "+(nosci(balanceEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmpNew).toString())+"</p>"));
					td.add(("<p>OLD:- "+(nosci(balanceEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmpNew).toString())+"</p>"));
				
//					td.add(nosci(contribEmp).toString());
//					td.add(nosci(contribEmplr).toString());
//					td.add(nosci(balanceEmp).toString());
//					td.add(nosci(balanceEmplr).toString());

					tr.add(td);

				} else {
					td.add(new StyledData(("<p>OLD:- 0**"+"<p>"+"<p> NEW:-  0**"+"</p>"),boldVO));
					td.add(new StyledData(("<p>OLD:- 0"+"<p>"+"<p> NEW:-  0"+"</p>"),boldVO));
					td.add(("<p>OLD:- "+(nosci(balanceEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmpNew).toString())+"</p>"));
					td.add(("<p>OLD:- "+(nosci(balanceEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmplrNew).toString())+"</p>"));
					tr.add(td);
				}

				//ashish end calulate april contri

				for (int i = 5; i <= 12; i++) {
					SBQuery = new StringBuilder();

					SBQuery.append(" SELECT nvl(cast(sum(temp.CUR_EMPL_CONTRIB) as bigint),0) as EmpContribution,nvl(cast(sum(temp.CUR_EMPLR_CONTRIB)as bigint),0) as EmplrContribution  FROM TEMPR3 temp inner join SGVC_FIN_YEAR_MST fin" 
							+" on fin.FIN_YEAR_DESC=temp.FIN_YEAR" 
							+" where temp.EMP_ID_NO='"+dcpsId+"' "
							+" and temp.PAY_MONTH="+i 
							+" and fin.FIN_YEAR_ID="+yearId+" ");

					StrSqlQuery = SBQuery.toString();
					rs1 = smt1.executeQuery(StrSqlQuery);


					
					
					SBQuery1 = new StringBuilder();
					SBQuery1.append(" SELECT nvl(cast(sum(temp.CONTRIB_EMPLOYEE) as bigint),0) as EmpContributionNew,nvl(cast(sum(temp.CONTRIB_EMPLOYER)as bigint),0) as EmplrContributionNew " 
							+" FROM MST_DCPS_CONTRIB_MONTHLY_NEW temp inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =temp.YEAR_ID " 
							+" where temp.DCPS_ID  ='"+dcpsId+"' "
							+" and temp.PAY_MONTH="+i 
							+" and fin.FIN_YEAR_ID="+yearId+" ");
					
					StrSqlQuery1 = SBQuery1.toString();
					rs5 = smt5.executeQuery(StrSqlQuery1);
					td = new ArrayList();
					td.add(arrMonths[i - 1]);
					
					if (rs5.next()) {

						contribEmplrNew = 0d;
						contribEmpNeww = 0d;
						balanceEmpNew = balanceEmpNew	+ Float.parseFloat(rs5.getString("EmpContributionNew"));

						contribEmplrNew = Double.parseDouble(rs5.getString("EmplrContributionNew"));
						contribEmpNeww = Double.parseDouble(rs5.getString("EmpContributionNew"));

						balanceEmplrNew = balanceEmplrNew + contribEmplrNew;
						
					}
					
					
					if (rs1.next()) {

						contribEmplr = 0d;
						balanceEmp = balanceEmp	+ Float.parseFloat(rs1.getString("EmpContribution"));

						contribEmplr = Double.parseDouble(rs1.getString("EmplrContribution"));
						balanceEmplr = balanceEmplr + contribEmplr;

						td.add("<p>OLD:- "+(rs1.getString("EmpContribution")+"<p>"+"<p> NEW:-  "+(nosci(contribEmpNeww).toString())+"</p>"));
						td.add(("<p>OLD:- "+(nosci(contribEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(contribEmplrNew).toString())+"</p>"));	
						td.add(("<p>OLD:- "+(nosci(balanceEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmpNew).toString())+"</p>"));
						td.add(("<p>OLD:- "+(nosci(balanceEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmplrNew).toString())+"</p>"));
						tr.add(td);

					} else {
						td.add(new StyledData(("<p>OLD:- 0**"+"<p>"+"<p> NEW:-  0**"+"</p>"),boldVO));
						td.add(new StyledData(("<p>OLD:- 0"+"<p>"+"<p> NEW:-  0"+"</p>"),boldVO));
						td.add(("<p>OLD:- "+(nosci(balanceEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmpNew).toString())+"</p>"));
						td.add(("<p>OLD:- "+(nosci(balanceEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmplrNew).toString())+"</p>"));
						tr.add(td);
					}
				}
				// Loop exe for starting two months and printing march contri as 0
				for (int i = 1; i <= 3; i++) {
					//run for jan and feb
					if(i!=3){
						SBQuery = new StringBuilder();

						SBQuery.append(" SELECT nvl(cast(sum(temp.CUR_EMPL_CONTRIB) as bigint),0) as EmpContribution,nvl(cast(sum(temp.CUR_EMPLR_CONTRIB)as bigint),0) as EmplrContribution FROM TEMPR3 temp inner join SGVC_FIN_YEAR_MST fin" 
								+" on fin.FIN_YEAR_DESC=temp.FIN_YEAR" 
								+" where temp.EMP_ID_NO='"+dcpsId+"' "
								+" and temp.PAY_MONTH="+i 
								+" and fin.FIN_YEAR_ID="+yearId+" ");

						StrSqlQuery = SBQuery.toString();
						rs1 = smt1.executeQuery(StrSqlQuery);
						
						
						SBQuery1 = new StringBuilder();
						SBQuery1.append(" SELECT nvl(cast(sum(temp.CONTRIB_EMPLOYEE) as bigint),0) as EmpContributionNew,nvl(cast(sum(temp.CONTRIB_EMPLOYER)as bigint),0) as EmplrContributionNew " 
								+" FROM MST_DCPS_CONTRIB_MONTHLY_NEW temp inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =temp.YEAR_ID " 
								+" where temp.DCPS_ID  ='"+dcpsId+"' "
								+" and temp.PAY_MONTH="+i 
								+" and fin.FIN_YEAR_ID="+yearId+" ");
						
						StrSqlQuery1 = SBQuery1.toString();
						rs5 = smt5.executeQuery(StrSqlQuery1);
					
						td = new ArrayList();
						td.add(arrMonths[i - 1]);
						
						if (rs5.next()) {
							contribEmplrNew = 0d;
							balanceEmpNew = balanceEmpNew	+ Float.parseFloat(rs5.getString("EmpContributionNew"));
							contribEmplrNew = Double.parseDouble(rs5.getString("EmplrContributionNew"));
							balanceEmplrNew = balanceEmplrNew + contribEmplrNew;
							contribEmpNew1 = Double.parseDouble(rs5.getString("EmpContributionNew"));

						}

						if (rs1.next()) {
							contribEmplr = 0d;
							balanceEmp = balanceEmp + Float.parseFloat(rs1.getString("EmpContribution"));
	
							contribEmplr = Double.parseDouble(rs1.getString("EmplrContribution"));
							balanceEmplr = balanceEmplr + contribEmplr;
							td.add(("<p>OLD:- "+(rs1.getString("EmpContribution"))+"<p>"+"<p> NEW:-  "+(nosci(contribEmpNew1).toString())+"</p>"));
							td.add(("<p>OLD:- "+(nosci(contribEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(contribEmplrNew).toString())+"</p>"));	
							td.add(("<p>OLD:- "+(nosci(balanceEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmpNew).toString())+"</p>"));
							td.add(("<p>OLD:- "+(nosci(balanceEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmplrNew).toString())+"</p>"));
							tr.add(td);

						} else {
							td.add(("<p>OLD:- 0**"+"<p>"+"<p> NEW:-  0**"+"</p>"));
							td.add(("<p>OLD:- 0"+"<p>"+"<p> NEW:-  0"+"</p>"));
							td.add(("<p>OLD:- "+(nosci(balanceEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmpNew).toString())+"</p>"));
							td.add(("<p>OLD:- "+(nosci(balanceEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmplrNew).toString())+"</p>"));
							tr.add(td);
						}

					}
					else
					{
						SBQuery = new StringBuilder();
						SBQuery.append(" SELECT  nvl(cast(sum(temp.CUR_EMPL_CONTRIB) as bigint),0) as EmpContribution,nvl(cast(sum(temp.CUR_EMPLR_CONTRIB)as bigint),0) as EmplrContribution  FROM TEMPR3 temp inner join SGVC_FIN_YEAR_MST fin" 
								+" on fin.FIN_YEAR_DESC=temp.FIN_YEAR" 
								+" where temp.EMP_ID_NO='"+dcpsId+"' "
								+" and temp.PAY_MONTH="+i 
								+" and fin.FIN_YEAR_ID="+yearId+" ");
						StrSqlQuery = SBQuery.toString();
						rs1 = smt1.executeQuery(StrSqlQuery);
						
						SBQuery1 = new StringBuilder();
						SBQuery1.append(" SELECT nvl(cast(sum(temp.CONTRIB_EMPLOYEE) as bigint),0) as EmpContributionNew,nvl(cast(sum(temp.CONTRIB_EMPLOYER)as bigint),0) as EmplrContributionNew " 
								+" FROM MST_DCPS_CONTRIB_MONTHLY_NEW temp inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =temp.YEAR_ID " 
								+" where temp.DCPS_ID  ='"+dcpsId+"' "
								+" and temp.PAY_MONTH="+i 
								+" and fin.FIN_YEAR_ID="+yearId+" ");
						
						StrSqlQuery1 = SBQuery1.toString();
						rs5 = smt5.executeQuery(StrSqlQuery1);
						
						if (rs5.next()) {
							contribEmplrNew = 0d;
							contribEmplrNew = Double.parseDouble(rs5.getString("EmplrContributionNew"));
						}

						td = new ArrayList();
						td.add(arrMonths[i - 1]);
						if (rs1.next()) {

							contribEmplr = 0d;
							contribEmplr = Double.parseDouble(rs1.getString("EmplrContribution"));
							//	balanceEmplr = balanceEmplr + contribEmplr;
							td.add(("<p>OLD:- 0"+"<p>"+"<p> NEW:-  0"+"</p>"));
							td.add(("<p>OLD:- 0"+"<p>"+"<p> NEW:-  0"+"</p>"));
							td.add(("<p>OLD:- "+(nosci(balanceEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmpNew).toString())+"</p>"));
							td.add(("<p>OLD:- "+(nosci(balanceEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmplrNew).toString())+"</p>"));
							tr.add(td);

						} else {
							td.add(new StyledData(("<p>OLD:- 0**"+"<p>"+"<p> NEW:-  0**"+"</p>"),boldVO));
							td.add(new StyledData(("<p>OLD:- 0"+"<p>"+"<p> NEW:-  0"+"</p>"),boldVO));
							td.add(("<p>OLD:- "+(nosci(balanceEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmpNew).toString())+"</p>"));
							td.add(("<p>OLD:- "+(nosci(balanceEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(balanceEmplrNew).toString())+"</p>"));
							tr.add(td);
						}
					}
				}

				SBQuery = new StringBuilder();

				SBQuery.append("SELECT cast(sum(temp.CUR_EMPL_CONTRIB) as DECIMAL(16,2)) as contrib_employee ,cast((tr3.OPEN_EMPLR_CONTRIB+sum(temp.CUR_EMPLR_CONTRIB)+tr3.INT_EMPL_CONTRIB) as DECIMAL(16,2))  as closeEmplrBal,cast((tr3.OPEN_EMPLR_CONTRIB+sum(temp.CUR_EMPLR_CONTRIB)+tr3.INT_EMPL_CONTRIB+tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB+tr3.OPEN_EMPL_CONTRIB+tr3.tier2 +tr3.OPEN_INT +sum(temp.CUR_EMPL_CONTRIB)) as DECIMAL(16,2)) as Closenet,cast(sum(temp.CUR_EMPLR_CONTRIB) as DECIMAL(16,2)) as contrib_employer, cast((tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB+tr3.OPEN_EMPL_CONTRIB+tr3.tier2 +sum(temp.CUR_EMPL_CONTRIB))as DECIMAL(16,2)) as  CloseBal, tr3.tier2 as tier2,cast((tr3.INT_TIER2_CONTRIB+tr3.INT_EMPL_CONTRIB) as DECIMAL(16,2)) as TotalEmpInt ,tr3.INT_TIER2_CONTRIB as INT_TIER2_CONTRIB,tr3.INT_EMPL_CONTRIB as int_contrb_employee,tr3.INT_EMPL_CONTRIB as int_contrb_employer,tr3.OPEN_INT as open_net  FROM TEMPR3 temp inner join TEMPEMPR3 tr3 on temp.emp_id_no=tr3.emp_id_no    "
						+ "   inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_DESC=temp.fin_year and fin.FIN_YEAR_DESC=tr3.fin_year   "
						+ "  where fin.FIN_YEAR_ID="+yearId+" and temp.EMP_ID_NO='"+dcpsId+"' "
						+ "    group by tr3.INT_EMPL_CONTRIB,tr3.INT_EMPL_CONTRIB,tr3.TIER2,tr3.INT_TIER2_CONTRIB,tr3.OPEN_EMPLR_CONTRIB,tr3.OPEN_EMPL_CONTRIB,tr3.OPEN_INT  " );
				StrSqlQuery = SBQuery.toString();
				rs2 = smt2.executeQuery(StrSqlQuery);
				
				
				SBQuery1 = new StringBuilder();
				SBQuery1.append(" SELECT cast(sum(temp.CONTRIB_EMPLOYEE) as DECIMAL(16,2)) as contrib_employee_New , cast((tr3.OPEN_EMPLOYEE +sum(temp.CONTRIB_EMPLOYER)+tr3.INT_CONTRB_EMPLOYEE) as DECIMAL(16,2)) as closeEmplrBalNew, "
						+ " cast((tr3.OPEN_EMPLOYER +sum(temp.CONTRIB_EMPLOYER)+tr3.INT_CONTRB_EMPLOYEE +tr3.INT_CONTRB_TIER2 +tr3.INT_CONTRB_EMPLOYER +tr3.OPEN_EMPLOYER +tr3.CONTRIB_TIER2 +tr3.OPEN_INT +sum(temp.CONTRIB_EMPLOYER)) as DECIMAL(16,2)) as ClosenetNew, "
						+ " cast(sum(temp.CONTRIB_EMPLOYER) as DECIMAL(16,2)) as contrib_employer_New, cast((tr3.INT_CONTRB_TIER2 +tr3.INT_CONTRB_EMPLOYER +tr3.OPEN_INT_EMPLR +tr3.CONTRIB_TIER2 +sum(temp.CONTRIB_EMPLOYER))as DECIMAL(16,2)) as CloseBalNew, "
						+ " tr3.CONTRIB_TIER2 as tier2New, cast((tr3.INT_CONTRB_TIER2+tr3.INT_CONTRB_EMPLOYEE) as DECIMAL(16,2)) as TotalEmpIntNew , "
						+ " tr3.INT_CONTRB_TIER2 as INT_CONTRB_TIER2_New, tr3.INT_CONTRB_EMPLOYER as int_contrb_employee_New, tr3.INT_CONTRB_EMPLOYEE as int_contrb_employer_New, tr3.OPEN_INT as open_net_New "
						+ " FROM MST_DCPS_CONTRIB_MONTHLY_NEW temp inner join MST_DCPS_CONTRIB_YEARLY_NEW tr3 on temp.DCPS_ID =tr3.DCPS_ID  "   
						+ " inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =temp.YEAR_ID and fin.FIN_YEAR_ID =tr3.YEAR_ID  "
						+ " where fin.FIN_YEAR_ID= "+yearId+" and temp.DCPS_ID='"+dcpsId+"' "
						+ " group by tr3.INT_CONTRB_EMPLOYEE, tr3.INT_CONTRB_EMPLOYER,tr3.CONTRIB_TIER2,tr3.INT_CONTRB_TIER2,tr3.OPEN_EMPLOYEE,tr3.OPEN_EMPLOYER,tr3.OPEN_INT,tr3.OPEN_INT_EMPLR " );

				StrSqlQuery1 = SBQuery1.toString();
				rs5 = smt5.executeQuery(StrSqlQuery1);
				if (rs5.next()) {
					TotalEmpNew = Double.parseDouble(rs5.getString("contrib_employee_New"))+ Double.parseDouble(rs5.getString("tier2New"));
					TotalEmplrNew = Double.parseDouble(rs5.getString("contrib_employer_New"));
				
					
					contribEmployeeNew = Double.parseDouble(rs5.getString("contrib_employee_New"));
					contribEmployerNew = Double.parseDouble(rs5.getString("contrib_employer_New"));
					tier2New = Double.parseDouble(rs5.getString("tier2New"));
					TotalEmpIntNew = Double.parseDouble(rs5.getString("TotalEmpIntNew"));
					intContrbEmployerNew = Double.parseDouble(rs5.getString("int_contrb_employer_New"));

					closeEmplrBalNew = Double.parseDouble(rs5.getString("closeEmplrBalNew"));
					CloseBalNew = Double.parseDouble(rs5.getString("CloseBalNew"));
					
					gLogger.info("contribEmployeeNew"+contribEmployeeNew);
					gLogger.info("contribEmployerNew"+contribEmployerNew);
					gLogger.info("tier2New"+tier2New);
					gLogger.info("intContrbEmployerNew"+intContrbEmployerNew);
					gLogger.info("closeEmplrBalNew"+closeEmplrBalNew);
					gLogger.info("CloseBalNew"+CloseBalNew);
					
				}
				
				
				if (rs2.next()) 
				{
					Double TotalEmp = Double.parseDouble(rs2.getString("contrib_employee"))+ Double.parseDouble(rs2.getString("tier2"));
					Double TotalEmplr = Double.parseDouble(rs2.getString("contrib_employer"));
					td = new ArrayList();
					td.add(new StyledData("Total Amount(A)",boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData(("<p>OLD:- "+(rs2.getString("contrib_employee"))+"<p>"+"<p> NEW:-  "+contribEmployeeNew+"</p>"),boldVO));
					td.add(new StyledData(("<p>OLD:- "+(rs2.getString("contrib_employer"))+"<p>"+"<p> NEW:-  "+contribEmployerNew+"</p>"),boldVO));
					tr.add(td);

					td = new ArrayList();
					td.add(new StyledData("Tier2(B) :" ,boldVO));
					td.add("");
					td.add("");
					td.add(new StyledData(("<p>OLD:- "+(rs2.getString("tier2"))+"<p>"+"<p> NEW:-  "+tier2New+"</p>"),boldVO));
					td.add("");
					tr.add(td);

					td = new ArrayList();
					td.add(new StyledData("Total Amount(A+B)",boldVO));
					td.add("");
					td.add("");
					
					td.add(new StyledData(("<p>OLD:- "+(nosci(TotalEmp).toString())+"<p>"+"<p> NEW:-  "+(nosci(TotalEmpNew).toString())+"</p>"),boldVO));
					td.add(new StyledData(("<p>OLD:- "+(nosci(TotalEmplr).toString())+"<p>"+"<p> NEW:-  "+(nosci(TotalEmplrNew).toString())+"</p>"),boldVO));
					tr.add(td);

					td = new ArrayList();
					td.add(new StyledData("Interest(C)",boldVO));
					td.add("");
					td.add("");		
					td.add(new StyledData(("<p>OLD:- "+(rs2.getString("TotalEmpInt"))+"<p>"+"<p> NEW:-  "+TotalEmpIntNew+"</p>"),boldVO));
					td.add(new StyledData(("<p>OLD:- "+(rs2.getString("int_contrb_employer"))+"<p>"+"<p> NEW:-  "+intContrbEmployerNew+"</p>"),boldVO));
					tr.add(td);

					td = new ArrayList();
					td.add("<b>Closing Balance</b>");
					td.add("");
					td.add("");
					td.add(new StyledData(("<p>OLD:- "+(rs2.getString("CloseBal"))+"<p>"+"<p> NEW:-  "+CloseBalNew+"</p>"),boldVO));
					td.add(new StyledData(("<p>OLD:- "+(rs2.getString("closeEmplrBal"))+"<p>"+"<p> NEW:-  "+closeEmplrBalNew+"</p>"),boldVO));
					tr.add(td);

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
				rowList.add("Date and Time of generation :          "+dateFormat5.format(date));
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

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	public List getAllTreasuries( String lStrLangId, String lStrLocId) {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100003,100006) and CM.LANG_ID = 1 and CM.LOC_ID not in(9991,1111) order by CM.loc_Id ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");				
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id +"-"+treasury_name);
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
				e.printStackTrace();
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	}

	public List getAllDDO(String treasuryId, String lStrLangId, String lStrLocId) {

		ArrayList<ComboValuesVO> arrDDO = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String ddo_code = null;
		String ddo_name = null;
		try {
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					" SELECT DM.ddo_code as ddo_code, DM.ddo_name as ddo_name FROM Rlt_Ddo_Org RO, Org_Ddo_Mst DM,Cmn_Location_Mst LM "
					+ "WHERE RO.location_Code = '"
					+ treasuryId
					+ "' AND RO.ddo_Code = DM.ddo_Code AND LM.location_Code = RO.location_Code AND LM.LANG_ID = 1 order by DM.ddo_code ASC");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			if(lRs!=null)
			{	
				while (lRs.next()) {
					ComboValuesVO vo = new ComboValuesVO();
					ddo_code = lRs.getString("ddo_code");
					ddo_name = lRs.getString("ddo_name");
					vo.setId(ddo_code);
					vo.setDesc("<![CDATA["+"("+ddo_code+") "+ddo_name+"]]>");
					arrDDO.add(vo);
				}
			}
			else
			{
				ComboValuesVO vo = new ComboValuesVO();
				vo.setId("-1");
				vo.setDesc("--Select--");
				arrDDO.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.info("Sql Exception:" + e, e);

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
		return arrDDO;
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

	public List getAllEmpsUnderDDO(String lStrddoCode, String lStrLangId,
			String lStrLocId) {

		List<Object> lLstReturnList = null;
		try {
			if (!lStrddoCode.equals("-1")) {
				StringBuilder sb = new StringBuilder();
				Session lObjSession = ServiceLocator.getServiceLocator()
				.getSessionFactory().getCurrentSession();
				sb.append(" SELECT EM.dcpsEmpId,EM.name,EM.dcpsId FROM MstEmp EM ");
				sb.append(" WHERE EM.ddoCode = :ddoCode and EM.dcpsOrGpf = 'Y' and regStatus = 1  order by EM.name");
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
						Long dcpsEmpId=Long.parseLong(obj[0].toString());
						String empName=obj[1].toString();
						String dcpsId=obj[2].toString();
						lObjComboValuesVO.setId(obj[0].toString());
						lObjComboValuesVO.setDesc(empName +"--"+dcpsId);
						//	lObjComboValuesVO.setDesc(obj[1].toString());
						lLstReturnList.add(lObjComboValuesVO);
					}
				}
				else {
					lLstReturnList = new ArrayList<Object>();
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("-1");
					lObjComboValuesVO.setDesc("-- Select --");
					lLstReturnList.add(lObjComboValuesVO);
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

	public List getInterestRatesForGivenYear(Long finYearId) {

		List<Object> lLstReturnList = null;
		StringBuilder lSBQuery = null;
		Query sqlQuery = null;

		try {

			lSBQuery = new StringBuilder();
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			lSBQuery.append(" SELECT IR.INTEREST,IR.EFFECTIVE_FROM,nvl(IR.APPLICABLE_TO,FY.TO_DATE) FROM MST_DCPS_INTEREST_RATE IR LEFT JOIN SGVC_FIN_YEAR_MST FY ON IR.FIN_YEAR_ID = FY.FIN_YEAR_ID WHERE IR.FIN_YEAR_ID =" + finYearId + " ORDER BY IR.EFFECTIVE_FROM ASC");
			sqlQuery = lObjSession.createSQLQuery(lSBQuery.toString());

			lLstReturnList = sqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lLstReturnList;
	}

	public static double Round(double Rval, int Rpl) {
		double p = (double)Math.pow(10,Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return (double)tmp/p;
	}
	//start to calculate current year march contri to add into current year april
	public double preContriMarch(Long empId, Long yearId,String dcpsId)
	{
		Statement smt1=null;
		ResultSet rs3=null;
		Connection con=null;
		Double contribEmplrMarch = 0d;
		try{

			con=DBConnection.getConnection();
			smt1=con.createStatement();
		
			String lStrPostEmpContriDoneOrNot = null;
			StringBuilder	SBQuery1= new StringBuilder();
			SBQuery1.append(" SELECT  nvl(cast(sum(temp.CUR_EMPL_CONTRIB) as bigint),0) as EmpContribution,nvl(cast(sum(temp.CUR_EMPLR_CONTRIB)as bigint),0) as EmplrContribution  FROM TEMPR3 temp inner join SGVC_FIN_YEAR_MST fin" 
					+" on fin.FIN_YEAR_DESC=temp.FIN_YEAR" 
					+" where temp.EMP_ID_NO='"+dcpsId+"' "
					+" and temp.PAY_MONTH=3 and fin.FIN_YEAR_ID="+yearId+" ");

			gLogger.info("query to get contri of last march "+SBQuery1);
			String StrSqlQuery1 = SBQuery1.toString();
			gLogger.info("query to get contri of last march in .tostring"+StrSqlQuery1);

			rs3 = smt1.executeQuery(StrSqlQuery1);
			gLogger.info("query to get contri of last march query executed "+SBQuery1);

			if(rs3.next())
			{

				contribEmplrMarch = 0d;

				contribEmplrMarch = Double.parseDouble(rs3.getString("EmplrContribution"));
				gLogger.info("outside ashish contri of march:"+contribEmplrMarch);
			}
		}
		catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
		} finally {
			try {
				if (smt1 != null) {
					smt1.close();
				}
				if (rs3 != null) {
					rs3.close();
				}
				if (con != null) {
					con.close();
				}

				smt1 = null;
				rs3 = null;
				con = null;
			} catch (Exception e) {
				e.printStackTrace();
				gLogger.info("Sql Exception:" + e, e);
			}
		}

		return  contribEmplrMarch;
		
		//end to calculate current year march contri to add into current year april
	}

	
	public double preContriMarchNew(Long empId, Long yearId,String dcpsId)

	{
		Statement smt1=null;
		ResultSet rs3=null;
		Connection con=null;
		Double contribEmplrMarch = 0d;
		try{

			con=DBConnection.getConnection();
			smt1=con.createStatement();

			String lStrPostEmpContriDoneOrNot = null;
			StringBuilder	SBQuery1= new StringBuilder();
			SBQuery1.append(" SELECT nvl(cast(sum(temp.CONTRIB_EMPLOYEE) as bigint),0) as EmpContribution,nvl(cast(sum(temp.CONTRIB_EMPLOYER)as bigint),0) as EmplrContribution " 
					+" FROM MST_DCPS_CONTRIB_MONTHLY_NEW temp inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID =temp.YEAR_ID " 
					+" where temp.DCPS_ID  ='"+dcpsId+"' "
					+"  and temp.PAY_MONTH=3 and fin.FIN_YEAR_ID="+yearId+" ");

			gLogger.info("query to get contri of last march "+SBQuery1);
			String StrSqlQuery1 = SBQuery1.toString();
			gLogger.info("query to get contri of last march in .tostring"+StrSqlQuery1);

			rs3 = smt1.executeQuery(StrSqlQuery1);
			gLogger.info("query to get contri of last march query executed "+SBQuery1);

			if(rs3.next())
			{

				contribEmplrMarch = 0d;
				contribEmplrMarch = Double.parseDouble(rs3.getString("EmplrContribution"));
				gLogger.info("outside ashish contri of march:"+contribEmplrMarch);
			}
		}
		catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
		} finally {
			try {
				if (smt1 != null) {
					smt1.close();
				}
				if (rs3 != null) {
					rs3.close();
				}
				if (con != null) {
					con.close();
				}

				smt1 = null;
				rs3 = null;
				con = null;
			} catch (Exception e) {
				e.printStackTrace();
				gLogger.info("Sql Exception:" + e, e);
			}
		}

		return  contribEmplrMarch;
		
		//end to calculate current year march contri to add into current year april
	}
	
	
}
