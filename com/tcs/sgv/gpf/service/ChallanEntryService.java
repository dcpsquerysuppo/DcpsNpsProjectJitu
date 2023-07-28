/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 06, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Sep 06, 2011
 */
public interface ChallanEntryService {
	ResultObject loadGPFChallanEntry(Map<String, Object> inputMap);
}
