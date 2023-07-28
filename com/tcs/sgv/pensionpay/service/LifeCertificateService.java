/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 30, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * May 30, 2011
 */
public interface LifeCertificateService {

	ResultObject getLifeCertificateList(Map<String, Object> inputMap);
	
	ResultObject generateLifeCertificateList(Map<String,Object> inputMap);
	
	ResultObject getPnsnrDtlsFromPpoNo(Map<String,Object> inputMap);
	
	ResultObject insertPensionerSeenDtls(Map<String,Object> inputMap);
	
	ResultObject getReceivedLifeCertList(Map<String,Object> inputMap);
	
	ResultObject approveLifeCertificates(Map<String,Object> inputMap);
	
	ResultObject saveArrearPaymentDtls(Map<String,Object> inputMap);
}
