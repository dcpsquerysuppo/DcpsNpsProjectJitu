package com.tcs.sgv.mdc.report;

import java.util.Collection;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.util.reports.IReportConstants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;

public class DemoReportDAOImpl extends GenericDaoHibernateImpl implements
		DemoReportDAO {

	private SessionFactory sessionFactory = null;
	Session ghibSession = null;

	public DemoReportDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = getSession();
		//ghibSession = sessionFactory.getCurrentSession();
		//setSessionFactory(sessionFactory);
	}


public List getTotalDDOCountEnter(Long Loc_code , String Field) {
		
		List lLstResult = null;
		//Map fieldMap=new HashMap();
		StringBuffer lSBQuery = null;
		
		try {		
			logger.info("Enter in DemoReportDAO Impl with getTotalDDOCountEnter : ");
			
			lSBQuery = new StringBuffer(" SELECT COUNT( DISTINCT DDO.DDO_CODE),Count(EMP.DCPS_EMP_ID), admin.LOC_NAME ,FIE.LOC_NAME, Fie.LOC_ID   ");
			lSBQuery.append("  FROM ORG_DDO_MST DDO INNER JOIN MST_DCPS_EMP  emp on emp.DDO_CODE = DDO.DDO_CODE   ");
			lSBQuery.append("   INNER JOIN MST_POST_DIFF_DTLS pst on ddo.DDO_CODE = pst.DDO_CODE   ");
			lSBQuery.append("   INNER JOIN CMN_LOCATION_MST FIE ON fie.LOC_ID = ddo.HOD_LOC_CODE  ");
			lSBQuery.append("  INNER JOIN CMN_LOCATION_MST admin on admin.loc_ID = DDO.DEPT_LOC_CODE    ");
			lSBQuery.append("   WHERE PST.PRINT = 1  and admin.LOC_ID =:loccode and Fie.LOC_ID =:fiecode  ");
			lSBQuery.append("   group by ADMIN.LOC_NAME, FIE.LOC_NAME,FIE.LOC_ID   ");
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("loccode", Loc_code);
		lQuery.setParameter("fiecode", Field);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
		  lLstResult = lQuery.list();
				
		  logger.info(" Total List size of DDo count entered  ******.. "+lLstResult.size());	   	    	 			 			 		
		
			}
		catch (Exception e) {
			logger.info("Exception occurred while retrieving data From getTotalDDOCountEnter(string) : " + e, e);
		}
		return lLstResult;
}


public List getField_ID(Long Loc_code) {
	
	List lLstResult = null;
	StringBuffer lSBQuery = null;
	
	try {
	
		logger.info("Enter in DemoReportDAO Impl with getField_ID : ");
		
		lSBQuery = new StringBuffer(" SELECT fie.loc_Id,fie.loc_Name FROM Cmn_Location_Mst fie ,cmn_Language_Mst lan where lan.lang_Id=1 and fie.parent_Loc_Id =loccode ");
		
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("loccode", Loc_code);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	lLstResult = lQuery.list();
		 logger.info(" Field id List lLstResult.. "+lLstResult.size());	
				 
		
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getTotalDDOCount(String) : " + e, e);
	}
	return lLstResult;
}




public List getTotalDDOCount(Long Loc_code) {
		
		List lLstResult = null;
		StringBuffer lSBQuery = null;
		
		try {
		
			logger.info("Enter in DemoReportDAO Impl with getTotalDDOCount : ");
			
			lSBQuery = new StringBuffer(" SELECT admin.LOC_ID,FIE.LOC_NAME,COUNT( DISTINCT DDO.DDO_CODE),Count(EMP.DCPS_EMP_ID),Fie.LOC_ID FROM ORG_DDO_MST DDO ");
			lSBQuery.append("  INNER JOIN MST_DCPS_EMP  emp on emp.DDO_CODE = DDO.DDO_CODE   ");
			lSBQuery.append("   INNER JOIN CMN_LOCATION_MST FIE ON FIE.LOC_ID = DDO.HOD_LOC_CODE  ");
			lSBQuery.append("   INNER JOIN CMN_LOCATION_MST ADMIN ON ADMIN.LOC_ID = DDO.DEPT_LOC_CODE  ");
			lSBQuery.append("  WHERE DDO.ACTIVATE_FLAG = 1 and  admin.LOC_ID =:loccode ");
			lSBQuery.append(" GROUP BY admin.loc_id, ADMIN.LOC_NAME, FIE.LOC_NAME , Fie.LOC_ID ");
			lSBQuery.append(" order by admin.LOC_ID ");
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("loccode", Loc_code);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
		lLstResult = lQuery.list();
			 logger.info(" Total DDO count lLstResult.. "+lLstResult.size());	
					 
			
			}
		catch (Exception e) {
			logger.info("Exception occurred while retrieving data From getTotalDDOCount(String) : " + e, e);
		}
		return lLstResult;
}


	public Long getFieldCount(Long lStrAdminName) {
		
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
			lSBQuery.append(" FROM org_post_mst post, CMN_LOCATION_MST admin, CMN_LOCATION_MST fld, ORG_DDO_MST ddo, ORG_USERPOST_RLT up, ORG_POST_DETAILS_RLT dtls, MST_POST_DIFF_DTLS pst       ");
			lSBQuery.append("  where  post.LOCATION_CODE = ddo.LOCATION_CODE and ddo.DEPT_LOC_CODE = admin.LOC_ID and ddo.HOD_LOC_CODE = fld.LOC_ID and pst.DDO_CODE = ddo.DDO_CODE   ");
			lSBQuery.append("   and admin.LOC_ID =:loccode1 and post.POST_ID <> ddo.POST_ID and post.POST_ID = dtls.POST_ID and DDo.ACTIVATE_FLAG =1  and post.POST_TYPE_LOOKUP_ID in (10001198129,10001198130) and up.POST_ID = post.POST_ID    ");
			lSBQuery.append(" GROUP BY admin.LOC_id,ADMin.LOC_NAME");
			
		
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
	
	
	
	
/*public List getTotalDDOCount(String Loc_code) {
		
		List lLstResult = null;
		//Map fieldMap =new HashMap();
		StringBuffer lSBQuery = null;
		
		try {
			//Session hibSession = getSession();
			logger.info("Enter in DemoReportDAO Impl with getTotalDDOCount : ");
			
			lSBQuery = new StringBuffer(" SELECT ADMIN.LOC_NAME, FIE.LOC_NAME, FIE.LOC_id,COUNT(DDO.DDO_CODE)Field_count FROM MST_POST_DIFF_DTLS pst ");
			lSBQuery.append(" INNER JOIN ORG_DDO_MST ddo on ddo.DDO_CODE = pst.DDO_CODE ");
			lSBQuery.append(" INNER JOIN CMN_LOCATION_MST FIE ON fie.LOC_ID = ddo.HOD_LOC_CODE  ");
			lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.loc_ID = DDO.DEPT_LOC_CODE  ");
			//lSBQuery.append(" WHERE PST.PRINT = 1 and admin.LOC_ID =10001 group by ADMIN.LOC_NAME, FIE.LOC_NAME, FIE.LOC_id  ");
			lSBQuery.append(" WHERE PST.PRINT = 1 and admin.LOC_ID =:loccode group by ADMIN.LOC_NAME, FIE.LOC_NAME, FIE.LOC_id  ");
			
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("loccode", Loc_code);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
		logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
		lLstResult = lQuery.list();
			 logger.info("lLstResult.. "+lLstResult.size());	   	    	 			 			 		
			return lLstResult;
			}
		catch (Exception e) {
			logger.info("Exception occurred while retrieving data From getTotalDDOCount : " + e, e);
		}
		return lLstResult;
}
*/
public List getTotalDDOCountnotenter(String Loc_code) {
	
	List lLstResult = null;
	//Map fieldMap =new HashMap();
	StringBuffer lSBQuery = null;
	
	try {
		//Session hibSession = getSession();
		logger.info("Enter in DemoReportDAO Impl with getTotalDDOCountnotenter : ");
		
		lSBQuery = new StringBuffer(" SELECT ADMIN.LOC_NAME, FIE.LOC_NAME, FIE.LOC_id,COUNT(DDO.DDO_CODE)Field_count FROM MST_POST_DIFF_DTLS pst ");
		lSBQuery.append(" INNER JOIN ORG_DDO_MST ddo on ddo.DDO_CODE = pst.DDO_CODE ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST FIE ON fie.LOC_ID = ddo.HOD_LOC_CODE  ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.loc_ID = DDO.DEPT_LOC_CODE  ");
		//lSBQuery.append(" WHERE PST.PRINT = 1 and admin.LOC_ID =10001 group by ADMIN.LOC_NAME, FIE.LOC_NAME, FIE.LOC_id  ");
		lSBQuery.append(" WHERE PST.PRINT = 1 and admin.LOC_ID =:loccode group by ADMIN.LOC_NAME, FIE.LOC_NAME, FIE.LOC_id  ");
		
		
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("loccode", Loc_code);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	lLstResult = lQuery.list();
		 	   	    	 			 			 		
		//return lLstResult;
	logger.info("lLstResult "+lLstResult.size());
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getTotalDDOCountnotenter : " + e, e);
	}
	return lLstResult;
}



public List getTotalEmployeeEnterCount(String Loc_code) {
	
	List lLstResult = null;
	StringBuffer lSBQuery = null;
	
	try {
		
		logger.info("Enter in DemoReportDAO Impl with getTotalEmployeeEnterCount :------------------------ ");
		
		lSBQuery = new StringBuffer("SELECT admin.LOC_ID,Count(EMP.DCPS_EMP_ID) ");
		lSBQuery.append(" FROM ORG_DDO_MST DDO INNER JOIN MST_DCPS_EMP  emp on emp.DDO_CODE = DDO.DDO_CODE   ");
		lSBQuery.append("  INNER JOIN MST_POST_DIFF_DTLS pst on ddo.DDO_CODE = pst.DDO_CODE   ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST FIE ON fie.LOC_ID = ddo.HOD_LOC_CODE ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.loc_ID = DDO.DEPT_LOC_CODE  ");
		lSBQuery.append(" WHERE PST.PRINT = 1  and admin.LOC_ID =:loccode group by ADMIN.LOC_ID, FIE.LOC_NAME,FIE.LOC_ID   ");
		
		
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("loccode", Loc_code);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	lLstResult = lQuery.list();
		 	   	    	 			 			 		
		//return lLstResult;
	logger.info("Employee  entered Count "+lLstResult.size());
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getTotalEmployeeEnterCount : " + e, e);
	}
	return lLstResult;
}




public List getTotalEmployeeCount(String Loc_code) {
	
	List lLstResult = null;
	StringBuffer lSBQuery = null;
	
	try {
	
		logger.info("Enter in DemoReportDAO Impl with getTotalEmployeeCount : ");
		
		lSBQuery = new StringBuffer(" SELECT Admin.LOC_ID,Count(EMP.DCPS_EMP_ID)DDO_COunt FROM ORG_DDO_MST DDO");
		lSBQuery.append(" INNER JOIN MST_DCPS_EMP  emp on emp.DDO_CODE = DDO.DDO_CODE  ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST FIE ON FIE.LOC_ID = DDO.HOD_LOC_CODE  ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST ADMIN ON ADMIN.LOC_ID = DDO.DEPT_LOC_CODE  ");
		lSBQuery.append(" WHERE DDO.ACTIVATE_FLAG = 1 and  admin.LOC_ID=:loccode ");
		lSBQuery.append(" GROUP BY admin.loc_id, ADMIN.LOC_NAME, FIE.LOC_NAME , Fie.LOC_ID ");
				
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("loccode", Loc_code);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	lLstResult = lQuery.list();
		 	   	    	 			 			 		
		//return lLstResult;
	logger.info("Employee  count "+lLstResult.size());
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getTotalEmployeeCount : " + e, e);
	}
	return lLstResult;
}

public List getDDOInformation(String lStrAdminid,String lStrfieldid) {
	
	List lLstResult = null;
	//Map fieldMap =new HashMap();
	StringBuffer lSBQuery = null;
	int lIntSerialNo = 1;
	try {
		Session hibSession = getSession();
		logger.info("Enter in DemoReportDAO Impl with getDDOInformation : ");
		
		lSBQuery = new StringBuffer(" SELECT admin.LOC_id loc1,admin.LOC_NAME ADMIN_DEPT ,fld.loc_name FIELD_DEPT,fld.loc_id, ddo.DDO_CODE code_ddo, ddo.DDO_NAME DDO_name, ddo.DDO_OFFICE office_name,  ");
	lSBQuery.append("  (select count(1) from org_post_mst post2, CMN_LOCATION_MST admin2, CMN_LOCATION_MST fld2, ORG_DDO_MST ddo2, ORG_USERPOST_RLT up2    ");
	lSBQuery.append(" where post2.LOCATION_CODE = ddo2.LOCATION_CODE and ddo2.DEPT_LOC_CODE = admin2.LOC_ID and ddo2.HOD_LOC_CODE = fld2.LOC_ID and post2.POST_ID <> ddo2.POST_ID   ");
	lSBQuery.append(" and post2.POST_TYPE_LOOKUP_ID = 10001198129 and up2.POST_ID = post2.POST_ID and up2.ACTIVATE_FLAG = 1 and   ");
	lSBQuery.append("  ddo2.ddo_code = ddo.ddo_code  GROUP BY ADMin2.LOC_NAME, Fld2.LOC_NAME) A1,  ");
	lSBQuery.append(" (select count(1) from org_post_mst post1, CMN_LOCATION_MST admin1, CMN_LOCATION_MST fld1, ORG_DDO_MST ddo1, ORG_USERPOST_RLT up1  ");
	lSBQuery.append(" where post1.LOCATION_CODE = ddo1.LOCATION_CODE and ddo1.DEPT_LOC_CODE = admin1.LOC_ID and ddo1.HOD_LOC_CODE = fld1.LOC_ID and post1.POST_ID <> ddo1.POST_ID     ");
	lSBQuery.append(" and post1.POST_TYPE_LOOKUP_ID = 10001198130 and up1.POST_ID = post1.POST_ID and up1.ACTIVATE_FLAG = 1 and post1.end_date > sysdate   ");
	lSBQuery.append(" and ddo1.ddo_code = ddo.ddo_code  GROUP BY ADMin1.LOC_NAME, Fld1.LOC_NAME) A2, pst.PERMNT_POST_EMP_ATCH B1, pst.TEMP_POST_EMP_ATCH B2  ");
	lSBQuery.append(" FROM org_post_mst post, CMN_LOCATION_MST admin, CMN_LOCATION_MST fld, ORG_DDO_MST ddo, ORG_USERPOST_RLT up, ORG_POST_DETAILS_RLT dtls, MST_POST_DIFF_DTLS pst     ");
	lSBQuery.append(" where admin.LOC_id= :loccode and fld.LOC_ID =:fid_loc and post.LOCATION_CODE = ddo.LOCATION_CODE and ddo.DEPT_LOC_CODE = admin.LOC_ID and ddo.HOD_LOC_CODE = fld.LOC_ID and pst.DDO_CODE = ddo.DDO_CODE    ");
	lSBQuery.append(" and post.POST_ID <> ddo.POST_ID and post.POST_ID = dtls.POST_ID and  pst.PRINT = 1 and post.POST_TYPE_LOOKUP_ID in (10001198129,10001198130) and up.POST_ID = post.POST_ID and up.ACTIVATE_FLAG = 1    ");
	lSBQuery.append(" GROUP BY admin.LOC_id,ADMin.LOC_NAME, ddo.DDO_CODE,ddo.DDO_OFFICE,ddo.DDO_NAME,fld.loc_id, fld.LOC_NAME, pst.TEMP_POST_EMP_ATCH, pst.PERMNT_POST_EMP_ATCH     ");
	

	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("loccode", lStrAdminid);
	lQuery.setParameter("fid_loc", lStrfieldid);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	lLstResult = lQuery.list();
		 	   	    	 			 			 		
		return lLstResult;
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data: " + e, e);
	}
	return lLstResult;
}


public Long getTotalNoOFDDOCount(String Loc_code,String Loc_id) {
	
	Long lLstResult = null;
	//Map fieldMap =new HashMap();
	StringBuffer lSBQuery = null;
	
	try {
		//Session hibSession = getSession();
		logger.info("Enter in DemoReportDAO Impl with getTotalNoOFDDOCount : ");
		
		lSBQuery = new StringBuffer(" SELECT COUNT(DDO.DDO_CODE) FROM ORG_DDO_MST DDO ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST FIE ON FIE.LOC_ID = DDO.HOD_LOC_CODE ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST ADMIN ON ADMIN.LOC_ID = DDO.DEPT_LOC_CODE ");
		lSBQuery.append(" WHERE DDO.ACTIVATE_FLAG = 1 and admin.LOC_ID=:loccode and FIE.LOC_ID=:locid ");
		lSBQuery.append(" GROUP BY ADMIN.LOC_NAME, FIE.LOC_NAME,FIE.LOC_ID   ");
		
		
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("loccode", Loc_code);
	lQuery.setParameter("locid", Loc_id);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	Object result= lQuery.uniqueResult();
	if(result!=null)
		lLstResult=Long.parseLong(result.toString());
	else
		lLstResult=0L;
		 logger.info(" DDO count is lLstResult.. "+lLstResult);	   	    	 			 			 		
		
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getTotalNoOFDDOCount : " + e);
	}
	return lLstResult;
}


public Long getCountTotalNoOFDDOEnter(String Loc_code,String Loc_id) {
	
	Long lLstResult = null;
	//Map fieldMap =new HashMap();
	StringBuffer lSBQuery = null;
	
	try {
		//Session hibSession = getSession();
		logger.info("Enter in DemoReportDAO Impl with getCountTotalNoOFDDOEnter : ");
		
		lSBQuery = new StringBuffer(" SELECT COUNT(DDO.DDO_CODE) DDO_COUNT  FROM MST_POST_DIFF_DTLS pst ");
		lSBQuery.append(" INNER JOIN ORG_DDO_MST ddo on ddo.DDO_CODE = pst.DDO_CODE  ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST FIE ON fie.LOC_ID = ddo.HOD_LOC_CODE ");
		lSBQuery.append(" INNER JOIN CMN_LOCATION_MST admin on admin.loc_ID = DDO.DEPT_LOC_CODE  ");
		lSBQuery.append(" WHERE PST.PRINT = 1  and admin.LOC_ID =:loccode  and Fie.LOC_ID =:locid  ");
		lSBQuery.append(" group by ADMIN.LOC_NAME, FIE.LOC_NAME,FIE.LOC_ID  ");
		
		
	Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	lQuery.setParameter("loccode", Loc_code);
	lQuery.setParameter("locid", Loc_id);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lQuery);
	logger.info("==> getGpfForClassFourTotalDtls query :: "+lSBQuery.toString());
	Object result = lQuery.uniqueResult();
	if(result!=null)
		lLstResult=Long.parseLong(result.toString());
	else
		lLstResult=0L;
	 logger.info(" Enter DDO count is lLstResult.. "+lLstResult);	   	    	 			 			 		
		
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getCountTotalNoOFDDOEnter : " + e);
	}
	return lLstResult;
}


@Override
public List getTotalDDOCountnotenter(Long Loc_code) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public List getTotalEmployeeCount(Long Loc_code) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public List getTotalEmployeeEnterCount(Long Loc_code) {
	// TODO Auto-generated method stub
	return null;
}





}
