package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;

/**
 * Class Description -
 * 
 * 
 * @author Jayraj Chudasama
 * @version 0.1
 * @since JDK 5.0 Oct 17, 2012
 */

public interface PostDetailsDAO extends GenericDao 
{
	List getPostDetails(String lStrDdoCode) throws Exception;
	
	Long getPkForPostDiffDtls(String lStrDdoCode) throws Exception;
	
	List getEnteredData(String lStrDdoCode) throws Exception;
	
	void updatePrintFlag(String lStrDdoCode) throws Exception;
	
	String chkForPrint(String lStrDdoCode) throws Exception;
}
