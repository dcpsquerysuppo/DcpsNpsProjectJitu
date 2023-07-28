package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description - 
 *
 *
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0
 * Jul 23, 2012
 */

public interface UpdateSixPCArrearMasterAmountDAO extends GenericDao
{
	List getDataFromEmpCode(String lStrEmpCode)throws Exception;
	
	void updateMstDcpsSixPC(Long lLngDcpsEmpId, Long lLngNewAmount, Long lLngPostId, Long lLngUserId)throws Exception;
	
	void updateRltDcpsSixPCYearly(Long lLngDcpsEmpId, Long lLngNewAmount, String lStrPayComm, Long lLngPostId, Long lLngUserId)throws Exception;
}
