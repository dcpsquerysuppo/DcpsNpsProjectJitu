/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Aug 25, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Aug 25, 2011
 */
public class CVPRestorationLetterDAOImpl extends GenericDaoHibernateImpl implements CVPRestorationLetterDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	SessionFactory sessionFactory = null;

	public CVPRestorationLetterDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.service.CVPRestorationLetterDAO#
	 * getElgibilePensionerCode(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */

	public List getCVPRestorationDtls(String strFromDate, String strToDate, String strLocationCode) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstCVPRestorationDtls = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT cvp.pensionerCode,cvp.amount,cvp.restnAplnReceivedDate,cvp.fromDate,cvp.toDate ");
			lSBQuery.append(" FROM TrnCvpRestorationDtls cvp WHERE cvp.locationCode =:locationCode AND cvp.restnAplnReceivedDate BETWEEN :fromDate AND :toDate ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setDate("fromDate", StringUtility.convertStringToDate(strFromDate));
			hqlQuery.setDate("toDate", StringUtility.convertStringToDate(strToDate));
			hqlQuery.setParameter("locationCode", Long.valueOf(strLocationCode));
			lLstCVPRestorationDtls = hqlQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstCVPRestorationDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.CVPRestorationLetterDAO#getEffectvAmnt
	 * (java.lang.String, java.lang.String) Get latest paid details from bill
	 * tables.
	 */

	public List getRstrnLetterDtls(String strPensionerCode, Integer lIntYearMonth) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstCVPRestorationDtls = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT rqst.cvpOrderNo,mst.dateOfRetirement,dtls.pensionerName,rqst.ppoNo,rqst.basicPensionAmount,dtls.pensionerCode,dtls.grossAmount,dtls.arrearAmount,dtls.tiArrearAmount,mst.pensionerAddr,reg.billDate");
			lSBQuery.append(" FROM TrnPensionBillDtls dtls,TrnPensionBillHdr hdr,TrnBillRegister reg,TrnPensionRqstHdr rqst,MstPensionerHdr mst ");
			lSBQuery.append(" WHERE dtls.pensionerCode=rqst.pensionerCode AND mst.pensionerCode=dtls.pensionerCode AND mst.pensionerCode=rqst.pensionerCode ");
			lSBQuery.append(" AND hdr.trnPensionBillHdrId = dtls.trnPensionBillHdrId AND reg.billNo = hdr.billNo ");
			lSBQuery.append(" AND (hdr.billType = 'PENSION' OR hdr.billType = 'Monthly') AND reg.currBillStatus NOT IN (-15,-1,5,10,60) ");
			// lSBQuery.append(" AND dtls.pensionerCode=:pensionerCode AND dtls.payForMonth = :payForMonth");
			lSBQuery.append(" AND dtls.pensionerCode=:pensionerCode ");
			lSBQuery.append(" order by reg.billDate desc");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setMaxResults(1);
			hqlQuery.setParameter("pensionerCode", strPensionerCode);
			// hqlQuery.setInteger("payForMonth", lIntYearMonth);
			lLstCVPRestorationDtls = hqlQuery.list();

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstCVPRestorationDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.CVPRestorationLetterDAO#getCommutedAmnt
	 * (java.lang.String)
	 */

	public BigDecimal getCommutedAmnt(String strPensionerCode) {

		BigDecimal lBDCommutedAmnt = BigDecimal.ZERO;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List<BigDecimal> lLstTempList = new ArrayList<BigDecimal>();
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT SUM(amount) FROM TrnCvpRestorationDtls  ");
			lSBQuery.append("  WHERE pensionerCode =:pensionerCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("pensionerCode", strPensionerCode);
			lLstTempList = hqlQuery.list();
			if (lLstTempList != null && lLstTempList.size() > 0) {
				lBDCommutedAmnt = lLstTempList.get(0);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lBDCommutedAmnt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.service.CVPRestorationLetterDAO#getOffiCeAddr()
	 */

	public String getOffiCeAddr(String lStrLocationCode) {

		StringBuilder lSBQuery = null;
		String lStrOffAddr = "";
		Query hqlQuery = null;
		List lLstTempList = null;
		String lStrLocAddr1 = "";
		String lStrLocAddr2 = "";
		String lStrCityName = "";
		String lStrDistrictName = "";
		String lStrStateName = "";
		String lStrPinCode = "";
		Object[] tuple;

		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT loc.locAddr1, loc.locAddr2, city.cityName, dist.districtName, state.stateName, loc.locPin  \n");
			lSBQuery.append(" FROM CmnLocationMst loc,CmnCityMst city,CmnDistrictMst dist,CmnStateMst state \n");
			lSBQuery.append(" WHERE city.cityId = loc.locCityId AND dist.districtId = city.cmnDistrictMst.districtId AND state.stateId = dist.cmnStateMst.stateId   \n");
			lSBQuery.append(" AND (loc.departmentId  =  100003 or loc.departmentId = 1) AND loc.locationCode = :locationCode ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			lLstTempList = hqlQuery.list();
			Iterator it = lLstTempList.iterator();

			if (lLstTempList != null && lLstTempList.size() > 0) {
				if (lLstTempList.get(0) != null) {
					tuple = (Object[]) it.next();
					lStrLocAddr1 = tuple[0] == null ? "" : tuple[0].toString() + ", ";
					lStrLocAddr2 = tuple[1] == null ? "" : tuple[1].toString() + ", ";
					lStrCityName = tuple[2] == null ? "" : tuple[2].toString() + "- ";
					lStrDistrictName = tuple[3] == null ? "" : tuple[3].toString() + ", ";
					lStrStateName = tuple[4] == null ? "" : tuple[4].toString() + ", ";
					lStrPinCode = tuple[5] == null ? "" : tuple[5].toString();
					lStrOffAddr = lStrLocAddr1 + lStrLocAddr2 + lStrStateName + lStrDistrictName + lStrCityName + lStrPinCode;
				}
			}

		} catch (Exception e) {

			gLogger.error("Error is :" + e, e);
		}
		return lStrOffAddr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.CVPRestorationLetterDAO#checkBillIsExistsOrNot
	 * (java.lang.String, java.lang.Integer)
	 */

	public Boolean checkBillIsExistsOrNot(String strPensionerCode, Integer intYearMonth) {

		Boolean lBlFlag = false;
		Integer lIntCnt = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstTempList = new ArrayList();
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT COUNT(*) FROM TrnPensionBillDtls dtls,TrnPensionBillHdr hdr,TrnBillRegister reg ");
			lSBQuery.append(" WHERE hdr.trnPensionBillHdrId = dtls.trnPensionBillHdrId AND reg.billNo = hdr.billNo ");
			lSBQuery.append(" AND (hdr.billType = 'PENSION' OR hdr.billType = 'Monthly') AND reg.currBillStatus NOT IN (-15,-1,5,10,60) ");
			// lSBQuery.append(" AND dtls.pensionerCode=:pensionerCode AND dtls.payForMonth = :payForMonth");
			lSBQuery.append(" AND dtls.pensionerCode=:pensionerCode "); // check
																		// whether
																		// any
																		// monthly
																		// or
																		// pension
																		// bill
																		// is
																		// paid
																		// to
																		// the
																		// pensioner
																		// or
																		// not.
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("pensionerCode", strPensionerCode);
			// hqlQuery.setParameter("payForMonth", intYearMonth);
			lLstTempList = hqlQuery.list();
			if (lLstTempList != null && lLstTempList.size() > 0) {
				lIntCnt = Integer.valueOf(lLstTempList.get(0).toString());
			}
			if (lIntCnt > 0) {
				lBlFlag = true;
			}

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}

		return lBlFlag;

	}

}
