/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 28, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.common.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Apr 28, 2011
 */
public interface CommonService {

	// get districts from state
	public ResultObject getDistrictsFromState(Map inputMap);

	// get Branch list From Bank code
	public ResultObject getBranchListFromBankCode(Map inputMap);

	// Added by Kapil for displaying status at home page
	public ResultObject displayDCPSContent(Map inputMap);
	
	public ResultObject getBranchesFromBankCodeLocCode(Map<String, Object> inputMap);
}
