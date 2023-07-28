package com.tcs.sgv.gpf.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class GPFEmpPayrollProcessingDAOImpl extends GenericDaoHibernateImpl implements GPFEmpPayrollProcessingDAO {
	Session ghibSession = null;

	public GPFEmpPayrollProcessingDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getMonthlySubscriptionForBillgroup(Long lLngBillGroupId, Long lLngYearId, Long lLngMonthId) {
		List subscriptionList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("SELECT MGM.gpfAccNo,MGM.regularSubscription ");
		lSBQuery.append(" FROM MstGpfMonthly MGM");
		lSBQuery.append(" WHERE MGM.finYearId = :yearId AND MGM.monthId=:monthId AND MGM.billgroupId=:billgroupId ");
		lSBQuery.append(" AND status != 'X' ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("billgroupId", lLngBillGroupId);
		lQuery.setParameter("monthId", lLngMonthId);
		lQuery.setParameter("yearId", lLngYearId);

		subscriptionList = lQuery.list();
		return subscriptionList;
	}

	public Object getGPFAccountObjForEmpId(Long lLngEmpId) {
		List empList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("FROM RltGpfEmp WHERE empId = :empId ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", lLngEmpId);

		empList = lQuery.list();

		return empList.get(0);
	}

	public List getAdvanceDetailsForGPFAccNo(String lStrGpfAccNo) {
		List advanceList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append("FROM MstGpfAdvance WHERE gpfAccNo = :gpfAccNo AND recoveryStatus <> 1 ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("gpfAccNo", lStrGpfAccNo);

		advanceList = lQuery.list();

		return advanceList;
	}
}
