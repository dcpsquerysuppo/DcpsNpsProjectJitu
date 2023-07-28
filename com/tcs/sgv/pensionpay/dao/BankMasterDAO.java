package com.tcs.sgv.pensionpay.dao;

import java.util.List;

import com.tcs.sgv.common.valueobject.MstBank;
import com.tcs.sgv.common.valueobject.MstBankPay;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.eis.valueobject.HrEisBankMst;

public interface BankMasterDAO extends GenericDao{

	List getAllBankMasterData(long langId);
	public List searchBankMasterData(long langId, String lStrBankName);
	public MstBank getBankIdData(long BankId,long langId);
	public List checkBankName(String bankName,long langId);
	public List bankMasterAutoComplt(long langId, String lStrBankName);
}
