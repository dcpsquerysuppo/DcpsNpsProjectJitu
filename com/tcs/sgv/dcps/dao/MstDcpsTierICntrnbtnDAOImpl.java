package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionproc.dao.PensionProcComparators;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;


// TODO: Auto-generated Javadoc
/**
 * The Class MstDcpsTierICntrnbtnDAOImpl.
 */
public class MstDcpsTierICntrnbtnDAOImpl 	
	extends GenericDaoHibernateImpl 
	implements MstDcpsTierICntrnbtnDAO {

	
	/** The Constant gLogger. */
	private final static Logger gLogger = Logger.getLogger(MstDcpsTierICntrnbtnDAOImpl.class);		
	
	/** The ghib session. */
	private Session ghibSession = null;
	
	/**
	 * Instantiates a new mst dcps tier i cntrnbtn dao impl.
	 * 
	 * @param type the type
	 * @param sessionFactory the session factory
	 */
	public MstDcpsTierICntrnbtnDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.dao.MstDcpsTierICntrnbtnDAO#getEmpData(java.lang.Long)
	 */
	public List getEmpData(Long lLongEmpId) throws Exception {		
	
		List lArrLstEmpData = new ArrayList();
		List lLstEmpData = null;
		Iterator itr;
		Object[] obj;		
		try{
		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		
		lSBQuery.append(" SELECT dcpsId, fname ");		
		lSBQuery.append(" FROM MstEmp where dcpsEmpId = :dcpsEmpId");		
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());		
		lQuery.setParameter("dcpsEmpId", lLongEmpId);
		//System.out.println(" lQuery "+lQuery);

		lLstEmpData = lQuery.list();
		//System.out.println("lLstEmpData" + lLstEmpData.size());
		if (lLstEmpData != null && lLstEmpData.size() > 0) {
			itr = lLstEmpData.iterator();
			while (itr.hasNext()) {		
				obj = (Object[]) itr.next();		
			
				lArrLstEmpData.add(obj[0].toString());
				lArrLstEmpData.add(obj[1].toString());				
			}
		}
		
		}
		catch (Exception e) {
		gLogger.error("Error is:" + e, e);
		e.printStackTrace();
		}
		return lArrLstEmpData;		
	
	}
	
}
