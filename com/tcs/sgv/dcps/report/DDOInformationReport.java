/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jan 9, 2015	Ashish Sharma							
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
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
public class DDOInformationReport extends DefaultReportDataFinder {

	private static final Logger gLogger = Logger
	.getLogger(DDOInformationReport.class);
	String Lang_Id = "en_US";
	String Loc_Id = "LC1";

	Map requestAttributes = null;

	SessionFactory lObjSessionFactory = null;
	Session ghibSession = null;
	ServiceLocator serviceLocator = null;

	public Collection findReportData(ReportVO report, Object criteria)
	throws ReportException {
		List DataList = new ArrayList();

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
			ghibSession = lObjSessionFactory.getCurrentSession();
			Long locationId = (Long) loginDetail.get("locationId");

			String StrSqlQuery = "";

			if (report.getReportCode().equals("9000157")) {

				// report.setStyleList(noBorder);

				ArrayList rowList = new ArrayList();

				String tresuryCode = report.getParameterValue("treasuryCode").toString();
				String lStrDdoCode = (String)report.getParameterValue("ddoCode");
				String Status = (String) report.getParameterValue("Status");
				gLogger.info("Status is ---------------"+Status);	
				gLogger.info("ddoCode is************"+lStrDdoCode);
				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(null,serviceLocator.getSessionFactory());
				String url = "";
			
				
				String ddoCode=null;
				String ddoName=null;
				String dtoReg=null;
				String address1=null;
				String address2=null;
				String address3=null;
				String city=null;
				String pinCode=null;
				String deptName=null;
				String phoneNo=null;
				String emailId=null;
				String designation=null;
				String parentDept=null;
				
			
				StringBuilder sb = new StringBuilder();

                sb.append(" SELECT ddo.DDO_CODE,ddo.DDO_NAME,dto.DTO_REG_NO,off.ADDRESS1_NEW,off.ADDRESS2_NEW,off.ADDRESS3_NEW,off.TOWN_NEW,off.OFFICE_PIN,loc2.loc_name||'',loc3.loc_name,off.TEL_NO_NEW,off.EMAIL_NEW,ddo.DSGN_NAME    ");
				sb.append(" FROM CMN_LOCATION_MST loc inner join MST_DTO_REG dto on dto.LOC_ID=loc.LOC_ID ");
				sb.append(" inner join CMN_LOCATION_MST loc1 on (loc1.PARENT_LOC_ID=loc.LOC_ID or loc1.LOC_ID=dto.LOC_ID) "); 
				sb.append(" inner join ORG_DDO_MST ddo on substr(ddo.DDO_CODE,1,4)=loc1.LOC_ID ");
				sb.append(" inner join MST_DCPS_DDO_OFFICE off  on ddo.DDO_CODE=off.DDO_CODE   ");
				sb.append(" inner join CMN_LOCATION_MST loc2 on loc2.LOC_ID=ddo.DEPT_LOC_CODE   ");
				sb.append(" inner join CMN_LOCATION_MST loc3 on loc3.LOC_ID=ddo.HOD_LOC_CODE   ");
				sb.append("	where (loc1.LOC_ID =:tresuryCode or loc1.parent_loc_id=:tresuryCode )and off.DDO_OFFICE='Yes'  "); 
                if( Long.parseLong(report.getParameterValue("ddoCode").toString())!= -1){
                sb.append(" and ddo.ddo_code=:lStrDdoCode ");
                }
                if(report.getParameterValue("Status").toString().equals("Updated"))
				{
                	
                	sb.append(" and off.ADDRESS1_NEW is not null ");
				}
                else if(report.getParameterValue("Status").toString().equals("Non Updated")){
                	sb.append(" and off.ADDRESS1_NEW is  null ");
                }
				sb.append("	group by ddo.DDO_CODE,ddo.DDO_NAME,dto.DTO_REG_NO,off.ADDRESS1_NEW,off.ADDRESS2_NEW,off.ADDRESS3_NEW,off.TOWN_NEW,off.OFFICE_PIN,loc2.loc_name||'',loc3.loc_name,off.TEL_NO_NEW,off.EMAIL_NEW,ddo.DSGN_NAME    ");
				sb.append(" order by ddo.ddo_code");
				
				
				
				StrSqlQuery = sb.toString();
			
				SQLQuery lQuery = ghibSession.createSQLQuery(StrSqlQuery.toString());
				
				lQuery.setString("tresuryCode",tresuryCode);
				 if( Long.parseLong(report.getParameterValue("ddoCode").toString())!= -1){
					 lQuery.setString("lStrDdoCode",lStrDdoCode);
		              }
			
				
				List outputlist = lQuery.list();
				gLogger.info("StrSqlQuery***********"+StrSqlQuery.toString());
				Integer counter = 1;
			
				List dataListForTable = new ArrayList();

				if (outputlist != null && !outputlist.isEmpty()) {
					int count = 0;

					for (Iterator it = outputlist.iterator(); it.hasNext();)

					{
						count++;
						dataListForTable = new ArrayList();
						Object[] lObj = (Object[]) it.next();


						ddoCode = (lObj[0] != null) ? lObj[0].toString(): "NA";
						gLogger.info("DDO Code is  ***********" + ddoCode);

						ddoName = (lObj[1] != null) ? lObj[1].toString() : "";
						gLogger.info("ddo Name  is***********"+ ddoName);

						dtoReg = (lObj[2] != null) ? lObj[2].toString() : "";						
						gLogger.info(" dto Reg  ***********"+ dtoReg);

						address1 = (lObj[3] != null) ? lObj[3].toString() : "";						
						gLogger.info(" address1 ***********"+ address1);



						address2 = (lObj[4] != null) ? lObj[4].toString() : "";						
						gLogger.info(" address2  ***********"+ address2);


						address3 = (lObj[5] != null) ? lObj[5].toString() : "";						
						gLogger.info("address3***********"+ address3);

						city = (lObj[6] != null) ? lObj[6].toString() : "";						
						gLogger.info(" city is ***********"+ city);


						pinCode = (lObj[7] != null) ? lObj[7].toString() : "";						
						gLogger.info(" pinCode is ***********"+ pinCode);

						deptName = (lObj[9] != null) ? lObj[9].toString() : "";						
						gLogger.info(" deptName  ***********"+ deptName);


						phoneNo = (lObj[10] != null) ? lObj[10].toString() : "";						
						gLogger.info(" phoneNo ***********"+ phoneNo);

						emailId = (lObj[11]!= null) ? lObj[11].toString() : "";						
						gLogger.info(" emailId  ***********"+ emailId);


						designation = (lObj[12] != null) ? lObj[12].toString() : "";						
						gLogger.info(" designation ***********"+ designation);

						parentDept = (lObj[8] != null) ? lObj[8].toString() : "";						
						gLogger.info(" parentDept ***********"+ parentDept);



						dataListForTable.add(count);
						dataListForTable.add(ddoCode);
						dataListForTable.add(ddoName);	
						dataListForTable.add(dtoReg);
						dataListForTable.add(address1);
						dataListForTable.add(address2);
						dataListForTable.add(address3);
						dataListForTable.add(city);
						dataListForTable.add(pinCode);
						dataListForTable.add(deptName);
						dataListForTable.add(phoneNo);
						dataListForTable.add(emailId);
						dataListForTable.add(designation);
						dataListForTable.add(parentDept);
						DataList.add(dataListForTable);

					}
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
		return DataList;
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
	public List getAllDDO(String treasuryId, String lStrLangId, String lStrLocId) {

		ArrayList<ComboValuesVO> arrDDO = new ArrayList<ComboValuesVO>();
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
					+ "WHERE RO.location_Code = '"
					+ treasuryId 
					+ "' AND RO.ddo_Code = DM.ddo_Code AND LM.location_Code = RO.location_Code AND LM.LANG_ID = 1 order by DM.ddo_code");

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
	}
	public List getStatus(String lStrLangId, String lStrLocId) {

		List<Object> lArrStatus = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

			/*String lStrBufLang = "SELECT distinct status,case when status='A' then 'Approved and voucher not match' when status='B' then 'Voucher Date or Amount mismatch' when status='E' then 'DDO mismatch' when status='F' then 'Match' else 'Post Employer Done' end from mst_dcps_contri_voucher_dtls where status is not null and status in ('A','B','E','F','G') ";

			Query lObjQuery = lObjSession.createSQLQuery(lStrBufLang);*/
		

			List<String> lLstResult =new ArrayList<String>();
			
			lLstResult.add("Updated");
			lLstResult.add("Non Updated");
			ComboValuesVO lObjComboValuesVO = null;
			Object[] lArrData = null;

			if (lLstResult != null && !lLstResult.isEmpty()) {
			/*	for (int lIntCtr = 0; lIntCtr < lLstResult.size(); lIntCtr++) {
					lObjComboValuesVO = new ComboValuesVO();
					lArrData = (String []) lLstResult.get(lIntCtr);
					lObjComboValuesVO.setId(lArrData[0].toString());
					lObjComboValuesVO.setDesc(lArrData[0].toString());
					lArrStatus.add(lObjComboValuesVO);
				}*/
				
				for (String lStrCat : lLstResult) {
					if (lStrCat != null) {
						lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(lStrCat);
						lObjComboValuesVO.setDesc(lStrCat);
						
							
						lArrStatus.add(lObjComboValuesVO);
					}
				}
				
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
		}

		return lArrStatus;
	}

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
}
