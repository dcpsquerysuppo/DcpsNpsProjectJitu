/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	June 13, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 June 13, 2011
 */
public interface ChangeEmpDeptService {
	
	ResultObject loadChangeEmpDept(Map<String, Object> inputMap)
			throws Exception;

	ResultObject saveChangeEmpDept(Map<String, Object> inputMap)
			throws Exception;

	ResultObject getEmpDetails(Map<String, Object> inputMap) throws Exception;
	
	public ResultObject loadChangeParentDept (Map<String, Object> inputMap)
	throws Exception ;
	
	public ResultObject changeDDOCode(Map<String, Object> inputMap) throws Exception ;
}
