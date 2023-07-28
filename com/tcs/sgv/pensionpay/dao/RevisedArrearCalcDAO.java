package com.tcs.sgv.pensionpay.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeRate;

public interface RevisedArrearCalcDAO {

	String getMaxOfForMonth(String lStrPensionerCode) throws Exception;
	
	public List getPensionerDtlsFromPPONo(String lStrPPONo,String lStrBankCode,String lStrBranchCode,String lStrAccountNo,String lStrLocationCode) throws Exception;
	
	public List<RltPensionHeadcodeRate> getRateFromHeadcodeRateRlt(Long lLngHeadcode, String lStrFieldType, Double lDbPnsnBasic, Date lDtFromDate, Date lDtToDate) throws Exception ;
	
	public RltPensionHeadcodeRate getDPRate(String lStrFieldType) throws Exception ;
	
	public List<ComboValuesVO> getDaRateFromPayCommission(String lStrPayCommissionType) throws Exception;
}
