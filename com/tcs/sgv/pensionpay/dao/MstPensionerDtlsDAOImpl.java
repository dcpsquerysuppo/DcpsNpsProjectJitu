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
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;


public class MstPensionerDtlsDAOImpl extends GenericDaoHibernateImpl implements MstPensionerDtlsDAO {

	Log gLogger = LogFactory.getLog(getClass());

	public MstPensionerDtlsDAOImpl(Class<MstPensionerDtls> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public MstPensionerDtls getMstPensionerDtls(String lStrPensionerCode) throws Exception {

		MstPensionerDtls lobjMstPensionerDtls = new MstPensionerDtls();

		try {
			Session hiSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" FROM  MstPensionerDtls H WHERE H.pensionerCode = :lPnsrCode ");
			Query lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lPnsrCode", lStrPensionerCode);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lobjMstPensionerDtls = (MstPensionerDtls) lLstVO.get(0);
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lobjMstPensionerDtls;
	}

	/*
	 * public ArrayList<LifeCertificateVO> getLifeCertificateVOList(String
	 * lStrBankCode,String lStrBranchCode,BigDecimal lBDHeadCode,String
	 * lStrLocCode,String lStrPostId, String lforMonth,SessionFactory
	 * sFactory,Integer pageNumber) throws Exception { List resultList; Iterator
	 * itr; Object[] tuple; String lStrName = ""; ArrayList<LifeCertificateVO>
	 * lObjLifeCertificateVOList = new ArrayList<LifeCertificateVO>();
	 * 
	 * StringBuffer lSBQuery = new StringBuffer(3000); //Query lQuery =null;
	 * 
	 * try { Session hiSession = getSession();
	 */

	/*
	 * SELECT TPRH.PPO_NO, MPH.FIRST_NAME || ' ' || MPH.MIDDLE_NAME || ' ' ||
	 * MPH.LAST_NAME, MPD.ACOUNT_NO, MB.BANK_NAME, RBB.BRANCH_NAME, MPH.PAN_NO,
	 * RBB.BRANCH_CODE, TPRH.HEAD_CODE , fd.name, fd.date_of_death FROM
	 * MST_PENSIONER_HDR MPH LEFT JOIN MST_PENSIONER_FAMILY_DTLS FD ON
	 * FD.PENSIONER_CODE = MPH.PENSIONER_CODE AND FD.PERCENTAGE=100,
	 * MST_PENSIONER_DTLS MPD, TRN_PENSION_RQST_HDR TPRH, MST_BANK MB,
	 * RLT_BANK_BRANCH RBB, WF_JOB_MST WFJ WHERE MPH.PENSIONER_CODE =
	 * TPRH.PENSIONER_CODE AND TPRH.PENSIONER_CODE = MPD.PENSIONER_CODE AND
	 * MPD.BANK_CODE = MB.BANK_CODE AND MB.BANK_CODE = RBB.BANK_CODE AND
	 * MPD.BRANCH_CODE = RBB.BRANCH_CODE AND MPH.CASE_STATUS = 'APPROVED' AND
	 * TPRH.LOCATION_CODE = 10055 AND TPRH.STATUS = 'Continue' AND
	 * RBB.LOCATION_CODE = TPRH.LOCATION_CODE AND WFJ.LOC_ID =
	 * TPRH.LOCATION_CODE AND WFJ.JOB_STATUS = 'Active' AND WFJ.DOC_ID = 210003
	 * AND WFJ.JOB_REF_ID_num = TPRH.PENSION_REQUEST_ID AND
	 * WFJ.LST_ACT_POST_ID_num = 104505 AND MPD.CASE_STATUS = 'APPROVED' AND
	 * TPRH.CASE_STATUS = 'APPROVED' AND MPH.LOCATION_CODE = MPD.LOCATION_CODE
	 * AND MPD.LOCATION_CODE = TPRH.LOCATION_CODE AND MPD.BANK_CODE = 12000 AND
	 * MPD.BRANCH_CODE = 12001 AND TPRH.HEAD_CODE = 1 AND ((TPRH.END_DATE IS NOT
	 * NULL AND TPRH.END_DATE >= SYSDATE) OR (TPRH.END_DATE IS NULL)) AND ((Not
	 * exists ( SELECT 1 FROM TRN_PENSION_REVALIDATION_DTLS REV where
	 * rev.ppo_no= TPRH.PPO_NO)) OR (exists (SELECT 1 FROM
	 * TRN_PENSION_REVALIDATION_DTLS REV, TRN_PENSION_RQST_HDR RQ,
	 * TRN_PENSIONER_SEEN_DTLS SEEN WHERE REV.PPO_NO = RQ.PPO_NO AND
	 * RQ.PENSIONER_CODE = SEEN.PENSIONER_CODE AND RQ.LOCATION_CODE = 10055 AND
	 * MONTHS_BETWEEN(SYSDATE, SEEN.SEEN_DATE) < 24 AND REV.INWARD_DATE IS NOT
	 * NULL AND REV.OUTWARD_DATE IS NOT NULL AND MONTHS_BETWEEN(SYSDATE,
	 * REV.INWARD_DATE) < 24 and REV.PPO_NO = TPRH.PPO_NO ))) ORDER BY
	 * RBB.BRANCH_CODE,TPRH.HEAD_CODE,TPRH.PPO_NO
	 */

	/*
	 * lSBQuery.append("SELECT TPRH.PPO_NO PPO,MPH.FIRST_NAME FNAME, "+
	 * " MPD.ACOUNT_NO ACCNO,MB.BANK_NAME BNK,RBB.BRANCH_NAME BRN,MPH.PAN_NO PAN,RBB.BRANCH_CODE BRCD,TPRH.HEAD_CODE HDCD, "
	 * +
	 * " fd.name LName,fd.date_of_death FDOD,mph.date_of_death DOD, MPH.date_of_birth DOB, "
	 * + " MPH.MIDDLE_NAME MNAME, "+ " MPH.LAST_NAME   LSNAME, "+
	 * " TPRH.LEDGER_NO  LDGNO,"+ " TPRH.PAGE_NO    PGNO,"+
	 * " FD.NAME         FMNAME"+ " FROM MST_PENSIONER_HDR MPH  "+
	 * "LEFT JOIN MST_PENSIONER_FAMILY_DTLS FD ON FD.PENSIONER_CODE = MPH.PENSIONER_CODE AND FD.PERCENTAGE=:fpPer, "
	 * +
	 * "MST_PENSIONER_DTLS  MPD,TRN_PENSION_RQST_HDR TPRH,MST_BANK MB,RLT_BANK_BRANCH RBB,WF_JOB_MST WFJ "
	 * +
	 * "WHERE MPH.PENSIONER_CODE = TPRH.PENSIONER_CODE AND TPRH.PENSIONER_CODE = MPD.PENSIONER_CODE and MPD.active_flag = 'Y' AND "
	 * +
	 * "MPD.BANK_CODE = MB.BANK_CODE AND MB.BANK_CODE = RBB.BANK_CODE AND MPD.BRANCH_CODE = RBB.BRANCH_CODE AND "
	 * +
	 * "MPH.CASE_STATUS = :caseStstus AND TPRH.LOCATION_CODE = :locationCode AND TPRH.STATUS = :status AND "
	 * +
	 * "RBB.LOCATION_CODE = TPRH.LOCATION_CODE AND WFJ.LOC_ID = TPRH.LOCATION_CODE AND WFJ.JOB_STATUS = :jobStatus AND "
	 * +
	 * "WFJ.DOC_ID = :docId AND WFJ.JOB_REF_ID_NUM = TPRH.PENSION_REQUEST_ID AND "
	 * +
	 * "WFJ.LST_ACT_POST_ID_num = :postId AND MPD.CASE_STATUS = :caseStstus AND TPRH.CASE_STATUS = :caseStstus AND "
	 * +
	 * "MPH.LOCATION_CODE = MPD.LOCATION_CODE AND MPD.LOCATION_CODE = TPRH.LOCATION_CODE  "
	 * );
	 */

	/*
	 * lSBQuery.append("SELECT tprh.ppo_no PPO,mph.first_name FNAME,mpd.acount_no ACCNO,"
	 * + "MB.BANK_NAME BNK,RBB.BRANCH_NAME BRN,MPH.PAN_NO PAN," +
	 * "RBB.BRANCH_CODE BRCD,TPRH.HEAD_CODE HDCD,fd.name LName," +
	 * "fd.date_of_death FDOD,mph.date_of_death DOD,MPH.date_of_birth DOB," +
	 * "MPH.MIDDLE_NAME MNAME,MPH.LAST_NAME LSNAME,TPRH.LEDGER_NO  LDGNO," +
	 * "TPRH.PAGE_NO PGNO,FD.NAME FMNAME" + " FROM trn_pension_bill_hdr bh" +
	 * " JOIN trn_pension_bill_dtls bd ON bh.trn_pension_bill_hdr_id = bd.trn_pension_bill_hdr_id "
	 * + "AND bh.for_month = bd.pay_for_month AND bh.rejected = 0 " +
	 * "JOIN trn_pension_rqst_hdr tprh ON tprh.pensioner_code = bd.pensioner_code AND tprh.case_status = :caseStstus "
	 * + "AND tprh.seen_flag = 'Y' AND tprh.status = :status" +
	 * " JOIN mst_pensioner_hdr mph ON mph.pensioner_code = tprh.pensioner_code "
	 * + "AND tprh.case_status = mph.case_status " +
	 * "JOIN mst_pensioner_dtls mpd ON mpd.pensioner_code = tprh.pensioner_code "
	 * + "AND tprh.case_status = mpd.case_status AND mpd.active_flag = 'Y' " +
	 * "JOIN RLT_BANK_BRANCH RBB ON RBB.LOCATION_CODE = BH.LOCATION_CODE AND RBB.BRANCH_CODE = MPD.BRANCH_CODE "
	 * + "JOIN MST_BANK MB ON RBB.BANK_CODE = MB.BANK_CODE " +
	 * "LEFT JOIN mst_pensioner_family_dtls fd ON fd.pensioner_code = tprh.pensioner_code AND percentage = :fpPer "
	 * + "WHERE bh.location_code = :locationCode AND bh.bill_type = :lBillType "
	 * + "AND bh.for_month = :lforMonth " + "AND bh.created_post_id = :postId");
	 * 
	 * 
	 * if(lStrBankCode != null && ! lStrBankCode.equals("-1")) {
	 * lSBQuery.append(" AND BH.BANK_CODE = :bankCode" ); if(lStrBranchCode !=
	 * null && ! lStrBranchCode.equals("-1"))
	 * lSBQuery.append(" AND BH.BRANCH_CODE = :branchCode"); } if(lBDHeadCode !=
	 * null && ! lBDHeadCode.toString().equals("-1"))
	 * lSBQuery.append(" AND TPRH.HEAD_CODE = :headCode");
	 */

	// lSBQuery.append(" ORDER BY BH.BRANCH_CODE,TPRH.HEAD_CODE,TPRH.LEDGER_NO,TPRH.PAGE_NO,tprh.ppo_no");
	/*
	 * lSBQuery.append("AND((TPRH.END_DATE IS NOT NULL AND TPRH.END_DATE >= :currDate) OR (TPRH.END_DATE IS NULL)) AND "
	 * +
	 * "((Not exists ( SELECT 1 FROM TRN_PENSION_REVALIDATION_DTLS REV where rev.ppo_no= TPRH.PPO_NO)) OR "
	 * +
	 * "(exists (SELECT 1  FROM TRN_PENSION_REVALIDATION_DTLS REV, TRN_PENSION_RQST_HDR RQ, "
	 * +
	 * "TRN_PENSIONER_SEEN_DTLS SEEN WHERE REV.PPO_NO = RQ.PPO_NO AND RQ.PENSIONER_CODE = SEEN.PENSIONER_CODE "
	 * + "AND RQ.LOCATION_CODE = :locationCode AND ");
	 * 
	 * if(! ApplicationHelper.DBType.equalsIgnoreCase("mysql")) {
	 * lSBQuery.append("MONTHS_BETWEEN(:currDate, SEEN.SEEN_DATE) < :lMonths ");
	 * } else {lSBQuery.append(
	 * "period_diff(date_format(:currDate,'%Y%m') , (date_format(SEEN.SEEN_DATE,'%Y%m')))< :lMonths "
	 * ); }lSBQuery.append(
	 * "AND REV.INWARD_DATE IS NOT NULL AND REV.OUTWARD_DATE IS NOT NULL AND ");
	 * 
	 * if(! ApplicationHelper.DBType.equalsIgnoreCase("mysql")) {
	 * lSBQuery.append
	 * ("MONTHS_BETWEEN(:currDate, REV.INWARD_DATE) < :lMonths "); } else {
	 * lSBQuery.append(
	 * "period_diff(date_format(:currDate,'%Y%m') , (date_format(REV.INWARD_DATE,'%Y%m'))) < :lMonths "
	 * ); }lSBQuery.append(
	 * "and REV.PPO_NO = TPRH.PPO_NO )))ORDER BY RBB.BRANCH_CODE,TPRH.HEAD_CODE,TPRH.PPO_NO "
	 * );
	 */
	/*
	 * SQLQuery lQuery = hiSession.createSQLQuery(lSBQuery.toString());
	 * 
	 * lQuery.addScalar("PPO", Hibernate.STRING); lQuery.addScalar("FNAME",
	 * Hibernate.STRING); lQuery.addScalar("ACCNO", Hibernate.STRING);
	 * lQuery.addScalar("BNK", Hibernate.STRING); lQuery.addScalar("BRN",
	 * Hibernate.STRING); lQuery.addScalar("PAN", Hibernate.STRING);
	 * lQuery.addScalar("BRCD", Hibernate.STRING); lQuery.addScalar("HDCD",
	 * Hibernate.BIG_DECIMAL); lQuery.addScalar("LName", Hibernate.STRING);
	 * lQuery.addScalar("FDOD", Hibernate.DATE); lQuery.addScalar("DOD",
	 * Hibernate.DATE); lQuery.addScalar("DOB", Hibernate.DATE);
	 * lQuery.addScalar("MNAME", Hibernate.STRING); lQuery.addScalar("LSNAME",
	 * Hibernate.STRING); lQuery.addScalar("LDGNO", Hibernate.STRING);
	 * lQuery.addScalar("PGNO", Hibernate.STRING); lQuery.addScalar("FMNAME",
	 * Hibernate.STRING);
	 * 
	 * lQuery.setLong("fpPer", 100); //lQuery.setInteger("lMonths",24);
	 * lQuery.setString("caseStstus","APPROVED");
	 * lQuery.setString("locationCode", lStrLocCode);
	 * lQuery.setString("status","Continue");
	 * //lQuery.setString("jobStatus","Active");
	 * //lQuery.setLong("docId",210003L); //lQuery.setLong("postId",gLngPostId);
	 * lQuery.setString("postId",lStrPostId);
	 * //lQuery.setDate("currDate",SessionHelper.getCurDate());
	 * lQuery.setString("lBillType", "Monthly"); lQuery.setString("lforMonth",
	 * lforMonth); //lQuery.setFirstResult((pageNumber - 1)1000);
	 * //lQuery.setMaxResults(1000);
	 * 
	 * if(lStrBankCode != null && ! lStrBankCode.equals("-1")) {
	 * lQuery.setString("bankCode", lStrBankCode); if(lStrBranchCode != null &&
	 * ! lStrBranchCode.equals("-1")) { lQuery.setString("branchCode",
	 * lStrBranchCode); } } if(lBDHeadCode != null && !
	 * lBDHeadCode.toString().equals("-1")) { lQuery.setBigDecimal("headCode",
	 * lBDHeadCode); }
	 * 
	 * resultList = lQuery.list();
	 * 
	 * IFMSCommonDAO commonDAO = new IFMSCommonDAOImpl(sFactory);
	 * 
	 * Map<String, Object> mapParam = new LinkedHashMap<String, Object>();
	 * mapParam.put("fpPer", 100); mapParam.put("lMonths",24);
	 * mapParam.put("caseStstus","APPROVED"); mapParam.put("locationCode",
	 * lStrLocCode); mapParam.put("status","Continue");
	 * mapParam.put("jobStatus","Active"); mapParam.put("docId","210003");
	 * mapParam.put("postId",gLngPostId.toString());
	 * mapParam.put("currDate",SessionHelper.getCurDate());
	 * 
	 * resultList = commonDAO.findByNamedQuery("pension.lifecert.getVOList",
	 * mapParam);
	 * 
	 * 
	 * if (!resultList.isEmpty()) { itr = resultList.iterator();
	 * 
	 * while (itr.hasNext()) { LifeCertificateVO lObjLifeCertificateVO = new
	 * LifeCertificateVO();
	 * 
	 * tuple = (Object[]) itr.next(); if(tuple[10]!=null) { if(tuple[9]==null) {
	 * if(tuple[8]!=null) lStrName = tuple[8].toString(); else if(tuple[1] !=
	 * null) { StringBuffer lStrPenName = new StringBuffer();
	 * lStrPenName.append(tuple[1] != null ? tuple[1]+" " : "");
	 * lStrPenName.append(tuple[12] != null ? tuple[12]+" " : "");
	 * lStrPenName.append(tuple[13] != null ? tuple[13] : ""); lStrName =
	 * lStrPenName.toString(); } } else if(tuple[1] != null) { StringBuffer
	 * lStrPenName = new StringBuffer(); lStrPenName.append(tuple[1] != null ?
	 * tuple[1]+" " : ""); lStrPenName.append(tuple[12] != null ? tuple[12]+" "
	 * : ""); lStrPenName.append(tuple[13] != null ? tuple[13] : ""); lStrName =
	 * lStrPenName.toString(); } } else{ if(tuple[1] != null) { StringBuffer
	 * lStrPenName = new StringBuffer(); lStrPenName.append(tuple[1] != null ?
	 * tuple[1]+" " : ""); lStrPenName.append(tuple[12] != null ? tuple[12]+" "
	 * : ""); lStrPenName.append(tuple[13] != null ? tuple[13] : ""); lStrName =
	 * lStrPenName.toString(); } }
	 * 
	 * lObjLifeCertificateVO.setPpoNo((String) tuple[0]);
	 * lObjLifeCertificateVO.setName(lStrName);
	 * lObjLifeCertificateVO.setAcountNo((String) tuple[2]);
	 * lObjLifeCertificateVO.setBankName((String) tuple[3]);
	 * lObjLifeCertificateVO.setBranchName((String) tuple[3]+" "+(String)
	 * tuple[4]); lObjLifeCertificateVO.setHeadCode(tuple[7] != null ?
	 * (BigDecimal)tuple[7] : BigDecimal.ZERO); if(tuple[5] != null) {
	 * lObjLifeCertificateVO.setPanNo((String) tuple[5]); } else {
	 * lObjLifeCertificateVO.setPanNo(""); }
	 * lObjLifeCertificateVO.setDateOfBirth(tuple[11] != null ?
	 * IFMSCommonServiceImpl.getStringFromDate((Date) tuple[11]) : "");
	 * lObjLifeCertificateVO.setLedgerNo(tuple[14] != null ? (String)
	 * tuple[14]:""); lObjLifeCertificateVO.setPageNo(tuple[15] != null ?
	 * (String) tuple[15]:""); lObjLifeCertificateVO.setFamilyName((String)
	 * tuple[16]); lObjLifeCertificateVOList.add(lObjLifeCertificateVO); } }
	 * 
	 * } catch (Exception e) { gLogger.error("Error is : "+e,e); throw (e); }
	 * Collections.sort(lObjLifeCertificateVOList, new
	 * LifeCertificateComparator()); return lObjLifeCertificateVOList; }
	 */

	/*
	 * public int getLifeCertificateVOListCount(String lStrBankCode,String
	 * lStrBranchCode,BigDecimal lBDHeadCode,String lStrLocCode,String
	 * lStrPostId, String lforMonth,SessionFactory sFactory,Integer pageNumber)
	 * throws Exception { List resultList; Iterator itr; int count = 0; Object[]
	 * tuple; String lStrName = ""; ArrayList<LifeCertificateVO>
	 * lObjLifeCertificateVOList = new ArrayList<LifeCertificateVO>();
	 * 
	 * StringBuffer lSBQuery = new StringBuffer(3000); //Query lQuery =null;
	 * 
	 * try { Session hiSession = getSession();
	 * 
	 * lSBQuery.append("SELECT count(1) as count FROM MST_PENSIONER_HDR MPH  "+
	 * "LEFT JOIN MST_PENSIONER_FAMILY_DTLS FD ON FD.PENSIONER_CODE = MPH.PENSIONER_CODE AND FD.PERCENTAGE=:fpPer, "
	 * +
	 * "MST_PENSIONER_DTLS  MPD,TRN_PENSION_RQST_HDR TPRH,MST_BANK MB,RLT_BANK_BRANCH RBB,WF_JOB_MST WFJ "
	 * +
	 * "WHERE MPH.PENSIONER_CODE = TPRH.PENSIONER_CODE AND TPRH.PENSIONER_CODE = MPD.PENSIONER_CODE and MPD.active_flag = 'Y' AND "
	 * +
	 * "MPD.BANK_CODE = MB.BANK_CODE AND MB.BANK_CODE = RBB.BANK_CODE AND MPD.BRANCH_CODE = RBB.BRANCH_CODE AND "
	 * +
	 * "MPH.CASE_STATUS = :caseStstus AND TPRH.LOCATION_CODE = :locationCode AND TPRH.STATUS = :status AND "
	 * +
	 * "RBB.LOCATION_CODE = TPRH.LOCATION_CODE AND WFJ.LOC_ID = TPRH.LOCATION_CODE AND WFJ.JOB_STATUS = :jobStatus AND "
	 * +
	 * "WFJ.DOC_ID = :docId AND WFJ.JOB_REF_ID_NUM = TPRH.PENSION_REQUEST_ID AND "
	 * +
	 * "WFJ.LST_ACT_POST_ID_NUM = :postId AND MPD.CASE_STATUS = :caseStstus AND TPRH.CASE_STATUS = :caseStstus AND "
	 * +
	 * "MPH.LOCATION_CODE = MPD.LOCATION_CODE AND MPD.LOCATION_CODE = TPRH.LOCATION_CODE  "
	 * );
	 * 
	 * if(lStrBankCode != null && ! lStrBankCode.equals("-1")) {
	 * lSBQuery.append("AND MPD.BANK_CODE = :bankCode " ); if(lStrBranchCode !=
	 * null && ! lStrBranchCode.equals("-1"))
	 * lSBQuery.append("AND MPD.BRANCH_CODE = :branchCode "); } if(lBDHeadCode
	 * != null && ! lBDHeadCode.toString().equals("-1"))
	 * lSBQuery.append("AND TPRH.HEAD_CODE = :headCode ");
	 * 
	 * lSBQuery.append(
	 * "AND((TPRH.END_DATE IS NOT NULL AND TPRH.END_DATE >= :currDate) OR (TPRH.END_DATE IS NULL)) AND "
	 * +
	 * "((Not exists ( SELECT 1 FROM TRN_PENSION_REVALIDATION_DTLS REV where rev.ppo_no= TPRH.PPO_NO)) OR "
	 * +
	 * "(exists (SELECT 1  FROM TRN_PENSION_REVALIDATION_DTLS REV, TRN_PENSION_RQST_HDR RQ, "
	 * +
	 * "TRN_PENSIONER_SEEN_DTLS SEEN WHERE REV.PPO_NO = RQ.PPO_NO AND RQ.PENSIONER_CODE = SEEN.PENSIONER_CODE "
	 * + "AND RQ.LOCATION_CODE = :locationCode AND ");
	 * 
	 * if(! ApplicationHelper.DBType.equalsIgnoreCase("mysql")) {
	 * lSBQuery.append("MONTHS_BETWEEN(:currDate, SEEN.SEEN_DATE) < :lMonths ");
	 * } else {lSBQuery.append(
	 * "period_diff(date_format(:currDate,'%Y%m') , (date_format(SEEN.SEEN_DATE,'%Y%m')))< :lMonths "
	 * ); }lSBQuery.append(
	 * "AND REV.INWARD_DATE IS NOT NULL AND REV.OUTWARD_DATE IS NOT NULL AND ");
	 * 
	 * if(! ApplicationHelper.DBType.equalsIgnoreCase("mysql")) {
	 * lSBQuery.append
	 * ("MONTHS_BETWEEN(:currDate, REV.INWARD_DATE) < :lMonths "); } else {
	 * lSBQuery.append(
	 * "period_diff(date_format(:currDate,'%Y%m') , (date_format(REV.INWARD_DATE,'%Y%m'))) < :lMonths "
	 * ); } lSBQuery.append("and REV.PPO_NO = TPRH.PPO_NO ))) ");
	 * 
	 * lSBQuery.append("SELECT count(1) as count FROM" +
	 * " trn_pension_bill_hdr bh" +
	 * " JOIN trn_pension_bill_dtls bd ON bh.trn_pension_bill_hdr_id = bd.trn_pension_bill_hdr_id "
	 * + "AND bh.for_month = bd.pay_for_month AND bh.rejected = 0 " +
	 * "JOIN trn_pension_rqst_hdr tprh ON tprh.pensioner_code = bd.pensioner_code AND tprh.case_status = :caseStstus "
	 * + "AND tprh.seen_flag = 'Y' AND tprh.status = :status" +
	 * " JOIN mst_pensioner_hdr mph ON mph.pensioner_code = tprh.pensioner_code "
	 * + "AND tprh.case_status = mph.case_status " +
	 * "JOIN mst_pensioner_dtls mpd ON mpd.pensioner_code = tprh.pensioner_code "
	 * + "AND tprh.case_status = mpd.case_status AND mpd.active_flag = 'Y' " +
	 * "JOIN RLT_BANK_BRANCH RBB ON RBB.LOCATION_CODE = BH.LOCATION_CODE AND RBB.BRANCH_CODE = MPD.BRANCH_CODE "
	 * + "JOIN MST_BANK MB ON RBB.BANK_CODE = MB.BANK_CODE " +
	 * "LEFT JOIN mst_pensioner_family_dtls fd ON fd.pensioner_code = tprh.pensioner_code AND percentage = :fpPer "
	 * + "WHERE bh.location_code = :locationCode AND bh.bill_type = :lBillType "
	 * + "AND bh.for_month = :lforMonth " + "AND bh.created_post_id = :postId");
	 * 
	 * 
	 * if(lStrBankCode != null && ! lStrBankCode.equals("-1")) {
	 * lSBQuery.append(" AND BH.BANK_CODE = :bankCode" ); if(lStrBranchCode !=
	 * null && ! lStrBranchCode.equals("-1"))
	 * lSBQuery.append(" AND BH.BRANCH_CODE = :branchCode"); } if(lBDHeadCode !=
	 * null && ! lBDHeadCode.toString().equals("-1"))
	 * lSBQuery.append(" AND TPRH.HEAD_CODE = :headCode");
	 * 
	 * SQLQuery lQuery = hiSession.createSQLQuery(lSBQuery.toString());
	 * 
	 * lQuery.addScalar("count",Hibernate.INTEGER);
	 * 
	 * lQuery.setLong("fpPer", 100); //lQuery.setInteger("lMonths",24);
	 * lQuery.setString("caseStstus","APPROVED");
	 * lQuery.setString("locationCode", lStrLocCode);
	 * lQuery.setString("status","Continue");
	 * //lQuery.setString("jobStatus","Active");
	 * //lQuery.setLong("docId",210003L); //lQuery.setLong("postId",gLngPostId);
	 * lQuery.setString("postId",lStrPostId);
	 * //lQuery.setDate("currDate",SessionHelper.getCurDate());
	 * lQuery.setString("lBillType", "Monthly"); lQuery.setString("lforMonth",
	 * lforMonth); lQuery.setFirstResult((pageNumber - 1)1000);
	 * lQuery.setMaxResults(1000);
	 * 
	 * if(lStrBankCode != null && ! lStrBankCode.equals("-1")) {
	 * lQuery.setString("bankCode", lStrBankCode); if(lStrBranchCode != null &&
	 * ! lStrBranchCode.equals("-1")) { lQuery.setString("branchCode",
	 * lStrBranchCode); } } if(lBDHeadCode != null && !
	 * lBDHeadCode.toString().equals("-1")) { lQuery.setBigDecimal("headCode",
	 * lBDHeadCode); }
	 * 
	 * resultList = lQuery.list();
	 * 
	 * BigDecimal lBdTemp = new BigDecimal(0); if (!resultList.isEmpty()) { if
	 * (resultList.get(0) != null) { count =
	 * Integer.parseInt(resultList.get(0).toString()); } }
	 * 
	 * } catch (Exception e) { gLogger.error("Error is : "+e,e); throw (e); }
	 * return count; }
	 */

	public Long getPnsionerDtlsIdFromPensionerCode(String pensionerCode) {

		Long pnsnDtlsId = null;
		StringBuffer query = new StringBuffer();
		try {
			Session hiSession = getSession();
			query.append(" SELECT d.pensionerDtlsId from MstPensionerDtls d  WHERE d.pensionerCode =:pensionerCode  AND d.activeFlag=:activeFlag ");
			Query hqlQuery = hiSession.createQuery(query.toString());
			hqlQuery.setString("pensionerCode", pensionerCode);
			hqlQuery.setString("activeFlag", "Y");
			List list = hqlQuery.list();
			if (!list.isEmpty()) {
				Iterator itr = list.iterator();
				while (itr.hasNext()) {
					pnsnDtlsId = (Long) (itr.next());
				}
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return pnsnDtlsId;
	}

	public Long getPnsionerDtlsIdFromPensionerCode(String pnsnCode, String lStrStatus) {

		Long pnsnDtlsId = 0L;
		StringBuffer query = new StringBuffer();
		try {
			Session hiSession = getSession();
			query.append(" SELECT d.pensionerDtlsId from MstPensionerDtls d  WHERE d.pensionerCode =:pensionerCode  AND d.activeFlag=:activeFlag AND caseStatus =:caseStatus ");

			Query hqlQuery = hiSession.createQuery(query.toString());
			hqlQuery.setString("pensionerCode", pnsnCode);
			hqlQuery.setString("caseStatus", lStrStatus);
			hqlQuery.setString("activeFlag", "Y");
			List list = hqlQuery.list();
			if (!list.isEmpty()) {
				Iterator itr = list.iterator();
				while (itr.hasNext()) {
					pnsnDtlsId = (Long) (itr.next());
				}

			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return pnsnDtlsId;
	}

	public List<MstPensionerDtls> getMstPensionerDtlsDiff(String lStrPnsnrCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Query lQuery = null;
		List<MstPensionerDtls> lLstResLst = new ArrayList<MstPensionerDtls>();
		Session ghibSession = getSession();
		try {
			lSBQuery.append(" FROM MstPensionerDtls D WHERE D.pensionerCode  =:PensionerCode  AND D.caseStatus in(:caseStatus1,:caseStatus2) and D.active_flag = 'Y' ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("PensionerCode", lStrPnsnrCode);
			lQuery.setString("caseStatus1", "NEW");
			lQuery.setString("caseStatus2", "APPROVED");
			lLstResLst = lQuery.list();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstResLst;
	}

	/*
	 * public String getACCNo(String lStrBankCode, String lStrBranchCode, String
	 * lStrPensionerCode) throws Exception {
	 * 
	 * String lStrACCNo = null;
	 * 
	 * List<String> lLstResult = null;
	 * 
	 * StringBuffer lSBQuery = new StringBuffer(200);
	 * 
	 * Query lQuery = null;
	 * 
	 * try { Session hiSession = getSession(); lSBQuery.append(" SELECT acountNo
	 * FROM MstPensionerDtls WHERE bankCode = :bankCode AND branchCode =
	 * :branchCode AND pensionerCode = :pensionerCode ");
	 * 
	 * lQuery = hiSession.createQuery(lSBQuery.toString());
	 * 
	 * lQuery.setString("bankCode", lStrBankCode);
	 * lQuery.setString("branchCode", lStrBranchCode);
	 * lQuery.setString("pensionerCode", lStrPensionerCode);
	 * 
	 * lLstResult = (List<String>) lQuery.list();
	 * 
	 * if (!lLstResult.isEmpty()) { lStrACCNo = (String) lLstResult.get(0); } }
	 * catch (Exception e) { gLogger.error("Error is : " + e, e); throw (e); }
	 * return lStrACCNo; }
	 */

	public String getACCNo(String lStrPensionerCode) throws Exception {

		String lStrACCNo = null;

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(200);

		Query lQuery = null;

		try {
			Session hiSession = getSession();
			lSBQuery.append(" SELECT acountNo FROM MstPensionerDtls WHERE pensionerCode = :pensionerCode ");

			lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrACCNo = lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrACCNo;
	}

	public List getAuditorAddress(String gStrLocCode, Long gLngLangId) throws Exception {

		ArrayList arrAuditorAddress = new ArrayList();
		StringBuffer lSBQuery = new StringBuffer();
		List resultList;
		Iterator itr;
		Object[] tuple;

		try {
			Session ghibSession = getSession();
			lSBQuery
					.append(" SELECT L.locName,L.locAddr1,L.locAddr2,C.cityName,D.districtName,S.stateName FROM  CmnLocationMst L,CmnCityMst C,CmnDistrictMst D,CmnStateMst  S WHERE  L.locCityId = C.cityId AND  L.locDistrictId = D.districtId AND  L.locStateId = S.stateId AND L.locationCode = :locationCode AND L.cmnLanguageMst.langId= :langId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("locationCode", gStrLocCode);
			lQuery.setLong("langId", gLngLangId);
			lQuery.setCacheable(true).setCacheRegion("pensionCache");

			resultList = lQuery.list();

			if (!resultList.isEmpty()) {
				itr = resultList.iterator();
				if (itr.hasNext()) {
					tuple = (Object[]) itr.next();
					if (tuple[0] != null) {
						arrAuditorAddress.add(tuple[0]);
					}
					if (tuple[1] != null) {
						arrAuditorAddress.add(tuple[1]);
					}
					if (tuple[2] != null) {
						arrAuditorAddress.add(tuple[2]);
					}
					if (tuple[3] != null) {
						arrAuditorAddress.add(tuple[3]);
					}
					if (tuple[4] != null) {
						arrAuditorAddress.add(tuple[4]);
					}
					if (tuple[5] != null) {
						arrAuditorAddress.add(tuple[5]);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return arrAuditorAddress;
	}

	public String getBranchName(String lStrBranchCode, String lStrAuditorBankCode) throws Exception {

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(150);

		String lStrReturn = null;

		Long lLngBankCode = null;
		Long lLngBranchCode = null;

		try {
			Session ghibSession = getSession();
			lLngBankCode = Long.parseLong(lStrAuditorBankCode);
			lLngBranchCode = Long.parseLong(lStrBranchCode);

			lSBQuery.append(" SELECT branchName FROM RltBankBranch WHERE bankCode = :bankCode AND branchCode = :branchCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("bankCode", lLngBankCode);
			lQuery.setLong("branchCode", lLngBranchCode);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrReturn = lLstResult.get(0);
			}
		} catch (Exception e) {
			logger.info("Error is : " + e, e);
			throw (e);
		}
		return lStrReturn;
	}

	public MstPensionerDtls getMstPnsnrDtlsWithStatus(String lStrPensionerCode, String lStrCaseStatus) throws Exception {

		MstPensionerDtls lobjMstPensionerDtls = null;

		try {
			Session hiSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM  MstPensionerDtls H WHERE H.pensionerCode = :lPnsrCode ");
			lSBQuery.append(" AND H.caseStatus = :caseStatus AND H.activeFlag=:activeFlag ");
			Query lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setString("lPnsrCode", lStrPensionerCode);
			lQuery.setString("caseStatus", lStrCaseStatus);
			lQuery.setString("activeFlag", "Y");

			lobjMstPensionerDtls = (MstPensionerDtls) lQuery.uniqueResult();

		} catch (Exception e) {
			gLogger.error(" Error is : For Pensioner Code " + lStrPensionerCode + " And Status ----- > " + lStrCaseStatus + e, e);
			throw (e);
		}
		return lobjMstPensionerDtls;
	}

}
