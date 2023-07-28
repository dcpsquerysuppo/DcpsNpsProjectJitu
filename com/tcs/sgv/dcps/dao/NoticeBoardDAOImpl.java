/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 27, 2011		Meeta Thacker								
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
 * May 27, 2011
 */
public class NoticeBoardDAOImpl extends GenericDaoHibernateImpl implements NoticeBoardDAO{

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public NoticeBoardDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}
	public List getAllNotices() {

		List listNotices = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append(" SELECT noticeSubject,fileName,issuedBy,issuedDate,expiryDate ");
		lSBQuery.append(" FROM NoticeBoard");
		
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		
		listNotices = lQuery.list();

		return listNotices;
	}

}
