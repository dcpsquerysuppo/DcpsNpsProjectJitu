package com.tcs.sgv.pensionpay.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class UserwiseFunctionalitiesReportQueryDAOImpl extends GenericDaoHibernateImpl {

	private static final Logger gLogger = Logger.getLogger("UserwiseFunctionalitiesReportQueryDAOImpl");
	Session ghibSession = null;

	public UserwiseFunctionalitiesReportQueryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List<Object> getRoleElementDtlsForPenPay() {
		List<Object> lLnaRoleElementDtls = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT AEM.elementName,RDR.roleName,AEM.elementId");
			lSBQuery.append(" FROM AclElementMst AEM,AclRoleElementRlt RER,AclRoleDetailsRlt RDR");
			lSBQuery.append(" where AEM.elementCode = RER.elementCode AND RER.aclRoleMst.roleId = RDR.aclRoleMst.roleId");
			lSBQuery.append(" AND AEM.cmnLanguageMst.langId = 1 AND AEM.elementId LIKE '365%' AND AEM.cmnLookupMstByType.lookupId = 7");
			lSBQuery.append(" AND RER.cmnLookupMstByActivate.lookupId = 1 AND AEM.cmnLookupMstByStatus.lookupId = 1 ORDER BY 3");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lLnaRoleElementDtls = lQuery.list();
		} catch (Exception e) {
			gLogger.error("UserwiseFunctionalitiesReportQueryDAOImpl Exception is" + e, e);
		}
		return lLnaRoleElementDtls;
	}

	public List<Object> getRoleElementDtlsForPenProc() {
		List<Object> lLnaRoleElementDtls = new ArrayList<Object>();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT AEM.elementName,RDR.roleName,AEM.elementId");
			lSBQuery.append(" FROM AclElementMst AEM,AclRoleElementRlt RER,AclRoleDetailsRlt RDR");
			lSBQuery.append(" where AEM.elementCode = RER.elementCode AND RER.aclRoleMst.roleId = RDR.aclRoleMst.roleId");
			lSBQuery.append(" AND AEM.cmnLanguageMst.langId = 1 AND AEM.elementId LIKE '376%' AND AEM.cmnLookupMstByType.lookupId = 7");
			lSBQuery.append(" AND RER.cmnLookupMstByActivate.lookupId = 1 AND AEM.cmnLookupMstByStatus.lookupId = 1 ORDER BY 3");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lLnaRoleElementDtls = lQuery.list();
		} catch (Exception e) {
			gLogger.error("UserwiseFunctionalitiesReportQueryDAOImpl Exception is" + e, e);
		}
		return lLnaRoleElementDtls;
	}
}
