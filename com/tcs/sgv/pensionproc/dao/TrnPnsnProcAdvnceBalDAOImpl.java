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
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcAdvnceBal;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Feb 5, 2011
 */
public class TrnPnsnProcAdvnceBalDAOImpl extends GenericDaoHibernateImpl implements TrnPnsnProcAdvnceBalDAO
{

	/**
	 * @param type
	 */
	public TrnPnsnProcAdvnceBalDAOImpl(Class<TrnPnsnProcAdvnceBal> type,SessionFactory sessionFactory) 
	{
		super(type);
		setSessionFactory(sessionFactory);
		// TODO Auto-generated constructor stub
	}

}
