package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GPFInterestConfigService {

	public ResultObject loadInterestConfig(Map<String, Object> inputMap);

	public ResultObject saveInterestConfig(Map<String, Object> inputMap);

}
