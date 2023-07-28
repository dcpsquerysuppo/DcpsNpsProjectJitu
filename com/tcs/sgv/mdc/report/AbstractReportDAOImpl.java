package com.tcs.sgv.mdc.report;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class AbstractReportDAOImpl extends GenericDaoHibernateImpl {

	private SessionFactory sessionFactory = null;
	Session ghibSession = null;

	public AbstractReportDAOImpl (Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = getSession();
		//ghibSession = sessionFactory.getCurrentSession();
		//setSessionFactory(sessionFactory);
	}
	
	
public List getAdminID() {
		

		List lLstResult121 = null;
		StringBuffer lSBQuery = null;
		
		try {
		
			logger.info("Enter in DemoReportDAO Impl with getTotalDDOCount : ");
			
			lSBQuery = new StringBuffer("SELECT admin.loc_Id FROM Cmn_Location_Mst admin, cmn_Language_Mst lang where lang.lang_Id =1 and admin.department_Id = 100001  ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
		lLstResult121 = lQuery.list();
			 logger.info(" Admin id is lLstResult.. "+lLstResult121.size());	
					
			}
		catch (Exception e) {
			logger.info("Exception occurred while retrieving data From getEnteredFieldCount : " + e, e);
		}
		return lLstResult121;
		
	}
	
	
	

	public String getAdminID(Long admin_id) {
			

			String lLstResult121 = null;
			StringBuffer lSBQuery = null;
			
			try {
			
				logger.info("Enter in DemoReportDAO Impl with getTotalDDOCount : ");
				
				lSBQuery = new StringBuffer("SELECT admin.LOC_NAME FROM Cmn_Location_Mst admin, cmn_Language_Mst lang where lang.lang_Id =1 and admin.department_Id = 100001 and admin.LOC_ID=:fiecode  ");
				Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
				lQuery.setParameter("fiecode", admin_id);
			logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
			logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
			lLstResult121 = (String) lQuery.uniqueResult();
				 logger.info(" Admin id is lLstResult.. "+lLstResult121.length());	
						
				}
			catch (Exception e) {
				logger.info("Exception occurred while retrieving data From getEnteredFieldCount : " + e, e);
			}
			return lLstResult121;
			
		}
	
	
public Long getFieldCount( Long lStrAdminName) {
		
		Long lLstResult = null;
		StringBuffer lSBQuery = null;
		
		try {
			
			logger.info("Enter in DemoReportDAO Impl with getFieldCount : ");
			
		lSBQuery = new StringBuffer(" select count(1)from CMN_LOCATION_MST where PARENT_LOC_ID=:loccode1 ");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("loccode1", lStrAdminName);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
		Object result= lQuery.uniqueResult();
		
			if(result!=null)
				lLstResult=Long.parseLong(result.toString());
			else
				lLstResult=0L;
				 logger.info(" Field count is ******.. "+lLstResult);	   	    	 			 			 		
				
						}
		catch (Exception e) {
			logger.info("Exception occurred while retrieving data From getFieldCount : " + e, e);
		}
		return lLstResult;
		
		
}


public Long getEnteredFieldCount(Long lStrAdminName) {
	
	Long lLstResult = null;
	StringBuffer lSBQuery = null;
	
	try {
		
		logger.info("Enter in DemoReportDAO Impl with getTotalDDOCount : ");
		
		lSBQuery = new StringBuffer(" SELECT count(distinct fld.loc_name)  ");
		lSBQuery.append(" 	 FROM org_post_mst post, CMN_LOCATION_MST admin, CMN_LOCATION_MST fld, ORG_DDO_MST ddo, ORG_USERPOST_RLT up, ORG_POST_DETAILS_RLT dtls, MST_POST_DIFF_DTLS pst     ");
		lSBQuery.append(" where  post.LOCATION_CODE = ddo.LOCATION_CODE and ddo.DEPT_LOC_CODE = admin.LOC_ID and ddo.HOD_LOC_CODE = fld.LOC_ID and pst.DDO_CODE = ddo.DDO_CODE  ");
		lSBQuery.append("   and post.POST_ID <> ddo.POST_ID and post.POST_ID = dtls.POST_ID and  pst.PRINT = 1  and post.POST_TYPE_LOOKUP_ID in (10001198129,10001198130) and up.POST_ID = post.POST_ID   ");
		lSBQuery.append(" AND ADMIN.LOC_ID =:loccode1 GROUP BY admin.LOC_id,ADMin.LOC_NAME  ");
		
	
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("loccode1", lStrAdminName);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	Object result= lQuery.uniqueResult();
	if(result!=null)
		lLstResult=Long.parseLong(result.toString());
	else
		lLstResult=0L;
		 logger.info(" Enter Field count is ******.. "+lLstResult);	 	   	    	 			 			 		
	
	
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getFieldCount : " + e, e);
	}
	return lLstResult;
}


public List getSumOfDDOCount(Long fie) {
	
	List lLstResult = null;
	StringBuffer lSBQuery = null;
	
	try {
	
		logger.info("Enter in DemoReportDAO Impl with getTotalDDOCount : ");
		
		lSBQuery = new StringBuffer(" SELECT admin.LOC_ID+0 as AdminLocId,concat(FIE.LOC_NAME,'') as fielLocName ,COUNT( DISTINCT DDO.DDO_CODE),Fie.LOC_ID+0 as fieldLocId,Count(EMP.DCPS_EMP_ID),concat(ADMIN.LOC_NAME,'') as adminLocName FROM ORG_DDO_MST DDO ");
		lSBQuery.append("  INNER JOIN MST_DCPS_EMP  emp on emp.DDO_CODE = DDO.DDO_CODE   ");
		lSBQuery.append("   INNER JOIN CMN_LOCATION_MST FIE ON FIE.LOC_ID = DDO.HOD_LOC_CODE  ");
		lSBQuery.append("   INNER JOIN CMN_LOCATION_MST ADMIN ON ADMIN.LOC_ID = DDO.DEPT_LOC_CODE  ");
		lSBQuery.append("  WHERE DDO.ACTIVATE_FLAG = 1  and admin.LOC_ID=:loc ");
		lSBQuery.append(" GROUP BY admin.loc_id, ADMIN.LOC_NAME, FIE.LOC_NAME , Fie.LOC_ID ");
		lSBQuery.append(" order by admin.LOC_ID ");
	
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	//lQuery.setParameter("loccode", Loc_code);
	lQuery.setParameter("loc", fie);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	lLstResult = lQuery.list();
		 logger.info(" Total DDO count lLstResult.. "+lLstResult.size());	
		 logger.info(" field id1 --------------------"+		  lLstResult);
		 /* for(int i=0;i<lLstResult.size();i++){
			  Object lObj[] = (Object[]) lLstResult.get(i);
			  for(int j=0;j<lObj.length;j++)
			  System.out.print(" i= ===========  "+i+"================"+lObj[j]);
			  logger.info("");
		  }
				*/ 
		
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getTotalDDOCount(String) : " + e, e);
	}
	return lLstResult;
}




public List getSumOfDDOCountEnter(Long Field) {
		
		List lLstResult1 = null;
		//Map fieldMap=new HashMap();
		StringBuffer lSBQuery = null;
		
		try {		
			logger.info("Enter in DemoReportDAO Impl with getTotalDDOCountEnter : ");
			
			lSBQuery = new StringBuffer(" SELECT COUNT( DISTINCT DDO.DDO_CODE),Count(EMP.DCPS_EMP_ID), admin.LOC_NAME ,FIE.LOC_NAME, Fie.LOC_ID");
			lSBQuery.append("  FROM ORG_DDO_MST DDO INNER JOIN MST_DCPS_EMP  emp on emp.DDO_CODE = DDO.DDO_CODE   ");
			lSBQuery.append("   INNER JOIN MST_POST_DIFF_DTLS pst on ddo.DDO_CODE = pst.DDO_CODE    ");
			lSBQuery.append("  INNER JOIN CMN_LOCATION_MST FIE ON fie.LOC_ID = ddo.HOD_LOC_CODE ");
			lSBQuery.append("  INNER JOIN CMN_LOCATION_MST admin on admin.loc_ID = DDO.DEPT_LOC_CODE    ");
			lSBQuery.append("  WHERE PST.PRINT = 1  and admin.LOC_ID =:fiecode  ");
			lSBQuery.append("  group by ADMIN.LOC_NAME, FIE.LOC_NAME,FIE.LOC_ID    ");
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		//lQuery.setParameter("loccode", Loc_code);
		lQuery.setParameter("fiecode", Field);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
		  lLstResult1 = lQuery.list();
				
		  logger.info(" Total List size of DDo count entered  ******.. "+lLstResult1.size());	   	    	 			 			 		
		  logger.info(" field id1 --------------------"+		  lLstResult1);
		/*  for(int i=0;i<lLstResult1.size();i++){
			  Object lObj[] = (Object[]) lLstResult1.get(i);
			  for(int j=0;j<lObj.length;j++)
			  System.out.print(" i= ===========  "+i+"================"+lObj[j]);
			  logger.info("");
		  }*/
			}
		catch (Exception e) {
			logger.info("Exception occurred while retrieving data From getTotalDDOCountEnter(string) : " + e, e);
		}
		return lLstResult1;
}

	
		

}





