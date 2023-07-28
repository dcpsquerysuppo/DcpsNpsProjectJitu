

package com.tcs.sgv.dcps.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class AttendanceReportDAOImpl extends GenericDaoHibernateImpl implements AttendanceReportDAO{
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public AttendanceReportDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	@Override
	public List getDeptList() {

		List lLstDept = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT DEPT_ID,DEPT_NAME FROM ORG_ATTENDANCE_REPORT_DEPT_MST order by DEPT_ID ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			gLogger.info("query to get Dept List: "+lSBQuery.toString());
			lLstDept= lQuery.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLstDept;
	}
}
