package com.tcs.sgv.dcps.report;

import java.awt.Color;
import java.awt.Paint;
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
import org.jfree.data.Range;

import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.ChartReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.chart.ComparableKey;
import com.tcs.sgv.common.chart.data.ChartReportData;
import com.tcs.sgv.common.chart.data.ReportBarDataSet;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportColumnVO;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Jun 06, 2012
 */

public class UID_EID_EmpStatisticsReport extends DefaultReportDataFinder implements ReportDataFinder , ChartReportDataFinder
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
		List lLstAdminFieldDetails = null;
		List lLstDeptWiseDtls = null;
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		Map<String, String> lMapAdminWiseDetails_Total = new HashMap<String, String>();
		Map<String, String> lMapAdminWiseDetails_EID = new HashMap<String, String>();
		Map<String, String> lMapAdminWiseDetails_UID = new HashMap<String, String>();
		Map<String, String> lMapAdminWiseDetails_NotEntered = new HashMap<String, String>();
		Map<String, String> lMapFieldWiseDetails_Total = new HashMap<String, String>();
		Map<String, String> lMapFieldWiseDetails_UID = new HashMap<String, String>();
		Map<String, String> lMapFieldWiseDetails_EID = new HashMap<String, String>();
		Map<String, String> lMapFieldWiseDetails_NotEntered = new HashMap<String, String>();
		Map<String, String> lMapOfficeWiseDetails = new HashMap<String, String>();
		
		LoginDetails lObjLoginVO = null;
		Long lLngDeptAdmin = 100001l;
		Long lLngDeptField = 100011l;
		String lStrReportType = "";
		String lStrAdminDept = "";
		String lStrFieldDept = "";		
		String lStrAdminDeptName = "";
		String lStrFieldDeptName = "";
		String lStrBlank = "";
		new ReportsDAOImpl();		
		Object []lObj = null;
		Object []lObjCnt = null;
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
			
			if (lObjReport.getReportCode().equals("700091")) 
			{
				lStrAdminDept = lObjReport.getParameterValue("adminDept").toString();
				lStrFieldDept = lObjReport.getParameterValue("fieldDept").toString();				
				
				Date lObjDt = new Date();
				String lSBHeader = lObjReport.getReportName();
				
				if(lStrAdminDept.equals("") && lStrFieldDept.equals("")){
					lStrReportType = "Admin";
					lLstDeptWiseDtls = getAdminFieldDeptDtls(lLngDeptAdmin, null);						
					lSBHeader = lSBHeader + " As On "+ lObjSimpleDateFormat.format(lObjDt) + "\r\n (Administrative Department Wise)"; 	
					
					lLstAdminFieldDetails = getAllCountDataForEmployee_Admin();
					for(Integer lIntCnt = 0; lIntCnt < lLstAdminFieldDetails.size(); lIntCnt++)
					{
						lObjCnt = (Object[]) lLstAdminFieldDetails.get(lIntCnt);
						
						if(lObjCnt[2].toString().equals("Total")){
							if(lObjCnt[0] != null && lObjCnt[1] != null){
								lMapAdminWiseDetails_Total.put(lObjCnt[0].toString(), lObjCnt[1].toString());
							}
						}else if(lObjCnt[2].toString().equals("UID")){
							if(lObjCnt[0] != null && lObjCnt[1] != null){
								lMapAdminWiseDetails_UID.put(lObjCnt[0].toString(), lObjCnt[1].toString());
							}
						}else if(lObjCnt[2].toString().equals("EID")){
							if(lObjCnt[0] != null && lObjCnt[1] != null){
								lMapAdminWiseDetails_EID.put(lObjCnt[0].toString(), lObjCnt[1].toString());
							}
						}else if(lObjCnt[2].toString().equals("NOT Entered")){
							if(lObjCnt[0] != null && lObjCnt[1] != null){
								lMapAdminWiseDetails_NotEntered.put(lObjCnt[0].toString(), lObjCnt[1].toString());
							}
						}
					}
					
				}else if(!lStrAdminDept.equals("")){
					if(!lStrFieldDept.equals("")){
						lStrReportType = "Office";
						lLstDeptWiseDtls = getAllOffices(Long.parseLong(lStrAdminDept),Long.parseLong(lStrFieldDept));
						lStrFieldDeptName = getDepartmentName(Long.parseLong(lStrFieldDept));
						lStrAdminDeptName = getDepartmentName(Long.parseLong(lStrAdminDept));
						lSBHeader = "UID/EID Employee Statistics Report As On "+ lObjSimpleDateFormat.format(lObjDt)
						+ "\r\n (Office Wise)";
						lObjReport.setAdditionalHeader("Admin Department : "+lStrAdminDeptName + "\r\n Field Department : "+lStrFieldDeptName);
					}else{
						lStrReportType = "Field";
						lStrAdminDeptName = getDepartmentName(Long.parseLong(lStrAdminDept));
						lLstDeptWiseDtls = getAdminFieldDeptDtls(lLngDeptField, Long.parseLong(lStrAdminDept));						
						lSBHeader = "UID/EID Employee Statistics Report As On "+ lObjSimpleDateFormat.format(lObjDt)
									+ "\r\n (Field Department Wise)";
						lObjReport.setAdditionalHeader("Admin Department : "+lStrAdminDeptName);
						
						lLstAdminFieldDetails = getAllCountDataForEmployee_Field();
						for(Integer lIntCnt = 0; lIntCnt < lLstAdminFieldDetails.size(); lIntCnt++)
						{
							lObjCnt = (Object[]) lLstAdminFieldDetails.get(lIntCnt);
							
							if(lObjCnt[3].toString().equals("Total")){
								if(lObjCnt[1] != null && lObjCnt[2] != null){
									lMapFieldWiseDetails_Total.put(lObjCnt[1].toString(), lObjCnt[2].toString());
								}
							}else if(lObjCnt[3].toString().equals("UID")){
								if(lObjCnt[1] != null && lObjCnt[2] != null){
									lMapFieldWiseDetails_UID.put(lObjCnt[1].toString(), lObjCnt[2].toString());
								}
							}else if(lObjCnt[3].toString().equals("EID")){
								if(lObjCnt[1] != null && lObjCnt[2] != null){
									lMapFieldWiseDetails_EID.put(lObjCnt[1].toString(), lObjCnt[2].toString());
								}
							}else if(lObjCnt[3].toString().equals("NOT Entered")){
								if(lObjCnt[1] != null && lObjCnt[2] != null){
									lMapFieldWiseDetails_NotEntered.put(lObjCnt[1].toString(), lObjCnt[2].toString());
								}
							}
						}
					}
				}
				
				lObjReport.setReportName(lSBHeader);
							
				ArrayList rowList = new ArrayList();
				
				ReportColumnVO[] lArrReportColumnVO = lObjReport.getReportColumns();
				for(int lIntCnt = 0; lIntCnt < lArrReportColumnVO.length; lIntCnt++)
				{
					if(lArrReportColumnVO[lIntCnt].getColumnId() == 2){
						if(lStrReportType.equals("Field")){
							lArrReportColumnVO[lIntCnt].setColumnHeader("Field Department");
						}else if(lStrReportType.equals("Office")){
							lArrReportColumnVO[lIntCnt].setColumnHeader("Office Name");
						}
						break;
					}
				}
				
				
				Iterator IT = lLstDeptWiseDtls.iterator();
				Integer counter = 1;
				Long lLngTotalEmp = null;
				Long lLngTotalEid = null;
				Integer lIntPercentUid = null;
				Long lLngTotalUid = null;
				Integer lIntPercentEid = null;
				Long lLngNotEntered = null;
				Integer lIntNotEntered = null;
				
				while (IT.hasNext()) {
					lObj = (Object[]) IT.next();
					rowList = new ArrayList();
					
					rowList.add(counter);
					
					if (lObj[1] != null) {
						if(lStrReportType.equals("Admin")){
							rowList.add(new URLData(lObj[1],"ifms.htm?actionFlag=reportService&reportCode=700091&action=generateReport&adminDept="+lObj[0]+"&fieldDept="+lStrBlank));
						}else if(lStrReportType.equals("Field")){
							rowList.add(new URLData(lObj[1],"ifms.htm?actionFlag=reportService&reportCode=700091&action=generateReport&adminDept="+lStrAdminDept+"&fieldDept="+lObj[0]+"&office="+lStrBlank));
						}else if(lStrReportType.equals("Office")){
							rowList.add(new URLData(lObj[1],"ifms.htm?actionFlag=reportService&reportCode=700092&action=generateReport&adminDept="+lStrAdminDept
									+"&fieldDept="+lStrFieldDept+"&office="+lObj[0]));
						}
					}else {
						rowList.add("");
					}
					
					if(lStrReportType.equals("Admin")){
						lLngTotalEmp = lMapAdminWiseDetails_Total.get(lObj[0].toString()) != null ? Long.parseLong(lMapAdminWiseDetails_Total.get(lObj[0].toString())) : null;
						//lLngTotalEmp = Long.parseLong(countAllEmployeeForAdmin(Long.parseLong(lObj[0].toString()),"").toString());
					}else if(lStrReportType.equals("Field")){						
						lLngTotalEmp = lMapFieldWiseDetails_Total.get(lObj[0].toString()) != null ? Long.parseLong(lMapFieldWiseDetails_Total.get(lObj[0].toString())) : null;
						//lLngTotalEmp = Long.parseLong(countAllEmployeeForField(Long.parseLong(lStrAdminDept),Long.parseLong(lObj[0].toString()),"").toString());
					}else if(lStrReportType.equals("Office")){
						lLngTotalEmp = Long.parseLong(countAllEmployeeForOffice(lObj[0].toString(),"",Long.parseLong(lStrFieldDept),Long.parseLong(lStrAdminDept)).toString());
					}
					
					if (lLngTotalEmp != null) {
						rowList.add(lLngTotalEmp);
					} else {
						rowList.add(0l);
					}
					
					if(lStrReportType.equals("Admin")){
						lLngTotalUid = lMapAdminWiseDetails_UID.get(lObj[0].toString()) != null ? Long.parseLong(lMapAdminWiseDetails_UID.get(lObj[0].toString())) : null;
						//lLngTotalUid = Long.parseLong(countAllEmployeeForAdmin(Long.parseLong(lObj[0].toString()),"UID").toString());
					}else if(lStrReportType.equals("Field")){
						lLngTotalUid = lMapFieldWiseDetails_UID.get(lObj[0].toString()) != null ? Long.parseLong(lMapFieldWiseDetails_UID.get(lObj[0].toString())) : null;
						//lLngTotalUid = Long.parseLong(countAllEmployeeForField(Long.parseLong(lStrAdminDept),Long.parseLong(lObj[0].toString()),"UID").toString());
					}else if(lStrReportType.equals("Office")){
						lLngTotalUid = Long.parseLong(countAllEmployeeForOffice(lObj[0].toString(),"UID",Long.parseLong(lStrFieldDept),Long.parseLong(lStrAdminDept)).toString());
					}
					
					if (lLngTotalUid != null) {
						rowList.add(lLngTotalUid);
					} else{
						rowList.add(0l);
					}
					
					if(lLngTotalUid != null && lLngTotalEmp != null){
						lIntPercentUid = Math.round(((float)lLngTotalUid * 100) / (float)lLngTotalEmp) ;
					}else{
						lIntPercentUid = 0;
					}
					
					if (lIntPercentUid != null) {
						rowList.add(lIntPercentUid);
					} else{
						rowList.add(0);
					}
					
					if(lStrReportType.equals("Admin")){
						lLngTotalEid = lMapAdminWiseDetails_EID.get(lObj[0].toString()) != null ? Long.parseLong(lMapAdminWiseDetails_EID.get(lObj[0].toString())) : null;
						//lLngTotalEid = Long.parseLong(countAllEmployeeForAdmin(Long.parseLong(lObj[0].toString()),"EID").toString());
					}else if(lStrReportType.equals("Field")){
						lLngTotalEid = lMapFieldWiseDetails_EID.get(lObj[0].toString()) != null ? Long.parseLong(lMapFieldWiseDetails_EID.get(lObj[0].toString())) : null;
						//lLngTotalEid = Long.parseLong(countAllEmployeeForField(Long.parseLong(lStrAdminDept),Long.parseLong(lObj[0].toString()),"EID").toString());
					}else if(lStrReportType.equals("Office")){
						lLngTotalEid = Long.parseLong(countAllEmployeeForOffice(lObj[0].toString(),"EID",Long.parseLong(lStrFieldDept),Long.parseLong(lStrAdminDept)).toString());
					}
					
					if (lLngTotalEid != null) {						
						rowList.add(lLngTotalEid);
					} else{
						rowList.add(0l);						
					}
					
					if(lLngTotalEid != null && lLngTotalEmp != null){
						lIntPercentEid = Math.round(((float)lLngTotalEid * 100) / (float)lLngTotalEmp) ;
					}else{
						lIntPercentEid = 0;
					}
					
					if (lIntPercentEid != null) {
						rowList.add(lIntPercentEid);
					} else{
						rowList.add(0);
					}
					
					if(lStrReportType.equals("Admin")){
						lLngNotEntered = lMapAdminWiseDetails_NotEntered.get(lObj[0].toString()) != null ? Long.parseLong(lMapAdminWiseDetails_NotEntered.get(lObj[0].toString())) : null;
						//lLngNotEntered = Long.parseLong(countAllEmployeeForAdmin(Long.parseLong(lObj[0].toString()),"NotEntered").toString());
					}else if(lStrReportType.equals("Field")){
						lLngNotEntered = lMapFieldWiseDetails_NotEntered.get(lObj[0].toString()) != null ? Long.parseLong(lMapFieldWiseDetails_NotEntered.get(lObj[0].toString())) : null;
						//lLngNotEntered = Long.parseLong(countAllEmployeeForField(Long.parseLong(lStrAdminDept),Long.parseLong(lObj[0].toString()),"NotEntered").toString());
					}else if(lStrReportType.equals("Office")){
						lLngNotEntered = Long.parseLong(countAllEmployeeForOffice(lObj[0].toString(),"NotEntered",Long.parseLong(lStrFieldDept), Long.parseLong(lStrAdminDept)).toString());
					}
					
					if (lLngNotEntered != null) {
						rowList.add(lLngNotEntered);
					} else{
						rowList.add(0l);
					}
					
					if(lLngNotEntered != null && lLngTotalEmp != null){
						lIntNotEntered = Math.round(((float)lLngNotEntered * 100) / (float)lLngTotalEmp) ;
					}else{
						lIntNotEntered = 0;
					}
					
					if (lIntNotEntered != null) {
						rowList.add(lIntNotEntered);
					} else{
						rowList.add(0);
					}
					
					rowList.add(Long.parseLong(lObj[0].toString()));
					
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
	
	public List getAdminFieldDeptDtls(Long lLngDeptId, Long lLngAdminId)
	{
		List lLstResData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT LOC_ID, LOC_NAME ");
			lSBQuery.append("FROM CMN_LOCATION_MST ");
			lSBQuery.append("WHERE DEPARTMENT_ID = :deptId ");
			if(lLngAdminId != null){
				lSBQuery.append("AND PARENT_LOC_ID = :adminId ");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("deptId", lLngDeptId);
			if(lLngAdminId != null){
				lQuery.setLong("adminId", lLngAdminId);
			}
			lLstResData = lQuery.list();
		}catch (Exception e) {
			gLogger.error("Exception in getAdminFieldDeptDtls:" + e, e);
		}
		return lLstResData;
	}
	
	public List getAllOffices(Long lLngAdminDept, Long lLngFieldDeptId)
	{
		List lLstResData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DDO_CODE, DDO_OFFICE FROM ORG_DDO_MST ");
			lSBQuery.append("WHERE DEPT_LOC_CODE =:adminDept AND HOD_LOC_CODE = :fieldDept ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("adminDept", lLngAdminDept);
			lQuery.setLong("fieldDept", lLngFieldDeptId);
			lLstResData = lQuery.list();
		}catch(Exception e){
			gLogger.error("Exception in getAllOffices:" + e, e);
		}
		return lLstResData;
	}
	
	public Integer countAllEmployeeForAdmin(Long lLngDeptId, String lStrEidUid)
	{
		List lLstResData = null;
		Integer lIntEmpCnt = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT COUNT(ME.DCPS_EMP_ID) FROM MST_DCPS_EMP ME JOIN ORG_DDO_MST ODM ");
			lSBQuery.append("ON ODM.DDO_CODE = ME.DDO_CODE ");
			lSBQuery.append("WHERE ODM.DEPT_LOC_CODE =:adminDept ");			
			if(lStrEidUid.equals("EID")){
				lSBQuery.append("AND ME.EID_NO is not null AND ME.EID_NO != '0'");
			}else if(lStrEidUid.equals("UID")){
				lSBQuery.append("AND ME.UID_NO is not null AND ME.UID_NO != '0'");
			}else if(lStrEidUid.equals("NotEntered")){
				lSBQuery.append("AND (ME.EID_NO is null OR ME.EID_NO = '0')AND (ME.UID_NO is null OR ME.UID_NO = '0') ");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("adminDept", lLngDeptId);
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lIntEmpCnt = (Integer) lLstResData.get(0);
			}
		}catch(Exception e){
			gLogger.error("Exception in countAllEmployeeForAdmin:" + e, e);
		}
		return lIntEmpCnt;
	}
	
	public Integer countAllEmployeeForField(Long lLngAdmDeptId, Long lLngFldDeptId, String lStrEidUid)
	{
		List lLstResData = null;
		Integer lIntEmpCnt = 0;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT COUNT(ME.DCPS_EMP_ID) FROM MST_DCPS_EMP ME JOIN ORG_DDO_MST ODM ");
			lSBQuery.append("ON ODM.DDO_CODE = ME.DDO_CODE ");
			lSBQuery.append("WHERE ODM.DEPT_LOC_CODE =:adminDept AND ODM.HOD_LOC_CODE =:fieldDept ");
			if(lStrEidUid.equals("EID")){
				lSBQuery.append("AND ME.EID_NO is not null AND ME.EID_NO != '0'");
			}else if(lStrEidUid.equals("UID")){
				lSBQuery.append("AND ME.UID_NO is not null AND ME.UID_NO != '0'");
			}else if(lStrEidUid.equals("NotEntered")){
				lSBQuery.append("AND (ME.EID_NO is null OR ME.EID_NO = '0') AND (ME.UID_NO is null OR ME.UID_NO = '0') ");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("adminDept", lLngAdmDeptId);
			lQuery.setLong("fieldDept", lLngFldDeptId);
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lIntEmpCnt = (Integer) lLstResData.get(0);
			}
		}catch(Exception e){
			gLogger.error("Exception in countAllEmployeeForField:" + e, e);
		}
		return lIntEmpCnt;
	}
	
	public Integer countAllEmployeeForOffice(String lStrDdoCode, String lStrEidUid, Long lLngFldDept, Long lLngAdmDept)
	{
		List lLstResData = null;
		Integer lIntEmpCnt = 0;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT COUNT(ME.DCPS_EMP_ID) FROM MST_DCPS_EMP ME JOIN ORG_DDO_MST ODM ");
			lSBQuery.append("ON ODM.DDO_CODE = ME.DDO_CODE ");
			lSBQuery.append("WHERE ODM.DEPT_LOC_CODE =:adminDept AND ODM.HOD_LOC_CODE =:fieldDept AND ODM.DDO_CODE =:ddoCode ");
			if(lStrEidUid.equals("EID")){
				lSBQuery.append("AND EID_NO is not null AND EID_NO != '0'");
			}else if(lStrEidUid.equals("UID")){
				lSBQuery.append("AND UID_NO is not null AND UID_NO != '0'");
			}else if(lStrEidUid.equals("NotEntered")){
				lSBQuery.append("AND (EID_NO is null OR EID_NO = '0') AND (UID_NO is null OR UID_NO = '0') ");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lQuery.setLong("adminDept",lLngAdmDept);
			lQuery.setLong("fieldDept",lLngFldDept);
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lIntEmpCnt = (Integer) lLstResData.get(0);
			}
		}catch(Exception e){
			gLogger.error("Exception in countAllEmployeeForOffice:" + e, e);
		}
		return lIntEmpCnt;
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
	
	public static StyleVO[] getStyleVOArray(HashMap<Integer,String> valueMapForStyleVO)
	{
		StyleVO[] styleVOArray = null;
		styleVOArray = new StyleVO[valueMapForStyleVO.size()];
		Integer index = 0;
		for (Integer  idKey: valueMapForStyleVO.keySet()) 
		{
			styleVOArray[index] = new StyleVO();
			styleVOArray[index].setStyleId(idKey);
			styleVOArray[index].setStyleValue(valueMapForStyleVO.get(idKey));
			index++;
		}
		return styleVOArray;
	}
	
	public ChartReportData getChartData(ReportVO lObjReport, Object criteria) throws ReportException 
	{		
		ChartReportData lObjChartReportData = null;
		String lStrMaxRange = "";
		Double lDblTotalEmp = null;
		Double lDblTotalEID = null;
		Double lDblTotalUID = null;
		Integer lIntFirstDegit = null;
		
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		
		try{
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
			
			if(lObjReport.getReportCode().equals("700093"))
			{
				ReportBarDataSet barDataSet = new ReportBarDataSet();
				
				lDblTotalEmp = Double.parseDouble(countAllEmployee("").toString());
				lDblTotalEID = Double.parseDouble(countAllEmployee("EID").toString());
				lDblTotalUID = Double.parseDouble(countAllEmployee("UID").toString());
				
				lIntFirstDegit = Integer.parseInt(lDblTotalEmp.toString().subSequence(0, 1).toString());
				lIntFirstDegit++;
				lStrMaxRange = lStrMaxRange + lIntFirstDegit.toString();
				for(Integer lIntCnt = 0; lIntCnt<lDblTotalEmp.toString().trim().length() - 3; lIntCnt++)
				{
					lStrMaxRange += "0";
				}
				
				
				barDataSet.addValue(lDblTotalEmp,new ComparableKey("Total-X","Total Employees") , new ComparableKey("Total-Y","Total"));
				barDataSet.addValue(lDblTotalUID,new ComparableKey("UID-X","UID Entered Employees") , new ComparableKey("UID-Y","UID"));
				barDataSet.addValue(lDblTotalEID,new ComparableKey("EID-X","EID Entered Employees") , new ComparableKey("EID-Y","EID"));
				
				float arr[] = Color.RGBtoHSB(193,255,193, null);//light blue
				barDataSet.addColors(new Paint[]{Color.getHSBColor(arr[0], arr[1], arr[2])});
		
				barDataSet.setValueAxisRange(new Range(0, Double.parseDouble(lStrMaxRange)));
				
				HashMap<Integer,String> valueMapForStyleVO = new HashMap<Integer,String>();
				valueMapForStyleVO.put(21, "Employees : ");// x axis label
				valueMapForStyleVO.put(22, "Count");// Y axis label
				valueMapForStyleVO.put(12, "Yes");// tooltip
				valueMapForStyleVO.put(49, "Yes");// value on bar 
				valueMapForStyleVO.put(54, "0");// value on bar with angle				
				valueMapForStyleVO.put(18, "520");// height
				valueMapForStyleVO.put(17, "590");// width
				valueMapForStyleVO.put(73, "3");
				valueMapForStyleVO.put(78, "ifms.htm?actionFlag=reportService&reportCode=700091&action=generateReport");
				valueMapForStyleVO.put(13, "Yes");// is url required
				valueMapForStyleVO.put(26, "ifms.htm?actionFlag=getHomePage");
				
				lObjReport.setStyleList(getStyleVOArray(valueMapForStyleVO));
				
				return barDataSet;
			}
		}catch(Exception e){
			gLogger.error("Exception in getChartData:" + e, e);
		}
		return lObjChartReportData;
	}
	
	public Integer countAllEmployee(String lStrType)
	{
		List lLstResData = null;
		Integer lIntEmpCnt = null;
		Long lLngDepartmentId = 100001l;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT COUNT(ME.DCPS_EMP_ID) FROM MST_DCPS_EMP ME JOIN ORG_DDO_MST ODM ON ODM.DDO_CODE = ME.DDO_CODE ");
			lSBQuery.append("JOIN CMN_LOCATION_MST CLM ON ODM.DEPT_LOC_CODE = CLM.LOC_ID WHERE CLM.DEPARTMENT_ID = :deptId ");
			if(lStrType.trim().equals("UID")){
				lSBQuery.append("AND ME.UID_NO is not null AND ME.UID_NO != '0' ");
			}else if(lStrType.trim().equals("EID")){
				lSBQuery.append("AND ME.EID_NO is not null AND ME.EID_NO != '0' ");
				lSBQuery.append(" AND ( ME.UID_NO is null or ME.UID_NO = '0') ");
			}
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());	
			lQuery.setLong("deptId", lLngDepartmentId);
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lIntEmpCnt = (Integer) lLstResData.get(0);
			}
		}catch(Exception e){
			gLogger.error("Exception in countAllEmployee:" + e, e);
		}
		return lIntEmpCnt;
	}
	
	public List getAllCountDataForEmployee_Admin()
	{
		List lLstResData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ODM.DEPT_LOC_CODE,COUNT(ME.DCPS_EMP_ID),'Total' ");
			lSBQuery.append("FROM MST_DCPS_EMP ME "); 
			lSBQuery.append("JOIN ORG_DDO_MST ODM ON ODM.DDO_CODE = ME.DDO_CODE ");
			lSBQuery.append("group by ODM.DEPT_LOC_CODE ");
			lSBQuery.append("union all ");
			lSBQuery.append("SELECT ODM.DEPT_LOC_CODE,COUNT(ME.DCPS_EMP_ID),'NOT Entered' ");
			lSBQuery.append("FROM MST_DCPS_EMP ME "); 
			lSBQuery.append("JOIN ORG_DDO_MST ODM ON ODM.DDO_CODE = ME.DDO_CODE and (ME.EID_NO is null OR ME.EID_NO = '0') AND (ME.UID_NO is null OR ME.UID_NO = '0') ");
			lSBQuery.append("group by ODM.DEPT_LOC_CODE ");
			lSBQuery.append("union all ");
			lSBQuery.append("SELECT ODM.DEPT_LOC_CODE,COUNT(ME.DCPS_EMP_ID),'EID' ");
			lSBQuery.append("FROM MST_DCPS_EMP ME  ");
			lSBQuery.append("JOIN ORG_DDO_MST ODM ON ODM.DDO_CODE = ME.DDO_CODE AND (ME.EID_NO is not null AND ME.EID_NO != '0') AND ( ME.UID_NO is null or ME.UID_NO = '0') ");
			lSBQuery.append("group by ODM.DEPT_LOC_CODE ");
			lSBQuery.append("union all ");
			lSBQuery.append("SELECT ODM.DEPT_LOC_CODE,COUNT(ME.DCPS_EMP_ID),'UID' ");
			lSBQuery.append("FROM MST_DCPS_EMP ME  ");
			lSBQuery.append("JOIN ORG_DDO_MST ODM ON ODM.DDO_CODE = ME.DDO_CODE AND (ME.UID_NO is not null AND ME.UID_NO != '0') ");
			lSBQuery.append("group by ODM.DEPT_LOC_CODE ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
		}catch(Exception e){
			gLogger.error("Exception in getAllCountDataForEmployee:" + e, e);
		}
		return lLstResData;
	}
	
	public List getAllCountDataForEmployee_Field()
	{
		List lLstResData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ODM.DEPT_LOC_CODE,ODM.HOD_LOC_CODE,COUNT(ME.DCPS_EMP_ID),'Total' ");
			lSBQuery.append("FROM MST_DCPS_EMP ME "); 
			lSBQuery.append("JOIN ORG_DDO_MST ODM ON ODM.DDO_CODE = ME.DDO_CODE ");
			lSBQuery.append("group by ODM.DEPT_LOC_CODE,ODM.HOD_LOC_CODE ");
			lSBQuery.append("union all ");
			lSBQuery.append("SELECT ODM.DEPT_LOC_CODE,ODM.HOD_LOC_CODE,COUNT(ME.DCPS_EMP_ID),'NOT Entered' ");
			lSBQuery.append("FROM MST_DCPS_EMP ME "); 
			lSBQuery.append("JOIN ORG_DDO_MST ODM ON ODM.DDO_CODE = ME.DDO_CODE and (ME.EID_NO is null OR ME.EID_NO = '0') AND (ME.UID_NO is null OR ME.UID_NO = '0') ");
			lSBQuery.append("group by ODM.DEPT_LOC_CODE,ODM.HOD_LOC_CODE ");
			lSBQuery.append("union all ");
			lSBQuery.append("SELECT ODM.DEPT_LOC_CODE,ODM.HOD_LOC_CODE,COUNT(ME.DCPS_EMP_ID),'EID' ");
			lSBQuery.append("FROM MST_DCPS_EMP ME  ");
			lSBQuery.append("JOIN ORG_DDO_MST ODM ON ODM.DDO_CODE = ME.DDO_CODE AND (ME.EID_NO is not null AND ME.EID_NO != '0') AND ( ME.UID_NO is null or ME.UID_NO = '0') ");
			lSBQuery.append("group by ODM.DEPT_LOC_CODE,ODM.HOD_LOC_CODE ");
			lSBQuery.append("union all ");
			lSBQuery.append("SELECT ODM.DEPT_LOC_CODE,ODM.HOD_LOC_CODE,COUNT(ME.DCPS_EMP_ID),'UID' ");
			lSBQuery.append("FROM MST_DCPS_EMP ME  ");
			lSBQuery.append("JOIN ORG_DDO_MST ODM ON ODM.DDO_CODE = ME.DDO_CODE AND (ME.UID_NO is not null AND ME.UID_NO != '0') ");
			lSBQuery.append("group by ODM.DEPT_LOC_CODE,ODM.HOD_LOC_CODE ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			
		}catch(Exception e){
			gLogger.error("Exception in getAllCountDataForEmployee:" + e, e);
		}
		return lLstResData;
	}
}

