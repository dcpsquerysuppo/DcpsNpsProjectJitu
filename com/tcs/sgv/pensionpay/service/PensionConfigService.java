/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 13, 2011		Bhargav Trivedi								
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
 * @since JDK 5.0 May 13, 2011
 */
public interface PensionConfigService {

	public ResultObject loadAuditorBankBrnchMapping(Map<String, Object> inputMap);

	public ResultObject displayBranchesForBank(Map<String, Object> inputMap);

	public ResultObject saveAuditorBankBrnchMapping(Map<String, Object> inputMap);

	public ResultObject getMappingDtls(Map<String, Object> inputMap);

	public ResultObject getRemarksOfBill(Map<String, Object> inputMap);

	public ResultObject loadHeadCodeConfig(Map<String, Object> inputMap);

	public ResultObject saveHeadCodeConfig(Map<String, Object> inputMap);

	public ResultObject loadChangeStmntCtgryConfig(Map<String, Object> inputMap);

	public ResultObject saveChangeStmntCtgryConfig(Map<String, Object> inputMap);

	public ResultObject getPPODetails(Map<String, Object> inputMap);

	public ResultObject savePPONo(Map<String, Object> inputMap);

	public ResultObject showPPONoHistory(Map<String, Object> inputMap);

	public ResultObject loadMainCategoryConfig(Map<String, Object> inputMap);

	public ResultObject saveMainCategoryConfig(Map<String, Object> inputMap);

	public ResultObject loadSchemeCode(Map<String, Object> inputMap);
	
	public ResultObject vaildateBnkBrnchWithAuditor(Map<String, Object> inputMap);
	
	public ResultObject checkSchemeCode(Map<String, Object> inputMap);

}
