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
public interface SixPCArrearsYearlyService {
	
	ResultObject loadsixthPCYearlyInstallment(Map inputMap) throws Exception;
	ResultObject forwardDCPSArrearsYearly(Map inputMap) throws Exception;
	ResultObject saveSixPCArrearsYearly(Map inputMap) throws Exception;
	ResultObject loadSixthPCArrearsYearlyDDO(Map inputMap) throws Exception;
	ResultObject approveSixthPCArrearsYearlyByDDO(Map inputMap) throws Exception;
	ResultObject rejectSixthPCArrearsYearlyByDDO(Map inputMap) throws Exception;
	ResultObject loadSixthPCArrearsYearlyTO(Map inputMap) throws Exception;
	ResultObject approveSixthPCArrearsYearlyByTO(Map inputMap) throws Exception;
	ResultObject rejectSixthPCArrearsYearlyByTO(Map inputMap) throws Exception;
	ResultObject loadSixPCArrearAmountSchedule(Map inputMap) throws Exception; 
	ResultObject loadSixPCArrearAmountScheduleDDO(Map inputMap) throws Exception; 

}
