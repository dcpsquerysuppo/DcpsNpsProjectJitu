package com.tcs.sgv.dcps.TestWebService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class EmpDetailsWsDAOImpl extends GenericDaoHibernateImpl{
	public EmpDetailsWsDAOImpl(Class type, SessionFactory sessionFactory) 
	{
		super(type);
		setSessionFactory(sessionFactory);
	}

	
	
	 public List getEmpDetails(String sevarthId){
		 logger.info("inside getEmpDetails");
			Session hibSession = getSession();
			
			StringBuilder newQuery = new StringBuilder();


	         List empData=null;
			
	         newQuery.append("SELECT emp_name,dob,doj FROM mst_dcps_emp where SEVARTH_ID = '"+sevarthId+"' ");
	         Query query=hibSession.createSQLQuery(newQuery.toString());
	        logger.info("query getEmpDetails"+query);
	         empData=query.list();
			
			return empData;
	}

}
