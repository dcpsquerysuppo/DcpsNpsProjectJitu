/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 24, 2011		Shivani Rana								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.pensionpay.valueobject.StgDeliveryDtls;


/**
 * Class Description -
 * 
 * 
 * @author Shivani Rana
 * @version 0.1
 * @since JDK 5.0 Mar 24, 2011
 */
public interface StgDeliveryDtlsDAO extends GenericDao {

	public List<StgDeliveryDtls> getStgDeliveryDtls(Map displayTag,String lStrLocationCode) throws Exception;

	public int getStgDeliveryDtlsCount(String lStrLocationCode) throws Exception;
}
