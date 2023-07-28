package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;

public interface DeputationEmpBankUpdateDao {
	
	
	public List getAdminDepartment();


	
	public List<ComboValuesVO> getFieldDepartmentFromAdminDepartment(String strTreasuryId);
	public List getAllDcpsDeputationEmployeesZPForBankUpdate(String ddoCode,String SevaarthId);

	public List checkBankAccountNumberForDeputation(String bank, String accountNumber,String empId);
	
	public int  CheckFieldDepartmentOfemployee(String SevaarthId);
	
}
