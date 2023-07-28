/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 9, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 May 9, 2011
 */
public class TrnChequeDtlsDAOImpl extends GenericDaoHibernateImpl implements TrnChequeDtlsDAO {

	Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();

	/**
	 * @param type
	 */
	public TrnChequeDtlsDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}

}
