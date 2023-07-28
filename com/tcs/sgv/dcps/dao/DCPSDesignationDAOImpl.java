/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 22, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;
import com.tcs.sgv.dcps.valueobject.MstDcpsDesignation;
import com.tcs.sgv.pensionproc.dao.PensionProcComparators;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Mar 22, 2011
 */
public class DCPSDesignationDAOImpl extends GenericDaoHibernateImpl implements DCPSDesignationDAO 
{
	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(DCPSDesignationDAOImpl.class);
	
	public DCPSDesignationDAOImpl(Class type,SessionFactory sessionFactory) 
	{
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.dao.DCPSDesignationDAO#getCadres(java.util.Map)
	 */
	public List<ComboValuesVO> getCadres(Map inputmap) throws Exception 
	{
		List<ComboValuesVO> lLstCadres = new ArrayList<ComboValuesVO>();
		List lLstResultList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		ComboValuesVO cmbVO;
		
		
		
		try
		{
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT cadre.cadreCode,cadre.cadreName FROM DcpsCadreMst cadre WHERE cadre.cadreCode IS NOT NULL");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList,new PensionProcComparators.ObjectArrayComparator(false, 1,0, 2, 0, true));
			cmbVO = new ComboValuesVO();
			if (lLstResultList != null && lLstResultList.size() > 0) 
			{
				Iterator it = lLstResultList.iterator();
				while (it.hasNext()) 
				{
					cmbVO = new ComboValuesVO();
					Object[] obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstCadres.add(cmbVO);
				}
			}
			//System.out.println("lLstCadres.size is " + lLstCadres.size());
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			gLogger.info("Error is  " + e);
		}
		return lLstCadres;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.dao.DCPSDesignationDAO#getDesigDsplyDtls(java.util.Map)
	 */
	public List getDesigDsplyDtls(Map inputmap) throws Exception 
	{
		List lLstDesigDtls = null;
		Query hqlQuery = null;
		StringBuilder lSBQuery = null;
		
		
		
		
		try
		{
			ghibSession = getSession();
			lSBQuery= new StringBuilder();
			lSBQuery.append(" SELECT desig.desigId,desig.desigCode,desig.desigDesc,cadre.cadreName,look.lookupName,look1.lookupName ");
			lSBQuery.append(" FROM MstDcpsDesignation desig,DcpsCadreMst cadre ,CmnLookupMst look,CmnLookupMst look1 ");
			lSBQuery.append(" WHERE cadre.cadreCode = desig.cadreTypeId ");
			lSBQuery.append(" AND look.lookupId = cadre.groupId AND look1.lookupId = desig.payComsnId ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstDesigDtls = hqlQuery.list();
			//System.out.println("Size of lLstDesigDtls is " + lLstDesigDtls.size());
			
			/*SELECT desig.desig_id,desig.desig_code,desig.desig_desc,cadre.cadre_name,look.lookup_name , look1.lookup_name
			FROM  MST_DCPS_DESIGNATION desig,mst_dcps_cadre cadre,cmn_lookup_mst look,cmn_lookup_mst look1
			WHERE cadre.cadre_code = desig.cadre_type_id  AND look.lookup_id = cadre.group_id AND look1.lookup_id = desig.pay_comsn_id*/
			
		}
		catch(Exception e)
		{
			gLogger.error("Error is :"+e,e);
			throw e;
		}		  
		return lLstDesigDtls;
	}
}
