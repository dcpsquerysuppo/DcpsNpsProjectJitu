/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	May 13, 2011		Bhargav Trivedi								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.common.valueobject.MstScheme;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionHeadcode;
import com.tcs.sgv.pensionpay.valueobject.MstPensionerDtls;
import com.tcs.sgv.pensionpay.valueobject.RltAuditorBank;


/**
 * Class Description -
 * 
 * 
 * @author Bhargav Trivedi
 * @version 0.1
 * @since JDK 5.0 May 13, 2011
 */
public class PensionConfigDAOImpl extends GenericDaoHibernateImpl implements PensionConfigDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	SessionFactory sessionFactory = null;

	public PensionConfigDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionPaymentAdminDAO#getAuditorName(java
	 * .util.Map, java.lang.String)
	 */
	public List getAuditorName(Map inputMap, String lStrLocCode) throws Exception {

		List<Object> lLstAuditors = null;
		List lLstResult = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT oup.orgPostMstByPostId.postId,concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname) \n");
			lSBQuery.append(" FROM AclRoleMst arm,AclPostroleRlt apr,OrgUserpostRlt oup,OrgEmpMst oem, OrgPostMst opm \n");
			lSBQuery.append(" WHERE arm.roleId=apr.aclRoleMst.roleId \n");
			lSBQuery.append("AND oup.orgPostMstByPostId.postId=apr.orgPostMst.postId \n");
			lSBQuery.append("AND oup.orgUserMst.userId = oem.orgUserMst.userId \n");
			lSBQuery.append("AND oup.orgPostMstByPostId.postId=opm.postId\n");
			lSBQuery.append("AND arm.roleId=:roleId \n");
			lSBQuery.append("AND opm.locationCode=:locationCode \n");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("locationCode", lStrLocCode);
			lHibQry.setParameter("roleId", Long.valueOf(gObjRsrcBndle.getString("PPMT.AUDITORROLE")));
			lLstResult = lHibQry.list();
			ComboValuesVO lObjComboValuesVO = null;

			if (lLstResult != null && lLstResult.size() != 0) {
				lLstAuditors = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstAuditors.add(lObjComboValuesVO);
				}
			} else {
				lLstAuditors = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstAuditors.add(lObjComboValuesVO);
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}

		return lLstAuditors;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionPaymentAdminDAO#getBankNames(java.lang
	 * .Long, java.lang.String)
	 */
	public List getBankNames(Long lLngLangId, String lStrLocCode) throws Exception {

		List<Object> lLstBankNames = null;
		List lLstResult = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT MB.bankCode,MB.bankName FROM MstBank MB where MB.langId = :langId ");
			lSBQuery.append(" Order By MB.bankName ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", lLngLangId);
			// lHibQry.setParameter("locationCode", lStrLocCode);

			lLstResult = lHibQry.list();
			ComboValuesVO lObjComboValuesVO = null;

			if (lLstResult != null && lLstResult.size() != 0) {
				lLstBankNames = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstBankNames.add(lObjComboValuesVO);
				}
			} else {
				lLstBankNames = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstBankNames.add(lObjComboValuesVO);
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}

		return lLstBankNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionPaymentAdminDAO#validateMapping(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	public Boolean validateMapping(String lStrPostId, String lStrBankCode, String lStrBranchCode, String lStrLocationCode) {

		List lLstValidateMappings = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" FROM RltAuditorBank auditbank ");
			lSBQuery.append(" WHERE auditbank.bankCode = :bankCode AND auditbank.branchCode = :branchCode ");
			// lSBQuery.append(" AND auditbank.postId = :postId AND auditbank.locationCode = :locationCode ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("bankCode", lStrBankCode);
			lHibQry.setParameter("branchCode", lStrBranchCode);
			// lHibQry.setParameter("postId", Long.valueOf(lStrPostId));
			// lHibQry.setParameter("locationCode", lStrLocationCode);
			lLstValidateMappings = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		if (lLstValidateMappings != null && lLstValidateMappings.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionPaymentAdminDAO#getMappingDtls(java
	 * .lang.Long, java.lang.String, java.lang.String)
	 */
	public List getMappingDtls(Long lLngAuditorPostId, String lStrBankCode, String gStrLocationCode) throws Exception {

		List lLstMappingsDtls = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT branch.branchCode,branch.branchName FROM RltAuditorBank audit,RltBankBranch branch ");
			lSBQuery.append(" WHERE audit.branchCode = branch.branchCode AND audit.bankCode = branch.bankCode  AND audit.bankCode = :bankCode AND audit.postId = :postId AND audit.locationCode = :locationCode ");
			lSBQuery.append(" Order By branch.branchName,branch.branchCode ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("bankCode", lStrBankCode);
			lHibQry.setParameter("postId", lLngAuditorPostId);
			lHibQry.setParameter("locationCode", gStrLocationCode);
			lLstMappingsDtls = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstMappingsDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionPaymentAdminDAO#removeMapping(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	public void removeMapping(String lStrPostId, String lStrBankCode, String lStrArrBranchCodeVal, String lStrLocationCode) throws Exception {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" DELETE FROM RltAuditorBank audit");
			lSBQuery.append(" WHERE audit.branchCode = :branchCode AND audit.bankCode = :bankCode AND audit.postId = :postId ");
			// lSBQuery.append(" AND audit.locationCode = :locationCode ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("branchCode", lStrArrBranchCodeVal);
			lHibQry.setParameter("bankCode", lStrBankCode);
			lHibQry.setParameter("postId", Long.valueOf(lStrPostId));
			// lHibQry.setParameter("locationCode", lStrLocationCode);
			lHibQry.executeUpdate();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionPaymentAdminDAO#getRemarksOfBill(java
	 * .lang.Long, java.lang.String, java.lang.Long)
	 */
	public List getRemarksOfBill(Long lLngBillNo, String gStrLocationCode, Long gLngLangId) {

		List lLstTotalUsers = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {

			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT concat(concat(concat(concat(e.empFname,' '),e.empMname),' '),e.empLname),rd.roleName,m.billRemarks FROM TrnBillMvmnt m,OrgUserMst u, \n");
			lSBQuery.append(" OrgEmpMst e,OrgUserpostRlt up, AclPostroleRlt pr,AclRoleDetailsRlt rd,OrgUserMst oum \n");
			lSBQuery.append(" WHERE m.billNo = :billNo AND m.locationCode =:locationCode  \n");
			lSBQuery.append(" AND m.statusUpdtUserid = u.userId AND u.userId = e.orgUserMst.userId \n");
			lSBQuery.append(" AND u.userId = up.orgUserMst.userId AND up.activateFlag=1  AND pr.orgPostMst.postId = up.orgPostMstByPostId.postId AND pr.aclRoleMst.roleId = rd.aclRoleMst.roleId \n ");
			lSBQuery.append(" GROUP BY rd.roleName ORDER BY m.movemntId,m.mvmntStatus   \n");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("billNo", lLngBillNo);
			// lHibQry.setParameter("postId", gLngLangId);
			lHibQry.setParameter("locationCode", gStrLocationCode);
			lLstTotalUsers = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstTotalUsers;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getFinYearIdFromCurrDt(java
	 * .lang.String)
	 */
	public Long getFinYearIdFromCurrDt(String lStrCurYear) {

		Long lLngFinYearId = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT sfym.finYearId FROM SgvcFinYearMst sfym where sfym.finYearCode =:finYearCode ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("finYearCode", lStrCurYear);
			lLngFinYearId = Long.valueOf(lHibQry.list().get(0).toString());
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLngFinYearId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getHeadCodeConfigDtls(java
	 * .lang.Long, java.util.Map)
	 */
	public List getHeadCodeConfigDtls(Long gLngLangId, Map displayTag) {

		List lLstHeadCodeConfigDtls = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {

			/*
			 * SELECT
			 * mst.pension_headcode_id,rlt1.pension_headcode_chargable_id,
			 * mst.head_code,mst.description, rlt1.scheme_code AS
			 * pension,rlt2.scheme_code AS CVP,rlt3.scheme_code AS DCRG FROM
			 * mst_pension_headcode mst,rlt_pension_headcode_chargable
			 * rlt1,rlt_pension_headcode_chargable
			 * rlt2,rlt_pension_headcode_chargable rlt3 WHERE mst.head_code =
			 * rlt1.head_code AND mst.head_code = rlt2.head_code AND
			 * mst.head_code = rlt3.head_code AND rlt1.head_code =
			 * rlt2.head_code AND rlt1.head_code = rlt3.head_code AND
			 * rlt2.head_code = rlt3.head_code AND mst.lang_id = 1 AND
			 * rlt1.scheme_code IS NOT NULL AND rlt1.bill_type ='PENSION' AND
			 * rlt2.bill_type = 'CVP' AND rlt3.bill_type = 'DCRG'
			 */

			lSBQuery = new StringBuffer();
			String[] columnValues = new String[]{"", "mst.pensionHeadcodeId", "rlt1.pensionHeadcodeChargableId", "mst.headCode", "mst.description", "rlt1.schemeCode", "rlt2.schemeCode",
					"rlt3.schemeCode", "mst.series", "mst.mainCatDesc"};
			lSBQuery.append(" SELECT mst.pensionHeadcodeId,rlt1.pensionHeadcodeChargableId,mst.headCode,mst.description,rlt1.schemeCode,rlt2.schemeCode,rlt3.schemeCode,mst.series,mst.mainCatDesc \n");
			lSBQuery.append(" FROM MstPensionHeadcode mst,RltPensionHeadcodeChargable rlt1,RltPensionHeadcodeChargable rlt2,RltPensionHeadcodeChargable rlt3 \n");
			lSBQuery.append(" WHERE mst.headCode = rlt1.headCode AND mst.headCode = rlt2.headCode AND mst.headCode = rlt3.headCode AND mst.langId =:langId  \n");
			lSBQuery.append(" AND rlt1.headCode = rlt2.headCode AND rlt2.headCode = rlt3.headCode AND rlt1.headCode = rlt3.headCode  \n");
			lSBQuery.append(" AND rlt1.billType =:pension AND rlt2.billType =:cvp  AND rlt3.billType =:dcrg  \n");
			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append(" ORDER BY mst.headCode");
			}
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", BigDecimal.valueOf(gLngLangId));
			lHibQry.setParameter("pension", gObjRsrcBndle.getString("PPMT.PENSION1"));
			lHibQry.setParameter("cvp", gObjRsrcBndle.getString("PPMT.CVP"));
			lHibQry.setParameter("dcrg", gObjRsrcBndle.getString("PPMT.DCRG"));

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lHibQry.setFirstResult((pageNo.intValue() - 1) * Constants.PDWL_PAGE_SIZE);
			lHibQry.setMaxResults(Constants.PDWL_PAGE_SIZE);
			lLstHeadCodeConfigDtls = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstHeadCodeConfigDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getHeadCodeConfigDtlsCount
	 * (java.lang.Long, java.util.Map)
	 */
	public Integer getHeadCodeConfigDtlsCount(Long gLngLangId, Map displayTag) {

		Integer lIntCount = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT COUNT(*) ");
			lSBQuery.append(" FROM MstPensionHeadcode mst,RltPensionHeadcodeChargable rlt1,RltPensionHeadcodeChargable rlt2,RltPensionHeadcodeChargable rlt3 \n");
			lSBQuery.append(" WHERE mst.headCode = rlt1.headCode AND mst.headCode = rlt2.headCode AND mst.headCode = rlt3.headCode AND mst.langId =:langId  \n");
			lSBQuery.append(" AND rlt1.headCode = rlt2.headCode AND rlt2.headCode = rlt3.headCode AND rlt1.headCode = rlt3.headCode  \n");
			lSBQuery.append(" AND rlt1.billType =:pension AND rlt2.billType =:cvp  AND rlt3.billType =:dcrg  \n");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", BigDecimal.valueOf(gLngLangId));
			lHibQry.setParameter("pension", gObjRsrcBndle.getString("PPMT.PENSION1"));
			lHibQry.setParameter("cvp", gObjRsrcBndle.getString("PPMT.CVP"));
			lHibQry.setParameter("dcrg", gObjRsrcBndle.getString("PPMT.DCRG"));
			if (lHibQry.list() != null && lHibQry.list().size() > 0) {
				lIntCount = Integer.parseInt(lHibQry.list().get(0).toString());
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
	 * com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getRltPensionHeadcodeChargableVO
	 * (java.math.BigDecimal)
	 */
	public List getRltPensionHeadcodeChargableVO(String lStrHeadCode) {

		List lLstRltPensionHeadcodeChargableVO = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" FROM RltPensionHeadcodeChargable WHERE headCode =:headCode ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("headCode", Long.valueOf(lStrHeadCode));
			lLstRltPensionHeadcodeChargableVO = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstRltPensionHeadcodeChargableVO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getMstPensionHeadcodeVO(java
	 * .lang.String)
	 */
	public MstPensionHeadcode getMstPensionHeadcodeVO(String lStrSeries) {

		MstPensionHeadcode lObjMstPensionHeadcodeVO = new MstPensionHeadcode();
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" FROM MstPensionHeadcode WHERE series =:series ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("series", lStrSeries.trim());
			lObjMstPensionHeadcodeVO = (MstPensionHeadcode) lHibQry.list().get(0);
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lObjMstPensionHeadcodeVO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionConfigDAO#delRltPensionHeadcodeChargableVO
	 * (java.math.BigDecimal)
	 */
	public void delRltPensionHeadcodeChargableVO(BigDecimal lBDHeadCode) {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" DELETE FROM RltPensionHeadcodeChargable rlt WHERE rlt.headCode = :headCode ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("headCode", lBDHeadCode.longValue());
			lHibQry.executeUpdate();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getHeadCodeNos(java.lang.
	 * Long)
	 */
	public List getHeadCodeNos() {

		List<BigDecimal> lLstHeadCode = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT main.headCode FROM TrnPensionRqstHdr main GROUP BY  main.headCode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lLstHeadCode = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstHeadCode;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionConfigDAO#currMappingDtls(java.lang
	 * .String, java.lang.Long, java.lang.String)
	 */
	public List getCurrMappingDtls(String gStrLocationCode, Long gLngLangId, String lStrAuditorPostId) {

		List lLstCurrMappingDtls = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname),bank.bankName,branch.branchName \n");
			lSBQuery.append(" FROM AclRoleMst arm,AclPostroleRlt apr,OrgUserpostRlt oup,MstBank bank,RltBankBranch branch,RltAuditorBank audit,OrgEmpMst oem, OrgPostMst opm \n");
			lSBQuery.append(" WHERE branch.branchCode = audit.branchCode AND branch.bankCode = bank.bankCode AND audit.bankCode = bank.bankCode \n");
			lSBQuery.append(" AND arm.roleId=apr.aclRoleMst.roleId AND oup.orgPostMstByPostId.postId=apr.orgPostMst.postId \n");
			lSBQuery.append(" AND oup.orgUserMst.userId = oem.orgUserMst.userId \n");
			lSBQuery.append(" AND oup.orgPostMstByPostId.postId=opm.postId AND audit.postId = oup.orgPostMstByPostId.postId  \n");
			lSBQuery.append(" AND audit.postId = apr.orgPostMst.postId AND audit.postId = opm.postId \n");
			lSBQuery.append(" AND arm.roleId=:roleId AND audit.postId =:postId AND bank.langId = branch.langId \n");
			lSBQuery.append(" AND opm.locationCode=:locationCode AND audit.locationCode =:locationCode AND bank.langId =:langId \n");
			lSBQuery.append(" Order By bank.bankName,branch.branchName \n");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setLong("langId", gLngLangId);
			lHibQry.setParameter("postId", Long.valueOf(lStrAuditorPostId));
			lHibQry.setParameter("locationCode", gStrLocationCode);
			lHibQry.setParameter("roleId", Long.valueOf(gObjRsrcBndle.getString("PPMT.AUDITORROLE")));
			lLstCurrMappingDtls = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstCurrMappingDtls;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getMaxHeadCode()
	 */

	public BigDecimal getMaxHeadCode() throws Exception {

		BigDecimal lBDMaxHeadCode = BigDecimal.ZERO;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT MAX(headCode) FROM MstPensionHeadcode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			if (lHibQry.list() != null && lHibQry.list().size() > 0) {
				lBDMaxHeadCode = BigDecimal.valueOf(Long.valueOf((lHibQry.list().get(0).toString())));
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lBDMaxHeadCode;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.MonthlyPensionBillDAO#removeCtgryIfExists(
	 * java.lang.String[], java.lang.String)
	 */

	public void removeCtgryIfExists(String lStrLocCode) {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" DELETE FROM MstChangeStmntCtgry change");
			lSBQuery.append(" WHERE change.locationCode = :locationCode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("locationCode", Long.valueOf(lStrLocCode));
			lHibQry.executeUpdate();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
		}

	}

	public List<Integer> getSelectedChngStmntCatgry(Long lLngLocCode) throws Exception {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		List<Integer> lLstCategory = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" select mcs.categoryId  \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstChangeStmntCtgry mcs \n");
			lSBQuery.append("where mcs.locationCode = :locationCode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("locationCode", lLngLocCode);
			lLstCategory = lHibQry.list();
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstCategory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getPPODetails(java.lang.String
	 * , java.lang.Long, java.lang.String)
	 */

	public List getPPODetails(String lStrPPONo, String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstPPODetails = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT hdr.firstName,req.pensionerCode,bank.bankName,branch.branchName,dtls.accountNo ");
			lSBQuery.append(" FROM TrnPensionRqstHdr req,MstPensionerDtls dtls,MstPensionerHdr hdr,MstBank bank,RltBankBranch branch ");
			lSBQuery.append(" WHERE req.pensionerCode = dtls.pensionerCode AND req.pensionerCode = hdr.pensionerCode AND dtls.pensionerCode = hdr.pensionerCode ");
			lSBQuery.append(" AND bank.bankCode = dtls.bankCode AND bank.bankCode = branch.bankCode AND branch.branchCode= dtls.branchCode ");
			lSBQuery.append(" AND req.ppoNo = :ppoNo AND req.locationCode= :locationCode ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("ppoNo", lStrPPONo);
			hqlQuery.setParameter("locationCode", lStrLocationCode);
			lLstPPODetails = hqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstPPODetails;

	}

	public List getPPONoHistory(String lStrPensionerCode) throws Exception {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List lLstPPONoHistory = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT  hst.oldppoNo,hst.newppoNo,hst.updatedDate ");
			lSBQuery.append(" FROM HstPnsnPmntPpoNo hst");
			lSBQuery.append(" WHERE hst.pensionerCode = :pensionerCode ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("pensionerCode", lStrPensionerCode.trim());
			lLstPPONoHistory = hqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstPPONoHistory;

	}

	@SuppressWarnings("unchecked")
	public List<Integer> getChangeStmntCtgry(String lStrLocationCode) throws Exception {

		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		List<Integer> lLstChangeStmntCtgryConfig = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT  ctgry.categoryId ");
			lSBQuery.append(" FROM MstChangeStmntCtgry ctgry");
			lSBQuery.append(" WHERE ctgry.locationCode = :locationCode ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("locationCode", Long.valueOf(lStrLocationCode.trim()));
			lLstChangeStmntCtgryConfig = hqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lLstChangeStmntCtgryConfig;
	}

	public List<ComboValuesVO> getBankGroupOfAuditor(Long lLngAuditPostId, String lStrLocCode) throws Exception {

		List<ComboValuesVO> lLstBankGrp = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO = null;
		StringBuffer lSBQuery = new StringBuffer();
		Query hqlQuery = null;
		try {
			lSBQuery.append("Select \n");
			lSBQuery.append("bankGrpId,groupName \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstBankGrp \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("auditorPostId = :lLngPostId \n");
			lSBQuery.append("and locationCode = :lStrLocCode \n");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setLong("lLngPostId", lLngAuditPostId);
			hqlQuery.setString("lStrLocCode", lStrLocCode);
			List<Object[]> lLstObj = hqlQuery.list();
			for (Object[] lArrObj : lLstObj) {
				cmbVO = new ComboValuesVO();
				cmbVO.setId(lArrObj[0].toString());
				cmbVO.setDesc(lArrObj[1].toString());
				lLstBankGrp.add(cmbVO);
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lLstBankGrp;
	}

	public void deleteBankBranchGrpDtls(Long lLngGrpId) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Query hqlQuery = null;
		try {
			lSBQuery.append("Delete from RltBankBranchGrp where bankGrpId = :lLngBankGrpId ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setLong("lLngBankGrpId", lLngGrpId);
			hqlQuery.executeUpdate();
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
	}

	public StringBuilder getBankGroupDtlsByGrpId(Long lLngGrpId, Long lLngPostId, String lStrLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Query hqlQuery = null;
		String lStrBankName = null;
		String lStrBranchName = null;
		String lStrBankCode = null;
		String lStrBranchCode = null;
		StringBuilder lSbRes = new StringBuilder();
		try {
			lSBQuery.append("Select mb.bankName,rbb.branchName,rbg.bankCode,rbg.branchCode \n");
			lSBQuery.append("From \n");
			lSBQuery.append("RltBankBranchGrp rbg, \n");
			lSBQuery.append("MstBank mb , \n");
			lSBQuery.append("RltBankBranch rbb  \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("rbg.bankCode = mb.bankCode \n");
			lSBQuery.append("and rbg.branchCode = rbb.branchCode \n");
			lSBQuery.append("and rbb.locationCode = :lStrLocCode \n");
			lSBQuery.append("and rbg.bankGrpId = :lLngBankGrpId \n");
			lSBQuery.append("and rbg.auditorPostId = :lLngPostId \n");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setLong("lLngBankGrpId", lLngGrpId);
			hqlQuery.setLong("lLngPostId", lLngPostId);
			hqlQuery.setString("lStrLocCode", lStrLocCode);
			List<Object[]> lLstRes = hqlQuery.list();
			if (lLstRes.size() > 0) {
				lSbRes.append("<XMLDOC>");
				for (Object[] lArrObj : lLstRes) {
					lStrBankName = (lArrObj[0] != null) ? lArrObj[0].toString() : "";
					lStrBranchName = (lArrObj[1] != null) ? lArrObj[1].toString() : "";
					lStrBankCode = (lArrObj[2] != null) ? lArrObj[2].toString() : "";
					lStrBranchCode = (lArrObj[3] != null) ? lArrObj[3].toString() : "";
					lSbRes.append("<GRPDTLS>");
					lSbRes.append("<BANKNAME>");
					lSbRes.append(lStrBankName);
					lSbRes.append("</BANKNAME>");
					lSbRes.append("<BRANCHNAME>");
					lSbRes.append(lStrBranchName);
					lSbRes.append("</BRANCHNAME>");
					lSbRes.append("<BANKCODE>");
					lSbRes.append(lStrBankCode);
					lSbRes.append("</BANKCODE>");
					lSbRes.append("<BRANCHCODE>");
					lSbRes.append(lStrBranchCode);
					lSbRes.append("</BRANCHCODE>");
					lSbRes.append("</GRPDTLS>");
				}
				lSbRes.append("</XMLDOC>");
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lSbRes;
	}

	public String getGrpNameFromBranchCode(String lStrBankCode, String lStrBranchCode, String lStrLocationCode, Long lLngPostId) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Query hqlQuery = null;
		String lStrGrpName = "";
		List<String> lLstGrpName = null;
		try {
			lSBQuery.append("Select mbg.groupName \n");
			lSBQuery.append("From  \n");
			lSBQuery.append("MstBankGrp mbg,RltBankBranchGrp bbg  \n");
			lSBQuery.append("Where  \n");
			lSBQuery.append("mbg.bankGrpId = bbg.bankGrpId  \n");
			lSBQuery.append("and bbg.bankCode = :lStrBankCode  \n");
			lSBQuery.append("and bbg.branchCode = :lStrBranchCode  \n");
			lSBQuery.append("and bbg.auditorPostId = :lLngAudPostId  \n");
			lSBQuery.append("and bbg.locationCode = :lLocCode  \n");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("lStrBankCode", lStrBankCode);
			hqlQuery.setString("lStrBranchCode", lStrBranchCode);
			hqlQuery.setLong("lLngAudPostId", lLngPostId);
			hqlQuery.setString("lLocCode", lStrLocationCode);
			lLstGrpName = hqlQuery.list();
			if (lLstGrpName.size() > 0) {
				lStrGrpName = (lLstGrpName.get(0) != null) ? lLstGrpName.get(0) : "";
			}
		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lStrGrpName;
	}

	public Integer getMainCategoryConfigDtlsCount(Long gLngLangId, Map displayTag) throws Exception {

		Integer lIntCount = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT COUNT(*) ");
			lSBQuery.append(" FROM MstPensionMainCategory main \n");
			lSBQuery.append(" WHERE main.langId =:langId  \n");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", BigDecimal.valueOf(gLngLangId));
			if (lHibQry.list() != null && lHibQry.list().size() > 0) {
				lIntCount = Integer.parseInt(lHibQry.list().get(0).toString());
			} else {
				lIntCount = 0;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lIntCount;

	}

	public List getMainCategoryConfigDtls(Long gLngLangId, Map displayTag) throws Exception {

		List lLstMainCategoryConfigDtls = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {

			lSBQuery = new StringBuffer();
			String[] columnValues = new String[]{"", "main.mainCatId", "main.mainCatDesc", "main.schemeCodePension", "main.schemeCodeCVP", "main.schemeCodeDCRG"};
			lSBQuery.append(" SELECT main.mainCatId,main.mainCatDesc,main.schemeCodePension,main.schemeCodeCVP,main.schemeCodeDCRG \n");
			lSBQuery.append(" FROM MstPensionMainCategory main \n");
			lSBQuery.append(" WHERE main.langId =:langId  \n");
			String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER) ? (String) displayTag.get(Constants.KEY_SORT_ORDER) : "desc");

			Integer orderbypara = null;

			if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
				orderbypara = (Integer) displayTag.get(Constants.KEY_SORT_PARA);
				lSBQuery.append(columnValues[orderbypara.intValue()] + " " + orderString);
			} else {
				lSBQuery.append(" ORDER BY main.mainCatId");
			}
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", BigDecimal.valueOf(gLngLangId));

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			lHibQry.setFirstResult((pageNo.intValue() - 1) * Constants.PDWL_PAGE_SIZE);
			lHibQry.setMaxResults(Constants.PDWL_PAGE_SIZE);
			lLstMainCategoryConfigDtls = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstMainCategoryConfigDtls;

	}

	public Long getMaxMainCatId(Long gLngLangId) throws Exception {

		Long llngMainCatId = null;
		StringBuilder lSBQuery = null;
		Query hqlQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT MAX(main.mainCatId) ");
			lSBQuery.append(" FROM  MstPensionMainCategory main");
			lSBQuery.append(" WHERE main.langId =:langId  ");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setParameter("langId", BigDecimal.valueOf(gLngLangId));
			if (hqlQuery.list().get(0) == null) {
				llngMainCatId = 1L;
			} else {
				llngMainCatId = Long.valueOf(hqlQuery.list().get(0).toString());
			}
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return llngMainCatId;

	}

	public List getAllMainCtgryId(Long gLngLangId) throws Exception {

		List<Integer> lLstMainCategory = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT mainCatCode FROM MstPensionHeadcode WHERE langId  =:langId GROUP BY mainCatCode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", BigDecimal.valueOf(gLngLangId));
			lLstMainCategory = lHibQry.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstMainCategory;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getMstSchemeCount(java
	 * .lang.Long)
	 */
	public Integer getMstSchemeCount(Long lLngLangId) throws Exception {

		// TODO Auto-generated method stub
		List lLstResult = new ArrayList();
		StringBuffer lSBQuery = new StringBuffer();
		Integer lIntCount = 0;

		try {
			lSBQuery.append("SELECT COUNT(schemeId) FROM MstScheme where schemeType = 'P' and langId = :langId");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setLong("langId", lLngLangId);
			// hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			if (hqlQuery.list() != null && hqlQuery.list().size() > 0) {
				lIntCount = Integer.parseInt(hqlQuery.list().get(0).toString());
			} else {
				lIntCount = 0;
			}

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lIntCount;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tcs.sgv.pensionpay.dao.PensionConfigDAO#getSchemeCodeList(java
	 * .lang.Long)
	 */
	public List<MstScheme> getMstSchemeList(Map displayTag, Long lLngLangId) throws Exception {

		List<MstScheme> lLstMstSchemeVO = new ArrayList<MstScheme>();
		StringBuffer lSBQuery = new StringBuffer();
		String lStrColumnValues = null;
		try {
			lStrColumnValues = "schemeCode," + "schemeName,";

			lSBQuery.append("SELECT new com.tcs.sgv.common.valueobject.MstScheme(schemeCode, schemeName) FROM MstScheme where schemeType = 'P' and langId = :langId");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());

			Integer pageNo = (displayTag.containsKey(Constants.KEY_PAGE_NO) ? (Integer) displayTag.get(Constants.KEY_PAGE_NO) : 1);
			hqlQuery.setFirstResult((pageNo.intValue() - 1) * Constants.PAGE_SIZE);
			hqlQuery.setMaxResults(Constants.PAGE_SIZE);

			hqlQuery.setLong("langId", lLngLangId);

			// hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");

			lLstMstSchemeVO = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstMstSchemeVO;
	}

	public List getMainCtgryConfigDtls(Long gLngLangId) throws Exception {

		List lLstMainCtgryConfigDtls = null;
		List lLstResult = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT mainCatId,mainCatDesc FROM MstPensionMainCategory WHERE langId = :langId ORDER BY  mainCatId ASC ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("langId", BigDecimal.valueOf(gLngLangId));
			// lHibQry.setParameter("locationCode", lStrLocCode);

			lLstResult = lHibQry.list();
			ComboValuesVO lObjComboValuesVO = null;

			if (lLstResult != null && lLstResult.size() != 0) {
				lLstMainCtgryConfigDtls = new ArrayList<Object>();
				Object obj[];
				for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
					obj = (Object[]) lLstResult.get(liCtr);
					lObjComboValuesVO = new ComboValuesVO();
					lObjComboValuesVO.setId(obj[0].toString());
					lObjComboValuesVO.setDesc(obj[1].toString());
					lLstMainCtgryConfigDtls.add(lObjComboValuesVO);
				}
			} else {
				lLstMainCtgryConfigDtls = new ArrayList<Object>();
				lObjComboValuesVO = new ComboValuesVO();
				lObjComboValuesVO.setId("-1");
				lObjComboValuesVO.setDesc("--Select--");
				lLstMainCtgryConfigDtls.add(lObjComboValuesVO);
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
			throw e;
		}

		return lLstMainCtgryConfigDtls;

	}

	public Long getTotalPnsrsOfBranch(String lStrBranchCode, String lStrBankCode, String lStrLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Query hqlQuery = null;
		String lStrGrpName = "";
		Long lLngTotalPnsrCount = 0l;
		List<Object> lLstTotalPnsrCount = null;
		try {
			lSBQuery.append("Select count(pensionerCode) \n");
			lSBQuery.append("From  \n");
			lSBQuery.append("MstPensionerDtls  \n");
			lSBQuery.append("Where  \n");
			lSBQuery.append("branchCode = :lStrBranchCode  \n");
			lSBQuery.append("and bankCode = :lStrBankCode  \n");
			lSBQuery.append("and locationCode = :lStrLocCode  \n");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("lStrBankCode", lStrBankCode);
			hqlQuery.setString("lStrBranchCode", lStrBranchCode);
			hqlQuery.setString("lStrLocCode", lStrLocCode);
			lLstTotalPnsrCount = hqlQuery.list();
			if (lLstTotalPnsrCount != null && lLstTotalPnsrCount.size() > 0) {
				for (Object lObj : lLstTotalPnsrCount) {
					lLngTotalPnsrCount = (lObj != null) ? ((Long) lObj) : 0;
				}
			}

		} catch (Exception e) {
			gLogger.error("Error is :" + e, e);
		}
		return lLngTotalPnsrCount;
	}

	public Long getAuditorPostId(String lStrBranchCode, String lStrBankCode, String lStrLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Query hqlQuery = null;
		Long lLngAuditorPostId = 0l;
		// select post_id from rlt_auditor_bank where branch_code = 5001007 and
		// bank_code = 9 and location_code = 7101
		try {
			lSBQuery.append("SELECT postId \n");
			lSBQuery.append("FROM RltAuditorBank WHERE branchCode = :branchCode AND bankCode = :bankCode AND locationCode = :locationCode\n");
			hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("branchCode", lStrBranchCode);
			hqlQuery.setString("bankCode", lStrBankCode);
			hqlQuery.setString("locationCode", lStrLocCode);
			if (hqlQuery.list() != null && hqlQuery.list().size() > 0) {
				lLngAuditorPostId = Long.valueOf(hqlQuery.list().get(0).toString().trim());
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}
		return lLngAuditorPostId;

	}

	public List getBnkBrnchDtls(List<Long> lLstAuditorPostId, List<Long> lLstBranchCode) throws Exception {

		List lLstBnkBrnchDtls = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT  concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname),rlt.branchName,mst.bankName ");
			lSBQuery.append(" FROM RltBankBranch rlt,MstBank mst,RltAuditorBank rab,OrgEmpMst oem,OrgUserMst oum,OrgPostMst opm, OrgUserpostRlt oupr WHERE");
			lSBQuery.append(" oupr.orgPostMstByPostId.postId=opm.postId AND oupr.orgUserMst.userId = oum.userId ");
			lSBQuery.append(" AND oem.orgUserMst.userId = oupr.orgUserMst.userId AND rab.postId = opm.postId AND opm.postId IN (:postIdList) AND ");
			lSBQuery.append(" rab.branchCode = rlt.branchCode AND rab.bankCode = mst.bankCode AND ");
			lSBQuery.append(" mst.bankCode = rlt.bankCode AND rlt.branchCode IN (:branchCodeList) ");

			lHibQry = ghibSession.createQuery(lSBQuery.toString());

			lHibQry.setParameterList("postIdList", lLstAuditorPostId);
			lHibQry.setParameterList("branchCodeList", lLstBranchCode);

			lLstBnkBrnchDtls = lHibQry.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstBnkBrnchDtls;

	}

	public List<RltAuditorBank> getRltAuditorBankVO(String lStrBranchCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<RltAuditorBank> lLstRltAuditorBank = new ArrayList<RltAuditorBank>();
		try {
			objCrt = ghibSession.createCriteria(RltAuditorBank.class);
			objCrt.add(Restrictions.like("branchCode", lStrBranchCode));
			lLstRltAuditorBank = objCrt.list();
		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("PensionConfigDAOImpl : getRltAuditorBankVO() : Error is :" + e, e);
			throw e;
		}
		return lLstRltAuditorBank;
	}

	public List<MstPensionerDtls> getMstPensionerDtlsVO(String lStrBranchCode) throws Exception {

		// TODO Auto-generated method stub
		Criteria objCrt = null;
		List<MstPensionerDtls> lLstMstPensionerDtls = new ArrayList<MstPensionerDtls>();
		try {
			objCrt = ghibSession.createCriteria(MstPensionerDtls.class);
			objCrt.add(Restrictions.like("branchCode", lStrBranchCode));
			lLstMstPensionerDtls = objCrt.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("PensionConfigDAOImpl : getMstPensionerDtlsVO() : Error is :" + e, e);
			throw e;
		}
		return lLstMstPensionerDtls;
	}

	public List<String> getPensionerCode(String lStrBranchCode) throws Exception {

		List<String> lLstPensionerCode = null;
		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			lSBQuery = new StringBuffer();

			lSBQuery.append(" SELECT  rqst.pensionerCode FROM ");
			lSBQuery.append(" TrnPensionRqstHdr rqst,MstPensionerDtls dtls WHERE");
			lSBQuery.append(" dtls.pensionerCode = rqst.pensionerCode AND dtls.branchCode = :branchCode ");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());

			lHibQry.setParameter("branchCode", lStrBranchCode);
			lLstPensionerCode = lHibQry.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);
		}

		return lLstPensionerCode;

	}

	public List<String> checkSchemeCode(String lStrSchemeCode) throws Exception {

		List<String> lLstResult = new ArrayList<String>();
		StringBuffer lSBQuery = new StringBuffer();
		try {
			lSBQuery.append(" SELECT schemeCode FROM MstScheme  WHERE schemeType = 'P' and schemeCode = :schemeCode");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("schemeCode", lStrSchemeCode);

			lLstResult = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstResult;

	}

}
