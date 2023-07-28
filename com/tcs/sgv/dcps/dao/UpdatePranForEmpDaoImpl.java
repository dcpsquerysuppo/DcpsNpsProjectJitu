package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UpdatePranForEmpDaoImpl extends GenericDaoHibernateImpl
  implements UpdatePranForEmpDao
{
  private Session ghibSession = null;
  private final Logger gLogger = Logger.getLogger(getClass());

  public UpdatePranForEmpDaoImpl(Class type, SessionFactory sessionFactory)
  {
    super(type);
    setSessionFactory(sessionFactory);
    this.ghibSession = sessionFactory.getCurrentSession();
  }

  public List getEmpInfoFromSevaarthId(String lStrSevaarthId, String lStrPranNo) {
    List lLstEmpDeselect = null;

    StringBuilder Strbld = new StringBuilder();
    try {
      Strbld.append("  SELECT emp.EMP_NAME,emp.SEVARTH_ID,emp.DCPS_ID,emp.DDO_CODE,desig.dsgn_name ,loc.LOC_NAME, emp.PPAN,emp.PRAN_NO, ");
      Strbld.append("   substr(loc.LOC_ID,1,4) as treasury_code , emp.PRAN_REMARK,emp.DCPS_EMP_ID  FROM mst_dcps_emp emp ");
      Strbld.append("   left outer join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)= substr(emp.DDO_CODE,1,2) and loc.DEPARTMENT_ID=100003  ");
      Strbld.append("  inner join ORG_EMP_MST org on org.emp_id=emp.ORG_EMP_MST_ID and ( org.EMP_SRVC_EXP > sysdate or org.EMP_SRVC_EXP is null) ");
      Strbld.append(" inner join  ORG_USERPOST_RLT opt on opt.user_id=org.user_id and opt.activate_flag=1 ");
      Strbld.append("  inner join  ORG_POST_DETAILS_RLT post on post.post_id =opt.post_id ");
      Strbld.append(" inner join ORG_DESIGNATION_MST desig on desig.dsgn_id=post.DSGN_ID  ");
      Strbld.append("   where  emp.FORM_STATUS = 1 and emp.REG_STATUS in (1,2) and emp.PRAN_ACTIVE=0  ");

      if ((lStrSevaarthId != null) && (!("".equals(lStrSevaarthId)))) {
        Strbld.append(" AND UPPER(emp.SEVARTH_ID) = :sevarthId");
      }

      if ((lStrPranNo != null) && (!("".equals(lStrPranNo)))) {
        Strbld.append(" AND emp.PRAN_NO = :pranNo");
      }

      SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

      if ((lStrSevaarthId != null) && (!("".equals(lStrSevaarthId)))) {
        lQuery.setString("sevarthId", lStrSevaarthId);
      }

      if ((lStrPranNo != null) && (!("".equals(lStrPranNo)))) {
        lQuery.setString("pranNo", lStrPranNo);
      }

      this.logger.info("query getEmpInfoFromSevaarthId ---------" + Strbld.toString());
      lLstEmpDeselect = lQuery.list();
    }
    catch (Exception e)
    {
      this.logger.info("Error occured in lstgetAllEmp ---------" + e);
      e.printStackTrace();
    }
    return lLstEmpDeselect;
  }

  public List CheckPranActiveOrNot(String lStrSevaarthId, String lStrPranNo)
  {
    List lLstEmpDeselect = null;

    StringBuilder Strbld = new StringBuilder();
    try {
      Strbld.append("  SELECT emp.PRAN_ACTIVE,emp.SEVARTH_ID,emp.DCPS_EMP_ID  FROM  mst_dcps_emp emp  where emp.reg_status in (1,2)  and emp.PRAN_NO is not null ");

      if ((lStrSevaarthId != null) && (!("".equals(lStrSevaarthId)))) {
        Strbld.append(" AND UPPER(emp.SEVARTH_ID) = :sevarthId");
      }

      if ((lStrPranNo != null) && (!("".equals(lStrPranNo)))) {
        Strbld.append(" AND emp.PRAN_NO = :pranNo");
      }

      SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

      if ((lStrSevaarthId != null) && (!("".equals(lStrSevaarthId)))) {
        lQuery.setString("sevarthId", lStrSevaarthId);
      }

      if ((lStrPranNo != null) && (!("".equals(lStrPranNo)))) {
        lQuery.setString("pranNo", lStrPranNo);
      }

      this.logger.info("query CheckPranActiveOrNot ---------" + Strbld.toString());
      lLstEmpDeselect = lQuery.list();
    }
    catch (Exception e)
    {
      this.logger.info("Error occured in lstgetAllEmp ---------" + e);
      e.printStackTrace();
    }
    return lLstEmpDeselect;
  }

  public List CheckPostActiveOrNot(String lStrSevaarthId, String lStrPranNo)
  {
    List lLstEmpDeselect = null;

    StringBuilder Strbld = new StringBuilder();
    try {
      Strbld.append("  SELECT emp.PRAN_ACTIVE,emp.SEVARTH_ID,emp.DCPS_EMP_ID,post.ACTIVATE_FLAG  FROM  mst_dcps_emp emp   ");
      Strbld.append("  inner join ORG_EMP_MST org on org.EMP_ID=emp.ORG_EMP_MST_ID  ");
      Strbld.append("  inner join ORG_USERPOST_RLT post on post.user_id=org.USER_ID and post.ACTIVATE_FLAG=1 ");
      Strbld.append("  where emp.reg_status in (1,2)  and emp.PRAN_NO is not null  ");

      if ((lStrSevaarthId != null) && (!("".equals(lStrSevaarthId)))) {
        Strbld.append(" AND UPPER(emp.SEVARTH_ID) = :sevarthId");
      }

      if ((lStrPranNo != null) && (!("".equals(lStrPranNo)))) {
        Strbld.append(" AND emp.PRAN_NO = :pranNo");
      }

      SQLQuery lQuery = this.ghibSession.createSQLQuery(Strbld.toString());

      if ((lStrSevaarthId != null) && (!("".equals(lStrSevaarthId)))) {
        lQuery.setString("sevarthId", lStrSevaarthId);
      }

      if ((lStrPranNo != null) && (!("".equals(lStrPranNo)))) {
        lQuery.setString("pranNo", lStrPranNo);
      }

      this.logger.info("query CheckPostActiveOrNot ---------" + Strbld.toString());
      lLstEmpDeselect = lQuery.list();
    }
    catch (Exception e)
    {
      this.logger.info("Error occured in lstgetAllEmp ---------" + e);
      e.printStackTrace();
    }
    return lLstEmpDeselect;
  }

  public void UpdatePranDeActiveStatus(long MstDcpsEmpId, String strSevaarthId, String PranNo, String remarks, long userId, long postId)
  {
    Date sysdate;
    try
    {
      sysdate = new Date();
      this.logger.error("Inside the UpdatePranDeActiveStatus" + sysdate);
      StringBuilder lSBQuery = new StringBuilder();
      lSBQuery.append("update mst_dcps_emp set PRAN_ACTIVE=1 ,UPDATED_DATE=sysdate where sevarth_id = :sevaarthId and DCPS_EMP_ID=:MstDcpsEmpId");
      Query lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
      lQuery.setParameter("sevaarthId", strSevaarthId);

      lQuery.setParameter("MstDcpsEmpId", Long.valueOf(MstDcpsEmpId));
      this.logger.error("Inside the update dao" + lQuery.toString());

      lQuery.executeUpdate();

      StringBuffer str1 = new StringBuffer();
      this.logger.info("ddoCode daoimpl" + MstDcpsEmpId);
      this.logger.info("activateFlag daoimpl" + PranNo);
      this.logger.info("activateFlag daoimpl" + remarks);
      str1.append("insert into TRN_DCPS_PRAN_ACTVN_DTLS(DCPS_EMP_ID,PRAN_NO,ACTIVATION_REMARK,CREATED_POST_ID,CREATED_USER_ID,ACTIVATION_DATE,UPDATED_POST_ID,UPDATED_USER_ID,UPDATED_DATE )values('" + MstDcpsEmpId + "','" + PranNo + "','" + remarks + "', '" + postId + "','" + userId + "',sysdate,null,null,null)");
      Query query1 = this.ghibSession.createSQLQuery(str1.toString());
      this.logger.info("insertHrPayCmpDdoCode------" + str1.toString());

      query1.executeUpdate();
    }
    catch (Exception e)
    {
      this.logger.error(" Error is updateDOB: " + e, e);
    }
  }
}