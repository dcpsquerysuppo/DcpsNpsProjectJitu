/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 27, 2011		Bhargav Trivedi								
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
import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.HstPnsnPmntPpoNo;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 27, 2011
 */
public class TransferPPODAOImpl extends GenericDaoHibernateImpl implements TransferPPODAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public TransferPPODAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getNameAndTreasuryFromPPONo
	 * (java.lang.String)
	 */
	public List getNameAndTreasuryFromPPONo(String lStrPPONo, Long gLngLangId, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = null;
		Query sqlQuery = null;
		List lLstNameAndTreasury = null;
		List<String> lLstTransferStatus = new ArrayList<String>();
		lLstTransferStatus.add(gObjRsrcBndle.getString("STATFLG.APPROVED"));
		lLstTransferStatus.add(gObjRsrcBndle.getString("STATUS.RQSTREJECT"));
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" Select pension.first_Name,loc.loc_Name,loc.location_Code,req.pensioner_Code,req.case_Status,ptd.request_Id,ptd.to_location,pfd.name,loc.office_code,ptd.appln_receive_date \n ");
			lSBQuery.append(" from Trn_Pension_Rqst_Hdr req \n");
			lSBQuery.append(" join Mst_Pensioner_Hdr pension on req.pensioner_Code = pension.pensioner_Code \n");
			lSBQuery.append(" join Cmn_Location_Mst loc on loc.location_Code = req.location_Code \n");
			lSBQuery.append(" left outer join Mst_Pensioner_Family_Dtls pfd on pfd.pensioner_Code = pension.pensioner_Code and pfd.percentage = :lPerc \n");
			lSBQuery.append(" left outer join trn_pension_transfer_dtls ptd on ptd.pensioner_code = req.pensioner_code and ptd.status not in (:TransferStatus) \n ");
			lSBQuery.append(" where  \n");
			lSBQuery.append(" req.ppo_No = :ppoNo \n");
			lSBQuery.append(" and loc.lang_Id = :langId \n");
			lSBQuery.append(" and loc.location_Code = :locationCode \n");
			// lSBQuery.append(" group by pension.first_Name,loc.loc_Name,loc.location_Code,req.pensioner_Code,req.case_Status,ptd.request_Id ");
			sqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			sqlQuery.setString("locationCode", lStrLocationCode);
			sqlQuery.setLong("lPerc", 100);
			sqlQuery.setString("ppoNo", lStrPPONo.toUpperCase());
			sqlQuery.setInteger("langId", gLngLangId.intValue());
			sqlQuery.setParameterList("TransferStatus", lLstTransferStatus);
			lLstNameAndTreasury = sqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstNameAndTreasury;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.TransferPPODAO#getAllTreasuryName(long)
	 */
	public List getAllTreasuryName(long langId) throws Exception {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			Session ghibSession = getSession();

			strQuery.append("SELECT locationCode,locName  FROM CmnLocationMst WHERE cmnLanguageMst.langId =:langId  AND departmentId IN (100003,365450) order by locName");
			Query hiQuery = ghibSession.createQuery(strQuery.toString());
			hiQuery.setCacheable(true).setCacheRegion("pensionCache");
			hiQuery.setLong("langId", langId);
			resultList = hiQuery.list();
			if (!resultList.isEmpty()) {
				Iterator itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					arrTreasury.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw e;
		}
		return arrTreasury;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#updateMstPensionerDtls(java
	 * .lang.String)
	 */
	public void updateMstPensionerDtls(String lStrPensionerCode) {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append("UPDATE MstPensionerDtls pnsnrdtls SET pnsnrdtls.bankCode =:bankCode,pnsnrdtls.branchCode =:branchCode,pnsnrdtls.accountNo =:accountNo ");

			lSBQuery.append(" WHERE pnsnrdtls.pensionerCode = :pensionerCode  ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("bankCode", null);
			lQuery.setParameter("branchCode", null);
			lQuery.setParameter("accountNo", null);
			lQuery.setParameter("pensionerCode", lStrPensionerCode);
			lQuery.executeUpdate();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#updateTrnPensionRqstHdr(java
	 * .lang.String)
	 */
	public void updateTrnPensionRqstHdr(String lStrPensionerCode, String lStrFlagValue, String lStrLocationCode, Long lLngUserId, Long lLngPostId, Date lDtUpdated) {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			if (lStrFlagValue != null && lStrFlagValue.length() > 0) {
				if (lStrFlagValue.equals("1")) // for received PPO case
				{
					lSBQuery.append(" UPDATE TrnPensionRqstHdr reqhdr SET reqhdr.caseStatus =:caseStatus ,reqhdr.typeFlag = 'T',reqhdr.locationCode=:locationCode,reqhdr.updatedUserId = :lLngUserId,reqhdr.updatedPostId = :lLngPostId,reqhdr.updatedDate = :lDtUpdt ");
					lSBQuery.append(" WHERE reqhdr.pensionerCode = :pensionerCode  ");
				}
				if (lStrFlagValue.equals("2")) // for rejected PPO case
				{
					lSBQuery.append(" UPDATE TrnPensionRqstHdr reqhdr SET reqhdr.caseStatus =:caseStatus,reqhdr.updatedUserId = :lLngUserId,reqhdr.updatedPostId = :lLngPostId,reqhdr.updatedDate = :lDtUpdt ");
					lSBQuery.append(" WHERE reqhdr.pensionerCode = :pensionerCode  ");
				}
				if (lStrFlagValue.equals("3")) // move PPO case into record room
				{
					lSBQuery.append(" UPDATE TrnPensionRqstHdr reqhdr SET reqhdr.caseStatus =:caseStatus,reqhdr.updatedUserId = :lLngUserId,reqhdr.updatedPostId = :lLngPostId,reqhdr.updatedDate = :lDtUpdt ");
					lSBQuery.append(" WHERE reqhdr.pensionerCode = :pensionerCode  ");
				}

			} else // for first time click on submit means in transfer to other
					// treasury
			{
				lSBQuery.append(" UPDATE TrnPensionRqstHdr reqhdr SET reqhdr.caseStatus =:caseStatus,reqhdr.updatedUserId = :lLngUserId,reqhdr.updatedPostId = :lLngPostId,reqhdr.updatedDate = :lDtUpdt ");
				lSBQuery.append(" WHERE reqhdr.pensionerCode = :pensionerCode  ");
				//
			}

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			if (lStrFlagValue != null && lStrFlagValue.length() > 0) {
				if (lStrFlagValue.equals("1")) // for received PPO case
				{
					lQuery.setParameter("caseStatus", gObjRsrcBndle.getString("STATFLG.NEW"));
					lQuery.setParameter("locationCode", lStrLocationCode);
				}
				if (lStrFlagValue.equals("2")) // for rejected PPO case
				{
					lQuery.setParameter("caseStatus", gObjRsrcBndle.getString("STATFLG.TRANSFERREJECT"));
				}
				if (lStrFlagValue.equals("3")) // move PPO case into record room
				{
					lQuery.setParameter("caseStatus", gObjRsrcBndle.getString("STATFLG.APPROVED"));
				}
			} else // for first time click on submit means in transfer to other
					// treasury
			{
				lQuery.setParameter("caseStatus", gObjRsrcBndle.getString("STATFLG.TRANSFERNEW"));
			}

			lQuery.setParameter("pensionerCode", lStrPensionerCode);
			lQuery.setLong("lLngUserId", lLngUserId);
			lQuery.setLong("lLngPostId", lLngPostId);
			lQuery.setDate("lDtUpdt", lDtUpdated);
			lQuery.executeUpdate();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#updateMstPensionerDtls(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	public void updateMstPensionerDtls(String lStrPensionerCode, String lStrBankCode, String lStrBranchCode, String lStrAccNo, String lStrFlagVal) {

		StringBuilder lSBQuery = null;
		Query lQuery = null;
		try {
			lSBQuery = new StringBuilder();
			lSBQuery.append(" UPDATE MstPensionerDtls pnsnrdtls SET pnsnrdtls.bankCode =:bankCode ,pnsnrdtls.branchCode =:branchCode,pnsnrdtls.accountNo =:accountNo ");
			if (lStrFlagVal != null && lStrFlagVal.length() > 0) {
				if (lStrFlagVal.equals("1")) {
					lSBQuery.append(" ,pnsnrdtls.registrationNo =:registrationNo");
				}

			}
			lSBQuery.append(" WHERE pnsnrdtls.pensionerCode = :pensionerCode  ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("pensionerCode", lStrPensionerCode);
			lQuery.setParameter("bankCode", lStrBankCode);
			lQuery.setParameter("branchCode", lStrBranchCode);
			lQuery.setParameter("accountNo", lStrAccNo);
			if (lStrFlagVal != null && lStrFlagVal.length() > 0) {
				if (lStrFlagVal.equals("1")) {
					lQuery.setParameter("registrationNo", null);
				}
			}
			lQuery.executeUpdate();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getTransferCases(java.lang.
	 * Long, java.lang.String, java.lang.String)
	 */
	public List getTransferCases(Long gLngLangId, String gStrLocationCode, String lStrCmbSearchBy, String lStrSearchVal, String lStrTransferCase, Map displaytag) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstTransferCases = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			String[] columnValues = new String[]{"", "transfer.transferDtlsId", "transfer.ppoNo", "pension.firstName", "loc.locName"};
			lSBQuery.append(" SELECT transfer.transferDtlsId,transfer.ppoNo,pension.firstName,loc.locName,transfer.agFlag,pension.pensionerCode ");
			lSBQuery.append(" FROM TrnPensionTransferDtls transfer,MstPensionerHdr pension,CmnLocationMst loc ");
			//lSBQuery.append(" ,MstPensionerDtls pnsnrdtls ");
			lSBQuery.append(" WHERE transfer.pensionerCode = pension.pensionerCode " );
			//lSBQuery.append(" and transfer.pensionerCode = pnsnrdtls.pensionerCode AND pension.pensionerCode =  pnsnrdtls.pensionerCode  ");
			if (lStrTransferCase != null && lStrTransferCase.length() > 0) {
				if (lStrTransferCase.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.RECEIVE"))) {
					if (lStrCmbSearchBy != null && lStrCmbSearchBy.length() > 0 && lStrSearchVal != null) {
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PPONO"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND transfer.ppoNo =:ppoNo ");
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PNSRNAME"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND pension.firstName like :firstName ");
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.TREASURYNAME"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND loc.locationCode =:locationCode");
							}
						}
						lSBQuery.append(" AND loc.locationCode = transfer.fromLocation   AND loc.cmnLanguageMst.langId = :langId ");
						lSBQuery.append(" AND transfer.toLocation =:toLocation  AND transfer.status =:status ");
					} else {
						lSBQuery.append(" AND loc.locationCode = transfer.fromLocation   AND loc.cmnLanguageMst.langId = :langId ");
						lSBQuery.append(" AND transfer.toLocation =:toLocation  AND transfer.status =:status ");
					}

				}
				if (lStrTransferCase.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.REJECT"))) {

					if (lStrCmbSearchBy != null && lStrCmbSearchBy.length() > 0 && lStrSearchVal != null) {
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PPONO"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND transfer.ppoNo =:ppoNo ");
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PNSRNAME"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND pension.firstName like :firstName ");
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.TREASURYNAME"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND loc.locationCode =:locationCode");
							}
						}
						lSBQuery.append("  AND loc.locationCode = transfer.toLocation   AND loc.cmnLanguageMst.langId = :langId ");
						lSBQuery.append("  AND transfer.fromLocation =:fromLocation  AND transfer.status =:status ");
					} else {
						lSBQuery.append("  AND loc.locationCode = transfer.toLocation   AND loc.cmnLanguageMst.langId = :langId ");
						lSBQuery.append("  AND transfer.fromLocation =:fromLocation  AND transfer.status =:status ");
					}
				}
			}
			lSBQuery.append("  AND transfer.receivePostId  IS NULL  AND transfer.receiveDate IS NULL ");

			String orderString = (displaytag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displaytag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displaytag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displaytag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append(" ORDER BY pension.firstName DESC");
			}

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			if (lStrTransferCase != null && lStrTransferCase.length() > 0) {
				if (lStrTransferCase.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.RECEIVE"))) {
					if (lStrCmbSearchBy != null && lStrCmbSearchBy.length() > 0 && lStrSearchVal != null) {
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PPONO"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("ppoNo", lStrSearchVal);
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PNSRNAME"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("firstName", '%' + lStrSearchVal + '%');
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.TREASURYNAME"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("locationCode", lStrSearchVal);
							}
						}
						hqlQuery.setParameter("status", gObjRsrcBndle.getString("STATUS.TRANSFERED"));
						hqlQuery.setParameter("toLocation", gStrLocationCode);
					} else {
						hqlQuery.setParameter("status", gObjRsrcBndle.getString("STATUS.TRANSFERED"));
						hqlQuery.setParameter("toLocation", gStrLocationCode);
					}
				}
				if (lStrTransferCase.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.REJECT"))) {
					if (lStrCmbSearchBy != null && lStrCmbSearchBy.length() > 0 && lStrSearchVal != null) {
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PPONO"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("ppoNo", lStrSearchVal);
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PNSRNAME"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("firstName", '%' + lStrSearchVal + '%');
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.TREASURYNAME"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("locationCode", lStrSearchVal);
							}
						}
						hqlQuery.setParameter("status", gObjRsrcBndle.getString("STATUS.REJECTED"));
						hqlQuery.setParameter("fromLocation", gStrLocationCode);
					} else {
						hqlQuery.setParameter("status", gObjRsrcBndle.getString("STATUS.REJECTED"));
						hqlQuery.setParameter("fromLocation", gStrLocationCode);
					}
				}
			}
			hqlQuery.setParameter("langId", gLngLangId);

			Integer pageNo = (displaytag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displaytag.get(Constants.KEY_PAGE_NO) : 1);
			hqlQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PDWL_PAGE_SIZE);
			hqlQuery.setMaxResults(Constants.PDWL_PAGE_SIZE);

			lLstTransferCases = hqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstTransferCases;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getTransferCasesCount(java.
	 * lang.Long, java.lang.String, java.lang.String)
	 */
	public Integer getTransferCasesCount(Long gLngLangId, String gStrLocationCode, String lStrCmbSearchBy, String lStrSearchVal, String lStrTransferCase, Map displaytag) throws Exception {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		Integer lIntCount = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT count(*) ");
			lSBQuery.append(" FROM TrnPensionTransferDtls transfer,MstPensionerHdr pension,CmnLocationMst loc ");
			//lSBQuery.append(" ,MstPensionerDtls pnsnrdtls ");
			lSBQuery.append(" WHERE transfer.pensionerCode = pension.pensionerCode " );
			//lSBQuery.append(" and transfer.pensionerCode = pnsnrdtls.pensionerCode AND pension.pensionerCode =  pnsnrdtls.pensionerCode ");
			if (lStrTransferCase != null && lStrTransferCase.length() > 0) {
				if (lStrTransferCase.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.RECEIVE"))) {
					if (lStrCmbSearchBy != null && lStrCmbSearchBy.length() > 0 && lStrSearchVal != null) {
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PPONO"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND transfer.ppoNo =:ppoNo ");
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PNSRNAME"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND pension.firstName like :firstName ");
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.TREASURYNAME"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND loc.locationCode =:locationCode");
							}
						}
						lSBQuery.append(" AND loc.locationCode = transfer.fromLocation   AND loc.cmnLanguageMst.langId = :langId ");
						lSBQuery.append(" AND transfer.toLocation =:toLocation  AND transfer.status =:status ");
					} else {
						lSBQuery.append(" AND loc.locationCode = transfer.fromLocation   AND loc.cmnLanguageMst.langId = :langId ");
						lSBQuery.append(" AND transfer.toLocation =:toLocation  AND transfer.status =:status ");
					}

				}
				if (lStrTransferCase.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.REJECT"))) {

					if (lStrCmbSearchBy != null && lStrCmbSearchBy.length() > 0 && lStrSearchVal != null) {
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PPONO"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND transfer.ppoNo =:ppoNo ");
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PNSRNAME"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND pension.firstName like :firstName ");
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.TREASURYNAME"))) {
							if (lStrSearchVal.length() > 0) {
								lSBQuery.append(" AND loc.locationCode =:locationCode");
							}
						}
						lSBQuery.append("  AND loc.locationCode = transfer.toLocation   AND loc.cmnLanguageMst.langId = :langId ");
						lSBQuery.append("  AND transfer.fromLocation =:fromLocation  AND transfer.status =:status ");
					} else {
						lSBQuery.append("  AND loc.locationCode = transfer.toLocation   AND loc.cmnLanguageMst.langId = :langId ");
						lSBQuery.append("  AND transfer.fromLocation =:fromLocation  AND transfer.status =:status ");
					}
				}
			}
			lSBQuery.append("  AND transfer.receivePostId  IS NULL  AND transfer.receiveDate IS NULL AND transfer.updatedUserId IS NOT NULL ");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			if (lStrTransferCase != null && lStrTransferCase.length() > 0) {
				if (lStrTransferCase.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.RECEIVE"))) {
					if (lStrCmbSearchBy != null && lStrCmbSearchBy.length() > 0 && lStrSearchVal != null) {
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PPONO"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("ppoNo", lStrSearchVal);
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PNSRNAME"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("firstName", '%' + lStrSearchVal + '%');
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.TREASURYNAME"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("locationCode", lStrSearchVal);
							}
						}
						hqlQuery.setParameter("status", gObjRsrcBndle.getString("STATUS.TRANSFERED"));
						hqlQuery.setParameter("toLocation", gStrLocationCode);
					} else {
						hqlQuery.setParameter("status", gObjRsrcBndle.getString("STATUS.TRANSFERED"));
						hqlQuery.setParameter("toLocation", gStrLocationCode);
					}
				}
				if (lStrTransferCase.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("PPMT.REJECT"))) {
					if (lStrCmbSearchBy != null && lStrCmbSearchBy.length() > 0 && lStrSearchVal != null) {
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PPONO"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("ppoNo", lStrSearchVal);
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.PNSRNAME"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("firstName", '%' + lStrSearchVal + '%');
							}
						}
						if (lStrCmbSearchBy.equals(ResourceBundle.getBundle("resources/pensionpay/PensionCaseLabels").getString("SEARCH.TREASURYNAME"))) {
							if (lStrSearchVal.length() > 0) {
								hqlQuery.setParameter("locationCode", lStrSearchVal);
							}
						}
						hqlQuery.setParameter("status", gObjRsrcBndle.getString("STATUS.REJECTED"));
						hqlQuery.setParameter("fromLocation", gStrLocationCode);
					} else {
						hqlQuery.setParameter("status", gObjRsrcBndle.getString("STATUS.REJECTED"));
						hqlQuery.setParameter("fromLocation", gStrLocationCode);
					}
				}
			}
			hqlQuery.setParameter("langId", gLngLangId);

			if (hqlQuery.list() != null && hqlQuery.list().size() > 0) {
				lIntCount = Integer.parseInt(hqlQuery.list().get(0).toString());
			} else {
				lIntCount = 0;
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lIntCount;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getRegNoFromMstPensionerDtls
	 * (java.lang.String)
	 */

	public String getRegNoFromMstPensionerDtls(String lStrPensionerCode) {

		String lStrRegNo = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		Session lHibSession = null;
		try {
			lHibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT  dtls.registrationNo FROM MstPensionerDtls dtls WHERE dtls.pensionerCode =:pensionerCode \n");
			lHibQry = lHibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("pensionerCode", lStrPensionerCode);

			lStrRegNo = lHibQry.list().get(0).toString();

		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lStrRegNo;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getAuditorPostIdFromBnkBrnchCode
	 * (java.lang.String)
	 */

	public Long getAuditorPostIdFromBnkBrnchCode(String lStrBranchCode) {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		Session lHibSession = null;
		Long lLngAuditorPostId = null;
		try {
			lHibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT audi.postId FROM RltAuditorBank audi WHERE audi.branchCode =:branchCode ");
			lHibQry = lHibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("branchCode", lStrBranchCode);
			if (!lHibQry.list().isEmpty()) {
				lLngAuditorPostId = Long.valueOf(lHibQry.list().get(0).toString());
			} else {
				lLngAuditorPostId = 0l;
			}

		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
		return lLngAuditorPostId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getAllBnkBrnchCodes(java.lang
	 * .String)
	 */

	public List getAllBrnchCodes(String lStrLocationCode) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstBrnchCodes = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT audi.branchCode ");
			lSBQuery.append(" FROM RltAuditorBank audi ");
			lSBQuery.append(" WHERE audi.locationCode =:locationCode");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			lLstBrnchCodes = hqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstBrnchCodes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getAllPPONo(java.lang.String)
	 */

	public List getAllPPONo() {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstPPONo = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT rqst.ppoNo ");
			lSBQuery.append(" FROM  TrnPensionRqstHdr rqst  ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstPPONo = hqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstPPONo;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#setDtlsInHstPnsnPmntPpoNo(java
	 * .lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	public HstPnsnPmntPpoNo setDtlsInHstPnsnPmntPpoNo(HstPnsnPmntPpoNo lObjHstPnsnPmntPpoNo, Map inputMap, String strLocationCode, String strNewPPONo, String strOldPPONo, String lStrPensionerCode) {

		lObjHstPnsnPmntPpoNo.setDbId(BigDecimal.valueOf(SessionHelper.getDbId(inputMap)));
		lObjHstPnsnPmntPpoNo.setLocationCode(Long.valueOf(strLocationCode));
		lObjHstPnsnPmntPpoNo.setPensionerCode(lStrPensionerCode);
		lObjHstPnsnPmntPpoNo.setOldppoNo(strOldPPONo);
		lObjHstPnsnPmntPpoNo.setNewppoNo(strNewPPONo);
		lObjHstPnsnPmntPpoNo.setRemarks("TRANSFER OUTSIDE AG");
		lObjHstPnsnPmntPpoNo.setCreatedUserId(BigDecimal.valueOf(SessionHelper.getUserId(inputMap)));
		lObjHstPnsnPmntPpoNo.setCreatedPostId(BigDecimal.valueOf(SessionHelper.getPostId(inputMap)));
		lObjHstPnsnPmntPpoNo.setCreatedDate(DBUtility.getCurrentDateFromDB());
		lObjHstPnsnPmntPpoNo.setUpdatedDate(DBUtility.getCurrentDateFromDB());
		lObjHstPnsnPmntPpoNo.setUpdatedPostId(BigDecimal.valueOf(SessionHelper.getPostId(inputMap)));
		lObjHstPnsnPmntPpoNo.setUpdatedUserId(BigDecimal.valueOf(SessionHelper.getUserId(inputMap)));
		return lObjHstPnsnPmntPpoNo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getPensionerCodeFromPpoNo(java
	 * .lang.String)
	 */

	public String getPensionerCodeFromPpoNo(String strArrOldPPONo) {

		String lStrPensionerCode = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstPPONo = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT rqst.pensionerCode ");
			lSBQuery.append(" FROM  TrnPensionRqstHdr rqst WHERE rqst.ppoNo = :ppoNo ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("ppoNo", strArrOldPPONo);
			lLstPPONo = hqlQuery.list();
			if (lLstPPONo.size() > 0) {
				lStrPensionerCode = lLstPPONo.get(0).toString().trim();
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrPensionerCode;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.TransferPPODAO#getAllPnsnrCodes()
	 */

	public List<String> getAllPnsnrCodes(String lStrLocationCode) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List<String> lLstPnsnsrCodes = null;

		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT pensionerCode FROM TrnPensionTransferDtls WHERE fromLocation = :fromLocation ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("fromLocation", lStrLocationCode.trim());
			lLstPnsnsrCodes = hqlQuery.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstPnsnsrCodes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getTransferLetterDetails(java
	 * .lang.String, java.lang.String)
	 */

	public List getTransferLetterDetails(String lStrPnsnrCode, String lStrLocationCode, String lStrOtherStateName) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstTransferLetterDtls = null;
		List<String> lLstTransferStatus = new ArrayList<String>();
		lLstTransferStatus.add(gObjRsrcBndle.getString("STATFLG.APPROVED"));
		lLstTransferStatus.add(gObjRsrcBndle.getString("STATUS.RQSTREJECT"));
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			/*
			 * SELECT
			 * hdr.first_name,rqst.ppo_no,loc.loc_name,rqst.rop_type,rqst.
			 * basic_pension_amount
			 * ,transfer.to_location,loc.loc_state_id,hdr.pensner_addr
			 * ,loc1.loc_name FROM mst_pensioner_hdr
			 * hdr,trn_pension_transfer_dtls transfer,trn_pension_rqst_hdr
			 * rqst,cmn_location_mst loc,cmn_location_mst loc1 WHERE
			 * hdr.pensioner_code = transfer.pensioner_code AND
			 * rqst.pensioner_code = hdr.pensioner_code AND rqst.pensioner_code
			 * = transfer.pensioner_code AND transfer.to_location =
			 * loc.location_code AND AND loc1.location_code = hdr.location_code
			 * AND hdr.pensioner_code = 99100002618 AND transfer.from_location =
			 * 7101
			 */

			lSBQuery.append(" SELECT hdr.firstName,rqst.ppoNo,loc.locName,rqst.ropType,rqst.basicPensionAmount,hdr.pensionerAddr,loc1.locName,loc.locationCode ");
			lSBQuery.append(" FROM MstPensionerHdr hdr,TrnPensionTransferDtls transfer,TrnPensionRqstHdr rqst,CmnLocationMst loc,CmnLocationMst loc1 ");
			lSBQuery.append(" WHERE transfer.pensionerCode = hdr.pensionerCode AND rqst.pensionerCode = hdr.pensionerCode AND rqst.pensionerCode = transfer.pensionerCode ");
			lSBQuery.append(" AND loc1.locationCode = hdr.locationCode ");
			lSBQuery.append(" and transfer.status not in (:lLstTransferStatus) \n");
			if (lStrOtherStateName != null && lStrOtherStateName.length() > 0) {
				lSBQuery.append(" AND rqst.locationCode = loc.locationCode ");
			} else {
				lSBQuery.append(" AND transfer.toLocation = loc.locationCode ");
			}

			lSBQuery.append(" AND hdr.pensionerCode = :pensionerCode AND transfer.fromLocation = :locationCode ");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			hqlQuery.setParameter("pensionerCode", lStrPnsnrCode);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			hqlQuery.setParameterList("lLstTransferStatus", lLstTransferStatus);

			lLstTransferLetterDtls = hqlQuery.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstTransferLetterDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getDistrictName(java.lang.String
	 * , java.lang.String)
	 */

	public String getDistrictName(String lStrTreasuryName) {

		String lStrDistrictName = "";
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {

			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT dist.districtName ");
			lSBQuery.append(" FROM  CmnLocationMst loc,CmnDistrictMst dist ");
			lSBQuery.append(" WHERE loc.locDistrictId = dist.districtId ");
			lSBQuery.append(" AND loc.locName = :locName ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("locName", lStrTreasuryName);

			if (hqlQuery.list().size() > 0) {
				lStrDistrictName = hqlQuery.list().get(0).toString().trim();
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrDistrictName;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#getBillAmntDtls(java.lang.String
	 * )
	 */

	public List getBillAmntDtls(String lStrPnsnrCode,List<Short> lLstBillStatus) {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstBillAmntDtls = new ArrayList();
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			/*
			 * SELECT da_rate,ti_amount,cvp_month_amount,pay_for_month FROM
			 * trn_pension_bill_dtls WHERE pensioner_code = 99100002610
			 */
			lSBQuery.append("select tpd from \n");
			lSBQuery.append("TrnBillRegister tbr,TrnPensionBillHdr tbh,TrnPensionBillDtls tpd \n");
			lSBQuery.append("where tbr.billNo = tbh.billNo \n");
			lSBQuery.append("and tbh.trnPensionBillHdrId = tpd.trnPensionBillHdrId \n");
			lSBQuery.append("and tbr.currBillStatus not in (:billStatusList) and tpd.pensionerCode = :pensionerCode order by tpd.payForMonth desc \n");
			
			//lSBQuery.append("SELECT * FROM TrnPensionBillDtls WHERE pensionerCode = :pensionerCode order by payForMonth desc");

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("pensionerCode", lStrPnsnrCode);
			hqlQuery.setParameterList("billStatusList", lLstBillStatus);
			lLstBillAmntDtls = hqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstBillAmntDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.TransferPPODAO#checkTransferType(java.lang
	 * .String, java.lang.String)
	 */

	public String checkTransferType(String lStrPnsnrCode, String lStrLocationCode, String lStrRequestId) {

		String lStrOtherStateName = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstResult = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT transfer.otherStateName ");
			lSBQuery.append(" FROM  TrnPensionTransferDtls transfer ");
			lSBQuery.append(" Where \n");
			lSBQuery.append(" transfer.fromLocation = :fromLocation ");
			if (lStrPnsnrCode != null) {
				lSBQuery.append(" and transfer.pensionerCode = :pensionerCode");
			}
			if (lStrRequestId != null) {
				lSBQuery.append(" and transfer.requestId = :requestId ");
			}

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			if (lStrPnsnrCode != null) {
				hqlQuery.setParameter("pensionerCode", lStrPnsnrCode.trim());
			}
			if (lStrRequestId != null) {
				hqlQuery.setParameter("requestId", lStrRequestId.trim());
			}

			hqlQuery.setParameter("fromLocation", lStrLocationCode);
			lLstResult = hqlQuery.list();
			if ((lLstResult != null) && (lLstResult.size() > 0)) {
				if (lLstResult.get(0) != null) {
					lStrOtherStateName = lLstResult.get(0).toString().trim();
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrOtherStateName;

	}

	public List getDatesFromTrnPensionRqstHdr(String lStrPensionerCode) throws Exception {

		List lLstTempList = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT rqst.ppoInwardDate,rqst.ppoRegDate,rqst.commensionDate ");
			lSBQuery.append(" FROM TrnPensionRqstHdr rqst WHERE rqst.pensionerCode = :pensionerCode ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("pensionerCode", lStrPensionerCode);
			lLstTempList = hqlQuery.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLstTempList;
	}

	public Long getMaxRequestId(String locationCode) throws Exception {

		Long llngRequestId = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT MAX(requestId) ");
			lSBQuery.append(" FROM  TrnPensionTransferDtls transfer");
			lSBQuery.append(" WHERE transfer.fromLocation =:fromLocation  ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("fromLocation", locationCode);
			if (hqlQuery.list().get(0) == null) {
				llngRequestId = 0l;
			} else {
				llngRequestId = Long.valueOf(hqlQuery.list().get(0).toString());
			}
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return llngRequestId;
	}

	public List getTransferDtlsFromReqId(String lStrRequestId, String lStrLocationCode, String lStrOtherStateName) throws Exception {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstTransferLetterDtls = null;
		try {
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT transfer.pensionerCode,transfer.ppoNo,hdr.firstName,loc.locName,");
			lSBQuery.append(" transfer.agFlag,transfer.otherStateName,transfer.toLocation,rqst.caseStatus,loc1.locName,transfer.transferDtlsId,loc2.locName,loc2.locationCode ");
			lSBQuery.append(" FROM MstPensionerHdr hdr,TrnPensionTransferDtls transfer,TrnPensionRqstHdr rqst,CmnLocationMst loc,CmnLocationMst loc1,CmnLocationMst loc2 ");
			lSBQuery.append(" WHERE transfer.pensionerCode = hdr.pensionerCode AND rqst.pensionerCode = hdr.pensionerCode AND rqst.pensionerCode = transfer.pensionerCode ");
			lSBQuery.append(" AND loc.locationCode = rqst.locationCode AND transfer.status = :status ");
			lSBQuery.append(" AND transfer.requestId = :requestId AND transfer.fromLocation = :locationCode ");
			lSBQuery.append(" AND loc1.officeCode = loc2.locationCode ");
			if (lStrOtherStateName != null && lStrOtherStateName.length() > 0) {
				lSBQuery.append(" AND transfer.fromLocation = loc1.locationCode ");
			} else {
				lSBQuery.append(" AND transfer.toLocation = loc1.locationCode ");
			}

			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("status", gObjRsrcBndle.getString("STATUS.TRANSFERED"));
			hqlQuery.setParameter("requestId", lStrRequestId.trim());
			hqlQuery.setParameter("locationCode", lStrLocationCode.trim());

			lLstTransferLetterDtls = hqlQuery.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstTransferLetterDtls;
	}

}
