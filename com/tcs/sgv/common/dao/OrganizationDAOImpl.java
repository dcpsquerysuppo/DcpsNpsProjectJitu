package com.tcs.sgv.common.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class OrganizationDAOImpl extends GenericDaoHibernateImpl<Object,Long> implements OrganizationDAO {
	Log logger = LogFactory.getLog(OrganizationDAOImpl.class);

	public OrganizationDAOImpl(Class<Object> type,SessionFactory sessionFactory)
    {
		super(type);
        setSessionFactory(sessionFactory); 
    }

	/**
	 * This method returns user id by post id 
	 * @param postId 
	 * @return userId 
	 */
	public Long getUserByPostId(Long postId) throws Exception {

		Long userId = null;
		try 
		{
			Session hibSession = getSession();
			List resList = hibSession.createSQLQuery("SELECT o.user_id FROM " +
					"org_userpost_rlt o WHERE o.post_id=:postId and activate_flag = 1 ").setLong("postId", postId).list();
			Iterator it = resList.iterator();
			while (it.hasNext()) 
			{
				Object tuple = (Object) it.next();
				userId = Long.valueOf(tuple.toString());
			}
		} catch (Exception e) 
		{
			logger.error("Error occured in OrganizationDAOImpl.getUserByPostId #\n" + e);
			throw e;
		}
		return userId;
	}
	public Map<Long,Long> getUserByPostId(Long[] postId) throws Exception 
	{
		Map<Long,Long> postUserMap = new HashMap<Long,Long>();
		try 
		{
			Session hibSession = getSession();
			List resList = hibSession.createSQLQuery("SELECT o.user_id,o.post_id FROM org_userpost_rlt o WHERE o.post_id in(:postId)").setParameterList("postId", postId).list();
			for(Object row : resList) 
			{
				Object[] tuple = (Object[]) row;
				if(tuple[1] != null && tuple[0]!= null)
					postUserMap.put(Long.parseLong(tuple[1].toString()),Long.parseLong(tuple[0].toString()));
			}
		} 
		catch (Exception e) 
		{
			logger.error("Error occured in OrganizationDAOImpl.getUserByPostId #\n" + e);
			throw e;
		}
		return postUserMap;
	}
}
