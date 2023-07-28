/**
 * Class Description - 
 *
 *
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0
 * Mar 16, 2011
 */
/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 9, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.SixPCArrearsDAO;
import com.tcs.sgv.dcps.dao.SixPCArrearsDAOImpl;
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
public class SixPCArrearsChkLstReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger.getLogger(DCPSEmployeeAcknowledgementReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;
	ServiceLocator serviceLocator = null;
	SessionFactory lObjSessionFactory = null;

	public Collection findReportData(ReportVO report, Object criteria) throws ReportException {

		String langId = report.getLangId();
		String locId = report.getLocId();
		
		Connection con = null;

		Map requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);

		Map lServiceMap = (Map) requestAttributes.get("serviceMap");
		Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
		Long postId = (Long) lBaseLoginMap.get("loggedInPost");
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

			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			SessionFactory lObjSessionFactory = serviceLocator.getSessionFactorySlave();
			con = lObjSessionFactory.getCurrentSession().connection();
			smt = con.createStatement();
			Map sessionKeys = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			Map loginDetail = (HashMap) sessionKeys.get("loginDetailsMap");

			Long locationId = (Long) loginDetail.get("locationId");

			StringBuffer sql = new StringBuffer();
			String StrSqlQuery = "";

			if (report.getReportCode().equals("700009")) {

				String lStrDdoCode = null;
				String lStrUserType = null;
				String lStrDesignation = null;

				// report.setStyleList(noBorder);

				SixPCArrearsDAO lObjSixPCArrearsDAO = new SixPCArrearsDAOImpl(SixPCArrearsChkLstReport.class, serviceLocator.getSessionFactory());

				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null, serviceLocator.getSessionFactory());

				String url = "ifms.htm?actionFlag=validateLogin";

				StyleVO[] lObjStyleVO = new StyleVO[report.getStyleList().length];
				lObjStyleVO = report.getStyleList();
				for (Integer lInt = 0; lInt < report.getStyleList().length; lInt++) {
					if (lObjStyleVO[lInt].getStyleId() == 26) {
						lObjStyleVO[lInt].setStyleValue(url);
					}
				}

				lStrUserType = report.getParameterValue("UserType").toString();
				if (lStrUserType != null && lStrUserType.length() > 0) {
					if (lStrUserType.equals("DDOAsst")) {
						lStrDdoCode = lObjSixPCArrearsDAO.getDdoCode(postId);
					}

					if (lStrUserType.equals("DDO")) {
						lStrDdoCode = lObjSixPCArrearsDAO.getDdoCodeForDDO(postId);
					}
				}
				OrgDdoMst lObjOrgDdoMst = lObjDcpsCommonDAO.getDDOInfoVOForDDOCode(lStrDdoCode);
				lStrDesignation = lObjOrgDdoMst.getDesignName();

				report.setAdditionalHeader(new StyledData("<b>" + "<font size=\"2px\"> " + lStrDdoCode + " (" + lStrDesignation + ") " + "</font></b>", CenterAlignVO));

				ArrayList rowList = new ArrayList();

				sql = new StringBuffer("select em.EMP_NAME as name, em.DCPS_ID as dcpsId, pc.TOTAL_AMOUNT as totalArrearAmount,pc.status_flag as Status from mst_dcps_emp em join mst_dcps_sixpc pc on em.DCPS_EMP_ID=pc.DCPS_EMP_ID WHERE em.DDO_CODE='" + lStrDdoCode + "' ");

				/*
				if (lStrUserType.equals("DDOAsst")) {
					sql.append(" and pc.STATUS_FLAG in ('D','R') ");
				}
				if (lStrUserType.equals("DDO")) {
					sql.append(" and pc.STATUS_FLAG in ('F','A') ");
				}
				*/
				
				sql.append(" and pc.STATUS_FLAG in ('D','R','F','A')");
				
				sql.append(" order by em.EMP_NAME ASC");
				
				StrSqlQuery = sql.toString();
				rs = smt.executeQuery(StrSqlQuery);
				Integer counter = 1;

				while (rs.next()) {

					rowList = new ArrayList();
					rowList.add(new StyledData(counter, CenterAlignVO));
					rowList.add(new StyledData(rs.getString("name"), LeftAlignVO));
					rowList.add(new StyledData(rs.getString("dcpsId"), CenterAlignVO));
					rowList.add(new StyledData(rs.getString("totalArrearAmount"), CenterAlignVO));

					if (rs.getString("Status").equalsIgnoreCase("A")) {
						rowList.add(new StyledData("APPROVED", CenterAlignVO));
					}
					if (rs.getString("Status").equalsIgnoreCase("D")) {
						rowList.add(new StyledData("DRAFT", CenterAlignVO));
					}
					if (rs.getString("Status").equalsIgnoreCase("R")) {
						rowList.add(new StyledData("REJECTED", CenterAlignVO));
					}
					if (rs.getString("Status").equalsIgnoreCase("F")) {
						rowList.add(new StyledData("PENDING FOR APPROVAL", CenterAlignVO));
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
