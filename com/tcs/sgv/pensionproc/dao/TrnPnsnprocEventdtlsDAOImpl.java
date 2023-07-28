/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 2, 2011		Sneha								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


/**
 * Class Description - 
 *
 *
 * @author Sneha
 * @version 0.1
 * @since JDK 5.0
 * Feb 2, 2011
 */
public class TrnPnsnprocEventdtlsDAOImpl 
		extends GenericDaoHibernateImpl
			implements TrnPnsnprocEventdtlsDAO {

	/**
	 * @param type
	 */
	
	private final static Logger gLogger = Logger.getLogger(TrnPnsnprocEventdtlsDAOImpl.class);		
	private Session ghibSession = null;
	public TrnPnsnprocEventdtlsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		// TODO Auto-generated constructor stub
	}
	


}
