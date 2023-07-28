package com.tcs.sgv.pensionpay.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;


public interface TrnPensionBillDtlsDAO extends GenericDao<TrnPensionBillDtls, Long> {

	List getTrnPensionBillDtls(long TrnPensionBillHdrPK) throws Exception;
	TrnPensionBillDtls getTrnPensionBillDtlsVo(long TrnPensionBillHdrPK) throws Exception;
	String getDesgDesc(String lStrDesignation) throws Exception;
	int getMaxOfForMonth(String lStrPensionerCode, String lStrPPONumber, String lStrRecoveryFromFlag) throws Exception;
}
