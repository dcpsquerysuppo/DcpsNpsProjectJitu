package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class JudiciaryRecoveryDAOImpl extends GenericDaoHibernateImpl{

	Session ghibSession = null;

	public JudiciaryRecoveryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
		
	}

	public List getEmployeeInfo(String lStrSevaarthId) {
		List lLstEmployeeInfo = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT EM.emp_name,EM.ddo_code,DDO.DDO_NAME, FROM mst_dcps_emp EM ");
			lSBQuery.append("left outer join org_ddo_mst DDO on EM.DDO_CODE = DDO.DDO_CODE left outer join mst_dcps_ddo_office DOF on EM.CURR_OFF = DOF.DCPS_DDO_OFFICE_MST_ID ");
			lSBQuery.append("where EM.sevarth_id = :sevaarthId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lLstEmployeeInfo = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is getEmployeeInfo: " + e, e);
		}
		return lLstEmployeeInfo;
	}

}
