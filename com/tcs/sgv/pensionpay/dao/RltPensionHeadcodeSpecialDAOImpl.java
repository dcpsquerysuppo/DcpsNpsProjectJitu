package com.tcs.sgv.pensionpay.dao;

import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeSpecial;

public class RltPensionHeadcodeSpecialDAOImpl extends
		GenericDaoHibernateImpl<RltPensionHeadcodeSpecial, Long> implements
		RltPensionHeadcodeSpecialDAO {

	public RltPensionHeadcodeSpecialDAOImpl(Class<RltPensionHeadcodeSpecial> type,
			SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}
}
