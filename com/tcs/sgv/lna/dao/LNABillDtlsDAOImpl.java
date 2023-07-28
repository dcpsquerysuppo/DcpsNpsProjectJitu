package com.tcs.sgv.lna.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.lna.valueobject.MstLnaBillDtls;
import com.tcs.sgv.lna.valueobject.MstLnaPayrollLoanTypeMpg;

public class LNABillDtlsDAOImpl extends GenericDaoHibernateImpl implements LNABillDtlsDAO{
	
	Session ghibSession = null;
	
	public LNABillDtlsDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}
	
	public void updateBillNoForCompAdv(List<String> lLstSevaarthId, List<String> lLstTrnsId,Long lLngBillId) {
		StringBuilder lSb = new StringBuilder();
		
		lSb.append("update MstLnaCompAdvance set lnaBillId = :lLngBillId where sevaarthId IN (:lLstSevaarthId) and transactionId in (:lLstTrnsId)");
		
		Query lQuery = ghibSession.createQuery(lSb.toString());
		lQuery.setParameter("lLngBillId", lLngBillId);
		lQuery.setParameterList("lLstSevaarthId", lLstSevaarthId);
		lQuery.setParameterList("lLstTrnsId", lLstTrnsId);
		lQuery.executeUpdate();
		
	}

	
	public void updateBillNoForMotorAdv(List<String> lLstSevaarthId, List<String> lLstTrnsId,Long lLngBillId) {
		StringBuilder lSb = new StringBuilder();
		
		lSb.append("update MstLnaMotorAdvance set lnaBillId = :lLngBillId where sevaarthId IN (:lLstSevaarthId) and transactionId in (:lLstTrnsId)");
		
		Query lQuery = ghibSession.createQuery(lSb.toString());
		lQuery.setParameter("lLngBillId", lLngBillId);
		lQuery.setParameterList("lLstSevaarthId", lLstSevaarthId);
		lQuery.setParameterList("lLstTrnsId", lLstTrnsId);
		lQuery.executeUpdate();
	}
	
	
	public void updateBillNoForHouseAdv(List<String> lLstSevaarthId, List<String> lLstTrnsId,Long lLngBillId) {
		StringBuilder lSb = new StringBuilder();
		
		lSb.append("update MstLnaHouseAdvance set lnaBillId = :lLngBillId where sevaarthId IN (:lLstSevaarthId) and transactionId in (:lLstTrnsId)");
		
		Query lQuery = ghibSession.createQuery(lSb.toString());
		lQuery.setParameter("lLngBillId", lLngBillId);
		lQuery.setParameterList("lLstSevaarthId", lLstSevaarthId);
		lQuery.setParameterList("lLstTrnsId", lLstTrnsId);
		lQuery.executeUpdate();
	}

	
	public List<MstLnaBillDtls> getLoanBillDtls(String lStrLocation) {
	
		List<MstLnaBillDtls> lLstBillDtls = null;
		StringBuilder lSb = new StringBuilder();
		
		lSb.append("SELECT BILL FROM MstLnaBillDtls BILL where BILL.locationCode = :lStrLocation and BILL.statusFlag != 0");		
		Query lQuery = ghibSession.createQuery(lSb.toString());
		lQuery.setParameter("lStrLocation", lStrLocation);		
		lLstBillDtls = lQuery.list();
		
		return lLstBillDtls;
	}

	
	public List<MstLnaPayrollLoanTypeMpg> getPayrollLoanId(Long lLngLoanId) {
		List<MstLnaPayrollLoanTypeMpg> lLstPayrollLoanId = null;
		StringBuilder lSb = new StringBuilder();
		
		lSb.append("FROM MstLnaPayrollLoanTypeMpg where lnaLoanId = :lnaLoanId ");
		
		Query lQuery = ghibSession.createQuery(lSb.toString());
		lQuery.setParameter("lnaLoanId", lLngLoanId);		
		lLstPayrollLoanId = lQuery.list();
		
		return lLstPayrollLoanId;
	}

	
	public List<Object> getEmpCompLoanDtls(Long lLngLoanBillId) {
	
		List<Object> lLstEmpCompLoanDtls = null;
		StringBuilder lSb = new StringBuilder();
		
		lSb.append("SELECT mde.ORG_EMP_MST_ID,comp.AMOUNT_SANCTIONED,comp.SANCTIONED_INSTALLMENTS,comp.SANCTIONED_DATE,ddo.LOCATION_CODE,comp.INSTALLMENT_AMOUNT, ");
		lSb.append("comp.ORDER_NO,comp.ODD_INSTALLMENT_NO,comp.ODD_INSTALLMENT,bill.VOUCHER_NO,bill.VOUCHER_DATE,comp.ORDER_DATE ");
		lSb.append("FROM MST_LNA_COMP_ADVANCE comp,mst_dcps_emp mde,hr_eis_emp_mst hem,ORG_DDO_MST ddo,MST_LNA_BILL_DTLS bill ");
		lSb.append("where comp.LOAN_BILL_ID = :lLngLoanBillId and comp.SEVAARTH_ID = mde.SEVARTH_ID and mde.ORG_EMP_MST_ID = hem.emp_mpg_id ");
		lSb.append("and mde.DDO_CODE = ddo.DDO_CODE and bill.BILL_DTLS_ID = :lLngLoanBillId ");
				
		Query lQuery = ghibSession.createSQLQuery(lSb.toString());
		lQuery.setParameter("lLngLoanBillId", lLngLoanBillId);		
		lLstEmpCompLoanDtls = lQuery.list();
		
		return lLstEmpCompLoanDtls;		
	}

	
	public void rejectBillForAdv(String lStrTableName, Long lLngBillId) {
		
		StringBuilder lSb = new StringBuilder();
		
		lSb.append("update ");
		lSb.append(	lStrTableName);
		lSb.append(" set lnaBillId = null where lnaBillId = :lLngBillId");
		
		Query lQuery = ghibSession.createQuery(lSb.toString());
		lQuery.setParameter("lLngBillId", lLngBillId);
		lQuery.executeUpdate();
		
	}


	public Boolean isLoanBillPending(Long lLngLocationCode) {

		return null;
	}

	
	public List<Object> getEmpHouseLoanDtls(Long lLngLoanBillId) {
	
		List<Object> lLstEmpHouseLoanDtls = null;
		StringBuilder lSb = new StringBuilder();
		
		lSb.append("SELECT mde.ORG_EMP_MST_ID,hba.AMOUNT_SANCTIONED,hba.INTEREST_AMOUNT,hba.SANC_PRINCIPAL_INSTALLMENTS,hba.SANC_INTEREST_INSTALLMENTS, ");
		lSb.append("hba.SANCTIONED_DATE,ddo.LOCATION_CODE,hba.PRINCIPAL_INST_AMT_MONTH, hba.INTEREST_INST_AMT_MONTH,hba.ORDER_NO, ");
		lSb.append("hba.ODD_INSTALLMENT_NO,hba.ODD_INSTALLMENT,hba.ODD_INSTALLMENT_NO,hba.ODD_INSTALLMENT,bill.VOUCHER_NO,bill.VOUCHER_DATE,hba.ORDER_DATE,hba.ADVANCE_SUB_TYPE,hba.DISBURSEMENT_ONE,hba.DISBURSEMENT_two ");
		lSb.append("FROM MST_LNA_HOUSE_ADVANCE hba,mst_dcps_emp mde,hr_eis_emp_mst hem,ORG_DDO_MST ddo,MST_LNA_BILL_DTLS bill ");
		lSb.append("where hba.LOAN_BILL_ID = :lLngLoanBillId and hba.SEVAARTH_ID = mde.SEVARTH_ID and mde.ORG_EMP_MST_ID = hem.emp_mpg_id ");
		lSb.append("and mde.DDO_CODE = ddo.DDO_CODE and bill.BILL_DTLS_ID = :lLngLoanBillId");
		Query lQuery = ghibSession.createSQLQuery(lSb.toString());
		lQuery.setParameter("lLngLoanBillId", lLngLoanBillId);		
		lLstEmpHouseLoanDtls = lQuery.list();
		
		return lLstEmpHouseLoanDtls;	
	}

	
	public List<Object> getEmpMotorLoanDtls(Long lLngLoanBillId) {
	
		List<Object> lLstEmpMotorLoanDtls = null;
		StringBuilder lSb = new StringBuilder();
		
		lSb.append("SELECT mde.ORG_EMP_MST_ID,mca.AMOUNT_SANCTIONED,mca.INTEREST_AMOUNT,mca.SANC_CAPITAL_INSTALLMENTS,mca.SANC_INTEREST_INSTALLMENTS, ");
		lSb.append("mca.SANCTIONED_DATE,ddo.LOCATION_CODE,mca.CAPITAL_INST_AMT_MONTH, mca.INTEREST_INST_AMT_MONTH,mca.ORDER_NO, ");
		lSb.append("mca.ODD_INSTALLMENT_NO,mca.ODD_INSTALLMENT,mca.ODD_INSTALLMENT_NO,mca.ODD_INSTALLMENT,bill.VOUCHER_NO,bill.VOUCHER_DATE,mca.ORDER_DATE ");
		lSb.append("FROM MST_LNA_MOTOR_ADVANCE mca,mst_dcps_emp mde,hr_eis_emp_mst hem,ORG_DDO_MST ddo,MST_LNA_BILL_DTLS bill ");
		lSb.append("where mca.LOAN_BILL_ID = :lLngLoanBillId and mca.SEVAARTH_ID = mde.SEVARTH_ID and mde.ORG_EMP_MST_ID = hem.emp_mpg_id ");
		lSb.append("and mde.DDO_CODE = ddo.DDO_CODE and bill.BILL_DTLS_ID = :lLngLoanBillId");
		Query lQuery = ghibSession.createSQLQuery(lSb.toString());
		lQuery.setParameter("lLngLoanBillId", lLngLoanBillId);		
		lLstEmpMotorLoanDtls = lQuery.list();
		
		return lLstEmpMotorLoanDtls;		
	}

	public List<MstLnaBillDtls> getLoanOrderDtls(String lStrLocation) {
		
		List<MstLnaBillDtls> lLstBillDtls = null;
		StringBuilder lSb = new StringBuilder();
		
		lSb.append(" FROM MstLnaBillDtls BILL where BILL.locationCode = :lStrLocation and BILL.statusFlag != 0");		
		Query lQuery = ghibSession.createQuery(lSb.toString());
		lQuery.setParameter("lStrLocation", lStrLocation);		
		lLstBillDtls = lQuery.list();
		
		return lLstBillDtls;
	}
}
