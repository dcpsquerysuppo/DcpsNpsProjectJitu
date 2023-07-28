package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 23, 2012
 */

public interface UpdateSixPCArrearMasterAmountService 
{
	ResultObject getSixPCArrearDtlsFromEmpCode(Map<String, Object> inputMap);
	
	ResultObject updateSixPCArrearMsterAmount(Map<String, Object> inputMap);
}
