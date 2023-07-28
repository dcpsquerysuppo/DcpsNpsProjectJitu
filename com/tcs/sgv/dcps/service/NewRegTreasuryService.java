/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 11, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 11, 2011
 */
public interface NewRegTreasuryService {

	public ResultObject loadAllSavedCases(Map inputMap) throws Exception;

	public ResultObject loadTOPhyFormRcvdList(Map inputMap);

	public ResultObject loadTOPhyFormRcvd(Map inputMap) throws Exception;

	public ResultObject loadFormStatusList(Map inputMap);

	public ResultObject ApproveForm(Map inputMap) throws Exception;

	public ResultObject rejectRequest(Map objectArgs);

	public String generateDCPSId(Map inputMap, Long lLngEmpId)  throws Exception;

	public ResultObject submitPhyFormRcvdStatus(Map inputMap) throws Exception;

	public ResultObject rejectRequestForPhyFormNotRcvd(Map objectArgs);

}
