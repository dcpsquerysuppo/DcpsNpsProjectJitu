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
 * @author Ashish Sharma
 * @version 0.1
 * @since JDK 7.0
 * Nov 11, 2014
 */

package com.tcs.sgv.dcps.report;

import java.sql.CallableStatement;
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
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
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
 * @since JDK 5.0 Nov 11, 2014
 */
public class EmployeeCountReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger
	.getLogger(EmployeeCountReport.class);
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

			if (report.getReportCode().equals("8000160")) {

				// report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				Long treasuryCode = Long.valueOf((String) report.getParameterValue("treasuryCode"));
				
				String lStrTreasuryCode=null;
				String lStryTreasuryName=null;
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
				String url = "";

			
				StringBuilder sb = new StringBuilder();

                sb.append(" SELECT loc.LOC_ID,loc.LOC_NAME,emp.DDO_CODE, ");
				sb.append(" count(case when look.LOOKUP_ID =700240 then 1 else null end ) as Total_IAS_Employee,  ");
				sb.append(" count(case when look.LOOKUP_ID =700241 then 1 else null end ) as Total_IPS_Employee,  "); 
				sb.append(" count(case when look.LOOKUP_ID =700242 then 1 else null end ) as Total_IFS_Employee, ");
				sb.append(" count(case when grd.GRADE_ID=100001 then 1 else null end ) as Total_A_grade,  ");
				sb.append(" count(case when grd.GRADE_ID=100064 then 1 else null end ) as Total_B_grade,  ");
				sb.append("	count(case when grd.GRADE_ID=100065 then 1 else null end ) as Total_BnGz_grade,  "); 
				sb.append(" count(case when grd.GRADE_ID=100066 then 1 else null end ) as Total_C_grade,  ");
				sb.append(" count(case when grd.GRADE_ID=100067 then 1 else null end ) as Total_D_grade ");
				sb.append(" FROM mst_dcps_emp emp inner join ORG_USER_MST user on user.USER_NAME=emp.SEVARTH_ID ");
				sb.append(" inner join ORG_EMP_MST mst on mst.USER_ID=user.USER_ID ");
				sb.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID=emp.AC_DCPS_MAINTAINED_BY "); 
				sb.append(" inner join ORG_GRADE_MST grd on grd.GRADE_ID=mst.GRADE_ID ");
				sb.append(" inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(emp.ddo_code,1,4) and loc.DEPARTMENT_ID in (100003,100006) ");
				sb.append(" where emp.REG_STATUS=1 and emp.FORM_STATUS=1 and emp.SUPER_ANN_DATE > sysdate and  loc.LOC_ID = "+treasuryCode+" or loc.PARENT_LOC_ID = "+treasuryCode+" ");
				sb.append(" and length(dcps_id)=20 and emp.LOC_ID <> 380001  ");
				sb.append(" group by  loc.LOC_ID,loc.LOC_NAME,emp.DDO_CODE ");
				sb.append(" order by loc.LOC_ID,emp.DDO_CODE ");
				
				
				
				StrSqlQuery = sb.toString();
				rs = smt.executeQuery(StrSqlQuery);
				
				
				
				gLogger.info("StrSqlQuery***********"+StrSqlQuery.toString());
				Integer counter = 1;
			
				while (rs.next()) {
					rowList = new ArrayList();

					//Sr No.
					rowList.add(new StyledData(counter, CenterAlignVO));
					
					if (!(rs.getString(1).equals("") || rs.getString(1) == null)) {
						lStrTreasuryCode = rs.getString(1).toString().trim();
					}
					//	Treasury
					
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
					
				
					//DDO Code
					if (!(rs.getString(3) == null)) {
						if (!rs.getString(3).equals("")) {
							rowList.add(new StyledData(rs.getString(3),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					gLogger.info("ddo code is***********"+rs.getString(3));
					
					//IAS
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
					
					gLogger.info("IAS count is***********"+rs.getString(4));
					//IPS
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
					gLogger.info("IPS count is***********"+rs.getString(5));
					
					
					
					//IFS
					if (!(rs.getString(6) == null)) {
						if (!rs.getString(6).equals("")) {
							rowList.add(new StyledData(rs.getString(6),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					
					gLogger.info("IFS count is***********"+rs.getString(6));
					
					Long IAS=null;
					Long IPS=null;
					Long IFS=null;
					
					if(rs.getString(4) != null || !rs.getString(4).equals("")){
                     IAS = Long.parseLong(rs.getString(4).toString().trim());
					}
					if(rs.getString(5) != null || !rs.getString(5).equals("")){
					 IPS = Long.parseLong(rs.getString(5).toString().trim());
					}
					if(rs.getString(6) != null || !rs.getString(6).equals("")){
					 IFS = Long.parseLong(rs.getString(6).toString().trim());
					}
					
					Long totalCount= IAS+IPS+IFS;
					//Total(AIS)
					if (!(totalCount == null)) {
						if (!totalCount.equals("")) {
							rowList.add(new StyledData(totalCount,
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					gLogger.info("Total AIS count is***********"+totalCount);
					
					
					//Grp A
					if (!(rs.getString(7) == null)) {
						if (!rs.getString(7).equals("")) {
							rowList.add(new StyledData(rs.getString(7),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					
					
					gLogger.info("group A count is***********"+rs.getString(7));
					//Grp B
					if (!(rs.getString(8) == null)) {
						if (!rs.getString(8).equals("")) {
							rowList.add(new StyledData(rs.getString(8),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					
					gLogger.info("group B count is***********"+rs.getString(8));
					
					
					//Grp B n Z
					if (!(rs.getString(9) == null)) {
						if (!rs.getString(9).equals("")) {
							rowList.add(new StyledData(rs.getString(9),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					
					gLogger.info("group BnGz count is***********"+rs.getString(9));
					
					//Grp C
					if (!(rs.getString(10) == null)) {
						if (!rs.getString(10).equals("")) {
							rowList.add(new StyledData(rs.getString(10),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					gLogger.info("group C count is***********"+rs.getString(10));
					
					//Grp D
					if (!(rs.getString(11) == null)) {
						if (!rs.getString(11).equals("")) {
							rowList.add(new StyledData(rs.getString(11),
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}

					gLogger.info("group D count is***********"+rs.getString(11));
					Long totalAGrade=null;
					Long totalBGrade=null;
					Long totalBnGzGrade=null;
					Long totalCGrade=null;
					Long totalDGrade=null;
					
					if(rs.getString(7) != null || !rs.getString(7).equals("")){
						totalAGrade = Long.parseLong(rs.getString(7).toString().trim());
					}
					if(rs.getString(8) != null || !rs.getString(8).equals("")){
						totalBGrade = Long.parseLong(rs.getString(8).toString().trim());
					}
					
					if(rs.getString(9) != null || !rs.getString(9).equals("")){
						totalBnGzGrade = Long.parseLong(rs.getString(9).toString().trim());
					}
					if(rs.getString(10) != null || !rs.getString(10).equals("")){
						totalCGrade = Long.parseLong(rs.getString(10).toString().trim());
					}
					
					if(rs.getString(11) != null || !rs.getString(11).equals("")){
						totalDGrade = Long.parseLong(rs.getString(11).toString().trim());
						}
					
					//Total (srka)
					Long totalCountGrp= totalAGrade+totalBGrade+totalBnGzGrade+totalCGrade+totalDGrade;
					gLogger.info("  totalCountGrp count is***********"+totalCountGrp);
					
					if (!(totalCountGrp == null)) {
						if (!totalCountGrp.equals("")) {
							rowList.add(new StyledData(totalCountGrp,
									CenterAlignVO));
						} else {
							rowList.add(new StyledData("", CenterAlignVO));
						}
					} else {
						rowList.add(new StyledData("", CenterAlignVO));
					}
					
					
					
					
					
					gLogger.info("Treasury code is***********"+rs.getString(1));
					gLogger.info("Treasury name is***********"+lStryTreasuryName);
					
					
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

		

	
	
	public List getAllTreasuries( String lStrLangId, String lStrLocId) {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {			
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100003)  and CM.LANG_ID = 1 and CM.LOC_ID not in(1111)  order by CM.loc_id  ");

			lCon = DBConnection.getConnection();
			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id+"-"+treasury_name);
				arrTreasury.add(vo);
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
		return arrTreasury;

	}
	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
}
