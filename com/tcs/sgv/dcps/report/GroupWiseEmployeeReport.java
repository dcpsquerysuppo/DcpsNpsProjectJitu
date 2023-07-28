package com.tcs.sgv.dcps.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

public class GroupWiseEmployeeReport extends DefaultReportDataFinder implements ReportDataFinder
{
	private static final Logger gLogger = Logger.getLogger("DCPSReports");
	public static String newline = System.getProperty("line.separator");
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Map lMapSeriesHeadCode = null;	
	Session ghibSession = null;
	
	public Collection findReportData(ReportVO lObjReport, Object criteria) throws ReportException 
	{
		List lLstDataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		Map<String, String> lMapEmpCount_A = new HashMap<String, String>();
		Map<String, String> lMapEmpCount_B = new HashMap<String, String>();
		Map<String, String> lMapEmpCount_BnGz = new HashMap<String, String>();
		Map<String, String> lMapEmpCount_C = new HashMap<String, String>();
		Map<String, String> lMapEmpCount_D = new HashMap<String, String>();
		
		LoginDetails lObjLoginVO = null;
		Object []lObjEmpData = null;
		
		try {						
			lMapRequestAttributes = (Map) ((Map) criteria).get(IReportConstants.REQUEST_ATTRIBUTES);
			lMapSessionAttributes = (Map) ((Map) criteria).get(IReportConstants.SESSION_KEYS);
			lObjLoginVO = (LoginDetails) lMapSessionAttributes.get("loginDetails");
			serviceLocator = (ServiceLocator) lMapRequestAttributes.get("serviceLocator");
			gObjSessionFactory = serviceLocator.getSessionFactorySlave();
			gStrLocCode = lObjLoginVO.getLocation().getLocationCode();
			gLngLangId = lObjLoginVO.getLangId();
			Map lServiceMap = (Map) lMapRequestAttributes.get("serviceMap");
			Map lBaseLoginMap = (Map) lServiceMap.get("baseLoginMap");
			gLngPostId = (Long) lBaseLoginMap.get("loggedInPost");
			ghibSession = gObjSessionFactory.getCurrentSession();
			SimpleDateFormat lObjSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			String lStrLocCode = "";
			String []lStrFromToDate = null;
			String lStrFromAndToDate = "";
			String lStrFromDate = "";
			String lStrLocName = "";
			List lLstGroupWiseEmpCount = null;
			List lLstDistinctCount = null;
			Date lDtFromDate = null;
			String lStrFinYearDesc = "";
			
			if(lObjReport.getReportCode().equals("700095")) 
			{
				Long lLngFinYear = Long.parseLong(lObjReport.getParameterValue("FinYear").toString());
				lStrLocCode = lObjReport.getParameterValue("TreasuryCode").toString();
				
				if(lLngFinYear != null){
					lStrFromAndToDate = getFromToDate(lLngFinYear);
					lStrFromToDate = lStrFromAndToDate.split(",");
					lStrFromDate = lStrFromToDate[0];
					lDtFromDate = lObjSimpleDateFormat.parse(lStrFromDate);
				}
				
				lStrFinYearDesc = getFinYearDec(lLngFinYear);
				
				if(!lStrLocCode.equals("")){
					lStrLocName = getLocationName(Long.parseLong(lStrLocCode));
					lObjReport.setAdditionalHeader("Financial Year: "+lStrFinYearDesc+"\r\nTreasury : "+lStrLocCode +" ("+lStrLocName+")");
				}else{
					lObjReport.setAdditionalHeader("Financial Year: "+lStrFinYearDesc);
				}
				
				ReportColumnVO[] lArrReportColumnVO = lObjReport.getReportColumns();
				for(int lIntCnt = 0; lIntCnt < lArrReportColumnVO.length; lIntCnt++)
				{
					if(lArrReportColumnVO[lIntCnt].getColumnId() == 2){
						if(!lStrLocCode.equals("")){
							lArrReportColumnVO[lIntCnt].setColumnHeader("DDO Office");
						}else{
							lArrReportColumnVO[lIntCnt].setColumnHeader("Treasury Name");
						}
						break;
					}
				}
				
				if(!lStrLocCode.equals("")){
					lLstGroupWiseEmpCount = getDDOWiseCount(lDtFromDate, lStrLocCode);
				}else{
					lLstGroupWiseEmpCount = getTreasuryWiseCount(lDtFromDate);
				}
				
				for(Integer lIntCnt = 0; lIntCnt < lLstGroupWiseEmpCount.size(); lIntCnt++)
				{
					lObjEmpData = (Object[]) lLstGroupWiseEmpCount.get(lIntCnt);
					
					if(lObjEmpData[1].toString().equals("A")){
						lMapEmpCount_A.put(lObjEmpData[0].toString(), lObjEmpData[2].toString());
					}else if(lObjEmpData[1].toString().equals("B")){
						lMapEmpCount_B.put(lObjEmpData[0].toString(), lObjEmpData[2].toString());
					}else if(lObjEmpData[1].toString().equals("BnGz")){
						lMapEmpCount_BnGz.put(lObjEmpData[0].toString(), lObjEmpData[2].toString());
					}else if(lObjEmpData[1].toString().equals("C")){
						lMapEmpCount_C.put(lObjEmpData[0].toString(), lObjEmpData[2].toString());
					}else if(lObjEmpData[1].toString().equals("D")){
						lMapEmpCount_D.put(lObjEmpData[0].toString(), lObjEmpData[2].toString());
					}
				}
				
				ArrayList rowList = new ArrayList();
				
				if(!lStrLocCode.equals("")){
					lLstDistinctCount = getDistinctDDOWiseCount(lDtFromDate, lStrLocCode);
				}else{
					lLstDistinctCount = getDistinctTreasury(lDtFromDate);
				}				
				
				Iterator IT = lLstDistinctCount.iterator();
				Integer Counter = 1;
				Long lLngCount_A = null;
				Long lLngCount_B = null;
				Long lLngCount_BnGz = null;
				Long lLngCount_C = null;
				Long lLngCount_D = null;
				Long lLngTotal = null;
				String lObjLocCode = "";
				
				while(IT.hasNext()){
					
					lObjLocCode = (String) IT.next();
					
					lLngCount_A = lMapEmpCount_A.get(lObjLocCode) != null ? Long.parseLong(lMapEmpCount_A.get(lObjLocCode)) : 0l;
					lLngCount_B = lMapEmpCount_B.get(lObjLocCode) != null ? Long.parseLong(lMapEmpCount_B.get(lObjLocCode)) : 0l;
					lLngCount_BnGz = lMapEmpCount_BnGz.get(lObjLocCode) != null ? Long.parseLong(lMapEmpCount_BnGz.get(lObjLocCode)) : 0l;
					lLngCount_C = lMapEmpCount_C.get(lObjLocCode) != null ? Long.parseLong(lMapEmpCount_C.get(lObjLocCode)) : 0l;
					lLngCount_D = lMapEmpCount_D.get(lObjLocCode) != null ? Long.parseLong(lMapEmpCount_D.get(lObjLocCode)) : 0l;
					
					rowList = new ArrayList();
					
					rowList.add(Counter);					
					
					
					if(!lStrLocCode.equals("")){
						lStrLocName = getLocationNameForDdo(lObjLocCode);
						rowList.add(lStrLocName);
					}else{
						lStrLocName = getLocationName(Long.parseLong(lObjLocCode));
						rowList.add(new URLData(lStrLocName,"ifms.htm?actionFlag=reportService&reportCode=700095&action=generateReport&FinYear="+lLngFinYear+"&TreasuryCode="+lObjLocCode));
					}	
					
					
					
					
					lLngTotal = lLngCount_A + lLngCount_B + lLngCount_BnGz + lLngCount_C + lLngCount_D;
					
					rowList.add(lLngCount_A);
					rowList.add(lLngCount_B);
					rowList.add(lLngCount_BnGz);
					rowList.add(lLngCount_C);
					rowList.add(lLngCount_D);
					
					rowList.add(lLngTotal);
					
					lLstDataList.add(rowList);
					
					Counter = Counter + 1;
				}
				
			}
		}catch (Exception e) {
			gLogger.info("findReportData(): Exception is" + e, e);
		} 
		return lLstDataList;
	}
	
	public List getTreasuryWiseCount(Date ldtFromDate)throws Exception
	{
		List lLstEmpCountdata = null;		
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT RDO.LOCATION_CODE,ME.EMP_GROUP,COUNT(ME.DCPS_EMP_ID) ");
			lSBQuery.append("FROM Mst_Dcps_Emp ME, Rlt_Ddo_Org RDO, CMN_LOCATION_MST CM ");
			lSBQuery.append("WHERE ME.DDO_CODE = RDO.DDO_CODE AND ME.DOJ > :DOJ AND CM.LOC_ID = RDO.LOCATION_CODE ");
			lSBQuery.append("AND ME.reg_status in (1,2) AND ME.EMP_GROUP IN ('A','B','BnGz','C','D') ");
			lSBQuery.append("GROUP BY ME.EMP_GROUP,RDO.LOCATION_CODE ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setDate("DOJ", ldtFromDate);
			
			lLstEmpCountdata = lQuery.list();
		}catch (Exception e) {
			gLogger.info("Exception is" + e, e);
		}
		return lLstEmpCountdata;
	}
	
	public List getDDOWiseCount(Date lDtFromDate, String lStrLocCode)throws Exception
	{
		List lLstEmpCountdata = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT RDO.DDO_CODE,ME.EMP_GROUP,COUNT(ME.DCPS_EMP_ID) ");
			lSBQuery.append("FROM Mst_Dcps_Emp ME, Rlt_Ddo_Org RDO, CMN_LOCATION_MST CM ");
			lSBQuery.append("WHERE ME.DDO_CODE = RDO.DDO_CODE AND CM.LOC_ID = RDO.LOCATION_CODE ");
			lSBQuery.append("AND ME.DOJ > :DOJ AND ME.reg_status in (1,2) AND RDO.LOCATION_CODE = :locCode ");
			lSBQuery.append("AND ME.EMP_GROUP IN ('A','B','BnGz','C','D') ");
			lSBQuery.append("GROUP BY ME.EMP_GROUP,RDO.DDO_CODE ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setDate("DOJ", lDtFromDate);
			lQuery.setParameter("locCode", lStrLocCode);
			
			lLstEmpCountdata = lQuery.list();
		}catch (Exception e) {
			gLogger.info("Exception is" + e, e);
		}
		return lLstEmpCountdata;
	}
	
	public String getFromToDate(Long lLngFinYearId)
	{
		List lLstResData = null;
		SimpleDateFormat lObjSimpleDate = new SimpleDateFormat("dd/MM/yyyy");
		String lStrFromDate = "";
		String lStrToDate = "";
		String lStrResData = "";
		Object lObj[] = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT fromDate, toDate FROM SgvcFinYearMst ");
			lSBQuery.append("WHERE finYearId = :finYearId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("finYearId", lLngFinYearId);
			lLstResData = lQuery.list();
			
			if(lLstResData != null && lLstResData.size() > 0){
				lObj = (Object[]) lLstResData.get(0);
				lStrFromDate = lObjSimpleDate.format(lObj[0]);
				lStrToDate = lObjSimpleDate.format(lObj[1]);
			}
			
			lStrResData = lStrFromDate+","+lStrToDate;
		}catch(Exception e){
			gLogger.error("Exception in getFromToDate:" + e, e);
		}
		
		return lStrResData;
	}
	
	public String getLocationName(Long lLngLocId)
	{
		List lLstData = null;
		String lStrLocName = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT LOC_NAME FROM CMN_LOCATION_MST WHERE LOC_ID = :locId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("locId", lLngLocId);
			lLstData = lQuery.list();
			
			if(lLstData.size() > 0){
				lStrLocName = lLstData.get(0).toString();
			}
		}catch(Exception e){
			gLogger.error("Exception in getLocationName:" + e, e);
		}
		return lStrLocName;
	}
	
	public String getLocationNameForDdo(String lStrDdoCode)
	{
		List lLstData = null;
		String lStrLocName = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT CM.LOC_NAME FROM CMN_LOCATION_MST CM, ORG_DDO_MST ODM WHERE ODM.DDO_CODE = :ddoCode ");
			lSBQuery.append("AND ODM.LOCATION_CODE = CM.LOCATION_CODE ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLstData = lQuery.list();
			
			if(lLstData.size() > 0){
				lStrLocName = lLstData.get(0).toString();
			}
		}catch(Exception e){
			gLogger.error("Exception in getLocationName:" + e, e);
		}
		return lStrLocName;
	}
	
	public List getDistinctTreasury(Date ldtFromDate)throws Exception
	{
		List lLstEmpCountdata = null;		
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DISTINCT (RDO.LOCATION_CODE) ");
			lSBQuery.append("FROM Mst_Dcps_Emp ME, Rlt_Ddo_Org RDO, CMN_LOCATION_MST CM ");
			lSBQuery.append("WHERE ME.DDO_CODE = RDO.DDO_CODE AND ME.DOJ > :DOJ AND CM.LOC_ID = RDO.LOCATION_CODE ");
			lSBQuery.append("AND ME.reg_status in (1,2) AND ME.EMP_GROUP IN ('A','B','BnGz','C','D') ");			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setDate("DOJ", ldtFromDate);
			
			lLstEmpCountdata = lQuery.list();
		}catch (Exception e) {
			gLogger.info("Exception is" + e, e);
		}
		return lLstEmpCountdata;
	}
	 
	public List getDistinctDDOWiseCount(Date lDtFromDate, String lStrLocCode)throws Exception
	{
		List lLstEmpCountdata = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DISTINCT RDO.DDO_CODE FROM Mst_Dcps_Emp ME, Rlt_Ddo_Org RDO, CMN_LOCATION_MST CM ");
			lSBQuery.append("WHERE ME.DDO_CODE = RDO.DDO_CODE AND CM.LOC_ID = RDO.LOCATION_CODE AND ME.DOJ > :DOJ ");
			lSBQuery.append("AND ME.reg_status in (1,2) AND RDO.LOCATION_CODE =:locCode ");
			lSBQuery.append("AND ME.EMP_GROUP IN ('A','B','BnGz','C','D') ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setDate("DOJ", lDtFromDate);
			lQuery.setParameter("locCode", lStrLocCode);
			
			lLstEmpCountdata = lQuery.list();
		}catch (Exception e) {
			gLogger.info("Exception is" + e, e);
		}
		return lLstEmpCountdata;
	}
	
	public String getFinYearDec(Long lLngFinYear)
	{
		String lStrFinYearDesc = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT FIN_YEAR_DESC FROM SGVC_FIN_YEAR_MST WHERE FIN_YEAR_ID =:finYear ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("finYear", lLngFinYear);
			lStrFinYearDesc = lQuery.list().get(0).toString();
		}catch (Exception e) {
			gLogger.error("Exception is" + e, e);
		}
		return lStrFinYearDesc;
	}
}