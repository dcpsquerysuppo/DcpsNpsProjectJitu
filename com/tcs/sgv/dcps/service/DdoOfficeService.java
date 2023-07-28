/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 26, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 26, 2011
 */
public interface DdoOfficeService {

	public ResultObject loadDdoOffice(Map inputMap) throws Exception;

	public ResultObject saveDdoOffice(Map<String, Object> inputMap)
			throws Exception;

	public ResultObject updateDdoOffice(Map<String, Object> inputMap)
			throws Exception;

	public ResultObject popUpDdoOfficeDtls(Map inputMap);

}
