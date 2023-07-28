/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jan 31, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Jan 31, 2011
 */
public interface TreasuryService {

	ResultObject loadEmpDeputation(Map inputMap) throws Exception;

	ResultObject dcpsEmpDeputation(Map inputMap) throws Exception;
	
	ResultObject loadDummyOffice(Map inputMap) throws Exception;

	ResultObject insertDummyOffice(Map inputMap) throws Exception;

	ResultObject saveContriForDeptn(Map inputMap);

	/*
	ResultObject loadTOPhyFormRcvdList(Map inputMap) throws Exception;

	ResultObject loadSixthPCArrearsYearlyTO(Map inputMap) throws Exception;

	ResultObject loadsixthPCYearlyInstallmentTO(Map inputMap) throws Exception;

	ResultObject approveSixthPCArrearsYearlyByTO(Map inputMap) throws Exception;

	ResultObject rejectSixthPCArrearsYearlyByTO(Map inputMap) throws Exception;
	*/
	}
