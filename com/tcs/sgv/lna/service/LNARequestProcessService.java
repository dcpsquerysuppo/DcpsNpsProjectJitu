package com.tcs.sgv.lna.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface LNARequestProcessService {
	public ResultObject loadLoanAdvanceRequest(Map<String, Object> inputMap);

	public ResultObject loadLNASearchForm(Map<String, Object> inputMap) throws Exception;

	public ResultObject getEmpNameForAutoCompleteLNA(Map<String, Object> inputMap);

	public ResultObject loadDraftRequestList(Map<String, Object> inputMap);

	public ResultObject loadEmpLoanStatus(Map<String, Object> inputMap);
	
	public ResultObject popupEmpNameFromSevaarthId(Map<String, Object> inputMap);
}
