/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 22, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.report;

import java.math.BigDecimal;
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
import com.tcs.sgv.common.dao.FinancialYearDAOImpl;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
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
 * @since JDK 5.0 Sep 22, 2011
 */
public class GPFAccountForm1Report extends DefaultReportDataFinder implements ReportDataFinder {

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

			con = gObjSessionFactory.getCurrentSession().connection();

			smt = con.createStatement();
			String StrSqlQuery = "";

			StyleVO[] noBorder = new StyleVO[3];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			noBorder[1] = new StyleVO();
			noBorder[1].setStyleId(IReportConstants.REPORT_PAGINATION);
			noBorder[1].setStyleValue("NO");
			noBorder[2] = new StyleVO();
			noBorder[2].setStyleId(26);
			noBorder[2].setStyleValue("JavaScript:self.close();");

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

			if (lObjReport.getReportCode().equals("800004")) {
				
				String lStrSevaarthId = "";
				String lStrDesignation = "";
				String lStrOfficeName = "";
				String lStrGpfAccNo = "";
				String lStrEmpName = "";
				
				lStrGpfAccNo = lObjReport.getParameterValue("gpfAccountNo").toString();
				lStrEmpName = lObjReport.getParameterValue("name").toString();
				
				
				Long lLngFinYear = Long.parseLong(lObjReport.getParameterValue("financialYear").toString());
				new FinancialYearDAOImpl(null, serviceLocator.getSessionFactory());
				ScheduleGenerationDAO lObjScheduleGenerationDAO = new ScheduleGenerationDAOImpl(null, serviceLocator
						.getSessionFactory());
				Double lDblOpeningBal = 0d;
				lObjReport.setStyleList(noBorder);
				List lLstEmpData = null;
				if (!lStrGpfAccNo.equals("") && !lStrGpfAccNo.equals(null)) {
					lLstEmpData = lObjScheduleGenerationDAO.getEmpDataForLedger(lStrGpfAccNo, (lLngFinYear -1l));
				} else {
					lStrGpfAccNo = getGpfAccNo(lStrEmpName);
					lLstEmpData = lObjScheduleGenerationDAO.getEmpDataForLedger(lStrGpfAccNo, (lLngFinYear -1l));
				}
				if (lLstEmpData != null && lLstEmpData.size() > 0) {
					Object[] lArrObj = (Object[]) lLstEmpData.get(0);
					lStrEmpName = (String) lArrObj[0];
					lStrSevaarthId = (String) lArrObj[1];
					lStrDesignation = (String) lArrObj[2];
					lStrOfficeName = (String) lArrObj[3];
					lDblOpeningBal = (Double) lArrObj[4];
				}
				if (!lStrEmpName.equals("")) {

					String header1 = "<center>" + "<b><font size=\"1\"> " + "Group D - GPF A/C Form 1 for the Year "
							+ getFinYearDesc(lLngFinYear) + "</font></b></center>";

					String additionalHeader = header1 + newline;
					lObjReport.setAdditionalHeader(additionalHeader);

					ArrayList rowList = new ArrayList();
					rowList.add("Name of the Employee:" + lStrEmpName);
					lLstDataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("Sevaarth ID:" + lStrSevaarthId);
					lLstDataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("Designation:" + lStrDesignation);
					lLstDataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("Name of the office:" + lStrOfficeName);
					lLstDataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("GPF A/C No:" + lStrGpfAccNo);
					lLstDataList.add(rowList);

					rowList = new ArrayList();
					rowList.add("Opening Balance:" + lDblOpeningBal);
					lLstDataList.add(rowList);

					StringBuilder SBQuery = new StringBuilder();

					SBQuery
							.append("SELECT smm.month_name , mgm.regular_subscription, mgm.advance_recovery ,"
									+ "mgm.voucher_no , mgm.arrear_amount , mgm.advance_sanctioned ,mgm.inst_no ,mgm.total_inst ,"
									+ "mgm.deputation_challan ,mgm.pre_pay_of_Advance, mga.voucher_no "
									+ " FROM sgva_month_mst smm JOIN mst_gpf_monthly mgm ON mgm.month_id = smm.month_id " 
									+ " LEFT OUTER JOIN mst_gpf_advance mga ON mgm.ADVANCE_TRN_ID = mga.TRANSACTION_ID "
									+ " WHERE mgm.gpf_acc_no='" + lStrGpfAccNo
									+ "' AND (mgm.fin_year_id=" + lLngFinYear
									+ " AND mgm.month_id>3 OR mgm.fin_year_id=" + (lLngFinYear + 1)
									+ " AND mgm.month_id<=3) AND mgm.status='A' ");

					StrSqlQuery = SBQuery.toString();
					rs = smt.executeQuery(StrSqlQuery);
					Integer counter = 1;
					Double lDblMonthlyTotal = lDblOpeningBal;
					Double lDblContribution = 0d;
					Double lDblRecovery = 0d;
					Double lDblArrear = 0d;
					Double lDblWithdrawal = 0d;
					Double lDblTotalReceipt = 0d;
					Double lDblTotalWithdrawal = 0d;
					Double lDblTotalForInterest = 0d;
					String lStrArrear = null;
					String lStrWithdraw = null;
					String lStrRecovery = null;
					String lStrInstNo = "";
					String lStrTotalInst = "";
					String lStrVoucherNoRecovery = "";
					String lStrVoucherNoWithdrwl = "";
					// Double lDblDeputationChallan = 0d;
					// Double lDblPrePayAdvance = 0d;
					String lStrContri = null;
					Double lDblInterest = 0d;
					Double lDblClosingBalance = 0d;
					Double lDblTotalContri = 0d;
					Double lDblTotalRecovery = 0d;
					Double lDblTotalArrear = 0d;
					tr = new ArrayList();
					while (rs.next()) {

						lStrContri = rs.getString(2);
						lStrRecovery = rs.getString(3);
						lStrVoucherNoRecovery = rs.getString(4);
						lStrArrear = rs.getString(5);
						lStrWithdraw = rs.getString(6);
						lStrInstNo = rs.getString(7);
						lStrTotalInst = rs.getString(8);
						lStrVoucherNoWithdrwl = rs.getString(11);
						// lDblDeputationChallan =
						// Double.parseDouble(rs.getString(9));
						// lDblPrePayAdvance =
						// Double.parseDouble(rs.getString(10));
						if (lStrContri != null) {
							lDblContribution = Double.parseDouble(lStrContri);
						} else {
							lDblContribution = 0d;
						}
						if (lStrRecovery != null) {
							lDblRecovery = Double.parseDouble(lStrRecovery);
						} else {
							lDblRecovery = 0d;
						}
						if (lStrArrear != null) {
							lDblArrear = Double.parseDouble(lStrArrear);
						} else {
							lDblArrear = 0d;
						}
						if (lStrWithdraw != null) {
							lDblWithdrawal = Double.parseDouble(lStrWithdraw);
						} else {
							lDblWithdrawal = 0d;
						}
						if(lStrVoucherNoRecovery != null){
							
						}else{
							lStrVoucherNoRecovery = "";
						}
						if(lStrVoucherNoWithdrwl != null){
							
						}else{
							lStrVoucherNoWithdrwl = "";
						}

						lDblTotalReceipt = lDblTotalReceipt + lDblContribution + lDblRecovery + lDblArrear;
						lDblTotalWithdrawal = lDblTotalWithdrawal + lDblWithdrawal;
						lDblMonthlyTotal = lDblMonthlyTotal + lDblContribution + lDblRecovery + lDblArrear
								- lDblWithdrawal;
						td = new ArrayList();
						td.add(rs.getString(1));
						td.add(lDblContribution);
						if (lDblRecovery != 0) {
							td.add(lDblRecovery + "(" + lStrInstNo + "/" + lStrTotalInst + ")");
						} else {
							td.add(lDblRecovery + "(0/0)");
						}
						
						td.add(lStrVoucherNoRecovery);
						td.add(lDblArrear);
						td.add(lStrVoucherNoWithdrwl);
						td.add(lDblWithdrawal);
						// if (lDblDeputationChallan != 0 || lDblPrePayAdvance
						// != 0) {
						// td.add("");
						// tr.add(td);
						// td = new ArrayList();
						// td.add(rs.getString("month"));
						// if (lDblDeputationChallan != 0) {
						// td.add(lDblDeputationChallan + "-");
						// } else {
						// td.add("");
						// }
						// if (lDblPrePayAdvance != 0) {
						// td.add(lDblPrePayAdvance + "-");
						// } else {
						// td.add("");
						// }
						// td.add("");
						// td.add("");
						// td.add("");
						// td.add("");
						// td.add(lDblMonthlyTotal + lDblDeputationChallan +
						// lDblPrePayAdvance);
						// tr.add(td);
						// lDblTotalRecovery =
						// lDblTotalRecovery+lDblPrePayAdvance;
						// lDblTotalContri = lDblTotalContri +
						// lDblDeputationChallan;
						// } else {
						td.add(lDblMonthlyTotal);
						tr.add(td);
						// }

						counter = counter + 1;
						lDblTotalForInterest = lDblTotalForInterest + lDblMonthlyTotal;
						lDblTotalContri = lDblTotalContri + lDblContribution;
						lDblTotalRecovery = lDblTotalRecovery + lDblRecovery;
						lDblTotalArrear = lDblTotalArrear + lDblArrear;
					}
					if (counter > 1) {
						td = new ArrayList();
						td.add(new StyledData("Total", boldVO));
						td.add(new StyledData(lDblTotalContri, boldVO));
						td.add(new StyledData(lDblTotalRecovery, boldVO));
						td.add(new StyledData("", boldVO));
						td.add(new StyledData(lDblTotalArrear, boldVO));
						td.add(new StyledData("", boldVO));
						td.add(new StyledData(lDblTotalWithdrawal, boldVO));
						td.add(new StyledData("", boldVO));

						tr.add(td);
						rptTd = new TabularData(tr);
						RptVo = reportsDao.getReport("800003", lObjReport.getLangId(), lObjReport.getLocId());

						(rptTd).setRelatedReport(RptVo);

						rptList1 = new ArrayList();
						rptList1.add(rptTd);
						lLstDataList.add(rptList1);
					}

					lDblInterest = lDblTotalForInterest * 8 / 1200;
					lDblClosingBalance = lDblMonthlyTotal + lDblInterest;

					rowList = new ArrayList();
					rowList.add(newline);
					lLstDataList.add(rowList);

					ArrayList sixRowsLeft = new ArrayList();

					rowList = new ArrayList();
					rowList.add("Opening Balance");
					rowList.add(": Rs." + lDblOpeningBal);
					sixRowsLeft.add(rowList);

					rowList = new ArrayList();
					rowList.add("Total Receipt");
					rowList.add(": Rs." + Math.round(lDblTotalReceipt));
					sixRowsLeft.add(rowList);

					rowList = new ArrayList();
					rowList.add("Withdrawal");
					rowList.add(": Rs." + Math.round(lDblTotalWithdrawal));
					sixRowsLeft.add(rowList);

					rowList = new ArrayList();
					rowList.add("Interest@ 8%");
					rowList.add(": Rs." + Math.round(lDblInterest));
					sixRowsLeft.add(rowList);

					rowList = new ArrayList();
					rowList.add("Closing Balance");
					rowList.add(": Rs." + Math.round(lDblClosingBalance));
					sixRowsLeft.add(rowList);

					String lStrTotAmtInWords = EnglishDecimalFormat
							.convertWithSpace(new BigDecimal(lDblClosingBalance));

					rowList = new ArrayList();
					rowList.add("Amount in Words ");
					rowList.add(": Rs." + lStrTotAmtInWords);
					sixRowsLeft.add(rowList);

					rptTd = new TabularData(sixRowsLeft);
					RptVo = reportsDao.getReport("800009", lObjReport.getLangId(), lObjReport.getLocId());
					ReportColumnVO[] lArrReportColumnVO = RptVo.getReportColumns();
					lArrReportColumnVO[0].setColumnWidth(10);
					lArrReportColumnVO[1].setColumnWidth(90);
					(rptTd).setRelatedReport(RptVo);
					(rptTd).setStyles(noBorder);
					rowList = new ArrayList();
					rowList.add(rptTd);
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
				} else {
					ArrayList rowList = new ArrayList();
					rowList.add(new StyledData("Invalid Data", boldVO));
					lLstDataList.add(rowList);
				}
			}

		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
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
				gLogger.error("Exception :" + e1, e1);

			}
		}
		return lLstDataList;
	}

	// /**
	// *
	// * <H3>Description -</H3>
	// *
	// *
	// *
	// * @author 383385
	// * @param lObjReport
	// * @param lLngLangId
	// * @param lStrLocCode
	// * @return
	// */
	// public List getGPFAccountFrom1Report(ReportVO lObjReport, String
	// lStrLocCode) {
	//
	// ScheduleGenerationDAO lObjScheduleGenerationDAO = new
	// ScheduleGenerationDAOImpl(null, serviceLocator.getSessionFactory());
	// List dataList = new ArrayList();
	// String StrSqlQuery = "";
	// ResultSet rs = null;
	// Statement smt = null;
	// try {
	//			
	//			
	// } catch (Exception e) {
	// gLogger.error("Error is :" + e);
	// }
	//
	// return dataList;
	// }

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
	
	private String getGpfAccNo(String lStrEmpName)
	{
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String lStrGpfAccNo = "";
		
		try{
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer("select MGA.GPF_ACC_NO from MST_DCPS_EMP MDE, MST_EMP_GPF_ACC MGA where UPPER(MDE.EMP_NAME) = UPPER('"
					+ lStrEmpName + "') and MDE.SEVARTH_ID = MGA.SEVAARTH_ID");
			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			lRs.next();	
			lStrGpfAccNo = lRs.getString(1).toString();
		}catch (Exception e){
			gLogger.error(" Error is : " + e, e);			
		}finally {
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
		return lStrGpfAccNo;
	}
}
