package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.dao.PostEmpContriDAO;
import com.tcs.sgv.dcps.dao.PostEmpContriDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;
import com.tcs.sgv.dcps.report.BroadSheetReport;

public class MonthlyBroadSheetReport extends DefaultReportDataFinder {
	private static final Logger gLogger = Logger
			.getLogger(MonthlyBroadSheetReport.class);
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

			if (report.getReportCode().equals("700024")) {

				ArrayList rowList = new ArrayList();

				report.setStyleList(rowsFontsVO);

				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

				SimpleDateFormat lObjDateFormat1 = new SimpleDateFormat(
						"yyyy-MM-dd");
				SimpleDateFormat lObjDateFormat2 = new SimpleDateFormat(
						"dd/MM/yyyy");

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
				String lStrTreasuryCode = (String) report.getParameterValue("treasuryCode");
				String lStrDDOCode = (String) report.getParameterValue("ddoCode");
                 
				 Long LtreasuryCode=   Long.valueOf(lStrTreasuryCode);
                 Long yearId= Long.valueOf(lStrYearId);
                 Long monthId= Long.valueOf(lStrMonthId);
                 
                  String lStrTreasuryName = getLocationName(LtreasuryCode);
				 String lStrDdoName = getDdoName(lStrDDOCode);
				 String lStrYear = lObjDcpsCommonDAO.getFinYearForYearId(yearId);
				String lStrMonthname=lObjDcpsCommonDAO.getMonthForId(monthId);

				
				
		//Added by ashish to display header			
				report.setAdditionalHeader("Treasury Name :"+lStrTreasuryName+"\r\nDDO Name :"+lStrDdoName+"\r\n Month :"+lStrMonthname
						+"\r\nFinancial Year :"+lStrYear);
		//ended by ashish to display header
				
				gLogger.info("DDO CODE is-----"+lStrDDOCode);
				gLogger.info("Trseury Code is-----"+lStrTreasuryCode);
				gLogger.info("lStrYearId is-----"+lStrYearId);
				
				Long lLongYearId = null;
				Long lLongMonthId = null;
				if (lStrYearId != null && !"".equalsIgnoreCase(lStrYearId)) {
					lLongYearId = Long.valueOf(lStrYearId);
				}
				if (lStrMonthId != null && !"".equalsIgnoreCase(lStrMonthId)) {
					lLongMonthId = Long.valueOf(lStrMonthId);
				}
				
				
				Long lLongTreasuryCode = Long.parseLong(lStrTreasuryCode);

				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append(" Select EM.DCPS_ID ,EM.EMP_NAME ,TR.CONTRIBUTION ,TR.TYPE_OF_PAYMENT ,VC.voucher_no ,VC.voucher_date ,TR.TREASURY_CODE , TR.DDO_CODE ,VC.emplr_voucher_no , VC.emplr_voucher_date emplrVoucherDate,TR.DCPS_EMP_ID ,TR.EMPLOYER_CONTRI_FLAG ");
				lSBQuery.append(" from mst_dcps_emp EM,trn_dcps_contribution TR,mst_dcps_contri_voucher_dtls VC ");
				lSBQuery.append(" where EM.DCPS_EMP_ID = TR.DCPS_EMP_ID ");
				lSBQuery.append(" AND tr.rlt_contri_voucher_id = VC.mst_dcps_contri_voucher_dtls");
				lSBQuery.append(" and TR.FIN_YEAR_ID = " + lLongYearId);
				lSBQuery.append(" and TR.MONTH_ID = " + lLongMonthId);
				lSBQuery.append(" and TR.REG_STATUS = 1 ");
				lSBQuery.append(" AND EM.REG_STATUS = 1");
				lSBQuery.append(" and TR.TREASURY_CODE = " + lLongTreasuryCode);
				lSBQuery.append(" and TR.DDO_CODE = '" +  lStrDDOCode.trim()+"'");
				lSBQuery.append(" AND EM.DCPS_ID IS NOT NULL");
				lSBQuery.append(" ORDER BY TR.TREASURY_CODE,TR.DDO_CODE ASC");

				StrSqlQuery = lSBQuery.toString();
				rs = smt.executeQuery(StrSqlQuery);

				String lStrDcpsId = null;
				String lStrEmpName = null;
				String lStrEmployeeContribution = null;
				Double lDoubleContributionEmployee = 0d;
				Double lDoubleContributionEmployer = 0d;
				Double lDoubleContributionRcvdforPrvsMonths = 0d;
				Double lDoubleTotalContribution = 0d;
				Double lDoubleGrandTotal = 0d;
				String lStrVoucherNo = null;
				String lStrVoucherDate = null;
				String lStrEmployerContriFlag = null;

				Integer lIntCounter = 1;

				while (rs.next()) {
					
					if(!rs.getString("TypeOfPayment").equals("700046"))
					{
						continue ;
					}

					rowList = new ArrayList();

					rowList.add(new StyledData(lIntCounter, CenterAlignVO));
					lIntCounter = lIntCounter + 1;

					lStrDcpsId = rs.getString("DcpsId");
					if (!(lStrDcpsId == null)) {
						if (!lStrDcpsId.equals("")) {
							rowList
									.add(new StyledData(lStrDcpsId, LeftAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					lStrEmpName = rs.getString("EmpName");
					if (!(lStrEmpName == null)) {
						if (!lStrEmpName.equals("")) {
							rowList
									.add(new StyledData(lStrEmpName,
											LeftAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					lStrEmployeeContribution = rs.getString("Contribution");
					if (lStrEmployeeContribution != null
							&& !lStrEmployeeContribution.equals("")) {
						lDoubleContributionEmployee = Double
								.valueOf(lStrEmployeeContribution);
					}

					if (!(lStrEmployeeContribution == null)) {
						if (!lStrEmployeeContribution.equals("")) {
							rowList
									.add(new StyledData(
											lDoubleContributionEmployee,
											CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					
					lDoubleContributionRcvdforPrvsMonths = objOfflineContriDAO.getDelayedContribution(Long.parseLong(rs.getString("dcpsEmpId")), lLongYearId,
							lLongMonthId);

					rowList
							.add(new StyledData(
									lDoubleContributionRcvdforPrvsMonths,
									CenterAlignVO));

					lDoubleTotalContribution = lDoubleContributionEmployee
							+ lDoubleContributionRcvdforPrvsMonths;

					rowList.add(new StyledData(lDoubleTotalContribution,
							CenterAlignVO));

					lStrVoucherNo = rs.getString("VoucherNo");
					lStrVoucherDate = rs.getString("VoucherDate");
					if (!(lStrVoucherNo == null || lStrVoucherDate == null)) {
						if (!lStrVoucherNo.equals("")
								&& !lStrVoucherDate.equals("")) {
							rowList.add(new StyledData(lStrVoucherNo
									+ "-"
									+ lObjDateFormat2.format(lObjDateFormat1
											.parse(lStrVoucherDate)),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					//lDoubleContributionEmployer = lDoubleContributionEmployee;
					
					lStrEmployerContriFlag = rs.getString("EmployerContriFlag");
					if(lStrEmployerContriFlag != null)
					{
						if(!"".equals(lStrEmployerContriFlag))
						{
							if(lStrEmployerContriFlag.equals("Y"))
							{
								lDoubleContributionEmployer = lDoubleContributionEmployee ;
							}
						}
					}
					
					lDoubleContributionEmployer = lDoubleContributionEmployer + objOfflineContriDAO.getDelayedContributionMatched(Long.parseLong(rs.getString("dcpsEmpId")), lLongYearId,lLongMonthId);

					if(lDoubleContributionEmployer != 0d)
					{
						rowList.add(new StyledData(lDoubleContributionEmployer,
								CenterAlignVO));
					}
					else
					{
						rowList.add(new StyledData(" - ",
								CenterAlignVO));
					}


					if (rs.getString("emplrVoucherNo") !=null &&  rs.getString("emplrVoucherDate") !=null){
						rowList.add(new StyledData(rs.getString("emplrVoucherNo")
								+ "-"
								+ lObjDateFormat2.format(lObjDateFormat1.parse(rs.getString("emplrVoucherDate"))), CenterAlignVO));
					} else {
						rowList.add(new StyledData("-  -", CenterAlignVO));
					}

					lDoubleGrandTotal = lDoubleTotalContribution
							+ lDoubleContributionEmployer;
					rowList
							.add(new StyledData(lDoubleGrandTotal,
									CenterAlignVO));
					rowList.add(rs.getString("ddoCode"));
					rowList.add(rs.getString("treasuryCode"));
					dataList.add(rowList);

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

			String lStrBufLang = "SELECT finYearId, finYearDesc FROM SgvcFinYearMst WHERE langId = :langId and finYearCode BETWEEN '2007' AND '2014' ORDER BY finYearCode";

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

	public List getTreasuryCode(String lStrLangId, String lStrLocId) {

		List<Object> lArrTreasuries = new ArrayList<Object>();

		try {
			Session lObjSession = ServiceLocator.getServiceLocator()
					.getSessionFactory().getCurrentSession();

			String lStrBufLang = "SELECT locId, locName FROM CmnLocationMst WHERE departmentId in ( 100003,100006)   and LOC_ID<>1111     AND LOC_ID <>9991 order by locId ";

			Query lObjQuery = lObjSession.createQuery(lStrBufLang);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[0].toString()+"-"+lArrData[1].toString());
					lArrTreasuries.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}

		return lArrTreasuries;

}
//added by ashish to display ddo code with ddo name in drop down
	
	public List getDdoForTreasury(String treasuryCode, String lStrLangId,String lStrLocId) {

		List<Object> lArrDdos = new ArrayList<Object>();
		//Long lLongTreasuryCode = Long.valueOf(treasuryCode);

		try {
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			StringBuilder lStrBufLang = new StringBuilder();

			lStrBufLang.append(" SELECT DM.ddo_code , DM.ddo_name  FROM Rlt_Ddo_Org RO, Org_Ddo_Mst DM,Cmn_Location_Mst LM ");
			lStrBufLang.append(" WHERE RO.location_Code = '"+ treasuryCode+ "' ");
			lStrBufLang.append("  AND RO.ddo_Code = DM.ddo_Code AND LM.location_Code = RO.location_Code AND LM.LANG_ID = 1 order by DM.ddo_code ");

			Query lObjQuery = lObjSession.createSQLQuery(lStrBufLang.toString());
		//	lObjQuery.setParameter("lLongTreasuryCode", lLongTreasuryCode);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (Object[]) lLstResult.get(lIntCtr);
					String ddoCode=lArrData[0].toString();
					String ddoName=lArrData[1].toString();
					gLogger.info("ddoCode  is *****"+ddoCode);
					gLogger.info("ddoName  is *****"+ddoName);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc("<![CDATA["+"("+ddoCode+") "+ddoName+"]]>");
					//lObjComboValuesVO.setDesc("<![CDATA["+"(lArrData[0].toString())lArrData[1].toString()]]>");
					lArrDdos.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}

		return lArrDdos;
	}

	//ended by ashish to display ddo code with ddo name in drop down
	
	
		/*ArrayList<ComboValuesVO> arrDDO = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String ddo_code = null;
		String ddo_name = null;
		try {
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					"SELECT DM.ddo_code as ddo_code, DM.ddo_name as ddo_name FROM Rlt_Ddo_Org RO, Org_Ddo_Mst DM,Cmn_Location_Mst LM "
					+ "WHERE RO.location_Code = '"+ treasuryCode+ "' AND RO.ddo_Code = DM.ddo_Code AND LM.location_Code = RO.location_Code AND LM.LANG_ID = 1 order by DM.ddo_name");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			if(lRs!=null)
			{	
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				ddo_code = lRs.getString("ddo_code");
				ddo_name = lRs.getString("ddo_name");
				vo.setId(ddo_code);
				//vo.setDesc(ddo_name);
				vo.setDesc("<![CDATA["+"("+ddo_code+") "+ddo_name+"]]>");
				arrDDO.add(vo);
			}
			}
			else
			{
				ComboValuesVO vo = new ComboValuesVO();
				vo.setId("-1");
				vo.setDesc("--Select--");
				arrDDO.add(vo);
			}
		} catch (Exception e) {			
			gLogger.info("Sql Exception:" + e, e);

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
		return arrDDO;
	}*/

		
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

//added by ashish to add header with DDo name only 
	
	public String getDdoName(String lStrDdoCode)
	{
		List lLstData = null;
		String lStrDdoName = "";

		try{
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DDO_NAME FROM ORG_DDO_MST WHERE DDO_CODE = :ddoCode order by DDO_NAME");
			Query lQuery = lObjSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLstData = lQuery.list();

			if(lLstData.size() > 0){
				lStrDdoName = lLstData.get(0).toString();
			}
		}catch(Exception e){
			gLogger.error("Exception in getDdoName:" + e, e);
		}
		return lStrDdoName;
	}	//ended by ashish to add header with DDo name only 

}
