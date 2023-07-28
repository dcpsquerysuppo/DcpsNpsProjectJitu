package com.tcs.sgv.gpf.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * @author 397138
 * 
 */
public interface GPFApprovedRequestDAO extends GenericDao {

	List getGPFApprovedRequestList(String lStrDdoCode);

	String getNewOrderRefId(String lStrLocCode);

	String getGpfReqID(String transactionId);
	
	List LoadBillWorkList(String lStrLocationCode) throws Exception;
	
	List getAdvanceDetails(String lStrAdvanceTranId) throws Exception;

	public int getMaxOfBillNo() ;
}
