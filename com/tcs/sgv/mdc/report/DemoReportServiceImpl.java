package com.tcs.sgv.mdc.report;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.PageBreak;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportTemplate;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.TabularData;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.service.ServiceLocator;



public class DemoReportServiceImpl extends DefaultReportDataFinder implements ReportDataFinder {

	private static Logger logger = Logger
			.getLogger(PostEmpCorrectionDtlsDAOImpl.class);

	private StyleVO[] selfCloseVO = null;
	private ResultSet lRs1 = null;
	Session ghibSession = null;
	public Collection findReportData(ReportVO report, Object criteria)throws ReportException {

		Connection lCon = null;
		Statement lStmt = null;

		Map requestKeys = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
		Map serviceMap = (Map) requestKeys.get("serviceMap");
		Map baseLoginMap = (Map) serviceMap.get("baseLoginMap");
		CmnLocationMst locationVO = (CmnLocationMst) baseLoginMap.get("locationVO");
		String locationName = locationVO.getLocName();
		long locationId = locationVO.getLocId();
		String locationNameincaps = locationName.toUpperCase();
		ServiceLocator serv1 = (ServiceLocator) requestKeys.get("serviceLocator");

		ArrayList DataList = new ArrayList();

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

			if (report.getReportCode().equals("700100")) {
				StringBuffer lsb = new StringBuffer();
		String Location_code;
				Location_code = (String) report.getParameterValue("Loc_Id");

				logger.info("Location code is  ***********" + Location_code);
				
			 DemoReportDAO Objpostempcorredtl = new
			 DemoReportDAOImpl(CmnLocationMst.class,
			 serv.getSessionFactory()); //ArrayList outerList = new	 ArrayList();
			 Objpostempcorredtl.setSessionFactory(serv.getSessionFactory());
			 //outerList= (ArrayList)
			Map fieldMap=new HashMap();
			//fieldMap=Objpostempcorredtl.getFieldlist(Location_code);
			 
			 
			 //logger.info("DemoReportDAO Cantaining List  ***********"+ outerList);
			 
			 
			 String deptHeader ="This is DEMO REPORT";
			 
			 ArrayList styleList = new ArrayList(); 
			 ArrayList stData = new ArrayList();
			 
			 StyledData styledHeader = new StyledData
			 (deptHeader,headerStyleVo); styledHeader.setColspan(2);
			 stData.add(styledHeader);
			 
			 styleList.add(stData);
			 
			 
			 TabularData tData = new TabularData(styleList);
			 
			 tData.addStyle(IReportConstants.STYLE_FONT_ALIGNMENT,
			 IReportConstants.VALUE_FONT_ALIGNMENT_CENTER);
			 tData.addStyle(IReportConstants.BORDER, "No");
			 tData.addStyle(IReportConstants.SHOW_REPORT_NAME,
			 IReportConstants.VALUE_NO);
			 report.setAdditionalHeader(tData);
			 
						/* 
			 RowList= (List)
			 Objpostempcorredtl.getFieldlist(Location_code);
			 
			 
			 Iterator itr = RowList.iterator();
			 
			 Long Field_Location_Id ;
			 String Field_name;
			 
			 while (itr.hasNext()) { Object[] rowList = (Object[])
			 itr.next(); Field_Location_Id = (Long) rowList[0];
			 Field_name = (String) rowList[1];
			 
			
			 logger.info("Field Location**********"+Field_Location_Id);
			 logger.info("Field Name*****************"+Field_name);
			 */
			 ArrayList row = new ArrayList();
			 
			
			 
			  int lIntSerialNo =0;
			 Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			 Iterator<Map.Entry<Integer, Integer>> entries = fieldMap.entrySet().iterator();
			 while (entries.hasNext())
			 {   
				 Map.Entry<Integer, Integer> entry = entries.next(); 
				 
				 System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				row.add(lIntSerialNo++);
				 row.add(entry.getKey());
				 row.add(entry.getValue());
				 
			 }
			 
			 
			
			
		
			 
			 DataList.add(row);
			 
			 return DataList;
			}
		} catch (Exception e) {
			logger.error("Error in DEMO Report Service impl" + e.getMessage());
			logger.error("Printing StackTrace");
			e.printStackTrace();
		}

		return DataList;

	}
	
	public List<ComboValuesVO> getDetails(Hashtable otherArgs, String lStrLangId, String lStrLocId) {

		Hashtable sessionKeys = (Hashtable) (otherArgs).get(IReportConstants.SESSION_KEYS);
		Map loginMap = (Map) sessionKeys.get("loginDetailsMap");
		Long lLngLangId = null;
		if (loginMap.containsKey("langId")) {
			lLngLangId = (Long) loginMap.get("langId");
		}
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		List<ComboValuesVO> lLstadmin = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;
  
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT locId,locName FROM CmnLocationMst where cmnLanguageMst.langId =:langId and departmentId = 100001 ");

		try {
			ghibSession = ServiceLocator.getServiceLocator().getSessionFactory().getCurrentSession();
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
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
			logger.error(" Error is in getDetails methods###################### : " + e, e);
		}

		return lLstadmin;
	}

}

/*
 * public class DemoReportServiceImpl extends DefaultReportDataFinder implements
 * ReportDataFinder{
 * 
 * SessionFactory lObjSessionFactory=null; ArrayList outerList = new
 * ArrayList(); public Collection findReportData(ReportVO report, Object
 * criteria) throws ReportException {
 * System.out.println("Report service***********"); Map requestAttributes =
 * (Map) ((Map)criteria).get(IReportConstants.REQUEST_ATTRIBUTES); Map
 * sessionAttributes = (Map) ((Map)criteria).get(IReportConstants.SESSION_KEYS);
 * 
 * ServiceLocator serv =
 * (ServiceLocator)requestAttributes.get("serviceLocator"); lObjSessionFactory =
 * serv.getSessionFactorySlave();
 * 
 * 
 * 
 * List lListTestReturn=new ArrayList();
 * 
 * Long Location_code;
 * 
 * try{
 * 
 * Location_code=(Long) report.getParameterValue("Loc_Id");
 * System.out.println(Location_code+"******************");
 * System.out.println("$$$$$$$"+Location_code);
 * 
 * DemoReportDAOImpl Objpostempcorredtl = new
 * DemoReportDAOImpl(CmnLocationMst.class, serv.getSessionFactory());
 * 
 * Objpostempcorredtl.setSessionFactory(lObjSessionFactory); outerList=
 * (ArrayList) Objpostempcorredtl.getFieldlist(Location_code);
 * 
 * System.out.println(outerList); return outerList ; } catch(Exception e) {
 * 
 * System.out.println("Error is---------------- "+ e); e.getStackTrace(); }
 * return outerList ;
 * 
 * } }
 */