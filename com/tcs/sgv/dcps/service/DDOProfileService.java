/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 24, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.Map;

import com.tcs.sgv.core.valueobject.ResultObject;

/**
 * Class Description -
 * 
 * 
 * @author Kapil Devani
 * @version 0.1
 * @since JDK 5.0 Feb 24, 2011
 */
public interface DDOProfileService {

	public ResultObject populateBranchNamesUsingAjax(
			Map<String, Object> lMapInputMap);

	public ResultObject attachGroup(Map<String, Object> inputMap);

	public ResultObject loadDdoEmpSelection(Map inputMap) throws Exception;

	public ResultObject loadDdoEmpDeselction(Map inputMap) throws Exception;

	public ResultObject ddoEmpDeselection(Map inputMap) throws Exception;

	public ResultObject ddoEmpSelection(Map inputMap) throws Exception;

	public ResultObject loadDCPSTierDDO(Map<String, Object> inputMap)
			throws Exception;

	public ResultObject displayIFSCCodeForBranch(Map inputMap) throws Exception;
	
	public ResultObject displayIFSCCodeForBranchBsrCode(Map inputMap) throws Exception ;

	public ResultObject detachGroup(Map<String, Object> inputMap);
	
	public ResultObject chkDtLaterThanDeSelectionDt(
			Map<String, Object> inputMap);
	//added by shailesh
	public ResultObject getPostTypeLst(Map<String, Object> inputMap );
	public ResultObject billGeneratedOrNot(Map<String, Object> inputMap );
}
