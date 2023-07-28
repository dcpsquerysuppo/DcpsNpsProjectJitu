package com.tcs.sgv.lna.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface LNADataEntryService {

	public ResultObject loadDataEntryForm(Map<String, Object> inputMap);

	public ResultObject popUpEmpDtlsDataEntryForm(Map<String, Object> inputMap);

	public ResultObject saveLNADataEntryForm(Map<String, Object> inputMap);

	public ResultObject loadDraftDataEntry(Map<String, Object> inputMap);

	public ResultObject loadLNADataEntryCase(Map<String, Object> inputMap);

	public ResultObject forwardLNADataEntryForm(Map<String, Object> inputMap);

	public ResultObject approveLNADataEntryForm(Map<String, Object> inputMap);
}
