package com.tcs.sgv.pensionproc.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface CommonPensionService {

	public ResultObject loadDistricts(Map inputMap);

	public ResultObject getHOOFromDepartment(Map inputMap);

	public ResultObject getBranch(Map<String, Object> inputMap);
}
