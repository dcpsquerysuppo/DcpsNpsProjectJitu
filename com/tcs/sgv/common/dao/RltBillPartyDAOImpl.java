package com.tcs.sgv.common.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.valueobject.RltBillParty;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class RltBillPartyDAOImpl extends GenericDaoHibernateImpl<RltBillParty, Long> implements RltBillPartyDAO {

	private Log gLogger = LogFactory.getLog(getClass());

	public RltBillPartyDAOImpl(Class<RltBillParty> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public List<RltBillParty> getPartyByBill(Long lLngBillNo) {

		List<RltBillParty> lLstBillParty = null;
		// RltBillParty rltBillParty = null;
		StringBuilder lSbQuery = new StringBuilder();
		Session hibSession = getSession();
		try {
			lSbQuery.append("from RltBillParty where billNo = :billno order by partyName ");
			Query lQuery = hibSession.createQuery(lSbQuery.toString());
			lQuery.setParameter("billno", lLngBillNo);
			lLstBillParty = lQuery.list();
			/*
			 * if(lLstBillParty!=null && !lLstBillParty.isEmpty()) rltBillParty
			 * = lLstBillParty.get(0);
			 */
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error in getPartyByBill. Error is : " + e, e);
		}
		return lLstBillParty;
	}
}
