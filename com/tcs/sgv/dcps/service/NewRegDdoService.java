/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 8, 2011		Vihan Khatri								
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
 * @since JDK 5.0 Apr 8, 2011
 */
public interface NewRegDdoService {

	public ResultObject loadRegistrationForm(Map inputMap) throws Exception;

	public ResultObject saveDCPSEmpData(Map inputMap);

	public ResultObject updateDCPSEmpData(Map inputMap);

	public ResultObject populateDesigsUsingAjax(Map<String, Object> lMapInputMap);

	public ResultObject populateGroupUsingAjax(Map<String, Object> inputMap)
			throws Exception;

	public ResultObject saveNomineeDetails(Map inputMap);

	public ResultObject forwardRequestToDDO(Map objectArgs);

	public ResultObject forwardRequestToTreasury(Map objectArgs);

	public ResultObject showAndUpdateForm(Map inputMap) throws Exception;

	public ResultObject rejectRequestDDO(Map inputMap);

	public ResultObject popUpEmpDtls(Map inputMap) throws Exception;

	public ResultObject getDesigsForPFDAndCadre(Map<String, Object> lMapInputMap);
	
	public ResultObject getFirstDesignationForAutoComplete(
			Map<String, Object> inputMap) throws Exception;
	
	public ResultObject viewApprovedForms(Map inputMap) throws Exception;

	public ResultObject viewFormsForwardedByAsst(Map inputMap) throws Exception;
	
	public ResultObject viewDraftForms(Map inputMap) throws Exception;
	
	public ResultObject getLookupValuesForParentAG(Map<String, Object> lMapInputMap);
	
	public ResultObject loadMapDDOAsst(Map inputMap);
}
