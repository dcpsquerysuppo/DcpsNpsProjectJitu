/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	July 11 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Jul 11, 2011
 */
public interface GPFAdvanceProcessDAO extends GenericDao {
	List getSavedAdvance(String strGpfAccNo, String requestType, String lStrPostId);

	Boolean requestDataAlreadyExists(String strGpfAccNo);

	Long getMstAdvanceIdForTransactionId(String lStrTransactionId);

	List getAdvanceNotRecovered(String lStrGpfAccNo);

	List getSubTypesForPurpose(Long lLongPurpose);

	List getSubType(Long lLngNonRefundType);

	String getUserName(Long lLngUserId);

	Object getGPFAccountObjectForAccountNo(String gpfAccNo);

	List getAdvanceRequestForAdvanceId(String strGpfAccNo, Long lLngAdvanceId, String requestType, String lStrPostId);

	Long getOrgEmpIdForGpfAccNo(String lStrGpfAccNo);
	
	void updateOrderNo(String lStrGpfReqId, String lStrOrderNo) throws Exception;
	
	String getDesignationAndOfficeName(Long lLngPostId, String lStrLocCode)throws Exception;
}
