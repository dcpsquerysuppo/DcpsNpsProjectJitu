/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 29, 2011		383385								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.List;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.RltBankBranchTmp;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Sep 29, 2011
 */
public interface BankBranchMappingDAO  extends GenericDao{
	
	List<ComboValuesVO> getPasBankList(String lStrLocationCode) throws Exception;
	List getPasBranchListFromBankCode(String lStrLocationCode, String lStrPasBankCode) throws Exception;
	List<ComboValuesVO> getDistrictList(String lStrLocationCode) throws Exception;
	List<RltBankBranchTmp> getBranchsFromBankCode(String lStrLocationCode, String lStrBankCode, String lStrDistrictName) throws Exception;
	void deleteBankMpgDtls(String lStrLocationCode, String lStrPasBankCode) throws Exception;
	void updateBankBranchMapping(String lStrLocationCode, String lStrIfmsBankCode, String lStrIfmsBranchCode, String lStrPasBankCode, String lStrPasBranchCode) throws Exception;
	List<ComboValuesVO> getBankListFromDistrict(String lStrLocationCode,String lStrDistrictName) throws Exception;

}
