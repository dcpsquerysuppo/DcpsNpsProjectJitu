
/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	June 02, 2011		Meeta Thacker								
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
 * @since JDK 5.0 June 02, 2011
 */

public class FormMTR15A extends DefaultReportDataFinder{

	private static final Logger gLogger = Logger
			.getLogger(SixPayArrearAmountDepositionReportSchedule.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";
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
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();

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
			
			if (report.getReportCode().equals("700014")) {

				
				report.setStyleList(noBorder);
				report.setStyleList(rowsFontsVO);

				String header1 = "<center><b>" + "<font size=\"2px\"> "	+ "(See Rule 406-A)" + "</font></b></center>";
				String header2 = "<center><b>" + "<font size=\"2px\"> "	+ "(Staple Receipt)" + "</font></b></center>";
				
				String additionalHeader =  header1 + header2 ;
				report.setAdditionalHeader(additionalHeader);

				ArrayList rowList = new ArrayList();

				rowList.add("" + space(1));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Name of Treasury/ Sub Treasury:",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Token No."+ space(230) +"Office of the"+space(5),leftAlign));
				dataList.add(rowList);
								
				rowList = new ArrayList();
				rowList.add(new StyledData("Director of Accounts & Treasuries",rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Date:"+ space(200) +"Maharashtra State,Mumbai-400021 ",leftAlign));
				//rowList.add(new StyledData("Date:",leftAlign)+new StyledData("Maharashtra State,Mumbai-400021 ",rightAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Voucher No:",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Date:"+ space(240) +"Bill No:"+ space(5),leftAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData(	"HEAD OF ACCOUNT",centerBoldBig));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Administrative Department",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Demand No. ",leftAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Major Head ",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Minor Head",leftAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Sub Head",leftAlign));
				dataList.add(rowList);

				rowList = new ArrayList();
				rowList.add(new StyledData("Detail Head",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("(Subject of Expenditure)"+space(180)+"Payment of Professional &     Service",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("SHRI/SMT.............................................................................................................................................................................",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Received the sum of Rupees............................................................................................................................................................",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("On Account of..........................................................................................",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Sanctioned by........................................................................................................................................................................",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Under is No."+ space(70)+".........................................................................................",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("(Copy enclosed)",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Received Payment",centerAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Signature & Designation",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add("..............................................................................................");
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add("..............................................................................................");
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData(	"FOR USE OF TREASURY",centerBoldBig));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Pay Rs " +space(70)+ "(In words Rs"+space(150)+")",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				rowList = new ArrayList();
				rowList.add("" + space(1));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Date:.......................",leftAlign));
				dataList.add(rowList);
				
				rowList = new ArrayList();
				rowList.add(new StyledData("Demo Treasury",rightAlign));
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

