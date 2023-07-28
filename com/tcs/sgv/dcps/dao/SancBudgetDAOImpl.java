/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 30, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Apr 30, 2011
 */
public class SancBudgetDAOImpl extends GenericDaoHibernateImpl implements
		SancBudgetDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/pensionproc/PensionCaseConstants");

	public SancBudgetDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	public List getAllSanctionedBudgets(Long finYear,Long orgType) {

		List listSanctionedBudgets = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT dcpsSanctionedBudgetIdPk,dcpsSancBudgetOrgId,dcpsSancBudgetFinYear,dcpsSancBudgetDate,dcpsSancBudgetAmount,dcpsSancBudgetType,totalBudget ");
		lSBQuery.append(" FROM SanctionedBudget WHERE dcpsSancBudgetFinYear=:finYear ");
		
		if(orgType != null)
		{
			lSBQuery.append(" and dcpsSancBudgetOrgId = :orgType  order by CREATED_DATE ");
		}

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
		
		if(orgType != null)
		{
			lQuery.setParameter("orgType", orgType);
		}
		
		listSanctionedBudgets = lQuery.list();

		return listSanctionedBudgets;
	}
	
	//----Starts Changes by ashish to get emplrSchemeCode according to ac_main_by
	
	
	public Long getOrgIdForYearId(Long finYear,String accMain) {

		List<Long> lListOrgId = null;

		Long lLngOrgId = 0L;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" SELECT dcpsSancBudgetOrgId FROM SanctionedBudget WHERE dcpsSancBudgetFinYear= :finYear ");

		 if(accMain!="" && !accMain.equals("") && Long.parseLong(accMain)==700240) 
		 {
			 lSBQuery.append(" and dcpsSancBudgetOrgId=991000016 ");
		 }	 
		 else if(accMain!="" && !accMain.equals("") && Long.parseLong(accMain)==700241)	
		 {
			 lSBQuery.append(" and dcpsSancBudgetOrgId=991000017 ");
		 }
		 else if(accMain!="" && !accMain.equals("") && Long.parseLong(accMain)==700242)	
		 {
			 lSBQuery.append(" and dcpsSancBudgetOrgId=991000018 ");
		 }
			
		 else
		 {
			 lSBQuery.append(" and dcpsSancBudgetOrgId=991000015 ");
		 }
		 lSBQuery.append(" ORDER BY CreatedDate DESC");
		
		
		
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
		lListOrgId = lQuery.list();

		if (lListOrgId.size() != 0 && lListOrgId != null) {
			if (lListOrgId.get(0) != null) {
				lLngOrgId = lListOrgId.get(0);
			}
		}
		return lLngOrgId;
	}
	//----ended Changes by ashish to get emplrSchemeCode according to ac_main_by
	
	

	public String getSchemeCodeForOrgId(Long lngOrgId) {
		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String schemeCode = null;

		lSBQuery.append(" select emplrSchemeCode FROM MstDcpsOrganization WHERE orgId = :orgId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("orgId", lngOrgId);

		tempList = lQuery.list();

		schemeCode = tempList.get(0).toString();
		return schemeCode;
	}

	public Long getTotalBudget(Long finYear, String strOrganizationId){
		
		StringBuilder lSBQuery = new StringBuilder();
		StringBuilder lSBQuery1 = new StringBuilder();
		List<Double> tempList = new ArrayList();
		Double lDoubleCreditAmount = 0d;
		Double lDoubleDebitAmount = 0d;
		Double lDoubleCurrentTotal = 0d;
		Long lLngCurrentTotal = 0l;
		
		lSBQuery.append("SELECT cast(sum(SANC_BUDGET) as double) FROM mst_dcps_sanc_budget WHERE sanc_budget_type = 'Credit' and ORG_ID=:orgId and fin_year = :finYear");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);
		lQuery.setParameter("orgId", strOrganizationId);

		tempList = lQuery.list();

		if (tempList.get(0) != null) {
			lDoubleCreditAmount = tempList.get(0);
		}

		lSBQuery1.append("SELECT cast(sum(SANC_BUDGET) as double) FROM mst_dcps_sanc_budget WHERE sanc_budget_type = 'Debit' and ORG_ID=:organisationId and fin_year = :finYear");

		lQuery = ghibSession.createSQLQuery(lSBQuery1.toString());
		lQuery.setParameter("finYear", finYear);
		lQuery.setParameter("organisationId", strOrganizationId);
		tempList = lQuery.list();

		if (tempList.get(0) != null) {
			lDoubleDebitAmount = tempList.get(0);
		}
		
		lDoubleCurrentTotal = lDoubleCreditAmount - lDoubleDebitAmount;
		lLngCurrentTotal = lDoubleCurrentTotal.longValue();
		
		return (lLngCurrentTotal);

	}

	public void updateExpenditure(Long finYear) {
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
				.append(" update PostEmpContri set excessExpenditure = 0 where finYear = :finYear ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("finYear", finYear);

		lQuery.executeUpdate();

	}
}
