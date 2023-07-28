package com.tcs.sgv.dcps.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
import com.tcs.sgv.common.valuebeans.reports.PageBreak;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.core.service.ServiceLocator;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 May 26, 2012
 */

public class UIDReport extends DefaultReportDataFinder implements ReportDataFinder {

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

	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException 
	{	
		List lLstDataList = new ArrayList();
		List lLstEmpDtls = null;
		List lLstDdoData = null;
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		String lStrDdoCode = "";
		String lStrDesgn = "";
		String lStrDepartment = "";
		new ReportsDAOImpl();		
		Object []lObj = null;
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

			if (lObjReport.getReportCode().equals("700090")) 
			{	
				StyleVO[] lObjHomePageStyleList = new StyleVO[1];
				lObjHomePageStyleList[0] = new StyleVO();
				lObjHomePageStyleList[0].setStyleId(IReportConstants.REPORT_FOOTER);
				lObjHomePageStyleList[0].setStyleValue("* If EID or UID is not obtained write NO EID (Enrollment ID which is given on the spot by Adhaar authorities till pending receipt of UID).");
				lObjReport.addReportStyles(lObjHomePageStyleList);
				
				lLstDdoData = getDdoDtlsFromLocId(gStrLocCode);
				lObj = (Object[]) lLstDdoData.get(0);
				
				lStrDdoCode = lObj[0].toString();
				lStrDesgn = lObj[1].toString();
				lStrDepartment = lObj[2].toString();
				
				String lSBHeader = lObjReport.getReportName();
				lSBHeader = lSBHeader + "\r\n Department : " +lStrDepartment + "\r\n DDO Code : " + lStrDdoCode + "\r\n Designation : " + lStrDesgn+ "" ;
				lObjReport.setReportName(lSBHeader);
				
				StringBuilder SBQuery = new StringBuilder();
				
				if (!lStrDdoCode.equals("")) {
					SBQuery.append("SELECT dcps.EMP_NAME, dcps.SEVARTH_ID, dcps.UID_NO, dcps.EID_NO FROM MST_DCPS_EMP dcps");
					SBQuery.append(" inner join ORG_EMP_MST emp on dcps.ORG_EMP_MST_ID = emp.emp_id ");
					SBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.user_id = emp.user_id and usr.activate_flag = 1 ");
					SBQuery.append("WHERE DDO_CODE = '"+lStrDdoCode+"' ");					
				} 
				Query lQuery = ghibSession.createSQLQuery(SBQuery.toString());
				lLstEmpDtls = lQuery.list();
				Iterator IT = lLstEmpDtls.iterator();
				Integer counter = 1;
				ArrayList rowList = new ArrayList();				
				
				while (IT.hasNext()) {
					lObj = (Object[]) IT.next();
					rowList = new ArrayList();
					
					if (lObj[0] != null) {
						rowList.add(lObj[0]);
					}else {
						rowList.add("");
					}
					
					if (lObj[1] != null) {
						rowList.add(lObj[1]);
					} else {
						rowList.add("");
					}
					
					if (lObj[2] != null) {						
						rowList.add(lObj[2]);
					} else{
						rowList.add("");						
					}
					
					/*if (lObj[3] != null && ( lObj[2] == null ) {
						rowList.add(lObj[3]);
					} else{
						rowList.add("");
					}*/
					
					if (lObj[3] != null) {						
						rowList.add(lObj[3]);
					} else{
						rowList.add("");						
					}
					rowList.add("");
					
					lLstDataList.add(rowList);
					counter = counter + 1;
				}							
			}
			if (lObjReport.getReportCode().equals("8000073")) 
			{	
				StyleVO[] style = new StyleVO[2];
				style[0] = new StyleVO();
				style[0].setStyleId(IReportConstants.REPORT_PAGE_OK_BTN_URL);
				style[0].setStyleValue("ifms.htm?actionFlag=getHomePage");
				style[1] = new StyleVO();
				style[1].setStyleId(IReportConstants.ROWS_PER_PAGE);
				style[1].setStyleValue("20");
				lObjReport.setStyleList(style);
				
				StyleVO[] lObjHomePageStyleList = new StyleVO[1];
				lObjHomePageStyleList[0] = new StyleVO();
				lObjHomePageStyleList[0].setStyleId(IReportConstants.REPORT_FOOTER);
				lObjHomePageStyleList[0].setStyleValue("* If EID or UID is not obtained write NO EID (Enrollment ID which is given on the spot by Adhaar authorities till pending receipt of UID).");
				//lObjReport.addReportStyles(lObjHomePageStyleList);
				
				lLstDdoData = getDdoDtlsFromLocId(gStrLocCode);
				lObj = (Object[]) lLstDdoData.get(0);
				
				lStrDdoCode = lObj[0].toString();
				lStrDesgn = lObj[1].toString();
				lStrDepartment = lObj[2].toString();
				
				String lSBHeader = lObjReport.getReportName();
				lSBHeader = lSBHeader + "\r\n Department : " +lStrDepartment + "\r\n DDO Code : " + lStrDdoCode + "\r\n Designation : " + lStrDesgn+ "" ;
				lObjReport.setReportName(lSBHeader);
				
				StringBuilder SBQuery = new StringBuilder();
				
				if (!lStrDdoCode.equals("")) {
					SBQuery.append("SELECT em.EMP_NAME, em.SEVARTH_ID, em.UID_NO, em.EID_NO FROM MST_DCPS_EMP em ");
					SBQuery.append(" inner join ORG_EMP_MST emp on em.ORG_EMP_MST_ID = emp.emp_id ");
					SBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.user_id = emp.user_id and usr.activate_flag = 1 ");
					SBQuery.append("WHERE em.DDO_CODE = '"+lStrDdoCode+"' and (em.UID_NO is not null and em.UID_NO <> 0)");
					//SBQuery.append(" and SUPER_ANN_DATE > sysdate ");
					SBQuery.append(" and em.REG_STATUS in (1,2) and em.FORM_STATUS  = 1 ");
				} 
				Query lQuery = ghibSession.createSQLQuery(SBQuery.toString());
				lLstEmpDtls = lQuery.list();
				Iterator IT = lLstEmpDtls.iterator();
				Integer counter = 1;
				ArrayList rowList = new ArrayList();				
				
				while (IT.hasNext()) {
					lObj = (Object[]) IT.next();
					rowList = new ArrayList();
					
					if (lObj[0] != null) {
						rowList.add(lObj[0]);
					}else {
						rowList.add("");
					}
					
					if (lObj[1] != null) {
						rowList.add(lObj[1]);
					} else {
						rowList.add("");
					}
					
					if (lObj[2] != null) {						
						rowList.add(lObj[2]);
					} else{
						rowList.add("");						
					}
					
					/*if (lObj[3] != null && lObj[2] == null) {
						rowList.add(lObj[3]);
					} else{
						rowList.add("");
					}*/
					
					//rowList.add("");
					
					lLstDataList.add(rowList);
					if(counter%20 == 0){
						rowList = new ArrayList();
						rowList.add(new PageBreak());
						lLstDataList.add(rowList);
						}
					counter = counter + 1;
				}							
			}
			if (lObjReport.getReportCode().equals("8000074")) 
			{	
				StyleVO[] style = new StyleVO[2];
				style[0] = new StyleVO();
				style[0].setStyleId(IReportConstants.REPORT_PAGE_OK_BTN_URL);
				style[0].setStyleValue("ifms.htm?actionFlag=getHomePage");
				style[1] = new StyleVO();
				style[1].setStyleId(IReportConstants.ROWS_PER_PAGE);
				style[1].setStyleValue("20");
				
				lObjReport.setStyleList(style);
				
				lLstDdoData = getDdoDtlsFromLocId(gStrLocCode);
				lObj = (Object[]) lLstDdoData.get(0);
				
				lStrDdoCode = lObj[0].toString();
				lStrDesgn = lObj[1].toString();
				lStrDepartment = lObj[2].toString();
				
				String lSBHeader = lObjReport.getReportName();
				lSBHeader = lSBHeader + "\r\n Department : " +lStrDepartment + "\r\n DDO Code : " + lStrDdoCode + "\r\n Designation : " + lStrDesgn+ "" ;
				lObjReport.setReportName(lSBHeader);
				
				StringBuilder SBQuery = new StringBuilder();
				
				if (!lStrDdoCode.equals("")) {
					SBQuery.append("SELECT emp.EMP_NAME, emp.SEVARTH_ID, emp.UID_NO, emp.EID_NO,reason.NOT_UID_REASON,emp.DCPS_EMP_ID  ");
					SBQuery.append(" FROM MST_DCPS_EMP emp inner join ifms.mst_dcps_not_uid_reason reason  ");
					SBQuery.append(" on reason.DCPS_EMP_ID = emp.DCPS_EMP_ID and reason.SEVAARTH_ID = emp.SEVARTH_ID  ");
					SBQuery.append(" inner join ORG_EMP_MST em on emp.ORG_EMP_MST_ID = em.emp_id ");
					SBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.user_id = em.user_id and usr.activate_flag = 1 ");					
					SBQuery.append(" where emp.DDO_CODE = "+lStrDdoCode+" and (emp.UID_NO is null OR emp.UID_NO = 0) and emp.FORM_STATUS = 1 and emp.REG_STATUS in (1,2) ");
					//SBQuery.append(" and emp.SUPER_ANN_DATE > sysdate  ");
				} 
				Query lQuery = ghibSession.createSQLQuery(SBQuery.toString());
				lLstEmpDtls = lQuery.list();
				Iterator IT = lLstEmpDtls.iterator();
				Integer counter = 1;
				ArrayList rowList = new ArrayList();				
				
				while (IT.hasNext()) {
					lObj = (Object[]) IT.next();
					rowList = new ArrayList();
					
					if (lObj[0] != null) {
						rowList.add(lObj[0]);
					}else {
						rowList.add("");
					}
					
					if (lObj[1] != null) {
						rowList.add(lObj[1]);
					} else {
						rowList.add("");
					}
					
					if (lObj[3] != null) {						
						rowList.add(lObj[3]);
					} else{
						rowList.add("");						
					}
					if (lObj[4] != null) {						
						rowList.add(lObj[4]);
					} else{
						rowList.add("");						
					}
					/*if (lObj[3] != null && lObj[2] == null) {
						rowList.add(lObj[3]);
					} else{
						rowList.add("");
					}*/
					
					//rowList.add("");
					
					lLstDataList.add(rowList);
					if(counter%20 == 0){
					rowList = new ArrayList();
					rowList.add(new PageBreak());
					lLstDataList.add(rowList);
					}
					counter = counter + 1;
				}							
			}	
		} catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		} 
		return lLstDataList;
	}

	public String space(int noOfSpace) {
		String blank = "";
		for (int i = 0; i < noOfSpace; i++) {
			blank += "\u00a0";
		}
		return blank;
	}
	
	public List getDdoDtlsFromLocId(String lLngLocId)
	{
		List lLstResData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DDO_CODE,DSGN_NAME,DDO_NAME FROM ORG_DDO_MST ");
			lSBQuery.append("WHERE LOCATION_CODE =:locId AND ACTIVATE_FLAG = 1 ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("locId", lLngLocId);
			lLstResData = lQuery.list();			
		}catch (Exception e) {
			gLogger.error("Exception in getDdoCodeFromPostId:" + e, e);
		}
		return lLstResData;
	}

}
