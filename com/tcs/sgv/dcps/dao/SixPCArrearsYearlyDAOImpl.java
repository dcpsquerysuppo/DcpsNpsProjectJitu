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

import java.util.ArrayList;
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
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstSixPCArrears;

/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Apr 8, 2011
 */
public class SixPCArrearsYearlyDAOImpl extends GenericDaoHibernateImpl
		implements SixPCArrearsYearlyDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	/**
	 * @param type
	 */
	public SixPCArrearsYearlyDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.sixPCArrearsYearlyDAO#getDdoCodeForDDO(java.lang
	 * .Long)
	 */
	public String getDdoCodeForDDO(Long lLngPostId) throws Exception {

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

			lStrDdoCode = lLstDdoDtls.get(0).toString();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);

		}
		return lStrDdoCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.dcps.dao.sixPCArrearsYearlyDAO#
	 * getEmpListForSixPCArrearAmountSchedule(java.lang.String, java.lang.Long)
	 */
	public List getEmpListForSixPCArrearAmountSchedule(String lStrDDOCode,
			Long yearId) throws Exception {
		List empList = null;
		try {

			StringBuilder SBQuery = new StringBuilder();
			SBQuery
					.append("select EM.dcpsEmpId,EM.dcpsId,EM.name,PC.yearlyAmount,PC.dcpsSixPCYearlyId,FY.finYearCode FROM MstEmp EM ,RltDcpsSixPCYearly PC,SgvcFinYearMst FY");
			SBQuery
					.append(" where EM.dcpsEmpId=PC.dcpsEmpId and PC.finYearId=FY.finYearId and EM.ddoCode= :DDOCode");
			SBQuery
					.append(" and EM.dcpsId is not null and PC.finYearId= :yearId and PC.statusFlag in (:statusFlagFwded,:statusFlagAprd) ");
			SBQuery
					.append(" order by EM.name ASC");

			Query stQuery = ghibSession.createQuery(SBQuery.toString());
			stQuery.setParameter("DDOCode", lStrDDOCode);
			stQuery.setParameter("yearId", yearId);
			stQuery.setParameter("statusFlagFwded", 'G');
			stQuery.setParameter("statusFlagAprd", 'A');
			empList = stQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return empList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#getYears()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#getAllDDOListForPhyFormRcvd
	 * (java.lang.String, java.lang.String)
	 */
	public List getAllDDOListForArrearsForwarded(String lStrPostId,
			String lStrUserType,String gStrLocationCode) throws Exception {

		List listAllForms = null;
		List<ComboValuesVO> listAllFormsCombo = new ArrayList<ComboValuesVO>();
		StringBuilder lSBQuery = new StringBuilder();
		ComboValuesVO cmbVO;
		List lLstResultList = null;
		Iterator itr;
		Object[] obj;
		
		
		try {
			if (lStrUserType != null) {
				if (lStrUserType.equals("TO")) {
					lSBQuery.append("select distinct DM.ddoCode, DM.ddoName from OrgDdoMst DM, RltDcpsSixPCYearly PC, MstEmp EM, RltDdoOrg RD,CmnLocationMst CM");
					//lSBQuery.append(" WfJobMst WF,");
					lSBQuery.append(" WHERE " );
					//lSBQuery.append(" WF.jobRefId = PC.dcpsSixPCYearlyId AND PC.dcpsEmpId = EM.dcpsEmpId AND DM.ddoCode = EM.ddoCode AND WF.lstActPostId = :postId AND RD.locationCode = CM.locationCode ");
					lSBQuery.append(" PC.dcpsEmpId = EM.dcpsEmpId AND DM.ddoCode = RD.ddoCode AND DM.ddoCode = EM.ddoCode AND RD.locationCode = CM.locationCode AND PC.statusFlag in ('G','A') ");
					lSBQuery.append(" AND RD.locationCode = '" + gStrLocationCode.trim() + "'" );
					lSBQuery.append(" order by DM.ddoCode ASC");
					Query lQuery = ghibSession.createQuery(lSBQuery.toString());
					//lQuery.setParameter("postId", lStrPostId);
					lLstResultList = lQuery.list();

					if (lLstResultList != null && lLstResultList.size() > 0) {
						itr = lLstResultList.iterator();
						while (itr.hasNext()) {

							obj = (Object[]) itr.next();

							cmbVO = new ComboValuesVO();
							cmbVO.setId(obj[0].toString());
							cmbVO.setDesc(obj[1].toString().replaceAll("&",
									"And"));
							listAllFormsCombo.add(cmbVO);
						}
					}
				}
			} else {
				lSBQuery
						.append("select distinct DM.ddoCode, DM.ddoName from OrgDdoMst DM, MstEmp EM,WfJobMst WF");
				lSBQuery
						.append(" WHERE DM.ddoCode = EM.ddoCode AND WF.jobRefId = EM.dcpsEmpId AND WF.lstActPostId = :postId ");
				Query lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("postId", lStrPostId);
				listAllForms = lQuery.list();
			}

		} catch (Exception e) {
			gLogger
					.error("Exception occured from getAllDDOListForPhyFormRcvd of TreasuryDAOImpl is :: "
							+ e);
			e.printStackTrace();
		}
		if (lStrUserType.equals("TO")) {
			return listAllFormsCombo;
		} else {
			return listAllForms;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#getDdoCode(java.lang.Long)
	 */
	public String getDdoCode(Long lLngAsstPostId) throws Exception {

		StringBuilder lSBQuery = null;
		String lStrDdoCode = null;
		List lLstCodeList = null;
		Query hqlQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM RltDdoAsst RD, OrgDdoMst OD");
			lSBQuery
					.append(" WHERE OD.postId = RD.ddoPostId AND RD.asstPostId = :asstPostId ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("asstPostId", lLngAsstPostId);
			lLstCodeList = hqlQuery.list();
			lStrDdoCode = lLstCodeList.get(0).toString();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lStrDdoCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#getEmpListForSixPCArrearsYearly
	 * (java.lang.String, java.lang.Long, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	public List getEmpListForSixPCArrearsYearly(String lStrDDOCode,
			Long finYearId, String lStrDesignation, Map displaytag)
			throws Exception {

		List lLstEmpList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {

			lSBQuery = new StringBuilder();
			String[] columnValues = new String[] { "EM.Emp_name",
					"fy.fin_year_desc", "EM.DCPS_ID", "EM.Emp_name",
					"SPC.TOTAL_AMOUNT", "SPC.AMOUNT_PAID",
					"nvl(YPC.YEARLY_AMOUNT,0)", "YPC.status_flag",
					"YPC.remarks" };

			if (lStrDesignation != null && lStrDesignation.length() > 0) {

				lSBQuery
						.append(" SELECT fy.fin_year_desc,fy.fin_year_desc,EM.DCPS_ID,EM.Emp_name,SPC.TOTAL_AMOUNT,SPC.AMOUNT_PAID,nvl(YPC.YEARLY_AMOUNT,0),");
				lSBQuery
						.append(" nvl(ypc.DCPS_SIXPC_YEARLY_ID,0),EM.DCPS_EMP_ID,fy.fin_year_id,SPC.dcps_sixpc_id,YPC.status_flag,YPC.remarks,YPC.VOUCHER_NO,YPC.VOUCHER_DATE,YPC.TREASURY ");
				lSBQuery
						.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC ");
				lSBQuery
						.append(" WHERE  SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "
								+ finYearId);
				lSBQuery.append(" AND YPC.status_flag IN ('D','R')");
				lSBQuery.append(" AND Em.dcps_emp_id = spc.dcps_emp_id ");
				lSBQuery.append("AND fy.fin_year_id  = " + finYearId);
				lSBQuery
						.append("  AND SPC.status_flag = 'A' AND YPC.active_flag = 1 AND  EM.REG_STATUS = 1 AND  EM.DDO_CODE= '"
								+ lStrDDOCode + "'  ");
				lSBQuery
						.append(" AND EM.DESIGNATION='" + lStrDesignation + "'");
				// lSBQuery.append(" GROUP BY EM.DCPS_EMP_ID ");
				lSBQuery.append(" ORDER BY ");
			} else {
				lSBQuery
						.append(" SELECT fy.fin_year_desc,fy.fin_year_desc,EM.DCPS_ID,EM.Emp_name,SPC.TOTAL_AMOUNT,SPC.AMOUNT_PAID,nvl(YPC.YEARLY_AMOUNT,0),");
				lSBQuery
						.append(" nvl(ypc.DCPS_SIXPC_YEARLY_ID,0),EM.DCPS_EMP_ID,fy.fin_year_id,SPC.dcps_sixpc_id,YPC.status_flag,YPC.remarks,YPC.VOUCHER_NO,YPC.VOUCHER_DATE,YPC.TREASURY ");
				lSBQuery
						.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC ");
				lSBQuery
						.append(" WHERE  SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "
								+ finYearId);
				lSBQuery.append(" AND YPC.status_flag IN ('D','R')");
				lSBQuery.append(" AND Em.dcps_emp_id = spc.dcps_emp_id ");
				lSBQuery.append("AND fy.fin_year_id  = " + finYearId);
				lSBQuery
						.append("  AND SPC.status_flag = 'A' AND YPC.active_flag = 1 AND  EM.REG_STATUS = 1  AND EM.DDO_CODE= '"
								+ lStrDDOCode + "'");
				lSBQuery.append(" ORDER BY ");
			}

			String orderString = (displaytag
					.containsKey(Constants.KEY_SORT_ORDER) ? (String) displaytag
					.get(Constants.KEY_SORT_ORDER)
					: "asc");

			Integer orderbypara = null;

			if (displaytag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displaytag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " "
						+ orderString);
			} else {
				lSBQuery.append(" EM.Emp_name ASC");
			}

			hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

		//	Integer pageNo = (displaytag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displaytag.get(Constants.KEY_PAGE_NO): 1);
		//	hqlQuery.setFirstResult((pageNo.intValue() - 1)* Constants.PDWL_PAGE_SIZE);
		//	hqlQuery.setMaxResults(Constants.PDWL_PAGE_SIZE);

			lLstEmpList = hqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLstEmpList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#getEmpListForSixPCArrearsYearlyDDO
	 * (java.lang.String, java.lang.Long, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public List getEmpListForSixPCArrearsYearlyDDO(String lStrDDOCode,
			Long finYearId, String lStrPostId, String lStrDesignation,
			Map displaytag) throws Exception {

		List lLstEmpList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			lSBQuery = new StringBuilder();
			String[] columnValues = new String[] { "EM.Emp_name",
					"fy.fin_year_desc", "EM.DCPS_ID", "EM.Emp_name",
					"SPC.TOTAL_AMOUNT", "SPC.AMOUNT_PAID",
					"nvl(YPC.YEARLY_AMOUNT,0)", "YPC.status_flag",
					"YPC.remarks" };

			if (lStrDesignation != null && lStrDesignation.length() > 0) {

				lSBQuery.append(" SELECT fy.fin_year_desc,fy.fin_year_desc,EM.DCPS_ID,EM.Emp_name,SPC.TOTAL_AMOUNT,SPC.AMOUNT_PAID,nvl(YPC.YEARLY_AMOUNT,0),");
				lSBQuery.append(" nvl(ypc.DCPS_SIXPC_YEARLY_ID,0),EM.DCPS_EMP_ID,fy.fin_year_id,SPC.dcps_sixpc_id,YPC.status_flag,YPC.REMARKS,YPC.VOUCHER_NO,YPC.VOUCHER_DATE,YPC.TREASURY ");
				lSBQuery.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC");
				//lSBQuery.append(" ,wf_job_mst job   ");
				lSBQuery.append(" WHERE SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "+ finYearId);
				lSBQuery.append(" AND fy.fin_year_id =" + finYearId);
				//lSBQuery.append(" AND job.lst_act_post_id = '"+ lStrPostId+ "' AND job.doc_id = 700002 AND job.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID " );
				lSBQuery.append(" AND YPC.status_flag = 'F' "+ " AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND SPC.status_flag = 'A' AND  YPC.active_flag = 1 AND EM.REG_STATUS = 1  AND EM.DDO_CODE= '"
								+ lStrDDOCode + "'");
				lSBQuery.append(" AND EM.DESIGNATION='" + lStrDesignation + "'");
				// lSBQuery.append(" GROUP BY EM.DCPS_EMP_ID ");
				lSBQuery.append(" ORDER BY ");

			} else {
				lSBQuery.append(" SELECT fy.fin_year_desc,fy.fin_year_desc,EM.DCPS_ID,EM.Emp_name,SPC.TOTAL_AMOUNT,SPC.AMOUNT_PAID,nvl(YPC.YEARLY_AMOUNT,0),");
				lSBQuery.append(" nvl(ypc.DCPS_SIXPC_YEARLY_ID,0),EM.DCPS_EMP_ID,fy.fin_year_id,SPC.dcps_sixpc_id,YPC.status_flag,YPC.REMARKS,YPC.VOUCHER_NO,YPC.VOUCHER_DATE,YPC.TREASURY ");
				lSBQuery.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC ");
				//lSBQuery.append(" ,wf_job_mst job  ");
				lSBQuery.append(" WHERE SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "+ finYearId);
				lSBQuery.append(" AND fy.fin_year_id =" + finYearId);
				//lSBQuery.append(" AND job.lst_act_post_id = '"+ lStrPostId	+ "' AND job.doc_id = 700002 AND job.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID");
				lSBQuery.append(" AND YPC.status_flag = 'F' " + " AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND SPC.status_flag = 'A' AND  YPC.active_flag = 1 AND EM.REG_STATUS = 1   AND EM.DDO_CODE= '"
								+ lStrDDOCode + "'");
				lSBQuery.append(" ORDER BY ");
			}

			String orderString = (displaytag
					.containsKey(Constants.KEY_SORT_ORDER) ? (String) displaytag
					.get(Constants.KEY_SORT_ORDER)
					: "asc");

			Integer orderbypara = null;

			if (displaytag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displaytag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " "
						+ orderString);
			} else {
				lSBQuery.append(" EM.Emp_name ASC");
			}

			hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			//Integer pageNo = (displaytag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displaytag.get(Constants.KEY_PAGE_NO): 1);
			//hqlQuery.setFirstResult((pageNo.intValue() - 1)* Constants.PDWL_PAGE_SIZE);
			//hqlQuery.setMaxResults(Constants.PDWL_PAGE_SIZE);

			lLstEmpList = hqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLstEmpList;

	}

	public List getEmpScheduleList(String lStrDDOCode, Long finYearId,
			String lStrPostId, String lStrDesignation,String lStrSevarthID,String lStrNADdo) throws Exception {
		List lLstEmpList = null;
		StringBuilder lSBQuery = new StringBuilder();
		Query hqlQuery = null;

		
		if(lStrNADdo.length()>1){/////$t 25-6-2021 Schedule id
		
		lSBQuery.append(" SELECT sgv.FIN_YEAR_DESC,YPC.SCHEDULE_ID,YPC.status_flag,ypc.ddo_code||'-'||org.ddo_name,ypc.ddo_code,ypc.fin_year_id ");
		lSBQuery.append(" FROM rlt_dcps_sixpc_yearly YPC join mst_dcps_emp emp on YPC.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
		lSBQuery.append(" join SGVC_FIN_YEAR_MST sgv on ypc.FIN_YEAR_ID=sgv.FIN_YEAR_ID ");
		lSBQuery.append(" join org_ddo_mst org on ypc.ddo_code=org.ddo_code ");
		
		lSBQuery.append(" WHERE ");
		
		lSBQuery.append(" YPC.status_flag IN ('G') AND YPC.active_flag = 1 AND YPC.ddo_code like '"+ lStrDDOCode+ "%' "
				+ " and YPC.schedule_id is not null  group by YPC.SCHEDULE_ID,YPC.status_flag,sgv.FIN_YEAR_DESC,ypc.ddo_code,org.ddo_name,ypc.fin_year_id order by YPC.ddo_code ");//and emp.sevarth_id='MPWBKGF7901'
		}else{
			
			lSBQuery.append(" SELECT YPC.fin_year_id,YPC.SCHEDULE_ID,YPC.status_flag,cast(DECODE(YPC.status_flag,'A',1,'G',2)as bigint) ");
			lSBQuery.append(" FROM rlt_dcps_sixpc_yearly YPC join mst_dcps_emp emp on YPC.DCPS_EMP_ID=emp.DCPS_EMP_ID ");
			
			//lSBQuery.append(" ,wf_job_mst WF");

			lSBQuery.append(" WHERE ");
			//lSBQuery.append(" WF.lst_act_post_id = '"+ lStrPostId+ "' AND WF.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID ");
			
			if(lStrSevarthID.length()>0)/////$t 25-6-2021 Schedule id
		    lSBQuery.append(" emp.sevarth_id='"+lStrSevarthID+"' and ");
			
			lSBQuery.append(" YPC.status_flag IN ('G','A') and YPC.fin_year_id = "
							+ finYearId
							+ " AND  YPC.active_flag = 1 AND YPC.ddo_code = '"
							+ lStrDDOCode
							+ "' and YPC.schedule_id is not null  group by YPC.schedule_id,YPC.fin_year_id,YPC.status_flag ");//and emp.sevarth_id='MPWBKGF7901'
		}

		hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lLstEmpList = hqlQuery.list();
		return lLstEmpList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#getEmpListForSixPCArrearsYearlyTO
	 * (java.lang.String, java.lang.Long, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public List getEmpListForSixPCArrearsYearlyTO(String lStrDDOCode,
			Long finYearId, String lStrPostId, String lStrDesignation,
			Map displaytag, String lStrScheduleId) throws Exception {

		List lLstEmpList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;

		try {
			lSBQuery = new StringBuilder();
			String[] columnValues = new String[] { "EM.Emp_name",
					"fy.fin_year_desc", "EM.DCPS_ID", "EM.Emp_name",
					"SPC.TOTAL_AMOUNT", "SPC.AMOUNT_PAID",
					"nvl(YPC.YEARLY_AMOUNT,0)", "YPC.status_flag",
					"YPC.remarks" };

			if (lStrDesignation != null && lStrDesignation.length() > 0) {

				lSBQuery.append(" SELECT fy.fin_year_desc,fy.fin_year_desc,EM.DCPS_ID,EM.Emp_name,SPC.TOTAL_AMOUNT,SPC.AMOUNT_PAID,nvl(YPC.YEARLY_AMOUNT,0),");
				lSBQuery.append(" nvl(ypc.DCPS_SIXPC_YEARLY_ID,0),EM.DCPS_EMP_ID,fy.fin_year_id,SPC.dcps_sixpc_id,YPC.status_flag,YPC.DDO_CODE,YPC.VOUCHER_NO,YPC.VOUCHER_DATE,YPC.TREASURY");
				lSBQuery.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC");
				//lSBQuery.append(" ,wf_job_mst job ");
				lSBQuery.append(" WHERE SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "
								+ finYearId);
				lSBQuery.append(" AND fy.fin_year_id =" + finYearId);
				//lSBQuery.append(" AND job.lst_act_post_id = '"+ lStrPostId+ "' AND job.doc_id = 700002 AND job.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID ");
				lSBQuery.append(" AND YPC.status_flag IN ('G','A') AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND SPC.status_flag = 'A' AND  YPC.active_flag = 1 AND EM.REG_STATUS = 1 ");

				if (lStrDDOCode != null && !lStrDDOCode.equalsIgnoreCase("")) {
					lSBQuery.append(" AND EM.DDO_CODE= '" + lStrDDOCode + "'");
				}

				lSBQuery.append(" AND EM.DESIGNATION='" + lStrDesignation
						+ "' AND YPC.SCHEDULE_ID =" + lStrScheduleId);

				lSBQuery.append(" ORDER BY ");
			} else {

				lSBQuery.append(" SELECT fy.fin_year_desc,fy.fin_year_desc,EM.DCPS_ID,EM.Emp_name,SPC.TOTAL_AMOUNT,SPC.AMOUNT_PAID,nvl(YPC.YEARLY_AMOUNT,0),");
				lSBQuery.append(" nvl(ypc.DCPS_SIXPC_YEARLY_ID,0),EM.DCPS_EMP_ID,fy.fin_year_id,SPC.dcps_sixpc_id,YPC.status_flag,YPC.SCHEDULE_ID,YPC.VOUCHER_NO,YPC.VOUCHER_DATE,YPC.TREASURY");
				lSBQuery.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC");
				//lSBQuery.append(" ,wf_job_mst job ");
				lSBQuery.append(" WHERE SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "
								+ finYearId);
				lSBQuery.append(" AND fy.fin_year_id =" + finYearId);
				//lSBQuery.append(" AND job.lst_act_post_id = '"+ lStrPostId+ "' AND job.doc_id = 700002 AND job.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID ");
				lSBQuery.append(" AND YPC.status_flag IN ('G','A') AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND SPC.status_flag = 'A' AND  YPC.active_flag = 1 AND EM.REG_STATUS = 1 AND YPC.SCHEDULE_ID ="
								+ lStrScheduleId);

				if (lStrDDOCode != null && !lStrDDOCode.equalsIgnoreCase("")) {
					lSBQuery.append(" AND EM.DDO_CODE= '" + lStrDDOCode + "'");
				}

				lSBQuery.append(" ORDER BY ");
			}

			String orderString = (displaytag
					.containsKey(Constants.KEY_SORT_ORDER) ? (String) displaytag
					.get(Constants.KEY_SORT_ORDER)
					: "asc");

			Integer orderbypara = null;

			if (displaytag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displaytag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " "
						+ orderString);
			} else {
				lSBQuery.append(" EM.Emp_name ASC");
			}
			hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			//Integer pageNo = (displaytag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displaytag.get(Constants.KEY_PAGE_NO): 1);
			//hqlQuery.setFirstResult((pageNo.intValue() - 1)* Constants.PDWL_PAGE_SIZE);
			//hqlQuery.setMaxResults(Constants.PDWL_PAGE_SIZE);

			lLstEmpList = hqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLstEmpList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#getDcpsEmpIdFromSirxthPCYearlyId
	 * (java.lang.Long)
	 */
	public Long getDcpsEmpIdFromSirxthPCYearlyId(Long lLngDcpsSixthPCYearlyId)
			throws Exception {
		Long lLngDcpsEmpId = null;
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery
					.append("SELECT rlt.dcpsEmpId FROM RltDcpsSixPCYearly rlt WHERE rlt.dcpsSixPCYearlyId = :DCPSSixthPcYearlyId ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("DCPSSixthPcYearlyId", lLngDcpsSixthPCYearlyId);
			lLngDcpsEmpId = Long.valueOf(lQuery.list().get(0).toString());

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLngDcpsEmpId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#update(java.lang.Long)
	 */

	public void update(Long lLngDCPSEmpId, Long lLngCurPostID,
			Long lLngCurUserId, Date lDtCurDate) throws Exception {
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery
					.append("UPDATE RltDcpsSixPCYearly rlt SET rlt.statusFlag = 'F',rlt.updatedUserId = :updatedUserId ,rlt.updatedPostId = :updatedPostID ,rlt.updatedDate = :upadtedDate ");
			lSBQuery.append(" WHERE rlt.dcpsEmpId = :DCPSSixthPcYearlyId  ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("DCPSSixthPcYearlyId", lLngDCPSEmpId);
			lQuery.setLong("updatedUserId", lLngCurUserId);
			lQuery.setLong("updatedPostID", lLngCurPostID);
			lQuery.setParameter("upadtedDate", lDtCurDate);
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#updateForTO(java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.util.Date)
	 */
	public void updateForTO(Long lLngDcpsSixPcId, Long lLngAmountPaid,
			Long lLngCurPostID, Long lLngCurUserId, Date lDtCurDate)
			throws Exception {
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery
					.append("UPDATE MstSixPCArrears SPC SET SPC.amountPaid = SPC.amountPaid + :amountPaid ,SPC.updatedUserId = :updatedUserId ,SPC.updatedPostId = :updatedPostID ,SPC.updatedDate = :upadtedDate ,SPC.noOfInstallment = (SPC.noOfInstallment-1) ");
			lSBQuery.append(" WHERE SPC.dcpsSixPCId = :DCPSSixthPcId  ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("DCPSSixthPcId", lLngDcpsSixPcId);
			lQuery.setLong("amountPaid", lLngAmountPaid);
			lQuery.setLong("updatedUserId", lLngCurUserId);
			lQuery.setLong("updatedPostID", lLngCurPostID);
			lQuery.setParameter("upadtedDate", lDtCurDate);

			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#rejectForDDO(java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.util.Date)
	 */
	public void rejectForDDO(Long lLngDCPSEmpId, Long lLngCurPostID,
			Long lLngCurUserId, Date lDtCurDate) throws Exception {
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery
					.append("UPDATE RltDcpsSixPCYearly rlt SET rlt.statusFlag = 'R',rlt.updatedUserId = :updatedUserId ,rlt.updatedPostId = :updatedPostID ,rlt.updatedDate = :upadtedDate ");
			lSBQuery.append(" WHERE rlt.dcpsEmpId = :DCPSSixthPcYearlyId  ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("DCPSSixthPcYearlyId", lLngDCPSEmpId);
			lQuery.setLong("updatedUserId", lLngCurUserId);
			lQuery.setLong("updatedPostID", lLngCurPostID);
			lQuery.setParameter("upadtedDate", lDtCurDate);
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#rejectForTO(java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.util.Date)
	 */
	public void rejectForTO(Long lLngDCPSEmpId, Long lLngCurPostID,
			Long lLngCurUserId, Date lDtCurDate) throws Exception {
		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery
					.append("UPDATE RltDcpsSixPCYearly rlt SET rlt.statusFlag = 'R',rlt.updatedUserId = :updatedUserId ,rlt.updatedPostId = :updatedPostID ,rlt.updatedDate = :upadtedDate ");
			lSBQuery.append(" WHERE rlt.dcpsEmpId = :DCPSSixthPcYearlyId  ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("DCPSSixthPcYearlyId", lLngDCPSEmpId);
			lQuery.setLong("updatedUserId", lLngCurUserId);
			lQuery.setLong("updatedPostID", lLngCurPostID);
			lQuery.setParameter("upadtedDate", lDtCurDate);
			lQuery.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.SixPCArrearsDAO#getYearsForSixPCYearly()
	 */
	public List getYearsForSixPCYearly() throws Exception {
		String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2009' and '2013' order by finYearCode ";
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
	 * @seecom.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#
	 * getEmpListForSixPCArrearsYearlyCount(java.lang.String, java.lang.Long,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	public Integer getEmpListForSixPCArrearsYearlyCount(String lStrDDOCode,
			Long finYearId, String lStrDesignation) throws Exception {

		Integer count = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {

			lSBQuery = new StringBuilder();

			if (lStrDesignation != null && lStrDesignation.length() > 0) {

				lSBQuery.append(" SELECT count(*) ");
				lSBQuery
						.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC ");
				lSBQuery
						.append(" WHERE  SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "
								+ finYearId);
				lSBQuery.append(" AND YPC.status_flag IN ('D','R')");
				lSBQuery.append(" AND EM.dcps_emp_id = spc.dcps_emp_id ");
				lSBQuery.append(" AND fy.fin_year_id  = " + finYearId);
				lSBQuery
						.append(" AND SPC.status_flag = 'A' AND YPC.active_flag = 1 AND  EM.REG_STATUS = 1 AND  EM.DDO_CODE= '"
								+ lStrDDOCode + "' ");
				lSBQuery
						.append(" AND EM.DESIGNATION='" + lStrDesignation + "'");
				// lSBQuery.append(" GROUP BY EM.DCPS_EMP_ID ");

			} else {
				lSBQuery.append(" SELECT count(*) ");
				lSBQuery
						.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC ");
				lSBQuery
						.append(" WHERE  SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "
								+ finYearId);
				lSBQuery.append(" AND YPC.status_flag IN ('D','R')");
				lSBQuery.append(" AND Em.dcps_emp_id = spc.dcps_emp_id ");
				lSBQuery.append(" AND fy.fin_year_id  = " + finYearId);
				lSBQuery
						.append(" AND SPC.status_flag = 'A' AND YPC.active_flag = 1 AND  EM.REG_STATUS = 1  AND EM.DDO_CODE= '"
								+ lStrDDOCode + "' ");
			}

			hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			if (hqlQuery.list() != null && hqlQuery.list().size() > 0) {
				count = Integer.parseInt(hqlQuery.list().get(0).toString());
			} else {
				count = 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#
	 * getEmpListForSixPCArrearsYearlyDDOCount(java.lang.String, java.lang.Long,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.util.Map)
	 */
	public Integer getEmpListForSixPCArrearsYearlyDDOCount(String lStrDDOCode,
			Long finYearId, String lStrPostId, String lStrDesignation)
			throws Exception {

		Integer count = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			lSBQuery = new StringBuilder();

			if (lStrDesignation != null && lStrDesignation.length() > 0) {

				lSBQuery.append(" SELECT count(*) ");
				lSBQuery.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC   ");
				//SBQuery.append(",wf_job_mst job");
				lSBQuery.append(" WHERE SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "+ finYearId);
				lSBQuery.append(" AND fy.fin_year_id =" + finYearId);
				//lSBQuery.append(" AND job.lst_act_post_id = '"+ lStrPostId	+ "' AND job.doc_id = 700002 AND job.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID ");
				lSBQuery.append(" AND YPC.status_flag = 'F' "+ " AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND SPC.status_flag = 'A' AND  YPC.active_flag = 1 AND EM.REG_STATUS = 1   AND EM.DDO_CODE= '"
								+ lStrDDOCode + "'");
				lSBQuery.append(" AND EM.DESIGNATION='" + lStrDesignation + "'");
				// lSBQuery.append(" GROUP BY EM.DCPS_EMP_ID ");
			} else {
				lSBQuery.append(" SELECT count(*) ");
				lSBQuery.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC  ");
				//SBQuery.append(",wf_job_mst job");
				lSBQuery.append(" WHERE SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "
								+ finYearId);
				lSBQuery.append(" AND fy.fin_year_id =" + finYearId);
				//lSBQuery.append(" AND job.lst_act_post_id = '"+ lStrPostId+ "' AND job.doc_id = 700002 AND job.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID ");
				lSBQuery.append(" AND YPC.status_flag = 'F' " + " AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND SPC.status_flag = 'A' AND  YPC.active_flag = 1 AND EM.REG_STATUS = 1   AND EM.DDO_CODE= '"+ lStrDDOCode + "'");
			}

			hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			if (hqlQuery.list() != null && hqlQuery.list().size() > 0) {
				count = Integer.parseInt(hqlQuery.list().get(0).toString());
			} else {
				count = 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return count;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.dcps.dao.SixPCArrearsYearlyDAO#
	 * getEmpListForSixPCArrearsYearlyTOCount(java.lang.String, java.lang.Long,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.util.Map)
	 */
	public Integer getEmpListForSixPCArrearsYearlyTOCount(String lStrDDOCode,
			Long finYearId, String lStrPostId, String lStrDesignation,
			String lStrSchedule) throws Exception {

		Integer count = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {

			lSBQuery = new StringBuilder();

			if (lStrDesignation != null && lStrDesignation.length() > 0) {

				lSBQuery.append(" SELECT count(*) ");
				lSBQuery.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC");
				//lSBQuery.append(" ,wf_job_mst job ");
				lSBQuery.append(" WHERE SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "
								+ finYearId);
				lSBQuery.append(" AND fy.fin_year_id =" + finYearId);
				//lSBQuery.append(" AND job.lst_act_post_id = '"+ lStrPostId+ "' AND job.doc_id = 700002 AND job.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID ");
				lSBQuery.append(" AND YPC.status_flag IN ('G','A')"+ " AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND SPC.status_flag = 'A' AND  YPC.active_flag = 1 AND EM.REG_STATUS = 1 and YPC.schedule_id = "
								+ lStrSchedule);

				if (lStrDDOCode != null && !lStrDDOCode.equalsIgnoreCase("")) {
					lSBQuery.append(" AND EM.DDO_CODE= '" + lStrDDOCode + "'");
				}
				lSBQuery.append(" AND EM.DESIGNATION='" + lStrDesignation + "'");
				// lSBQuery.append(" GROUP BY EM.DCPS_EMP_ID ");
			} else {

				lSBQuery.append(" SELECT count(*) ");
				lSBQuery.append(" FROM mst_dcps_emp EM,sgvc_fin_year_mst fy,mst_dcps_sixpc spc ,rlt_dcps_sixpc_yearly YPC");
				//lSBQuery.append(" ,wf_job_mst job ");
				lSBQuery.append(" WHERE SPC.dcps_emp_id = YPC.dcps_emp_id  AND YPC.fin_year_id = "+ finYearId);
				lSBQuery.append(" AND fy.fin_year_id =" + finYearId);
				//lSBQuery.append(" AND job.lst_act_post_id = '"+ lStrPostId+ "' AND job.doc_id = 700002 AND job.job_ref_id = YPC.DCPS_SIXPC_YEARLY_ID ");
				lSBQuery.append(" AND YPC.status_flag IN ('G','A')"+ " AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND SPC.status_flag = 'A' AND  YPC.active_flag = 1 AND EM.REG_STATUS = 1 and YPC.schedule_id = "
								+ lStrSchedule);
				if (lStrDDOCode != null && !lStrDDOCode.equalsIgnoreCase("")) {
					lSBQuery.append(" AND EM.DDO_CODE= '" + lStrDDOCode + "'");
				}
				// lSBQuery.append(" GROUP BY EM.DCPS_EMP_ID ");
			}

			hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			if (hqlQuery.list() != null && hqlQuery.list().size() > 0) {
				count = Integer.parseInt(hqlQuery.list().get(0).toString());
			} else {
				count = 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return count;

	}

	public Long getNextScheduleId() throws Exception {

		StringBuilder lSBQuery = null;
		Long lLngScheduleId = null;
		List lLstScheduleId = null;
		Query hqlQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT MAX(scheduleId) from RltDcpsSixPCYearly");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			lLstScheduleId = hqlQuery.list();

			if (lLstScheduleId.get(0) != null) {
				lLngScheduleId = Long.parseLong(lLstScheduleId.get(0)
						.toString()) + 1;
			} else {
				lLngScheduleId = 100001L;
			}
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLngScheduleId;
	}

	public List getSixPCYearlyIdPksForScheduleId(Long lLongScheduleID)
			throws Exception {

		List<Long> lListSixPCYearlyIdPkForScheduleId = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT RL.dcpsSixPCYearlyId ");
			lSBQuery.append(" FROM  RltDcpsSixPCYearly RL");
			lSBQuery.append(" WHERE RL.scheduleId = :scheduleId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("scheduleId", lLongScheduleID);

			lListSixPCYearlyIdPkForScheduleId = lQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lListSixPCYearlyIdPkForScheduleId;
	}

	public List getSixPCDtlsOnApprovalForScheduleId(Long lLongScheduleID)
			throws Exception {

		List lListSixPCDtlsOnApproval = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT RL.dcpsSixPCYearlyId,RL.yearlyAmount,MS.dcpsSixPCId ");
			lSBQuery.append(" FROM RltDcpsSixPCYearly RL,MstSixPCArrears MS");
			lSBQuery.append(" WHERE RL.dcpsEmpId = MS.dcpsEmpId ");
			lSBQuery.append(" AND RL.scheduleId = :scheduleId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("scheduleId", lLongScheduleID);

			lListSixPCDtlsOnApproval = lQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lListSixPCDtlsOnApproval;
	}
	
	public Boolean checkPayComPadmabhan(Long dcpsEmpId) {

		StringBuilder lSBQuery = new StringBuilder();

		Query lQuery = null;
		Boolean lBlFlagPadmanabhan = false;

		lSBQuery.append(" select payCommission FROM MstEmp EM");
		lSBQuery.append(" WHERE EM.dcpsEmpId = :dcpsEmpId ");
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("dcpsEmpId", dcpsEmpId);

		List tempList = lQuery.list();
		if(tempList != null)
		{
			if (tempList.size() != 0) {
				if(tempList.get(0) != null)
				{
					if(tempList.get(0).toString().trim().equals("700339"))
					{
						lBlFlagPadmanabhan = true;
					}
				}
			}
		}
		return lBlFlagPadmanabhan;
	}
	
	public List getAllTreasuries() {

		String query = "select LM.locationCode, LM.locName from CmnLocationMst LM where departmentId IN (100003, 100006) order by LM.locationCode ";
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
				lObjComboValuesVO.setDesc("("+obj[0].toString()+")"+obj[1].toString());
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
}
