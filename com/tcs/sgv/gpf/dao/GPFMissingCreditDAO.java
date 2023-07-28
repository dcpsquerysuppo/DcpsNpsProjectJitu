package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 18, 2012
 */

public interface GPFMissingCreditDAO extends GenericDao
{
	String getGpfAccNo(String lStrSevaarthId) throws Exception;
	
	String getBillGroupId(String lStrSevaarthId) throws Exception;
	
	Long getMissingCreditId(String lStrGpfAccNo, String lStrFinYear, String lStrMonth, Long lLngBillGroup, String lStrStatus)throws Exception;
	
	String chkForApprovedReq(String lStrGpfAccNo, String []lArrFinYear, String []lArrMonth, Long lLngBillGroup)throws Exception;
	
	String getMonthNameFromID(String lStrMonth)throws Exception;
	
	List getInstAmountTrnIdForCurrentRA(String lStrGpfAccNo)throws Exception;
	
	List getDraftData(String lStrGpfAccNo, Long lLngBillGroup)throws Exception;
	
	List loadDraftDataWorkList(Long lLngPostId)throws Exception;
}
