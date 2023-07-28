package com.tcs.sgv.gpf.dao;
/**
 * @author Niteesh Bhargava
 * @version 0.1
 * @since JDK 1.7 September 3, 2014
 * 
 * This DAO is created for adding new AG Details & PF series specified 
 * under Accountant General
 * Implementation can be found in AGGPFSeriesDAOImpl class
 * 
 */
		
import java.util.List;
import java.util.Map;

import com.tcs.sgv.core.dao.GenericDao;
public interface AGGPFSeriesDAO extends GenericDao {
	/**
	 * 
	 * @param langId defines language id for current logged in user
	 * @return List of All Accountant Generals Recognized by IFMS
	 * @throws Exception
	 */
	List getAGList() throws Exception;
	
	/**
	 * 
	 * @return  List of all AG for specified type
	 * @throws Exception
	 */
	List getObsoletePFSeriesFromAGLookupId(String agLookupId) throws Exception;
	List getActivePFSeriesFromAGLookupId(String agLookupId) throws Exception;
	List getPFSeriesFromAGLookupId(String agLookupId) throws Exception;
	List getObsoleteAGList() throws Exception;
	List getActiveAGList() throws Exception;
	List getPFSeries(long agId) throws Exception;
	public long getPFPrntLkpIdFromAGLkpId(String agLookupId);
	public int addAG(String agName);
	public int addPFSeries(Long agId,String pFSeriesName);
	public int upDateAGName(String lookupId,String newName);
	public int upDatePFName(String lookupId,String newName);
	public int obsolateAG(String lookupId);
	public int activateAG(String lookupId);

	
}
