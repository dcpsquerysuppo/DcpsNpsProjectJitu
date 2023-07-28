package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.constants.PensionConstants;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;


public class TrnPensionArrearDtlsDAOImpl extends GenericDaoHibernateImpl<TrnPensionArrearDtls, Long> implements TrnPensionArrearDtlsDAO {

	/* Global Variable for Logger Class */
	private Log logger = LogFactory.getLog(getClass());

	public TrnPensionArrearDtlsDAOImpl(Class<TrnPensionArrearDtls> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	/**
	 * Getting a List of Vo's of TnrPensionArrearDtls.
	 * 
	 * @author Sagar
	 * @param lPensionCode
	 * @return List
	 */
	public List<TrnPensionArrearDtls> getTrnPensionArrearDtlsVo(String lStrPnsnCode) throws Exception {

		List<TrnPensionArrearDtls> lPensionArrearDtlsVOLst = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM  TrnPensionArrearDtls H WHERE H.pensionerCode = :lPnsrCode and H.arrearFieldType " + " not in ('TI','Pension_2006','Pension_2006_S') ORDER BY pensionArrearDtlsId");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPnsnCode);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lPensionArrearDtlsVOLst = new ArrayList<TrnPensionArrearDtls>();
				for (int i = 0; i < lLstVO.size(); i++) {
					lPensionArrearDtlsVOLst.add((TrnPensionArrearDtls) lLstVO.get(i));
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lPensionArrearDtlsVOLst;
	}

	/**
	 * Get a Arrear Amount for the Given month.
	 * 
	 * @author Sagar
	 */
	public Double getArrearForMonth(Integer lForMonth, String lStrPnsnCode) throws Exception {

		Double lArrearAmount = 0D;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT SUM(A.differenceAmount) FROM  TrnPensionArrearDtls A"
					+ " WHERE A.paymentFromYyyymm <= :lForMonth AND A.paymentToYyyymm >= :lForMonth AND A.pensionerCode = :lPnsrCode GROUP BY A.pensionerCode");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPnsnCode);
			lQuery.setInteger("lForMonth", lForMonth);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lArrearAmount = Double.valueOf(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lArrearAmount;
	}

	/**
	 * Get a TI Arrear Amount for the Given month.
	 * 
	 * @author Sagar
	 */
	public Double getTIArrearForMonth(Integer lForMonth, String lStrPnsnCode) throws Exception {

		Double lTIArrearAmount = 0D;

		String lStrTI = PensionConstants.ARREARTI;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT SUM(A.differenceAmount) FROM  TrnPensionArrearDtls A WHERE A.paymentFromYyyymm <= :lForMonth AND A.paymentToYyyymm >= :lForMonth"
					+ " AND A.pensionerCode = :lPnsrCode AND A.arrearFieldType = :lTI  GROUP BY A.pensionerCode");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPnsnCode);
			lQuery.setInteger("lForMonth", lForMonth);
			lQuery.setString("lTI", lStrTI);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lTIArrearAmount = Double.valueOf(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lTIArrearAmount;
	}

	/**
	 * Get a Fields wise total Arrear Amount for the Given month.
	 * 
	 * @author Sagar
	 */
	public ArrayList getArrearDtls(String lStrPensionerCode, Integer lStrForMonth) throws Exception {

		ArrayList larrArrearDtls = new ArrayList();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT H.arrearFieldType,SUM(H.differenceAmount) FROM TrnPensionArrearDtls H WHERE H.pensionerCode = :lPnsrCode ");
			if (lStrForMonth != null) {
				lSBQuery.append(" AND H.paymentFromYyyymm <= :lForMonth AND H.paymentToYyyymm >= :lForMonth ");
			}

			lSBQuery.append("GROUP BY H.arrearFieldType ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			if (lStrForMonth != null) {
				lQuery.setInteger("lForMonth", lStrForMonth);
			}

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				Iterator itr = lLstVO.iterator();
				Object tuple = null;
				while (itr.hasNext()) {
					tuple = itr.next();
					larrArrearDtls.add(tuple);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return larrArrearDtls;
	}

	public void deleteArrearsDtlsBypensionRequestIdAndIsManual(Long pensionRequestId, String isManual) throws Exception {

		StringBuffer lBdQuery = new StringBuffer();
		try {
			Session hiSession = getSession();
			lBdQuery.append(" DELETE FROM TrnPensionArrearDtls WHERE pensionRequestId=:pensionRequestId AND isManual=:isManual ");
			Query hbQuery = hiSession.createQuery(lBdQuery.toString());
			hbQuery.setLong("pensionRequestId", pensionRequestId);
			hbQuery.setString("isManual", isManual);
			hbQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
	}

	public void deleteArrearsDtlsBypensionRequestIdAndIsManualAndRemarks(Long pensionRequestId, String isManual, String lFieldType) throws Exception {

		StringBuffer lBdQuery = new StringBuffer();
		try {
			Session hiSession = getSession();
			lBdQuery.append(" DELETE FROM TrnPensionArrearDtls WHERE pensionRequestId=:pensionRequestId AND isManual=:isManual and arrearFieldType=:FieldType ");
			Query hbQuery = hiSession.createQuery(lBdQuery.toString());
			hbQuery.setLong("pensionRequestId", pensionRequestId);
			hbQuery.setString("isManual", isManual);
			hbQuery.setString("FieldType", lFieldType);
			hbQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
	}

	public void deleteArrearsDtlsByBillNo(Long billNo) throws Exception {

		StringBuffer lBdQuery = new StringBuffer();
		try {
			Session hiSession = getSession();
			lBdQuery.append(" DELETE FROM TrnPensionArrearDtls WHERE billNo=:billNo ");
			Query hbQuery = hiSession.createQuery(lBdQuery.toString());
			hbQuery.setLong("billNo", billNo);
			hbQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
	}

	public BigDecimal getSumofArrearsAmountByPnsnrCode(String lStrPnsnrCode, List lLstFieldType) throws Exception {

		StringBuffer lBdQuery = new StringBuffer();
		BigDecimal lBDResAmnt = BigDecimal.ZERO;
		try {
			Session hiSession = getSession();
			lBdQuery.append(" SELECT SUM(T.DIFFERENCE_AMOUNT) difAmnt FROM TRN_PENSION_ARREAR_DTLS T WHERE " + " T.PENSIONER_CODE =:PnsnrCode AND T.ARREAR_FIELD_TYPE IN(:FieldList) ");

			SQLQuery hbQuery = hiSession.createSQLQuery(lBdQuery.toString());

			hbQuery.setParameterList("FieldList", lLstFieldType);
			hbQuery.setString("PnsnrCode", lStrPnsnrCode);

			hbQuery.addScalar("difAmnt", Hibernate.BIG_DECIMAL);

			List lLstRes = hbQuery.list();

			if (!lLstRes.isEmpty()) {
				if (lLstRes.get(0) != null) {
					lBDResAmnt = new BigDecimal(lLstRes.get(0).toString());
				}
			}

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lBDResAmnt;
	}

	public void updateArrearsByPnsnrCodeAndFieldType(String lStrPnsnrCode, String lStrFieldType, BigDecimal lBDNewBasic, BigDecimal lBDOldBasic, BigDecimal lBDTotalArrears, BigDecimal lBDDiffAmnt,
			Long gLngPostId, Long gLngUserId, Date gDate, String lStrNewFieldType) throws Exception {

		StringBuffer lBdQuery = new StringBuffer();
		try {
			Session hiSession = getSession();
			lBdQuery.append(" UPDATE Trn_Pension_Arrear_Dtls " + " SET OLD_AMOUNT_PERCENTAGE =" + lBDOldBasic
					+ " ,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date" + " ,NEW_AMOUNT_PERCENTAGE =" + lBDNewBasic + " ,TOTAL_DIFFERENCE_AMT="
					+ lBDTotalArrears);
			if (lBDDiffAmnt != null) {
				lBdQuery.append(",DIFFERENCE_AMOUNT = " + lBDDiffAmnt);
			}
			if (lStrNewFieldType != null) {
				lBdQuery.append(",arrear_Field_Type = '" + lStrNewFieldType + "'");
			}
			lBdQuery.append(" WHERE pensioner_Code=:pensionerCode and arrear_Field_Type=:FieldType ");
			Query hbQuery = hiSession.createSQLQuery(lBdQuery.toString());
			hbQuery.setString("pensionerCode", lStrPnsnrCode);
			hbQuery.setString("FieldType", lStrFieldType);
			hbQuery.setLong("updated_user_id", gLngUserId);
			hbQuery.setLong("updated_post_id", gLngPostId);
			hbQuery.setDate("updated_date", gDate);
			hbQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
	}

	public void deleteTiArrForProvPpo(String lStrPenCode) throws Exception {

		try {
			Query lQuery = getSession().createSQLQuery(" DELETE FROM trn_pension_arrear_dtls WHERE arrear_field_type = 'TI' AND pensioner_code = :pensioner_code ");

			lQuery.setString("pensioner_code", lStrPenCode);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw (e);
		}
	}

	public void insertArrearDtls(List<TrnPensionArrearDtls> lLstTrnPensionArrearDtls) throws Exception {

		Session hibSession = getSession();
		try {
			for (TrnPensionArrearDtls lObjTrnPensionArrearDtls : lLstTrnPensionArrearDtls) {
				hibSession.save(lObjTrnPensionArrearDtls);
			}
			hibSession.flush();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}
}
