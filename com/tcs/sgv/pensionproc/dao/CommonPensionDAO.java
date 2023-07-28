/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 2, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 2, 2011
 */
public interface CommonPensionDAO {

	List<ComboValuesVO> getAllState(Long langId) throws Exception;

	List<ComboValuesVO> getAllDepartment(Long lLngDepartmentId, Long langId) throws Exception;

	List<ComboValuesVO> getDistrictsOfState(Long stateId, Long langId) throws Exception;

	List<ComboValuesVO> getAllTreasury(List<Long> lLstDepartmentId, Long langId) throws Exception;

	List<ComboValuesVO> getAllDesignation(Long langId) throws Exception;

	List<ComboValuesVO> getHooFromDepartmentLoc(Long strDepCode, Long langId) throws Exception;

	List<ComboValuesVO> getBankNames(Long lLngLangId, String lStrLocCode) throws Exception;

	List<ComboValuesVO> getBranchesOfBank(String bankCode, Long langId, String locCode) throws Exception;
	
	String getIfscCodeFromBranchCode(Long lLngBranchCode, String lStrLocationCode) throws Exception ;
	
	String getStateNameFromStateCode(Long lLngStateCode,Long lLngLangId) throws Exception ;
	
	String getDistrictNameFromDistrictCode(Long lLngDistrictCode,Long lLngLangId) throws Exception ;
	
	List<ComboValuesVO> getAdminDept() throws Exception;
	
	Long getAdminDeptNameFromFieldDept(Long lLngFieldDept) throws Exception;

	List<ComboValuesVO> getFieldDeptFromAdmDept(Long lLngFieldDept) throws Exception;
}
