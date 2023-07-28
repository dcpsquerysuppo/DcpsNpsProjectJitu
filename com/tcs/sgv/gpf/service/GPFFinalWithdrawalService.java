package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GPFFinalWithdrawalService {

	public ResultObject saveFinalWithdrawal(Map<String, Object> inputMap);

	public ResultObject forwardFinalWithdrawalDEOApprover(Map<String, Object> inputMap);

	public ResultObject forwardFinalWithdrawalToHO(Map<String, Object> inputMap);

	public ResultObject approveFinalWithdrawalByHO(Map<String, Object> inputMap);

	public ResultObject rejectFinalWithdrawalByDEOApprover(Map<String, Object> inputMap);

	public ResultObject rejectFinalWithdrawalByHO(Map<String, Object> inputMap);

}
