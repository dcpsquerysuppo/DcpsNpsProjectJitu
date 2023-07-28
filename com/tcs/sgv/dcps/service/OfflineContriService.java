/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Vihan Khatri								
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
 * @since JDK 5.0 Apr 7, 2011
 */
public interface OfflineContriService {

	public ResultObject loadOfflineDCPSForm(Map inputMap) throws Exception;

	public ResultObject saveContributions(Map inputMap);

	public ResultObject deleteContributions(Map inputMap);

	//public ResultObject forwardRequestToTO(Map objectArgs) throws Exception;

	public ResultObject getSchemeforBillGroup(Map<String, Object> inputMap)
			throws Exception;

	public ResultObject approveContributions(Map objectArgs);

	public ResultObject getBillGroupsForDdo(Map inputMap);

	public ResultObject FwdContriToDDO(Map objectArgs) throws Exception ;

	public ResultObject FwdContriToTO(Map objectArgs);

	public ResultObject saveVoucherDtlsForContri(Map inputMap) throws Exception;

	public ResultObject getDDOsForTreasury(Map inputMap);

	public ResultObject revertAcceptedContri(Map inputMap) throws Exception;
	
	public ResultObject displaySchemeNameForCodeInMissingCredit(Map inputMap) ;

}
