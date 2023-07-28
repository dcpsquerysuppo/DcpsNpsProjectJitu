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

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportParameterVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;

public class MissingCreditsForm2EntriesReport extends DefaultReportDataFinder {
	private static final Logger gLogger = Logger.getLogger(MissingCreditsForm2EntriesReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	public static String newline = System.getProperty("line.separator");

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		Connection con = null;
		Statement smt = null;
		ResultSet rs = null;

		ArrayList dataList = new ArrayList();
		ArrayList tabledata = new ArrayList();
		
		ArrayList td = null;

		try {
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();

			Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			StyleVO[] rowsFontsVO = new StyleVO[5];
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
			rowsFontsVO[3].setStyleValue("black");
			rowsFontsVO[4] = new StyleVO();
			rowsFontsVO[4].setStyleId(IReportConstants.BORDER);
			rowsFontsVO[4].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] withBorder = new StyleVO[1];
			withBorder[0] = new StyleVO();
			withBorder[0].setStyleId(IReportConstants.BORDER);
			withBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_MEDIUM);

			report.setStyleList(rowsFontsVO);

			if (report.getReportCode().equals("700019")) {

				Map lMapSessionAttributes = null;
				LoginDetails lObjLoginVO = null;
				lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
				lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
				lObjLoginVO.getLocation().getLocationCode().trim();
				String lStrDdoCode = (String) report.getParameterValue("ddoCode");
				String dcpsEmpId = (String) report.getParameterValue("EmpName");
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat sdfyyyyMMDD = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				String curDate = dateFormat.format(date);

				String header1 = "<center><b>" + "<font size=\"2px\"> " + "GOVT. of Maharashtra" + "</font></b></center>";
				String header2 = "<center><b>" + "<font size=\"2px\"> " + "Missing Credits (Form2 entries) as on " + curDate + "</font></b></center>";

				String additionalHeader = header1 + header2;
				report.setAdditionalHeader(additionalHeader);

				StringBuilder SBQuery;
				Object[] lArrObj;
				Integer lIntSrNo = 0;
				Long lLngDcpsEmpId;
				String lStrEmpName;
				Date lDtDoj;
				String lStrDsgn;
				String lStrDcpsId;
				
				List lEmpList = getAllEmpsUnderDDO(lStrDdoCode,dcpsEmpId);
				if (!lEmpList.isEmpty()) {
					for (int lInt = 0; lInt < lEmpList.size(); lInt++) {
						lArrObj = (Object[]) lEmpList.get(lInt);
						lLngDcpsEmpId = (Long) lArrObj[0];
						lStrEmpName = (String) lArrObj[1];
						lDtDoj = (Date) lArrObj[2];
						lStrDsgn = (String) lArrObj[3];
						lStrDcpsId = (String) lArrObj[4];
						SBQuery = new StringBuilder();
						SBQuery.append("select ofy.fin_year_desc,ofm.month_name,ofy.fin_year_id,ofm.month_id,nvl(mc.missing_credit_id,0),mc.amount_deduction,"
								+ "mc.voucher_no,mc.voucher_date,mc.remarks,mc.dcps_emp_id from (  select fir.total_year as total_year,fir.total_month as total_month from "
								+ "(select fy.fin_year_code as total_year,fm.month_no total_month from sgva_month_mst fm,sgvc_fin_year_mst fy "
								+ "where  (date(fy.fin_year_code || '-' || fm.month_no || '-' ||01) between '"
								+ lDtDoj
								+ "' and '"
								+ sdfyyyyMMDD.format(date)
								+ "' ) "
								+ " and fm.lang_id = 'en_US') as fir left outer join  "
								+ "(select year(tr.startdate) as pay_year,month(tr.startdate) as pay_month from trn_dcps_contribution tr "
								+ "where tr.dcps_emp_id = "
								+ lLngDcpsEmpId
								+ " and tr.startdate > '"
								+ lDtDoj
								+ "' and tr.enddate < '"
								+ sdfyyyyMMDD.format(date)
								+ "' or (month(tr.enddate) = month('"
								+ sdfyyyyMMDD.format(date)
								+ "') and year(tr.enddate) = year('"
								+ sdfyyyyMMDD.format(date)
								+ "')) "
								+ "and tr.type_of_payment in (700046,700047)) "
								+ "as sec on (fir.total_year = sec.pay_year and fir.total_month = sec.pay_month) "
								+ "where pay_month is null and pay_year is null order by 1,2) as thi "
								+ "join sgvc_fin_year_mst ofy on date(thi.total_year || '-' || thi.total_month || '-' || '01') between ofy.from_date and ofy.to_date "
								+ "join sgva_month_mst ofm on thi.total_month = ofm.month_no and ofm.lang_id = 'en_US' "
								+ "left outer join trn_dcps_missing_credits_dtls mc on  mc.month = ofm.month_id and mc.year = ofy.fin_year_id "
								+ "and mc.dcps_emp_id = "
								+ lLngDcpsEmpId + " order by ofy.fin_year_id,ofm.month_id");

						String StrSqlQuery = SBQuery.toString();
						gLogger.info("Query is **********: " + StrSqlQuery);
						rs = smt.executeQuery(StrSqlQuery);
						
						

						td = new ArrayList();
						report.initializeDynamicTreeModel();
						report.initializeTreeModel();

						if (rs.next()) {
							lIntSrNo = lInt + 1;

							td = new ArrayList();
							td.add(lIntSrNo);
							td.add(lStrDcpsId);
							td.add(lStrEmpName);
							td.add(lStrDsgn);
							td.add(dateFormat.format(lDtDoj));
							td.add(lStrDdoCode);
							tabledata.add(td);

							td = new ArrayList();
							td.add(newline);
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							tabledata.add(td);

							td = new ArrayList();
							td.add("Missing mm/yyyy");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							tabledata.add(td);

							td = new ArrayList();
							td.add(newline);
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							tabledata.add(td);

							Integer lIntCount = 0;
							td = new ArrayList();
							while (rs.next()) {
								String lStrMonth = rs.getString(4);
								if (lStrMonth.length() == 1) {
									lStrMonth = "0" + lStrMonth;
								}
								if (lIntCount % 6 == 0 && lIntCount != 0) {
									tabledata.add(td);
									td = new ArrayList();
									td.add(lStrMonth + "/" + rs.getString(1));
								} else {
									td.add(lStrMonth + "/" + rs.getString(1));
								}

								lIntCount++;
							}
							Integer reminder = 6 - (lIntCount % 6);
							if (reminder > 0) {
								while (reminder > 0) {
									td.add("");
									reminder--;
								}
								tabledata.add(td);
							}
							td = new ArrayList();
							td.add(newline);
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							tabledata.add(td);
						}
					}
				}
			}
			
			if (report.getReportCode().equals("9000505")) 
			{


				Map lMapSessionAttributes = null;
				LoginDetails lObjLoginVO = null;
				lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
				lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
				lObjLoginVO.getLocation().getLocationCode().trim();
				String lStrDdoCode = (String) report.getParameterValue("ddoCode");
				String dcpsEmpId = (String) report.getParameterValue("EmpName");
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat sdfyyyyMMDD = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				String curDate = dateFormat.format(date);
				TabularData rptTd = null;
				ReportVO RptVo = null;
				ArrayList rptList1 = null;
				
				ReportsDAO reportsDao = new ReportsDAOImpl();
				String header1 = "<center><b>" + "<font size=\"2px\"> " + "GOVT. of Maharashtra" + "</font></b></center>";
				String header2 = "<center><b>" + "<font size=\"2px\"> " + "Missing Credits (Form2 entries) as on " + curDate + "</font></b></center>";

				String additionalHeader = header1 + header2;
				report.setAdditionalHeader(additionalHeader);

				StringBuilder SBQuery;
				Object[] lArrObj;
				Integer lIntSrNo = 0;
				Long lLngDcpsEmpId;
				String lStrEmpName;
				Date lDtDoj;
				String lStrDsgn;
				String lStrDcpsId;
				
				List lEmpList = getAllEmpsUnderDDO(lStrDdoCode,dcpsEmpId);
				if (!lEmpList.isEmpty()) {
					for (int lInt = 0; lInt < lEmpList.size(); lInt++) {
						lArrObj = (Object[]) lEmpList.get(lInt);
						lLngDcpsEmpId = (Long) lArrObj[0];
						lStrEmpName = (String) lArrObj[1];
						lDtDoj = (Date) lArrObj[2];
						lStrDsgn = (String) lArrObj[3];
						lStrDcpsId = (String) lArrObj[4];
						SBQuery = new StringBuilder();
						SBQuery.append("select ofy.fin_year_desc,ofm.month_name,ofy.fin_year_id,ofm.month_id,nvl(mc.missing_credit_id,0),mc.amount_deduction,"
								+ "mc.voucher_no,mc.voucher_date,mc.remarks,mc.dcps_emp_id from (  select fir.total_year as total_year,fir.total_month as total_month from "
								+ "(select fy.fin_year_code as total_year,fm.month_no total_month from sgva_month_mst fm,sgvc_fin_year_mst fy "
								+ "where  (date(fy.fin_year_code || '-' || fm.month_no || '-' ||01) between '"
								+ lDtDoj
								+ "' and '"
								+ sdfyyyyMMDD.format(date)
								+ "' ) "
								+ " and fm.lang_id = 'en_US') as fir left outer join  "
								+ "(select year(tr.startdate) as pay_year,month(tr.startdate) as pay_month from trn_dcps_contribution tr "
								+ "where tr.dcps_emp_id = "
								+ lLngDcpsEmpId
								+ " and tr.startdate > '"
								+ lDtDoj
								+ "' and tr.enddate < '"
								+ sdfyyyyMMDD.format(date)
								+ "' or (month(tr.enddate) = month('"
								+ sdfyyyyMMDD.format(date)
								+ "') and year(tr.enddate) = year('"
								+ sdfyyyyMMDD.format(date)
								+ "')) "
								+ "and tr.type_of_payment in (700046,700047)) "
								+ "as sec on (fir.total_year = sec.pay_year and fir.total_month = sec.pay_month) "
								+ "where pay_month is null and pay_year is null order by 1,2) as thi "
								+ "join sgvc_fin_year_mst ofy on date(thi.total_year || '-' || thi.total_month || '-' || '01') between ofy.from_date and ofy.to_date "
								+ "join sgva_month_mst ofm on thi.total_month = ofm.month_no and ofm.lang_id = 'en_US' "
								+ "left outer join trn_dcps_missing_credits_dtls mc on  mc.month = ofm.month_id and mc.year = ofy.fin_year_id "
								+ "and mc.dcps_emp_id = "
								+ lLngDcpsEmpId + " order by ofy.fin_year_id,ofm.month_id");

						String StrSqlQuery = SBQuery.toString();
						gLogger.info("Query2 is **********: " + StrSqlQuery);
						rs = smt.executeQuery(StrSqlQuery);

						td = new ArrayList();
						report.initializeDynamicTreeModel();
						report.initializeTreeModel();

						if (rs.next()) {
							lIntSrNo = lInt + 1;

							td = new ArrayList();
							td.add(lIntSrNo);
							td.add(lStrDcpsId);
							td.add(lStrEmpName);
							td.add(lStrDsgn);
							td.add(dateFormat.format(lDtDoj));
							td.add(lStrDdoCode);
							dataList.add(td);

							td = new ArrayList();
							td.add(newline);
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							dataList.add(td);

							td = new ArrayList();
							td.add("Missing mm/yyyy");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							dataList.add(td);

							td = new ArrayList();
							td.add(newline);
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							dataList.add(td);

							Integer lIntCount = 0;
							td = new ArrayList();
							while (rs.next()) {
								String lStrMonth = rs.getString(4);
								if (lStrMonth.length() == 1) {
									lStrMonth = "0" + lStrMonth;
								}
								if (lIntCount % 6 == 0 && lIntCount != 0) {
									dataList.add(td);
									td = new ArrayList();
									td.add(lStrMonth + "/" + rs.getString(1));
								} else {
									td.add(lStrMonth + "/" + rs.getString(1));
								}

								lIntCount++;
							}
							Integer reminder = 6 - (lIntCount % 6);
							if (reminder > 0) {
								while (reminder > 0) {
									td.add("");
									reminder--;
								}
								dataList.add(td);
							}
							td = new ArrayList();
							td.add(newline);
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							td.add("");
							dataList.add(td);
						}
						
						rptTd = new TabularData(dataList);
						RptVo = reportsDao.getReport("700019", report.getLangId(), report.getLocId());

						(rptTd).setRelatedReport(RptVo);

						rptList1 = new ArrayList();
						rptList1.add(rptTd);
						tabledata.add(rptList1);
					}
				}
			
				
			}
			
		} catch (Exception e) {
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

		return tabledata;
	}
	
	

	public List getAllEmpsUnderDDO(String lStrDdoCode, String dcpsEmpId) {

		gLogger.info("dcpsEmpId is **********: " + dcpsEmpId);
		List<Object> lLstReturnList = null;
		Long dcpsempId=Long.parseLong(dcpsEmpId);
		try {
			StringBuilder sb = new StringBuilder();
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			sb.append(" SELECT EM.dcpsEmpId,EM.name,EM.doj,ODM.dsgnName,EM.dcpsId FROM MstEmp EM,OrgDesignationMst ODM");
			sb.append(" WHERE EM.ddoCode = :DdoCode and EM.dcpsOrGpf = 'Y' and EM.regStatus = 1 AND EM.designation = ODM.dsgnId");
			if(dcpsEmpId!=null && !dcpsEmpId.equals("") && Long.parseLong(dcpsEmpId)!=-1){
				
				sb.append(" AND  EM.dcpsEmpId=:dcpsempId");
			}
			Query selectQuery = lObjSession.createQuery(sb.toString());
			gLogger.info("selectQuery is : " + selectQuery);
			selectQuery.setParameter("DdoCode", lStrDdoCode);
			if(dcpsEmpId!=null && !dcpsEmpId.equals("") && Long.parseLong(dcpsEmpId)!=-1){
				selectQuery.setLong("dcpsempId", dcpsempId);
			}
			lLstReturnList = selectQuery.list();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}
		return lLstReturnList;
	}

	public List getAllDDO(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		String lStrLocationId = null;
		if (loginMap.containsKey("locationId")) {
			lStrLocationId = loginMap.get("locationId").toString();
		}
		List<Object> lLstReturnList = null;

		try {
			StringBuilder sb = new StringBuilder();
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			sb.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
			sb.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND LM.cmnLanguageMst.langId = 1");
			sb.append("ORDER BY DM.ddoCode ");
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
	public List getEmpForDDOReport(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		String lStrLocationId = null;
		if (loginMap.containsKey("locationId")) {
			lStrLocationId = loginMap.get("locationId").toString();
		}
		List<Object> lLstReturnList = null;

		try {
			StringBuilder sb = new StringBuilder();
			Long l=Long.parseLong(lStrLocationId);
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			sb.append("SELECT EM.dcpsEmpId,EM.name FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM,MstEmp EM ");
			sb.append("WHERE RO.ddoOrgId = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND LM.cmnLanguageMst.langId = 1");
			sb.append(" and EM.ddoCode=DM.ddoCode and EM.dcpsOrGpf = 'Y' and EM.regStatus = 1 ");
			Query selectQuery = lObjSession.createQuery(sb.toString());
			selectQuery.setParameter("locationCode", l);
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
	public List getAllEmpUnderDDO(String lStrddoCode, String lStrLangId,
			String lStrLocId) {

		List<Object> lLstReturnList = null;
		try {
			if (!lStrddoCode.equals("-1")) {
				StringBuilder sb = new StringBuilder();
				Session lObjSession = ServiceLocator.getServiceLocator()
				.getSessionFactory().getCurrentSession();
				sb.append(" SELECT EM.dcpsEmpId,EM.name FROM MstEmp EM ");
				sb
				.append(" WHERE EM.ddoCode = :ddoCode and EM.dcpsOrGpf = 'Y' and regStatus = 1  order by EM.name");
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
			gLogger.error("Error is : " + e, e);
		}
		return lLstReturnList; 
	}
}
