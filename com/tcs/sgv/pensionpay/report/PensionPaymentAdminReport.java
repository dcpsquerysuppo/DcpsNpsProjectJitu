/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 16, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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

import com.tcs.sgv.dcps.report.SixPayArrearAmountDepositionReportSchedule;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * May 16, 2011
 */
public class PensionPaymentAdminReport extends DefaultReportDataFinder
{


	private static final Logger gLogger = Logger
			.getLogger(SixPayArrearAmountDepositionReportSchedule.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/pensionpay/PensionCaseConstants");
	
	public Collection findReportData(ReportVO report, Object criteria)
			throws ReportException {

		gLogger.info("inside reports function");
		gLogger
				.info("***********************Outward Register****************************************");

		String langId = report.getLangId();
		gLogger.info("\n\n LangId is " + langId);

		String locId = report.getLocId();
		gLogger.info("\n\n LocationId is " + locId);
		Connection con = null;
		String lStrBankCode = null;
		String lStrAuditorPostId = null;
		Long lLngYearId = null; 
		
		requestAttributes = (Map) ((Map) criteria)
		.get(IReportConstants.REQUEST_ATTRIBUTES);
serviceLocator = (ServiceLocator) requestAttributes
		.get("serviceLocator");
lObjSessionFactory = serviceLocator.getSessionFactorySlave();
		
		Map lServiceMap = (Map) requestAttributes.get("serviceMap");
		Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
		
		Long postId = (Long) lBaseLoginMap.get("loggedInPost");
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
		

		try 
		{
		
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			gLogger
					.info("***********************11****************************************");
			Map sessionKeys = (Map) ((Map) criteria)
					.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			gLogger.info("Logindetails contents of map :" + loginDetail);
			gLogger
					.info("***********************loginDetail****************************************"
							+ loginDetail);
			// LoginDetails objLoginDetails =
			// (LoginDetails)loginDetail.get("baseLoginVO");

			Long locationId = (Long) loginDetail.get("locationId");
			gLogger.info("Location id from Map :" + locationId);

			new StringBuffer();
			gLogger
					.info("***********************22****************************************");

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
			rowsFontsVO[4]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();

			StyleVO[] bottomBorder = new StyleVO[2];
			bottomBorder[0] = new StyleVO();
			bottomBorder[0].setStyleId(IReportConstants.BORDER);
			bottomBorder[0]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			bottomBorder[1] = new StyleVO();
			bottomBorder[1].setStyleId(IReportConstants.BORDER_BOTTOM);
			bottomBorder[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_MEDIUM);
			report.setStyleList(rowsFontsVO);

			StyleVO[] centerUnderlineBold = new StyleVO[5];
			centerUnderlineBold[0] = new StyleVO();
			centerUnderlineBold[0]
					.setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerUnderlineBold[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			centerUnderlineBold[1] = new StyleVO();
			centerUnderlineBold[1]
					.setStyleId(IReportConstants.STYLE_TEXT_DECORATION);
			centerUnderlineBold[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_TEXT_DECORATION_UNDERLINE);
			centerUnderlineBold[2] = new StyleVO();
			centerUnderlineBold[2]
					.setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			centerUnderlineBold[2]
					.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			centerUnderlineBold[3] = new StyleVO();
			centerUnderlineBold[3].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			centerUnderlineBold[3].setStyleValue("16");
			centerUnderlineBold[4] = new StyleVO();
			centerUnderlineBold[4]
					.setStyleId(IReportConstants.STYLE_FONT_FAMILY);
			centerUnderlineBold[4].setStyleValue("Shruti");

			StyleVO[] centerBold = new StyleVO[4];
			centerBold[0] = new StyleVO();
			centerBold[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerBold[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			centerBold[1] = new StyleVO();
			centerBold[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			centerBold[1]
					.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			centerBold[2] = new StyleVO();
			centerBold[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			centerBold[2].setStyleValue("14");
			centerBold[3] = new StyleVO();
			centerBold[3].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
			centerBold[3].setStyleValue("Shruti");

			StyleVO[] centerBoldBig = new StyleVO[3];
			centerBoldBig[0] = new StyleVO();
			centerBoldBig[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerBoldBig[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			centerBoldBig[1] = new StyleVO();
			centerBoldBig[1].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			centerBoldBig[1]
					.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			centerBoldBig[2] = new StyleVO();
			centerBoldBig[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			centerBoldBig[2].setStyleValue("16");

			StyleVO[] boldUnderline = new StyleVO[3];
			boldUnderline[0] = new StyleVO();
			boldUnderline[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldUnderline[0]
					.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldUnderline[1] = new StyleVO();
			boldUnderline[1].setStyleId(IReportConstants.STYLE_TEXT_DECORATION);
			boldUnderline[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_TEXT_DECORATION_UNDERLINE);
			boldUnderline[2] = new StyleVO();
			boldUnderline[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldUnderline[2].setStyleValue("14");

			StyleVO[] centerUnderlineBoldBig = new StyleVO[4];
			centerUnderlineBoldBig[0] = new StyleVO();
			centerUnderlineBoldBig[0]
					.setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerUnderlineBoldBig[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			centerUnderlineBoldBig[1] = new StyleVO();
			centerUnderlineBoldBig[1]
					.setStyleId(IReportConstants.STYLE_TEXT_DECORATION);
			centerUnderlineBoldBig[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_TEXT_DECORATION_UNDERLINE);
			centerUnderlineBoldBig[2] = new StyleVO();
			centerUnderlineBoldBig[2]
					.setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			centerUnderlineBoldBig[2]
					.setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			centerUnderlineBoldBig[3] = new StyleVO();
			centerUnderlineBoldBig[3]
					.setStyleId(IReportConstants.STYLE_FONT_SIZE);
			centerUnderlineBoldBig[3]
					.setStyleValue(IReportConstants.VALUE_FONT_SIZE_LARGE);

			StyleVO[] rightAlign = new StyleVO[2];
			rightAlign[0] = new StyleVO();
			rightAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightAlign[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			rightAlign[1] = new StyleVO();
			rightAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightAlign[1].setStyleValue("14");

			StyleVO[] centerAlign = new StyleVO[1];
			centerAlign[0] = new StyleVO();
			centerAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerAlign[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);

			StyleVO[] boldVO = new StyleVO[2];
			boldVO[0] = new StyleVO();
			boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldVO[1] = new StyleVO();
			boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldVO[1].setStyleValue("14");

			StyleVO[] leftPadding = new StyleVO[1];
			leftPadding[0] = new StyleVO();
			leftPadding[0].setStyleId(IReportConstants.STYLE_LINE_PADDING_LEFT);
			leftPadding[0].setStyleValue("20");

			StyleVO[] fontStyle = new StyleVO[1];
			fontStyle[0] = new StyleVO();
			fontStyle[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			fontStyle[0].setStyleValue("14");

			StyleVO[] leftAlign = new StyleVO[2];
			leftAlign[0] = new StyleVO();
			leftAlign[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			leftAlign[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
			leftAlign[1] = new StyleVO();
			leftAlign[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			leftAlign[1].setStyleValue("14");

			// for Center Alignment format
			StyleVO[] CenterAlignVO = new StyleVO[2];
			CenterAlignVO[0] = new StyleVO();
			CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			CenterAlignVO[0]
					.setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1]
					.setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			String StrSqlQuery = "";
			if (report.getReportCode().equals("365452")) 
			{

				report.setStyleList(noBorder);
				ArrayList rowList = new ArrayList();

			

				// rowList
				// .add(new StyledData("<b>" + "<font size=\"5px\"> "
				// +
				// "6th PC Arrear Amount(01/01/2006-31/03/2009) deposition to DCPS Contribution CheckList"
				// + "</font></b>",
				// CenterAlignVO));

				// rowList = new ArrayList();
				// rowList.add("" + space(1));
				// dataList.add(rowList);
				// rowList = new ArrayList();
				// rowList.add("" + space(1));
				// dataList.add(rowList);
				// rowList = new ArrayList();
				// rowList.add("" + space(1));
				// dataList.add(rowList);

				
			
				lStrAuditorPostId = report.getParameterValue("AuditorPostId").toString();
				lStrBankCode = report.getParameterValue("BankCode").toString();
				
				
				

				StringBuilder SBQuery = new StringBuilder();

				SBQuery.append("SELECT CONCAT(CONCAT(CONCAT(CONCAT(emp.emp_fname, ' '), emp.emp_mname), ' '), emp.emp_lname) AS Auditor_Name ,bank.bank_name AS Bank_Name,branch.branch_name AS Branch_Name");
				SBQuery.append(" FROM acl_role_mst role, acl_postrole_rlt rolerlt, org_userpost_rlt userpost, mst_bank bank , rlt_auditor_bank audit,rlt_bank_branch branch, ");
				SBQuery
						.append(" org_emp_mst emp, org_post_mst post WHERE branch.branch_code = audit.branch_code AND branch.bank_code = bank.bank_code  AND branch.bank_code = bank.bank_code AND audit.bank_code = bank.bank_code AND ");
				SBQuery.append(" role.role_id=rolerlt.role_id AND userpost.post_id=rolerlt.post_id AND userpost.user_id=emp.user_id ");
				SBQuery
						.append(" AND userpost.post_id=post.post_id AND post.location_code='"+String.valueOf(locationId)+"' AND role.role_id = '"+Long.valueOf(gObjRsrcBndle.getString("PPMT.AUDITORROLE"))+"'  ");

				SBQuery
				.append("AND audit.post_id = '"+Long.valueOf(lStrAuditorPostId)+"'  AND audit.location_code = '"+String.valueOf(locationId)+"' ");
		

				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);
				Integer counter = 1;

				while (rs.next()) {

					rowList = new ArrayList();
					rowList.add(new StyledData(counter, CenterAlignVO));
					rowList.add(new StyledData(rs.getString("Auditor_Name"),CenterAlignVO));
					rowList.add(new StyledData(rs.getString("Bank_Name"),
							CenterAlignVO));
					rowList.add(new StyledData(rs.getString("Branch_Name"),
							CenterAlignVO));
					
					dataList.add(rowList);

					counter = counter + 1;

				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
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
				//e1.printStackTrace();
				gLogger.error("Exception :" + e1, e1);

			}
		}

		gLogger.info("DATALIST SIZE IS" + dataList.size());

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
