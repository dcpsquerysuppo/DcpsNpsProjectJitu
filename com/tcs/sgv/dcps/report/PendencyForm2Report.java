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
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAO;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.core.service.ServiceLocator;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 March 17, 2011
 */

	public class PendencyForm2Report extends DefaultReportDataFinder{
		private static final Logger gLogger = Logger
		.getLogger(PendencyForm2Report.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	
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
		Map sessionKeys = (Map) ((Map) criteria)
				.get(IReportConstants.SESSION_KEYS);
		Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");
	
		
		Long locationId = (Long) loginDetail.get("locationId");
	
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
		rowsFontsVO[3].setStyleValue("white");
		rowsFontsVO[4] = new StyleVO();
		rowsFontsVO[4].setStyleId(IReportConstants.BORDER);
		rowsFontsVO[4].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
	
		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
	
		report.setStyleList(rowsFontsVO);
	
		StyleVO[] centerBoldBig = new StyleVO[3];
		centerBoldBig[0] = new StyleVO();
		centerBoldBig[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		centerBoldBig[0]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		centerBoldBig[1] = new StyleVO();
		centerBoldBig[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		centerBoldBig[1].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		centerBoldBig[2] = new StyleVO();
		centerBoldBig[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		centerBoldBig[2].setStyleValue("16");
	
		StyleVO[] boldVO = new StyleVO[2];
		boldVO[0] = new StyleVO();
		boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
		boldVO[1] = new StyleVO();
		boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		boldVO[1].setStyleValue("14");
		
		if (report.getReportCode().equals("700028")) {
	
			//report.setStyleList(noBorder);
			report.setStyleList(rowsFontsVO);
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
	        Date date = new Date();
	        String curDate = dateFormat.format(date);
	        
			ArrayList rowList = new ArrayList();
	
			rowList = new ArrayList();
			rowList.add("" + space(1));
			dataList.add(rowList);
			
			rowList = new ArrayList();
			rowList.add(new StyledData("GOVT. of Maharashtra",centerBoldBig));
			dataList.add(rowList);
			
			rowList = new ArrayList();
			rowList.add(new StyledData(curDate,boldVO));
			dataList.add(rowList);
					
			rowList = new ArrayList();
			rowList.add(new StyledData("(Treasury,DDO,Office,Employee-wise)" + space(30) + "Pendency of Form1 entries as on " + curDate,boldVO));
			dataList.add(rowList);
			
			rowList = new ArrayList();
			rowList.add(new StyledData("Treasury :AMRAVATI(6101)" + space(35) + "DDO : DISTRICT SAINIK WELFARE OFFICER AMRAVATI(000131)",boldVO));
			dataList.add(rowList);
						
			tr = new ArrayList();
			td = new ArrayList();
			
				
			td = new ArrayList();
			td.add("2");
			td.add("Office 1");
			td.add("239");
			td.add("J V Mathew(1DEMODDDDDDJVMF6501F)");
			td.add("-");
			td.add("-");
			td.add("-");
			td.add("-");
			tr.add(td);
	
			td = new ArrayList();
			td.add("3");
			td.add("Office 2");
			td.add("239");
			td.add("J V Mathew(1DEMODDDDDDJVMF6502A)");
			td.add("-");
			td.add("-");
			td.add("-");
			td.add("-");
			tr.add(td);
			
			rptTd = new TabularData(tr);
			RptVo = reportsDao.getReport("700029", report.getLangId(),
					report.getLocId());
			(rptTd).setRelatedReport(RptVo);
			
			rptList1 = new ArrayList();
			rptList1.add(rptTd);
			dataList.add(rptList1);
			
			int rowCount = rptTd.getTotalRows() - 1;
			
			rowList = new ArrayList();
			rowList.add("No Employees are approved today:");
			dataList.add(rowList);
			
			rowList = new ArrayList();
			rowList.add("No.of Employees approved till date are:" + rowCount);
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
	
	public String space(int noOfSpace) {
	String blank = "";
	for (int i = 0; i < noOfSpace; i++) {
		blank += "\u00a0";
	}
	return blank;
	}
	
}
