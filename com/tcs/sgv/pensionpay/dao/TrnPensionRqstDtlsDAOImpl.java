package com.tcs.sgv.pensionpay.dao;

import org.hibernate.SessionFactory;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;

public class TrnPensionRqstDtlsDAOImpl extends GenericDaoHibernateImpl<TrnPensionRqstHdr, Long>{

	public TrnPensionRqstDtlsDAOImpl(Class<TrnPensionRqstHdr> type,SessionFactory sessionFactory)
	{
		super(type);
		setSessionFactory(sessionFactory);
	}
}
