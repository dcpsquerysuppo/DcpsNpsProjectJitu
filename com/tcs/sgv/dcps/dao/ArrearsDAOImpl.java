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
package com.tcs.sgv.dcps.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsContribution;
import com.tcs.sgv.pensionproc.dao.PensionProcComparators;

/**
 * 
 * Class Description -
 * 
 * 
 * @author Sneha
 * @version 0.1
 * @since JDK 5.0 Mar 4, 2011
 */
public class ArrearsDAOImpl extends GenericDaoHibernateImpl implements
		ArrearsDAO {

	/**
	 * @param type
	 */

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle
			.getBundle("resources/pensionproc/PensionCaseConstants");

	public ArrearsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.CommonDCPSArrearsDAO#getAllDepartment(java.lang.
	 * Long, java.lang.Long)
	 */

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
			lStrQuery
					.append(" WHERE dsgn.cmnLanguageMst.langId =:langId order by dsgn.dsgnName,dsgn.dsgnId ");
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

	public List getEmpListForSixPCArrears(String lStrDDOCode) throws Exception {
		List lLstEmpList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {

			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT EM.dcpsEmpId,EM.dcpsId,EM.name,PC.totalAmount,PC.dcpsSixPCId,PC.statusFlag ");
			lSBQuery
					.append(" FROM MstEmp EM ,MstSixPCArrears PC WHERE (PC.statusFlag = 'D' OR PC.statusFlag = 'R') AND EM.dcpsEmpId=PC.dcpsEmpId");
			lSBQuery
					.append(" AND EM.ddoCode= :DDOCode AND EM.dcpsId is not null ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("DDOCode", lStrDDOCode);
			lLstEmpList = hqlQuery.list();

		} catch (Exception e) {

			gLogger.error("Error is :" + e, e);
		}
		return lLstEmpList;

	}

	public List getEmpListForSixPCArrearsYearly(String lStrDDOCode,
			Long finYearId) throws Exception {
		List empList = null;
		try {
			StringBuilder SBQuery = new StringBuilder();

			SBQuery
					.append("SELECT fy.fin_year_desc,EM.DCPS_ID,EM.Emp_name,SPC.TOTAL_AMOUNT,SPC.AMOUNT_PAID,nvl(YPC.YEARLY_AMOUNT,0),nvl(ypc.DCPS_SIXPC_YEARLY_ID,0),EM.DCPS_EMP_ID,fy.fin_year_id,SPC.dcps_sixpc_id,YPC.status_flag");
			SBQuery.append(" FROM sgvc_fin_year_mst fy,mst_dcps_emp EM, ");
			SBQuery
					.append(" mst_dcps_sixpc SPC LEFT OUTER JOIN rlt_dcps_sixpc_yearly YPC ON SPC.dcps_emp_id=YPC.dcps_emp_id and YPC.fin_year_id="
							+ finYearId);
			SBQuery.append(" WHERE fy.fin_year_id =" + finYearId);
			SBQuery
					.append(" AND SPC.DCPS_EMP_ID = EM.DCPS_EMP_ID AND EM.REG_STATUS = 1 AND ((YPC.status_flag = 'D' OR YPC.status_flag = 'R') AND SPC.status_flag = 'A') AND EM.DDO_CODE= '"
							+ lStrDDOCode + "'");

			Query stQuery = ghibSession.createSQLQuery(SBQuery.toString());

			empList = stQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return empList;
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

	public List getEmpListForContribution(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId) {

		List empList = null;
		List finalList = new ArrayList();

		try {
			ghibSession = getSession();
			StringBuilder SBQuery = new StringBuilder();
			SBQuery
					.append("SELECT EM.DCPS_EMP_ID,EM.DCPS_ID,EM.EMP_NAME,EM.PAY_COMMISSION,nvl(CO.BASIC_PAY,EM.BASIC_PAY),nvl(CO.DCPS_CONTRIBUTION_ID,0),nvl(CO.TYPE_OF_PAYMENT,700046),nvl(CO.MONTH_ID,0),nvl(CO.FIN_YEAR_ID,0) "
							+ " FROM mst_dcps_emp EM LEFT OUTER JOIN TRN_DCPS_CONTRIBUTION CO ON EM.DCPS_EMP_ID=CO.DCPS_EMP_ID "
							+ " AND CO.MONTH_ID="
							+ monthId
							+ " AND CO.FIN_YEAR_ID="
							+ yearId
							+ " WHERE EM.DDO_CODE='"
							+ lStrDDOCode
							+ "'"
							+ " AND BILLGROUP_ID="
							+ lLongbillGroupId
							+ "  AND EM.REG_STATUS=1 ");
			Query stQuery = ghibSession.createSQLQuery(SBQuery.toString());

			empList = stQuery.list();

			for (Integer lInt1 = 0; lInt1 < empList.size(); lInt1++) {
				Object[] tempObjectList = (Object[]) empList.get(lInt1);
				Object[] newList = new Object[tempObjectList.length + 3];
				Integer lInt2 = 0;
				lInt2 = tempObjectList.length;
				for (lInt2 = 0; lInt2 < tempObjectList.length; lInt2++) {
					newList[lInt2] = tempObjectList[lInt2];
				}
				Double BasicPay = Double.parseDouble(tempObjectList[4]
						.toString());
				Double DP = 0d;
				Double DA = (BasicPay * 0.27);
				Double employeeContribution = ((double) BasicPay + DA) * 0.10;

				newList[lInt2] = DP;
				newList[lInt2 + 1] = DA;
				newList[lInt2 + 2] = employeeContribution;

				finalList.add(newList);
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			e.printStackTrace();
		}
		return finalList;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.ArrearsDAO#getEmpDtlsFromDDODesig(java.lang.Long,
	 * java.util.Map)
	 */
	public List getEmpDtlsFromDDODesig(String lStrDesigId, Map inputMap)
			throws Exception {
		List<MstEmp> lLstEmpDtls = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT emp.dcpsEmpId,emp.name FROM MstEmp emp WHERE emp.designation = :designation ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("designation", lStrDesigId);
			lLstEmpDtls = hqlQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger
					.info("Error  while executing getCadreList of  DCPSCadreMasterDAOImpl is "
							+ e);

		}
		return lLstEmpDtls;
		// SELECT dcps_emp_id,emp_name FROM mst_dcps_emp WHERE designation =
		// 101003
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.ArrearsDAO#getEmpNameFromId(java.lang.Long)
	 */
	public String getEmpNameFromId(Long lLngdcpsEmpId) throws Exception {
		String lStrEmpName = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT emp.name FROM MstEmp emp WHERE emp.dcpsEmpId = :dcpsEmpId");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("dcpsEmpId", lLngdcpsEmpId);
			lStrEmpName = hqlQuery.list().get(0).toString();

		} catch (Exception e) {
			e.printStackTrace();
			gLogger
					.info("Error  while executing getCadreList of  DCPSCadreMasterDAOImpl is "
							+ e);

		}
		return lStrEmpName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.ArrearsDAO#getAllSavedRequestsForTier(java.lang.
	 * Integer, java.lang.String)
	 */
	public List getAllSavedRequestsForTier(Integer lIntCriteria,
			String lStrPostId) throws Exception {

		List lListSavedRequests = null;
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		try {
			// For drafts
			if (lIntCriteria == 1) {

				lSBQuery
						.append(" SELECT tierI.employeeId,tierI.createdDate,desig.dsgnName,look.lookupName,tierI.tierICntrnbtnFromDate,tierI.tierICntrnbtnToDate");
				lSBQuery
						.append(" FROM MstDcpsTierICntrnbtn tierI,OrgDesignationMst desig,CmnLookupMst look");
				lSBQuery
						.append(" WHERE tierI.arrearstypeLookupid= look.lookupId AND tierI.designationId = desig.dsgnId ");

				/*
				 * SELECT
				 * tier.employee_id,tier.created_date,desig.dsgn_name,look
				 * .lookup_name
				 * ,tier.tierI_cntrnbtn_from_date,tierI_cntrnbtn_to_date FROM
				 * mst_dcps_tieri_cntrnbtn tier ,org_designation_mst desig
				 * ,cmn_lookup_mst look WHERE tier.arrearstype_lookupid =
				 * look.lookup_id AND tier.designation_id = desig.dsgn_id
				 */
			}

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lListSavedRequests = lQuery.list();

		} catch (Exception e) {
			gLogger
					.error("Exception occured from getAllSavedRequestsForTier ::: "
							+ e);
		}
		return lListSavedRequests;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.dcps.dao.ArrearsDAO#getAllDtlFromDesig(java.lang.String)
	 */
	public List getTierDtlFromDesig(String lStrDesignation) throws Exception {

		List lLstTierDtlsFromDesig = null;
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {

			lSBQuery
					.append(" SELECT tierI.employeeId,tierI.tierICntrnbtnFromDate,tierI.employeePensionId,tierI.employeeName,tierI.totalAmount,tierI.tierICntrnbtnFromDate,tierI.tierICntrnbtnToDate");
			lSBQuery
					.append(" FROM MstDcpsTierICntrnbtn tierI,OrgDesignationMst desig");
			lSBQuery
					.append(" WHERE tierI.designationId = desig.dsgnId AND desig.dsgnName = :dsgnName");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("dsgnName", lStrDesignation);
			lLstTierDtlsFromDesig = lQuery.list();

			/*
			 * SELECTemployee_id,tierI_cntrnbtn_from_date,employee_pension_id,
			 * employee_name
			 * ,total_amount,tierI_cntrnbtn_from_date,tierI_cntrnbtn_to_date
			 * FROM mst_dcps_tieri_cntrnbtn tierI,org_designation_mst desig
			 * WHERE desig.dsgn_name = 'ASI' AND tierI.designation_id =
			 * desig.dsgn_id
			 */
		} catch (Exception e) {
			e.printStackTrace();
			gLogger
					.error("Exception occurred From getTierDtlFromDesig :: "
							+ e);
		}

		return lLstTierDtlsFromDesig;

	}

	public List getEmpListForSixPCArrearAmountSchedule(String lStrDDOCode,
			Long yearId) throws Exception {
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

			e.printStackTrace();
			logger.error("Error is :" + e, e);

		}
		return empList;

	}

	public List getEmpListForContributionCorrection(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId) {

		List finalList = new ArrayList();

		try {
			ghibSession = getSession();
			StringBuilder SBQuery = new StringBuilder();

			SBQuery
					.append("SELECT EM.DCPS_EMP_ID,EM.DCPS_ID,EM.EMP_NAME,CO.DCPS_CONTRIBUTION_ID "
							+ " FROM mst_dcps_emp EM INNER JOIN TRN_DCPS_CONTRIBUTION CO ON EM.DCPS_EMP_ID=CO.DCPS_EMP_ID "
							+ " AND CO.MONTH_ID="
							+ monthId
							+ " AND CO.FIN_YEAR_ID="
							+ yearId
							+ " WHERE EM.DDO_CODE='"
							+ lStrDDOCode
							+ "'"
							+ " AND BILLGROUP_ID="
							+ lLongbillGroupId
							+ " AND EM.REG_STATUS=1 ");

			Query stQuery = ghibSession.createSQLQuery(SBQuery.toString());

			finalList = stQuery.list();

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			e.printStackTrace();
		}
		return finalList;
	}

	public Long getSixPCIDforEmpId(Long empId) {
		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long sixPCId = 0L;

		lSBQuery
				.append(" select dcpsSixPCId FROM MstSixPCArrears WHERE dcpsEmpId.dcpsEmpId = :empId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", empId);

		tempList = lQuery.list();
		sixPCId = tempList.get(0);
		return sixPCId;

	}

	public Long getRltSixPCYearlyIDforEmpId(Long empId) {
		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long rltDcpsSixPCYearlyId = 0L;

		lSBQuery
				.append(" select dcpsSixPCYearlyId FROM RltDcpsSixPCYearly WHERE dcpsEmpId.dcpsEmpId = :empId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("empId", empId);

		tempList = lQuery.list();
		rltDcpsSixPCYearlyId = tempList.get(0);
		return rltDcpsSixPCYearlyId;

	}

	public TrnDcpsContribution getContributionDetailsForEmpId(Long empId) {

		TrnDcpsContribution lObjTrnDcpsContribution = null;

		try {
			StringBuilder SBQuery = new StringBuilder();

			ghibSession = getSession();
			SBQuery.append("from TrnDcpsContribution where dcpsEmpId = :empId");
			Query lQuery = ghibSession.createQuery(SBQuery.toString());
			lQuery.setParameter("empId", empId);

			lObjTrnDcpsContribution = (TrnDcpsContribution) lQuery
					.uniqueResult();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lObjTrnDcpsContribution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.ArrearsDAO#getEmpIdforContributionId(java.lang.Long)
	 */
	public Long getEmpIdforContributionId(Long contributionId) {
		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList();
		Long empId = 0L;

		lSBQuery
				.append(" select dcpsEmpId FROM TrnDcpsContribution WHERE dcpsContributionId = :contributionId");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("contributionId", contributionId);

		tempList = lQuery.list();
		empId = tempList.get(0);
		return empId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.ArrearsDAO#getEmpListForContribution(java.lang.String
	 * , java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */

	public List getEmpListForContribution(String lStrDDOCode,
			Long lLongbillGroupId, Long monthId, Long yearId, String lStrUser,
			String lStrUse, String lStrPostId) {

		List empList = null;
		List finalList = new ArrayList();

		try {
			ghibSession = getSession();
			StringBuilder SBQuery = new StringBuilder();
			SBQuery
					.append("SELECT EM.DCPS_EMP_ID,EM.DCPS_ID,EM.EMP_NAME,EM.PAY_COMMISSION,nvl(CO.BASIC_PAY,EM.BASIC_PAY),nvl(CO.DCPS_CONTRIBUTION_ID,0),nvl(CO.TYPE_OF_PAYMENT,700046),nvl(CO.MONTH_ID,0),nvl(CO.FIN_YEAR_ID,0)"
							// +
							// ",cast(nvl(CO.startDate,0) as date),nvl(CO.endDate,0) "
							+ " FROM mst_dcps_emp EM LEFT OUTER JOIN TRN_DCPS_CONTRIBUTION CO ON EM.DCPS_EMP_ID=CO.DCPS_EMP_ID "
							+ " AND CO.MONTH_ID="
							+ monthId
							+ " AND CO.FIN_YEAR_ID=" + yearId);

			if (lStrUser.equals("TO") && lStrUse.equals("ViewForwarded")) {
				SBQuery
						.append(" JOIN wf_job_mst WF ON WF.job_ref_id=CO.DCPS_CONTRIBUTION_ID AND WF.lst_act_post_id = '"
								+ lStrPostId + "'");
			}

			SBQuery.append(" WHERE EM.DDO_CODE='" + lStrDDOCode + "'"
					+ " AND BILLGROUP_ID=" + lLongbillGroupId
					+ "  AND EM.REG_STATUS=1 ");

			if (lStrUser.equals("TO") && lStrUse.equals("ViewForwarded")) {
				SBQuery.append(" AND CO.REG_STATUS = 0");
			}

			if (lStrUser.equals("ATO") && lStrUse.equals("ViewRejected")) {
				SBQuery.append(" AND CO.REG_STATUS = -1");
			}

			Query stQuery = ghibSession.createSQLQuery(SBQuery.toString());

			empList = stQuery.list();

			for (Integer lInt1 = 0; lInt1 < empList.size(); lInt1++) {
				Object[] tempObjectList = (Object[]) empList.get(lInt1);
				Object[] newList = new Object[tempObjectList.length + 3];
				Integer lInt2 = 0;
				lInt2 = tempObjectList.length;
				for (lInt2 = 0; lInt2 < tempObjectList.length; lInt2++) {
					newList[lInt2] = tempObjectList[lInt2];
				}
				Double BasicPay = Double.parseDouble(tempObjectList[4]
						.toString());
				Double DP = 0d;
				Double DA = (BasicPay * 0.27);
				Double employeeContribution = ((double) BasicPay + DA) * 0.10;

				newList[lInt2] = DP;
				newList[lInt2 + 1] = DA;
				newList[lInt2 + 2] = employeeContribution;

				finalList.add(newList);
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			e.printStackTrace();
		}
		return finalList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.dcps.dao.ArrearsDAO#getMstDcpsTierICntrnbtnPk(java.lang.Long)
	 */
	public Long getMstDcpsTierICntrnbtnPk(Long lLngEmpId) throws Exception {
		Long lLngTierICntrnbtnId = null;
		try {
			getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery
					.append(" SELECT tier.tierICntrnbtnId FROM MstDcpsTierICntrnbtn tier ");
			lSBQuery.append(" WHERE tier.employeeId = :lLngDcpsEmpId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lLngDcpsEmpId", lLngEmpId);
			lLngTierICntrnbtnId = (Long) lQuery.list().get(0);
		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLngTierICntrnbtnId;
	}
}