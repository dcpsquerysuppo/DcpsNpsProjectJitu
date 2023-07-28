package com.tcs.sgv.common.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.constant.DBConstants;
import com.tcs.sgv.common.valueobject.EmpPostVO;
import com.tcs.sgv.common.valueobject.MstAuditorConfig;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

/**
 * 
 * @author 206819
 *
 */
public class PostConfigurationDAOImpl extends GenericDaoHibernateImpl<MstAuditorConfig,Integer> implements PostConfigurationDAO {
	
	Log logger = LogFactory.getLog(PostConfigurationDAOImpl.class);
	
	public PostConfigurationDAOImpl(Class<MstAuditorConfig> type, SessionFactory sessionFactory){
        super(type);
        setSessionFactory(sessionFactory);
	}
	
	/**
	 * This method get the employee bound to task on basis of value of cardexNo, billType, majorHead, goNgo  
	 * @param office_id Office location Id
	 * @param branch_id Branch Id 
	 * @param mpArg Contains query criteria(s) (cardexNo, billType, majorHead, goNgo)
	 * @return EmpPostVO
	 */
	public EmpPostVO getSelectedEmp(String officeCode, String branchCode, Map mpArg, Long lLngLangId)
	{
		EmpPostVO empPostVO = null;
	
		Session hibSession = getSession();
		try 
		{
			String cardexNo = mpArg.get("cardexNo")!=null?mpArg.get("cardexNo").toString():null;
			String billType = mpArg.get("billType")!=null?mpArg.get("billType").toString():null;
			String majorHead =mpArg.get("majorHead")!=null?mpArg.get("majorHead").toString():null;
			String goNgo = mpArg.get("goNgo")!=null?mpArg.get("goNgo").toString():null;
			String deptCode = mpArg.get("deptCode")!=null?mpArg.get("deptCode").toString():null;
			
			Query lQuery = hibSession.createQuery("SELECT MAC.mbgcFlag,MAC.majorHead,MAC.billTypeGtrCode,MAC.goNgo,MAC.cardexNo,"
					+" MAC.auditorPostId, E.empPrefix,E.empFname,E.empMname,E.empLname,E.empId,UP.orgUserMst.userId,"
					+" UP.orgPostMstByPostId.postId FROM MstAuditorConfig MAC, OrgEmpMst E, OrgUserpostRlt UP WHERE "
					+" MAC.auditorPostId=UP.orgPostMstByPostId.postId AND UP.orgUserMst.userId=E.orgUserMst.userId AND "
					+" E.cmnLanguageMst.langId=:langId AND UP.activateFlag=:activateFlag AND MAC.locationCode=:officeCode "
					+" AND MAC.activateFlag=:activateFlag AND UP.orgUserMst.userId<>:vacantUserId"
					+" AND MAC.branchCode=:branchCode AND MAC.mbgcFlag is not null ORDER BY MAC.priority desc");
			
			lQuery.setParameter("langId", lLngLangId);
			lQuery.setParameter("branchCode", branchCode);
			lQuery.setParameter("officeCode", officeCode);
			lQuery.setParameter("activateFlag", Long.valueOf("1"));
			lQuery.setString("vacantUserId", DBConstants.VACANT_USER_ID);
			
			List lListQuery = lQuery.list();
			if(lListQuery!=null && lListQuery.size()>0)
			{
				Iterator lIterQuery = lListQuery.iterator();
				String lStrMBGCFlag=null;
				String lStrMajorHead = null;
				String lStrBillType = null;
				String lStrGoNgo = null;
				String lStrCardexNo = null;
				Object[] lObjTuple = null;
				boolean lBDataFound = false;
				while(lIterQuery.hasNext())
				{
					lObjTuple = (Object[])lIterQuery.next();
					lStrMBGCFlag = lObjTuple[0]!=null?lObjTuple[0].toString():null;
					lStrMajorHead = lObjTuple[1]!=null?lObjTuple[1].toString():null;
					lStrBillType = lObjTuple[2]!=null?lObjTuple[2].toString():null;
					lStrGoNgo = lObjTuple[3]!=null?lObjTuple[3].toString():null;
					lStrCardexNo = lObjTuple[4]!=null?lObjTuple[4].toString():null;
					
					if(("MBGC".equals(lStrMBGCFlag)) && lStrMajorHead.equals(majorHead) && lStrBillType.equals(billType) && lStrGoNgo.equals(goNgo) && lStrCardexNo.equals(cardexNo))
							lBDataFound=true;
					else if(("MBG".equals(lStrMBGCFlag)) && lStrMajorHead.equals(majorHead) && lStrBillType.equals(billType) && lStrGoNgo.equals(goNgo))
						lBDataFound=true;
					else if(("MGC".equals(lStrMBGCFlag)) && lStrMajorHead.equals(majorHead) && lStrGoNgo.equals(goNgo) && lStrCardexNo.equals(cardexNo))
						lBDataFound=true;
					else if(("MBC".equals(lStrMBGCFlag)) && lStrMajorHead.equals(majorHead) && lStrBillType.equals(billType) && lStrCardexNo.equals(cardexNo))
						lBDataFound=true;
					else if(("BGC".equals(lStrMBGCFlag)) && lStrBillType.equals(billType) && lStrGoNgo.equals(goNgo) && lStrCardexNo.equals(cardexNo))
						lBDataFound=true;
										
					else if(("MB".equals(lStrMBGCFlag)) && lStrMajorHead.equals(majorHead) && lStrBillType.equals(billType))
						lBDataFound=true;
					else if(("MG".equals(lStrMBGCFlag)) && lStrMajorHead.equals(majorHead) && lStrGoNgo.equals(goNgo))
						lBDataFound=true;
					else if(("MC".equals(lStrMBGCFlag)) && lStrMajorHead.equals(majorHead) && lStrCardexNo.equals(cardexNo))
						lBDataFound=true;
					else if(("BG".equals(lStrMBGCFlag)) && lStrBillType.equals(billType) && lStrGoNgo.equals(goNgo))
						lBDataFound=true;
					else if(("BC".equals(lStrMBGCFlag)) && lStrBillType.equals(billType) && lStrCardexNo.equals(cardexNo))
						lBDataFound=true;
					else if(("GC".equals(lStrMBGCFlag)) && lStrGoNgo.equals(goNgo) && lStrCardexNo.equals(cardexNo))
						lBDataFound=true;
					
					else if(("M".equals(lStrMBGCFlag)) && lStrMajorHead.equals(majorHead))
						lBDataFound=true;
					else if(("B".equals(lStrMBGCFlag)) && lStrBillType.equals(billType))
						lBDataFound=true;
					else if(("G".equals(lStrMBGCFlag)) && lStrGoNgo.equals(goNgo))
						lBDataFound=true;
					else if(("C".equals(lStrMBGCFlag)) && lStrCardexNo.equals(cardexNo))
						lBDataFound=true;
					
					if(lBDataFound)
					{
						empPostVO = new EmpPostVO();
						empPostVO.setEmpId((Long) lObjTuple[10]);					
						empPostVO.setUserId((Long) lObjTuple[11]);
						empPostVO.setPostId((Long) lObjTuple[12]);
						empPostVO.setEmpPrefix(lObjTuple[6].toString());
						empPostVO.setEmpFname(lObjTuple[7].toString());
						empPostVO.setEmpMname(lObjTuple[8].toString());
						empPostVO.setEmpLname(lObjTuple[9].toString());
						empPostVO.setFullName(lObjTuple[6].toString() + " " + lObjTuple[7].toString() + " "+ lObjTuple[8].toString() + " " + lObjTuple[9].toString());
						lBDataFound=false;
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error("Exception occured in PostConfigurationDAOImpl.getSelectedEmp #\n"+e);
		}
		return empPostVO;
	}
	/**
	 * This method get the employee bound to task on basis of cardexNo, billType, majorHead, goNgo
	 * @param branch_id Branch Id 
	 * @param office_id Office Id
	 * @return List
	 */
	public List getEmpsList(String office_code, String branch_code, Long lLngLangId) {
		List dataList = new ArrayList();
		
		try 
		{			
			 String lStrSqlQuery=" SELECT DISTINCT E.empId, E.empPrefix,E.empFname,E.empMname,E.empLname,UP.orgUserMst.userId,"
			 					+" UP.orgPostMstByPostId.postId FROM OrgEmpMst E, OrgUserpostRlt UP, MstAuditorConfig MAC "
			 					+" WHERE MAC.auditorPostId = UP.orgPostMstByPostId.postId "
			 					+" AND UP.orgUserMst.userId = E.orgUserMst.userId AND UP.activateFlag = :activateFlag "
			 					+" AND E.cmnLanguageMst.langId = :langId AND MAC.locationCode = :officeCode AND "
			 					+" MAC.branchCode = :branchCode AND MAC.activateFlag=:activateFlag "
			 					+" AND UP.orgUserMst.userId<>:vacantUserId "
			 					+" ORDER BY E.empFname,E.empMname,E.empLname";
			 		
			Session hibSession = getSession();
			Query sqlQuery = hibSession.createQuery(lStrSqlQuery);
			sqlQuery.setString("officeCode", office_code);
			sqlQuery.setString("branchCode", branch_code);
			sqlQuery.setParameter("langId", lLngLangId);
			sqlQuery.setShort("activateFlag", Short.valueOf("1"));
			sqlQuery.setString("vacantUserId", DBConstants.VACANT_USER_ID);
			
			List resList = sqlQuery.list();
			if (resList!=null) {
				Iterator it = resList.iterator();
				while(it.hasNext()) {
					Object[] objArr = (Object[]) it.next();
					EmpPostVO empPostVO = new EmpPostVO();
					empPostVO.setEmpId(Long.parseLong(objArr[0].toString()));
					empPostVO.setEmpPrefix(objArr[1].toString());
					empPostVO.setEmpFname(objArr[2].toString());
					empPostVO.setEmpMname(objArr[3].toString());
					empPostVO.setEmpLname(objArr[4].toString());
					empPostVO.setFullName(objArr[1].toString() + " " + objArr[2].toString() + " "+ objArr[3].toString() +" " + objArr[4].toString());
					empPostVO.setUserId(Long.parseLong(objArr[5].toString()));
					empPostVO.setPostId(Long.parseLong(objArr[6].toString()));
					dataList.add(empPostVO);					
				}
			}				
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured in PostConfigurationDAOImpl.getEmpsList #\n"+e);
		}
		
		return dataList;
	}
	public Map getEmpPostVOList(String locationCode, String branchCode, Long langId)
	{
		Map<String,Object> map = new HashMap<String,Object>();
		List<EmpPostVO> dataList = new ArrayList<EmpPostVO>();
		Map uniquePostMap = new HashMap();
		Session hibSession = getSession();
		EmpPostVO empPostVO = null;
		StringBuffer hibQuery = new StringBuffer();
		try
		{
			hibQuery.append("select MAC.majorHead,  MAC.auditorPostId, em.empPrefix, em.empFname, em.empMname, em.empLname " +
			"   from MstAuditorConfig MAC , OrgEmpMst em ,OrgUserpostRlt up ,OrgUserMst um " +
			"	where MAC.locationCode = :locCode " +
			"   and MAC.branchCode = :branchCode" +
			"   and MAC.activateFlag = :activeFlag " +
			"	and MAC.auditorPostId= up.orgPostMstByPostId.postId " +
			"	and  um.userId = up.orgUserMst.userId " +
			"	and em.orgUserMst.userId = um.userId " +
			"	and up.activateFlag = :activeFlag " +
			"	and em.cmnLanguageMst.langId = :langId group by MAC.majorHead,MAC.auditorPostId ");

			Query sqlQuery = hibSession.createQuery(hibQuery.toString());
			sqlQuery.setCacheable(true);
			sqlQuery.setCacheRegion("sampleCache4");
			
			sqlQuery.setString("locCode", locationCode);
			sqlQuery.setString("branchCode", branchCode);
			sqlQuery.setLong("langId", langId);
			sqlQuery.setShort("activeFlag",Short.valueOf("1"));
			
			List resList = sqlQuery.list();

			for (Object row : resList) 
			{
				Object[] cols = (Object[])row;
				empPostVO = new EmpPostVO();
				empPostVO.setPostId(Long.parseLong(cols[1].toString()));
				empPostVO.setEmpPrefix(cols[2].toString());
				empPostVO.setEmpFname(cols[3].toString());
				empPostVO.setEmpMname(cols[4].toString());
				empPostVO.setEmpLname(cols[5].toString());
				empPostVO.setFullName(cols[2].toString() + " " + cols[3].toString() + " "+ cols[4].toString() + " " + cols[5].toString());
				if(!uniquePostMap.containsKey(cols[1]))
					{
					uniquePostMap.put(cols[1],empPostVO);
					}
				if(cols[0] != null)
				{
					map.put(cols[0].toString(), empPostVO);
				}
			}
			for (Object empPostVO1 : uniquePostMap.keySet()) 
			{
				dataList.add((EmpPostVO)uniquePostMap.get(empPostVO1));
			}
			map.put("empList", dataList);
		}

		catch(Exception ex)
		{
			logger.error("Exception occured in ReceiptDAOImpl.getEmpPostVOList # \n"+ex,ex);
		}
		return map;
	}
	
	/**
     * Returns the Auditor List Branch wise in Inward Online Bill jsp at Counter Level..
     * 
     * @param String, String
     * 				 : officeCode, branchCode
     *            
     * @return List
     */
    public List getSelectedEmpOBPM(String officeCode, String branchCode) throws Exception
	{
		StringBuilder lsb = new StringBuilder();
		List lLstRsltSet = null;
		
		try
		{
			lsb.append(" SELECT cas.auditorPostId, cas.majorHead ");
			lsb.append(" FROM  MstAuditorConfig cas");
			lsb.append(" WHERE cas.activateFlag = :activateFlag AND");
			lsb.append(" cas.branchCode=:branchCode AND cas.locationCode = :officeCode");
					
			
			Query lQuery = getSession().createQuery(lsb.toString());
	
	        lQuery.setParameter("branchCode", branchCode);
	        lQuery.setParameter("officeCode", officeCode);
	        lQuery.setParameter("activateFlag", true);
	
	        lLstRsltSet = lQuery.list();
		}
		catch (Exception e)
        {
            logger.error("Error is : " + e, e);
            throw (e);
        }
		return lLstRsltSet; 
		
	}
    
    /**
     * Get post id on the base of the major head and location code
     * 
     * @param String lStrMajorHead, String lStrLocationCode
     * @return long lLngPostId
     * @author 218580
     */
    public long getChallanPostId(String lStrMajorHead, String lStrLocationCode) throws Exception
    {
        List resList = null;
        long lLngPostId = 0;
        Session hibSession = getSession();

        try
        {
            StringBuilder lSBQuery = new StringBuilder();
            
            lSBQuery.append("SELECT MA.auditorPostId FROM MstAuditorConfig MA ");
            lSBQuery.append("WHERE MA.branchCode = :branchCode ");
            lSBQuery.append("AND MA.locationCode = :locationCode ");
            lSBQuery.append("AND MA.majorHead = :majorHead ");
            
            Query lQuery = hibSession.createQuery(lSBQuery.toString());
            lQuery.setString("branchCode", DBConstants.BRCH_ID_PDPLA);
            lQuery.setString("locationCode", lStrLocationCode);
            lQuery.setString("majorHead", lStrMajorHead);
            resList = lQuery.list();
            
            if (resList != null && !resList.isEmpty())
            {
                lLngPostId = (Long) resList.get(0);
            }
        }
        catch (Exception e)
        {
        	logger.error("Error is : " + e, e);
            throw e;
        }

        return lLngPostId;
    }


    /**
     * Get post id on the base of location code and acc No
     * 
     * @param String lStrAccNo, String lStrLocationCode
     * @return Map lMapResMap
     * @author 218580
     */
    public Map getChqPostId(String lStrAccNo, String lStrLocationCode,Integer lIntFinYrId) throws Exception
    {
        List lLstResList = null;
        Map<String, Object> lMapResMap = null;
        Session hibSession = getSession();
        try
        {
            lMapResMap = new HashMap<String, Object>();
            
            StringBuilder lSBQueryCfg = new StringBuilder();
            
            lSBQueryCfg.append("SELECT MA.auditorPostId FROM MstAuditorConfig MA,MstPdAccount pd ");
            lSBQueryCfg.append("WHERE MA.branchCode = :branchCode ");
            lSBQueryCfg.append("AND MA.locationCode = :locationCode ");
            lSBQueryCfg.append("AND MA.majorHead = pd.pdMjrhd AND pd.accountNo = :accountNo  and pd.finYearId = :finYrId ");
            lSBQueryCfg.append("AND pd.locationCode = MA.locationCode) ");   
            
            Query lQuery = hibSession.createQuery(lSBQueryCfg.toString());
            lQuery.setString("branchCode", DBConstants.BRCH_ID_PDPLA);
            lQuery.setString("locationCode", lStrLocationCode);
            lQuery.setString("accountNo", lStrAccNo);
            lQuery.setLong("finYrId", lIntFinYrId.longValue());
            lLstResList = lQuery.list();
            
            if(lLstResList != null && !lLstResList.isEmpty())
            {
                lMapResMap.put("postId", lLstResList.get(0).toString());
            }
        }
        catch (Exception e)
        {
        	logger.error("Error is : " + e, e);
            throw e;
        }
        return lMapResMap;
    }
    
    /**
	 * get Forward User List
	 * 
	 * @param String lStrLocCode, long lLangId, long lPostId, long lUserId
	 * @return List lLstReturnList
	 * @author 231224
	 */
	public List getForwardUser(String lStrLocCode, long lLangId, long lPostId, long lUserId) throws Exception
	{	
		List lLstReturnList = null;
		Session hibSession = getSession();
		
		try
		{
            StringBuffer lStrBffQuery = new StringBuffer();
            
            lStrBffQuery.append("SELECT DISTINCT UP.orgPostMstByPostId.postId, ");
            lStrBffQuery.append(" EM.empPrefix, EM.empFname, EM.empMname, EM.empLname ");
            lStrBffQuery.append("FROM OrgEmpMst EM , OrgUserMst UM, OrgUserpostRlt UP, MstAuditorConfig MA, ");
            lStrBffQuery.append("MstPdMjrhd  M ");
            lStrBffQuery.append("WHERE MA.branchCode = :branchCode AND MA.locationCode = :locationCode ");
            lStrBffQuery.append("AND EM.orgUserMst.userId = UM.userId ");
            lStrBffQuery.append("AND UP.orgPostMstByPostId.postId = MA.auditorPostId ");
            lStrBffQuery.append("AND EM.orgUserMst.userId = UP.orgUserMst.userId ");
            lStrBffQuery.append("AND MA.majorHead = M.majorHd AND M.pdMajorhd = 1 ");
            lStrBffQuery.append("AND EM.orgUserMst.userId != :userId ");
	
            Query lQuery = hibSession.createQuery(lStrBffQuery.toString());
            lQuery.setString("branchCode", DBConstants.BRCH_ID_PDPLA);
            lQuery.setString("locationCode", lStrLocCode);
            lQuery.setLong("userId", lUserId);
			List lLstResList = lQuery.list();
            
			if (lLstResList!=null && !lLstResList.isEmpty()) 
			{
				lLstReturnList = new ArrayList();
                ComboValuesVO lObjComboVal=null;
				for(int lIntCtr=0; lIntCtr<lLstResList.size(); lIntCtr++)
				{
					Object[] lArrData = (Object[])lLstResList.get(lIntCtr);
					if(lArrData != null)
					{
						lObjComboVal = new ComboValuesVO(); 
						String lStrUserId = lArrData[0].toString();
						String lStrUserName = lArrData[1].toString() + ". " + lArrData[2].toString() + ". "+ lArrData[3].toString() + ". "+ lArrData[4].toString();
						lObjComboVal.setId(lStrUserId);
						lObjComboVal.setDesc(lStrUserName);            	
			    		lLstReturnList.add(lObjComboVal);
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error is : " + e,e);
			throw e;
		}
		return lLstReturnList;
	}
	public String getBranchCodeFromPostId(Long lLngPostId,Long lLngLangId) throws Exception{
		Session hibSession = getSession();
		StringBuilder lSBQuery = new StringBuilder();
		String lStrBranchCode = null;
		lSBQuery.append("SELECT cbm.branchCode FROM CmnBranchMst cbm,OrgPostDetailsRlt opdr" 
			+"	WHERE opdr.orgPostMst.postId=:postId AND opdr.cmnBranchMst.branchId=cbm.branchId and " +
				"cbm.cmnLanguageMst.langId = :langId");
		
		Query lQuery = hibSession.createQuery(lSBQuery.toString());
        lQuery.setLong("langId", lLngLangId);
        lQuery.setLong("postId", lLngPostId);
		List lLstResList = lQuery.list();
		if(lLstResList!=null && lLstResList.size()>0){
			lStrBranchCode = lLstResList.get(0).toString();
		}
		return lStrBranchCode;
	}
	public List getBranchVoFromPostId(Long lLngPostId,Long lLngLangId) throws Exception{
		Session hibSession = getSession();
		StringBuilder lSBQuery = new StringBuilder();
		String lStrBranchCode = null;
		lSBQuery.append("SELECT cbm FROM CmnBranchMst cbm,OrgPostDetailsRlt opdr" 
			+"	WHERE opdr.orgPostMst.postId=:postId AND opdr.cmnBranchMst.branchId=cbm.branchId and " +
				"cbm.cmnLanguageMst.langId = :langId");
		
		Query lQuery = hibSession.createQuery(lSBQuery.toString());
        lQuery.setLong("langId", lLngLangId);
        lQuery.setLong("postId", lLngPostId);
		List lLstResList = lQuery.list();		
		return lLstResList;
	}
}
