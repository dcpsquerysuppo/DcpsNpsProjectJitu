package com.tcs.sgv.common.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class UserConfigTODAOImpl extends GenericDaoHibernateImpl{
	
	SessionFactory sessionFactory = null;
	
	public UserConfigTODAOImpl(Class type ,SessionFactory sessionFactory) 
	{
		super(type);
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public String getLocationName(String lStrLocCode) throws Exception
	{
		String lStrLocName = "";
		List lLstResData = null;
		Session ghibSession = getSession();
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT locName FROM CmnLocationMst WHERE locationCode = :locCode \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locCode", lStrLocCode);
			lLstResData = lQuery.list();
			
			if(lLstResData.size() > 0){
				lStrLocName = (String) lLstResData.get(0);
			}
		} catch (Exception e) {			
			logger.error("Error is : " + e, e);
			throw e;
		}
		return lStrLocName;
	}

}
