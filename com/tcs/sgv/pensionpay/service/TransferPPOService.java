/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 27, 2011		Bhargav Trivedi								
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
 * May 27, 2011
 */
public interface TransferPPOService 
{
	ResultObject loadTransferToOtherTreasury(Map<String, Object> inputMap);
	ResultObject getNameAndTreasuryFromPPONo(Map<String, Object> inputMap);
	ResultObject insertTransferOfPPOCase(Map<String, Object> inputMap);
	ResultObject loadReceiveTransferCase(Map<String, Object> inputMap);
	ResultObject changeInPPOCase(Map<String, Object> inputMap);
	ResultObject loadRejectedTransferCase(Map<String, Object> inputMap);
	ResultObject generateTransferLetter(Map<String, Object> inputMap);
	ResultObject getTransferDtlsFromReqId(Map<String, Object> inputMap);
	
	
}
