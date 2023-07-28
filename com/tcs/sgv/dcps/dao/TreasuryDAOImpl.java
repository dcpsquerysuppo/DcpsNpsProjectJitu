/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jan 31, 2011		Shivani Rana								
 *******************************************************************************
 */


package com.tcs.sgv.dcps.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.ibm.db2.jcc.am.in;
import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContriVoucherDtls;
import com.tcs.sgv.dcps.valueobject.MstDummyOffice;
import com.tcs.sgv.pensionproc.dao.PensionProcComparators;

/**
 * Class Description -
 * 
 * 
 * @author Vihan Khatri
 * @version 0.1
 * @since JDK 5.0 Jan 31, 2011
 */
public class TreasuryDAOImpl extends GenericDaoHibernateImpl implements
TreasuryDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	SessionFactory sessionFactory = null;

	public TreasuryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getAllDDOListForPhyFormRcvd(String lStrPostId,
			String lStrUserType) {

		List listAllForms = null;
		List<ComboValuesVO> listAllFormsCombo = new ArrayList<ComboValuesVO>();
		StringBuilder lSBQuery = new StringBuilder();
		ComboValuesVO cmbVO;
		List lLstResultList = null;
		Iterator itr;
		Object[] obj;
		try {
			//System.out.println("lStrPostId is  " + lStrPostId);
			if (lStrUserType == null) {
				lSBQuery
				.append("select distinct DDO.ddoCode, DDO.ddoName from OrgDdoMst DDO, MstEmp emp,WfJobMst wf");
				lSBQuery
				.append(" WHERE DDO.ddoCode = emp.ddoCode AND wf.jobRefId = emp.dcpsEmpId AND wf.lstActPostId = :postId ");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("postId", lStrPostId);
				listAllForms = lQuery.list();
			} else {
				lSBQuery
				.append("select distinct DDO.ddoCode, DDO.ddoName from OrgDdoMst DDO, MstEmp emp,WfJobMst wf");
				lSBQuery
				.append(" WHERE DDO.ddoCode = emp.ddoCode AND wf.jobRefId = emp.dcpsEmpId AND wf.lstActPostId = :postId ");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("postId", lStrPostId);
				lLstResultList = lQuery.list();
				cmbVO = new ComboValuesVO();
				if (lLstResultList != null && lLstResultList.size() > 0) {
					itr = lLstResultList.iterator();
					while (itr.hasNext()) {

						obj = (Object[]) itr.next();
						cmbVO.setId(obj[0].toString());
						cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
						listAllFormsCombo.add(cmbVO);
					}
				}
			}

		} catch (Exception e) {
			gLogger
			.error("Exception occured from getAllDDOListForPhyFormRcvd of TreasuryDAOImpl is :: "
					+ e);
			e.printStackTrace();
		}
		if (lStrUserType == null) {
			return listAllForms;
		} else {
			return listAllFormsCombo;
		}

	}

	public List getAllFormsForDDO(String lStrDDODode, String lStrPostId) {

		getSession();
		List listAllForms = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery
		.append("SELECT EM.dcpsEmpId,EM.ddoCode,EM.name,EM.gender,EM.dob,");
		lSBQuery.append("EM.designation FROM MstEmp EM, WfJobMst wf");
		lSBQuery
		.append(" WHERE EM.ddoCode =:ddoCode AND  wf.jobRefId = EM.dcpsEmpId AND wf.lstActPostId = :postId ");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("ddoCode", lStrDDODode);
		lQuery.setParameter("postId", lStrPostId);
		listAllForms = lQuery.list();

		return listAllForms;

	}

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

	public List getDummyOffices(String lStrLocationCode,String lStrUse) throws Exception {

		List<ComboValuesVO> lLstOffice = new ArrayList<ComboValuesVO>();
		List lLstResultList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		ComboValuesVO cmbVO;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append("SELECT dummyOfficeId,dummyOfficeName FROM MstDummyOffice WHERE dummyOfficeId IS NOT NULL AND treasury = :treasury  order by dummyOfficeName  ");
			

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("treasury", lStrLocationCode.trim());
			
			logger.info("Query to DUMMY OFFICE heaqder**********"
					+ lSBQuery.toString());
			lLstResultList = hqlQuery.list();
			Collections.sort(lLstResultList,
					new PensionProcComparators.ObjectArrayComparator(false, 1,
							0, 2, 0, true));
			cmbVO = new ComboValuesVO();
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator it = lLstResultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					Object[] obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstOffice.add(cmbVO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.info("Error is  " + e);
		}
		return lLstOffice;

	}

	public List getEmpSearchDeptn(String lStrEmpName,String lStrSevarthId) throws Exception {

		List lLstEmpDeputn = null;
		Query hqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		lStrEmpName = lStrEmpName.toUpperCase();

		try {

			lSBQuery.append(" SELECT emp.dcpsEmpId,emp.dcpsId,emp.name ");
			lSBQuery.append(" FROM MstEmp emp WHERE emp.regStatus IN (1,2)  ");

			/*
			 * if (lStrQuery.equals("Attach")) {
			 * lStrQuery.append(" AND emp.currOff IS NULL "); }
			 * 
			 * if (lStrQuery.equals("Detach")) {
			 * lStrQuery.append(" AND emp.currOff IS NOT NULL "); }
			 */

			if (lStrEmpName != null && lStrEmpName.length() > 1) {
				lSBQuery.append(" AND UPPER(emp.name) = :lStrEmpName ");
			}
			if (lStrSevarthId != null && lStrSevarthId.length() > 1) {
				lSBQuery.append(" AND UPPER(emp.sevarthId) = :lStrSevarthId ");
			}
			hqlQuery = session.createQuery(lSBQuery.toString());

			if (lStrEmpName != null && lStrEmpName.length() > 1) {
				hqlQuery.setParameter("lStrEmpName", lStrEmpName.trim());
			}
			if (lStrSevarthId != null && lStrSevarthId.length() > 1) {
				hqlQuery.setParameter("lStrSevarthId", lStrSevarthId.trim());
			}
			lLstEmpDeputn = hqlQuery.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstEmpDeputn;
	}

	/*
	public List getEmpDeptn(String lStrQuery,String lStrSevarthId,String lStrEmpName) throws Exception {

		List lLstEmpDeptn = null;
		SQLQuery sqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();

		try {
			if (lStrQuery.equals("Attach")) {
				lSBQuery.append(" SELECT emp.dcps_emp_id,emp.DCPS_ID,emp_name,nvl(hed.off_code,'0'),nvl(hed.hst_dcps_empdeptn_id,0),hed.attach_date,hed.reason,hed.remarks,emp.DESELECTION_DATE ");
				lSBQuery.append(" FROM mst_dcps_emp emp LEFT JOIN hst_dcps_emp_deputation hed ON hed.dcps_emp_id = emp.dcps_emp_id ");
				lSBQuery.append(" WHERE emp.REG_STATUS IN (1,2) AND emp.EMP_ON_DEPTN IN (1,2) ");
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND UPPER(emp.sevarth_id) = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND UPPER(emp.emp_name) = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
				lSBQuery.append(" order by emp.emp_name,hed.off_code ASC");
				// 1 for deputed employees and 2 for those who have been de-selected by DDO
			}

			if (lStrQuery.equals("Detach")) {
				lSBQuery.append(" SELECT emp.dcps_emp_id,emp.DCPS_ID,emp_name,hed.off_code,hed.hst_dcps_empdeptn_id,hed.detach_date,hed.reason,hed.remarks,hed.attach_date ");
				lSBQuery.append(" FROM mst_dcps_emp emp JOIN hst_dcps_emp_deputation hed ON hed.dcps_emp_id = emp.dcps_emp_id ");
				lSBQuery.append(" WHERE emp.REG_STATUS IN (1,2) AND emp.EMP_ON_DEPTN = 1 AND hed.detach_date IS NULL ");
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND UPPER(emp.sevarth_id) = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND UPPER(emp.emp_name) = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
				lSBQuery.append(" order by emp.emp_name,hed.off_code ASC");
			}

			sqlQuery = session.createSQLQuery(lSBQuery.toString());
			lLstEmpDeptn = sqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstEmpDeptn;

	}

	 */

	// Above method is the old one. Commented and a new one is written below.
	public List getEmpDeptn(String lStrQuery,String lStrSevarthId,String lStrEmpName,Long gLongLocationCode,Boolean flag) throws Exception {
		System.out.println("flag is***"+flag);
		List lLstEmpDeptn = null;
		SQLQuery sqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();

		try {
			if (lStrQuery.equals("Attach")) {

				lSBQuery.append(" SELECT emp.dcps_emp_id,emp.DCPS_ID,emp_name,nvl(hed.off_code,'0'),nvl(hed.hst_dcps_empdeptn_id,0),hed.attach_date,hed.reason,hed.remarks,emp.DESELECTION_DATE,hed.REASON_DETACH,hed.REMARKS_DETACH,emp.sevarth_id ");
				lSBQuery.append(" FROM mst_dcps_emp emp  ");
				lSBQuery.append(" LEFT JOIN hst_dcps_emp_deputation hed ON hed.dcps_emp_id = emp.dcps_emp_id AND hed.detach_date is NULL");
				// In above line, last check of detach_date null is very important. Never ever change it. 
				lSBQuery.append(" AND hed.LOCATION_CODE = " + gLongLocationCode );

				lSBQuery.append(" WHERE emp.REG_STATUS = 1 AND emp.ddo_code is NULL AND emp.dcps_id is not null  ");
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND emp.sevarth_id = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND emp.emp_name = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
				if(flag==true){
					lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242)");
				}
				else
				{
					lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY =700174 ");
				}
				lSBQuery.append(" order by emp.emp_name,hed.off_code ASC");
				// 1 for deputed employees and 2 for those who have been de-selected by DDO
			}

			if (lStrQuery.equals("Detach")) {

				lSBQuery.append(" SELECT emp.dcps_emp_id,emp.DCPS_ID,emp_name,hed.off_code,hed.hst_dcps_empdeptn_id,hed.detach_date,hed.reason,hed.remarks,hed.attach_date,hed.REASON_DETACH,hed.REMARKS_DETACH,emp.sevarth_id ");
				lSBQuery.append(" FROM mst_dcps_emp emp ");
				lSBQuery.append(" JOIN hst_dcps_emp_deputation hed ON hed.dcps_emp_id = emp.dcps_emp_id ");
				lSBQuery.append(" AND hed.LOCATION_CODE = " + gLongLocationCode );

				lSBQuery.append(" WHERE emp.REG_STATUS = 1 AND emp.ddo_code is NULL AND emp.dcps_id is not null AND hed.detach_date IS NULL ");
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND emp.sevarth_id = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND emp.emp_name = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
				if(flag==true){
					lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242)");
				}
				else
				{
					lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY = 700174 ");
				}
				lSBQuery.append(" order by emp.emp_name,hed.off_code ASC");
			}

			sqlQuery = session.createSQLQuery(lSBQuery.toString());
			lLstEmpDeptn = sqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstEmpDeptn;

	}  /************changed start***************** #here */
	public List getEmpDeptnList(String lStrQuery,String lStrSevarthId,String lStrEmpName,Long gLongLocationCode,Boolean flag,String loadDCPS) throws Exception {
		System.out.println("flag is***"+flag);
		List lLstEmpDeptn = null;
		SQLQuery sqlQuery = null;//lStrQuery=attach,gLongLocationCode=1111,flag=true
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();

		try {
			if (lStrQuery.equals("Attach")) {

				lSBQuery.append(" SELECT emp.dcps_emp_id,emp.DCPS_ID,emp_name,nvl(hed.off_code,'0'),nvl(hed.hst_dcps_empdeptn_id,0),hed.attach_date,hed.reason,hed.remarks,emp.DESELECTION_DATE,hed.REASON_DETACH,hed.REMARKS_DETACH,emp.sevarth_id,emp.DCPS_ID || '/' || NVL(emp.PRAN_NO,'') as id,emp.DDO_CODE,a.end_date,a.start_date  ");
				lSBQuery.append(" FROM mst_dcps_emp emp  ");
				lSBQuery.append(" LEFT outer JOIN hst_dcps_emp_deputation hed ON hed.dcps_emp_id = emp.dcps_emp_id");
				lSBQuery.append(" AND hed.LOCATION_CODE = " + gLongLocationCode );
				lSBQuery.append(" left outer join  (select max(HEI.END_DATE) as end_date ,max(hei.START_DATE) as start_date ,hei.DCPS_EMP_ID");
				lSBQuery.append(" from HST_DCPS_EMP_DETAILS hei,mst_dcps_emp m  where hei.DCPS_EMP_ID=m.dcps_emp_id  ");
				// In above line, last check of detach_date null is very important. Never ever change it. 
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND m.sevarth_id = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND m.emp_name = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
				lSBQuery.append(" group by hei.DCPS_EMP_ID ) a on a.DCPS_EMP_ID=emp.dcps_emp_id ");

				lSBQuery.append(" WHERE emp.REG_STATUS = 1 and emp.DCPS_OR_GPF='Y' AND emp.dcps_id is not null and (emp.DDO_CODE is null or substr(emp.DDO_CODE,1,4)='" + gLongLocationCode+"') ");
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND emp.sevarth_id = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND emp.emp_name = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
				if(flag==true){
					
					if(loadDCPS!=null && !loadDCPS.equalsIgnoreCase("") && "YES".equalsIgnoreCase(loadDCPS))
					{
						lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242,700174)");
						lSBQuery.append(" and emp.DOJ < '2015-04-01' ");
					}else
					{
						lSBQuery.append(" AND ((emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and emp.DOJ >='2004-01-01') ");
						lSBQuery.append(" or (emp.AC_DCPS_MAINTAINED_BY in (700174)  and emp.DOJ >='2005-11-01')) ");
					}
				}
				else
				{
					lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY =700174 ");
				}
				
				lSBQuery.append(" order by emp.emp_name,hed.off_code ASC");
				// 1 for deputed employees and 2 for those who have been de-selected by DDO
			}

			if (lStrQuery.equals("Detach")) {

				lSBQuery.append(" SELECT emp.dcps_emp_id,emp.DCPS_ID,emp_name,hed.off_code,hed.hst_dcps_empdeptn_id,hed.detach_date,hed.reason,hed.remarks,hed.attach_date,hed.REASON_DETACH,hed.REMARKS_DETACH,emp.sevarth_id,emp.DCPS_ID || '/' || NVL(emp.PRAN_NO,'') as id,emp.DDO_CODE ");
				lSBQuery.append(" FROM mst_dcps_emp emp ");
				lSBQuery.append(" JOIN hst_dcps_emp_deputation hed ON hed.dcps_emp_id = emp.dcps_emp_id and hed.REASON_DETACH is null ");
				lSBQuery.append(" AND hed.LOCATION_CODE = " + gLongLocationCode );

				lSBQuery.append(" WHERE emp.REG_STATUS = 1 AND (emp.DDO_CODE is null or substr(emp.DDO_CODE,1,4)='" + gLongLocationCode+"')  AND emp.dcps_id is not null ");
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND emp.sevarth_id = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND emp.emp_name = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
				if(flag==true){
					if(loadDCPS!=null && !loadDCPS.equalsIgnoreCase("") && "YES".equalsIgnoreCase(loadDCPS))
					{
						lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242,700174)");
						lSBQuery.append(" and emp.DOJ < '2015-04-01' ");
					}else
					{
						lSBQuery.append(" AND ((emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and emp.DOJ >='2004-01-01') ");
						lSBQuery.append(" or (emp.AC_DCPS_MAINTAINED_BY in (700174)  and emp.DOJ >='2005-11-01')) ");
					}
				}
				else
				{
					lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY = 700174 ");
					
					
				}
				lSBQuery.append(" order by emp.emp_name,hed.off_code ASC");
			}

			sqlQuery = session.createSQLQuery(lSBQuery.toString());
			gLogger.info("sqlQuery :"+sqlQuery);
			lLstEmpDeptn = sqlQuery.list();
			gLogger.info("lLstEmpDeptn :"+lLstEmpDeptn);

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstEmpDeptn;
	}
	public List getEmpDeptnListAll(String lStrQuery,Long gLongLocationCode,Boolean flag,String loadDCPS) throws Exception {
		System.out.println("flag is***"+flag);
		List lLstEmpDeptn = null;
		SQLQuery sqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
//		gLogger.info("############################&&loadDCPS is :"+loadDCPS);
//		////$t 2020 1-13
//		SessionFactory sessionFactory1=new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
//        Session neardrSession = sessionFactory1.openSession();
//        //
        
		try {
			if (lStrQuery.equals("Attach")) {
				
				gLogger.info("############################&&IN ATTACH");

				lSBQuery.append(" SELECT emp.dcps_emp_id,emp.DCPS_ID,emp_name,nvl(hed.off_code,'0'),nvl(hed.hst_dcps_empdeptn_id,0),hed.attach_date,hed.reason,hed.remarks,emp.DESELECTION_DATE,hed.REASON_DETACH,hed.REMARKS_DETACH,emp.sevarth_id,emp.DCPS_ID || '/' || NVL(emp.PRAN_NO,'') as id,emp.DDO_CODE,a.end_date,a.start_date ");
				lSBQuery.append(" FROM mst_dcps_emp emp  ");
				lSBQuery.append(" LEFT outer JOIN hst_dcps_emp_deputation hed ON hed.dcps_emp_id = emp.dcps_emp_id ");
				lSBQuery.append(" AND hed.LOCATION_CODE = " + gLongLocationCode );
				lSBQuery.append(" left outer join  (select max(HEI.END_DATE) as end_date ,max(hei.START_DATE) as start_date ,hei.DCPS_EMP_ID");
				lSBQuery.append(" from HST_DCPS_EMP_DETAILS hei  ");
				// In above line, last check of detach_date null is very important. Never ever change it. 
			
				lSBQuery.append(" group by hei.DCPS_EMP_ID ) a on a.DCPS_EMP_ID=emp.dcps_emp_id ");

				lSBQuery.append(" WHERE emp.REG_STATUS = 1 and emp.DCPS_OR_GPF='Y' AND emp.dcps_id is not null and (emp.DDO_CODE is null or substr(emp.DDO_CODE,1,4)='" + gLongLocationCode+"') ");
			
				if(flag==true){
					
					if(loadDCPS!=null && !loadDCPS.equalsIgnoreCase("") && "YES".equalsIgnoreCase(loadDCPS))
					{
						lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242,700174)");
						lSBQuery.append(" and emp.DOJ < '2015-04-01' ");
					}else
					{
						lSBQuery.append(" AND ((emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and emp.DOJ >='2004-01-01') ");
						lSBQuery.append(" or (emp.AC_DCPS_MAINTAINED_BY in (700174)  and emp.DOJ >='2005-11-01')) ");
					}
				}
				else
				{
					lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY =700174 ");
				}
				
			
				lSBQuery.append(" order by emp.emp_name ASC");
				// 1 for deputed employees and 2 for those who have been de-selected by DDO
			}

			if (lStrQuery.equals("Detach")) {
				gLogger.info("############################&&IN DETACH");
				lSBQuery.append(" SELECT emp.dcps_emp_id,emp.DCPS_ID,emp_name,hed.off_code,hed.hst_dcps_empdeptn_id,hed.detach_date,hed.reason,hed.remarks,hed.attach_date,hed.REASON_DETACH,hed.REMARKS_DETACH,emp.sevarth_id,emp.DCPS_ID || '/' || NVL(emp.PRAN_NO,'') as id,emp.DDO_CODE ");
				lSBQuery.append(" FROM mst_dcps_emp emp ");
				lSBQuery.append(" JOIN hst_dcps_emp_deputation hed ON hed.dcps_emp_id = emp.dcps_emp_id and hed.REASON_DETACH is null ");
				lSBQuery.append(" AND hed.LOCATION_CODE = " + gLongLocationCode );

				lSBQuery.append(" WHERE emp.REG_STATUS = 1 AND   emp.DCPS_OR_GPF='Y' and (emp.DDO_CODE is null or substr(emp.DDO_CODE,1,4)='" + gLongLocationCode+"') AND emp.dcps_id is not null  ");
				
				if(flag==true){
					
					if(loadDCPS!=null && !loadDCPS.equalsIgnoreCase("") && "YES".equalsIgnoreCase(loadDCPS))
					{
						lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242,700174)");
						lSBQuery.append(" and emp.DOJ < '2015-04-01' ");
					}else
					{
						lSBQuery.append(" AND ((emp.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and emp.DOJ >='2004-01-01') ");
						lSBQuery.append(" or (emp.AC_DCPS_MAINTAINED_BY in (700174)  and emp.DOJ >='2005-11-01')) ");
					}
				}
				else
				{
					lSBQuery.append(" AND emp.AC_DCPS_MAINTAINED_BY = 700174 ");
				}
				
				
				
				lSBQuery.append(" order by emp.emp_name,hed.off_code ASC");
			}
			gLogger.info("#####################&&Query is :"+lSBQuery.toString());
			sqlQuery = session.createSQLQuery(lSBQuery.toString());
			lLstEmpDeptn = sqlQuery.list();
		    ////$t 2020 1-13
//			sqlQuery = neardrSession.createSQLQuery(lSBQuery.toString());
//			lLstEmpDeptn = sqlQuery.list();
//			//
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}finally{
			gLogger.info("bf neardrSession.close();");
			//neardrSession.close();
			gLogger.info("af neardrSession.close();");
		}
		return lLstEmpDeptn;

	}
	public String getEmpDDOCode(String lStrSevarthId,String lStrEmpName,Long gLongLocationCode,Boolean flag) throws Exception {
		System.out.println("flag is***"+flag);
		List lLstEmpDeptn = null;
		SQLQuery sqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		String ddoCode="";

		try {
				lSBQuery.append(" SELECT emp.ddo_code");
				lSBQuery.append(" FROM mst_dcps_emp emp  ");
			
				lSBQuery.append(" WHERE 1=1  ");
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND emp.sevarth_id = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND emp.emp_name = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
			sqlQuery = session.createSQLQuery(lSBQuery.toString());
			gLogger.info("getEmpDDOCode *****" + sqlQuery);
			lLstEmpDeptn = sqlQuery.list();
			if(lLstEmpDeptn!=null &&  lLstEmpDeptn.size()>0 && lLstEmpDeptn.get(0)!=null && !"".equalsIgnoreCase(lLstEmpDeptn.get(0).toString()))
			{
				ddoCode=lLstEmpDeptn.get(0).toString();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return ddoCode;

	}
	public Boolean chkIfDdoOfHodDefined(String lStrSevarthId,String lStrEmpName) throws Exception {
		
		List lLstEmpDeptn = null;
		SQLQuery sqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		String ddoCode="";
		boolean returnFlag=false;

		try {
				lSBQuery.append(" SELECT hod.DDO_CODE FROM mst_dcps_emp mst,ACL_HODDDO_RLT hod,ORG_DDO_MST ddo where mst.PARENT_DEPT=ddo.hod_loc_code and ddo.dept_loc_code=hod.HOD_LOC_ID and hod.ACTIVATE_FLAG=1   ");
			
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND mst.sevarth_id = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND mst.emp_name = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
			sqlQuery = session.createSQLQuery(lSBQuery.toString());
			lLstEmpDeptn = sqlQuery.list();
			if(lLstEmpDeptn!=null && lLstEmpDeptn.size()>0 && lLstEmpDeptn.get(0)!=null && !"".equalsIgnoreCase(lLstEmpDeptn.get(0).toString()))
			{
				returnFlag=true;
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return returnFlag;

	}
public String getEmpDept (String lStrSevarthId,String lStrEmpName) throws Exception {
		
		List lLstEmpDeptn = null;
		SQLQuery sqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		String depTname="";
		boolean returnFlag=false;

		try {
				lSBQuery.append(" SELECT cmn1.LOC_NAME FROM mst_dcps_emp emp inner join CMN_LOCATION_MST cmn on cmn.loc_id = emp.PARENT_DEPT inner join CMN_LOCATION_MST cmn1 on cmn1.loc_id = cmn.PARENT_LOC_ID   ");
			
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND emp.sevarth_id = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND emp.emp_name = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
			sqlQuery = session.createSQLQuery(lSBQuery.toString());
			gLogger.info("script for getEmpDept---------"+lSBQuery.toString() );
			lLstEmpDeptn = sqlQuery.list();
			if(lLstEmpDeptn!=null && lLstEmpDeptn.size()>0 && lLstEmpDeptn.get(0)!=null && !"".equalsIgnoreCase(lLstEmpDeptn.get(0).toString()))
			{
				depTname=lLstEmpDeptn.get(0).toString();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return depTname;

	}
/************changed end***************** #here */
	public Long getHstEmpDeputationPkVal(Long lLngDcpsEmpId) throws Exception {
		Long lLngHstDcpsEmpId = null;
		try {

			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
			.append(" SELECT HED.hstdcpsEmpDeptnId FROM  HstEmpDeputation HED");
			lSBQuery.append(" WHERE HED.dcpsEmpId.dcpsEmpId = :lLngDcpsEmpId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLngDcpsEmpId", lLngDcpsEmpId);

			lLngHstDcpsEmpId = (Long) lQuery.list().get(0);

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLngHstDcpsEmpId;
	}

	public List getEmpListForSixPCArrearsYearlyTO(String lStrDDOCode,
			Long finYearId, String lStrPostId) {

		List empList = null;
		try {
			StringBuilder SBQuery = new StringBuilder();

			SBQuery
			.append("SELECT fy.fin_year_desc,EM.DCPS_ID,EM.Emp_name,SPC.TOTAL_AMOUNT,SPC.AMOUNT_PAID,nvl(YPC.YEARLY_AMOUNT,0),nvl(ypc.DCPS_SIXPC_YEARLY_ID,0),EM.DCPS_EMP_ID,fy.fin_year_id,SPC.dcps_sixpc_id,YPC.status_flag");
			SBQuery
			.append(" FROM sgvc_fin_year_mst fy,mst_dcps_emp EM,wf_job_mst job, ");
			SBQuery
			.append(" mst_dcps_sixpc SPC LEFT OUTER JOIN rlt_dcps_sixpc_yearly YPC ON SPC.dcps_emp_id=YPC.dcps_emp_id and YPC.fin_year_id="
					+ finYearId);
			SBQuery.append(" WHERE fy.fin_year_id =" + finYearId);
			SBQuery
			.append(" AND job.lst_act_post_id = '"
					+ lStrPostId
					+ "' AND job.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID AND YPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND EM.REG_STATUS = 1   AND EM.DDO_CODE= '"
					+ lStrDDOCode + "'");

			Query stQuery = ghibSession.createSQLQuery(SBQuery.toString());

			empList = stQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return empList;

	}

	public List getYears() throws Exception {

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

	public List getDummyOfficesList(String lStrLocationCode) throws Exception {

		List lLstDummyOfficeDtls = null;
		StringBuilder lSBQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT dummy.dummyOfficeId,dummy.dummyOfficeName,dummy.statusFlag FROM MstDummyOffice dummy where dummy.treasury = :treasury order by dummy.dummyOfficeId");
			Query stQuery = ghibSession.createQuery(lSBQuery.toString());
			stQuery.setParameter("treasury", lStrLocationCode.trim());
			lLstDummyOfficeDtls = stQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstDummyOfficeDtls;
	}

	public MstDummyOffice getDummyOfficeInfo(String dummyOfficeId,String lStrTreasuryCode) throws Exception {

		MstDummyOffice lObjDummyOffice = null;
		StringBuilder lSBQuery = null;

		try {

			lSBQuery = new StringBuilder();
			lSBQuery.append("FROM MstDummyOffice where dummyOfficeId= :dummyOfficeId");
			lSBQuery.append(" and treasury = :treasury");
			Query stQuery = ghibSession.createQuery(lSBQuery.toString());
			stQuery.setParameter("dummyOfficeId", dummyOfficeId);
			stQuery.setParameter("treasury", lStrTreasuryCode);

			lObjDummyOffice = (MstDummyOffice) stQuery.uniqueResult();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lObjDummyOffice;
	}

	public List getEmployeesFromDummyOffice(String dummyOfficeId,Long monthId,Long yearId,String gStrLocationCode,String lStrSubmittedOrNot,String lStrUse,String Irrigation) throws Exception {

		List lLstDummyOfficeDtls = null;
		StringBuilder lSBQuery = null;
		////////$t 16-12-2020 IRRI
		StringBuilder lSBQuery2 = null;
		List dateList = null;
		String finDate = "";

		try {
		    ////$t 16-12-2020 IRRI
			lSBQuery2 = new StringBuilder();
			lSBQuery2.append(" SELECT fin.FIN_YEAR_CODE FROM SGVC_FIN_YEAR_MST fin where fin.FIN_YEAR_ID= '"+yearId+"' ");
			Query lQuery2 = this.ghibSession.createSQLQuery(lSBQuery2.toString());
			dateList = lQuery2.list();
			if ((dateList != null) && (dateList.size() > 0) && (dateList.get(0) != null)) {
			finDate = dateList.get(0).toString();//2013-05-31 00:00:00.0
			if(monthId==12){
			finDate=finDate+"-"+(monthId)+"-01";
			}else if(monthId>3){
			finDate=finDate+"-"+(monthId+1)+"-01";
			}else{
			finDate=(Integer.parseInt(finDate)+1)+"-"+(monthId+1)+"-01";
			}
			}//if
			
			lSBQuery = new StringBuilder();
			//lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),'','',0,0,'','',0,'','',EM.PAY_COMMISSION,'', BIGINT(EM.BASIC_PAY),0,0,nvl(EM.doj,'')  ");//nvl(DC.DCPS_DEPTN_CONTRI_ID,0)
			lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),BIGINT(nvl(EM.SEVEN_PC_BASIC,0)),EM.AC_DCPS_MAINTAINED_BY,"
					+ "cast(decode(DCC.STATUS,'N','10','M','20','H','30','A','40','G','50','D',15) as bigint) ");////25-11-2020 EM.SEVEN_PC_BASIC=null ////$t irri 13-1-2021
			lSBQuery.append(" FROM mst_dcps_emp EM ");
			lSBQuery.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
			lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY");
			lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID AND");
			lSBQuery.append(" DC.FIN_YEAR_ID = :yearId AND DC.MONTH_ID = :monthId");
		    ////$t 20-04-2020 IRRI
			lSBQuery.append(" LEFT JOIN trn_dcps_contribution DCC ON DC.DCPS_EMP_ID = DCC.DCPS_EMP_ID ");
			lSBQuery.append(" AND DCC.FIN_YEAR_ID = :yearId AND DCC.MONTH_ID = :monthId");
			lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID = :dummyOfficeId ");
			lSBQuery.append(" AND EM.doj<'"+finDate+"' ");
			lSBQuery.append(" AND DF.TREASURY = :gStrLocationCode ");
			lSBQuery.append(" AND EM.doj<='2015-03-31' and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null ");
			if(Irrigation.equals("yes")){
				lSBQuery.append(" AND (DCC.status in ('N','A','G')) ");
				}else{
				//lSBQuery.append(" AND (DCC.status='G' or DCC.status is null)  ");//or DCC.status is null	
				lSBQuery.append(" AND ((DCC.status in ('A','G')) or (DCC.status is null))  ");//or DCC.status is null
				}
		    ////$t 
			
			/*if(lStrUse.equals("WithoutEmplrContri"))
			{
				lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI = 'Y' ");
			}
			else
			{
				lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI IS NULL");
			}*/

			Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			
			logger.info("Query to EMPLOYEELIST heaqder**********"
					+ lSBQuery.toString()); 
			
			stQuery.setParameter("dummyOfficeId", dummyOfficeId);
			stQuery.setParameter("gStrLocationCode", gStrLocationCode);
			stQuery.setParameter("yearId", yearId);
		    stQuery.setParameter("monthId", monthId);
			lLstDummyOfficeDtls = stQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstDummyOfficeDtls;
	}
	/*//$t 2019 16-11
	public List getEmployeesFromDummyOfficeNPS(String dummyOfficeId,Long monthId,Long yearId,String gStrLocationCode,String lStrSubmittedOrNot,String lStrUse) throws Exception {

		List lLstDummyOfficeDtls = null;
		StringBuilder lSBQuery = null;
		String fDate="";
		logger.info("Query to monthId in  heaqder**********"+ monthId);
		logger.info("Query to year in  heaqder**********"+ yearId);
		if(monthId.equals("4") && yearId.equals("2019")){
			fDate="2019-04-30";
		}else if(monthId.equals("5") && yearId.equals("2019")){
			fDate="2019-05-31";
		}
		else if(monthId.equals("6") && yearId.equals("2019")){
			fDate="2019-06-30";
		}
		else if(monthId.equals("7") && yearId.equals("2019")){
			fDate="2019-07-31";
		}
		else if(monthId.equals("8") && yearId.equals("2019")){
			fDate="2019-08-31";
		}
		else if(monthId.equals("9") && yearId.equals("2019")){
			fDate="2019-09-30";
		}
//        String date="2019-0"+monthId+"01";
//		//String date = "1/13/2012";
//		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//		Date convertedDate = dateFormat.parse(date);
//		Calendar c = Calendar.getInstance();
//		c.setTime(convertedDate);
//		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		
      //$t 2019
		try {
			lSBQuery = new StringBuilder();
			//lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),'','',0,0,'','',0,'','',EM.PAY_COMMISSION,'', BIGINT(EM.BASIC_PAY),0,0,nvl(EM.doj,'')  ");//nvl(DC.DCPS_DEPTN_CONTRI_ID,0)
			lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),EM.SEVEN_PC_BASIC,EM.AC_DCPS_MAINTAINED_BY ");
			lSBQuery.append(" FROM mst_dcps_emp EM ");
			lSBQuery.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
			lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY ");
			lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID and ");
			lSBQuery.append(" DC.FIN_YEAR_ID = :yearId AND DC.MONTH_ID = :monthId");
			lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID = :dummyOfficeId ");
			lSBQuery.append(" AND DF.TREASURY = :gStrLocationCode ");
			lSBQuery.append("  and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null and (dc.IS_ARREARS is null or dc.IS_ARREARS='S') "); //$t 2019
			lSBQuery.append(" AND ((EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') ");
			lSBQuery.append(" or (EM.AC_DCPS_MAINTAINED_BY in (700174)  and EM.DOJ >='2005-11-01')) ");
			
			
			//lSBQuery.append(" AND em.dcps_emp_id in (SELECT trn.dcps_emp_id FROM TRN_DCPS_CONTRIBUTION trn where trn.STARTDATE>='2019-04-01' and trn.ENDDATE<='2019-08-31' and trn.IS_ARREARS<>'Y' ) ");
			
			//shw
			//and (trn.CONTRIBUTION_EMPLR !> 0 or trn.CONTRIBUTION_EMPLR is null or trn.CONTRIBUTION=trn.CONTRIBUTION_EMPLR )
			
			if(lStrUse.equals("WithoutEmplrContri"))
			{
				lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI = 'Y' ");
			}
			else
			{
				lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI IS NULL");
			}

			Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			logger.info("Query to getEmployeesFromDummyOfficeNPS in  heaqder**********"
					+ lSBQuery.toString());
			stQuery.setParameter("dummyOfficeId", dummyOfficeId);
			stQuery.setParameter("gStrLocationCode", gStrLocationCode);
		    stQuery.setParameter("yearId", yearId);
		    stQuery.setParameter("monthId", monthId);
			lLstDummyOfficeDtls = stQuery.list();
			
			System.out.println("count-->"+lLstDummyOfficeDtls.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstDummyOfficeDtls;
	}
*/
//$t 2019 18-11
	public List getEmployeesFromDummyOfficeNPS(String dummyOfficeId,Long monthId,Long yearId,String gStrLocationCode,String lStrSubmittedOrNot,String lStrUse,String Irrigation) throws Exception {

		List lLstDummyOfficeDtls = null;
		StringBuilder lSBQuery = null;
		StringBuilder lSBQuery2 = null;
		List dateList = null;
		String finDate = "";
		
      //$t 2019
		try {
		    ////$t 16-12-2020 IRRI
			lSBQuery2 = new StringBuilder();
			lSBQuery2.append(" SELECT fin.FIN_YEAR_CODE FROM SGVC_FIN_YEAR_MST fin where fin.FIN_YEAR_ID= '"+yearId+"' ");
			Query lQuery2 = this.ghibSession.createSQLQuery(lSBQuery2.toString());
			dateList = lQuery2.list();
			if ((dateList != null) && (dateList.size() > 0) && (dateList.get(0) != null)) {
			finDate = dateList.get(0).toString();//2013-05-31 00:00:00.0
			if(monthId==12){
			finDate=finDate+"-"+(monthId)+"-01";
			}else if(monthId>3){
			finDate=finDate+"-"+(monthId+1)+"-01";
			}else{
			finDate=(Integer.parseInt(finDate)+1)+"-"+(monthId+1)+"-01";
			}
			}//if
			//$t
			
			lSBQuery = new StringBuilder();
			//lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),'','',0,0,'','',0,'','',EM.PAY_COMMISSION,'', BIGINT(EM.BASIC_PAY),0,0,nvl(EM.doj,'')  ");//nvl(DC.DCPS_DEPTN_CONTRI_ID,0)
			lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),BIGINT(nvl(EM.SEVEN_PC_BASIC,0)),EM.AC_DCPS_MAINTAINED_BY,cast(decode(DCC.STATUS,'N','10','M','20','H','30','D',15) as bigint) ");////25-11-2020 EM.SEVEN_PC_BASIC=null ////$t irri 13-1-2021
			lSBQuery.append(" FROM mst_dcps_emp EM ");
			lSBQuery.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
			lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY ");
			lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID ");
			//+ " and  DC.DCPS_EMP_ID not In (SELECT dcps_emp_id FROM HST_DCPS_EMP_DEPUTATION where DETACH_DATE between '2019-04-01' and '2019-09-30' and location_CODE='"+gStrLocationCode+"') ");
			//lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID  "); //original Line
			lSBQuery.append(" AND DC.FIN_YEAR_ID = :yearId AND DC.MONTH_ID = :monthId");
			////$t 20-04-2020 IRRI
			lSBQuery.append(" LEFT JOIN trn_dcps_contribution DCC ON DC.DCPS_EMP_ID = DCC.DCPS_EMP_ID ");
			lSBQuery.append(" AND DCC.FIN_YEAR_ID = :yearId AND DCC.MONTH_ID = :monthId");
			lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID = :dummyOfficeId ");
			lSBQuery.append(" AND EM.doj<'"+finDate+"' ");//////$t 16-12-2020 IRRI
			lSBQuery.append(" AND DF.TREASURY = :gStrLocationCode ");
			lSBQuery.append("  and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null and (dc.IS_ARREARS is null or dc.IS_ARREARS='S') "); //$t 2019
			lSBQuery.append(" AND ((EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') ");
			lSBQuery.append(" or (EM.AC_DCPS_MAINTAINED_BY in (700174)  and EM.DOJ >='2005-11-01')) ");
		    ////$t 20-04-2020 IRRI
			//lSBQuery.append(" AND (DCC.status in ('N','H') or DCC.status is null) ");
			if(Irrigation.equals("yes")){
				lSBQuery.append(" AND (DCC.status in ('N','H')) ");
				}else{
				lSBQuery.append(" AND (DCC.status='H' or DCC.status is null)  ");//or DCC.status is null
				}
		    ////$t 		
					
			Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			logger.info("Query to getEmployeesFromDummyOfficeNPS in  heaqder**********"
					+ lSBQuery.toString());
			stQuery.setParameter("dummyOfficeId", dummyOfficeId);
			stQuery.setParameter("gStrLocationCode", gStrLocationCode);
		    stQuery.setParameter("yearId", yearId);
		    stQuery.setParameter("monthId", monthId);
			lLstDummyOfficeDtls = stQuery.list();
			
			System.out.println("count-->"+lLstDummyOfficeDtls.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstDummyOfficeDtls;
	}

	
//	//$t 2019 20-11
//		public List getEmployeesFromDummyOfficeNPS(String dummyOfficeId,Long monthId,Long yearId,String gStrLocationCode,String lStrSubmittedOrNot,String lStrUse) throws Exception {
//			List lLstDummyOfficeDtls = null;
//			List lLstAttachDateEmp = null;
//			List lLstCheckBillEmp = null;
//			List lLstFinalEmp = null;
//			
//			StringBuilder lSBQuery = null;
//			try {
//				lSBQuery = new StringBuilder();
//				//lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),'','',0,0,'','',0,'','',EM.PAY_COMMISSION,'', BIGINT(EM.BASIC_PAY),0,0,nvl(EM.doj,'')  ");//nvl(DC.DCPS_DEPTN_CONTRI_ID,0)
//				lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),EM.SEVEN_PC_BASIC,EM.AC_DCPS_MAINTAINED_BY ");
//				lSBQuery.append(" FROM mst_dcps_emp EM ");
//				lSBQuery.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
//				lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY ");
//				lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID "
//						+ " and  DC.DCPS_EMP_ID not In (SELECT dcps_emp_id FROM HST_DCPS_EMP_DEPUTATION where DETACH_DATE between '2019-04-01' and '2019-09-30' and location_CODE='"+gStrLocationCode+"') ");
//				//lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID  "); //original Line
//				lSBQuery.append(" AND DC.FIN_YEAR_ID = :yearId AND DC.MONTH_ID = :monthId");
//				lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID = :dummyOfficeId ");
//				lSBQuery.append(" AND DF.TREASURY = :gStrLocationCode ");
//				lSBQuery.append("  and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null and (dc.IS_ARREARS is null or dc.IS_ARREARS='S') "); //$t 2019
//				lSBQuery.append(" AND ((EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') ");
//				lSBQuery.append(" or (EM.AC_DCPS_MAINTAINED_BY in (700174)  and EM.DOJ >='2005-11-01')) ");
//				
//				Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
//				logger.info("Query to getEmployeesFromDummyOfficeNPS in  heaqder**********"+ lSBQuery.toString());
//				stQuery.setParameter("dummyOfficeId", dummyOfficeId);
//				stQuery.setParameter("gStrLocationCode", gStrLocationCode);
//			    stQuery.setParameter("yearId", yearId);
//			    stQuery.setParameter("monthId", monthId);
//				lLstDummyOfficeDtls = stQuery.list();
//				
//				//System.out.println("count-->"+lLstDummyOfficeDtls.size());
//				logger.info("count-->**********"+lLstDummyOfficeDtls.size());
//				
//				StringBuilder lSBQuery1 = new StringBuilder();
//				lSBQuery1.append(" SELECT HD.dcps_emp_id FROM hst_dcps_emp_deputation HD ");
//				lSBQuery1.append(" where location_CODE='"+gStrLocationCode+"' ");
//				lSBQuery1.append(" and hd.ATTACH_DATE between '2019-04-01' and '2019-09-30' and HD.REASON_DETACH is null and HD.DETACH_DATE is null	 ");
//				Query stQuery1 = ghibSession.createSQLQuery(lSBQuery1.toString());
//				logger.info("Query to attach date '2019-04-01' and '2019-09-30'  getEmployeesFromDummyOfficeNPS**********"+ lSBQuery1.toString());
//			    lLstAttachDateEmp = stQuery1.list();
//			    logger.info("count lLstAttachDateEmp-->**********"+lLstAttachDateEmp.size());
//			    
//			    
//			    if(lLstAttachDateEmp!=null && lLstAttachDateEmp.size()>0)
//				{
//					Object obj[];
//					Long bfMonth=monthId-1;
//					
//					for (int liCtr = 0; liCtr < lLstAttachDateEmp.size(); liCtr++) {
//						obj = (Object[]) lLstAttachDateEmp.get(liCtr);
//						Long dcpsEmpId=Long.parseLong(obj[0].toString());
//						
//						StringBuilder lSBQuery2 = new StringBuilder();
//						lSBQuery2.append(" SELECT m.DCPS_EMP_ID ");
//						lSBQuery2.append(" FROM mst_dcps_emp m ");
//						lSBQuery2.append(" join HR_EIS_EMP_MST hr on m.org_emp_mst_id = hr.EMP_MPG_ID ");
//						lSBQuery2.append(" join HR_PAY_PAYBILL hp  on hp.emp_id = hr.emp_id ");
//						lSBQuery2.append(" join PAYBILL_HEAD_MPG p on p.PAYBILL_ID=hp.paybill_grp_id ");
//						lSBQuery2.append(" where m.DCPS_EMP_ID='"+dcpsEmpId+"' and p.PAYBILL_YEAR='2019' and p.PAYBILL_MONTH='"+bfMonth+"' and p.APPROVE_FLAG IN(0,5) ");
//						
//						Query stQuery2 = ghibSession.createSQLQuery(lSBQuery2.toString());
//						logger.info("Query lLstCheckBillEmp  getEmployeesFromDummyOfficeNPS**********"+ lSBQuery2.toString());
//					    lLstCheckBillEmp = stQuery2.list();
//					    logger.info("count lLstCheckBillEmp-->**********"+lLstCheckBillEmp.size());
//					    }
//					}
//			    
//			      if(lLstCheckBillEmp!=null && lLstCheckBillEmp.size()>0)
//				   {
//					Object obj[];
//					for (int liCtr = 0; liCtr < lLstFinalEmp.size(); liCtr++) {
//						obj = (Object[]) lLstFinalEmp.get(liCtr);
//						Long dcpsEmpId=Long.parseLong(obj[0].toString());
//						
//						StringBuilder lSBQuery3 = new StringBuilder();
//						lSBQuery3.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),EM.SEVEN_PC_BASIC,EM.AC_DCPS_MAINTAINED_BY ");
//						lSBQuery3.append(" FROM mst_dcps_emp EM ");
//						lSBQuery3.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
//						lSBQuery3.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY ");
//						lSBQuery3.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID  "); 
//						lSBQuery3.append(" AND DC.DCPS_EMP_ID='"+dcpsEmpId+"' AND  DC.FIN_YEAR_ID ='"+yearId+"' AND DC.MONTH_ID ='"+monthId+"' ");
//						lSBQuery3.append(" WHERE DF.DUMMYOFFICE_ID ='"+dummyOfficeId+"' ");
//						lSBQuery3.append(" AND DF.TREASURY ='"+gStrLocationCode+"' ");
//						lSBQuery3.append("  and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null and (dc.IS_ARREARS is null or dc.IS_ARREARS='S') "); 
//						lSBQuery3.append(" AND ((EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') ");
//						lSBQuery3.append(" or (EM.AC_DCPS_MAINTAINED_BY in (700174)  and EM.DOJ >='2005-11-01')) ");
//						
//						Query stQuery3 = ghibSession.createSQLQuery(lSBQuery3.toString());
//						logger.info("Query lLstFinalEmp  getEmployeesFromDummyOfficeNPS**********"+ lSBQuery3.toString());
//						lLstFinalEmp = stQuery3.list();
//					    logger.info("count lLstFinalEmp-->**********"+lLstFinalEmp.size());
//					    }
//					}
//			      
//			      if ((lLstFinalEmp != null && lLstFinalEmp.size() > 0)) {
//			    	  lLstDummyOfficeDtls.addAll(lLstFinalEmp);
//			    	  }
//			} catch (Exception e) {
//				e.printStackTrace();
//				logger.error("Error is :" + e, e);
//			}
//			return lLstDummyOfficeDtls;
//		}

	
	
	public List getEmployeesFromDummyOfficeNPSFour(String dummyOfficeId,Long monthId,Long yearId,String gStrLocationCode,String lStrSubmittedOrNot,String lStrUse) throws Exception {

		
		List lLstDummyOfficeDtls = null;
		StringBuilder lSBQuery = null;
		//$t 2019 '2019-09-30'
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" (SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''), ");
			lSBQuery.append(" DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''), ");
			lSBQuery.append(" DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''), ");
			lSBQuery.append(" EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)), ");
			lSBQuery.append(" BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),EM.SEVEN_PC_BASIC,EM.AC_DCPS_MAINTAINED_BY,case when DC.IS_ARREARS is null then 'X' else DC.IS_ARREARS end ");
			lSBQuery.append(" FROM mst_dcps_emp EM JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id ");
			lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY ");
			lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID AND DC.FIN_YEAR_ID =" + yearId +" AND DC.MONTH_ID =" + monthId + " ");
			lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID ='" + dummyOfficeId +"' AND DF.TREASURY ='" + gStrLocationCode +"' and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null ");
			lSBQuery.append(" and DC.startdate between '2019-04-01' and '2019-09-30' AND((EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) " 
					              + "AND EM.DOJ >='2004-01-01') or (EM.AC_DCPS_MAINTAINED_BY in (700174) and EM.DOJ >='2005-11-01')) ");
			lSBQuery.append(" and  DC.IS_ARREARS is null and DC.CONTRIBUTION=dc.CONTRIBUTION_EMPLR)  UNION ALL  ");
			lSBQuery.append(" (SELECT distinct EM.emp_name, EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)), ");
			lSBQuery.append(" nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''), ");
			lSBQuery.append(" EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),EM.SEVEN_PC_BASIC, ");
			lSBQuery.append(" EM.AC_DCPS_MAINTAINED_BY, DC.IS_ARREARS  FROM mst_dcps_emp EM  JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id ");
			lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID  and HD.LOCATION_CODE = DF.TREASURY  ");
			lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID  ");
			lSBQuery.append(" AND DC.FIN_YEAR_ID =" + yearId +"  AND DC.MONTH_ID =" + monthId + " ");
			lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID ='" + dummyOfficeId +"' ");
			lSBQuery.append(" AND DF.TREASURY ='" + gStrLocationCode +"'  ");
			lSBQuery.append(" and EM.PAY_COMMISSION not in ('700337','700338') ");
			lSBQuery.append(" and HD.REASON_DETACH is null ");
			lSBQuery.append(" and DC.startdate between '2019-04-01' and '2019-09-30' ");//$t 2019 '2019-09-30'
			lSBQuery.append(" AND(( EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') ");
			lSBQuery.append(" or (EM.AC_DCPS_MAINTAINED_BY in (700174) and EM.DOJ >='2005-11-01'))and  DC.IS_ARREARS ='Y' and DC.CONTRIBUTION < dc.CONTRIBUTION_EMPLR ) ");
			
			
			Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			logger.info("Query to getEmployeesFromDummyOfficeNPS in  heaqder**********"+ lSBQuery.toString());
			System.out.println("lSBQuery.toString()...................."+lSBQuery.toString());
			
			lLstDummyOfficeDtls = stQuery.list();
	     
		
//			
//		
//			/*lSBQuery.append(" (SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)), ");
//			lSBQuery.append(" nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''), ");
//			lSBQuery.append(" nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)), ");
//			lSBQuery.append(" nvl(EM.doj,''),EM.SEVEN_PC_BASIC,EM.AC_DCPS_MAINTAINED_BY,DC.IS_ARREARS FROM mst_dcps_emp EM JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id ");
//			lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID ");
//			lSBQuery.append(" and DC.FIN_YEAR_ID =" + yearId +" and DC.MONTH_ID =" +monthId + " ");
//			lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID ='" + dummyOfficeId +"' AND DF.TREASURY ='" + gStrLocationCode +"' and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null  ");
//			lSBQuery.append(" and dc.startdate between '2019-04-01' and '2019-08-31' and dc.IS_ARREARS is null and dc.CONTRIBUTION=dc.CONTRIBUTION_EMPLR ");
//			lSBQuery.append(" AND((EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') or (EM.AC_DCPS_MAINTAINED_BY in (700174) and EM.DOJ >='2005-11-01'))) ");
//			lSBQuery.append(" UNION all ");
//			lSBQuery.append(" (SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)), ");
//			lSBQuery.append(" nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''), ");
//			lSBQuery.append(" EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),EM.SEVEN_PC_BASIC, ");
//			lSBQuery.append(" EM.AC_DCPS_MAINTAINED_BY,DC.IS_ARREARS FROM mst_dcps_emp EM JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID ");
//			lSBQuery.append(" and HD.LOCATION_CODE = DF.TREASURY LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID AND DC.FIN_YEAR_ID =" + yearId +" and DC.MONTH_ID =" +monthId + " ");
//			lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID ='" + dummyOfficeId +"' AND DF.TREASURY ='" + gStrLocationCode +"' and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null and dc.startdate between '2019-04-01' and '2019-08-31'  ");
//			lSBQuery.append(" and dc.IS_ARREARS ='Y' AND(( EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') or (EM.AC_DCPS_MAINTAINED_BY in (700174) and EM.DOJ >='2005-11-01'))) ");
//			*/
//		
//			
//			lSBQuery = new StringBuilder();
//			//lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),'','',0,0,'','',0,'','',EM.PAY_COMMISSION,'', BIGINT(EM.BASIC_PAY),0,0,nvl(EM.doj,'')  ");//nvl(DC.DCPS_DEPTN_CONTRI_ID,0)
//			lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),EM.SEVEN_PC_BASIC,EM.AC_DCPS_MAINTAINED_BY,DC.IS_ARREARS ");
//			lSBQuery.append(" FROM mst_dcps_emp EM ");
//			lSBQuery.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
//			lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY");
//			lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID AND");
//			lSBQuery.append(" DC.FIN_YEAR_ID = :yearId AND DC.MONTH_ID = :monthId");
//			lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID = :dummyOfficeId ");
//			lSBQuery.append(" AND DF.TREASURY = :gStrLocationCode ");
//			lSBQuery.append("  and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null ");
//			lSBQuery.append(" AND ((EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') ");
//			lSBQuery.append(" or (EM.AC_DCPS_MAINTAINED_BY in (700174)  and EM.DOJ >='2005-11-01')) and DC.startdate between '2019-04-01' and '2019-08-31' and DC.IS_ARREARS is null and DC.CONTRIBUTION=dc.CONTRIBUTION_EMPLR ");
//			
//			
			
			
			/*lSBQuery.append(" and em.dcps_emp_id in (SELECT trn.dcps_emp_id FROM trn_dcps_contribution trn "
					+ "where  trn.startdate between '2019-04-01' and '2019-08-31' and trn.IS_ARREARS is null and trn.CONTRIBUTION=trn.CONTRIBUTION_EMPLR) order by DC.IS_ARREARS  ");
*/
			
			 
//			 Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
//				logger.info("Query to getEmployeesFromDummyOfficeNPS in  heaqder**********"
//						+ lSBQuery.toString());
//				stQuery.setParameter("dummyOfficeId", dummyOfficeId);
//				stQuery.setParameter("gStrLocationCode", gStrLocationCode);
//			    stQuery.setParameter("yearId", yearId);
//			    stQuery.setParameter("monthId", monthId);
//				lLstDummyOfficeDtls = stQuery.list();
//		     
			

			
			//lSBQuery.append(" and em.dcps_emp_id in (select trn.dcps_emp_id from TRN_DCPS_CONTRIBUTION trn where trn.startdate>='2019-04-01' and trn.ENDDATE<='2019-08-31' and (trn.IS_ARREARS<>'Y' "
//					+ "or trn.IS_ARREARS is null) and (trn.CONTRIBUTION_EMPLR !> 0 or trn.CONTRIBUTION_EMPLR is null or trn.CONTRIBUTION=trn.CONTRIBUTION_EMPLR)) ");
//			
			
			//lSBQuery.append(" AND em.dcps_emp_id in (SELECT trn.dcps_emp_id FROM TRN_DCPS_CONTRIBUTION trn where trn.STARTDATE>='2019-04-01' and trn.ENDDATE<='2019-08-31' and trn.IS_ARREARS<>'Y' ) ");
			
			//shw
			//and (trn.CONTRIBUTION_EMPLR !> 0 or trn.CONTRIBUTION_EMPLR is null or trn.CONTRIBUTION=trn.CONTRIBUTION_EMPLR )
			
			/*if(lStrUse.equals("WithoutEmplrContri"))
			{
				lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI = 'Y' ");
			}
			else
			{
				lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI IS NULL");
			}*/

			
//			Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
//			logger.info("Query to getEmployeesFromDummyOfficeNPS in  heaqder**********"
//					+ lSBQuery.toString());
		
//			lLstDummyOfficeDtls = stQuery.list();
//			
//			List Validationm=new ArrayList();
//			
//			IF(LLSTDUMMYOFFICEDTLS!=NULL && LLSTDUMMYOFFICEDTLS.SIZE()>0)
//			{
//				OBJECT OBJ[];
//				FOR (INT LICTR = 0; LICTR < LLSTDUMMYOFFICEDTLS.SIZE(); LICTR++) {
//					OBJ = (OBJECT[]) LLSTDUMMYOFFICEDTLS.GET(LICTR);
//					
//					SYSTEM.OUT.PRINTLN("OBJ[1]-->"+OBJ[1].TOSTRING());
//					
//					IF(!VALIDATIONM.CONTAINS(OBJ[1].TOSTRING()))
//					{
//						VALIDATIONM.ADD(OBJ[1].TOSTRING());
//						SYSTEM.OUT.PRINTLN("---->"+OBJ);
//						
//						LLSTDUMMYOFFICEDTLS1.ADDALL(OBJ);
//					}
//				}
//			}
//			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstDummyOfficeDtls;
	}

	
	
	public String getSchemeCodeForBillGroupId(Long billGroupId) {

		StringBuilder lSBQuery = new StringBuilder();
		List<String> tempList = new ArrayList();
		String schemeCode = null;

		lSBQuery
		.append("Select dcpsDdoSchemeCode FROM MstDcpsBillGroup WHERE dcpsDdoBillGroupId = :billGroupId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("billGroupId", billGroupId);

		tempList = lQuery.list();

		if(tempList.size()!= 0 )
		{
			schemeCode = tempList.get(0).toString();
		}

		return schemeCode;

	}

	public Long getCountofChallanOfficesForGivenTreasury(String lStrTreasury) {

		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long count = 0L;

		lSBQuery.append(" select count(*) FROM MstDummyOffice");
		lSBQuery.append(" WHERE treasury = :treasury");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("treasury", lStrTreasury);

		tempList = lQuery.list();
		count = tempList.get(0);
		return count;

	}

	public List getTowns(Long lStrCurrDst) throws Exception {

		ArrayList<ComboValuesVO> lLstTaluka = new ArrayList<ComboValuesVO>();
		Object[] obj;

		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" Select cityId,cityName ");
			lSBQuery.append(" FROM CmnCityMst ");

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

	public MstDcpsContriVoucherDtls getContriVoucherVOForInputDtlsForChallan(Long yearId,
			Long monthId, String ddoCode, Long treasuryCode, String lStrSchemeCode, Long lLongVoucherNo, Date lDateVoucherDate) throws Exception{

		StringBuilder lSBQuery = new StringBuilder();
		MstDcpsContriVoucherDtls lObjMstDcpsContriVoucherDtls = null;
		Query lQuery = null;
        //query return whole obj of hbm of table   MstDcpsContriVoucherDtls
		lSBQuery.append(" FROM MstDcpsContriVoucherDtls");
		lSBQuery.append(" WHERE yearId = :yearId ");
		lSBQuery.append(" and monthId= :monthId ");
		lSBQuery.append(" and ddoCode= :ddoCode ");
		lSBQuery.append(" and treasuryCode= :treasuryCode ");
		lSBQuery.append(" and schemeCode= :schemeCode ");
		lSBQuery.append(" and voucherNo= :voucherNo ");
		lSBQuery.append(" and voucherDate= :voucherDate ");

		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", yearId);
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("treasuryCode", treasuryCode);
		lQuery.setParameter("schemeCode", lStrSchemeCode);
		lQuery.setParameter("voucherNo", lLongVoucherNo);
		lQuery.setParameter("voucherDate", lDateVoucherDate);

		List tempList = null;
		tempList = lQuery.list();
		if(tempList != null && tempList.size() != 0)
		{
			lObjMstDcpsContriVoucherDtls = (MstDcpsContriVoucherDtls) tempList.get(0);
		}

		return lObjMstDcpsContriVoucherDtls;
	}

	public Double getTotalVoucherAmountForGivenChallan(Long monthId,
			Long finYearId, String schemeCode, String ddoCode, Long treasuryCode, Long challanNo, Date challanDate,String lStrUse) {

		StringBuilder lSBQuery = new StringBuilder();
		List tempList = null;
		Double voucherAmount = null;

		/*
		lSBQuery.append(" SELECT SUM(BIGINT(CONTRIBUTION)) FROM TRN_DCPS_DEPUTATION_CONTRIBUTION "
						+ " WHERE MONTH_ID = :monthId"
						+ " AND FIN_YEAR_ID = :yearId"
						+ " AND SCHEME_CODE = :schemeCode"
						+ " AND DUMMYOFFICE_ID = :ddoCode"
						+ " AND TREASURY = :treasuryCode"
						+ " AND CHALLAN_NO_EMPLR = :challanNo"	
						+ " AND CHALLAN_DATE_EMPLR = :challanDate"
				);
		 */

		lSBQuery.append(" SELECT SUM(BIGINT(contribution)) FROM TrnDcpsDeputationContribution");
		lSBQuery.append(" WHERE monthId = :monthId ");
		lSBQuery.append(" AND finYearId = :yearId ");
		lSBQuery.append(" AND schemeCode = :schemeCode ");
		lSBQuery.append(" AND dummyOfficeId = :ddoCode ");
		lSBQuery.append(" AND treasury = :treasuryCode ");

		if(lStrUse.equals("WithoutEmplrContri"))
		{
			lSBQuery.append(" AND challanNo = :challanNo ");
			lSBQuery.append(" AND challanDate = :challanDate ");
		}
		else
		{
			lSBQuery.append(" AND challanNoEmplr = :challanNo ");
			lSBQuery.append(" AND challanDateEmplr = :challanDate ");
		}

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("yearId", finYearId);
		lQuery.setParameter("monthId", monthId);
		lQuery.setParameter("ddoCode", ddoCode);
		lQuery.setParameter("treasuryCode", treasuryCode.toString());
		lQuery.setParameter("schemeCode", schemeCode);
		lQuery.setParameter("challanNo", challanNo.toString());
		lQuery.setParameter("challanDate", challanDate);

		tempList = lQuery.list();
		if (tempList.get(0) != null) {
			voucherAmount = Double.parseDouble(tempList.get(0).toString());
		}
		return voucherAmount;
	}

	public void updateDummyOfficeDetails(MstDummyOffice lObjMstDummyOfficeVO,Map inputMap) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();

		lSBQuery.append(" UPDATE MST_DCPS_DUMMY_OFFICE SET");
		lSBQuery.append(" DUMMYOFFICE_NAME = :offName ,");
		lSBQuery.append(" ADMINDEPT = :adminDept ,");
		lSBQuery.append(" OFFADDR1 = :offAddr1 ,");
		lSBQuery.append(" OFFADDR2 = :offAddr2 ,");
		lSBQuery.append(" DISTRICT = :district ,");
		lSBQuery.append(" TALUKA = :taluka ,");
		lSBQuery.append(" TOWN = :town ,");
		lSBQuery.append(" VILLAGE = :village ,");
		lSBQuery.append(" PINCODE = :pinCode ,");
		lSBQuery.append(" TELNO1 = :telNo1 ,");
		lSBQuery.append(" TELNO2 = :telNo2 ,");
		lSBQuery.append(" FAXNO = :faxNo ,");
		lSBQuery.append(" EMAILADDR = :emailAddr ,");
		lSBQuery.append(" STATUSFLAG = :statusFlag ,");
		lSBQuery.append(" UPDATED_USER_ID = :updatedUserId ,");
		lSBQuery.append(" UPDATED_POST_ID = :updatedPostId ,");
		lSBQuery.append(" UPDATED_DATE = :updatedDate ,");
		lSBQuery.append(" IS_WITHOUT_EMPLR_CONTRI = :isWithoutEmplrContri");

		lSBQuery.append(" WHERE DUMMYOFFICE_ID = :dummyOfficeId AND TREASURY = :treasury ");

		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		lQuery.setParameter("offName", lObjMstDummyOfficeVO.getDummyOfficeName());
		lQuery.setParameter("adminDept", lObjMstDummyOfficeVO.getAdminDept());
		lQuery.setParameter("offAddr1", lObjMstDummyOfficeVO.getOffAddr1());
		lQuery.setParameter("offAddr2", lObjMstDummyOfficeVO.getOffAddr2());
		lQuery.setParameter("district", lObjMstDummyOfficeVO.getDistrict());

		lQuery.setParameter("taluka", lObjMstDummyOfficeVO.getTaluka());
		lQuery.setParameter("town", lObjMstDummyOfficeVO.getTown());
		lQuery.setParameter("village", lObjMstDummyOfficeVO.getVillage());
		lQuery.setParameter("pinCode", lObjMstDummyOfficeVO.getPinCode());

		lQuery.setParameter("telNo1", lObjMstDummyOfficeVO.getTelNo1());
		lQuery.setParameter("telNo2", lObjMstDummyOfficeVO.getTelNo2());
		lQuery.setParameter("faxNo", lObjMstDummyOfficeVO.getFaxNo());
		lQuery.setParameter("emailAddr", lObjMstDummyOfficeVO.getEmailAddr());

		lQuery.setParameter("statusFlag", lObjMstDummyOfficeVO.getStatusFlag());
		lQuery.setParameter("updatedUserId",SessionHelper.getUserId(inputMap));
		lQuery.setParameter("updatedPostId",SessionHelper.getPostId(inputMap));
		lQuery.setParameter("updatedDate", SessionHelper.getCurDate());

		lQuery.setParameter("dummyOfficeId", lObjMstDummyOfficeVO.getDummyOfficeId().trim());
		lQuery.setParameter("treasury", lObjMstDummyOfficeVO.getTreasury().trim());
		lQuery.setParameter("isWithoutEmplrContri", lObjMstDummyOfficeVO.getIsWithoutEmplrContri());

		lQuery.executeUpdate();

	}

	@Override
	public List getFinyears() {

		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2005' and '2014' order by finYearCode ASC";
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

	@Override
	public List getDeptnInfo(String DcpsId) {
		String query = "SELECT ATTACH_DATE,nvl(DETACH_DATE,'1111-11-11') FROM  hst_dcps_emp_deputation where dcps_emp_id = (SELECT dcps_emp_id FROM mst_dcps_emp where dcps_id='"+DcpsId+"')";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		List lLstResult = selectQuery.list();

		return lLstResult;
	}

	@Override
	public List getTOPInfo(String dcpsId,String TypeOfPayment) {
		String query = "SELECT nvl(STARTDATE,'11-11-1111'),nvl(ENDDATE,'11-11-1111') FROM TRN_DCPS_CONTRIBUTION where TYPE_OF_PAYMENT='"+TypeOfPayment+"' and dcps_emp_id = (SELECT dcps_emp_id FROM mst_dcps_emp where dcps_id='"+dcpsId+"')";
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		List lLstResult = selectQuery.list();

		return lLstResult;
	}

	@Override
	public List getDelRegInfo(String dcpsId, String typeOfPayment) {
		String query=null;
		gLogger.info("typeOfPayment####"+typeOfPayment);
		if(typeOfPayment.equals("700046")){
			gLogger.info("in 700046####"+typeOfPayment);
		query = "SELECT nvl(STARTDATE,'11-11-1111'),nvl(ENDDATE,'11-11-1111') FROM TRN_DCPS_CONTRIBUTION where TYPE_OF_PAYMENT='700047' and dcps_emp_id = (SELECT dcps_emp_id FROM mst_dcps_emp where dcps_id='"+dcpsId+"')";
		}else if(typeOfPayment.equals("700047")){
		query= "SELECT nvl(STARTDATE,'11-11-1111'),nvl(ENDDATE,'11-11-1111') FROM TRN_DCPS_CONTRIBUTION where TYPE_OF_PAYMENT='700046' and dcps_emp_id = (SELECT dcps_emp_id FROM mst_dcps_emp where dcps_id='"+dcpsId+"')";	
		}
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		
		Query selectQuery = ghibSession.createSQLQuery(sb.toString());
		List lLstResult = selectQuery.list();

		return lLstResult;
	}
//$t 2019
	public List getFinyearsForNPS1() {

		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode='2019' order by finYearCode ASC";
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

	
	@Override
	public List getFinyearsForNPS() {
		
		Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        if(4>month+1){
            year = year-1;
        }

		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2015' and '"+year+"' order by finYearCode ASC";
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
	
	public String getEmpCmpCode(Long dcpsEmpId) throws Exception {
		
		List lLstEmpDeptn = null;
		SQLQuery sqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		String CmpCode="";

		try {
				lSBQuery.append("  SELECT mpg.COMPO_ID FROM HR_EIS_EMP_COMPONENT_GRP_MST gm,HR_EIS_EMP_COMPONENT_MPG mpg, ");
				lSBQuery.append(" HR_EIS_EMP_MST emp,mst_dcps_emp mst where mst.ORG_EMP_MST_ID=emp.EMP_MPG_ID  ");
				lSBQuery.append(" and emp.EMP_ID=gm.EMP_ID and gm.EMP_COMPO_GRP_ID=mpg.COMPO_GROUP_ID and mpg.COMPO_TYPE = 2500134   ");
				lSBQuery.append(" and mpg.COMPO_ID in (162,10) and mpg.IS_ACTIVE=1 and gm.IS_ACTIVE=1   ");
				lSBQuery.append(" and mst.DCPS_EMP_ID = '" +dcpsEmpId+"'");
				
			sqlQuery = session.createSQLQuery(lSBQuery.toString()); 
			logger.info("Query to CODE heaqder**********"
					+ lSBQuery.toString()); 
			
			lLstEmpDeptn = sqlQuery.list();
			if(lLstEmpDeptn!=null &&  lLstEmpDeptn.size()>0 && lLstEmpDeptn.get(0)!=null && !"".equalsIgnoreCase(lLstEmpDeptn.get(0).toString()))
			{
				CmpCode=lLstEmpDeptn.get(0).toString();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return CmpCode;

	}

	@Override
	public List getFieldDept(long parentDeptId)   {
		
		ArrayList<ComboValuesVO> lLstFieldDept = new ArrayList<ComboValuesVO>();
		SQLQuery sqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		Object obj[];

		try {
				
				lSBQuery.append(" SELECT clm.loc_Id,clm.loc_Name FROM Cmn_Location_Mst clm  ");
				lSBQuery.append(" WHERE clm.parent_loc_Id="+parentDeptId+"  ");
				lSBQuery.append(" and clm.lang_Id =1  ");
				
				
			sqlQuery = session.createSQLQuery(lSBQuery.toString()); 
			
			
			List lLstResult = sqlQuery.list();
			ComboValuesVO lObjComboValuesVO = null;
			if (lLstResult != null && lLstResult.size() > 0) {
				Iterator it = lLstResult.iterator();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("-- Select --");
				lLstFieldDept.add(lObjComboValuesVO);
				
				while (it.hasNext()) {
					lObjComboValuesVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstFieldDept.add(lObjComboValuesVO);
				}
			}
			
			
			
			 
			
			

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			//throw e;
		}
		return lLstFieldDept;

	}
public String getOfficeName (String lStrSevarthId,String lStrEmpName) throws Exception {
		
		List lLstOfficeName = null;
		SQLQuery sqlQuery = null;
		StringBuilder lSBQuery = new StringBuilder();
		Session session = getSession();
		String offName="";

		try {
				lSBQuery.append(" SELECT (off.DUMMYOFFICE_NAME ||' , '|| nvl(LOC_SHORT_NAME,'') ) FROM mst_dcps_emp emp inner join HST_DCPS_EMP_DEPUTATION dep on dep.DCPS_EMP_ID = emp.DCPS_EMP_ID inner join MST_DCPS_DUMMY_OFFICE off on off.DUMMYOFFICE_ID = dep.OFF_CODE  and dep.LOCATION_CODE = off.TREASURY inner join CMN_LOCATION_MST cmn on substr(cmn.LOC_ID,1,2) = substr(dep.LOCATION_CODE,1,2) and cmn.DEPARTMENT_ID = 100003  where  1=1 and dep.REASON_DETACH is null ");
			
				if(lStrSevarthId != null && !"".equals(lStrSevarthId))
				{
					lSBQuery.append(" AND emp.sevarth_id = '" + lStrSevarthId.trim().toUpperCase() +"'");
				}
				if(lStrEmpName != null && !"".equals(lStrEmpName))
				{
					lSBQuery.append(" AND emp.emp_name = '" + lStrEmpName.trim().toUpperCase() +"'");
				}
			sqlQuery = session.createSQLQuery(lSBQuery.toString());
			gLogger.info("getOfficeName is *****:" +sqlQuery);
			lLstOfficeName = sqlQuery.list();
			if(lLstOfficeName!=null && lLstOfficeName.size()>0)
			{
				offName=lLstOfficeName.get(0).toString();
			}
			gLogger.error("offName is :" +offName);


		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return offName;

	}

   @Override
   public List getDummyOfficesDdoAst(String gStrLocationCode,
		String lStrWithOrWithoutEmplrContri, String gStrPostId) {
	List<ComboValuesVO> lLstOffice = new ArrayList<ComboValuesVO>();
	List lLstResultList = null;
	
	
	ComboValuesVO cmbVO;
	
	SQLQuery sqlQuery = null;
	StringBuilder lSBQuery = new StringBuilder();
	Session session = getSession();
	//Object obj[];
	
	try {
		ghibSession = getSession();
		lSBQuery = new StringBuilder();
        ////$t irri   
		lSBQuery.append(" SELECT d.DUMMYOFFICE_ID,d.DUMMYOFFICE_NAME,d.TREASURY FROM  MST_DCPS_DUMMY_OFFICE d INNER JOIN ORG_DDO_MST o on  substr(o.ddo_code,1,4)=d.TREASURY INNER join rlt_dcps_ddo_asst r on r.DDO_POST_ID=o.POST_ID "
				+ "WHERE r.ASST_POST_ID ='" + gStrPostId.trim() +"' and d.DUMMYOFFICE_ID In('X00013','X00017','X00014') order by d.DUMMYOFFICE_NAME  ");//X00025
		
       sqlQuery = session.createSQLQuery(lSBQuery.toString());
		
		logger.info("Query to DUMMY OFFICE heaqder**********"
				+ lSBQuery.toString());
		lLstResultList = sqlQuery.list();
		Collections.sort(lLstResultList,
				new PensionProcComparators.ObjectArrayComparator(false, 1,
						0, 2, 0, true));
		cmbVO = new ComboValuesVO();
		if (lLstResultList != null && lLstResultList.size() > 0) {
			Iterator it = lLstResultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				Object[] obj = (Object[]) it.next();
				cmbVO.setId(obj[0].toString());
				cmbVO.setDesc(obj[1].toString());
				lLstOffice.add(cmbVO);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		gLogger.info("Error is  " + e);
	}
	return lLstOffice;
}

@Override
public List getDummyOfficesDdo(String gStrLocationCode,String lStrWithOrWithoutEmplrContri, String gStrPostId) {
	List<ComboValuesVO> lLstOffice = new ArrayList<ComboValuesVO>();
	List lLstResultList = null;
	
	
	ComboValuesVO cmbVO;
	
	SQLQuery sqlQuery = null;
	StringBuilder lSBQuery = new StringBuilder();
	Session session = getSession();
	
	try {
		ghibSession = getSession();
		lSBQuery = new StringBuilder();
        /////$t irri 
		lSBQuery.append(" SELECT d.DUMMYOFFICE_ID,d.DUMMYOFFICE_NAME,d.TREASURY FROM  MST_DCPS_DUMMY_OFFICE d INNER JOIN ORG_DDO_MST o on  substr(o.ddo_code,1,4)=d.TREASURY "
				+ "WHERE o.post_id='" + gStrPostId.trim() +"' and d.DUMMYOFFICE_ID In('X00013','X00017','X00014') order by d.DUMMYOFFICE_NAME  ");//('X00025')
		

		  sqlQuery = session.createSQLQuery(lSBQuery.toString());
		//hqlQuery.setParameter("treasury", gStrPostId.trim());
		
		logger.info("Query to DUMMY OFFICE heaqder**********"
				+ lSBQuery.toString());
		lLstResultList = sqlQuery.list();
		Collections.sort(lLstResultList,
				new PensionProcComparators.ObjectArrayComparator(false, 1,
						0, 2, 0, true));
		cmbVO = new ComboValuesVO();
		if (lLstResultList != null && lLstResultList.size() > 0) {
			Iterator it = lLstResultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				Object[] obj = (Object[]) it.next();
				cmbVO.setId(obj[0].toString());
				cmbVO.setDesc(obj[1].toString());
				lLstOffice.add(cmbVO);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		gLogger.info("Error is  " + e);
	}
	return lLstOffice;
}

@Override
public List getEmployeesFromDummyOfficeAstNPS(String lStrDummyOfficeId,
		Long monthId, Long yearId, String gStrLocationCode,
		String lStrSubmittedOrNot, String lStrWithOrWithoutEmplrContri,
		String gStrPostId) {
	List lLstDummyOfficeDtls = null;
	StringBuilder lSBQuery = null;

	////$t 20-04-2020 IRRI
	String ddoCode = "";
	//String locid="";
    List tempList = null;
    StringBuilder lSBQuery1 = null;
    StringBuilder lSBQuery2 = null;
    List dateList = null;
    String finDate = "";
	try {
	    ////$t 16-12-2020 IRRI
		lSBQuery2 = new StringBuilder();
		lSBQuery2.append(" SELECT fin.FIN_YEAR_CODE FROM SGVC_FIN_YEAR_MST fin where fin.FIN_YEAR_ID= '"+yearId+"' ");
		Query lQuery2 = this.ghibSession.createSQLQuery(lSBQuery2.toString());
		dateList = lQuery2.list();
		if ((dateList != null) && (dateList.size() > 0) && (dateList.get(0) != null)) {
		finDate = dateList.get(0).toString();//2013-05-31 00:00:00.0
		if(monthId==12){
		finDate=finDate+"-"+(monthId)+"-01";
		}else if(monthId>3){
		finDate=finDate+"-"+(monthId+1)+"-01";
		}else{
		finDate=(Integer.parseInt(finDate)+1)+"-"+(monthId+1)+"-01";
		}
		}//if
		//$t
		gStrLocationCode = getTreasuryCodeForAst(gStrLocationCode).toString();
		
		lSBQuery1 = new StringBuilder();
		
		lSBQuery1.append(" SELECT OD.POST_ID FROM RLT_DCPS_DDO_ASST RD, ORG_DDO_MST OD  WHERE OD.POST_ID = RD.DDO_POST_ID AND RD.ASST_POST_ID = '"+gStrPostId+"'  ");
	    Query lQuery1 = this.ghibSession.createSQLQuery(lSBQuery1.toString());
	    tempList = lQuery1.list();
	    if ((tempList != null) && (tempList.size() > 0) && (tempList.get(0) != null)) {
	    	ddoCode = tempList.get(0).toString();
	    }
	    
/*        lSBQuery2.append(" SELECT OD.LOCATION_CODE FROM RLT_DCPS_DDO_ASST RD, ORG_DDO_MST OD  WHERE OD.POST_ID = RD.DDO_POST_ID AND RD.ASST_POST_ID = '"+gStrPostId+"'  ");
        tempList.clear();
	    Query lQuery2 = this.ghibSession.createSQLQuery(lSBQuery2.toString());
	    tempList = lQuery2.list();
	    if ((tempList != null) && (tempList.size() > 0) && (tempList.get(0) != null)) {
	    	locid = tempList.get(0).toString();
	    }
*/	    ////$t
	    
	    lSBQuery = new StringBuilder();
		//lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),'','',0,0,'','',0,'','',EM.PAY_COMMISSION,'', BIGINT(EM.BASIC_PAY),0,0,nvl(EM.doj,'')  ");//nvl(DC.DCPS_DEPTN_CONTRI_ID,0)
		lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),BIGINT(nvl(EM.SEVEN_PC_BASIC,0)),EM.AC_DCPS_MAINTAINED_BY,cast(decode(DCC.STATUS,'N','10','M','20','H','30','D',15) as bigint) ");////25-11-2020 EM.SEVEN_PC_BASIC=null ////$t irri 13-1-2021
		lSBQuery.append(" FROM mst_dcps_emp EM ");
		lSBQuery.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
		lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY" );
		lSBQuery.append(" INNER JOIN ORG_DDO_MST ddo on  substr(ddo.ddo_code,1,4)=DF.TREASURY ");
		lSBQuery.append(" INNER join rlt_dcps_ddo_asst ddoa on ddoa.DDO_POST_ID=ddo.POST_ID ");
		lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID AND");
		lSBQuery.append(" DC.FIN_YEAR_ID = :yearId AND DC.MONTH_ID = :monthId");
	    ////$t 20-04-2020 IRRI
		lSBQuery.append(" LEFT JOIN trn_dcps_contribution DCC ON DC.DCPS_EMP_ID = DCC.DCPS_EMP_ID ");
		lSBQuery.append(" AND DCC.FIN_YEAR_ID = :yearId AND DCC.MONTH_ID = :monthId");
		lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID = :dummyOfficeId ");
		lSBQuery.append(" AND EM.doj<'"+finDate+"' ");///$t 16-12-2020 IRRI
		lSBQuery.append(" AND DF.TREASURY = :gStrLocationCode ");
		lSBQuery.append(" AND ddo.post_id = :gStrPostId ");///$t irri
		lSBQuery.append("  and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null ");
		lSBQuery.append(" AND ((EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') ");
		lSBQuery.append(" or (EM.AC_DCPS_MAINTAINED_BY in (700174)  and EM.DOJ >='2005-11-01')) ");
	    ////$t 20-04-2020
		//lSBQuery.append(" AND DCC.status is null ");//
		lSBQuery.append(" AND (DCC.status='D' or DCC.status is null) ");////$t irri 13-1-2021
	    ////$t
		/*if(lStrUse.equals("WithoutEmplrContri"))
		{
			lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI = 'Y' ");
		}
		else
		{
			lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI IS NULL");
		}*/

		Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		logger.info("Query to getEmployeesFromDummyOfficeNPS in  heaqder**********"
				+ lSBQuery.toString());
		stQuery.setParameter("dummyOfficeId", lStrDummyOfficeId);
		////$t 20-04-2020 IRRI
		stQuery.setParameter("gStrLocationCode", gStrLocationCode);
		/////$t
	    stQuery.setParameter("yearId", yearId);
	    stQuery.setParameter("monthId", monthId);
	    stQuery.setParameter("gStrPostId", ddoCode);
		lLstDummyOfficeDtls = stQuery.list();

	} catch (Exception e) {
		e.printStackTrace();
		logger.error("Error is :" + e, e);
	}
	return lLstDummyOfficeDtls;
}

@Override
public List getEmployeesFromDummyOfficeDdoNPS(String lStrDummyOfficeId,
		Long monthId, Long yearId, String gStrLocationCode,
		String lStrSubmittedOrNot, String lStrWithOrWithoutEmplrContri,
		String gStrPostId) {
	List lLstDummyOfficeDtls = null;
	StringBuilder lSBQuery = null;

	try {
		////$t irri
		gStrLocationCode = getTreasuryCodeForAst(gStrLocationCode).toString();
		
		lSBQuery = new StringBuilder();
		//lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),'','',0,0,'','',0,'','',EM.PAY_COMMISSION,'', BIGINT(EM.BASIC_PAY),0,0,nvl(EM.doj,'')  ");//nvl(DC.DCPS_DEPTN_CONTRI_ID,0)
		lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),BIGINT(nvl(EM.SEVEN_PC_BASIC,0)),EM.AC_DCPS_MAINTAINED_BY,cast(decode(DCC.STATUS,'N','10','M','20','H','30','D',15) as bigint) ");////25-11-2020 EM.SEVEN_PC_BASIC=null
		lSBQuery.append(" FROM mst_dcps_emp EM ");
		lSBQuery.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
		lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY ");
		lSBQuery.append(" INNER JOIN ORG_DDO_MST ddo on  substr(ddo.ddo_code,1,4)=DF.TREASURY ");
		lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID AND");
		lSBQuery.append(" DC.FIN_YEAR_ID = :yearId AND DC.MONTH_ID = :monthId");
	    ////$t 20-04-2020
		lSBQuery.append(" LEFT JOIN trn_dcps_contribution DCC ON DC.DCPS_EMP_ID = DCC.DCPS_EMP_ID ");
		lSBQuery.append(" AND DCC.FIN_YEAR_ID = :yearId AND DCC.MONTH_ID = :monthId");
		////$t
		lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID = :dummyOfficeId ");
		lSBQuery.append(" AND DF.TREASURY = :gStrLocationCode ");
		lSBQuery.append(" AND ddo.post_id = :gStrPostId ");
		lSBQuery.append("  and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null ");
		lSBQuery.append(" AND ((EM.AC_DCPS_MAINTAINED_BY in (700240,700241,700242) and EM.DOJ >='2004-01-01') ");
		lSBQuery.append(" or (EM.AC_DCPS_MAINTAINED_BY in (700174)  and EM.DOJ >='2005-11-01')) ");
	    ////$t 20-04-2020
		lSBQuery.append(" AND DCC.status='M' ");
	    ////$t
		/*if(lStrUse.equals("WithoutEmplrContri"))
		{
			lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI = 'Y' ");
		}
		else
		{
			lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI IS NULL");
		}*/

		Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		logger.info("Query to getEmployeesFromDummyOfficeNPS in  heaqder**********"
				+ lSBQuery.toString());
		stQuery.setParameter("dummyOfficeId", lStrDummyOfficeId);
		stQuery.setParameter("gStrLocationCode", gStrLocationCode);
	    stQuery.setParameter("yearId", yearId);
	    stQuery.setParameter("monthId", monthId);
	    stQuery.setParameter("gStrPostId", gStrPostId);
		lLstDummyOfficeDtls = stQuery.list();
		logger.info("Count lLstDummyOfficeDtls **********"+ lLstDummyOfficeDtls.size());//$t IRRI
	} catch (Exception e) {
		e.printStackTrace();
		logger.error("Error is :" + e, e);
	}
	return lLstDummyOfficeDtls;
}


@Override
public List getEmployeesFromDummyOfficeAst(String lStrDummyOfficeId,
		Long monthId, Long yearId, String gStrLocationCode,
		String lStrSubmittedOrNot, String lStrWithOrWithoutEmplrContri,
		String gStrPostId) {
	List lLstDummyOfficeDtls = null;
	StringBuilder lSBQuery = null;
    ////$t 20-04-2020 IRRI
	String ddoCode = "";
	List tempList = null;
    StringBuilder lSBQuery1 = null;
    StringBuilder lSBQuery2 = null;
    List dateList = null;
    String finDate = "";

	try {
	   ////$t 16-12-2020 IRRI
			lSBQuery2 = new StringBuilder();
			lSBQuery2.append(" SELECT fin.FIN_YEAR_CODE FROM SGVC_FIN_YEAR_MST fin where fin.FIN_YEAR_ID= '"+yearId+"' ");
		    Query lQuery2 = this.ghibSession.createSQLQuery(lSBQuery2.toString());
		    dateList = lQuery2.list();
		    if ((dateList != null) && (dateList.size() > 0) && (dateList.get(0) != null)) {
			finDate = dateList.get(0).toString();//2013-05-31 00:00:00.0
			if(monthId==12){
			finDate=finDate+"-"+(monthId)+"-01";
			}else if(monthId>3){
			finDate=finDate+"-"+(monthId+1)+"-01";
			}else{
			finDate=(Integer.parseInt(finDate)+1)+"-"+(monthId+1)+"-01";
			}
			}//if
		//$t
		gStrLocationCode = getTreasuryCodeForAst(gStrLocationCode).toString();
		
		lSBQuery1 = new StringBuilder();
		
		lSBQuery1.append(" SELECT OD.POST_ID FROM RLT_DCPS_DDO_ASST RD, ORG_DDO_MST OD  WHERE OD.POST_ID = RD.DDO_POST_ID AND RD.ASST_POST_ID = '"+gStrPostId+"'  ");
	    Query lQuery1 = this.ghibSession.createSQLQuery(lSBQuery1.toString());
	    tempList = lQuery1.list();
	    if ((tempList != null) && (tempList.size() > 0) && (tempList.get(0) != null)) {
	    	ddoCode = tempList.get(0).toString();
	    }
		
		lSBQuery = new StringBuilder();
		//lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),'','',0,0,'','',0,'','',EM.PAY_COMMISSION,'', BIGINT(EM.BASIC_PAY),0,0,nvl(EM.doj,'')  ");//nvl(DC.DCPS_DEPTN_CONTRI_ID,0)
		lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),BIGINT(nvl(EM.SEVEN_PC_BASIC,0)),EM.AC_DCPS_MAINTAINED_BY, cast(decode(DCC.STATUS,'N','10','M','20','H','30','D',15) as bigint) ");////25-11-2020 EM.SEVEN_PC_BASIC=null ////$t irri 13-1-2021
		lSBQuery.append(" FROM mst_dcps_emp EM ");
		lSBQuery.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
		lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY");
		lSBQuery.append(" INNER JOIN ORG_DDO_MST ddo on  substr(ddo.ddo_code,1,4)=DF.TREASURY ");
		lSBQuery.append(" INNER join rlt_dcps_ddo_asst ddoa on ddoa.DDO_POST_ID=ddo.POST_ID ");
		lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID AND");
		lSBQuery.append(" DC.FIN_YEAR_ID = :yearId AND DC.MONTH_ID = :monthId");
	    ////$t 20-04-2020 IRRI
	    lSBQuery.append(" LEFT JOIN trn_dcps_contribution DCC ON DC.DCPS_EMP_ID = DCC.DCPS_EMP_ID ");
	    lSBQuery.append(" AND DCC.FIN_YEAR_ID = :yearId AND DCC.MONTH_ID = :monthId");
		////$t
		lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID = :dummyOfficeId ");
		lSBQuery.append(" AND EM.doj<'"+finDate+"' ");
		lSBQuery.append(" AND DF.TREASURY = :gStrLocationCode ");
		lSBQuery.append(" AND ddo.post_id = :gStrPostId ");
		lSBQuery.append(" AND EM.doj<='2015-03-31' and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null ");
	    ////$t 20-04-2020
	    //lSBQuery.append(" AND DCC.status is null ");
		lSBQuery.append(" AND (DCC.status='D' or DCC.status is null) ");////$t irri 13-1-2021
		////$t
		/*if(lStrUse.equals("WithoutEmplrContri"))
		{
			lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI = 'Y' ");
		}
		else
		{
			lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI IS NULL");
		}*/

		Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		
		logger.info("Query to EMPLOYEELIST heaqder**********"
				+ lSBQuery.toString()); 
		
		stQuery.setParameter("dummyOfficeId", lStrDummyOfficeId);
		stQuery.setParameter("gStrLocationCode", gStrLocationCode);
		stQuery.setParameter("yearId", yearId);
	    stQuery.setParameter("monthId", monthId);
	    stQuery.setParameter("gStrPostId", ddoCode);///$t irri
		lLstDummyOfficeDtls = stQuery.list();

	} catch (Exception e) {
		e.printStackTrace();
		logger.error("Error is :" + e, e);
	}
	return lLstDummyOfficeDtls;
}

@Override
public List getEmployeesFromDummyOfficeDdo(String lStrDummyOfficeId,
		Long monthId, Long yearId, String gStrLocationCode,
		String lStrSubmittedOrNot, String lStrWithOrWithoutEmplrContri,
		String gStrPostId) {
	List lLstDummyOfficeDtls = null;
	StringBuilder lSBQuery = null;
	
       ////$t irri
		gStrLocationCode = getTreasuryCodeForAst(gStrLocationCode).toString();
	
	System.out.println( lStrDummyOfficeId+"..............."+gStrPostId+" -----------"+yearId+monthId);
	Iterator itr;
	Object[] obj;
	try {
		lSBQuery = new StringBuilder();
		//lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),'','',0,0,'','',0,'','',EM.PAY_COMMISSION,'', BIGINT(EM.BASIC_PAY),0,0,nvl(EM.doj,'')  ");//nvl(DC.DCPS_DEPTN_CONTRI_ID,0)
		lSBQuery.append(" SELECT distinct EM.emp_name,EM.dcps_emp_id,EM.dcps_id||'/'||nvl(EM.pran_no,''),nvl(DC.CHALLAN_NO,''),DC.CHALLAN_DATE,BIGINT(nvl(DC.CONTRIBUTION,0)),nvl(DC.DCPS_DEPTN_CONTRI_ID,0),nvl(DC.CHALLAN_NO_EMPLR,''),DC.CHALLAN_DATE_EMPLR,BIGINT(nvl(DC.CONTRIBUTION_EMPLR,0)),nvl(DC.STARTDATE,''),nvl(DC.ENDDATE,''),EM.PAY_COMMISSION,nvl(DC.TYPE_OF_PAYMENT,'700046'),BIGINT(nvl(DC.BASIC_PAY,EM.BASIC_PAY)),BIGINT(nvl(DC.DP,0)),BIGINT(nvl(DC.DA,0)),nvl(EM.doj,''),BIGINT(nvl(EM.SEVEN_PC_BASIC,0)),EM.AC_DCPS_MAINTAINED_BY,cast(decode(DCC.STATUS,'N','10','M','20','H','30','D',15) as bigint) ");////25-11-2020 EM.SEVEN_PC_BASIC=null ////$t irri 13-1-2021
		lSBQuery.append(" FROM mst_dcps_emp EM ");
		lSBQuery.append(" JOIN hst_dcps_emp_deputation HD on HD.dcps_emp_id = EM.dcps_emp_id");
		lSBQuery.append(" JOIN mst_dcps_dummy_office DF ON HD.OFF_CODE = DF.DUMMYOFFICE_ID and HD.LOCATION_CODE = DF.TREASURY");
		lSBQuery.append(" INNER JOIN ORG_DDO_MST ddo on  substr(ddo.ddo_code,1,4)=DF.TREASURY ");
		lSBQuery.append(" LEFT JOIN trn_dcps_deputation_contribution DC ON EM.DCPS_EMP_ID = DC.DCPS_EMP_ID AND");
		lSBQuery.append(" DC.FIN_YEAR_ID = :yearId AND DC.MONTH_ID = :monthId");
	    ////$t 20-04-2020
	    lSBQuery.append(" LEFT JOIN trn_dcps_contribution DCC ON DC.DCPS_EMP_ID = DCC.DCPS_EMP_ID ");
	    lSBQuery.append(" AND DCC.FIN_YEAR_ID = :yearId AND DCC.MONTH_ID = :monthId");
		////$t
		lSBQuery.append(" WHERE DF.DUMMYOFFICE_ID = :dummyOfficeId ");
		lSBQuery.append(" AND DF.TREASURY = :gStrLocationCode ");
		lSBQuery.append(" AND ddo.post_id = :gStrPostId ");
		lSBQuery.append(" AND EM.doj<='2015-03-31' and EM.PAY_COMMISSION not in ('700337','700338') and HD.REASON_DETACH is null ");
	    ////$t 20-04-2020
		lSBQuery.append(" AND DCC.status='M' ");
		////$t
		/*if(lStrUse.equals("WithoutEmplrContri"))
		{
			lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI = 'Y' ");
		}
		else
		{
			lSBQuery.append(" AND DF.IS_WITHOUT_EMPLR_CONTRI IS NULL");
		}*/

		Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		
		logger.info("Query to EMPLOYEELIST heaqder**********"
				+ lSBQuery.toString()); 
		
		stQuery.setParameter("dummyOfficeId", lStrDummyOfficeId);
		stQuery.setParameter("gStrLocationCode", gStrLocationCode);
		stQuery.setParameter("yearId", yearId);
	    stQuery.setParameter("monthId", monthId);
	    stQuery.setParameter("gStrPostId", gStrPostId);
		lLstDummyOfficeDtls = stQuery.list();
		logger.info("Count DCPS lLstDummyOfficeDtls **********"+ lLstDummyOfficeDtls.size());//$t IRRI
		
		/*if (lLstDummyOfficeDtls != null && lLstDummyOfficeDtls.size() > 0) {
			  itr = lLstDummyOfficeDtls.iterator();
			while (itr.hasNext()) {

				obj = (Object[]) itr.next();
				String s1=obj[1].toString();
				System.out.println(s1);
				
			}
			}*/

	} catch (Exception e) {
		e.printStackTrace();
		logger.error("Error is :" + e, e);
	}
	return lLstDummyOfficeDtls;
 }

//$t 2019
@Override
public void updateFlag(Long dcpsEmpId, Long month, Long year, Date date, Date date2) {
	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd"); //2019-07-02
	logger.info("dcpsEmpId**********"+dcpsEmpId+"month**********"+month+"year**********"+year+"date**********"+format.format(date)+"date2**********"+format.format(date2)); 
	
	StringBuilder lSBQuery=new StringBuilder();
	lSBQuery.append(" UPDATE TRN_DCPS_CONTRIBUTION  SET IS_ARREARS = 'S' where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' AND IS_ARREARS IS NULL and STARTDATE='"+format.format(date)+"' and ENDDATE='"+format.format(date2)+"' ");
	logger.info("lSBQuery-->"+lSBQuery);
	//Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
	this.logger.info("Query to updateFlag in TRN_DCPS_CONTRIBUTION heaqder**********" + lSBQuery.toString());
	updateQuery.executeUpdate();
	
	StringBuilder sb1 = new StringBuilder();
  
	sb1.append(" UPDATE TRN_DCPS_DEPUTATION_CONTRIBUTION  SET IS_ARREARS = 'S' where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' AND IS_ARREARS IS NULL and STARTDATE='"+format.format(date)+"' and ENDDATE='"+format.format(date2)+"' ");
	logger.info("lSBQuery-->"+sb1);
	//Query stQuery = ghibSession.createSQLQuery(lSBQuery.toString());
	Query updateQuery1 = this.ghibSession.createSQLQuery(sb1.toString());
	this.logger.info("Query to updateFlag in TRN_DCPS_DEPUTATION_CONTRIBUTION heaqder**********" + sb1.toString());
    updateQuery1.executeUpdate();
    
  }


/////$t 20-04-2020 IRRI
public String getTreasuryCodeForAst(String glngLocId){
    List tempList = null;
    StringBuilder lSBQuery1 = null;
    String  loc_id=null;
    
    lSBQuery1 = new StringBuilder();
    lSBQuery1.append(" SELECT trea.loc_id FROM ORG_DDO_MST ddo inner join MST_TREASURY_DTLS trea " );
    lSBQuery1.append(" on left(trea.LOC_ID,4) =left(ddo.DDO_CODE,4) ");
    lSBQuery1.append(" where ddo.LOCATION_CODE  = '"+glngLocId+"'  ");
    
    Query lQuery1 = this.ghibSession.createSQLQuery(lSBQuery1.toString());
    tempList = lQuery1.list();
    if ((tempList != null) && (tempList.size() > 0) && (tempList.get(0) != null)) {
    loc_id=   tempList.get(0).toString();
    }
    
    return loc_id;

}
////$t

////$t 20-04-2020 IRRI
public void updateContriFlagDDO(String dcpsEmpId, String month, String year, String date, String date2) throws ParseException {
	SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
    Date date1 = formatter1.parse(date);
    //System.out.println(date1);
    //System.out.println(formatter2.format(date1));
        
    Date date3 = formatter1.parse(date2);
        
	logger.info("dcpsEmpId**********"+dcpsEmpId+"month**********"+month+"year**********"+year+"date1**********"+formatter2.format(date1)+"date2**********"+formatter2.format(date3)); 
	
	StringBuilder lSBQuery=new StringBuilder();
	lSBQuery.append(" UPDATE TRN_DCPS_CONTRIBUTION  SET Status = 'N' where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' "
			+ "AND status='M' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
	logger.info("lSBQuery-->"+lSBQuery);
	Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
	this.logger.info("Query to updateContriFlagDDO in TRN_DCPS_CONTRIBUTION heaqder**********" + lSBQuery.toString());
	updateQuery.executeUpdate();
  }

////$t 20-04-2020 IRRI
public void updateContriFlagTO(String dcpsEmpId, String month, String year, String date, String date2,String isNPS,String lStrUse) throws ParseException {
SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
Date date1 = formatter1.parse(date);
//System.out.println(date1);
//System.out.println(formatter2.format(date1));
    
Date date3 = formatter1.parse(date2);
    
logger.info("dcpsEmpId**********"+dcpsEmpId+"month**********"+month+"year**********"+year+"date1**********"+formatter2.format(date1)+"date2**********"+formatter2.format(date3)); 

StringBuilder lSBQuery=new StringBuilder();////doubt
if(isNPS.equals("yes")){
lSBQuery.append(" UPDATE TRN_DCPS_CONTRIBUTION  SET Status = 'H' where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' "
		+ "AND status='N' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
}else{
	if(lStrUse.equals("WithEmplrContri")){
	lSBQuery.append(" UPDATE TRN_DCPS_CONTRIBUTION  SET Status = 'G' where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' "
	+ "AND status='N' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
	}else{
	lSBQuery.append(" UPDATE TRN_DCPS_CONTRIBUTION  SET Status = 'A' where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' "
	+ "AND status='N' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
	}
}
logger.info("lSBQuery-->"+lSBQuery);
Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
this.logger.info("Query to updateContriFlagTO in TRN_DCPS_CONTRIBUTION heaqder**********" + lSBQuery.toString());
updateQuery.executeUpdate();    
}

////$t 20-04-2020 IRRI
public void rejectContriFlagDDO(String dcpsEmpId, String month, String year, String date, String date2) throws ParseException {
SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
Date date1 = formatter1.parse(date);
//System.out.println(date1);
//System.out.println(formatter2.format(date1));
    
Date date3 = formatter1.parse(date2);
    
logger.info("dcpsEmpId**********"+dcpsEmpId+"month**********"+month+"year**********"+year+"date1**********"+formatter2.format(date1)+"date2**********"+formatter2.format(date3)); 

StringBuilder lSBQuery=new StringBuilder();
lSBQuery.append(" delete from TRN_DCPS_CONTRIBUTION where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' "
		+ "AND status='M' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
logger.info("lSBQuery-->"+lSBQuery);
Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
this.logger.info("Query to updateContriFlagDDO in TRN_DCPS_CONTRIBUTION heaqder**********" + lSBQuery.toString());
int rowsDeleted = updateQuery.executeUpdate();

StringBuilder lSBQuery1=new StringBuilder();
lSBQuery1.append(" delete from TRN_DCPS_DEPUTATION_CONTRIBUTION where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' "
		+ "AND FIN_YEAR_ID='"+year+"' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
logger.info("lSBQuery1-->"+lSBQuery1);
Query updateQuery1 = this.ghibSession.createSQLQuery(lSBQuery1.toString());
this.logger.info("Query to updateContriFlagDDO in TRN_DCPS_DEPUTATION_CONTRIBUTION heaqder**********" + lSBQuery1.toString());
rowsDeleted = updateQuery1.executeUpdate();
logger.info("******* updateContriFlagDDO in TRN_DCPS_DEPUTATION_CONTRIBUTION rowsDeleted"+rowsDeleted);
/*lSBQuery.append(" UPDATE TRN_DCPS_CONTRIBUTION  SET Status=null where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' AND status='M' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
logger.info("lSBQuery-->"+lSBQuery);
Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
this.logger.info("Query to rejectContriFlagDDO in TRN_DCPS_CONTRIBUTION heaqder**********" + lSBQuery.toString());
updateQuery.executeUpdate();*/    
}

////$t 20-04-2020 IRRI
public void rejectContriFlagTO(String dcpsEmpId, String month, String year, String date, String date2) throws ParseException {
SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
Date date1 = formatter1.parse(date);
//System.out.println(date1);
//System.out.println(formatter2.format(date1));

Date date3 = formatter1.parse(date2);

logger.info("dcpsEmpId**********"+dcpsEmpId+"month**********"+month+"year**********"+year+"date1**********"+formatter2.format(date1)+"date2**********"+formatter2.format(date3)); 

StringBuilder lSBQuery=new StringBuilder();/////$t irri 13-1-2021
/*lSBQuery.append(" UPDATE TRN_DCPS_CONTRIBUTION  SET Status='M' where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' "
+ "AND status='N' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");*/
lSBQuery.append(" DELETE FROM TRN_DCPS_CONTRIBUTION where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' "
+ "AND status='N' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
logger.info("lSBQuery-->"+lSBQuery);
Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
this.logger.info("Query to rejectContriFlagTO in TRN_DCPS_CONTRIBUTION heaqder**********" + lSBQuery.toString());
updateQuery.executeUpdate();

lSBQuery=new StringBuilder();

lSBQuery.append(" DELETE FROM TRN_DCPS_DEPUTATION_CONTRIBUTION where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' "
+ " and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
logger.info("lSBQuery-->"+lSBQuery);
Query updateQuery1 = this.ghibSession.createSQLQuery(lSBQuery.toString());
this.logger.info("Query to rejectContriFlagTO in TRN_DCPS_DEPUTATION_CONTRIBUTION heaqder**********" + lSBQuery.toString());
updateQuery1.executeUpdate();

}

////$t $t irri 13-1-2021
public void updateContriFlagDdoAsst(String dcpsEmpId, String month, String year, String date, String date2) throws ParseException {
SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
Date date1 = formatter1.parse(date);

Date date3 = formatter1.parse(date2);

logger.info("dcpsEmpId**********"+dcpsEmpId+"month**********"+month+"year**********"+year+"date1**********"+formatter2.format(date1)+"date2**********"+formatter2.format(date3)); 

StringBuilder lSBQuery=new StringBuilder();/////$t irri 13-1-2021
lSBQuery.append(" UPDATE TRN_DCPS_CONTRIBUTION  SET Status='M' where DCPS_EMP_ID='"+dcpsEmpId+"' AND MONTH_ID='"+month+"' AND FIN_YEAR_ID='"+year+"' "
+ "AND status='D' and STARTDATE='"+formatter2.format(date1)+"' and ENDDATE='"+formatter2.format(date3)+"' ");
logger.info("lSBQuery-->"+lSBQuery);
Query updateQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
this.logger.info("Query to updateContriFlagDdoAsst in TRN_DCPS_CONTRIBUTION heaqder**********" + lSBQuery.toString());
updateQuery.executeUpdate();
}

}

