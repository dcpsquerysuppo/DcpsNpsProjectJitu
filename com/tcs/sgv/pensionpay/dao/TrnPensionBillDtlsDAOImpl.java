package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillDtls;


public class TrnPensionBillDtlsDAOImpl extends GenericDaoHibernateImpl<TrnPensionBillDtls, Long> implements TrnPensionBillDtlsDAO {

	private final Log logger = LogFactory.getLog(getClass());

	public TrnPensionBillDtlsDAOImpl(Class<TrnPensionBillDtls> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public List getTrnPensionBillDtls(long TrnPensionBillHdrPK) throws Exception {

		Iterator it = null;
		Object[] tuple = null;
		List lLstDtlsList = new ArrayList();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery
			.append(" Select A.pensionerCode,A.ppoNo,A.reducedPension,A.recoveryAmount,A.pensionerName,A.arrearAmount,A.other2,A.other3,A.arrearDtls,A.grossAmount,A.withHeldAmnt FROM TrnPensionBillDtls A WHERE A.trnPensionBillHdrId = :ltrnPensionBillHdrId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("ltrnPensionBillHdrId", TrnPensionBillHdrPK);

			List resultList = lQuery.list();
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lLstDtlsList.add(tuple[0]);
					lLstDtlsList.add(tuple[1]);
					lLstDtlsList.add(tuple[2]);
					lLstDtlsList.add(tuple[3]);
					lLstDtlsList.add(tuple[4]);
					lLstDtlsList.add(tuple[5]);
					lLstDtlsList.add(tuple[6]);
					lLstDtlsList.add(tuple[7]);
					lLstDtlsList.add(tuple[8]);
					lLstDtlsList.add(tuple[9]);
					lLstDtlsList.add(tuple[10]);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstDtlsList;
	}

	public TrnPensionBillDtls getTrnPensionBillDtlsVo(long TrnPensionBillHdrPK) throws Exception {

		TrnPensionBillDtls lLstDtlsList = new TrnPensionBillDtls();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionBillDtls A WHERE A.trnPensionBillHdrId = :ltrnPensionBillHdrId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("ltrnPensionBillHdrId", TrnPensionBillHdrPK);

			List resultList = lQuery.list();
			if (!resultList.isEmpty()) {
				lLstDtlsList = (TrnPensionBillDtls) resultList.get(0);
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstDtlsList;
	}

	public String getDesgDesc(String lStrDesignation) throws Exception {

		String lStrDesgDesc = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT A.dsgnName FROM OrgDesignationMst A WHERE A.dsgnCode = :lDesgCode");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lDesgCode", lStrDesignation);

			List lLst = lQuery.list();

			if (!lLst.isEmpty()) {
				lStrDesgDesc = (lLst.get(0)).toString();
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lStrDesgDesc;
	}

	public int getMaxOfForMonth(String lStrPensionerCode, String lStrPPONumber, String lStrRecoveryFromFlag) throws Exception {

		int lIntReturn = 0;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT max(B.forMonth)  FROM TrnPensionBillDtls A,TrnPensionBillHdr B  WHERE A.trnPensionBillHdrId = B.trnPensionBillHdrId "
					+ " AND A.pensionerCode = :pensionerCode  AND A.ppoNo = :ppoNo  AND B.billType = :billType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			lQuery.setString("ppoNo", lStrPPONumber);
			lQuery.setString("billType", lStrRecoveryFromFlag);

			List lLst = lQuery.list();

			if (!lLst.isEmpty()) {
				if (lLst.get(0) != null) {
					lIntReturn = Integer.parseInt((lLst.get(0)).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
		return lIntReturn;
	}
}
