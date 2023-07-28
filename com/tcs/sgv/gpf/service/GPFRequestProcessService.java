package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GPFRequestProcessService {
	ResultObject loadGPFRequestProcess(Map<String, Object> inputMap);

	ResultObject loadGPFRequestList(Map<String, Object> inputMap);

	ResultObject loadWithdrawalDetailList(Map<String, Object> inputMap);

	ResultObject loadAdvanceDetailList(Map<String, Object> inputMap);

	ResultObject populateNonRefundSubType(Map<String, Object> inputMap);
}
