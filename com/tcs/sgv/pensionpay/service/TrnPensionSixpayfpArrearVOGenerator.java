/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 13, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSixpayfpArrear;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Jun 13, 2011
 */
public interface TrnPensionSixpayfpArrearVOGenerator 
{
	ResultObject generateMap(Map inputMap);
	List<TrnPensionSixpayfpArrear> generateTrnPensionSixpayfpArrearVO(Map inputMap);
}
