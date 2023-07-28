package com.tcs.sgv.pensionpay.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.tcs.sgv.pensionpay.valueobject.TrnPensionOtherPartyPayment;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;



public class TrnPensionOtherPartyPaymentDAOImpl extends GenericDaoHibernateImpl<TrnPensionOtherPartyPayment, Long> implements TrnPensionOtherPartyPaymentDAO {

	Log gLogger = LogFactory.getLog(getClass());

	public TrnPensionOtherPartyPaymentDAOImpl(Class<TrnPensionOtherPartyPayment> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}
}