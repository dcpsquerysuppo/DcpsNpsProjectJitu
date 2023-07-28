/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 26, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description - 
 *
 *
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0
 * May 26, 2011
 */
public class FeedbackDAOImpl extends GenericDaoHibernateImpl implements FeedbackDAO{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public FeedbackDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}
	
	public List getFeedbacksForSRKA() throws Exception {

		List listReversionRequests = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT FB.ddo,OD.ddoName,FB.comments,FB.feedBackDate,FB.emailAddress,FB.contactNo ");
		lSBQuery.append(" FROM Feedback FB,OrgDdoMst OD");
		lSBQuery.append(" Where FB.ddo = OD.ddoCode");

		lQuery = ghibSession.createQuery(lSBQuery.toString());

		listReversionRequests = lQuery.list();

		return listReversionRequests;

	}
}