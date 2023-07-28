
package com.tcs.sgv.dcps.dao;

import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;
/**
 * 
 * Class Description - 
 *
 *
 * @author Sneha
 * @version 0.1
 * @since JDK 5.0
 * Mar 3, 2011
 */
public interface MstDcpsTierICntrnbtnDAO extends GenericDao{
	/*
	 * 
	 */
	public List getEmpData(Long lLongEmpId) throws Exception;
}
