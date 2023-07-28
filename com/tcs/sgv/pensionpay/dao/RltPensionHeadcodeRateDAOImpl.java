package com.tcs.sgv.pensionpay.dao;

import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;

public class RltPensionHeadcodeRateDAOImpl extends
		GenericDaoHibernateImpl<RltPensionHeadcodeRate, Long> implements RltPensionHeadcodeRateDAO {

	public RltPensionHeadcodeRateDAOImpl(Class<RltPensionHeadcodeRate> type,
			SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}
}
