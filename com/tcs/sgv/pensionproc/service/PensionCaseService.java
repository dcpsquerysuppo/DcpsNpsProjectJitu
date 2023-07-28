/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 1, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 1, 2011
 */
public interface PensionCaseService {
	public ResultObject loadPensionCaseInwardForm(Map<String, Object> inputMap);

	/**
	 * 
	 * <H3>Description -</H3>
	 * 
	 * 
	 * 
	 * @author Anjana Suvariya
	 * @param inputMap
	 * @return
	 * @throws Exception
	 */
	public ResultObject insertPensionCase(Map inputMap) throws Exception;
	
	public ResultObject loadViewOrAddAddressPopup(Map inputMap) throws Exception;
	
	public ResultObject getBranchesOfBank(Map inputMap) ;
	
	public ResultObject getValidEmoluments(Map inputMap) throws Exception;
	
	public ResultObject getCvpRate(Map inputMap) throws Exception;
	
	public ResultObject getDesgListForPensionCase(Map inputMap) throws Exception;
	
	public ResultObject loadPenProcInwardForm(Map inputMap) throws Exception;

	public ResultObject getIfscCodeFromBranchCode(Map<String, Object> inputMap);
	
	public ResultObject updateCaseStatus(Map inputMap);
	
	public ResultObject approvePensionCaseForAg(Map inputMap);
}
