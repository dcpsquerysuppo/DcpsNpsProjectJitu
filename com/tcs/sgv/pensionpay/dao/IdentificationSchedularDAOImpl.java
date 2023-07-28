/* Copyright TCS 2011, All Rights Reserved.
 * 
 * 
 ******************************************************************************
 ***********************Modification History***********************************
 *  Date   				Initials	     Version		Changes and additions
 ******************************************************************************
 * 	Feb 14, 2011		Shripal Soni								
 *******************************************************************************
 */
package com.tcs.sgv.pensionpay.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.pensionpay.valueobject.MstPensionChecklist;
import com.tcs.sgv.pensionpay.valueobject.TrnPensionerChecklistDtls;
import com.tcs.sgv.pensionpay.valueobject.TrnPnsnpmntSchedular;


/**
 * Class Description -
 * 
 * 
 * @author Shripal Soni
 * @version 0.1
 * @since JDK 5.0 Feb 14, 2011
 */
public class IdentificationSchedularDAOImpl extends GenericDaoHibernateImpl implements IdentificationSchedularDAO {

	private final Log gLogger = LogFactory.getLog(getClass());
	Session ghibSession = null;

	SessionFactory sessionFactory = null;
	private final ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/pensionpay/PensionCaseConstants");

	public IdentificationSchedularDAOImpl(Class type, SessionFactory sessionFactory) {

		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public ArrayList<TrnPnsnpmntSchedular> getLastAssignedSlotDetail(Long locCode) throws Exception {

		Query lQuery = null;
		StringBuffer lSb = new StringBuffer();
		List resultList = new ArrayList();
		ArrayList<TrnPnsnpmntSchedular> lastAssignedSlotDtlList = null;
		try {
			ghibSession = this.getSession();
			lSb.append("from TrnPnsnpmntSchedular \n");
			lSb.append("where locationCode = :locCode \n");
			lSb.append("order by callDate desc,slotNo desc ");
			lQuery = ghibSession.createQuery(lSb.toString());
			lQuery.setLong("locCode", locCode);
			lQuery.setMaxResults(1);
			resultList = lQuery.list();
			if (resultList != null && resultList.size() > 0) {
				lastAssignedSlotDtlList = new ArrayList<TrnPnsnpmntSchedular>();
				lastAssignedSlotDtlList.add((TrnPnsnpmntSchedular) resultList.get(0));
			}
		} catch (Exception e) {
			//e.printStackTrace();
			throw (e);
		}
		return lastAssignedSlotDtlList;
	}

	public List getSchedularObjFromPsnrCode(Long pnsrCode, Long locCode) throws Exception {

		Query lQuery = null;
		StringBuffer lSb = new StringBuffer();
		List resultList = new ArrayList();
		try {
			// ---There may be two entries for one pensioner in one location for
			// transfer
			// cases.So getting latest entry from table.
			ghibSession = this.getSession();
			lSb.append("from TrnPnsnpmntSchedular \n");
			lSb.append("where pensionerCode= :pnsrCode \n");
			lSb.append("and locationCode= :locCode \n");
			lSb.append("order by schedular_id desc");
			lQuery = ghibSession.createQuery(lSb.toString());
			lQuery.setLong("pnsrCode", pnsrCode);
			lQuery.setLong("locCode", locCode);
			lQuery.setMaxResults(1);
			resultList = lQuery.list();
		} catch (Exception e) {
			//e.printStackTrace();
			throw (e);
		}
		return resultList;

	}

	public List getScheduleDtls() throws Exception {

		Query lQuery = null;
		StringBuffer lSb = new StringBuffer();
		List resultList = new ArrayList();
		try {
			ghibSession = this.getSession();
			lSb.append("SELECT schedularId,callDate from TrnPnsnpmntSchedular \n");
			lSb.append("where scheduleStatus = :scheduleStatus");
			lQuery = ghibSession.createQuery(lSb.toString());
			lQuery.setParameter("scheduleStatus", gObjRsrcBndle.getString("IDENT.AWAITED"));
			resultList = lQuery.list();
		} catch (Exception e) {
			gLogger.error("Error in getScheduleDtls is :" + e);
			throw (e);
		}
		return resultList;
	}

	public void updateScheduleStatus(Long argLocCode, Date argCurrDate) throws Exception {

		Query lQuery = null;
		StringBuffer lSb = new StringBuffer();
		try {
			ghibSession = this.getSession();
			lSb.append("UPDATE trn_pnsnpmnt_schedular SET schedule_status = :scheduleToStatus \n");
			lSb.append("WHERE schedule_status = :scheduleFromStatus \n");
			lSb.append("AND location_code = :locCode \n");
			lSb.append("AND call_date < :currDate \n");
			lQuery = ghibSession.createSQLQuery(lSb.toString());
			lQuery.setString("scheduleToStatus", gObjRsrcBndle.getString("IDENT.REMINDER"));
			lQuery.setString("scheduleFromStatus", gObjRsrcBndle.getString("IDENT.AWAITED"));
			lQuery.setLong("locCode", argLocCode);
			lQuery.setDate("currDate", argCurrDate);
			lQuery.executeUpdate();
		} catch (Exception e) {
			gLogger.error("Error in updateScheduleStatus is :" + e);
			throw (e);
		}

	}

	public List<Object[]> getIdentificationLetterDtls(String[] lLstPnsrCode, String lStrLocCode) throws Exception {

		List<Object[]> lLstIdentLetterDtl = new ArrayList<Object[]>();
		Query hqlQuery = null;
		try {

			StringBuilder lStrQuery = new StringBuilder();
			Session hibSession = getSession();

			// lStrQuery.append("SELECT tpr.ppoNo,mphr.firstName, \n");
			// lStrQuery.append(" tps.callDate,csm.stateName,cdm.districtName, \n");
			// lStrQuery.append(" mphr.pnsnrAddr1,mphr.pnsnrAddr2,mphr.pnsnrAddrTown,mphr.pnsnrAddrLocality, \n");
			// lStrQuery.append(" clm.locName,clm.locShortName,clm.locAddr1,clm.locAddr2,concat(concat(concat(concat(oem.empFname,' '),oem.empMname),' '),oem.empLname), \n");
			// lStrQuery.append(" tps.slotTime,tpr.pensionerCode,mphr.dateOfDeath \n");
			// lStrQuery.append(" FROM TrnPensionRqstHdr tpr, MstPensionerDtls mpd, MstPensionerHdr mphr, \n");
			// lStrQuery.append(" CmnStateMst csm,CmnDistrictMst cdm,CmnLocationMst clm, RltAuditorBank audi, \n");
			// lStrQuery.append(" OrgUserpostRlt usrpst, OrgUserMst usr,OrgEmpMst oem,TrnPnsnpmntSchedular tps \n");
			// lStrQuery.append(" WHERE mphr.pensionerCode=tpr.pensionerCode AND mphr.pensionerCode=mpd.pensionerCode AND  tpr.pensionerCode=mpd.pensionerCode \n");
			// lStrQuery.append(" AND tps.pensionerCode = mphr.pensionerCode \n");
			// lStrQuery.append(" AND csm.stateId=mphr.stateCode AND cdm.districtId=mphr.districtCode \n");
			// lStrQuery.append(" AND mphr.locationCode=clm.locId AND usrpst.orgPostMstByPostId.postId = audi.postId \n");
			// lStrQuery.append(" AND usr.userId = usrpst.orgUserMst.userId AND usrpst.orgUserMst.userId= oem.orgUserMst.userId \n");
			// lStrQuery.append(" AND audi.branchCode=mpd.branchCode \n");
			// lStrQuery.append(" AND mphr.pensionerCode IN (:lLstPnsrCode) \n");
			// lStrQuery.append(" AND audi.locationCode=:lLocationCode \n");
			// lStrQuery.append(" AND tps.locationCode=:lLngLocCode \n");

			lStrQuery.append("SELECT tpr.ppoNo,mphr.firstName, \n");
			lStrQuery.append(" tps.callDate,csm.stateName,cdm.districtName, \n");
			lStrQuery.append(" mphr.pnsnrAddr1,mphr.pnsnrAddr2,mphr.pnsnrAddrTown,mphr.pnsnrAddrLocality, \n");			
			lStrQuery.append(" clm.locName,clm.locShortName,clm.locAddr1,clm.locAddr2, \n");
			lStrQuery.append(" tps.slotTime,tpr.pensionerCode,mphr.dateOfDeath, \n");
			lStrQuery.append(" mphr.pnsnrAddr1Marathi,mphr.pnsnrAddr2Marathi,mphr.pnsnrAddrTownMarathi, \n");
			lStrQuery.append(" mphr.stateCode,mphr.districtCode \n");
			lStrQuery.append(" FROM TrnPensionRqstHdr tpr, MstPensionerDtls mpd, MstPensionerHdr mphr, \n");
			lStrQuery.append(" CmnStateMst csm,CmnDistrictMst cdm,CmnLocationMst clm, \n");
			lStrQuery.append(" TrnPnsnpmntSchedular tps \n");
			lStrQuery.append(" WHERE mphr.pensionerCode=tpr.pensionerCode \n");
			lStrQuery.append(" AND mphr.pensionerCode=mpd.pensionerCode \n");
			lStrQuery.append(" AND  tpr.pensionerCode=mpd.pensionerCode \n");
			lStrQuery.append(" AND tps.pensionerCode = mphr.pensionerCode \n");
			lStrQuery.append(" AND csm.stateCode=mphr.stateCode AND cdm.districtCode=mphr.districtCode \n");
			lStrQuery.append(" AND csm.cmnLanguageMst.langId = :langId AND cdm.cmnLanguageMst.langId = :langId \n");
			lStrQuery.append(" AND mphr.locationCode=clm.locId \n");
			lStrQuery.append(" AND mphr.pensionerCode IN (:lLstPnsrCode) \n");
			lStrQuery.append(" AND tps.locationCode=:lLngLocCode \n");

			hqlQuery = hibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("lLngLocCode", Long.valueOf(lStrLocCode));
			hqlQuery.setInteger("langId", 2);
			hqlQuery.setParameterList("lLstPnsrCode", lLstPnsrCode);

			lLstIdentLetterDtl = hqlQuery.list();

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error(" Error is :" + e, e);
			throw e;
		}
		return lLstIdentLetterDtl;
	}

	public Map<String, String> getValidFamilyMemberName(String[] lArrPnsrCode) throws Exception {

		Map<String, String> lMapFamilyDtls = new HashMap<String, String>();
		try {
			StringBuilder lSBQuery = new StringBuilder();

			ghibSession = getSession();

			lSBQuery.append("select fd.pensionerCode,fd.name \n");
			lSBQuery.append("from MstPensionerFamilyDtls fd \n");
			lSBQuery.append("where fd.pensionerCode in (:lArrPnsrCode) \n");
			lSBQuery.append("and fd.percentage=:lPer \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());

			lQuery.setLong("lPer", 100);
			lQuery.setParameterList("lArrPnsrCode", lArrPnsrCode);

			List<Object[]> lLstVO = lQuery.list();
			for (Object[] lArrObj : lLstVO) {
				if (lArrObj[0] != null) {
					lMapFamilyDtls.put(lArrObj[0].toString(), (lArrObj[1] != null) ? lArrObj[1].toString() : "");
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapFamilyDtls;
	}

	public List<MstPensionChecklist> getAllCheckList() throws Exception {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		List<MstPensionChecklist> lLstChecklistVO = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append("from \n");
			lSBQuery.append("MstPensionChecklist ORDER BY docId \n");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lLstChecklistVO = lHibQry.list();
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstChecklistVO;
	}

	public void deletePnsrCheckList(String lStrPnsrCode) throws Exception {

		Query lHibQry = null;
		StringBuffer lSBQuery = null;
		try {
			ghibSession = getSession();
			lSBQuery = new StringBuffer();
			lSBQuery.append(" delete from TrnPensionerChecklistDtls pcd");
			lSBQuery.append(" where pcd.pensionerCode = :lStrPnsrCode");
			lHibQry = ghibSession.createQuery(lSBQuery.toString());
			lHibQry.setParameter("lStrPnsrCode", lStrPnsrCode);
			lHibQry.executeUpdate();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error is :" + e, e);
		}
	}

	public Map<String, List<String>> getSelectedDocsName(String[] lArrPnsrCode) throws Exception {

		Map<String, List<String>> lMapCheckLstDtls = new HashMap<String, List<String>>();
		String lStrPnsrCode = null;
		List<String> lLstDocDesc = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();

			ghibSession = getSession();

			lSBQuery.append("select pcd.pensionerCode,mpc.docDesc \n");
			lSBQuery.append("from \n");
			lSBQuery.append("MstPensionChecklist mpc, \n");
			lSBQuery.append("TrnPensionerChecklistDtls pcd \n");
			lSBQuery.append("where  \n");
			lSBQuery.append("mpc.docId = pcd.docId  \n");
			lSBQuery.append("and pcd.pensionerCode in (:lArrPnsrCode) \n");

			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("lArrPnsrCode", lArrPnsrCode);

			List<Object[]> lLstVO = lQuery.list();
			for (Object[] lArrObj : lLstVO) {
				if (lArrObj[0] != null) {
					lLstDocDesc = lMapCheckLstDtls.get(lArrObj[0].toString());
					if (lLstDocDesc != null) {
						lLstDocDesc.add((lArrObj[1] != null) ? lArrObj[1].toString() : "");
					} else {
						lLstDocDesc = new ArrayList<String>();
						lLstDocDesc.add((lArrObj[1] != null) ? lArrObj[1].toString() : "");
					}
					lMapCheckLstDtls.put(lArrObj[0].toString(), lLstDocDesc);
				}
			}
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lMapCheckLstDtls;
	}
	
	public List<TrnPensionerChecklistDtls> getSelectedCheckList(String lStrPnsrCode) throws Exception 
	{
		List lLstTrnPensionerChecklistDtls = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();

			ghibSession = getSession();

			lSBQuery.append("FROM TrnPensionerChecklistDtls ");
			lSBQuery.append("WHERE pensionerCode = :pensionerCode ");
			lSBQuery.append("ORDER BY docId");
			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("pensionerCode",lStrPnsrCode);
			lLstTrnPensionerChecklistDtls = lQuery.list();
		}catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		
		return lLstTrnPensionerChecklistDtls;
	}
	
	public List<String> getPensionerCode(String[] lLstPensionerCode,Integer lIntLocId, String lStrLetterType) throws Exception
	{
		List lLstPensionerCodeDtls = null;
		try{
			StringBuilder lSBQuery = new StringBuilder();

			ghibSession = getSession();

			lSBQuery.append("Select pensionerCode ");
			lSBQuery.append("FROM TrnFinYearLetterNo ");
			lSBQuery.append("WHERE pensionerCode in (:pensionerCode) AND locationCode = :locationCode ");
			lSBQuery.append("AND letterType = :letterType");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameterList("pensionerCode",lLstPensionerCode);
			lQuery.setParameter("locationCode",lIntLocId);
			lQuery.setParameter("letterType", lStrLetterType);
			lLstPensionerCodeDtls = lQuery.list();
			
		}catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLstPensionerCodeDtls;
	}	
	
	public Long getLetterNo(String lStrPensionerCode, Integer lIntLocId, String lStrLetterType) throws Exception
	{
		Long lLngLetterNo = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();

			ghibSession = getSession();

			lSBQuery.append("SELECT letterNo FROM TrnFinYearLetterNo ");
			lSBQuery.append("WHERE pensionerCode =:pensionerCode AND locationCode = :locationCode ");
			lSBQuery.append("AND letterType = :letterType");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("pensionerCode",lStrPensionerCode);
			lQuery.setParameter("locationCode",lIntLocId);
			lQuery.setParameter("letterType",lStrLetterType);
			if(lQuery.list().size() > 0){
				lLngLetterNo = (Long) lQuery.list().get(0);
			}
		}catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		return lLngLetterNo;
	}	
	
	public String getAuthorityName(Long lLngUserId) throws Exception
	{
		String lStrAuthName = "";
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
	
			ghibSession = getSession();
	
			lSBQuery.append("SELECT OEM.empFname || ' ' || OEM.empMname || ' ' || OEM.empLname ");
			lSBQuery.append("FROM OrgUserpostRlt OUPR,OrgEmpMst OEM ");
			lSBQuery.append("WHERE OUPR.empPostId = :userID AND OUPR.orgUserMst.userId = OEM.empId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("userID",lLngUserId);
			if(lQuery.list().size() > 0){
				lStrAuthName = (String) lQuery.list().get(0);
			}
		}catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}	
		
		return lStrAuthName;
	}
	
	public List getAllDistrictName(String lStrStateCode) throws Exception
	{
		List lLstDistrict = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			
			ghibSession = getSession();
	
			lSBQuery.append("SELECT cdm.districtName, cdm.districtCode, cdm.cmnLanguageMst.langId ");
			lSBQuery.append("FROM CmnDistrictMst cdm ");
			lSBQuery.append("WHERE cdm.cmnStateMst.stateCode = :stateCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("stateCode",lStrStateCode);
			lLstDistrict = lQuery.list();
		}catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		
		return lLstDistrict;
	}
	
	public List getAllStateName() throws Exception
	{
		List lLstState = null;
		
		try{
			StringBuilder lSBQuery = new StringBuilder();
			
			ghibSession = getSession();
	
			lSBQuery.append("SELECT stateName,stateCode,cmnLanguageMst.langId ");
			lSBQuery.append("FROM CmnStateMst ");
			lSBQuery.append("WHERE stateCode = :stateCode");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("stateCode","15");
			lLstState = lQuery.list();
		}catch (Exception e) {
			logger.error(" Error is : " + e, e);
			throw (e);
		}
		
		return lLstState;
	}
}
