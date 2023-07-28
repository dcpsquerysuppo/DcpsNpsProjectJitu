package com.tcs.sgv.lna.report;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;

public class LNALedgerDAOImpl extends DefaultReportDataFinder implements ReportDataFinder {
	private static final Logger gLogger = Logger.getLogger("LNAReports");
	public static String newline = System.getProperty("line.separator");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Map lMapSeriesHeadCode = null;
	Session ghibSession = null;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {
		Connection con = null;
		Statement smt = null;
		ResultSet rs = null;
		List lLstDataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		ReportVO RptVo = null;
		ReportsDAO reportsDao = new ReportsDAOImpl();
		ArrayList tr = null;
		ArrayList td = null;
		ArrayList rptList1 = null;
		TabularData rptTd = null;
		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat lObjDateFormat2 = new SimpleDateFormat("ddMMyyyy");
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
			StyleVO[] noBorder = new StyleVO[2];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			noBorder[1] = new StyleVO();
			noBorder[1].setStyleId(IReportConstants.REPORT_PAGINATION);
			noBorder[1].setStyleValue("NO");

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

			
			if (lObjReport.getReportCode().equals("8000054")) {

				ArrayList rowList = null;
				String lStrEmpName = "";
				String lStrSevaarthId = "";
				String lStrDesignation = "";
				String lStrOfficeName = "";
				String lStrSubType = "";
				Long lLngSancAmount = null;
				Double lDblSancInterestAmount = null;
				String lStrOrderNo = "";
				String lStrTreasury = "";
				Date lDtOrderDate = null;
				Long lLngIntsAmount = null;
				Double lDblIntsInterestAmount = null;
				Integer lIntTotalIns = null;
				Integer lIntTotalInterestIns = null;
				Long lLngOddIns = null;
				Long lLngOddInsNo = null;
				Long lLngOddInterestIns = null;
				Long lLngOddInterestInsNo = null;
				String lStrTransactionId = "";
				lStrSevaarthId = lObjReport.getParameterValue("sevaarthId").toString().trim();
				Long lLngLoanType = Long.parseLong(lObjReport.getParameterValue("cmbLoanType").toString());
				Long lLngFinYear = Long.parseLong(lObjReport.getParameterValue("financialYear").toString());
				LNALedgerQueryDAOImpl lObjLedgerQueryDAOImpl = new LNALedgerQueryDAOImpl(null, serviceLocator.getSessionFactory());
				lObjReport.setStyleList(noBorder);
				Long lLngPayrollLoanType = null;
				if (lLngLoanType == Long.parseLong(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE"))) {
					lLngPayrollLoanType = 58L;
				}else if (lLngLoanType == Long.parseLong(gObjRsrcBndle.getString("LNA.MOTORADVANCE"))) {
					lLngPayrollLoanType = 57L;
				}else if (lLngLoanType == Long.parseLong(gObjRsrcBndle.getString("LNA.HOUSEADVANCE"))) {
					lLngPayrollLoanType = 67L;
				}
					
				List lLstEmpData = null;
				if (lLngLoanType == Long.parseLong(gObjRsrcBndle.getString("LNA.COMPUTERADVANCE"))) {
					lLstEmpData = lObjLedgerQueryDAOImpl.getEmpDtlsForCompAdvance(lStrSevaarthId,gStrLocCode);

					if (lLstEmpData != null && lLstEmpData.size() > 0) {
						Object[] lArrObj = (Object[]) lLstEmpData.get(0);
						lStrSevaarthId = (String) lArrObj[0];
						lStrEmpName = (String) lArrObj[1];
						lStrDesignation = (String) lArrObj[2];
						lStrOfficeName = (String) lArrObj[3];
						lStrSubType = (String) lArrObj[4];
						lLngSancAmount = (Long) lArrObj[5];
						lStrOrderNo = (String) lArrObj[6];
						lDtOrderDate = (Date) lArrObj[7];
						lLngIntsAmount = (Long) lArrObj[8];
						lIntTotalIns = (Integer) lArrObj[9];
						lLngOddIns = (Long) lArrObj[10];
						lLngOddInsNo = (Long) lArrObj[11];
						lStrTreasury = (String) lArrObj[12];
						lStrTransactionId = (String) lArrObj[13];
					
					}
					if (!lStrEmpName.equals("")) {

						rowList = new ArrayList();
						rowList.add("<b>Name of the Employee:</b>" + space(10) + ((lStrEmpName==null)?"":lStrEmpName));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Sevaarth ID:</b>" + space(27) +  ((lStrSevaarthId==null)?"":lStrSevaarthId));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Designation:</b>" + space(27) +  ((lStrDesignation==null)?"":lStrDesignation));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Name of the office:</b>" + space(16) +  ((lStrOfficeName==null)?"":lStrOfficeName));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Loan Sub Type:</b>" + space(22) +  ((lStrSubType==null)?"":lStrSubType));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Sanctioned Amount:</b>" + space(15) +  ((lLngSancAmount==null)?"":lLngSancAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Order No:</b>" + space(31) +  ((lStrOrderNo==null)?"":lStrOrderNo));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Order Date:</b>" + space(28) +  ((lDtOrderDate==null)?"":lObjDateFormat.format(lDtOrderDate)));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Installment Amount:</b>" + space(15) +  ((lLngIntsAmount==null)?"":lLngIntsAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Total Installment:</b>" + space(19) +  ((lIntTotalIns==null)?"":lIntTotalIns));
						lLstDataList.add(rowList);

						/*rowList = new ArrayList();
						rowList.add("Treasury:" + lStrTreasury);
						lLstDataList.add(rowList);*/

						if (lLngOddIns != null) {
							if (lLngOddInsNo == 800056L) {
								rowList = new ArrayList();
								rowList.add("<b>First Odd Installment:</b>" + space(13) + lLngOddIns);
								lLstDataList.add(rowList);
							} else if (lLngOddInsNo == 800057L) {
								rowList = new ArrayList();
								rowList.add("<b>Last Odd Installment:</b>" + space(13) + lLngOddIns);
								lLstDataList.add(rowList);
							}
						}
					}

				} else if (lLngLoanType == Long.parseLong(gObjRsrcBndle.getString("LNA.MOTORADVANCE"))) {
					lLstEmpData = lObjLedgerQueryDAOImpl.getEmpDtlsForMotorAdvance(lStrSevaarthId,gStrLocCode);
					if (lLstEmpData != null && lLstEmpData.size() > 0) {
						Object[] lArrObj = (Object[]) lLstEmpData.get(0);
						lStrSevaarthId = (String) lArrObj[0];
						lStrEmpName = (String) lArrObj[1];
						lStrDesignation = (String) lArrObj[2];
						lStrOfficeName = (String) lArrObj[3];
						lStrSubType = (String) lArrObj[4];
						lLngSancAmount = (Long) lArrObj[5];
						lDblSancInterestAmount = (Double) lArrObj[6];
						lStrOrderNo = (String) lArrObj[7];
						lDtOrderDate = (Date) lArrObj[8];
						lLngIntsAmount = (Long) lArrObj[9];
						lDblIntsInterestAmount = (Double) lArrObj[10];
						lIntTotalIns = (Integer) lArrObj[11];
						lIntTotalInterestIns = (Integer) lArrObj[12];
						lLngOddIns = (Long) lArrObj[13];
						lLngOddInsNo = (Long) lArrObj[14];
						lLngOddInterestIns = (Long) lArrObj[15];
						lLngOddInterestInsNo = (Long) lArrObj[16];
						lStrTreasury = (String) lArrObj[17];
						lStrTransactionId = (String) lArrObj[18];
					}
					if (!lStrEmpName.equals("")) {

						rowList = new ArrayList();
						rowList.add("<b>Name of the Employee:</b>" + space(14) + ((lStrEmpName==null)?"":lStrEmpName));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Sevaarth ID:</b>" + space(31) +  ((lStrSevaarthId==null)?"":lStrSevaarthId));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Designation:</b>" + space(31) +  ((lStrDesignation==null)?"":lStrDesignation));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Name of the office:</b>" + space(20) +  ((lStrOfficeName==null)?"":lStrOfficeName));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Loan Sub Type:</b>" + space(26) +  ((lStrSubType==null)?"":lStrSubType));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Sanctioned Amount:</b>" + space(19) +  ((lLngSancAmount==null)?"":lLngSancAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Interest Amount:</b>" + space(24) +  ((lDblSancInterestAmount==null)?"":lDblSancInterestAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Order No:</b>" + space(36) +  ((lStrOrderNo==null)?"":lStrOrderNo));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Order Date:</b>" + space(33) +  ((lDtOrderDate==null)?"":lObjDateFormat.format(lDtOrderDate)));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Principal Installment Amount:</b>" + space(3) +  ((lLngIntsAmount==null)?"":lLngIntsAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Interest Installment Amount:</b>" + space(4) +  ((lDblIntsInterestAmount==null)?"":lDblIntsInterestAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Total Principal Installment:</b>" + space(8) +  ((lIntTotalIns==null)?"":lIntTotalIns));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Total Interest Installment:</b>" + space(9) +  ((lIntTotalInterestIns==null)?"":lIntTotalInterestIns));
						lLstDataList.add(rowList);

						/*rowList = new ArrayList();
						rowList.add("Treasury:" + lStrTreasury);
						lLstDataList.add(rowList);*/

						if (lLngOddIns != null) {
							if (lLngOddInsNo == 800056L) {
								rowList = new ArrayList();
								rowList.add("<b>First Odd Principal Installment:</b>" + space(2) + lLngOddIns);
								lLstDataList.add(rowList);
							} else if (lLngOddInsNo == 800057L) {
								rowList = new ArrayList();
								rowList.add("<b>Last Odd Principal Installment:</b>" + space(2) + lLngOddIns);
								lLstDataList.add(rowList);
							}
						}

						if (lLngOddInterestIns != null) {

							if (lLngOddInterestInsNo == 800056L) {
								rowList = new ArrayList();
								rowList.add("<b>First Odd Interest Installment:</b>" + space(3) + lLngOddInterestIns);
								lLstDataList.add(rowList);
							} else if (lLngOddInterestInsNo == 800057L) {
								rowList = new ArrayList();
								rowList.add("<b>Last Odd Interest Installment:</b>" + space(3) + lLngOddInterestIns);
								lLstDataList.add(rowList);
							}
						}
					}

				} else if (lLngLoanType == Long.parseLong(gObjRsrcBndle.getString("LNA.HOUSEADVANCE"))) {
					lLstEmpData = lObjLedgerQueryDAOImpl.getEmpDtlsForHouseAdvance(lStrSevaarthId,gStrLocCode);
					if (lLstEmpData != null && lLstEmpData.size() > 0) {
						Object[] lArrObj = (Object[]) lLstEmpData.get(0);
						lStrSevaarthId = (String) lArrObj[0];
						lStrEmpName = (String) lArrObj[1];
						lStrDesignation = (String) lArrObj[2];
						lStrOfficeName = (String) lArrObj[3];
						lStrSubType = (String) lArrObj[4];
						lLngSancAmount = (Long) lArrObj[5];
						lDblSancInterestAmount = (Double) lArrObj[6];
						lStrOrderNo = (String) lArrObj[7];
						lDtOrderDate = (Date) lArrObj[8];
						lLngIntsAmount = (Long) lArrObj[9];
						lDblIntsInterestAmount = (Double) lArrObj[10];
						lIntTotalIns = (Integer) lArrObj[11];
						lIntTotalInterestIns = (Integer) lArrObj[12];
						lLngOddIns = (Long) lArrObj[13];
						lLngOddInsNo = (Long) lArrObj[14];
						lLngOddInterestIns = (Long) lArrObj[15];
						lLngOddInterestInsNo = (Long) lArrObj[16];
						lStrTreasury = (String) lArrObj[17];
						lStrTransactionId = (String) lArrObj[18];

					}
					if (!lStrEmpName.equals("")) {

						rowList = new ArrayList();
						rowList.add("<b>Name of the Employee:</b>" + space(14) + ((lStrEmpName == null)?"":lStrEmpName));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Sevaarth ID:</b>" + space(31) +  ((lStrSevaarthId == null)?"":lStrSevaarthId));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Designation:</b>" + space(31) +  ((lStrDesignation == null)?"":lStrDesignation));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Name of the office:</b>" + space(20) +  ((lStrOfficeName == null)?"":lStrOfficeName));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Loan Sub Type:</b>" + space(26) +  ((lStrSubType == null)?"":lStrSubType));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Sanctioned Amount:</b>" + space(19) +  ((lLngSancAmount == null)?"":lLngSancAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Interest Amount:</b>" + space(24) +  ((lDblSancInterestAmount == null)?"":lDblSancInterestAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Order No:</b>" + space(36) +  ((lStrOrderNo == null)?"":lStrOrderNo));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Order Date:</b>" + space(33) +  ((lDtOrderDate == null)?"":lObjDateFormat.format(lDtOrderDate)));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Principal Installment Amount:</b>" + space(3) +  ((lLngIntsAmount == null)?"":lLngIntsAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Interest Installment Amount:</b>" + space(4) +  ((lDblIntsInterestAmount == null)?"":lDblIntsInterestAmount));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Total Principal Installment:</b>" + space(8) +  ((lIntTotalIns == null)?"":lIntTotalIns));
						lLstDataList.add(rowList);

						rowList = new ArrayList();
						rowList.add("<b>Total Interest Installment:</b>" + space(9) +  ((lIntTotalInterestIns == null)?"":lIntTotalInterestIns));
						lLstDataList.add(rowList);

						/*rowList = new ArrayList();
						rowList.add("Treasury:" + lStrTreasury);
						lLstDataList.add(rowList);*/

						if (lLngOddIns != null) {
							if (lLngOddInsNo == 800056L) {
								rowList = new ArrayList();
								rowList.add("<b>First Odd Principal Installment:</b>" + space(2) + lLngOddIns);
								lLstDataList.add(rowList);
							} else if (lLngOddInsNo == 800057L) {
								rowList = new ArrayList();
								rowList.add("<b>Last Odd Principal Installment:</b>" + space(2) + lLngOddIns);
								lLstDataList.add(rowList);
							}
						}

						if (lLngOddInterestIns != null) {

							if (lLngOddInterestInsNo == 800056L) {
								rowList = new ArrayList();
								rowList.add("<b>First Odd Interest Installment:</b>" + space(3) + lLngOddInterestIns);
								lLstDataList.add(rowList);
							} else if (lLngOddInterestInsNo == 800057L) {
								rowList = new ArrayList();
								rowList.add("<b>Last Odd Interest Installment:</b>" + space(3) + lLngOddInterestIns);
								lLstDataList.add(rowList);
							}
						}
					}
				}
				rowList = new ArrayList();
				rowList.add(newline);
				lLstDataList.add(rowList);
				if (lLstEmpData != null && lLstEmpData.size() > 0) {
				tr = new ArrayList();
				Long lLngTotalPrinAmt = 0L;
				Long lLngTotalInterestAmt = 0L;
				List lLstEmpVoucherDtls = lObjLedgerQueryDAOImpl.getEmpVoucherDtls(lStrSevaarthId, lLngPayrollLoanType, lLngFinYear);
				if (lLstEmpVoucherDtls != null && lLstEmpVoucherDtls.size() > 0) {
					Object[] lArrObj = null;
					for(Integer lInt = 0;lInt<lLstEmpVoucherDtls.size();lInt++){
						lArrObj = (Object[]) lLstEmpVoucherDtls.get(lInt);
						td = new ArrayList();
						td.add(lArrObj[5]);
						//td.add("Voucher");
						td.add( lArrObj[0]);
						
						lLngTotalPrinAmt = lLngTotalPrinAmt + Long.parseLong(lArrObj[1].toString());					
						td.add(lArrObj[1]);
						td.add("");
						
						//td.add(lArrObj[5]);
						td.add(lStrTreasury);
						td.add("V/"+lArrObj[3]+"/"+((lArrObj[4] == null)?"":lObjDateFormat2.format(lArrObj[4])));
						//td.add(lObjDateFormat.format(lArrObj[7]));
						tr.add(td);
					}
				} /*else {
					lLstEmpVoucherDtls = lObjLedgerQueryDAOImpl.getEmpChallanDtlsForApr(lStrSevaarthId, lStrTransactionId, lLngFinYear);
					if (lLstEmpVoucherDtls != null && lLstEmpVoucherDtls.size() > 0) {
						Object[] lArrObj = (Object[]) lLstEmpVoucherDtls.get(0);
						td = new ArrayList();
						td.add("April");
						//td.add("Challan");
						td.add("(From:" + lArrObj[0] + "  To:" + lArrObj[1] + "/" + lIntTotalIns + ")");
						if ("P".equals(lArrObj[3])) {
							lLngTotalPrinAmt = lLngTotalPrinAmt + (Long) lArrObj[2];
							td.add(lArrObj[2]);
							td.add("");
						} else {
							lLngTotalInterestAmt = lLngTotalInterestAmt + (Long) lArrObj[2];
							td.add("");
							td.add(lArrObj[2]);
						}
						//td.add(lArrObj[4]);
						td.add(lStrTreasury);
						td.add(lArrObj[5]);
						td.add(lObjDateFormat.format(lArrObj[6]));
						tr.add(td);
					}*/ else {
						td = new ArrayList();

						td.add("-");
						//td.add("-");
						td.add("-");
						td.add("-");
						td.add("-");
						td.add("-");
						td.add("-");
						//td.add("-");
						//td.add("-");
						tr.add(td);

					//}
				}
			
				td = new ArrayList();

				td.add(new StyledData("Total", boldVO));
				//td.add("");
				td.add("");
				td.add(new StyledData(lLngTotalPrinAmt, boldVO));
				td.add(new StyledData(lLngTotalInterestAmt, boldVO));
				td.add("");
				td.add("");
				//td.add("");
				//td.add("");
				tr.add(td);

				rptTd = new TabularData(tr);
				RptVo = reportsDao.getReport("8000055", lObjReport.getLangId(), lObjReport.getLocId());

				(rptTd).setRelatedReport(RptVo);

				rptList1 = new ArrayList();
				rptList1.add(rptTd);
				lLstDataList.add(rptList1);

				rowList = new ArrayList();
				rowList.add(newline);
				lLstDataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("<b>Recorded by :</b>");
				lLstDataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("<b>Verified by :</b>");
				lLstDataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("<b>Approved by :</b>");
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
				e1.printStackTrace();
				gLogger.error("Exception :" + e1, e1);

			}
		}
		return lLstDataList;
	}

	public List<ComboValuesVO> getLoanType(String lStrLangId, String lStrLocationCode) {

		ArrayList<ComboValuesVO> lArrListLoanType = new ArrayList<ComboValuesVO>();
		List<String> lLstLoan = null;
		Object[] obj;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT lookupId,lookupName FROM CmnLookupMst");
			lSBQuery.append(" WHERE parentLookupId = :lookupId");
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lookupId", Long.parseLong(gObjRsrcBndle.getString("LNA.LOANSADVANCES")));
			lLstLoan = lQuery.list();
			if (!lLstLoan.isEmpty()) {
				Iterator it = lLstLoan.iterator();
				while (it.hasNext()) {
					ComboValuesVO cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lArrListLoanType.add(cmbVO);
				}
			}

		} catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
		}
		return lArrListLoanType;
	}

	public List<ComboValuesVO> getYear(String lStrLangId, String lStrLocationCode) throws Exception {

		ArrayList<ComboValuesVO> arrYear = new ArrayList<ComboValuesVO>();
		List<String> lLstYear = null;
		Object[] obj;
		StringBuilder lSBQuery = new StringBuilder();
		try {

			lSBQuery.append("select finYearCode,finYearDesc from SgvcFinYearMst ");
			lSBQuery.append("where langId = :langId  and finYearCode between '2010' and '2014' order by finYearCode");
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("langId", lStrLangId);
			lLstYear = lQuery.list();
			if (!lLstYear.isEmpty()) {
				Iterator it = lLstYear.iterator();
				while (it.hasNext()) {
					ComboValuesVO cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrYear.add(cmbVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
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

}
