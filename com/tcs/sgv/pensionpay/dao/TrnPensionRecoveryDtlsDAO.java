package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;

/**
 * TrnPensionRecoveryDtls specific Service Interface
 * 
 * @author Rupsa Mukherjee
 * @version 1.0
 */

public interface TrnPensionRecoveryDtlsDAO extends
		GenericDao<TrnPensionRecoveryDtls, Long> {

	ArrayList<TrnPensionRecoveryDtls> getTrnPensionRecoveryDtls(
			String lStrPensionerCode, String lStrStatus) throws Exception;

	ArrayList<TrnPensionRecoveryDtls> getTrnPensionRecoveryDtlsVOArray(
			String lStrPensionerCode, String lStrRecoveryFromFlagCVP,
			String lStrRecoveryFromFlagDCRG,
			String lStrRecoveryFromFlagPension, String lStrRecoveryFromFlagOMR,
			String lStrRecoveryFromFlagMonthly,
			String lStrRecoveryFromFlagChallan) throws Exception;

	ArrayList<TrnPensionRecoveryDtls> getTrnPensionRecoveryChallanDtlsVOArray(
			String lStrPensionerCode, String lStrRecoveryFromFlag)
			throws Exception;

	List getPKForTableTrnPensionRecoveryDtls(Long lLngPensionRequestId,
			String lStrPensionerCode, String lStrTypeFlag) throws Exception;
}
