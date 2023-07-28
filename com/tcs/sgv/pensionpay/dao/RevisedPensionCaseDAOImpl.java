/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 29, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls;


/**
 * Class Description - 
 *
 *
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0
 * Jun 29, 2011
 */
public class RevisedPensionCaseDAOImpl  extends GenericDaoHibernateImpl implements RevisedPensionCaseDAO{
	
	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(RevisedPensionCaseDAOImpl.class);
	
	public RevisedPensionCaseDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.RevisedPensionCaseDAO#getTrnPensionerRivisionDtlsVO(java.lang.String, java.lang.String)
	 */
	public List<TrnPensionerRivisionDtls> getTrnPensionerRevisionDtlsVO(String lStrPensionerCode, String lStrLocationCode) throws Exception {

		Criteria objCrt = null;
		List<TrnPensionerRivisionDtls> lLstTrnPensionerRevisionDtls = new ArrayList<TrnPensionerRivisionDtls>();
		
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionerRivisionDtls.class);
			objCrt.add(Restrictions.like("pensionerCode", lStrPensionerCode));
			objCrt.add(Restrictions.like("locationCode", lStrLocationCode));
			lLstTrnPensionerRevisionDtls = objCrt.list();
			
		} catch (Exception e) {
			gLogger.error("RevisedPensionCaseDAOImpl : getTrnPensionerRivisionDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lLstTrnPensionerRevisionDtls;
	}

}
