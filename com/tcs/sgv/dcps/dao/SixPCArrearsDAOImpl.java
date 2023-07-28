package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.common.constant.Constants;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SixPCArrearsDAOImpl extends GenericDaoHibernateImpl
  implements SixPCArrearsDAO
{
  private final Log gLogger = LogFactory.getLog(getClass());
  Session ghibSession = null;

  public SixPCArrearsDAOImpl(Class type, SessionFactory sessionFactory)
  {
    super(type);
    this.ghibSession = sessionFactory.getCurrentSession();
    setSessionFactory(sessionFactory);
  }

  public String getDdoCodeForDDO(Long lLngPostId)
    throws Exception
  {
    String lStrDdoCode = null;
    List lLstDdoDtls = null;
    try
    {
      StringBuilder lSBQuery = new StringBuilder();
      lSBQuery.append(" SELECT OD.ddoCode");
      lSBQuery.append(" FROM  OrgDdoMst OD");
      lSBQuery.append(" WHERE OD.postId = :postId ");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("postId", lLngPostId);
      lLstDdoDtls = lQuery.list();

      lStrDdoCode = lLstDdoDtls.get(0).toString();
    }
    catch (Exception e) {
      this.gLogger.error(new StringBuilder().append("Error is :").append(e).toString(), e);
    }

    return lStrDdoCode;
  }

  public String getDdoCode(Long lLngAsstPostId)
    throws Exception
  {
    StringBuilder lSBQuery = null;
    String lStrDdoCode = null;
    List lLstCodeList = null;
    Query hqlQuery = null;
    try {
      lSBQuery = new StringBuilder();
      lSBQuery.append(" SELECT OD.ddoCode");
      lSBQuery.append(" FROM RltDdoAsst RD, OrgDdoMst OD");
      lSBQuery.append(" WHERE OD.postId = RD.ddoPostId AND RD.asstPostId = :asstPostId ");

      hqlQuery = this.ghibSession.createQuery(lSBQuery.toString());
      hqlQuery.setParameter("asstPostId", lLngAsstPostId);
      lLstCodeList = hqlQuery.list();
      lStrDdoCode = lLstCodeList.get(0).toString();
    } catch (Exception e) {
      this.gLogger.error(new StringBuilder().append("Error is :").append(e).toString(), e);
    }
    return lStrDdoCode;
  }

  public List getEmpListForSixPCArrears(String lStrDDOCode, String lStrDesignation, Map displayTag)
    throws Exception
  {
    List lLstEmpList = null;
    StringBuilder lSBQuery = null;
    Query hqlQuery = null;
    try {
      lSBQuery = new StringBuilder();
      String[] columnValues = { "EM.name", "EM.dcpsId", "EM.name", "PC.fromDate", "PC.toDate", "PC.totalAmount", "PC.statusFlag", "PC.remarks" };

      if ((lStrDesignation != null) && (lStrDesignation.length() > 0) && (!(lStrDesignation.equals("All Designations"))))
      {
        lSBQuery.append(" SELECT EM.dcpsEmpId,EM.dcpsId,EM.name,PC.totalAmount,PC.dcpsSixPCId,PC.statusFlag,PC.remarks,EM.doj,PC.fromDate,PC.toDate ");

        lSBQuery.append(" FROM MstEmp EM ,MstSixPCArrears PC WHERE EM.dcpsEmpId=PC.dcpsEmpId");

        lSBQuery.append(" AND EM.ddoCode= :DDOCode AND EM.dcpsId is not null and EM.designation = :designation");

        lSBQuery.append(" AND (PC.statusFlag = 'D' OR PC.statusFlag  = 'R') ");

        lSBQuery.append(" ORDER BY ");
      }
      else {
        lSBQuery.append(" SELECT EM.dcpsEmpId,EM.dcpsId,EM.name,PC.totalAmount,PC.dcpsSixPCId,PC.statusFlag,PC.remarks,EM.doj,PC.fromDate,PC.toDate ");

        lSBQuery.append(" FROM MstEmp EM ,MstSixPCArrears PC WHERE (PC.statusFlag = 'D' OR PC.statusFlag = 'R') AND EM.dcpsEmpId=PC.dcpsEmpId");

        lSBQuery.append(" AND EM.ddoCode= :DDOCode AND EM.dcpsId is not null ");

        lSBQuery.append(" ORDER BY ");
      }

      String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER)) ? (String)displayTag.get(Constants.KEY_SORT_ORDER) : "asc";

      Integer orderbypara = null;

      if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
        orderbypara = (Integer)displayTag.get(Constants.KEY_SORT_PARA);
        lSBQuery.append(new StringBuilder().append(columnValues[orderbypara.intValue()]).append(" ").append(orderString).toString());
      }
      else {
        lSBQuery.append(" EM.name ASC");
      }

      hqlQuery = this.ghibSession.createQuery(lSBQuery.toString());

      if ((lStrDesignation != null) && (lStrDesignation.length() > 0) && (!(lStrDesignation.equals("All Designations"))))
      {
        hqlQuery.setParameter("designation", lStrDesignation);
      }

      hqlQuery.setParameter("DDOCode", lStrDDOCode);

      lLstEmpList = hqlQuery.list();
    }
    catch (Exception e) {
      e.printStackTrace();
      this.gLogger.error(new StringBuilder().append("Error is :").append(e).toString(), e);
    }
    return lLstEmpList;
  }

  public List getEmpListForSixPCArrearsDDO(String lStrDDOCode, String lStrDesignation, Map displayTag)
    throws Exception
  {
    List lLstEmpList = null;
    StringBuilder lSBQuery = null;
    Query hqlQuery = null;
    try {
      lSBQuery = new StringBuilder();
      String[] columnValues = { "EM.name", "EM.dcpsId", "EM.name", "PC.fromDate", "PC.toDate", "PC.totalAmount", "PC.statusFlag", "PC.remarks" };

      if ((lStrDesignation != null) && (lStrDesignation.length() > 0) && (!(lStrDesignation.equals("All Designations"))))
      {
        lSBQuery.append(" SELECT EM.dcpsEmpId,EM.dcpsId,EM.name,PC.totalAmount,PC.dcpsSixPCId,PC.statusFlag,PC.remarks,EM.doj,PC.fromDate,PC.toDate ");

        lSBQuery.append(" FROM MstEmp EM ,MstSixPCArrears PC WHERE EM.dcpsEmpId=PC.dcpsEmpId");

        lSBQuery.append(" AND EM.ddoCode= :DDOCode AND EM.dcpsId is not null AND EM.designation = :designation");

        lSBQuery.append(" AND (PC.statusFlag = 'F' OR PC.statusFlag  = 'A') ");

        lSBQuery.append(" ORDER BY ");
      } else {
        lSBQuery.append(" SELECT EM.dcpsEmpId,EM.dcpsId,EM.name,PC.totalAmount,PC.dcpsSixPCId,PC.statusFlag,PC.remarks,EM.doj,PC.fromDate,PC.toDate  ");

        lSBQuery.append(" FROM MstEmp EM ,MstSixPCArrears PC WHERE (PC.statusFlag = 'F' OR PC.statusFlag = 'A') AND EM.dcpsEmpId=PC.dcpsEmpId");

        lSBQuery.append(" AND EM.ddoCode= :DDOCode AND EM.dcpsId is not null ");

        lSBQuery.append(" ORDER BY ");
      }

      String orderString = (displayTag.containsKey(Constants.KEY_SORT_ORDER)) ? (String)displayTag.get(Constants.KEY_SORT_ORDER) : "desc";

      Integer orderbypara = null;

      if (displayTag.containsKey(Constants.KEY_SORT_PARA)) {
        orderbypara = (Integer)displayTag.get(Constants.KEY_SORT_PARA);
        lSBQuery.append(new StringBuilder().append(columnValues[orderbypara.intValue()]).append(" ").append(orderString).toString());
      }
      else {
        lSBQuery.append(" EM.name ASC");
      }

      hqlQuery = this.ghibSession.createQuery(lSBQuery.toString());
      if ((lStrDesignation != null) && (lStrDesignation.length() > 0) && (!(lStrDesignation.equals("All Designations"))))
      {
        hqlQuery.setParameter("designation", lStrDesignation);
      }

      hqlQuery.setParameter("DDOCode", lStrDDOCode);

      lLstEmpList = hqlQuery.list();
    }
    catch (Exception e) {
      e.printStackTrace();
      this.gLogger.error(new StringBuilder().append("Error is :").append(e).toString(), e);
    }
    return lLstEmpList;
  }

  public Integer getEmpListForSixPCArrearsCount(String lStrDDOCode, String lStrDesignation)
    throws Exception
  {
    Integer count = null;
    StringBuilder lSBQuery = null;
    Query hqlQuery = null;
    try
    {
      lSBQuery = new StringBuilder();

      if ((lStrDesignation != null) && (lStrDesignation.length() > 0) && (!(lStrDesignation.equals("All Designations"))))
      {
        lSBQuery.append(" SELECT count(*) ");
        lSBQuery.append(" FROM MstEmp EM ,MstSixPCArrears PC WHERE EM.dcpsEmpId=PC.dcpsEmpId");

        lSBQuery.append(" AND EM.ddoCode= :DDOCode AND EM.dcpsId is not null AND EM.designation = :designation");

        lSBQuery.append(" AND (PC.statusFlag = 'D' OR PC.statusFlag  = 'R') ");
      }
      else
      {
        lSBQuery.append(" SELECT count(*) ");
        lSBQuery.append(" FROM MstEmp EM ,MstSixPCArrears PC WHERE (PC.statusFlag = 'D' OR PC.statusFlag = 'R') AND EM.dcpsEmpId=PC.dcpsEmpId");

        lSBQuery.append(" AND EM.ddoCode= :DDOCode AND EM.dcpsId is not null");
      }

      hqlQuery = this.ghibSession.createQuery(lSBQuery.toString());
      if ((lStrDesignation != null) && (lStrDesignation.length() > 0) && (!(lStrDesignation.equals("All Designations"))))
      {
        hqlQuery.setParameter("designation", lStrDesignation);
      }

      hqlQuery.setParameter("DDOCode", lStrDDOCode);

      if ((hqlQuery.list() != null) && (hqlQuery.list().size() > 0))
        count = Integer.valueOf(Integer.parseInt(hqlQuery.list().get(0).toString()));
      else
        count = Integer.valueOf(0);
    }
    catch (Exception e)
    {
      this.logger.error(new StringBuilder().append(" Error is : ").append(e).toString(), e);
      e.printStackTrace();
      throw e;
    }
    return count;
  }

  public Integer getEmpListForSixPCArrearsDDOCount(String lStrDDOCode, String lStrDesignation)
    throws Exception
  {
    Integer count = null;
    StringBuilder lSBQuery = null;
    Query hqlQuery = null;
    try {
      lSBQuery = new StringBuilder();

      if ((lStrDesignation != null) && (lStrDesignation.length() > 0) && (!(lStrDesignation.equals("All Designations"))))
      {
        lSBQuery.append(" SELECT count(*) ");
        lSBQuery.append(" FROM MstEmp EM ,MstSixPCArrears PC WHERE EM.dcpsEmpId=PC.dcpsEmpId");

        lSBQuery.append(" AND EM.ddoCode= :DDOCode AND EM.dcpsId is not null ");

        lSBQuery.append(" AND EM.designation = :designation ");
        lSBQuery.append(" AND (PC.statusFlag = 'F' OR PC.statusFlag  = 'A') ");
      }
      else
      {
        lSBQuery.append(" SELECT count(*) ");
        lSBQuery.append(" FROM MstEmp EM ,MstSixPCArrears PC WHERE (PC.statusFlag = 'F' OR PC.statusFlag = 'A') AND EM.dcpsEmpId=PC.dcpsEmpId");

        lSBQuery.append(" AND EM.ddoCode= :DDOCode AND EM.dcpsId is not null ");
      }

      hqlQuery = this.ghibSession.createQuery(lSBQuery.toString());
      if ((lStrDesignation != null) && (lStrDesignation.length() > 0) && (!(lStrDesignation.equals("All Designations"))))
      {
        hqlQuery.setParameter("designation", lStrDesignation);
      }

      hqlQuery.setParameter("DDOCode", lStrDDOCode);

      if ((hqlQuery.list() != null) && (hqlQuery.list().size() > 0))
        count = Integer.valueOf(Integer.parseInt(hqlQuery.list().get(0).toString()));
      else
        count = Integer.valueOf(0);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      this.gLogger.error(new StringBuilder().append("Error is :").append(e).toString(), e);
    }
    return count;
  }
}