/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 29, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.eis.valueobject.HrEisScaleMst;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Jun 29, 2011
 */
public interface GPFRequestProcessDAO extends GenericDao {
	public List getEmployeeDetail(String lStrSevaarthId, String empName, String criteria, String lStrLocationCode,
			String lStrUser) throws Exception;

	public List getGPFRequestList(String lStrUserType, String lStrPostId, String lStrLocationCode, String lStrCriteria,
			String lStrName, Date lDtSaveDate);

	public List getWithdrawalDetail(String lStrGpfAccNo, String lStrAdvanceType);

	List getAdvanceHistory(String lStrGpfAccNo, Long lLngYearId);

	Long getDPOrGP(Long lLngEmpId, String lStrPayComm);

	List getGPFAccountBalance(String lStrGpfAccNo, Long lLngYearId);

	String getNewTransactionId(String lStrSevaarthId);
	
	public String getDdoCode(String lStrLocationCode);

	public Long getDDOPostIdForVerifierHo(Long lLngPostId, String lStrUserType);

	public String getDdoCodeForDDO(Long lLngPostId);

	// public Double getMonthlySubscription(String lStrGpfAccNo, Integer
	// lIntMonthId, Integer lIntFinYearId);

	List getAdvanceDetail(String lStrGpfAccNo, String lStrAdvanceType);

	HrEisScaleMst getPayScaleData(Long lLngEmpId);

	Boolean withdrawalExistsForFinYear(String strGpfAccNo, Long lLngFinYearId);

	List getEmpNameForAutoComplete(String searchKey, String lStrDdoCode);

	Double getOpeningBalForCurrYear(String lStrGpfAccNo, Long lLngYearId) throws Exception;

	String getLocationCodeOfUser(Long lLngPostId) throws Exception;

	List getDraftRequestList(String lStrPostId, String lStrLocationCode, String lStrCriteria, String lStrName,
			Date lDtSaveDate) throws Exception;

	String getDistrictNameForId(Long lLngDistrictId);

	String getStateNameForId(Long lLngStateId);
	
	public boolean isDataEntryComplete(String lStrSevaarthId);
	
	String getDdoCodeForDEO(String lStrLocationCode)throws Exception;
	
	List getDsgnAndOfficeName(String lStrSevaarthId);
	
	Double getClosingBalance(String lStrGPFAccNo);
	
	String getEmployerOfficeName(String lStrDDOCode);
	
	String getEmployerDsgnName(Long lLngPostId);
	
	String getTreasuryNameOfEmp(String lStrDDOCode);
	
	Date getEmpRetirmentDate(String lStrSevaarthId);
	
	Double getLatestSubscription(String lStrGpfAccNo);
	
	String getSevaarthIdFromName(String lStrName);

	public String getRegularSubscription(String strSevaarthId, String strDdoCode);
	
	public String getGradePayFrmSevaarthId(String lStrSevaarthId);
	public Long getAdvanceHistoryByAdvanceType(String lStrGpfAccNo, Long lLngYearId,String advanceType) ;
	public String getEmployerNameFrmPostId(String postId);
	public String getPurposeNameFrmId(String lookUpID);	
	public String getPrpsFrmTransactionId(String transactionId);
	public List getDDODeptDetails(String empSevaarthCode);

	public Date getApplicationDateFrmTranId(String strTransactionId);
}
