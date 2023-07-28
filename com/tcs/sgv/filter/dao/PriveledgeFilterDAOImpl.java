package com.tcs.sgv.filter.dao;

import java.util.List;




import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.ess.valueobject.OrgUserMst;

public class PriveledgeFilterDAOImpl extends GenericDaoHibernateImpl{
	
	public PriveledgeFilterDAOImpl(Class<OrgUserMst> type, SessionFactory sessionFactory){
		super(type);
		setSessionFactory(sessionFactory);
	}

	public List getRoleListForUser(String userName) {
		List roleList=null;
		Session session= getSession();
		StringBuffer sb=new StringBuffer();
		sb.append("select rolepost.role_id from ORG_USER_MST user ");
		sb.append("inner join ORG_USERPOST_RLT userpost on user.user_id=userpost.user_id ");
		sb.append("inner join ACL_POSTROLE_RLT rolepost on userpost.post_id=rolepost.post_id ");
		sb.append("where user.user_name='"+userName+"'");
		Query query= session.createSQLQuery(sb.toString());
		roleList= query.list();
		return roleList;
	}

	public boolean checkPriveledgeForElementId(String elementId,List roleId) {
		boolean isValid=false;
		Session session= getSession();
		StringBuffer sb=new StringBuffer();
		sb.append("select count(*) from acl_role_element_rlt ");
		//sb.append("where element_code="+elementId+" and role_id in("+roleId+") and activate_flag=1");
		sb.append("where element_code="+elementId+" and role_id in (:roleId) and activate_flag=1");
		Query query= session.createSQLQuery(sb.toString());
		logger.info("checkPriveledgeForElementId>>>>>>>>>"+query);
		query.setParameterList("roleId", roleId);
		if(Integer.parseInt(query.list().get(0).toString())>0){
			isValid=true;
		}
		return isValid;
	}
	
	public boolean checkPriveledgeForElementId(String elementId,String actionNm) {
		boolean isValid=false;
		Session session= getSession();
		StringBuffer sbb=new StringBuffer();
		sbb.append(" SELECT count(*) FROM ACL_ELEMENT_MST where ELEMENT_URL like '%="+actionNm+"%' ");
		Query queryy= session.createSQLQuery(sbb.toString());
		logger.info("checkPriveledgeForElementId>>>>>>>>>"+queryy);
		if(Integer.parseInt(queryy.list().get(0).toString())>0){
			StringBuffer sb=new StringBuffer();
			sb.append("SELECT count(*) FROM ACL_ELEMENT_MST where ELEMENT_ID="+elementId+" and ELEMENT_URL like '%="+actionNm+"%' ");
			Query query= session.createSQLQuery(sb.toString());
			logger.info("checkPriveledgeForElementId>>>>>>>>>"+query);
			if(Integer.parseInt(query.list().get(0).toString())>0){
				isValid=true;
			}
		}
		else {
			isValid=true;
		}
		return isValid;
	}
	
	
public List getRoleIdList(Long userId) throws Exception{
		
		logger.error("chkHOOrNot");
		List lLstResData = null;
		String lStrHoRoleFlag = "";
		Session session= getSession();
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT role.role_id FROM ORG_USER_MST ur ");
			lSBQuery.append(" INNER JOIN ORG_USERPOST_RLT post on ur.USER_ID=post.USER_ID ");
			lSBQuery.append("INNER JOIN ACL_POSTROLE_RLT role on role.POST_ID=post.POST_ID where post.ACTIVATE_FLAG=1 and role.ACTIVATE_FLAG=1 and ur.USER_ID='"+userId+"'  ");
			Query lQuery = session.createSQLQuery(lSBQuery.toString());
			lLstResData = lQuery.list();
			if(lLstResData.size()>0 && lLstResData.get(0)!= null)
			{
				lStrHoRoleFlag = lLstResData.get(0).toString();
			}
		}catch(Exception e){
			 logger.error(" Error is : " + e, e);
			  throw e; 
		}
		
		return lLstResData;
	}
}
