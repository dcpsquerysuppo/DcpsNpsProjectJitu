package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GPFArrearsManualEntryService {
	public ResultObject popUpEmpDtlsGroup(Map inputMap);

	public ResultObject popUpEmpArrearsDtls(Map inputMap);

	public ResultObject approveGPFArrears(Map inputMap);
}
