package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;

/**
 * TrnPensionRecoveryDtlsDAOImpl specific DAO Implementation
 * 
 * @author Rupsa Mukherjee
 * @version 1.0
 */

public class TrnPensionRecoveryDtlsDAOImpl extends
		GenericDaoHibernateImpl<TrnPensionRecoveryDtls, Long> implements
		TrnPensionRecoveryDtlsDAO {

	Log gLogger = LogFactory.getLog(getClass());

	public TrnPensionRecoveryDtlsDAOImpl(Class<TrnPensionRecoveryDtls> type,
			SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);

	}

	public ArrayList<TrnPensionRecoveryDtls> getTrnPensionRecoveryDtls(
			String lStrPensionerCode, String lStrStatus) throws Exception {

		List<TrnPensionRecoveryDtls> lLstobjTrnPensionRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery
					.append(" FROM TrnPensionRecoveryDtls A WHERE A.pensionerCode = :lPnsrCode AND A.recoveryFromFlag = :lStatus  And billNo is null ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			lQuery.setString("lStatus", lStrStatus);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				for (int i = 0; i < lLstVO.size(); i++) {
					lLstobjTrnPensionRecoveryDtls
							.add((TrnPensionRecoveryDtls) lLstVO.get(i));
				}
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}

		return (ArrayList<TrnPensionRecoveryDtls>) lLstobjTrnPensionRecoveryDtls;
	}

	public ArrayList<TrnPensionRecoveryDtls> getTrnPensionRecoveryDtlsVOArray(
			String lStrPensionerCode, String lStrRecoveryFromFlagCVP,
			String lStrRecoveryFromFlagDCRG,
			String lStrRecoveryFromFlagPension, String lStrRecoveryFromFlagOMR,
			String lStrRecoveryFromFlagMonthly,
			String lStrRecoveryFromFlagChallan) throws Exception {

		ArrayList<TrnPensionRecoveryDtls> lObjTrnPensionRqstHdrArray = new ArrayList<TrnPensionRecoveryDtls>();

		StringBuffer lSBQuery = new StringBuffer();

		Query lQuery = null;

		try {
			Session ghibSession = getSession();
			if (lStrPensionerCode != null) {
				lSBQuery
						.append(" FROM TrnPensionRecoveryDtls WHERE pensionerCode = :pensionerCode  AND recoveryFromFlag IN (:recoveryFromFlagCVP,:recoveryFromFlagDCRG,:recoveryFromFlagPension,:recoveryFromFlagOMR,:recoveryFromFlagMonthly,:recoveryFromFlagChallan) ");
				lQuery = ghibSession.createQuery(lSBQuery.toString());

				lQuery.setString("pensionerCode", lStrPensionerCode);
				lQuery
						.setString("recoveryFromFlagCVP",
								lStrRecoveryFromFlagCVP);
				lQuery.setString("recoveryFromFlagDCRG",
						lStrRecoveryFromFlagDCRG);
				lQuery.setString("recoveryFromFlagPension",
						lStrRecoveryFromFlagPension);
				lQuery
						.setString("recoveryFromFlagOMR",
								lStrRecoveryFromFlagOMR);
				lQuery.setString("recoveryFromFlagMonthly",
						lStrRecoveryFromFlagMonthly);
				lQuery.setString("recoveryFromFlagChallan",
						lStrRecoveryFromFlagChallan);

				lObjTrnPensionRqstHdrArray = (ArrayList<TrnPensionRecoveryDtls>) lQuery
						.list();
			}
		} catch (Exception e) {
			gLogger.error("Error is " + e, e);
			throw (e);
		}
		return lObjTrnPensionRqstHdrArray;
	}

	public ArrayList<TrnPensionRecoveryDtls> getTrnPensionRecoveryChallanDtlsVOArray(
			String lStrPensionerCode, String lStrRecoveryFromFlag)
			throws Exception {

		ArrayList<TrnPensionRecoveryDtls> lObjTrnPensionRqstHdrArray = new ArrayList<TrnPensionRecoveryDtls>();

		StringBuffer lSBQuery = new StringBuffer();
		Query lQuery = null;

		try {
			Session ghibSession = getSession();
			if (lStrPensionerCode != null) {
				lSBQuery
						.append(" FROM TrnPensionRecoveryDtls  WHERE pensionerCode = :pensionerCode  AND recoveryFromFlag = :recoveryFromFlag ");
				lQuery = ghibSession.createQuery(lSBQuery.toString());

				lQuery.setString("pensionerCode", lStrPensionerCode);
				lQuery.setString("recoveryFromFlag", lStrRecoveryFromFlag);
				lObjTrnPensionRqstHdrArray = (ArrayList<TrnPensionRecoveryDtls>) lQuery
						.list();
			}
		} catch (Exception e) {
			gLogger.error("Error is " + e, e);
			throw (e);
		}
		return lObjTrnPensionRqstHdrArray;
	}

	public List getPKForTableTrnPensionRecoveryDtls(Long lLngPensionRequestId,
			String lStrPensionerCode, String lStrTypeFlag) throws Exception {

		List<Long> lLstReturn = null;
		StringBuffer lSBQuery = new StringBuffer();

		try {
			Session ghibSession = getSession();
			lSBQuery
					.append(" SELECT trnPensionRecoveryDtlsId  FROM TrnPensionRecoveryDtls  WHERE pensionerCode = :pensionerCode and isManual is null ");

			if (lStrTypeFlag != null) {
				lSBQuery.append(" AND recoveryFromFlag = :recoveryFromFlag ");
			}

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			if (lStrTypeFlag != null) {
				lQuery.setString("recoveryFromFlag", lStrTypeFlag);
			}

			lLstReturn = lQuery.list();
		} catch (Exception e) {
			gLogger
					.error(
							"Exception occurred in getPKForTableTrnPensionRecoveryDtls() method of PensionRecoveryInfoDAOImpl Class "
									+ e, e);
			throw (e);
		}

		return lLstReturn;
	}
}
