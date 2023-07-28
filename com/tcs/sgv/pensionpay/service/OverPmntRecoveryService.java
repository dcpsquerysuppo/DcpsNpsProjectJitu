/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jul 1, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Jul 1, 2011
 */
public interface OverPmntRecoveryService 
{
	public ResultObject loadOverPmntRecovery(Map<String, Object> inputMap);
	public ResultObject getPPODtls(Map<String, Object> inputMap);
	public ResultObject saveOverPmntRecovery(Map<String, Object> inputMap);
	public ResultObject getTotalRecoveryAmnt(Map<String, Object> inputMap);
	public ResultObject getRecoveryDetails(Map<String, Object> inputMap);
	public ResultObject approveOverPmntRecovery(Map<String, Object> inputMap);
	public ResultObject generateRecoveryLetter(Map<String, Object> inputMap);
}
