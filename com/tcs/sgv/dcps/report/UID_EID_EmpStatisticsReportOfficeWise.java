package com.tcs.sgv.dcps.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.service.ServiceLocator;

public class UID_EID_EmpStatisticsReportOfficeWise extends DefaultReportDataFinder implements ReportDataFinder
{
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
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;		
		String lStrReportType = "";
		String lStrAdminDept = "";
		String lStrFieldDept = "";
		String lStrOffice = "";
		String lStrAdminDeptName = "";
		String lStrFieldDeptName = "";
		String lStrOfficeName = "";		
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
			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			if (lObjReport.getReportCode().equals("700092")) 
			{
				lStrAdminDept = lObjReport.getParameterValue("adminDept").toString();
				lStrFieldDept = lObjReport.getParameterValue("fieldDept").toString();
				lStrOffice = lObjReport.getParameterValue("office").toString();
				
				Date lObjDt = new Date();
				String lSBHeader = lObjReport.getReportName();
				
				lStrFieldDeptName = getDepartmentName(Long.parseLong(lStrFieldDept));
				lStrAdminDeptName = getDepartmentName(Long.parseLong(lStrAdminDept));
				lStrOfficeName = getOfficeName(lStrOffice);
				
				lSBHeader = "UID/EID Employee Statistics Report As On "+ lObjSimpleDateFormat.format(lObjDt)+"\r\n (Employee Wise)";
				lObjReport.setReportName(lSBHeader);
				
				lObjReport.setAdditionalHeader("Admin Department : "+lStrAdminDeptName + "\r\n Field Department : "+lStrFieldDeptName
						+ "\r\n Office : "+lStrOfficeName);
				
				lLstEmpDtls = getAllEmployees(lStrOffice, Long.parseLong(lStrFieldDept), Long.parseLong(lStrAdminDept));
							
				ArrayList rowList = new ArrayList();
				Iterator IT = lLstEmpDtls.iterator();
				Integer counter = 1;
				
				while (IT.hasNext()) {
					lObj = (Object[]) IT.next();
					rowList = new ArrayList();
					
					rowList.add(counter);
					
					if (lObj[0] != null) {
						rowList.add(lObj[0].toString());
					}else {
						rowList.add("");
					}
					
					if (lObj[1] != null) {
						rowList.add(lObj[1].toString());
					}else {
						rowList.add("");
					}
					
					if (lObj[2] != null) {
						rowList.add(lObj[2].toString());
					}else {
						rowList.add("");
					}
					
					if (lObj[3] != null && !lObj[3].toString().equals("0")) {
						rowList.add(lObj[3].toString());
					}else {
						rowList.add("");
					}
					
					if (lObj[4] != null  && !lObj[4].toString().equals("0")) {
						rowList.add(lObj[4].toString());
					}else {
						rowList.add("");
					}
					
					lLstDataList.add(rowList);
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
	
	public String getDepartmentName(Long lLngDeptId)
	{
		List lLstDept = null;
		String lStrDeptName = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT LOC_NAME FROM CMN_LOCATION_MST WHERE LOC_ID = :locId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("locId", lLngDeptId);
			lLstDept = lQuery.list();
			
			if(lLstDept.size() > 0){
				lStrDeptName = lLstDept.get(0).toString();
			}
		}catch(Exception e){
			gLogger.error("Exception in getDepartmentName:" + e, e);
		}
		return lStrDeptName;
	}
	
	public String getOfficeName(String lStrDdoCode)
	{
		List lLstOffice = null;
		String lStrOfficeName = "";
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DDO_OFFICE FROM ORG_DDO_MST WHERE DDO_CODE = :ddoCode ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLstOffice = lQuery.list();
			
			if(lLstOffice.size() > 0){
				lStrOfficeName = lLstOffice.get(0).toString();
			}
		}catch(Exception e){
			gLogger.error("Exception in getOfficeName:" + e, e);
		}
		return lStrOfficeName;
	}
	
	public List getAllEmployees(String lStrDdoCode, Long lLngFieldDept, Long lLngAdminDept)
	{
		List lLstEmpDtls = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ME.SEVARTH_ID, ME.EMP_NAME, nvl(ODM.DSGN_NAME,' '), ME.UID_NO, ME.EID_NO ");
			lSBQuery.append("FROM ORG_DDO_MST OM JOIN MST_DCPS_EMP ME ON ME.DDO_CODE = OM.DDO_CODE ");
			lSBQuery.append("LEFT OUTER JOIN ORG_DESIGNATION_MST ODM ON ME.DESIGNATION = ODM.DSGN_CODE ");			
			lSBQuery.append("WHERE OM.DEPT_LOC_CODE =:adminDept AND OM.HOD_LOC_CODE =:fieldDept AND OM.DDO_CODE =:ddoCode ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("adminDept", lLngAdminDept);
			lQuery.setLong("fieldDept", lLngFieldDept);
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLstEmpDtls = lQuery.list();
		}catch(Exception e){
			gLogger.error("Exception in getAllEmployees:" + e, e);
		}
		
		return lLstEmpDtls;
	}
}
