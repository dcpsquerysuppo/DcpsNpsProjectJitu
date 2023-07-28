package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class AdminPostDtlsDAOImpl extends GenericDaoHibernateImpl implements AdminPostDtlsDAO{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;
	
	public AdminPostDtlsDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = getSession();
	}

	
	public List<ComboValuesVO> getFieldDeptFromAdminDeptCode(Long lLngAdminDeptCode) {
		
		
		ArrayList<ComboValuesVO> lLstReturnList = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		
		lStrQuery.append(" SELECT clm.locId,clm.locName FROM CmnLocationMst clm ");
		//lStrQuery.append(" WHERE clm.parentLocId = :AdminDeptCode  order by clm.locName");	
		lStrQuery.append(" WHERE clm.locId = :AdminDeptCode  order by clm.locName");
		StringBuilder sb = new StringBuilder();
		sb.append(lStrQuery.toString());
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("AdminDeptCode", lLngAdminDeptCode);
		
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;

		if (lLstResult != null && lLstResult.size() != 0) {			
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString().replaceAll("&", "And"));
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {			
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		return lLstReturnList;
	}


	
	public List<ComboValuesVO> getDdoListFromFieldDept(String lStrFieldDeptCode) {
		ArrayList<ComboValuesVO> lLstReturnList = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		
		lStrQuery.append(" SELECT ddoCode,ddoName FROM OrgDdoMst ");
		lStrQuery.append(" WHERE hodLocCode = :FieldDeptCode  order by ddoCode");
		
		Query selectQuery = ghibSession.createQuery(lStrQuery.toString());
		selectQuery.setParameter("FieldDeptCode", lStrFieldDeptCode);
		
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		String lStrDDOCodeAndName = "";

		lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("-- Select --");
		lLstReturnList.add(lObjComboValuesVO);
		if (lLstResult != null && lLstResult.size() != 0) {			
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lStrDDOCodeAndName = "("+obj[0].toString()+") " +obj[1].toString().replaceAll("&", "And");
				lObjComboValuesVO.setDesc(lStrDDOCodeAndName);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} 
		return lLstReturnList;
	}


	
	public String getLocationCodeForDDO(String lStrDDOCode) {
		
		String lStrLocationCode = "";
		StringBuilder lStrQuery = new StringBuilder();
		
		lStrQuery.append(" SELECT locationCode FROM OrgDdoMst ");
		lStrQuery.append(" WHERE ddoCode = :DDOCode  ");	
		
		Query selectQuery = ghibSession.createQuery(lStrQuery.toString());
		selectQuery.setParameter("DDOCode", lStrDDOCode);
		
		lStrLocationCode = (String) selectQuery.uniqueResult();
		
		return lStrLocationCode;
		
	}
	 public List getAllBillsFromLocation(Long locId)
	 {
	       
		 StringBuilder lStrQuery = new StringBuilder();
		 lStrQuery.append(" from MstDcpsBillGroup where LocId= :locId and (billDeleted is null or billDeleted <> 'Y') ");
		 lStrQuery.append(" and (billDcps is null or billDcps <> 'Y') and subBGOrNot = 0 ");
	     
		 Query query = ghibSession.createQuery(lStrQuery.toString());
		 query.setParameter("locId", locId);
	     List resultList=query.list();
	     return resultList;	   
	 }
	 public List getPostNameForDisplay(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn ){
			List postNameList = new ArrayList();
			Session hibSession = getSession();
			StringBuffer sb = new StringBuffer();
			sb.append("  select pd.post_name,pd.post_id,(select concat(concat(concat(o.emp_fname, ' '), concat(o.emp_mname, ' ')),concat( o.emp_lname,' ')) from org_emp_mst o, org_userpost_rlt up ");
			//sb.append("  select pd.post_name,pd.post_id,(select concat(concat(concat(o.emp_fname, ' '), concat(o.emp_mname, ' ')),concat( o.emp_lname,' ')) from org_emp_mst o, org_userpost_rlt up, org_post_mst pm ");
			sb.append("  where o.lang_id = 1 and o.user_id = up.user_id and up.post_id = pd.post_id and up.end_date is null and up.activate_flag = 1) ");
			sb.append(" ,ds.dsgn_name , P.PSR_NO ");
			if(BillNo != null && !(BillNo.trim()).equals("")){			        
				sb.append("  , mp.DESCRIPTION from org_post_details_rlt pd, org_designation_mst ds, HR_PAY_POST_PSR_MPG p left outer join ");				
				sb.append(" mst_dcps_bill_group mp on p.bill_no = mp.BILL_GROUP_ID "); 
			}
			else
			{
				sb.append(",(select mp.DESCRIPTION  from mst_dcps_bill_group mp where  p.bill_no = mp.BILL_GROUP_ID) BillNo" );
				sb.append(",org.post_type_lookup_id,cmn.lookup_name,org.START_DATE,org.END_DATE,o.ORDER_NAME,o.ORDER_DATE" );
				sb.append(" from org_post_details_rlt pd, org_designation_mst ds,org_post_mst org");
				sb.append(",cmn_lookup_mst cmn,HR_PAY_POST_PSR_MPG p,HR_PAY_ORDER_MST o,HR_PAY_ORDER_HEAD_MPG h,HR_PAY_ORDER_HEAD_POST_MPG op ");
			}
			sb.append(" ,ORG_DDO_MST ddo "); // added by Mumb dev
			sb.append("  where pd.loc_id = '" + locId +"' and P.POST_ID = PD.POST_ID and  pd.dsgn_id = ds.dsgn_id and ds.lang_id = 1  " );
			if(PsrNo != null && !(PsrNo.trim()).equals(""))
				sb.append("  and p.psr_no = " + PsrNo );
			else if(BillNo != null && !(BillNo.trim()).equals(""))
				sb.append("  and mp.bill_no  = " + BillNo );
			else if(Dsgn != null && !(Dsgn.trim()).equals(""))
				sb.append("  and  upper(ds.dsgn_name) like  upper('%" + Dsgn+ "%')  ");
			else
				sb.append("  and  upper(pd.post_name) like  upper('%" + lPostName+ "%') ");   
			sb.append(" and pd.post_id=org.post_id ");
			sb.append(" and cmn.lookup_id=org.post_type_lookup_id and org.activate_flag=1 ");
			sb.append(" and op.POST_ID = pd.POST_ID and h.ORDER_HEAD_ID = op.ORDER_HEAD_ID and o.ORDER_ID = h.ORDER_ID ");
			//added by shailesh
			sb.append(" and org.ACTIVATE_FLAG=1 and org.POST_TYPE_LOOKUP_ID  in  (10001198130,10001198129) ");
			sb.append(" and (org.END_DATE > sysdate or org.END_DATE is null) ");			
			sb.append(" and org.post_id <> ddo.post_id and ddo.LOCATION_CODE =  "+locId);
			sb.append("   order by pd.CREATED_DATE desc  ");
			
		   
		    Query query = hibSession.createSQLQuery(sb.toString());
		    
		    logger.info(sb.toString());
		    postNameList = query.list();
		    
			return postNameList;
		}
	 
		public List getPostNameForDisplayThruEmpId(Long locId, String empId, Long langId)
		{
			List postNameList = new ArrayList();
			Session hibSession = getSession();
			StringBuffer sb = new StringBuffer();
			sb.append(" select pd.post_name,  pd.post_id, concat(concat(concat(o.emp_fname, ' '), concat(o.emp_mname, ' ')), concat(o.emp_lname, ' ')), ds.dsgn_name, p.psr_no, (select mp.bill_no  from hr_pay_bill_subhead_mpg mp  ");
			sb.append(" where  p.bill_no = mp.bill_subhead_id) BillNo  from org_emp_mst o,  org_userpost_rlt  up,  HR_PAY_POST_PSR_MPG p , org_post_details_rlt    pd, org_designation_mst     ds  where o.lang_id = 1 and o.user_id = up.user_id  ");
			sb.append(" and up.post_id = pd.post_id and pd.post_id = p.post_id and  pd.loc_id = p.loc_id and up.activate_flag=1");
			sb.append(" and o.emp_id = "+ empId +" and  pd.loc_id = " +locId + " and pd.dsgn_id = ds.dsgn_id and ds.lang_id = " + langId + " order by pd.post_name ");
		
		    Query query = hibSession.createSQLQuery(sb.toString());
		    postNameList = query.list();
		 	return postNameList;
			
		}	
		
		public List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn,Long designationId,Long BillGroupId)
		{
			List postNameList = new ArrayList();
			Session hibSession = getSession();
			StringBuffer sb = new StringBuffer();
			sb.append("  select pd.post_name,pd.post_id,(select concat(concat(concat(o.emp_fname, ' '), concat(o.emp_mname, ' ')),concat( o.emp_lname,' ')) from org_emp_mst o, org_userpost_rlt up ");
			sb.append("  where  o.lang_id = 1 and o.user_id = up.user_id and up.post_id = pd.post_id and up.end_date is null and up.activate_flag = 1),ds.dsgn_name , P.PSR_NO ");
			sb.append("  , (select mp.DESCRIPTION  from mst_dcps_bill_group mp where  p.bill_no = mp.BILL_GROUP_ID) BillNo ");			
			sb.append(",org.post_type_lookup_id,cmn.lookup_name,org.START_DATE,org.END_DATE,o.ORDER_NAME,o.ORDER_DATE" );
			sb.append(" from org_post_details_rlt pd, org_designation_mst ds,org_post_mst org");
			sb.append(",cmn_lookup_mst cmn,HR_PAY_POST_PSR_MPG p,HR_PAY_ORDER_MST o,HR_PAY_ORDER_HEAD_MPG h,HR_PAY_ORDER_HEAD_POST_MPG op ");
			if(BillGroupId>0)
			{
				sb.append(" ,mst_dcps_bill_group bill");
			}
			sb.append(" , ORG_DDO_MST ddo "); // added by Mumb dev
			sb.append("  where pd.loc_id = '" + locId +"' and P.POST_ID = PD.POST_ID and  pd.dsgn_id = ds.dsgn_id and ds.lang_id = 1  " );
		
			sb.append(" and org.post_id = pd.post_id and org.location_code = pd.loc_id and org.activate_flag = 1 ");
			sb.append("  and  upper(pd.post_name) like  upper('%" + lPostName+ "%') ");   
			
			if(designationId>0)
			{
				sb.append(" and ds.dsgn_id="+designationId+" ");
			}
			if(BillGroupId>0)
			{
				sb.append(" and bill.BILL_GROUP_ID="+BillGroupId+" and bill.BILL_GROUP_ID=p.bill_no");
			}
			
			sb.append(" and pd.post_id=org.post_id ");
			sb.append(" and cmn.lookup_id=org.post_type_lookup_id ");
			sb.append(" and op.POST_ID = pd.POST_ID and h.ORDER_HEAD_ID = op.ORDER_HEAD_ID and o.ORDER_ID = h.ORDER_ID ");
			//added by shailesh
			sb.append(" and org.ACTIVATE_FLAG=1 and org.POST_TYPE_LOOKUP_ID  in  (10001198130,10001198129) ");
			sb.append(" and (org.END_DATE > sysdate or org.END_DATE is null) ");
			sb.append(" and org.post_id <> ddo.post_id and ddo.LOCATION_CODE =  "+locId);
			sb.append("   order by pd.post_name  ");
			
		    Query query = hibSession.createSQLQuery(sb.toString());
		    postNameList = query.list();
		    return postNameList;
		}
		
		public List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn )
		{
			List postNameList = new ArrayList();
			Session hibSession = getSession();
			StringBuffer sb = new StringBuffer();
			sb.append("  select pd.post_name,pd.post_id,(select concat(concat(concat(o.emp_fname, ' '), concat(o.emp_mname, ' ')),concat( o.emp_lname,' ')) from org_emp_mst o, org_userpost_rlt up ");
			sb.append("  where  o.lang_id = 1 and o.user_id = up.user_id and up.post_id = pd.post_id and up.end_date is null and up.activate_flag = 1),ds.dsgn_name , P.PSR_NO ");
			sb.append("  , (select mp.DESCRIPTION  from mst_dcps_bill_group mp where  p.bill_no = mp.BILL_GROUP_ID) BillNo " );			
			sb.append(",org.post_type_lookup_id,cmn.lookup_name,org.START_DATE,org.END_DATE,o.ORDER_NAME,o.ORDER_DATE" );
			sb.append(" from org_post_details_rlt pd, org_designation_mst ds,org_post_mst org");
			sb.append(",cmn_lookup_mst cmn,HR_PAY_POST_PSR_MPG p,HR_PAY_ORDER_MST o,HR_PAY_ORDER_HEAD_MPG h,HR_PAY_ORDER_HEAD_POST_MPG op ");
			sb.append(" , ORG_DDO_MST ddo ");
			sb.append("  where pd.loc_id = '" + locId +"' and P.POST_ID = PD.POST_ID and  pd.dsgn_id = ds.dsgn_id and ds.lang_id = 1  " );
			sb.append("  and  upper(pd.post_name) like  upper('%" + lPostName+ "%') ");   
			sb.append(" and pd.post_id=org.post_id ");
			sb.append(" and cmn.lookup_id=org.post_type_lookup_id ");
			//added by shailesh
			sb.append(" and org.ACTIVATE_FLAG=1 and org.POST_TYPE_LOOKUP_ID  in  (10001198130,10001198129) ");
			sb.append(" and (org.END_DATE > sysdate or org.END_DATE is null) ");
			sb.append(" and org.post_id <> ddo.post_id  and ddo.LOCATION_CODE =  "+locId);
			sb.append(" and op.POST_ID = pd.POST_ID and h.ORDER_HEAD_ID = op.ORDER_HEAD_ID and o.ORDER_ID = h.ORDER_ID ");
			
			//sb.append("   order by pd.post_name  ");
			sb.append("   order by pd.CREATED_DATE desc, org.post_type_lookup_id   ");
			
			logger.info("The Query String is:-"+sb.toString());
			logger.info("The Query lPostName is:-"+lPostName);
			
			
		    Query query = hibSession.createSQLQuery(sb.toString());
		    postNameList = query.list();
		    logger.info("Parent Post List size is:-"+postNameList.size());
		    logger.info("Parent Post List size is:-"+postNameList.listIterator());
			return postNameList;
		}


		
		public String getOfficeCityClassAndHra(Long lLngOfficeId) {
			
			String lStrOfficeCityClass = "";
			
			String HQL_QUERY = "select dcpsDdoOfficeCityClass from DdoOffice where dcpsDdoOfficeIdPk = :OfficeId";
		    Query query = ghibSession.createQuery(HQL_QUERY);
		    query.setParameter("OfficeId", lLngOfficeId);
		    List resultList = query.list();
		    
		    if(resultList != null && !resultList.isEmpty())
		    	lStrOfficeCityClass = (String) resultList.get(0);
		    		
		 	return lStrOfficeCityClass;
		}
}
