package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GetGPFResponse {
	public ResultObject getGPFResponse(Map<String, Object> inputMap)
	throws Exception;
}
