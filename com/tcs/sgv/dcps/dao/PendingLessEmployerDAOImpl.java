package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.valueobject.TrnNPSBeamsIntegration;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.loader.custom.Return;

public class PendingLessEmployerDAOImpl extends GenericDaoHibernateImpl
  implements NsdlNpsDAO
{
  private final Log gLogger = LogFactory.getLog(getClass());
  Session ghibSession = null;
  
  public PendingLessEmployerDAOImpl(Class type, SessionFactory sessionFactory)
  {
    super(type);
    this.ghibSession = sessionFactory.getCurrentSession();
    setSessionFactory(sessionFactory);
  }

  public List getFinyear()
  {
	  
		Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        if(4>month+1){
            year = year-1;
        }

    String query = "select finYearCode,finYearCode from SgvcFinYearMst where finYearCode between '2015' and '"+year+"' order by finYearCode ASC";
    List<Object> lLstReturnList = null;
    StringBuilder sb = new StringBuilder();
    sb.append(query);
    Query selectQuery = this.ghibSession.createQuery(sb.toString());
    List lLstResult = selectQuery.list();
    ComboValuesVO lObjComboValuesVO = null;
    if ((lLstResult != null) && (lLstResult.size() != 0))
    {
      lLstReturnList = new ArrayList();
      for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++)
      {
        Object[] obj = (Object[])lLstResult.get(liCtr);
        lObjComboValuesVO = new ComboValuesVO();
        lObjComboValuesVO.setId(obj[0].toString());
        lObjComboValuesVO.setDesc(obj[1].toString());
        lLstReturnList.add(lObjComboValuesVO);
      }
    }
    else
    {
      lLstReturnList = new ArrayList();
      lObjComboValuesVO = new ComboValuesVO();
      lObjComboValuesVO.setId("-1");
      lObjComboValuesVO.setDesc("--Select--");
      lLstReturnList.add(lObjComboValuesVO);
    }
    return lLstReturnList;
  }
  
 
  public List getAllSubTreasury(String treasuryId)
  {
    List<ComboValuesVO> lLstReturnList = null;
    StringBuilder sb = new StringBuilder();
    
    if(treasuryId.equalsIgnoreCase("NA"))
    sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where department_id= 100003   order by loc_name ");
    else
        sb.append("SELECT loc_id, loc_name FROM CMN_LOCATION_MST where department_id= 100003  and LOC_ID= '"+treasuryId+"' order by loc_name ");

    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
    
    lLstReturnList = new ArrayList();
    
    List lLstResult = selectQuery.list();
    
    ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
    lObjComboValuesVO.setId("-1");
    lObjComboValuesVO.setDesc("--Select--");
    lLstReturnList.add(lObjComboValuesVO);
    if ((lLstResult != null) && (lLstResult.size() != 0))
    {
      for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++)
      {
        Object[] obj = (Object[])lLstResult.get(liCtr);
        lObjComboValuesVO = new ComboValuesVO();
        lObjComboValuesVO.setId(obj[0].toString());
        String desc = obj[1].toString();
        lObjComboValuesVO.setDesc(desc);
        lLstReturnList.add(lObjComboValuesVO);
      }
    }
    else
    {
      lLstReturnList = new ArrayList();
      lObjComboValuesVO = new ComboValuesVO();
      lObjComboValuesVO.setId("-1");
      lObjComboValuesVO.setDesc("--Select--");
      lLstReturnList.add(lObjComboValuesVO);
    }
    return lLstReturnList;
  }
  public List getAllDDo(String treasuryId)
  {
    List<ComboValuesVO> lLstReturnList = null;
    StringBuilder sb = new StringBuilder();
    
    sb.append("select DDO_CODE ,DDO_CODE||'-'||ddo_name from ORG_DDO_MST where DDO_CODE =:loc_id  ");
    Query selectQuery = this.ghibSession.createSQLQuery(sb.toString());
    selectQuery.setParameter("loc_id", treasuryId);
    
    lLstReturnList = new ArrayList();
    
    List lLstResult = selectQuery.list();
    
    ComboValuesVO lObjComboValuesVO = new ComboValuesVO();
    lObjComboValuesVO.setId("-1");
    lObjComboValuesVO.setDesc("--Select--");
    lLstReturnList.add(lObjComboValuesVO);
    if ((lLstResult != null) && (lLstResult.size() != 0))
    {
      for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++)
      {
        Object[] obj = (Object[])lLstResult.get(liCtr);
        lObjComboValuesVO = new ComboValuesVO();
        lObjComboValuesVO.setId(obj[0].toString());
        String desc = obj[1].toString();
        lObjComboValuesVO.setDesc(desc);
        lLstReturnList.add(lObjComboValuesVO);
      }
    }
    else
    {
      lLstReturnList = new ArrayList();
      lObjComboValuesVO = new ComboValuesVO();
      lObjComboValuesVO.setId("-1");
      lObjComboValuesVO.setDesc("--Select--");
      lLstReturnList.add(lObjComboValuesVO);
    }
    return lLstReturnList;
  }
  public String getDdoCodeForDDO(Long lLngPostId) {

		String lStrDdoCode = null;
		List lLstDdoDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.postId = :postId ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("postId", lLngPostId);

			lLstDdoDtls = lQuery.list();

			if(lLstDdoDtls != null)
			{
				if(lLstDdoDtls.size()!= 0)
				{
					if(lLstDdoDtls.get(0) != null)
					{
						lStrDdoCode = lLstDdoDtls.get(0).toString();
					}
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrDdoCode;
	}
  
  public String getDDOPostID(Long lLngPostId) {

		String lStrDdoCode = null;
		List lLstDdoDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select DDO_POST_ID  from RLT_DCPS_DDO_ASST where ASST_POST_ID= '"+lLngPostId+"'  ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lLstDdoDtls = lQuery.list();

			if(lLstDdoDtls != null)
			{
				if(lLstDdoDtls.size()!= 0)
				{
					if(lLstDdoDtls.get(0) != null)
					{
						lStrDdoCode = lLstDdoDtls.get(0).toString();
					}
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrDdoCode;
	}

  
  public String getRoleId(Long lLngPostId) {

		String lStrRoleId = "NA";
		List lLstRoleDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" select ROLE_ID  from ACL_POSTROLE_RLT where POST_ID ='"+ lLngPostId + "'  and ROLE_ID in (700003,700001,700004,700002) ");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());

			lLstRoleDtls = lQuery.list();

			if (lLstRoleDtls != null) {
				if (lLstRoleDtls.size() != 0) {
					if (lLstRoleDtls.get(0) != null) {
						lStrRoleId = lLstRoleDtls.get(0).toString();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			gLogger.error("Error is :" + e, e);

		}
		return lStrRoleId;
	}
	
	

}
