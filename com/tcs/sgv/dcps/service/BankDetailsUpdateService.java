package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface BankDetailsUpdateService {
	ResultObject getDdoList(Map<String, Object> inputMap);
	ResultObject getEmplForDDO(Map<String, Object> inputMap);
}
