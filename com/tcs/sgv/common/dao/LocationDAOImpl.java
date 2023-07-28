package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.CmnDistrictMst;
import com.tcs.sgv.common.valueobject.CmnLocationMst;
import com.tcs.sgv.common.valueobject.RltLocationDepartment;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;


public class LocationDAOImpl extends GenericDaoHibernateImpl<CmnLocationMst, Long> implements LocationDAO {

	Log logger = LogFactory.getLog(CmnLocationMst.class);

	public LocationDAOImpl(Class<CmnLocationMst> type, SessionFactory sessionFactory) {

		super(type);
		setSessionFactory(sessionFactory);
	}

	/**
	 * This method returns department location by lookup name and language name
	 * 
	 * @param lookupName
	 * @param langName
	 * @return
	 */
	public List getDeptLocation(String lookupName, String langName) throws Exception {

		List dataList = null;
		try {
			Session hibSession = getSession();
			List resList = hibSession.createSQLQuery(
					" select loc_id, loc_name, loc_short_name, department_id,parent_loc_id from Cmn_Location_Mst where lang_Id = (select lang_Id from Cmn_Language_Mst where lang_Short_Name='"
							+ langName + "') and type_Lookup_Id = (select lookup_Id from Cmn_Lookup_Mst where lookup_Name='" + lookupName + "') order by loc_name").list();
			;
			if (resList != null && resList.size() > 0) {
				dataList = new ArrayList();
				Iterator it = resList.iterator();
				Object[] obj = null;
				CmnLocationMst vo = null;
				while (it.hasNext()) {
					obj = (Object[]) it.next();
					vo = new CmnLocationMst();

					vo.setLocId(Long.parseLong(obj[0].toString()));
					vo.setLocName((String) obj[1]);
					vo.setLocShortName((String) obj[2]);
					vo.setDepartmentId(Long.parseLong(obj[3].toString()));
					vo.setParentLocId(Long.parseLong(obj[4].toString()));
					dataList.add(vo);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured in LOcationDAO .getDeptLocation # \n" + e);
			throw e;
		}
		return dataList;
	}

	public String getDeptCodeByLocaId(long billNo, String langName) throws Exception {

		String ddoCode = null;
		try {
			Session hibSession = getSession();
			Query sqlQuery = hibSession.createSQLQuery("select DEPT_ID from sgvo_dept_mst where LANG_ID='" + langName
					+ "' and DEPT_ID=(select DEPARTMENT_ID from rlt_location_department where LOC_CODE=(select DDO_DEPT_ID from trn_bill_register where BILL_NO=" + billNo + "))");
			List resList = sqlQuery.list();
			if (resList != null && resList.size() > 0) {

				Iterator it = resList.iterator();
				Object tuple = null;
				while (it.hasNext()) {
					tuple = (Object) it.next();
					ddoCode = tuple.toString();
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured in VoucherDAOImpl.getVouchersListForDet # \n" + e);
			throw e;
		}
		return ddoCode;
	}

	public String getDeptNameByLocCode(String LocCode, String LangId) throws Exception {

		String LocName = "";
		try {
			Session hibSession = getSession();
			Query sqlQuery = hibSession.createSQLQuery("select Loc_name from cmn_location_mst where location_code = '" + LocCode + "' and LANG_ID=" + LangId);
			List resList = sqlQuery.list();
			if (resList != null && resList.size() > 0) {

				Iterator it = resList.iterator();
				Object tuple = null;
				while (it.hasNext()) {
					tuple = (Object) it.next();
					LocName = tuple.toString();

				}
			}

		} catch (Exception e) {
			logger.error("Exception occured in LocationDAO IMPL  \n" + e);
			throw e;
		}
		return LocName;
	}

	/**
	 * This method reutnrs department code by location id
	 * 
	 * @param locId
	 *            location of the department
	 * @param langName
	 *            locale
	 * @return String
	 */
	public String getDeptCodeByLocId(String locCode, String langName) throws Exception {

		String ddoCode = null;
		try {
			Session hibSession = getSession();
			Query sqlQuery = hibSession.createSQLQuery("select DEPT_ID from sgvo_dept_mst where LANG_ID='" + langName
					+ "' and DEPT_ID=(select DEPARTMENT_ID from rlt_location_department where LOCATION_CODE=" + locCode + ")");
			List resList = sqlQuery.list();
			if (resList != null && resList.size() > 0) {

				Iterator it = resList.iterator();
				Object tuple = null;
				while (it.hasNext()) {
					tuple = (Object) it.next();
					ddoCode = tuple.toString();
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured # \n" + e);
			throw e;
		}
		return ddoCode;
	}

	/**
	 * This method returns all location of a department
	 * 
	 * @param deptName
	 *            Name of the department
	 * @return List
	 */
	public List getLocByDept(ArrayList deptNames) throws Exception {

		List dataList = null;
		try {
			Session hibSession = getSession();
			Query query = hibSession.createQuery(" from CmnLocationMst cl where cl.departmentId in (select departmentId from OrgDepartmentMst dp where dp.depName in (:deptList))");
			query.setParameterList("deptList", deptNames);
			query.setCacheable(true).setCacheRegion("ecache_lookup");
			dataList = query.list();
		} catch (Exception e) {
			logger.error("Exception occured # \n" + e);
			throw e;
		}
		return dataList;
	}

	/**
	 * Method getLocByDept returns Location Id from RLT_LOCATION_DEPARTMENT for
	 * passed department, lang_id, db_id
	 * 
	 * @param lStrDeptCode
	 *            Department Code
	 * @param lStrLangId
	 *            Language ID
	 * @param lLngDbId
	 *            DataBase ID
	 * 
	 * @return ArrayList Containing Location ID
	 */

	public List<RltLocationDepartment> getLocByDept(String lStrDeptCode, Long lLngLangId, Long lLngDbId) throws Exception {

		List<RltLocationDepartment> lLstData = null;
		try {
			Session hibSession = getSession();
			Query query = hibSession.createQuery(" from RltLocationDepartment rld where rld.langId = :langId and departmentId = :deptCode");

			query.setString("langId", lLngLangId + "");
			query.setString("deptCode", lStrDeptCode);
			query.setCacheable(true).setCacheRegion("ecache_lookup");

			lLstData = query.list();

		} catch (Exception e) {
			logger.error("Exception in getLocByDept : " + e, e);
			throw e;
		}
		return lLstData;
	}

	
	/**
	 * @author 219480 Jignesh Sakhiya Method getLocByDept returns Location Id
	 *         from RLT_LOCATION_DEPARTMENT for passed department, lang_id,
	 *         db_id
	 * 
	 * @param lStrDeptCode
	 *            Department Code
	 * @param lStrLangId
	 *            Language ID
	 * @param lLngDbId
	 *            DataBase ID
	 * 
	 * @return ArrayList Containing Location ID
	 */

	public List<RltLocationDepartment> getLocByDept(ArrayList lDeptCode, Long lLngLangId) throws Exception {

		List<RltLocationDepartment> lLstData = null;
		try {
			Session hibSession = getSession();
			Query query = hibSession.createQuery(" from RltLocationDepartment rld where rld.langId = :langId and departmentId in ( :departmentList )");
			query.setString("langId", lLngLangId.toString());
			query.setParameterList("departmentList", lDeptCode);
			query.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstData = query.list();

		} catch (Exception e) {
			logger.error("Exception in getLocByDept : " + e);
			throw e;
		}
		return lLstData;
	}

	public CmnDistrictMst getDistCodeFrmLocId(String lStrLocCode, Long lLngLangId) throws Exception {

		List lLstResult = null;
		CmnDistrictMst lObjCmnDistMst = null;
		try {
			Session hibSession = getSession();
			Query query = hibSession.createQuery(" from CmnDistrictMst cdm where cdm.districtId in (select clm.locDistrictId from CmnLocationMst clm where clm.locationCode='" + lStrLocCode
					+ "' and clm.cmnLanguageMst.langId = " + lLngLangId + ")");
			
			query.setCacheable(true).setCacheRegion("ecache_lookup");
			
			lLstResult = query.list();
			if (lLstResult != null && lLstResult.size() > 0) {
				lObjCmnDistMst = (CmnDistrictMst) lLstResult.get(0);
			}

		} catch (Exception e) {
			logger.error("Exception in getLocByDept : " + e);
			throw e;
		}
		return lObjCmnDistMst;
	}

	public String getLocationName(String lLocationCode, String langId) throws Exception {

		List lLstResult = null;

		try {
			Session hibSession = getSession();
			Query query = hibSession.createQuery(" from CmnLocationMst l where l.locationCode = :locationCode and l.cmnLanguageMst.langId = :langId ");
			query.setString("locationCode", lLocationCode);
			query.setString("langId", langId);
			query.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResult = query.list();

			if (lLstResult != null && lLstResult.size() > 0) {
				Iterator it = lLstResult.iterator();
				if (it.hasNext()) {
					CmnLocationMst cmnLoc = (CmnLocationMst) it.next();
					return cmnLoc.getLocName();
				}
			}

		} catch (Exception e) {
			logger.error("Exception in getLocByDept : " + e);
			throw e;
		}
		return "";

	}

	/**
	 * 
	 * Method getAllDistrict returns District Id,District Name of all District
	 * of Gujarat from CMN_DISTRICT_MST for passed lang_id
	 * 
	 * @param lLngLangId
	 *            Language ID
	 * @return ArrayList Containing District Id,District Name
	 */

	public List getAllDistrict(Long lLngLangId) throws Exception {

		List lLstAllDistrict = new ArrayList();
		ComboValuesVO vo = null;
		try {
			Session hibSession = getSession();
			StringBuffer sbQuery = new StringBuffer();
			sbQuery.append("SELECT dis.districtId,dis.districtName FROM CmnDistrictMst dis WHERE dis.cmnLanguageMst.langId = :langId AND dis.cmnStateMst.stateId = 1 ORDER BY dis.districtName");

			Query hibQuery = hibSession.createQuery(sbQuery.toString());
			hibQuery.setLong("langId", lLngLangId);
			hibQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			List result = hibQuery.list();

			Iterator itResult = result.iterator();
			Object[] tuple = null;
			String strDistrictCode = null;
			String strDistrictName = null;
			while (itResult.hasNext()) {
				vo = new ComboValuesVO();
				tuple = (Object[]) itResult.next();
				strDistrictCode = tuple[0].toString();
				strDistrictName = tuple[1].toString();
				vo.setId(strDistrictCode);
				vo.setDesc(strDistrictName);
				lLstAllDistrict.add(vo);
			}
		} catch (Exception e) {
			logger.error("Exception occured in getAllDistrict. # \n" + e, e);
			throw e;
		}
		return lLstAllDistrict;
	}

	/**
	 * 
	 * Method getAllSecretariateDept returns Location Code,Location Name of all
	 * Departments belonging to Sachivalya from CMN_LOCATION_MST for passed
	 * lang_id
	 * 
	 * @param lLngLangId
	 *            Language ID
	 * @return ArrayList Containing Location Code, Location Name
	 */

	public List getAllSecretariateDept(Long lLngLangId) throws Exception {

		List lLstAllDepartment = new ArrayList();
		ComboValuesVO vo = null;
		try {
			Session hibSession = getSession();
			StringBuffer sbQuery = new StringBuffer();
			sbQuery.append("SELECT loc.locationCode,loc.locName FROM CmnLocationMst loc WHERE loc.departmentId=100001 AND loc.locStateId = 1 and loc.cmnLanguageMst.langId=:langId"
					+ " order by loc.locName ");
			Query hibQuery = hibSession.createQuery(sbQuery.toString());
			hibQuery.setLong("langId", lLngLangId);
			hibQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			List result = hibQuery.list();

			Iterator itResult = result.iterator();
			Object[] tuple = null;
			String strDepartmentCode = null;
			String strDepartmentName = null;
			while (itResult.hasNext()) {
				vo = new ComboValuesVO();
				tuple = (Object[]) itResult.next();
				strDepartmentCode = tuple[0].toString();
				strDepartmentName = tuple[1].toString();
				vo.setId(strDepartmentCode);
				vo.setDesc(strDepartmentName);
				lLstAllDepartment.add(vo);
			}
		} catch (Exception e) {
			logger.error("Exception occured in getAllSecretariateDept. # \n" + e, e);
			throw e;
		}
		return lLstAllDepartment;
	}

	/**
	 * 
	 * Method getAllDistrictOffices returns Location Code,Location Name of all
	 * Offices from CMN_LOCATION_MST for passed lang_id
	 * 
	 * @param lLngLangId
	 *            Language ID
	 * @return ArrayList Containing Location Code, Location Name
	 */

	public List getAllDistrictOffices(Long lLngLangId) throws Exception {

		List lLstallDistrictOffices = new ArrayList();
		ComboValuesVO vo = null;
		try {
			Session hibSession = getSession();
			StringBuffer sbQuery = new StringBuffer();
			sbQuery.append("SELECT loc.locationCode,loc.locName FROM CmnLocationMst loc WHERE loc.departmentId IN (100003,100004) AND loc.locStateId = 1 and loc.cmnLanguageMst.langId=:langId");
			Query hibQuery = hibSession.createQuery(sbQuery.toString());
			hibQuery.setLong("langId", lLngLangId);
			hibQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			List result = hibQuery.list();

			Iterator itResult = result.iterator();
			Object[] tuple = null;
			String strLocCode = null;
			String strLocName = null;
			while (itResult.hasNext()) {
				vo = new ComboValuesVO();
				tuple = (Object[]) itResult.next();
				strLocCode = tuple[0].toString();
				strLocName = tuple[1].toString();
				vo.setId(strLocCode);
				vo.setDesc(strLocName);
				lLstallDistrictOffices.add(vo);
			}
		} catch (Exception e) {
			logger.error("Exception occured in getAllDistrictOffices. # \n" + e, e);
			throw e;
		}
		return lLstallDistrictOffices;
	}
	
	
	public List getAllDistrictCode(Long lLngLangId) throws Exception
	{
		List allDistrictOffices=new ArrayList();
		ComboValuesVO vo=null;
		try{
			Session hibSession=getSession();
			StringBuffer sbQuery = new StringBuffer();
			sbQuery.append("SELECT loc.officeCode,loc.locName"+
							" FROM CmnLocationMst loc"+
							" WHERE loc.departmentId IN (100003,100004)"+
							" AND loc.locStateId = 1"+
							" and loc.cmnLanguageMst.langId=:langId");
			Query hibQuery=hibSession.createQuery(sbQuery.toString());
			hibQuery.setLong("langId",lLngLangId);
			hibQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			List result=hibQuery.list();
		
			Iterator itResult=result.iterator();
			Object[] tuple=null;
			String strLocCode=null;
			String strLocName=null;
			while(itResult.hasNext())
			{
				vo=new ComboValuesVO();
				tuple=(Object[])itResult.next();
				if(tuple[0]!=null)
				{
					strLocCode=tuple[0].toString();
					strLocName=tuple[1].toString();
					
					vo.setId(strLocCode);
					vo.setDesc(strLocName);
			
					allDistrictOffices.add(vo);
				}
			}
		}
		catch(Exception e){
			logger.error( "Exception occured in getAllDistrictCode. # \n"+e , e);
			throw e;
		}
		return allDistrictOffices;
	}

	/**
	 * 
	 * Method getAllHod returns Location Code,Location Name of all HOD from
	 * CMN_LOCATION_MST for passed lang_id
	 * 
	 * @param lLngLangId
	 *            Language ID
	 * @return ArrayList Containing Location Code, Location Name
	 */

	public List getAllHod(Long lLngLangId) throws Exception {

		List lLstAllHod = new ArrayList();
		ComboValuesVO vo = null;
		try {
			Session hibSession = getSession();
			StringBuffer sbQuery = new StringBuffer();
			sbQuery.append("SELECT loc.locationCode,loc.locName FROM CmnLocationMst loc WHERE loc.departmentId = 100011 AND loc.locStateId = 1 and loc.cmnLanguageMst.langId=:langId");
			Query hibQuery = hibSession.createQuery(sbQuery.toString());
			hibQuery.setLong("langId", lLngLangId);
			hibQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			List result = hibQuery.list();

			Iterator itResult = result.iterator();
			Object[] tuple = null;
			String strLocCode = null;
			String strLocName = null;
			while (itResult.hasNext()) {
				vo = new ComboValuesVO();
				tuple = (Object[]) itResult.next();
				strLocCode = tuple[0].toString();
				strLocName = tuple[1].toString();
				vo.setId(strLocCode);
				vo.setDesc(strLocName);
				lLstAllHod.add(vo);
			}
		} catch (Exception e) {
			logger.error("Exception occured in getAllHod. # \n" + e, e);
			throw e;
		}
		return lLstAllHod;
	}

	/**
	 * 
	 * Method getHodByDept returns Location Code,Location Name of selected HOD's
	 * of particular Department from CMN_LOCATION_MST for passed location_code,
	 * lang_id
	 * 
	 * @param strLocationCode
	 *            Location Code
	 * @param lLngLangId
	 *            Language ID
	 * @return ArrayList Containing Location Code, Location Name
	 */

	public List getHodByDept(String strLocationCode, Long lLngLangId) throws Exception {

		List lLstSelectedHod = new ArrayList();
		ComboValuesVO vo = null;
		try {
			Session hibSession = getSession();
			StringBuffer sbQuery = new StringBuffer();
			sbQuery
					.append("SELECT loc.locationCode,loc.locName FROM CmnLocationMst loc WHERE loc.departmentId = 100011 AND loc.locStateId = 1 AND loc.parentLocId= :department and loc.cmnLanguageMst.langId=:langId");
			Query hibQuery = hibSession.createQuery(sbQuery.toString());
			hibQuery.setLong("langId", lLngLangId);
			hibQuery.setString("department", strLocationCode);
			hibQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			List result = hibQuery.list();

			Iterator itResult = result.iterator();
			Object[] tuple = null;
			String strLocCode = null;
			String strLocName = null;

			while (itResult.hasNext()) {
				vo = new ComboValuesVO();
				tuple = (Object[]) itResult.next();
				strLocCode = tuple[0].toString();
				strLocName = tuple[1].toString();
				vo.setId(strLocCode);
				vo.setDesc(strLocName);
				lLstSelectedHod.add(vo);
			}
		} catch (Exception e) {
			logger.error("Exception occured in getHodByDept. # \n" + e, e);
			throw e;
		}
		return lLstSelectedHod;

	}

}
