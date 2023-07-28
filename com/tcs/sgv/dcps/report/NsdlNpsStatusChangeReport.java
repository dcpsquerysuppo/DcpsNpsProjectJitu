package com.tcs.sgv.dcps.report;

import java.sql.Connection;
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
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

public class NsdlNpsStatusChangeReport extends DefaultReportDataFinder {
	private static final Logger gLogger = Logger
	.getLogger(NsdlNpsStatusChangeReport.class);
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
		ArrayList td;
		
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

			loginDetail.get("locationId");

			new StringBuffer();
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

			if (report.getReportCode().equals("80000870")) {

				ArrayList rowList = new ArrayList();

				report.setStyleList(rowsFontsVO);

				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

				SimpleDateFormat lObjDateFormat1 = new SimpleDateFormat(
				"yyyy-MM-dd");
				SimpleDateFormat lObjDateFormat2 = new SimpleDateFormat(
				"dd/MM/yyyy");
				DateFormat dateFormat5 = new SimpleDateFormat(
				"dd/MM/yy :hh:mm:ss");
				Date date = new Date();
				dateFormat.format(date);

				ServiceLocator.getServiceLocator().getSessionFactory()
				.getCurrentSession();

				OfflineContriDAO objOfflineContriDAO = new OfflineContriDAOImpl(
						OfflineContriDAO.class, serviceLocator
						.getSessionFactory());

				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class,serviceLocator.getSessionFactory());

				String lStrYearId = (String) report.getParameterValue("yearid");
				String lStrMonthId = (String) report.getParameterValue("monthid");

				gLogger.info("lStrYearId"+lStrYearId);
				gLogger.info("lStrMonthId"+lStrMonthId);
				
				Long yearId= Long.valueOf(lStrYearId);
				Long monthId= Long.valueOf(lStrMonthId);
				gLogger.info("yearId"+yearId);
				gLogger.info("monthId"+monthId);

				String lStrYear = lObjDcpsCommonDAO.getFinYearForYearId(yearId);
				String lStrMonthname=lObjDcpsCommonDAO.getMonthForId(monthId);

				gLogger.info("lStrYear"+lStrYear);
				gLogger.info("lStrMonthname"+lStrMonthname);
				
				Long lLongYearId = null;
				Long lLongMonthId = null;
				if (lStrYearId != null && !"".equalsIgnoreCase(lStrYearId)) {
					lLongYearId = Long.valueOf(lStrYearId);
				}
				if (lStrMonthId != null && !"".equalsIgnoreCase(lStrMonthId)) {
					lLongMonthId = Long.valueOf(lStrMonthId);
				}



				
				List TableData = null;
				
				StringBuilder SBQuery = new StringBuilder();
				
				SBQuery.append("SELECT c.LOC_NAME,cast(n.TREASURY_CODE as varchar),n.FILE_NAME,n.TOTAL_AMOUNT,n.REASON,n.REMARKS,n.CREATED_DATE FROM NSDL_NPS_FILE n inner join CMN_LOCATION_MST c on c.LOCATION_CODE = n.TREASURY_CODE where 1=1 and ");
				SBQuery.append(" n.YEAR = '"+lStrYearId+"' and n.MONTH = '"+lStrMonthId+"' ");

				Session ghibSession = ServiceLocator.getServiceLocator()
						.getSessionFactorySlave().getCurrentSession();
				Query lQuery = ghibSession.createSQLQuery(SBQuery.toString());
				
				TableData= lQuery.list();
				if(TableData!=null && TableData.size() >0)
				{
					for(int i = 0 ; i<TableData.size();i++){
					Object[] tuple = (Object[]) TableData.get(i);
					td = new ArrayList();
					if (tuple[0] != null) 
					{
							td.add(i+1);
					}
					else
					{
							td.add("-");
					}
					if (tuple[0] != null) 
					{
							td.add(tuple[0]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[1] != null) 
					{
							td.add(tuple[1]);
					}
					else
					{
							td.add("-");
					}
					
					if (tuple[2] != null) 
					{
							td.add(tuple[2]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[3] != null) 
					{
							td.add(tuple[3]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[4] != null) 
					{
							td.add(tuple[4]);
					}
					else
					{
							td.add("-");
					}
					
					if (tuple[5] != null) 
					{
							td.add(tuple[5]);
					}
					else
					{
							td.add("-");
					}
					if (tuple[6] != null) 
					{
							td.add(dateFormat5.format(tuple[6]));
					}
					else
					{
						
							td.add("-");
					}
					
					dataList.add(td);
				
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

		String lStrBufLang = "SELECT finYearCode, finYearCode FROM SgvcFinYearMst WHERE langId = :langId and finYearCode BETWEEN '2015' AND '2019' ORDER BY finYearCode";

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

}
