/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 25, 2011		Vihan Khatri								
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
 * @since JDK 5.0 Apr 25, 2011
 */
public interface SearchEmployeeService {

	public ResultObject searchEmployee(Map inputMap) throws Exception;
	
	public ResultObject getPayDetails(Map inputMap) throws Exception ;
	
	public ResultObject changePayDtlsOfEmp(Map inputMap) throws Exception;
	
	public ResultObject loadSearchEmpForSRKA(Map inputMap) throws Exception ;
	
	public ResultObject getEmpNameForAutoCompleteDCPS(Map<String, Object> inputMap) ;
	
}
