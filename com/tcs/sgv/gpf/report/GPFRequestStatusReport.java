/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Nov 04, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.report;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAO;
import com.tcs.sgv.gpf.dao.GPFRequestProcessDAOImpl;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Nov 04, 2011
 */
public class GPFRequestStatusReport extends DefaultReportDataFinder implements ReportDataFinder {

	private static final Logger gLogger = Logger.getLogger("GPFReports");
	public static String newline = System.getProperty("line.separator");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Map lMapSeriesHeadCode = null;
	Map requestAttributes = null;
	Session ghibSession = null;

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException {
		Connection con = null;
		Statement smt = null;
		ResultSet rs = null;
		List lLstDataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		new ReportsDAOImpl();
		String lStrDdoCode = "";
		Long lLngDdoPostId = null;
		try {
			requestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			serviceLocator = (ServiceLocator) requestAttributes.get("serviceLocator");
			lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
			serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");
			gObjSessionFactory = serviceLocator.getSessionFactorySlave();
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			Map lServiceMap = (Map) requestAttributes.get("serviceMap");
			Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
			gLngPostId = (Long) lBaseLoginMap.get("loggedInPost");
			ghibSession = gObjSessionFactory.getCurrentSession();
			GPFRequestProcessDAO lObjGPFReqProcess = new GPFRequestProcessDAOImpl(null, serviceLocator
					.getSessionFactory());
			
			String StrSqlQuery = "";

			StyleVO[] noBorder = new StyleVO[2];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			noBorder[1] = new StyleVO();
			noBorder[1].setStyleId(IReportConstants.REPORT_PAGINATION);
			noBorder[1].setStyleValue("NO");

			StyleVO[] RightAlignVO = new StyleVO[2];
			RightAlignVO[0] = new StyleVO();
			RightAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			RightAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);
			RightAlignVO[1] = new StyleVO();
			RightAlignVO[1].setStyleId(IReportConstants.BORDER);
			RightAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] boldVO = new StyleVO[2];
			boldVO[0] = new StyleVO();
			boldVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			boldVO[1] = new StyleVO();
			boldVO[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			boldVO[1].setStyleValue("10");

			if (lObjReport.getReportCode().equals("800008")) {

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String lStrFromDate = lObjReport.getParameterValue("fromDate").toString();
				String lStrToDate = lObjReport.getParameterValue("toDate").toString();
				String lStrUser = lObjReport.getParameterValue("UserType").toString();
				Date lDtFromDate = new Date(sdf.parse(lStrFromDate).getTime());
				Date lDtToDate = new Date(sdf.parse(lStrToDate).getTime());
				lDtToDate.setDate(lDtToDate.getDate() + 1);
				StringBuilder SBQuery = new StringBuilder();

				/*if (lStrUser.equals("DEO")) {
					lStrDdoCode = lObjGPFReqProcess.getDdoCode(gLngPostId);
				} else {
					lLngDdoPostId = lObjGPFReqProcess.getDDOPostIdForVerifierHo(gLngPostId, lStrUser);
					lStrDdoCode = lObjGPFReqProcess.getDdoCodeForDDO(lLngDdoPostId);
				}*/
				
				lStrDdoCode = getDdoCodeForPostId(gLngPostId);

				if (lStrUser.equals("DEO")) {
					SBQuery
							.append("select * from (select ME.emp_name,CS.transaction_id,'CS',MEG.sevaarth_id,CS.application_date,CS.created_date,CS.deo_action_date,CS.status_flag"
									+ " from mst_gpf_emp_subscription CS, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME"
									+ " where CS.gpf_Acc_No = MEG.gpf_Acc_No AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_or_gpf='N' and CS.created_date >='"
									+ lDtFromDate
									+ "' and CS.created_date <='"
									+ lDtToDate
									+ "' AND ME.ddo_code ="
									+ lStrDdoCode
									+ " AND ME.emp_group = 'D' AND CS.status_flag <> 'X' "
									+ " UNION "
									+ "select ME.emp_name,MGA.transaction_id,MGA.advance_type,MEG.sevaarth_id,MGA.application_date,MGA.created_date,MGA.deo_action_date,MGA.status_flag"
									+ " from Mst_Gpf_Advance MGA, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME"
									+ " where MGA.gpf_Acc_No = MEG.gpf_Acc_No AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_or_gpf='N' and MGA.created_date >='"
									+ lDtFromDate
									+ "' and MGA.created_date <='"
									+ lDtToDate
									+ "' AND ME.ddo_code ="
									+ lStrDdoCode
									+ " AND ME.emp_group = 'D' AND MGA.status_flag <> 'X' "
									+ " UNION "
									+ "select ME.emp_name,TGF.transaction_id,'FW',MEG.sevaarth_id,TGF.application_date,TGF.created_date,TGF.deo_action_date,TGF.req_status"
									+ " from trn_gpf_final_withdrawal TGF, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME"
									+ " where TGF.gpf_Acc_No = MEG.gpf_Acc_No AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_or_gpf='N' and TGF.created_date >= '"
									+ lDtFromDate
									+ "' and TGF.created_date <='"
									+ lDtToDate
									+ "' AND ME.ddo_code ="
									+ lStrDdoCode + " AND ME.emp_group = 'D' AND TGF.req_status <> 'X') order by 2");
				} else if (lStrUser.equals("DEOAPP")) {
					SBQuery
							.append("select * from (select ME.emp_name,CS.transaction_id,'CS',MEG.sevaarth_id,CS.application_date,CS.deo_action_date,coalesce(CS.ho_receive_date,CS.verifier_action_date),CS.status_flag"
									+ " from mst_gpf_emp_subscription CS, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME"
									+ " where CS.gpf_Acc_No = MEG.gpf_Acc_No AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_or_gpf='N' and CS.deo_action_date >= '"
									+ lDtFromDate
									+ "' and CS.deo_action_date <='"
									+ lDtToDate
									+ "' AND ME.ddo_code ="
									+ lStrDdoCode
									+ " AND ME.emp_group = 'D' AND CS.status_flag <> 'X' "
									+ " UNION "
									+ "select ME.emp_name,MGA.transaction_id,MGA.advance_type,MEG.sevaarth_id,MGA.application_date,MGA.deo_action_date,coalesce(MGA.ho_receive_date,MGA.verifier_action_date),MGA.status_flag"
									+ " from Mst_Gpf_Advance MGA, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME"
									+ " where MGA.gpf_Acc_No = MEG.gpf_Acc_No AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_or_gpf='N' and MGA.deo_action_date >= '"
									+ lDtFromDate
									+ "' and MGA.deo_action_date <='"
									+ lDtToDate
									+ "' AND ME.ddo_code ="
									+ lStrDdoCode
									+ " AND ME.emp_group = 'D' AND MGA.status_flag <> 'X' "
									+ " UNION "
									+ "select ME.emp_name,TGF.transaction_id,'FW',MEG.sevaarth_id,TGF.application_date,TGF.deo_action_date,coalesce(TGF.ho_receive_date,TGF.verifier_action_date),TGF.req_status"
									+ " from trn_gpf_final_withdrawal TGF, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME"
									+ " where TGF.gpf_Acc_No = MEG.gpf_Acc_No AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_or_gpf='N' and TGF.deo_action_date >= '"
									+ lDtFromDate
									+ "' and TGF.deo_action_date <='"
									+ lDtToDate
									+ "' AND ME.ddo_code ="
									+ lStrDdoCode + " AND ME.emp_group = 'D' AND TGF.req_status <> 'X' ) order by 2");
				} else if (lStrUser.equals("HO")) {
					SBQuery
							.append("select * from (select ME.emp_name,CS.transaction_id,'CS',MEG.sevaarth_id,CS.application_date,CS.ho_receive_date,CS.ho_action_date,CS.status_flag"
									+ " from mst_gpf_emp_subscription CS, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME"
									+ " where CS.gpf_Acc_No = MEG.gpf_Acc_No AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_or_gpf='N' and CS.ho_receive_date >= '"
									+ lDtFromDate
									+ "' and CS.ho_receive_date <='"
									+ lDtToDate
									+ "' AND ME.ddo_code ="
									+ lStrDdoCode
									+ " AND ME.emp_group = 'D' AND CS.status_flag <> 'X' "
									+ " UNION "
									+ "select ME.emp_name,MGA.transaction_id,MGA.advance_type,MEG.sevaarth_id,MGA.application_date,MGA.ho_receive_date,MGA.ho_action_date,MGA.status_flag"
									+ " from Mst_Gpf_Advance MGA, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME"
									+ " where MGA.gpf_Acc_No = MEG.gpf_Acc_No AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_or_gpf='N' and MGA.ho_receive_date >= '"
									+ lDtFromDate
									+ "' and MGA.ho_receive_date <='"
									+ lDtToDate
									+ "' AND ME.ddo_code ="
									+ lStrDdoCode
									+ " AND ME.emp_group = 'D' AND MGA.status_flag <> 'X' "
									+ " UNION "
									+ "select ME.emp_name,TGF.transaction_id,'FW',MEG.sevaarth_id,TGF.application_date,TGF.ho_receive_date,TGF.ho_action_date,TGF.req_status"
									+ " from trn_gpf_final_withdrawal TGF, Mst_Emp_Gpf_Acc MEG, Mst_dcps_Emp ME"
									+ " where TGF.gpf_Acc_No = MEG.gpf_Acc_No AND MEG.mst_Gpf_Emp_Id = ME.dcps_Emp_Id and ME.dcps_or_gpf='N' and TGF.ho_receive_date >= '"
									+ lDtFromDate
									+ "' and TGF.ho_receive_date <='"
									+ lDtToDate
									+ "' AND ME.ddo_code ="
									+ lStrDdoCode + " AND ME.emp_group = 'D' AND TGF.req_status <> 'X' ) order by 2");
				}
				StrSqlQuery = SBQuery.toString();
				con = gObjSessionFactory.getCurrentSession().connection();

				smt = con.createStatement();
				rs = smt.executeQuery(StrSqlQuery);
				Integer counter = 1;
				ArrayList rowList = new ArrayList();
				String requestType = "";
				String reqStatus = "";
				while (rs.next()) {
					rowList = new ArrayList();
					rowList.add(rs.getString(1));
					if (rs.getString(2) != null) {
						rowList.add(rs.getString(2));
					} else {
						rowList.add("");
					}
					if (rs.getString(3).equals("CS")) {
						requestType = "Change Subscription";
					} else if (rs.getString(3).equals("RA")) {
						requestType = "Refundable Advance";
					} else if (rs.getString(3).equals("NRA")) {
						requestType = "Non-Refundable Advance";
					} else if (rs.getString(3).equals("FW")) {
						requestType = "Final Withdrawal";
					}
					rowList.add(requestType);
					rowList.add(rs.getString(4));
					if (rs.getDate(5) != null) {
						rowList.add(sdf.format(rs.getDate(5).getTime()));
					} else {
						rowList.add("");
					}
					if (rs.getDate(6) != null) {
						rowList.add(sdf.format(rs.getDate(6).getTime()));
					} else {
						rowList.add("");
					}
					if (rs.getString(7) != null) {
						rowList.add(sdf.format(rs.getDate(7).getTime()));
					} else {
						rowList.add("");
					}
					if (rs.getString(8).equals("D") || rs.getString(8).equals("R")) {
						reqStatus = "Pending with DEO";
					} else if (rs.getString(8).equals("F1")) {
						reqStatus = "Pending with Verifier";
					} else if (rs.getString(8).equals("F2")) {
						reqStatus = "Pending with Head Officer";
					} else if (rs.getString(8).equals("A")) {
						reqStatus = "Approved";
					}

					rowList.add(reqStatus);
					lLstDataList.add(rowList);
					counter = counter + 1;
				}
			}

		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
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
				gLogger.error("Exception :" + e1, e1);

			}
		}
		return lLstDataList;
	}
	
	public String getDdoCodeForPostId(Long lLngPostId)
	{
		List lListData = null;
		String lStrDdoCode = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ODM.DDO_CODE FROM ORG_DDO_MST ODM, ORG_POST_MST OPM ");
			lSBQuery.append(" WHERE OPM.POST_ID = :postId AND OPM.LOCATION_CODE = ODM.LOCATION_CODE");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);
			lListData = lQuery.list();
			
			if(lListData != null && lListData.size() > 0){
				lStrDdoCode = (String) lListData.get(0);
			}
		}catch(Exception e){
			gLogger.error("Exception is  : ", e);
		}
		return lStrDdoCode;
	}

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}

}
