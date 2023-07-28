/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 5, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcPnsnrDtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocFamilydtls;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Mar 5, 2011
 */
public class TrnPnsnprocFamilydtlsDAOImpl extends GenericDaoHibernateImpl implements TrnPnsnprocFamilydtlsDAO{

	private Session ghibSession = null;
	private static final Logger gLogger = Logger
			.getLogger(TrnPnsnProcInwardPensionDAOImpl.class);
	

	public TrnPnsnprocFamilydtlsDAOImpl(Class type, SessionFactory sessionFactory) 
	{
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}
	
	
	
	public List<TrnPnsnprocFamilydtls> getListOfPnsnrFamilyDtls(Long lLngInwardPensionId) throws Exception {
		Criteria objCrt=null;
	
		List<TrnPnsnprocFamilydtls> lLstPnsnrFmlydtls=new ArrayList<TrnPnsnprocFamilydtls>();

		try
		{
			
			Session hibSession=getSession();
			objCrt= hibSession.createCriteria(TrnPnsnprocFamilydtls.class);
			objCrt.add(Restrictions.like("inwardPensionId", lLngInwardPensionId));
			lLstPnsnrFmlydtls=objCrt.list();
		}
		catch(Exception e)
		{
			gLogger.error("TrnPnsnprocFamilydtlsDAOImpl : getListOfPnsnrFamilyDtls() : Error is :"+e,e);
			
		}
		return lLstPnsnrFmlydtls;
	}
}
