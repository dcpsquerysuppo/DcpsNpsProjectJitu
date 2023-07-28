package com.tcs.sgv.common.dao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import java.util.List;

import org.hibernate.Session;
/**
 * @see This class is used to get users in heirarchy of workflow when bill is moving
 * @author 157045
 * @version 1.0
 */
public class WorkFlowDAOImpl 
{
	/**
	 * This method is used to get same level of users for send to peer functionality
	 * @param hrchyRefId
	 * @param levelId
	 * @param postId
	 * @param session
	 * @return List
	 */
	public List getSameLevelUsers(String hrchyRefId, long levelId,long postId, SessionFactory session)
	{	  
		String str = "select post_id, level_id from wf_hierachy_post_mpg where hierachy_ref_id ="+hrchyRefId+" and level_id ="+levelId+" and post_id != '"+postId+"' and activate_flag = 1";
		Session ses  = session.getCurrentSession();
		Query query  = ses.createSQLQuery(str);
		return query.list();
	}
	/**
	 *  This is to get Heirarchy reference if from document Id	  
	 * @param locId
	 * @param docId
	 * @param session
	 * @return Long
	 */
	
	public Long getHierarchyRefIdByDocAndLoc(String locId, Long docId, SessionFactory session) 
	{
		Long hrchyRefId = null;
		String sql = "select hierachy_ref_id from wf_hierarchy_reference_mst where loc_Code=:locId and doc_Id = :docId";
		Query query  = session.getCurrentSession().createSQLQuery(sql);
		query.setString("locId",locId);
		query.setLong("docId",docId);
		hrchyRefId = Long.valueOf((query.list().iterator().next().toString()));
		
		return hrchyRefId;
	}
}
