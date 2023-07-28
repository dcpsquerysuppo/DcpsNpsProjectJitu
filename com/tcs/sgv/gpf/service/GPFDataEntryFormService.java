/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Nov 29, 2011		Jayraj Chudasama								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.service;

import java.util.Map;
import com.tcs.sgv.core.valueobject.ResultObject;


/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Nov 29, 2011
 */
public interface GPFDataEntryFormService {
	
	ResultObject loadGPFDataEntryForm(Map<String, Object> inputMap);
	
	ResultObject loadDataEntryDraftRequest(Map<String, Object> inputMap);
	
	ResultObject getEmployeeNameFromEmpCode(Map<String, Object> inputMap);
	
	ResultObject loadDataEntryCase(Map<String, Object> inputMap);
	
	ResultObject saveData(Map<String, Object> inputMap);
	
	ResultObject forwardData(Map<String, Object> inputMap);
	
	ResultObject approveRequest(Map<String, Object> inputMap);
}
