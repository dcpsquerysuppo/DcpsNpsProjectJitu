package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GPFEmpPayrollProcessingService {
	ResultObject updateGPFDataOnPaybillGeneration(Map<String, Object> inputMap);

	ResultObject getEmpSubscriptionForBillgroup(Map<String, Object> inputMap);
}
