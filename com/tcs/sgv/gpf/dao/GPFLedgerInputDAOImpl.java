/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 12, 2011		Chudasama Jayraj								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description - 
 *
 *
 * @author Chudasama Jayraj
 * @version 0.1
 * @since JDK 5.0
 * Dec 12, 2011
 */
public class GPFLedgerInputDAOImpl extends GenericDaoHibernateImpl implements GPFLedgerInputDAO
{
	Session ghibSession = null;
	public GPFLedgerInputDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public String getEmpNameFromUserId(Long lLngUserId) throws Exception {
		String lStrUserName = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("Select ME.name ");
			lSBQuery.append("FROM MstEmp ME, OrgEmpMst OEM ");
			lSBQuery.append("WHERE OEM.orgUserMst.userId = :userId ");
			lSBQuery.append("AND OEM.orgUserMst.userId = ME.orgEmpMstId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("userId", lLngUserId);
			lStrUserName = lQuery.list().get(0).toString();
		}
		catch(Exception e){
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrUserName;
	}
	
}
