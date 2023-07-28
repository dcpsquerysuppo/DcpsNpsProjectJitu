/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 29, 2011		Meeta Thacker								
 *******************************************************************************
 */
package com.tcs.sgv.gpf.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.gpf.valueobject.MstGpfMonthly;

/**
 * Class Description -
 * 
 * 
 * @author Meeta Thacker
 * @version 0.1
 * @since JDK 5.0 Aug 29, 2011
 */
public class ScheduleGenerationDAOImpl extends GenericDaoHibernateImpl implements ScheduleGenerationDAO {
	Session ghibSession = null;

	public ScheduleGenerationDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * Method to get Monthly Subscription of the employees of the provided Bill
	 * group (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getEmpMonthlySubsData(java.
	 * lang.Long, java.lang.Long, java.lang.Long)
	 */
	public List getEmpMonthlySubsData(Long lLngBillGroupId, Long lLngMonthId, Long lLngYearId) throws Exception {
		List lLstMonthlySubs = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT MGA.gpf_Acc_No,");
			lSBQuery
					.append(" COALESCE((SELECT monthly_subscription FROM mst_gpf_emp_subscription WHERE fin_year_id = "
							+ lLngYearId
							+ " AND effect_from_month="
							+ lLngMonthId.intValue()
							+ " AND gpf_acc_no = MGA.gpf_Acc_no AND status_flag='A' AND active_flag=1),(SELECT monthly_subscription FROM mst_emp_gpF_acc WHERE gpf_acc_no = MGA.gpf_Acc_no)) ");
			lSBQuery.append(" FROM Mst_dcps_Emp MGE,Mst_Emp_Gpf_Acc MGA");
			lSBQuery.append(" WHERE MGE.BILLGROUP_ID=" + lLngBillGroupId.doubleValue()
					+ " AND MGE.dcps_Emp_Id = MGA.mst_Gpf_Emp_Id AND MGE.dcps_or_gpf = 'N' AND MGE.emp_group='D'");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lLstMonthlySubs = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getEmpMonthlySubsData of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstMonthlySubs;
	}

	/*
	 * Method to get the Recovery Details if any Advance recovery is in progress
	 * for the employee (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getEmpAdvanceRecovery(java.
	 * lang.String)
	 */
	public List getEmpAdvanceRecovery(String lStrGpfAccNo) throws Exception {
		Double lDblAdvanceRecovery = 0d;
		List lLstAdvanceRecovery = new ArrayList();
		List lLstFinalList = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		Date lDtCurDate = new Date();
		Date lDtStartDate = new Date(lDtCurDate.getYear(), lDtCurDate.getMonth(), 01);

		try {
			lSBQuery
					.append("SELECT installmentsLeft,installmentAmount,oddInstallment AS recovery,transactionId,noOfInstallments,sanctionedDate,oddInstallmentNo");
			lSBQuery.append(" FROM MstGpfAdvance");
			lSBQuery
					.append(" WHERE gpfAccNo = :gpfAccNo AND installmentsLeft>0 AND statusFlag = 'A' AND sanctionedDate < :prevDate");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("prevDate", lDtStartDate);

			lLstAdvanceRecovery = lQuery.list();
			if (lLstAdvanceRecovery != null && lLstAdvanceRecovery.size() > 0) {
				Object[] lObjRecoverRecord = null;
				Integer lIntInstallmentsLeft = 0;
				Integer lIntTotalInstallments = 0;
				Double lDblInstallmentAmt = 0d;
				Double lDblOddInstallment = 0d;
				Integer lIntOddinstallmentNo = 0;
				for (int i = 0; i < lLstAdvanceRecovery.size(); i++) {
					lObjRecoverRecord = (Object[]) lLstAdvanceRecovery.get(i);
					lIntInstallmentsLeft = (Integer) lObjRecoverRecord[0];
					lIntTotalInstallments = (Integer) lObjRecoverRecord[4];
					lDblInstallmentAmt = (Double) lObjRecoverRecord[1];
					lDblOddInstallment = (Double) lObjRecoverRecord[2];
					if (lObjRecoverRecord[6] != null) {
						lIntOddinstallmentNo = (Integer) lObjRecoverRecord[6];
					}
					if (lDblOddInstallment != 0
							&& ((lIntInstallmentsLeft == 1 && lIntOddinstallmentNo == 800057) || (lIntInstallmentsLeft == lIntTotalInstallments && lIntOddinstallmentNo == 800056))) {
						lDblAdvanceRecovery += lDblOddInstallment;
					} else {
						lDblAdvanceRecovery += lDblInstallmentAmt;
					}
					lObjRecoverRecord[2] = lDblAdvanceRecovery;
					lObjRecoverRecord[0] = lIntTotalInstallments - lIntInstallmentsLeft + 1;
					lLstFinalList.add(lObjRecoverRecord);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in getEmpAdvanceRecovery of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstFinalList;
	}

	/*
	 * Method to Get the opening GPF Account balance of employee for the month
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getOpeningBalForCurrMonth(java
	 * .lang.String, java.lang.Long, java.lang.Long)
	 */
	public Double getOpeningBalForCurrMonth(String lStrGpfAccNo, Long lLngMonthId, Long lLngYearId) throws Exception {
		Double lDblOpeningBal = null;
		List lLstPreviousMonthClosing = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		try {
			// in case of April month get the closing balance of previous year
			if (lLngMonthId == 4) {
				lSBQuery
						.append("SELECT closingBalance FROM MstGpfYearly WHERE gpfAccNo = :gpfAccNo AND finYearId = :finYearId");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
				lQuery.setParameter("finYearId", lLngYearId - 1);
			} else if (lLngMonthId == 1) {
				lSBQuery
						.append("SELECT closingBalance FROM MstGpfMonthly WHERE gpfAccNo = :gpfAccNo AND monthId = :monthId AND finYearId = :finYearId AND status = 'A'");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
				lQuery.setParameter("monthId", 12L);
				lQuery.setParameter("finYearId", lLngYearId - 1);
			} else {
				lSBQuery
						.append("SELECT closingBalance FROM MstGpfMonthly WHERE gpfAccNo = :gpfAccNo AND monthId = :monthId AND finYearId = :finYearId AND status = 'A'");
				lQuery = ghibSession.createQuery(lSBQuery.toString());
				lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
				lQuery.setParameter("monthId", lLngMonthId - 1);
				lQuery.setParameter("finYearId", lLngYearId);
			}

			lLstPreviousMonthClosing = lQuery.list();
			if (lLstPreviousMonthClosing != null && lLstPreviousMonthClosing.size() > 0
					&& lLstPreviousMonthClosing.get(0) != null) {
				lDblOpeningBal = Double.parseDouble((lLstPreviousMonthClosing.get(0).toString()));
			} else {
				lDblOpeningBal = 0d;
			}
		} catch (Exception e) {
			logger.error("Exception in getOpeningBalForCurrMonth of ScheduleGenerationDAOImpl  : ", e);
		}
		return lDblOpeningBal;
	}

	/*
	 * Method to check if Schedule for the month is already generated
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#isScheduleAlreadyGenerated(
	 * java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	public Boolean isScheduleAlreadyGenerated(Long lLngBillGroupId, Long lLngMonthId, Long lLngYearId) throws Exception {
		List lLstScheduleData = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT mstGpfMonthlyId from MstGpfMonthly ");
			lSBQuery
					.append(" WHERE billgroupId= :billgroupId AND (finYearId > :finYearId OR (finYearId = :finYearId AND monthId >=:monthId)) AND status !='D'");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billgroupId", lLngBillGroupId);
			lQuery.setParameter("finYearId", lLngYearId);
			lQuery.setParameter("monthId", lLngMonthId);

			lLstScheduleData = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in isScheduleAlreadyGenerated of ScheduleGenerationDAOImpl  : ", e);
		}
		if (lLstScheduleData == null || lLstScheduleData.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Method to check if the schedule for the month is approved (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#isScheduleAlreadyApproved(java
	 * .lang.Long, java.lang.Long, java.lang.Long)
	 */
	public Boolean isScheduleAlreadyApproved(Long lLngBillGroupId, Long lLngMonthId, Long lLngYearId) throws Exception {
		List lLstScheduleData = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		// check if any schedule present for the bill group
		lSBQuery.append("SELECT mstGpfMonthlyId from MstGpfMonthly ");
		lSBQuery.append(" WHERE billgroupId= :billgroupId AND status not IN ('D','X')");

		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("billgroupId", lLngBillGroupId);

		lLstScheduleData = lQuery.list();
		// if its the first schedule for the bill group then return true for any
		// month-year
		if (lLstScheduleData == null || lLstScheduleData.size() == 0) {
			return true;
		}
		// else check the last schedule is approved or not
		else {
			List lLstScheduleData2 = new ArrayList();
			StringBuilder lSBQuery2 = new StringBuilder();

			lSBQuery2.append("SELECT mstGpfMonthlyId from MstGpfMonthly ");
			lSBQuery2
					.append(" WHERE billgroupId= :billgroupId AND monthId =:monthId AND finYearId = :finYearId AND status='A'");

			Query lQuery2 = ghibSession.createQuery(lSBQuery2.toString());
			lQuery2.setParameter("billgroupId", lLngBillGroupId);
			lQuery2.setParameter("finYearId", lLngYearId);
			lQuery2.setParameter("monthId", lLngMonthId);

			lLstScheduleData2 = lQuery2.list();
			if (lLstScheduleData2 == null || lLstScheduleData2.size() == 0) {
				return false;
			} else {
				return true;
			}
		}
	}

	/*
	 * Method to get the DDO Office name. Used for Schedule Report (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getOfficeNameForOfficeId(java
	 * .lang.Long)
	 */
	public List getOfficeNameForOfficeId(Long lLngOfficeId) throws Exception {
		List lLstOffice = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT dcpsDdoOfficeName,dcpsDdoCode FROM DdoOffice");
			lSBQuery.append(" WHERE dcpsDdoOfficeIdPk=:officeId");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("officeId", lLngOfficeId);

			lLstOffice = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getOfficeNameForOfficeId of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstOffice;
	}

	/*
	 * Method to get the Treasury Code for DDO Code (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getTreasuryCodeForDDO(java.
	 * lang.String)
	 */
	public String getTreasuryCodeForDDO(String lStrDdoCode) {

		String lStrTreasuryName = null;
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("SELECT LM.locationCode FROM RltDdoOrg RO, CmnLocationMst LM ");
			sb.append("WHERE RO.ddoCode = :ddoCode AND	LM.locationCode = RO.locationCode");
			Query selectQuery = ghibSession.createQuery(sb.toString());
			selectQuery.setParameter("ddoCode", lStrDdoCode);
			lStrTreasuryName = selectQuery.list().get(0).toString();
		} catch (Exception e) {
			logger.error("Exception in getTreasuryCodeForDDO of ScheduleGenerationDAOImpl  : ", e);
		}
		return lStrTreasuryName;
	}

	/*
	 * Method to get the Treasury Name for DDO Code (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getTreasuryNameForDDO(java.
	 * lang.String)
	 */
	public String getTreasuryNameForDDO(String lStrDdoCode) {

		String lStrTreasuryName = null;
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("SELECT LM.locName FROM RltDdoOrg RO, CmnLocationMst LM ");
			sb.append("WHERE RO.ddoCode = :ddoCode AND	LM.locationCode = RO.locationCode");
			Query selectQuery = ghibSession.createQuery(sb.toString());
			selectQuery.setParameter("ddoCode", lStrDdoCode);
			lStrTreasuryName = selectQuery.list().get(0).toString();
		} catch (Exception e) {
			logger.error("Exception in getTreasuryNameForDDO of ScheduleGenerationDAOImpl  : ", e);
		}
		return lStrTreasuryName;
	}

	/*
	 * Method to get the Draft Schedules For the Bill groups of provided DDO
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getDraftScheduleForBillGroups
	 * (java.lang.String, java.lang.Long)
	 */
	public List getDraftScheduleForBillGroups(String lStrDdoCode, Long lLngYearId) {
		List lLstScheduleData = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		Long lLngNxtYearId = lLngYearId + 1l;
		try {
			lSBQuery
					.append("SELECT MDB.dcpsDdoBillDescription,SMM.monthName,SYM.finYearDesc,SUM(MGM.regularSubscription)+SUM(MGM.advanceRecovery) ,MGM.status,MGM.voucherNo,MGM.voucherDate,MGM.billgroupId,MGM.monthId,MGM.finYearId");
			lSBQuery.append(" FROM MstGpfMonthly MGM,MstDcpsBillGroup MDB,SgvaMonthMst SMM,SgvcFinYearMst SYM");
			lSBQuery
					.append(" WHERE ((MGM.finYearId = :finYearId AND MGM.monthId>3) OR (MGM.finYearId = :nxtFinYearId AND MGM.monthId<=3)) AND MGM.billgroupId = MDB.dcpsDdoBillGroupId AND MDB.dcpsDdoCode=:ddoCode");
			lSBQuery
					.append(" AND SMM.monthId=MGM.monthId AND SYM.finYearId=:finYearId AND MGM.status IN ('P','A') GROUP BY MGM.billgroupId,MGM.monthId,  ");
			lSBQuery
					.append(" MDB.dcpsDdoBillDescription,SMM.monthName,SYM.finYearDesc,MGM.status,MGM.voucherNo,MGM.voucherDate,MGM.finYearId  ");
			lSBQuery.append(" order by MGM.finYearId DESC,MGM.monthId DESC");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("finYearId", lLngYearId);
			lQuery.setParameter("nxtFinYearId", lLngNxtYearId);
			lQuery.setParameter("ddoCode", lStrDdoCode);

			lLstScheduleData = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getDraftScheduleForBillGroups of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstScheduleData;

	}

	/*
	 * Method to get the discarded schedules of bill group of provided DDO
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getDiscardedScheduleForBillGroups
	 * (java.lang.String, java.lang.Long)
	 */
	public List getDiscardedScheduleForBillGroups(String lStrDdoCode, Long lLngYearId) {
		List lLstScheduleData = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		Long lLngNxtYearId = lLngYearId + 1l;
		try {
			lSBQuery
					.append("SELECT MDB.dcpsDdoBillDescription,SMM.monthName,SYM.finYearDesc,SUM(MGM.regularSubscription)+SUM(MGM.advanceRecovery) ,MGM.status,MGM.voucherNo,MGM.voucherDate");
			lSBQuery.append(" FROM MstGpfMonthly MGM,MstDcpsBillGroup MDB,SgvaMonthMst SMM,SgvcFinYearMst SYM");
			lSBQuery
					.append(" WHERE ((MGM.finYearId = :finYearId AND MGM.monthId>3) OR (MGM.finYearId = :nxtFinYearId AND MGM.monthId<=3)) AND MGM.billgroupId = MDB.dcpsDdoBillGroupId AND MDB.dcpsDdoCode=:ddoCode");
			lSBQuery
					.append(" AND SMM.monthId=MGM.monthId AND SYM.finYearId=:finYearId AND MGM.status = 'D' GROUP BY  MDB.dcpsDdoBillDescription,SMM.monthName,SYM.finYearDesc,MGM.status,MGM.voucherNo,MGM.voucherDate ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("finYearId", lLngYearId);
			lQuery.setParameter("nxtFinYearId", lLngNxtYearId);
			lQuery.setParameter("ddoCode", lStrDdoCode);

			lLstScheduleData = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getDiscardedScheduleForBillGroups of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstScheduleData;

	}

	/*
	 * Method to get the DDO Code form post id of DDO (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getDdoCodeForDDO(java.lang.
	 * Long)
	 */
	public String getDdoCodeForDDO(Long lLngPostId) {

		String lStrDdoCode = null;
		List lLstDdoDtls = null;
		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();

			lStrDdoCode = lLstDdoDtls.get(0).toString();
		} catch (Exception e) {
			logger.error("Exception in getDdoCodeForDDO of ScheduleGenerationDAOImpl  : ", e);
		}
		return lStrDdoCode;
	}

	/*
	 * Method to get the DDO office details from post id of DDO (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getDdoInfoForPost(java.lang
	 * .Long)
	 */
	public List getDdoInfoForPost(Long lLngPostId) {

		List lLstDdoDtls = null;
		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append(" SELECT OD.ddoCode,OD.ddoOffice");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getDdoInfoForPost of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstDdoDtls;
	}

	/**
	 * Method to get the DDO Code for DDO post id
	 * 
	 * @param lLngPostId
	 * @return
	 */
	public String getDdoOfficeForDDO(Long lLngPostId) {

		String lStrDdoCode = null;
		List lLstDdoDtls = null;
		getSession();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();

			lStrDdoCode = lLstDdoDtls.get(0).toString();
		} catch (Exception e) {
			logger.error("Exception in getDdoOfficeForDDO of ScheduleGenerationDAOImpl  : ", e);
		}
		return lStrDdoCode;
	}

	/*
	 * Method to get the Month list with Id and Month Name (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getMonths()
	 */
	public List getMonths() {
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		try {
			String query = "select monthId,monthName from SgvaMonthMst where monthId < 13";

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
		} catch (Exception e) {
			logger.error("Exception in getMonths of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstReturnList;
	}

	/*
	 * Method to get the Financial year list with Id and Year Code (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getYears()
	 */
	public List getYears() {
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		try {
			String query = "select finYearId,finYearCode from SgvcFinYearMst where finYearCode between '2008' and '2015'";

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
		} catch (Exception e) {
			logger.error("Exception in getYears of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstReturnList;
	}

	// public List getCurrentOffices(String lStrDdoCode) {
	//
	// ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
	// ComboValuesVO cmbVO;
	// Object[] obj;
	//
	// String query =
	// "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice where dcpsDdoCode= :ddoCode";
	//
	// StringBuilder sb = new StringBuilder();
	// sb.append(query);
	// Query selectQuery = ghibSession.createQuery(sb.toString());
	// selectQuery.setParameter("ddoCode", lStrDdoCode);
	// List resultList = selectQuery.list();
	//
	// cmbVO = new ComboValuesVO();
	//
	// if (resultList != null && resultList.size() > 0) {
	// cmbVO = new ComboValuesVO();
	// cmbVO.setId("-1");
	// cmbVO.setDesc("-- Select --");
	// finalList.add(cmbVO);
	// Iterator it = resultList.iterator();
	// while (it.hasNext()) {
	// cmbVO = new ComboValuesVO();
	// obj = (Object[]) it.next();
	// cmbVO.setId(obj[0].toString());
	// cmbVO.setDesc(obj[1].toString());
	// finalList.add(cmbVO);
	// }
	// }
	//
	// return finalList;
	// }

	/*
	 * Method to get the Bill group List (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getBillGroups(java.lang.String)
	 */
	public List getBillGroups(String lStrDdoCode) throws Exception {
		List<Object> lLstReturnList = null;
		StringBuilder sb = new StringBuilder();
		try {
			String query = "select BG.dcpsDdoBillGroupId, BG.dcpsDdoBillDescription from MstDcpsBillGroup BG WHERE dcpsDdoCode=:ddoCode";

			sb.append(query);
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
		} catch (Exception e) {
			logger.error("Exception in getBillGroups of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstReturnList;
	}

	/*
	 * Method to get the Advance Amount Sanctioned to employee for the Month
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getAdvanceSanctionedForMonth
	 * (java.lang.String, java.lang.Integer, java.lang.String)
	 */
	public Double getAdvanceSanctionedForMonth(String lStrGpfAccNo, Integer lLngMonthId, String lStrFinYear) {

		List lLstAdvanceSanc = null;
		Double lDblAdvanceSanctioned = 0d;
		Integer lIntYearCode = Integer.parseInt(lStrFinYear.substring(0, 4));
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT SUM(amountSanctioned)");
			lSBQuery.append(" FROM MstGpfAdvance");
			lSBQuery
					.append(" WHERE statusFlag='A' AND gpfAccNo=:gpfAccNo AND MONTH(sanctionedDate) =:monthId AND YEAR(sanctionedDate)=:yearCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("monthId", lLngMonthId);
			lQuery.setParameter("yearCode", lIntYearCode);

			lLstAdvanceSanc = lQuery.list();
			if (lLstAdvanceSanc != null && lLstAdvanceSanc.size() > 0 && lLstAdvanceSanc.get(0) != null) {
				lDblAdvanceSanctioned = (Double) lLstAdvanceSanc.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getAdvanceSanctionedForMonth of ScheduleGenerationDAOImpl  : ", e);
		}
		return lDblAdvanceSanctioned;
	}

	/*
	 * Method to get the Financial year Code from year Id (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getFinYearCodeForFinYearId(
	 * java.lang.Long)
	 */
	public String getFinYearCodeForFinYearId(Long lLngFinYearId) {
		List lLstYearCode = null;
		String lStrYearCode = "";
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT finYearDesc");
			lSBQuery.append(" FROM SgvcFinYearMst");
			lSBQuery.append(" WHERE finYearId=:finYearId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("finYearId", lLngFinYearId);

			lLstYearCode = lQuery.list();
			if (lLstYearCode != null && lLstYearCode.size() > 0) {
				lStrYearCode = lLstYearCode.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Exception in getFinYearCodeForFinYearId of ScheduleGenerationDAOImpl  : ", e);
		}
		return lStrYearCode;
	}

	/*
	 * Method to get the Challan Paid in this month (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getChallanPaidForMonth(java
	 * .lang.String, java.lang.Integer, java.lang.String, java.lang.Long)
	 */
	public Double getChallanPaidForMonth(String lStrGpfAccNo, Integer lLngMonthId, String lStrFinYear,
			Long lLngChallanType) {

		List lLstChallan = null;
		Double lDblChallanAmount = 0d;
		Integer lIntYearCode = Integer.parseInt(lStrFinYear.substring(0, 4));
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT SUM(amount)");
			lSBQuery.append(" FROM MstGpfChallanDtls");
			lSBQuery
					.append(" WHERE gpfAccNo=:gpfAccNo AND MONTH(createdDate) =:monthId AND YEAR(createdDate)=:yearCode AND challanType=:challanType");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("monthId", lLngMonthId);
			lQuery.setParameter("yearCode", lIntYearCode);
			lQuery.setParameter("challanType", lLngChallanType);

			lLstChallan = lQuery.list();
			if (lLstChallan != null && lLstChallan.size() > 0 && lLstChallan.get(0) != null) {
				lDblChallanAmount = (Double) lLstChallan.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getChallanPaidForMonth of ScheduleGenerationDAOImpl  : ", e);
		}
		return lDblChallanAmount;
	}

	/*
	 * Method to get the Arrear details of employee for the month (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getArrearsDetails(java.lang
	 * .String, java.lang.Long, java.lang.Long)
	 */
	public Double getArrearsDetails(String lStrGpfAccNo, Long lLngMonthId, Long lLongYearId) {

		List lLstArrears = null;
		Double lDblArrearAmount = 0d;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT arrearAmount");
			lSBQuery.append(" FROM MstGpfArrearsYearly");
			lSBQuery.append(" WHERE gpfAccNo=:gpfAccNo AND monthId = :monthId AND yearId = :yearId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("monthId", lLngMonthId);
			lQuery.setParameter("yearId", lLongYearId);

			lLstArrears = lQuery.list();
			if (lLstArrears != null && lLstArrears.size() > 0) {
				lDblArrearAmount = (Double) lLstArrears.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getArrearsDetails of ScheduleGenerationDAOImpl  : ", e);
		}
		return lDblArrearAmount;
	}

	/*
	 * Method to get the Employee details for Ledger Report (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getEmpDataForLedger(java.lang
	 * .String, java.lang.Long)
	 */
	public List getEmpDataForLedger(String lStrGpfAccNo, Long lLngYearId) {
		List lLstEmpDtls = null;

		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append(" SELECT ME.name,MGA.sevaarthId,ODM.dsgnName,MDO.dcpsDdoOfficeName,MGY.closingBalance");
			lSBQuery.append(" FROM MstEmp ME,MstEmpGpfAcc MGA,MstGpfYearly MGY,OrgDesignationMst ODM,DdoOffice MDO");
			lSBQuery
					.append(" WHERE MGY.gpfAccNo= MGA.gpfAccNo AND MGY.finYearId = :yearId AND ME.dcpsEmpId = MGA.mstGpfEmpId AND ME.dcpsOrGpf='N'");
			lSBQuery
					.append(" AND MGA.gpfAccNo=:gpfAccNo AND ODM.dsgnId=ME.designation AND ME.currOff=MDO.dcpsDdoOfficeIdPk");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("yearId", lLngYearId);
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);

			lLstEmpDtls = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getEmpDataForLedger of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstEmpDtls;
	}

	/*
	 * Method to get Month and Year Id of last generated schedule (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getLastScheduleData(java.lang
	 * .String)
	 */
	public List getLastScheduleData(String lStrGpfAccNo) throws Exception {
		List lLstLastSchedule = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT monthId,finYearId");
			lSBQuery.append(" FROM MstGpfMonthly");
			lSBQuery.append(" WHERE gpfAccNo = :gpfAccNo ORDER BY finYearId DESC,monthId DESC LIMIT 1");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);

			lLstLastSchedule = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getLastScheduleData of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstLastSchedule;
	}

	/*
	 * Method to get the pending schedules from Monthly Table using Billgroup
	 * Id, Month Id and Year Id (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getPendingSchedule(java.lang
	 * .Long, java.lang.Long, java.lang.Long)
	 */
	public List getPendingSchedule(Long lLngBillgroupId, Long lLngMonthId, Long lLongYearId) {
		List lLstScheduleData = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery
					.append("FROM MstGpfMonthly WHERE billgroupId=:billgroupId AND monthId=:monthId AND finYearId=:finYearId AND status='P'");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billgroupId", lLngBillgroupId);
			lQuery.setParameter("monthId", lLngMonthId);
			lQuery.setParameter("finYearId", lLongYearId);

			lLstScheduleData = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getPendingSchedule of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstScheduleData;
	}

	/*
	 * Method to get the Start date of given financial year (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getStartDateOfFinancialYear
	 * (java.lang.Long)
	 */
	public Date getStartDateOfFinancialYear(Long lLngFinYearId) {
		List lLstFromDate = null;
		Date lDtStartDate = null;

		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("SELECT fromDate");
			lSBQuery.append(" FROM SgvcFinYearMst");
			lSBQuery.append(" WHERE finYearId=:finYearId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("finYearId", lLngFinYearId);

			lLstFromDate = lQuery.list();
			if (lLstFromDate != null && lLstFromDate.size() > 0) {
				lDtStartDate = (Date) lLstFromDate.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getStartDateOfFinancialYear of ScheduleGenerationDAOImpl  : ", e);
		}
		return lDtStartDate;
	}

	/*
	 * Method to get Monthly Data for insertion in Payroll Module (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.gpf.dao.ScheduleGenerationDAO#getMonthlyEmpListForPayroll
	 * (java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	public List getMonthlyEmpListForPayroll(Long lLngBillgroupId, Long lLngMonthId, Long lLongYearId) {
		List lLstScheduleData = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery
					.append("SELECT ME.orgEmpMstId,MGM.regularSubscription,MGM.advanceRecovery,MGM.advanceTrnId,MGM.instNo,MGM.totalInst FROM MstGpfMonthly MGM,MstEmpGpfAcc MGA,MstEmp ME WHERE MGM.gpfAccNo=MGA.gpfAccNo AND MGA.mstGpfEmpId=ME.dcpsEmpId AND MGM.billgroupId=:billgroupId AND MGM.monthId=:monthId AND MGM.finYearId=:finYearId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("billgroupId", lLngBillgroupId);
			lQuery.setParameter("monthId", lLngMonthId);
			lQuery.setParameter("finYearId", lLongYearId);

			lLstScheduleData = lQuery.list();
		} catch (Exception e) {
			logger.error("Exception in getMonthlyEmpListForPayroll of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLstScheduleData;
	}

	public MstGpfMonthly getMstGpfMonthlyObject(String lStrGpfAccNo, Long lLngMonthId, Long lLongYearId) {
		List lLstMonthlyData = new ArrayList();
		MstGpfMonthly lObjMstGpfMonthly = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery
					.append("FROM MstGpfMonthly WHERE gpfAccNo=:gpfAccNo AND monthId=:monthId AND finYearId=:finYearId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("gpfAccNo", lStrGpfAccNo);
			lQuery.setParameter("monthId", lLngMonthId);
			lQuery.setParameter("finYearId", lLongYearId);

			lLstMonthlyData = lQuery.list();
			if (lLstMonthlyData != null && lLstMonthlyData.size() > 0) {
				lObjMstGpfMonthly = (MstGpfMonthly) lLstMonthlyData.get(0);
			}
		} catch (Exception e) {
			logger.error("Exception in getPendingSchedule of ScheduleGenerationDAOImpl  : ", e);
		}
		return lObjMstGpfMonthly;
	}
	
	public Long getFinyearFromYearCode(String lStrFinYearCode)
	{
		Long lLngFinYear = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT finYearId FROM SgvcFinYearMst ");
			lSBQuery.append("WHERE ");
			lSBQuery.append("finYearCode = :finYearCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("finYearCode", lStrFinYearCode);
			
			lLngFinYear = (Long) lQuery.list().get(0);
		}catch (Exception e) {
			logger.error("Exception in getPendingSchedule of ScheduleGenerationDAOImpl  : ", e);
		}
		return lLngFinYear;
	}
}
