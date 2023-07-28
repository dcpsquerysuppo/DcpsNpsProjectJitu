package com.tcs.sgv.dcps.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.acl.login.valueobject.LoginDetails;
import com.tcs.sgv.common.business.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportAttributeVO;
import com.tcs.sgv.common.valuebeans.reports.ReportTemplate;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.core.service.ServiceLocator;


 public class Gender_wiseReport extends DefaultReportDataFinder implements ReportDataFinder {
	 
	 
	private static Logger logger = Logger.getLogger(Gender_wiseReport.class);
	
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Map lMapSeriesHeadCode = null;
	Map requestAttributes = null;
	Session ghibSession = null;

	public Collection findReportData(ReportVO report, Object criteria)	throws ReportException {
		List DataList = new ArrayList();
		Map lMapRequestAttributes = null;
		Map lMapSessionAttributes = null;
		LoginDetails lObjLoginVO = null;
		String lStrAdminDept = null;
		String lStrFieldDept = null;
		String lStrDDOcode = null;
		
		
		
		String lStrAdminDeptName = null;
		String lStrFieldDeptName = null;
		
	    
		
		new ReportsDAOImpl();
		
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
			
			
			StyleVO[] boldStyleVO = new StyleVO[1];
			boldStyleVO[0] = new StyleVO();
			boldStyleVO[0].setStyleId(IReportConstants.STYLE_FONT_WEIGHT);
			boldStyleVO[0].setStyleValue(IReportConstants.VALUE_FONT_WEIGHT_BOLDER);
			
			
			
			  SimpleDateFormat objsimpledatefrm = new SimpleDateFormat("dd/MM/yyyy");
			
			if (report.getReportCode().equals("700107")|| report.getReportCode().equals("700113") || report.getReportCode().equals("700114")) {
				logger.info("in report 700107  ***********");
				
				lStrAdminDept = report.getParameterValue("adminDept").toString();
				logger.info("loc id of AdminDept;;;;;;;;;"+lStrAdminDept);
				
				lStrFieldDept = report.getParameterValue("fieldDept").toString();
				logger.info("loc id of fieldDept;;;;;;;;;"+lStrFieldDept);
				
				lStrDDOcode = report.getParameterValue("ddocode").toString();
				logger.info("loc id of DDocode;;;;;;;;;"+lStrDDOcode);
				
                  Date date = new Date();				    
                 // String strheader = null;
                
                  
				String srno = null;
				int male = 0;
				String adminname = null;
				String adminid = null;
				String fieldname = null;
				String fieldid = null;
				String ddocode = null;
				String DDOname = null;
				String lStrBlank = "";
				String strname =null;
				String strsevarthid = null;
				String strGender = null;
				
				int female = 0;
				int transgender = 0;
				int total = 0;
				float perofFemale = 0;
				float roundperoffemale = 0;
				int TotalFemale = 0;
				int TotalMale = 0;
				int mainTotal = 0;
				int Totaltransgen = 0;
				float perTotal = 0 ;
				List lLstFinal = null;
				
				  
				String strheader = null;
				
				if(lStrAdminDept.equals("")&& lStrFieldDept.equals(""))
				{
					
					
					  strheader = report.getReportName();
					
							
					strheader = strheader + "As On" + objsimpledatefrm.format(date) + "\n (Administrative Department Wise)"; 
					
				    lLstFinal = getAdminDeptDtls();				 
				 logger.info("Lists value  -----------"+lLstFinal.toString());
							  
									
					report.setReportName(strheader);
				   
				}
			 if (!lStrAdminDept.equals(""))
				{
				 				  strheader = report.getReportName();
					
					strheader = "Employee Statistics - Gender-wise " + "As On" + objsimpledatefrm.format(date) + "\n (Field Department Wise)"; 	
					
					 lLstFinal = getfieldDeptList(Long.parseLong(lStrAdminDept));					 
					 logger.info("Field Name -----------"+lLstFinal.toString());	
					 
					 lStrAdminDeptName = getDepartmentName(Long.parseLong(lStrAdminDept));
					 logger.info("ADmin Name -----------"+lLstFinal.toString());	
					 
					 report.setAdditionalHeader("Admin Department : "+lStrAdminDeptName);
									
					 report.setReportName(strheader);
				}
			 
			 if (!lStrFieldDept.equals(""))
				{
				
				  strheader = report.getReportName();
					strheader = "Employee Statistics - Gender-wise " + "As On" + objsimpledatefrm.format(date) + "\n (DDO Wise)"; 	
					
					 lLstFinal = getDDOListbyField(Long.parseLong(lStrFieldDept));					 
					 logger.info("DDO Name -----------"+lLstFinal.toString());		
					 
					 lStrFieldDeptName = getDepartmentName(Long.parseLong(lStrFieldDept));
					 logger.info("Field  Name -----------"+lStrFieldDeptName);	
					lStrAdminDeptName = getDepartmentName(Long.parseLong(lStrAdminDept));
					 logger.info("Admin Name -----------"+lStrAdminDeptName);
					 
					 report.setAdditionalHeader("Admin Department : "+lStrAdminDeptName + "\r\n Field Department : "+lStrFieldDeptName);
									
					 report.setReportName(strheader);
				}
				
					
				 
			
				List dataListForTable = new ArrayList();

				if (lLstFinal != null && !lLstFinal.isEmpty()) {
					int count = 0;

					for (Iterator it = lLstFinal.iterator(); it.hasNext();)

					{
						count++;
						dataListForTable = new ArrayList();
						Object[] lObj = (Object[]) it.next();
						if(lStrAdminDept.equals("")&& lStrFieldDept.equals(""))
						{
						adminid = (lObj[0] != null) ? lObj[0].toString(): "";
						logger.info("Admin Id is  ***********" + adminid);						
						adminname = (lObj[1] != null) ? lObj[1].toString(): "";
						logger.info("Admin name is  ***********" + adminname);
						}
						 if (!lStrAdminDept.equals("")) 
						 {
						
									 fieldid = (lObj[0] != null) ? lObj[0].toString(): "";
								     logger.info("Admin Id is  ***********" + fieldid);
						
									 fieldname = (lObj[1] != null) ? lObj[1].toString(): "";
									logger.info("Admin name is  ***********" + fieldname);
						
						
						 }
						 
						 if (!lStrFieldDept.equals(""))
						 {
							 
							 ddocode = (lObj[0] != null) ? lObj[0].toString(): "";
						     logger.info("Admin Id is  ***********" + ddocode);
				
						     DDOname = (lObj[1] != null) ? lObj[1].toString(): "";
							logger.info("Admin name is  ***********" + DDOname);
							 
						 }
						 
						

						male = (lObj[2] != null) ? Integer.parseInt(lObj[2].toString()) : new Integer(0);
						logger.info(" male count  ***********"+ male);
						
						TotalMale = TotalMale + male;
						logger.info("total male count  ***********"+ TotalMale);
						
						female = (lObj[3] != null) ? Integer.parseInt(lObj[3].toString()) : new Integer(0);						
						logger.info(" female count  ***********"+ female);
						
						TotalFemale = TotalFemale + female;
						logger.info(" Total female count  ***********"+ TotalFemale);
						
						transgender = (lObj[4] != null) ? Integer.parseInt(lObj[4].toString()) : new Integer(0);						
						logger.info("  Trans Gender count  ***********"+ transgender);
						
						Totaltransgen = Totaltransgen +  transgender;
						logger.info(" Total of Trans Gender count  ***********"+ Totaltransgen);
						
						
						total = (lObj[5] != null) ? Integer.parseInt(lObj[5].toString()) : new Integer(0);						
						logger.info(" total count   ***********"+ total);

						mainTotal = mainTotal+ total;
						logger.info(" main total count   ***********"+ mainTotal);
						
						perofFemale = (float)female*100/total;
						logger.info(" per of female ***********"+ perofFemale);
						
						
						
						/*roundperoffemale = Float.parseFloat(new DecimalFormat("##.##").format(perofFemale));					
						
						logger.info(" per of female ***********"+ roundperoffemale);*/

						perTotal = (float)TotalFemale*100/mainTotal; 
						logger.info("  Total per of female   ***********"+perTotal);
						
						dataListForTable.add(count);
						
						if (!lStrFieldDept.equals(""))
						{
							
							dataListForTable.add(new URLData(ddocode,"ifms.htm?actionFlag=reportService&reportCode=700111&action=generateReport&ddocode="+ddocode + "&fieldDept="+lStrFieldDept ));
						}
						
						else if (!lStrAdminDept.equals(""))
						{
							dataListForTable.add(new URLData(fieldname,"ifms.htm?actionFlag=reportService&reportCode=700114&action=generateReport&fieldDept="+fieldid +"&adminDept="+lStrAdminDept ));			
											
						}
						else
						{
							dataListForTable.add(new URLData(adminname,"ifms.htm?actionFlag=reportService&reportCode=700113&action=generateReport&adminDept="+adminid ));
							
						}
						dataListForTable.add(male);					
						dataListForTable.add(female);
						dataListForTable.add(transgender);
						dataListForTable.add(total);
						dataListForTable.add(perofFemale);
						DataList.add(dataListForTable);

					}
					
					if (!lStrAdminDept.equals("")||!lStrFieldDept.equals("")) 
					 {
					  ArrayList row2 = new ArrayList();
						 
						 StyledData dataStyle1 = new StyledData();
						 dataStyle1 =  new StyledData();
						 dataStyle1.setStyles(boldStyleVO);
						 dataStyle1.setData("TOTAL");
						 row2.add(""); 
						 row2.add(dataStyle1);
						 
						 dataStyle1 = new StyledData();
						 dataStyle1 =  new StyledData();
						 dataStyle1.setStyles(boldStyleVO);
						 dataStyle1.setData(TotalMale);
						 row2.add(dataStyle1);
						 
						 dataStyle1 = new StyledData();
						 dataStyle1 =  new StyledData();
						 dataStyle1.setStyles(boldStyleVO);
						 dataStyle1.setData(TotalFemale);
						 row2.add(dataStyle1);
						 
						 dataStyle1 = new StyledData();
						 dataStyle1 =  new StyledData();
						 dataStyle1.setStyles(boldStyleVO);
						 dataStyle1.setData(Totaltransgen);
						 row2.add(dataStyle1);
						 
						 dataStyle1 = new StyledData();
						 dataStyle1 =  new StyledData();
						 dataStyle1.setStyles(boldStyleVO);
						 dataStyle1.setData(mainTotal);
						 row2.add(dataStyle1);
						 
						 dataStyle1 = new StyledData();
						 dataStyle1 =  new StyledData();
						 dataStyle1.setStyles(boldStyleVO);
						 dataStyle1.setData(perTotal);
						 row2.add(dataStyle1);
						 DataList.add(row2);
						 
				}
				}
				
				
	}
	
			 if (report.getReportCode().equals("700111"))
			 {
				 
				 
					lStrDDOcode = report.getParameterValue("ddocode").toString();
					logger.info("loc id of DDocode;;;;;;;;;"+lStrDDOcode);
					
					lStrFieldDept = report.getParameterValue("fieldDept").toString();
					logger.info("loc id of fieldDept;;;;;;;;;"+lStrFieldDept);
					
					
				       Date date = new Date();				    
	                   String strheader = null;	                  
	                   List lLstFinal = null;
	                   
	              	 lStrFieldDeptName = getDepartmentName(Long.parseLong(lStrFieldDept));
					 logger.info("Field  Name -----------"+lStrFieldDeptName);
				 
	                   strheader = report.getReportName();
			      strheader = strheader + "As On" + objsimpledatefrm.format(date);
			      report.setReportName(strheader);
			      
			      report.setAdditionalHeader("Field Department : "+ lStrFieldDeptName + "\r\n DDO Code: "+lStrDDOcode);
			 
			      lLstFinal = getEmployeebyDDO(Long.parseLong(lStrDDOcode));
			 
			       List dataListForTable = new ArrayList();
			     
			        String strname =null;
					String strsevarthid = null;
					String strGender = null;

				if (lLstFinal != null && !lLstFinal.isEmpty()) {
					int count = 0;

					for (Iterator it = lLstFinal.iterator(); it.hasNext();)

					{
						count++;
						dataListForTable = new ArrayList();
						Object[] lObj = (Object[]) it.next();
						
						strname = (lObj[0] != null) ? lObj[0].toString(): "";
						logger.info("name is  ***********" + strname);	
						strsevarthid = (lObj[1] != null) ? lObj[1].toString(): "";
						logger.info("sevarth Id is  ***********" + strsevarthid);	
						strGender = (lObj[2] != null) ? lObj[2].toString(): "";
						logger.info("gender Id is  ***********" + strGender);
						
						dataListForTable.add(count);
						dataListForTable.add(strname);
						dataListForTable.add(strsevarthid);
						dataListForTable.add(strGender);
						DataList.add(dataListForTable);
						
						
						}
					}
				
				   /*  ArrayList row2 = new ArrayList();
				 
					 StyledData dataStyle1 = new StyledData();
					 dataStyle1 =  new StyledData();
					 dataStyle1.setStyles(boldStyleVO);
					 dataStyle1.setData("TOTAL");
					 row2.add(""); 
					 row2.add(dataStyle1);
					 
					 dataStyle1 = new StyledData();
					 dataStyle1 =  new StyledData();
					 dataStyle1.setStyles(boldStyleVO);
					 dataStyle1.setData(TotalMale);
					 row2.add(dataStyle1);*/
				
				
		 }
	
			
			
		} catch (Exception e) {
			logger.error("Error in Gender_waiseReport " + e.getMessage());
			logger.error("Printing StackTrace");
			e.printStackTrace();
		}

		return DataList;

	}

	public List getEmployeebyDDO(Long lStrDDOcode)
	{
		List lLstResData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT em.EMP_NAME,em.SEVARTH_ID,em.GENDER   FROM  mst_dcps_emp em ");
			lSBQuery.append(" INNER JOIN org_ddo_mst ddo on em.DDO_CODE = ddo.DDO_CODE  ");
			lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.LOC_ID = ddo.DEPT_LOC_CODE ");
				lSBQuery.append(" INNER JOIN CMN_LOCATION_MST fld on fld.LOC_ID = ddo.HOD_LOC_CODE ");
				lSBQuery.append(" where em.FORM_STATUS =1 and em.REG_STATUS in (1,2) and ddo.DDO_CODE <> '1111222222' and  ddo.DDO_CODE=:ddocode ");
				
			Session gbsSession = serviceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
			SQLQuery lQuery = gbsSession.createSQLQuery(lSBQuery.toString());
				
				lQuery.setLong("ddocode", lStrDDOcode);		
			lLstResData = lQuery.list();
			
		}catch (Exception e) {
			logger.error("Exception in getAdminFieldDeptDtls:" + e, e);
		}
		return lLstResData;
	}
	
	private List getAdminDeptDtls() {
		
		List Lstadmin = null;
		
		try
		{
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" SELECT ocmn.LOC_ID,ocmn.LOC_NAME,  (select count(ORG_EMP_MST_ID)  FROM mst_dcps_emp emp inner join ORG_DDO_MST ddo on emp.DDO_CODE = ddo.DDO_CODE ");
		lSBQuery.append(" inner join CMN_LOCATION_MST cmn on  ddo.DEPT_LOC_CODE=cmn.LOC_ID  where ddo.ACTIVATE_FLAG = 1  and emp.REG_STATUS in (1,2) and emp.GENDER='M' and ddo.DDO_CODE <> '1111222222' and cmn.LOC_ID =ocmn.LOC_ID) MALE_COUNT ");
		lSBQuery.append("  ,(select count(ORG_EMP_MST_ID)  FROM mst_dcps_emp emp  inner join ORG_DDO_MST ddo on emp.DDO_CODE = ddo.DDO_CODE ");
		lSBQuery.append(" inner join CMN_LOCATION_MST cmn on  ddo.DEPT_LOC_CODE=cmn.LOC_ID  where ddo.ACTIVATE_FLAG = 1  and emp.REG_STATUS in (1,2) and emp.GENDER='F' and ddo.DDO_CODE <> '1111222222' and cmn.LOC_ID =ocmn.LOC_ID) FEMALE_COUNT ");
		lSBQuery.append("   ,(select count(ORG_EMP_MST_ID)  FROM mst_dcps_emp emp  inner join ORG_DDO_MST ddo on emp.DDO_CODE = ddo.DDO_CODE  ");
		lSBQuery.append(" inner join CMN_LOCATION_MST cmn on  ddo.DEPT_LOC_CODE=cmn.LOC_ID  where ddo.ACTIVATE_FLAG = 1  and emp.REG_STATUS in (1,2) and emp.GENDER='T' and ddo.DDO_CODE <> '1111222222' and cmn.LOC_ID =ocmn.LOC_ID) TransGender_COUNT  ");
		lSBQuery.append("  ,(select count(ORG_EMP_MST_ID)  FROM mst_dcps_emp emp inner join ORG_DDO_MST ddo on emp.DDO_CODE = ddo.DDO_CODE  ");
		lSBQuery.append(" inner join CMN_LOCATION_MST cmn on  ddo.DEPT_LOC_CODE=cmn.LOC_ID   where ddo.ACTIVATE_FLAG = 1  and emp.REG_STATUS in (1,2) and ddo.DDO_CODE <> '1111222222' and cmn.LOC_ID =ocmn.LOC_ID) TOTAL_EMP ");
		lSBQuery.append("  from  CMN_LOCATION_MST ocmn  ");
		lSBQuery.append("  where ocmn.DEPARTMENT_ID = 100001   ");
		lSBQuery.append("  group by ocmn.LOC_ID,ocmn.LOC_NAME order by ocmn.LOC_ID ");
		
		Session gbsSession = serviceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		
		SQLQuery lstQuery = gbsSession.createSQLQuery(lSBQuery.toString());
		
		logger.info("Gender_wiseReport  ***********" + lSBQuery.toString());
		Lstadmin = lstQuery.list();	
		
		}
		catch(Exception E)
		{
			logger.info("Error Occure getAdminDeptDtls() of in Gender_wise Report ---- "+E);					
		}		
		return Lstadmin;
		
		
	}
	
	List getfieldDeptList(Long adminlocid)
	{
		
	List FieldLst = null;
	
	try
	{
	StringBuilder lSBQuery = new StringBuilder();
	lSBQuery.append(" select cfld.loc_id,cfld.LOC_NAME fld_name,(SELECT count(em.ORG_EMP_MST_ID)  ");
	lSBQuery.append("  FROM  mst_dcps_emp em ");
	lSBQuery.append(" INNER JOIN org_ddo_mst ddo on em.DDO_CODE = ddo.DDO_CODE ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.LOC_ID = ddo.DEPT_LOC_CODE ");
	lSBQuery.append("  INNER JOIN CMN_LOCATION_MST fld on fld.LOC_ID = ddo.HOD_LOC_CODE  ");
	lSBQuery.append(" where em.FORM_STATUS =1 and em.REG_STATUS in (1,2) and em.GENDER='M' and ddo.DDO_CODE <> '1111222222' and ddo.ACTIVATE_FLAG = 1 and admin.LOC_ID=cadmin.LOC_ID and fld.loc_id= cfld.loc_id) as MALE_COUNT, ");
	lSBQuery.append(" (SELECT count(em.ORG_EMP_MST_ID) FROM  mst_dcps_emp em ");
	lSBQuery.append(" INNER JOIN org_ddo_mst ddo on em.DDO_CODE = ddo.DDO_CODE ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.LOC_ID = ddo.DEPT_LOC_CODE  ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST fld on fld.LOC_ID = ddo.HOD_LOC_CODE ");
	lSBQuery.append("  where em.FORM_STATUS =1 and em.REG_STATUS in (1,2) and em.GENDER='F' and ddo.DDO_CODE <> '1111222222' and ddo.ACTIVATE_FLAG = 1 and admin.LOC_ID=cadmin.LOC_ID and fld.loc_id= cfld.loc_id) as FEMALE_COUNT, ");
	lSBQuery.append(" (SELECT count(em.ORG_EMP_MST_ID) FROM  mst_dcps_emp em  ");
	lSBQuery.append(" INNER JOIN org_ddo_mst ddo on em.DDO_CODE = ddo.DDO_CODE ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.LOC_ID = ddo.DEPT_LOC_CODE	 ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST fld on fld.LOC_ID = ddo.HOD_LOC_CODE ");
	lSBQuery.append(" where em.FORM_STATUS =1 and em.REG_STATUS in (1,2) and em.GENDER='T' and ddo.DDO_CODE <> '1111222222' and ddo.ACTIVATE_FLAG = 1 and admin.LOC_ID=cadmin.LOC_ID and fld.loc_id= cfld.loc_id) as TRANS_COUNT,");
	lSBQuery.append(" (SELECT count(em.ORG_EMP_MST_ID)  FROM  mst_dcps_emp em ");
	lSBQuery.append(" INNER JOIN org_ddo_mst ddo on em.DDO_CODE = ddo.DDO_CODE  ");
	lSBQuery.append("  INNER JOIN CMN_LOCATION_MST admin on admin.LOC_ID = ddo.DEPT_LOC_CODE  INNER JOIN CMN_LOCATION_MST fld on fld.LOC_ID = ddo.HOD_LOC_CODE ");
	lSBQuery.append(" where em.FORM_STATUS =1 and em.REG_STATUS in (1,2) and ddo.DDO_CODE <> '1111222222' and ddo.ACTIVATE_FLAG = 1 and admin.LOC_ID=cadmin.LOC_ID and fld.loc_id= cfld.loc_id) as TOTAL_COUNT ");
	lSBQuery.append(" from mst_dcps_emp cem INNER JOIN org_ddo_mst cddo on cem.DDO_CODE = cddo.DDO_CODE  ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST cadmin on cadmin.LOC_ID = cddo.DEPT_LOC_CODE  ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST cfld on cfld.LOC_ID = cddo.HOD_LOC_CODE where cddo.ACTIVATE_FLAG = 1 and cadmin.LOC_ID =:adminid group by cadmin.LOC_ID,cfld.loc_id,cfld.LOC_NAME order by cfld.loc_id  ");
	
	
	Session gbsSession = serviceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
	
	SQLQuery lstQuery = gbsSession.createSQLQuery(lSBQuery.toString());	
	logger.info("Gender_wiseReport  ***********" + lSBQuery.toString());
	lstQuery.setLong("adminid",adminlocid);
	FieldLst = lstQuery.list();	
	
	}
	catch(Exception E)
	{
		logger.info("Error Occure getAdminDeptDtls() of in Gender_wise Report ---- "+E);					
	}		
	
	
	return FieldLst;
		
		
	}
	
	List getDDOListbyField(Long fieldlocid)
	{
		
	List FieldLst = null;
	
	try
	{
	StringBuilder lSBQuery = new StringBuilder();
	lSBQuery.append(" select cddo.DDO_CODE,cddo.DDO_NAMe,(SELECT count(em.ORG_EMP_MST_ID)  ");
	lSBQuery.append("  FROM  mst_dcps_emp em ");
	lSBQuery.append(" INNER JOIN org_ddo_mst ddo on em.DDO_CODE = ddo.DDO_CODE ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.LOC_ID = ddo.DEPT_LOC_CODE ");
	lSBQuery.append("  INNER JOIN CMN_LOCATION_MST fld on fld.LOC_ID = ddo.HOD_LOC_CODE  ");
	lSBQuery.append(" where em.FORM_STATUS =1 and em.REG_STATUS in (1,2) and em.GENDER='M' and ddo.DDO_CODE <> '1111222222' and ddo.ACTIVATE_FLAG = 1 and admin.LOC_ID=cadmin.LOC_ID and fld.loc_id= cfld.loc_id and ddo.DDO_CODE=cddo.DDO_CODE) as MALE_COUNT, ");
	lSBQuery.append(" (SELECT count(em.ORG_EMP_MST_ID) FROM  mst_dcps_emp em ");
	lSBQuery.append(" INNER JOIN org_ddo_mst ddo on em.DDO_CODE = ddo.DDO_CODE ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.LOC_ID = ddo.DEPT_LOC_CODE  ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST fld on fld.LOC_ID = ddo.HOD_LOC_CODE ");
	lSBQuery.append("  where em.FORM_STATUS =1 and em.REG_STATUS in (1,2) and em.GENDER='F' and ddo.DDO_CODE <> '1111222222' and ddo.ACTIVATE_FLAG = 1 and admin.LOC_ID=cadmin.LOC_ID and fld.loc_id= cfld.loc_id and ddo.DDO_CODE=cddo.DDO_CODE) as FEMALE_COUNT, ");
	lSBQuery.append(" (SELECT count(em.ORG_EMP_MST_ID) FROM  mst_dcps_emp em  ");
	lSBQuery.append(" INNER JOIN org_ddo_mst ddo on em.DDO_CODE = ddo.DDO_CODE ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.LOC_ID = ddo.DEPT_LOC_CODE	 ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST fld on fld.LOC_ID = ddo.HOD_LOC_CODE ");
	lSBQuery.append(" where em.FORM_STATUS =1 and em.REG_STATUS in (1,2) and em.GENDER='T' and ddo.DDO_CODE <> '1111222222' and ddo.ACTIVATE_FLAG = 1 and admin.LOC_ID=cadmin.LOC_ID and fld.loc_id= cfld.loc_id and ddo.DDO_CODE=cddo.DDO_CODE) as TRANS_COUNT, ");
	lSBQuery.append(" (SELECT count(em.ORG_EMP_MST_ID)  FROM  mst_dcps_emp em ");
	lSBQuery.append(" INNER JOIN org_ddo_mst ddo on em.DDO_CODE = ddo.DDO_CODE  ");
	lSBQuery.append("  INNER JOIN CMN_LOCATION_MST admin on admin.LOC_ID = ddo.DEPT_LOC_CODE  INNER JOIN CMN_LOCATION_MST fld on fld.LOC_ID = ddo.HOD_LOC_CODE ");
	lSBQuery.append("   where em.FORM_STATUS =1 and em.REG_STATUS in (1,2) and ddo.DDO_CODE <> '1111222222' and ddo.ACTIVATE_FLAG = 1 and admin.LOC_ID=cadmin.LOC_ID and fld.loc_id= cfld.loc_id and ddo.DDO_CODE=cddo.DDO_CODE) as TOTAL_COUNT ");
	lSBQuery.append(" from mst_dcps_emp cem INNER JOIN org_ddo_mst cddo on cem.DDO_CODE = cddo.DDO_CODE ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST cadmin on cadmin.LOC_ID = cddo.DEPT_LOC_CODE  ");
	lSBQuery.append(" INNER JOIN CMN_LOCATION_MST cfld on cfld.LOC_ID = cddo.HOD_LOC_CODE where cddo.ACTIVATE_FLAG = 1 and cfld.LOC_ID =:fieldid  group by cadmin.LOC_ID,cfld.loc_id,cddo.DDO_CODE,cddo.DDO_NAMe order by cddo.DDO_CODE ");
	
	
	Session gbsSession = serviceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
	
	SQLQuery lstQuery = gbsSession.createSQLQuery(lSBQuery.toString());	
	logger.info("Gender_wiseReport  ***********" + lSBQuery.toString());
	lstQuery.setLong("fieldid",fieldlocid);
	FieldLst = lstQuery.list();	
	
	}
	catch(Exception E)
	{
		logger.info("Error Occure getAdminDeptDtls() of in Gender_wise Report ---- "+E);					
	}		
	
	
	return FieldLst;
		
		
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
			logger.error("Exception in getDepartmentName:" + e, e);
		}
		return lStrDeptName;
	}
	
	
}
 
 
