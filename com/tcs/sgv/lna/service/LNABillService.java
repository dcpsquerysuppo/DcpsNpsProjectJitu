package com.tcs.sgv.lna.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface LNABillService {
	
	public ResultObject generateLoanBill(Map<String, Object> inputMap);
	
	public ResultObject loadLoanBillDtls(Map<String, Object> inputMap);
	
	public ResultObject viewLoanBill(Map<String, Object> inputMap);
	
	public ResultObject approveLoanBill(Map<String, Object> inputMap);
	
	public ResultObject rejectLoanBill(Map<String, Object> inputMap);
	
	public ResultObject viewOrderReport(Map<String, Object> inputMap);
}
