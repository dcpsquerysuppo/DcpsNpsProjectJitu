package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.ibm.icu.text.SimpleDateFormat;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;


public class MstPensionerHdrDAOImpl extends GenericDaoHibernateImpl<MstPensionerHdr, Long> implements MstPensionerHdrDAO {

	Log gLogger = LogFactory.getLog(getClass());

	public MstPensionerHdrDAOImpl(Class<MstPensionerHdr> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	public String getName(String lStrPensionerCode) throws Exception {

		String lStrName = null;
		List lLstResult = new ArrayList();
		Query lQuery = null;
		Object[] lObjArr = null;

		try {
			Session hiSession = getSession();

			lQuery = hiSession.createQuery(" SELECT firstName, middleName, lastName FROM MstPensionerHdr WHERE pensionerCode = :pensionerCode ");
			lQuery.setCacheable(true).setCacheRegion("pensionCache");
			lQuery.setParameter("pensionerCode", lStrPensionerCode);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lObjArr = (Object[]) lLstResult.get(0);
				lStrName = (lObjArr[0] == null ? "" : (String) lObjArr[0]) + " " + (lObjArr[1] == null ? "" : (String) lObjArr[1]) + " " + (lObjArr[2] == null ? "" : (String) lObjArr[2]);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrName;
	}

	public List getNameAndDesignation(String lStrPPONo, String strDate, String lStrLocCode) throws Exception {

		List lStrNameAndDesignation = new ArrayList();
		ArrayList lFQuater = new ArrayList();
		ArrayList lTQuater = new ArrayList();
		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer(200);
		Query lQuery = null;
		int iYear = Integer.valueOf(strDate.substring(6, 10));
		int assYear = iYear + 1;
		lFQuater.add("01/april/" + iYear);
		lFQuater.add("01/july/" + iYear);
		lFQuater.add("01/october/" + iYear);
		lFQuater.add("01/january/" + assYear);
		lTQuater.add("30/june/" + iYear);
		lTQuater.add("30/september/" + iYear);
		lTQuater.add("31/december/" + iYear);
		lTQuater.add("31/march/" + assYear);
		Integer.valueOf(strDate.substring(3, 5));
		int iDOJmonth = 0;
		int iDORmonth = 0;
		String strFromDate = "";
		String strTODate = "";
		try {
			Session hiSession = getSession();
			lSBQuery.append(" SELECT MPH.firstName||' '||MPH.middleName||' '||MPH.lastName,MPH.designation,MPH.panNo,MPH.dateOfJoin,MPH.dateOfRetirement,MPH.gender,MPH.dateOfBirth"
					+ " FROM MstPensionerHdr MPH,TrnPensionRqstHdr TPRH" + " WHERE TPRH.pensionerCode = MPH.pensionerCode"
					+ " AND TPRH.ppoNo = :lStrPPONo ANd TPRH.locationCode = MPH.locationCode AND TPRH.locationCode = :locationCode ");

			lQuery = hiSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lStrPPONo", lStrPPONo);
			lQuery.setParameter("locationCode", lStrLocCode);

			lLstResult = lQuery.list();
			Iterator it;
			Object tuple[] = null;
			it = lLstResult.iterator();
			while (it.hasNext()) { // Create By Jatin
				tuple = (Object[]) it.next();
				lStrNameAndDesignation.add(tuple[0].toString()); // name
				try {
					lStrNameAndDesignation.add(tuple[2].toString()); // pan
				} catch (Exception ex) {
					lStrNameAndDesignation.add("No PAN Number"); // pan
				}
				if (tuple[1] != null) { // designation
					lStrNameAndDesignation.add(getDesigName(Long.valueOf(tuple[1].toString())) == null ? "No Designation " : getDesigName(Long.valueOf(tuple[1].toString())));
				}
				lStrNameAndDesignation.add(iYear); // finyr
				if (tuple[3] != null) {
					strFromDate = (new SimpleDateFormat("dd/MM/yyyy").format(tuple[3])).toString();
					if (assYear == Integer.valueOf(strFromDate.substring(6, 10))) {
						iDOJmonth = Integer.valueOf(strFromDate.substring(3, 5));
						lFQuater.add(1, Integer.valueOf(strFromDate));
						if (iDOJmonth > 3 && iDOJmonth < 7) {
							lFQuater.add(0, strDate);
						} else if (iDOJmonth > 6 && iDOJmonth < 10) {
							lFQuater.add(1, strDate);
							lFQuater.add(0, "");
						} else if (iDOJmonth > 9 && iDOJmonth < 1) {
							lFQuater.add(2, strDate);
							lFQuater.add(0, "");
							lFQuater.add(1, "");
						} else {
							lFQuater.add(3, strDate);
							lFQuater.add(0, "");
							lFQuater.add(1, "");
							lFQuater.add(2, "");
						}
					}
				}
				lStrNameAndDesignation.add(lFQuater);
				if (tuple[4] != null) {
					strTODate = (new SimpleDateFormat("dd/MM/yyyy").format(tuple[4])).toString();
					if (assYear == Integer.valueOf(strTODate.substring(6, 10))) {
						iDORmonth = Integer.valueOf(strTODate.substring(3, 5));
						lTQuater.add(1, Integer.valueOf(strTODate));
						if (iDORmonth > 3 && iDORmonth < 7) {
							lTQuater.add(0, strDate);
						} else if (iDORmonth > 6 && iDORmonth < 10) {
							lTQuater.add(1, strDate);
							lTQuater.add(0, "");
							lTQuater.remove(0);
						} else if (iDORmonth > 9 && iDORmonth < 1) {
							lTQuater.add(2, strDate);
							lTQuater.add(0, "");
							lTQuater.add(1, "");
						} else {
							lTQuater.add(3, strDate);
							lTQuater.add(0, "");
							lTQuater.add(1, "");
							lTQuater.add(2, "");
						}
					}

				}
				lStrNameAndDesignation.add(lTQuater);

				BigDecimal BGGrossIncome = BigDecimal.ZERO;
				BGGrossIncome = getITGrossIncome(lStrPPONo, iYear, lStrLocCode);
				lStrNameAndDesignation.add((BGGrossIncome == null ? BigDecimal.ZERO : BGGrossIncome));
				if (tuple[5] != null) {
					lStrNameAndDesignation.add(tuple[5].toString());
				} else {
					lStrNameAndDesignation.add("");
				}
				if (tuple[6] != null) {
					lStrNameAndDesignation.add(new SimpleDateFormat("dd/MM/yyyy").format(tuple[6]).toString());
				} else {
					lStrNameAndDesignation.add("");
				}
			}
			lStrNameAndDesignation.add(assYear);
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrNameAndDesignation;
	}

	public String getPANNo(String lStrPensionerCode) throws Exception {

		String lStrPANNo = null;

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(100);

		Query lQuery = null;

		try {
			Session hiSession = getSession();
			lSBQuery.append(" SELECT panNo FROM MstPensionerHdr WHERE pensionerCode = :pensionerCode ");

			lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("pensionerCode", lStrPensionerCode);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrPANNo = lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrPANNo;
	}

	public Long getPensionerIdfromPensnrCode(String lStrPnsnrCode, String lStrCaseStatus) throws Exception {

		Session hiSession = getSession();
		Long lbgdcCaseId = 0L;
		StringBuilder strQuery = new StringBuilder();
		List resultList;
		try {

			strQuery.append(" SELECT r.pensionerId " + // Updated By Jatin
					" FROM MstPensionerHdr r " + " WHERE r.pensionerCode =:pensionerCode AND r.caseStatus =:caseStatus");

			Query hqlQuery = hiSession.createQuery(strQuery.toString());
			hqlQuery.setString("pensionerCode", lStrPnsnrCode.trim());
			hqlQuery.setString("caseStatus", lStrCaseStatus);
			resultList = hqlQuery.list();

			if (!resultList.isEmpty()) {
				lbgdcCaseId = (Long) resultList.get(0);
			}

			return lbgdcCaseId;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<MstPensionerHdr> getMstPensionerHdrDiff(String lStrPnsnrCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Query lQuery = null;
		List<MstPensionerHdr> lLstResLst = new ArrayList<MstPensionerHdr>();
		Session ghibSession = getSession();
		try {
			lSBQuery.append(" FROM MstPensionerHdr D WHERE D.pensionerCode  = '" + lStrPnsnrCode + "' AND " + " (D.caseStatus = 'NEW' OR D.caseStatus = 'APPROVED' ) ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lLstResLst = lQuery.list();
		} catch (Exception e) {
			throw e;
		}
		return lLstResLst;
	}

	public MstPensionerHdr getMstPensionerHdrDtls(String lStrPensionerCode) throws Exception {

		MstPensionerHdr lobjMstPensionerHdr = new MstPensionerHdr();

		try {
			Session hiSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" FROM  MstPensionerHdr H WHERE H.pensionerCode = :lPnsrCode ");
			Query lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lPnsrCode", lStrPensionerCode);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lobjMstPensionerHdr = (MstPensionerHdr) lLstVO.get(0);
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lobjMstPensionerHdr;
	}

	public MstPensionerHdr getMstPnsnrHdrWithStatus(String lStrPensionerCode, String lStrCaseStatus) throws Exception {

		MstPensionerHdr lobjMstPensionerHdr = null;

		try {
			Session hiSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" FROM  MstPensionerHdr H WHERE H.pensionerCode = :lPnsrCode ");
			// lSBQuery.append(" AND H.caseStatus = :caseStatus ");
			Query lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lPnsrCode", lStrPensionerCode);
			// lQuery.setParameter("caseStatus", lStrCaseStatus);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lobjMstPensionerHdr = (MstPensionerHdr) lLstVO.get(0);
			}
		} catch (Exception e) {
			gLogger.error(" Error is : " + e, e);
			throw (e);
		}
		return lobjMstPensionerHdr;
	}

	/*
	 * public List getMRRelatedInfoSaved(String lStrPPONO,String
	 * lStrCaseStatus,String gStrLocationCode,String lStrMedRemId,String Scheme)
	 * throws Exception { StringBuilder lSBQuery = new StringBuilder(); Query
	 * lQuery = null; List lLstResLst = new ArrayList(); Session ghibSession =
	 * getSession(); try {lSBQuery.append(
	 * " SELECT  tprh.schemeType,tprh.medicalAllowenceAmount,mph.pnsnrPrefix||' '||mph.firstName||' '||mph.middleName||' '||mph.lastName, "
	 * ); if((Scheme.equals("IRLA")) || (Scheme.equals("PSB"))) {
	 * lSBQuery.append(
	 * " mpd.branchCode,tprh.headCode,rbb.branchName,tprh.caseOwner,tpmr.remAmnt,tpmr.inwdNo,tpmr.inwdDate, "
	 * ); } else {lSBQuery.append(
	 * " '',tprh.headCode,'',tprh.caseOwner,tpmr.remAmnt,tpmr.inwdNo,tpmr.inwdDate, "
	 * ); }lSBQuery.append(
	 * " tpmr.patientName,tpmr.relationship,tpmr.age,tpmr.hospitalName,tpmr.fromMonth,tpmr.toMonth, "
	 * +
	 * " tpmr.deductionAmnt,tpmr.permissibleAmnt,tpmr.grantAmnt,tpmr.reason,tpmr.objRemarks,tpmr.indoorPatient "
	 * ); if((Scheme.equals("IRLA")) || (Scheme.equals("PSB"))) {
	 * lSBQuery.append(
	 * " FROM TrnPensionRqstHdr tprh,MstPensionerHdr mph,MstPensionerDtls mpd,RltBankBranch rbb,TrnPensionMedRembrsmnt tpmr "
	 * ); } else {lSBQuery.append(
	 * " FROM TrnPensionRqstHdr tprh,MstPensionerHdr mph,TrnPensionMedRembrsmnt tpmr "
	 * ); }lSBQuery.append(
	 * " WHERE tprh.pensionerCode = mph.pensionerCode AND tprh.ppoNo = :ppoNo AND tprh.caseStatus = :caseStatus and tprh.ppoNo = tpmr.ppoNo "
	 * +
	 * " AND tprh.caseStatus = mph.caseStatus AND tprh.locationCode = :LocationCode "
	 * ); if((Scheme.equals("IRLA")) || (Scheme.equals("PSB"))) {
	 * lSBQuery.append(" AND mpd.pensionerCode = mph.pensionerCode " +
	 * " AND rbb.branchCode = mpd.branchCode and rbb.locationCode =:LocationCode "
	 * ); } lSBQuery.append(" AND tpmr.medRembrsmntId = :medRembrsmntId ");
	 * 
	 * lQuery = ghibSession.createQuery(lSBQuery.toString());
	 * 
	 * lQuery.setParameter("ppoNo",lStrPPONO);
	 * lQuery.setParameter("caseStatus",lStrCaseStatus);
	 * //lQuery.setParameter("locationCode",gStrLocationCode);
	 * lQuery.setParameter("medRembrsmntId",Long.parseLong(lStrMedRemId));
	 * lQuery.setString("LocationCode",gStrLocationCode); lLstResLst =
	 * (List)lQuery.list(); } catch(Exception e) {
	 * gLogger.error("Error is : "+e,e); throw e; } return lLstResLst; }
	 */

	public String getDesigName(long lStrDesignation) throws Exception {

		String lStrDesigName = null;

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(200);

		Query lQuery = null;
		try {
			Session hiSession = getSession();
			lSBQuery.append(" SELECT dsgnName  FROM OrgDesignationMst odm WHERE odm.dsgnId = :lStrDesignation");

			lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("lStrDesignation", lStrDesignation);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrDesigName = lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrDesigName;

	}

	public String getScheme(String lStrPPONo, String lStrLocCode) throws Exception {

		String lStrScheme = null;

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(100);

		Query lQuery = null;

		try {
			Session hiSession = getSession();
			lSBQuery.append(" SELECT schemeType FROM TrnPensionRqstHdr WHERE ppoNo = :ppoNo AND locationCode = :locationCode ");

			lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("ppoNo", lStrPPONo);
			lQuery.setParameter("locationCode", lStrLocCode);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrScheme = lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrScheme;
	}

	public List getITRelatedInfo(String lStrPPONO, String lStrCaseStatus, String lStrLocationCode, String lStrLangId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		List lLstResLst = new ArrayList();
		Session ghibSession = getSession();
		try {
			lSBQuery.append(" SELECT MPH.pnsnrPrefix,MPH.firstName,MPH.middleName,MPH.lastName,MPH.gender, " + " MPH.pensnerAddr,MPH.teleNo,ST.stateName,DT.districtName, "
					+ " MPH.dateOfBirth,MPH.panNo,RBB.branchName " + " FROM TrnPensionRqstHdr TPRH,MstPensionerHdr    MPH, " + " MstPensionerDtls MPD,RltBankBranch RBB, "
					+ " CmnStateMst ST,CmnDistrictMst DT " + " WHERE TPRH.pensionerCode = MPH.pensionerCode AND " + " TPRH.ppoNo = :ppoNo AND TPRH.caseStatus = :caseStatus AND "
					+ " TPRH.caseStatus = MPH.caseStatus AND " + " MPD.pensionerCode = MPH.pensionerCode AND MPD.activeFlag = 'Y' and "
					+ " RBB.bankCode = MPD.bankCode AND RBB.branchCode = MPD.branchCode AND " + " TPRH.locationCode = :locationCode AND MPH.stateCode = ST.stateCode AND "
					+ " ST.stateId = DT.cmnStateMst.stateId " +
					// --lSBQuery.append(" AND mph.DISTRICT_CODE = dt.DISTRICT_CODE ");
					" AND DT.cmnLanguageMst.langId = :langId AND ST.cmnLanguageMst.langId = :langId AND " + " TPRH.locationCode = RBB.locationCode ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("ppoNo", lStrPPONO);
			lQuery.setParameter("caseStatus", lStrCaseStatus);
			lQuery.setString("locationCode", lStrLocationCode);
			lQuery.setParameter("langId", Long.parseLong(lStrLangId));
			lLstResLst = lQuery.list();
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstResLst;
	}

	public BigDecimal getITGrossIncome(String lStrPPONO, int lIntFinYr, String locCode) throws Exception {

		List resultList;
		StringBuffer lSBQuery = new StringBuffer(3000);
		Query lQuery = null;
		BigDecimal lBDGross = new BigDecimal(0);
		int assYear = lIntFinYr + 1;

		String from = lIntFinYr + "04";
		String to = assYear + "03";
		try {
			Session hiSession = getSession();
			lSBQuery.append(" SELECT sum(dt.reducedPension)+ SUM(dt.incomeTaxCutAmount) " + " FROM TrnPensionBillDtls dt , TrnPensionBillHdr hd "
					+ " WHERE dt.trnPensionBillHdrId = hd.trnPensionBillHdrId " + " AND dt.ppoNo = :ppoNo " + " AND hd.forMonth BETWEEN :fromDate AND :toDate and hd.locationCode=:locCode ");

			lQuery = hiSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ppoNo", lStrPPONO);
			lQuery.setParameter("fromDate", Integer.parseInt(from));
			lQuery.setParameter("toDate", Integer.parseInt(to));
			lQuery.setParameter("locCode", locCode);
			resultList = lQuery.list();

			if (!resultList.isEmpty()) {
				if (resultList.get(0) != null) {
					lBDGross = new BigDecimal(resultList.get(0).toString());

				}
				// System.out.println("hi" + lBDGross);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lBDGross;
	}

	public String getFamilyAliveInfo(String lStrPnsnrCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		String lStrFamilyName = null;
		Session ghibSession = getSession();
		List resultList = null;

		try {
			lSBQuery.append(" SELECT mpf.name FROM MstPensionerFamilyDtls mpf WHERE mpf.pensionerCode  = '" + lStrPnsnrCode + "' " + " AND mpf.dateOfDeath IS NULL AND mpf.percentage = 100 ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			resultList = lQuery.list();

			if (!resultList.isEmpty()) {
				lStrFamilyName = resultList.get(0).toString();
			}

		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw e;
		}
		return lStrFamilyName;
	}

	public String getApprovedScheme(String lStrPPONo, String lStrLocCode) throws Exception {

		String lStrScheme = null;

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(100);

		Query lQuery = null;

		try {
			Session hiSession = getSession();
			lSBQuery.append(" SELECT schemeType FROM TrnPensionRqstHdr WHERE ppoNo = :ppoNo AND caseStatus = 'APPROVED' AND locationCode = :locationCode ");

			lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("ppoNo", lStrPPONo);
			lQuery.setParameter("locationCode", lStrLocCode);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrScheme = lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrScheme;
	}

	public void updateDeathDateByPensinoerCodeAndStatus(String lStrPensinoerCode, Date DeathDate, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" update MstPensionerHdr set dateOfDeath=:DeathDate,aliveFlag='N',updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date where pensionerCode=:pensionerCode and caseStatus in ('NEW','APPROVED','REJECT') ");
			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("pensionerCode", lStrPensinoerCode);
			lQuery.setDate("DeathDate", DeathDate);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public void updateFMDeathDateByPensinoerCodeAndStatus(String lStrPensinoerCode, Date DeathDate, String lStrIsUpdate, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" update MstPensionerFamilyDtls set dateOfDeath=:DeathDate,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date where pensionerCode=:pensionerCode and percentage=100  ");
			if (lStrIsUpdate.equalsIgnoreCase("N")) {
				lSBQuery.append(" and dateOfDeath is null ");
			}

			Query lQuery = hibSession.createQuery(lSBQuery.toString());
			lQuery.setString("pensionerCode", lStrPensinoerCode);
			lQuery.setDate("DeathDate", DeathDate);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
	}

	public String getPensionerName(String lStrPnsnCode) throws Exception {

		String lStrPenName = null;

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(100);

		Query lQuery = null;

		try {
			Session hiSession = getSession();
			lSBQuery.append(" SELECT firstName FROM MstPensionerHdr WHERE pensionerCode = :pensionerCode ");

			lQuery = hiSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("pensionerCode", lStrPnsnCode);

			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrPenName = lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrPenName;
	}
}
