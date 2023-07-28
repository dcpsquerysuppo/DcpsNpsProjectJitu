package com.tcs.sgv.pensionpay.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valuebeans.CommonVO;
import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.SgvaMonthMst;
import com.tcs.sgv.common.valueobject.SgvcFinYearMst;
import com.tcs.sgv.core.service.ServiceLocator;
import com.tcs.sgv.pensionpay.constants.PensionConstants;
import com.tcs.sgv.pensionpay.valueobject.MstPensionRqstReason;
import com.tcs.sgv.pensionpay.valueobject.RltPensionHeadcodeChargable;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionCorrectionDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstHdr;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionRqstTracking;


public class CommonPensionDAOImpl implements CommonPensionDAO {

	/* Global Variable for Logger Class */
	private Log logger = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory = null;

	static boolean isOracleDB = true;
	static String nvlOrIfNullString = "";

	public CommonPensionDAOImpl(SessionFactory sessionFactory) {

		this.sessionFactory = sessionFactory;
	}

	static ResourceBundle bundleApplicationDB = ResourceBundle.getBundle("ApplicationDB");
	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");
	static {
		if (bundleApplicationDB.getString("DBTYPE").equalsIgnoreCase("mysql")) {
			isOracleDB = false;
		}

		if (isOracleDB) {
			nvlOrIfNullString = "nvl";
		} else {
			nvlOrIfNullString = "ifnull";
		}

	}
	Session ghibSession = ServiceLocator.getServiceLocator().getSessionFactorySlave().getCurrentSession();

	public Session getSession() {

		boolean allowCreate = true;
		return SessionFactoryUtils.getSession(sessionFactory, allowCreate);
	}

	public String getBranchCodeFromPPONo(String lStrPPONo, String lArgsLocCode) throws Exception {

		String lStrBranchCode = null;
		List resultList;
		try {
			StringBuffer strQuery = new StringBuffer();

			strQuery.append(" SELECT md.branchCode FROM TrnPensionRqstHdr rq,MstPensionerDtls md ");
			strQuery.append(" WHERE rq.pensionerCode = md.pensionerCode and md.active_flag = 'Y' AND rq.ppoNo =:ppoNo and rq.locationCode=:lArgsLocCode ");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("ppoNo", lStrPPONo);
			hqlQuery.setString("lArgsLocCode", lArgsLocCode);

			resultList = hqlQuery.list();

			if (resultList != null && !resultList.isEmpty()) {
				lStrBranchCode = resultList.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrBranchCode;
	}

	/* Method to get all headcode */
	public List getAllHeadCode() throws Exception {

		ArrayList arrHeadCode = new ArrayList();
		Iterator it;
		ComboValuesVO vo;
		Object[] tuple;
		List resultList;
		try {
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" SELECT headCode,description FROM MstPensionHeadcode order by headCode");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					vo = new ComboValuesVO();
					tuple = (Object[]) it.next();
					vo.setId(tuple[0].toString());
					vo.setDesc(tuple[1].toString());
					arrHeadCode.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrHeadCode;
	}

	/* Method to get discription of headcode */
	public String getAllHeadCodeDesc(String strHeadCode, long langId) throws Exception {

		String strHeadDesc = null;
		List resultList;
		try {
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" SELECT stateDesc  FROM MstPensionStateRate  WHERE stateCode =:stateCode and langId = :langID");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setString("stateCode", strHeadCode);
			hqlQuery.setString("langID", String.valueOf(langId));

			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				strHeadDesc = resultList.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return strHeadDesc;
	}

	/**
	 * @param lLstHeadCode
	 * @param langId
	 * @return Map having Head_Code as Key and description as value
	 * @throws Exception
	 */
	public Map<String, String> getHeadCodeDesc(Object[] lLstHeadCode, long langId) throws Exception {

		List resultList = null;
		HashMap<String, String> lMapHeadDesc = new HashMap<String, String>();
		try {
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" SELECT headCode, description  FROM MstPensionHeadcode  WHERE headCode in(:headCode) and langId = :langID");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setParameterList("headCode", lLstHeadCode);
			hqlQuery.setString("langID", String.valueOf(langId));

			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				for (Object lObj : resultList) {
					lMapHeadDesc.put(((Object[]) lObj)[0].toString(), ((Object[]) lObj)[1].toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lMapHeadDesc;
	}

	/* Method to get pensioncaseId, pensionID from PPo NO */
	public List getPensionerDtlsfromPpoNo(String ppoNo, String lStrArgLocCode, Integer currCaseStatus) throws Exception {

		ArrayList arrPnsnrDtls = new ArrayList();
		StringBuffer strQuery = new StringBuffer();
		Iterator itr;
		Object[] tuple;
		List resultList;
		try {
			strQuery.append(" SELECT r.pensionRequestId,r.pensionerCode  FROM TrnPensionRqstHdr r " + " WHERE r.ppoNo=:ppoNo and r.locationCode=:lStrArgLocCode and currCaseStatus=:currCaseStatus");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("ppoNo", ppoNo.trim());
			hqlQuery.setString("lStrArgLocCode", lStrArgLocCode);
			hqlQuery.setInteger("currCaseStatus", currCaseStatus);

			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				itr = resultList.iterator();
				while (itr.hasNext()) {
					tuple = (Object[]) itr.next();
					if (tuple[0] != null) {
						arrPnsnrDtls.add(tuple[0]);
					}
					if (tuple[1] != null) {
						arrPnsnrDtls.add(tuple[1]);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrPnsnrDtls;
	}

	public List getAllAuditor(String lStrLocationCode, Long lLngLangId) throws Exception {

		ArrayList<ComboValuesVO> arrAuditorVo = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		Iterator itr;
		Object[] obj;
		ComboValuesVO cmbVO;
		List resultList;
		try {
			strQuery.append(" SELECT WMP.POST_ID postID,(" + nvlOrIfNullString + "(OEM.EMP_FNAME,'')|| ' ' || " + nvlOrIfNullString + " (OEM.EMP_MNAME,'')|| ' ' ||" + nvlOrIfNullString
					+ "(OEM.EMP_LNAME,'')) empName" + " FROM WF_HIERACHY_POST_MPG WMP,WF_HIERARCHY_REFERENCE_MST HRM,ORG_USERPOST_RLT OUPR,ORG_EMP_MST OEM  "
					+ " WHERE HRM.HIERACHY_REF_ID = WMP.HIERACHY_REF_ID  AND WMP.POST_ID = OUPR.POST_ID  AND "
					+ " OUPR.USER_ID = OEM.USER_ID  AND HRM.LOC_CODE =:LOCATION_CODE AND HRM.DOC_ID =:DOCID  AND HRM.DESCRIPTION =:DESC "
					+ " AND WMP.LEVEL_ID =:LEVEL_ID AND OEM.LANG_ID = :langId AND OUPR.activate_flag = OEM.activate_flag AND OEM.activate_flag = WMP.activate_flag AND OEM.activate_flag = 1 ");

			SQLQuery Query = ghibSession.createSQLQuery(strQuery.toString());

			Query.setCacheable(true).setCacheRegion("pensionCache");

			Query.addScalar("postID", Hibernate.STRING);
			Query.addScalar("empName", Hibernate.STRING);
			Query.setString("LOCATION_CODE", lStrLocationCode);
			Query.setLong("LEVEL_ID", PensionConstants.WFPENSIONAUDITORLEVEL);
			Query.setLong("DOCID", PensionConstants.WFDocIdPensionBill);
			Query.setString("DESC", PensionConstants.ONLINEBILLSUBJECT);
			Query.setLong("langId", lLngLangId);

			resultList = Query.list();
			if (!resultList.isEmpty()) {
				itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrAuditorVo.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrAuditorVo;
	}

	public TrnPensionRqstHdr getTrnPensionRqstHdrDtls(String lStrPPONO, String lStrStatus, String lStrArgsLocCode) throws Exception {

		TrnPensionRqstHdr lobjPensionRqstHdr = new TrnPensionRqstHdr();
		Session ghibSession = getSession();

		try {
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" FROM TrnPensionRqstHdr A WHERE A.ppoNo = :lppoNo and A.locationCode=:lArgsLocCode");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lppoNo", lStrPPONO);
			// lQuery.setString("lStatus", lStrStatus);
			lQuery.setString("lArgsLocCode", lStrArgsLocCode);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				lobjPensionRqstHdr = (TrnPensionRqstHdr) lLstVO.get(0);
			}
		} catch (Exception e) {
			logger.error("Error in getTrnPensionRqstHdrDtls. Error is : " + e, e);
			throw (e);
		}

		return lobjPensionRqstHdr;
	}

	public List getAllState(long langId) throws Exception {

		ArrayList<ComboValuesVO> arrStateVo = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		Iterator it;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			strQuery.append(" Select stateId,stateName  From CmnStateMst WHERE cmnLanguageMst.langId=:langId order by stateName");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setLong("langId", langId);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrStateVo.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrStateVo;
	}

	public List getDistrictsOfState(BigDecimal stateId, long langId) throws Exception {

		ArrayList<ComboValuesVO> arrDistrict = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			strQuery.append(" SELECT CDM.districtId,CDM.districtName  FROM CmnDistrictMst CDM,CmnLookupMst CLM  " + " WHERE CDM.cmnLanguageMst.langId = :langId "
					+ " AND CLM.lookupId = CDM.districtType  AND CLM.lookupName =:Dist  AND CDM.cmnStateMst.stateId = :stateId  " + " order by CDM.districtName ");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setLong("langId", langId);
			hqlQuery.setLong("stateId", Long.valueOf(stateId.toString()));
			hqlQuery.setString("Dist", "District");
			resultList = hqlQuery.list();
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("--Select--");
			arrDistrict.add(cmbVO);
			if (!resultList.isEmpty()) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrDistrict.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrDistrict;
	}

	public List getAllBank(String bankCodeFrmRequest, Long langId, String locCode) throws Exception {

		ArrayList<ComboValuesVO> arrBankVO = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		ComboValuesVO cmbVO = null;
		Object[] obj = null;
		List lLstTempRes = new ArrayList();
		Query hqlQuery = null;
		SQLQuery sqlQuery = null;
		try {
			if (bankCodeFrmRequest != null) {
				strQuery.append(" SELECT MB.bankCode, MB.bankName, RBB.branchCode, RBB.branchName,  MB.bankName || ' ' || RBB.branchName ");
				strQuery.append(" FROM MstBank MB, RltBankBranch RBB ");
				strQuery.append(" WHERE MB.bankCode = RBB.bankCode AND RBB.locationCode = :locationCode ");
				strQuery.append(" AND RBB.langId = MB.langId AND ");
				strQuery.append(" RBB.langId = :langId ");
				if (!bankCodeFrmRequest.equals("-1")) {
					strQuery.append(" AND RBB.branchCode = :branchCode ");
				}
				strQuery.append(" ORDER BY MB.bankName ");
				hqlQuery = ghibSession.createQuery(strQuery.toString());
				hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
				if (!bankCodeFrmRequest.equals("-1")) {
					hqlQuery.setString("branchCode", bankCodeFrmRequest);
				}
				hqlQuery.setLong("langId", langId);
				hqlQuery.setString("locationCode", locCode);
				resultList = hqlQuery.list();
			} else {
				strQuery.append(" SELECT  MB.bank_Code bnkCode, MB.bank_Name bnkName");
				strQuery.append(" FROM Mst_Bank MB where MB.lang_Id =:langId and");
				strQuery.append(" ( MB.bank_Code,MB.lang_Id =:langId ) in");
				strQuery.append(" (select bank_Code,lang_Id from Rlt_Bank_Branch RBB ");
				strQuery.append(" WHERE RBB.location_Code = :locationCode ");
				strQuery.append(" AND RBB.lang_Id = :langId )");
				strQuery.append(" ORDER BY MB.bank_Name ");

				sqlQuery = ghibSession.createSQLQuery(strQuery.toString());
				sqlQuery.setCacheable(true).setCacheRegion("pensionCache");
				sqlQuery.addScalar("bnkCode", Hibernate.STRING);
				sqlQuery.addScalar("bnkName", Hibernate.STRING);

				sqlQuery.setLong("langId", langId);
				sqlQuery.setString("locationCode", locCode);
				resultList = sqlQuery.list();
			}
			if (bankCodeFrmRequest != null) {
				lLstTempRes = resultList;
			} else {
				if (!resultList.isEmpty()) {
					Iterator itr = resultList.iterator();
					while (itr.hasNext()) {
						cmbVO = new ComboValuesVO();
						obj = (Object[]) itr.next();
						cmbVO.setId(obj[0].toString());
						cmbVO.setDesc(obj[1].toString());
						arrBankVO.add(cmbVO);
					}
				}
				lLstTempRes = arrBankVO;
			}
			return lLstTempRes;
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}

	public String getAllBankParty(String bankCodeFrmRequest, Long langId, String locCode) throws Exception {

		ArrayList<ComboValuesVO> arrBankVO = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		ComboValuesVO cmbVO = null;
		Object[] obj = null;
		List lLstTempRes = new ArrayList();
		String lStrBankBranchName = null;
		try {
			// strQuery.append(" SELECT CASE WHEN RBB.REPORTING_BRANCH_CODE IS NOT NULL THEN ( SELECT MB1.BANK_NAME||' '||RBB1.BRANCH_NAME FROM MST_BANK MB1, RLT_BANK_BRANCH RBB1 WHERE MB1.BANK_CODE = RBB1.BANK_CODE AND MB1.LANG_ID = :LLNG AND RBB.REPORTING_BRANCH_CODE = RBB1.BRANCH_CODE AND RBB1.LOCATION_CODE = :LLOCCODE ) ELSE MB.BANK_NAME||' '||RBB.BRANCH_NAME END BRNCHNAME FROM MST_BANK MB, RLT_BANK_BRANCH RBB WHERE MB.BANK_CODE = RBB.BANK_CODE AND MB.LANG_ID = 1 AND RBB.BRANCH_CODE = :LBRANCHCODE AND RBB.LOCATION_CODE = :LLOCCODE ");

			strQuery.append("SELECT CASE " + " WHEN RBB.REPORTING_BRANCH_CODE IS NOT NULL THEN " + " (SELECT CONCAT(CONCAT(MB1.BANK_NAME,' '),RBB1.BRANCH_NAME) "
					+ "  FROM MST_BANK MB1, RLT_BANK_BRANCH RBB1 " + "  WHERE MB1.BANK_CODE = RBB1.BANK_CODE AND MB1.LANG_ID = :LLNG AND "
					+ "        RBB.REPORTING_BRANCH_CODE = RBB1.BRANCH_CODE AND " + "        RBB1.LOCATION_CODE = :LLOCCODE ) " + "  ELSE " + " CONCAT(CONCAT(MB.BANK_NAME,' '),RBB.BRANCH_NAME) "
					+ " END BRNCHNAME " + " FROM MST_BANK MB, RLT_BANK_BRANCH RBB " + "      WHERE MB.BANK_CODE = RBB.BANK_CODE AND MB.LANG_ID = :LLNG AND "
					+ "            RBB.BRANCH_CODE = :LBRANCHCODE AND RBB.LOCATION_CODE = :LLOCCODE ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(strQuery.toString());

			hqlQuery.addScalar("BRNCHNAME", Hibernate.STRING);

			hqlQuery.setLong("LLNG", langId);
			hqlQuery.setString("LLOCCODE", locCode);
			hqlQuery.setString("LBRANCHCODE", bankCodeFrmRequest);

			resultList = hqlQuery.list();

			if (!resultList.isEmpty()) {
				lStrBankBranchName = resultList.get(0).toString();
			}
			return lStrBankBranchName;
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
	}

	public List getBranchsOfBank(String bankCode, Long langId, String locCode) throws Exception {

		ArrayList<ComboValuesVO> arrBranchVO = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			strQuery.append(" SELECT branchCode,branchName  FROM RltBankBranch  WHERE bankCode =:bankCode AND locationCode =:locationCode " + " order by branchName");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setString("bankCode", bankCode);
			hqlQuery.setString("locationCode", locCode);
			resultList = hqlQuery.list();
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("--Select--");
			arrBranchVO.add(cmbVO);
			if (!resultList.isEmpty()) {
				Iterator itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrBranchVO.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrBranchVO;
	}

	public List getBranchByBranchId(String branchCode, Long langId, String locCode) throws Exception {

		StringBuffer strQuery = new StringBuffer();
		List resultList = new ArrayList();
		List lLstDtlsList = new ArrayList();
		Iterator it;
		Object tuple[] = null;
		try {
			Session ghibSession = getSession();
			strQuery.append(" SELECT rb.bankCode,rb.branchCode,rb.branchName,mb.bankName  FROM RltBankBranch rb,MstBank mb"
					+ "  WHERE rb.branchCode =:branchCode and rb.locationCode =:locationCode AND mb.bankCode = rb.bankCode ");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setLong("branchCode", Long.parseLong(branchCode));
			hqlQuery.setString("locationCode", locCode);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lLstDtlsList.add(tuple[0]);
					lLstDtlsList.add(tuple[1]);
					lLstDtlsList.add(tuple[2]);
					lLstDtlsList.add(tuple[3]);
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw e;
		}
		return lLstDtlsList;
	}

	public List getAllDepartment(long langId, Long lLngDeptId) throws Exception {

		ArrayList<ComboValuesVO> arrDepartnent = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		ComboValuesVO cmbVO;
		List resultList;
		Iterator itr;
		Object[] obj;
		try {
			strQuery.append(" SELECT dept.deptId, dept.deptName FROM MstPensionDept dept  " + "WHERE dept.deptIdentifier =:Identifier   and dept.langId =:langId ");
			if (lLngDeptId != null) {
				strQuery.append(" AND  dept.deptId =:deptId ");
			}
			strQuery.append("order by deptName");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("Identifier", "DEPT");

			if (lLngDeptId != null) {
				hqlQuery.setLong("deptId", lLngDeptId);
			}
			hqlQuery.setLong("langId", langId);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrDepartnent.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw e;
		}
		return arrDepartnent;
	}

	public List getAllDesignation(String lStrDesigCode, long langId) throws Exception {

		ArrayList<ComboValuesVO> arrDesignation = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		Iterator itr;
		Object[] obj;
		ComboValuesVO cmbVO;
		try {
			strQuery.append(" SELECT dsgnCode,dsgnName  FROM OrgDesignationMst  WHERE cmnLanguageMst.langId =:langId ");
			if (lStrDesigCode != null) {
				strQuery.append(" AND dsgnCode=:dsgnCode ");
			}
			strQuery.append("order by dsgnName ");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setLong("langId", langId);
			if (lStrDesigCode != null) {
				hqlQuery.setString("dsgnCode", lStrDesigCode);
			}

			resultList = hqlQuery.list();

			if (!resultList.isEmpty()) {
				itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrDesignation.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw e;
		}
		return arrDesignation;
	}

	public List getSubTreasurysOfTreasury(String locCode, long langId) throws Exception {

		ArrayList<ComboValuesVO> arrBranchVO = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		ComboValuesVO cmbVO = null;
		List resultList;
		Iterator itr;
		Object[] obj;
		try {
			Session ghibSession = getSession();
			strQuery.append(" SELECT locationCode,locName  FROM CmnLocationMst  WHERE parentLocId=(SELECT locId FROM CmnLocationMst "
					+ " WHERE locationCode=:parentLocId AND cmnLanguageMst.langId=:langId)  and cmnLanguageMst.langId=:langId AND locationCode !=:parentLocId ");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setString("parentLocId", locCode);
			hqlQuery.setLong("langId", langId);
			resultList = hqlQuery.list();

			if (!resultList.isEmpty()) {
				cmbVO = new ComboValuesVO();
				cmbVO.setId("-1");
				cmbVO.setDesc("--Select--");
				arrBranchVO.add(cmbVO);
				itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrBranchVO.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw e;
		}
		return arrBranchVO;
	}

	public List getAllTreasury(long langId, String locationCode) throws Exception {

		ArrayList<ComboValuesVO> arrTreasury = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			Session ghibSession = getSession();
			strQuery.append("SELECT locationCode, locName  FROM CmnLocationMst WHERE cmnLanguageMst.langId =:langId2 and locationCode =:locationCode ");
			Query hiQuery = ghibSession.createQuery(strQuery.toString());
			hiQuery.setCacheable(true).setCacheRegion("pensionCache");
			hiQuery.setLong("langId2", langId);
			hiQuery.setString("locationCode", locationCode);
			resultList = hiQuery.list();
			if (!resultList.isEmpty()) {
				Iterator itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrTreasury.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw e;
		}
		return arrTreasury;
	}

	public List getHodFromDepartmet(BigDecimal strDepCode, long langId, Long lLngHodId) throws Exception {

		ComboValuesVO vo = null;
		ArrayList arrHOD = new ArrayList();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		Object[] tuple;
		try {
			strQuery.append(" SELECT dept.deptId,dept.deptName  FROM MstPensionDept dept  WHERE dept.deptIdentifier =:Identifier  " + " and dept.parentDeptId =:parentDeptId ");
			if (lLngHodId != null) {
				strQuery.append(" and dept.deptId =:deptId ");
			}
			strQuery.append(" and dept.langId =:langId order by deptName");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setString("Identifier", "HOD");
			hqlQuery.setLong("parentDeptId", Long.valueOf(strDepCode.toString()));
			hqlQuery.setLong("langId", langId);
			if (lLngHodId != null) {
				hqlQuery.setLong("deptId", lLngHodId);
			}
			resultList = hqlQuery.list();
			vo = new ComboValuesVO();
			vo.setId("-1");
			vo.setDesc("--Select--");
			arrHOD.add(vo);
			if (!resultList.isEmpty()) {
				Iterator it = resultList.iterator();
				while (it.hasNext()) {
					vo = new ComboValuesVO();
					tuple = (Object[]) it.next();
					vo.setId(tuple[0].toString());
					vo.setDesc(tuple[1].toString());
					arrHOD.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw e;
		}
		return arrHOD;
	}

	public List getAuditorBankCodeList(Long lPostId, String lStrLocCode) throws Exception {

		ArrayList<ComboValuesVO> arrBankVO = new ArrayList<ComboValuesVO>();
		List<String> lLstReturn = null;
		ComboValuesVO cmbVO = null;
		StringBuffer lSBQuery = new StringBuffer();

		Object[] obj = null;
		try {
			lSBQuery.append(" SELECT DISTINCT A.bankCode,B.bankName  FROM RltAuditorBank A,MstBank B WHERE A.bankCode = B.bankCode and" + " A.locationCode = :lLocCode");
			if (lPostId != null) {
				lSBQuery.append(" AND A.postId = :postId \n");
			}
			lSBQuery.append(" ORDER BY B.bankName \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			if (lPostId != null) {
				lQuery.setLong("postId", lPostId);
			}
			lQuery.setString("lLocCode", lStrLocCode);

			lLstReturn = lQuery.list();

			if (!lLstReturn.isEmpty()) {
				Iterator itr = lLstReturn.iterator();

				cmbVO = new ComboValuesVO();
				cmbVO.setId("-1");
				cmbVO.setDesc("--Select--");
				arrBankVO.add(cmbVO);
				cmbVO = null;

				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrBankVO.add(cmbVO);
					cmbVO = null;
				}

			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return arrBankVO;
	}

	public List getAuditorBranchCodeList(Long lPostId, String lStrAuditorBankCode, String lStrLocCode) throws Exception {

		ArrayList<ComboValuesVO> arrBranchVO = new ArrayList<ComboValuesVO>();
		List<String> lLstReturn = null;
		ComboValuesVO cmbVO = null;
		StringBuffer lSBQuery = new StringBuffer();
		Object[] obj = null;
		try {
			lSBQuery.append(" SELECT DISTINCT A.branchCode,B.branchName  FROM RltAuditorBank A,RltBankBranch B WHERE "
					+ " A.branchCode = B.branchCode  AND A.bankCode = B.bankCode AND A.postId = :postId  AND A.bankCode = :bankCode  " + " AND A.locationCode = B.locationCode "
					+ " AND B.locationCode = :locCode ");

			lSBQuery.append(" order by B.branchName ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("postId", lPostId);
			lQuery.setString("bankCode", lStrAuditorBankCode);
			lQuery.setString("locCode", lStrLocCode);

			lLstReturn = lQuery.list();

			if (!lLstReturn.isEmpty()) {
				Iterator itr = lLstReturn.iterator();
				cmbVO = new ComboValuesVO();
				cmbVO.setId("-1");
				cmbVO.setDesc("--Select--");
				arrBranchVO.add(cmbVO);
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrBranchVO.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return arrBranchVO;
	}

	/**
	 * To populate month combo with Sgva_Month_Mst table records
	 * 
	 * @param String
	 *            : Lang Id
	 * @return Array of VOs : of type SgvaMonthMst
	 */
	public List<SgvaMonthMst> getSgvaMonthMstVO(final String lStrLangId) throws Exception {

		List<SgvaMonthMst> lObjSgvaMonthMst = new ArrayList<SgvaMonthMst>();

		StringBuffer lSBQuery = new StringBuffer(100);

		String lStrLangFlag = "";

		Query lQuery = null;

		try {
			if ("1".equals(lStrLangId)) {
				lStrLangFlag = "en_US";
			} else {
				lStrLangFlag = "gu";
			}

			lSBQuery.append(" FROM SgvaMonthMst WHERE langId = :langId ORDER BY monthId ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setCacheable(true).setCacheRegion("pensionCache");
			lQuery.setString("langId", lStrLangFlag);

			lObjSgvaMonthMst = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lObjSgvaMonthMst;
	}

	/**
	 * To get the two digit month number from Sgva_Month_Mst
	 * 
	 * @param String
	 *            : lStrLangId,lStrMonthName
	 * @return String lStrReturnMonthNo
	 */
	public String getMonthNo(final String lStrLangId, final String lStrMonthName) throws Exception {

		StringBuffer lSBQuery = new StringBuffer(100);

		String lStrReturnMonthNo = "";
		String lStrLangFlag = "";

		try {
			if ("1".equals(lStrLangId)) {
				lStrLangFlag = "en_US";
			} else {
				lStrLangFlag = "gu";
			}

			lSBQuery.append(" SELECT monthNo FROM SgvaMonthMst WHERE langId= :langId AND monthName= :monthName ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("langId", lStrLangFlag);
			lQuery.setString("monthName", lStrMonthName);

			List lList = lQuery.list();

			if (!lList.isEmpty()) {
				lStrReturnMonthNo = lList.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrReturnMonthNo;
	}

	/**
	 * To populate month combo with Sgvc_Fin_Year_Mst table records
	 * 
	 * @param String
	 *            : Lang Id
	 * @return Array of VOs : of type SgvcFinYearMst
	 */
	public List<SgvcFinYearMst> getSgvcFinYearMstVO(String lStrLangId) throws Exception {

		List<SgvcFinYearMst> lObjSgvcFinYearMst = new ArrayList<SgvcFinYearMst>();

		StringBuffer lSBQuery = new StringBuffer(100);

		String lStrLangFlag = "";

		Query lQuery = null;

		try {
			if ("1".equals(lStrLangId)) {
				lStrLangFlag = "en_US";
			} else {
				lStrLangFlag = "gu";
			}

			lSBQuery.append(" FROM SgvcFinYearMst WHERE langId = :langId ORDER BY finYearCode ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("langId", lStrLangFlag);

			lObjSgvcFinYearMst = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lObjSgvcFinYearMst;
	}

	public String getBankCodeForPensioner(String lStrPensionerCode, String lStrBranchCode) throws Exception {

		String lStrbankCode = "";
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		try {

			Session ghibSession = getSession();
			strQuery.append(" SELECT bankCode  FROM MstPensionerDtls  WHERE pensionerCode = :pensionerCode  AND branchCode = :branchCode and active_flag = 'Y' ");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("pensionerCode", lStrPensionerCode.trim());
			hqlQuery.setString("branchCode", lStrBranchCode.trim());
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				lStrbankCode = resultList.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrbankCode;
	}

	public String getPensionerCodefromPpoNo(String ppoNo, String lStrLocCd) throws Exception {

		String strPensionerCode = "";
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		try {
			strQuery.append(" SELECT pensionerCode  FROM TrnPensionRqstHdr WHERE ppoNo = :ppoNo and locationCode = :locationCode ");

			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("ppoNo", ppoNo.trim());
			hqlQuery.setString("locationCode", lStrLocCd);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				strPensionerCode = resultList.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return strPensionerCode;
	}

	public List getAllBranchsForLocation(String langId, String locationCode) throws Exception {

		ArrayList<ComboValuesVO> arrBranchVO = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			strQuery.append(" SELECT branchCode,branchName  FROM RltBankBranch  where locationCode=:locationCode order by branchName");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");
			hqlQuery.setString("locationCode", locationCode);
			resultList = hqlQuery.list();
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("--Select--");
			arrBranchVO.add(cmbVO);
			if (!resultList.isEmpty()) {
				Iterator itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrBranchVO.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is:" + e, e);
			throw e;
		}
		return arrBranchVO;
	}

	public List getBillTypeFromHeadCode(BigDecimal lBDHeadCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Query lQuery = null;
		List lLstResLst = new ArrayList();
		try {
			lSBQuery.append(" SELECT RPC.billType FROM RltPensionHeadcodeChargable RPC,MstPensionHeadcode MPC WHERE RPC.headCode = MPC.headCode  AND MPC.headCode =:headCode  ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setCacheable(true).setCacheRegion("pensionCache");
			lQuery.setBigDecimal("headCode", lBDHeadCode);
			lLstResLst = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}

		return lLstResLst;
	}

	public List<TrnPensionRqstHdr> getPensionerDtlsDiffFromPpoNo(String ppoNo, String lArgsLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		Query lQuery = null;
		List<TrnPensionRqstHdr> lLstResLst = new ArrayList<TrnPensionRqstHdr>();

		Session ghibSession = getSession();
		try {
			lSBQuery.append(" FROM TrnPensionRqstHdr D WHERE D.ppoNo  =:ppoNo AND  " + " (D.caseStatus =:newStatus OR D.caseStatus =:approveStatus ) and D.locationCode=:lArgsLocCode ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("ppoNo", ppoNo);
			lQuery.setString("newStatus", "NEW");
			lQuery.setString("approveStatus", "APPROVED");
			lQuery.setString("lArgsLocCode", lArgsLocCode);
			lLstResLst = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstResLst;
	}

	public List getCurrentUserFromPPO(String lstrPPONo, Long lLngLangId, String lStrArgsLocCode) throws Exception {

		StringBuffer lQuery = new StringBuffer();
		List<String[]> userList = new ArrayList<String[]>();
		Object[] tuple = null;
		String middleName = null;
		String[] result = null;
		try {
			lQuery.append(" SELECT ur.orgPostMstByPostId.postId,em.empFname, em.empMname,em.empLname,MM.dsgnName "
					+ " from TrnPensionRqstHdr th,OrgUserpostRlt ur,OrgEmpMst em,OrgDesignationMst MM,OrgPostMst P "
					+ " where th.locationCode=:LocCode th.caseOwner = ur.orgPostMstByPostId.postId and ur.orgPostMstByPostId.postId = em.orgUserMst.userId "
					+ " and em.cmnLanguageMst.langId=:langId and ur.activateFlag=1 and em.activateFlag = ur.activateFlag and p.activateFlag =1 and "
					+ " ur.orgPostMstByPostId.postId = P.postId AND P.dsgnCode = MM.dsgnCode  and th.ppoNo = :ppoNo ");

			Query query = ghibSession.createQuery(lQuery.toString());
			query.setString("ppoNo", lstrPPONo);
			query.setLong("langId", lLngLangId);
			query.setString("LocCode", lStrArgsLocCode);

			List lLstQList = query.list();
			Iterator it = lLstQList.iterator();
			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				result = new String[3];
				middleName = tuple[2] != null ? tuple[2].toString() : "";
				String name = " " + tuple[1] + " " + middleName + " " + tuple[3] + " [" + tuple[4] + "]";
				result[0] = name;
				result[1] = tuple[0].toString();
				result[2] = "5";
				userList.add(result);
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return userList;
	}

	/**
	 * get Totat Pensioner IT Cut For the Given Month. Written By Sagar
	 */
	public int getLastBillCreatedMonth(String lStrPensionerCode) throws Exception {

		int lLastBillCrtdMnth = 0;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" SELECT Max(hd.forMonth) FROM TrnPensionBillDtls dt, TrnPensionBillHdr hd  "
					+ " WHERE dt.trnPensionBillHdrId = hd.trnPensionBillHdrId  AND (hd.billType = :PensionFlag OR hd.billType = :MonthlyFlag) AND dt.pensionerCode = :PensionerCode and hd.rejected = 0 ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("PensionerCode", lStrPensionerCode);
			lQuery.setString("PensionFlag", PensionConstants.RECOVERYPENSION);
			lQuery.setString("MonthlyFlag", PensionConstants.RECOVERYMONTHLY);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lLastBillCrtdMnth = Integer.parseInt(lLstVO.get(0).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lLastBillCrtdMnth;
	}

	public List getTiAndMdeAllowFromByHeadCode(String lStrHeadCode, String lStrRetDate, String lStrTypeFlag, String lStrBasicPension) throws Exception {

		StringBuffer strQuery = new StringBuffer();
		List resultList = new ArrayList();
		Iterator it;
		Object[] tuple = null;
		List lLstDtlsList = new ArrayList();
		try {
			strQuery.append(" Select RPR.rate,RPR.minAmount FROM RltPensionHeadcodeRate RPR WHERE RPR.headCode =:HeadCode ");
			if (lStrRetDate != null) {
				strQuery.append(" AND (  ( RPR.effectiveFromDate <=:lStrRetDate  AND effectiveToDate >=:lStrRetDate)   OR ( RPR.effectiveFromDate <=:lStrRetDate AND effectiveToDate IS NULL ) )");

			}
			if (lStrTypeFlag != null) {
				strQuery.append(" AND RPR.fieldType =:FieldType");
			}
			if (lStrBasicPension != null) {
				strQuery.append(" AND RPR.uptoBasic =:Basic");
			}
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());

			hqlQuery.setDate("lStrRetDate", new Date());
			hqlQuery.setBigDecimal("HeadCode", new BigDecimal(lStrHeadCode));
			if (lStrTypeFlag != null) {
				hqlQuery.setString("FieldType", lStrTypeFlag);
			}
			if (lStrBasicPension != null) {
				hqlQuery.setBigDecimal("Basic", new BigDecimal(lStrBasicPension));
			}
			resultList = hqlQuery.list();

			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lLstDtlsList.add(tuple[0]);
					lLstDtlsList.add(tuple[1]);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDtlsList;
	}

	public List getSanctionAuthPrefix(Long lLngId) throws Exception {

		ArrayList<ComboValuesVO> lLstCmbSanAuth = new ArrayList<ComboValuesVO>();
		List<String> lLstReturn = null;
		ComboValuesVO cmbVO = null;
		StringBuffer lSBQuery = new StringBuffer();
		Object[] obj = null;
		try {
			lSBQuery.append(" SELECT authorityCode,authorityName  FROM MstPensionAuthority  WHERE langId =:lLngId  ORDER BY authorityName ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setCacheable(true).setCacheRegion("pensionCache");
			lQuery.setBigDecimal("lLngId", BigDecimal.valueOf(lLngId));

			lLstReturn = lQuery.list();

			if (!lLstReturn.isEmpty()) {
				Iterator itr = lLstReturn.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					lLstCmbSanAuth.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstCmbSanAuth;
	}

	public String getPrifixSancAuth(Long authorityCode, Long lLngId) {

		StringBuffer lSBQuery = new StringBuffer();
		String lStrResPrifix = "";
		List lLstRes = null;
		try {
			lSBQuery.append(" SELECT authorityPrefix  FROM MstPensionAuthority  WHERE langId =:lLngId AND " + " authorityCode =:authorityCode  ORDER BY authorityName ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setBigDecimal("lLngId", BigDecimal.valueOf(lLngId));
			lQuery.setLong("authorityCode", authorityCode);
			lLstRes = lQuery.list();
			if (!lLstRes.isEmpty() && lLstRes.get(0) != null) {
				lStrResPrifix = lLstRes.get(0).toString();
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
		}
		return lStrResPrifix;
	}

	public Long getPKForBillHdr(Long lLngBillNo) throws Exception {

		List lLstVO = null;
		Long lLngPkId = 0L;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT trnPensionBillHdrId FROM TrnPensionBillHdr  WHERE billNo =:BillNo ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("BillNo", lLngBillNo);
			lLstVO = lQuery.list();
			Object tuple = lLstVO.get(0);
			lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				if (tuple != null) {
					lLngPkId = (Long) tuple;
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLngPkId;
	}

	public List getBillMvmntVo(Long lLngBillNo) {

		Iterator it = null;
		Object[] tuple = null;
		List lLstDtlsList = new ArrayList();
		try {
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append(" Select A.billRemarks,A.objRemarks FROM TrnBillMvmnt A WHERE A.billNo = :billNo AND A.movemntId = (Select MAX(movemntId) " + " FROM TrnBillMvmnt WHERE billNo =:billNo ) ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setLong("billNo", lLngBillNo);
			List resultList = lQuery.list();
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					lLstDtlsList.add(tuple[0]);
					lLstDtlsList.add(tuple[1]);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
		}
		return lLstDtlsList;
	}

	public List getPayScaleList(Map inputMap, Long lLngId) throws Exception {

		ArrayList<ComboValuesVO> arrPayScaleVo = new ArrayList<ComboValuesVO>();
		StringBuffer strQuery = new StringBuffer();
		List resultList;
		Iterator it;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			strQuery.append(" Select payscale,payscaleDesc  From CmnPayscaleMst WHERE langId=:langId order by payscale");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setLong("langId", lLngId);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrPayScaleVo.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrPayScaleVo;
	}

	public String getLocCodeFromRltPPOTrsryMapping(String lLngLocCode) throws Exception {

		StringBuffer strQuery = new StringBuffer();
		String lStrRes = "";
		List resultList;
		try {
			strQuery.append("SELECT TRSYRY_LOC_CODE FROM Rlt_Ppo_Trsyry_Mapping WHERE PPO_LOC_CODE =:locCode ");
			Query hqlQuery = ghibSession.createSQLQuery(strQuery.toString());
			hqlQuery.setParameter("locCode", lLngLocCode);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				lStrRes = resultList.get(0).toString();
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrRes;
	}

	public List getTiRateForSpecialHeadCode(String lStrHeadCode, String lStrRetDate, String lStrBasic) throws Exception {

		StringBuffer strQuery = new StringBuffer();
		List resultList = new ArrayList();
		Iterator it;
		Object[] tuple = null;
		List lLstDtlsList = new ArrayList();
		try {
			strQuery.append(" Select RPR.newAmount FROM RltPensionHeadcodeSpecial RPR  WHERE RPR.headCode =:heaCode ");
			if (lStrRetDate != null) {
				strQuery.append(" AND (  ( RPR.fromDate <=:lStrRetDate AND RPR.toDate >=:lStrRetDate )   " + " OR ( RPR.fromDate <=:lStrRetDate AND RPR.toDate IS NULL ) )");
			}
			if (lStrBasic.length() > 0) {
				strQuery.append(" AND RPR.oldAmount =:oldAmount ");
			}
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setDate("lStrRetDate", new Date());
			hqlQuery.setBigDecimal("heaCode", new BigDecimal(lStrHeadCode));
			if (lStrBasic.length() > 0) {
				hqlQuery.setBigDecimal("oldAmount", new BigDecimal(lStrBasic));
			}
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				lLstDtlsList.add(resultList.get(0).toString());
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDtlsList;
	}

	public String getLocationName(String lStrLocId, Long lLngID) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		String lStrlocName = "";
		String lStrlocAdd1 = "";
		String lStrlocAdd2 = "";
		String lstrSpace = "                                       ";
		Object[] tuple;
		try {
			lSBQuery.append(" SELECT cl.locName,cl.locAddr1,cl.locAddr2 FROM CmnLocationMst cl  WHERE cl.locationCode = :lLocID AND cl.cmnLanguageMst.langId = :lLagId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lLocID", lStrLocId);
			lQuery.setLong("lLagId", lLngID);

			List lLstVO = lQuery.list();
			Iterator it = lLstVO.iterator();
			if (!lLstVO.isEmpty()) {
				tuple = (Object[]) it.next();
				lStrlocName = tuple[0].toString();
				lStrlocAdd1 = tuple[1] == null ? "" : tuple[1].toString() + " , ";
				lStrlocAdd2 = tuple[2] == null ? "" : tuple[2].toString();
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lStrlocName;
	}

	/**
	 * Method return a Location Name And Address For given Location ID And Lang
	 * Id
	 * 
	 * @author Jatin Miyanee Param lStrLocId , lLngID
	 */
	public String getLocationNameAndAddress(String lStrLocId, Long lLngID) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		String lStrlocName = "";
		String lStrlocAdd1 = "";
		String lStrlocAdd2 = "";
		String lstrSpace = "                                       ";
		Object[] tuple;
		try {
			lSBQuery.append(" SELECT cl.locName,cl.locAddr1,cl.locAddr2 FROM CmnLocationMst cl  WHERE cl.locationCode = :lLocID AND cl.cmnLanguageMst.langId = :lLagId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lLocID", lStrLocId);
			lQuery.setLong("lLagId", lLngID);

			List lLstVO = lQuery.list();
			Iterator it = lLstVO.iterator();
			if (!lLstVO.isEmpty()) {
				tuple = (Object[]) it.next();
				lStrlocName = tuple[0].toString();
				lStrlocAdd1 = tuple[1] == null ? "" : tuple[1].toString() + " , ";
				lStrlocAdd2 = tuple[2] == null ? "" : tuple[2].toString();

			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lStrlocName + "\r\n" + lstrSpace + lStrlocAdd1 + lStrlocAdd2;
	}

	/**
	 * Get Last Bill Created and Paid Month.
	 * 
	 * @param lStrPnsnrCode
	 * @return
	 * @throws Exception
	 */
	public Integer getLastMonth(String lStrPnsnrCode) throws Exception {

		Integer lastMonth = 0;
		String lStrBillType1 = PensionConstants.MNTHPENSION1;
		String lStrBillType2 = PensionConstants.MNTHMONTHLY;
		try {
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append(" select max(h.forMonth) from TrnPensionBillHdr h, TrnPensionBillDtls d  " + " where h.trnPensionBillHdrId = d.trnPensionBillHdrId  and d.pensionerCode = :lpnsrCode "
					+ " and ((h.billType = :lbillType2) or h.billType = :lbillType1) and h.rejected = 0 ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lpnsrCode", lStrPnsnrCode);
			lQuery.setString("lbillType1", lStrBillType1);
			lQuery.setString("lbillType2", lStrBillType2);

			List lLstVO = lQuery.list();
			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lastMonth = Integer.parseInt(lLstVO.get(0).toString());
				}

			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lastMonth;
	}

	public List getFormIssueAuthCombo(Long lLngLangId) throws Exception {

		StringBuffer strQuery = new StringBuffer();
		List resultList;
		try {
			strQuery.append(" SELECT auithId,form22IssueAuth,lpcIssueAuth FROM MstPensionFormIssueAuth WHERE langId =:langId AND " + " isActive =:ActiveFlag ");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setBigDecimal("langId", new BigDecimal(lLngLangId));
			hqlQuery.setString("ActiveFlag", "Y");
			resultList = hqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return resultList;
	}

	/**
	 * Method return a parent location ID for given Location ID
	 * 
	 * @author Sagar
	 */
	public String getParentLocFromLocation(String lStrLocId, Long lLngID) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		String lStrPrntLocID = null;

		try {
			lSBQuery.append(" SELECT cl.parentLocId FROM CmnLocationMst cl WHERE cl.locationCode = :lLocID AND " + " cl.cmnLanguageMst.langId = :lLagId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("lLocID", lStrLocId);
			lQuery.setLong("lLagId", lLngID);

			List lLstVO = lQuery.list();

			if (!lLstVO.isEmpty()) {
				if (lLstVO.get(0) != null) {
					lStrPrntLocID = lLstVO.get(0).toString();
				}

			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lStrPrntLocID;
	}

	public List getSeriesData() throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		Object[] tuple;
		ArrayList<ComboValuesVO> arrSeriesVo = new ArrayList<ComboValuesVO>();
		StringBuilder strQuery = new StringBuilder();
		List resultList;
		Iterator it;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			lSBQuery.append(" select p.series_code,p.series_name from mst_pension_series_dtls p ");
			Query hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) it.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString());
					arrSeriesVo.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return arrSeriesVo;
	}

	public String getSeriesHeadCode(String SeriesCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		String strHeadCode = "";
		try {
			Object tuple;
			Iterator it;

			Session ghibSession = getSession();
			lSBQuery.append(" select distinct rsh.head_code from rlt_pension_series_head_code rsh where rsh.series_code in " + " (" + SeriesCode + ") ");
			Query hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {

					tuple = it.next();
					strHeadCode += tuple.toString();
					if (it.hasNext()) {
						strHeadCode += ",";
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return strHeadCode;
	}

	public String getPensionJottingData(String Fromdate, String lArgsMjrHead, BigDecimal lBDHeadCode, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		String strTotal = "0";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {

			Session ghibSession = getSession();
			int month = Integer.parseInt(Fromdate.substring(4, 6));
			int Year = Integer.parseInt(Fromdate.substring(0, 4));
			Calendar cal2006 = Calendar.getInstance();
			cal2006.setTime(new SimpleDateFormat("dd/MM/yyyy").parse("01/" + Fromdate.substring(4, 6) + "/" + Fromdate.substring(0, 4)));
			int days = cal2006.getActualMaximum(Calendar.DAY_OF_MONTH);

			lSBQuery.append(" select sum(" + nvlOrIfNullString + "(ed.gross_amnt, 0)) lSum" + " from trn_bill_register   tbr, trn_pension_bill_hdr hdr, rpt_expenditure_dtls ed "
					+ " where tbr.bill_no = ed.exp_no And tbr.bill_no = hdr.bill_no And tbr.budmjr_hd in ('" + lArgsMjrHead + "' ) "
					+ " and tbr.voucher_date >=:fromDate and tbr.voucher_date <=:toDate   " + " and tbr.bill_no = hdr.bill_no " + " and hdr.Location_code = :lLocCode ");
			if (lBDHeadCode != null) {
				lSBQuery.append(" and hdr.head_code =:HeadCode  ");
			}
			SQLQuery hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			hqlQuery.setDate("fromDate", sdf.parse(new SimpleDateFormat("dd/MM/yyyy").format(cal2006.getTime())));

			cal2006.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(days + "/" + Fromdate.substring(4, 6) + "/" + Fromdate.substring(0, 4)));
			hqlQuery.setDate("toDate", new SimpleDateFormat("dd/MM/yyyy").parse(days + "/" + Fromdate.substring(4, 6) + "/" + Fromdate.substring(0, 4)));

			if (lBDHeadCode != null) {
				hqlQuery.setBigDecimal("HeadCode", lBDHeadCode);
			}

			hqlQuery.setString("lLocCode", lStrLocCode);

			hqlQuery.addScalar("lSum", Hibernate.BIG_DECIMAL);

			resultList = hqlQuery.list();
			Object tuple;
			Iterator it;
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {

					tuple = it.next();
					if (tuple != null) {
						strTotal = tuple.toString();
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return strTotal;
	}

	public List getPensionJottingDtlData(String Fromdate, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		String strTotal = "0";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			int month = Integer.parseInt(Fromdate.substring(4, 6));
			int Year = Integer.parseInt(Fromdate.substring(0, 4));
			Calendar cal2006 = Calendar.getInstance();
			cal2006.setTime(new SimpleDateFormat("dd/MM/yyyy").parse("01/" + Fromdate.substring(4, 6) + "/" + Fromdate.substring(0, 4)));
			int days = cal2006.getActualMaximum(Calendar.DAY_OF_MONTH);

			lSBQuery.append("  SELECT x.series_name sName, ifnull(amnt, 0) lAmnt " + " FROM    mst_pension_series_dtls x" + " LEFT JOIN"
					+ " (SELECT ifnull(sum(re.gross_amnt - ifnull(rd.amount, 0)), 0) amnt, " + "  msd.series_name AS sname " + " FROM trn_bill_register tbr " + " JOIN " + " trn_pension_bill_hdr bh "
					+ " ON bh.bill_no = tbr.bill_no " + " JOIN " + " rpt_expenditure_dtls re " + " ON tbr.bill_no = re.exp_no " + " LEFT JOIN " + " Rpt_Receipt_Dtls rd "
					+ " ON rd.exp_id = re.exp_id AND rd.mjr_hd = '0071' " + " JOIN " + " rlt_pension_series_head_code rhc " + "  ON rhc.head_code = bh.head_code " + " JOIN "
					+ " rlt_pension_series_bill_type rbt " + " ON rbt.bill_type = bh.bill_type " + " JOIN " + " rlt_pension_series_scheme rsch " + " ON rsch.scheme = bh.scheme " + " JOIN "
					+ " mst_pension_series_dtls msd " + " ON msd.series_code = rhc.series_code " + " AND msd.series_code = rbt.series_code " + " AND msd.series_code = rsch.series_code "
					+ " WHERE     tbr.location_code = re.tsry_code " + " AND tbr.location_code =:locCode " + " AND tbr.voucher_date BETWEEN :fromDate AND :toDate " + " AND msd.series_code >= 26 "
					+ " AND msd.series_code <= 47 " + " GROUP BY msd.series_name) tmp " + " ON x.series_name = tmp.sname " + " WHERE x.series_code >= 26 AND x.series_code <= 47 ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			hqlQuery.addScalar("sName", Hibernate.STRING);
			hqlQuery.addScalar("lAmnt", Hibernate.BIG_DECIMAL);

			hqlQuery.setDate("fromDate", sdf.parse(new SimpleDateFormat("dd/MM/yyyy").format(cal2006.getTime())));
			hqlQuery.setString("locCode", lStrLocCode);

			cal2006.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(days + "/" + Fromdate.substring(4, 6) + "/" + Fromdate.substring(0, 4)));
			hqlQuery.setDate("toDate", new SimpleDateFormat("dd/MM/yyyy").parse(days + "/" + Fromdate.substring(4, 6) + "/" + Fromdate.substring(0, 4)));

			resultList = hqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return resultList;
	}

	public String[] getPensionMISData(String Fromdate, String lStrSeriesCode, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		String[] strTotal = {"0", "0"};
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			int month = Integer.parseInt(Fromdate.substring(4, 6));
			int Year = Integer.parseInt(Fromdate.substring(0, 4));
			Calendar cal2006 = Calendar.getInstance();
			cal2006.setTime(new SimpleDateFormat("dd/MM/yyyy").parse("01/" + Fromdate.substring(4, 6) + "/" + Fromdate.substring(0, 4)));
			int days = cal2006.getActualMaximum(Calendar.DAY_OF_MONTH);

			lSBQuery.append(" select sum(" + nvlOrIfNullString + "(ed.gross_amnt, 0) - " + nvlOrIfNullString + "(rd.amount, 0)) lAmnt1, ");
			/*
			 * if (isOracleDB) {lSBQuery.append(
			 * "sum(case when to_char(tbr.voucher_date, 'dd') between 1 and 15 then "
			 * ); } else {lSBQuery.append(
			 * "sum(case  when day(tbr.voucher_date) between 1 and 15 then "); }
			 */
			lSBQuery.append("sum(case  when EXTRACT(DAY FROM tbr.voucher_date) between 1 and 15 then ");
			lSBQuery.append(" " + nvlOrIfNullString + "(ed.gross_amnt, 0) - " + nvlOrIfNullString
					+ " (rd.amount, 0) end) lAmnt2 from trn_bill_register tbr inner join TRN_PENSION_BILL_HDR HDR USE INDEX (ind_trn_pensionbill_hdrbillno ) on "
					+ " TBR.BILL_NO = HDR.BILL_NO and HDR.Location_code = :lLocCode " + " inner join RPT_EXPENDITURE_DTLS ED on TBR.BILL_NO = ED.EXP_NO "
					+ " LEFT OUTER JOIN RPT_RECEIPT_DTLS RD ON RD.EXP_ID = ED.EXP_ID  and RD.MJR_HD = '0071' " + " inner join RLT_PENSION_SERIES_HEAD_CODE R on HDR.HEAD_CODE= R.HEAD_CODE "
					+ " inner join RLT_PENSION_SERIES_BILL_TYPE RPN on R.SERIES_CODE = RPN.SERIES_CODE and RPN.BILL_TYPE= HDR.BILL_TYPE "
					+ " inner join RLT_PENSION_SERIES_SCHEME RPS on RPN.SERIES_CODE = RPS.SERIES_CODE and RPS.SCHEME = HDR.SCHEME "
					+ " inner join MST_PENSION_SERIES_DTLS S on RPS.SERIES_CODE= S.SERIES_CODE and S.SERIES_CODE IN (" + lStrSeriesCode + ") "
					+ "  WHERE TBR.VOUCHER_DATE >= :fromDate AND TBR.VOUCHER_DATE <= :toDate");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			hqlQuery.addScalar("lAmnt1", Hibernate.BIG_DECIMAL);
			hqlQuery.addScalar("lAmnt2", Hibernate.BIG_DECIMAL);

			hqlQuery.setParameter("lLocCode", lStrLocCode);
			hqlQuery.setDate("fromDate", sdf.parse(new SimpleDateFormat("dd/MM/yyyy").format(cal2006.getTime())));
			cal2006.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(days + "/" + Fromdate.substring(4, 6) + "/" + Fromdate.substring(0, 4)));
			hqlQuery.setDate("toDate", new SimpleDateFormat("dd/MM/yyyy").parse(days + "/" + Fromdate.substring(4, 6) + "/" + Fromdate.substring(0, 4)));

			resultList = hqlQuery.list();
			Object[] tuple;
			Iterator it;
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {

					tuple = (Object[]) it.next();
					if (tuple[0] != null) {
						strTotal[0] = tuple[0].toString();
					}
					if (tuple[1] != null) {
						strTotal[1] = tuple[1].toString();
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return strTotal;
	}

	public String[] getSeriesData(String Series) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		String[] data = new String[3];
		try {
			Object[] tuple;
			Iterator it;

			lSBQuery.append(" select rpsb.bill_type, rpss.scheme, rpsh.head_code " + " from mst_pension_series_dtls      msd,  rlt_pension_series_bill_type rpsb, "
					+ " rlt_pension_series_head_code rpsh,  rlt_pension_series_scheme    rpss " + " where rpss.series_code = msd.series_code and  rpsh.series_code = msd.series_code and"
					+ " rpsb.series_code = msd.series_code  and msd.series_code =:seriesName");
			Query hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			hqlQuery.setString("seriesName", Series);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {

					tuple = (Object[]) it.next();
					data[0] = tuple[0].toString();
					data[1] = tuple[1].toString();
					data[2] = tuple[2].toString();
				}
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return data;
	}

	public List getSeriesDynameicData(List Series, Date fromDate, Date toDate) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		String[] data = new String[3];
		try {
			Object[] tuple;
			Iterator it;

			lSBQuery.append(" SELECT distinct BH.BILL_TYPE  FROM trn_pension_bill_hdr bh,trn_pension_bill_dtls bd " + " WHERE bh.trn_pension_bill_hdr_id = bd.trn_pension_bill_hdr_id "
					+ " AND bh.created_date >= :fromDate AND bh.created_date <= :toDate " + " group by bh.scheme,BH.BILL_TYPE,bh.created_date");
			Query hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			hqlQuery.setDate("fromDate", fromDate);
			hqlQuery.setDate("toDate", toDate);
			resultList = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return resultList;
	}

	public Map getSeriesCashBookDataFor2ndList(String Series, String lStrMajorHead, Date fromDate, Date toDate, String locCode) throws Exception {

		Map<String, BigDecimal> lMapSeriesAmt = null;

		try {
			List resultList = null;
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT SUM(bbr.bill_Net_Amount + bbr.deduction_a) lAmnt, md.series_Name seriesName ");
			lSBQuery.append(" FROM Rlt_Pension_Headcode_Chargable ph, Rlt_Pension_Series_Head_Code mh, Rlt_Pension_Series_Bill_Type rb, ");
			lSBQuery.append(" Rlt_Pension_Series_Scheme ms, Mst_Pension_Series_Dtls md, Trn_Pension_Bill_Hdr bh, Trn_Bill_Register bbr USE INDEX(ind_trnbilreg_loccode,ind_tbr_curr_bill_status) ");
			lSBQuery.append(" WHERE bbr.voucher_date BETWEEN :fromDate AND :toDate AND bbr.curr_Bill_Status = 160 ");
			lSBQuery.append(" AND bh.location_code = :locCode AND bbr.location_code = :locCode ");
			lSBQuery.append(" AND (bh.rejected = 0 OR bh.rejected IS NULL) ");

			if (!"-1".equals(Series)) {
				lSBQuery.append(" AND md.series_Code = :seriesCode ");
			}

			if (!"-1".equals(lStrMajorHead)) {
				lSBQuery.append(" AND bbr.budmjr_hd = :majorHead ");
			}

			lSBQuery.append(" AND rb.bill_Type = CASE ");
			lSBQuery.append(" WHEN bh.bill_Type IN ('Monthly','OMR') THEN 'PENSION' ");
			lSBQuery.append(" ELSE bh.bill_Type ");
			lSBQuery.append(" END ");

			lSBQuery.append(" AND ph.bill_Type = CASE ");
			lSBQuery.append(" WHEN rb.bill_Type IN ('Monthly','OMR') THEN 'PENSION' ");
			lSBQuery.append(" ELSE rb.bill_Type ");
			lSBQuery.append(" END ");

			lSBQuery.append(" AND bh.head_Code = mh.head_Code AND ms.scheme = CASE WHEN bh.scheme IS NULL THEN 'RUBARU' ELSE bh.scheme END ");
			lSBQuery.append(" AND md.series_Code = mh.series_Code AND md.series_Code = rb.series_Code AND md.series_Code = ms.series_Code ");
			lSBQuery.append(" AND bbr.bill_No = bh.book_Bill_No AND ph.head_Code = mh.head_Code ");
			lSBQuery.append(" GROUP BY md.series_Name ");

			if (!(Series != null && !"-1".equals(Series)) && ("2071".equals(lStrMajorHead) || "-1".equals(lStrMajorHead))) {
				lSBQuery.append(" UNION ALL ");

				lSBQuery.append(" SELECT SUM(br.bill_Net_Amount + br.deduction_a) lAmnt, br.demand_code seriesName ");
				lSBQuery.append(" FROM trn_bill_register br WHERE br.subject_id  = 38 AND br.voucher_date ");
				lSBQuery.append(" BETWEEN :fromDate AND :toDate AND br.location_code = :locCode AND br.curr_bill_status = 160 AND ");
				lSBQuery.append(" br.demand_code IN ('048', '077') AND br.budmjr_hd = '2071' AND br.bud_submjr_hd = '01' ");
				lSBQuery.append(" AND br.bud_min_hd = '108' AND br.bud_sub_hd = '01' ");
				lSBQuery.append(" GROUP BY br.demand_code, br.budmjr_hd, br.bud_submjr_hd, br.bud_min_hd, br.bud_sub_hd ");

				lSBQuery.append(" UNION ALL ");

				lSBQuery.append(" SELECT SUM(br.bill_Net_Amount + br.deduction_a) lAmnt, 'STO and PSB' seriesName ");
				lSBQuery.append("  FROM trn_bill_register br WHERE br.subject_id  = 38 AND br.voucher_date BETWEEN :fromDate AND :toDate ");
				lSBQuery.append(" AND br.location_code = :locCode AND br.curr_bill_status = 160 AND ");
				lSBQuery.append(" br.demand_code NOT IN ('048', '077') AND br.budmjr_hd = '2071' ");
				lSBQuery.append(" GROUP BY br.budmjr_hd ");
			}

			SQLQuery hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");

			hqlQuery.addScalar("lAmnt", Hibernate.BIG_DECIMAL);
			hqlQuery.addScalar("seriesName", Hibernate.STRING);

			hqlQuery.setDate("fromDate", fromDate);
			hqlQuery.setDate("toDate", toDate);
			if (Series != null && !"-1".equals(Series)) {
				hqlQuery.setString("seriesCode", Series);
			}
			if (lStrMajorHead != null && !"-1".equals(lStrMajorHead)) {
				hqlQuery.setString("majorHead", lStrMajorHead);
			}
			hqlQuery.setString("locCode", locCode);

			resultList = hqlQuery.list();

			if (resultList != null && !resultList.isEmpty()) {

				Object[] lObjArr = null;
				lMapSeriesAmt = new TreeMap();

				for (int y = 0; y < resultList.size(); y++) {

					lObjArr = (Object[]) resultList.get(y);

					lMapSeriesAmt.put((String) lObjArr[1], (BigDecimal) lObjArr[0]);
				}
			}

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lMapSeriesAmt;
	}

	public List getSeriesCashBookData(String Series, String lStrMajorHead, Date fromDate, Date toDate, String locCode, Integer lIntFirstSecondList) throws Exception {

		List finalList = new ArrayList();

		try {
			StringBuilder lSBQuery = new StringBuilder();
			List resultList = null;
			List resultList2 = null;

			lSBQuery.append(" SELECT bbr.voucher_date vchrDate, SUM(bbr.bill_Net_Amount + bbr.deduction_a) lAmnt, md.series_Name seriesName ");
			lSBQuery.append(" FROM Rlt_Pension_Headcode_Chargable ph, Rlt_Pension_Series_Head_Code mh, Rlt_Pension_Series_Bill_Type rb, ");
			lSBQuery.append(" Rlt_Pension_Series_Scheme ms, Mst_Pension_Series_Dtls md, Trn_Pension_Bill_Hdr bh, Trn_Bill_Register bbr USE INDEX(ind_trnbilreg_loccode,ind_tbr_curr_bill_status) ");
			lSBQuery.append(" WHERE bbr.voucher_date BETWEEN :fromDate AND :toDate AND bbr.curr_Bill_Status = 160 ");
			lSBQuery.append(" AND bh.location_code = :locCode AND bbr.location_code = :locCode ");
			lSBQuery.append(" AND (bh.rejected = 0 OR bh.rejected IS NULL) ");

			if (!"-1".equals(Series)) {
				lSBQuery.append(" AND md.series_Code = :seriesCode ");
			}

			if (!"-1".equals(lStrMajorHead)) {
				lSBQuery.append(" AND bbr.budmjr_hd = :majorHead ");
			}

			lSBQuery.append(" AND rb.bill_Type = CASE ");
			lSBQuery.append(" WHEN bh.bill_Type IN ('Monthly','OMR') THEN 'PENSION' ");
			lSBQuery.append(" ELSE bh.bill_Type ");
			lSBQuery.append(" END ");

			lSBQuery.append(" AND ph.bill_Type = CASE ");
			lSBQuery.append(" WHEN rb.bill_Type IN ('Monthly','OMR') THEN 'PENSION' ");
			lSBQuery.append(" ELSE rb.bill_Type ");
			lSBQuery.append(" END ");

			lSBQuery.append(" AND bh.head_Code = mh.head_Code AND ms.scheme = CASE WHEN bh.scheme IS NULL THEN 'RUBARU' ELSE bh.scheme END ");
			lSBQuery.append(" AND md.series_Code = mh.series_Code AND md.series_Code = rb.series_Code AND md.series_Code = ms.series_Code ");
			lSBQuery.append(" AND bbr.bill_No = bh.book_Bill_No AND ph.head_Code = mh.head_Code ");
			lSBQuery.append(" GROUP BY bbr.voucher_date, md.series_Code, md.series_Name ");

			if (!(Series != null && !"-1".equals(Series)) && ("2071".equals(lStrMajorHead) || "-1".equals(lStrMajorHead))) {
				lSBQuery.append(" UNION ALL ");

				lSBQuery.append(" SELECT br.voucher_date vchrDate, SUM(br.bill_Net_Amount + br.deduction_a) lAmnt, br.demand_code seriesName ");
				lSBQuery.append(" FROM trn_bill_register br WHERE br.subject_id  = 38 AND br.voucher_date ");
				lSBQuery.append(" BETWEEN :fromDate AND :toDate AND br.location_code = :locCode AND br.curr_bill_status = 160 AND ");
				lSBQuery.append(" br.demand_code IN ('048', '077') AND br.budmjr_hd = '2071' AND br.bud_submjr_hd = '01' ");
				lSBQuery.append(" AND br.bud_min_hd = '108' AND br.bud_sub_hd = '01' ");
				lSBQuery.append(" GROUP BY br.voucher_date, br.demand_code, br.budmjr_hd, br.bud_submjr_hd, br.bud_min_hd, br.bud_sub_hd ");

				lSBQuery.append(" UNION ALL ");

				lSBQuery.append(" SELECT br.voucher_date vchrDate, SUM(br.bill_Net_Amount + br.deduction_a) lAmnt, 'STO and PSB' seriesName  FROM trn_bill_register br ");
				lSBQuery.append(" WHERE br.subject_id  = 38 AND br.voucher_date BETWEEN :fromDate AND :toDate ");
				lSBQuery.append(" AND br.location_code = :locCode AND br.curr_bill_status = 160 AND ");
				lSBQuery.append(" br.demand_code NOT IN ('048', '077') AND br.budmjr_hd = '2071' ");
				lSBQuery.append(" GROUP BY br.voucher_date, br.budmjr_hd ");
			}

			lSBQuery.append(" ORDER BY 1, 3 ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			hqlQuery.setCacheable(true).setCacheRegion("pensionCache");

			hqlQuery.addScalar("vchrDate", Hibernate.DATE);
			hqlQuery.addScalar("lAmnt", Hibernate.BIG_DECIMAL);
			hqlQuery.addScalar("seriesName", Hibernate.STRING);

			hqlQuery.setDate("fromDate", fromDate);
			hqlQuery.setDate("toDate", toDate);
			if (Series != null && !"-1".equals(Series)) {
				hqlQuery.setString("seriesCode", Series);
			}
			if (lStrMajorHead != null && !"-1".equals(lStrMajorHead)) {
				hqlQuery.setString("majorHead", lStrMajorHead);
			}
			hqlQuery.setString("locCode", locCode);

			resultList = hqlQuery.list();

			// we need to create map in case of Series Wise date wise summary
			// and Cash Book Report
			if (resultList != null && !resultList.isEmpty() && lIntFirstSecondList != null) {

				lSBQuery = new StringBuilder();

				lSBQuery.append("SELECT DISTINCT s.series_code, s.series_name ");
				lSBQuery.append("FROM mst_pension_series_dtls s, rlt_pension_series_head_code h, rlt_pension_headcode_chargable hc ");
				lSBQuery.append("WHERE s.series_code = h.series_code AND ");
				lSBQuery.append("h.head_code = hc.head_code ");
				if (lStrMajorHead != null && !"-1".equals(lStrMajorHead)) {
					lSBQuery.append(" AND hc.mjrhd_code = :majorHead  ");
				}
				lSBQuery.append("ORDER BY series_code ");

				hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

				if (lStrMajorHead != null && !"-1".equals(lStrMajorHead)) {
					hqlQuery.setString("majorHead", lStrMajorHead);
				}

				resultList2 = hqlQuery.list();

				Map lMapSeries = new TreeMap();
				Map lMapVoucherDate = null;
				Object[] lObjArr = null;
				Object[] lObjArr2 = null;

				for (int i = 0; i < resultList2.size(); i++) {

					lObjArr2 = (Object[]) resultList2.get(i);
					lMapVoucherDate = new TreeMap();
					for (int j = 0; j < resultList.size(); j++) {

						lObjArr = (Object[]) resultList.get(j);

						if (((String) lObjArr2[1]).equals(lObjArr[2])) {

							lMapVoucherDate.put(lObjArr[0], lObjArr[1]);
							lMapSeries.put(lObjArr2[1], lMapVoucherDate);
						}
					}
				}

				Map lMapVoucherDate077 = new TreeMap();
				Map lMapVoucherDate048 = new TreeMap();
				Map lMapVoucherDateSTOPSB = new TreeMap();

				for (int j = 0; j < resultList.size(); j++) {

					lObjArr = (Object[]) resultList.get(j);

					if (("077").equals(lObjArr[2])) {

						lMapVoucherDate077.put(lObjArr[0], lObjArr[1]);
						lMapSeries.put("077", lMapVoucherDate077);
					} else if (("048").equals(lObjArr[2])) {

						lMapVoucherDate048.put(lObjArr[0], lObjArr[1]);
						lMapSeries.put("048", lMapVoucherDate048);
					} else if (("STO and PSB").equals(lObjArr[2])) {

						lMapVoucherDateSTOPSB.put(lObjArr[0], lObjArr[1]);
						lMapSeries.put("STO and PSB", lMapVoucherDateSTOPSB);
					}
				}

				finalList.add(lMapSeries);
			}

			// if(!(lIntFirstSecondList != null &&
			// !"-1".equals(lStrMajorHead))){
			if (lIntFirstSecondList == null) {

				finalList.addAll(resultList);
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return finalList;
	}

	public List getReconciationData(Date fromDate, Date toDate, String lStrLoc) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		try {

			lSBQuery.append("select " + nvlOrIfNullString + "(amnt1, 0) lAmnt1," + nvlOrIfNullString + " (amnt2, 0) lAmnt2,msd.series_name sName," + nvlOrIfNullString + "(amnt3, 0) lAmnt3," + " "
					+ nvlOrIfNullString + "(amnt4, 0) lAmnt4," + nvlOrIfNullString + "(amnt5, 0) lAmnt5" + " from mst_pension_series_dtls msd left join (select " + nvlOrIfNullString
					+ "(sum(re.gross_amnt), 0) amnt1," + " sum(" + nvlOrIfNullString + "(rd.amount, 0)) amnt2,series_name sname, " + nvlOrIfNullString + " (sum(tpbd.allcation_bf_1_11_56), 0) amnt3, "
					+ nvlOrIfNullString + " (sum(tpbd.allcation_af_1_11_56), 0) amnt4," + nvlOrIfNullString + " (sum(tpbd.allcation_af_1_05_60),  0) amnt5"
					+ " from trn_bill_register tbr join trn_pension_bill_hdr bh on tbr.bill_no = bh.bill_no " + " join rpt_expenditure_dtls re on tbr.bill_no = re.exp_no "
					+ " left join Rpt_Receipt_Dtls rd on rd.exp_id = re.exp_id and rd.mjr_hd = '0071' "
					+ " join trn_pension_bill_dtls tpbd on tpbd.trn_pension_bill_hdr_id = bh.trn_pension_bill_hdr_id "
					+ " and bh.for_month = tpbd.pay_for_month join rlt_pension_series_head_code rhc on rhc.head_code = bh.head_code "
					+ " join rlt_pension_series_bill_type rbt on rbt.bill_type = bh.bill_type " + " join rlt_pension_series_scheme rsch on rsch.scheme = bh.scheme "
					+ " join mst_pension_series_dtls msd on msd.series_code = rhc.series_code " + " and msd.series_code = rbt.series_code and msd.series_code = rsch.series_code"
					+ " where tbr.location_code =  re.tsry_code and tbr.location_code = :locCode " + " and tbr.voucher_date between :fromDate and :toDate  and  tbr.audit_status != 2"
					+ " group by msd.series_name) temp on msd.series_name = temp.sname ");
			SQLQuery hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			hqlQuery.addScalar("lAmnt1", Hibernate.BIG_DECIMAL);
			hqlQuery.addScalar("lAmnt2", Hibernate.BIG_DECIMAL);
			hqlQuery.addScalar("sName", Hibernate.STRING);
			hqlQuery.addScalar("lAmnt3", Hibernate.BIG_DECIMAL);
			hqlQuery.addScalar("lAmnt4", Hibernate.BIG_DECIMAL);
			hqlQuery.addScalar("lAmnt5", Hibernate.BIG_DECIMAL);

			hqlQuery.setDate("fromDate", fromDate);
			hqlQuery.setDate("toDate", toDate);
			hqlQuery.setString("locCode", lStrLoc);

			resultList = hqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return resultList;
	}

	public String[] getAllocationData(String strHeadcode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		String[] data = new String[3];
		data[0] = "0";
		data[1] = "0";
		data[2] = "0";

		try {
			lSBQuery.append(" SELECT SUM(" + nvlOrIfNullString + "(BB56,0)),SUM(" + nvlOrIfNullString + "(AB56,0)), " + " SUM(" + nvlOrIfNullString + "(AG60,0)),SUM(" + nvlOrIfNullString
					+ "(BB56,0) + " + nvlOrIfNullString + "(AB56,0) + " + " " + nvlOrIfNullString + "(AG60,0))  FROM (SELECT MPH.HEAD_CODE HDCODE,MPH.DESCRIPTION DESCRIPTION,  "
					+ " SUM(TRH.RED_BF_1_11_56) BB56,SUM(TRH.RED_AF_1_11_56) AB56,  SUM(TRH.RED_AF_1_05_60) AG60 "
					+ " FROM TRN_PENSION_RQST_HDR TRH, MST_PENSION_HEADCODE MPH  WHERE MPH.HEAD_CODE = TRH.HEAD_CODE " + " AND TRH.HEAD_CODE IN (" + strHeadcode
					+ ")  AND TRH.STATUS = 'Continue'   GROUP BY MPH.HEAD_CODE, " + " MPH.DESCRIPTION) GROUP BY HDCODE, DESCRIPTION");
			Query hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			resultList = hqlQuery.list();
			Object[] tuple;
			Iterator it;
			if (!resultList.isEmpty()) {
				it = resultList.iterator();
				while (it.hasNext()) {
					tuple = (Object[]) it.next();
					data[0] = tuple[0].toString();
					data[1] = tuple[1].toString();
					data[2] = tuple[2].toString();
				}
			}
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return data;
	}

	public List getPensionHuzurData(Date Fromdate, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		String[] data = new String[2];
		try {

			lSBQuery.append("SELECT " + nvlOrIfNullString + "(amnt1,0) lAmnt1," + nvlOrIfNullString + " (amnt2,0) lAmnt2,msd.series_name sName FROM mst_pension_series_dtls msd LEFT JOIN( "
					+ "  SELECT SUM(" + nvlOrIfNullString + "(re.gross_amnt, 0)) amnt1, SUM(" + nvlOrIfNullString + " (rd.amount, 0)) amnt2,series_name sname  FROM trn_bill_register tbr JOIN "
					+ " trn_pension_bill_hdr bh ON tbr.bill_no = bh.bill_no " + " JOIN rpt_expenditure_dtls re ON tbr.bill_no = re.exp_no LEFT "
					+ " JOIN Rpt_Receipt_Dtls rd ON rd.exp_id = re.exp_id AND rd.mjr_hd = '0071' " + " JOIN rlt_pension_series_head_code rhc ON rhc.head_code = bh.head_code"
					+ " JOIN rlt_pension_series_bill_type rbt ON rbt.bill_type = bh.bill_type" + " JOIN rlt_pension_series_scheme rsch ON rsch.scheme = bh.scheme "
					+ " JOIN mst_pension_series_dtls msd ON msd.series_code = rhc.series_code " + " AND msd.series_code = rbt.series_code AND msd.series_code = rsch.series_code "
					+ " WHERE tbr.voucher_date =:Fromdate AND tbr.location_code = :lLocCode AND " + " tbr.location_code = re.tsry_code GROUP BY msd.series_name) tmp ON msd.series_name = tmp.sname ");

			SQLQuery hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			hqlQuery.setDate("Fromdate", Fromdate);
			hqlQuery.addScalar("lAmnt1", Hibernate.BIG_DECIMAL);
			hqlQuery.addScalar("lAmnt2", Hibernate.BIG_DECIMAL);
			hqlQuery.addScalar("sName", Hibernate.STRING);

			hqlQuery.setParameter("lLocCode", lStrLocCode);

			resultList = hqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return resultList;
	}

	public List getPensionAccountData(Date Fromdate, Date todate, String AccountType, String SeriesData, String lStrLocCode) throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;
		try {
			lSBQuery.append(" SELECT tbr.voucher_no,c.advice_no,bd.pensioner_name,bd.ppo_no,bh.head_code,bd.allcation_bf_1_11_56, "
					+ " bd.allcation_af_1_11_56,bd.allcation_af_1_05_60,bd.pension_amount,bd.pensn_cut_amount,  " + "bd.dp_amount,bd.cvp_month_amount,  bd.reduced_pension ,"
					+ " bd.ti_amount,bd.ti_arrear_amount,bd.medical_allowence_amount,bd.other_benefits,bd.ir_amount, "
					+ " tbr.bill_gross_amount,bd.income_tax_cut_amount,bd.recovery_amount,  tbr.bill_net_amount,bd.arrear_amount  "
					+ "  FROM trn_pension_bill_dtls bd, trn_pension_bill_hdr bh,trn_cheque_dtls c,rlt_bill_cheque rbc , "
					+ " trn_bill_register tbr  WHERE bh.trn_pension_bill_hdr_id = bd.trn_pension_bill_hdr_id  " + " and bh.location_code = :lLocCode and bh.location_code = tbr.location_code "
					+ " And tbr.bill_no = bh.bill_no and c.cheque_id = rbc.cheque_id and rbc.bill_no = tbr.bill_no ");

			if (Fromdate.equals(todate)) {
				lSBQuery.append(" AND c.chq_dsptch_date >= :fromDate ");
			} else {
				lSBQuery.append(" AND c.chq_dsptch_date >= :fromDate AND c.chq_dsptch_date <= :toDate ");
			}
			if (AccountType != null && !"-1".equals(AccountType)) {
				lSBQuery.append(" AND c.cheque_type =:chequeType ");
			}
			lSBQuery.append(" AND bh.for_month = bd.pay_for_month ");
			if (SeriesData != null && !"-1".equals(SeriesData)) {
				lSBQuery.append("and  (bh.bill_type,bh.scheme,bh.head_code) in " + " ( select rpsb.bill_type, rpss.scheme, rpsh.head_code  from mst_pension_series_dtls      msd, "
						+ " rlt_pension_series_bill_type rpsb, rlt_pension_series_head_code rpsh, rlt_pension_series_scheme    rpss"
						+ " where rpss.series_code = msd.series_code and rpsh.series_code = msd.series_code and" + " rpsb.series_code = msd.series_code and msd.series_code =" + SeriesData + ")");
			}

			Query hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			hqlQuery.setDate("fromDate", Fromdate);
			hqlQuery.setString("lLocCode", lStrLocCode);

			if (!Fromdate.equals(todate)) {
				hqlQuery.setDate("toDate", todate);
			}

			if (AccountType != null && !"-1".equals(AccountType)) {
				hqlQuery.setString("chequeType", AccountType);

			}

			resultList = hqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return resultList;
	}

	/**
	 * Method Check for PPO Case First Payment
	 * 
	 * @param lStrPensionerCode
	 * @throws Exception
	 * @author Sagar
	 */
	public int checkForFirstPayment(String lStrPensionerCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();

		int lIntMonth = 0;

		List lLstReturn = null;

		Query lQuery = null;

		try {
			lSBQuery.append(" SELECT COUNT(*) FROM TrnPensionBillHdr A, TrnPensionBillDtls B " + " WHERE A.trnPensionBillHdrId = B.trnPensionBillHdrId AND B.pensionerCode = :pensionerCode ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);

			lLstReturn = lQuery.list();

			if (lLstReturn.get(0) != null && !lLstReturn.isEmpty()) {
				lIntMonth = Integer.parseInt(lLstReturn.get(0).toString());
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lIntMonth;
	}

	public List getAuditorsForCaseAndBill(String lStrLocCode, String lStrDocId, String lStrLevelId, Long lLngLangId) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstReturn = null;
		List userList = new ArrayList();
		Query lQuery = null;
		String lStrName = "";
		try {
			lSBQuery.append("SELECT O.EMP_PREFIX,O.EMP_FNAME,O.EMP_MNAME,O.EMP_LNAME,R.POST_ID"
					+ " FROM ORG_POST_MST P,ORG_USERPOST_RLT R,ORG_EMP_MST O, WF_HIERARCHY_REFERENCE_MST WHRM,WF_HIERACHY_POST_MPG WHPM"
					+ " WHERE R.POST_ID = P.POST_ID and O.LANG_ID =:langId and R.POST_ID = WHPM.POST_ID and" + " WHPM.LOC_ID =:locCode AND WHRM.DOC_ID =:DocId AND"
					+ " WHPM.LEVEL_ID =:levelId AND WHRM.HIERACHY_REF_ID = WHPM.HIERACHY_REF_ID and R.USER_ID = O.USER_ID "
					+ " AND O.activate_flag = 1 AND P.activate_flag = R.activate_flag AND R.activate_flag = O.activate_flag AND O.activate_flag = WHPM.activate_flag ");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setLong("langId", lLngLangId);
			lQuery.setString("levelId", lStrLevelId);
			lQuery.setString("DocId", lStrDocId);
			lQuery.setString("locCode", lStrLocCode);
			lLstReturn = lQuery.list();
			Iterator it = lLstReturn.iterator();
			String[] result = null;
			int lIntLoopJ = 0;
			Object[] tuple = null;
			while (it.hasNext()) {
				tuple = (Object[]) it.next();
				if (tuple != null) {
					lStrName = "";
					result = new String[2];
					/*
					 * if (tuple[0] != null) { lStrName = lStrName + " " +
					 * tuple[0].toString(); }
					 */
					if (tuple[1] != null) {
						lStrName = lStrName + " " + tuple[1].toString();
					}
					if (tuple[2] != null) {
						lStrName = lStrName + " " + tuple[2].toString();
					}
					if (tuple[3] != null) {
						lStrName = lStrName + " " + tuple[3].toString();
					}
					result[0] = lStrName;
					if (tuple[4] != null) {
						result[1] = tuple[4].toString();
					}
					userList.add(result);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return userList;
	}

	public List getNewBasicByBasicAmounts(BigDecimal lBDAmount1, BigDecimal lBDAmount2) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstRes = new ArrayList();
		Query lQuery = null;
		try {
			lSBQuery.append(" SELECT MROP.NEW_AMOUNT,MROP.OLD_AMOUNT FROM MST_PENSION_ROP_2006 MROP WHERE ");
			lSBQuery.append(" MROP.OLD_AMOUNT in(:lBDAmount1,:lBDAmount2) ");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setBigDecimal("lBDAmount1", lBDAmount1);
			lQuery.setBigDecimal("lBDAmount2", lBDAmount2);

			lLstRes = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstRes;
	}

	public List getAuditorPostIdByBranchCode(String lStrBranchCode, String lStrLocCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstRes = new ArrayList();
		Query lQuery = null;
		try {
			lSBQuery.append(" SELECT POST_ID FROM RLT_AUDITOR_BANK WHERE BRANCH_CODE=:branchCode ");
			lSBQuery.append(" AND PENSION_SCHEME =:scheme AND LOCATION_CODE = :LocCode ");
			lSBQuery.append(" AND BILL_TYPE IS NULL ");
			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setString("branchCode", lStrBranchCode);
			lQuery.setString("scheme", "IRLA");
			lQuery.setString("LocCode", lStrLocCode);
			lLstRes = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstRes;
	}

	public List getAllDiseases() throws Exception {

		ArrayList arrDiseases = new ArrayList();
		ComboValuesVO vo;
		List resultList;
		StringBuffer strQuery = new StringBuffer();

		try {
			strQuery.append(" SELECT diseaseCode,diseaseName FROM CmnDiseaseMst order by diseaseId ");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				Iterator it = resultList.iterator();
				it = resultList.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					vo = new ComboValuesVO();

					vo.setId(tuple[0].toString());
					vo.setDesc(tuple[1].toString());
					arrDiseases.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrDiseases;
	}

	public List getAllHospitals(String lStrLocationCode) throws Exception {

		ArrayList arrDiseases = new ArrayList();
		ComboValuesVO vo;
		List resultList;
		StringBuffer strQuery = new StringBuffer();

		try {
			strQuery.append(" SELECT hospitalCode,hospitalName FROM MstPensionHospital WHERE locationCode = :locationCode order by hospitalId ");
			Query hqlQuery = ghibSession.createQuery(strQuery.toString());
			hqlQuery.setString("locationCode", lStrLocationCode);

			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				Iterator it = resultList.iterator();
				it = resultList.iterator();
				while (it.hasNext()) {
					Object[] tuple = (Object[]) it.next();
					vo = new ComboValuesVO();

					vo.setId(tuple[0].toString());
					vo.setDesc(tuple[1].toString());
					arrDiseases.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrDiseases;
	}

	public List<ComboValuesVO> getPPONo(String lStrPPO, String lStrLocation) throws Exception {

		StringBuffer strQuery = new StringBuffer();
		ArrayList<ComboValuesVO> arrPPONo = new ArrayList<ComboValuesVO>();
		ComboValuesVO vo;
		List resultList;
		try {
			strQuery.append(" select rq.ppo_No from trn_Pension_Rqst_Hdr rq where rq.location_Code = :lCode  and "
					+ " rq.case_Status = 'APPROVED' and rq.status = 'Continue'  and rq.ppo_No like :lPPO Order By rq.ppo_No ");

			Query hqlQuery = ghibSession.createSQLQuery(strQuery.toString());

			hqlQuery.setString("lCode", lStrLocation);
			hqlQuery.setString("lPPO", lStrPPO + "%");
			hqlQuery.setMaxResults(20);
			resultList = hqlQuery.list();
			if (!resultList.isEmpty()) {
				Iterator it = resultList.iterator();
				it = resultList.iterator();
				while (it.hasNext()) {
					String tuple = (String) it.next();
					vo = new ComboValuesVO();

					vo.setId(tuple);
					vo.setDesc(tuple);
					arrPPONo.add(vo);
				}
			}

		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrPPONo;
	}

	public List getSeriesNames() throws Exception {

		StringBuilder lSBQuery = new StringBuilder();
		List resultList;

		try {
			lSBQuery.append("select mpsd.series_name from mst_pension_series_dtls mpsd");
			Query hqlQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			resultList = hqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}

		return resultList;
	}

	// Correction Report
	public List getCorrectionReportData(String argPPONO, Date argFromDate, Date argToDate) throws Exception {

		List lLstCorRepData;
		StringBuffer lSBQry = new StringBuffer();

		try {

			/*
			 * SELECT PC.FIELD_TYPE, PC.APPROVAL_STATUS, OEM.EMP_FNAME ||
			 * OEM.EMP_MNAME || OEM.EMP_LNAME CRTD, PC.CREATED_DATE,
			 * PC.PRVS_FIELD_VALUE, PC.CRNT_FIELD_VALUE, PC.APPROVED_POST_ID,
			 * PC.APPROVED_DATE FROM TRN_PENSION_CORRECTION_DTLS PC, ORG_EMP_MST
			 * OEM, ORG_USERPOST_RLT OUR WHERE PC.CREATED_DATE >= '12-mar-2006'
			 * AND PC.CREATED_DATE <= '12-mar-2009' AND PC.APPROVAL_STATUS <>
			 * 'S' AND OEM.USER_ID = OUR.USER_ID AND OUR.POST_ID =
			 * PC.CREATED_POST_ID AND OEM.LANG_ID = 1 AND OEM.ACTIVATE_FLAG = 1
			 * AND PC.PENSIONER_CODE IN (SELECT PH.PENSIONER_CODE FROM
			 * TRN_PENSION_RQST_HDR PH WHERE PH.PPO_NO = 'DPP/P/059857' AND
			 * PH.APPROVE_STATUS = 'APPROVED')
			 */

			lSBQry.append(" SELECT PC.FIELD_TYPE, PC.APPROVAL_STATUS, OEM.EMP_FNAME ||' '|| OEM.EMP_MNAME ||' '|| "
					+ "OEM.EMP_LNAME CRTD, PC.CREATED_DATE, PC.PRVS_FIELD_VALUE, PC.CRNT_FIELD_VALUE, PC.APPROVED_POST_ID, "
					+ "PC.APPROVED_DATE   FROM TRN_PENSION_CORRECTION_DTLS PC,   ORG_EMP_MST                 " + "OEM,   ORG_USERPOST_RLT            OUR   WHERE PC.CREATED_DATE >= :fromDate AND   "
					+ "PC.CREATED_DATE <= :toDate AND PC.APPROVAL_STATUS <> :aprvStat AND   OEM.USER_ID = "
					+ "OUR.USER_ID AND OUR.POST_ID = PC.CREATED_POST_ID AND OEM.LANG_ID = :langId AND OEM.ACTIVATE_FLAG = OUR.ACTIVATE_FLAG AND OEM.ACTIVATE_FLAG = :actvFlag ");

			if (!"".equals(argPPONO)) {
				lSBQry.append(" AND PC.PENSIONER_CODE IN        (SELECT PH.PENSIONER_CODE           "
						+ "FROM TRN_PENSION_RQST_HDR PH          WHERE PH.PPO_NO = :PpoNo AND PH.APPROVE_STATUS = 'APPROVED')  ");
			}

			Query sqlQuery = ghibSession.createSQLQuery(lSBQry.toString());
			// Query hqlQuery = ghibSession.createSQLQuery(lSBry.toString());
			sqlQuery.setDate("fromDate", argFromDate);
			sqlQuery.setDate("toDate", argToDate);
			sqlQuery.setString("aprvStat", "S");
			sqlQuery.setInteger("langId", 1);
			sqlQuery.setInteger("actvFlag", 1);

			if (!"".equals(argPPONO)) {
				sqlQuery.setString("PpoNo", argPPONO);
			}
			lLstCorRepData = sqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}

		return (lLstCorRepData);

	}

	public List getPensionCutDtlsByPnsnrCode(String lStrPnsnrCode, int FromMonth, int ToMonth) throws Exception {

		List lLstResData = null;
		StringBuffer lSBQry = new StringBuffer();
		try {
			lSBQry.append(" SELECT * FROM TRN_PENSION_IT_CUT_DTLS X WHERE X.PENSIONER_CODE =:PnsnrCode AND "
					+ " ( X.TYPE_FLAG ='PP' OR (X.TYPE_FLAG = 'PT' AND X.FROM_MONTH >=:fromMonth  AND X.TO_MONTH <=:toMonth) ) " + " ORDER BY X.FROM_MONTH");

			Query sqlQuery = ghibSession.createSQLQuery(lSBQry.toString());
			sqlQuery.setString("PnsnrCode", lStrPnsnrCode);
			sqlQuery.setInteger("fromMonth", FromMonth);
			sqlQuery.setInteger("toMonth", ToMonth);
			lLstResData = sqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}
		return lLstResData;
	}

	public List getPensnrCodeForCorrectionReportData(Date argFromDate, Date argToDate, String lStrPPONo, String argBranchCode, String lStrAuditorPodtId, String lStrLocCode) throws Exception {

		List lLstPensnrCode = null;
		StringBuffer lSBPreQry = new StringBuffer();
		try {
			lSBPreQry.append(" SELECT DISTINCT (TCD.PENSIONER_CODE) " + // ,TCD.CREATED_POST_ID
					// PPSTID
					" FROM TRN_PENSION_CORRECTION_DTLS TCD ");

			if (lStrAuditorPodtId != null && !lStrAuditorPodtId.equals("-1") && lStrAuditorPodtId.length() > 0) {
				lSBPreQry.append("JOIN ORG_USERPOST_RLT OUP ON TCD.CREATED_USER_ID = OUP.USER_ID AND OUP.activate_flag=1 ");
			}

			if (!lStrPPONo.equals("")) {
				lSBPreQry.append(" JOIN trn_pension_rqst_hdr trh ON trh.pensioner_code = tcd.pensioner_code AND trh.ppo_no = :PpoNo "
						+ " AND trh.Case_Status = 'APPROVED' and TRH.LOCATION_CODE=:locCode ");
			}

			if (!argBranchCode.equals("-1") && argBranchCode.length() > 0) {
				lSBPreQry.append(" JOIN MST_PENSIONER_DTLS MPD ON MPD.PENSIONER_CODE = tcd.PENSIONER_CODE AND MPD.BRANCH_CODE = "
						+ " :branchCode AND MPD.CASE_STATUS = 'APPROVED' AND MPD.active_flag = 'Y' ");
				if (lStrPPONo != "") {
					lSBPreQry.append(" AND MPD.PENSIONER_CODE = TRH.PENSIONER_CODE ");
				}
			}

			lSBPreQry.append(" WHERE tcd.CREATED_DATE >= :fromDate AND tcd.CREATED_DATE <= :toDate " + " AND (tcd.APPROVAL_STATUS IS NULL OR tcd.APPROVAL_STATUS = 'A' OR tcd.APPROVAL_STATUS = 'R') ");

			if (lStrAuditorPodtId != null && !lStrAuditorPodtId.equals("-1") && lStrAuditorPodtId.length() > 0) {
				lSBPreQry.append(" AND OUP.USER_ID = :lAudPostID ");
			}
			// lSBPreQry.append(" ORDER BY TCD.CREATED_POST_ID ");

			Query sqlPreQuery = ghibSession.createSQLQuery(lSBPreQry.toString());

			sqlPreQuery.setDate("fromDate", argFromDate);

			Calendar cal = Calendar.getInstance();
			cal.setTime(argToDate);
			cal.add(Calendar.DATE, 1);
			sqlPreQuery.setDate("toDate", cal.getTime());

			if (lStrPPONo != "") {
				sqlPreQuery.setString("PpoNo", lStrPPONo);
			}
			if (!argBranchCode.equals("-1") && argBranchCode.length() > 0) {
				sqlPreQuery.setString("branchCode", argBranchCode);
			}

			if (lStrAuditorPodtId != null && !lStrAuditorPodtId.equals("-1") && lStrAuditorPodtId.length() > 0) {
				sqlPreQuery.setString("lAudPostID", lStrAuditorPodtId);
			}
			if (!lStrPPONo.equals("")) {
				sqlPreQuery.setString("locCode", lStrLocCode);
			}

			lLstPensnrCode = sqlPreQuery.list();
		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}

		return (lLstPensnrCode);
	}

	public List getCorrectionReportData(String lStrPensionerCode, String argPPONO, Date argFromDate, Date argToDate, String argBranchCode, String argcmbBankCode, List argLstPostId, String lStrLocCode)
			throws Exception {

		List lLstCorRepData;

		StringBuffer lSBQry;
		StringBuffer lSBPreQry = new StringBuffer();

		try {

			lSBQry = new StringBuffer();
			lSBQry.append(" SELECT C1 col1,C2 col2,C3 col3,C4 col4,C5 col5,C6 col6,OEM2.EMP_FNAME || ' ' || OEM2.EMP_MNAME || ' ' || "
					+ " OEM2.EMP_LNAME APPRVNAME,C8 col8,C9 col9,C10 col10,C11 col11,C12 col12  FROM ( " + "SELECT PC.FIELD_TYPE C1, PC.APPROVAL_STATUS C2, "
					+ " OEM.EMP_FNAME ||' '|| OEM.EMP_MNAME ||' '|| OEM.EMP_LNAME C3," + " PC.CREATED_DATE C4, " + " PC.PRVS_FIELD_VALUE C5,"
					+ " PC.CRNT_FIELD_VALUE C6,PC.APPROVED_POST_ID C7,PC.APPROVED_DATE C8," + " TRH.PPO_NO C9,  mh.first_name C10,mh.middle_name C11,mh.last_name C12,PC.APPROVED_USER_ID C13 "
					+ " FROM TRN_PENSION_CORRECTION_DTLS PC ");
			if (lStrPensionerCode != "") {
				lSBQry.append(" JOIN TRN_PENSION_RQST_HDR TRH ON TRH.PENSIONER_CODE = PC.PENSIONER_CODE AND "
						+ " TRH.CASE_STATUS = 'APPROVED' AND TRH.PENSIONER_CODE = :pensionerCode and TRH.LOCATION_CODE=:locCode ");
			}

			if (!argBranchCode.equals("-1") && argBranchCode.length() > 0) {
				lSBQry.append(" JOIN MST_PENSIONER_DTLS MPD ON MPD.PENSIONER_CODE = PC.PENSIONER_CODE AND "
						+ " MPD.BRANCH_CODE = :branchCode AND MPD.CASE_STATUS = 'APPROVED' AND MPD.active_flag = 'Y' ");
				if (lStrPensionerCode != "") {
					lSBQry.append(" AND MPD.PENSIONER_CODE = TRH.PENSIONER_CODE ");
				}
			}
			if (!argcmbBankCode.equals("-1") && argBranchCode.equals("-1") && argBranchCode.length() > 0) {
				lSBQry.append(" JOIN MST_PENSIONER_DTLS MPD ON MPD.PENSIONER_CODE = PC.PENSIONER_CODE AND " + " MPD.BANK_CODE = :bankCode AND MPD.CASE_STATUS = 'APPROVED' AND MPD.active_flag = 'Y' ");
				if (lStrPensionerCode != "") {
					lSBQry.append(" AND MPD.PENSIONER_CODE = TRH.PENSIONER_CODE ");
				}
			}
			lSBQry.append(" LEFT OUTER JOIN ORG_EMP_MST OEM ON OEM.USER_ID = PC.CREATED_USER_ID AND  OEM.LANG_ID = :langId AND OEM.ACTIVATE_FLAG = :actvFlag ,mst_pensioner_hdr mh WHERE PC.CREATED_DATE >= :fromDate AND "
					+ " PC.CREATED_DATE <= :toDate  AND mh.pensioner_code = trh.pensioner_code AND trh.Case_Status = mh.case_status "
					+ " AND (PC.APPROVAL_STATUS IS NULL OR PC.APPROVAL_STATUS = 'A' OR  PC.APPROVAL_STATUS = 'R') ");
			if (argLstPostId.size() != 0) {
				lSBQry.append(" AND PC.APPROVED_USER_ID IN ( :lstPostId ) ");
			}
			lSBQry.append(" ) temp  LEFT OUTER JOIN ORG_EMP_MST OEM2 ON ");
			lSBQry.append(" C13 = OEM2.USER_ID ");
			lSBQry.append(" AND OEM2.LANG_ID = :langId AND OEM2.ACTIVATE_FLAG = :actvFlag ");

			SQLQuery sqlQuery = ghibSession.createSQLQuery(lSBQry.toString());

			sqlQuery.setDate("fromDate", argFromDate);

			Calendar cal = Calendar.getInstance();
			cal.setTime(argToDate);
			cal.add(Calendar.DATE, 1);
			sqlQuery.setDate("toDate", cal.getTime());
			sqlQuery.setInteger("langId", 1);
			sqlQuery.setInteger("actvFlag", 1);

			if (lStrPensionerCode != "") {
				sqlQuery.setString("pensionerCode", lStrPensionerCode);
			}
			if (!argBranchCode.equals("-1") && argBranchCode.length() > 0) {
				sqlQuery.setString("branchCode", argBranchCode);
			}
			if (!argcmbBankCode.equals("-1") && argcmbBankCode.length() > 0) {
				sqlQuery.setString("bankCode", argcmbBankCode);
			}

			if (argLstPostId.size() != 0) {
				sqlQuery.setParameterList("lstPostId", argLstPostId);
			}

			sqlQuery.setString("locCode", lStrLocCode);
			sqlQuery.addScalar("col1", Hibernate.STRING);
			sqlQuery.addScalar("col2", Hibernate.STRING);
			sqlQuery.addScalar("col3", Hibernate.STRING);
			sqlQuery.addScalar("col4", Hibernate.DATE);
			sqlQuery.addScalar("col5", Hibernate.STRING);
			sqlQuery.addScalar("col6", Hibernate.STRING);
			sqlQuery.addScalar("APPRVNAME", Hibernate.STRING);
			sqlQuery.addScalar("col8", Hibernate.DATE);
			sqlQuery.addScalar("col9", Hibernate.STRING);
			sqlQuery.addScalar("col10", Hibernate.STRING);
			sqlQuery.addScalar("col11", Hibernate.STRING);
			sqlQuery.addScalar("col12", Hibernate.STRING);
			lLstCorRepData = sqlQuery.list();

		} catch (Exception e) {
			logger.error("Error is" + e, e);
			throw e;
		}

		return (lLstCorRepData);

	}

	public List<MstPensionRqstReason> getMstRqstReasonVOList(final Long lLangId) throws Exception {

		List<MstPensionRqstReason> lObjMstRqstReason = new ArrayList<MstPensionRqstReason>();

		StringBuffer lSBQuery = new StringBuffer(100);

		String lStrLangFlag = "";

		Query lQuery = null;

		Session ghibSession = getSession();

		try {
			lSBQuery.append(" FROM MstPensionRqstReason WHERE langId = :langId ORDER BY rqstReasonId ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("langId", lLangId);

			lObjMstRqstReason = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lObjMstRqstReason;
	}

	public BigDecimal getNewBasicByOldBasicAmount(BigDecimal lBDOldAmount) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List lLstRes = new ArrayList();
		Query lQuery = null;
		BigDecimal lBDTempBasic = BigDecimal.ZERO;
		try {
			lSBQuery.append(" SELECT MROP.NEW_AMOUNT FROM MST_PENSION_ROP_2006 MROP WHERE ");
			lSBQuery.append(" MROP.OLD_AMOUNT =:lBDAmount1 ");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lQuery.setBigDecimal("lBDAmount1", lBDOldAmount);

			lLstRes = lQuery.list();

			if (!lLstRes.isEmpty()) {
				lBDTempBasic = new BigDecimal(lLstRes.get(0).toString());
			}

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lBDTempBasic;
	}

	public List<TrnPensionRqstTracking> getSavedPensionRequests(BigDecimal lintStatus) throws Exception {

		List<TrnPensionRqstTracking> lObjPensionRqstTracking = new ArrayList<TrnPensionRqstTracking>();
		StringBuffer lSBQuery = new StringBuffer(100);
		String lStrLangFlag = "";
		Query lQuery = null;

		Session ghibSession = getSession();

		try {
			lSBQuery.append(" FROM TrnPensionRqstTracking TPR WHERE TPR.status = :lIntStatus ORDER BY TPR.pensionRqstTrackingId ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setBigDecimal("lIntStatus", lintStatus);
			lObjPensionRqstTracking = lQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lObjPensionRqstTracking;
	}

	public List getPPONoandName(String lStrPensionerCode, String lStrArgsLocCode) throws Exception {

		String lStrPPONo = null;

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(100);

		Query lQuery = null;

		try {
			Session ghibSession = getSession();
			lSBQuery.append("SELECT rq.ppoNo,mh.pnsnrPrefix||' '||mh.firstName||' '||mh.middleName||' '||mh.lastName "
					+ " FROM TrnPensionRqstHdr rq,MstPensionerHdr mh WHERE mh.pensionerCode = rq.pensionerCode AND " + " mh.pensionerCode =:pensionerCode and rq.locationCode=:ArgsLocCode ");

			lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setString("pensionerCode", lStrPensionerCode);
			lQuery.setString("ArgsLocCode", lStrArgsLocCode);

			lLstResult = lQuery.list();

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public String getAuthorityNameFrmAuthorityCode(String lStrAuthorityCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer(100);
		String lStrAuthorityName = null;
		List<String> lLstResult = null;
		Query lQuery = null;

		try {
			Session ghibSession = getSession();
			lSBQuery.append(" SELECT MPA.authorityName FROM MstPensionAuthority MPA WHERE MPA.authorityCode= :AUTHORITY_CODE");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("AUTHORITY_CODE", lStrAuthorityCode);
			lLstResult = lQuery.list();
			if (!lLstResult.isEmpty()) {
				lStrAuthorityName = lLstResult.get(0);
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrAuthorityName;
	}

	public String getAuditorName(String lStrChangeItem, String lStrItemFlag) throws Exception {

		String lStrAuditorName = null;

		List<String> lLstResult = null;

		StringBuffer lSBQuery = new StringBuffer(100);

		Query lQuery = null;

		try {
			Session ghibSession = getSession();

			lSBQuery.append("SELECT oem.emp_fname||' '||oem.emp_mname||' '||oem.emp_lname FROM org_emp_mst oem, "
					+ " org_user_mst oum,org_userpost_rlt oup WHERE oem.activate_flag = 1 AND oum.activate_flag = 1 AND oup.activate_flag=1 AND oem.user_id = oum.user_id AND "
					+ " oup.user_id = oum.user_id AND oup.post_id IN (SELECT DISTINCT (CASE WHEN PRT.ITEM_FLAG = 'B' "
					+ " THEN (SELECT DISTINCT (RB.POST_ID) FROM RLT_AUDITOR_BANK RB WHERE RB.BRANCH_CODE = :BRANCH_CODE) "
					+ " ELSE (CASE WHEN MPD.BRANCH_CODE IS NOT NULL THEN (SELECT DISTINCT RAB.POST_ID FROM RLT_AUDITOR_BANK RAB, "
					+ " MST_PENSIONER_DTLS MPD WHERE RAB.BRANCH_CODE = MPD.BRANCH_CODE AND MPD.active_flag = 'Y' AND MPD.PENSIONER_CODE = :PENSIONER_CODE) "
					+ " ELSE (SELECT DISTINCT PRH.CASE_OWNER FROM TRN_PENSION_RQST_HDR PRH WHERE PRH.PENSIONER_CODE = :PENSIONER_CODE) END) "
					+ " END) AUDITOR_NAME FROM TRN_PENSION_RQST_TRACKING PRT, MST_PENSIONER_DTLS MPD WHERE PRT.CHANGE_ITEM = :CHANGE_ITEM)");

			lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			if ("B".equals(lStrItemFlag)) {
				lQuery.setString("PENSIONER_CODE", null);
				lQuery.setString("BRANCH_CODE", lStrChangeItem);
				lQuery.setString("CHANGE_ITEM", lStrChangeItem);

			}

			else {
				lQuery.setString("BRANCH_CODE", null);
				lQuery.setString("PENSIONER_CODE", lStrChangeItem);
				lQuery.setString("CHANGE_ITEM", lStrChangeItem);
			}
			lQuery.setString("PENSIONER_CODE", lStrChangeItem);
			lQuery.setString("BRANCH_CODE", lStrChangeItem);

			lLstResult = lQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				lStrAuditorName = lLstResult.get(0).toString();
			}

		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lStrAuditorName;
	}

	public List<TrnPensionRqstTracking> getSavedRequestsForUpdate(Long argPostId) throws Exception {

		List lLstRqst = new ArrayList<TrnPensionRqstTracking>();

		StringBuffer lSBQry = new StringBuffer();
		Query lQuery = null;
		List<BigDecimal> lLstStatus = new ArrayList<BigDecimal>();

		Session ghibSession = getSession();
		try {
			/*
			 * lSBQry.append(
			 * " SELECT TPRT.INWD_NO, TPRT.INWD_DATE, TPRT.RQST_REASON_DESC,  TPRT.AUTHORITY_NO, TPRT.AUTHORITY_DATE  FROM TRN_PENSION_RQST_TRACKING TPRT "
			 * + " WHERE TPRT.STATUS = :status ");
			 */
			lLstStatus.add(new BigDecimal(5));
			lLstStatus.add(new BigDecimal(10));

			lSBQry.append(" FROM TrnPensionRqstTracking   WHERE status IN (:status) AND auditorPostId = :postId ");

			lQuery = ghibSession.createQuery(lSBQry.toString());
			lQuery.setParameterList("status", lLstStatus);
			lQuery.setLong("postId", argPostId);

			lLstRqst = lQuery.list();
			logger.info(lLstRqst);
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return (lLstRqst);
	}

	public TrnPensionRqstTracking getSavedInwdRqstData(String lStrInwdNo) throws Exception {

		List lLstResult = null;
		StringBuffer lSBQuery = new StringBuffer(100);
		Query lQuery = null;
		TrnPensionRqstTracking lObjTrnPensionRqstTrackingVO = new TrnPensionRqstTracking();

		Session ghibSession = getSession();

		try {
			lSBQuery.append(" FROM TrnPensionRqstTracking trp WHERE trp.inwdNo=:InwrdNo");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("InwrdNo", lStrInwdNo);
			lLstResult = lQuery.list();

			if (!lLstResult.isEmpty()) {
				lObjTrnPensionRqstTrackingVO = (TrnPensionRqstTracking) (lLstResult.get(0));
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lObjTrnPensionRqstTrackingVO;
	}

	public List getPensionrequestData(BigDecimal lStrStatus, Long gLngPostId, String lStrLocCode) throws Exception {

		List lLstRqst = new ArrayList();
		StringBuffer lSBQry = new StringBuffer();
		Query lQuery = null;
		try {
			lSBQry.append("(SELECT TR.INWD_NO INWD_NO,       TR.INWD_DATE INWD_DATE,       " + "RQ.PPO_NO CHANGEITEM,       TR.ITEM_FLAG ITEMFLAG,       "
					+ "CONCAT(CONCAT(CONCAT(MH.FIRST_NAME,' '),CONCAT(COALESCE(MH.MIDDLE_NAME,'.'),' ')),COALESCE(MH.LAST_NAME,'.')) "
					+ "CHANGEITEMDESC,       MA.AUTHORITY_NAME AUTHNAME,       TR.RQST_REASON_DESC REASONDESC,       "
					+ "CONCAT(CONCAT(CONCAT(CAST (OEM.EMP_FNAME AS CHAR(200)),' '),CONCAT(COALESCE(CAST(OEM.EMP_MNAME AS CHAR(200)),'.'),' ')), "
					+ "COALESCE(CAST (OEM.EMP_LNAME AS CHAR(200)),'.')) AUDITORNAME,       TR.STATUS STATUS,       "
					+ "TR.PENSION_RQST_TRACKING_ID PK,       OUP.POST_ID    AUDITORPOSTID FROM TRN_PENSION_RQST_HDR  RQ,     "
					+ "MST_PENSIONER_HDR     MH,     MST_PENSION_AUTHORITY MA,     TRN_PENSION_RQST_TRACKING TR,     "
					+ "ORG_EMP_MST           OEM,     ORG_USER_MST          OUM,     ORG_USERPOST_RLT      "
					+ "OUP WHERE OUP.activate_flag = 1 AND OEM.activate_flag = 1 AND OUM.activate_flag = 1 AND RQ.PENSIONER_CODE = TR.CHANGE_ITEM  AND TR.ITEM_FLAG = 'P'      "
					+ "AND MH.PENSIONER_CODE = RQ.PENSIONER_CODE      AND OEM.USER_ID = OUM.USER_ID      " + "AND OUP.USER_ID = OUM.USER_ID      AND OUP.POST_ID = TR.AUDITOR_POST_ID      "
					+ "AND MH.CASE_STATUS = RQ.CASE_STATUS      AND RQ.CASE_STATUS = 'APPROVED'      "
					+ "AND TR.SANC_AUTHORITY_CODE = MA.AUTHORITY_CODE      AND RQ.LOCATION_CODE = MH.LOCATION_CODE      "
					+ "AND MH.LOCATION_CODE = :lLocCode      AND MA.LANG_ID = 1      AND TR.STATUS = :lStrStatus ");

			if (lStrStatus.equals(5)) {
				lSBQry.append(" AND TR.AUDITOR_POST_ID = :postId ");
			}
			lSBQry.append(") UNION( SELECT TR.INWD_NO INWD_NO,       TR.INWD_DATE INWD_DATE,       " + "CAST (RBB.BRANCH_CODE AS CHAR(50)) CHANGEITEM,       TR.ITEM_FLAG ITEMFLAG,       "
					+ "CONCAT(CONCAT(CAST (MB.BANK_NAME AS CHAR(200)),' '),RBB.BRANCH_NAME) CHANGEITEMDESC,       " + "MA.AUTHORITY_NAME AUTHNAME,       TR.RQST_REASON_DESC REASONDESC,       "
					+ "CONCAT(CONCAT(CONCAT(CAST (OEM.EMP_FNAME AS CHAR(200)),' '), " + "CONCAT(COALESCE(CAST(OEM.EMP_MNAME AS CHAR(200)),'.'),' ')), "
					+ "COALESCE(CAST (OEM.EMP_LNAME AS CHAR(200)),'.')) AUDITORNAME,       " + "TR.STATUS STATUS,       TR.PENSION_RQST_TRACKING_ID PK,       OUP.POST_ID    "
					+ "AUDITORPOSTID FROM TRN_PENSION_RQST_HDR  RQ,     MST_PENSIONER_DTLS    MD,    " + "MST_PENSION_AUTHORITY MA,     TRN_PENSION_RQST_TRACKING TR,     ORG_EMP_MST           "
					+ "OEM,     ORG_USER_MST          OUM,     ORG_USERPOST_RLT      OUP,     MST_BANK             "
					+ " MB,     RLT_BANK_BRANCH       RBB WHERE OUP.activate_flag = 1 AND OEM.activate_flag = 1 AND OUM.activate_flag = 1 AND MD.PENSIONER_CODE = RQ.PENSIONER_CODE      "
					+ "AND MD.BRANCH_CODE = TR.CHANGE_ITEM      AND TR.ITEM_FLAG = 'B'      AND OEM.USER_ID = OUM.USER_ID      "
					+ "AND OUP.USER_ID = OUM.USER_ID      AND OUP.POST_ID = TR.AUDITOR_POST_ID      AND MD.CASE_STATUS = RQ.CASE_STATUS      "
					+ "AND RQ.CASE_STATUS = 'APPROVED'      AND TR.SANC_AUTHORITY_CODE = MA.AUTHORITY_CODE     " + " AND MB.BANK_CODE = RBB.BANK_CODE      AND RBB.BRANCH_CODE = MD.BRANCH_CODE      "
					+ "AND RQ.LOCATION_CODE = MD.LOCATION_CODE AND MD.active_flag = 'Y' AND RBB.LOCATION_CODE = RQ.LOCATION_CODE      "
					+ "AND MD.LOCATION_CODE = :lLocCode      AND MA.LANG_ID = RBB.LANG_ID      AND RBB.LANG_ID = MB.LANG_ID     " + " AND RBB.LANG_ID = 1      AND TR.STATUS = :lStrStatus ");

			if (lStrStatus.equals(5)) {
				lSBQry.append(" AND TR.AUDITOR_POST_ID = :postId ");
			}
			lSBQry.append("GROUP BY TR.INWD_NO,TR.INWD_DATE,RBB.BRANCH_CODE,TR.ITEM_FLAG,MB.BANK_NAME,RBB.BRANCH_NAME,         "
					+ "MA.AUTHORITY_NAME,TR.RQST_REASON_DESC,OEM.EMP_FNAME,OEM.EMP_MNAME,OEM.EMP_LNAME,         " + "TR.STATUS,TR.PENSION_RQST_TRACKING_ID,OUP.POST_ID)");

			lQuery = ghibSession.createSQLQuery(lSBQry.toString());
			lQuery.setBigDecimal("lStrStatus", lStrStatus);
			if (lStrStatus.equals(5)) {
				lQuery.setLong("postId", gLngPostId);
			}

			lQuery.setString("lLocCode", lStrLocCode);
			lLstRqst = lQuery.list();
			logger.info(lLstRqst);
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return (lLstRqst);
	}

	public List getPostIDForCorrectionReportData(Long argHeirRefId, String argCorrectedBy) throws Exception {

		List lLstPostId = new ArrayList();
		StringBuffer lSBQry = new StringBuffer();

		lSBQry.append("	SELECT OUR.POST_ID  FROM WF_HIERACHY_POST_MPG WHPM,ORG_USERPOST_RLT OUR  WHERE " + " WHPM.HIERACHY_REF_ID = :heirRefId  AND WHPM.LEVEL_ID  = :correctedBy  AND WHPM.POST_ID = "
				+ " OUR.POST_ID AND OUR.ACTIVATE_FLAG = WHPM.ACTIVATE_FLAG AND OUR.ACTIVATE_FLAG = :actvFlag");

		Query sqlQuery = ghibSession.createSQLQuery(lSBQry.toString());

		sqlQuery.setInteger("correctedBy", Integer.parseInt(argCorrectedBy));
		sqlQuery.setLong("heirRefId", argHeirRefId);
		sqlQuery.setInteger("actvFlag", 1);

		lLstPostId = sqlQuery.list();

		return lLstPostId;
	}

	public List<TrnPensionCorrectionDtls> getCorrectedValuesByppoNo(String lStrPpoNo, String lArgsLocCode) throws Exception {

		StringBuffer lSBQry = new StringBuffer();
		List lLstRes = new ArrayList();
		List<TrnPensionCorrectionDtls> lLstTrnPensionCorrectionDtls = new ArrayList<TrnPensionCorrectionDtls>();
		try {
			lSBQry.append("select tcd.field_type,tcd.crnt_field_value,tcd.prvs_field_value  "
					+ " from trn_pension_correction_dtls tcd,trn_pension_rqst_hdr trh where tcd.pensioner_code = trh.pensioner_code and "
					+ " trh.case_status = 'NEW' and tcd.approval_status is null and trh.ppo_no =:ppoNo and trh.location_code=:lArgsLocCode ");

			Query sqlQuery = ghibSession.createSQLQuery(lSBQry.toString());
			sqlQuery.setString("ppoNo", lStrPpoNo);
			sqlQuery.setString("lArgsLocCode", lArgsLocCode);

			lLstRes = sqlQuery.list();
			TrnPensionCorrectionDtls lObjTempVo = null;
			Object[] lObjTuple = null;
			if (!lLstRes.isEmpty()) {
				for (int i = 0; i < lLstRes.size(); i++) {
					lObjTuple = (Object[]) lLstRes.get(i);
					lObjTempVo = new TrnPensionCorrectionDtls();
					if (lObjTuple[0] != null) {
						lObjTempVo.setFieldType(lObjTuple[0].toString());
					}
					if (lObjTuple[1] != null) {
						lObjTempVo.setCrntFieldValue(lObjTuple[1].toString());
					}
					if (lObjTuple[2] != null) {
						lObjTempVo.setPrvsFieldValue(lObjTuple[2].toString());
					}
					lLstTrnPensionCorrectionDtls.add(lObjTempVo);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}
		return lLstTrnPensionCorrectionDtls;
	}

	public List getRevisionBillDtlsForRectify(String lStrLocCode, String lStrPPONO) throws Exception {

		StringBuffer lSBQry = new StringBuffer();

		List lResultLst = null;

		try {
			lSBQry.append(" SELECT PC.RLT_PENSIONCASE_BILL_ID,PC.PPO_NO,PC.BILL_TYPE, PC.PAY_MODE, PC.PAY_AMOUNT " + " FROM RLT_PENSIONCASE_BILL PC , TRN_PENSION_RQST_HDR RH "
					+ " WHERE RH.CASE_STATUS = :CaseStatus AND RH.PPO_NO = PC.PPO_NO " + " AND RH.LOCATION_CODE = :LocCode AND RH.LOCATION_CODE = PC.LOCATION_CODE "
					+ " AND PC.BILL_NO IS NULL AND PC.BILL_TYPE IN ('CVP','DCRG') AND PC.STATUS = :BillStatus " + " AND PC.PPO_NO = :PPONO");

			Query sqlQuery = ghibSession.createSQLQuery(lSBQry.toString());

			sqlQuery.setString("PPONO", lStrPPONO);
			sqlQuery.setString("LocCode", lStrLocCode);
			sqlQuery.setString("CaseStatus", "APPROVED");
			sqlQuery.setString("BillStatus", "Y");

			lResultLst = sqlQuery.list();
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		return lResultLst;
	}

	public void updateRectifyBillDtls(String lStrBillId, String lStrPPONo, String lStrPayMode, String lStrNewPayAmt, String lStrLocCode, String lStrBillType, Long gLngPostId, Long gLngUserId,
			Date gDate) throws Exception {

		Object[] lObjArr = null;
		StringBuffer updateSql = new StringBuffer();
		List lPrvBillDateLst = null;
		Session ghibSession = getSession();

		try {
			Query lQuery = null;

			updateSql.append(" UPDATE RLT_PENSIONCASE_BILL SET PAY_MODE = :lPayMode,updated_user_id = :updated_user_id,updated_post_id = :updated_post_id,updated_date = :updated_date ");

			if (lStrNewPayAmt != null && Double.valueOf(lStrNewPayAmt) > 0) {
				updateSql.append(", PAY_AMOUNT = :lNewPayAmt");
			}
			updateSql.append(" WHERE LOCATION_CODE = :LocCode AND PPO_NO = :lStrPPONo AND BILL_TYPE = :lBillType AND RLT_PENSIONCASE_BILL_ID = :lBillId ");

			lQuery = ghibSession.createSQLQuery(updateSql.toString());

			lQuery.setParameter("lBillType", lStrBillType);
			lQuery.setParameter("lStrPPONo", lStrPPONo);
			lQuery.setParameter("LocCode", lStrLocCode);
			lQuery.setParameter("lBillId", lStrBillId);
			lQuery.setParameter("lPayMode", lStrPayMode);
			lQuery.setLong("updated_user_id", gLngUserId);
			lQuery.setLong("updated_post_id", gLngPostId);
			lQuery.setDate("updated_date", gDate);

			if (lStrNewPayAmt != null && Double.valueOf(lStrNewPayAmt) > 0) {
				lQuery.setParameter("lNewPayAmt", lStrNewPayAmt);
			}

			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error in  : " + e, e);
			throw e;
		}
	}

	public void deleteRectifyBillDtls(String lStrBillId, String lStrPPONo, String lStrLocCode, String lStrBillType) throws Exception {

		Object[] lObjArr = null;
		String updateSql = null;
		List lPrvBillDateLst = null;

		try {
			Session ghibSession = getSession();
			Query lQuery = null;

			updateSql = " DELETE FROM RLT_PENSIONCASE_BILL WHERE LOCATION_CODE = :LocCode AND PPO_NO = :lStrPPONo " + " AND BILL_TYPE = :lBillType AND RLT_PENSIONCASE_BILL_ID = :lBillId ";

			lQuery = ghibSession.createSQLQuery(updateSql.toString());

			lQuery.setParameter("lBillType", lStrBillType);
			lQuery.setParameter("lStrPPONo", lStrPPONo);
			lQuery.setParameter("LocCode", lStrLocCode);
			lQuery.setParameter("lBillId", lStrBillId);

			lQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error in  : " + e, e);
			throw e;
		}
	}

	public void updatePensionRemarks(String lStrRemarks) throws Exception {

		Session ghibSession = getSession();
		Query lQuery = null;
		try {
			lQuery = ghibSession.createSQLQuery(lStrRemarks);
			lQuery.executeUpdate();
		} catch (Exception e) {
			throw e;
		}
	}

	public Map<String, List<CmnLookupMst>> getPartialLookupVo(List lstLookUpNames, Long lLangId) throws Exception {

		Query lQuery = null;
		List lLstLookupId = new ArrayList();
		Map lMaplookupIdAndName = new HashMap();
		Map<String, List<CmnLookupMst>> FinalMap = new HashMap();
		try {
			StringBuffer lSBParentLookupDtls = new StringBuffer();
			lSBParentLookupDtls.append(" SELECT lookupName,lookupId from CmnLookupMst where lookupName in (:lookupNameList) " + " and cmnLanguageMst.langId=:langId order by lookupId ");
			lQuery = ghibSession.createQuery(lSBParentLookupDtls.toString());
			lQuery.setCacheable(true).setCacheRegion("pensionCache");
			lQuery.setParameterList("lookupNameList", lstLookUpNames);
			lQuery.setParameter("langId", lLangId);
			List lLstResTemp = lQuery.list();
			Object[] tuple = null;
			if (!lLstResTemp.isEmpty()) {
				for (int i = 0; i < lLstResTemp.size(); i++) {
					tuple = (Object[]) lLstResTemp.get(i);
					lLstLookupId.add(new Long(tuple[1].toString()));
					lMaplookupIdAndName.put(tuple[1].toString(), tuple[0].toString());
				}
			}
			StringBuffer lSBQuery = new StringBuffer();
			lSBQuery.append("SELECT lookupName,lookupDesc,lookupShortName,parentLookupId from CmnLookupMst where " + " parentLookupId in(:lLstLookupId)" + " order by parentLookupId,orderNo ");
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setCacheable(true).setCacheRegion("pensionCache");
			lQuery.setParameterList("lLstLookupId", lLstLookupId);
			lLstResTemp = lQuery.list();
			CmnLookupMst lObjCmnLookupMst = null;
			Long lLngTempLookupId = 0L;
			List<CmnLookupMst> lObjTempVo = null;
			if (!lLstResTemp.isEmpty()) {
				for (int i = 0; i < lLstResTemp.size(); i++) {
					tuple = (Object[]) lLstResTemp.get(i);
					lObjCmnLookupMst = new CmnLookupMst();
					lObjCmnLookupMst.setLookupName(tuple[0].toString());
					lObjCmnLookupMst.setLookupDesc(tuple[1].toString());
					lObjCmnLookupMst.setLookupShortName(tuple[2].toString());
					if (!lLngTempLookupId.equals(new Long(tuple[3].toString()))) {
						if (!lLngTempLookupId.equals(0L)) {
							FinalMap.put((String) lMaplookupIdAndName.get(lLngTempLookupId.toString()), lObjTempVo);
						}
						lLngTempLookupId = new Long(tuple[3].toString());
						lObjTempVo = new ArrayList<CmnLookupMst>();
					}
					lObjTempVo.add(lObjCmnLookupMst);
				}
				if (!lLngTempLookupId.equals(0L)) {
					FinalMap.put((String) lMaplookupIdAndName.get(lLngTempLookupId.toString()), lObjTempVo);
				}
			}
		} catch (Exception e) {
			logger.error("Error in  : " + e, e);
			throw e;
		}
		// TODO Auto-generated method stub
		return FinalMap;
	}

	public Map getfulllLookupVo(List lstLookUpNames, Long LLangId) throws Exception {

		try {

		} catch (Exception e) {

		}
		// TODO Auto-generated method stub
		return null;
	}

	public List getBranchsOfAuditorBank(String lStrBankCode, String lStrAuditerPostId, Long langId, String locCode) throws Exception {

		ArrayList<ComboValuesVO> arrBranchVO = new ArrayList<ComboValuesVO>();
		String strQuery = null;
		List resultList;
		ComboValuesVO cmbVO;
		Object[] obj;
		try {
			strQuery = "select bb.branch_name,bb.branch_code\n" + "from rlt_bank_branch bb,rlt_auditor_bank ab\n" + "where bb.bank_code=ab.bank_code\n" + "and bb.branch_code=ab.branch_code\n"
					+ " AND ab.bill_type IS NULL \n" + "and bb.location_code=:locCode\n" + "and ab.post_id=:postId\n" + "and ab.bank_code=:bankCode\n" + "order by bb.branch_name";

			Query sqlQuery = ghibSession.createSQLQuery(strQuery);
			sqlQuery.setLong("bankCode", Long.parseLong(lStrBankCode));
			sqlQuery.setLong("postId", Long.parseLong(lStrAuditerPostId));
			sqlQuery.setString("locCode", locCode);
			resultList = sqlQuery.list();
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("--Select--");
			arrBranchVO.add(cmbVO);
			if (!resultList.isEmpty()) {
				Iterator itr = resultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[1].toString());
					cmbVO.setDesc(obj[0].toString() + " " + obj[1].toString());
					arrBranchVO.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return arrBranchVO;
	}

	public List getMonthDtls(Long lLngLangId) throws Exception {

		logger.info("Start");

		List<CommonVO> lReturnList = null;
		SgvaMonthMst lMonthMstObj = null;
		CommonVO lComVo = null;
		List<SgvaMonthMst> lList = null;
		String lStrLangId = lLngLangId == 1 ? "en_US" : "gu";
		try {
			ghibSession = this.getSession();
			Criteria lObjCriteria = ghibSession.createCriteria(SgvaMonthMst.class);
			lObjCriteria.add(Restrictions.eq("langId", lStrLangId));
			lObjCriteria.addOrder(Order.asc("monthNo"));

			lObjCriteria.setCacheable(true).setCacheRegion("ecache_lookup");
			lList = lObjCriteria.list();

			if (lList != null && !lList.isEmpty()) {
				lReturnList = new ArrayList<CommonVO>();

				Iterator lItr = lList.iterator();

				while (lItr.hasNext()) {
					lMonthMstObj = (SgvaMonthMst) lItr.next();
					lComVo = new CommonVO();
					lComVo.setCommonId(lMonthMstObj.getMonthCode());
					lComVo.setCommonDesc(lMonthMstObj.getMonthName());
					lReturnList.add(lComVo);
				}
			}
		} catch (Exception e) {
			logger.error("Error is : " + e, e);
			throw (e);
		}

		logger.info("End");
		return lReturnList;
	}

	public String getAtoNameFromLocationCode(String lStrLocCode) throws Exception {

		String lStrAtoName = null;
		List lLstResult = null;
		try {
			StringBuffer lSBQuery = new StringBuffer();
			
			lSBQuery.append(" SELECT concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname)");
			lSBQuery.append(" FROM OrgPostMst opm, AclPostroleRlt apr, OrgUserpostRlt our, OrgEmpMst oem  ");
			lSBQuery.append(" WHERE opm.postId = apr.orgPostMst.postId");
			lSBQuery.append(" AND opm.postId =  our.orgPostMstByPostId.postId ");
			lSBQuery.append(" AND our.orgUserMst.userId =  oem.orgUserMst.userId ");
			lSBQuery.append(" AND apr.aclRoleMst.roleId  =  :roleId AND apr.cmnLookupMstByActivate.lookupId = 1 ");
			lSBQuery.append(" AND opm.locationCode  =  :locationCode ");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("roleId", gObjRsrcBndle.getString("PPMT.ATOROLE"));
			hqlQuery.setString("locationCode", lStrLocCode);

			lLstResult = hqlQuery.list();
			if (lLstResult != null && !lLstResult.isEmpty()) {
				lStrAtoName = lLstResult.get(0).toString();
			}

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrAtoName;
	}

	public Map<BigDecimal, String> getAllHeadCodeSeriesMap() throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		Map<BigDecimal, String> lMapHeadCode = new HashMap<BigDecimal, String>();
		try {
			lSBQuery.append("Select \n");
			lSBQuery.append("series,headCode \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstPensionHeadcode  \n");

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			lLstResult = hqlQuery.list();

			for (Object[] lArrObj : lLstResult) {
				lMapHeadCode.put((BigDecimal) lArrObj[1], lArrObj[0].toString());
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lMapHeadCode;
	}

	public String getRoleByElement(String lStrElementCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List<Long> lLstResult = new ArrayList<Long>();
		String lStrRoleId = "";
		try {
			if (lStrElementCode.trim().length() > 0) {
				lSBQuery.append("Select \n");
				lSBQuery.append("aclRoleMst.roleId \n");
				lSBQuery.append("from \n");
				lSBQuery.append("AclRoleElementRlt \n");
				lSBQuery.append("where \n");
				lSBQuery.append("elementCode = :lIntElementCode \n");

				Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
				hqlQuery.setInteger("lIntElementCode", Integer.valueOf(lStrElementCode));
				lLstResult = hqlQuery.list();
				if (lLstResult != null && lLstResult.size() > 0) {
					lStrRoleId = lLstResult.get(0).toString();
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lStrRoleId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.CommonPensionDAO#getPaymentSchemeCodeMap(java
	 * .util.List)
	 */
	public Map<String, String> getPaymentSchemeCodeMap(List<String> lLstSchemeCode) throws Exception {

		StringBuffer lSBQuery = new StringBuffer();
		List<Object[]> lLstResult = new ArrayList<Object[]>();
		Map<String, String> lMapSchemeCode = new HashMap<String, String>();
		try {
			lSBQuery.append("Select \n");
			lSBQuery.append("schemeCode,schemeName \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstScheme  \n");

			if (lLstSchemeCode != null && !lLstSchemeCode.isEmpty()) {
				lSBQuery.append("where schemeCode in (:schemeCodeList)");
			}

			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			if (lLstSchemeCode != null && !lLstSchemeCode.isEmpty()) {
				hqlQuery.setParameterList("schemeCodeList", lLstSchemeCode);
			}
			lLstResult = hqlQuery.list();

			for (Object[] lArrObj : lLstResult) {
				lMapSchemeCode.put(lArrObj[0].toString(), lArrObj[1].toString());
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lMapSchemeCode;
	}

	public Map<Long, RltPensionHeadcodeChargable> getRltPensionHeadcodeChargableDtls(String lStrBillType) throws Exception {

		Map<Long, RltPensionHeadcodeChargable> lMapHeadCodeChargable = new HashMap<Long, RltPensionHeadcodeChargable>();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer(150);

			lSBQuery.append(" FROM RltPensionHeadcodeChargable H WHERE H.billType = :lBillType  AND H.activeFlag =:activeFlag");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lBillType", lStrBillType);
			lQuery.setString("activeFlag", "Y");
			List<RltPensionHeadcodeChargable> lLstVO = lQuery.list();

			if (lLstVO != null) {
				for (RltPensionHeadcodeChargable lObjRltPensionHeadcodeChargable : lLstVO) {
					lMapHeadCodeChargable.put(lObjRltPensionHeadcodeChargable.getheadCode(), lObjRltPensionHeadcodeChargable);
				}
			}
		} catch (Exception e) {
			logger.error("Error in getMstPensionHeadcodeChargableDtls BudgetDtlsDAOImpl Error is : " + e, e);
			throw (e);
		}

		return lMapHeadCodeChargable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.CommonPensionDAO#getGeneratedIdForLibraryNo
	 * (java.lang.String, java.lang.String)
	 */
	public String getGeneratedIdForLibraryNo(String lStrLibraryNo, String lStrLocationCode) throws Exception {

		// TODO Auto-generated method stub
		String lStrGeneratedId = "";
		List lLstResult = new ArrayList();
		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select generatedId FROM LibraryNoSeqMst WHERE libraryNo = :libraryNo AND locationCode = :locationCode");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setString("libraryNo", lStrLibraryNo);
			hqlQuery.setString("locationCode", lStrLocationCode);
			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				if (lLstResult.get(0) != null) {
					lStrGeneratedId = lLstResult.get(0).toString();
				}
			}

		} catch (Exception e) {
			logger.error("Error in getGeneratedIdForLibraryNo is : " + e, e);
			throw (e);
		}

		return lStrGeneratedId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tcs.sgv.pensionpay.dao.CommonPensionDAO#updateGeneratedIdForLibraryNo
	 * (java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public void updateGeneratedIdForLibraryNo(String lStrLibraryNo, String lStrLocationCode, Integer lIntGeneratedId) throws Exception {

		// TODO Auto-generated method stub

		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer(150);

			lSBQuery.append("update LibraryNoSeqMst set generatedId = :generatedId WHERE libraryNo = :libraryNo AND locationCode =:locationCode");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			hqlQuery.setInteger("generatedId", lIntGeneratedId);
			hqlQuery.setString("libraryNo", lStrLibraryNo);
			hqlQuery.setString("locationCode", lStrLocationCode);
			hqlQuery.executeUpdate();

		} catch (Exception e) {
			logger.error("Error in updateGeneratedIdForLibraryNo is : " + e, e);
			throw (e);
		}

	}

	public Long getMaxLetterNo(Integer lIntLocId, Integer lIntFinYearId, String lLetterType) throws Exception {

		Long lLngLetterNo = null;
		List lLstRes = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();

			ghibSession = getSession();

			lSBQuery.append("SELECT MAX(letterNo) FROM TrnFinYearLetterNo ");
			lSBQuery.append("WHERE locationCode = :locationCode \n");
			lSBQuery.append("and letterType = :lLetterType \n");
			lSBQuery.append("and finYearId = :finYearId \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("locationCode", lIntLocId);
			lQuery.setInteger("finYearId", lIntFinYearId);
			lQuery.setParameter("lLetterType", lLetterType);
			lLstRes = lQuery.list();
			if (lLstRes.size() > 0) {
				lLngLetterNo = (Long) lLstRes.get(0);
			}
			if (lLngLetterNo != null) {
				lLngLetterNo++;
			} else {
				lLngLetterNo = 1l;
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLngLetterNo;
	}

	public List<Object[]> getAGCircleDtlFromLocationCode(String lStrLocCode) throws Exception {

		List<Object[]> lLstResult = null;
		StringBuilder lSBQuery = new StringBuilder();
		try {
			lSBQuery.append("Select clm2.locationCode,clm2.locName \n");
			lSBQuery.append("From \n");
			lSBQuery.append("CmnLocationMst clm1,CmnLocationMst clm2 \n");
			lSBQuery.append("Where \n");
			lSBQuery.append("clm1.officeCode = clm2.locationCode \n");
			lSBQuery.append("and clm1.locationCode = :lStrLocCode \n");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setString("lStrLocCode", lStrLocCode);
			lLstResult = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstResult;
	}

	public List<ComboValuesVO> getDaRateFromPayCommissionStateDept(String lStrPayCommissionType, BigDecimal lBDDaForState) throws Exception {

		ArrayList<ComboValuesVO> lArrListDARate = new ArrayList<ComboValuesVO>();
		List<BigDecimal> lLstDARate = new ArrayList<BigDecimal>();
		StringBuilder lSBQuery = new StringBuilder();
		ComboValuesVO cmbVO = null;
		BigDecimal lBdDARate = null;
		// lLstDARate.add(BigDecimal.ZERO);

		try {
			Session ghibSession = getSession();
			lSBQuery.append("SELECT rate FROM RltPensionHeadcodeRate");
			lSBQuery.append(" WHERE fieldType = :PayCommissionType and headCode = :lDaForState Order by 1");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("PayCommissionType", lStrPayCommissionType);
			lQuery.setBigDecimal("lDaForState", lBDDaForState);
			lLstDARate.addAll(lQuery.list());
			cmbVO = new ComboValuesVO();
			cmbVO.setId("-1");
			cmbVO.setDesc("----Select----");
			lArrListDARate.add(cmbVO);
			if (!lLstDARate.isEmpty()) {
				for (Integer lInt = 0; lInt < lLstDARate.size(); lInt++) {
					cmbVO = new ComboValuesVO();
					lBdDARate = lLstDARate.get(lInt);
					cmbVO.setId(lLstDARate.get(lInt).toString());
					cmbVO.setDesc(lLstDARate.get(lInt).toString());
					lArrListDARate.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Exception is : " + e, e);
			throw (e);
		}
		return lArrListDARate;

	}
	public String getRoleByPost(Long lLngPostId) throws Exception {

		List lLstResultList = null;
		Query hqlQuery = null;
		String lStrToRole = "";
		StringBuilder lStrQuery = new StringBuilder();
		Session session = getSession();
		try {
			lStrQuery.append(" SELECT acl.aclRoleMst.roleId from AclPostroleRlt acl where acl.orgPostMst.postId=:lLngPostId ");

			hqlQuery = session.createQuery(lStrQuery.toString());
			hqlQuery.setParameter("lLngPostId", lLngPostId);
			lLstResultList = hqlQuery.list();

			if (lLstResultList.size() > 0) {
				for (int i = 0; i < lLstResultList.size(); i++) {
					if (lStrToRole == null || lStrToRole.equals("")) {
						lStrToRole = (lLstResultList.get(i).toString());
					} else {
						lStrToRole = lStrToRole + "," + (lLstResultList.get(i).toString());
					}
				}
			} else {
				lStrToRole = "";
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
			throw e;
		}
		logger.info("getRoleByPost in Role Ids : " + lStrToRole);
		logger.info("getRoleByPost end...................");

		return lStrToRole;
	}

	
	public List<String> getLocationListOfAgCircle(String lStrLocCode)
			throws Exception {
		// TODO Auto-generated method stub
		String lStrGeneratedId = "";
		List lLstResult = new ArrayList();
		List<String> lLstLocCode = new ArrayList<String>();
 		try {
			Session ghibSession = getSession();
			StringBuffer lSBQuery = new StringBuffer();

			lSBQuery.append("select locationCode from CmnLocationMst \n");
			lSBQuery.append(" where officeCode = (select officeCode from CmnLocationMst where locationCode = :locationCode)");
			Query hqlQuery = ghibSession.createQuery(lSBQuery.toString());
			
			hqlQuery.setString("locationCode", lStrLocCode);
			lLstResult = hqlQuery.list();

			if (lLstResult != null && !lLstResult.isEmpty()) {
				for(int lIntCnt = 0;lIntCnt < lLstResult.size();lIntCnt++)
				{
					lLstLocCode.add(lLstResult.get(lIntCnt).toString());
				}
			}

		} catch (Exception e) {
			logger.error("Error in getGeneratedIdForLibraryNo is : " + e, e);
			throw (e);
		}

		return lLstLocCode;
	}
}
