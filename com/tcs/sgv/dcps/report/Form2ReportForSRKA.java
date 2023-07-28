package com.tcs.sgv.dcps.report;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.EnglishDecimalFormat;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.PageBreak;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;

public class Form2ReportForSRKA extends DefaultReportDataFinder implements
ReportDataFinder {
	private static final Logger gLogger = Logger
	.getLogger(PrintFormR2ConsolidatedReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	private HttpServletRequest request = null;
	public static String newline = System.getProperty("line.separator");

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

		ReportsDAO reportsDao = new ReportsDAOImpl();
		ArrayList dataList = new ArrayList();
		ArrayList tr = null;
		ArrayList td = null;
		ArrayList rptList1 = null;
		TabularData rptTd = null;
		ReportVO RptVo = null;
		ReportVO RptVoForHiddenColumns = null;

		TabularData td1 = null;
		TabularData td2 = null;

		try {
			requestAttributes = (Map) ((Map) criteria)
			.get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes
			.get("serviceLocator");
			Map serviceMap = (Map)requestAttributes.get("serviceMap");		
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

			StyleVO[] rowsFontsVO = new StyleVO[5];
			rowsFontsVO[0] = new StyleVO();
			rowsFontsVO[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
			rowsFontsVO[0].setStyleValue("26");
			rowsFontsVO[1] = new StyleVO();
			rowsFontsVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rowsFontsVO[1].setStyleValue("10");
			rowsFontsVO[2] = new StyleVO();
			rowsFontsVO[2].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			rowsFontsVO[2].setStyleValue("white");
			rowsFontsVO[3] = new StyleVO();
			rowsFontsVO[3].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rowsFontsVO[3]
			            .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
			rowsFontsVO[4] = new StyleVO();
			rowsFontsVO[4].setStyleId(IReportConstants.BORDER);
			rowsFontsVO[4]
			            .setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] normalFontRightAlign = new StyleVO[2];
			normalFontRightAlign[0] = new StyleVO();
			normalFontRightAlign[0]
			                     .setStyleId(IReportConstants.STYLE_FONT_SIZE);
			normalFontRightAlign[0].setStyleValue("10");
			normalFontRightAlign[1] = new StyleVO();
			normalFontRightAlign[1]
			                     .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			normalFontRightAlign[1]
			                     .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);

			StyleVO[] boldAndBigFontCenterAlign = new StyleVO[3];
			boldAndBigFontCenterAlign[0] = new StyleVO();
			boldAndBigFontCenterAlign[0]
			                          .setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldAndBigFontCenterAlign[0].setStyleValue("14");
			boldAndBigFontCenterAlign[1] = new StyleVO();
			boldAndBigFontCenterAlign[1]
			                          .setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldAndBigFontCenterAlign[1]
			                          .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldAndBigFontCenterAlign[2] = new StyleVO();
			boldAndBigFontCenterAlign[2]
			                          .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			boldAndBigFontCenterAlign[2]
			                          .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);

			StyleVO[] boldFontLeftAlign = new StyleVO[3];
			boldFontLeftAlign[0] = new StyleVO();
			boldFontLeftAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldFontLeftAlign[0].setStyleValue("10");
			boldFontLeftAlign[1] = new StyleVO();
			boldFontLeftAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldFontLeftAlign[1]
			                  .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldFontLeftAlign[2] = new StyleVO();
			boldFontLeftAlign[2]
			                  .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			boldFontLeftAlign[2]
			                  .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);

			StyleVO[] boldFontRightAlign = new StyleVO[3];
			boldFontRightAlign[0] = new StyleVO();
			boldFontRightAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldFontRightAlign[0].setStyleValue("10");
			boldFontRightAlign[1] = new StyleVO();
			boldFontRightAlign[1]
			                   .setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldFontRightAlign[1]
			                   .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldFontRightAlign[2] = new StyleVO();
			boldFontRightAlign[2]
			                   .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			boldFontRightAlign[2]
			                   .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			if (report.getReportCode().equals("700103")) {

				report.setStyleList(rowsFontsVO);

				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date date = new Date();
				String curDate = dateFormat.format(date);
				/*				DateFormat dateFormat1 = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss");

				String additionalHeader = "<i><font size=\"1px\"> "
						+ "(As referred to in para no. 14,15,17 & 28 of Government Resolution,Finance Department,No. CPS 1007/18/SER-4,dated 7 July,2007)"
						+ "</font></i>";
				report.setAdditionalHeader(additionalHeader);
				 */
				Map lMapRequestAttributes = null;
				Map lMapSessionAttributes = null;
				List lArrReportData = null;
				LoginDetails lObjLoginVO = null;
				String gStrLocCode = null;
				Long gLngLangId = null;
				Long gLngPostId = null;

				lMapRequestAttributes = (Map) ((Map) criteria)
				.get(IReportConstants.REQUEST_ATTRIBUTES);
				lMapSessionAttributes = (Map) ((Map) criteria)
				.get(IReportConstants.SESSION_KEYS);
				lObjLoginVO = (LoginDetails) lMapSessionAttributes
				.get("loginDetails");
				gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
				gLngLangId = lObjLoginVO.getLangId();
				gLngPostId = lObjLoginVO.getLoggedInPost().getPostId();

				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
						serviceLocator.getSessionFactory());

				OfflineContriDAO lObjOfflineContriDAO = new OfflineContriDAOImpl(TrnDcpsContribution.class, serviceLocator.getSessionFactory());

				/*String lStrDDOCode = lObjDcpsCommonDAO
						 .getDdoCodeForDDO(gLngPostId);
				 */
				request = (HttpServletRequest) serviceMap.get("requestObj");
				String lStrDDOCode = request.getParameter("DDOCode");
				gLogger.info("lStrDDOCode "+lStrDDOCode);
				String lStrDDOName = lObjDcpsCommonDAO
				.getDdoNameForCode(lStrDDOCode);
				DdoOffice lObjDdoOffice = null;
				lObjDdoOffice = lObjDcpsCommonDAO.getDdoMainOffice(lStrDDOCode);
				String lStrDDOOffice = "";
				if(lObjDdoOffice != null)
				{
					lStrDDOOffice = lObjDdoOffice.getDcpsDdoOfficeName();
				}
				String lStrTreasuryCode = lObjDcpsCommonDAO
				.getTreasuryCodeForDDO(lStrDDOCode);
				String lStrAdmBudgetCode =  lObjDcpsCommonDAO.getAdminBudgetCodeForDDO(lStrDDOCode);

				String lStrBillGroupId = (String) report
				.getParameterValue("billGroupId");
				String lStrYearId = (String) report.getParameterValue("yearId");
				String lStrMonthId = (String) report
				.getParameterValue("monthId");

				String lStrAccMaintainedBy = (String) report.getParameterValue("MaintainedBy");

				System.out.println("Account Mainted by==========================="+lStrAccMaintainedBy);

				Long lLongYearId = Long.valueOf(lStrYearId);

				DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

				if(lStrAccMaintainedBy.equals("700240")||lStrAccMaintainedBy.equals("700241")||lStrAccMaintainedBy.equals("700242"))
				{
					String additionalHeader = "<i><font size=\"1px\"> "
						+ "(As referred to in para no. 14,15,17 & 28 of Government Resolution,Finance Department,No. BHAPRASE 2506/C.R.360/06/9 ,dated 13 February,2013)"
						+ "</font></i>";
					report.setAdditionalHeader(additionalHeader);

				}else
				{
					String additionalHeader = "<i><font size=\"1px\"> "
						+ "(As referred to in para no. 14,15,17 & 28 of Government Resolution,Finance Department,No. CPS 1007/18/SER-4,dated 7 July,2007)"
						+ "</font></i>";
					report.setAdditionalHeader(additionalHeader);

				}
				ArrayList rowList = new ArrayList();
				rowList
				.add(new StyledData(
						"Schedule Showing Employer's contribution towards Tier 1 of the New Defined Contribution Pension Scheme",
						boldFontLeftAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Name of Office: " + lStrDDOOffice,
						boldFontLeftAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Name of DDO/Code No.:"
						+ lStrDDOName + space(10) + lStrDDOCode,
						boldFontLeftAlign));
				dataList.add(rowList);

				/*
				rowList = new ArrayList();
				rowList.add(new StyledData("Department Code: " + lStrAdmBudgetCode,
						boldFontLeftAlign));
				dataList.add(rowList);
				 */

				String lStrYearCode = lObjDcpsCommonDAO
				.getYearCodeForYearId(Long.valueOf(lStrYearId));
				String lStrMonth = lObjDcpsCommonDAO.getMonthForId(Long
						.valueOf(lStrMonthId));

				Long lLongMonthId = Long.valueOf(lStrMonthId.trim());
				if(lLongMonthId == 1l || lLongMonthId == 2l || lLongMonthId == 3l)
				{
					Long lLongYearCode = Long.valueOf(lStrYearCode);
					lLongYearCode = lLongYearCode + 1;
					lStrYearCode = lLongYearCode.toString();
				}

				rowList = new ArrayList();
				rowList.add(new StyledData("For the Month of " + lStrMonth
						+ space(2) + lStrYearCode, boldFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Region/Treasury/Sub-Treasury Code:"
						+ lStrTreasuryCode, boldFontRightAlign));
				dataList.add(rowList);

				// Old Query commented

				/*
				StringBuffer lSBQuery = new StringBuffer();

				lSBQuery
						.append(" SELECT EM.dcps_id,EM.emp_name,TR.startDate,TR.endDate,TR.BASIC_PAY,TR.DP,TR.DA,TR.contribution,TR.ddo_code");
				lSBQuery
						.append(" FROM trn_dcps_contribution TR,mst_dcps_emp EM");
				lSBQuery.append(" WHERE TR.dcps_emp_id = EM.dcps_emp_id ");
				lSBQuery.append(" AND TR.month_id = :month");
				lSBQuery.append(" AND TR.FIN_YEAR_ID = :year");
				lSBQuery.append(" AND TR.bill_group_id = :billGroupId");
				lSBQuery.append(" AND TR.TYPE_OF_PAYMENT = 700046");
				lSBQuery.append(" AND TR.REG_STATUS IN (1,3)");
				lSBQuery.append(" AND EM.REG_STATUS = 1");
				lSBQuery.append(" AND EM.dcps_id IS NOT NULL");
				lSBQuery.append(" ORDER BY EM.emp_name ASC");

				Session ghibSession = ServiceLocator.getServiceLocator()
						.getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession
						.createSQLQuery(lSBQuery.toString());
				Query.setLong("month", Long.valueOf(lStrMonthId));
				Query.setLong("year", Long.valueOf(lStrYearId));
				Query.setLong("billGroupId", Long.valueOf(lStrBillGroupId));

				List lLstFinal = Query.list();

				 */

				// New Query

				StringBuilder lSBQuery = new StringBuilder();

				lSBQuery.append(" SELECT EM.dcps_id,EM.emp_name,HES.emp_id,EM.dcps_emp_id,HPP.PO, HPP.D_PAY, HPP.DA, HPP.DCPS, EM.dcps_emp_id");
				lSBQuery.append(" FROM HR_PAY_PAYBILL HPP");
				lSBQuery.append(" join PAYBILL_HEAD_MPG PHM on HPP.PAYBILL_GRP_ID = PHM.PAYBILL_ID and PHM.APPROVE_FLAG in (0,1,5)");
				lSBQuery.append(" join hr_eis_emp_mst HES on HPP.EMP_ID = HES.EMP_ID");
				lSBQuery.append(" join MST_DCPS_EMP EM on EM.org_emp_mst_id = HES.emp_mpg_id");
				lSBQuery.append(" join CMN_LOOKUP_MST look on em.AC_DCPS_MAINTAINED_BY = look.LOOKUP_ID ");
				lSBQuery.append(" where");
				lSBQuery.append(" HPP.PAYBILL_MONTH = :month ");
				lSBQuery.append(" and HPP.PAYBILL_YEAR = :year ");
				lSBQuery.append(" and PHM.BILL_NO = :billGroupId ");
				lSBQuery.append(" and look.LOOKUP_ID = :MaintainedBy ");
				lSBQuery.append(" and PHM.APPROVE_FLAG in (0,1,5)");
				lSBQuery.append(" and EM.dcps_or_gpf = 'Y' ");
				lSBQuery.append(" AND EM.REG_STATUS = 1");
				lSBQuery.append(" AND EM.dcps_id IS NOT NULL");
				lSBQuery.append(" order by EM.emp_name ASC");

				Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
				Query.setLong("month", Long.valueOf(lStrMonthId));
				Query.setLong("year", Long.valueOf(lStrYearCode));
				Query.setLong("billGroupId", Long.valueOf(lStrBillGroupId));
				Query.setLong("MaintainedBy", Long.valueOf(lStrAccMaintainedBy));
               // gLogger.info("SRKA Query is--------------" +Query);
                System.out.println("SRKA Query is--------------" +Query);
				List lLstFinal = Query.list();

				Long lLongSRNo = 1l;
				Double lDoubleTotalContribution = 0d;

				ArrayList dataListForTable = new ArrayList();

				// constructing Start date and end date

				String lStrStartDate =  "01/" + lStrMonthId + "/" + lStrYearCode ;
				lLongMonthId = Long.valueOf(lStrMonthId);

				Integer lIntYearIdCal = Integer.valueOf(lStrYearCode);
				Integer lIntMonthIdCal = Integer.valueOf(lLongMonthId.toString()) - 1;

				Calendar cal = Calendar.getInstance();
				cal.clear();
				cal.set(lIntYearIdCal, lIntMonthIdCal , 1);

				int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				String lStrEndDateDay = Integer.valueOf(days).toString().trim();

				String lStrEndDate = lStrEndDateDay + "/" + lStrMonthId + "/" + lStrYearCode;

				// constructing Start date and end date ends

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				Long lLongHrEisEmpIdFromPaybill = null;
				Boolean BrokenPeriodFlagForEmployee = false;
				Long lLongHrEisEmpIdFromBroken = null;

				Long lLongDcpsEmpId = null;
				List lListBrokenIdPKListForEmp = null;
				List lListBasicStartAndEndDateForBrokenIdPK = null;
				Long lLongBrokenIdPk = null;

				Double lDoubleBasic = 0d;
				Double lDoubleDP = 0d;
				Double lDoubleDA = 0d;
				Double lDoubleContribution = 0d;

				String lStrStartDateBroken = null;
				String lStrEndDateBroken = null;
				String lStrStartDateBrokenForDisplay = null;
				String lStrEndDateBrokenForDisplay = null;

				List lListTempListDPForBrokenId = null;
				List lListTempListDAForBrokenId = null;

				Object lObjTempDA = null;
				Object lObjTempDP = null;

				String lStrEmpName = null;
				String lStrDCPSId = null;

				Object[] lArrObjBasicStartAndEndDateForBrokenIdPK = null;

				List lListDcpsEmpIdAndPCForEISId = null;
				Object[] lArrDcpsEmpIdAndPCForEISId = null;

				List lListEmpListBrokenPeriodDCPS = lObjOfflineContriDAO.getEmpListOfDCPSBrokenPeriodForMonth(lLongYearId,lLongMonthId);

				if (lLstFinal != null && !lLstFinal.isEmpty()) {
					Iterator it = lLstFinal.iterator();

					while (it.hasNext()) {Object[] tuple = (Object[]) it.next();

					BrokenPeriodFlagForEmployee = false;
					lDoubleBasic = 0d;
					lDoubleDP = 0d;
					lDoubleDA = 0d;
					lDoubleContribution = 0d;

					lLongHrEisEmpIdFromPaybill = Long.valueOf(tuple[2].toString().trim());

					if(lListEmpListBrokenPeriodDCPS != null)
					{
						if(lListEmpListBrokenPeriodDCPS.size() != 0)
						{
							for(Integer lIntInner = 0 ; lIntInner < lListEmpListBrokenPeriodDCPS.size() ; lIntInner++)
							{
								if(lListEmpListBrokenPeriodDCPS.get(lIntInner) != null)
								{
									lLongHrEisEmpIdFromBroken = Long.valueOf(lListEmpListBrokenPeriodDCPS.get(lIntInner).toString());
									if((lLongHrEisEmpIdFromBroken.compareTo(lLongHrEisEmpIdFromPaybill) == 0))
									{
										BrokenPeriodFlagForEmployee = true;
										break;
									}
								}
							}
						}
					}

					if(!BrokenPeriodFlagForEmployee)
					{
						td = new ArrayList();
						td.add(lLongSRNo); // SR No

						if (tuple[1] != null) // name
						{
							td.add(tuple[1].toString());
						} else {
							td.add("");
						}

						if (tuple[0] != null) // DCPS ID
						{
							td.add(tuple[0].toString());
						} else {
							td.add("");
						}

						if (tuple[2] != null) // From
						{
							//td.add(sdf.format(tuple[2]));
							td.add(lStrStartDate);
						} else {
							td.add("");
						}

						if (tuple[3] != null) // To
						{
							//td.add(sdf.format(tuple[3]));
							td.add(lStrEndDate);
						} else {
							td.add("");
						}

						if (tuple[4] != null) // Basic Pay
						{
							td.add(Round(Double.parseDouble(tuple[4].toString()), 0));
						} else {
							td.add("");
						}

						if (tuple[5] != null) // DP
						{
							td.add(Round(Double.parseDouble(tuple[5].toString()), 0));
						} else {
							td.add("");
						}

						if (tuple[6] != null) // DA
						{
							td.add(Round(Double.parseDouble(tuple[6].toString()), 0));
						} else {
							td.add("");
						}

						if (tuple[7] != null) // Contribution
						{
							td.add(Round(Double.parseDouble(tuple[7].toString()), 0));
							lDoubleTotalContribution = Double.parseDouble(tuple[7].toString()) + lDoubleTotalContribution;
						} else {
							td.add("");
						}

						td.add(""); // Remarks

						dataListForTable.add(td);

						lLongSRNo++;
					}
					else
					{
						lListBrokenIdPKListForEmp = null;
						lListBrokenIdPKListForEmp = lObjOfflineContriDAO.getBrokenIdPKForEmployee(lLongHrEisEmpIdFromBroken,lLongYearId,lLongMonthId);
						if(lListBrokenIdPKListForEmp != null)
						{
							if(lListBrokenIdPKListForEmp.size() != 0)
							{
								for(Integer lIntCntBrokenIdPk = 0 ;lIntCntBrokenIdPk < lListBrokenIdPKListForEmp.size() ; lIntCntBrokenIdPk++)
								{
									if(lListBrokenIdPKListForEmp.get(lIntCntBrokenIdPk) != null)
									{
										lListBasicStartAndEndDateForBrokenIdPK = null;
										lLongBrokenIdPk = Long.valueOf(lListBrokenIdPKListForEmp.get(lIntCntBrokenIdPk).toString());

										// Gets Basic, Start Date and End Date for Broken Period Cases
										lListBasicStartAndEndDateForBrokenIdPK = lObjOfflineContriDAO.getBasicStartAndEndDateForBrokenIdPK(lLongBrokenIdPk);
										lArrObjBasicStartAndEndDateForBrokenIdPK = (Object[]) lListBasicStartAndEndDateForBrokenIdPK.get(0);

										lDoubleBasic = Double.valueOf(lArrObjBasicStartAndEndDateForBrokenIdPK[0].toString().trim());

										lStrStartDateBroken = lArrObjBasicStartAndEndDateForBrokenIdPK[1].toString().trim();
										lStrStartDateBrokenForDisplay = sdf.format(sdfDate.parse(lStrStartDateBroken.trim()));

										lStrEndDateBroken = lArrObjBasicStartAndEndDateForBrokenIdPK[2].toString().trim();
										lStrEndDateBrokenForDisplay = sdf.format(sdfDate.parse(lStrEndDateBroken.trim()));

										lListDcpsEmpIdAndPCForEISId = null;
										lArrDcpsEmpIdAndPCForEISId = null;

										lListDcpsEmpIdAndPCForEISId = lObjOfflineContriDAO.getDcpsEmpIdAndPCForEisId(lLongHrEisEmpIdFromBroken);
										lArrDcpsEmpIdAndPCForEISId = (Object[]) lListDcpsEmpIdAndPCForEISId.get(0);

										lLongDcpsEmpId = Long.valueOf(lArrDcpsEmpIdAndPCForEISId[0].toString());

										/*
										if(lArrDcpsEmpIdAndPCForEISId[1] != null)
										{
											lStrPayCommission = lArrDcpsEmpIdAndPCForEISId[1].toString();
										}
										 */

										lStrEmpName = lArrDcpsEmpIdAndPCForEISId[2].toString();
										lStrDCPSId = lArrDcpsEmpIdAndPCForEISId[3].toString();

										lDoubleContribution = Double.parseDouble(lObjOfflineContriDAO.getDCPSValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0).toString().trim());

										lListTempListDAForBrokenId = lObjOfflineContriDAO.getDAValueForBrokenPeriodIDPk(lLongBrokenIdPk);

										if(lListTempListDAForBrokenId != null)
										{
											if(lListTempListDAForBrokenId.size() != 0)
											{
												if(lListTempListDAForBrokenId.get(0) != null)
												{
													lObjTempDA = lObjOfflineContriDAO.getDAValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0);
													if(lObjTempDA != null)
													{
														lDoubleDA = Double.parseDouble(lObjOfflineContriDAO.getDAValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0).toString().trim());
													}
												}
											}
										}

										lListTempListDPForBrokenId = lObjOfflineContriDAO.getDPValueForBrokenPeriodIDPk(lLongBrokenIdPk);

										if(lListTempListDPForBrokenId != null)
										{
											if(lListTempListDPForBrokenId.size() != 0)
											{
												if(lListTempListDPForBrokenId.get(0) != null)
												{
													lObjTempDP = lObjOfflineContriDAO.getDPValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0);
													if(lObjTempDP != null)
													{
														lDoubleDP = Double.parseDouble(lObjOfflineContriDAO.getDPValueForBrokenPeriodIDPk(lLongBrokenIdPk).get(0).toString().trim());
													}
												}
											}
										}

										// Display code for employees having broken contributions starts

										td = new ArrayList();
										td.add(lLongSRNo); // SR No

										if (tuple[1] != null) // name
										{
											//td.add(tuple[1].toString());
											td.add(lStrEmpName);
										} else {
											td.add("");
										}

										if (tuple[0] != null) // DCPS ID
										{
											//td.add(tuple[0].toString());
											td.add(lStrDCPSId);
										} else {
											td.add("");
										}

										if (tuple[2] != null) // From
										{
											//td.add(sdf.format(tuple[2]));
											td.add(lStrStartDateBrokenForDisplay);
										} else {
											td.add("");
										}

										if (tuple[3] != null) // To
										{
											//td.add(sdf.format(tuple[3]));
											td.add(lStrEndDateBrokenForDisplay);
										} else {
											td.add("");
										}

										if (tuple[4] != null) // Basic Pay
										{
											//td.add(Round(Double.parseDouble(tuple[4].toString()), 0));
											td.add(Round(lDoubleBasic,0));
										} else {
											td.add("");
										}

										if (tuple[5] != null) // DP
										{
											//td.add(Round(Double.parseDouble(tuple[5].toString()), 0));
											td.add(Round(lDoubleDP,0));
										} else {
											td.add("");
										}

										if (tuple[6] != null) // DA
										{
											//td.add(Round(Double.parseDouble(tuple[6].toString()), 0));
											td.add(Round(lDoubleDA,0));
										} else {
											td.add("");
										}

										if (tuple[7] != null) // Contribution
										{
											//td.add(Round(Double.parseDouble(tuple[7].toString()), 0));
											td.add(Round(lDoubleContribution,0));
											lDoubleTotalContribution = lDoubleContribution + lDoubleTotalContribution;
										} else {
											td.add("");
										}

										td.add(""); // Remarks

										dataListForTable.add(td);

										lLongSRNo++;

										// Display code for employees having broken contributions ends
									}
								}
							}
						}
					}
					}
				}

				if(dataListForTable.size()!=0)
				{
					RptVo = reportsDao.getReport("700038", report.getLangId(),
							report.getLocId()); // The 10 column report for table
					td1 = new TabularData(dataListForTable);
					(td1).setRelatedReport(RptVo);
					rowList = new ArrayList();
					rowList.add(td1);
					dataList.add(rowList);
				}

				ArrayList dataListForTableWithHiddenColumns = new ArrayList();
				RptVoForHiddenColumns = reportsDao.getReport("700038", report
						.getLangId(), report.getLocId());
				ReportColumnVO[] lArrReportColumnVOs = RptVoForHiddenColumns
				.getReportColumns();
				lArrReportColumnVOs[0].setHidden("Y");
				lArrReportColumnVOs[1].setHidden("Y");
				lArrReportColumnVOs[2].setHidden("Y");
				lArrReportColumnVOs[3].setHidden("Y");
				lArrReportColumnVOs[4].setHidden("Y");
				lArrReportColumnVOs[5].setHidden("Y");
				lArrReportColumnVOs[6].setHidden("Y");
				lArrReportColumnVOs[7].setHidden("Y");
				lArrReportColumnVOs[8].setColumnHeader("");
				lArrReportColumnVOs[8].setColumnName("");
				lArrReportColumnVOs[8].setColumnWidth(71);
				lArrReportColumnVOs[9].setColumnHeader("");
				lArrReportColumnVOs[9].setColumnName("");
				lArrReportColumnVOs[9].setColumnWidth(20);
				lArrReportColumnVOs[10].setColumnHeader("");
				lArrReportColumnVOs[10].setColumnName("");
				lArrReportColumnVOs[10].setColumnWidth(9);
				RptVoForHiddenColumns.setReportColumns(lArrReportColumnVOs);

				if(!lStrAccMaintainedBy.equals(""))
				{

					StringBuffer lStrForthLastLineInTable = null;
					StringBuffer lStrThirdLastLineInTable = null;
					StringBuffer lStrSecondLastLineInTable = null;
					StringBuffer lStrLastLineInTable =  null;

					if(lStrAccMaintainedBy.equals("700240"))
					{
						lStrForthLastLineInTable = new StringBuffer(
						"Total Amount of Employee's Contribution - Head Of Account 8342- other Deposits, 117 - ");
						lStrForthLastLineInTable
						.append("Government Employee's Defined Contribution Pension Scheme (01)(08)- Defined Contribution Pension Scheme - Government Servants Contribution ");
						lStrForthLastLineInTable
						.append("(IAS) Tier-1 (8342 522-1)");

						lStrThirdLastLineInTable = new StringBuffer(
						"Add - Employer's contribution");
						lStrThirdLastLineInTable
						.append("Head of Account 8432-Other Deposits 00,117-Government Employees Defined Contribution Pension scheme - (02)(08)Defined Contribution Pension scheme - Employee Contribution(IAS) Tier-1 (8342 5016) ");

						lStrSecondLastLineInTable = new StringBuffer(" 8342- other Deposits 00,117 - ");
						lStrSecondLastLineInTable
						.append(" Defined Contribution Pension Scheme (03)(08)- Defined Contribution Pension Scheme, - Government Servants Contribution  ");
						lStrSecondLastLineInTable
						.append("(IAS) Tier-2 (8342 523-2)");

						lStrLastLineInTable = new StringBuffer(
						"Grand Total");
					}
					else if (lStrAccMaintainedBy.equals("700242"))
					{
						lStrForthLastLineInTable = new StringBuffer(
						"Total Amount of Employee's Contribution - Head Of Account 8342- other Deposits, 117 - ");
						lStrForthLastLineInTable
						.append("Government Employee's Defined Contribution Pension Scheme (01)(10)- Defined Contribution Pension Scheme - Government Servants Contribution ");
						lStrForthLastLineInTable
						.append("(IFS) Tier-1 (8342 526-1)");

						lStrThirdLastLineInTable = new StringBuffer(
						"Add - Employer's contribution");
						lStrThirdLastLineInTable
						.append("Head of Account 8432-Other Deposits 00,117-Government Employees Defined Contribution Pension scheme - (02)(10)Defined Contribution Pension scheme - Employee Contribution(IFS) Tier-1 (8342 503-4) ");

						lStrSecondLastLineInTable = new StringBuffer(" 8342- other Deposits 00,117 - ");
						lStrSecondLastLineInTable
						.append(" Defined Contribution Pension Scheme (03)(10)- Defined Contribution Pension Scheme, - Government Servants Contribution  ");
						lStrSecondLastLineInTable
						.append("(IFS) Tier-2 (8342 527-7)");

						lStrLastLineInTable = new StringBuffer(
						"Grand Total");

					}

					else if(lStrAccMaintainedBy.equals("700241"))
					{

						lStrForthLastLineInTable = new StringBuffer(
						"Total Amount of Employee's Contribution - Head Of Account 8342- other Deposits  00,  117 - ");
						lStrForthLastLineInTable
						.append("Government Employee's Defined Contribution Pension Scheme (01)(09)- Defined Contribution Pension Scheme - Government Servants Contribution ");
						lStrForthLastLineInTable
						.append("(IPS) Tier-1 (8342 524-1)");

						lStrThirdLastLineInTable = new StringBuffer(
						"Add - Employer's contribution");
						lStrThirdLastLineInTable
						.append("Head of Account 8432-Other Deposits 00,117-Government Employees Defined Contribution Pension scheme - (02)(09)Defined Contribution Pension scheme - Employee Contribution(IPS) Tier-1 (8342 5026) ");

						lStrSecondLastLineInTable = new StringBuffer(" 8342- other Deposits 00,117 - ");
						lStrSecondLastLineInTable
						.append(" Defined Contribution Pension Scheme (03)(09)- Defined Contribution Pension Scheme, - Government Servants Contribution  ");
						lStrSecondLastLineInTable
						.append("(IPS) Tier-2 (8342 525-9)");

						lStrLastLineInTable = new StringBuffer(
						"Grand Total"); 

					}

					else
					{

						lStrThirdLastLineInTable = new StringBuffer(
						"Total Amount of Employee's Contribution - Head Of Account 8342- other Deposits, 117 - ");
						lStrThirdLastLineInTable.append
						("Government Employee's Defined Contribution Pension Scheme (02)(01)- Defined Contribution Pension Scheme,");
						lStrThirdLastLineInTable.append
						("Government Employee's Contribution Tier-1(8432-508-1),32-Contributions");

						lStrSecondLastLineInTable = new StringBuffer(
						"Add - Employer's contribution(not applicable in case of Government Employees)");
						lStrSecondLastLineInTable.append
						("Head of Account 8432-Other Deposits,117-Government Employees Defined Contribution Pension scheme");

						lStrLastLineInTable = new StringBuffer(
						"Grand Total");
					}


					if ((lStrAccMaintainedBy.equals("700240")) || (lStrAccMaintainedBy.equals("700242")) || (lStrAccMaintainedBy.equals("700241")))
					{
						td = new ArrayList();
						td.add(new StyledData(lStrForthLastLineInTable, rowsFontsVO));
						td.add(lDoubleTotalContribution);
						td.add("" + space(1));
						dataListForTableWithHiddenColumns.add(td);

						td = new ArrayList();
						td.add(new StyledData(lStrThirdLastLineInTable, rowsFontsVO));
						td.add(0);
						td.add("" + space(1));
						dataListForTableWithHiddenColumns.add(td);

						td = new ArrayList();
						td.add(new StyledData(lStrSecondLastLineInTable, rowsFontsVO));
						td.add(lDoubleTotalContribution);
						td.add("" + space(1));
						dataListForTableWithHiddenColumns.add(td);

						td = new ArrayList();
						td.add(new StyledData(lStrLastLineInTable, rowsFontsVO));
						td.add(lDoubleTotalContribution);
						td.add("" + space(1));
						dataListForTableWithHiddenColumns.add(td);

						td2 = new TabularData(dataListForTableWithHiddenColumns);
						(td2).setRelatedReport(RptVoForHiddenColumns);
						rowList = new ArrayList();
						rowList.add(td2);
						dataList.add(rowList);
					}
					else
					{
						td = new ArrayList();
						td.add(new StyledData(lStrThirdLastLineInTable, rowsFontsVO));
						td.add(lDoubleTotalContribution);
						td.add("" + space(1));
						dataListForTableWithHiddenColumns.add(td);

						td = new ArrayList();
						td.add(new StyledData(lStrSecondLastLineInTable, rowsFontsVO));
						td.add(0);
						td.add("" + space(1));
						dataListForTableWithHiddenColumns.add(td);

						td = new ArrayList();
						td.add(new StyledData(lStrLastLineInTable, rowsFontsVO));
						td.add(lDoubleTotalContribution);
						td.add("" + space(1));
						dataListForTableWithHiddenColumns.add(td);

						td2 = new TabularData(dataListForTableWithHiddenColumns);
						(td2).setRelatedReport(RptVoForHiddenColumns);
						rowList = new ArrayList();
						rowList.add(td2);
						dataList.add(rowList);
					}
				}

				/* Page Break for Certificate */
				rowList = new ArrayList();
				rowList.add(new PageBreak());
				dataList.add(rowList);
				/* Page Break Ends */

				rowList = new ArrayList();
				rowList.add(new StyledData("Total Amount Ruppes: "
						+ lDoubleTotalContribution + "/-", rowsFontsVO));
				dataList.add(rowList);

				String lStrTotAmtInWords = EnglishDecimalFormat
				.convertWithSpace(new BigDecimal(
						lDoubleTotalContribution));

				rowList = new ArrayList();
				rowList.add(new StyledData("In words " + lStrTotAmtInWords,
						rowsFontsVO));
				dataList.add(rowList);

				Object[] lObjScheme = lObjDcpsCommonDAO
				.getSchemeNameFromBillGroup(Long
						.valueOf(lStrBillGroupId));

				rowList = new ArrayList();
				rowList.add(new StyledData("Under the Major Head Of Account "
						+ lObjScheme[1].toString(), rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"Show the details of Service Head of account here",
						rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"Gross Amount Of the bill/challan Rs.=", rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(
						"Net Amount Of the bill/challan Rs.=", rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Challan No." + space(30)
						+ "& Date.", rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("CERTIFICATE",
						boldAndBigFontCenterAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
				.add(new StyledData(
						"Certiffied that I have personally verified the correctness of the details in this schedule and they are found to be correct.",
						rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Date:" + space(35)
						+ "Date of Encashment:", rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("" + space(1), rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Signature", normalFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData(lStrDDOName, normalFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				if(lObjDdoOffice != null)
				{
					rowList.add(new StyledData(lObjDdoOffice.getDcpsDdoOfficeAddress1(), normalFontRightAlign));
				}
				else
				{
					rowList.add(new StyledData("", normalFontRightAlign));
				}
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("For Use of Audit Officer:");
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
				.add(new StyledData(
						"1: Certified htat the name amount of the individual's deduction and the total shown in column(8) have been checked with reference to the bill,vide,paragraph 224 of the Audit Manual",
						rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList
				.add(new StyledData(
						"2: Certified that the rate of pay as shown in column (5) has been verified with the amount actually drawn in the bill ",
						rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("3: Certified that challan for Rs."
						+ space(10) + "is attached to this schedule",
						rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("" + space(1), rowsFontsVO));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Signature", normalFontRightAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Treasury Officer",
						normalFontRightAlign));
				dataList.add(rowList);

				String lStrTreasuryCityForDDO = lObjDcpsCommonDAO
				.getTreasuryCityForDDO(lStrDDOCode);

				rowList = new ArrayList();
				rowList.add(new StyledData(lStrTreasuryCityForDDO,
						normalFontRightAlign));
				dataList.add(rowList);

			}
		}

		catch (Exception e) {
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

	public List getBillGroup( String parentParamValue,String lStrLangId,
			String lStrLocCode) {


		List<Object> lArrBillGroups = new ArrayList<Object>();

		try {
			Session lObjSession = ServiceLocator.getServiceLocator()
			.getSessionFactory().getCurrentSession();

			gLogger.info("current DDO code---------------------"+parentParamValue);

			String lStrBufLang = "SELECT BILL_GROUP_ID,DESCRIPTION FROM MST_DCPS_BILL_GROUP where DDO_CODE =:dcpsDdoCode  ORDER BY DESCRIPTION";

			Query lObjQuery = lObjSession.createSQLQuery(lStrBufLang);

			lObjQuery.setString("dcpsDdoCode", parentParamValue);

			gLogger.info("current ddo script---------------------"+lObjQuery.toString()+" list size "+lObjQuery.list().size());
			System.out.println("Bill group Query----------"+lObjQuery);
			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc("<![CDATA["+lArrData[1].toString()+"]]>");
					lArrBillGroups.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}

		return lArrBillGroups;
	}

	public List getMonth(String lStrLangId, String lStrLocCode) {
		List<Object> lArrMonths = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator()
			.getSessionFactory().getCurrentSession();

			String lStrBufLang = "SELECT monthId, monthName FROM SgvaMonthMst WHERE langId = :langId ORDER BY monthNo";

			Query lObjQuery = lObjSession.createQuery(lStrBufLang);
			lObjQuery.setString("langId", lStrLangId);
			 System.out.println("GEtmon QUERY IS:-----------"+lObjQuery);
			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[1].toString());
					lArrMonths.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}

		return lArrMonths;
	}

	public List getYear(String lStrLangId, String lStrLocId) {

		List<Object> lArrYears = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator()
			.getSessionFactory().getCurrentSession();

			String lStrBufLang = "SELECT finYearId, finYearDesc FROM SgvcFinYearMst WHERE langId = :langId and finYearCode BETWEEN '2007' AND '2015' ORDER BY finYearCode";

			Query lObjQuery = lObjSession.createQuery(lStrBufLang);
			lObjQuery.setString("langId", lStrLangId);
			 System.out.println("GEtyear QUERY IS:-------------"+lObjQuery);
			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[1].toString());
					lArrYears.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}

		return lArrYears;
	}


	public List getAccMaintainedBy(String lStrLangId, String lStrLocId) throws Exception {

		List<Object> lLstAMBList = new ArrayList<Object>();
		try
		{
			Session lObjSession = serviceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			StringBuilder sblist = new StringBuilder();
			sblist.append("SELECT LOOKUP_ID,LOOKUP_DESC from CMN_LOOKUP_MST   ");
			sblist.append("where PARENT_LOOKUP_ID = 700173 and LANG_ID =  ");
			sblist.append("(SELECT LANG_ID from cmn_Language_Mst where LANG_SHORT_NAME =:langId ) ");		 
			Query lQuery = lObjSession.createSQLQuery(sblist.toString());

			System.out.println("Land iD is*************************************************************** "+ lStrLangId);
			lQuery.setString("langId", lStrLangId);
			 System.out.println("GEtacc QUERY IS:--------------"+lQuery);
			List lLstList = lQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object [] lstobj = null;

			if(lLstList!= null && !lLstList.isEmpty())
			{				 
				for (int lIntCtr = 0; lIntCtr < lLstList.size(); lIntCtr++)
				{
					lObjComboValuesVO = new ComboValuesVO();
					lstobj = (Object[]) lLstList.get(lIntCtr);
					lObjComboValuesVO.setId(lstobj[0].toString());
					lObjComboValuesVO.setDesc(lstobj[1].toString());
					lLstAMBList.add(lObjComboValuesVO);
				}	

			}
		}			 			 

		catch(Exception e){
			gLogger.error(" Error in getaccMaintainedBy method :////////////////////// " + e, e);
			throw e;
		}
		return lLstAMBList;

	}



	public List getTreasury(Hashtable otherArgs, String lStrLangId,
			String lStrLocCode)
	{
		List LstFinal = new ArrayList<Object>();

		try
		{
			Session lObjSession = ServiceLocator.getServiceLocator()
			.getSessionFactory().getCurrentSession();

			StringBuilder lStrBufLang = new StringBuilder();
			lStrBufLang.append(" SELECT LOC_ID,LOC_NAME FROM CMN_LOCATION_MST where DEPARTMENT_ID =100003 and LANG_ID =  ");
			lStrBufLang.append("(SELECT LANG_ID from cmn_Language_Mst where LANG_SHORT_NAME =:langId )");

			Query lObjQuery = lObjSession.createSQLQuery(lStrBufLang.toString());
			lObjQuery.setString("langId", lStrLangId);
			 System.out.println("TRESURY QUERY IS:--------------"+lObjQuery);
			List lLstResult = lObjQuery.list();
             
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {


				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[0].toString()+"-"+lArrData[1].toString());
					LstFinal.add(lObjComboValuesVO);
				}
			}	
			else {		
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				LstFinal.add(lObjComboValuesVO);
			}
		}
		catch(Exception e)
		{
			gLogger.info("Error Occure in grt Treasry()"+e);
			e.printStackTrace();
		}


		return LstFinal;



	}


	public List getDDOCodebyTres(String parentParamValue, String lStrLangId, String lStrLocId)
	{
		List LstFinal = new ArrayList<Object>();

		try
		{
			Session lObjSession = ServiceLocator.getServiceLocator()
			.getSessionFactory().getCurrentSession();

			StringBuilder lStrBufLang = new StringBuilder();
			lStrBufLang.append(" SELECT DM.ddo_Code, DM.ddo_Name FROM Rlt_Ddo_Org RO, Org_Ddo_Mst DM,Cmn_Location_Mst LM     ");
			lStrBufLang.append(" WHERE RO.location_Code =:location_Code AND RO.ddo_Code = DM.ddo_Code AND LM.location_Code = RO.location_Code AND DM.DDO_CODE IS NOT NULL   ");

			Query lObjQuery = lObjSession.createSQLQuery(lStrBufLang.toString());

			String treasry_code = parentParamValue; 

			gLogger.info("given location code is-----"+treasry_code);
			lObjQuery.setString("location_Code", treasry_code);
			 System.out.println("GetDDO QUERY IS:--------------"+lObjQuery);
			List lLstResult = lObjQuery.list();

			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {


				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[0].toString());
					LstFinal.add(lObjComboValuesVO);
				}
			}	
			else {

				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				LstFinal.add(lObjComboValuesVO);
			}
		}
		catch(Exception e)
		{
			gLogger.info("Error Occure in getDDOforTres()"+e);
			e.printStackTrace();
		}


		return LstFinal;



	}

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

	public String getNewline() {
		String getNewline = "";
		getNewline = "\\u000d\\u000a";
		return getNewline;
	}

	public static double Round(double Rval, int Rpl) {
		double p = Math.pow(10, Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return tmp / p;
	}
}

