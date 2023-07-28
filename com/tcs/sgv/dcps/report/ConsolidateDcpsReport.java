/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 18, 2011		Vihan Khatri								
 *******************************************************************************
 */
/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 18, 2011
 */

package com.tcs.sgv.dcps.report;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.MatchContriEntryDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import java.util.ResourceBundle;
/**
 * Class Description -
 * 
 * 
 * @author Ashish Sharma 
 * @version 0.1
 * @since JDK 7.0 Nov 02, 2014
 */
public class ConsolidateDcpsReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger
	.getLogger(ConsolidateDcpsReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;

	SessionFactory lObjSessionFactory = null;

	ServiceLocator serviceLocator = null;

	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException {


	  String locId = report.getLocId();

		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		ArrayList dataList = new ArrayList();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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

		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		
		
		StyleVO[] leftboldStyleVO  = new StyleVO[3];
		leftboldStyleVO[0] = new StyleVO();
		leftboldStyleVO[0]
		                .setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
		leftboldStyleVO[0]
		                .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_NORMAL);
		leftboldStyleVO[1] = new StyleVO();
		leftboldStyleVO[1]
		                .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		leftboldStyleVO[1].setStyleValue("Left");
		leftboldStyleVO[2] = new StyleVO();
		leftboldStyleVO[2]
		                .setStyleId(IReportConstants.BORDER);
		leftboldStyleVO[2].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		
		
		
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
			
			ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/dcps/dcpsLabels");
			

			SessionFactory lObjSessionFactory = serviceLocator
			.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria)
			.get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			Long locationId = (Long) loginDetail.get("locationId");

			String StrSqlQuery = "";

			if (report.getReportCode().equals("8000149")) {

				// report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				Long treasuryCode = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				String fromDate = ((String) report.getParameterValue("fromDate"));
                String toDate = ((String) report.getParameterValue("toDate"));
				String yearDesc = ((String) report.getParameterValue("yearDesc"));
				Long yearId = Long.valueOf((String) report.getParameterValue("yearId"));
				Long acmaintBy = Long.valueOf((String) report.getParameterValue("acmaintBy"));
				String lStrTreasuryCode=null;
				String lStryTreasuryName=null;
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
				String url = "";

				
					url = "ifms.htm?actionFlag=loadConsolidateTreasury&yearId="
						+ yearId
						+ "&fromDate="
						+ fromDate
						+ "&toDate="
						+ toDate
					    + "&acmaintBy="
					    + acmaintBy;

				StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
				lObjStyleVO = report.getStyleList();
				for (Integer lInt = 0; lInt < report.getStyleList().length; lInt++) {
					if (lObjStyleVO[lInt].getStyleId() == 26) {
						lObjStyleVO[lInt].setStyleValue(url);
					}
				}

				
			
				
				String fromDateQuery = sdf2.format(sdf1.parse(fromDate));
				String toDateQuery = sdf2.format(sdf1.parse(toDate));

				StringBuilder sb = new StringBuilder();

				sb.append(" SELECT loc.LOC_ID,loc.LOC_NAME,mon.MONTH_NAME,(case when trn.MONTH_ID>3 then cast(fin.FIN_YEAR_CODE as bigint) else cast ");
				sb.append(" (fin.FIN_YEAR_CODE as bigint)+1 end),cast(SUM(trn.CONTRIBUTION) as DECIMAL(20,0)) as contribution,cast(SUM(trn.CONTRIBUTION) as DECIMAL(20,0)) as contribution,trn.month_id FROM  mst_dcps_emp emp  ");
				sb.append(" inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
				sb.append(" inner join MST_DCPS_CONTRI_VOUCHER_DTLS dtls on dtls.MST_DCPS_CONTRI_VOUCHER_DTLS=trn.RLT_CONTRI_VOUCHER_ID ");
				sb.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=trn.TREASURY_CODE   ");
				sb.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=emp.AC_DCPS_MAINTAINED_BY   ");
				sb.append(" inner join SGVC_FIN_YEAR_MST fin on fin.FIN_YEAR_ID=trn.FIN_YEAR_ID ");
				sb.append(" inner join SGVA_MONTH_MST mon on mon.MONTH_ID=trn.MONTH_ID ");
				sb.append(" where trn.REG_STATUS=1 and emp.REG_STATUS=1 and emp.DCPS_ID is not null ");
				sb.append(" and loc.DEPARTMENT_ID in (100003,100006) and trn.TREASURY_CODE in (SELECT LOC_ID FROM CMN_LOCATION_MST where LOC_ID="+treasuryCode+" or PARENT_LOC_ID="+treasuryCode+") ");
				sb.append(" and emp.AC_DCPS_MAINTAINED_BY="+acmaintBy+" and trn.FIN_YEAR_ID="+yearId+" ");
				sb.append(" group by loc.LOC_ID,loc.LOC_NAME,trn.MONTH_ID,fin.FIN_YEAR_CODE,mon.MONTH_NAME,mon.month_id ");
				sb.append(" order by loc.LOC_ID ");
				
				StrSqlQuery = sb.toString();
				rs = smt.executeQuery(StrSqlQuery);
				
				
				
				gLogger.info("StrSqlQuery***********"+StrSqlQuery.toString());
				Integer counter = 1;
			
				String urlPrefix = "ifms.htm?actionFlag=reportService&reportCode=8000152&action=generateReport&DirectReport=TRUE&displayOK=TRUE";
				gLogger.info("url Prefix is "+urlPrefix);
				while (rs.next()) {

					rowList = new ArrayList();

					if (!(rs.getString(1).equals("") || rs.getString(1) == null)) {
						lStrTreasuryCode = rs.getString(1).toString().trim();
					}

				
					rowList.add(new URLData(rs.getString(3), urlPrefix
							+ "&treasuryCode=" + rs.getString(1)+ "&yearId=" + yearId
							+ "&acmaintBy="+ acmaintBy+"&monthName="+rs.getString(3)));
					
					gLogger.info("url is "+urlPrefix+ "&treasuryCode=" + rs.getString(1)+ "&yearId=" + yearId
							+ "&acmaintBy="+ acmaintBy+"&monthName="+rs.getString(3));
					/*if (!(rs.getString(3) == null)) {
						if (!rs.getString(3).equals("")) {
							rowList.add(new StyledData(rs.getString(3),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}*/

					if (!(rs.getString(4) == null)) {
						if (!rs.getString(4).equals("")) {
							rowList.add(new StyledData(rs.getString(4),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}


					if (!(rs.getString(5) == null)) {
						if (!rs.getString(5).equals("")) {
							rowList.add(new StyledData(rs.getString(5),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					
					if (!(rs.getString(5) == null)) {
						if (!rs.getString(5).equals("")) {
							rowList.add(new StyledData(rs.getString(5),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					if (!(rs.getString(1).equals("") || rs.getString(1) == null)) {
						lStrTreasuryCode = rs.getString(1).toString().trim();
						//lStrDDOCodePrvs = lStrDDOCode;
						//lStrDDOName = lObjDcpsCommonDAO.getDdoNameForCode(lStrDDOCode);
						if(rs.getString(2) != null)
						{
							lStryTreasuryName = rs.getString(2).toString().trim();
						}
						else
						{
							lStryTreasuryName = "";
						}
						rowList.add(new StyledData(rs.getString(1).toString() + space(3)
								+ lStryTreasuryName, CenterAlignVO));
					} else {
						lStrTreasuryCode = "";
						lStryTreasuryName = "";

						rowList.add(new StyledData("", CenterAlignVO));
					}
					
					
					dataList.add(rowList);
					
				
					counter = counter + 1;
					
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

				} catch (Exception e) {
					e.printStackTrace();
					gLogger.error("Exception :" + e, e);
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
