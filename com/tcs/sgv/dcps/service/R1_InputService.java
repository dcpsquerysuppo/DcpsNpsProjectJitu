package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface R1_InputService 
{
	ResultObject loadR1Input(Map<String, Object> inputMap);
}
