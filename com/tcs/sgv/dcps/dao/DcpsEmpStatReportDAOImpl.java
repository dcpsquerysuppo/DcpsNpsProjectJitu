package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.DdoRegDao;


public class DcpsEmpStatReportDAOImpl extends GenericDaoHibernateImpl implements DcpsEmpStatReportDao{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
	.getBundle("resources/pensionproc/PensionCaseConstants");

	public DcpsEmpStatReportDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);


}


@Override
public List getDataOnLoad() {
	List lLstResult = null;
	//Map fieldMap=new HashMap();
	StringBuffer lSBQuery = new StringBuffer();
	
	try {		
		
		logger.info("Enter in getDataOnLoad ");
		
		
		lSBQuery.append(" select loc.loc_id,loc.loc_name,count(DCPS_EMP_ID) as total_dcps_emp, count(case when emp.doj < '2015-03-31' then 1 else null end) as doj_lt_2015_3_31, count(case when emp.doj > '2015-04-01' then 1 else null end) as doj_gt_2015_4_1,count(case when emp.doj < '2015-03-31' and emp.pran_no is not null then 1 else null end) as doj_lt_2015_3_31_and_pran_not_null,  count(case when emp.doj > '2015-04-01' and emp.pran_no is not null then 1 else null end) as doj_gt_2015_4_1_and_pran_not_null from mst_dcps_emp emp ");
		lSBQuery.append(" inner join cmn_location_mst loc on substr(loc.loc_id,1,2) = substr(emp.ddo_code,1,2) and loc.department_id=100003 ");
		lSBQuery.append(" where emp.reg_status=1 and emp.dcps_or_gpf='Y' and emp.form_status=1 and emp.emp_servend_dt > sysdate");
		lSBQuery.append(" group by loc.loc_id,loc.loc_name ");
		lSBQuery.append(" order by loc.loc_id ");
		
	
	
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

	logger.info("==> getDataOnLoad query :: "+lQuery);
	logger.info("==> getDataOnLoad query :: "+lSBQuery.toString());
	  lLstResult = lQuery.list();
			
	  logger.info(" Total List size of data is  ******.. "+lLstResult.size());	   	    	 			 			 		
	
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getDataOnLoad : " + e, e);
	}
	return lLstResult;
}

@Override
public List getDataOnLoadForTo(String strLocationCode) {
	List lLstResult = null;
	String loc_id=strLocationCode.substring(0,2);
	gLogger.info("loc_id######"+loc_id);
	//Map fieldMap=new HashMap();
	StringBuffer lSBQuery = new StringBuffer();
	
	try {		
		
		logger.info("Enter in getDataOnLoadForTo ");
		
		
		
		lSBQuery.append(" select loc.loc_id,loc.loc_name,count(DCPS_EMP_ID) as total_dcps_emp, count(case when emp.doj < '2015-03-31' then 1 else null end) as doj_lt_2015_3_31, count(case when emp.doj > '2015-04-01' then 1 else null end) as doj_gt_2015_4_1,count(case when emp.doj < '2015-03-31' and emp.pran_no is not null then 1 else null end) as doj_lt_2015_3_31_and_pran_not_null,  count(case when emp.doj > '2015-04-01' and emp.pran_no is not null then 1 else null end) as doj_gt_2015_4_1_and_pran_not_null from mst_dcps_emp emp  "); 
		lSBQuery.append(" inner join cmn_location_mst loc on substr(loc.loc_id,1,4) = substr(emp.ddo_code,1,4) ");
		lSBQuery.append(" where emp.reg_status=1 and emp.dcps_or_gpf='Y' and loc.loc_id  like '"+loc_id+"%' ");
		lSBQuery.append(" group by loc.loc_id,loc.loc_name  ");
		lSBQuery.append(" order by loc.loc_id  ");
				
	
	
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

	logger.info("==> getDataOnLoadForTo query :: "+lQuery);
	logger.info("==> getDataOnLoadForTo query :: "+lSBQuery.toString());
	  lLstResult = lQuery.list();
			
	  logger.info(" Total List size of data is  ******.. "+lLstResult.size());	   	    	 			 			 		
	
		}
	catch (Exception e) {
		logger.info("Exception occurred while retrieving data From getDataOnLoad : " + e, e);
	}
	return lLstResult;
}



}




