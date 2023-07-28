package com.tcs.sgv.lna.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface LNAComputerAdvanceService {
	public ResultObject saveComputerAdvance(Map<String, Object> inputMap);

	public ResultObject forwardComAdvanceToDEOVerifier(Map<String, Object> inputMap);

	public ResultObject forwardComAdvanceToHO(Map<String, Object> inputMap);

	public ResultObject forwardComAdvanceToAsstHOD(Map<String, Object> inputMap);

	public ResultObject forwardComAdvanceToHOD(Map<String, Object> inputMap);

	public ResultObject approveComputerAdvance(Map<String, Object> inputMap);

	public ResultObject rejectComAdvanceByDEOApprover(Map<String, Object> inputMap);

	public ResultObject rejectComAdvanceByHO(Map<String, Object> inputMap);

	public ResultObject forwardOfflineEntryCAToHOD(Map<String, Object> inputMap);

}
