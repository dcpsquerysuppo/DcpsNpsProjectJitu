package com.tcs.sgv.dcps.report;
import java.math.BigInteger;
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
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.NsdlNpsDAOImpl;
import com.tcs.sgv.dcps.dao.OfflineContriDAO;
import com.tcs.sgv.dcps.dao.OfflineContriDAOImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;
import com.tcs.sgv.reports.dao.ReportParameterDAO;

public class FrmContributionstatusReportDDOImpl  extends DefaultReportDataFinder 

{
	private static final Logger gLogger = Logger.getLogger(FrmContributionstatusReportDDOImpl.class);
	 Long finYearId = Long.valueOf(7340579277617758208L);
	


	public List getYearForDCPSReports(String lStrLangId, String lStrLocId) {
		ArrayList<ComboValuesVO> arrYear = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		Logger logger = Logger.getLogger(ReportParameterDAO.class);
		String fin_year_id = null;
		String fin_name = null;
		try {

			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					"select * from sgvc_fin_year_mst where lang_id ='"
							+ lStrLangId
							+ "'  and fin_year_code between '2015' and '2020' order by fin_year_code ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				fin_year_id = lRs.getString("FIN_YEAR_CODE");
				fin_name = lRs.getString("FIN_YEAR_CODE");
				vo.setId(fin_year_id);
				vo.setDesc(fin_name);
				arrYear.add(vo);
			}

		} catch (Exception e) {
			logger.error("Exception is : " + e, e);
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
				e.printStackTrace();
				logger.info("Sql Exception:" + e, e);
			}
		}
		return arrYear;
	}
		
	public List getAllTreasuries( String lStrLangId, String lStrLocId) 
	{


		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;

		try {
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
			"select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100003) and CM.LANG_ID = 1 and CM.LOC_ID not in(9991,1111) order by CM.loc_Id ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");				
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id +"-"+treasury_name);
				arrTreasury.add(vo);
			}
			ComboValuesVO vo = new ComboValuesVO();
			vo.setId("CmnPool");
			vo.setDesc("Common Pool Employees");
			arrTreasury.add(vo);
		} catch (Exception e) {

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
				e.printStackTrace();
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;

	
	}
	//Get Sub treasury From Treasury
	public List getSubTreasury(String treasuryId,  String lStrLangId, String lStrLocId) 
	{
		
		
		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;
		String treasury_id = null;
		String treasury_name = null;
		
		try {
			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					"select loc_Id , loc_Name  from Cmn_Location_Mst where PARENT_LOC_ID='"+treasuryId+"' or  LOC_ID ='"+treasuryId+"'  and department_Id in (100003) and LANG_ID = 1 and LOC_ID not in(9991,1111) ");
			
			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				treasury_id = lRs.getString("loc_Id");				
				treasury_name = lRs.getString("loc_Name");
				vo.setId(treasury_id);
				vo.setDesc(treasury_id +"-"+treasury_name);
				arrTreasury.add(vo);
			}
			ComboValuesVO vo = new ComboValuesVO();
			vo.setId("CmnPool");
			vo.setDesc("Common Pool Employees");
			arrTreasury.add(vo);
		} catch (Exception e) {
			
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
				e.printStackTrace();
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrTreasury;
		
		
	}
	
	//For Month 
	public List getMonth(String lStrLangId, String lStrLocCode) {
		List<Object> lArrMonths = new ArrayList<Object>();
		try {
			Session lObjSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

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
	//For Deputition 
		public List getDepution(String lStrLangId, String lStrLocCode) {
			List iDeputionlst = new ArrayList();
			try {
				ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(1+"");
				lObjComboValuesVO.setDesc("No");
				iDeputionlst.add(lObjComboValuesVO);
				System.out.println("-->List Size is...-->"+iDeputionlst.size());

				ComboValuesVO lObjComboValuesVO1 = new ComboValuesVO();
				lObjComboValuesVO1.setId(2+"");
				lObjComboValuesVO1.setDesc("Yes");
				iDeputionlst.add(lObjComboValuesVO1);
				System.out.println("-->List Size is...-->"+iDeputionlst.size());
				
			} catch (Exception e) {
				gLogger.error("Error is : " + e, e);
			}

			return iDeputionlst;
		}
		//For View Report 
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
					ArrayList resultList = new ArrayList();
					ArrayList td;
					
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
			CenterAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			CenterAlignVO[1] = new StyleVO();
			CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
			CenterAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			StyleVO[] LeftAlignVO = new StyleVO[2];
			LeftAlignVO[0] = new StyleVO();
			LeftAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			LeftAlignVO[0].setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);
			LeftAlignVO[1] = new StyleVO();
			LeftAlignVO[1].setStyleId(IReportConstants.BORDER);
			LeftAlignVO[1].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);

			report.setStyleList(rowsFontsVO);
			report.initializeDynamicTreeModel();
			report.initializeTreeModel();

			StyleVO[] noBorder = new StyleVO[1];
			noBorder[0] = new StyleVO();
			noBorder[0].setStyleId(IReportConstants.BORDER);
			noBorder[0].setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
			
			if (report.getReportCode().equals("80000936")) {


				dataList=new ArrayList();
				ArrayList rowList = new ArrayList();

				report.setStyleList(rowsFontsVO);

				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

				SimpleDateFormat lObjDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat lObjDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat dateFormat5 = new SimpleDateFormat("dd/MM/yy :hh:mm:ss");
				Date date = new Date();
				dateFormat.format(date);

				ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

				OfflineContriDAO objOfflineContriDAO = new OfflineContriDAOImpl(OfflineContriDAO.class,serviceLocator.getSessionFactory());

				DcpsCommonDAO lObjDcpsCommonDAO = new DcpsCommonDAOImpl(SgvcFinYearMst.class,serviceLocator.getSessionFactory());
				String lStrYearId = (String) report.getParameterValue("yearId");
				String iStreMOnthId = (String) report.getParameterValue("monthId");
				String iStrtreasury = (String) report.getParameterValue("treasury");
				String iSIs_Depution = (String) report.getParameterValue("Is_Depution");
				Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				
				// For link
				String urlPrefix1 = "ifms.htm?actionFlag=reportService&reportCode=80000933&action=generateReport&DirectReport=TRUE";
				// For First List
				if(iSIs_Depution.equalsIgnoreCase("1")) //for non deputition

				{
				List TableData1 = null;
				List TableNsdlData = null;

				StringBuilder SBQuery1 = new StringBuilder();
				StringBuilder SBQuery2 = new StringBuilder();
				// Treasry Id-->lStrReeasuryId

					SBQuery1.append("   SELECT loc.LOC_NAME,loc.LOC_ID, sum(paybill.DED_ADJUST) as DED_ADJUST  FROM PAYBILL_HEAD_MPG head   inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID  ");
					SBQuery1.append("  inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID  inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID ");
					SBQuery1.append("  inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=  substr(ddo.ddo_code,1,2) inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4) ");
					SBQuery1.append("  where head.PAYBILL_YEAR='"+lStrYearId+"' and head.PAYBILL_MONTH='"+iStreMOnthId+"'  and substr(ddo.ddo_code,1,4) in ((select  loc_id from Cmn_Location_Mst  where PARENT_LOC_ID= '"+iStrtreasury+"')  union    (select  loc_id from Cmn_Location_Mst  where LOC_ID= '"+iStrtreasury+"' )) ");
					SBQuery1.append("  and head.APPROVE_FLAG=1 and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  and mstemp.DCPS_OR_GPF='Y' group by loc.LOC_ID,loc.LOC_NAME ");

				Query lQuery = ghibSession.createSQLQuery(SBQuery1.toString());
				TableData1 = lQuery.list();
                String  TresuryCode=iStrtreasury.substring(0,2);
				SBQuery2.append(" SELECT substr(bh.file_name,1,4) ,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as BIGINT) as sd_amnt  FROM NSDL_SD_DTLS sd inner join mst_ddo_reg reg on reg.DDO_REG_NO = sd.ddo_reg_no  inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME  "); 
				SBQuery2.append("  and bh.STATUS <>-1 and bh.YEAR='"+lStrYearId+"' and bh.MONTH='"+iStreMOnthId+"'    and bh.file_name like '"+TresuryCode+"%' and bh.file_name not like '"+TresuryCode+"%D'  group by substr(bh.file_name,1,4) ");
				Query lQuery1 = ghibSession.createSQLQuery(SBQuery2.toString());
				TableNsdlData = lQuery1.list();
				String Flag = "true";
				if (TableData1 != null && TableData1.size() > 0) {
					for (int i = 0; i < TableData1.size(); i++) { // For First
																	// PayBill
																	// List
						Flag = "true";
						Object[] tuple = (Object[]) TableData1.get(i);
						td = new ArrayList();
						td.add(i + 1);
						if (tuple[0] != null) {
							td.add(tuple[0]);
						} else {
							td.add("-");
						}
						if (tuple[1] != null) {
							td.add(tuple[1]);
						} else {
							td.add("-");
						}
						if (tuple[2] != null) {
							td.add(tuple[2]);
						} else {
							td.add("-");
						}
						if (tuple[2] != null) {
							td.add(tuple[2]);
						} else {
							td.add("-");
						}

						if (TableNsdlData != null && TableNsdlData.size() > 0) // For Second Nsdl List
						{
							int Count=0;
							for (int k = 0; k < TableNsdlData.size(); k++) {
								Object[] tupleNsdl = (Object[]) TableNsdlData.get(k);
								{
									if (tuple[1].toString().equalsIgnoreCase((tupleNsdl[0].toString()))) {
										if (tupleNsdl[1] != null) {
											StringTokenizer st2 = new StringTokenizer(tupleNsdl[1].toString()); 
											Long Rst2=Long.parseLong(st2.nextToken("."));
											if(Long.parseLong(tuple[2].toString())<Rst2) 
											{
											td.add(tuple[2]);
											td.add(tuple[2]);
											}
											else
											{
												td.add(Rst2);
												td.add(Rst2);	
											}
											
											StringTokenizer st1 = new StringTokenizer(tupleNsdl[1].toString()); 
											Long Pending=0L;
											if(Long.parseLong(tuple[2].toString())>Rst2) 
											{
											 Pending = Long.parseLong(tuple[2].toString())-Long.parseLong(st1.nextToken("."));
											}
											
											if (Pending == 0)
											{
												td.add(0);
											    td.add(0);
											    td.add(0); 
											}
											else
											{
											   td.add(Pending);
											   td.add(Pending);
											   Long Sum=Pending+Pending;
											  td.add(new URLData(Sum,urlPrefix1+ "&year="+ lStrYearId+ "&iStreMOnthId="+ iStreMOnthId+ "&subtreasuryCode="+ tuple[1]+ "&Is_Depution=No"));
											Flag = "false";
											 }
											} else {
											Flag = "false";
										}
										break;
									}
									else
									{
										Count++;
									}
									
								}
							}
							if(Count==TableNsdlData.size())
							{
								Flag="NA";
							}
						}

						if (Flag.equalsIgnoreCase("true")) {
							td.add(tuple[2]);
							td.add(tuple[2]);
							td.add(0);
							td.add(0);
							td.add(0);
						}
						else if(Flag.equalsIgnoreCase("NA"))
						{
							td.add(0);
							td.add(0);
							td.add(tuple[2]);
							td.add(tuple[2]);
							StringTokenizer st2 = new StringTokenizer(tuple[2].toString()); 
							Long Rst2=Long.parseLong(st2.nextToken("."));
							Long Total=Rst2+Rst2;
							td.add(new URLData(Total,urlPrefix1+ "&year="+ lStrYearId+ "&iStreMOnthId="+ iStreMOnthId+ "&subtreasuryCode="+ iStrtreasury+ "&ddoCode="+tuple[0]+"&isDeputition=No"));
						}
						dataList.add(td);
					}

				}
			}
			else
			{
				//for Deputition
				List TableData1 = null;

				StringBuilder SBQuery1 = new StringBuilder();
				// Treasry Id-->lStrReeasuryId
				  String finYearCode = "";
				  String monthId  = (String ) report.getParameterValue("monthId");
				  String yearId = (String) report.getParameterValue("yearId");
				  Long Lmonth=Long.parseLong(monthId);
				  
				  StringBuffer str = new StringBuffer();
				  
				     str.append("select FIN_YEAR_ID  from sgvc_fin_year_mst where FIN_YEAR_CODE='"+yearId+"' ");
				    Query query = ghibSession.createSQLQuery(str.toString());
				    List  lstYearID = query.list();  
					
				    Long LYearId = (long) Integer.parseInt(lstYearID.get(0).toString());
				  
				  if (Lmonth.longValue() < 4L)
		          {
		            finYearCode = String.valueOf(LYearId.longValue() - 1);
		          }
		          else
		          {
		            finYearCode = String.valueOf(LYearId);
		          }
				  
				  

					List TableData11 = null;
					List TableNsdlData = null;

					StringBuilder SBQuery11 = new StringBuilder();
					StringBuilder SBQuery2 = new StringBuilder();
					
					SBQuery11.append(" select  substr(trn.TREASURY_CODE,1,4),loc.LOC_NAME,cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as dcpscontribution from TRN_DCPS_CONTRIBUTION trn inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(trn.TREASURY_CODE,1,4) where   trn.IS_CHALLAN='Y'  and trn.STATUS='H' ");
					SBQuery11.append(" and trn.IS_DEPUTATION='Y' and  trn.FIN_YEAR_ID='"
									+ finYearCode
									+ "' and trn.MONTH_ID='"
									+ iStreMOnthId
									+ "' and substr(trn.TREASURY_CODE,1,4)  in ( (select loc_id from Cmn_Location_Mst where PARENT_LOC_ID= '"
									+ iStrtreasury + "') ");
					SBQuery11.append("   union(select loc_id from Cmn_Location_Mst where LOC_ID= '"
									+ iStrtreasury
									+ "')) group by substr(trn.TREASURY_CODE,1,4),loc.LOC_NAME ");
					
					Query lQuery1 = ghibSession.createSQLQuery(SBQuery11.toString());
					TableData11 = lQuery1.list();
					String  TresuryCode=iStrtreasury.substring(0,2);
 					SBQuery2.append(" select substr(bh.file_name,1,4) ,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as BIGINT) as sd_amnt FROM NSDL_SD_DTLS sd  inner join  MST_DCPS_EMP emp on sd.SD_PRAN_NO =emp.PRAN_NO ");
					SBQuery2.append("  inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME  and bh.STATUS <>-1  and bh.YEAR='"+lStrYearId+"' and bh.MONTH='"+iStreMOnthId+"' and   bh.file_name like '"+TresuryCode+"%D' group by substr(bh.file_name,1,4)");
 					
 					
 					Query lQuery = ghibSession.createSQLQuery(SBQuery2.toString());
		            TableNsdlData = lQuery.list();
					String Flag = "true";
					if (TableData11 != null && TableData11.size() > 0) {
						for (int i = 0; i < TableData11.size(); i++) { // For First// PayBill// List
							Flag = "true";
							Object[] tuple = (Object[]) TableData11.get(i);
							td = new ArrayList();
							td.add(i + 1);
							if (tuple[0] != null) {
								td.add(tuple[0]);
							} else {
								td.add("-");
							}
							if (tuple[1] != null) {
								td.add(tuple[1]);
							} else {
								td.add("-");
							}
							if (tuple[2] != null) {
								td.add(tuple[2]);
							} else {
								td.add("-");
							}
							if (tuple[2] != null) {
								td.add(tuple[2]);
							} else {
								td.add("-");
							}

							if (TableNsdlData != null && TableNsdlData.size() > 0) // For Second Nsdl List
							{
								int Count=0;
								for (int k = 0; k < TableNsdlData.size(); k++) {
									Object[] tupleNsdl = (Object[]) TableNsdlData.get(k);
									{
										if (tuple[0].toString().equalsIgnoreCase((tupleNsdl[0].toString()))) {
											if (tupleNsdl[1] != null) {
												StringTokenizer st2 = new StringTokenizer(tupleNsdl[1].toString()); 
												Long Rst2=Long.parseLong(st2.nextToken("."));
												td.add(Rst2);
												td.add(Rst2);
												StringTokenizer st1 = new StringTokenizer(tupleNsdl[1].toString()); 
												StringTokenizer st1tuple2 = new StringTokenizer(tuple[2].toString()); 

												Long Pending = Long.parseLong(st1tuple2.nextToken("."))-Long.parseLong(st1.nextToken("."));
												if (Pending == 0)
												{
													td.add(0);
												    td.add(0);
												    td.add(0); 
												}
												    else
												    {
												    	td.add(Pending);
												    	td.add(Pending);
												    	Long Sum=Pending+Pending;
												    	td.add(new URLData(Sum,urlPrefix1+ "&year="+ lStrYearId+ "&iStreMOnthId="+ iStreMOnthId+ "&subtreasuryCode="+ iStrtreasury+ "&ddoCode="+tuple[0]+"&isDeputition=No"));
												    	Flag = "false";
												    }
												} else {
												Flag = "false";
											}
											break;
										}
										{
											Count++;
										}
										
									}
								}
								if(Count==TableNsdlData.size())
								{
									Flag="NA";
								}
							}

							if (Flag.equalsIgnoreCase("true")) {
								td.add(tuple[2]);
								td.add(tuple[2]);
								td.add(0);
								td.add(0);
								td.add(0);
							}
							else if(Flag.equalsIgnoreCase("NA"))
							{
								td.add(0);
								td.add(0);
								td.add(tuple[2]);
								td.add(tuple[2]);
								StringTokenizer st2 = new StringTokenizer(tuple[2].toString()); 
								Long Rst2=Long.parseLong(st2.nextToken("."));
								Long Total=Rst2+Rst2;
								td.add(new URLData(Total,urlPrefix1+ "&year="+ lStrYearId+ "&iStreMOnthId="+ iStreMOnthId+ "&subtreasuryCode="+ iStrtreasury+ "&ddoCode="+tuple[0]+"&isDeputition=Yes"));
							}
							dataList.add(td);
						}

					}
			}
			
			}
			else if (report.getReportCode().equals("80000933")) {

				dataList=new ArrayList();
				ArrayList rowList80000933 = new ArrayList();

				report.setStyleList(rowsFontsVO);

				DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yy");

				SimpleDateFormat lObjDateFormat11 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat lObjDateFormat21 = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat dateFormat51 = new SimpleDateFormat("dd/MM/yy :hh:mm:ss");
				Date date1 = new Date();
				dateFormat1.format(date1);

				ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

				OfflineContriDAO objOfflineContriDAO1 = new OfflineContriDAOImpl(OfflineContriDAO.class,serviceLocator.getSessionFactory());

				DcpsCommonDAO lObjDcpsCommonDAO1 = new DcpsCommonDAOImpl(SgvcFinYearMst.class,serviceLocator.getSessionFactory());
				String lStrYearId1 = (String) report.getParameterValue("year");
				String iStreMOnthId1 = (String) report.getParameterValue("iStreMOnthId");
				String lStrReeasuryId1 = (String) report.getParameterValue("subtreasuryCode");
				String iSIs_Depution1 = (String) report.getParameterValue("Is_Depution");
				Session ghibSession1 = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				
//				td.add(new URLData(Sum,urlPrefix1+ "&year="+ lStrYearId+ "&iStreMOnthId="+ iStreMOnthId+ "&subtreasuryCode="+ tuple[1]+ "&isDeputition=No"));
				
			
				
				// For link
				String urlPrefix11 = "ifms.htm?actionFlag=reportService&reportCode=80000935&action=generateReport&DirectReport=TRUE";
				// For First List
				if(iSIs_Depution1.equalsIgnoreCase("No")) //for non deputition

				{
				List TableData111 = null;
				List TableNsdlData1 = null;

				StringBuilder SBQuery111 = new StringBuilder();
				StringBuilder SBQuery21 = new StringBuilder();
				// Treasry Id-->lStrReeasuryId

				SBQuery111.append(" SELECT ddo.DDO_CODE,ddo.DDO_NAME, sum(paybill.DED_ADJUST) as DED_ADJUST ");
				SBQuery111.append(" FROM PAYBILL_HEAD_MPG head   inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA =paybill.DED_ADJUST inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID ");
				SBQuery111.append(" inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID   inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)= ");
				SBQuery111.append(" substr(ddo.ddo_code,1,2) inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4)  where head.PAYBILL_YEAR='"
						+ lStrYearId1
						+ "' and head.PAYBILL_MONTH='"
						+ iStreMOnthId1
						+ "'  and substr(ddo.ddo_code,1,4)='"
						+ lStrReeasuryId1
						+ "' and head.APPROVE_FLAG=1 and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1 ");
				SBQuery111.append(" and mstemp.DCPS_OR_GPF='Y' group by ddo.DDO_CODE ");
				Query lQuery11 = ghibSession1.createSQLQuery(SBQuery111.toString());
				TableData111 = lQuery11.list();

				SBQuery21.append("SELECT reg.DDO_CODE as ddo ,cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amnt  FROM NSDL_SD_DTLS sd inner join mst_ddo_reg reg on reg.DDO_REG_NO = sd.ddo_reg_no  inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 and bh.YEAR='"
						+ lStrYearId1 + "' and bh.MONTH='" + iStreMOnthId1 + "' ");
				SBQuery21.append(" and bh.file_name like '" + lStrReeasuryId1+ "%'  and bh.file_name not like '" + lStrReeasuryId1+ "%D' group by  reg.DDO_CODE ");
				Query lQuery111 = ghibSession1.createSQLQuery(SBQuery21.toString());
				TableNsdlData1 = lQuery111.list();
				String Flag1 = "true";
				if (TableData111 != null && TableData111.size() > 0) {
					for (int i = 0; i < TableData111.size(); i++) { // For First
																	// PayBill
																	// List
						Flag1 = "true";
						Object[] tuple = (Object[]) TableData111.get(i);
						td = new ArrayList();
						td.add(i + 1);
						if (tuple[0] != null) {
							td.add(tuple[0]);
						} else {
							td.add("-");
						}
						if (tuple[1] != null) {
							td.add(tuple[1]);
						} else {
							td.add("-");
						}
						if (tuple[2] != null) {
							td.add(tuple[2]);
						} else {
							td.add("-");
						}
						if (tuple[2] != null) {
							td.add(tuple[2]);
						} else {
							td.add("-");
						}

						if (TableNsdlData1 != null && TableNsdlData1.size() > 0) // For Second Nsdl List
						{
							int Count=0;
							for (int k = 0; k < TableNsdlData1.size(); k++) {
								Object[] tupleNsdl = (Object[]) TableNsdlData1.get(k);
								{
									if (tuple[0].toString().equalsIgnoreCase((tupleNsdl[0].toString()))) {
										if (tupleNsdl[1] != null) {
											StringTokenizer st2 = new StringTokenizer(tupleNsdl[1].toString()); 
											Long Rst2=Long.parseLong(st2.nextToken("."));
											td.add(Rst2);
											td.add(Rst2);
											StringTokenizer st1 = new StringTokenizer(tupleNsdl[1].toString()); 

											Long Pending = Long.parseLong(tuple[2].toString())-Long.parseLong(st1.nextToken("."));
											if (Pending == 0)
											{
												td.add(0);
											    td.add(0);
											    td.add(0); 
											}
											    else
											    {
											    	td.add(Pending);
											    	td.add(Pending);
											    	Long Sum=Pending+Pending;
											    	td.add(new URLData(Sum,urlPrefix11+ "&year="+ lStrYearId1+ "&iStreMOnthId="+ iStreMOnthId1+ "&subtreasuryCode="+ lStrReeasuryId1+ "&ddoCode="+tuple[0]+"&isDeputition=No"));
											Flag1 = "false";
											    }
											} else {
											Flag1 = "false";
										}
										break;
									}
									else
									{
										Count++;
									}
									
								}
							}
							if(Count==TableNsdlData1.size())
							{
								Flag1="NA";
							}
						}

						if (Flag1.equalsIgnoreCase("true")) {
							td.add(tuple[2]);
							td.add(tuple[2]);
							td.add(0);
							td.add(0);
							td.add(0);
						}
						else if(Flag1.equalsIgnoreCase("NA"))
						{
							td.add(0);
							td.add(0);
							td.add(tuple[2]);
							td.add(tuple[2]);
							StringTokenizer st2 = new StringTokenizer(tuple[2].toString()); 
							Long Rst2=Long.parseLong(st2.nextToken("."));
							Long Total=Rst2+Rst2;
					    	td.add(new URLData(Total,urlPrefix11+ "&year="+ lStrYearId1+ "&iStreMOnthId="+ iStreMOnthId1+ "&subtreasuryCode="+ lStrReeasuryId1+ "&ddoCode="+tuple[0]+"&isDeputition=No"));
						}
						dataList.add(td);
					}

				}
			}
			else
			{
				//for Deputition
				List TableData111 = null;

				StringBuilder SBQuery111 = new StringBuilder();
				  String finYearCode1 = "";
//				  String yearId = (String) report.getParameterValue("yearId");
				  Long Lmonth1=Long.parseLong(iStreMOnthId1);
				  StringBuffer str1 = new StringBuffer();
				     str1.append("select FIN_YEAR_ID  from sgvc_fin_year_mst where FIN_YEAR_CODE='"+lStrYearId1+"' ");
				    Query query1 = ghibSession1.createSQLQuery(str1.toString());
				    List  lstYearID1 = query1.list();  
					
				    Long LYearId1 = (long) Integer.parseInt(lstYearID1.get(0).toString());
				  
				  if (Lmonth1.longValue() < 4L)
		          {
		            finYearCode1 = String.valueOf(LYearId1.longValue() - 1);
		          }
		          else
		          {
		            finYearCode1 = String.valueOf(LYearId1);
		          }
				  
				  

					List TableData1111 = null;
					List TableNsdlData1 = null;

					StringBuilder SBQuery1111 = new StringBuilder();
					StringBuilder SBQuery21 = new StringBuilder();
					// Treasry Id-->lStrReeasuryId
					SBQuery1111.append(" SELECT   ddo.DDO_CODE as dcpsddo,ddo.DDO_NAME, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as  dcpscontribution FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID ");
					SBQuery1111.append(" inner join ORG_DDO_MST ddo on ddo.DDO_CODE=mstemp.DDO_CODE inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code where trn.FIN_YEAR_ID='"+finYearCode1+"' and trn.MONTH_ID='"+iStreMOnthId1+"' and substr(trn.TREASURY_CODE,1,4)='"+lStrReeasuryId1+"' and trn.IS_CHALLAN='Y'  and trn.STATUS='H' ");
					SBQuery1111.append(" and trn.IS_DEPUTATION='Y'  and mstemp.DDO_CODE is not null and PRAN_ACTIVE=1 and mstemp.PRAN_NO is not null and mstemp.DCPS_OR_GPF='Y' group  by  ddo.DDO_CODE,ddo.DDO_NAME ");
					Query lQuery11 = ghibSession1.createSQLQuery(SBQuery1111.toString());
					TableData1111 = lQuery11.list();
					
					SBQuery21.append("   SELECT emp.DDO_CODE  as nsdlddo, cast(sum(nvl(sd.SD_EMP_AMOUNT,0)) as double) as sd_amntnsdl FROM NSDL_SD_DTLS sd  inner join  MST_DCPS_EMP emp on emp.PRAN_NO=sd.SD_PRAN_NO   inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME  and bh.STATUS <>-1  and bh.YEAR='"+lStrYearId1+"' and bh.MONTH='"+iStreMOnthId1+"' and ");
 					SBQuery21.append("  bh.file_name like '"+lStrReeasuryId1+"%D' group by emp.DDO_CODE ");
   					Query lQuery111 = ghibSession1.createSQLQuery(SBQuery21.toString());
		            TableNsdlData1 = lQuery111.list();
					String Flag1 = "true";
					if (TableData1111 != null && TableData1111.size() > 0) {
						for (int i = 0; i < TableData1111.size(); i++) { // For First// PayBill// List
							Flag1 = "true";
							Object[] tuple = (Object[]) TableData1111.get(i);
							td = new ArrayList();
							td.add(i + 1);
							if (tuple[0] != null) {
								td.add(tuple[0]);
							} else {
								td.add("-");
							}
							if (tuple[1] != null) {
								td.add(tuple[1]);
							} else {
								td.add("-");
							}
							if (tuple[2] != null) {
								td.add(tuple[2]);
							} else {
								td.add("-");
							}
							if (tuple[2] != null) {
								td.add(tuple[2]);
							} else {
								td.add("-");
							}

							if (TableNsdlData1 != null && TableNsdlData1.size() > 0) // For Second Nsdl List
							{
								int Count=0;
								for (int k = 0; k < TableNsdlData1.size(); k++) {
									Object[] tupleNsdl = (Object[]) TableNsdlData1.get(k);
									{
										if (tuple[0].toString().equalsIgnoreCase((tupleNsdl[0].toString()))) {
											if (tupleNsdl[1] != null) {
												StringTokenizer st2 = new StringTokenizer(tupleNsdl[1].toString()); 
												Long Rst2=Long.parseLong(st2.nextToken("."));
												td.add(Rst2);
												td.add(Rst2);
												StringTokenizer st1 = new StringTokenizer(tupleNsdl[1].toString()); 
												StringTokenizer st1tuple2 = new StringTokenizer(tuple[2].toString()); 

												Long Pending = Long.parseLong(st1tuple2.nextToken("."))-Long.parseLong(st1.nextToken("."));
												if (Pending == 0)
												{
													td.add(0);
												    td.add(0);
												    td.add(0); 
												}
												    else
												    {
												    	td.add(Pending);
												    	td.add(Pending);
												    	Long Sum=Pending+Pending;
												    	td.add(Sum);
//												    	td.add(new URLData(Sum,urlPrefix11+ "&year="+ lStrYearId1+ "&iStreMOnthId="+ iStreMOnthId1+ "&subtreasuryCode="+ lStrReeasuryId1+ "&ddoCode="+tuple[0]+"&isDeputition=No"));
												Flag1 = "false";
												    }
												} else {
												Flag1 = "false";
											}
										}
										{
											Count++;
										}
										
									}
								}
								if(Count==TableNsdlData1.size())
								{
									Flag1="NA";
								}
							}

							if (Flag1.equalsIgnoreCase("true")) {
								td.add(tuple[2]);
								td.add(tuple[2]);
								td.add(0);
								td.add(0);
								td.add(0);
							}
							else if(Flag1.equalsIgnoreCase("NA"))
							{
								td.add(0);
								td.add(0);
								td.add(tuple[2]);
								td.add(tuple[2]);
								StringTokenizer st2 = new StringTokenizer(tuple[2].toString()); 
								Long Rst2=Long.parseLong(st2.nextToken("."));
								Long Total=Rst2+Rst2;
								td.add(Total);
//						    	td.add(new URLData(Total,urlPrefix11+ "&year="+ lStrYearId1+ "&iStreMOnthId="+ iStreMOnthId1+ "&subtreasuryCode="+ lStrReeasuryId1+ "&ddoCode="+tuple[0]+"&isDeputition=No"));
							}
							dataList.add(td);
						}

					}
			
			}
			} else if (report.getReportCode().equals("80000935")) {
				
				// For View Report Link
				
				
				String header1 = "<center><B><font size=\"3px\"> DDO CODE: " + space(1) +  (String) report.getParameterValue("ddoCode")+"</font></b></center>";
				report.setAdditionalHeader(header1);
				
				dataList=new ArrayList();

//				report.setStyleList(rowsFontsVO);

				DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yy");

				SimpleDateFormat lObjDateFormat11 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat lObjDateFormat21 = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat dateFormat51 = new SimpleDateFormat("dd/MM/yy :hh:mm:ss");
				Date date1 = new Date();
				dateFormat1.format(date1);

				ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();

				OfflineContriDAO objOfflineContriDAO1 = new OfflineContriDAOImpl(OfflineContriDAO.class,serviceLocator.getSessionFactory());

				DcpsCommonDAO lObjDcpsCommonDAO1 = new DcpsCommonDAOImpl(SgvcFinYearMst.class,serviceLocator.getSessionFactory());
				String lStrYearId1 = (String) report.getParameterValue("year"); 
				String iStreMOnthId1 = (String) report.getParameterValue("iStreMOnthId");
				String lSSubtrReeasuryId = (String) report.getParameterValue("subtreasuryCode");
				String lsDDOCode = (String) report.getParameterValue("ddoCode");
				String isDeputition = (String) report.getParameterValue("isDeputition");
				Session ghibSession1 = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				
				
				if(isDeputition.equalsIgnoreCase("Yes"))
				{
					//For Deputition Employee
					  StringBuffer str1 = new StringBuffer();
					  String finYearCode1 = "";
					  Long Lmonth1=Long.parseLong(iStreMOnthId1);
					  
					     str1.append("select FIN_YEAR_ID  from sgvc_fin_year_mst where FIN_YEAR_CODE='"+lStrYearId1+"' ");
					    Query query1 = ghibSession1.createSQLQuery(str1.toString());
					    List  lstYearID1 = query1.list();  
						
					    Long LYearId1 = (long) Integer.parseInt(lstYearID1.get(0).toString());
					  
					  if (Lmonth1.longValue() < 4L)
			          {
			            finYearCode1 = String.valueOf(LYearId1.longValue() - 1);
			          }
			          else
			          {
			            finYearCode1 = String.valueOf(LYearId1);
			          }
					
					
					

					List TableData111 = null;

					StringBuilder SBQuery111 = new StringBuilder();
					StringBuilder SBQuery21 = new StringBuilder();
					// Treasry Id-->lStrReeasuryId

					SBQuery111.append(" SELECT   mstemp.EMP_NAME,mstemp.PRAN_NO as PRAN_NO, cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as d , cast(sum(nvl(trn.CONTRIBUTION,0) ) as double) as sd_amnt  FROM MST_DCPS_EMP mstemp inner join TRN_DCPS_CONTRIBUTION trn on trn.DCPS_EMP_ID=mstemp.DCPS_EMP_ID  ");
					SBQuery111.append(" inner join ORG_DDO_MST ddo on ddo.DDO_CODE=mstemp.DDO_CODE inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code where trn.FIN_YEAR_ID='"+finYearCode1+"' and trn.MONTH_ID='"+iStreMOnthId1+"'   and substr(trn.TREASURY_CODE,1,4)='"+lSSubtrReeasuryId+"' and trn.IS_CHALLAN='Y'  and trn.STATUS='H'  ");
					SBQuery111.append(" and trn.IS_DEPUTATION='Y'  and mstemp.DDO_CODE is not null and PRAN_ACTIVE=1 and mstemp.PRAN_NO is not null and mstemp.DCPS_OR_GPF='Y'  and reg.DDO_CODE='"+lsDDOCode+"' and  mstemp.PRAN_NO not in ( ");
					SBQuery111.append(" SELECT sd.SD_PRAN_NO as PRAN_NO FROM NSDL_SD_DTLS sd  inner join mst_ddo_reg reg on reg.DDO_REG_NO = sd.ddo_reg_no   ");
					SBQuery111.append(" inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME  and bh.STATUS <>-1  and bh.YEAR='"+lStrYearId1+"' and bh.MONTH='"+iStreMOnthId1+"'   and bh.file_name like '%"+lSSubtrReeasuryId+"%D' and  reg.DDO_CODE='"+lsDDOCode+"' group by reg.DDO_CODE,reg.DDO_OFFICE_NAME,sd.SD_PRAN_NO  ");
					SBQuery111.append("  )   group  by  mstemp.PRAN_NO ,mstemp.EMP_NAME ");
 			 	    Query lQuery11 = ghibSession1.createSQLQuery(SBQuery111.toString());
 						TableData111 = lQuery11.list();

					
					
					int SrNo=0;
					if (TableData111 != null && TableData111.size() > 0) {
						for (int i = 0; i < TableData111.size(); i++) { // For First PayBill List
							td = new ArrayList();
							Object[] tuple = (Object[]) TableData111.get(i);
							
								td.add(SrNo + 1);
								if (tuple[0] != null) {
									td.add(tuple[0]);
								} else {
									td.add("-");
								}
								if (tuple[1] != null) {
									td.add(tuple[1]);
								} else {
									td.add("-");
								}
								if (tuple[2] != null) {
									td.add(tuple[2]);
								} else {
									td.add("-");
								}
								if (tuple[3] != null) {
									td.add(tuple[3]);
								} else {
									td.add("-");
								}
								if (tuple[3] != null) {
									td.add(tuple[3]);
								} else {
									td.add("-");
								}
								if (tuple[3] != null) {
									td.add(tuple[3]);
								} else {
									td.add("-");
								}
								dataList.add(td);
								SrNo++;
						}
					}
				
					
					
					
					//End Of Depution Employee
				}
				else
				{

				List TableData111 = null;
				List TableNsdlData1 = null;

				StringBuilder SBQuery111 = new StringBuilder();
				StringBuilder SBQuery21 = new StringBuilder();
				// Treasry Id-->lStrReeasuryId

				SBQuery111.append(" SELECT mstemp.EMP_NAME,mstemp.PRAN_NO,paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA as employee_contribution,(paybill.DED_ADJUST) as employer_contribution  FROM PAYBILL_HEAD_MPG head   inner join NSDL_PAYBILL_DATA paybill on paybill.PAYBILL_GRP_ID =head.PAYBILL_ID and paybill.DCPS+paybill.DCPS_PAY+paybill.DCPS_DELAY+paybill.DCPS_DA = ");
				SBQuery111.append("paybill.DED_ADJUST inner join ORG_DDO_MST ddo on ddo.LOCATION_CODE=paybill.LOC_ID and  ddo.DDO_CODE='"+lsDDOCode+"'  inner join HR_EIS_EMP_MST hreis on hreis.EMP_ID=paybill.EMP_ID  inner join MST_DCPS_EMP mstemp on mstemp.ORG_EMP_MST_ID=hreis.EMP_MPG_ID  ");
				SBQuery111.append("inner join mst_ddo_reg reg on reg.ddo_code = ddo.ddo_code  inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=  substr(ddo.ddo_code,1,2) inner join CMN_LOCATION_MST loc on loc.LOC_ID=substr(ddo.DDO_CODE,1,4) ");
				SBQuery111.append(" where head.PAYBILL_YEAR='"+lStrYearId1+"' and head.PAYBILL_MONTH='"+iStreMOnthId1+"'  and substr(ddo.ddo_code,1,4)='"+lSSubtrReeasuryId+"' and head.APPROVE_FLAG=1 and mstemp.PRAN_NO is not null and PRAN_ACTIVE=1  and mstemp.DCPS_OR_GPF='Y'  ");

				Query lQuery11 = ghibSession1.createSQLQuery(SBQuery111.toString());
				TableData111 = lQuery11.list();

				SBQuery21.append(" SELECT  sd.SD_PRAN_NO as ddo ,sd.SD_EMP_AMOUNT as employee,sd.SD_EMPLR_AMOUNT  as employer   FROM NSDL_SD_DTLS sd inner join mst_ddo_reg reg on reg.DDO_REG_NO = sd.ddo_reg_no  and reg.DDO_CODE='"+lsDDOCode+"'  inner join NSDL_BH_DTLS bh on bh.FILE_NAME=sd.FILE_NAME and bh.STATUS <>-1 and ");
						SBQuery21.append(" bh.YEAR='"+lStrYearId1+"' and bh.MONTH='"+iStreMOnthId1+"'  and bh.file_name like '"+lSSubtrReeasuryId+"%' and  bh.file_name not like '"+lSSubtrReeasuryId+"%D'  ");

				Query lQuery111 = ghibSession1.createSQLQuery(SBQuery21.toString());
				TableNsdlData1 = lQuery111.list();
				
				int SrNo=0;
				if (TableData111 != null && TableData111.size() > 0) {
					for (int i = 0; i < TableData111.size(); i++) { // For First PayBill List
						td = new ArrayList();
						String Flag1 = "true";
						Object[] tuple = (Object[]) TableData111.get(i);
						if (TableNsdlData1 != null && TableNsdlData1.size() > 0) // For Second  Nsdl List
						{
							for (int k = 0; k < TableNsdlData1.size(); k++) {
								Object[] tupleNsdl = (Object[]) TableNsdlData1.get(k);
								{
									StringTokenizer st = new StringTokenizer(tupleNsdl[1].toString());  
									StringTokenizer st1 = new StringTokenizer(tupleNsdl[1].toString());  
									System.out.println("Pran No -->"+tuple[1].toString());
									System.out.println("Pran No NSDL SIDE -->"+tupleNsdl[0].toString());
									System.out.println("Pran No Contribution-->"+tuple[2].toString());
									System.out.println("Pran No NSDL SIDE  Contribution -->"+st1.nextToken("."));
								
									if (tuple[1].toString().equalsIgnoreCase((tupleNsdl[0].toString())) &&tuple[2].toString().equalsIgnoreCase(st.nextToken("."))) {
										Flag1 = "false";
									}
									else
									{
									}
								}
							}
							if(Flag1.equalsIgnoreCase("true"))
							{
								
							td.add(SrNo + 1);
							if (tuple[0] != null) {
								td.add(tuple[0]);
							} else {
								td.add("-");
							}
							if (tuple[1] != null) {
								td.add(tuple[1]);
							} else {
								td.add("-");
							}
							if (tuple[2] != null) {
								td.add(tuple[2]);
							} else {
								td.add("-");
							}
							if (tuple[3] != null) {
								td.add(tuple[3]);
							} else {
								td.add("-");
							}
							if (tuple[2] != null && tuple[3] != null ) {
								Long Sum=(Long.parseLong(tuple[2].toString())+(Long.parseLong(tuple[3].toString())));
//								td.add( Double.parseDouble(tuple[2].toString() )+Double.parseDouble(tuple[3].toString() ));
								td.add(Sum);
							} else {
								td.add("-");
							}
							dataList.add(td);
							SrNo++;
						}
						}
						else
						{
							td.add(SrNo + 1);
							if (tuple[0] != null) {
								td.add(tuple[0]);
							} else {
								td.add("-");
							}
							if (tuple[1] != null) {
								td.add(tuple[1]);
							} else {
								td.add("-");
							}
							if (tuple[2] != null) {
								td.add(tuple[2]);
							} else {
								td.add("-");
							}
							if (tuple[3] != null) {
								td.add(tuple[3]);
							} else {
								td.add("-");
							}
							if (tuple[2] != null && tuple[3] != null ) {
								Long Sum=(Long.parseLong(tuple[2].toString())+(Long.parseLong(tuple[3].toString())));
//								td.add( Double.parseDouble(tuple[2].toString() )+Double.parseDouble(tuple[3].toString() ));
								td.add(Sum);
							} else {
								td.add("-");
							}
							dataList.add(td);
							SrNo++;
						}
						
					}
				}
			}
			}

		
		
		}catch (Exception e) {
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
			