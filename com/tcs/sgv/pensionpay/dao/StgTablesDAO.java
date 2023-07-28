/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 31, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Mar 31, 2011
 */
public interface StgTablesDAO {

	public Map getStgTableData(Session lSess, String[] lStrArrTableName, Long delvId, String lStrLocationCode) throws Exception;

	public Map<String, BigDecimal> getAllHeadCodeMap(Session lHibSession) throws Exception;
	
	void updateDelvStatusToFailed(Long lLngDelvId, Session lHibSession) throws Exception;
	
	void updateDelvStatusToSuccessful(Long lLngDelvId, Session lHibSession) throws Exception;
	
	String getPensionerCodeFromPpoNo(String ppoNo, String lStrLocCd,Session lHibSession) throws Exception;
	
	void deletePnsnrFamilyDetails(String lStrPensionerCode,Session lHibSession) throws Exception;
	
	List<String> getLocCodeListFromAgCircle(String lStrAgCircleCode,Session lHibSession) throws Exception;
	
	List getPensionerNameForRevisedPpoList(List<String> lLstPpoNo,Session lHibSession) throws Exception;
	
	List<String> getRevisedPpoList(Session lHibSession) throws Exception;

}
