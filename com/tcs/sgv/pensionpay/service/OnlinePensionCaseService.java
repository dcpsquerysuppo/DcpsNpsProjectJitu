/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 25, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Mar 25, 2011
 */
public interface OnlinePensionCaseService {

	public ResultObject loadOnlinePensionCasesFromAG(Map<String, Object> inputMap);

	public ResultObject genRegNoSaveBnkDtls(Map<String, Object> inputMap);

	public ResultObject savePnsnrBankBranchDtls(Map<String, Object> inputMap);

	public ResultObject getBranchNamesFromBankCode(Map<String, Object> inputMap);

	public ResultObject getAudiNameFrmBrchCdUsingAjax(Map<String, Object> inputMap);

	public ResultObject getBnkBrnchAudiNmeFrmBnkCode(Map<String, Object> inputMap);

	public ResultObject forwardPensionCase(Map<String, Object> inputMap);
	
	public ResultObject validatePPOBeforeArchive(Map<String, Object> inputMap);
	
	public ResultObject sendCaseBackToPensionClerk(Map<String, Object> inputMap);
}
