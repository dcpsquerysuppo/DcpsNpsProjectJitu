package com.tcs.sgv.pensionpay.service;

import java.util.Date;
import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

public interface RevisedArrearCalcService {
	
	ResultObject loadArrearCalculationPage(Map<String, Object> inputMap);
	
	ResultObject calculateArrearForRevision(Map<String, Object> inputMap);
	
	ResultObject getPensionerDtlsFromPPONo(Map<String, Object> inputMap);
	
	ResultObject loadSixthPayArrearStatement(Map<String, Object> inputMap);

	ResultObject getDaRateFromPayCommission(Map<String, Object> inputMap);

	ResultObject generateDAArrearRecoveryReport(Map<String, Object> inputMap);

}
