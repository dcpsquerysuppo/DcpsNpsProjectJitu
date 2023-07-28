package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;


public interface NewPensionBillService {

	public ResultObject getNewPensionBillData(Map<String, Object> inputMap);

	public ResultObject getSavedNewPensionBillData(Map<String, Object> inputMap);

	public Map getMonthlyPensionBillVO(Map<String, Object> inputMap) throws Exception;
}
