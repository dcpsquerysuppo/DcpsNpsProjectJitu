package com.tcs.sgv.common.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class FinancialYearDAOImpl extends GenericDaoHibernateImpl<SgvcFinYearMst, Long> implements FinancialYearDAO {

	Log logger = LogFactory.getLog(SgvcFinYearMst.class);

	public FinancialYearDAOImpl(Class<SgvcFinYearMst> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public int getFinYearId(String date) {

		int FinYearId = 0;
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			Date frmDate = sdf.parse(date);
			Date toDate = sdf.parse(date);
			
			Session hibSession = getSession();
			Query sqlQuery = hibSession.createQuery("select fym.finYearId from SgvcFinYearMst fym where fym.fromDate<= :frmDate and fym.toDate>= :toDate ");
			
			
			sqlQuery.setDate("frmDate", frmDate);
			sqlQuery.setDate("toDate", toDate);
			sqlQuery.setCacheable(true);
			sqlQuery.setCacheRegion("ecache_lookup");
			
			List resList = sqlQuery.list();
			if (resList != null && resList.size() > 0) {

				Iterator it = resList.iterator();
				while (it.hasNext()) {
					Object tuple = (Object) it.next();
					FinYearId = Integer.parseInt(tuple.toString());
					logger.info("----Financial Year Id is :----" + tuple + "---");
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured in FinancialYearDAOImpl.getFinYearId # \n" + e);
		}
		return FinYearId;
	}

	public int getFinYearIdByCurDate()
	{
		int FinYearId = 0;
		
		try {

			Session hibSession = getSession();
			//Date lDCurrDate = new Date(System.currentTimeMillis());
			Date lDCurrDate = DBUtility.getCurrentDateFromDB();
			Query sqlQuery = hibSession.createQuery("select fym.finYearId from SgvcFinYearMst fym where fym.fromDate<= :currDate and fym.toDate>=:currDate");
			sqlQuery.setParameter("currDate", lDCurrDate);
			sqlQuery.setCacheable(true);
			sqlQuery.setCacheRegion("ecache_lookup");
			
			List resList = sqlQuery.list();
			if (resList != null && resList.size() > 0) 
			{
				Iterator it = resList.iterator();
				while (it.hasNext()) 
				{
					Object tuple = (Object) it.next();
					FinYearId = Integer.parseInt(tuple.toString());
					logger.info("----Financial Year Id is :----" + tuple + "---");
				}
			}
		} 
		catch(Exception e) 
		{
			logger.error("Exception occured in FinancialYearDAOImpl.getFinYearIdByCurDate # \n" + e);
		}
		
		return FinYearId;
	}

}
