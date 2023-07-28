package com.tcs.sgv.common.dao;

import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class GpfLnaDashboardDaoImpl
  extends GenericDaoHibernateImpl
  implements GpfLnaDashboardDao
{
  Session ghibSession = null;
  Log glogger = LogFactory.getLog(getClass());
  private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");
  
  public GpfLnaDashboardDaoImpl(Class type, SessionFactory sessionFactory)
  {
    super(type);
    this.ghibSession = sessionFactory.getCurrentSession();
    setSessionFactory(sessionFactory);
  }
  
  public String getCount(String lStrPostId)
  {
    this.logger.info("getEmployeeDetailForApprover called");
    this.logger.info("lStrPostId" + lStrPostId);
    
    List empListForDeoAppover = new ArrayList();
    List advanceRequestList = new ArrayList();
    List houseAdvanceRequestList = new ArrayList();
    
    StringBuilder lSBQueryCA = new StringBuilder();
    StringBuilder lSBQueryMA = new StringBuilder();
    StringBuilder lSBQueryHA = new StringBuilder();
    
    String lStrCount = "";
    
    lSBQueryCA.append(" select CA.transactionId,CA.applicationDate,CA.sevaarthId,ME.name,'800028',CA.computerAdvanceId,CLM.lookupName,CA.frwrdToRHO");
    lSBQueryCA.append(" FROM MstLnaCompAdvance CA,WfJobMst WJ, OrgEmpMst OEM, MstEmp ME,CmnLookupMst CLM");
    lSBQueryCA.append(" WHERE CA.sevaarthId = ME.sevarthId AND CA.statusFlag = 'F' AND OEM.empId = ME.orgEmpMstId");
    lSBQueryCA.append(" AND WJ.jobRefId = CA.computerAdvanceId AND WJ.lstActPostId = :postId AND WJ.wfDocMst.docId = :docId");
    lSBQueryCA.append(" AND CLM.lookupId = CA.advanceSubType");
    
    Query lQuery = this.ghibSession.createQuery(lSBQueryCA.toString());
    lQuery.setParameter("docId", Long.valueOf(Long.parseLong(this.gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST"))));
    lQuery.setParameter("postId", lStrPostId);
    empListForDeoAppover = lQuery.list();
    
    lSBQueryMA.append(" select MCA.transactionId,MCA.applicationDate,MCA.sevaarthId,ME.name,'800030',MCA.motorAdvanceId,CLM.lookupName,MCA.frwrdToRHO");
    lSBQueryMA.append(" FROM MstLnaMotorAdvance MCA,WfJobMst WJ, OrgEmpMst OEM, MstEmp ME,CmnLookupMst CLM");
    lSBQueryMA.append(" WHERE MCA.sevaarthId = ME.sevarthId AND MCA.statusFlag = 'F' AND OEM.empId = ME.orgEmpMstId");
    lSBQueryMA.append(" AND WJ.jobRefId = MCA.motorAdvanceId AND WJ.lstActPostId = :postId AND WJ.wfDocMst.docId = :docId");
    lSBQueryMA.append(" AND CLM.lookupId = MCA.advanceSubType");
    
    Query lQueryForAdvance = this.ghibSession.createQuery(lSBQueryMA.toString());
    lQueryForAdvance.setParameter("docId", Long.valueOf(Long.parseLong(this.gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST"))));
    lQueryForAdvance.setParameter("postId", lStrPostId);
    advanceRequestList = lQueryForAdvance.list();
    
    lSBQueryHA.append(" select HBA.transactionId,HBA.applicationDate,HBA.sevaarthId,ME.name,'800029',HBA.houseAdvanceId,CLM.lookupName,HBA.frwrdToRHO");
    lSBQueryHA.append(" FROM MstLnaHouseAdvance HBA,WfJobMst WJ, OrgEmpMst OEM, MstEmp ME,CmnLookupMst CLM");
    lSBQueryHA.append(" WHERE HBA.sevaarthId = ME.sevarthId AND HBA.statusFlag = 'F' AND OEM.empId = ME.orgEmpMstId");
    lSBQueryHA.append(" AND WJ.jobRefId = HBA.houseAdvanceId AND WJ.lstActPostId = :postId AND WJ.wfDocMst.docId = :docId");
    lSBQueryHA.append(" AND CLM.lookupId = HBA.advanceSubType");
    
    Query lQueryForHouseAdvance = this.ghibSession.createQuery(lSBQueryHA.toString());
    lQueryForHouseAdvance.setParameter("docId", Long.valueOf(Long.parseLong(this.gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST"))));
    lQueryForHouseAdvance.setParameter("postId", lStrPostId);
    houseAdvanceRequestList = lQueryForHouseAdvance.list();
    
    empListForDeoAppover.addAll(advanceRequestList);
    empListForDeoAppover.addAll(houseAdvanceRequestList);
    
    lStrCount = String.valueOf(empListForDeoAppover.size());
    return lStrCount;
  }
  
  public String getUserType(Long lLngUserId)
  {
    String type = "";
    List tempList = null;
    StringBuilder lSBQuery = new StringBuilder();
    
    lSBQuery.append(" SELECT detail.ROLE_NAME FROM ORG_USER_MST user, ORG_USERPOST_RLT post, ACL_POSTROLE_RLT role, ACL_ROLE_DETAILS_RLT detail ");
    lSBQuery.append(" where user.USER_ID=post.USER_ID and post.POST_ID=role.POST_ID and role.ROLE_ID=detail.ROLE_ID ");
    lSBQuery.append(" and user.USER_ID=:lLngUserId ");
    
    Query lQuery = this.ghibSession.createSQLQuery(lSBQuery.toString());
    lQuery.setParameter("lLngUserId", lLngUserId);
    tempList = lQuery.list();
    if ((tempList != null) && (tempList.size() > 0) && (tempList.get(0) != null)) {
      type = tempList.get(0).toString();
    }
    return type;
  }
  
  public String getLNANoOfApprovedRequests(String lStrLocCode, String gStrPostId, String lStrUser)
  {
    this.glogger.info("lStrLocCode" + lStrLocCode);
    this.glogger.info("gStrPostId" + gStrPostId);
    this.glogger.info("lStrUser" + lStrUser);
    
    List lLnaApprovedListCA = new ArrayList();
    List lLnaApprovedListHBA = new ArrayList();
    List lLnaApprovedListMCA = new ArrayList();
    StringBuilder lSBQueryCA = new StringBuilder();
    StringBuilder lSBQueryHBA = new StringBuilder();
    StringBuilder lSBQueryMCA = new StringBuilder();
    
    String lStrCount = "";
    
    Query lQueryCA = null;
    if (lStrUser.equals("HOD 2"))
    {
      lSBQueryCA.append("SELECT CA.transactionId,CA.applicationDate,CA.sevaarthId,ME.name,CA.amountSanctioned,CA.advanceType,CLM.lookupName,");
      lSBQueryCA.append(" CA.lnaBillId FROM MstEmp ME,MstLnaCompAdvance CA,CmnLookupMst CLM,OrgDdoMst DDO");
      lSBQueryCA.append(" where CA.statusFlag = 'A' AND ME.sevarthId = CA.sevaarthId AND CA.advanceSubType = CLM.lookupId");
      lSBQueryCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :LocCode");
      lQueryCA = this.ghibSession.createQuery(lSBQueryCA.toString());
      lQueryCA.setParameter("LocCode", lStrLocCode);
    }
    if (lStrUser.equals("HO"))
    {
      lSBQueryCA.append("SELECT CA.transactionId,CA.applicationDate,CA.sevaarthId,ME.name,CA.amountSanctioned,CA.advanceType,CLM.lookupName,");
      lSBQueryCA.append(" CA.lnaBillId from MstEmp ME, MstLnaCompAdvance CA,CmnLookupMst CLM ");
      lSBQueryCA.append(" where CA.statusFlag = 'A' AND CA.advanceSubType = CLM.lookupId AND ME.sevarthId = CA.sevaarthId ");
      lSBQueryCA.append(" AND CA.toPostID = " + gStrPostId);
      lQueryCA = this.ghibSession.createQuery(lSBQueryCA.toString());
    }
    lLnaApprovedListCA = lQueryCA.list();
    
    Query lQueryHBA = null;
    if (lStrUser.equals("HOD 2"))
    {
      lSBQueryHBA.append("SELECT HA.transactionId,HA.applicationDate,HA.sevaarthId,ME.name,HA.amountSanctioned,HA.advanceType,CLM.lookupName,");
      lSBQueryHBA.append(" HA.lnaBillId,HA.advanceSubType,HA.disbursementOne FROM MstEmp ME,MstLnaHouseAdvance HA,CmnLookupMst CLM,OrgDdoMst DDO");
      lSBQueryHBA.append(" where (HA.statusFlag = 'A' or (HA.statusFlag='A1' and HA.advanceSubType = 800038) ");
      lSBQueryHBA.append(" or (HA.statusFlag = 'A1' and HA.advanceSubType = 800058))");
      lSBQueryHBA.append(" AND ME.sevarthId = HA.sevaarthId AND HA.advanceSubType = CLM.lookupId");
      lSBQueryHBA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :LocCode");
      lQueryHBA = this.ghibSession.createQuery(lSBQueryHBA.toString());
      lQueryHBA.setParameter("LocCode", lStrLocCode);
    }
    if (lStrUser.equals("HO"))
    {
      lSBQueryHBA.append("SELECT HA.transactionId,HA.applicationDate,HA.sevaarthId,ME.name,HA.amountSanctioned,HA.advanceType,CLM.lookupName,");
      lSBQueryHBA.append(" HA.lnaBillId,HA.advanceSubType,HA.disbursementOne from MstEmp ME, MstLnaHouseAdvance HA,CmnLookupMst CLM ");
      lSBQueryHBA.append(" where (HA.statusFlag = 'A' or (HA.statusFlag='A1' and HA.advanceSubType = 800038) ");
      lSBQueryHBA.append(" or (HA.statusFlag = 'A1' and HA.advanceSubType = 800058))");
      lSBQueryHBA.append("  AND ME.sevarthId = HA.sevaarthId AND  HA.advanceSubType = CLM.lookupId  ");
      lSBQueryHBA.append(" AND HA.toPostID = " + gStrPostId);
      lQueryHBA = this.ghibSession.createQuery(lSBQueryHBA.toString());
    }
    lLnaApprovedListHBA = lQueryHBA.list();
    
    Query lQueryMCA = null;
    if (lStrUser.equals("HOD 2"))
    {
      lSBQueryMCA.append("SELECT MA.transactionId,MA.applicationDate,MA.sevaarthId,ME.name,MA.amountSanctioned,MA.advanceType,CLM.lookupName,");
      lSBQueryMCA.append(" MA.lnaBillId FROM MstEmp ME,MstLnaMotorAdvance MA,CmnLookupMst CLM,OrgDdoMst DDO");
      lSBQueryMCA.append(" where MA.statusFlag = 'A' AND ME.sevarthId = MA.sevaarthId AND MA.advanceSubType = CLM.lookupId");
      lSBQueryMCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :LocCode");
      lQueryMCA = this.ghibSession.createQuery(lSBQueryMCA.toString());
      lQueryMCA.setParameter("LocCode", lStrLocCode);
    }
    if (lStrUser.equals("HO"))
    {
      lSBQueryMCA.append("SELECT MA.transactionId,MA.applicationDate,MA.sevaarthId,ME.name,MA.amountSanctioned,MA.advanceType,CLM.lookupName,");
      lSBQueryMCA.append(" MA.lnaBillId FROM MstEmp ME, MstLnaMotorAdvance MA,CmnLookupMst CLM ");
      lSBQueryMCA.append(" where MA.statusFlag = 'A' AND MA.advanceSubType = CLM.lookupId AND ME.sevarthId = MA.sevaarthId ");
      lSBQueryMCA.append(" AND MA.toPostID = " + gStrPostId);
      lQueryMCA = this.ghibSession.createQuery(lSBQueryMCA.toString());
    }
    lLnaApprovedListMCA = lQueryMCA.list();
    
    lLnaApprovedListCA.addAll(lLnaApprovedListHBA);
    lLnaApprovedListCA.addAll(lLnaApprovedListMCA);
    
    lStrCount = String.valueOf(lLnaApprovedListCA.size());
    
    return lStrCount;
  }
  
  public String getNoOfOrdersGenerated(String lStrLocCode, String gStrPostId, String lStrUser)
  {
    this.glogger.info("lStrLocCode" + lStrLocCode);
    this.glogger.info("gStrPostId" + gStrPostId);
    this.glogger.info("lStrUser" + lStrUser);
    
    List lLnaApprovedListCA = new ArrayList();
    List lLnaApprovedListHBA = new ArrayList();
    List lLnaApprovedListMCA = new ArrayList();
    StringBuilder lSBQueryCA = new StringBuilder();
    StringBuilder lSBQueryHBA = new StringBuilder();
    StringBuilder lSBQueryMCA = new StringBuilder();
    
    String lStrCount = "";
    
    Query lQueryCA = null;
    if (lStrUser.equals("HOD 2"))
    {
      lSBQueryCA.append("SELECT CA.transactionId,CA.applicationDate,CA.sevaarthId,ME.name,CA.amountSanctioned,CA.advanceType,CLM.lookupName,");
      lSBQueryCA.append(" CA.lnaBillId FROM MstEmp ME,MstLnaCompAdvance CA,CmnLookupMst CLM,OrgDdoMst DDO");
      lSBQueryCA.append(" where CA.statusFlag = 'A' AND ME.sevarthId = CA.sevaarthId AND CA.advanceSubType = CLM.lookupId");
      lSBQueryCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :LocCode and CA.lnaBillId is not null");
      lQueryCA = this.ghibSession.createQuery(lSBQueryCA.toString());
      lQueryCA.setParameter("LocCode", lStrLocCode);
    }
    if (lStrUser.equals("HO"))
    {
      lSBQueryCA.append("SELECT CA.transactionId,CA.applicationDate,CA.sevaarthId,ME.name,CA.amountSanctioned,CA.advanceType,CLM.lookupName,");
      lSBQueryCA.append(" CA.lnaBillId from MstEmp ME, MstLnaCompAdvance CA,CmnLookupMst CLM ");
      lSBQueryCA.append(" where CA.statusFlag = 'A' AND CA.advanceSubType = CLM.lookupId AND ME.sevarthId = CA.sevaarthId and CA.lnaBillId is not null ");
      lSBQueryCA.append(" AND CA.toPostID = " + gStrPostId);
      lQueryCA = this.ghibSession.createQuery(lSBQueryCA.toString());
    }
    lLnaApprovedListCA = lQueryCA.list();
    
    Query lQueryHBA = null;
    if (lStrUser.equals("HOD 2"))
    {
      lSBQueryHBA.append("SELECT HA.transactionId,HA.applicationDate,HA.sevaarthId,ME.name,HA.amountSanctioned,HA.advanceType,CLM.lookupName,");
      lSBQueryHBA.append(" HA.lnaBillId,HA.advanceSubType,HA.disbursementOne FROM MstEmp ME,MstLnaHouseAdvance HA,CmnLookupMst CLM,OrgDdoMst DDO");
      lSBQueryHBA.append(" where (HA.statusFlag = 'A' or (HA.statusFlag='A1' and HA.advanceSubType = 800038) ");
      lSBQueryHBA.append(" or (HA.statusFlag = 'A1' and HA.advanceSubType = 800058))");
      lSBQueryHBA.append(" AND ME.sevarthId = HA.sevaarthId AND HA.advanceSubType = CLM.lookupId");
      lSBQueryHBA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :LocCode and HA.lnaBillId is not null");
      lQueryHBA = this.ghibSession.createQuery(lSBQueryHBA.toString());
      lQueryHBA.setParameter("LocCode", lStrLocCode);
    }
    if (lStrUser.equals("HO"))
    {
      lSBQueryHBA.append("SELECT HA.transactionId,HA.applicationDate,HA.sevaarthId,ME.name,HA.amountSanctioned,HA.advanceType,CLM.lookupName,");
      lSBQueryHBA.append(" HA.lnaBillId,HA.advanceSubType,HA.disbursementOne from MstEmp ME, MstLnaHouseAdvance HA,CmnLookupMst CLM ");
      lSBQueryHBA.append(" where (HA.statusFlag = 'A' or (HA.statusFlag='A1' and HA.advanceSubType = 800038) ");
      lSBQueryHBA.append(" or (HA.statusFlag = 'A1' and HA.advanceSubType = 800058))");
      lSBQueryHBA.append("  AND ME.sevarthId = HA.sevaarthId AND  HA.advanceSubType = CLM.lookupId and HA.lnaBillId is not null");
      lSBQueryHBA.append(" AND HA.toPostID = " + gStrPostId);
      lQueryHBA = this.ghibSession.createQuery(lSBQueryHBA.toString());
    }
    lLnaApprovedListHBA = lQueryHBA.list();
    
    Query lQueryMCA = null;
    if (lStrUser.equals("HOD 2"))
    {
      lSBQueryMCA.append("SELECT MA.transactionId,MA.applicationDate,MA.sevaarthId,ME.name,MA.amountSanctioned,MA.advanceType,CLM.lookupName,");
      lSBQueryMCA.append(" MA.lnaBillId FROM MstEmp ME,MstLnaMotorAdvance MA,CmnLookupMst CLM,OrgDdoMst DDO");
      lSBQueryMCA.append(" where MA.statusFlag = 'A' AND ME.sevarthId = MA.sevaarthId AND MA.advanceSubType = CLM.lookupId");
      lSBQueryMCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.hodLocCode = :LocCode AND MA.lnaBillId is not null");
      lQueryMCA = this.ghibSession.createQuery(lSBQueryMCA.toString());
      lQueryMCA.setParameter("LocCode", lStrLocCode);
    }
    if (lStrUser.equals("HO"))
    {
      lSBQueryMCA.append("SELECT MA.transactionId,MA.applicationDate,MA.sevaarthId,ME.name,MA.amountSanctioned,MA.advanceType,CLM.lookupName,");
      lSBQueryMCA.append(" MA.lnaBillId FROM MstEmp ME, MstLnaMotorAdvance MA,CmnLookupMst CLM ");
      lSBQueryMCA.append(" where MA.statusFlag = 'A' AND MA.advanceSubType = CLM.lookupId AND ME.sevarthId = MA.sevaarthId AND MA.lnaBillId is not null");
      lSBQueryMCA.append(" AND MA.toPostID = " + gStrPostId);
      lQueryMCA = this.ghibSession.createQuery(lSBQueryMCA.toString());
    }
    lLnaApprovedListMCA = lQueryMCA.list();
    
    lLnaApprovedListCA.addAll(lLnaApprovedListHBA);
    lLnaApprovedListCA.addAll(lLnaApprovedListMCA);
    
    lStrCount = String.valueOf(lLnaApprovedListCA.size());
    
    return lStrCount;
  }
  
  public String getLNADraftRequestCount(String lStrHodLocCode, String gStrPostId)
  {
    List empDraftList = new ArrayList();
    List empDraftList2 = new ArrayList();
    List empDraftList3 = new ArrayList();
    StringBuilder lSBQueryCA = new StringBuilder();
    StringBuilder lSBQueryMCA = new StringBuilder();
    StringBuilder lSBQueryHBA = new StringBuilder();
    
    lSBQueryCA.append("select ME.name,CA.sevaarthId,'800028',CLM.lookupName,CA.applicationDate,CA.statusFlag,CA.computerAdvanceId,CA.hoRemarks,CA.createdDate,CA.rhoRemarks,CA.approverRemarks");
    lSBQueryCA.append(" FROM MstLnaCompAdvance CA, MstEmp ME,CmnLookupMst CLM,OrgDdoMst DDO");
    lSBQueryCA.append(" WHERE CA.sevaarthId = ME.sevarthId AND CA.statusFlag IN ('D','R')");
    lSBQueryCA.append(" AND CLM.lookupId = CA.advanceSubType");
    lSBQueryCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode AND CA.toPostID = :toPostId ");
    
    Query lQuery = this.ghibSession.createQuery(lSBQueryCA.toString());
    
    lQuery.setParameter("hodLocCode", lStrHodLocCode);
    lQuery.setParameter("toPostId", gStrPostId);
    empDraftList = lQuery.list();
    
    lSBQueryMCA.append("select ME.name,MCA.sevaarthId,'800030',CLM.lookupName,MCA.applicationDate,MCA.statusFlag,MCA.motorAdvanceId,MCA.hoRemarks,MCA.createdDate,MCA.rhoRemarks,MCA.approverRemarks");
    lSBQueryMCA.append(" FROM MstLnaMotorAdvance MCA, MstEmp ME,CmnLookupMst CLM,OrgDdoMst DDO");
    lSBQueryMCA.append(" WHERE MCA.sevaarthId = ME.sevarthId AND MCA.statusFlag IN ('D','R')");
    lSBQueryMCA.append(" AND CLM.lookupId = MCA.advanceSubType");
    lSBQueryMCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode AND MCA.toPostID = :toPostId ");
    
    Query lQueryMCA = this.ghibSession.createQuery(lSBQueryMCA.toString());
    
    lQueryMCA.setParameter("hodLocCode", lStrHodLocCode);
    lQueryMCA.setParameter("toPostId", gStrPostId);
    empDraftList2 = lQueryMCA.list();
    
    lSBQueryHBA.append("select ME.name,HBA.sevaarthId,'800029',CLM.lookupName,HBA.applicationDate,HBA.statusFlag,HBA.houseAdvanceId,HBA.hoRemarks,HBA.createdDate,HBA.rhoRemarks,HBA.approverRemarks");
    lSBQueryHBA.append(" FROM MstLnaHouseAdvance HBA, MstEmp ME,CmnLookupMst CLM,OrgDdoMst DDO");
    lSBQueryHBA.append(" WHERE HBA.sevaarthId = ME.sevarthId AND HBA.statusFlag IN ('D','R')");
    lSBQueryHBA.append(" AND CLM.lookupId = HBA.advanceSubType");
    lSBQueryHBA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode AND HBA.toPostID = :toPostId ");
    
    Query lQueryHBA = this.ghibSession.createQuery(lSBQueryHBA.toString());
    lQueryHBA.setParameter("hodLocCode", lStrHodLocCode);
    lQueryHBA.setParameter("toPostId", gStrPostId);
    
    empDraftList3 = lQueryHBA.list();
    
    empDraftList.addAll(empDraftList2);
    empDraftList.addAll(empDraftList3);
    
    String DraftCount = String.valueOf(empDraftList.size());
    
    return DraftCount;
  }
  
  public String getRejectedReqCount(String lStrHodLocCode, String gStrPostId)
  {
    List empDraftList = new ArrayList();
    List empDraftList2 = new ArrayList();
    List empDraftList3 = new ArrayList();
    StringBuilder lSBQueryCA = new StringBuilder();
    StringBuilder lSBQueryMCA = new StringBuilder();
    StringBuilder lSBQueryHBA = new StringBuilder();
    
    lSBQueryCA.append("select ME.name,CA.sevaarthId,'800028',CLM.lookupName,CA.applicationDate,CA.statusFlag,CA.computerAdvanceId,CA.hoRemarks,CA.createdDate,CA.rhoRemarks,CA.approverRemarks");
    lSBQueryCA.append(" FROM MstLnaCompAdvance CA, MstEmp ME,CmnLookupMst CLM,OrgDdoMst DDO");
    lSBQueryCA.append(" WHERE CA.sevaarthId = ME.sevarthId AND CA.statusFlag = 'R' ");
    lSBQueryCA.append(" AND CLM.lookupId = CA.advanceSubType");
    lSBQueryCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode AND CA.toPostID = :toPostId ");
    
    Query lQuery = this.ghibSession.createQuery(lSBQueryCA.toString());
    
    lQuery.setParameter("hodLocCode", lStrHodLocCode);
    lQuery.setParameter("toPostId", gStrPostId);
    empDraftList = lQuery.list();
    
    lSBQueryMCA.append("select ME.name,MCA.sevaarthId,'800030',CLM.lookupName,MCA.applicationDate,MCA.statusFlag,MCA.motorAdvanceId,MCA.hoRemarks,MCA.createdDate,MCA.rhoRemarks,MCA.approverRemarks");
    lSBQueryMCA.append(" FROM MstLnaMotorAdvance MCA, MstEmp ME,CmnLookupMst CLM,OrgDdoMst DDO");
    lSBQueryMCA.append(" WHERE MCA.sevaarthId = ME.sevarthId AND MCA.statusFlag = 'R' ");
    lSBQueryMCA.append(" AND CLM.lookupId = MCA.advanceSubType");
    lSBQueryMCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode AND MCA.toPostID = :toPostId ");
    
    Query lQueryMCA = this.ghibSession.createQuery(lSBQueryMCA.toString());
    
    lQueryMCA.setParameter("hodLocCode", lStrHodLocCode);
    lQueryMCA.setParameter("toPostId", gStrPostId);
    empDraftList2 = lQueryMCA.list();
    
    lSBQueryHBA.append("select ME.name,HBA.sevaarthId,'800029',CLM.lookupName,HBA.applicationDate,HBA.statusFlag,HBA.houseAdvanceId,HBA.hoRemarks,HBA.createdDate,HBA.rhoRemarks,HBA.approverRemarks");
    lSBQueryHBA.append(" FROM MstLnaHouseAdvance HBA, MstEmp ME,CmnLookupMst CLM,OrgDdoMst DDO");
    lSBQueryHBA.append(" WHERE HBA.sevaarthId = ME.sevarthId AND HBA.statusFlag = 'R' ");
    lSBQueryHBA.append(" AND CLM.lookupId = HBA.advanceSubType");
    lSBQueryHBA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode AND HBA.toPostID = :toPostId ");
    
    Query lQueryHBA = this.ghibSession.createQuery(lSBQueryHBA.toString());
    lQueryHBA.setParameter("hodLocCode", lStrHodLocCode);
    lQueryHBA.setParameter("toPostId", gStrPostId);
    
    empDraftList3 = lQueryHBA.list();
    
    empDraftList.addAll(empDraftList2);
    empDraftList.addAll(empDraftList3);
    
    String DraftCount = String.valueOf(empDraftList.size());
    
    return DraftCount;
  }
}
