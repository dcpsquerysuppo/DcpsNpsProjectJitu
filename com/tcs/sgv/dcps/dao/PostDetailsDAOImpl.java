package com.tcs.sgv.dcps.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Oct 17, 2012
 */

public class PostDetailsDAOImpl extends GenericDaoHibernateImpl implements PostDetailsDAO
{
	Session ghibSession = null;
	public PostDetailsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public List getPostDetails(String lStrDdoCode) throws Exception
	{
		List lLstPostData = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT admin.LOC_NAME , Fld.LOC_NAME , ddo.DDO_CODE,ddo.DDO_NAME, "
					+ "(select count(1) from org_post_mst post2, CMN_LOCATION_MST admin2, CMN_LOCATION_MST fld2, ORG_DDO_MST ddo2, ORG_USERPOST_RLT up2 "
					+ "where post2.LOCATION_CODE = ddo2.LOCATION_CODE and ddo2.DEPT_LOC_CODE = admin2.LOC_ID and ddo2.HOD_LOC_CODE = fld2.LOC_ID and post2.POST_ID <> ddo2.POST_ID "
					+ "and post2.POST_TYPE_LOOKUP_ID = 10001198129 and up2.POST_ID = post2.POST_ID and up2.ACTIVATE_FLAG = 1 and "
					+ " post2.activate_flag = 1 and"
					+ " ddo2.ddo_code = ddo.ddo_code "
					+ "GROUP BY DDO2.DDO_CODE, ADMin2.LOC_NAME, fld2.LOC_NAME, ddo2.DDO_NAME), "
					+ "(select count(1) from org_post_mst post1, CMN_LOCATION_MST admin1, CMN_LOCATION_MST fld1, ORG_DDO_MST ddo1, ORG_USERPOST_RLT up1 "
					+ "where post1.LOCATION_CODE = ddo1.LOCATION_CODE and ddo1.DEPT_LOC_CODE = admin1.LOC_ID and ddo1.HOD_LOC_CODE = fld1.LOC_ID and post1.POST_ID <> ddo1.POST_ID "
					+ "and post1.POST_TYPE_LOOKUP_ID = 10001198130 and up1.POST_ID = post1.POST_ID and up1.ACTIVATE_FLAG = 1 and post1.end_date > sysdate "
					+ " and post1.activate_flag = 1 "
					+ "and ddo1.ddo_code = ddo.ddo_code "
					+ "GROUP BY DDO1.DDO_CODE, ADMin1.LOC_NAME, fld1.LOC_NAME, ddo1.DDO_NAME), "
					+ "(select count(1) from org_post_mst post4, CMN_LOCATION_MST admin4, CMN_LOCATION_MST fld4, ORG_DDO_MST ddo4 "
					+ "where post4.LOCATION_CODE = ddo4.LOCATION_CODE and ddo4.DEPT_LOC_CODE = admin4.LOC_ID and ddo4.HOD_LOC_CODE = fld4.LOC_ID and post4.POST_ID <> ddo4.POST_ID "
					+ "and post4.POST_TYPE_LOOKUP_ID = 10001198130 and post4.end_date < sysdate "
					+ "and ddo4.ddo_code = ddo.ddo_code "
					+ "GROUP BY DDO4.DDO_CODE, ADMin4.LOC_NAME, fld4.LOC_NAME, ddo4.DDO_NAME), "
					+ "(select count(1) from org_post_mst post3, CMN_LOCATION_MST admin3, CMN_LOCATION_MST fld3, ORG_DDO_MST ddo3 "
					+ "where post3.LOCATION_CODE = ddo3.LOCATION_CODE and ddo3.DEPT_LOC_CODE = admin3.LOC_ID and ddo3.HOD_LOC_CODE = fld3.LOC_ID and post3.POST_ID <> ddo3.POST_ID "
					+ "and post3.POST_TYPE_LOOKUP_ID = 10001198129 "
					+ " and post3.activate_flag = 1 "
					+ "and ddo3.ddo_code = ddo.ddo_code "
					+ "GROUP BY DDO3.DDO_CODE, ADMin3.LOC_NAME, fld3.LOC_NAME, ddo3.DDO_NAME), "
					+ "(select count(1) from org_post_mst post3, CMN_LOCATION_MST admin3, CMN_LOCATION_MST fld3, ORG_DDO_MST ddo3 "
					+ "where post3.LOCATION_CODE = ddo3.LOCATION_CODE and ddo3.DEPT_LOC_CODE = admin3.LOC_ID and ddo3.HOD_LOC_CODE = fld3.LOC_ID and post3.POST_ID <> ddo3.POST_ID "
					+ "and post3.POST_TYPE_LOOKUP_ID = 10001198130 "
					+ " and post3.activate_flag = 1 "
					+ "and ddo3.ddo_code = ddo.ddo_code "
					+ "GROUP BY DDO3.DDO_CODE, ADMin3.LOC_NAME, fld3.LOC_NAME, ddo3.DDO_NAME), "
					+ "(SELECT count(1) FROM mst_dcps_emp dcps, ORG_DDO_MST ddo5 , ORG_EMP_MST org, ORG_USERPOST_RLT up5 "
					+ "where dcps.reg_status in (1,2) and dcps.DDO_CODE = ddo5.DDO_CODE and ddo5.DDO_CODE = ddo.DDO_CODE "
					+ "and dcps.ORG_EMP_MST_ID = org.EMP_ID and up5.USER_ID = org.USER_ID and up5.ACTIVATE_FLAG = 1), "
					+ "(SELECT count(1) FROM mst_dcps_emp dcps6, ORG_DDO_MST ddo6, ORG_EMP_MST org6, ORG_USERPOST_RLT up6 "
					+ "where dcps6.reg_status in (1,2) and dcps6.SUPER_ANN_DATE < sysdate and dcps6.DDO_CODE = ddo6.DDO_CODE and ddo6.DDO_CODE = ddo.DDO_CODE "
					+ "and dcps6.ORG_EMP_MST_ID = org6.EMP_ID and up6.USER_ID = org6.USER_ID and up6.ACTIVATE_FLAG = 1) "
					+ "FROM org_post_mst post, CMN_LOCATION_MST admin, CMN_LOCATION_MST fld, ORG_DDO_MST ddo, ORG_USERPOST_RLT up, ORG_POST_DETAILS_RLT dtls "
					+ "where post.LOCATION_CODE = ddo.LOCATION_CODE and ddo.DEPT_LOC_CODE = admin.LOC_ID and ddo.HOD_LOC_CODE = fld.LOC_ID and post.POST_ID <> ddo.POST_ID "
					+ "and post.POST_ID = dtls.POST_ID and post.POST_TYPE_LOOKUP_ID in (10001198129,10001198130) and up.POST_ID = post.POST_ID and up.ACTIVATE_FLAG = 1 and ddo.ddo_code = '"+lStrDdoCode + "'"
					+ " GROUP BY DDO.DDO_CODE, ADMin.LOC_NAME, fld.LOC_NAME, ddo.DDO_NAME ");
			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstPostData = lQuery.list();

		}catch(Exception e){
			logger.error("Error is : " + e, e);
			throw e;
		}
		
		return lLstPostData;
	}
	
	public Long getPkForPostDiffDtls(String lStrDdoCode) throws Exception
	{
		List lLstData = null;
		Long lLngDiffDtlsId = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT mstPostDiffDtlsId FROM MstPostDiffDtls ");
			lSBQuery.append("WHERE ddoCode = :ddoCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			
			lLstData = lQuery.list();
			if(lLstData != null && lLstData.size() > 0){
				lLngDiffDtlsId = (Long) lLstData.get(0);
			}
		}catch(Exception e){
			logger.error("Error is : " + e, e);
			throw e;
		}
		
		return lLngDiffDtlsId;
	}
	
	public List getEnteredData(String lStrDdoCode) throws Exception
	{
		List lLstData = null;
		Long lLngDiffDtlsId = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT totalPost, permntPostEmpAtch, tempPostEmpAtch, tempPostExpired, permntVacant, tempVacant, ");
			lSBQuery.append("rsnTotalPost, rsnPermntPostEmpAtch, rsnTempPostEmpAtch, rsnTempPostExpired, rsnPermntVacant, rsnTempVacant, activeEmp, rsnActEmployee ");
			lSBQuery.append("FROM MstPostDiffDtls ");
			lSBQuery.append("WHERE ddoCode = :ddoCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			
			lLstData = lQuery.list();			
		}catch(Exception e){
			logger.error("Error is : " + e, e);
			throw e;
		}
		
		return lLstData;
	}
	
	public void updatePrintFlag(String lStrDdoCode) throws Exception
	{
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE MstPostDiffDtls SET print = '1' WHERE ddoCode = :ddoCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lQuery.executeUpdate();
		}catch(Exception e){
			logger.error("Error is : " + e, e);
			throw e;
		}
	}
	
	public String chkForPrint(String lStrDdoCode) throws Exception
	{
		List lLstData = null;
		String lStrPrint = "";
		String lStrData = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT print FROM MstPostDiffDtls WHERE ddoCode = :ddoCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lLstData = lQuery.list();
			
			if(lLstData != null && lLstData.size() > 0){
				lStrPrint = lLstData.get(0).toString();
				
				if(lStrPrint.equals("0")){
					lStrData = "N";
				}else if(lStrPrint.equals("1")){
					lStrData = "Y";
				}
			}
		}catch(Exception e){
			logger.error("Error is : " + e, e);
			throw e;
		}
		
		return lStrData;
	}
}
