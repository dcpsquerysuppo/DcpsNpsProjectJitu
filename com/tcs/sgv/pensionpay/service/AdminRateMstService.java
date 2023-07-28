package com.tcs.sgv.pensionpay.service;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;

public interface AdminRateMstService {
	ResultObject loadAdminRateMst(Map<String, Object> inputMap);

	ResultObject getDataFromHeadcode(Map<String, Object> inputMap);

	ResultObject saveAdmin(Map<String, Object> inputMap);

	List<TrnPensionArrearDtls> getArrearForPayment(Map<String, Object> inputMap) throws Exception;
}
