package com.tcs.sgv.lna.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface LNAHouseAdvanceService {
	public ResultObject saveHouseAdvance(Map<String, Object> inputMap);

	public ResultObject forwardHouseAdvanceToDEOVerifier(Map<String, Object> inputMap);

	public ResultObject forwardHouseAdvanceToHO(Map<String, Object> inputMap);

	public ResultObject forwardHouseAdvanceToAsstHOD(Map<String, Object> inputMap);

	public ResultObject forwardHouseAdvanceToHOD(Map<String, Object> inputMap);

	public ResultObject approveHouseAdvance(Map<String, Object> inputMap);

	public ResultObject rejectHouseAdvanceByDEOApprover(Map<String, Object> inputMap);

	public ResultObject rejectHouseAdvanceByHO(Map<String, Object> inputMap);

	public ResultObject forwardOfflineEntryHBAToHOD(Map<String, Object> inputMap);

	public ResultObject popUpGuarantorDtls(Map<String, Object> inputMap);

	public ResultObject chckEligibilityForExtensionOfRoom(Map<String, Object> inputMap);

}
