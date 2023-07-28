package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.dao.PostEmpContriDAO;
import com.tcs.sgv.dcps.dao.PostEmpContriDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;

public class MonthlyBroadSheetReportForTreasury extends DefaultReportDataFinder {
	private static final Logger gLogger = Logger
			.getLogger(MonthlyBroadSheetReportForDdo.class);
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
		ResultSet rs = null;

		new ReportsDAOImpl();
		ArrayList dataList = new ArrayList();
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
			DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,
					serviceLocator.getSessionFactory());
			SessionFactory lObjSessionFactory = serviceLocator
					.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria)
					.get(IReportConstants.SESSION_KEYS);
			Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
			String lStrLocationId = null;
			if (loginMap.containsKey("locationId")) {
				lStrLocationId = loginMap.get("locationId").toString();
			}

			String StrSqlQuery = "";

			StyleVO[] rowsFontsVO = new StyleVO[4];
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

			StyleVO[] CenterAlignVO = new StyleVO[2];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] LeftAlignVO = new StyleVO[2];
			LeftAlignVO[0] = new StyleVO();
			LeftAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			LeftAlignVO[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
			LeftAlignVO[1] = new StyleVO();
			LeftAlignVO[1].setStyleId(IReportConstants.BORDER);
			LeftAlignVO[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			if (report.getReportCode().equals("700046")) {

				ArrayList rowList = new ArrayList();

				report.setStyleList(rowsFontsVO);

				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

				SimpleDateFormat lObjDateFormat1 = new SimpleDateFormat(
						"yyyy-MM-dd");
				SimpleDateFormat lObjDateFormat2 = new SimpleDateFormat(
						"dd/MM/yyyy");

				Date date = new Date();
				dateFormat.format(date);

				ServiceLocator.getServiceLocator().getSessionFactory()
						.getCurrentSession();

				PostEmpContriDAO objPostEmpContriDAO = new PostEmpContriDAOImpl(
						PostEmpContri.class, serviceLocator.getSessionFactory());
				OfflineContriDAO objOfflineContriDAO = new OfflineContriDAOImpl(
						OfflineContriDAO.class, serviceLocator
								.getSessionFactory());
				PostEmpContri lObjPostEmpContri = null;

				String lStrYearId = (String) report.getParameterValue("yearId");
				String lStrMonthId = (String) report.getParameterValue("monthId");
				String lStrDDOCode = (String) report.getParameterValue("ddoCode");
				
				Long lLongYearId = null;
				Long lLongMonthId = null;
				Long lLongTreasuryCode = Long.parseLong(lStrLocationId);

				if (lStrYearId != null && !"".equalsIgnoreCase(lStrYearId)) {
					lLongYearId = Long.valueOf(lStrYearId.trim());
				}
				if (lStrMonthId != null && !"".equalsIgnoreCase(lStrMonthId)) {
					lLongMonthId = Long.valueOf(lStrMonthId.trim());
				}

				StringBuilder lSBQuery = new StringBuilder();
				
				lSBQuery.append(" Select EM.DCPS_ID AS DcpsId,EM.EMP_NAME as EmpName,TR.CONTRIBUTION as Contribution,TR.TYPE_OF_PAYMENT as TypeOfPayment,VC.voucher_no as VoucherNo,VC.voucher_date as VoucherDate,TR.DDO_CODE as ddoCode,TR.DCPS_EMP_ID as dcpsEmpId,TR.EMPLOYER_CONTRI_FLAG as EmployerContriFlag,VC.emplr_voucher_no as emplrVoucherNo, VC.emplr_voucher_date emplrVoucherDate ");
				lSBQuery.append(" from mst_dcps_emp EM,trn_dcps_contribution TR,mst_dcps_contri_voucher_dtls VC ");
				lSBQuery.append(" where EM.DCPS_EMP_ID = TR.DCPS_EMP_ID ");
				lSBQuery.append(" AND tr.rlt_contri_voucher_id = VC.mst_dcps_contri_voucher_dtls");
				lSBQuery.append(" and TR.FIN_YEAR_ID = " + lLongYearId);
				lSBQuery.append(" and TR.MONTH_ID = " + lLongMonthId);
				lSBQuery.append(" and TR.TREASURY_CODE = " + lLongTreasuryCode);
				lSBQuery.append(" and TR.DDO_CODE = '" +  lStrDDOCode.trim()+"'");
				lSBQuery.append(" and TR.REG_STATUS = 1 ");
				lSBQuery.append(" AND EM.REG_STATUS = 1 ");
				lSBQuery.append(" AND EM.DCPS_ID IS NOT NULL");
				lSBQuery.append(" order by TR.DDO_CODE ASC,EM.EMP_NAME");

				StrSqlQuery = lSBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);

				String lStrDcpsId = null;
				String lStrEmpName = null;
				String lStrEmployeeContribution = null;
				Double lDoubleContributionEmployee = 0d;
				Double lDoubleContributionEmployer = 0d;
				Double lDoubleContributionRcvdforPrvsMonths = 0d;
				Double lDoubleTotalContribution = 0d;
				Double lDoubleGrandTotal = 0d;
				String lStrVoucherNo = null;
				String lStrVoucherDate = null;
				String lStrEmployerContriFlag = null;

				Integer lIntCounter = 1;

				while (rs.next()) {
					
					if(!rs.getString("TypeOfPayment").equals("700046"))
					{
						continue ;
					}
					
					String lStrDdoCode = rs.getString("ddoCode");
					rowList = new ArrayList();

					rowList.add(new StyledData(lIntCounter, CenterAlignVO));
					lIntCounter = lIntCounter + 1;

					lStrDcpsId = rs.getString("DcpsId");
					if (!(lStrDcpsId == null)) {
						if (!lStrDcpsId.equals("")) {
							rowList
									.add(new StyledData(lStrDcpsId, LeftAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					lStrEmpName = rs.getString("EmpName");
					if (!(lStrEmpName == null)) {
						if (!lStrEmpName.equals("")) {
							rowList
									.add(new StyledData(lStrEmpName,
											LeftAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					lStrEmployeeContribution = rs.getString("Contribution");
					if (lStrEmployeeContribution != null
							&& !lStrEmployeeContribution.equals("")) {
						lDoubleContributionEmployee = Double
								.valueOf(lStrEmployeeContribution);
					}

					if (!(lStrEmployeeContribution == null)) {
						if (!lStrEmployeeContribution.equals("")) {
							rowList
									.add(new StyledData(
											lDoubleContributionEmployee,
											CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					lDoubleContributionRcvdforPrvsMonths = objOfflineContriDAO
							.getDelayedContribution(Long.parseLong(rs
									.getString("dcpsEmpId")), lLongYearId,
									lLongMonthId);

					rowList
							.add(new StyledData(
									lDoubleContributionRcvdforPrvsMonths,
									CenterAlignVO));

					lDoubleTotalContribution = lDoubleContributionEmployee
							+ lDoubleContributionRcvdforPrvsMonths;

					rowList.add(new StyledData(lDoubleTotalContribution,
							CenterAlignVO));

					lStrVoucherNo = rs.getString("VoucherNo");
					lStrVoucherDate = rs.getString("VoucherDate");
					if (!(lStrVoucherNo == null || lStrVoucherDate == null)) {
						if (!lStrVoucherNo.equals("")
								&& !lStrVoucherDate.equals("")) {
							rowList.add(new StyledData(lStrVoucherNo
									+ "-"
									+ lObjDateFormat2.format(lObjDateFormat1
											.parse(lStrVoucherDate)),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					//lDoubleContributionEmployer = lDoubleTotalContribution;
					
					lStrEmployerContriFlag = rs.getString("EmployerContriFlag");
					if(lStrEmployerContriFlag != null)
					{
						if(!"".equals(lStrEmployerContriFlag))
						{
							if(lStrEmployerContriFlag.equals("Y"))
							{
								lDoubleContributionEmployer = lDoubleContributionEmployee ;
							}
						}
					}
					
					lDoubleContributionEmployer = lDoubleContributionEmployer + objOfflineContriDAO.getDelayedContributionMatched(Long.parseLong(rs.getString("dcpsEmpId")), lLongYearId,lLongMonthId);

					if(lDoubleContributionEmployer != 0d)
					{
						rowList.add(new StyledData(lDoubleContributionEmployer,
								CenterAlignVO));
					}
					else
					{
						rowList.add(new StyledData(" - ",
								CenterAlignVO));
					}

			/*		lObjPostEmpContri = objPostEmpContriDAO
							.getPostEmpContriVOForGivenMonthAndYear(
									lLongMonthId, lLongYearId);

					if (lObjPostEmpContri != null
							&& lObjPostEmpContri.getVoucherNo() != null
							&& lObjPostEmpContri.getVoucherDate() != null) {
						rowList.add(new StyledData(lObjPostEmpContri
								.getVoucherNo()
								+ "-"
								+ lObjDateFormat2.format(lObjPostEmpContri
										.getVoucherDate()), CenterAlignVO));
					} else {
						rowList.add(new StyledData("-  -", CenterAlignVO));
					}
*/
				// Added by Ashish for to login	

					if (rs.getString("emplrVoucherNo") !=null &&  rs.getString("emplrVoucherDate") !=null){
						rowList.add(new StyledData(rs.getString("emplrVoucherNo")
								+ "-"
								+ lObjDateFormat2.format(lObjDateFormat1.parse(rs.getString("emplrVoucherDate"))), CenterAlignVO));
					} else {
						rowList.add(new StyledData("-  -", CenterAlignVO));
					}
					
					lDoubleGrandTotal = lDoubleTotalContribution
							+ lDoubleContributionEmployer;
					rowList
							.add(new StyledData(lDoubleGrandTotal,
									CenterAlignVO));

					String lStrDdoName = lObjDcpsCommonDAO
							.getDdoNameForCode(lStrDdoCode);

					rowList.add(lStrDdoCode + " - " + lStrDdoName);

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

	public List getMonth(String lStrLangId, String lStrLocCode) {
		List<Object> lArrMonths = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator()
					.getSessionFactory().getCurrentSession();

			String lStrBufLang = "SELECT monthId, monthName FROM SgvaMonthMst WHERE langId = :langId ORDER BY monthNo";

			Query lObjQuery = lObjSession.createQuery(lStrBufLang);
			lObjQuery.setString("langId", lStrLangId);

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

			String lStrBufLang = "SELECT finYearId, finYearDesc FROM SgvcFinYearMst WHERE langId = :langId and finYearCode BETWEEN '2008' AND '2014' ORDER BY finYearCode";

			Query lObjQuery = lObjSession.createQuery(lStrBufLang);
			lObjQuery.setString("langId", lStrLangId);

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

	public List getAllDDOsInTreasury(Hashtable otherArgs, String lStrLangId,
			String lStrLocCode) {

		Hashtable sessionKeys = (Hashtable) (otherArgs)
				.get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		String lStrLocationId = null;
		if (loginMap.containsKey("locationId")) {
			lStrLocationId = loginMap.get("locationId").toString();
		}
		List<Object> lLstReturnList = null;

		try {
			StringBuilder sb = new StringBuilder();
			Session lObjSession = ServiceLocator.getServiceLocator()
					.getSessionFactory().getCurrentSession();
			sb
					.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
			sb
					.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode");
			sb.append(" order by DM.ddoCode");
			Query selectQuery = lObjSession.createQuery(sb.toString());
			selectQuery.setParameter("locationCode", lStrLocationId);
			List lLstResult = selectQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc("("+obj[0].toString()+")"+obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return lLstReturnList;
	}

}
