/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 30, 2011		Meeta Thacker								
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
 * @since JDK 5.0
 * May 30, 2011
 */
public interface PostEmpContriService {
	public ResultObject loadPostEmpContri(Map<String, Object> inputMap)
	throws Exception ;
	
	public ResultObject savePostEmpContri(Map<String, Object> inputMap)
	throws Exception ;
	
	ResultObject actionOnPostEmpContri(Map<String, Object> inputMap)
	throws Exception;
	
	ResultObject getSancBudgetForFinYear(Map<String, Object> inputMap)
	throws Exception;
}
