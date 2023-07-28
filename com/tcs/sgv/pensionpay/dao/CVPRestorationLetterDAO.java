/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 25, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Aug 25, 2011
 */
public interface CVPRestorationLetterDAO extends GenericDao 
{
	List getCVPRestorationDtls(String lStrFromDate,String lStrToDate,String gStrLocationCode);
	List getRstrnLetterDtls(String lStrPensionerCode,Integer lIntYearMonth);
	BigDecimal getCommutedAmnt(String lStrPensionerCode) ;
	String getOffiCeAddr(String lStrLocationCode);
	Boolean checkBillIsExistsOrNot(String lStrPensionerCode,Integer lIntYearMonth);
	
}
