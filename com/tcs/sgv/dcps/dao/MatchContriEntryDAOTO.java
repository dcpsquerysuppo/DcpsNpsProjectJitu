/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Dec 12, 2013		Ashish Sharma								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Ashish Sharma
 * @version 0.1
 * @since JDK 7.0 Dec 12, 2013
 */
public interface MatchContriEntryDAOTO extends GenericDao {

	public List getAllTreasuriesForMatchedEntries(String fromDate, String toDate, String subTresury, Long acMain);

	public List getAllTreasuriesForUnMatchedEntries(String lStrFromDate,
			String lStrToDate);
	
	public List getUnMatchedVouchersForMatching(String lStrFromDate,
			String lStrToDate, Long treasuryCode);
	
	/*
	public List getUnMatchedVouchersAllForMatching(String lStrFromDate,
			String lStrToDate, Long treasuryCode);
			*/
	
	public void updateVouchersManuallyMatched (Long voucherIdPk) throws Exception;
	
	public List getUnMatchedVouchersForMatchingFromMstContriVoucherDtls(String lStrFromDate,
			String lStrToDate, String treasuryCode);
	
	
	public List getUnMatchedVouchersForMatchingFromTreasuryNetData(String lStrFromDate,
			String lStrToDate, String treasuryCode);
	
	public List getAllTreasuriesForUnMatchedEntriesMstcontri(String lStrFromDate,String lStrToDate, String subTresury);
	
	public List getAllTreasuriesForUnMatchedEntriesTreasuryNet(String lStrFromDate,String lStrToDate,String subTresury);

	public List getSubTresuryList(String strLocationCode);

	public List getAllSubTreasuries (String treasuryCode) ;
	
	public Long getRoleOfUserFrmPostId (Long postId);



	public void updateVouchersManuallyTrnsMatched(Long strVoucherIdsPks,
			Long strNewVchrNos, String strNewSchemeCode, Date dtNewVchrDate)throws Exception;

	public List selectDcpsIdForVoucherInTrn(Long strVoucherIdsPks) throws Exception;

	public List getPayBillId(Long dcpsId, Long monthId, String yearDesc)throws Exception ;

	public void updateVouchersManuallyPayMatched(Long strNewVchrNos,
			Date dtNewVchrDate, Long payBillId, Long monthId, String yearDesc)throws Exception;

	public List getAllDDOForTreasury(String tresuryCode);

	public List getFinyears();

	public Boolean checkUpdateStatusDtls(String dddoCode, String strNewVchrNo,
		 Date nvoucherdate,String strNewSchemeCode)throws Exception;


	public List getAcMainDetails()throws Exception;


	public Long getSubTreasuryList(String strLocationCode);

	
	
	

}
