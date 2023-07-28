/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 8, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Apr 8, 2011
 */
public interface SixPCArrearsYearlyDAO extends GenericDao {

	Integer getEmpListForSixPCArrearsYearlyCount(String lStrDDOCode,
			Long finYearId, String lStrDesignation) throws Exception;

	List getEmpListForSixPCArrearsYearly(String lStrDDOCode, Long finYearId,
			String lStrDesignation, Map displaytag) throws Exception;

	String getDdoCodeForDDO(Long lLngPostId) throws Exception;

	Integer getEmpListForSixPCArrearsYearlyDDOCount(String lStrDDOCode,
			Long finYearId, String lStrPostId, String lStrDesignation)
			throws Exception;

	List getEmpListForSixPCArrearsYearlyDDO(String lStrDDOCode, Long finYearId,
			String lStrPostId, String lStrDesignation, Map displaytag)
			throws Exception;

	Integer getEmpListForSixPCArrearsYearlyTOCount(String lStrDDOCode,
			Long finYearId, String lStrPostId, String lStrDesignation,
			String lStrSchedule) throws Exception;

	List getEmpListForSixPCArrearsYearlyTO(String lStrDDOCode, Long finYearId,
			String lStrPostId, String lStrDesignation, Map displaytag,
			String lStrSchedule) throws Exception;

	List getEmpListForSixPCArrearAmountSchedule(String lStrDDOCode, Long yearId)
			throws Exception;

	List getYears() throws Exception;

	String getDdoCode(Long lLngAsstPostId) throws Exception;

	List getAllDDOListForArrearsForwarded(String lStrPostId, String lStrUserType,String gStrLocationCode)
			throws Exception;

	Long getDcpsEmpIdFromSirxthPCYearlyId(Long lLngDcpsSixthPCYearlyId)
			throws Exception;

	void update(Long lLngDCPSEmpId, Long lLngCurPostID, Long lLngCurUserId,
			Date lDtCurDate) throws Exception;

	void updateForTO(Long lLngDcpsSixPcId, Long lLngAmountPaid,
			Long lLngCurPostID, Long lLngCurUserId, Date lDtCurDate)
			throws Exception;

	void rejectForTO(Long lLngDCPSEmpId, Long lLngCurPostID,
			Long lLngCurUserId, Date lDtCurDate) throws Exception;

	void rejectForDDO(Long lLngDCPSEmpId, Long lLngCurPostID,
			Long lLngCurUserId, Date lDtCurDate) throws Exception;

	List getYearsForSixPCYearly() throws Exception;

	public Long getNextScheduleId() throws Exception;

	public List getEmpScheduleList(String lStrDDOCode, Long finYearId,
			String lStrPostId, String lStrDesignation,String lStrSevarthID,String lStrNADdo) throws Exception;/////$t 25-6-2021 Schedule id
	
	public List getSixPCYearlyIdPksForScheduleId(Long lLongScheduleID) throws Exception ;
	
	public List getSixPCDtlsOnApprovalForScheduleId(Long lLongScheduleID) throws Exception ;
	
	public Boolean checkPayComPadmabhan(Long dcpsEmpId);

	public List getAllTreasuries();
}
