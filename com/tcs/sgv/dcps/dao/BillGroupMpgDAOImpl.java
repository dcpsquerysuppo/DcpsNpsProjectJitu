/**
 * 
 */
package com.tcs.sgv.dcps.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.RltDcpsBillGroupMpg;

/**
 * @author 379674
 *
 */
public class BillGroupMpgDAOImpl extends GenericDaoHibernateImpl<RltDcpsBillGroupMpg, Long>  implements BillGroupMpgDAO  {

	/**
	 * 
	 */	
	public BillGroupMpgDAOImpl(Class<RltDcpsBillGroupMpg> type, SessionFactory sessionFactory) {
		// TODO Auto-generated constructor stub
		  super(type);
	      setSessionFactory(sessionFactory);
	}
	public RltDcpsBillGroupMpg getRltDcpsBillGroupMpg(long mstDcpsBillNo)
	{
		RltDcpsBillGroupMpg billGroupMpg = new RltDcpsBillGroupMpg();
		Session session = getSession();
		String st ="select rlt from RltDcpsBillGroupMpg rlt, MstDcpsBillGroup mst  where rlt.mstDcpsBillGroup.dcpsDdoBillGroupId=mst.dcpsDdoBillGroupId and mst.dcpsDdoBillGroupId="+mstDcpsBillNo;
		Query qry =session.createQuery(st);
		return (RltDcpsBillGroupMpg)qry.uniqueResult();
	}

}
