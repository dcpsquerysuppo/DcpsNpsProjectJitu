/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 12, 2011		Chudasama Jayraj								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Chudasama Jayraj
 * @version 0.1
 * @since JDK 5.0
 * Dec 12, 2011
 */
public interface GpfLedgerInputService 
{
	ResultObject loadLedgerInput(Map<String, Object> inputMap);
	
	ResultObject loadUserDetails(Map<String, Object> inputMap);
}
