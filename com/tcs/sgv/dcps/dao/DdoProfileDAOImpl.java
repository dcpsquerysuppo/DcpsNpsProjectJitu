/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 24, 2011		Kapil Devani								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.valueobject.CmnDistrictMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.DDOInformationDetail;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.HstEmp;
import com.tcs.sgv.dcps.valueobject.MstDcpsBillGroup;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstSixPCArrears;
import com.tcs.sgv.pensionproc.dao.PensionProcComparators;

public class DdoProfileDAOImpl extends GenericDaoHibernateImpl implements DdoProfileDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public DdoProfileDAOImpl(Class type, SessionFactory sessionFactory) {

		// TODO Auto-generated constructor stub
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getYears() {

		String query = "select finYearId,finYearCode from SgvcFinYearMst where finYearCode between '2008' and '2015'";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;

		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		return lLstReturnList;
	}

	public List getBankNames() throws Exception {

		String query = "select MB.bankCode, MB.bankName from MstBankPay MB";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;

	}

	/**
	 * DAO method to used to get the Branch Names according to selected bank
	 * name
	 * 
	 * @param String
	 *            lStrBankName
	 * @return List
	 */

	public List getBranchNames(Long lLngBankCode) throws Exception// for
	// report
	{

		List<Object> lLstReturnList = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT RB.branchId, RB.branchName from RltBankBranchPay RB ");
			lSBQuery.append("WHERE RB.bankCode = :bankCode ");

			Session lObjSession = getReadOnlySession();
			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());

			lObjQuery.setParameter("bankCode", lLngBankCode);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			// e.printStackTrace();
			throw e;
		}
		return lLstReturnList;
	}

	/**
	 * DAO method to used to get the State Names from database
	 * 
	 * @param
	 * @return List
	 */

	public List getStateNames(Long langId) throws Exception {

		ArrayList<ComboValuesVO> lstStates = new ArrayList<ComboValuesVO>();
		List resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {

			StringBuilder strQuery = new StringBuilder();
			/*
			 * strQuery
			 * .append("Select SM.stateId,SM.stateName from CmnStateMst SM");
			 * Query query = ghibSession.createQuery(strQuery.toString());
			 */

			strQuery.append(" Select stateId,stateName ");
			strQuery.append(" FROM CmnStateMst ");
			strQuery.append(" WHERE cmnLanguageMst.langId =:langId ");

			Query query = ghibSession.createQuery(strQuery.toString());

			query.setParameter("langId", langId);

			resultList = query.list();

			cmbVO = new ComboValuesVO();

			if (resultList != null && resultList.size() > 0) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lstStates.add(cmbVO);
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			// e.printStackTrace();
			throw e;
		}
		return lstStates;

	}

	public List getDistricts(Long lStrCurrState) throws Exception {

		ArrayList<ComboValuesVO> lLstDistrict = new ArrayList<ComboValuesVO>();
		Object[] obj;
		ComboValuesVO lObjComboValuesVO = null;
		if (lStrCurrState != -1L) {

			try {
				StringBuilder lSBQuery = new StringBuilder();
				new CmnDistrictMst();

				lSBQuery.append(" Select districtId,districtName ");
				lSBQuery.append(" FROM CmnDistrictMst ");

				lSBQuery.append(" WHERE cmnStateMst.stateId =:stateId ");

				Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());

				lObjQuery.setParameter("stateId", lStrCurrState);

				List lLstResult = lObjQuery.list();

				if (lLstResult != null && lLstResult.size() > 0) {
					Iterator it = lLstResult.iterator();
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("-1");
					lObjComboValuesVO.setDesc("-- Select --");
					lLstDistrict.add(lObjComboValuesVO);
					while (it.hasNext()) {
						lObjComboValuesVO = new ComboValuesVO();
						obj = (Object[]) it.next();
						lObjComboValuesVO.setId(obj[0].toString());
						lObjComboValuesVO.setDesc(obj[1].toString());
						lLstDistrict.add(lObjComboValuesVO);
					}
				}
			} catch (Exception e) {
				gLogger.error("Error is : " + e, e);
				throw e;
			}

		} else {
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstDistrict.add(lObjComboValuesVO);
		}

		return lLstDistrict;

	}

	public List getTaluka(Long lStrCurrDst) throws Exception {

		ArrayList<ComboValuesVO> lLstTaluka = new ArrayList<ComboValuesVO>();
		Object[] obj;

		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" Select talukaId,talukaName ");
			lSBQuery.append(" FROM CmnTalukaMst ");

			lSBQuery.append(" WHERE cmnDistrictMst.districtId =:districtId ");

			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());

			lObjQuery.setParameter("districtId", lStrCurrDst);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() > 0) {
				Iterator it = lLstResult.iterator();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("-- Select --");
				lLstTaluka.add(lObjComboValuesVO);
				while (it.hasNext()) {
					lObjComboValuesVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstTaluka.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is : " + e, e);
			throw e;
		}

		return lLstTaluka;

	}

	public List getAllOffices(String lStrDdoCode) {

		List listSavedOffices = null;

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append(" SELECT dcpsDdoOfficeIdPk,dcpsDdoOfficeName,dcpsDdoOfficeDdoFlag,dcpsDdoOfficeAddress1,dcpsDdoOfficeAddress2 ");
		lSBQuery.append(" FROM DdoOffice");
		lSBQuery.append(" WHERE dcpsDdoCode = :ddoCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDdoCode);

		listSavedOffices = lQuery.list();

		return listSavedOffices;
	}

	public String getDdoCode(Long lLngAsstPostId) {

		StringBuilder lSBQuery = null;
		String lStrDdoCode = null;
		List lLstCodeList = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM RltDdoAsst RD, OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = RD.ddoPostId AND RD.asstPostId = :asstPostId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("asstPostId", lLngAsstPostId);
			lLstCodeList = lQuery.list();
			lStrDdoCode = lLstCodeList.get(0).toString();

		} catch (Exception e) {
			gLogger.error("Exception Occurred From getDdoCode of DdoProfileDAOImpl is :: " + e);
		}
		return lStrDdoCode;
	}

	public List getSchemeListForDDO(String lStrDDOCode) {

		List lDcpsDdoSchemList = null;
		try {
			StringBuilder SBQuery = new StringBuilder();

			SBQuery
					.append("select rlt.dcpsSchemeCode,mst.schemeName FROM RltDcpsDdoScheme rlt,MstDcpsSchemes mst where mst.schemeCode=rlt.dcpsSchemeCode and rlt.dcpsDdoCode = :ddoCode order by mst.schemeName,rlt.dcpsSchemeCode ");
			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDDOCode);
			lDcpsDdoSchemList = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is :" + e, e);

		}
		return lDcpsDdoSchemList;
	}

	public List getSchemeNamesFromCode(String schemeCode) {

		List resultList = null;

		try {
			StringBuilder SBQuery = new StringBuilder();

			ghibSession = getSession();
			SBQuery.append("from MstDcpsSchemes where schemeCode like :schemeCode");
			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("schemeCode", schemeCode + '%');

			resultList = lQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return resultList;
	}

	public String getSchemeNameFromCode(String schemeCode) {

		String schemeName = null;

		try {
			StringBuilder SBQuery = new StringBuilder();

			ghibSession = getSession();
			SBQuery.append("select schemeName from MstDcpsSchemes where schemeCode = :schemeCode");
			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("schemeCode", schemeCode);

			schemeName = lQuery.list().get(0).toString();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return schemeName;
	}

	public List<MstEmp> getShowGroupList(String lStrDDOCode) throws Exception {

		List showgroupList = null;
		try {
			ghibSession = getSession();

			// String query =
			// "select BG.dcpsEmpId,EM.fname,EM.mname,EM.lname,BG.dcpsBillGroupId from dcps_attach_bill_group BG JOIN MST_DCPS_EMP EM ON BG.dcpsEmpId=EM.dcpsEmpId";
			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(" from MstEmp where billGroupId is not null and ddoCode= :DDOCode ");

			Session session = getSession();
			Query stQuery = session.createQuery(SBQuery.toString());
			stQuery.setParameter("DDOCode", lStrDDOCode);
			showgroupList = stQuery.list();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return showgroupList;
	}

	public Integer getEmpListCount(String lStrDDOCode, Map displayTag) throws Exception {

		Integer count;

		try {
			ghibSession = getSession();

			String query = "select count(*) from MstEmp where ddoCode= :DDOCode and billGroupId is null";

			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(query);
			Query stQuery = ghibSession.createQuery(SBQuery.toString());
			stQuery.setParameter("DDOCode", lStrDDOCode);
			count = Integer.parseInt(stQuery.list().get(0).toString());

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			// e.printStackTrace();
			throw (e);
		}
		return count;
	}

	public List<MstEmp> getEmpListForBGIdNotNull(String lStrDDOCode) throws Exception {

		List<MstEmp> empList = new ArrayList<MstEmp>();

		try {
			ghibSession = getSession();

			String query = "from MstEmp where ddoCode= :DDOCode and billGroupId is not null";

			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(query);
			Query stQuery = ghibSession.createQuery(SBQuery.toString());
			stQuery.setParameter("DDOCode", lStrDDOCode);
			empList = stQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			// e.printStackTrace();
			throw (e);
		}
		return empList;
	}

	public List<MstDcpsBillGroup> getBillGroupList(String lStrDDOCode) throws Exception {

		List<MstDcpsBillGroup> billgroupList = new ArrayList<MstDcpsBillGroup>();
		try {
			ghibSession = getSession();
			String query = "FROM MstDcpsBillGroup";
			StringBuilder SBQuery = new StringBuilder();

			SBQuery.append(query);
			SBQuery.append(" WHERE dcpsDdoCode =  :DDOCode");

			Query stQuery = ghibSession.createQuery(SBQuery.toString());
			stQuery.setParameter("DDOCode", lStrDDOCode);
			billgroupList = stQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(" Error is : " + e, e);

			throw (e);
		}
		return billgroupList;
	}

	public DdoOffice getDdoOfficeDtls(Long ddoOfficeId) {

		List<DdoOffice> resultList = null;

		try {
			StringBuilder SBQuery = new StringBuilder();
			ghibSession = getSession();
			SBQuery.append("from DdoOffice where dcpsDdoOfficeIdPk = :ddoOfficeId");
			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("ddoOfficeId", ddoOfficeId);

			resultList = lQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return resultList.get(0);
	}

	public List<DdoOffice> getDDOOfficeList() throws Exception {

		List<DdoOffice> DCPSDdoOfficeList = null;

		try {
			ghibSession = getSession();

			String query = "FROM DdoOffice";
			// String query = "select * FROM MST_DCPS_DDO_OFFICE";
			StringBuilder SBQuery = new StringBuilder();

			SBQuery.append(query);
			Query stQuery = ghibSession.createQuery(SBQuery.toString());

			DCPSDdoOfficeList = stQuery.list();
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return DCPSDdoOfficeList;
	}

	public List<ComboValuesVO> getAllDepartment(Long lLngDepartmentId, Long langId) throws Exception {

		ArrayList<ComboValuesVO> lArrLstDepartnent = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		ComboValuesVO cmbVO;
		List lLstResultList;
		Iterator itr;
		Object[] obj;

		try {

			lStrQuery.append(" SELECT clm.locId,clm.locName FROM CmnLocationMst clm, OrgDepartmentMst odm ");
			lStrQuery.append(" WHERE odm.departmentId=:departmentId  ");
			lStrQuery.append(" AND clm.departmentId=odm.departmentId ");
			lStrQuery.append(" and clm.cmnLanguageMst.langId =:langId ");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			// hqlQuery.setString("Identifier", "DEPT");
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
					lArrLstDepartnent.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lArrLstDepartnent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.CommonDCPSArrearsDAO#getAllDesignation(java.lang
	 * .Long)
	 */

	public List<ComboValuesVO> getAllDesignation(Long langId) throws Exception {

		StringBuilder lStrQuery = new StringBuilder();
		ArrayList<ComboValuesVO> lArrLstDesignation = new ArrayList<ComboValuesVO>();
		List lLstResultList;
		ComboValuesVO cmbVO;
		Iterator itr;
		Object[] obj;
		try {

			lStrQuery.append(" Select dsgn.dsgnId,dsgn.dsgnName ");
			lStrQuery.append(" FROM OrgDesignationMst dsgn ");
			lStrQuery.append(" WHERE dsgn.cmnLanguageMst.langId =:langId order by dsgn.dsgnName,dsgn.dsgnId");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			// hqlQuery.setString("Identifier", "DEPT");
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
					lArrLstDesignation.add(cmbVO);
				}
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);

			// e.printStackTrace();
		}
		return lArrLstDesignation;
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

	public List getSavedBillGroups(String lStrDdoCode) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("SELECT dcpsDdoBillGroupId, dcpsDdoBillDescription FROM MstDcpsBillGroup");
		lSBQuery.append(" WHERE dcpsDdoCode = :ddoCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDdoCode);

		List lLstBillGroup = lQuery.list();

		return lLstBillGroup;
	}

	public MstDcpsBillGroup getBillGroupDtlsForBillGroupId(Long lLongBillGroupId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append(" FROM MstDcpsBillGroup where dcpsDdoBillGroupId = :dcpsDdoBillGroupId");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsDdoBillGroupId", lLongBillGroupId);

		MstDcpsBillGroup lObjMstDcpsBillGroup = (MstDcpsBillGroup) lQuery.uniqueResult();

		return lObjMstDcpsBillGroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.DdoProfileDAO#getEmpListForDeselection(java.util
	 * .Map)
	 */

	// Old HQL Query
	/*
	 * public List getEmpListForDeselection(String lStrDdoCode, Long
	 * lLngDesigId, String lStrEmpName, String lStrSevaarthId) throws Exception
	 * {
	 * 
	 * List lLstEmpDeselect = null; Query hqlQuery = null; StringBuilder
	 * lStrQuery = new StringBuilder(); Session session = getSession(); Long
	 * lLngDesigAll = -2L;
	 * 
	 * try {
	 * 
	 * lStrQuery.append(
	 * " SELECT  emp.dcpsEmpId,emp.name,DM.dsgnName,emp.dcpsId,emp.sevarthId,emp.ddoAsstOrNot,ODO.dcpsDdoOfficeName "
	 * );
	 * lStrQuery.append(" FROM MstEmp emp, OrgDesignationMst DM, DdoOffice ODO "
	 * );lStrQuery.append(
	 * " WHERE emp.regStatus IN (1,2) AND emp.ddoCode = :DdoCode AND emp.ddoCode IS NOT NULL AND emp.designation=DM.dsgnId "
	 * ); lStrQuery.append(" AND emp.currOff = ODO.dcpsDdoOfficeIdPk ");
	 * 
	 * if (lLngDesigId != null) { if (!(lLngDesigAll.equals(lLngDesigId))) {
	 * lStrQuery.append(" AND DM.dsgnId = :designationId "); } } if
	 * (!"".equals(lStrEmpName) && lStrEmpName != null) {
	 * lStrQuery.append(" AND UPPER(emp.name) = :lStrEmpName"); } if
	 * (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
	 * lStrQuery.append(" AND UPPER(emp.sevarthId) = :lStrSevaarthId"); }
	 * lStrQuery.append(
	 * " ORDER BY emp.name,emp.dcpsEmpId,DM.dsgnName,emp.dcpsId,emp.ddoAsstOrNot,ODO.dcpsDdoOfficeName"
	 * );
	 * 
	 * hqlQuery = session.createQuery(lStrQuery.toString());
	 * hqlQuery.setParameter("DdoCode", lStrDdoCode);
	 * 
	 * if (lLngDesigId != null) { if (!(lLngDesigId.equals(lLngDesigAll))) {
	 * hqlQuery.setParameter("designationId", lLngDesigId); } } if
	 * (!"".equals(lStrEmpName) && lStrEmpName != null) {
	 * hqlQuery.setParameter("lStrEmpName", lStrEmpName.trim()); } if
	 * (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
	 * hqlQuery.setParameter("lStrSevaarthId", lStrSevaarthId.trim()); }
	 * lLstEmpDeselect = hqlQuery.list();
	 * 
	 * } catch (Exception e) { gLogger.error("Error is :" + e, e); throw e; }
	 * return lLstEmpDeselect; }
	 */
	public List getEmpListForDeselection(String lStrDdoCode, Long lLngDesigId, String lStrEmpName, String lStrSevaarthId)
			throws Exception {

		List lLstEmpDeselect = null;
		Query hqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		Session session = getSession();
		Long lLngDesigAll = -2L;

		Date lDtCurrDate = SessionHelper.getCurDate();

		try {

			lStrQuery
					.append(" SELECT emp.DCPS_EMP_ID,emp.EMP_NAME,nvl(DM.DSGN_NAME,''),emp.dcps_id,emp.SEVARTH_ID,emp.DDOASST_OR_NOT,nvl(ODO.OFF_NAME,'') ");
			lStrQuery.append(" FROM mst_dcps_emp emp ");
			lStrQuery.append(" left join org_designation_mst DM on emp.DESIGNATION = DM.DSGN_ID ");
			lStrQuery.append(" left join mst_dcps_ddo_office ODO on ODO.DCPS_DDO_OFFICE_MST_ID = emp.CURR_OFF ");
			lStrQuery
					.append(" WHERE emp.REG_STATUS IN (1,2) AND emp.DDO_CODE = :DdoCode AND emp.DDO_CODE IS NOT NULL ");

			if (lLngDesigId != null) {
				if (!(lLngDesigAll.equals(lLngDesigId))) {
					lStrQuery.append(" AND DM.DSGN_ID = :designationId ");
				}
			}
			if (!"".equals(lStrEmpName) && lStrEmpName != null) {
				lStrQuery.append(" AND UPPER(emp.EMP_NAME) = :lStrEmpName");
			}
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				lStrQuery.append(" AND UPPER(emp.SEVARTH_ID) = :lStrSevaarthId");
			}

			lStrQuery.append(" AND ( emp.EMP_SERVEND_DT is null or emp.EMP_SERVEND_DT  >= :currentDate ) ");

			lStrQuery
					.append(" ORDER BY emp.EMP_NAME,emp.DCPS_EMP_ID,nvl(DM.DSGN_NAME,''),emp.dcps_id,emp.SEVARTH_ID,emp.DDOASST_OR_NOT,nvl(ODO.OFF_NAME,'') ");

			hqlQuery = session.createSQLQuery(lStrQuery.toString());
			hqlQuery.setParameter("DdoCode", lStrDdoCode);
			hqlQuery.setDate("currentDate", lDtCurrDate);

			if (lLngDesigId != null) {
				if (!(lLngDesigId.equals(lLngDesigAll))) {
					hqlQuery.setParameter("designationId", lLngDesigId);
				}
			}
			if (!"".equals(lStrEmpName) && lStrEmpName != null) {
				hqlQuery.setParameter("lStrEmpName", lStrEmpName.trim());
			}
			if (!"".equals(lStrSevaarthId) && lStrSevaarthId != null) {
				hqlQuery.setParameter("lStrSevaarthId", lStrSevaarthId.trim());
			}
			lLstEmpDeselect = hqlQuery.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstEmpDeselect;
	}

	public Boolean checkMultipleEntryInHstEmpForEmpIdOrNot(Long dcpsEmpId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = false;

		lSBQuery.append(" select hstdcpsId FROM HstEmp WHERE dcpsEmpId = :dcpsEmpId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		tempList = lQuery.list();
		if (tempList.size() > 1) {
			flag = true;
		}
		return flag;

	}

	public Long getDcpsEmpIdFromSevaarthIdOrName(String lStrEmpName, String lStrSevarthId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long dcpsEmpId = 0L;
		Query hqlQuery = null;

		lSBQuery.append(" select dcpsEmpId FROM MstEmp emp where emp.regStatus in (1,2) ");

		if (!"".equals(lStrEmpName) && lStrEmpName != null) {
			lSBQuery.append(" AND UPPER(emp.name) = :lStrEmpName");
		}
		if (!"".equals(lStrSevarthId) && lStrSevarthId != null) {
			lSBQuery.append(" AND UPPER(emp.sevarthId) = :lStrSevarthId");
		}

		hqlQuery = ghibSession.createQuery(lSBQuery.toString());

		if (lStrEmpName != null && !lStrEmpName.equals("")) {
			hqlQuery.setParameter("lStrEmpName", lStrEmpName.trim());
		}
		if (lStrSevarthId != null && !lStrSevarthId.equals("")) {
			hqlQuery.setParameter("lStrSevarthId", lStrSevarthId.trim());
		}

		tempList = hqlQuery.list();

		if (tempList != null && tempList.size() != 0) {
			if (tempList.get(0) != null) {
				if (!"".equals(tempList.get(0).toString())) {
					dcpsEmpId = Long.valueOf(tempList.get(0).toString());
				}
			}
		}

		return dcpsEmpId;

	}

	// Method commented to use SQL query to use left join

	/*
	 * public List getEmpListForSelection(String lStrEmpName, String
	 * lStrSevarthId,Boolean lBlMultipleEntriesInHstEmpForEmpId) throws
	 * Exception {
	 * 
	 * List lLstEmpSelect = null; Query hqlQuery = null; StringBuilder lStrQuery
	 * = new StringBuilder();
	 * 
	 * try {
	 * 
	 * lStrQuery.append(
	 * " SELECT emp.dcpsEmpId,emp.name,DM.dsgnName,emp.dcpsId,emp.currOff,ODO.dcpsDdoOfficeName,HE.orderNo,HE.orderDate"
	 * ); //lStrQuery.append(" OP.orgPostMst.postId,OP.postName");
	 * lStrQuery.append
	 * (" FROM MstEmp emp,OrgDesignationMst DM,DdoOffice ODO,HstEmp HE ");
	 * //lStrQuery.append(" ,RltDcpsPayrollEmp RL,OrgPostDetailsRlt OP")
	 * lStrQuery.append(
	 * " WHERE emp.regStatus IN (1,2) AND emp.ddoCode IS NULL AND emp.designation=DM.dsgnId "
	 * ); lStrQuery.append(" AND emp.currOff = ODO.dcpsDdoOfficeIdPk ");
	 * //lStrQuery.append(" AND emp.empOnDeptn=0 ");//lStrQuery.append(
	 * " AND RL.dcpsEmpId = emp.dcpsEmpId AND OP.orgPostMst.postId = RL.postId AND"
	 * ); lStrQuery.append(" AND HE.dcpsEmpId = emp.dcpsEmpId");
	 * 
	 * if(lBlMultipleEntriesInHstEmpForEmpId) {lStrQuery.append(
	 * " AND HE.updatedDate = (select max(HEI.updatedDate) from HstEmp HEI where HEI.dcpsEmpId = emp.dcpsEmpId)"
	 * ); }
	 * 
	 * if (!"".equals(lStrEmpName) && lStrEmpName != null) {
	 * lStrQuery.append(" AND UPPER(emp.name) = :lStrEmpName"); } if
	 * (!"".equals(lStrSevarthId) && lStrSevarthId != null) {
	 * lStrQuery.append(" AND UPPER(emp.sevarthId) = :lStrSevarthId"); }
	 * lStrQuery.append(
	 * " ORDER BY emp.name,emp.dcpsEmpId,DM.dsgnName,emp.dcpsId,emp.currOff,HE.orderNo,HE.orderDate"
	 * );
	 * 
	 * //lStrQuery.append(" ,OP.orgPostMst.postId,OP.postName"); hqlQuery =
	 * ghibSession.createQuery(lStrQuery.toString());
	 * 
	 * if (lStrEmpName != null && !lStrEmpName.equals("")) {
	 * hqlQuery.setParameter("lStrEmpName", lStrEmpName.trim()); } if
	 * (lStrSevarthId != null && !lStrSevarthId.equals("")) {
	 * hqlQuery.setParameter("lStrSevarthId", lStrSevarthId.trim()); }
	 * lLstEmpSelect = hqlQuery.list();
	 * 
	 * } catch (Exception e) { gLogger.error("Error is :" + e, e); throw e; }
	 * return lLstEmpSelect; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.DdoProfileDAO#getDdoCodeForEmpSlctDeSlct(java.lang
	 * .Long)
	 */

	public List getEmpListForSelection(String lStrEmpName, String lStrSevarthId,
			Boolean lBlMultipleEntriesInHstEmpForEmpId, String lStrDesignation) throws Exception {

		List lLstEmpSelect = null;
		Query sqlQuery = null;
		StringBuilder lStrQuery = new StringBuilder();
		Date lDtCurrDate = SessionHelper.getCurDate();

		try {

			lStrQuery
					.append(" SELECT EM.dcps_emp_id , EM.emp_name, nvl(DS.DSGN_NAME,'') , EM.dcps_id, nvl(EM.CURR_OFF,''),nvl(OD.OFF_NAME,''), HE.ORDER_NO, HE.ORDER_DATE,HE.HST_DCPS_ID,varchar_format(HE.END_DATE,'DD/MM/YYYY')");
			// lStrQuery.append(" OP.orgPostMst.postId,OP.postName");
			lStrQuery.append(" from mst_dcps_emp EM  ");
			// lStrQuery.append(" ,RltDcpsPayrollEmp RL,OrgPostDetailsRlt OP")
			lStrQuery.append(" left join org_designation_mst DS on EM.DESIGNATION = DS.DSGN_ID ");
			lStrQuery.append(" left join mst_dcps_ddo_office OD on OD.DCPS_DDO_OFFICE_MST_ID = EM.CURR_OFF ");
			// lStrQuery.append(" AND emp.empOnDeptn=0 ");
			// lStrQuery.append(" AND RL.dcpsEmpId = emp.dcpsEmpId AND OP.orgPostMst.postId = RL.postId AND");
			lStrQuery.append(" join hst_dcps_emp_details HE on HE.DCPS_EMP_ID = EM.DCPS_EMP_ID");

			if (lBlMultipleEntriesInHstEmpForEmpId) {
				lStrQuery
						.append(" and HE.END_DATE = (select max(HEI.END_DATE) from HST_DCPS_EMP_DETAILS HEI where HEI.DCPS_EMP_ID = EM.DCPS_EMP_ID )");
			}

			lStrQuery.append(" where EM.REG_STATUS in (1,2) and EM.DDO_CODE is null");
			if (lStrDesignation != null) {
				lStrQuery.append(" AND DS.DSGN_NAME = :designationId ");
			}
			if (!"".equals(lStrEmpName) && lStrEmpName != null) {
				lStrQuery.append(" AND UPPER(EM.EMP_NAME) = '" + lStrEmpName + "'");
			}
			if (!"".equals(lStrSevarthId) && lStrSevarthId != null) {
				lStrQuery.append(" AND UPPER(EM.SEVARTH_ID) = '" + lStrSevarthId + "'");
			}

			lStrQuery.append(" AND ( EM.EMP_SERVEND_DT is null or EM.EMP_SERVEND_DT  >= :currentDate ) ");

			lStrQuery.append(" order by EM.emp_name,EM.DCPS_EMP_ID,DS.DSGN_ID,EM.DCPS_ID, HE.ORDER_NO, HE.ORDER_DATE");

			// lStrQuery.append(" ,OP.orgPostMst.postId,OP.postName");

			sqlQuery = ghibSession.createSQLQuery(lStrQuery.toString());
			if (lStrDesignation != null) {
				sqlQuery.setParameter("designationId", lStrDesignation);
			}
			sqlQuery.setDate("currentDate", lDtCurrDate);

			lLstEmpSelect = sqlQuery.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstEmpSelect;
	}

	public String getDdoCodeForDDO(Long lLngPostId) {

		String lStrDdoCode = null;
		List lLstDdoDtls = null;

		try {
			getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();

			lStrDdoCode = lLstDdoDtls.get(0).toString();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);

		}
		return lStrDdoCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.DdoProfileDAO#deselectEmp(java.lang.String,
	 * java.util.Map)
	 */
	public void deselectEmp(String lStrDcpsId, Map inputMap) throws Exception {

		try {
			getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE MstEmp emp SET emp.ddoCode = NULL WHERE emp.dcpsId =:dcpsId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("dcpsId", lStrDcpsId);
			lQuery.executeUpdate();

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.DdoProfileDAO#selectEmp(java.lang.String,
	 * java.lang.String, java.util.Map)
	 */
	public void selectEmp(String lStrDcpsId, String lStrDdoCode, Map inputMap) throws Exception {

		try {
			getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE MstEmp emp SET emp.ddoCode =:ddoCode WHERE emp.dcpsId =:dcpsId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lQuery.setParameter("dcpsId", lStrDcpsId);
			lQuery.executeUpdate();

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.DdoProfileDAO#getHstEmpVO(java.lang.Long)
	 */
	public Long getHstEmpPkVal(Long lLngDcpsEmpId) throws Exception {

		Long lLngHstDcpsID = null;
		try {
			getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT HE.hstdcpsId FROM HstEmp HE");
			lSBQuery.append(" WHERE HE.dcpsEmpId.dcpsEmpId = :lLngDcpsEmpId");
			lSBQuery.append(" and HE.endDate is null ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLngDcpsEmpId", lLngDcpsEmpId);
			lLngHstDcpsID = (Long) lQuery.list().get(0);

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLngHstDcpsID;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.DdoProfileDAO#getEmpListForSixPCArrearsDDO(java.
	 * lang.String)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.DdoProfileDAO#getYearsForSixPCYearly
	 */
	public List getYearsForSixPCYearly() {

		String query = "select finYearId,finYearDesc from SgvcFinYearMst";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;

		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		return lLstReturnList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.DdoProfileDAO#getEmpListForSixPCArrearsYearlyDDO
	 * (java.lang.String, java.lang.Long)
	 */

	public List getEmpListForSixPCArrearAmountSchedule(String lStrDDOCode, Long yearId) throws Exception {

		List empList = null;

		try {

			String query = "select EM.dcpsEmpId,EM.dcpsId,EM.name,PC.yearlyAmount,PC.dcpsSixPCYearlyId,FY.finYearCode FROM MstEmp EM ,RltDcpsSixPCYearly PC,SgvcFinYearMst FY where EM.dcpsEmpId=PC.dcpsEmpId and PC.finYearId=FY.finYearId and EM.ddoCode= :DDOCode and EM.dcpsId is not null and PC.finYearId= :yearId";

			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(query);
			Query stQuery = ghibSession.createQuery(SBQuery.toString());
			stQuery.setParameter("DDOCode", lStrDDOCode);
			stQuery.setParameter("yearId", yearId);
			empList = stQuery.list();

		} catch (Exception e) {

			// e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return empList;

	}

	public List getSchemesforDDOComboVO(String lStrDDOCode) {

		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		String query = "select rlt.dcpsSchemeCode,mst.schemeName FROM RltDcpsDdoScheme rlt,MstDcpsSchemes mst where mst.schemeCode=rlt.dcpsSchemeCode and rlt.dcpsDdoCode = :ddoCode order by mst.schemeName,rlt.dcpsSchemeCode ";

		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDDOCode);

		List resultList = selectQuery.list();

		cmbVO = new ComboValuesVO();

		if (resultList != null && resultList.size() > 0) {
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("-- Select --");
			finalList.add(cmbVO);
			Iterator it = resultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				obj = (Object[]) it.next();
				cmbVO.setId(obj[0].toString());
				cmbVO.setDesc(obj[1].toString());
				finalList.add(cmbVO);
			}
		}

		return finalList;
	}

	public String getIFSCCodeForBranch(Long branchName) throws Exception {

		String lLngIFSCode = null;
		try {
			getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT ifscCode FROM RltBankBranchPay");
			lSBQuery.append(" WHERE branchId = :branchName ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("branchName", branchName);
			if (lQuery.list() != null) {
				if (lQuery.list().get(0) != null) {
					lLngIFSCode = (String) lQuery.list().get(0);
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLngIFSCode;
	}

	public String getIFSCCodeForBranchBsrCode(String branchName) throws Exception {

		String lLngIFSCode = null;
		try {
			getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT ifscCode FROM  RltBankBranchPay");
			lSBQuery.append(" WHERE bsrCode = :branchName ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("branchName", branchName);
			if (lQuery.list() != null) {
				if (lQuery.list().get(0) != null) {
					lLngIFSCode = (String) lQuery.list().get(0);
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLngIFSCode;
	}

	public List getClassGroupforBillGroupId(Long billGroupId) throws Exception {

		List classGroupList = null;
		try {

			String query = "select dcpsClassGroup from where dcpsBillGroupId= :billGroupId";

			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(query);
			Query stQuery = ghibSession.createQuery(SBQuery.toString());
			stQuery.setParameter("billGroupId", billGroupId);
			classGroupList = stQuery.list();

		} catch (Exception e) {

			// e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return classGroupList;

	}

	public List getBillGroups(String lStrDDOCode) throws Exception {

		String query = "select dcpsDdoBillGroupId,dcpsDdoBillDescription from MstDcpsBillGroup where dcpsDdoCode = :dcpsDdoCode";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("dcpsDdoCode", lStrDDOCode);

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List getDDOOffices(String lStrDDOCode) throws Exception {

		String query = "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice where dcpsDdoCode = :dcpsDdoCode";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("dcpsDdoCode", lStrDDOCode);

		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc(obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	public List<MstEmp> getEmpListForGivenBillGroup(Long lLongBillGroupId, String lStrDDOCode) throws Exception {

		List<MstEmp> empList = new ArrayList<MstEmp>();

		try {
			ghibSession = getSession();

			String query = "from MstEmp where ddoCode= :DDOCode and billGroupId= :billGroupId";

			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(query);
			Query stQuery = ghibSession.createQuery(SBQuery.toString());
			stQuery.setParameter("billGroupId", lLongBillGroupId);
			stQuery.setParameter("DDOCode", lStrDDOCode);
			empList = stQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			// e.printStackTrace();
			throw (e);
		}
		return empList;
	}

	public String getBillGroupDetailsForBillGroupId(Long lLongBillGroupId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery
				.append("select dcpsDdoBillDescription FROM MstDcpsBillGroup where dcpsDdoBillGroupId = :dcpsDdoBillGroupId");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsDdoBillGroupId", lLongBillGroupId);

		String lStrBillGroupDtls = lQuery.uniqueResult().toString();

		return lStrBillGroupDtls;
	}

	public Boolean checkArrearsApprovedBeforeDeselection(Long dcpsEmpId, Long yearId) {

		// Checks Arrears approved or not for current financial year
		
		Boolean flag = true;
		Boolean lBlPadmaAndCurrFinYearFlag = false;
		
		// Code to set lBlPadmaAndCurrFinYearFlag flag
		
		StringBuilder lSBQueryPadmaAndCurrYear = new StringBuilder();
		List<Long> tempListPadmaAndCurrYear = new ArrayList();

		lSBQueryPadmaAndCurrYear.append(" SELECT dcpsEmpId from MstEmp EM ");
		lSBQueryPadmaAndCurrYear.append(" WHERE EM.dcpsEmpId = :dcpsEmpId and EM.payCommission = :padmaPayCommission");

		Query lQueryPadmaAndCurrYear = ghibSession.createQuery(lSBQueryPadmaAndCurrYear.toString());
		lQueryPadmaAndCurrYear.setParameter("dcpsEmpId", dcpsEmpId);
		lQueryPadmaAndCurrYear.setParameter("padmaPayCommission", "700339");

		tempListPadmaAndCurrYear = lQueryPadmaAndCurrYear.list();
		if(tempListPadmaAndCurrYear != null)
		{
			if (tempListPadmaAndCurrYear.size() > 0 && yearId == 25) {
				lBlPadmaAndCurrFinYearFlag = true;
			}
		}
		
		// Code to set lBlPadmaAndCurrFinYearFlag flag overs
		
		if(!lBlPadmaAndCurrFinYearFlag)
		{
			StringBuilder lSBQuery = new StringBuilder();
			List<Long> tempList = new ArrayList();
	
		if ( yearId!= 26)
		{
			lSBQuery.append(" SELECT RL.dcpsSixPCYearlyId FROM RltDcpsSixPCYearly RL,MstSixPCArrears MS ");
			lSBQuery.append(" WHERE RL.dcpsEmpId = MS.dcpsEmpId ");
			lSBQuery.append(" AND RL.statusFlag = 'A' ");
			lSBQuery.append(" AND RL.dcpsEmpId = :dcpsEmpId ");
			lSBQuery.append(" AND RL.finYearId = :finYearId ");
	
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("dcpsEmpId", dcpsEmpId);
			lQuery.setParameter("finYearId", yearId);
	
			tempList = lQuery.list();
			if (tempList.size() == 0) {
				flag = false;
			}
		}
	}
		// Checks Arrears are not in any intermediate stage for any year since
		// they will be lost when employee is transferred.

		StringBuilder lSBQuery2 = new StringBuilder();
		List<Long> tempList2 = new ArrayList();

		lSBQuery2.append(" SELECT RL.dcpsSixPCYearlyId FROM RltDcpsSixPCYearly RL,MstSixPCArrears MS ");
		lSBQuery2.append(" WHERE RL.dcpsEmpId = MS.dcpsEmpId ");
		lSBQuery2.append(" AND RL.statusFlag in ('F','G','R') ");
		lSBQuery2.append(" AND RL.dcpsEmpId = :dcpsEmpId ");

		Query lQuery2 = ghibSession.createQuery(lSBQuery2.toString());
		lQuery2.setParameter("dcpsEmpId", dcpsEmpId);

		tempList2 = lQuery2.list();
		if (tempList2 != null) {
			if (tempList2.size() != 0) {
				flag = false;
			}
		}

		return flag;
	}

	public Boolean checkArrearsExistForEmp(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;
		Boolean lBlArrearsExist = true;
		MstSixPCArrears lObjMstSixPCArrears = null;

		lSBQuery.append(" FROM MstSixPCArrears");
		lSBQuery.append(" WHERE dcpsEmpId.dcpsEmpId = :dcpsEmpId ");
		lSBQuery.append(" AND totalAmount != 0");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		lObjMstSixPCArrears = (MstSixPCArrears) lQuery.uniqueResult();

		if (lObjMstSixPCArrears == null) {
			lBlArrearsExist = false;
		}

		return lBlArrearsExist;
	}
	
	public Boolean checkIfPrevArrsApproved(Long dcpsEmpId, Long yearId) {

		// Checks Arrears approved or not for current financial year

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Boolean flag = true;

		lSBQuery.append(" SELECT RL.dcpsSixPCYearlyId FROM RltDcpsSixPCYearly RL,MstSixPCArrears MS ");
		lSBQuery.append(" WHERE RL.dcpsEmpId = MS.dcpsEmpId ");
		lSBQuery.append(" AND RL.dcpsEmpId = :dcpsEmpId ");
		lSBQuery.append(" AND RL.finYearId < :finYearId ");
		lSBQuery.append(" AND RL.statusFlag <> 'A' ");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);
		lQuery.setParameter("finYearId", yearId);

		tempList = lQuery.list();
		if(tempList != null)
		{
			if (tempList.size() != 0) {
				flag = false;
			}
		}
		
		return flag;
	}

	public List getVacantPostsInOffice(Long lLongOffice, long locId, String lStrDDOCode, long lLongDesig) throws Exception {

		List<Object> lLstReturnList;
		try {

			StringBuilder lSBQuery = new StringBuilder();
			Date lDtCurrDate = SessionHelper.getCurDate();

			/*
			 * lSBQuery.append(" Select distinct PD.orgPostMst.postId,PD.postName from HrPayOfficepostMpg HR,OrgPostDetailsRlt PD,OrgPostMst OP"
			 * );lSBQuery.append(
			 * " where HR.orgPostMstByPostId.postId = PD.orgPostMst.postId and OP.postId=PD.orgPostMst.postId and OP.activateFlag=1"
			 * );lSBQuery.append(
			 * " and PD.cmnLocationMst.locId = OP.locationCode and PD.cmnLocationMst.locId="
			 * ); lSBQuery.append(locId);
			 * lSBQuery.append(" and HR.ddoOffice.dcpsDdoOfficeIdPk = :office "
			 * ); // lSBQuery.append(" and OP.postId = PD.orgPostMst.postId");
			 * lSBQuery.append(
			 * " and HR.orgPostMstByPostId.postId not in (select UP.orgPostMstByPostId.postId from OrgUserpostRlt UP where UP.activateFlag=1) "
			 * );lSBQuery.append(
			 * " and HR.orgPostMstByPostId.postId not in (select RL.postId from RltDcpsPayrollEmp RL,MstEmp EM where RL.dcpsEmpId = EM.dcpsEmpId and RL.postId is not null  and EM.ddoCode = :ddoCode and EM.regStatus not in (1,2) )"
			 * ); lSBQuery.append(" order by PD.postName");
			 */

			lSBQuery.append(" select a.POST_ID,b.POST_NAME,a.POST_TYPE_LOOKUP_ID from ");
			lSBQuery.append(" org_post_mst a inner join ORG_POST_DETAILS_RLT b on b.POST_ID=a.POST_ID");
			lSBQuery.append(" left outer join ORG_USERPOST_RLT c on c.POST_ID=a.POST_ID and c.ACTIVATE_FLAG=1");
			lSBQuery.append(" inner join HR_PAY_OFFICEPOST_MPG d on d.POST_ID=a.POST_ID");
			lSBQuery.append(" inner join MST_DCPS_DDO_OFFICE e on e.DCPS_DDO_OFFICE_MST_ID=d.OFFICE_ID");
			lSBQuery
					.append(" where b.LOC_ID = :locId and c.POST_ID is null and a.LOCATION_CODE = :office and (a.END_DATE > :currentDate or a.END_DATE is null)");
			lSBQuery
					.append(" and d.post_Id not in (select RL.post_Id from rlt_dcps_payroll_emp RL,mst_dcps_emp EM where RL.dcps_Emp_Id = EM.dcps_Emp_Id and RL.post_Id is not null and EM.ddo_Code = :ddoCode and EM.reg_Status not in (1,2))");
			if(lLongDesig != 0) //added by shailesh
				lSBQuery.append("  and b.DSGN_ID = "+lLongDesig);
			lSBQuery.append(" and (a.END_DATE > sysdate or a.END_DATE is null) ");
			lSBQuery.append(" and a.ACTIVATE_FLAG = 1 and POST_TYPE_LOOKUP_ID  in  (10001198130,10001198129)  order by b.post_name");
			logger.info("lSBQuery. "+lSBQuery.toString());
			logger.info("locId. "+locId);
			logger.info("lLongOffice. "+lLongOffice);
			logger.info("lDtCurrDate. "+lDtCurrDate);
			logger.info("lStrDDOCode. "+lStrDDOCode);
			lLstReturnList = null;
			Query selectQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			selectQuery.setParameter("office", lLongOffice);
			selectQuery.setParameter("locId", locId);
			selectQuery.setDate("currentDate", lDtCurrDate);
			selectQuery.setParameter("ddoCode", lStrDDOCode);

			List lLstResult = selectQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
				
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					//added by shailesh
					
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					
					if(obj[2] != null)
					{
						if(obj[2].toString().trim().equals("10001198129"))
						{
							lObjComboValuesVO.setDesc(obj[1].toString().concat(" (P)"));
						}
						else if(obj[2].toString().trim().equals("10001198130"))
						{
							lObjComboValuesVO.setDesc(obj[1].toString().concat(" (T)"));
						}
						else
						{
							lObjComboValuesVO.setDesc(obj[1].toString());
						}
					}
					else
					{
						lObjComboValuesVO.setDesc(obj[1].toString());
					}
					
					lLstReturnList.add(lObjComboValuesVO);
				
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw e;
		}

		return lLstReturnList;
	}

	public List getVacantPostsInOffice(Long lLongOffice, String lStrDDOCode, long lLongDesig) throws Exception {

		List<Object> lLstReturnList;
		try {

			StringBuilder lSBQuery = new StringBuilder();
			Date lDtCurrDate = SessionHelper.getCurDate();

			// Old Query
			/*
			 * lSBQuery.append(" Select distinct PD.orgPostMst.postId,PD.postName from HrPayOfficepostMpg HR,OrgPostDetailsRlt PD,OrgPostMst OP"
			 * );lSBQuery.append(
			 * " where HR.orgPostMstByPostId.postId = PD.orgPostMst.postId and OP.postId=PD.orgPostMst.postId and OP.activateFlag=1"
			 * );lSBQuery.append(
			 * " and PD.cmnLocationMst.locId = OP.locationCode and HR.ddoOffice.dcpsDdoOfficeIdPk = :office "
			 * ); // lSBQuery.append(" and OP.postId = PD.orgPostMst.postId");
			 * lSBQuery.append(
			 * " and HR.orgPostMstByPostId.postId not in (select UP.orgPostMstByPostId.postId from OrgUserpostRlt UP where  UP.activateFlag=1) "
			 * );lSBQuery.append(
			 * " and HR.orgPostMstByPostId.postId not in (select RL.postId from RltDcpsPayrollEmp RL,MstEmp EM where RL.dcpsEmpId = EM.dcpsEmpId and RL.postId is not null  and EM.ddoCode = :ddoCode and EM.regStatus not in (1,2) )"
			 * ); lSBQuery.append(" order by PD.postName ");
			 */

			// New Query
			/*
			 * select a.POST_ID,b.POST_NAME from org_post_mst a inner join
			 * ORG_POST_DETAILS_RLT b on b.POST_ID=a.POST_ID left outer join
			 * ORG_USERPOST_RLT c on c.POST_ID=a.POST_ID and c.ACTIVATE_FLAG=1
			 * inner join HR_PAY_OFFICEPOST_MPG d on d.POST_ID=a.POST_ID inner
			 * join MST_DCPS_DDO_OFFICE e on
			 * e.DCPS_DDO_OFFICE_MST_ID=d.OFFICE_ID where c.POST_ID is null and
			 * a.LOCATION_CODE=2000053 and (a.END_DATE > sysdate or a.END_DATE
			 * is null) order by b.post_name
			 */

			lSBQuery.append(" select a.POST_ID,b.POST_NAME,a.POST_TYPE_LOOKUP_ID from");
			lSBQuery.append(" org_post_mst a inner join ORG_POST_DETAILS_RLT b on b.POST_ID=a.POST_ID");
			lSBQuery.append(" left outer join ORG_USERPOST_RLT c on c.POST_ID=a.POST_ID and c.ACTIVATE_FLAG=1");
			lSBQuery.append(" inner join HR_PAY_OFFICEPOST_MPG d on d.POST_ID=a.POST_ID");
			lSBQuery.append(" inner join MST_DCPS_DDO_OFFICE e on e.DCPS_DDO_OFFICE_MST_ID=d.OFFICE_ID");
			lSBQuery
					.append(" where c.POST_ID is null and a.LOCATION_CODE = :office and (a.END_DATE >= :currentDate or a.END_DATE is null)");
			lSBQuery
					.append(" and d.post_Id not in (select RL.post_Id from rlt_dcps_payroll_emp RL,mst_dcps_emp EM where RL.dcps_Emp_Id = EM.dcps_Emp_Id and RL.post_Id is not null and EM.ddo_Code = :ddoCode and EM.reg_Status not in (1,2))");
			if(lLongDesig != 0) //added by shailesh
			lSBQuery.append(" and b.DSGN_ID = "+lLongDesig);
			lSBQuery.append(" and (a.END_DATE > sysdate or a.END_DATE is null) ");
			lSBQuery.append(" and a.ACTIVATE_FLAG = 1 and a.POST_TYPE_LOOKUP_ID  in  (10001198130,10001198129)  order by b.post_name");
			
			logger.info("lSBQuery. "+lSBQuery.toString());
			logger.info("lLongOffice. "+lLongOffice);
			logger.info("lStrDDOCode. "+lStrDDOCode);
			lLstReturnList = null;
			Query selectQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			selectQuery.setParameter("office", lLongOffice);
			selectQuery.setDate("currentDate", lDtCurrDate);
			selectQuery.setParameter("ddoCode", lStrDDOCode);
			logger.info("lSBQuery. "+lLongOffice);
			logger.info("lSBQuery. "+lDtCurrDate);
			logger.info("lSBQuery. "+lStrDDOCode);
			List lLstResult = selectQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				/*
				 * lObjComboValuesVO = new ComboValuesVO();
				 * lObjComboValuesVO.setId("-1");
				 * lObjComboValuesVO.setDesc("--Select--");
				 * lLstReturnList.add(lObjComboValuesVO);
				 */
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {

					obj = (Object[]) lLstResult.get(liCtr);
					//added by shailesh
					//if(!obj[2].toString().trim().equals("10001198152")){
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					if(obj[2] != null)
					{
						if(obj[2].toString().trim().equals("10001198129"))
						{
							lObjComboValuesVO.setDesc(obj[1].toString().concat(" (P)"));
						}
						else if(obj[2].toString().trim().equals("10001198130"))
						{
							lObjComboValuesVO.setDesc(obj[1].toString().concat(" (T)"));
						}
						else
						{
							lObjComboValuesVO.setDesc(obj[1].toString());
						}
					}
					else
					{
						lObjComboValuesVO.setDesc(obj[1].toString());
					}
					lLstReturnList.add(lObjComboValuesVO);
				}
				//}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw e;
		}

		return lLstReturnList;
	}

	public HstEmp getHstEmpVOLatest(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();
		HstEmp lObjHstEmpVO = null;
		Query lQuery = null;

		lSBQuery.append(" FROM HstEmp");
		lSBQuery.append(" WHERE dcpsEmpId = :dcpsEmpId order by updatedDate desc ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		lObjHstEmpVO = (HstEmp) lQuery.list().get(0);

		return lObjHstEmpVO;
	}

	public Object[] getPostBGAndGROrderForEmp(Long dcpsEmpId) {

		Object[] lObjArrPostBGAndGR = new Object[6];
		StringBuilder lSBQuery = new StringBuilder();
		List lListPostBGAndGR = new ArrayList();

		lSBQuery.append(" SELECT ou.POST_ID,OP.POST_NAME,BG.BILL_GROUP_ID,BG.DESCRIPTION,ot.ORDER_ID,ot.ORDER_NAME ");
		lSBQuery
				.append(" FROM ORG_EMP_MST OE,ORG_USERPOST_RLT OU,ORG_POST_DETAILS_RLT OP,MST_DCPS_BILL_GROUP BG,MST_DCPS_EMP EM ,HR_PAY_ORDER_HEAD_POST_MPG MP,HR_PAY_ORDER_HEAD_MPG MPG,HR_PAY_ORDER_MST OT");
		lSBQuery
				.append(" where oe.USER_ID=ou.USER_ID and OU.POST_ID = OP.POST_ID and EM.ORG_EMP_MST_ID = OE.EMP_ID and BG.BILL_GROUP_ID = EM.BILLGROUP_ID");
		lSBQuery
				.append(" and ou.ACTIVATE_FLAG=1 and mp.ORDER_HEAD_ID = mpg.ORDER_HEAD_ID and mpg.ORDER_ID = ot.ORDER_ID and mp.POST_ID = ou.POST_ID");
		lSBQuery.append(" and EM.DCPS_EMP_ID = " + dcpsEmpId);
		logger.info("lsbquery "+lSBQuery.toString());
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lListPostBGAndGR = lQuery.list();
		if (lListPostBGAndGR != null) {
			if (lListPostBGAndGR.size() != 0) {

				lObjArrPostBGAndGR = (Object[]) lListPostBGAndGR.get(0);
			}
		}

		return lObjArrPostBGAndGR;

	}
	
	/*
	public void update4thInstToDraftFromRjt(Long lLngDCPSEmpId, Long lLngCurPostID,
			Long lLngCurUserId, Date lDtCurDate) throws Exception {
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			
			lSBQuery.append(" UPDATE RltDcpsSixPCYearly SET statusFlag = 'D',updatedUserId = :updatedUserId ,updatedPostId = :updatedPostID ,updatedDate = :upadtedDate ");
			lSBQuery.append(" WHERE dcpsEmpId = :lLngDCPSEmpId and finYearId = 25 and statusFlag = 'R' ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("lLngDCPSEmpId", lLngDCPSEmpId);
			lQuery.setLong("updatedUserId", lLngCurUserId);
			lQuery.setLong("updatedPostID", lLngCurPostID);
			lQuery.setParameter("upadtedDate", lDtCurDate);
			
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
	}
	*/
	
	
	//added by shailesh : start
	public List getSubstituteList(){
		List substituteList = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT LOOKUP_ID,LOOKUP_NAME FROM CMN_LOOKUP_MST where PARENT_LOOKUP_ID = 10001198152");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		substituteList = lQuery.list();
		return substituteList;
	}
	
	public List getPostDetails(long dcpsEmpId){
		List postDetails = null;
		StringBuffer lSBQBuffer = new StringBuffer();
		lSBQBuffer.append("SELECT ou.POST_ID,OP.POST_NAME  FROM ORG_EMP_MST OE,ORG_USERPOST_RLT OU,ORG_POST_DETAILS_RLT OP,MST_DCPS_EMP EM");
		lSBQBuffer.append(" where oe.USER_ID=ou.USER_ID and OU.POST_ID = OP.POST_ID and EM.ORG_EMP_MST_ID = OE.EMP_ID and ou.ACTIVATE_FLAG = 1 and EM.DCPS_EMP_ID = "); 
		lSBQBuffer.append(dcpsEmpId);
		gLogger.info("lSBQBuffer "+lSBQBuffer);
		Query query = ghibSession.createSQLQuery(lSBQBuffer.toString());
		if(query != null)
			postDetails = query.list();
		return postDetails;
	}
	
	public String billGenerated(long empID,int year,int month,String locId){
		StringBuffer lSBQBuffer = new StringBuffer();
		long billId = 0;
		/*lSBQBuffer.append("SELECT PAYBILL_ID FROM mst_dcps_emp emp ,PAYBILL_HEAD_MPG pbh where emp.BILLGROUP_ID=pbh.BILL_NO "); 
		lSBQBuffer.append(" and pbh.APPROVE_FLAG in (0,1) and emp.DCPS_EMP_ID= "+empID);
		lSBQBuffer.append(" and pbh.PAYBILL_MONTH = "+month+ " and pbh.PAYBILL_YEAR = "+year);*/
		
		/*lSBQBuffer.append("SELECT payslip.PAYSLIP_ID FROM HR_PAY_PAYSLIP payslip,MST_DCPS_EMP emp,HR_EIS_EMP_MST eis where eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID  and eis.EMP_ID=payslip.EMP_ID and payslip.PAYSLIP_MONTH=");
		lSBQBuffer.append(month+" and payslip.payslip_year ="+year +"  and emp.DCPS_EMP_ID= "+empID);*/
		
		lSBQBuffer.append("SELECT head.PAYBILL_MONTH,head.PAYBILL_YEAR,head.APPROVE_FLAG FROM ");  
		lSBQBuffer.append("hr_pay_paybill paybill,MST_DCPS_EMP emp,HR_EIS_EMP_MST eis,PAYBILL_HEAD_MPG head ");
		lSBQBuffer.append("where emp.ORG_EMP_MST_ID=eis.EMP_MPG_ID ");
		lSBQBuffer.append(" and eis.EMP_ID=paybill.EMP_ID ");
		lSBQBuffer.append(" and head.LOC_ID="+locId);
		lSBQBuffer.append(" and emp.DCPS_EMP_ID="+empID);
		lSBQBuffer.append(" and paybill.PAYBILL_GRP_ID=head.PAYBILL_ID ");
		/*lSBQBuffer.append("--and head.PAYBILL_MONTH=12");
		lSBQBuffer.append("--and head.PAYBILL_year=2012");
		*/lSBQBuffer.append("and head.APPROVE_FLAG in (0,5,6,7,8,9,4)");
		
		/// removed to improve system performance on 01/07/2014
		/*lSBQBuffer.append(" and head.PAYBILL_YEAR >=( SELECT max(paySlip.PAYSLIP_YEAR) FROM HR_PAY_PAYSLIP paySlip,MST_DCPS_EMP dcps,HR_EIS_EMP_MST eis ");
		lSBQBuffer.append("  where payslip.EMP_ID=eis.EMP_ID  ");
		lSBQBuffer.append("  and eis.EMP_MPG_ID=dcps.ORG_EMP_MST_ID and dcps.DCPS_EMP_ID= "+empID);
		lSBQBuffer.append("  and payslip.LOC_ID="+locId+" ) ");
		lSBQBuffer.append("  order by  head.PAYBILL_YEAR desc,head.PAYBILL_MONTH desc ");
		*/

		gLogger.info("lSBQBuffer "+lSBQBuffer);
		Query query = ghibSession.createSQLQuery(lSBQBuffer.toString());
		if(query != null){
			Object[] latestBillRow=null;
			if(query.list().size() > 0){
				latestBillRow=(Object[] ) query.list().get(0);
				if(Integer.parseInt(latestBillRow[2].toString())==1)
					return "no";
				else
					return latestBillRow[0].toString()+"/"+latestBillRow[1].toString();
			}
			
		}
		return "no";
	}


	//added by shailesh : end
	//LPC uniqueID
	public int getLPCUniqueID(String payslipId,String empId){
		StringBuffer lSBQBuffer = new StringBuffer();
		lSBQBuffer.append("select count(1) FROM HR_PAY_PAYSLIP pay,HR_EIS_EMP_MST eis,MST_DCPS_EMP emp  ");
		lSBQBuffer.append("  where pay.EMP_ID=eis.EMP_ID ");
		lSBQBuffer.append("  and eis.EMP_MPG_ID=emp.ORG_EMP_MST_ID ");
		lSBQBuffer.append("  and pay.PAYBILL_ID= "+payslipId);
		lSBQBuffer.append("  and emp.DCPS_EMP_ID="+empId);
		gLogger.info("lSBQBuffer "+lSBQBuffer);
		Query query = ghibSession.createSQLQuery(lSBQBuffer.toString());
		return Integer.parseInt(query.uniqueResult().toString());
		
	}
	
	public String getLocId(String office)
	{
		String locId = null;
		StringBuffer lSBQBuffer = new StringBuffer();
		lSBQBuffer.append("SELECT LOC_ID FROM MST_DCPS_DDO_OFFICE where DCPS_DDO_OFFICE_MST_ID = "+office);
		gLogger.info("lSBQBuffer "+lSBQBuffer);
		gLogger.info("office----"+office);
		Query query = ghibSession.createSQLQuery(lSBQBuffer.toString());
		locId = query.uniqueResult().toString();
		gLogger.info("locid-----"+locId);
		return locId;
	}

}
