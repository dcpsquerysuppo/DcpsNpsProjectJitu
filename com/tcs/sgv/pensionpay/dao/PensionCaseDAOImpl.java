package com.tcs.sgv.pensionpay.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.RltPensioncaseBill;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;


public class PensionCaseDAOImpl extends GenericDaoHibernateImpl<MstPensionerHdr, Long> implements PensionCaseDAO {

	/* Global Variable for Logger Class */
	private Log logger = LogFactory.getLog(getClass());

	public PensionCaseDAOImpl(Class<MstPensionerHdr> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public List getBillCaseDtls(String ppoNo, String lStrBillType, String lLocCode) throws Exception {

		Session hiSession = getSession();
		StringBuffer query = new StringBuffer();
		List<RltPensioncaseBill> listBillCaseDtls = null;
		try {
			query.append(" SELECT P FROM RltPensioncaseBill P WHERE P.ppoNo=:ppoNo AND P.status=:status AND P.locationCode= :locCode ");
			if (lStrBillType != null) {
				query.append(" AND P.billType=:billType ");
			}

			Query sqlQuery = hiSession.createQuery(query.toString());
			sqlQuery.setString("ppoNo", ppoNo);
			sqlQuery.setString("status", "Y");
			sqlQuery.setString("locCode", lLocCode);
			if (lStrBillType != null) {
				sqlQuery.setString("billType", lStrBillType);
			}

			listBillCaseDtls = sqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return listBillCaseDtls;
	}

	public List getDCRGPaidFlag(String ppoNo, String lStrBillType, String lLocCode) throws Exception {

		Session hiSession = getSession();
		StringBuffer query = new StringBuffer();
		List<TrnPensionRqstHdr> lLstFlagDtls = null;
		try {
			query.append(" SELECT TRH.dcrgPaidFlag " + "FROM TrnPensionRqstHdr TRH,RltPensioncaseBill PCB " + "WHERE TRH.ppoNo=PCB.ppoNo " + "AND TRH.ppoNo=:ppoNo "
					+ "AND TRH.locationCode= :locCode ");
			if (lStrBillType != null) {
				query.append(" AND PCB.billType=:billType ");
			}

			Query sqlQuery = hiSession.createQuery(query.toString());
			sqlQuery.setString("ppoNo", ppoNo);
			sqlQuery.setString("locCode", lLocCode);
			if (lStrBillType != null) {
				sqlQuery.setString("billType", lStrBillType);
			}

			lLstFlagDtls = sqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstFlagDtls;
	}

	public List getCVPPaidFlag(String ppoNo, String lStrBillType, String lLocCode) throws Exception {

		Session hiSession = getSession();
		StringBuffer query = new StringBuffer();
		List<TrnPensionRqstHdr> lLstFlagDtls = null;
		try {
			query
					.append(" SELECT TRH.cvpPaidFlag " + "FROM TrnPensionRqstHdr TRH,RltPensioncaseBill PCB " + "WHERE TRH.ppoNo=PCB.ppoNo " + "AND TRH.ppoNo=:ppoNo "
							+ "AND TRH.locationCode= :locCode ");
			if (lStrBillType != null) {
				query.append(" AND PCB.billType=:billType ");
			}

			Query sqlQuery = hiSession.createQuery(query.toString());
			sqlQuery.setString("ppoNo", ppoNo);
			sqlQuery.setString("locCode", lLocCode);
			if (lStrBillType != null) {
				sqlQuery.setString("billType", lStrBillType);
			}

			lLstFlagDtls = sqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstFlagDtls;
	}
	
	public List<TrnBillRegister> getPensionBillDtls(List<Long> lLstBillNo, List<Short> lLstBillStatus) throws Exception {

		Session hiSession = getSession();
		StringBuffer query = new StringBuffer();
		List<TrnBillRegister> lLstBillDtls = null;
		try {
			query.append(" SELECT P FROM TrnBillRegister P WHERE P.billNo in (:billNoList) AND P.currBillStatus in (:billStatusList) ");
			
			Query sqlQuery = hiSession.createQuery(query.toString());
			sqlQuery.setParameterList("billNoList", lLstBillNo);
			sqlQuery.setParameterList("billStatusList", lLstBillStatus);

			lLstBillDtls = sqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstBillDtls;
	}
}