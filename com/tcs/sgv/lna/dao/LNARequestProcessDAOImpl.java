package com.tcs.sgv.lna.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tcs.sgv.apps.common.valuebeans.ComboValuesVO;
import com.tcs.sgv.common.service.IFMSCommonServiceImpl;
import com.tcs.sgv.common.utils.DBUtility;
import com.tcs.sgv.core.dao.GenericDaoHibernateImpl;
import com.tcs.sgv.dcps.dao.NewRegDdoDAO;
import com.tcs.sgv.dcps.dao.NewRegDdoDAOImpl;
import com.tcs.sgv.eis.valueobject.HrEisScaleMst;
import com.tcs.sgv.lna.valueobject.MstLnaCompAdvance;

public class LNARequestProcessDAOImpl extends GenericDaoHibernateImpl implements LNARequestProcessDAO {
	Session ghibSession = null;
	private ResourceBundle gObjRsrcBndle = ResourceBundle.getBundle("resources/lna/LNAConstants");
	Log gLogger = LogFactory.getLog(getClass());
	public LNARequestProcessDAOImpl(Class type, SessionFactory sessionFactory) {
		super(type);
		ghibSession = sessionFactory.getCurrentSession();
		setSessionFactory(sessionFactory);
	}

	public List getGPFEmployeeDetail(Long lLngEmpId) {
		List lnaEmpList = new ArrayList();
		try {

			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("select ME.name,CLM.lookupName,ME.gender,ME.basicPay,ME.orgEmpMstId,ME.group,ODM.dsgnName,");
			lSBQuery.append("HRGD.pfSeries || '/' || HRGD.gpfAccNo,ME.dob,ME.doj,ME.appointmentDate,ME.building_address,");
			lSBQuery.append("ME.building_street,ME.landmark,ME.district,CSM1.stateName,ME.pincode,ME.cntctNo,ME.cellNo,");
			lSBQuery.append("OEM.empSrvcExp,DO.dcpsDdoOfficeName,DO.dcpsDdoOfficeAddress1,DO.dcpsDdoOfficeVillage,CTM.talukaName,CDM.districtName,CSM.stateName,");
			lSBQuery.append("DO.dcpsDdoOfficeTelNo1,DO.dcpsDdoOfficeTelNo2");
			lSBQuery.append(" FROM MstEmp ME,HrPayGpfBalanceDtls HRGD,OrgEmpMst OEM,OrgDesignationMst ODM,CmnLookupMst CLM,DdoOffice DO,");
			lSBQuery.append("CmnStateMst CSM,CmnStateMst CSM1,CmnDistrictMst CDM,CmnTalukaMst CTM WHERE");
			lSBQuery.append(" CLM.lookupId = ME.payCommission AND ME.currOff = DO.dcpsDdoOfficeIdPk AND CSM1.stateId = ME.state");
			lSBQuery.append(" AND CSM.stateId = DO.dcpsDdoOfficeState AND CDM.districtId = DO.dcpsDdoOfficeDistrict AND CTM.talukaId=DO.dcpsDdoOfficeTaluka");
			lSBQuery.append(" and ME.orgEmpMstId = OEM.empId and HRGD.userId = OEM.orgUserMst.userId and ME.designation = ODM.dsgnId and OEM.empId = :empId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("empId", lLngEmpId);
			lnaEmpList = lQuery.list();

		} catch (Exception e) {

			logger.error(" Error is : " + e, e);

		}
		return lnaEmpList;
	}

	public List getDCPSEmployeeDetail(Long lLngEmpId) {
		List lnaEmpList = new ArrayList();
		try {

			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("select ME.name,CLM.lookupName,ME.gender,ME.basicPay,ME.orgEmpMstId,ME.group,ODM.dsgnName,");
			lSBQuery.append("ME.dcpsId,ME.dob,ME.doj,ME.appointmentDate,ME.building_address,");
			lSBQuery.append("ME.building_street,ME.landmark,ME.district,CSM1.stateName,ME.pincode,ME.cntctNo,ME.cellNo,");
			lSBQuery.append("OEM.empSrvcExp,DO.dcpsDdoOfficeName,DO.dcpsDdoOfficeAddress1,DO.dcpsDdoOfficeVillage,CTM.talukaName,CDM.districtName,CSM.stateName,");
			lSBQuery.append("DO.dcpsDdoOfficeTelNo1,DO.dcpsDdoOfficeTelNo2");
			lSBQuery.append(" FROM MstEmp ME,OrgEmpMst OEM,OrgDesignationMst ODM,CmnLookupMst CLM,DdoOffice DO,");
			lSBQuery.append("CmnStateMst CSM,CmnStateMst CSM1,CmnDistrictMst CDM,CmnTalukaMst CTM WHERE");
			lSBQuery.append(" CLM.lookupId = ME.payCommission AND ME.currOff = DO.dcpsDdoOfficeIdPk AND CSM1.stateId = ME.state");
			lSBQuery.append(" AND CSM.stateId = DO.dcpsDdoOfficeState AND CDM.districtId = DO.dcpsDdoOfficeDistrict AND CTM.talukaId=DO.dcpsDdoOfficeTaluka");
			lSBQuery.append(" and ME.orgEmpMstId = OEM.empId and ME.designation = ODM.dsgnId and OEM.empId = :empId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("empId", lLngEmpId);
			lnaEmpList = lQuery.list();

		} catch (Exception e) {

			logger.error(" Error is : " + e, e);

		}
		return lnaEmpList;
	}

	public List getEmployeeDetailForApprover(String lStrPostId) {
		List empListForDeoAppover = new ArrayList();
		List advanceRequestList = new ArrayList();
		List houseAdvanceRequestList = new ArrayList();
		StringBuilder lSBQueryCA = new StringBuilder();
		StringBuilder lSBQueryMA = new StringBuilder();
		StringBuilder lSBQueryHA = new StringBuilder();
		lSBQueryCA.append("select CA.transactionId,CA.applicationDate,CA.sevaarthId,ME.name,'800028',CA.computerAdvanceId,CLM.lookupName,CA.frwrdToRHO");
		lSBQueryCA.append(" FROM MstLnaCompAdvance CA,WfJobMst WJ, OrgEmpMst OEM, MstEmp ME,CmnLookupMst CLM");
		lSBQueryCA.append(" WHERE CA.sevaarthId = ME.sevarthId AND CA.statusFlag = 'F' AND OEM.empId = ME.orgEmpMstId");
		lSBQueryCA.append(" AND WJ.jobRefId = CA.computerAdvanceId AND WJ.lstActPostId = :postId AND WJ.wfDocMst.docId = :docId");
		lSBQueryCA.append(" AND CLM.lookupId = CA.advanceSubType");
		Query lQuery = ghibSession.createQuery(lSBQueryCA.toString());
		lQuery.setParameter("docId", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
		lQuery.setParameter("postId", lStrPostId);
		empListForDeoAppover = lQuery.list();

		lSBQueryMA.append("select MCA.transactionId,MCA.applicationDate,MCA.sevaarthId,ME.name,'800030',MCA.motorAdvanceId,CLM.lookupName,MCA.frwrdToRHO");
		lSBQueryMA.append(" FROM MstLnaMotorAdvance MCA,WfJobMst WJ, OrgEmpMst OEM, MstEmp ME,CmnLookupMst CLM");
		lSBQueryMA.append(" WHERE MCA.sevaarthId = ME.sevarthId AND MCA.statusFlag = 'F' AND OEM.empId = ME.orgEmpMstId");
		lSBQueryMA.append(" AND WJ.jobRefId = MCA.motorAdvanceId AND WJ.lstActPostId = :postId AND WJ.wfDocMst.docId = :docId");
		lSBQueryMA.append(" AND CLM.lookupId = MCA.advanceSubType");
		Query lQueryForAdvance = ghibSession.createQuery(lSBQueryMA.toString());
		lQueryForAdvance.setParameter("docId", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
		lQueryForAdvance.setParameter("postId", lStrPostId);
		advanceRequestList = lQueryForAdvance.list();

		lSBQueryHA.append("select HBA.transactionId,HBA.applicationDate,HBA.sevaarthId,ME.name,'800029',HBA.houseAdvanceId,CLM.lookupName,HBA.frwrdToRHO");
		lSBQueryHA.append(" FROM MstLnaHouseAdvance HBA,WfJobMst WJ, OrgEmpMst OEM, MstEmp ME,CmnLookupMst CLM");
		lSBQueryHA.append(" WHERE HBA.sevaarthId = ME.sevarthId AND HBA.statusFlag = 'F' AND OEM.empId = ME.orgEmpMstId");
		lSBQueryHA.append(" AND WJ.jobRefId = HBA.houseAdvanceId AND WJ.lstActPostId = :postId AND WJ.wfDocMst.docId = :docId");
		lSBQueryHA.append(" AND CLM.lookupId = HBA.advanceSubType");
		Query lQueryForHouseAdvance = ghibSession.createQuery(lSBQueryHA.toString());
		lQueryForHouseAdvance.setParameter("docId", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
		lQueryForHouseAdvance.setParameter("postId", lStrPostId);
		houseAdvanceRequestList = lQueryForHouseAdvance.list();

		empListForDeoAppover.addAll(advanceRequestList);
		empListForDeoAppover.addAll(houseAdvanceRequestList);
		return empListForDeoAppover;
	}

	public String getNewTransactionId(String lStrSevaarthId, Long lLngAdvanceType) {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList<Long>();
		String lStrTrnsId = "";
		String lStrMonth = "";
		Long count = 0L;

		Calendar cal = Calendar.getInstance();

		Integer lIntMonth = cal.get(Calendar.MONTH) + 1;
		Integer lIntYear = cal.get(Calendar.YEAR);
		if (lIntMonth.toString().length() == 1) {
			lStrMonth = "0" + lIntMonth;
		} else {
			lStrMonth = lIntMonth.toString();
		}
		lStrTrnsId = lStrSevaarthId.charAt(0) + lStrMonth + lIntYear.toString().substring(2, 4);

		lSBQuery.append(" select count(*) FROM MstLnaRequest WHERE transactionId LIKE :lStrTrnsId");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrTrnsId", lStrTrnsId + '%');
		tempList = lQuery.list();
		count = tempList.get(0);
		return String.format(lStrTrnsId + "%06d", count + 1);
	}

	public Boolean checkEligibilityForLNA(String lStrSevaarthId) {
		Date empDOJ;
		Integer lInt = null;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("select ME.doj");
			lSBQuery.append(" FROM MstEmp ME");
			lSBQuery.append(" WHERE ME.sevarthId = :SevaarthId");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId);
			empDOJ = (Date) lQuery.uniqueResult();
			Date futureDate = empDOJ;
			futureDate.setYear(futureDate.getYear() + 5);
			Date currDate = new Date();
			lInt = currDate.compareTo(futureDate);
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		if (lInt < 0) {
			return true;
		} else {
			return false;
		}

	}

	public HrEisScaleMst getPayScaleData(Long empId) {
		HrEisScaleMst hrOtherInfo = new HrEisScaleMst();
		String lStrQuery = "select  empLookup.hrEisSgdMpg.hrEisScaleMst  from HrEisOtherDtls as empLookup where empLookup.hrEisEmpMst.orgEmpMst.empId = :empId";
		Query lQuery = ghibSession.createQuery(lStrQuery);
		lQuery.setParameter("empId", empId);
		hrOtherInfo = (HrEisScaleMst) lQuery.uniqueResult();
		return hrOtherInfo;
	}

	public List getCheckList(String lStrSevaarthId, Long lLngReqType, Long lLngReqSubType) {
		StringBuilder lSBQuery = new StringBuilder();
		List documentCheckList = new ArrayList();
		lSBQuery.append("select checklistName,checked");
		lSBQuery.append(" FROM MstLnaDocChecklist");
		lSBQuery.append(" WHERE sevaarthID = :SevaarthId AND lnaReqType = :ReqType AND reqSubType = :ReqSubType ORDER BY MstLnaDocChecklistId");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("SevaarthId", lStrSevaarthId);
		lQuery.setParameter("ReqType", lLngReqType);
		lQuery.setParameter("ReqSubType", lLngReqSubType);
		documentCheckList = lQuery.list();
		return documentCheckList;
	}

	public List getChecklistPk(String lStrSevaarthId, Long lLngRequestType, Long lLngReqSubType) {
		StringBuilder lSBQuery = new StringBuilder();
		List checkListPk = new ArrayList();
		lSBQuery.append("select MstLnaDocChecklistId");
		lSBQuery.append(" FROM MstLnaDocChecklist");
		lSBQuery.append(" WHERE sevaarthID = :SevaarthId AND lnaReqType = :ReqType AND reqSubType = :ReqSubType");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("SevaarthId", lStrSevaarthId);
		lQuery.setParameter("ReqType", lLngRequestType);
		lQuery.setParameter("ReqSubType", lLngReqSubType);
		checkListPk = lQuery.list();
		return checkListPk;
	}

	public List getEmployeeDcpsOrGpf(String lStrSevaarthId, String empName, String criteria, String lStrDdoCode, String lStrHodLocCode,String lStrUser) {
		List lLstdcpsOrGpf;
		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery;
		lSBQuery.append("select ME.dcpsOrGpf,ME.orgEmpMstId,ME.sevarthId");
		lSBQuery.append(" FROM MstEmp ME,OrgDdoMst ODM WHERE ME.ddoCode = ODM.ddoCode");
		if(lStrUser.equals("HOD"))
			lSBQuery.append("   AND ODM.hodLocCode = :HodLocCode ");
		if (criteria.equals("1")) {
			lSBQuery.append(" AND ME.sevarthId = :SevaarthId ");
			if (lStrDdoCode != null && !"".equals(lStrDdoCode)) {
				lSBQuery.append(" AND ME.ddoCode = :ddoCode");
			}
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId.toUpperCase().trim());
		} else if (criteria.equals("2")) {
			lSBQuery.append(" AND UPPER(ME.name) = :empName");
			if (lStrDdoCode != null && !"".equals(lStrDdoCode)) {
				lSBQuery.append(" AND ME.ddoCode = :ddoCode");
			}
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("empName", empName.toUpperCase().trim());
		} else {
			lSBQuery.append(" AND ME.sevarthId = :SevaarthId AND  UPPER(ME.name) = :empName");
			if (lStrDdoCode != null && !"".equals(lStrDdoCode)) {
				lSBQuery.append(" AND ME.ddoCode = :ddoCode");
			}
			lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId.toUpperCase().trim());
			lQuery.setParameter("empName", empName.toUpperCase().trim());
		}
		if (lStrDdoCode != null && !"".equals(lStrDdoCode)) {
			lQuery.setParameter("ddoCode", lStrDdoCode);
		}
		if(lStrUser.equals("HOD"))
			lQuery.setParameter("HodLocCode", lStrHodLocCode);
		
		lLstdcpsOrGpf = lQuery.list();
		return lLstdcpsOrGpf;
	}

	public List getEmpNameForAutoComplete(String searchKey, String lStrDdoCode, String lStrHodLocCode,String lStrUser) {
		ArrayList<ComboValuesVO> finalList = new ArrayList<ComboValuesVO>();
		ComboValuesVO cmbVO;
		Object[] obj;

		StringBuilder lSBQuery = new StringBuilder();
		Query lQuery = null;
		lSBQuery.append("select ME.orgEmpMstId,ME.name from MstEmp ME,OrgDdoMst ODM where UPPER(ME.name) LIKE :searchKey");
		lSBQuery.append("  AND ME.ddoCode = ODM.ddoCode ");
		//lSBQuery.append("   AND ODM.hodLocCode = :HodLocCode");
		if (lStrDdoCode != null && !"".equals(lStrDdoCode)) {
			lSBQuery.append(" and ODM.ddoCode = :ddoCode");
		}
		lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("searchKey", '%' + searchKey + '%');
		//lQuery.setParameter("HodLocCode", lStrHodLocCode);
		if (lStrDdoCode != null && !"".equals(lStrDdoCode)) {
			lQuery.setParameter("ddoCode", lStrDdoCode);
		}
		List resultList = lQuery.list();

		if (resultList != null && !resultList.isEmpty()) {
			Iterator it = resultList.iterator();
			while (it.hasNext()) {
				cmbVO = new ComboValuesVO();
				obj = (Object[]) it.next();
				cmbVO.setId(obj[1].toString());
				cmbVO.setDesc(obj[1].toString());
				finalList.add(cmbVO);
			}
		}

		return finalList;
	}

	public List getDraftRequestList(String lStrCriteria, String lStrName, Date lDtSaveDate, String lStrHodLocCode, String gStrPostId) {
		List empDraftList = new ArrayList();
		List empDraftList2 = new ArrayList();
		List empDraftList3 = new ArrayList();
		StringBuilder lSBQueryCA = new StringBuilder();
		StringBuilder lSBQueryMCA = new StringBuilder();
		StringBuilder lSBQueryHBA = new StringBuilder();
		Query lQuery;
		Query lQueryMCA;
		Query lQueryHBA;
		Date lDtToDate = null;
		if (lDtSaveDate != null) {
			lDtToDate = (Date) lDtSaveDate.clone();
			Date lDtTemp = lDtSaveDate;
			lDtToDate.setDate(lDtTemp.getDate() + 1);
		}
		lSBQueryCA.append("select ME.name,CA.sevaarthId,'800028',CLM.lookupName,CA.applicationDate,CA.statusFlag,CA.computerAdvanceId,CA.hoRemarks,CA.createdDate,CA.rhoRemarks,CA.approverRemarks");
		lSBQueryCA.append(" FROM MstLnaCompAdvance CA, MstEmp ME,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryCA.append(" WHERE CA.sevaarthId = ME.sevarthId AND CA.statusFlag IN ('D','R')");
		lSBQueryCA.append(" AND CLM.lookupId = CA.advanceSubType");
		lSBQueryCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode AND CA.toPostID = :toPostId ");
		if (lStrCriteria.equalsIgnoreCase("name")) {
			lSBQueryCA.append(" AND UPPER(ME.name) = :name ");
			lQuery = ghibSession.createQuery(lSBQueryCA.toString());
			lQuery.setParameter("name", lStrName.toUpperCase().trim());
		} else if (lStrCriteria.equalsIgnoreCase("date")) {
			lSBQueryCA.append(" AND CA.createdDate >= :fromDate AND CA.createdDate <= :toDate");
			lQuery = ghibSession.createQuery(lSBQueryCA.toString());
			lQuery.setDate("fromDate", lDtSaveDate);
			lQuery.setDate("toDate", lDtToDate);
		} else if (lStrCriteria.equalsIgnoreCase("both")) {
			lSBQueryCA.append(" AND UPPER(ME.name) = :name ");
			lSBQueryCA.append(" AND CA.createdDate >= :fromDate AND CA.createdDate <= :toDate");
			lQuery = ghibSession.createQuery(lSBQueryCA.toString());
			lQuery.setParameter("name", lStrName.toUpperCase().trim());
			lQuery.setDate("fromDate", lDtSaveDate);
			lQuery.setDate("toDate", lDtToDate);
		} else {
			lQuery = ghibSession.createQuery(lSBQueryCA.toString());
		}
		lQuery.setParameter("hodLocCode", lStrHodLocCode);
		lQuery.setParameter("toPostId", gStrPostId);
		empDraftList = lQuery.list();

		lSBQueryMCA.append("select ME.name,MCA.sevaarthId,'800030',CLM.lookupName,MCA.applicationDate,MCA.statusFlag,MCA.motorAdvanceId,MCA.hoRemarks,MCA.createdDate,MCA.rhoRemarks,MCA.approverRemarks");
		lSBQueryMCA.append(" FROM MstLnaMotorAdvance MCA, MstEmp ME,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryMCA.append(" WHERE MCA.sevaarthId = ME.sevarthId AND MCA.statusFlag IN ('D','R')");
		lSBQueryMCA.append(" AND CLM.lookupId = MCA.advanceSubType");
		lSBQueryMCA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode AND MCA.toPostID = :toPostId ");
		if (lStrCriteria.equalsIgnoreCase("name")) {
			lSBQueryMCA.append(" AND UPPER(ME.name) = :name ");
			lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
			lQueryMCA.setParameter("name", lStrName.toUpperCase().trim());
		} else if (lStrCriteria.equalsIgnoreCase("date")) {
			lSBQueryMCA.append(" AND MCA.createdDate >= :fromDate AND MCA.createdDate <= :toDate");
			lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
			lQueryMCA.setDate("fromDate", lDtSaveDate);
			lQueryMCA.setDate("toDate", lDtToDate);
		} else if (lStrCriteria.equalsIgnoreCase("both")) {
			lSBQueryMCA.append(" AND UPPER(ME.name) = :name ");
			lSBQueryMCA.append(" AND MCA.createdDate >= :fromDate AND MCA.createdDate <= :toDate");
			lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
			lQueryMCA.setParameter("name", lStrName.toUpperCase().trim());
			lQueryMCA.setDate("fromDate", lDtSaveDate);
			lQueryMCA.setDate("toDate", lDtToDate);
		} else {
			lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
		}
		lQueryMCA.setParameter("hodLocCode", lStrHodLocCode);
		lQueryMCA.setParameter("toPostId", gStrPostId);
		empDraftList2 = lQueryMCA.list();

		lSBQueryHBA.append("select ME.name,HBA.sevaarthId,'800029',CLM.lookupName,HBA.applicationDate,HBA.statusFlag,HBA.houseAdvanceId,HBA.hoRemarks,HBA.createdDate,HBA.rhoRemarks,HBA.approverRemarks");
		lSBQueryHBA.append(" FROM MstLnaHouseAdvance HBA, MstEmp ME,CmnLookupMst CLM,OrgDdoMst DDO");
		lSBQueryHBA.append(" WHERE HBA.sevaarthId = ME.sevarthId AND HBA.statusFlag IN ('D','R')");
		lSBQueryHBA.append(" AND CLM.lookupId = HBA.advanceSubType");
		lSBQueryHBA.append(" AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = :hodLocCode AND HBA.toPostID = :toPostId ");
		if (lStrCriteria.equalsIgnoreCase("name")) {
			lSBQueryHBA.append(" AND UPPER(ME.name) = :name ");
			lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());
			lQueryHBA.setParameter("name", lStrName.toUpperCase().trim());
		} else if (lStrCriteria.equalsIgnoreCase("date")) {
			lSBQueryHBA.append(" AND HBA.createdDate >= :fromDate AND HBA.createdDate <= :toDate");
			lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());
			lQueryHBA.setDate("fromDate", lDtSaveDate);
			lQueryHBA.setDate("toDate", lDtToDate);
		} else if (lStrCriteria.equalsIgnoreCase("both")) {
			lSBQueryHBA.append(" AND UPPER(ME.name) = :name ");
			lSBQueryHBA.append(" AND HBA.createdDate >= :fromDate AND HBA.createdDate <= :toDate");
			lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());
			lQueryHBA.setParameter("name", lStrName.toUpperCase().trim());
			lQueryHBA.setDate("fromDate", lDtSaveDate);
			lQueryHBA.setDate("toDate", lDtToDate);
		} else {
			lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());

		}
		lQueryHBA.setParameter("hodLocCode", lStrHodLocCode);
		lQueryHBA.setParameter("toPostId", gStrPostId);
		empDraftList3 = lQueryHBA.list();

		empDraftList.addAll(empDraftList2);
		empDraftList.addAll(empDraftList3);
		return empDraftList;

	}

	public List getEmpLoanDetails(Long lLngUserId) {
		List lLnaStatusListCA = new ArrayList();
		List lLnaStatusListHBA = new ArrayList();
		List lLnaStatusListMCA = new ArrayList();
		StringBuilder lSBQueryCA = new StringBuilder();
		StringBuilder lSBQueryHBA = new StringBuilder();
		StringBuilder lSBQueryMCA = new StringBuilder();

		lSBQueryCA.append("SELECT ME.name,CA.applicationDate,CA.sevaarthId,CA.advanceType,CLM.lookupName,CA.statusFlag");
		lSBQueryCA.append(" FROM MstEmp ME,MstLnaCompAdvance CA,CmnLookupMst CLM,OrgEmpMst ORM");
		lSBQueryCA.append(" where ORM.orgUserMst.userId = :UserId AND ORM.empId = ME.orgEmpMstId");
		lSBQueryCA.append(" AND ME.sevarthId = CA.sevaarthId AND CLM.lookupId = CA.advanceSubType ORDER BY 2");
		Query lQueryCA = ghibSession.createQuery(lSBQueryCA.toString());
		lQueryCA.setLong("UserId", lLngUserId);
		lLnaStatusListCA = lQueryCA.list();

		lSBQueryHBA.append("SELECT ME.name,HA.applicationDate,HA.sevaarthId,HA.advanceType,CLM.lookupName,HA.statusFlag");
		lSBQueryHBA.append(" FROM MstEmp ME,MstLnaHouseAdvance HA,CmnLookupMst CLM,OrgEmpMst ORM");
		lSBQueryHBA.append(" where ORM.orgUserMst.userId = :UserId AND ORM.empId = ME.orgEmpMstId");
		lSBQueryHBA.append(" AND ME.sevarthId = HA.sevaarthId AND CLM.lookupId = HA.advanceSubType ORDER BY 2");
		Query lQueryHBA = ghibSession.createQuery(lSBQueryHBA.toString());
		lQueryHBA.setLong("UserId", lLngUserId);
		lLnaStatusListHBA = lQueryHBA.list();

		lSBQueryMCA.append("SELECT ME.name,MA.applicationDate,MA.sevaarthId,MA.advanceType,CLM.lookupName,MA.statusFlag");
		lSBQueryMCA.append(" FROM MstEmp ME,MstLnaMotorAdvance MA,CmnLookupMst CLM,OrgEmpMst ORM");
		lSBQueryMCA.append(" where ORM.orgUserMst.userId = :UserId AND ORM.empId = ME.orgEmpMstId");
		lSBQueryMCA.append(" AND ME.sevarthId = MA.sevaarthId AND CLM.lookupId = MA.advanceSubType  ORDER BY 2");
		Query lQueryMCA = ghibSession.createQuery(lSBQueryMCA.toString());
		lQueryMCA.setLong("UserId", lLngUserId);
		lLnaStatusListMCA = lQueryMCA.list();

		lLnaStatusListCA.addAll(lLnaStatusListHBA);
		lLnaStatusListCA.addAll(lLnaStatusListMCA);

		return lLnaStatusListCA;
	}

	public List getEmpBankDetails(String lStrSevaarthId) {
		List lLstEmpBankDtls = new ArrayList();
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT MBP.bankName,RBB.branchName,ME.bankAccountNo,ME.IFSCCode");
			lSBQuery.append(" FROM MstEmp ME,RltBankBranchPay RBB,MstBankPay MBP");
			lSBQuery.append(" WHERE ME.sevarthId = :SevaarthId and MBP.bankCode = ME.bankName and RBB.branchId = ME.branchName");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId);
			lLstEmpBankDtls = lQuery.list();
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstEmpBankDtls;
	}

	
	public String getEmpNameFromSevaarthId(String lStrSevaarthId) {
		List<String> lLstEmpName = new ArrayList<String>();
		String lStrEmpName = "";
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT name");
			lSBQuery.append(" FROM MstEmp WHERE sevarthId = :SevaarthId ");			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId);
			lLstEmpName = lQuery.list();
			if(!lLstEmpName.isEmpty())
				lStrEmpName = lLstEmpName.get(0);
			
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lStrEmpName;
	}
	public List<ComboValuesVO> getAllDepartment(Long lLngLocationCode, Long langId) throws Exception {
		List<ComboValuesVO> lLstDepartnent = new ArrayList<ComboValuesVO>();
		StringBuilder lStrQuery = new StringBuilder();
		ComboValuesVO cmbVO;
		List lLstResultList = null;
		Iterator itr;
		Object[] obj;

		try {

			lStrQuery.append(" SELECT clm.locId,clm.locName FROM CmnLocationMst clm");
			lStrQuery.append(" WHERE clm.parentLocId = :LocationCode  ");			
			lStrQuery.append(" and clm.cmnLanguageMst.langId =:langId order by clm.locName");
			Query hqlQuery = ghibSession.createQuery(lStrQuery.toString());
			hqlQuery.setLong("langId", langId);
			hqlQuery.setLong("LocationCode", lLngLocationCode);
			hqlQuery.setCacheable(true).setCacheRegion("ecache_lookup");
			lLstResultList = hqlQuery.list();			
			if (lLstResultList != null && lLstResultList.size() > 0) {
				itr = lLstResultList.iterator();
				while (itr.hasNext()) {
					cmbVO = new ComboValuesVO();
					obj = (Object[]) itr.next();
					cmbVO.setId(obj[0].toString());
					cmbVO.setDesc(obj[1].toString().replaceAll("&", "And"));
					lLstDepartnent.add(cmbVO);
				}
			}
		} catch (Exception e) {
			logger.error("Error is :" + e, e);
			throw e;
		}
		return lLstDepartnent;
	}

	public List getEmployeeDetailForApprover(Long lLngOfficeId, Date lDtFromDate, Date lDtToDate, String lStrPostId) {
		List empListForDeoAppover = new ArrayList();
		List advanceRequestList = new ArrayList();
		List houseAdvanceRequestList = new ArrayList();
		StringBuilder lSBQueryCA = new StringBuilder();
		StringBuilder lSBQueryMA = new StringBuilder();
		StringBuilder lSBQueryHA = new StringBuilder();
		lSBQueryCA.append("select CA.transactionId,CA.applicationDate,CA.sevaarthId,ME.name,'800028',CA.computerAdvanceId,CLM.lookupName,CA.frwrdToRHO");
		lSBQueryCA.append(" FROM MstLnaCompAdvance CA,WfJobMst WJ, OrgEmpMst OEM, MstEmp ME,CmnLookupMst CLM");
		
		if(lLngOfficeId != null && lLngOfficeId != 0l)
			lSBQueryCA.append(" ,OrgDdoMst DDO,CmnLocationMst LOC");
					
		lSBQueryCA.append(" WHERE CA.sevaarthId = ME.sevarthId AND CA.statusFlag = 'F' AND OEM.empId = ME.orgEmpMstId");
		lSBQueryCA.append(" AND WJ.jobRefId = CA.computerAdvanceId AND WJ.lstActPostId = :postId AND WJ.wfDocMst.docId = :docId");
		lSBQueryCA.append(" AND CLM.lookupId = CA.advanceSubType ");
		
		if(lLngOfficeId != null && lLngOfficeId != 0l)
			lSBQueryCA.append(" AND LOC.locId = :lLngOfficeId AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = LOC.locId");
		
		if(lDtFromDate != null && lDtToDate != null)
			lSBQueryCA.append(" AND CA.applicationDate BETWEEN :lDtFromDate AND :lDtToDate ");
		
		Query lQuery = ghibSession.createQuery(lSBQueryCA.toString());
		lQuery.setParameter("docId", Long.parseLong(gObjRsrcBndle.getString("LNA.CompAdvanceIDHODASST")));
		lQuery.setParameter("postId", lStrPostId);
		
		if(lLngOfficeId != null && lLngOfficeId != 0l)
			lQuery.setParameter("lLngOfficeId", lLngOfficeId);
		
		if(lDtFromDate != null && lDtToDate != null){
			lQuery.setDate("lDtFromDate", lDtFromDate);
			lQuery.setDate("lDtToDate", lDtToDate);
		}
		
		empListForDeoAppover = lQuery.list();

		lSBQueryMA.append("select MCA.transactionId,MCA.applicationDate,MCA.sevaarthId,ME.name,'800030',MCA.motorAdvanceId,CLM.lookupName,MCA.frwrdToRHO");
		lSBQueryMA.append(" FROM MstLnaMotorAdvance MCA,WfJobMst WJ, OrgEmpMst OEM, MstEmp ME,CmnLookupMst CLM");
		
		if(lLngOfficeId != null && lLngOfficeId != 0l)
			lSBQueryMA.append(" ,OrgDdoMst DDO,CmnLocationMst LOC");
		
		lSBQueryMA.append(" WHERE MCA.sevaarthId = ME.sevarthId AND MCA.statusFlag = 'F' AND OEM.empId = ME.orgEmpMstId");
		lSBQueryMA.append(" AND WJ.jobRefId = MCA.motorAdvanceId AND WJ.lstActPostId = :postId AND WJ.wfDocMst.docId = :docId");
		lSBQueryMA.append(" AND CLM.lookupId = MCA.advanceSubType");
		
		if(lLngOfficeId != null && lLngOfficeId != 0l)
			lSBQueryMA.append(" AND LOC.locId = :lLngOfficeId AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = LOC.locId");
		
		if(lDtFromDate != null && lDtToDate != null)
			lSBQueryMA.append(" AND MCA.applicationDate BETWEEN :lDtFromDate AND :lDtToDate ");
		
		Query lQueryForAdvance = ghibSession.createQuery(lSBQueryMA.toString());
		lQueryForAdvance.setParameter("docId", Long.parseLong(gObjRsrcBndle.getString("LNA.MotorAdvanceIDHODASST")));
		lQueryForAdvance.setParameter("postId", lStrPostId);
		
		if(lLngOfficeId != null && lLngOfficeId != 0l)
			lQueryForAdvance.setParameter("lLngOfficeId", lLngOfficeId);
		
		if(lDtFromDate != null && lDtToDate != null){
			lQueryForAdvance.setDate("lDtFromDate", lDtFromDate);
			lQueryForAdvance.setDate("lDtToDate", lDtToDate);
		}
		
		advanceRequestList = lQueryForAdvance.list();

		lSBQueryHA.append("select HBA.transactionId,HBA.applicationDate,HBA.sevaarthId,ME.name,'800029',HBA.houseAdvanceId,CLM.lookupName,HBA.frwrdToRHO");
		lSBQueryHA.append(" FROM MstLnaHouseAdvance HBA,WfJobMst WJ, OrgEmpMst OEM, MstEmp ME,CmnLookupMst CLM");
		
		if(lLngOfficeId != null && lLngOfficeId != 0l)
			lSBQueryHA.append(" ,OrgDdoMst DDO,CmnLocationMst LOC");
		
		lSBQueryHA.append(" WHERE HBA.sevaarthId = ME.sevarthId AND HBA.statusFlag = 'F' AND OEM.empId = ME.orgEmpMstId");
		lSBQueryHA.append(" AND WJ.jobRefId = HBA.houseAdvanceId AND WJ.lstActPostId = :postId AND WJ.wfDocMst.docId = :docId");
		lSBQueryHA.append(" AND CLM.lookupId = HBA.advanceSubType");
		
		if(lLngOfficeId != null && lLngOfficeId != 0l)
			lSBQueryHA.append(" AND LOC.locId = :lLngOfficeId AND ME.ddoCode = DDO.ddoCode AND DDO.locationCode = LOC.locId");
		
		if(lDtFromDate != null && lDtToDate != null)
			lSBQueryHA.append(" AND HBA.applicationDate BETWEEN :lDtFromDate AND :lDtToDate ");
		
		Query lQueryForHouseAdvance = ghibSession.createQuery(lSBQueryHA.toString());
		lQueryForHouseAdvance.setParameter("docId", Long.parseLong(gObjRsrcBndle.getString("LNA.HouseAdvanceIDHODASST")));
		lQueryForHouseAdvance.setParameter("postId", lStrPostId);
		
		if(lLngOfficeId != null && lLngOfficeId != 0l)
			lQueryForHouseAdvance.setParameter("lLngOfficeId", lLngOfficeId);
		
		if(lDtFromDate != null && lDtToDate != null){
			lQueryForHouseAdvance.setDate("lDtFromDate", lDtFromDate);
			lQueryForHouseAdvance.setDate("lDtToDate", lDtToDate);
		}
		
		houseAdvanceRequestList = lQueryForHouseAdvance.list();

		empListForDeoAppover.addAll(advanceRequestList);
		empListForDeoAppover.addAll(houseAdvanceRequestList);
		return empListForDeoAppover;
	}
	
	public String generateOrderNo() {

		StringBuilder lSBQuery = new StringBuilder();
		List<Long> tempList = new ArrayList<Long>();
		String lStrTrnsId = "";		
		Long count = 0L;

		SimpleDateFormat lObjDateFormat = new SimpleDateFormat("MMyy");
		Date lDtCurrDate = DBUtility.getCurrentDateFromDB();
		String lStrCurrDate = lObjDateFormat.format(lDtCurrDate);
		
		lStrTrnsId = lStrCurrDate + "/";

		lSBQuery.append(" select count(*) FROM MstLnaOrderReq WHERE orderNo LIKE :lStrTrnsId");
		Query lQuery = ghibSession.createQuery(lSBQuery.toString());
		lQuery.setParameter("lStrTrnsId", lStrTrnsId + '%');
		tempList = lQuery.list();
		count = tempList.get(0);
		return String.format(lStrTrnsId + "%06d", count + 1);
	}
	
	public Boolean isPriLoanMappedWithDDO(String lStrSevaarthId, Long lLngLoanType) {
		List<String> lLstPriLoanId = new ArrayList<String>();
		Boolean lBIsMapped = false;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT l.LOAN_ADV_NAME FROM mst_dcps_emp dcp,ORG_DDO_MST ddo,HR_PAY_COMPONENT_GRP_MST g,HR_PAY_LOC_COMPONENT_MPG c ");
			lSBQuery.append(",MST_LNA_PAYROLL_LOAN_TYPE_MPG m,HR_LOAN_ADV_MST l ");
			lSBQuery.append("where dcp.SEVARTH_ID = :SevaarthId and dcp.DDO_CODE = ddo.DDO_CODE and ddo.LOCATION_CODE = g.LOC_ID and g.COMPO_GROUP_ID = c.COMPO_GRP_ID ");
			lSBQuery.append("and c.ISACTIVE = 1 and m.LNA_LOAN_ID = :lLngLoanType and c.COMPO_ID = m.PAYROLL_PRI_LOAN_ID and l.LOAN_ADV_ID = m.PAYROLL_PRI_LOAN_ID ");			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId);
			lQuery.setParameter("lLngLoanType", lLngLoanType);
			lLstPriLoanId = lQuery.list();
			if(!lLstPriLoanId.isEmpty())
				lBIsMapped = true;
			
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lBIsMapped;
	}
	
	public Boolean isIntLoanMappedWithDDO(String lStrSevaarthId, Long lLngLoanType) {
		List<String> lLstIntLoanId = new ArrayList<String>();
		Boolean lBIsMapped = false;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT l.LOAN_ADV_NAME FROM mst_dcps_emp dcp,ORG_DDO_MST ddo,HR_PAY_COMPONENT_GRP_MST g,HR_PAY_LOC_COMPONENT_MPG c ");
			lSBQuery.append(",MST_LNA_PAYROLL_LOAN_TYPE_MPG m,HR_LOAN_ADV_MST l ");
			lSBQuery.append("where dcp.SEVARTH_ID = :SevaarthId and dcp.DDO_CODE = ddo.DDO_CODE and ddo.LOCATION_CODE = g.LOC_ID and g.COMPO_GROUP_ID = c.COMPO_GRP_ID ");
			lSBQuery.append("and c.ISACTIVE = 1 and m.LNA_LOAN_ID = :lLngLoanType and c.COMPO_ID = m.PAYROLL_INT_LOAN_ID and l.LOAN_ADV_ID = m.PAYROLL_INT_LOAN_ID ");			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("SevaarthId", lStrSevaarthId);
			lQuery.setParameter("lLngLoanType", lLngLoanType);
			lLstIntLoanId = lQuery.list();
			if(!lLstIntLoanId.isEmpty())
				lBIsMapped = true;
			
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lBIsMapped;
	}

	
	public String getAdvNameFromId(Long lLngLoanId) {
		List<String> lLstLoanName = new ArrayList<String>();
		String lStrLoanName = "";
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT loanAdvName");
			lSBQuery.append(" FROM HrLoanAdvMst WHERE loanAdvId = :loanAdvId ");			
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("loanAdvId", lLngLoanId);
			lLstLoanName = lQuery.list();
			if(!lLstLoanName.isEmpty())
				lStrLoanName = lLstLoanName.get(0);
			
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lStrLoanName;
	}

	
	public String getDdoCodeFromLocCode(String lStrLocationCode) {
		String lStrDdoCode = null;
		List lLstDdoDtls = null;

		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT OD.ddoCode");
			lSBQuery.append(" FROM  OrgDdoMst OD");
			lSBQuery.append(" WHERE OD.locationCode = :lStrLocationCode ");
			Query lQuery = ghibSession.createQuery(lSBQuery.toString());
			lQuery.setParameter("lStrLocationCode", lStrLocationCode);

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
			logger.error("Error is :" + e, e);

		}
		return lStrDdoCode;
}
	
	public String getSevarthIDfromEmpName(String lStrName)
	{
		//SELECT SEVARTH_ID FROM MST_DCPS_EMP where EMP_NAME like 'ANIL MALLESHA PATIL' 
		List<String> lLstID = new ArrayList<String>();
       List empID=null;
         String sevarthID="";
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT SEVARTH_ID");
			lSBQuery.append(" FROM MST_DCPS_EMP WHERE EMP_NAME=:EMP_NAME");			
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("EMP_NAME",lStrName);
			lLstID = lQuery.list();
			System.out.println("size:" +lLstID.size());
			if(!lLstID.isEmpty())
				sevarthID = lLstID.get(0).toString();
			System.out.println("sevId:" +sevarthID);
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return sevarthID;
	}
	
	

	public Boolean isPriLoanAlreadyTaken(String lStrSevaarthId, Long lLngRequestType) {
		List<String> lLstPrilLoan = new ArrayList<String>();
		Boolean isPriLoan = false;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT hl.EMP_ID,hl.LOAN_TYPE_ID,md.EMP_NAME FROM HR_LOAN_EMP_DTLS  hl,HR_LOAN_ADV_MST l");
	        lSBQuery.append(",MST_LNA_PAYROLL_LOAN_TYPE_MPG m,HR_EIS_EMP_MST he,MST_DCPS_EMP md");
			lSBQuery.append(" where md.SEVARTH_ID =:SEVARTH_ID");
			lSBQuery.append(" and m.LNA_LOAN_ID = :LNA_LOAN_ID"); 
			lSBQuery.append(" and l.LOAN_ADV_ID = m.PAYROLL_PRI_LOAN_ID and hl.LOAN_TYPE_ID=m.PAYROLL_PRI_LOAN_ID");
			lSBQuery.append(" and hl.emp_id = he.emp_id and he.EMP_MPG_ID = md.ORG_EMP_MST_ID");
			lSBQuery.append(" and hl.LOAN_ACTIVATE_FLAG=1");
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			System.out.println("query:" +lSBQuery.toString());
			System.out.println("sevID:" +lStrSevaarthId);
			System.out.println("requesttype:" +lLngRequestType);
			lQuery.setParameter("SEVARTH_ID",lStrSevaarthId);
			lQuery.setParameter("LNA_LOAN_ID",lLngRequestType);
			lLstPrilLoan = lQuery.list();
			System.out.println("count:" +lLstPrilLoan.size());
			if(!lLstPrilLoan.isEmpty())
				isPriLoan=true;
			else
				isPriLoan=false;
			System.out.println("is priloan:" +isPriLoan);
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		
		return isPriLoan;
	}

	public Boolean isIntLoanAlreadyTaken(String lStrSevaarthId, Long lLngRequestType) {
		List lLstIntLoan = null;
		Boolean isIntLoan = false;
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT hl.EMP_ID,hl.LOAN_TYPE_ID,md.EMP_NAME FROM HR_LOAN_EMP_DTLS  hl,HR_LOAN_ADV_MST l");
	        lSBQuery.append(",MST_LNA_PAYROLL_LOAN_TYPE_MPG m,HR_EIS_EMP_MST he,MST_DCPS_EMP md");
			lSBQuery.append(" where md.SEVARTH_ID =:SEVARTH_ID");
			lSBQuery.append(" and m.LNA_LOAN_ID = :LNA_LOAN_ID"); 
			lSBQuery.append(" and l.LOAN_ADV_ID = m.PAYROLL_INT_LOAN_ID and hl.LOAN_TYPE_ID=m.PAYROLL_INT_LOAN_ID");
			lSBQuery.append(" and hl.emp_id = he.emp_id and he.EMP_MPG_ID = md.ORG_EMP_MST_ID");
			lSBQuery.append(" and hl.LOAN_ACTIVATE_FLAG=1");
						
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());
			lQuery.setParameter("SEVARTH_ID",lStrSevaarthId);
			lQuery.setParameter("LNA_LOAN_ID",lLngRequestType);
			lLstIntLoan = lQuery.list();
			if(!lLstIntLoan.isEmpty())
				isIntLoan=true;
			else
				isIntLoan=false;
			
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return isIntLoan;
	}
	
	public List getHROList(Long locationCode) {
		List lLstHROLst = null;
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append("SELECT emp.EMP_FNAME || ' '||  emp.EMP_MNAME || ' ' || emp.EMP_LNAME , mst2.LOC_ID,mst2.LOC_NAME,role.POST_ID,ddo.DDO_CODE,ddo.DDO_NAME,district.DISTRICT_NAME FROM CMN_LOCATION_MST mst1  ");
	        lSBQuery.append(" inner join CMN_LOCATION_MST mst2 on mst1.PARENT_LOC_ID = mst2.PARENT_LOC_ID ");
			lSBQuery.append(" inner join ORG_POST_DETAILS_RLT post on post.LOC_ID = mst2.LOC_ID  ");
			lSBQuery.append(" inner join ACL_POSTROLE_RLT role on post.POST_id = role.POST_ID "); 
			lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.POST_ID = role.POST_ID and usr.ACTIVATE_FLAG = 1 ");
			lSBQuery.append(" inner join org_emp_mst emp on emp.user_id = usr.user_id ");
			lSBQuery.append(" inner join org_ddo_mst ddo on ddo.location_code = mst2.LOC_ID ");	
			lSBQuery.append(" inner join CMN_DISTRICT_MST district on mst2.LOC_DISTRICT_ID = district.DISTRICT_ID ");
			lSBQuery.append(" where  mst2.DEPARTMENT_ID = 100007 and role.ROLE_ID = 800002 and role.ACTIVATE_FLAG = 1 and ");
			lSBQuery.append(" mst1.LOCATION_CODE = "+locationCode+" and mst2.LOC_ID not in ("+locationCode+", 380001) ");	
			logger.error(" getHROList query.. : " + lSBQuery);
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());			
			if(lQuery != null && lQuery.list().size() > 0){
				lLstHROLst = lQuery.list();
			}						
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return lLstHROLst;
	}
	
	public Long getHODPostId(Long locationCode) {
		Long hodPostId = null;
		
		try {
			StringBuilder lSBQuery = new StringBuilder();
			lSBQuery.append(" SELECT post.POST_ID FROM ORG_POST_DETAILS_RLT post inner join ACL_POSTROLE_RLT role ");	
			lSBQuery.append(" on post.POST_ID = role.POST_ID and role.ROLE_ID = 100018 and role.ACTIVATE_FLAG = 1 ");
			lSBQuery.append(" inner join org_ddo_mst ddo on ddo.HOD_LOC_CODE = post.LOC_ID ");
			lSBQuery.append(" inner join ORG_USERPOST_RLT usr on usr.POST_ID = post.POST_ID and usr.ACTIVATE_FLAG = 1 ");
			lSBQuery.append(" where ddo.LOCATION_CODE = "+locationCode);
			
			logger.error(" getHROList query.. : " + lSBQuery);
			Query lQuery = ghibSession.createSQLQuery(lSBQuery.toString());			
			if(lQuery != null && lQuery.list().size() > 0){
				hodPostId =  Long.parseLong(lQuery.uniqueResult().toString());
				 //= Long.parseLong(obj[0].toString());
			}						
		} catch (Exception e) {
			logger.error(" Error is : " + e, e);
		}
		return hodPostId;
	}
	
}
