package com.tcs.sgv.lna.report;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

public class LNALedgerQueryDAOImpl extends GenericDaoHibernateImpl {

	Session ghibSession = null;

	public LNALedgerQueryDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getEmpDtlsForCompAdvance(String lStrSevaartId,String lStrHodLocCode) {

		List lLstEmpDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select ME.sevarthId,ME.name,ODM.dsgnName,DO.dcpsDdoOfficeName,CLM.lookupName,CA.amountSanctioned,CA.orderNo,CA.orderDate,");
		lSBQuery.append("CA.installmentAmount,CA.sancInstallments,CA.oddInstallment,CA.oddInstallmentNumber,LM.locName,CA.transactionId");
		lSBQuery.append(" FROM MstEmp ME,MstLnaCompAdvance CA,OrgDesignationMst ODM,CmnLookupMst CLM,DdoOffice DO,");
		lSBQuery.append("RltDdoOrg RDO,CmnLocationMst LM,OrgDdoMst DDO ");
		lSBQuery.append(" WHERE CA.statusFlag = 'A' AND ME.sevarthId = CA.sevaarthId AND ");
		lSBQuery.append(" CA.sevaarthId = :sevaarthId AND ME.currOff = DO.dcpsDdoOfficeIdPk");
		lSBQuery.append(" AND ME.designation = ODM.dsgnId AND CLM.lookupId = CA.advanceSubType");
		lSBQuery.append(" AND RDO.ddoCode = ME.ddoCode AND LM.locId = RDO.locationCode");
		lSBQuery.append(" AND DDO.hodLocCode = :HodLocCode AND DDO.ddoCode =  ME.ddoCode ");
		
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaartId);
		lQuery.setParameter("HodLocCode", lStrHodLocCode);
		lLstEmpDtls = lQuery.list();
		return lLstEmpDtls;

	}

	public List getEmpDtlsForHouseAdvance(String lStrSevaartId,String lStrHodLocCode) {

		List lLstEmpDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select ME.sevarthId,ME.name,ODM.dsgnName,DO.dcpsDdoOfficeName,CLM.lookupName,HBA.amountSanctioned,HBA.interestAmount,HBA.orderNo,HBA.orderDate,");
		lSBQuery.append("HBA.principalInstAmtMonth,HBA.interestInstAmtMonth,HBA.sancPrinInst,HBA.sancInterestInst,HBA.oddInstallment,");
		lSBQuery.append("HBA.oddInstallmentNumber,HBA.oddInterestInstallment,HBA.oddInterestInstallmentNo,LM.locName,HBA.transactionId");
		lSBQuery.append(" FROM MstEmp ME,MstLnaHouseAdvance HBA,OrgDesignationMst ODM,CmnLookupMst CLM,DdoOffice DO,");
		lSBQuery.append("RltDdoOrg RDO,CmnLocationMst LM,OrgDdoMst DDO ");
		lSBQuery.append(" WHERE HBA.statusFlag = 'A' AND ME.sevarthId = HBA.sevaarthId AND ");
		lSBQuery.append(" HBA.sevaarthId = :sevaarthId AND ME.currOff = DO.dcpsDdoOfficeIdPk");
		lSBQuery.append(" AND ME.designation = ODM.dsgnId AND CLM.lookupId = HBA.advanceSubType");
		lSBQuery.append(" AND RDO.ddoCode = ME.ddoCode AND LM.locId = RDO.locationCode");
		lSBQuery.append(" AND DDO.hodLocCode = :HodLocCode AND DDO.ddoCode =  ME.ddoCode ");
		
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaartId);
		lQuery.setParameter("HodLocCode", lStrHodLocCode);
		lLstEmpDtls = lQuery.list();
		return lLstEmpDtls;

	}

	public List getEmpDtlsForMotorAdvance(String lStrSevaartId,String lStrHodLocCode) {

		List lLstEmpDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select ME.sevarthId,ME.name,ODM.dsgnName,DO.dcpsDdoOfficeName,CLM.lookupName,MCA.amountSanctioned,MCA.interestAmount,MCA.orderNo,MCA.orderDate,");
		lSBQuery.append("MCA.cappitalInstAmtMonth,MCA.interestInstAmtMonth,MCA.sancCapitalInst,MCA.sancInterestInst,MCA.oddInstallment,");
		lSBQuery.append("MCA.oddInstallmentNumber,MCA.oddInterestInstallment,MCA.oddInterestInstallmentNo,LM.locName,MCA.transactionId");
		lSBQuery.append(" FROM MstEmp ME,MstLnaMotorAdvance MCA,OrgDesignationMst ODM,CmnLookupMst CLM,DdoOffice DO,");
		lSBQuery.append("RltDdoOrg RDO,CmnLocationMst LM,OrgDdoMst DDO ");
		lSBQuery.append(" WHERE MCA.statusFlag = 'A' AND ME.sevarthId = MCA.sevaarthId AND ");
		lSBQuery.append(" MCA.sevaarthId = :sevaarthId AND ME.currOff = DO.dcpsDdoOfficeIdPk");
		lSBQuery.append(" AND ME.designation = ODM.dsgnId AND CLM.lookupId = MCA.advanceSubType");
		lSBQuery.append(" AND RDO.ddoCode = ME.ddoCode AND LM.locId = RDO.locationCode");
		lSBQuery.append(" AND DDO.hodLocCode = :HodLocCode AND DDO.ddoCode =  ME.ddoCode ");
		
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaartId);
		lQuery.setParameter("HodLocCode", lStrHodLocCode);
		lLstEmpDtls = lQuery.list();
		return lLstEmpDtls;

	}

	public List getEmpVoucherDtls(String lStrSevaartId, Long lLngAdvanceType, Long lLngFinYearId) {

		List lLstEmpDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT ln.RECOVERED_INST");
		
		if(lLngAdvanceType == 57L){
			lSBQuery.append(", pay.OTHER_VEH_ADV");
		}else if(lLngAdvanceType == 58L){
			lSBQuery.append(", pay.COMPUTER_ADV");
		}else if(lLngAdvanceType == 67L){
			lSBQuery.append(", pay.HBA");
		}
		lSBQuery.append(",loc.LOC_NAME, h.VOUCHER_NO, h.VOUCHER_DATE,SMM.MONTH_NAME FROM PAYBILL_HEAD_MPG h,");
		lSBQuery.append("mst_dcps_emp d, HR_PAY_PAYBILL pay, HR_EIS_EMP_MST eis, HR_PAY_PAYBILL_LOAN_DTLS ln, HR_LOAN_EMP_DTLS lemp ");
		lSBQuery.append(",CMN_LOCATION_MST loc,SGVA_MONTH_MST SMM where d.SEVARTH_ID = :sevaarthId and SUBSTR(d.DDO_CODE,1,4) = loc.LOC_ID ");
		lSBQuery.append("and d.ORG_EMP_MST_ID = eis.EMP_MPG_ID and eis.EMP_ID = pay.EMP_ID ");
		lSBQuery.append("and h.BILL_NO = d.BILLGROUP_ID and h.APPROVE_FLAG= 1 ");
		lSBQuery.append("AND (h.PAYBILL_YEAR = :finYearId AND h.PAYBILL_MONTH>3 OR h.PAYBILL_YEAR = :nextFinYearId AND h.PAYBILL_MONTH<=3) ");
		lSBQuery.append("and lemp.EMP_ID = pay.EMP_ID and ln.LOAN_TYPE_ID = :advanceType  and lemp.EMP_LOAN_ID= ln.EMP_LOAN_ID ");
		lSBQuery.append("and lemp.LOAN_ACTIVATE_FLAG =1 and pay.id= ln.PAYBILL_ID and pay.PAYBILL_GRP_ID = h.PAYBILL_ID ");
		lSBQuery.append("and smm.MONTH_ID = h.PAYBILL_MONTH order by h.PAYBILL_YEAR, h.PAYBILL_MONTH");
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaartId);
		lQuery.setParameter("advanceType", lLngAdvanceType);
		lQuery.setParameter("finYearId", lLngFinYearId);
		lQuery.setParameter("nextFinYearId", lLngFinYearId + 1);
		
		lLstEmpDtls = lQuery.list();
		return lLstEmpDtls;
	}

	public List getEmpChallanDtls(String lStrSevaartId, String lStrTransactionId, Long lLngFinYearId) {

		List lLstEmpDtls = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("select MLC.instFrom,MLC.instTo,MLC.installmentAmount,MLC.prinOrInterestAmount,");
		lSBQuery.append("MLC.openingBalance,MLC.challanNo,MLC.challanDate ");
		lSBQuery.append("FROM MstLnaChallanDtls MLC ");
		lSBQuery.append("WHERE MLC.sevaarthId = :sevaarthId AND ");
		lSBQuery.append("MLC.transactionId = :transactionId AND MLC.finYearId = :finYearId AND MLC.statusFlag = 'A' AND MLC.monthId = 1");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaartId);
		lQuery.setParameter("transactionId", lStrTransactionId);
		lQuery.setParameter("finYearId", lLngFinYearId);
		lLstEmpDtls = lQuery.list();
		return lLstEmpDtls;
	}

	
	
	public Long getEmpLoanId(String lStrSevaarthId, Long lLngAdvanceType){
		
		List<Long> lLstEmpLoanId = null;
		Long lLngEmpLoanId = null;
		StringBuilder lSBQuery = new StringBuilder();
		lSBQuery.append("SELECT lem.EMP_LOAN_ID FROM HR_LOAN_EMP_DTLS lem,mst_dcps_emp mde,hr_eis_emp_mst eem ");
		lSBQuery.append("where mde.SEVARTH_ID = :sevaarthId and mde.ORG_EMP_MST_ID = eem.EMP_ID and eem.EMP_ID = lem.EMP_ID ");
		lSBQuery.append("and lem.LOAN_TYPE_ID = :advanceType");
		
		Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
		lQuery.setParameter("sevaarthId", lStrSevaarthId);		
		lQuery.setParameter("advanceType", lLngAdvanceType);
		
		lLstEmpLoanId = lQuery.list();
		
		if(!lLstEmpLoanId.isEmpty())
			lLngEmpLoanId = lLstEmpLoanId.get(0);
		
		return lLngEmpLoanId;
	}
}
