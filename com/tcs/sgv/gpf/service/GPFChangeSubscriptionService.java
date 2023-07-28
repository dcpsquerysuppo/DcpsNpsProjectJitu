package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GPFChangeSubscriptionService {
	public ResultObject saveChangeSubscription(Map<String, Object> inputMap);

	public ResultObject forwardChangeSubToDEOApprover(
			Map<String, Object> inputMap);

	public ResultObject forwardChangeSubToHO(Map<String, Object> inputMap);

	public ResultObject approveChangeSubByHO(Map<String, Object> inputMap);

	public ResultObject rejectChangeSubByDEOApprover(
			Map<String, Object> inputMap);

	public ResultObject rejectChangeSubByHO(Map<String, Object> inputMap);
}
