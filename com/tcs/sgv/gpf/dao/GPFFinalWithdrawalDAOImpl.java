package com.tcs.sgv.gpf.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class GPFFinalWithdrawalDAOImpl extends GenericDaoHibernateImpl implements GPFFinalWithdrawalDAO {
	Session ghibSession = null;

	public GPFFinalWithdrawalDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFFinalWithdrawalDAO#getFinalWithdrawalToDEOApprover
	 * (java.lang.String, java.lang.Long, java.lang.Long)
	 */
	public List getFinalWithdrawalToDEOApprover(String lStrGpfAccNo, Long lLngFinalWithdrawalId, Long lLngPostId) {
		List gpfEmpApproverlist = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append("select FW.trnGpfFinalWithdrawalId");
			lSBQuery.append(" FROM TrnGpfFinalWithdrawal FW");
			lSBQuery
					.append(" WHERE FW.gpfAccNo = :gpfAccNo AND FW.reqStatus LIKE 'F%' AND FW.trnGpfFinalWithdrawalId = :trnsId AND FW.updatedPostId <> :postId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("trnsId", lLngFinalWithdrawalId);
			lQuery.setParameter("postId", lLngPostId);

			gpfEmpApproverlist = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getFinalWithdrawalToDEOApprover of GPFFinalWithdrawalDAOImpl  : ", e);			
		}
		return gpfEmpApproverlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.GPFFinalWithdrawalDAO#requestDataAlreadyExists(java
	 * .lang.String)
	 */
	public Boolean requestDataAlreadyExists(String strGpfAccNo) {
		List FWRequest = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("select FW.transactionId");
			lSBQuery.append(" FROM TrnGpfFinalWithdrawal FW");
			lSBQuery.append(" WHERE FW.gpfAccNo = :gpfAccNo AND FW.reqStatus LIKE 'F%'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", strGpfAccNo);
			FWRequest = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in requestDataAlreadyExists of GPFFinalWithdrawalDAOImpl  : ", e);			
		}
		if (FWRequest.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
