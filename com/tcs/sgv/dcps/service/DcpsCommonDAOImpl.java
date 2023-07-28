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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.common.valueobject.CmnDistrictMst;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.DdoOffice;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.pensionproc.dao.PensionProcComparators;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Apr 7, 2011
 */
public class DcpsCommonDAOImpl extends GenericDaoHibernateImpl implements
		DcpsCommonDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/pensionproc/PensionCaseConstants");

	public DcpsCommonDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		/*ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
		*/
		setSessionFactory(sessionFactory);
		ghibSession = getSession();

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
		
		Calendar c = Calendar.getInstance();
	    int year = c.get(Calendar.YEAR);
	    int month = c.get(Calendar.MONTH);
	    if(4>month+1){
	        year = year-1;
	    }	

		String query = "select finYearId,finYearCode from SgvcFinYearMst where finYearCode between '2007' and '"+year+"'";
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

	
	public List getFinyear() {
		
		Calendar c = Calendar.getInstance();
	    int year = c.get(Calendar.YEAR);
	    int month = c.get(Calendar.MONTH);
	    if(4>month+1){
	        year = year-1;
	    }
		
		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '"+year+"' order by finYearCode ASC";
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
	
	public List getFinyearsForDelayedType() {
		Calendar c = Calendar.getInstance();
	    int year = c.get(Calendar.YEAR);
	    int month = c.get(Calendar.MONTH);
	    if(4>month+1){
	        year = year-1;
	    }
		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2005' and '"+year+"' order by finYearCode ASC";
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
	
	public List getFinyearsAfterCurrYear() {
		Calendar c = Calendar.getInstance();
	    int year = c.get(Calendar.YEAR);
	    int month = c.get(Calendar.MONTH);
	    
		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '"+year+"' order by finYearCode ASC";
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

		String lStrDdoCode = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append(" SELECT OD.ddoCode");
		lSBQuery.append(" FROM RltDdoAsst RD, OrgDdoMst OD");
		lSBQuery.append(" WHERE OD.postId = RD.ddoPostId AND RD.asstPostId = :asstPostId ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("asstPostId", lLngAsstPostId);

		List lLstCodeList = lQuery.list();

		if(lLstCodeList != null)
		{
			if(lLstCodeList.size() != 0)
			{
				if(lLstCodeList.get(0) != null)
				{
					lStrDdoCode = lLstCodeList.get(0).toString();
				}
			}
		}

		return lStrDdoCode;
	}

	public List<ComboValuesVO> getAllDesignation(Long lLngDeptId, Long langId)
			throws Exception {

		StringBuilder lStrQuery = new StringBuilder();
		ArrayList<ComboValuesVO> lArrLstDesignation = new ArrayList<ComboValuesVO>();
		List lLstResultList;
		ComboValuesVO cmbVO;
		Iterator itr;
		Object[] obj;
		try {

			lStrQuery
					.append(" SELECT ODM.dsgnId,ODM.dsgnName FROM OrgDesignationMst ODM, MstDcpsDesignation MDD ");
			lStrQuery
					.append(" WHERE ODM.dsgnId = MDD.orgDesignationId AND MDD.fieldDeptId = :fieldDeptId AND MDD.langId =:langId order by ODM.dsgnName,ODM.dsgnId");

			// lStrQuery.append(" SELECT desigId,desigDesc FROM MstDcpsDesignation ");
			// lStrQuery.append(" WHERE fieldDeptId = :fieldDeptId AND langId =:langId order by desigDesc,desigId");

			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());

			hqlQuery.setLong("langId", langId);
			hqlQuery.setLong("fieldDeptId", lLngDeptId);

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
			//e.printStackTrace();
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

	public List<ComboValuesVO> getAllHODDepartment(Long lLngDepartmentId,
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

	public List getAllTreasuries() throws Exception {

		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId = 100003 order by CM.locName";
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
	
	public List getAllTreasuriesAndSubTreasuries() throws Exception {

		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId in (100003,100006) order by CM.locName";
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

		lSBQuery.append(" select dcpsDdoBillSchemeName,dcpsDdoSchemeCode FROM MstDcpsBillGroup WHERE dcpsDdoBillGroupId = :billGroupId");//REPLACE(SCHEME_NAME,'%','PERCENT')
		
		//lSBQuery.append(" select SCHEME_NAME,SCHEME_CODE FROM MST_DCPS_BILL_GROUP WHERE BILL_GROUP_ID = '"+billGroupId+"' ");

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
			//e.printStackTrace();
			gLogger
					.info("Error  while executing getCadreList of  DCPSCadreMasterDAOImpl is "
							+ e);

		}
		return lStrYearCode;
	}

	public String getMonthForId(Long monthId) {

		String lStrYearCode = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT monthName FROM SgvaMonthMst WHERE monthId = :monthId");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("monthId", BigDecimal.valueOf(monthId));
			lStrYearCode = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error is " + e);

		}
		return lStrYearCode;
	}

	public List getCadres() {

		String query = "select CM.cadreId,CM.cadreName FROM DcpsCadreMst CM";
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
		//String query = "select MB.bankCode, MB.bankName from MstBankPay MB order by MB.bankName,MB.bankCode"; 
		String query = "select MB.bankCode, MB.bankName from MstBankPay MB where MB.activateFlag = '1' order by MB.bankName,MB.bankCode";   // changed by sid, added MB.activateFlag = '1'
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
			.append(" SELECT RB.branchId, RB.branchName from RltBankBranchPay RB ");
			lSBQuery
			.append(" WHERE RB.bankCode = :bankCodeVar and RB.activateFlag = '1' order by RB.branchName,RB.branchId "); //changed by sid, added RB.activateFlag = '1'

			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());

			lObjQuery.setParameter("bankCodeVar", lLngBankCode);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			lLstReturnList = new ArrayList<Object>();

			if (lLstResult != null && lLstResult.size() != 0) {
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {

				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			//e.printStackTrace();
			throw e;
		}
		return lLstReturnList;
	}

	public List getBranchNamesWithBsrCodes(Long lLngBankCode) throws Exception {

		List<Object> lLstReturnList = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			
			//lSBQuery.append("SELECT RB.bsrCode, RB.branchName from RltBankBranchPay RB ");
			//lSBQuery.append("WHERE RB.bankCode = :bankCode order by RB.branchName,RB.bsrCode ");
			
			//SQL Query used.
			
			lSBQuery.append(" select trim(BSR_CODE),branch_name from RLT_BANK_BRANCH_PAY");//modified by sunitha
			lSBQuery.append(" where BANK_CODE = :bankCode and activate_Flag = '1' "); //changed by sid ,added activateFlag = '1'
			lSBQuery.append(" order by branch_name ,bsr_code");

			
			Query lObjQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lObjQuery.setParameter("bankCode", lLngBankCode);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			lLstReturnList = new ArrayList<Object>();

			if (lLstResult != null && lLstResult.size() != 0) {
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {

				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			//e.printStackTrace();
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
			//e.printStackTrace();
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

			strQuery.append(" SELECT stateId,stateName ");
			strQuery.append(" FROM CmnStateMst ");
			strQuery.append(" WHERE cmnLanguageMst.langId =:langId order by stateName,stateId");

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
			//e.printStackTrace();
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
				
				lSBQuery.append(" Select districtId,districtName ");
				lSBQuery.append(" FROM CmnDistrictMst ");

				lSBQuery.append(" WHERE cmnStateMst.stateId =:stateId and cmnLanguageMst.langId = 1 and activateFlag = 1 ");

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
			//e.printStackTrace();
			gLogger.error("Error is : " + e, e);
			throw e;
		}

		return lLstTaluka;

	}

	public String getDdoCodeForDDO(Long lLngPostId) {

		String lStrDdoCode = null;
		List lLstDdoDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();

			if(lLstDdoDtls != null)
			{
				if(lLstDdoDtls.size()!= 0)
				{
					if(lLstDdoDtls.get(0) != null)
					{
						lStrDdoCode = lLstDdoDtls.get(0).toString();
					}
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
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
			.append(" WHERE OM.officeId = OD.officeId AND OM.officeName = :officeName order by OD.dsgnName,OD.dsgnId");
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

	public List getCurrentOffices(String lStrDdoCode) {

		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		String query = "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice where dcpsDdoCode= :ddoCode";

		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
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

	public List getAllOffices() {

		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		String query = "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice ";

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

	public String getCmnLookupNameFromId(Long lookupId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String lookupName;

		lSBQuery
				.append(" select lookupName FROM CmnLookupMst WHERE lookupId = :lookupId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lookupId", lookupId);

		tempList = lQuery.list();
		lookupName = tempList.get(0);
		return lookupName;

	}

	public String getDesigNameFromId(Long lookupId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String lookupName;

		lSBQuery
				.append(" select dsgnName FROM OrgDesignationMst WHERE dsgnId = :dsgnId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dsgnId", lookupId);

		tempList = lQuery.list();
		lookupName = tempList.get(0);
		return lookupName;

	}

	public OrgDdoMst getDDOInfoVOForDDOCode(String ddoCode) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM OrgDdoMst");
		lSBQuery.append(" WHERE ddoCode = :ddoCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", ddoCode);

		OrgDdoMst lObjDcpsDdoInfo = (OrgDdoMst) lQuery.uniqueResult();

		return lObjDcpsDdoInfo;
	}

	public String getDdoNameForCode(String lStrDdoCode) {

		String lStrDdoName = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
				.append(" SELECT ddoName from OrgDdoMst WHERE ddoCode =  :DdoCode ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("DdoCode", lStrDdoCode);

		List lLstCodeList = lQuery.list();
		lStrDdoName = lLstCodeList.get(0).toString();

		return lStrDdoName;
	}

	public DdoOffice getDdoMainOffice(String lStrDdoCode) {

		DdoOffice lObjDdoMainOffice = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
				.append(" from DdoOffice WHERE dcpsDdoCode =  :dcpsDdoCode and dcpsDdoOfficeDdoFlag = 'Yes' ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsDdoCode", lStrDdoCode);

		List<DdoOffice> lLstOfficeList = lQuery.list();
		
		if(lLstOfficeList != null)
		{
			if(lLstOfficeList.size() != 0)
			{
				lObjDdoMainOffice = lLstOfficeList.get(0);
			}
		}

		return lObjDdoMainOffice;
	}

	public OrgDdoMst getDdoVOForDdoCode(String ddoCode) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;

		lSBQuery.append("FROM OrgDdoMst");
		lSBQuery.append(" WHERE ddoCode = :ddoCode ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", ddoCode);

		OrgDdoMst lObjOrgDdoMst = (OrgDdoMst) lQuery.uniqueResult();

		return lObjOrgDdoMst;
	}

	public String getTreasuryNameForDDO(String lStrDdoCode) {

		String lStrTreasuryName = null;
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT LM.locName FROM RltDdoOrg RO, CmnLocationMst LM ");
		sb
				.append("WHERE RO.ddoCode = :ddoCode AND	LM.locationCode = RO.locationCode");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
		lStrTreasuryName = selectQuery.list().get(0).toString();
		return lStrTreasuryName;
	}

	public String getTreasuryShortNameForDDO(String lStrDdoCode) {

		String lStrTreasuryName = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT LM.locShortName FROM RltDdoOrg RO, CmnLocationMst LM ");
		sb
				.append("WHERE RO.ddoCode = :ddoCode AND	LM.locationCode = RO.locationCode");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
		lStrTreasuryName = selectQuery.list().get(0).toString();
		return lStrTreasuryName;
	}

	public List getParentDeptForDDO(String lStrDdoCode) {

		List lStrParentDept = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT CM.locId,CM.locName FROM OrgDdoMst DM, CmnLocationMst CM ");
		sb.append("WHERE DM.ddoCode = :ddoCode AND	CM.locId = DM.hodLocCode");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
		lStrParentDept = selectQuery.list();
		return lStrParentDept;
	}

	public String getTreasuryCodeForDDO(String lStrDdoCode) {

		String lStrTreasuryName = null;
		StringBuilder sb = new StringBuilder();
		List tempList = null;

		sb.append("SELECT LM.locationCode FROM RltDdoOrg RO, CmnLocationMst LM ");
		sb.append("WHERE RO.ddoCode = :ddoCode AND	LM.locationCode = RO.locationCode");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
		
		tempList = selectQuery.list();
		if(tempList != null)
		{
			if(tempList.get(0) != null)
			{
				lStrTreasuryName = selectQuery.list().get(0).toString();
			}
		}
		return lStrTreasuryName;
	}

	public String getTreasuryCityForDDO(String lStrDdoCode) {

		String lStrCityName = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT CM.cityName FROM RltDdoOrg RO, CmnLocationMst LM ,CmnCityMst CM ");
		sb
				.append("WHERE RO.ddoCode = :ddoCode AND LM.locationCode = RO.locationCode AND LM.locCityId = CM.cityId ");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
		if(selectQuery.list() != null)
		{
			if(selectQuery.list().size() != 0)
			{
				if(selectQuery.list().get(0) != null)
				{
					lStrCityName = selectQuery.list().get(0).toString();
				}
			}
		}
		return lStrCityName;
	}

	public List getCadreForDept(Long lLngDeptCode) throws Exception {

		List<Object> lLstReturnList = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT CM.cadreId, CM.cadreName from DcpsCadreMst CM ");
			lSBQuery.append("WHERE CM.fieldDeptId = :fieldDeptId ");

			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());

			lObjQuery.setParameter("fieldDeptId", lLngDeptCode);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
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
			//e.printStackTrace();
			throw e;
		}
		return lLstReturnList;
	}

	public List getDesigsForPFDAndCadre(Long fieldDeptId) throws Exception {

		List<Object> lLstReturnList = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append(" SELECT ODM.dsgnId,ODM.dsgnName FROM OrgDesignationMst ODM, MstDcpsDesignation MDD ");
			lSBQuery
			.append(" WHERE ODM.dsgnId = MDD.orgDesignationId AND MDD.fieldDeptId = :fieldDeptId order by ODM.dsgnName,ODM.dsgnId");

			Session lObjSession = getReadOnlySession();
			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());

			lObjQuery.setParameter("fieldDeptId", fieldDeptId);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
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
			//e.printStackTrace();
			throw e;
		}
		return lLstReturnList;
	}

	public List getLookupValuesForParent(Long lLngParentLookupId)
			throws Exception {

		List<Object> lLstReturnList = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery
					.append(" SELECT lookupId,lookupName FROM CmnLookupMst where parentLookupId = :parentLookupId");

			Session lObjSession = getReadOnlySession();
			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());

			lObjQuery.setParameter("parentLookupId", lLngParentLookupId);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstReturnList.add(lObjComboValuesVO);
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
			//e.printStackTrace();
			throw e;
		}
		return lLstReturnList;
	}

	public List getDeptNameFromDdoCode(String lStrDdoCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT lm.locationCode,lm.locName FROM CmnLocationMst lm,OrgDdoMst dm ");
		sb
				.append("WHERE dm.ddoCode = :ddoCode AND dm.deptLocCode = lm.locationCode");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode);
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

	public String getLocNameforLocId(Long locId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList<String>();
		String locName = null;

		lSBQuery
				.append(" Select locName FROM CmnLocationMst WHERE locId = :locId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("locId", locId);

		tempList = lQuery.list();
		if (tempList != null && tempList.size() != 0) {
			locName = tempList.get(0);
		}
		return locName;
	}

	public String getCadreNameforCadreId(Long cadreId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList<String>();
		String cadreName = null;

		lSBQuery
				.append(" Select cadreName FROM DcpsCadreMst WHERE cadreId = :cadreId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("cadreId", cadreId);

		tempList = lQuery.list();
		if (tempList != null && tempList.size() != 0) {
			cadreName = tempList.get(0);
		}
		return cadreName;
	}
	
	public Long getCadreCodeforCadreId(Long cadreId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList<Long>();
		Long cadreCode = null;

		lSBQuery.append(" Select cadreCode FROM DcpsCadreMst WHERE cadreId = :cadreId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("cadreId", cadreId);

		tempList = lQuery.list();
		if (tempList != null && tempList.size() != 0) {
			if(tempList.get(0) != null)
			{
				if(!tempList.get(0).toString().equals(""))
				{
					cadreCode = Long.valueOf(tempList.get(0).toString());
				}
			}
		}
		return cadreCode;
	}
	
	public Long getCadreIdforCadreCodeAndFieldDept(Long cadreCode,Long fieldDeptId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList<Long>();
		Long cadreId = null;

		lSBQuery.append(" Select cadreId FROM DcpsCadreMst WHERE cadreCode = :cadreCode and fieldDeptId = :fieldDeptId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("cadreCode", cadreCode);
		lQuery.setParameter("fieldDeptId", fieldDeptId);

		tempList = lQuery.list();
		if (tempList != null && tempList.size() != 0) {
			if(tempList.get(0) != null)
			{
				if(!tempList.get(0).toString().equals(""))
				{
					cadreId = Long.valueOf(tempList.get(0).toString());
				}
			}
		}
		return cadreId;
	}

	public String getGroupIdforCadreId(Long cadreId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList<String>();
		String groupId = null;

		lSBQuery
				.append(" Select groupId FROM DcpsCadreMst WHERE cadreId = :cadreId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("cadreId", cadreId);

		tempList = lQuery.list();
		if (tempList != null && tempList.size() != 0) {
			groupId = tempList.get(0);
		}
		return groupId;
	}

	public String getDddoOfficeNameNameforId(Long dcpsDdoOfficeIdPk) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList<String>();
		String ddoOfficeName = null;

		lSBQuery
				.append(" Select dcpsDdoOfficeName FROM DdoOffice WHERE dcpsDdoOfficeIdPk = :dcpsDdoOfficeIdPk");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsDdoOfficeIdPk", dcpsDdoOfficeIdPk);

		tempList = lQuery.list();
		if (tempList != null && tempList.size() != 0) {
			ddoOfficeName = tempList.get(0);
		}
		return ddoOfficeName;
	}

	public List getOfficesForPost(Long lLongPostId) throws Exception {

		List<Object> lLstReturnList = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT DO.dcpsDdoOfficeIdPk, DO.dcpsDdoOfficeName from DdoOffice DO, HrPayOfficepostMpg OP");
			lSBQuery
					.append(" WHERE DO.dcpsDdoOfficeIdPk = OP.ddoOffice.dcpsDdoOfficeIdPk and OP.orgPostMstByPostId.postId = :lLongPostId");

			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());

			lObjQuery.setParameter("lLongPostId", lLongPostId);

			List lLstResult = lObjQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();

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
			//e.printStackTrace();
			throw e;
		}
		return lLstReturnList;
	}

	public List<ComboValuesVO> getAllOrgType() throws Exception {

		ArrayList<ComboValuesVO> lArrLstDepartnent = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		ComboValuesVO cmbVO;
		List lLstResultList;
		Iterator itr;
		Object[] obj;

		try {

			lStrQuery
					.append(" SELECT orgId,orgDesc FROM MstDcpsOrganization MDO order by MDO.orgType ASC");

			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());

			lLstResultList = hqlQuery.list();
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

	public MstEmp getEmpVOForEmpId(Long dcpsEmpId) throws Exception {

		StringBuilder lStrQuery = new StringBuilder();
		MstEmp lObjMstEmp = null;
		try {

			lStrQuery.append(" FROM MstEmp WHERE dcpsEmpId=:dcpsEmpId");

			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setParameter("dcpsEmpId", dcpsEmpId);
			lObjMstEmp = (MstEmp) hqlQuery.uniqueResult();
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lObjMstEmp;
	}

	public List getDatesFromFinYearId(Long yearId) throws Exception {
		List lstDates = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT fromDate,toDate FROM SgvcFinYearMst WHERE finYearId = :yearId");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("yearId", yearId);
			lstDates = hqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lstDates;
	}

	public String getCurrentInterestRate() {

		String interestRate = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT interest FROM InterestRate WHERE status=1");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			interestRate = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return interestRate;
	}

	public String getFinYearForYearId(Long yearId) {

		String lStrYearDesc = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT finYearDesc FROM SgvcFinYearMst WHERE finYearId = :yearId");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("yearId", yearId);
			lStrYearDesc = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lStrYearDesc;
	}

	public Long getFinYearIdForYearCode(String yearCode) {

		Long lLongYearId = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;

		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT finYearId FROM SgvcFinYearMst WHERE finYearCode = :finYearCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("finYearCode", yearCode);

			if (hqlQuery.list().get(0) != null) {
				lLongYearId = Long.valueOf(hqlQuery.list().get(0).toString());
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lLongYearId;
	}

	public Float getCurrentDARate(String payComm) {

		Float daRate = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT daRate FROM DARate WHERE status=1 AND payCommission = :payComm");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("payComm", payComm);
			daRate = (Float) hqlQuery.list().get(0);

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return daRate;
	}

	public String getTreasuryNameForTreasuryId(Long treasuryId) {

		String lStrYearDesc = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT locName FROM CmnLocationMst WHERE locationCode = :treasuryCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("treasuryCode", treasuryId.toString());
			lStrYearDesc = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lStrYearDesc;
	}

	public String getBankNameForBankCode(String bankCode) {

		String lStrBankName = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;

		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT bankName FROM MstBankPay WHERE bankCode = :bankCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("bankCode", bankCode);
			lStrBankName = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);
		}
		return lStrBankName;
	}

	public String getBranchNameForBranchCode(String branchId) {

		String lStrBranchName = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;

		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT branchName FROM RltBankBranchPay WHERE branchId = :branchId");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("branchId", Long.valueOf(branchId));
			if (hqlQuery.list().size() == 0) {
				lStrBranchName = "";
			} else {
				lStrBranchName = hqlQuery.list().get(0).toString();
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);
		}
		return lStrBranchName;
	}

	public List getAllDDOForTreasury(String lStrTreasuryLocCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
		sb
				.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode");
		sb.append(" order by DM.ddoName");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc("(" + obj[0].toString() + ") " + obj[1].toString());
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

	public Boolean checkPFDForDDO(String lStrDdoCode) {
		Boolean Status = false;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List<Boolean> lLstDdo = null;

		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT adminFlag FROM OrgDdoMst WHERE ddoCode = :ddoCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("ddoCode", lStrDdoCode);
			lLstDdo = hqlQuery.list();

			if (lLstDdo.size() > 0) {
				Status = lLstDdo.get(0);
			}

			if (Status == true) {
				lSBQuery = new StringBuilder();
				lSBQuery
						.append(" update OrgDdoMst set adminFlag = 0 WHERE ddoCode = :ddoCode");
				hqlQuery = ghibSession.createQuery(lSBQuery.toString());
				hqlQuery.setParameter("ddoCode", lStrDdoCode);
				hqlQuery.executeUpdate();
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);
		}
		return Status;
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
	
	public List getBillGroupsNotDeletedAndNotDCPS(String lStrDDOCode) throws Exception {

		String query = "select dcpsDdoBillGroupId,dcpsDdoBillDescription from MstDcpsBillGroup where dcpsDdoCode = :dcpsDdoCode";
		query = query + " and (billDeleted is null or billDeleted <> 'Y') and (billDcps is null or billDcps <> 'Y') ";
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

	public Long getDDOPostIdForDDOAsst(Long lLngPostId) {

		Long lLongDdoPostId = null;
		List lLstDdoDtls = null;

		try {
			getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT ddoPostId");
			lSBQuery.append(" FROM  RltDdoAsst");
			lSBQuery.append(" WHERE asstPostId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();

			if (lLstDdoDtls.size() != 0) {
				lLongDdoPostId = Long.valueOf(lLstDdoDtls.get(0).toString());
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);

		}
		return lLongDdoPostId;
	}

	public String getFinYearCodeForYearId(Long yearId) {

		String lStrYearCode = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;

		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT finYearCode FROM SgvcFinYearMst WHERE finYearId = :yearId");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("yearId", yearId);
			lStrYearCode = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lStrYearCode;
	}
	
	public String getFinYearDescForYearCode(String finYearCode) {

		String lStrYearCode = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;

		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT finYearDesc FROM SgvcFinYearMst WHERE finYearCode = :finYearCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("finYearCode", finYearCode);
			lStrYearCode = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lStrYearCode;
	}

	public String getFinYearIdForDate(Date FinDate) {

		String lStrYearId = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;

		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT finYearId FROM SgvcFinYearMst WHERE :finDate between fromDate and toDate");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("finDate", FinDate);
			lStrYearId = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lStrYearId;
	}
	
	public List getStates(Long langId) {

		String query = "select stateId,stateName from CmnStateMst where cmnCountryMst.countryId = 1 and cmnLanguageMst.langId = :langId";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setLong("langId", langId);
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
	
	public String getAdminBudgetCodeForDDO(String lStrDDOCode) throws Exception {

		String lStrAdmBudgetCode = "";
		List tempList = null;
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT AD.admBdgtCd FROM AdmDept AD,OrgDdoMst OD, CmnLocationMst CM");
			lSBQuery.append(" WHERE OD.deptLocCode = CM.locId ");
			lSBQuery.append(" AND CM.locId = AD.locId");
			lSBQuery.append(" AND OD.ddoCode = :ddoCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDDOCode.trim());
			
			tempList = lQuery.list();
			if(tempList != null)
			{	
				if(tempList.size() != 0)
				{
					if(tempList.get(0) != null)
					{
						lStrAdmBudgetCode = tempList.get(0).toString();
					}
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lStrAdmBudgetCode;
	}
	
	public Long getDDOAsstPostIdForDDO(String lStrDDOCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long lLongAsstPostId = 0L;

		lSBQuery.append(" SELECT min(RD.ASST_POST_ID) FROM rlt_dcps_ddo_asst RD join org_ddo_mst OD on OD.post_id = RD.ddo_post_id where OD.ddo_code = '"+lStrDDOCode+"'");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		lLongAsstPostId = Long.valueOf(tempList.get(0).toString());
		return lLongAsstPostId;

	}
	
	public Long getDDOPostIdForDDO(String lStrDDOCode) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long lLongDDOPostId = 0L;

		lSBQuery.append(" SELECT OD.post_id FROM org_ddo_mst OD where OD.ddo_code = '"+lStrDDOCode+"'");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		lLongDDOPostId = Long.valueOf(tempList.get(0).toString());
		return lLongDDOPostId;

	}
	
	public Long getUserIdForPostId(Long lLongPostId) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = new ArrayList();
		Long lLongAsstUserId = 0L;

		lSBQuery.append(" select user_id from org_userpost_rlt where post_id = " + lLongPostId + " and activate_flag = 1");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		tempList = lQuery.list();
		lLongAsstUserId = Long.valueOf(tempList.get(0).toString());
		return lLongAsstUserId;

	}
	
	public String getFieldDeptOfDDO(String lStrDdoCode) {

		String lStrFieldDept = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT OD.hodLocCode FROM OrgDdoMst OD ");
		sb.append(" WHERE OD.ddoCode = :ddoCode ");
		
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("ddoCode", lStrDdoCode.trim());
		lStrFieldDept = selectQuery.list().get(0).toString();
		return lStrFieldDept;
	}
	
	public List getAllAdminDeptsForReportIncludingAllDepts() throws Exception {

		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId = 100001 order by CM.locName";
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
		
		lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("AllDepts");
		lObjComboValuesVO.setDesc("All Departments");
		lLstReturnList.add(lObjComboValuesVO);

		return lLstReturnList;
	}
	

	public List getAllDDOInclAll(String lStrTreasuryLocCode) {

		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb
				.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
		sb
				.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
		sb.append(" order by DM.ddoName");
		Query selectQuery = ghibSession.createQuery(sb.toString());
		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
		List lLstResult = selectQuery.list();
		ComboValuesVO lObjComboValuesVO = null;
		if (lLstResult != null && lLstResult.size() != 0) {
			
			lLstReturnList = new ArrayList<Object>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("-- Select --");
			lLstReturnList.add(lObjComboValuesVO);
			
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("AllDDO");
			lObjComboValuesVO.setDesc("All DDOs");
			lLstReturnList.add(lObjComboValuesVO);
			
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				lObjComboValuesVO.setDesc("(" + obj[0].toString() + ") " + obj[1].toString());
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
		
			lLstReturnList = new ArrayList<Object>();
			
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("AllDDO");
			lObjComboValuesVO.setDesc("All DDOs");
			lLstReturnList.add(lObjComboValuesVO);
			
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}
		
		

		return lLstReturnList;
	}
	public String getFinYearIdForYearDesc(String yearDesc) {

		String lLongYearId = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;

		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT finYearId FROM SgvcFinYearMst WHERE finYearDesc = :yearDesc");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("yearDesc", yearDesc);

			if (hqlQuery.list().get(0) != null) {
				lLongYearId = hqlQuery.list().get(0).toString();
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error " + e);

		}
		return lLongYearId;
	}
	public String getMonthIdForName(String monthname) {

		String monthid = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		logger.info("monthname is in dao "+monthname);
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT monthId FROM SgvaMonthMst WHERE monthName = :monthname ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("monthname",monthname);
			if (hqlQuery.list().get(0) != null) {
			monthid = hqlQuery.list().get(0).toString();
			logger.info("monthid is in dao "+monthid);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.info("Error is " + e);

		}
		return monthid;
	}
	public List getAllTreasuriesForInterest() throws Exception {

		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId = 100003 and CM.locId not in (9991,2028914,2028915)  order by CM.locId";
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
				lObjComboValuesVO.setDesc(obj[0].toString() +"-"+ obj[1].toString());
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
	
	public List getAllTreasuriesAndSubTreasuriesForInterest() throws Exception {

		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId in (100003,100006) order by CM.locId";
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
				lObjComboValuesVO.setDesc(obj[0].toString() +"-"+ obj[1].toString());
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
	public String getAstDDONameForAstDDO(String empUserName,String strDdoCode) {
		Session hibSession = getSession();
		
		String finalCheckFlag=null;
		List lstFinalCheck=null;
		List chkEmpInSystem=null;
		StringBuffer sb= new StringBuffer();
		StringBuffer sb1= new StringBuffer();
		gLogger.info("empUserName: "+empUserName+" strDdoCode: "+strDdoCode);
		
		//to check emp in system
		sb1.append(" SELECT EMP_NAME FROM mst_dcps_emp where SEVARTH_ID='"+empUserName+"'  ");
		gLogger.info("Query to check emp in system:  " + sb1.toString());
		Query sqlQuery1 = hibSession.createSQLQuery(sb1.toString());
		chkEmpInSystem=sqlQuery1.list();
		
		
		//to check emp in system and with ddo
		sb.append(" SELECT EMP_NAME FROM mst_dcps_emp where SEVARTH_ID='"+empUserName+"'  ");
		sb.append(" and DDO_CODE = '"+strDdoCode+"' ");
		gLogger.info("Query to get emp name with ddo:  " + sb.toString());
		Query sqlQuery = hibSession.createSQLQuery(sb.toString());
		lstFinalCheck=sqlQuery.list();
		
		if(lstFinalCheck.size() > 0 && chkEmpInSystem.size() > 0)
		{
			finalCheckFlag=lstFinalCheck.get(0).toString();
		}
		else if (chkEmpInSystem.size() > 0)
		{
			finalCheckFlag="notWithDDO";
		}
		else 
		{
			finalCheckFlag="notInSystem";
		}
		return finalCheckFlag;
	}

	@Override
	public String validateEmpDobDojForResetPwd(String empUserName,
			String strDdoCode, String dob,String doj) {
		Session hibSession = getSession();
		
		String finalCheckFlag=null;
		List lstFinalCheck=null;
		StringBuffer sb= new StringBuffer();
		gLogger.info("empUserName: "+empUserName+" strDdoCode: "+strDdoCode);
		
		
		sb.append(" SELECT count(1) FROM mst_dcps_emp where SEVARTH_ID='"+empUserName+"'  ");
		sb.append(" and DDO_CODE = '"+strDdoCode+"' and DOB = '"+dob+"' and DOJ = '"+doj+"' ");
		gLogger.info("Query to get emp name:  " + sb.toString());
		Query sqlQuery1 = hibSession.createSQLQuery(sb.toString());
		lstFinalCheck=sqlQuery1.list();
		if(lstFinalCheck.size() > 0)
		{
			finalCheckFlag=lstFinalCheck.get(0).toString();
		}
		else 
		{
			finalCheckFlag="0";
		}
		return finalCheckFlag;
	}

	@Override
	public void UpdatePwd(String empUserName) {
		// TODO Auto-generated method stub
			Session hibSession = getSession();
			StringBuffer sb= new StringBuffer();
			gLogger.info("empUserName: "+empUserName);
			sb.append(" update ORG_USER_MST set PASSWORD = '0b76f0f411f6944f9d192da0fcbfb292' where USER_NAME = '"+empUserName+"' ");
			gLogger.info("Query to update password:  " + sb.toString());
			Query sqlQuery1 = hibSession.createSQLQuery(sb.toString());
			sqlQuery1.executeUpdate();
	}
	
	public List getReasonValues(Long parentLookupId) {

		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb.append("SELECT LOOKUP_ID,LOOKUP_NAME FROM CMN_LOOKUP_MST where PARENT_LOOKUP_ID="+parentLookupId+" and LOOKUP_ID <> 700053 ");		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	

		lLstReturnList = new ArrayList<ComboValuesVO>();

		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());

		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
		if (lLstResult != null && lLstResult.size() != 0) {
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				String desc="<![CDATA["+obj[1].toString()+"]]>";
				lObjComboValuesVO.setDesc(desc);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<ComboValuesVO>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}
	
	public List getDDOoficeDesgn(String ddoCode) {

		List lLstResult = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb.append(" SELECT org.DDO_OFFICE,desg.DESIG_DESC FROM ORG_DDO_MST org  ");
		sb.append(" inner join ORG_POST_DETAILS_RLT post on post.POST_ID = org.POST_ID ");	
		sb.append(" inner JOIN MST_PAYROLL_DESIGNATION desg on desg.ORG_DESIGNATION_ID = post.DSGN_ID and org.HOD_LOC_CODE=desg.FIELD_DEPT_ID where org.DDO_CODE="+ddoCode);		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
		gLogger.info("Query to get emp name with ddo:  " + sb.toString());
		 lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());	

		return lLstResult;
	}


	public List getDeptReasonValues(Long parentLookupId) {

		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb.append("SELECT LOOKUP_ID,LOOKUP_NAME FROM CMN_LOOKUP_MST where LOOKUP_ID="+parentLookupId);		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	

		lLstReturnList = new ArrayList<ComboValuesVO>();

		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());

		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
		if (lLstResult != null && lLstResult.size() != 0) {
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();				
				lObjComboValuesVO.setId(obj[0].toString());
				String desc="<![CDATA["+obj[1].toString()+"]]>";
				lObjComboValuesVO.setDesc(desc);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<ComboValuesVO>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}
	
	public String checkDDOCodePresent(String strDdoCode) {
		Session hibSession = getSession();
		
		String finalCheckFlag=null;
		List lstFinalCheck=null;
		List chkEmpInSystem=null;
		StringBuffer sb= new StringBuffer();	
		//to check emp in system and with ddo
		sb.append(" SELECT DDO_CODE FROM org_ddo_mst where ddo_code='"+strDdoCode+"'  ");		
		gLogger.info("Query to get emp name with ddo:  " + sb.toString());
		Query sqlQuery = hibSession.createSQLQuery(sb.toString());
		//lstFinalCheck=sqlQuery.list();
		
		if (sqlQuery.list()!=null && sqlQuery.list().size() > 0)
		{
			finalCheckFlag="YES";
		}
		else 
		{
			finalCheckFlag="NO";
		}
		return finalCheckFlag;
	}

	@Override
	public List getDDOoficeTreasury(String ddoCode) {

		List lLstResult = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb.append(" SELECT ofc.OFF_NAME,LM.loc_Name FROM Rlt_Ddo_Org RO inner join Cmn_Location_Mst LM  on	LM.location_Code = RO.location_Code  ");
		sb.append(" inner join MST_DCPS_DDO_OFFICE ofc on ofc.DDO_CODE = RO.DDO_CODE ");	
		sb.append(" WHERE RO.ddo_Code = "+ddoCode+" and ofc.DDO_OFFICE = 'Yes' ");		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
		gLogger.info("Query to get treasury office name with ddo:  " + sb.toString());
		 lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());	

		return lLstResult;
	}
	public List getNewTreasury(String ddoCode) {

		List lLstResult = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb.append(" SELECT LM.location_Code,LM.LOC_NAME FROM Rlt_Ddo_Org RO, Cmn_Location_Mst LM   ");
		sb.append(" WHERE RO.ddo_Code = "+ddoCode+" AND	LM.location_Code = RO.location_Code ");			
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
		gLogger.info("Query to getNewTreasury name with ddo:  " + sb.toString());
		 lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());	

		return lLstResult;
	}

	@Override
	public List<ComboValuesVO> getTreasuryList() {
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb.append("SELECT loc_id,loc_Name  FROM CMN_LOCATION_MST  where department_Id=100003 order by loc_Name");		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	

		lLstReturnList = new ArrayList<ComboValuesVO>();

		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());

		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
		if (lLstResult != null && lLstResult.size() != 0) {
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				String desc="<![CDATA["+obj[1].toString()+"]]>";
				lObjComboValuesVO.setDesc(desc);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<ComboValuesVO>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
	}

	@Override
	public List<ComboValuesVO> getSubTreasuryList(Long treasuryId) {
		List<ComboValuesVO> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where (department_id=100006 and PARENT_LOC_ID=:loc_id ) or LOC_ID= :loc_id order by loc_name ");
		gLogger.info("query to select sub treasury from treasury code:::" + sb);
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		gLogger.info("sql query created");
		selectQuery.setParameter("loc_id", treasuryId);	
		

		lLstReturnList = new ArrayList<ComboValuesVO>();

		List lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());

		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
		lObjComboValuesVO.setId("-1");
		lObjComboValuesVO.setDesc("--Select--");
		lLstReturnList.add(lObjComboValuesVO);
		if (lLstResult != null && lLstResult.size() != 0) {
			Object obj[];
			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
				obj = (Object[]) lLstResult.get(liCtr);
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId(obj[0].toString());
				String desc="<![CDATA["+obj[1].toString()+"]]>";
				lObjComboValuesVO.setDesc(desc);
				lLstReturnList.add(lObjComboValuesVO);
			}
		} else {
			lLstReturnList = new ArrayList<ComboValuesVO>();
			lObjComboValuesVO = new ComboValuesVO();
			lObjComboValuesVO.setId("-1");
			lObjComboValuesVO.setDesc("--Select--");
			lLstReturnList.add(lObjComboValuesVO);
		}

		return lLstReturnList;
		
	}

	@Override
	public List getDdoCodeForAutoCompleteTresury(String ddoCode, Long locId) {
		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;
		Session hibSession = getSession();
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		lSBQuery.append("select ddo_Code from rlt_ddo_org where ddo_Code LIKE :searchKey and location_code= "+locId+" order by ddo_Code");
		lQuery = hibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("searchKey", ddoCode + '%');
		
		List resultList = lQuery.list();
		
		if (resultList != null && !resultList.isEmpty()) {
			for(Integer lInt = 0;lInt<resultList.size();lInt++){
				cmbVO = new ComboValuesVO();			
				cmbVO.setId(resultList.get(lInt).toString());
				cmbVO.setDesc(resultList.get(lInt).toString());
				finalList.add(cmbVO);
			}
		}

		return finalList;
	}

	@Override
	public String getMaxDDOCode(String searchKey) {
		String lStrDdoCode = null;
		List lLstDdoDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select max(ddo_Code) from Org_Ddo_Mst where ddo_Code LIKE :searchKey ");			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("searchKey", searchKey + '%');

			lLstDdoDtls = lQuery.list();

			if(lLstDdoDtls != null)
			{
				if(lLstDdoDtls.size()!= 0)
				{
					if(lLstDdoDtls.get(0) != null)
					{
						lStrDdoCode = lLstDdoDtls.get(0).toString();
					}
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrDdoCode;
	}

	@Override
	public int updateDDOCode(String tableName, String oldDdoCode,
			String newDdoCode) {
Session hibSession = getSession();
		String locId=newDdoCode.substring(0,4);
		StringBuffer strQuery = new StringBuffer(); 
		if(tableName.equals("RLT_DDO_ORG"))
			strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"',location_code = "+locId+"  where ddo_code='"+oldDdoCode+"' ");
		else if(tableName.equals("TRN_DCPS_CONTRIBUTION"))
		strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"',TREASURY_CODE = "+locId+"  where ddo_code='"+oldDdoCode+"' ");
		else 
			strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"'  where ddo_code='"+oldDdoCode+"' ");
		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
		gLogger.info("Query to get treasury office name with ddo:  " +strQuery.toString());
int updateCount = updateQuery.executeUpdate();
updateCount=updateCount+1;
		return updateCount;
		
	}

	@Override
	public String getAstUsername(String oldDdoCode) {
		String astUsername = "";
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT usermst.USER_NAME FROM ORG_DDO_MST org inner join RLT_DCPS_DDO_ASST rlt on org.POST_ID = rlt.DDO_POST_ID ");
		sb.append(" inner join ORG_USERPOST_RLT userpost on userpost.POST_ID = rlt.ASST_POST_ID ");
		sb.append(" inner join ORG_USER_MST usermst on usermst.USER_ID = userpost.USER_ID ");
		sb.append(" where userpost.ACTIVATE_FLAG = 1 and org.DDO_CODE = '"+oldDdoCode+"' ");	
		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	
		if(selectQuery.list()!=null &&!selectQuery.list().isEmpty())
		astUsername = selectQuery.list().get(0).toString();
		return astUsername;
	}

	@Override
	public int updateAstUserName(String newASTuserName, String astUserName) {
Session hibSession = getSession();
		
		StringBuffer strQuery = new StringBuffer(); 
		strQuery.append("update org_user_mst set USER_NAME = '"+newASTuserName+"',UPDATED_DATE = sysdate where USER_NAME = '"+astUserName+"' ");
		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
		gLogger.info("Query to updateAstUserName for AST:  " +strQuery.toString());
int updateCount = updateQuery.executeUpdate();
		return updateCount;
	}

	@Override
	public String getDDOUsername(String oldDdoCode,String newDdoCode) {
		String ddoUsername = "";
		StringBuilder sb = new StringBuilder();
		Session hibSession = getSession();
		sb.append(" select user_name from org_user_mst where user_name = '"+oldDdoCode+"' ");		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		if(selectQuery.list()!=null &&!selectQuery.list().isEmpty())
		ddoUsername = selectQuery.list().get(0).toString();
		if(ddoUsername.equals(oldDdoCode)){
			StringBuilder sb1 = new StringBuilder();
			
			sb1.append(" update org_user_mst set user_name = '"+newDdoCode+"',updated_date = sysdate  where user_name = '"+oldDdoCode+"' ");
			Query updateQuery = hibSession.createSQLQuery(sb1.toString());
			gLogger.info("Query to updateAstUserName for AST:  " +sb1.toString());
	int updateCount = updateQuery.executeUpdate();
	return newDdoCode;
		}else
		return ddoUsername;
		
	}

	@Override
	public String getLocationCode(String oldDdoCode) {
		String locationCode = "";
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT LOCATION_CODE FROM ORG_DDO_MST  where DDO_CODE = '"+oldDdoCode+"' ");	
		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	
		if(selectQuery.list()!=null &&!selectQuery.list().isEmpty())
			locationCode = selectQuery.list().get(0).toString();
		return locationCode;
	}

	@Override
	public int updatenewTreasuryDDOCode(String tableName, String oldDdoCode,
			String newDdoCode,String locationCode,String ddoPostId,String asstDdoPostId) {
		Session hibSession = getSession();
		String locId=newDdoCode.substring(0,4);
		int updateCount=0;
		StringBuffer strQuery = new StringBuffer(); 
		if(tableName.equals("RLT_DDO_ORG"))
			strQuery.append("update "+tableName+" set location_code = "+locId+"  where ddo_code='"+newDdoCode+"' ");
		else if(tableName.equals("TRN_DCPS_CONTRIBUTION"))
		strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"',TREASURY_CODE = "+locId+"  where ddo_code='"+oldDdoCode+"' ");
		else if(tableName.equals("ORG_DDO_MST"))
			strQuery.append("update "+tableName+" set location_code = "+locationCode+",post_id= '"+ddoPostId+"' where ddo_code='"+newDdoCode+"' ");
		else if(tableName.equals("MST_DCPS_DDO_OFFICE"))
			strQuery.append("update "+tableName+" set LOC_ID = "+locationCode+"  where ddo_code='"+newDdoCode+"' ");
		else 
			strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"'  where ddo_code='"+oldDdoCode+"' ");
		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
		gLogger.info("Query to get treasury office name with ddo:  " +strQuery.toString());
 updateCount = updateQuery.executeUpdate();
updateCount=updateCount++;
gLogger.info("updateCount:  " +updateCount);
		return updateCount;
	}

	@Override
	public List getPostId(String oldDdoCode) {
		List lLstResult = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb.append(" SELECT rlt.DDO_POST_ID,rlt.ASST_POST_ID FROM org_ddo_mst org inner join RLT_DCPS_DDO_ASST rlt on org.POST_ID = rlt.DDO_POST_ID where org.DDO_CODE = "+ oldDdoCode);	
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
		gLogger.info("Query to get treasury office name with ddo:  " + sb.toString());
		 lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());	

		return lLstResult;
	}

	@Override
	public void updateOldDDOCodePostIdLocId(String oldDdoCode, String ddoPostId,String newDdoPostId) {
		Session hibSession = getSession();	
		StringBuffer strQuery = new StringBuffer(); 		
			strQuery.append("update ORG_DDO_MST set location_code = null ,post_id = null where ddo_code='"+oldDdoCode+"' ");	
		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
		gLogger.info("Query to get treasury office name with ddo:  " +strQuery.toString());
 updateQuery.executeUpdate();
	}

	@Override
	public List getnewDDOPostId(String newDdoCode) {
		List lLstResult = null;
		StringBuilder sb = new StringBuilder();

		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
		sb.append(" SELECT rlt.DDO_POST_ID,rlt.ASST_POST_ID FROM org_ddo_mst org inner join RLT_DCPS_DDO_ASST rlt on org.POST_ID = rlt.DDO_POST_ID where org.DDO_CODE = "+ newDdoCode);	
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
		gLogger.info("Query to get treasury office name with ddo:  " + sb.toString());
		 lLstResult = selectQuery.list();
		gLogger.info("list size:" +lLstResult.size());	

		return lLstResult;
	}

	@Override
	public void updatenewTreasuryAsstDDOPostId(String asstDdoPostId,
			String newAsstDdoPostId) {
		Session hibSession = getSession();	
		StringBuffer strQuery = new StringBuffer();
		strQuery.append("update org_userpost_rlt set post_id ='"+asstDdoPostId+"' where post_id='"+newAsstDdoPostId+"' ");
		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
		gLogger.info("Query to get treasury office name with ddo:  " +strQuery.toString());
 updateQuery.executeUpdate();
	}

	@Override
	public String checkAstUserName(String newASTuserName) {
Session hibSession = getSession();
		
		String finalCheckFlag=null;
		List lstFinalCheck=null;
		List chkEmpInSystem=null;
		StringBuffer sb= new StringBuffer();	
		//to check emp in system and with ddo
		sb.append(" SELECT user_name FROM org_user_mst where user_name='"+newASTuserName+"'  ");		
		gLogger.info("Query to get emp name with ddo:  " + sb.toString());
		Query sqlQuery = hibSession.createSQLQuery(sb.toString());
		//lstFinalCheck=sqlQuery.list();
		
		if (sqlQuery.list()!=null && sqlQuery.list().size() > 0)
		{
			finalCheckFlag="YES";
		}
		else 
		{
			finalCheckFlag="NO";
		}
		return finalCheckFlag;
	}

	@Override
	public String checkdeleteDDOcode() {
Session hibSession = getSession();
		
		String finalCheckFlag=null;
		List lstFinalCheck=null;
		List chkEmpInSystem=null;
		StringBuffer sb= new StringBuffer();	
		//to check emp in system and with ddo
		sb.append(" SELECT ddo_code FROM org_ddo_mst where ddo_code like '111111%'  ");		
		gLogger.info("Query to get ddo_code with ddo:  " + sb.toString());
		Query sqlQuery = hibSession.createSQLQuery(sb.toString());
		//lstFinalCheck=sqlQuery.list();
		
		if (sqlQuery.list()!=null && sqlQuery.list().size() > 0)
		{
			finalCheckFlag="YES";
		}
		else 
		{
			finalCheckFlag="NO";
		}
		return finalCheckFlag;
	}
	
	public List getYearForDCPSReports(String lStrLangId, String lStrLocId) {
		ArrayList<ComboValuesVO> arrYear = new ArrayList<ComboValuesVO>();
		Connection lCon = null;
		PreparedStatement lStmt = null;
		ResultSet lRs = null;

		String fin_year_id = null;
		String fin_name = null;
		try {

			lCon = DBConnection.getConnection();
			StringBuffer lsb = new StringBuffer();
			lsb = new StringBuffer(
					"select * from sgvc_fin_year_mst where lang_id ='"
							+ lStrLangId
							+ "'  and fin_year_code between '2007' and '2015' order by fin_year_code ");

			lStmt = lCon.prepareStatement(lsb.toString());
			lRs = lStmt.executeQuery();
			while (lRs.next()) {
				ComboValuesVO vo = new ComboValuesVO();
				fin_year_id = lRs.getString("fin_year_id");
				fin_name = lRs.getString("fin_year_desc");
				vo.setId(fin_year_id);
				vo.setDesc(fin_name);
				arrYear.add(vo);
			}

		} catch (Exception e) {
			gLogger.error("Exception is : " + e, e);
		} finally {
			try {
				if (lStmt != null) {
					lStmt.close();
				}
				if (lRs != null) {
					lRs.close();
				}
				if (lCon != null) {
					lCon.close();
				}

				lStmt = null;
				lRs = null;
				lCon = null;
			} catch (Exception e) {
				e.printStackTrace();
				gLogger.info("Sql Exception:" + e, e);
			}
		}
		return arrYear;
	}

	@Override
	public Long getdeleteDDOcode() {
		Long deleteDddocode = 0l;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT max(ddo_code) FROM org_ddo_mst where ddo_code like '111111%'  ");	
		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	
		if(selectQuery.list()!=null &&!selectQuery.list().isEmpty())
			deleteDddocode =Long.parseLong( selectQuery.list().get(0).toString());
		return deleteDddocode;
	}

	@Override
	public int updateNewDDOCodeDelete(String crontableName,
			Long newDDocodeDelete, String newDdoCode) {
		Session hibSession = getSession();		
		StringBuffer strQuery = new StringBuffer(); 		
			strQuery.append("update "+crontableName+" set ddo_code ='"+newDDocodeDelete+"'  where ddo_code='"+newDdoCode+"' ");
		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
		gLogger.info("Query to get treasury office name with new ddo:  " +updateQuery.toString());
int updateCount = updateQuery.executeUpdate();
updateCount=updateCount+1;
		return updateCount;
	}
	
//added by vamsi	
	public List getLevel()
	{
		List temp=null;
		List lLstReturnList=null;
		Session hibSession = getSession();
		StringBuilder SBQuery = new StringBuilder();
		try
		{		
			SBQuery.append("SELECT LEVEL_ID,LEVEL FROM RLT_PAYBAND_GP_7PC ORDER BY LEVEL_ID");
			Query sqlQuery= hibSession.createSQLQuery(SBQuery.toString());
			logger.error("sqlQuery Size"+sqlQuery.toString());
			temp= sqlQuery.list();
			
			ComboValuesVO lObjComboValuesVO = null;
			if (temp != null && temp.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();
				for (int liCtr = 0; liCtr < temp.size(); liCtr++) {
					obj = (Object[]) temp.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-----Select-----");
				lObjComboValuesVO.setDesc("-1");
				lLstReturnList.add(lObjComboValuesVO);
			}
			
			logger.error("List Size"+temp.size());
			
		}
		catch(Exception e){
			logger.error("Error in DcpsCommonDAOImpl \n " + e);
			e.printStackTrace();
		}
		return lLstReturnList;
	}
	
	//added by pooja 03-01-2019
	public List getStateLevel()
	{
		List temp=null;
		List lLstReturnList=null;
		Session hibSession = getSession();
		StringBuilder SBQuery = new StringBuilder();
		try
		{		
			SBQuery.append("SELECT LEVEL_ID,LEVEL FROM RLT_PAYBAND_GP_STATE_7PC ORDER BY LEVEL_ID");
			Query sqlQuery= hibSession.createSQLQuery(SBQuery.toString());
			logger.error("sqlQuery getStateLevel--"+sqlQuery.toString());
			temp= sqlQuery.list();
			
			ComboValuesVO lObjComboValuesVO = null;
			if (temp != null && temp.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();
				for (int liCtr = 0; liCtr < temp.size(); liCtr++) {
					obj = (Object[]) temp.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-----Select-----");
				lObjComboValuesVO.setDesc("-1");
				lLstReturnList.add(lObjComboValuesVO);
			}
			
			logger.error("List Size"+temp.size());
			
		}
		catch(Exception e){
			logger.error("Error in DcpsCommonDAOImpl \n " + e);
			e.printStackTrace();
		}
		return lLstReturnList;
	}
	
	public List getPIPBForSevenPayEmployee(String scaleId)
	{
		List lLstReturnList=null;
		Session hibSession = getSession();
		StringBuilder SBQuery = new StringBuilder();
		SBQuery.append(" SELECT SCALE_START_AMT,SCALE_END_AMT,SCALE_GRADE_PAY FROM HR_EIS_SCALE_MST WHERE SCALE_ID ='"+scaleId+"'");
        Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
        gLogger.info("query for getPIPBForSevenPayEmployee "+sqlQuery);
        lLstReturnList = sqlQuery.list();
		return lLstReturnList;
	}
	public String getLevelForPayBand(String payBand,String gradePay)
	{
		String x="";
		List lLstReturnList=null;
		Session hibSession = getSession();
		StringBuilder SBQuery = new StringBuilder();
		SBQuery.append(" SELECT LEVEL FROM RLT_PAYBAND_GP_7PC WHERE PAY_IN_PAYBAND ='"+payBand+"' AND GRADE_PAY ='"+gradePay+"'");
        Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
        gLogger.info("query for getPIPBForSevenPayEmployee "+sqlQuery);
        lLstReturnList = sqlQuery.list();
        if(lLstReturnList.size()>0 && lLstReturnList!=null){
    		x=(lLstReturnList.get(0).toString());
    		}
		return x;
	}
	//added by pooja - 03-01-2019
	public String getStateLevelForPayBand(String payBand,String gradePay)
	{
		String x="";
		List lLstReturnList=null;
		Session hibSession = getSession();
		StringBuilder SBQuery = new StringBuilder();
		SBQuery.append(" SELECT LEVEL FROM RLT_PAYBAND_GP_STATE_7PC WHERE PAY_IN_PAYBAND ='"+payBand+"' AND GRADE_PAY ='"+gradePay+"'");
        Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
        gLogger.info("query for getStateLevelForPayBand "+sqlQuery);
        lLstReturnList = sqlQuery.list();
        if(lLstReturnList.size()>0 && lLstReturnList!=null){
    		x=(lLstReturnList.get(0).toString());
    		}
		return x;
	}
	public int getLevelIdForGivenLevel(String sevenLevel)
	{
		int x=0;
		List lLstReturnList=null;
		Session hibSession = getSession();
		StringBuilder SBQuery = new StringBuilder();
		SBQuery.append(" SELECT LEVEL_ID FROM RLT_PAYBAND_GP_7PC WHERE LEVEL = '"+sevenLevel+"'");
        Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
        gLogger.info("query for getPIPBForSevenPayEmployee "+sqlQuery);
        lLstReturnList = sqlQuery.list();
        if(lLstReturnList.size()>0 && lLstReturnList!=null){
    		x=Integer.parseInt(lLstReturnList.get(0).toString());
    		}
		return x;
	}
	//added by pooja 03-01-2019
	public int getStateLevelIdForGivenLevel(String sevenLevel)
	{
		int x=0;
		List lLstReturnList=null;
		Session hibSession = getSession();
		StringBuilder SBQuery = new StringBuilder();
		SBQuery.append(" SELECT LEVEL_ID FROM RLT_PAYBAND_GP_STATE_7PC WHERE LEVEL = '"+sevenLevel+"'");
        Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
        gLogger.info("query for getStateLevelIdForGivenLevel "+sqlQuery);
        lLstReturnList = sqlQuery.list();
        if(lLstReturnList.size()>0 && lLstReturnList!=null){
    		x=Integer.parseInt(lLstReturnList.get(0).toString());
    		}
		return x;
	}
	//added by pooja -12-10-2019 Change paypost deatils
	public List getNEwMEDHTEBasicAsPerMAtrixForBunchPayPost(String gradeId,String tableName,String resStr) {
		List temp=null;
		List lLstReturnList=null;
		Session session = getSession();
		
		//hibSession = getSession();
		try
		{		
			String branchQuery = "SELECT "+gradeId+",cell FROM "+tableName+" where   "+gradeId+" > 0 order by "+resStr+"";
			Query sqlQuery= session.createSQLQuery(branchQuery);
			logger.error("sqlQuery getNEwStateBasicAsPerMAtrixForBunchPayPost"+sqlQuery.toString());
			temp= sqlQuery.list();
			
			ComboValuesVO lObjComboValuesVO = null;
			if (temp != null && temp.size() != 0) {
				lLstReturnList = new ArrayList<Object>();
				Object obj[];
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				//lObjComboValuesVO.setId("----Select-----");
			//	lObjComboValuesVO.setDesc("-1");
			//	lLstReturnList.add(lObjComboValuesVO);
				for (int liCtr = 0; liCtr < temp.size(); liCtr++) {
					obj = (Object[]) temp.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[1].toString());
					lObjComboValuesVO.setDesc(obj[0].toString());
					lLstReturnList.add(lObjComboValuesVO);
				}
			} else {
				lLstReturnList = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-----Select-----");
				lObjComboValuesVO.setDesc("-1");
				lLstReturnList.add(lObjComboValuesVO);
			}
			
			logger.error("List Size"+temp.size());
			
		}
		catch(Exception e){
			logger.error("Error in ZpAdminOfficeMstDAOImpl \n " + e);
			e.printStackTrace();
		}
		return lLstReturnList;
	} 	
	public int getHTEMEDLevelIdForGivenLevel(String sevenLevel,String tableName)
	{
		int x=0;
		List lLstReturnList=null;
		Session hibSession = getSession();
		StringBuilder SBQuery = new StringBuilder();
		SBQuery.append(" SELECT LEVEL_ID FROM "+tableName+" WHERE LEVEL = '"+sevenLevel+"'");
        Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
        gLogger.info("query for getStateLevelIdForGivenLevel "+sqlQuery);
        lLstReturnList = sqlQuery.list();
        if(lLstReturnList.size()>0 && lLstReturnList!=null){
    		x=Integer.parseInt(lLstReturnList.get(0).toString());
    		}
		return x;
	}
	
	//added by pooja 12-10-2019
		public List getHTEMEDLevel(String tableName)
		{
			List temp=null;
			List lLstReturnList=null;
			Session hibSession = getSession();
			StringBuilder SBQuery = new StringBuilder();
			try
			{		
				SBQuery.append("SELECT LEVEL_ID,LEVEL FROM "+tableName+" ORDER BY LEVEL_ID");
				Query sqlQuery= hibSession.createSQLQuery(SBQuery.toString());
				logger.error("sqlQuery getStateLevel--"+sqlQuery.toString());
				temp= sqlQuery.list();
				
				ComboValuesVO lObjComboValuesVO = null;
				if (temp != null && temp.size() != 0) {
					lLstReturnList = new ArrayList<Object>();
					Object obj[];
					lLstReturnList = new ArrayList<Object>();
					for (int liCtr = 0; liCtr < temp.size(); liCtr++) {
						obj = (Object[]) temp.get(liCtr);
						lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(obj[0].toString());
						lObjComboValuesVO.setDesc(obj[1].toString());
						lLstReturnList.add(lObjComboValuesVO);
					}
				} else {
					lLstReturnList = new ArrayList<Object>();
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("-----Select-----");
					lObjComboValuesVO.setDesc("-1");
					lLstReturnList.add(lObjComboValuesVO);
				}
				
				logger.error("List Size"+temp.size());
				
			}
			catch(Exception e){
				logger.error("Error in DcpsCommonDAOImpl \n " + e);
				e.printStackTrace();
			}
			return lLstReturnList;
		}
		
		public String getHTEDMELevelForPayBand(String payBand,String gradePay,String tableName)
		{
			String x="";
			List lLstReturnList=null;
			Session hibSession = getSession();
			StringBuilder SBQuery = new StringBuilder();
			SBQuery.append(" SELECT LEVEL FROM "+tableName+" WHERE PAY_IN_PAYBAND ='"+payBand+"' AND GRADE_PAY ='"+gradePay+"'");
	        Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
	        gLogger.info("query for getStateLevelForPayBand "+sqlQuery);
	        lLstReturnList = sqlQuery.list();
	        if(lLstReturnList.size()>0 && lLstReturnList!=null){
	    		x=(lLstReturnList.get(0).toString());
	    		}
			return x;
		}
		//Added by Sumit DCPS
		 public List getMonths1() {
		        final String query = "select monthId,monthName from SgvaMonthMst where monthId between  '4' and '12'";
		        List<Object> lLstReturnList = null;
		        final StringBuilder sb = new StringBuilder();
		        sb.append(query);
		        final Query selectQuery = this.ghibSession.createQuery(sb.toString());
		        final List lLstResult = selectQuery.list();
		        ComboValuesVO lObjComboValuesVO = null;
		        if (lLstResult != null && lLstResult.size() != 0) {
		            lLstReturnList = new ArrayList<Object>();
		            for (int liCtr = 0; liCtr < lLstResult.size(); ++liCtr) {
		                final Object[] obj = (Object[]) lLstResult.get(liCtr);
		                lObjComboValuesVO = new ComboValuesVO();
		                lObjComboValuesVO.setId(obj[0].toString());
		                lObjComboValuesVO.setDesc(obj[1].toString());
		                lLstReturnList.add(lObjComboValuesVO);
		            }
		        }
		        else {
		            lLstReturnList = new ArrayList<Object>();
		            lObjComboValuesVO = new ComboValuesVO();
		            lObjComboValuesVO.setId("-1");
		            lObjComboValuesVO.setDesc("--Select--");
		            lLstReturnList.add(lObjComboValuesVO);
		        }
		        return lLstReturnList;
		    }

		 public List getFinyears() {
			 Calendar c = Calendar.getInstance();
			    int year = c.get(Calendar.YEAR);
			    int month = c.get(Calendar.MONTH);
			    if(4>month+1){
			        year = year-1;
			    }
				String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '"+year+"' order by finYearCode ASC";
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
			 
		//add by uday
			public List getYears1() {
				Calendar c = Calendar.getInstance();
			    int year = c.get(Calendar.YEAR);
			    int month = c.get(Calendar.MONTH);
			    if(4>month+1){
			        year = year-1;
			    }
				//String query = "select lookupId,lookupName from CmnLookupMst where lookupId in (10001198148,10001198404,10001198405)";
				String query = "select finYearId,finYearCode from SgvcFinYearMst where finYearCode between '2012' and '"+year+"' order by finYearCode ASC";
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
			
			public List getAllDDO(String LocId) {

				String query = "select  DM.DDO_CODE ||' '||DM.DDO_NAME,DM.DDO_CODE  from RLT_DDO_ORG  RO, ORG_DDO_MST DM,CMN_LOCATION_MST LM where RO.LOCATION_CODE= '"+LocId+"' and ro.DDO_CODE =DM.DDO_CODE and LM.LOC_ID =RO.LOCATION_CODE  order by DM.DDO_NAME asc";
				List<Object> lLstReturnList = null;
				StringBuilder sb = new StringBuilder();
				sb.append(query);
				Query selectQuery = ghibSession.createSQLQuery(sb.toString());
				List lLstResult = selectQuery.list();
				ComboValuesVO lObjComboValuesVO = null;

				if (lLstResult != null && lLstResult.size() != 0) {
					lLstReturnList = new ArrayList<Object>();
					Object obj[];
					for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
						obj = (Object[]) lLstResult.get(liCtr);
						lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(obj[1].toString());
						lObjComboValuesVO.setDesc(obj[0].toString());
						lLstReturnList.add(lObjComboValuesVO);
					}
				} else {
					/*lLstReturnList = new ArrayList<Object>();
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("-1");
					lObjComboValuesVO.setDesc("--Select--");
					lLstReturnList.add(lObjComboValuesVO);*/
				}
				return lLstReturnList;
			}
			
			public List getAllTreasury(String LocId,String role) {
				String query=null;
				if(role.equalsIgnoreCase("700003"))
				 query = " (select  '('||LOCATION_CODE ||') '|| LOC_NAME ,LOCATION_CODE  from CMN_LOCATION_MST where LOC_ID = '"+LocId+"' and DEPARTMENT_ID=100003 and LOC_ID not in (2028915) union all  "
				 		+ "select  '('||LOCATION_CODE ||') '|| LOC_NAME ,LOCATION_CODE  from CMN_LOCATION_MST where substr(LOC_ID,0,2) = substr('"+LocId+"',0,2) and DEPARTMENT_ID =100006 and LOC_ID not in (2028915) ) order by LOCATION_CODE ";
				else
			    query = " (select  '('||LOCATION_CODE ||') '|| LOC_NAME ,LOCATION_CODE  from CMN_LOCATION_MST where substr(LOC_ID,0,1) = substr('"+LocId+"',0,1) and DEPARTMENT_ID=100003 and LOC_ID not in (2028915) union all "
			    		+  "select  '('||LOCATION_CODE ||') '|| LOC_NAME ,LOCATION_CODE  from CMN_LOCATION_MST where substr(LOC_ID,0,1) = substr('"+LocId+"',0,1) and DEPARTMENT_ID =100006 and LOC_ID not in (2028915)) order by LOCATION_CODE "; 

			
				
				
				List<Object> lLstReturnList = null;
				StringBuilder sb = new StringBuilder();
				sb.append(query);
				Query selectQuery = ghibSession.createSQLQuery(sb.toString());
				List lLstResult = selectQuery.list();
				ComboValuesVO lObjComboValuesVO = null;

				if (lLstResult != null && lLstResult.size() != 0) {
					lLstReturnList = new ArrayList<Object>();
					Object obj[];
					for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
						obj = (Object[]) lLstResult.get(liCtr);
						lObjComboValuesVO = new ComboValuesVO();
						lObjComboValuesVO.setId(obj[1].toString());
						lObjComboValuesVO.setDesc(obj[0].toString());
						lLstReturnList.add(lObjComboValuesVO);
					}
				} else {
					/*lLstReturnList = new ArrayList<Object>();
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId("-1");
					lObjComboValuesVO.setDesc("--Select--");
					lLstReturnList.add(lObjComboValuesVO);*/
				}
				return lLstReturnList;
			}

			public MstEmp getEmployeeDetails(String lStrSevaarthId) {
			      StringBuilder lStrQuery = new StringBuilder();
			      MstEmp lObjMstEmp = null;

			      try {
			         lStrQuery.append(" FROM MstEmp WHERE sevarthId=:sevarthId");
			         Query hqlQuery = this.ghibSession.createQuery(lStrQuery.toString());
			         hqlQuery.setParameter("sevarthId", lStrSevaarthId);
			         lObjMstEmp = (MstEmp)hqlQuery.uniqueResult();
			         return lObjMstEmp;
			      } catch (Exception var5) {
			         this.logger.error("Error is :" + var5, var5);
			         throw var5;
			      }
			   }
}


////$t 18-1-2020 till date correct
///* Copyright TCS 2011, All Rights Reserved.
// * 
// * 
// ******************************************************************************
// ***********************Modification History***********************************
// *  Date   				Initials	     Version		Changes and additions
// ******************************************************************************
// * 	Apr 7, 2011		Vihan Khatri								
// *******************************************************************************
// */
//package com.tcs.sgv.dcps.service;
//
//import java.math.BigDecimal;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ResourceBundle;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//
//import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
//import com.tcs.sgv.common.util.DBConnection;
//import com.tcs.sgv.common.valueobject.CmnDistrictMst;
//import com.tcs.sgv.common.valueobject.OrgDdoMst;
//import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
//import com.tcs.sgv.dcps.valueobject.DdoOffice;
//import com.tcs.sgv.dcps.valueobject.MstEmp;
//import com.tcs.sgv.pensionproc.dao.PensionProcComparators;
//
///**
// * Class Description -
// * 
// * 
// * @author Vihan Khatri
// * @version 0.1
// * @since JDK 5.0 Apr 7, 2011
// */
//public class DcpsCommonDAOImpl extends GenericDaoHibernateImpl implements
//		DcpsCommonDAO {
//
//	private final Log gLogger = LogFactory.getLog(getClass());
//	Session ghibSession = null;
//
//	private final ResourceBundle gObjRsrcBndle = ResourceBundle
//			.getBundle("resources/pensionproc/PensionCaseConstants");
//
//	public DcpsCommonDAOImpl(Class type, SessionFactory sessionFactory) {
//
//		super(type);
//		/*ghibSession = sessionFactory.getCurrentSession();
//		setSessionFactory(sessionFactory);
//		*/
//		setSessionFactory(sessionFactory);
//		ghibSession = getSession();
//
//	}
//
//	public List getMonths() {
//
//		String query = "select monthId,monthName from SgvaMonthMst where monthId < 13";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);//(1,January)
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		return lLstReturnList;
//	}
// 
//	public List getYears() {
//
//		String query = "select finYearId,finYearCode from SgvcFinYearMst where finYearCode between '2007' and '2019'";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		return lLstReturnList;
//	}
//		
//	public List getFinyear() {
//
//		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '2014' order by finYearCode ASC";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		return lLstReturnList;
//	}
//
//	
//	public List getFinyearsForDelayedType() {
//
//		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2005' and '2019' order by finYearCode ASC";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		return lLstReturnList;
//	}
//	
//	public List getFinyearsAfterCurrYear() {
//
//		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '2019' order by finYearCode ASC";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		return lLstReturnList;
//	}
//
//	public String getDdoCode(Long lLngAsstPostId) {
//
//		String lStrDdoCode = null;
//		StringBuilder lSBQuery = new StringBuilder();
//		lSBQuery.append(" SELECT OD.ddoCode");
//		lSBQuery.append(" FROM RltDdoAsst RD, OrgDdoMst OD");
//		lSBQuery.append(" WHERE OD.postId = RD.ddoPostId AND RD.asstPostId = :asstPostId ");
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("asstPostId", lLngAsstPostId);
//
//		List lLstCodeList = lQuery.list();
//
//		if(lLstCodeList != null)
//		{
//			if(lLstCodeList.size() != 0)
//			{
//				if(lLstCodeList.get(0) != null)
//				{
//					lStrDdoCode = lLstCodeList.get(0).toString();
//				}
//			}
//		}
//
//		return lStrDdoCode;
//	}
//
//	public List<ComboValuesVO> getAllDesignation(Long lLngDeptId, Long langId)
//			throws Exception {
//
//		StringBuilder lStrQuery = new StringBuilder();
//		ArrayList<ComboValuesVO> lArrLstDesignation = new ArrayList<ComboValuesVO>();
//		List lLstResultList;
//		ComboValuesVO cmbVO;
//		Iterator itr;
//		Object[] obj;
//		try {
//
//			lStrQuery
//					.append(" SELECT ODM.dsgnId,ODM.dsgnName FROM OrgDesignationMst ODM, MstDcpsDesignation MDD ");
//			lStrQuery
//					.append(" WHERE ODM.dsgnId = MDD.orgDesignationId AND MDD.fieldDeptId = :fieldDeptId AND MDD.langId =:langId order by ODM.dsgnName,ODM.dsgnId");
//
//			// lStrQuery.append(" SELECT desigId,desigDesc FROM MstDcpsDesignation ");
//			// lStrQuery.append(" WHERE fieldDeptId = :fieldDeptId AND langId =:langId order by desigDesc,desigId");
//
//			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
//
//			hqlQuery.setLong("langId", langId);
//			hqlQuery.setLong("fieldDeptId", lLngDeptId);
//
//			lLstResultList = hqlQuery.list();
//			Collections.sort(lLstResultList,
//					new PensionProcComparators.ObjectArrayComparator(false, 1,
//							0, 2, 0, true));
//			if (lLstResultList != null && lLstResultList.size() > 0) {
//				itr = lLstResultList.iterator();
//				while (itr.hasNext()) {
//					cmbVO = new ComboValuesVO();
//					obj = (Object[]) itr.next();
//					cmbVO.setId(obj[0].toString());
//					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
//					lArrLstDesignation.add(cmbVO);
//				}
//			}
//
//		} catch (Exception e) {
//			logger.error("Error is :" + e, e);
//			//e.printStackTrace();
//		}
//		return lArrLstDesignation;
//	}
//
//	public List<ComboValuesVO> getAllDepartment(Long lLngDepartmentId,
//			Long langId) throws Exception {
//
//		ArrayList<ComboValuesVO> lArrLstDepartnent = new ArrayList<ComboValuesVO>();
//		StringBuilder lStrQuery = new StringBuilder();
//		ComboValuesVO cmbVO;
//		List lLstResultList;
//		Iterator itr;
//		Object[] obj;
//
//		try {
//
//			lStrQuery
//					.append(" SELECT clm.locId,clm.locName FROM CmnLocationMst clm, OrgDepartmentMst odm ");
//			lStrQuery.append(" WHERE odm.departmentId=:departmentId  ");
//			lStrQuery.append(" AND clm.departmentId=odm.departmentId ");
//			lStrQuery.append(" and clm.cmnLanguageMst.langId =:langId ");
//			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
//
//			hqlQuery.setLong("langId", langId);
//			hqlQuery.setLong("departmentId", lLngDepartmentId);
//			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
//			lLstResultList = hqlQuery.list();
//			Collections.sort(lLstResultList,
//					new PensionProcComparators.ObjectArrayComparator(false, 1,
//							0, 2, 0, true));
//			if (lLstResultList != null && lLstResultList.size() > 0) {
//				itr = lLstResultList.iterator();
//				while (itr.hasNext()) {
//					cmbVO = new ComboValuesVO();
//					obj = (Object[]) itr.next();
//					cmbVO.setId(obj[0].toString());
//					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
//					lArrLstDepartnent.add(cmbVO);
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Error is :" + e, e);
//			throw e;
//		}
//		return lArrLstDepartnent;
//	}
//
//	public List<ComboValuesVO> getAllHODDepartment(Long lLngDepartmentId,
//			Long langId) throws Exception {
//
//		ArrayList<ComboValuesVO> lArrLstDepartnent = new ArrayList<ComboValuesVO>();
//		StringBuilder lStrQuery = new StringBuilder();
//		ComboValuesVO cmbVO;
//		List lLstResultList;
//		Iterator itr;
//		Object[] obj;
//
//		try {
//
//			lStrQuery
//					.append(" SELECT clm.locId,clm.locName FROM CmnLocationMst clm, OrgDepartmentMst odm ");
//			lStrQuery.append(" WHERE odm.departmentId=:departmentId  ");
//			lStrQuery.append(" AND clm.departmentId=odm.departmentId ");
//			lStrQuery.append(" and clm.cmnLanguageMst.langId =:langId ");
//			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
//
//			hqlQuery.setLong("langId", langId);
//			hqlQuery.setLong("departmentId", lLngDepartmentId);
//			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
//			lLstResultList = hqlQuery.list();
//			Collections.sort(lLstResultList,
//					new PensionProcComparators.ObjectArrayComparator(false, 1,
//							0, 2, 0, true));
//			if (lLstResultList != null && lLstResultList.size() > 0) {
//				itr = lLstResultList.iterator();
//				while (itr.hasNext()) {
//					cmbVO = new ComboValuesVO();
//					obj = (Object[]) itr.next();
//					cmbVO.setId(obj[0].toString());
//					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
//					lArrLstDepartnent.add(cmbVO);
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Error is :" + e, e);
//			throw e;
//		}
//		return lArrLstDepartnent;
//	}
//
//	public List getAllTreasuries() throws Exception {
//
//		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId = 100003 order by CM.locName";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//	
//	public List getAllTreasuriesAndSubTreasuries() throws Exception {
//
//		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId in (100003,100006) order by CM.locName";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//
//	public List getBillGroups() throws Exception {
//
//		String query = "select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//
//	public Date getLastDate(Integer month, Integer year) {
//
//		Date date = new Date();
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month, 1);
//		Integer day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//
//		date.setMonth(month);
//		date.setYear(year - 1900);
//		date.setDate(day);
//
//		return date;
//	}
//
//	public Date getFirstDate(Integer month, Integer year) {
//
//		Date date = new Date();
//
//		date.setMonth(month);
//		date.setYear(year - 1900);
//		date.setDate(1);
//
//		return date;
//	}
//
//	public Object[] getSchemeNameFromBillGroup(Long billGroupId) {
//
//		getSession();
//
//		Object[] lObjArrSchemeNameAndCode = new Object[2];
//		StringBuilder lSBQuery = new StringBuilder();
//		List schemeList = new ArrayList();
//
//		lSBQuery
//				.append(" select dcpsDdoBillSchemeName,dcpsDdoSchemeCode FROM MstDcpsBillGroup WHERE dcpsDdoBillGroupId = :billGroupId");
//
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("billGroupId", billGroupId);
//
//		schemeList = lQuery.list();
//		lObjArrSchemeNameAndCode = (Object[]) schemeList.get(0);
//
//		return lObjArrSchemeNameAndCode;
//
//	}
//
//	public String getYearCodeForYearId(Long yearId) {
//
//		String lStrYearCode = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT finYearCode FROM SgvcFinYearMst WHERE finYearId = :yearId");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("yearId", yearId);
//			lStrYearCode = hqlQuery.list().get(0).toString();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger
//					.info("Error  while executing getCadreList of  DCPSCadreMasterDAOImpl is "
//							+ e);
//
//		}
//		return lStrYearCode;
//	}
//
//	public String getMonthForId(Long monthId) {
//
//		String lStrYearCode = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT monthName FROM SgvaMonthMst WHERE monthId = :monthId");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("monthId", BigDecimal.valueOf(monthId));
//			lStrYearCode = hqlQuery.list().get(0).toString();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error is " + e);
//
//		}
//		return lStrYearCode;
//	}
//
//	public List getCadres() {
//
//		String query = "select CM.cadreId,CM.cadreName FROM DcpsCadreMst CM";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		return lLstReturnList;
//	}
//
//	public List getBankNames() throws Exception {
//		//String query = "select MB.bankCode, MB.bankName from MstBankPay MB order by MB.bankName,MB.bankCode"; 
//		String query = "select MB.bankCode, MB.bankName from MstBankPay MB where MB.activateFlag = '1' order by MB.bankName,MB.bankCode";   // changed by sid, added MB.activateFlag = '1'
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		return lLstReturnList;
//	}
//
//	public List getBranchNames(Long lLngBankCode) throws Exception {
//
//		List<Object> lLstReturnList = null;
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//			lSBQuery
//			.append(" SELECT RB.branchId, RB.branchName from RltBankBranchPay RB ");
//			lSBQuery
//			.append(" WHERE RB.bankCode = :bankCodeVar and RB.activateFlag = '1' order by RB.branchName,RB.branchId "); //changed by sid, added RB.activateFlag = '1'
//
//			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());
//
//			lObjQuery.setParameter("bankCodeVar", lLngBankCode);
//
//			List lLstResult = lObjQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//			lLstReturnList = new ArrayList<Object>();
//
//			if (lLstResult != null && lLstResult.size() != 0) {
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//				Object obj[];
//				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//					obj = (Object[]) lLstResult.get(liCtr);
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId(obj[0].toString());
//					lObjComboValuesVO.setDesc(obj[1].toString());
//					lLstReturnList.add(lObjComboValuesVO);
//				}
//			} else {
//
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} catch (Exception e) {
//			gLogger.error("Error is : " + e, e);
//			//e.printStackTrace();
//			throw e;
//		}
//		return lLstReturnList;
//	}
//
//	public List getBranchNamesWithBsrCodes(Long lLngBankCode) throws Exception {
//
//		List<Object> lLstReturnList = null;
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//			
//			//lSBQuery.append("SELECT RB.bsrCode, RB.branchName from RltBankBranchPay RB ");
//			//lSBQuery.append("WHERE RB.bankCode = :bankCode order by RB.branchName,RB.bsrCode ");
//			
//			//SQL Query used.
//			
//			lSBQuery.append(" select trim(BSR_CODE),branch_name from RLT_BANK_BRANCH_PAY");//modified by sunitha
//			lSBQuery.append(" where BANK_CODE = :bankCode and activate_Flag = '1' "); //changed by sid ,added activateFlag = '1'
//			lSBQuery.append(" order by branch_name ,bsr_code");
//
//			
//			Query lObjQuery = ghibSession.createSQLQuery(lSBQuery.toString());
//
//			lObjQuery.setParameter("bankCode", lLngBankCode);
//
//			List lLstResult = lObjQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//			lLstReturnList = new ArrayList<Object>();
//
//			if (lLstResult != null && lLstResult.size() != 0) {
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//				Object obj[];
//				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//					obj = (Object[]) lLstResult.get(liCtr);
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId(obj[0].toString());
//					lObjComboValuesVO.setDesc(obj[1].toString());
//					lLstReturnList.add(lObjComboValuesVO);
//				}
//			} else {
//
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} catch (Exception e) {
//			gLogger.error("Error is : " + e, e);
//			//e.printStackTrace();
//			throw e;
//		}
//		return lLstReturnList;
//	}
//
//	public Long getIFSCCodeForBranch(Long branchName) throws Exception {
//
//		Long lLngHstDcpsID = null;
//		try {
//			getSession();
//			StringBuilder lSBQuery = new StringBuilder();
//			lSBQuery.append(" SELECT micrCode FROM  RltBankBranchPay");
//			lSBQuery.append(" WHERE branchId = :branchName ");
//			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//			lQuery.setParameter("branchName", branchName);
//			lLngHstDcpsID = (Long) lQuery.list().get(0);
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.error("Error is :" + e, e);
//		}
//		return lLngHstDcpsID;
//	}
//
//	public List getStateNames(Long langId) throws Exception {
//
//		ArrayList<ComboValuesVO> lstStates = new ArrayList<ComboValuesVO>();
//		List resultList;
//		ComboValuesVO cmbVO;
//		Object[] obj;
//		try {
//
//			StringBuilder strQuery = new StringBuilder();
//			/*
//			 * strQuery
//			 * .append("Select SM.stateId,SM.stateName from CmnStateMst SM");
//			 * Query query = ghibSession.createQuery(strQuery.toString());
//			 */
//
//			strQuery.append(" SELECT stateId,stateName ");
//			strQuery.append(" FROM CmnStateMst ");
//			strQuery.append(" WHERE cmnLanguageMst.langId =:langId order by stateName,stateId");
//
//			Query query = ghibSession.createQuery(strQuery.toString());
//
//			query.setParameter("langId", langId);
//
//			resultList = query.list();
//		
//			cmbVO = new ComboValuesVO();
//
//			if (resultList != null && resultList.size() > 0) {
//				Iterator it = resultList.iterator();
//				while (it.hasNext()) {
//					cmbVO = new ComboValuesVO();
//					obj = (Object[]) it.next();
//					cmbVO.setId(obj[0].toString());
//					cmbVO.setDesc(obj[1].toString());
//					lstStates.add(cmbVO);
//				}
//			}
//		} catch (Exception e) {
//			gLogger.error("Error is :" + e, e);
//			//e.printStackTrace();
//			throw e;
//		}
//		return lstStates;
//
//	}
//
//	public List getDistricts(Long lStrCurrState) throws Exception {
//
//		ArrayList<ComboValuesVO> lLstDistrict = new ArrayList<ComboValuesVO>();
//		Object[] obj;
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lStrCurrState != -1L) {
//
//			try {
//				StringBuilder lSBQuery = new StringBuilder();
//				
//				lSBQuery.append(" Select districtId,districtName ");
//				lSBQuery.append(" FROM CmnDistrictMst ");
//
//				lSBQuery.append(" WHERE cmnStateMst.stateId =:stateId and cmnLanguageMst.langId = 1 and activateFlag = 1 ");
//
//				Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());
//
//				lObjQuery.setParameter("stateId", lStrCurrState);
//
//				List lLstResult = lObjQuery.list();
//				
//				if (lLstResult != null && lLstResult.size() > 0) {
//					Iterator it = lLstResult.iterator();
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId("-1");
//					lObjComboValuesVO.setDesc("-- Select --");
//					lLstDistrict.add(lObjComboValuesVO);
//					while (it.hasNext()) {
//						lObjComboValuesVO = new ComboValuesVO();
//						obj = (Object[]) it.next();
//						lObjComboValuesVO.setId(obj[0].toString());
//						lObjComboValuesVO.setDesc(obj[1].toString());
//						lLstDistrict.add(lObjComboValuesVO);
//					}
//				}
//			} catch (Exception e) {
//				gLogger.error("Error is : " + e, e);
//				throw e;
//			}
//
//		} else {
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("-- Select --");
//			lLstDistrict.add(lObjComboValuesVO);
//		}
//
//		return lLstDistrict;
//
//	}
//
//	public List getTaluka(Long lStrCurrDst) throws Exception {
//
//		ArrayList<ComboValuesVO> lLstTaluka = new ArrayList<ComboValuesVO>();
//		Object[] obj;
//
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//
//			lSBQuery.append(" Select talukaId,talukaName ");
//			lSBQuery.append(" FROM CmnTalukaMst ");
//
//			lSBQuery.append(" WHERE cmnDistrictMst.districtId =:districtId ");
//
//			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());
//
//			lObjQuery.setParameter("districtId", lStrCurrDst);
//
//			List lLstResult = lObjQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//			if (lLstResult != null && lLstResult.size() > 0) {
//				Iterator it = lLstResult.iterator();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("-- Select --");
//				lLstTaluka.add(lObjComboValuesVO);
//				while (it.hasNext()) {
//					lObjComboValuesVO = new ComboValuesVO();
//					obj = (Object[]) it.next();
//					lObjComboValuesVO.setId(obj[0].toString());
//					lObjComboValuesVO.setDesc(obj[1].toString());
//					lLstTaluka.add(lObjComboValuesVO);
//				}
//			}
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.error("Error is : " + e, e);
//			throw e;
//		}
//
//		return lLstTaluka;
//
//	}
//
//	public String getDdoCodeForDDO(Long lLngPostId) {
//
//		String lStrDdoCode = null;
//		List lLstDdoDtls = null;
//
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//			lSBQuery.append(" SELECT OD.ddoCode");
//			lSBQuery.append(" FROM  OrgDdoMst OD");
//			lSBQuery.append(" WHERE OD.postId = :postId ");
//			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//			lQuery.setParameter("postId", lLngPostId);
//
//			lLstDdoDtls = lQuery.list();
//
//			if(lLstDdoDtls != null)
//			{
//				if(lLstDdoDtls.size()!= 0)
//				{
//					if(lLstDdoDtls.get(0) != null)
//					{
//						lStrDdoCode = lLstDdoDtls.get(0).toString();
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.error("Error is :" + e, e);
//
//		}
//		return lStrDdoCode;
//	}
//	
//	public List getDesignations(String lStrCurrOffice) throws Exception {
//
//		List<Object> lLstReturnList = null;
//
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//			lSBQuery
//					.append("SELECT OD.designationName FROM RltOfficeDesig OD,MstOffice OM ");
//			lSBQuery
//			.append(" WHERE OM.officeId = OD.officeId AND OM.officeName = :officeName order by ODM.dsgnName,ODM.dsgnId");
//			Session lObjSession = getReadOnlySession();
//			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());
//			lObjQuery.setParameter("officeName", lStrCurrOffice);
//
//			List lLstResult = lObjQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//			if (lLstResult != null && lLstResult.size() != 0) {
//				lLstReturnList = new ArrayList<Object>();
//
//				String lStrDesigName = null;
//				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//					lStrDesigName = (String) lLstResult.get(liCtr);
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId(lStrDesigName);
//					lObjComboValuesVO.setDesc(lStrDesigName);
//
//					lLstReturnList.add(lObjComboValuesVO);
//				}
//			} else {
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} catch (Exception e) {
//			gLogger.error("Error is : " + e, e);
//			throw e;
//		}
//
//		return lLstReturnList;
//
//	}
//
//	public List getCurrentOffices(String lStrDdoCode) {
//
//		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
//		ComboValuesVO cmbVO;
//		Object[] obj;
//
//		String query = "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice where dcpsDdoCode= :ddoCode";
//
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("ddoCode", lStrDdoCode);
//		List resultList = selectQuery.list();
//
//		cmbVO = new ComboValuesVO();
//
//		if (resultList != null && resultList.size() > 0) {
//			cmbVO = new ComboValuesVO();
//			cmbVO.setId("-1");
//			cmbVO.setDesc("-- Select --");
//			finalList.add(cmbVO);
//			Iterator it = resultList.iterator();
//			while (it.hasNext()) {
//				cmbVO = new ComboValuesVO();
//				obj = (Object[]) it.next();
//				cmbVO.setId(obj[0].toString());
//				cmbVO.setDesc(obj[1].toString());
//				finalList.add(cmbVO);
//			}
//		}
//
//		return finalList;
//	}
//
//	public List getAllOffices() {
//
//		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
//		ComboValuesVO cmbVO;
//		Object[] obj;
//
//		String query = "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice ";
//
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		List resultList = selectQuery.list();
//
//		cmbVO = new ComboValuesVO();
//
//		if (resultList != null && resultList.size() > 0) {
//			cmbVO = new ComboValuesVO();
//			cmbVO.setId("-1");
//			cmbVO.setDesc("-- Select --");
//			finalList.add(cmbVO);
//			Iterator it = resultList.iterator();
//			while (it.hasNext()) {
//				cmbVO = new ComboValuesVO();
//				obj = (Object[]) it.next();
//				cmbVO.setId(obj[0].toString());
//				cmbVO.setDesc(obj[1].toString());
//				finalList.add(cmbVO);
//			}
//		}
//
//		return finalList;
//	}
//
//	public String getCmnLookupNameFromId(Long lookupId) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List<String> tempList = new ArrayList();
//		String lookupName;
//
//		lSBQuery
//				.append(" select lookupName FROM CmnLookupMst WHERE lookupId = :lookupId");
//
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("lookupId", lookupId);
//
//		tempList = lQuery.list();
//		lookupName = tempList.get(0);
//		return lookupName;
//
//	}
//
//	public String getDesigNameFromId(Long lookupId) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List<String> tempList = new ArrayList();
//		String lookupName;
//
//		lSBQuery
//				.append(" select dsgnName FROM OrgDesignationMst WHERE dsgnId = :dsgnId");
//
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("dsgnId", lookupId);
//
//		tempList = lQuery.list();
//		lookupName = tempList.get(0);
//		return lookupName;
//
//	}
//
//	public OrgDdoMst getDDOInfoVOForDDOCode(String ddoCode) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//
//		Query lQuery = null;
//
//		lSBQuery.append("FROM OrgDdoMst");
//		lSBQuery.append(" WHERE ddoCode = :ddoCode ");
//		lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("ddoCode", ddoCode);
//
//		OrgDdoMst lObjDcpsDdoInfo = (OrgDdoMst) lQuery.uniqueResult();
//
//		return lObjDcpsDdoInfo;
//	}
//
//	public String getDdoNameForCode(String lStrDdoCode) {
//
//		String lStrDdoName = null;
//		StringBuilder lSBQuery = new StringBuilder();
//		lSBQuery
//				.append(" SELECT ddoName from OrgDdoMst WHERE ddoCode =  :DdoCode ");
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("DdoCode", lStrDdoCode);
//
//		List lLstCodeList = lQuery.list();
//		lStrDdoName = lLstCodeList.get(0).toString();
//
//		return lStrDdoName;
//	}
//
//	public DdoOffice getDdoMainOffice(String lStrDdoCode) {
//
//		DdoOffice lObjDdoMainOffice = null;
//		StringBuilder lSBQuery = new StringBuilder();
//		lSBQuery
//				.append(" from DdoOffice WHERE dcpsDdoCode =  :dcpsDdoCode and dcpsDdoOfficeDdoFlag = 'Yes' ");
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("dcpsDdoCode", lStrDdoCode);
//
//		List<DdoOffice> lLstOfficeList = lQuery.list();
//		
//		if(lLstOfficeList != null)
//		{
//			if(lLstOfficeList.size() != 0)
//			{
//				lObjDdoMainOffice = lLstOfficeList.get(0);
//			}
//		}
//
//		return lObjDdoMainOffice;
//	}
//
//	public OrgDdoMst getDdoVOForDdoCode(String ddoCode) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//
//		Query lQuery = null;
//
//		lSBQuery.append("FROM OrgDdoMst");
//		lSBQuery.append(" WHERE ddoCode = :ddoCode ");
//		lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("ddoCode", ddoCode);
//
//		OrgDdoMst lObjOrgDdoMst = (OrgDdoMst) lQuery.uniqueResult();
//
//		return lObjOrgDdoMst;
//	}
//
//	public String getTreasuryNameForDDO(String lStrDdoCode) {
//
//		String lStrTreasuryName = null;
//		StringBuilder sb = new StringBuilder();
//
//		sb.append("SELECT LM.locName FROM RltDdoOrg RO, CmnLocationMst LM ");
//		sb
//				.append("WHERE RO.ddoCode = :ddoCode AND	LM.locationCode = RO.locationCode");
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("ddoCode", lStrDdoCode);
//		lStrTreasuryName = selectQuery.list().get(0).toString();
//		return lStrTreasuryName;
//	}
//
//	public String getTreasuryShortNameForDDO(String lStrDdoCode) {
//
//		String lStrTreasuryName = null;
//		StringBuilder sb = new StringBuilder();
//
//		sb
//				.append("SELECT LM.locShortName FROM RltDdoOrg RO, CmnLocationMst LM ");
//		sb
//				.append("WHERE RO.ddoCode = :ddoCode AND	LM.locationCode = RO.locationCode");
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("ddoCode", lStrDdoCode);
//		lStrTreasuryName = selectQuery.list().get(0).toString();
//		return lStrTreasuryName;
//	}
//
//	public List getParentDeptForDDO(String lStrDdoCode) {
//
//		List lStrParentDept = null;
//		StringBuilder sb = new StringBuilder();
//
//		sb
//				.append("SELECT CM.locId,CM.locName FROM OrgDdoMst DM, CmnLocationMst CM ");
//		sb.append("WHERE DM.ddoCode = :ddoCode AND	CM.locId = DM.hodLocCode");
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("ddoCode", lStrDdoCode);
//		lStrParentDept = selectQuery.list();
//		return lStrParentDept;
//	}
//
//	public String getTreasuryCodeForDDO(String lStrDdoCode) {
//
//		String lStrTreasuryName = null;
//		StringBuilder sb = new StringBuilder();
//		List tempList = null;
//
//		sb.append("SELECT LM.locationCode FROM RltDdoOrg RO, CmnLocationMst LM ");
//		sb.append("WHERE RO.ddoCode = :ddoCode AND	LM.locationCode = RO.locationCode");
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("ddoCode", lStrDdoCode);
//		
//		tempList = selectQuery.list();
//		if(tempList != null)
//		{
//			if(tempList.get(0) != null)
//			{
//				lStrTreasuryName = selectQuery.list().get(0).toString();
//			}
//		}
//		return lStrTreasuryName;
//	}
//
//	public String getTreasuryCityForDDO(String lStrDdoCode) {
//
//		String lStrCityName = null;
//		StringBuilder sb = new StringBuilder();
//
//		sb
//				.append("SELECT CM.cityName FROM RltDdoOrg RO, CmnLocationMst LM ,CmnCityMst CM ");
//		sb
//				.append("WHERE RO.ddoCode = :ddoCode AND LM.locationCode = RO.locationCode AND LM.locCityId = CM.cityId ");
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("ddoCode", lStrDdoCode);
//		if(selectQuery.list() != null)
//		{
//			if(selectQuery.list().size() != 0)
//			{
//				if(selectQuery.list().get(0) != null)
//				{
//					lStrCityName = selectQuery.list().get(0).toString();
//				}
//			}
//		}
//		return lStrCityName;
//	}
//
//	public List getCadreForDept(Long lLngDeptCode) throws Exception {
//
//		List<Object> lLstReturnList = null;
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//			lSBQuery
//					.append("SELECT CM.cadreId, CM.cadreName from DcpsCadreMst CM ");
//			lSBQuery.append("WHERE CM.fieldDeptId = :fieldDeptId ");
//
//			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());
//
//			lObjQuery.setParameter("fieldDeptId", lLngDeptCode);
//
//			List lLstResult = lObjQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//			if (lLstResult != null && lLstResult.size() != 0) {
//				lLstReturnList = new ArrayList<Object>();
//				Object obj[];
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//					obj = (Object[]) lLstResult.get(liCtr);
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId(obj[0].toString());
//					lObjComboValuesVO.setDesc(obj[1].toString());
//					lLstReturnList.add(lObjComboValuesVO);
//				}
//			} else {
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} catch (Exception e) {
//			gLogger.error("Error is : " + e, e);
//			//e.printStackTrace();
//			throw e;
//		}
//		return lLstReturnList;
//	}
//
//	public List getDesigsForPFDAndCadre(Long fieldDeptId) throws Exception {
//
//		List<Object> lLstReturnList = null;
//
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//
//			lSBQuery
//					.append(" SELECT ODM.dsgnId,ODM.dsgnName FROM OrgDesignationMst ODM, MstDcpsDesignation MDD ");
//			lSBQuery
//			.append(" WHERE ODM.dsgnId = MDD.orgDesignationId AND MDD.fieldDeptId = :fieldDeptId order by ODM.dsgnName,ODM.dsgnId");
//
//			Session lObjSession = getReadOnlySession();
//			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());
//
//			lObjQuery.setParameter("fieldDeptId", fieldDeptId);
//
//			List lLstResult = lObjQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//			if (lLstResult != null && lLstResult.size() != 0) {
//				lLstReturnList = new ArrayList<Object>();
//				Object obj[];
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//					obj = (Object[]) lLstResult.get(liCtr);
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId(obj[0].toString());
//					lObjComboValuesVO.setDesc(obj[1].toString());
//					lLstReturnList.add(lObjComboValuesVO);
//				}
//			} else {
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} catch (Exception e) {
//			gLogger.error("Error is : " + e, e);
//			//e.printStackTrace();
//			throw e;
//		}
//		return lLstReturnList;
//	}
//
//	public List getLookupValuesForParent(Long lLngParentLookupId)
//			throws Exception {
//
//		List<Object> lLstReturnList = null;
//
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//
//			lSBQuery
//					.append(" SELECT lookupId,lookupName FROM CmnLookupMst where parentLookupId = :parentLookupId");
//
//			Session lObjSession = getReadOnlySession();
//			Query lObjQuery = lObjSession.createQuery(lSBQuery.toString());
//
//			lObjQuery.setParameter("parentLookupId", lLngParentLookupId);
//
//			List lLstResult = lObjQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//			if (lLstResult != null && lLstResult.size() != 0) {
//				lLstReturnList = new ArrayList<Object>();
//				Object obj[];
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//					obj = (Object[]) lLstResult.get(liCtr);
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId(obj[0].toString());
//					lObjComboValuesVO.setDesc(obj[1].toString());
//					lLstReturnList.add(lObjComboValuesVO);
//				}
//			} else {
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} catch (Exception e) {
//			gLogger.error("Error is : " + e, e);
//			//e.printStackTrace();
//			throw e;
//		}
//		return lLstReturnList;
//	}
//
//	public List getDeptNameFromDdoCode(String lStrDdoCode) {
//
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//
//		sb
//				.append("SELECT lm.locationCode,lm.locName FROM CmnLocationMst lm,OrgDdoMst dm ");
//		sb
//				.append("WHERE dm.ddoCode = :ddoCode AND dm.deptLocCode = lm.locationCode");
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("ddoCode", lStrDdoCode);
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//
//	public String getLocNameforLocId(Long locId) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List<String> tempList = new ArrayList<String>();
//		String locName = null;
//
//		lSBQuery
//				.append(" Select locName FROM CmnLocationMst WHERE locId = :locId");
//
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("locId", locId);
//
//		tempList = lQuery.list();
//		if (tempList != null && tempList.size() != 0) {
//			locName = tempList.get(0);
//		}
//		return locName;
//	}
//
//	public String getCadreNameforCadreId(Long cadreId) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List<String> tempList = new ArrayList<String>();
//		String cadreName = null;
//
//		lSBQuery
//				.append(" Select cadreName FROM DcpsCadreMst WHERE cadreId = :cadreId");
//
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("cadreId", cadreId);
//
//		tempList = lQuery.list();
//		if (tempList != null && tempList.size() != 0) {
//			cadreName = tempList.get(0);
//		}
//		return cadreName;
//	}
//	
//	public Long getCadreCodeforCadreId(Long cadreId) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List<Long> tempList = new ArrayList<Long>();
//		Long cadreCode = null;
//
//		lSBQuery.append(" Select cadreCode FROM DcpsCadreMst WHERE cadreId = :cadreId");
//
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("cadreId", cadreId);
//
//		tempList = lQuery.list();
//		if (tempList != null && tempList.size() != 0) {
//			if(tempList.get(0) != null)
//			{
//				if(!tempList.get(0).toString().equals(""))
//				{
//					cadreCode = Long.valueOf(tempList.get(0).toString());
//				}
//			}
//		}
//		return cadreCode;
//	}
//	
//	public Long getCadreIdforCadreCodeAndFieldDept(Long cadreCode,Long fieldDeptId) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List<Long> tempList = new ArrayList<Long>();
//		Long cadreId = null;
//
//		lSBQuery.append(" Select cadreId FROM DcpsCadreMst WHERE cadreCode = :cadreCode and fieldDeptId = :fieldDeptId");
//
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("cadreCode", cadreCode);
//		lQuery.setParameter("fieldDeptId", fieldDeptId);
//
//		tempList = lQuery.list();
//		if (tempList != null && tempList.size() != 0) {
//			if(tempList.get(0) != null)
//			{
//				if(!tempList.get(0).toString().equals(""))
//				{
//					cadreId = Long.valueOf(tempList.get(0).toString());
//				}
//			}
//		}
//		return cadreId;
//	}
//
//	public String getGroupIdforCadreId(Long cadreId) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List<String> tempList = new ArrayList<String>();
//		String groupId = null;
//
//		lSBQuery
//				.append(" Select groupId FROM DcpsCadreMst WHERE cadreId = :cadreId");
//
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("cadreId", cadreId);
//
//		tempList = lQuery.list();
//		if (tempList != null && tempList.size() != 0) {
//			groupId = tempList.get(0);
//		}
//		return groupId;
//	}
//
//	public String getDddoOfficeNameNameforId(Long dcpsDdoOfficeIdPk) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List<String> tempList = new ArrayList<String>();
//		String ddoOfficeName = null;
//
//		lSBQuery
//				.append(" Select dcpsDdoOfficeName FROM DdoOffice WHERE dcpsDdoOfficeIdPk = :dcpsDdoOfficeIdPk");
//
//		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//		lQuery.setParameter("dcpsDdoOfficeIdPk", dcpsDdoOfficeIdPk);
//
//		tempList = lQuery.list();
//		if (tempList != null && tempList.size() != 0) {
//			ddoOfficeName = tempList.get(0);
//		}
//		return ddoOfficeName;
//	}
//
//	public List getOfficesForPost(Long lLongPostId) throws Exception {
//
//		List<Object> lLstReturnList = null;
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//			lSBQuery
//					.append("SELECT DO.dcpsDdoOfficeIdPk, DO.dcpsDdoOfficeName from DdoOffice DO, HrPayOfficepostMpg OP");
//			lSBQuery
//					.append(" WHERE DO.dcpsDdoOfficeIdPk = OP.ddoOffice.dcpsDdoOfficeIdPk and OP.orgPostMstByPostId.postId = :lLongPostId");
//
//			Query lObjQuery = ghibSession.createQuery(lSBQuery.toString());
//
//			lObjQuery.setParameter("lLongPostId", lLongPostId);
//
//			List lLstResult = lObjQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//			if (lLstResult != null && lLstResult.size() != 0) {
//				lLstReturnList = new ArrayList<Object>();
//				Object obj[];
//				lLstReturnList = new ArrayList<Object>();
//
//				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//					obj = (Object[]) lLstResult.get(liCtr);
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId(obj[0].toString());
//					lObjComboValuesVO.setDesc(obj[1].toString());
//					lLstReturnList.add(lObjComboValuesVO);
//				}
//			} else {
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} catch (Exception e) {
//			gLogger.error("Error is : " + e, e);
//			//e.printStackTrace();
//			throw e;
//		}
//		return lLstReturnList;
//	}
//
//	public List<ComboValuesVO> getAllOrgType() throws Exception {
//
//		ArrayList<ComboValuesVO> lArrLstDepartnent = new ArrayList<ComboValuesVO>();
//		StringBuilder lStrQuery = new StringBuilder();
//		ComboValuesVO cmbVO;
//		List lLstResultList;
//		Iterator itr;
//		Object[] obj;
//
//		try {
//
//			lStrQuery
//					.append(" SELECT orgId,orgDesc FROM MstDcpsOrganization MDO order by MDO.orgType ASC");
//
//			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
//
//			lLstResultList = hqlQuery.list();
//			if (lLstResultList != null && lLstResultList.size() > 0) {
//				itr = lLstResultList.iterator();
//				while (itr.hasNext()) {
//					cmbVO = new ComboValuesVO();
//					obj = (Object[]) itr.next();
//					cmbVO.setId(obj[0].toString());
//					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
//					lArrLstDepartnent.add(cmbVO);
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Error is :" + e, e);
//			throw e;
//		}
//		return lArrLstDepartnent;
//	}
//
//	public MstEmp getEmpVOForEmpId(Long dcpsEmpId) throws Exception {
//
//		StringBuilder lStrQuery = new StringBuilder();
//		MstEmp lObjMstEmp = null;
//		try {
//
//			lStrQuery.append(" FROM MstEmp WHERE dcpsEmpId=:dcpsEmpId");
//
//			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
//			hqlQuery.setParameter("dcpsEmpId", dcpsEmpId);
//			lObjMstEmp = (MstEmp) hqlQuery.uniqueResult();
//		} catch (Exception e) {
//			logger.error("Error is :" + e, e);
//			throw e;
//		}
//		return lObjMstEmp;
//	}
//
//	public List getDatesFromFinYearId(Long yearId) throws Exception {
//		List lstDates = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT fromDate,toDate FROM SgvcFinYearMst WHERE finYearId = :yearId");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("yearId", yearId);
//			lstDates = hqlQuery.list();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return lstDates;
//	}
//
//	public String getCurrentInterestRate() {
//
//		String interestRate = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT interest FROM InterestRate WHERE status=1");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//
//			interestRate = hqlQuery.list().get(0).toString();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return interestRate;
//	}
//
//	public String getFinYearForYearId(Long yearId) {
//
//		String lStrYearDesc = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT finYearDesc FROM SgvcFinYearMst WHERE finYearId = :yearId");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("yearId", yearId);
//			lStrYearDesc = hqlQuery.list().get(0).toString();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return lStrYearDesc;
//	}
//
//	public Long getFinYearIdForYearCode(String yearCode) {
//
//		Long lLongYearId = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT finYearId FROM SgvcFinYearMst WHERE finYearCode = :finYearCode");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("finYearCode", yearCode);
//
//			if (hqlQuery.list().get(0) != null) {
//				lLongYearId = Long.valueOf(hqlQuery.list().get(0).toString());
//			}
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return lLongYearId;
//	}
//
//	public Float getCurrentDARate(String payComm) {
//
//		Float daRate = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT daRate FROM DARate WHERE status=1 AND payCommission = :payComm");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("payComm", payComm);
//			daRate = (Float) hqlQuery.list().get(0);
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return daRate;
//	}
//
//	public String getTreasuryNameForTreasuryId(Long treasuryId) {
//
//		String lStrYearDesc = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT locName FROM CmnLocationMst WHERE locationCode = :treasuryCode");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("treasuryCode", treasuryId.toString());
//			lStrYearDesc = hqlQuery.list().get(0).toString();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return lStrYearDesc;
//	}
//
//	public String getBankNameForBankCode(String bankCode) {
//
//		String lStrBankName = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT bankName FROM MstBankPay WHERE bankCode = :bankCode");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("bankCode", bankCode);
//			lStrBankName = hqlQuery.list().get(0).toString();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//		}
//		return lStrBankName;
//	}
//
//	public String getBranchNameForBranchCode(String branchId) {
//
//		String lStrBranchName = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT branchName FROM RltBankBranchPay WHERE branchId = :branchId");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("branchId", Long.valueOf(branchId));
//			if (hqlQuery.list().size() == 0) {
//				lStrBranchName = "";
//			} else {
//				lStrBranchName = hqlQuery.list().get(0).toString();
//			}
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//		}
//		return lStrBranchName;
//	}
//
//	public List getAllDDOForTreasury(String lStrTreasuryLocCode) {
//
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//
//		sb
//				.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
//		sb
//				.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode");
//		sb.append(" order by DM.ddoName");
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("-- Select --");
//			lLstReturnList.add(lObjComboValuesVO);
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc("(" + obj[0].toString() + ") " + obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//
//	public Boolean checkPFDForDDO(String lStrDdoCode) {
//		Boolean Status = false;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//		List<Boolean> lLstDdo = null;
//
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT adminFlag FROM OrgDdoMst WHERE ddoCode = :ddoCode");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("ddoCode", lStrDdoCode);
//			lLstDdo = hqlQuery.list();
//
//			if (lLstDdo.size() > 0) {
//				Status = lLstDdo.get(0);
//			}
//
//			if (Status == true) {
//				lSBQuery = new StringBuilder();
//				lSBQuery
//						.append(" update OrgDdoMst set adminFlag = 0 WHERE ddoCode = :ddoCode");
//				hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//				hqlQuery.setParameter("ddoCode", lStrDdoCode);
//				hqlQuery.executeUpdate();
//			}
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//		}
//		return Status;
//	}
//
//	public List getBillGroups(String lStrDDOCode) throws Exception {
//
//		String query = "select dcpsDdoBillGroupId,dcpsDdoBillDescription from MstDcpsBillGroup where dcpsDdoCode = :dcpsDdoCode";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("dcpsDdoCode", lStrDDOCode);
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//	
//	public List getBillGroupsNotDeletedAndNotDCPS(String lStrDDOCode) throws Exception {
//
//		String query = "select dcpsDdoBillGroupId,dcpsDdoBillDescription from MstDcpsBillGroup where dcpsDdoCode = :dcpsDdoCode";
//		query = query + " and (billDeleted is null or billDeleted <> 'Y') and (billDcps is null or billDcps <> 'Y') ";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("dcpsDdoCode", lStrDDOCode);
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//
//	public Long getDDOPostIdForDDOAsst(Long lLngPostId) {
//
//		Long lLongDdoPostId = null;
//		List lLstDdoDtls = null;
//
//		try {
//			getSession();
//			StringBuilder lSBQuery = new StringBuilder();
//			lSBQuery.append(" SELECT ddoPostId");
//			lSBQuery.append(" FROM  RltDdoAsst");
//			lSBQuery.append(" WHERE asstPostId = :postId ");
//			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//			lQuery.setParameter("postId", lLngPostId);
//
//			lLstDdoDtls = lQuery.list();
//
//			if (lLstDdoDtls.size() != 0) {
//				lLongDdoPostId = Long.valueOf(lLstDdoDtls.get(0).toString());
//			}
//
//		} catch (Exception e) {
//			gLogger.error("Error is :" + e, e);
//
//		}
//		return lLongDdoPostId;
//	}
//
//	public String getFinYearCodeForYearId(Long yearId) {
//
//		String lStrYearCode = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery.append(" SELECT finYearCode FROM SgvcFinYearMst WHERE finYearId = :yearId");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("yearId", yearId);
//			lStrYearCode = hqlQuery.list().get(0).toString();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return lStrYearCode;
//	}
//	
//	public String getFinYearDescForYearCode(String finYearCode) {
//
//		String lStrYearCode = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT finYearDesc FROM SgvcFinYearMst WHERE finYearCode = :finYearCode");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("finYearCode", finYearCode);
//			lStrYearCode = hqlQuery.list().get(0).toString();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return lStrYearCode;
//	}
//
//	public String getFinYearIdForDate(Date FinDate) {
//
//		String lStrYearId = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery
//					.append(" SELECT finYearId FROM SgvcFinYearMst WHERE :finDate between fromDate and toDate");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("finDate", FinDate);
//			lStrYearId = hqlQuery.list().get(0).toString();
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return lStrYearId;
//	}
//	
//	public List getStates(Long langId) {
//
//		String query = "select stateId,stateName from CmnStateMst where cmnCountryMst.countryId = 1 and cmnLanguageMst.langId = :langId";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setLong("langId", langId);
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		return lLstReturnList;
//	}
//	
//	public String getAdminBudgetCodeForDDO(String lStrDDOCode) throws Exception {
//
//		String lStrAdmBudgetCode = "";
//		List tempList = null;
//		
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//			lSBQuery.append(" SELECT AD.admBdgtCd FROM AdmDept AD,OrgDdoMst OD, CmnLocationMst CM");
//			lSBQuery.append(" WHERE OD.deptLocCode = CM.locId ");
//			lSBQuery.append(" AND CM.locId = AD.locId");
//			lSBQuery.append(" AND OD.ddoCode = :ddoCode");
//			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
//			lQuery.setParameter("ddoCode", lStrDDOCode.trim());
//			
//			tempList = lQuery.list();
//			if(tempList != null)
//			{	
//				if(tempList.size() != 0)
//				{
//					if(tempList.get(0) != null)
//					{
//						lStrAdmBudgetCode = tempList.get(0).toString();
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.error("Error is :" + e, e);
//		}
//		return lStrAdmBudgetCode;
//	}
//	
//	public Long getDDOAsstPostIdForDDO(String lStrDDOCode) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List tempList = new ArrayList();
//		Long lLongAsstPostId = 0L;
//
//		lSBQuery.append(" SELECT min(RD.ASST_POST_ID) FROM rlt_dcps_ddo_asst RD join org_ddo_mst OD on OD.post_id = RD.ddo_post_id where OD.ddo_code = '"+lStrDDOCode+"'");
//
//		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
//
//		tempList = lQuery.list();
//		lLongAsstPostId = Long.valueOf(tempList.get(0).toString());
//		return lLongAsstPostId;
//
//	}
//	
//	public Long getDDOPostIdForDDO(String lStrDDOCode) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List tempList = new ArrayList();
//		Long lLongDDOPostId = 0L;
//
//		lSBQuery.append(" SELECT OD.post_id FROM org_ddo_mst OD where OD.ddo_code = '"+lStrDDOCode+"'");
//
//		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
//
//		tempList = lQuery.list();
//		lLongDDOPostId = Long.valueOf(tempList.get(0).toString());
//		return lLongDDOPostId;
//
//	}
//	
//	public Long getUserIdForPostId(Long lLongPostId) {
//
//		StringBuilder lSBQuery = new StringBuilder();
//		List tempList = new ArrayList();
//		Long lLongAsstUserId = 0L;
//
//		lSBQuery.append(" select user_id from org_userpost_rlt where post_id = " + lLongPostId + " and activate_flag = 1");
//
//		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
//
//		tempList = lQuery.list();
//		lLongAsstUserId = Long.valueOf(tempList.get(0).toString());
//		return lLongAsstUserId;
//
//	}
//	
//	public String getFieldDeptOfDDO(String lStrDdoCode) {
//
//		String lStrFieldDept = null;
//		StringBuilder sb = new StringBuilder();
//
//		sb.append(" SELECT OD.hodLocCode FROM OrgDdoMst OD ");
//		sb.append(" WHERE OD.ddoCode = :ddoCode ");
//		
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("ddoCode", lStrDdoCode.trim());
//		lStrFieldDept = selectQuery.list().get(0).toString();
//		return lStrFieldDept;
//	}
//	
//	public List getAllAdminDeptsForReportIncludingAllDepts() throws Exception {
//
//		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId = 100001 order by CM.locName";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		
//		lObjComboValuesVO = new ComboValuesVO();
//		lObjComboValuesVO.setId("AllDepts");
//		lObjComboValuesVO.setDesc("All Departments");
//		lLstReturnList.add(lObjComboValuesVO);
//
//		return lLstReturnList;
//	}
//	
//
//	public List getAllDDOInclAll(String lStrTreasuryLocCode) {
//
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//
//		sb
//				.append("SELECT DM.ddoCode, DM.ddoName FROM RltDdoOrg RO, OrgDdoMst DM,CmnLocationMst LM ");
//		sb
//				.append("WHERE RO.locationCode = :locationCode AND RO.ddoCode = DM.ddoCode AND LM.locationCode = RO.locationCode AND DM.ddoName IS NOT NULL");
//		sb.append(" order by DM.ddoName");
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//		selectQuery.setParameter("locationCode", lStrTreasuryLocCode);
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("-- Select --");
//			lLstReturnList.add(lObjComboValuesVO);
//			
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("AllDDO");
//			lObjComboValuesVO.setDesc("All DDOs");
//			lLstReturnList.add(lObjComboValuesVO);
//			
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc("(" + obj[0].toString() + ") " + obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//		
//			lLstReturnList = new ArrayList<Object>();
//			
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("AllDDO");
//			lObjComboValuesVO.setDesc("All DDOs");
//			lLstReturnList.add(lObjComboValuesVO);
//			
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//		
//		
//
//		return lLstReturnList;
//	}
//	public String getFinYearIdForYearDesc(String yearDesc) {
//
//		String lLongYearId = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery.append(" SELECT finYearId FROM SgvcFinYearMst WHERE finYearDesc = :yearDesc");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("yearDesc", yearDesc);
//
//			if (hqlQuery.list().get(0) != null) {
//				lLongYearId = hqlQuery.list().get(0).toString();
//			}
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error " + e);
//
//		}
//		return lLongYearId;
//	}
//	public String getMonthIdForName(String monthname) {
//
//		String monthid = null;
//		StringBuilder lSBQuery = null;
//		Query hqlQuery = null;
//		logger.info("monthname is in dao "+monthname);
//		try {
//			ghibSession = getSession();
//			lSBQuery = new StringBuilder();
//			lSBQuery.append(" SELECT monthId FROM SgvaMonthMst WHERE monthName = :monthname ");
//			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
//			hqlQuery.setParameter("monthname",monthname);
//			if (hqlQuery.list().get(0) != null) {
//			monthid = hqlQuery.list().get(0).toString();
//			logger.info("monthid is in dao "+monthid);
//			}
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.info("Error is " + e);
//
//		}
//		return monthid;
//	}
//	public List getAllTreasuriesForInterest() throws Exception {
//
//		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId = 100003 and CM.locId not in (9991,2028914,2028915)  order by CM.locId";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[0].toString() +"-"+ obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//	
//	public List getAllTreasuriesAndSubTreasuriesForInterest() throws Exception {
//
//		String query = "select CM.locId , CM.locName from CmnLocationMst CM where departmentId in (100003,100006) order by CM.locId";
//		List<Object> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//		sb.append(query);
//		Query selectQuery = ghibSession.createQuery(sb.toString());
//
//		List lLstResult = selectQuery.list();
//		ComboValuesVO lObjComboValuesVO = null;
//		if (lLstResult != null && lLstResult.size() != 0) {
//			lLstReturnList = new ArrayList<Object>();
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				lObjComboValuesVO.setDesc(obj[0].toString() +"-"+ obj[1].toString());
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<Object>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//	public String getAstDDONameForAstDDO(String empUserName,String strDdoCode) {
//		Session hibSession = getSession();
//		
//		String finalCheckFlag=null;
//		List lstFinalCheck=null;
//		List chkEmpInSystem=null;
//		StringBuffer sb= new StringBuffer();
//		StringBuffer sb1= new StringBuffer();
//		gLogger.info("empUserName: "+empUserName+" strDdoCode: "+strDdoCode);
//		
//		//to check emp in system
//		sb1.append(" SELECT EMP_NAME FROM mst_dcps_emp where SEVARTH_ID='"+empUserName+"'  ");
//		gLogger.info("Query to check emp in system:  " + sb1.toString());
//		Query sqlQuery1 = hibSession.createSQLQuery(sb1.toString());
//		chkEmpInSystem=sqlQuery1.list();
//		
//		
//		//to check emp in system and with ddo
//		sb.append(" SELECT EMP_NAME FROM mst_dcps_emp where SEVARTH_ID='"+empUserName+"'  ");
//		sb.append(" and DDO_CODE = '"+strDdoCode+"' ");
//		gLogger.info("Query to get emp name with ddo:  " + sb.toString());
//		Query sqlQuery = hibSession.createSQLQuery(sb.toString());
//		lstFinalCheck=sqlQuery.list();
//		
//		if(lstFinalCheck.size() > 0 && chkEmpInSystem.size() > 0)
//		{
//			finalCheckFlag=lstFinalCheck.get(0).toString();
//		}
//		else if (chkEmpInSystem.size() > 0)
//		{
//			finalCheckFlag="notWithDDO";
//		}
//		else 
//		{
//			finalCheckFlag="notInSystem";
//		}
//		return finalCheckFlag;
//	}
//
//	@Override
//	public String validateEmpDobDojForResetPwd(String empUserName,
//			String strDdoCode, String dob,String doj) {
//		Session hibSession = getSession();
//		
//		String finalCheckFlag=null;
//		List lstFinalCheck=null;
//		StringBuffer sb= new StringBuffer();
//		gLogger.info("empUserName: "+empUserName+" strDdoCode: "+strDdoCode);
//		
//		
//		sb.append(" SELECT count(1) FROM mst_dcps_emp where SEVARTH_ID='"+empUserName+"'  ");
//		sb.append(" and DDO_CODE = '"+strDdoCode+"' and DOB = '"+dob+"' and DOJ = '"+doj+"' ");
//		gLogger.info("Query to get emp name:  " + sb.toString());
//		Query sqlQuery1 = hibSession.createSQLQuery(sb.toString());
//		lstFinalCheck=sqlQuery1.list();
//		if(lstFinalCheck.size() > 0)
//		{
//			finalCheckFlag=lstFinalCheck.get(0).toString();
//		}
//		else 
//		{
//			finalCheckFlag="0";
//		}
//		return finalCheckFlag;
//	}
//
//	@Override
//	public void UpdatePwd(String empUserName) {
//		// TODO Auto-generated method stub
//			Session hibSession = getSession();
//			StringBuffer sb= new StringBuffer();
//			gLogger.info("empUserName: "+empUserName);
//			sb.append(" update ORG_USER_MST set PASSWORD = '0b76f0f411f6944f9d192da0fcbfb292' where USER_NAME = '"+empUserName+"' ");
//			gLogger.info("Query to update password:  " + sb.toString());
//			Query sqlQuery1 = hibSession.createSQLQuery(sb.toString());
//			sqlQuery1.executeUpdate();
//	}
//	
//	public List getReasonValues(Long parentLookupId) {
//
//		List<ComboValuesVO> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//
//		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
//		sb.append("SELECT LOOKUP_ID,LOOKUP_NAME FROM CMN_LOOKUP_MST where PARENT_LOOKUP_ID="+parentLookupId+" and LOOKUP_ID <> 700053 ");		
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	
//
//		lLstReturnList = new ArrayList<ComboValuesVO>();
//
//		List lLstResult = selectQuery.list();
//		gLogger.info("list size:" +lLstResult.size());
//
//		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
//		lObjComboValuesVO.setId("-1");
//		lObjComboValuesVO.setDesc("--Select--");
//		lLstReturnList.add(lObjComboValuesVO);
//		if (lLstResult != null && lLstResult.size() != 0) {
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				String desc="<![CDATA["+obj[1].toString()+"]]>";
//				lObjComboValuesVO.setDesc(desc);
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<ComboValuesVO>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//	
//	public List getDDOoficeDesgn(String ddoCode) {
//
//		List lLstResult = null;
//		StringBuilder sb = new StringBuilder();
//
//		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
//		sb.append(" SELECT org.DDO_OFFICE,desg.DESIG_DESC FROM ORG_DDO_MST org  ");
//		sb.append(" inner join ORG_POST_DETAILS_RLT post on post.POST_ID = org.POST_ID ");	
//		sb.append(" inner JOIN MST_PAYROLL_DESIGNATION desg on desg.ORG_DESIGNATION_ID = post.DSGN_ID and org.HOD_LOC_CODE=desg.FIELD_DEPT_ID where org.DDO_CODE="+ddoCode);		
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
//		gLogger.info("Query to get emp name with ddo:  " + sb.toString());
//		 lLstResult = selectQuery.list();
//		gLogger.info("list size:" +lLstResult.size());	
//
//		return lLstResult;
//	}
//
//
//	public List getDeptReasonValues(Long parentLookupId) {
//
//		List<ComboValuesVO> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//
//		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
//		sb.append("SELECT LOOKUP_ID,LOOKUP_NAME FROM CMN_LOOKUP_MST where LOOKUP_ID="+parentLookupId);		
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	
//
//		lLstReturnList = new ArrayList<ComboValuesVO>();
//
//		List lLstResult = selectQuery.list();
//		gLogger.info("list size:" +lLstResult.size());
//
//		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
//		lObjComboValuesVO.setId("-1");
//		lObjComboValuesVO.setDesc("--Select--");
//		lLstReturnList.add(lObjComboValuesVO);
//		if (lLstResult != null && lLstResult.size() != 0) {
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();				
//				lObjComboValuesVO.setId(obj[0].toString());
//				String desc="<![CDATA["+obj[1].toString()+"]]>";
//				lObjComboValuesVO.setDesc(desc);
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<ComboValuesVO>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//	
//	public String checkDDOCodePresent(String strDdoCode) {
//		Session hibSession = getSession();
//		
//		String finalCheckFlag=null;
//		List lstFinalCheck=null;
//		List chkEmpInSystem=null;
//		StringBuffer sb= new StringBuffer();	
//		//to check emp in system and with ddo
//		sb.append(" SELECT DDO_CODE FROM org_ddo_mst where ddo_code='"+strDdoCode+"'  ");		
//		gLogger.info("Query to get emp name with ddo:  " + sb.toString());
//		Query sqlQuery = hibSession.createSQLQuery(sb.toString());
//		//lstFinalCheck=sqlQuery.list();
//		
//		if (sqlQuery.list()!=null && sqlQuery.list().size() > 0)
//		{
//			finalCheckFlag="YES";
//		}
//		else 
//		{
//			finalCheckFlag="NO";
//		}
//		return finalCheckFlag;
//	}
//
//	@Override
//	public List getDDOoficeTreasury(String ddoCode) {
//
//		List lLstResult = null;
//		StringBuilder sb = new StringBuilder();
//
//		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
//		sb.append(" SELECT ofc.OFF_NAME,LM.loc_Name FROM Rlt_Ddo_Org RO inner join Cmn_Location_Mst LM  on	LM.location_Code = RO.location_Code  ");
//		sb.append(" inner join MST_DCPS_DDO_OFFICE ofc on ofc.DDO_CODE = RO.DDO_CODE ");	
//		sb.append(" WHERE RO.ddo_Code = "+ddoCode+" and ofc.DDO_OFFICE = 'Yes' ");		
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
//		gLogger.info("Query to get treasury office name with ddo:  " + sb.toString());
//		 lLstResult = selectQuery.list();
//		gLogger.info("list size:" +lLstResult.size());	
//
//		return lLstResult;
//	}
//	public List getNewTreasury(String ddoCode) {
//
//		List lLstResult = null;
//		StringBuilder sb = new StringBuilder();
//
//		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
//		sb.append(" SELECT LM.location_Code,LM.LOC_NAME FROM Rlt_Ddo_Org RO, Cmn_Location_Mst LM   ");
//		sb.append(" WHERE RO.ddo_Code = "+ddoCode+" AND	LM.location_Code = RO.location_Code ");			
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
//		gLogger.info("Query to getNewTreasury name with ddo:  " + sb.toString());
//		 lLstResult = selectQuery.list();
//		gLogger.info("list size:" +lLstResult.size());	
//
//		return lLstResult;
//	}
//
//	@Override
//	public List<ComboValuesVO> getTreasuryList() {
//		List<ComboValuesVO> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//
//		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
//		sb.append("SELECT loc_id,loc_Name  FROM CMN_LOCATION_MST  where department_Id=100003 order by loc_Name");		
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	
//
//		lLstReturnList = new ArrayList<ComboValuesVO>();
//
//		List lLstResult = selectQuery.list();
//		gLogger.info("list size:" +lLstResult.size());
//
//		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
//		lObjComboValuesVO.setId("-1");
//		lObjComboValuesVO.setDesc("--Select--");
//		lLstReturnList.add(lObjComboValuesVO);
//		if (lLstResult != null && lLstResult.size() != 0) {
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				String desc="<![CDATA["+obj[1].toString()+"]]>";
//				lObjComboValuesVO.setDesc(desc);
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<ComboValuesVO>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//	}
//
//	@Override
//	public List<ComboValuesVO> getSubTreasuryList(Long treasuryId) {
//		List<ComboValuesVO> lLstReturnList = null;
//		StringBuilder sb = new StringBuilder();
//
//		sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where (department_id=100006 and PARENT_LOC_ID=:loc_id ) or LOC_ID= :loc_id order by loc_name ");
//		gLogger.info("query to select sub treasury from treasury code:::" + sb);
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
//		gLogger.info("sql query created");
//		selectQuery.setParameter("loc_id", treasuryId);	
//		
//
//		lLstReturnList = new ArrayList<ComboValuesVO>();
//
//		List lLstResult = selectQuery.list();
//		gLogger.info("list size:" +lLstResult.size());
//
//		ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
//		lObjComboValuesVO.setId("-1");
//		lObjComboValuesVO.setDesc("--Select--");
//		lLstReturnList.add(lObjComboValuesVO);
//		if (lLstResult != null && lLstResult.size() != 0) {
//			Object obj[];
//			for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//				obj = (Object[]) lLstResult.get(liCtr);
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId(obj[0].toString());
//				String desc="<![CDATA["+obj[1].toString()+"]]>";
//				lObjComboValuesVO.setDesc(desc);
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//		} else {
//			lLstReturnList = new ArrayList<ComboValuesVO>();
//			lObjComboValuesVO = new ComboValuesVO();
//			lObjComboValuesVO.setId("-1");
//			lObjComboValuesVO.setDesc("--Select--");
//			lLstReturnList.add(lObjComboValuesVO);
//		}
//
//		return lLstReturnList;
//		
//	}
//
//	@Override
//	public List getDdoCodeForAutoCompleteTresury(String ddoCode, Long locId) {
//		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
//		ComboValuesVO cmbVO;
//		Object[] obj;
//		Session hibSession = getSession();
//		StringBuilder lSBQuery = new StringBuilder();
//		Query lQuery = null;
//		lSBQuery.append("select ddo_Code from rlt_ddo_org where ddo_Code LIKE :searchKey and location_code= "+locId+" order by ddo_Code");
//		lQuery = hibSession.createSQLQuery(lSBQuery.toString());
//		lQuery.setParameter("searchKey", ddoCode + '%');
//		
//		List resultList = lQuery.list();
//		
//		if (resultList != null && !resultList.isEmpty()) {
//			for(Integer lInt = 0;lInt<resultList.size();lInt++){
//				cmbVO = new ComboValuesVO();			
//				cmbVO.setId(resultList.get(lInt).toString());
//				cmbVO.setDesc(resultList.get(lInt).toString());
//				finalList.add(cmbVO);
//			}
//		}
//
//		return finalList;
//	}
//
//	@Override
//	public String getMaxDDOCode(String searchKey) {
//		String lStrDdoCode = null;
//		List lLstDdoDtls = null;
//
//		try {
//			StringBuilder lSBQuery = new StringBuilder();
//			lSBQuery.append(" select max(ddo_Code) from Org_Ddo_Mst where ddo_Code LIKE :searchKey ");			
//			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
//			lQuery.setParameter("searchKey", searchKey + '%');
//
//			lLstDdoDtls = lQuery.list();
//
//			if(lLstDdoDtls != null)
//			{
//				if(lLstDdoDtls.size()!= 0)
//				{
//					if(lLstDdoDtls.get(0) != null)
//					{
//						lStrDdoCode = lLstDdoDtls.get(0).toString();
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			//e.printStackTrace();
//			gLogger.error("Error is :" + e, e);
//
//		}
//		return lStrDdoCode;
//	}
//
//	@Override
//	public int updateDDOCode(String tableName, String oldDdoCode,
//			String newDdoCode) {
//Session hibSession = getSession();
//		String locId=newDdoCode.substring(0,4);
//		StringBuffer strQuery = new StringBuffer(); 
//		if(tableName.equals("RLT_DDO_ORG"))
//			strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"',location_code = "+locId+"  where ddo_code='"+oldDdoCode+"' ");
//		else if(tableName.equals("TRN_DCPS_CONTRIBUTION"))
//		strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"',TREASURY_CODE = "+locId+"  where ddo_code='"+oldDdoCode+"' ");
//		else 
//			strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"'  where ddo_code='"+oldDdoCode+"' ");
//		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
//		gLogger.info("Query to get treasury office name with ddo:  " +strQuery.toString());
//int updateCount = updateQuery.executeUpdate();
//updateCount=updateCount+1;
//		return updateCount;
//		
//	}
//
//	@Override
//	public String getAstUsername(String oldDdoCode) {
//		String astUsername = "";
//		StringBuilder sb = new StringBuilder();
//
//		sb.append(" SELECT usermst.USER_NAME FROM ORG_DDO_MST org inner join RLT_DCPS_DDO_ASST rlt on org.POST_ID = rlt.DDO_POST_ID ");
//		sb.append(" inner join ORG_USERPOST_RLT userpost on userpost.POST_ID = rlt.ASST_POST_ID ");
//		sb.append(" inner join ORG_USER_MST usermst on usermst.USER_ID = userpost.USER_ID ");
//		sb.append(" where userpost.ACTIVATE_FLAG = 1 and org.DDO_CODE = '"+oldDdoCode+"' ");	
//		
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	
//		if(selectQuery.list()!=null &&!selectQuery.list().isEmpty())
//		astUsername = selectQuery.list().get(0).toString();
//		return astUsername;
//	}
//
//	@Override
//	public int updateAstUserName(String newASTuserName, String astUserName) {
//Session hibSession = getSession();
//		
//		StringBuffer strQuery = new StringBuffer(); 
//		strQuery.append("update org_user_mst set USER_NAME = '"+newASTuserName+"',UPDATED_DATE = sysdate where USER_NAME = '"+astUserName+"' ");
//		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
//		gLogger.info("Query to updateAstUserName for AST:  " +strQuery.toString());
//int updateCount = updateQuery.executeUpdate();
//		return updateCount;
//	}
//
//	@Override
//	public String getDDOUsername(String oldDdoCode,String newDdoCode) {
//		String ddoUsername = "";
//		StringBuilder sb = new StringBuilder();
//		Session hibSession = getSession();
//		sb.append(" select user_name from org_user_mst where user_name = '"+oldDdoCode+"' ");		
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
//		if(selectQuery.list()!=null &&!selectQuery.list().isEmpty())
//		ddoUsername = selectQuery.list().get(0).toString();
//		if(ddoUsername.equals(oldDdoCode)){
//			StringBuilder sb1 = new StringBuilder();
//			
//			sb1.append(" update org_user_mst set user_name = '"+newDdoCode+"',updated_date = sysdate  where user_name = '"+oldDdoCode+"' ");
//			Query updateQuery = hibSession.createSQLQuery(sb1.toString());
//			gLogger.info("Query to updateAstUserName for AST:  " +sb1.toString());
//	int updateCount = updateQuery.executeUpdate();
//	return newDdoCode;
//		}else
//		return ddoUsername;
//		
//	}
//
//	@Override
//	public String getLocationCode(String oldDdoCode) {
//		String locationCode = "";
//		StringBuilder sb = new StringBuilder();
//
//		sb.append(" SELECT LOCATION_CODE FROM ORG_DDO_MST  where DDO_CODE = '"+oldDdoCode+"' ");	
//		
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	
//		if(selectQuery.list()!=null &&!selectQuery.list().isEmpty())
//			locationCode = selectQuery.list().get(0).toString();
//		return locationCode;
//	}
//
//	@Override
//	public int updatenewTreasuryDDOCode(String tableName, String oldDdoCode,
//			String newDdoCode,String locationCode,String ddoPostId,String asstDdoPostId) {
//		Session hibSession = getSession();
//		String locId=newDdoCode.substring(0,4);
//		int updateCount=0;
//		StringBuffer strQuery = new StringBuffer(); 
//		if(tableName.equals("RLT_DDO_ORG"))
//			strQuery.append("update "+tableName+" set location_code = "+locId+"  where ddo_code='"+newDdoCode+"' ");
//		else if(tableName.equals("TRN_DCPS_CONTRIBUTION"))
//		strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"',TREASURY_CODE = "+locId+"  where ddo_code='"+oldDdoCode+"' ");
//		else if(tableName.equals("ORG_DDO_MST"))
//			strQuery.append("update "+tableName+" set location_code = "+locationCode+",post_id= '"+ddoPostId+"' where ddo_code='"+newDdoCode+"' ");
//		else if(tableName.equals("MST_DCPS_DDO_OFFICE"))
//			strQuery.append("update "+tableName+" set LOC_ID = "+locationCode+"  where ddo_code='"+newDdoCode+"' ");
//		else 
//			strQuery.append("update "+tableName+" set ddo_code ='"+newDdoCode+"'  where ddo_code='"+oldDdoCode+"' ");
//		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
//		gLogger.info("Query to get treasury office name with ddo:  " +strQuery.toString());
// updateCount = updateQuery.executeUpdate();
//updateCount=updateCount++;
//gLogger.info("updateCount:  " +updateCount);
//		return updateCount;
//	}
//
//	@Override
//	public List getPostId(String oldDdoCode) {
//		List lLstResult = null;
//		StringBuilder sb = new StringBuilder();
//
//		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
//		sb.append(" SELECT rlt.DDO_POST_ID,rlt.ASST_POST_ID FROM org_ddo_mst org inner join RLT_DCPS_DDO_ASST rlt on org.POST_ID = rlt.DDO_POST_ID where org.DDO_CODE = "+ oldDdoCode);	
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
//		gLogger.info("Query to get treasury office name with ddo:  " + sb.toString());
//		 lLstResult = selectQuery.list();
//		gLogger.info("list size:" +lLstResult.size());	
//
//		return lLstResult;
//	}
//
//	@Override
//	public void updateOldDDOCodePostIdLocId(String oldDdoCode, String ddoPostId,String newDdoPostId) {
//		Session hibSession = getSession();	
//		StringBuffer strQuery = new StringBuffer(); 		
//			strQuery.append("update ORG_DDO_MST set location_code = null ,post_id = null where ddo_code='"+oldDdoCode+"' ");	
//		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
//		gLogger.info("Query to get treasury office name with ddo:  " +strQuery.toString());
// updateQuery.executeUpdate();
//	}
//
//	@Override
//	public List getnewDDOPostId(String newDdoCode) {
//		List lLstResult = null;
//		StringBuilder sb = new StringBuilder();
//
//		//SELECT dm.DDO_CODE,dm.DDO_NAME FROM RLT_DDO_ORG RD,ORG_DDO_MST DM where RD.DDO_CODE=DM.DDO_CODE AND RD.LOCATION_CODE='1201'
//		sb.append(" SELECT rlt.DDO_POST_ID,rlt.ASST_POST_ID FROM org_ddo_mst org inner join RLT_DCPS_DDO_ASST rlt on org.POST_ID = rlt.DDO_POST_ID where org.DDO_CODE = "+ newDdoCode);	
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());			
//		gLogger.info("Query to get treasury office name with ddo:  " + sb.toString());
//		 lLstResult = selectQuery.list();
//		gLogger.info("list size:" +lLstResult.size());	
//
//		return lLstResult;
//	}
//
//	@Override
//	public void updatenewTreasuryAsstDDOPostId(String asstDdoPostId,
//			String newAsstDdoPostId) {
//		Session hibSession = getSession();	
//		StringBuffer strQuery = new StringBuffer();
//		strQuery.append("update org_userpost_rlt set post_id ='"+asstDdoPostId+"' where post_id='"+newAsstDdoPostId+"' ");
//		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
//		gLogger.info("Query to get treasury office name with ddo:  " +strQuery.toString());
// updateQuery.executeUpdate();
//	}
//
//	@Override
//	public String checkAstUserName(String newASTuserName) {
//Session hibSession = getSession();
//		
//		String finalCheckFlag=null;
//		List lstFinalCheck=null;
//		List chkEmpInSystem=null;
//		StringBuffer sb= new StringBuffer();	
//		//to check emp in system and with ddo
//		sb.append(" SELECT user_name FROM org_user_mst where user_name='"+newASTuserName+"'  ");		
//		gLogger.info("Query to get emp name with ddo:  " + sb.toString());
//		Query sqlQuery = hibSession.createSQLQuery(sb.toString());
//		//lstFinalCheck=sqlQuery.list();
//		
//		if (sqlQuery.list()!=null && sqlQuery.list().size() > 0)
//		{
//			finalCheckFlag="YES";
//		}
//		else 
//		{
//			finalCheckFlag="NO";
//		}
//		return finalCheckFlag;
//	}
//
//	@Override
//	public String checkdeleteDDOcode() {
//Session hibSession = getSession();
//		
//		String finalCheckFlag=null;
//		List lstFinalCheck=null;
//		List chkEmpInSystem=null;
//		StringBuffer sb= new StringBuffer();	
//		//to check emp in system and with ddo
//		sb.append(" SELECT ddo_code FROM org_ddo_mst where ddo_code like '111111%'  ");		
//		gLogger.info("Query to get ddo_code with ddo:  " + sb.toString());
//		Query sqlQuery = hibSession.createSQLQuery(sb.toString());
//		//lstFinalCheck=sqlQuery.list();
//		
//		if (sqlQuery.list()!=null && sqlQuery.list().size() > 0)
//		{
//			finalCheckFlag="YES";
//		}
//		else 
//		{
//			finalCheckFlag="NO";
//		}
//		return finalCheckFlag;
//	}
//	
//	public List getYearForDCPSReports(String lStrLangId, String lStrLocId) {
//		ArrayList<ComboValuesVO> arrYear = new ArrayList<ComboValuesVO>();
//		Connection lCon = null;
//		PreparedStatement lStmt = null;
//		ResultSet lRs = null;
//
//		String fin_year_id = null;
//		String fin_name = null;
//		try {
//
//			lCon = DBConnection.getConnection();
//			StringBuffer lsb = new StringBuffer();
//			lsb = new StringBuffer(
//					"select * from sgvc_fin_year_mst where lang_id ='"
//							+ lStrLangId
//							+ "'  and fin_year_code between '2007' and '2015' order by fin_year_code ");
//
//			lStmt = lCon.prepareStatement(lsb.toString());
//			lRs = lStmt.executeQuery();
//			while (lRs.next()) {
//				ComboValuesVO vo = new ComboValuesVO();
//				fin_year_id = lRs.getString("fin_year_id");
//				fin_name = lRs.getString("fin_year_desc");
//				vo.setId(fin_year_id);
//				vo.setDesc(fin_name);
//				arrYear.add(vo);
//			}
//
//		} catch (Exception e) {
//			gLogger.error("Exception is : " + e, e);
//		} finally {
//			try {
//				if (lStmt != null) {
//					lStmt.close();
//				}
//				if (lRs != null) {
//					lRs.close();
//				}
//				if (lCon != null) {
//					lCon.close();
//				}
//
//				lStmt = null;
//				lRs = null;
//				lCon = null;
//			} catch (Exception e) {
//				e.printStackTrace();
//				gLogger.info("Sql Exception:" + e, e);
//			}
//		}
//		return arrYear;
//	}
//
//	@Override
//	public Long getdeleteDDOcode() {
//		Long deleteDddocode = 0l;
//		StringBuilder sb = new StringBuilder();
//
//		sb.append(" SELECT max(ddo_code) FROM org_ddo_mst where ddo_code like '111111%'  ");	
//		
//		Query selectQuery = ghibSession.createSQLQuery(sb.toString());	
//		if(selectQuery.list()!=null &&!selectQuery.list().isEmpty())
//			deleteDddocode =Long.parseLong( selectQuery.list().get(0).toString());
//		return deleteDddocode;
//	}
//
//	@Override
//	public int updateNewDDOCodeDelete(String crontableName,
//			Long newDDocodeDelete, String newDdoCode) {
//		Session hibSession = getSession();		
//		StringBuffer strQuery = new StringBuffer(); 		
//			strQuery.append("update "+crontableName+" set ddo_code ='"+newDDocodeDelete+"'  where ddo_code='"+newDdoCode+"' ");
//		Query updateQuery = hibSession.createSQLQuery(strQuery.toString());
//		gLogger.info("Query to get treasury office name with new ddo:  " +updateQuery.toString());
//int updateCount = updateQuery.executeUpdate();
//updateCount=updateCount+1;
//		return updateCount;
//	}
//	
//	public List getLevel()
//	  {
//	    List temp = null;
//	    List lLstReturnList = null;
//	    Session hibSession = getSession();
//	    StringBuilder SBQuery = new StringBuilder();
//	    try
//	    {
//	      SBQuery.append("SELECT LEVEL_ID,LEVEL FROM RLT_PAYBAND_GP_7PC ORDER BY LEVEL_ID");
//	      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
//	      this.logger.error("sqlQuery Size" + sqlQuery.toString());
//	      temp = sqlQuery.list();
//	      
//	      ComboValuesVO lObjComboValuesVO = null;
//	      if ((temp != null) && (temp.size() != 0))
//	      {
//	        lLstReturnList = new ArrayList();
//	        
//	        lLstReturnList = new ArrayList();
//	        for (int liCtr = 0; liCtr < temp.size(); liCtr++)
//	        {
//	          Object[] obj = (Object[])temp.get(liCtr);
//	          lObjComboValuesVO = new ComboValuesVO();
//	          lObjComboValuesVO.setId(obj[0].toString());
//	          lObjComboValuesVO.setDesc(obj[1].toString());
//	          lLstReturnList.add(lObjComboValuesVO);
//	        }
//	      }
//	      else
//	      {
//	        lLstReturnList = new ArrayList();
//	        lObjComboValuesVO = new ComboValuesVO();
//	        lObjComboValuesVO.setId("-----Select-----");
//	        lObjComboValuesVO.setDesc("-1");
//	        lLstReturnList.add(lObjComboValuesVO);
//	      }
//	      this.logger.error("List Size" + temp.size());
//	    }
//	    catch (Exception e)
//	    {
//	      this.logger.error("Error in DcpsCommonDAOImpl \n " + e);
//	      e.printStackTrace();
//	    }
//	    return lLstReturnList;
//	  }
//	  
//	  public List getStateLevel()
//	  {
//	    List temp = null;
//	    List lLstReturnList = null;
//	    Session hibSession = getSession();
//	    StringBuilder SBQuery = new StringBuilder();
//	    try
//	    {
//	      SBQuery.append("SELECT LEVEL_ID,LEVEL FROM RLT_PAYBAND_GP_STATE_7PC ORDER BY LEVEL_ID");
//	      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
//	      this.logger.error("sqlQuery getStateLevel--" + sqlQuery.toString());
//	      temp = sqlQuery.list();
//	      
//	      ComboValuesVO lObjComboValuesVO = null;
//	      if ((temp != null) && (temp.size() != 0))
//	      {
//	        lLstReturnList = new ArrayList();
//	        
//	        lLstReturnList = new ArrayList();
//	        for (int liCtr = 0; liCtr < temp.size(); liCtr++)
//	        {
//	          Object[] obj = (Object[])temp.get(liCtr);
//	          lObjComboValuesVO = new ComboValuesVO();
//	          lObjComboValuesVO.setId(obj[0].toString());
//	          lObjComboValuesVO.setDesc(obj[1].toString());
//	          lLstReturnList.add(lObjComboValuesVO);
//	        }
//	      }
//	      else
//	      {
//	        lLstReturnList = new ArrayList();
//	        lObjComboValuesVO = new ComboValuesVO();
//	        lObjComboValuesVO.setId("-----Select-----");
//	        lObjComboValuesVO.setDesc("-1");
//	        lLstReturnList.add(lObjComboValuesVO);
//	      }
//	      this.logger.error("List Size" + temp.size());
//	    }
//	    catch (Exception e)
//	    {
//	      this.logger.error("Error in DcpsCommonDAOImpl \n " + e);
//	      e.printStackTrace();
//	    }
//	    return lLstReturnList;
//	  }
//	  
//	  public List getPIPBForSevenPayEmployee(String scaleId)
//	  {
//	    List lLstReturnList = null;
//	    Session hibSession = getSession();
//	    StringBuilder SBQuery = new StringBuilder();
//	    SBQuery.append(" SELECT SCALE_START_AMT,SCALE_END_AMT,SCALE_GRADE_PAY FROM HR_EIS_SCALE_MST WHERE SCALE_ID ='" + scaleId + "'");
//	    Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
//	    this.gLogger.info("query for getPIPBForSevenPayEmployee " + sqlQuery);
//	    lLstReturnList = sqlQuery.list();
//	    return lLstReturnList;
//	  }
//	  
//	  public String getLevelForPayBand(String payBand, String gradePay)
//	  {
//	    String x = "";
//	    List lLstReturnList = null;
//	    Session hibSession = getSession();
//	    StringBuilder SBQuery = new StringBuilder();
//	    SBQuery.append(" SELECT LEVEL FROM RLT_PAYBAND_GP_7PC WHERE PAY_IN_PAYBAND ='" + payBand + "' AND GRADE_PAY ='" + gradePay + "'");
//	    Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
//	    this.gLogger.info("query for getPIPBForSevenPayEmployee " + sqlQuery);
//	    lLstReturnList = sqlQuery.list();
//	    if ((lLstReturnList.size() > 0) && (lLstReturnList != null)) {
//	      x = lLstReturnList.get(0).toString();
//	    }
//	    return x;
//	  }
//	  
//	  public String getStateLevelForPayBand(String payBand, String gradePay)
//	  {
//	    String x = "";
//	    List lLstReturnList = null;
//	    Session hibSession = getSession();
//	    StringBuilder SBQuery = new StringBuilder();
//	    SBQuery.append(" SELECT LEVEL FROM RLT_PAYBAND_GP_STATE_7PC WHERE PAY_IN_PAYBAND ='" + payBand + "' AND GRADE_PAY ='" + gradePay + "'");
//	    Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
//	    this.gLogger.info("query for getStateLevelForPayBand " + sqlQuery);
//	    lLstReturnList = sqlQuery.list();
//	    if ((lLstReturnList.size() > 0) && (lLstReturnList != null)) {
//	      x = lLstReturnList.get(0).toString();
//	    }
//	    return x;
//	  }
//	  
//	  public int getLevelIdForGivenLevel(String sevenLevel)
//	  {
//	    int x = 0;
//	    List lLstReturnList = null;
//	    Session hibSession = getSession();
//	    StringBuilder SBQuery = new StringBuilder();
//	    SBQuery.append(" SELECT LEVEL_ID FROM RLT_PAYBAND_GP_7PC WHERE LEVEL = '" + sevenLevel + "'");
//	    Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
//	    this.gLogger.info("query for getPIPBForSevenPayEmployee " + sqlQuery);
//	    lLstReturnList = sqlQuery.list();
//	    if ((lLstReturnList.size() > 0) && (lLstReturnList != null)) {
//	      x = Integer.parseInt(lLstReturnList.get(0).toString());
//	    }
//	    return x;
//	  }
//	  
//	  public int getStateLevelIdForGivenLevel(String sevenLevel)
//	  {
//	    int x = 0;
//	    List lLstReturnList = null;
//	    Session hibSession = getSession();
//	    StringBuilder SBQuery = new StringBuilder();
//	    SBQuery.append(" SELECT LEVEL_ID FROM RLT_PAYBAND_GP_STATE_7PC WHERE LEVEL = '" + sevenLevel + "'");
//	    Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
//	    this.gLogger.info("query for getStateLevelIdForGivenLevel " + sqlQuery);
//	    lLstReturnList = sqlQuery.list();
//	    if ((lLstReturnList.size() > 0) && (lLstReturnList != null)) {
//	      x = Integer.parseInt(lLstReturnList.get(0).toString());
//	    }
//	    return x;
//	  }
//	//$t 2019
//		public List getMonths1() {
//
//			String query = "select monthId,monthName from SgvaMonthMst where monthId between  '4' and '12'";//$t 2019
//			List<Object> lLstReturnList = null;
//			StringBuilder sb = new StringBuilder();
//			sb.append(query);
//			Query selectQuery = ghibSession.createQuery(sb.toString());
//			List lLstResult = selectQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//
//			if (lLstResult != null && lLstResult.size() != 0) {
//				lLstReturnList = new ArrayList<Object>();
//				Object obj[];
//				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//					obj = (Object[]) lLstResult.get(liCtr);//(1,January)
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId(obj[0].toString());
//					lObjComboValuesVO.setDesc(obj[1].toString());
//					lLstReturnList.add(lObjComboValuesVO);
//				}
//			} else {
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//			return lLstReturnList;
//		}
//		 public List getYears1()
//		  {
//		    String query = "select finYearId,finYearCode from SgvcFinYearMst where finYearCode between '2016' and '2019' order by finYearCode ASC";
//		    List<Object> lLstReturnList = null;
//		    StringBuilder sb = new StringBuilder();
//		    sb.append(query);
//		    Query selectQuery = this.ghibSession.createQuery(sb.toString());
//		    List lLstResult = selectQuery.list();
//		    ComboValuesVO lObjComboValuesVO = null;
//		    if ((lLstResult != null) && (lLstResult.size() != 0))
//		    {
//		      lLstReturnList = new ArrayList();
//		      for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++)
//		      {
//		        Object[] obj = (Object[])lLstResult.get(liCtr);
//		        lObjComboValuesVO = new ComboValuesVO();
//		        lObjComboValuesVO.setId(obj[0].toString());
//		        lObjComboValuesVO.setDesc(obj[1].toString());
//		        lLstReturnList.add(lObjComboValuesVO);
//		      }
//		    }
//		    else
//		    {
//		      lLstReturnList = new ArrayList();
//		      lObjComboValuesVO = new ComboValuesVO();
//		      lObjComboValuesVO.setId("-1");
//		      lObjComboValuesVO.setDesc("--Select--");
//		      lLstReturnList.add(lObjComboValuesVO);
//		    }
//		    return lLstReturnList;
//		  }
//		public List getFinyears() {
//
//			String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2007' and '2020' order by finYearCode ASC";
//			List<Object> lLstReturnList = null;
//			StringBuilder sb = new StringBuilder();
//			sb.append(query);
//			Query selectQuery = ghibSession.createQuery(sb.toString());
//			List lLstResult = selectQuery.list();
//			ComboValuesVO lObjComboValuesVO = null;
//
//			if (lLstResult != null && lLstResult.size() != 0) {
//				lLstReturnList = new ArrayList<Object>();
//				Object obj[];
//				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
//					obj = (Object[]) lLstResult.get(liCtr);
//					lObjComboValuesVO = new ComboValuesVO();
//					lObjComboValuesVO.setId(obj[0].toString());
//					lObjComboValuesVO.setDesc(obj[1].toString());
//					lLstReturnList.add(lObjComboValuesVO);
//				}
//			} else {
//				lLstReturnList = new ArrayList<Object>();
//				lObjComboValuesVO = new ComboValuesVO();
//				lObjComboValuesVO.setId("-1");
//				lObjComboValuesVO.setDesc("--Select--");
//				lLstReturnList.add(lObjComboValuesVO);
//			}
//			return lLstReturnList;
//		}
//
//	
//}
