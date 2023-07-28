package com.tcs.sgv.pensionpay.dao;

import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.RltBankBranch;
import com.tcs.sgv.common.valueobject.RltBankBranchPay;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.eis.valueobject.HrEisBranchMst;

public interface BranchMasterDAO extends GenericDao<RltBankBranch, Long>
{

	List getAllBranchMasterData(long langId);	
	public RltBankBranch getBranchIdData(String branchId);	
	public List checkBranchName(String branchName,String bank_code,long langId);	
	public List getBranchIdFromBankCode(String branchId);	
	public List<ComboValuesVO> getAllTreasury() throws Exception;
	public String getBankCodeFromId(Long lLngBankId);
	public String checkIFSCcode(String ifscCode);
	public List branchMasterAutoComplt(long langId, String lStrBranchName, String lStrBankCode);
	public List searchBranchMasterFromBranch(long langId, String lStrBranchName);
	public List searchBranchMasterFromBank(long langId, String lStrBankName);
	public List searchBranchMasterFromBankBranch(long langId, String lStrBankName, String lStrBranchName);
	public String getBankCodeFromName(String lStrBankName) throws Exception;
}
