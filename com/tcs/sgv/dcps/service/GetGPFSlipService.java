package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface GetGPFSlipService  {

	public ResultObject getGPFSlip(Map<String, Object> inputMap)
	throws Exception;

}
