package com.tcs.sgv.lna.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface LNAMotorcarAdvanceService {
	public ResultObject saveMotorcarAdvance(Map<String, Object> inputMap);

	public ResultObject forwardMotorAdvanceToDEOVerifier(Map<String, Object> inputMap);

	public ResultObject forwardMotorAdvanceToHO(Map<String, Object> inputMap);

	public ResultObject forwardMotorAdvanceToAsstHOD(Map<String, Object> inputMap);

	public ResultObject forwardMotorAdvanceToHOD(Map<String, Object> inputMap);

	public ResultObject approveMotorAdvance(Map<String, Object> inputMap);

	public ResultObject rejectMotorAdvanceByDEOApprover(Map<String, Object> inputMap);

	public ResultObject rejectMotorAdvanceByHO(Map<String, Object> inputMap);

	public ResultObject forwardOfflineEntryMCAToHOD(Map<String, Object> inputMap);
}
