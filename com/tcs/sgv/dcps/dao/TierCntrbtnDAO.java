/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 8, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Apr 8, 2011
 */
public interface TierCntrbtnDAO extends GenericDao 
{
	List getEmpDtlsFromDDODesig(String lStrDesigId, Map inputMap) throws Exception;
	String getEmpNameFromId(Long lLngdcpsEmpId) throws Exception;
	List getAllSavedRequestsForTier(Integer lIntCriteria,String lStrPostId) throws Exception;
	List getTierDtlFromDesig(String lStrDesignation) throws Exception;
	Long getMstDcpsTierICntrnbtnPk(Long lLngEmpId) throws Exception;
	
	
}
