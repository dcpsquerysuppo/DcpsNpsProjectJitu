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

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Apr 8, 2011
 */
public interface SrkaMasterService 
{
	ResultObject insertDesignation(Map inputMap) throws Exception;
	ResultObject loadDesigInfo(Map inputMap) throws Exception;
	ResultObject loadCadreMaster(Map inputMap)throws Exception;
	ResultObject insertCadre(Map inputMap)throws Exception;
	ResultObject loadOrgInfo(Map inputMap) throws Exception;
	ResultObject insertOrganization(Map inputMap) throws Exception;
	public ResultObject checkDesigExistInCadreAndFieldDept(Map<String, Object> inputMap);

}
