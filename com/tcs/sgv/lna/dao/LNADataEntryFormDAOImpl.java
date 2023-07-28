package com.tcs.sgv.lna.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class LNADataEntryFormDAOImpl extends GenericDaoHibernateImpl implements LNADataEntryFormDAO {
	Session ghibSession = null;

	public LNADataEntryFormDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List<ComboValuesVO> getFinYear() {
		List<ComboValuesVO> lLstFinYear = new ArrayList<ComboValuesVO>();
		List<Object> lLstTemp = null;
		Object[] obj;
		ComboValuesVO cmbVO = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select finYearId,finYearCode");
		lSBQuery.append(" FROM SgvcFinYearMst");
		lSBQuery.append(" WHERE finYearCode between '2012' and '2016'");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lLstTemp = lQuery.list();

		if (!lLstTemp.isEmpty()) {
			for (Integer lInt = 0; lInt < lLstTemp.size(); lInt++) {
				cmbVO = new ComboValuesVO();
				obj = (Object[]) lLstTemp.get(lInt);
				cmbVO.setId(obj[0].toString());
				cmbVO.setDesc(obj[1].toString());
				lLstFinYear.add(cmbVO);
			}
		}
		return lLstFinYear;
	}

	public List getEmpDtls(String lStrEmpCode, String lStrHodLocCode) {
		List lLstEmpDtls = null;
		List finEmpDtls=null;
/*SELECT ME.EMP_NAME,ODM.DSGN_NAME,DO.OFF_NAME FROM MST_DCPS_EMP ME,ORG_DESIGNATION_MST ODM, MST_DCPS_DDO_OFFICE DO,ORG_DDO_MST DM
		WHERE ME.SEVARTH_ID = 'MEVLGKM6901' AND ME.DESIGNATION = ODM.DSGN_ID AND ME.CURR_OFF = DO.DCPS_DDO_OFFICE_MST_ID
		 AND ME.DDO_CODE = DM.DDO_CODE AND DM.LOCATION_CODE = '2000059'*/
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ME.EMP_NAME,ODM.DSGN_NAME,DO.OFF_NAME ");
			lSBQuery.append(" FROM MST_DCPS_EMP ME,ORG_DESIGNATION_MST ODM, MST_DCPS_DDO_OFFICE DO,ORG_DDO_MST DM ");
			lSBQuery.append(" WHERE ME.SEVARTH_ID=:sevarthId AND ME.DESIGNATION = ODM.DSGN_ID AND ME.CURR_OFF = DO.DCPS_DDO_OFFICE_MST_ID ");
			lSBQuery.append(" AND ME.DDO_CODE = DM.DDO_CODE AND DM.LOCATION_CODE=:lStrHodLocCode");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			System.out.println("query:+++++" +lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrEmpCode);
			lQuery.setParameter("lStrHodLocCode", lStrHodLocCode);
			lLstEmpDtls = lQuery.list();
			
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstEmpDtls;
	}

	public List getNewEmpDtls(String lStrEmpCode) {
		List lLstEmpDtls = null;
		List finEmpDtls=null;
/*SELECT ME.EMP_NAME,ODM.DSGN_NAME,DO.OFF_NAME FROM MST_DCPS_EMP ME,ORG_DESIGNATION_MST ODM, MST_DCPS_DDO_OFFICE DO,ORG_DDO_MST DM
		WHERE ME.SEVARTH_ID = 'MEVLGKM6901' AND ME.DESIGNATION = ODM.DSGN_ID AND ME.CURR_OFF = DO.DCPS_DDO_OFFICE_MST_ID
		 AND ME.DDO_CODE = DM.DDO_CODE AND DM.LOCATION_CODE = '2000059'*/
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ME.EMP_NAME,ODM.DSGN_NAME,DO.OFF_NAME ");
			lSBQuery.append(" FROM MST_DCPS_EMP ME,ORG_DESIGNATION_MST ODM, MST_DCPS_DDO_OFFICE DO,ORG_DDO_MST DM ");
			lSBQuery.append(" WHERE ME.SEVARTH_ID=:sevarthId AND ME.DESIGNATION = ODM.DSGN_ID AND ME.CURR_OFF = DO.DCPS_DDO_OFFICE_MST_ID ");
			lSBQuery.append(" AND ME.DDO_CODE = DM.DDO_CODE");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			System.out.println("query:+++++" +lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrEmpCode);
		//	lQuery.setParameter("lStrHodLocCode", lStrHodLocCode);
			lLstEmpDtls = lQuery.list();
			
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstEmpDtls;
	}
	public Long getBillGroupId(String lStrSevaarthId) {
		Long lLngBillGroupId = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT billGroupId ");
			lSBQuery.append("FROM MstEmp ");
			lSBQuery.append("WHERE sevarthId = :sevarthId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevarthId", lStrSevaarthId);
			lLngBillGroupId = (Long) lQuery.list().get(0);
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLngBillGroupId;
	}

	public List getDraftReq(String lStrUserType, String lStrHodLocCode) {
		List lLstDraftReq = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT ELD FROM TrnEmpLoanDtls ELD,MstEmp ME,OrgDdoMst DM ");
			if ("HODASST".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("WHERE ELD.status = 'D'");
			} else if ("HOD".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("WHERE ELD.status = 'F'");
			}
			lSBQuery.append(" AND ME.sevarthId = ELD.sevaarthId AND ME.ddoCode = DM.ddoCode AND DM.locationCode = :HodLocCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("HodLocCode", lStrHodLocCode);
			lLstDraftReq = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstDraftReq;
	}

	public List getCompAdvance(String lStrSevaarthId, String lStrUserType) {
		List lLstDraftReq = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT amountSanctioned,sanctionedDate,advanceSubType,interestRate,orderNo,orderDate,sancInstallments,installmentLeft,transactionId");
			lSBQuery.append(",userRemarks,computerAdvanceId FROM MstLnaCompAdvance ");
			lSBQuery.append("WHERE sevaarthId = :sevaarthId ");
			if ("HODASST".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and statusFlag = 'DD'");
			} else if ("HOD".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and statusFlag = 'F'");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lLstDraftReq = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstDraftReq;
	}

	public List getHouseAdvance(String lStrSevaarthId, String lStrUserType) {
		List lLstDraftReq = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT amountSanctioned,sanctionedDate,advanceSubType,interestRate,orderNo,orderDate,interestAmount,sancPrinInst,sancInterestInst");
			lSBQuery.append(",principalInstLeft,InterestInstLeft,transactionId,userRemarks,houseAdvanceId,releaseDateOne,releaseDateTwo,releaseDateThree,releaseDateFour ");
			lSBQuery.append("FROM MstLnaHouseAdvance ");
			lSBQuery.append("WHERE sevaarthId = :sevaarthId ");
			if ("HODASST".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and statusFlag = 'DD'");
			} else if ("HOD".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and statusFlag = 'F'");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lLstDraftReq = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstDraftReq;
	}

	public List getMotorAdvance(String lStrSevaarthId, String lStrUserType) {

		List lLstDraftReq = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT amountSanctioned,sanctionedDate,advanceSubType,interestRate,orderNo,orderDate,interestAmount,sancCapitalInst,");
			lSBQuery.append("sancInterestInst,capitalInstLeft,InterestInstLeft,transactionId");
			lSBQuery.append(",userRemarks,motorAdvanceId FROM MstLnaMotorAdvance ");
			lSBQuery.append("WHERE sevaarthId = :sevaarthId ");
			if ("HODASST".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and statusFlag = 'DD'");
			} else if ("HOD".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and statusFlag = 'F'");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lLstDraftReq = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstDraftReq;
	}

	public List getVoucherDtls(String lStrSevaarthId, Long lLngAdvance, String lStrUserType) {

		List lLstDraftReq = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT 'V',instNo,monthId,finYearId,installmentAmount,openingBalance,voucherNo,voucherDate,lnaMonthlyId,prinOrInterestAmount ");
			lSBQuery.append(",treasuryId,isPayrollData FROM MstLnaMonthly ");
			lSBQuery.append("WHERE sevaarthId = :sevaarthId and advanceType = :advanceType ");
			if ("HODASST".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and status = 'D'");
			} else if ("HOD".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and status = 'F'");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lQuery.setParameter("advanceType", lLngAdvance);
			lLstDraftReq = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstDraftReq;
	}

	public List getChallanDtls(String lStrSevaarthId, String lStrTransactionId, String lStrUserType) {

		List lLstDraftReq = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT 'C',instFrom,instTo,installmentAmount,challanNo,challanDate,lnaChallanDtlsId,openingBalance,prinOrInterestAmount");
			lSBQuery.append(",monthId,finYearId,treasuryId,isPayrollData FROM MstLnaChallanDtls ");
			lSBQuery.append("WHERE sevaarthId = :sevaarthId and transactionId = :transactionId ");
			if ("HODASST".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and statusFlag = 'D'");
			} else if ("HOD".equalsIgnoreCase(lStrUserType)) {
				lSBQuery.append("and statusFlag = 'F'");
			}
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lQuery.setParameter("transactionId", lStrTransactionId);
			lLstDraftReq = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstDraftReq;
	}

	public List getPrevChallanDtls(String lStrSevaarthId, String lStrTransactionId) {
		List lLstDraftReq = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM MstLnaChallanDtls ");
			lSBQuery.append("WHERE sevaarthId = :sevaarthId and statusFlag = 'D' and transactionId = :transactionId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lQuery.setParameter("transactionId", lStrTransactionId);
			lLstDraftReq = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstDraftReq;
	}

	public List getPrevVoucherDtls(String lStrSevaarthId, Long lLngAdvance) {
		List lLstDraftReq = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("FROM MstLnaMonthly ");
			lSBQuery.append("WHERE sevaarthId = :sevaarthId and status = 'D' and advanceType = :advanceType");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("sevaarthId", lStrSevaarthId);
			lQuery.setParameter("advanceType", lLngAdvance);
			lLstDraftReq = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstDraftReq;
	}

	public void updateChallanDtls(String lStrSevaarthId) {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update MstLnaChallanDtls ");
			lSBQuery.append("set statusFlag = 'A' WHERE sevaarthId = :SevaarthId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
	}

	public void updateVoucherDtls(String lStrSevaarthId) {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update MstLnaMonthly ");
			lSBQuery.append("set status = 'A' WHERE sevaarthId = :SevaarthId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
	}

	public void updateCompAdvance(String lStrCompAdvanceId, String lStrRemark) {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update MstLnaCompAdvance ");
			lSBQuery.append("set statusFlag = 'A',hoRemarks = :Remark,hodActionDate=sysdate WHERE computerAdvanceId = :CompAdvanceId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("CompAdvanceId", Long.parseLong(lStrCompAdvanceId));
			lQuery.setParameter("Remark", lStrRemark);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
	}

	public void updateHouseAdvance(String lStrHouseAdvanceId, String lStrRemark) {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update MstLnaHouseAdvance ");
			lSBQuery.append("set statusFlag = 'A',hoRemarks = :Remark,hodActionDate=sysdate WHERE houseAdvanceId = :HouseAdvanceId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("HouseAdvanceId", Long.parseLong(lStrHouseAdvanceId));
			lQuery.setParameter("Remark", lStrRemark);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
	}

	public void updateMotorAdvance(String lStrMotorAdvanceId, String lStrRemark) {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update MstLnaMotorAdvance ");
			lSBQuery.append("set statusFlag = 'A',hoRemarks = :Remark,hodActionDate=sysdate WHERE motorAdvanceId = :MotorAdvanceId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("MotorAdvanceId", Long.parseLong(lStrMotorAdvanceId));
			lQuery.setParameter("Remark", lStrRemark);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
	}

	public void updateEmpLoanDtls(String lStrEmpLoanDtlsId) {
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;

		try {
			lSBQuery.append("update TrnEmpLoanDtls ");
			lSBQuery.append("set status = 'A' WHERE empLoanDtlsId = :EmpLoanDtlsId");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("EmpLoanDtlsId", Long.parseLong(lStrEmpLoanDtlsId));
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
	}

	public String requestPendingStatus(String lStrSevaarthId) {
		List lLstPendingRequest = new ArrayList();
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select empLoanDtlsId");
		lSBQuery.append(" FROM TrnEmpLoanDtls");
		lSBQuery.append(" WHERE sevaarthId = :SevaarthId AND status = 'D')");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("SevaarthId", lStrSevaarthId);
		lLstPendingRequest = lQuery.list();
		if (lLstPendingRequest.isEmpty()) {
			return "No";
		} else {
			return "Yes";
		}
	}
	public List<ComboValuesVO> getAllTreasury(Long lLngDepartmentId, Long langId) throws Exception {
		List<ComboValuesVO> lLstTreasury = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList;
		try {
			lStrQuery.append(" Select locId,locName ");
			lStrQuery.append(" FROM CmnLocationMst ");
			lStrQuery.append(" WHERE departmentId = :lLngDepartmentId");
			lStrQuery.append(" AND cmnLanguageMst.langId =:langId");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setParameter("lLngDepartmentId", lLngDepartmentId);
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

	
	public List getEmpLoanDtlsFromPayroll(String lStrSevaarthId, Long lLngLoanType) {
		
		List lLstEmpDtls = null;
		List finEmpDtls=null;
		StringBuilder lSBQuery = new StringBuilder();
		
		if(lLngLoanType == 56L){
			lSBQuery.append("SELECT lemp.LOAN_PRIN_AMT,lemp.LOAN_DATE,'','',lemp.LOAN_SANC_ORDER_NO,lemp.LOAN_SANC_ORDER_DATE,'' ");
			lSBQuery.append(" ,lemp.LOAN_PRIN_INST_NO,'',lpre.TOTAL_RECOVERED_INST,'','' ");
		}else if(lLngLoanType == 58L){
			lSBQuery.append("SELECT lemp.LOAN_PRIN_AMT,lemp.LOAN_DATE,'','',lemp.LOAN_SANC_ORDER_NO,lemp.LOAN_SANC_ORDER_DATE ");
			lSBQuery.append(" ,lemp.LOAN_PRIN_INST_NO,lpre.TOTAL_RECOVERED_INST,'','','' ");
		}else if(lLngLoanType == 67L){
			lSBQuery.append("SELECT lemp.LOAN_PRIN_AMT,lemp.LOAN_DATE,'','',lemp.LOAN_SANC_ORDER_NO,lemp.LOAN_SANC_ORDER_DATE,'' ");
			lSBQuery.append(" ,lemp.LOAN_PRIN_INST_NO,'',lpre.TOTAL_RECOVERED_INST,'','','','','','','','','' ");
		}
		
		lSBQuery.append(" FROM mst_dcps_emp d,  HR_EIS_EMP_MST eis,  HR_LOAN_EMP_DTLS lemp,HR_LOAN_EMP_PRIN_RECOVER_DTLS lpre ");
		lSBQuery.append("where d.SEVARTH_ID = :sevaarthId and d.ORG_EMP_MST_ID = eis.EMP_MPG_ID ");
		lSBQuery.append("and lemp.EMP_ID = eis.EMP_ID and lemp.LOAN_TYPE_ID = :advanceType  and lemp.LOAN_ACTIVATE_FLAG =1 ");
		lSBQuery.append("and lemp.EMP_LOAN_ID = lpre.EMP_LOAN_ID ");
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("advanceType", lLngLoanType);
		
		
		lLstEmpDtls = lQuery.list();
		
		return lLstEmpDtls;
	}

	
	public List getEmpLoanRecoveryDtlsFromPayroll(String lStrSevaarthId, Long lLngLoanType) {
		
		List lLstEmpDtls = null;
		StringBuilder lSBQuery = new StringBuilder();		
		
		if(lLngLoanType == 56L){
			lSBQuery.append("SELECT 'V',ln.RECOVERED_INST,SMM.MONTH_ID,SFY.FIN_YEAR_ID, pay.MOTOR_CYCLE_ADV,'', h.VOUCHER_NO, h.VOUCHER_DATE,'','P',loc.LOC_ID ");
		}else if(lLngLoanType == 58L){
			lSBQuery.append("SELECT 'V',ln.RECOVERED_INST,SMM.MONTH_ID,SFY.FIN_YEAR_ID, pay.COMPUTER_ADV,'', h.VOUCHER_NO, h.VOUCHER_DATE,'','',loc.LOC_ID ");
		}else if(lLngLoanType == 67L){
			lSBQuery.append("SELECT 'V',ln.RECOVERED_INST,SMM.MONTH_ID,SFY.FIN_YEAR_ID, pay.HBA_HOUSE,'', h.VOUCHER_NO, h.VOUCHER_DATE,'','P',loc.LOC_ID ");
		}
		lSBQuery.append(" FROM PAYBILL_HEAD_MPG h,");
		lSBQuery.append("mst_dcps_emp d, HR_PAY_PAYBILL pay, HR_EIS_EMP_MST eis, HR_PAY_PAYBILL_LOAN_DTLS ln, HR_LOAN_EMP_DTLS lemp ");
		lSBQuery.append(",CMN_LOCATION_MST loc,SGVA_MONTH_MST SMM,SGVC_FIN_YEAR_MST SFY where d.SEVARTH_ID = :sevaarthId and SUBSTR(d.DDO_CODE,1,4) = loc.LOC_ID ");
		lSBQuery.append("and d.ORG_EMP_MST_ID = eis.EMP_MPG_ID and eis.EMP_ID = pay.EMP_ID ");
		lSBQuery.append("and h.BILL_NO = d.BILLGROUP_ID and h.APPROVE_FLAG= 1 ");
		lSBQuery.append("AND h.PAYBILL_YEAR = 2012 AND h.PAYBILL_MONTH>3 ");
		lSBQuery.append("and lemp.EMP_ID = pay.EMP_ID and ln.LOAN_TYPE_ID = :advanceType  and lemp.EMP_LOAN_ID= ln.EMP_LOAN_ID ");
		lSBQuery.append("and lemp.LOAN_ACTIVATE_FLAG =1 and pay.id= ln.PAYBILL_ID and pay.PAYBILL_GRP_ID = h.PAYBILL_ID ");
		lSBQuery.append("and smm.MONTH_ID = h.PAYBILL_MONTH and SFY.FIN_YEAR_CODE = h.PAYBILL_YEAR order by h.PAYBILL_YEAR, h.PAYBILL_MONTH");
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);
		lQuery.setParameter("advanceType", lLngLoanType);
		
		
		lLstEmpDtls = lQuery.list();
		return lLstEmpDtls;
	}

	

	
	
}
