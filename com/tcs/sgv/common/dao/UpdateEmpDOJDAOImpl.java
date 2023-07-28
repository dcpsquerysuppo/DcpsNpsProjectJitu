package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class UpdateEmpDOJDAOImpl extends GenericDaoHibernateImpl implements UpdateEmpDOJDAO {

	Session ghibSession = null;

	public UpdateEmpDOJDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getEmployeeInfo(String lStrSevaarthId) {
		List lLstEmployeeInfo = new ArrayList();
		System.out.println("in dao");
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT EM.ddo_code,DDO.DDO_NAME,EM.cadre,DOF.OFF_NAME,EM.DOB,EM.EMP_SERVEND_DT,EM.ORG_EMP_MST_ID ,EM.EMP_NAME, EM.DOJ ,EM.REG_STATUS , EM.DCPS_OR_GPF FROM mst_dcps_emp EM ");
			lSBQuery.append("left outer join org_ddo_mst DDO on EM.DDO_CODE = DDO.DDO_CODE left outer join mst_dcps_ddo_office DOF on EM.CURR_OFF = DOF.DCPS_DDO_OFFICE_MST_ID ");
			lSBQuery.append("where EM.sevarth_id = :sevaarthId ");
			lSBQuery.append(" and  EM.REG_STATUS NOT IN ( '-1', 0) ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lLstEmployeeInfo = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is getEmployeeInfo: " + e, e);
		}
		System.out.println("lLstEmployeeInfo size is "+lLstEmployeeInfo);
		return lLstEmployeeInfo;
	}

	public void updateDOJ(String lStrSevaarthId, Date lDtDOJ) {
		try {
			StringBuilder lSBQuery = new StringBuilder();
			System.out.println("############## doj "+lDtDOJ+"sev id "+lStrSevaarthId);
			lSBQuery.append("update mst_dcps_emp set doj = :DOJ where sevarth_id = :sevaarthId  and REG_STATUS = 2 and DCPS_OR_GPF = 'N' ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lQuery.setDate("DOJ", lDtDOJ);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is updateDOJ: " + e, e);
		}
	}

	public void updateJoiningDate(Long lLngOrgEmpId, Date lDtDOJ) {

		try {
			StringBuilder lSBQuery = new StringBuilder();
			System.out.println("############## doj "+lDtDOJ+"lLngOrgEmpId "+lLngOrgEmpId);

			lSBQuery.append("update org_emp_mst set EMP_DOJ = :DOJ where emp_id = :EmpId");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("EmpId", lLngOrgEmpId);
			lQuery.setDate("DOJ", lDtDOJ);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is updateJoiningDate: " + e, e);
		}
	}
	
	
	public void updateJoiningDateTable(String lStrSevaarthId, String lStrEmpName,Long postId ,String lStrDdoCode, Date lDtOldDOJ,Date lDtNewDOJ,String reason) {

		try {
			StringBuilder lSBQuery = new StringBuilder();
			System.out.println("############## doj "+lDtNewDOJ+"lStrSevaarthId "+lStrSevaarthId+"lDtOldDOJ "+lDtOldDOJ+"lStrEmpName "+lStrEmpName+"reason "+reason);
			System.out.println("in updateJoiningDateTable");
			lSBQuery.append("insert into IFMS.MST_DOJ_UPDATED_DTLS  (SEVARTH_ID,EMP_NAME,UPDATED_BY_POST,DDO_CODE,OLD_DOJ,NEW_DOJ,DOJ_UPDATION_DATE,DOJ_UPDATION_REMARKS) VALUES (:SevaarthId,:EmpName ,:postId ,:DdoCode, :OldDOJ,:NewDOJ, sysdate,:reason )");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId);
			lQuery.setParameter("EmpName", lStrEmpName);
			lQuery.setParameter("DdoCode",lStrDdoCode);
			
			lQuery.setDate("OldDOJ", lDtOldDOJ);
			lQuery.setDate("NewDOJ", lDtNewDOJ);
			lQuery.setParameter("reason",reason);
			lQuery.setLong("postId", postId);
			lQuery.executeUpdate();
			System.out.println("updateJoiningDateTable ended");
		} catch (Exception e) {
			logger.error(" Error is updateJoiningDate: " + e, e);
		}
	}
	
}
