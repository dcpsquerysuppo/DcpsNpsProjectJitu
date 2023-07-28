/* Decompiler 123ms, total 1127ms, lines 852 */
package com.tcs.sgv.dcps.dao;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.valueobject.HstDcpsChanges;
import com.tcs.sgv.dcps.valueobject.HstDcpsNomineeChanges;
import com.tcs.sgv.dcps.valueobject.MstEmp;
import com.tcs.sgv.dcps.valueobject.MstEmpNmn;
import com.tcs.sgv.dcps.valueobject.RltDcpsPayrollEmp;
import com.tcs.sgv.dcps.valueobject.TrnDcpsChanges;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ChangesFormDAOImpl extends GenericDaoHibernateImpl implements ChangesFormDAO {
   private final Log gLogger = LogFactory.getLog(this.getClass());
   Session ghibSession = null;

   public ChangesFormDAOImpl(Class type, SessionFactory sessionFactory) {
      super(type);
      this.ghibSession = sessionFactory.getCurrentSession();
      this.setSessionFactory(sessionFactory);
   }

   public List getAllDcpsEmployees(String lStrDesignationId, String lStrDdoCode, String sevarthId, String employeeName) {
      StringBuilder lSBQuery = new StringBuilder();
      List<MstEmp> EmpList = null;
      lSBQuery.append(" select emp.DCPS_EMP_ID, emp.EMP_NAME, emp.DCPS_ID, emp.SEVARTH_ID FROM MST_DCPS_EMP emp where emp.REG_STATUS IN (1,2) and emp.DDO_CODE = '" + lStrDdoCode + "'");
      if (lStrDesignationId != null && !lStrDesignationId.equals("-1")) {
         lSBQuery.append(" and emp.DESIGNATION = '" + lStrDesignationId + "' ");
      }

      if (sevarthId != null && !sevarthId.equals("")) {
         lSBQuery.append(" and emp.SEVARTH_ID = '" + sevarthId + "' ");
      }

      if (employeeName != null && !employeeName.equals("")) {
         lSBQuery.append(" and emp.EMP_NAME = '" + employeeName + "' ");
      }

      lSBQuery.append(" order by emp.EMP_NAME ");
      this.logger.info("Query for get getAllDcpsEmployees is---->>>>" + lSBQuery.toString());
      Query lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
      this.logger.info("query is---->>>>" + lQuery.toString());
      this.gLogger.info("designationId is in  " + lStrDesignationId);
      this.gLogger.info("ddoCode is in  " + lStrDdoCode);
      this.gLogger.info("sevarthId is in  " + sevarthId);
      this.gLogger.info("employeeName is in  " + employeeName);
      this.gLogger.info("lQuery is in getAllDcpsEmployees " + lQuery);
      EmpList = lQuery.list();
      this.gLogger.info("EmpList size is  " + EmpList.size());
      return EmpList;
   }

   public MstEmp getEmpDetails(Long dcpsEmpId) {
      StringBuilder lSBQuery = new StringBuilder();
      MstEmp EmpList = null;
      lSBQuery.append("FROM MstEmp where dcpsEmpId = :dcpsEmpId)");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsEmpId", dcpsEmpId);
      this.gLogger.info("lSBQuery is in  " + lSBQuery);
      this.gLogger.info("lQuery is in  " + lQuery);
      this.gLogger.info("dcpsEmpId is   " + dcpsEmpId);
      EmpList = (MstEmp)lQuery.uniqueResult();
      this.gLogger.info("EmpList is   " + EmpList);
      return EmpList;
   }

   public List getCurrentOffices() {
      ArrayList<ComboValuesVO> finalList = new ArrayList();
      String query = "select dcpsDdoOfficeIdPk,dcpsDdoOfficeName from DdoOffice";
      StringBuilder sb = new StringBuilder();
      sb.append(query);
      Query selectQuery = this.ghibSession.createQuery(sb.toString());
      List resultList = selectQuery.list();
      new ComboValuesVO();
      if (resultList != null && resultList.size() > 0) {
         ComboValuesVO cmbVO = new ComboValuesVO();
         cmbVO.setId("-1");
         cmbVO.setDesc("-- Select --");
         finalList.add(cmbVO);
         Iterator it = resultList.iterator();

         while(it.hasNext()) {
            cmbVO = new ComboValuesVO();
            Object[] obj = (Object[])it.next();
            cmbVO.setId(obj[0].toString());
            cmbVO.setDesc(obj[1].toString());
            finalList.add(cmbVO);
         }
      }

      return finalList;
   }

   public List getNominees(String empId) {
      StringBuilder lSBQuery = new StringBuilder();
      List<MstEmpNmn> NomineesList = null;
      lSBQuery.append(" FROM MstEmpNmn WHERE dcpsEmpId.dcpsEmpId = :empId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("empId", Long.parseLong(empId));
      NomineesList = lQuery.list();
      return NomineesList;
   }

   public List getChangesDraftsForDesig(String lStrDesignationId, String lStrUserType, String lStrDDOCode) {
      StringBuilder lSBQuery = new StringBuilder();
      List ChangesDraftsList = null;
      lSBQuery.append(" SELECT nvl(CN.UPDATED_DATE,CN.CREATED_DATE),EM.DCPS_ID,EM.EMP_NAME,EM.DCPS_EMP_ID,CN.TYPE_OF_CHANGES, CN.DCPS_CHANGES_ID,EM.SEVARTH_ID FROM hst_dcps_changes CN JOIN MST_DCPS_EMP EM ON CN.DCPS_EMP_ID=EM.DCPS_EMP_ID  WHERE EM.DESIGNATION='" + lStrDesignationId + "' ");
      if (lStrUserType.equals("DDOAsst")) {
         lSBQuery.append(" AND (CN.FORM_STATUS IS NULL OR CN.FORM_STATUS = -1)");
      }

      if (lStrUserType.equals("DDO")) {
         lSBQuery.append(" AND (CN.FORM_STATUS = 0)");
      }

      if (lStrDDOCode != null && !"".equals(lStrDDOCode)) {
         lSBQuery.append(" AND EM.DDO_CODE = '" + lStrDDOCode.trim() + "'");
      }

      Query lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
      ChangesDraftsList = lQuery.list();
      return ChangesDraftsList;
   }

   public HstDcpsChanges getChangesDetails(Long dcpsChangesId) {
      StringBuilder lSBQuery = new StringBuilder();
      HstDcpsChanges HstDcpsChangesObj = null;
      lSBQuery.append("FROM HstDcpsChanges where dcpsChangesId = :dcpsChangesId)");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      HstDcpsChangesObj = (HstDcpsChanges)lQuery.uniqueResult();
      return HstDcpsChangesObj;
   }

   public Long getPersonalChangesIdforChangesId(Long dcpsChangesId) {
      StringBuilder lSBQuery = new StringBuilder();
      new ArrayList();
      Long changesPersonalId = 0L;
      lSBQuery.append(" select dcpsPersonalChangesId FROM HstDcpsPersonalChanges WHERE dcpsChangesId = :dcpsChangesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      List<Long> tempList = lQuery.list();
      changesPersonalId = (Long)tempList.get(0);
      return changesPersonalId;
   }

   public RltDcpsPayrollEmp getEmpPayrollDetailsForEmpId(Long dcpsEmpId) {
      StringBuilder lSBQuery = new StringBuilder();
      RltDcpsPayrollEmp EmpList = null;
      lSBQuery.append("FROM RltDcpsPayrollEmp where dcpsEmpId = :dcpsEmpId)");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsEmpId", dcpsEmpId);
      EmpList = (RltDcpsPayrollEmp)lQuery.uniqueResult();
      return EmpList;
   }

   public Long getOfficeChangesIdforChangesId(Long dcpsChangesId) {
      StringBuilder lSBQuery = new StringBuilder();
      new ArrayList();
      Long changesOfficeId = 0L;
      lSBQuery.append(" select dcpsOfficeChangesId FROM HstDcpsOfficeChanges WHERE dcpsChangesId = :dcpsChangesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      List<Long> tempList = lQuery.list();
      changesOfficeId = (Long)tempList.get(0);
      return changesOfficeId;
   }

   public Long getOtherChangesIdforChangesId(Long dcpsChangesId) {
      StringBuilder lSBQuery = new StringBuilder();
      new ArrayList();
      Long changesOtherId = 0L;
      lSBQuery.append(" select dcpsOtherChangesId FROM HstDcpsOtherChanges WHERE dcpsChangesId = :dcpsChangesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      List<Long> tempList = lQuery.list();
      changesOtherId = (Long)tempList.get(0);
      return changesOtherId;
   }

   public void deleteNomineesFromHstForGivenEmployee(Long lLongDcpsHstChangesId) {
      StringBuilder lSBQuery = new StringBuilder();
      lSBQuery.append(" delete from HstDcpsNomineeChanges where dcpsChangesId = :dcpsChangesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsChangesId", lLongDcpsHstChangesId);
      lQuery.executeUpdate();
   }

   public Long getLatestRefIdForNomineeChanges(Long dcpsEmpId, Long dcpsChangesId) {
      this.getSession();
      StringBuilder lSBQuery = new StringBuilder();
      Long maxRefId = null;
      lSBQuery.append(" select max(changesNomineeRefId) from HstDcpsNomineeChanges where dcpsEmpId= :dcpsEmpId and dcpsChangesId= :dcpsChangesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsEmpId", dcpsEmpId);
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      maxRefId = (Long)lQuery.list().get(0);
      return maxRefId;
   }

   public List getNomineesFromHst(Long changesNomineeRefId, Long dcpsEmpId) {
      StringBuilder lSBQuery = new StringBuilder();
      List<HstDcpsNomineeChanges> NomineesHstList = null;
      lSBQuery.append(" FROM HstDcpsNomineeChanges WHERE changesNomineeRefId = :changesNomineeRefId and dcpsEmpId = :dcpsEmpId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("changesNomineeRefId", changesNomineeRefId);
      lQuery.setParameter("dcpsEmpId", dcpsEmpId);
      NomineesHstList = lQuery.list();
      return NomineesHstList;
   }

   public List getChangesFromTrnForChangesId(Long dcpsChangesId) {
      StringBuilder lSBQuery = new StringBuilder();
      List<TrnDcpsChanges> TrnDcpsChangesList = null;
      lSBQuery.append(" FROM TrnDcpsChanges WHERE dcpsChangesId = :dcpsChangesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      TrnDcpsChangesList = lQuery.list();
      return TrnDcpsChangesList;
   }

   public Boolean checkPkInTrnExistsForTheChange(String fieldName, String oldValue, Long dcpsChangesId) {
      StringBuilder lSBQuery = new StringBuilder();
      new ArrayList();
      Boolean flag = true;
      lSBQuery.append(" select dcpsChangesIdPk FROM TrnDcpsChanges WHERE fieldName = :fieldName and newValue = :oldValue and dcpsChangesId = :dcpsChangesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("fieldName", fieldName);
      lQuery.setParameter("oldValue", oldValue);
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      List<Long> tempList = lQuery.list();
      if (tempList.size() == 0 && !oldValue.equals("")) {
         flag = false;
      }

      return flag;
   }

   public Long getPksFromTrnForTheChange(String fieldName, String oldValue, Long dcpsChangesId) {
      StringBuilder lSBQuery = new StringBuilder();
      new ArrayList();
      Long trnPkId = 0L;
      Query lQuery;
      if (oldValue.equals("")) {
         lSBQuery.append(" select dcpsChangesIdPk FROM TrnDcpsChanges WHERE fieldName = :fieldName and dcpsChangesId = :dcpsChangesId");
         lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      } else {
         lSBQuery.append(" select dcpsChangesIdPk FROM TrnDcpsChanges WHERE fieldName = :fieldName and newValue = :oldValue and dcpsChangesId = :dcpsChangesId");
         lQuery = this.ghibSession.createQuery(lSBQuery.toString());
         lQuery.setParameter("oldValue", oldValue);
      }

      lQuery.setParameter("fieldName", fieldName);
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      List<Long> tempList = lQuery.list();
      trnPkId = (Long)tempList.get(0);
      return trnPkId;
   }

   public Long getPkFromTrnForTheChangeInPhotoSign(Long dcpsChangesId, String fieldName) {
      StringBuilder lSBQuery = new StringBuilder();
      new ArrayList();
      Long trnPkId = 0L;
      lSBQuery.append(" select dcpsChangesIdPk FROM TrnDcpsChanges WHERE fieldName = :fieldName and dcpsChangesId = :dcpsChangesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      lQuery.setParameter("fieldName", fieldName);
      List<Long> tempList = lQuery.list();
      trnPkId = (Long)tempList.get(0);
      return trnPkId;
   }

   public void deleteTrnVOForPk(Long TrnIdPk) {
      StringBuilder lSBQuery = new StringBuilder();
      lSBQuery.append(" delete from TrnDcpsChanges where dcpsChangesIdPk = :dcpsChangesIdPk");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsChangesIdPk", TrnIdPk);
      lQuery.executeUpdate();
   }

   public void deleteTrnVOForDcpsChangesId(Long dcpsChangesId) {
      StringBuilder lSBQuery = new StringBuilder();
      lSBQuery.append(" delete from TrnDcpsChanges where dcpsChangesId = :dcpsChangesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsChangesId", dcpsChangesId);
      lQuery.executeUpdate();
   }

   public Date getDobForTheEmployee(Long dcpsEmpId) {
      StringBuilder lSBQuery = new StringBuilder();
      new ArrayList();
      Date dob = null;
      lSBQuery.append(" select dob FROM MstEmp WHERE dcpsEmpId = :dcpsEmpId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsEmpId", dcpsEmpId);
      List<Date> tempList = lQuery.list();
      dob = (Date)tempList.get(0);
      return dob;
   }

   public Long getNextRefIdForHstNomineeChanges(Long dcpsEmpId) {
      StringBuilder lSBQuery = new StringBuilder();
      Long count = null;
      lSBQuery.append(" select max(changesNomineeRefId) from HstDcpsNomineeChanges where dcpsEmpId= :dcpsEmpId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("dcpsEmpId", dcpsEmpId);
      count = (Long)lQuery.list().get(0);
      if (count == null) {
         count = 0L;
      }

      count = count + 1L;
      return count;
   }

   public List getPhotoSignNewValue(Long lLngChangesId) {
      new ArrayList();
      StringBuilder lSBQuery = new StringBuilder();
      lSBQuery.append(" select newValue from TrnDcpsChanges where dcpsChangesId = :changesId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("changesId", lLngChangesId);
      List lLstNewValue = lQuery.list();
      return lLstNewValue;
   }

   public String getGroupIdForCadreId(Long cadreId) {
      StringBuilder lSBQuery = new StringBuilder();
      new ArrayList();
      String groupId = null;
      lSBQuery.append(" Select groupId FROM DcpsCadreMst WHERE cadreId = :cadreId");
      Query lQuery = this.ghibSession.createQuery(lSBQuery.toString());
      lQuery.setParameter("cadreId", cadreId);
      List<String> tempList = lQuery.list();
      if (tempList != null && tempList.size() != 0) {
         groupId = (String)tempList.get(0);
      }

      return groupId;
   }

   public void updateEmpNameInOrgEmpMst(MstEmp objMstEmp) {
      StringBuilder lSBQuery = new StringBuilder();
      String[] name = new String[3];
      if (objMstEmp.getName() != null) {
         name = objMstEmp.getName().split(" ");
         this.gLogger.info("name-----" + name.length);
      }

      lSBQuery.append("update ORG_EMP_MST set ");
      if (name.length > 0 && name[0] != null) {
         lSBQuery.append("EMP_FNAME = '" + name[0] + "', ");
      }

      if (name.length > 1 && name[1] != null) {
         if (name.length > 2 && name[2] != null) {
            lSBQuery.append("EMP_MNAME = '" + name[1] + "', ");
         } else {
            lSBQuery.append("EMP_LNAME = '" + name[1] + "',EMP_MNAME = '' ");
         }
      }

      if (name.length > 2 && name[2] != null) {
         lSBQuery.append("EMP_LNAME = '" + name[2] + "' ");
      } else if (name.length == 1) {
         lSBQuery.append("EMP_LNAME = ' ' ");
      }

      lSBQuery.append("where EMP_ID = " + objMstEmp.getOrgEmpMstId());
      this.logger.info("update name query------" + lSBQuery.toString());
      Query lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
      lQuery.executeUpdate();
   }

   public String getGradeForGivenLevel(String level) {
      String grade = "";
      new ArrayList();
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT ID FROM RLT_PAYBAND_GP_7PC WHERE LEVEL ='" + level + "'");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getGradeForGivenLevel " + sqlQuery);
      List<String> tempList = sqlQuery.list();
      if (tempList != null && tempList.size() != 0) {
         grade = (String)tempList.get(0);
      }

      return grade;
   }

   public String getGradeForGivenStateLevel(String level) {
      String grade = "";
      new ArrayList();
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT ID FROM RLT_PAYBAND_GP_state_7pc WHERE LEVEL ='" + level + "'");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getGradeForGivenStateLevel " + sqlQuery);
      List<String> tempList = sqlQuery.list();
      if (tempList != null && tempList.size() != 0) {
         grade = (String)tempList.get(0);
      }

      return grade;
   }

   public List getBasicAsPerGrade(String grade) {
      List basicPay = null;
      List lLstReturnList = null;
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT " + grade + ",cell FROM MST_MATRIX_7THPAY WHERE " + grade + ">0 order by GRADE_1");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getBasicAsPerGrade " + sqlQuery);
      basicPay = sqlQuery.list();
      ComboValuesVO lObjComboValuesVO = null;
      if (basicPay != null && basicPay.size() != 0) {
         new ArrayList();
         lLstReturnList = new ArrayList();

         for(int liCtr = 0; liCtr < basicPay.size(); ++liCtr) {
            Object[] obj = (Object[])basicPay.get(liCtr);
            lObjComboValuesVO = new ComboValuesVO();
            lObjComboValuesVO.setId(obj[1].toString());
            lObjComboValuesVO.setDesc(obj[0].toString());
            lLstReturnList.add(lObjComboValuesVO);
         }
      } else {
         lLstReturnList = new ArrayList();
         lObjComboValuesVO = new ComboValuesVO();
         lObjComboValuesVO.setId("-----Select-----");
         lObjComboValuesVO.setDesc("-1");
         lLstReturnList.add(lObjComboValuesVO);
      }

      return lLstReturnList;
   }

   public List getStateBasicAsPerGrade(String grade) {
      List basicPay = null;
      List lLstReturnList = null;
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT " + grade + ",cell FROM MST_STATE_MATRIX_7THPAY WHERE " + grade + ">0 order by S_1");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getBasicAsPerGrade " + sqlQuery);
      basicPay = sqlQuery.list();
      ComboValuesVO lObjComboValuesVO = null;
      if (basicPay != null && basicPay.size() != 0) {
         new ArrayList();
         lLstReturnList = new ArrayList();

         for(int liCtr = 0; liCtr < basicPay.size(); ++liCtr) {
            Object[] obj = (Object[])basicPay.get(liCtr);
            lObjComboValuesVO = new ComboValuesVO();
            lObjComboValuesVO.setId(obj[1].toString());
            lObjComboValuesVO.setDesc(obj[0].toString());
            lLstReturnList.add(lObjComboValuesVO);
         }
      } else {
         lLstReturnList = new ArrayList();
         lObjComboValuesVO = new ComboValuesVO();
         lObjComboValuesVO.setId("-----Select-----");
         lObjComboValuesVO.setDesc("-1");
         lLstReturnList.add(lObjComboValuesVO);
      }

      return lLstReturnList;
   }

   public String getPayInPayBandAndGradePayForStateLevel(String level) {
      List tempList = null;
      String x = "";
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT PAY_IN_PAYBAND ||'~'|| GRADE_PAY FROM RLT_PAYBAND_GP_STATE_7PC WHERE LEVEL ='" + level + "'");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getPayInPayBandAndGradePayForLevel " + sqlQuery);
      tempList = sqlQuery.list();
      if (tempList.size() > 0 && tempList != null) {
         x = tempList.get(0).toString();
      }

      return x;
   }

   public String getPayInPayBandAndGradePayForLevel(String level) {
      List tempList = null;
      String x = "";
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT PAY_IN_PAYBAND ||'~'|| GRADE_PAY FROM RLT_PAYBAND_GP_7PC WHERE LEVEL ='" + level + "'");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getPayInPayBandAndGradePayForLevel " + sqlQuery);
      tempList = sqlQuery.list();
      if (tempList.size() > 0 && tempList != null) {
         x = tempList.get(0).toString();
      }

      return x;
   }

   public String getScaleForLevel(String scaleStartAmt, String scaleEndAmt, String lStrGradePay) {
      List tempList = null;
      String x = "";
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT SCALE_ID FROM HR_EIS_SCALE_MST WHERE SCALE_START_AMT = '" + scaleStartAmt + "' AND SCALE_END_AMT = '" + scaleEndAmt + "' AND SCALE_GRADE_PAY = '" + lStrGradePay + "'");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getScaleForLevel " + sqlQuery);
      tempList = sqlQuery.list();
      if (tempList.size() > 0 && tempList != null) {
         x = tempList.get(0).toString();
      }

      return x;
   }

   public String getPayInPayBandAndGradePayForHTEDMELevel(String level, String tableName) {
      List tempList = null;
      String x = "";
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT PAY_IN_PAYBAND ||'~'|| GRADE_PAY FROM " + tableName + " WHERE LEVEL ='" + level + "'");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getPayInPayBandAndGradePayForLevel " + sqlQuery);
      tempList = sqlQuery.list();
      if (tempList.size() > 0 && tempList != null) {
         x = tempList.get(0).toString();
      }

      return x;
   }

   public List getdetails(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * FROM MST_EMP_GPF_ACC ");
      sb.append(" where SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for getGPFDuplEmpList is" + sql1query.toString());
      return sql1query.list();
   }

   public void updateMstEmpGpfEmpAcc(String empSevarthId, String getPfAcNo, String getPfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = getPfSeriesDesc + "/" + getPfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update MST_EMP_GPF_ACC set GPF_ACC_NO ='" + newGpfAcctNo + "',GPF_UPDATE_FLAG = 'Y' where SEVAARTH_ID = '" + empSevarthId + "'");
      this.logger.error("update gpfaccdetails------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getdetails1(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * FROM MST_GPF_YEARLY ");
      sb.append(" where SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for getGPFDuplEmpList is" + sql1query.toString());
      return sql1query.list();
   }

   public void updateMstGpfYearly(String empSevarthId, String getPfAcNo, String getPfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = getPfSeriesDesc + "/" + getPfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update MST_GPF_YEARLY set GPF_ACC_NO ='" + newGpfAcctNo + "' where SEVAARTH_ID = '" + empSevarthId + "'");
      this.logger.error("update gpfaccdetails------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getdetails11(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * FROM TRN_EMP_GPF_ACC ");
      sb.append(" where SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for getTRN_EMP_GPF_ACC is" + sql1query.toString());
      return sql1query.list();
   }

   public void updateTrnEmpGpfAcc(String empSevarthId, String getPfAcNo, String getPfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = getPfSeriesDesc + "/" + getPfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update TRN_EMP_GPF_ACC set GPF_ACC_NO ='" + newGpfAcctNo + "' where SEVAARTH_ID = '" + empSevarthId + "'");
      this.logger.error("update gpfaccdetails------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getdetailsforgpfadvance(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * FROM MST_GPF_ADVANCE ");
      sb.append(" where SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for getMST_GPF_ADVANCE is" + sql1query.toString());
      return sql1query.list();
   }

   public void updategpfadvance(String empSevarthId, String pfAcNo, String pfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = pfSeriesDesc + "/" + pfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update MST_GPF_ADVANCE set GPF_ACC_NO ='" + newGpfAcctNo + "' where SEVAARTH_ID = '" + empSevarthId + "'");
      this.logger.error("update gpfaccdetails------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getdetailsfortrngpfwithdrawal(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * FROM TRN_GPF_FINAL_WITHDRAWAL ");
      sb.append(" where SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for getTRN_GPF_FINAL_WITHDRAWAL is" + sql1query.toString());
      return sql1query.list();
   }

   public void updatetrngpfwithdrawal(String empSevarthId, String pfAcNo, String pfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = pfSeriesDesc + "/" + pfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update TRN_GPF_FINAL_WITHDRAWAL set GPF_ACC_NO ='" + newGpfAcctNo + "' where SEVAARTH_ID = '" + empSevarthId + "'");
      this.logger.error("update gpfaccdetails------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getTransactionIdDetails(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT bill.TRANSACTION_ID,adv.SEVAARTH_ID,bill.BILL_DTLS_ID FROM TRN_GPF_FINAL_WITHDRAWAL adv  ");
      sb.append(" inner join MST_GPF_BILL_DTLS bill on bill.TRANSACTION_ID = adv.TRANSACTION_ID ");
      sb.append(" where adv.SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for get transactionidforTRN_GPF_FINAL_WITHDRAWAL is" + sql1query.toString());
      return sql1query.list();
   }

   public void updateTrngpfwithdrawal(String tranid, String pfAcNo, String pfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = pfSeriesDesc + "/" + pfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update MST_GPF_BILL_DTLS set GPF_ACC_NO ='" + newGpfAcctNo + "' where TRANSACTION_ID = '" + tranid + "'");
      this.logger.error("update MST_GPF_BILL_DTLS-withdrawal-----" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getTransactionIddtsforgpfadvance(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT bill.TRANSACTION_ID,adv.SEVAARTH_ID,bill.BILL_DTLS_ID FROM MST_GPF_ADVANCE adv  ");
      sb.append(" inner join MST_GPF_BILL_DTLS bill on bill.TRANSACTION_ID = adv.TRANSACTION_ID ");
      sb.append(" where adv.SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for get transactionid for MST_GPF_ADVANCE is" + sql1query.toString());
      return sql1query.list();
   }

   public void updateTransactionidmstbilldetails(String TransId, String pfAcNo, String pfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = pfSeriesDesc + "/" + pfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update MST_GPF_BILL_DTLS set GPF_ACC_NO ='" + newGpfAcctNo + "' where TRANSACTION_ID = '" + TransId + "'");
      this.logger.error("update MST_GPF_BILL_DTLS------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getdetailsforgpfmonthly(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * FROM MST_GPF_MONTHLY ");
      sb.append(" where SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for getMST_GPF_MONTHLY is" + sql1query.toString());
      return sql1query.list();
   }

   public void updategpfmonthly(String empSevarthId, String pfAcNo, String pfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = pfSeriesDesc + "/" + pfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update MST_GPF_MONTHLY set GPF_ACC_NO ='" + newGpfAcctNo + "' where SEVAARTH_ID = '" + empSevarthId + "'");
      this.logger.error("update MST_GPF_MONTHLY------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getdetailsforgpfinterestdtls(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * FROM MST_GPF_INTEREST_DTLS ");
      sb.append(" where SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for getMST_GPF_INTEREST_DTLS is" + sql1query.toString());
      return sql1query.list();
   }

   public void updategpfinterestdtls(String empSevarthId, String pfAcNo, String pfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = pfSeriesDesc + "/" + pfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update MST_GPF_INTEREST_DTLS set GPF_ACC_NO ='" + newGpfAcctNo + "' where SEVAARTH_ID = '" + empSevarthId + "'");
      this.logger.error("update MST_GPF_INTEREST_DTLS------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getdtlsfor7Pcgpfinterest(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * FROM MST_GPF_7PC_INTEREST_DTLS ");
      sb.append(" where SEVAARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for getMST_GPF_7PC_INTEREST_DTLSis" + sql1query.toString());
      return sql1query.list();
   }

   public void update7pcgpfinterestdtls(String empSevarthId, String pfAcNo, String pfSeriesDesc) {
      Session hibSession = this.getSession();
      String newGpfAcctNo = null;
      newGpfAcctNo = pfSeriesDesc + "/" + pfAcNo;
      StringBuffer str2 = new StringBuffer();
      str2.append("update MST_GPF_7PC_INTEREST_DTLS set GPF_ACC_NO ='" + newGpfAcctNo + "' where SEVAARTH_ID = '" + empSevarthId + "'");
      this.logger.error("update MST_GPF_7PC_INTEREST_DTLS------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public List getdetailstoUpdatepanno(String empSevarthId) {
      Session hibSession = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT * FROM MST_DCPS_EMP emp ");
      sb.append(" inner join FRM_FORM_S1_DTLS frm on frm.SEVARTH_ID = emp.SEVARTH_ID ");
      sb.append(" where emp.SEVARTH_ID = '" + empSevarthId + "'");
      Query sql1query = hibSession.createSQLQuery(sb.toString());
      this.logger.error("sql query for getdetailstoUpdatepanno is" + sql1query.toString());
      return sql1query.list();
   }

   public void UpdatePanNoDcpsEmp(String empSevarthId, String panNo, String dcpsid) {
      Session hibSession = this.getSession();
      StringBuffer str2 = new StringBuffer();
      str2.append("update FRM_FORM_S1_DTLS set TIN_OR_PAN ='" + panNo + "' where SEVARTH_ID = '" + empSevarthId + "'and DCPS_ID ='" + dcpsid + "' and (STAGE is null or STAGE in (1,2,4,5)) ");
      this.logger.error("update FRM_FORM_S1_DTLS------" + str2.toString());
      Query query3 = hibSession.createSQLQuery(str2.toString());
      query3.executeUpdate();
   }

   public void updatePanDetails(String Sevaarthid, String panno) {
      try {
         StringBuilder SBQuery = new StringBuilder();
         SBQuery.append("UPDATE FRM_FORM_S1_DTLS SET TIN_OR_PAN='" + panno + "' WHERE SEVARTH_ID='" + Sevaarthid + "'and  (STAGE is null or STAGE in (1,2,4,5)) ");
         Query lQuery = this.ghibSession.createSQLQuery(SBQuery.toString());
         this.logger.info("the query is ********" + lQuery.toString());
         lQuery.executeUpdate();
      } catch (Exception var5) {
         var5.printStackTrace();
         this.logger.error("Error is :" + var5, var5);
      }

   }

   public int getStagetoUpdatepanno(String empSevarthId) {
      this.logger.error("hii getcuntOFTemp");
      Session session = this.getSession();
      StringBuffer sb = new StringBuffer();
      sb.append(" SELECT  case when frm.stage is null then 0 else frm.stage end FROM MST_DCPS_EMP emp ");
      sb.append(" inner join FRM_FORM_S1_DTLS frm on frm.SEVARTH_ID = emp.SEVARTH_ID ");
      sb.append(" where emp.SEVARTH_ID = '" + empSevarthId + "'");
      Query query = session.createSQLQuery(sb.toString());
      this.logger.error("query is getcuntOFTemp*************" + query.toString());
      String status = query.uniqueResult().toString();
      this.logger.error("get status" + status);
      int stage = Integer.parseInt(status);
      this.logger.error("get stage" + stage);
      return stage;
   }

   public int getsevarthidcount(String sevarthid, String dcpsempid) {
      StringBuilder sb = new StringBuilder();
      int count = 0;
      Session hibSession = this.getSession();
      sb.append(" SELECT COUNT(*) FROM MST_DCPS_EMP emp ");
      sb.append(" inner join FRM_FORM_S1_DTLS frm on frm.SEVARTH_ID = emp.SEVARTH_ID ");
      sb.append(" where emp.SEVARTH_ID = '" + sevarthid + "' and frm.DCPS_ID ='" + dcpsempid + "' and (frm.STAGE is null or frm.STAGE in (1,2,4,5)) ");
      Query lQuery = hibSession.createSQLQuery(sb.toString());
      this.logger.error("query1 getPAOCount----" + lQuery.toString());
      if (lQuery.uniqueResult() != null) {
         count = Integer.parseInt(lQuery.uniqueResult().toString());
      }

      return count;
   }

   public String getPayInPayBandAndGradePayForLevelRedd(String level) {
      List tempList = null;
      String x = "";
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT PAY_IN_PAYBAND ||'~'|| '0' FROM RLT_PAYBAND_GP_REDDYPCNEW WHERE LEVEL ='" + level + "'");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getPayInPayBandAndGradePayForLevel " + sqlQuery);
      tempList = sqlQuery.list();
      if (tempList.size() > 0 && tempList != null) {
         x = tempList.get(0).toString();
      }

      return x;
   }

   public String getScaleForLevelRedd(String scaleStartAmt, String scaleEndAmt, String lStrGradePay) {
      List tempList = null;
      String x = "";
      Session hibSession = this.getSession();
      StringBuilder SBQuery = new StringBuilder();
      SBQuery.append(" SELECT SCALE_ID FROM HR_EIS_SCALE_MST WHERE SCALE_START_AMT = '" + scaleStartAmt + "' AND SCALE_END_AMT = '" + scaleEndAmt + "' AND commission_id = 2500348 ");
      Query sqlQuery = hibSession.createSQLQuery(SBQuery.toString());
      this.gLogger.info("query for getScaleForLevel " + sqlQuery);
      tempList = sqlQuery.list();
      if (tempList.size() > 0 && tempList != null) {
         x = tempList.get(0).toString();
      }

      return x;
   }
}