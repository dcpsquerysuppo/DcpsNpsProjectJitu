/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Jun 13, 2011		Bhargav Trivedi								
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.utils.StringUtility;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionSixpayfpArrear;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 Jun 13, 2011
 */
public class SixPayFPArrearDAOImpl extends GenericDaoHibernateImpl implements SixPayFPArrearDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;
	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	/**
	 * @param type
	 */
	public SixPayFPArrearDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#getAllPnsnrCode()
	 */
	public List getAllPnsnrCode() {

		List lLstPnsnrCode = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT arrear.pensionerCode FROM TrnPensionSixpayfpArrear arrear GROUP BY  arrear.pensionerCode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lLstPnsnrCode = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstPnsnrCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#getPPODtls(java.lang.String)
	 */
	public List getPPODtls(String lStrPnsnrCode, String lStrHistory) {

		List lLstHeadCode = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT arrear.payInMonth,arrear.installmentAmnt,arrear.remarks,arrear.arrearId,arrear.paidFlag FROM TrnPensionSixpayfpArrear arrear WHERE arrear.pensionerCode =:pensionerCode ");
			if (lStrHistory == null) {
				lSBQuery.append(" And arrear.arrearType = 'S' AND arrear.activeFlag = 'Y' AND arrear.revisionCounter = (SELECT MAX(arrear1.revisionCounter) FROM TrnPensionSixpayfpArrear arrear1  WHERE  arrear1.pensionerCode =:pensionerCode)  ");
			} else {
				lSBQuery.append(" And arrear.arrearType = 'S' ");
			}
			lSBQuery.append(" ORDER BY arrear.revisionCounter DESC,arrear.installmentNo ASC  ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPnsnrCode);
			lLstHeadCode = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstHeadCode;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#getMonthDescFromMonthId(
	 * java.lang.String, java.lang.String)
	 */
	public String getMonthDescFromMonthId(String lStrMonthId, String lStrLangId) {

		String lStrMonthDesc = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT monthName FROM SgvaMonthMst  WHERE langId = :langId AND monthId=:monthId ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", lStrLangId);
			lHibQry.setParameter("monthId", BigDecimal.valueOf(Long.valueOf(lStrMonthId)));
			lStrMonthDesc = lHibQry.list().get(0).toString().trim();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lStrMonthDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#getMaxRvsnCntr(java.lang
	 * .String)
	 */
	public Character getMaxRvsnCntr(String lStrPnsnrCode) {

		Character lCharRvsnCntr = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT MAX(arrear.revisionCounter) FROM TrnPensionSixpayfpArrear arrear  WHERE  arrear.pensionerCode =:pensionerCode ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPnsnrCode);

			if (lHibQry.list().get(0) == null) {
				lCharRvsnCntr = null;
			} else {
				lCharRvsnCntr = StringUtility.convertToChar(lHibQry.list().get(0).toString().trim());
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lCharRvsnCntr;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#updateOldPPOEntries(java
	 * .lang.Character, java.lang.String)
	 */
	public void updateOldPPOEntries(Character lCharRvsnCntr, String lStrPnsnrCode) {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" UPDATE TrnPensionSixpayfpArrear arrear SET arrear.activeFlag = 'N'");
			lSBQuery.append(" WHERE arrear.pensionerCode =:pensionerCode AND arrear.revisionCounter =:revisionCounter and arrearType = :lArrearType ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("pensionerCode", lStrPnsnrCode);
			lQuery.setParameter("revisionCounter", lCharRvsnCntr);
			lQuery.setCharacter("lArrearType", 'S');
			lQuery.executeUpdate();

		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#getTrnPensionSixpayfpArrearVOs
	 * (java.lang.String, java.lang.Character)
	 */
	public List<TrnPensionSixpayfpArrear> getTrnPensionSixpayfpArrearVOs(String lStrPnsnrCode, Character lCharRvsnCntr) {

		List lLstTrnPensionSixpayfpArrearVO = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" FROM TrnPensionSixpayfpArrear arrear WHERE arrear.pensionerCode =:pensionerCode AND arrear.revisionCounter =:revisionCounter AND arrear.arrearType = :lCharArrearType ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPnsnrCode);
			lHibQry.setParameter("revisionCounter", lCharRvsnCntr);
			lHibQry.setCharacter("lCharArrearType", 'S');
			lLstTrnPensionSixpayfpArrearVO = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstTrnPensionSixpayfpArrearVO;

	}
	
	public String getSixpayfpArrearTotalPaidCashAmt(String lStrPnsnrCode) throws Exception {

		List lLstResult = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		String lStrCashAmt = "";
		BigDecimal lBgDcmlCashAmt = BigDecimal.ZERO;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append("select cast(sum(installment_amnt) as bigint) from trn_pension_sixpayfp_arrear where pensioner_code = :pensionerCode ");
			lSBQuery.append(" and paid_flag = 'Y' and arrear_type = :lCharArrearType group by pensioner_code");
			lHibQry = ghibSession.createSQLQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPnsnrCode);
			lHibQry.setCharacter("lCharArrearType", 'C');
			lLstResult = lHibQry.list();
			if(!lLstResult.isEmpty())
			{
				lStrCashAmt = lLstResult.get(0).toString();
			}
		} catch (Exception e) {
			
			gLogger.error("Error is :" + e, e);
			throw e;
		}

		return lStrCashAmt;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#getSixPayFPArrearList(java
	 * .lang.String, java.util.List, java.util.Map)
	 */
	public List getSixPayFPArrearList(String lStrNoOfInstallment, List<BigDecimal> lLstDaRateFor, Map displaytag, String lStrArrearConfigBy, String lStrPPONo) {

		List lLstSixPayFPArrears = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		String[] columnValues = null;
		try {

			lSBQuery = new StringBuffer();
			if ("P".equals(lStrArrearConfigBy)) {
				columnValues = new String[]{"", "arrear.arrearId", "stateCode.stateDesc", "rqst.ledgerNo", "rqst.pageNo", "rqst.ppoNo,pnsnhdr.firstName,bank.bankName,branch.branchName"};
				lSBQuery.append(" SELECT arrear.arrearId,stateCode.stateDesc,rqst.ledgerNo,rqst.pageNo,rqst.ppoNo,pnsnhdr.firstName,bank.bankName,branch.branchName ");
				lSBQuery.append(" FROM TrnPensionRqstHdr rqst,MstPensionerDtls dtls,MstPensionerHdr pnsnhdr,TrnPensionSixpayfpArrear arrear,MstBank bank,RltBankBranch branch,MstPensionStateRate stateCode ");
				lSBQuery.append(" WHERE rqst.pensionerCode = dtls.pensionerCode AND rqst.pensionerCode = pnsnhdr.pensionerCode AND dtls.pensionerCode = pnsnhdr.pensionerCode ");
				lSBQuery.append(" AND arrear.pensionerCode = rqst.pensionerCode AND arrear.pensionerCode = dtls.pensionerCode AND arrear.pensionerCode = pnsnhdr.pensionerCode ");
				lSBQuery.append(" AND bank.bankCode = branch.bankCode AND bank.bankCode = dtls.bankCode AND branch.branchCode = dtls.branchCode ");
				lSBQuery.append(" AND stateCode.stateCode = rqst.daRateForState ");
				lSBQuery.append(" AND rqst.status = 'Continue' AND dtls.identificationFlag = 'Y' AND rqst.ropType = '2006' AND rqst.calcType = 'A' AND arrear.activeFlag = 'Y' ");
				lSBQuery.append(" AND arrear.installmentNo =:installmentNo AND arrear.payInMonth IS NULL ");
			}
			if ("S".equals(lStrArrearConfigBy)) {
				columnValues = new String[]{"", "arrear.arrearId"};
				lSBQuery.append(" SELECT arrear.arrearId \n");
				lSBQuery.append(" FROM TrnPensionRqstHdr rqst,MstPensionerDtls dtls,MstPensionerHdr pnsnhdr,TrnPensionSixpayfpArrear arrear \n");
				lSBQuery.append(" WHERE rqst.pensionerCode = dtls.pensionerCode AND rqst.pensionerCode = pnsnhdr.pensionerCode AND dtls.pensionerCode = pnsnhdr.pensionerCode \n");
				lSBQuery.append(" AND arrear.pensionerCode = rqst.pensionerCode AND arrear.pensionerCode = dtls.pensionerCode AND arrear.pensionerCode = pnsnhdr.pensionerCode \n");
				lSBQuery.append(" AND rqst.status = 'Continue' AND dtls.identificationFlag = 'Y' AND rqst.ropType = '2006' AND rqst.calcType = 'A' AND arrear.activeFlag = 'Y' \n");
				lSBQuery.append(" AND arrear.installmentNo =:installmentNo AND arrear.payInMonth IS NULL ");
			}

			if ("S".equals(lStrArrearConfigBy) && lLstDaRateFor.size() > 0) {
				lSBQuery.append(" AND rqst.daRateForState IN (:daRateForState) ");
			}
			if (lStrNoOfInstallment.equals("1")) {
				lSBQuery.append(" AND arrear.installmentAmnt != 0 ");
			}
			if ("P".equals(lStrArrearConfigBy)) {
				lSBQuery.append(" AND rqst.ppoNo = :lStrPPONo ");
			}

			if (displaytag != null) {
				String orderString = (displaytag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displaytag.get(Constants.KEY_SORT_ORDER) : "desc");
				Integer orderbypara = null;
				if (displaytag.containsKey(Constants.KEY_SORT_PARA)) {
					orderbypara = (Integer) displaytag.get(Constants.KEY_SORT_PARA);
					lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
				} else {
					lSBQuery.append(" ORDER BY rqst.daRateForState DESC");
				}
			}

			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("installmentNo", Integer.valueOf(lStrNoOfInstallment));
			if ("S".equals(lStrArrearConfigBy) && lLstDaRateFor.size() > 0) {
				lHibQry.setParameterList("daRateForState", lLstDaRateFor);
			}
			if ("P".equals(lStrArrearConfigBy)) {
				lHibQry.setString("lStrPPONo", lStrPPONo);
			}
			if (displaytag != null) {
				Integer pageNo = (displaytag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displaytag.get(Constants.KEY_PAGE_NO) : 1);
				lHibQry.setFirstResult((pageNo.intValue() - 1) * Constants.PDWL_PAGE_SIZE);
				lHibQry.setMaxResults(Constants.PDWL_PAGE_SIZE);
			}
			lLstSixPayFPArrears = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLstSixPayFPArrears;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#getSixPayFPArrearListCount
	 * (java.lang.String, java.util.List, java.util.Map)
	 */
	public Integer getSixPayFPArrearListCount(String lStrNoOfInstallment, List<BigDecimal> lLstDaRateFor, Map displaytag, String lStrArrearConfigBy, String lStrPPONo) {

		Integer lIntCount = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		String[] columnValues = null;
		try {

			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT COUNT(*) ");
			lSBQuery.append(" FROM TrnPensionRqstHdr rqst,MstPensionerDtls dtls,MstPensionerHdr pnsnhdr,TrnPensionSixpayfpArrear arrear,MstBank bank,RltBankBranch branch ");
			lSBQuery.append(" WHERE rqst.pensionerCode = dtls.pensionerCode AND rqst.pensionerCode = pnsnhdr.pensionerCode AND dtls.pensionerCode = pnsnhdr.pensionerCode ");
			lSBQuery.append(" AND arrear.pensionerCode = rqst.pensionerCode AND arrear.pensionerCode = dtls.pensionerCode AND arrear.pensionerCode = pnsnhdr.pensionerCode ");
			lSBQuery.append(" AND bank.bankCode = branch.bankCode AND bank.bankCode = dtls.bankCode AND branch.branchCode = dtls.branchCode ");
			lSBQuery.append(" AND rqst.status = 'Continue' AND dtls.identificationFlag = 'Y' AND rqst.ropType = '2006' AND rqst.calcType = 'A' AND arrear.activeFlag = 'Y' ");
			lSBQuery.append(" AND arrear.installmentNo =:installmentNo AND arrear.payInMonth IS NULL ");
			if ("S".equals(lStrArrearConfigBy) && (lLstDaRateFor.size() > 0)) {
				lSBQuery.append(" AND rqst.daRateForState IN (:daRateForState) ");
			}
			if (lStrNoOfInstallment.equals("1")) {
				lSBQuery.append(" AND arrear.installmentAmnt != 0 ");
			}
			if ("P".equals(lStrArrearConfigBy)) {
				lSBQuery.append(" AND rqst.ppoNo = :lStrPPONo ");
			}
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("installmentNo", Integer.valueOf(lStrNoOfInstallment));
			if ("S".equals(lStrArrearConfigBy) && (lLstDaRateFor.size() > 0)) {
				lHibQry.setParameterList("daRateForState", lLstDaRateFor);
			}
			if ("P".equals(lStrArrearConfigBy)) {
				lHibQry.setString("lStrPPONo", lStrPPONo);
			}
			if (lHibQry.list() != null && lHibQry.list().size() > 0) {
				lIntCount = Integer.parseInt(lHibQry.list().get(0).toString());
			} else {
				lIntCount = 0;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lIntCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#getAllHeadCodeAndDesc(java
	 * .lang.String)
	 */

	public List<ComboValuesVO> getAllHeadCodeAndDesc(String lStrLangId) {

		List<ComboValuesVO> lLstHeadCodes = new ArrayList<ComboValuesVO>();
		Iterator itr = null;
		ComboValuesVO lObjComboValuesVO = null;

		List lLstResult = null;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT headCode,description FROM MstPensionHeadcode WHERE langId=:langId ORDER BY headCode");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("langId", lStrLangId);
			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				itr = lLstResult.iterator();
				while (itr.hasNext()) {
					lObjComboValuesVO = new ComboValuesVO();
					Object[] tuple = (Object[]) itr.next();
					lObjComboValuesVO.setId(tuple[0].toString());
					lObjComboValuesVO.setDesc(tuple[1].toString());
					lLstHeadCodes.add(lObjComboValuesVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			// e.printStackTrace();

		}
		return lLstHeadCodes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.SixPayFPArrearDAO#getRopType(java.lang.String,
	 * java.lang.String)
	 */

	public String getRopType(String lStrPPONo, String lStrLocationCode) {

		String lStrRopType = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT rqst.ropType FROM TrnPensionRqstHdr rqst WHERE  rqst.ppoNo =:ppoNo AND rqst.locationCode = :locationCode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("ppoNo", lStrPPONo);
			lHibQry.setParameter("locationCode", lStrLocationCode);
			if (!lHibQry.list().isEmpty() && lHibQry.list().size() > 0) {
				lStrRopType = lHibQry.list().get(0).toString().trim();
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lStrRopType;

	}

	public List<Integer> getPaidPPOEntries(Character lCharRvsnCntr, String lStrPnsnrCode) {

		List<Integer> lLstPaidPPOEntries = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT arrear.installmentNo  FROM TrnPensionSixpayfpArrear arrear WHERE arrear.pensionerCode =:pensionerCode AND arrear.revisionCounter =:revisionCounter  ");
			lSBQuery.append(" AND arrear.paidFlag = 'Y' AND arrear.activeFlag = 'Y' ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPnsnrCode);
			lHibQry.setParameter("revisionCounter", lCharRvsnCntr);
			lLstPaidPPOEntries = lHibQry.list();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstPaidPPOEntries;
	}

	public BigDecimal getPrevInstAmnt(String lStrPnsnrCode, Character lCharRvsnCntr, Integer lIntInstallmentNo) {

		BigDecimal lBDInstAmnt = BigDecimal.ZERO;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT arrear.installmentAmnt  FROM TrnPensionSixpayfpArrear arrear WHERE arrear.pensionerCode =:pensionerCode AND arrear.revisionCounter =:revisionCounter  ");
			lSBQuery.append(" AND arrear.installmentNo = :installmentNo AND arrear.paidFlag = 'Y' AND arrear.activeFlag = 'Y' ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPnsnrCode.trim());
			lHibQry.setParameter("revisionCounter", lCharRvsnCntr);
			lHibQry.setParameter("installmentNo", lIntInstallmentNo);

			if (lHibQry.list() != null && lHibQry.list().size() > 0) {
				lBDInstAmnt = BigDecimal.valueOf(Double.valueOf((lHibQry.list().get(0).toString())));
			}
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lBDInstAmnt;
	}

	public void deleteUnPaidSixPayArrearDtls(String lStrPnsrCode) throws Exception {

		Query lHibQry = null;
		StringBuilder lSBQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" Delete  from TrnPensionSixpayfpArrear arrear \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" arrear.pensionerCode =:pensionerCode \n");
			lSBQuery.append(" And arrear.arrearType =:arrearType  \n");
			lSBQuery.append(" And arrear.paidFlag =:paidFlag  \n");
			lSBQuery.append(" And arrear.activeFlag =:activeFlag  \n");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPnsrCode);
			lHibQry.setParameter("arrearType", 'C');
			lHibQry.setParameter("paidFlag", 'N');
			lHibQry.setParameter("activeFlag", 'Y');
			lHibQry.executeUpdate();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
	}

	public List<Object[]> getCashSixPayArrearDtls(String lStrPnsrCode) throws Exception {

		Query lHibQry = null;
		StringBuilder lSBQuery = null;
		List<Object[]> lLstArrObj = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" Select payInMonth,installmentAmnt,paidFlag,arrearId \n");
			lSBQuery.append(" From  TrnPensionSixpayfpArrear \n");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" arrearType = :arrearType \n");
			lSBQuery.append(" and activeFlag = :activeFlag \n");
			lSBQuery.append(" and pensionerCode = :lPnsrCode \n");
			lSBQuery.append(" order by paidFlag desc,payInMonth,installmentAmnt \n");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("arrearType", "C");
			lHibQry.setParameter("activeFlag", "Y");
			lHibQry.setParameter("lPnsrCode", lStrPnsrCode);
			lLstArrObj = lHibQry.list();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
		return lLstArrObj;
	}

	public List<Object[]> validatePPONo(String lStrPPONo, Long gLngLangId, String gStrLocationCode) {

		Boolean lBLFlag = false;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List<Object[]> lLstPPONoDtls = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT  ppoNo,caseStatus,ropType ");
			lSBQuery.append(" FROM  TrnPensionRqstHdr ");
			lSBQuery.append(" WHERE  locationCode = :locationCode  and ppoNo =:ppoNo");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("locationCode", gStrLocationCode.trim());
			// hqlQuery.setParameter("caseStatus",
			// gObjRsrcBndle.getString("STATFLG.APPROVED"));
			// hqlQuery.setParameter("ropType", "2006");
			hqlQuery.setParameter("ppoNo", lStrPPONo.trim());
			lLstPPONoDtls = hqlQuery.list();
		} catch (Exception e) {
			// e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstPPONoDtls;
	}

	public void updatePayInMonthByArrearIds(List<Long> lLstArrearIds, String lStrPayInMonth, BigDecimal lBDUserId, BigDecimal lBDPostId, Date lDtUpdate) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("Update  TrnPensionSixpayfpArrear \n");
			lSBQuery.append("set payInMonth = :lStrPayInMonth,updatedUserId = :lBDUserId,updatedPostId = :lBDPostId,updatedDate = :lDtUpdate \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("arrearId in (:lLstArrearIds) \n");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameterList("lLstArrearIds", lLstArrearIds);
			hqlQuery.setString("lStrPayInMonth", lStrPayInMonth);
			hqlQuery.setBigDecimal("lBDUserId", lBDUserId);
			hqlQuery.setBigDecimal("lBDPostId", lBDPostId);
			hqlQuery.setDate("lDtUpdate", lDtUpdate);
			hqlQuery.executeUpdate();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
			throw e;
		}
	}

	public Long getMaxRevision(String lStrPensionerCode) throws Exception {

		Long lLngRevision = null;
		List lLstResData = null;
		try {
			Session ghibSession = getSession();
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MAX(revision) FROM HstTrnPensionSixpayfpArrear ");
			lSBQuery.append("WHERE pensionerCode =:PensionerCode ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("PensionerCode", lStrPensionerCode);
			lLstResData = lQuery.list();

			if (lLstResData.get(0) != null) {
				lLngRevision = (Long) lLstResData.get(0);
			} else {
				lLngRevision = 1l;
				return lLngRevision;
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return (lLngRevision + 1l);
	}

	public void updateSixPayArrearInstlmentDate(String lStrPayInMonth, String lStrLocCode, List<BigDecimal> lLstBgDaRateForState, BigDecimal lBDUserId, BigDecimal lBDPostId, Date lDtUpdate)
			throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		try {
			Session ghibSession = getSession();
			lSBQuery.append("update TRN_PENSION_SIXPAYFP_ARREAR s1 \n");
			lSBQuery.append("set s1.PAY_IN_MONTH = :lStrPayInMonth,s1.Updated_User_Id = :lBDUserId,s1.Updated_Post_Id = :lBDPostId,s1.Updated_Date = :lDtUpdate \n");
			lSBQuery.append("where exists  \n");
			lSBQuery.append("(select 1 from  \n");
			lSBQuery.append("	(select distinct s.pensioner_Code from TRN_PENSION_SIXPAYFP_ARREAR s \n");
			lSBQuery.append("	where exists (select 1 from \n");
			lSBQuery.append("	TRN_PENSION_RQST_HDR prh, \n");
			lSBQuery.append("	MST_PENSIONER_HDR mph \n");
			lSBQuery.append("	Where \n");
			lSBQuery.append("	mph.pensioner_Code = prh.pensioner_Code \n");
			lSBQuery.append("	and mph.location_Code = prh.location_Code \n");
			lSBQuery.append("	and mph.pensioner_Code = s.pensioner_Code \n");
			lSBQuery.append("	and prh.rop_Type = :lRopType \n");
			lSBQuery.append("	and prh.seen_Flag = :lSeenFlag \n");
			lSBQuery.append("	and nvl(to_char(mph.date_Of_Death,'YYYYMM'),'0') <= s.pay_In_Month \n");
			lSBQuery.append("	and prh.COMMENCEMENT_DATE <= '2009-03-31' \n");
			lSBQuery.append("	and prh.location_Code = :lLocCode \n");
			lSBQuery.append("	and s.installment_No = 1 \n");
			lSBQuery.append("	and s.pay_In_Month is not null \n");
			lSBQuery.append("	and s.arrear_Type = 'S' \n");
			lSBQuery.append("	and prh.DA_RATE_FOR_STATE in (:lLstDaRateForState))) innq \n");
			lSBQuery.append("where innq.pensioner_Code = s1.pensioner_Code \n");
			lSBQuery.append("and   s1.installment_No = 4 \n");
			lSBQuery.append("and   s1.installment_Amnt > 0 \n");
			lSBQuery.append("and   s1.active_Flag = 'Y' \n");
			lSBQuery.append("and   s1.paid_Flag = 'N' \n");
			lSBQuery.append("and   s1.ARREAR_TYPE = 'S' \n");
			lSBQuery.append("and   s1.pay_In_Month is null) \n");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("lRopType", "2006");
			lQuery.setString("lSeenFlag", "Y");
			lQuery.setString("lLocCode", lStrLocCode);
			lQuery.setString("lStrPayInMonth", lStrPayInMonth);
			lQuery.setParameterList("lLstDaRateForState", lLstBgDaRateForState);
			lQuery.setBigDecimal("lBDUserId", lBDUserId);
			lQuery.setBigDecimal("lBDPostId", lBDPostId);
			lQuery.setDate("lDtUpdate", lDtUpdate);
			lQuery.executeUpdate();
			// throw new Exception("test exception ");
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}

	
	public 	String getSupplRequestNoFromPnsnrCode(String lStrPensionerCode, List<Short> lLstStatus, String lStrLocCode) throws Exception {
		// TODO Auto-generated method stub
		String lStrRequestNo = "";
		List lLstResult = new ArrayList();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();
			
			lSBQuery.append("select distinct tpsb.requestNo from TrnPensionSupplyBillDtls tpsb, TrnPensionSixpayfpArrear tpf \n");
			lSBQuery.append(" where tpsb.pensionSupplyBillId = tpf.pensionSupplyBillId and tpsb.billType = 'PENSION' \n");
			lSBQuery.append(" and tpf.activeFlag = 'Y' and tpf.pensionSupplyBillId is not null \n");
			lSBQuery.append(" and tpsb.pensionerCode = :pensionerCode and tpsb.locationCode = :locationCode and tpsb.status in (:statusList) ");
								
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			lQuery.setString("locationCode", lStrLocCode);
			lQuery.setParameterList("statusList", lLstStatus);
						
			lLstResult = lQuery.list();
			if(!lLstResult.isEmpty())
			{
				lStrRequestNo = lLstResult.get(0).toString();
			}

		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lStrRequestNo;
	}
}
