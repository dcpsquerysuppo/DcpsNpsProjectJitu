/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Sep 14, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.dcps.valueobject.MstBrokenPeriodPay;
import com.tcs.sgv.dcps.valueobject.RltBrokenPeriodAllow;
import com.tcs.sgv.dcps.valueobject.RltBrokenPeriodDeduc;
import com.tcs.sgv.eis.valueobject.HrEisEmpMst;

/**
 * Class Description -
 * 
 * 
 * @author Kapil Devani
 * @version 0.1
 * @since JDK 5.0 Sep 14, 2011
 */
public interface BrokenPeriodDAO extends GenericDao {

	public List SearchEmployeeWithName(String lStrSearchName, long locId) throws Exception;

	public String getDesignationName(String lStrEmpId) throws Exception;

	public String getOfficeName(String lStrEmpId) throws Exception;

	public String getGPFNo(String lStrEmpId) throws Exception;
	
	public HrEisEmpMst getHrEisEmpMstVOForEmpMpgId(Long empMpgId);
	
	public List getAllowancesListForGivenEmp(Long lLongEmpId);
	
	public List getDeductionsListForGivenEmp(Long lLongEmpId);
	
	public Boolean checkBrokenPeriodPayExistsOrNot(Long lLongEmpId,Long lLongYearId,Long lLongMonthId, String strBillType);
	
	public List<MstBrokenPeriodPay> getAddedBrokenPeriodPaysForEmp(Long lLongEmpId,Long lLongYearId,Long lLongMonthId, String strBillType);
	
	public List getAddedAllowancesForEmp(Long lLongRltBrokenPeriodId);
	
	public List getAddedDeductionsForEmp(Long lLongRltBrokenPeriodId);
	
	public void deleteAllBrokenPeriodPaysForPk(Long lLongBrokenPrdId);
	
	public void deleteAllBrokenPeriodAllowancesForBrknPrdId(Long lLongBrokenPrdId);
	
	public void deleteAllBrokenPeriodDeductionsForBrknPrdId(Long lLongBrokenPrdId);
	
	public String getSevarthId(long emloyeeId);
	
	public String getGPFOrDCPSNo(String user_id);
	
	public int isGenerated(long eisEmpId, long month, long year);
	
	public long payBilYr(long month, long year);
	
	public List getEmpMappedAllownace(long empId,String locId);

	public int getyearfromFinYear(String finyearID,int monthId);
	
	public List getAllowIDfrmAllowdedCode(String allowDedidStr);
	
	public List getDedIDfrmAllowdedCode(String allowDedidStr);

	public Boolean checkPreviousBillExistsOrNot(Long longEisEmpId,
			Long longYear, Long longMonth);

	public List checkBrokenPeriodExistsOrNot(Long longEisEmpId, Long longYear,
			Long longMonth);
}
