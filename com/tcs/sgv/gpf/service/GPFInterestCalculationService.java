package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GPFInterestCalculationService {
	public ResultObject loadInterestCal(Map inputMap);

	public ResultObject popUpEmpDtlsForInterestCal(Map inputMap);

	public ResultObject popUpEmpDtlsUsingOfficeName(Map inputMap);

	public ResultObject popUpInterestRate(Map inputMap);

	public ResultObject approveInterestCase(Map inputMap);
}
