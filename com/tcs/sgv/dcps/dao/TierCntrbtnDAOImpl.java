/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 8, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;

/**
 * Class Description - 
 *
 *
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0
 * Apr 8, 2011
 */
public class TierCntrbtnDAOImpl extends GenericDaoHibernateImpl implements TierCntrbtnDAO 
{
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;
	/**
	 * @param type
	 */
	public TierCntrbtnDAOImpl(Class type,SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
		
	}
	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.dao.TierCntrbtnDAO#getAllSavedRequestsForTier(java.lang.Integer, java.lang.String)
	 */
	public List getAllSavedRequestsForTier(Integer lIntCriteria,
			String lStrPostId) throws Exception {

		List lListSavedRequests = null;
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		try
		{
		// For drafts
		if (lIntCriteria == 1) 
		{
			
			lSBQuery.append(" SELECT tierI.employeeId,tierI.createdDate,desig.dsgnName,look.lookupName,tierI.tierICntrnbtnFromDate,tierI.tierICntrnbtnToDate");
			lSBQuery.append(" FROM MstDcpsTierICntrnbtn tierI,OrgDesignationMst desig,CmnLookupMst look");
			lSBQuery.append(" WHERE tierI.arrearstypeLookupid= look.lookupId AND tierI.designationId = desig.dsgnId AND (tierI.statusFlag = 'D' OR tierI.statusFlag = 'R')");
			
			
			
			
			/*
			 * SELECT tier.employee_id,tier.created_date,desig.dsgn_name,look.lookup_name,tier.tierI_cntrnbtn_from_date,tierI_cntrnbtn_to_date 
			 * FROM mst_dcps_tieri_cntrnbtn tier ,org_designation_mst  desig ,cmn_lookup_mst look
			 * WHERE tier.arrearstype_lookupid = look.lookup_id AND tier.designation_id = desig.dsgn_id
			 * 
			 * */
		}
		if(lIntCriteria == 2)
		{
			lSBQuery.append(" SELECT tierI.employeeId,tierI.updatedDate,desig.dsgnName,look.lookupName,tierI.tierICntrnbtnFromDate,tierI.tierICntrnbtnToDate,tierI.statusFlag");
			lSBQuery.append(" FROM MstDcpsTierICntrnbtn tierI,OrgDesignationMst desig,CmnLookupMst look");
			lSBQuery.append(" WHERE tierI.arrearstypeLookupid= look.lookupId AND tierI.designationId = desig.dsgnId AND (tierI.statusFlag = 'F' OR tierI.statusFlag = 'A') ");
		}

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lListSavedRequests = lQuery.list();
		
		}
		catch(Exception e)
		{
			gLogger.error("Exception occured from getAllSavedRequestsForTier ::: " + e);
		}
		return lListSavedRequests;

	}
	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.dao.TierCntrbtnDAO#getEmpDtlsFromDDODesig(java.lang.String, java.util.Map)
	 */
	public List getEmpDtlsFromDDODesig(String lStrDesigId, Map inputMap)
			throws Exception {
		List<MstEmp> lLstEmpDtls = null;
		StringBuilder lSBQuery=null;
		Query hqlQuery = null;
		try
		{
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT emp.dcpsEmpId,emp.name FROM MstEmp emp WHERE emp.designation = :designation ");		    
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("designation", lStrDesigId);
			lLstEmpDtls = hqlQuery.list();
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			gLogger.info("Error  while executing getCadreList of  DCPSCadreMasterDAOImpl is " + e);
			
		}
		return lLstEmpDtls;
		 //SELECT dcps_emp_id,emp_name FROM mst_dcps_emp WHERE  designation = 101003
	}
	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.dao.TierCntrbtnDAO#getEmpNameFromId(java.lang.Long)
	 */
	public String getEmpNameFromId(Long lLngdcpsEmpId) throws Exception {
		
		String lStrEmpName = null;
		StringBuilder lSBQuery=null;
		Query hqlQuery = null;
		try
		{
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT emp.name FROM MstEmp emp WHERE emp.dcpsEmpId = :dcpsEmpId");		    
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("dcpsEmpId", lLngdcpsEmpId);
			lStrEmpName = hqlQuery.list().get(0).toString();
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			gLogger.info("Error  while executing getCadreList of  DCPSCadreMasterDAOImpl is " + e);
			
		}
		return lStrEmpName;
	}
	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.dao.TierCntrbtnDAO#getMstDcpsTierICntrnbtnPk(java.lang.Long)
	 */
	public Long getMstDcpsTierICntrnbtnPk(Long lLngEmpId) throws Exception {
		Long lLngTierICntrnbtnId = null;
		try
		{
			Session hibSession=getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT tier.tierICntrnbtnId FROM MstDcpsTierICntrnbtn tier ");
			lSBQuery.append(" WHERE tier.employeeId = :lLngDcpsEmpId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLngDcpsEmpId", lLngEmpId);
			lLngTierICntrnbtnId = (Long)lQuery.list().get(0);
			
			

	}
		catch(Exception e)
		{
			e.printStackTrace();
			gLogger.error("Error is :"+e,e);
			
		}
		return lLngTierICntrnbtnId;
		
	

	}
	/* (non-Javadoc)
	 * @see com.tcs.sgv.dcps.dao.TierCntrbtnDAO#getTierDtlFromDesig(java.lang.String)
	 */
	public List getTierDtlFromDesig(String lStrDesignation) throws Exception {
	
		List lLstTierDtlsFromDesig = null;
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		
		try
		{
			
			lSBQuery.append(" SELECT tierI.employeeId,tierI.tierICntrnbtnFromDate,tierI.employeePensionId,tierI.employeeName,tierI.totalAmount,tierI.tierICntrnbtnFromDate,tierI.tierICntrnbtnToDate");
			lSBQuery.append(" FROM MstDcpsTierICntrnbtn tierI,OrgDesignationMst desig");
			lSBQuery.append(" WHERE tierI.designationId = desig.dsgnId AND desig.dsgnName = :dsgnName");
			
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("dsgnName", lStrDesignation);
			lLstTierDtlsFromDesig = lQuery.list();
			
			
			
			/*
			 * SELECT employee_id,tierI_cntrnbtn_from_date,employee_pension_id,employee_name ,total_amount,tierI_cntrnbtn_from_date,tierI_cntrnbtn_to_date
          FROM mst_dcps_tieri_cntrnbtn tierI,org_designation_mst desig WHERE   desig.dsgn_name = 'ASI' AND tierI.designation_id = desig.dsgn_id 
			 * 
			 * */
		}
		catch(Exception e)
		{
			e.printStackTrace();
			gLogger.error("Exception occurred From getTierDtlFromDesig :: " + e);
		}
		

		
		return lLstTierDtlsFromDesig;

		
	}

}
