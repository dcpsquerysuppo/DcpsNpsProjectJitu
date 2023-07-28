/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 14, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.CmnHolidayMst;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 14, 2011
 */
public class CmnHolidayMstDAOImpl extends GenericDaoHibernateImpl<CmnHolidayMst, Long> implements CmnHolidayMstDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	SessionFactory sessionFactory = null;

	public CmnHolidayMstDAOImpl(Class<CmnHolidayMst> type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public ArrayList<Date> getHolidayList(Date lDtCurrDate, Date lDtAf3Years) throws Exception {

		Query lQuery = null;
		StringBuffer lSb = new StringBuffer();
		List resultList = new ArrayList();
		lDtCurrDate = new Date(); // getting holiday list from current date to 3
									// years after current date.
		ArrayList<Date> holidayList = new ArrayList<Date>();
		try {
			ghibSession = this.getSession();
			lSb.append("SELECT hldyDt from CmnHolidayMst  ");
			lSb.append("where hldyDt >= :currDate and hldyDt <= :af3YearDt");
			lQuery = ghibSession.createQuery(lSb.toString());
			lQuery.setParameter("currDate", lDtCurrDate);
			lQuery.setParameter("af3YearDt", lDtAf3Years);
			resultList = lQuery.list();
			Iterator<Timestamp> lItr = resultList.iterator();
			while (lItr.hasNext()) {
				Date d = new Date(lItr.next().getTime());
				holidayList.add(d);
			}
		} catch (Exception e) {
			gLogger.error("Error is " + e, e);
			throw (e);
		}
		return holidayList;

	}
}
