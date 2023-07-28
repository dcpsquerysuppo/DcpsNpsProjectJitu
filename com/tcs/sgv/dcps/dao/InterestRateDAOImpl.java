/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 30, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

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
 * @since JDK 5.0 Apr 30, 2011
 */
public class InterestRateDAOImpl extends GenericDaoHibernateImpl implements InterestRateDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionproc/PensionCaseConstants");

	public InterestRateDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	public List getAllInterestRates() {

		List listInterestRates = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT interestRateIdPk,interest,effectiveFromDate,applicableToDate,status");
		lSBQuery.append(" FROM InterestRate order by status DESC");

		lQuery = ghibSession.createQuery(lSBQuery.toString());

		listInterestRates = lQuery.list();

		return listInterestRates;
	}

	public void UpdatePreviousInterestRate(Date dtToDate) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" Update InterestRate set applicableToDate = :ToDate,status = 0 where applicableToDate IS NULL ");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ToDate", dtToDate);

		lQuery.executeUpdate();
	}
}
