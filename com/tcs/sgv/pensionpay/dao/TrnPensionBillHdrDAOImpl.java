package com.tcs.sgv.pensionpay.dao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionBillHdr;


public class TrnPensionBillHdrDAOImpl extends GenericDaoHibernateImpl<TrnPensionBillHdr, Long> implements TrnPensionBillHdrDAO {

	private final Log logger = LogFactory.getLog(getClass());

	public TrnPensionBillHdrDAOImpl(Class<TrnPensionBillHdr> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public int getBillGenerationMonth(String lStrPensionerCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer(400);

		int lIntMonth = 0;

		List lLstReturn = null;

		Query lQuery = null;

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT max(A.forMonth) FROM TrnPensionBillHdr A, TrnPensionBillDtls B WHERE A.trnPensionBillHdrId = B.trnPensionBillHdrId AND B.pensionerCode = :pensionerCode AND A.rejected != 1 ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);

			lLstReturn = lQuery.list();
			
			if(lLstReturn != null)
			{
				if (lLstReturn.get(0) != null && !lLstReturn.isEmpty()) 
				{
					lIntMonth = Integer.parseInt(lLstReturn.get(0).toString());
				}
			}
		} 
		catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lIntMonth;
	}

	public TrnPensionBillHdr getTrnPensionBillHdr(Long lLngBillNo, String lStrBillType) throws Exception {

		TrnPensionBillHdr lobjTrnPensionBillHdr = new TrnPensionBillHdr();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionBillHdr A WHERE A.billNo = :lBillNo AND A.billType = :lBillType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("lBillNo", lLngBillNo);
			lQuery.setString("lBillType", lStrBillType);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lobjTrnPensionBillHdr = (TrnPensionBillHdr) lLstVO.get(0);
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lobjTrnPensionBillHdr;
	}

	public TrnPensionBillHdr getUniqueTrnPensionBillHdr(Long lLngBillHdrId) throws Exception {

		TrnPensionBillHdr lobjTrnPensionBillHdr = new TrnPensionBillHdr();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionBillHdr A WHERE A.trnPensionBillHdrId = :lLngBillHdrId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("lLngBillHdrId", lLngBillHdrId);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lobjTrnPensionBillHdr = (TrnPensionBillHdr) lLstVO.get(0);
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lobjTrnPensionBillHdr;
	}

	public ArrayList<TrnPensionBillHdr> getTrnPensionBillHdrList(Long lLngBillNo, String lStrBillType) throws Exception {

		List<TrnPensionBillHdr> lLstobjTrnPensionBillHdr = new ArrayList<TrnPensionBillHdr>();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionBillHdr A WHERE A.billNo = :lBillNo AND A.billType = :lBillType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("lBillNo", lLngBillNo);
			lQuery.setString("lBillType", lStrBillType);

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				for (int i = 0; i < lLstVO.size(); i++) {
					lLstobjTrnPensionBillHdr.add((TrnPensionBillHdr) lLstVO.get(i));
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return (ArrayList<TrnPensionBillHdr>) lLstobjTrnPensionBillHdr;
	}

	public void updateTrnPensionBillHdrRejectStatus(Long lLngBillNo,Long gLngPostId,Long gLngUserId,Date gDate) throws Exception {

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" UPDATE TRN_PENSION_BILL_HDR SET REJECTED = 1,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_NO =:lBillNo ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lBillNo", lLngBillNo);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
	}

	public void updateRecoveryDtilsFromRejection(Long lLngBillNo,Long gLngPostId,Long gLngUserId,Date gDate) throws Exception
	{
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" UPDATE TRN_PENSION_RECOVERY_DTLS SET BILL_NO = null,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date WHERE BILL_NO =:lBillNo ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("lBillNo", lLngBillNo);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
	}
}
