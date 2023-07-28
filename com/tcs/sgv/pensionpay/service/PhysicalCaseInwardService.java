/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 19, 2011		Shivani Rana								
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
 * @since JDK 5.0 Apr 19, 2011
 */
public interface PhysicalCaseInwardService {

	ResultObject loadPhysicalPensionCase(Map<String, Object> inputMap);

	ResultObject insertPhysicalPensionCase(Map<String, Object> inputMap);

	ResultObject getHeadCodeDesc(Map<String, Object> inputMap);

	ResultObject getDARateFromROPType(Map<String, Object> inputMap);

	ResultObject getIfscCodeFromBranchCode(Map<String, Object> inputMap);

	ResultObject validatePPONo(Map<String, Object> inputMap);

	ResultObject showModificationByAuditor(Map<String, Object> inputMap);

	ResultObject approveModification(Map<String, Object> inputMap);

	ResultObject getPensionerDetailsFrmPpoNo(Map<String, Object> inputMap);

	ResultObject validateSchemeCode(Map<String, Object> inputMap);

	ResultObject getOfficeListForAutoComplete(Map<String, Object> inputMap);

	ResultObject getDesgListForAutoComplete(Map<String, Object> inputMap);

	ResultObject loadCommutationHistory(Map<String, Object> inputMap);

	ResultObject loadDcrgHistory(Map<String, Object> inputMap);

	ResultObject loadSchemeCodePopUp(Map<String, Object> inputMap);

	ResultObject getPensionCaseRemarks(Map<String, Object> inputMap);

	ResultObject saveCommutationHistory(Map<String, Object> inputMap);

	ResultObject deleteCommutationHistory(Map<String, Object> inputMap);

	ResultObject saveDcrgHistoryDtls(Map<String, Object> inputMap);
	
	ResultObject getBankBranchFromPaymentScheme(Map<String, Object> inputMap);
	
	ResultObject validatePensionArrearDtls(Map<String, Object> inputMap);
	
	ResultObject validateMajorHead(Map<String, Object> inputMap);
	
	ResultObject getSchemeCodeListFrmMajorHead(Map<String, Object> inputMap);
	
	ResultObject getSchemeNameFromSchemeCode(Map<String, Object> inputMap);
	
	ResultObject loadUpdateBankBranchMigratedPpo(Map<String, Object> inputMap);
	
	ResultObject validatePpoBeforeSetCaseOwner(Map<String, Object> inputMap);
	
	ResultObject updateBankBranchForMigratedPpo(Map<String, Object> inputMap);
}
