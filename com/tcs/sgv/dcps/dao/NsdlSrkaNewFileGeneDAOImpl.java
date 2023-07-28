package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.util.DBConnection;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.MstDcpsContributionYearly;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


public class NsdlSrkaNewFileGeneDAOImpl
  extends GenericDaoHibernateImpl
{
  private Session ghibSession = null;
  private final Logger gLogger = Logger.getLogger(getClass());
  
  public NsdlSrkaNewFileGeneDAOImpl(Class type, SessionFactory sessionFactory) {
    super(type);
    setSessionFactory(sessionFactory);
    ghibSession = sessionFactory.getCurrentSession();
  }
  
  public List getFinyears()
  {
    String query = "select finYearId,finYearCode from SgvcFinYearMst where finYearCode between '2008' and '2015' order by finYearCode ASC";
    List<Object> lLstReturnList = null;
    StringBuilder sb = new StringBuilder();
    sb.append(query);
    Query selectQuery = ghibSession.createQuery(sb.toString());
    List lLstResult = selectQuery.list();
    ComboValuesVO lObjComboValuesVO = null;
    
    if ((lLstResult != null) && (lLstResult.size() != 0)) {
      lLstReturnList = new ArrayList();
      
      for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
        Object[] obj = (Object[])lLstResult.get(liCtr);
        lObjComboValuesVO = new ComboValuesVO();
        lObjComboValuesVO.setId(obj[0].toString());
        lObjComboValuesVO.setDesc(obj[1].toString());
        lLstReturnList.add(lObjComboValuesVO);
      }
    } else {
      lLstReturnList = new ArrayList();
      lObjComboValuesVO = new ComboValuesVO();
      lObjComboValuesVO.setId("-1");
      lObjComboValuesVO.setDesc("--Select--");
      lLstReturnList.add(lObjComboValuesVO);
    }
    return lLstReturnList;
  }
  
  public List getAISlist()
  {
    List lstAis = null;
    try
    {
      StringBuilder sb = new StringBuilder();
      sb.append("   SELECT LOOKUP_ID,LOOKUP_DESC FROM CMN_LOOKUP_MST where LOOKUP_ID in (700240,700241,700242,700174)  ");
      Query LsQuery = ghibSession.createSQLQuery(sb.toString());
      
      logger.info("Script is ----------------" + LsQuery.toString());
      List lLstResult = LsQuery.list();
      ComboValuesVO lObjComboValuesVO = null;
      if ((lLstResult != null) && (lLstResult.size() != 0))
      {
        lstAis = new ArrayList();
        
        for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++)
        {
          Object[] obj = (Object[])lLstResult.get(liCtr);
          lObjComboValuesVO = new ComboValuesVO();
          lObjComboValuesVO.setId(obj[0].toString());
          lObjComboValuesVO.setDesc(obj[1].toString());
          lstAis.add(lObjComboValuesVO);
        }
      } else {
        lstAis = new ArrayList();
        lObjComboValuesVO = new ComboValuesVO();
        lObjComboValuesVO.setId("-1");
        lObjComboValuesVO.setDesc("--ALL--");
        lstAis.add(lObjComboValuesVO);
      }
    }
    catch (Exception e)
    {
      logger.info("Error found in getAISlist ----------" + e);
    }
    return lstAis;
  }
 

  public List getEmployeeListNsdlbackup(String finType, String treasury, Integer preyr)
  {
    List empLst = null;
    


    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append("select abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.CONTRIB_EMPLOYEE,abc.CONTRIB_EMPLOYER,abc.INT_CONTRB_EMPLOYEE, abc.INT_CONTRB_EMPLOYER,abc.loc_name,abc.dto_reg_no,abc.ddo_reg_no, ");
      Strbld.append(" abc.CONTRIB_EMP_DIFF,abc.CONTRIB_EMPLR_DIFF,abc.INT_EMP_DIFF,abc.INT_EMPLR_DIFF,abc.OPEN_INT_EMP,abc.OPEN_INT_EMPLR from ");
      
      Strbld.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double), ");
      Strbld.append(" cast(yr.INT_CONTRB_EMPLOYER as double) ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF, ");
      Strbld.append(" DEC(cast(nvl(yr.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(yr.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
      
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and loc.loc_id='" + treasury + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is  null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)) abc where  ");
      Strbld.append(" (abc.CONTRIB_EMP_DIFF > 0 or abc.INT_EMP_DIFF > 0 or abc.OPEN_INT_EMPLR >0 ) ");
      Strbld.append(" order by  abc.ddo_reg_no ");
      



      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      
      empLst = lQuery.list();
    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empLst;
  }
  

  public List getEmployeeListNsdl(String finType, String treasury, Integer preyr)
  {
    List empLst = null;
    


    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append("select abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.CONTRIB_EMPLOYEE,abc.CONTRIB_EMPLOYER,abc.INT_CONTRB_EMPLOYEE, abc.INT_CONTRB_EMPLOYER,abc.loc_name,abc.dto_reg_no,abc.ddo_reg_no, ");
      Strbld.append(" abc.CONTRIB_EMP_DIFF,abc.CONTRIB_EMPLR_DIFF,abc.INT_EMP_DIFF,abc.INT_EMPLR_DIFF,abc.OPEN_INT_EMP,abc.OPEN_INT_EMPLR from ");
      
      Strbld.append(" (SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double), ");
      Strbld.append(" cast(yr.INT_CONTRB_EMPLOYER as double) ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF, ");
      Strbld.append(" DEC(cast(nvl(yr.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(yr.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
      
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and loc.loc_id='" + treasury + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is  null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)) abc where  ");
      Strbld.append(" (abc.CONTRIB_EMP_DIFF > 0 or abc.INT_EMP_DIFF > 0 or abc.OPEN_INT_EMPLR >0 ) ");
      Strbld.append(" order by  abc.ddo_reg_no ");
      



      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      
      empLst = lQuery.list();
    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empLst;
  }
  



  public String getyear(String yeardesc)
  {
    String year = null;
    String query = "SELECT fin_year_id FROM SGVC_FIN_YEAR_MST where fin_year_desc =:yeardesc";
    StringBuilder sb = new StringBuilder();
    sb.append(query);
    Query selectQuery = ghibSession.createSQLQuery(sb.toString());
    selectQuery.setString("yeardesc", yeardesc);
    year = (String)selectQuery.uniqueResult();
    
    return year;
  }
  


  public String getmonth(String monthid)
  {
    String month = null;
    String query = "SELECT MONTH_NAME FROM SGVA_MONTH_MST where MONTH_ID=:monthid";
    StringBuilder sb = new StringBuilder();
    sb.append(query);
    Query selectQuery = ghibSession.createSQLQuery(sb.toString());
    selectQuery.setString("monthid", monthid);
    month = (String)selectQuery.uniqueResult();
    
    return month;
  }
  


  public void updateRepStatus(String dcpsId, String BatchId, String finyr, String treasury)
  {
    StringBuffer sb = new StringBuffer();
    try
    {
      sb.append(" update MST_DCPS_CONTRIB_TERMINATION_YEARLY set batch_id=" + BatchId + "    ");
      
      sb.append("  where  dcps_id='" + dcpsId + "' and  YEAR_ID='" + finyr + "' and batch_id is null  ");
      int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void updateRStatus(String dcpsid, String finType, String treasuryyno)
  {
    StringBuffer sb = new StringBuffer();
    
    try
    {
      sb.append(" update MST_DCPS_CONTRIB_TERMINATION_YEARLY set OPEN_INT_EMP=0 , OPEN_INT_EMPLR=0 ");
      
      sb.append("  where  dcps_id='" + dcpsid + "' and  YEAR_ID='" + finType + "' ");
      int i = ghibSession.createSQLQuery(sb.toString()).executeUpdate();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  

  public void insertDataForNSDLRepo(String BatchId, String finYr, String treasury)
  {
    StringBuilder Strbld = new StringBuilder();
    try
    {
      Strbld.append(" insert into MST_NSDL_SRKA_GEN (FIN_YEAR,loc_id,BATCH_ID,CREATED_DATE,IS_GENE) values ('" + finYr + "','" + treasury + "','" + BatchId + "',sysdate,'N') ");
      
      int i = ghibSession.createSQLQuery(Strbld.toString()).executeUpdate();
      
      logger.info("Query is *************" + Strbld.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void updateDataForNSDLRepo(String BatchId, String finYr, String treasury) {
    StringBuilder Strbld = new StringBuilder();
    try
    {
      Strbld.append("update MST_NSDL_SRKA_GEN set batch_id='" + BatchId + "' where FIN_YEAR='" + finYr + "' and LOC_ID='" + treasury + "' ");
      
      int i = ghibSession.createSQLQuery(Strbld.toString()).executeUpdate();
      
      logger.info("Query is *************" + Strbld.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public String selectDataForNSDLRepo(String acMain, String billno, String finType)
  {
    String BATCH_ID = null;
    StringBuilder Strbld = new StringBuilder();
    Strbld.append(" Select max(BATCH_ID) from NSDL_REPORT ");
    SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
    List transactionId = lQuery.list();
    if (transactionId.size() > 0)
    {
      BATCH_ID = transactionId.get(0).toString();
    }
    return BATCH_ID;
  }
  
  public List selectTrnPk(String finYr, String treasury)
    throws Exception
  {
    List contrList = null;
    Query lQuery = null;
    StringBuilder lSBQuery = null;
    try {
      lSBQuery = new StringBuilder();
      
      lSBQuery.append(" select distinct(DCPS_ID) from ");
      lSBQuery.append("( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) , ");
      lSBQuery.append("cast(yr.INT_CONTRB_EMPLOYER as double) ,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
      lSBQuery.append("(cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      lSBQuery.append("(cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      lSBQuery.append("dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,1) as INT_EMP_DIFF, ");
      lSBQuery.append("dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,1) as INT_EMPLR_DIFF, ");
      lSBQuery.append(" DEC(cast(nvl(yr.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(yr.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
      

      lSBQuery.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      lSBQuery.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID ");
      lSBQuery.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      lSBQuery.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
      lSBQuery.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      lSBQuery.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
      lSBQuery.append(" where yr.YEAR_ID='" + finYr + "' and loc.loc_id='" + treasury + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT  >= '2015-04-01' or emp.EMP_SERVEND_DT is null)) abc where  ");
      lSBQuery.append(" (abc.CONTRIB_EMP_DIFF > 0 or abc.INT_EMP_DIFF > 0 or abc.OPEN_INT_EMPLR > 0) ");
      lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
      
      contrList = lQuery.list();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      gLogger.error("Error is :" + e, e);
      throw e;
    }
    return contrList;
  }
  
  public List selectDataForNSDLGen(String finType, String treasury)
  {
    List temp = null;
    String batchId = null;
    StringBuilder Strbld = new StringBuilder();
    Strbld.append(" SELECT * FROM MST_NSDL_SRKA_GEN where  FIN_YEAR = '" + finType + "' and loc_id='" + treasury + "' ");
    
    SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
    

    temp = lQuery.list();
    logger.info("temp size" + temp.size());
    
    return temp;
  }
  


  public List getFinyeardesc()
  {
    String query = "select finYearId,finYearDesc from SgvcFinYearMst where finYearCode between '2012' and '2023' order by finYearCode ASC";
    List<Object> lLstReturnList = null;
    StringBuilder sb = new StringBuilder();
    sb.append(query);
    Query selectQuery = ghibSession.createQuery(sb.toString());
    List lLstResult = selectQuery.list();
    ComboValuesVO lObjComboValuesVO = null;
    
    if ((lLstResult != null) && (lLstResult.size() != 0)) {
      lLstReturnList = new ArrayList();
      
      for (int liCtr = 0; liCtr < lLstResult.size(); liCtr++) {
        Object[] obj = (Object[])lLstResult.get(liCtr);
        lObjComboValuesVO = new ComboValuesVO();
        lObjComboValuesVO.setId(obj[0].toString());
        lObjComboValuesVO.setDesc(obj[1].toString());
        lLstReturnList.add(lObjComboValuesVO);
      }
    } else {
      lLstReturnList = new ArrayList();
      lObjComboValuesVO = new ComboValuesVO();
      lObjComboValuesVO.setId("-1");
      lObjComboValuesVO.setDesc("--Select--");
      lLstReturnList.add(lObjComboValuesVO);
    }
    return lLstReturnList;
  }
  
  public List selectFromToDate(String finYearId)
    throws Exception
  {
    List contrList = null;
    Query lQuery = null;
    StringBuilder lSBQuery = null;
    try {
      lSBQuery = new StringBuilder();
      lSBQuery.append("SELECT to_char(FROM_DATE,'yyyy-MM-dd'),to_char(TO_DATE,'yyyy-MM-dd') FROM  SGVC_FIN_YEAR_MST where FIN_YEAR_ID= :finYearId ");
      
      lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
      
      lQuery.setString("finYearId", finYearId);
      contrList = lQuery.list();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      logger.error("Error is :" + e, e);
      throw e;
    }
    return contrList;
  }
  
  public String getEmployeeIntList(String finType, String treasury)
  {
    List empLst = null;
    String Intamount = null;
    

    StringBuilder Strbld = new StringBuilder();
    

    Strbld.append(" select cast(sum(abc.INT_EMPLR_DIFF) as double) from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) ,cast(yr.INT_CONTRB_EMPLOYER as double) , ");
    Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg ,      ");
    Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
    Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
    Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF, ");
    Strbld.append("  dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF  ");
    


    Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr   ");
    Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID    ");
    Strbld.append("  inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2)  ");
    Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
    Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE    ");
    Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
    Strbld.append(" where   yr.YEAR_ID='" + finType + "' and loc.loc_id='" + treasury + "'and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is  null and (emp.EMP_SERVEND_DT  >= '2015-04-01' or emp.EMP_SERVEND_DT is null)");
    Strbld.append("  order by  reg.ddo_reg_no ) abc   ");
    

    SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
    logger.info("script for all employee ---------" + lQuery.toString());
    
    empLst = lQuery.list();
    if ((empLst != null) && (empLst.size() > 0) && 
      (empLst.get(0) != null))
    {
      Intamount = empLst.get(0).toString();
    }
    


    return Intamount;
  }
  
  public String getFinyrdesc(long yrId) {
    List lyrDesc = null;
    String yrDesc = null;
    
    StringBuilder lSBQuery = new StringBuilder();
    
    Query lQuery = null;
    
    lSBQuery.append(" select fin_Year_Desc from Sgvc_Fin_Year_Mst where fin_Year_id=" + yrId);
    
    lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
    
    lyrDesc = lQuery.list();
    if ((lyrDesc.size() != 0) && (lyrDesc != null) && 
      (lyrDesc.get(0) != null)) {
      yrDesc = lyrDesc.get(0).toString();
    }
    
    return yrDesc;
  }
  
  public Long getNextSeqNum(String generatePkForBatchId)
  {
    Long genId = Long.valueOf(0L);
    List lGenIdForMonthly = null;
    
    StringBuilder lSBQuery = new StringBuilder();
    MstDcpsContributionYearly lObjMstDcpsContributionYearly = null;
    Query lQuery = null;
    
    lSBQuery.append(" SELECT GENERATED_ID FROM INT_TABLE_SEQ_MST where UPPER(TABLE_NAME)= :generatePkForBatchId ");
    lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
    lQuery.setParameter("generatePkForBatchId", generatePkForBatchId);
    
    lGenIdForMonthly = lQuery.list();
    if ((lGenIdForMonthly.size() > 0) && (lGenIdForMonthly.get(0) != null)) {
      genId = Long.valueOf(Long.parseLong(lGenIdForMonthly.get(0).toString()));
    }
    
    return genId;
  }
  
  public void updateGeneratedId(Long dcpsContributiongeneratePkForBatchId, String tableName)
  {
    Session session = getSession();
    StringBuilder lSBQuery = new StringBuilder();
    
    lSBQuery.append(" UPDATE INT_TABLE_SEQ_MST SET GENERATED_ID=:generatedId WHERE UPPER(TABLE_NAME) = :tableName");
    
    Query lQuery = session.createSQLQuery(lSBQuery.toString());
    lQuery.setLong("generatedId", dcpsContributiongeneratePkForBatchId.longValue());
    lQuery.setParameter("tableName", tableName);
    
    int status = lQuery.executeUpdate();
  }
  

  public String getbatchIdForNsdl(String finType, String treasury)
  {
    List temp = null;
    String batchId = null;
    StringBuilder Strbld = new StringBuilder();
    Strbld.append(" SELECT BATCH_ID FROM MST_NSDL_SRKA_GEN where  FIN_YEAR = '" + finType + "' and loc_id='" + treasury + "' and is_gene='N' ");
    
    SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
    

    temp = lQuery.list();
    logger.info("temp size" + temp.size());
    if ((temp != null) && (temp.size() > 0)) {
      batchId = temp.get(0).toString();
    }
    
    return batchId;
  }
  

  public String getTranbatchIdForNsdl(String finType, String treasury)
  {
    List temp = null;
    String batchId = null;
    StringBuilder Strbld = new StringBuilder();
    Strbld.append(" SELECT BATCH_ID FROM MST_NSDL_SRKA_GEN where  FIN_YEAR = '" + finType + "' and loc_id='" + treasury + "' and is_gene='Y' ");
    
    SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
    

    temp = lQuery.list();
    logger.info("temp size" + temp.size());
    if ((temp != null) && (temp.size() > 0)) {
      batchId = temp.get(0).toString();
    }
    
    return batchId;
  }
  

  public String getEmployeeContriTotalList(String finType, String treasury, Integer preyr)
  {
    List empLst = null;
    String amountTotal = null;
    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append(" select cast(sum(final.CONTRIB_EMP_DIFF) as double)  from  (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double),cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF  ");
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc  where abc.CONTRIB_EMP_DIFF > 0) final ");
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0) && (empLst.get(0) != null)) {
        amountTotal = empLst.get(0).toString();
      }
    }
    catch (Exception localException) {}
    


    return amountTotal;
  }
  
  public String getOpenEmployeeContriTotalList(String finType, String treasury)
  {
    List empLst = null;
    

    String amountTotal = null;
    StringBuilder Strbld = new StringBuilder();
    

    Strbld.append(" select cast(sum(final.OPEN_INT_EMP) as double)  from  (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
    Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg,       ");
    Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
    Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
    Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
    Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF,  ");
    Strbld.append(" cast(nvl(yr.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(yr.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR ");
    

    Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
    Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
    Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
    Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
    Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
    Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
    Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
    Strbld.append("  order by  reg.ddo_reg_no ) abc ) final ");
    
    SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
    logger.info("script for all employee ---------" + lQuery.toString());
    
    empLst = lQuery.list();
    if ((empLst != null) && (empLst.size() > 0) && (empLst.get(0) != null)) {
      amountTotal = empLst.get(0).toString();
    }
    
    return amountTotal;
  }
  

  public String getOpenEmplreContriTotalList(String finType, String treasury, Integer preyr)
  {
    List empLst = null;
    

    String amountTotal = null;
    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append(" select cast(sum(final.OPEN_INT_EMPLR) as double)  from  (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double),cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF,  ");
      
      Strbld.append(" DEC(cast(nvl(yr.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(yr.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
      

      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      

      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc where abc.OPEN_INT_EMPLR > 0 ) final ");
      


      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      
      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0) && (empLst.get(0) != null)) {
        amountTotal = empLst.get(0).toString();
      }
    }
    catch (Exception localException) {}
    
    return amountTotal;
  }
  

  public String getEmployeeContriTotalListInterest(String finType, String treasury)
  {
    List empLst = null;
    

    String amountTotal = null;
    StringBuilder Strbld = new StringBuilder();
    
    Strbld.append(" select  cast(sum(final.INT_EMP_DIFF) as double) from ");
    Strbld.append(" (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
    Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg,       ");
    Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
    Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
    Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
    Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF  ");
    


    Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
    Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
    Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
    Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
    Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
    Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
    Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
    Strbld.append("  order by  reg.ddo_reg_no ) abc  where abc.INT_EMP_DIFF > 0) final  ");
    
    SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
    logger.info("script for all employee ---------" + lQuery.toString());
    
    empLst = lQuery.list();
    if ((empLst != null) && (empLst.size() > 0) && (empLst.get(0) != null)) {
      amountTotal = empLst.get(0).toString();
    }
    
    return amountTotal;
  }
  
  public String getEmployeeContriTotalListInterestEmplr(String finType, String treasury, Integer preyr) {
    List empLst = null;
    

    String amountTotal = null;
    StringBuilder Strbld = new StringBuilder();
    

    try
    {
      Strbld.append(" select  cast(sum(final.INT_EMPLR_DIFF) as double) from ");
      Strbld.append(" (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE ,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double),cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF  ");
      


      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)   ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc  where abc.INT_EMP_DIFF > 0) final  ");
      




      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      
      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0) && (empLst.get(0) != null)) {
        amountTotal = empLst.get(0).toString();
      }
    }
    catch (Exception localException) {}
    

    return amountTotal;
  }
  
  public Long getDDoRegCount(String finType, String treasury)
  {
    List temp = null;
    Long regCount = null;
    StringBuilder Strbld = new StringBuilder();
    
    Strbld.append(" SELECT count(distinct(reg.ddo_reg_no))  ");
    Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr  ");
    Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID    ");
    Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2)   ");
    Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
    Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE   ");
    Strbld.append(" left outer join dcps_legacy_data ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
    Strbld.append("  where  yr.YEAR_ID='" + finType + "' and loc.loc_id='" + treasury + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1   ");
    SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
    

    temp = lQuery.list();
    logger.info("temp size" + temp.size());
    if ((temp != null) && (temp.size() > 0)) {
      regCount = Long.valueOf(Long.parseLong(temp.get(0).toString()));
    }
    
    return regCount;
  }
  
  public List getEmployeeListBkp(String finType, String treasury)
  {
    List empLst = null;
    StringBuilder Strbld = new StringBuilder();
   
    try
    {
      Strbld.append("select abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.CONTRIB_EMPLOYEE,abc.CONTRIB_EMPLOYER,abc.INT_CONTRB_EMPLOYEE, abc.INT_CONTRB_EMPLOYER,abc.loc_name,abc.dto_reg_no,abc.ddo_reg_no from ");
      
      Strbld.append("( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE,yr.CONTRIB_EMPLOYER,yr.INT_CONTRB_EMPLOYEE,");
      Strbld.append(" yr.INT_CONTRB_EMPLOYER,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ");
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double))  ,sum(cast(nvl(l.EMPLR_CONTRI,0) as double))  , sum(cast(nvl(l.EMP_INT,0) as double))  ,sum(cast(nvl(l.EMPLR_INT,0) as double))  ,sum(cast(nvl(l.OPEN_EMP,0) as double))  ,sum(cast(nvl(l.OPEN_EMPLR,0) as double))   FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and loc.loc_id='" + treasury + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)) abc   ");
      Strbld.append(" order by  abc.ddo_reg_no ");
      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
       empLst = lQuery.list();

    }
    catch (Exception e)
    {

      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empLst;
  }
  


  public List getEmployeeList(String finType, String treasury,Integer pr)
  {
    List empLst = null;
    StringBuilder Strbld = new StringBuilder();
   
    try
    {
      Strbld.append("select abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.CONTRIB_EMPLOYEE,abc.CONTRIB_EMPLOYER,abc.INT_CONTRB_EMPLOYEE, abc.INT_CONTRB_EMPLOYER,abc.loc_name,abc.dto_reg_no,abc.ddo_reg_no, ");
      Strbld.append(" abc.CONTRIB_EMP_DIFF,abc.CONTRIB_EMPLR_DIFF,abc.INT_EMP_DIFF,abc.INT_EMPLR_DIFF,abc.OPEN_INT_EMP,abc.OPEN_INT_EMPLR from ");
      Strbld.append("( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE,yr.CONTRIB_EMPLOYER,yr.INT_CONTRB_EMPLOYEE,");
      Strbld.append(" yr.INT_CONTRB_EMPLOYER,loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      

      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF,  ");
      
      Strbld.append(" DEC(cast(nvl(yr.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(yr.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003 ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and loc.loc_id='" + treasury + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)) abc where  ");
      Strbld.append(" (abc.CONTRIB_EMP_DIFF > 0 or abc.INT_EMP_DIFF > 0 or abc.OPEN_INT_EMPLR > 0) ");
      Strbld.append(" order by  abc.ddo_reg_no ");
      














      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee 1  ---------" + lQuery.toString());
      



      empLst = lQuery.list();
    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empLst;
  }
  


  public String getEmployeeListDdoregNsdl(String finType, String treasury, String ddoRegNo)
  {
    List empLst = null;
    
    String empDdoLst = null;
    
    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append(" select cast(sum(final.CONTRIB_EMP_DIFF) as double)  from  (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF  ");
      



      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc) final where final.CONTRIB_EMP_DIFF > 0  group by final.ddoreg ");
      Strbld.append("  having final.ddoreg='" + ddoRegNo + "' ");
      










      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee 2 ---------" + lQuery.toString());
      



      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0))
      {
        empDdoLst = empLst.get(0).toString();
      }
      

    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empDdoLst;
  }
  

  public String getEmployeeListDdoregNsdlInt(String finType, String treasury, String ddoRegNo)
  {
    List empLst = null;
    
    String empDdoLst = null;
    
    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append("  select cast(sum(final.INT_EMP_DIFF) as double)  from ");
      Strbld.append(" (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF  ");
      


      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc) final where final.INT_EMP_DIFF > 0  group by final.ddoreg  ");
      Strbld.append("  having final.ddoreg='" + ddoRegNo + "' ");
      










      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      



      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0))
      {
        empDdoLst = empLst.get(0).toString();
      }
      

    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empDdoLst;
  }
  


  public String getEmployeeOpenListDdoregNsdlInt(String finType, String treasury, String ddoRegNo)
  {
    List empLst = null;
    
    String empDdoLst = "0";
    
    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append("  select cast(sum(final.OPEN_INT_EMP) as double)  from ");
      Strbld.append(" (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF, ");
      

      Strbld.append(" cast(nvl(yr.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(yr.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR ");
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc) final where final.OPEN_INT_EMP > 0  group by final.ddoreg  ");
      Strbld.append("  having final.ddoreg='" + ddoRegNo + "' ");
      










      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      



      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0))
      {
        empDdoLst = empLst.get(0).toString();
      }
      

    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empDdoLst;
  }
  

  public String getEmplrOpenListDdoregNsdlInt(String finType, String treasury, String ddoRegNo)
  {
    List empLst = null;
    
    String empDdoLst = "0";
    
    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append("  select cast(sum(final.OPEN_INT_EMPLR) as double)  from ");
      Strbld.append(" (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF, ");
      

      Strbld.append(" cast(nvl(yr.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(yr.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR ");
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc) final where final.OPEN_INT_EMPLR > 0  group by final.ddoreg  ");
      Strbld.append("  having final.ddoreg='" + ddoRegNo + "' ");
      










      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      



      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0))
      {
        empDdoLst = empLst.get(0).toString();
      }
      

    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empDdoLst;
  }
  

  public String getEmployeeListDdoregNsdlIntEmplr(String finType, String treasury, String ddoRegNo)
  {
    List empLst = null;
    
    String empDdoLst = null;
    
    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append("  select cast(sum(final.INT_EMPLR_DIFF) as double)  from ");
      Strbld.append(" (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF  ");
      


      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata  on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc) final where final.INT_EMP_DIFF > 0  group by final.ddoreg  ");
      Strbld.append("  having final.ddoreg='" + ddoRegNo + "' ");
      










      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      



      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0))
      {
        empDdoLst = empLst.get(0).toString();
      }
      

    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empDdoLst;
  }
  


  public String getEmployeeRecordCountDdoregNsdl(String finType, String treasury, String ddoRegNo)
  {
    List empLst = null;
    
    String empDdoLst = null;
    
    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append(" select sum(final.count1)+sum(final.count2)+sum(final.count3) as a from ( select  abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.a,abc.CONTRIB_EMPLOYER,abc.b, ");
      Strbld.append(" abc.c, abc.loc_name,abc.dto_reg_no,abc.ddoreg, abc.CONTRIB_EMP_DIFF,abc.CONTRIB_EMPLR_DIFF,abc.INT_EMP_DIFF,abc.INT_EMPLR_DIFF, ");
      Strbld.append(" case when abc.CONTRIB_EMP_DIFF <= 0 then 0 else 1 end as count1, case when abc.INT_EMP_DIFF <= 0 then 0 else 1 end as count2, ");
      Strbld.append(" case when abc.OPEN_INT_EMP <= 0 then 0 else 1 end as count3  from ");
      Strbld.append(" ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) as b, ");
      Strbld.append(" cast(yr.INT_CONTRB_EMPLOYER as double) as c, loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no as ddoreg, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append("  dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF , ");
      Strbld.append(" cast(nvl(yr.OPEN_INT_EMP,0) as double ) as OPEN_INT_EMP,cast(nvl(yr.OPEN_INT_EMPLR,0) as double ) as OPEN_INT_EMPLR ");
      


      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID    ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append(" order by  reg.ddo_reg_no ) abc ) final ");
      Strbld.append(" group by final.ddoreg ");
      Strbld.append(" having final.ddoreg='" + ddoRegNo + "' ");
      Strbld.append("");
      
      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      

      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0)) {
        empDdoLst = empLst.get(0).toString();
      }
      
    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empDdoLst;
  }
  


  public String[] getEmployeeCountDdoregNsdl(String finType, String treasury)
  {
    List empLst = null;
    
    String[] empCountLst = new String['✐'];
    
    StringBuilder Strbld = new StringBuilder();
    
    try
    {
      Strbld.append("  SELECT sum(tmp.count1)FROM (SELECT count(DISTINCT emp.pran_no) as count1,reg.ddo_reg_no  as regno     ");
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join dcps_legacy_data ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null  and emp.PRAN_ACTIVE=1 ");
      Strbld.append(" group by yr.dcps_id,reg.ddo_reg_no  order by reg.ddo_reg_no )tmp group by tmp.regno ");
      










      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      



      empLst = lQuery.list();
      for (int i = 0; i < empLst.size(); i++) {
        empCountLst[i] = empLst.get(i).toString();
      }
    }
    catch (Exception e) {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return empCountLst;
  }
  



  public List getAllTreasuries()
  {
    ArrayList<ComboValuesVO> arrTreasury = new ArrayList();
    Connection lCon = null;
    PreparedStatement lStmt = null;
    ResultSet lRs = null;
    String treasury_id = null;
    String treasury_name = null;
    try
    {
      StringBuffer lsb = new StringBuffer();
      lsb = new StringBuffer(
        "select CM.loc_Id , CM.loc_Name from Cmn_Location_Mst CM where department_Id in (100003)  and CM.LANG_ID = 1 and CM.LOC_ID not in(9991,1111)  order by CM.loc_id  ");
      
      lCon = DBConnection.getConnection();
      lStmt = lCon.prepareStatement(lsb.toString());
      lRs = lStmt.executeQuery();
      while (lRs.next()) {
        ComboValuesVO vo = new ComboValuesVO();
        treasury_id = lRs.getString("loc_Id");
        treasury_name = lRs.getString("loc_Name");
        vo.setId(treasury_id);
        vo.setDesc(treasury_id + "-" + treasury_name);
        arrTreasury.add(vo);
      }
    }
    catch (Exception e) {
      gLogger.info("Sql Exception:" + e, e);
      try
      {
        if (lStmt != null) {
          lStmt.close();
        }
        if (lRs != null) {
          lRs.close();
        }
        if (lCon != null) {
          lCon.close();
        }
        
        lStmt = null;
        lRs = null;
        lCon = null;
      } catch (Exception E) {
        gLogger.info("Sql Exception:" + e, e);
      }
    }
    finally
    {
      try
      {
        if (lStmt != null) {
          lStmt.close();
        }
        if (lRs != null) {
          lRs.close();
        }
        if (lCon != null) {
          lCon.close();
        }
        
        lStmt = null;
        lRs = null;
        lCon = null;
      } catch (Exception e) {
        gLogger.info("Sql Exception:" + e, e);
      }
    }
    return arrTreasury;
  }
  

  public HashMap getEmployeeRecordCountDdoregNsdlMap(String finType, String treasury, Integer preyr)
  {
    List empLst = null;
    
    Object[] empDdoLst = null;
    
    StringBuilder Strbld = new StringBuilder();
    
    HashMap m = new HashMap();
    

    try
    {
      Strbld.append(" select sum(final.count1)+sum(final.count2)+sum(final.count3) ,final.ddo_reg_no from ( select  abc.EMP_NAME,abc.DCPS_ID,abc.PRAN_NO,abc.a,abc.CONTRIB_EMPLOYER,abc.INT_CONTRB_EMPLOYEE, ");
      Strbld.append(" abc.INT_CONTRB_EMPLOYER, abc.loc_name,abc.dto_reg_no,abc.ddo_reg_no, abc.CONTRIB_EMP_DIFF,abc.CONTRIB_EMPLR_DIFF,abc.INT_EMP_DIFF,abc.INT_EMPLR_DIFF, ");
      Strbld.append(" case when abc.CONTRIB_EMP_DIFF <= 0 then 0 else 1 end as count1, case when abc.INT_EMP_DIFF <= 0 then 0 else 1 end as count2, ");
      Strbld.append(" case when abc.OPEN_INT_EMPLR <= 0 then 0 else 1 end as count3  from ");
      Strbld.append(" ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE as a,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double), ");
      Strbld.append(" cast(yr.INT_CONTRB_EMPLOYER as double), loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append("  dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF , ");
      
      Strbld.append("DEC(cast(nvl(yr.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(yr.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
      


      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      


      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID    ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append(" order by  reg.ddo_reg_no ) abc ) final ");
      Strbld.append(" group by final.ddo_reg_no ");
      
      Strbld.append("");
      


      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      

      empLst = lQuery.list();
      logger.info("empLst====" + empLst);
      if ((empLst != null) && (empLst.size() > 0))
      {
        for (int i = 0; i < empLst.size(); i++)
        {
          empDdoLst = (Object[])empLst.get(i);
          if ((empDdoLst != null) && (empDdoLst.length > 0))
          {
            logger.info("Data ---------" + empDdoLst[1] + "-----" + empDdoLst[0]);
            m.put(empDdoLst[1], empDdoLst[0]);
          }
          
        }
        
      }
    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return m;
  }
  

  public HashMap getEmployeeListDdoregNsdlMap(String finType, String treasury, Integer preyr)
  {
    List empLst = null;
    
    Object[] empDdoLst = null;
    
    StringBuilder Strbld = new StringBuilder();
    
    HashMap m = new HashMap();
    

    try
    {
      Strbld.append(" select cast(sum(final.CONTRIB_EMP_DIFF) as double),final.ddo_reg_no  from  (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) ,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF  ");
      



      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc) final where final.CONTRIB_EMP_DIFF > 0  group by final.ddo_reg_no ");
      













      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      



      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0))
      {
        for (int i = 0; i < empLst.size(); i++)
        {
          empDdoLst = (Object[])empLst.get(i);
          if ((empDdoLst != null) && (empDdoLst.length > 0))
          {
            m.put(empDdoLst[1], empDdoLst[0]);
          }
          
        }
        
      }
      
    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return m;
  }
  



  public HashMap getEmployeeListDdoregNsdlIntEmplrMap(String finType, String treasury, Integer preyr)
  {
    List empLst = null;
    
    Object[] empDdoLst = null;
    
    StringBuilder Strbld = new StringBuilder();
    
    HashMap m = new HashMap();
    
    try
    {
      Strbld.append("  select cast(sum(final.INT_EMPLR_DIFF) as double),final.ddo_reg_no  from ");
      Strbld.append(" (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE ,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) ,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no ,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF  ");
     
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(l.EMP_CONTRI as double)) as EMP_CONTRI,sum(cast(l.EMPLR_CONTRI as double)) as EMPLR_CONTRI,sum(cast(l.EMP_INT as double)) as EMP_INT,sum(cast(l.EMPLR_INT as double)) as EMPLR_INT FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata  on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code  ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc) final where final.INT_EMP_DIFF > 0  group by final.ddo_reg_no  ");
    
      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      
      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0))
      {
        for (int i = 0; i < empLst.size(); i++)
        {
          empDdoLst = (Object[])empLst.get(i);
          if ((empDdoLst != null) && (empDdoLst.length > 0))
          {
            m.put(empDdoLst[1], empDdoLst[0]);
          }
          
        }
        
      }
      
    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return m;
  }
  

  public HashMap getEmplrOpenListDdoregNsdlIntMap(String finType, String treasury, Integer preyr)
  {
    List empLst = null;
    
    Object[] empDdoLst = null;
    
    StringBuilder Strbld = new StringBuilder();
    
    HashMap m = new HashMap();
    

    try
    {
      Strbld.append("  select cast(sum(final.OPEN_INT_EMPLR) as double),final.ddo_reg_no  from ");
      Strbld.append(" (select * from  ( SELECT emp.EMP_NAME,emp.DCPS_ID,emp.PRAN_NO,yr.CONTRIB_EMPLOYEE ,yr.CONTRIB_EMPLOYER,cast(yr.INT_CONTRB_EMPLOYEE as double) ,cast(yr.INT_CONTRB_EMPLOYER as double),   ");
      Strbld.append(" loc.loc_name,dto.dto_reg_no,reg.ddo_reg_no,       ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYEE as double) - cast(nvl(ldata.EMP_CONTRI,0) as double))as CONTRIB_EMP_DIFF, ");
      Strbld.append(" (cast(yr.CONTRIB_EMPLOYER as double) - cast(nvl(ldata.EMPLR_CONTRI,0) as double))as CONTRIB_EMPLR_DIFF, ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYEE as double)-cast(nvl(ldata.EMP_INT,0) as double )),25,2) as INT_EMP_DIFF,  ");
      Strbld.append(" dec((cast(yr.INT_CONTRB_EMPLOYER as double)-cast(nvl(ldata.EMPLR_INT,0) as double )),25,2) as INT_EMPLR_DIFF, ");
      

      Strbld.append(" DEC(cast(nvl(yr.OPEN_INT_EMP,0) as double) -cast(nvl(ldata.OPEN_EMP,0) as double ),25,2) as OPEN_INT_EMP,DEC(cast(nvl(yr.OPEN_INT_EMPLR,0) as double) -cast(nvl(ldata.OPEN_EMPLR,0) as double ),25,2) as OPEN_INT_EMPLR ");
      Strbld.append(" FROM MST_DCPS_CONTRIB_TERMINATION_YEARLY yr ");
      Strbld.append(" inner join mst_dcps_emp emp on emp.DCPS_ID= yr.DCPS_ID     ");
      Strbld.append(" inner join MST_DTO_REG dto on substr(dto.LOC_ID,1,2)=substr(emp.ddo_code,1,2) ");
      Strbld.append(" inner join CMN_LOCATION_MST loc on substr(loc.LOC_ID,1,2)=substr(emp.DDO_CODE,1,2) and loc.department_id=100003  ");
      Strbld.append(" inner join mst_ddo_reg reg on reg.ddo_code=emp.DDO_CODE  ");
      
      Strbld.append(" left outer join (SELECT l.FIN_YEAR_CODE,l.DCPS_ID,sum(cast(nvl(l.EMP_CONTRI,0) as double)) as EMP_CONTRI,sum(cast(nvl(l.EMPLR_CONTRI,0) as double)) as EMPLR_CONTRI, sum(cast(nvl(l.EMP_INT,0) as double)) as EMP_INT,sum(cast(nvl(l.EMPLR_INT,0) as double)) as EMPLR_INT,sum(cast(nvl(l.OPEN_EMP,0) as double)) as OPEN_EMP,sum(cast(nvl(l.OPEN_EMPLR,0) as double)) as OPEN_EMPLR FROM DCPS_LEGACY_DATA l group by l.FIN_YEAR_CODE,l.DCPS_ID) ldata on yr.DCPS_ID=ldata.dcps_id and yr.YEAR_ID=ldata.fin_year_code ");
      Strbld.append(" where yr.YEAR_ID='" + finType + "' and emp.AC_DCPS_MAINTAINED_BY=700174  and  loc.loc_id='" + treasury + "' and emp.pran_no is not null and emp.PRAN_ACTIVE=1 and yr.batch_id is null and (emp.EMP_SERVEND_DT >= '2015-04-01' or emp.EMP_SERVEND_DT is null)  ");
      Strbld.append("  order by  reg.ddo_reg_no ) abc) final where final.OPEN_INT_EMPLR > 0  group by final.ddo_reg_no  ");
      

      logger.info("   ---------" + Strbld.toString());
      SQLQuery lQuery = ghibSession.createSQLQuery(Strbld.toString());
      logger.info("script for all employee ---------" + lQuery.toString());
      



      empLst = lQuery.list();
      if ((empLst != null) && (empLst.size() > 0))
      {
        for (int i = 0; i < empLst.size(); i++)
        {
          empDdoLst = (Object[])empLst.get(i);
          if ((empDdoLst != null) && (empDdoLst.length > 0))
          {
            m.put(empDdoLst[1], empDdoLst[0]);
          }
          
        }
        
      }
      
    }
    catch (Exception e)
    {
      logger.info("Error occer in  getEmployeeList ---------" + e);
    }
    return m;
  }
}
