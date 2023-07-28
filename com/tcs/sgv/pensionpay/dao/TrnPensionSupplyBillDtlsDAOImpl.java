/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 7, 2011		Shivani Rana								
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
 * @since JDK 5.0 Jun 7, 2011
 */
public class TrnPensionSupplyBillDtlsDAOImpl extends GenericDaoHibernateImpl implements TrnPensionSupplyBillDtlsDAO {

	/**
	 * @param type
	 */

	Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();

	public TrnPensionSupplyBillDtlsDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

}
