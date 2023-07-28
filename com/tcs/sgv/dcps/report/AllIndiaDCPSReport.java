package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportTemplate;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.dcps.dao.AllIndiaServicesDAOImpl;
import com.tcs.sgv.dcps.service.AllIndiaServicesEmpServiceImpl;
import com.tcs.sgv.dcps.service.DcpsCommonDAO;
import com.tcs.sgv.dcps.service.DcpsCommonDAOImpl;

import org.apache.log4j.Logger;

public class AllIndiaDCPSReport extends DefaultReportDataFinder implements ReportDataFinder 
{
	private static Logger logger = Logger.getLogger(AllIndiaServicesEmpServiceImpl.class);
	
	private StyleVO[] selfCloseVO = null;
	private ResultSet lRs1 = null;
	Session ghibSession = null;
	
	public Collection findReportData(ReportVO report, Object criteria)throws ReportException
	{
		Connection lCon = null;
		Statement lStmt = null;

		Map requestKeys = (Map) ((Map) criteria)
		.get(IReportConstants.REQUEST_ATTRIBUTES);
		Map serviceMap = (Map) requestKeys.get("serviceMap");
		Map baseLoginMap = (Map) serviceMap.get("baseLoginMap");
		CmnLocationMst locationVO = (CmnLocationMst) baseLoginMap
		.get("locationVO");
		String locationName = locationVO.getLocName();
		long locationId = locationVO.getLocId();
		String locationNameincaps = locationName.toUpperCase();
		ServiceLocator serv1 = (ServiceLocator) requestKeys
		.get("serviceLocator");

		List DataList = new ArrayList();

		StyleVO[] baseFont = new StyleVO[1];
		baseFont[0] = new StyleVO();
		baseFont[0].setStyleId(IReportConstants.STYLE_FONT_SIZE);
		baseFont[0].setStyleValue("13");

		ReportTemplate rt = new ReportTemplate();
		rt.put(IReportConstants.TMPLT_COLUMN_HEADER, baseFont);
		rt.put(IReportConstants.TMPLT_BASE_FONT, baseFont);
		report.setReportTemplate(rt);

		ReportAttributeVO ravo1 = new ReportAttributeVO();
		ravo1.setAttributeType(IReportConstants.ADDL_HEADER_LOCATION);
		ravo1.setAttributeType(IReportConstants.ADDL_HEADER_ON_EACH_PAGE);
		ravo1.setAlignment(IReportConstants.HEADER_ALIGN_CENTER);
		report.addReportAttributeItem(ravo1);
		report.setAdditionalHeader("");
		StyleVO[] styleVOPgBrk = null;
		styleVOPgBrk = new StyleVO[2];

		styleVOPgBrk[0] = new StyleVO();
		styleVOPgBrk[0]
		             .setStyleId(IReportConstants.PAGE_BREAK_BRFORE_SUBREPORT);
		styleVOPgBrk[0].setStyleValue("yes");
		styleVOPgBrk[0] = new StyleVO();

		styleVOPgBrk[1] = new StyleVO();
		styleVOPgBrk[1].setStyleId(IReportConstants.SHOW_REPORT_WHEN_NO_DATA);
		styleVOPgBrk[1].setStyleValue("yes");

		report.addReportStyles(styleVOPgBrk);
		logger.info("EmployeeLoanHistroyDAO111");

		StyleVO[] CenterAlignVO = new StyleVO[2];
		CenterAlignVO[0] = new StyleVO();
		CenterAlignVO[0].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
		CenterAlignVO[0]
		              .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
		CenterAlignVO[1] = new StyleVO();
		CenterAlignVO[1].setStyleId(IReportConstants.BORDER);
		CenterAlignVO[1]
		              .setStyleValue(IReportConstants.VALUE_STYLE_BORDER_NONE);
		
		try {

			Map sessionKeys = (Map) ((Map) criteria)
			.get(IReportConstants.SESSION_KEYS);
			Map requestAttributes = (Map) ((Map) criteria)
			.get(IReportConstants.REQUEST_ATTRIBUTES);
			ServiceLocator serviceLocator = (ServiceLocator) requestAttributes
			.get("serviceLocator");
			SessionFactory sessionFactory = serviceLocator.getSessionFactory();
			Session session = sessionFactory.getCurrentSession();
			Session hibSession = sessionFactory.getCurrentSession();
			ServiceLocator serv = (ServiceLocator) requestKeys
			.get("serviceLocator");

			StyleVO[] boldStyleVO = new StyleVO[1];
			boldStyleVO[0] = new StyleVO();
			boldStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldStyleVO[0]
			            .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
			StyledData dataStyle = null;

			StyleVO[] colorStyleVO = new StyleVO[1];
			colorStyleVO[0] = new StyleVO();
			colorStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_COLOR);
			colorStyleVO[0].setStyleValue("blue");
			selfCloseVO = new StyleVO[1];
			selfCloseVO[0] = new StyleVO();
			selfCloseVO[0].setStyleId(IReportConstants.REPORT_PAGE_OK_BTN_URL);
			selfCloseVO[0].setStyleValue("javascript:self.close()");

			lCon = (Connection) DBConnection.getConnection();
			lStmt = (Statement) lCon.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			StyleVO[] leftHeader = new StyleVO[3];
			leftHeader[0] = new StyleVO();
			leftHeader[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			leftHeader[0]
			           .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			leftHeader[1] = new StyleVO();
			leftHeader[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			leftHeader[1].setStyleValue("10");
			leftHeader[2] = new StyleVO();
			leftHeader[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			leftHeader[2]
			           .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_LEFT);

			StyleVO[] rightHead = new StyleVO[3];
			rightHead[0] = new StyleVO();
			rightHead[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			rightHead[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLD);
			rightHead[1] = new StyleVO();
			rightHead[1].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			rightHead[1].setStyleValue("10");
			rightHead[2] = new StyleVO();
			rightHead[2].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			rightHead[2]
			          .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_RIGHT);

			StyleVO[] headerStyleVo = new StyleVO[4];
			headerStyleVo[0] = new StyleVO();
			headerStyleVo[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			headerStyleVo[0]
			              .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
			headerStyleVo[1] = new StyleVO();
			headerStyleVo[1].setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			headerStyleVo[1]
			              .setStyleValue(IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			headerStyleVo[2] = new StyleVO();
			headerStyleVo[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			headerStyleVo[2].setStyleValue("14");
			headerStyleVo[3] = new StyleVO();
			headerStyleVo[3].setStyleId(IReportConstants.STYLE_FONT_FAMILY);
			headerStyleVo[3]
			              .setStyleValue(IReportConstants.VALUE_FONT_FAMILY_ARIAL);

			StyleVO[] centerboldStyleVO1 = new StyleVO[3];
			centerboldStyleVO1[0] = new StyleVO();
			centerboldStyleVO1[0]
			                   .setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			centerboldStyleVO1[0]
			                   .setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
			centerboldStyleVO1[1] = new StyleVO();
			centerboldStyleVO1[1]
			                   .setStyleId(IReportConstants.STYLE_FONT_ALIGNMENT);
			centerboldStyleVO1[1].setStyleValue("Center");
			centerboldStyleVO1[2] = new StyleVO();
			centerboldStyleVO1[2].setStyleId(IReportConstants.STYLE_FONT_SIZE);
			centerboldStyleVO1[2].setStyleValue("10");
								
			
			if (report.getReportCode().equals("700104"))
			{
				
				String DCPS_ID = null;
				String Empname= null;
				String Sevaarth_id = null;
				String dateoj = null;
				String dateofjoining = null;
				String pranNo= null;
				String accMaintainedBy = null;
				 logger.info("in if state  ***********");
				
				 Date date = new Date();
					//Date curDate = dateFormat.format(date);
				 logger.info("Current date of system ***********************************" + date);
								
						
				 String AccmaintainedLook_id = (String) report.getParameterValue("allindiaid");
				 
				 logger.info("Drpo Down Values  ***********" + AccmaintainedLook_id);
				   
				StringBuilder lSBQuery = new StringBuilder();
								
				lSBQuery.append(" SELECT mst.DCPS_EMP_ID,mst.EMP_NAME,mst.SEVARTH_ID,day(mst.doj)||'/'||month(mst.doj)||'/'||year(mst.doj),mst.dcps_id,mst.pran_no,look.LOOKUP_NAME  FROM MST_DCPS_EMP mst inner join RLT_DCPS_PAYROLL_EMP rlt ");
				lSBQuery.append("  on mst.DCPS_EMP_ID = rlt.DCPS_EMP_ID ");
				lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID = rlt.GIS_APPLICABLE ");
				lSBQuery.append(" where rlt.GIS_APPLICABLE = :id ");//,700215,700216)");				
				lSBQuery.append(" and mst.DOJ >= '2004-01-01' and mst.REG_STATUS in (1,2) and mst.FORM_STATUS = 1 and mst.DDO_CODE <> '1111222222'  ");
				lSBQuery.append(" order by mst.EMP_NAME,rlt.GIS_APPLICABLE ");
				
				Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
				SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
				
				Query.setString("id", AccmaintainedLook_id);
				 logger.info("All india DCPS Script  ***********" + lSBQuery.toString());
				List lLstFinal = Query.list();
				
				List dataListForTable = new ArrayList();
				
				if (lLstFinal != null && !lLstFinal.isEmpty())
				{
					
					for (Iterator it = lLstFinal.iterator();it.hasNext();)
		
					{
						dataListForTable = new ArrayList();
					 Object[] lObj = (Object[])it.next();
					 
					 Empname = (lObj[1]!=null)?lObj[1].toString():"NA";
					 logger.info("Employee name is  ***********" + 	Empname);
					 
					 Sevaarth_id = (lObj[2]!=null)?lObj[2].toString():"NA";
					 logger.info(" sevaarth is  ***********" + 	Sevaarth_id);
					 
					 dateoj	 = (lObj[3]!=null)?lObj[3].toString():"NA";	
					 logger.info("Date of joing is  ***********" + 	dateoj);
					 
					 DCPS_ID = (lObj[4]!=null)?lObj[4].toString():"NA";
					 		 logger.info("DCPS id is  ***********" + 	DCPS_ID);
					 		 						 
					
					 
					 pranNo = (lObj[5]!=null)?lObj[5].toString():"NA";
					 logger.info("Pran Number is  ***********" + 	pranNo);
					 
					 accMaintainedBy = (lObj[6]!=null)?lObj[6].toString():"NA";
					 logger.info("account maintained By  ***********" + 	accMaintainedBy);
					 
					 dataListForTable.add( Sevaarth_id);
					 dataListForTable.add( Empname);
					 dataListForTable.add( dateoj);
					 dataListForTable.add( DCPS_ID);					 				
					 dataListForTable.add( pranNo);
					 dataListForTable.add( accMaintainedBy);					 
					 DataList.add(dataListForTable);
							 
					}
								   }
				 }
			
			if(report.getReportCode().equals("700105"))
			{
				
				String DCPS_ID = null;
				String Empname= null;
				String Sevaarth_id = null;
				String dateoj = null;
				String pranNo= null;
				String accMaintainedBy = null;
				String date = null;
				 logger.info("in if state  ***********");
				
			
				  //  AllIndiaServicesDAOImpl lObjAlIndSer = new AllIndiaServicesDAOImpl(null,serv.getSessionFactory()); 
							
					 String AccmaintainedLook_id = (String) report.getParameterValue("allindiaid");
					   
					StringBuilder lSBQuery = new StringBuilder();
									
					lSBQuery.append(" SELECT mst.DCPS_EMP_ID,mst.EMP_NAME,mst.SEVARTH_ID,day(mst.doj)||'/'||month(mst.doj)||'/'||year(mst.doj),mst.dcps_id,mst.pran_no,look.LOOKUP_NAME,day(mst.UPDATED_DATE)||'/'||month(mst.UPDATED_DATE)||'/'||year(mst.UPDATED_DATE) FROM MST_DCPS_EMP mst inner join RLT_DCPS_PAYROLL_EMP rlt ");
					lSBQuery.append("  on mst.DCPS_EMP_ID = rlt.DCPS_EMP_ID ");
					lSBQuery.append(" inner join CMN_LOOKUP_MST look on look.LOOKUP_ID = rlt.GIS_APPLICABLE ");
					lSBQuery.append(" where rlt.GIS_APPLICABLE in (:id) ");//,700215,700216)");				
					lSBQuery.append(" and mst.DOJ >= '2004-01-01' and mst.REG_STATUS in (1,2) and mst.FORM_STATUS = 1 and mst.DDO_CODE <> '1111222222' and mst.ALL_INDIA_SERVI_OLD_EMP = 'Y'");
					lSBQuery.append(" order by mst.EMP_NAME,rlt.GIS_APPLICABLE ");
					
					Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
					SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
					Query.setString("id", AccmaintainedLook_id);
					 logger.info("All india DCPS Script  ***********" + lSBQuery.toString());
					List lLstFinal = Query.list();
					
					List dataListForTable = new ArrayList();
					
					if (lLstFinal != null && !lLstFinal.isEmpty())
					{
						
						for (Iterator it = lLstFinal.iterator();it.hasNext();)
				
						{
							
							dataListForTable = new ArrayList();
						 Object[] lObj = (Object[])it.next();
						 
						 Empname = (lObj[1]!=null)?lObj[1].toString():"NA";
						 logger.info("Employee name is  ***********" + 	Empname);
						 
						 Sevaarth_id = (lObj[2]!=null)?lObj[2].toString():"NA";
						 logger.info(" sevaarth is  ***********" + 	Sevaarth_id);
						 
						 dateoj = (lObj[3]!=null)?lObj[3].toString():"NA";					 
						 logger.info("Date of joing is  ***********" + 	dateoj);						 
						 
						 DCPS_ID = (lObj[4]!=null)?lObj[4].toString():"NA";
						 		 logger.info("DCPS id is  ***********" + 	DCPS_ID);
						 		  
						 pranNo = (lObj[5]!=null)?lObj[5].toString():"NA";
						 logger.info("Pran Number is  ***********" + 	pranNo);
						 
						 accMaintainedBy = (lObj[6]!=null)?lObj[6].toString():"NA";
						 logger.info("account maintained By  ***********" + 	accMaintainedBy);
						 
						 
						 date = ((lObj[7]!=null)?lObj[7].toString():"NA");
						 logger.info("Updated Date  ***********" + 	date);
						 
						 dataListForTable.add( Sevaarth_id);
						 dataListForTable.add( Empname);
						 dataListForTable.add( DCPS_ID);
						 dataListForTable.add( dateoj);				
						 dataListForTable.add( pranNo);
						 dataListForTable.add( accMaintainedBy);	
						 dataListForTable.add(date);
						 DataList.add(dataListForTable);;
								 
						}
					   }
					 }
				
				
			
			
		}
		catch(Exception e)
		{
			logger.error("Error in ALL INDIA DCPS REPORT " + e.getMessage());
			logger.error("Printing StackTrace");
			e.printStackTrace();
		}
		finally {
			try {
				
					lStmt.close();
					lCon.close();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error("Exception :" + e1);

			}
		}

		return DataList;
		
	}
	
	public List<ComboValuesVO> getGISApplicableid(Hashtable otherArgs,String lStrLangId, String lStrLocId) {
	{
		Hashtable sessionKeys = (Hashtable) (otherArgs)
		.get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstadmin = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT LOOKUP_ID,LOOKUP_NAME FROM CMN_LOOKUP_MST where LOOKUP_ID in (700214,700215,700216) and LANG_ID =  ");
		lSBQuery.append(" (SELECT LANG_ID from cmn_Language_Mst where LANG_ID =:langId )  ");

		try {
			ghibSession = ServiceLocator.getServiceLocator()
			.getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("langId", lLngLangId);
			
			lLstResult = lQuery.list();

			if (lLstResult != null && lLstResult.size() > 0) {
				for (Object[] lArrObj : lLstResult) {
					lObjComboValueVO = new ComboValuesVO();
					lObjComboValueVO.setId(lArrObj[0].toString());
					lObjComboValueVO.setDesc(lArrObj[1].toString());
					lLstadmin.add(lObjComboValueVO);
				}
			}

		} catch (Exception e) {
			logger.error(
					" Error is in getGISApplicableid methods ###################### : "
					+ e);
			e.printStackTrace();
		}

		return lLstadmin;
	}

	}
}
