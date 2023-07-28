package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.valueobject.RltBankBranch;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class ReportingBankBranchDAOImpl extends GenericDaoHibernateImpl implements ReportingBankBranchDAO {

	Session ghibSession = null;
	Log gLogger = LogFactory.getLog(getClass());

	public ReportingBankBranchDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List<RltBankBranch> getRltBankBranchDtls(String lStrBankCode, String lStrLocCode, Map displayTag) {

		List<RltBankBranch> lLstRltBankBranchDtls = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append(" FROM RltBankBranch \n");
			lSBQuery.append(" WHERE bankCode = :bankCode AND locationCode = :locationCode \n");
			lSBQuery.append(" Order By branchName\n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("bankCode", lStrBankCode);
			lQuery.setParameter("locationCode", lStrLocCode);
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			lQuery.setMaxResults(Constants.PAGE_SIZE);
			lLstRltBankBranchDtls = lQuery.list();
		} catch (Exception e) {
			gLogger.error("Error in getRltBankBranchDtls :" + e);
		}
		return lLstRltBankBranchDtls;
	}

	public Integer getRltBankBranchCount(String lStrBankCode, String lStrLocCode) {

		List lLstRltBankBranchDtls = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		Integer lIntCount = 0;
		try {
			lSBQuery.append(" Select count(*) FROM RltBankBranch");
			lSBQuery.append(" WHERE bankCode = :bankCode AND locationCode = :locationCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("bankCode", lStrBankCode);
			lQuery.setParameter("locationCode", lStrLocCode);
			lLstRltBankBranchDtls = lQuery.list();
			lIntCount = Integer.parseInt((lLstRltBankBranchDtls.get(0).toString()));
		} catch (Exception e) {
			gLogger.error("Error in getRltBankBranchDtls :" + e);
		}
		return lIntCount;
	}

	public void updateAllReportingBankBranch(String lStrBankCode, String lStrLocCode, String lStrReportingBankCode, String lStrReportingBranchCode) {

		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append(" update RltBankBranch set reportingBankCode = :reportingBankCode,reportingBranchCode = :reportingBranchCode");
			lSBQuery.append(" WHERE bankCode = :bankCode AND locationCode = :locationCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("bankCode", lStrBankCode);
			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameter("reportingBankCode", lStrReportingBankCode);
			lQuery.setParameter("reportingBranchCode", lStrReportingBranchCode);
			lQuery.executeUpdate();

		} catch (Exception e) {
			gLogger.error("Error in getRltBankBranchDtls :" + e);
		}
	}

	public void updateIndividualReportingBankBranch(Long lLngBranchCode, String lStrLocCode, String lStrReportingBankCode, String lStrReportingBranchCode, String lStrEcsBranch, String lStrMicrCode,
			String lStrIfscCode, Long lLngUpdtUserId, Long lLngUptdPostId, Date lUpdtDate) {

		StringBuilder lSBQuery = new StringBuilder();
		if ("-1".equals(lStrEcsBranch)) {
			lStrEcsBranch = "N";
		}
		try {
			lSBQuery.append(" update RltBankBranch set reportingBankCode = :reportingBankCode,reportingBranchCode = :reportingBranchCode ");
			lSBQuery.append(" ,ecsBranch = :ecsBranch,ifscCode = :ifscCode ,micrCode = :micrCode,updatedUserId = :updtUserId,updatedPostId = :updtPostId,updatedDate = :updtDate ");
			lSBQuery.append(" WHERE branchCode = :branchCode AND locationCode = :locationCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("branchCode", lLngBranchCode);
			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameter("ecsBranch", lStrEcsBranch);
			lQuery.setLong("updtUserId", lLngUpdtUserId);
			lQuery.setLong("updtPostId", lLngUptdPostId);
			lQuery.setDate("updtDate", lUpdtDate);
			if ("-1".equals(lStrMicrCode)) {
				lQuery.setParameter("micrCode", null);
			} else {
				lQuery.setParameter("micrCode", Long.parseLong(lStrMicrCode));
			}
			if ("-1".equals(lStrIfscCode)) {
				lQuery.setParameter("ifscCode", null);
			} else {
				lQuery.setParameter("ifscCode", lStrIfscCode);
			}
			if ("-1".equals(lStrReportingBankCode)) {
				lQuery.setParameter("reportingBankCode", null);
			} else {
				lQuery.setParameter("reportingBankCode", lStrReportingBankCode);
			}
			if ("-1".equals(lStrReportingBranchCode)) {
				lQuery.setParameter("reportingBranchCode", null);
			} else {
				lQuery.setParameter("reportingBranchCode", lStrReportingBranchCode);
			}

			lQuery.executeUpdate();

		} catch (Exception e) {
			gLogger.error("Error in getRltBankBranchDtls :" + e);
		}
	}

	public List getReportingBranchName() {

		List lLstReportingBranchName = new ArrayList<String>();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append(" select branchCode,branchName FROM RltBankBranch");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstReportingBranchName = lQuery.list();
		} catch (Exception e) {
			gLogger.error("Error in getRltBankBranchDtls :" + e);
		}
		return lLstReportingBranchName;
	}
}
