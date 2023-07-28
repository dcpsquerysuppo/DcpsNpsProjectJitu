package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;

public interface EmpBankUpdateDao extends GenericDao {
	public List getTreasury();

	public List<ComboValuesVO> getsubTreasuryForTreasury(String strTreasuryId);
	public List<ComboValuesVO> getDDOForSubTreasury(String lStrSubTreasuryName);
	public List getAllDcpsEmployeesZPForBankUpdate(String ddoCode);
	public List getBankNames();
	public List getBranchList(String cmbBank);
	public void updateBankDetails(String string, String string2, String string3, String string4);
}
