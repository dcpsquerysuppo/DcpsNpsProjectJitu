package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class UpdateEmpDOBAndDORDAOImpl extends GenericDaoHibernateImpl implements UpdateEmpDOBAndDORDAO {

	Session ghibSession = null;

	public UpdateEmpDOBAndDORDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getEmployeeInfo(String lStrSevaarthId) {
		List lLstEmployeeInfo = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT EM.ddo_code,DDO.DDO_NAME,EM.cadre,DOF.OFF_NAME,EM.DOB,EM.EMP_SERVEND_DT,EM.ORG_EMP_MST_ID FROM mst_dcps_emp EM ");
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

	public void updateDOB(String lStrSevaarthId, Date lDtDOB, Date lDtDOR) {
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("update mst_dcps_emp set dob = :DOB,EMP_SERVEND_DT = :DOR,SUPER_ANN_DATE = :DOR where sevarth_id = :sevaarthId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lQuery.setDate("DOB", lDtDOB);
			lQuery.setDate("DOR", lDtDOR);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is updateDOB: " + e, e);
		}
	}

	public void updateDOR(Long lLngOrgEmpId, Date lDtDOB, Date lDtDOR,String reason) {

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("update org_emp_mst set EMP_DOB = :DOB,EMP_SRVC_EXP = :DOR,reason = :reason where emp_id = :EmpId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("EmpId", lLngOrgEmpId);
			lQuery.setDate("DOB", lDtDOB);
			lQuery.setDate("DOR", lDtDOR);
			lQuery.setParameter("reason",reason);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is updateDOR: " + e, e);
		}
	}
}
