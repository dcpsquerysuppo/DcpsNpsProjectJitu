package com.tcs.sgv.pensionpay.dao;

import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.RltPensionRevisedPayment;

public class RltPensionRevisedPaymentDAOImpl extends
		GenericDaoHibernateImpl<RltPensionRevisedPayment, Long> implements
		RltPensionRevisedPaymentDAO {

	public RltPensionRevisedPaymentDAOImpl(Class<RltPensionRevisedPayment> type,
			SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}
}
