/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 19, 2011		Bhargav Trivedi								
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
 * @since JDK 5.0 Mar 19, 2011
 */
public interface DdoInfoService {

	public ResultObject loadDDOInfoPage(Map inputMap) throws Exception;

	public ResultObject saveDDOInformation(Map inputMap);

	public ResultObject populateBranchNames(Map<String, Object> lMapInputMap);

	public ResultObject displayIFSCCodeForBranch(Map inputMap) throws Exception;

}
