/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 8, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Apr 8, 2011
 */
public interface SixPCArrearsService 
{
	ResultObject loadsixthPCArrearsEntry(Map inputMap) throws Exception;
	ResultObject createandfrwrdWF(Map inputMap) throws Exception;
	ResultObject saveSixPCArrears(Map inputMap) throws Exception;
	ResultObject loadSixthPCArrearsDDO(Map inputMap) throws Exception;
	ResultObject rejectSixthPCArrears(Map inputMap) throws Exception;
	ResultObject approveSixthPCArrears(Map inputMap) throws Exception;
	ResultObject insertSixPCArrears(Map inputMap) throws Exception;
}
