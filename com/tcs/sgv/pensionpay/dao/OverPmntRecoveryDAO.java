/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jul 1, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Jul 1, 2011
 */
public interface OverPmntRecoveryDAO extends GenericDao
{
	public List getPPODtls(String lStrPPONo,Long langId,String lStrLocationCode);
	public List getRecoveryDtls(String lStrRequestId,String lStrLocationCode);
	public Long getMaxRequestId(String locationCode);
	public List getEligibleBills(Date lDtExpiryDate,String lStrPnsnrCode,String lStrBillType,String gStrLocationCode);
	public List getAllPnsnrCodes(); 
	public List getRecoveryLtrDtls(String lStrPnsnrCode,String lStrLocationCode);
	public List getfamilyDtls(String lStrPnsnrCode,String lStrLocationCode);
	public List getPeriodDates(String lStrBillType,Date lDtDeathDate,String lStrPnsnrCode,String lStrLocationCode);
}
