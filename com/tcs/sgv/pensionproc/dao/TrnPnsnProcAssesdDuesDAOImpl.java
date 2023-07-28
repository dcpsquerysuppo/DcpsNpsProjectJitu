/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 5, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAssesdDues;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Feb 5, 2011
 */
public class TrnPnsnProcAssesdDuesDAOImpl extends GenericDaoHibernateImpl implements TrnPnsnProcAssesdDuesDAO 
{

	/**
	 * @param type
	 */
	public TrnPnsnProcAssesdDuesDAOImpl(Class<TrnPnsnProcAssesdDues> type,SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		// TODO Auto-generated constructor stub
	}

}
