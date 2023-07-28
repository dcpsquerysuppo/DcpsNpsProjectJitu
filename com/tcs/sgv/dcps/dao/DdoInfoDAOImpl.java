/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Mar 19, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.DDOInformationDetail;
import com.tcs.sgv.ess.valueobject.OrgEmpMst;
import com.tcs.sgv.pensionproc.dao.TrnPnsnProcInwardPensionDAOImpl;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Mar 19, 2011
 */
public class DdoInfoDAOImpl extends GenericDaoHibernateImpl implements DdoInfoDAO {

	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(TrnPnsnProcInwardPensionDAOImpl.class);

	public DdoInfoDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	public DDOInformationDetail getDdoInfo(String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM DDOInformationDetail");
		lSBQuery.append(" WHERE ddoCode = :ddoCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDdoCode);

		DDOInformationDetail lObjDdoInformation = (DDOInformationDetail) lQuery.uniqueResult();

		return lObjDdoInformation;
	}

	public Boolean checkDdoExistOrNot(String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;
		Boolean DdoExistsOrNot = false;
		List ddoList = null;

		lSBQuery.append("FROM DDOInformationDetail");
		lSBQuery.append(" WHERE ddoCode = :ddoCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDdoCode);

		ddoList = lQuery.list();
		if (ddoList.size() != 0) {
			DdoExistsOrNot = true;
		}

		return DdoExistsOrNot;
	}

	public OrgDdoMst getDdoInformation(String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM OrgDdoMst");
		lSBQuery.append(" WHERE ddoCode = :ddoCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDdoCode);

		OrgDdoMst ddoList = (OrgDdoMst) lQuery.uniqueResult();

		return ddoList;
	}
	
	public void updateDdoName(Long lLngDdoAstPostId, String lStrName)
	{
		StringBuilder lSBQuery = new StringBuilder();
		if(!"".equals(lStrName))
		{
			lStrName = lStrName.toUpperCase();
		}
		
		try{
			lSBQuery.append("update org_emp_mst set emp_fname = '"+lStrName+"' where user_id ");
			lSBQuery.append("in (select user_id from ORG_USERPOST_RLT where post_id ");
			lSBQuery.append("in (select ddo_post_id from RLT_DCPS_DDO_ASST where ASST_POST_ID = "+lLngDdoAstPostId+"))");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.executeUpdate();
		}
		catch(Exception e)
		{
			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
	}
	
	public void updateParentLocInCmnLocMstForDDO(Long lLongLocIdOfDDO,String lStrFieldHodDept)
	{
		StringBuilder lSBQuery = new StringBuilder();
		
		try{
			lSBQuery.append(" update cmn_location_mst set PARENT_LOC_ID = " + Long.valueOf(lStrFieldHodDept)  + " where loc_id = " + Long.valueOf(lLongLocIdOfDDO));
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.executeUpdate();
		}
		catch(Exception e)
		{
			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
	}
	
	public void updateDesigInOrgPost(String lStrDDOCode,String lStrDdoDesignation)
	{
		StringBuilder lSBQuery = new StringBuilder();
		StringBuilder lSBQueryPostDtl = new StringBuilder();
		
		try{
			lSBQuery.append(" update org_post_mst set DSGN_CODE = :lStrDdoDesignation where post_id = (select post_id from org_ddo_mst where ddo_code = :lStrDDOCode) ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("lStrDdoDesignation", lStrDdoDesignation.trim());
			lQuery.setParameter("lStrDDOCode", lStrDDOCode.trim());
			lQuery.executeUpdate();
			
			lSBQueryPostDtl.append(" update ORG_POST_DETAILS_RLT set DSGN_ID = :lStrDdoDesignation where post_id = (select post_id from org_ddo_mst where ddo_code = :lStrDDOCode )");
			Query lQueryPostDtl = ghibSession.createSQLQuery(lSBQueryPostDtl.toString());
			lQueryPostDtl.setParameter("lStrDdoDesignation", lStrDdoDesignation.trim());
			lQueryPostDtl.setParameter("lStrDDOCode", lStrDDOCode.trim());
			lQueryPostDtl.executeUpdate();
			
		}
		catch(Exception e)
		{
			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
	}
	
	public void updateDesigNameInOrgPostDtlRlt(String lStrDDOCode,String lStrDdoDesignation)
	{
		StringBuilder lSBQuery = new StringBuilder();
		StringBuilder lSBQueryPostDtl = new StringBuilder();
		
		try{
			lSBQuery.append(" update ORG_POST_DETAILS_RLT set POST_NAME = (select DSGN_NAME from ORG_DESIGNATION_MST where DSGN_ID = :lStrDdoDesignation ) where post_id = (select post_id from org_ddo_mst where ddo_code = :lStrDDOCode)");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("lStrDdoDesignation", lStrDdoDesignation.trim());
			lQuery.setParameter("lStrDDOCode", lStrDDOCode.trim());
			lQuery.executeUpdate();
			
		}
		catch(Exception e)
		{
			gLogger.error(" Error is : " + e, e);
			//e.printStackTrace();
		}
	}

}
