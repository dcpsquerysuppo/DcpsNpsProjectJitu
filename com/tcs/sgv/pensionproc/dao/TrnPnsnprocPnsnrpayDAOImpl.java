package com.tcs.sgv.pensionproc.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionproc.valueobject.TrnPnsnprocPnsnrpay;

public class TrnPnsnprocPnsnrpayDAOImpl extends GenericDaoHibernateImpl	implements TrnPnsnprocPnsnrpayDAO {

	private final static Logger gLogger = Logger
			.getLogger(TrnPnsnprocPnsnrpayDAOImpl.class);

	private Session ghibSession = null;

	public TrnPnsnprocPnsnrpayDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
		// TODO Auto-generated constructor stub
	}

	
	public Long getPayScaleDtls(Long lLngEmpId) {		
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> lLstPayScaleId = null;
		Long lLngPayScaleId = null;
		try {
			lSBQuery.append(" select ESM.hrEisScaleMst.scaleId from HrEisSgdMpg ESM,HrEisOtherDtls EOD,HrEisEmpMst HEM where ");
			lSBQuery.append(" HEM.orgEmpMst.empId = :EmpId AND HEM.empId = EOD.hrEisEmpMst.empId AND ESM.sgdMapId = EOD.hrEisSgdMpg.sgdMapId ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("EmpId", lLngEmpId);
			lLstPayScaleId = lHibQry.list();		
			
			if(!lLstPayScaleId.isEmpty())
				lLngPayScaleId = lLstPayScaleId.get(0);
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnprocPnsnrpayDAOImpl : getPayScaleDtls() : Error is :" + e, e);

		}
		return lLngPayScaleId;		
	}


	
	public Long getBasicPay(Long lLngEmpId) {
		StringBuilder lSBQuery = new StringBuilder();
		List<Long> lLstBasicPay = null;
		Long lLngBasicPay = null;
		try {
			lSBQuery.append(" select EOD.otherCurrentBasic from HrEisEmpMst HEM,HrEisOtherDtls EOD where ");
			lSBQuery.append(" HEM.orgEmpMst.empId = :EmpId AND HEM.empId = EOD.hrEisEmpMst.empId ");
			Query lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("EmpId", lLngEmpId);
			lLstBasicPay = lHibQry.list();		
			
			if(!lLstBasicPay.isEmpty())
				lLngBasicPay = lLstBasicPay.get(0);
			
		} catch (Exception e) {
			gLogger.error("TrnPnsnprocPnsnrpayDAOImpl : getPayScaleDtls() : Error is :" + e, e);

		}
		return lLngBasicPay;	
	}


	
	public List getAvgPayDtls(Long lLngEmpId,String lStrFormYearMonth,String lStrToYearMonth,String lStrPayComm) {
	
		StringBuilder lSBQuery = new StringBuilder();
		List lLstAvgPayDtls = null;
	
		try {
			lSBQuery.append(" SELECT  CONCAT(head.PAYBILL_YEAR,lpad(head.PAYBILL_MONTH,2,0)),paybill.po,");
			if(lStrPayComm.equals("700015")){
				lSBQuery.append("paybill.D_PAY,paybill.NON_PRAC_ALLOW");
			}
			else if(lStrPayComm.equals("700016")){
				lSBQuery.append("paybill.GPAY,paybill.NON_PRAC_ALLOW");
			}
			lSBQuery.append(" FROM hr_pay_paybill paybill,PAYBILL_HEAD_MPG head where ");
			lSBQuery.append(" head.APPROVE_FLAG in (0,1)  and paybill.EMP_ID = :EmpId and head.PAYBILL_ID =paybill.PAYBILL_GRP_ID");
			lSBQuery.append(" and CONCAT(head.PAYBILL_YEAR,lpad(head.PAYBILL_MONTH,2,0)) >= :lStrFormYearMonth ");
			lSBQuery.append(" and CONCAT(head.PAYBILL_YEAR,lpad(head.PAYBILL_MONTH,2,0)) <= :lStrToYearMonth");
			Query lHibQry = ghibSession.createSQLQuery(lSBQuery.toString());
			lHibQry.setParameter("EmpId", lLngEmpId);
			lHibQry.setParameter("lStrFormYearMonth", lStrFormYearMonth);
			lHibQry.setParameter("lStrToYearMonth", lStrToYearMonth);
			lLstAvgPayDtls = lHibQry.list();					
		} catch (Exception e) {
			gLogger.error("TrnPnsnprocPnsnrpayDAOImpl : getAvgPayDtls() : Error is :" + e, e);

		}
		return lLstAvgPayDtls;
	}
}
