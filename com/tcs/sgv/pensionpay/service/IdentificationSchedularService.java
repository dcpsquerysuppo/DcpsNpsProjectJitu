/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 11, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 11, 2011
 */
public interface IdentificationSchedularService {

	public ResultObject scheduleIdentification(Map inputMap);

	public ResultObject saveScheduleDtls(Map inputMap);
	
	public ResultObject saveArchivedCase(Map inputMap);

}
