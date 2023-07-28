package com.tcs.sgv.dcps.report;

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
import com.tcs.sgv.common.business.reports.ReportDataFinder;
import com.tcs.sgv.common.dao.reports.DefaultReportDataFinder;
import com.tcs.sgv.common.dao.reports.ReportsDAOImpl;
import com.tcs.sgv.common.exception.reports.ReportException;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.common.valuebeans.reports.ReportVO;
import com.tcs.sgv.common.valuebeans.reports.StyleVO;
import com.tcs.sgv.common.valuebeans.reports.StyledData;
import com.tcs.sgv.common.valuebeans.reports.URLData;
import com.tcs.sgv.core.service.ServiceLocator;

public class Post_wiseReport extends DefaultReportDataFinder implements ReportDataFinder {
	
	private static final Logger logger = Logger.getLogger(Post_wiseReport.class);
	
	ServiceLocator serviceLocator = null;
	SessionFactory gObjSessionFactory = null;
	String gStrLocCode = null;
	Long gLngLangId = null;
	Long gLngPostId = null;
	Map lMapSeriesHeadCode = null;
	Map requestAttributes = null;
	Session ghibSession = null;
	
	public Collection findReportData(ReportVO report, Object criteria) throws ReportException 
	{
		
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
			
			if (report.getReportCode().equals("700108")|| report.getReportCode().equals("700117")|| report.getReportCode().equals("700118")) 
			{
				logger.info("in report 700108 ***********");
				
				lStrAdminDept = report.getParameterValue("adminDept").toString();
				logger.info("loc id of AdminDept;;;;;;;;;"+lStrAdminDept);
				
				lStrFieldDept = report.getParameterValue("fieldDept").toString();
				logger.info("loc id of fieldDept;;;;;;;;;"+lStrFieldDept);
				
				/*lStrDDOcode = report.getParameterValue("ddocode").toString();
				logger.info("loc id of DDocode;;;;;;;;;"+lStrDDOcode);*/
				
			
				 String strheader  = null;
				 List lLstFinal = null;
				
				 String srno = null;				
					String adminname = null;
					String fieldname  = null;
					String ddoname  = null;
					String ddocode= null;
					String adminid = null;
					String fieldid = null;
					
					int Totalpermnt = 0;
					int Totaltemp= 0;
					int TotalVcper = 0;
					int TotalVcTem = 0;
					int FinalTotalpst = 0;
					int permnt = 0;
					int temp = 0;
					int vacant_permnt = 0;
					int vacant_temp = 0;
				    int totalpst = 0;
				
				
                Date date = new Date();
				
            	if(lStrAdminDept.equals("")&& lStrFieldDept.equals(""))
				{
				        	 							 
            		 strheader  = report.getReportName();
  					strheader = strheader + "As On" + objsimpledatefrm.format(date) + "\n (Administrative Department Wise)"; 
  					
  				    lLstFinal = getadminwisePost();				 
  				      logger.info("Lists value  -----------"+lLstFinal.toString());
  							    									
  					   report.setReportName(strheader);
            		
				}
                
            	if (!lStrAdminDept.equals(""))
				{
					
					 strheader = report.getReportName();				
					strheader = "Employee Statistics - Post-wise " + "As On" + objsimpledatefrm.format(date) + "\n (Field Department Wise)"; 	
					
					 lLstFinal = getFieldwisePost(Long.parseLong(lStrAdminDept));
					 logger.info("Field Name -----------"+lLstFinal.toString());
					 
						lStrAdminDeptName = getDepartmentName(Long.parseLong(lStrAdminDept));
						 logger.info("ADmin Name -----------"+lLstFinal.toString());	
					 
						 
				report.setAdditionalHeader("Admin Department : "+lStrAdminDeptName);
				
				 report.setReportName(strheader);				
					
				}
			 
			 if (!lStrFieldDept.equals(""))
				{
				  strheader = report.getReportName();
					strheader = "Employee Statistics - Post-wise " + "As On" + objsimpledatefrm.format(date) + "\n (DDO Wise)"; 	
					
					lStrFieldDeptName = getDepartmentName(Long.parseLong(lStrFieldDept));
					 logger.info("Field  Name -----------"+lStrFieldDeptName);	
					lStrAdminDeptName = getDepartmentName(Long.parseLong(lStrAdminDept));
					 logger.info("Admin Name -----------"+lStrAdminDeptName);	
					
					 lLstFinal = getDDOwisePost(Long.parseLong(lStrFieldDept));					 
					 logger.info("DDO Name -----------"+lLstFinal.toString());					 				  
									
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

						if(!lStrFieldDept.equals(""))
						{
						
							ddocode = (lObj[0] != null) ? lObj[0].toString(): "";
							logger.info("DDO Code is  ***********" + ddocode);
							
							ddoname = (lObj[1] != null) ? lObj[1].toString(): "";
							logger.info("DDO Name is  ***********" + ddoname);
						
						
						}
						else  if (!lStrAdminDept.equals(""))
						{
							fieldid = (lObj[0] != null) ? lObj[0].toString(): "";
							logger.info("FieldID is  ***********" + fieldid);
							
							fieldname = (lObj[1] != null) ? lObj[1].toString(): "";
							logger.info("Field name is  ***********" + fieldname);
							
						}
						else
							
						{
							
							adminid  = (lObj[0] != null) ? lObj[0].toString(): "";
							logger.info("AdminID is  ***********" + adminid);
							
							adminname = (lObj[1] != null) ? lObj[1].toString(): "";
							logger.info("Admin name is  ***********" + adminname);
							
							
							
						}

						permnt = (lObj[2] != null) ? Integer.parseInt(lObj[2].toString()) : new Integer(0);
						logger.info("total permanent post count  ***********"+ permnt);
						
						Totalpermnt = Totalpermnt +permnt;
						

						temp = (lObj[3] != null) ? Integer.parseInt(lObj[3].toString()) : new Integer(0);						
						logger.info(" total Temporary Post count  ***********"+ temp);
						
						Totaltemp = Totaltemp + temp;
						
						vacant_permnt = (lObj[4] != null) ? Integer.parseInt(lObj[4].toString()) : new Integer(0);						
						logger.info(" total vacant permanent post count  ***********"+ vacant_permnt);
						
						TotalVcper = TotalVcper + vacant_permnt; 
						
						vacant_temp = (lObj[5] != null) ? Integer.parseInt(lObj[5].toString()) : new Integer(0);						
						logger.info(" total vacant Temporary Post count   ***********"+ vacant_temp);
						
						TotalVcTem = TotalVcTem + vacant_temp;

						totalpst = permnt + temp ;
						logger.info("total number of posts  ***********"+ totalpst);
						
						FinalTotalpst  = FinalTotalpst + totalpst;

						
						dataListForTable.add(count);
						
						if (!lStrFieldDept.equals(""))
						{
							
							dataListForTable.add(ddocode);      //new URLData(multipalename,"ifms.htm?actionFlag=reportService&reportCode=700107&action=generateReport&ddocode="+ddocode ));
						}
						
						else if (!lStrAdminDept.equals(""))
						{
							dataListForTable.add(new URLData(fieldname,"ifms.htm?actionFlag=reportService&reportCode=700118&action=generateReport&fieldDept="+fieldid +"&adminDept="+lStrAdminDept ));			
											
						}
						else
						{
							dataListForTable.add(new URLData(adminname,"ifms.htm?actionFlag=reportService&reportCode=700117&action=generateReport&adminDept="+adminid ));
							
						}
						//dataListForTable.add(multipalename);
						dataListForTable.add(permnt);					
						dataListForTable.add(temp);
						dataListForTable.add(vacant_permnt);
						dataListForTable.add(vacant_temp);
						dataListForTable.add(totalpst);
						DataList.add(dataListForTable);

					}
				}
				
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
				 dataStyle1.setData(Totalpermnt);
				 row2.add(dataStyle1);
				 
				 dataStyle1 = new StyledData();
				 dataStyle1 =  new StyledData();
				 dataStyle1.setStyles(boldStyleVO);
				 dataStyle1.setData(Totaltemp);
				 row2.add(dataStyle1);
				 
				 dataStyle1 = new StyledData();
				 dataStyle1 =  new StyledData();
				 dataStyle1.setStyles(boldStyleVO);
				 dataStyle1.setData(TotalVcper);
				 row2.add(dataStyle1);
				 
				 dataStyle1 = new StyledData();
				 dataStyle1 =  new StyledData();
				 dataStyle1.setStyles(boldStyleVO);
				 dataStyle1.setData(TotalVcTem);
				 row2.add(dataStyle1);
				 
				 dataStyle1 = new StyledData();
				 dataStyle1 =  new StyledData();
				 dataStyle1.setStyles(boldStyleVO);
				 dataStyle1.setData(FinalTotalpst);
				 row2.add(dataStyle1);
				 DataList.add(row2);
			}

		} catch (Exception e) {
			logger.error("Error in Post_waiseReport " + e.getMessage());
			logger.error("Printing StackTrace");
			e.printStackTrace();
		}

		return DataList;

	}
	
	
	public List getadminwisePost()
	{
		List adminList = null;
		
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT cmn.Loc_id ,cmn.LOC_NAME Admin_Name,sum(pdd.PERMNT_POST_EMP_ATCH),sum(pdd.TEMP_POST_EMP_ATCH),sum(PERMNT_VACANT),sum(TEMP_VACANT) ");
		lSBQuery.append(" FROM ORG_DDO_MST ddo inner join MST_POST_DIFF_DTLS pdd on pdd.ddo_code=ddo.ddo_code inner join CMN_LOCATION_MST cmn on ddo.DEPT_LOC_CODE=cmn.LOC_ID ");
		lSBQuery.append(" where ddo.ACTIVATE_FLAG =1 and cmn.DEPARTMENT_ID =100001 and ddo.DDO_CODE <> '1111222222' group by cmn.LOC_id,CMN.lOC_NAME order by cmn.LOC_NAME  ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
		logger.info("Post_wiseReport  ***********" + lSBQuery.toString());
		
	  adminList = Query.list();
		
		return adminList;
		
		
	}
	
	
	public List getFieldwisePost(Long adminid)
	{
		List FieldList = null;
		
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" SELECT loc_id,loc_name,sum(pdd.PERMNT_POST_EMP_ATCH) as p_allocated ,sum(pdd.TEMP_POST_EMP_ATCH) t_alloated,sum(pdd.PERMNT_VACANT) p_vacant,sum(pdd.TEMP_VACANT) t_vacant  ");
		lSBQuery.append(" FROM CMN_LOCATION_MST cmn INNER JOIN ORG_DDO_MST ddo on ddo.HOD_LOC_CODE=cmn.LOC_ID INNER JOIN MST_POST_DIFF_DTLS pdd on pdd.ddo_code=ddo.ddo_code ");
		lSBQuery.append(" where ddo.ACTIVATE_FLAG =1 and  cmn.PARENT_LOC_ID =:adminid  group by loc_id,loc_name order by loc_id ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
		logger.info("Post_wiseReport  ***********" + lSBQuery.toString());
		Query.setLong("adminid", adminid);	
		
	  FieldList = Query.list();
		
		return FieldList;
		
		
	}
	
	
	public List getDDOwisePost(Long fieldid)
	{
		List adminList = null;
		
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" select ddo.ddo_code,ddo.ddo_name,sum(pdd.PERMNT_POST_EMP_ATCH) as p_allocated ,sum(pdd.TEMP_POST_EMP_ATCH) t_alloated,sum(pdd.PERMNT_VACANT) p_vacant,sum(pdd.TEMP_VACANT) t_vacant  ");
		lSBQuery.append(" from ORG_DDO_MST as ddo INNER JOIN MST_POST_DIFF_DTLS pdd on pdd.ddo_code=ddo.ddo_code ");
		lSBQuery.append(" where ddo.ACTIVATE_FLAG =1 and  ddo.HOD_LOC_CODE =:fieldid  group by ddo.ddo_code,ddo.ddo_name order by ddo.ddo_code ");
		
		Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();
		SQLQuery Query = ghibSession.createSQLQuery(lSBQuery.toString());
		logger.info("Post_wiseReport  ***********" + lSBQuery.toString());
		
		Query.setLong("fieldid", fieldid);
	  adminList = Query.list();
		
		return adminList;
		
		
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