package com.tcs.sgv.pensionpay.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.tcs.sgv.pensionpay.valueobject.TrnPensionOtherPartyPay;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;



public class TrnPensionOtherPartyPayDAOImpl extends GenericDaoHibernateImpl<TrnPensionOtherPartyPay, Long> implements TrnPensionOtherPartyPayDAO {

	Log gLogger = LogFactory.getLog(getClass());

	public TrnPensionOtherPartyPayDAOImpl(Class<TrnPensionOtherPartyPay> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}
}