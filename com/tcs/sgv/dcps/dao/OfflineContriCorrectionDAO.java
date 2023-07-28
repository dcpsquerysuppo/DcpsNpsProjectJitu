/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 7, 2011
 */
public interface OfflineContriCorrectionDAO extends GenericDao {

	public List getEmpListForContributionCorrection(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId) throws Exception;

	public Long getEmpIdforContributionId(Long contributionId);

}
