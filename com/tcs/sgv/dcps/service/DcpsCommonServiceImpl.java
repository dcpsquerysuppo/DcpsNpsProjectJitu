/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 7, 2011		Vihan Khatri								
 *******************************************************************************
 */
package com.tcs.sgv.dcps.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.CmnDistrictMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionproc.dao.PensionProcComparators;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 7, 2011
 */
public class DcpsCommonServiceImpl extends GenericDaoHibernateImpl implements
		DcpsCommonService {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/pensionproc/PensionCaseConstants");

	public DcpsCommonServiceImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	public List getMonths() {
		String query = "select monthId,monthName from SgvaMonthMst where monthId < 13";
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

	public String getDdoCode(Long lLngAsstPostId) {
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" SELECT OD.ddoCode");
		lSBQuery.append(" FROM RltDdoAsst RD, OrgDdoMst OD");
		lSBQuery
				.append(" WHERE OD.postId = RD.ddoPostId AND RD.asstPostId = :asstPostId ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("asstPostId", lLngAsstPostId);

		List lLstCodeList = lQuery.list();

		String lStrDdoCode = lLstCodeList.get(0).toString();

		return lStrDdoCode;
	}

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
			lStrQuery
					.append(" WHERE dsgn.cmnLanguageMst.langId =:langId order by dsgn.dsgnName");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			// hqlQuery.setString("Identifier", "DEPT");
			hqlQuery.setLong("langId", langId);
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList,
					new PensionProcComparators.ObjectArrayComparator(false, 1,
							0, 2, 0, true));
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
			e.printStackTrace();
		}
		return lArrLstDesignation;
	}

	public List<ComboValuesVO> getAllDepartment(Long lLngDepartmentId,
			Long langId) throws Exception {
		ArrayList<ComboValuesVO> lArrLstDepartnent = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		ComboValuesVO cmbVO;
		List lLstResultList;
		Iterator itr;
		Object[] obj;

		try {

			lStrQuery
					.append(" SELECT clm.locId,clm.locName FROM CmnLocationMst clm, OrgDepartmentMst odm ");
			lStrQuery.append(" WHERE odm.departmentId=:departmentId  ");
			lStrQuery.append(" AND clm.departmentId=odm.departmentId ");
			lStrQuery.append(" and clm.cmnLanguageMst.langId =:langId ");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			// hqlQuery.setString("Identifier", "DEPT");
			hqlQuery.setLong("langId", langId);
			hqlQuery.setLong("departmentId", lLngDepartmentId);
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList,
					new PensionProcComparators.ObjectArrayComparator(false, 1,
							0, 2, 0, true));
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

	public List getBillGroups() throws Exception {

		String query = "select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG";
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

	public Date getLastDate(Integer month, Integer year) {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		Integer day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		date.setMonth(month);
		date.setYear(year - 1900);
		date.setDate(day);

		return date;
	}

	public Date getFirstDate(Integer month, Integer year) {
		Date date = new Date();

		date.setMonth(month);
		date.setYear(year - 1900);
		date.setDate(1);

		return date;
	}

	public Object[] getSchemeNameFromBillGroup(Long billGroupId) {
		getSession();

		Object[] lObjArrSchemeNameAndCode = new Object[2];
		StringBuilder lSBQuery = new StringBuilder();
		List schemeList = new ArrayList();

		lSBQuery
				.append(" select dcpsDdoBillSchemeName,dcpsDdoSchemeCode FROM MstDcpsBillGroup WHERE dcpsDdoBillGroupId = :billGroupId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("billGroupId", billGroupId);

		schemeList = lQuery.list();
		lObjArrSchemeNameAndCode = (Object[]) schemeList.get(0);

		return lObjArrSchemeNameAndCode;

	}

	public String getYearCodeForYearId(Long yearId) {
		String lStrYearCode = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT finYearCode FROM SgvcFinYearMst WHERE finYearId = :yearId");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("yearId", yearId);
			lStrYearCode = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger
					.info("Error  while executing getCadreList of  DCPSCadreMasterDAOImpl is "
							+ e);

		}
		return lStrYearCode;
	}

	public List getCadres() {

		Session hibSession = getSession();
		String query = "select CM.cadreName FROM MstCadre CM";

		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = hibSession.createQuery(sb.toString());
		List finalList = selectQuery.list();

		return finalList;
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

	public List getBranchNames(Long lLngBankCode) throws Exception {
		List<Object> lLstReturnList = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT RB.branchId, RB.branchName from RltBankBranchPay RB ");
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
			e.printStackTrace();
			throw e;
		}
		return lLstReturnList;
	}

	public Long getIFSCCodeForBranch(Long branchName) throws Exception {
		Long lLngHstDcpsID = null;
		try {
			getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT micrCode FROM  RltBankBranchPay");
			lSBQuery.append(" WHERE branchId = :branchName ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("branchName", branchName);
			lLngHstDcpsID = (Long) lQuery.list().get(0);

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLngHstDcpsID;
	}

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
			e.printStackTrace();
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
			e.printStackTrace();
			gLogger.error("Error is : " + e, e);
			throw e;
		}

		return lLstTaluka;

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

	public List getDesignations(String lStrCurrOffice) throws Exception {

		List<Object> lLstReturnList = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT OD.designationName FROM RltOfficeDesig OD,MstOffice OM ");
			lSBQuery
					.append(" WHERE OM.officeId = OD.officeId AND OM.officeName = :officeName");
			Session lObjSession = getReadOnlySession();
			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());
			lObjQuery.setParameter("officeName", lStrCurrOffice);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();

				String lStrDesigName = null;
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					lStrDesigName = (String) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(lStrDesigName);
					lObjComboValuesVO.setDesc(lStrDesigName);

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
			throw e;
		}

		return lLstReturnList;

	}

	public List getCurrentOffices() {
		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		String query = "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice";

		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
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

}
