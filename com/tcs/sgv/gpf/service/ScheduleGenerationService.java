/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 29, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Aug 29, 2011
 */
public interface ScheduleGenerationService {
	ResultObject loadScheduleGenerationForm(Map<String, Object> inputMap);

	ResultObject generateGPFSchedule(Map<String, Object> inputMap);

	ResultObject discardGPFSchedule(Map<String, Object> inputMap);

	ResultObject approveGPFSchedule(Map<String, Object> inputMap);
}
