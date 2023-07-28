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
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Apr 8, 2011
 */
public interface TierCntrbtnService 
{

	ResultObject getDCPSTierI(Map inputMap) throws Exception;

	ResultObject insertDCPSTierI(Map inputMap) throws Exception;

	ResultObject loadDCPSTier(Map inputMap) throws Exception;

	ResultObject forwardTierDDO(Map inputMap) throws Exception;

	ResultObject loadDCPSTierDDO(Map inputMap) throws Exception;
	
	ResultObject loadSavedFormForTier(Map inputMap) throws Exception;
	
	ResultObject rejectTierCase(Map inputMap) throws Exception;
	
	ResultObject approveTierCase(Map inputMap) throws Exception;
	
	ResultObject getEmpDtlsFromDDODesig(Map inputMap)throws Exception;

	ResultObject getEmpNameFromId(Map inputMap)throws Exception;

}
