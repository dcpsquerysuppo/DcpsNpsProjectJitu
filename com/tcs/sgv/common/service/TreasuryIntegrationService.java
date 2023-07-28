/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 21, 2012		Vrajesh Raval								
 *******************************************************************************
 */
package com.tcs.sgv.common.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;


/**
 * Class Description -
 * 
 * 
 * @author Vrajesh Raval
 * @version 0.1
 * @since JDK 5.0 Aug 21, 2012
 */
public interface TreasuryIntegrationService {

	ResultObject forwardPensionBillDataToBEAMS(Map objectArgs);

	HashMap getBillApprvFrmBEAMSWS(HashMap billData, String BEAMSWSURL);

	ResultObject getBeamsAuthSlip(Map objectArgs);

	ResultObject saveBEAMSVoucherDetails(Map objectArgs) throws Exception;

	byte[] getBytesFromInputStream(InputStream lObjInputStream);

	void setSessionInfo(Map<String, Object> inputMap);

	ResultObject generateSBICMPExcelFile(Map objectArgs) throws Exception;
	
	void saveVoucherDetailsFromArthwahini(List<Map> lLstVoucerDtls,Map objectArgs);
	
	// Mubeen 22 Nov 2012
	ResultObject checkBillForSchemePaymode(Map inputMap);
	
}
