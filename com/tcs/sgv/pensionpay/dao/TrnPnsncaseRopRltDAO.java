package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsncaseRopRlt;


public class TrnPnsncaseRopRltDAO extends GenericDaoHibernateImpl<TrnPnsncaseRopRlt, Long> {

	Log gLogger = LogFactory.getLog(getClass());

	public TrnPnsncaseRopRltDAO(Class<TrnPnsncaseRopRlt> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public List getROPDtlsFromPpoNo(String ppoNo) throws Exception {

		Session hiSession = getSession();
		ArrayList RopDtls = new ArrayList();
		StringBuffer strQuery = new StringBuffer();
		Object tuple;
		List resultList;
		try {
			strQuery.append(" SELECT pnsncaseRopRltId  FROM TrnPnsncaseRopRlt  WHERE ppoNo=:ppoNo");

			Query hqlQuey = hiSession.createQuery(strQuery.toString());
			hqlQuey.setString("ppoNo", ppoNo);
			resultList = hqlQuey.list();
			if (!resultList.isEmpty())
				for (int i = 0; i < resultList.size(); i++) {
					tuple = (Object) resultList.get(i);
					RopDtls.add((Long) tuple);
				}
		} catch (Exception e) {
			gLogger.error("Error is," + e, e);
			throw e;
		}
		return RopDtls;
	}

	public void deleteRopDtlsByPPONo(String lStrPPONo) throws Exception {

		StringBuffer lBdQuery = new StringBuffer();
		try {
			Session hiSession = getSession();
			lBdQuery.append(" DELETE FROM TrnPnsncaseRopRlt WHERE ppoNo=:ppoNo ");
			Query hbQuery = hiSession.createQuery(lBdQuery.toString());
			hbQuery.setString("ppoNo", lStrPPONo);
			hbQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
	}

	public TrnPnsncaseRopRlt getROPDtlsByPpoNoAndROPType(String ppoNo, String rop) throws Exception {

		Session hiSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		TrnPnsncaseRopRlt lObjTempVo = null;
		List resultList;
		try {
			strQuery.append(" FROM TrnPnsncaseRopRlt  WHERE ppoNo=:ppoNo and rop=:rop");

			Query hqlQuey = hiSession.createQuery(strQuery.toString());
			hqlQuey.setString("ppoNo", ppoNo);
			hqlQuey.setString("rop", rop);
			resultList = hqlQuey.list();
			if (!resultList.isEmpty()) {
				lObjTempVo = (TrnPnsncaseRopRlt) resultList.get(0);
			}

		} catch (Exception e) {
			gLogger.error("Error is," + e, e);
			throw e;
		}
		return lObjTempVo;
	}
	
	
	public List<TrnPnsncaseRopRlt> getROPDtlsVoByPpoNoAndROPType(String ppoNo) throws Exception {

		Session hiSession = getSession();
		StringBuffer strQuery = new StringBuffer();
		TrnPnsncaseRopRlt lObjTempVo = null;
		List<TrnPnsncaseRopRlt> resultList;
		try {
			strQuery.append(" FROM TrnPnsncaseRopRlt  WHERE ppoNo=:ppoNo ");

			Query hqlQuey = hiSession.createQuery(strQuery.toString());
			hqlQuey.setString("ppoNo", ppoNo);
			resultList = hqlQuey.list();

		} catch (Exception e) {
			gLogger.error("Error is," + e, e);
			throw e;
		}
		return resultList;
	}
}
