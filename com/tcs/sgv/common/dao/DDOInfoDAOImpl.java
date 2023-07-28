package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.tcs.sgv.common.helper.SessionHelper;
import com.tcs.sgv.common.valuebeans.CommonVO;
import com.tcs.sgv.common.valueobject.OrgDdoMst;
import com.tcs.sgv.ess.valueobject.OrgDesignationMst;
import com.tcs.sgv.ess.valueobject.OrgPostMst;


/**
 * 
 * @author Nirav Bumia
 * 
 */

public class DDOInfoDAOImpl implements DDOInfoDAO {

	Log logger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	public DDOInfoDAOImpl(SessionFactory sessionFactory) {

		ghibSession = sessionFactory.getCurrentSession();
	}

	public List getDDOInfo(String lStrDDOCode, Long lLngLangId, Long lLngDBId) throws Exception {

		List<OrgDdoMst> lListReturn = null;

		try {
			Criteria lObjCriteria = ghibSession.createCriteria(OrgDdoMst.class);
			lObjCriteria.add(Restrictions.eq("ddoCode", lStrDDOCode));
			lObjCriteria.add(Restrictions.eq("langId", lLngLangId));
			lObjCriteria.add(Restrictions.eq("dbId", lLngDBId));
			//lObjCriteria.setCacheable(true).setCacheRegion("ddoCache");

			lListReturn = lObjCriteria.list();
		} catch (Exception e) {
			logger.error("Error in getDDOInfo and Error is : " + e, e);
			throw e;
		}

		return lListReturn;
	}

	public List getDDOInfoByPost(Long lLngPostId, Long lLngLangId, Long lLngDBId) throws Exception {

		List<OrgDdoMst> lListReturn = null;

		try {
			Criteria lObjCriteria = ghibSession.createCriteria(OrgDdoMst.class);
			lObjCriteria.add(Restrictions.eq("postId", lLngPostId));
			lObjCriteria.add(Restrictions.eq("langId", lLngLangId));
			lObjCriteria.add(Restrictions.eq("dbId", lLngDBId));
			//lObjCriteria.setCacheable(true).setCacheRegion("ddoCache");

			lListReturn = lObjCriteria.list();
		} catch (Exception e) {
			logger.error("Error in getDDOInfoByPost and Error is : " + e, e);
			throw e;
		}

		return lListReturn;
	}

	public List<OrgDdoMst> getCompleteDDOInfoByCardex(Integer lIntCardexNo, String lStrLocationCode, Long lLngLangId) throws Exception {

		List<OrgDdoMst> lListReturn = null;

		try {			
			/*CacheManager cacheManager = CacheManager.getInstance();
			Cache cardexInfoCache = cacheManager.getCache("ddoDataCache");
			
			Element cardexInfo = cardexInfoCache.get(lIntCardexNo + "-" + lStrLocationCode + "-" + lLngLangId);
			
			if(cardexInfo == null){*/
				StringBuilder lSBQuery = new StringBuilder();

				lSBQuery.append("SELECT DM FROM OrgDdoMst DM, RltDdoOrg DO WHERE DM.cardexNo = :cardexNo AND DM.langId = :langId AND DM.ddoCode = DO.ddoCode AND DO.locationCode=:locationCode AND DM.activateFlag=1 AND DM.type in (1,2,3) ");
				Query query = ghibSession.createQuery(lSBQuery.toString());
				query.setInteger("cardexNo", lIntCardexNo);
				query.setString("locationCode", lStrLocationCode);
				query.setLong("langId", lLngLangId);
				//query.setCacheable(true).setCacheRegion("ddoCache");
				lListReturn = query.list();
				
				/*cardexInfo = new Element(lIntCardexNo + "-" + lStrLocationCode + "-" + lLngLangId,lListReturn);				
				cardexInfoCache.put(cardexInfo);
			} else {
				lListReturn = (List<OrgDdoMst>)cardexInfo.getValue();
			}*/
			
		} catch (Exception e) {
			logger.error("Error in getCompleteDDOInfoByCardex and Error is : " + e, e);
			throw e;
		}

		return lListReturn;
	}

	public List getDDOInfoByNo(Long lLngDDONo, String lDDOType, Long lLngLangId, Long lLngDBId) throws Exception {

		List<OrgDdoMst> lListReturn = null;

		try {
			Criteria lObjCriteria = ghibSession.createCriteria(OrgDdoMst.class);
			lObjCriteria.add(Restrictions.eq("ddoNo", lLngDDONo));
			lObjCriteria.add(Restrictions.eq("ddoType", (lDDOType != null && lDDOType.trim().length() > 0) ? lDDOType:new String[]{"A", "D"}));					
			lObjCriteria.add(Restrictions.eq("langId", lLngLangId));
			lObjCriteria.add(Restrictions.eq("dbId", lLngDBId));
			//lObjCriteria.setCacheable(true).setCacheRegion("ddoCache");

			lListReturn = lObjCriteria.list();
		} catch (Exception e) {
			logger.error("Error in getDDOInfoByNo and Error is : " + e, e);
			throw e;
		}

		return lListReturn;
	}

	public List getBillOfficeForDDO(String lStrDDOCode, Long lLngLangId, Long lLngDBId) throws Exception {

		List<CommonVO> lListReturn = new ArrayList<CommonVO>();

		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT B.locationCode, B.locName FROM RltDdoOffice A, ");
			lSBQuery.append(" CmnLocationMst B WHERE A.loctnCode = B.locationCode ");
			lSBQuery.append(" AND A.ddoCode = :ddoCode AND ");
			lSBQuery.append(" B.cmnLanguageMst.langId = :langId ");
			lSBQuery.append(" AND A.dbId = :dbId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDDOCode);
			lQuery.setParameter("langId", lLngLangId);
			lQuery.setParameter("dbId", lLngDBId);
			//lQuery.setCacheable(true).setCacheRegion("ddoCache");

			logger.info("Query for getBillOfficeForDDO : " + lQuery.toString());
			logger.info("And Parameter is " + lStrDDOCode + " " + lLngLangId + " " + lLngDBId);

			List lListData = lQuery.list();

			if (lListData != null) {
				Iterator lItr = lListData.iterator();
				Object[] lObj = null;
				CommonVO lComVo = null;

				while (lItr.hasNext()) {
					lObj = (Object[]) lItr.next();
					if (lObj != null) {
						lComVo = new CommonVO();
						lComVo.setCommonId(lObj[0].toString());
						lComVo.setCommonDesc(lObj[1].toString());
						lListReturn.add(lComVo);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error in getBillOfficeForDDO and Error is : " + e, e);
			throw e;
		}

		return lListReturn;
	}

	public List getTrsryOfficeForDDO(String lStrDDOCode, Long lLngLangId) throws Exception {

		List<CommonVO> lListReturn = new ArrayList<CommonVO>();

		StringBuilder lSBQuery = new StringBuilder();

		try {
			lSBQuery.append(" select loc.locId, loc.locName from RltDdoOrg rd,");
			lSBQuery.append(" CmnLocationMst loc");
			lSBQuery.append(" where rd.ddoCode = :ddoCode and loc.locationCode = rd.locationCode and");
			lSBQuery.append(" loc.cmnLanguageMst.langId = :langId and rd.activateFlag = 1 ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDDOCode);
			lQuery.setParameter("langId", lLngLangId);
			//lQuery.setCacheable(true).setCacheRegion("ddoCache");

			List lLstData = lQuery.list();

			if (lLstData != null) {
				Iterator lItr = lLstData.iterator();
				Object[] lObj = null;
				CommonVO lComVo = null;

				while (lItr.hasNext()) {
					lObj = (Object[]) lItr.next();

					if (lObj != null) {
						lComVo = new CommonVO();
						lComVo.setCommonId(lObj[0].toString());
						lComVo.setCommonDesc(lObj[1].toString());
						lListReturn.add(lComVo);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error in getTrsryOfficeForDDO and Error is : " + e, e);
			throw e;
		}

		return lListReturn;
	}

	public List getOrgPostMstbyPost(Long lLngPostId) throws Exception {

		List<OrgPostMst> lListReturn = null;

		try {
			Criteria lObjCriteria = ghibSession.createCriteria(OrgPostMst.class);
			lObjCriteria.add(Restrictions.eq("postId", lLngPostId));

			lListReturn = lObjCriteria.list();
			logger.info("Parameter for getOrgPostMstbyPost() is " + lLngPostId);
		} catch (Exception e) {
			logger.error("Error in getOrgPostMstbyPost() of  DDOInfoDAOImpl : " + e, e);
			throw e;
		}

		return lListReturn;
	}

	public List getOrgDesgMstbyDesgCode(String lStrDesgnCode) throws Exception {

		List<OrgDesignationMst> resList = null;

		try {
			Criteria lObjCriteria = ghibSession.createCriteria(OrgDesignationMst.class);
			lObjCriteria.add(Restrictions.eq("dsgnCode", lStrDesgnCode));

			resList = lObjCriteria.list();
		} catch (Exception e) {
			logger.error("Error in getOrgDesgMstbyDesgCode() of DDOInfoDAOImpl : " + e, e);
			throw e;
		}

		return resList;
	}

	public String getLocationCodeFromDdoCode(String lStrDdoCode, Long lLngLangId) throws Exception {

		List lLstData = null;
		StringBuilder lSBQuery = new StringBuilder();
		String lStrLocCode = null;

		try {
			lSBQuery.append(" select dm.locationCode OrgDdoMst dm ");
			lSBQuery.append(" where dm.ddoCode= :ddoCode ");
			lSBQuery.append(" and dm.langId= :langId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("ddoCode", lStrDdoCode);
			lQuery.setParameter("langId", lLngLangId);
			//lQuery.setCacheable(true).setCacheRegion("ddoCache");

			lLstData = lQuery.list();

			if (lLstData != null && !lLstData.isEmpty()) {
				lStrLocCode = (String) lLstData.get(0);
			}
		} catch (Exception e) {
			logger.error("Error in getLocationCodeFromDdoCode : " + e, e);
			throw e;
		}

		return lStrLocCode;
	}

	public String getAllCardexByLocation(String lStrLocCode, Long lLngLangId) throws Exception {

		List lLstData = new ArrayList();
		String strList = "";

		try {

			Query lQuery = ghibSession.createQuery("select dm.cardexNo, dm.ddoNo, dm.ddoName, dm.ddoCode, dm.activateFlag from RltDdoOrg do, OrgDdoMst dm "
					+ "where do.locationCode = :locationCode and dm.type in (1,2,3) and dm.langId = :langId and dm.ddoCode=do.ddoCode ");

			lQuery.setParameter("locationCode", lStrLocCode);
			lQuery.setParameter("langId", lLngLangId);
			//lQuery.setCacheable(true).setCacheRegion("ddoCache");

			lLstData = lQuery.list();

			if (!lLstData.isEmpty()) {
				Object[] obj = null;
				for (int x = 0; x < lLstData.size(); x++) {
					obj = (Object[]) lLstData.get(x);
					strList += obj[0] + "~" + obj[1] + "~" + obj[2] + "~" + obj[3] + "~" + obj[4] + "^";
				}
			}

		} catch (Exception e) {
			logger.error("Error in getAllCardexByLocation(String lStrLocCode, Long lLngLangId) : " + e, e);
			throw e;
		}

		return strList;
	}

	public String getAllCardexByLocation(List subLoCode, Long lLngLangId) throws Exception {

		List<Object[]> lLstData = null;
		StringBuffer strList = new StringBuffer();
		char tield = '~';
		try {
			Query lQuery = ghibSession.createSQLQuery(" SELECT DM.CARDEX_NO, DM.DDO_NO, DM.DDO_NAME, DM.DDO_CODE  FROM RLT_DDO_ORG DO, ORG_DDO_MST DM "
					+ " WHERE DO.LOCATION_CODE IN (:locationCode) AND DM.TYPE IN (1, 2, 3) AND  DM.LANG_ID = :langId AND DM.DDO_CODE = DO.DDO_CODE AND  DM.ACTIVATE_FLAG = 1 UNION "
					+ " SELECT -1 CDX, DM.DDO_NO, DM.DDO_NAME, DM.DDO_CODE FROM ORG_DDO_MST DM  WHERE DM.ADMIN_FLAG = 1 AND DM.LANG_ID = :langId ");

			lQuery.setParameterList("locationCode", subLoCode);
			lQuery.setParameter("langId", lLngLangId);
			
			lLstData = lQuery.list();
			if (!lLstData.isEmpty()) 
			{
				for (Object[] obj : lLstData) 
				{
				   strList.append((obj[0]== null ? ""   : obj[0].toString()) + tield + 
								  (obj[1] == null ? "*" : obj[1].toString()) + tield + 
								  (obj[2] == null ? ""  : obj[2].toString()) + tield +
								  (obj[3] == null ? ""  : obj[3].toString()) + "^");
				}
			}

		} catch (Exception e) {
			logger.error("Error in getAllCardexByLocation(List subLoCode, Long lLngLangId): " + e, e);
			throw e;
		}
		return strList.toString();
	}

	/**
	 * Method to make search for DDO information from Cardex No
	 * @param lIntCardexNo
	 * @param lStrOfficeCode
	 * @param lLngLangId
	 * @param lLngDBId
	 * 
	 * @return List
	 */
	public List getDDOInfoByCardexNo(Integer lIntCardexNo, String lStrOfficeCode, Long lLngLangId, Long lLngDBId) throws Exception {

		List lListReturn = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT O.cardexNo,O.ddoNo,O.ddoName,O.ddoCode,O.type FROM OrgDdoMst O, RltDdoOrg RO ");
			lSBQuery.append(" WHERE RO.ddoCode = O.ddoCode AND O.cardexNo = :cardexNo ");
			lSBQuery.append(" AND RO.locationCode = :locationCode ");
			lSBQuery.append(" AND O.langId = :langId AND O.dbId = :dbId ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			//lQuery.setCacheable(true).setCacheRegion("ddoCache");
			lQuery.setParameter("cardexNo", lIntCardexNo);
			lQuery.setParameter("locationCode", lStrOfficeCode);
			lQuery.setParameter("langId", lLngLangId);
			lQuery.setParameter("dbId", lLngDBId);

			lListReturn = lQuery.list();

		} catch (Exception e) {
			logger.error("Error in getDDOInfoByCardex and Error is : " + e, e);
			throw e;
		}

		return lListReturn;
	}

	/**
	 * Method to make search for DDO information from DDO Name
	 * @param lStrDDOName
	 * @param lStrOfficeCode
	 * @param lLngLangId
	 * @param lLngDBId
	 * 
	 * @return List
	 */
	public List getDDOInfoByName(String lStrDDOName, String lStrOfficeCode, Long lLngLangId, Long lLngDBId) throws Exception {

		String query = null;
		Query lQuery = null;
		List dataList = null;

		try {
			query = "select odm.cardex_no,odm.ddo_no,odm.ddo_name,odm.ddo_code from org_ddo_mst odm, rlt_ddo_org rdo "
					+ "where odm.ddo_code=rdo.ddo_code and odm.db_id=:dbid and rdo.location_code=:locationCode " + "and upper(odm.ddo_name) like upper('%" + lStrDDOName
					+ "%') and odm.lang_id=:langId ";
			lQuery = ghibSession.createSQLQuery(query);
			lQuery.setParameter("dbid", lLngDBId);
			lQuery.setParameter("locationCode", lStrOfficeCode);
			lQuery.setParameter("langId", lLngLangId);

			dataList = lQuery.list();

		} catch (Exception e) {
			logger.error("Error in getDDOInfoByName and Error is : " + e, e);
			throw e;
		}

		return dataList;
	}

	/**
	 * Method to check whether this ddo has raised any bill within a month.
	 * 
	 * @param lStrDDOName
	 * @param lStrOfficeCode
	 * @param lLngLangId
	 * @param lLngDBId
	 * 
	 * @return List
	 */
	public Date getLastBillDate(Integer lIntCardexNo, String lStrOfficeCode, Long lLngLangId, Date lDtCurDate) throws Exception {

		Query lQuery = null;
		List dataList = null;
		Date lDtLastBillDate = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" SELECT MAX(tbr.inwardDt) FROM TrnBillRegister tbr, OrgDdoMst odm, RltDdoOrg rdo" +
				" WHERE odm.cardexNo = :cardexNo AND rdo.locationCode = :locationCode AND odm.type < 4 " +
				" AND tbr.tsryOfficeCode = rdo.locationCode " +
				" AND (odm.endDate is null or odm.endDate > :endDate) " +
				" AND odm.activateFlag = 1 " +
				" AND odm.langId = :langId " +
				" AND odm.ddoCode = rdo.ddoCode " +
				" AND tbr.tokenNum IS NOT NULL " +
				" AND tbr.cardexNo = odm.cardexNo " +
				" AND tbr.ddoCode = rdo.ddoCode");

			lQuery = ghibSession.createQuery(lSBQuery.toString());
			
			lQuery.setParameter("cardexNo", lIntCardexNo);
			lQuery.setParameter("locationCode", lStrOfficeCode);
			lQuery.setParameter("langId", lLngLangId);
			lQuery.setParameter("endDate", lDtCurDate);

			dataList = lQuery.list();
			if (dataList != null && dataList.size() > 0) {
				lDtLastBillDate = (Date) dataList.get(0);
			}
		} catch (Exception e) {
			logger.error("Error in getDDOInfoByNo and Error is : " + e, e);
			throw e;
		}

		return lDtLastBillDate;
	}

	public String getCardexMapping(List subLoCode, Long lLngLangId) throws Exception {

		List<Object[]> lLstData = new ArrayList();
		StringBuffer strList = new StringBuffer();
		char tield = '~';
		try {

			Query lQuery = ghibSession.createQuery("select dm.cardexNo, dm.ddoName from RltDdoOrg do, OrgDdoMst dm  where do.locationCode in(:locationCode) and dm.type in (1,2,3) "
					+ " and dm.langId = :langId and dm.ddoCode=do.ddoCode and dm.activateFlag = 1 ");

			lQuery.setParameterList("locationCode", subLoCode);
			lQuery.setParameter("langId", lLngLangId);
			//lQuery.setCacheable(true).setCacheRegion("ddoCache");
			
			lLstData = lQuery.list();
			if (lLstData != null && !lLstData.isEmpty()) {
				for (Object[] cols : lLstData) {
					for (Object val : cols) {
						strList.append((val != null ? val : ""));
						strList.append('^');
					}
					strList.append(tield);
				}
			} else
				strList.append("error");

		} catch (Exception e) {
			logger.error("Error in getCardexMapping(List subLoCode, Long lLngLangId): " + e, e);
			throw e;
		}

		return new AjaxXmlBuilder().addItemAsCData("receiptDetId", strList.toString()).toString();
	}

	/**
	 * Fetches all Administrative DDOs whose grant can be used by Non-Government DDO(type=3)
	 * @param lLngLangId
	 * @param lLngDBId
	 * 
	 * @return List
	 */
	public List<OrgDdoMst> getAllAdminDDO(Long lLngLangId, Long lLngDBId) throws Exception {

		List<OrgDdoMst> lListReturn = null;
		try {			
			/*CacheManager cacheManager = CacheManager.getInstance();
			Cache ddoDataCache = cacheManager.getCache("ddoDataCache");
			
			Element adminDdoElement = ddoDataCache.get("ADMIN_DDO_LIST");
			
			if(adminDdoElement == null){*/
				StringBuilder lSBQuery = new StringBuilder();
				lSBQuery.append(" FROM OrgDdoMst dm WHERE dm.adminFlag=1 AND dm.type = 1 ");
				lSBQuery.append(" AND dm.langId=:langId AND dm.activateFlag=1 ORDER BY dm.cardexNo ");

				Query lQuery = ghibSession.createQuery(lSBQuery.toString());

				lQuery.setParameter("langId", lLngLangId);
				//lQuery.setCacheable(true).setCacheRegion("ddoCache");
				
				lListReturn = lQuery.list();
				/*adminDdoElement = new Element("ADMIN_DDO_LIST",lListReturn);
				ddoDataCache.put(adminDdoElement);
			} else {
				lListReturn = (List<OrgDdoMst>)adminDdoElement.getValue();
			}*/
		} catch (Exception e) {
			logger.error("Error in getAllAdminDDO and Error is : " + e, e);
			throw e;
		}

		return lListReturn;
	}

	/**
	 * Checks whether the given combination of bill type and DDO exists or not
	 * @param subjectId
	 * @param ddoCode
	 * @param langId
	 * 
	 * @return List
	 */
	public boolean ddoBilltypeExists(Long subjectId, String ddoCode, Long langId) throws Exception {

		String result = null;
		StringBuilder lSBQuery = new StringBuilder();
		List paramList = new ArrayList();
		boolean ddoBilltypeExists = false;

		try {
			paramList.add(-1l);
			paramList.add(subjectId);

			lSBQuery
					.append(" SELECT dm.DDO_CODE ddoCode FROM rlt_ddo_billtype bd,org_ddo_mst dm WHERE dm.DDO_CODE = bd.DDO_CODE AND dm.ddo_code=:ddoCode AND bd.subject_id IN (:billType) AND dm.activate_flag=1 AND dm.TYPE < 4 AND dm.lang_id=:langId ");

			SQLQuery lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			//lQuery.setCacheable(true).setCacheRegion("ddoCache");
			
			lQuery.setString("ddoCode", ddoCode);
			lQuery.setLong("langId", langId);
			lQuery.setParameterList("billType", paramList);
			
			lQuery.addScalar("ddoCode",Hibernate.STRING); 
			
			result = (String)lQuery.uniqueResult();

			if (result != null) {
				ddoBilltypeExists = true;
			}
		} catch (Exception e) {
			logger.error("Error in ddoBilltypeExists and Error is : " + e, e);
			throw e;
		}

		return ddoBilltypeExists;
	}

	/**
	 * Fetches DDO Information based on given major head and office code
	 * @param majorHead
	 * @param officeCode
	 * @param lLngLangId
	 * @param lLngDBId
	 * 
	 * @return List
	 */
	public List getDDOInfoByOfficeCodeAndMjrHd(String majorHead, String officeCode, Long lLngLangId, Long lLngDBId) throws Exception {

		List lListReturn = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();

			lSBQuery.append(" FROM OrgDdoMst dm WHERE dm.majorHead=:majorHead AND dm.officeCode=:officeCode ");
			lSBQuery.append(" AND dm.langId=:langId AND dm.dbId=:dbId AND dm.activateFlag=1 ");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			//lQuery.setCacheable(true).setCacheRegion("ddoCache");

			lQuery.setParameter("majorHead", majorHead);
			lQuery.setParameter("officeCode", officeCode);
			lQuery.setParameter("langId", lLngLangId);
			lQuery.setParameter("dbId", lLngDBId);

			lListReturn = lQuery.list();
		} catch (Exception e) {
			logger.error("Error in getDDOInfoByOfficeCodeAndMjrHd and Error is : " + e, e);
			throw e;
		}

		return lListReturn;
	}

}