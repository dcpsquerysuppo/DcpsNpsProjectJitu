/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 29, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls;



/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Jun 29, 2011
 */
public interface RevisedPensionCaseVOGenerator {

	ResultObject generateMap(Map inputMap);
	List<TrnPensionerRivisionDtls> generateTrnPensionerRivisionDtlsVO(Map<String, Object> inputMap) throws Exception;
}
