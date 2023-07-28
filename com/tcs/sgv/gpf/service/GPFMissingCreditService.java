package com.tcs.sgv.gpf.service;

import java.util.Map;
import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 18, 2012
 */

public interface GPFMissingCreditService 
{
	ResultObject loadMissingCredit(Map<String, Object> inputMap);
}
