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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO;
import com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;


/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Feb 9, 2011
 */
public class SixPCArrearsYearlyDepositionReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(DCPSEmployeeAcknowledgementReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;

	SessionFactory lObjSessionFactory = null;


	ServiceLocator serviceLocator = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		String locId = report.getLocId();
		
		Connection con = null;

		criteria.getClass();

		Statement smt = null;
		ResultSet rs = null;
		ArrayList dataList = new ArrayList();

		// for Center Alignment format
		StyleVO[] CenterAlignVO = new StyleVO[2];
		CenterAlignVO[0] = new StyleVO();
		CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		CenterAlignVO[1] = new StyleVO();
		CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
		CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
	
		// for Left Alignment format
		StyleVO[] LeftAlignVO = new StyleVO[2];
		LeftAlignVO[0] = new StyleVO();
		LeftAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		LeftAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
		LeftAlignVO[1] = new StyleVO();
		LeftAlignVO[1].setStyleId(IReportConstants.BORDER);
		LeftAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		StyleVO[] noBorder = new StyleVO[1];
		noBorder[0] = new StyleVO();
		noBorder[0].setStyleId(IReportConstants.BORDER);
		noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

		try {

			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lObjSessionFactory = serviceLocator.getSessionFactorySlave();

			Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);

			Map lServiceMap = (Map) requestAttributes.get("serviceMap");
			Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
			Long postId = (Long) lBaseLoginMap.get("loggedInPost");

			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");

			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			Long locationId = (Long) loginDetail.get("locationId");

			String StrSqlQuery = "";

			if (report.getReportCode().equals("700010")) {

				String lStrDdoCode = null;
				String lStrUserType = null;
				String lStrDesignation = null;
				report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				Long yearid = Long.valueOf((String) report.getParameterValue("yearid"));

				SixPCArrearsYearlyDAO lObjSixPCArrearsYearlyDAO = new SixPCArrearsYearlyDAOImpl(SixPCArrearsYearlyDepositionReport.class, serviceLocator.getSessionFactory());

				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());

				lStrUserType = report.getParameterValue("UserType").toString();
				if (lStrUserType != null && lStrUserType.length() > 0) {
					if (lStrUserType.equals("DDOAsst")) {
						lStrDdoCode = lObjSixPCArrearsYearlyDAO.getDdoCode(postId);
					}

					if (lStrUserType.equals("DDO")) {
						lStrDdoCode = lObjSixPCArrearsYearlyDAO.getDdoCodeForDDO(postId);
					}
				}

				OrgDdoMst lObjOrgDdoMst = lObjDcpsCommonDAO.getDDOInfoVOForDDOCode(lStrDdoCode);
				lStrDesignation = lObjOrgDdoMst.getDesignName();

				report.setAdditionalHeader(new StyledData("<b>" + "<font size=\"2px\"> " + lStrDdoCode + " (" + lStrDesignation + ") " + "</font></b>", CenterAlignVO));

				StringBuilder SBQuery = new StringBuilder();

				SBQuery
						.append("SELECT fy.fin_year_desc,EM.DCPS_ID as dcpsId,EM.Emp_name as name,SPC.TOTAL_AMOUNT,SPC.AMOUNT_PAID,nvl(YPC.YEARLY_AMOUNT,0) as amountforYear,nvl(ypc.DCPS_SIXPC_YEARLY_ID,0),EM.DCPS_EMP_ID,fy.fin_year_id,YPC.status_flag as Status");
				SBQuery.append(" FROM sgvc_fin_year_mst fy,mst_dcps_emp EM, ");
				SBQuery.append(" mst_dcps_sixpc SPC LEFT OUTER JOIN rlt_dcps_sixpc_yearly YPC ON SPC.dcps_emp_id=YPC.dcps_emp_id and YPC.fin_year_id=" + yearid);
				SBQuery.append(" WHERE fy.fin_year_id =" + yearid);
				SBQuery.append(" AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND EM.REG_STATUS = 1 and EM.DDO_CODE='" + lStrDdoCode + "' AND SPC.status_flag = 'A' ");

				/*
				if (lStrUserType.equals("DDOAsst")) {
					SBQuery.append(" AND YPC.STATUS_FLAG in ('D','R')");
				}
				if (lStrUserType.equals("DDO")) {
					SBQuery.append(" AND YPC.STATUS_FLAG in ('F','A')");
				}
				if (lStrUserType.equals("TO")) {
					SBQuery.append(" AND YPC.STATUS_FLAG in ('F','A')");
				}
				*/
				
				SBQuery.append(" AND YPC.STATUS_FLAG in ('D','R','F','G','A')");
				
				SBQuery.append(" ORDER BY EM.emp_name");

				StrSqlQuery = SBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);
				Integer counter = 1;

				while (rs.next()) {

					rowList = new ArrayList();
					rowList.add(new StyledData(counter, LeftAlignVO));
					rowList.add(new StyledData(rs.getString("name"), LeftAlignVO));
					rowList.add(new StyledData(rs.getString("dcpsId"), LeftAlignVO));
					rowList.add(new StyledData(rs.getString("amountforYear"), LeftAlignVO));

					if (rs.getString("Status").equals("D")) {
						rowList.add(new StyledData("DRAFT", LeftAlignVO));
					}
					if (rs.getString("Status").equals("F")) {
						rowList.add(new StyledData("PENDING FOR APPROVAL WITH DDO", LeftAlignVO));
					}
					if (rs.getString("Status").equals("G")) {
						rowList.add(new StyledData("PENDING FOR APPROVAL WITH TO", LeftAlignVO));
					}
					if (rs.getString("Status").equals("R")) {
						rowList.add(new StyledData("REJECTED", LeftAlignVO));
					}
					if (rs.getString("Status").equals("A")) {
						rowList.add(new StyledData("APPROVED", LeftAlignVO));
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
				e.printStackTrace();
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

	public List getYear(String lStrLangId, String lStrLocId) {

		ArrayList<ComboValuesVO> arrYear = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;

		String fin_year_id = null;
		String fin_name = null;
		try {

			lCon = DBConnection.getConnection();
			// lCon.setReadOnly(true);

			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer("select * from sgvc_fin_year_mst where lang_id ='" + lStrLangId + "'  and fin_year_code between '2009' and '2013' order by fin_year_code ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				fin_year_id = lRs.getString("fin_year_id");
				fin_name = lRs.getString("fin_year_desc");
				vo.setId(fin_year_id);
				vo.setDesc(fin_name);
				arrYear.add(vo);
			}

		} catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
		} finally {
			try {
				if (lStmt != null) {
					lStmt.close();
				}
				if (lRs != null) {
					lRs.close();
				}
				if (lCon != null) {
					lCon.close();
				}

				lStmt = null;
				lRs = null;
				lCon = null;
			} catch (Exception e) {
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrYear;
	}

}
