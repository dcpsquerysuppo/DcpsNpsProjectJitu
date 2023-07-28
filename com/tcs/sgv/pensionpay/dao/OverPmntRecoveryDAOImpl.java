/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jul 1, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Jul 1, 2011
 */
public class OverPmntRecoveryDAOImpl extends GenericDaoHibernateImpl implements OverPmntRecoveryDAO {

	/**
	 * @param type
	 */
	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	private final ResourceBundle gObjBillRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionConstants");

	SessionFactory sessionFactory = null;

	public OverPmntRecoveryDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO#getPPODtls(java.lang.String
	 * , java.lang.Long)
	 */

	public List getPPODtls(String lStrPPONo, Long langId, String lStrLocationCode) {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		List lLstPPODtls = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT pension.first_name,req.pensioner_code,req.commencement_date,req.case_status,req.head_code,nvl(dtls.request_id,' '),pension.date_of_death,dtls.total_amount_torecover \n");
			lSBQuery.append(" FROM trn_pension_rqst_hdr req  \n");
			lSBQuery.append(" JOIN mst_pensioner_hdr pension ON req.pensioner_code=pension.pensioner_code \n");
			lSBQuery.append(" JOIN cmn_location_mst loc  ON loc.location_code=req.location_code  AND loc.lang_id ='" + langId + "'  \n");
			lSBQuery.append(" LEFT OUTER JOIN trn_pension_challan_dtls dtls ON dtls.pensioner_code =  req.pensioner_code and dtls.status = :lRqstStatus \n");
			lSBQuery.append(" WHERE \n");
			lSBQuery.append(" req.ppo_no = '" + lStrPPONo.trim() + "'  AND req.location_code = '" + lStrLocationCode + "' ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("lRqstStatus", gObjRsrcBndle.getString("OPR.PENDING"));
			lLstPPODtls = lQuery.list();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstPPODtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO#getMaxRequestId()
	 */

	public Long getMaxRequestId(String locationCode) {

		Long llngRequestId = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT MAX(requestId) ");
			lSBQuery.append(" FROM TrnPensionChallanDtls recovery");
			lSBQuery.append(" WHERE recovery.locationCode =:locationCode  ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("locationCode", locationCode);
			if (hqlQuery.list().get(0) == null) {
				llngRequestId = 0l;
			} else {
				llngRequestId = Long.valueOf(hqlQuery.list().get(0).toString());
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return llngRequestId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO#getEligibleBills(java.
	 * util.Date, java.lang.String, java.lang.String)
	 */

	public List getEligibleBills(Date lDtExpiryDate, String lStrPnsnrCode, String lStrBillType, String gStrLocationCode) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstEligibleBills = null;
		List<String> lLstBillType = new ArrayList<String>();
		lLstBillType.add(gObjBillRsrcBndle.getString("RECOVERY.MONTHLY"));
		lLstBillType.add(gObjBillRsrcBndle.getString("RECOVERY.PENSION"));
		List<Short> lLstBillStatus = new ArrayList<Short>();
		lLstBillStatus.add(DBConstants.ST_BILL_DISCARD);
		lLstBillStatus.add(DBConstants.ST_BILL_ARCHEIVED);
		lLstBillStatus.add(DBConstants.ST_BILL_CREATED);
		lLstBillStatus.add(DBConstants.ST_BILL_FORW_TO_ATO);
		lLstBillStatus.add(DBConstants.ST_BILL_REJECTED);
		SimpleDateFormat lSdf = new SimpleDateFormat("yyyyMM");
		Integer lIntDOR = Integer.valueOf(lSdf.format(lDtExpiryDate));
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT dtls.net_Amount,hdr.for_Month \n");
			lSBQuery.append(" FROM Trn_Pension_Bill_Dtls dtls,Trn_Pension_Bill_Hdr hdr,Trn_Bill_Register tbr \n");
			lSBQuery.append(" WHERE dtls.trn_Pension_Bill_Hdr_Id=hdr.trn_Pension_Bill_Hdr_Id \n");
			lSBQuery.append(" AND tbr.bill_No = hdr.bill_No ");
			lSBQuery.append(" AND  dtls.pensioner_Code =:pensionerCode \n");
			lSBQuery.append(" AND hdr.for_Month >=:lIntDOR  AND hdr.bill_Type in (:billType)  ");
			lSBQuery.append(" AND tbr.curr_Bill_Status not in (:lLstBillStatus)");
			lSBQuery.append(" UNION ALL \n");
			lSBQuery.append(" SELECT dtlsarc.net_Amount,hdrarc.for_Month \n");
			lSBQuery.append(" FROM Trn_Pension_Bill_Dtls_Arc dtlsarc,Trn_Pension_Bill_Hdr_Arc hdrarc,Trn_Bill_Register tbr \n");
			lSBQuery.append(" WHERE dtlsarc.trn_Pension_Bill_Hdr_Arc_Id=hdrarc.trn_Pension_Bill_Hdr_Arc_Id \n");
			lSBQuery.append(" AND tbr.bill_No = hdrarc.bill_No ");
			lSBQuery.append(" AND  dtlsarc.pensioner_Code =:pensionerCode \n");
			lSBQuery.append(" AND hdrarc.for_Month >=:lIntDOR  AND hdrarc.bill_Type in (:billType) ");
			lSBQuery.append(" AND tbr.curr_Bill_Status not in (:lLstBillStatus)");
			hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			hqlQuery.setParameter("pensionerCode", lStrPnsnrCode);
			// hqlQuery.setParameter("locationCode", gStrLocationCode);
			hqlQuery.setInteger("lIntDOR", lIntDOR);
			hqlQuery.setParameterList("billType", lLstBillType);
			hqlQuery.setParameterList("lLstBillStatus", lLstBillStatus);
			lLstEligibleBills = hqlQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstEligibleBills;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO#getRecoveryDtls(java.lang
	 * .String, java.lang.String)
	 */

	public List getRecoveryDtls(String lStrRequestId, String lStrLocationCode) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstRecoveryDtls = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT dtls.pensionerCode,dtls.ppoNo,dtls.name,dtls.deathDate,dtls.totalAmountTorecover,dtls.schemeCode, ");
			lSBQuery.append(" req.headCode,dtls.status,dtls.payOrderNo,dtls.payOrderDate,dtls.challanNo,dtls.challanDate,dtls.payOrderAmnt ");
			lSBQuery.append(" FROM TrnPensionRqstHdr req,TrnPensionChallanDtls dtls ");
			lSBQuery.append(" WHERE req.pensionerCode = dtls.pensionerCode ");
			lSBQuery.append(" AND dtls.requestId = :requestId AND dtls.locationCode=:locationCode");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			// hqlQuery.setParameter("status",
			// gObjRsrcBndle.getString("OPR.PENDING"));
			hqlQuery.setParameter("requestId", lStrRequestId);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			lLstRecoveryDtls = hqlQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstRecoveryDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO#getAllPnsnrCodes()
	 */

	public List getAllPnsnrCodes() {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List<String> lLstPnsnsrCodes = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT pensionerCode,status FROM TrnPensionChallanDtls ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstPnsnsrCodes = hqlQuery.list();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstPnsnsrCodes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO#getRecoveryLtrDtls(java
	 * .lang.String, java.lang.String)
	 */

	public List getRecoveryLtrDtls(String lStrRqstId, String lStrLocationCode) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstRecoveryLtrDtls = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT bank.bankName,bank.bankAddress,branch.branchName,branch.branchAddress,hdr.firstName,hdr.dateOfDeath,  ");
			lSBQuery.append(" dtls.accountNo,recovery.totalAmountTorecover,loc.locName,recovery.requestId ");
			lSBQuery.append(" FROM MstPensionerDtls dtls,MstBank bank,RltBankBranch branch,MstPensionerHdr hdr,TrnPensionChallanDtls recovery,CmnLocationMst loc ");
			lSBQuery.append(" WHERE dtls.bankCode = bank.bankCode AND dtls.branchCode = branch.branchCode AND bank.bankCode  = branch.bankCode  ");
			lSBQuery.append(" AND loc.locationCode = hdr.locationCode ");
			lSBQuery.append(" AND dtls.pensionerCode = hdr.pensionerCode AND recovery.pensionerCode = dtls.pensionerCode AND recovery.pensionerCode = hdr.pensionerCode ");
			lSBQuery.append(" AND recovery.requestId = :lStrRqstId AND loc.locationCode = :locationCode ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("lStrRqstId", lStrRqstId);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			lLstRecoveryLtrDtls = hqlQuery.list();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstRecoveryLtrDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO#getfamilyDtls(java.lang
	 * .String, java.lang.String)
	 */

	public List getfamilyDtls(String lStrPnsnrCode, String lStrLocationCode) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstRecoveryDtls = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT family.name,hdr.pensionerAddr ");
			lSBQuery.append(" FROM MstPensionerHdr hdr,MstPensionerFamilyDtls family ");
			lSBQuery.append(" WHERE family.pensionerCode = hdr.pensionerCode ");
			lSBQuery.append(" AND hdr.pensionerCode = :pensionerCode AND hdr.locationCode = :locationCode AND family.percentage = 100 ");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("pensionerCode", lStrPnsnrCode);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			lLstRecoveryDtls = hqlQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstRecoveryDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.OverPmntRecoveryDAO#getPeriodDates(java.lang
	 * .String, java.util.Date, java.lang.String, java.lang.String)
	 */

	public List getPeriodDates(String lStrBillType, Date lDtDeathDate, String lStrPnsnrCode, String lStrLocationCode) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstPeriodDates = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT dtls.netAmount,hdr.forMonth,hdr.billDate ");
			lSBQuery.append(" FROM TrnPensionBillHdr hdr,TrnPensionBillDtls dtls ");
			lSBQuery.append(" WHERE dtls.trnPensionBillHdrId = hdr.trnPensionBillHdrId AND dtls.pensionerCode = :pensionerCode ");
			lSBQuery.append(" AND hdr.billDate >= :deathDate AND hdr.billType = :billType AND hdr.locationCode = :locationCode  ");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("pensionerCode", lStrPnsnrCode);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			hqlQuery.setDate("deathDate", lDtDeathDate);
			hqlQuery.setParameter("billType", lStrBillType);
			lLstPeriodDates = hqlQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstPeriodDates;
	}

}
