package com.tcs.sgv.dcps.service;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.BankBranchMstDaoImpl;
import com.tcs.sgv.pensionpay.dao.BankBranchMappingDAOImpl;
import java.util.*;

public class TestDaoImpl extends GenericDaoHibernateImpl{
	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(BankBranchMstDaoImpl.class);

	//private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public TestDaoImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}
	
	public String getTargetDDo(String ddoCode){
		  Session hibSession = getSession();
			StringBuffer sb = new StringBuffer();
			ddoCode=(ddoCode!=null && ddoCode!="" )?ddoCode:"0";
			sb.append("SELECT ddo_code||' , '||DDO_NAME FROM org_ddo_mst where ddo_code=");
			sb.append(ddoCode);
			Query sqllquery=hibSession.createSQLQuery(sb.toString());
			logger.info("Employee loan data list"+ sqllquery.toString());
			Object result=sqllquery.uniqueResult();
		    String tragetDDoCode=(result!=null && result!="")?result.toString():"N.A";
		    logger.info("Employee loan data list"+ tragetDDoCode);
			return tragetDDoCode;		
	  }
	
	public boolean updateEmp(String ddoCode){
		  Session hibSession = getSession();
			StringBuffer sb = new StringBuffer();
			
			sb.append("update mst_dcps_emp set emp_name = 'test123' where DCPS_EMP_ID = 992000032123");
			sb.append(ddoCode);
			int sqllquery=hibSession.createSQLQuery(sb.toString()).executeUpdate();
			
			return true;		
	  }
}
