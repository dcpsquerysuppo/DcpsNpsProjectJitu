/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 2, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionproc.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.dcps.valueobject.DdoOffice;

/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Feb 2, 2011
 */
public class CommonPensionDAOImpl implements CommonPensionDAO {

	private Session ghibSession = null;
	private final static Logger logger = Logger.getLogger(CommonPensionDAOImpl.class);

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionproc/PensionCaseConstants");

	public CommonPensionDAOImpl(SessionFactory sessionFactory) {
		// super(type);
		// setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.CommonPensionDAO#getAllDepartment(java.lang
	 * .Long)
	 */

	public List<ComboValuesVO> getAllDepartment(Long lLngDepartmentId, Long langId) throws Exception {
		List<ComboValuesVO> lLstDepartnent = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		ComboValuesVO cmbVO;
		List lLstResultList = null;
		Iterator itr;
		Object[] obj;

		try {

			lStrQuery.append(" SELECT clm.locId,clm.locName FROM CmnLocationMst clm, OrgDepartmentMst odm ");
			lStrQuery.append(" WHERE odm.departmentId=:departmentId  ");
			lStrQuery.append(" AND clm.departmentId=odm.departmentId ");
			lStrQuery.append(" and clm.cmnLanguageMst.langId =:langId ");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("langId", langId);
			hqlQuery.setLong("departmentId", lLngDepartmentId);
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList, new PensionProcComparators.ObjectArrayComparator(false, 1, 0, 2, 0, true));
			if (lLstResultList != null && lLstResultList.size() > 0) {
				itr = lLstResultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					lLstDepartnent.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDepartnent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.CommonPensionDAO#getAllDesignation(java.lang
	 * .Long)
	 */

	public List<ComboValuesVO> getAllDesignation(Long langId) throws Exception {
		StringBuilder lStrQuery = new StringBuilder();
		List<ComboValuesVO> lLstDesignation = new ArrayList<ComboValuesVO>();
		List lLstResultList = null;
		ComboValuesVO cmbVO;
		Iterator itr;
		Object[] obj;
		try {

			lStrQuery.append(" Select dsgn.dsgnId,dsgn.dsgnName ");
			lStrQuery.append(" FROM OrgDesignationMst dsgn ");
			lStrQuery.append(" WHERE dsgn.cmnLanguageMst.langId =:langId order by dsgn.dsgnName");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("langId", langId);
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList, new PensionProcComparators.ObjectArrayComparator(false, 1, 0, 2, 0, true));
			if (lLstResultList != null && lLstResultList.size() > 0) {
				itr = lLstResultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					lLstDesignation.add(cmbVO);
				}
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			e.printStackTrace();
		}
		return lLstDesignation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.CommonPensionDAO#getAllState(java.lang.Long)
	 */

	public List<ComboValuesVO> getAllState(Long langId) throws Exception {
		List<ComboValuesVO> lLstState = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList;
		try {

			lStrQuery.append(" Select stateId,stateName ");
			lStrQuery.append(" FROM CmnStateMst ");
			lStrQuery.append(" WHERE cmnLanguageMst.langId =:langId ");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("langId", langId);
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList, new PensionProcComparators.ObjectArrayComparator(false, 1, 0, 2, 0, true));
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator itr = lLstResultList.iterator();
				ComboValuesVO cmbVO = null;
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					Object[] obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstState.add(cmbVO);
				}
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.CommonPensionDAO#getAllTreasury(java.lang
	 * .Long)
	 */

	public List<ComboValuesVO> getAllTreasury(List<Long> lLstDepartmentId, Long langId) throws Exception {

		List<ComboValuesVO> lLstTreasury = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList;
		try {
			lStrQuery.append(" Select locId,locName ");
			lStrQuery.append(" FROM CmnLocationMst ");
			lStrQuery.append(" WHERE departmentId IN (:lLstDepartmentId)");
			lStrQuery.append(" AND cmnLanguageMst.langId =:langId order by locName");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setParameterList("lLstDepartmentId", lLstDepartmentId);
			hqlQuery.setLong("langId", langId);
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResultList = hqlQuery.list();
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator itr = lLstResultList.iterator();

				ComboValuesVO cmbVO = null;
				Object[] obj = null;
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					lLstTreasury.add(cmbVO);
				}
			}
			return lLstTreasury;
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.CommonPensionDAO#getDistrictsOfState(java
	 * .math.BigDecimal, java.lang.Long)
	 */

	public List<ComboValuesVO> getDistrictsOfState(Long stateId, Long langId) throws Exception {
		List<ComboValuesVO> lLstDistrict = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			lStrQuery.append(" Select districtId,districtName ");
			lStrQuery.append(" FROM CmnDistrictMst ");
			lStrQuery.append(" WHERE cmnLanguageMst.langId = :langId");
			lStrQuery.append(" and cmnStateMst.stateId = :stateId  ");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("langId", langId);
			hqlQuery.setParameter("stateId", Long.valueOf(stateId.toString()));
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList, new PensionProcComparators.ObjectArrayComparator(false, 1, 0, 2, 0, true));
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("--Select--");
			lLstDistrict.add(cmbVO);
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator it = lLstResultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstDistrict.add(cmbVO);
				}
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDistrict;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionproc.dao.CommonPensionDAO#getHodFromDepartmetLoc(java
	 * .lang.Long, java.lang.Long)
	 */

	public List<ComboValuesVO> getHooFromDepartmentLoc(Long lLngParentLocId, Long langId) throws Exception {
		List<ComboValuesVO> lLstHOO = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO = null;
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList = null;

		Object[] obj;
		try {
			
			lStrQuery.append(" SELECT MDO.dcpsDdoOfficeIdPk,MDO.dcpsDdoOfficeName");
			lStrQuery.append(" FROM OrgDdoMst ODM,DdoOffice MDO");
			lStrQuery.append(" WHERE ODM.hodLocCode = :parentLocId");
			lStrQuery.append(" AND MDO.dcpsDdoCode = ODM.ddoCode");
			lStrQuery.append(" AND ODM.langId =:langId ");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("parentLocId", lLngParentLocId);
			hqlQuery.setLong("langId", langId);
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList, new PensionProcComparators.ObjectArrayComparator(false, 1, 0, 2, 0, true));
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("--Select--");
			lLstHOO.add(cmbVO);
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator it = lLstResultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstHOO.add(cmbVO);
				}
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstHOO;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionproc.dao.CommonPensionDAO#getBankNames(java.lang.Long, java.lang.String)
	 */

	public List<ComboValuesVO> getBankNames(Long lLngLangId, String lStrLocCode) {

		List<ComboValuesVO> lLstBankNames = null;
		List lLstResult = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT MB.bankCode,MB.bankName FROM MstBank MB where MB.langId = :langId ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", lLngLangId);
			//	lHibQry.setParameter("locationCode", lStrLocCode);

			lLstResult = lHibQry.list();
			ComboValuesVO lObjComboValuesVO = null;

			if (lLstResult != null && lLstResult.size() != 0) {
				lLstBankNames = new ArrayList<ComboValuesVO>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstBankNames.add(lObjComboValuesVO);
				}
			} else {
				lLstBankNames = new ArrayList<ComboValuesVO>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstBankNames.add(lObjComboValuesVO);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}

		return lLstBankNames;

	}

	public List<ComboValuesVO> getBranchesOfBank(String bankCode, Long langId, String locCode) throws Exception {

		ArrayList<ComboValuesVO> arrBranchVO = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List<ComboValuesVO> resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			strQuery.append(" SELECT branchCode,branchName  FROM RltBankBranch  WHERE bankCode =:bankCode AND locationCode =:locationCode " + " order by branchName");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setString("bankCode", bankCode);
			hqlQuery.setString("locationCode", locCode);
			resultList = hqlQuery.list();
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("--Select--");
			arrBranchVO.add(cmbVO);
			if (!resultList.isEmpty()) {
				Iterator itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrBranchVO.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrBranchVO;
	}
	public String getIfscCodeFromBranchCode(Long lLngBranchCode, String lStrLocationCode) throws Exception {

		String lStrIfscCode = "";
		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			lSBQuery.append("SELECT rlt.ifscCode  FROM  RltBankBranch rlt WHERE rlt.branchCode=:branchCode AND rlt.locationCode=:locationCode");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("branchCode", lLngBranchCode);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				if (lLstResult.get(0) != null) {
					lStrIfscCode = lLstResult.get(0).toString();
				}
			}

		} catch (Exception e) {
			logger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lStrIfscCode;
	}

	
	public String getDistrictNameFromDistrictCode(Long lLngDistrictCode,Long lLngLangId) throws Exception {
		String lStrDistrictCode = "";
		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			lSBQuery.append("SELECT districtName FROM  CmnDistrictMst  WHERE districtId = :DistrictCode and cmnLanguageMst.langId =:langId and districtId != 0");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("DistrictCode", lLngDistrictCode);
			hqlQuery.setParameter("langId",lLngLangId);
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				if (lLstResult.get(0) != null) {
					lStrDistrictCode = lLstResult.get(0).toString();
				}
			}

		} catch (Exception e) {
			logger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lStrDistrictCode;
	}


	public String getStateNameFromStateCode(Long lLngStateCode,Long lLngLangId) throws Exception {
		String lStrStateCode = "";
		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			lSBQuery.append("SELECT stateName FROM  CmnStateMst  WHERE stateId = :StateCode and cmnLanguageMst.langId =:langId and stateId != 0");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("StateCode", lLngStateCode);
			hqlQuery.setParameter("langId",lLngLangId);
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				if (lLstResult.get(0) != null) {
					lStrStateCode = lLstResult.get(0).toString();
				}
			}

		} catch (Exception e) {
			logger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lStrStateCode;
	}

	
	public List<ComboValuesVO> getAdminDept() throws Exception {
		ArrayList<ComboValuesVO> arrBranchVO = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List<ComboValuesVO> resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			strQuery.append(" SELECT locId,locName  FROM CmnLocationMst  WHERE departmentId = :departmentId order by locName");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setLong("departmentId", 100001L);			
			resultList = hqlQuery.list();
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("--Select--");
			arrBranchVO.add(cmbVO);
			if (!resultList.isEmpty()) {
				Iterator itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrBranchVO.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrBranchVO;
	}
	
	public Long getAdminDeptNameFromFieldDept(Long lLngFieldDept) throws Exception {
		Long lLngAdminDeptCode = null;
		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			lSBQuery.append("SELECT CLM2.locId FROM CmnLocationMst CLM1,CmnLocationMst CLM2  WHERE CLM1.locId = :lLngFieldDept ");
			lSBQuery.append("AND CLM1.parentLocId = CLM2.locId");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("lLngFieldDept", lLngFieldDept);			
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				if (lLstResult.get(0) != null) {
					lLngAdminDeptCode = (Long) lLstResult.get(0);
				}
			}

		} catch (Exception e) {
			logger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lLngAdminDeptCode;
	}
	
	public List<ComboValuesVO> getFieldDeptFromAdmDept(Long lLngFieldDept) throws Exception {
		ArrayList<ComboValuesVO> arrBranchVO = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List<ComboValuesVO> resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			strQuery.append(" SELECT locId,locName  FROM CmnLocationMst  WHERE parentLocId = :lLngFieldDept order by locName");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());			
			hqlQuery.setLong("lLngFieldDept", lLngFieldDept);			
			resultList = hqlQuery.list();
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("--Select--");
			arrBranchVO.add(cmbVO);
			if (!resultList.isEmpty()) {
				Iterator itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());					
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					arrBranchVO.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrBranchVO;
	}
}
