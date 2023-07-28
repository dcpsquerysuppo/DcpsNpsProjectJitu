package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface MonthlyPensionBillService {

	 public ResultObject loadMonthlyPension(Map<String,Object> inputMap);
	 public ResultObject getMonthlyPension(Map<String,Object> inputMap);
	 public ResultObject saveMonthlyPension(Map<String,Object> inputMap);
	 public ResultObject viewMonthlyBill(Map<String,Object> inputMap);
	 public ResultObject validateBill(Map<String,Object> inputMap) throws Exception;
	 public ResultObject printMonthlyBill(Map<String,Object> inputMap);
}
