package com.tcs.sgv.common.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class DashBoardDao extends GenericDaoHibernateImpl {
	private Session ghibSession = null;
	private final Logger gLogger = Logger.getLogger(getClass());

	public DashBoardDao(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}
	StringBuilder Strbld = new StringBuilder();
	public Map getPayBill() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		   LocalDateTime now = LocalDateTime.now();
		Map<Object,Object> map =new HashMap<Object, Object>();
		
		List<Map<Object,Object>> list1 = new  ArrayList<Map<Object,Object>>();
		List list = new ArrayList<>();
		Object[] lObj = null;
		
		/*Strbld.append("SELECT "); 
		Strbld.append("count(*), "); 
		Strbld.append("case WHEN approve_flag='0' THEN 'TOTAL_GENARATED_BILL' WHEN approve_flag='5' THEN 'TOTAL_BDS_GENERATED' WHEN approve_flag='1' THEN 'TOTAL_BIIL_LOCK' END "); 
		Strbld.append("FROM paybill_head_mpg "); 
		Strbld.append("where approve_flag in (0,1,5) "); 
		Strbld.append("group by approve_flag ");*/
		Strbld.append("SELECT "); 
		Strbld.append("count(*), "); 
		Strbld.append("case WHEN approve_flag='0' THEN 'TOTAL_GENARATED_BILL' WHEN approve_flag='5' THEN 'TOTAL_BDS_GENERATED' WHEN approve_flag='1' THEN 'TOTAL_BIIL_LOCK' END "); 
		Strbld.append("FROM paybill_head_mpg "); 
		Strbld.append("where approve_flag in (0,1,5) and CREATED_DATE like'"+dtf.format(now)+"%' "); 
//		Strbld.append("where approve_flag in (0,1,5)"); 
		Strbld.append("group by approve_flag "); 

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		list = lQuery.list();

		for (Iterator it = list.iterator(); it.hasNext();) {
			int i = 0;
			
			lObj = (Object[]) it.next();
			map.put(lObj[i],lObj[i+1]);			 
		}
		return map;
	}
	public Map getPayBill(String month, String year) {
		Map<Object,Object> map =new HashMap<Object, Object>();
		
		List<Map<Object,Object>> list1 = new  ArrayList<Map<Object,Object>>();
		List list = new ArrayList<>();
		Object[] lObj = null;
		Strbld.append("SELECT "); 
		Strbld.append("count(*), "); 
		Strbld.append("case WHEN approve_flag='0' THEN 'TOTAL_GENARATED_BILL' WHEN approve_flag='5' THEN 'TOTAL_BDS_GENERATED' WHEN approve_flag='1' THEN 'TOTAL_BIIL_LOCK' END "); 
		Strbld.append("FROM paybill_head_mpg "); 
		Strbld.append("where paybill_year='"+year+"' and PAYBILL_MONTH='"+month+"'"); 
		Strbld.append(" and approve_flag in (0,1,5) "); 
		Strbld.append(" group by approve_flag ");

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		list = lQuery.list();

		for (Iterator it = list.iterator(); it.hasNext();) {
			int i = 0;
			
			lObj = (Object[]) it.next();
			map.put(lObj[i],lObj[i+1]);			 
		}
		return map;
	}
	
	public Map getPayBillfinancial(String year) {
		Map<Object,Object> map =new HashMap<Object, Object>();
		
		List<Map<Object,Object>> list1 = new  ArrayList<Map<Object,Object>>();
		List list = new ArrayList<>();
		Object[] lObj = null;
		
		Strbld.append("SELECT "); 
		Strbld.append("count(*), "); 
		Strbld.append("case WHEN approve_flag='0' THEN 'TOTAL_GENARATED_BILL' WHEN approve_flag='5' THEN 'TOTAL_BDS_GENERATED' WHEN approve_flag='1' THEN 'TOTAL_BIIL_LOCK' END "); 
		Strbld.append("FROM paybill_head_mpg "); 
		Strbld.append("where ((paybill_year='"+year.substring(0,4)+"' and PAYBILL_MONTH in(4,5,6,7,8,9,10,11,12))OR (paybill_year='"+year.substring(5,9)+"' and PAYBILL_MONTH in(1,2,3))) "); 
		Strbld.append("and approve_flag in (0,1,5) "); 
		Strbld.append("group by approve_flag ");
		
		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		list = lQuery.list();

		for (Iterator it = list.iterator(); it.hasNext();) {
			int i = 0;
			
			lObj = (Object[]) it.next();
			map.put(lObj[i],lObj[i+1]);			 
		}
		return map;
	}
	
	
	//group wise Exp.
	public Map groupWiseYealyExp() {
		Map<Object,Object> map =new HashMap<Object, Object>();
		
		List<Map<Object,Object>> list1 = new  ArrayList<Map<Object,Object>>();
		List list = new ArrayList<>();
		Object[] lObj = null;

		Strbld.append("	SELECT grade.GRADE_NAME,sum(pay.GROSS_NEW) as total_expenditure  from ORG_EMP_MST org "); 
		Strbld.append("     inner join ORG_GRADE_MST grade on grade.GRADE_ID=org.GRADE_ID "); 
		Strbld.append("     inner join MST_DCPS_EMP emp on emp.ORG_EMP_MST_ID = org.EMP_ID "); 
		Strbld.append("     inner join org_ddo_mst ddo on ddo.ddo_code=emp.ddo_code "); 
		Strbld.append("     inner join hr_eis_emp_mst eis on eis.EMP_MPG_ID = emp.ORG_EMP_MST_ID "); 
		Strbld.append("     inner join hr_pay_paybill pay on pay.emp_id = eis.EMP_ID "); 
		Strbld.append("     inner join PAYBILL_HEAD_MPG head on head.PAYBILL_ID = pay.PAYBILL_GRP_ID "); 
		Strbld.append("     inner join CMN_LOCATION_MST loc on loc.location_code = ddo.dept_loc_code "); 
		Strbld.append("     inner join CMN_LOCATION_MST loc1 on loc1.location_code = ddo.hod_loc_code  where "); 
		Strbld.append("     ((head.PAYBILL_YEAR = 2020 AND HEAD.PAYBILL_MONTH > 3) OR (head.PAYBILL_YEAR = 2021 AND  head.PAYBILL_MONTH < 4 )) and "); 
		Strbld.append("	head.LOC_ID <> 380001 and head.APPROVE_FLAG in (1,5) "); 
		Strbld.append("   	GROUP BY grade.GRADE_NAME WITH UR ");

		SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

		list = lQuery.list();

		for (Iterator it = list.iterator(); it.hasNext();) {
			int i = 0;
			
			lObj = (Object[]) it.next();
			map.put(lObj[i+1],lObj[i]);			 
		}
		return map;
	}
	
	public List getMonths() {

		String query = "select monthId,monthName from SgvaMonthMst where monthId < 13";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;

		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		return lLstReturnList;
	}

	 public List getFinyears() {
		 Calendar c = Calendar.getInstance();
		    int year = c.get(Calendar.YEAR);
		    int month = c.get(Calendar.MONTH);
		    if(4>month+1){
		        year = year-1;
		    }
			String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '"+year+"' order by finYearCode ASC";
			List<Object> lLstReturnList = null;
			StringBuilder sb = new StringBuilder();
			sb.append(query);
			Query selectQuery = ghibSession.createQuery(sb.toString());
			List lLstResult = selectQuery.list();
			ComboValuesVO lObjComboValuesVO = null;

			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
			return lLstReturnList;
		}
	 public List getYear() {
		 Calendar c = Calendar.getInstance();
		    int year = c.get(Calendar.YEAR);
		    
			List list = new ArrayList<>();
			Object[] lObj = null;
			Strbld.append("SELECT FIN_YEAR_CODE FROM SGVC_FIN_YEAR_MST  WHERE FIN_YEAR_CODE BETWEEN '2007' and '"+year+"' ");


			SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());

			list = lQuery.list();

			
			return list;
		}
}