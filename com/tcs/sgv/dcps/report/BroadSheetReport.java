/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	March 17, 2011		Meeta Thacker								
 *******************************************************************************
 */
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.tcs.sgv.common.util.DBConnection;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mysql.jdbc.PreparedStatement;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 March 17, 2011
 */

	public class BroadSheetReport extends DefaultReportDataFinder{
		private static final Logger gLogger = Logger
			.getLogger(BroadSheetReport.class);
		String Lang_Id = "en_US";
		String Loc_Id = "LC1";
		public static String newline = System.getProperty("line.separator");
		
		@SuppressWarnings("unchecked")
		Map requestAttributes = null;
		ServiceLocator serviceLocator = null;
		SessionFactory lObjSessionFactory = null;
		
		@SuppressWarnings("unchecked")
		public Collection findReportData(ReportVO report, Object criteria)
			throws ReportException {
		
		String langId = report.getLangId();
		
		String locId = report.getLocId();
		Connection con = null;
		
		criteria.getClass();
		
		Statement smt = null;
		ResultSet rs = null;
		
		ReportsDAO reportsDao = new ReportsDAOImpl();
		
		List dataList = new ArrayList();
		ArrayList dataListForMainReport = new ArrayList();
		
		ArrayList dataListForAprilToSept = new ArrayList();
		ArrayList dataListForOctToMarch = new ArrayList();

		ArrayList td = null;
		ArrayList rowList = null;
		
		ReportVO RptVoForAprilToSept = null;
		
		TabularData td1 = null;
		
		
		
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
		
			Long locationId = (Long) loginDetail.get("locationId");
		
			StyleVO[] rowsFontsVO = new StyleVO[4];
			rowsFontsVO[0] = new StyleVO();
			rowsFontsVO[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
			rowsFontsVO[0].setStyleValue("26");
			rowsFontsVO[1] = new StyleVO();
			rowsFontsVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rowsFontsVO[1].setStyleValue("10");
			rowsFontsVO[2] = new StyleVO();
			rowsFontsVO[2].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
			rowsFontsVO[2].setStyleValue("Shruti");
			rowsFontsVO[3] = new StyleVO();
			rowsFontsVO[3].setStyleId(IReportConstants.BACKGROUNDCOLOR);
			rowsFontsVO[3].setStyleValue("white");
			
			
			StyleVO[] boldFontLeftAlign = new StyleVO[3];
			boldFontLeftAlign[0] = new StyleVO();
			boldFontLeftAlign[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldFontLeftAlign[0].setStyleValue("14");
			boldFontLeftAlign[1] = new StyleVO();
			boldFontLeftAlign[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldFontLeftAlign[1]
					.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldFontLeftAlign[2] = new StyleVO();
			boldFontLeftAlign[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			boldFontLeftAlign[2]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
			
			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();
			
			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			
			if (report.getReportCode().equals("700023")) {
				
				//report.setStyleList(noBorder);
				report.setStyleList(rowsFontsVO);
				
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		        Date date = new Date();
		        String curDate = dateFormat.format(date);
		        
		        DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class,
						serviceLocator.getSessionFactory());
		        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
				String lStrYearId = (String) report.getParameterValue("yearId");
		        String lStrTreasuryCode = (String) report.getParameterValue("treasuryCode");
		        
		     	 Long LtreasuryCode=   Long.valueOf(lStrTreasuryCode);
		     	 Long yearId= Long.valueOf(lStrYearId);
		     	 String lStrTreasuryNames = getLocationName(LtreasuryCode);
		     	 String lStrYears = lObjDcpsCommonDAO.getFinYearForYearId(yearId);
		     	  
		     	//Added by ashish to display header			
					report.setAdditionalHeader("Treasury Name :"+lStrTreasuryNames+"\r\nFinancial Year :"+lStrYears);
			//ended by ashish to display header
		     	 
		     	 
		     	 
		        String lStrYear = lObjDcpsCommonDAO.getYearCodeForYearId(Long.valueOf(lStrYearId));
		        SgvcFinYearMst lObjFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(Long.valueOf(lStrYearId));
		        String lStrTreasuryName = lObjDcpsCommonDAO.getLocNameforLocId(Long.valueOf(lStrTreasuryCode));
		        String lStrFinYearDesc = lObjFinYearMst.getFinYearDesc();
		        String lStrFinYearFromDate = sdf.format(lObjFinYearMst.getFromDate());
		        String lStrFinYearToDate = sdf.format(lObjFinYearMst.getToDate());
		        String lStrFinYearPeriod = lStrFinYearFromDate.concat(" - ").concat(lStrFinYearToDate) ; 
		        
		        String lStrDDOCode = "";
		        String lStrDDOName = "";

		        String header1 = "<p align=\"right\"><b>" + "<font size=\"1px\"> "	+ "I : Regular" + "</font></b></p>";
				String header2 = "<p align=\"right\"><b>" + "<font size=\"1px\"> "	+ "II : Delayed" + "</font></b></p>";
				String header3 ="<p align=\"right\"><b>" + "<font size=\"1px\"> "	+ "III : Total" + "</font></b></p>";
				String header4 = "<center><b>" + "<font size=\"2px\"> "	+ "GOVT. of Maharashtra" + "</font></b></center>";
				
				String header6 = "<b>" + "<font size=\"2px\"> " + "Treasury : " + lStrTreasuryName + "</font></b>";
				
				String header5 = "<b><div>" + "<h3 style=\"float: left; width: 34%; text-align: left;\">" + curDate + "</h3>" + "<p style=\"float: left; width: 33%; text-align: center;\">" + "Broad Sheet for the year of " + lStrFinYearDesc + "</p>" + "<p style=\"float: left; width: 33%; text-align: right;\">" + "Period " + lStrFinYearPeriod +"</p></div></b>";
				
				String additionalHeader =  header1 +  header2 + header3 + newline + header4 + header5 + newline + header6;
				report.setAdditionalHeader(additionalHeader);
				
				StringBuffer lSBQuery = new StringBuffer();
				
//				lSBQuery.append(" SELECT em.emp_name,em.dcps_id,nvl(yearly.open_net,0) AS Opening_Balance,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 4 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS April_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 4 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS April_Delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 5 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS May_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 5 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS May_Delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 6 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS June_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 6 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS June_Delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 7 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS July_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 7 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS July_delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 8 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Aug_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 8 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Aug_delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 9 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Sep_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 9 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Sep_delayed,");

				
				lSBQuery.append(" SELECT em.emp_name,em.dcps_id,nvl(yearly.open_net,0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 4 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 4 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 5 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 5 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 6 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 6 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 7 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 7 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 8 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 8 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 9 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 9 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" tr.ddo_code");
				lSBQuery.append(" FROM sgva_month_mst sg");
				lSBQuery.append(" LEFT OUTER JOIN ");
				lSBQuery.append(" trn_dcps_contribution tr ON sg.month_id = tr.month_id  ");
				lSBQuery.append(" AND tr.fin_year_id = :year");
				lSBQuery.append(" JOIN mst_dcps_emp em ON em.dcps_emp_id = tr.dcps_emp_id");
				lSBQuery.append(" LEFT OUTER JOIN mst_dcps_contribution_yearly yearly ");
				lSBQuery.append(" ON yearly.dcps_id = em.dcps_id AND tr.fin_year_id = yearly.year_id");
				lSBQuery.append(" WHERE sg.lang_id = 'en_US'");
				lSBQuery.append(" AND sg.month_id IN (4,5,6,7,8,9)");
				lSBQuery.append(" AND TR.FIN_YEAR_ID = :year ");
				lSBQuery.append(" AND TR.TREASURY_CODE = :treasuryCode");
				lSBQuery.append(" AND em.REG_STATUS = 1");
				lSBQuery.append(" GROUP BY TR.dcps_emp_id");
				lSBQuery.append(" , em.emp_name,em.dcps_id, nvl(yearly.open_net,0),tr.ddo_code");
				lSBQuery.append(" ORDER BY tr.ddo_code,TR.dcps_emp_id ASC");
				
				Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
				Query.setLong("year",Long.valueOf(lStrYearId));
				Query.setLong("treasuryCode",Long.valueOf(lStrTreasuryCode));
				
				List lLstFinal = Query.list();
				
				Integer SrNo = 1 ;
				
				if (lLstFinal != null && !lLstFinal.isEmpty())
				{
					Iterator it = lLstFinal.iterator();
					
					while (it.hasNext())
					{
						Object[] tuple = (Object[]) it.next();
						td = new ArrayList();
						
						// Sr No
						td.add(SrNo);
						SrNo++;
						
						// Name and DCPS Id
						if(tuple[0] != null && tuple[1] != null )
						{
							td.add(tuple[0].toString() + space(1) + "(" + tuple[1].toString()  + ")" );
						}
						else
						{
							td.add("");
						}
						
						// Opening Balance
						if(tuple[2] != null)
						{
							td.add(Double.valueOf(tuple[2].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// April I
						if(tuple[3] != null)
						{
							td.add(Double.valueOf(tuple[3].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// April II
						if(tuple[4] != null)
						{
							td.add(Double.valueOf(tuple[4].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// April III
						if(tuple[3] != null && tuple[4] != null )
						{
							td.add(Double.valueOf(tuple[3].toString()).longValue() + Double.valueOf(tuple[4].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// May I
						if(tuple[5] != null)
						{
							td.add(Double.valueOf(tuple[5].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// May II
						if(tuple[6] != null)
						{
							td.add(Double.valueOf(tuple[6].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// May III
						if(tuple[5] != null && tuple[6] != null)
						{
							td.add(Double.valueOf(tuple[5].toString()).longValue() + Double.valueOf(tuple[5].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// June I
						if(tuple[7] != null)
						{
							td.add(Double.valueOf(tuple[7].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// June II
						if(tuple[8] != null)
						{
							td.add(Double.valueOf(tuple[8].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// June III
						if(tuple[7] != null && tuple[8] != null)
						{
							td.add(Double.valueOf(tuple[7].toString()).longValue() + Double.valueOf(tuple[8].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// July I
						if(tuple[9] != null)
						{
							td.add(Double.valueOf(tuple[9].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// July II
						if(tuple[10] != null)
						{
							td.add(Double.valueOf(tuple[10].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// July III
						if(tuple[9] != null && tuple[10] != null)
						{
							td.add(Double.valueOf(tuple[9].toString()).longValue() + Double.valueOf(tuple[10].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// August I
						if(tuple[11] != null)
						{
							td.add(Double.valueOf(tuple[11].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// August II
						if(tuple[12] != null)
						{
							td.add(Double.valueOf(tuple[12].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// August III
						if(tuple[11] != null && tuple[12] != null)
						{
							td.add(Double.valueOf(tuple[11].toString()).longValue() + Double.valueOf(tuple[12].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// September I
						if(tuple[13] != null)
						{
							td.add(Double.valueOf(tuple[13].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// September II
						if(tuple[14] != null)
						{
							td.add(Double.valueOf(tuple[14].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// September III
						if(tuple[13] != null && tuple[14] != null)
						{
							td.add(Double.valueOf(tuple[13].toString()).longValue() + Double.valueOf(tuple[14].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						
						// For Group By DDO
						if(tuple[15] != null)
						{
							lStrDDOCode = tuple[15].toString().trim();
							lStrDDOName = lObjDcpsCommonDAO.getDdoNameForCode(lStrDDOCode);
							//lStrDDOName = "ABC"; // hard-coded temporarily;
							td.add(new StyledData("(" + lStrDDOCode + ")" + lStrDDOName ,boldFontLeftAlign ));
						}
						else
						{
							td.add("");
						}
						
						dataListForAprilToSept.add(td);
					}
				}
				
				//dataList = new ArrayList();
				
				/*
				List tmpLst = new ArrayList();
				tmpLst.add(1);
				tmpLst.add("A.C.NAIK (12201000130ACNM8401L)");
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add(100);
				tmpLst.add("(2201000130)ABC");
				List tempList2 = new ArrayList();
				tempList2.add(tmpLst);
				*/
				dataList.addAll(dataListForAprilToSept);
				//dataList.addAll(tempList2);
			}
			
			if(report.getReportCode().equals("700048"))
			{
				
				//report.setStyleList(noBorder);
				report.setStyleList(rowsFontsVO);
				
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		        Date date = new Date();
		        String curDate = dateFormat.format(date);
		        
		        DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class,
						serviceLocator.getSessionFactory());
		        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
				String lStrYearId = (String) report.getParameterValue("yearId");
		        String lStrTreasuryCode = (String) report.getParameterValue("treasuryCode");
		        
		        String lStrYear = lObjDcpsCommonDAO.getYearCodeForYearId(Long.valueOf(lStrYearId));
		        SgvcFinYearMst lObjFinYearMst = (SgvcFinYearMst) lObjDcpsCommonDAO.read(Long.valueOf(lStrYearId));
		        String lStrTreasuryName = lObjDcpsCommonDAO.getLocNameforLocId(Long.valueOf(lStrTreasuryCode));
		        String lStrFinYearDesc = lObjFinYearMst.getFinYearDesc();
		        String lStrFinYearFromDate = sdf.format(lObjFinYearMst.getFromDate());
		        String lStrFinYearToDate = sdf.format(lObjFinYearMst.getToDate());
		        String lStrFinYearPeriod = lStrFinYearFromDate.concat(" - ").concat(lStrFinYearToDate) ; 
		        
		        String lStrDDOCode = "";
		        String lStrDDOName = "";

		  /*      String header1 = "<p align=\"right\"><b>" + "<font size=\"1px\"> "	+ "I : Regular" + "</font></b></p>";
				String header2 = "<p align=\"right\"><b>" + "<font size=\"1px\"> "	+ "II : Delayed" + "</font></b></p>";
				String header3 ="<p align=\"right\"><b>" + "<font size=\"1px\"> "	+ "III : Total" + "</font></b></p>";
				String header4 = "<center><b>" + "<font size=\"2px\"> "	+ "GOVT. of Maharashtra" + "</font></b></center>";
				
				String header6 = "<b>" + "<font size=\"2px\"> " + "Treasury : " + lStrTreasuryName + "</font></b>";
				
				String header5 = "<b><div>" + "<h2 style=\"float: left; width: 34%; text-align: left;\">" + curDate + "</h2>" + "<p style=\"float: left; width: 33%; text-align: center;\">" + "Broad Sheet for the year of " + lStrFinYearDesc + "</p>" + "<p style=\"float: left; width: 33%; text-align: right;\">" + "Period " + lStrFinYearPeriod +"</p></div></b>";
				
				String additionalHeader =  header1 +  header2 + header3 + newline + header4 + header5 + newline + header6;
				report.setAdditionalHeader(additionalHeader);
		 */
				
				StringBuffer lSBQuery = new StringBuffer();
				
//				lSBQuery.append(" SELECT em.emp_name,em.dcps_id,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 10 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Oct_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 10 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Oct_Delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 11 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Nov_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 11 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Nov_Delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 12 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Dec_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 12 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Dec_Delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 1 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Jan_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 1 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Jan_delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 2 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Feb_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 2 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS feb_delayed,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 3 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Mar_Regular,");
//				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 3 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) AS Mar_delayed,");
//				lSBQuery.append(" (nvl(yearly.int_contrb_employee,0) +  nvl(yearly.int_contrb_employer,0) + nvl(yearly.open_int,0) )AS Interest,");
//				lSBQuery.append(" nvl(yearly.close_net,0) AS ClosingBalance,");
				
				lSBQuery.append(" SELECT em.emp_name,em.dcps_id,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 10 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 10 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 11 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 11 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 12 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 12 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 1 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 1 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 2 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 2 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 3 THEN (CASE WHEN type_of_payment = 700046 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" nvl(SUM((CASE WHEN sg.month_id = 3 THEN (CASE WHEN type_of_payment = 700047 THEN nvl(tr.contribution,0) ELSE 0 END)END)),0) ,");
				lSBQuery.append(" (nvl(yearly.int_contrb_employee,0) +  nvl(yearly.int_contrb_employer,0) + nvl(yearly.open_int,0) ),");
				lSBQuery.append(" nvl(yearly.close_net,0) ,");
				
				
				lSBQuery.append(" tr.ddo_code");
				lSBQuery.append(" FROM sgva_month_mst sg");
				lSBQuery.append(" LEFT OUTER JOIN ");
				lSBQuery.append(" trn_dcps_contribution tr ON sg.month_id = tr.month_id  ");
				lSBQuery.append(" AND tr.fin_year_id = :year");
				lSBQuery.append(" JOIN mst_dcps_emp em ON em.dcps_emp_id = tr.dcps_emp_id");
				lSBQuery.append(" LEFT OUTER JOIN mst_dcps_contribution_yearly yearly ");
				lSBQuery.append(" ON yearly.dcps_id = em.dcps_id AND tr.fin_year_id = yearly.year_id");
				lSBQuery.append(" WHERE sg.lang_id = 'en_US'");
				lSBQuery.append(" AND sg.month_id IN (10,11,12,1,2,3)");
				lSBQuery.append(" AND TR.FIN_YEAR_ID = :year AND TR.TREASURY_CODE = :treasuryCode");
				lSBQuery.append(" AND TR.REG_STATUS = 1 ");
				lSBQuery.append(" GROUP BY TR.dcps_emp_id");
				lSBQuery.append(" ,em.emp_name,em.dcps_id,(nvl(yearly.int_contrb_employee,0) +  nvl(yearly.int_contrb_employer,0) + nvl(yearly.open_int,0) ),nvl(yearly.close_net,0),tr.ddo_code");
				lSBQuery.append(" ORDER BY tr.ddo_code,TR.dcps_emp_id ASC");
				
				Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
				Query.setLong("year",Long.valueOf(lStrYearId));
				Query.setLong("treasuryCode",Long.valueOf(lStrTreasuryCode));
				
				List lLstFinal = Query.list();
				
				Integer SrNo = 1 ;
				
				if (lLstFinal != null && !lLstFinal.isEmpty())
				{
					Iterator it = lLstFinal.iterator();
					
					while (it.hasNext())
					{
						Object[] tuple = (Object[]) it.next();
						td = new ArrayList();
						
						// Sr No
						td.add(SrNo);
						SrNo++;
						
						// Name and DCPS Id
						if(tuple[0] != null && tuple[1] != null )
						{
							td.add(tuple[0].toString() + space(1) + "(" + tuple[1].toString()  + ")" );
						}
						else
						{
							td.add("");
						}
						
						// October I
						if(tuple[2] != null)
						{
							td.add(Double.valueOf(tuple[2].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// October II
						if(tuple[3] != null)
						{
							td.add(Double.valueOf(tuple[3].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// October III
						if(tuple[2] != null && tuple[3] != null )
						{
							td.add(Double.valueOf(tuple[2].toString()).longValue() + Double.valueOf(tuple[3].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// November I
						if(tuple[4] != null)
						{
							td.add(Double.valueOf(tuple[4].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// November II
						if(tuple[5] != null)
						{
							td.add(Double.valueOf(tuple[5].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// November III
						if(tuple[4] != null && tuple[5] != null)
						{
							td.add(Double.valueOf(tuple[4].toString()).longValue() + Double.valueOf(tuple[5].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// December I
						if(tuple[6] != null)
						{
							td.add(Double.valueOf(tuple[6].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// December II
						if(tuple[7] != null)
						{
							td.add(Double.valueOf(tuple[7].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// December III
						if(tuple[6] != null && tuple[7] != null)
						{
							td.add(Double.valueOf(tuple[6].toString()).longValue() + Double.valueOf(tuple[7].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// January I
						if(tuple[8] != null)
						{
							td.add(Double.valueOf(tuple[8].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// January II
						if(tuple[9] != null)
						{
							td.add(Double.valueOf(tuple[9].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// January III
						if(tuple[8] != null && tuple[9] != null)
						{
							td.add(Double.valueOf(tuple[8].toString()).longValue() + Double.valueOf(tuple[9].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// February I
						if(tuple[10] != null)
						{
							td.add(Double.valueOf(tuple[10].toString()).longValue());
							
						}
						else
						{
							td.add("");
						}
						
						// February II
						if(tuple[11] != null)
						{
							td.add(Double.valueOf(tuple[11].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// February III
						if(tuple[10] != null && tuple[11] != null)
						{
							td.add(Double.valueOf(tuple[10].toString()).longValue() + Double.valueOf(tuple[11].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// March I
						if(tuple[12] != null)
						{
							td.add(Double.valueOf(tuple[12].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// March II
						if(tuple[13] != null)
						{
							td.add(Double.valueOf(tuple[13].toString()).longValue());
						}
						else
						{
							td.add("");
						}
						
						// March III
						if(tuple[12] != null && tuple[13] != null)
						{
							td.add(Double.valueOf(tuple[12].toString()).longValue() + Double.valueOf(tuple[13].toString()).longValue());
							//td.add(11L);
						}
						else
						{
							td.add("");
							//td.add(11L);
						}
						
						//  Interest
						if(tuple[14] != null)
						{
							td.add(Double.valueOf(tuple[14].toString()).longValue());
							//td.add(11L);
						}
						else
						{
							td.add("");
							//td.add(11L);
						}
						
						// Closing Balance
						if(tuple[15] != null)
						{
							td.add(Double.valueOf(tuple[15].toString()).longValue());
							//td.add(11L);
						}
						else
						{
							td.add("");
							//td.add(11L);
						}
						
						// For Group By DDO
						if(tuple[16] != null)
						{
							lStrDDOCode = tuple[16].toString().trim();
							lStrDDOName = lObjDcpsCommonDAO.getDdoNameForCode(lStrDDOCode);
							//lStrDDOName = "ABC"; // hard-coded temporarily;
							td.add(new StyledData("(" + lStrDDOCode + ")" + lStrDDOName ,boldFontLeftAlign ));
						}
						else
						{
							td.add("");
						}
						
						dataListForOctToMarch.add(td);
					}
				}
				
				//	dataList = new ArrayList();
				dataList.addAll(dataListForOctToMarch);
				
				
				
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
		
		public List getAllTreasuries(String lStrLangId, String lStrLocId) {
			
			 List<Object> lArrTreasuries = new ArrayList<Object>();
			  try
			  {
			      Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			      String lStrBufLang = " SELECT locId, locName FROM CmnLocationMst WHERE departmentId in ( 100003,100006)   and LOC_ID<>1111 order by locId ";
			      Query lObjQuery = lObjSession.createQuery(lStrBufLang);
			      
			  	  List lLstResult = lObjQuery.list();
			      ComboValuesVO lObjComboValuesVO = null;
			      Object[] lArrData = null;
			      
			      if(lLstResult != null && !lLstResult.isEmpty())
			      {
			      	for(int lIntCtr=0; lIntCtr  < lLstResult.size();lIntCtr++)
			      	{
			      		lObjComboValuesVO = new ComboValuesVO();
			      		lArrData=(Object[])lLstResult.get(lIntCtr);
			      		lObjComboValuesVO.setId(lArrData[0].toString());
			      		lObjComboValuesVO.setDesc(lArrData[0].toString()+"-"+lArrData[1].toString());
			      		lArrTreasuries.add(lObjComboValuesVO);
			      	}
			      }
			  }
			  catch (Exception e)
			  {
			      gLogger.error("Error is : " + e, e);
			  }
			  return lArrTreasuries;
			}
		
		public List getYear(String lStrLangId, String lStrLocId) {
			
		 List<Object> lArrYears = new ArrayList<Object>();
		  try
		  {
		      Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
		
		      String lStrBufLang = "SELECT finYearId, finYearDesc FROM SgvcFinYearMst WHERE langId = :langId and finYearCode BETWEEN '2007' AND '2015' ORDER BY finYearCode";
		      
		      Query lObjQuery = lObjSession.createQuery(lStrBufLang);
		      lObjQuery.setString("langId", lStrLangId);
		      
		  	List lLstResult = lObjQuery.list();
		      ComboValuesVO lObjComboValuesVO = null;
		      Object[] lArrData = null;
		      
		      if(lLstResult != null && !lLstResult.isEmpty())
		      {
		      	for(int lIntCtr=0; lIntCtr  < lLstResult.size();lIntCtr++)
		      	{
		      		lObjComboValuesVO = new ComboValuesVO();
		      		lArrData=(Object[])lLstResult.get(lIntCtr);
		      		lObjComboValuesVO.setId(lArrData[0].toString());
		      		lObjComboValuesVO.setDesc(lArrData[1].toString());
		      		lArrYears.add(lObjComboValuesVO);
		      	}
		      }
		  }
		  catch (Exception e)
		  {
		      gLogger.error("Error is : " + e, e);
		  }
	  
		  return lArrYears;
		}
		
		public String space(int noOfSpace) {
			String blank = "";
			for (int i = 0; i < noOfSpace; i++) {
				blank += "\u00a0";
			}
			return blank;
		}
		
		//added by ashish to add header with tresury name only 
		public String getLocationName(Long lLngLocId)
		{
			List lLstDept = null;
			String lStrLocName = "";

			try{
				Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append("SELECT LOC_NAME FROM CMN_LOCATION_MST WHERE LOC_ID = :locId");
				Query lQuery = lObjSession.createSQLQuery(lSBQuery.toString());
				lQuery.setParameter("locId", lLngLocId);
				lLstDept = lQuery.list();

				if(lLstDept.size() > 0){
					lStrLocName = lLstDept.get(0).toString();
				}
			}catch(Exception e){
				gLogger.error("Exception in getLocationName:" + e, e);
			}
			return lStrLocName;
		}
		
		
	//ended by ashish to add header with tresury name only 
		
		
	}
