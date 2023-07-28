/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 30, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.PostEmpContri;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 May 30, 2011
 */
public interface PostEmpContriDAO extends GenericDao {
	//List getAllContributions(String userType, Long finYear, String contriMonth);
	
	public List getAllContributions(String userType, Long finYear, String strAcDcpsMntndBy);

	Long getSancBudget(Long finYear);

	Long getSancBudgetPK(Long finYear);

	public PostEmpContri getPostEmpContriVOForGivenMonthAndYear(Long monthId, Long yearId);

	public Long getExpenditure(Long finYear, String strAcDcpsMntndBy);

	public String getBillNumber(Long finYear, String strAcDcpsMntndBy);

	public Long getExcessAmount(Long finYear, String strAcDcpsMntndBy);

	//public Double getExpInCurrBill(String finYearCode, Long monthId);

	//public void updateBillNoAndYearIdForPostEmpcontri(String lStrBillno, Long lLongYearId, String finYearCode,Long monthId);
	
	public void updateBillNoAndYearIdForPostEmpcontri(String lStrBillno, Long lLongYearId, String finYearCode,String lStrFromDate,String lStrToDate) ;

	public void updateVoucherPostEmpStatusOnApproval(String lStrBillno, Long lLongYearId) throws Exception ;

	public void updatePostEmplrVoucherDtlsOfApprovedBills(String lStrBillno, Long lLongYearId,
			Long lLongEmplrVoucherNo, Date lDateEmplrVoucherDate) throws Exception ;

	void updateTrnDcpsContributionList(String finYearCode, Long monthId,String lStrBillNo,Long lLongYearId);
	
	public Double getExpInCurrBillPrdWise(Long finYearId,String lStrFromDate,String lStrToDate);
	
	public Double getExpInCurrBillPrdWiseFromTrn(Long finYearId,String lStrFromDate,String lStrToDate,String lStrAcDcpsMntndBy) ;
	
	public void updateBillNoAndYearIdForPostEmpcontriInTrn(String lStrBillno, Long lLongYearId, String finYearCode,String lStrFromDate,String lStrToDate,String lStrAcDcpsMntndBy,Date lDateFromDate,Date lDateToDate) throws Exception;
	
	public void updateTrnDcpsContributionListInTrn(String lStrBillNo,Long lLongYearId) throws Exception;
	
	public void updateBillNoAndYearIdForPostEmpcontriWithAcMntndBy(String lStrBillno, Long lLongYearId, String finYearCode,String lStrFromDate,String lStrToDate,String lStrAcDcpsMntndBy,Date lDateFromDate,Date lDateToDate) throws Exception;
	
	public void updateBillNoAndYearIdForPostEmpcontriInTrnAndMstBoth(String lStrBillno, Long lLongYearId,String lStrAcDcpsMntndBy,Date lDateFromDate,Date lDateToDate) throws Exception;
	
	public void updateVoucherPostEmpStatusOnApprovalInTrnAndMstBoth(String lStrBillno, Long lLongYearId) throws Exception;

	public Long getSancBudgetForAcc(Long lngFinYear, String strAcDcpsMntndBy);

	public String getBillNumber(Long finYear);
	
	public String getFinYear(String lStrPostEmpContriId,String accMain);
	
	public String accMain(String accMain); 
	
	public Date getBillGendt(String lStrPostEmpContriId,String accMain);
}
