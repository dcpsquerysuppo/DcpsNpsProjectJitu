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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.DcpsCadreMst;
import com.tcs.sgv.dcps.valueobject.MstDcpsOrganization;
import com.tcs.sgv.pensionproc.dao.PensionProcComparators;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Apr 8, 2011
 */
public class SrkaMasterDAOImpl extends GenericDaoHibernateImpl implements SrkaMasterDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	/**
	 * @param type
	 */
	public SrkaMasterDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.SrkaMasterDAO#getCadreList()
	 */
	public List<DcpsCadreMst> getCadreList(Long CurrFieldDeptId) throws Exception {

		List<DcpsCadreMst> lLstCadreDtls = null;
		StringBuilder lSBQuery = null;

		Query hqlQuery = null;
		try {

			lSBQuery = new StringBuilder();

			lSBQuery.append(" Select CM.cadreCode,CM.cadreName,LM.locName,CLM.lookupName,CM.ministerialFlag, CM.superAntunAge FROM DcpsCadreMst CM, CmnLocationMst LM,CmnLookupMst CLM ");
			lSBQuery.append(" where CM.fieldDeptId = LM.locId AND CM.groupId=CLM.lookupId and CM.fieldDeptId  = :CurrFieldDeptId");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("CurrFieldDeptId", CurrFieldDeptId);

			lLstCadreDtls = hqlQuery.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error  while executing getCadreList of  DCPSCadreMasterDAOImpl is " + e);

		}
		return lLstCadreDtls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.SrkaMasterDAO#getCadres(java.util.Map)
	 */
	public List<ComboValuesVO> getCadres() throws Exception {

		List<ComboValuesVO> lLstCadres = new ArrayList<ComboValuesVO>();
		List lLstResultList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		ComboValuesVO cmbVO;

		try {

			lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT cadre.cadreCode,cadre.cadreName FROM DcpsCadreMst cadre WHERE cadre.cadreCode IS NOT NULL");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList, new PensionProcComparators.ObjectArrayComparator(false, 1, 0, 2, 0, true));
			cmbVO = new ComboValuesVO();
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator it = lLstResultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					Object[] obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstCadres.add(cmbVO);
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error is  " + e);
		}
		return lLstCadres;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.SrkaMasterDAO#getDesigDsplyDtls(java.util.Map)
	 */
	public List getDesigDsplyDtls(Long CurrFieldDeptId) throws Exception {

		List lLstDesigDtls = null;
		Query hqlQuery = null;
		StringBuilder lSBQuery = null;

		try {

			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT desig.desigId,desig.desigCode,desig.desigDesc,look1.lookupDesc,look.lookupDesc, LM.locName ");
			lSBQuery.append(" FROM MstDcpsDesignation desig,CmnLookupMst look,CmnLookupMst look1, CmnLocationMst LM ");
			lSBQuery.append(" WHERE look.lookupId = desig.payComsnId AND look1.lookupId = desig.cadreTypeId AND LM.locId = desig.fieldDeptId ");
			lSBQuery.append(" AND desig.fieldDeptId = :CurrFieldDeptId order by desig.desigCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("CurrFieldDeptId", CurrFieldDeptId);
			lLstDesigDtls = hqlQuery.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDesigDtls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.SrkaMasterDAO#getOrgDsplyDtls(java.util.Map)
	 */
	public List<MstDcpsOrganization> getOrgDsplyDtls() throws Exception {

		List lLstOrgDtls = null;
		Query hqlQuery = null;
		StringBuilder lSBQuery = null;

		try {

			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT org.orgId,org.orgType,org.orgDesc,org.empHeadAcc,org.empSchemeCode,org.emplrHeadAcc,"
					+ "org.emplrSchemeCode,org.deptEmpHeadAcc,org.deptEmpSchemeCode,org.deptEmplrHeadAcc,org.deptEmplrSchemeCode ");
			lSBQuery.append(" FROM MstDcpsOrganization org order by org.orgType");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstOrgDtls = hqlQuery.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstOrgDtls;
	}

	public List<ComboValuesVO> getDdoCodes(String lStrTreasuryCode) {

		ArrayList<ComboValuesVO> lLstDdoCodes = new ArrayList<ComboValuesVO>();

		ComboValuesVO cmbVO;
		List lLstResultList;
		Iterator itr;
		Object[] obj;
		StringBuilder lSBQuery = new StringBuilder();
		try {

			lSBQuery.append("SELECT DM.ddoId, DM.ddoCode FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM WHERE RO.locationCode = :treasuryCode AND RO.ddoCode = DM.ddoCode AND LM.locId = RO.locationCode ");
			lSBQuery.append(" AND (DM.deptLocCode IS NULL OR DM.hodLocCode IS NULL) ");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("treasuryCode", lStrTreasuryCode);
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList, new PensionProcComparators.ObjectArrayComparator(false, 1, 0, 2, 0, true));
			if (lLstResultList != null && lLstResultList.size() > 0) {
				
				cmbVO = new ComboValuesVO();
				cmbVO.setId("-1");
				cmbVO.setDesc("--Select--");
				lLstDdoCodes.add(cmbVO);
				
				itr = lLstResultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					lLstDdoCodes.add(cmbVO);
				}
			}
			else
			{
				cmbVO = new ComboValuesVO();
				cmbVO.setId("-1");
				cmbVO.setDesc("--Select--");
				lLstDdoCodes.add(cmbVO);
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			//e.printStackTrace();
		}
		return lLstDdoCodes;
	}

	public List<ComboValuesVO> getFieldDept(Long lLngDepartmentId) {

		ArrayList<ComboValuesVO> lArrLstDepartnent = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		ComboValuesVO cmbVO;
		List lLstResultList;
		Iterator itr;
		Object[] obj;

		try {

			lStrQuery.append(" SELECT clm.locId,clm.locName FROM CmnLocationMst clm ");
			lStrQuery.append(" WHERE clm.parentLocId=:departmentId  ");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());

			hqlQuery.setLong("departmentId", lLngDepartmentId);

			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList, new PensionProcComparators.ObjectArrayComparator(false, 1, 0, 2, 0, true));
			if (lLstResultList != null && lLstResultList.size() > 0) {
				
				cmbVO = new ComboValuesVO();
				cmbVO.setId("-1");
				cmbVO.setDesc("--Select--");
				lArrLstDepartnent.add(cmbVO);
				
				itr = lLstResultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					lArrLstDepartnent.add(cmbVO);
				}
			}
			else
			{
				cmbVO = new ComboValuesVO();
				cmbVO.setId("-1");
				cmbVO.setDesc("--Select--");
				lArrLstDepartnent.add(cmbVO);
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			//e.printStackTrace();
		}
		return lArrLstDepartnent;
	}

	public Long getMaxCadreInFieldDept(Long lLongFieldDeptId) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long count = 0L;

		lSBQuery.append(" SELECT MAX(cadreCode) FROM DcpsCadreMst WHERE fieldDeptId = :fieldDeptId ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("fieldDeptId", lLongFieldDeptId);

		tempList = lQuery.list();

		if (tempList.get(0) != null) {
			count = Long.valueOf(tempList.get(0).toString().trim());
		} else {
			count = 0l;
		}

		return count;

	}

	public Long getMaxDesigInFieldDept(Long lLongFieldDeptId) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long count = 0L;

		lSBQuery.append(" SELECT MAX(desigCode) FROM MstDcpsDesignation WHERE fieldDeptId = :fieldDeptId ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("fieldDeptId", lLongFieldDeptId);

		tempList = lQuery.list();

		if (tempList.get(0) != null) {
			count = Long.valueOf(tempList.get(0).toString().trim());
		} else {
			count = 0l;
		}

		return count;

	}
	
	public Boolean checkDesigExistsInOrgDesigMstOrNot(String lStrDesigName) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList<Long>();
		Boolean flag = false;

		lSBQuery.append(" select dsgn_id from org_designation_mst where trim(upper(Dsgn_name)) = :dsgnName");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dsgnName", lStrDesigName.trim().toUpperCase());

		tempList = lQuery.list();
		
		if (tempList.size() != 0 && tempList != null) {
			flag = true;
		}
		return flag;

	}
	
	public Boolean checkDesigExistInCadreAndFieldDept(String lStrDesigName,Long lLongFieldDept) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList<Long>();
		Boolean flag = false;

		lSBQuery.append(" select desig_id from MST_DCPS_DESIGNATION where trim(upper(DESIG_DESC)) = :dsgnName and FIELD_DEPT_ID = :fieldDept");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dsgnName", lStrDesigName.trim().toUpperCase());
		lQuery.setParameter("fieldDept", lLongFieldDept);

		tempList = lQuery.list();
		
		if (tempList.size() != 0 && tempList != null) {
			flag = true;
		}
		return flag;

	}

	public Long getDesigIdForDesigName(String lStrDesigName)  throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList<Long>();
		Long lLongOrgDesigId = null;
		String lStrOrgDesigId = null;
		
		lSBQuery.append(" select dsgn_id from org_designation_mst where trim(upper(Dsgn_name)) = :dsgnName");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("dsgnName", lStrDesigName.trim().toUpperCase());

		tempList = lQuery.list();
		if(tempList != null)
		{
			if(tempList.get(0) != null)
			{
				lStrOrgDesigId= tempList.get(0).toString().trim();
				if(lStrOrgDesigId != null && !"".equals(lStrOrgDesigId))
				lLongOrgDesigId = Long.valueOf(lStrOrgDesigId);
			}
		}
		return lLongOrgDesigId;
	}

}
