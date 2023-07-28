package com.tcs.sgv.lna.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface LNAHodSignatureConfigService {
	public ResultObject saveHODSignature(Map<String, Object> inputMap) throws Exception;
}
