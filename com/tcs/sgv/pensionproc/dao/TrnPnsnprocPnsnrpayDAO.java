package com.tcs.sgv.pensionproc.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * 
 * Class Description - 
 *
 *
 * @author Sneha
 * @version 0.1
 * @since JDK 5.0
 * Feb 3, 2011
 */


public interface TrnPnsnprocPnsnrpayDAO extends GenericDao{

	Long getPayScaleDtls(Long lLngEmpId);
	
	Long getBasicPay(Long lLngEmpId);
	
	List getAvgPayDtls(Long lLngEmpId,String lStrFormYearMonth,String lStrToYearMonth,String lStrPayComm);
}
