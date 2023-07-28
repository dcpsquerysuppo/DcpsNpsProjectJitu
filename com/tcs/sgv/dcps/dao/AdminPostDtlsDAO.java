package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;

public interface AdminPostDtlsDAO {
	
	List<ComboValuesVO> getFieldDeptFromAdminDeptCode(Long lLngAdminDeptCode);
	
	List<ComboValuesVO> getDdoListFromFieldDept(String lStrFieldDeptCode);
	
	String getLocationCodeForDDO(String lStrDDOCode);
	
	List getAllBillsFromLocation(Long locId);
	
	List getPostNameForDisplay(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn );
	
	List getPostNameForDisplayThruEmpId(Long locId, String empId, Long langId);
	
	List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn,Long designationId,Long BillGroupId);
	
	List getPostNameForDisplayWithSearch(Long locId,String lPostName,String PsrNo,String BillNo,String Dsgn);
	
	String getOfficeCityClassAndHra(Long lLngOfficeId);
}
