/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 10, 2011		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;


/**
 * Class Description - This interface declares all the methods of Six Pay arrear
 * and FP change from 7years to 10 years arrear functionality.
 * 
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Jun 10, 2011
 */
public interface SixPayFPArrearService {

	/**
	 * 
	 * <H3>Description -</H3> This service method will load the initial data to
	 * display the arrear page
	 * 
	 * 
	 * @author Vrajesh Raval
	 * @param inputMap
	 *            Map containing framework and session data
	 * @return Object containing the result of the service to be displayed on
	 *         the screen
	 */
	ResultObject getArrearScreen(Map inputMap);

	/**
	 * 
	 * <H3>Description -</H3> This service method will load the initial data to
	 * display the arrear page
	 * 
	 * 
	 * @author Vrajesh Raval
	 * @param inputMap
	 *            Map containing framework and session data
	 * @return Object containing the result of the service to be displayed on
	 *         the screen
	 */
	ResultObject getSixPayPPODtls(Map inputMap);
	
	ResultObject saveSixPayArrears(Map inputMap);
	
	ResultObject loadSixPayFPArrearConfig(Map inputMap);
}
