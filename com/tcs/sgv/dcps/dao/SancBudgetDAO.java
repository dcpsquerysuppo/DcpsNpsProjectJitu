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

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Apr 30, 2011
 */
public interface SancBudgetDAO extends GenericDao {
	
	public List getAllSanctionedBudgets(Long finYear,Long orgType);

	String getSchemeCodeForOrgId(Long lngOrgId);

	public Long getTotalBudget(Long finYear, String strOrganizationId);

	public void updateExpenditure(Long finyear);
	
	public Long getOrgIdForYearId(Long finYear, String strAcDcpsMntndBy);
	
}
