/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 26, 2011		383385								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Sep 26, 2011
 */
public interface BankBranchMappingService {
	
	ResultObject loadBankBranchMapping(Map<String, Object> inputMap);
	
	ResultObject getPasBranchListFromBankCode(Map<String, Object> inputMap);
	
	ResultObject getBankListFromDistrict(Map<String, Object> inputMap);
	
	ResultObject getBranchsFromBankCode(Map<String, Object> inputMap);
	
	ResultObject mapBankBranch(Map<String, Object> inputMap);
	
	ResultObject loadBankBranchDtls(Map<String, Object> inputMap);

}
