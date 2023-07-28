package com.tcs.sgv.common.service;

import java.util.List;

import org.directwebremoting.util.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class LoggedUserRoleIdDAOImpl extends GenericDaoHibernateImpl {

	Session hibSession=null; 
	SessionFactory factory=null;//This is hibernate session
	Logger logger=null;
	StringBuilder strQuery= null;
		
	public LoggedUserRoleIdDAOImpl(Class type,SessionFactory sessionFactory) {
		super(type);
		factory=sessionFactory;
		hibSession=factory.getCurrentSession();
		logger = Logger.getLogger(this.getClass());
		strQuery = new StringBuilder();
		// TODO Auto-generated constructor stub
	}	
	public Object getLoggedUserRoleIdList(Long gLngPostId) 
	
	{	
		List roleidlist = null;
		strQuery.setLength(0);
        strQuery.append("SELECT role_id FROM ACL_POSTROLE_RLT where post_id = "+gLngPostId+"");
        Query query = hibSession.createSQLQuery(strQuery.toString());
        roleidlist =  query.list();
        return roleidlist;	
				
	}

}
