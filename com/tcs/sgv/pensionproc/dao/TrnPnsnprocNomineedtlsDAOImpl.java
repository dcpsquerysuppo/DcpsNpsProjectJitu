/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 7, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.tcs.sgv.core.dao.GenericDao;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcInwardPension;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnProcRecovery;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocFamilydtls;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocNomineedtls;

/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Mar 7, 2011
 */
public class TrnPnsnprocNomineedtlsDAOImpl extends GenericDaoHibernateImpl implements TrnPnsnprocNomineedtlsDAO{
	
	private Session ghibSession = null;
	private static final Logger gLogger = Logger
			.getLogger(TrnPnsnProcInwardPensionDAOImpl.class);
	

	public TrnPnsnprocNomineedtlsDAOImpl(Class type, SessionFactory sessionFactory) 
	{
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}
	
	public List<TrnPnsnprocNomineedtls> getListOfPnsnrNomineeDtls(Long lLngInwardPensionId) throws Exception 
	{
		Criteria objCrt=null;
		
		List<TrnPnsnprocNomineedtls> lLstPnsnrNomineedtls=new ArrayList<TrnPnsnprocNomineedtls>();

		try
		{
			Query lQuery = null;
			StringBuilder lStrQuery = new StringBuilder();
			
			lStrQuery.append("SELECT mpn.pnsnrnominee_id,mpn.name,mpn.percentage,mpn.bank_Code,mpn.branch_Code,mpn.account_No,rlt.branch_Name,mpn.relation"
					+ " FROM TRN_PNSNPROC_NOMINEEDTLS mpn LEFT OUTER JOIN Rlt_Bank_Branch rlt ON mpn.branch_Code=rlt.branch_Code" + " WHERE  mpn.INWARD_PENSION_ID =:inwardPensionId");
			lQuery = ghibSession.createSQLQuery(lStrQuery.toString());
			lQuery.setParameter("inwardPensionId", lLngInwardPensionId);
			
			lLstPnsnrNomineedtls=lQuery.list();
		}
		catch(Exception e)
		{
			gLogger.error("TrnPnsnprocNomineedtlsDAOImpl : getListOfPnsnrNomineeDtls() : Error is :"+e,e);
			
		}
		return lLstPnsnrNomineedtls;
	}

	
	public List<TrnPnsnprocNomineedtls> getPnsnrNomineeDtls(Long lLngInwardPensionId) throws Exception {
		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List lLstResult = new ArrayList();
		List<TrnPnsnprocNomineedtls> lLstPnsnrNomineedtls=new ArrayList<TrnPnsnprocNomineedtls>();

		try {
		
			objCrt = ghibSession.createCriteria(TrnPnsnprocNomineedtls.class);
			objCrt.add(Restrictions.like("inwardPensionId",	lLngInwardPensionId));
			lLstPnsnrNomineedtls = objCrt.list();
			
		} catch (Exception e) {
			gLogger.error("Error is :"	+ e, e);

		}
		return lLstPnsnrNomineedtls;
	}
}
