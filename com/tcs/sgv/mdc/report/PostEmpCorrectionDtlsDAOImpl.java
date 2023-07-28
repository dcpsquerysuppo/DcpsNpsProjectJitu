package com.tcs.sgv.mdc.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class PostEmpCorrectionDtlsDAOImpl extends GenericDaoHibernateImpl implements PostEmpCorrectionDtlsDAO 
{
	
	Session ghibSession = null;
	public PostEmpCorrectionDtlsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	@Override
	public List getAllCorrectemployee() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("in get all emp list ");
		List   lLnaGenratedListMR = null;
			
		StringBuilder lSBQueryMR = new StringBuilder();
		lSBQueryMR.append("SELECT admin.LOC_id ,admin.LOC_NAME ADMIN_DEPT,fld.loc_name FIELD_DEPT, ddo.DDO_CODE, ddo.DDO_NAME, ddo.DDO_OFFICE,  ");
		lSBQueryMR.append(" (select count(1) from org_post_mst post2, CMN_LOCATION_MST admin2, CMN_LOCATION_MST fld2, ORG_DDO_MST ddo2, ORG_USERPOST_RLT up2 ");
		lSBQueryMR.append("  where post2.LOCATION_CODE = ddo2.LOCATION_CODE and ddo2.DEPT_LOC_CODE = admin2.LOC_ID and ddo2.HOD_LOC_CODE = fld2.LOC_ID and post2.POST_ID <> ddo2.POST_ID  ");
		lSBQueryMR.append(" and post2.POST_TYPE_LOOKUP_ID = 10001198129 and up2.POST_ID = post2.POST_ID and up2.ACTIVATE_FLAG = 1 and ");
		lSBQueryMR.append("  ddo2.ddo_code = ddo.ddo_code  GROUP BY ADMin2.LOC_NAME, Fld2.LOC_NAME) A1,  ");
		lSBQueryMR.append("  (select count(1) from org_post_mst post1, CMN_LOCATION_MST admin1, CMN_LOCATION_MST fld1, ORG_DDO_MST ddo1, ORG_USERPOST_RLT up1   ");
		lSBQueryMR.append("  where post1.LOCATION_CODE = ddo1.LOCATION_CODE and ddo1.DEPT_LOC_CODE = admin1.LOC_ID and ddo1.HOD_LOC_CODE = fld1.LOC_ID and post1.POST_ID <> ddo1.POST_ID   ");
		lSBQueryMR.append("  and post1.POST_TYPE_LOOKUP_ID = 10001198130 and up1.POST_ID = post1.POST_ID and up1.ACTIVATE_FLAG = 1 and post1.end_date > sysdate  ");
		lSBQueryMR.append("  and ddo1.ddo_code = ddo.ddo_code  GROUP BY ADMin1.LOC_NAME, Fld1.LOC_NAME) A2, pst.PERMNT_POST_EMP_ATCH B1, pst.TEMP_POST_EMP_ATCH B2   ");
		
		lSBQueryMR.append("  FROM org_post_mst post, CMN_LOCATION_MST admin, CMN_LOCATION_MST fld, ORG_DDO_MST ddo, ORG_USERPOST_RLT up, ORG_POST_DETAILS_RLT dtls, MST_POST_DIFF_DTLS pst   ");
		lSBQueryMR.append("  where post.LOCATION_CODE = ddo.LOCATION_CODE and ddo.DEPT_LOC_CODE = admin.LOC_ID and ddo.HOD_LOC_CODE = fld.LOC_ID and pst.DDO_CODE = ddo.DDO_CODE   ");
		lSBQueryMR.append("  and post.POST_ID <> ddo.POST_ID and post.POST_ID = dtls.POST_ID and   ");
		
		lSBQueryMR.append("  pst.PRINT = 1 and post.POST_TYPE_LOOKUP_ID in (10001198129,10001198130) and up.POST_ID = post.POST_ID and up.ACTIVATE_FLAG = 1   ");
		lSBQueryMR.append("  GROUP BY admin.LOC_id,ADMin.LOC_NAME, ddo.DDO_CODE,ddo.DDO_OFFICE,ddo.DDO_NAME, fld.LOC_NAME, pst.TEMP_POST_EMP_ATCH, pst.PERMNT_POST_EMP_ATCH   ");
		
			Query lQueryCA = ghibSession.createSQLQuery(lSBQueryMR.toString());
		
		lLnaGenratedListMR = lQueryCA.list();
		
		return lLnaGenratedListMR;
	}

	
	// get admin name-------------------------------------
  @Override
  public	List getAdminName() throws Exception{
		// TODO Auto-generated method stub
		
	  List lLstAdminName = null;
	  List<ComboValuesVO> lLstAlladminDept = new ArrayList<ComboValuesVO>();
	  Session ghibSession = getSession();
	  try{
	  
	  StringBuilder lSBQueryAD = new StringBuilder();
	  lSBQueryAD .append(" SELECT LOC_ID,LOC_NAME FROM CMN_LOCATION_MST    ");
	  
	  Query lQueryCA = ghibSession.createSQLQuery(lSBQueryAD.toString());		
	  lLstAdminName = lQueryCA.list();
	  
	  if(lLstAdminName != null && lLstAdminName.size() > 0){
		  Iterator it = lLstAdminName.iterator();
		  
		  ComboValuesVO cmbVO = new ComboValuesVO();
		  Object[] lObj = null;
		  while(it.hasNext()){
			  cmbVO = new ComboValuesVO();
			  lObj = (Object[]) it.next();
			  cmbVO.setId(lObj[0].toString());
			  cmbVO.setDesc(lObj[1].toString());
			  lLstAlladminDept.add(cmbVO);
		  			}
	  		}
	  }
	  catch(Exception e){
		  logger.error(" Error is : " + e, e);
		  throw e;
	  }
		return lLstAlladminDept;
	}
	
  public List getFieldbyAdmin(Long adminID) throws Exception{
		// TODO Auto-generated method stub
		
	  List lLstFieldName = null;
	  List<ComboValuesVO> lLstAllFieldDept = new ArrayList<ComboValuesVO>();
	  Session ghibSession = getSession();
	  try{
	  
	  StringBuilder lSBQueryFD = new StringBuilder();
	  lSBQueryFD .append(" SELECT LOC_NAME,LOC_ID FROM CMN_LOCATION_MST where PARENT_LOC_ID=(SELECT PARENT_LOC_ID FROM CMN_LOCATION_MST where LOC_ID  =:adminID) "); 
	  
	  Query lQueryCA = ghibSession.createSQLQuery(lSBQueryFD.toString());		
	  lLstFieldName = lQueryCA.list();
	  
	  if(lLstFieldName != null && lLstFieldName.size() > 0){
		  Iterator it = lLstFieldName.iterator();
		  
		  ComboValuesVO cmbVO = new ComboValuesVO();
		  Object[] lObj = null;
		  while(it.hasNext()){
			  cmbVO = new ComboValuesVO();
			  lObj = (Object[]) it.next();
			  cmbVO.setId(lObj[0].toString());
			  cmbVO.setDesc(lObj[2].toString());
			  lLstAllFieldDept.add(cmbVO);
		  			}
	  		}
	  }
	  catch(Exception e){
		  logger.error(" Error is : " + e, e);
		  throw e;
	  }
		return lLstAllFieldDept;
	}

 
  public List getTotalDDOCount() throws Exception{
		// TODO Auto-generated method stub
		
	  List lLstTotalDDOcount = null;
		  Session ghibSession = getSession();
	  try{
	  
	  StringBuilder lSBQueryFD = new StringBuilder();
	  lSBQueryFD .append("SELECT ADMIN.LOC_NAME, FIE.LOC_NAME, COUNT(DDO.DDO_CODE) FROM ORG_DDO_MST DDO  ");
	  lSBQueryFD .append("   INNER JOIN CMN_LOCATION_MST FIE ON FIE.LOC_ID = DDO.HOD_LOC_CODE ");
	  lSBQueryFD .append("  INNER JOIN CMN_LOCATION_MST ADMIN ON ADMIN.LOC_ID = DDO.DEPT_LOC_CODE ");
	  lSBQueryFD .append("  WHERE DDO.ACTIVATE_FLAG = 1 GROUP BY ADMIN.LOC_NAME, FIE.LOC_NAME " ) ;
  
	  Query lQueryCA = ghibSession.createSQLQuery(lSBQueryFD.toString());		
	  lLstTotalDDOcount = lQueryCA.list();
	  	 
	  }
	  catch(Exception e){
		  logger.error(" Error is : " + e, e);
		  throw e;
	  }
		return lLstTotalDDOcount;
	}


  public List getTotalCountWhoHaveEnter() throws Exception{
		// TODO Auto-generated method stub
		
	  List lLstTotalcount = null;
		  Session ghibSession = getSession();
	  try{
	  
	  StringBuilder lSBQueryFD = new StringBuilder();
	  lSBQueryFD .append("SELECT ADMIN.LOC_NAME, FIE.LOC_NAME,COUNT(DDO.DDO_CODE) DDO_COUNT FROM MST_POST_DIFF_DTLS pst  ");
	  lSBQueryFD .append("   INNER JOIN ORG_DDO_MST ddo on ddo.DDO_CODE = pst.DDO_CODE  ");
	  lSBQueryFD .append("  INNER JOIN CMN_LOCATION_MST FIE ON fie.LOC_ID = ddo.HOD_LOC_CODE   ");
	  lSBQueryFD .append("  INNER JOIN CMN_LOCATION_MST admin on admin.loc_ID = DDO.DEPT_LOC_CODE  " ) ;
	  lSBQueryFD .append("  WHERE PST.PRINT = 1 group by ADMIN.LOC_NAME, FIE.LOC_NAME  " ) ;

	  Query lQueryCA = ghibSession.createSQLQuery(lSBQueryFD.toString());		
	  lLstTotalcount = lQueryCA.list();
	  	 
	  }
	  catch(Exception e){
		  logger.error(" Error is : " + e, e);
		  throw e;
	  }
		return lLstTotalcount;
	}

  
  
	
	
  public List getFieldName() throws Exception{
		// TODO Auto-generated method stub
		
	  List lLstFieldName = null;
	  List<ComboValuesVO> lLstAllFieldDept = new ArrayList<ComboValuesVO>();
	  Session ghibSession = getSession();
	  try{
	  
	  StringBuilder lSBQueryFD = new StringBuilder();
	  lSBQueryFD .append(" SELECT admin.LOC_ID,fld.LOC_ID, fld.LOC_NAME FROM org_post_mst post, CMN_LOCATION_MST admin, CMN_LOCATION_MST fld, ORG_DDO_MST ddo, ORG_USERPOST_RLT up, ORG_POST_DETAILS_RLT dtls, MST_POST_DIFF_DTLS pst   ");
	  lSBQueryFD .append(" where post.LOCATION_CODE = ddo.LOCATION_CODE  and ddo.DEPT_LOC_CODE = admin.LOC_ID 	  and ddo.HOD_LOC_CODE = fld.LOC_ID 	  and pst.DDO_CODE = ddo.DDO_CODE   ");
	  lSBQueryFD .append("  and post.POST_ID <> ddo.POST_ID  		and post.POST_ID = dtls.POST_ID  		and pst.PRINT = 1  		and post.POST_TYPE_LOOKUP_ID in (10001198129,10001198130)  ");
	  lSBQueryFD .append(" and up.POST_ID = post.POST_ID and up.ACTIVATE_FLAG = 1   GROUP BY fld.LOC_NAME ,fld.LOC_ID,admin.LOC_ID    ");
	  
	  Query lQueryCA = ghibSession.createSQLQuery(lSBQueryFD.toString());		
	  lLstFieldName = lQueryCA.list();
	  
	  if(lLstFieldName != null && lLstFieldName.size() > 0){
		  Iterator it = lLstFieldName.iterator();
		  
		  ComboValuesVO cmbVO = new ComboValuesVO();
		  Object[] lObj = null;
		  while(it.hasNext()){
			  cmbVO = new ComboValuesVO();
			  lObj = (Object[]) it.next();
			  cmbVO.setId(lObj[0].toString());
			  cmbVO.setDesc(lObj[2].toString());
			  lLstAllFieldDept.add(cmbVO);
		  			}
	  		}
	  }
	  catch(Exception e){
		  logger.error(" Error is : " + e, e);
		  throw e;
	  }
		return lLstAllFieldDept;
	}
	
 /* 
  public List  getFieldDeptName(String Loc_id) throws Exception{
		// TODO Auto-generated method stub
		
	  List lLstFielddeptName = null;
	  List<ComboValuesVO> lLstAllFieldDept1 = new ArrayList<ComboValuesVO>();
	  Session ghibSession = getSession();
	  try{
	  
	  StringBuilder lSBQueryFD = new StringBuilder();
	  lSBQueryFD.append("SELECT LOC_ID,LOC_NAME FROM CMN_LOCATION_MST where PARENT_LOC_ID= :Loc_id");
	  lSBQueryFD .append(" SELECT admin.LOC_ID,fld.LOC_ID, fld.LOC_NAME FROM org_post_mst post, CMN_LOCATION_MST admin, CMN_LOCATION_MST fld, ORG_DDO_MST ddo, ORG_USERPOST_RLT up, ORG_POST_DETAILS_RLT dtls, MST_POST_DIFF_DTLS pst   ");
	  lSBQueryFD .append(" where post.LOCATION_CODE = ddo.LOCATION_CODE  and ddo.DEPT_LOC_CODE = admin.LOC_ID 	  and ddo.HOD_LOC_CODE = fld.LOC_ID 	  and pst.DDO_CODE = ddo.DDO_CODE   ");
	  lSBQueryFD .append("  and post.POST_ID <> ddo.POST_ID  		and post.POST_ID = dtls.POST_ID  		and pst.PRINT = 1  		and post.POST_TYPE_LOOKUP_ID in (10001198129,10001198130)  ");
	  lSBQueryFD .append(" and up.POST_ID = post.POST_ID and up.ACTIVATE_FLAG = 1   GROUP BY fld.LOC_NAME ,fld.LOC_ID,admin.LOC_ID    ");
	  
	  Query lQueryCA = ghibSession.createSQLQuery(lSBQueryFD.toString());		
	  lLstFielddeptName = lQueryCA.list();
	  
	  if(lLstFielddeptName != null && lLstFielddeptName.size() > 0){
		  Iterator it = lLstFielddeptName.iterator();
		  
		  ComboValuesVO cmbVO = new ComboValuesVO();
		  Object[] lObj = null;
		  while(it.hasNext()){
			  cmbVO = new ComboValuesVO();
			  lObj = (Object[]) it.next();
			  cmbVO.setId(lObj[0].toString());
			  cmbVO.setDesc(lObj[2].toString());
			  lLstAllFieldDept1.add(cmbVO);
		  			}
	  		}
	  }
	  catch(Exception e){
		  logger.error(" Error is : " + e, e);
		  throw e;
	  }
		return lLstAllFieldDept1;
	}
	
  
  public List  getAdminWiseList() throws Exception{
	  
	  
	  
	return null;
  }*/
  
 
	

		}
