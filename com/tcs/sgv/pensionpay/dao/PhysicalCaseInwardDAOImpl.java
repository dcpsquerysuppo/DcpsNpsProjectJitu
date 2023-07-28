/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Apr 20, 2011		Anjana Suvariya								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.common.valueobject.RltBankBranch;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pension.valueobject.TrnPrvosionalPensionDtls;
import com.tcs.sgv.pensionpay.valueobject.HstCommutationDtls;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntDcrgDtls;
import com.tcs.sgv.pensionpay.valueobject.HstReEmploymentDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerFamilyDtls;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerHdr;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerNomineeDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnCvpRestorationDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionArrearDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCorrectionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCutDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerRivisionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalPensionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnProvisionalVoucherDtls;


/**
 * Class Description -
 * 
 * 
 * @author Anjana Suvariya
 * @version 0.1
 * @since JDK 5.0 Apr 20, 2011
 */
public class PhysicalCaseInwardDAOImpl extends GenericDaoHibernateImpl implements PhysicalCaseInwardDAO {

	private Session ghibSession = null;
	private static final Logger gLogger = Logger.getLogger(PhysicalCaseInwardDAOImpl.class);

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public PhysicalCaseInwardDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
		ghibSession = sessionFactory.getCurrentSession();
	}
	public PhysicalCaseInwardDAOImpl(Class type,Session ghibSession) {
		super(type);
		this.ghibSession = ghibSession;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getAllHeadCode()
	 */
	public List<ComboValuesVO> getAllHeadCode() throws Exception {

		List<ComboValuesVO> lLstHeadCode = new ArrayList<ComboValuesVO>();
		Iterator itr;
		ComboValuesVO lObjComboValueVO;
		Object[] obj;
		List lLstResult = null;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT headCode,series FROM MstPensionHeadcode order by headCode");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				itr = lLstResult.iterator();
				while (itr.hasNext()) {
					lObjComboValueVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					lObjComboValueVO.setId(obj[0].toString());
					lObjComboValueVO.setDesc(obj[1].toString());
					lLstHeadCode.add(lObjComboValueVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstHeadCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getHeadCodeDesc(java
	 * .lang.String, java.lang.Long)
	 */
	public String getHeadCodeDesc(String lStrHeadCode, Long langId) throws Exception {

		String lStrHeadCodeDesc = null;
		List lLstResult = null;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT description FROM MstPensionHeadcode  WHERE headCode =:headCode and langId = :langID");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setString("headCode", lStrHeadCode);
			hqlQuery.setString("langID", String.valueOf(langId));

			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				lStrHeadCodeDesc = lLstResult.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrHeadCodeDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getRltPensionHeadcodeRate
	 * (java.lang.String, java.lang.String)
	 */
	public BigDecimal getRltPensionHeadcodeRate(String lStrStateCode, String lStrTypeFlag) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstResult = new ArrayList();
		BigDecimal lBgDcmlRate = BigDecimal.ZERO;
		try {
			lSBQuery.append(" Select RPR.rate FROM RltPensionHeadcodeRate RPR WHERE RPR.headCode =:HeadCode AND RPR.effectiveToDate is null ");
			if (lStrTypeFlag != null) {
				lSBQuery.append(" AND RPR.fieldType =:FieldType");
			}

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			hqlQuery.setBigDecimal("HeadCode", new BigDecimal(lStrStateCode));
			if (lStrTypeFlag != null) {
				hqlQuery.setString("FieldType", lStrTypeFlag);
			}

			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				if (lLstResult.get(0) != null) {
					lBgDcmlRate = new BigDecimal(lLstResult.get(0).toString());
				}
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lBgDcmlRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#isValidPPONo(java.lang
	 * .String, java.lang.Long, java.lang.String)
	 */
	public List<String> isValidPPONo(String lStrPPONo,List<String> lLstLocCode) throws Exception {

		// TODO Auto-generated method stub
		List<String> lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer();

		try {
			lSBQuery.append(" SELECT tpr.ppo_no, clm.loc_Name,mph.first_Name, \n");
			lSBQuery.append("(case when mpd.bank_code is not null then (select mb.bank_name  from mst_bank mb where mpd.bank_code=mb.bank_code) else '' end), \n");
			lSBQuery.append("(case when mpd.branch_code is not null then (select rbb.branch_name  from rlt_bank_branch rbb where mpd.branch_code=rbb.branch_code) else '' end) \n");
			lSBQuery.append(" FROM Mst_Pensioner_Hdr mph, Mst_Pensioner_Dtls mpd, Trn_Pension_Rqst_Hdr tpr, Cmn_Location_Mst clm \n");
			lSBQuery.append(" WHERE mph.pensioner_Code = mpd.pensioner_Code and mph.pensioner_Code = tpr.pensioner_Code \n");
			lSBQuery.append(" and tpr.location_code=clm.loc_Id AND tpr.ppo_No=:PPONo and tpr.location_code in (:locCodeList)");
			Query sqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			sqlQuery.setString("PPONo", lStrPPONo.toUpperCase());
			sqlQuery.setParameterList("locCodeList", lLstLocCode);

			lLstResult = sqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstResult;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getIfscCodeFromBranchCode
	 * (java.lang.String, java.lang.String)
	 */
	public String getIfscCodeFromBranchCode(Long lLngBranchCode, String lStrLocationCode) throws Exception {

		String lStrIfscCode = "";
		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			lSBQuery.append("SELECT rlt.micrCode  FROM  RltBankBranch rlt WHERE rlt.branchCode=:branchCode AND rlt.locationCode=:locationCode");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("branchCode", lLngBranchCode);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				if (lLstResult.get(0) != null) {
					lStrIfscCode = lLstResult.get(0).toString();
				}
			}

		} catch (Exception e) {
			logger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lStrIfscCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getTreasuryName(java
	 * .lang.String, java.lang.String)
	 */
	public String getTreasuryName(Long lLngLangId, Long lLngLocationCode) throws Exception {

		String lStrTreasuryName = null;
		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			lSBQuery.append("SELECT locName FROM CmnLocationMst WHERE  cmnLanguageMst.langId = :langid AND locId = :locationCode");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("langid", lLngLangId);
			hqlQuery.setParameter("locationCode", lLngLocationCode);
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				lStrTreasuryName = lLstResult.get(0).toString();
			}

		} catch (Exception e) {
			logger.error("Exception::" + e.getMessage(), e);
			throw (e);
		}
		return lStrTreasuryName;
	}

	public TrnPensionRqstHdr getTrnPensionRqstHdrVO(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<TrnPensionRqstHdr> lLstTrnPensionRqstHdr = new ArrayList<TrnPensionRqstHdr>();
		TrnPensionRqstHdr lObjTrnPensionRqstHdrVO = new TrnPensionRqstHdr();
		try {
			//Session hibSession = getSession();
			objCrt = ghibSession.createCriteria(TrnPensionRqstHdr.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			lLstTrnPensionRqstHdr = objCrt.list();
			if (lLstTrnPensionRqstHdr != null && !lLstTrnPensionRqstHdr.isEmpty()) {
				lObjTrnPensionRqstHdrVO = lLstTrnPensionRqstHdr.get(0);
			}
		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : getTrnPensionRqstHdrVO() : Error is :" + e, e);
			throw e;

		}
		return lObjTrnPensionRqstHdrVO;
	}

	public MstPensionerHdr getMstPensionerHdrVO(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<MstPensionerHdr> lLstMstPensionerHdr = new ArrayList<MstPensionerHdr>();
		MstPensionerHdr lObjMstPensionerHdrVO = new MstPensionerHdr();
		try {
			//Session hibSession = getSession();
			objCrt = ghibSession.createCriteria(MstPensionerHdr.class);
			objCrt.add(Restrictions.eq("pensionerId", Long.parseLong(lStrPensionerCode)));
			lLstMstPensionerHdr = objCrt.list();
			if (lLstMstPensionerHdr != null && !lLstMstPensionerHdr.isEmpty()) {
				lObjMstPensionerHdrVO = lLstMstPensionerHdr.get(0);
			}
		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : getMstPensionerHdrVO() : Error is :" + e, e);
			throw e;
		}
		return lObjMstPensionerHdrVO;
	}

	public MstPensionerDtls getMstPensionerDtlsVO(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<MstPensionerDtls> lLstMstPensionerDtls = new ArrayList<MstPensionerDtls>();
		MstPensionerDtls lObjMstPensionerDtlsVO = new MstPensionerDtls();
		try {
			//Session hibSession = getSession();
			objCrt = ghibSession.createCriteria(MstPensionerDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			lLstMstPensionerDtls = objCrt.list();
			if (lLstMstPensionerDtls != null && !lLstMstPensionerDtls.isEmpty()) {
				lObjMstPensionerDtlsVO = lLstMstPensionerDtls.get(0);
			}
		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : getMstPensionerDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lObjMstPensionerDtlsVO;
	}

	public List<MstPensionerFamilyDtls> getMstPensionerFamilyDtlsVO(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<MstPensionerFamilyDtls> lLstMstPensionerFamilyDtls = new ArrayList<MstPensionerFamilyDtls>();
		try {
			//Session hibSession = getSession();
			objCrt = ghibSession.createCriteria(MstPensionerFamilyDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			lLstMstPensionerFamilyDtls = objCrt.list();

		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : getMstPensionerFamilyDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lLstMstPensionerFamilyDtls;
	}

	public List<MstPensionerNomineeDtls> getMstPensionerNomineeDtlsVO(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub

		List<MstPensionerNomineeDtls> lLstMstPensionerNomineeDtls = new ArrayList<MstPensionerNomineeDtls>();
		try {
			Query lQuery = null;
			StringBuilder lStrQuery = new StringBuilder();

			lStrQuery.append("SELECT mpn.nominee_Id,mpn.name,mpn.percent,mpn.bank_Code,mpn.branch_Code,mpn.account_No,rlt.branch_Name,mpn.relationship,mpn.date_of_birth "
					+ " FROM Mst_Pensioner_Nominee_Dtls mpn LEFT OUTER JOIN Rlt_Bank_Branch rlt ON mpn.branch_Code=rlt.branch_Code" + " WHERE  mpn.pensioner_Code=:pensionerCode");
			lQuery = ghibSession.createSQLQuery(lStrQuery.toString());
			lQuery.setParameter("pensionerCode", lStrPensionerCode);

			lLstMstPensionerNomineeDtls = lQuery.list();

		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : getMstPensionerNomineeDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lLstMstPensionerNomineeDtls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getMstPensionerNomDtlsVO
	 * (java.lang.String)
	 */
	public List<MstPensionerNomineeDtls> getMstPensionerNomDtlsVO(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<MstPensionerNomineeDtls> lLstMstPensionerNomineeDtls = new ArrayList<MstPensionerNomineeDtls>();
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(MstPensionerNomineeDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			lLstMstPensionerNomineeDtls = objCrt.list();

		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : getMstPensionerFamilyDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lLstMstPensionerNomineeDtls;
	}

	public TrnPensionerRivisionDtls getTrnPensionerRivisionDtlsVO(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<TrnPensionerRivisionDtls> lLstTrnPensionerRivisionDtls = new ArrayList<TrnPensionerRivisionDtls>();
		TrnPensionerRivisionDtls lObjTrnPensionerRivisionDtlsVO = new TrnPensionerRivisionDtls();
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionerRivisionDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			lLstTrnPensionerRivisionDtls = objCrt.list();
			if (lLstTrnPensionerRivisionDtls != null && !lLstTrnPensionerRivisionDtls.isEmpty()) {
				lObjTrnPensionerRivisionDtlsVO = lLstTrnPensionerRivisionDtls.get(0);
			}

		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : getTrnPensionerRivisionDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lObjTrnPensionerRivisionDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#updatePensionCaseStatus
	 * (java.lang.String)
	 */
	public void updatePensionCaseStatus(String lStrPensionerCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hiSession = getSession();
		try {
			lSBQuery.append("UPDATE TrnPensionRqstHdr SET caseStatus = :status WHERE pensionerCode = :pensionerCode) ");

			Query hqlQuery = hiSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("status", gObjRsrcBndle.getString("STATFLG.MODIFIEDBYAUDITOR"));
			hqlQuery.setString("pensionerCode", lStrPensionerCode);

			hqlQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#
	 * getTrnProvisionalPensionDtlsVO(java.lang.String)
	 */
	public List<TrnProvisionalPensionDtls> getTrnProvisionalPensionDtlsVO(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<TrnProvisionalPensionDtls> lLstTrnProvisionalPensionDtls = new ArrayList<TrnProvisionalPensionDtls>();

		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnProvisionalPensionDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			lLstTrnProvisionalPensionDtls = objCrt.list();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : getTrnPensionerRivisionDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lLstTrnProvisionalPensionDtls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getTrnPensionRecoveryDtlsVO
	 * (java.lang.String)
	 */
	public List getTrnPensionRecoveryDtlsVO(String lStrPensionerCode, List<String> lLstRecoveryFrom) throws Exception {

		List<List> lLstPensionRecoveryDtls = new ArrayList<List>();
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
		List<TrnPensionRecoveryDtls> lLstAdvanceDtls = new ArrayList<TrnPensionRecoveryDtls>();
		List<TrnPensionRecoveryDtls> lLstRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
		List<TrnPensionRecoveryDtls> lLstAssessedDuesDtls = new ArrayList<TrnPensionRecoveryDtls>();
		List<TrnPensionRecoveryDtls> lLstDCRGRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
		TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
		String lStrRecoveryType = null;
		try {
			Session hibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select new com.tcs.sgv.pensionpay.valueobject.TrnPensionRecoveryDtls(tpr.trnPensionRecoveryDtlsId,tpr.pensionerCode, ");
			lSBQuery.append(" tpr.recoveryFromFlag, tpr.recoveryType,tpr.mjrhdCode,tpr.totalRecoveryAmt,tpr.amount, tpr.fromMonth,tpr.toMonth, tpr.billNo,tpr.noOfInstallments, tpr.nature, tpr.schemeCode,tpr.dcrgDtlsId,ms.schemeName) ");
			lSBQuery.append(" FROM TrnPensionRecoveryDtls tpr,MstScheme ms WHERE tpr.schemeCode = ms.schemeCode and tpr.pensionerCode = :pensionerCode \n");
			lSBQuery.append(" and tpr.recoveryFromFlag IN(:recoveryFromList)");

			Query hqlQuery = hibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("pensionerCode", lStrPensionerCode);
			hqlQuery.setParameterList("recoveryFromList", lLstRecoveryFrom);
			lLstTrnPensionRecoveryDtls = hqlQuery.list();

			if (lLstTrnPensionRecoveryDtls != null && !lLstTrnPensionRecoveryDtls.isEmpty()) {
				for (Integer lIntCnt = 0; lIntCnt < lLstTrnPensionRecoveryDtls.size(); lIntCnt++) {
					lObjTrnPensionRecoveryDtlsVO = new TrnPensionRecoveryDtls();
					lObjTrnPensionRecoveryDtlsVO = lLstTrnPensionRecoveryDtls.get(lIntCnt);
					lStrRecoveryType = lObjTrnPensionRecoveryDtlsVO.getRecoveryType();
					if (gObjRsrcBndle.getString("PPMT.DCRGRECOVERY").equalsIgnoreCase(lStrRecoveryType) && lObjTrnPensionRecoveryDtlsVO.getDcrgDtlsId() == null) {
						lLstDCRGRecoveryDtls.add(lObjTrnPensionRecoveryDtlsVO);
					}
					else
					{
						lLstAdvanceDtls.add(lObjTrnPensionRecoveryDtlsVO);
					}
//					if (gObjRsrcBndle.getString("PPMT.HBA").equalsIgnoreCase(lStrRecoveryType) || gObjRsrcBndle.getString("PPMT.MCA").equalsIgnoreCase(lStrRecoveryType)
//							|| gObjRsrcBndle.getString("PPMT.OTHER").equalsIgnoreCase(lStrRecoveryType)|| gObjRsrcBndle.getString("PPMT.ADVANCE").equalsIgnoreCase(lStrRecoveryType)) {
//						lLstAdvanceDtls.add(lObjTrnPensionRecoveryDtlsVO);
//					} else if (gObjRsrcBndle.getString("PPMT.OVERPAY").equalsIgnoreCase(lStrRecoveryType) || gObjRsrcBndle.getString("PPMT.ARREAR").equalsIgnoreCase(lStrRecoveryType)
//							|| gObjRsrcBndle.getString("PPMT.LICENSEFEE").equalsIgnoreCase(lStrRecoveryType)) {
//						lLstRecoveryDtls.add(lObjTrnPensionRecoveryDtlsVO);
//
//					} else if (gObjRsrcBndle.getString("PPMT.ASSESSEDDUES").equalsIgnoreCase(lStrRecoveryType)) {
//						lLstAssessedDuesDtls.add(lObjTrnPensionRecoveryDtlsVO);
//					} 
				}
			}
			lLstPensionRecoveryDtls.add(lLstAdvanceDtls);
//			lLstPensionRecoveryDtls.add(lLstRecoveryDtls);
//			lLstPensionRecoveryDtls.add(lLstAssessedDuesDtls);
			lLstPensionRecoveryDtls.add(lLstDCRGRecoveryDtls);

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : getTrnPensionRecoveryDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lLstPensionRecoveryDtls;
	}

	
	public List<TrnPensionRecoveryDtls> getTrnPensionRecoveryDtlsForDelete(String lStrPensionerCode,List<String> lLstRecoveryFrom) throws Exception {
		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtls = new ArrayList<TrnPensionRecoveryDtls>();
		
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionRecoveryDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			objCrt.add(Restrictions.in("recoveryFromFlag", lLstRecoveryFrom));
			lLstTrnPensionRecoveryDtls = objCrt.list();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : getTrnPensionRecoveryDtlsForDelete() : Error is :" + e, e);
			throw e;
		}
		return lLstTrnPensionRecoveryDtls;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#
	 * getMstTrnProvisionalVoucherDtlsVO(java.lang.String)
	 */
	public List<TrnProvisionalVoucherDtls> getTrnProvisionalVoucherDtlsVO(String lStrPensionerCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<TrnProvisionalVoucherDtls> lLstTrnProvisionalVoucherDtls = new ArrayList<TrnProvisionalVoucherDtls>();
		new TrnProvisionalVoucherDtls();
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnProvisionalVoucherDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			lLstTrnProvisionalVoucherDtls = objCrt.list();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : getTrnProvisionalVoucherDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lLstTrnProvisionalVoucherDtls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#
	 * getRecoveryDtlsFromRecoveryType(java.lang.String, java.lang.String)
	 */
	public TrnPensionRecoveryDtls getRecoveryDtlsFromRecoveryType(String lStrPensionerCode, String lStrRecoveryType) throws Exception {

		Session hibSession = getSession();
		StringBuffer lSBQuery = new StringBuffer();
		List lLstResult;
		TrnPensionRecoveryDtls lObjTrnPensionRecoveryDtlsVO = null;
		try {
			lSBQuery.append(" FROM TrnPensionRecoveryDtls WHERE pensionerCode=:pensionerCode and recoveryType=:recoveryType ");

			Query hqlQuery = hibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("pensionerCode", lStrPensionerCode);
			hqlQuery.setString("recoveryType", lStrRecoveryType);
			lLstResult = hqlQuery.list();
			if (!lLstResult.isEmpty()) {
				lObjTrnPensionRecoveryDtlsVO = (TrnPensionRecoveryDtls) lLstResult.get(0);
			}
		} catch (Exception e) {
			gLogger.error("error" + e, e);
			throw e;
		}
		return lObjTrnPensionRecoveryDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getTrnCvpRestorationDtlsVO
	 * (java.lang.String)
	 */
	public List<TrnCvpRestorationDtls> getTrnCvpRestorationDtlsVO(String lStrPensionerCode) throws Exception {

		Criteria objCrt = null;
		List<TrnCvpRestorationDtls> lLstTrnCvpRestorationDtls = new ArrayList<TrnCvpRestorationDtls>();
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnCvpRestorationDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			objCrt.addOrder(Order.asc("fromDate"));
			lLstTrnCvpRestorationDtls = objCrt.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstTrnCvpRestorationDtls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getHstReEmploymentDtlsVO
	 * (java.lang.String)
	 */
	public List<HstReEmploymentDtls> getHstReEmploymentDtlsVO(String lStrPensionerCode) throws Exception {

		Criteria objCrt = null;
		List<HstReEmploymentDtls> lLstHstReEmploymentDtlsVO = new ArrayList<HstReEmploymentDtls>();
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(HstReEmploymentDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			objCrt.addOrder(Order.asc("fromDate"));
			lLstHstReEmploymentDtlsVO = objCrt.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstHstReEmploymentDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getHstCommutationDtlsVO
	 * (java.lang.String)
	 */
	public List getHstCommutationDtlsVO(String lStrPensionerCode) throws Exception {

		List lLstHstCommutationDtlsVO = new ArrayList();
		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session hibSession = getSession();
			lSBQuery.append("select hcd.cvp_dtls_id,hcd.order_no,hcd.order_date,hcd.payment_amount,hcd.name_of_payee, \n");
			lSBQuery.append(" clm.loc_name,hcd.voucher_no,hcd.voucher_date,hcd.bill_no,hcd.cvp_authority,hcd.cvp_start_date,cvp_end_date, \n");
			lSBQuery.append(" basic_pension_amt,cvp_monthly_amt,reduced_pension_amt \n");
			lSBQuery.append(" from hst_commutation_dtls hcd left outer join cmn_location_mst clm on hcd.trsry_for_payment = clm.loc_id where hcd.pensioner_code = :pensionerCode ");
			
			Query hqlQuery = hibSession.createSQLQuery(lSBQuery.toString());
			hqlQuery.setString("pensionerCode", lStrPensionerCode);
					
			lLstHstCommutationDtlsVO =  hqlQuery.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstHstCommutationDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getCorrectedValuesByppoNo
	 * (java.lang.String, java.lang.String)
	 */
	public List<TrnPensionCorrectionDtls> getCorrectedValuesByPensionerCode(String lStrPensionerCode, String lStrLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hibSession = getSession();
		List<TrnPensionCorrectionDtls> lLstResult = new ArrayList<TrnPensionCorrectionDtls>();

		try {
			lSBQuery.append("SELECT new com.tcs.sgv.pensionpay.valueobject.TrnPensionCorrectionDtls(tcd.fieldType,tcd.crntFieldValue,tcd.prvsFieldValue)  "
					+ " FROM TrnPensionCorrectionDtls tcd,TrnPensionRqstHdr trh WHERE tcd.pensionerCode = trh.pensionerCode AND  "
					+ " tcd.approvalStatus IS NULL AND tcd.pensionerCode=:pensionerCode  AND trh.locationCode=:locationCode ");

			Query hqlQuery = hibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("pensionerCode", lStrPensionerCode);
			hqlQuery.setString("locationCode", lStrLocCode);

			lLstResult = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#
	 * updateCorrectionDtlsByPensionerCode(java.lang.Long, java.lang.String,
	 * java.lang.Long, java.lang.Long, java.util.Date)
	 */
	public void updateCorrectionDtlsByPensionerCode(List<Long> lLstPensionerCode, String lStrApproveStatus, Long gLngPostId, Long gLngUserId, Date gDate) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Session hiSession = getSession();
		try {
			lSBQuery.append("UPDATE TrnPensionCorrectionDtls SET approvalStatus =:approvalStatus,approvedDate =:approvedDate,approvedPostId=:approvedPostId,approvedUserId = :approvedUserId WHERE "
					+ " approvalStatus IS NULL AND pensionerCode IN(:pensionerCodeList) ");

			Query hqlQuery = hiSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameterList("pensionerCodeList", lLstPensionerCode);
			hqlQuery.setLong("approvedPostId", gLngPostId);
			hqlQuery.setLong("approvedUserId", gLngUserId);
			hqlQuery.setDate("approvedDate", gDate);
			hqlQuery.setString("approvalStatus", lStrApproveStatus);
			hqlQuery.executeUpdate();
		} catch (Exception e) {
			logger.error("Error is " + e, e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getAllTreasury(java.
	 * lang.L ong)
	 */
	public List<ComboValuesVO> getAllTreasury(List<Long> lLstDepartmentId, Long lLngLangId) throws Exception {

		List<ComboValuesVO> lLstTreasury = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		List lLstResultList;
		try {
			lStrQuery.append(" Select locId,locName ");
			lStrQuery.append(" FROM CmnLocationMst ");
			lStrQuery.append(" WHERE departmentId IN (:lLstDepartmentId)");
			lStrQuery.append(" AND cmnLanguageMst.langId =:langId");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setParameterList("lLstDepartmentId", lLstDepartmentId);
			hqlQuery.setLong("langId", lLngLangId);
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResultList = hqlQuery.list();
			if (lLstResultList != null && lLstResultList.size() > 0) {
				Iterator itr = lLstResultList.iterator();

				ComboValuesVO cmbVO = null;
				Object[] obj = null;
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					lLstTreasury.add(cmbVO);
				}
			}
			return lLstTreasury;
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getPnsnrDtlsFromPnsnrCode
	 * (java.lang.String)
	 */
	public List getPnsnrDtlsFromPnsnrCode(String lStrPensionerCode, String lStrLocationCode, Long lLngLangId) throws Exception {

		List lLstPensionerDetails = new ArrayList();

		Query lSqlQuery = null;
		try {

			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();

			lStrQuery.append("SELECT mph.pensioner_code,prh.ppo_no, mph.first_name, prh.ledger_no, prh.page_no, mph.appln_no, \n");
			lStrQuery.append("  mph.pnsr_file_no, mph.guardian_name, mb.bank_name, rlt.branch_name, prh.pension_type,mpd.acount_no, \n");
			lStrQuery.append(" mph.designation, mph.date_of_join, mph.date_of_retirement, mph.date_of_birth, mpd.IDENTIFICATION_DATE,  \n");
			lStrQuery.append(" mph.identity_mark, mph.pnsnr_addr1, mph.pnsnr_addr2,mph.pnsnr_addr_town,mph.pin_code, \n");
			lStrQuery.append(" csm.state_name,cdm.district_name, mph.pan_no, mph.moblile_no, mph.tele_no, mph.pnsnr_email_id, \n");
			lStrQuery.append(" prh.basic_pension_amount , prh.commencement_date, prh.total_cvp_amount, prh.dcrg_order_no, tpd.gratuity_amount, \n");
			lStrQuery.append(" pfd.name, prh.fp1_amount, prh.fp1_date, prh.fp2_amount, prh.fp2_date, \n");
			lStrQuery.append("  prh.org_Bf_1_11_36,prh.org_Af_1_11_36,prh.org_Af_1_11_56, prh.org_Af_1_05_60, prh.ORG_ZP_AF, \n ");
			lStrQuery.append(" mph.pensioner_photo_id,mph.pensioner_sign_id, prh.reduced_pension, mph.employment_office, prh.case_received_from  \n");
			lStrQuery.append(" FROM TRN_PENSION_RQST_HDR prh  \n");
			lStrQuery.append(" JOIN MST_PENSIONER_HDR mph ON prh.pensioner_code=mph.pensioner_code  \n");
			lStrQuery.append(" JOIN MST_PENSIONER_DTLS mpd ON mph.pensioner_code=mpd.pensioner_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN mst_pensioner_family_dtls pfd ON pfd.pensioner_code = mph.pensioner_code AND pfd.percentage = :lPercent \n");
			lStrQuery.append(" LEFT OUTER JOIN trn_prvosional_pension_dtls tpd ON tpd.pensioner_code = mph.pensioner_code   \n");
			lStrQuery.append(" LEFT OUTER JOIN MST_BANK mb ON mb.bank_code = mpd.bank_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN rlt_bank_branch rlt ON mpd.branch_code = rlt.branch_code  \n");
			lStrQuery.append(" LEFT OUTER JOIN cmn_state_mst csm ON mph.state_code=csm.state_code and csm.lang_id = :langId \n");
			lStrQuery.append(" LEFT OUTER JOIN cmn_district_mst cdm ON mph.district_code=cdm.district_code and cdm.lang_id = :langId \n");
			lStrQuery.append(" WHERE prh.status= :status AND prh.location_code= :locationCode AND mph.pensioner_code= :pensionerCode  ");

			lSqlQuery = hibSession.createSQLQuery(lStrQuery.toString());

			lSqlQuery.setString("status", gObjRsrcBndle.getString("STATUS.CONTINUE"));
			lSqlQuery.setString("pensionerCode", lStrPensionerCode);
			lSqlQuery.setString("locationCode", lStrLocationCode);
			lSqlQuery.setLong("langId", lLngLangId);
			lSqlQuery.setLong("lPercent", 100);

			lLstPensionerDetails = lSqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is :" + e, e);
			throw e;
		}
		return lLstPensionerDetails;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#isValidSchemeCode(java
	 * .lang.String)
	 */
	public List<String> isValidSchemeCode(String lStrSchemeCode,String lStrSchemeType) throws Exception {

		// TODO Auto-generated method stub
		List<String> lLstResult = new ArrayList<String>();
		StringBuffer lSBQuery = new StringBuffer();

		try {
			lSBQuery.append(" SELECT schemeCode FROM MstScheme  WHERE schemeType = :schemeType and schemeCode = :schemeCode");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("schemeCode", lStrSchemeCode);
			hqlQuery.setString("schemeType", lStrSchemeType);

			lLstResult = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getBankName(java.lang
	 * .String)
	 */
	public String getBankName(Long lLngBankCode) throws Exception {

		String lStrBankName = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			lSBQuery.append(" SELECT B.bankName FROM MstBank B WHERE B.bankCode = :bankCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("bankCode", lLngBankCode);

			List lLstResult = lQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty() && lLstResult.get(0) != null) {
				lStrBankName = lLstResult.get(0).toString();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrBankName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getBranchName(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	public String getBranchName(Long lLngBranchCode, String lStrLocCode) throws Exception {

		String lStrBranchName = null;
		StringBuffer lSBQuery = new StringBuffer();
		try {
			lSBQuery.append(" SELECT B.branchName FROM RltBankBranch B WHERE B.branchCode = :branchCode ");
			lSBQuery.append(" AND B.locationCode = :lLocCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setParameter("branchCode", lLngBranchCode);
			lQuery.setParameter("lLocCode", lStrLocCode);

			List lLstResult = lQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty() && lLstResult.get(0) != null) {
				lStrBranchName = lLstResult.get(0).toString();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(" Error is : " + e, e);
			throw e;
		}
		return lStrBranchName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getDesignationName(java
	 * .lang.Long, java.lang.Long)
	 */
	public String getDesignationName(Long lLngDesignationId, Long lLngLangId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		String lStrDesignationName = null;
		List<String> lLstResult = new ArrayList<String>();
		try {

			lSBQuery.append(" Select dsgn.dsgnName ");
			lSBQuery.append(" FROM OrgDesignationMst dsgn ");
			lSBQuery.append(" WHERE dsgn.cmnLanguageMst.langId =:langId AND dsgn.dsgnId = :designationId");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setLong("designationId", lLngDesignationId);
			hqlQuery.setLong("langId", lLngLangId);
			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty() && lLstResult.get(0) != null) {
				lStrDesignationName = lLstResult.get(0).toString();
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			//e.printStackTrace();
		}
		return lStrDesignationName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getDepartmentName(java
	 * .lang.Long, java.lang.Long, java.lang.Long)
	 */
	public String getDepartmentName(Long lLngDepartmentId, Long lLngLocId, Long lLngLangId) throws Exception {

		List<String> lLstResult = new ArrayList<String>();
		StringBuilder lSBQuery = new StringBuilder();
		String lStrDepartmentName = null;

		try {

			lSBQuery.append(" SELECT clm.locName FROM CmnLocationMst clm, OrgDepartmentMst odm ");
			lSBQuery.append(" WHERE odm.departmentId=:departmentId  ");
			lSBQuery.append(" AND clm.departmentId=odm.departmentId AND clm.locId = :locationId");
			lSBQuery.append(" AND clm.cmnLanguageMst.langId =:langId ");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setLong("locationId", lLngLocId);
			hqlQuery.setLong("departmentId", lLngDepartmentId);
			hqlQuery.setLong("langId", lLngLangId);

			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty() && lLstResult.get(0) != null) {
				lStrDepartmentName = lLstResult.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrDepartmentName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getStateName(java.lang
	 * .Long, java.lang.Long)
	 */
	public String getStateName(Long lLngStateId, Long lLngLangId) throws Exception {

		String lStrStateName = null;
		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = new ArrayList();

		try {

			lSBQuery.append(" SELECT stateName ");
			lSBQuery.append(" FROM CmnStateMst ");
			lSBQuery.append(" WHERE stateId = :stateId AND cmnLanguageMst.langId =:langId ");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setLong("stateId", lLngStateId);
			hqlQuery.setLong("langId", lLngLangId);

			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty() && lLstResult.get(0) != null) {
				lStrStateName = lLstResult.get(0).toString();
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrStateName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getDistrictName(java
	 * .lang.Long, java.lang.Long)
	 */
	public String getDistrictName(String lStrDistrictCode, Long lLngLangId) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List lLstResult = new ArrayList();
		String lLstDistrictName = null;

		try {
			lSBQuery.append(" Select districtName ");
			lSBQuery.append(" FROM CmnDistrictMst ");
			lSBQuery.append(" WHERE  districtCode = :districtCode");
			lSBQuery.append(" AND cmnLanguageMst.langId = :langId  ");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			hqlQuery.setString("districtCode", lStrDistrictCode);
			hqlQuery.setLong("langId", lLngLangId);

			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty() && lLstResult.get(0) != null) {
				lLstDistrictName = lLstResult.get(0).toString();
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDistrictName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getOfficeListForAutoComplete
	 * (java.lang.String)
	 */
	public List<ComboValuesVO> getOfficeListForAutoComplete(String lStrOfficeName) throws Exception {

		List<ComboValuesVO> lLstOfficeName = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;
		List lLstResult = new ArrayList();
		String lStrOffice = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DISTINCT(employmentOffice) FROM MstPensionerHdr WHERE employmentOffice LIKE '" + lStrOfficeName + "%'");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				Iterator it = lLstResult.iterator();
				while (it.hasNext()) {
					lObjComboValueVO = new ComboValuesVO();
					lStrOffice = (String) it.next();
					lObjComboValueVO.setId(lStrOffice);
					lObjComboValueVO.setDesc(lStrOffice);
					lLstOfficeName.add(lObjComboValueVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstOfficeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getProvisionalPensionDtls
	 * (java.lang.String)
	 */
	public List<TrnPrvosionalPensionDtls> getProvisionalPensionDtls(String lStrPensionerCode) throws Exception {

		Criteria objCrt = null;
		StringBuilder lSBQuery = new StringBuilder();
		List<TrnPrvosionalPensionDtls> lLstTrnPrvosionalPensionDtlsVO = new ArrayList<TrnPrvosionalPensionDtls>();
		try {
			lSBQuery.append("from TrnProvisionalPensionDtls where pensionerCode = :pensionerCode and billType = :billType");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("pensionerCode", lStrPensionerCode);
			hqlQuery.setString("billType", gObjRsrcBndle.getString("PPMT.GRATUITY"));

			lLstTrnPrvosionalPensionDtlsVO = hqlQuery.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstTrnPrvosionalPensionDtlsVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getDcrgHistory(java.
	 * lang.String)
	 */
	public List getDcrgHistory(String lStrPensionerCode) throws Exception {

		List lLstHstPnsnPmntDcrgDtls = new ArrayList();
		StringBuffer lSBQuery = new StringBuffer();
		try {
			Session hibSession = getSession();
			lSBQuery.append("select hpd.dcrg_dtls_id,hpd.order_no,hpd.bill_no,hpd.order_date,hpd.total_order_amount,hpd.paid_amount, \n"); 
			lSBQuery.append(" hpd.name_of_payee,clm.loc_name,hpd.voucher_no,hpd.voucher_date,hpd.payment_authority, \n");
			lSBQuery.append(" hpd.withheld_amount,hpd.total_recovery_amount from \n");
		    lSBQuery.append(" hst_pnsnpmnt_dcrg_dtls hpd left outer join cmn_location_mst clm on hpd.trsry_for_payment = clm.loc_id \n");
			lSBQuery.append(" where pensioner_code = :pensionerCode"); 
		    			
			Query hqlQuery = hibSession.createSQLQuery(lSBQuery.toString());
			hqlQuery.setString("pensionerCode", lStrPensionerCode);
					
			lLstHstPnsnPmntDcrgDtls =  hqlQuery.list();

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstHstPnsnPmntDcrgDtls;
			
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#
	 * getDesignationForAutoComplete(java.lang.String)
	 */
	public List<ComboValuesVO> getDesignationForAutoComplete(String lStrDesignation) throws Exception {

		List<ComboValuesVO> lLstDesignation = new ArrayList<ComboValuesVO>();
		ComboValuesVO lObjComboValueVO = null;
		List lLstResult = new ArrayList();
		String lStrOffice = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT DISTINCT(designation) FROM MstPensionerHdr WHERE designation LIKE '" + lStrDesignation + "%'");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				Iterator it = lLstResult.iterator();
				while (it.hasNext()) {
					lObjComboValueVO = new ComboValuesVO();
					lStrOffice = (String) it.next();
					lObjComboValueVO.setId(lStrOffice);
					lObjComboValueVO.setDesc(lStrOffice);
					lLstDesignation.add(lObjComboValueVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDesignation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getMstSchemeCount(java
	 * .lang.Long)
	 */
	public Integer getMstSchemeCount(Long lLngLangId, String lStrSchemeCode, String lStrSchemeType) throws Exception {

		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		StringBuffer lSBQuery = new StringBuffer();
		Integer lIntCount = 0;

		try {
			lSBQuery.append("SELECT COUNT(schemeId) FROM MstScheme where langId = :langId");
			if(!"".equals(lStrSchemeType) && lStrSchemeType != null)
			{
				lSBQuery.append(" and schemeType = :schemeType ");
			}
			if(!"".equals(lStrSchemeCode) && lStrSchemeCode != null)
			{
				lSBQuery.append(" and schemeCode like :schemeCode ");
			}
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setLong("langId", lLngLangId);
			if(!"".equals(lStrSchemeType) && lStrSchemeType != null)
			{
				hqlQuery.setString("schemeType", lStrSchemeType);
			}
			if(!"".equals(lStrSchemeCode) && lStrSchemeCode != null)
			{
				hqlQuery.setString("schemeCode", lStrSchemeCode+ "%");
			}
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResult = hqlQuery.list();

			lIntCount = Integer.parseInt(lLstResult.get(0).toString());

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lIntCount;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getSchemeCodeList(java
	 * .lang.Long)
	 */
	public List<MstScheme> getMstSchemeList(Map displayTag, Long lLngLangId, String lStrSchemeCode, String lStrSchemeType) throws Exception {

		List<MstScheme> lLstMstSchemeVO = new ArrayList<MstScheme>();
		StringBuffer lSBQuery = new StringBuffer();
		String lStrColumnValues = null;
		try {
			lStrColumnValues = "schemeCode," + "schemeName,";

			lSBQuery.append("SELECT new com.tcs.sgv.common.valueobject.MstScheme(schemeCode, schemeName) FROM MstScheme where langId = :langId");
			if(!"".equals(lStrSchemeType) && lStrSchemeType != null)
			{
				lSBQuery.append(" and schemeType = :schemeType ");
			}
			if(!"".equals(lStrSchemeCode) && lStrSchemeCode != null)
			{
				lSBQuery.append(" and schemeCode like :schemeCode ");
			}
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			if(!"".equals(lStrSchemeType) && lStrSchemeType != null)
			{
				hqlQuery.setString("schemeType", lStrSchemeType);
			}
			if(!"".equals(lStrSchemeCode) && lStrSchemeCode != null)
			{
				hqlQuery.setString("schemeCode", lStrSchemeCode+ "%");
			}
			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			hqlQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			hqlQuery.setMaxResults(Constants.PAGE_SIZE);

			hqlQuery.setLong("langId", lLngLangId);

			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			lLstMstSchemeVO = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstMstSchemeVO;
	}

	public List getPensionCaseRemarks(String lStrPensionerCode, String lStrLocCode, Long lLngLangId) throws Exception {

		List lLstRemarks = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;

		try {
			Session ghibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT concat(concat(concat(concat(emp.empFname,' '),emp.empMname),' '),emp.empLname),rd.roleName,mvmnt.orderNo,mvmnt.orderDate,mvmnt.remarks,mvmnt.updatedDate ");
			lSBQuery.append(" FROM TrnPensionCaseMvmnt mvmnt,TrnPensionRqstHdr rqst,OrgUserMst user,OrgEmpMst emp,AclRoleDetailsRlt rd \n");
			lSBQuery.append(" WHERE rqst.pensionerCode = mvmnt.pensionerCode AND mvmnt.updatedUserId = user.userId AND user.userId = emp.orgUserMst.userId  \n");
			lSBQuery.append(" AND  mvmnt.roleId = rd.aclRoleMst.roleId  \n");
			// lSBQuery.append(" AND post.postId = up.orgPostMstByPostId.postId AND post.postId = pr.orgPostMst.postId AND pr.aclRoleMst.roleId = rd.aclRoleMst.roleId  \n");
			lSBQuery.append(" AND emp.activateFlag=1  \n ");
			lSBQuery.append(" AND mvmnt.pensionerCode = :pensionerCode AND rqst.locationCode =:locationCode AND  rd.cmnLanguageMst.langId=:langId \n");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPensionerCode.trim());
			lHibQry.setParameter("langId", lLngLangId);
			lHibQry.setParameter("locationCode", lStrLocCode);
			lLstRemarks = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
		}

		return lLstRemarks;


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getPensionCutDtls(java
	 * .lang.String)
	 */
	public List<TrnPensionCutDtls> getPensionCutDtls(String lStrPensionerCode) throws Exception {

		Criteria objCrt = null;
		List<TrnPensionCutDtls> lLstTrnPensionCutDtls = new ArrayList<TrnPensionCutDtls>();

		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionCutDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			lLstTrnPensionCutDtls = objCrt.list();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : getPensionCutDtls() : Error is :" + e, e);
			throw e;
		}
		return lLstTrnPensionCutDtls;
	}

	public void deleteCommutationHistory(List<Long> lLstCvpDtlsId) throws Exception {

		// TODO Auto-generated method stub
		String lStrHqlQry = null;
		Query lHibQry = null;
		try {
			// Session hibSession = getSession();

			lStrHqlQry = " delete from HstCommutationDtls WHERE cvpDtlsId in (:CvpDtlsId)";

			lHibQry = ghibSession.createQuery(lStrHqlQry);

			lHibQry.setParameterList("CvpDtlsId", lLstCvpDtlsId);

			lHibQry.executeUpdate();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : deleteCommutationHistory() : Error is :" + e, e);
			throw e;
		}

	}
	public HstPnsnPmntDcrgDtls getHstPnsnPmntDcrgDtlsVO(String lStrPensionerCode) throws Exception {

		Criteria objCrt = null;
		List<HstPnsnPmntDcrgDtls> lLstHstPnsnPmntDcrgDtls = new ArrayList<HstPnsnPmntDcrgDtls>();
		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtlsVO = new HstPnsnPmntDcrgDtls();
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(HstPnsnPmntDcrgDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			lLstHstPnsnPmntDcrgDtls = objCrt.list();
			if (lLstHstPnsnPmntDcrgDtls != null && !lLstHstPnsnPmntDcrgDtls.isEmpty()) {
				lObjHstPnsnPmntDcrgDtlsVO = lLstHstPnsnPmntDcrgDtls.get(0);
			}
		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : getHstPnsnPmntDcrgDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lObjHstPnsnPmntDcrgDtlsVO;

	}

	public List getTrnPensionRecoveryDtlsForDCRGHstry(String lStrPensionerCode) throws Exception {

		Criteria objCrt = null;
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsForDCRGHstry = new ArrayList<TrnPensionRecoveryDtls>();

		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionRecoveryDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			objCrt.add(Restrictions.eq("recoveryType", gObjRsrcBndle.getString("PPMT.DCRGRECOVERY")));
			objCrt.add(Restrictions.eq("recoveryFromFlag", gObjRsrcBndle.getString("PPMT.DCRG")));
			objCrt.add(Restrictions.isNull("dcrgDtlsId"));
			lLstTrnPensionRecoveryDtlsForDCRGHstry = objCrt.list();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : getTrnPensionRecoveryDtlsForDCRGHstry() : Error is :" + e, e);
			throw e;
		}
		return lLstTrnPensionRecoveryDtlsForDCRGHstry;

	}

	public List getTrnPensionRecoveryDtlsForDCRGHstryPopUp(String lStrPensionerCode) throws Exception {

		Criteria objCrt = null;
		List<TrnPensionRecoveryDtls> lLstTrnPensionRecoveryDtlsForDCRGHstryPopUp = new ArrayList<TrnPensionRecoveryDtls>();
		List<String> lLstDcrgRecoveryFrom = new ArrayList<String>();
		
		try {
			lLstDcrgRecoveryFrom.add(gObjRsrcBndle.getString("PPMT.DCRGHISTORY"));
			lLstDcrgRecoveryFrom.add(gObjRsrcBndle.getString("RECOVERY.SUPPDCRG"));
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionRecoveryDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			//objCrt.add(Restrictions.eq("recoveryType", gObjRsrcBndle.getString("PPMT.DCRGRECOVERY")));
			objCrt.add(Restrictions.in("recoveryFromFlag", lLstDcrgRecoveryFrom));
			//objCrt.add(Restrictions.eq("recoveryFromFlag", gObjRsrcBndle.getString("PPMT.DCRGHISTORY")));
			//objCrt.add(Restrictions.isNotNull("dcrgDtlsId"));
			objCrt.addOrder(Order.asc("dcrgDtlsId"));
			lLstTrnPensionRecoveryDtlsForDCRGHstryPopUp = objCrt.list();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : getTrnPensionRecoveryDtlsForDCRGHstryPopUp() : Error is :" + e, e);
			throw e;
		}
		return lLstTrnPensionRecoveryDtlsForDCRGHstryPopUp;

	}

	public List<Long> getHstDcrgDtlsId(String lStrPensionerCode) throws Exception {

		List<Long> lLstHstDcrgDtlsId = new ArrayList<Long>();
		Query lHibQry = null;
		StringBuffer lSBQuery = null;

		try {
			Session ghibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT dcrgDtlsId FROM HstPnsnPmntDcrgDtls WHERE pensionerCode = :pensionerCode ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPensionerCode.trim());
			lLstHstDcrgDtlsId = lHibQry.list();// handle [null] in above query
												// in dao
			/*
			 * if(!lLstHstDcrgDtlsId.isEmpty() && lLstHstDcrgDtlsId.size() >0) {
			 * if(lLstHstDcrgDtlsId.get(0) == null) {
			 * System.out.println("null"); lLstHstDcrgDtlsId.clear(); } }
			 */
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLstHstDcrgDtlsId;
	}

	public void removeHstPnsnPmntDcrgDtlsAndRecoveryDtls(List<Long> lLstDcrgDtlsId) throws Exception {

		String lStrHqlQry1 = null;
		String lStrHqlQry2 = null;
		Query lHibQry1 = null;
		Query lHibQry2 = null;
		try {
			lStrHqlQry1 = "delete from HstPnsnPmntDcrgDtls WHERE dcrgDtlsId in (:dcrgDtlsId)";
			lHibQry1 = ghibSession.createQuery(lStrHqlQry1);
			lHibQry1.setParameterList("dcrgDtlsId", lLstDcrgDtlsId);
			lHibQry1.executeUpdate();
			lStrHqlQry2 = "delete from TrnPensionRecoveryDtls WHERE dcrgDtlsId in (:dcrgDtlsId)";
			lHibQry2 = ghibSession.createQuery(lStrHqlQry2);
			lHibQry2.setParameterList("dcrgDtlsId", lLstDcrgDtlsId);
			lHibQry2.executeUpdate();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : removeHstPnsnPmntDcrgDtlsAndRecoveryDtls() : Error is :" + e, e);
			throw e;
		}

	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getBankBranchFrmPaymentScheme(java.lang.String)
	 */
	public RltBankBranch getBankBranchFrmPaymentScheme(String lStrLocationCode) throws Exception {

		List lLstResult = new ArrayList();
		RltBankBranch lObjRltBankBranch =new RltBankBranch();
		Query lHibQry = null;
		StringBuffer lSBQuery = null;

		try {
			Session ghibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" FROM RltBankBranch WHERE bankCode = :bankCode and locationCode = :locationCode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("bankCode", gObjRsrcBndle.getString("BANKCODE.PMNTMONEYORDER"));
			lHibQry.setParameter("locationCode", lStrLocationCode);
			
			lLstResult = lHibQry.list();
			
			if(!lLstResult.isEmpty())
			{
				lObjRltBankBranch = (RltBankBranch) lLstResult.get(0);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lObjRltBankBranch;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getHstDcrgDtlsFrmBillNo(java.lang.Long, java.lang.String)
	 */
	public HstPnsnPmntDcrgDtls getHstDcrgDtlsFrmBillNo(Long lLngBillNo, String lStrPensionerCode) throws Exception {

		HstPnsnPmntDcrgDtls lObjHstPnsnPmntDcrgDtls = new HstPnsnPmntDcrgDtls();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM HstPnsnPmntDcrgDtls WHERE billNo = :billNo AND pensionerCode = :pensionerCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", lLngBillNo);
			lQuery.setString("pensionerCode", lStrPensionerCode);

			List lLstResult = lQuery.list();
			if(!lLstResult.isEmpty())
			{
				lObjHstPnsnPmntDcrgDtls = (HstPnsnPmntDcrgDtls) lLstResult.get(0);
			}
			
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lObjHstPnsnPmntDcrgDtls;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getHstCommutationDtlsFrmBillNo(java.lang.Long, java.lang.String)
	 */
	public HstCommutationDtls getHstCommutationDtlsFrmBillNo(Long lLngBillNo, String lStrPensionerCode) throws Exception {

		HstCommutationDtls lObjHstCommutationDtls = new HstCommutationDtls();

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM HstCommutationDtls WHERE billNo = :billNo AND pensionerCode = :pensionerCode ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("billNo", lLngBillNo);
			lQuery.setString("pensionerCode", lStrPensionerCode);

			List lLstResult = lQuery.list();
			if(!lLstResult.isEmpty())
			{
				lObjHstCommutationDtls = (HstCommutationDtls) lLstResult.get(0);
			}
			
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lObjHstCommutationDtls;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#getTrnPensionArrearDtlsVO(java.lang.String, java.lang.String)
	 */
	public TrnPensionArrearDtls getTrnPensionArrearDtlsVO(String lStrPensionerCode,String lStrArrearType) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<TrnPensionArrearDtls> lLstTrnPensionArrearDtls = new ArrayList<TrnPensionArrearDtls>();
		TrnPensionArrearDtls lObjTrnPensionArrearDtlsVO = new TrnPensionArrearDtls();
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionArrearDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			objCrt.add(Restrictions.eq("arrearFieldType", lStrArrearType));
			objCrt.add(Restrictions.eq("paidFlag", "N"));
			objCrt.addOrder(Order.desc("createdDate"));

			lLstTrnPensionArrearDtls = objCrt.list();
			if (lLstTrnPensionArrearDtls != null && !lLstTrnPensionArrearDtls.isEmpty()) {
				lObjTrnPensionArrearDtlsVO = lLstTrnPensionArrearDtls.get(0);
			}
		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : getTrnPensionArrearDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lObjTrnPensionArrearDtlsVO;
	}

	/* (non-Javadoc)
	 * @see com.tcs.sgv.pensionpay.dao.PhysicalCaseInwardDAO#validateArrearDtls(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<TrnPensionArrearDtls> validateArrearDtls(String lStrPensionerCode, String lStrArrearType, String lStrPayInMonthYear) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<TrnPensionArrearDtls> lLstTrnPensionArrearDtls = new ArrayList<TrnPensionArrearDtls>();
		
		try {
			Session hibSession = getSession();
			objCrt = hibSession.createCriteria(TrnPensionArrearDtls.class);
			objCrt.add(Restrictions.eq("pensionerCode", lStrPensionerCode));
			objCrt.add(Restrictions.eq("arrearFieldType", lStrArrearType));
			objCrt.add(Restrictions.like("paymentFromYyyymm", Integer.parseInt(lStrPayInMonthYear)));
			objCrt.add(Restrictions.eq("paidFlag", 'Y'));

			lLstTrnPensionArrearDtls = objCrt.list();
		
		} catch (Exception e) {
			gLogger.error("PhysicalCaseInwardDAOImpl : validateArrearDtls() : Error is :" + e, e);
			throw e;
		}
		return lLstTrnPensionArrearDtls;
	}

	
	public List<MstScheme> getSchemeCodeListFrmMajorHead(String lStrMajorHead,
			String lStrSchemeType) throws Exception {
		// TODO Auto-generated method stub
		List<MstScheme> lLstSchemeCode = new ArrayList<MstScheme>();
		StringBuilder lStrQuery = new StringBuilder();
		
		try {
			lStrQuery.append(" Select  new com.tcs.sgv.common.valueobject.MstScheme(schemeCode, schemeName) ");
			lStrQuery.append(" FROM MstScheme ");
			lStrQuery.append(" WHERE majorHead = :majorHead ");
			lStrQuery.append(" AND schemeType = :schemeType");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setString("majorHead", lStrMajorHead.trim());
			hqlQuery.setString("schemeType", lStrSchemeType);
						
			lLstSchemeCode = hqlQuery.list();
			
			return lLstSchemeCode;
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}

	
	public String getSchemeNameFromSchemeCode(String lStrSchemeCode)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		String lStrSchemeName ="";
		StringBuilder lStrQuery = new StringBuilder();
		
		try {
			lStrQuery.append(" Select schemeName FROM MstScheme WHERE schemeCode = :schemeCode ");
			
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setString("schemeCode", lStrSchemeCode.trim());
								
			lLstResult = hqlQuery.list();
			
			if (lLstResult != null && !lLstResult.isEmpty()) {
				if (lLstResult.get(0) != null) {
					lStrSchemeName = lLstResult.get(0).toString();
				}
			}
			return lStrSchemeName;
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}

	
	public List validateMajorHead(String lStrMajorHead, String lStrSchemeType)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		String lStrSchemeName ="";
		StringBuilder lStrQuery = new StringBuilder();
		
		try {
			lStrQuery.append("FROM MstScheme WHERE  majorHead = :majorHead and schemeType = :schemeType");
			
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setString("majorHead", lStrMajorHead);
			hqlQuery.setString("schemeType", lStrSchemeType);
								
			lLstResult = hqlQuery.list();
			
			return lLstResult;
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}

	public String getPensionerCodeFromPpoNo(String lStrPpoNo,String lStrLocationCode, String lStrCaseStatus) throws Exception {

		String lStrPensionerCode = null;

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("Select pensionerCode FROM TrnPensionRqstHdr where ppoNo = :ppoNo and locationCode = :locationCode and caseStatus = :caseStatus");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("ppoNo", lStrPpoNo.toUpperCase());
			lQuery.setString("caseStatus", lStrCaseStatus);
			lQuery.setString("locationCode", lStrLocationCode);
			
			List lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lStrPensionerCode = lLstResult.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lStrPensionerCode;
	}
	
	public List getBillDtlsFromPensionerCode(String lStrPensionerCode)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("FROM TrnPensionBillDtls WHERE pensionerCode = :pensionerCode");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
						
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstResult;
	}
	
	public List getPnsnrDtlsFromForUpdateBankDtls(String lStrPensionerCode,String lStrLocationCode, String lStrCaseStatus)
			throws Exception {
		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select tph.pensioner_code,mpd.bank_code,mpd.branch_code,rbb.branch_name,tph.location_code from \n");
			lSBQuery.append(" trn_pension_rqst_hdr tph join mst_pensioner_dtls mpd on tph.pensioner_code = mpd.pensioner_code \n"); 
			lSBQuery.append(" and tph.case_status = :caseStatus and tph.location_code = :locationCode \n");
			lSBQuery.append(" left outer join rlt_bank_branch rbb on mpd.branch_code = rbb.branch_code where tph.pensioner_code = :pensionerCode"); 
				
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			lQuery.setString("caseStatus", lStrCaseStatus);
			lQuery.setString("locationCode", lStrLocationCode);
						
			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLstResult;
	}
	
	public void updateBankBranchDtlsForMigratedPpo(String lStrPensionerCode,
			String lStrLocationCode, String lStrBankCode, String lStrBranchCode, BigDecimal lBgDcmlPostId,BigDecimal lBgDcmlUserId,Date lDtCurrDate) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer lSBQuery = new StringBuffer();
		Query lHibQry = null;
		try {
			// Session hibSession = getSession();

			lSBQuery.append("update MstPensionerDtls set bankCode = :bankCode, branchCode = :branchCode, updatedUserId = :userId,  \n");
			lSBQuery.append(" updatedPostId = :postId, updatedDate =:currDate where pensionerCode = :pensionerCode and locationCode = :locationCode ");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());

			lHibQry.setParameter("bankCode",lStrBankCode);
			lHibQry.setParameter("branchCode",lStrBranchCode);
			lHibQry.setParameter("pensionerCode",lStrPensionerCode);
			lHibQry.setParameter("locationCode",lStrLocationCode);
			lHibQry.setParameter("userId",lBgDcmlUserId);
			lHibQry.setParameter("postId",lBgDcmlPostId);
			lHibQry.setParameter("currDate",lDtCurrDate);

			lHibQry.executeUpdate();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : updateBankBranchDtlsForMigratedPpo() : Error is :" + e, e);
			throw e;
		}
	}
	
	public void updateCaseOwnerForMigratedPpo(String lStrPensionerCode,
			String lStrLocationCode, BigDecimal lBgDcmlCaseOwner, BigDecimal lBgDcmlPostId,BigDecimal lBgDcmlUserId,Date lDtCurrDate)  throws Exception{
		// TODO Auto-generated method stub
		StringBuffer lSBQuery = new StringBuffer();
		Query lHibQry = null;
		try {
			// Session hibSession = getSession();

			lSBQuery.append("update TrnPensionRqstHdr set caseOwner = :caseOwner, updatedUserId = :userId,  \n");
			lSBQuery.append(" updatedPostId = :postId, updatedDate =:currDate where pensionerCode = :pensionerCode and locationCode = :locationCode ");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());

			lHibQry.setParameter("caseOwner",lBgDcmlCaseOwner);
			lHibQry.setParameter("pensionerCode",lStrPensionerCode);
			lHibQry.setParameter("locationCode",lStrLocationCode);
			lHibQry.setParameter("userId",lBgDcmlUserId);
			lHibQry.setParameter("postId",lBgDcmlPostId);
			lHibQry.setParameter("currDate",lDtCurrDate);

			lHibQry.executeUpdate();

		} catch (Exception e) {

			gLogger.error("PhysicalCaseInwardDAOImpl : updateCaseOwnerForMigratedPpo() : Error is :" + e, e);
			throw e;
		}
	}
}
