package com.tcs.sgv.pensionpay.service;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface PensionBillService {

	public ResultObject createPensionSpecificBill(Map<String, Object> inputMap);

	public ResultObject savePensionSpecificBills(Map<String, Object> inputMap);

	public Map insertPensionBillWF(Map<String, Object> objectArgs)
			throws Exception;

	public Map updatePensionBill(Map<String, Object> objectArgs)
			throws Exception;

	// public ResultObject getBillData(Map<String,Object> lMapInput);
	public List getClaimantInfo(Map<String, Object> inputMap) throws Exception;

	public ResultObject validatePensionBillsTokenNo(Map<String, Object> inputMap);

	// public ResultObject getSavedPensionBills(Map<String,Object> lInputMap);
	// public ResultObject forwardPensionBills(Map<String,Object> lInputMap);
	ResultObject getRemarksOfBill(Map<String, Object> inputMap);

	public ResultObject getAllFirstPensionBillCases(Map<String, Object> inputMap)
			throws Exception;

	public ResultObject getDraftBills(Map<String, Object> inputMap);
	
	ResultObject getBillVoucherMappingList(Map<String, Object> inputMap);
	
	ResultObject updateBillVoucherMpgDtls(Map<String, Object> inputMap);
	
	ResultObject archieveBillAfterVoucherMpg(Map<String, Object> inputMap);
	
	ResultObject viewPensionOuterBill(Map<String, Object> inputMap);
	
	ResultObject viewPensionInnerBill(Map<String, Object> inputMap);
}
