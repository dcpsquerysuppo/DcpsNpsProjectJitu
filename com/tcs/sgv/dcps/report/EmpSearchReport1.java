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

public class EmpSearchReport1 extends DefaultReportDataFinder{
	private static final Logger gLogger = Logger
		.getLogger(MonthlyBroadSheetReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
	
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
	
		StringBuffer sql = new StringBuffer();
		String StrSqlQuery = "";
	
		StyleVO[] rowsFontsVO = new StyleVO[5];
		rowsFontsVO[0] = new StyleVO();
		rowsFontsVO[0].setStyleId(IReportConstants.ROWS_PER_PAGE);
		rowsFontsVO[0].setStyleValue("20");
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
		report.initializeDynamicTreeModel();
		report.initializeTreeModel();
	
		report.setStyleList(rowsFontsVO);
	
		StyleVO[] rightAlign = new StyleVO[2];
		rightAlign[0] = new StyleVO();
		rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		rightAlign[0]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
		rightAlign[1] = new StyleVO();
		rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		rightAlign[1].setStyleValue("14");
	
		StyleVO[] centerAlign = new StyleVO[2];
		centerAlign[0] = new StyleVO();
		centerAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		centerAlign[0]
				.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		centerAlign[1] = new StyleVO();
		centerAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		centerAlign[1].setStyleValue("14");
	
		StyleVO[] leftAlign = new StyleVO[2];
		leftAlign[0] = new StyleVO();
		leftAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		leftAlign[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		leftAlign[1] = new StyleVO();
		leftAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		leftAlign[1].setStyleValue("14");
		
		
		if (report.getReportCode().equals("700020")) {
	
			report.setStyleList(noBorder);
			report.setStyleList(rowsFontsVO);
			
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
	        Date date = new Date();
	        String curDate = dateFormat.format(date);
	        
			ArrayList rowList = new ArrayList();
	
			rowList.add(new StyledData("SRK1" + space(95) + "State Record Keeping Agency" + space(70) + "Date : " + curDate,leftAlign));
			dataList.add(rowList);
			
			rowList = new ArrayList();
			rowList.add(new StyledData("New Pension Account Nos.(DCPS) allotted",centerAlign));
			dataList.add(rowList);
			
			rowList = new ArrayList();
			rowList.add(new StyledData("to Employees joining service after 01/11/2005",centerAlign));
			dataList.add(rowList);
			
			rowList = new ArrayList();
			rowList.add(new StyledData("Total No. of Employees : 2",rightAlign));
			dataList.add(rowList);
	
			rowList = new ArrayList();
			rowList.add(new StyledData("Form-1 forwarded by Treasury : 1201" + space(10) + "DDO : 000131", leftAlign));
			dataList.add(rowList);
			
			rowList = new ArrayList();
			rowList.add("" + space(1));
			dataList.add(rowList);
			
			tr = new ArrayList();
			td = new ArrayList();
	
			td = new ArrayList();
			td.add("1");
			td.add("Ghadge Vilas Laxman");
			td.add("11201000131VLGM6001U");
			td.add("M");
			td.add("01/06/1960");
			td.add("02/03/2009");
			td.add("C");
			td.add("Hostel Supdt (male)");
			td.add("4000-100-6000");
			tr.add(td);
			
			td = new ArrayList();
			td.add("2");
			td.add("Jadhav Sahebrao Punju");
			td.add("11201000131SPJM6601W");
			td.add("M");
			td.add("01/06/1966");
			td.add("01/03/2007");
			td.add("C");
			td.add("Clerk Cum Typist");
			td.add("3050-75-3950-80-4590");
			tr.add(td);
			
			rptTd = new TabularData(tr);
			RptVo = reportsDao.getReport("700025", report.getLangId(),
					report.getLocId());
			(rptTd).setRelatedReport(RptVo);
			
			rptList1 = new ArrayList();
			rptList1.add(rptTd);
			dataList.add(rptList1);
			int rowCount = rptTd.getTotalRows() - 1;
			rowList = new ArrayList();
			rowList.add(new StyledData("Total No. of Employees under New Pension Scheme :" + rowCount,centerAlign));
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
