/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 25, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0
 * May 25, 2011
 */
public interface FeedbackService {

	public ResultObject loadFeedbackForm(Map<String, Object> inputMap)
	throws Exception ;
	
	public ResultObject saveFeedback(Map<String, Object> inputMap)
	throws Exception ;
	
	public ResultObject showFeedbacksForSRKA(Map<String, Object> inputMap)
	throws Exception ;
}