package com.tcs.sgv.common.dao;

import com.tcs.sgv.common.valueobject.CmnLookupMst;
import com.tcs.sgv.common.valueobject.TrnBillRegister;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.ess.valueobject.OrgUserMst;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class IFMSCommonDAOImpl
  extends GenericDaoHibernateImpl
  implements IFMSCommonDAO
{
  private Session hibSession = null;
  
  public IFMSCommonDAOImpl(SessionFactory sessionFactory)
  {
    super(TrnBillRegister.class);
    setSessionFactory(sessionFactory);
  }
  
  public IFMSCommonDAOImpl(Session hibSession)
  {
    super(TrnBillRegister.class);
    this.hibSession = hibSession;
  }
  
  public IFMSCommonDAOImpl(Class type, SessionFactory sessionFactory)
  {
    super(type);
    setSessionFactory(sessionFactory);
  }
  
  private Session getDBSession()
  {
    if (this.hibSession != null) {
      return this.hibSession;
    }
    return getSession();
  }
  
  public List findByNamedQuery(String queryName)
    throws Exception
  {
    return findByNamedQuery(queryName, null);
  }
  
  public List findByNamedQuery(String queryName, Map<String, Object> options)
    throws Exception
  {
    try
    {
      Query query = prepareNamedQuery(queryName, options);
      
      return query.list();
    }
    catch (Exception e)
    {
      this.logger.error("Error in executing query '" + queryName + "' : ", e);
      throw e;
    }
  }
  
  public int deleteOrUpdateByNamedQuery(String queryName)
    throws Exception
  {
    return deleteOrUpdateByNamedQuery(queryName, null);
  }
  
  public int deleteOrUpdateByNamedQuery(String queryName, Map<String, Object> options)
    throws Exception
  {
    try
    {
      return prepareNamedQuery(queryName, options).executeUpdate();
    }
    catch (Exception e)
    {
      this.logger.error("Error in executing query '" + queryName + "' : ", e);
      throw e;
    }
  }
  
  private Query prepareNamedQuery(String queryName, Map<String, Object> options)
    throws Exception
  {
    Session session = getDBSession();
    Query query = session.getNamedQuery(queryName);
    try
    {
      String[] arrayOfString;
      int j = (arrayOfString = query.getNamedParameters()).length;
      for (int i = 0; i < j; i++)
      {
        String paramName = arrayOfString[i];
        if (options != null)
        {
          Object value = options.get(paramName);
          if (value == null) {
            throw new Exception("\n==========\nOne or more options are missing required for query parameters. \nQuery Name : " + 
              queryName + " \nMissing Parameter : " + paramName + "\n==========");
          }
          if ((value instanceof Collection)) {
            query.setParameterList(paramName, (Collection)value);
          } else {
            query.setParameter(paramName, value);
          }
        }
        else
        {
          throw new Exception("One or more options are missing required for query parameters.");
        }
      }
    }
    catch (Exception e)
    {
      throw e;
    }
    return query;
  }
  
  public Map<String, List<CmnLookupMst>> getPartialCommonLookupMstVo(List lLstLookUpNames, Long lLangId)
    throws Exception
  {
    Session ghibSession = getSession();
    Query lQuery = null;
    List lLstLookupId = new ArrayList();
    Map lMaplookupIdAndName = new HashMap();
    Map<String, List<CmnLookupMst>> FinalMap = new HashMap();
    try
    {
      if (!lLstLookUpNames.isEmpty())
      {
        StringBuffer lSBParentLookupDtls = new StringBuffer();
        lSBParentLookupDtls.append(" SELECT lookupName,lookupId from CmnLookupMst where lookupName in (:lookupNameList)  and cmnLanguageMst.langId=:langId order by lookupId ");
        
        lQuery = ghibSession.createQuery(lSBParentLookupDtls.toString());
        lQuery.setCacheable(true).setCacheRegion("ecache_lookup");
        lQuery.setParameterList("lookupNameList", lLstLookUpNames);
        lQuery.setParameter("langId", lLangId);
        List lLstResTemp = lQuery.list();
        Object[] tuple = (Object[])null;
        if (!lLstResTemp.isEmpty()) {
          for (int i = 0; i < lLstResTemp.size(); i++)
          {
            tuple = (Object[])lLstResTemp.get(i);
            lLstLookupId.add(new Long(tuple[1].toString()));
            lMaplookupIdAndName.put(tuple[1].toString(), tuple[0].toString());
          }
        }
        if (!lLstLookupId.isEmpty())
        {
          StringBuffer lSBQuery = new StringBuffer();
          lSBQuery.append("SELECT lookupName,lookupDesc,lookupShortName,parentLookupId from CmnLookupMst where  parentLookupId in(:lLstLookupId) order by parentLookupId,orderNo ");
          
          lQuery = ghibSession.createQuery(lSBQuery.toString());
          lQuery.setCacheable(true).setCacheRegion("ecache_lookup");
          lQuery.setParameterList("lLstLookupId", lLstLookupId);
          lLstResTemp = lQuery.list();
          CmnLookupMst lObjCmnLookupMst = null;
          Long lLngTempLookupId = Long.valueOf(0L);
          List<CmnLookupMst> lObjTempVo = null;
          if (!lLstResTemp.isEmpty())
          {
            for (int i = 0; i < lLstResTemp.size(); i++)
            {
              tuple = (Object[])lLstResTemp.get(i);
              lObjCmnLookupMst = new CmnLookupMst();
              lObjCmnLookupMst.setLookupName(tuple[0].toString());
              lObjCmnLookupMst.setLookupDesc(tuple[1].toString());
              lObjCmnLookupMst.setLookupShortName(tuple[2].toString());
              if (!lLngTempLookupId.equals(new Long(tuple[3].toString())))
              {
                if (!lLngTempLookupId.equals(Long.valueOf(0L))) {
                  FinalMap.put((String)lMaplookupIdAndName.get(lLngTempLookupId.toString()), lObjTempVo);
                }
                lLngTempLookupId = new Long(tuple[3].toString());
                lObjTempVo = new ArrayList();
              }
              lObjTempVo.add(lObjCmnLookupMst);
            }
            if (!lLngTempLookupId.equals(Long.valueOf(0L))) {
              FinalMap.put((String)lMaplookupIdAndName.get(lLngTempLookupId.toString()), lObjTempVo);
            }
          }
        }
      }
    }
    catch (Exception e)
    {
      this.logger.error("Error in  : " + e, e);
      throw e;
    }
    return FinalMap;
  }
  
  public List<OrgUserMst> getUserListOfLoggedinLocation(String userName, Long locId, long langId)
  {
    List<OrgUserMst> userList = new ArrayList();
    
    String query = "select orgUserMst  from CmnLocationMst location,  OrgUserMst orgUserMst,  OrgUserpostRlt orgUserpostRlts  where orgUserMst.userName like :username   and orgUserMst.userId = orgUserpostRlts.orgUserMst.userId  and orgUserpostRlts.activateFlag = 1 and orgUserpostRlts.orgPostMstByPostId.activateFlag = 1 and location.locationCode = orgUserpostRlts.orgPostMstByPostId.locationCode  and (location.locId = :locId OR location.parentLocId = :locId) and location.activateFlag = 1  and location.cmnLanguageMst.langId =:langid ";
    
    Session hibSession = getSession();
    Query sqlQuery = hibSession.createQuery(query);
    sqlQuery.setParameter("username", userName + "%");
    sqlQuery.setParameter("locId", locId);
    sqlQuery.setParameter("langid", Long.valueOf(langId));
    
    userList = sqlQuery.list();
    
    return userList;
  }
  
  public int findFilesCount(long locId, String roleId)
  {
    Session session = getSession();
    List list = null;
    int count = 0;
    StringBuffer sb = new StringBuffer();
    this.logger.info("---- retrieve usertype------");
    if (roleId.equalsIgnoreCase("700003"))
    {
      sb.append("SELECT  count(1)  FROM ifms.nsdl_bh_dtls bh ");
      sb.append(" where bh.transaction_id is null   ");
      
      sb.append(" and substr(bh.FILE_NAME,1,2) =substr(" + locId + ",1,2) and bh.status <>-1 ");
      this.logger.info("---- retrieve usertype---" + sb.toString());
      Query query = session.createSQLQuery(sb.toString());
      
      list = query.list();
      if ((list != null) && (list.size() > 0)) {
        count = Integer.parseInt(list.get(0).toString());
      }
    }
    return count;
  }
  
  public int findTreasuryCount(long locId, String roleId)
  {
    Session session = getSession();
    List list = null;
    int count = 0;
    
   
    
    
    StringBuffer sb = new StringBuffer();
    this.logger.info("---- retrieve usertype------");
    if (roleId.equalsIgnoreCase("700003"))
    {
      sb.append("SELECT  count(1)  FROM ifms.nsdl_bh_dtls bh");
      sb.append("  where bh.TRANSACTION_ID is not null  and bh.FILE_NAME not in (SELECT FILE_NAME FROM ifms.NSDL_BILL_DTLS )   ");
      
      sb.append(" and substr(bh.FILE_NAME,1,2) =substr(" + locId + ",1,2) and bh.status <> -1 ");
      
      this.logger.info("---- retrieve usertype---" + sb.toString());
      Query query = session.createSQLQuery(sb.toString());
      
      list = query.list();
      if ((list != null) && (list.size() > 0)) {
        count = Integer.parseInt(list.get(0).toString());
      }
    }
    return count;
  }
  
  public String getRoleID(String roleid)
  {
    Session session = getSession();
    System.out.println("locId::" + roleid);
    List lLstRADetails = new ArrayList();
    lLstRADetails = null;
    String rollid = null;
    try
    {
      StringBuilder lSBQuery = new StringBuilder();
      lSBQuery.append("select role_id from acl_postrole_rlt where post_id=:roleid");
      Query lQuery1 = session.createSQLQuery(lSBQuery.toString());
      this.logger.info("query is ***********" + lQuery1.toString());
      lQuery1.setParameter("roleid", roleid);
      this.logger.info("roleid: " + roleid);
      lLstRADetails = lQuery1.list();
      rollid = lLstRADetails.get(0).toString();
      this.logger.info("getLoadEmpTickets slLstRADetails::::::::" + lLstRADetails.size());
    }
    catch (Exception e)
    {
      this.logger.error(" Error is : " + e, e);
      try
      {
        throw e;
      }
      catch (Exception e1)
      {
        e1.printStackTrace();
      }
    }
    return rollid;
  }

public int findTreasuryFilesCount(long locId, String roleId) {
	 Session session = getSession();
	    List list = null;
	    int count = 0;
	
	    StringBuffer sb = new StringBuffer();
	    this.logger.info("---- retrieve usertype------");
	    if (roleId.equalsIgnoreCase("700003"))
	    {
	      sb.append(" SELECT  count(1) FROM ifms.nsdl_bh_dtls bh");
	      sb.append("  where bh.TRANSACTION_ID is not null  and file_status=11 and ((bh.FILE_NAME not in (SELECT FILE_NAME FROM ifms.NSDL_BILL_DTLS )) OR bh.FILE_NAME in (SELECT FILE_NAME FROM ifms.NSDL_BILL_DTLS WHERE BILL_STATUS<>3)) ");
	      sb.append(" and substr(bh.FILE_NAME,1,2) =substr(" + locId + ",1,2) and bh.status <> -1 ");
	      sb.append(" and cast ((bh_date)-VARCHAR_FORMAT(date(sysdate),'ddmmyyyy') as INTEGER) in ('-8000000') ");
	      
	      this.logger.info("---- retrieve usertype---" + sb.toString());
	      Query query = session.createSQLQuery(sb.toString());
	      
	      list = query.list();
	      if ((list != null) && (list.size() > 0)) {
	        count = Integer.parseInt(list.get(0).toString());
	      }
	    }
	    return count;
  }

public int filesNotSendTO2DaysAlert(long locId, String roleId) {
	 Session session = getSession();
	    List list = null;
	    int count = 0;
	
	    StringBuffer sb = new StringBuffer();
	    this.logger.info("---- retrieve usertype------");
	    if (roleId.equalsIgnoreCase("700003"))
	    {
	      sb.append(" SELECT  count(1) FROM ifms.nsdl_bh_dtls bh");
	      sb.append("  where bh.TRANSACTION_ID is not null  and file_status=11 and ((bh.FILE_NAME not in (SELECT FILE_NAME FROM ifms.NSDL_BILL_DTLS )) OR bh.FILE_NAME in (SELECT FILE_NAME FROM ifms.NSDL_BILL_DTLS WHERE BILL_STATUS<>3)) ");
	      sb.append(" and substr(bh.FILE_NAME,1,2) =substr(" + locId + ",1,2) and bh.status <> -1 ");
	      sb.append(" and cast ((bh_date)-VARCHAR_FORMAT(date(sysdate),'ddmmyyyy') as INTEGER) in ('-9000000') ");
	      
	      this.logger.info("---- retrieve usertype---" + sb.toString());
	      Query query = session.createSQLQuery(sb.toString());
	      
	      list = query.list();
	      if ((list != null) && (list.size() > 0)) {
	        count = Integer.parseInt(list.get(0).toString());
	      }
	    }
	    return count;
}

public int filesNotSendTO1DaysAlert(long locId, String roleId) {
	 Session session = getSession();
	    List list = null;
	    int count = 0;
	
	    StringBuffer sb = new StringBuffer();
	    this.logger.info("---- retrieve usertype------");
	    if (roleId.equalsIgnoreCase("700003"))
	    {
	      sb.append(" SELECT  count(1) FROM ifms.nsdl_bh_dtls bh");
	      sb.append("  where bh.TRANSACTION_ID is not null  and file_status=11 and ((bh.FILE_NAME not in (SELECT FILE_NAME FROM ifms.NSDL_BILL_DTLS )) OR bh.FILE_NAME in (SELECT FILE_NAME FROM ifms.NSDL_BILL_DTLS WHERE BILL_STATUS<>3)) ");
	      sb.append(" and substr(bh.FILE_NAME,1,2) =substr(" + locId + ",1,2) and bh.status <> -1 ");
	      sb.append(" and cast ((bh_date)-VARCHAR_FORMAT(date(sysdate),'ddmmyyyy') as INTEGER) in ('-10000000') ");
	      
	      this.logger.info("---- retrieve usertype---" + sb.toString());
	      Query query = session.createSQLQuery(sb.toString());
	      
	      list = query.list();
	      if ((list != null) && (list.size() > 0)) {
	        count = Integer.parseInt(list.get(0).toString());
	      }
	    }
	    return count;
}
}
