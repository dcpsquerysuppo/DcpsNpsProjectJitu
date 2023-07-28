package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

public interface BankDetailsUpdateDao extends GenericDao  {
	
	public List getDDO(String TOCode);

	public void updateBankDetails(String string, String string2, String string3, String string4);

	public List getAllDcpsEmployeesZPForBankUpdate(String strDDO);

	public List getBankNames();

	public List getBankBranchList(String cmbBank);

	public String checkBankDetails(String bank, String accountNumber);

	public Long checkBankAccountNumber(String bank, String accountNumber,String empId);
	
}
