/* Copyright TCS 2013, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	DEC 12, 2013		Ashish Sharma								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Ashish Sharma
 * @version 0.1
 * @since JDK 7.0 Dec 12, 2013
 */
public interface MatchContriEntryServiceTO {

	public ResultObject loadMatchContriEntryFormTO(Map inputMap);
	
	public ResultObject loadUnMatchContriEntryFormTO(Map inputMap);
	
	public ResultObject loadManualMatchTO(Map inputMap) ;

}
