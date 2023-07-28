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
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.core.service.ServiceLocator;

public class DayBookReport extends DefaultReportDataFinder implements ReportDataFinder
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
		Map<String, String> lMapMonthDetails = new HashMap<String, String>();
		
		LoginDetails lObjLoginVO = null;
		
		List llStMonthData = null;
		Object []lObj = null;
		
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
			
			String lStrTreasuryName = "";
			
			if(lObjReport.getReportCode().equals("700096")) 
			{
				String lStrFromDate = lObjReport.getParameterValue("FromDate").toString();
				String lStrToDate = lObjReport.getParameterValue("ToDate").toString();
				String lStrTreasuryCode = lObjReport.getParameterValue("TreasuryCode").toString();
				
				Integer MaxDate = Integer.parseInt(lStrToDate.substring(0, 2));
				String lStrMonYear = lStrToDate.substring(2);
				
				lStrTreasuryName = getLocationName(Long.parseLong(lStrTreasuryCode));
				lObjReport.setAdditionalHeader("Treasury Name: "+lStrTreasuryName);
				
				llStMonthData = getAllDataForMonth(lObjSimpleDateFormat.parse(lStrFromDate), lObjSimpleDateFormat.parse(lStrToDate), lStrTreasuryCode);
				
				for(Integer lIntCnt = 0; lIntCnt < llStMonthData.size(); lIntCnt++)
				{
					lObj = (Object[]) llStMonthData.get(lIntCnt);
					
					if(lObj[0] != null){
						lMapMonthDetails.put(lObj[2].toString().substring(0, 2), lObj[1].toString());
					}
				}
				
				ArrayList rowList = new ArrayList();
				Iterator IT = llStMonthData.iterator();
				Integer Counter = 1;
				Long lLngContribution = 0l;
				Long lLngProgTotal = 0l;
				Long lLngTotal = 0l;
				String lStrDate = "";
				
				while(Counter <= MaxDate)
				{						
					rowList = new ArrayList();
					
					rowList.add(Counter);
					
					if(Counter < 10){
						rowList.add("0"+Counter+lStrMonYear);
						
						if(lMapMonthDetails.get("0"+Counter.toString()) != null){
							rowList.add(lMapMonthDetails.get("0"+Counter.toString()));						
							lLngProgTotal += Math.round(Double.parseDouble(lMapMonthDetails.get("0"+Counter.toString())));
							lLngTotal = lLngProgTotal + Math.round(Double.parseDouble(lMapMonthDetails.get("0"+Counter.toString())));
						}else{
							rowList.add(0l);
							lLngTotal = lLngProgTotal;
						}
						
					}else{
						rowList.add(Counter+lStrMonYear);
						
						if(lMapMonthDetails.get(Counter.toString()) != null){
							rowList.add(lMapMonthDetails.get(Counter.toString()));
							lLngProgTotal += Math.round(Double.parseDouble(lMapMonthDetails.get(Counter.toString())));
							lLngTotal = lLngProgTotal + Math.round(Double.parseDouble(lMapMonthDetails.get(Counter.toString())));
						}else{
							rowList.add(0l);
							lLngTotal = lLngProgTotal;
						}
					}
							
					rowList.add(lLngProgTotal);
					
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
	
	public List getAllDataForMonth(Date lDtFromdate, Date lDtToDate, String lStrLocationCode)
	{
		List lLstData = null;		
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT CV.VOUCHER_DATE,sum(TR.CONTRIBUTION),substr(CV.VOUCHER_DATE,9) FROM TRN_DCPS_CONTRIBUTION TR ");
			lSBQuery.append("JOIN MST_DCPS_CONTRI_VOUCHER_DTLS CV on CV.MST_DCPS_CONTRI_VOUCHER_DTLS = TR.RLT_CONTRI_VOUCHER_ID ");
			lSBQuery.append("WHERE CV.VOUCHER_DATE between :FromDate AND :ToDate AND TR.TREASURY_CODE = :treasuryCode ");
			lSBQuery.append("and TR.REG_STATUS in (3,1) and CV.VOUCHER_STATUS in (3,1) ");
			lSBQuery.append("GROUP BY CV.VOUCHER_DATE ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("treasuryCode", lStrLocationCode);
			lQuery.setDate("FromDate", lDtFromdate);
			lQuery.setDate("ToDate", lDtToDate);
			
			lLstData = lQuery.list();
		}catch(Exception e){
			gLogger.error("Exception in getAllDataForMonth:" + e, e);
		}
		
		return lLstData;
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
}
