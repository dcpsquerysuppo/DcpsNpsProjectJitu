/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 28, 2011		Vihan Khatri								
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
 * @since JDK 5.0 Apr 28, 2011
 */
public interface DdoBillGroupService {

	public ResultObject loadBillGroupCreation(Map<String, Object> inputMap);

	public ResultObject popUpBillGroupDtls(Map<String, Object> inputMap);

	public ResultObject loadAttachBillGroup(Map<String, Object> inputMap);

	public ResultObject attachAndDetachEmpToBG(Map<String, Object> inputMap);

	public ResultObject saveDCPSDdoBill(Map inputMap);
	
	public ResultObject deleteSubBillGroup(Map<String, Object> inputMap);
	
	public ResultObject getTotalSubBGsForScheme(Map<String, Object> inputMap) ;

}
